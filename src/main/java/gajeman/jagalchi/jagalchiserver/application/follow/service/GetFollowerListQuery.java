package gajeman.jagalchi.jagalchiserver.application.follow.service;

import gajeman.jagalchi.jagalchiserver.application.follow.usecase.GetFollowerListUseCase;
import gajeman.jagalchi.jagalchiserver.domain.follow.Follow;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow.FollowRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowListResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFollowerListQuery implements GetFollowerListUseCase {

    private final FollowRepository followRepository;
    private final UsersRepository userRepository;

    @Override
    public FollowListResponse getFollowerList(String name) {

        Users me = userRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<Follow> follows = followRepository.findByFollowing(me);

        List<FollowUserResponse> users = follows.stream()
                .map(follow -> {
                    Users follower = follow.getFollower();

                    boolean isFollowingBack =
                            followRepository.existsByFollowerAndFollowing(me, follower);

                    return FollowUserResponse.from(follower, isFollowingBack);
                })
                .toList();

        return FollowListResponse.of(me.getId(), "FOLLOWER", users);
    }

}

