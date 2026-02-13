'use client';

import { ReactFlowProvider } from '@xyflow/react';

import { RoadmapCanvas } from '../../../canvas/components';
import { ColorPicker } from '../../molecules/ColorPicker';
import { EditorHeader } from '../../organisms/EditorHeader';
import { EditorSidebar } from '../../organisms/EditorSidebar';
import { EditorToolbar } from '../../organisms/EditorToolbar';

interface EditorContentProps {
  onBack?: () => void;
}

function EditorContent({ onBack }: EditorContentProps) {
  return (
    <div className="relative flex h-screen w-screen">
      <EditorHeader onBack={onBack} />

      <div className="relative flex flex-1 overflow-hidden">
        <div className="flex-1">
          <RoadmapCanvas />
        </div>
        <EditorSidebar />
      </div>

      <EditorToolbar />
      <ColorPicker />
    </div>
  );
}

interface RoadmapEditorProps {
  onBack?: () => void;
}

export function RoadmapEditor({ onBack }: RoadmapEditorProps) {
  return (
    <ReactFlowProvider>
      <EditorContent onBack={onBack} />
    </ReactFlowProvider>
  );
}
