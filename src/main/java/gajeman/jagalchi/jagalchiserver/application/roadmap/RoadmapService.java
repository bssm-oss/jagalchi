package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.UpdateRoadmapRequest;
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
    public RoadmapResponse update(Long roadmapId, UpdateRoadmapRequest request, Long userId) {
        Roadmap roadmap = findByIdAndOwner(roadmapId, userId);
        if (roadmap == null) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        if (request.getDirectoryId() != null) {
            directoryRepository.findById(request.getDirectoryId())
                    .filter(it -> it.isOwnedBy(userId))
                    .orElseThrow(() -> new ResourceNotFoundException("Directory", request.getDirectoryId()));
        }

        roadmap.update(
                request.getTitle(),
                request.getDescription(),
                request.getDirectoryId(),
                request.getIsPublic());

        return RoadmapResponse.from(roadmap);
    }

    private Roadmap findByIdAndOwner(Long roadmapId, Long ownerId) {
        return roadmapRepository.findById(roadmapId)
                .filter(roadmap -> roadmap.isOwnedBy(ownerId))
                .orElse(null);
    }
}
