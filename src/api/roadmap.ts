import { apiClient } from './client';

// === Types (aligned with docs/api.md Node service) ===

interface RoadmapEvent {
  type: string;
  eventId: string;
  sequence: number;
  payload: Record<string, unknown>;
}

// === API Functions ===

/** GET /api/roadmap/{roadmapId}/events?since={sequence} — 이벤트 히스토리 조회 */
export const getRoadmapEvents = (roadmapId: string, since = 0) =>
  apiClient.get<RoadmapEvent[]>(`/roadmap/${roadmapId}/events?since=${since}`);

// === Type Exports ===

export type { RoadmapEvent };
