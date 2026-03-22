import React from 'react';

import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

vi.mock('@xyflow/react', () => ({
  ReactFlowProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
  useReactFlow: () => ({ fitView: vi.fn(), zoomIn: vi.fn(), zoomOut: vi.fn(), getZoom: () => 1 }),
  useNodesState: () => [[], vi.fn(), vi.fn()],
  useEdgesState: () => [[], vi.fn(), vi.fn()],
  ReactFlow: () => <div data-testid="react-flow" />,
  Background: () => null,
  Controls: () => null,
  MiniMap: () => null,
}));

import { RoadmapHeader } from './index';

describe('RoadmapHeader', () => {
  it('renders the roadmap title', () => {
    render(<RoadmapHeader roadmapTitle="My Roadmap" />);
    expect(screen.getByText('My Roadmap')).toBeTruthy();
  });

  it('renders default roadmapMeta when not provided', () => {
    render(<RoadmapHeader roadmapTitle="Test" />);
    expect(screen.getByText('Draft • 공개 상태')).toBeTruthy();
  });

  it('renders custom roadmapMeta when provided', () => {
    render(<RoadmapHeader roadmapTitle="Test" roadmapMeta="공개" />);
    expect(screen.getByText('공개')).toBeTruthy();
  });

  it('calls onInfo when 상세 보기 button is clicked', async () => {
    const onInfo = vi.fn();
    render(<RoadmapHeader roadmapTitle="Test" onInfo={onInfo} />);
    await userEvent.click(screen.getByText('상세 보기'));
    expect(onInfo).toHaveBeenCalledTimes(1);
  });

  it('calls onShare when 공유 button is clicked', async () => {
    const onShare = vi.fn();
    render(<RoadmapHeader roadmapTitle="Test" onShare={onShare} />);
    await userEvent.click(screen.getByText('공유'));
    expect(onShare).toHaveBeenCalledTimes(1);
  });

  it('calls onExport when 옵션 button is clicked', async () => {
    const onExport = vi.fn();
    render(<RoadmapHeader roadmapTitle="Test" onExport={onExport} />);
    await userEvent.click(screen.getByText('옵션'));
    expect(onExport).toHaveBeenCalledTimes(1);
  });
});
