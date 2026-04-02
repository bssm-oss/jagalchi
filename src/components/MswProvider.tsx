'use client';

import { useEffect } from 'react';

const isMockingEnabled = process.env.NEXT_PUBLIC_API_MOCKING === 'true';

export function MSWProvider({ children }: { children: React.ReactNode }) {
  useEffect(() => {
    if (!isMockingEnabled) return;

    async function init() {
      const { initMocks } = await import('@/mocks');
      await initMocks();
    }
    init();
  }, []);

  return <>{children}</>;
}
