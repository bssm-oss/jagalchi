import { useMutation, useQueryClient } from '@tanstack/react-query';

import { markNotificationRead } from '@/api/notifications';
import { queryKeys } from '@/lib/query-keys';

export function useMarkRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (notificationId: number) => markNotificationRead(notificationId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.notifications.all });
    },
  });
}
