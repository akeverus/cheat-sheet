const { test, expect } = require('@playwright/test');

// Opt-in read-only live fixture IDs, audited in PostgreSQL before the run.
const versioned = process.env.E2E_REVISIONED_QUESTION_ID;
const expectedRevision = process.env.E2E_EXPECTED_REVISION_ID;
const unversioned = process.env.E2E_UNVERSIONED_QUESTION_ID;
test.skip(!versioned || !expectedRevision || !unversioned,
  'Requires audited existing versioned/unversioned question IDs; never creates metadata');

test.beforeEach(async ({ page }) => {
  await page.route('**/*', route => {
    if (['GET', 'HEAD', 'OPTIONS'].includes(route.request().method())) return route.continue();
    return route.fulfill({ status: 503, contentType: 'application/json', body: '{}' });
  });
});

for (const hasRevision of [true, false]) {
  test(`real SSR revision metadata reaches AJAX payload: present=${hasRevision}`, async ({ page }) => {
    let payload;
    await page.route('**/api/answer', route => {
      payload = route.request().postDataJSON();
      return route.fulfill({ status: 409, contentType: 'application/problem+json', body: JSON.stringify({
        status: 409, errorCode: 'REVISION_MISMATCH', detail: 'Контент вопроса обновился — обновите страницу и ответьте снова',
      }) });
    });
    const questionId = hasRevision ? versioned : unversioned;
    // HTML and JavaScript are served by the real runtime. No response or DOM
    // revision injection, source replacement, or request payload patching.
    const response = await page.goto('/practice?questionId=' + encodeURIComponent(questionId));
    expect(response.ok()).toBe(true);
    await expect(page.locator('#interview-form input[name="questionId"]')).toHaveValue(questionId);
    const revision = page.locator('#interview-form input[name="questionRevisionId"]');
    await expect(revision).toHaveCount(hasRevision ? 1 : 0);
    if (hasRevision) await expect(revision).toHaveValue(expectedRevision);
    await expect(page.locator('#interview-options input')).toHaveCount(4);
    await expect(page.locator('#interview-submit')).toHaveAttribute('title', /Клавиши/);
    await page.locator('#interview-options label').first().click();
    await page.locator('#interview-submit').click();
    await expect.poll(() => payload !== undefined).toBe(true);
    expect(payload.questionId).toBe(Number(questionId));
    if (hasRevision) {
      expect(payload.questionRevisionId).toBe(Number(expectedRevision));
    } else {
      expect(payload).not.toHaveProperty('questionRevisionId');
    }
    await expect(page.locator('#result-feedback')).toBeHidden();
  });
}
