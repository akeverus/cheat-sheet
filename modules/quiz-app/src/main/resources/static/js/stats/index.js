import { initTablist } from '../ui/tablist.js';
import { initTopicCharts } from './topic-charts.js';
import { initDifficultyDonut } from './difficulty-donut.js';
import { initAccuracyTrend } from './accuracy-trend.js';
import { initForecastDates } from './forecast-dates.js';
import { initMobileDisclosures } from '../ui/mobile-disclosures.js';

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
  // До 1199, а не до 767 (iter-158). Планшетная щель 768–1199 давала вкладкам
  // разделов 1326–1375-й пиксель при сгибах 768–1194: стойка «где я» (585–700px),
  // витрина и отбор не сворачивались, хотя довод «статус смотрят мельком, разделами
  // работают» на планшете тот же, а шапки всех трёх раскрытий несут факты и в
  // свёрнутом виде. Попытка вместо этого дать стойке планшетный ряд «ранг | метрики
  // столбиком» ОТОЗВАНА замером: плитка метрики в колонке 340px оказалась ~165px
  // высотой, и стойка выросла с 684 до 714. Замер после смены порога: вкладки
  // 690/672/709 при сгибах 1024/768/1194 — в первом экране на всех трёх.
  initMobileDisclosures('(max-width: 1199px)');
}
