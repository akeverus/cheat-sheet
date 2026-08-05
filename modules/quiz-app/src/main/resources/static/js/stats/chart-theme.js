import { motionReduced } from '../core/dom.js';

/**
 * Мост между CSS-токенами темы и Chart.js.
 *
 * Chart.js рисует в canvas — bitmap вне досягаемости CSS. Ни каскад, ни
 * `prefers-reduced-motion`, ни переключатель дизайна до него не доходят, поэтому
 * цвета и анимацию читаем из живого CSS вручную и перерисовываем графики сами.
 */

function token(name, fallback) {
  try {
    const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim();
    return value || fallback;
  } catch {
    return fallback;
  }
}

export function chartPalette() {
  return {
    text: token('--color-text-secondary', '#5A5145'),
    grid: token('--color-border-secondary', 'rgba(0,0,0,0.08)'),
    accent: token('--color-accent-primary', '#2EC4B6'),
    surface: token('--color-bg-secondary', '#0d1b22'),
    learned: token('--color-status-success', '#2E6B45'),
    remaining: token('--color-text-tertiary', '#8A8073'),
    accHigh: token('--color-status-success', '#2E6B45'),
    accMid: token('--color-status-warning', '#9A6B00'),
    accLow: token('--color-status-error', '#B3261E')
  };
}

/** Ось «Движение» из настроек — та же, что гасит CSS-анимации. */
export function chartAnimation() {
  return motionReduced() ? false : {};
}

/**
 * Ключ палитры: тема + дизайн. Переключатель дизайна меняет те же
 * `--color-*`-токены, что и тема, поэтому следим за обоими атрибутами.
 */
function paletteKey() {
  const root = document.documentElement;
  return `${root.getAttribute('data-theme')}/${root.getAttribute('data-design')}`;
}

/**
 * Перерисовывать `redraw` при смене палитры или оси «Движение». Без этого оси,
 * бары и легенда остаются в цветах прежней темы до перезагрузки страницы.
 */
export function onChartThemeChange(redraw) {
  let lastKey = paletteKey();
  new MutationObserver(() => {
    const key = paletteKey();
    if (key === lastKey) return;
    lastKey = key;
    redraw();
  }).observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme', 'data-design'] });
  // data-motion палитру не меняет, поэтому MutationObserver выше его пропустил бы;
  // head.html шлёт motionchange — по нему включаем/выключаем анимацию сразу.
  document.addEventListener('motionchange', redraw);
}

/** Chart.js подключается отдельным тегом и может не доехать (офлайн, блокировщик). */
export function chartsAvailable() {
  return typeof Chart !== 'undefined';
}

/**
 * Держит один график живым сквозь смену темы: рисует сейчас и перерисовывает
 * при смене палитры, уничтожая прежний экземпляр (Chart.js не умеет менять
 * цвета уже отрисованного канваса).
 */
export function liveChart(build, resizeKey, boxes) {
  let chart = null;
  let applied = null;
  const render = () => {
    // Ключ снимаем ДО постройки. Тем же замером коробки пользуется и `build`,
    // так что это ровно тот бюджет, с которым график будет нарисован; снятый
    // после — уже другой, см. `onChartResize`.
    applied = resizeKey ? resizeKey() : null;
    if (chart) {
      try { chart.destroy(); } catch { /* уже уничтожен */ }
      chart = null;
    }
    try { chart = build(chartPalette(), chartAnimation()); } catch { /* fallback уже в разметке */ }
  };
  render();
  onChartThemeChange(render);
  // График, чьи подписи выведены из коробки, обязан пересчитаться при её смене:
  // иначе бюджет, снятый в скрытой панели или в неустоявшемся layout, останется
  // снятым навсегда.
  if (resizeKey) onChartResize({ current: resizeKey, applied: () => applied, redraw: render, boxes });
}

/**
 * Коробка, по которой график размеряется.
 *
 * Меряем родителя, а не канвас: канвас без атрибутов `width`/`height` имеет
 * собственные 300×150, и до того как Chart.js его отмерит, `getBoundingClientRect`
 * возвращает именно этот интринсик — правдоподобное число, тихо портящее любую
 * формулу, выведенную из размера.
 *
 * `null` означает «размер сейчас неизвестен», а не «ноль». Так бывает штатно:
 * графики создаются до `initStatsTabs`, и панель, которая через миг станет
 * скрытой, ещё участвует в потоке — а панель уже скрытая даёт 0×0. Бюджет,
 * посчитанный по такой коробке, был бы мусором; вместо него потребитель обязан
 * снять ограничение и пересчитать по `onChartResize` (переключение вкладки шлёт
 * `resize`, см. `stats/index.js`).
 */
export function chartBox(canvas) {
  const box = (canvas?.parentElement ?? canvas)?.getBoundingClientRect();
  return box && box.width > 0 && box.height > 0 ? box : null;
}

/** Ширина строки шрифтом подписей Chart.js — для бюджета, а не для отрисовки. */
export function labelMeasurer(canvas, fontPx = 12) {
  const ctx = document.createElement('canvas').getContext('2d');
  const family = window.Chart?.defaults?.font?.family || (canvas ? getComputedStyle(canvas).fontFamily : 'sans-serif');
  ctx.font = `${fontPx}px ${family}`;
  return (text) => ctx.measureText(String(text ?? '')).width;
}

/**
 * Пересчитать график, когда изменилась не тема, а доступное место.
 *
 * Слушаем КОРОБКУ, а не окно. `window.resize` не покрывает ни один из случаев,
 * ради которых бюджет вообще пересчитывается: раскрытие скрытой панели (0×0 →
 * настоящий размер), догрузку шрифта и устаканивание layout после первой
 * отрисовки.
 *
 * И сравниваем ДВА БЮДЖЕТА, а не бюджет со снимком «как было при подписке»:
 * `current()` — что коробка говорит сейчас, `applied()` — с чем график
 * фактически построен. Снимок при подписке врёт, потому что Chart.js в
 * конструкторе сам проставляет канвасу размеры и коробка от этого меняется.
 * Замер поймал ровно это: ось дат строилась по коробке 585px (11 подписей из
 * 14), а подписка через строку записывала «эталон» уже с устоявшихся 1027px
 * (14) — расхождение с первой же секунды считалось нормой, и ни поворот экрана,
 * ни переключение вкладки его не чинили.
 *
 * Сравнение бюджетов, а не размеров, заодно страхует от петли: перерисовка
 * приводит `applied()` к `current()`, и второй раз она не запускается.
 */
export function onChartResize({ current, applied, redraw, boxes = [], delay = 150 }) {
  let timer = 0;
  const schedule = () => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      if (current() !== applied()) redraw();
    }, delay);
  };
  window.addEventListener('resize', schedule);
  if (typeof ResizeObserver === 'undefined') return;
  const observer = new ResizeObserver(schedule);
  boxes.filter(Boolean).forEach((el) => observer.observe(el));
}

/** Парсит JSON-блок, встроенный сервером. Пустой/битый → пустой массив. */
export function readJsonBlock(id) {
  const el = document.getElementById(id);
  if (!el) return [];
  try {
    const data = JSON.parse(el.textContent || '[]');
    return Array.isArray(data) ? data : [];
  } catch {
    return [];
  }
}
