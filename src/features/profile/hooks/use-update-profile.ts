import { useMutation, useQueryClient } from '@tanstack/react-query';

import { updateProfile } from '@/api/profile';
import type { UpdateProfileRequest } from '@/api/profile';
import { queryKeys } from '@/lib/query-keys';

export function useUpdateProfile(name: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UpdateProfileRequest) => updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.users.detail(name) });
    },
  });
}
