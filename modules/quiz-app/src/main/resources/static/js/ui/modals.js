import { apiFetch, parseApiError } from '../core/api-client.js';
import { API } from '../core/constants.js';

/**
 * Радиокнопки категорий жалобы, отрисованные сервером в инертный <template>
 * (focus-training.html). Своей таблицы категорий здесь нет намеренно: это была
 * копия QuestionIssueCategory, невидимая компилятору, а серверный fromString
 * снисходителен к неизвестному имени и молча кладёт жалобу в OTHER — то есть
 * расхождение проявилось бы не ошибкой, а неправильно классифицированной жалобой.
 * Нет шаблона — нет и группы: категория необязательна, сервер подставит «Другое».
 */
function categoryOptions() {
  const tpl = document.getElementById('report-category-options');
  if (!tpl || !tpl.content.querySelector('input[name="report-category"]')) return null;
  return tpl.content.cloneNode(true);
}

export function promptModal(opts = {}) {
  const isConfirm = opts.mode === 'confirm';
  return new Promise((resolve) => {
    const lastFocused = document.activeElement;
    const overlay = document.createElement('div');
    overlay.className = 'prompt-overlay';
    overlay.setAttribute('role', isConfirm ? 'alertdialog' : 'dialog');
    overlay.setAttribute('aria-modal', 'true');
    overlay.setAttribute('aria-labelledby', 'prompt-modal-title');
    if (isConfirm) overlay.setAttribute('aria-describedby', 'prompt-modal-message');
    overlay.innerHTML =
      '<form class="prompt-modal" novalidate>' +
      '<h3 id="prompt-modal-title" class="prompt-modal-title"></h3>' +
      (isConfirm
        ? '<p id="prompt-modal-message" class="prompt-modal-message"></p>'
        : '<label class="prompt-modal-label" for="prompt-modal-input"></label>' +
          '<input id="prompt-modal-input" class="prompt-modal-input" autocomplete="off" spellcheck="false" type="' +
            (opts.inputType === 'password' ? 'password' : 'text') + '">') +
      '<div class="prompt-modal-actions">' +
      '<button type="button" class="btn secondary-btn" data-prompt-cancel></button>' +
      '<button type="submit" class="btn' + (isConfirm ? ' danger-btn' : '') + '" data-prompt-ok></button>' +
      '</div></form>';

    overlay.querySelector('.prompt-modal-title').textContent =
      opts.title || (isConfirm ? 'Подтвердите действие' : 'Ввод');
    if (isConfirm) overlay.querySelector('.prompt-modal-message').textContent = opts.message || '';
    else overlay.querySelector('.prompt-modal-label').textContent = opts.label || '';
    overlay.querySelector('[data-prompt-ok]').textContent = opts.okText || (isConfirm ? 'Подтвердить' : 'OK');
    overlay.querySelector('[data-prompt-cancel]').textContent = opts.cancelText || 'Отмена';

    const form = overlay.querySelector('.prompt-modal');
    const input = overlay.querySelector('#prompt-modal-input');
    document.body.appendChild(overlay);

    const inerted = [];
    for (const el of Array.from(document.body.children)) {
      if (el === overlay || el.tagName === 'SCRIPT') continue;
      if (el.hasAttribute('inert') || el.getAttribute('aria-hidden') === 'true') continue;
      el.setAttribute('inert', '');
      el.setAttribute('aria-hidden', 'true');
      inerted.push(el);
    }
    const initialFocus = isConfirm ? overlay.querySelector('[data-prompt-cancel]') : input;
    requestAnimationFrame(() => initialFocus.focus());

    const cancelValue = isConfirm ? false : null;
    let done = false;
    function settle(value) {
      if (done) return;
      done = true;
      inerted.forEach((el) => { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
      overlay.remove();
      if (lastFocused?.focus) lastFocused.focus();
      resolve(value);
    }
    form.addEventListener('submit', (e) => { e.preventDefault(); settle(isConfirm ? true : input.value); });
    overlay.querySelector('[data-prompt-cancel]').addEventListener('click', () => settle(cancelValue));
    overlay.addEventListener('click', (e) => { if (e.target === overlay) settle(cancelValue); });
    overlay.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') { e.preventDefault(); settle(cancelValue); return; }
      if (e.key !== 'Tab') return;
      const f = overlay.querySelectorAll('button, input, [href], [tabindex]:not([tabindex="-1"])');
      if (!f.length) return;
      const first = f[0];
      const last = f[f.length - 1];
      if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    });
  });
}

export function confirmModal(opts) {
  return promptModal(Object.assign({ mode: 'confirm' }, opts));
}

export function reportModal(questionId) {
  if (!questionId) return;
  const lastFocused = document.activeElement;
  const overlay = document.createElement('div');
  overlay.className = 'prompt-overlay';
  overlay.setAttribute('role', 'dialog');
  overlay.setAttribute('aria-modal', 'true');
  overlay.setAttribute('aria-labelledby', 'report-modal-title');
  const categories = categoryOptions();
  overlay.innerHTML =
    '<form class="prompt-modal report-modal" novalidate>' +
    '<h3 id="report-modal-title" class="prompt-modal-title">Сообщить о проблеме</h3>' +
    (categories
      ? '<fieldset class="report-fieldset"><legend class="report-legend">Что не так с вопросом?</legend></fieldset>'
      : '') +
    '<label class="prompt-modal-label" for="report-comment">Комментарий (необязательно)</label>' +
    '<textarea id="report-comment" class="report-comment" maxlength="2000" rows="3"></textarea>' +
    '<p class="report-error hidden" role="alert"></p>' +
    '<div class="prompt-modal-actions">' +
    '<button type="button" class="btn secondary-btn" data-report-cancel>Отмена</button>' +
    '<button type="submit" class="btn next-btn" data-report-ok>Отправить</button>' +
    '</div></form>';
  if (categories) overlay.querySelector('.report-fieldset').appendChild(categories);
  document.body.appendChild(overlay);

  const inerted = [];
  for (const el of Array.from(document.body.children)) {
    if (el === overlay || el.tagName === 'SCRIPT') continue;
    if (el.hasAttribute('inert') || el.getAttribute('aria-hidden') === 'true') continue;
    el.setAttribute('inert', '');
    el.setAttribute('aria-hidden', 'true');
    inerted.push(el);
  }
  const form = overlay.querySelector('.report-modal');
  const okBtn = overlay.querySelector('[data-report-ok]');
  const errEl = overlay.querySelector('.report-error');
  // Ни одна категория заранее не выбрана: предвыбор первой отправлял каждую
  // неосознанную жалобу как «Неверный ответ» — самую тревожную из пяти. В
  // незаполненной радиогруппе первая кнопка всё равно попадает в Tab-порядок,
  // так что фокус ставится туда же, куда и раньше.
  const initialFocus = overlay.querySelector('input[name="report-category"]')
    || overlay.querySelector('#report-comment');
  requestAnimationFrame(() => initialFocus.focus());

  let done = false;
  let inFlight = false;
  function close() {
    if (done) return;
    done = true;
    inerted.forEach((el) => { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
    overlay.remove();
    if (lastFocused?.focus) lastFocused.focus();
  }
  overlay.querySelector('[data-report-cancel]').addEventListener('click', close);
  overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  overlay.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') { e.preventDefault(); close(); return; }
    if (e.key !== 'Tab') return;
    const f = overlay.querySelectorAll('button, input, textarea, [href], [tabindex]:not([tabindex="-1"])');
    if (!f.length) return;
    const first = f[0];
    const last = f[f.length - 1];
    if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
    else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
  });

  function showSuccess() {
    form.innerHTML =
      '<h3 class="prompt-modal-title">Спасибо!</h3>' +
      '<p class="report-success">Проблема отправлена на проверку. Мы разберёмся.</p>' +
      '<div class="prompt-modal-actions"><button type="button" class="btn next-btn" data-report-done>Готово</button></div>';
    const doneBtn = form.querySelector('[data-report-done]');
    doneBtn.addEventListener('click', close);
    requestAnimationFrame(() => doneBtn.focus());
  }
  function showError(msg) {
    errEl.textContent = msg;
    errEl.classList.remove('hidden');
    okBtn.removeAttribute('aria-busy');
    okBtn.removeAttribute('aria-disabled');
    inFlight = false;
  }
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (inFlight) return;
    inFlight = true;
    okBtn.setAttribute('aria-busy', 'true');
    okBtn.setAttribute('aria-disabled', 'true');
    errEl.classList.add('hidden');
    // Категория не выбрана → уходит пустой, а не своё 'OTHER': правило
    // «неизвестное/пустое → OTHER» принадлежит серверу (QuestionIssueCategory)
    // и должно жить в одном месте.
    const checked = overlay.querySelector('input[name="report-category"]:checked');
    const comment = overlay.querySelector('#report-comment').value.trim();
    const body = new URLSearchParams({ questionId: String(questionId) });
    if (checked) body.set('category', checked.value);
    if (comment) body.set('comment', comment);
    try {
      const resp = await apiFetch(API.REPORT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body
      });
      if (resp.ok) showSuccess();
      else showError('Не удалось отправить — возможно, вопрос устарел. Обнови страницу и повтори.');
    } catch (err) {
      console.error('Report submit failed:', err);
      showError('Не удалось отправить. Проверь сеть и повтори.');
    }
  });
}

let reportBound = false;
export function initReportTriggers() {
  if (reportBound) return;
  reportBound = true;
  document.querySelectorAll('[data-report-trigger]').forEach((b) => b.classList.remove('hidden'));
  document.addEventListener('click', (e) => {
    const trg = e.target.closest('[data-report-trigger]');
    if (!trg) return;
    e.preventDefault();
    reportModal(trg.getAttribute('data-question-id'));
  });
}
