import { render, screen } from '@testing-library/react';
import { Provider } from 'jotai';
import { describe, expect, it } from 'vitest';

import { MyRoadmapsLayout } from './index';

const renderWithProvider = (ui: React.ReactElement) => {
  return render(<Provider>{ui}</Provider>);
};

describe('MyRoadmapsLayout', () => {
  it('renders children', () => {
    renderWithProvider(
      <MyRoadmapsLayout>
        <div>child content</div>
      </MyRoadmapsLayout>,
    );
    expect(screen.getByText('child content')).toBeInTheDocument();
  });

  it('renders sidebar', () => {
    renderWithProvider(
      <MyRoadmapsLayout>
        <div>child</div>
      </MyRoadmapsLayout>,
    );
    expect(screen.getByText('내 로드맵')).toBeInTheDocument();
  });
});
