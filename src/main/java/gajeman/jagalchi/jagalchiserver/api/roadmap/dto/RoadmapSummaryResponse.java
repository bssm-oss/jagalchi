package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoadmapSummaryResponse {

    private Long id;
    private String title;
    private String thumbnailUrl;
    private Boolean isPublic;
    private LocalDateTime updatedAt;

    public static RoadmapSummaryResponse from(Roadmap roadmap) {
        return RoadmapSummaryResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .thumbnailUrl(roadmap.getThumbnailUrl())
                .isPublic(roadmap.getIsPublic())
                .updatedAt(roadmap.getUpdatedAt())
                .build();
    }
}
