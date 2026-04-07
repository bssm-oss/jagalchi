import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { Provider } from 'jotai';
import { describe, it, expect, vi } from 'vitest';

import { Community } from './index';

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false } },
});

vi.mock('@/hooks/use-popular-roadmaps', () => ({
  usePopularRoadmaps: () => ({ data: undefined, isLoading: false }),
}));

vi.mock('../../molecules/CommunityHeader', () => ({
  CommunityHeader: () => <div data-testid="community-header">Header</div>,
}));

vi.mock('../../molecules/CommunityHero', () => ({
  CommunityHero: () => <div data-testid="community-hero">Hero</div>,
}));

vi.mock('../../molecules/CommunityFilter', () => ({
  CommunityFilter: () => <div data-testid="community-filter">Filter</div>,
}));

vi.mock('../../organisms/CommunityGrid', () => ({
  CommunityGrid: () => <div data-testid="community-grid">Grid</div>,
}));

describe('Community Template', () => {
  it('renders all main sections', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <Provider>
          <Community />
        </Provider>
      </QueryClientProvider>,
    );

    expect(screen.getByTestId('community-header')).toBeInTheDocument();
    expect(screen.getByTestId('community-hero')).toBeInTheDocument();
    expect(screen.getByTestId('community-filter')).toBeInTheDocument();
    expect(screen.getByTestId('community-grid')).toBeInTheDocument();
  });
});
