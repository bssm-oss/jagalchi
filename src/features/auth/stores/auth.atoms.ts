import { atom } from 'jotai';

import { setAccessToken, clearAccessToken } from '@/api/client';
import {
  accessTokenAtom,
  currentUserIdAtom,
  currentUserNameAtom,
  currentUserRoleAtom,
} from '@/lib/auth-atoms';
import {
  extractUserIdFromToken,
  extractUserNameFromToken,
  extractUserRoleFromToken,
  mapToStompRole,
} from '@/lib/jwt';
import { clearCurrentUser, setCurrentUser } from '@/lib/realtime-user';

export {
  currentUserIdAtom,
  currentUserNameAtom,
  currentUserRoleAtom,
  isAuthenticatedAtom,
} from '@/lib/auth-atoms';

/** 인증 초기화 완료 여부 (refresh 시도 후 true) */
export const isAuthInitializedAtom = atom<boolean>(false);

/** 로그인 시 토큰 저장 + JWT에서 이름 추출 */
export const loginAtom = atom(null, (_get, set, token: string) => {
  set(accessTokenAtom, token);
  setAccessToken(token);
  const name = extractUserNameFromToken(token);
  const userId = extractUserIdFromToken(token);
  const userRole = mapToStompRole(extractUserRoleFromToken(token));
  set(currentUserNameAtom, name);
  set(currentUserIdAtom, userId);
  set(currentUserRoleAtom, userRole);
  if (userId) {
    setCurrentUser(userId, userRole);
  }
});

/** 로그아웃 시 토큰 삭제 + 상태 초기화 */
export const logoutAtom = atom(null, (_get, set) => {
  set(accessTokenAtom, null);
  set(currentUserNameAtom, null);
  set(currentUserIdAtom, null);
  set(currentUserRoleAtom, null);
  clearCurrentUser();
  clearAccessToken();
});
