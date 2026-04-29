import { atom } from 'jotai';

/** 액세스 토큰 atom — 메모리에만 저장 (XSS 방어) */
export const accessTokenAtom = atom<string | null>(null);

/** 현재 로그인한 사용자 이름 — JWT payload에서 추출 */
export const currentUserNameAtom = atom<string | null>(null);

/** 현재 로그인한 사용자 ID — JWT payload의 id claim 우선, sub fallback */
export const currentUserIdAtom = atom<string | null>(null);

/** 현재 로그인한 사용자 역할 — STOMP X-User-Role 헤더 값 */
export const currentUserRoleAtom = atom<string | null>(null);

/** 현재 로그인한 사용자 권한 — STOMP X-Permissions 헤더 값 */
export const currentUserPermissionsAtom = atom<string | null>(null);

/** 현재 로그인 상태 (토큰 존재 여부 기반) */
export const isAuthenticatedAtom = atom<boolean>((get) => {
  const token = get(accessTokenAtom);
  return token !== null;
});
