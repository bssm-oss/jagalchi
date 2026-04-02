import { useMutation } from '@tanstack/react-query';

import { resetPassword } from '@/api/auth';
import type { ResetPasswordRequest, ResetPasswordResponse } from '@/api/auth';

export function useResetPassword() {
  return useMutation<ResetPasswordResponse, Error, ResetPasswordRequest>({
    mutationFn: resetPassword,
  });
}
