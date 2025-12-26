package gajeman.jagalchi.jagalchiserver.application.progress;

import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgress;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final RoadmapNodeProgressRepository progressRepository;

    @Transactional
    public void completeNode(Long roadmapId, Long nodeId, Long userId, Boolean isCompleted) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        if (!roadmap.isAccessibleBy(userId)) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        RoadmapNode node = roadmapNodeRepository.findById(nodeId)
                .filter(found -> found.getRoadmapId().equals(roadmapId))
                .orElseThrow(() -> new ResourceNotFoundException("RoadmapNode", nodeId));

        RoadmapNodeProgress progress = progressRepository
                .findByRoadmapIdAndNodeIdAndUserId(roadmapId, nodeId, userId)
                .orElseGet(() -> RoadmapNodeProgress.builder()
                        .roadmapId(roadmapId)
                        .nodeId(nodeId)
                        .userId(userId)
                        .build());

        progress.toggleComplete(isCompleted != null ? isCompleted : true);
        progressRepository.save(progress);
    }
}
