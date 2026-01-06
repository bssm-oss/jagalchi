package gajeman.jagalchi.jagalchiserver.application.auth.service;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.application.auth.usecase.LoginUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.WrongLoginException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.service.TokenService;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginCommand implements LoginUseCase {

    private final TokenService tokenService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public LoginResult login(LoginRequest request) {
        Users user = usersRepository.findByEmail(request.getEmail())
                        .orElseThrow(UserNotFoundException::new);

        validate(request.getPassword(), user.getPassword());

        LoginResult result = LoginResult.from(
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user));

        return result;

    }

    private void validate(String rawPassword, String encodedPassword) {
        if (!bCryptPasswordEncoder.matches(rawPassword, encodedPassword)) {
            throw new WrongLoginException();
        }
    }
}
