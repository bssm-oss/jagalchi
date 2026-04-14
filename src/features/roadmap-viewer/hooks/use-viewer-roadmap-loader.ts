'use client';

import { useEffect } from 'react';

import { useSetAtom } from 'jotai';

import { loadRoadmapFromLocalStorage } from '@/lib/roadmap-storage';
import type { Roadmap } from '@/types/roadmap.types';

import { viewerErrorAtom, viewerLoadingAtom, viewerRoadmapAtom } from '../stores/viewer-atoms';

const isRealtimeEnabled = process.env.NEXT_PUBLIC_REALTIME_ENABLED === 'true';

/**
 * Attempts to load a roadmap from the API.
 * Falls back to null when API is unavailable.
 */
async function loadFromApi(roadmapId: string): Promise<Roadmap | null> {
  if (!isRealtimeEnabled) return null;

  try {
    const response = await fetch(`/api/roadmaps/${roadmapId}`);
    if (!response.ok) return null;
    return (await response.json()) as Roadmap;
  } catch {
    return null;
  }
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
      const localRoadmap = loadRoadmapFromLocalStorage(Number(roadmapId));

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
