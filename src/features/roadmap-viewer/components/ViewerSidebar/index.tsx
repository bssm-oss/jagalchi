import type { ReactElement } from 'react';

import { ChevronDown, ChevronRight, Layers, Search, SlidersHorizontal, X } from 'lucide-react';

interface ViewerSidebarItem {
  level: number;
  label: string;
  children?: ViewerSidebarItem[];
}

const ITEMS: ViewerSidebarItem[] = [
  {
    level: 0,
    label: '전체 트리',
    children: [
      { level: 1, label: 'Frontend Roadmap' },
      { level: 1, label: 'Backend Roadmap' },
      { level: 1, label: 'DevOps Foundation' },
    ],
  },
  {
    level: 0,
    label: '노드 타입',
    children: [
      { level: 1, label: '학습 노드 (9)' },
      { level: 1, label: '실전 프로젝트 (5)' },
      { level: 1, label: '참고 링크 (4)' },
    ],
  },
  {
    level: 0,
    label: '메모',
    children: [
      { level: 1, label: '폴더: 주차별' },
      { level: 1, label: '우선순위 메모' },
    ],
  },
];

const renderNode = (item: ViewerSidebarItem): ReactElement => {
  const hasChildren = Boolean(item.children?.length);

  return (
    <li key={`${item.label}-${item.level}`}>
      <button
        type="button"
        className={`${
          item.level === 0 ? 'font-semibold text-slate-200' : 'font-medium text-slate-300'
        } flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-left text-[12px] hover:text-white`}
      >
        {hasChildren ? (
          item.level === 0 ? (
            <ChevronDown className="h-3.5 w-3.5 text-slate-500" />
          ) : (
            <ChevronRight className="h-3.5 w-3.5 text-slate-400" />
          )
        ) : (
          item.level > 0 && <Layers className="h-3.5 w-3.5 text-slate-400" />
        )}
        <span className="truncate text-[12px] text-slate-200">{item.label}</span>
      </button>
      {hasChildren ? (
        <ul className="ml-4 border-l border-white/15 pl-2">
          {item.children?.map((child) => renderNode(child))}
        </ul>
      ) : null}
    </li>
  );
};

export function ViewerSidebar() {
  return (
    <aside className="flex h-full w-[320px] flex-col border-l border-white/10 bg-[#020617] text-slate-100">
      <div className="border-b border-white/10 bg-black/20 px-3 py-3">
        <div className="mb-3 flex items-center justify-between">
          <p className="text-xs font-semibold text-slate-200">레이어 패널</p>
          <button type="button" className="rounded p-1 text-slate-500 hover:bg-slate-100">
            <X className="h-3.5 w-3.5" />
          </button>
        </div>

        <div className="relative">
          <Search className="pointer-events-none absolute top-1/2 left-2 h-3.5 w-3.5 -translate-y-1/2 text-slate-500" />
          <input
            type="text"
            placeholder="레이어 검색"
            className="h-7 w-full rounded-md border border-white/15 bg-[#111827] pr-2 pl-7 text-[12px] text-slate-100 outline-none placeholder:text-slate-500"
          />
        </div>
      </div>

      <div className="border-b border-white/10 px-3 py-2">
        <button
          type="button"
          className="inline-flex h-7 items-center gap-1 rounded-md border border-white/15 px-2 text-[11px] text-slate-300"
        >
          <SlidersHorizontal className="h-3.5 w-3.5" />
          필터
        </button>
      </div>

      <ul className="scrollbar-thin flex-1 space-y-1 overflow-y-auto p-3 text-left">
        {ITEMS.map((item) => renderNode(item))}
      </ul>

      <div className="border-t border-white/10 px-3 py-2 text-[11px] text-slate-400">
        총 18개 항목
      </div>
    </aside>
  );
}
