'use client';

import { Github } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface GitHubAuthButtonProps {
  variant: 'login' | 'register';
  className?: string;
  onClick?: () => void;
}

export function GitHubAuthButton({ variant, className, onClick }: GitHubAuthButtonProps) {
  const label = variant === 'login' ? 'GitHub로 로그인' : 'GitHub로 회원가입';

  return (
    <Button type="button" className={cn('w-full font-semibold', className)} onClick={onClick}>
      <Github className="size-4" />
      {label}
    </Button>
  );
}
