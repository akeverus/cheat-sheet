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

export function setInlineAlert(message, kind = 'error') {
  const alertEl = document.getElementById('interview-alert');
  if (!alertEl) return;
  if (kind === 'success') {
    alertEl.setAttribute('role', 'status');
    alertEl.setAttribute('aria-live', 'polite');
  } else {
    alertEl.setAttribute('role', 'alert');
    alertEl.setAttribute('aria-live', 'assertive');
  }
  alertEl.textContent = message;
  alertEl.classList.remove('hidden', 'success');
  if (kind === 'success') alertEl.classList.add('success');
  const r = alertEl.getBoundingClientRect();
  if (r.top < 0 || r.bottom > window.innerHeight) {
    alertEl.scrollIntoView({ behavior: motionReduced() ? 'auto' : 'smooth', block: 'center' });
  }
}

export function clearInlineAlert() {
  const alertEl = document.getElementById('interview-alert');
  if (!alertEl) return;
  alertEl.textContent = '';
  alertEl.classList.add('hidden');
  alertEl.classList.remove('success');
}
