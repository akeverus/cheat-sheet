const { test, expect } = require('@playwright/test');

/**
 * Reflow на узком CSS viewport. Это проверка перекладки, а не реального
 * browser zoom. Порог +1px допускает субпиксельное округление движков.
 */
const SURFACES = ['/', '/practice', '/train', '/stats', '/topics', '/bookmarks', '/settings', '/nope-404'];

for (const width of [320, 1280]) {
  test(`long inline code stays inside every answer at ${width}px`, async ({ page }) => {
    await page.setViewportSize({ width, height: 812 });
    await page.goto('/practice');
    await expect(page.locator('#interview-options label').first()).toBeVisible();
    // Synthetic content checks clipping independently of document.scrollWidth.
    // Every option receives the same tokens; no answer or user data is submitted.
    const bounds = await page.evaluate(() => [...document.querySelectorAll('#interview-options label')].map(label => {
      const text = label.querySelector('span:not([class])');
      const short = document.createElement('code');
      short.textContent = 'O(log n)';
      const long = document.createElement('code');
      long.textContent = 'VeryLongNamespace'.repeat(18) + '.invoke()';
      text.append(' ', short, ' ', long);
      const outer = text.getBoundingClientRect();
      const shortRect = short.getBoundingClientRect();
      const longRect = long.getBoundingClientRect();
      return { left: outer.left, right: outer.right, shortHeight: shortRect.height,
        lineHeight: parseFloat(getComputedStyle(short).lineHeight),
        longLeft: longRect.left, longRight: longRect.right, text: long.textContent };
    }));
    expect(bounds).toHaveLength(4);
    for (const box of bounds) {
      expect(box.longLeft).toBeGreaterThanOrEqual(box.left - 1);
      expect(box.longRight).toBeLessThanOrEqual(box.right + 1);
      expect(box.shortHeight, 'short complexity token should occupy one line').toBeLessThanOrEqual(box.lineHeight + 1);
      expect(box.text).toBe('VeryLongNamespace'.repeat(18) + '.invoke()');
    }
  });
}

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

test('home mobile canvas clears fixed navigation without false viewport scroll', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/', { waitUntil: 'load' });
  await expect(page.locator('.home-stage')).toBeVisible();

  const geometry = await page.evaluate(() => {
    const doc = document.documentElement;
    const main = document.querySelector('#main-content').getBoundingClientRect();
    const nav = document.querySelector('.mobile-nav').getBoundingClientRect();
    return {
      viewportWidth: window.innerWidth,
      clientWidth: doc.clientWidth,
      clientHeight: doc.clientHeight,
      scrollHeight: doc.scrollHeight,
      mainBottom: main.bottom,
      navTop: nav.top,
    };
  });

  expect(geometry.clientWidth, 'mobile shell не должен резервировать desktop scrollbar gutter')
    .toBe(geometry.viewportWidth);
  expect(geometry.mainBottom, 'контент Home не должен уходить под fixed mobile-nav')
    .toBeLessThanOrEqual(geometry.navTop + 1);
  expect(geometry.scrollHeight - geometry.clientHeight,
    'короткий Home не должен получать ложную прокрутку из min-height + nav clearance')
    .toBeLessThanOrEqual(1);
});

test('long mobile page grows the document and still clears fixed navigation', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/stats', { waitUntil: 'load' });
  await expect(page.locator('#main-content')).toBeVisible();

  const before = await page.evaluate(() => ({
    clientHeight: document.documentElement.clientHeight,
    scrollHeight: document.documentElement.scrollHeight,
  }));
  expect(before.scrollHeight, 'длинная страница должна прокручиваться документом, а не обрезаться')
    .toBeGreaterThan(before.clientHeight);

  await page.addStyleTag({ content: 'html { scroll-behavior: auto !important; }' });
  await page.evaluate(() => window.scrollTo(0, document.documentElement.scrollHeight));
  await expect.poll(() => page.evaluate(() =>
    window.scrollY >= document.documentElement.scrollHeight - window.innerHeight - 1))
    .toBe(true);
  const bottom = await page.evaluate(() => ({
    mainBottom: document.querySelector('#main-content').getBoundingClientRect().bottom,
    navTop: document.querySelector('.mobile-nav').getBoundingClientRect().top,
  }));
  expect(bottom.mainBottom, 'конец длинной страницы не должен оставаться под fixed mobile-nav')
    .toBeLessThanOrEqual(bottom.navTop + 1);
});

/**
 * Промежуточные ширины проверяют переходы между основными раскладками.
 * setViewportSize не проверяет механизм масштабирования браузера.
 */
const REFLOW_WIDTHS = [756, 1152, 1382, 1571];

test.describe('intermediate reflow widths', () => {
  for (const width of REFLOW_WIDTHS) {
    for (const path of SURFACES) {
      test(`no horizontal scroll at ${width}px: ${path}`, async ({ page }) => {
        await page.setViewportSize({ width, height: 700 });
        await page.goto(path, { waitUntil: 'load' });
        await page.waitForTimeout(300);
        const overflow = await page.evaluate(() => {
          const doc = document.documentElement;
          return doc.scrollWidth - doc.clientWidth;
        });
        expect(overflow, `горизонтальный вылет на ${path} при ${width}px`).toBeLessThanOrEqual(1);
      });
    }
  }
});
