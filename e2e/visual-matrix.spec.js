const { test, expect } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

/**
 * TEST-005 — визуальная матрица theme × screen-preset × motion.
 * Скриншоты → e2e/visual-results/ (gitignored).
 * Golden: VISUAL_GOLDEN=1 сравнивает с e2e/visual-baselines/
 *   (обновить: e2e/scripts/run-visual-goldens.sh --update
 *    или VISUAL_GOLDEN=1 npx playwright test visual-matrix --update-snapshots).
 * CI: .github/workflows/visual-goldens.yml (workflow_dispatch).
 *
 * Ось «пиксельность» снята вместе с pixel-слоем (FOLIO, миссия 2026-08-29):
 * пресет экрана теперь несёт две оси — плотность и меру чтения.
 */
const OUT_DIR = path.join(__dirname, 'visual-results');

/* Матрица стережёт ДИЗАЙН, а не содержимое банка. Голдены падали от живого
   состояния: клавиатурный спек отвечает на вопрос, очередь сдвигается, и на
   home рендерится другой вопрос (найдено webkit-прогоном iter-164: дифф 7% —
   целиком текст вопроса/вариантов и счётчики). Всё, что меняется от
   ИСПОЛЬЗОВАНИЯ приложения, маскируется; каркас, навигация, карточки,
   контролы и типографика статичных подписей остаются под охраной пикселей. */
/* Home рендерит ТЕКУЩИЙ вопрос очереди, а его двигает любое использование —
   маскировать мало: другой вопрос меняет ГЕОМЕТРИЮ (высота заголовка, код-блок).
   Пин: ЯВНЫЙ questionId (GET / его поддерживает, вопрос детерминирован
   независимо от SRS-истории). После реимпорта банка id может уехать — тогда
   тест падает ГРОМКО (другой вопрос = другой голден), и константу обновляют
   осознанно (REF-022). */
const PINNED_HOME =
  '/?questionId=2060&topic='
  + encodeURIComponent('algorithms/algorithmic-paradigms/backtracking-interview')
  + '&ordered=true&shuffle=false';

const DYNAMIC_CONTENT = [
  '.focus-question', '#interview-options', '.topic-brief', '.question-code',
  '.focus-bank-progress', '.today-chip', '.focus-aside .btn',
  '.stats-next-actions-grid', '.dashboard-grid', '.stats-standing', '.metrics-legend',
  '.builder-outcome', '.filter-chip-count', '.session-preview',
  '.page-head-lead',
];

/* Маска лечит ЗНАЧЕНИЯ, но не ПРИСУТСТВИЕ: блок, который то есть, то нет
   (причина повторения зависит от истории ответов, баннер — от незавершённой
   сессии), сдвигает всю геометрию ниже на свою высоту, и снимок краснеет
   вокруг масок. Такие блоки на время снимка скрываются целиком — их пиксели
   матрица честно НЕ стережёт (поведение причин закрыто контрактами разметки,
   баннера — клавиатурным спеком). */
const VOLATILE_PRESENCE = ['.focus-question-reason', '.resume-banner', '.notice', '.today-chip'];
const stabilizePresence = (page) =>
  page.addStyleTag({ content: VOLATILE_PRESENCE.join(', ') + ' { display: none !important; }' });
const GOLDEN = process.env.VISUAL_GOLDEN === '1';

/** @type {Array<{ id: string, path: string, theme: string, preset: string, motion: string }>} */
const MATRIX = [
  { id: 'home-dark-laptop-auto', path: PINNED_HOME, theme: 'dark', preset: 'laptop', motion: 'auto' },
  { id: 'home-light-laptop-on', path: PINNED_HOME, theme: 'light', preset: 'laptop', motion: 'on' },
  { id: 'home-dark-phone-off', path: PINNED_HOME, theme: 'dark', preset: 'phone', motion: 'off' },
  { id: 'home-dark-wide-on', path: PINNED_HOME, theme: 'dark', preset: 'wide', motion: 'on' },
  { id: 'home-light-wide-off', path: PINNED_HOME, theme: 'light', preset: 'wide', motion: 'off' },
  // /train — конструктор сессии (собственная поверхность с b0a45a6f, UXD-001):
  // ключевая страница цикла, обязана жить в матрице. Волатильные счётчики
  // набора и стрика — в масках ниже.
  { id: 'train-dark-laptop-auto', path: '/train', theme: 'dark', preset: 'laptop', motion: 'auto' },
  { id: 'train-light-laptop-on', path: '/train', theme: 'light', preset: 'laptop', motion: 'on' },
  { id: 'settings-dark-laptop-off', path: '/settings', theme: 'dark', preset: 'laptop', motion: 'off' },
  { id: 'settings-light-laptop-on', path: '/settings', theme: 'light', preset: 'laptop', motion: 'on' },
  { id: 'stats-dark-laptop-off', path: '/stats', theme: 'dark', preset: 'laptop', motion: 'off' },
  { id: 'stats-light-phone-off', path: '/stats', theme: 'light', preset: 'phone', motion: 'off' },
];

/** Extra viewports for Wave 8 responsive smoke (subset of full matrix). */
const VIEWPORTS = [
  { id: 'vp-375', width: 375, height: 812 },
  { id: 'vp-768', width: 768, height: 1024 },
  { id: 'vp-1280', width: 1280, height: 800 },
];

/* Пресет экрана раскладывается в атрибуты раскладки до первого кадра
   (head.html): phone → data-density="compact", wide → data-reading-width="wide",
   laptop — без атрибутов. Матрица проверяет живой контракт, а не localStorage. */
const PRESET_ATTRS = {
  phone:  { density: 'compact', readingWidth: null },
  laptop: { density: null, readingWidth: null },
  wide:   { density: null, readingWidth: 'wide' },
};

function applyPrefsScript({ theme, preset, motion }) {
  try {
    if (theme) localStorage.setItem('theme', theme);
    else localStorage.removeItem('theme');
    if (preset && preset !== 'laptop') localStorage.setItem('screenPreset', preset);
    else localStorage.removeItem('screenPreset');
    localStorage.removeItem('pixelIntensity');
    if (motion && motion !== 'auto') localStorage.setItem('motion', motion);
    else localStorage.removeItem('motion');
  } catch (_) { /* private mode */ }
}

function expectedResolvedTheme(theme) {
  return theme === 'auto' ? 'dark' : theme;
}

test.describe('visual preference matrix', () => {
  test.beforeAll(() => {
    fs.mkdirSync(OUT_DIR, { recursive: true });
  });

  for (const combo of MATRIX) {
    test(combo.id, async ({ page }) => {
      await page.addInitScript(applyPrefsScript, {
        theme: combo.theme,
        preset: combo.preset,
        motion: combo.motion,
      });

      const response = await page.goto(combo.path, { waitUntil: 'domcontentloaded' });
      expect(response?.ok(), `HTTP ${response?.status()} for ${combo.path}`).toBeTruthy();
      await stabilizePresence(page);

      await expect(page.locator('body')).toBeVisible();
      await expect(page.locator('#main-content')).toBeVisible({ timeout: 20_000 });

      const html = page.locator('html');
      await expect(html).toHaveAttribute('data-theme', expectedResolvedTheme(combo.theme));
      const attrs = PRESET_ATTRS[combo.preset];
      if (attrs.density) {
        await expect(html).toHaveAttribute('data-density', attrs.density);
      } else {
        await expect(html).not.toHaveAttribute('data-density');
      }
      if (attrs.readingWidth) {
        await expect(html).toHaveAttribute('data-reading-width', attrs.readingWidth);
      } else {
        await expect(html).not.toHaveAttribute('data-reading-width');
      }
      if (combo.motion === 'auto') {
        await expect(html).not.toHaveAttribute('data-motion');
      } else {
        await expect(html).toHaveAttribute('data-motion', combo.motion);
      }

      await page.waitForTimeout(400);

      const file = path.join(OUT_DIR, `${combo.id}.png`);
      await page.screenshot({ path: file, fullPage: false });
      expect(fs.existsSync(file)).toBeTruthy();

      if (GOLDEN) {
        await expect(page).toHaveScreenshot(`${combo.id}.png`, {
          maxDiffPixelRatio: 0.03,
          animations: 'disabled',
          mask: DYNAMIC_CONTENT.map((sel) => page.locator(sel)),
        });
      }
    });
  }

  for (const vp of VIEWPORTS) {
    test(`home-dark-laptop-${vp.id}`, async ({ page }) => {
      await page.setViewportSize({ width: vp.width, height: vp.height });
      await page.addInitScript(applyPrefsScript, {
        theme: 'dark',
        preset: 'laptop',
        motion: 'off',
      });
      const response = await page.goto(PINNED_HOME, { waitUntil: 'domcontentloaded' });
      await stabilizePresence(page);
      expect(response?.ok()).toBeTruthy();
      await expect(page.locator('#main-content')).toBeVisible({ timeout: 20_000 });
      const file = path.join(OUT_DIR, `home-dark-laptop-${vp.id}.png`);
      await page.screenshot({ path: file, fullPage: false });
      expect(fs.existsSync(file)).toBeTruthy();

      if (GOLDEN) {
        await expect(page).toHaveScreenshot(`home-dark-laptop-${vp.id}.png`, {
          maxDiffPixelRatio: 0.03,
          animations: 'disabled',
          mask: DYNAMIC_CONTENT.map((sel) => page.locator(sel)),
        });
      }
    });
  }

  /**
   * Легаси-ключ одной оси мигрирует в ближайший пресет, а не молча в laptop.
   * Дефект найден этой матрицей: pixelIntensity=low не участвовал в выборе —
   * migrateLegacyPreset читал legacyPixel и не использовал его. Сама ось снята,
   * но перенос сохранён: единственный выбор человека не должен пропасть.
   */
  for (const [legacy, wantPreset] of [
    ['low', 'phone'],
    ['high', 'wide'],
  ]) {
    test(`legacy pixelIntensity=${legacy} migrates to ${wantPreset}`, async ({ page }) => {
      await page.addInitScript((v) => {
        try {
          localStorage.clear();
          localStorage.setItem('pixelIntensity', v);
        } catch (_) { /* private mode */ }
      }, legacy);
      await page.goto('/', { waitUntil: 'domcontentloaded' });
      const attrs = PRESET_ATTRS[wantPreset];
      const html = page.locator('html');
      if (attrs.density) {
        await expect(html).toHaveAttribute('data-density', attrs.density);
      }
      if (attrs.readingWidth) {
        await expect(html).toHaveAttribute('data-reading-width', attrs.readingWidth);
      }
      const stored = await page.evaluate(() => ({
        preset: localStorage.getItem('screenPreset'),
        legacyGone: localStorage.getItem('pixelIntensity') === null,
      }));
      expect(stored.preset).toBe(wantPreset);
      expect(stored.legacyGone, 'легаси-ключ обязан быть удалён после переноса').toBe(true);
    });
  }
});
