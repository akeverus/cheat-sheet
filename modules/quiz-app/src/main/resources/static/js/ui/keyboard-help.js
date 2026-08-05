/**
 * Оверлей «?» со списком горячих клавиш.
 *
 * Список зависит от страницы: на экране вопроса это выбор варианта и оценка
 * флешкарты, в аналитике — переход к поиску и сортировка колонки. Раньше это были
 * две отдельные реализации диалога (фокус-ловушка, inert-фон, Esc) — правки
 * доступности приходилось дублировать. Теперь диалог один, различаются данные.
 */

const HELP_KEY = '?';

/** Экраны, где справка имеет смысл: маркер разметки → набор шорткатов. */
const SURFACES = [
  {
    match: () => !!document.querySelector('.focus-question'),
    shortcuts: [
      { keys: ['1', '2', '…', '9'], desc: 'Выбрать вариант ответа' },
      { keys: ['↑', '↓'], desc: 'Переключить вариант' },
      { keys: ['Enter'], desc: 'Отправить ответ' },
      { keys: ['Space'], desc: 'Раскрыть флешкарту' },
      { keys: ['1', '2', '3', '4'], desc: 'Оценить флешкарту (не помню → отлично)' }
    ]
  },
  {
    match: () => !!document.getElementById('stats-tablist'),
    shortcuts: [
      { keys: ['/'], desc: 'Перейти к поиску по вопросам' },
      { keys: ['Enter', 'Space'], desc: 'Сортировать колонку таблицы (когда в фокусе)' }
    ],
    // «/» — прыжок в поиск. Не работает при открытой справке: фокус ушёл бы за
    // оверлей, и клавиатура с скринридером остались бы в ловушке под модалкой.
    extraKeys: {
      '/': ({ isOpen }) => {
        if (isOpen) return false;
        const search = document.querySelector('.search-input');
        if (!search) return false;
        search.focus();
        return true;
      }
    }
  }
];

const COMMON_SHORTCUTS = [
  { keys: [HELP_KEY], desc: 'Показать / скрыть эту справку' },
  { keys: ['Esc'], desc: 'Закрыть справку' }
];

let bound = false;

/**
 * Текстовые поля глушат хоткеи — там «?» и «/» легитимный ввод. Radio и checkbox
 * текст не принимают: с фокуса на фильтре-чекбоксе справка должна открываться.
 */
function swallowsKeys(target) {
  if (!target) return false;
  if (target.tagName === 'TEXTAREA' || target.tagName === 'SELECT') return true;
  return target.tagName === 'INPUT' && !['radio', 'checkbox'].includes(target.type);
}

export function initKeyboardHelp() {
  if (bound) return;
  const surface = SURFACES.find((s) => s.match());
  if (!surface) return;
  bound = true;

  const shortcuts = [...surface.shortcuts, ...COMMON_SHORTCUTS];
  const overlay = document.createElement('div');
  overlay.className = 'kbd-help-overlay hidden';
  overlay.setAttribute('role', 'dialog');
  overlay.setAttribute('aria-modal', 'true');
  overlay.setAttribute('aria-labelledby', 'kbd-help-title');
  overlay.innerHTML =
    '<div class="kbd-help-modal">' +
    '<h2 id="kbd-help-title">Горячие клавиши</h2>' +
    '<dl class="kbd-help-list">' +
    shortcuts.map((s) =>
      '<dt>' + s.keys.map((k) => '<kbd>' + k + '</kbd>').join(' ') + '</dt>' +
      '<dd>' + s.desc + '</dd>'
    ).join('') +
    '</dl>' +
    '<button type="button" class="kbd-help-close" aria-label="Закрыть"><span aria-hidden="true">×</span></button>' +
    '</div>';
  document.body.appendChild(overlay);
  const closeBtn = overlay.querySelector('.kbd-help-close');

  const isOpen = () => !overlay.classList.contains('hidden');
  let lastFocused = null;
  let inerted = [];

  const close = () => {
    if (!isOpen()) return;
    overlay.classList.add('hidden');
    inerted.forEach((el) => { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
    inerted = [];
    if (lastFocused?.focus) lastFocused.focus();
    lastFocused = null;
  };

  // Фон под модалкой inert+aria-hidden: aria-modal не удерживает курсор скринридера
  // в browse-mode. Запоминаем ровно те узлы, которым проставили, — чтобы не снять
  // атрибут с элемента, скрытого по другой причине.
  const open = () => {
    lastFocused = document.activeElement;
    overlay.classList.remove('hidden');
    inerted = [];
    for (const el of Array.from(document.body.children)) {
      if (el === overlay || el.tagName === 'SCRIPT') continue;
      if (el.hasAttribute('inert') || el.getAttribute('aria-hidden') === 'true') continue;
      el.setAttribute('inert', '');
      el.setAttribute('aria-hidden', 'true');
      inerted.push(el);
    }
    closeBtn.focus();
  };

  overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  closeBtn.addEventListener('click', close);
  overlay.addEventListener('keydown', (e) => {
    if (e.key !== 'Tab') return;
    const focusable = overlay.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
    if (!focusable.length) return;
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
    else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
  });

  document.addEventListener('keydown', (event) => {
    // Esc обрабатываем до проверки поля ввода: открытая модалка должна закрываться
    // с любого фокуса, иначе поле поиска внутри неё стало бы ловушкой.
    if (event.key === 'Escape' && isOpen()) {
      event.preventDefault();
      close();
      return;
    }
    if (swallowsKeys(event.target)) return;
    if (event.key === HELP_KEY) {
      event.preventDefault();
      isOpen() ? close() : open();
      return;
    }
    const extra = surface.extraKeys?.[event.key];
    if (extra && extra({ isOpen: isOpen() })) event.preventDefault();
  });
}
