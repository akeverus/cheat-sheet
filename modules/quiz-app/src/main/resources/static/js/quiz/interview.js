import { apiFetch, parseApiError } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { icon, escapeHtml, pluralRu, pluralDays } from '../core/dom.js';
import { sanitizeHtml } from '../content/sanitize.js';
import { renderDynamicContent } from '../content/dynamic-content.js';
import { clearInlineAlert, setInlineAlert } from '../ui/alerts.js';
import { confirmModal } from '../ui/modals.js';
import { refreshStatsFromServer, updateSessionProgress } from './stats.js';

let interviewBound = false;

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
  const extraAnalysisBtn = document.getElementById('extra-analysis-toggle');
  const answerFlowHint = document.getElementById('answer-flow-hint');
  if (!form || !optionsContainer) return;
  interviewBound = true;

  const thinkTime = createThinkTimeTracker();
  thinkTime.markQuestionShown();

  const optionInputs = Array.from(form.querySelectorAll('input[name="optionId"]'));
  const ANSWER_LAYOUT_SKEW_THRESHOLD = 1.4;
  let answered = false;
  let clientAttemptId = null;
  let clientAttemptQuestionId = null;

  // #result-feedback НЕ live-регион (иначе SR зачитывает весь ~2900-симв. разбор атомарно).
  // Краткий вердикт анонсируется через #result-announce; сюда переносится focus (см. ниже).
  const announceEl = document.getElementById('result-announce');

  function setInteractionBusy(busy) {
    form.setAttribute('aria-busy', busy ? 'true' : 'false');
    optionsContainer.setAttribute('aria-disabled', busy ? 'true' : 'false');
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

  function updateAnswerLayoutMode() {
    const labels = Array.from(optionsContainer.querySelectorAll('label[data-option-id]'));
    if (labels.length < 2) return;
    const lengths = labels.map((label) => {
      const text = label.querySelector('span')?.textContent || '';
      return Math.max(1, text.replace(/\s+/g, ' ').trim().length);
    });
    const minLength = Math.min(...lengths);
    const maxLength = Math.max(...lengths);
    const lengthRatio = maxLength / minLength;
    const inlineCodeCounts = labels.map((label) => label.querySelectorAll('span:not([class]) code').length);
    const hasInlineCodeSkew = Math.max(...inlineCodeCounts) > Math.min(...inlineCodeCounts);
    const forceSingleColumn = lengthRatio > ANSWER_LAYOUT_SKEW_THRESHOLD || hasInlineCodeSkew;
    optionsContainer.dataset.answerLayout = forceSingleColumn ? 'single' : 'balanced';
    optionsContainer.dataset.answerSkew = lengthRatio.toFixed(2);
    if (forceSingleColumn) {
      optionsContainer.dataset.answerLayoutReason = hasInlineCodeSkew ? 'inline-code-skew' : 'length-skew';
    } else {
      delete optionsContainer.dataset.answerLayoutReason;
    }
  }

  function updateSubmitAvailability() {
    if (answered) return;
    const selected = form.querySelector('input[name="optionId"]:checked');
    if (submitBtn) {
      const armed = !!selected;
      submitBtn.disabled = !armed;
      submitBtn.classList.toggle('is-disabled', !armed);
      submitBtn.classList.toggle('is-armed', armed);
      submitBtn.textContent = armed ? 'Проверить выбранный ответ' : 'Проверить ответ';
    }
  }

  function selectOptionByIndex(index) {
    const target = optionInputs[index];
    if (!target) return false;
    target.checked = true;
    target.focus();
    target.dispatchEvent(new Event('change', { bubbles: true }));
    updateSubmitAvailability();
    return true;
  }

  updateAnswerLayoutMode();
  updateSubmitAvailability();

  optionInputs.forEach((input) => {
    input.addEventListener('change', updateSubmitAvailability);
  });

  if (submitBtn) {
    submitBtn.setAttribute('title', `Клавиши 1–${Math.min(optionInputs.length, 9)} — выбор, Enter — ответить`);
  }

  document.addEventListener('keydown', (event) => {
    if (document.querySelector('.kbd-help-overlay:not(.hidden)')) return;
    if (answered) return;
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName) && event.target.type !== 'radio') return;
    if (event.target && (event.key === 'Enter' || event.key === ' ') &&
        ['SUMMARY', 'BUTTON', 'A'].includes(event.target.tagName)) return;
    if (event.target?.type === 'radio' && event.key === 'Enter') {
      event.preventDefault();
      form.dispatchEvent(new Event('submit', { cancelable: true }));
      return;
    }
    const key = event.key;
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

  function buildVerdictLine(data) {
    if (data.correct) return icon('circle-check', 'ed-icon-lead') + 'Верно';
    const selected = data.selectedOptionLetter
      ? 'Ты выбрал ' + escapeHtml(data.selectedOptionLetter) + '. '
      : '';
    const letter = data.correctOptionLetter
      ? 'Правильный ответ ' + escapeHtml(data.correctOptionLetter)
      : 'Неверно';
    return icon('circle-x', 'ed-icon-lead') + selected + letter;
  }

  function buildXpBadge(data) {
    const xp = Number(data.xpAwarded || 0);
    if (!(xp > 0)) return '';
    return `<span class="result-xp" aria-label="Начислено ${xp} XP">+${xp} XP</span>`;
  }

  function buildNextReviewLine(data) {
    const days = Number(data.intervalDays || 0);
    const when = days > 0
      ? (days === 1 ? 'завтра' : ('через ' + days + ' ' + pluralDays(days)))
      : '—';
    return `<p class="next-review">`
      + `<span class="next-review-eyebrow">Следующее повторение:</span> `
      + `<span class="next-review-when">${when}</span></p>`
      + `<p class="review-policy muted tiny">`
      + `Интервальные повторения: верный ответ отодвигает карточку, ошибка возвращает её раньше.`
      + `</p>`;
  }

  function buildCorrectAnswerBlock(data) {
    const letter = data.correctOptionLetter;
    if (!letter) return '';
    const label = optionsContainer.querySelector(`label[data-option-id="${data.correctOptionId}"] span`);
    const text = label ? (label.textContent || '').trim() : '';
    return `<div class="feedback-correct-answer">`
      + `<span class="feedback-block-eyebrow">Правильный ответ</span>`
      + `<p class="feedback-correct-answer-line">`
      + `<strong class="feedback-option-letter">${escapeHtml(letter)}.</strong> `
      + (text ? escapeHtml(text) : '')
      + `</p></div>`;
  }

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

  function renderFeedbackHtml(data) {
    const isCorrect = data.correct;
    const leadHtml = sanitizeHtml(data.answerLeadHtml || '');
    const restHtml = sanitizeHtml(data.answerRestHtml || '');
    const hasLead = leadHtml.trim().length > 0;
    const fullHtml = sanitizeHtml(data.answerHtml || '');
    const keyDifferencePanel = hasLead
      ? `<div class="feedback-key-difference explanation-panel">`
        + `<span class="explanation-eyebrow">Ключевое отличие</span>`
        + `<div class="result-lead answer markdown-content">${leadHtml}</div>`
        + `</div>`
      : '';
    const explanationPanel = (!hasLead && fullHtml.trim())
      ? `<div class="feedback-explanation explanation-panel">`
        + `<span class="explanation-eyebrow">Почему это так?</span>`
        + `<div class="result-lead answer markdown-content">${fullHtml}</div>`
        + `</div>`
      : '';
    const additionalPanel = (hasLead && restHtml.trim())
      ? `<details class="feedback-explanation explanation-panel additional-panel explanation-more">`
        + `<summary class="explanation-eyebrow">Подробнее</summary>`
        + `<div class="answer markdown-content">${restHtml}</div>`
        + `</details>`
      : '';

    feedbackDiv.innerHTML = `
      <div class="feedback-flow">
        <div class="feedback-verdict ${isCorrect ? 'result-correct' : 'result-wrong'} result-banner">
          <strong class="result-verdict">${buildVerdictLine(data)}</strong>
        </div>
        ${buildCorrectAnswerBlock(data)}
        ${keyDifferencePanel}
        ${explanationPanel}
        ${buildDistractorPanel(data)}
        ${additionalPanel}
        <div class="feedback-meta">
          ${buildXpBadge(data)}
          ${buildNextReviewLine(data)}
        </div>
      </div>
    `;
    feedbackDiv.classList.remove('hidden');
    renderDynamicContent(feedbackDiv);
    // WCAG 4.1.3: лаконичный статус — только строка вердикта, а не весь разбор.
    if (announceEl) {
      announceEl.textContent = feedbackDiv.querySelector('.result-verdict')?.textContent?.trim() || '';
    }
    const hasExplanation = hasLead || fullHtml.trim().length > 0;
    const celebrateDelay = hasExplanation ? 360 : 80;
    window.setTimeout(() => {
      if (window.__pixelFx?.celebrate) {
        window.__pixelFx.celebrate(!!isCorrect, { xp: Number(data.xpAwarded || 0) });
      }
    }, celebrateDelay);
  }

  function applyOptionStyles(data) {
    const labels = optionsContainer.querySelectorAll('label[data-option-id]');
    optionInputs.forEach((input) => { input.disabled = true; });
    labels.forEach((label) => {
      const optionId = parseInt(label.getAttribute('data-option-id'), 10);
      const optionText = label.querySelector('span')?.textContent?.trim() || '';
      let statusLabelText = '';
      if (optionId === data.correctOptionId) {
        label.classList.add('option-correct');
        statusLabelText = 'Правильный ответ';
      } else if (optionId === data.selectedOptionId) {
        label.classList.add('option-wrong');
        statusLabelText = 'Твой выбор (ошибка)';
      } else {
        label.classList.add('option-dimmed');
        statusLabelText = 'Не выбран';
      }
      if (statusLabelText) {
        label.setAttribute('aria-label', (statusLabelText + ': ' + optionText).trim());
        const statusLabel = document.createElement('span');
        statusLabel.className = 'option-status-label ' + (optionId === data.correctOptionId
          ? 'option-status-correct'
          : (optionId === data.selectedOptionId ? 'option-status-selected' : 'option-status-muted'));
        statusLabel.textContent = statusLabelText;
        label.appendChild(statusLabel);
      }
    });
  }

  function appendAnalysisBlock(el) {
    const sink = document.getElementById('extra-analysis-content');
    if (sink) sink.appendChild(el);
    else feedbackDiv.after(el);
  }

  function renderRelatedQuestions(data) {
    if (!data.relatedQuestions?.length) return;
    const currentGroup = form.querySelector('input[name="group"]')?.value || '';
    const currentOrdered = form.querySelector('input[name="ordered"]')?.value || 'true';
    const currentImportant = form.querySelector('input[name="important"]')?.value === 'true';
    const currentOnlyWrong = form.querySelector('input[name="onlyWrong"]')?.value === 'true';
    const currentShuffle = form.querySelector('input[name="shuffle"]')?.value === 'true';
    const currentWeakTopics = form.querySelector('input[name="weakTopics"]')?.value === 'true';

    const relatedDiv = document.createElement('div');
    relatedDiv.className = 'related-questions';
    let relatedHtml = '<h3 class="related-questions-title">' + icon('link', 'ed-icon-lead') + 'Похожие вопросы для закрепления:</h3>';
    relatedHtml += '<div class="related-questions-list" role="list">';
    data.relatedQuestions.forEach((rq) => {
      relatedHtml += '<div role="listitem"><a class="related-question-item" href="/?topic=' + encodeURIComponent(rq.topic)
        + '&group=' + encodeURIComponent(currentGroup)
        + '&ordered=' + encodeURIComponent(currentOrdered)
        + (currentImportant ? '&important=true' : '')
        + (currentOnlyWrong ? '&onlyWrong=true' : '')
        + (currentShuffle ? '&shuffle=true' : '')
        + (currentWeakTopics ? '&weakTopics=true' : '')
        + '">' + escapeHtml(rq.text) + '</a></div>';
    });
    relatedHtml += '</div>';
    relatedDiv.innerHTML = relatedHtml;
    appendAnalysisBlock(relatedDiv);
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
    feedbackDiv.after(confidenceDiv);

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
          const confidenceResp = await apiFetch(API.CONFIDENCE, {
            method: 'POST',
            body: new URLSearchParams({ questionId, grade }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          });
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
    const base = location.pathname.startsWith('/review') ? '/review' : '/';
    return query ? base + '?' + query : base;
  }

  function showResult(data) {
    const questionId = (form.querySelector('input[name="questionId"]') || {}).value || '';
    applyOptionStyles(data);
    submitBtn.classList.add('hidden');
    nextLink.textContent = 'Следующий вопрос';
    nextLink.href = buildNextQuestionHref(questionId);
    delete nextLink.dataset.finishSession;
    renderFeedbackHtml(data);
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

    const postAnswerZone = document.querySelector('[data-ui-fragment="post-answer-controls"]');
    if (postAnswerZone && nextLink.parentElement !== postAnswerZone) {
      postAnswerZone.appendChild(nextLink);
      const trainingActions = document.querySelector('[data-ui-fragment="training-actions"]');
      if (trainingActions) trainingActions.classList.add('is-answered');
    }
    setInteractionBusy(false);
    feedbackDiv.setAttribute('tabindex', '-1');
    feedbackDiv.focus();

    const hasRelated = Array.isArray(data.relatedQuestions) && data.relatedQuestions.length > 0;
    if (extraAnalysisBtn && hasRelated) {
      extraAnalysisBtn.classList.remove('hidden');
      extraAnalysisBtn.removeAttribute('aria-disabled');
      extraAnalysisBtn.setAttribute('aria-expanded', 'false');
      let relatedRevealed = false;
      extraAnalysisBtn.onclick = () => {
        if (relatedRevealed) return;
        relatedRevealed = true;
        extraAnalysisBtn.setAttribute('aria-expanded', 'true');
        renderRelatedQuestions(data);
        extraAnalysisBtn.classList.add('hidden');
      };
    } else if (extraAnalysisBtn) {
      extraAnalysisBtn.classList.add('hidden');
    }

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
      if (window.__pixelFx?.portalWipe) {
        window.__pixelFx.portalWipe(() => { window.location.href = nextLink.href; });
      } else {
        window.location.href = nextLink.href;
      }
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
    // Skip uses formaction=/skip — let the browser POST (no option required).
    if (isSkipSubmit(event)) {
      if (form.dataset.skipSubmitted === '1') {
        event.preventDefault();
        return;
      }
      form.dataset.skipSubmitted = '1';
      return;
    }

    event.preventDefault();
    if (answered) return;

    const selected = form.querySelector('input[name="optionId"]:checked');
    if (!selected) {
      setInlineAlert('Сначала выбери один вариант ответа.');
      if (optionInputs[0]) optionInputs[0].focus();
      return;
    }

    answered = true;
    const originalSubmitText = (submitBtn.textContent || '').trim() || 'Проверить ответ';
    submitBtn.disabled = true;
    submitBtn.classList.add('is-submitting');
    submitBtn.textContent = 'Проверяю…';
    submitBtn.setAttribute('aria-busy', 'true');
    setInteractionBusy(true);
    clearInlineAlert();

    const formData = new FormData(form);
    formData.set('clientAttemptId', currentClientAttemptId());
    formData.set('responseTimeMs', String(thinkTime.currentResponseTimeMs()));

    try {
      const response = await apiFetch(API.ANSWER, {
        method: 'POST',
        body: new URLSearchParams(formData),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      });

      if (!response.ok) {
        answered = false;
        submitBtn.disabled = false;
        submitBtn.classList.remove('is-submitting');
        submitBtn.textContent = originalSubmitText;
        submitBtn.removeAttribute('aria-busy');
        setInteractionBusy(false);
        updateSubmitAvailability();
        thinkTime.markQuestionShown();
        setInlineAlert('Не удалось проверить ответ — возможно, вопрос устарел. Обнови страницу и загрузи новый вопрос.');
        if (document.activeElement === document.body) submitBtn.focus({ preventScroll: true });
        return;
      }

      const data = await response.json();
      submitBtn.removeAttribute('aria-busy');
      showResult(data);
    } catch (err) {
      console.error('AJAX answer failed:', err);
      answered = false;
      submitBtn.disabled = false;
      submitBtn.classList.remove('is-submitting');
      submitBtn.textContent = originalSubmitText;
      submitBtn.removeAttribute('aria-busy');
      setInteractionBusy(false);
      updateSubmitAvailability();
      thinkTime.markQuestionShown();
      setInlineAlert('Сервер недоступен. Проверь соединение и повтори отправку.');
      if (document.activeElement === document.body) submitBtn.focus({ preventScroll: true });
    }
  }

  form.addEventListener('submit', handleSubmit);
}
