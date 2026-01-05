package gajeman.jagalchi.jagalchiserver.presentation.user;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.application.auth.service.*;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.cookie.CookieUtil;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.ChangePasswordRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.LoginRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.RefreshTokenRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.SignUpRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.LoginResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.SignUpResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final SignUpCommand signUpCommand;
    private final ChangePasswordCommand changePasswordCommand;
    private final LoginCommand loginCommand;
    private final CookieUtil cookieUtil;
    private final RefreshAccessTokenCommand refreshAccessTokenCommand;
    private final DeleteAccountCommand deleteAccountCommand;

    /**
     * 회원가입 메서드
     * @param request 이메일, 비밀번호, 이름을 담은 DTO
     */
    @PostMapping
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(signUpCommand.signUp(request));
    }

    /**
     * 비밀번호 변경 메서드
     * @param request 이메일, 신규비밀번호를 담은 DTO
     */
    @PatchMapping("/auth/password-reset")
    public void changePassword(
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        changePasswordCommand.changePassword(request);
    }

    /**
     * 로그인 변경 메서드
     * @param request 로그인용 이메일, 비밀번호를 담은 DTO
     */
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse httpServletResponse
    ) {
        LoginResult result = loginCommand.login(request);

        LoginResponse response = LoginResponse.from(result.accessToken());

        cookieUtil.addRefreshToken(httpServletResponse, result.refreshToken(), true);

        return ResponseEntity.ok(response);
    }

    /**
     * 구글 로그인 메서드
     * @param response 리다이렉트용
     */
    @GetMapping("/auth/login/google")
    public void loginGoogle(
            HttpServletResponse response
    ) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    /**
     * 깃허브 로그인 메서드
     * @param response 리다이렉트용
     */
    @GetMapping("/auth/login/github")
    public void loginGithub(
            HttpServletResponse response
    ) throws IOException {
        response.sendRedirect("/oauth2/authorization/github");
    }

    /**
     * 리프레시 토큰 재발급 메서드
     * @param request 리프레시 토큰
     */
    @PatchMapping("/auth/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletResponse httpServletResponse
    ){
        LoginResult result = refreshAccessTokenCommand.refreshAccessToken(request.getRefreshToken());

        LoginResponse response = LoginResponse.from(result.accessToken());

        cookieUtil.addRefreshToken(httpServletResponse, result.refreshToken(), true);

        return ResponseEntity.ok(response);
    }

    /**
     * 리프레시 토큰 재발급 메서드
     * @param user 액세스토큰
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUsers(
            @AuthenticationPrincipal Users user
    ) {
        deleteAccountCommand.deleteAccount(user);
        return ResponseEntity.noContent().build();
    }
  
}
