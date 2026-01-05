package gajeman.jagalchi.jagalchiserver.presentation.user.dto.response;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;

public record FollowUserResponse(
        Long id,
        String name,
        String profileImage,
        Boolean isFollowing
) {
    public static FollowUserResponse from(Users user, boolean isFollowing) {
        String url = user.getProfileUrl() == null ? null : user.getProfileUrl();
        return new FollowUserResponse(
                user.getId(),
                user.getName(),
                url,
                isFollowing
        );
    }
}
