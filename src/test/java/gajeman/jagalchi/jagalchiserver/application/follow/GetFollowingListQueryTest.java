package gajeman.jagalchi.jagalchiserver.application.follow;

import gajeman.jagalchi.jagalchiserver.application.follow.service.GetFollowingListQuery;
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
class GetFollowingListQueryTest {

    @InjectMocks
    private GetFollowingListQuery getFollowingListQuery;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UsersRepository userRepository;

    @Test
    void 팔로잉_목록을_조회한다() {
        // given
        String name = "me";

        Users me = Users.from("me@test.com", "pw", name);
        Users following1 = Users.from("a@test.com", "pw", "followingA");
        Users following2 = Users.from("b@test.com", "pw", "followingB");

        Follow follow1 = Follow.of(me, following1);
        Follow follow2 = Follow.of(me, following2);

        given(userRepository.findByName(name))
                .willReturn(Optional.of(me));

        given(followRepository.findByFollower(me))
                .willReturn(List.of(follow1, follow2));

        given(followRepository.existsByFollowerAndFollowing(following1, me))
                .willReturn(true);

        given(followRepository.existsByFollowerAndFollowing(following2, me))
                .willReturn(false);

        // when
        FollowListResponse response =
                getFollowingListQuery.getFollowingList(name);

        // then
        assertThat(response.userId()).isEqualTo(me.getId());
        assertThat(response.type()).isEqualTo("FOLLOWING");

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
                getFollowingListQuery.getFollowingList("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저 없음");
    }
}
