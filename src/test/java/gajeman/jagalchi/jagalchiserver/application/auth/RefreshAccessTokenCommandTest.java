package gajeman.jagalchi.jagalchiserver.application.auth;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.application.auth.service.RefreshAccessTokenCommand;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.TokenNotEqualsException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshAccessTokenCommandTest {

    @InjectMocks
    private RefreshAccessTokenCommand refreshAccessTokenCommand;

    @Mock
    private TokenService tokenService;

    @Test
    void 리프레시_토큰으로_액세스_토큰을_재발급한다() {
        // given
        String refreshToken = "refresh-token";

        LoginResult expected = LoginResult.from(
                "new-access-token",
                "new-refresh-token"
        );

        given(tokenService.refreshAccessToken(refreshToken))
                .willReturn(expected);

        // when
        LoginResult result =
                refreshAccessTokenCommand.refreshAccessToken(refreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

        verify(tokenService).refreshAccessToken(refreshToken);
    }

    @Test
    void 유효하지_않은_리프레시_토큰이면_예외가_발생한다() {
        // given
        String refreshToken = "invalid-refresh-token";

        given(tokenService.refreshAccessToken(refreshToken))
                .willThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."));

        // when & then
        assertThrows(TokenNotEqualsException.class,
                () -> refreshAccessTokenCommand.refreshAccessToken(refreshToken));
    }

    //깃허브 좆같은 새끼들
}
