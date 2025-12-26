package gajeman.jagalchi.jagalchiserver.api.progress;

import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgress;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProgressMyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private RoadmapNodeRepository roadmapNodeRepository;

    @Autowired
    private RoadmapNodeProgressRepository progressRepository;

    @Test
    void when_정상요청이면_내_진도를_조회한다() throws Exception {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        RoadmapNode node1 = RoadmapNode.builder()
                .roadmapId(savedRoadmap.getId())
                .label("노드1")
                .build();
        RoadmapNode node2 = RoadmapNode.builder()
                .roadmapId(savedRoadmap.getId())
                .label("노드2")
                .build();
        RoadmapNode savedNode1 = roadmapNodeRepository.save(node1);
        roadmapNodeRepository.save(node2);

        RoadmapNodeProgress progress = RoadmapNodeProgress.builder()
                .roadmapId(savedRoadmap.getId())
                .nodeId(savedNode1.getId())
                .userId(1L)
                .build();
        progress.toggleComplete(true);
        progressRepository.save(progress);

        mockMvc.perform(get("/roadmaps/{roadmapId}/my-progress", savedRoadmap.getId())
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNodes").value(2))
                .andExpect(jsonPath("$.completedNodes").value(1))
                .andExpect(jsonPath("$.progressPercentage").value(50.0));
    }

    @Test
    void when_비공개_로드맵이면_404가_반환된다() throws Exception {
        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(false)
                .build();
        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        mockMvc.perform(get("/roadmaps/{roadmapId}/my-progress", savedRoadmap.getId())
                        .header("X-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }
}
