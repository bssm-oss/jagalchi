package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class RoadmapListItemResponse {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Boolean isPublic;
    private Long viewCount;
    private Long forkCount;
    private List<String> tags;
    private RoadmapOwnerResponse owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoadmapListItemResponse from(Roadmap roadmap, User owner) {
        return RoadmapListItemResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .thumbnailUrl(roadmap.getThumbnailUrl())
                .isPublic(roadmap.getIsPublic())
                .viewCount(roadmap.getViewCount())
                .forkCount(roadmap.getForkCount())
                .tags(parseTags(roadmap.getTags()))
                .owner(RoadmapOwnerResponse.from(owner))
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
