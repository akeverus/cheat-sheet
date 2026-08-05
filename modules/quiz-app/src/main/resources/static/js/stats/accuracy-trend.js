import { chartsAvailable, readJsonBlock, liveChart, chartBox, labelMeasurer } from './chart-theme.js';

/** `2026-07-31` → `31.07`: в 14-дневном окне год и так очевиден. */
function dayLabel(day) {
  const match = /-(\d{2})-(\d{2})$/.exec(day || '');
  return match ? `${match[2]}.${match[1]}` : (day || '');
}

/**
 * Бюджет подписей оси дат.
 *
 * Раньше их число было константой `maxTicksLimit: 7`, не связанной ни с шириной
 * графика, ни с шириной подписи. Замер показал обе стороны ошибки сразу: на 1440
 * ось выбрасывала семь дат из четырнадцати, имея 981px и нуждаясь в 46px на
 * подпись, а на 390 держала те же семь с зазором 5.4px до столкновения — один
 * шаг шрифта от наложения.
 *
 * Теперь предел считается из коробки. Подписи здесь равной ширины (`31.07`), но
 * `dayLabel` при неожиданном формате даты отдаёт строку как есть, поэтому меряем
 * самую широкую фактическую, а не образец.
 *
 * `autoSkip` остаётся включённым — в отличие от оси тем (`topic-charts.js`), где
 * он молча съедал имена. Разница не в механизме, а в цене: пропущенная дата на
 * равномерной 14-дневной сетке восстанавливается взглядом, столбик и тултип
 * остаются на месте, а таблица рядом хранит все дни. Пропущенное имя темы не
 * восстанавливается ничем. Поэтому там промах сделан видимым, а здесь autoSkip
 * оставлен страховкой под записанным бюджетом.
 */
const MIN_LABEL_GAP_PX = 16;
const Y_AXIS_RESERVE_PX = 46;
const MIN_TICKS = 2;

function tickBudget(canvas, labels) {
  const box = chartBox(canvas);
  // Коробки нет (панель скрыта) — ограничение снимаем: пусть решает autoSkip,
  // а настоящий предел приедет с onChartResize при показе вкладки.
  if (!box) return undefined;
  const width = labelMeasurer(canvas);
  const widest = labels.reduce((max, label) => Math.max(max, width(label)), 0);
  const plot = Math.max(0, box.width - Y_AXIS_RESERVE_PX);
  const fits = Math.floor(plot / (widest + MIN_LABEL_GAP_PX));
  return Math.max(MIN_TICKS, Math.min(labels.length, fits));
}

/**
 * Тренд точности за 14 дней.
 *
 * У блока два серверных дубля: таблица (a11y) и столбики. Столбики — визуальный
 * fallback, поэтому при удачной отрисовке канваса их прячем: иначе те же данные
 * стоят рядом дважды, с разными осями (даты против номеров дней).
 */
export function initAccuracyTrend() {
  const canvas = document.getElementById('accuracyTrendChart');
  if (!canvas || !chartsAvailable()) return;

  const days = readJsonBlock('accuracy-trend-data');
  if (!days.length) return;

  const labels = days.map((d) => dayLabel(d.day));

  liveChart((palette, animation) => {
    const chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Точность %',
          data: days.map((d) => (d.accuracyPercent == null ? null : d.accuracyPercent)),
          backgroundColor: palette.accent,
          borderRadius: 2,
          maxBarThickness: 18
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation,
        scales: {
          y: {
            beginAtZero: true,
            max: 100,
            ticks: { color: palette.text, callback: (v) => `${v}%` },
            grid: { color: palette.grid }
          },
          x: {
            grid: { display: false },
            ticks: { color: palette.text, maxRotation: 0, autoSkip: true, maxTicksLimit: tickBudget(canvas, labels) }
          }
        },
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                const day = days[ctx.dataIndex];
                if (!day || day.answered === 0) return 'нет ответов';
                return `${Math.round(ctx.parsed.y)}% · ${day.correct}/${day.answered}`;
              }
            }
          }
        }
      }
    });
    const bars = document.querySelector('.accuracy-trend-bars');
    if (bars) bars.hidden = true;
    canvas.closest('.accuracy-trend-row')?.classList.add('is-chart-only');
    return chart;
  }, () => String(tickBudget(canvas, labels)), [canvas.parentElement]);
}
