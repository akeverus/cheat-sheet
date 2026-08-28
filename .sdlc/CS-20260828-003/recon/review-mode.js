/*
 * CS-20260828-003 Блок E — `/review` как режим, не раздел (§18.6).
 * Проверяет: активный пункт навигации на /review, маркер onlyWrong в DOM,
 * счётчик "Ошибок к разбору" до/после ответа заведомо неверным вариантом.
 */
const { chromium } = require('playwright');
const { execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const BASE = process.env.E2E_BASE_URL || 'http://localhost:8091';
const DATA_DIR = path.resolve(__dirname, '..', 'data');
const SHOTS_DIR = path.resolve(__dirname, '..', 'shots');

function dbQuery(sql) {
  return execFileSync('docker', ['exec', 'quiz-postgres', 'psql', '-U', 'interview', '-d', 'interview_recon', '-tAc', sql])
    .toString().trim();
}

function extractErrorCount(html) {
  const m = html.match(/Ошибок к разбору<\/span>\s*<span>(\d+)<\/span>/);
  return m ? Number(m[1]) : null;
}

(async () => {
  const browser = await chromium.launch();
  const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page = await context.newPage();

  const result = {};

  // --- 1. /review отвечает тем же шаблоном, что и /, форсирует onlyWrong ---
  const respReview = await page.goto(BASE + '/review', { waitUntil: 'domcontentloaded' });
  result.reviewStatus = respReview.status();
  const html1 = await page.content();
  result.onlyWrongMarkerPresent = html1.includes('name="onlyWrong" value="true"');

  // --- 2. Ровно один активный пункт навигации, и это FOCUS ---
  const activeLinks = await page.locator('a.active[aria-current="page"]').all();
  const activeTexts = [];
  for (const l of activeLinks) activeTexts.push((await l.textContent() || '').trim());
  result.activeNavCount = activeLinks.length;
  result.activeNavLabels = activeTexts;
  result.activeIsTraining = activeTexts.every((t) => t.includes('Тренировка'));

  // --- 3. Счётчик "Ошибок к разбору" ДО ---
  const errorsBefore = extractErrorCount(html1);
  result.errorsBefore = errorsBefore;
  await page.screenshot({ path: path.join(SHOTS_DIR, 'review-mode-before.png'), fullPage: false });

  // --- 4. Определить заведомо неверный вариант для показанного вопроса ---
  const questionIdInput = await page.locator('input[name="questionId"]').first();
  const questionId = await questionIdInput.getAttribute('value').catch(() => null);
  let wrongOptionId = null;
  let dbError = null;
  if (questionId) {
    try {
      const out = dbQuery(`SELECT id FROM answer_options WHERE question_id=${Number(questionId)} AND is_correct=0 ORDER BY display_order LIMIT 1`);
      wrongOptionId = out ? Number(out) : null;
    } catch (e) {
      dbError = String(e.message || e);
    }
  }
  result.reviewQuestionId = questionId;
  result.wrongOptionId = wrongOptionId;
  if (dbError) result.dbError = dbError;

  // --- 5. Ответить заведомо неверным вариантом через реальную форму ---
  if (wrongOptionId) {
    await page.locator(`label[data-option-id="${wrongOptionId}"] input[type="radio"]`).check({ force: true });
    await page.locator('#interview-submit').click({ force: true });
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(500);
  }

  // --- 6. Счётчик ПОСЛЕ — снова открыть /review ---
  const respAfter = await page.goto(BASE + '/review', { waitUntil: 'domcontentloaded' });
  const html2 = await page.content();
  const errorsAfter = extractErrorCount(html2);
  result.errorsAfter = errorsAfter;
  result.errorsDelta = (errorsBefore !== null && errorsAfter !== null) ? errorsAfter - errorsBefore : null;
  await page.screenshot({ path: path.join(SHOTS_DIR, 'review-mode-after.png'), fullPage: false });

  await browser.close();
  fs.writeFileSync(path.join(DATA_DIR, 'review-mode.json'), JSON.stringify(result, null, 2));
  console.log(JSON.stringify(result, null, 2));
})();
