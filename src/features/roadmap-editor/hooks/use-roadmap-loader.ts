import { useEffect, useState } from 'react';

import { useRouter } from 'next/navigation';

import { useSetAtom } from 'jotai';
import { nanoid } from 'nanoid';

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
  retry: () => void;
}

interface ApiRoadmap {
  nodes: RoadmapNode[];
  edges: Edge[];
  title: string;
}

/**
 * Attempt to fetch roadmap data from the API.
 * Returns null when the API is unavailable — caller falls back to localStorage.
 * TODO: Implement actual GET /api/roadmap/{id}/events call when API is ready.
 */
async function loadFromApi(_roadmapId: string): Promise<ApiRoadmap | null> {
  // API not yet available — always return null so caller uses localStorage
  return null;
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
  const local = loadRoadmapFromLocalStorage(roadmapId);
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
          const newId = nanoid();
          const newRoadmap = createEmptyRoadmap(newId);
          saveRoadmapToLocalStorage(newRoadmap);

          // Redirect to new roadmap ID
          router.replace(`/editor/${newId}`);
          return;
        }

        // Load roadmap: API first → localStorage fallback
        const roadmap = await loadRoadmapData(roadmapId);

        if (!roadmap) {
          setError('로드맵을 찾을 수 없습니다.');
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
