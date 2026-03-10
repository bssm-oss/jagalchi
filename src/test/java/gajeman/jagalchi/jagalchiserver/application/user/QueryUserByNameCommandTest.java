package gajeman.jagalchi.jagalchiserver.application.user;

import gajeman.jagalchi.jagalchiserver.application.user.service.QueryUserByNameCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow.FollowRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq.ActivityService;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.QueryUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class QueryUserByNameCommandTest {

    @InjectMocks
    private QueryUserByNameCommand queryUserByNameCommand;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ActivityService activityService;

    @Test
    void 이름으로_사용자를_성공적으로_조회한다() throws Exception {
        // given
        Users targetUser = Users.from("target@test.com", "pw", "타겟유저");
        Users currentUser = Users.from("current@test.com", "pw", "현재유저");

        String externalLinksJson = "{\"github\":\"https://github.com/target\"}";
        targetUser.updateProfile(null, null, externalLinksJson);

        Map<String, String> externalLinks = new HashMap<>();
        externalLinks.put("github", "https://github.com/target");

        given(userRepository.findByName("타겟유저")).willReturn(Optional.of(targetUser));
        given(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)).willReturn(true);
        given(followRepository.countByFollowing(targetUser)).willReturn(100L);
        given(followRepository.countByFollower(targetUser)).willReturn(50L);
        given(objectMapper.readValue(anyString(), any(TypeReference.class))).willReturn(externalLinks);

        // when
        QueryUserResponse response = queryUserByNameCommand.getUserByName("타겟유저", currentUser);

        // then
        assertThat(response).isNotNull();
        assertThat(response.user().name()).isEqualTo("타겟유저");
        assertThat(response.user().isFollowed()).isTrue();
        assertThat(response.user().stats().followersCount()).isEqualTo(100L);
        assertThat(response.user().stats().followingCount()).isEqualTo(50L);
    }

    @Test
    void 비로그인_상태에서_사용자를_조회하면_팔로우_여부는_false이다() throws Exception {
        // given
        Users targetUser = Users.from("target@test.com", "pw", "타겟유저");

        given(userRepository.findByName("타겟유저")).willReturn(Optional.of(targetUser));
        given(followRepository.countByFollowing(targetUser)).willReturn(100L);
        given(followRepository.countByFollower(targetUser)).willReturn(50L);

        // when
        QueryUserResponse response = queryUserByNameCommand.getUserByName("타겟유저", null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.user().isFollowed()).isFalse();
    }

    @Test
    void 팔로우하지_않은_사용자를_조회하면_팔로우_여부는_false이다() throws Exception {
        // given
        Users targetUser = Users.from("target@test.com", "pw", "타겟유저");
        Users currentUser = Users.from("current@test.com", "pw", "현재유저");

        given(userRepository.findByName("타겟유저")).willReturn(Optional.of(targetUser));
        given(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)).willReturn(false);
        given(followRepository.countByFollowing(targetUser)).willReturn(100L);
        given(followRepository.countByFollower(targetUser)).willReturn(50L);

        // when
        QueryUserResponse response = queryUserByNameCommand.getUserByName("타겟유저", currentUser);

        // then
        assertThat(response).isNotNull();
        assertThat(response.user().isFollowed()).isFalse();
    }

    @Test
    void 존재하지_않는_사용자를_조회하면_예외가_발생한다() {
        // given
        given(userRepository.findByName("없는유저")).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> queryUserByNameCommand.getUserByName("없는유저", null));
    }

    @Test
    void externalLinks가_null이면_빈_Map을_반환한다() throws Exception {
        // given
        Users targetUser = Users.from("target@test.com", "pw", "타겟유저");

        given(userRepository.findByName("타겟유저")).willReturn(Optional.of(targetUser));
        given(followRepository.countByFollowing(targetUser)).willReturn(0L);
        given(followRepository.countByFollower(targetUser)).willReturn(0L);

        // when
        QueryUserResponse response = queryUserByNameCommand.getUserByName("타겟유저", null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.user().externalLinks()).isEmpty();
    }
}