/**
 * Chart.js приезжает, когда канвас реально попадает в вид, — как mermaid.
 *
 * Замер (iter-179): chart.umd.min.js (~200 КБ) грузился жадным
 * `<script defer>` из head на каждом заходе /stats, хотя все четыре канваса
 * лежат на 51–62% документа, вне первого экрана: библиотека оплачивалась
 * до того, как её кто-то мог увидеть. (Попутная гипотеза «defer держит
 * стрессовый LCP» опровергнута контрольным замером: LCP не сдвинулся без
 * единого запроса библиотеки — поздняя LCP-запись оказалась re-paint'ом
 * от прибытия woff2; разбор в FRD-231.)
 *
 * Путь к UMD отдаёт meta name="chart-src" (как mermaid-src): @{...} проходит
 * ResourceUrlProvider и в prod несёт content-hash — знает его только шаблон.
 */

let chartLoad = null;

export function ensureChart() {
  if (typeof Chart !== 'undefined') return Promise.resolve(true);
  if (chartLoad) return chartLoad;

  const meta = document.querySelector('meta[name="chart-src"]');
  const src = meta && meta.getAttribute('content');
  if (!src) return Promise.resolve(false);

  chartLoad = new Promise((resolve) => {
    const script = document.createElement('script');
    script.src = src;
    script.onload = () => resolve(typeof Chart !== 'undefined');
    script.onerror = () => resolve(false);
    document.head.appendChild(script);
  });
  return chartLoad;
}

/**
 * Первый видимый канвас запускает загрузку и единый `start()`.
 *
 * Панели вкладок и мобильные раскрытия скрыты (`display: none`) — их канвасы
 * дают нулевой прямоугольник, и IntersectionObserver молчит до открытия:
 * ровно как у mermaid, 200 КБ платятся за просмотренный график.
 *
 * `start()` зовётся и когда библиотека НЕ доехала (офлайн, блокировщик):
 * модули графиков сами разбираются через chartsAvailable() и показывают
 * таблицы-фоллбэки — этот контракт не меняется.
 */
export function whenChartNeeded(canvasIds, start) {
  const els = canvasIds.map((id) => document.getElementById(id)).filter(Boolean);
  if (!els.length) return;

  let fired = false;
  let observer = null;
  const fire = () => {
    if (fired) return;
    fired = true;
    observer?.disconnect();
    ensureChart().then(() => start());
  };

  // Переход по якорю может миновать канвас. Запрос таблицы также запускает данные.
  document.querySelectorAll('.chart-table-disclosure').forEach((disclosure) => {
    disclosure.addEventListener('toggle', () => { if (disclosure.open) fire(); });
    if (disclosure.open) fire();
  });
  if (fired) return;

  if (typeof IntersectionObserver === 'undefined') {
    fire();
    return;
  }
  observer = new IntersectionObserver((entries) => {
    if (entries.some((e) => e.isIntersecting)) {
      observer.disconnect();
      fire();
    }
  }, { rootMargin: '200px' });
  els.forEach((el) => observer.observe(el));
}
