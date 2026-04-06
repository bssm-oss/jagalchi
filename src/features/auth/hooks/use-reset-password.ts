import { useMutation } from '@tanstack/react-query';

import { resetPassword } from '@/api/auth';
import type { ChangePasswordRequest, MessageResponse } from '@/api/auth';

export function useResetPassword() {
  return useMutation<MessageResponse, Error, ChangePasswordRequest>({
    mutationFn: resetPassword,
  });
}
