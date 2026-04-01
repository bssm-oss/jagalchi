import { test, expect } from '@playwright/test';

test.describe('Auth E2E', () => {
  test.describe('Login Page', () => {
    test.beforeEach(async ({ page }) => {
      await page.goto('/login');
    });

    test('renders correctly with email input, password input, and submit button', async ({
      page,
    }) => {
      // Title and description
      await expect(page.getByText('로그인해서 계속하기')).toBeVisible();
      await expect(page.getByText('이메일 주소를 입력해주세요')).toBeVisible();

      // Email input
      const emailInput = page.getByPlaceholder('이메일 입력');
      await expect(emailInput).toBeVisible();

      // Password input
      const passwordInput = page.getByPlaceholder('비밀번호 입력');
      await expect(passwordInput).toBeVisible();

      // Submit button
      const submitButton = page.getByRole('button', { name: '로그인' });
      await expect(submitButton).toBeVisible();
    });

    test('shows validation errors for empty submission', async ({ page }) => {
      // Click submit without filling any fields
      const submitButton = page.getByRole('button', { name: '로그인' });
      await submitButton.click();

      // Validation error messages should appear
      await expect(page.getByText('이메일을 입력해주세요')).toBeVisible();
      await expect(page.getByText('비밀번호를 입력해주세요')).toBeVisible();
    });

    test('shows email format validation error', async ({ page }) => {
      // Fill invalid email
      const emailInput = page.getByPlaceholder('이메일 입력');
      await emailInput.fill('invalid-email');

      // Click submit to trigger validation
      const submitButton = page.getByRole('button', { name: '로그인' });
      await submitButton.click();

      // Email format error should appear
      await expect(page.getByText('올바른 이메일 형식이 아닙니다')).toBeVisible();
    });

    test('has link to register page', async ({ page }) => {
      const registerLink = page.getByRole('link', { name: '회원가입' });
      await expect(registerLink).toBeVisible();
      await expect(registerLink).toHaveAttribute('href', '/register');
    });

    test('has link to find-password page', async ({ page }) => {
      const findPasswordLink = page.getByRole('link', { name: '비밀번호를 잊어버렸나요?' });
      await expect(findPasswordLink).toBeVisible();
      await expect(findPasswordLink).toHaveAttribute('href', '/find-password');
    });
  });

  test.describe('Register Page', () => {
    test('renders and shows Step 1 with email, password, and verification fields', async ({
      page,
    }) => {
      await page.goto('/register');

      // Title and description
      await expect(page.getByText('회원가입')).toBeVisible();
      await expect(page.getByText('회원가입할 이메일 정보를 입력해주세요')).toBeVisible();

      // Email input
      const emailInput = page.getByPlaceholder('이메일 입력');
      await expect(emailInput).toBeVisible();

      // Password input
      const passwordInput = page.getByPlaceholder('비밀번호 지정');
      await expect(passwordInput).toBeVisible();

      // Verification code label (disabled state initially)
      await expect(page.getByText('인증번호')).toBeVisible();

      // Send verification code button (initial state)
      const sendCodeButton = page.getByRole('button', { name: '인증번호 전송' });
      await expect(sendCodeButton).toBeVisible();
    });
  });

  test.describe('Find Password Page', () => {
    test('renders correctly with email and verification fields', async ({ page }) => {
      await page.goto('/find-password');

      // Title and description
      await expect(page.getByText('이메일 인증')).toBeVisible();
      await expect(page.getByText('비밀번호를 재설정할 이메일을 입력해주세요')).toBeVisible();

      // Email input
      const emailInput = page.getByPlaceholder('이메일 입력');
      await expect(emailInput).toBeVisible();

      // Verification code label
      await expect(page.getByText('인증번호')).toBeVisible();

      // Send verification code button
      const sendCodeButton = page.getByRole('button', { name: '인증번호 전송' });
      await expect(sendCodeButton).toBeVisible();

      // Login link in footer
      const loginLink = page.getByRole('link', { name: '로그인하기' });
      await expect(loginLink).toBeVisible();
      await expect(loginLink).toHaveAttribute('href', '/login');
    });
  });

  test.describe('Navigation between auth pages', () => {
    test('login → register link navigates correctly', async ({ page }) => {
      await page.goto('/login');

      const registerLink = page.getByRole('link', { name: '회원가입' });
      await registerLink.click();

      await expect(page).toHaveURL(/\/register/);
      await expect(page.getByText('회원가입')).toBeVisible();
    });

    test('login → find-password link navigates correctly', async ({ page }) => {
      await page.goto('/login');

      const findPasswordLink = page.getByRole('link', { name: '비밀번호를 잊어버렸나요?' });
      await findPasswordLink.click();

      await expect(page).toHaveURL(/\/find-password/);
      await expect(page.getByText('이메일 인증')).toBeVisible();
    });
  });
});
