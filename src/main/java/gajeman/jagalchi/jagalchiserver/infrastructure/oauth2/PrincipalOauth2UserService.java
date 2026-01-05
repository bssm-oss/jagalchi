package gajeman.jagalchi.jagalchiserver.infrastructure.oauth2;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository userRepository;

    /**
     * 로그인 변경 메서드
     * @param userRequest 로그인용 OAuth2 유저 객체
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = getEmail(registrationId, oauth2User);
        String name = getName(registrationId, oauth2User);

        Users user = userRepository.findByEmail(email)
                .orElseGet(() ->
                        userRepository.save(
                                Users.builder()
                                        .email(email)
                                        .name(name)
                                        .password(null)
                                        .build()
                        )
                );

        return new PrincipalDetails(user, oauth2User.getAttributes());
    }

    /**
     * 이메일 확인용 메서드
     * @param registrationId github/google 구분용
     * @param oauth2User OAuth2 유저
     */
    private String getEmail(String registrationId, OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");

        if (email == null && "github".equals(registrationId)) {
            String login = oauth2User.getAttribute("login");
            email = login + "@users.noreply.github.com";
        }

        return email;
    }

    /**
     * 닉네임 확인용 메서드
     * @param registrationId github/google 구분용
     * @param oauth2User OAuth2 유저
     */
    private String getName(String registrationId, OAuth2User oauth2User) {
        String name = oauth2User.getAttribute("name");

        if (name == null && "github".equals(registrationId)) {
            name = oauth2User.getAttribute("login");
        }

        return name;
    }
}