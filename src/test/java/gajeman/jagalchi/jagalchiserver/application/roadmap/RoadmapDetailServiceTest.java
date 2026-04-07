package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.application.user.UserService;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadmapDetailServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapNodeRepository roadmapNodeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void when_타인이_조회하면_조회수가_증가한다() {
        // given
        Long roadmapId = 1L;
        Long ownerId = 2L;
        Long viewerId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(ownerId)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);
        ReflectionTestUtils.setField(roadmap, "viewCount", 0L);

        User owner = User.builder()
                .nickname("owner")
                .email("owner@example.com")
                .profileImageUrl("profile.jpg")
                .build();
        ReflectionTestUtils.setField(owner, "id", ownerId);

        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.of(roadmap));
        when(roadmapNodeRepository.countByRoadmapId(roadmapId)).thenReturn(2L);
        when(userService.findById(ownerId)).thenReturn(owner);

        // when
        RoadmapDetailResponse response = roadmapService.getDetail(roadmapId, viewerId);

        // then
        assertThat(response.getViewCount()).isEqualTo(1L);
        assertThat(response.getStats().getTotalNodes()).isEqualTo(2L);
        assertThat(response.getStats().getTotalEdges()).isEqualTo(1L);
        assertThat(response.getOwner().getId()).isEqualTo(ownerId);
        assertThat(response.getOwner().getNickname()).isEqualTo("owner");
        assertThat(response.getOwner().getProfileImageUrl()).isEqualTo("profile.jpg");
    }

    @Test
    void when_소유자가_조회하면_조회수가_증가하지_않는다() {
        // given
        Long roadmapId = 1L;
        Long ownerId = 2L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(ownerId)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);
        ReflectionTestUtils.setField(roadmap, "viewCount", 5L);

        User owner = User.builder()
                .nickname("owner")
                .email("owner@example.com")
                .build();

        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.of(roadmap));
        when(roadmapNodeRepository.countByRoadmapId(roadmapId)).thenReturn(3L);
        when(userService.findById(ownerId)).thenReturn(owner);

        // when
        RoadmapDetailResponse response = roadmapService.getDetail(roadmapId, ownerId);

        // then
        assertThat(response.getViewCount()).isEqualTo(5L); // 조회수 증가하지 않음
        assertThat(response.getStats().getTotalNodes()).isEqualTo(3L);
        assertThat(response.getStats().getTotalEdges()).isEqualTo(2L);
    }

    @Test
    void when_비공개_로드맵에_접근하면_예외가_발생한다() {
        // given
        Long roadmapId = 1L;
        Long ownerId = 2L;
        Long viewerId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(ownerId)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.of(roadmap));

        // when & then
        assertThatThrownBy(() -> roadmapService.getDetail(roadmapId, viewerId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void when_비공개_로드맵을_소유자가_조회하면_성공한다() {
        // given
        Long roadmapId = 1L;
        Long ownerId = 2L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("비공개 로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(ownerId)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);
        ReflectionTestUtils.setField(roadmap, "viewCount", 0L);

        User owner = User.builder()
                .nickname("owner")
                .email("owner@example.com")
                .build();

        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.of(roadmap));
        when(roadmapNodeRepository.countByRoadmapId(roadmapId)).thenReturn(1L);
        when(userService.findById(ownerId)).thenReturn(owner);

        // when
        RoadmapDetailResponse response = roadmapService.getDetail(roadmapId, ownerId);

        // then
        assertThat(response.getTitle()).isEqualTo("비공개 로드맵");
        assertThat(response.getIsPublic()).isFalse();
        assertThat(response.getViewCount()).isEqualTo(0L); // 소유자 조회는 조회수 증가 안함
        assertThat(response.getOwner().getNickname()).isEqualTo("owner");
    }

    @Test
    void when_존재하지_않는_로드맵을_조회하면_예외가_발생한다() {
        // given
        Long nonExistentRoadmapId = 999L;
        Long userId = 1L;

        when(roadmapRepository.findById(nonExistentRoadmapId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roadmapService.getDetail(nonExistentRoadmapId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Roadmap not found with id: 999");
    }
}
