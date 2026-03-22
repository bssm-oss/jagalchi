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

import { ZoomButtonGroup } from './index';

describe('ZoomButtonGroup', () => {
  const defaultProps = {
    value: 100,
    onZoomIn: vi.fn(),
    onZoomOut: vi.fn(),
    onZoomReset: vi.fn(),
  };

  it('renders zoom in button', () => {
    render(<ZoomButtonGroup {...defaultProps} />);
    expect(screen.getByLabelText('확대')).toBeTruthy();
  });

  it('renders zoom out button', () => {
    render(<ZoomButtonGroup {...defaultProps} />);
    expect(screen.getByLabelText('축소')).toBeTruthy();
  });

  it('renders reset button with current zoom value', () => {
    render(<ZoomButtonGroup {...defaultProps} value={150} />);
    expect(screen.getByLabelText('확대 초기화')).toBeTruthy();
    expect(screen.getByText('150%')).toBeTruthy();
  });

  it('calls onZoomIn when 확대 button is clicked', async () => {
    const onZoomIn = vi.fn();
    render(<ZoomButtonGroup {...defaultProps} onZoomIn={onZoomIn} />);
    await userEvent.click(screen.getByLabelText('확대'));
    expect(onZoomIn).toHaveBeenCalledTimes(1);
  });

  it('calls onZoomOut when 축소 button is clicked', async () => {
    const onZoomOut = vi.fn();
    render(<ZoomButtonGroup {...defaultProps} onZoomOut={onZoomOut} />);
    await userEvent.click(screen.getByLabelText('축소'));
    expect(onZoomOut).toHaveBeenCalledTimes(1);
  });

  it('calls onZoomReset when 확대 초기화 button is clicked', async () => {
    const onZoomReset = vi.fn();
    render(<ZoomButtonGroup {...defaultProps} onZoomReset={onZoomReset} />);
    await userEvent.click(screen.getByLabelText('확대 초기화'));
    expect(onZoomReset).toHaveBeenCalledTimes(1);
  });
});
