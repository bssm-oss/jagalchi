import { test, expect } from '@playwright/test';

test.describe('Community E2E', () => {
  test.describe('Community List Page', () => {
    test.beforeEach(async ({ page }) => {
      await page.goto('/community');
      // Wait for the grid to render with roadmap cards
      await page.waitForSelector('a[href^="/community/"]', { timeout: 15000 });
    });

    test('community page loads and shows roadmap list', async ({ page }) => {
      // The page should have roadmap cards rendered in a grid
      const cards = page.locator('a[href^="/community/"]');
      await expect(cards.first()).toBeVisible();

      // Mock data has 15 items; verify at least some are shown
      const count = await cards.count();
      expect(count).toBeGreaterThan(0);

      // Each card should display a title and author
      const firstCardTitle = cards.first().locator('p').first();
      await expect(firstCardTitle).toBeVisible();
      await expect(firstCardTitle).not.toHaveText('');
    });

    test('filter and sort controls are visible', async ({ page }) => {
      // Tab buttons: 인기, 최신, 저장된 로드맵
      await expect(page.getByText('인기')).toBeVisible();
      await expect(page.getByText('최신')).toBeVisible();
      await expect(page.getByText('저장된 로드맵')).toBeVisible();

      // Sort dropdown button (shows 내림차순 by default)
      await expect(page.getByText('내림차순')).toBeVisible();
    });

    test('roadmap card click navigates to detail page', async ({ page }) => {
      // Click the first roadmap card
      const firstCard = page.locator('a[href^="/community/"]').first();
      const href = await firstCard.getAttribute('href');
      await firstCard.click();

      // Should navigate to the detail page
      await page.waitForURL(`**${href}`, { timeout: 15000 });
      expect(page.url()).toContain('/community/');
    });
  });

  test.describe('Community Detail Page', () => {
    test.beforeEach(async ({ page }) => {
      // Navigate to the first mock item (id: "1")
      await page.goto('/community/1');
      // Wait for the detail page title to render
      await page.waitForSelector('h1', { timeout: 15000 });
    });

    test('community detail page renders roadmap info', async ({ page }) => {
      // Title should be visible
      const title = page.locator('h1');
      await expect(title).toBeVisible();
      await expect(title).not.toHaveText('');

      // "로드맵 보기" button should be visible
      await expect(page.getByText('로드맵 보기')).toBeVisible();

      // "내 로드맵에 추가" button should be visible
      await expect(page.getByText('내 로드맵에 추가')).toBeVisible();

      // About section should be present
      await expect(page.getByText('About')).toBeVisible();

      // Made by section should be present
      await expect(page.getByText('Made by')).toBeVisible();
    });
  });
});
