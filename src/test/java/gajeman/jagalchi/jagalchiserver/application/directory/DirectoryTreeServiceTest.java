package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryTreeResponse;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryTreeServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private DirectoryService directoryService;

    @Test
    void when_트리를_조회하면_이름순으로_정렬된다() {
        Directory rootA = Directory.builder()
                .name("A")
                .parentId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(rootA, "id", 1L);

        Directory rootB = Directory.builder()
                .name("B")
                .parentId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(rootB, "id", 2L);

        Directory child = Directory.builder()
                .name("A-1")
                .parentId(1L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(child, "id", 3L);

        Roadmap root1 = Roadmap.builder()
                .title("Root1")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(root1, "id", 10L);

        Roadmap root2 = Roadmap.builder()
                .title("Root2")
                .description("desc")
                .directoryId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(root2, "id", 11L);

        Roadmap mapA = Roadmap.builder()
                .title("Amap")
                .description("desc")
                .directoryId(1L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(mapA, "id", 12L);

        Roadmap mapB = Roadmap.builder()
                .title("Bmap")
                .description("desc")
                .directoryId(1L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(mapB, "id", 13L);

        when(directoryRepository.findByOwnerId(1L)).thenReturn(List.of(rootB, rootA, child));
        when(roadmapRepository.findByOwnerId(1L)).thenReturn(List.of(root2, root1, mapB, mapA));

        List<DirectoryTreeResponse> result = directoryService.getTree(1L);

        assertThat(result.get(0).getName()).isEqualTo("(루트)");
        assertThat(result.get(1).getName()).isEqualTo("A");
        assertThat(result.get(2).getName()).isEqualTo("B");
        assertThat(result.get(1).getChildren()).hasSize(1);
        assertThat(result.get(1).getChildren().get(0).getName()).isEqualTo("A-1");
        assertThat(result.get(0).getRoadmaps().get(0).getTitle()).isEqualTo("Root1");
        assertThat(result.get(0).getRoadmaps().get(1).getTitle()).isEqualTo("Root2");
        assertThat(result.get(1).getRoadmaps().get(0).getTitle()).isEqualTo("Amap");
        assertThat(result.get(1).getRoadmaps().get(1).getTitle()).isEqualTo("Bmap");
    }
}
