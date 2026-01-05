package gajeman.jagalchi.jagalchiserver.api.directory;

import com.fasterxml.jackson.databind.ObjectMapper;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.UpdateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DirectoryUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DirectoryRepository directoryRepository;

    @Test
    void when_디렉토리_이름_수정_요청이_유효하면_200_응답을_반환한다() throws Exception {
        Directory directory = directoryRepository.save(Directory.builder()
                .name("Old Name")
                .ownerId(1L)
                .build());

        UpdateDirectoryRequest request = new UpdateDirectoryRequest("New Name");

        mockMvc.perform(patch("/directories/{id}", directory.getId())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.path").value("/New Name"));
    }

    @Test
    void when_존재하지_않는_디렉토리면_404_응답을_반환한다() throws Exception {
        UpdateDirectoryRequest request = new UpdateDirectoryRequest("New Name");

        mockMvc.perform(patch("/directories/{id}", 999L)
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
