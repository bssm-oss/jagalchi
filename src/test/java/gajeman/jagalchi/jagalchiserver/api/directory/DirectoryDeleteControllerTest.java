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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DirectoryDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Test
    void when_정상요청이면_디렉토리_삭제에_성공한다() throws Exception {
        Directory parent = Directory.builder()
                .name("상위")
                .parentId(null)
                .ownerId(1L)
                .build();
        Directory savedParent = directoryRepository.save(parent);

        Directory target = Directory.builder()
                .name("삭제대상")
                .parentId(savedParent.getId())
                .ownerId(1L)
                .build();
        Directory savedTarget = directoryRepository.save(target);

        Directory child = Directory.builder()
                .name("자식")
                .parentId(savedTarget.getId())
                .ownerId(1L)
                .build();
        Directory savedChild = directoryRepository.save(child);

        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(savedTarget.getId())
                .ownerId(1L)
                .build();
        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        mockMvc.perform(delete("/roadmaps/directories/{id}", savedTarget.getId())
                        .param("mode", "move")
                        .param("targetDirectoryId", String.valueOf(savedParent.getId()))
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Directory deleted successfully"));

        Optional<Directory> deleted = directoryRepository.findById(savedTarget.getId());
        assertThat(deleted).isEmpty();

        Directory movedChild = directoryRepository.findById(savedChild.getId()).orElseThrow();
        assertThat(movedChild.getParentId()).isEqualTo(savedParent.getId());

        Roadmap movedRoadmap = roadmapRepository.findById(savedRoadmap.getId()).orElseThrow();
        assertThat(movedRoadmap.getDirectoryId()).isEqualTo(savedParent.getId());
    }

    @Test
    void when_타인소유_디렉토리면_404가_반환된다() throws Exception {
        Directory target = Directory.builder()
                .name("삭제대상")
                .parentId(null)
                .ownerId(2L)
                .build();
        Directory savedTarget = directoryRepository.save(target);

        mockMvc.perform(delete("/roadmaps/directories/{id}", savedTarget.getId())
                        .param("mode", "delete")
                        .header("X-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }
}
