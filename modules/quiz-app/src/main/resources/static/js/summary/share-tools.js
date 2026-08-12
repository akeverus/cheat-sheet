import { icon } from '../core/dom.js';

const STATUS_RESET_MS = 4000;

function clean(text) {
  return (text || '').trim().replace(/\s+/g, ' ');
}

function textOf(selector, root = document) {
  const el = root.querySelector(selector);
  return el ? clean(el.textContent) : '';
}

/**
 * Текст итога собираем из отрендеренного DOM, а не из второй серверной модели:
 * так «скопировано» и «видно на экране» не могут разойтись.
 */
function buildShareText() {
  const lines = ['Итоги сессии — Подготовка к собеседованию'];

  // Режим читаем из надзаголовка шапки: с iter-63 он живёт там, а не чипом в
  // карточке счёта. Копия обязана следовать за разметкой — иначе строка «Режим»
  // молча исчезает из буфера, и «скопировано» перестаёт совпадать с экраном.
  const mode = textOf('.page-head .eyebrow');
  if (mode) lines.push(`Режим: ${mode}`);
  const accuracy = textOf('.summary-score-value');
  if (accuracy) lines.push(`Точность сессии: ${accuracy}`);
  const count = textOf('.summary-hero-count');
  if (count) lines.push(count);

  document.querySelectorAll('.summary-metrics .metric').forEach((metric) => {
    const label = textOf('.metric-label', metric);
    const value = textOf('.metric-value', metric);
    if (label && value) lines.push(`${label}: ${value}`);
  });

  document.querySelectorAll('.summary-kpi').forEach((kpi) => {
    const label = textOf('.summary-kpi-label', kpi);
    const value = textOf('.summary-kpi-value', kpi);
    const sub = textOf('.summary-kpi-sub', kpi);
    if (label && value) lines.push(`${label}: ${value}${sub ? ` (${sub})` : ''}`);
  });

  const rows = document.querySelectorAll('.summary-table tbody tr');
  if (rows.length) {
    lines.push('', 'По темам:');
    rows.forEach((row) => {
      const cells = row.querySelectorAll('td');
      if (cells.length < 5) return;
      lines.push(`- ${clean(cells[0].textContent)}: ${clean(cells[2].textContent)}`
        + `/${clean(cells[1].textContent)} (${clean(cells[4].textContent)})`);
    });
  }

  const plan = document.querySelectorAll('.summary-next-label');
  if (plan.length) {
    lines.push('', 'Что дальше:');
    plan.forEach((el) => lines.push(`- ${clean(el.textContent)}`));
  }

  return lines.join('\n');
}

function copyButton(status) {
  // Без Clipboard API кнопка не появляется вовсе: неработающая кнопка хуже её
  // отсутствия — текст всё равно можно выделить и скопировать руками.
  if (!navigator.clipboard?.writeText) return null;
  const button = document.createElement('button');
  button.type = 'button';
  button.className = 'btn secondary-btn';
  button.innerHTML = `${icon('copy')} Скопировать итог`;
  let resetTimer = null;
  button.addEventListener('click', async () => {
    try {
      await navigator.clipboard.writeText(buildShareText());
      status.textContent = 'Итог скопирован в буфер обмена.';
    } catch {
      status.textContent = 'Не удалось скопировать — выдели текст вручную.';
    }
    clearTimeout(resetTimer);
    resetTimer = setTimeout(() => { status.textContent = ''; }, STATUS_RESET_MS);
  });
  return button;
}

/**
 * Кнопки «Скопировать итог» и «Печать / PDF» под карточкой результата.
 *
 * Инжектим из JS: обе бесполезны без него. Печать опирается на @media print,
 * которое прячет навигацию и оставляет чистый лист.
 */
export function initSummaryTools() {
  // Якорь — план «Что дальше», а не карточка счёта. Инструменты вставлялись
  // СРАЗУ ПОСЛЕ счёта, то есть между результатом и главным действием: на экране
  // итогов первым в порядке чтения и обхода табом оказывалось «Скопировать итог»,
  // а «Разобрать ошибки: 6» — третьим. Это ровно перевёрнутая иерархия: копия и
  // печать нужны единицам и потом, разбор ошибок — то, ради чего экран открыт.
  // Фолбэк на карточку счёта оставлен: без плана (пустой nextActions) якоря нет.
  const anchor = document.querySelector('.summary-actions') || document.querySelector('.summary-hero');
  if (!anchor || document.querySelector('.summary-tools')) return;

  const tools = document.createElement('div');
  tools.className = 'summary-tools';

  const status = document.createElement('span');
  status.className = 'summary-tools-status';
  status.setAttribute('role', 'status');
  status.setAttribute('aria-live', 'polite');

  const copy = copyButton(status);
  if (copy) tools.appendChild(copy);

  const print = document.createElement('button');
  print.type = 'button';
  print.className = 'btn secondary-btn';
  print.innerHTML = `${icon('printer')} Печать / PDF`;
  print.addEventListener('click', () => window.print());
  tools.appendChild(print);

  tools.appendChild(status);
  anchor.insertAdjacentElement('afterend', tools);
}
