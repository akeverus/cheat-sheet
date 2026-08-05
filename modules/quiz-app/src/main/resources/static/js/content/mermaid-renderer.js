/**
 * Единственный рендерер Mermaid — и для SSR-фрагментов, и для AJAX-контента.
 *
 * Библиотека грузится лениво (см. loadMermaid): сам модуль весит килобайты, а
 * mermaid.min.js — мегабайты, поэтому платить за него имеет смысл только когда
 * схему действительно смотрят. Точка входа — observeDiagrams; renderAllMermaid
 * остаётся публичной для принудительной перерисовки (смена темы, legacy-хуки
 * window.initMermaid / window.renderAllMermaid).
 */

let mermaidReady = false;
let themeRerenderTimer = null;
let toolbarDelegated = false;
let mermaidLoad = null;
let diagramObserver = null;
const THEME_RERENDER_MS = 120;
/** Начинаем тянуть библиотеку чуть раньше, чем схема доедет до вьюпорта. */
const DIAGRAM_PRELOAD_MARGIN = '300px';
/** Per-diagram pan/zoom state; survives theme re-render without rebinding listeners. */
const diagramUiState = new WeakMap();

function readThemeVars() {
  const mmStyle = getComputedStyle(document.documentElement);
  const mmTok = (name, fb) => (mmStyle.getPropertyValue(name) || '').trim() || fb;
  return {
    darkMode: document.documentElement.getAttribute('data-theme') !== 'light',
    background: mmTok('--color-bg-secondary', '#0d1b22'),
    fontFamily: mmTok('--font-family-body', 'Inter, system-ui, sans-serif'),
    primaryColor: mmTok('--color-surface-soft', '#102029'),
    primaryTextColor: mmTok('--color-text-primary', '#eef6f5'),
    primaryBorderColor: mmTok('--color-border-strong', '#335463'),
    secondaryColor: mmTok('--color-bg-tertiary', '#12252e'),
    secondaryTextColor: mmTok('--color-text-primary', '#eef6f5'),
    secondaryBorderColor: mmTok('--color-border-strong', '#335463'),
    tertiaryColor: mmTok('--color-bg-tertiary', '#12252e'),
    tertiaryTextColor: mmTok('--color-text-primary', '#eef6f5'),
    tertiaryBorderColor: mmTok('--color-border-strong', '#335463'),
    mainBkg: mmTok('--color-surface-soft', '#102029'),
    nodeBorder: mmTok('--color-border-strong', '#335463'),
    nodeTextColor: mmTok('--color-text-primary', '#eef6f5'),
    lineColor: mmTok('--color-text-tertiary', '#8397a0'),
    edgeLabelBackground: mmTok('--color-bg-secondary', '#0d1b22'),
    clusterBkg: mmTok('--color-bg-secondary', '#0d1b22'),
    clusterBorder: mmTok('--color-border-strong', '#335463'),
    titleColor: mmTok('--color-text-primary', '#eef6f5'),
    labelColor: mmTok('--color-text-primary', '#eef6f5'),
    labelTextColor: mmTok('--color-text-primary', '#eef6f5'),
    textColor: mmTok('--color-text-secondary', '#a8b9bd'),
    accent: mmTok('--color-accent-primary', '#39d0c7')
  };
}

/**
 * Семантические классы узлов: фон-подложка, контурный цвет статуса, текст темы.
 * Импорт (DiagramPaletteNormalizer) заменяет захардкоженные в markdown цвета на
 * `class <узел> dgm<Semantic>` — конкретные значения подставляются здесь, из тех же
 * токенов, что и остальная тема, поэтому диаграмма переключается вместе с ней.
 */
const DIAGRAM_CLASS_TOKENS = {
  dgmDanger: ['--color-status-error-wash', '--color-status-error'],
  dgmWarning: ['--color-status-warning-wash', '--color-status-warning'],
  dgmSuccess: ['--color-status-success-wash', '--color-status-success'],
  dgmInfo: ['--color-status-info-wash', '--color-status-info'],
  dgmAccent: ['--color-accent-wash', '--color-accent-primary'],
  dgmNeutral: ['--color-bg-tertiary', '--color-border-strong']
};

const DIAGRAM_CLASS_REFERENCE = /\bdgm(?:Danger|Warning|Success|Info|Accent|Neutral)\b/;

/**
 * Дописывает в диаграмму определения семантических классов.
 *
 * Mermaid принимает `classDef` только после строки с типом диаграммы, поэтому
 * вставляем сразу за первой содержательной строкой, а не в начало. Диаграммы без
 * семантических классов не трогаем: `classDef` валиден не во всех типах графов,
 * и лишняя строка сломала бы, например, sequenceDiagram.
 */
function withSemanticClassDefs(source) {
  if (!DIAGRAM_CLASS_REFERENCE.test(source)) return source;
  const style = getComputedStyle(document.documentElement);
  const token = (name) => (style.getPropertyValue(name) || '').trim();
  const ink = token('--color-text-primary') || '#eef6f5';

  const defs = Object.entries(DIAGRAM_CLASS_TOKENS)
    .filter(([name]) => new RegExp('\\b' + name + '\\b').test(source))
    .map(([name, [fill, stroke]]) => '  classDef ' + name
      + ' fill:' + token(fill) + ',stroke:' + token(stroke)
      + ',color:' + ink + ',stroke-width:1.5px');
  if (!defs.length) return source;

  const lines = source.split('\n');
  const head = lines.findIndex((l) => l.trim() && !l.trim().startsWith('%%'));
  if (head < 0) return source;
  lines.splice(head + 1, 0, ...defs);
  return lines.join('\n');
}

function stripScriptsFromSvg(svg) {
  if (!svg || svg.indexOf('<script') < 0) return svg;
  try {
    const doc = new DOMParser().parseFromString(svg, 'image/svg+xml');
    doc.querySelectorAll('script').forEach((node) => node.remove());
    return new XMLSerializer().serializeToString(doc.documentElement);
  } catch (_) {
    return svg.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
  }
}

function scopedCleanupMermaidArtifacts(root) {
  const scope = root && root.querySelectorAll ? root : document;
  scope.querySelectorAll('[id^="dmmd-"]').forEach((n) => n.remove());
  scope.querySelectorAll('.error-icon').forEach((n) => {
    const p = n.closest('div');
    if (p) p.remove();
  });
}

export function failDiagram(el) {
  const qd = el.closest('.question-diagram');
  const details = qd ? qd.querySelector('.diagram-source-details') : null;
  if (qd && details) {
    qd.setAttribute('data-render-failed', 'true');
    el.textContent = '';
    details.open = true;
  } else if (qd) {
    qd.remove();
  } else {
    el.remove();
  }
}

function applySvgToElement(el, svgString) {
  const parsed = new DOMParser().parseFromString(svgString, 'text/html');
  const svg = parsed.body.querySelector('svg');
  if (!svg) return false;
  svg.removeAttribute('height');
  svg.style.width = '100%';
  svg.style.height = 'auto';
  svg.style.maxHeight = '100%';
  el.replaceChildren(document.importNode(svg, true));
  return true;
}

/**
 * Догружает mermaid по требованию. Резолвится в `true`, если глобальный `mermaid`
 * доступен, и в `false`, если библиотеки нет и не будет (нет meta, сеть, CSP).
 *
 * Инъекция без nonce — сознательно: политика разрешает `script-src 'self'`, а тег
 * создан кодом, который сам уже прошёл проверку. Пробрасывать сюда nonce значило бы
 * копировать его в ещё одно место без выигрыша в безопасности.
 */
function loadMermaid() {
  if (typeof mermaid !== 'undefined') return Promise.resolve(true);
  if (mermaidLoad) return mermaidLoad;

  const meta = document.querySelector('meta[name="mermaid-src"]');
  const src = meta && meta.getAttribute('content');
  if (!src) return Promise.resolve(false);

  mermaidLoad = new Promise((resolve) => {
    const script = document.createElement('script');
    script.src = src;
    script.onload = () => resolve(typeof mermaid !== 'undefined');
    script.onerror = () => resolve(false);
    document.head.appendChild(script);
  });
  return mermaidLoad;
}

/**
 * Пока библиотека едет, блок обязан объяснить паузу, а не стоять пустым.
 *
 * Заодно убираем из элемента сырой текст графа: до отрисовки он невидим, но
 * держит высоту в несколько экранов, и подпись уезжала бы под этот пустой столб.
 * Источник не теряется — он уже лежит в data-mermaid-source, откуда его и читает
 * рендер.
 */
function markDiagramLoading(scope, loading) {
  scope.querySelectorAll('.mermaid:not([data-rendered="true"])').forEach((el) => {
    if (!loading) {
      el.removeAttribute('data-diagram-loading');
      el.removeAttribute('aria-busy');
      return;
    }
    if (!el.getAttribute('data-mermaid-source') && el.textContent) {
      el.setAttribute('data-mermaid-source', el.textContent);
    }
    el.textContent = '';
    el.setAttribute('data-diagram-loading', 'true');
    el.setAttribute('aria-busy', 'true');
  });
}

/**
 * Схема лежит во вкладке, которую ещё не успели развести?
 *
 * До инициализации вкладок обе панели раскрыты (прогрессивное улучшение: без JS
 * пользователь обязан видеть и код, и схему). Модуль вкладок приезжает роутовым
 * чанком, то есть позже, и в этом окне закрытая по итогу схема выглядит видимой.
 * Такие блоки пропускаем — их поставит в очередь событие `contenttabsready`.
 */
function isAwaitingTabs(el) {
  const materials = el.closest('.question-materials');
  return !!materials
    && !!materials.querySelector('.content-tablist')
    && materials.getAttribute('data-content-tabs-ready') !== 'true';
}

/**
 * Ставит схемы в очередь на ленивую отрисовку.
 *
 * Скрытая вкладка «Схема» — `display: none`, то есть нулевой прямоугольник, и
 * IntersectionObserver молчит до тех пор, пока её не откроют. Ровно этого мы и
 * хотим: 2.7 МБ платятся за просмотренную схему, а не за существующую.
 */
export function observeDiagrams(root) {
  const scope = root && root.querySelectorAll ? root : document;
  const els = Array.from(scope.querySelectorAll('.mermaid:not([data-rendered="true"])'))
    .filter((el) => !isAwaitingTabs(el));
  if (!els.length) return;

  if (typeof IntersectionObserver === 'undefined') {
    renderAllMermaid(false, scope);
    return;
  }
  if (!diagramObserver) {
    diagramObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        observer.unobserve(entry.target);
        renderAllMermaid(false, entry.target.closest('.question-diagram') || entry.target.parentElement);
      });
    }, { rootMargin: DIAGRAM_PRELOAD_MARGIN });
  }
  els.forEach((el) => diagramObserver.observe(el));
}

export function initMermaid() {
  if (typeof mermaid === 'undefined') return;
  mermaid.initialize({
    startOnLoad: false,
    securityLevel: 'strict',
    theme: 'base',
    themeVariables: readThemeVars()
  });
  mermaidReady = true;
}

export async function renderAllMermaid(force, root) {
  const scope = root && root.querySelectorAll ? root : document;
  const els = scope.querySelectorAll('.mermaid');
  if (!els.length) return;

  markDiagramLoading(scope, true);
  const loaded = await loadMermaid();
  markDiagramLoading(scope, false);
  if (!loaded) {
    els.forEach(failDiagram);
    return;
  }
  if (!mermaidReady) initMermaid();

  for (const el of els) {
    if (!force && el.getAttribute('data-rendered') === 'true') continue;
    let source = el.getAttribute('data-mermaid-source');
    if (!source) {
      source = el.textContent;
      el.setAttribute('data-mermaid-source', source);
    }
    try {
      const id = 'mmd-' + Math.random().toString(36).slice(2);
      const res = await mermaid.render(id, withSemanticClassDefs(source));
      const safeSvg = stripScriptsFromSvg(res.svg);
      if (applySvgToElement(el, safeSvg)) {
        el.setAttribute('data-rendered', 'true');
        const qd = el.closest('.question-diagram');
        if (qd) {
          qd.removeAttribute('data-render-failed');
          // Свежий <svg> заменяет старый без inline-transform, но pan/zoom-state
          // в diagramUiState переживает ре-рендер (WeakMap по .question-diagram).
          // После смены темы переносим transform на новый SVG — иначе визуал
          // сбрасывается в scale=1, а следующий zoom прыгает от сохранённого стейта.
          const st = diagramUiState.get(qd);
          if (st && (st.scale !== 1 || st.panX !== 0 || st.panY !== 0)) {
            applyDiagramTransform(qd, st);
          }
        }
      } else {
        failDiagram(el);
      }
    } catch (_) {
      scopedCleanupMermaidArtifacts(el.closest('.question-diagram') || el.parentElement || el);
      failDiagram(el);
    }
  }
  wireDiagramToolbar(scope);
}

export function wireDiagramToolbar(root) {
  const scope = root && root.querySelectorAll ? root : document;

  // One-time document delegation — theme re-render must not multiply handlers (P1).
  if (!toolbarDelegated) {
    toolbarDelegated = true;
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('[data-diagram-action]');
      if (!btn) return;
      const qd = btn.closest('.question-diagram');
      if (!qd) return;
      const action = btn.getAttribute('data-diagram-action');
      const state = ensureDiagramState(qd);
      if (action === 'zoom-in') {
        state.scale = Math.min(2.5, Math.round((state.scale + 0.15) * 100) / 100);
        applyDiagramTransform(qd, state);
      } else if (action === 'zoom-out') {
        state.scale = Math.max(0.5, Math.round((state.scale - 0.15) * 100) / 100);
        applyDiagramTransform(qd, state);
      } else if (action === 'fit') {
        state.scale = 1;
        state.panX = 0;
        state.panY = 0;
        applyDiagramTransform(qd, state);
      } else if (action === 'fullscreen') {
        if (!document.fullscreenElement && qd.requestFullscreen) qd.requestFullscreen();
        else if (document.exitFullscreen) document.exitFullscreen();
      }
    });
    document.addEventListener('keydown', (e) => {
      if (e.key !== 'Enter' && e.key !== ' ') return;
      const btn = e.target.closest('[data-diagram-action]');
      if (!btn) return;
      e.preventDefault();
      btn.click();
    });
  }

  // Только mermaid-блоки: зум и панорама двигают SVG, который рисует mermaid.js.
  // У текстовой схемы (ASCII_DIAGRAM) SVG нет — панорама там перехватывала бы
  // pointer-события и мешала выделять текст, ничего при этом не двигая.
  scope.querySelectorAll('.question-diagram[data-content-type="MERMAID"]').forEach((qd) => {
    ensureDiagramState(qd);
    qd.querySelectorAll('[data-diagram-action]').forEach((btn) => {
      if (!btn.hasAttribute('tabindex')) btn.setAttribute('tabindex', '0');
      const actionLabels = {
        'zoom-in': 'Увеличить диаграмму',
        'zoom-out': 'Уменьшить диаграмму',
        fit: 'Вписать диаграмму',
        fullscreen: 'Диаграмма на весь экран'
      };
      const action = btn.getAttribute('data-diagram-action');
      if (action && actionLabels[action] && !btn.getAttribute('aria-label')) {
        btn.setAttribute('aria-label', actionLabels[action]);
      }
    });

    if (qd.getAttribute('data-pan-ready') === 'true') return;
    qd.setAttribute('data-pan-ready', 'true');
    const viewport = qd.querySelector('.diagram-viewport') || qd;
    let dragging = false;
    let startX = 0;
    let startY = 0;
    let originX = 0;
    let originY = 0;

    viewport.addEventListener('pointerdown', (e) => {
      if (e.button !== 0) return;
      if (e.target.closest('button, a, summary, details')) return;
      const state = ensureDiagramState(qd);
      dragging = true;
      startX = e.clientX;
      startY = e.clientY;
      originX = state.panX;
      originY = state.panY;
      viewport.classList.add('is-panning');
      try { viewport.setPointerCapture(e.pointerId); } catch (_) {}
    });
    viewport.addEventListener('pointermove', (e) => {
      if (!dragging) return;
      const state = ensureDiagramState(qd);
      state.panX = originX + (e.clientX - startX);
      state.panY = originY + (e.clientY - startY);
      applyDiagramTransform(qd, state);
    });
    function endPan(e) {
      if (!dragging) return;
      dragging = false;
      viewport.classList.remove('is-panning');
      try { viewport.releasePointerCapture(e.pointerId); } catch (_) {}
    }
    viewport.addEventListener('pointerup', endPan);
    viewport.addEventListener('pointercancel', endPan);
  });
}

function ensureDiagramState(qd) {
  let state = diagramUiState.get(qd);
  if (!state) {
    state = { scale: 1, panX: 0, panY: 0 };
    diagramUiState.set(qd, state);
  }
  return state;
}

function applyDiagramTransform(qd, state) {
  const viewport = qd.querySelector('.diagram-viewport') || qd;
  viewport.setAttribute('data-diagram-scale', String(state.scale));
  const svg = qd.querySelector('.mermaid svg');
  if (svg) {
    svg.style.transform = 'translate(' + state.panX + 'px,' + state.panY + 'px) scale(' + state.scale + ')';
    svg.style.transformOrigin = 'center top';
  }
}

function scheduleThemeRerender() {
  // Смена темы не повод грузить библиотеку: если ни одной схемы ещё не открывали,
  // перекрашивать нечего, а тянуть 2.7 МБ ради невидимого элемента — тем более.
  if (typeof mermaid === 'undefined') return;
  clearTimeout(themeRerenderTimer);
  themeRerenderTimer = setTimeout(() => {
    initMermaid();
    document.querySelectorAll('.mermaid').forEach((el) => {
      if (!el.getAttribute('data-mermaid-source') && el.textContent) {
        el.setAttribute('data-mermaid-source', el.textContent);
      }
      el.removeAttribute('data-rendered');
    });
    // Keep data-pan-ready / delegation — do not rebind listeners on theme change.
    renderAllMermaid(true);
  }, THEME_RERENDER_MS);
}

function bootMermaidModule() {
  window.initMermaid = initMermaid;
  window.renderAllMermaid = renderAllMermaid;

  document.addEventListener('DOMContentLoaded', () => observeDiagrams(document));
  document.addEventListener('contenttabsready', (e) => observeDiagrams(e.detail && e.detail.root));
  document.addEventListener('themechange', scheduleThemeRerender);

  if (typeof MutationObserver !== 'undefined') {
    let lastTheme = document.documentElement.getAttribute('data-theme');
    new MutationObserver(() => {
      const t = document.documentElement.getAttribute('data-theme');
      if (t !== lastTheme) {
        lastTheme = t;
        scheduleThemeRerender();
      }
    }).observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
  }
}

bootMermaidModule();
