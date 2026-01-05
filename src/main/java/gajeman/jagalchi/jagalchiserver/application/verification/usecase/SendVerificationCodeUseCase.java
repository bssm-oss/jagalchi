package gajeman.jagalchi.jagalchiserver.application.verification.usecase;

import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.SendVerificationCodeRequest;

public interface SendVerificationCodeUseCase {
    /**
     * 인증코드 보내는 메서드
     * @param request 인증 코드를 받을 이메일 정보를 담은 요청 DTO
     * @param type 인증 타입 (회원가입, 비밀번호)
     */
    void sendVerificationCode(SendVerificationCodeRequest request, VerificationType type);
}
