const { test, expect } = require('@playwright/test');

/**
 * Каждый woff2 качается ровно один раз — в обоих движках.
 *
 * Инцидент iter-178: для same-origin шрифтов Chromium и WebKit зеркально
 * расходятся в матчинге preload и @font-face-загрузки (Chromium требует
 * crossorigin, WebKit от него двоит: 6 против 12 сетевых запросов), поэтому
 * preload-линки создаёт head-скрипт с UA-ветвлением crossOrigin. Спек держит
 * оба конца класса «двойная загрузка»: рассинхрон crossorigin (каждый файл
 * дважды) и мёртвый preload (преднагруженный путь без реального запроса —
 * значит настоящий файл пришёл отдельно, седьмым). Контентные субсеты сверх
 * шести преднагруженных легитимны — жёсткого счёта нет, только «без дублей».
 * Статический двойник — FontPreloadContractTest (JVM).
 */
test.describe('font network', () => {
  test('every woff2 is fetched exactly once', async ({ page }) => {
    const requests = [];
    page.on('request', (req) => {
      if (req.url().includes('/fonts/')) requests.push(new URL(req.url()).pathname);
    });
    await page.goto('/', { waitUntil: 'networkidle' });

    const counts = {};
    for (const path of requests) counts[path] = (counts[path] || 0) + 1;
    const doubled = Object.entries(counts).filter(([, n]) => n > 1);
    expect(doubled, `двойная загрузка шрифтов: ${JSON.stringify(doubled)}`).toHaveLength(0);

    const preloaded = await page.evaluate(() =>
      [...document.head.querySelectorAll('link[rel="preload"][as="font"]')]
        .map((l) => new URL(l.href).pathname));
    expect(preloaded, 'head-скрипт создал шесть preload-линков').toHaveLength(6);
    for (const path of preloaded) {
      expect(counts[path], `преднагруженный ${path} так и не запрошен сетью`).toBe(1);
    }
  });
});
