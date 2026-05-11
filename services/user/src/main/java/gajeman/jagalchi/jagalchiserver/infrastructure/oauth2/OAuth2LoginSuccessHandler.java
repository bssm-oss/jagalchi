package gajeman.jagalchi.jagalchiserver.infrastructure.oauth2;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.infrastructure.cookie.CookieUtil;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @Value("${front.url}")
    private String frontUrl;

    /**
     * 로그인 성공 핸들러
     * @param request http 요청
     * @param response 쿠키 세팅용
     * @param authentication 시큐리티 인증객체
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        assert principalDetails != null;
        String accessToken = tokenService.generateAccessToken(principalDetails.getUser());
        String refreshToken = tokenService.generateRefreshToken(principalDetails.getUser());

        LoginResult result = LoginResult.from(accessToken, refreshToken);

        cookieUtil.addAccessToken(response, result.accessToken(), true);
        cookieUtil.addRefreshToken(response, result.refreshToken(), true);

        response.sendRedirect(frontUrl);
    }

}