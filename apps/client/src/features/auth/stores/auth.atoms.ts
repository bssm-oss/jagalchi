import { atom } from 'jotai';

import { setAccessToken, clearAccessToken } from '@/api/client';
import {
  accessTokenAtom,
  currentUserEmailAtom,
  currentUserIdAtom,
  currentUserNameAtom,
  currentUserPermissionsAtom,
  currentUserRoleAtom,
} from '@/lib/auth-atoms';
import {
  extractUserEmailFromToken,
  extractUserIdFromToken,
  extractUserNameFromToken,
  extractUserRoleFromToken,
  mapToStompPermissions,
  mapToStompRole,
} from '@/lib/jwt';
import { clearCurrentUser, setCurrentUser } from '@/lib/realtime-user';

export {
  currentUserEmailAtom,
  currentUserIdAtom,
  currentUserNameAtom,
  currentUserPermissionsAtom,
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
  const email = extractUserEmailFromToken(token);
  const userId = extractUserIdFromToken(token);
  const tokenRole = extractUserRoleFromToken(token);
  const userRole = mapToStompRole(tokenRole);
  const userPermissions = mapToStompPermissions(tokenRole);
  set(currentUserNameAtom, name);
  set(currentUserEmailAtom, email);
  set(currentUserIdAtom, userId);
  set(currentUserRoleAtom, userRole);
  set(currentUserPermissionsAtom, userPermissions);
  if (userId) {
    setCurrentUser(userId, userRole, userPermissions);
  } else {
    setCurrentUser(null, null, null);
  }
});

/** 로그아웃 시 토큰 삭제 + 상태 초기화 */
export const logoutAtom = atom(null, (_get, set) => {
  set(accessTokenAtom, null);
  set(currentUserNameAtom, null);
  set(currentUserEmailAtom, null);
  set(currentUserIdAtom, null);
  set(currentUserRoleAtom, null);
  set(currentUserPermissionsAtom, null);
  clearCurrentUser();
  clearAccessToken();
});
