import { useQuery } from '@tanstack/react-query';

import { getPopularRoadmaps } from '@/api/roadmap';
import type { PopularRoadmapsParams, PopularRoadmapsResponse } from '@/api/roadmap';
import { queryKeys } from '@/lib/query-keys';

export function usePopularRoadmaps(params: PopularRoadmapsParams = {}) {
  return useQuery<PopularRoadmapsResponse>({
    queryKey: queryKeys.roadmaps.popular(params),
    queryFn: () => getPopularRoadmaps(params),
  });
}
