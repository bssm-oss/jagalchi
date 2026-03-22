import { act, renderHook } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { useVerificationCode } from './use-verification-code';

describe('useVerificationCode', () => {
  it('초기 상태에서 isCodeSent가 false이다', () => {
    const { result } = renderHook(() => useVerificationCode());
    expect(result.current.isCodeSent).toBe(false);
  });

  it('handleSendCode 호출 후 isCodeSent가 true가 된다', () => {
    const { result } = renderHook(() => useVerificationCode());

    act(() => {
      result.current.handleSendCode();
    });

    expect(result.current.isCodeSent).toBe(true);
  });

  it('handleSendCode를 여러 번 호출해도 isCodeSent가 true를 유지한다', () => {
    const { result } = renderHook(() => useVerificationCode());

    act(() => {
      result.current.handleSendCode();
    });
    act(() => {
      result.current.handleSendCode();
    });

    expect(result.current.isCodeSent).toBe(true);
  });
});
