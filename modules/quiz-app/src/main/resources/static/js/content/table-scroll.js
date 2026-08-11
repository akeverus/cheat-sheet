/** Скролл-подсказка для таблиц данных.
 *
 *  Все таблицы приложения лежат в общем скролл-контейнере `.table-scroll`
 *  (components-table.css) — и разметочные (/stats, /session-summary), и
 *  прозаические из markdown (обёртку ставит MarkdownRenderService). Контейнер сам
 *  по себе «голый», и у голого скролла две проблемы:
 *   (1) нативные overlay-скроллбары macOS скрыты до момента скролла → нет
 *       визуального сигнала, что справа есть ещё столбцы (правый столбец кажется
 *       обрезанным, а не прокручиваемым);
 *   (2) без `tabindex` скролл-контейнер недостижим с клавиатуры — клавиатурный
 *       пользователь не может доскроллить до скрытых столбцов (WCAG 2.1.1 Keyboard).
 *
 *  Помечаем РЕАЛЬНО переполненные контейнеры классом `.is-scrollable` (CSS рисует
 *  стойкий тонкий акцентный скроллбар + фокус-кольцо), делаем их фокусируемыми
 *  (`tabindex=0`) и даём им имя (`role=region` + `aria-label` из
 *  `data-scroll-label`, иначе — из подписи/`aria-label` таблицы): безымянная
 *  остановка табуляции для скринридера — «группа», о которой нечего сказать.
 *  Семантику самой `<table>` НЕ трогаем — роль и навигация по строкам/столбцам
 *  остаются на таблице, регионом становится обёртка.
 *
 *  Не-переполненные контейнеры ни класса, ни tab-стопа, ни landmark'а не получают:
 *  на широком экране, где таблица укладывается целиком, лишняя остановка табуляции
 *  и лишний регион только удлиняют путь. При ресайзе состояние переоценивается в
 *  обе стороны. */

const SCROLL_QUERY = '.table-scroll';

function scrollLabel(box) {
  if (box.dataset.scrollLabel) return box.dataset.scrollLabel;
  const table = box.querySelector('table');
  if (!table) return null;
  const aria = table.getAttribute('aria-label');
  if (aria) return aria;
  const caption = table.querySelector('caption');
  const text = caption && caption.textContent ? caption.textContent.trim() : '';
  return text || null;
}

function evaluateBox(box) {
  const overflows = box.scrollWidth - box.clientWidth > 1;
  if (overflows && box.dataset.scrollHint !== 'on') {
    box.dataset.scrollHint = 'on';
    box.classList.add('is-scrollable');
    if (!box.hasAttribute('tabindex')) box.setAttribute('tabindex', '0');
    const label = scrollLabel(box);
    if (label && !box.hasAttribute('role')) {
      box.setAttribute('role', 'region');
      box.setAttribute('aria-label', label);
    }
  } else if (!overflows && box.dataset.scrollHint === 'on') {
    delete box.dataset.scrollHint;
    box.classList.remove('is-scrollable');
    box.removeAttribute('tabindex');
    if (box.getAttribute('role') === 'region') {
      box.removeAttribute('role');
      box.removeAttribute('aria-label');
    }
  }
}

/** Динамически вставленный контент (AJAX-ответ): container — сам блок с прозой. */
export function enhanceScrollableTables(container) {
  if (!container || typeof container.querySelectorAll !== 'function') return;
  container.querySelectorAll(SCROLL_QUERY).forEach(evaluateBox);
  if (typeof container.matches === 'function' && container.matches(SCROLL_QUERY)) evaluateBox(container);
}

function evaluateAllScrollBoxes() {
  document.querySelectorAll(SCROLL_QUERY).forEach(evaluateBox);
}

let bound = false;

/** Инициализация на загрузке страницы + переоценка при ресайзе и после загрузки шрифтов. */
export function initScrollableTables() {
  evaluateAllScrollBoxes();
  if (bound) return;
  bound = true;

  // Метрики столбцов зависят от гарнитуры — переоценить, когда реальный шрифт доехал
  // (FOUT мог сузить/расширить таблицу против fallback-шрифта на первом замере).
  if (typeof document !== 'undefined' && document.fonts && document.fonts.ready) {
    document.fonts.ready.then(evaluateAllScrollBoxes).catch(() => { /* некритично */ });
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
    const run = () => { scheduled = false; evaluateAllScrollBoxes(); };
    if (window.requestAnimationFrame) window.requestAnimationFrame(run);
    else window.setTimeout(run, 150);
  });
}
