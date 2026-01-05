package gajeman.jagalchi.jagalchiserver.application.follow.usecase;

import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowListResponse;

public interface GetFollowingListUseCase {
    /**
     * 팔로잉 리스트를 반환하는 메서드
     * @param name 유저 닉네임
     */
    FollowListResponse getFollowingList(String name);
}
