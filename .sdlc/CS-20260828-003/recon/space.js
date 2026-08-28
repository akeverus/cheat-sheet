/*
 * CS-20260828-003 Блок A (продолжение) — метрики пустого пространства (§17).
 * Для Tier A экранов на 1920x1080 и 2560x1440, обе темы: ширина вьюпорта,
 * rect #main-content, rect самого широкого видимого блока внутри, доля полезной
 * ширины, крупнейший горизонтальный жёлоб (максимальный пустой промежуток между
 * #main-content и краями вьюпорта либо между соседними видимыми блоками верхнего
 * уровня внутри #main-content).
 */
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const BASE = process.env.E2E_BASE_URL || 'http://localhost:8091';
const DATA_DIR = path.resolve(__dirname, '..', 'data');
fs.mkdirSync(DATA_DIR, { recursive: true });

const PINNED_HOME =
  '/?questionId=2060&topic='
  + encodeURIComponent('algorithms/algorithmic-paradigms/backtracking-interview')
  + '&ordered=true&shuffle=false';

const SCREENS = { home: PINNED_HOME, train: '/train', stats: '/stats', topics: '/topics' };
const VIEWPORTS = [{ w: 1920, h: 1080 }, { w: 2560, h: 1440 }];
const THEMES = ['dark', 'light'];

const measureScript = () => {
  const main = document.querySelector('#main-content');
  if (!main) return null;
  const mainRect = main.getBoundingClientRect();
  const vw = window.innerWidth;

  // Самый широкий видимый (площадь > 0) descendant внутри #main-content.
  const all = Array.from(main.querySelectorAll('*'));
  let widest = null;
  let widestW = 0;
  for (const el of all) {
    const r = el.getBoundingClientRect();
    if (r.width <= 0 || r.height <= 0) continue;
    if (el.offsetParent === null && getComputedStyle(el).position !== 'fixed') continue;
    if (r.width > widestW) {
      widestW = r.width;
      widest = { selector: el.tagName.toLowerCase() + (el.className && typeof el.className === 'string' ? '.' + el.className.trim().split(/\s+/).join('.') : ''), rect: { x: r.x, y: r.y, width: r.width, height: r.height } };
    }
  }

  // Горизонтальные жёлоба: слева/справа от #main-content до краёв вьюпорта, и
  // между непосредственными детьми верхнего уровня внутри #main-content.
  const gutters = [
    { name: 'left-of-main', px: Math.max(0, mainRect.x) },
    { name: 'right-of-main', px: Math.max(0, vw - mainRect.right) },
  ];
  const topChildren = Array.from(main.children).filter((el) => {
    const r = el.getBoundingClientRect();
    return r.width > 0 && r.height > 0;
  });
  for (let i = 0; i < topChildren.length - 1; i++) {
    const a = topChildren[i].getBoundingClientRect();
    const b = topChildren[i + 1].getBoundingClientRect();
    // соседи по горизонтали (одна строка) — считаем зазор между правым краем a и левым b
    if (Math.abs(a.y - b.y) < 4) {
      gutters.push({ name: `between-${i}-${i + 1}`, px: Math.max(0, b.x - a.right) });
    }
  }
  const largestGutter = gutters.reduce((m, g) => (g.px > m.px ? g : m), { name: 'none', px: 0 });

  return {
    viewportWidth: vw,
    viewportHeight: window.innerHeight,
    mainRect: { x: mainRect.x, y: mainRect.y, width: mainRect.width, height: mainRect.height },
    widestBlock: widest,
    usefulWidthRatio: widest ? widest.rect.width / vw : mainRect.width / vw,
    mainWidthRatio: mainRect.width / vw,
    largestGutter,
    dataPixel: document.documentElement.getAttribute('data-pixel'),
  };
};

(async () => {
  const browser = await chromium.launch();
  const results = [];
  for (const theme of THEMES) {
    for (const vp of VIEWPORTS) {
      for (const [screen, url] of Object.entries(SCREENS)) {
        const context = await browser.newContext({ viewport: { width: vp.w, height: vp.h } });
        const page = await context.newPage();
        await page.addInitScript((t) => {
          try { localStorage.setItem('theme', t); localStorage.removeItem('screenPreset'); } catch (_) {}
        }, theme);
        const resp = await page.goto(BASE + url, { waitUntil: 'domcontentloaded' });
        if (!resp || !resp.ok()) {
          results.push({ screen, theme, viewport: `${vp.w}x${vp.h}`, error: `HTTP ${resp ? resp.status() : 'no-response'}` });
          await context.close();
          continue;
        }
        await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 });
        await page.waitForTimeout(300);
        const metrics = await page.evaluate(measureScript);
        results.push({ screen, theme, viewport: `${vp.w}x${vp.h}`, ...metrics });
        await context.close();
      }
    }
  }
  await browser.close();
  fs.writeFileSync(path.join(DATA_DIR, 'space-metrics.json'), JSON.stringify(results, null, 2));
  console.log(`Measured ${results.length} combos`);
  for (const r of results) {
    console.log(`${r.screen} ${r.theme} ${r.viewport}: mainWidthRatio=${r.mainWidthRatio?.toFixed(3)} usefulWidthRatio=${r.usefulWidthRatio?.toFixed(3)} largestGutter=${r.largestGutter ? r.largestGutter.name + '=' + Math.round(r.largestGutter.px) + 'px' : 'n/a'}`);
  }
})();
