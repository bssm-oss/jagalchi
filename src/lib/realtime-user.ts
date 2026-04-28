let currentUserId: string | null = null;
let currentUserRole: string | null = null;

/** STOMP CONNECT/SEND 헤더에 사용할 현재 사용자 정보를 설정한다. */
export function setCurrentUser(userId: string | null, userRole: string | null): void {
  currentUserId = userId;
  currentUserRole = userRole;
}

/** 로그아웃/세션 만료 시 STOMP 사용자 정보를 초기화한다. */
export function clearCurrentUser(): void {
  currentUserId = null;
  currentUserRole = null;
}

/** STOMP SEND 프레임에 첨부할 현재 사용자 헤더를 반환한다. */
export function getCurrentUserHeaders(): Record<string, string> {
  return {
    ...(currentUserId ? { 'X-User-ID': currentUserId } : {}),
    ...(currentUserRole ? { 'X-User-Role': currentUserRole } : {}),
  };
}
