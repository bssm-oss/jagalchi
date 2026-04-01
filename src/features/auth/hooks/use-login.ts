import { useMutation } from '@tanstack/react-query';

import { login } from '@/api/auth';
import type { AuthResponse, LoginRequest } from '@/api/auth';

export function useLogin() {
  return useMutation<AuthResponse, Error, LoginRequest>({
    mutationFn: login,
  });
}
