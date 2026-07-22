import { confirmModal } from './modals.js';

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
  const NAV_FORM_SELECTOR = [
    '#session-form',
    'form[action$="/flashcard-grade"]',
    'form[action$="/flashcard-reveal"]',
    'form[action$="/pause"]',
    'form[action$="/finish"]',
    'form[action$="/empty"]',
    'form[action$="/settings/reset-options"]',
    '.danger-zone form'
  ].join(', ');
  document.addEventListener('submit', (e) => {
    const form = e.target;
    if (!(form instanceof HTMLFormElement) || !form.matches(NAV_FORM_SELECTOR)) return;
    if (form.dataset.submitted === '1') { e.preventDefault(); return; }
    form.dataset.submitted = '1';
    const controls = form.querySelectorAll('button[type="submit"], input[type="submit"]');
    setTimeout(() => controls.forEach((c) => { c.disabled = true; c.setAttribute('aria-busy', 'true'); }), 0);
    setTimeout(() => {
      if (!form.isConnected) return;
      form.dataset.submitted = '';
      controls.forEach((c) => { c.disabled = false; c.removeAttribute('aria-busy'); });
    }, 5000);
  });
}
