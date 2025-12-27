package gajeman.jagalchi.jagalchiserver.application.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.ProgressResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressUserServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapNodeRepository roadmapNodeRepository;

    @Mock
    private RoadmapNodeProgressRepository progressRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void when_소유자가_요청하면_특정유저_진도가_계산된다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 5, 21, 15, 30);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));
        when(roadmapNodeRepository.countByRoadmapId(1L)).thenReturn(2L);
        when(progressRepository.countCompletedByRoadmapIdAndUserId(1L, 2L)).thenReturn(1L);
        when(progressRepository.findCompletedNodeIdsByRoadmapIdAndUserId(1L, 2L)).thenReturn(List.of(10L));
        when(progressRepository.findLatestUpdatedAtByRoadmapIdAndUserId(1L, 2L)).thenReturn(Optional.of(updatedAt));

        ProgressResponse response = progressService.getUserProgress(1L, 2L, 1L);

        assertThat(response.getProgressPercentage()).isEqualTo(new BigDecimal("50.0"));
        assertThat(response.getCompletedNodeIds()).containsExactly(10L);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void when_소유자가_아니면_예외가_발생한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));

        assertThatThrownBy(() -> progressService.getUserProgress(1L, 2L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
