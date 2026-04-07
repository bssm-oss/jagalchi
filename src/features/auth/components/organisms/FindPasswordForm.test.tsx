import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { FindPasswordForm } from './FindPasswordForm';

vi.mock('next/navigation', () => ({
  useRouter: () => ({ push: vi.fn(), replace: vi.fn(), back: vi.fn() }),
}));

vi.mock('../../hooks/use-reset-password', () => ({
  useResetPassword: () => ({
    mutate: vi.fn(),
    isPending: false,
    isError: false,
    error: null,
  }),
}));

let mockIsCodeSent = false;
vi.mock('../../hooks/use-password-reset-code', () => ({
  usePasswordResetCode: () => ({
    get isCodeSent() {
      return mockIsCodeSent;
    },
    handleSendCode: vi.fn((_email: string, onSuccess?: () => void) => {
      mockIsCodeSent = true;
      onSuccess?.();
    }),
    handleVerifyCode: vi.fn().mockResolvedValue(undefined),
    isSendingCode: false,
    isCooldownActive: false,
    cooldownSeconds: 0,
  }),
}));

describe('FindPasswordForm', () => {
  beforeEach(() => {
    mockIsCodeSent = false;
  });

  it('초기에 step 1 (이메일/인증번호) 폼을 렌더링한다', () => {
    render(<FindPasswordForm />);

    expect(screen.getByPlaceholderText('이메일 입력')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('인증번호 입력')).toBeInTheDocument();
  });

  it('초기에 인증번호 전송 버튼을 렌더링한다', () => {
    render(<FindPasswordForm />);

    expect(screen.getByRole('button', { name: '인증번호 전송' })).toBeInTheDocument();
  });

  it('인증번호 전송 클릭 후 다음 버튼을 렌더링한다', async () => {
    const user = userEvent.setup();
    render(<FindPasswordForm />);

    await user.click(screen.getByRole('button', { name: '인증번호 전송' }));

    expect(screen.getByRole('button', { name: '다음' })).toBeInTheDocument();
  });

  it('인증번호 전송 후 재전송 버튼을 표시한다', async () => {
    const user = userEvent.setup();
    render(<FindPasswordForm />);

    await user.click(screen.getByRole('button', { name: '인증번호 전송' }));

    expect(screen.getByRole('button', { name: '인증번호 재전송' })).toBeInTheDocument();
  });

  it('step 1 제출 후 step 2 (새 비밀번호) 폼으로 전환된다', async () => {
    const user = userEvent.setup();
    render(<FindPasswordForm />);

    await user.type(screen.getByPlaceholderText('이메일 입력'), 'test@example.com');
    await user.click(screen.getByRole('button', { name: '인증번호 전송' }));
    await user.type(screen.getByPlaceholderText('인증번호 입력'), '123456');
    await user.click(screen.getByRole('button', { name: '다음' }));

    await waitFor(() => {
      expect(screen.getByPlaceholderText('비밀번호 입력')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('비밀번호 다시 입력')).toBeInTheDocument();
    });
  });

  it('step 2에서 완료 버튼을 렌더링한다', async () => {
    const user = userEvent.setup();
    render(<FindPasswordForm />);

    await user.type(screen.getByPlaceholderText('이메일 입력'), 'test@example.com');
    await user.click(screen.getByRole('button', { name: '인증번호 전송' }));
    await user.type(screen.getByPlaceholderText('인증번호 입력'), '123456');
    await user.click(screen.getByRole('button', { name: '다음' }));

    await waitFor(() => {
      expect(screen.getByRole('button', { name: '완료' })).toBeInTheDocument();
    });
  });

  it('step 2에서 비밀번호 불일치 시 에러를 표시한다', async () => {
    const user = userEvent.setup();
    render(<FindPasswordForm />);

    // Step 1 통과
    await user.type(screen.getByPlaceholderText('이메일 입력'), 'test@example.com');
    await user.click(screen.getByRole('button', { name: '인증번호 전송' }));
    await user.type(screen.getByPlaceholderText('인증번호 입력'), '123456');
    await user.click(screen.getByRole('button', { name: '다음' }));

    // Step 2
    await waitFor(() => {
      expect(screen.getByPlaceholderText('비밀번호 입력')).toBeInTheDocument();
    });

    await user.type(screen.getByPlaceholderText('비밀번호 입력'), 'Password1!');
    await user.type(screen.getByPlaceholderText('비밀번호 다시 입력'), 'Different1!');
    await user.click(screen.getByRole('button', { name: '완료' }));

    await waitFor(() => {
      expect(screen.getByText('비밀번호가 일치하지 않습니다')).toBeInTheDocument();
    });
  });

  it('onStepChange 콜백을 step 전환 시 호출한다', async () => {
    const user = userEvent.setup();
    const onStepChange = vi.fn();
    render(<FindPasswordForm onStepChange={onStepChange} />);

    await user.type(screen.getByPlaceholderText('이메일 입력'), 'test@example.com');
    await user.click(screen.getByRole('button', { name: '인증번호 전송' }));
    await user.type(screen.getByPlaceholderText('인증번호 입력'), '123456');
    await user.click(screen.getByRole('button', { name: '다음' }));

    await waitFor(() => {
      expect(onStepChange).toHaveBeenCalledWith(
        2,
        '새 비밀번호 입력',
        '재설정할 비밀번호를 입력해주세요',
      );
    });
  });
});
