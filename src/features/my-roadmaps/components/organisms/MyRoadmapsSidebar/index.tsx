import { useState } from 'react';

import { useAtom } from 'jotai';
import {
  BookOpen,
  ChevronDown,
  ChevronRight,
  Clock,
  Files,
  Folder,
  LogOut,
  Search,
  Star,
  Users,
} from 'lucide-react';

import type { DirectoryTreeNode } from '@/api/roadmap';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import { AUTH_MESSAGES, MY_ROADMAPS_MESSAGES } from '@/constants/messages';
import { cn } from '@/lib/utils';

import { useDirectoryTree } from '../../../hooks/use-directory-tree';
import { type SidebarCategory, sidebarCategoryAtom } from '../../../stores/my-roadmaps.atoms';

import type { LucideIcon } from 'lucide-react';

interface SidebarItem {
  icon: LucideIcon;
  label: string;
  id: SidebarCategory;
}

const SIDEBAR_GROUPS: SidebarItem[][] = [
  [
    { icon: Clock, label: MY_ROADMAPS_MESSAGES.SIDEBAR_RECENT, id: 'recent' },
    { icon: BookOpen, label: MY_ROADMAPS_MESSAGES.SIDEBAR_COMMUNITY, id: 'community' },
  ],
  [
    { icon: Files, label: MY_ROADMAPS_MESSAGES.SIDEBAR_MY_ROADMAP, id: 'my-roadmap' },
    { icon: Users, label: MY_ROADMAPS_MESSAGES.SIDEBAR_SHARED, id: 'shared' },
  ],
  [{ icon: Star, label: MY_ROADMAPS_MESSAGES.SIDEBAR_FAVORITES, id: 'favorites' }],
];

function DirectoryNode({ node, depth }: { node: DirectoryTreeNode; depth: number }) {
  const [isOpen, setIsOpen] = useState(false);
  const hasChildren = node.children && node.children.length > 0;

  return (
    <div>
      <button
        type="button"
        onClick={() => hasChildren && setIsOpen((prev) => !prev)}
        className="flex h-7 w-full items-center gap-1.5 rounded-md px-3 text-sm text-slate-700 transition-colors hover:bg-black/5"
        style={{ paddingLeft: `${12 + depth * 16}px` }}
      >
        {hasChildren ? (
          <ChevronRight className={cn('h-3.5 w-3.5 transition-transform', isOpen && 'rotate-90')} />
        ) : (
          <span className="w-3.5" />
        )}
        <Folder className="h-4 w-4 text-slate-500" />
        <span className="truncate">{node.name}</span>
      </button>
      {isOpen &&
        hasChildren &&
        node.children.map((child) => (
          <DirectoryNode key={child.id} node={child} depth={depth + 1} />
        ))}
    </div>
  );
}

interface MyRoadmapsSidebarProps {
  className?: string;
  userName?: string;
  userEmail?: string;
  onLogout?: () => void;
}

export function MyRoadmapsSidebar({
  className,
  userName = 'UserName',
  userEmail = 'user@example.com',
  onLogout,
}: MyRoadmapsSidebarProps) {
  const [activeCategory, setActiveCategory] = useAtom(sidebarCategoryAtom);
  const { data: directoryTree } = useDirectoryTree();

  return (
    <div
      className={cn(
        'flex min-h-screen w-[240px] flex-col border-r border-slate-200 bg-slate-100 p-4',
        className,
      )}
    >
      {/* Profile Section */}
      <div className="mb-4 flex items-center gap-3 rounded-md px-3 py-2">
        <Avatar className="h-8 w-8">
          <AvatarImage src="/placeholder-avatar.png" />
          <AvatarFallback>U</AvatarFallback>
        </Avatar>
        <div className="flex min-w-0 flex-1 flex-col">
          <span className="truncate text-sm font-normal text-slate-950">{userName}</span>
          <span className="truncate text-xs leading-4 text-slate-500">{userEmail}</span>
        </div>
        <ChevronDown className="h-5 w-5 text-slate-500" />
      </div>

      {/* Search */}
      <div className="mb-4">
        <div className="relative">
          <Search className="absolute top-1/2 left-3 h-5 w-5 -translate-y-1/2 text-slate-500" />
          <Input
            placeholder="Search"
            className="h-9 border border-slate-200 bg-white pl-10 text-sm shadow-xs"
          />
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex flex-col">
        {SIDEBAR_GROUPS.map((group, groupIndex) => (
          <div key={groupIndex}>
            {groupIndex > 0 && <Separator className="my-2" />}
            {group.map((item) => (
              <button
                key={item.id}
                type="button"
                onClick={() => setActiveCategory(item.id)}
                className={cn(
                  'flex h-8 w-full items-center gap-2 rounded-md px-3 py-1 text-sm transition-colors',
                  activeCategory === item.id
                    ? 'bg-slate-200 text-slate-700'
                    : 'text-slate-700 hover:bg-black/5',
                )}
              >
                <item.icon className="h-5 w-5" />
                {item.label}
              </button>
            ))}
          </div>
        ))}
      </nav>

      {/* Directory Tree */}
      {directoryTree && directoryTree.length > 0 && (
        <>
          <Separator className="my-2" />
          <div className="flex flex-col gap-0.5">
            {directoryTree.map((dir) => (
              <DirectoryNode key={dir.id} node={dir} depth={0} />
            ))}
          </div>
        </>
      )}

      {/* Logout */}
      {onLogout && (
        <>
          <Separator className="my-2 mt-auto" />
          <button
            type="button"
            onClick={onLogout}
            className="flex h-8 w-full items-center gap-2 rounded-md px-3 py-1 text-sm text-slate-700 transition-colors hover:bg-black/5"
          >
            <LogOut className="h-5 w-5" />
            {AUTH_MESSAGES.LOGOUT}
          </button>
        </>
      )}
    </div>
  );
}
