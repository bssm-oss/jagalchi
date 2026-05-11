package gajeman.jagalchi.jagalchiserver.application.user;

import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("ID로 사용자를 조회할 수 있다")
    void findById() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .nickname("testuser")
                .email("test@example.com")
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        User result = userService.findById(userId);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외가 발생한다")
    void findByIdNotFound() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 999");
    }

    @Test
    @DisplayName("여러 ID로 사용자들을 조회할 수 있다")
    void findByIds() {
        // given
        List<Long> userIds = List.of(1L, 2L);
        User user1 = User.builder().nickname("user1").email("user1@example.com").build();
        User user2 = User.builder().nickname("user2").email("user2@example.com").build();
        
        // Reflection을 사용하여 ID 설정 (실제로는 JPA가 설정)
        setId(user1, 1L);
        setId(user2, 2L);
        
        given(userRepository.findByIdIn(userIds)).willReturn(List.of(user1, user2));

        // when
        Map<Long, User> result = userService.findByIds(userIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1L)).isEqualTo(user1);
        assertThat(result.get(2L)).isEqualTo(user2);
    }

    @Test
    @DisplayName("새 사용자를 생성할 수 있다")
    void create() {
        // given
        String nickname = "newuser";
        String email = "new@example.com";
        String profileImageUrl = "profile.jpg";
        
        User savedUser = User.builder()
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .build();
        
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByNickname(nickname)).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        User result = userService.create(nickname, email, profileImageUrl);

        // then
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getProfileImageUrl()).isEqualTo(profileImageUrl);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일로 사용자 생성 시 예외가 발생한다")
    void createWithDuplicateEmail() {
        // given
        String nickname = "newuser";
        String email = "existing@example.com";
        
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(nickname, email, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists: existing@example.com");
    }

    @Test
    @DisplayName("중복된 닉네임으로 사용자 생성 시 예외가 발생한다")
    void createWithDuplicateNickname() {
        // given
        String nickname = "existinguser";
        String email = "new@example.com";
        
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(nickname, email, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nickname already exists: existinguser");
    }

    @Test
    @DisplayName("사용자 프로필을 업데이트할 수 있다")
    void updateProfile() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .nickname("oldnick")
                .email("test@example.com")
                .profileImageUrl("old.jpg")
                .build();
        
        String newNickname = "newnick";
        String newProfileImageUrl = "new.jpg";
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname(newNickname)).willReturn(false);

        // when
        User result = userService.updateProfile(userId, newNickname, newProfileImageUrl);

        // then
        assertThat(result.getNickname()).isEqualTo(newNickname);
        assertThat(result.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
    }

    @Test
    @DisplayName("중복된 닉네임으로 프로필 업데이트 시 예외가 발생한다")
    void updateProfileWithDuplicateNickname() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .nickname("oldnick")
                .email("test@example.com")
                .build();
        
        String duplicateNickname = "existinguser";
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname(duplicateNickname)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(userId, duplicateNickname, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nickname already exists: existinguser");
    }

    // Helper method to set ID using reflection (for testing purposes)
    private void setId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}