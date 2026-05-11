'use client';

import { useEffect, useState } from 'react';

/**
 * MSW는 개발/프리뷰 환경과 E2E 전용 production build 에서만 허용한다.
 * 프로덕션 번들 유입을 막기 위해 아래 조건을 모두 만족해야 동작한다:
 *   1) NEXT_PUBLIC_API_MOCKING === 'true'
 *   2) NODE_ENV !== 'production' 또는 NEXT_PUBLIC_E2E_MOCKING === 'true'
 * 프로덕션 배포에서 실수로 NEXT_PUBLIC_API_MOCKING 가 true 로 설정되더라도
 * E2E 전용 플래그가 없으면 MSW 가 로드되지 않는다.
 */
const isE2EMockingEnabled = process.env.NEXT_PUBLIC_E2E_MOCKING === 'true';
const isMockingEnabled =
  process.env.NEXT_PUBLIC_API_MOCKING === 'true' &&
  (process.env.NODE_ENV !== 'production' || isE2EMockingEnabled);

export function MSWProvider({ children }: { children: React.ReactNode }) {
  const [isReady, setIsReady] = useState(!isMockingEnabled);

  useEffect(() => {
    if (!isMockingEnabled) return;

    const timeout = setTimeout(() => setIsReady(true), 3000);

    async function init() {
      try {
        const { initMocks } = await import('@/mocks');
        await initMocks();
      } catch {
        // MSW init failed — proceed without mocking
      }
      clearTimeout(timeout);
      setIsReady(true);
    }
    init();

    return () => clearTimeout(timeout);
  }, []);

  if (!isReady) return null;
  return <>{children}</>;
}
