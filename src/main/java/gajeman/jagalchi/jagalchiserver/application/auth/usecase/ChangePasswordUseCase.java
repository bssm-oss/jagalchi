package gajeman.jagalchi.jagalchiserver.application.auth.usecase;

import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    /**
     * 비밀번호 변경 메서드
     * @param request 이메일, 신규비밀번호를 담은 DTO
     */
    void changePassword(ChangePasswordRequest request);
}
