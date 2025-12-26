package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoadmapSummaryResponse {

    private Long id;
    private String title;

    public static RoadmapSummaryResponse from(Roadmap roadmap) {
        return RoadmapSummaryResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .build();
    }
}
