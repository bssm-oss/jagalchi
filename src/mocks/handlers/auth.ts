import { http, HttpResponse } from 'msw';

import {
  createMockToken,
  MOCK_USERS,
  MOCK_VERIFICATION_CODE,
  type MockUser,
} from '../fixtures/users';

// === Request/Response Types ===

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
  user: Omit<MockUser, 'password'>;
}

interface RegisterRequest {
  email: string;
  password: string;
  username: string;
  links?: { name: string; url: string }[];
}

interface SendVerificationCodeRequest {
  email: string;
}

interface VerifyCodeRequest {
  email: string;
  code: string;
}

interface ResetPasswordRequest {
  email: string;
  newPassword: string;
}

interface ErrorResponse {
  message: string;
}

// 가변 유저 저장소 (등록된 유저를 런타임에서 추적)
const registeredUsers: MockUser[] = [...MOCK_USERS];

/** 비밀번호를 제외한 사용자 정보 반환 */
const sanitizeUser = (user: MockUser): Omit<MockUser, 'password'> => ({
  id: user.id,
  email: user.email,
  username: user.username,
  bio: user.bio,
  links: user.links,
  createdAt: user.createdAt,
});

/** Auth API 핸들러 */
export const authHandlers = [
  // POST /api/auth/login
  http.post<Record<string, never>, LoginRequest>('/api/auth/login', async ({ request }) => {
    const body = await request.json();
    const { email, password } = body;

    const user = registeredUsers.find((u) => u.email === email);

    if (!user || user.password !== password) {
      return HttpResponse.json<ErrorResponse>(
        { message: '이메일 또는 비밀번호가 올바르지 않습니다' },
        { status: 401 },
      );
    }

    return HttpResponse.json<LoginResponse>({
      token: createMockToken(user.id),
      user: sanitizeUser(user),
    });
  }),

  // POST /api/auth/register
  http.post<Record<string, never>, RegisterRequest>('/api/auth/register', async ({ request }) => {
    const body = await request.json();
    const { email, password, username, links } = body;

    const existingUser = registeredUsers.find((u) => u.email === email);

    if (existingUser) {
      return HttpResponse.json<ErrorResponse>(
        { message: '이미 등록된 이메일입니다' },
        { status: 409 },
      );
    }

    const newUser: MockUser = {
      id: `user-${Date.now()}`,
      email,
      password,
      username,
      links: links ?? [],
      createdAt: new Date().toISOString(),
    };

    registeredUsers.push(newUser);

    return HttpResponse.json(
      {
        token: createMockToken(newUser.id),
        user: sanitizeUser(newUser),
      },
      { status: 201 },
    );
  }),

  // POST /api/auth/send-verification-code
  http.post<Record<string, never>, SendVerificationCodeRequest>(
    '/api/auth/send-verification-code',
    async ({ request }) => {
      const body = await request.json();
      const { email } = body;

      if (!email) {
        return HttpResponse.json<ErrorResponse>(
          { message: '이메일을 입력해주세요' },
          { status: 400 },
        );
      }

      // Mock: 항상 성공 (실제로 코드를 보내지 않음)
      return HttpResponse.json({
        message: '인증 코드가 발송되었습니다',
        expiresIn: 300, // 5분
      });
    },
  ),

  // POST /api/auth/verify-code
  http.post<Record<string, never>, VerifyCodeRequest>(
    '/api/auth/verify-code',
    async ({ request }) => {
      const body = await request.json();
      const { email, code } = body;

      if (!email || !code) {
        return HttpResponse.json<ErrorResponse>(
          { message: '이메일과 인증 코드를 입력해주세요' },
          { status: 400 },
        );
      }

      if (code !== MOCK_VERIFICATION_CODE) {
        return HttpResponse.json<ErrorResponse>(
          { message: '인증 코드가 올바르지 않습니다' },
          { status: 401 },
        );
      }

      return HttpResponse.json({ isVerified: true });
    },
  ),

  // POST /api/auth/reset-password
  http.post<Record<string, never>, ResetPasswordRequest>(
    '/api/auth/reset-password',
    async ({ request }) => {
      const body = await request.json();
      const { email, newPassword } = body;

      const user = registeredUsers.find((u) => u.email === email);

      if (!user) {
        return HttpResponse.json<ErrorResponse>(
          { message: '등록되지 않은 이메일입니다' },
          { status: 404 },
        );
      }

      // 비밀번호 업데이트
      user.password = newPassword;

      return HttpResponse.json({ message: '비밀번호가 변경되었습니다' });
    },
  ),
];

// 테스트에서 사용자 목록 초기화할 때 사용
export const resetRegisteredUsers = (): void => {
  registeredUsers.length = 0;
  registeredUsers.push(...MOCK_USERS);
};
