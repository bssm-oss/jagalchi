'use client';

import { useMemo } from 'react';

import { useRouter } from 'next/navigation';

import { useAtomValue, useSetAtom } from 'jotai';

import { logoutAtom } from '@/features/auth';
import { MyRoadmapsToolbar } from '@/features/my-roadmaps/components/molecules/MyRoadmapsToolbar';
import { MyRoadmapsGrid } from '@/features/my-roadmaps/components/organisms/MyRoadmapsGrid';
import { MyRoadmapsHeader } from '@/features/my-roadmaps/components/organisms/MyRoadmapsHeader';
import { MyRoadmapsLayout } from '@/features/my-roadmaps/components/templates';
import {
  filterCategoryAtom,
  myRoadmapItemsAtom,
  searchQueryAtom,
  sidebarCategoryAtom,
  sortByAtom,
  sortOrderAtom,
} from '@/features/my-roadmaps/stores/my-roadmaps.atoms';

export default function MyRoadmapsPage() {
  const router = useRouter();
  const logout = useSetAtom(logoutAtom);
  const items = useAtomValue(myRoadmapItemsAtom);
  const activeCategory = useAtomValue(sidebarCategoryAtom);
  const sortOrder = useAtomValue(sortOrderAtom);
  const sortBy = useAtomValue(sortByAtom);
  const filterCategory = useAtomValue(filterCategoryAtom);
  const searchQuery = useAtomValue(searchQueryAtom);

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  const filteredRoadmaps = useMemo(
    () =>
      items
        .filter((roadmap) => {
          // 1. Sidebar Category Filter
          let categoryMatch = false;
          switch (activeCategory) {
            case 'recent':
              categoryMatch = true;
              break;
            case 'community':
              categoryMatch = roadmap.category === 'community';
              break;
            case 'my-roadmap':
              categoryMatch = roadmap.category === 'my-roadmap';
              break;
            case 'shared':
              categoryMatch = !!roadmap.isShared;
              break;
            case 'favorites':
              categoryMatch = !!roadmap.isFavorite;
              break;
            default:
              categoryMatch = true;
          }

          if (!categoryMatch) return false;

          // 2. Toolbar Category Filter
          if (filterCategory !== 'all') {
            const typeMatch =
              filterCategory === 'roadmap'
                ? roadmap.type === 'Roadmap'
                : roadmap.type === 'Directory';
            if (!typeMatch) return false;
          }

          // 3. Search Query Filter
          if (searchQuery.trim()) {
            const lowerQuery = searchQuery.toLowerCase();
            if (!roadmap.title.toLowerCase().includes(lowerQuery)) return false;
          }

          return true;
        })
        .sort((a, b) => {
          // 4. Sorting
          let comparison = 0;
          switch (sortBy) {
            case 'recent':
              comparison =
                new Date(b.updatedAt || 0).getTime() - new Date(a.updatedAt || 0).getTime();
              break;
            case 'name':
              comparison = (b.title || '').localeCompare(a.title || '');
              break;
            case 'size':
              comparison = (b.fileCount || 0) - (a.fileCount || 0);
              break;
            default:
              comparison = 0;
          }

          return sortOrder === 'asc' ? -comparison : comparison;
        }),
    [items, sortOrder, sortBy, filterCategory, activeCategory, searchQuery],
  );

  return (
    <MyRoadmapsLayout onLogout={handleLogout}>
      <div className="flex h-full flex-col">
        <MyRoadmapsHeader />
        <div className="flex-1 px-20 pb-20">
          <MyRoadmapsToolbar />
          <div className="mt-6">
            <MyRoadmapsGrid roadmaps={filteredRoadmaps} />
          </div>
        </div>
      </div>
    </MyRoadmapsLayout>
  );
}
