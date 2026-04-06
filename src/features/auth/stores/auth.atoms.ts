import { atom } from 'jotai';
import { atomWithStorage } from 'jotai/utils';

import { ACCESS_TOKEN_KEY, setAccessToken, clearAccessToken } from '@/api/client';

/** 액세스 토큰 atom — localStorage와 자동 동기화 (SSR 안전) */
const accessTokenAtom = atomWithStorage<string | null>(ACCESS_TOKEN_KEY, null);

/** 현재 로그인 상태 (토큰 존재 여부 기반) */
export const isAuthenticatedAtom = atom<boolean>((get) => {
  const token = get(accessTokenAtom);
  return token !== null;
});

/** 로그인 시 토큰 저장 */
export const loginAtom = atom(null, (_get, set, token: string) => {
  set(accessTokenAtom, token);
  setAccessToken(token);
});

/** 로그아웃 시 토큰 삭제 + 상태 초기화 */
export const logoutAtom = atom(null, (_get, set) => {
  set(accessTokenAtom, null);
  clearAccessToken();
});
