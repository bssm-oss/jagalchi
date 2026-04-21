import { useQuery } from '@tanstack/react-query';

import { getNotifications } from '@/api/notifications';
import type { NotificationListParams, NotificationListResponse } from '@/api/notifications';
import { queryKeys } from '@/lib/query-keys';

export function useNotifications(params?: NotificationListParams) {
  return useQuery<NotificationListResponse>({
    queryKey: queryKeys.notifications.lists(params),
    queryFn: () => getNotifications(params),
  });
}
