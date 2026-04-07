import { useMutation } from '@tanstack/react-query';

import { sendPasswordResetCode } from '@/api/auth';
import type { MessageResponse, SendVerificationCodeRequest } from '@/api/auth';

export function useSendPasswordResetCode() {
  return useMutation<MessageResponse, Error, SendVerificationCodeRequest>({
    mutationFn: sendPasswordResetCode,
  });
}
