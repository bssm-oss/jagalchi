package gajeman.jagalchi.jagalchiserver.presentation.user.dto.response;

import java.util.List;

public record FollowListResponse(
        Long userId,
        String type,
        long totalCount,
        List<FollowUserResponse> users
) {
    public static FollowListResponse of(
            Long userId,
            String type,
            List<FollowUserResponse> users
    ) {
        return new FollowListResponse(
                userId,
                type,
                users.size(),
                users
        );
    }
}
