package gajeman.jagalchi.jagalchiserver.application.follow;

import gajeman.jagalchi.jagalchiserver.application.follow.service.ToggleFollowCommand;
import gajeman.jagalchi.jagalchiserver.domain.follow.Follow;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow.FollowRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleFollowCommandTest {

    @InjectMocks
    private ToggleFollowCommand toggleFollowCommand;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private FollowRepository followRepository;

    @Test
    void 팔로우를_추가한다() {
        Users me = Users.from("me@test.com", "pw", "me");
        Users target = Users.from("t@test.com", "pw", "target");

        ReflectionTestUtils.setField(me, "id", 1L);
        ReflectionTestUtils.setField(target, "id", 2L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(me));
        given(usersRepository.findByName("target")).willReturn(Optional.of(target));
        given(followRepository.existsByFollowerAndFollowing(me, target)).willReturn(false);

        toggleFollowCommand.toggleFollowing(1L, "target", true);

        then(followRepository).should().save(any(Follow.class));
    }

    @Test
    void 이미_팔로우한_상태에서_toggle_true면_아무것도_하지_않는다() {
        Users me = Users.from("me@test.com", "pw", "me");
        Users target = Users.from("t@test.com", "pw", "target");

        ReflectionTestUtils.setField(me, "id", 1L);
        ReflectionTestUtils.setField(target, "id", 2L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(me));
        given(usersRepository.findByName("target")).willReturn(Optional.of(target));
        given(followRepository.existsByFollowerAndFollowing(me, target)).willReturn(true);

        toggleFollowCommand.toggleFollowing(1L, "target", true);

        then(followRepository).should(never()).save(any());
        then(followRepository).should(never()).delete(any());
    }

    @Test
    void 팔로우를_취소한다() {
        Users me = Users.from("me@test.com", "pw", "me");
        Users target = Users.from("t@test.com", "pw", "target");
        Follow follow = Follow.of(me, target);

        ReflectionTestUtils.setField(me, "id", 1L);
        ReflectionTestUtils.setField(target, "id", 2L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(me));
        given(usersRepository.findByName("target")).willReturn(Optional.of(target));
        given(followRepository.existsByFollowerAndFollowing(me, target)).willReturn(true);
        given(followRepository.findByFollowerAndFollowing(me, target))
                .willReturn(Optional.of(follow));

        toggleFollowCommand.toggleFollowing(1L, "target", false);

        then(followRepository).should().delete(follow);
    }

    @Test
    void 팔로우하지_않은_상태에서_toggle_false면_아무것도_하지_않는다() {
        Users me = Users.from("me@test.com", "pw", "me");
        Users target = Users.from("t@test.com", "pw", "target");

        ReflectionTestUtils.setField(me, "id", 1L);
        ReflectionTestUtils.setField(target, "id", 2L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(me));
        given(usersRepository.findByName("target")).willReturn(Optional.of(target));
        given(followRepository.existsByFollowerAndFollowing(me, target)).willReturn(false);

        toggleFollowCommand.toggleFollowing(1L, "target", false);

        then(followRepository).should(never()).delete(any());
    }

    @Test
    void 대상_유저가_없으면_예외가_발생한다() {
        Users me = Users.from("me@test.com", "pw", "me");
        ReflectionTestUtils.setField(me, "id", 1L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(me));
        given(usersRepository.findByName("target")).willReturn(Optional.empty());

        assertThatThrownBy(() ->
                toggleFollowCommand.toggleFollowing(1L, "target", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저가 존재하지 않습니다.");
    }

    @Test
    void 자기_자신을_팔로우하려고_하면_예외가_발생한다() {
        Users me = Users.from("me@test.com", "pw", "me");
        ReflectionTestUtils.setField(me, "id", 1L);

        given(usersRepository.findById(1L)).willReturn(Optional.of(me));
        given(usersRepository.findByName("me")).willReturn(Optional.of(me));

        assertThatThrownBy(() ->
                toggleFollowCommand.toggleFollowing(1L, "me", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신은 팔로우할 수 없습니다.");
    }
}
