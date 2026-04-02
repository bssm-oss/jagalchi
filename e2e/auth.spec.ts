import { test, expect } from '@playwright/test';

test.describe('Auth E2E', () => {
  test.describe('Login Page', () => {
    test.beforeEach(async ({ page }) => {
      await page.goto('/login');
    });

    test('renders correctly with email input, password input, and submit button', async ({
      page,
    }) => {
      await expect(page.getByText('로그인해서 계속하기')).toBeVisible();
      await expect(page.getByText('이메일 주소를 입력해주세요')).toBeVisible();

      const emailInput = page.getByPlaceholder('이메일 입력');
      await expect(emailInput).toBeVisible();

      const passwordInput = page.getByPlaceholder('비밀번호 입력');
      await expect(passwordInput).toBeVisible();

      const submitButton = page.getByRole('button', { name: '로그인', exact: true });
      await expect(submitButton).toBeVisible();
    });

    test('shows validation errors for empty submission', async ({ page }) => {
      const submitButton = page.getByRole('button', { name: '로그인', exact: true });
      await submitButton.click();

      await expect(page.getByText('이메일을 입력해주세요')).toBeVisible();
      await expect(page.getByText('비밀번호를 입력해주세요')).toBeVisible();
    });

    test('shows email format validation error', async ({ page }) => {
      const emailInput = page.getByPlaceholder('이메일 입력');
      await emailInput.fill('invalid-email');

      const submitButton = page.getByRole('button', { name: '로그인', exact: true });
      await submitButton.click();

      await expect(page.getByText('올바른 이메일 형식이 아닙니다')).toBeVisible();
    });

    test('login with valid credentials redirects to home', async ({ page }) => {
      const emailInput = page.getByPlaceholder('이메일 입력');
      const passwordInput = page.getByPlaceholder('비밀번호 입력');
      const submitButton = page.getByRole('button', { name: '로그인', exact: true });

      await emailInput.fill('kim@example.com');
      await passwordInput.fill('Test1234!');
      await submitButton.click();

      await expect(page).toHaveURL('/', { timeout: 10000 });
    });

    test('login with invalid credentials shows error message', async ({ page }) => {
      const emailInput = page.getByPlaceholder('이메일 입력');
      const passwordInput = page.getByPlaceholder('비밀번호 입력');
      const submitButton = page.getByRole('button', { name: '로그인', exact: true });

      await emailInput.fill('wrong@example.com');
      await passwordInput.fill('wrongpassword');
      await submitButton.click();

      await expect(page.getByText('이메일 또는 비밀번호가 올바르지 않습니다')).toBeVisible({
        timeout: 10000,
      });
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
    test.beforeEach(async ({ page }) => {
      await page.goto('/register');
    });

    test('renders Step 1 with email, password, and verification fields', async ({ page }) => {
      await expect(page.getByText('회원가입', { exact: true })).toBeVisible();
      await expect(page.getByText('회원가입할 이메일 정보를 입력해주세요')).toBeVisible();

      await expect(page.getByPlaceholder('이메일 입력')).toBeVisible();
      await expect(page.getByPlaceholder('비밀번호 지정')).toBeVisible();
      await expect(page.getByText('인증번호', { exact: true })).toBeVisible();
      await expect(page.getByRole('button', { name: '인증번호 전송' })).toBeVisible();
    });

    test('Step 1: send verification code, verify, and proceed to Step 2', async ({ page }) => {
      const emailInput = page.getByPlaceholder('이메일 입력');
      const passwordInput = page.getByPlaceholder('비밀번호 지정');

      // Fill email and password
      await emailInput.fill('newuser@example.com');
      await passwordInput.fill('NewPass1234!');

      // Send verification code
      const sendCodeButton = page.getByRole('button', { name: '인증번호 전송' });
      await sendCodeButton.click();

      // Wait for code to be sent - "다음" button should appear
      const nextButton = page.getByRole('button', { name: '다음' });
      await expect(nextButton).toBeVisible({ timeout: 10000 });

      // Fill verification code
      const codeInput = page.getByPlaceholder('인증번호 입력');
      await codeInput.fill('123456');

      // Submit Step 1
      await nextButton.click();

      // Should proceed to Step 2 (username input)
      await expect(page.getByPlaceholder('사용자 이름 입력')).toBeVisible({ timeout: 10000 });
    });

    test('Step 1 → Step 2 → Step 3 → complete registration', async ({ page }) => {
      // Step 1: email, password, verification
      await page.getByPlaceholder('이메일 입력').fill('fullflow@example.com');
      await page.getByPlaceholder('비밀번호 지정').fill('NewPass1234!');
      await page.getByRole('button', { name: '인증번호 전송' }).click();

      const nextButton = page.getByRole('button', { name: '다음' });
      await expect(nextButton).toBeVisible({ timeout: 10000 });
      await page.getByPlaceholder('인증번호 입력').fill('123456');
      await nextButton.click();

      // Step 2: username
      const usernameInput = page.getByPlaceholder('사용자 이름 입력');
      await expect(usernameInput).toBeVisible({ timeout: 10000 });
      await usernameInput.fill('테스트유저');
      await page.getByRole('button', { name: '확인' }).click();

      // Step 3: links (skip)
      const skipButton = page.getByRole('button', { name: '건너뛰기' });
      await expect(skipButton).toBeVisible({ timeout: 10000 });
      await skipButton.click();

      // Should redirect to home after registration
      await expect(page).toHaveURL('/', { timeout: 10000 });
    });
  });

  test.describe('Find Password Page', () => {
    test.beforeEach(async ({ page }) => {
      await page.goto('/find-password');
    });

    test('renders correctly with email and verification fields', async ({ page }) => {
      await expect(page.getByText('이메일 인증')).toBeVisible();
      await expect(page.getByText('비밀번호를 재설정할 이메일을 입력해주세요')).toBeVisible();

      await expect(page.getByPlaceholder('이메일 입력')).toBeVisible();
      await expect(page.getByText('인증번호', { exact: true })).toBeVisible();
      await expect(page.getByRole('button', { name: '인증번호 전송' })).toBeVisible();

      const loginLink = page.getByRole('link', { name: '로그인하기' });
      await expect(loginLink).toBeVisible();
      await expect(loginLink).toHaveAttribute('href', '/login');
    });

    test('Step 1: verify email, then Step 2: reset password and redirect to login', async ({
      page,
    }) => {
      // Step 1: enter email and send verification code
      const emailInput = page.getByPlaceholder('이메일 입력');
      await emailInput.fill('kim@example.com');

      const sendCodeButton = page.getByRole('button', { name: '인증번호 전송' });
      await sendCodeButton.click();

      // Wait for "다음" button to appear
      const nextButton = page.getByRole('button', { name: '다음' });
      await expect(nextButton).toBeVisible({ timeout: 10000 });

      // Fill verification code and submit
      const codeInput = page.getByPlaceholder('인증번호 입력');
      await codeInput.fill('123456');
      await nextButton.click();

      // Step 2: enter new password
      const newPasswordInput = page.getByPlaceholder('비밀번호 입력');
      await expect(newPasswordInput).toBeVisible({ timeout: 10000 });

      const confirmPasswordInput = page.getByPlaceholder('비밀번호 다시 입력');
      await expect(confirmPasswordInput).toBeVisible();

      await newPasswordInput.fill('NewPassword1234!');
      await confirmPasswordInput.fill('NewPassword1234!');

      const submitButton = page.getByRole('button', { name: '완료' });
      await submitButton.click();

      // Should redirect to login page
      await expect(page).toHaveURL(/\/login/, { timeout: 10000 });
    });
  });

  test.describe('Navigation between auth pages', () => {
    test('login → register link navigates correctly', async ({ page }) => {
      await page.goto('/login');

      const registerLink = page.getByRole('link', { name: '회원가입' });
      await registerLink.click();

      await expect(page).toHaveURL(/\/register/);
      await expect(page.getByText('회원가입', { exact: true })).toBeVisible();
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
