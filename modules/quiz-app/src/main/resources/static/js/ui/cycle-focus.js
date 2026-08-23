/** Передача фокуса через MPA-переход шага цикла (A11Y-11, REF-014).
 *
 *  AJAX-ответ сажает фокус на вердикт, серверный result — autofocus'ом; а вот
 *  GET «Следующий вопрос» и redirect'ы форм цикла (skip, finish → итоги,
 *  resume, оценка флешкарты, старт) оставляли activeElement на BODY:
 *  клавиатурный пользователь после каждого шага начинал Tab-путь с нуля
 *  (skip-link → шапка → навигация), скринридер терял место в цикле (§54).
 *
 *  Механика: перед переходом шага ставится sessionStorage-флаг (клик по
 *  [data-cycle-next] или submit формы цикла), после загрузки фокус садится на
 *  main#main-content (tabindex=-1 уже в разметке всех страниц цикла) и флаг
 *  снимается. Обычные заходы и reload флага не имеют — skip-link остаётся
 *  первым Tab'ом, фокус никто не угоняет. Программный focus() не рисует
 *  :focus-visible-кольцо — это точка отсчёта, а не подсветка. PE: без JS
 *  переходы работают как раньше (браузерное умолчание).
 */

const FLAG = 'cycleFocusMain';

/* Формы шагов цикла: их submit заканчивается redirect'ом на следующий экран
   цикла. Формы с data-confirm (finish, discard) первый submit гасят модалкой —
   defaultPrevented отсекает его, флаг ставит только подтверждённая отправка. */
const CYCLE_FORMS = [
  'form[action$="/skip"]',
  'form[action$="/finish"]',
  'form[action$="/resume"]',
  'form[action$="/pause"]',
  'form[action$="/pause/discard"]',
  'form[action$="/flashcard-grade"]',
  'form[action$="/flashcard-reveal"]',
  'form[action$="/study-confirm"]',
  '#session-form'
].join(', ');

function setFlag() {
  try { sessionStorage.setItem(FLAG, '1'); } catch { /* приватный режим */ }
}

let bound = false;

export function initCycleFocusHandoff() {
  let flagged = false;
  try {
    flagged = sessionStorage.getItem(FLAG) === '1';
    if (flagged) sessionStorage.removeItem(FLAG);
  } catch { /* приватный режим */ }
  if (flagged) {
    /* Уступка точному приземлению страницы (REF-017): у страниц с [autofocus]
       (серверный result — вердикт; reveal-режимы — эталонный ответ, iter-167)
       фокус уже сидит там, где страница решила. Садиться на main поверх — значит
       угонять фокус с точного на общее: живая регрессия typed recall, пойманная
       e2e в обоих движках. Флаг к этому моменту уже снят — рукопожатие съедено. */
    const specific = document.querySelector('[autofocus]');
    if (!specific) {
      const main = document.getElementById('main-content');
      if (main) main.focus();
    }
  }

  if (bound) return;
  bound = true;
  document.addEventListener('click', (e) => {
    if (e.target instanceof Element && e.target.closest('[data-cycle-next]')) setFlag();
  });
  document.addEventListener('submit', (e) => {
    const form = e.target;
    if (form instanceof HTMLFormElement && form.matches(CYCLE_FORMS) && !e.defaultPrevented) setFlag();
  });
}
