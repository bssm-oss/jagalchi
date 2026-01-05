package gajeman.jagalchi.jagalchiserver.application.follow.usecase;

import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowListResponse;

public interface GetFollowerListUseCase {
    /**
     * 팔로워 리스트를 반환하는 메서드
     * @param name 유저닉네임
     */
    FollowListResponse getFollowerList(String name);
}
