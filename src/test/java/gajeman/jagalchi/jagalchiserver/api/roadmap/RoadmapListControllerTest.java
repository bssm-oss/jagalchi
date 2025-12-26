package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoadmapListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Test
    void when_정상요청이면_접근가능한_로드맵을_조회한다() throws Exception {
        Roadmap publicRoadmap = Roadmap.builder()
                .title("공개")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(true)
                .build();
        Roadmap savedPublic = roadmapRepository.save(publicRoadmap);

        Roadmap privateOther = Roadmap.builder()
                .title("비공개")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(false)
                .build();
        roadmapRepository.save(privateOther);

        Roadmap privateMine = Roadmap.builder()
                .title("내비공개")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(false)
                .build();
        Roadmap savedMine = roadmapRepository.save(privateMine);

        mockMvc.perform(get("/roadmaps")
                        .header("X-User-Id", "1")
                        .param("size", "50")
                        .param("sort", "latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasItems(savedPublic.getId().intValue(), savedMine.getId().intValue())));
    }

    @Test
    void when_정렬값이_잘못되면_400이_반환된다() throws Exception {
        mockMvc.perform(get("/roadmaps")
                        .param("sort", "wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }
}
