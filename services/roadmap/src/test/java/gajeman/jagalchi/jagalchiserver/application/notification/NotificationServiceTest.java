package gajeman.jagalchi.jagalchiserver.application.notification;

import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationListResponse;
import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationSettingResponse;
import gajeman.jagalchi.jagalchiserver.domain.notification.Notification;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationRepository;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationSetting;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationSettingRepository;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationType;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSettingRepository notificationSettingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private User testForker;
    private Roadmap testRoadmap;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .nickname("owner")
                .email("owner@test.com")
                .build();

        testForker = User.builder()
                .nickname("forker")
                .email("forker@test.com")
                .build();

        testRoadmap = Roadmap.builder()
                .title("Test Roadmap")
                .ownerId(1L)
                .build();

        testNotification = Notification.builder()
                .user(testUser)
                .type(NotificationType.FORK)
                .title("로드맵이 포크되었습니다")
                .message("forker님이 당신의 로드맵을 포크하여 학습 중입니다.")
                .referenceId(2L)
                .build();
    }

    @Test
    @DisplayName("포크 알림 생성 - 성공")
    void createForkNotification_Success() {
        // given
        given(roadmapRepository.findById(1L)).willReturn(Optional.of(testRoadmap));
        given(notificationSettingRepository.isNotificationEnabled(1L, NotificationType.FORK)).willReturn(true);
        given(userRepository.findById(2L)).willReturn(Optional.of(testForker));
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(notificationRepository.save(any(Notification.class))).willReturn(testNotification);

        // when
        notificationService.createForkNotification(1L, 2L, 2L);

        // then
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("포크 알림 생성 - 알림 비활성화")
    void createForkNotification_Disabled() {
        // given
        given(roadmapRepository.findById(1L)).willReturn(Optional.of(testRoadmap));
        given(notificationSettingRepository.isNotificationEnabled(1L, NotificationType.FORK)).willReturn(false);

        // when
        notificationService.createForkNotification(1L, 2L, 2L);

        // then
        verify(notificationRepository, org.mockito.Mockito.never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("알림 목록 조회 - 성공")
    void getNotifications_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> notificationPage = new PageImpl<>(List.of(testNotification), pageable, 1);
        
        given(notificationRepository.findByUserId(1L, pageable)).willReturn(notificationPage);
        given(notificationRepository.countByUserIdAndIsReadFalse(1L)).willReturn(1L);

        // when
        NotificationListResponse response = notificationService.getNotifications(1L, 0, 10, null, null);

        // then
        assertThat(response.getNotifications()).hasSize(1);
        assertThat(response.getUnreadCount()).isEqualTo(1);
        assertThat(response.getTotalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("알림 읽음 처리 - 성공")
    void markAsRead_Success() {
        // given
        given(notificationRepository.findById(1L)).willReturn(Optional.of(testNotification));

        // when
        notificationService.markAsRead(1L, testUser.getId());

        // then
        assertThat(testNotification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("전체 알림 읽음 처리 - 성공")
    void markAllAsRead_Success() {
        // given
        given(notificationRepository.markAllAsReadByUserId(1L)).willReturn(5);

        // when
        int count = notificationService.markAllAsRead(1L);

        // then
        assertThat(count).isEqualTo(5);
        verify(notificationRepository).markAllAsReadByUserId(1L);
    }

    @Test
    @DisplayName("알림 설정 조회 - 성공")
    void getNotificationSettings_Success() {
        // given
        given(notificationSettingRepository.findByUserId(1L)).willReturn(List.of());

        // when
        List<NotificationSettingResponse> settings = notificationService.getNotificationSettings(1L);

        // then
        assertThat(settings).hasSize(NotificationType.values().length);
        assertThat(settings).allMatch(NotificationSettingResponse::getIsEnabled);
    }

    @Test
    @DisplayName("알림 설정 업데이트 - 성공 (기존 설정 존재)")
    void updateNotificationSetting_ExistingSetting() {
        // given
        NotificationSetting existingSetting = NotificationSetting.builder()
                .user(testUser)
                .notificationType(NotificationType.FORK)
                .isEnabled(true)
                .build();
        
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(notificationSettingRepository.findByUserIdAndNotificationType(1L, NotificationType.FORK))
                .willReturn(Optional.of(existingSetting));

        // when
        notificationService.updateNotificationSetting(1L, NotificationType.FORK, false);

        // then
        assertThat(existingSetting.getIsEnabled()).isFalse();
    }

    @Test
    @DisplayName("알림 설정 업데이트 - 성공 (새 설정 생성)")
    void updateNotificationSetting_NewSetting() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(notificationSettingRepository.findByUserIdAndNotificationType(1L, NotificationType.FORK))
                .willReturn(Optional.empty());

        // when
        notificationService.updateNotificationSetting(1L, NotificationType.FORK, false);

        // then
        verify(notificationSettingRepository).save(any(NotificationSetting.class));
    }

    @Test
    @DisplayName("알림 활성화 확인 - 활성화")
    void isNotificationEnabled_True() {
        // given
        given(notificationSettingRepository.isNotificationEnabled(1L, NotificationType.FORK)).willReturn(true);

        // when
        boolean result = notificationService.isNotificationEnabled(1L, NotificationType.FORK);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("알림 활성화 확인 - 비활성화")
    void isNotificationEnabled_False() {
        // given
        given(notificationSettingRepository.isNotificationEnabled(1L, NotificationType.FORK)).willReturn(false);

        // when
        boolean result = notificationService.isNotificationEnabled(1L, NotificationType.FORK);

        // then
        assertThat(result).isFalse();
    }
}
