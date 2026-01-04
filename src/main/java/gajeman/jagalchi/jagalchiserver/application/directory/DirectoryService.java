package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final RoadmapRepository roadmapRepository;

    @Transactional
    public void delete(Long directoryId, Long userId, String mode, Long targetDirectoryId) {
        Directory directory = findByIdAndOwner(directoryId, userId);
        if (directory == null) {
            throw new ResourceNotFoundException("Directory", directoryId);
        }

        String action = mode == null ? "delete" : mode;
        if ("move".equals(action)) {
            moveChildren(directory, userId, targetDirectoryId);
            directoryRepository.delete(directory);
            return;
        }

        if ("delete".equals(action)) {
            deleteRecursively(directory, userId);
            return;
        }

        throw new IllegalArgumentException("mode must be delete or move");
    }

    private void moveChildren(Directory directory, Long userId, Long targetDirectoryId) {
        if (targetDirectoryId == null) {
            throw new IllegalArgumentException("targetDirectoryId is required");
        }
        if (directory.getId().equals(targetDirectoryId)) {
            throw new IllegalArgumentException("targetDirectoryId must be different");
        }

        Directory target = findByIdAndOwner(targetDirectoryId, userId);
        if (target == null) {
            throw new ResourceNotFoundException("Directory", targetDirectoryId);
        }

        List<Directory> children = directoryRepository.findByOwnerIdAndParentId(userId, directory.getId());
        for (Directory child : children) {
            child.moveTo(targetDirectoryId);
        }

        List<Roadmap> roadmaps = roadmapRepository.findByOwnerIdAndDirectoryId(userId, directory.getId());
        for (Roadmap roadmap : roadmaps) {
            roadmap.moveToDirectory(targetDirectoryId);
        }
    }

    private void deleteRecursively(Directory directory, Long userId) {
        List<Directory> children = directoryRepository.findByOwnerIdAndParentId(userId, directory.getId());
        for (Directory child : children) {
            deleteRecursively(child, userId);
        }

        roadmapRepository.deleteByOwnerIdAndDirectoryId(userId, directory.getId());
        directoryRepository.delete(directory);
    }

    @Transactional
    public DirectoryResponse create(CreateDirectoryRequest request, Long ownerId) {
        if (request.getParentId() != null) {
            Directory parent = findByIdAndOwner(request.getParentId(), ownerId);
            if (parent == null) {
                throw new ResourceNotFoundException("Directory", request.getParentId());
            }
        }

        Directory directory = Directory.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .ownerId(ownerId)
                .build();

        Directory saved = directoryRepository.save(directory);
        String path = calculatePath(saved);
        return DirectoryResponse.from(saved, path);
    }

    private String calculatePath(Directory directory) {
        if (directory.getParentId() == null) {
            return "/" + directory.getName();
        }

        Directory parent = directoryRepository.findById(directory.getParentId()).orElse(null);
        if (parent == null) {
            return "/" + directory.getName();
        }

        return calculatePath(parent) + "/" + directory.getName();
    }

    private Directory findByIdAndOwner(Long directoryId, Long ownerId) {
        return directoryRepository.findById(directoryId)
                .filter(directory -> directory.isOwnedBy(ownerId))
                .orElse(null);
    }
}
