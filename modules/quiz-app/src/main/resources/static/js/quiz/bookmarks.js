import { apiFetch, apiPostJson, parseApiError } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { clearInlineAlert, setInlineAlert } from '../ui/alerts.js';

let bookmarksBound = false;

/**
 * Auto-grow заметки закладки под её содержимое. Серверный rows="3" резервирует
 * ~118px даже под однострочную заметку → список тонет в пустоте. Подгоняем
 * высоту к тексту (короткая — компактна, длинная — растёт). Только для карточек
 * .has-note: пустые (.no-note) свёрнуты CSS-ом через :focus-within (не трогаем,
 * иначе inline-height перебьёт элегантный collapse). box-sizing:border-box →
 * scrollHeight не включает border, добавляем его, чтобы не появился микро-скролл.
 */
function autosizeNote(textarea) {
  // height:'0' (не 'auto') — у textarea 'auto' резолвится в высоту по атрибуту
  // rows="3", поэтому scrollHeight застыл бы на 3 строках; обнуление даёт
  // scrollHeight = реальная высота контента. min-height (CSS) держит нижний пол.
  textarea.style.height = '0px';
  const cs = getComputedStyle(textarea);
  const border = parseFloat(cs.borderTopWidth || 0) + parseFloat(cs.borderBottomWidth || 0);
  textarea.style.height = (textarea.scrollHeight + border) + 'px';
}

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
    const resp = await apiPostJson(API.BOOKMARK_NOTE, { questionId: Number(questionId), note });
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
    // Пересчитать высоту под нормализованный сервером текст (только has-note —
    // no-note держит CSS-collapse, inline-height его бы сломал).
    if (item?.classList.contains('has-note')) {
      autosizeNote(textarea);
    }
    // Проза заметки на экране вопроса: без обновления она осталась бы прежней до
    // перезагрузки, то есть экран показывал бы старый текст рядом с новым в поле.
    const questionNote = form.closest('.question-note');
    if (questionNote) {
      let prose = questionNote.querySelector('.question-note-text');
      if (data.note) {
        if (!prose) {
          prose = document.createElement('p');
          prose.className = 'question-note-text';
          questionNote.insertBefore(prose, questionNote.querySelector('.question-note-disclosure'));
        }
        prose.textContent = data.note;
      } else if (prose) {
        prose.remove();
      }
      const summary = questionNote.querySelector('.question-note-summary');
      if (summary) summary.textContent = data.note ? 'Изменить заметку' : 'Записать мысль';
    }
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
    const resp = await apiPostJson(API.BOOKMARK_REMOVE, { questionId: Number(questionId) });
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

/**
 * Формы заметок — на ДВУХ экранах. Гейт был по классу страницы
 * (`bookmarks-page`), и с появлением заметки на экране вопроса он означал бы
 * «напиши второй такой же обработчик». Гейт теперь по наличию самих форм:
 * страница, где их нет, выходит так же рано, как и раньше.
 */
export function initBookmarksPage() {
  if (bookmarksBound) return;
  if (!document.querySelector('.bookmark-note-form')) return;
  bookmarksBound = true;

  document.querySelectorAll('.bookmark-note-form').forEach((form) => {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      void saveNote(form);
    });
  });

  // Auto-grow заметки. Считать размер на ЗАКРЫТОМ раскрытии нельзя: у скрытого
  // поля scrollHeight равен нулю, и высота залипла бы на нуле до перезагрузки.
  // Поэтому первый расчёт — на открытии <details>, дальше на ввод.
  document.querySelectorAll('.bookmark-note-disclosure').forEach((disclosure) => {
    const textarea = disclosure.querySelector('textarea[name="note"]');
    if (!textarea) return;
    disclosure.addEventListener('toggle', () => {
      if (disclosure.open) autosizeNote(textarea);
    });
    textarea.addEventListener('input', () => autosizeNote(textarea));
  });

  document.querySelectorAll('.bookmark-remove-form').forEach((form) => {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      void removeBookmark(form);
    });
  });
}
