package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
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

import static org.hamcrest.Matchers.hasItems;
import static gajeman.jagalchi.jagalchiserver.support.TestJwtTokens.bearerEditToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoadmapListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void when_정상요청이면_접근가능한_로드맵을_조회한다() throws Exception {
        User requester = userRepository.save(User.builder()
                .nickname("requester")
                .email("requester@example.com")
                .build());
        User other = userRepository.save(User.builder()
                .nickname("other")
                .email("other@example.com")
                .build());

        Roadmap publicRoadmap = Roadmap.builder()
                .title("공개")
                .description("desc")
                .directoryId(null)
                .ownerId(other.getId())
                .isPublic(true)
                .build();
        Roadmap savedPublic = roadmapRepository.save(publicRoadmap);

        Roadmap privateOther = Roadmap.builder()
                .title("비공개")
                .description("desc")
                .directoryId(null)
                .ownerId(other.getId())
                .isPublic(false)
                .build();
        roadmapRepository.save(privateOther);

        Roadmap privateMine = Roadmap.builder()
                .title("내비공개")
                .description("desc")
                .directoryId(null)
                .ownerId(requester.getId())
                .isPublic(false)
                .build();
        Roadmap savedMine = roadmapRepository.save(privateMine);

        mockMvc.perform(get("/roadmaps")
                        .header("Authorization", bearerEditToken(requester.getId()))
                        .param("size", "50")
                        .param("sort", "latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id",
                        hasItems(savedPublic.getId().intValue(), savedMine.getId().intValue())))
                .andExpect(jsonPath("$.content[*].owner.id", hasItems(
                        requester.getId().intValue(), other.getId().intValue())))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(50))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void when_정렬값이_잘못되면_400이_반환된다() throws Exception {
        mockMvc.perform(get("/roadmaps")
                        .header("Authorization", bearerEditToken(1L))
                        .param("sort", "wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }
}
