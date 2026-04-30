'use client';

import { useEffect } from 'react';

import { useSetAtom } from 'jotai';

import { getRoadmap } from '@/api/roadmap';
import type { RoadmapEvent } from '@/api/roadmap';
import { loadRoadmapFromLocalStorage } from '@/lib/roadmap-storage';
import type { Roadmap, RoadmapNode } from '@/types/roadmap.types';

import { viewerErrorAtom, viewerLoadingAtom, viewerRoadmapAtom } from '../stores/viewer-atoms';

import type { Edge } from '@xyflow/react';

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function isRoadmapNode(value: unknown): value is RoadmapNode {
  return (
    isRecord(value) &&
    typeof value.id === 'string' &&
    isRecord(value.position) &&
    isRecord(value.data)
  );
}

function isEdge(value: unknown): value is Edge {
  return (
    isRecord(value) &&
    typeof value.id === 'string' &&
    typeof value.source === 'string' &&
    typeof value.target === 'string'
  );
}

function replayEvents(events: RoadmapEvent[]): Pick<Roadmap, 'nodes' | 'edges'> {
  return events.reduce<Pick<Roadmap, 'nodes' | 'edges'>>(
    (roadmap, event) => {
      const payload = event.payload;
      if (!isRecord(payload)) return roadmap;
      if (Array.isArray(payload.nodes) || Array.isArray(payload.edges)) {
        return {
          nodes: Array.isArray(payload.nodes) ? payload.nodes.filter(isRoadmapNode) : roadmap.nodes,
          edges: Array.isArray(payload.edges) ? payload.edges.filter(isEdge) : roadmap.edges,
        };
      }
      if (isRoadmapNode(payload)) return { ...roadmap, nodes: [...roadmap.nodes, payload] };
      if (isEdge(payload)) return { ...roadmap, edges: [...roadmap.edges, payload] };
      return roadmap;
    },
    { nodes: [], edges: [] },
  );
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

      // 1. API에서 로드맵 상세 조회 → 메타데이터 + localStorage에서 노드/엣지
      try {
        const [{ getRoadmapEvents }, detail] = await Promise.all([
          import('@/api/roadmap'),
          getRoadmap(Number(roadmapId)),
        ]);

        if (isCancelled) return;

        const local = loadRoadmapFromLocalStorage(Number(roadmapId));
        const events = await getRoadmapEvents(roadmapId, 0).catch(() => []);
        const replayed = replayEvents(events);

        setRoadmap({
          id: detail.id,
          title: detail.title,
          description: detail.description ?? undefined,
          nodes: replayed.nodes.length > 0 ? replayed.nodes : (local?.nodes ?? []),
          edges: replayed.edges.length > 0 ? replayed.edges : (local?.edges ?? []),
          author: { id: detail.owner.id, name: detail.owner.nickname },
          isPublic: detail.isPublic,
          createdAt: detail.createdAt,
          updatedAt: detail.updatedAt,
        });
        setLoading(false);
        return;
      } catch {
        // API 실패 시 localStorage fallback
      }

      if (isCancelled) return;

      // 2. Fallback: localStorage만으로 로드
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
