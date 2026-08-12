import { apiFetch } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { setProgressValue, pluralRu } from '../core/dom.js';

let streakBound = false;

export function initStreakBar() {
  if (streakBound) return;
  const bars = document.querySelectorAll('[data-streak-bar]');
  if (!bars.length) return;
  streakBound = true;

  const daysText = (d) => (typeof d.streakLabel === 'string' && d.streakLabel)
    ? d.streakLabel
    : d.streak + ' ' + pluralRu(d.streak, 'день', 'дня', 'дней');

  apiFetch(API.STREAK)
    .then((r) => r.json())
    .then((d) => {
      if (!d || typeof d.streak !== 'number' || typeof d.goal !== 'number' || typeof d.today !== 'number') return;
      const pct = d.goal > 0 ? Math.min(100, (d.today / d.goal) * 100) : 0;
      bars.forEach((bar) => {
        const daysEl = bar.querySelector('[data-streak-days]');
        const fillEl = bar.querySelector('[data-streak-fill]');
        const countEl = bar.querySelector('[data-streak-count]');
        const fireEl = bar.querySelector('.streak-fire, .px-icon');
        const hasGoalProgress = !!fillEl || !!countEl;
        if (d.streak > 0) {
          if (daysEl) { daysEl.textContent = daysText(d); daysEl.classList.remove('hidden'); }
          if (fireEl) fireEl.classList.remove('hidden');
        } else {
          if (daysEl) daysEl.classList.add('hidden');
          if (fireEl) fireEl.classList.add('hidden');
        }
        if (fillEl) setProgressValue(fillEl, pct);
        if (countEl) {
          countEl.textContent = 'Дневная цель — ' + d.today + ' из ' + d.goal;
          countEl.title = 'Дневная цель ответов (не прогресс банка карточек)';
        }
        if (d.streak > 0 || hasGoalProgress) bar.classList.remove('hidden');
        if (d.goalReached) bar.classList.add('streak-goal-reached');
      });
    })
    .catch(() => {});
}
