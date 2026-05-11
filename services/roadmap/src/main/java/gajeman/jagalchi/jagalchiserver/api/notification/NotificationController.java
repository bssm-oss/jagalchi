package gajeman.jagalchi.jagalchiserver.api.notification;

import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationListResponse;
import gajeman.jagalchi.jagalchiserver.api.notification.dto.NotificationSettingResponse;
import gajeman.jagalchi.jagalchiserver.common.auth.AuthPrincipal;
import gajeman.jagalchi.jagalchiserver.application.notification.NotificationService;
import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unreadOnly,
            @RequestParam(required = false) String type) {
        NotificationListResponse response = notificationService.getNotifications(
                principal.userId(), page, size, unreadOnly, type);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        notificationService.markAsRead(notificationId, principal.userId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Long> markAllAsRead(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        int count = notificationService.markAllAsRead(principal.userId());
        return ResponseEntity.ok().body((long) count);
    }

    @GetMapping("/settings")
    public ResponseEntity<List<NotificationSettingResponse>> getNotificationSettings(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        List<NotificationSettingResponse> settings = notificationService.getNotificationSettings(principal.userId());
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/settings")
    public ResponseEntity<Void> updateNotificationSetting(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @RequestParam NotificationType type,
            @RequestParam Boolean enabled) {
        notificationService.updateNotificationSetting(principal.userId(), type, enabled);
        return ResponseEntity.ok().build();
    }
}
