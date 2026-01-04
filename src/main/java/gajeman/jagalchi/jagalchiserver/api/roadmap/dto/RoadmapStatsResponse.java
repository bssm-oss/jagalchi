package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoadmapStatsResponse {

    private Long totalNodes;
    private Long totalEdges;
    private Long forkCount;
}
