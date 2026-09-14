const { test, expect } = require('@playwright/test');

const routes = ['/', '/practice', '/review', '/train', '/topics', '/bookmarks', '/stats', '/settings', '/session-summary', '/nope-404'];

async function geometry(page) {
  return page.evaluate(() => {
    const box = el => {
      const r = el.getBoundingClientRect();
      const s = getComputedStyle(el);
      return { x: r.x, y: r.y, width: r.width, height: r.height,
        font: parseFloat(s.fontSize), line: parseFloat(s.lineHeight) || 0 };
    };
    const sidebar = document.querySelector('.sidebar');
    const visible = getComputedStyle(sidebar).display !== 'none';
    return {
      visible,
      mainX: document.querySelector('.shell-main').getBoundingClientRect().x,
      elements: [...document.querySelectorAll(visible
        ? '.sidebar, .sidebar .brand, .sidebar .avatar, .sidebar .brand-crest, .sidebar .nav, .sidebar .nav a, .sidebar .nav .ui-icon, .sidebar-footer'
        : '.mobile-header, .mobile-header .brand, .mobile-nav, .mobile-nav a')].map(box),
      overflow: document.documentElement.scrollWidth - document.documentElement.clientWidth,
    };
  });
}

function sameShell(actual, reference, context) {
  expect(actual.visible, context).toBe(reference.visible);
  expect(Math.abs(actual.mainX - reference.mainX), context + ' main boundary').toBeLessThanOrEqual(1);
  expect(actual.elements.length, context).toBe(reference.elements.length);
  actual.elements.forEach((box, i) => {
    for (const key of Object.keys(box)) {
      expect(Math.abs(box[key] - reference.elements[i][key]), `${context}: element ${i} ${key}`).toBeLessThanOrEqual(1);
    }
  });
  expect(actual.overflow, context + ' document overflow').toBeLessThanOrEqual(1);
}

for (const [width, height] of [[375, 812], [1280, 800], [1728, 1117], [2056, 1329], [320, 812], [756, 800], [768, 800], [900, 800], [901, 800], [1168, 755], [1496, 967]]) {
  for (const theme of ['dark', 'light']) {
    test(`shell stays stable across routes ${width}x${height} ${theme}`, async ({ page }, info) => {
      await page.setViewportSize({ width, height });
      await page.addInitScript(theme => localStorage.setItem('theme', theme), theme);
      let reference;
      const measurements = [];
      for (const route of routes) {
        await page.goto(route);
        await page.evaluate(() => document.fonts.ready);
        // Expose overflow instead of accepting clipping at the document root.
        await page.evaluate(() => {
          document.documentElement.style.overflowX = 'visible';
          document.body.style.overflowX = 'visible';
        });
        const actual = await geometry(page);
        measurements.push({ route, ...actual });
        if (!reference) reference = actual;
        sameShell(actual, reference, route);
      }
      await info.attach('shell-measurements', { body: JSON.stringify(measurements), contentType: 'application/json' });
    });
  }
}

for (const preset of ['phone', 'laptop', 'wide']) {
  test(`140% text keeps route and history geometry with ${preset} profile`, async ({ page }) => {
    await page.setViewportSize({ width: 1168, height: 755 });
    await page.addInitScript(preset => {
      localStorage.setItem('fontScale', '1.4');
      localStorage.setItem('screenPreset', preset);
    }, preset);
    await page.goto('/');
    await page.evaluate(() => document.fonts.ready);
    const reference = await geometry(page);
    await page.locator('.sidebar .nav a[href="/practice"]').click();
    await page.evaluate(() => document.fonts.ready);
    sameShell(await geometry(page), reference, 'menu');
    await page.reload();
    sameShell(await geometry(page), reference, 'reload');
    await page.goBack();
    sameShell(await geometry(page), reference, 'Back');
    await page.goForward();
    sameShell(await geometry(page), reference, 'Forward');
    const stable = await geometry(page);
    await page.evaluate(() => {
      document.querySelector('.sidebar .nav a span').textContent = 'ОченьДлинноеНазваниеРаздела'.repeat(6);
      document.querySelector('.sidebar-status')?.remove();
    });
    const long = await geometry(page);
    expect(long.elements[0].width).toBe(stable.elements[0].width);
    expect(long.mainX).toBe(stable.mainX);
    expect(long.overflow).toBeLessThanOrEqual(1);
  });
}

test('saved text size is applied before JavaScript modules arrive', async ({ page }) => {
  await page.addInitScript(() => localStorage.setItem('fontScale', '1.4'));
  await page.route('**/js/**', route => route.abort());
  await page.goto('/');
  expect(await page.evaluate(() => parseFloat(getComputedStyle(document.documentElement).fontSize))).toBeCloseTo(22.4, 1);
});

for (const width of [320, 375]) {
  test(`mobile navigation respects text size and keeps complete labels at ${width}px`, async ({ page }) => {
    await page.setViewportSize({ width, height: 812 });
    await page.goto('/settings');
    const label = page.locator('.mobile-nav a[href="/practice"] span');
    const initial = await label.evaluate(el => parseFloat(getComputedStyle(el).fontSize));
    await page.evaluate(() => window.__fontScale.set(1.4));
    expect(await label.evaluate(el => parseFloat(getComputedStyle(el).fontSize))).toBeCloseTo(initial * 1.4, 1);
    for (const link of await page.locator('.mobile-nav a').all()) {
      const boxes = await link.evaluate(el => {
        const nav = el.closest('nav').getBoundingClientRect();
        const span = el.querySelector('span');
        const r = span.getBoundingClientRect();
        return { top: r.top - nav.top, bottom: nav.bottom - r.bottom,
          overflow: span.scrollWidth - span.clientWidth };
      });
      expect(boxes.top).toBeGreaterThanOrEqual(0);
      expect(boxes.bottom).toBeGreaterThanOrEqual(0);
      expect(boxes.overflow).toBeLessThanOrEqual(1);
    }
  });
}

test('statistics uses the full row for metrics and keeps each definition together', async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 800 });
  await page.goto('/stats');
  const grid = page.locator('.dashboard-grid');
  const row = await grid.boundingBox();
  const last = await grid.locator('.metric').last().boundingBox();
  expect(Math.abs(row.x + row.width - last.x - last.width), 'no empty column after the metrics').toBeLessThanOrEqual(1);
  await page.locator('.metrics-legend > summary').click();
  const definitions = page.locator('.metrics-legend-list > div');
  await expect(definitions).toHaveCount(4);
  for (const definition of await definitions.all()) {
    await expect(definition.locator('dt')).not.toBeEmpty();
    await expect(definition.locator('dd')).not.toBeEmpty();
  }
});
