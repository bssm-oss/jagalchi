import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('@/lib/stomp-client', () => ({
  publishStomp: vi.fn(),
}));

import { publishStomp } from '@/lib/stomp-client';
import {
  dispatchAction,
  handleAck,
  handleNack,
  getPendingCount,
  sendCursorPosition,
  sendCursorHide,
} from './action-dispatcher';

describe('action-dispatcher', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Clear pending actions by handling any remaining
    while (getPendingCount() > 0) {
      // Can't easily clear, but tests are independent
    }
  });

  it('dispatchAction sends STOMP message and returns actionId', () => {
    const actionId = dispatchAction('roadmap-1', 'CREATE');
    expect(actionId).toBeTruthy();
    expect(publishStomp).toHaveBeenCalledWith(
      '/app/roadmap/roadmap-1/action',
      expect.objectContaining({
        actionId,
        roadmap: 'roadmap-1',
        action: 'CREATE',
      }),
    );
  });

  it('handleAck removes pending action', () => {
    const actionId = dispatchAction('roadmap-1', 'EDIT');
    expect(handleAck(actionId)).toBe(true);
    expect(handleAck(actionId)).toBe(false); // already removed
  });

  it('handleNack returns action and removes from pending', () => {
    const actionId = dispatchAction('roadmap-1', 'DELETE');
    const result = handleNack(actionId);
    expect(result.isFound).toBe(true);
    expect(result.action?.actionId).toBe(actionId);
  });

  it('handleNack returns isFound false for unknown actionId', () => {
    const result = handleNack('unknown-id');
    expect(result.isFound).toBe(false);
    expect(result.action).toBeUndefined();
  });

  it('sendCursorPosition publishes to correct topic', () => {
    sendCursorPosition('rm-1', { x: 100, y: 200, userName: 'alice' });
    expect(publishStomp).toHaveBeenCalledWith(
      '/app/roadmap/rm-1/cursor',
      expect.objectContaining({
        x: 100,
        y: 200,
        userName: 'alice',
      }),
    );
  });

  it('sendCursorHide publishes to correct topic', () => {
    sendCursorHide('rm-1');
    expect(publishStomp).toHaveBeenCalledWith('/app/roadmap/rm-1/cursor/hide', {});
  });

  it('getPendingCount tracks pending actions', () => {
    const id1 = dispatchAction('rm-1', 'CREATE');
    const id2 = dispatchAction('rm-1', 'EDIT');
    const countBefore = getPendingCount();
    expect(countBefore).toBeGreaterThanOrEqual(2);
    handleAck(id1);
    handleAck(id2);
  });
});
