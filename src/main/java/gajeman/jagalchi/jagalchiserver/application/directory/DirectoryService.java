package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final RoadmapRepository roadmapRepository;

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
