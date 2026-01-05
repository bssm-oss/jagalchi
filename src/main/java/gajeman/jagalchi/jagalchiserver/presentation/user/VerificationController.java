package gajeman.jagalchi.jagalchiserver.presentation.user;

import gajeman.jagalchi.jagalchiserver.application.verification.service.SendVerificationCodeCommand;
import gajeman.jagalchi.jagalchiserver.application.verification.service.ValidVerificationCodeCommand;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.SendVerificationCodeRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.VerifyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class VerificationController {

    private final SendVerificationCodeCommand sendVerificationCodeCommand;
    private final ValidVerificationCodeCommand validVerificationCodeCommand;

    /**
     * 회원가입용 이메일 인증 코드를 발송합니다.
     *
     * @param request 이메일 정보를 담은 요청 DTO
     */
    @PostMapping("/verification")
    public void sendSignupVerificationCode(
            @RequestBody @Valid SendVerificationCodeRequest request
    ){
        sendVerificationCodeCommand.sendVerificationCode(request, VerificationType.SIGN_UP);
    }

    /**
     * 비밀번호 재설정용 이메일 인증 코드를 발송합니다.
     *
     * @param request 이메일 정보를 담은 요청 DTO
     */
    @PostMapping("/auth/password-reset")
    public void sendPasswordResetVerificationCode(
            @RequestBody @Valid SendVerificationCodeRequest request
    ){
        sendVerificationCodeCommand.sendVerificationCode(request, VerificationType.UPDATE_PASSWORD);
    }

    /**
     * 회원가입용 인증코드를 검증합니다.
     *
     * @param request 이메일과 인증코드를 담은 요청 DTO
     */
    @PatchMapping("/verification")
    public void validateSignupVerificationCode(
            @RequestBody @Valid VerifyRequest request
    ){
        validVerificationCodeCommand.validVerificationCode(request);
    }

    /**
     * 비밀번호 재설정용 인증코드를 검증합니다.
     *
     * @param request 이메일과 인증코드를 담은 요청 DTO
     */
    @PatchMapping("/auth/password-reset/verify")
    public void validatePasswordResetVerificationCode(
            @RequestBody @Valid VerifyRequest request
    ){
        validVerificationCodeCommand.validVerificationCode(request);
    }


}
