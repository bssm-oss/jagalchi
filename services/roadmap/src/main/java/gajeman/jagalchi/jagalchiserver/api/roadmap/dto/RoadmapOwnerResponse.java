package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import gajeman.jagalchi.jagalchiserver.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoadmapOwnerResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;

    public static RoadmapOwnerResponse from(User user) {
        return RoadmapOwnerResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
