import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

/** 인증이 필요한 라우트 */
const PROTECTED_ROUTES = ['/myroadmap', '/profile', '/editor'];

/** 로그인 상태에서 접근 차단할 라우트 */
const AUTH_ROUTES = ['/login', '/register', '/find-password'];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const token = request.cookies.get('jagalchi-access-token')?.value;

  // TODO(security): pre-production blocker — 현재 쿠키는 non-httpOnly이며 서명 검증 없음.
  // httpOnly Secure 쿠키 + jose JWT 서명 검증으로 전환 필요.
  // 현재는 클라이언트 측 가드에 위임.

  const isProtectedRoute = PROTECTED_ROUTES.some((route) => pathname.startsWith(route));
  const isAuthRoute = AUTH_ROUTES.some((route) => pathname.startsWith(route));

  // 로그인 상태에서 auth 페이지 접근 시 홈으로 리다이렉트
  if (isAuthRoute && token) {
    return NextResponse.redirect(new URL('/myroadmap', request.url));
  }

  // 비로그인 상태에서 보호 라우트 접근 시 로그인으로 리다이렉트
  if (isProtectedRoute && !token) {
    const loginUrl = new URL('/login', request.url);
    // Open Redirect 방지: PROTECTED_ROUTES에 매칭되는 경로만 redirect 허용
    if (PROTECTED_ROUTES.some((route) => pathname.startsWith(route))) {
      loginUrl.searchParams.set('redirect', pathname);
    }
    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    '/myroadmap/:path*',
    '/profile/:path*',
    '/editor/:path*',
    '/login',
    '/register',
    '/find-password',
  ],
};
