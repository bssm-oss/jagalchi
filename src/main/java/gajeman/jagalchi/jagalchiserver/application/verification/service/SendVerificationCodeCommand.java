package gajeman.jagalchi.jagalchiserver.application.verification.service;

import gajeman.jagalchi.jagalchiserver.application.verification.usecase.SendVerificationCodeUseCase;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.infrastructure.mail.MailUtil;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.SendVerificationCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendVerificationCodeCommand implements SendVerificationCodeUseCase {

    private final MailUtil mailUtil;
    private final VerificationRepository verificationRepository;

    @Override
    public void sendVerificationCode(SendVerificationCodeRequest request, VerificationType type){
        Verification verification = Verification.from(request.getEmail(), type);

        mailUtil.sendMimeMessage(verification.getEmail(), verification.getCode());

        verificationRepository.save(verification);
    }

}
