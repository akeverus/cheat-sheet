const { test, expect } = require('@playwright/test');

/**
 * TEST-006 — основной цикл только с клавиатуры (без мыши), в обоих движках.
 * Предусловие: приложение на E2E_BASE_URL (default http://localhost:8080).
 *
 * Конвенция движков (замер iter-160): WebKit, как и настольный Safari, по
 * умолчанию НЕ включает кнопки и ссылки в обход по Tab — по ним ходит
 * Option+Tab (в Playwright — 'Alt+Tab'). 80 нажатий Tab в WebKit так и не
 * дошли до кнопки старта, Alt+Tab дошёл за 19 — как и Tab в Chromium.
 * Это поведение платформы, а не дефект продукта; спек обязан говорить
 * с каждым движком на его языке, иначе WebKit-прогон ложно красный.
 */
const tabKey = (browserName) => (browserName === 'webkit' ? 'Alt+Tab' : 'Tab');

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

  /**
   * Полная цепочка: конструктор → старт → выбор → отправка → фокус в разборе →
   * «Следующий вопрос». Раньше проверялся только ответ на уже открытом вопросе;
   * старт сессии и переход к следующему вопросу клавиатурой не покрывались нигде.
   */
  test('full cycle: start from builder, answer, reach next question', async ({ page, browserName }) => {
    const TAB = tabKey(browserName);
    await page.goto('/settings');

    // 1. Табом до кнопки старта конструктора (по пути — поля формы, это нормально)
    let reachedStart = false;
    for (let i = 0; i < 80; i++) {
      await page.keyboard.press(TAB);
      const cls = await page.evaluate(() => (document.activeElement.className || '').toString());
      if (cls.includes('builder-start-cta')) { reachedStart = true; break; }
    }
    expect(reachedStart, 'кнопка старта недостижима с клавиатуры').toBe(true);

    // 2. Enter стартует сессию и приводит на вопрос
    await Promise.all([
      page.waitForURL(/\/(\?.*)?$/, { timeout: 20_000 }),
      page.keyboard.press('Enter'),
    ]);

    const options = page.locator('#interview-options input[type="radio"]');
    test.skip((await options.count()) === 0, 'Первый вопрос без вариантов (flashcard)');

    // 3. Горячая клавиша «1» выбирает, стрелка меняет выбор
    await page.keyboard.press('1');
    await expect(options.nth(0)).toBeChecked();
    await page.keyboard.press('ArrowDown');
    await expect(options.nth(1)).toBeChecked();

    // 4. Enter отправляет; вердикт появляется, фокус — в приёмнике разбора
    await page.keyboard.press('Enter');
    const feedback = page.locator('#result-feedback');
    await expect(feedback).toBeVisible({ timeout: 20_000 });
    await expect(page.locator('.feedback-verdict').first()).toBeVisible();
    const focusInFeedback = await page.evaluate(() =>
      !!document.activeElement.closest('#result-feedback, .result-feedback'));
    expect(focusInFeedback, 'фокус после ответа обязан быть в разборе').toBe(true);

    // 5. Первый же таб из разбора — «Следующий вопрос» (порядок FRD-186/AnsweredOrder)
    let reachedNext = false;
    for (let i = 0; i < 30; i++) {
      await page.keyboard.press(TAB);
      const text = await page.evaluate(() => (document.activeElement.textContent || '').trim());
      if (/Следующий вопрос/.test(text)) { reachedNext = true; break; }
    }
    expect(reachedNext, '«Следующий вопрос» недостижим с клавиатуры').toBe(true);

    // 6. Enter ведёт к следующему вопросу
    await Promise.all([
      page.waitForNavigation({ waitUntil: 'load' }).catch(() => null),
      page.keyboard.press('Enter'),
    ]);
    await expect(page.locator('#interview-options, .flashcard-phase').first())
      .toBeVisible({ timeout: 20_000 });
  });

  /**
   * Флешкарта клавиатурой: Space раскрывает, цифра оценивает, приходит следующая.
   * До iter-166 карточный цикл не покрывался вовсе — MCQ-тесты скипались на
   * флешкартах («Нет #interview-form»), а собственного теста у режима не было.
   * Выбор режима в конструкторе — предусловие, а не предмет теста: кликаем.
   */
  test('flashcard: Space reveals, digit grades, next card arrives', async ({ page }) => {
    await page.goto('/settings');
    await page.locator('label:has(input[name="mode"][value="FLASHCARD"])').first().click();
    await Promise.all([
      page.waitForURL(/\/(\?.*)?$/, { timeout: 20_000 }),
      page.locator('.builder-start-cta').click(),
    ]);

    const question = page.locator('.focus-question');
    await expect(question).toBeVisible({ timeout: 20_000 });

    // до раскрытия оценки скрыты — иначе они подсказывают, что ответ уже виден
    const grades = page.locator('.flashcard-grade-buttons');
    await expect(grades).toBeHidden();

    await page.keyboard.press(' ');
    await expect(grades).toBeVisible({ timeout: 10_000 });

    // «2» = Трудно. Следующая карта приезжает ЗАКРЫТОЙ — оценки снова спрятаны.
    // Сравнивать ТЕКСТ вопроса нельзя: «Трудно» в SRS может тут же вернуть ту же
    // карту (relearn-шаг), и «текст сменился» ложно краснеет — поймано первым же
    // прогоном в обоих движках. Нажатие — с повтором: в WebKit «видимость оценок»
    // опережает подвес их клавиатурного обработчика, и одиночная цифра теряется
    // (поймано вторым прогоном; лишняя цифра на закрытой карте — no-op).
    await expect
      .poll(async () => {
        await page.keyboard.press('2');
        return grades.isHidden();
      }, { timeout: 20_000, intervals: [400, 800, 1500] })
      .toBe(true);
    await expect(question).toBeVisible();
  });
});
