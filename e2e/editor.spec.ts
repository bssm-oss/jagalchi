import { test, expect } from '@playwright/test';

import { loginAsTestUser } from './helpers/auth';

// MSW fixture에 있는 로드맵 ID 사용
const TEST_ROADMAP_ID = 'roadmap-1';

// Editor는 /api/roadmap/:id/events 엔드포인트에 의존하며,
// 이벤트 기반 로드맵 로더의 완전한 구현이 필요.
// MSW mock이 실제 에디터 이벤트 포맷과 맞으면 활성화.
test.describe('Editor E2E', () => {
  test.beforeEach(async ({ page }) => {
    await loginAsTestUser(page);
    await page.goto(`/editor/${TEST_ROADMAP_ID}`);
  });

  test.fixme('editor page loads and canvas renders', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const canvas = page.locator('.react-flow');
    await expect(canvas).toBeVisible();
  });

  test.fixme('node can be added via toolbar', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const initialNodes = await page.locator('.react-flow__node').count();
    await page.getByTestId('toolbar-add-node').click();
    await expect(page.locator('.react-flow__node')).toHaveCount(initialNodes + 1);
  });

  test.fixme('node selection shows properties panel', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    await page.getByTestId('toolbar-add-node').click();
    const nodes = page.locator('.react-flow__node');
    await nodes.first().click();
    await expect(page.getByTestId('properties-panel-header')).toBeVisible({ timeout: 5000 });
  });

  test.fixme('Ctrl+Z undo works', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const initialCount = await page.locator('.react-flow__node').count();
    await page.getByTestId('toolbar-add-node').click();
    await expect(page.locator('.react-flow__node')).toHaveCount(initialCount + 1);
    await page.locator('.react-flow__pane').click();
    await page.keyboard.press('Control+z');
    await expect(page.locator('.react-flow__node')).toHaveCount(initialCount, { timeout: 5000 });
  });
});
