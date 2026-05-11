package gajeman.jagalchi.jagalchiserver.presentation.user.response;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;

import java.util.Map;

public record QueryUserResponse(
        QueryUserDto user,
        StreakResponseDto streak
) {
    public static QueryUserResponse from(Users user, boolean isFollowed, long followerCount, long followingCount, Map<String, String> externalLinks, StreakResponseDto streak) {
        return new QueryUserResponse(
                QueryUserDto.From(user, isFollowed, followerCount, followingCount, externalLinks),
                streak
        );
    }
}
