const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? '/api';

interface ApiError {
  message: string;
  status: number;
}

interface RequestOptions {
  headers?: Record<string, string>;
  signal?: AbortSignal;
}

class ApiClientError extends Error {
  status: number;

  constructor({ message, status }: ApiError) {
    super(message);
    this.name = 'ApiClientError';
    this.status = status;
  }
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

  const response = await fetch(url, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init.headers,
    },
  });

  if (!response.ok) {
    const errorBody = (await response.json().catch(() => ({
      message: '알 수 없는 오류가 발생했습니다',
    }))) as { message: string };

    throw new ApiClientError({
      message: errorBody.message,
      status: response.status,
    });
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

  delete: <T>(endpoint: string, options?: RequestOptions) =>
    request<T>(endpoint, { ...options, method: 'DELETE' }),
};

export { ApiClientError };
