package gajeman.jagalchi.jagalchiserver.api.notification.dto;

import gajeman.jagalchi.jagalchiserver.domain.notification.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingResponse {
    private NotificationType type;
    private Boolean isEnabled;

    public static NotificationSettingResponse of(NotificationType type, Boolean isEnabled) {
        return NotificationSettingResponse.builder()
                .type(type)
                .isEnabled(isEnabled)
                .build();
    }
}
