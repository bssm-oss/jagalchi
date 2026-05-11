import { useEffect, useState } from 'react';

import { useRouter } from 'next/navigation';

import { useSetAtom } from 'jotai';

import type { RoadmapEvent } from '@/api/roadmap';
import { isEnabled } from '@/lib/feature-flags';

import {
  createEmptyRoadmap,
  loadRoadmapFromLocalStorage,
  saveRoadmapToLocalStorage,
} from '../services/roadmap-storage';
import { nodesAtom, edgesAtom, roadmapTitleAtom } from '../stores/editor-atoms';
import { hashNodes, hashEdges } from '../utils/fast-hash';

import type { RoadmapNode } from '../types/editor.types';
import type { Edge } from '@xyflow/react';

interface UseRoadmapLoaderProps {
  roadmapId: string;
}

interface UseRoadmapLoaderReturn {
  isLoading: boolean;
  error: string | null;
  initialNodes: string;
  initialEdges: string;
  initialTitle: string;
  retry: () => Promise<void>;
}

interface ApiRoadmap {
  nodes: RoadmapNode[];
  edges: Edge[];
  title: string;
}

const isRealtimeEnabled = isEnabled('REALTIME_ENABLED');

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function isRoadmapNode(value: unknown): value is RoadmapNode {
  if (!isRecord(value)) return false;
  return typeof value.id === 'string' && isRecord(value.position) && isRecord(value.data);
}

function isEdge(value: unknown): value is Edge {
  if (!isRecord(value)) return false;
  return (
    typeof value.id === 'string' &&
    typeof value.source === 'string' &&
    typeof value.target === 'string'
  );
}

function replaceById<T extends { id: string }>(items: T[], nextItem: T): T[] {
  const index = items.findIndex((item) => item.id === nextItem.id);
  if (index === -1) return [...items, nextItem];
  return items.map((item) => (item.id === nextItem.id ? nextItem : item));
}

function readNodes(value: unknown): RoadmapNode[] {
  return Array.isArray(value) ? value.filter(isRoadmapNode) : [];
}

function readEdges(value: unknown): Edge[] {
  return Array.isArray(value) ? value.filter(isEdge) : [];
}

function applyStateEvent(eventPayload: Record<string, unknown>, current: ApiRoadmap): ApiRoadmap {
  const target = isRecord(eventPayload.target) ? eventPayload.target : null;
  if (!target || typeof target.object !== 'string') {
    return current;
  }

  const targetId = target.object;
  const targetType = typeof target.type === 'string' ? target.type : '';
  const nextState = eventPayload.state;
  const stateRecord = isRecord(nextState) ? nextState : null;
  const data = isRecord(eventPayload.data) ? eventPayload.data : null;
  const isDeleted = Boolean(eventPayload.deletedNode);

  if (typeof data?.title === 'string') {
    return { ...current, title: data.title };
  }

  if (targetType === 'EDGE') {
    if (isDeleted) {
      return { ...current, edges: current.edges.filter((edge) => edge.id !== targetId) };
    }

    const existingEdge = current.edges.find((edge) => edge.id === targetId);
    if (existingEdge && stateRecord) {
      return {
        ...current,
        edges: current.edges.map((edge) =>
          edge.id === targetId ? ({ ...edge, ...stateRecord } as Edge) : edge,
        ),
      };
    }

    if (isEdge(nextState)) {
      return { ...current, edges: replaceById(current.edges, nextState) };
    }

    return current;
  }

  const isNodeTarget = ['GROUP', 'NODE', 'SECTION', 'TEXT'].includes(targetType);
  if (!isNodeTarget) return current;

  if (isDeleted) {
    return { ...current, nodes: current.nodes.filter((node) => node.id !== targetId) };
  }

  const existingNode = current.nodes.find((node) => node.id === targetId);
  if (existingNode && stateRecord) {
    return {
      ...current,
      nodes: current.nodes.map((node) =>
        node.id === targetId ? ({ ...node, ...stateRecord } as RoadmapNode) : node,
      ),
    };
  }

  if (isRoadmapNode(nextState)) {
    return { ...current, nodes: replaceById(current.nodes, nextState) };
  }

  return current;
}

function replayRoadmapEvents(events: RoadmapEvent[], initialTitle: string): ApiRoadmap {
  return events.reduce<ApiRoadmap>(
    (current, event) => {
      const payload = event.payload;
      if (!isRecord(payload)) return current;

      if (
        event.type === 'SNAPSHOT' ||
        Array.isArray(payload.nodes) ||
        Array.isArray(payload.edges)
      ) {
        const title = typeof payload.title === 'string' ? payload.title : current.title;
        return {
          title,
          nodes: readNodes(payload.nodes),
          edges: readEdges(payload.edges),
        };
      }

      if (isRoadmapNode(payload)) {
        return { ...current, nodes: replaceById(current.nodes, payload) };
      }

      if (isEdge(payload)) {
        return { ...current, edges: replaceById(current.edges, payload) };
      }

      if (isRecord(payload.target)) {
        return applyStateEvent(payload, current);
      }

      return current;
    },
    { title: initialTitle, nodes: [], edges: [] },
  );
}

/**
 * Attempt to fetch roadmap data from the API.
 * Returns null when the API is unavailable — caller falls back to localStorage.
 */
async function loadFromApi(roadmapId: string): Promise<ApiRoadmap | null> {
  if (!isRealtimeEnabled) return null;

  try {
    const { getRoadmap, getRoadmapEvents } = await import('@/api/roadmap');
    const [detail, events] = await Promise.all([
      getRoadmap(Number(roadmapId)),
      getRoadmapEvents(roadmapId, 0),
    ]);
    if (!events || !Array.isArray(events)) return null;

    const roadmap = replayRoadmapEvents(events, detail.title);
    saveRoadmapToLocalStorage({
      id: Number(roadmapId),
      title: roadmap.title,
      description: detail.description ?? undefined,
      nodes: roadmap.nodes,
      edges: roadmap.edges,
      isPublic: detail.isPublic,
      createdAt: detail.createdAt,
      updatedAt: detail.updatedAt,
    });
    return roadmap;
  } catch {
    return null;
  }
}

/**
 * Load roadmap from API first, then fall back to localStorage.
 * Structured so replacing `loadFromApi` body is the only change needed
 * once the real API is ready.
 */
async function loadRoadmapData(roadmapId: string): Promise<ApiRoadmap | null> {
  const apiResult = await loadFromApi(roadmapId);
  if (apiResult !== null) {
    return apiResult;
  }

  // Fallback to localStorage
  const local = loadRoadmapFromLocalStorage(Number(roadmapId));
  if (!local) return null;

  return {
    nodes: local.nodes,
    edges: local.edges,
    title: local.title,
  };
}

export function useRoadmapLoader({ roadmapId }: UseRoadmapLoaderProps): UseRoadmapLoaderReturn {
  const router = useRouter();
  const setNodes = useSetAtom(nodesAtom);
  const setEdges = useSetAtom(edgesAtom);
  const setTitle = useSetAtom(roadmapTitleAtom);

  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [initialNodes, setInitialNodes] = useState<string>('');
  const [initialEdges, setInitialEdges] = useState<string>('');
  const [initialTitle, setInitialTitle] = useState<string>('');

  useEffect(() => {
    const loadRoadmap = async () => {
      try {
        setIsLoading(true);
        setError(null);

        // Check if roadmapId is 'new' - create new roadmap
        if (roadmapId === 'new') {
          const newId = Date.now();
          const newRoadmap = createEmptyRoadmap(newId);
          saveRoadmapToLocalStorage(newRoadmap);

          // Redirect to new roadmap ID
          router.replace(`/editor/${newId}`);
          setIsLoading(false);
          return;
        }

        // Load roadmap: API first → localStorage fallback
        const roadmap = await loadRoadmapData(roadmapId);

        if (!roadmap) {
          setError('로드맵을 찾을 수 없습니다.');
          setIsLoading(false);
          return;
        }

        // Initialize editor state
        setNodes(roadmap.nodes);
        setEdges(roadmap.edges);
        setTitle(roadmap.title);

        // Store initial state for change detection (using fast hash)
        setInitialNodes(hashNodes(roadmap.nodes));
        setInitialEdges(hashEdges(roadmap.edges));
        setInitialTitle(roadmap.title);

        setIsLoading(false);
      } catch (err) {
        setError(err instanceof Error ? err.message : '로드맵을 불러오는 중 오류가 발생했습니다.');
        setIsLoading(false);
      }
    };

    loadRoadmap();
  }, [roadmapId, router, setNodes, setEdges, setTitle]);

  const retry = async () => {
    setError(null);
    setIsLoading(true);

    try {
      const roadmap = await loadRoadmapData(roadmapId);
      if (roadmap) {
        setNodes(roadmap.nodes);
        setEdges(roadmap.edges);
        setTitle(roadmap.title);
        setInitialNodes(hashNodes(roadmap.nodes));
        setInitialEdges(hashEdges(roadmap.edges));
        setInitialTitle(roadmap.title);
        setIsLoading(false);
      } else {
        setError('로드맵을 찾을 수 없습니다.');
        setIsLoading(false);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '로드맵을 불러오는 중 오류가 발생했습니다.');
      setIsLoading(false);
    }
  };

  return {
    isLoading,
    error,
    initialNodes,
    initialEdges,
    initialTitle,
    retry,
  };
}
