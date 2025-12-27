package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;

    @Transactional
    public RoadmapDetailResponse getDetail(Long roadmapId, Long userId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        if (!roadmap.isAccessibleBy(userId)) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        if (roadmap.getIsPublic() && (userId == null || !roadmap.isOwnedBy(userId))) {
            roadmap.incrementViewCount();
        }

        long totalNodes = roadmapNodeRepository.countByRoadmapId(roadmapId);
        long totalEdges = Math.max(totalNodes - 1, 0);
        return RoadmapDetailResponse.from(roadmap, totalNodes, totalEdges);
    }
}
