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

        // Load existing roadmap
        const roadmap = loadRoadmapFromLocalStorage(roadmapId);

        if (!roadmap) {
          setError('로드맵을 찾을 수 없습니다.');
          return;
        }

        // Initialize editor state
        setNodes(roadmap.nodes);
        setEdges(roadmap.edges);
        setTitle(roadmap.title);

        // Store initial state for change detection
        setInitialNodes(JSON.stringify(roadmap.nodes));
        setInitialEdges(JSON.stringify(roadmap.edges));
        setInitialTitle(roadmap.title);

        setIsLoading(false);
      } catch (err) {
        setError(err instanceof Error ? err.message : '로드맵을 불러오는 중 오류가 발생했습니다.');
        setIsLoading(false);
      }
    };

    loadRoadmap();
  }, [roadmapId, router, setNodes, setEdges, setTitle]);

  const retry = () => {
    setError(null);
    setIsLoading(true);

    // Re-trigger load
    const roadmap = loadRoadmapFromLocalStorage(roadmapId);
    if (roadmap) {
      setNodes(roadmap.nodes);
      setEdges(roadmap.edges);
      setTitle(roadmap.title);
      setInitialNodes(JSON.stringify(roadmap.nodes));
      setInitialEdges(JSON.stringify(roadmap.edges));
      setInitialTitle(roadmap.title);
      setIsLoading(false);
    } else {
      setError('로드맵을 찾을 수 없습니다.');
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
