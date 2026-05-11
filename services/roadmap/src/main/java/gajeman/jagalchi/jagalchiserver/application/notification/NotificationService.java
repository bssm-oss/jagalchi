package gajeman.jagalchi.jagalchiserver.application.notification;

import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationListResponse;
import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationResponse;
import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationSettingResponse;
import gajeman.jagalchi.jagalchiserver.domain.notification.Notification;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationRepository;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationSetting;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationSettingRepository;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationType;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;

    @Transactional
    public void createForkNotification(Long originalRoadmapId, Long forkedRoadmapId, Long forkedUserId) {
        var roadmap = roadmapRepository.findById(originalRoadmapId).orElse(null);
        if (roadmap == null) {
            return;
        }

        Long originalRoadmapOwnerId = roadmap.getOwnerId();
        
        if (!isNotificationEnabled(originalRoadmapOwnerId, NotificationType.FORK)) {
            return;
        }

        User forkedUser = userRepository.findById(forkedUserId).orElse(null);
        String forkerName = forkedUser != null ? forkedUser.getNickname() : "Unknown";

        User originalOwner = userRepository.findById(originalRoadmapOwnerId).orElse(null);
        if (originalOwner == null) {
            return;
        }

        Notification notification = Notification.builder()
                .user(originalOwner)
                .type(NotificationType.FORK)
                .title("로드맵이 포크되었습니다")
                .message(forkerName + "님이 당신의 로드맵을 포크하여 학습 중입니다.")
                .referenceId(forkedRoadmapId)
                .build();

        notificationRepository.save(notification);
    }

    public NotificationListResponse getNotifications(Long userId, int page, int size, Boolean unreadOnly, String type) {
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> notificationsPage;
        
        if (type != null && !type.isBlank()) {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            notificationsPage = notificationRepository.findByUserIdAndType(userId, notificationType, pageable);
        } else if (Boolean.TRUE.equals(unreadOnly)) {
            notificationsPage = notificationRepository.findByUserIdAndIsReadFalse(userId, pageable);
        } else {
            notificationsPage = notificationRepository.findByUserId(userId, pageable);
        }

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        List<NotificationResponse> responses = notificationsPage.getContent().stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());

        return NotificationListResponse.of(
                responses,
                notificationsPage.getTotalElements(),
                unreadCount,
                resolvedPage,
                resolvedSize);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to user: " + userId);
        }

        notification.markAsRead();
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }

    public List<NotificationSettingResponse> getNotificationSettings(Long userId) {
        List<NotificationSetting> settings = notificationSettingRepository.findByUserId(userId);
        
        return Arrays.stream(NotificationType.values())
                .map(type -> {
                    NotificationSetting setting = settings.stream()
                            .filter(s -> s.getNotificationType().equals(type))
                            .findFirst()
                            .orElse(null);
                    
                    boolean isEnabled = setting != null ? setting.getIsEnabled() : true;
                    return NotificationSettingResponse.of(type, isEnabled);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateNotificationSetting(Long userId, NotificationType type, Boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        NotificationSetting setting = notificationSettingRepository
                .findByUserIdAndNotificationType(userId, type)
                .orElse(null);

        if (setting != null) {
            setting.updateEnabled(enabled);
        } else {
            setting = NotificationSetting.builder()
                    .user(user)
                    .notificationType(type)
                    .isEnabled(enabled)
                    .build();
            notificationSettingRepository.save(setting);
        }
    }

    public boolean isNotificationEnabled(Long userId, NotificationType type) {
        return notificationSettingRepository.isNotificationEnabled(userId, type);
    }
}
