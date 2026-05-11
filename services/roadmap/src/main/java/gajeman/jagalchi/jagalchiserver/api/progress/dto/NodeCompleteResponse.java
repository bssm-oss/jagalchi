package gajeman.jagalchi.jagalchiserver.api.progress.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class NodeCompleteResponse {

    private Long nodeId;
    private Boolean isCompleted;
    private BigDecimal roadmapProgress;
    private LocalDateTime completedAt;
}
