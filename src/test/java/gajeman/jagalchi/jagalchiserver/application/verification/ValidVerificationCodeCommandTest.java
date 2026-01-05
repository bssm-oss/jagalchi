package gajeman.jagalchi.jagalchiserver.application.verification;

import gajeman.jagalchi.jagalchiserver.application.verification.service.ValidVerificationCodeCommand;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.NotVerificationException;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.VerificationNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.VerifyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ValidVerificationCodeCommandTest {

    @InjectMocks
    private ValidVerificationCodeCommand validVerificationCodeCommand;

    @Mock
    private VerificationRepository verificationRepository;

    @Test
    void 인증코드가_일치하면_verified가_true로_변경된다() {
        // given
        String email = "test@test.com";
        Verification verification =
                Verification.from(email, VerificationType.SIGN_UP);

        String code = verification.getCode();

        VerifyRequest request =
                new VerifyRequest(email, code);

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));

        // when
        validVerificationCodeCommand.validVerificationCode(request);

        // then
        assertThat(verification.isVerified()).isTrue();
        then(verificationRepository).should().save(verification);
    }

    @Test
    void 인증정보가_없으면_예외가_발생한다() {
        // given
        VerifyRequest request =
                new VerifyRequest("test@test.com", "123456");

        given(verificationRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(VerificationNotFoundException.class,
                () -> validVerificationCodeCommand.validVerificationCode(request));

        then(verificationRepository).should(never()).save(any());
    }

    @Test
    void 인증코드가_틀리면_예외가_발생한다() {
        // given
        String email = "test@test.com";
        Verification verification =
                Verification.from(email, VerificationType.SIGN_UP);

        VerifyRequest request =
                new VerifyRequest(email, "틀린코드");

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));

        // when & then
        assertThrows(NotVerificationException.class,
                () -> validVerificationCodeCommand.validVerificationCode(request));

        assertThat(verification.isVerified()).isFalse();
        then(verificationRepository).should(never()).save(any());
    }
}
