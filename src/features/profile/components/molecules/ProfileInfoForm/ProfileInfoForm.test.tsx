import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect } from 'vitest';

import { createTestWrapper } from '@/test-utils/create-test-wrapper';

import { profileModeAtom } from '../../../stores/profile-atoms';

import { ProfileInfoForm } from './index';

describe('ProfileInfoForm', () => {
  const defaultProps = {
    name: 'Test User',
    email: 'test@example.com',
  };

  const createProfileWrapper = (mode: 'show' | 'edit') =>
    createTestWrapper([[profileModeAtom, mode]]);

  it('renders correctly in view mode', () => {
    render(<ProfileInfoForm {...defaultProps} />, { wrapper: createProfileWrapper('show') });
    expect(screen.getByText('Test User')).toBeInTheDocument();
    expect(screen.getByText('test@example.com')).toBeInTheDocument();
    expect(screen.queryByDisplayValue('Test User')).not.toBeInTheDocument();
  });

  it('renders inputs in edit mode', () => {
    render(<ProfileInfoForm {...defaultProps} />, { wrapper: createProfileWrapper('edit') });
    expect(screen.getByDisplayValue('Test User')).toBeInTheDocument();
    expect(screen.getByDisplayValue('test@example.com')).toBeInTheDocument();
  });

  it('updates inputs when typed', () => {
    render(<ProfileInfoForm {...defaultProps} />, { wrapper: createProfileWrapper('edit') });

    const nameInput = screen.getByDisplayValue('Test User');
    fireEvent.change(nameInput, { target: { value: 'New Name' } });
    expect(nameInput).toHaveValue('New Name');
  });
});
