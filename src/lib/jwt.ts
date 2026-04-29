/**
 * JWT payload를 클라이언트 사이드에서 디코딩하는 유틸리티.
 * 서명 검증 없이 payload만 파싱하므로 표시 용도로만 사용할 것.
 */

interface JwtPayload {
  sub?: string;
  id?: string | number;
  name?: string;
  email?: string;
  role?: string;
  type?: string;
  exp?: number;
  iat?: number;
}

/**
 * JWT 토큰에서 payload를 디코딩한다.
 * 파싱 실패 시 null 반환.
 */
export function decodeJwtPayload(token: string): JwtPayload | null {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    const payload = parts[1];
    // base64url → base64 변환
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
    const bytes = Uint8Array.from(atob(padded), (c) => c.charCodeAt(0));
    return JSON.parse(new TextDecoder().decode(bytes)) as JwtPayload;
  } catch {
    return null;
  }
}

/**
 * JWT 토큰에서 사용자 이름을 추출한다.
 * name claim → id claim → sub claim 순서로 시도하고 모두 없으면 null 반환.
 */
export function extractUserNameFromToken(token: string): string | null {
  const payload = decodeJwtPayload(token);
  if (!payload) return null;
  return payload.name ?? (payload.id !== undefined ? String(payload.id) : (payload.sub ?? null));
}

/**
 * JWT 토큰에서 사용자 ID를 추출한다.
 */
export function extractUserIdFromToken(token: string): string | null {
  const payload = decodeJwtPayload(token);
  if (!payload) return null;
  return payload.id !== undefined ? String(payload.id) : (payload.sub ?? null);
}

/**
 * JWT 토큰에서 사용자 역할을 추출한다.
 */
export function extractUserRoleFromToken(token: string): string | null {
  const payload = decodeJwtPayload(token);
  if (!payload) return null;
  return payload.role ?? null;
}

/**
 * 인증 토큰의 역할을 STOMP 서버가 기대하는 X-User-Role 값으로 변환한다.
 * Gateway/User 도메인 역할: STUDENT, TEACHER, ADMIN
 * Node STOMP 역할: USER, ADMIN, GUEST
 */
export function mapToStompRole(role: string | null): string {
  if (!role) return 'GUEST';

  switch (role.toUpperCase()) {
    case 'STUDENT':
      return 'USER';
    case 'TEACHER':
    case 'ADMIN':
      return 'ADMIN';
    case 'USER':
      return 'USER';
    case 'GUEST':
      return 'GUEST';
    default:
      return 'GUEST';
  }
}

/**
 * 인증 토큰의 역할을 STOMP 서버가 기대하는 X-Permissions 값으로 변환한다.
 */
export function mapToStompPermissions(role: string | null): string {
  const stompRole = mapToStompRole(role);

  switch (stompRole) {
    case 'ADMIN':
      return 'ALL';
    case 'USER':
      return 'READ,WRITE';
    default:
      return 'READ';
  }
}
