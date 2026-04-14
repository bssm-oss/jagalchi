import { useMutation } from '@tanstack/react-query';

import { sendVerificationCode } from '@/api/auth';
import type { MessageResponse, SendVerificationCodeRequest } from '@/api/auth';

export function useSendVerificationCode() {
  return useMutation<MessageResponse, Error, SendVerificationCodeRequest>({
    mutationFn: sendVerificationCode,
  });
}
