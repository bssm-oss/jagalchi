import { useCallback, useEffect } from 'react';

import { useSetAtom } from 'jotai';

import { useStomp } from '@/hooks/use-stomp';

import { handleAck, handleNack } from '../services/action-dispatcher';
import { nodesAtom, edgesAtom, remoteCursorsAtom } from '../stores/editor-atoms';

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
  const setRemoteCursors = useSetAtom(remoteCursorsAtom);
  const { isConnected, subscribe } = useStomp({
    isAutoConnect: isRealtimeEnabled && isEnabled,
  });

  // ACK 구독 — 액션 수락 응답 처리
  const handleAckMessage = useCallback((message: { body: string }) => {
    try {
      const data = JSON.parse(message.body) as { type?: string; actionId: string; status?: string };
      handleAck(data.actionId);
    } catch {
      /* invalid JSON — skip */
    }
  }, []);

  // NACK 구독 — 액션 거부 응답 처리
  const handleNackMessage = useCallback((message: { body: string }) => {
    try {
      const data = JSON.parse(message.body) as {
        actionId: string;
        actionType: string;
        errorCode: string;
        errorMessage: string;
      };
      const { isFound } = handleNack(data.actionId);
      if (isFound) {
        // TODO: NACK 시 로컬 상태 롤백 또는 사용자에게 에러 알림
      }
    } catch {
      /* invalid JSON — skip */
    }
  }, []);

  // 스냅샷 구독 — 초기 전체 상태 수신 (/topic/roadmap/{id}/state 구독 시 자동 전송)
  const handleSnapshotMessage = useCallback(
    (message: { body: string }) => {
      try {
        const snapshot = JSON.parse(message.body) as {
          type: string;
          nodes: RoadmapNode[];
          edges: Edge[];
        };
        if (snapshot.type !== 'SNAPSHOT') return;
        setNodes(snapshot.nodes);
        setEdges(snapshot.edges);
      } catch {
        /* invalid JSON — skip */
      }
    },
    [setNodes, setEdges],
  );

  // 커서 위치 구독 — 다른 유저의 커서 위치 수신
  const handleCursorsMessage = useCallback(
    (message: { body: string }) => {
      try {
        const data = JSON.parse(message.body) as {
          userId: number;
          userName: string;
          x: number;
          y: number;
          state: string;
        };
        setRemoteCursors((prev) => {
          const next = new Map(prev);
          next.set(String(data.userId), {
            userName: data.userName,
            x: data.x,
            y: data.y,
            state: data.state,
          });
          return next;
        });
      } catch {
        /* invalid JSON — skip */
      }
    },
    [setRemoteCursors],
  );

  const handleCursorsHideMessage = useCallback(
    (message: { body: string }) => {
      try {
        const data = JSON.parse(message.body) as { userId: number };
        setRemoteCursors((prev) => {
          const next = new Map(prev);
          next.delete(String(data.userId));
          return next;
        });
      } catch {
        /* invalid JSON — skip */
      }
    },
    [setRemoteCursors],
  );

  const handleStateEvent = useCallback(
    (message: { body: string }) => {
      let event: {
        type: string;
        payload: {
          type: string;
          target: { type: string; object: string };
          state?: Record<string, unknown>;
        };
      };
      try {
        event = JSON.parse(message.body);
      } catch {
        return;
      }

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
    const nackSub = subscribe('/user/queue/nack', handleNackMessage);
    const snapshotSub = subscribe('/user/queue/snapshot', handleSnapshotMessage);
    const stateSub = subscribe(`/topic/roadmap/${roadmapId}/state`, handleStateEvent);
    const cursorsSub = subscribe(`/topic/roadmap/${roadmapId}/cursors`, handleCursorsMessage);
    const cursorsHideSub = subscribe(
      `/topic/roadmap/${roadmapId}/cursors/hide`,
      handleCursorsHideMessage,
    );

    return () => {
      ackSub?.unsubscribe();
      nackSub?.unsubscribe();
      snapshotSub?.unsubscribe();
      stateSub?.unsubscribe();
      cursorsSub?.unsubscribe();
      cursorsHideSub?.unsubscribe();
    };
  }, [
    isConnected,
    roadmapId,
    isEnabled,
    subscribe,
    handleAckMessage,
    handleNackMessage,
    handleSnapshotMessage,
    handleStateEvent,
    handleCursorsMessage,
    handleCursorsHideMessage,
  ]);

  return { isConnected: isRealtimeEnabled && isConnected };
}
