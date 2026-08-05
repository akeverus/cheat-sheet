import { initTablist } from '../ui/tablist.js';
import { initTopicCharts } from './topic-charts.js';
import { initDifficultyDonut } from './difficulty-donut.js';
import { initAccuracyTrend } from './accuracy-trend.js';
import { initTableSort } from './table-sort.js';
import { initForecastDates } from './forecast-dates.js';

/**
 * Вкладки аналитики: Обзор / Темы / Динамика / Ошибки.
 *
 * Инициализировать ПОСЛЕ графиков: Chart.js меряет канвас при создании, а до
 * первого переключения панель «Темы» ещё видима. При переключении шлём resize,
 * чтобы график, отрисованный в скрытой панели, пересчитался под реальный размер.
 */
function initStatsTabs() {
  initTablist(document.getElementById('stats-tablist'), {
    storageKey: 'statsTab',
    syncHash: true,
    defaultHash: 'overview',
    reveal() {
      document.body.classList.add('js-tabs');
      document.getElementById('stats-tablist').classList.remove('hidden');
    },
    onActivate() {
      window.dispatchEvent(new Event('resize'));
    }
  });
}

export function initStatsPage() {
  initTopicCharts();
  initDifficultyDonut();
  initAccuracyTrend();
  initForecastDates();
  initTableSort();
  initStatsTabs();
}
