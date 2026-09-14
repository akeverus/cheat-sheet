const { test, expect } = require('@playwright/test');
const path = require('path');
const fs = require('fs');

test.skip(process.env.E2E_ISOLATED_DATA !== '1'
  || !/^http:\/\/(?:localhost|127\.0\.0\.1):8081\/?$/.test(process.env.E2E_BASE_URL || ''),
  'Real session writes require the isolated application on :8081');

const output = path.resolve(__dirname, process.env.E2E_EVIDENCE_DIR || '../output/playwright/redesign-20260905-current/modes');
const phase = process.env.E2E_EVIDENCE_PHASE || 'after';
const topic = 'algorithms/algorithmic-paradigms/backtracking-interview';

async function start(page, mode) {
  await page.goto('/train?topic=' + encodeURIComponent(topic));
  const choice = page.locator('input[type="radio"][name="mode"][value="' + mode + '"]');
  if (!(await choice.isVisible())) await page.locator('details.mode-more > summary').click();
  await page.locator("label.mode-card").filter({ has: choice }).click();
  await expect(choice).toBeChecked();
  if (mode !== 'TRAINING') await page.locator('input[name="count"]').fill('3');
  await page.locator('.builder-start-cta').click();
  await expect(page).toHaveURL(/\/practice(?:\?|$)/);
  await expect(page.locator('.focus-question')).toBeVisible();
  await page.evaluate(() => document.fonts.ready);
}

for (const mode of ['TRAINING', 'EXAM', 'MARATHON', 'STUDY', 'FLASHCARD', 'TYPED_RECALL', 'CODE_CHALLENGE', 'INTERVIEW']) {
  test(mode + ': working surface and answer cycle', async ({ page }, testInfo) => {
    await start(page, mode);
    fs.mkdirSync(output, { recursive: true });
    fs.writeFileSync(path.join(output, `${phase}-${mode}-${testInfo.project.name}.json`), JSON.stringify({
      route: page.url(), mode, browser: testInfo.project.name, phase,
      questionId: await page.locator('input[name="questionId"]').first().inputValue().catch(() => null),
      question: await page.locator('.focus-question').innerText(),
      fixture: 'Isolated PostgreSQL interview_ui_verify; real imported bank; topic ' + topic,
      screenshots: ['dark', 'light'].flatMap(theme => [375, 1728].map(width => `${phase}-${mode}-${theme}-${width}-${testInfo.project.name}.png`))
    }, null, 2));
    for (const theme of ['dark', 'light']) {
      // Exercise the product's switch; do not force theme attributes.
      if (await page.locator('html').getAttribute('data-theme') !== theme) {
        await page.locator('[data-theme-toggle]:visible').first().click();
      }
      await expect(page.locator('html')).toHaveAttribute('data-theme', theme);
      for (const size of [{ width: 1728, height: 1117 }, { width: 375, height: 812 }]) {
        await page.setViewportSize(size);
        await page.screenshot({ animations: 'disabled', path: path.join(output, `${phase}-${mode}-${theme}-${size.width}-${testInfo.project.name}.png`) });
        expect(await page.evaluate(() => document.documentElement.scrollWidth - document.documentElement.clientWidth)).toBeLessThanOrEqual(1);
      }
    }
    if (mode === 'STUDY') {
      await expect(page.locator('#interview-form')).toHaveCount(0);
      await page.locator('.study-confirm-btn').click();
    }
    if (['TRAINING', 'EXAM', 'MARATHON', 'STUDY'].includes(mode)) {
      const options = page.locator('#interview-options input');
      await expect(options).toHaveCount(4);
      await page.locator('#interview-options label').first().click();
      await page.locator('#interview-submit').click();
      await expect(page.locator('#result-feedback')).toBeVisible();
      await expect(page.locator('#next-question')).toBeVisible();
      await page.locator('#next-question').click();
      await expect(page.locator('.focus-question')).toBeVisible();
    } else {
      if (mode !== 'FLASHCARD') await page.locator('#typed-answer').fill('Собственный ответ\n  return value < limit;');
      await expect(page.locator('.flashcard-grade-buttons')).toHaveCount(0);
      await page.locator('.flashcard-reveal-btn').first().click();
      await expect(page.locator('.flashcard-grade-buttons')).toBeVisible();
      if (mode !== 'FLASHCARD') await expect(page.locator('.recall-your-answer-body')).toHaveText('Собственный ответ\n  return value < limit;');
      await page.locator('.flashcard-grade-btn[value="3"]').click();
      await expect(page.locator('.flashcard-grade-buttons')).toHaveCount(0);
      await expect(page).toHaveTitle(/Вопрос 2 из 3/);
    }
  });
}

test('typed draft survives reload and failed grade navigation', async ({ page }) => {
  await start(page, 'TYPED_RECALL');
  const draft = 'Этот ответ должен пережить перезагрузку.\nИ ошибку отправки.';
  await page.locator('#typed-answer').fill(draft);
  await page.reload();
  await expect(page.locator('#typed-answer')).toHaveValue(draft);
  await page.locator('.flashcard-reveal-btn').click();
  await expect(page.locator('.recall-your-answer-body')).toHaveText(draft);
  await page.route('**/flashcard-grade', route => route.fulfill({ status: 503, contentType: 'text/html', body: '<h1>Simulated grade failure</h1>' }));
  await page.locator('.flashcard-grade-btn[value="3"]').click();
  await page.goto('/practice');
  await expect(page.locator('.recall-your-answer-body')).toHaveText(draft);
  await page.unroute('**/flashcard-grade');
  await page.locator('.flashcard-grade-btn[value="3"]').click();
  await expect(page.locator('#typed-answer')).toHaveValue('');
});

test('replayed native grade cannot advance the following revealed question', async ({ page }) => {
  await start(page, 'FLASHCARD');
  await page.locator('.flashcard-reveal-btn').click();
  const firstQuestion = await page.locator('.flashcard-grade-buttons input[name="questionId"]').inputValue();
  const firstPost = page.waitForRequest(r => r.method() === 'POST' && new URL(r.url()).pathname === '/flashcard-grade');
  await page.locator('.flashcard-grade-btn[value="3"]').click();
  const request = await firstPost;
  await expect(page).toHaveTitle(/Вопрос 2 из 3/);
  await page.locator('.flashcard-reveal-btn').click();
  const secondQuestion = await page.locator('.flashcard-grade-buttons input[name="questionId"]').inputValue();
  expect(secondQuestion).not.toBe(firstQuestion);
  const response = await page.request.post('/flashcard-grade', {
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data: request.postData(),
  });
  expect(response.ok()).toBe(true);
  await page.goto('/practice');
  await expect(page).toHaveTitle(/Вопрос 2 из 3/);
  await expect(page.locator('.flashcard-grade-buttons')).toBeVisible();
  await expect(page.locator('.flashcard-grade-buttons input[name="questionId"]')).toHaveValue(secondQuestion);
});

test('no-JS typed answer is retained beside the reference after reveal and reload', async ({ browser }) => {
  const context = await browser.newContext({ javaScriptEnabled: false });
  const page = await context.newPage();
  await page.goto(process.env.E2E_BASE_URL + '/train?topic=' + encodeURIComponent(topic));
  await page.locator('details.mode-more > summary').click();
  await page.locator('input[type="radio"][name="mode"][value="TYPED_RECALL"]').check();
  await page.locator('input[name="count"]').fill('3');
  await page.locator('.builder-start-cta').click();
  const draft = 'Ответ без JavaScript: <script>не исполнять</script>\nВторая строка.';
  await page.locator('#typed-answer').fill(draft);
  await page.locator('.flashcard-reveal-btn').click();
  await expect(page.locator('.recall-your-answer')).toBeVisible();
  await expect(page.locator('.recall-your-answer-body')).toHaveText(draft);
  await page.reload();
  await expect(page.locator('.recall-your-answer-body')).toHaveText(draft);
  await expect(page.locator('.recall-your-answer-body script')).toHaveCount(0);
  await page.locator('.flashcard-grade-btn[value="3"]').click();
  await expect(page.locator('#typed-answer')).toHaveValue('');
  await context.close();
});

test.describe('native mode cycles', () => {
  test.use({ javaScriptEnabled: false });
  for (const mode of ['TRAINING', 'EXAM', 'MARATHON', 'STUDY', 'FLASHCARD', 'TYPED_RECALL', 'CODE_CHALLENGE', 'INTERVIEW']) {
    test(mode + ': server forms preserve the learning cycle', async ({ page }) => {
      await start(page, mode);
      if (mode === 'STUDY') {
        await expect(page.locator('#interview-form')).toHaveCount(0);
        await page.locator('.study-confirm-btn').click();
      }
      if (['TRAINING', 'EXAM', 'MARATHON', 'STUDY'].includes(mode)) {
        await page.locator('#interview-options label').first().click();
        await page.locator('#interview-submit').click();
        await expect(page.locator('.result-verdict')).toBeVisible();
        await page.getByRole('link', { name: 'Следующий вопрос', exact: true }).click();
        await expect(page.locator('.focus-question')).toBeVisible();
      } else {
        if (mode !== 'FLASHCARD') await page.locator('#typed-answer').fill('Собственный ответ без JS.\n  return value;');
        await page.locator('.flashcard-reveal-btn').click();
        if (mode !== 'FLASHCARD') await expect(page.locator('.recall-your-answer-body')).toHaveText('Собственный ответ без JS.\n  return value;');
        await page.locator('.flashcard-grade-btn[value="3"]').click();
        await expect(page).toHaveTitle(/Вопрос 2 из 3/);
        await expect(page.locator('.flashcard-grade-buttons')).toHaveCount(0);
      }
    });
  }
});

test('code input keeps native Tab exit, line numbers and editor shortcuts', async ({ page, browserName }) => {
  await start(page, 'CODE_CHALLENGE');
  await page.setViewportSize({ width: 375, height: 812 });
  const field = page.locator('#typed-answer');
  const code = Array.from({ length: 45 }, (_, i) => '  value_' + i + ' = "' + 'identifier'.repeat(12) + '";').join('\n');
  await field.fill(code);
  await expect(page.locator('.code-status')).toContainText('45 строк');
  await expect(page.locator('.code-gutter')).toHaveText(Array.from({ length: 45 }, (_, i) => String(i + 1)).join('\n'));
  await field.press('End');
  await field.press('Enter');
  await field.press('1');
  await field.press('?');
  await expect(page.locator('.kbd-help-overlay')).toBeHidden();
  await expect(page.locator('.flashcard-grade-buttons')).toHaveCount(0);
  await expect(page.locator('.code-status')).toContainText('46 строк');
  await page.keyboard.press(browserName === 'webkit' ? 'Alt+Tab' : 'Tab');
  await expect(page.locator('.typed-answer-input button[type="submit"]')).toBeFocused();
  await page.keyboard.press('Enter');
  await expect(page.locator('.recall-your-answer-body')).toContainText('identifier'.repeat(12));
});

test('help keeps report dialog focus and matches recall phase', async ({ page }) => {
  await start(page, 'FLASHCARD');
  await page.keyboard.press('?');
  await expect(page.locator('.kbd-help-overlay')).toBeVisible();
  await expect(page.locator('.kbd-help-list')).toContainText('Раскрыть ответ');
  await expect(page.locator('.kbd-help-list')).not.toContainText('Выбрать вариант');
  await page.keyboard.press('Escape');
  await page.locator('[data-report-trigger]').click();
  await expect(page.locator('.report-modal input[name="report-category"], .report-modal #report-comment').first()).toBeFocused();
  const cancel = page.locator('[data-report-cancel]');
  await cancel.focus();
  await page.keyboard.press('?');
  await expect(page.locator('.kbd-help-overlay')).toBeHidden();
  await expect(cancel).toBeFocused();
  await page.keyboard.press('Escape');
  await expect(page.locator('.report-modal')).toHaveCount(0);
});

test('interview timer starts by request, pauses and resumes without submitting notes', async ({ page }) => {
  await start(page, 'INTERVIEW');
  const timer = page.locator('.rehearsal-timer');
  const clock = timer.locator('.rehearsal-timer-clock');
  const toggle = timer.locator('.rehearsal-timer-toggle');
  await expect(clock).toHaveText('0:00');
  await page.clock.install();
  await toggle.click();
  await page.clock.runFor(5000);
  await expect(clock).toHaveText('0:05');
  await toggle.click();
  await page.clock.runFor(3000);
  await expect(clock).toHaveText('0:05');
  await toggle.click();
  await page.clock.runFor(2000);
  await expect(clock).toHaveText('0:07');
  await expect(page.locator('#typed-answer')).toBeVisible();
  await expect(page.locator('.flashcard-grade-buttons')).toHaveCount(0);
});

test('server validation returns to the builder with the mode, topic and invalid count', async ({ page }) => {
  await page.goto('/train?topic=' + encodeURIComponent(topic));
  await page.evaluate(() => localStorage.setItem('quiz.session.count', '27'));
  const exam = page.locator('input[type="radio"][name="mode"][value="EXAM"]');
  if (!(await exam.isVisible())) await page.locator('details.mode-more > summary').click();
  await page.locator('label.mode-card').filter({ has: exam }).click();
  const count = page.locator('input[name="count"]');
  await count.fill('-1');
  // Deliberately bypass client validation to exercise the real server rejection.
  await page.locator('#session-form').evaluate(form => { form.noValidate = true; });
  await page.locator('.builder-start-cta').click();
  await expect(page).toHaveURL(/\/train\?/);
  await expect(page.locator('#start-validation-error')).toContainText('Сессия не начата');
  await expect(exam).toBeChecked();
  await expect(page.locator('#filters-form select[name="topic"]')).toHaveValue(topic);
  await expect(count).toHaveValue('-1');
  await expect(count).toHaveAttribute('aria-invalid', 'true');
  await count.fill('3');
  await expect(count).not.toHaveAttribute('aria-invalid');
  await page.locator('.builder-start-cta').click();
  await expect(page.locator('.focus-question')).toBeVisible();
  await expect(page).toHaveTitle(/Вопрос 1 из 3/);
});

test('finished recall session has a direct summary action without a refresh loop', async ({ page }) => {
  await page.goto('/train?mode=FLASHCARD&topic=' + encodeURIComponent(topic));
  await page.locator('input[name="count"]').fill('1');
  await page.locator('.builder-start-cta').click();
  await page.locator('.flashcard-reveal-btn').click();
  await page.locator('.flashcard-grade-btn[value="3"]').click();
  await expect(page).toHaveURL(/\/practice$/);
  await expect(page.getByRole('heading', { name: 'Сессия завершена', exact: true })).toBeVisible();
  await expect(page).toHaveTitle('Сессия завершена — Подготовка к собеседованию');
  await expect(page.locator('.empty-action-retry')).toHaveCount(0);
  await page.locator('.empty-action-summary').click();
  await expect(page).toHaveURL(/\/session-summary$/);
  await expect(page.getByRole('heading', { name: 'Итоги сессии', exact: true })).toBeVisible();
  await expect(page).toHaveTitle('1 из 1 — Итоги сессии — Подготовка к собеседованию');
});

test('pause honors a pinned question and resume restores the unsent recall draft', async ({ page, browser }) => {
  const discovery = await browser.newContext({ baseURL: process.env.E2E_BASE_URL });
  const catalog = await discovery.newPage();
  await catalog.goto('/practice?topic=' + encodeURIComponent(topic));
  const targetId = await catalog.locator('input[name="questionId"]').first().inputValue();
  const targetText = await catalog.locator('.focus-question').innerText();
  await discovery.close();
  await start(page, 'TYPED_RECALL');
  const originalText = await page.locator('.focus-question').innerText();
  const draft = 'Мой незавершённый ответ до паузы.';
  await page.locator('#typed-answer').fill(draft);
  await page.goto('/practice?questionId=' + encodeURIComponent(targetId));
  await expect(page.locator('.intent-banner')).toBeVisible();
  await expect(page.locator('.focus-question')).toHaveText(originalText);
  await page.locator('.intent-banner button[type="submit"]').click();
  await expect(page).toHaveURL(new RegExp('/practice\\?questionId=' + targetId + '$'));
  await expect(page.locator('.focus-question')).toHaveText(targetText);
  await expect(page.locator('.intent-banner')).toHaveCount(0);
  await page.locator('form[action="/resume"] button[type="submit"]').click();
  await expect(page.locator('.focus-question')).toHaveText(originalText);
  await expect(page.locator('#typed-answer')).toHaveValue(draft);
});
