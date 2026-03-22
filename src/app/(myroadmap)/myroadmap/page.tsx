'use client';

import { useAtomValue } from 'jotai';

import { MyRoadmapsToolbar } from '@/features/my-roadmaps/components/molecules/MyRoadmapsToolbar';
import { MyRoadmapsGrid } from '@/features/my-roadmaps/components/organisms/MyRoadmapsGrid';
import { MyRoadmapsHeader } from '@/features/my-roadmaps/components/organisms/MyRoadmapsHeader';
import { MyRoadmapsLayout } from '@/features/my-roadmaps/components/templates';
import {
  filterCategoryAtom,
  myRoadmapItemsAtom,
  sidebarCategoryAtom,
  sortByAtom,
  sortOrderAtom,
} from '@/features/my-roadmaps/stores/my-roadmaps.atoms';

export default function MyRoadmapsPage() {
  const items = useAtomValue(myRoadmapItemsAtom);
  const activeCategory = useAtomValue(sidebarCategoryAtom);
  const sortOrder = useAtomValue(sortOrderAtom);
  const sortBy = useAtomValue(sortByAtom);
  const filterCategory = useAtomValue(filterCategoryAtom);

  const filteredRoadmaps = items
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
          filterCategory === 'roadmap' ? roadmap.type === 'Roadmap' : roadmap.type === 'Directory';
        if (!typeMatch) return false;
      }

      return true;
    })
    .sort((a, b) => {
      // 3. Sorting
      let comparison = 0;
      switch (sortBy) {
        case 'recent':
          comparison = new Date(b.updatedAt || 0).getTime() - new Date(a.updatedAt || 0).getTime();
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
    });

  return (
    <MyRoadmapsLayout>
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
