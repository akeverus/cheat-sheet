const { test, expect } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

/**
 * TEST-005 — визуальная матрица theme × pixel × motion.
 * Скриншоты → e2e/visual-results/ (gitignored).
 * Golden: VISUAL_GOLDEN=1 сравнивает с e2e/visual-baselines/
 *   (обновить: e2e/scripts/run-visual-goldens.sh --update
 *    или VISUAL_GOLDEN=1 npx playwright test visual-matrix --update-snapshots).
 * CI: .github/workflows/visual-goldens.yml (workflow_dispatch).
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
   Пин: ?topic&ordered=true отдаёт первый вопрос темы независимо от SRS-истории
   (проверено с мутацией между прогонами: id 2060 оба раза). Слаг констатен
   нарочно: переименование темы должно уронить тест громко, а не молча сдвинуть
   голден. После реимпорта банка id меняются, но первый вопрос темы — тот же. */
/* REF-022: пин темой оказался ложной стабильностью — browse-выборка идёт через
   SRS-стратегию (due-first), и первая же тренировка по теме переставляет очередь:
   home-dark-med-vp-768 упал сдвигом ВЫСОТЫ замаскированного контента. Якорь —
   ЯВНЫЙ questionId (GET / его поддерживает, вопрос детерминирован независимо от
   SRS-истории). После реимпорта банка id может уехать — тогда тест падает ГРОМКО
   (другой вопрос = другой голден), и константу обновляют осознанно. */
const PINNED_HOME =
  '/?questionId=2060&topic='
  + encodeURIComponent('algorithms/algorithmic-paradigms/backtracking-interview')
  + '&ordered=true&shuffle=false';

const DYNAMIC_CONTENT = [
  '.focus-question', '#interview-options', '.topic-brief', '.question-code',
  '.focus-bank-progress', '.today-chip', '.focus-aside .btn',
  '.stats-next-actions-grid', '.dashboard-grid', '.stats-standing', '.metrics-legend',
  '.page-head-lead',
];

/* Маска лечит ЗНАЧЕНИЯ, но не ПРИСУТСТВИЕ: блок, который то есть, то нет
   (причина повторения зависит от истории ответов, баннер — от незавершённой
   сессии), сдвигает всю геометрию ниже на свою высоту, и снимок краснеет
   вокруг масок. Такие блоки на время снимка скрываются целиком — их пиксели
   матрица честно НЕ стережёт (поведение причин закрыто контрактами разметки,
   баннера — клавиатурным спеком). */
/* .today-chip не исчезает, но МЕНЯЕТ РАЗМЕР от охвата очереди (полный банк ↔
   фильтр сессии): маска повторяет бокс элемента, и вокруг неё краснеют полосы.
   Прячем вместе с presence-волатильными. shuffle=false в пине — той же природы:
   перемешивание вариантов меняло высоты карточек ответов между прогонами. */
const VOLATILE_PRESENCE = ['.focus-question-reason', '.resume-banner', '.notice', '.today-chip'];
const stabilizePresence = (page) =>
  page.addStyleTag({ content: VOLATILE_PRESENCE.join(', ') + ' { display: none !important; }' });
const GOLDEN = process.env.VISUAL_GOLDEN === '1';

/** @type {Array<{ id: string, path: string, theme: string, pixel: string, motion: string }>} */
const MATRIX = [
  { id: 'home-dark-med-auto', path: PINNED_HOME, theme: 'dark', pixel: 'med', motion: 'auto' },
  { id: 'home-light-med-on', path: PINNED_HOME, theme: 'light', pixel: 'med', motion: 'on' },
  { id: 'home-dark-low-off', path: PINNED_HOME, theme: 'dark', pixel: 'low', motion: 'off' },
  { id: 'home-dark-high-on', path: PINNED_HOME, theme: 'dark', pixel: 'high', motion: 'on' },
  { id: 'home-light-high-off', path: PINNED_HOME, theme: 'light', pixel: 'high', motion: 'off' },
  { id: 'settings-dark-med-off', path: '/settings', theme: 'dark', pixel: 'med', motion: 'off' },
  { id: 'settings-light-med-on', path: '/settings', theme: 'light', pixel: 'med', motion: 'on' },
  { id: 'stats-dark-med-off', path: '/stats', theme: 'dark', pixel: 'med', motion: 'off' },
  { id: 'stats-light-low-off', path: '/stats', theme: 'light', pixel: 'low', motion: 'off' },
];

/** Extra viewports for Wave 8 responsive smoke (subset of full matrix). */
const VIEWPORTS = [
  { id: 'vp-375', width: 375, height: 812 },
  { id: 'vp-768', width: 768, height: 1024 },
  { id: 'vp-1280', width: 1280, height: 800 },
];

/* Пиксельность с редизайна персонализации (0aad5535) не хранится своей осью:
   её задаёт пресет экрана (phone→low, laptop→med, wide→high). Прежний ключ
   pixelIntensity — легаси, он мигрирует в пресет при первой загрузке; матрица
   говорит на живом контракте, а миграцию проверяет отдельный тест ниже. */
const PRESET_FOR_PIXEL = { low: 'phone', med: 'laptop', high: 'wide' };

function applyPrefsScript({ theme, pixel, motion, preset }) {
  try {
    if (theme) localStorage.setItem('theme', theme);
    else localStorage.removeItem('theme');
    if (preset) localStorage.setItem('screenPreset', preset);
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
        pixel: combo.pixel,
        motion: combo.motion,
        preset: combo.pixel === 'med' ? null : PRESET_FOR_PIXEL[combo.pixel],
      });

      const response = await page.goto(combo.path, { waitUntil: 'domcontentloaded' });
      expect(response?.ok(), `HTTP ${response?.status()} for ${combo.path}`).toBeTruthy();
      await stabilizePresence(page);

      await expect(page.locator('body')).toBeVisible();
      await expect(page.locator('#main-content')).toBeVisible({ timeout: 20_000 });

      const html = page.locator('html');
      await expect(html).toHaveAttribute('data-theme', expectedResolvedTheme(combo.theme));
      await expect(html).toHaveAttribute('data-pixel', combo.pixel);
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
    test(`home-dark-med-${vp.id}`, async ({ page }) => {
      await page.setViewportSize({ width: vp.width, height: vp.height });
      await page.addInitScript(applyPrefsScript, {
        theme: 'dark',
        pixel: 'med',
        motion: 'off',
      });
      const response = await page.goto(PINNED_HOME, { waitUntil: 'domcontentloaded' });
      await stabilizePresence(page);
      expect(response?.ok()).toBeTruthy();
      await expect(page.locator('#main-content')).toBeVisible({ timeout: 20_000 });
      const file = path.join(OUT_DIR, `home-dark-med-${vp.id}.png`);
      await page.screenshot({ path: file, fullPage: false });
      expect(fs.existsSync(file)).toBeTruthy();

      if (GOLDEN) {
        await expect(page).toHaveScreenshot(`home-dark-med-${vp.id}.png`, {
          maxDiffPixelRatio: 0.03,
          animations: 'disabled',
          mask: DYNAMIC_CONTENT.map((sel) => page.locator(sel)),
        });
      }
    });
  }

  /**
   * Легаси-ключ одной оси мигрирует в ближайший пресет, а не молча в laptop/med.
   * Дефект найден этой матрицей: pixelIntensity=low давал data-pixel="med" —
   * migrateLegacyPreset читал legacyPixel и не использовал его в выборе.
   */
  for (const [legacy, wantPreset, wantPixel] of [
    ['low', 'phone', 'low'],
    ['high', 'wide', 'high'],
  ]) {
    test(`legacy pixelIntensity=${legacy} migrates to ${wantPreset}`, async ({ page }) => {
      await page.addInitScript((v) => {
        try {
          localStorage.clear();
          localStorage.setItem('pixelIntensity', v);
        } catch (_) { /* private mode */ }
      }, legacy);
      await page.goto('/', { waitUntil: 'domcontentloaded' });
      await expect(page.locator('html')).toHaveAttribute('data-pixel', wantPixel);
      const stored = await page.evaluate(() => ({
        preset: localStorage.getItem('screenPreset'),
        legacyGone: localStorage.getItem('pixelIntensity') === null,
      }));
      expect(stored.preset).toBe(wantPreset);
      expect(stored.legacyGone, 'легаси-ключ обязан быть удалён после переноса').toBe(true);
    });
  }
});
