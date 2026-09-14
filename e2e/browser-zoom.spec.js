const { test, expect, chromium } = require('@playwright/test');
const fs = require('fs');
const os = require('os');
const path = require('path');

// chrome.tabs.setZoom changes the browser's real per-origin zoom, not CSS font
// size, deviceScaleFactor or pinch zoom. The extension/profile are test-only.
test('real browser zoom 200% keeps navigation and content reachable', async ({ browserName, baseURL }, info) => {
  test.skip(browserName !== 'chromium', 'Chrome zoom API is specific to Chromium');
  const extension = fs.mkdtempSync(path.join(os.tmpdir(), 'cheat-sheet-zoom-'));
  fs.writeFileSync(path.join(extension, 'manifest.json'), JSON.stringify({
    manifest_version: 3, name: 'Local layout verification', version: '1.0',
    permissions: ['tabs'], background: { service_worker: 'background.js' },
  }));
  fs.writeFileSync(path.join(extension, 'background.js'), 'chrome.runtime.onInstalled.addListener(() => {});');
  let context;
  try {
    context = await chromium.launchPersistentContext('', {
      channel: 'chromium', headless: true, viewport: null, deviceScaleFactor: undefined,
      ignoreDefaultArgs: ['--hide-scrollbars'],
      args: ['--window-size=1280,900', `--disable-extensions-except=${extension}`, `--load-extension=${extension}`],
    });
    const worker = context.serviceWorkers()[0] || await context.waitForEvent('serviceworker');
    const page = await context.newPage();
    await page.goto(baseURL + '/');
    const before = await page.evaluate(() => ({ width: innerWidth, dpr: devicePixelRatio }));
    const zoom = await worker.evaluate(async baseURL => {
      const tabs = await chrome.tabs.query({});
      const tab = tabs.find(t => t.url.startsWith(baseURL));
      await chrome.tabs.setZoom(tab.id, 2);
      return chrome.tabs.getZoom(tab.id);
    }, baseURL);
    expect(zoom).toBe(2);
    await expect.poll(() => page.evaluate(() => innerWidth)).toBeLessThan(before.width * 0.51);
    const measurements = [];
    for (const route of ['/', '/practice', '/train', '/topics', '/bookmarks', '/stats', '/settings']) {
      await page.goto(baseURL + route);
      await page.evaluate(() => document.fonts.ready);
      await expect(page.locator('.mobile-nav')).toBeVisible();
      const reading = await page.evaluate(() => ({
        width: innerWidth, dpr: devicePixelRatio,
        font: parseFloat(getComputedStyle(document.documentElement).fontSize),
        overflow: document.documentElement.scrollWidth - document.documentElement.clientWidth,
      }));
      expect(reading.font).toBe(16);
      expect(reading.dpr).toBeCloseTo(before.dpr * 2, 1);
      expect(reading.overflow).toBeLessThanOrEqual(1);
      measurements.push({ route, ...reading });
      await page.screenshot({ path: info.outputPath(`zoom200-${route.slice(1) || 'home'}.png`) });
    }
    await info.attach('native-zoom-measurements', { body: JSON.stringify({ before, zoom, measurements }, null, 2), contentType: 'application/json' });
  } finally {
    await context?.close();
    fs.rmSync(extension, { recursive: true, force: true });
  }
});
