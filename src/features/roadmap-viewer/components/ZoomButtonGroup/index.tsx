'use client';

import { Minus, Plus, RotateCcw } from 'lucide-react';

interface ZoomButtonGroupProps {
  value: number;
  onZoomIn: () => void;
  onZoomOut: () => void;
  onZoomReset: () => void;
}

export function ZoomButtonGroup({ value, onZoomIn, onZoomOut, onZoomReset }: ZoomButtonGroupProps) {
  return (
    <div className="absolute right-8 bottom-8 z-30 flex flex-col overflow-hidden rounded-md border border-white/15 bg-[#020617] shadow-sm">
      <button
        type="button"
        onClick={onZoomIn}
        className="flex h-9 w-9 items-center justify-center border-b border-white/15 text-sm font-semibold text-slate-200 hover:bg-white/10"
        aria-label="확대"
      >
        <Plus className="h-4 w-4" />
      </button>
      <button
        type="button"
        onClick={onZoomOut}
        className="flex h-9 w-9 items-center justify-center border-b border-white/15 text-sm font-semibold text-slate-200 hover:bg-white/10"
        aria-label="축소"
      >
        <Minus className="h-4 w-4" />
      </button>
      <button
        type="button"
        onClick={onZoomReset}
        className="flex h-9 w-16 items-center justify-center gap-1 bg-white/10 text-[11px] font-semibold text-slate-200 hover:bg-white/20"
        aria-label="확대 초기화"
      >
        <RotateCcw className="h-3.5 w-3.5" />
        {value}%
      </button>
    </div>
  );
}
