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
import { HeaderExportMenu } from './index';

describe('HeaderExportMenu', () => {
  it('renders the export trigger button', () => {
    render(<HeaderExportMenu />);
    expect(screen.getByText(VIEWER_MESSAGES.MENU_EXPORT)).toBeTruthy();
  });

  it('shows export format options after clicking the trigger', async () => {
    render(<HeaderExportMenu />);
    await userEvent.click(screen.getByText(VIEWER_MESSAGES.MENU_EXPORT));
    expect(screen.getByText(VIEWER_MESSAGES.EXPORT_MARKDOWN)).toBeTruthy();
    expect(screen.getByText(VIEWER_MESSAGES.EXPORT_PDF)).toBeTruthy();
    expect(screen.getByText(VIEWER_MESSAGES.EXPORT_JSON)).toBeTruthy();
  });
});
