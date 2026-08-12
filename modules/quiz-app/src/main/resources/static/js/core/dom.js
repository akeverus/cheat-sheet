/** Small DOM helpers shared across modules. */

export function icon(name, extraClass) {
  return '<svg class="px-icon' + (extraClass ? ' ' + extraClass : '') +
    '" aria-hidden="true"><use href="#' + name + '"></use></svg>';
}

/**
 * Ставит заполненность полосы прогресса в процентах.
 *
 * Пишем только `--progress`; ось (ширина или высота) — дело CSS. Пока ширину
 * ставил сам JS, полосы делились на два сорта: горизонтальные, где инлайновая
 * ширина работала, и вертикальный тренд точности, которому та же ширина ломала
 * вид, — из-за чего у /stats жила своя копия гидратора с исключением по классу.
 *
 * `value` — всегда число с точкой: из JS приходит Number, из разметки —
 * `data-progress`, который шаблоны печатают через явный POINT-разделитель.
 * Раньше здесь стоял `.replace(',', '.')`, гасивший русскую запятую от
 * `#numbers.formatDecimal` — компенсация вместо починки источника. Источник
 * починен; там, где такой компенсации не было (SSR-дуга кольца итогов,
 * `stroke-dasharray`), запятая ломала рендер молча.
 */
export function setProgressValue(element, value) {
  if (!element) return;
  const numeric = Number.parseFloat(value);
  const safe = Number.isFinite(numeric) ? Math.max(0, Math.min(100, numeric)) : 0;
  element.setAttribute('data-progress', safe.toFixed(1));
  element.style.setProperty('--progress', safe.toFixed(1));
}

export function hydrateProgressBarsFromData(root = document) {
  root.querySelectorAll('[data-progress]').forEach((el) => {
    setProgressValue(el, el.getAttribute('data-progress') || '0');
  });
}

export function escapeHtml(str) {
  if (str == null) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

export function pluralRu(n, one, few, many) {
  const mod10 = n % 10;
  const mod100 = n % 100;
  if (mod10 === 1 && mod100 !== 11) return one;
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return few;
  return many;
}

export function motionReduced() {
  const dm = document.documentElement.getAttribute('data-motion');
  if (dm === 'off') return true;
  if (dm === 'on') return false;
  return !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
}
