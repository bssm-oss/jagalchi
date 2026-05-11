package gajeman.jagalchi.jagalchiserver.presentation.user.response;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;

public record FollowUserResponse(
        Long id,
        String name,
        String profileImage,
        Boolean isFollowing
) {
    public static FollowUserResponse from(Users user, boolean isFollowing) {
        String url = user.getProfileImageUrl() == null ? null : user.getProfileImageUrl();
        return new FollowUserResponse(
                user.getId(),
                user.getName(),
                url,
                isFollowing
        );
    }
}
