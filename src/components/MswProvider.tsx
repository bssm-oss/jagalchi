'use client';

import { useEffect, useState } from 'react';

const isMockingEnabled = process.env.NEXT_PUBLIC_API_MOCKING === 'true';

export function MSWProvider({ children }: { children: React.ReactNode }) {
  const [isReady, setIsReady] = useState(!isMockingEnabled);

  useEffect(() => {
    if (!isMockingEnabled) return;

    async function init() {
      const { initMocks } = await import('@/mocks');
      await initMocks();
      setIsReady(true);
    }
    init();
  }, []);

  if (!isReady) return null;
  return <>{children}</>;
}
