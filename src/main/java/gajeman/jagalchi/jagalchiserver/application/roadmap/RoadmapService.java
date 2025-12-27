package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapUpdateResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.UpdateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
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

    @Transactional
    public RoadmapUpdateResponse update(Long roadmapId, UpdateRoadmapRequest request, Long userId) {
        Roadmap roadmap = findByIdAndOwner(roadmapId, userId);
        if (roadmap == null) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        roadmap.update(
                request.getTitle(),
                request.getDescription(),
                request.getIsPublic(),
                request.getThumbnailUrl());
        if (request.getTags() != null) {
            roadmap.updateTags(joinTags(request.getTags()));
        }

        return RoadmapUpdateResponse.builder()
                .id(roadmap.getId())
                .updatedAt(roadmap.getUpdatedAt())
                .build();
    }

    private Roadmap findByIdAndOwner(Long roadmapId, Long ownerId) {
        return roadmapRepository.findById(roadmapId)
                .filter(roadmap -> roadmap.isOwnedBy(ownerId))
                .orElse(null);
    }

    private String joinTags(java.util.List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }
}
