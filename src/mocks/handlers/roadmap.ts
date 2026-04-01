import { http, HttpResponse } from 'msw';

import type { CreateRoadmapInput, Roadmap, UpdateRoadmapInput } from '@/types/roadmap.types';

import { MOCK_ROADMAPS } from '../fixtures/roadmaps';

interface ErrorResponse {
  message: string;
}

// 가변 로드맵 저장소 (런타임에서 CRUD 추적)
const roadmapStore: Roadmap[] = [...MOCK_ROADMAPS];

/** Roadmap API 핸들러 */
export const roadmapHandlers = [
  // GET /api/roadmaps - 로드맵 목록 조회
  http.get('/api/roadmaps', () => {
    return HttpResponse.json<Roadmap[]>(roadmapStore);
  }),

  // GET /api/roadmaps/:id - 단일 로드맵 조회
  http.get<{ id: string }>('/api/roadmaps/:id', ({ params }) => {
    const { id } = params;
    const roadmap = roadmapStore.find((r) => r.id === id);

    if (!roadmap) {
      return HttpResponse.json<ErrorResponse>(
        { message: '로드맵을 찾을 수 없습니다' },
        { status: 404 },
      );
    }

    return HttpResponse.json<Roadmap>(roadmap);
  }),

  // POST /api/roadmaps - 로드맵 생성
  http.post<Record<string, never>, CreateRoadmapInput>('/api/roadmaps', async ({ request }) => {
    const body = await request.json();

    const newRoadmap: Roadmap = {
      id: `roadmap-${Date.now()}`,
      title: body.title ?? '새 로드맵',
      description: body.description,
      nodes: [],
      edges: [],
      author: { id: 'user-1', name: '김선배' },
      isPublic: body.isPublic ?? false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    roadmapStore.push(newRoadmap);

    return HttpResponse.json<Roadmap>(newRoadmap, { status: 201 });
  }),

  // PUT /api/roadmaps/:id - 로드맵 업데이트
  http.put<{ id: string }, UpdateRoadmapInput>('/api/roadmaps/:id', async ({ params, request }) => {
    const { id } = params;
    const roadmapIndex = roadmapStore.findIndex((r) => r.id === id);

    if (roadmapIndex === -1) {
      return HttpResponse.json<ErrorResponse>(
        { message: '로드맵을 찾을 수 없습니다' },
        { status: 404 },
      );
    }

    const body = await request.json();
    const existing = roadmapStore[roadmapIndex];

    const updatedRoadmap: Roadmap = {
      ...existing,
      title: body.title ?? existing.title,
      description: body.description ?? existing.description,
      nodes: body.nodes ?? existing.nodes,
      edges: body.edges ?? existing.edges,
      isPublic: body.isPublic ?? existing.isPublic,
      updatedAt: new Date().toISOString(),
    };

    roadmapStore[roadmapIndex] = updatedRoadmap;

    return HttpResponse.json<Roadmap>(updatedRoadmap);
  }),

  // DELETE /api/roadmaps/:id - 로드맵 삭제
  http.delete<{ id: string }>('/api/roadmaps/:id', ({ params }) => {
    const { id } = params;
    const roadmapIndex = roadmapStore.findIndex((r) => r.id === id);

    if (roadmapIndex === -1) {
      return HttpResponse.json<ErrorResponse>(
        { message: '로드맵을 찾을 수 없습니다' },
        { status: 404 },
      );
    }

    roadmapStore.splice(roadmapIndex, 1);

    return new HttpResponse(null, { status: 204 });
  }),
];

// 테스트에서 로드맵 저장소 초기화할 때 사용
export const resetRoadmapStore = (): void => {
  roadmapStore.length = 0;
  roadmapStore.push(...MOCK_ROADMAPS);
};
