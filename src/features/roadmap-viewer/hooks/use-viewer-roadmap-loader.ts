'use client';

import { useEffect } from 'react';

import { useSetAtom } from 'jotai';

import { loadRoadmapFromLocalStorage } from '@/lib/roadmap-storage';

import { viewerErrorAtom, viewerLoadingAtom, viewerRoadmapAtom } from '../stores/viewer-atoms';

export function useViewerRoadmapLoader(roadmapId: string) {
  const setRoadmap = useSetAtom(viewerRoadmapAtom);
  const setLoading = useSetAtom(viewerLoadingAtom);
  const setError = useSetAtom(viewerErrorAtom);

  useEffect(() => {
    const roadmap = loadRoadmapFromLocalStorage(roadmapId);

    if (roadmap) {
      setRoadmap(roadmap);
      setLoading(false);
      setError(null);
    } else {
      setLoading(false);
      setError(`Roadmap not found: ${roadmapId}`);
    }
  }, [roadmapId, setRoadmap, setLoading, setError]);
}
