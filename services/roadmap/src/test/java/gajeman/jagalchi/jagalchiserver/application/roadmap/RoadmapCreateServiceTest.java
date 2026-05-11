package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.CreateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadmapCreateServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void when_로드맵을_생성하면_저장된_로드맵이_반환된다() {
        CreateRoadmapRequest request = new CreateRoadmapRequest(
                "Title", "Desc", null, true, "url", List.of("tag1", "tag2")
        );
        Roadmap saved = Roadmap.builder()
                .title("Title")
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(saved, "id", 100L);

        when(roadmapRepository.save(any(Roadmap.class))).thenReturn(saved);

        RoadmapResponse response = roadmapService.create(request, 1L);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getTitle()).isEqualTo("Title");
        verify(roadmapRepository).save(any(Roadmap.class));
    }
}
