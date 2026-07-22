/** Small DOM helpers shared across modules. */

export function icon(name, extraClass) {
  return '<svg class="ed-icon' + (extraClass ? ' ' + extraClass : '') +
    '" aria-hidden="true"><use href="#i-' + name + '"></use></svg>';
}

export function setProgressValue(element, value) {
  if (!element) return;
  const numeric = Number.parseFloat(String(value).replace(',', '.'));
  const safe = Number.isFinite(numeric) ? Math.max(0, Math.min(100, numeric)) : 0;
  element.setAttribute('data-progress', safe.toFixed(1));
  element.style.width = safe.toFixed(1) + '%';
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

export function pluralDays(n) {
  const mod10 = n % 10;
  const mod100 = n % 100;
  if (mod10 === 1 && mod100 !== 11) return 'день';
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 10 || mod100 >= 20)) return 'дня';
  return 'дней';
}

export function motionReduced() {
  const dm = document.documentElement.getAttribute('data-motion');
  if (dm === 'off') return true;
  if (dm === 'on') return false;
  return !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
}
