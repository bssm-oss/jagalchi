import { useQuery } from '@tanstack/react-query';

import { getAccessToken } from '@/api/client';
import { getNotifications } from '@/api/notifications';
import type { NotificationListParams, NotificationListResponse } from '@/api/notifications';
import { queryKeys } from '@/lib/query-keys';

export function useNotifications(params?: NotificationListParams) {
  const isAuthenticated = !!getAccessToken();

  return useQuery<NotificationListResponse>({
    queryKey: queryKeys.notifications.lists(params),
    queryFn: () => getNotifications(params),
    enabled: isAuthenticated,
    refetchInterval: isAuthenticated ? 30_000 : false,
    refetchOnWindowFocus: isAuthenticated,
  });
}
