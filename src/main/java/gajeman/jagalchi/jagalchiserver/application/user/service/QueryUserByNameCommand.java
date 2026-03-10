package gajeman.jagalchi.jagalchiserver.application.user.service;

import gajeman.jagalchi.jagalchiserver.application.user.usecase.QueryUserByNameUseCase;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow.FollowRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq.ActivityService;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.QueryUserResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.StreakResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryUserByNameCommand implements QueryUserByNameUseCase {

    private final UsersRepository userRepository;
    private final FollowRepository followRepository;
    private final ActivityService activityService;
    private final ObjectMapper objectMapper;

    @Override
    public QueryUserResponse getUserByName(String name, Users currentUser) {
        Users targetUser = userRepository.findByName(name)
                .orElseThrow(UserNotFoundException::new);

        boolean isFollowed = checkFollowStatus(currentUser, targetUser);

        long followerCount = followRepository.countByFollowing(targetUser);

        long followingCount = followRepository.countByFollower(targetUser);

        Map<String, String> externalLinks = parseExternalLinks(targetUser.getExternalLinks());

        StreakResponseDto streak = activityService.getOneYearStreak(targetUser);

        return QueryUserResponse.from(
                targetUser,
                isFollowed,
                followerCount,
                followingCount,
                externalLinks,
                streak
        );
    }

    private boolean checkFollowStatus(Users currentUser, Users targetUser) {
        if (currentUser == null) {
            return false;
        }

        return followRepository.existsByFollowerAndFollowing(currentUser, targetUser);
    }

    private Map<String, String> parseExternalLinks(String externalLinksJson) {
        if (externalLinksJson == null || externalLinksJson.isEmpty()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(
                    externalLinksJson,
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}