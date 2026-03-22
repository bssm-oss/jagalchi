import { atom } from 'jotai';
import { atomWithStorage } from 'jotai/utils';

import type { SortOrder, SortBy, FilterCategory } from '@/types/sort.types';

import { MOCK_MY_ROADMAPS } from '../constants/my-roadmaps.mock';

import type { RoadmapData } from '../types/my-roadmaps.types';

export type SidebarCategory = 'recent' | 'community' | 'my-roadmap' | 'shared' | 'favorites';

export const sortOrderAtom = atom<SortOrder>('desc');
export const sortByAtom = atom<SortBy>('recent');
export const filterCategoryAtom = atom<FilterCategory>('all');
export const sidebarCategoryAtom = atom<SidebarCategory>('my-roadmap');

export const myRoadmapItemsAtom = atomWithStorage<RoadmapData[]>(
  'jagalchi-my-roadmaps',
  MOCK_MY_ROADMAPS,
);
