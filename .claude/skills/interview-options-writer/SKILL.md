---
name: interview-options-writer
description: >-
  Legacy-only router and execution skill for maintaining A/B/C/D options in the old
  SQLite answer_options flow through sqlite3, scripts/insert-options.py,
  scripts/seed-options.py, :quiz-app:seedOptions, or option.txt. Use only when the
  task explicitly targets the legacy table-based store. For seed/mcq JSON, schema
  validation, new MCQ generation, or corpus-wide JSON quality work, stop and route
  to mcq-quality-fixer.
---

# Interview Options Writer

## Status

**Legacy-only. Deprecated for new content.**

This skill exists only to maintain already existing MCQ options stored in the legacy
SQLite table `answer_options` or produced by the legacy seeding pipeline.

Do not create new legacy content merely because this skill is available. New and
actively maintained MCQ content belongs to:

```text
modules/quiz-app/src/main/resources/seed/mcq/**/*.json
```

and must be handled by `/mcq-quality-fixer`.

## Hard routing rule

Classify the request before reading or changing files.

| Request mentions | Action |
|---|---|
| `seed/mcq`, JSON MCQ, `mcq-schema.json`, `verify-mcq-json.sh`, JSON sections | Stop and use `/mcq-quality-fixer` |
| adding or changing `## Q<N>` theory in cheatsheets | Use `/interview-writer` |
| `answer_options`, SQLite, `interview.db`, `insert-options.py`, `seed-options.py`, `seedOptions`, `option.txt` | This skill applies |
| ambiguous request such as “fix interview answers” | Inspect paths first; do not assume legacy |

If both legacy and JSON flows are present, treat the JSON flow as authoritative unless
the user explicitly asks to maintain the legacy table.

## Progressive loading

Read `references/full-guide.md` only after confirming that the request is legacy work.

The full guide is mandatory when the task includes any of the following:

- generating missing options;
- rewriting distractors;
- auditing quality or answer-position distribution;
- inserting or updating rows;
- changing `option.txt`;
- running the Java seeder;
- repairing legacy data invariants.

Do not load the full guide merely to route a JSON MCQ request elsewhere.

## Source-of-truth order

Use this precedence for legacy tasks:

1. The user's explicit request.
2. Existing question `question_text` and `answer_markdown`.
3. Current database schema and constraints.
4. `modules/quiz-app/src/main/resources/prompts/option.txt`, if the runtime seeder uses it.
5. `references/full-guide.md`.
6. Historical examples.

Never let a historical example override current schema, code, or the user's artifact.

## Execution modes

Choose exactly one primary mode:

- `AUDIT_ONLY` — inspect and report; no writes.
- `CREATE_MISSING` — add options only where none exist.
- `REWRITE_EXISTING` — improve existing options for selected questions.
- `REPAIR_INVARIANTS` — fix count, correct flag, order, duplicates, or broken metadata.
- `UPDATE_PROMPT_CONTRACT` — edit `option.txt`; do not touch rows unless explicitly requested.
- `APPLY_BATCH` — insert a prepared batch transactionally.
- `MIGRATION_SUPPORT` — compare legacy rows with JSON MCQ, but do not silently migrate or delete.

State the selected mode internally before acting. Do not mix modes accidentally.

## Safety contract

Before a write:

1. Confirm the task is legacy.
2. Locate the exact database/file and inspect its schema.
3. Check whether the application or seeder is running and could overwrite rows.
4. Preserve existing user changes; do not reset unrelated files or rows.
5. Use a transaction or an idempotent helper.
6. Never use broad destructive SQL such as unscoped `DELETE FROM answer_options`.
7. Never overwrite a question that already has options in `CREATE_MISSING` mode.
8. Do not run `git add -A`.
9. Do not commit or push unless explicitly requested.
10. Record exact `question_id` values changed.

For a database write, create a backup when practical:

```bash
cp modules/quiz-app/data/db/interview.db \
   modules/quiz-app/data/db/interview.db.bak
```

If the DB is large or the environment has an established backup command, use that
instead and report what was done.

## Quality contract

Every option set must satisfy all of the following:

1. Exactly 4 options.
2. Exactly 1 correct option.
3. Orders are unique and equal to `0,1,2,3`.
4. All options answer the same stem.
5. Facts are correct and the correct answer is uniquely defensible.
6. Each distractor represents a realistic misconception.
7. Each distractor has one primary semantic error.
8. All 4 options have comparable information depth.
9. All 4 options are visually indistinguishable by length, syntax, punctuation,
   code formatting, technical density, and editorial quality.
10. Russian is natural, concise, and readable.
11. The correct option differs only by truth, not by polish.
12. Explanations teach the contrast instead of saying “this is wrong”.

Before finalizing, mentally remove the correct flag. If the correct option can be
guessed by appearance, rewrite the set.

## Mental-model-first rule

Do not start by drafting A/B/C/D.

First write four internal models:

- the correct model;
- misconception 1;
- misconception 2;
- misconception 3.

Then convert them into options using one natural block-level format.

A distractor should be:

```text
partly correct reasoning
+ one realistic mistaken assumption
+ a concrete false conclusion
```

It must not be:

```text
an obvious absurdity
+ exaggerated wording
+ filler added only to match length
```

## Visual Format Parity

Correct and incorrect options must look as if one careful author wrote all four.

Align, without mechanical cloning:

- number of sentences;
- opening shape;
- grammatical form;
- punctuation rhythm;
- `backtick` usage;
- parentheses and enumerations;
- number of technical claims;
- tone and confidence;
- presence of examples or consequences.

Use a shared semantic skeleton, not literal carbon copies. Natural Russian is more
important than forcing identical character counts.

## Validation

Use the checks available in the repository. At minimum:

```bash
sqlite3 modules/quiz-app/data/db/interview.db \
  "SELECT question_id,
          COUNT(*) AS option_count,
          SUM(CASE WHEN is_correct = 1 THEN 1 ELSE 0 END) AS correct_count,
          COUNT(DISTINCT display_order) AS distinct_orders
   FROM answer_options
   WHERE question_id IN (<ids>)
   GROUP BY question_id;"
```

Expected for every changed question:

```text
option_count = 4
correct_count = 1
distinct_orders = 4
```

When available, also run:

```bash
python3 scripts/mcq-quality-audit.py <target>
```

or the current legacy audit command documented in the repository.

Do not claim completion if a required validator fails.

## Workflow

1. Route the task.
2. Read `references/full-guide.md`.
3. Inspect the source question and reference answer.
4. Select the exact learning objective.
5. Build four mental models.
6. Draft options with Single-Delta where appropriate.
7. Run quality and blind-style review.
8. Write explanations.
9. Validate position distribution and invariants.
10. Apply via transaction or idempotent helper.
11. Re-read the persisted rows.
12. Report exact changes, validation, and residual risk.

## Final response

Report briefly:

1. Mode used.
2. Database/file changed.
3. `question_id` values changed.
4. Validation commands and results.
5. Remaining risk or manual-review items.

<!-- skill-cross-linker:start -->
## Related skills

**Route to:**
- `/mcq-quality-fixer` — JSON MCQ in `seed/mcq/**`, all new content, schema and parity gates.
- `/interview-writer` — create or update theory questions and reference answers.

<!-- skill-cross-linker:end -->
