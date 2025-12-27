package gajeman.jagalchi.jagalchiserver.api.progress.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProgressResponse {

    private Long roadmapId;
    private Long totalNodes;
    private Long completedNodes;
    private BigDecimal progressPercentage;
    private List<Long> completedNodeIds;
    private LocalDateTime updatedAt;
}
