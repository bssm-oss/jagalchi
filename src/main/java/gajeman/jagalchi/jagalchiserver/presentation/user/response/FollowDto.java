package gajeman.jagalchi.jagalchiserver.presentation.user.response;

public record FollowDto(
        long followersCount,
        long followingCount
) {
    public static FollowDto from(long followersCount, long followingCount) {
        return new FollowDto(followersCount, followingCount);
    }
}
