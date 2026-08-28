/*
 * CS-20260828-003 Блок C — «слой pixel ничего структурного не меняет» (§18.5).
 * На /(pinned) и /stats, тёмная тема, 1440x900, screenPreset=wide (худший случай
 * data-pixel=high) обходит до 400 узлов внутри #main-content, для каждого через
 * CDP CSS.getMatchedStylesForNode собирает правила и проверяет, входит ли
 * выигравшее объявление свойства в правило слоя pixel.
 *
 * Слой pixel определяется НЕ по эвристике имени файла, а разбором самой
 * отданной ленты: все `@layer pixel { ... }` блоки (по одному на файл
 * pixel-*.css, со сбалансированными скобками) дают множество селекторов,
 * которые точно принадлежат слою pixel в ЖИВОЙ ленте текущего запроса.
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

const STRUCTURAL_RE = /^(position|display|float|grid.*|flex.*|inset|top|right|bottom|left)$/;
const EXTENDED_RE = /^(width|height|min-.*|max-.*|margin.*)$/;

function normSel(s) {
  return s.replace(/\s+/g, ' ').trim();
}

/** Извлекает из каждого `@layer pixel { ... }` блока ленты (со сбалансированными
 *  скобками) пары (селектор|свойство) → значение, ТОЛЬКО для структурных и
 *  расширенных свойств. Ключ по паре (не по одному селектору!) обязателен:
 *  один и тот же текст селектора легитимно повторяется в РАЗНЫХ слоях с
 *  разными свойствами (например `.app` — в shell.css задаёт grid-раскладку, а
 *  в pixel-motion-parity.css внутри @media(prefers-reduced-motion) гасит
 *  animation/background-image под тем же селектором). Матч только по тексту
 *  селектора даёт ложный «структурный» вердикт для чужого правила той же
 *  строки-селектора из другого слоя — поймано и пофикшено на этом самом
 *  прогоне (алгоритм ловил .app{display:grid} из shell.css как «pixel»,
 *  хотя pixel трогает у .app только animation/background-image). */
function extractPixelLayerTrackedProps(cssText) {
  const tracked = new Map(); // "selector|property" -> Set(values)
  const marker = '@layer pixel';
  let idx = 0;
  while ((idx = cssText.indexOf(marker, idx)) !== -1) {
    const braceStart = cssText.indexOf('{', idx);
    if (braceStart === -1) break;
    let depth = 1;
    let i = braceStart + 1;
    while (i < cssText.length && depth > 0) {
      if (cssText[i] === '{') depth++;
      else if (cssText[i] === '}') depth--;
      i++;
    }
    const inner = cssText.slice(braceStart + 1, i - 1);
    // Пары (селектор, тело объявлений) верхнего уровня, включая тела внутри
    // вложенных @media (регекс не различает глубину, но нам важны только
    // реальные наборы объявлений — @-условия сами по себе тела не имеют).
    const ruleRe = /([^{}]+)\{([^{}]*)\}/g;
    let m;
    while ((m = ruleRe.exec(inner)) !== null) {
      const rawSel = m[1].trim();
      const body = m[2];
      if (rawSel.startsWith('@') || rawSel === '') continue;
      const sel = normSel(rawSel);
      const declRe = /([a-zA-Z-]+)\s*:\s*([^;]+);?/g;
      let d;
      while ((d = declRe.exec(body)) !== null) {
        const prop = d[1].trim();
        if (!STRUCTURAL_RE.test(prop) && !EXTENDED_RE.test(prop)) continue;
        const key = sel + '|' + prop;
        if (!tracked.has(key)) tracked.set(key, new Set());
        tracked.get(key).add(d[2].trim());
      }
    }
    idx = i;
  }
  return tracked;
}

async function scanPage(browser, url, label) {
  const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page = await context.newPage();
  const cdp = await context.newCDPSession(page);
  await cdp.send('DOM.enable');
  await cdp.send('CSS.enable');
  await page.addInitScript(() => {
    try {
      localStorage.setItem('theme', 'dark');
      localStorage.setItem('screenPreset', 'wide');
    } catch (_) {}
  });
  const resp = await page.goto(BASE + url, { waitUntil: 'domcontentloaded' });
  if (!resp || !resp.ok()) {
    await context.close();
    return { label, error: `HTTP ${resp ? resp.status() : 'no-response'}` };
  }
  await page.locator('#main-content').waitFor({ state: 'visible', timeout: 20000 });
  const dataPixel = await page.locator('html').getAttribute('data-pixel');

  const cssHref = await page.locator('link[rel="stylesheet"]').getAttribute('href');
  const cssResp = await page.request.get(BASE + cssHref);
  const cssText = await cssResp.text();
  const pixelTracked = extractPixelLayerTrackedProps(cssText);

  const selectors = await page.evaluate(() => {
    const nodes = Array.from(document.querySelectorAll('#main-content *')).slice(0, 400);
    const out = [];
    for (const el of nodes) {
      let sel;
      if (el.id) sel = '#' + el.id;
      else if (el.className && typeof el.className === 'string' && el.className.trim()) {
        sel = el.tagName.toLowerCase() + '.' + el.className.trim().split(/\s+/).join('.');
      } else {
        sel = el.tagName.toLowerCase();
      }
      out.push(sel);
    }
    return out;
  });

  const { root } = await cdp.send('DOM.getDocument', { depth: -1, pierce: true });
  const violations = [];
  const extendedFindings = [];
  let scanned = 0;
  const seenSelectors = new Set();

  for (const sel of selectors) {
    if (seenSelectors.has(sel)) continue;
    seenSelectors.add(sel);
    let nodeIdResult;
    try {
      nodeIdResult = await cdp.send('DOM.querySelector', { nodeId: root.nodeId, selector: sel });
    } catch (_) { continue; }
    if (!nodeIdResult || !nodeIdResult.nodeId) continue;
    let matched;
    try {
      matched = await cdp.send('CSS.getMatchedStylesForNode', { nodeId: nodeIdResult.nodeId });
    } catch (_) { continue; }
    scanned++;
    const rules = matched.matchedCSSRules || [];
    for (const m of rules) {
      const ruleSelText = m.rule.selectorList && normSel(m.rule.selectorList.text);
      if (!ruleSelText) continue;
      const props = (m.rule.style && m.rule.style.cssProperties) || [];
      for (const p of props) {
        if (p.disabled) continue;
        const isStructural = STRUCTURAL_RE.test(p.name);
        const isExtended = EXTENDED_RE.test(p.name);
        if (!isStructural && !isExtended) continue;
        const key = ruleSelText + '|' + p.name;
        const trackedValues = pixelTracked.get(key);
        if (!trackedValues || !trackedValues.has(p.value.trim())) continue; // не из pixel-блока с этим значением
        if (isStructural) {
          violations.push({ selector: sel, rule: ruleSelText, property: p.name, value: p.value });
        } else {
          extendedFindings.push({ selector: sel, rule: ruleSelText, property: p.name, value: p.value });
        }
      }
    }
  }

  await context.close();
  return {
    label, url, dataPixel, nodesScanned: scanned, uniqueSelectors: selectors.length,
    pixelLayerRuleCount: pixelTracked.size, violations, extendedFindings,
  };
}

(async () => {
  const browser = await chromium.launch();
  const home = await scanPage(browser, PINNED_HOME, 'home');
  const stats = await scanPage(browser, '/stats', 'stats');
  await browser.close();
  const result = { home, stats };
  fs.writeFileSync(path.join(DATA_DIR, 'pixel-layer-scan.json'), JSON.stringify(result, null, 2));
  console.log(JSON.stringify({
    home: { dataPixel: home.dataPixel, nodesScanned: home.nodesScanned, pixelRules: home.pixelLayerRuleCount, violations: home.violations.length, extended: home.extendedFindings.length },
    stats: { dataPixel: stats.dataPixel, nodesScanned: stats.nodesScanned, pixelRules: stats.pixelLayerRuleCount, violations: stats.violations.length, extended: stats.extendedFindings.length },
  }, null, 2));
})();
