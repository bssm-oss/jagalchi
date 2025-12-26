package gajeman.jagalchi.jagalchiserver.api.progress.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProgressResponse {

    private Long roadmapId;
    private Long userId;
    private Long totalNodes;
    private Long completedNodes;
    private BigDecimal progressPercentage;
}
