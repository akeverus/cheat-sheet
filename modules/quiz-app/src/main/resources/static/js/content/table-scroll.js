/** Скролл-подсказка для контент-таблиц.
 *
 *  Контент-таблицы (`.markdown-content table`) на узких экранах / при широком
 *  содержимом прокручиваются по X — правило `display:block; overflow-x:auto`
 *  в components-prose.css. Две проблемы этого «голого» скролла:
 *   (1) нативные overlay-скроллбары macOS скрыты до момента скролла → нет
 *       визуального сигнала, что справа есть ещё столбцы (правый столбец кажется
 *       обрезанным, а не прокручиваемым);
 *   (2) без `tabindex` скролл-контейнер недостижим с клавиатуры — клавиатурный
 *       пользователь не может доскроллить до скрытых столбцов (WCAG 2.1.1 Keyboard).
 *
 *  Помечаем РЕАЛЬНО переполненные таблицы классом `.is-scrollable` (CSS рисует
 *  стойкий тонкий teal-скроллбар + фокус-кольцо) и делаем их фокусируемыми
 *  (`tabindex=0`). Семантику `<table>` НЕ трогаем (никакого `role=region`, иначе
 *  потерялись бы `role=table` и навигация по строкам/столбцам для скринридеров).
 *  Не-переполненные таблицы класса и tab-стопа не получают; при ресайзе состояние
 *  переоценивается в обе стороны. */

const CONTENT_TABLE_QUERY = '.markdown-content table';

function evaluateTable(table) {
  const overflows = table.scrollWidth - table.clientWidth > 1;
  if (overflows && table.dataset.scrollHint !== 'on') {
    table.dataset.scrollHint = 'on';
    table.classList.add('is-scrollable');
    if (!table.hasAttribute('tabindex')) table.setAttribute('tabindex', '0');
  } else if (!overflows && table.dataset.scrollHint === 'on') {
    delete table.dataset.scrollHint;
    table.classList.remove('is-scrollable');
    table.removeAttribute('tabindex');
  }
}

/** Динамически вставленный контент (AJAX-ответ): container — сам блок с прозой. */
export function enhanceScrollableTables(container) {
  if (!container || typeof container.querySelectorAll !== 'function') return;
  container.querySelectorAll('table').forEach(evaluateTable);
}

function evaluateAllContentTables() {
  document.querySelectorAll(CONTENT_TABLE_QUERY).forEach(evaluateTable);
}

let bound = false;

/** Инициализация на загрузке страницы + переоценка при ресайзе и после загрузки шрифтов. */
export function initScrollableTables() {
  evaluateAllContentTables();
  if (bound) return;
  bound = true;

  // Метрики столбцов зависят от гарнитуры — переоценить, когда реальный шрифт доехал
  // (FOUT мог сузить/расширить таблицу против fallback-шрифта на первом замере).
  if (typeof document !== 'undefined' && document.fonts && document.fonts.ready) {
    document.fonts.ready.then(evaluateAllContentTables).catch(() => { /* некритично */ });
  }

  // Таблица внутри свёрнутого <details> (reveal флешкарты, disclosure-пояснения
  // вариантов на мобильном) на загрузке display:none → замер даёт 0, overflow не
  // виден. Переоцениваем поддерево при раскрытии. `toggle` не всплывает — ловим в
  // capture-фазе на document.
  if (typeof document !== 'undefined' && document.addEventListener) {
    document.addEventListener('toggle', (e) => {
      const d = e.target;
      if (d && d.tagName === 'DETAILS' && d.open) enhanceScrollableTables(d);
    }, true);
  }

  if (typeof window === 'undefined' || !window.addEventListener) return;
  let scheduled = false;
  window.addEventListener('resize', () => {
    if (scheduled) return;
    scheduled = true;
    const run = () => { scheduled = false; evaluateAllContentTables(); };
    if (window.requestAnimationFrame) window.requestAnimationFrame(run);
    else window.setTimeout(run, 150);
  });
}
