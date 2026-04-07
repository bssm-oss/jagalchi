import { useCallback, useEffect } from 'react';

import { useSetAtom } from 'jotai';

import { useStomp } from '@/hooks/use-stomp';

import { handleAck, handleNack } from '../services/action-dispatcher';
import { nodesAtom, edgesAtom } from '../stores/editor-atoms';

import type { RoadmapNode } from '../types/editor.types';
import type { Edge } from '@xyflow/react';

const isRealtimeEnabled = process.env.NEXT_PUBLIC_REALTIME_ENABLED === 'true';

interface UseRealtimeSyncOptions {
  roadmapId: string;
  isEnabled?: boolean;
}

/**
 * 에디터 실시간 동기화 훅.
 * STOMP 이벤트를 구독하여 원격 변경사항을 로컬 atom에 반영.
 * NEXT_PUBLIC_REALTIME_ENABLED=true 일 때만 활성화.
 */
export function useRealtimeSync({ roadmapId, isEnabled = true }: UseRealtimeSyncOptions) {
  const setNodes = useSetAtom(nodesAtom);
  const setEdges = useSetAtom(edgesAtom);
  const { isConnected, subscribe } = useStomp({
    isAutoConnect: isRealtimeEnabled && isEnabled,
  });

  // ACK/NACK 구독
  const handleAckMessage = useCallback((message: { body: string }) => {
    const data = JSON.parse(message.body) as { type?: string; actionId: string; status?: string };

    if (data.type === 'ACK') {
      handleAck(data.actionId);
    } else {
      const { isFound } = handleNack(data.actionId);
      if (isFound) {
        // TODO: NACK 시 로컬 상태 롤백 또는 사용자에게 에러 알림
      }
    }
  }, []);

  // 로드맵 이벤트 구독 (다른 사용자의 변경사항)
  const handleStateEvent = useCallback(
    (message: { body: string }) => {
      const event = JSON.parse(message.body) as {
        type: string;
        payload: {
          type: string;
          target: { type: string; object: string };
          state?: Record<string, unknown>;
        };
      };

      // 이벤트 타입에 따라 로컬 상태 업데이트
      const { payload } = event;
      if (!payload?.target) return;

      const targetType = payload.target.type;
      const targetId = payload.target.object;

      if (targetType === 'NODE' || targetType === 'SECTION' || targetType === 'TEXT') {
        setNodes((prev) =>
          prev.map((node) => {
            if (node.id !== targetId) return node;
            const state = payload.state as Record<string, unknown> | undefined;
            return state ? ({ ...node, ...state } as RoadmapNode) : node;
          }),
        );
      } else if (targetType === 'EDGE') {
        setEdges((prev) =>
          prev.map((edge) => {
            if (edge.id !== targetId) return edge;
            const state = payload.state as Record<string, unknown> | undefined;
            return state ? ({ ...edge, ...state } as Edge) : edge;
          }),
        );
      }
    },
    [setNodes, setEdges],
  );

  // 구독 설정
  useEffect(() => {
    if (!isConnected || !isRealtimeEnabled || !isEnabled) return;

    const ackSub = subscribe('/user/queue/ack', handleAckMessage);
    const stateSub = subscribe(`/topic/roadmap/${roadmapId}/state`, handleStateEvent);

    return () => {
      ackSub?.unsubscribe();
      stateSub?.unsubscribe();
    };
  }, [isConnected, roadmapId, isEnabled, subscribe, handleAckMessage, handleStateEvent]);

  return { isConnected: isRealtimeEnabled && isConnected };
}
