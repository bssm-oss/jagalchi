import { renderHook, act } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';

import { createTestWrapper } from '@/test-utils';

import type { MessageHandler } from '@/lib/stomp-client';

// Set env BEFORE module import via vi.hoisted (runs before all vi.mock and imports)
vi.hoisted(() => {
  process.env.NEXT_PUBLIC_REALTIME_ENABLED = 'true';
});

// Mock useStomp
const subscribeCallbacks = new Map<string, MessageHandler>();
const mockSubscribe = vi.fn((destination: string, callback: MessageHandler) => {
  subscribeCallbacks.set(destination, callback);
  return { unsubscribe: vi.fn() };
});

const mockUseStomp = vi.fn((_options?: unknown) => ({
  isConnected: true,
  subscribe: mockSubscribe,
  publish: vi.fn(),
  connect: vi.fn(),
  disconnect: vi.fn(),
}));

vi.mock('@/hooks/use-stomp', () => ({
  useStomp: (options?: unknown) => mockUseStomp(options),
}));

// Mock action dispatcher
vi.mock('../services/action-dispatcher', () => ({
  handleAck: vi.fn(() => true),
  handleNack: vi.fn(() => ({ action: undefined, isFound: true })),
  sendCursorHide: vi.fn(),
}));

import { handleAck, handleNack, sendCursorHide } from '../services/action-dispatcher';

import { useRealtimeSync } from './use-realtime-sync';

function simulateMessage(destination: string, body: Record<string, unknown>) {
  const callback = subscribeCallbacks.get(destination);
  if (callback) {
    callback({ body: JSON.stringify(body) } as Parameters<MessageHandler>[0]);
  }
}

describe('useRealtimeSync', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    subscribeCallbacks.clear();
  });

  it('connects and subscribes to Swagger STOMP destinations', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '1' }), {
      wrapper: createTestWrapper(),
    });

    expect(mockUseStomp).toHaveBeenCalledWith({
      isAutoConnect: true,
      roadmapId: '1',
      onBeforeDisconnect: expect.any(Function),
    });
    expect(mockSubscribe).toHaveBeenCalledTimes(5);
    expect(mockSubscribe).toHaveBeenCalledWith('/user/queue/ack', expect.any(Function));
    expect(mockSubscribe).toHaveBeenCalledWith('/user/queue/nack', expect.any(Function));
    expect(mockSubscribe).toHaveBeenCalledWith('/topic/roadmap/1/state', expect.any(Function));
    expect(mockSubscribe).toHaveBeenCalledWith('/topic/roadmap/1/cursors', expect.any(Function));
    expect(mockSubscribe).toHaveBeenCalledWith(
      '/topic/roadmap/1/cursors/hide',
      expect.any(Function),
    );
  });

  it('does not pass client-controlled user identity to useStomp', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '7' }), {
      wrapper: createTestWrapper(),
    });

    expect(mockUseStomp).toHaveBeenCalledWith({
      isAutoConnect: true,
      roadmapId: '7',
      onBeforeDisconnect: expect.any(Function),
    });
  });

  it('hides cursor before disconnecting STOMP', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '9' }), {
      wrapper: createTestWrapper(),
    });

    const options = mockUseStomp.mock.calls.at(-1)?.[0] as
      | { onBeforeDisconnect?: () => void }
      | undefined;

    options?.onBeforeDisconnect?.();

    expect(sendCursorHide).toHaveBeenCalledWith('9');
  });

  it('handles ACK message', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '1' }), {
      wrapper: createTestWrapper(),
    });

    act(() => {
      simulateMessage('/user/queue/ack', {
        type: 'ACK',
        actionId: 'act-1',
        status: 'ACCEPTED',
      });
    });

    expect(handleAck).toHaveBeenCalledWith('act-1');
  });

  it('handles NACK message', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '1' }), {
      wrapper: createTestWrapper(),
    });

    act(() => {
      simulateMessage('/user/queue/nack', {
        actionId: 'act-2',
        actionType: 'CREATE',
        errorCode: 'CONFLICT',
        errorMessage: '충돌',
      });
    });

    expect(handleNack).toHaveBeenCalledWith('act-2');
  });

  it('skips invalid JSON gracefully', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '1' }), {
      wrapper: createTestWrapper(),
    });

    const callback = subscribeCallbacks.get('/user/queue/ack');
    expect(() => {
      callback?.({ body: 'invalid json{{{' } as Parameters<MessageHandler>[0]);
    }).not.toThrow();
  });

  it('does not subscribe when disabled', () => {
    renderHook(() => useRealtimeSync({ roadmapId: '1', isEnabled: false }), {
      wrapper: createTestWrapper(),
    });

    expect(mockSubscribe).not.toHaveBeenCalled();
  });

  it('returns isConnected status', () => {
    const { result } = renderHook(() => useRealtimeSync({ roadmapId: '1' }), {
      wrapper: createTestWrapper(),
    });

    expect(result.current.isConnected).toBe(true);
  });
});
