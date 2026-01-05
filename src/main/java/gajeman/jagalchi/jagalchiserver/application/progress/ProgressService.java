package gajeman.jagalchi.jagalchiserver.application.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.NodeCompleteResponse;
import gajeman.jagalchi.jagalchiserver.api.progress.dto.ProgressResponse;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final RoadmapNodeProgressRepository progressRepository;

    public ProgressResponse getMyProgress(Long roadmapId, Long userId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        if (!roadmap.isAccessibleBy(userId)) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        return calculateProgress(roadmapId, userId);
    }

    public ProgressResponse getUserProgress(Long roadmapId, Long targetUserId, Long requesterId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        if (!roadmap.isOwnedBy(requesterId)) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        return calculateProgress(roadmapId, targetUserId);
    }

    private ProgressResponse calculateProgress(Long roadmapId, Long userId) {
        long totalNodes = roadmapNodeRepository.countByRoadmapId(roadmapId);
        long completedNodes = progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, userId);
        List<Long> completedNodeIds = progressRepository.findCompletedNodeIdsByRoadmapIdAndUserId(roadmapId, userId);
        LocalDateTime updatedAt = progressRepository.findLatestUpdatedAtByRoadmapIdAndUserId(roadmapId, userId)
                .orElse(null);

        BigDecimal percentage = calculateProgressPercentage(totalNodes, completedNodes);

        return ProgressResponse.builder()
                .roadmapId(roadmapId)
                .totalNodes(totalNodes)
                .completedNodes(completedNodes)
                .progressPercentage(percentage)
                .completedNodeIds(completedNodeIds)
                .updatedAt(updatedAt)
                .build();
    }

    @Transactional
    public NodeCompleteResponse completeNode(Long roadmapId, Long nodeId, Long userId, Boolean isCompleted) {
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
        RoadmapNodeProgress saved = progressRepository.save(progress);

        long totalNodes = roadmapNodeRepository.countByRoadmapId(roadmapId);
        long completedNodes = progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, userId);
        BigDecimal progressPercentage = calculateProgressPercentage(totalNodes, completedNodes);

        return NodeCompleteResponse.builder()
                .nodeId(nodeId)
                .isCompleted(saved.getIsCompleted())
                .roadmapProgress(progressPercentage)
                .completedAt(saved.getCompletedAt())
                .build();
    }

    private BigDecimal calculateProgressPercentage(long totalNodes, long completedNodes) {
        if (totalNodes == 0) {
            return new BigDecimal("0.0");
        }
        return BigDecimal.valueOf(completedNodes)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalNodes), 1, RoundingMode.HALF_UP);
    }
}