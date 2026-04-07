'use client';

import React, { useMemo } from 'react';

import { useAtomValue } from 'jotai';

import { COMMUNITY_MESSAGES } from '@/constants/messages';
import { usePopularRoadmaps } from '@/hooks/use-popular-roadmaps';

import { MOCK_COMMUNITY_DATA } from '../../../constants/community.mock';
import {
  activeTabAtom,
  filterCategoryAtom,
  searchQueryAtom,
  sortByAtom,
  sortOrderAtom,
} from '../../../stores/community.atoms';
import { filterAndSortCommunityItems } from '../../../utils/community-utils';
import { CommunityFilter } from '../../molecules/CommunityFilter';
import { CommunityHeader } from '../../molecules/CommunityHeader';
import { CommunityHero } from '../../molecules/CommunityHero';
import { CommunityGrid } from '../../organisms/CommunityGrid';

import type { CommunityItem } from '../../../types/community.types';

export function Community() {
  const searchQuery = useAtomValue(searchQueryAtom);
  const activeTab = useAtomValue(activeTabAtom);
  const sortOrder = useAtomValue(sortOrderAtom);
  const sortBy = useAtomValue(sortByAtom);
  const filterCategory = useAtomValue(filterCategoryAtom);

  const { data: popularData, isLoading: isPopularLoading } = usePopularRoadmaps({ size: 12 });

  const popularItems = useMemo<
    Pick<CommunityItem, 'id' | 'title' | 'author' | 'imageUrl'>[]
  >(() => {
    if (!popularData?.content) return [];
    return popularData.content.map((item) => ({
      id: item.id,
      title: item.title,
      author: item.owner.nickname,
      imageUrl: item.thumbnailUrl ?? undefined,
    }));
  }, [popularData]);

  const filteredItems = useMemo(
    () =>
      filterAndSortCommunityItems(MOCK_COMMUNITY_DATA, {
        searchQuery,
        filterCategory,
        activeTab,
        sortBy,
        sortOrder,
      }),
    [searchQuery, activeTab, sortOrder, sortBy, filterCategory],
  );

  const isPopularTab = activeTab === 'popular';

  return (
    <div className="flex min-h-screen w-full flex-col items-center bg-white">
      <CommunityHeader />
      <CommunityHero />
      <div className="flex w-full flex-col items-center bg-white pt-[40px] pb-[100px]">
        <CommunityFilter />
        {isPopularTab ? (
          isPopularLoading ? (
            <div className="flex h-[400px] w-full items-center justify-center">
              <p className="text-sm text-slate-500">불러오는 중...</p>
            </div>
          ) : popularItems.length === 0 ? (
            <div className="flex h-[400px] w-full items-center justify-center rounded-xl border border-dashed border-slate-200 bg-slate-50">
              <p className="text-sm font-medium text-slate-500">
                {COMMUNITY_MESSAGES.POPULAR_EMPTY}
              </p>
            </div>
          ) : (
            <CommunityGrid items={popularItems} />
          )
        ) : (
          <CommunityGrid items={filteredItems} />
        )}
      </div>
    </div>
  );
}
