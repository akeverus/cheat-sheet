import { apiPostJson, parseApiError } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { clearInlineAlert, setInlineAlert } from '../ui/alerts.js';
import { setPending, clearPending } from '../ui/pending.js';

let bookmarksBound = false;
const pendingNotes = new WeakSet();
const pendingRemovals = new WeakSet();
const savedNotes = new WeakMap();

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
  if (pendingNotes.has(form)) return;
  const questionId = form.getAttribute('data-question-id');
  const textarea = form.querySelector('textarea[name="note"]');
  if (!questionId || !textarea) return;
  const note = textarea.value || '';
  const submit = form.querySelector('button[type="submit"]');
  pendingNotes.add(form);
  setPending(submit, 'Сохраняю…');
  setStatus(form, '', false);
  try {
    const resp = await apiPostJson(API.BOOKMARK_NOTE, { questionId: Number(questionId), note });
    if (!resp.ok) {
      await parseApiError(resp);
      setStatus(form, 'Не удалось сохранить. Текст остался в поле — попробуй ещё раз.', true);
      return;
    }
    const data = await resp.json();
    const savedNote = data.note == null ? '' : String(data.note);
    savedNotes.set(form, savedNote);
    // The response acknowledges the submitted snapshot, not edits made while it
    // was in flight. Keep those edits available for the next explicit save.
    const editedWhileSaving = textarea.value !== note;
    if (!editedWhileSaving) textarea.value = savedNote;
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
      if (savedNote) {
        if (!prose) {
          prose = document.createElement('p');
          prose.className = 'question-note-text';
          const disclosure = questionNote.querySelector('.question-note-disclosure');
          disclosure.before(prose);
        }
        prose.textContent = savedNote;
      } else if (prose) {
        prose.remove();
      }
      const summary = questionNote.querySelector('.question-note-summary');
      if (summary) summary.textContent = savedNote ? 'Изменить заметку' : 'Записать мысль';
      const outerSummary = questionNote.querySelector('.question-note-outer-summary');
      let mark = outerSummary?.querySelector('.question-note-outer-mark');
      if (savedNote && outerSummary && !mark) {
        mark = document.createElement('span');
        mark.className = 'muted tiny question-note-outer-mark';
        mark.textContent = 'есть';
        outerSummary.appendChild(mark);
      } else if (!savedNote && mark) {
        mark.remove();
      }
      questionNote.querySelector('.question-note-saved')?.remove();
    }
    if (item) {
      item.classList.toggle('has-note', !!savedNote);
      item.classList.toggle('no-note', !savedNote);
      let prose = item.querySelector('.bookmark-note-text');
      if (savedNote) {
        if (!prose) {
          prose = document.createElement('p');
          prose.className = 'bookmark-note-text';
          item.querySelector('.bookmark-row-actions')?.before(prose);
        }
        prose.textContent = savedNote;
      } else {
        prose?.remove();
      }
      const summary = item.querySelector('.bookmark-note-summary');
      if (summary) {
        summary.textContent = savedNote ? 'Изменить заметку' : 'Добавить заметку';
        const question = item.querySelector('.bookmark-question')?.textContent.trim();
        summary.setAttribute('aria-label', summary.textContent + ' к вопросу: ' + (question || ''));
      }
    }
    const noteStatus = item?.querySelector('.bookmark-note-status');
    if (noteStatus) {
      noteStatus.textContent = data.note ? 'есть заметка' : 'без заметки';
    }
    setStatus(form, editedWhileSaving
      ? 'Предыдущий текст сохранён. Новые изменения ещё не сохранены.'
      : 'Сохранено', false);
    if (item && !editedWhileSaving) {
      const filter = new URLSearchParams(window.location.search);
      const noteFilter = filter.get('note');
      const query = filter.get('q')?.trim();
      const noLongerMatchesNote = (noteFilter === 'yes' && !savedNote)
        || (noteFilter === 'no' && !!savedNote);
      // Search includes note text. Let the same server predicate resolve both
      // filtered empty states; never reload over edits made during this save.
      if (noLongerMatchesNote || query) window.location.reload();
    }
  } catch (err) {
    console.error('Bookmark note save failed:', err);
    setStatus(form, 'Не удалось сохранить. Текст остался в поле — проверь соединение и повтори.', true);
  } finally {
    pendingNotes.delete(form);
    clearPending(submit, '');
  }
}

async function removeBookmark(form) {
  const questionId = form.getAttribute('data-question-id');
  if (!questionId || pendingRemovals.has(form)) return;
  const button = form.querySelector('button[type="submit"]');
  pendingRemovals.add(form);
  setPending(button, 'Убираю…');
  try {
    const resp = await apiPostJson(API.BOOKMARK_REMOVE, { questionId: Number(questionId) });
    if (!resp.ok) {
      await parseApiError(resp);
      setInlineAlert('Не удалось убрать закладку. Она осталась в списке — попробуй ещё раз.');
      return;
    }
    clearInlineAlert();
    const item = form.closest('.bookmark-item');
    const nextFocus = item?.nextElementSibling?.querySelector('.bookmark-question')
      || item?.previousElementSibling?.querySelector('.bookmark-question');
    item?.remove();
    const list = document.querySelector('.bookmark-list');
    if (list && !list.querySelector('.bookmark-item')) {
      // The server re-evaluates filters, pagination, and the two distinct empty states.
      window.location.reload();
      return;
    }
    const total = document.getElementById('bookmarks-total');
    if (total) {
      const count = Math.max(0, Number(total.dataset.count) - 1);
      total.dataset.count = String(count);
      const plural = new Intl.PluralRules('ru').select(count);
      total.textContent = count + ' ' + ({ one: 'закладка', few: 'закладки', many: 'закладок' }[plural] || 'закладок');
      const filteredCount = document.querySelector('.filter-bar-count');
      if (filteredCount) {
        const matched = Math.max(0, Number(filteredCount.dataset.count) - 1);
        filteredCount.dataset.count = String(matched);
        filteredCount.textContent = matched + ' из ' + count;
      }
    }
    const status = document.getElementById('bookmarks-status');
    if (status) status.textContent = 'Закладка и её заметка удалены. Вопрос остался в банке.';
    nextFocus?.focus();
  } catch (err) {
    console.error('Bookmark remove failed:', err);
    setInlineAlert('Не удалось убрать закладку. Она осталась в списке — проверь соединение и повтори.');
  } finally {
    pendingRemovals.delete(form);
    clearPending(button, '');
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
    const textarea = form.querySelector('textarea[name="note"]');
    if (textarea) {
      savedNotes.set(form, textarea.value);
      textarea.addEventListener('input', () => {
        if (pendingNotes.has(form)) return;
        setStatus(form, textarea.value === savedNotes.get(form) ? '' : 'Есть несохранённые изменения', false);
      });
    }
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
