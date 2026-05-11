package gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq;

import gajeman.jagalchi.jagalchiserver.domain.user.UserDailyActivity;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UserDailyActivityRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.DailyActivityDto;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.StreakResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final UserDailyActivityRepository activityRepository;
    private final UsersRepository userRepository;

    @Transactional
    public void recordActivity(Long userId, LocalDate date) {
        log.debug("Recording activity: userId={}, date={}", userId, date);

        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        UserDailyActivity activity =
                activityRepository
                        .findByUserAndActivityDate(user, date)
                        .orElseGet(() -> {
                            log.info("Creating new UserDailyActivity for userId={}, date={}", userId, date);
                            return activityRepository.save(
                                    UserDailyActivity.from(user, date)
                            );
                        });

        activity.increase();
        log.info("Activity recorded successfully: userId={}, date={}, count={}",
                userId, date, activity.getActivityCount());
    }

    @Transactional(readOnly = true)
    public StreakResponseDto getOneYearStreak(Users currentUser) {

        Users user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(364);

        List<UserDailyActivity> activities =
                activityRepository.findByUserAndActivityDateBetween(
                        user,
                        start,
                        today
                );

        Map<LocalDate, Integer> activityMap =
                activities.stream()
                        .collect(Collectors.toMap(
                                UserDailyActivity::getActivityDate,
                                UserDailyActivity::getActivityCount
                        ));

        List<DailyActivityDto> result = new ArrayList<>();

        for (int i = 0; i < 365; i++) {
            LocalDate date = start.plusDays(i);
            int count = activityMap.getOrDefault(date, 0);
            result.add(DailyActivityDto.from(date, count));
        }

        int currentStreak = calculateCurrentStreak(activityMap, today);

        return StreakResponseDto.from(currentStreak, result);
    }

    private int calculateCurrentStreak(
            Map<LocalDate, Integer> map,
            LocalDate today
    ) {
        int streak = 0;
        LocalDate date = today;

        while (map.getOrDefault(date, 0) > 0) {
            streak++;
            date = date.minusDays(1);
        }

        return streak;
    }
}
