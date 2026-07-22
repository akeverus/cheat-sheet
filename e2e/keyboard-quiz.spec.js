const { test, expect } = require('@playwright/test');

/**
 * TEST-006 — полный MCQ-ответ только с клавиатуры (без мыши).
 * Предусловие: приложение на E2E_BASE_URL (default http://localhost:8080).
 */
test.describe('keyboard quiz flow', () => {
  test('skip-link activates main, digit selects, Enter submits feedback', async ({ page }) => {
    await page.goto('/');

    const skip = page.locator('a.skip-link').first();
    await expect(skip).toBeVisible();
    // Skip-link обычно position:absolute off-screen until focus — активируем клавиатурой
    await skip.focus();
    await expect(skip).toBeFocused();
    await page.keyboard.press('Enter');
    await expect(page.locator('#main-content')).toBeFocused();

    const form = page.locator('#interview-form');
    const hasForm = await form.count();
    test.skip(!hasForm, 'Нет #interview-form — банк пуст или flashcard/study mode');

    const options = form.locator('input[name="optionId"]');
    await expect(options.first()).toBeVisible();

    const first = options.first();
    await first.focus();
    await expect(first).toBeFocused();

    // Цифра 1 выбирает первый вариант (handler требует focus внутри формы)
    await page.keyboard.press('1');
    await expect(first).toBeChecked();

    await page.keyboard.press('Enter');

    const feedback = page.locator('#result-feedback');
    await expect(feedback).toBeVisible({ timeout: 20_000 });
    await expect(feedback).not.toBeEmpty();
    await expect(feedback).toContainText(/Верно|Неверно|Правильный|Ты выбрал/i);
  });

  test('ArrowDown moves selection without mouse', async ({ page }) => {
    await page.goto('/');
    const form = page.locator('#interview-form');
    test.skip((await form.count()) === 0, 'Нет MCQ-формы');

    const options = form.locator('input[name="optionId"]');
    const count = await options.count();
    test.skip(count < 2, 'Меньше двух вариантов');

    await options.first().focus();
    await page.keyboard.press('1');
    await expect(options.nth(0)).toBeChecked();

    await page.keyboard.press('ArrowDown');
    await expect(options.nth(1)).toBeChecked();
  });
});
