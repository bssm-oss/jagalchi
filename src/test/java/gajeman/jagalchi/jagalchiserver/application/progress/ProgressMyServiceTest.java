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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressMyServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapNodeRepository roadmapNodeRepository;

    @Mock
    private RoadmapNodeProgressRepository progressRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void when_진도_조회하면_진행률이_계산된다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));
        when(roadmapNodeRepository.countByRoadmapId(1L)).thenReturn(4L);
        when(progressRepository.countCompletedByRoadmapIdAndUserId(1L, 1L)).thenReturn(1L);

        ProgressResponse response = progressService.getMyProgress(1L, 1L);

        assertThat(response.getTotalNodes()).isEqualTo(4L);
        assertThat(response.getCompletedNodes()).isEqualTo(1L);
        assertThat(response.getProgressPercentage()).isEqualTo(new BigDecimal("25.0"));
    }

    @Test
    void when_비공개_로드맵이면_예외가_발생한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));

        assertThatThrownBy(() -> progressService.getMyProgress(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
