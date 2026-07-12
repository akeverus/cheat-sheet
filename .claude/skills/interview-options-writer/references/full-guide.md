# Interview Options Writer — full guide

## 1. Status and purpose

This guide is the detailed procedure for the legacy `interview-options-writer`
skill.

> **Legacy-only. Deprecated for new MCQ content.**

Use it only for the old table-based flow:

- SQLite table `answer_options`;
- `modules/quiz-app/data/db/interview.db`;
- `scripts/insert-options.py`;
- `scripts/seed-options.py`;
- `:quiz-app:seedOptions`;
- `modules/quiz-app/src/main/resources/prompts/option.txt`;
- historical `option.txt`-based seeding.

For JSON MCQ under `modules/quiz-app/src/main/resources/seed/mcq/**`, stop and
use `/mcq-quality-fixer`.

For adding or changing theory questions in `cheatsheets/interview/**`, use
`/interview-writer`.

This guide does not authorize migration, deletion, or duplication between the
legacy and JSON stores.

---

# 2. Operating principles

## 2.1 The goal

The goal is not to generate three obviously wrong alternatives around a polished
correct answer.

The goal is to create a learning item where:

- all 4 options look equally credible;
- the learner must know the topic;
- each wrong option exposes a real misconception;
- the explanation repairs that misconception;
- the correct answer cannot be guessed from length, structure, tone, formatting,
  or position.

The concise rule:

```text
The correct option must differ only by truth.
```

## 2.2 Priority order

When rules conflict, use this priority:

1. Factual correctness.
2. Exactly one defensible answer.
3. Agreement with the stem and reference answer.
4. Plausibility of misconceptions.
5. Natural Russian.
6. Visual and structural parity.
7. Numeric length targets.
8. Position balancing.
9. Stylistic preferences.

Never make a statement false, ambiguous, or unreadable merely to satisfy a
metric.

## 2.3 Source-of-truth precedence

For legacy work:

1. User instruction.
2. Current `question_text`.
3. Current `answer_markdown`.
4. Current database schema and constraints.
5. Current seeder/runtime prompt.
6. This guide.
7. Historical examples.

When sources conflict, do not guess. Preserve data and report the conflict.

---

# 3. Scope classification

Before doing any work, classify the request.

## 3.1 `AUDIT_ONLY`

Use when the user asks to inspect quality, gaps, duplicates, weak distractors,
or position distribution.

Allowed:

- read database;
- run SQL reports;
- export candidate rows;
- produce recommendations.

Forbidden:

- modifying rows;
- rewriting files;
- running a write seeder.

## 3.2 `CREATE_MISSING`

Use when existing questions lack options.

Rules:

- do not overwrite questions that already have 4 options;
- generate in small batches;
- use the existing `question_text` and `answer_markdown`;
- apply transactionally;
- re-read inserted rows.

## 3.3 `REWRITE_EXISTING`

Use when existing options are poor.

Rules:

- target explicit `question_id` values or a bounded query;
- preserve unrelated rows;
- keep the correct answer semantically correct;
- change the correct flag only when the source answer proves it is wrong;
- document every changed question.

## 3.4 `REPAIR_INVARIANTS`

Use for structural corruption:

- not exactly 4 options;
- zero or multiple corrects;
- duplicated `display_order`;
- duplicate text;
- orphan rows;
- invalid metadata.

Do not mix quality rewriting into an invariant repair unless requested.

## 3.5 `UPDATE_PROMPT_CONTRACT`

Use when editing `option.txt` or the Java seeder prompt.

Rules:

- do not silently rewrite existing rows;
- keep runtime format compatible;
- compare the prompt with this guide;
- report incompatibilities.

## 3.6 `APPLY_BATCH`

Use when a prepared JSON batch already exists.

Rules:

- validate before write;
- apply idempotently;
- use a transaction;
- verify persisted rows.

## 3.7 `MIGRATION_SUPPORT`

Use only when explicitly asked to compare old rows with JSON MCQ.

Rules:

- no automatic deletion;
- no silent conversion;
- no claim that one store is synchronized without checking;
- JSON is normally the maintained destination for new content.

---

# 4. Legacy data contract

The common legacy shape is:

```text
answer_options
├── question_id
├── option_text
├── is_correct
├── display_order
├── source
├── explanation
├── prompt_version
└── quality_profile_version
```

Inspect the real schema before relying on these columns:

```bash
sqlite3 modules/quiz-app/data/db/interview.db \
  ".schema answer_options"
```

## 4.1 Hard invariants

For every question:

- exactly 4 rows;
- exactly 1 row with `is_correct = 1`;
- unique `display_order` values;
- orders exactly `0,1,2,3`;
- non-empty option text;
- non-empty explanation when the current schema requires it;
- no duplicate option text after whitespace normalization;
- all rows reference an existing question.

## 4.2 Batch JSON shape

When using `scripts/insert-options.py`, prefer:

```json
[
  {
    "qid": 834,
    "options": [
      {
        "text": "...",
        "correct": false,
        "explanation": "..."
      },
      {
        "text": "...",
        "correct": true,
        "explanation": "..."
      },
      {
        "text": "...",
        "correct": false,
        "explanation": "..."
      },
      {
        "text": "...",
        "correct": false,
        "explanation": "..."
      }
    ]
  }
]
```

Array order maps to `display_order = 0..3`.

Do not add fields that the helper ignores or rejects.

---

# 5. Preflight safety

## 5.1 Confirm the target

Inspect:

```bash
pwd
git status --short
git rev-parse --show-toplevel
```

Locate the actual DB and scripts rather than assuming paths.

## 5.2 Check running processes

A running application or seeder can overwrite recently inserted rows.

```bash
ps aux | grep -Ei 'gradle|java|quiz-app' | grep -v grep
```

Do not kill a process unless the user explicitly permits it. If it may race with
the write, stop and report the collision.

## 5.3 Back up the DB

When practical:

```bash
cp modules/quiz-app/data/db/interview.db \
   modules/quiz-app/data/db/interview.db.bak
```

For repeated work, use timestamped backups or the repository's established
backup mechanism.

## 5.4 Inspect constraints

```bash
sqlite3 modules/quiz-app/data/db/interview.db \
  "PRAGMA foreign_keys;
   PRAGMA journal_mode;
   PRAGMA table_info(answer_options);
   PRAGMA index_list(answer_options);"
```

Do not assume that a unique partial index exists. Verify it.

## 5.5 Define the bounded target

Every write must have a bounded target:

- explicit `question_id` list;
- one topic plus `LIMIT`;
- one prepared batch.

Never update all rows because a filter accidentally became empty.

---

# 6. Read the learning source

Before writing options, read both:

```text
question_text
answer_markdown
```

Example query:

```bash
sqlite3 -json modules/quiz-app/data/db/interview.db \
  "SELECT id, topic, question_text, answer_markdown
   FROM questions
   WHERE id IN (1067,1068,1069);"
```

Do not infer the correct answer from the stem alone when a reference answer
exists.

For each question, extract:

1. The exact concept being tested.
2. The minimal correct proposition.
3. Conditions under which it is true.
4. Nearby concepts that are easy to confuse.
5. Version/configuration assumptions.
6. The expected difficulty level.
7. Whether code is needed to make the question unambiguous.

If the stem is ambiguous or has more than one defensible answer, do not hide the
problem inside the options. Mark the question for `/interview-writer`.

---

# 7. Question categories

Choose one category before drafting.

## 7.1 Concept / boundary

Examples:

- what a functional interface is;
- ownership in Hibernate associations;
- guarantees of `volatile`;
- semantics of a Kafka consumer group.

Best distractors:

- adjacent concept;
- scope confusion;
- outdated behavior;
- wrong condition;
- overgeneralization.

## 7.2 Comparison

Examples:

- `Function` vs `UnaryOperator`;
- `Runnable` vs `Callable`;
- `LAZY` vs `EAGER`.

All 4 options must compare the same entities using the same criteria and order.

## 7.3 Behavioral code

Examples:

- output;
- thrown exception;
- concurrency behavior;
- collection mutation;
- transaction result.

Prefer Single-Delta and concrete outcomes.

## 7.4 Choose an implementation

Each option should be a comparable implementation or code fragment.

All snippets must use the same language level, amount of code, and visible
context.

## 7.5 Lifecycle / sequence

All options should describe a sequence with the same set of stages and one
difference in order, presence, or trigger.

## 7.6 Failure mode

All options should explain the same symptom using competing causal models.

## 7.7 Decision / trade-off

All options should be plausible decisions. Wrong options fail because of one
misweighted constraint, not because they are reckless caricatures.

---

# 8. Mental-model-first workflow

Do not draft A/B/C/D immediately.

For each question, write four internal mental models.

## 8.1 Correct model

Capture the smallest complete proposition that is true under the stem.

## 8.2 Wrong model 1: confusion

Swap two nearby concepts, roles, methods, or layers.

Example:

```text
`mappedBy` is placed on the owning side
```

The API is real; the ownership rule is reversed.

## 8.3 Wrong model 2: overgeneralization

Take a correct local rule and apply it universally.

Example:

```text
EAGER avoids N+1 because all relations are fetched immediately
```

The intuition “fewer deferred loads” is real; the conclusion is not generally
true.

## 8.4 Wrong model 3: wrong trade-off or condition

Use the right mechanism under the wrong condition.

Example:

```text
Use ReadWriteLock when writes dominate because it separates readers from writers
```

The mechanism is real; the applicability condition is reversed.

## 8.5 Mental-model acceptance test

A wrong model is acceptable only when:

- a competent middle developer might sincerely believe it;
- it contains a recognizable grain of truth;
- it has one primary semantic error;
- it is unambiguously false under the stem;
- it does not depend on an unstated version or configuration;
- it differs from the other distractors.

---

# 9. Single-Delta

## 9.1 Definition

Single-Delta means the options share a common semantic frame and differ on one
important element.

Use it strongly for:

- code output;
- lifecycle order;
- parameter values;
- method selection;
- cardinality;
- boolean combinations;
- failure causes.

## 9.2 Good forms

### Same stages, different order

```text
A. `@PostConstruct` → `afterPropertiesSet()` → custom init
B. `afterPropertiesSet()` → `@PostConstruct` → custom init
C. custom init → `@PostConstruct` → `afterPropertiesSet()`
D. `@PostConstruct` → custom init → `afterPropertiesSet()`
```

### Same signature, one changed role

```text
A. `Function<T,R>`: `T` in, `R` out
B. `Consumer<T>`: `T` in, `R` out
C. `Supplier<T>`: `T` in, no output
D. `Predicate<T>`: no input, `boolean` out
```

The surface form is comparable; each wrong option changes one signature fact.

## 9.3 Where not to force it

Do not force literal one-token differences when the result becomes:

- unnatural;
- duplicated;
- ambiguous;
- dependent on nonexistent APIs;
- a mechanical negation of the correct answer.

Use one semantic error, not necessarily one textual token.

---

# 10. Distractor taxonomy

Each wrong option should have a named error type.

## 10.1 Adjacent concept confusion

A real fact from a nearby class, method, layer, or mechanism is assigned to the
target.

## 10.2 Scope confusion

The mechanism is right at the wrong scope:

- object vs class;
- transaction vs request;
- upstream vs downstream;
- entity vs association;
- thread vs pool.

## 10.3 Wrong condition

The mechanism is valid, but the trigger or applicability condition is wrong.

## 10.4 Overgeneralization

A local rule is treated as universal.

## 10.5 Outdated model

The statement was true in an older version.

Use only when the version boundary is known and the stem makes the current
context clear.

## 10.6 Wrong lifecycle order

Correct stages, wrong sequence or trigger.

## 10.7 Wrong cardinality

Examples:

- zero-or-one confused with exactly-one;
- one-to-many confused with many-to-many;
- bounded queue confused with unbounded queue.

## 10.8 Wrong trade-off

A real benefit is chosen while a decisive cost or constraint is ignored.

## 10.9 Causal inversion

A true observation is linked to the wrong cause.

## 10.10 API-shape confusion

A method name or signature is real, but assigned the wrong input, output,
exception contract, or owning type.

## 10.11 Partial truth with false conclusion

The premise is true; the conclusion is not.

## 10.12 Plausible implementation bug

Code is almost correct and fails at one specific point.

---

# 11. Forbidden distractor patterns

Rewrite a distractor if it is:

- an unrelated technology;
- an invented method used only to look wrong;
- obvious nonsense;
- a joke;
- a villain-manager caricature;
- a direct negation echo of the correct answer;
- wrong for several independent reasons;
- partially correct under a reasonable interpretation;
- dependent on an unstated version;
- a one-line stub next to a detailed correct answer;
- artificially padded with filler;
- grammatically incompatible with the stem;
- the only option without technical terms;
- the only option with extreme tone;
- a carbon copy with one visibly swapped word and no natural Russian flow.

Forbidden soft-skill caricature markers include combinations such as:

```text
любой ценой
не спрашивая
публично и жёстко
заставить
угрожать
просто игнорировать
оставить человека в покое
```

Words such as `всегда` or `никогда` are not automatically forbidden. They become
a style tell when only distractors are categorical and the correct answer is
carefully hedged.

---

# 12. Option Parity

## 12.1 Principle

All 4 options must have comparable information depth.

The correct option must not stand out by:

- length;
- number of claims;
- technical vocabulary;
- conditions;
- examples;
- code spans;
- precision;
- professional tone;
- explanatory completeness.

## 12.2 Practical target

Use word count and sentence count as diagnostics, not absolute truth.

A reasonable default:

- longest/shortest word-count ratio preferably `≤ 1.35`;
- sentence count differs by no more than 1;
- correct is not systematically the longest;
- code-span and number density are comparable.

Do not ruin natural language to hit a ratio.

## 12.3 Direction of repair

When the correct option is much richer:

1. Move post-answer teaching detail into its explanation.
2. Raise distractors with plausible parallel detail.
3. Preserve one primary error per distractor.
4. Recheck uniqueness of the correct answer.

Do not gut the correct answer into a vague fragment.

---

# 13. Plausibility Parity

A long distractor can still be obviously wrong.

For each distractor ask:

1. Could a reasonable middle or senior engineer believe it?
2. What grain of truth makes it tempting?
3. What one assumption is wrong?
4. Is it false under the exact stem?
5. Does it sound mature rather than absurd?
6. Is its tone comparable to the correct option?
7. Is it distinct from the other two misconceptions?

A useful formula:

```text
real concept
+ plausible reasoning
+ one wrong assumption
+ concrete consequence
```

---

# 14. Russian Readability Parity

All options must be natural Russian, not translated template prose.

## 14.1 Requirements

- Prefer direct syntax.
- Keep sentences readable.
- Use one term consistently.
- Avoid bureaucratic wording.
- Avoid unexplained English where a Russian phrase is clearer.
- Preserve canonical Java/API names in `backticks`.
- Avoid machine-generated chains with repeated arrows.
- Avoid filler introduced only to equalize length.
- Keep punctuation semantically motivated.

## 14.2 Readability warning signs

Rewrite when:

- one sentence contains too many independent clauses;
- there are 3+ semicolons without a real enumeration;
- there are 3+ arrows in ordinary prose;
- every option repeats the same unnatural phrase;
- the option reads like an explanation section rather than an answer;
- the Russian sounds translated word-for-word;
- the syntax is technically correct but cognitively heavy.

## 14.3 The non-negotiable rule

```text
Slightly different length with natural Russian
is better than identical length with wooden Russian.
```

---

# 15. Visual Format Parity

## 15.1 Principle

If the correct flag is hidden, the answer must not be visually guessable.

Align the following at block level:

- opening shape;
- grammatical form;
- number of sentences;
- punctuation rhythm;
- use of `backticks`;
- parentheses;
- enumerations;
- code snippets;
- cause/effect structure;
- technical density;
- tone;
- confidence;
- level of editorial polish.

## 15.2 Shared surface + semantic skeleton (`SKEL` — HARD)

**Hard gate.** All four `option.text` must share one surface skeleton. This maps
to plan column `SKEL` and `llm_review.skel`. Without it: no `PAR`, no `FINAL`,
no `four_option_pass`. Scripts cannot close `SKEL`.

Before drafting, describe the block skeleton in one line, then write **all four**
options only in that shape:

| Axis | Must match across A/B/C/D |
|---|---|
| Opening | same class |
| Sentences / clauses | same class |
| Punctuation frame | same `;` / `:` / `—` / parentheses class |
| Backticks / lists | same density class |
| Answer type | definition↔definition, API-pair↔API-pair, etc. |

Example:

```text
<term comparison>. <default or consequence>.
```

```text
In API <spec>: <A> — <role A>; <B> — <role B>.
```

Do not copy punctuation mechanically for stamp. A natural distractor in the **same
skeleton** beats a distorted sentence made only to match comma counts. Different
skeletons with similar length = `SKEL` FAIL.

## 15.3 Visual blind test

Hide the stem and the correct flag. Ask:

- Which option looks most polished?
- Which is longest?
- Which has the most `backticks`?
- Which is the only one with a colon?
- Which sounds most cautious and professional?
- Which looks like a mini-reference article?

If the same option repeatedly wins these questions, rewrite the block.

## 15.4 Anti-pattern: Visually Distinguishable Correct Option

The correct answer looks like the only carefully written answer.

Fix:

- select one natural format;
- rewrite all 4 as full answers;
- equalize information depth;
- preserve different mental models;
- rerun the blind test.

## 15.5 Anti-pattern: Stamped Clone

All 4 options have been padded into near-identical carbon copies.

Symptoms:

- same filler phrase;
- same clause count despite unnatural meaning;
- one bolded or swapped token determines everything;
- repetitive rhythm;
- duplicate long n-grams;
- low readability.

Fix:

- return to the four mental models;
- keep a shared semantic skeleton;
- allow natural variation;
- preserve one error per distractor.

---

# 16. Correct-answer uniqueness

For every block, prove:

1. Why the correct option is true under every condition in the stem.
2. Why distractor 1 is false.
3. Why distractor 2 is false.
4. Why distractor 3 is false.
5. Why none becomes true under a common alternative interpretation.
6. Whether version, provider, configuration, locale, transaction boundary, or
   runtime mode changes the answer.

If an answer depends on hidden context, the stem needs repair by
`/interview-writer`.

Soft-skill questions should ask for the **most appropriate** or **most complete**
approach in a defined situation, not pretend that one universal human behavior
is always correct.

---

# 17. Explanation contract

Legacy rows usually have one `explanation` field per option.

## 17.1 Correct explanation

Include:

1. The central mechanism.
2. The decisive condition or guarantee.
3. One practical implication or edge case.

Avoid:

- “This is the correct answer.”
- repeating the option word-for-word;
- interview coaching language;
- generic “best practice” claims.

## 17.2 Distractor explanation

Include:

1. Why the option sounds plausible.
2. The exact broken fact.
3. The adjacent concept or condition it belongs to.
4. A concise corrected model.

Good structure:

```text
Путаница возникает из-за <реальный источник>. Ошибка в том, что <конкретный факт>.
Это правило относится к <соседний механизм>. В данном случае <корректная модель>.
```

Do not force the same lead-in for every row. Vary natural wording while keeping
equal teaching depth.

## 17.3 Length

Prefer 2–4 useful sentences.

If a full tutorial is needed, it belongs in `answer_markdown`, not in every
option explanation.

## 17.4 No fabricated incidents

Do not invent company incidents, financial losses, outage numbers, or production
stories.

Use a named postmortem only when it is present in the source material or verified
from an authoritative source in a separate research task.

A concrete generic symptom is enough:

```text
The task failure remains inside Future and is missed if get() is never called.
```

---

# 18. Code-option rules

When options contain code:

- all snippets must be syntactically comparable;
- use the same Java version and imports;
- keep line counts similar;
- do not make only the correct snippet formatted;
- avoid nonexistent APIs unless existence is the tested property;
- one distractor should not fail compilation while the others test runtime
  behavior unless compile failure is explicitly part of the question;
- explain whether the failure is compile-time, runtime, or semantic;
- preserve the exact code context needed to answer.

For output questions, options should be concrete outputs or behaviors, not mixed
with architectural essays.

---

# 19. Position distribution

Position must not become a hint.

## 19.1 File/topic target

Aim for a balanced distribution across `display_order = 0..3`.

A practical gate:

```text
max(position_count) / min(nonzero_position_count) <= 2.0
```

For tiny batches, avoid obvious streaks.

## 19.2 Do not use a rigid visible cycle

A perfect repeating sequence such as A→B→C→D can also become predictable.

Balance positions while varying order.

## 19.3 Audit query

```bash
sqlite3 modules/quiz-app/data/db/interview.db \
  "SELECT ao.display_order, COUNT(*) AS n
   FROM answer_options ao
   JOIN questions q ON q.id = ao.question_id
   WHERE ao.is_correct = 1
     AND q.topic LIKE '%<topic>%'
   GROUP BY ao.display_order
   ORDER BY ao.display_order;"
```

---

# 20. Drafting workflow

## Step 1: select a small batch

Recommended batch size: 3–6 questions.

Larger batches increase repetition and hallucination risk.

## Step 2: read source rows

```bash
sqlite3 -json modules/quiz-app/data/db/interview.db \
  "SELECT id, topic, question_text, answer_markdown
   FROM questions
   WHERE id IN (<ids>);"
```

## Step 3: classify each question

Record:

- category;
- difficulty;
- learning objective;
- correct model;
- three distractor types.

## Step 4: draft mental models

Do not assign A/B/C/D yet.

## Step 5: draft options

Choose one natural block-level format.

## Step 6: draft explanations

Explanations must repair each misconception.

## Step 7: blind quality review

Hide correct flags and inspect style.

## Step 8: semantic review

Prove exactly one answer.

## Step 9: distribute positions

Assign correct positions after content is stable.

## Step 10: serialize batch

Use UTF-8 and valid JSON.

## Step 11: validate batch before write

Example Python check:

```python
import json
from pathlib import Path

data = json.loads(Path("batch.json").read_text(encoding="utf-8"))

for item in data:
    options = item["options"]
    assert len(options) == 4
    assert sum(bool(option["correct"]) for option in options) == 1
    assert all(option["text"].strip() for option in options)
    assert all(option["explanation"].strip() for option in options)
```

## Step 12: apply transactionally

Prefer the repository helper:

```bash
python3 scripts/insert-options.py /tmp/option-batches/<batch>.json
```

If the helper skips existing rows, confirm this behavior before relying on it.

## Step 13: re-read persisted rows

```bash
sqlite3 -json modules/quiz-app/data/db/interview.db \
  "SELECT question_id, display_order, is_correct, option_text, explanation
   FROM answer_options
   WHERE question_id IN (<ids>)
   ORDER BY question_id, display_order;"
```

## Step 14: run invariant audit

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

## Step 15: report

Include exact IDs, commands, and remaining manual-review items.

---

# 21. Updating existing options safely

Do not replace all rows by default.

## 21.1 Read current state

```bash
sqlite3 -json modules/quiz-app/data/db/interview.db \
  "SELECT *
   FROM answer_options
   WHERE question_id = <qid>
   ORDER BY display_order;"
```

## 21.2 Compare against the reference answer

Determine whether the issue is:

- wrong fact;
- weak distractor;
- visual tell;
- poor explanation;
- duplicate misconception;
- ambiguous stem;
- position metadata;
- invalid row count.

## 21.3 Preserve unaffected fields

Update only the fields required by the task.

## 21.4 Use a transaction

Example:

```sql
BEGIN IMMEDIATE;

UPDATE answer_options
SET option_text = ?,
    explanation = ?
WHERE question_id = ?
  AND display_order = ?;

COMMIT;
```

Verify affected-row counts. Roll back when they differ from expectations.

## 21.5 Never use unbounded replacement

Forbidden:

```sql
DELETE FROM answer_options;
```

Also forbidden:

```sql
DELETE FROM answer_options WHERE question_id IN ();
```

Build the target list explicitly.

---

# 22. Audit queries

## 22.1 Questions without options

```sql
SELECT q.id, q.topic, q.question_text
FROM questions q
WHERE NOT EXISTS (
  SELECT 1
  FROM answer_options ao
  WHERE ao.question_id = q.id
)
ORDER BY q.id;
```

## 22.2 Wrong number of options

```sql
SELECT question_id, COUNT(*) AS option_count
FROM answer_options
GROUP BY question_id
HAVING COUNT(*) <> 4;
```

## 22.3 Wrong number of correct options

```sql
SELECT question_id,
       SUM(CASE WHEN is_correct = 1 THEN 1 ELSE 0 END) AS correct_count
FROM answer_options
GROUP BY question_id
HAVING correct_count <> 1;
```

## 22.4 Duplicate orders

```sql
SELECT question_id, display_order, COUNT(*) AS n
FROM answer_options
GROUP BY question_id, display_order
HAVING COUNT(*) > 1;
```

## 22.5 Duplicate texts

```sql
SELECT question_id, TRIM(option_text) AS normalized_text, COUNT(*) AS n
FROM answer_options
GROUP BY question_id, TRIM(option_text)
HAVING COUNT(*) > 1;
```

## 22.6 Empty explanations

```sql
SELECT question_id, display_order
FROM answer_options
WHERE TRIM(COALESCE(explanation, '')) = '';
```

## 22.7 Short distractor candidates

Character length is only a candidate signal:

```sql
SELECT question_id,
       display_order,
       LENGTH(option_text) AS chars,
       option_text
FROM answer_options
WHERE is_correct = 0
  AND LENGTH(TRIM(option_text)) < 60
ORDER BY chars;
```

Do not rewrite solely because a concise answer is short.

## 22.8 Position distribution

```sql
SELECT display_order, COUNT(*) AS n
FROM answer_options
WHERE is_correct = 1
GROUP BY display_order
ORDER BY display_order;
```

---

# 23. Quality audit rubric

Score each block from 0 to 2 on each dimension.

| Dimension | 0 | 1 | 2 |
|---|---|---|---|
| Factual correctness | wrong/ambiguous | minor uncertainty | verified |
| Unique answer | multiple defensible | needs context | exactly one |
| Distractor plausibility | obvious | mixed | all credible |
| Single primary error | multiple errors | one unclear | one precise |
| Option parity | visible tell | some skew | no form tell |
| Russian readability | wooden | acceptable | natural |
| Visual format parity | correct stands out | minor tell | blind-safe |
| Explanation value | labels only | partial teaching | repairs model |

A block below 13/16 should not be treated as complete.

A score is an editorial aid, not a substitute for semantic review.

---

# 24. Blind-review protocol

## 24.1 Style-only review

Export options without:

- question text;
- correct flag;
- explanation.

Ask the reviewer to identify the “most likely correct-looking” option based only
on style.

Repeated success indicates a visual tell.

## 24.2 Semantic defense review

With the question visible but correct hidden, ask:

- Can each option be defended?
- What hidden assumption would make it true?
- Is that assumption common enough to make the stem ambiguous?

If a distractor is reasonably defensible, rewrite it or clarify the stem.

## 24.3 Human-readable review

Read options aloud.

Rewrite when a sentence sounds unnatural, overloaded, or artificially padded.

---

# 25. Legacy prompt contract (`option.txt`)

`option.txt` may be used by the Java seeder.

Before modifying it:

1. Locate the code that loads it.
2. Confirm required output format.
3. Compare the prompt with this guide.
4. Keep instructions concise enough for runtime use.
5. Preserve strict output parsing requirements.
6. Do not embed repository-specific assumptions that the seeder cannot satisfy.
7. Test one small batch before bulk generation.

If `option.txt` conflicts with this guide:

- do not silently choose one;
- identify which contract the runtime actually enforces;
- update both only when requested;
- report the discrepancy.

Do not reintroduce old emoji-marker templates unless the current parser and user
explicitly require them.

---

# 26. Java seeder

When using:

```bash
./gradlew :quiz-app:seedOptions -PseedArgs="..."
```

First inspect the task implementation and accepted arguments.

Do not assume that historical flags still exist.

Run a small bounded batch first.

After execution:

- inspect inserted rows;
- verify counts;
- verify position distribution;
- check source/prompt metadata;
- confirm that existing rows were not overwritten unexpectedly.

Never place API keys in prompts, files, commits, or shell history. Use environment
variables only.

---

# 27. Error handling and rollback

## 27.1 Helper failure

If `insert-options.py` fails:

- capture the error;
- verify whether the transaction rolled back;
- re-read all target IDs;
- do not rerun blindly.

## 27.2 Partial write

If partial rows exist:

- stop;
- identify affected question IDs;
- restore from backup or repair in a transaction;
- validate before continuing.

## 27.3 Application overwrite

If rows change after insertion:

- stop the workflow;
- identify the running seeder/application;
- do not fight the process with repeated writes;
- report the race.

## 27.4 Schema mismatch

If expected columns are absent:

- inspect migrations;
- adapt to the real schema;
- do not alter the schema unless explicitly asked.

## 27.5 Ambiguous content

If the reference answer is insufficient:

- do not invent the correct answer;
- mark the item for `/interview-writer`;
- preserve existing rows.

---

# 28. Anti-pattern catalog

## 28.1 Short-stub distractor

Correct is a detailed explanation; wrong options are fragments.

## 28.2 Inflated caricature

Wrong option is long but obviously absurd or toxic.

## 28.3 Visually distinguishable correct

Correct alone has polished syntax, code terms, examples, or careful caveats.

## 28.4 Stamped clone

All options are mechanical copies and read unnaturally.

## 28.5 Multiple-error distractor

One option contains several independent false claims.

## 28.6 Negation echo

Wrong option merely negates the correct one.

## 28.7 Adjacent-but-irrelevant technology

The distractor leaves the domain and becomes easy to discard.

## 28.8 Invented API

A nonexistent method is used without existence being the tested fact.

## 28.9 Explanation tautology

“This is wrong” or “This is correct” without teaching.

## 28.10 Hidden-context ambiguity

The answer changes with version, provider, or configuration not stated in the
stem.

## 28.11 Fake production anecdote

An unverified incident is used to make an explanation sound authoritative.

## 28.12 Position pattern

Correct answers follow a visible sequence or are concentrated in one position.

---

# 29. Final block checklist

Before persisting each question, answer “yes” to all:

1. Are there exactly 4 options?
2. Is exactly 1 correct?
3. Do orders map to `0,1,2,3`?
4. Does every option answer the same question?
5. Is the correct answer true under the full stem?
6. Is every distractor unambiguously false?
7. Does each distractor have one primary error?
8. Are the three misconceptions different?
9. Are all terms and APIs real?
10. Could a competent developer sincerely choose each distractor?
11. Is no option a caricature?
12. Is no distractor a simple negation echo?
13. Are lengths and claim counts comparable?
14. Is the correct option not the lone longest or richest?
15. Is punctuation and code formatting comparable?
16. Are all options natural Russian?
17. Does the correct answer differ only by truth?
18. Do explanations repair misconceptions?
19. Are there no fabricated facts or incidents?
20. Is the correct position non-predictable?
21. Does the DB write target only intended IDs?
22. Will the application/seeder not overwrite the result?
23. Was the persisted state re-read?
24. Did all invariant checks pass?

If any answer is “no”, do not mark the item complete.

---

# 30. Batch checklist

Before finishing a batch:

- [ ] Target IDs are explicit.
- [ ] Backup or rollback plan exists.
- [ ] Running-process collision was checked.
- [ ] Every question has 4 options and 1 correct.
- [ ] Mental models were defined before A/B/C/D.
- [ ] Distractors use different misconception types.
- [ ] Option, Plausibility, Russian Readability, and Visual Format Parity pass.
- [ ] No visually distinguishable correct option remains.
- [ ] No stamped clones remain.
- [ ] Correct positions are reasonably balanced.
- [ ] Batch JSON parses.
- [ ] Helper/transaction completed successfully.
- [ ] Persisted rows were re-read.
- [ ] Invariant SQL returned expected values.
- [ ] No unrelated rows or files changed.
- [ ] Remaining ambiguous stems were routed to `/interview-writer`.

---

# 31. Final report format

```markdown
## Legacy option update

### Mode

`CREATE_MISSING` / `REWRITE_EXISTING` / `REPAIR_INVARIANTS` / ...

### Target

- Database:
- Topic:
- Question IDs:

### Changes

- Added:
- Rewritten:
- Skipped:
- Manual review:

### Validation

- Batch JSON:
- Insert/update command:
- Persisted-row check:
- 4-options invariant:
- 1-correct invariant:
- Display-order invariant:
- Position distribution:
- Quality audit:

### Risks

- Running-process collision:
- Ambiguous stems:
- Runtime prompt mismatch:
- Other:
```

---

# 32. Related skills

- `/mcq-quality-fixer` — all JSON MCQ and new MCQ content.
- `/interview-writer` — question stems, theory, reference answers.
- `skill-cross-linker` — generated skill-link blocks; do not edit generated
  markers manually.
