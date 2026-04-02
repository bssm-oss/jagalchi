import { useMutation } from '@tanstack/react-query';

import { register } from '@/api/auth';
import type { AuthResponse, RegisterRequest } from '@/api/auth';

export function useRegister() {
  return useMutation<AuthResponse, Error, RegisterRequest>({
    mutationFn: register,
  });
}
