import { useMutation } from '@tanstack/react-query';

import { verifyCode } from '@/api/auth';
import type { VerifyCodeRequest, VerifyCodeResponse } from '@/api/auth';

export function useVerifyCode() {
  return useMutation<VerifyCodeResponse, Error, VerifyCodeRequest>({
    mutationFn: verifyCode,
  });
}
