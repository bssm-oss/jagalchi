package gajeman.jagalchi.jagalchiserver.application.user;

import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public Map<Long, User> findByIds(List<Long> userIds) {
        return userRepository.findByIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Transactional
    public User create(String nickname, String email, String profileImageUrl) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("Nickname already exists: " + nickname);
        }

        User user = User.builder()
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(Long userId, String nickname, String profileImageUrl) {
        User user = findById(userId);
        
        if (nickname != null && !nickname.equals(user.getNickname())) {
            if (userRepository.existsByNickname(nickname)) {
                throw new IllegalArgumentException("Nickname already exists: " + nickname);
            }
        }

        user.updateProfile(nickname, profileImageUrl);
        return user;
    }
}