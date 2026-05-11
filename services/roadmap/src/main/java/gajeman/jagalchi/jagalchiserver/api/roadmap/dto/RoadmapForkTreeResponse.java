package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoadmapForkTreeResponse {
    private Long id;
    private String title;
    private Long ownerId;
    private String ownerName;
    private Long forkCount;
    private List<RoadmapForkTreeResponse> children;

    public static RoadmapForkTreeResponse from(Roadmap roadmap, String ownerName, List<RoadmapForkTreeResponse> children) {
        return RoadmapForkTreeResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .ownerId(roadmap.getOwnerId())
                .ownerName(ownerName)
                .forkCount(roadmap.getForkCount())
                .children(children)
                .build();
    }
}
