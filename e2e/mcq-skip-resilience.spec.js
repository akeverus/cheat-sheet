const { test, expect } = require('@playwright/test');
const fs = require('fs');
const path = require('path');

// Use the running app for GETs only. Session controls and API outcomes are
// synthetic so this regression never starts a session or records an answer.
test.beforeEach(async ({ page }) => {
  await page.route('**/*', route => {
    if (['GET', 'HEAD', 'OPTIONS'].includes(route.request().method())) return route.continue();
    return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
  });
  await page.route('**/js/quiz/interview*.js', route => route.fulfill({
    contentType: 'text/javascript',
    body: fs.readFileSync(path.resolve(__dirname,
      '../modules/quiz-app/src/main/resources/static/js/quiz/interview.js'), 'utf8'),
  }));
  await page.route('**/practice*', async route => {
    if (route.request().method() !== 'GET') return route.fallback();
    const response = await route.fetch();
    const html = await response.text();
    const formStart = html.indexOf('id="interview-form"');
    expect(formStart, 'the running app must serve an MCQ form').toBeGreaterThan(-1);
    const formEnd = html.indexOf('</form>', formStart);
    const form = html.slice(formStart, formEnd);
    // Match focus-training.html's active-session control before JS initializes.
    const skip = form.includes('class="skip-btn"') ? ''
      : '<div class="skip-action"><button type="submit" class="skip-btn" '
        + 'formnovalidate formaction="/skip">Не знаю ответа</button></div>';
    await route.fulfill({ response, body: html.slice(0, formEnd) + skip + html.slice(formEnd) });
  });
});

async function openQuestion(page) {
  await page.goto('/practice');
  await expect(page.locator('#interview-options input')).toHaveCount(4);
  await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
  return page.locator('#interview-form .skip-btn');
}

function answer(payload) {
  return {
    correct: true,
    selectedOptionId: payload.optionId,
    correctOptionId: payload.optionId,
    verdictText: 'Верно. Выбранный ответ правильный.',
    answerLeadHtml: '<p>Пояснение для проверки перехода к следующему вопросу.</p>',
    nextReviewText: 'завтра',
    optionExplanations: [],
  };
}

async function dispatchSkip(page) {
  return page.locator('#interview-form').evaluate(form => !form.dispatchEvent(new SubmitEvent('submit', {
    bubbles: true, cancelable: true, submitter: form.querySelector('.skip-btn'),
  })));
}

async function beginNativeSkip(page) {
  // Cancel navigation after the product handler; no POST reaches the DB.
  return page.locator('#interview-form').evaluate(form => {
    let allowed = false;
    const observe = event => { allowed = !event.defaultPrevented; event.preventDefault(); };
    form.addEventListener('submit', observe, { once: true });
    form.requestSubmit(form.querySelector('.skip-btn'));
    return allowed;
  });
}

test('pending answer blocks skip, failure restores it, accepted answer keeps it blocked', async ({ page }) => {
  const requests = [];
  let release;
  const pending = new Promise(resolve => { release = resolve; });
  await page.route('**/api/answer', async route => {
    const payload = route.request().postDataJSON();
    requests.push(payload);
    if (requests.length === 1) {
      await pending;
      return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
    }
    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(answer(payload)) });
  });
  const skip = await openQuestion(page);
  await expect(skip).toBeEnabled();
  await page.keyboard.press('1');
  await page.keyboard.press('Enter');
  await expect.poll(() => requests.length).toBe(1);
  try {
    await expect(skip).toBeDisabled();
    expect(await dispatchSkip(page)).toBe(true);
  } finally {
    release();
  }
  await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
  await expect(skip).toBeEnabled();
  await expect(page.locator('#interview-options input').first()).toBeChecked();
  await page.locator('#interview-submit').click();
  await expect(page.locator('#result-feedback')).toBeVisible();
  await expect(page.locator('#result-feedback')).toContainText('Пояснение для проверки');
  await expect(page.locator('#next-question')).toBeVisible();
  await expect(skip).toBeDisabled();
  expect(await dispatchSkip(page)).toBe(true);
  expect(requests).toHaveLength(2);
  expect(requests[1].clientAttemptId).toBe(requests[0].clientAttemptId);
  await page.locator('#next-question').click();
  await expect(page.locator('#result-feedback')).toBeHidden();
  await expect(page.locator('#interview-form .skip-btn')).toBeEnabled();
});

test('native skip works without selection and excludes a competing answer or second skip', async ({ page }) => {
  let answerRequests = 0;
  await page.route('**/api/answer', route => {
    answerRequests++;
    return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
  });
  await openQuestion(page);
  expect(await beginNativeSkip(page)).toBe(true);
  expect(await dispatchSkip(page)).toBe(true);
  await page.keyboard.press('1');
  await page.keyboard.press('Enter');
  expect(answerRequests).toBe(0);
  await expect(page.locator('#interview-submit')).not.toHaveAttribute('aria-busy');
});

test('restored page releases the native navigation lock but preserves an accepted answer', async ({ page }) => {
  await page.route('**/api/answer', route => route.fulfill({
    status: 200, contentType: 'application/json', body: JSON.stringify(answer(route.request().postDataJSON())),
  }));
  const skip = await openQuestion(page);
  expect(await beginNativeSkip(page)).toBe(true);
  await page.evaluate(() => window.dispatchEvent(new PageTransitionEvent('pageshow', { persisted: false })));
  expect(await dispatchSkip(page)).toBe(true);
  // Model the browser's return from BFCache after native navigation.
  await page.evaluate(() => window.dispatchEvent(new PageTransitionEvent('pageshow', { persisted: true })));
  await page.keyboard.press('1');
  await page.keyboard.press('Enter');
  await expect(page.locator('#result-feedback')).toBeVisible();
  await page.evaluate(() => window.dispatchEvent(new PageTransitionEvent('pageshow', { persisted: true })));
  await expect(skip).toBeDisabled();
  expect(await dispatchSkip(page)).toBe(true);
});
