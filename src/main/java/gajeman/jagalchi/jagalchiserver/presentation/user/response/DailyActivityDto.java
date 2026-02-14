package gajeman.jagalchi.jagalchiserver.presentation.user.response;

import java.time.LocalDate;

public record DailyActivityDto(
        LocalDate date,
        int count
) {
    public static DailyActivityDto from(LocalDate date, int count) {
        return new DailyActivityDto(date, count);
    }
}
