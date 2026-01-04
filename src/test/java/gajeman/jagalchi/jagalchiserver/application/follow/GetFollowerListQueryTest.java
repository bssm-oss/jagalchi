package gajeman.jagalchi.jagalchiserver.application.follow;

import gajeman.jagalchi.jagalchiserver.application.follow.service.GetFollowerListQuery;
import gajeman.jagalchi.jagalchiserver.domain.follow.Follow;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow.FollowRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowListResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetFollowerListQueryTest {

    @InjectMocks
    private GetFollowerListQuery getFollowerListQuery;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UsersRepository userRepository;

    @Test
    void 팔로워_목록을_조회한다() {
        // given
        String name = "me";

        Users me = Users.from("me@test.com", "pw", name);
        Users follower1 = Users.from("a@test.com", "pw", "followerA");
        Users follower2 = Users.from("b@test.com", "pw", "followerB");

        Follow follow1 = Follow.of(follower1, me);
        Follow follow2 = Follow.of(follower2, me);

        given(userRepository.findByName(name))
                .willReturn(Optional.of(me));

        given(followRepository.findByFollowing(me))
                .willReturn(List.of(follow1, follow2));

        given(followRepository.existsByFollowerAndFollowing(me, follower1))
                .willReturn(true);

        given(followRepository.existsByFollowerAndFollowing(me, follower2))
                .willReturn(false);

        // when
        FollowListResponse response =
                getFollowerListQuery.getFollowerList(name);

        // then
        assertThat(response.userId()).isEqualTo(me.getId());
        assertThat(response.type()).isEqualTo("FOLLOWER");

        List<FollowUserResponse> users = response.users();
        assertThat(users).hasSize(2);

        assertThat(users.get(0).isFollowing()).isTrue();
        assertThat(users.get(1).isFollowing()).isFalse();
    }

    @Test
    void 유저가_없으면_예외가_발생한다() {
        // given
        given(userRepository.findByName("unknown"))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                getFollowerListQuery.getFollowerList("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저 없음");
    }
}
