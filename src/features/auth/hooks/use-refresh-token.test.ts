import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

import { createTestWrapper } from '@/test-utils';

vi.mock('@/api/auth', () => ({
  refreshToken: vi.fn(),
}));

vi.mock('@/api/client', () => ({
  clearAccessToken: vi.fn(),
}));

const mockSetLogin = vi.fn();
const mockSetLogout = vi.fn();
const mockSetInitialized = vi.fn();

vi.mock('../stores/auth.atoms', () => ({
  loginAtom: { write: () => {} },
  logoutAtom: { write: () => {} },
  isAuthInitializedAtom: { write: () => {} },
}));

vi.mock('jotai', async (importOriginal) => {
  const actual = await importOriginal<typeof import('jotai')>();
  return {
    ...actual,
    useSetAtom: (atom: unknown) => {
      const { loginAtom, logoutAtom, isAuthInitializedAtom } =
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        require('../stores/auth.atoms');
      if (atom === loginAtom) return mockSetLogin;
      if (atom === logoutAtom) return mockSetLogout;
      if (atom === isAuthInitializedAtom) return mockSetInitialized;
      return vi.fn();
    },
  };
});

import { refreshToken } from '@/api/auth';
import { clearAccessToken } from '@/api/client';

import { useRefreshToken } from './use-refresh-token';

describe('useRefreshToken', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('calls refreshToken on mount and sets initialized', async () => {
    vi.mocked(refreshToken).mockResolvedValue({ accessToken: 'new-token' });

    renderHook(() => useRefreshToken(), { wrapper: createTestWrapper() });

    // flush async
    await act(async () => {
      await vi.runAllTimersAsync();
    });

    expect(refreshToken).toHaveBeenCalledTimes(1);
    expect(mockSetLogin).toHaveBeenCalledWith('new-token');
    expect(mockSetInitialized).toHaveBeenCalledWith(true);
  });

  it('clears token and logs out on refresh failure', async () => {
    vi.mocked(refreshToken).mockRejectedValue(new Error('expired'));

    renderHook(() => useRefreshToken(), { wrapper: createTestWrapper() });

    await act(async () => {
      await vi.runAllTimersAsync();
    });

    expect(clearAccessToken).toHaveBeenCalled();
    expect(mockSetLogout).toHaveBeenCalled();
    expect(mockSetInitialized).toHaveBeenCalledWith(true);
  });

  it('starts interval on successful refresh', async () => {
    vi.mocked(refreshToken).mockResolvedValue({ accessToken: 'token' });

    renderHook(() => useRefreshToken(), { wrapper: createTestWrapper() });

    await act(async () => {
      await vi.runAllTimersAsync();
    });

    vi.mocked(refreshToken).mockClear();

    // Advance by 14 minutes
    await act(async () => {
      await vi.advanceTimersByTimeAsync(14 * 60 * 1000);
    });

    expect(refreshToken).toHaveBeenCalledTimes(1);
  });

  it('does not start interval on failed refresh', async () => {
    vi.mocked(refreshToken).mockRejectedValue(new Error('fail'));

    renderHook(() => useRefreshToken(), { wrapper: createTestWrapper() });

    await act(async () => {
      await vi.runAllTimersAsync();
    });

    vi.mocked(refreshToken).mockClear();

    await act(async () => {
      await vi.advanceTimersByTimeAsync(14 * 60 * 1000);
    });

    expect(refreshToken).not.toHaveBeenCalled();
  });

  it('cleans up interval on unmount', async () => {
    vi.mocked(refreshToken).mockResolvedValue({ accessToken: 'token' });

    const { unmount } = renderHook(() => useRefreshToken(), {
      wrapper: createTestWrapper(),
    });

    await act(async () => {
      await vi.runAllTimersAsync();
    });

    unmount();
    vi.mocked(refreshToken).mockClear();

    await act(async () => {
      await vi.advanceTimersByTimeAsync(14 * 60 * 1000);
    });

    expect(refreshToken).not.toHaveBeenCalled();
  });
});
