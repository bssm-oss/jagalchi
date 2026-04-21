import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

import { randomBytes } from 'crypto';

const CSRF_COOKIE_NAME = 'csrf-token';
const TOKEN_MAX_AGE_SECONDS = 7_200; // 2시간
const IS_PRODUCTION = process.env.NODE_ENV === 'production';

/**
 * CSRF 토큰 발급 엔드포인트.
 *
 * 기존 `csrf-token` 쿠키가 유효하면 재사용하고,
 * 없으면 새 토큰을 발급해 non-HttpOnly 쿠키로 세트한다.
 * 클라이언트는 응답 JSON의 `token` 필드를 읽어
 * 변경 요청(POST/PUT/PATCH/DELETE) 시 `X-CSRF-Token` 헤더에 담아야 한다.
 */
export function GET(request: NextRequest) {
  const existingToken = request.cookies.get(CSRF_COOKIE_NAME)?.value;
  const token = existingToken ?? randomBytes(32).toString('hex');

  const response = NextResponse.json({ token });

  if (!existingToken) {
    response.cookies.set(CSRF_COOKIE_NAME, token, {
      httpOnly: false, // JS가 읽어야 하므로 HttpOnly 비활성화
      secure: IS_PRODUCTION,
      sameSite: 'lax',
      path: '/',
      maxAge: TOKEN_MAX_AGE_SECONDS,
    });
  }

  return response;
}
