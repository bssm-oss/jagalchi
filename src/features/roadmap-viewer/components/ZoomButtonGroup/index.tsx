'use client';

import { Maximize, Minus, Plus } from 'lucide-react';

interface ZoomButtonGroupProps {
  zoom: number;
  onZoomIn: () => void;
  onZoomOut: () => void;
  onFitView: () => void;
}

export function ZoomButtonGroup({ zoom, onZoomIn, onZoomOut, onFitView }: ZoomButtonGroupProps) {
  return (
    <div className="flex h-9 items-center overflow-hidden rounded-lg border border-[#e2e8f0] bg-white">
      {/* Fullscreen/FitView */}
      <button
        type="button"
        onClick={onFitView}
        className="flex h-full w-9 items-center justify-center border-r border-[#e2e8f0] hover:bg-slate-50"
        aria-label="화면 맞춤"
      >
        <Maximize className="h-4 w-4 text-[#020617]" />
      </button>

      {/* Zoom percentage */}
      <span className="flex h-full min-w-[52px] items-center justify-center border-r border-[#e2e8f0] px-2 text-sm font-semibold text-[#020617]">
        {Math.round(zoom * 100)}%
      </span>

      {/* +/- stacked */}
      <div className="flex h-full flex-col">
        <button
          type="button"
          onClick={onZoomIn}
          className="flex h-1/2 w-9 items-center justify-center border-b border-[#e2e8f0] hover:bg-slate-50"
          aria-label="확대"
        >
          <Plus className="h-3 w-3 text-[#020617]" />
        </button>
        <button
          type="button"
          onClick={onZoomOut}
          className="flex h-1/2 w-9 items-center justify-center hover:bg-slate-50"
          aria-label="축소"
        >
          <Minus className="h-3 w-3 text-[#020617]" />
        </button>
      </div>
    </div>
  );
}
