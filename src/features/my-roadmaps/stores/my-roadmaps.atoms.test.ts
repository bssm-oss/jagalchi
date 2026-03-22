import { createStore } from 'jotai';
import { describe, it, expect, beforeEach } from 'vitest';

import { MOCK_MY_ROADMAPS } from '../constants/my-roadmaps.mock';
import type { RoadmapData } from '../types/my-roadmaps.types';
import { myRoadmapItemsAtom } from './my-roadmaps.atoms';

describe('myRoadmapItemsAtom', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('initial value is MOCK_MY_ROADMAPS', () => {
    const store = createStore();
    expect(store.get(myRoadmapItemsAtom)).toEqual(MOCK_MY_ROADMAPS);
  });

  it('can add items', () => {
    const store = createStore();
    const newItem: RoadmapData = {
      id: '99',
      title: 'New Roadmap',
      author: '테스트',
      type: 'Roadmap',
      updatedAt: '2024-03-01T00:00:00Z',
      category: 'my-roadmap',
    };

    store.set(myRoadmapItemsAtom, [...store.get(myRoadmapItemsAtom), newItem]);

    const items = store.get(myRoadmapItemsAtom);
    expect(items).toHaveLength(MOCK_MY_ROADMAPS.length + 1);
    expect(items.find((i) => i.id === '99')).toEqual(newItem);
  });

  it('persists to localStorage', () => {
    const store = createStore();
    const updated = store.get(myRoadmapItemsAtom).slice(0, 2);
    store.set(myRoadmapItemsAtom, updated);

    const stored = localStorage.getItem('jagalchi-my-roadmaps');
    expect(stored).not.toBeNull();
    expect(JSON.parse(stored!)).toEqual(updated);
  });
});
