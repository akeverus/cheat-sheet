/*
 * CS-20260828-003 Блок A — визуальная матрица (§18.1) + мёртвое пространство (§17).
 * Запуск: E2E_BASE_URL=http://localhost:8091 node ../../.sdlc/CS-20260828-003/recon/shots.js
 * (из каталога e2e, чтобы резолвился playwright из node_modules).
 */
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const BASE = process.env.E2E_BASE_URL || 'http://localhost:8091';
const OUT_DIR = path.resolve(__dirname, '..', 'shots');
const DATA_DIR = path.resolve(__dirname, '..', 'data');
fs.mkdirSync(OUT_DIR, { recursive: true });
fs.mkdirSync(DATA_DIR, { recursive: true });

// Тот же пин, что в e2e/visual-matrix.spec.js:33-36 — детерминированный первый
// вопрос темы независимо от SRS-истории recon-БД.
const PINNED_HOME =
  '/?questionId=2060&topic='
  + encodeURIComponent('algorithms/algorithmic-paradigms/backtracking-interview')
  + '&ordered=true&shuffle=false';

const SCREENS = {
  home: PINNED_HOME,
  train: '/train',
  stats: '/stats',
  topics: '/topics',
};

const TIER_B = {
  bookmarks: '/bookmarks',
  settings: '/settings',
  review: '/review',
  summary: '/session-summary',
};

const VIEWPORTS = [
  { w: 1280, h: 800 },
  { w: 1440, h: 900 },
  { w: 1680, h: 1050 },
  { w: 1920, h: 1080 },
  { w: 2560, h: 1440 },
];

const THEMES = ['dark', 'light'];

// e2e/visual-matrix.spec.js:56-58 — блоки, чьё присутствие волатильно (история
// ответов / незавершённая сессия), скрываются на время снимка целиком.
const VOLATILE_PRESENCE = ['.focus-question-reason', '.resume-banner', '.notice', '.today-chip'];

async function applyPrefs(page, theme) {
  await page.addInitScript((t) => {
    try {
      localStorage.setItem('theme', t);
      localStorage.removeItem('screenPreset');
      localStorage.removeItem('pixelIntensity');
      localStorage.removeItem('motion');
    } catch (_) { /* private mode */ }
  }, theme);
}

async function stabilize(page) {
  await page.addStyleTag({ content: VOLATILE_PRESENCE.join(', ') + ' { display: none !important; }' });
}

async function shoot(page, file) {
  await page.waitForTimeout(300);
  await page.screenshot({ path: file, fullPage: false });
}

(async () => {
  const browser = await chromium.launch();
  const index = [];
  const missing = [];
  const unreachable = [];

  for (const theme of THEMES) {
    for (const vp of VIEWPORTS) {
      for (const [screen, url] of Object.entries(SCREENS)) {
        const context = await browser.newContext({ viewport: { width: vp.w, height: vp.h } });
        const page = await context.newPage();
        await applyPrefs(page, theme);
        const resp = await page.goto(BASE + url, { waitUntil: 'domcontentloaded' });
        const ok = resp && resp.ok();
        if (!ok) {
          unreachable.push({ screen, theme, vp: `${vp.w}x${vp.h}`, status: resp ? resp.status() : 'no-response' });
          await context.close();
          continue;
        }
        await stabilize(page);
        await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 });
        const dataPixel = await page.locator('html').getAttribute('data-pixel');
        const dataTheme = await page.locator('html').getAttribute('data-theme');
        const name = `${screen}-${theme}-${vp.w}x${vp.h}.png`;
        const file = path.join(OUT_DIR, name);
        await shoot(page, file);
        index.push({
          file: `shots/${name}`, screen, theme, viewport: `${vp.w}x${vp.h}`,
          dataPixel, dataTheme, dpr: 1,
        });
        await context.close();
      }
    }
  }

  // Retina — 2 обязательных снимка.
  for (const [screen] of [['home'], ['stats']]) {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 }, deviceScaleFactor: 2 });
    const page = await context.newPage();
    await applyPrefs(page, 'dark');
    const url = SCREENS[screen];
    const resp = await page.goto(BASE + url, { waitUntil: 'domcontentloaded' });
    if (resp && resp.ok()) {
      await stabilize(page);
      await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 });
      const dataPixel = await page.locator('html').getAttribute('data-pixel');
      const dataTheme = await page.locator('html').getAttribute('data-theme');
      const name = `${screen}-dark-1440x900-dpr2.png`;
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen, theme: 'dark', viewport: '1440x900', dataPixel, dataTheme, dpr: 2 });
    } else {
      unreachable.push({ screen, theme: 'dark', vp: '1440x900-dpr2', status: resp ? resp.status() : 'no-response' });
    }
    await context.close();
  }

  // Tier B — один вьюпорт 1440x900, обе темы.
  for (const theme of THEMES) {
    for (const [screen, url] of Object.entries(TIER_B)) {
      const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
      const page = await context.newPage();
      await applyPrefs(page, theme);
      const resp = await page.goto(BASE + url, { waitUntil: 'domcontentloaded' });
      const ok = resp && resp.ok() && !resp.url().endsWith('/session-summary') === false ? true : (resp && resp.ok());
      const finalUrl = page.url();
      const reallyOk = resp && resp.status() < 400 && finalUrl.includes(url.split('?')[0]);
      if (!reallyOk) {
        missing.push(`НЕДОСТИЖИМО: ${screen}/${theme} — HTTP ${resp ? resp.status() : 'no-response'}, редирект на ${finalUrl}`);
        await context.close();
        continue;
      }
      await stabilize(page);
      await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 }).catch(() => {});
      const dataPixel = await page.locator('html').getAttribute('data-pixel');
      const dataTheme = await page.locator('html').getAttribute('data-theme');
      const name = `${screen}-${theme}-1440x900.png`;
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen, theme, viewport: '1440x900', dataPixel, dataTheme, dpr: 1, tier: 'B' });
      await context.close();
    }
  }

  await browser.close();

  fs.writeFileSync(path.join(DATA_DIR, 'shots-index.json'), JSON.stringify({ index, unreachable, missing }, null, 2));
  console.log(`Shots taken: ${index.length}`);
  console.log(`Unreachable: ${JSON.stringify(unreachable)}`);
  console.log(`Missing (Tier B): ${JSON.stringify(missing)}`);
})();
