package gajeman.jagalchi.jagalchiserver.application.auth;

import gajeman.jagalchi.jagalchiserver.application.auth.service.ChangePasswordCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.NotVerificationException;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.VerificationNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.ChangePasswordRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordCommandTest {

    @InjectMocks
    private ChangePasswordCommand changePasswordCommand;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void 비밀번호_변경에_성공한다() {
        // given
        String email = "test@test.com";
        String newPassword = "newPassword";
        String encodedPassword = "encodedPassword";

        ChangePasswordRequest request =
                new ChangePasswordRequest(email, newPassword);

        Verification verification = Verification.from(email, VerificationType.UPDATE_PASSWORD);
        verification.verify(verification.getCode());

        Users user = mock(Users.class);

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));
        given(usersRepository.findByEmail(email))
                .willReturn(Optional.of(user));
        given(bCryptPasswordEncoder.encode(newPassword))
                .willReturn(encodedPassword);

        // when
        changePasswordCommand.changePassword(request);

        // then
        verify(user, times(1)).changePassword(encodedPassword);
        verify(verificationRepository, times(1)).delete(verification);
    }

    @Test
    void 인증코드를_찾을_수_없으면_에러가_발생한다() {
        // given
        ChangePasswordRequest request =
                new ChangePasswordRequest("test@test.com", "newPassword");

        given(verificationRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(VerificationNotFoundException.class,
                () -> changePasswordCommand.changePassword(request));

        verify(usersRepository, never()).findByEmail(any());
    }

    @Test
    void 인증되지_않은_인증코드라면_에러가_발생한다() {
        // given
        String email = "test@test.com";
        ChangePasswordRequest request =
                new ChangePasswordRequest(email, "newPassword");

        Verification verification = Verification.from(email, VerificationType.UPDATE_PASSWORD);

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));

        // when & then
        assertThrows(NotVerificationException.class,
                () -> changePasswordCommand.changePassword(request));

        verify(usersRepository, never()).findByEmail(any());
        verify(verificationRepository, never()).delete(any());
    }

    @Test
    void 인증타입이_다르면_에러가_발생한다() {
        // given
        String email = "test@test.com";
        ChangePasswordRequest request =
                new ChangePasswordRequest(email, "newPassword");

        Verification verification = Verification.from(email, VerificationType.SIGN_UP);
        verification.verify(verification.getCode());

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));

        // when & then
        assertThrows(NotVerificationException.class,
                () -> changePasswordCommand.changePassword(request));

        verify(usersRepository, never()).findByEmail(any());
        verify(verificationRepository, never()).delete(any());
    }

    @Test
    void 유저가_존재하지_않으면_에러가_발생한다() {
        // given
        String email = "test@test.com";
        ChangePasswordRequest request =
                new ChangePasswordRequest(email, "newPassword");

        Verification verification = Verification.from(email, VerificationType.UPDATE_PASSWORD);
        verification.verify(verification.getCode());

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));
        given(usersRepository.findByEmail(email))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> changePasswordCommand.changePassword(request));
    }
}

