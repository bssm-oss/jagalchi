package gajeman.jagalchi.jagalchiserver.application.verification.usecase;

import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.VerifyRequest;

public interface ValidVerificationCodeUseCase {

    /**
     사용자 작성한 인증코드가 올바른지 검증
     * @param request 인증코드와 이메일 정보를 담은 요청 DTO
     */
    void validVerificationCode(VerifyRequest request);
}
