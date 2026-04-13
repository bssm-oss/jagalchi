import { useQuery } from '@tanstack/react-query';

import { getForkTree, type ForkTreeNode } from '@/api/roadmap';
import { queryKeys } from '@/lib/query-keys';

export function useForkTree(roadmapId: string) {
  return useQuery<ForkTreeNode>({
    queryKey: queryKeys.roadmaps.forkTree(roadmapId),
    queryFn: () => getForkTree(roadmapId),
    enabled: Boolean(roadmapId),
  });
}
