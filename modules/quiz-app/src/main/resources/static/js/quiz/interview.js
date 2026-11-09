import { apiFetch, apiPostJson, parseApiError } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { isSecondaryControl, isShortcutEventIgnored } from '../core/keyboard.js';
import { icon, escapeHtml, pluralRu } from '../core/dom.js';
import { sanitizeHtml } from '../content/sanitize.js';
import { renderDynamicContent } from '../content/dynamic-content.js';
import { clearInlineAlert, setInlineAlert } from '../ui/alerts.js';
import { confirmModal } from '../ui/modals.js';
import { setPending, clearPending } from '../ui/pending.js';
import { initTablist } from '../ui/tablist.js';
import { refreshStatsFromServer, updateSessionProgress } from './session-stats.js';

let interviewBound = false;

/**
 * Поля SubmitAnswerRequest и способ приведения типа. Список явный, а не
 * «всё что есть в форме»: в форме живут и `_csrf` (уходит заголовком
 * X-XSRF-TOKEN), и `weakTopics` — параметр выборки вопросов, которого в DTO
 * нет. Слепая сериализация FormData в JSON роняла бы привязку на них.
 */
const ANSWER_FIELD_TYPES = {
  questionId: 'number',
  optionId: 'number',
  questionRevisionId: 'number',
  confidence: 'number',
  responseTimeMs: 'number',
  important: 'boolean',
  onlyWrong: 'boolean',
  shuffle: 'boolean',
  ordered: 'boolean',
  topic: 'string',
  group: 'string',
  clientAttemptId: 'string',
  openedContentBlockIds: 'string'
};

/** FormData формы ответа → JSON-тело для POST /api/answer. */
function formDataToAnswerPayload(formData) {
  const payload = {};
  Object.keys(ANSWER_FIELD_TYPES).forEach((field) => {
    const raw = formData.get(field);
    if (raw === null || raw === '') return;
    const value = String(raw);
    switch (ANSWER_FIELD_TYPES[field]) {
      case 'number': {
        const num = Number(value);
        if (Number.isFinite(num)) payload[field] = num;
        break;
      }
      case 'boolean':
        // Чекбокс присылает своё value только когда отмечен, поэтому само
        // присутствие поля уже значит true — кроме явного "false".
        payload[field] = value !== 'false' && value !== '0';
        break;
      default:
        payload[field] = value;
    }
  });
  return payload;
}

function appendPenaltyNotice(added, total) {
  const feedback = document.getElementById('result-feedback');
  if (!feedback) return;
  const note = document.createElement('p');
  note.className = 'result-penalty-note';
  note.textContent = '+' + added + ' '
    + pluralRu(added, 'штрафной вопрос', 'штрафных вопроса', 'штрафных вопросов')
    + ' за ошибку — теперь в сессии ' + total + '.';
  feedback.appendChild(note);
}

/** Think-time tracker: pauses while tab is hidden (Page Visibility). */
function createThinkTimeTracker() {
  let questionShownAt = (typeof performance !== 'undefined' && performance.now)
    ? performance.now()
    : Date.now();
  let pausedMs = 0;
  let pauseStartedAt = null;

  function nowMs() {
    return (typeof performance !== 'undefined' && performance.now)
      ? performance.now()
      : Date.now();
  }

  function pause() {
    if (pauseStartedAt === null) pauseStartedAt = nowMs();
  }

  function resume() {
    if (pauseStartedAt !== null) {
      pausedMs += nowMs() - pauseStartedAt;
      pauseStartedAt = null;
    }
  }

  function markQuestionShown() {
    questionShownAt = nowMs();
    pausedMs = 0;
    pauseStartedAt = document.hidden ? nowMs() : null;
  }

  function currentResponseTimeMs() {
    let activePause = 0;
    if (pauseStartedAt !== null) activePause = nowMs() - pauseStartedAt;
    const ms = Math.round(nowMs() - questionShownAt - pausedMs - activePause);
    if (!Number.isFinite(ms) || ms < 0) return 0;
    return Math.min(ms, 1_800_000);
  }

  document.addEventListener('visibilitychange', () => {
    if (document.hidden) pause();
    else resume();
  });

  return { markQuestionShown, currentResponseTimeMs };
}

export function initInterviewForm() {
  if (interviewBound) return;
  const form = document.getElementById('interview-form');
  const optionsContainer = document.getElementById('interview-options');
  const submitBtn = document.getElementById('interview-submit');
  const feedbackDiv = document.getElementById('result-feedback');
  const nextLink = document.getElementById('next-question');
  const answerFlowHint = document.getElementById('answer-flow-hint');
  if (!form || !optionsContainer) return;
  interviewBound = true;

  const thinkTime = createThinkTimeTracker();
  thinkTime.markQuestionShown();

  const optionInputs = Array.from(form.querySelectorAll('input[name="optionId"]'));
  const skipBtn = form.querySelector('.skip-btn');
  let answered = false;
  let clientAttemptId = null;
  let clientAttemptQuestionId = null;

  // BFCache возвращает документ вместе с флагом native POST. Навигация уже
  // закончилась, поэтому её блокировка больше не должна запрещать отправку.
  window.addEventListener('pageshow', (event) => {
    if (event.persisted) delete form.dataset.skipSubmitted;
  });

  // #result-feedback НЕ live-регион (иначе SR зачитывает весь ~2900-симв. разбор атомарно).
  // Краткий вердикт анонсируется через #result-announce; сюда переносится focus (см. ниже).
  const announceEl = document.getElementById('result-announce');

  // Варианты на время проверки — именно НЕДОСТУПНЫ, а не «заняты»: работа идёт
  // на кнопке, и она же её показывает (setPending). До iter-84 сюда вешался ещё
  // и `aria-busy` на форму — второй ключ того же состояния, ничего не менявший
  // ни на экране, ни в озвучке.
  function setInteractionBusy(busy) {
    const disabled = busy || answered;
    optionsContainer.setAttribute('aria-disabled', disabled ? 'true' : 'false');
    optionInputs.forEach((input) => { input.disabled = disabled; });
    if (skipBtn) skipBtn.disabled = disabled;
  }

  function currentClientAttemptId() {
    const qid = (form.querySelector('input[name="questionId"]') || {}).value || '';
    if (clientAttemptId === null || clientAttemptQuestionId !== qid) {
      clientAttemptId = (window.crypto?.randomUUID)
        ? crypto.randomUUID()
        : 'ca-' + qid + '-' + Date.now().toString(36) + Math.floor(Math.random() * 1e9).toString(36);
      clientAttemptQuestionId = qid;
    }
    return clientAttemptId;
  }

  function updateSubmitAvailability() {
    if (answered) return;
    const selected = form.querySelector('input[name="optionId"]:checked');
    if (submitBtn) {
      const armed = !!selected;
      submitBtn.disabled = !armed;
      submitBtn.classList.toggle('is-disabled', !armed);
      submitBtn.classList.toggle('is-armed', armed);
      submitBtn.textContent = 'Проверить ответ';
    }
  }

  function selectOptionByIndex(index) {
    const target = optionInputs[index];
    if (!target || target.disabled) return false;
    target.checked = true;
    /* Скрытый radio занимает 1px у верхнего края label: нативный focus-scroll
       показывал только этот пиксель, оставляя выбранный длинный вариант целиком
       под сгибом/mobile-nav. Фокусируем без промежуточного скачка и прокручиваем
       реальную тач-/визуальную цель с её scroll-margin. */
    target.focus({ preventScroll: true });
    const label = target.closest('label');
    if (label) label.scrollIntoView({ block: 'nearest', inline: 'nearest' });
    target.dispatchEvent(new Event('change', { bubbles: true }));
    updateSubmitAvailability();
    return true;
  }

  updateSubmitAvailability();

  optionInputs.forEach((input) => {
    input.addEventListener('change', updateSubmitAvailability);
  });

  if (submitBtn) {
    submitBtn.setAttribute('title', `Клавиши 1–${Math.min(optionInputs.length, 9)} — выбор, Enter — ответить`);
  }

  document.addEventListener('keydown', (event) => {
    if (isShortcutEventIgnored(event)) return;
    if (document.querySelector('.kbd-help-overlay:not(.hidden)')) return;
    if (answered) return;
    if (isSecondaryControl(event.target, optionsContainer)) return;
    if (event.target?.type === 'radio' && event.key === 'Enter') {
      event.preventDefault();
      form.dispatchEvent(new Event('submit', { cancelable: true }));
      return;
    }
    const key = event.key;
    /* A11Y-02 (REF-021): стрелки с фокуса внутри скролл-региона (код-листинг,
       диаграмма, таблица — рецепт REF-007 «переполнен → фокусируем») остаются
       браузерным скроллом. Без этого ArrowDown с код-региона ВЫБИРАЛ вариант
       и угонял фокус — чтение кода с клавиатуры меняло ответ на экзамене. */
    if ((key === 'ArrowDown' || key === 'ArrowUp')
        && event.target instanceof Element
        && event.target.closest('.code-viewport, .diagram-viewport, .table-scroll, pre[tabindex], .is-scrollable')) {
      return;
    }
    if (key === 'ArrowDown' || key === 'ArrowUp') {
      event.preventDefault();
      const currentIndex = optionInputs.findIndex((input) => input.checked);
      if (currentIndex === -1) { selectOptionByIndex(0); return; }
      const delta = key === 'ArrowDown' ? 1 : -1;
      selectOptionByIndex((currentIndex + delta + optionInputs.length) % optionInputs.length);
      return;
    }
    if (key === 'Escape') {
      const selected = form.querySelector('input[name="optionId"]:checked');
      if (selected) {
        event.preventDefault();
        selected.checked = false;
        selected.dispatchEvent(new Event('change', { bubbles: true }));
        selected.focus({ preventScroll: true });
      }
      return;
    }
    if (key >= '1' && key <= '9') {
      // Работают со всей страницы (кроме текстовых полей — гейт выше), чтобы
      // не требовать автофокус первого radio при load (он ломал skip-link / Tab).
      const index = parseInt(key, 10) - 1;
      if (selectOptionByIndex(index)) event.preventDefault();
    }
    if (key === 'Enter') {
      event.preventDefault();
      form.dispatchEvent(new Event('submit', { cancelable: true }));
    }
  });

  // Не автофокусируем первый radio при загрузке: иначе первый Tab уходит
  // mid-page (дальше по форме → «Сообщить о проблеме»), а skip-link обходится.
  // Выбор 1–9 / стрелки остаётся; фокус ставит selectOptionByIndex по действию.

  // Формулировку вердикта здесь больше не собирают: её присылает сервер
  // (`verdictText`, общий AnswerCopy). Раньше строку строили дважды — тут и в
  // Java для no-JS-страницы, — и редакции разъезжались.
  function buildVerdictLine(data) {
    const name = data.correct ? 'circle-check' : 'circle-x';
    const tone = data.correct ? 'ui-icon-success' : 'ui-icon-error';
    return icon(name, 'ui-icon-lead ' + tone)
      + `<span>${escapeHtml(data.verdictText || '')}</span>`;
  }

  function buildXpBadge(data) {
    const xp = Number(data.xpAwarded || 0);
    if (!(xp > 0)) return '';
    return `<span class="result-xp" aria-label="Начислено ${xp} XP">+${xp} XP</span>`;
  }

  function buildNextReviewLine(data) {
    // Срок тоже приходит готовым (`nextReviewText`): склонение «день/дня/дней» и
    // подмена «через 1 день» на «завтра» — один раз в AnswerCopy, а не по разу
    // на каждом рендерере.
    const when = escapeHtml(data.nextReviewText || '—');
    return `<p class="next-review">`
      + `<span class="next-review-eyebrow">Следующее повторение:</span> `
      + `<span class="next-review-when">${when}</span></p>`
      + `<p class="review-policy muted tiny">`
      + `Интервальные повторения: верный ответ отодвигает карточку, ошибка возвращает её раньше.`
      + `</p>`;
  }

  // Отдельного блока «Правильный ответ: C. <текст варианта>» здесь больше нет.
  // Тот же факт уже сказан дважды выше: вердикт называет букву («Правильный
  // ответ C»), а сам вариант в списке несёт зелёную рамку и чип «Правильный
  // ответ». Третье повторение — ровно тот случай, против которого написан
  // комментарий в result.html («повторяло бы то же самое третий раз»), и
  // одновременно единственное место, где два пути разбора расходились по
  // составу блоков.

  function buildDistractorPanel(data) {
    const opts = data.optionExplanations || [];
    let rows = '';
    opts.forEach((oe, i) => {
      if (oe.id === data.correctOptionId) return;
      if (!oe.explanationHtml?.trim()?.length) return;
      const letter = String.fromCharCode(65 + i);
      const mine = oe.id === data.selectedOptionId ? ' distractor-row-mine' : '';
      rows += `<div class="distractor-row${mine}">`
        + `<strong class="distractor-letter">${letter} — неверно.</strong> `
        + `<span class="distractor-reason markdown-content">${sanitizeHtml(oe.explanationHtml)}</span>`
        + `</div>`;
    });
    if (!rows) return '';
    return `<div class="explanation-panel distractor-panel">`
      + `<span class="explanation-eyebrow">Почему другие варианты не подходят</span>`
      + `<div class="distractor-list">${rows}</div>`
      + `</div>`;
  }

  function renderFeedbackHtml(data, restoredSelection) {
    const isCorrect = data.correct;
    const leadHtml = sanitizeHtml(data.answerLeadHtml || '');
    const restHtml = sanitizeHtml(data.answerRestHtml || '');
    const hasLead = leadHtml.trim().length > 0;
    const fullHtml = sanitizeHtml(data.answerHtml || '');
    // Лид и «весь разбор» — одна и та же панель под одним заголовком: это первый
    // абзац объяснения, а не сравнение с выбранным вариантом. Заголовок
    // «Ключевое отличие» обещал сопоставление, которого в тексте нет, и вдобавок
    // расходился с no-JS-страницей, где тот же лид всегда назывался «Почему это
    // так?». Классы-носители `feedback-key-difference` / `feedback-explanation`
    // ушли вместе с `order`-раскладкой: порядок задаёт сам DOM.
    const explanationPanel = (hasLead || fullHtml.trim())
      ? `<div class="explanation-panel">`
        + `<span class="explanation-eyebrow">Почему это так?</span>`
        + `<div class="result-lead answer markdown-content">${hasLead ? leadHtml : fullHtml}</div>`
        + `</div>`
      : '';
    const additionalPanel = (hasLead && restHtml.trim())
      ? `<details class="explanation-panel additional-panel">`
        + `<summary class="explanation-eyebrow">Подробнее</summary>`
        + `<div class="answer markdown-content">${restHtml}</div>`
        + `</details>`
      : '';

    // РАЗБОР — ВКЛАДКИ, А НЕ ЛЕНТА В ТРИ ЭКРАНА.
    //
    // Замер до правки (Chromium, экзамен с кодом): документ после ответа рос с 1205 до
    // 3369 на 1440×900 и с 2146 до 5278 на 390×844, то есть в 2.5–2.8 раза. Три четверти
    // прироста давала одна панель — разбор неверных вариантов (1499px из 1955).
    //
    // Вердикт и мета остаются ВНЕ вкладок: это результат, он нужен всегда. Переключаются
    // объяснения — почему верно, почему остальные нет, что почитать рядом. Панель без
    // содержимого не создаётся, и если панель осталась одна, переключателя нет вовсе:
    // вкладка, у которой нет соседей, — это заголовок, притворяющийся управлением.
    const panels = [];
    if (explanationPanel || additionalPanel) {
      panels.push({ id: 'why', label: 'Почему это так', html: explanationPanel + additionalPanel });
    }
    const distractors = buildDistractorPanel(data);
    if (distractors) {
      panels.push({ id: 'others', label: 'Другие варианты', html: distractors });
    }
    const related = buildRelatedPanel(data);
    if (related) {
      panels.push({ id: 'related', label: 'Похожие вопросы', html: related });
    }

    const tablist = panels.length > 1
      ? `<div class="section-tabs feedback-tabs" role="tablist" id="feedback-tablist" aria-label="Разбор ответа">`
        + panels.map((panel, i) => `<button type="button" class="section-tab" role="tab"`
            + ` id="feedback-tab-${panel.id}" aria-controls="feedback-panel-${panel.id}"`
            + ` aria-selected="${i === 0}" tabindex="${i === 0 ? 0 : -1}">${panel.label}</button>`).join('')
        + `</div>`
      : '';
    const panelsHtml = panels.map((panel, i) => `<div class="feedback-panel${i === 0 ? ' is-active' : ''}"`
      + ` id="feedback-panel-${panel.id}"`
      + (panels.length > 1 ? ` role="tabpanel" aria-labelledby="feedback-tab-${panel.id}"` : '') + '>'
      + panel.html + `</div>`).join('');

    // ПОРЯДОК ЧИТАЕТСЯ КАК РЕЗУЛЬТАТ: вердикт → что это дало → куда дальше →
    // и только потом разбор. Ровно этот порядок уже стоит на серверной странице
    // ответа (`/answer`, iter-136), а здесь оставался прежний: вердикт → вкладки →
    // панель → мета → уверенность → «Следующий вопрос».
    //
    // Замер до правки (Chromium и WebKit, тренировка, один ответ): «Следующий
    // вопрос» оказывался на 1728…2682-м пикселе, и после автопрокрутки к вердикту
    // в первый экран он НЕ попадал на 390×844, 1280×720 и 2560×1440 в Chromium и
    // на четырёх из шести ширин в WebKit. Один и тот же момент продукта имел два
    // разных порядка — с JS и без него.
    //
    // Слот `#feedback-controls` — место, куда уверенность и «дальше» встают ПО
    // РАЗМЕТКЕ, а не по `order`: обе врезки содержат фокусируемое, и визуальная
    // перестановка развела бы порядок обхода с порядком чтения (FRD-184).
    feedbackDiv.innerHTML = `
      <div class="feedback-verdict result-banner ${isCorrect ? 'result-correct' : 'result-wrong'}">
        <strong class="result-verdict">${buildVerdictLine(data)}</strong>
        ${restoredSelection ? '<p class="result-replay-note">Повторная отправка: показан ранее сохранённый ответ.</p>' : ''}
      </div>
      <div class="feedback-meta">
        ${buildXpBadge(data)}
        ${buildNextReviewLine(data)}
      </div>
      <div class="feedback-controls" id="feedback-controls"></div>
      ${tablist}
      ${panelsHtml}
    `;
    if (panels.length > 1) {
      initTablist(document.getElementById('feedback-tablist'));
    }
    feedbackDiv.classList.remove('hidden');
    renderDynamicContent(feedbackDiv);
    // WCAG 4.1.3: лаконичный статус — только строка вердикта, а не весь разбор.
    if (announceEl) {
      announceEl.textContent = feedbackDiv.querySelector('.result-verdict')?.textContent?.trim() || '';
    }
  }

  function applyOptionStyles(data) {
    const labels = optionsContainer.querySelectorAll('label[data-option-id]');
    optionInputs.forEach((input) => {
      input.checked = Number(input.value) === data.selectedOptionId;
      input.disabled = true;
    });
    // «Выбери один вариант» над списком, в котором уже ничего не выбрать, —
    // инструкция к действию, которое кончилось. Снимаем вместе с блокировкой
    // радиокнопок: с этого момента список читается как разбор, а не как форма.
    // aria-describedby на радиогруппе указывает на удалённый id — это валидно
    // (ссылка на несуществующий элемент просто игнорируется), но и группа уже
    // disabled, так что описание всё равно не озвучивается.
    document.getElementById('options-flow-hint')?.remove();
    labels.forEach((label) => {
      const optionId = parseInt(label.getAttribute('data-option-id'), 10);
      const optionText = label.querySelector('span')?.textContent?.trim() || '';
      let statusLabelText = '';
      if (optionId === data.correctOptionId) {
        label.classList.add('option-correct');
        statusLabelText = 'Правильный ответ';
      } else if (optionId === data.selectedOptionId) {
        label.classList.add('option-wrong');
        // Без «(ошибка)»: вариант уже в красной рамке, а вердикт над списком
        // сказал, что ответ неверный. Формулировка ровно та же, что печатает
        // сервер на result.html — один компонент, один текст.
        statusLabelText = 'Твой выбор';
      } else {
        // Нейтральный вариант: приглушаем визуально, но БЕЗ метки-чипа —
        // зеркалим канон server-шаблона result.html (UX-13: «опции = только
        // марка состояния»). Три чипа «Не выбран» на каждый отвеченный вопрос
        // = шум; значимы лишь «Правильный ответ» и «Твой выбор». option-dimmed
        // достаточно как affordance «это не ответ и ты его не выбирал».
        label.classList.add('option-dimmed');
      }
      if (statusLabelText) {
        label.setAttribute('aria-label', (statusLabelText + ': ' + optionText).trim());
        const statusLabel = document.createElement('span');
        // Ветки ровно две: чип ставится только когда statusLabelText непуст, а он
        // непуст только для верного и для выбранного. Третьей марки («не выбран»)
        // здесь быть не может — нейтральный вариант уходит в option-dimmed выше.
        statusLabel.className = 'option-status-label ' + (optionId === data.correctOptionId
          ? 'option-status-correct'
          : 'option-status-selected');
        statusLabel.textContent = statusLabelText;
        label.appendChild(statusLabel);
      }
    });
  }

  /**
   * Похожие вопросы. Раньше они лежали за отдельной кнопкой «Похожие вопросы» и
   * дописывались в зону #extra-analysis-content ПОД разбором — то есть под лентой в
   * полторы тысячи пикселей, куда доскроллить успевали немногие. Теперь это третья
   * вкладка разбора: то же содержимое, но в одном переключателе с остальными
   * объяснениями, без второй кнопки и второй зоны.
   */
  function buildRelatedPanel(data) {
    if (!data.relatedQuestions?.length) return '';
    const currentGroup = form.querySelector('input[name="group"]')?.value || '';
    const currentOrdered = form.querySelector('input[name="ordered"]')?.value || 'true';
    const currentImportant = form.querySelector('input[name="important"]')?.value === 'true';
    const currentOnlyWrong = form.querySelector('input[name="onlyWrong"]')?.value === 'true';
    const currentShuffle = form.querySelector('input[name="shuffle"]')?.value === 'true';
    const currentWeakTopics = form.querySelector('input[name="weakTopics"]')?.value === 'true';

    let relatedHtml = '<h2 class="related-questions-title">' + icon('link', 'ui-icon-lead') + 'Похожие вопросы для закрепления:</h2>';
    relatedHtml += '<div class="related-questions-list" role="list">';
    data.relatedQuestions.forEach((rq) => {
      relatedHtml += '<div role="listitem"><a class="related-question-item" href="/practice?topic=' + encodeURIComponent(rq.topic)
        + '&questionId=' + encodeURIComponent(rq.id)
        + '&group=' + encodeURIComponent(currentGroup)
        + '&ordered=' + encodeURIComponent(currentOrdered)
        + (currentImportant ? '&important=true' : '')
        + (currentOnlyWrong ? '&onlyWrong=true' : '')
        + (currentShuffle ? '&shuffle=true' : '')
        + (currentWeakTopics ? '&weakTopics=true' : '')
        + '">' + escapeHtml(rq.text) + '</a></div>';
    });
    relatedHtml += '</div>';
    return '<div class="related-questions">' + relatedHtml + '</div>';
  }

  function attachConfidenceButtons(data) {
    if (!data.correct) return;
    const confidenceDiv = document.createElement('div');
    confidenceDiv.className = 'confidence-buttons';
    confidenceDiv.setAttribute('role', 'group');
    confidenceDiv.setAttribute('aria-labelledby', 'confidence-label');
    confidenceDiv.setAttribute('aria-live', 'polite');
    confidenceDiv.innerHTML = `
      <span class="confidence-label" id="confidence-label">Насколько ты уверен по этому вопросу?</span>
      <button type="button" class="confidence-btn confidence-guess" data-grade="3" aria-pressed="false" aria-label="Уровень уверенности: угадал">Угадал</button>
      <button type="button" class="confidence-btn confidence-hard" data-grade="4" aria-pressed="false" aria-label="Уровень уверенности: с трудом">С трудом</button>
      <button type="button" class="confidence-btn confidence-sure" data-grade="5" aria-pressed="false" aria-label="Уровень уверенности: знал точно">Знал точно</button>
    `;
    // Уверенность спрашивают о ТОЛЬКО ЧТО прошедшем ответе — её место рядом с
    // вердиктом, до разбора, а не после него.
    (document.getElementById('feedback-controls') || feedbackDiv).appendChild(confidenceDiv);

    let confidenceSubmitted = false;
    const questionId = (form.querySelector('input[name="questionId"]') || {}).value || '';
    const confidenceBtns = confidenceDiv.querySelectorAll('.confidence-btn');
    confidenceBtns.forEach((btn) => {
      btn.addEventListener('click', async () => {
        if (confidenceSubmitted) return;
        confidenceSubmitted = true;
        confidenceBtns.forEach((b) => { b.disabled = true; });
        const grade = btn.getAttribute('data-grade');
        try {
          const confidenceResp = await apiFetch(
            API.CONFIDENCE + '?questionId=' + encodeURIComponent(questionId)
              + '&grade=' + encodeURIComponent(grade),
            { method: 'POST' }
          );
          if (!confidenceResp.ok) throw await parseApiError(confidenceResp);
          clearInlineAlert();
        } catch (err) {
          console.error('Confidence update failed:', err);
          confidenceSubmitted = false;
          confidenceBtns.forEach((b) => { b.disabled = false; });
          setInlineAlert('Не удалось сохранить уверенность. Попробуй ещё раз.');
          return;
        }
        confidenceBtns.forEach((b) => {
          b.classList.remove('selected');
          b.setAttribute('aria-pressed', 'false');
          b.disabled = true;
        });
        btn.classList.add('selected');
        btn.setAttribute('aria-pressed', 'true');
      });
    });
  }

  function buildNextQuestionHref(excludeQuestionId) {
    const params = new URLSearchParams();
    const bind = (name) => {
      const input = form.querySelector(`input[name="${name}"]`);
      if (input?.value) params.set(name, input.value);
    };
    bind('topic');
    bind('group');
    bind('ordered');
    ['important', 'onlyWrong', 'shuffle', 'weakTopics'].forEach((name) => {
      const input = form.querySelector(`input[name="${name}"]`);
      if (input?.value === 'true') params.set(name, 'true');
    });
    if (excludeQuestionId) params.set('excludeQuestionId', String(excludeQuestionId));
    const query = params.toString();
    // Stay on /review surface when answering in mistake-review mode.
    const base = location.pathname.startsWith('/review') ? '/review' : '/practice';
    return query ? base + '?' + query : base;
  }

  function showResult(data) {
    const questionId = (form.querySelector('input[name="questionId"]') || {}).value || '';
    const selected = form.querySelector('input[name="optionId"]:checked');
    const restoredSelection = selected && Number(selected.value) !== data.selectedOptionId;
    applyOptionStyles(data);
    submitBtn.classList.add('hidden');
    nextLink.textContent = 'Следующий вопрос';
    nextLink.href = buildNextQuestionHref(questionId);
    delete nextLink.dataset.finishSession;
    renderFeedbackHtml(data, restoredSelection);
    if (answerFlowHint) answerFlowHint.classList.remove('hidden');

    const progressTrackEl = document.querySelector('.session-progress-track');
    const prevSessionTotal = progressTrackEl
      ? Number.parseInt(progressTrackEl.getAttribute('aria-valuemax') || '0', 10)
      : 0;
    updateSessionProgress(data);
    if (data.session) {
      if (prevSessionTotal > 0 && data.session.total > prevSessionTotal) {
        appendPenaltyNotice(data.session.total - prevSessionTotal, data.session.total);
      }
      if (data.session.finished) {
        nextLink.textContent = 'Итоги сессии';
        nextLink.dataset.finishSession = 'true';
      }
    }
    refreshStatsFromServer(form);
    attachConfidenceButtons(data);
    nextLink.classList.remove('hidden');

    // «Следующий вопрос» переезжает в слот управления сразу под вердиктом и мета.
    // Прежде он дописывался в конец пост-ответной зоны, то есть ПОСЛЕ всего
    // разбора; откат к прежнему месту — только если слота почему-то нет.
    const controlsSlot = document.getElementById('feedback-controls');
    const postAnswerZone = controlsSlot
      || document.querySelector('[data-ui-fragment="post-answer-controls"]');
    if (postAnswerZone && nextLink.parentElement !== postAnswerZone) {
      postAnswerZone.appendChild(nextLink);
      const trainingActions = document.querySelector('[data-ui-fragment="training-actions"]');
      if (trainingActions) trainingActions.classList.add('is-answered');
    }
    setInteractionBusy(false);
    // ПРОКРУТКА ЦЕЛИТСЯ В ВЕРХ РАЗБОРА, А НЕ «КУДА ПОЛУЧИТСЯ». Раньше её делал сам
    // `focus()`, и точка остановки зависела от движка и высоты панели: замер WebKit
    // 390×844 на длинном вопросе (документ 4296) показал прокрутку на 2241 при
    // вердикте на 1805 — экран уезжал НИЖЕ вердикта, а вместе с ним и «Следующий
    // вопрос», который теперь стоит сразу под ним. Фокус ставим без прокрутки, а
    // прокрутку задаём явно: верх приёмника к верху экрана.
    feedbackDiv.setAttribute('tabindex', '-1');
    feedbackDiv.focus({ preventScroll: true });
    feedbackDiv.scrollIntoView({ block: 'start' });

    nextLink.onclick = (e) => {
      if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
      if (!nextLink.href) return;
      e.preventDefault();
      if (nextLink.dataset.finishSession === 'true') {
        confirmModal({
          title: 'Завершить сессию?',
          message: 'Прогресс сохранится в итогах. Перейти к итогам сессии?'
        }).then((ok) => {
          if (!ok) return;
          const csrfInput = form.querySelector('input[name="_csrf"]');
          const finishForm = document.createElement('form');
          finishForm.method = 'post';
          finishForm.action = '/finish';
          if (csrfInput) {
            const csrf = document.createElement('input');
            csrf.type = 'hidden';
            csrf.name = csrfInput.name;
            csrf.value = csrfInput.value;
            finishForm.appendChild(csrf);
          }
          document.body.appendChild(finishForm);
          finishForm.submit();
        });
        return;
      }
      window.location.href = nextLink.href;
    };
    nextLink.onkeydown = (e) => {
      if (e.key === ' ') {
        e.preventDefault();
        nextLink.click();
      }
    };
  }

  function isSkipSubmit(event) {
    const submitter = event.submitter;
    if (!(submitter instanceof HTMLElement)) return false;
    const action = (submitter.getAttribute('formaction') || submitter.formAction || '').toString();
    return /\/skip(?:\?|$)/.test(action);
  }

  async function handleSubmit(event) {
    // Ответ и «Не знаю» завершают один вопрос: пока один исход отправляется
    // или уже принят, другой submit не должен запускать конкурирующий POST.
    if (answered || form.dataset.skipSubmitted === '1') {
      event.preventDefault();
      return;
    }

    // Skip uses formaction=/skip — let the browser POST (no option required).
    if (isSkipSubmit(event)) {
      form.dataset.skipSubmitted = '1';
      return;
    }

    event.preventDefault();

    const selected = form.querySelector('input[name="optionId"]:checked');
    if (!selected) {
      setInlineAlert('Сначала выбери один вариант ответа.');
      if (optionInputs[0]) optionInputs[0].focus();
      return;
    }

    // Capture the checked radio before disabling the controls: disabled fields
    // are deliberately omitted by FormData.
    const formData = new FormData(form);
    answered = true;
    setPending(submitBtn, 'Проверяю…');
    if (optionsContainer.contains(document.activeElement)) submitBtn.focus({ preventScroll: true });
    setInteractionBusy(true);
    clearInlineAlert();

    formData.set('clientAttemptId', currentClientAttemptId());
    formData.set('responseTimeMs', String(thinkTime.currentResponseTimeMs()));

    try {
      const response = await apiPostJson(API.ANSWER, formDataToAnswerPayload(formData));

      if (!response.ok) {
        const conflict = response.status === 409 ? await parseApiError(response) : null;
        answered = false;
        // Голос отдаём тексту отказа ниже (setInlineAlert), поэтому регион
        // ожидания гасим пустой строкой, а не объявляем «готово».
        clearPending(submitBtn, '');
        setInteractionBusy(false);
        updateSubmitAvailability();
        thinkTime.markQuestionShown();
        setInlineAlert(conflict
          ? conflict.detail || 'Не удалось восстановить попытку. Обнови страницу и повтори действие.'
          : 'Не удалось проверить ответ. Выбранный вариант сохранён — повтори отправку.');
        if (document.activeElement === document.body) submitBtn.focus({ preventScroll: true });
        return;
      }

      const data = await response.json();
      // Вердикт объявляет #result-announce — регион ожидания замолкает.
      clearPending(submitBtn, '');
      showResult(data);
    } catch (err) {
      console.error('AJAX answer failed:', err);
      answered = false;
      clearPending(submitBtn, '');
      setInteractionBusy(false);
      updateSubmitAvailability();
      thinkTime.markQuestionShown();
      setInlineAlert('Сервер недоступен. Проверь соединение и повтори отправку.');
      if (document.activeElement === document.body) submitBtn.focus({ preventScroll: true });
    }
  }

  form.addEventListener('submit', handleSubmit);
}
