'use client';

import { useRouter } from 'next/navigation';

import { ArrowLeft, ChevronDown, Search, Sparkles } from 'lucide-react';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

interface RoadmapHeaderProps {
  roadmapTitle?: string;
  onAiFeedback?: () => void;
}

export function RoadmapHeader({
  roadmapTitle = "Jagalchi's Roadmap",
  onAiFeedback,
}: RoadmapHeaderProps) {
  const router = useRouter();

  return (
    <header className="flex h-12 w-full items-center justify-between bg-white px-5">
      {/* Left: Back + Title dropdown */}
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => router.back()}
          className="flex items-center justify-center rounded-lg p-2 hover:bg-slate-100"
          aria-label="뒤로가기"
        >
          <ArrowLeft className="h-4 w-4 text-[#020617]" />
        </button>
        <button
          type="button"
          className="flex items-center gap-1 text-sm font-semibold text-[#020617] hover:text-slate-600"
        >
          {roadmapTitle}
          <ChevronDown className="h-3 w-3" />
        </button>
      </div>

      {/* Center: Avatar */}
      <Avatar className="h-8 w-8">
        <AvatarImage src="" alt="User" />
        <AvatarFallback>U</AvatarFallback>
      </Avatar>

      {/* Right: Search + AI button */}
      <div className="flex items-center gap-3">
        <div className="relative">
          <Search className="absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-[#64748b]" />
          <Input
            type="text"
            placeholder="로드맵 안에서 검색"
            className="h-9 w-[200px] rounded-lg border-[#e2e8f0] bg-white pl-9 text-sm"
          />
        </div>
        <Button
          onClick={onAiFeedback}
          className="h-9 rounded-lg bg-[#0f172a] px-4 text-sm font-semibold text-white hover:bg-[#1e293b]"
        >
          <Sparkles className="mr-1.5 h-4 w-4" />
          AI 학습 피드백
        </Button>
      </div>
    </header>
  );
}
