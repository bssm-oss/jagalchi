import { useQuery } from '@tanstack/react-query';

import { getForkStatus } from '@/api/roadmap';
import type { ForkStatusResponse } from '@/api/roadmap';
import { queryKeys } from '@/lib/query-keys';

export function useForkStatus(roadmapId: string) {
  return useQuery<ForkStatusResponse>({
    queryKey: queryKeys.roadmaps.forkStatus(roadmapId),
    queryFn: () => getForkStatus(roadmapId),
    enabled: Boolean(roadmapId),
  });
}
