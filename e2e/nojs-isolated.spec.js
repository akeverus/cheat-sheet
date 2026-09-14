const { test, expect } = require('@playwright/test');

// These are real server writes. They are opt-in and restricted to the dedicated
// isolated app; running the normal suite against :8080 can never execute them.
test.skip(process.env.E2E_ISOLATED_DATA !== '1'
  || !/^http:\/\/(?:localhost|127\.0\.0\.1):8081\/?$/.test(process.env.E2E_BASE_URL || ''),
'Requires E2E_ISOLATED_DATA=1 and the isolated application on :8081');
test.use({ javaScriptEnabled: false });

test('native code view exposes the full listing without inert toolbar buttons', async ({ page }) => {
  await page.goto('/practice?questionId=4063');
  const viewport = page.locator('.code-viewport').first();
  await expect(viewport).toBeVisible();
  const buttons = page.locator('.code-toolbar-actions button, .diagram-toolbar-actions button');
  expect(await buttons.count()).toBeGreaterThan(0);
  for (const button of await buttons.all()) {
    await expect(button).toBeHidden();
  }
  const geometry = await viewport.evaluate(el => ({
    clientHeight: el.clientHeight,
    scrollHeight: el.scrollHeight,
    overflowY: getComputedStyle(el).overflowY,
    content: el.textContent.trim(),
  }));
  expect(geometry.content.length).toBeGreaterThan(20);
  expect(geometry.scrollHeight - geometry.clientHeight).toBeLessThanOrEqual(1);
});

test('native Mermaid source disclosure remains readable without rendering', async ({ page }) => {
  await page.goto('/practice?questionId=' + (process.env.E2E_MERMAID_QUESTION_ID || '8489'));
  const diagram = page.locator('.question-diagram[data-content-type="MERMAID"]').first();
  await expect(diagram).toBeVisible();
  await expect(diagram.locator('.mermaid svg')).toHaveCount(0);
  const buttons = diagram.locator('.diagram-toolbar-actions button');
  expect(await buttons.count()).toBeGreaterThan(0);
  for (const button of await buttons.all()) {
    await expect(button).toBeHidden();
  }
  const disclosure = diagram.locator('.diagram-source-details');
  await disclosure.locator('summary').click();
  await expect(disclosure).toHaveAttribute('open');
  const source = disclosure.locator('.diagram-source');
  await expect(source).toBeVisible();
  const geometry = await source.evaluate(el => ({
    clientHeight: el.clientHeight,
    scrollHeight: el.scrollHeight,
    content: el.textContent.trim(),
  }));
  expect(geometry.content.length).toBeGreaterThan(20);
  expect(geometry.scrollHeight - geometry.clientHeight).toBeLessThanOrEqual(1);
});

test('native builder, note, validation, answer, next and home resume preserve context', async ({ page }) => {
  await page.goto('/train?topic=frameworks%2Fspring%2Fspring-graphql-interview&mode=MARATHON');
  await expect(page.getByText('JavaScript отключён', { exact: true })).toBeVisible();
  if (await page.locator('details.mode-more').getAttribute('open') === null) {
    await page.locator('details.mode-more > summary').click();
  }
  await page.locator('.mode-card').filter({ has: page.locator('input[name="mode"][value="MARATHON"]') }).click();
  await page.locator('input[name="count"]').fill('3');
  await page.locator('.builder-start-cta').click();
  await expect(page).toHaveURL(/\/practice(?:\?|$)/);
  const question = page.locator('#interview-form input[name="questionId"]');
  const questionId = await question.inputValue();
  const questionText = await page.locator('.focus-question').innerText();

  // Native required validation runs before any POST.
  const answerRequests = [];
  page.on('request', request => {
    if (request.method() === 'POST' && new URL(request.url()).pathname === '/answer') answerRequests.push(request);
  });
  await page.locator('#interview-submit').click();
  await expect(page.locator('input[name="optionId"]').first()).toBeFocused();
  expect(answerRequests).toHaveLength(0);
  await expect(question).toHaveValue(questionId);

  await page.locator('.question-note-outer > summary').click();
  await page.locator('.question-note-disclosure > summary').click();
  const noteText = 'Изолированная no-JS проверка: сохранить мысль и текущий вопрос.';
  await page.locator('.question-note-form textarea').fill(noteText);
  await page.locator('.question-note-form button[type="submit"]').click();
  await expect(page).toHaveURL(new RegExp('/practice\\?questionId=' + questionId + '$'));
  await expect(question).toHaveValue(questionId);
  await expect(page.locator('.focus-question')).toHaveText(questionText);
  await expect(page.locator('.question-note-saved')).toHaveText('Заметка сохранена.');
  await expect(page.locator('.question-note-text')).toHaveText(noteText);
  await page.locator('.question-note-disclosure > summary').click();
  await expect(page.locator('.question-note-form textarea')).toHaveValue(noteText);

  await page.locator('#interview-options label').first().click();
  const chosenValue = await page.locator('input[name="optionId"]:checked').inputValue();
  await page.locator('#interview-submit').click();
  await expect(page).toHaveURL(/\/answer(?:\?|$)/);
  await expect(page.locator('.result-verdict')).toContainText(/Верно|Неверно|Правильный|Ты выбрал/i);
  expect(answerRequests).toHaveLength(1);
  expect(new URLSearchParams(answerRequests[0].postData()).get('optionId')).toBe(chosenValue);
  await page.getByRole('link', { name: 'Следующий вопрос', exact: true }).click();
  await expect(page.locator('.focus-question')).toBeVisible();

  const beforePause = await question.inputValue();
  await page.locator('.focus-session-more > summary').click();
  await page.locator('.rail-pause button').click();
  await page.goto('/');
  await expect(page.locator('.home-primary-form')).toHaveAttribute('action', '/resume');
  await page.locator('.home-primary-form button').click();
  await expect(question).toHaveValue(beforePause);
});

test('native settings goal overshoot keeps the actual answer count and names completion', async ({ page }, testInfo) => {
  const beforeResponse = await page.request.get('/api/streak');
  expect(beforeResponse.ok()).toBe(true);
  const before = await beforeResponse.json();
  expect(before.today, 'the isolated fixture must have more than one real answer').toBeGreaterThan(1);

  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/settings');
  const goal = page.locator('#settings-daily-goal');
  const save = page.locator('.settings-goal-form button[type="submit"]');
  const originalGoal = await goal.inputValue();
  const submissions = [];
  page.on('request', request => {
    if (request.method() === 'POST' && new URL(request.url()).pathname === '/streak/goal') {
      submissions.push(new URLSearchParams(request.postData()));
    }
  });

  try {
    await goal.fill('1');
    await save.click();
    await expect(page).toHaveURL(/\/settings(?:\?|$)/);
    await expect(goal).toHaveValue('1');
    expect(submissions).toHaveLength(1);
    expect(submissions[0].get('goal')).toBe('1');
    expect(submissions[0].get('returnTo')).toBe('settings');

    const afterResponse = await page.request.get('/api/streak');
    expect(afterResponse.ok()).toBe(true);
    const after = await afterResponse.json();
    expect(after.today).toBe(before.today);
    expect(after.goal).toBe(1);
    expect(after.goalReached).toBe(true);
    const hint = page.locator('#settings-daily-goal-hint');
    await expect(hint).toContainText(`Цель выполнена · ${before.today} `);
    await expect(hint).not.toContainText(/\d+\s+из\s+1(?:\D|$)/);
    await hint.scrollIntoViewIfNeeded();
    await page.screenshot({
      path: require('path').resolve(__dirname, process.env.E2E_EVIDENCE_DIR || '../output/playwright/redesign-20260905-current/diagnostics',
        `settings-goal-overshoot-${testInfo.project.name}.png`),
    });
  } finally {
    await goal.fill(originalGoal);
    await save.click();
    await expect(goal).toHaveValue(originalGoal);
  }
});
