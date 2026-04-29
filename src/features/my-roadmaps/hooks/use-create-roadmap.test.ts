import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';

import { createTestWrapper } from '@/test-utils';

vi.mock('@/api/roadmap', () => ({
  createRoadmap: vi.fn().mockResolvedValue({
    id: 100,
    title: 'Test Roadmap',
    description: null,
    isPublic: false,
  }),
}));

vi.mock('@/lib/roadmap-storage', () => ({
  createEmptyRoadmap: vi.fn((id: number, input: object) => ({
    id,
    ...input,
    nodes: [],
    edges: [],
  })),
  saveRoadmapToLocalStorage: vi.fn(),
}));

import { createRoadmap } from '@/api/roadmap';
import { createEmptyRoadmap, saveRoadmapToLocalStorage } from '@/lib/roadmap-storage';
import { useCreateRoadmap } from './use-create-roadmap';

describe('useCreateRoadmap', () => {
  it('calls createRoadmap on mutate', async () => {
    const { result } = renderHook(() => useCreateRoadmap(), {
      wrapper: createTestWrapper(),
    });

    result.current.mutate({ title: 'Test Roadmap', directoryId: 1 });

    await waitFor(() => {
      expect(createRoadmap).toHaveBeenCalledWith({
        title: 'Test Roadmap',
        directoryId: 1,
      });
    });

    expect(createEmptyRoadmap).toHaveBeenCalledWith(100, {
      title: 'Test Roadmap',
      description: undefined,
      isPublic: false,
    });
    expect(saveRoadmapToLocalStorage).toHaveBeenCalledWith({
      id: 100,
      title: 'Test Roadmap',
      description: undefined,
      isPublic: false,
      nodes: [],
      edges: [],
    });
  });
});
