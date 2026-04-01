import { apiClient } from './client';

// === Request Types ===

interface LoginRequest {
  email: string;
  password: string;
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

// === Response Types ===

interface AuthUser {
  id: string;
  email: string;
  username: string;
  bio?: string;
  links: { name: string; url: string }[];
  createdAt: string;
}

interface AuthResponse {
  token: string;
  user: AuthUser;
}

interface SendVerificationCodeResponse {
  message: string;
  expiresIn: number;
}

interface VerifyCodeResponse {
  isVerified: boolean;
}

interface ResetPasswordResponse {
  message: string;
}

// === API Functions ===

export const login = (data: LoginRequest) => apiClient.post<AuthResponse>('/auth/login', data);

export const register = (data: RegisterRequest) =>
  apiClient.post<AuthResponse>('/auth/register', data);

export const sendVerificationCode = (data: SendVerificationCodeRequest) =>
  apiClient.post<SendVerificationCodeResponse>('/auth/send-verification-code', data);

export const verifyCode = (data: VerifyCodeRequest) =>
  apiClient.post<VerifyCodeResponse>('/auth/verify-code', data);

export const resetPassword = (data: ResetPasswordRequest) =>
  apiClient.post<ResetPasswordResponse>('/auth/reset-password', data);

// === Type Exports ===

export type {
  LoginRequest,
  RegisterRequest,
  SendVerificationCodeRequest,
  VerifyCodeRequest,
  ResetPasswordRequest,
  AuthUser,
  AuthResponse,
  SendVerificationCodeResponse,
  VerifyCodeResponse,
  ResetPasswordResponse,
};
