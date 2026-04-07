package gajeman.jagalchi.jagalchiserver.api.notification.dto;

import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationSettingRequest {
    private NotificationType type;
    private Boolean isEnabled;
}
