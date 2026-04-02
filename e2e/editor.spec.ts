import { test, expect } from '@playwright/test';

const STORAGE_KEY = 'jagalchi-roadmaps';
const TEST_ROADMAP_ID = 'test-roadmap';

const testRoadmap = {
  id: TEST_ROADMAP_ID,
  title: 'Test Roadmap',
  nodes: [],
  edges: [],
  isPublic: false,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
};

test.describe('Editor E2E', () => {
  test.beforeEach(async ({ page }) => {
    // Seed localStorage with test roadmap before navigating
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    await page.evaluate(
      ({ key, data }) => {
        localStorage.setItem(key, JSON.stringify([data]));
      },
      { key: STORAGE_KEY, data: testRoadmap },
    );

    await page.goto(`/editor/${TEST_ROADMAP_ID}`);
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('.react-flow', { timeout: 30000 });
  });

  test('editor page loads and canvas renders', async ({ page }) => {
    const canvas = page.locator('.react-flow');
    await expect(canvas).toBeVisible();

    const addNodeButton = page.getByTestId('toolbar-add-node');
    await expect(addNodeButton).toBeVisible();
  });

  test('node can be added via toolbar', async ({ page }) => {
    const initialNodes = await page.locator('.react-flow__node').count();

    const addNodeButton = page.getByTestId('toolbar-add-node');
    await addNodeButton.click();

    await expect(page.locator('.react-flow__node')).toHaveCount(initialNodes + 1);
  });

  test('node selection shows properties panel', async ({ page }) => {
    const addNodeButton = page.getByTestId('toolbar-add-node');
    await addNodeButton.click();

    const nodes = page.locator('.react-flow__node');
    await expect(nodes.first()).toBeVisible();

    await nodes.first().click();

    const panelHeader = page.getByTestId('properties-panel-header');
    await expect(panelHeader).toBeVisible({ timeout: 5000 });
  });

  test('Ctrl+Z undo works', async ({ page }) => {
    const initialCount = await page.locator('.react-flow__node').count();

    const addNodeButton = page.getByTestId('toolbar-add-node');
    await addNodeButton.click();
    await expect(page.locator('.react-flow__node')).toHaveCount(initialCount + 1);

    await page.locator('.react-flow__pane').click();

    await page.keyboard.press('Control+z');

    await expect(page.locator('.react-flow__node')).toHaveCount(initialCount, { timeout: 5000 });
  });
});
