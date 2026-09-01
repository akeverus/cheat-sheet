/*
 * CS-20260829-044 — живая матрица §5/§6/§17 (адаптация CS-20260828-003/recon/shots.js).
 * Запуск: E2E_BASE_URL=http://localhost:8091 node ../../.sdlc/CS-20260829-044/recon/shots.js
 * (из каталога e2e, чтобы резолвился playwright из node_modules).
 */
const { chromium } = require('playwright');
const fs = require('fs');
const os = require('os');
const path = require('path');

const BASE = process.env.E2E_BASE_URL || 'http://localhost:8091';
const OUT_DIR = path.resolve(__dirname, '..', 'shots');
const DATA_DIR = path.resolve(__dirname, '..', 'data');
fs.mkdirSync(OUT_DIR, { recursive: true });
fs.mkdirSync(DATA_DIR, { recursive: true });

const PINNED_HOME =
  '/?questionId=2060&topic='
  + encodeURIComponent('algorithms/algorithmic-paradigms/backtracking-interview')
  + '&ordered=true&shuffle=false';

const VOLATILE_PRESENCE = ['.focus-question-reason', '.resume-banner', '.notice', '.today-chip'];

const MODES = ['TRAINING', 'EXAM', 'MARATHON', 'STUDY', 'FLASHCARD', 'TYPED_RECALL', 'INTERVIEW', 'CODE_CHALLENGE'];

// app.security.mode=multi (для достижимости /login, §5 экран 8) требует форму
// входа для GET-страниц тоже (application.yml:143 — дефолтные trainer/trainer).
// Логинимся один раз, переиспользуем storageState во всех контекстах кроме
// анонимного снимка самой /login.
// Вне .sdlc/ (в /tmp): storageState несёт сессионный cookie, коммитить его
// незачем — это одноразовый артефакт recon-логина в изолированную БД.
const AUTH_STATE_FILE = path.join(os.tmpdir(), `cs-20260829-044-auth-${process.pid}.json`);
async function login(browser) {
  const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page = await context.newPage();
  await page.goto(BASE + '/login', { waitUntil: 'domcontentloaded' });
  await page.locator('input[name="username"]').fill('trainer');
  await page.locator('input[name="password"]').fill('trainer');
  await Promise.all([
    page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    page.locator('.login-form button[type="submit"]').click(),
  ]);
  await context.storageState({ path: AUTH_STATE_FILE });
  await context.close();
}

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

async function startMode(page, mode) {
  await page.goto(BASE + '/train', { waitUntil: 'domcontentloaded' });
  await page.locator(`.mode-card-input[value="${mode}"]`).check({ force: true });
  await Promise.all([
    page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    page.locator('#session-start-btn').click(),
  ]);
}

// data-confirm гасит первый submit модалкой (ui/form-guards.js); подтверждение
// шлёт форму заново через requestSubmit() — второй клик по [data-prompt-ok].
async function finishSession(page) {
  // Пауза/финиш живут в <details class="focus-session-more"> — свёрнутом
  // меню действий; кнопка скрыта, пока summary не раскрыт кликом.
  await page.locator('.focus-session-more-summary').click();
  await page.locator('.rail-finish button[type="submit"]').click();
  await page.locator('[data-prompt-ok]').click();
  await page.waitForURL('**/session-summary', { waitUntil: 'domcontentloaded' });
}

// §17 — метрики мёртвого пространства (адаптация CS-20260828-003/recon/space.js:26-70).
const measureSpaceScript = () => {
  const main = document.querySelector('#main-content');
  if (!main) return null;
  const mainRect = main.getBoundingClientRect();
  const vw = window.innerWidth;
  const all = Array.from(main.querySelectorAll('*'));
  let widest = null;
  let widestW = 0;
  for (const el of all) {
    const r = el.getBoundingClientRect();
    if (r.width <= 0 || r.height <= 0) continue;
    if (el.offsetParent === null && getComputedStyle(el).position !== 'fixed') continue;
    if (r.width > widestW) {
      widestW = r.width;
      widest = { selector: el.tagName.toLowerCase(), rect: { x: r.x, y: r.y, width: r.width, height: r.height } };
    }
  }
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
    if (Math.abs(a.y - b.y) < 4) gutters.push({ name: `between-${i}-${i + 1}`, px: Math.max(0, b.x - a.right) });
  }
  const largestGutter = gutters.reduce((m, g) => (g.px > m.px ? g : m), { name: 'none', px: 0 });
  const usedArea = topChildren.reduce((sum, el) => {
    const r = el.getBoundingClientRect();
    return sum + r.width * r.height;
  }, 0);
  return {
    viewportWidth: vw,
    viewportHeight: window.innerHeight,
    mainRect: { x: mainRect.x, y: mainRect.y, width: mainRect.width, height: mainRect.height },
    widestBlock: widest,
    usefulWidthRatio: widest ? widest.rect.width / vw : mainRect.width / vw,
    mainWidthRatio: mainRect.width / vw,
    usedAreaRatio: usedArea / (vw * window.innerHeight),
    largestGutter,
  };
};

(async () => {
  const browser = await chromium.launch();
  const index = [];
  const unreachable = [];

  await login(browser);
  const AUTHED = { storageState: AUTH_STATE_FILE };

  // --- 10 не-workspace экранов, тёмная тема, 1440x900 ---
  // login снимается АНОНИМНО (страница входа теряет смысл для залогиненного);
  // остальные девять — с authed storageState (иначе редиректят на /login).
  const NON_WORKSPACE = {
    train: '/train',
    topics: '/topics',
    stats: '/stats',
    bookmarks: '/bookmarks',
    settings: '/settings',
    login: '/login',
    error: '/this-page-does-not-exist-recon-404',
    review: '/review',
  };
  for (const [screen, url] of Object.entries(NON_WORKSPACE)) {
    const context = await browser.newContext({
      viewport: { width: 1440, height: 900 },
      ...(screen === 'login' ? {} : AUTHED),
    });
    const page = await context.newPage();
    await applyPrefs(page, 'dark');
    const resp = await page.goto(BASE + url, { waitUntil: 'domcontentloaded' });
    const ok = resp && resp.status() < 500;
    if (!ok) {
      unreachable.push({ screen, status: resp ? resp.status() : 'no-response' });
      await context.close();
      continue;
    }
    await stabilize(page);
    await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 }).catch(() => {});
    const name = `${screen}-dark-1440x900.png`;
    const file = path.join(OUT_DIR, name);
    await shoot(page, file);
    index.push({ file: `shots/${name}`, screen, theme: 'dark', viewport: '1440x900', status: resp.status() });
    await context.close();
  }

  // --- result: POST /answer no-JS fallback (result.html), тёмная тема, 1440x900 ---
  {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 }, javaScriptEnabled: false, ...AUTHED });
    const page = await context.newPage();
    const resp = await page.goto(BASE + PINNED_HOME, { waitUntil: 'domcontentloaded' });
    if (resp && resp.ok()) {
      const firstOption = page.locator('#interview-options input[type="radio"]').first();
      await firstOption.check({ force: true });
      await Promise.all([
        page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
        page.locator('#interview-submit').click(),
      ]);
      const name = 'result-dark-1440x900.png';
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen: 'result', theme: 'dark', viewport: '1440x900' });
    } else {
      unreachable.push({ screen: 'result', status: resp ? resp.status() : 'no-response' });
    }
    await context.close();
  }

  // --- 8 workspace-состояний (по одному на InterviewMode), тёмная тема, 1440x900 ---
  for (const mode of MODES) {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 }, ...AUTHED });
    const page = await context.newPage();
    await applyPrefs(page, 'dark');
    try {
      await startMode(page, mode);
      await stabilize(page);
      await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 }).catch(() => {});
      const name = `workspace-${mode.toLowerCase()}-dark-1440x900.png`;
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen: `workspace-${mode.toLowerCase()}`, mode, theme: 'dark', viewport: '1440x900' });
    } catch (e) {
      unreachable.push({ screen: `workspace-${mode.toLowerCase()}`, error: String(e) });
    }
    await context.close();
  }

  // --- summary: EXAM mode start -> POST /finish -> /session-summary, тёмная тема ---
  {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 }, ...AUTHED });
    const page = await context.newPage();
    await applyPrefs(page, 'dark');
    try {
      await startMode(page, 'EXAM');
      await finishSession(page);
      await stabilize(page);
      await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 }).catch(() => {});
      const name = 'summary-dark-1440x900.png';
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen: 'summary', theme: 'dark', viewport: '1440x900' });
    } catch (e) {
      unreachable.push({ screen: 'summary-dark-1440x900', error: String(e) });
    }
    await context.close();
  }

  // --- 4 ultrawide 2560x1440 (§17): workspace-training, stats, summary, topics ---
  const spaceMetrics = [];
  const ULTRAWIDE = {
    'workspace-training': { url: null, mode: 'TRAINING' },
    stats: { url: '/stats' },
    summary: { url: '/session-summary', needsSummary: true },
    topics: { url: '/topics' },
  };
  for (const [screen, cfg] of Object.entries(ULTRAWIDE)) {
    const context = await browser.newContext({ viewport: { width: 2560, height: 1440 }, ...AUTHED });
    const page = await context.newPage();
    await applyPrefs(page, 'dark');
    try {
      if (cfg.mode) {
        await startMode(page, cfg.mode);
      } else if (cfg.needsSummary) {
        await startMode(page, 'EXAM');
        await finishSession(page);
      } else {
        await page.goto(BASE + cfg.url, { waitUntil: 'domcontentloaded' });
      }
      await stabilize(page);
      await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 }).catch(() => {});
      const name = `${screen}-dark-2560x1440.png`;
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen, theme: 'dark', viewport: '2560x1440' });
      const metrics = await page.evaluate(measureSpaceScript);
      spaceMetrics.push({ screen, theme: 'dark', viewport: '2560x1440', ...metrics });
    } catch (e) {
      unreachable.push({ screen: `${screen}-2560x1440`, error: String(e) });
    }
    await context.close();
  }

  // --- 4 светлая тема 1440x900: workspace-training, stats, settings, summary ---
  const LIGHT = {
    'workspace-training': { mode: 'TRAINING' },
    stats: { url: '/stats' },
    settings: { url: '/settings' },
    summary: { needsSummary: true },
  };
  for (const [screen, cfg] of Object.entries(LIGHT)) {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 }, ...AUTHED });
    const page = await context.newPage();
    await applyPrefs(page, 'light');
    try {
      if (cfg.mode) {
        await startMode(page, cfg.mode);
      } else if (cfg.needsSummary) {
        await startMode(page, 'EXAM');
        await finishSession(page);
      } else {
        await page.goto(BASE + cfg.url, { waitUntil: 'domcontentloaded' });
      }
      await stabilize(page);
      await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 }).catch(() => {});
      const name = `${screen}-light-1440x900.png`;
      const file = path.join(OUT_DIR, name);
      await shoot(page, file);
      index.push({ file: `shots/${name}`, screen, theme: 'light', viewport: '1440x900' });
    } catch (e) {
      unreachable.push({ screen: `${screen}-light-1440x900`, error: String(e) });
    }
    await context.close();
  }

  await browser.close();
  fs.rmSync(AUTH_STATE_FILE, { force: true });

  fs.writeFileSync(path.join(OUT_DIR, 'shots-index.json'), JSON.stringify({ index, unreachable }, null, 2));
  fs.writeFileSync(path.join(DATA_DIR, 'space-metrics.json'), JSON.stringify(spaceMetrics, null, 2));
  console.log(`Shots taken: ${index.length}`);
  console.log(`Unreachable: ${JSON.stringify(unreachable)}`);
  console.log(`Space metrics: ${spaceMetrics.length}`);
  for (const m of spaceMetrics) {
    if (m.error) { console.log(`${m.screen}: ERROR ${m.error}`); continue; }
    console.log(`${m.screen}: mainWidthRatio=${m.mainWidthRatio.toFixed(3)} usefulWidthRatio=${m.usefulWidthRatio.toFixed(3)} usedAreaRatio=${m.usedAreaRatio.toFixed(3)} largestGutter=${m.largestGutter.name}=${Math.round(m.largestGutter.px)}px`);
  }
})();
