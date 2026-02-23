'use client';

import { ArrowRight, CalendarDays, ChevronDown, Plus } from 'lucide-react';

import { HeaderExportMenu } from '../HeaderExportMenu';
import { HeaderMenu } from '../HeaderMenu';
import { HeaderSaveAsImageMenu } from '../HeaderSaveAsImageMenu';
import { RoadmapHeader } from '../RoadmapHeader';
import { ViewerSidebar } from '../ViewerSidebar';
import { ZoomButtonGroup } from '../ZoomButtonGroup';

interface RoadmapNode {
  title: string;
  summary: string;
  x: number;
  y: number;
}

type ViewerLayout = 'page' | 'cards';

interface RoadmapViewerProps {
  roadmapId: string;
  mode?: ViewerLayout;
}

const NODES: RoadmapNode[] = [
  { title: '시작 단계', summary: '요구사항 정리', x: 180, y: 140 },
  { title: '기초 학습', summary: '웹 기초, HTML/CSS/JS 정복', x: 540, y: 320 },
  { title: '프로젝트 기획', summary: '기획서 작성, API 분석', x: 940, y: 520 },
  { title: '실전 구현', summary: '핵심 화면 및 테스트', x: 460, y: 760 },
  { title: '배포', summary: 'CI/CD와 운영 반영', x: 980, y: 980 },
];

const CONNECTIONS: Array<[number, number]> = [
  [0, 1],
  [1, 2],
  [2, 3],
  [3, 4],
  [0, 3],
];

function NodeCard({ node, index }: { node: RoadmapNode; index: number }) {
  return (
    <article
      className="absolute w-[250px] rounded-lg border border-white/15 bg-[#0f172a] p-4 shadow-sm"
      style={{ left: node.x, top: node.y }}
    >
      <p className="text-[11px] font-semibold text-slate-400">Phase {index + 1}</p>
      <h3 className="mt-1 text-[16px] font-semibold text-slate-100">{node.title}</h3>
      <p className="mt-1.5 text-[12px] text-slate-300">{node.summary}</p>
      <button
        type="button"
        className="mt-3 inline-flex items-center gap-1.5 rounded-md border border-white/15 bg-white/5 px-2.5 py-1 text-[11px] text-slate-200 hover:bg-white/10"
      >
        상세 보기
        <ArrowRight className="h-3 w-3" />
      </button>
    </article>
  );
}

function CardListMode() {
  return (
    <div className="grid gap-4 p-6">
      {NODES.map((node, index) => (
        <article
          key={node.title}
          className="flex items-center gap-4 rounded-lg border border-white/15 bg-[#111827] p-4"
        >
          <div className="flex h-10 w-10 items-center justify-center rounded-md border border-white/15 bg-[#1f2937] text-sm font-bold text-slate-300">
            {index + 1}
          </div>
          <div className="min-w-0 flex-1">
            <h3 className="text-sm font-semibold text-slate-100">{node.title}</h3>
            <p className="text-[12px] text-slate-300">{node.summary}</p>
          </div>
          <button
            type="button"
            className="inline-flex h-8 items-center rounded-md border border-white/15 bg-white/5 px-3 text-xs text-slate-200 hover:bg-white/10"
          >
            보기
          </button>
        </article>
      ))}
      <div className="py-4 text-center text-xs text-slate-400">
        + 더 많은 노드가 100% 연결 모드에서 표시됩니다.
      </div>
    </div>
  );
}

function CanvasMode() {
  return (
    <section className="relative h-[1320px] overflow-hidden rounded-xl border border-white/15 bg-[#020617]">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_16%_20%,rgba(59,130,246,0.08),transparent_40%),radial-gradient(circle_at_74%_16%,rgba(16,185,129,0.06),transparent_30%),radial-gradient(circle_at_56%_84%,rgba(249,115,22,0.05),transparent_30%)]" />
      <div className="absolute top-4 left-5 z-10 flex items-center gap-2">
        <HeaderMenu />
        <HeaderExportMenu />
        <HeaderSaveAsImageMenu />
      </div>

      <div className="relative h-full px-20 py-16">
        <svg className="pointer-events-none absolute inset-0 h-full w-full" aria-hidden="true">
          {CONNECTIONS.map(([from, to], idx) => (
            <line
              key={`${from}-${to}-${idx}`}
              x1={NODES[from].x + 125}
              y1={NODES[from].y + 90}
              x2={NODES[to].x + 125}
              y2={NODES[to].y + 4}
              stroke="#94a3b8"
              strokeWidth={2}
              strokeLinecap="round"
            />
          ))}
        </svg>

        {NODES.map((node, index) => (
          <NodeCard node={node} index={index} key={node.title} />
        ))}

        <button
          type="button"
          className="absolute top-[1120px] left-[760px] inline-flex items-center gap-2 rounded-full border border-dashed border-slate-500 bg-slate-700 px-3 py-2 text-[11px] text-slate-200"
        >
          <Plus className="h-3.5 w-3.5" />
          다음 단계 추가
        </button>
      </div>
    </section>
  );
}

export function RoadmapViewer({ roadmapId, mode = 'page' }: RoadmapViewerProps) {
  return (
    <div className="min-h-screen bg-[#020617]">
      <RoadmapHeader roadmapTitle={`Roadmap Viewer · ${roadmapId}`} roadmapMeta="Desktop viewer" />
      <div className="mx-auto flex w-full max-w-[2011px] gap-4 px-4 py-4">
        <div className="relative flex-1">
          <div className="rounded-xl border border-white/10 bg-[#111827]">
            <div className="flex border-b border-white/10 px-4 py-3">
              <CalendarDays className="mr-2 h-4 w-4 text-slate-500" />
              <span className="text-xs font-medium text-slate-300">마지막 수정: 2026-02-17</span>
              <button
                type="button"
                className="ml-auto inline-flex h-8 items-center gap-2 rounded-md border border-white/15 bg-white/5 px-3 text-xs text-slate-200 hover:bg-white/10"
              >
                <ChevronDown className="h-3.5 w-3.5" />뷰 설정
              </button>
            </div>
            {mode === 'cards' ? <CardListMode /> : <CanvasMode />}
          </div>
        </div>

        <ViewerSidebar />
      </div>

      <ZoomButtonGroup
        value={100}
        onZoomIn={() => {}}
        onZoomOut={() => {}}
        onZoomReset={() => {}}
      />
    </div>
  );
}
