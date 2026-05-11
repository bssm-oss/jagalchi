package gajeman.jagalchi.jagalchiserver.application.auth.service;

import gajeman.jagalchi.jagalchiserver.application.auth.usecase.SignUpUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.NotVerificationException;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.VerificationNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.SignUpRequest;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCommand implements SignUpUseCase {

    private final VerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    @Override
    public SignUpResponse signUp(SignUpRequest request){
        validate(request.getEmail());

        Users user = usersRepository.save(
                Users.from(
                        request.getEmail(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getName()
                )
        );

        SignUpResponse response = SignUpResponse.from(user);

        return response;
    }

    /**
     * 인증코드 검증 메서드
     * 검증이 될 시 삭제
     * @param email 인증코드를 찾기 위한 이메일
     */
    private void validate(String email){
        Verification verification = verificationRepository.findByEmail(email)
                .orElseThrow(VerificationNotFoundException::new);

        if(!verification.isVerified() || verification.getType() != VerificationType.SIGN_UP){
            throw new NotVerificationException();
        }else{
            verificationRepository.delete(verification);
        }
    }

}
