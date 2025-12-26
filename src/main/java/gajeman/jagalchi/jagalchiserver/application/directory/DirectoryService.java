package gajeman.jagalchi.jagalchiserver.application.directory;

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
    public void delete(Long directoryId, Long userId) {
        Directory directory = findByIdAndOwner(directoryId, userId);
        if (directory == null) {
            throw new ResourceNotFoundException("Directory", directoryId);
        }

        List<Directory> children = directoryRepository.findByOwnerIdAndParentId(userId, directoryId);
        for (Directory child : children) {
            child.moveTo(directory.getParentId());
        }

        List<Roadmap> roadmaps = roadmapRepository.findByOwnerIdAndDirectoryId(userId, directoryId);
        for (Roadmap roadmap : roadmaps) {
            roadmap.moveToDirectory(directory.getParentId());
        }

        directoryRepository.delete(directory);
    }

    private Directory findByIdAndOwner(Long directoryId, Long ownerId) {
        return directoryRepository.findById(directoryId)
                .filter(directory -> directory.isOwnedBy(ownerId))
                .orElse(null);
    }
}
