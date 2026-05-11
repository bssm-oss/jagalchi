import { renderHook, waitFor } from '@testing-library/react';
import { Provider, createStore } from 'jotai';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { STORAGE_KEY } from '../services/roadmap-storage';
import { edgesAtom, nodesAtom, roadmapTitleAtom } from '../stores/editor-atoms';

import { useRoadmapLoader } from './use-roadmap-loader';

import type { RoadmapNode } from '../types/editor.types';
import type { Edge } from '@xyflow/react';

const mocks = vi.hoisted(() => {
  process.env.NEXT_PUBLIC_REALTIME_ENABLED = 'true';
  return {
    getRoadmap: vi.fn(),
    getRoadmapEvents: vi.fn(),
    replace: vi.fn(),
  };
});

vi.mock('next/navigation', () => ({
  useRouter: () => ({ replace: mocks.replace }),
}));

vi.mock('@/api/roadmap', () => ({
  getRoadmap: mocks.getRoadmap,
  getRoadmapEvents: mocks.getRoadmapEvents,
}));

const now = '2025-12-15T14:30:00.000Z';

function createWrapper(store = createStore()) {
  return function TestWrapper({ children }: { children: React.ReactNode }) {
    return <Provider store={store}>{children}</Provider>;
  };
}

function createNode(id = 'node-1'): RoadmapNode {
  return {
    id,
    type: 'jagalchi-node',
    position: { x: 100, y: 50 },
    data: {
      label: 'HTML/CSS 기초',
      description: '웹 기초',
      resources: [],
      variant: 'blue',
      isLocked: false,
    },
  };
}

function createEdge(): Edge {
  return { id: 'edge-1-2', source: 'node-1', target: 'node-2' };
}

function mockRoadmapDetail(title = 'API 로드맵') {
  mocks.getRoadmap.mockResolvedValue({
    id: 1,
    title,
    description: 'API 설명',
    thumbnailUrl: null,
    isPublic: true,
    viewCount: 0,
    owner: { id: 1, nickname: '김선배', profileImageUrl: null },
    stats: { totalNodes: 1, totalEdges: 1, forkCount: 0 },
    tags: [],
    createdAt: now,
    updatedAt: now,
  });
}

describe('useRoadmapLoader', () => {
  beforeEach(() => {
    localStorage.clear();
    mocks.getRoadmap.mockReset();
    mocks.getRoadmapEvents.mockReset();
    mocks.replace.mockClear();
  });

  it('replays API events into editor atoms and local cache', async () => {
    const store = createStore();
    const node = createNode();
    const edge = createEdge();
    mockRoadmapDetail();
    mocks.getRoadmapEvents.mockResolvedValue([
      { type: 'EVENT', eventId: 'evt-node', sequence: 1, payload: node },
      { type: 'EVENT', eventId: 'evt-edge', sequence: 2, payload: edge },
      {
        type: 'EVENT',
        eventId: 'evt-node-edit',
        sequence: 3,
        payload: {
          target: { type: 'NODE', object: 'node-1' },
          state: { position: { x: 180, y: 90 } },
        },
      },
      {
        type: 'EVENT',
        eventId: 'evt-title',
        sequence: 4,
        payload: {
          target: { type: 'NODE', object: '1' },
          data: { title: '이벤트 제목' },
        },
      },
    ]);

    const { result } = renderHook(() => useRoadmapLoader({ roadmapId: '1' }), {
      wrapper: createWrapper(store),
    });

    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.error).toBeNull();
    expect(mocks.getRoadmapEvents).toHaveBeenCalledWith('1', 0);
    expect(store.get(roadmapTitleAtom)).toBe('이벤트 제목');
    expect(store.get(nodesAtom)).toEqual([{ ...node, position: { x: 180, y: 90 } }]);
    expect(store.get(edgesAtom)).toEqual([edge]);

    const cached = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? '[]') as Array<{
      title: string;
      nodes: RoadmapNode[];
      edges: Edge[];
    }>;
    expect(cached[0]?.title).toBe('이벤트 제목');
    expect(cached[0]?.nodes).toHaveLength(1);
    expect(cached[0]?.edges).toHaveLength(1);
  });

  it('falls back to localStorage when API loading fails', async () => {
    const store = createStore();
    const localNode = createNode('local-node');
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify([
        {
          id: 2,
          title: '캐시 로드맵',
          description: '캐시 설명',
          nodes: [localNode],
          edges: [],
          isPublic: false,
          createdAt: now,
          updatedAt: now,
        },
      ]),
    );
    mocks.getRoadmap.mockRejectedValue(new Error('offline'));
    mocks.getRoadmapEvents.mockRejectedValue(new Error('offline'));

    const { result } = renderHook(() => useRoadmapLoader({ roadmapId: '2' }), {
      wrapper: createWrapper(store),
    });

    await waitFor(() => expect(result.current.isLoading).toBe(false));

    expect(result.current.error).toBeNull();
    expect(store.get(roadmapTitleAtom)).toBe('캐시 로드맵');
    expect(store.get(nodesAtom)).toEqual([localNode]);
    expect(store.get(edgesAtom)).toEqual([]);
  });
});
