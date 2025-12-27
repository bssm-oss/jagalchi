package gajeman.jagalchi.jagalchiserver.application.progress;

import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgress;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressCompleteServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapNodeRepository roadmapNodeRepository;

    @Mock
    private RoadmapNodeProgressRepository progressRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void when_완료요청이면_진행률이_저장된다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(1L)
                .label("노드")
                .build();
        ReflectionTestUtils.setField(node, "id", 10L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));
        when(roadmapNodeRepository.findById(10L)).thenReturn(Optional.of(node));
        when(roadmapNodeRepository.countByRoadmapId(1L)).thenReturn(2L);
        when(progressRepository.findByRoadmapIdAndNodeIdAndUserId(1L, 10L, 1L))
                .thenReturn(Optional.empty());
        when(progressRepository.save(any(RoadmapNodeProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(progressRepository.countCompletedByRoadmapIdAndUserId(1L, 1L)).thenReturn(1L);

        var response = progressService.completeNode(1L, 10L, 1L, true);

        ArgumentCaptor<RoadmapNodeProgress> captor = ArgumentCaptor.forClass(RoadmapNodeProgress.class);
        verify(progressRepository).save(captor.capture());

        RoadmapNodeProgress saved = captor.getValue();
        assertThat(saved.getIsCompleted()).isTrue();
        assertThat(saved.getCompletedAt()).isNotNull();
        assertThat(response.getNodeId()).isEqualTo(10L);
        assertThat(response.getIsCompleted()).isTrue();
        assertThat(response.getRoadmapProgress()).isEqualTo(new BigDecimal("50.0"));
        assertThat(response.getCompletedAt()).isNotNull();
    }

    @Test
    void when_비공개_로드맵에_접근하면_예외가_발생한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));

        assertThatThrownBy(() -> progressService.completeNode(1L, 10L, 1L, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
