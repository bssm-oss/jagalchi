package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static gajeman.jagalchi.jagalchiserver.support.TestJwtTokens.bearerEditToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoadmapDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadmapNodeRepository roadmapNodeRepository;

    @Test
    void when_공개_로드맵이면_상세조회에_성공한다() throws Exception {
        User viewer = userRepository.save(User.builder()
                .nickname("viewer")
                .email("viewer@example.com")
                .profileImageUrl("viewer.jpg")
                .build());
        User owner = userRepository.save(User.builder()
                .nickname("owner")
                .email("owner@example.com")
                .profileImageUrl("owner.jpg")
                .build());

        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(owner.getId())
                .isPublic(true)
                .build();
        Roadmap saved = roadmapRepository.save(roadmap);
        roadmapNodeRepository.save(RoadmapNode.builder()
                .roadmapId(saved.getId())
                .label("노드1")
                .build());
        roadmapNodeRepository.save(RoadmapNode.builder()
                .roadmapId(saved.getId())
                .label("노드2")
                .build());

        mockMvc.perform(get("/roadmaps/{id}", saved.getId())
                        .header("Authorization", bearerEditToken(viewer.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.owner.id").value(owner.getId()))
                .andExpect(jsonPath("$.stats.totalNodes").value(2))
                .andExpect(jsonPath("$.stats.totalEdges").value(1))
                .andExpect(jsonPath("$.viewCount").value(1));
    }

    @Test
    void when_비공개_로드맵에_접근하면_404가_반환된다() throws Exception {
        User viewer = userRepository.save(User.builder()
                .nickname("viewer2")
                .email("viewer2@example.com")
                .build());
        User owner = userRepository.save(User.builder()
                .nickname("owner2")
                .email("owner2@example.com")
                .build());

        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(null)
                .ownerId(owner.getId())
                .isPublic(false)
                .build();
        Roadmap saved = roadmapRepository.save(roadmap);

        mockMvc.perform(get("/roadmaps/{id}", saved.getId())
                        .header("Authorization", bearerEditToken(viewer.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }
}
