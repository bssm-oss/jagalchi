package gajeman.jagalchi.jagalchiserver.application.auth;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.application.auth.service.LoginCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.WrongLoginException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.service.TokenService;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginCommandTest {

    @InjectMocks
    private LoginCommand loginCommand;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void 로그인에_성공한다() {
        // given
        String email = "test@test.com";
        String rawPassword = "1234";
        String encodedPassword = "encoded";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        LoginRequest request = new LoginRequest(email, rawPassword);

        Users user = Users.from(email, encodedPassword, "이건우");

        given(usersRepository.findByEmail(email))
                .willReturn(Optional.of(user));

        given(bCryptPasswordEncoder.matches(rawPassword, encodedPassword))
                .willReturn(true);

        given(tokenService.generateAccessToken(user))
                .willReturn(accessToken);

        given(tokenService.generateRefreshToken(user))
                .willReturn(refreshToken);

        // when
        LoginResult result = loginCommand.login(request);

        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);

        verify(tokenService).generateAccessToken(user);
        verify(tokenService).generateRefreshToken(user);
    }

    @Test
    void 유저가_없으면_로그인에_실패한다() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "1234");

        given(usersRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> loginCommand.login(request));

        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }

    @Test
    void 비밀번호가_일치하지_않으면_로그인에_실패한다() {
        // given
        String email = "test@test.com";
        String rawPassword = "1234";
        String encodedPassword = "encoded";

        LoginRequest request = new LoginRequest(email, rawPassword);
        Users user = Users.from(email, encodedPassword, "이건우");

        given(usersRepository.findByEmail(email))
                .willReturn(Optional.of(user));

        given(bCryptPasswordEncoder.matches(rawPassword, encodedPassword))
                .willReturn(false);

        // when & then
        assertThrows(WrongLoginException.class,
                () -> loginCommand.login(request));

        verify(tokenService, never()).generateAccessToken(any());
        verify(tokenService, never()).generateRefreshToken(any());
    }
}
