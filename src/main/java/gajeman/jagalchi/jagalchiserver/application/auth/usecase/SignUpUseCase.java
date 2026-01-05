package gajeman.jagalchi.jagalchiserver.application.auth.usecase;

import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.SignUpRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.SignUpResponse;

public interface SignUpUseCase {
    /**
     * 회원가입 메서드
     * @param request 이메일, 비밀번호, 이름을 담은 DTO
     */
    SignUpResponse signUp(SignUpRequest request);
}
