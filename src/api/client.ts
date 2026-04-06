const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? '/api';

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
  return localStorage.getItem('jagalchi-access-token');
}

/** 액세스 토큰 저장 */
export function setAccessToken(token: string): void {
  localStorage.setItem('jagalchi-access-token', token);
}

/** 액세스 토큰 삭제 */
export function clearAccessToken(): void {
  localStorage.removeItem('jagalchi-access-token');
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

  if (response.status === 401) {
    clearAccessToken();
    if (typeof window !== 'undefined' && !endpoint.includes('/auth/')) {
      window.location.href = '/login';
    }
  }

  if (!response.ok) {
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
