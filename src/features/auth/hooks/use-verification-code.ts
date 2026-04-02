'use client';

import { useState } from 'react';

import { useSendVerificationCode } from './use-send-verification-code';
import { useVerifyCode } from './use-verify-code';

/**
 * Manages the state and logic for sending and verifying verification codes during authentication.
 * @returns Object containing the code sent state, handlers, and mutation states
 */
export function useVerificationCode() {
  const [isCodeSent, setIsCodeSent] = useState(false);
  const sendCodeMutation = useSendVerificationCode();
  const verifyCodeMutation = useVerifyCode();

  const handleSendCode = (email: string) => {
    sendCodeMutation.mutate(
      { email },
      {
        onSuccess: () => {
          setIsCodeSent(true);
        },
      },
    );
  };

  const handleVerifyCode = (email: string, code: string) => {
    return verifyCodeMutation.mutateAsync({ email, code });
  };

  return {
    isCodeSent,
    handleSendCode,
    handleVerifyCode,
    isSendingCode: sendCodeMutation.isPending,
    isVerifyingCode: verifyCodeMutation.isPending,
    sendCodeError: sendCodeMutation.error,
    verifyCodeError: verifyCodeMutation.error,
  };
}
