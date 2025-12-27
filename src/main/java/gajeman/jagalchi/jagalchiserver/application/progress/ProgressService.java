package gajeman.jagalchi.jagalchiserver.application.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.ProgressResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
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

    private BigDecimal calculateProgressPercentage(long totalNodes, long completedNodes) {
        if (totalNodes == 0) {
            return new BigDecimal("0.0");
        }
        return BigDecimal.valueOf(completedNodes)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalNodes), 1, RoundingMode.HALF_UP);
    }
}
