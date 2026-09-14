import { renderDynamicContent } from '../content/dynamic-content.js';
import { isSecondaryControl, isShortcutEventIgnored } from '../core/keyboard.js';

let browseBound = false;
let flashShortcutsBound = false;
let recallEchoBound = false;

const RECALL_ECHO_KEY = 'recall-typed-answer';

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

/** Preserve a draft while typing; the server renders the submitted answer without JS. */
export function initRecallTypedEcho() {
  if (recallEchoBound) return;
  const typedForm = document.querySelector('form.typed-answer-input');
  const echo = document.querySelector('.recall-your-answer');
  const surface = typedForm || echo;
  if (!surface) return;
  recallEchoBound = true;
  const key = surface.dataset.recallKey || surface.dataset.questionId;
  const readStore = () => {
    try { return JSON.parse(sessionStorage.getItem(RECALL_ECHO_KEY) || 'null'); }
    catch (_) { return null; }
  };
  const saved = readStore();
  const matches = saved?.key === key;

  if (typedForm) {
    const field = typedForm.querySelector('textarea[name="typedAnswer"]');
    if (!field) return;
    if (matches) field.value = saved.text;
    const save = () => {
      try { sessionStorage.setItem(RECALL_ECHO_KEY, JSON.stringify({ key, text: field.value })); }
      catch (_) { /* The native field and server submission still work without storage. */ }
    };
    field.addEventListener('input', save);
    typedForm.addEventListener('submit', save);
    // A different question invalidates the previous draft, including after successful grading.
    save();
    return;
  }

  // SSR is authoritative after reveal. The local draft is only a recovery fallback.
  const body = echo.querySelector('.recall-your-answer-body');
  if (body && !body.textContent && matches) body.textContent = saved.text;
  const hasAnswer = !!body?.textContent.trim();
  echo.hidden = !hasAnswer;
  const heading = document.querySelector('.recall-etalon-heading');
  if (heading) heading.hidden = !hasAnswer;
  // Do not clear on submit: an unsuccessful grade must leave the answer available.
}

export function initFlashcardShortcuts() {
  if (flashShortcutsBound) return;
  const phase = document.querySelector('.flashcard-phase');
  if (!phase) return;
  flashShortcutsBound = true;

  const revealBtn = phase.querySelector('.flashcard-reveal-btn');
  const gradeBtns = phase.querySelectorAll('.flashcard-grade-btn');
  document.addEventListener('keydown', (event) => {
    if (isShortcutEventIgnored(event) || isSecondaryControl(event.target)) return;
    if (document.querySelector('.kbd-help-overlay:not(.hidden)')) return;
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
