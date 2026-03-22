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

import { VIEWER_MESSAGES } from '@/constants/messages';
import { HeaderSaveAsImageMenu } from './index';

describe('HeaderSaveAsImageMenu', () => {
  it('renders the save image trigger button', () => {
    render(<HeaderSaveAsImageMenu />);
    expect(screen.getByText(VIEWER_MESSAGES.MENU_SAVE_IMAGE)).toBeTruthy();
  });

  it('shows image format options after clicking the trigger', async () => {
    render(<HeaderSaveAsImageMenu />);
    await userEvent.click(screen.getByText(VIEWER_MESSAGES.MENU_SAVE_IMAGE));
    expect(screen.getByText(VIEWER_MESSAGES.IMAGE_PNG)).toBeTruthy();
    expect(screen.getByText(VIEWER_MESSAGES.IMAGE_JPG)).toBeTruthy();
    expect(screen.getByText(VIEWER_MESSAGES.IMAGE_SVG)).toBeTruthy();
  });
});
