package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryDeleteServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private DirectoryService directoryService;

    @Test
    void when_이동모드면_하위항목을_대상으로_이동한다() {
        Directory directory = Directory.builder()
                .name("삭제")
                .parentId(5L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(directory, "id", 10L);

        Directory target = Directory.builder()
                .name("대상")
                .parentId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(target, "id", 20L);

        Directory child = Directory.builder()
                .name("자식")
                .parentId(10L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(child, "id", 11L);

        Roadmap roadmap = Roadmap.builder()
                .title("로드맵")
                .description("desc")
                .directoryId(10L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 12L);

        when(directoryRepository.findById(10L)).thenReturn(Optional.of(directory));
        when(directoryRepository.findById(20L)).thenReturn(Optional.of(target));
        when(directoryRepository.findByOwnerIdAndParentId(1L, 10L)).thenReturn(List.of(child));
        when(roadmapRepository.findByOwnerIdAndDirectoryId(1L, 10L)).thenReturn(List.of(roadmap));

        directoryService.delete(10L, 1L, "move", 20L);

        assertThat(child.getParentId()).isEqualTo(20L);
        assertThat(roadmap.getDirectoryId()).isEqualTo(20L);
        verify(directoryRepository).delete(directory);
    }

    @Test
    void when_삭제모드면_하위항목을_삭제한다() {
        Directory directory = Directory.builder()
                .name("삭제")
                .parentId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(directory, "id", 10L);

        Directory child = Directory.builder()
                .name("자식")
                .parentId(10L)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(child, "id", 11L);

        when(directoryRepository.findById(10L)).thenReturn(Optional.of(directory));
        when(directoryRepository.findByOwnerIdAndParentId(1L, 10L)).thenReturn(List.of(child));
        when(directoryRepository.findByOwnerIdAndParentId(1L, 11L)).thenReturn(List.of());

        directoryService.delete(10L, 1L, "delete", null);

        verify(roadmapRepository).deleteByOwnerIdAndDirectoryId(1L, 11L);
        verify(roadmapRepository).deleteByOwnerIdAndDirectoryId(1L, 10L);
        verify(directoryRepository).delete(child);
        verify(directoryRepository).delete(directory);
    }

    @Test
    void when_디렉토리가_없으면_예외가_발생한다() {
        when(directoryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directoryService.delete(10L, 1L, null, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
