/**
 * Ожидание как система (iter-84) — ЕДИНСТВЕННОЕ место, где приложение
 * включает и выключает состояние «идёт работа».
 *
 * До iter-84 это делали пять модулей пятью разными наборами ключей:
 *
 *   form-guards      native `disabled` + aria-busy
 *   export           aria-disabled + aria-busy
 *   interview        native `disabled` + класс .is-submitting + подмена подписи + aria-busy
 *   modals           aria-busy + aria-disabled
 *   mermaid-renderer data-diagram-loading + aria-busy
 *
 * Ни один из пяти не сообщал ничего скринридеру: `aria-busy` на кнопке не
 * объявляется, а живого региона про ожидание в приложении не существовало.
 * Четыре из пяти не говорили ни слова и глазами (единственный сигнал —
 * приглушение), а form-guards гасил кнопку НАТИВНЫМ `disabled` — в Chromium
 * это выбрасывает фокус в <body> (замерено; в WebKit фокус остаётся, то есть
 * дефект ещё и движко-зависимый).
 *
 * Здесь всё это сведено к трём функциям и одному ключу `data-pending`.
 */

const LIVE_REGION_ID = 'pending-status';
const LABEL_SLOT = 'pendingLabelWas';
const WIDTH_SLOT = 'pendingWidthWas';

/**
 * Последняя непустая текстовая нода контрола. Подпись меняем ТОЛЬКО в ней:
 * `textContent = '…'` стёр бы вложенную иконку (`<svg class="ed-icon">`), а
 * её исчезновение и возврат — это дёрганье вёрстки на ровном месте.
 */
function labelNode(el) {
  for (let i = el.childNodes.length - 1; i >= 0; i--) {
    const n = el.childNodes[i];
    if (n.nodeType === Node.TEXT_NODE && n.nodeValue.trim()) return n;
  }
  return null;
}

/** Единый живой регион. Печатается в оболочке; создаём про запас, если его нет. */
function liveRegion() {
  let el = document.getElementById(LIVE_REGION_ID);
  if (el) return el;
  el = document.createElement('p');
  el.id = LIVE_REGION_ID;
  el.className = 'visually-hidden';
  el.setAttribute('role', 'status');
  el.setAttribute('aria-live', 'polite');
  el.setAttribute('aria-atomic', 'true');
  document.body.appendChild(el);
  return el;
}

/** Сказать вслух. Пустая строка гасит регион, не объявляя тишину. */
export function announcePending(message) {
  liveRegion().textContent = message || '';
}

/**
 * Включить ожидание на контроле.
 *
 * @param {Element} el      кнопка или ссылка-действие
 * @param {string}  label   видимая подпись на время ожидания («Проверяю…»)
 * @param {string}  [say]   что объявить скринридеру; по умолчанию — label
 */
export function setPending(el, label, say) {
  if (!el || el.getAttribute('data-pending') === 'action') return;

  // Ширину фиксируем ДО подмены подписи: «Начать тренировку» → «Готовлю…»
  // короче на треть, и без замка соседи в футере действия поехали бы.
  //
  // Мера — `offsetWidth`, а НЕ `getBoundingClientRect().width`: под браузерным
  // масштабом прямоугольник приезжает В ЭКРАННЫХ пикселях (уже умноженных на
  // zoom), а `style.minWidth` читается в ЛОКАЛЬНЫХ и умножается на zoom ещё
  // раз. Замерено на 1440×900, зум 125%: rect=294.56 при offsetWidth=236, и
  // замок 295px разносило до 369px — кнопка в ожидании РОСЛА на 74px и толкала
  // соседей, то есть замок от сдвига сам сдвиг и создавал. Дефект движко-
  // зависимый: в WebKit rect под тем же зумом остаётся 235.65 (локальный), в
  // Chromium — нет. `offsetWidth` в обоих движках и на обоих зумах даёт 236.
  const width = el.offsetWidth;
  if (width > 0) {
    el.dataset[WIDTH_SLOT] = el.style.minWidth || '';
    el.style.minWidth = width + 'px';
  }

  if (label) {
    const node = labelNode(el);
    if (node) {
      el.dataset[LABEL_SLOT] = node.nodeValue;
      node.nodeValue = ' ' + label;
    }
  }

  // Руна — реальный узел: оба псевдоэлемента главных кнопок заняты слоем pixel
  // (стрелка и фаска), и спор за них компонент проиграл бы порядком слоёв.
  const rune = document.createElement('span');
  rune.className = 'pending-rune';
  rune.setAttribute('aria-hidden', 'true');
  el.appendChild(rune);

  el.setAttribute('data-pending', 'action');
  el.setAttribute('aria-busy', 'true');
  // aria-disabled, а НЕ native disabled: нативный атрибут в Chromium уносит
  // фокус в <body>, и человек, нажавший кнопку с клавиатуры, теряет место в
  // документе ровно в тот момент, когда ждёт ответа.
  el.setAttribute('aria-disabled', 'true');
  announcePending(say || label || 'Идёт обработка…');
}

/** Выключить ожидание и вернуть контролу исходный вид. */
export function clearPending(el, say) {
  if (!el || !el.hasAttribute('data-pending')) return;

  const rune = el.querySelector(':scope > .pending-rune');
  if (rune) rune.remove();

  const stored = el.dataset[LABEL_SLOT];
  if (stored !== undefined) {
    const node = labelNode(el);
    if (node) node.nodeValue = stored;
    delete el.dataset[LABEL_SLOT];
  }
  if (el.dataset[WIDTH_SLOT] !== undefined) {
    el.style.minWidth = el.dataset[WIDTH_SLOT];
    delete el.dataset[WIDTH_SLOT];
  }

  el.removeAttribute('data-pending');
  el.removeAttribute('aria-busy');
  el.removeAttribute('aria-disabled');
  if (say !== undefined) announcePending(say);
}

/**
 * Ожидание целой области: скелетон вместо контента (роль `block`).
 * Подпись живёт в атрибуте — её печатает CSS через `content: attr(...)`,
 * так что разметка области не трогается и рендер в неё не вмешивается.
 */
export function setBlockPending(el, label) {
  if (!el) return;
  el.setAttribute('data-pending', 'block');
  el.setAttribute('aria-busy', 'true');
  if (label) el.setAttribute('data-pending-label', label);
}

export function clearBlockPending(el) {
  if (!el) return;
  el.removeAttribute('data-pending');
  el.removeAttribute('aria-busy');
  el.removeAttribute('data-pending-label');
}

let guardBound = false;

/**
 * Повторное действие не должно уходить на сервер.
 *
 * Перехват на фазе capture, а не `pointer-events: none` в стилях: во-первых,
 * снятая интерактивность забирает у контрола курсор `progress` (курсор берётся
 * у предка), во-вторых, она вообще не касается клавиатуры — Enter на кнопке
 * с `pointer-events: none` отправляет форму как ни в чём не бывало.
 */
export function initPendingGuard() {
  if (guardBound) return;
  guardBound = true;
  const swallow = (e) => {
    const el = e.target instanceof Element ? e.target.closest('[data-pending="action"]') : null;
    if (!el) return;
    e.preventDefault();
    e.stopPropagation();
  };
  document.addEventListener('click', swallow, true);
  document.addEventListener('submit', (e) => {
    const form = e.target;
    if (!(form instanceof HTMLFormElement)) return;
    const busy = Array.from(form.elements).some(
      (c) => c.getAttribute && c.getAttribute('data-pending') === 'action');
    if (busy) { e.preventDefault(); e.stopPropagation(); }
  }, true);
}
