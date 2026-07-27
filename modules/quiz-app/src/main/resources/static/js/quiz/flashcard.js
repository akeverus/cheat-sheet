import { renderDynamicContent } from '../content/dynamic-content.js';

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

/**
 * Typed-recall / code-challenge / interview: серверный /flashcard-reveal НЕ
 * персистит введённый ответ, поэтому после раскрытия эталона пользователю нечего
 * было бы сравнить со своим ответом. Сохраняем его в sessionStorage при сабмите
 * reveal-формы и подставляем в блок «Твой ответ» на раскрытой странице по
 * совпадению questionId. Чистим после оценки, чтобы не протёк в следующий вопрос.
 * Полностью клиентский, без бэкенд-состояния (progressive enhancement).
 */
export function initRecallTypedEcho() {
  if (recallEchoBound) return;
  recallEchoBound = true;

  const readStore = () => {
    try { return JSON.parse(sessionStorage.getItem(RECALL_ECHO_KEY) || 'null'); }
    catch (e) { return null; }
  };

  // 1) Захват ответа при сабмите reveal-формы (до навигации).
  const typedForm = document.querySelector('form.typed-answer-input');
  if (typedForm) {
    typedForm.addEventListener('submit', () => {
      const qid = typedForm.getAttribute('data-question-id');
      const textarea = typedForm.querySelector('textarea[name="typedAnswer"]');
      const text = textarea ? textarea.value : '';
      if (!qid || !text.trim()) return;
      try { sessionStorage.setItem(RECALL_ECHO_KEY, JSON.stringify({ id: qid, text })); }
      catch (e) { /* приватный режим / переполнение — тихо пропускаем */ }
    });
  }

  // 2) Эхо на раскрытой странице, если сохранённый ответ относится к этому вопросу.
  const echo = document.querySelector('.recall-your-answer');
  if (echo) {
    const stored = readStore();
    const curId = echo.getAttribute('data-question-id');
    const hasEcho = stored && String(stored.id) === String(curId) && stored.text && stored.text.trim();
    if (hasEcho) {
      const body = echo.querySelector('.recall-your-answer-body');
      if (body) body.textContent = stored.text; // textContent → экранирование, без XSS
      echo.hidden = false;
    } else {
      // Ввод пропущен → сравнивать не с чем: убираем одинокую метку «Эталон»,
      // чтобы раскрытый ответ выглядел как обычная флешкарта, без висящего лейбла.
      const etalonHeading = document.querySelector('.recall-etalon-heading');
      if (etalonHeading) etalonHeading.hidden = true;
    }
  }

  // 3) Очистка после оценки — ответ отыграл свою роль, дальше он лишний.
  const gradeForm = document.querySelector('.flashcard-grade-buttons form');
  if (gradeForm) {
    gradeForm.addEventListener('submit', () => {
      try { sessionStorage.removeItem(RECALL_ECHO_KEY); }
      catch (e) { /* no-op */ }
    });
  }
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
