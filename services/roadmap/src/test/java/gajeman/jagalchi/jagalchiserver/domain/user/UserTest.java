package gajeman.jagalchi.jagalchiserver.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("사용자를 생성할 수 있다")
    void createUser() {
        // given
        String nickname = "testuser";
        String email = "test@example.com";
        String profileImageUrl = "https://example.com/profile.jpg";

        // when
        User user = User.builder()
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .build();

        // then
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getProfileImageUrl()).isEqualTo(profileImageUrl);
    }

    @Test
    @DisplayName("사용자 프로필을 업데이트할 수 있다")
    void updateProfile() {
        // given
        User user = User.builder()
                .nickname("oldnick")
                .email("test@example.com")
                .profileImageUrl("old.jpg")
                .build();

        String newNickname = "newnick";
        String newProfileImageUrl = "new.jpg";

        // when
        user.updateProfile(newNickname, newProfileImageUrl);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
        assertThat(user.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
        assertThat(user.getEmail()).isEqualTo("test@example.com"); // 이메일은 변경되지 않음
    }

    @Test
    @DisplayName("닉네임만 업데이트할 수 있다")
    void updateNicknameOnly() {
        // given
        User user = User.builder()
                .nickname("oldnick")
                .email("test@example.com")
                .profileImageUrl("old.jpg")
                .build();

        String newNickname = "newnick";

        // when
        user.updateProfile(newNickname, null);

        // then
        assertThat(user.getNickname()).isEqualTo(newNickname);
        assertThat(user.getProfileImageUrl()).isEqualTo("old.jpg"); // 기존 값 유지
    }

    @Test
    @DisplayName("프로필 이미지만 업데이트할 수 있다")
    void updateProfileImageOnly() {
        // given
        User user = User.builder()
                .nickname("oldnick")
                .email("test@example.com")
                .profileImageUrl("old.jpg")
                .build();

        String newProfileImageUrl = "new.jpg";

        // when
        user.updateProfile(null, newProfileImageUrl);

        // then
        assertThat(user.getNickname()).isEqualTo("oldnick"); // 기존 값 유지
        assertThat(user.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
    }
}