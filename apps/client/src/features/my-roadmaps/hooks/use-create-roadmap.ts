import { useMutation, useQueryClient } from '@tanstack/react-query';

import { createRoadmap } from '@/api/roadmap';
import type { CreateRoadmapRequest } from '@/api/roadmap';
import { queryKeys } from '@/lib/query-keys';
import { createEmptyRoadmap, saveRoadmapToLocalStorage } from '@/lib/roadmap-storage';

export function useCreateRoadmap() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateRoadmapRequest) => {
      const response = await createRoadmap(data);
      saveRoadmapToLocalStorage(
        createEmptyRoadmap(response.id, {
          title: response.title || data.title,
          description: response.description ?? data.description ?? undefined,
          isPublic: response.isPublic,
        }),
      );
      return response;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.roadmaps.lists() });
    },
  });
}
