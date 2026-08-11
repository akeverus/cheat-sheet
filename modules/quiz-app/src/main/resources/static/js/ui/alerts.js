import { motionReduced } from '../core/dom.js';

let bound = false;

export function initOfflineBanner() {
  if (bound) return;
  bound = true;

  function syncOfflineBanner() {
    const banner = document.getElementById('network-offline-banner');
    if (!banner) return;
    const offline = typeof navigator !== 'undefined' && navigator.onLine === false;
    banner.classList.toggle('hidden', !offline);
    if (offline) banner.removeAttribute('hidden');
    else banner.setAttribute('hidden', '');
  }

  window.addEventListener('online', syncOfflineBanner);
  window.addEventListener('offline', syncOfflineBanner);
  syncOfflineBanner();
}

/**
 * Куда говорить. Страничный слот выигрывает у общего: там, где у сообщения есть
 * законное место рядом с действием (экран вопроса, разбор ответа), оно должно
 * появляться там, а не улетать в начало документа.
 *
 * Поиск по атрибуту, а не по `#interview-alert`, как было до iter-82: тот id
 * печатали ровно две страницы, и на /bookmarks функция молча возвращалась —
 * четыре сообщения об ошибке из quiz/bookmarks.js не доезжали никуда. Общий слот
 * теперь печатает shell-head.html, то есть все семь страниц, и «сказать некуда»
 * стало невозможным состоянием.
 */
function noticeHost() {
  return document.querySelector('[data-notice-host="page"]')
      || document.querySelector('[data-notice-host="shell"]');
}

export function setInlineAlert(message, kind = 'error') {
  const host = noticeHost();
  if (!host) return;
  const success = kind === 'success';
  host.setAttribute('role', success ? 'status' : 'alert');
  host.setAttribute('aria-live', success ? 'polite' : 'assertive');
  host.textContent = message;
  host.classList.remove('hidden');
  host.classList.toggle('notice-success', success);
  // Слот приезжает и с классом, и с атрибутом `hidden`: без JS он не должен
  // занимать место даже до загрузки скинов. Снимать надо оба.
  host.removeAttribute('hidden');
  const r = host.getBoundingClientRect();
  if (r.top < 0 || r.bottom > window.innerHeight) {
    host.scrollIntoView({ behavior: motionReduced() ? 'auto' : 'smooth', block: 'center' });
  }
}

export function clearInlineAlert() {
  const host = noticeHost();
  if (!host) return;
  host.textContent = '';
  host.classList.add('hidden');
  host.classList.remove('notice-success');
  host.setAttribute('hidden', '');
}
