'use client';

import { useCallback } from 'react';

import { useReactFlow } from '@xyflow/react';
import { useAtomValue } from 'jotai';

import { viewerZoomLevelAtom } from '../../stores/viewer-atoms';
import { ZoomButtonGroup } from '../ZoomButtonGroup';

export function ViewerZoomControls() {
  const zoomLevel = useAtomValue(viewerZoomLevelAtom);
  const { zoomIn, zoomOut, fitView } = useReactFlow();

  const handleZoomIn = useCallback(() => {
    zoomIn();
  }, [zoomIn]);

  const handleZoomOut = useCallback(() => {
    zoomOut();
  }, [zoomOut]);

  const handleZoomReset = useCallback(() => {
    fitView();
  }, [fitView]);

  return (
    <ZoomButtonGroup
      value={Math.round(zoomLevel * 100)}
      onZoomIn={handleZoomIn}
      onZoomOut={handleZoomOut}
      onZoomReset={handleZoomReset}
    />
  );
}
