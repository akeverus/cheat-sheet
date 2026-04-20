# Design: Trainer Improvements — UX, SM-2 Visibility, Content Pipeline

**Date:** 2026-04-20  
**Scope:** Three independent improvements to the local quiz trainer.

---

## 1. UX: Reset Confirmation Dialog

### Problem
The "Сбросить банк вариантов" button in `/settings` submits a form immediately — one misclick destroys all generated AI options with no undo.

### Solution
- Add `data-confirm="..."` attribute to the dangerous submit button in `settings.html`.
- In `app.js`, add a single `submit` event listener that intercepts any form containing a button with `data-confirm`, calls `confirm(message)`, and cancels submission if the user declines.
- Pattern is reusable for other destructive actions in the future.

### Files
- `templates/settings.html` — add `data-confirm` to the reset button
- `static/js/app.js` — add `initDangerousFormGuard()` function, call from `DOMContentLoaded`

---

## 2. SM-2 Visibility: Topic Maturity Column in Stats

### Problem
The `/stats` topic table shows Total / Due / Learned / Accuracy but no indication of *how deeply* a topic is learned. A topic can appear "learned" (repetitions ≥ threshold) but still have a low ease factor and short intervals, meaning it's fragile.

### Solution
Add a **«Зрелость»** (Maturity) column to the topic stats table. Maturity is defined as:

```
maturity = avg(ease_factor × interval_days)  per topic
```

Visual tiers (CSS classes):
- **Красный** `maturity-low`: maturity < 3 (new / struggling)
- **Жёлтый** `maturity-mid`: 3 ≤ maturity < 10 (learning)
- **Зелёный** `maturity-high`: maturity ≥ 10 (solid retention)

### Data Layer
`QuestionStatsRepository.findTopicStats()` — extend the SQL query to compute `AVG(rs.ease_factor * rs.interval_days)` per topic. Add `maturityScore` field to `TopicStats` record.

### Presentation Layer
- `StatsPageService` — pass `maturityScore` through (no transformation needed).
- `stats.html` — add column header + cell with colored badge.
- `styles.css` — add `.maturity-low`, `.maturity-mid`, `.maturity-high` classes.

### Files
- `QuestionStatsRepository.java` — extend SQL + `TopicStats` record
- `StatsPageService.java` — pass through (if TopicStats is a record, no changes needed)
- `templates/stats.html` — new column
- `static/css/styles.css` — 3 new badge classes

---

## 3. Content Pipeline: Option Prompt Quality

### Problem
`option.txt` has solid structural constraints (length equality, no meta-markers) but lacks:
1. A **topic anchor** rule — distractors can drift to adjacent technical domains unrelated to the question.
2. A **symmetry ban** — distractors should not be simple inversions/negations of the correct answer.
3. `explanation` field is not constrained in the prompt — can produce multi-sentence meta-advice.

### Solution
Add three new rules to `option.txt`:

**Rule 14 (topic anchor):** Each distractor must stay in the same technical sub-domain as the question. If the question is about a specific API/class/algorithm, every option must reference that same API/class/algorithm family — not a different one.

**Rule 15 (no negation distractors):** Distractors must not be simple negations or inversions of the correct answer. "Does X" vs "Does NOT X" is forbidden as a distractor pair.

**Rule 16 (explanation contract):** The `explanation` field must be exactly 1–2 sentences, purely technical, explaining why the correct answer is right and why the main distractor is wrong. No career advice, no meta-interview coaching.

### Files
- `src/main/resources/prompts/option.txt` — add rules 14–16

---

## Non-Goals
- SM-2 parameter tuning (GRADE_WRONG, intervals) — current values match standard Anki SM-2, no evidence of problems.
- Topic coverage gaps analysis — deferred, requires data from a running instance.
- Flashcard multi-grade self-rating — separate future feature.
