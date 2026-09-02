import { pluralRu } from '../core/dom.js';

let shuffleBound = false;
let sessionModeBound = false;
let liveFiltersBound = false;
let sessionSyncBound = false;

export function initShuffleTopic() {
  if (shuffleBound) return;
  const shuffleBox = document.getElementById('shuffle-checkbox');
  const topicSelect = document.querySelector('select[name="topic"]');
  const groupSelect = document.querySelector('select[name="group"]');
  const orderedSelect = document.querySelector('select[name="ordered"]');
  const modeHint = document.getElementById('filter-mode-hint');
  if (!shuffleBox || !topicSelect) return;
  shuffleBound = true;

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
      if (modeHint) modeHint.textContent = 'Режим микс: вопросы идут случайно по всем темам.';
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

export function initSessionModeForm() {
  if (sessionModeBound) return;
  // Группа радиокнопок, а не <select>: режим выбирается карточками. Слушатель
  // висит на самой группе — событие change с радиокнопки всплывает, и восемь
  // отдельных подписок не нужны.
  const modeChoice = document.getElementById('session-mode-choice');
  const countField = document.getElementById('session-count-field');
  const startBtn = document.getElementById('session-start-btn');
  if (!modeChoice || !countField || !startBtn) return;
  sessionModeBound = true;

  // Подпись кнопки и признак «без счётчика» читаются с ВЫБРАННОЙ радиокнопки
  // (data-cta / data-unlimited из ModeUtils.options). Раньше здесь лежала своя
  // копия всех восьми подписей, ключами — имена enum-констант: переименование
  // константы молча роняло CTA в нейтральное «Начать сессию», а подпись могла
  // разойтись с той же подписью в Java, ничего не сломав.
  const selectedMode = () => modeChoice.querySelector('input[name="mode"]:checked');
  const countInput = countField.querySelector('input[name="count"]');
  const preview = document.getElementById('session-preview');
  const previewCount = preview?.querySelector('[data-preview-count]');
  const previewTime = preview?.querySelector('[data-preview-time]');
  const previewFilters = document.querySelector('[data-preview-filters]');
  const filtersForm = document.getElementById('filters-form');
  const SECONDS_PER_Q = 45;

  const chipCount = (form, key) => {
    const el = form.querySelector('[data-filter-count="' + key + '"]');
    if (!el) return null;
    const raw = (el.textContent || '').trim();
    return raw.length ? raw : null;
  };

  const describeFilters = () => {
    if (!filtersForm) return 'все темы';
    const parts = [];
    const groupSel = filtersForm.querySelector('[name="group"]');
    const topicSel = filtersForm.querySelector('[name="topic"]');
    if (groupSel?.value) {
      const opt = groupSel.selectedOptions[0];
      const raw = (opt?.textContent || '').trim();
      parts.push(raw.split(' - ')[0] || raw);
    }
    if (topicSel?.value) {
      const opt = topicSel.selectedOptions[0];
      parts.push((opt?.textContent || topicSel.value).trim());
    }
    if (filtersForm.querySelector('[name="important"]')?.checked) {
      const n = chipCount(filtersForm, 'favorites');
      parts.push(n != null ? ('избранные · ' + n) : 'избранные');
    }
    if (filtersForm.querySelector('[name="onlyWrong"]')?.checked) {
      const n = chipCount(filtersForm, 'wrong');
      parts.push(n != null ? ('ошибки · ' + n) : 'ошибки');
    }
    if (filtersForm.querySelector('[name="shuffle"]')?.checked) parts.push('микс тем');
    if (filtersForm.querySelector('[name="weakTopics"]')?.checked) {
      const n = chipCount(filtersForm, 'weak');
      parts.push(n != null ? ('слабые темы · ' + n) : 'слабые темы');
    }
    const ordered = filtersForm.querySelector('[name="ordered"]');
    if (ordered?.value === 'false') parts.push('свободный порядок');
    return parts.length ? parts.join(' · ') : 'все темы';
  };

  const updatePreview = () => {
    // Строка действующего отбора живёт в блоке «Что попадёт» рядом с самим
    // отбором, а не в полосе запуска: она описывает НАБОР, а полоса — сессию.
    // Поэтому её ищем в документе, а не внутри полосы.
    if (previewFilters) previewFilters.textContent = describeFilters();
    if (!preview) return;
    // Полоса приходит скрытой: без скрипта её числа были бы заглушками.
    preview.hidden = false;
    if (selectedMode()?.dataset.unlimited === 'true') {
      // Формулировки короткие сознательно: предпросмотр переехал в закреплённую
      // строку запуска, и его высота теперь вычитается из видимой части
      // конструктора. Прежние «Без ограничения по числу» и «сессия идёт, пока не
      // остановишь» не вставали в один ряд с двумя другими метриками и кнопкой —
      // строка ломалась на два ряда и накрывала собой чипы фильтров.
      if (previewCount) previewCount.textContent = 'без ограничения';
      if (previewTime) previewTime.textContent = 'пока не остановишь';
      return;
    }
    const n = countInput ? (Number.parseInt(countInput.value || '', 10) || 0) : 0;
    const pool = Number.parseInt(preview.getAttribute('data-pool-total') || '', 10);
    const mins = Math.max(1, Math.round((n * SECONDS_PER_Q) / 60));
    if (previewCount) {
      let label = n + ' ' + pluralRu(n, 'вопрос', 'вопроса', 'вопросов');
      if (!Number.isNaN(pool) && pool > 0) label += ' из ' + pool;
      previewCount.textContent = label;
    }
    if (previewTime) {
      previewTime.textContent = '≈ ' + mins + ' ' + pluralRu(mins, 'минута', 'минуты', 'минут');
    }
  };

  const sync = () => {
    const opt = selectedMode();
    // Прячем ВЕСЬ шаг «Сколько», а не одно поле внутри него: у режима без
    // счётчика шаг остался бы заголовком с номером над пустотой.
    const unlimited = opt?.dataset.unlimited === 'true';
    const volumeStep = document.getElementById('builder-volume') || countField;
    // Шаг подъезжает в вид, когда РАСКРЫВАЕТСЯ выбором счётного режима: без
    // этого на 1440×900 он появлялся целиком за вьюпортом и под закреплённой
    // полосой запуска — пользователь видел только смену подписи кнопки (P1
    // адверсарной приёмки viewport-first). scroll-margin-bottom у шага держит
    // запас над полосой; behavior не задаём — мгновенно и без конфликта с
    // prefers-reduced-motion.
    const wasHidden = volumeStep.classList.contains('hidden');
    volumeStep.classList.toggle('hidden', unlimited);
    if (wasHidden && !unlimited) volumeStep.scrollIntoView({ block: 'nearest' });
    startBtn.textContent = opt?.dataset.cta || 'Начать сессию';
    const modeMirror = document.getElementById('filters-mode-mirror');
    if (modeMirror) modeMirror.value = selectedMode()?.value || '';
    updatePreview();
  };

  if (countInput) countInput.addEventListener('input', updatePreview);
  modeChoice.addEventListener('change', () => {
    const def = selectedMode()?.dataset.defaultCount;
    if (countInput && def) countInput.value = def;
  });
  modeChoice.addEventListener('change', sync);
  if (filtersForm) {
    filtersForm.addEventListener('change', updatePreview);
    filtersForm.addEventListener('input', updatePreview);
  }
  window.__updateSessionPreview = updatePreview;
  sync();
}

export function initLiveFilters() {
  if (liveFiltersBound) return;
  const filtersForm = document.getElementById('filters-form');
  const sessionForm = document.getElementById('session-form');
  if (!filtersForm || !sessionForm) return;
  liveFiltersBound = true;

  const applyBtn = filtersForm.querySelector('[data-filters-apply]');
  if (applyBtn) applyBtn.classList.add('hidden');

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

  const syncSessionFromFilters = () => {
    const shuffleEnabled = !!getFilterField('shuffle')?.checked;
    upsertHidden('topic', shuffleEnabled ? '' : (getFilterField('topic')?.value || ''));
    upsertHidden('group', shuffleEnabled ? '' : (getFilterField('group')?.value || ''));
    upsertHidden('ordered', getFilterField('ordered')?.value || 'true');
    upsertHidden('important', getFilterField('important')?.checked ? 'true' : '');
    upsertHidden('onlyWrong', getFilterField('onlyWrong')?.checked ? 'true' : '');
    upsertHidden('shuffle', getFilterField('shuffle')?.checked ? 'true' : '');
    upsertHidden('weakTopics', getFilterField('weakTopics')?.checked ? 'true' : '');
    if (typeof window.__updateSessionPreview === 'function') window.__updateSessionPreview();
  };

  let timer = null;
  const FILTER_DEBOUNCE_MS = 400;
  const scheduleApply = () => {
    syncSessionFromFilters();
    const checkedMode = document.querySelector('#session-mode-choice input[name="mode"]:checked');
    const modeMirror = document.getElementById('filters-mode-mirror');
    if (checkedMode && modeMirror) modeMirror.value = checkedMode.value;
    clearTimeout(timer);
    timer = setTimeout(() => {
      if (typeof filtersForm.requestSubmit === 'function') filtersForm.requestSubmit();
      else filtersForm.submit();
    }, FILTER_DEBOUNCE_MS);
  };

  filtersForm.addEventListener('change', scheduleApply);
  syncSessionFromFilters();
}

export function initSessionFormSync() {
  if (sessionSyncBound) return;
  const filtersForm = document.getElementById('filters-form');
  const sessionForm = document.getElementById('session-form');
  if (!filtersForm || !sessionForm) return;
  sessionSyncBound = true;

  const countInput = sessionForm.querySelector('input[name="count"]');
  const sessionCountStorageKey = 'quiz.session.count';
  if (countInput) {
    let storedRaw = '';
    try { storedRaw = localStorage.getItem(sessionCountStorageKey) || ''; } catch (_) {}
    const storedCount = Number.parseInt(storedRaw, 10);
    if (!Number.isNaN(storedCount) && storedCount >= 1 && storedCount <= 200) {
      countInput.value = String(storedCount);
    }
    countInput.addEventListener('change', () => {
      const value = Number.parseInt(countInput.value || '', 10);
      if (!Number.isNaN(value) && value >= 1 && value <= 200) {
        try { localStorage.setItem(sessionCountStorageKey, String(value)); } catch (_) {}
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
    upsertHidden('topic', shuffleEnabled ? '' : (getFilterField('topic')?.value || ''));
    upsertHidden('group', shuffleEnabled ? '' : (getFilterField('group')?.value || ''));
    upsertHidden('ordered', getFilterField('ordered')?.value || 'true');
    upsertHidden('important', getFilterField('important')?.checked ? 'true' : '');
    upsertHidden('onlyWrong', getFilterField('onlyWrong')?.checked ? 'true' : '');
    upsertHidden('shuffle', getFilterField('shuffle')?.checked ? 'true' : '');
    upsertHidden('weakTopics', getFilterField('weakTopics')?.checked ? 'true' : '');
  });
}
