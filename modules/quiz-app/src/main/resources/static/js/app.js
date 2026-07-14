(() => {
  const UI_CONSTANTS = Object.freeze({
    FAVORITE_ADD_LABEL: 'Добавить в избранное',
    FAVORITE_REMOVE_LABEL: 'Убрать из избранного',
    // v2→v3: ловушка round-01 B7. Контролы режимов (instant/hard/review/adaptive/timer)
    // удалены из шаблонов вместе со старым index.html — UI для их изменения больше нет,
    // но loadLearningPrefs продолжал ПРИМЕНЯТЬ stale-значения v2: hardMode=true навсегда
    // прятал confidence-кнопки (L1825) и доп.анализ (L1832), timerSeconds>0 запускал
    // неотключаемый таймер. Бамп ключа осиротляет старые v2-данные (их больше не читают),
    // а v3 никто не пишет (onLearningPrefChange недостижим) → prefs всегда = defaults.
    LEARNING_PREFS_STORAGE_KEY: 'quiz.learning.prefs.v3'
  });
  const API = {
    ANSWER: '/api/answer',
    NEXT: '/api/next',
    STATS: '/api/stats',
    TOPIC_STATS: '/api/topic-stats',
    CONFIDENCE: '/api/confidence',
    FAVORITE: '/api/favorite',
    STREAK: '/api/streak'
  };
  const FAVORITE_ADD_LABEL = UI_CONSTANTS.FAVORITE_ADD_LABEL;
  const FAVORITE_REMOVE_LABEL = UI_CONSTANTS.FAVORITE_REMOVE_LABEL;
  const LEARNING_PREFS_STORAGE_KEY = UI_CONSTANTS.LEARNING_PREFS_STORAGE_KEY;
  const defaultLearningPrefs = Object.freeze({
    instantMode: false,
    hardMode: false,
    reviewMode: false,
    adaptiveMode: false,
    timerSeconds: 0
  });
  let learningPrefs = { ...defaultLearningPrefs };
  const OPTION_EXPLANATION_COLLAPSE_QUERY = '(max-width: 760px)';

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
    // Politeness по типу: success — статус-подтверждение, не должно ПРЕРЫВАТЬ речь
    // скринридера → polite/status. Ошибки остаются assertive/alert (срочно). Шаблон
    // inline-alert.html хардкодит alert/assertive — здесь управляем динамически.
    // Ставим ДО textContent, чтобы озвучка ушла с новой вежливостью.
    if (kind === 'success') {
      alertEl.setAttribute('role', 'status');
      alertEl.setAttribute('aria-live', 'polite');
    } else {
      alertEl.setAttribute('role', 'alert');
      alertEl.setAttribute('aria-live', 'assertive');
    }
    alertEl.textContent = message;
    alertEl.classList.remove('hidden', 'success');
    if (kind === 'success') {
      alertEl.classList.add('success');
    }
    // #interview-alert — единственный ВИДИМЫЙ канал ошибки, стоит выше вердикта и
    // кнопки доп-анализа. При провале доп-анализа (юзер внизу у кнопки) он уходит за
    // верхнюю кромку; при сабмите без выбора focus() уводит к первой опции и алерт
    // остаётся ниже фолда. role=alert озвучивает его AT, но зрячий не видит —
    // подтягиваем в кадр, если вне вьюпорта. Скролл уважает ось «Движение».
    const r = alertEl.getBoundingClientRect();
    if (r.top < 0 || r.bottom > window.innerHeight) {
      const dm = document.documentElement.getAttribute('data-motion');
      const reduced = dm === 'off' ? true
        : dm === 'on' ? false
        : !!(window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches);
      alertEl.scrollIntoView({ behavior: reduced ? 'auto' : 'smooth', block: 'center' });
    }
  }

  function clearInlineAlert() {
    const alertEl = document.getElementById('interview-alert');
    if (!alertEl) return;
    alertEl.textContent = '';
    alertEl.classList.add('hidden');
    alertEl.classList.remove('success');
  }

  function setInteractionBusy(busy) {
    if (form) {
      form.setAttribute('aria-busy', busy ? 'true' : 'false');
    }
    if (optionsContainer) {
      optionsContainer.setAttribute('aria-disabled', busy ? 'true' : 'false');
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
          const daysEl = bar.querySelector('[data-streak-days]');
          const fillEl = bar.querySelector('[data-streak-fill]');
          const countEl = bar.querySelector('[data-streak-count]');
          // Флейм-обёртка: .streak-fire в детальном баре, иначе сам .ed-icon в чипе.
          const fireEl = bar.querySelector('.streak-fire, .ed-icon');
          const hasGoalProgress = !!fillEl || !!countEl;
          // Стрик 0 (новый пользователь): «🔥 0 дней» демотивирует и противоречит
          // анти-дофаминовому тону виджета (см. today-widget.html). Прячем флейм+дни
          // (через .hidden — у .streak-fire/.ed-icon явный display, атрибут hidden их
          // не скрыл бы). Бар показываем только если в нём есть дневной goal-прогресс
          // (детальный #streak-bar = «0/10» осмыслен и при нулевом стрике).
          if (d.streak > 0) {
            if (daysEl) { daysEl.textContent = daysText(d.streak); daysEl.classList.remove('hidden'); }
            if (fireEl) fireEl.classList.remove('hidden');
          } else {
            if (daysEl) daysEl.classList.add('hidden');
            if (fireEl) fireEl.classList.add('hidden');
          }
          if (fillEl) setProgressValue(fillEl, pct);
          if (countEl) countEl.textContent = d.today + '/' + d.goal;
          if (d.streak > 0 || hasGoalProgress) bar.classList.remove('hidden');
          if (d.goalReached) bar.classList.add('streak-goal-reached');
        });
      })
      .catch(() => { /* прогресс за день не критичен — тихо пропускаем */ });
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
    // CTA называет КОНКРЕТНЫЙ режим (винительный падеж), а не дженерик «Начать
    // сессию» — кнопка обещает ровно то, что произойдёт после клика. Ключи = enum
    // SessionMode; «Начать сессию» остаётся defensive-фоллбэком для неизвестного
    // значения. Падежи зафиксированы вручную (тренировка→тренировку — единственная
    // неноминативная форма), поэтому карта, а не lowcase(option.text).
    const MODE_CTA = {
      TRAINING: 'Начать тренировку',
      STUDY: 'Начать изучение',
      FLASHCARD: 'Начать флешкарты',
      EXAM: 'Начать экзамен',
      MARATHON: 'Начать интенсив',
    };
    const sync = () => {
      const isTraining = modeSelect.value === 'TRAINING';
      countField.classList.toggle('hidden', isTraining);
      startBtn.textContent = MODE_CTA[modeSelect.value] || 'Начать сессию';
    };
    const countInput = countField.querySelector('input[name="count"]');
    // SET-15: при явной СМЕНЕ режима подставляем per-mode серверный дефолт
    // (data-default-count у <option>) — чтобы поле не оставалось жёстко «20» для
    // MARATHON/STUDY/FLASHCARD (их серверный дефолт 50, EXAM=20). Слушатель на
    // 'change' срабатывает ТОЛЬКО на явную смену, не на первичном sync() при
    // загрузке — иначе затёрли бы restore счётчика из localStorage (initSessionFormSync).
    modeSelect.addEventListener('change', () => {
      const opt = modeSelect.selectedOptions[0];
      const def = opt && opt.getAttribute('data-default-count');
      if (countInput && def) countInput.value = def;
    });
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
      // localStorage обёрнут в try/catch: в private/lockdown-режимах getItem кидает
      // SecurityError, а это самый ранний вызов в цепочке DOMContentLoaded —
      // непойманное исключение прервало бы инициализацию персонализации, вкладок
      // и т.д. ниже. Недоступность хранилища деградирует мягко (без restore).
      let storedRaw = '';
      try { storedRaw = localStorage.getItem(sessionCountStorageKey) || ''; } catch (_) { /* приватный режим — без restore */ }
      const storedCount = Number.parseInt(storedRaw, 10);
      if (!Number.isNaN(storedCount) && storedCount >= 1 && storedCount <= 200) {
        countInput.value = String(storedCount);
      }
      countInput.addEventListener('change', () => {
        const value = Number.parseInt(countInput.value || '', 10);
        if (!Number.isNaN(value) && value >= 1 && value <= 200) {
          try { localStorage.setItem(sessionCountStorageKey, String(value)); } catch (_) { /* приватный режим — не сохраняем */ }
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

  // Доступная замена window.prompt для ввода admin-токена. native prompt() при
  // вводе учётных данных выглядит как фишинг-диалог («localhost:8080 says…») и не
  // стилизуется — для токена это плохо. Здесь — внутри-приложенческая модалка
  // (role=dialog/aria-modal, focus-trap, Esc/Отмена, возврат фокуса, type=password).
  // Возвращает Promise<string|null> (null = отмена). Классы .prompt-* — отдельная
  // модалка (НЕ kbd-help), стилизована теми же токенами (base.css).
  function promptModal(opts) {
    opts = opts || {};
    // mode:'confirm' — подтверждение деструктивного действия: alertdialog без
    // input, сообщение вместо label, фокус на «Отмена» (безопасный дефолт),
    // resolve(true|false) вместо строки. Обычный режим не тронут.
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
        '</div>' +
        '</form>';
      // textContent (не innerHTML): opts не доверяем — защита от инъекции.
      overlay.querySelector('.prompt-modal-title').textContent = opts.title || (isConfirm ? 'Подтвердите действие' : 'Ввод');
      if (isConfirm) overlay.querySelector('.prompt-modal-message').textContent = opts.message || '';
      else overlay.querySelector('.prompt-modal-label').textContent = opts.label || '';
      overlay.querySelector('[data-prompt-ok]').textContent = opts.okText || (isConfirm ? 'Подтвердить' : 'OK');
      overlay.querySelector('[data-prompt-cancel]').textContent = opts.cancelText || 'Отмена';
      const form = overlay.querySelector('.prompt-modal');
      const input = overlay.querySelector('#prompt-modal-input');
      document.body.appendChild(overlay);
      // Фон под модалкой делаем inert+aria-hidden: aria-modal сам по себе НЕ
      // удерживает SR-курсор (browse-mode NVDA/VoiceOver читал бы шапку/контент за
      // оверлеем). Снимаем ровно с тех, кому проставили, в settle.
      const inerted = [];
      for (const el of Array.from(document.body.children)) {
        if (el === overlay || el.tagName === 'SCRIPT') continue;
        if (el.hasAttribute('inert') || el.getAttribute('aria-hidden') === 'true') continue;
        el.setAttribute('inert', '');
        el.setAttribute('aria-hidden', 'true');
        inerted.push(el);
      }
      const initialFocus = isConfirm ? overlay.querySelector('[data-prompt-cancel]') : input;
      window.requestAnimationFrame(() => initialFocus.focus());

      const cancelValue = isConfirm ? false : null;
      let done = false;
      function settle(value) {
        if (done) return;
        done = true;
        inerted.forEach((el) => { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
        overlay.remove();
        if (lastFocused && typeof lastFocused.focus === 'function') lastFocused.focus();
        resolve(value);
      }
      // submit (Enter / кнопка OK) → значение (confirm: true);
      // Отмена / Esc / клик по фону → null (confirm: false).
      form.addEventListener('submit', (e) => { e.preventDefault(); settle(isConfirm ? true : input.value); });
      overlay.querySelector('[data-prompt-cancel]').addEventListener('click', () => settle(cancelValue));
      overlay.addEventListener('click', (e) => { if (e.target === overlay) settle(cancelValue); });
      overlay.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') { e.preventDefault(); settle(cancelValue); return; }
        if (e.key !== 'Tab') return;
        const f = overlay.querySelectorAll('button, input, [href], [tabindex]:not([tabindex="-1"])');
        if (!f.length) return;
        const first = f[0], last = f[f.length - 1];
        if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
        else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
      });
    });
  }

  // Подтверждение деструктивного действия — alertdialog на машинерии promptModal
  // (inert-фон, focus-trap, Esc, возврат фокуса). Возвращает Promise<boolean>.
  function confirmModal(opts) {
    return promptModal(Object.assign({ mode: 'confirm' }, opts));
  }

  async function obtainAdminToken() {
    let token = readStoredToken();
    if (!token) {
      const entered = await promptModal({
        title: 'Admin-токен',
        label: 'Введи admin-токен (APP_ADMIN_TOKEN) — нужен для экспорта прогресса и регенерации вариантов:',
        inputType: 'password',
        okText: 'Продолжить',
        cancelText: 'Отмена'
      });
      token = (entered || '').trim();
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
    const token = await obtainAdminToken();
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
    const statusEl = document.getElementById('personalization-status');
    const announceSaved = (label, value) => {
      if (!statusEl) return;
      statusEl.textContent = 'Сохранено на этом устройстве: ' + label + ' — ' + value + '.';
      statusEl.classList.remove('hidden');
    };

    // Универсальная привязка seg-control (radiogroup персонализации) к
    // window.__X-API. Заменяет 5 почти одинаковых блоков (тема/дизайн/раскладка/
    // движение/ширина-чтения): подсветка активной кнопки + aria-checked + roving
    // tabindex, клик = set, ←→↑↓ = переход и set, ре-синк по событию X-change.
    // Единый контракт клавиатуры/a11y — меньше копипасты и мест для рассинхрона.
    const wireSegControl = (controlId, attr, label, getCurrent, setValue, eventName) => {
      const ctl = document.getElementById(controlId);
      if (!ctl) return;
      const btns = Array.from(ctl.querySelectorAll('[' + attr + ']'));
      if (!btns.length) return;
      const sync = () => {
        const cur = getCurrent();
        btns.forEach((b) => {
          const on = b.getAttribute(attr) === cur;
          b.setAttribute('aria-checked', on ? 'true' : 'false');
          b.classList.toggle('is-active', on);
          b.tabIndex = on ? 0 : -1;
        });
      };
      const apply = (button) => {
        const next = button.getAttribute(attr);
        if (getCurrent() === next) return;
        setValue(next);
        announceSaved(label, button.textContent.trim());
      };
      btns.forEach((b, i) => {
        b.addEventListener('click', () => apply(b));
        b.addEventListener('keydown', (e) => {
          let idx = -1;
          if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % btns.length;
          else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + btns.length) % btns.length;
          if (idx < 0) return;
          e.preventDefault();
          apply(btns[idx]);
          btns[idx].focus();
        });
      });
      if (eventName) document.addEventListener(eventName, sync);
      sync();
    };

    // Источник истины каждого сегмента — соответствующий window.__X (head.html);
    // переключение мгновенное (флип data-* на <html>, без перезагрузки).
    wireSegControl('theme-pref-control', 'data-theme-pref', 'Тема оформления',
      () => window.__theme.pref(), (v) => window.__theme.set(v), 'themechange');
    if (window.__design) wireSegControl('design-pref-control', 'data-design-pref', 'Дизайн',
      () => window.__design.current(), (v) => window.__design.set(v), 'designchange');
    if (window.__layout) wireSegControl('layout-pref-control', 'data-layout-pref', 'Раскладка',
      () => window.__layout.current(), (v) => window.__layout.set(v), 'layoutchange');
    if (window.__motion) wireSegControl('motion-pref-control', 'data-motion-pref', 'Движение',
      () => window.__motion.current(), (v) => window.__motion.set(v), 'motionchange');
    if (window.__readingWidth) wireSegControl('reading-width-pref-control', 'data-reading-width-pref', 'Ширина чтения',
      () => window.__readingWidth.current(), (v) => window.__readingWidth.set(v), 'readingwidthchange');
    if (window.__density) wireSegControl('density-pref-control', 'data-density-pref', 'Плотность',
      () => window.__density.current(), (v) => window.__density.set(v), 'densitychange');

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
    const announceFontScale = () => announceSaved('Размер шрифта', Math.round(fs.get() * 100) + '%');
    if (decBtn) decBtn.addEventListener('click', () => {
      if (decBtn.getAttribute('aria-disabled') !== 'true') { fs.stepBy(-fs.STEP); announceFontScale(); }
    });
    if (incBtn) incBtn.addEventListener('click', () => {
      if (incBtn.getAttribute('aria-disabled') !== 'true') { fs.stepBy(fs.STEP); announceFontScale(); }
    });
    if (resetBtn) resetBtn.addEventListener('click', () => {
      if (resetBtn.getAttribute('aria-disabled') !== 'true') { fs.reset(); announceFontScale(); }
    });
    document.addEventListener('fontscalechange', syncFont);
    syncFont();
  }

  // Вкладки /settings (Сессия | Оформление | Данные). PE: tablist скрыт без JS,
  // здесь раскрываем и ставим .js-tabs на <body> (CSS прячет неактивные панели).
  // Полный ARIA tabs-паттерн: roving tabindex, ←→↑↓/Home/End, aria-selected.
  // Последний раздел запоминаем в localStorage (по умолчанию — Сессия).
  function initSettingsTabs() {
    const tablist = document.getElementById('settings-tablist');
    if (!tablist) return;
    const tabs = Array.from(tablist.querySelectorAll('[role="tab"]'));
    if (!tabs.length) return;
    const panels = tabs.map((t) => document.getElementById(t.getAttribute('aria-controls')));
    if (panels.some((p) => !p)) return;

    document.body.classList.add('js-tabs');
    tablist.classList.remove('hidden');

    const STORAGE_KEY = 'settingsTab';
    const activate = (idx, focusTab) => {
      tabs.forEach((t, i) => {
        const on = i === idx;
        t.setAttribute('aria-selected', on ? 'true' : 'false');
        t.tabIndex = on ? 0 : -1;
        panels[i].classList.toggle('is-active', on);
      });
      if (focusTab) tabs[idx].focus();
      try { localStorage.setItem(STORAGE_KEY, tabs[idx].id); } catch (e) { /* приватный режим */ }
    };

    tabs.forEach((tab, i) => {
      tab.addEventListener('click', () => activate(i, false));
      tab.addEventListener('keydown', (e) => {
        let idx = -1;
        if (e.key === 'ArrowRight' || e.key === 'ArrowDown') idx = (i + 1) % tabs.length;
        else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') idx = (i - 1 + tabs.length) % tabs.length;
        else if (e.key === 'Home') idx = 0;
        else if (e.key === 'End') idx = tabs.length - 1;
        if (idx < 0) return;
        e.preventDefault();
        activate(idx, true);
      });
    });

    // Восстанавливаем последнюю вкладку (или первую — Сессия).
    let initial = 0;
    try {
      const savedIdx = tabs.findIndex((t) => t.id === localStorage.getItem(STORAGE_KEY));
      if (savedIdx >= 0) initial = savedIdx;
    } catch (e) { /* приватный режим */ }
    activate(initial, false);
  }

  document.addEventListener('DOMContentLoaded', () => {
    hydrateProgressBarsFromData();
    document.querySelectorAll('.btn-favorite').forEach((button) => {
      const favorite = button.getAttribute('aria-pressed') === 'true' || button.classList.contains('active');
      applyFavoriteButtonState(button, favorite);
    });
    initBrowseFlashcardReveal();
    initStreakBar();
    initShuffleTopic();
    initSessionFormSync();
    initSessionModeForm();
    initExportButtons();
    initPersonalization();
    initSettingsTabs();
    initDangerousFormGuard(confirmModal);
    initSubmitOnceGuard();
    initFlashcardShortcuts();
    initKeyboardHelp();
    enhanceOptionExplanationDisclosure(document);
    initCopyCode();
  });

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
        await parseApiError(resp);
        // НЕ пробрасываем server-message: /api/favorite кидает QuestionNotFound
        // с внутренним «id=N» (FavoriteService:32) → утечка БД-id. Фикс-строка
        // actionable, как clean-фоллбэк в catch ниже (сеть).
        setInlineAlert('Не удалось обновить избранное — возможно, вопрос устарел. Обнови страницу и повтори.');
      }
    } catch (err) {
      console.error('Favorite toggle failed:', err);
      setInlineAlert('Не удалось обновить избранное. Проверь сеть и повтори.');
    }
  }

  // Favorite: delegate from document so it works on index and result pages
  document.addEventListener('click', (e) => {
    const fav = e.target.closest('.btn-favorite');
    if (fav) {
      e.preventDefault();
      const questionId = fav.getAttribute('data-question-id');
      if (questionId) toggleFavorite(questionId, fav);
    }
  });

  const form = document.getElementById('interview-form');
  const optionsContainer = document.getElementById('interview-options');
  const submitBtn = document.getElementById('interview-submit');
  const feedbackDiv = document.getElementById('result-feedback');
  const nextLink = document.getElementById('next-question');
  const extraAnalysisBtn = document.getElementById('extra-analysis-toggle');
  const answerFlowHint = document.getElementById('answer-flow-hint');
  const instantModeToggle = document.getElementById('instant-mode-toggle');
  const hardModeToggle = document.getElementById('hard-mode-toggle');
  const reviewModeToggle = document.getElementById('review-mode-toggle');
  const adaptiveModeToggle = document.getElementById('adaptive-mode-toggle');
  const timerSelect = document.getElementById('timer-select');
  const timerBadge = document.getElementById('question-timer');

  if (feedbackDiv) feedbackDiv.setAttribute('aria-live', 'polite');

  if (!form || !optionsContainer) return;

  const optionInputs = Array.from(form.querySelectorAll('input[name="optionId"]'));
  const ANSWER_LAYOUT_SKEW_THRESHOLD = 1.4;
  let answered = false;
  let timerInterval = null;

  function updateAnswerLayoutMode() {
    const labels = Array.from(optionsContainer.querySelectorAll('label[data-option-id]'));
    if (labels.length < 2) return;

    const lengths = labels.map(label => {
      const text = label.querySelector('span')?.textContent || '';
      return Math.max(1, text.replace(/\s+/g, ' ').trim().length);
    });
    const minLength = Math.min(...lengths);
    const maxLength = Math.max(...lengths);
    const lengthRatio = maxLength / minLength;
    const inlineCodeCounts = labels.map(label => label.querySelectorAll('span:not([class]) code').length);
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
  }

  loadLearningPrefs();
  applyLearningPrefsToDom();
  syncLearningPrefsToRequest();
  updateAnswerLayoutMode();
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
      // WCAG 2.1.4 Character Key Shortcuts (R1.44): печатный одиночный шорткат
      // активен ТОЛЬКО когда фокус внутри формы вопроса — исключение «active only
      // on focus». Autofocus (ниже) сажает фокус на 1-ю опцию при загрузке, так что
      // 1-9 работают сразу; но если пользователь увёл фокус (шапка/ссылка) — не
      // перехватываем цифру (раньше шорткат был document-wide → нарушение 2.1.4).
      if (form.contains(document.activeElement)) {
        const index = parseInt(key, 10) - 1;
        if (selectOptionByIndex(index)) {
          event.preventDefault();
        }
      }
    }
    if (key === 'Enter') {
      event.preventDefault();
      form.dispatchEvent(new Event('submit', { cancelable: true }));
    }
  });

  // WCAG 2.1.4 (R1.44): сажаем фокус на 1-ю опцию при загрузке вопроса, чтобы
  // клавиатурные шорткаты 1-9 (scope: фокус внутри формы, см. keydown выше)
  // работали сразу, а не только после ручного Tab. preventScroll — не прыгаем
  // мимо текста вопроса; программный focus() НЕ триггерит :focus-visible, поэтому
  // визуального кольца на загрузке нет (появится при первом Tab/стрелке). Только
  // MCQ-режим (есть опции) и до ответа.
  if (optionInputs.length > 0 && !answered) {
    optionInputs[0].focus({ preventScroll: true });
  }

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

  function optionExplanationSummaryText(explanation) {
    if (explanation.classList.contains('explanation-correct')) {
      return 'Почему это правильный ответ';
    }
    return explanation.closest('.option-wrong') ? 'Почему выбранный ответ неверен' : 'Пояснение к варианту';
  }

  function enhanceOptionExplanationDisclosure(container) {
    if (!container || !window.matchMedia || !window.matchMedia(OPTION_EXPLANATION_COLLAPSE_QUERY).matches) {
      return;
    }
    container.querySelectorAll('.option-explanation:not([data-disclosure-bound])').forEach((explanation) => {
      if (explanation.closest('.option-explanation-disclosure')) return;

      const details = document.createElement('details');
      details.className = 'option-explanation-disclosure';
      details.classList.add(explanation.classList.contains('explanation-correct')
        ? 'option-explanation-disclosure-correct'
        : 'option-explanation-disclosure-wrong');

      const summary = document.createElement('summary');
      summary.className = 'option-explanation-summary';
      summary.textContent = optionExplanationSummaryText(explanation);

      explanation.parentNode.insertBefore(details, explanation);
      details.appendChild(summary);
      details.appendChild(explanation);
      explanation.setAttribute('data-disclosure-bound', 'true');
    });
  }

  // Дорисовывает mermaid-диаграммы и highlight.js в контенте, вставленном
  // ПОСЛЕ DOMContentLoaded (ответ/пояснения приходят через AJAX, и начальные
  // инициализаторы mermaid/hljs их уже не трогают). Без этого диаграмма
  // оставалась сырым `graph TD ...`, а код — без подсветки.
  function renderDynamicContent(container) {
    if (!container) return;
    enhanceOptionExplanationDisclosure(container);
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
    stopQuestionTimer();
    // Запоминаем реальную подпись кнопки (шаблон рендерит «Проверить ответ»),
    // чтобы при ошибке вернуть её, а не хардкод «Ответить» (рассинхрон меток).
    const originalSubmitText = (submitBtn.textContent || '').trim() || 'Проверить ответ';
    submitBtn.disabled = true;
    submitBtn.textContent = 'Проверяю…';
    submitBtn.setAttribute('aria-busy', 'true');
    setInteractionBusy(true);
    clearInlineAlert();

    const formData = new FormData(form);
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
        // НЕ пробрасываем err.message: для /api/answer единственные доменные
        // ошибки — QuestionNotFound/OptionNotFound с внутренним «id=N» в тексте
        // (InterviewService 192/253/258) → утечка БД-id + dead-end. Фикс-строка
        // actionable и покрывает оба случая (устаревший вопрос/вариант).
        setInlineAlert('Не удалось проверить ответ — возможно, вопрос устарел. Обнови страницу и загрузи новый вопрос.');
        // submitBtn.disabled=true (выше) при клавиатурной активации кнопки сбрасывает
        // фокус на <body> (поэтому в других местах — aria-disabled). На success фокус
        // переносит showResult; в error-ветках возвращаем его на кнопку, иначе keyboard/SR
        // юзер осиротевает на <body> (WCAG 2.4.3). preventScroll бережёт скролл алерта.
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
      submitBtn.textContent = originalSubmitText;
      submitBtn.removeAttribute('aria-busy');
      setInteractionBusy(false);
      updateSubmitAvailability();
      startQuestionTimer();
      setInlineAlert('Сервер недоступен. Проверь соединение и повтори отправку.');
      // см. коммент в ветке !response.ok — возвращаем фокус на кнопку после re-enable.
      if (document.activeElement === document.body) submitBtn.focus({ preventScroll: true });
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
        statusLabelText = 'Твой выбор (ошибка)';
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
        // markdown-content — общие стили прозы: таблицы/код/списки
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
          setInlineAlert('Не удалось сохранить уверенность. Попробуй ещё раз.');
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

  // Раскрытые блоки «доп. анализа» (сегодня это только «похожие вопросы»)
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
    relatedHtml += '<div class="related-questions-list" role="list">';
    data.relatedQuestions.forEach(rq => {
      relatedHtml += '<a class="related-question-item" role="listitem" href="/?topic=' + encodeURIComponent(rq.topic)
        + '&group=' + encodeURIComponent(currentGroup)
        + '&ordered=' + encodeURIComponent(currentOrdered)
        + (currentImportant ? '&important=true' : '')
        + (currentOnlyWrong ? '&onlyWrong=true' : '')
        + (currentShuffle ? '&shuffle=true' : '')
        + (currentWeakTopics ? '&weakTopics=true' : '')
        + '">' + escapeHtml(rq.text) + '</a>';
    });
    relatedHtml += '</div>';
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
    // IA-фикс: после ответа «Следующий вопрос» переезжает в САМЫЙ низ пост-разбора —
    // под вердикт, пояснения и доп. анализ. Иначе primary-CTA «дальше» стоит ВЫШЕ
    // итога/объяснения (во всех трёх раскладках: в форме, над зоной разбора), и
    // вопрос можно перескочить, не прочитав разбор — прямой повод к «забыли про
    // удобство». Узел (со слушателями onclick/onkeydown) просто перемещаем: это
    // JS-only состояние (no-JS уходит POST-ом на /answer → result.html), id и стили
    // (.next-btn глобальный) сохраняются. Действие читается завершением сцены.
    const postAnswerZone = document.querySelector('[data-ui-fragment="post-answer-controls"]');
    if (postAnswerZone && nextLink.parentElement !== postAnswerZone) {
      postAnswerZone.appendChild(nextLink);
      // После переноса next блок training-actions полностью опустел (submit скрыт,
      // клавиатурная подсказка «1–9 — выбор…» уже неактуальна). Помечаем его
      // is-answered и прячем целиком (CSS): иначе пустой flex-контейнер со своим
      // margin-top оставил бы фантомный ~37px зазор между вариантами и зоной разбора.
      const trainingActions = document.querySelector('[data-ui-fragment="training-actions"]');
      if (trainingActions) trainingActions.classList.add('is-answered');
    }
    setInteractionBusy(false);
    feedbackDiv.setAttribute('tabindex', '-1');
    feedbackDiv.focus();
    // AI-разбор (feedback/comparison/takeaway/трейс кода) удалён вместе с
    // провайдерами. Единственное живое наполнение «доп. анализа» — похожие вопросы
    // для закрепления (приходят в ответе /api/answer, рендерятся без сети). Кнопка =
    // простое progressive-disclosure «Похожие вопросы»; в hard-режиме, как и раньше, скрыта.
    const hasRelated = !learningPrefs.hardMode
      && Array.isArray(data.relatedQuestions) && data.relatedQuestions.length > 0;
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
        // Кнопка отыграла роль — прячем, чтобы не осталась «нажатой пустышкой».
        extraAnalysisBtn.classList.add('hidden');
      };
    } else if (extraAnalysisBtn) {
      extraAnalysisBtn.classList.add('hidden');
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

})();

// Гард деструктивных форм ([data-confirm]). Вместо нативного window.confirm —
// внутренняя alertdialog-модалка (confirmModal передаётся из IIFE: она замкнута
// там вместе с promptModal; та же причина, что у obtainAdminToken — нативный
// диалог «localhost:8080 says…» не стилизуется и выглядит чужеродно). PE: без JS
// форма отправляется без подтверждения — как и раньше (confirm тоже жил в JS).
// После «Подтвердить» — requestSubmit, чтобы отработали остальные submit-гарды;
// повторный вход отсекается флагом data-confirmed.
function initDangerousFormGuard(confirmModal) {
  document.querySelectorAll('[data-confirm]').forEach((el) => {
    const form = el.closest('form');
    if (!form) return;
    form.addEventListener('submit', (e) => {
      if (form.dataset.confirmed === '1') { delete form.dataset.confirmed; return; }
      const msg = el.getAttribute('data-confirm');
      if (!msg) return;
      e.preventDefault();
      confirmModal({
        title: el.getAttribute('data-confirm-title') || 'Подтвердите действие',
        message: msg
      }).then((ok) => {
        if (!ok) return;
        form.dataset.confirmed = '1';
        form.requestSubmit();
      });
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
  // Справка перечисляет ТОЛЬКО шорткаты страницы-вопроса (выбор варианта,
  // отправка ответа, раскрытие/оценка флешкарты). На /settings и на странице
  // результата этих действий нет — не вешаем оверлей и обработчик «?», чтобы не
  // обещать несуществующие шорткаты. Гейт — наличие .focus-question (есть только
  // на focus-training, в обоих режимах). /stats подключает свой оверлей в stats.js.
  if (!document.querySelector('.focus-question')) return;
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
    '<button type="button" class="kbd-help-close" aria-label="Закрыть"><span aria-hidden="true">×</span></button>' +
    '</div>';
  document.body.appendChild(overlay);
  const closeBtn = overlay.querySelector('.kbd-help-close');
  // Фокус-менеджмент модалки (role=dialog/aria-modal): при открытии уводим
  // фокус внутрь и запоминаем откуда пришли; при закрытии возвращаем обратно;
  // Tab зациклен внутри (focus-trap), чтобы фокус не уходил под оверлей.
  let lastFocused = null;
  // Фон под модалкой inert+aria-hidden (см. promptModal: aria-modal не держит
  // SR-курсор). Храним проставленные, чтобы снять ровно их при закрытии.
  let inerted = [];
  const close = () => {
    if (overlay.classList.contains('hidden')) return;
    overlay.classList.add('hidden');
    inerted.forEach((el) => { el.removeAttribute('inert'); el.removeAttribute('aria-hidden'); });
    inerted = [];
    if (lastFocused && typeof lastFocused.focus === 'function') lastFocused.focus();
    lastFocused = null;
  };
  const open = () => {
    lastFocused = document.activeElement;
    overlay.classList.remove('hidden');
    inerted = [];
    for (const el of Array.from(document.body.children)) {
      if (el === overlay || el.tagName === 'SCRIPT') continue;
      if (el.hasAttribute('inert') || el.getAttribute('aria-hidden') === 'true') continue;
      el.setAttribute('inert', '');
      el.setAttribute('aria-hidden', 'true');
      inerted.push(el);
    }
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
    // Гасим «?» только на текстовых полях — там знак вопроса легитимный ввод.
    // radio/checkbox текст не принимают, а radio — основная клавиатурная
    // позиция на странице вопроса: без исключения справка недоступна с опций
    // (тот же принцип, что у гарда шорткатов 1-9 выше).
    if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)
        && !['radio', 'checkbox'].includes(event.target.type)) return;
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
  if (!blocks.length) return;
  // Общий live-region на результат копирования. Смена innerHTML на сфокусированной
  // кнопке скринридерами озвучивается ненадёжно (NVDA/VoiceOver по-разному), поэтому
  // исход дублируем в role=status — единый на все блоки кода. Критика round-01 C19.
  let copyStatus = document.getElementById('code-copy-status');
  if (!copyStatus) {
    copyStatus = document.createElement('div');
    copyStatus.id = 'code-copy-status';
    copyStatus.className = 'visually-hidden';
    copyStatus.setAttribute('role', 'status');
    copyStatus.setAttribute('aria-live', 'polite');
    document.body.appendChild(copyStatus);
  }
  const announceCopy = (msg) => {
    if (!copyStatus) return;
    // Пусто → текст на следующем кадре: гарантирует переобъявление даже при
    // повторном копировании того же блока (идентичный textContent иначе нем).
    copyStatus.textContent = '';
    requestAnimationFrame(() => { copyStatus.textContent = msg; });
  };
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
        .then(() => {
          flash('is-copied', svgIcon('check') + '<span class="code-copy-label">Скопировано</span>');
          announceCopy('Код скопирован в буфер обмена');
        })
        .catch(() => {
          flash('is-error', svgIcon('x') + '<span class="code-copy-label">Ошибка</span>');
          announceCopy('Не удалось скопировать код');
        });
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
    // Тот же гард, что в MCQ-обработчике (см. выше): элементы со своей семантикой
    // Enter/Space (нативный <summary> «Пример кода», кнопки, nav-ссылки шапки) не
    // перехватываем — иначе Enter/Space на них раскрывал бы флешкарту (или слал
    // POST /flashcard-reveal) вместо нативного действия. Критика round-01 B6 (WCAG 2.1.1).
    if (event.target && (event.key === 'Enter' || event.key === ' ') &&
        ['SUMMARY', 'BUTTON', 'A'].includes(event.target.tagName)) {
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
