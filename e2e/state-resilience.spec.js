const { test, expect } = require('@playwright/test');

// State tests never write an answer, note or session to the running user database.
// Each requested outcome is simulated; all other mutation requests are rejected.
test.beforeEach(async ({ page }) => {
  await page.route('**/*', async (route) => {
    if (['GET', 'HEAD', 'OPTIONS'].includes(route.request().method())) return route.continue();
    return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
  });
});

async function openPractice(page) {
  await page.goto('/practice');
  const options = page.locator('#interview-options input[name="optionId"]');
  test.skip(await options.count() < 2, 'Текущий вопрос не содержит MCQ-вариантов');
  // This title is installed immediately before the keyboard handler.
  await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
  await page.keyboard.press('1');
  await expect(options.first()).toBeChecked();
  return options;
}

async function openNote(page) {
  const outer = page.locator('.question-note-outer');
  if (!(await outer.evaluate(el => el.open))) await outer.locator(':scope > summary').click();
  const editor = page.locator('.question-note-disclosure');
  if (!(await editor.evaluate(el => el.open))) await editor.locator(':scope > summary').click();
  return page.locator('.question-note-form');
}

function successAnswer(payload) {
  return {
    correct: true,
    selectedOptionId: payload.optionId,
    correctOptionId: payload.optionId,
    verdictText: 'Верно. Выбранный ответ правильный.',
    answerLeadHtml: '<p>Смоделированный разбор для проверки состояния.</p>',
    nextReviewText: 'завтра',
    optionExplanations: [],
  };
}

test('MCQ pending freezes selection, prevents duplicates and recovers for retry', async ({ page }) => {
  const requests = [];
  let release;
  const gate = new Promise(resolve => { release = resolve; });
  await page.route('**/api/answer', async route => {
    const payload = route.request().postDataJSON();
    requests.push(payload);
    if (requests.length === 1) {
      await gate;
      return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
    }
    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(successAnswer(payload)) });
  });
  const options = await openPractice(page);
  const firstValue = Number(await options.first().inputValue());
  await page.keyboard.press('Enter');
  await expect.poll(() => requests.length).toBe(1);
  await expect(page.locator('#interview-submit')).toHaveAttribute('aria-busy', 'true');
  for (const option of await options.all()) await expect(option).toBeDisabled();
  // Native click on a disabled radio and repeated Enter must not change the snapshot.
  await options.nth(1).evaluate(el => el.click());
  await page.keyboard.press('Enter');
  await expect(options.first()).toBeChecked();
  expect(requests).toHaveLength(1);
  expect(requests[0].optionId).toBe(firstValue);
  release();
  await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
  await expect(page.locator('#interview-submit')).toBeEnabled();
  await expect(options.first()).toBeChecked();
  for (const option of await options.all()) await expect(option).toBeEnabled();
  await expect(page.locator('#result-feedback')).toBeHidden();
  await page.locator('#interview-submit').click();
  await expect(page.locator('#result-feedback')).toBeFocused();
  await expect(page.locator('#result-feedback')).toContainText('Верно');
  await expect(page.locator('#result-feedback [role="tabpanel"]')).toHaveCount(0);
  await expect(page.locator('#next-question')).toBeVisible();
  await page.keyboard.press('Enter');
  expect(requests).toHaveLength(2);
  expect(requests[1].clientAttemptId).toBe(requests[0].clientAttemptId);
});

test('quiz shortcuts preserve editor, composition and secondary-control context', async ({ page }) => {
  const options = await openPractice(page);
  const summary = page.locator('.question-note-outer > summary');
  await summary.focus();
  await page.keyboard.press('Escape');
  await page.keyboard.press('ArrowDown');
  await page.keyboard.press('2');
  await expect(summary).toBeFocused();
  await expect(options.first()).toBeChecked();
  await page.keyboard.press('Enter');
  await expect(page.locator('.question-note-outer')).toHaveAttribute('open');

  const note = await openNote(page);
  const textarea = note.locator('textarea');
  await textarea.fill('Черновик');
  await page.keyboard.press('End');
  await page.keyboard.type('1234');
  await page.keyboard.press('Enter');
  await page.keyboard.press('Escape');
  await expect(textarea).toHaveValue('Черновик1234\n');
  await expect(options.first()).toBeChecked();

  await page.evaluate(() => {
    const editable = document.createElement('div');
    editable.id = 'test-editor';
    editable.contentEditable = 'true';
    editable.innerHTML = '<span>Редактор</span>';
    document.querySelector('#main-content').appendChild(editable);
    editable.focus();
  });
  await page.keyboard.press('2');
  await page.keyboard.press('?');
  await expect(page.locator('.kbd-help-overlay')).toBeHidden();
  await expect(options.first()).toBeChecked();
  await options.first().focus();
  await options.first().dispatchEvent('keydown', { key: '2', isComposing: true });
  await options.first().dispatchEvent('keydown', { key: '2', keyCode: 229 });
  await options.first().dispatchEvent('keydown', { key: '2', ctrlKey: true });
  await options.first().dispatchEvent('keydown', { key: '2', metaKey: true });
  await expect(options.first()).toBeChecked();
  await page.keyboard.press('2');
  await expect(options.nth(1)).toBeChecked();
  await page.keyboard.press('Escape');
  await expect(page.locator('input[name="optionId"]:checked')).toHaveCount(0);
});

test('first note save updates nested disclosure and preserves edits made in flight', async ({ page }) => {
  const requests = [];
  let release;
  const gate = new Promise(resolve => { release = resolve; });
  await page.route('**/api/bookmark/note', async route => {
    const payload = route.request().postDataJSON();
    requests.push(payload);
    if (requests.length === 1) await gate;
    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ note: payload.note }) });
  });
  await openPractice(page);
  const questionId = await page.locator('#interview-form input[name="questionId"]').inputValue();
  const form = await openNote(page);
  // Local empty-note presentation is synthetic; the real note is never changed.
  await page.locator('.question-note').evaluate(el => el.querySelector('.question-note-text')?.remove());
  const textarea = form.locator('textarea');
  await textarea.fill('Первая мысль');
  await form.locator('button[type="submit"]').click();
  await expect.poll(() => requests.length).toBe(1);
  await expect(form.locator('button')).toHaveAttribute('aria-busy', 'true');
  await textarea.fill('Новая мысль во время сохранения');
  await form.dispatchEvent('submit');
  expect(requests).toHaveLength(1);
  release();
  await expect(form.locator('.bookmark-save-status')).toContainText('Новые изменения ещё не сохранены');
  await expect(textarea).toHaveValue('Новая мысль во время сохранения');
  await expect(page.locator('.question-note-text')).toHaveText('Первая мысль');
  await expect(page.locator('.question-note-outer-mark')).toHaveText('есть');
  await expect(form.locator('button')).not.toHaveAttribute('aria-busy');
  await expect(page.locator('#interview-form input[name="questionId"]')).toHaveValue(questionId);
  await form.locator('button[type="submit"]').click();
  await expect(form.locator('.bookmark-save-status')).toHaveText('Сохранено');
  await expect(page.locator('.question-note-text')).toHaveText('Новая мысль во время сохранения');
  expect(requests).toHaveLength(2);
  await textarea.fill('Следующий несохранённый черновик');
  await expect(form.locator('.bookmark-save-status')).toHaveText('Есть несохранённые изменения');
});

for (const failure of ['server', 'network']) {
  test(`note ${failure} failure retains draft and allows retry`, async ({ page }) => {
    let requests = 0;
    await page.route('**/api/bookmark/note', async route => {
      requests++;
      if (requests === 1) {
        if (failure === 'network') return route.abort('failed');
        return route.fulfill({ status: 500, contentType: 'application/json', body: '{}' });
      }
      return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ note: route.request().postDataJSON().note }) });
    });
    await openPractice(page);
    const form = await openNote(page);
    await form.locator('textarea').fill('Черновик, который нельзя потерять');
    await form.locator('button[type="submit"]').click();
    await expect(form.locator('.bookmark-save-status')).toContainText('Текст остался в поле');
    await expect(form.locator('textarea')).toHaveValue('Черновик, который нельзя потерять');
    await expect(form.locator('button')).not.toHaveAttribute('aria-busy');
    await form.locator('button[type="submit"]').click();
    await expect(form.locator('.bookmark-save-status')).toHaveText('Сохранено');
    expect(requests).toBe(2);
  });
}
