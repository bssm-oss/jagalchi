package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoadmapForkStatusResponse {

    private Long roadmapId;
    private Long forkCount;
    private boolean isForkedByCurrentUser;
    private Long originalRoadmapId;
    private String originalRoadmapTitle;

    public static RoadmapForkStatusResponse of(Long roadmapId, Long forkCount, boolean isForkedByCurrentUser, Long originalRoadmapId, String originalRoadmapTitle) {
        return RoadmapForkStatusResponse.builder()
                .roadmapId(roadmapId)
                .forkCount(forkCount)
                .isForkedByCurrentUser(isForkedByCurrentUser)
                .originalRoadmapId(originalRoadmapId)
                .originalRoadmapTitle(originalRoadmapTitle)
                .build();
    }
}
