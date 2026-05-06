'use client';

import { ReactFlowProvider } from '@xyflow/react';
import { useAtomValue } from 'jotai';

import { currentUserIdAtom, currentUserNameAtom } from '@/lib/auth-atoms';

import { RoadmapCanvas } from '../../../canvas/components';
import { useNackRollback } from '../../../hooks/use-nack-rollback';
import { useRealtimeSync } from '../../../hooks/use-realtime-sync';
import { ColorPicker } from '../../../properties/components';
import { EditorSidebar } from '../../../sidebar/components';
import { EditorToolbar } from '../../../toolbar/components';
import { EditorHeader } from '../EditorHeader';

interface EditorContentProps {
  onBack?: () => void;
  roadmapId?: string;
}

function EditorContent({ onBack, roadmapId }: EditorContentProps) {
  const userId = useAtomValue(currentUserIdAtom);
  const userName = useAtomValue(currentUserNameAtom);

  const { isConnected } = useRealtimeSync({
    roadmapId: roadmapId ?? '',
    isEnabled: !!roadmapId,
  });

  useNackRollback();

  return (
    <div className="relative flex h-screen w-screen">
      <EditorHeader onBack={onBack} isConnected={isConnected} />

      <div className="relative flex flex-1 overflow-hidden">
        <div className="flex-1">
          <RoadmapCanvas
            roadmapId={roadmapId}
            userId={userId ?? undefined}
            userName={userName ?? undefined}
          />
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
  roadmapId?: string;
}

export function RoadmapEditor({ onBack, roadmapId }: RoadmapEditorProps) {
  return (
    <ReactFlowProvider>
      <EditorContent onBack={onBack} roadmapId={roadmapId} />
    </ReactFlowProvider>
  );
}
