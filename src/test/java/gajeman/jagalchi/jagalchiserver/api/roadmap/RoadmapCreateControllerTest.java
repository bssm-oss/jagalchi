package gajeman.jagalchi.jagalchiserver.api.roadmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.CreateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoadmapCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Test
    void when_로드맵_생성_요청이_유효하면_201_응답을_반환한다() throws Exception {
        CreateRoadmapRequest request = new CreateRoadmapRequest(
                "Spring Boot",
                "Description",
                null,
                true,
                "http://thumb.com",
                List.of("spring", "boot")
        );

        mockMvc.perform(post("/roadmaps")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Boot"))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    void when_제목이_없으면_400_응답을_반환한다() throws Exception {
        CreateRoadmapRequest request = new CreateRoadmapRequest(
                "",
                "Description",
                null,
                true,
                "http://thumb.com",
                List.of("spring")
        );

        mockMvc.perform(post("/roadmaps")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
