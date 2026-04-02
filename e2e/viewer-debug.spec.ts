import { test } from '@playwright/test';

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

test('debug viewer errors', async ({ page }) => {
  const errors: string[] = [];
  page.on('console', (msg) => {
    if (msg.type() === 'error') errors.push(`CONSOLE ERROR: ${msg.text()}`);
  });
  page.on('pageerror', (err) => {
    errors.push(`PAGE ERROR: ${err.message}\n${err.stack}`);
  });

  await page.goto('/');
  await page.evaluate(
    ({ key, data }) => {
      localStorage.setItem(key, JSON.stringify([data]));
    },
    { key: STORAGE_KEY, data: testRoadmap },
  );

  await page.goto(`/viewer/${TEST_ROADMAP_ID}`);
  await page.waitForTimeout(5000);

  console.log('=== CAPTURED ERRORS ===');
  errors.forEach((e) => console.log(e));
  console.log('=== END ERRORS ===');

  const bodyText = await page.evaluate(() => document.body.innerText);
  console.log('=== PAGE TEXT ===');
  console.log(bodyText);
  console.log('=== END PAGE TEXT ===');
});
