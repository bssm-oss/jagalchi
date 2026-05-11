package gajeman.jagalchi.jagalchiserver.api.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationListResponse {
    private List<NotificationResponse> notifications;
    private long totalCount;
    private long unreadCount;
    private int page;
    private int size;

    public static NotificationListResponse of(
            List<NotificationResponse> notifications,
            long totalCount,
            long unreadCount,
            int page,
            int size) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .totalCount(totalCount)
                .unreadCount(unreadCount)
                .page(page)
                .size(size)
                .build();
    }
}
