package gajeman.jagalchi.jagalchiserver.api.directory;

import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
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
class DirectoryTreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Test
    void when_정상요청이면_디렉토리_트리를_조회한다() throws Exception {
        Directory root = Directory.builder()
                .name("학습")
                .parentId(null)
                .ownerId(1L)
                .build();
        Directory savedRoot = directoryRepository.save(root);

        Directory child = Directory.builder()
                .name("하위")
                .parentId(savedRoot.getId())
                .ownerId(1L)
                .build();
        directoryRepository.save(child);

        Roadmap roadmap = Roadmap.builder()
                .title("Java")
                .description("desc")
                .directoryId(savedRoot.getId())
                .ownerId(1L)
                .build();
        roadmapRepository.save(roadmap);

        mockMvc.perform(get("/directories/tree")
                        .header("Authorization", bearerEditToken(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Root"))
                .andExpect(jsonPath("$[0].path").value("/"))
                .andExpect(jsonPath("$[0].children[0].name").value("학습"))
                .andExpect(jsonPath("$[0].children[0].children[0].name").value("하위"))
                .andExpect(jsonPath("$[0].children[0].roadmaps[0].title").value("Java"));
    }

    @Test
    void when_토큰이_없으면_401이_반환된다() throws Exception {
        mockMvc.perform(get("/directories/tree"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }
}
