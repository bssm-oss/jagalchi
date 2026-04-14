import { useMutation } from '@tanstack/react-query';

import { verifyCode } from '@/api/auth';
import type { MessageResponse, VerifyCodeRequest } from '@/api/auth';

export function useVerifyCode() {
  return useMutation<MessageResponse, Error, VerifyCodeRequest>({
    mutationFn: verifyCode,
  });
}
