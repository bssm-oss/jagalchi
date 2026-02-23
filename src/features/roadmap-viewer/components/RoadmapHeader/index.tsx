import { Menu, Share2, FileText, MoreVertical } from 'lucide-react';

interface RoadmapHeaderProps {
  roadmapTitle: string;
  roadmapMeta?: string;
  className?: string;
  onExport?: () => void;
  onShare?: () => void;
  onInfo?: () => void;
}

export function RoadmapHeader({
  roadmapTitle,
  roadmapMeta = 'Draft • 공개 상태',
  className,
  onExport,
  onShare,
  onInfo,
}: RoadmapHeaderProps) {
  return (
    <header
      className={`sticky top-0 z-30 border-b border-white/10 bg-[#020617] px-6 py-3 shadow-[0_1px_0_rgba(15,23,42,0.45)] ${
        className ?? ''
      }`}
    >
      <div className="mx-auto flex min-h-16 w-full max-w-[2011px] items-center gap-3">
        <button
          type="button"
          className="inline-flex h-8 w-8 items-center justify-center rounded-md border border-white/15 text-slate-100 hover:bg-white/10"
          aria-label="메뉴"
        >
          <Menu className="h-5 w-5" />
        </button>

        <div className="min-w-0">
          <h1 className="truncate text-[18px] font-semibold tracking-tight text-white">
            {roadmapTitle}
          </h1>
          <p className="truncate text-[12px] text-slate-300">{roadmapMeta}</p>
        </div>

        <div className="ml-auto flex items-center gap-2">
          <button
            type="button"
            onClick={onInfo}
            className="inline-flex h-8 items-center gap-1 rounded-md border border-white/15 bg-white/5 px-2.5 text-[12px] text-slate-200 hover:bg-white/10"
          >
            <FileText className="h-3.5 w-3.5" />
            상세 보기
          </button>
          <button
            type="button"
            onClick={onShare}
            className="inline-flex h-8 items-center gap-1 rounded-md border border-white/15 bg-white/5 px-2.5 text-[12px] text-slate-200 hover:bg-white/10"
          >
            <Share2 className="h-3.5 w-3.5" />
            공유
          </button>
          <button
            type="button"
            onClick={onExport}
            className="inline-flex h-8 items-center gap-1 rounded-md border border-white/15 bg-white/5 px-2.5 text-[12px] text-slate-200 hover:bg-white/10"
          >
            <MoreVertical className="h-3.5 w-3.5" />
            옵션
          </button>
        </div>
      </div>
    </header>
  );
}
