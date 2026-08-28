const { test, expect } = require('@playwright/test');

/**
 * Chart.js оплачивается просмотром, а не существованием графиков.
 *
 * iter-179: жадный `<script defer>` с chart.umd (~200 КБ) грузился из head
 * /stats при канвасах на 51–62% документа — вне первого экрана. Теперь
 * библиотеку подгружает stats/chart-loader.js по видимости первого канваса
 * (как mermaid). Спек держит оба конца в обоих движках: наверху страницы
 * библиотека не запрошена; после скролла к канвасу — приезжает и рисует.
 */
test.describe('chart lazy load', () => {
  test('chart.umd arrives only when a canvas becomes visible', async ({ page }) => {
    const umdRequests = [];
    page.on('request', (req) => {
      if (req.url().includes('chart.umd')) umdRequests.push(req.url());
    });

    await page.goto('/stats', { waitUntil: 'networkidle' });
    expect(umdRequests, 'библиотека снова жадная: качается до показа графика')
      .toHaveLength(0);

    const canvas = page.locator('#topicProgressChart, #accuracyTrendChart').first();
    await canvas.scrollIntoViewIfNeeded();
    await expect
      .poll(() => page.evaluate(() => typeof window.Chart !== 'undefined'),
        { message: 'канвас в виде, а Chart так и не приехал', timeout: 10000 })
      .toBe(true);
    expect(umdRequests, 'после показа канваса библиотека обязана приехать')
      .toHaveLength(1);

    await expect
      .poll(() => canvas.evaluate((el) => el.width > 0 && !el.classList.contains('hidden')),
        { message: 'библиотека приехала, но график не отрисован', timeout: 10000 })
      .toBe(true);
  });
});
