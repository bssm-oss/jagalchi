const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? '/api';

export const ACCESS_TOKEN_KEY = 'jagalchi-access-token';

interface ApiError {
  message: string;
  status: number;
  code?: string;
}

interface RequestOptions {
  headers?: Record<string, string>;
  signal?: AbortSignal;
}

class ApiClientError extends Error {
  status: number;
  code?: string;

  constructor({ message, status, code }: ApiError) {
    super(message);
    this.name = 'ApiClientError';
    this.status = status;
    this.code = code;
  }
}

/** localStorage에서 액세스 토큰 읽기 */
function getAccessToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

/** 액세스 토큰 저장 (localStorage + 미들웨어용 쿠키) */
export function setAccessToken(token: string): void {
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
  if (typeof document !== 'undefined') {
    document.cookie = `${ACCESS_TOKEN_KEY}=${token}; path=/; SameSite=Strict`;
  }
}

/** 액세스 토큰 삭제 (localStorage + 쿠키) */
export function clearAccessToken(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  if (typeof document !== 'undefined') {
    document.cookie = `${ACCESS_TOKEN_KEY}=; path=/; max-age=0`;
  }
}

/** 인증 엔드포인트 여부 (401 시 리다이렉트 스킵) */
const isAuthEndpoint = (endpoint: string): boolean =>
  endpoint.includes('/users/auth/') ||
  endpoint === '/users' ||
  endpoint.includes('/users/verification');

async function request<T>(
  endpoint: string,
  init: {
    method: string;
    headers?: Record<string, string>;
    body?: string;
    signal?: AbortSignal;
  },
): Promise<T> {
  const url = `${BASE_URL}${endpoint}`;

  const token = getAccessToken();
  const authHeader: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};

  const response = await fetch(url, {
    ...init,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...authHeader,
      ...init.headers,
    },
  });

  if (!response.ok) {
    if (response.status === 401 && !isAuthEndpoint(endpoint)) {
      clearAccessToken();
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
      throw new ApiClientError({
        message: '인증이 만료되었습니다',
        status: 401,
        code: 'AUTH_REQUIRED',
      });
    }

    const errorBody = (await response.json().catch(() => ({
      message: '알 수 없는 오류가 발생했습니다',
    }))) as { message: string; code?: string };

    throw new ApiClientError({
      message: errorBody.message,
      status: response.status,
      code: errorBody.code,
    });
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const apiClient = {
  get: <T>(endpoint: string, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'GET' }),

  post: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    request<T>(endpoint, {
      ...options,
      method: 'POST',
      body: body ? JSON.stringify(body) : undefined,
    }),

  put: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    request<T>(endpoint, {
      ...options,
      method: 'PUT',
      body: body ? JSON.stringify(body) : undefined,
    }),

  patch: <T>(endpoint: string, body?: unknown, options?: RequestOptions) =>
    request<T>(endpoint, {
      ...options,
      method: 'PATCH',
      body: body ? JSON.stringify(body) : undefined,
    }),

  delete: <T>(endpoint: string, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'DELETE' }),
};

export { ApiClientError };
