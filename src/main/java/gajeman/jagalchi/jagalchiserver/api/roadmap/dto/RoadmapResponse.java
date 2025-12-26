package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoadmapResponse {

    private Long id;
    private String title;
    private String description;
    private Long directoryId;
    private Long ownerId;
    private Boolean isPublic;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoadmapResponse from(Roadmap roadmap) {
        return RoadmapResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .directoryId(roadmap.getDirectoryId())
                .ownerId(roadmap.getOwnerId())
                .isPublic(roadmap.getIsPublic())
                .viewCount(roadmap.getViewCount())
                .createdAt(roadmap.getCreatedAt())
                .updatedAt(roadmap.getUpdatedAt())
                .build();
    }
}
