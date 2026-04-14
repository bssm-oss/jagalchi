import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';

import { PROFILE_MESSAGES } from '@/constants/messages';

import { ProfileThirdBox } from './index';

describe('ProfileThirdBox', () => {
  it('renders completed roadmap section', () => {
    render(<ProfileThirdBox />);
    expect(screen.getByText(PROFILE_MESSAGES.COMPLETED_ROADMAP)).toBeInTheDocument();
  });

  it('renders in-progress roadmap section', () => {
    render(<ProfileThirdBox />);
    expect(screen.getByText(PROFILE_MESSAGES.IN_PROGRESS_ROADMAP)).toBeInTheDocument();
  });

  it('renders roadmap items', () => {
    render(<ProfileThirdBox />);
    const items = screen.getAllByText('유저 님의 프론트엔드 로드맵');
    expect(items.length).toBeGreaterThan(0);
  });
});
