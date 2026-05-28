(() => {
  const UI_CONSTANTS = Object.freeze({
    EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT: 'Показать доп. анализ',
    EXTRA_ANALYSIS_BUTTON_LOADING_TEXT: 'Загружаю...',
    EXTRA_ANALYSIS_BUTTON_DONE_TEXT: 'Доп. анализ загружен',
    FAVORITE_ADD_LABEL: 'Добавить в избранное',
    FAVORITE_REMOVE_LABEL: 'Убрать из избранного',
    LEARNING_PREFS_STORAGE_KEY: 'quiz.learning.prefs.v2',
    METRICS_STORAGE_KEY: 'quiz.ux.metrics.v2'
  });
  const API = {
    ANSWER: '/api/answer',
    NEXT: '/api/next',
    STATS: '/api/stats',
    TOPIC_STATS: '/api/topic-stats',
    REGENERATE: '/api/regenerate',
    HINT: '/api/hint',
    CONFIDENCE: '/api/confidence',
    WRONG_FEEDBACK: '/api/wrong-feedback',
    FAVORITE: '/api/favorite',
    TAKEAWAY: '/api/takeaway',
    COMPARISON: '/api/comparison',
    CODE_TRACE: '/api/code-trace',
    STREAK: '/api/streak'
  };
  const EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT = UI_CONSTANTS.EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;
  const EXTRA_ANALYSIS_BUTTON_LOADING_TEXT = UI_CONSTANTS.EXTRA_ANALYSIS_BUTTON_LOADING_TEXT;
  const EXTRA_ANALYSIS_BUTTON_DONE_TEXT = UI_CONSTANTS.EXTRA_ANALYSIS_BUTTON_DONE_TEXT;
  const FAVORITE_ADD_LABEL = UI_CONSTANTS.FAVORITE_ADD_LABEL;
  const FAVORITE_REMOVE_LABEL = UI_CONSTANTS.FAVORITE_REMOVE_LABEL;
  const LEARNING_PREFS_STORAGE_KEY = UI_CONSTANTS.LEARNING_PREFS_STORAGE_KEY;
  const METRICS_STORAGE_KEY = UI_CONSTANTS.METRICS_STORAGE_KEY;
  const defaultLearningPrefs = Object.freeze({
    instantMode: false,
    hardMode: false,
    reviewMode: false,
    adaptiveMode: false,
    timerSeconds: 0
  });
  let learningPrefs = { ...defaultLearningPrefs };

  function setProgressValue(element, value) {
    if (!element) return;
    const numeric = Number.parseFloat(String(value).replace(',', '.'));
    const safe = Number.isFinite(numeric) ? Math.max(0, Math.min(100, numeric)) : 0;
    element.setAttribute('data-progress', safe.toFixed(1));
    element.style.width = safe.toFixed(1) + '%';
  }

  // Централизованная инициализация progress-bar из data-progress.
  function hydrateProgressBarsFromData() {
    document.querySelectorAll('[data-progress]').forEach((el) => {
      setProgressValue(el, el.getAttribute('data-progress') || '0');
    });
  }

  function apiFetch(url, options = {}) {
    return fetch(url, { credentials: 'same-origin', ...options });
  }

  function parseApiError(response) {
    return response.text()
      .then(raw => {
        if (!raw) return new Error('HTTP ' + response.status);
        try {
          const payload = JSON.parse(raw);
          const message = payload?.message || payload?.error || ('HTTP ' + response.status);
          return new Error(message);
        } catch (_) {
          return new Error(raw);
        }
      })
      .catch(() => new Error('HTTP ' + response.status));
  }

  function setInlineAlert(message, kind = 'error') {
    const alertEl = document.getElementById('interview-alert');
    if (!alertEl) return;
    alertEl.textContent = message;
    alertEl.classList.remove('hidden', 'success');
    if (kind === 'success') {
      alertEl.classList.add('success');
    }
  }

  function clearInlineAlert() {
    const alertEl = document.getElementById('interview-alert');
    if (!alertEl) return;
    alertEl.textContent = '';
    alertEl.classList.add('hidden');
    alertEl.classList.remove('success');
  }

  function initCollapsibleSidebar() {
    const toggle = document.getElementById('sidebar-collapse-toggle');
    const content = document.getElementById('left-sidebar-content');
    const sidebarCard = document.getElementById('left-sidebar-card');
    if (!toggle || !content || !sidebarCard) return;

    const setCollapsed = (collapsed) => {
      sidebarCard.classList.toggle('is-collapsed', collapsed);
      content.hidden = collapsed;
      toggle.setAttribute('aria-expanded', collapsed ? 'false' : 'true');
      toggle.textContent = collapsed ? 'Развернуть панель' : 'Свернуть панель';
    };

    setCollapsed(false);
    toggle.addEventListener('click', () => {
      const collapsed = toggle.getAttribute('aria-expanded') === 'true';
      setCollapsed(collapsed);
    });
  }

  function setInteractionBusy(busy) {
    if (form) {
      form.setAttribute('aria-busy', busy ? 'true' : 'false');
    }
    if (optionsContainer) {
      optionsContainer.setAttribute('aria-disabled', busy ? 'true' : 'false');
    }
  }

  function trackUxMetric(metricName, payload = {}) {
    if (!metricName) return;
    try {
      const raw = sessionStorage.getItem(METRICS_STORAGE_KEY);
      const metrics = raw ? JSON.parse(raw) : {};
      metrics[metricName] = (metrics[metricName] || 0) + 1;
      metrics.__lastPayload = payload;
      metrics.__updatedAt = Date.now();
      sessionStorage.setItem(METRICS_STORAGE_KEY, JSON.stringify(metrics));
      window.dispatchEvent(new CustomEvent('quiz:ux-metric', {
        detail: {
          name: metricName,
          payload
        }
      }));
    } catch (_) {
      // Silent: telemetry must never break user flow.
    }
  }

  /**
   * Streak bar: fetch /api/streak and update DOM. No-op if #streak-bar is missing.
   */
  function initStreakBar() {
    const bar = document.getElementById('streak-bar');
    if (!bar) return;
    apiFetch(API.STREAK)
      .then(r => r.json())
      .then(d => {
        if (!d || typeof d !== 'object' || typeof d.streak !== 'number'
            || typeof d.goal !== 'number' || typeof d.today !== 'number') return;
        bar.classList.remove('hidden');
        const daysEl = document.getElementById('streak-days');
        if (daysEl) {
          const n = d.streak;
          daysEl.textContent = n + (n === 1 ? ' день' : (n >= 2 && n <= 4 ? ' дня' : ' дней'));
        }
        const pct = d.goal > 0 ? Math.min(100, (d.today / d.goal) * 100) : 0;
        const fillEl = document.getElementById('streak-progress-fill');
        if (fillEl) setProgressValue(fillEl, pct);
        const countEl = document.getElementById('streak-count');
        if (countEl) countEl.textContent = d.today + '/' + d.goal;
        if (d.goalReached) bar.classList.add('streak-goal-reached');
      })
      .catch(() => {
        setInlineAlert('Не удалось загрузить прогресс за день. Можно продолжать без него.');
      });
  }

  /**
   * Hint button: progressive AI hints. No-op if #btn-hint or #hint-container missing.
   */
  function initHintButton() {
    const btn = document.getElementById('btn-hint');
    const container = document.getElementById('hint-container');
    if (!btn || !container) return;

    btn.dataset.hintLevel = '0';
    const maxLevel = 3;
    const icons = ['💡', '💡💡', '💡💡💡'];

    window.__resetHintState = function resetHintState() {
      btn.dataset.hintLevel = '0';
      btn.disabled = false;
      btn.classList.remove('used', 'loading');
      btn.textContent = '💡';
      btn.title = 'Подсказка';
      container.innerHTML = '';
      container.classList.add('hidden');
      container.style.display = '';
    };

    btn.addEventListener('click', async () => {
      const currentLevel = Number.parseInt(btn.dataset.hintLevel || '0', 10);
      if (currentLevel >= maxLevel) return;

      const questionId = btn.getAttribute('data-question-id');
      if (!questionId) return;

      const nextLevel = currentLevel + 1;
      btn.classList.add('loading');
      btn.textContent = '⏳';

      try {
        const resp = await apiFetch(API.HINT, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: 'questionId=' + encodeURIComponent(questionId) + '&level=' + nextLevel
        });

        if (resp.ok) {
          clearInlineAlert();
          const data = await resp.json();
          container.classList.remove('hidden');
          container.style.display = 'block';

          const item = document.createElement('div');
          item.className = 'hint-item level-' + data.level;
          const iconSpan = document.createElement('span');
          iconSpan.className = 'hint-icon';
          iconSpan.textContent = icons[data.level - 1];
          const textSpan = document.createElement('span');
          textSpan.className = 'hint-text';
          textSpan.textContent = data.hint;
          item.appendChild(iconSpan);
          item.appendChild(document.createTextNode(' '));
          item.appendChild(textSpan);
          container.appendChild(item);

          btn.dataset.hintLevel = String(data.level);

          if (data.level >= maxLevel) {
            btn.classList.add('used');
            btn.textContent = '💡';
            btn.title = 'Все подсказки использованы';
            btn.disabled = true;
          } else {
            btn.textContent = '💡';
            btn.title = 'Подсказка (уровень ' + (data.level + 1) + '/' + maxLevel + ')';
          }
        } else {
          const err = await parseApiError(resp);
          setInlineAlert(err.message || 'Не удалось получить подсказку. Попробуйте ещё раз.');
          btn.textContent = '💡';
        }
      } catch (err) {
        console.error('Hint request failed:', err);
        setInlineAlert('Не удалось получить подсказку. Проверьте сеть и повторите.');
        btn.textContent = '💡';
      }

      btn.classList.remove('loading');
    });
  }

  /**
   * Shuffle/topic: when "Микс" is checked, disable topic select. No-op if elements missing.
   */
  function initShuffleTopic() {
    const shuffleBox = document.getElementById('shuffle-checkbox');
    const topicSelect = document.querySelector('select[name="topic"]');
    const groupSelect = document.querySelector('select[name="group"]');
    const orderedSelect = document.querySelector('select[name="ordered"]');
    const modeHint = document.getElementById('filter-mode-hint');
    if (!shuffleBox || !topicSelect) return;

    function setDisabled(selectEl, disabled) {
      if (!selectEl) return;
      selectEl.disabled = disabled;
      selectEl.classList.toggle('is-disabled-control', disabled);
    }

    function toggleTopic() {
      if (shuffleBox.checked) {
        setDisabled(topicSelect, true);
        setDisabled(groupSelect, true);
        setDisabled(orderedSelect, true);
        if (modeHint) {
          modeHint.textContent = 'Режим микс: вопросы идут случайно по всем темам.';
        }
      } else {
        setDisabled(topicSelect, false);
        setDisabled(groupSelect, false);
        setDisabled(orderedSelect, false);
        if (modeHint) {
          const orderedValue = orderedSelect?.value === 'false' ? 'Свободный' : 'Учебный';
          modeHint.textContent = orderedValue === 'Учебный'
            ? 'Учебный порядок: от базы к более сложным идеям.'
            : 'Свободный порядок: темы идут без фиксированной траектории.';
        }
      }
    }

    shuffleBox.addEventListener('change', toggleTopic);
    if (orderedSelect) orderedSelect.addEventListener('change', toggleTopic);
    toggleTopic();
  }

  /**
   * Syncs current filter controls to start-session form.
   * Prevents launching session with stale hidden parameters.
   */
  function initSessionFormSync() {
    const filtersForm = document.getElementById('filters-form');
    const sessionForm = document.getElementById('session-form');
    if (!filtersForm || !sessionForm) return;

    const countInput = sessionForm.querySelector('input[name="count"]');
    const sessionCountStorageKey = 'quiz.session.count';
    if (countInput) {
      const storedCount = Number.parseInt(localStorage.getItem(sessionCountStorageKey) || '', 10);
      if (!Number.isNaN(storedCount) && storedCount >= 1 && storedCount <= 200) {
        countInput.value = String(storedCount);
      }
      countInput.addEventListener('change', () => {
        const value = Number.parseInt(countInput.value || '', 10);
        if (!Number.isNaN(value) && value >= 1 && value <= 200) {
          localStorage.setItem(sessionCountStorageKey, String(value));
        }
      });
    }

    const getFilterField = (name) => filtersForm.querySelector('[name="' + name + '"]');
    const upsertHidden = (name, value) => {
      let hidden = sessionForm.querySelector('input[type="hidden"][name="' + name + '"]');
      if (value == null || value === '') {
        if (hidden) hidden.remove();
        return;
      }
      if (!hidden) {
        hidden = document.createElement('input');
        hidden.type = 'hidden';
        hidden.name = name;
        sessionForm.appendChild(hidden);
      }
      hidden.value = String(value);
    };

    sessionForm.addEventListener('submit', () => {
      const shuffleEnabled = !!getFilterField('shuffle')?.checked;
      const topicValue = shuffleEnabled ? '' : (getFilterField('topic')?.value || '');
      const groupValue = shuffleEnabled ? '' : (getFilterField('group')?.value || '');
      const orderedValue = getFilterField('ordered')?.value || 'true';

      upsertHidden('topic', topicValue);
      upsertHidden('group', groupValue);
      upsertHidden('ordered', orderedValue);
      upsertHidden('important', getFilterField('important')?.checked ? 'true' : '');
      upsertHidden('onlyWrong', getFilterField('onlyWrong')?.checked ? 'true' : '');
      upsertHidden('shuffle', getFilterField('shuffle')?.checked ? 'true' : '');
      upsertHidden('weakTopics', getFilterField('weakTopics')?.checked ? 'true' : '');
    });
  }

  function initResultPageExtraAnalysis() {
    const btn = document.getElementById('extra-analysis-toggle-result');
    const container = document.getElementById('result-extra-analysis');
    if (!btn || !container) return;

    const questionId = btn.getAttribute('data-question-id');
    const selectedOptionId = btn.getAttribute('data-selected-option-id');
    const isCorrect = btn.getAttribute('data-correct') === 'true';
    const related = document.getElementById('result-related-questions');
    if (!questionId) return;

    const addBlock = (className, html) => {
      const block = document.createElement('div');
      block.className = 'content-block ' + className;
      block.innerHTML = html;
      container.appendChild(block);
    };

    btn.addEventListener('click', async () => {
      if (btn.dataset.loaded === 'true') return;
      btn.disabled = true;
      btn.setAttribute('aria-busy', 'true');
      btn.textContent = EXTRA_ANALYSIS_BUTTON_LOADING_TEXT;
      container.classList.remove('hidden');
      container.innerHTML = '';
      clearInlineAlert();

      const requests = [];
      if (!isCorrect && selectedOptionId) {
        requests.push(
          apiFetch(API.WRONG_FEEDBACK, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'questionId=' + encodeURIComponent(questionId) + '&optionId=' + encodeURIComponent(selectedOptionId)
          })
            .then(async (resp) => {
              if (!resp.ok) throw await parseApiError(resp);
              const fb = await resp.json();
              if (fb.available && fb.feedback) {
                addBlock(
                  'wrong-feedback',
                  '<div class="wrong-feedback-title">💡 Почему это неверно:</div>' +
                  '<div class="wrong-feedback-text">' + escapeHtml(fb.feedback) + '</div>'
                );
              }
            })
        );
        requests.push(
          apiFetch(API.COMPARISON + '?questionId=' + encodeURIComponent(questionId) + '&selectedOptionId=' + encodeURIComponent(selectedOptionId))
            .then(async (resp) => {
              if (!resp.ok) throw await parseApiError(resp);
              const cmp = await resp.json();
              if (!cmp.comparison) return;
              const parsed = typeof cmp.comparison === 'string' ? JSON.parse(cmp.comparison) : cmp.comparison;
              if (!parsed.criteria || !parsed.criteria.length) return;
              let table = '<div class="comparison-title">📊 Сравнение: выбранное vs правильное</div>';
              table += '<table class="comparison-table"><tr><th>Критерий</th><th>Выбранное</th><th>Правильное</th></tr>';
              parsed.criteria.forEach(c => {
                table += '<tr><td>' + escapeHtml(c.criterion) + '</td><td>' + escapeHtml(c.selected) + '</td><td>' + escapeHtml(c.correct) + '</td></tr>';
              });
              table += '</table>';
              addBlock('comparison-block', table);
            })
        );
      }

      requests.push(
        apiFetch(API.TAKEAWAY + '?questionId=' + encodeURIComponent(questionId))
          .then(async (resp) => {
            if (!resp.ok) throw await parseApiError(resp);
            const tk = await resp.json();
            if (tk.takeaway) {
              addBlock(
                'takeaway-block',
                '<div class="takeaway-title">🎯 Главное, что нужно запомнить:</div>' +
                '<div class="takeaway-text">' + escapeHtml(tk.takeaway) + '</div>'
              );
            }
          })
      );

      requests.push(
        apiFetch(API.CODE_TRACE + '?questionId=' + encodeURIComponent(questionId))
          .then(async (resp) => {
            if (!resp.ok) throw await parseApiError(resp);
            const traceData = await resp.json();
            if (!traceData.trace) return;
            const parsed = typeof traceData.trace === 'string' ? JSON.parse(traceData.trace) : traceData.trace;
            if (!parsed.steps || !parsed.steps.length) return;
            let html = '<details><summary class="code-trace-title">🔍 Пошаговое выполнение кода</summary>';
            html += '<ol class="code-trace-steps">';
            parsed.steps.forEach(s => {
              html += '<li class="code-trace-step">';
              html += '<span class="trace-step-num">' + escapeHtml(s.step) + '</span>';
              if (s.line) html += '<span class="trace-step-line">' + escapeHtml(s.line) + '</span>';
              if (s.state) html += '<span class="trace-step-state">' + escapeHtml(s.state) + '</span>';
              html += '<span class="trace-step-explanation">' + escapeHtml(s.explanation) + '</span>';
              html += '</li>';
            });
            html += '</ol></details>';
            addBlock('code-trace-block', html);
          })
      );

      try {
        const results = await Promise.allSettled(requests);
        const failedCount = results.filter(r => r.status === 'rejected').length;
        btn.dataset.loaded = 'true';
        btn.textContent = EXTRA_ANALYSIS_BUTTON_DONE_TEXT;
        if (related) related.classList.remove('hidden');
        if (failedCount > 0) {
          setInlineAlert('Часть блоков доп. анализа не загрузилась. Можно продолжить тренировку.', 'error');
        }
      } catch (err) {
        console.error('Result extra analysis failed:', err);
        setInlineAlert('Не удалось загрузить доп. анализ. Попробуйте ещё раз.');
        btn.disabled = false;
        btn.textContent = EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;
      } finally {
        btn.removeAttribute('aria-busy');
      }
    });
  }

  document.addEventListener('DOMContentLoaded', () => {
    hydrateProgressBarsFromData();
    document.querySelectorAll('.btn-favorite').forEach((button) => {
      const favorite = button.getAttribute('aria-pressed') === 'true' || button.classList.contains('active');
      applyFavoriteButtonState(button, favorite);
    });
    const toggleCodeBtn = document.getElementById('toggle-code-btn');
    if (toggleCodeBtn) {
      toggleCodeBtn.addEventListener('click', () => window.toggleCode());
    }
    initStreakBar();
    initHintButton();
    initShuffleTopic();
    initSessionFormSync();
    initResultPageExtraAnalysis();
    const supportDetails = document.querySelector('.question-support');
    if (supportDetails) {
      supportDetails.addEventListener('toggle', () => {
        if (supportDetails.open) {
          trackUxMetric('support_opened');
        }
      });
    }
    initCollapsibleSidebar();
    initDangerousFormGuard();
    initFlashcardShortcuts();
    initKeyboardHelp();
  });

  function apiPost(url, formData) {
    return apiFetch(url, { method: 'POST', body: formData })
      .then(r => {
        if (!r.ok) return parseApiError(r).then(err => { throw err; });
        return r.json();
      });
  }
  function apiGet(url) {
    return apiFetch(url).then(r => {
      if (!r.ok) return parseApiError(r).then(err => { throw err; });
      return r.json();
    });
  }

  /**
   * Fetches from url, shows a loading placeholder in parentEl, then on success calls renderFn(data, placeholderEl);
   * on error removes placeholder и показывает сообщение.
   * @param {string} url - API URL
   * @param {HTMLElement} parentEl - Parent (or reference) for placeholder: appended as child, or inserted after if options.insertAfter
   * @param {string} loadingText - Inner HTML for the placeholder
   * @param {function(object, HTMLElement): void} renderFn - Called with (data, placeholderEl); may replace content or remove placeholder and insert elsewhere
   * @param {string} [method='GET'] - 'GET' or 'POST'
   * @param {{ postBody?: FormData|URLSearchParams, insertAfter?: boolean, placeholderClassName?: string, errorMessage?: string }} [options] - For POST pass postBody; insertAfter inserts after parentEl instead of appending
   */
  function fetchWithPlaceholder(url, parentEl, loadingText, renderFn, method = 'GET', options = {}) {
    const {
      postBody,
      insertAfter = false,
      placeholderClassName = 'loading-placeholder',
      errorMessage,
      rethrowOnError = false
    } = options;
    const placeholder = document.createElement('div');
    placeholder.className = placeholderClassName;
    placeholder.innerHTML = loadingText;
    if (insertAfter) {
      parentEl.after(placeholder);
    } else {
      parentEl.appendChild(placeholder);
    }
    const request = (method === 'POST' && postBody != null)
      ? apiPost(url, postBody)
      : apiGet(url);
    return request
      .then(data => {
        clearInlineAlert();
        renderFn(data, placeholder);
      })
      .catch(e => {
        console.warn('Operation failed:', e);
        placeholder.remove();
        setInlineAlert(errorMessage || e?.message || 'Не удалось загрузить дополнительные данные. Попробуйте ещё раз.');
        if (rethrowOnError) {
          throw e;
        }
      });
  }

  function updateSessionProgress(data) {
    const progressSpans = document.querySelectorAll('.session-progress');
    if (!progressSpans.length) return;
    if (!data || !data.session) {
      const fallbackIndex = progressSpans[0]?.dataset.field === 'index'
        ? Number.parseInt(progressSpans[0].textContent || '0', 10)
        : 0;
      const fallbackTotal = Array.from(progressSpans)
        .find(span => span.dataset.field === 'total');
      const totalValue = Number.parseInt(fallbackTotal?.textContent || '0', 10) || 0;
      const progressFill = document.querySelector('.session-progress-fill');
      if (progressFill && totalValue > 0) {
        setProgressValue(progressFill, (Math.max(0, fallbackIndex) * 100) / totalValue);
      }
      return;
    }

    const sessionInfo = data.session;
    const total = Number.isFinite(sessionInfo.total) ? sessionInfo.total : 0;
    const index = Number.isFinite(sessionInfo.index) ? sessionInfo.index : 0;
    const displayIndex = Math.min(total, index + 1);
    const correct = Number.isFinite(sessionInfo.correct) ? sessionInfo.correct : 0;
    const wrong = Number.isFinite(sessionInfo.wrong) ? sessionInfo.wrong : 0;

    progressSpans.forEach(span => {
      if (span.dataset.field === 'index') span.textContent = String(displayIndex);
      if (span.dataset.field === 'total') span.textContent = String(total);
      if (span.dataset.field === 'correct') span.textContent = String(correct);
      if (span.dataset.field === 'wrong') span.textContent = String(wrong);
    });

    const progressTrack = document.querySelector('.session-progress-track');
    const progressFill = document.querySelector('.session-progress-fill');
    if (progressTrack) {
      progressTrack.setAttribute('aria-valuenow', String(displayIndex));
      progressTrack.setAttribute('aria-valuemax', String(total));
    }
    if (progressFill) {
      const width = total > 0 ? Math.min(100, (displayIndex * 100) / total) : 0;
      setProgressValue(progressFill, width);
    }
  }

  function writeStatField(field, value, digits = 0) {
    if (!Number.isFinite(value)) return;
    const nodes = document.querySelectorAll('[data-stat-field="' + field + '"]');
    if (!nodes.length) return;
    const rendered = digits > 0 ? value.toFixed(digits) : String(Math.round(value));
    nodes.forEach((node) => {
      node.textContent = rendered;
    });
  }

  function applyStatsPayload(stats) {
    if (!stats || typeof stats !== 'object') return;
    writeStatField('total', Number(stats.total || 0));
    writeStatField('due', Number(stats.due || 0));
    writeStatField('learned', Number(stats.learned || 0));
    writeStatField('correct', Number(stats.correct || 0));
    writeStatField('wrong', Number(stats.wrong || 0));
    const attempts = Number(stats.correct || 0) + Number(stats.wrong || 0);
    const accuracy = attempts > 0 ? (Number(stats.correct || 0) * 100) / attempts : 0;
    writeStatField('accuracy', accuracy, 1);
    const barFill = document.getElementById('sidebar-accuracy-bar-fill');
    if (barFill) {
      setProgressValue(barFill, accuracy);
    }
  }

  function refreshStatsFromServer() {
    const topic = form.querySelector('input[name="topic"]')?.value || '';
    const group = form.querySelector('input[name="group"]')?.value || '';
    const important = form.querySelector('input[name="important"]')?.value === 'true';
    const onlyWrong = form.querySelector('input[name="onlyWrong"]')?.value === 'true';
    const shuffle = form.querySelector('input[name="shuffle"]')?.value === 'true';
    const ordered = form.querySelector('input[name="ordered"]')?.value !== 'false';

    const params = new URLSearchParams();
    if (topic) params.set('topic', topic);
    if (group) params.set('group', group);
    if (important) params.set('important', 'true');
    if (onlyWrong) params.set('onlyWrong', 'true');
    if (shuffle) params.set('shuffle', 'true');
    params.set('ordered', ordered ? 'true' : 'false');

    const query = params.toString();
    const url = query ? (API.STATS + '?' + query) : API.STATS;
    return apiGet(url)
      .then((stats) => {
        applyStatsPayload(stats);
      })
      .catch((err) => {
        console.warn('Stats refresh failed:', err);
      });
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function applyFavoriteButtonState(button, isFavorite) {
    if (!button) return;
    const favorite = !!isFavorite;
    const label = favorite ? FAVORITE_REMOVE_LABEL : FAVORITE_ADD_LABEL;
    button.classList.toggle('active', favorite);
    button.title = label;
    button.setAttribute('aria-label', label);
    button.setAttribute('aria-pressed', favorite ? 'true' : 'false');
  }

  /**
   * Toggle favorite state for a question. Button should have data-question-id.
   */
  async function toggleFavorite(questionId, button) {
    if (!questionId || !button) return;
    try {
      const resp = await apiFetch(API.FAVORITE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'questionId=' + encodeURIComponent(questionId)
      });
      if (resp.ok) {
        clearInlineAlert();
        const data = await resp.json();
        applyFavoriteButtonState(button, data.favorite);
      } else {
        const err = await parseApiError(resp);
        setInlineAlert(err.message || 'Не удалось обновить избранное.');
      }
    } catch (err) {
      console.error('Favorite toggle failed:', err);
      setInlineAlert('Не удалось обновить избранное. Проверьте сеть и повторите.');
    }
  }

  /**
   * Regenerate options for a question. Reloads page on index, only updates button on result.
   */
  async function regenerateQuestion(questionId, button) {
    if (!questionId || !button) return;
    button.classList.add('regenerating');
    button.title = 'Удаляю варианты...';
    try {
      const resp = await apiFetch(API.REGENERATE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'questionId=' + encodeURIComponent(questionId)
      });
      if (resp.ok) {
        clearInlineAlert();
        button.classList.remove('regenerating');
        button.classList.add('done');
        button.textContent = '✅';
        const isIndexPage = !!document.getElementById('interview-form');
        if (isIndexPage) {
          button.title = 'Варианты удалены. Выбери следующий вопрос без перезагрузки страницы.';
          const nextQuestionLink = document.getElementById('next-question');
          const nextHref = typeof buildNextQuestionHref === 'function' ? buildNextQuestionHref(questionId) : '/';
          if (nextQuestionLink) {
            nextQuestionLink.href = nextHref;
            nextQuestionLink.textContent = 'Загрузить новый вопрос';
            nextQuestionLink.classList.remove('hidden');
          }
          if (typeof window.__resetHintState === 'function') {
            window.__resetHintState();
          }
          const interviewOptions = document.getElementById('interview-options');
          if (interviewOptions) {
            interviewOptions.setAttribute('aria-disabled', 'true');
            interviewOptions.querySelectorAll('input[name="optionId"]').forEach(input => { input.disabled = true; });
          }
          const submit = document.getElementById('interview-submit');
          if (submit) {
            submit.disabled = true;
            submit.textContent = 'Варианты удалены';
          }
          setInlineAlert('Варианты удалены. Нажми «Загрузить новый вопрос», чтобы продолжить.', 'success');
        } else {
          button.title = 'Варианты удалены! При следующем показе будут перегенерированы.';
        }
      } else {
        const err = await parseApiError(resp);
        button.classList.remove('regenerating');
        button.textContent = '❌';
        button.title = 'Ошибка при удалении';
        setInlineAlert(err.message || 'Не удалось перегенерировать варианты.');
      }
    } catch (err) {
      button.classList.remove('regenerating');
      button.textContent = '❌';
      button.title = 'Ошибка сети';
      console.error('Regenerate failed:', err);
      setInlineAlert('Не удалось перегенерировать варианты. Проверьте сеть и повторите.');
    }
  }

  // Favorite and regenerate: delegate from document so they work on index and result pages
  document.addEventListener('click', (e) => {
    const fav = e.target.closest('.btn-favorite');
    if (fav) {
      e.preventDefault();
      const questionId = fav.getAttribute('data-question-id');
      if (questionId) toggleFavorite(questionId, fav);
      return;
    }
    const reg = e.target.closest('.btn-regenerate');
    if (reg) {
      e.preventDefault();
      const questionId = reg.getAttribute('data-question-id');
      if (questionId) regenerateQuestion(questionId, reg);
    }
  });

  const form = document.getElementById('interview-form');
  const optionsContainer = document.getElementById('interview-options');
  const submitBtn = document.getElementById('interview-submit') || document.getElementById('submitBtn');
  const feedbackDiv = document.getElementById('result-feedback');
  const nextLink = document.getElementById('next-question');
  const extraAnalysisBtn = document.getElementById('extra-analysis-toggle');
  const answerFlowHint = document.getElementById('answer-flow-hint');
  const answerFlowSteps = document.getElementById('answer-flow-steps');
  const instantModeToggle = document.getElementById('instant-mode-toggle');
  const hardModeToggle = document.getElementById('hard-mode-toggle');
  const reviewModeToggle = document.getElementById('review-mode-toggle');
  const adaptiveModeToggle = document.getElementById('adaptive-mode-toggle');
  const timerSelect = document.getElementById('timer-select');
  const timerBadge = document.getElementById('question-timer');

  if (feedbackDiv) feedbackDiv.setAttribute('aria-live', 'polite');

  if (!form || !optionsContainer) return;

  const optionInputs = Array.from(form.querySelectorAll('input[name="optionId"]'));
  let answered = false;
  let timerInterval = null;
  const questionStartedAt = Date.now();
  let firstAnswerTracked = false;

  function setAnswerFlowStep(step) {
    if (!answerFlowSteps) return;
    answerFlowSteps.classList.remove('hidden');
    answerFlowSteps.setAttribute('aria-hidden', 'false');
    const orderedSteps = ['selected', 'checking', 'result', 'explanation'];
    const currentIndex = orderedSteps.indexOf(step);
    answerFlowSteps.querySelectorAll('.answer-flow-step').forEach((el) => {
      const stepName = el.getAttribute('data-step');
      const idx = orderedSteps.indexOf(stepName);
      el.classList.remove('is-active', 'is-done');
      if (idx < currentIndex) {
        el.classList.add('is-done');
      } else if (idx === currentIndex) {
        el.classList.add('is-active');
      }
    });
  }

  function upsertFormHidden(name, value) {
    let hidden = form.querySelector('input[type="hidden"][name="' + name + '"]');
    if (value == null || value === '' || value === false) {
      if (hidden) hidden.remove();
      return;
    }
    if (!hidden) {
      hidden = document.createElement('input');
      hidden.type = 'hidden';
      hidden.name = name;
      form.appendChild(hidden);
    }
    hidden.value = String(value);
  }

  function saveLearningPrefs() {
    try {
      localStorage.setItem(LEARNING_PREFS_STORAGE_KEY, JSON.stringify(learningPrefs));
    } catch (_) {
      // Ignore storage errors.
    }
  }

  function loadLearningPrefs() {
    try {
      const raw = localStorage.getItem(LEARNING_PREFS_STORAGE_KEY);
      if (!raw) return;
      const parsed = JSON.parse(raw);
      learningPrefs = {
        ...defaultLearningPrefs,
        ...parsed,
        timerSeconds: Number.isFinite(parsed?.timerSeconds) ? Math.max(0, Number(parsed.timerSeconds)) : 0
      };
    } catch (_) {
      learningPrefs = { ...defaultLearningPrefs };
    }
  }

  function applyLearningPrefsToDom() {
    if (instantModeToggle) instantModeToggle.checked = !!learningPrefs.instantMode;
    if (hardModeToggle) hardModeToggle.checked = !!learningPrefs.hardMode;
    if (reviewModeToggle) reviewModeToggle.checked = !!learningPrefs.reviewMode;
    if (adaptiveModeToggle) adaptiveModeToggle.checked = !!learningPrefs.adaptiveMode;
    if (timerSelect) timerSelect.value = String(learningPrefs.timerSeconds || 0);
  }

  function syncLearningPrefsToRequest() {
    upsertFormHidden('onlyWrong', learningPrefs.reviewMode ? 'true' : '');
    upsertFormHidden('weakTopics', learningPrefs.adaptiveMode ? 'true' : '');
  }

  function updateSubmitAvailability() {
    if (answered) return;
    const selected = form.querySelector('input[name="optionId"]:checked');
    if (submitBtn) {
      submitBtn.disabled = !selected;
      submitBtn.classList.toggle('is-disabled', !selected);
    }
  }

  function stopQuestionTimer() {
    if (timerInterval) {
      clearInterval(timerInterval);
      timerInterval = null;
    }
  }

  function startQuestionTimer() {
    stopQuestionTimer();
    const durationSeconds = Number(learningPrefs.timerSeconds || 0);
    if (!timerBadge || !Number.isFinite(durationSeconds) || durationSeconds <= 0) {
      if (timerBadge) timerBadge.classList.add('hidden');
      return;
    }
    timerBadge.classList.remove('hidden');
    let remaining = durationSeconds;
    timerBadge.textContent = 'Осталось: ' + remaining + 'с';
    timerInterval = setInterval(() => {
      if (answered) {
        stopQuestionTimer();
        return;
      }
      remaining -= 1;
      if (remaining <= 0) {
        stopQuestionTimer();
        timerBadge.textContent = 'Время вышло';
        setInlineAlert('Время вышло. Выбери вариант и отправь ответ.');
        trackUxMetric('timer_expired', { durationSeconds });
        return;
      }
      timerBadge.textContent = 'Осталось: ' + remaining + 'с';
    }, 1000);
  }

  function onLearningPrefChange() {
    learningPrefs.instantMode = !!instantModeToggle?.checked;
    learningPrefs.hardMode = !!hardModeToggle?.checked;
    learningPrefs.reviewMode = !!reviewModeToggle?.checked;
    learningPrefs.adaptiveMode = !!adaptiveModeToggle?.checked;
    learningPrefs.timerSeconds = Number.parseInt(timerSelect?.value || '0', 10) || 0;
    saveLearningPrefs();
    syncLearningPrefsToRequest();
    startQuestionTimer();
    trackUxMetric('learning_pref_changed', { ...learningPrefs });
  }

  loadLearningPrefs();
  applyLearningPrefsToDom();
  syncLearningPrefsToRequest();
  startQuestionTimer();
  [instantModeToggle, hardModeToggle, reviewModeToggle, adaptiveModeToggle, timerSelect]
    .filter(Boolean)
    .forEach((el) => el.addEventListener('change', onLearningPrefChange));
  updateSubmitAvailability();
  function selectOptionByIndex(index) {
    const target = optionInputs[index];
    if (!target) return false;
    target.checked = true;
    target.focus();
    // Программная установка checked не всегда активирует слушатели формы.
    target.dispatchEvent(new Event('change', { bubbles: true }));
    updateSubmitAvailability();
    return true;
  }
  optionInputs.forEach((input) => {
    input.addEventListener('change', () => {
      updateSubmitAvailability();
      if (!answered && learningPrefs.instantMode && input.checked) {
        form.dispatchEvent(new Event('submit', { cancelable: true }));
      }
    });
  });

  if (submitBtn) {
    submitBtn.setAttribute('title', `Клавиши 1-${Math.min(optionInputs.length, 9)} — выбор, Enter — ответить`);
  }

  // Горячие клавиши: 1-9 выбор варианта, Enter — отправка
  document.addEventListener('keydown', (event) => {
    if (answered) return;
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName) && event.target.type !== 'radio') {
      return;
    }
    if (event.target && event.target.type === 'radio' && event.key === 'Enter') {
      event.preventDefault();
      form.dispatchEvent(new Event('submit', { cancelable: true }));
      return;
    }
    const key = event.key;
    if (key === 'ArrowDown' || key === 'ArrowUp') {
      event.preventDefault();
      const currentIndex = optionInputs.findIndex(input => input.checked);
      if (currentIndex === -1) {
        selectOptionByIndex(0);
        return;
      }
      const delta = key === 'ArrowDown' ? 1 : -1;
      const nextIndex = (currentIndex + delta + optionInputs.length) % optionInputs.length;
      selectOptionByIndex(nextIndex);
      return;
    }
    if (key >= '1' && key <= '9') {
      const index = parseInt(key, 10) - 1;
      if (selectOptionByIndex(index)) {
        event.preventDefault();
      }
    }
    if (key === 'Enter') {
      event.preventDefault();
      form.dispatchEvent(new Event('submit', { cancelable: true }));
    }
  });

  // Теги таблиц включены: сервер (MarkdownRenderService) рендерит GFM-таблицы
  // в <table>, а Jsoup-safelist их уже отсанитайзил. Без них этот клиентский
  // sanitizeHtml схлопывал таблицу в плоский текст ячеек — пользователь видел
  // мешанину вместо разметки в пояснениях и чек-листах.
  const ALLOWED_TAGS = new Set(['P', 'BR', 'STRONG', 'EM', 'B', 'I', 'UL', 'OL', 'LI', 'CODE', 'PRE', 'A', 'BLOCKQUOTE', 'H1', 'H2', 'H3', 'H4', 'TABLE', 'THEAD', 'TBODY', 'TR', 'TH', 'TD', 'HR', 'DIV', 'SPAN']);
  // На этих тегах сохраняем class: <div class="mermaid"> (диаграммы) и
  // <code class="language-java"> (подсветка highlight.js). Без class
  // динамически вставленный ответ терял диаграммы (mermaid показывался сырым
  // текстом graph TD...) и подсветку кода. class не исполняет JS — безопасно.
  const CLASS_PRESERVE_TAGS = new Set(['DIV', 'SPAN', 'CODE', 'PRE']);

  function sanitizeNode(node) {
    if (node.nodeType === Node.TEXT_NODE) {
      return document.createTextNode(node.textContent || '');
    }
    if (node.nodeType !== Node.ELEMENT_NODE) {
      return document.createTextNode('');
    }

    const tagName = node.tagName.toUpperCase();
    if (!ALLOWED_TAGS.has(tagName)) {
      const fragment = document.createDocumentFragment();
      Array.from(node.childNodes).forEach(child => fragment.appendChild(sanitizeNode(child)));
      return fragment;
    }

    const clean = document.createElement(tagName.toLowerCase());
    if (tagName === 'A') {
      const href = node.getAttribute('href') || '';
      if (/^(https?:|\/)/i.test(href)) {
        clean.setAttribute('href', href);
      }
      clean.setAttribute('rel', 'noopener noreferrer');
    }
    if (CLASS_PRESERVE_TAGS.has(tagName)) {
      const cls = node.getAttribute('class');
      if (cls) clean.setAttribute('class', cls);
    }
    Array.from(node.childNodes).forEach(child => clean.appendChild(sanitizeNode(child)));
    return clean;
  }

  // Парсим через DOMParser (скрипты не исполняются) и пересобираем дерево
  // только из разрешённых тегов — возвращаем готовый DocumentFragment, чтобы
  // вставлять узлы через appendChild без присваивания innerHTML на живой элемент.
  function sanitizeToFragment(html) {
    const fragment = document.createDocumentFragment();
    if (!html) return fragment;
    const parsed = new DOMParser().parseFromString(String(html), 'text/html');
    Array.from(parsed.body.childNodes).forEach(child => fragment.appendChild(sanitizeNode(child)));
    return fragment;
  }

  function sanitizeHtml(html) {
    const wrapper = document.createElement('div');
    wrapper.appendChild(sanitizeToFragment(html));
    return wrapper.innerHTML;
  }

  // Дорисовывает mermaid-диаграммы и highlight.js в контенте, вставленном
  // ПОСЛЕ DOMContentLoaded (ответ/пояснения приходят через AJAX, и начальные
  // инициализаторы mermaid/hljs их уже не трогают). Без этого диаграмма
  // оставалась сырым `graph TD ...`, а код — без подсветки.
  function renderDynamicContent(container) {
    if (!container) return;
    if (typeof hljs !== 'undefined') {
      container.querySelectorAll('pre code').forEach(function (block) {
        try { hljs.highlightElement(block); } catch (_) { /* подсветка не критична */ }
      });
    }
    if (typeof mermaid !== 'undefined') {
      container.querySelectorAll('.mermaid').forEach(async function (el) {
        if (el.getAttribute('data-rendered') === 'true') return;
        try {
          const id = 'mmd-dyn-' + Math.random().toString(36).slice(2);
          const res = await mermaid.render(id, el.textContent);
          // text/html-парсинг (как innerHTML, но без присваивания): достаём <svg>
          // и переносим узлом — избегаем innerHTML на живом элементе.
          const parsed = new DOMParser().parseFromString(res.svg, 'text/html');
          const svg = parsed.body.querySelector('svg');
          if (svg) {
            svg.removeAttribute('height');
            svg.style.width = '100%';
            svg.style.height = 'auto';
            el.replaceChildren(document.importNode(svg, true));
            el.setAttribute('data-rendered', 'true');
          } else {
            el.remove();
          }
        } catch (_) {
          el.remove();
        }
      });
    }
  }

  // Именованная функция для submit, чтобы можно было удалить listener при fallback
  async function handleSubmit(event) {
    event.preventDefault();
    if (answered) return;

    const selected = form.querySelector('input[name="optionId"]:checked');
    if (!selected) {
      setInlineAlert('Сначала выбери один вариант ответа.');
      const firstOption = optionInputs[0];
      if (firstOption) firstOption.focus();
      return;
    }

    answered = true;
    setAnswerFlowStep('selected');
    stopQuestionTimer();
    submitBtn.disabled = true;
    submitBtn.textContent = 'Проверяю...';
    submitBtn.setAttribute('aria-busy', 'true');
    setInteractionBusy(true);
    setAnswerFlowStep('checking');
    clearInlineAlert();

    const formData = new FormData(form);
    if (!firstAnswerTracked) {
      firstAnswerTracked = true;
      trackUxMetric('first_answer_latency_ms', { value: Date.now() - questionStartedAt });
    }
    trackUxMetric('answer_submitted', { hardMode: !!learningPrefs.hardMode, instantMode: !!learningPrefs.instantMode });
    try {
      const response = await apiFetch(API.ANSWER, {
        method: 'POST',
        body: new URLSearchParams(formData),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      });

      if (!response.ok) {
        const err = await parseApiError(response);
        answered = false;
        submitBtn.disabled = false;
        submitBtn.textContent = 'Ответить';
        submitBtn.removeAttribute('aria-busy');
        setInteractionBusy(false);
        updateSubmitAvailability();
        startQuestionTimer();
        if (answerFlowSteps) {
          answerFlowSteps.classList.add('hidden');
          answerFlowSteps.setAttribute('aria-hidden', 'true');
        }
        setInlineAlert(err.message || 'Не удалось проверить ответ. Попробуйте ещё раз.');
        return;
      }

      const data = await response.json();
      submitBtn.removeAttribute('aria-busy');
      showResult(data);
    } catch (err) {
      console.error('AJAX answer failed:', err);
      answered = false;
      submitBtn.disabled = false;
      submitBtn.textContent = 'Ответить';
      submitBtn.removeAttribute('aria-busy');
      setInteractionBusy(false);
      updateSubmitAvailability();
      startQuestionTimer();
      if (answerFlowSteps) {
        answerFlowSteps.classList.add('hidden');
        answerFlowSteps.setAttribute('aria-hidden', 'true');
      }
      setInlineAlert('Сервер недоступен. Проверьте соединение и повторите отправку.');
    }
  }

  form.addEventListener('submit', handleSubmit);

  function applyOptionStyles(data) {
    const labels = optionsContainer.querySelectorAll('label[data-option-id]');
    optionInputs.forEach(input => { input.disabled = true; });

    const explanationMap = {};
    if (data.optionExplanations) {
      data.optionExplanations.forEach(oe => {
        explanationMap[oe.id] = oe;
      });
    }

    labels.forEach(label => {
      const optionId = parseInt(label.getAttribute('data-option-id'), 10);
      const optionText = label.querySelector('span')?.textContent?.trim() || '';
      let statusLabelText = '';
      if (optionId === data.correctOptionId) {
        label.classList.add('option-correct');
        statusLabelText = 'Правильный ответ';
      } else if (optionId === data.selectedOptionId) {
        label.classList.add('option-wrong');
        statusLabelText = 'Ваш выбор (ошибка)';
      } else {
        label.classList.add('option-dimmed');
        statusLabelText = 'Не выбран';
      }
      if (statusLabelText) {
        label.setAttribute('aria-label', (statusLabelText + ': ' + optionText).trim());
      }
      if (statusLabelText) {
        const statusLabel = document.createElement('span');
        statusLabel.className = 'option-status-label ' + (optionId === data.correctOptionId
          ? 'option-status-correct'
          : (optionId === data.selectedOptionId ? 'option-status-selected' : 'option-status-muted'));
        statusLabel.textContent = statusLabelText;
        label.appendChild(statusLabel);
      }

      const optExpl = explanationMap[optionId];
      if (optExpl && optExpl.explanationHtml) {
        const explDiv = document.createElement('div');
        // markdown-content — те же стили, что у takeaway: таблицы/код/списки
        // в пояснении варианта выглядят согласованно. Контент пришёл с сервера
        // уже отрендеренным из markdown и отсанитайзенным (Jsoup), плюс здесь
        // повторно прогоняется через allowlist-санитайзер (defense-in-depth).
        explDiv.className = 'option-explanation markdown-content ' + (optExpl.correct ? 'explanation-correct' : 'explanation-wrong');
        explDiv.appendChild(sanitizeToFragment(optExpl.explanationHtml));
        label.appendChild(explDiv);
        label.classList.remove('option-dimmed');
        label.classList.add(optExpl.correct ? 'option-correct' : (optionId === data.selectedOptionId ? 'option-wrong' : 'option-other'));
      }
    });
    // Подсветка кода / mermaid в только что вставленных пояснениях вариантов.
    renderDynamicContent(optionsContainer);
  }

  function renderFeedbackHtml(data) {
    const isCorrect = data.correct;
    const safeHtml = sanitizeHtml(data.answerHtml);

    feedbackDiv.innerHTML = `
      <div class="${isCorrect ? 'result-correct' : 'result-wrong'}">
        <strong>${isCorrect ? '✅ Верно' : '❌ Неверно'}</strong>
        <div class="answer markdown-content">${safeHtml}</div>
      </div>
    `;
    feedbackDiv.classList.remove('hidden');
    renderDynamicContent(feedbackDiv);
  }

  function attachConfidenceButtons(data) {
    const isCorrect = data.correct;
    if (!isCorrect) return;

    const confidenceDiv = document.createElement('div');
    confidenceDiv.className = 'confidence-buttons';
    confidenceDiv.innerHTML = `
      <span class="confidence-label">Насколько ты уверен по этому вопросу?</span>
      <button type="button" class="confidence-btn confidence-guess" data-grade="3" aria-label="Уровень уверенности: угадал">🎲 Угадал</button>
      <button type="button" class="confidence-btn confidence-hard" data-grade="4" aria-label="Уровень уверенности: с трудом">🤔 С трудом</button>
      <button type="button" class="confidence-btn confidence-sure" data-grade="5" aria-label="Уровень уверенности: знал точно">💪 Знал точно</button>
    `;
    feedbackDiv.after(confidenceDiv);

    let confidenceSubmitted = false;
    const questionId = form.querySelector('input[name="questionId"]').value;
    confidenceDiv.querySelectorAll('.confidence-btn').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (confidenceSubmitted) return;
        confidenceSubmitted = true;

        const grade = btn.getAttribute('data-grade');
        try {
          const confidenceResp = await apiFetch(API.CONFIDENCE, {
            method: 'POST',
            body: new URLSearchParams({ questionId: questionId, grade: grade }),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          });
          if (!confidenceResp.ok) {
            throw await parseApiError(confidenceResp);
          }
          clearInlineAlert();
        } catch (err) {
          console.error('Confidence update failed:', err);
          confidenceSubmitted = false;
          setInlineAlert('Не удалось сохранить уверенность. Попробуйте ещё раз.');
          return;
        }

        confidenceDiv.querySelectorAll('.confidence-btn').forEach(b => {
          b.classList.remove('selected');
          b.disabled = true;
        });
        btn.classList.add('selected');
      });
    });
  }

  function buildNextQuestionHref(excludeQuestionId) {
    const params = new URLSearchParams();
    const bind = (name) => {
      const input = form.querySelector(`input[name="${name}"]`);
      if (input && input.value) {
        params.set(name, input.value);
      }
    };
    bind('topic');
    bind('group');
    bind('ordered');
    ['important', 'onlyWrong', 'shuffle', 'weakTopics'].forEach((name) => {
      const input = form.querySelector(`input[name="${name}"]`);
      if (input && input.value === 'true') {
        params.set(name, 'true');
      }
    });
    if (excludeQuestionId) {
      params.set('excludeQuestionId', String(excludeQuestionId));
    }
    const query = params.toString();
    return query ? '/?' + query : '/';
  }

  function fetchWrongFeedback(questionId, selectedOptionId, rethrowOnError = false) {
    const wrongBlock = feedbackDiv.querySelector('.result-wrong');
    if (!wrongBlock) return;
    return fetchWithPlaceholder(
      API.WRONG_FEEDBACK,
      wrongBlock,
      '<span class="wrong-feedback-loader">🤖 Анализирую ошибку...</span>',
      (fbData, placeholderEl) => {
        placeholderEl.classList.remove('loading');
        if (fbData.available && fbData.feedback) {
          placeholderEl.innerHTML =
            '<div class="wrong-feedback-title">💡 Почему это неверно:</div>' +
            '<div class="wrong-feedback-text">' + escapeHtml(fbData.feedback) + '</div>';
        } else {
          placeholderEl.remove();
        }
      },
      'POST',
      {
        postBody: new URLSearchParams({ questionId: questionId, optionId: String(selectedOptionId) }),
        placeholderClassName: 'content-block wrong-feedback loading',
        rethrowOnError: rethrowOnError
      }
    );
  }

  function fetchTakeaway(questionId, rethrowOnError = false) {
    return fetchWithPlaceholder(
      API.TAKEAWAY + '?questionId=' + encodeURIComponent(questionId),
      feedbackDiv,
      '<span class="takeaway-loading-text">Загрузка...</span>',
      (tkData, placeholderEl) => {
        placeholderEl.remove();
        if (tkData.takeaway) {
          const tkDiv = document.createElement('div');
          tkDiv.className = 'content-block takeaway-block';
          tkDiv.innerHTML =
            '<div class="takeaway-title">🎯 Главное, что нужно запомнить:</div>' +
            '<div class="takeaway-text">' + escapeHtml(tkData.takeaway) + '</div>';
          feedbackDiv.after(tkDiv);
        }
      },
      'GET',
      { insertAfter: true, placeholderClassName: 'content-block takeaway-block takeaway-loading', rethrowOnError: rethrowOnError }
    );
  }

  function fetchComparison(questionId, selectedOptionId, rethrowOnError = false) {
    const wrongBlock = feedbackDiv.querySelector('.result-wrong');
    if (!wrongBlock) return;
    return fetchWithPlaceholder(
      API.COMPARISON + '?questionId=' + encodeURIComponent(questionId) + '&selectedOptionId=' + encodeURIComponent(selectedOptionId),
      wrongBlock,
      '<span class="comparison-loading-text">Загрузка...</span>',
      (cmpData, placeholderEl) => {
        placeholderEl.remove();
        if (cmpData.comparison) {
          try {
            const parsed = typeof cmpData.comparison === 'string' ? JSON.parse(cmpData.comparison) : cmpData.comparison;
            if (parsed.criteria && parsed.criteria.length > 0) {
              const cmpDiv = document.createElement('div');
              cmpDiv.className = 'content-block comparison-block';
              let tableHtml = '<div class="comparison-title">📊 Сравнение: выбранное vs правильное</div>';
              tableHtml += '<table class="comparison-table"><tr><th>Критерий</th><th>Выбранное</th><th>Правильное</th></tr>';
              parsed.criteria.forEach(c => {
                tableHtml += '<tr><td>' + escapeHtml(c.criterion) + '</td><td>' + escapeHtml(c.selected) + '</td><td>' + escapeHtml(c.correct) + '</td></tr>';
              });
              tableHtml += '</table>';
              cmpDiv.innerHTML = tableHtml;
              wrongBlock.after(cmpDiv);
            }
          } catch (e) { /* ignore parse error */ }
        }
      },
      'GET',
      { insertAfter: true, placeholderClassName: 'content-block comparison-block comparison-loading', rethrowOnError: rethrowOnError }
    );
  }

  function fetchCodeTrace(questionId, rethrowOnError = false) {
    const codeBlock = document.querySelector('.question-code');
    if (!codeBlock) return;
    return fetchWithPlaceholder(
      API.CODE_TRACE + '?questionId=' + encodeURIComponent(questionId),
      feedbackDiv,
      '<span class="code-trace-loading-text">Загрузка...</span>',
      (traceData, placeholderEl) => {
        placeholderEl.remove();
        if (traceData.trace) {
          try {
            const parsed = typeof traceData.trace === 'string' ? JSON.parse(traceData.trace) : traceData.trace;
            if (parsed.steps && parsed.steps.length > 0) {
              const traceDiv = document.createElement('div');
              traceDiv.className = 'content-block code-trace-block';
              let html = '<details><summary class="code-trace-title">🔍 Пошаговое выполнение кода</summary>';
              html += '<ol class="code-trace-steps">';
              parsed.steps.forEach(s => {
                html += '<li class="code-trace-step">';
                html += '<span class="trace-step-num">' + escapeHtml(s.step) + '</span>';
                if (s.line) html += '<span class="trace-step-line">' + escapeHtml(s.line) + '</span>';
                if (s.state) html += '<span class="trace-step-state">' + escapeHtml(s.state) + '</span>';
                html += '<span class="trace-step-explanation">' + escapeHtml(s.explanation) + '</span>';
                html += '</li>';
              });
              html += '</ol></details>';
              traceDiv.innerHTML = html;
              feedbackDiv.after(traceDiv);
            }
          } catch (e) { /* ignore parse error */ }
        }
      },
      'GET',
      { insertAfter: true, placeholderClassName: 'content-block code-trace-block code-trace-loading', rethrowOnError: rethrowOnError }
    );
  }

  function renderRelatedQuestions(data) {
    if (!data.relatedQuestions || data.relatedQuestions.length === 0) return;
    const currentGroup = form.querySelector('input[name="group"]')?.value || '';
    const currentOrdered = form.querySelector('input[name="ordered"]')?.value || 'true';
    const currentImportant = form.querySelector('input[name="important"]')?.value === 'true';
    const currentOnlyWrong = form.querySelector('input[name="onlyWrong"]')?.value === 'true';
    const currentShuffle = form.querySelector('input[name="shuffle"]')?.value === 'true';
    const currentWeakTopics = form.querySelector('input[name="weakTopics"]')?.value === 'true';

    const relatedDiv = document.createElement('div');
    relatedDiv.className = 'related-questions';
    let relatedHtml = '<div class="related-questions-title">🔗 Похожие вопросы для закрепления:</div>';
    data.relatedQuestions.forEach(rq => {
      relatedHtml += '<a class="related-question-item" href="/?topic=' + encodeURIComponent(rq.topic)
        + '&group=' + encodeURIComponent(currentGroup)
        + '&ordered=' + encodeURIComponent(currentOrdered)
        + (currentImportant ? '&important=true' : '')
        + (currentOnlyWrong ? '&onlyWrong=true' : '')
        + (currentShuffle ? '&shuffle=true' : '')
        + (currentWeakTopics ? '&weakTopics=true' : '')
        + '">' + escapeHtml(rq.text) + '</a>';
    });
    relatedDiv.innerHTML = relatedHtml;
    feedbackDiv.after(relatedDiv);
  }

  function showResult(data) {
    const isCorrect = data.correct;
    const questionId = form.querySelector('input[name="questionId"]').value;

    applyOptionStyles(data);
    submitBtn.classList.add('hidden');
    nextLink.textContent = 'Следующий вопрос';
    nextLink.href = buildNextQuestionHref(questionId);
    renderFeedbackHtml(data);
    setAnswerFlowStep('result');
    if (answerFlowHint) {
      answerFlowHint.classList.remove('hidden');
    }

    updateSessionProgress(data);
    refreshStatsFromServer();

    if (!learningPrefs.hardMode) {
      attachConfidenceButtons(data);
    }
    nextLink.classList.remove('hidden');
    setInteractionBusy(false);
    feedbackDiv.setAttribute('tabindex', '-1');
    feedbackDiv.focus();
    if (extraAnalysisBtn && !learningPrefs.hardMode) {
      extraAnalysisBtn.classList.remove('hidden');
      extraAnalysisBtn.disabled = false;
      extraAnalysisBtn.textContent = EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;
      // При ошибке показываем ключевой разбор сразу, чтобы не терять учебный момент.
      if (!isCorrect) {
        fetchWrongFeedback(questionId, data.selectedOptionId);
        fetchComparison(questionId, data.selectedOptionId);
      }
      setAnswerFlowStep('explanation');
      let loaded = false;
      extraAnalysisBtn.onclick = async () => {
        if (loaded) return;
        loaded = true;
        extraAnalysisBtn.disabled = true;
        extraAnalysisBtn.setAttribute('aria-busy', 'true');
        extraAnalysisBtn.textContent = EXTRA_ANALYSIS_BUTTON_LOADING_TEXT;
        try {
          const requests = [];
          requests.push(fetchTakeaway(questionId, true));
          requests.push(fetchCodeTrace(questionId, true));
          const results = await Promise.allSettled(requests);
          renderRelatedQuestions(data);
          extraAnalysisBtn.textContent = EXTRA_ANALYSIS_BUTTON_DONE_TEXT;
          const failedCount = results.filter(r => r.status === 'rejected').length;
          if (failedCount > 0) {
            setInlineAlert('Часть блоков доп. анализа не загрузилась. Можно продолжить тренировку.');
          }
        } finally {
          extraAnalysisBtn.removeAttribute('aria-busy');
        }
      };
    } else {
      if (learningPrefs.hardMode) {
        if (extraAnalysisBtn) {
          extraAnalysisBtn.classList.add('hidden');
        }
      } else {
        // Fallback for pages where the extra-analysis button is absent.
        if (!isCorrect) {
          fetchWrongFeedback(questionId, data.selectedOptionId);
          fetchComparison(questionId, data.selectedOptionId);
        }
        fetchTakeaway(questionId);
        fetchCodeTrace(questionId);
        renderRelatedQuestions(data);
      }
      if (extraAnalysisBtn && learningPrefs.hardMode) {
        extraAnalysisBtn.classList.add('hidden');
      }
      setAnswerFlowStep('explanation');
    }

    nextLink.onclick = (e) => {
      if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
      if (!nextLink.href) return;
      e.preventDefault();
      trackUxMetric('next_question_click');
      window.location.href = nextLink.href;
    };
    nextLink.onkeydown = (e) => {
      if (e.key === ' ' || e.key === 'Enter') {
        e.preventDefault();
        window.location.href = nextLink.href;
      }
    };
  }
  window.toggleCode = function toggleCode() {
    const block = document.getElementById('codeBlock');
    if (!block) return;
    block.classList.toggle('hidden');
  };

  window.submitAnswer = function submitAnswer() {
    const activeForm = document.getElementById('interview-form');
    if (activeForm) {
      activeForm.dispatchEvent(new Event('submit', { cancelable: true }));
    }
  };

})();

function initDangerousFormGuard() {
  document.querySelectorAll('[data-confirm]').forEach((el) => {
    const form = el.closest('form');
    if (!form) return;
    form.addEventListener('submit', (e) => {
      const msg = el.getAttribute('data-confirm');
      if (msg && !window.confirm(msg)) {
        e.preventDefault();
      }
    });
  });
}

function initKeyboardHelp() {
  const shortcuts = [
    { keys: ['1', '2', '…', '9'], desc: 'Выбрать вариант ответа' },
    { keys: ['↑', '↓'], desc: 'Переключить вариант' },
    { keys: ['Enter'], desc: 'Отправить ответ' },
    { keys: ['Space'], desc: 'Раскрыть флешкарту' },
    { keys: ['1', '2', '3', '4'], desc: 'Оценить флешкарту (не помню → отлично)' },
    { keys: ['?'], desc: 'Показать / скрыть эту справку' },
    { keys: ['Esc'], desc: 'Закрыть справку' },
  ];
  const overlay = document.createElement('div');
  overlay.className = 'kbd-help-overlay hidden';
  overlay.setAttribute('role', 'dialog');
  overlay.setAttribute('aria-modal', 'true');
  overlay.setAttribute('aria-labelledby', 'kbd-help-title');
  overlay.innerHTML =
    '<div class="kbd-help-modal">' +
    '<h3 id="kbd-help-title">Горячие клавиши</h3>' +
    '<dl class="kbd-help-list">' +
    shortcuts.map(s =>
      '<dt>' + s.keys.map(k => '<kbd>' + k + '</kbd>').join(' ') + '</dt>' +
      '<dd>' + s.desc + '</dd>'
    ).join('') +
    '</dl>' +
    '<button type="button" class="kbd-help-close" aria-label="Закрыть">×</button>' +
    '</div>';
  document.body.appendChild(overlay);
  const close = () => overlay.classList.add('hidden');
  const open = () => overlay.classList.remove('hidden');
  overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  overlay.querySelector('.kbd-help-close').addEventListener('click', close);
  document.addEventListener('keydown', (event) => {
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)) return;
    if (event.key === '?') {
      event.preventDefault();
      overlay.classList.contains('hidden') ? open() : close();
    } else if (event.key === 'Escape' && !overlay.classList.contains('hidden')) {
      event.preventDefault();
      close();
    }
  });
}

function initFlashcardShortcuts() {
  const phase = document.querySelector('.flashcard-phase');
  if (!phase) return;
  const revealBtn = phase.querySelector('.flashcard-reveal-btn');
  const gradeBtns = phase.querySelectorAll('.flashcard-grade-btn');
  document.addEventListener('keydown', (event) => {
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)) {
      return;
    }
    if (event.metaKey || event.ctrlKey || event.altKey) return;
    if (revealBtn && (event.key === ' ' || event.key === 'Enter')) {
      event.preventDefault();
      revealBtn.click();
      return;
    }
    if (gradeBtns.length > 0 && event.key >= '1' && event.key <= '4') {
      const idx = parseInt(event.key, 10) - 1;
      const btn = gradeBtns[idx];
      if (btn) {
        event.preventDefault();
        btn.click();
      }
    }
  });
}
