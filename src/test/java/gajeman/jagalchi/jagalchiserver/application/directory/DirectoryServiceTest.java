package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private DirectoryService directoryService;

    @Test
    void when_상위디렉토리가_없으면_예외가_발생한다() {
        CreateDirectoryRequest request = new CreateDirectoryRequest("공부", 99L);

        when(directoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> directoryService.create(request, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void when_정상요청이면_디렉토리를_생성한다() {
        CreateDirectoryRequest request = new CreateDirectoryRequest("공부", null);
        Directory saved = Directory.builder()
                .name("공부")
                .parentId(null)
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(saved, "id", 1L);

        when(directoryRepository.save(any(Directory.class))).thenReturn(saved);

        DirectoryResponse response = directoryService.create(request, 1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("공부");
        assertThat(response.getPath()).isEqualTo("/공부");
    }
}
