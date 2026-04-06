'use client';

import { useEffect } from 'react';

import { useSetAtom } from 'jotai';

import { loadRoadmapFromLocalStorage } from '@/lib/roadmap-storage';
import type { Roadmap } from '@/types/roadmap.types';

import { viewerErrorAtom, viewerLoadingAtom, viewerRoadmapAtom } from '../stores/viewer-atoms';

/**
 * Attempts to load a roadmap from the API.
 * Currently returns null — real API integration is deferred.
 * Replace this with an actual fetch call when the endpoint is ready.
 */
async function loadFromApi(_roadmapId: string): Promise<Roadmap | null> {
  // TODO: Replace with actual API call
  // Example:
  //   const res = await fetch(`/api/roadmaps/${roadmapId}`);
  //   if (!res.ok) return null;
  //   return res.json() as Promise<Roadmap>;
  return null;
}

export function useViewerRoadmapLoader(roadmapId: string) {
  const setRoadmap = useSetAtom(viewerRoadmapAtom);
  const setLoading = useSetAtom(viewerLoadingAtom);
  const setError = useSetAtom(viewerErrorAtom);

  useEffect(() => {
    let isCancelled = false;

    async function load() {
      setLoading(true);
      setError(null);

      // 1. Try API first
      const apiRoadmap = await loadFromApi(roadmapId);

      if (isCancelled) return;

      if (apiRoadmap) {
        setRoadmap(apiRoadmap);
        setLoading(false);
        return;
      }

      // 2. Fallback to localStorage
      const localRoadmap = loadRoadmapFromLocalStorage(roadmapId);

      if (isCancelled) return;

      if (localRoadmap) {
        setRoadmap(localRoadmap);
        setLoading(false);
      } else {
        setLoading(false);
        setError(`Roadmap not found: ${roadmapId}`);
      }
    }

    load();

    return () => {
      isCancelled = true;
    };
  }, [roadmapId, setRoadmap, setLoading, setError]);
}
