import type { Page } from '@playwright/test';

/**
 * MSW mock 계정으로 로그인 후 인증 상태 설정
 * - kim@example.com / Test1234! (mock user-1)
 */
export async function loginAsTestUser(page: Page) {
  await page.goto('/login');
  await page.getByPlaceholder('이메일 입력').fill('kim@example.com');
  await page.getByPlaceholder('비밀번호 입력').fill('Test1234!');
  await page.getByRole('button', { name: '로그인', exact: true }).click();
  await page.waitForURL(/\/(myroadmap)?$/, { timeout: 15000 });
}
