import { render, screen } from '@testing-library/react';
import { Provider, WritableAtom } from 'jotai';
import { useHydrateAtoms } from 'jotai/utils';
import { describe, it, expect } from 'vitest';

import { profileImageAtom, profileModeAtom } from '../../../stores/profile-atoms';

import { ProfileHeader } from './index';

interface WrapperProps {
  initialValues: (readonly [WritableAtom<unknown, any[], any>, unknown])[];
  children: React.ReactNode;
}

const HydrateAtoms = ({ initialValues, children }: WrapperProps) => {
  useHydrateAtoms(initialValues);
  return children;
};

const TestProvider = ({ initialValues, children }: WrapperProps) => (
  <Provider>
    <HydrateAtoms initialValues={initialValues}>{children}</HydrateAtoms>
  </Provider>
);

describe('ProfileHeader', () => {
  const defaultProps = {
    userName: 'Jane Doe',
    email: 'jane@example.com',
    followerCount: 42,
    followingCount: 10,
  };

  it('renders user name and email in show mode', () => {
    render(
      <TestProvider
        initialValues={[
          [profileModeAtom, 'show'],
          [profileImageAtom, '/profile.svg'],
        ]}
      >
        <ProfileHeader {...defaultProps} />
      </TestProvider>,
    );
    expect(screen.getByText('Jane Doe')).toBeInTheDocument();
    expect(screen.getByText('jane@example.com')).toBeInTheDocument();
  });

  it('renders follower and following counts', () => {
    render(
      <TestProvider
        initialValues={[
          [profileModeAtom, 'show'],
          [profileImageAtom, '/profile.svg'],
        ]}
      >
        <ProfileHeader {...defaultProps} />
      </TestProvider>,
    );
    expect(screen.getByText('42')).toBeInTheDocument();
    expect(screen.getByText('10')).toBeInTheDocument();
  });

  it('defaults follower and following counts to 0 when not provided', () => {
    render(
      <TestProvider
        initialValues={[
          [profileModeAtom, 'show'],
          [profileImageAtom, '/profile.svg'],
        ]}
      >
        <ProfileHeader userName="Test" email="test@example.com" />
      </TestProvider>,
    );
    expect(screen.getAllByText('0')).toHaveLength(2);
  });

  it('renders avatar fallback initial when image cannot load', () => {
    render(
      <TestProvider
        initialValues={[
          [profileModeAtom, 'show'],
          [profileImageAtom, '/profile.svg'],
        ]}
      >
        <ProfileHeader {...defaultProps} />
      </TestProvider>,
    );
    // jsdom does not load images so the AvatarFallback initial is shown
    expect(screen.getByText('J')).toBeInTheDocument();
  });

  it('shows file input for image upload in edit mode', () => {
    const { container } = render(
      <TestProvider
        initialValues={[
          [profileModeAtom, 'edit'],
          [profileImageAtom, '/profile.svg'],
        ]}
      >
        <ProfileHeader {...defaultProps} />
      </TestProvider>,
    );
    // In edit mode ProfilePicture renders a hidden file input for upload
    expect(container.querySelector('input[type="file"]')).toBeInTheDocument();
  });
});
