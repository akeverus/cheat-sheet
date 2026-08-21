/**
 * Единственный рендерер Mermaid — и для SSR-фрагментов, и для AJAX-контента.
 *
 * Библиотека грузится лениво (см. loadMermaid): сам модуль весит килобайты, а
 * mermaid.min.js — мегабайты, поэтому платить за него имеет смысл только когда
 * схему действительно смотрят. Точка входа — observeDiagrams; renderAllMermaid
 * остаётся публичной для принудительной перерисовки (смена темы, legacy-хуки
 * window.initMermaid / window.renderAllMermaid).
 */

import { setBlockPending, clearBlockPending } from '../ui/pending.js';

let mermaidReady = false;
let themeRerenderTimer = null;
let toolbarDelegated = false;
let mermaidLoad = null;
let diagramObserver = null;
const THEME_RERENDER_MS = 120;
/** Начинаем тянуть библиотеку чуть раньше, чем схема доедет до вьюпорта. */
const DIAGRAM_PRELOAD_MARGIN = '300px';
/** Per-diagram zoom state; survives theme re-render without rebinding listeners. */
const diagramUiState = new WeakMap();
/**
 * Порог читаемости подписи в схеме. Ниже него масштаб «вписать по ширине» не
 * опускается — иначе схема превращается в узор.
 *
 * Замер (Chromium, 1512×982, окно схемы 550px, случайная выборка 40 схем из 1260
 * в корпусе): медиана масштаба 0.58, у 68% схем подпись выходила ниже 12px, у 40% —
 * ниже 8px, худшая 0.19 (подпись ~3px). Одновременно под окном схемы пустовало
 * 497px на 1512×982 и 807px на 2560×1440: место было, а материал был нечитаем.
 */
const MIN_LABEL_PX = 12;
/** Пределы ручного зума — доля натуральной величины схемы. */
const MIN_MANUAL_SCALE = 0.05;
const MAX_MANUAL_SCALE = 3;
/**
 * Шаг зума — МНОЖИТЕЛЬ, а не слагаемое. Масштабы схем разъезжаются на порядок
 * (замер по выборке: от 0.11 у самой широкой до 1.0), и слагаемое 0.15 означало
 * для одной схемы шаг в 2.4 раза, а для другой — в 1.15.
 */
const ZOOM_STEP = 1.25;
let resizeTimer = null;

function readThemeVars() {
  const mmStyle = getComputedStyle(document.documentElement);
  const mmTok = (name, fb) => (mmStyle.getPropertyValue(name) || '').trim() || fb;
  return {
    darkMode: document.documentElement.getAttribute('data-theme') !== 'light',
    background: mmTok('--color-bg-secondary', '#1e1b16'),
    fontFamily: mmTok('--font-family-body', 'Inter, system-ui, sans-serif'),
    primaryColor: mmTok('--color-surface-soft', '#221e18'),
    primaryTextColor: mmTok('--color-text-primary', '#f2ece0'),
    primaryBorderColor: mmTok('--color-border-strong', '#574e3f'),
    secondaryColor: mmTok('--color-bg-tertiary', '#12252e'),
    secondaryTextColor: mmTok('--color-text-primary', '#f2ece0'),
    secondaryBorderColor: mmTok('--color-border-strong', '#574e3f'),
    tertiaryColor: mmTok('--color-bg-tertiary', '#12252e'),
    tertiaryTextColor: mmTok('--color-text-primary', '#f2ece0'),
    tertiaryBorderColor: mmTok('--color-border-strong', '#574e3f'),
    mainBkg: mmTok('--color-surface-soft', '#221e18'),
    nodeBorder: mmTok('--color-border-strong', '#574e3f'),
    nodeTextColor: mmTok('--color-text-primary', '#f2ece0'),
    lineColor: mmTok('--color-text-tertiary', '#9d927e'),
    edgeLabelBackground: mmTok('--color-bg-secondary', '#1e1b16'),
    clusterBkg: mmTok('--color-bg-secondary', '#1e1b16'),
    clusterBorder: mmTok('--color-border-strong', '#574e3f'),
    titleColor: mmTok('--color-text-primary', '#f2ece0'),
    labelColor: mmTok('--color-text-primary', '#f2ece0'),
    labelTextColor: mmTok('--color-text-primary', '#f2ece0'),
    textColor: mmTok('--color-text-secondary', '#bcb19c'),
    accent: mmTok('--color-accent-primary', '#d0a33c')
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
  const ink = token('--color-text-primary') || '#f2ece0';

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

/** Имя, под которым стили mermaid переносятся мимо парсера. */
const CARRIED_STYLE = 'data-mmd-style';

/**
 * Возвращает перенесённые стили через CSSOM.
 *
 * <p>Политика приложения строгая: {@code style-src-attr 'none'} — то есть атрибут
 * {@code style} в РАЗМЕТКЕ запрещён. CSSOM под эту директиву не подпадает:
 * присваивание {@code element.style} — не разбор атрибута, а работа с объектной
 * моделью, и браузер её пропускает. Тем же приёмом в приложении уже живут
 * прогресс-полосы ({@code core/dom.js}: {@code style.setProperty('--progress')}).
 */
function restoreCarriedStyles(root) {
  const carriers = [root, ...root.querySelectorAll('[' + CARRIED_STYLE + ']')];
  carriers.forEach((node) => {
    const declarations = node.getAttribute(CARRIED_STYLE);
    if (!declarations) return;
    node.style.cssText = declarations;
    node.removeAttribute(CARRIED_STYLE);
  });
}

/**
 * Вставляет отрисованную mermaid SVG-строку в элемент.
 *
 * <p>ЗАМЕР, ИЗ КОТОРОГО ВЫРОСЛА ПОДМЕНА ИМЕНИ АТРИБУТА. На вопросе со схемой
 * консоль давала 114 нарушений CSP в Chromium и 24 в WebKit («Applying inline style
 * violates…»): mermaid проставляет на свои узлы атрибуты {@code style}, а политика
 * приложения их запрещает. Нарушения были не только шумом — объявления
 * ОТБРАСЫВАЛИСЬ: A/B-прогон с {@code bypassCSP} дал ту же схему размером 538×590
 * против 550×604 с политикой, потому что маркеры стрелок теряли
 * {@code stroke-width} и {@code stroke-dasharray}.
 *
 * <p>Поэтому атрибут переименовывается ДО разбора: парсер не видит ни одного
 * {@code style=}, нарушать нечего, а объявления возвращаются на место через CSSOM
 * уже в живом документе. Порядок важен: сначала стили mermaid, потом наши размеры —
 * иначе перенос затёр бы ширину и высоту, которые задаёт страница.
 */
function applySvgToElement(el, svgString) {
  const carried = svgString.replace(/\sstyle="/g, ' ' + CARRIED_STYLE + '="');
  const parsed = new DOMParser().parseFromString(carried, 'text/html');
  const svg = parsed.body.querySelector('svg');
  if (!svg) return false;
  const imported = document.importNode(svg, true);
  imported.removeAttribute('height');
  restoreCarriedStyles(imported);
  // Ширину ставит sizeDiagram — в пикселях, а не в процентах: масштаб схемы это
  // раскладка (окно тогда прокручивается нативно), а не `transform`, который
  // рисует крупно поверх коробки прежнего размера.
  imported.style.width = '100%';
  imported.style.height = 'auto';
  el.replaceChildren(imported);
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
/**
 * ОКНО РЕНДЕРА: инлайновые стили mermaid идут через CSSOM, а не атрибутом.
 *
 * Политика `style-src-attr 'none'` запрещает атрибутные стили, и mermaid во
 * временном контейнере рендера давал 125 нарушений на вопрос — консоль тонула в
 * шуме, а настоящие ошибки в нём. Замер по путям (iter-159): 45 нарушений —
 * атрибутные записи стиля, остальные 80 — d3-шные `setAttributeNS` и парсинг
 * `innerHTML` со `style="…"`. В ИТОГОВОМ DOM отброшенного нет (carry-перенос
 * возвращает всё при вставке строки SVG) — дефект именно консольный.
 *
 * Маршрут через `style.cssText` политикой разрешён (проверено трёхвариантным
 * экспериментом) и семантически эквивалентен: те же объявления, тот же
 * сериализованный атрибут — carry-перенос ниже работает как работал.
 *
 * Патчи действуют ТОЛЬКО НА ОКНО РЕНДЕРА и снимаются в finally. Глобальный патч
 * ослабил бы политику для всего приложения: `style-src-attr 'none'` — защита от
 * атрибутной инъекции стилей в пользовательском контенте, и включать обход для
 * чужих innerHTML нельзя.
 */
async function withInlineStylesRouted(work) {
  const proto = Element.prototype;
  const setAttr = proto.setAttribute;
  const setAttrNS = proto.setAttributeNS;
  const innerDesc = Object.getOwnPropertyDescriptor(proto, 'innerHTML');
  const viaCssom = (el, value) => {
    try {
      el.style.cssText = value;
      return true;
    } catch (_) {
      return false; // экзотический хозяин без CSSOM — обычный путь
    }
  };
  proto.setAttribute = function (name, value) {
    if (name === 'style' && typeof value === 'string' && viaCssom(this, value)) return;
    return setAttr.call(this, name, value);
  };
  proto.setAttributeNS = function (ns, name, value) {
    if (!ns && name === 'style' && typeof value === 'string' && viaCssom(this, value)) return;
    return setAttrNS.call(this, ns, name, value);
  };
  Object.defineProperty(proto, 'innerHTML', {
    ...innerDesc,
    set(value) {
      if (typeof value === 'string' && value.indexOf(' style="') !== -1) {
        innerDesc.set.call(this, value.replace(/\sstyle="/g, ' ' + CARRIED_STYLE + '="'));
        restoreCarriedStyles(this);
        return;
      }
      return innerDesc.set.call(this, value);
    },
  });
  // Последний путь — DOMPurify внутри mermaid: он парсит строку DOMParser-ом в
  // ИНЕРТНЫЙ документ, и Chromium репортит нарушение на каждый style-атрибут,
  // хотя в инертном документе стили даже не применяются (цель события —
  // HTMLDocument; замер фазовой пробой). Переименовываем атрибут ДО парса —
  // нарушать нечего; в итоговую строку атрибут едет под carry-именем, и его
  // возвращает тот же restoreCarriedStyles в applySvgToElement, что и остальные. */
  const parseFrom = DOMParser.prototype.parseFromString;
  DOMParser.prototype.parseFromString = function (text, type, ...rest) {
    if (typeof text === 'string' && text.indexOf(' style="') !== -1) {
      text = text.replace(/\sstyle="/g, ' ' + CARRIED_STYLE + '="');
    }
    return parseFrom.call(this, text, type, ...rest);
  };
  try {
    return await work();
  } finally {
    proto.setAttribute = setAttr;
    proto.setAttributeNS = setAttrNS;
    Object.defineProperty(proto, 'innerHTML', innerDesc);
    DOMParser.prototype.parseFromString = parseFrom;
  }
}

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
      clearBlockPending(el);
      return;
    }
    if (!el.getAttribute('data-mermaid-source') && el.textContent) {
      el.setAttribute('data-mermaid-source', el.textContent);
    }
    el.textContent = '';
    // Роль «занята область»: у схемы ждёт не контрол, а место под неё, и
    // показывать это обязан скелетон, а не одна моно-строка (iter-84).
    setBlockPending(el, 'Готовим схему…');
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
      const res = await withInlineStylesRouted(() => mermaid.render(id, withSemanticClassDefs(source)));
      const safeSvg = stripScriptsFromSvg(res.svg);
      if (applySvgToElement(el, safeSvg)) {
        el.setAttribute('data-rendered', 'true');
        const qd = el.closest('.question-diagram');
        if (qd) qd.removeAttribute('data-render-failed');
        // Свежий <svg> приходит без наших размеров, а состояние зума в
        // diagramUiState переживает ре-рендер (WeakMap по элементу схемы).
        // sizeDiagram и назначает масштаб по умолчанию, и возвращает ручной:
        // при `auto: false` он берёт сохранённый `scale` как есть. Схемы в разборе
        // (проза) идут тем же путём — материал один и тот же.
        sizeDiagram(el);
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
      const diagram = qd.querySelector('.mermaid');
      if (!diagram) return;
      const state = ensureDiagramState(diagram);
      if (action === 'zoom-in') {
        state.auto = false;
        state.scale = zoomed(state.scale, ZOOM_STEP);
        applyDiagramSize(diagram, state);
      } else if (action === 'zoom-out') {
        state.auto = false;
        state.scale = zoomed(state.scale, 1 / ZOOM_STEP);
        applyDiagramSize(diagram, state);
      } else if (action === 'fit') {
        // «Вписать» — показать схему целиком. Это ручной выбор: он может дать
        // подпись мельче порога, и правило читаемости его не отменяет.
        state.auto = false;
        const svg = diagram.querySelector('svg');
        const natural = svg && naturalSize(svg);
        state.scale = natural ? fitScaleOf(scrollHostOf(diagram), natural) : 1;
        scrollHostOf(diagram).scrollTo({ left: 0, top: 0 });
        applyDiagramSize(diagram, state);
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

    // Мышью схему таскают, а не прокручивают колесом до края — но двигаем мы
    // ПРОКРУТКУ окна, а не сам SVG. Тогда мышь, трекпад, палец, клавиатура и
    // полосы прокрутки двигают одно и то же, и ни один из этих способов не
    // оказывается «вторым», который надо согласовывать с первым.
    viewport.addEventListener('pointerdown', (e) => {
      if (e.button !== 0) return;
      if (e.target.closest('button, a, summary, details')) return;
      dragging = true;
      startX = e.clientX;
      startY = e.clientY;
      originX = viewport.scrollLeft;
      originY = viewport.scrollTop;
      viewport.classList.add('is-panning');
      try { viewport.setPointerCapture(e.pointerId); } catch (_) {}
    });
    viewport.addEventListener('pointermove', (e) => {
      if (!dragging) return;
      viewport.scrollLeft = originX - (e.clientX - startX);
      viewport.scrollTop = originY - (e.clientY - startY);
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

function ensureDiagramState(el) {
  let state = diagramUiState.get(el);
  if (!state) {
    // `auto` — масштаб выбран правилом читаемости и обязан пересчитаться при смене
    // ширины окна. Первое же ручное действие снимает флаг: выбор человека сильнее.
    state = { scale: null, auto: true };
    diagramUiState.set(el, state);
  }
  return state;
}

/** Направление графа берём из исходника: он лежит рядом, в `data-mermaid-source`. */
function isVerticalFlow(el) {
  const source = (el.getAttribute('data-mermaid-source') || '').trim();
  return /^\s*(flowchart|graph)\s+(TD|TB|BT)\b/.test(source);
}

/** Натуральная величина схемы — из `viewBox`, а не из нарисованного прямоугольника. */
function naturalSize(svg) {
  const vb = (svg.getAttribute('viewBox') || '').split(/[\s,]+/).map(Number);
  if (vb.length !== 4 || !(vb[2] > 0) || !(vb[3] > 0)) return null;
  return { width: vb[2], height: vb[3] };
}

/** Ширина, в которую схему можно вписать целиком (без полей окна). */
function fitScaleOf(viewport, natural) {
  const cs = getComputedStyle(viewport);
  const inner = viewport.clientWidth - parseFloat(cs.paddingLeft || 0) - parseFloat(cs.paddingRight || 0);
  return inner > 0 ? inner / natural.width : 1;
}

/**
 * Масштаб по умолчанию — не «сколько влезет», а «сколько можно прочитать».
 *
 * Подпись меряется на натуральной величине: у SVG масштаб равномерный, поэтому
 * высота подписи при масштабе s равна замеренной, умноженной на s. Отсюда
 * требуемый масштаб = MIN_LABEL_PX / замер. Больше натуральной величины схему не
 * растягиваем — размывать нечего, это векторная графика, но и смысла нет.
 */
function legibleScale(svg, natural) {
  const labels = Array.from(svg.querySelectorAll('.nodeLabel, foreignObject div, text'))
    .filter((node) => (node.textContent || '').trim())
    .slice(0, 24);
  if (!labels.length) return 1;
  const previous = svg.style.width;
  svg.style.width = natural.width + 'px';
  // Считаем по САМОЙ МЕЛКОЙ подписи, а не по первой попавшейся: в схеме рядом
  // живут заголовок узла и подпись стрелки, и «читаемо» — про меньшую из них.
  let smallest = Infinity;
  for (const node of labels) {
    const height = node.getBoundingClientRect().height;
    if (height > 0 && height < smallest) smallest = height;
  }
  svg.style.width = previous;
  if (!isFinite(smallest)) return 1;
  return Math.min(1, MIN_LABEL_PX / smallest);
}

/**
 * Ставит схеме размер и приводит окно в состояние честной прокручиваемой области:
 * с клавиатуры доступно, читалке названо, глазу видно, что содержимое шире окна.
 */
/**
 * Прокручиваемая коробка схемы. На экране вопроса это окно материала (у него кап
 * высоты и тулбар), в разборе — сам блок схемы внутри прозы.
 */
function scrollHostOf(el) {
  return el.closest('.diagram-viewport') || el;
}

function applyDiagramSize(el, state) {
  const host = scrollHostOf(el);
  const svg = el.querySelector('svg');
  if (!svg) return;
  const natural = naturalSize(svg);
  if (!natural) return;

  const scale = state.scale || 1;
  svg.style.width = Math.round(natural.width * scale) + 'px';
  svg.style.height = 'auto';
  host.setAttribute('data-diagram-scale', scale.toFixed(2));

  const overflowsX = host.scrollWidth - host.clientWidth > 1;
  const overflowsY = host.scrollHeight - host.clientHeight > 1;

  // С какого места смотреть схему, которая шире окна. У потока слева направо
  // (`graph LR`) начало — это левый край, и он же начало чтения. У дерева сверху вниз
  // (`graph TD`, 464 таких в корпусе против 568 LR) корень стоит ПО ЦЕНТРУ, и левый
  // край показывает лист без корня: на 390px в окне 325 при содержимом 995 первым в
  // кадре оказывался «ArrayDeque (push/pop)», а не вопрос «Что нужно?». Центрируем
  // один раз и только пока масштаб не трогали руками.
  if (overflowsX && state.auto && !state.centered && isVerticalFlow(el)) {
    host.scrollLeft = Math.round((host.scrollWidth - host.clientWidth) / 2);
    state.centered = true;
  }
  if (overflowsX) host.setAttribute('data-diagram-overflow', 'x');
  else host.removeAttribute('data-diagram-overflow');
  // Прокручиваемая область без доступа с клавиатуры — недостижимое содержимое
  // (WCAG 2.1.1). Стоп появляется ровно тогда, когда есть что прокручивать.
  if (overflowsX || overflowsY) {
    host.setAttribute('tabindex', '0');
    host.setAttribute('role', 'group');
    if (!host.getAttribute('aria-label')) {
      host.setAttribute('aria-label', 'Схема — прокручиваемая область');
    }
  } else {
    host.removeAttribute('tabindex');
    host.removeAttribute('role');
    host.removeAttribute('aria-label');
  }
}

/** Пересчитывает масштаб по умолчанию и применяет его. */
function sizeDiagram(el) {
  const svg = el && el.querySelector('svg');
  if (!svg) return;
  const natural = naturalSize(svg);
  if (!natural) return;
  const state = ensureDiagramState(el);
  state.fit = fitScaleOf(scrollHostOf(el), natural);
  if (state.auto || !state.scale) {
    state.scale = Math.min(1, Math.max(state.fit, legibleScale(svg, natural)));
  }
  applyDiagramSize(el, state);
}

function zoomed(scale, factor) {
  const next = (scale || 1) * factor;
  return Math.min(MAX_MANUAL_SCALE, Math.max(MIN_MANUAL_SCALE, Math.round(next * 1000) / 1000));
}

/**
 * Прокрутка окна схемы с клавиатуры.
 *
 * Chromium прокручивает сфокусированную коробку стрелками сам, WebKit — нет: замер
 * на широкой схеме (2871×278) дал `scrollLeft` 0→74 в Chromium и 0→0 в WebKit при
 * фокусе на окне. Своя обработка выравнивает движки и делает шаг предсказуемым.
 */
function handleDiagramKeys(e) {
  const host = e.target;
  if (!host || !host.matches || !host.matches('[data-diagram-overflow], .diagram-viewport[tabindex], .mermaid[tabindex]')) return;
  const stepX = Math.max(48, Math.round(host.clientWidth * 0.2));
  const stepY = Math.max(48, Math.round(host.clientHeight * 0.2));
  const moves = {
    ArrowRight: [stepX, 0], ArrowLeft: [-stepX, 0],
    ArrowDown: [0, stepY], ArrowUp: [0, -stepY],
    PageDown: [0, host.clientHeight], PageUp: [0, -host.clientHeight]
  };
  if (e.key === 'Home' || e.key === 'End') {
    e.preventDefault();
    host.scrollTo({ left: e.key === 'Home' ? 0 : host.scrollWidth, top: host.scrollTop });
    return;
  }
  const move = moves[e.key];
  if (!move) return;
  e.preventDefault();
  host.scrollBy({ left: move[0], top: move[1] });
}

/** Смена ширины окна меняет «вписать» — значит и масштаб по умолчанию. */
function scheduleDiagramResize() {
  clearTimeout(resizeTimer);
  resizeTimer = setTimeout(() => {
    document.querySelectorAll('.mermaid[data-rendered="true"]').forEach((el) => {
      const state = diagramUiState.get(el);
      if (state && !state.auto) return;
      sizeDiagram(el);
    });
  }, 150);
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
  window.addEventListener('resize', scheduleDiagramResize);
  // Полный экран — та же смена доступной ширины, что и ресайз окна: замер показал
  // окно 600 → 1463, а масштаб оставался прежним, пока никто не пересчитает.
  document.addEventListener('fullscreenchange', scheduleDiagramResize);
  document.addEventListener('webkitfullscreenchange', scheduleDiagramResize);
  document.addEventListener('keydown', handleDiagramKeys);
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
