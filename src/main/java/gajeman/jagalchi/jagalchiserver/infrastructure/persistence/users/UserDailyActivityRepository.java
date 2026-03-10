package gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users;

import gajeman.jagalchi.jagalchiserver.domain.user.UserDailyActivity;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDailyActivityRepository extends JpaRepository<UserDailyActivity, Long> {

    Optional<UserDailyActivity> findByUserAndActivityDate(Users user, LocalDate date);

    List<UserDailyActivity> findByUserAndActivityDateBetween(Users user, LocalDate start, LocalDate end);

}
