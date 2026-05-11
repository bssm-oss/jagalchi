package gajeman.jagalchi.jagalchiserver.application.auth.usecase;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;

public interface RefreshAccessTokenUseCase {
    /**
     * 액세스 토큰 재발급 메서드
     * @param refreshToken 리프레시 토큰
     */
    LoginResult refreshAccessToken(String refreshToken);
}
