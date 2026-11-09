const { test, expect } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

test.beforeEach(async ({ page }) => {
  // All mutations are synthetic; the running application's database is untouched.
  await page.route('**/*', route => {
    if (['GET', 'HEAD', 'OPTIONS'].includes(route.request().method())) return route.continue();
    return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
  });
  await page.route('**/js/quiz/interview*.js', route => route.fulfill({
    contentType: 'text/javascript',
    body: fs.readFileSync(path.resolve(__dirname,
      '../modules/quiz-app/src/main/resources/static/js/quiz/interview.js'), 'utf8'),
  }));
});

for (const changeSelection of [false, true]) {
  test(`lost successful response replays the recorded choice: changed selection=${changeSelection}`, async ({ page }) => {
    const requests = [];
    const journal = [];
    let correctOptionId;
    await page.route('**/api/answer', async route => {
      const payload = route.request().postDataJSON();
      requests.push(payload);
      if (requests.length === 1) {
        // Model a committed wrong answer whose successful response was lost.
        journal.push({ questionId: payload.questionId, optionId: payload.optionId,
          clientAttemptId: payload.clientAttemptId, correct: false });
        return route.abort('failed');
      }
      const recorded = journal.find(attempt => attempt.clientAttemptId === payload.clientAttemptId);
      expect(recorded).toBeDefined();
      return route.fulfill({
        status: 200, contentType: 'application/json', body: JSON.stringify({
          correct: recorded.correct,
          selectedOptionId: recorded.optionId,
          correctOptionId,
          verdictText: 'Неверно. Сохранён ответ A. Правильный ответ B.',
          answerLeadHtml: '<p>Разбор первой сохранённой попытки.</p>',
          nextReviewText: 'сегодня',
          optionExplanations: [],
        }),
      });
    });
    await page.goto('/practice');
    const options = page.locator('#interview-options input');
    const labels = page.locator('#interview-options label');
    await expect(options).toHaveCount(4);
    await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
    const firstOptionId = Number(await options.first().inputValue());
    correctOptionId = Number(await options.nth(1).inputValue());
    await labels.first().click();
    await page.locator('#interview-submit').click();
    await expect.poll(() => requests.length).toBe(1);
    await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
    await expect(page.locator('#interview-submit')).toBeEnabled();
    await expect(options.first()).toBeChecked();
    await expect(page.locator('#result-feedback')).toBeHidden();
    if (changeSelection) {
      await labels.nth(1).click();
      await expect(options.nth(1)).toBeChecked();
    }
    await page.locator('#interview-submit').click();
    await expect(page.locator('#result-feedback .result-wrong')).toBeVisible();
    await expect(page.locator('#result-feedback')).toContainText('Разбор первой сохранённой попытки');
    await expect(options.first()).toBeChecked();
    await expect(options.nth(1)).not.toBeChecked();
    await expect(labels.first()).toHaveClass(/option-wrong/);
    await expect(labels.first()).toContainText('Твой выбор');
    await expect(labels.nth(1)).toHaveClass(/option-correct/);
    if (changeSelection) {
      await expect(page.locator('.result-replay-note')).toHaveText('Повторная отправка: показан ранее сохранённый ответ.');
    } else {
      await expect(page.locator('.result-replay-note')).toHaveCount(0);
    }
    await expect(page.locator('#next-question')).toBeVisible();
    expect(journal).toHaveLength(1);
    expect(journal[0].optionId).toBe(firstOptionId);
    expect(journal[0].correct).toBe(false);
    expect(requests).toHaveLength(2);
    expect(requests[1].clientAttemptId).toBe(requests[0].clientAttemptId);
    expect(requests[1].optionId).toBe(changeSelection ? correctOptionId : firstOptionId);
  });
}

for (const conflict of [
  { code: 'QUESTION_STATE_INVALID', detail: 'Действие невозможно в текущем состоянии — обновите страницу и повторите' },
  { code: 'REVISION_MISMATCH', detail: 'Контент вопроса обновился — обновите страницу и ответьте снова' },
]) {
  test(`answer conflict keeps the current choice and explains ${conflict.code}`, async ({ page }) => {
    const requests = [];
    await page.route('**/api/answer', route => {
      requests.push(route.request().postDataJSON());
      return route.fulfill({ status: 409, contentType: 'application/problem+json', body: JSON.stringify({
        status: 409, title: 'Conflict', errorCode: conflict.code, detail: conflict.detail,
      }) });
    });
    await page.goto('/practice');
    const options = page.locator('#interview-options input');
    await expect(options).toHaveCount(4);
    await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
    await page.locator('#interview-options label').nth(1).click();
    await page.locator('#interview-submit').click();
    await expect(page.locator('[data-notice-host="page"]')).toHaveText(conflict.detail);
    await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
    await expect(page.locator('#interview-submit')).toBeEnabled();
    await expect(options.nth(1)).toBeChecked();
    await expect(options.nth(1)).toBeEnabled();
    await expect(page.locator('#result-feedback')).toBeHidden();
    await expect(page.locator('#next-question')).toBeHidden();
    await expect(page.locator('.option-correct, .option-wrong, .result-replay-note')).toHaveCount(0);
    await page.locator('#interview-submit').click();
    await expect.poll(() => requests.length).toBe(2);
    await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
    expect(requests[1].clientAttemptId).toBe(requests[0].clientAttemptId);
    expect(requests[1].optionId).toBe(requests[0].optionId);
    await expect(page.locator('#result-feedback')).toBeHidden();
  });
}

test('lost successful response followed by content drift shows conflict without a new verdict', async ({ page }) => {
  const requests = [];
  const recorded = [];
  const detail = 'Контент вопроса обновился — обновите страницу и ответьте снова';
  await page.route('**/api/answer', route => {
    const payload = route.request().postDataJSON();
    requests.push(payload);
    if (requests.length === 1) {
      recorded.push({ clientAttemptId: payload.clientAttemptId, optionId: payload.optionId, correct: false });
      return route.abort('failed');
    }
    return route.fulfill({ status: 409, contentType: 'application/problem+json', body: JSON.stringify({
      status: 409, title: 'Conflict', errorCode: 'REVISION_MISMATCH', detail,
    }) });
  });
  await page.goto('/practice');
  const options = page.locator('#interview-options input');
  await expect(options).toHaveCount(4);
  await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
  await page.locator('#interview-options label').first().click();
  await page.locator('#interview-submit').click();
  await expect.poll(() => requests.length).toBe(1);
  await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
  await page.locator('#interview-options label').nth(1).click();
  await page.locator('#interview-submit').click();
  await expect(page.locator('[data-notice-host="page"]')).toHaveText(detail);
  await expect(page.locator('#result-feedback')).toBeHidden();
  await expect(page.locator('#next-question')).toBeHidden();
  await expect(page.locator('.option-correct, .option-wrong, .result-replay-note')).toHaveCount(0);
  await expect(options.nth(1)).toBeChecked();
  await expect(options.nth(1)).toBeEnabled();
  await expect(page.locator('#interview-submit')).toBeEnabled();
  expect(requests).toHaveLength(2);
  expect(requests[1].clientAttemptId).toBe(recorded[0].clientAttemptId);
  expect(recorded).toHaveLength(1);
  expect(recorded[0].correct).toBe(false);
});

test('stale first submit stays ungraded until reload supplies a current revision', async ({ page }) => {
  const requests = [];
  const journal = [];
  const detail = 'Контент вопроса обновился — обновите страницу и ответьте снова';
  await page.route('**/api/answer', route => {
    const payload = route.request().postDataJSON();
    requests.push(payload);
    if (payload.questionRevisionId === 55) {
      return route.fulfill({ status: 409, contentType: 'application/problem+json', body: JSON.stringify({
        status: 409, title: 'Conflict', errorCode: 'REVISION_MISMATCH', detail,
      }) });
    }
    expect(payload.questionRevisionId).toBe(56);
    journal.push(payload);
    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({
      correct: true, selectedOptionId: payload.optionId, correctOptionId: payload.optionId,
      verdictText: 'Верно. Ответ на актуальную версию записан.',
      answerLeadHtml: '<p>Разбор актуального вопроса.</p>', nextReviewText: 'завтра', optionExplanations: [],
    }) });
  });
  await page.goto('/practice');
  const revision = page.locator('#interview-form input[name="questionRevisionId"]');
  await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
  // Model focus-training.html's non-null revision branch. The live fixture may
  // be a legacy question without a revision; no database metadata is changed.
  const renderRevision = value => page.locator('#interview-form').evaluate((form, revisionId) => {
    let input = form.querySelector('input[name="questionRevisionId"]');
    if (!input) {
      input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'questionRevisionId';
      form.appendChild(input);
    }
    input.value = revisionId;
  }, value);
  await renderRevision('55');
  await expect(revision).toHaveValue('55');
  await page.locator('#interview-options label').first().click();
  for (let retry = 0; retry < 2; retry++) {
    await page.locator('#interview-submit').click();
    await expect(page.locator('[data-notice-host="page"]')).toHaveText(detail);
    await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
    await expect(page.locator('#result-feedback')).toBeHidden();
    await expect(page.locator('#next-question')).toBeHidden();
    await expect(page.locator('#interview-options input').first()).toBeChecked();
  }
  expect(journal).toHaveLength(0);
  expect(requests).toHaveLength(2);
  expect(requests[1].clientAttemptId).toBe(requests[0].clientAttemptId);
  await page.reload();
  await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
  // A newly rendered page has a new revision and a new per-page attempt key.
  await renderRevision('56');
  await expect(revision).toHaveValue('56');
  await page.locator('#interview-options label').first().click();
  await page.locator('#interview-submit').click();
  await expect(page.locator('#result-feedback .result-correct')).toBeVisible();
  await expect(page.locator('#result-feedback')).toContainText('Разбор актуального вопроса');
  expect(journal).toHaveLength(1);
  expect(requests).toHaveLength(3);
  expect(journal[0].clientAttemptId).not.toBe(requests[0].clientAttemptId);
});
