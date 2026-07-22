import { apiFetch } from '../core/api-client.js';
import { ADMIN_TOKEN_KEY } from '../core/constants.js';
import { promptModal } from './modals.js';

let exportBound = false;
let exportInFlight = false;

function readStoredToken() {
  try { return localStorage.getItem(ADMIN_TOKEN_KEY) || ''; } catch (_) { return ''; }
}
function storeToken(t) {
  try { localStorage.setItem(ADMIN_TOKEN_KEY, t); } catch (_) {}
}
function clearStoredToken() {
  try { localStorage.removeItem(ADMIN_TOKEN_KEY); } catch (_) {}
}

function parseContentDispositionFilename(header, fallback) {
  if (!header) return fallback;
  const star = /filename\*=UTF-8''([^;]+)/i.exec(header);
  if (star) {
    try { return decodeURIComponent(star[1]); } catch (_) { /* fallthrough */ }
  }
  const plain = /filename=(?:"([^"]+)"|([^;]+))/i.exec(header);
  if (plain) return (plain[1] || plain[2] || '').trim() || fallback;
  return fallback;
}

function setExportStatus(el, message, isError) {
  if (!el) return;
  el.textContent = message || '';
  el.classList.toggle('hidden', !message);
  el.classList.toggle('export-status-error', !!(message && isError));
}

function tokenRequired(block) {
  // SSR: data-admin-token-required="false" when principal already has ROLE_ADMIN.
  return block?.getAttribute('data-admin-token-required') !== 'false';
}

async function obtainAdminToken() {
  let token = readStoredToken();
  if (!token) {
    const entered = await promptModal({
      title: 'Admin-токен',
      label: 'Введи admin-токен (APP_ADMIN_TOKEN) — нужен для экспорта прогресса:',
      inputType: 'password',
      okText: 'Продолжить',
      cancelText: 'Отмена'
    });
    token = (entered || '').trim();
    if (token) storeToken(token);
  }
  return token;
}

async function runExport(format, buttons, notify, block) {
  if (exportInFlight) return;
  const needToken = tokenRequired(block);
  let token = '';
  if (needToken) {
    token = await obtainAdminToken();
    if (!token) return;
  }
  exportInFlight = true;
  notify('Готовлю экспорт…', false);
  buttons.forEach((b) => { b.setAttribute('aria-disabled', 'true'); b.setAttribute('aria-busy', 'true'); });
  try {
    const headers = {};
    if (token) headers['X-Admin-Token'] = token;
    const resp = await apiFetch('/export?format=' + encodeURIComponent(format), { headers });
    if (resp.status === 401 || resp.status === 403) {
      if (token) clearStoredToken();
      notify(needToken
        ? 'Недостаточно прав. Проверь admin-токен (APP_ADMIN_TOKEN) и попробуй снова.'
        : 'Недостаточно прав для экспорта (нужна роль администратора).', true);
      return;
    }
    if (!resp.ok) {
      notify('Не удалось выполнить экспорт (HTTP ' + resp.status + ').', true);
      return;
    }
    const blob = await resp.blob();
    const filename = parseContentDispositionFilename(
      resp.headers.get('Content-Disposition'), 'quiz-progress.' + format);
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
    notify('Готово: ' + filename, false);
  } catch (e) {
    console.error('Export failed:', e);
    notify('Сеть недоступна — экспорт не выполнен.', true);
  } finally {
    exportInFlight = false;
    buttons.forEach((b) => { b.removeAttribute('aria-disabled'); b.removeAttribute('aria-busy'); });
  }
}

export function initExportButtons() {
  if (exportBound) return;
  const block = document.getElementById('data-export-block');
  if (!block) return;
  const buttons = Array.from(block.querySelectorAll('[data-export-format]'));
  if (!buttons.length) return;
  exportBound = true;
  block.classList.remove('hidden');
  const statusEl = document.getElementById('export-status');
  buttons.forEach((btn) => {
    btn.addEventListener('click', () => {
      if (btn.getAttribute('aria-busy') === 'true') return;
      runExport(
        btn.getAttribute('data-export-format'),
        buttons,
        (m, e) => setExportStatus(statusEl, m, e),
        block
      );
    });
  });
}
