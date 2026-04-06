'use client';

import { useRouter } from 'next/navigation';

import { useAtom } from 'jotai';

import { MY_ROADMAPS_MESSAGES } from '@/constants/messages';

import { breadcrumbPathAtom, myRoadmapItemsAtom } from '../../../stores/my-roadmaps.atoms';
import { RoadmapData } from '../../../types/my-roadmaps.types';
import { RoadmapCard } from '../../atoms/RoadmapCard';

interface MyRoadmapsGridProps {
  roadmaps: RoadmapData[];
  isSearching?: boolean;
}

export function MyRoadmapsGrid({ roadmaps, isSearching = false }: MyRoadmapsGridProps) {
  const router = useRouter();
  const [, setBreadcrumbPath] = useAtom(breadcrumbPathAtom);
  const [items, setItems] = useAtom(myRoadmapItemsAtom);

  const handleCardClick = (roadmap: RoadmapData) => {
    if (roadmap.type === 'Directory') {
      // Enter directory by updating breadcrumb
      setBreadcrumbPath((prev) => [...prev, { id: roadmap.id, name: roadmap.title }]);
    } else {
      // Navigate to editor for roadmaps
      router.push(`/editor/${roadmap.id}`);
    }
  };

  const handleRename = (id: string) => {
    const newName = window.prompt(MY_ROADMAPS_MESSAGES.RENAME_PROMPT);
    if (!newName || !newName.trim()) return;

    setItems(items.map((item) => (item.id === id ? { ...item, title: newName.trim() } : item)));
  };

  const handleDelete = (id: string) => {
    const confirmed = window.confirm(MY_ROADMAPS_MESSAGES.DELETE_CONFIRM);
    if (!confirmed) return;

    setItems(items.filter((item) => item.id !== id));
  };

  const handleFavorite = (id: string) => {
    setItems(
      items.map((item) => (item.id === id ? { ...item, isFavorite: !item.isFavorite } : item)),
    );
  };

  if (roadmaps.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-center">
        <p className="text-base font-semibold text-[#020617]">
          {isSearching ? MY_ROADMAPS_MESSAGES.EMPTY_SEARCH : MY_ROADMAPS_MESSAGES.EMPTY_LIST}
        </p>
        <p className="mt-1 text-sm text-[#64748b]">
          {isSearching
            ? MY_ROADMAPS_MESSAGES.EMPTY_SEARCH_DESCRIPTION
            : MY_ROADMAPS_MESSAGES.EMPTY_LIST_DESCRIPTION}
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-x-14 gap-y-14 md:grid-cols-2 lg:grid-cols-3">
      {roadmaps.map((roadmap) => (
        <RoadmapCard
          key={roadmap.id}
          title={roadmap.title}
          type={roadmap.type}
          author={roadmap.author}
          fileCount={roadmap.fileCount}
          imageUrl={roadmap.imageUrl}
          isFavorite={roadmap.isFavorite}
          onClick={() => handleCardClick(roadmap)}
          onRename={() => handleRename(roadmap.id)}
          onDelete={() => handleDelete(roadmap.id)}
          onFavorite={() => handleFavorite(roadmap.id)}
        />
      ))}
    </div>
  );
}
