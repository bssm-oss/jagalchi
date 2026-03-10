package gajeman.jagalchi.jagalchiserver.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "tbl_user_daily_activity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_date",
                        columnNames = {"user_id", "activity_date"}
                )
        }
)
@Getter
@NoArgsConstructor
public class UserDailyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(nullable = false)
    private int activityCount = 0;

    @Builder
    private UserDailyActivity(Users user, LocalDate activityDate) {
        this.user = user;
        this.activityDate = activityDate;
        this.activityCount = 0;
    }

    public static UserDailyActivity from(Users user, LocalDate activityDate) {
        return UserDailyActivity.builder()
                .user(user)
                .activityDate(activityDate)
                .build();
    }

    public void increase() {
        this.activityCount++;
    }

}
