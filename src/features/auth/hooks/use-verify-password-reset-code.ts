import { useMutation } from '@tanstack/react-query';

import { verifyPasswordResetCode } from '@/api/auth';
import type { MessageResponse, VerifyCodeRequest } from '@/api/auth';

export function useVerifyPasswordResetCode() {
  return useMutation<MessageResponse, Error, VerifyCodeRequest>({
    mutationFn: verifyPasswordResetCode,
  });
}
