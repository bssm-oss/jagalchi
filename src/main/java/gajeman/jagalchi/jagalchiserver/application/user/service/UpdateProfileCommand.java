package gajeman.jagalchi.jagalchiserver.application.user.service;

import gajeman.jagalchi.jagalchiserver.application.user.usecase.UpdateProfileUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.ExternalLinksLimitExceededException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class UpdateProfileCommand implements UpdateProfileUseCase {

    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void updateProfile(Users user, UpdateProfileRequest request){
        HashMap<String, String> externalLinks = request.getUser().getExternalLinks();

        validate(externalLinks);

        String jsonExternalLinks = objectMapper.writeValueAsString(externalLinks);

        user.updateProfile(
                request.getUser().getProfileImage(),
                request.getUser().getBio(),
                jsonExternalLinks
        );

        usersRepository.save(user);
    }

    private void validate(HashMap<String, String> externalLinks) {
        if(externalLinks.size() > 5){
            throw new ExternalLinksLimitExceededException();
        }
    }

}
