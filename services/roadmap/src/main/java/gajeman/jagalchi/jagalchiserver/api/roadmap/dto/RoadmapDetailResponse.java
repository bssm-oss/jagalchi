package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class RoadmapDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Boolean isPublic;
    private Long viewCount;
    private RoadmapOwnerResponse owner;
    private RoadmapStatsResponse stats;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoadmapDetailResponse from(Roadmap roadmap, gajeman.jagalchi.jagalchiserver.domain.user.User owner, long totalNodes, long totalEdges) {
        return RoadmapDetailResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .thumbnailUrl(roadmap.getThumbnailUrl())
                .isPublic(roadmap.getIsPublic())
                .viewCount(roadmap.getViewCount())
                .owner(RoadmapOwnerResponse.from(owner))
                .stats(RoadmapStatsResponse.builder()
                        .totalNodes(totalNodes)
                        .totalEdges(totalEdges)
                        .forkCount(roadmap.getForkCount())
                        .build())
                .tags(parseTags(roadmap.getTags()))
                .createdAt(roadmap.getCreatedAt())
                .updatedAt(roadmap.getUpdatedAt())
                .build();
    }

    private static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }
}
