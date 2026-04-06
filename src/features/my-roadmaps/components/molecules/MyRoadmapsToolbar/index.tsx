'use client';

import { useEffect, useRef, useState } from 'react';

import { useRouter } from 'next/navigation';

import { useAtom, useSetAtom } from 'jotai';
import { ChevronRight, ListFilter, Plus, Search } from 'lucide-react';
import { nanoid } from 'nanoid';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Input } from '@/components/ui/input';
import { MY_ROADMAPS_MESSAGES } from '@/constants/messages';
import { useClickOutside } from '@/hooks/use-click-outside';
import { cn } from '@/lib/utils';

import {
  breadcrumbPathAtom,
  myRoadmapItemsAtom,
  searchQueryAtom,
} from '../../../stores/my-roadmaps.atoms';
import { AddDirectoryModal } from '../AddDirectoryModal';
import { AddRoadmapModal } from '../AddRoadmapModal';
import { MyRoadmapsFilter } from '../MyRoadmapsFilter';

import type { RoadmapData } from '../../../types/my-roadmaps.types';

export function MyRoadmapsToolbar() {
  const router = useRouter();
  const setItems = useSetAtom(myRoadmapItemsAtom);
  const [breadcrumbPath, setBreadcrumbPath] = useAtom(breadcrumbPathAtom);
  const setSearchQuery = useSetAtom(searchQueryAtom);
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [isRoadmapModalOpen, setIsRoadmapModalOpen] = useState(false);
  const [isDirectoryModalOpen, setIsDirectoryModalOpen] = useState(false);
  const [localQuery, setLocalQuery] = useState('');
  const filterRef = useRef<HTMLDivElement>(null);

  // Debounce search input by 300ms before updating the shared atom
  useEffect(() => {
    const timer = setTimeout(() => {
      setSearchQuery(localQuery);
    }, 300);
    return () => clearTimeout(timer);
  }, [localQuery, setSearchQuery]);

  useClickOutside(filterRef, () => setIsFilterOpen(false));

  const handleAddRoadmap = (name: string, _locationId?: string | null) => {
    const id = nanoid();
    const newItem: RoadmapData = {
      id,
      title: name,
      type: 'Roadmap',
      updatedAt: new Date().toISOString(),
      category: 'my-roadmap',
    };
    setItems((prev) => [newItem, ...prev]);
    router.push(`/editor/${id}`);
  };

  const handleAddDirectory = (name: string) => {
    const id = nanoid();
    const newItem: RoadmapData = {
      id,
      title: name,
      type: 'Directory',
      fileCount: 0,
      updatedAt: new Date().toISOString(),
      category: 'my-roadmap',
    };
    setItems((prev) => [newItem, ...prev]);
  };

  return (
    <div className="flex w-full items-center justify-between py-6">
      {breadcrumbPath.length === 0 ? (
        <div className="flex h-9 items-center px-3">
          <span className="text-sm font-semibold">내 전체 로드맵</span>
        </div>
      ) : (
        <div className="flex h-9 items-center">
          <button
            type="button"
            className="text-foreground text-xl font-semibold transition-colors hover:opacity-70"
            onClick={() => setBreadcrumbPath([])}
          >
            내 전체 로드맵
          </button>
          {breadcrumbPath.map((segment, index) => {
            const isLast = index === breadcrumbPath.length - 1;
            return (
              <div key={segment.id} className="flex items-center">
                <ChevronRight className="text-muted-foreground mx-1 h-5 w-5" />
                {isLast ? (
                  <span className="text-base font-semibold text-blue-700">{segment.name}</span>
                ) : (
                  <button
                    type="button"
                    className="text-foreground text-base font-semibold transition-colors hover:opacity-70"
                    onClick={() => setBreadcrumbPath(breadcrumbPath.slice(0, index + 1))}
                  >
                    {segment.name}
                  </button>
                )}
              </div>
            );
          })}
        </div>
      )}
      <div className="flex items-center gap-[10px]">
        <div className="relative">
          <Search className="text-muted-foreground absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2" />
          <Input
            type="search"
            placeholder={MY_ROADMAPS_MESSAGES.SEARCH_PLACEHOLDER}
            className="border-border h-9 w-[240px] bg-white pl-9 text-xs"
            value={localQuery}
            onChange={(e) => setLocalQuery(e.target.value)}
          />
        </div>
        <div className="relative" ref={filterRef}>
          <Button
            variant="outline"
            size="icon"
            aria-label="Filter"
            className={cn(
              'border-border h-9 w-9 transition-colors',
              isFilterOpen && 'bg-slate-100',
            )}
            onClick={() => setIsFilterOpen(!isFilterOpen)}
          >
            <ListFilter className="text-muted-foreground h-4 w-4" />
          </Button>

          {isFilterOpen && <MyRoadmapsFilter />}
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button className="h-9 rounded-md bg-[#1d4ed8] px-4 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#1e40af]">
              New
              <Plus className="ml-1.5 h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            align="end"
            className="w-[140px] rounded-lg border-[#e2e8f0] bg-white p-1.5 shadow-lg"
          >
            <DropdownMenuItem
              className="flex cursor-pointer items-center rounded-lg px-3 py-2.5 transition-colors outline-none focus:bg-[#F3F4F6]"
              onClick={() => setIsRoadmapModalOpen(true)}
            >
              <span className="text-[13px] font-semibold text-[#020617]">로드맵</span>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem
              className="flex cursor-pointer items-center rounded-lg px-3 py-2.5 transition-colors outline-none focus:bg-[#F3F4F6]"
              onClick={() => setIsDirectoryModalOpen(true)}
            >
              <span className="text-[13px] font-semibold text-[#020617]">디렉토리</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <AddRoadmapModal
        isOpen={isRoadmapModalOpen}
        onClose={() => setIsRoadmapModalOpen(false)}
        onConfirm={handleAddRoadmap}
      />
      <AddDirectoryModal
        isOpen={isDirectoryModalOpen}
        onClose={() => setIsDirectoryModalOpen(false)}
        onConfirm={handleAddDirectory}
      />
    </div>
  );
}
