package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryTreeResponse;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.UpdateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapSummaryResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final RoadmapRepository roadmapRepository;

    public List<DirectoryTreeResponse> getTree(Long userId) {
        List<Directory> allDirectories = directoryRepository.findByOwnerId(userId);
        List<Roadmap> allRoadmaps = roadmapRepository.findByOwnerId(userId);

        Comparator<Directory> directoryComparator = Comparator.comparing(Directory::getName);
        Comparator<Roadmap> roadmapComparator = Comparator.comparing(Roadmap::getTitle);

        Map<Long, List<Roadmap>> roadmapsByDirectory = allRoadmaps.stream()
                .collect(Collectors.groupingBy(
                        roadmap -> roadmap.getDirectoryId() != null ? roadmap.getDirectoryId() : -1L,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream().sorted(roadmapComparator).toList())));

        Map<Long, List<Directory>> childrenByParent = allDirectories.stream()
                .collect(Collectors.groupingBy(
                        directory -> directory.getParentId() != null ? directory.getParentId() : -1L,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream().sorted(directoryComparator).toList())));

        List<Directory> roots = allDirectories.stream()
                .filter(directory -> directory.getParentId() == null)
                .sorted(directoryComparator)
                .toList();

        List<RoadmapSummaryResponse> rootRoadmaps = roadmapsByDirectory.getOrDefault(-1L, List.of())
                .stream()
                .map(RoadmapSummaryResponse::from)
                .toList();

        List<DirectoryTreeResponse> rootChildren = roots.stream()
                .map(root -> buildTree(root, childrenByParent, roadmapsByDirectory, "/"))
                .toList();

        DirectoryTreeResponse root = DirectoryTreeResponse.builder()
                .id(null)
                .name("Root")
                .path("/")
                .children(rootChildren)
                .roadmaps(rootRoadmaps)
                .build();

        return List.of(root);
    }

    @Transactional
    public DirectoryResponse update(Long directoryId, UpdateDirectoryRequest request, Long userId) {
        Directory directory = findByIdAndOwner(directoryId, userId);
        if (directory == null) {
            throw new ResourceNotFoundException("Directory", directoryId);
        }

        directory.updateName(request.getName());
        String path = calculatePath(directory);
        return DirectoryResponse.from(directory, path);
    }

    private DirectoryTreeResponse buildTree(
            Directory directory,
            Map<Long, List<Directory>> childrenByParent,
            Map<Long, List<Roadmap>> roadmapsByDirectory,
            String parentPath) {
        String currentPath = "/".equals(parentPath)
                ? parentPath + directory.getName()
                : parentPath + "/" + directory.getName();
        List<Directory> children = childrenByParent.getOrDefault(directory.getId(), List.of());
        List<Roadmap> roadmaps = roadmapsByDirectory.getOrDefault(directory.getId(), List.of());

        List<DirectoryTreeResponse> childTrees = children.stream()
                .map(child -> buildTree(child, childrenByParent, roadmapsByDirectory, currentPath))
                .toList();

        List<RoadmapSummaryResponse> roadmapResponses = roadmaps.stream()
                .map(RoadmapSummaryResponse::from)
                .toList();

        return DirectoryTreeResponse.builder()
                .id(directory.getId())
                .name(directory.getName())
                .path(currentPath)
                .children(childTrees)
                .roadmaps(roadmapResponses)
                .build();
    }

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