import { test, expect } from '@playwright/test';

// TODO: Viewer pages need investigation — blank render with MSWProvider active
// See: https://github.com/gajaedev/jagalchi-client/issues/179
test.describe.skip('Viewer E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/viewer/1');
    // Wait for the viewer header to render
    await page.waitForSelector('header', { timeout: 30000 });
  });

  test('viewer page loads and renders roadmap', async ({ page }) => {
    // Header should show the roadmap title
    await expect(page.getByText('Roadmap · 1')).toBeVisible();

    // View mode toggle buttons should be visible
    const canvasMode = page.getByText('캔버스');
    const cardMode = page.getByText('카드');
    await expect(canvasMode.or(cardMode).first()).toBeVisible();
  });

  test('header menu is visible', async ({ page }) => {
    // The header contains: back button, title, avatar, search input, AI button
    await expect(page.getByLabel('뒤로가기')).toBeVisible();
    await expect(page.getByPlaceholder('로드맵 안에서 검색')).toBeVisible();
    await expect(page.getByText('AI 학습 피드백')).toBeVisible();

    // Toolbar menu buttons should be visible
    // HeaderMenu uses a Settings icon trigger
    const settingsButton = page.locator('button').filter({ has: page.locator('svg') });
    await expect(settingsButton.first()).toBeVisible();
  });

  test('zoom controls are visible', async ({ page }) => {
    // Zoom controls include: fit-view button, zoom percentage, zoom-in, zoom-out
    await expect(page.getByLabel('화면 맞춤')).toBeVisible();
    await expect(page.getByLabel('확대')).toBeVisible();
    await expect(page.getByLabel('축소')).toBeVisible();

    // Zoom percentage text (e.g., "100%")
    await expect(
      page
        .locator('span')
        .filter({ hasText: /^\d+%$/ })
        .first(),
    ).toBeVisible();
  });
});
