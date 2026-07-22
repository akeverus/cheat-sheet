let kbdBound = false;

export function initKeyboardHelp() {
  if (kbdBound) return;
  if (!document.querySelector('.focus-question')) return;
  kbdBound = true;

  const shortcuts = [
    { keys: ['1', '2', '…', '9'], desc: 'Выбрать вариант ответа' },
    { keys: ['↑', '↓'], desc: 'Переключить вариант' },
    { keys: ['Enter'], desc: 'Отправить ответ' },
    { keys: ['Space'], desc: 'Раскрыть флешкарту' },
    { keys: ['1', '2', '3', '4'], desc: 'Оценить флешкарту (не помню → отлично)' },
    { keys: ['?'], desc: 'Показать / скрыть эту справку' },
    { keys: ['Esc'], desc: 'Закрыть справку' }
  ];

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

  let lastFocused = null;
  let inerted = [];
  const close = () => {
    if (overlay.classList.contains('hidden')) return;
    overlay.classList.add('hidden');
    inerted.forEach((el) => { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
    inerted = [];
    if (lastFocused?.focus) lastFocused.focus();
    lastFocused = null;
  };
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
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)
        && !['radio', 'checkbox'].includes(event.target.type)) return;
    if (event.key === '?') {
      event.preventDefault();
      overlay.classList.contains('hidden') ? open() : close();
    } else if (event.key === 'Escape' && !overlay.classList.contains('hidden')) {
      event.preventDefault();
      close();
    }
  });
}
