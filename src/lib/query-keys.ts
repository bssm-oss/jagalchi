/**
 * TanStack Query key factory
 * 일관된 쿼리 키 관리를 위한 중앙화된 키 팩토리
 */

export const queryKeys = {
  auth: {
    all: ['auth'] as const,
    me: () => [...queryKeys.auth.all, 'me'] as const,
  },

  users: {
    all: ['users'] as const,
    detail: (name: string) => [...queryKeys.users.all, 'detail', name] as const,
    followers: (name: string) => [...queryKeys.users.all, 'followers', name] as const,
    followings: (name: string) => [...queryKeys.users.all, 'followings', name] as const,
  },

  roadmaps: {
    all: ['roadmaps'] as const,
    lists: () => [...queryKeys.roadmaps.all, 'list'] as const,
    detail: (id: string) => [...queryKeys.roadmaps.all, 'detail', id] as const,
    events: (id: string, since?: number) =>
      [...queryKeys.roadmaps.all, 'events', id, since] as const,
    progress: (id: string) => [...queryKeys.roadmaps.all, 'progress', id] as const,
    forkTree: (id: string) => [...queryKeys.roadmaps.all, 'fork-tree', id] as const,
    forkStatus: (id: string) => [...queryKeys.roadmaps.all, 'fork-status', id] as const,
    popular: (params?: object) => [...queryKeys.roadmaps.all, 'popular', params] as const,
  },

  directories: {
    all: ['directories'] as const,
    tree: () => [...queryKeys.directories.all, 'tree'] as const,
  },

  community: {
    all: ['community'] as const,
    lists: (params?: object) => [...queryKeys.community.all, 'list', params] as const,
    detail: (id: string) => [...queryKeys.community.all, 'detail', id] as const,
  },
} as const;
