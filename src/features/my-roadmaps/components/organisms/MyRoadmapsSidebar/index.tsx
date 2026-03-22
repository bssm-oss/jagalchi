import { useAtom } from 'jotai';
import { BookOpen, ChevronDown, Clock, Files, Search, Star, Users } from 'lucide-react';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

import { type SidebarCategory, sidebarCategoryAtom } from '../../../stores/my-roadmaps.atoms';

import type { LucideIcon } from 'lucide-react';

interface SidebarItem {
  icon: LucideIcon;
  label: string;
  id: SidebarCategory;
}

const SIDEBAR_GROUPS: SidebarItem[][] = [
  [
    { icon: Clock, label: '최근', id: 'recent' },
    { icon: BookOpen, label: '커뮤니티', id: 'community' },
  ],
  [
    { icon: Files, label: '내 로드맵', id: 'my-roadmap' },
    { icon: Users, label: '공유된 로드맵', id: 'shared' },
  ],
  [{ icon: Star, label: '즐겨찾기', id: 'favorites' }],
];

interface MyRoadmapsSidebarProps {
  className?: string;
  userName?: string;
  userEmail?: string;
}

export function MyRoadmapsSidebar({
  className,
  userName = 'UserName',
  userEmail = 'user@example.com',
}: MyRoadmapsSidebarProps) {
  const [activeCategory, setActiveCategory] = useAtom(sidebarCategoryAtom);

  return (
    <div
      className={cn(
        'flex min-h-screen w-[240px] flex-col border-r border-[#e2e8f0] bg-[#f1f5f9] p-4',
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
          <span className="truncate text-sm font-normal text-[#020617]">{userName}</span>
          <span className="truncate text-xs leading-4 text-[#64748b]">{userEmail}</span>
        </div>
        <ChevronDown className="h-5 w-5 text-[#64748b]" />
      </div>

      {/* Search */}
      <div className="mb-4">
        <div className="relative">
          <Search className="absolute top-1/2 left-3 h-5 w-5 -translate-y-1/2 text-[#64748b]" />
          <Input
            placeholder="Search"
            className="h-9 border border-[#e2e8f0] bg-white pl-10 text-sm shadow-xs"
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
                    ? 'bg-[#e2e8f0] text-[#334155]'
                    : 'text-[#334155] hover:bg-black/5',
                )}
              >
                <item.icon className="h-5 w-5" />
                {item.label}
              </button>
            ))}
          </div>
        ))}
      </nav>
    </div>
  );
}
