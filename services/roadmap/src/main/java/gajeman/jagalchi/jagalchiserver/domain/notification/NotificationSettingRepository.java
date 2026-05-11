package gajeman.jagalchi.jagalchiserver.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    List<NotificationSetting> findByUserId(Long userId);

    Optional<NotificationSetting> findByUserIdAndNotificationType(Long userId, NotificationType notificationType);

    @Query("SELECT ns FROM NotificationSetting ns WHERE ns.user.id = :userId AND ns.notificationType = :type AND ns.isEnabled = true")
    Optional<NotificationSetting> findEnabledSetting(
            @Param("userId") Long userId,
            @Param("type") NotificationType type);

    @Query("SELECT COUNT(ns) > 0 FROM NotificationSetting ns WHERE ns.user.id = :userId AND ns.notificationType = :type AND ns.isEnabled = true")
    boolean isNotificationEnabled(
            @Param("userId") Long userId,
            @Param("type") NotificationType type);
}
