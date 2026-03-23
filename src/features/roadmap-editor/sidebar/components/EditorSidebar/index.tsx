'use client';

import { memo, useState } from 'react';

import { useAtomValue } from 'jotai';
import { ChevronsLeft, ChevronsRight } from 'lucide-react';

import { EDITOR_MESSAGES } from '@/constants/messages';

import {
  EdgePropertiesPanel,
  NodePropertiesPanel,
  SectionPropertiesPanel,
  TextPropertiesPanel,
} from '../../../properties/components';
import {
  singleSelectedNodeAtom,
  singleSelectedEdgeAtom,
  selectedNodeIdsAtom,
} from '../../../stores/editor-atoms';
import { MultiSelectPanel } from '../MultiSelectPanel';

import type {
  JagalchiNodeType,
  JagalchiSectionType,
  JagalchiTextType,
} from '../../../types/editor.types';

export const EditorSidebar = memo(function EditorSidebar() {
  const selectedNode = useAtomValue(singleSelectedNodeAtom);
  const selectedEdge = useAtomValue(singleSelectedEdgeAtom);
  const selectedNodeIds = useAtomValue(selectedNodeIdsAtom);
  const [isCollapsed, setIsCollapsed] = useState(false);

  const collapseButton = (
    <button
      className="flex h-8 w-8 items-center justify-center rounded-bl-lg border-b border-l border-[#e2e8f0] bg-white"
      onClick={() => setIsCollapsed((prev) => !prev)}
      aria-label={isCollapsed ? '사이드바 열기' : '사이드바 닫기'}
    >
      {isCollapsed ? <ChevronsLeft className="h-4 w-4" /> : <ChevronsRight className="h-4 w-4" />}
    </button>
  );

  if (isCollapsed) {
    return (
      <div className="relative flex h-full flex-col">
        <div className="absolute top-0 right-0">{collapseButton}</div>
      </div>
    );
  }

  // Multi-select (2개 이상 노드 선택)
  if (selectedNodeIds.length >= 2) {
    return (
      <aside className="relative h-full w-[240px] border-l bg-white shadow-md">
        <div className="absolute top-0 left-0">{collapseButton}</div>
        <MultiSelectPanel />
      </aside>
    );
  }

  // Edge가 선택된 경우
  if (selectedEdge) {
    return (
      <aside className="relative h-full w-[240px] border-l bg-white shadow-md">
        <div className="absolute top-0 left-0">{collapseButton}</div>
        <EdgePropertiesPanel edge={selectedEdge} />
      </aside>
    );
  }

  // Node가 선택된 경우
  if (selectedNode) {
    if (selectedNode.type === 'jagalchi-node') {
      return (
        <aside className="relative h-full w-[240px] border-l bg-white shadow-md">
          <div className="absolute top-0 left-0">{collapseButton}</div>
          <NodePropertiesPanel node={selectedNode as JagalchiNodeType} />
        </aside>
      );
    }

    if (selectedNode.type === 'jagalchi-section') {
      return (
        <aside className="relative h-full w-[240px] border-l bg-white shadow-md">
          <div className="absolute top-0 left-0">{collapseButton}</div>
          <SectionPropertiesPanel node={selectedNode as JagalchiSectionType} />
        </aside>
      );
    }

    if (selectedNode.type === 'jagalchi-text') {
      return (
        <aside className="relative h-full w-[240px] border-l bg-white shadow-md">
          <div className="absolute top-0 left-0">{collapseButton}</div>
          <TextPropertiesPanel node={selectedNode as JagalchiTextType} />
        </aside>
      );
    }
  }

  // 아무것도 선택되지 않은 경우
  return (
    <aside className="relative flex h-full w-[240px] items-center justify-center border-l bg-white">
      <div className="absolute top-0 left-0">{collapseButton}</div>
      <p className="text-muted-foreground text-sm">{EDITOR_MESSAGES.SIDEBAR_EMPTY_STATE}</p>
    </aside>
  );
});
