package gajeman.jagalchi.jagalchiserver.application.verification;

import gajeman.jagalchi.jagalchiserver.application.verification.service.SendVerificationCodeCommand;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.infrastructure.mail.MailUtil;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.SendVerificationCodeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SendVerificationCodeCommandTest {

    @InjectMocks
    private SendVerificationCodeCommand sendVerificationCodeCommand;

    @Mock
    private MailUtil mailUtil;

    @Mock
    private VerificationRepository verificationRepository;

    @Test
    void 인증코드를_생성하고_메일을_전송한_뒤_저장한다() {
        // given
        String email = "test@test.com";
        VerificationType type = VerificationType.SIGN_UP;

        SendVerificationCodeRequest request =
                new SendVerificationCodeRequest(email);

        ArgumentCaptor<Verification> captor =
                ArgumentCaptor.forClass(Verification.class);

        // when
        sendVerificationCodeCommand.sendVerificationCode(request, type);

        // then
        then(mailUtil).should().sendMimeMessage(eq(email), anyString());
        then(verificationRepository).should().save(captor.capture());

        Verification saved = captor.getValue();

        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getType()).isEqualTo(type);
        assertThat(saved.getCode()).isNotNull();
        assertThat(saved.isVerified()).isFalse();
    }

    @Test
    void 메일_전송에_실패하면_예외가_전파되고_저장되지_않는다() {
        // given
        String email = "test@test.com";
        VerificationType type = VerificationType.SIGN_UP;

        SendVerificationCodeRequest request =
                new SendVerificationCodeRequest(email);

        willThrow(new RuntimeException("메일 전송 실패"))
                .given(mailUtil)
                .sendMimeMessage(eq(email), anyString());

        // when & then
        assertThatThrownBy(() ->
                sendVerificationCodeCommand.sendVerificationCode(request, type))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("메일 전송 실패");

        then(verificationRepository).should(never()).save(any());
    }
}
