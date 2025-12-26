package gajeman.jagalchi.jagalchiserver.api.directory;

import com.fasterxml.jackson.databind.ObjectMapper;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DirectoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DirectoryRepository directoryRepository;

    @Test
    void when_정상요청이면_디렉토리_생성에_성공한다() throws Exception {
        CreateDirectoryRequest request = new CreateDirectoryRequest("학습자료", null);

        mockMvc.perform(post("/roadmaps/directories")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("학습자료"))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.parentId").value(nullValue()));
    }

    @Test
    void when_상위디렉토리가_타인소유면_404가_반환된다() throws Exception {
        Directory parent = Directory.builder()
                .name("타인폴더")
                .parentId(null)
                .ownerId(2L)
                .build();
        Directory saved = directoryRepository.save(parent);

        CreateDirectoryRequest request = new CreateDirectoryRequest("내폴더", saved.getId());

        mockMvc.perform(post("/roadmaps/directories")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }
}
