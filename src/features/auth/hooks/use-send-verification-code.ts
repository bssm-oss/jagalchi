import { useMutation } from '@tanstack/react-query';

import { sendVerificationCode } from '@/api/auth';
import type { SendVerificationCodeRequest, SendVerificationCodeResponse } from '@/api/auth';

export function useSendVerificationCode() {
  return useMutation<SendVerificationCodeResponse, Error, SendVerificationCodeRequest>({
    mutationFn: sendVerificationCode,
  });
}
