package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
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
    private DirectoryRepository directoryRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void when_타인이_조회하면_조회수가_증가한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);
        ReflectionTestUtils.setField(roadmap, "viewCount", 0L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));

        RoadmapResponse response = roadmapService.getDetail(1L, 1L);

        assertThat(response.getViewCount()).isEqualTo(1L);
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

        assertThatThrownBy(() -> roadmapService.getDetail(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
