package gajeman.jagalchi.jagalchiserver.application.auth;

import gajeman.jagalchi.jagalchiserver.application.auth.service.SignUpCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.SignUpRequest;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.response.SignUpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignUpCommandTest {

    @InjectMocks
    private SignUpCommand signUpCommand;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입에_성공한다() {
        // given
        String email = "test@test.com";
        String rawPassword = "123";
        String encodedPassword = "encodedPassword";

        SignUpRequest request = new SignUpRequest(
                email,
                "이건우",
                rawPassword
        );

        Verification verification =
                Verification.from(email, VerificationType.SIGN_UP);

        String code = verification.getCode();
        verification.verify(code);

        given(verificationRepository.findByEmail(email))
                .willReturn(Optional.of(verification));

        given(passwordEncoder.encode(rawPassword))
                .willReturn(encodedPassword);

        given(usersRepository.save(any(Users.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);

        // when
        SignUpResponse response = signUpCommand.signUp(request);

        // then
        verify(usersRepository).save(captor.capture());

        Users savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getName()).isEqualTo("이건우");

        assertThat(response.email()).isEqualTo(email);
    }
}
