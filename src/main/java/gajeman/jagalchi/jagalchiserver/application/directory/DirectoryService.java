package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryTreeResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapSummaryResponse;
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
}
