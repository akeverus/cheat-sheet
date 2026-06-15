(() => {
  const UI_CONSTANTS = Object.freeze({
    EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT: 'Показать доп. анализ',
    EXTRA_ANALYSIS_BUTTON_LOADING_TEXT: 'Загружаю…',
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

  // Иконка из SVG-спрайта (fragments/icons.html) как строка для innerHTML —
  // те же Lucide-символы, что в шаблонах. Монохром через currentColor, вставляем
  // только в наш литеральный markup (не через sanitizeHtml, который вырезал бы svg).
  function icon(name, extraClass) {
    return '<svg class="ed-icon' + (extraClass ? ' ' + extraClass : '') +
      '" aria-hidden="true"><use href="#i-' + name + '"></use></svg>';
  }

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
  // Multi-instance: один fetch /api/streak питает ВСЕ [data-streak-bar] на странице
  // (детальный бар в настройках + компактные чипы today-widget на /settings и /focus).
  // Дети ищутся scoped по data-атрибутам, поэтому id-коллизий между инстансами нет.
  // Сбой стрика молчим: дневной прогресс не критичен, нагло алертить не нужно.
  function initStreakBar() {
    const bars = document.querySelectorAll('[data-streak-bar]');
    if (!bars.length) return;
    const daysText = (n) => n + (n === 1 ? ' день' : (n >= 2 && n <= 4 ? ' дня' : ' дней'));
    apiFetch(API.STREAK)
      .then(r => r.json())
      .then(d => {
        if (!d || typeof d !== 'object' || typeof d.streak !== 'number'
            || typeof d.goal !== 'number' || typeof d.today !== 'number') return;
        const pct = d.goal > 0 ? Math.min(100, (d.today / d.goal) * 100) : 0;
        bars.forEach((bar) => {
          bar.classList.remove('hidden');
          const daysEl = bar.querySelector('[data-streak-days]');
          if (daysEl) daysEl.textContent = daysText(d.streak);
          const fillEl = bar.querySelector('[data-streak-fill]');
          if (fillEl) setProgressValue(fillEl, pct);
          const countEl = bar.querySelector('[data-streak-count]');
          if (countEl) countEl.textContent = d.today + '/' + d.goal;
          if (d.goalReached) bar.classList.add('streak-goal-reached');
        });
      })
      .catch(() => { /* прогресс за день не критичен — тихо пропускаем */ });
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

          // Уровень из ответа сервера: валидируем, иначе icons[level-1]===undefined
          // отрендерил бы текст «undefined» вместо иконки.
          const lvl = Number(data.level);
          const safeLvl = Number.isInteger(lvl) && lvl >= 1 && lvl <= icons.length ? lvl : 1;
          const item = document.createElement('div');
          item.className = 'hint-item level-' + safeLvl;
          const iconSpan = document.createElement('span');
          iconSpan.className = 'hint-icon';
          iconSpan.textContent = icons[safeLvl - 1];
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
  // ТРЕНИРОВКА — бесконечный режим без сессии (сервер: SessionFlowService.startSession
  // при TRAINING очищает сессию и игнорирует count). Честный UI: счётчик вопросов
  // прячем, CTA переименовываем — кнопка не обещает сессию, которой не будет.
  // Критика round-01 B1.
  function initSessionModeForm() {
    const modeSelect = document.getElementById('session-mode-select');
    const countField = document.getElementById('session-count-field');
    const startBtn = document.getElementById('session-start-btn');
    if (!modeSelect || !countField || !startBtn) return;
    const sync = () => {
      const isTraining = modeSelect.value === 'TRAINING';
      countField.classList.toggle('hidden', isTraining);
      startBtn.textContent = isTraining ? 'Начать тренировку' : 'Начать сессию';
    };
    modeSelect.addEventListener('change', sync);
    sync();
  }

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
    // Кнопка рендерится hidden (no-JS не должен видеть JS-only контрол) —
    // раскрываем её, раз JS доступен и обработчик навешивается.
    btn.classList.remove('hidden');

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
      // aria-busy в гарде: aria-disabled (в отличие от native disabled) не
      // блокирует повторную клавиатурную активацию во время загрузки.
      if (btn.dataset.loaded === 'true' || btn.getAttribute('aria-busy') === 'true') return;
      // aria-disabled, а не disabled: native disabled выбрасывает фокус
      // клавиатуры на <body>; aria-disabled оставляет кнопку в tab-order
      // (клик мышью гасит CSS pointer-events, повтор с клавиатуры — гард выше).
      btn.setAttribute('aria-disabled', 'true');
      btn.setAttribute('aria-busy', 'true');
      btn.setAttribute('aria-expanded', 'true');
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
                  '<div class="wrong-feedback-title">' + icon('lightbulb', 'ed-icon-lead') + 'Почему это неверно:</div>' +
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
              let table = '<div class="comparison-title">' + icon('chart', 'ed-icon-lead') + 'Сравнение: выбранное vs правильное</div>';
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
                '<div class="takeaway-title">' + icon('target', 'ed-icon-lead') + 'Главное, что нужно запомнить:</div>' +
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
            let html = '<details><summary class="code-trace-title">' + icon('search', 'ed-icon-lead') + 'Пошаговое выполнение кода</summary>';
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
        if (requests.length > 0 && failedCount === requests.length) {
          // Полный провал (все блоки упали): НЕ помечаем loaded='true' и НЕ пишем
          // «загружен» — иначе кнопка осталась бы заблокированной с ложной меткой
          // успеха. Возвращаем кнопку в исходное retryable-состояние.
          setInlineAlert('Не удалось загрузить доп. анализ. Попробуйте ещё раз.');
          btn.removeAttribute('aria-disabled');
          btn.textContent = EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;
          btn.setAttribute('aria-expanded', 'false');
          container.classList.add('hidden');
        } else {
          btn.dataset.loaded = 'true';
          btn.textContent = EXTRA_ANALYSIS_BUTTON_DONE_TEXT;
          if (related) related.classList.remove('hidden');
          if (failedCount > 0) {
            setInlineAlert('Часть блоков доп. анализа не загрузилась. Можно продолжить тренировку.', 'error');
          }
        }
      } catch (err) {
        console.error('Result extra analysis failed:', err);
        setInlineAlert('Не удалось загрузить доп. анализ. Попробуйте ещё раз.');
        btn.removeAttribute('aria-disabled');
        btn.textContent = EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;
      } finally {
        btn.removeAttribute('aria-busy');
      }
    });
  }

  // --- Экспорт прогресса (/export) ---------------------------------------
  // /export защищён admin-токеном через заголовок X-Admin-Token (SecurityConfig).
  // <a>-навигация заголовок не отправляет → клик всегда отдавал raw-JSON 403.
  // Здесь: apiFetch с заголовком, токен спрашиваем один раз и кэшируем в
  // localStorage, ответ скачиваем как файл (blob). localStorage обёрнут в
  // try/catch — приватные режимы браузера бросают на доступе к нему.
  // Единственная точка — data-export-block на /settings (inline-статус). Дубль
  // из шапки /stats убран: там нет app.js и нет места под статус (был битый).
  const ADMIN_TOKEN_KEY = 'cheatsheet-admin-token';
  // ДОГОВОР: дублирует серверный текст SensitiveEndpointAccessService
  // .buildForbiddenResponse («…app.admin-token не настроен»). Машиночитаемого
  // type у обоих 403 нет (оба FORBIDDEN), поэтому различаем по этой подстроке.
  // При смене формулировки на сервере — поправить здесь.
  const SERVER_TOKEN_UNSET_MARKER = 'не настроен';
  let exportInFlight = false;
  function readStoredToken() { try { return localStorage.getItem(ADMIN_TOKEN_KEY) || ''; } catch (_) { return ''; } }
  function storeToken(t) { try { localStorage.setItem(ADMIN_TOKEN_KEY, t); } catch (_) { /* ignore */ } }
  function clearStoredToken() { try { localStorage.removeItem(ADMIN_TOKEN_KEY); } catch (_) { /* ignore */ } }

  function obtainAdminToken() {
    let token = readStoredToken();
    if (!token) {
      token = (window.prompt('Введите admin-токен (APP_ADMIN_TOKEN) для экспорта:') || '').trim();
      if (token) storeToken(token);
    }
    return token;
  }

  // Имя файла из Content-Disposition: сначала RFC 5987 filename*=UTF-8''…,
  // затем обычный filename="…" / filename=… . Иначе — дефолт под серверный
  // нейминг (ExportApiService отдаёт quiz-progress.json|csv).
  function parseContentDispositionFilename(header, fallback) {
    if (!header) return fallback;
    const star = /filename\*=UTF-8''([^;]+)/i.exec(header);
    if (star) { try { return decodeURIComponent(star[1]); } catch (_) { /* fallthrough */ } }
    const plain = /filename=(?:"([^"]+)"|([^;]+))/i.exec(header);
    if (plain) return (plain[1] || plain[2] || '').trim() || fallback;
    return fallback;
  }

  // Статус через .hidden-класс (как result-feedback / chart-fallback), НЕ через
  // атрибут hidden: единый паттерн live-region в проекте. Текст пишем до показа.
  function setExportStatus(el, message, isError) {
    if (!el) return;
    el.textContent = message || '';
    el.classList.toggle('hidden', !message);
    el.classList.toggle('export-status-error', !!(message && isError));
  }

  // Общая логика: один запрос за раз (exportInFlight), кнопки на время полёта —
  // aria-disabled + aria-busy (НЕ native disabled: тот выбрасывает фокус
  // клавиатуры на <body>; см. extra-analysis-toggle). notify(message, isError) —
  // канал фидбэка (inline-статус на /settings, alert в шапке /stats).
  async function runExport(format, buttons, notify) {
    if (exportInFlight) return;
    const token = obtainAdminToken();
    if (!token) return; // пользователь отменил ввод — молча выходим
    exportInFlight = true;
    notify('Готовлю экспорт…', false);
    buttons.forEach((b) => { b.setAttribute('aria-disabled', 'true'); b.setAttribute('aria-busy', 'true'); });
    try {
      const resp = await apiFetch('/export?format=' + encodeURIComponent(format), {
        headers: { 'X-Admin-Token': token }
      });
      if (resp.status === 401 || resp.status === 403) {
        let serverMsg = '';
        try { serverMsg = (await resp.json())?.message || ''; } catch (_) { /* ignore */ }
        // «не настроен» — проблема сервера, токен пользователя ни при чём:
        // НЕ сбрасываем кэш. Иначе токен неверен → сбрасываем, чтобы переспросить.
        const notConfigured = serverMsg.toLowerCase().includes(SERVER_TOKEN_UNSET_MARKER);
        if (!notConfigured) clearStoredToken();
        notify(notConfigured
          ? 'Экспорт недоступен: на сервере не задан admin-токен (APP_ADMIN_TOKEN).'
          : 'Неверный admin-токен. Нажми «Экспорт» ещё раз и введи правильный.', true);
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

  function wireExportGroup(buttons, notify) {
    buttons.forEach((btn) => {
      btn.addEventListener('click', () => {
        if (btn.getAttribute('aria-busy') === 'true') return; // повтор во время полёта
        runExport(btn.getAttribute('data-export-format'), buttons, notify);
      });
    });
  }

  function initExportButtons() {
    // /settings — блок «Управление данными» с inline-статусом (единственная точка
    // экспорта; дубль из шапки /stats убран — там нет app.js и нет места под статус).
    const block = document.getElementById('data-export-block');
    if (!block) return;
    const buttons = Array.from(block.querySelectorAll('[data-export-format]'));
    if (!buttons.length) return;
    block.classList.remove('hidden'); // PE-reveal: с JS экспорт реально работает
    const statusEl = document.getElementById('export-status');
    wireExportGroup(buttons, (m, e) => setExportStatus(statusEl, m, e));
  }

  // Персонализация (/settings): тема (авто/светлая/тёмная) + размер шрифта.
  // Контролы оживают только с JS (источник истины — window.__theme/__fontScale из
  // head.html), поэтому карточка раскрывается здесь (PE-reveal). Сегмент темы —
  // radiogroup со стрелочной навигацией; шаг шрифта блокируется на краях через
  // aria-disabled (не native disabled — тот сбрасывает фокус на body).
  function initPersonalization() {
    const card = document.getElementById('personalization-card');
    if (!card || !window.__theme || !window.__fontScale) return;
    card.classList.remove('hidden');

    const themeCtl = document.getElementById('theme-pref-control');
    if (themeCtl) {
      const themeBtns = Array.from(themeCtl.querySelectorAll('[data-theme-pref]'));
      const syncTheme = () => {
        const pref = window.__theme.pref();
        themeBtns.forEach((b) => {
          const on = b.getAttribute('data-theme-pref') === pref;
          b.setAttribute('aria-checked', on ? 'true' : 'false');
          b.classList.toggle('is-active', on);
          b.tabIndex = on ? 0 : -1;
        });
      };
      themeBtns.forEach((b, i) => {
        b.addEventListener('click', () => window.__theme.set(b.getAttribute('data-theme-pref')));
        b.addEventListener('keydown', (e) => {
          let idx = -1;
          if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % themeBtns.length;
          else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + themeBtns.length) % themeBtns.length;
          if (idx < 0) return;
          e.preventDefault();
          window.__theme.set(themeBtns[idx].getAttribute('data-theme-pref'));
          themeBtns[idx].focus();
        });
      });
      document.addEventListener('themechange', syncTheme);
      syncTheme();
    }

    // Выбор дизайна (editorial/swiss/linear) — radiogroup, как сегмент темы.
    // Источник истины — window.__design (head.html), переключение мгновенное
    // (флип data-design на <html>, без перезагрузки).
    const designCtl = document.getElementById('design-pref-control');
    if (designCtl && window.__design) {
      const designBtns = Array.from(designCtl.querySelectorAll('[data-design-pref]'));
      const syncDesign = () => {
        const cur = window.__design.current();
        designBtns.forEach((b) => {
          const on = b.getAttribute('data-design-pref') === cur;
          b.setAttribute('aria-checked', on ? 'true' : 'false');
          b.classList.toggle('is-active', on);
          b.tabIndex = on ? 0 : -1;
        });
      };
      designBtns.forEach((b, i) => {
        b.addEventListener('click', () => window.__design.set(b.getAttribute('data-design-pref')));
        b.addEventListener('keydown', (e) => {
          let idx = -1;
          if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % designBtns.length;
          else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + designBtns.length) % designBtns.length;
          if (idx < 0) return;
          e.preventDefault();
          window.__design.set(designBtns[idx].getAttribute('data-design-pref'));
          designBtns[idx].focus();
        });
      });
      document.addEventListener('designchange', syncDesign);
      syncDesign();
    }

    const valEl = document.getElementById('font-scale-value');
    const decBtn = document.getElementById('font-decrease');
    const incBtn = document.getElementById('font-increase');
    const resetBtn = document.getElementById('font-reset');
    const fs = window.__fontScale;
    const syncFont = () => {
      const s = fs.get();
      if (valEl) valEl.textContent = Math.round(s * 100) + '%';
      if (decBtn) decBtn.setAttribute('aria-disabled', s <= fs.min ? 'true' : 'false');
      if (incBtn) incBtn.setAttribute('aria-disabled', s >= fs.max ? 'true' : 'false');
      if (resetBtn) resetBtn.setAttribute('aria-disabled', s === 1 ? 'true' : 'false');
    };
    if (decBtn) decBtn.addEventListener('click', () => { if (decBtn.getAttribute('aria-disabled') !== 'true') fs.stepBy(-fs.STEP); });
    if (incBtn) incBtn.addEventListener('click', () => { if (incBtn.getAttribute('aria-disabled') !== 'true') fs.stepBy(fs.STEP); });
    if (resetBtn) resetBtn.addEventListener('click', () => { if (resetBtn.getAttribute('aria-disabled') !== 'true') fs.reset(); });
    document.addEventListener('fontscalechange', syncFont);
    syncFont();
  }

  document.addEventListener('DOMContentLoaded', () => {
    hydrateProgressBarsFromData();
    document.querySelectorAll('.btn-favorite').forEach((button) => {
      const favorite = button.getAttribute('aria-pressed') === 'true' || button.classList.contains('active');
      applyFavoriteButtonState(button, favorite);
    });
    initBrowseFlashcardReveal();
    initStreakBar();
    initHintButton();
    initShuffleTopic();
    initSessionFormSync();
    initSessionModeForm();
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
    initExportButtons();
    initPersonalization();
    initDangerousFormGuard();
    initSubmitOnceGuard();
    initFlashcardShortcuts();
    initKeyboardHelp();
    initCopyCode();
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

  // Русская плюрализация по mod10/mod100 (1 вопрос / 2 вопроса / 5 вопросов).
  function pluralRu(n, one, few, many) {
    const mod10 = n % 10;
    const mod100 = n % 100;
    if (mod10 === 1 && mod100 !== 11) return one;
    if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return few;
    return many;
  }

  // EXAM добавляет штрафные вопросы за ошибку (app.exam-penalty-questions) —
  // сообщаем об этом в вердикте, иначе прогресс «1/1 → 2/6» меняется молча.
  // Один append в том же кадре, что и рендер вердикта: atomic-live-регион
  // перечитывается вместе с появлением, без повторных прочтений. round-01 B2.
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
    // Только null/undefined → пусто. Иначе falsy-но-значимые 0/false
    // (например, индекс шага трассировки 0) схлопывались в пустую строку.
    if (str == null) return '';
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
    // /api/regenerate admin-gated (SecurityConfig), как и /export — без заголовка
    // X-Admin-Token всегда 403. Тот же токен, что и для экспорта (один кэш).
    // В seed-first dev кнопка скрыта (th:if=aiEnabled=false), но при включённом
    // AI это рабочий путь только для админа — фикс того же класса, что экспорт.
    const token = obtainAdminToken();
    if (!token) return; // пользователь отменил ввод
    button.classList.add('regenerating');
    button.title = 'Удаляю варианты…';
    try {
      const resp = await apiFetch(API.REGENERATE, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-Admin-Token': token
        },
        body: 'questionId=' + encodeURIComponent(questionId)
      });
      if (resp.ok) {
        clearInlineAlert();
        button.classList.remove('regenerating');
        button.classList.add('done');
        button.innerHTML = icon('circle-check', 'ed-icon-success');
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
        button.innerHTML = icon('circle-x', 'ed-icon-error');
        button.title = 'Ошибка при удалении';
        // Неверный admin-токен — сбрасываем кэш, чтобы следующий клик переспросил.
        // «не настроен» (server-misconfig) НЕ сбрасываем: токен пользователя ни при чём.
        if (resp.status === 401 || resp.status === 403) {
          const notConfigured = (err.message || '').toLowerCase().includes(SERVER_TOKEN_UNSET_MARKER);
          if (!notConfigured) clearStoredToken();
        }
        setInlineAlert(err.message || 'Не удалось перегенерировать варианты.');
      }
    } catch (err) {
      button.classList.remove('regenerating');
      button.innerHTML = icon('circle-x', 'ed-icon-error');
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
    submitBtn.setAttribute('title', `Клавиши 1–${Math.min(optionInputs.length, 9)} — выбор, Enter — ответить`);
  }

  // Горячие клавиши: 1-9 выбор варианта, Enter — отправка
  document.addEventListener('keydown', (event) => {
    // Открыта модалка справки (role=dialog/aria-modal) → горячие клавиши страницы
    // не срабатывают: иначе «1-9» выбирали бы вариант ПОД оверлеем, а фокус утекал
    // из модалки (нарушение focus-trap). Подтверждено замером: «2» при открытой
    // справке выделяла вариант и уводила фокус на radio под оверлеем.
    if (document.querySelector('.kbd-help-overlay:not(.hidden)')) return;
    if (answered) return;
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName) && event.target.type !== 'radio') {
      return;
    }
    // Элементы со своей семантикой Enter/Space (нативный <summary> у «Пример
    // кода», кнопки, ссылки) не перехватываем — иначе Enter на summary сабмитил
    // бы ответ вместо раскрытия примера. Сабмит-кнопка form'ы тоже BUTTON, но
    // её нативный Enter/Space всё равно отправляет форму через submit-листенер.
    if (event.target && (event.key === 'Enter' || event.key === ' ') &&
        ['SUMMARY', 'BUTTON', 'A'].includes(event.target.tagName)) {
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
  // На этих тегах сохраняем class: <code class="language-java"> (подсветка
  // highlight.js). Без class динамически вставленный ответ терял подсветку кода.
  // class не исполняет JS — безопасно.
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

  // Дорисовывает highlight.js-подсветку в контенте, вставленном ПОСЛЕ
  // DOMContentLoaded (ответ/пояснения приходят через AJAX, и начальный
  // hljs-инициализатор их уже не трогает). Без этого код оставался без подсветки.
  function renderDynamicContent(container) {
    if (!container) return;
    if (typeof hljs !== 'undefined') {
      container.querySelectorAll('pre code').forEach(function (block) {
        try { hljs.highlightElement(block); } catch (_) { /* подсветка не критична */ }
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
    // Запоминаем реальную подпись кнопки (шаблон рендерит «Проверить ответ»),
    // чтобы при ошибке вернуть её, а не хардкод «Ответить» (рассинхрон меток).
    const originalSubmitText = (submitBtn.textContent || '').trim() || 'Проверить ответ';
    submitBtn.disabled = true;
    submitBtn.textContent = 'Проверяю…';
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
        submitBtn.textContent = originalSubmitText;
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
      submitBtn.textContent = originalSubmitText;
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
    // Подсветка кода в только что вставленных пояснениях вариантов.
    renderDynamicContent(optionsContainer);
  }

  function renderFeedbackHtml(data) {
    const isCorrect = data.correct;
    const safeHtml = sanitizeHtml(data.answerHtml);

    feedbackDiv.innerHTML = `
      <div class="${isCorrect ? 'result-correct' : 'result-wrong'}">
        <strong>${isCorrect ? icon('circle-check', 'ed-icon-lead') + 'Верно' : icon('circle-x', 'ed-icon-lead') + 'Неверно'}</strong>
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
    // role=group + aria-labelledby — как у .flashcard-grade-buttons в шаблоне:
    // обе группы это «оцени свою уверенность после ответа», a11y-семантика должна
    // совпадать. Без этого скринридер читал label как оторванный текст, а не как
    // имя группы кнопок. id стабилен: на странице одновременно одна такая группа
    // (между вопросами идёт навигация window.location).
    confidenceDiv.setAttribute('role', 'group');
    confidenceDiv.setAttribute('aria-labelledby', 'confidence-label');
    // aria-live=polite: группа вставляется ПОСЛЕ того как фокус уже ушёл в
    // #result-feedback (showResult → feedbackDiv.focus()), поэтому сама себя
    // озвучивает при появлении — иначе SR-пользователь не узнал бы о появлении
    // запроса «оцени уверенность» без ручного таб-обхода.
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
    confidenceBtns.forEach(btn => {
      btn.addEventListener('click', async () => {
        if (confidenceSubmitted) return;
        confidenceSubmitted = true;
        // Гасим всю группу сразу: клик принят, запрос в полёте. Без этого на
        // время await кнопки выглядели активными, а повторные клики молча
        // отбрасывались флагом — без видимого отклика. На ошибке вернём активность.
        confidenceBtns.forEach(b => { b.disabled = true; });

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
          confidenceBtns.forEach(b => { b.disabled = false; });
          setInlineAlert('Не удалось сохранить уверенность. Попробуйте ещё раз.');
          return;
        }

        confidenceBtns.forEach(b => {
          b.classList.remove('selected');
          b.setAttribute('aria-pressed', 'false');
          b.disabled = true;
        });
        btn.classList.add('selected');
        // Скринридеру нужно знать, КАКОЙ уровень выбран: .selected — чисто
        // визуальный класс. aria-pressed=true на нажатой (теперь disabled)
        // кнопке озвучивает выбор как нажатое состояние toggle-кнопки.
        btn.setAttribute('aria-pressed', 'true');
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

  // Раскрытые блоки «доп. анализа» (takeaway / трейс кода / похожие вопросы)
  // кладём в контейнер #extra-analysis-content, который стоит СРАЗУ ПОСЛЕ
  // кнопки-триггера. Раньше они вставлялись через feedbackDiv.after(), то есть
  // МЕЖДУ фидбэком и кнопкой — и кнопка «Доп. анализ загружен» оставалась
  // сиротой ВНИЗУ, под раскрытым контентом (триггер ниже того, что он раскрыл).
  // appendChild в общий контейнер сохраняет порядок добавления и держит весь
  // раскрытый контент ПОД кнопкой. Fallback на старое поведение, если контейнера
  // нет (напр. чужая разметка) — чтобы блоки не потерялись.
  function appendAnalysisBlock(el) {
    const sink = document.getElementById('extra-analysis-content');
    if (sink) sink.appendChild(el);
    else feedbackDiv.after(el);
  }

  function fetchWrongFeedback(questionId, selectedOptionId, rethrowOnError = false) {
    const wrongBlock = feedbackDiv.querySelector('.result-wrong');
    if (!wrongBlock) return;
    return fetchWithPlaceholder(
      API.WRONG_FEEDBACK,
      wrongBlock,
      '<span class="wrong-feedback-loader">' + icon('bot', 'ed-icon-lead') + 'Анализирую ошибку…</span>',
      (fbData, placeholderEl) => {
        placeholderEl.classList.remove('loading');
        if (fbData.available && fbData.feedback) {
          placeholderEl.innerHTML =
            '<div class="wrong-feedback-title">' + icon('lightbulb', 'ed-icon-lead') + 'Почему это неверно:</div>' +
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
      '<span class="takeaway-loading-text">Загрузка…</span>',
      (tkData, placeholderEl) => {
        placeholderEl.remove();
        if (tkData.takeaway) {
          const tkDiv = document.createElement('div');
          tkDiv.className = 'content-block takeaway-block';
          tkDiv.innerHTML =
            '<div class="takeaway-title">' + icon('target', 'ed-icon-lead') + 'Главное, что нужно запомнить:</div>' +
            '<div class="takeaway-text">' + escapeHtml(tkData.takeaway) + '</div>';
          appendAnalysisBlock(tkDiv);
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
      '<span class="comparison-loading-text">Загрузка…</span>',
      (cmpData, placeholderEl) => {
        placeholderEl.remove();
        if (cmpData.comparison) {
          try {
            const parsed = typeof cmpData.comparison === 'string' ? JSON.parse(cmpData.comparison) : cmpData.comparison;
            if (parsed.criteria && parsed.criteria.length > 0) {
              const cmpDiv = document.createElement('div');
              cmpDiv.className = 'content-block comparison-block';
              let tableHtml = '<div class="comparison-title">' + icon('chart', 'ed-icon-lead') + 'Сравнение: выбранное vs правильное</div>';
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
      '<span class="code-trace-loading-text">Загрузка…</span>',
      (traceData, placeholderEl) => {
        placeholderEl.remove();
        if (traceData.trace) {
          try {
            const parsed = typeof traceData.trace === 'string' ? JSON.parse(traceData.trace) : traceData.trace;
            if (parsed.steps && parsed.steps.length > 0) {
              const traceDiv = document.createElement('div');
              traceDiv.className = 'content-block code-trace-block';
              let html = '<details><summary class="code-trace-title">' + icon('search', 'ed-icon-lead') + 'Пошаговое выполнение кода</summary>';
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
              appendAnalysisBlock(traceDiv);
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
    let relatedHtml = '<h3 class="related-questions-title">' + icon('link', 'ed-icon-lead') + 'Похожие вопросы для закрепления:</h3>';
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
    appendAnalysisBlock(relatedDiv);
  }

  function showResult(data) {
    const isCorrect = data.correct;
    const questionId = (form.querySelector('input[name="questionId"]') || {}).value || '';

    applyOptionStyles(data);
    submitBtn.classList.add('hidden');
    nextLink.textContent = 'Следующий вопрос';
    nextLink.href = buildNextQuestionHref(questionId);
    delete nextLink.dataset.finishSession;
    renderFeedbackHtml(data);
    setAnswerFlowStep('result');
    if (answerFlowHint) {
      answerFlowHint.classList.remove('hidden');
    }

    // total ДО обновления прогресса: нужен для детекции штрафных вопросов EXAM.
    const progressTrackEl = document.querySelector('.session-progress-track');
    const prevSessionTotal = progressTrackEl
      ? Number.parseInt(progressTrackEl.getAttribute('aria-valuemax') || '0', 10)
      : 0;
    updateSessionProgress(data);
    if (data.session) {
      // EXAM наказывает ошибку штрафными вопросами (app.exam-penalty-questions) —
      // раньше total рос молча («1/1» → «2/6» без объяснения). round-01 B2.
      if (prevSessionTotal > 0 && data.session.total > prevSessionTotal) {
        appendPenaltyNotice(data.session.total - prevSessionTotal, data.session.total);
      }
      // Последний вопрос отвечен: «Следующего вопроса» не существует — кнопка
      // честно ведёт к итогам (POST /finish в onclick). round-01 C-live-2.
      if (data.session.finished) {
        nextLink.textContent = 'Итоги сессии';
        nextLink.dataset.finishSession = 'true';
      }
    }
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
      extraAnalysisBtn.removeAttribute('aria-disabled');
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
        // aria-disabled, не disabled: сохраняем фокус клавиатуры на кнопке
        // (native disabled сбросил бы его на <body>). Повтор гасит флаг loaded.
        extraAnalysisBtn.setAttribute('aria-disabled', 'true');
        extraAnalysisBtn.setAttribute('aria-busy', 'true');
        extraAnalysisBtn.setAttribute('aria-expanded', 'true');
        extraAnalysisBtn.textContent = EXTRA_ANALYSIS_BUTTON_LOADING_TEXT;
        try {
          const requests = [];
          requests.push(fetchTakeaway(questionId, true));
          requests.push(fetchCodeTrace(questionId, true));
          const results = await Promise.allSettled(requests);
          renderRelatedQuestions(data);
          // realCount исключает undefined-слоты (fetchCodeTrace возвращает undefined
          // для вопросов без кода → allSettled считает их fulfilled).
          const realCount = requests.filter(r => r != null).length;
          const failedCount = results.filter(r => r.status === 'rejected').length;
          if (realCount > 0 && failedCount === realCount) {
            // Полный провал: возвращаем кнопку в кликабельное состояние (как на
            // result-странице), иначе она застывала на «загружен» без контента.
            loaded = false;
            extraAnalysisBtn.removeAttribute('aria-disabled');
            extraAnalysisBtn.setAttribute('aria-expanded', 'false');
            extraAnalysisBtn.textContent = EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;
            setInlineAlert('Не удалось загрузить доп. анализ. Попробуйте ещё раз.');
          } else {
            extraAnalysisBtn.textContent = EXTRA_ANALYSIS_BUTTON_DONE_TEXT;
            if (failedCount > 0) {
              setInlineAlert('Часть блоков доп. анализа не загрузилась. Можно продолжить тренировку.');
            }
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
      // Сессия завершена → не «следующий вопрос», а POST /finish прямо к итогам
      // (минуя промежуточный экран «Сессия завершена»). round-01 C-live-2.
      if (nextLink.dataset.finishSession === 'true') {
        const csrfInput = form ? form.querySelector('input[name="_csrf"]') : null;
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
        return;
      }
      trackUxMetric('next_question_click');
      window.location.href = nextLink.href;
    };
    nextLink.onkeydown = (e) => {
      // Enter нативно активирует <a> → срабатывает onclick (с проверкой
      // модификаторов и метрикой), поэтому Enter не перехватываем. Space на
      // ссылке по умолчанию скроллит страницу, а не переходит — его обрабатываем
      // сами, но через .click(), чтобы пройти ту же ветку onclick.
      if (e.key === ' ') {
        e.preventDefault();
        nextLink.click();
      }
    };
  }
  // Browse-режим флешкарты (вне FLASHCARD-сессии): раскрытие — нативный <details>
  // (PE — работает без JS). Серверный POST /flashcard-reveal здесь невозможен (нет
  // сессии) и терял бы тему через redirect:/. Ответ уже отрендерён сервером внутри
  // details. JS лишь: (1) при первом открытии дорисовывает подсветку кода,
  // (2) синхронизирует подпись summary. ::before-стрелка ▸/▾ — пустоэлемент,
  // textContent её не затрагивает.
  function initBrowseFlashcardReveal() {
    const details = document.getElementById('browse-details');
    const summary = document.getElementById('browse-reveal-btn');
    const answer = document.getElementById('browse-answer');
    if (!details || !summary || !answer) return;
    details.addEventListener('toggle', () => {
      summary.textContent = details.open ? 'Скрыть ответ' : 'Показать ответ';
      // Гидратируем (hljs) ОДИН раз при первом раскрытии: hljs 11.x при повторном
      // highlightElement по уже подсвеченному блоку варнит и дублирует span-обёртки.
      if (details.open && !details.dataset.hydrated) {
        details.dataset.hydrated = '1';
        renderDynamicContent(answer);
      }
    });
  }

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

// Защита от двойной отправки plain server-POST форм. AJAX-форма ответа
// (#interview-form) НЕ входит — у неё собственный guard (disable во время
// fetch). Главный кейс — /flashcard-grade: быстрый двойной клик/тап (или
// два быстрых нажатия клавиш 1–4) иначе отправил бы оценку дважды и сдвинул
// SM-2 фазу карточки на два шага. Делегированный слушатель ловит и
// динамически добавленные формы.
function initSubmitOnceGuard() {
  const NAV_FORM_SELECTOR = '#session-form, form[action$="/flashcard-grade"], form[action$="/flashcard-reveal"]';
  document.addEventListener('submit', (e) => {
    const form = e.target;
    if (!(form instanceof HTMLFormElement) || !form.matches(NAV_FORM_SELECTOR)) return;
    if (form.dataset.submitted === '1') { e.preventDefault(); return; }
    form.dataset.submitted = '1';
    const controls = form.querySelectorAll('button[type="submit"], input[type="submit"]');
    // Отключаем на СЛЕДУЮЩЕМ тике: к этому моменту браузер уже сериализовал
    // форму с name/value нажатой кнопки (grade=N). Синхронный disable выкинул
    // бы grade из тела POST.
    setTimeout(() => controls.forEach(c => { c.disabled = true; c.setAttribute('aria-busy', 'true'); }), 0);
    // Подстраховка: если навигация не произошла (ошибка сервера, форма
    // осталась на странице) — вернуть форму в рабочее состояние, чтобы
    // пользователь не застрял с заблокированными кнопками.
    setTimeout(() => {
      if (!form.isConnected) return;
      form.dataset.submitted = '';
      controls.forEach(c => { c.disabled = false; c.removeAttribute('aria-busy'); });
    }, 5000);
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
  const closeBtn = overlay.querySelector('.kbd-help-close');
  // Фокус-менеджмент модалки (role=dialog/aria-modal): при открытии уводим
  // фокус внутрь и запоминаем откуда пришли; при закрытии возвращаем обратно;
  // Tab зациклен внутри (focus-trap), чтобы фокус не уходил под оверлей.
  let lastFocused = null;
  const close = () => {
    if (overlay.classList.contains('hidden')) return;
    overlay.classList.add('hidden');
    if (lastFocused && typeof lastFocused.focus === 'function') lastFocused.focus();
    lastFocused = null;
  };
  const open = () => {
    lastFocused = document.activeElement;
    overlay.classList.remove('hidden');
    closeBtn.focus();
  };
  overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
  closeBtn.addEventListener('click', close);
  overlay.addEventListener('keydown', (e) => {
    if (e.key !== 'Tab') return;
    const focusable = overlay.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
    if (focusable.length === 0) return;
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (e.shiftKey && document.activeElement === first) {
      e.preventDefault();
      last.focus();
    } else if (!e.shiftKey && document.activeElement === last) {
      e.preventDefault();
      first.focus();
    }
  });
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

// Кнопка «копировать» на код-блоках: ответы (.markdown-content pre), пример кода
// фокуса (.question-code-details pre), код-сниппет результата (pre.question-code).
// Оборачиваем <pre> в .code-copy-wrap (position:relative) и вешаем кнопку в угол.
// Запускается ПОСЛЕ hljs (DOMContentLoaded в head.html зарегистрирован раньше) —
// подсветка уже на <code>, перенос узла её сохраняет. navigator.clipboard нет в
// insecure-context (http не-localhost) → просто не добавляем кнопку (не дразним).
function initCopyCode() {
  if (!navigator.clipboard || !navigator.clipboard.writeText) return;
  // Локальный icon-хелпер: эта функция объявлена вне IIFE (рядом с
  // initKeyboardHelp/initFlashcardShortcuts), поэтому приватный icon() из IIFE
  // здесь недоступен — собираем тот же <svg><use> сами.
  const svgIcon = (name) => '<svg class="ed-icon" aria-hidden="true"><use href="#i-' + name + '"></use></svg>';
  const blocks = document.querySelectorAll('.markdown-content pre, .question-code-details pre, pre.question-code');
  blocks.forEach((pre) => {
    if (pre.parentElement && pre.parentElement.classList.contains('code-copy-wrap')) return;
    const wrap = document.createElement('div');
    wrap.className = 'code-copy-wrap';
    pre.parentNode.insertBefore(wrap, pre);
    wrap.appendChild(pre);
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'code-copy-btn';
    btn.setAttribute('aria-label', 'Копировать код');
    btn.title = 'Копировать код';
    const idle = svgIcon('copy') + '<span class="code-copy-label">Копировать</span>';
    btn.innerHTML = idle;
    wrap.appendChild(btn);
    let resetTimer = null;
    const flash = (cls, html) => {
      btn.classList.remove('is-copied', 'is-error');
      if (cls) btn.classList.add(cls);
      btn.innerHTML = html;
      if (resetTimer) clearTimeout(resetTimer);
      resetTimer = setTimeout(() => { btn.classList.remove('is-copied', 'is-error'); btn.innerHTML = idle; }, 2000);
    };
    btn.addEventListener('click', () => {
      const codeEl = pre.querySelector('code') || pre;
      navigator.clipboard.writeText(codeEl.innerText)
        .then(() => flash('is-copied', svgIcon('check') + '<span class="code-copy-label">Скопировано</span>'))
        .catch(() => flash('is-error', svgIcon('x') + '<span class="code-copy-label">Ошибка</span>'));
    });
  });
}

function initFlashcardShortcuts() {
  const phase = document.querySelector('.flashcard-phase');
  if (!phase) return;
  const revealBtn = phase.querySelector('.flashcard-reveal-btn');
  const gradeBtns = phase.querySelectorAll('.flashcard-grade-btn');
  document.addEventListener('keydown', (event) => {
    // Та же защита, что и для MCQ: при открытой модалке справки флешкард-шорткаты
    // (Space/Enter/1-4) не должны утекать под оверлей.
    if (document.querySelector('.kbd-help-overlay:not(.hidden)')) return;
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
