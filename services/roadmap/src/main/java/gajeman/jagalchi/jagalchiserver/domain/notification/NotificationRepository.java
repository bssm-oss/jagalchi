package gajeman.jagalchi.jagalchiserver.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.referenceId = :referenceId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findByUserAndReferenceAndType(
            @Param("userId") Long userId,
            @Param("referenceId") Long referenceId,
            @Param("type") NotificationType type);
}
