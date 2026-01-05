package gajeman.jagalchi.jagalchiserver.application.follow.service;

import gajeman.jagalchi.jagalchiserver.application.follow.usecase.ToggleFollowUseCase;
import gajeman.jagalchi.jagalchiserver.domain.follow.Follow;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow.FollowRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ToggleFollowCommand implements ToggleFollowUseCase {

    private final UsersRepository usersRepository;
    private final FollowRepository followRepository;

    @Override
    @Transactional
    public void toggleFollowing(Long userId, String targetName, boolean toggle) {
        Users me = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Users target = usersRepository.findByName(targetName)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        if (me.getId().equals(target.getId())) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        boolean exists = followRepository.existsByFollowerAndFollowing(me, target);

        if (toggle && !exists) {
            followRepository.save(Follow.of(me, target));
        }

        if (!toggle && exists) {
            followRepository.findByFollowerAndFollowing(me, target)
                    .ifPresent(followRepository::delete);
        }
    }

}
