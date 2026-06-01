# SERVER CONTRACT — что новый слой представления ОБЯЗАН сохранить

> Источник правды для пересборки. Java-контроллеры НЕ трогаем (кроме осознанных
> IA-правок). Новые шаблоны/CSS/JS должны удовлетворять этому контракту, иначе
> рантайм-500 или тихо сломанный квиз. Сгенерировано из server-contract-map workflow.

## 1. View-имена (рендерятся контроллерами) — менять только через `MvcNavigationService`

`focus-training` · `settings` · `result` · `stats` · `session-summary` · `error`

Литералы вью живут в `feature/interview/controller/MvcNavigationService.java`
(`focusView`/`settingsView`/`resultView`/`statsView`/`sessionSummaryView` + редиректы).
Model-атрибуты централизованы в `api/mapper/view/MvcModelAttributeMapper.java`.
**Переименовать шаблон = править MvcNavigationService, НЕ контроллер.**

## 2. Страницы, их вью и model-атрибуты

| Route | View | Ключевые model-атрибуты |
|---|---|---|
| `GET /` `/training` `/review` | `focus-training` | stats, topics, groups, selectedGroup, filter, mode, interviewSession, weakTopics, **current** (вопрос+options), generationUnavailable, studyLearnPhase, **flashcardMode**, flashcardRevealed, diagram?, studyAnswerHtml?, difficulty?, progressPercent, reviewMode, focusModeChipText, focusModeHintText, focusEmptyRetryHref, focusEmptyRetryText, aiEnabled |
| `GET /settings` | `settings` | stats, topics, groups, selectedGroup, filter, mode, interviewSession, weakTopics |
| `GET /stats` | `stats` | stats, topics, groups, selectedGroup, filter, searchQuery, searchResults, topicStats, **topicStatsJson**, coverageGaps, reviewForecast, reviewForecastMax |
| `GET /session-summary` | `session-summary` | **summary** (нет summary в сессии → redirect:/) |
| `POST /answer` | `result` | **result**, **answerHtml**, answerDisplayMode, stats, …filter…, diagram?, relatedQuestions, aiEnabled, reviewState |
| `POST /start` | redirect:/ | форма StartSessionRequest |
| `POST /study-confirm` | redirect:/ | — |
| `POST /flashcard-reveal` | redirect:/ | — |
| `POST /flashcard-grade` | redirect:/ | questionId, grade(0..5) |
| `POST /finish` | redirect:/session-summary | — |
| `POST /settings/reset-options` | redirect:/settings | flash resetDeleted |
| (exc) error | `error` | status (404/500/503); generic Exception→500 |

`?` = добавляется только когда non-null. **flashcardMode = sessionFlashcardMode ‖ noAiFlashcard** —
т.е. при seed-first без AI (дефолт) большинство вопросов идут как ФЛЕШКАРТЫ; MCQ-ветка
(/answer→result) включается только когда у вопроса ЕСТЬ варианты. Обе ветки — первоклассные.

## 3. Формы (имена полей = поля DTO, не переименовывать)

- **`#interview-form`** `POST /answer` (только не-flashcard): radio `name="optionId"` `value=opt.id`
  внутри `label[data-option-id=opt.id] > span(opt.optionText)`; hidden `questionId`, `topic`,
  `group`, `important=true?`, `onlyWrong=true?`, `shuffle=true?`, `weakTopics=true?`,
  `ordered`, плюс `confidence` (после ответа).
- **`#filters-form`** `GET /settings`: select `topic`,`group`,`ordered`; checkbox `important`,
  `onlyWrong`, `shuffle`(id `shuffle-checkbox`), `weakTopics` (все value=true).
- **`#session-form`** `POST /start`: select `mode`, number `count`(1..200, def 20) + зеркальные
  hidden important/onlyWrong/shuffle/weakTopics (app.js `initSessionFormSync` доливает при submit).
- **flashcard-grade** `POST /flashcard-grade`: hidden `questionId`, кнопки `name="grade"`
  value `1|2|3|4` (`.flashcard-grade-btn`). **flashcard-reveal** `POST /flashcard-reveal`.
- **reset-options** `POST /settings/reset-options` (data-confirm).

## 4. REST API (питает JS; всё form-urlencoded/query, НЕ JSON body; credentials same-origin)

| Endpoint | JS читает поля |
|---|---|
| `POST /api/answer` | correct, correctOptionId, selectedOptionId, **answerHtml**(sanitized HTML), optionExplanations[]{id,**explanationHtml**,correct}, relatedQuestions[]{id,text,topic}, session{index,total,correct,wrong,finished} |
| `GET /api/streak` | streak, goal, today, goalReached |
| `POST /api/hint` | level, hint (maxLevel из data-attr) |
| `POST /api/confidence` | только resp.ok (тело не читается) |
| `POST /api/wrong-feedback` | available, feedback |
| `GET /api/comparison` | comparison (JSON-СТРОКА → parse → criteria[]{criterion,selected,correct}) |
| `GET /api/takeaway` | takeaway |
| `GET /api/code-trace` | trace (JSON-СТРОКА → parse → steps[]{step,line,state,explanation}) |
| `POST /api/favorite` | favorite |
| `POST /api/regenerate` | только resp.ok |
| `GET /api/stats` | total, due, learned, correct, wrong (accuracy считается клиентом) |

Объявлены, но НЕ дёргаются: `/api/next` (следующий вопрос = переход по ссылке
`buildNextQuestionHref`), `/api/topic-stats` (берётся из inline-JSON, см. §6). Контракт
`TopicStatsResponse` всё равно нужен stats.js. Admin/export — не из JS.

## 5. DOM-хуки (app.js / stats.js привязаны — переименование = тихий разлом)

**IDs (обязательные):** `#interview-form` `#interview-options` `#interview-submit`(alias `#submitBtn`)
`#result-feedback` `#next-question` `#extra-analysis-toggle` `#answer-flow-hint` `#answer-flow-steps`
`#interview-alert` `#codeBlock` `#toggle-code-btn` `#browse-reveal-btn` `#browse-answer`
`#question-timer` `#action-footer` `#left-sidebar-content` `#sidebar-collapse-toggle`
`#sidebar-accuracy-bar-fill` `#filters-form` `#session-form` `#streak-bar` `#streak-days`
`#streak-progress-fill` `#streak-count` `#btn-favorite` `#btn-regenerate`
`#extra-analysis-toggle-result` `#result-extra-analysis` `#result-related-questions`.

**Stats (stats.js):** `#topic-stats-data` `#topicProgressChart(+Fallback)` `#topicAccuracyChart(+Fallback)`
`#topic-table` `#table-sort-status` `#topic-table-wrap` `#topic-table-expander`.

**Опциональные (app.js no-op если нет — добавить если хотим фичу):** `#btn-hint` `#hint-container`
`#instant-mode-toggle` `#hard-mode-toggle` `#review-mode-toggle` `#adaptive-mode-toggle` `#timer-select`.

**Классы:** `.btn-favorite` `.btn-regenerate` `.session-progress(-track/-fill)` `.answer-flow-step`
`.flashcard-phase` `.flashcard-reveal-btn` `.flashcard-grade-btn` `.grade-1..4` `.question-support`
`.question-code` `.kbd-help-overlay/-modal/-close` `.confidence-btn` `.result-correct/.result-wrong`
`.markdown-content` `.mermaid` `.option-correct/.option-wrong/.option-dimmed/.option-other`
`.loading-placeholder` `.sortable` `.chart-fallback` `.skip-link` `.visually-hidden`.

**data-*:** `data-option-id` `data-question-id` `data-selected-option-id` `data-correct`
`data-progress` `data-stat-field`(total/due/learned/correct/wrong/accuracy) `data-field`(index/total/correct/wrong)
`data-step`(selected/checking/result/explanation) `data-hint-level` `data-grade` `data-loaded`
`data-confirm` `data-submitted` `data-col` `data-sort-label` `data-dir` `data-base-aria-label`
`data-collapse-rows` `data-rendered` `data-sidebar-form`.

**Submit-once guard (NAV_FORM_SELECTOR):** `#session-form`, `form[action$=/flashcard-grade]`,
`form[action$=/flashcard-reveal]`. **localStorage:** `quiz.learning.prefs.v2`, `quiz.ux.metrics.v2`,
`quiz.session.count`, `theme`.

## 6. Inline server→JS JSON

`#topic-stats-data`: `<script type="application/json" id="topic-stats-data" th:inline="text">[(${topicStatsJson})]</script>`
(stats.html). stats.js парсит textContent как массив `{topic,total,learned,correct,wrong,due,regenSum}`.
**Не оборачивать в th:utext** — `[(...)]` уже выводит сырой JSON.

## 7. th:utext-поля (серверный HTML — НЕ экранировать)

- `${studyAnswerHtml}` — focus-training (flashcard reveal + study), `div.answer.markdown-content`.
- `${answerHtml}` — result, `div.answer.markdown-content`.
- `${@markdownRenderService.toHtml(opt.explanation)}` — result, `div.option-explanation.markdown-content`.
- AJAX-эквиваленты (sanitized клиентом): `AnswerResponse.answerHtml`, `optionExplanations[].explanationHtml`.
- **Санитайзер app.js ALLOWED_TAGS** сохраняет `table/thead/tbody/tr/th/td` и class на
  `div/span/code/pre` (для `.mermaid` и `language-*`). Серверный markdown должен жить в этих тегах.

## 8. A11y-каркас (обязан выжить)

- `a.skip-link[href=#main-content]` + `<main id="main-content">` на всех страницах.
- `role="radiogroup"` на `#interview-options`; native radio + per-option `aria-label="Вариант N: …"`.
- `role="progressbar"` на `.session-progress-track` (valuemin/max/now синхронит app.js).
- `aria-live`: `#result-feedback`(role=status polite atomic), `#result-extra-analysis`(polite),
  `.inline-alert`(role=alert), `#table-sort-status`(.visually-hidden polite atomic), chart fallbacks(polite).
- `aria-sort` на `th.sortable` (none→asc/desc), `tabindex=0`, `aria-keyshortcuts="Enter Space"`, role=columnheader, `<caption class=visually-hidden>`.
- **focus-trap** в kbd-help modal (role=dialog aria-modal, `?` toggle, Esc close, фокус возвращается); хоткеи 1-9 / flashcard 1-4/Space гасятся пока оверлей открыт.
- `aria-expanded` пары: toggle-code↔codeBlock, sidebar-collapse↔sidebar-content, browse-reveal↔browse-answer, topic-table-expander↔topic-table-wrap, favorite aria-pressed, confidence aria-pressed.
- `#question-timer` `aria-hidden=true` (намеренно — без посекундного спама скринридеру).

## 9. CSP / шрифты / тема (head.html — хирургически)

- CSP `style-src/script-src 'self' 'unsafe-inline'` + CDN: cdnjs, jsdelivr, fonts.googleapis/gstatic.
  Новый CSS — только из `/css/` своего origin. **Новые внешние хосты (иконки/шрифты) — ТОЛЬКО после
  добавления в `SecurityConfig`.** Fraunces/Newsreader/JetBrains Mono — с fonts.googleapis (уже в CSP).
- Тема: `data-theme` на `<html>`, pre-paint inline-скрипт (no-FOUC) в head + тогл в header,
  читают/пишут `localStorage 'theme'` и `meta[theme-color]`. Контракт `[data-theme=light/dark]` +
  `@media(prefers-color-scheme)` сохраняем (см. DESIGN_TOKENS.css).
- Cache-bust `?v=NN` в head.html на каждый CSS/JS — **бампать при каждой правке**.
- `favicon.svg` оставить (выбран вместо data: ради CSP).
