import { apiClient } from './client';

// === Roadmap Types (aligned with docs/api.md Roadmap Service) ===

interface CreateRoadmapRequest {
  title: string;
  description?: string;
  directoryId?: string;
  isPublic?: boolean;
  thumbnailUrl?: string;
  tags?: string[];
}

interface UpdateRoadmapRequest {
  title?: string;
  description?: string;
  isPublic?: boolean;
  thumbnailUrl?: string;
  tags?: string[];
}

interface RoadmapResponse {
  id: string;
  title: string;
  description: string | null;
  directoryId: string | null;
  ownerId: string;
  isPublic: boolean;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

interface RoadmapDetailResponse extends RoadmapResponse {
  owner: { id: string; name: string };
  stats: { viewCount: number; forkCount: number };
  tags: string[];
}

interface RoadmapListResponse {
  content: RoadmapResponse[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

interface RoadmapUpdateResponse {
  id: string;
  updatedAt: string;
}

interface RoadmapDeleteResponse {
  message: string;
}

// === Directory Types ===

interface DirectoryResponse {
  id: string;
  name: string;
  parentId: string | null;
}

interface DirectoryTreeNode {
  id: string;
  name: string;
  parentId: string | null;
  children: DirectoryTreeNode[];
}

type DirectoryTreeResponse = DirectoryTreeNode[];

// === Progress Types ===

interface ProgressResponse {
  roadmapId: string;
  totalNodes: number;
  completedNodes: number;
  progressPercentage: number;
  completedNodeIds: string[];
  updatedAt: string;
}

interface NodeCompleteResponse {
  nodeId: string;
  isCompleted: boolean;
  roadmapProgress: number;
  completedAt: string | null;
}

// === Event Types (Node Service) ===

interface RoadmapEvent {
  type: string;
  eventId: string;
  sequence: number;
  payload: Record<string, unknown>;
}

// === Roadmap CRUD ===

export const createRoadmap = (data: CreateRoadmapRequest) =>
  apiClient.post<RoadmapResponse>('/roadmaps', data);

export const getRoadmap = (roadmapId: string) =>
  apiClient.get<RoadmapDetailResponse>(`/roadmaps/${roadmapId}`);

interface RoadmapListParams {
  page?: number;
  size?: number;
  sort?: string;
  query?: string;
  userId?: string;
  directoryId?: string;
  isPublic?: boolean;
  tags?: string[];
}

export const getRoadmaps = (params: RoadmapListParams = {}) => {
  const searchParams = new URLSearchParams();
  if (params.page !== undefined) searchParams.set('page', String(params.page));
  if (params.size !== undefined) searchParams.set('size', String(params.size));
  if (params.sort) searchParams.set('sort', params.sort);
  if (params.query) searchParams.set('query', params.query);
  if (params.userId) searchParams.set('userId', params.userId);
  if (params.directoryId) searchParams.set('directoryId', params.directoryId);
  if (params.isPublic !== undefined) searchParams.set('isPublic', String(params.isPublic));
  if (params.tags?.length) searchParams.set('tags', params.tags.join(','));

  const qs = searchParams.toString();
  return apiClient.get<RoadmapListResponse>(`/roadmaps${qs ? `?${qs}` : ''}`);
};

export const updateRoadmap = (roadmapId: string, data: UpdateRoadmapRequest) =>
  apiClient.patch<RoadmapUpdateResponse>(`/roadmaps/${roadmapId}`, data);

export const deleteRoadmap = (roadmapId: string) =>
  apiClient.delete<RoadmapDeleteResponse>(`/roadmaps/${roadmapId}`);

// === Directory ===

export const getDirectoryTree = () => apiClient.get<DirectoryTreeResponse>('/directories/tree');

export const createDirectory = (data: { name: string; parentId?: string }) =>
  apiClient.post<DirectoryResponse>('/directories', data);

export const updateDirectory = (directoryId: string, data: { name: string }) =>
  apiClient.patch<DirectoryResponse>(`/directories/${directoryId}`, data);

export const deleteDirectory = (directoryId: string) =>
  apiClient.delete<void>(`/directories/${directoryId}`);

// === Progress ===

export const getMyProgress = (roadmapId: string) =>
  apiClient.get<ProgressResponse>(`/roadmaps/${roadmapId}/my-progress`);

export const getUserProgress = (roadmapId: string, userId: string) =>
  apiClient.get<ProgressResponse>(`/roadmaps/${roadmapId}/users/${userId}/progress`);

export const completeNode = (
  roadmapId: string,
  nodeId: string,
  data: { isCompleted: boolean; link?: string },
) => apiClient.post<NodeCompleteResponse>(`/roadmaps/${roadmapId}/nodes/${nodeId}/complete`, data);

// === Fork Types ===

interface ForkRoadmapResponse {
  id: string;
  title: string;
  description: string | null;
  directoryId: string | null;
  ownerId: string;
  isPublic: boolean;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

interface ForkTreeNode {
  id: string;
  title: string;
  ownerId: string;
  ownerName: string;
  forkCount: number;
  children: ForkTreeNode[];
}

interface ForkStatusResponse {
  roadmapId: string;
  forkCount: number;
  isForkedByCurrentUser: boolean;
  originalRoadmapId: string | null;
  originalRoadmapTitle: string | null;
}

// === Popular Types ===

interface PopularRoadmapItem {
  id: string;
  title: string;
  description: string | null;
  thumbnailUrl: string | null;
  isPublic: boolean;
  viewCount: number;
  forkCount: number;
  tags: string[];
  owner: {
    id: string;
    nickname: string;
    profileImageUrl: string | null;
  };
  createdAt: string;
  updatedAt: string;
}

interface PopularRoadmapsResponse {
  content: PopularRoadmapItem[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

interface PopularRoadmapsParams {
  page?: number;
  size?: number;
  sortBy?: 'forks' | 'views';
}

// === Events (Node Service) ===

export const getRoadmapEvents = (roadmapId: string, since = 0) =>
  apiClient.get<RoadmapEvent[]>(`/roadmap/${roadmapId}/events?since=${since}`);

// === Fork ===

export const forkRoadmap = (roadmapId: string) =>
  apiClient.post<ForkRoadmapResponse>(`/roadmaps/${roadmapId}/fork`);

export const getForkTree = (roadmapId: string) =>
  apiClient.get<ForkTreeNode>(`/roadmaps/${roadmapId}/fork-tree`);

export const getForkStatus = (roadmapId: string) =>
  apiClient.get<ForkStatusResponse>(`/roadmaps/${roadmapId}/fork-status`);

// === Popular ===

export const getPopularRoadmaps = (params: PopularRoadmapsParams = {}) => {
  const searchParams = new URLSearchParams();
  if (params.page !== undefined) searchParams.set('page', String(params.page));
  if (params.size !== undefined) searchParams.set('size', String(params.size));
  if (params.sortBy) searchParams.set('sortBy', params.sortBy);

  const qs = searchParams.toString();
  return apiClient.get<PopularRoadmapsResponse>(`/roadmaps/popular${qs ? `?${qs}` : ''}`);
};

// === Type Exports ===

export type {
  CreateRoadmapRequest,
  UpdateRoadmapRequest,
  RoadmapResponse,
  RoadmapDetailResponse,
  RoadmapListResponse,
  RoadmapListParams,
  RoadmapUpdateResponse,
  RoadmapDeleteResponse,
  DirectoryResponse,
  DirectoryTreeNode,
  DirectoryTreeResponse,
  ProgressResponse,
  NodeCompleteResponse,
  RoadmapEvent,
  ForkRoadmapResponse,
  ForkTreeNode,
  ForkStatusResponse,
  PopularRoadmapItem,
  PopularRoadmapsResponse,
  PopularRoadmapsParams,
};
