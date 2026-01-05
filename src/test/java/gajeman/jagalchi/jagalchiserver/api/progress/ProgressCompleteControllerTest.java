package gajeman.jagalchi.jagalchiserver.api.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.CompleteNodeRequest;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgress;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProgressCompleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private RoadmapNodeRepository roadmapNodeRepository;

    @Autowired
    private RoadmapNodeProgressRepository progressRepository;

    @Test
    void when_정상요청이면_노드완료에_성공한다() throws Exception {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(savedRoadmap.getId())
                .label("노드")
                .build();
        RoadmapNode savedNode = roadmapNodeRepository.save(node);
        RoadmapNode extraNode = RoadmapNode.builder()
                .roadmapId(savedRoadmap.getId())
                .label("노드2")
                .build();
        roadmapNodeRepository.save(extraNode);

        CompleteNodeRequest request = new CompleteNodeRequest(true, null);

        mockMvc.perform(post("/roadmaps/{roadmapId}/nodes/{nodeId}/complete", savedRoadmap.getId(), savedNode.getId())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeId").value(savedNode.getId()))
                .andExpect(jsonPath("$.isCompleted").value(true))
                .andExpect(jsonPath("$.roadmapProgress").value(50.0))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());

        Optional<RoadmapNodeProgress> progress = progressRepository
                .findByRoadmapIdAndNodeIdAndUserId(savedRoadmap.getId(), savedNode.getId(), 1L);
        assertThat(progress).isPresent();
        assertThat(progress.get().getIsCompleted()).isTrue();
        assertThat(progress.get().getCompletedAt()).isNotNull();
    }

    @Test
    void when_노드가_다른_로드맵이면_404가_반환된다() throws Exception {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        Roadmap otherRoadmap = Roadmap.builder()
                .title("다른")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        Roadmap savedOther = roadmapRepository.save(otherRoadmap);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(savedOther.getId())
                .label("노드")
                .build();
        RoadmapNode savedNode = roadmapNodeRepository.save(node);

        CompleteNodeRequest request = new CompleteNodeRequest(true, null);

        mockMvc.perform(post("/roadmaps/{roadmapId}/nodes/{nodeId}/complete", savedRoadmap.getId(), savedNode.getId())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }
}
