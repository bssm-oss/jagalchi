'use client';

import { memo } from 'react';

import { TEXT_PRESET_COLORS } from '../../../constants/preset-colors';
import { useUpdateNode } from '../../../hooks/use-update-node';
import { ColorSelector } from '../ColorSelector';
import { EditorInput } from '../EditorInput';
import { PanelHeader } from '../PanelHeader';

import type { JagalchiTextType, TextColorVariant } from '../../../types/editor.types';

interface TextPropertiesPanelProps {
  node: JagalchiTextType;
}

/**
 * Text 선택 시 표시되는 속성 패널
 *
 * Figma 디자인 기반 구조:
 * - Header: "Text_1" + Lock 버튼
 * - 텍스트 내용: EditorInput (multiline)
 * - 기본 컬러: ColorSelector
 */
export const TextPropertiesPanel = memo(function TextPropertiesPanel({
  node,
}: TextPropertiesPanelProps) {
  const { updateNode } = useUpdateNode(node.id);

  const toggleLock = () => {
    updateNode({ isLocked: !node.data.isLocked });
  };

  return (
    <div className="flex h-full w-full flex-col">
      <PanelHeader
        title={node.id}
        subtitle="텍스트"
        isLocked={node.data.isLocked}
        onToggleLock={toggleLock}
      />

      {/* Content */}
      <div className="flex-1 space-y-0 overflow-y-auto p-4">
        {/* 텍스트 크기 */}
        <div className="border-b border-[#e2e8f0] py-4">
          <div className="space-y-1.5">
            <label className="text-sm font-medium text-[#020617]">텍스트 크기</label>
            <div className="flex items-center gap-2">
              <EditorInput value="" onChange={() => {}} placeholder="Value" isDisabled />
              <p className="text-sm text-black">px</p>
            </div>
          </div>
        </div>

        {/* 기본 컬러 */}
        <div className="border-b border-[#e2e8f0] py-4">
          <ColorSelector
            type="text"
            nodeId={node.id}
            currentVariant={node.data.variant}
            presets={TEXT_PRESET_COLORS}
            onPresetSelect={(variant) => updateNode({ variant: variant as TextColorVariant })}
          />
        </div>
      </div>
    </div>
  );
});
