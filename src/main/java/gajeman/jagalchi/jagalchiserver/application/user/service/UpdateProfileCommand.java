package gajeman.jagalchi.jagalchiserver.application.user.service;

import gajeman.jagalchi.jagalchiserver.application.user.usecase.UpdateProfileUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateProfileCommand implements UpdateProfileUseCase {

    private final UsersRepository usersRepository;

    @Transactional
    @Override
    public void updateProfile(Users user, UpdateProfileRequest request){
        user.updateProfile(
                request.getUser().getProfileImage(),
                request.getUser().getBio(),
                request.getUser().getExternalLinks()
        );

        usersRepository.save(user);
    }

}
