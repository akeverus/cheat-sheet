/*
 * CS-20260828-003 Блок C — реальный каскад (§18.3) через CDP.
 * Запуск: NODE_PATH=<e2e/node_modules> E2E_BASE_URL=http://localhost:8091 node cascade.js
 */
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const BASE = process.env.E2E_BASE_URL || 'http://localhost:8091';
const DATA_DIR = path.resolve(__dirname, '..', 'data');
fs.mkdirSync(DATA_DIR, { recursive: true });

function findRuleWinner(matchedRules, propName) {
  // matchedRules — массив от getMatchedStylesForNode, отсортирован браузером от
  // МЕНЕЕ приоритетного к БОЛЕЕ приоритетному (порядок каскада как его видит CDP);
  // последнее совпадение с непустым значением свойства — победитель.
  let winner = null;
  for (const m of matchedRules) {
    const rule = m.rule;
    const props = (rule.style && rule.style.cssProperties) || [];
    const p = props.find((x) => x.name === propName && !x.disabled);
    if (p) {
      winner = {
        selector: rule.selectorList && rule.selectorList.text,
        value: p.value,
        styleSheetId: rule.styleSheetId,
        origin: rule.origin,
      };
    }
  }
  return winner;
}

(async () => {
  const browser = await chromium.launch();
  const results = {};

  // --- 1. Порядок @layer в отданной ленте (focus и stats) ---
  const bundles = {};
  for (const page of ['focus', 'stats']) {
    const url = page === 'focus' ? BASE + '/' : BASE + '/stats';
    const resp = await (await browser.newContext()).newPage().then(async (p) => {
      const r = await p.goto(url, { waitUntil: 'domcontentloaded' });
      const href = await p.locator('link[rel="stylesheet"]').getAttribute('href');
      const cssResp = await p.request.get(BASE + href);
      const text = await cssResp.text();
      await p.context().close();
      return { href, text };
    });
    bundles[page] = resp;
  }
  const layerLine = bundles.focus.text.match(/@layer\s+([^;]+);/);
  results.layerOrder = {
    declaredLine: layerLine ? layerLine[0] : null,
    expected: 'tokens, reset, shell, components, pages, utilities, pixel',
    matches: layerLine ? layerLine[1].replace(/\s+/g, ' ').trim() === 'tokens, reset, shell, components, pages, utilities, pixel' : false,
  };

  // --- 2. Гейт .filter-bar: отсутствует на focus, присутствует на stats ---
  // Наивный includes('.filter-bar') ловит ЛОЖНОЕ совпадение: print.css (гейт `*`)
  // упоминает `.filter-bar` в @media print списке `display:none` наравне с
  // resume-banner/report-zone (это гашение ПРИ ПЕЧАТИ, а не components-filter-bar.css).
  // Точный маркер файла — БАЗОВОЕ правило `html[data-design] .filter-bar {`
  // (components-filter-bar.css:8) с непосредственно открывающей скобкой, а не
  // запятой (список селекторов print.css заканчивает `.filter-bar,`, не `{`).
  const BASE_RULE_RE = /html\[data-design\]\s*\.filter-bar\s*\{/;
  results.filterBarGate = {
    focusHasFilterBar: BASE_RULE_RE.test(bundles.focus.text),
    statsHasFilterBar: BASE_RULE_RE.test(bundles.stats.text),
    note: 'Naive .filter-bar substring matches print.css incidental display:none reference on ВСЕХ страницах (гейт *); проверка сужена до базового правила components-filter-bar.css:8.',
  };

  // --- 3. components-pending vs components-controls на живом узле кнопки ---
  {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const page = await context.newPage();
    const cdp = await context.newCDPSession(page);
    await cdp.send('DOM.enable');
    await cdp.send('CSS.enable');
    await page.goto(BASE + '/?questionId=2060&topic=' + encodeURIComponent('algorithms/algorithmic-paradigms/backtracking-interview') + '&ordered=true&shuffle=false', { waitUntil: 'domcontentloaded' });
    await page.locator('#interview-submit').waitFor({ state: 'attached', timeout: 20000 });
    // #interview-submit несёт СОБСТВЕННЫЙ ID-скоупленный оверрайд
    // (components-controls.css:157-158, `.focus-page #interview-submit:disabled`),
    // который по специфичности (есть #id) старше общего спора pending vs controls
    // и замаскировал бы его. Манифест (components-pending.css:33-38) документирует
    // спор конкретно для `html[data-design] .btn[aria-disabled="true"]` —
    // общего правила без ID. Пробный узел — реальный `.btn` (тот же класс, что у
    // кнопок приложения), подключённый к живому документу и живой ленте CSS,
    // без ID-скоупа, чтобы наблюдать именно этот спор, а не его маскировку.
    await page.evaluate(() => {
      const btn = document.createElement('button');
      btn.className = 'btn';
      btn.id = 'cascade-probe-btn';
      btn.setAttribute('data-pending', 'action');
      btn.setAttribute('aria-disabled', 'true');
      document.body.appendChild(btn);
    });
    const { root } = await cdp.send('DOM.getDocument', { depth: -1, pierce: true });
    const nodeIdResult = await cdp.send('DOM.querySelector', { nodeId: root.nodeId, selector: '#cascade-probe-btn' });
    const matched = await cdp.send('CSS.getMatchedStylesForNode', { nodeId: nodeIdResult.nodeId });
    const cursorWinner = findRuleWinner(matched.matchedCSSRules || [], 'cursor');
    const opacityWinner = findRuleWinner(matched.matchedCSSRules || [], 'opacity');
    results.pendingVsControls = { cursorWinner, opacityWinner };
    await context.close();
  }

  // --- 4. pixel vs components на равной специфичности: .card background-image ---
  {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const page = await context.newPage();
    const cdp = await context.newCDPSession(page);
    await cdp.send('DOM.enable');
    await cdp.send('CSS.enable');
    await page.goto(BASE + '/stats', { waitUntil: 'domcontentloaded' });
    await page.locator('.card').first().waitFor({ state: 'attached', timeout: 20000 });
    const { root } = await cdp.send('DOM.getDocument', { depth: -1, pierce: true });
    const nodeIdResult = await cdp.send('DOM.querySelector', { nodeId: root.nodeId, selector: '.card' });
    const matched = await cdp.send('CSS.getMatchedStylesForNode', { nodeId: nodeIdResult.nodeId });
    const bgImageWinner = findRuleWinner(matched.matchedCSSRules || [], 'background-image');
    const bgWinner = findRuleWinner(matched.matchedCSSRules || [], 'background');
    results.pixelVsComponentsCard = { backgroundImageWinner: bgImageWinner, backgroundShorthandWinner: bgWinner };
    await context.close();
  }

  await browser.close();
  fs.writeFileSync(path.join(DATA_DIR, 'cascade-layers.json'), JSON.stringify({ layerOrder: results.layerOrder, filterBarGate: results.filterBarGate }, null, 2));
  fs.writeFileSync(path.join(DATA_DIR, 'cascade-pending.json'), JSON.stringify(results.pendingVsControls, null, 2));
  fs.writeFileSync(path.join(DATA_DIR, 'cascade-pixel-vs-components.json'), JSON.stringify(results.pixelVsComponentsCard, null, 2));
  console.log(JSON.stringify(results, null, 2));
})();
