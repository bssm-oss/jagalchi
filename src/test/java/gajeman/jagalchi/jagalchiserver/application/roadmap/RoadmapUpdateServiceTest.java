package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.UpdateRoadmapRequest;
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
class RoadmapUpdateServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private DirectoryRepository directoryRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void when_정상요청이면_로드맵을_수정한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("기존")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));

        UpdateRoadmapRequest request = new UpdateRoadmapRequest("변경", null, null, false);
        RoadmapResponse response = roadmapService.update(1L, request, 1L);

        assertThat(response.getTitle()).isEqualTo("변경");
        assertThat(response.getIsPublic()).isFalse();
    }

    @Test
    void when_타인소유_로드맵이면_예외가_발생한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("기존")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        when(roadmapRepository.findById(1L)).thenReturn(Optional.of(roadmap));

        UpdateRoadmapRequest request = new UpdateRoadmapRequest("변경", null, null, false);

        assertThatThrownBy(() -> roadmapService.update(1L, request, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
