'use client';

import { useEffect, useState } from 'react';

export function MSWProvider({ children }: { children: React.ReactNode }) {
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    async function init() {
      if (process.env.NEXT_PUBLIC_API_MOCKING === 'true') {
        const { initMocks } = await import('@/mocks');
        await initMocks();
      }
      setIsReady(true);
    }
    init();
  }, []);

  if (!isReady) return null;
  return <>{children}</>;
}
