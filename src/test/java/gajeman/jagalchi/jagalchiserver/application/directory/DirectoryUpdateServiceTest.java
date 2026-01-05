package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.UpdateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryUpdateServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @InjectMocks
    private DirectoryService directoryService;

    @Test
    void when_디렉토리_이름을_수정하면_변경된_정보가_반환된다() {
        Directory directory = Directory.builder()
                .name("Old Name")
                .ownerId(1L)
                .build();
        ReflectionTestUtils.setField(directory, "id", 10L);

        when(directoryRepository.findById(10L)).thenReturn(Optional.of(directory));

        UpdateDirectoryRequest request = new UpdateDirectoryRequest("New Name");
        DirectoryResponse response = directoryService.update(10L, request, 1L);

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getPath()).isEqualTo("/New Name");
    }

    @Test
    void when_존재하지_않는_디렉토리를_수정하면_예외가_발생한다() {
        when(directoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        UpdateDirectoryRequest request = new UpdateDirectoryRequest("New Name");

        assertThatThrownBy(() -> directoryService.update(999L, request, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
