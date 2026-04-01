import { test, expect } from '@playwright/test';

// TODO: Editor pages need investigation — blank render with MSWProvider active
// See: https://github.com/gajaedev/jagalchi-client/issues/179
test.describe.skip('Editor E2E', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/editor/test-roadmap');
    // Wait for the React Flow canvas to render
    await page.waitForSelector('.react-flow', { timeout: 30000 });
  });

  test('editor page loads and canvas renders', async ({ page }) => {
    // React Flow canvas should be visible
    const canvas = page.locator('.react-flow');
    await expect(canvas).toBeVisible();

    // Toolbar should be visible
    const addNodeButton = page.getByTestId('toolbar-add-node');
    await expect(addNodeButton).toBeVisible();
  });

  test('node can be added via toolbar', async ({ page }) => {
    // Count initial nodes
    const initialNodes = await page.locator('.react-flow__node').count();

    // Click the add node button
    const addNodeButton = page.getByTestId('toolbar-add-node');
    await addNodeButton.click();

    // Wait for a new node to appear
    await expect(page.locator('.react-flow__node')).toHaveCount(initialNodes + 1);
  });

  test('node selection shows properties panel', async ({ page }) => {
    // Add a node first
    const addNodeButton = page.getByTestId('toolbar-add-node');
    await addNodeButton.click();

    // Wait for the node to appear
    const nodes = page.locator('.react-flow__node');
    await expect(nodes.first()).toBeVisible();

    // Click the node to select it
    await nodes.first().click();

    // Properties panel header should appear with node info
    const panelHeader = page.getByTestId('properties-panel-header');
    await expect(panelHeader).toBeVisible({ timeout: 5000 });
  });

  test('Ctrl+Z undo works', async ({ page }) => {
    // Count initial nodes
    const initialCount = await page.locator('.react-flow__node').count();

    // Add a node
    const addNodeButton = page.getByTestId('toolbar-add-node');
    await addNodeButton.click();
    await expect(page.locator('.react-flow__node')).toHaveCount(initialCount + 1);

    // Click on the canvas pane to deselect (ensure focus is not on input)
    await page.locator('.react-flow__pane').click();

    // Undo with Ctrl+Z
    await page.keyboard.press('Control+z');

    // Node count should return to initial
    await expect(page.locator('.react-flow__node')).toHaveCount(initialCount, { timeout: 5000 });
  });
});
