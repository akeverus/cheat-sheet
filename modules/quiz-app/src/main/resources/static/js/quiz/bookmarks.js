import { apiFetch, parseApiError } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { clearInlineAlert, setInlineAlert } from '../ui/alerts.js';

let bookmarksBound = false;

function setStatus(form, message, isError) {
  const status = form.querySelector('.bookmark-save-status');
  if (!status) return;
  status.textContent = message || '';
  status.classList.toggle('is-error', !!isError);
}

async function saveNote(form) {
  const questionId = form.getAttribute('data-question-id');
  const textarea = form.querySelector('textarea[name="note"]');
  if (!questionId || !textarea) return;
  const note = textarea.value || '';
  setStatus(form, 'Сохраняю…', false);
  try {
    const body = new URLSearchParams();
    body.set('questionId', questionId);
    body.set('note', note);
    const resp = await apiFetch(API.BOOKMARK_NOTE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString()
    });
    if (!resp.ok) {
      await parseApiError(resp);
      setStatus(form, 'Не удалось сохранить', true);
      setInlineAlert('Не удалось сохранить заметку. Обнови страницу и повтори.');
      return;
    }
    clearInlineAlert();
    const data = await resp.json();
    textarea.value = data.note == null ? '' : data.note;
    const item = form.closest('.bookmark-item');
    const noteStatus = item?.querySelector('.bookmark-note-status');
    if (noteStatus) {
      noteStatus.textContent = data.note ? 'есть заметка' : 'без заметки';
    }
    setStatus(form, 'Сохранено', false);
  } catch (err) {
    console.error('Bookmark note save failed:', err);
    setStatus(form, 'Ошибка сети', true);
    setInlineAlert('Не удалось сохранить заметку. Проверь сеть и повтори.');
  }
}

async function removeBookmark(form) {
  const questionId = form.getAttribute('data-question-id');
  if (!questionId) return;
  try {
    const body = new URLSearchParams();
    body.set('questionId', questionId);
    const resp = await apiFetch(API.BOOKMARK_REMOVE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString()
    });
    if (!resp.ok) {
      await parseApiError(resp);
      setInlineAlert('Не удалось убрать закладку. Обнови страницу и повтори.');
      return;
    }
    clearInlineAlert();
    const item = form.closest('.bookmark-item');
    if (item) item.remove();
    const list = document.querySelector('.bookmark-list');
    if (list && !list.querySelector('.bookmark-item')) {
      window.location.reload();
    }
  } catch (err) {
    console.error('Bookmark remove failed:', err);
    setInlineAlert('Не удалось убрать закладку. Проверь сеть и повтори.');
  }
}

export function initBookmarksPage() {
  if (bookmarksBound) return;
  if (!document.body.classList.contains('bookmarks-page')) return;
  bookmarksBound = true;

  document.querySelectorAll('.bookmark-note-form').forEach((form) => {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      void saveNote(form);
    });
  });

  document.querySelectorAll('.bookmark-remove-form').forEach((form) => {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      void removeBookmark(form);
    });
  });
}
