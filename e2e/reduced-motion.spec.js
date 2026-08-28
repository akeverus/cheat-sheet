const { test, expect } = require('@playwright/test');

/**
 * prefers-reduced-motion реально гасит ВСЁ декоративное движение (инвариант 4).
 *
 * Замер iter-190: без reduce на / и /train живёт декоративная arrowNudge;
 * под reduce — ноль работающих анимаций на всех семи поверхностях в обоих
 * движках. Спек держит достигнутое: любая анимация или транзишен, оставшиеся
 * работать под reduce (забытый media-гейт у нового keyframes), — красный.
 * Функциональное движение, живущее по действию пользователя (секундомер
 * интервью), стартует после загрузки и сюда не попадает.
 *
 * Эмуляция — через page.emulateMedia() ДО перехода, а не через
 * test.use({ reducedMotion }): на Playwright 1.61 контекст-опция молча не
 * применялась (matchMedia в странице возвращал false), и спек проверял
 * страницы БЕЗ reduce — то есть не проверял ничего. emulateMedia бьёт в тот
 * же CDP-механизм напрямую и проверяется контрольным matchMedia в странице.
 */
const SURFACES = ['/', '/train', '/stats', '/topics', '/bookmarks', '/settings', '/nope-404'];

test.describe('reduced motion', () => {
  for (const path of SURFACES) {
    test(`no running animations under reduce: ${path}`, async ({ page }) => {
      await page.emulateMedia({ reducedMotion: 'reduce' });
      await page.goto(path, { waitUntil: 'networkidle' });
      const applied = await page.evaluate(
        () => matchMedia('(prefers-reduced-motion: reduce)').matches
      );
      expect(applied, 'эмуляция reduce не применилась — спек слеп').toBe(true);
      const running = await page.evaluate(() =>
        document.getAnimations()
          .filter((a) => a.playState === 'running')
          .map((a) => a.animationName || 'transition')
      );
      expect(running, `движение пережило reduce на ${path}`).toHaveLength(0);
    });
  }
});
