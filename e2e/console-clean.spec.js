const { test, expect } = require('@playwright/test');

/**
 * Консоль и сеть чисты на каждой поверхности — в обоих движках (iter-184).
 *
 * До этого спека JS-ошибка в проде не ловилась ничем, кроме глаз: клавиатурный
 * e2e замечает только поломку сценария, визуальный — только поломку пикселей,
 * а тихий pageerror (сломанный модуль, битый импорт, ошибка в обработчике)
 * проходил все сетки молча. Замер iter-184: восемь поверхностей чисты; спек
 * держит достигнутое.
 *
 * Единственное допущение — 404-статус САМОГО документа /nope-404: браузер
 * логирует его как console.error, и это правильное поведение страницы ошибки,
 * а не дефект. Любой другой console.error/warning, pageerror или ответ ≥400
 * (включая битую статику на самой странице 404) — красный.
 */
const SURFACES = ['/', '/practice', '/train', '/stats', '/topics', '/bookmarks', '/settings', '/review', '/nope-404'];

test.describe('console clean', () => {
  for (const path of SURFACES) {
    test(`no console errors or broken requests: ${path}`, async ({ page }) => {
      const issues = [];
      page.on('console', (msg) => {
        if (!['error', 'warning'].includes(msg.type())) return;
        if (path === '/nope-404' && msg.text().includes('404')) return;
        issues.push(`console.${msg.type()}: ${msg.text().slice(0, 160)}`);
      });
      page.on('pageerror', (e) => issues.push('pageerror: ' + String(e).slice(0, 160)));
      page.on('response', (r) => {
        if (r.status() < 400) return;
        if (path === '/nope-404' && new URL(r.url()).pathname === '/nope-404') return;
        issues.push(`http ${r.status()}: ${new URL(r.url()).pathname}`);
      });
      await page.goto(path, { waitUntil: 'networkidle' });
      expect(issues, `грязная консоль/сеть на ${path}`).toHaveLength(0);
    });
  }
});
