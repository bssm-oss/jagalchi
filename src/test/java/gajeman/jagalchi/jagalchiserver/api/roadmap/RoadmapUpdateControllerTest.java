package gajeman.jagalchi.jagalchiserver.api.roadmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.UpdateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoadmapUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Test
    void when_정상요청이면_로드맵_수정에_성공한다() throws Exception {
        Roadmap roadmap = Roadmap.builder()
                .title("기존")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .isPublic(true)
                .build();
        Roadmap saved = roadmapRepository.save(roadmap);

        UpdateRoadmapRequest request = new UpdateRoadmapRequest("변경", null, null, false);

        mockMvc.perform(patch("/roadmaps/{id}", saved.getId())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("변경"))
                .andExpect(jsonPath("$.isPublic").value(false));
    }

    @Test
    void when_타인소유_로드맵이면_404가_반환된다() throws Exception {
        Roadmap roadmap = Roadmap.builder()
                .title("기존")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(true)
                .build();
        Roadmap saved = roadmapRepository.save(roadmap);

        UpdateRoadmapRequest request = new UpdateRoadmapRequest("변경", null, null, false);

        mockMvc.perform(patch("/roadmaps/{id}", saved.getId())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }
}
