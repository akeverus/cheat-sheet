import { initTablist } from '../ui/tablist.js';
import { initTopicCharts } from './topic-charts.js';
import { initDifficultyDonut } from './difficulty-donut.js';
import { initAccuracyTrend } from './accuracy-trend.js';
import { initForecastDates } from './forecast-dates.js';

/**
 * Вкладки аналитики: Темы / Динамика / Ошибки.
 *
 * Порядок «после графиков» здесь исторический и с iter-80 уже ничего не значит:
 * панели «Динамика» и «Ошибки» скрыты CSS ещё до первого кадра, поэтому Chart.js
 * в любом случае строит их канвасы нулевого размера. Пересчёт делает resize из
 * onActivate — он же и раньше был единственным, что спасало график в скрытой
 * панели. Зато исчезла цена прежней схемы: графики трёх панелей больше не
 * рисуются в раскладку высотой 21734px, которой через 149 мс не будет.
 *
 * defaultHash — вкладка, которая в URL не пишется. С iter-61 это «Темы»:
 * вкладки «Обзор» больше нет, её единственная карточка поднята на уровень
 * страницы. Сохранённый у прежних посетителей 'stats-tab-overview' безопасен —
 * findIndex вернёт -1, и initTablist откатится на первую вкладку.
 */
function initStatsTabs() {
  initTablist(document.getElementById('stats-tablist'), {
    storageKey: 'statsTab',
    syncHash: true,
    defaultHash: 'topics',
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
  initStatsTabs();
}
