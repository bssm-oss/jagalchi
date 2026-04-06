import { apiClient } from './client';

// === Request Types (aligned with docs/api.md) ===

interface LoginRequest {
  email: string;
  password: string;
}

interface SignUpRequest {
  email: string;
  name: string;
  password: string;
}

interface SendVerificationCodeRequest {
  email: string;
}

interface VerifyCodeRequest {
  email: string;
  code: string;
}

interface ChangePasswordRequest {
  email: string;
  newPassword: string;
}

interface RefreshTokenRequest {
  refreshToken?: string;
}

// === Response Types ===

interface LoginResponse {
  accessToken: string;
}

interface SignUpResponse {
  id: number;
  email: string;
  name: string;
}

interface MessageResponse {
  message: string;
}

interface RefreshTokenResponse {
  accessToken: string;
}

// === API Functions (endpoints match docs/api.md) ===

/** POST /users/auth/login */
export const login = (data: LoginRequest) =>
  apiClient.post<LoginResponse>('/users/auth/login', data);

/** POST /users */
export const signUp = (data: SignUpRequest) => apiClient.post<SignUpResponse>('/users', data);

/** POST /users/verification — 회원가입 인증코드 전송 */
export const sendVerificationCode = (data: SendVerificationCodeRequest) =>
  apiClient.post<MessageResponse>('/users/verification', data);

/** PATCH /users/verification — 회원가입 인증코드 확인 */
export const verifyCode = (data: VerifyCodeRequest) =>
  apiClient.patch<MessageResponse>('/users/verification', data);

/** PATCH /users/auth/password-reset — 비밀번호 리셋 코드 전송 */
export const sendPasswordResetCode = (data: SendVerificationCodeRequest) =>
  apiClient.patch<MessageResponse>('/users/auth/password-reset', data);

/** PATCH /users/auth/password-reset/verify — 비밀번호 리셋 코드 확인 */
export const verifyPasswordResetCode = (data: VerifyCodeRequest) =>
  apiClient.patch<MessageResponse>('/users/auth/password-reset/verify', data);

/** POST /users/auth/reset-password — 비밀번호 변경 (레거시, MSW용) */
export const resetPassword = (data: ChangePasswordRequest) =>
  apiClient.post<MessageResponse>('/users/auth/reset-password', data);

/** PATCH /users/auth/refresh — 토큰 갱신 */
export const refreshToken = (data?: RefreshTokenRequest) =>
  apiClient.patch<RefreshTokenResponse>('/users/auth/refresh', data);

/** DELETE /users — 계정 삭제 */
export const deleteAccount = () => apiClient.delete<void>('/users');

// === Type Exports ===

export type {
  LoginRequest,
  SignUpRequest,
  SendVerificationCodeRequest,
  VerifyCodeRequest,
  ChangePasswordRequest,
  RefreshTokenRequest,
  LoginResponse,
  SignUpResponse,
  MessageResponse,
  RefreshTokenResponse,
};
