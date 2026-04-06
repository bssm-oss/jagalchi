'use client';

import { useEffect } from 'react';

import { useRouter } from 'next/navigation';

import { useAtomValue } from 'jotai';
import { ArrowLeft, Pencil } from 'lucide-react';

import { profileModeAtom } from '../../../stores/profile-atoms';
import { ProfileBio } from '../../molecules/ProfileBio';
import { ProfileCustomBoxArea } from '../../molecules/ProfileCustomBoxArea';
import { ProfileHeader } from '../../molecules/ProfileHeader';
import { ProfileStreak } from '../../molecules/ProfileStreak';
import { MadeRoadmapList } from '../../organisms/MadeRoadmapList';
import { ProfileThirdBox } from '../../organisms/ProfileThirdBox';

// TODO: Replace with actual user data from API/state
const MOCK_USER_DATA = {
  userName: 'John Doe',
  email: 'john.doe@example.com',
  followerCount: 3000,
  followingCount: 100,
};

export function Profile() {
  const router = useRouter();
  const mode = useAtomValue(profileModeAtom);

  // Warn user before leaving the page while in edit mode
  useEffect(() => {
    if (mode !== 'edit') return;

    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault();
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [mode]);

  return (
    <div className="flex min-h-screen w-full flex-col items-center bg-white">
      <header className="flex h-11 w-full items-center justify-between border-b border-[#e2e8f0] bg-white px-5">
        <button className="flex items-center gap-1 text-sm" onClick={() => router.back()}>
          <ArrowLeft size={14} />
          <span>프로필</span>
        </button>
        <div className="flex items-center gap-1 text-sm">
          <span>User</span>
          <Pencil size={16} />
        </div>
      </header>
      <div className="flex w-full max-w-[960px] flex-col gap-10 px-6 py-10">
        <ProfileHeader
          userName={MOCK_USER_DATA.userName}
          email={MOCK_USER_DATA.email}
          followerCount={MOCK_USER_DATA.followerCount}
          followingCount={MOCK_USER_DATA.followingCount}
        />
        <div className="flex w-full flex-col gap-6 lg:flex-row lg:gap-[76px]">
          <div className="w-full lg:w-[500px] lg:shrink-0">
            <ProfileBio />
          </div>
          <div className="flex-1">
            <ProfileCustomBoxArea />
          </div>
        </div>
        <ProfileStreak />
        <ProfileThirdBox />
        <MadeRoadmapList />
      </div>
    </div>
  );
}
