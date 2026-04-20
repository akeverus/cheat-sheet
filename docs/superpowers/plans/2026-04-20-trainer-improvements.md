# Trainer Improvements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Three independent improvements: reset-button guard, SM-2 maturity column in stats, and stronger option-prompt rules.

**Architecture:** (1) JS data-confirm guard — one event listener pattern, no framework. (2) SM-2 maturity — extend `TopicStats` domain record + SQL AVG, render badge in stats table. (3) Prompt — append rules 14-16 to `option.txt`.

**Tech Stack:** Spring Boot 3, Thymeleaf, SQLite, vanilla JS, Gradle.

---

## Task 1: Reset-Button Confirmation Guard (JS + HTML)

**Files:**
- Modify: `modules/quiz-app/src/main/resources/static/js/app.js` (near line 1493 — end of IIFE)
- Modify: `modules/quiz-app/src/main/resources/templates/settings.html` (line 137 — reset button)

- [ ] **Step 1: Add `data-confirm` attribute to the reset button in settings.html**

Open `modules/quiz-app/src/main/resources/templates/settings.html`, find the submit button (line ~137) and change it:

```html
<button type="submit" class="btn danger-btn"
        data-confirm="Удалить все сгенерированные варианты? Они будут перегенерированы заново. Это нельзя отменить.">Сбросить банк вариантов</button>
```

- [ ] **Step 2: Add `initDangerousFormGuard` to app.js**

In `modules/quiz-app/src/main/resources/static/js/app.js`, add the following function before the `initCollapsibleSidebar()` call at line ~1493:

```js
function initDangerousFormGuard() {
  document.querySelectorAll('[data-confirm]').forEach((el) => {
    const form = el.closest('form');
    if (!form) return;
    form.addEventListener('submit', (e) => {
      const msg = el.getAttribute('data-confirm');
      if (msg && !window.confirm(msg)) {
        e.preventDefault();
      }
    });
  });
}
```

Then add a call inside the `DOMContentLoaded` IIFE, after `initCollapsibleSidebar()`:

```js
  initCollapsibleSidebar();
  initDangerousFormGuard();
```

- [ ] **Step 3: Verify manually**

Run `./gradlew bootRun` (no API key needed), open `http://localhost:8080/settings`, click «Сбросить банк вариантов» — browser confirmation dialog should appear. Dismiss → no request sent. Confirm → form submits.

- [ ] **Step 4: Run test suite to catch regressions**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.ui.*"
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add modules/quiz-app/src/main/resources/static/js/app.js \
        modules/quiz-app/src/main/resources/templates/settings.html
git commit -m "feat(ux): подтверждение перед сбросом банка вариантов"
```

---

## Task 2: TopicStats Domain — Add maturityScore Field

**Files:**
- Modify: `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/TopicStats.java`

`TopicStats` is a record with 7 positional fields. Adding `maturityScore` as an 8th field would break all existing 7-arg constructors in tests. Solution: add an 8-arg canonical record field + a backwards-compatible 7-arg delegating constructor.

- [ ] **Step 1: Add `maturityScore` field + backwards-compatible constructor**

Replace the entire file content of `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/TopicStats.java`:

```java
package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Статистика по одной теме (для графиков и таблицы тем).
 *
 * @param topic         название темы
 * @param total         общее количество вопросов в теме
 * @param due           количество вопросов, готовых к повторению
 * @param learned       количество «выученных» вопросов
 * @param correct       суммарное количество правильных ответов
 * @param wrong         суммарное количество неправильных ответов
 * @param regenSum      суммарное количество перезагрузок вариантов по теме
 * @param maturityScore среднее ease_factor × interval_days по теме (0 = нет данных)
 */
@Builder(toBuilder = true)
public record TopicStats(
        String topic,
        long total,
        long due,
        long learned,
        long correct,
        long wrong,
        long regenSum,
        double maturityScore
) {
    public TopicStats {
        Objects.requireNonNull(topic, "topic не может быть null");
        if (total < 0) {
            throw new IllegalArgumentException("total не может быть отрицательным: " + total);
        }
    }

    /** Backwards-compatible constructor for existing callers without maturityScore. */
    public TopicStats(String topic, long total, long due, long learned,
                      long correct, long wrong, long regenSum) {
        this(topic, total, due, learned, correct, wrong, regenSum, 0.0);
    }
}
```

- [ ] **Step 2: Run domain tests**

```bash
./gradlew :quiz-domain:test
```

Expected: BUILD SUCCESSFUL (no tests in domain module — that's fine).

- [ ] **Step 3: Run full compile check**

```bash
./gradlew compileJava compileTestJava
```

Expected: BUILD SUCCESSFUL — 7-arg constructor still compiles everywhere.

---

## Task 3: QuestionStatsRepository — Add maturityScore to SQL

**Files:**
- Modify: `modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/QuestionStatsRepository.java` (lines ~188-203)
- Modify: `modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/QuestionStatsRepositoryTest.java` — add maturityScore test

- [ ] **Step 1: Extend `findTopicStats` SQL query**

In `QuestionStatsRepository.java`, replace the `findTopicStats` method body:

```java
public List<TopicStats> findTopicStats(long nowEpoch, int learnedThreshold) {
    return jdbcTemplate.query(
            "SELECT q.topic AS topic, COUNT(*) AS total, " +
                    "SUM(CASE WHEN rs.next_review_at <= ? THEN 1 ELSE 0 END) AS due, " +
                    "SUM(CASE WHEN rs.repetitions >= ? THEN 1 ELSE 0 END) AS learned, " +
                    "SUM(rs.correct_count) AS correct, SUM(rs.wrong_count) AS wrong, " +
                    "SUM(q.regen_count) AS regen_sum, " +
                    "AVG(rs.ease_factor * rs.interval_days) AS maturity_score " +
                    "FROM questions q JOIN review_state rs ON rs.question_id = q.id " +
                    "GROUP BY q.topic ORDER BY q.topic",
            (rs, rowNum) -> new TopicStats(
                    rs.getString("topic"), rs.getLong("total"),
                    rs.getLong("due"), rs.getLong("learned"),
                    rs.getLong("correct"), rs.getLong("wrong"),
                    rs.getLong("regen_sum"),
                    rs.getDouble("maturity_score")),
            nowEpoch, learnedThreshold);
}
```

- [ ] **Step 2: Write a failing test for maturityScore in QuestionStatsRepositoryTest**

In `QuestionStatsRepositoryTest.java`, add this test (after the existing tests):

```java
@Test
void findTopicStatsIncludesMaturityScore() {
    long nowEpoch = System.currentTimeMillis() / 1000;

    jdbcTemplate.update(
            "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)",
            "q1", "q1", "f.md", "java", "Q?", "A.", "h1");
    jdbcTemplate.update(
            "INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, " +
            "next_review_at, last_result, correct_count, wrong_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            1L, 3, 10, 2.5, nowEpoch - 1, "CORRECT", 5, 1);

    List<TopicStats> stats = repository.findTopicStats(nowEpoch, 3);

    assertThat(stats).hasSize(1);
    assertThat(stats.get(0).maturityScore()).isEqualTo(25.0, within(0.01));
    // 2.5 * 10 = 25.0
}
```

Add the `within` static import at the top of the file:
```java
import static org.assertj.core.api.Assertions.within;
```

- [ ] **Step 3: Run the test to verify it fails**

```bash
./gradlew :quiz-persistence:test --tests "com.cheatsheet.quiz.persistence.QuestionStatsRepositoryTest.findTopicStatsIncludesMaturityScore"
```

Expected: FAIL (column `maturity_score` does not exist yet — Step 1 fixes that, so if Step 1 was done first, it should PASS here already).

- [ ] **Step 4: Run all persistence tests**

```bash
./gradlew :quiz-persistence:test
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/TopicStats.java \
        modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/QuestionStatsRepository.java \
        modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/QuestionStatsRepositoryTest.java
git commit -m "feat(stats): добавить maturityScore (avg ease×interval) в TopicStats"
```

---

## Task 4: Stats Page — Maturity Column in Topic Table

**Files:**
- Modify: `modules/quiz-app/src/main/resources/templates/stats.html`
- Modify: `modules/quiz-app/src/main/resources/static/css/styles.css`

- [ ] **Step 1: Add CSS badge classes for maturity levels**

In `modules/quiz-app/src/main/resources/static/css/styles.css`, find the `.stats-page` section and add after the existing `.stats-page` rules (near the bottom of the stats section):

```css
/* Topic maturity badges */
.maturity-badge { display: inline-block; padding: 2px 7px; border-radius: 10px; font-size: 0.72rem; font-weight: 600; letter-spacing: 0.02em; }
.maturity-low  { background: var(--red-100);   color: var(--red-800);   }
.maturity-mid  { background: var(--yellow-100); color: var(--amber-800); }
.maturity-high { background: var(--green-100);  color: var(--green-800); }
```

- [ ] **Step 2: Add «Зрелость» column header in stats.html**

In `modules/quiz-app/src/main/resources/templates/stats.html`, after the `🔄` column header (line ~104), add:

```html
<th class="sortable w-80" data-col="5" data-sort-label="Зрелость" role="columnheader" aria-sort="none" tabindex="0" aria-keyshortcuts="Enter Space" aria-label="Сортировать по колонке: Зрелость" title="Среднее ease×interval — показатель глубины запоминания">Зрелость</th>
```

- [ ] **Step 3: Add maturity cell to each row in stats.html**

After the regen count `<td>` cell in the `th:each` loop (around line ~134), add:

```html
<td class="text-center">
    <span th:with="m=${item.maturityScore}"
          th:class="'maturity-badge ' + (${m} >= 10 ? 'maturity-high' : (${m} >= 3 ? 'maturity-mid' : 'maturity-low'))"
          th:text="${m == 0} ? '—' : (#numbers.formatDecimal(m, 1, 1))">—</span>
</td>
```

- [ ] **Step 4: Update VisualBaselineContractTest — stats sortable column count is not asserted, so no baseline change needed. Verify:**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.ui.VisualBaselineContractTest"
```

Expected: BUILD SUCCESSFUL (stats baseline checks content, not column count).

- [ ] **Step 5: Update TemplateFragmentContractTest CSS assertions if needed**

The CSS contract test checks for `.stats-page.*` selectors. The new badge classes use `.maturity-*`, not `.stats-page .maturity-*`, so no update needed. Verify:

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.ui.TemplateFragmentContractTest"
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Run full app test suite**

```bash
./gradlew :quiz-app:test
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 7: Commit**

```bash
git add modules/quiz-app/src/main/resources/templates/stats.html \
        modules/quiz-app/src/main/resources/static/css/styles.css
git commit -m "feat(stats): колонка «Зрелость» в таблице тем (SM-2 maturity badge)"
```

---

## Task 5: Improve option.txt Prompt — Rules 14–16

**Files:**
- Modify: `modules/quiz-app/src/main/resources/prompts/option.txt`
- Test: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/ai/QuestionPromptBuilderTest.java` — there is no test for `option.txt` content directly; `QuestionPromptBuilderTest` tests `QUESTION_V2_PROMPT_TEMPLATE`, not `option.txt`. We verify by checking that the `USER_PROMPT_TEMPLATE` constant in `AiPrompts` contains the new text.

- [ ] **Step 1: Append rules 14–16 to option.txt**

In `modules/quiz-app/src/main/resources/prompts/option.txt`, before the `Финальный self-check` block, add:

```
14) Все дистракторы остаются в той же технической области, что и вопрос: если вопрос о конкретном API/классе/алгоритме, каждый вариант должен ссылаться именно на него — не на смежную тему.
15) Дистракторы не должны быть простым отрицанием или инверсией правильного ответа. Пара «делает X» vs «не делает X» запрещена.
16) Поле `explanation` строго 1–2 предложения: технически точное объяснение почему правильный ответ верен и почему основной дистрактор неверен. Советы по карьере и интервью запрещены.
```

- [ ] **Step 2: Write a unit test that verifies the new rules are present in the loaded prompt**

In `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/ai/QuestionPromptBuilderTest.java`, add a new test class or add a test method (the file has `QuestionPromptBuilderTest` which tests `QuestionPromptBuilder`. Add a separate test for prompt content):

Add this test to `QuestionPromptBuilderTest.java`:

```java
@Test
void optionPromptContainsTopicAnchorAndNegationAndExplanationRules() {
    String prompt = com.cheatsheet.quiz.service.ai.prompt.AiPrompts.USER_PROMPT_TEMPLATE;
    assertThat(prompt).contains("той же технической области");
    assertThat(prompt).contains("простым отрицанием или инверсией");
    assertThat(prompt).contains("1–2 предложения");
}
```

- [ ] **Step 3: Run the test to confirm it fails (before editing option.txt)**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.ai.QuestionPromptBuilderTest.optionPromptContainsTopicAnchorAndNegationAndExplanationRules"
```

Expected: FAIL (strings not yet present).

- [ ] **Step 4: Edit option.txt (Step 1 above) then run again**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.ai.QuestionPromptBuilderTest.optionPromptContainsTopicAnchorAndNegationAndExplanationRules"
```

Expected: PASS.

- [ ] **Step 5: Run full test suite**

```bash
./gradlew test
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add modules/quiz-app/src/main/resources/prompts/option.txt \
        modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/ai/QuestionPromptBuilderTest.java
git commit -m "feat(prompt): правила 14-16 в option.txt — topic anchor, no-negation, explanation contract"
```

---

## Self-Review

**Spec coverage:**
- ✅ Reset confirmation — Task 1
- ✅ Maturity column (domain + SQL + UI + CSS) — Tasks 2, 3, 4
- ✅ option.txt rules 14–16 — Task 5

**Placeholder scan:** None found. All code blocks are complete.

**Type consistency:**
- `TopicStats.maturityScore()` used in Tasks 2, 3, 4 — consistent.
- `rs.getDouble("maturity_score")` in Task 3 matches SQL alias `AS maturity_score` — consistent.
- Thymeleaf `${item.maturityScore}` in Task 4 matches record accessor `maturityScore()` — consistent.
