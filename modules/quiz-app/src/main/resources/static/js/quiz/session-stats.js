import { apiGet } from '../core/api-client.js';
import { API } from '../core/constants.js';
import { setProgressValue } from '../core/dom.js';

function writeStatField(field, value, digits = 0) {
  if (!Number.isFinite(value)) return;
  const nodes = document.querySelectorAll('[data-stat-field="' + field + '"]');
  if (!nodes.length) return;
  const rendered = digits > 0 ? value.toFixed(digits) : String(Math.round(value));
  nodes.forEach((node) => { node.textContent = rendered; });
}

function applyStatsPayload(stats) {
  if (!stats || typeof stats !== 'object') return;
  writeStatField('total', Number(stats.total || 0));
  writeStatField('due', Number(stats.due || 0));
  writeStatField('fresh', Number(stats.fresh || 0));
  // Очередь состоит из повторений и вопросов, которые ни разу не открывали.
  // Считаем остаток здесь, а не отдельным полем API: сервер уже прислал оба
  // слагаемых, и разность не может разойтись с ними по времени.
  writeStatField('repeats', Math.max(0, Number(stats.due || 0) - Number(stats.fresh || 0)));
  writeStatField('learned', Number(stats.learned || 0));
  writeStatField('correct', Number(stats.correct || 0));
  writeStatField('wrong', Number(stats.wrong || 0));
  const attempts = Number(stats.correct || 0) + Number(stats.wrong || 0);
  const accuracy = attempts > 0 ? (Number(stats.correct || 0) * 100) / attempts : 0;
  writeStatField('accuracy', accuracy, 1);
  const barFill = document.getElementById('sidebar-accuracy-bar-fill');
  if (barFill) setProgressValue(barFill, accuracy);
}

/**
 * Обновляет сайдбар-статистику. Параметры порядка (shuffle/ordered) не шлём:
 * на агрегат они не влияют, а раньше shuffle=true заставлял сервер считать
 * статистику по всему банку вопросов вместо выбранной темы.
 */
export function refreshStatsFromServer(form) {
  if (!form) return Promise.resolve();
  const topic = form.querySelector('input[name="topic"]')?.value || '';
  const group = form.querySelector('input[name="group"]')?.value || '';
  const important = form.querySelector('input[name="important"]')?.value === 'true';
  const onlyWrong = form.querySelector('input[name="onlyWrong"]')?.value === 'true';

  const params = new URLSearchParams();
  if (topic) params.set('topic', topic);
  if (group) params.set('group', group);
  if (important) params.set('important', 'true');
  if (onlyWrong) params.set('onlyWrong', 'true');

  const query = params.toString();
  const url = query ? (API.STATS + '?' + query) : API.STATS;
  return apiGet(url)
    .then((stats) => applyStatsPayload(stats))
    .catch((err) => console.warn('Stats refresh failed:', err));
}

export function updateSessionProgress(data) {
  const progressSpans = document.querySelectorAll('.session-progress');
  if (!progressSpans.length) return;
  if (!data?.session) {
    const fallbackIndex = progressSpans[0]?.dataset.field === 'index'
      ? Number.parseInt(progressSpans[0].textContent || '0', 10)
      : 0;
    const fallbackTotal = Array.from(progressSpans).find((span) => span.dataset.field === 'total');
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

  progressSpans.forEach((span) => {
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
