package gajeman.jagalchi.jagalchiserver.application.auth.service;

import gajeman.jagalchi.jagalchiserver.application.auth.usecase.ChangePasswordUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.NotVerificationException;
import gajeman.jagalchi.jagalchiserver.domain.verification.exception.VerificationNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.ChangePasswordRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordCommand implements ChangePasswordUseCase {

    private final UsersRepository usersRepository;
    private final VerificationRepository verificationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        validate(request.getEmail());

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);

        user.changePassword(bCryptPasswordEncoder.encode(request.getNewPassword()));

    }

    /**
     * 비밀번호 인증코드 인증 후 삭제 하는 메서드
     * @param email 인증객체를 찾기 위한 매개변수
     */
    private void validate(String email){
        Verification verification = verificationRepository.findByEmail(email)
                .orElseThrow(VerificationNotFoundException::new);

        if(!verification.isVerified() || verification.getType() != VerificationType.UPDATE_PASSWORD){
            throw new NotVerificationException();
        }else{
            verificationRepository.delete(verification);
        }
    }
}
