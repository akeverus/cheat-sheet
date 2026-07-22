import { renderDynamicContent } from '../content/dynamic-content.js';

let browseBound = false;
let flashShortcutsBound = false;

export function initBrowseFlashcardReveal() {
  if (browseBound) return;
  const details = document.getElementById('browse-details');
  const summary = document.getElementById('browse-reveal-btn');
  const answer = document.getElementById('browse-answer');
  if (!details || !summary || !answer) return;
  browseBound = true;

  details.addEventListener('toggle', () => {
    summary.textContent = details.open ? 'Скрыть ответ' : 'Показать ответ';
    if (details.open && !details.dataset.hydrated) {
      details.dataset.hydrated = '1';
      renderDynamicContent(answer);
    }
  });
}

export function initFlashcardShortcuts() {
  if (flashShortcutsBound) return;
  const phase = document.querySelector('.flashcard-phase');
  if (!phase) return;
  flashShortcutsBound = true;

  const revealBtn = phase.querySelector('.flashcard-reveal-btn');
  const gradeBtns = phase.querySelectorAll('.flashcard-grade-btn');
  document.addEventListener('keydown', (event) => {
    if (document.querySelector('.kbd-help-overlay:not(.hidden)')) return;
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)) return;
    if (event.target && (event.key === 'Enter' || event.key === ' ') &&
        ['SUMMARY', 'BUTTON', 'A'].includes(event.target.tagName)) return;
    if (event.metaKey || event.ctrlKey || event.altKey) return;
    if (revealBtn && (event.key === ' ' || event.key === 'Enter')) {
      event.preventDefault();
      revealBtn.click();
      return;
    }
    if (gradeBtns.length > 0 && event.key >= '1' && event.key <= '4') {
      const idx = parseInt(event.key, 10) - 1;
      const btn = gradeBtns[idx];
      if (btn) {
        event.preventDefault();
        btn.click();
      }
    }
  });
}
