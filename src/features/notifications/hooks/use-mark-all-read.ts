import { useMutation, useQueryClient } from '@tanstack/react-query';

import { markAllNotificationsRead } from '@/api/notifications';
import { queryKeys } from '@/lib/query-keys';

export function useMarkAllRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => markAllNotificationsRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.notifications.all });
    },
  });
}
