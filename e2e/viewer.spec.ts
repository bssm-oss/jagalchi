import { test, expect } from '@playwright/test';

const STORAGE_KEY = 'jagalchi-roadmaps';
const TEST_ROADMAP_ID = 'viewer-test';

const testRoadmap = {
  id: TEST_ROADMAP_ID,
  title: 'Viewer Test Roadmap',
  nodes: [
    {
      id: 'node-1',
      type: 'jagalchi-node',
      position: { x: 100, y: 100 },
      data: { label: 'Test Node', color: '#3b82f6' },
    },
  ],
  edges: [],
  isPublic: true,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
};

test.describe('Viewer E2E', () => {
  test.beforeEach(async ({ page }) => {
    // Seed localStorage with test roadmap
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    await page.evaluate(
      ({ key, data }) => {
        localStorage.setItem(key, JSON.stringify([data]));
      },
      { key: STORAGE_KEY, data: testRoadmap },
    );

    await page.goto(`/viewer/${TEST_ROADMAP_ID}`);
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('header', { timeout: 30000 });
  });

  test('viewer page loads and renders roadmap', async ({ page }) => {
    await expect(page.getByText(`Roadmap · ${TEST_ROADMAP_ID}`)).toBeVisible();

    const canvasMode = page.getByText('캔버스');
    const cardMode = page.getByText('카드');
    await expect(canvasMode.or(cardMode).first()).toBeVisible();
  });

  test('header menu is visible', async ({ page }) => {
    await expect(page.getByLabel('뒤로가기')).toBeVisible();
    await expect(page.getByPlaceholder('로드맵 안에서 검색')).toBeVisible();
    await expect(page.getByText('AI 학습 피드백')).toBeVisible();

    const settingsButton = page.locator('button').filter({ has: page.locator('svg') });
    await expect(settingsButton.first()).toBeVisible();
  });

  test('zoom controls are visible', async ({ page }) => {
    await expect(page.getByLabel('화면 맞춤')).toBeVisible();
    await expect(page.getByLabel('확대')).toBeVisible();
    await expect(page.getByLabel('축소')).toBeVisible();

    await expect(
      page
        .locator('span')
        .filter({ hasText: /^\d+%$/ })
        .first(),
    ).toBeVisible();
  });
});
