import { beforeEach, describe, expect, it, vi } from 'vitest';

import { STORAGE_KEY, createEmptyRoadmap, saveRoadmapToLocalStorage } from './roadmap-storage';

describe('roadmap-storage', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('returns true when a roadmap is saved', () => {
    const roadmap = createEmptyRoadmap(1, { title: '저장 테스트' });

    expect(saveRoadmapToLocalStorage(roadmap)).toBe(true);
    expect(localStorage.getItem(STORAGE_KEY)).toContain('저장 테스트');
  });

  it('returns false when localStorage write fails', () => {
    const roadmap = createEmptyRoadmap(1, { title: '실패 테스트' });
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
      throw new DOMException('quota exceeded', 'QuotaExceededError');
    });

    expect(saveRoadmapToLocalStorage(roadmap)).toBe(false);
  });
});
