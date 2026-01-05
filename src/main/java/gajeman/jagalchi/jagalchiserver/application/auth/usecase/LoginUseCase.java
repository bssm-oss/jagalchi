package gajeman.jagalchi.jagalchiserver.application.auth.usecase;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.LoginRequest;

public interface LoginUseCase {
    /**
     * 로그인 메서드
     * @param request 이메일, 비밀번호를 담은 DTO
     */
    LoginResult login(LoginRequest request);
}
