package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryTreeResponse;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.UpdateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private DirectoryService directoryService;

    @Test
    @DisplayName("디렉토리 트리를 조회할 수 있다")
    void getTree() {
        // given
        Long userId = 1L;
        
        Directory rootDir = Directory.builder()
                .name("Backend")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(rootDir, "id", 1L);

        Directory childDir = Directory.builder()
                .name("Spring")
                .parentId(1L)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(childDir, "id", 2L);

        Roadmap rootRoadmap = Roadmap.builder()
                .title("루트 로드맵")
                .ownerId(userId)
                .directoryId(null)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(rootRoadmap, "id", 1L);

        Roadmap childRoadmap = Roadmap.builder()
                .title("Spring Boot 로드맵")
                .ownerId(userId)
                .directoryId(1L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(childRoadmap, "id", 2L);

        given(directoryRepository.findByOwnerId(userId)).willReturn(List.of(rootDir, childDir));
        given(roadmapRepository.findByOwnerId(userId)).willReturn(List.of(rootRoadmap, childRoadmap));

        // when
        List<DirectoryTreeResponse> result = directoryService.getTree(userId);

        // then
        assertThat(result).hasSize(1);
        DirectoryTreeResponse root = result.get(0);
        assertThat(root.getName()).isEqualTo("Root");
        assertThat(root.getPath()).isEqualTo("/");
        assertThat(root.getRoadmaps()).hasSize(1);
        assertThat(root.getRoadmaps().get(0).getTitle()).isEqualTo("루트 로드맵");
        assertThat(root.getChildren()).hasSize(1);
        
        DirectoryTreeResponse backend = root.getChildren().get(0);
        assertThat(backend.getName()).isEqualTo("Backend");
        assertThat(backend.getPath()).isEqualTo("/Backend");
        assertThat(backend.getRoadmaps()).hasSize(1);
        assertThat(backend.getRoadmaps().get(0).getTitle()).isEqualTo("Spring Boot 로드맵");
    }

    @Test
    @DisplayName("새 디렉토리를 생성할 수 있다")
    void create() {
        // given
        Long userId = 1L;
        CreateDirectoryRequest request = new CreateDirectoryRequest("새 폴더", null);
        
        Directory savedDirectory = Directory.builder()
                .name("새 폴더")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(savedDirectory, "id", 1L);

        given(directoryRepository.save(any(Directory.class))).willReturn(savedDirectory);

        // when
        DirectoryResponse result = directoryService.create(request, userId);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("새 폴더");
        assertThat(result.getParentId()).isNull();
        assertThat(result.getPath()).isEqualTo("/새 폴더");
        verify(directoryRepository).save(any(Directory.class));
    }

    @Test
    @DisplayName("부모 디렉토리가 있는 하위 디렉토리를 생성할 수 있다")
    void createWithParent() {
        // given
        Long userId = 1L;
        Long parentId = 1L;
        
        Directory parent = Directory.builder()
                .name("부모")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(parent, "id", parentId);

        CreateDirectoryRequest request = new CreateDirectoryRequest("자식", parentId);
        
        Directory savedDirectory = Directory.builder()
                .name("자식")
                .parentId(parentId)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(savedDirectory, "id", 2L);

        given(directoryRepository.findById(parentId)).willReturn(Optional.of(parent));
        given(directoryRepository.save(any(Directory.class))).willReturn(savedDirectory);

        // when
        DirectoryResponse result = directoryService.create(request, userId);

        // then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("자식");
        assertThat(result.getParentId()).isEqualTo(parentId);
        assertThat(result.getPath()).isEqualTo("/부모/자식");
    }

    @Test
    @DisplayName("존재하지 않는 부모 디렉토리로 생성 시 예외가 발생한다")
    void createWithNonExistentParent() {
        // given
        Long userId = 1L;
        Long nonExistentParentId = 999L;
        CreateDirectoryRequest request = new CreateDirectoryRequest("자식", nonExistentParentId);

        given(directoryRepository.findById(nonExistentParentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> directoryService.create(request, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Directory not found with id: 999");
    }

    @Test
    @DisplayName("디렉토리 이름을 수정할 수 있다")
    void update() {
        // given
        Long userId = 1L;
        Long directoryId = 1L;
        
        Directory directory = Directory.builder()
                .name("기존 이름")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(directory, "id", directoryId);

        UpdateDirectoryRequest request = new UpdateDirectoryRequest("새 이름");

        given(directoryRepository.findById(directoryId)).willReturn(Optional.of(directory));

        // when
        DirectoryResponse result = directoryService.update(directoryId, request, userId);

        // then
        assertThat(result.getId()).isEqualTo(directoryId);
        assertThat(result.getName()).isEqualTo("새 이름");
        assertThat(directory.getName()).isEqualTo("새 이름"); // 실제 엔티티도 변경됨
    }

    @Test
    @DisplayName("다른 사용자의 디렉토리 수정 시 예외가 발생한다")
    void updateNotOwner() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long directoryId = 1L;
        
        Directory directory = Directory.builder()
                .name("기존 이름")
                .parentId(null)
                .ownerId(otherUserId) // 다른 사용자 소유
                .build();
        ReflectionTestUtils.setField(directory, "id", directoryId);

        UpdateDirectoryRequest request = new UpdateDirectoryRequest("새 이름");

        given(directoryRepository.findById(directoryId)).willReturn(Optional.of(directory));

        // when & then
        assertThatThrownBy(() -> directoryService.update(directoryId, request, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Directory not found with id: 1");
    }

    @Test
    @DisplayName("디렉토리를 삭제할 수 있다")
    void delete() {
        // given
        Long userId = 1L;
        Long directoryId = 1L;
        
        Directory directory = Directory.builder()
                .name("삭제할 폴더")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(directory, "id", directoryId);

        given(directoryRepository.findById(directoryId)).willReturn(Optional.of(directory));
        given(directoryRepository.findByOwnerIdAndParentId(userId, directoryId)).willReturn(List.of());
        given(roadmapRepository.findByOwnerIdAndDirectoryId(userId, directoryId)).willReturn(List.of());

        // when
        directoryService.delete(directoryId, userId, "delete", null);

        // then
        verify(directoryRepository).delete(directory);
    }

    @Test
    @DisplayName("디렉토리를 이동 모드로 삭제할 수 있다")
    void deleteWithMove() {
        // given
        Long userId = 1L;
        Long directoryId = 1L;
        Long targetDirectoryId = 2L;
        
        Directory directory = Directory.builder()
                .name("삭제할 폴더")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(directory, "id", directoryId);

        Directory targetDirectory = Directory.builder()
                .name("대상 폴더")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(targetDirectory, "id", targetDirectoryId);

        Directory childDirectory = Directory.builder()
                .name("자식 폴더")
                .parentId(directoryId)
                .ownerId(userId)
                .build();

        Roadmap roadmap = Roadmap.builder()
                .title("이동할 로드맵")
                .ownerId(userId)
                .directoryId(directoryId)
                .build();

        given(directoryRepository.findById(directoryId)).willReturn(Optional.of(directory));
        given(directoryRepository.findById(targetDirectoryId)).willReturn(Optional.of(targetDirectory));
        given(directoryRepository.findByOwnerIdAndParentId(userId, directoryId)).willReturn(List.of(childDirectory));
        given(roadmapRepository.findByOwnerIdAndDirectoryId(userId, directoryId)).willReturn(List.of(roadmap));

        // when
        directoryService.delete(directoryId, userId, "move", targetDirectoryId);

        // then
        assertThat(childDirectory.getParentId()).isEqualTo(targetDirectoryId);
        assertThat(roadmap.getDirectoryId()).isEqualTo(targetDirectoryId);
        verify(directoryRepository).delete(directory);
    }

    @Test
    @DisplayName("잘못된 삭제 모드 시 예외가 발생한다")
    void deleteWithInvalidMode() {
        // given
        Long userId = 1L;
        Long directoryId = 1L;
        
        Directory directory = Directory.builder()
                .name("폴더")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(directory, "id", directoryId);

        given(directoryRepository.findById(directoryId)).willReturn(Optional.of(directory));

        // when & then
        assertThatThrownBy(() -> directoryService.delete(directoryId, userId, "invalid", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("mode must be delete or move");
    }

    @Test
    @DisplayName("이동 모드에서 대상 디렉토리 ID가 없으면 예외가 발생한다")
    void deleteWithMoveButNoTarget() {
        // given
        Long userId = 1L;
        Long directoryId = 1L;
        
        Directory directory = Directory.builder()
                .name("폴더")
                .parentId(null)
                .ownerId(userId)
                .build();
        ReflectionTestUtils.setField(directory, "id", directoryId);

        given(directoryRepository.findById(directoryId)).willReturn(Optional.of(directory));

        // when & then
        assertThatThrownBy(() -> directoryService.delete(directoryId, userId, "move", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("targetDirectoryId is required");
    }
}