package gajeman.jagalchi.jagalchiserver.application.auth.service;

import gajeman.jagalchi.jagalchiserver.application.auth.usecase.DeleteAccountUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAccountCommand implements DeleteAccountUseCase {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public void deleteAccount(Users user) {
        Users currentUser = usersRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        currentUser.changeActive();

        usersRepository.save(currentUser);
    }

}
