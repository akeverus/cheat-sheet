import { escapeHtml, setProgressValue } from '../core/dom.js';
import { enhanceScrollableTables } from '../content/table-scroll.js';
import { getTopicStats, displayTopicName } from './topic-data.js';
import { chartPalette, chartAnimation, chartsAvailable, onChartThemeChange, chartBox, labelMeasurer, onChartResize } from './chart-theme.js';

/**
 * Два графика вкладки «Темы»: прогресс и точность.
 *
 * Тем около 320 — все сразу дают нечитаемую кашу из тонких баров, поэтому рисуем
 * релевантный топ, а полные данные оставляем сортируемой таблице ниже.
 */
const MAX_BARS = 25;

/**
 * Бюджет подписей оси X.
 *
 * Подпись — имя темы из данных, а канвас режет молча: ни многоточия, ни ошибки,
 * ни следа в DOM. Замер на живой странице (1440, карточка 465×320, 25 баров):
 * Chart.js нарисовал 13 подписей из 25 — остальные удалил autoSkip, — а ось
 * заняла 151px из 320px, оставив данным 126px. Двенадцать баров без имени и
 * график, где подписи весят больше самих данных.
 *
 * Причина та же, что у фиксированного контейнера без записанного бюджета: длина
 * подписи приходит из данных, а размер коробки задан снаружи, и нигде не сказано,
 * сколько текста коробка держит. Поэтому бюджет считается здесь и выводится из
 * ГЕОМЕТРИИ КОРОБКИ, а не из длины пришедших имён:
 *
 *  - сколько баров — ширина области данных, делённая на шаг, при котором соседние
 *    подписи под 45° не наезжают друг на друга;
 *  - какой длины подпись — сколько её проекция съест по высоте при доле оси не
 *    больше {@link X_AXIS_MAX_SHARE} от канваса.
 *
 * Что не влезло — обрезается многоточием: промах обязан быть виден. Полное имя
 * остаётся в тултипе и в табличном дубле под графиком, поэтому обрезка ничего
 * не теряет. Коробка неизвестна (панель вкладки скрыта) — бюджета нет и обрезки
 * нет: пересчитаем по resize, который шлёт переключатель вкладок.
 */
const MIN_BAR_SLOT_PX = 22;
const X_AXIS_MAX_SHARE = 0.32;
const LABEL_FONT_PX = 12;
const LABEL_ROTATION_DEG = 45;
const LABEL_RISE = Math.sin((LABEL_ROTATION_DEG * Math.PI) / 180);
const Y_AXIS_RESERVE_PX = 56;
const MIN_BARS = 4;
const ELLIPSIS = '…';

/**
 * Бюджет коробки: сколько баров и сколько пикселей подписи она держит.
 *
 * Коробку берём у {@link chartBox} — он мерит обёртку, а не канвас, и отдаёт
 * `null` вместо интринсика 300×150 или нулей скрытой панели. Бюджет без коробки
 * — это отсутствие бюджета: рисуем максимум и не обрезаем, пока настоящий размер
 * не приедет с resize.
 */
function labelBudget(canvas) {
  const box = chartBox(canvas);
  if (!box) return { bars: MAX_BARS, labelPx: Infinity };
  const slots = Math.floor(Math.max(0, box.width - Y_AXIS_RESERVE_PX) / MIN_BAR_SLOT_PX);
  return {
    bars: Math.max(MIN_BARS, Math.min(MAX_BARS, slots)),
    // Минус две строки: Chart.js добавляет к проекции подписи собственные
    // отступы сверху и снизу оси. Без этой поправки ось выходила за
    // объявленную долю (0.36 вместо 0.32) — бюджет, который врёт.
    labelPx: Math.round(Math.max(LABEL_FONT_PX * 3, (box.height * X_AXIS_MAX_SHARE - LABEL_FONT_PX * 2) / LABEL_RISE))
  };
}

function shortenToWidth(width, text, maxPx) {
  if (!Number.isFinite(maxPx) || width(text) <= maxPx) return text;
  let fits = 0;
  let rest = text.length;
  while (fits < rest) {
    const mid = Math.ceil((fits + rest) / 2);
    if (width(text.slice(0, mid) + ELLIPSIS) <= maxPx) fits = mid;
    else rest = mid - 1;
  }
  return fits > 0 ? text.slice(0, fits).trimEnd() + ELLIPSIS : ELLIPSIS;
}

/**
 * Ось X с записанным бюджетом. autoSkip выключен намеренно: он «решал» проблему
 * длинных подписей, выбрасывая каждую вторую, и бар оставался без имени молча.
 * Влезать теперь обязано всё, что нарисовано.
 */
function budgetedAxis(baseX, labels, budget, width) {
  return {
    ...baseX,
    ticks: {
      ...baseX.ticks,
      autoSkip: false,
      callback: (_value, index) => shortenToWidth(width, labels[index] ?? '', budget.labelPx)
    }
  };
}

/** Тултип возвращает полное имя темы — обрезка живёт только на оси. */
function fullNameTooltip(basePlugins, labels) {
  return {
    ...basePlugins,
    tooltip: {
      ...basePlugins.tooltip,
      callbacks: { title: (items) => labels[items[0]?.dataIndex] ?? '' }
    }
  };
}

/**
 * Табличный дубль графика. Не visually-hidden: canvas недоступен не только
 * скринридеру, но и тому, кто просто хочет прочитать точные числа.
 */
function renderChartTable(tableId, caption, headers, rows) {
  const table = document.getElementById(tableId);
  if (!table || !rows.length) return;
  const thead = '<thead><tr>'
    + headers.map((h) => `<th scope="col">${escapeHtml(h)}</th>`).join('')
    + '</tr></thead>';
  const tbody = '<tbody>'
    + rows.map((row) => '<tr>' + row.map((cell) => `<td>${escapeHtml(String(cell))}</td>`).join('') + '</tr>').join('')
    + '</tbody>';
  table.innerHTML = `<caption>${escapeHtml(caption)}</caption>${thead}${tbody}`;
  table.classList.remove('hidden', 'visually-hidden');
  // Таблица живёт под раскрытием (details) и по умолчанию свёрнута: три
  // представления одних чисел разом — конкуренция за внимание, а не доступность.
  // НО если график не отрисовался, таблица остаётся единственным носителем
  // данных, и прятать её за нажатием нельзя — раскрываем.
  const disclosure = table.closest('.chart-table-disclosure');
  if (disclosure && chartFailed(table.id)) disclosure.open = true;
  // Дубль наполняется ПОСЛЕ initScrollableTables (данные приходят из /api),
  // и до этого момента таблица была `hidden` → замер обёртки дал бы 0.
  // Переоцениваем её здесь, иначе скролл-подсказка появилась бы только
  // после первого ресайза окна.
  enhanceScrollableTables(table.parentElement);
}

/** Не отрисовался ли график, к которому относится эта таблица. */
const failedCharts = new Set();
function chartFailed(tableId) {
  return failedCharts.has(tableId);
}

function showFallback(canvas, fallback, message, tableId) {
  canvas?.classList.add('hidden');
  if (fallback) {
    fallback.classList.remove('hidden');
    fallback.textContent = message || fallback.textContent;
  }
  if (tableId) {
    failedCharts.add(tableId);
    const table = document.getElementById(tableId);
    table?.classList.remove('hidden', 'visually-hidden');
    if (table) enhanceScrollableTables(table.parentElement);
  }
}

/**
 * 1–3 темы: координатная плоскость на три бара — избыточная фигура. Показываем
 * карточки с progress bar, они читаются с одного взгляда.
 */
function renderCompactCards(topics, canvases, fallbacks) {
  canvases.forEach((canvas) => canvas?.classList.add('hidden'));
  const markup = '<div class="topic-compact-list">' + topics.map((t) => {
    const learned = t.learned || 0;
    const total = Math.max(1, t.total || 1);
    const attempts = (t.correct || 0) + (t.wrong || 0);
    const accuracy = attempts === 0 ? null : Math.round(((t.correct || 0) * 100) / attempts);
    const href = `/?topic=${encodeURIComponent(t.topic || '')}&amp;ordered=true`;
    return '<div class="topic-compact-card">'
      + `<a class="topic-compact-name" href="${href}">${escapeHtml(displayTopicName(t))}</a>`
      + `<div class="topic-compact-bar" aria-hidden="true"><span class="topic-compact-bar-fill" data-progress="${Math.round((learned / total) * 100)}"></span></div>`
      + `<span class="muted tiny">Освоено ${learned} из ${total}`
      + (accuracy == null ? '' : ` · точность ${accuracy}%`)
      + '</span>'
      + (t.topic ? `<a class="topic-compact-action" href="${href}">Тренировать</a>` : '')
      + '</div>';
  }).join('') + '</div>';

  fallbacks.forEach((fallback) => {
    if (!fallback) return;
    fallback.classList.remove('hidden');
    // Заголовок секции уже дан H2 карточки — второй раз его не печатаем.
    fallback.innerHTML = markup;
  });
  document.querySelectorAll('.topic-compact-bar-fill[data-progress]')
    .forEach((el) => setProgressValue(el, el.getAttribute('data-progress')));
}

function enrich(topics) {
  return topics.map((t) => {
    const correct = t.correct || 0;
    const attempts = correct + (t.wrong || 0);
    const learned = t.learned || 0;
    return {
      name: displayTopicName(t),
      learned,
      total: t.total || 1,
      attempts,
      accuracy: attempts === 0 ? 0 : Math.round((correct * 100) / attempts),
      activity: learned + attempts
    };
  });
}

function baseOptions(palette) {
  return {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom', labels: { boxWidth: 14, color: palette.text, font: { size: 12 } } },
      tooltip: { enabled: true }
    },
    scales: {
      x: {
        ticks: { maxRotation: LABEL_ROTATION_DEG, color: palette.text, font: { size: 12 } },
        grid: { display: false },
        border: { color: palette.grid }
      },
      y: {
        beginAtZero: true,
        ticks: { color: palette.text, font: { size: 12 } },
        grid: { color: palette.grid, drawTicks: false },
        border: { color: palette.grid }
      }
    }
  };
}

/**
 * Свод вместо стопки: на узкой ширине таблица детализации стартует свёрнутой.
 *
 * <p>Разметка отдаёт `<details open>` — без JS и на десктопе таблица развёрнута,
 * как и была. Скрипт только СВОРАЧИВАЕТ и только когда экран узкий: обратный
 * порядок («закрыто в разметке, открывает JS») оставил бы пользователя без
 * таблицы при выключенном JS.
 *
 * <p>Один раз при загрузке, без подписки на resize: состояние раскрытия — выбор
 * пользователя, и вырывать его из-под руки при повороте экрана нельзя.
 */
function collapseDetailOnNarrow() {
  const disclosure = document.getElementById('topic-detail-disclosure');
  if (!disclosure) return;
  if (window.matchMedia && window.matchMedia('(max-width: 768px)').matches) {
    disclosure.open = false;
  }
}

export function initTopicCharts() {
  collapseDetailOnNarrow();
  const progressCanvas = document.getElementById('topicProgressChart');
  const accuracyCanvas = document.getElementById('topicAccuracyChart');
  const progressFallback = document.getElementById('topicProgressChartFallback');
  const accuracyFallback = document.getElementById('topicAccuracyChartFallback');
  if (!progressCanvas && !accuracyCanvas) return;

  const topics = getTopicStats();

  // Канвасы сервер рендерит только когда темы есть, а сюда мы попадаем лишь при
  // непустом JSON-блоке. Значит пустой список тут = сбой разбора встроенного JSON.
  if (!topics.length) {
    showFallback(progressCanvas, progressFallback,
      'Не удалось загрузить данные графика. Используй таблицу ниже.', 'topicProgressChartTable');
    showFallback(accuracyCanvas, accuracyFallback,
      'Не удалось загрузить данные графика. Используй таблицу ниже.', 'topicAccuracyChartTable');
    return;
  }

  if (topics.length <= 3) {
    renderCompactCards(topics, [progressCanvas, accuracyCanvas], [progressFallback, accuracyFallback]);
    return;
  }

  const enriched = enrich(topics);
  // Прогресс: только темы, с которыми работали, топ по активности. Нетронутые темы
  // и есть основной шум — в график их не тащим.
  const progressRanked = enriched
    .filter((t) => t.activity > 0)
    .sort((a, b) => b.activity - a.activity);
  // Точность: только отвеченные, слабые первыми (это actionable). Тему без попыток
  // не показываем как 0% красным — это путало бы «не трогал» с «всё провалил».
  const accuracyRanked = enriched
    .filter((t) => t.attempts > 0)
    .sort((a, b) => a.accuracy - b.accuracy || b.attempts - a.attempts);

  let charts = [];
  let appliedKey = '';
  const budgetKey = () => {
    const p = labelBudget(progressCanvas);
    const a = labelBudget(accuracyCanvas);
    return `${p.bars}:${p.labelPx}/${a.bars}:${a.labelPx}`;
  };

  const render = () => {
    const palette = chartPalette();
    const animation = chartAnimation();
    const common = baseOptions(palette);
    const progressBudget = labelBudget(progressCanvas);
    const accuracyBudget = labelBudget(accuracyCanvas);
    // Запоминаем бюджет ДО постройки: с ним график и будет нарисован, а после
    // конструктора Chart.js коробка уже другая (см. onChartResize).
    appliedKey = `${progressBudget.bars}:${progressBudget.labelPx}/${accuracyBudget.bars}:${accuracyBudget.labelPx}`;
    // Сколько баров рисуем — решает коробка, а не длина списка.
    const progressData = progressRanked.slice(0, progressBudget.bars);
    const accuracyData = accuracyRanked.slice(0, accuracyBudget.bars);
    charts.forEach((c) => { try { c.destroy(); } catch { /* уже уничтожен */ } });
    charts = [];

    if (!chartsAvailable()) {
      showFallback(progressCanvas, progressFallback,
        'График прогресса недоступен в текущем окружении. Используй таблицу ниже.', 'topicProgressChartTable');
      showFallback(accuracyCanvas, accuracyFallback,
        'График точности недоступен в текущем окружении. Используй таблицу ниже.', 'topicAccuracyChartTable');
      return;
    }

    if (progressCanvas && progressData.length) {
      progressCanvas.classList.remove('hidden');
      progressFallback?.classList.add('hidden');
      renderChartTable('topicProgressChartTable', 'Табличное представление прогресса по темам',
        ['Тема', 'Освоено %', 'Выучено', 'Осталось'],
        progressData.map((t) => [t.name, Math.round((t.learned / Math.max(1, t.total)) * 100),
          t.learned, Math.max(0, t.total - t.learned)]));
      try {
        const names = progressData.map((t) => t.name);
        const measure = labelMeasurer(progressCanvas, LABEL_FONT_PX);
        // VIS-01 (REF-005): стек «Выучено + Осталось» не показывал прогресс.
        // Замер на живых данных: learned 0–4 против total до 54 — сегмент
        // прогресса занимал ≤7% высоты бара и на большинстве тем был
        // невидим; график отвечал «сколько осталось», а не «как я иду».
        // Одна серия «Освоено %» делает сравнимой именно долю освоения;
        // абсолютные объёмы никуда не делись — тултип говорит «X из N»,
        // таблица-дубль несёт и %, и оба числа.
        const shares = progressData.map((t) => Math.round((t.learned / Math.max(1, t.total)) * 100));
        charts.push(new Chart(progressCanvas, {
          type: 'bar',
          data: {
            labels: names,
            datasets: [
              { label: 'Освоено %', data: shares, backgroundColor: palette.learned, borderRadius: 3 }
            ]
          },
          options: {
            ...common,
            animation,
            plugins: {
              ...fullNameTooltip(common.plugins, names),
              tooltip: {
                ...common.plugins.tooltip,
                callbacks: {
                  title: (items) => names[items[0]?.dataIndex] ?? '',
                  label: (item) => {
                    const t = progressData[item.dataIndex];
                    return `Освоено ${t.learned} из ${t.total} (${shares[item.dataIndex]}%)`;
                  }
                }
              }
            },
            scales: {
              x: budgetedAxis(common.scales.x, names, progressBudget, measure),
              y: { ...common.scales.y, max: 100, ticks: { ...common.scales.y.ticks, callback: (v) => `${v}%` } }
            }
          }
        }));
      } catch {
        showFallback(progressCanvas, progressFallback,
          'Не удалось отрисовать график прогресса. Используй таблицу ниже.', 'topicProgressChartTable');
      }
    } else if (progressCanvas) {
      showFallback(progressCanvas, progressFallback,
        'Пока нет активных тем. Начни отвечать — и здесь появится твой прогресс.');
    }

    if (accuracyCanvas && accuracyData.length) {
      accuracyCanvas.classList.remove('hidden');
      accuracyFallback?.classList.add('hidden');
      renderChartTable('topicAccuracyChartTable', 'Табличное представление точности по темам',
        ['Тема', 'Точность %', 'Попыток'],
        accuracyData.map((t) => [t.name, t.accuracy, t.attempts]));
      try {
        const values = accuracyData.map((t) => t.accuracy);
        const names = accuracyData.map((t) => t.name);
        const measure = labelMeasurer(accuracyCanvas, LABEL_FONT_PX);
        charts.push(new Chart(accuracyCanvas, {
          type: 'bar',
          data: {
            labels: names,
            datasets: [{
              label: 'Точность %',
              data: values,
              backgroundColor: values.map((v) => (v >= 80 ? palette.accHigh : v >= 50 ? palette.accMid : palette.accLow)),
              borderRadius: 3
            }]
          },
          options: {
            ...common,
            animation,
            plugins: fullNameTooltip(common.plugins, names),
            scales: {
              x: budgetedAxis(common.scales.x, names, accuracyBudget, measure),
              y: { ...common.scales.y, max: 100, ticks: { ...common.scales.y.ticks, callback: (v) => `${v}%` } }
            }
          }
        }));
      } catch {
        showFallback(accuracyCanvas, accuracyFallback,
          'Не удалось отрисовать график точности. Используй таблицу ниже.', 'topicAccuracyChartTable');
      }
    } else if (accuracyCanvas) {
      showFallback(accuracyCanvas, accuracyFallback,
        'Пока нет отвеченных вопросов. Ответь на несколько — и увидишь точность по темам.');
    }
  };

  render();
  onChartThemeChange(render);

  // Бюджет выведен из коробки — значит, при смене коробки он протухает. Chart.js
  // сам подстроит масштаб, но число баров и длину обрезки решаем мы, и повороту
  // экрана нечем нам об этом сообщить. Безусловная перерисовка тут не годится:
  // переключатель вкладок шлёт resize на каждый клик, и анимация входа гонялась
  // бы на ровном месте — поэтому сверяем бюджеты.
  onChartResize({
    current: budgetKey,
    applied: () => appliedKey,
    redraw: render,
    boxes: [progressCanvas?.parentElement, accuracyCanvas?.parentElement]
  });
}
