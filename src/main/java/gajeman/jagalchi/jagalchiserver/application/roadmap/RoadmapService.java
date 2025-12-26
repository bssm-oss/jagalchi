package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final DirectoryRepository directoryRepository;

    @Transactional
    public void delete(Long roadmapId, Long userId) {
        Roadmap roadmap = findByIdAndOwner(roadmapId, userId);
        if (roadmap == null) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }
        roadmapRepository.delete(roadmap);
    }

    private Roadmap findByIdAndOwner(Long roadmapId, Long ownerId) {
        return roadmapRepository.findById(roadmapId)
                .filter(roadmap -> roadmap.isOwnedBy(ownerId))
                .orElse(null);
    }
}
