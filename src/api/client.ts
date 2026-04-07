const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? '/api';

export const SESSION_COOKIE_KEY = 'jagalchi-session';

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

// --- Token Management (in-memory only) ---

let accessToken: string | null = null;
let isRefreshing = false;
let refreshPromise: Promise<string | null> | null = null;

export function getAccessToken(): string | null {
  return accessToken;
}

/** 액세스 토큰 저장 (메모리 + 미들웨어용 세션 플래그 쿠키) */
export function setAccessToken(token: string): void {
  accessToken = token;
  if (typeof document !== 'undefined') {
    document.cookie = `${SESSION_COOKIE_KEY}=1; path=/; SameSite=Strict`;
  }
}

/** 액세스 토큰 삭제 (메모리 + 세션 쿠키) */
export function clearAccessToken(): void {
  accessToken = null;
  if (typeof document !== 'undefined') {
    document.cookie = `${SESSION_COOKIE_KEY}=; path=/; max-age=0`;
  }
}

/** 인증 엔드포인트 여부 (401 시 refresh 스킵) */
const isAuthEndpoint = (endpoint: string): boolean =>
  endpoint.includes('/users/auth/') ||
  endpoint === '/users' ||
  endpoint.includes('/users/verification');

/** 리프레시 토큰으로 새 액세스 토큰 획득 */
async function tryRefreshToken(): Promise<string | null> {
  // 이미 리프레시 중이면 같은 Promise 공유 (중복 요청 방지)
  if (isRefreshing && refreshPromise) {
    return refreshPromise;
  }

  isRefreshing = true;
  refreshPromise = (async () => {
    try {
      const url = `${BASE_URL}/users/auth/refresh`;
      const response = await fetch(url, {
        method: 'PATCH',
        credentials: 'include', // httpOnly 리프레시 쿠키 자동 전송
        headers: { 'Content-Type': 'application/json' },
      });

      if (!response.ok) return null;

      const data = (await response.json()) as { accessToken: string };
      setAccessToken(data.accessToken);
      return data.accessToken;
    } catch {
      return null;
    } finally {
      isRefreshing = false;
      refreshPromise = null;
    }
  })();

  return refreshPromise;
}

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
    // 401 + 비인증 엔드포인트 → refresh 시도 후 재요청
    if (response.status === 401 && !isAuthEndpoint(endpoint)) {
      const newToken = await tryRefreshToken();

      if (newToken) {
        // 새 토큰으로 원래 요청 재시도
        const retryResponse = await fetch(url, {
          ...init,
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${newToken}`,
            ...init.headers,
          },
        });

        if (retryResponse.ok) {
          if (retryResponse.status === 204) return undefined as T;
          return retryResponse.json() as Promise<T>;
        }
      }

      // refresh 실패 → 로그아웃
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
