package gajeman.jagalchi.jagalchiserver.presentation.user.response;

import java.util.List;

public record StreakResponseDto(
        int currentStreak,
        List<DailyActivityDto> activities
) {
    public static StreakResponseDto from(int currentStreak, List<DailyActivityDto> activities) {
        return new StreakResponseDto(currentStreak, activities);
    }
}
