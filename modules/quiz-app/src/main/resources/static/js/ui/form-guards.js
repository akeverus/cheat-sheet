import { confirmModal } from './modals.js';
import { setPending, clearPending } from './pending.js';

let dangerousBound = false;
let submitOnceBound = false;

export function initDangerousFormGuard() {
  if (dangerousBound) return;
  dangerousBound = true;
  document.querySelectorAll('[data-confirm]').forEach((el) => {
    const form = el.closest('form');
    if (!form) return;
    form.addEventListener('submit', (e) => {
      if (form.dataset.confirmed === '1') { delete form.dataset.confirmed; return; }
      const msg = el.getAttribute('data-confirm');
      if (!msg) return;
      e.preventDefault();
      confirmModal({
        title: el.getAttribute('data-confirm-title') || 'Подтвердите действие',
        message: msg
      }).then((ok) => {
        if (!ok) return;
        form.dataset.confirmed = '1';
        form.requestSubmit();
      });
    });
  });
}

export function initSubmitOnceGuard() {
  if (submitOnceBound) return;
  submitOnceBound = true;
  document.addEventListener('keydown', (event) => {
    if (event.key !== 'Escape' || event.defaultPrevented) return;
    const disclosure = event.target.closest?.('.focus-session-more[open], .summary-more[open]');
    if (!disclosure) return;
    event.preventDefault();
    disclosure.open = false;
    disclosure.querySelector('summary')?.focus();
  });
  const NAV_FORM_SELECTOR = [
    '#session-form',
    'form[action$="/flashcard-grade"]',
    'form[action$="/flashcard-reveal"]',
    'form[action$="/pause"]',
    'form[action$="/finish"]',
    'form[action$="/empty"]',
    'form[action$="/settings/reset-options"]',
    'form[action$="/streak/goal"]',
    'form[action$="/resume"]',
    'form[action$="/pause/discard"]',
    // Вход — такая же навигационная отправка: POST уводит со страницы, и до
    // ответа сервера кнопка обязана показать «идёт», а второй клик не должен
    // порождать вторую попытку аутентификации.
    'form[action$="/login"]',
    '.danger-zone form'
  ].join(', ');
  document.addEventListener('submit', (e) => {
    const form = e.target;
    if (!(form instanceof HTMLFormElement) || !form.matches(NAV_FORM_SELECTOR)) return;
    // Отправку уже отменили — значит её НЕ БЫЛО. Так ведёт себя опасное
    // действие: `initDangerousFormGuard` гасит первый submit и ждёт ответа в
    // модалке, а подтверждение шлёт форму заново через `requestSubmit()`.
    // Без этой проверки первый — отменённый — submit помечал форму как
    // отправленную и включал на кнопке ожидание, после чего повторную отправку
    // глушили сразу двое: строка ниже и capture-гвард ожидания. Замерено:
    // «Завершить сессию» и формы `.danger-zone` не уходили на сервер вовсе —
    // POST /finish в сети отсутствовал, страница молча оставалась на месте.
    if (e.defaultPrevented) return;
    if (form.dataset.submitted === '1') { e.preventDefault(); return; }
    form.dataset.submitted = '1';
    // form.elements, а НЕ querySelectorAll: главная кнопка приложения
    // («Начать тренировку», #session-start-btn) лежит ВНЕ формы и привязана
    // атрибутом form=, поэтому поиск по потомкам её не находил — замерено,
    // гвард видел 0 контролов при одном реально отправляющем. Коллекция
    // form.elements собирается по владению, а не по вложенности.
    const controls = Array.from(form.elements).filter((c) => c.type === 'submit');
    setTimeout(() => controls.forEach(
      (c) => setPending(c, c.dataset.pendingLabel || 'Отправляю…')), 0);
    setTimeout(() => {
      if (!form.isConnected) return;
      form.dataset.submitted = '';
      controls.forEach((c) => clearPending(c, ''));
    }, 5000);
  });
}
