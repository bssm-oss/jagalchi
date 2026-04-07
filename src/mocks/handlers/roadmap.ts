import { http, HttpResponse } from 'msw';

import type { Roadmap } from '@/types/roadmap.types';

import { MOCK_ROADMAPS } from '../fixtures/roadmaps';

// 가변 로드맵 저장소 (런타임에서 CRUD 추적)
const roadmapStore: Roadmap[] = [...MOCK_ROADMAPS];

// Mock 디렉토리 저장소
const directoryStore = [
  { id: 'dir-1', name: '프론트엔드', parentId: null },
  { id: 'dir-2', name: '백엔드', parentId: null },
  { id: 'dir-3', name: 'React', parentId: 'dir-1' },
];

/** Roadmap API 핸들러 */
export const roadmapHandlers = [
  // GET /api/roadmaps — 로드맵 목록 조회 (페이지네이션)
  http.get('/api/roadmaps', ({ request }) => {
    const url = new URL(request.url);
    const page = Number(url.searchParams.get('page') ?? '0');
    const size = Number(url.searchParams.get('size') ?? '20');
    const query = url.searchParams.get('query') ?? '';
    const directoryId = url.searchParams.get('directoryId');

    let filtered = roadmapStore;

    if (query) {
      const q = query.toLowerCase();
      filtered = filtered.filter(
        (r) => r.title.toLowerCase().includes(q) || r.description?.toLowerCase().includes(q),
      );
    }

    if (directoryId) {
      // TODO: directoryId 필터링 (fixture에 directoryId 필드 추가 필요)
    }

    const start = page * size;
    const content = filtered.slice(start, start + size);

    return HttpResponse.json({
      content: content.map((r) => ({
        id: r.id,
        title: r.title,
        description: r.description,
        directoryId: null,
        ownerId: r.author?.id ?? 'user-1',
        isPublic: r.isPublic,
        viewCount: Math.floor(Math.random() * 100),
        createdAt: r.createdAt,
        updatedAt: r.updatedAt,
      })),
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      page,
      size,
    });
  }),

  // GET /api/roadmaps/:id — 단일 로드맵 조회
  http.get<{ id: string }>('/api/roadmaps/:id', ({ params }) => {
    const { id } = params;
    const roadmap = roadmapStore.find((r) => r.id === id);

    if (!roadmap) {
      return HttpResponse.json({ message: '로드맵을 찾을 수 없습니다' }, { status: 404 });
    }

    return HttpResponse.json({
      id: roadmap.id,
      title: roadmap.title,
      description: roadmap.description,
      directoryId: null,
      ownerId: roadmap.author?.id ?? 'user-1',
      isPublic: roadmap.isPublic,
      viewCount: 42,
      createdAt: roadmap.createdAt,
      updatedAt: roadmap.updatedAt,
      owner: roadmap.author ?? { id: 'user-1', name: '김선배' },
      stats: { viewCount: 42, forkCount: 5 },
      tags: [],
    });
  }),

  // POST /api/roadmaps — 로드맵 생성
  http.post('/api/roadmaps', async ({ request }) => {
    const body = (await request.json()) as {
      title: string;
      description?: string;
      isPublic?: boolean;
    };

    const now = new Date().toISOString();
    const newRoadmap: Roadmap = {
      id: `roadmap-${Date.now()}`,
      title: body.title,
      description: body.description,
      nodes: [],
      edges: [],
      author: { id: 'user-1', name: '김선배' },
      isPublic: body.isPublic ?? false,
      createdAt: now,
      updatedAt: now,
    };

    roadmapStore.push(newRoadmap);

    return HttpResponse.json(
      {
        id: newRoadmap.id,
        title: newRoadmap.title,
        description: newRoadmap.description,
        directoryId: null,
        ownerId: 'user-1',
        isPublic: newRoadmap.isPublic,
        viewCount: 0,
        createdAt: now,
        updatedAt: now,
      },
      { status: 201 },
    );
  }),

  // PATCH /api/roadmaps/:id — 로드맵 수정
  http.patch<{ id: string }>('/api/roadmaps/:id', async ({ params, request }) => {
    const { id } = params;
    const roadmapIndex = roadmapStore.findIndex((r) => r.id === id);

    if (roadmapIndex === -1) {
      return HttpResponse.json({ message: '로드맵을 찾을 수 없습니다' }, { status: 404 });
    }

    const body = (await request.json()) as {
      title?: string;
      description?: string;
      isPublic?: boolean;
    };
    const existing = roadmapStore[roadmapIndex];
    const now = new Date().toISOString();

    roadmapStore[roadmapIndex] = {
      ...existing,
      title: body.title ?? existing.title,
      description: body.description ?? existing.description,
      isPublic: body.isPublic ?? existing.isPublic,
      updatedAt: now,
    };

    return HttpResponse.json({ id, updatedAt: now });
  }),

  // DELETE /api/roadmaps/:id — 로드맵 삭제
  http.delete<{ id: string }>('/api/roadmaps/:id', ({ params }) => {
    const { id } = params;
    const roadmapIndex = roadmapStore.findIndex((r) => r.id === id);

    if (roadmapIndex === -1) {
      return HttpResponse.json({ message: '로드맵을 찾을 수 없습니다' }, { status: 404 });
    }

    roadmapStore.splice(roadmapIndex, 1);
    return HttpResponse.json({ message: '로드맵이 삭제되었습니다' });
  }),

  // GET /api/directories/tree — 디렉토리 트리 조회
  http.get('/api/directories/tree', () => {
    const buildTree = (parentId: string | null): typeof directoryStore => {
      return directoryStore
        .filter((d) => d.parentId === parentId)
        .map((d) => ({ ...d, children: buildTree(d.id) }));
    };
    return HttpResponse.json(buildTree(null));
  }),

  // POST /api/directories — 디렉토리 생성
  http.post('/api/directories', async ({ request }) => {
    const body = (await request.json()) as { name: string; parentId?: string };
    const newDir = {
      id: `dir-${Date.now()}`,
      name: body.name,
      parentId: body.parentId ?? null,
    };
    directoryStore.push(newDir);
    return HttpResponse.json(newDir, { status: 201 });
  }),

  // PATCH /api/directories/:id — 디렉토리 수정
  http.patch<{ directoryId: string }>(
    '/api/directories/:directoryId',
    async ({ params, request }) => {
      const { directoryId } = params;
      const body = (await request.json()) as { name: string };
      const dir = directoryStore.find((d) => d.id === directoryId);

      if (!dir) {
        return HttpResponse.json({ message: '디렉토리를 찾을 수 없습니다' }, { status: 404 });
      }

      dir.name = body.name;
      return HttpResponse.json(dir);
    },
  ),

  // DELETE /api/directories/:id — 디렉토리 삭제
  http.delete<{ directoryId: string }>('/api/directories/:directoryId', ({ params }) => {
    const { directoryId } = params;
    const idx = directoryStore.findIndex((d) => d.id === directoryId);

    if (idx === -1) {
      return HttpResponse.json({ message: '디렉토리를 찾을 수 없습니다' }, { status: 404 });
    }

    directoryStore.splice(idx, 1);
    return new HttpResponse(null, { status: 204 });
  }),

  // GET /api/roadmaps/:id/my-progress — 내 진행률
  http.get<{ roadmapId: string }>('/api/roadmaps/:roadmapId/my-progress', ({ params }) => {
    return HttpResponse.json({
      roadmapId: params.roadmapId,
      totalNodes: 5,
      completedNodes: 2,
      progressPercentage: 40,
      completedNodeIds: ['node-1', 'node-2'],
      updatedAt: new Date().toISOString(),
    });
  }),

  // POST /api/roadmaps/:roadmapId/nodes/:nodeId/complete — 노드 완료 토글
  http.post<{ roadmapId: string; nodeId: string }>(
    '/api/roadmaps/:roadmapId/nodes/:nodeId/complete',
    async ({ params, request }) => {
      const body = (await request.json()) as { isCompleted: boolean; link?: string };
      return HttpResponse.json({
        nodeId: params.nodeId,
        isCompleted: body.isCompleted,
        roadmapProgress: body.isCompleted ? 60 : 40,
        completedAt: body.isCompleted ? new Date().toISOString() : null,
      });
    },
  ),
];

export const resetRoadmapStore = (): void => {
  roadmapStore.length = 0;
  roadmapStore.push(...MOCK_ROADMAPS);
};
