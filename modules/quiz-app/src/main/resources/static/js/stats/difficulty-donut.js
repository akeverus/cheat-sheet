import { chartsAvailable, readJsonBlock, liveChart } from './chart-theme.js';

/**
 * Цвет сектора берётся с кружка легенды, а не из своей таблицы «уровень → токен».
 *
 * <p>Такая таблица была третьей копией уровней сложности: имена констант в ключах
 * JS, те же имена в CSS-классах легенды (`difficulty-easy` и т.д.) и само
 * перечисление на сервере. Совпадали они только на честном слове — донат и
 * кружок рядом с ним могли разъехаться в цвете, и заметить это можно было лишь
 * глазами. Теперь цвет ровно один: тот, который CSS уже нарисовал пользователю.
 * Смена темы пересобирает график через liveChart, и computed-цвет читается заново.
 */
function legendColor(level) {
  const dot = document.querySelector('.difficulty-' + String(level).toLowerCase() + ' .difficulty-dot');
  return dot ? getComputedStyle(dot).backgroundColor : null;
}

/**
 * Донат «Сложность вопросов».
 *
 * Легенда рядом — серверная и самодостаточная, поэтому у доната нет своей:
 * канвас здесь визуальное дополнение, а не единственный носитель данных.
 */
export function initDifficultyDonut() {
  const canvas = document.getElementById('difficultyDonut');
  if (!canvas || !chartsAvailable()) return;

  const slices = readJsonBlock('difficulty-data');
  if (!slices.length || !slices.some((s) => s.count > 0)) return;

  liveChart((palette, animation) => {
    return new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: slices.map((s) => s.label),
        datasets: [{
          data: slices.map((s) => s.count),
          backgroundColor: slices.map((s) => legendColor(s.level) || palette.remaining),
          borderColor: palette.surface,
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '62%',
        animation,
        plugins: {
          legend: { display: false },
          tooltip: { callbacks: { label: (ctx) => `${ctx.label}: ${ctx.parsed}` } }
        }
      }
    });
  });
}
