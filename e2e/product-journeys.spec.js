const { test, expect } = require('@playwright/test');
const path = require('path');

test.skip(process.env.E2E_ISOLATED_DATA !== '1'
  || !/^http:\/\/(?:localhost|127\.0\.0\.1):8081\/?$/.test(process.env.E2E_BASE_URL || ''),
  'These journeys save answers and notes only in the isolated database');

const output = path.resolve(__dirname, process.env.E2E_EVIDENCE_DIR || '../output/playwright/redesign-20260905-current/final-20260908');
const topic = 'frameworks/spring/spring-graphql-interview';

async function start(page, count = '3') {
  await page.goto('/train?mode=MARATHON&topic=' + encodeURIComponent(topic));
  await expect(page.locator('input[type="radio"][name="mode"][value="MARATHON"]')).toBeChecked();
  await page.locator('input[name="count"]').fill(count);
  await page.locator('#session-start-btn').click();
  await expect(page.locator('#interview-form')).toBeVisible();
}

async function openDetails(locator) {
  if (await locator.getAttribute('open') === null) await locator.locator(':scope > summary').click();
}

async function finish(page) {
  await openDetails(page.locator('.focus-session-more'));
  await page.locator('form[action="/finish"] button').click();
  await page.getByRole('alertdialog').getByRole('button', { name: 'Подтвердить', exact: true }).click();
  await expect(page).toHaveURL(/\/session-summary(?:\?|$)/);
}

async function reachable(locator) {
  await locator.scrollIntoViewIfNeeded();
  expect(await locator.evaluate(el => {
    const r = el.getBoundingClientRect();
    const top = document.elementFromPoint(r.x + r.width / 2, r.y + r.height / 2);
    return el === top || el.contains(top);
  }), 'the action must remain above the mobile navigation').toBe(true);
}

test('mobile home resume → answer → save note → next → partial summary → saved question and Back', async ({ page }, info) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await start(page);
  const question = page.locator('#interview-form input[name="questionId"]');
  const id = await question.inputValue();
  await page.goto('/');
  await expect(page.locator('.home-session-context')).toContainText('Интенсив · Spring for GraphQL');
  await expect(page.locator('.home-session-progress')).toHaveText('В процессе · отвечено 0 из 3');
  await page.screenshot({ path: path.join(output, `home-active-${info.project.name}.png`) });
  await page.locator('.home-primary-link').click();
  await expect(question).toHaveValue(id);
  await openDetails(page.locator('.focus-session-more'));
  await page.locator('.rail-pause button').click();
  await page.goto('/');
  await expect(page.locator('.home-session-context')).toContainText('Интенсив · Spring for GraphQL');
  await expect(page.locator('.home-session-progress')).toHaveText('На паузе · отвечено 0 из 3');
  await page.screenshot({ path: path.join(output, `home-paused-${info.project.name}.png`) });
  await reachable(page.locator('.home-recent a'));
  const resume = page.locator('.home-primary-form button');
  await reachable(resume);
  await resume.click();
  await expect(question).toHaveValue(id);

  await page.locator('#interview-options label').first().click();
  await reachable(page.locator('#interview-submit'));
  await page.locator('#interview-submit').click();
  await expect(page.locator('#result-feedback')).toBeVisible();
  await expect(page.locator('#result-feedback')).toBeFocused();
  await page.screenshot({ path: path.join(output, `journey-mobile-feedback-${info.project.name}.png`) });

  await openDetails(page.locator('.question-note-outer'));
  await openDetails(page.locator('.question-note-disclosure'));
  const form = page.locator('.question-note-form');
  const note = 'journey20260908' + info.project.name + ' — мысль после ответа';
  await form.locator('textarea').fill(note);
  await reachable(form.locator('button[type="submit"]'));
  await form.locator('button[type="submit"]').click();
  await expect(form.locator('.bookmark-save-status')).toHaveText('Сохранено');
  await expect(question).toHaveValue(id);
  await reachable(page.locator('#next-question'));
  await page.locator('#next-question').click();
  await expect(question).not.toHaveValue(id);
  await finish(page);
  await expect(page.locator('.page-head-lead')).toContainText('Завершена досрочно');
  await expect(page.locator('.summary-facts > div').first().locator('dd')).toHaveText('1 из 3');
  await expect(page.locator('.summary-hero-count')).toContainText(/из\s+1/);
  await reachable(page.locator('.summary-actions .next-btn'));
  await page.screenshot({ path: path.join(output, `summary-partial-${info.project.name}.png`) });

  const filtered = '/bookmarks?q=' + encodeURIComponent('journey20260908' + info.project.name) + '&note=yes&sort=recent';
  await page.goto(filtered);
  const card = page.locator('#q-' + id);
  await expect(card.locator('.bookmark-note-text')).toHaveText(note);
  await card.locator('.bookmark-question').click();
  await expect(question).toHaveValue(id);
  await page.goBack();
  await expect(page).toHaveURL(new RegExp('/bookmarks\\?q=journey20260908' + info.project.name));
  await expect(card.locator('.bookmark-note-text')).toHaveText(note);
});

test('topic search → configured session → question → browser Back preserves topic and search', async ({ page }) => {
  await page.goto('/topics?q=GraphQL&state=all');
  await page.locator('a[href*="/train?topic="]').first().click();
  await expect(page.locator('select[name="topic"]')).toHaveValue(topic);
  const mode = page.locator('input[type="radio"][name="mode"][value="FLASHCARD"]');
  await page.locator('.mode-card').filter({ has: mode }).click();
  await page.locator('input[name="count"]').fill('3');
  await page.locator('#session-start-btn').click();
  await expect(page.locator('.focus-question')).toBeVisible();
  await page.goBack();
  await expect(page.locator('select[name="topic"]')).toHaveValue(topic);
  await expect(mode).toBeChecked();
  await page.goBack();
  await expect(page).toHaveURL(/\/topics\?q=GraphQL&state=all/);
  await expect(page.locator('input[name="q"]')).toHaveValue('GraphQL');
});

test('ending an unanswered session explains zero answers without rewarding accuracy', async ({ page }, info) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await start(page);
  await finish(page);
  await expect(page.locator('.summary-headline')).toHaveText('Сессия завершена без ответов');
  await expect(page.locator('.summary-facts > div').first().locator('dd')).toHaveText('0 из 3');
  await expect(page.locator('.summary-hero')).not.toContainText('Все ответы верны');
  await reachable(page.locator('.summary-actions .next-btn'));
  await page.screenshot({ path: path.join(output, `summary-empty-${info.project.name}.png`) });
});

test('a real wrong answer exposes the explanation, next action and later review', async ({ page }, info) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/practice?questionId=' + (process.env.E2E_WRONG_QUESTION_ID || '3183'));
  // IDs are supplied from the isolated database; the option must actually be incorrect.
  const wrongOption = process.env.E2E_WRONG_OPTION_ID || '12729';
  await page.locator('#interview-options label[data-option-id="' + wrongOption + '"]').click();
  await page.locator('#interview-submit').click();
  await expect(page.locator('#result-feedback .result-wrong')).toBeVisible();
  await expect(page.locator('#result-feedback')).toContainText('правильн');
  await reachable(page.locator('#next-question'));
  await page.screenshot({ path: path.join(output, `wrong-answer-mobile-${info.project.name}.png`) });
  await page.locator('#next-question').click();
  await expect(page.locator('#interview-form')).toBeVisible();
  await page.goto('/review?topic=' + encodeURIComponent(process.env.E2E_WRONG_TOPIC || 'data-engineering/data-lake-lakehouse-interview'));
  await expect(page.locator('.focus-question')).toBeVisible();
  await expect(page.locator('body')).toHaveAttribute('class', /focus-page/);
});

test('stats keeps the topic filter through table sorting and explains the wider dynamics scope', async ({ page }) => {
  await page.goto('/stats');
  await page.locator('#stats-tab-topics').click();
  await openDetails(page.locator('.filter-bar-disclosure'));
  await page.locator('select[name="topic"]').selectOption(topic);
  await page.locator('.filter-bar-actions button').click();
  await expect(page.locator('select[name="topic"]')).toHaveValue(topic);
  await openDetails(page.locator('.topic-detail-disclosure'));
  await expect(page.locator('.topic-table tbody tr')).toHaveCount(1);
  await expect(page.locator('.topic-table tbody')).toContainText('GraphQL');
  await page.locator('.topic-table thead .col-sort').first().click();
  await expect(page.locator('select[name="topic"]')).toHaveValue(topic);
  await openDetails(page.locator('.topic-detail-disclosure'));
  await expect(page.locator('.topic-table tbody tr')).toHaveCount(1);
  const disclosure = page.locator('#stats-panel-topics .chart-table-disclosure').first();
  await openDetails(disclosure);
  await expect(disclosure.locator('table')).toBeVisible();
  await expect(disclosure.locator('tbody')).toContainText('GraphQL');
  await page.locator('#stats-tab-dynamics').click();
  await expect(page.locator('#stats-panel-dynamics .stats-scope-note')).toContainText('отбор её не сужает');
  await page.reload();
  await expect(page.locator('#stats-tab-dynamics')).toHaveAttribute('aria-selected', 'true');
  await expect(page.locator('#stats-panel-dynamics .stats-scope-note')).toBeVisible();
});

test('the chart table remains populated when the chart library cannot load', async ({ page }) => {
  await page.route('**/chart.umd*.js', route => route.abort('failed'));
  await page.setViewportSize({ width: 1280, height: 400 });
  await page.goto('/stats#topic-table-section');
  await page.locator('#stats-tab-topics').click();
  const disclosure = page.locator('#stats-panel-topics .chart-table-disclosure').first();
  await openDetails(disclosure);
  await expect(disclosure.locator('table')).toBeVisible();
  await expect(disclosure.locator('tbody tr').first()).toBeVisible();
  await expect(page.locator('#topicProgressChartFallback')).toContainText('таблицу ниже');
});

test('the mobile compact chart table keeps the complete topic and named values', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/stats?topic=' + encodeURIComponent(topic));
  await page.locator('#stats-tab-topics').click();
  const disclosure = page.locator('#stats-panel-topics .chart-table-disclosure').first();
  await openDetails(disclosure);
  const table = disclosure.locator('table');
  await expect(table.locator('tbody th[scope="row"]')).toHaveText('Spring for GraphQL');
  expect((await table.locator('tbody th').boundingBox()).width).toBeGreaterThan(150);
  await expect(table.locator('tbody td[data-label="Выучено"]')).toHaveCount(1);
  expect(await table.evaluate(el => el.parentElement.scrollWidth - el.parentElement.clientWidth)).toBeLessThanOrEqual(1);
});
