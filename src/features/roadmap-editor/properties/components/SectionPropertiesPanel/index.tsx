'use client';

import { memo } from 'react';

import { EDITOR_MESSAGES } from '@/constants/messages';

import { NODE_PRESET_COLORS } from '../../../constants/preset-colors';
import { useUpdateNode } from '../../../hooks/use-update-node';
import { ColorSelector } from '../ColorSelector';
import { EditorInput } from '../EditorInput';
import { PanelHeader } from '../PanelHeader';

import type { JagalchiSectionType, NodeColorVariant } from '../../../types/editor.types';

interface SectionPropertiesPanelProps {
  node: JagalchiSectionType;
}

/**
 * Section 선택 시 표시되는 속성 패널
 *
 * Figma 디자인 기반 구조:
 * - Header: "Section_1" + Lock 버튼
 * - 섹션 이름: EditorInput
 * - 기본 컬러: ColorSelector
 */
export const SectionPropertiesPanel = memo(function SectionPropertiesPanel({
  node,
}: SectionPropertiesPanelProps) {
  const { updateNode } = useUpdateNode(node.id);

  const toggleLock = () => {
    updateNode({ isLocked: !node.data.isLocked });
  };

  return (
    <div className="flex h-full w-full flex-col">
      <PanelHeader
        title={node.id}
        subtitle="섹션"
        isLocked={node.data.isLocked}
        onToggleLock={toggleLock}
      />

      {/* Content */}
      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        {/* 섹션 이름 */}
        <EditorInput
          label={EDITOR_MESSAGES.SIDEBAR_SECTION_NAME_LABEL}
          value={node.data.title}
          onChange={(value) => updateNode({ title: value })}
          placeholder="섹션 이름을 입력하세요"
          isDisabled={node.data.isLocked}
        />
      </div>

      {/* 크기 */}
      <div className="border-b border-slate-200 p-4">
        <div className="space-y-2">
          <label className="text-sm font-medium text-slate-900">크기</label>
          <div className="flex items-center gap-4">
            <div className="flex flex-1 items-center gap-2">
              <p className="text-sm text-black">W</p>
              <EditorInput value="" onChange={() => {}} placeholder="Value" isDisabled />
            </div>
            <div className="flex flex-1 items-center gap-2">
              <p className="text-sm text-black">H</p>
              <EditorInput value="" onChange={() => {}} placeholder="Value" isDisabled />
            </div>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 space-y-0 overflow-y-auto p-4">
        {/* 기본 컬러 */}
        <ColorSelector
          type="node"
          nodeId={node.id}
          currentVariant={node.data.variant}
          presets={NODE_PRESET_COLORS}
          onPresetSelect={(variant) => updateNode({ variant: variant as NodeColorVariant })}
        />
      </div>
    </div>
  );
});
