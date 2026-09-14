const { test, expect } = require('@playwright/test');

// These checks only navigate and expand content; they never save learning data.
test.beforeEach(async ({ page }) => {
  await page.route('**/*', route => ['GET', 'HEAD', 'OPTIONS'].includes(route.request().method())
    ? route.continue() : route.abort('blockedbyclient'));
});

for (const width of [320, 375, 768]) for (const theme of ['dark', 'light']) for (const scale of [1, 1.4]) {
  test(`mobile builder keeps the chosen volume beside the launch action at ${width}, ${theme}, text ${scale}`, async ({ page }) => {
    await page.setViewportSize({ width, height: 812 });
    await page.addInitScript(({ theme, scale }) => {
      localStorage.setItem('theme', theme);
      localStorage.setItem('fontScale', String(scale));
    }, { theme, scale });
    await page.goto('/train?mode=MARATHON');
    const input = page.locator('input[name="count"]');
    await input.fill('7');
    await page.evaluate(() => scrollTo(0, 0));
    await expect(page.locator('[data-preview-count]')).toContainText('7 вопросов');
    const reachable = await page.locator('[data-preview-count]').evaluate(el => {
      const r = el.getBoundingClientRect();
      const nav = document.querySelector('.mobile-nav').getBoundingClientRect();
      return r.top >= 0 && r.bottom <= nav.top && document.elementFromPoint(r.x + r.width / 2, r.y + r.height / 2) === el;
    });
    expect(reachable, 'session size must be readable before pressing the fixed launch button').toBe(true);
    const button = page.locator('#session-start-btn');
    await expect(button).toBeInViewport();
    expect(await button.evaluate(el => {
      const r = el.getBoundingClientRect();
      return el.contains(document.elementFromPoint(r.x + r.width / 2, r.y + r.height / 2));
    })).toBe(true);
  });
}

for (const javaScriptEnabled of [true, false]) {
  test.describe(`bookmark reading with JavaScript ${javaScriptEnabled}`, () => {
    test.use({ javaScriptEnabled });
    test('the long question can be read in place before navigating away', async ({ page }) => {
      await page.setViewportSize({ width: 375, height: 812 });
      await page.goto('/bookmarks');
      const detail = page.locator('.bookmark-question-detail').first();
      await expect(detail).toBeVisible();
      await detail.locator('summary').click();
      const full = detail.locator('.bookmark-question-full');
      await expect(full).toBeVisible();
      expect((await full.innerText()).length).toBeGreaterThan(160);
      await expect(page).toHaveURL(/\/bookmarks$/);
      const link = page.locator('.page-head-actions a');
      await expect(link).toHaveAttribute('href', '/train?important=true&ordered=true');
      await link.click();
      await expect(page.locator('#filters-form input[name="important"]')).toBeChecked();
      await expect(page.locator('.outcome-total')).toBeVisible();
      await expect(page.locator('[data-preview-filters]')).toContainText('избранные');
    });

    test('the selected topic and order are described before starting', async ({ page }) => {
      await page.goto('/train?topic=frameworks%2Fspring%2Fspring-graphql-interview&ordered=false');
      await expect(page.locator('[data-preview-filters]')).toContainText('Spring for GraphQL');
      await expect(page.locator('[data-preview-filters]')).toContainText('свободный порядок');
      await expect(page.locator('[data-preview-filters]')).not.toContainText('все темы');
    });
  });
}
