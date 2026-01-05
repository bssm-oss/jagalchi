package gajeman.jagalchi.jagalchiserver.application.auth.service;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.application.auth.usecase.RefreshAccessTokenUseCase;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshAccessTokenCommand implements RefreshAccessTokenUseCase {

    private final TokenService tokenService;

    public LoginResult refreshAccessToken(String refreshToken) {
        LoginResult result = tokenService.refreshAccessToken(refreshToken);

        return result;
    }

}
