const { test, expect } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

/**
 * TEST-005 — визуальная матрица theme × pixel × motion.
 * Скриншоты → e2e/visual-results/ (gitignored).
 * Golden: VISUAL_GOLDEN=1 сравнивает с e2e/visual-baselines/
 *   (обновить: e2e/scripts/run-visual-goldens.sh --update
 *    или VISUAL_GOLDEN=1 npx playwright test visual-matrix --update-snapshots).
 * CI: .github/workflows/visual-goldens.yml (workflow_dispatch).
 */
const OUT_DIR = path.join(__dirname, 'visual-results');
const GOLDEN = process.env.VISUAL_GOLDEN === '1';

/** @type {Array<{ id: string, path: string, theme: string, pixel: string, motion: string }>} */
const MATRIX = [
  { id: 'home-dark-med-auto', path: '/', theme: 'dark', pixel: 'med', motion: 'auto' },
  { id: 'home-light-med-on', path: '/', theme: 'light', pixel: 'med', motion: 'on' },
  { id: 'home-dark-low-off', path: '/', theme: 'dark', pixel: 'low', motion: 'off' },
  { id: 'home-dark-high-on', path: '/', theme: 'dark', pixel: 'high', motion: 'on' },
  { id: 'home-light-high-off', path: '/', theme: 'light', pixel: 'high', motion: 'off' },
  { id: 'settings-dark-med-off', path: '/settings', theme: 'dark', pixel: 'med', motion: 'off' },
  { id: 'settings-light-med-on', path: '/settings', theme: 'light', pixel: 'med', motion: 'on' },
  { id: 'stats-dark-med-off', path: '/stats', theme: 'dark', pixel: 'med', motion: 'off' },
  { id: 'stats-light-low-off', path: '/stats', theme: 'light', pixel: 'low', motion: 'off' },
];

/** Extra viewports for Wave 8 responsive smoke (subset of full matrix). */
const VIEWPORTS = [
  { id: 'vp-375', width: 375, height: 812 },
  { id: 'vp-768', width: 768, height: 1024 },
  { id: 'vp-1280', width: 1280, height: 800 },
];

function applyPrefsScript({ theme, pixel, motion }) {
  try {
    if (theme) localStorage.setItem('theme', theme);
    else localStorage.removeItem('theme');
    if (pixel && pixel !== 'med') localStorage.setItem('pixelIntensity', pixel);
    else localStorage.removeItem('pixelIntensity');
    if (motion && motion !== 'auto') localStorage.setItem('motion', motion);
    else localStorage.removeItem('motion');
  } catch (_) { /* private mode */ }
}

function expectedResolvedTheme(theme) {
  return theme === 'auto' ? 'dark' : theme;
}

test.describe('visual preference matrix', () => {
  test.beforeAll(() => {
    fs.mkdirSync(OUT_DIR, { recursive: true });
  });

  for (const combo of MATRIX) {
    test(combo.id, async ({ page }) => {
      await page.addInitScript(applyPrefsScript, {
        theme: combo.theme,
        pixel: combo.pixel,
        motion: combo.motion,
      });

      const response = await page.goto(combo.path, { waitUntil: 'domcontentloaded' });
      expect(response?.ok(), `HTTP ${response?.status()} for ${combo.path}`).toBeTruthy();

      await expect(page.locator('body')).toBeVisible();
      await expect(page.locator('#main-content')).toBeVisible({ timeout: 20_000 });

      const html = page.locator('html');
      await expect(html).toHaveAttribute('data-theme', expectedResolvedTheme(combo.theme));
      await expect(html).toHaveAttribute('data-pixel', combo.pixel);
      if (combo.motion === 'auto') {
        await expect(html).not.toHaveAttribute('data-motion');
      } else {
        await expect(html).toHaveAttribute('data-motion', combo.motion);
      }

      await page.waitForTimeout(400);

      const file = path.join(OUT_DIR, `${combo.id}.png`);
      await page.screenshot({ path: file, fullPage: false });
      expect(fs.existsSync(file)).toBeTruthy();

      if (GOLDEN) {
        await expect(page).toHaveScreenshot(`${combo.id}.png`, {
          maxDiffPixelRatio: 0.03,
          animations: 'disabled',
        });
      }
    });
  }

  for (const vp of VIEWPORTS) {
    test(`home-dark-med-${vp.id}`, async ({ page }) => {
      await page.setViewportSize({ width: vp.width, height: vp.height });
      await page.addInitScript(applyPrefsScript, {
        theme: 'dark',
        pixel: 'med',
        motion: 'off',
      });
      const response = await page.goto('/', { waitUntil: 'domcontentloaded' });
      expect(response?.ok()).toBeTruthy();
      await expect(page.locator('#main-content')).toBeVisible({ timeout: 20_000 });
      const file = path.join(OUT_DIR, `home-dark-med-${vp.id}.png`);
      await page.screenshot({ path: file, fullPage: false });
      expect(fs.existsSync(file)).toBeTruthy();

      if (GOLDEN) {
        await expect(page).toHaveScreenshot(`home-dark-med-${vp.id}.png`, {
          maxDiffPixelRatio: 0.03,
          animations: 'disabled',
        });
      }
    });
  }
});
