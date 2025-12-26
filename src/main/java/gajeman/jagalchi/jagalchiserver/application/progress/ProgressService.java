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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final RoadmapNodeProgressRepository progressRepository;

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

        BigDecimal percentage;
        if (totalNodes == 0) {
            percentage = BigDecimal.ZERO;
        } else {
            percentage = BigDecimal.valueOf(completedNodes)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalNodes), 1, RoundingMode.HALF_UP);
        }

        return ProgressResponse.builder()
                .roadmapId(roadmapId)
                .userId(userId)
                .totalNodes(totalNodes)
                .completedNodes(completedNodes)
                .progressPercentage(percentage)
                .build();
    }
}
