const { test, expect } = require('@playwright/test');

/**
 * WCAG 1.4.10 Reflow: при ширине 320 CSS px (≈400% зума) контент перекладывается
 * без горизонтальной прокрутки. Замер iter-175: все семь поверхностей чисты в
 * обоих движках на 320 и на 756 (200%); спек держит достигнутое. Порог +1px —
 * субпиксельные округления движков.
 */
const SURFACES = ['/', '/train', '/stats', '/topics', '/bookmarks', '/settings', '/nope-404'];

test.describe('reflow 320px', () => {
  for (const path of SURFACES) {
    test(`no horizontal scroll at 320px: ${path}`, async ({ page }) => {
      await page.setViewportSize({ width: 320, height: 256 });
      await page.goto(path, { waitUntil: 'load' });
      await page.waitForTimeout(300);
      const overflow = await page.evaluate(() => {
        const doc = document.documentElement;
        return doc.scrollWidth - doc.clientWidth;
      });
      expect(overflow, `горизонтальный вылет на ${path}`).toBeLessThanOrEqual(1);
    });
  }
});
