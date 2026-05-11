package gajeman.jagalchi.jagalchiserver.presentation.user.response;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;

import java.util.Map;

public record QueryUserDto(
        String name,
        String email,
        String profileImageUrl,
        String bio,
        boolean isFollowed,
        FollowDto stats,
        Map<String, String> externalLinks
) {
    public static QueryUserDto From(Users user, boolean isFollowed, long followerCount, long followingCount, Map<String, String> externalLinks) {
        return new QueryUserDto(
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getBio(),
                isFollowed,
                FollowDto.from(
                        followerCount,
                        followingCount
                ),
                externalLinks
        );
    }
}
