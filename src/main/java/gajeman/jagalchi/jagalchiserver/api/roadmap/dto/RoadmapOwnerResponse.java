package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoadmapOwnerResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;

    public static RoadmapOwnerResponse from(Long ownerId) {
        return RoadmapOwnerResponse.builder()
                .id(ownerId)
                .build();
    }
}
