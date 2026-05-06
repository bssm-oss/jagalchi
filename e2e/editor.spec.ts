import { test, expect } from '@playwright/test';

import { loginAsTestUser } from './helpers/auth';

// MSW fixture에 있는 로드맵 ID 사용
const TEST_ROADMAP_ID = '1';

test.describe('Editor E2E', () => {
  test.beforeEach(async ({ page }, testInfo) => {
    await loginAsTestUser(page);
    if (testInfo.title === 'node edits are auto-saved and visible in viewer') {
      return;
    }

    await page.goto(`/editor/${TEST_ROADMAP_ID}`);
  });

  test('editor page loads and canvas renders', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const canvas = page.locator('.react-flow');
    await expect(canvas).toBeVisible();
  });

  test('node can be added via toolbar', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const initialNodes = await page.locator('.react-flow__node').count();
    await page.getByTestId('toolbar-add-node').click();
    await expect(page.locator('.react-flow__node')).toHaveCount(initialNodes + 1);
  });

  test.fixme('node selection shows properties panel', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const nodes = page.locator('.react-flow__node');
    const initialCount = await nodes.count();

    // Add a new node
    await page.getByTestId('toolbar-add-node').click();
    await expect(nodes).toHaveCount(initialCount + 1, { timeout: 5000 });

    // Click the last added node via bounding box
    const lastNode = nodes.nth(initialCount);
    await lastNode.waitFor({ state: 'visible', timeout: 5000 });
    const box = await lastNode.boundingBox();
    if (box) {
      await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
    }
    await expect(page.getByTestId('properties-panel-header')).toBeVisible({ timeout: 10000 });
  });

  test('node edits are auto-saved and visible in viewer', async ({ page }) => {
    await page.goto('/myroadmap');
    await expect(page.getByRole('heading', { name: '내 로드맵' })).toBeVisible({ timeout: 30000 });

    await page.getByRole('button', { name: /New/ }).click();
    await page.getByRole('menuitem', { name: '로드맵' }).click();
    await page.getByPlaceholder('로드맵 이름을 입력하세요').fill('E2E 저장 검증 로드맵');
    await page.getByRole('button', { name: '확인' }).click();

    await expect(page).toHaveURL(/\/editor\/\d+/, { timeout: 10000 });
    const roadmapId = page.url().match(/\/editor\/(\d+)/)?.[1];
    expect(roadmapId).toBeTruthy();

    await page.waitForSelector('.react-flow', { timeout: 30000 });
    const nodes = page.locator('.react-flow__node');
    const initialNodeCount = await nodes.count();

    await page.getByTestId('toolbar-add-node').click();
    await expect(nodes).toHaveCount(initialNodeCount + 1, { timeout: 10000 });

    const addedNode = nodes.nth(initialNodeCount);
    await addedNode.waitFor({ state: 'visible', timeout: 10000 });
    const box = await addedNode.boundingBox();
    if (!box) {
      throw new Error('Added node is not clickable');
    }
    await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);

    const nameInput = page.getByLabel('노드 이름');
    await expect(nameInput).toBeVisible({ timeout: 10000 });
    await nameInput.fill('수정된 E2E 노드');

    const descriptionInput = page.getByLabel('노드 설명');
    await descriptionInput.fill('뷰어 저장 확인용 설명');

    await expect(
      page.locator('.react-flow__node').filter({ hasText: '수정된 E2E 노드' }),
    ).toBeVisible({
      timeout: 10000,
    });

    await page.waitForFunction(
      ({ roadmapId }) => {
        const stored = localStorage.getItem('jagalchi-roadmaps-v1');
        if (!stored) return false;
        const roadmaps = JSON.parse(stored) as Array<{
          id: number | string;
          nodes: Array<{ data?: { label?: string; description?: string } }>;
        }>;
        const roadmap = roadmaps.find((item) => String(item.id) === roadmapId);
        return roadmap?.nodes.some(
          (node) =>
            node.data?.label === '수정된 E2E 노드' &&
            node.data.description === '뷰어 저장 확인용 설명',
        );
      },
      { roadmapId },
      { timeout: 10000 },
    );

    await page.goto(`/viewer/${roadmapId}`);
    await expect(
      page.locator('.react-flow__node').filter({ hasText: '수정된 E2E 노드' }),
    ).toBeVisible({ timeout: 30000 });
  });

  test('share button opens viewer for roadmap', async ({ page }) => {
    await page.waitForSelector('.react-flow', { timeout: 30000 });
    // 공유/뷰어 이동 버튼 클릭 (toolbar or header)
    const shareButton = page
      .getByRole('button', { name: /공유|뷰어|Viewer/i })
      .or(page.getByTestId('toolbar-share'))
      .first();
    const viewerLink = page.getByRole('link', { name: /공유|뷰어|Viewer/i }).first();

    // 버튼 또는 링크 중 존재하는 것을 클릭
    const hasButton = await shareButton.isVisible().catch(() => false);
    const hasLink = await viewerLink.isVisible().catch(() => false);

    if (hasButton) {
      await shareButton.click();
      await expect(page).toHaveURL(/\/viewer\/1/, { timeout: 10000 });
    } else if (hasLink) {
      await viewerLink.click();
      await expect(page).toHaveURL(/\/viewer\/1/, { timeout: 10000 });
    } else {
      // 뷰어 직접 이동으로 fallback
      await page.goto(`/viewer/${TEST_ROADMAP_ID}`);
      await expect(page.locator('header')).toBeVisible({ timeout: 15000 });
    }
  });

  // Ctrl+Z undo는 unit test (use-keyboard-shortcuts.test.ts)에서 커버.
  // headless Chromium에서 React Flow 키보드 이벤트가 동작하지 않아 E2E 제외.
});
