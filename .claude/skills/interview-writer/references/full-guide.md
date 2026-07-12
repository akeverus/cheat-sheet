# Interview Writer — full guide

## 1. Purpose

This guide is the detailed operating procedure for `/interview-writer`.

It governs the theory surface:

```text
cheatsheets/interview/**/*.md
```

The outcome must satisfy three goals simultaneously:

1. **Parser safety** — the quiz application can extract every question.
2. **Human usefulness** — a real candidate can read, remember and explain the topic.
3. **Knowledge consistency** — theory, links, registries and JSON MCQ stay synchronized.

This guide intentionally separates theory writing from MCQ option writing.

---

# 2. Ownership boundaries

## 2.1 This skill owns

- new interview topics;
- question planning;
- `## Q<N>` lifecycle;
- factual answer content;
- readability and information architecture;
- examples, code, tables and diagrams;
- frontmatter compliance;
- local TOC;
- `## See also`;
- Markdown links;
- decisions about whether JSON synchronization is needed;
- coordinated handoff to `/mcq-quality-fixer`.

## 2.2 This skill does not own

- standalone A/B/C/D rewriting;
- JSON schema repair unrelated to theory changes;
- corpus-wide MCQ parity sweeps;
- legacy SQLite `answer_options`;
- bulk metadata normalization;
- production frontend or parser implementation.

Use:

- `/mcq-quality-fixer` for JSON MCQ;
- `/interview-options-writer` for legacy SQLite;
- `/obsidian-metadata` for metadata normalization.

## 2.3 No inline MCQ in Markdown

Since 2026-05-20 MCQ data lives in JSON.

Forbidden in theory files:

```markdown
> [!mcq]
> - [x] ...
> - [ ] ...
```

Do not preserve, generate or restore this historical format.

If legacy inline blocks are found:

1. Confirm the parallel JSON exists.
2. Migrate missing semantic coverage to JSON through `/mcq-quality-fixer`.
3. Remove inline blocks only after synchronization.
4. Validate both surfaces.

---

# 3. Rule precedence

When instructions conflict, use:

1. Explicit user request.
2. Actual parser/schema/runtime behavior.
3. Repository-local instructions and validators.
4. Current source files.
5. Skill responsible for the surface.
6. This guide.
7. Historical examples.

Examples are never more authoritative than current code.

When no safe interpretation exists, stop and report the conflict.

---

# 4. Execution modes

Choose one primary mode.

## 4.1 `CREATE_TOPIC`

Create a new theory file, parallel JSON, links and registry entries.

## 4.2 `EXPAND_TOPIC`

Append missing questions without deleting or renumbering existing ones.

## 4.3 `SEMANTIC_UPDATE`

Correct or deepen facts, mechanisms, conditions, examples or trade-offs.

This may require JSON updates.

## 4.4 `READABILITY_REFACTOR`

Improve Russian, layout and cognitive load without changing meaning.

JSON should normally remain byte-for-byte unchanged.

## 4.5 `STRUCTURE_REPAIR`

Fix frontmatter, headings, TOC, links, section placement and parser compatibility.

Do not mix in broad factual rewrites unless required.

## 4.6 `QUESTION_LIFECYCLE`

Add, split, merge, retire or delete stable `Q<N>` identifiers.

Requires reference and JSON analysis.

## 4.7 `RENAME_TOPIC`

Rename the Markdown file/topic and update JSON, `topic_slug`, links and registries.

## 4.8 `AUDIT_ONLY`

Read and report. No writes.

---

# 5. Repository map

Typical surfaces:

```text
cheatsheets/interview/<category>/<topic>-interview.md
modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json
modules/quiz-app/src/main/resources/seed/mcq-schema.json
docs/golden-examples.md
TOC.md
README.md
scripts/
.claude/skills/
```

Do not assume every repository uses exactly these paths. Confirm them.

---

# 6. Preflight

Before editing:

```bash
pwd
git rev-parse --show-toplevel
git status --short
```

Then inspect:

```bash
find cheatsheets/interview -maxdepth 2 -type f -name '*-interview.md' | sort
find modules/quiz-app/src/main/resources/seed/mcq -type f -name '*.json' | sort
```

Read:

- target Markdown in full;
- parallel JSON in full;
- relevant neighboring topics;
- parser code when formatting is uncertain;
- frontmatter skill;
- MCQ skill when JSON may change.

Do not edit a dirty target file without understanding the existing diff.

---

# 7. Research and factuality

## 7.1 Research triggers

Research is mandatory when:

- adding new factual content;
- changing a mechanism or guarantee;
- describing version-dependent behavior;
- adding defaults, limits or numerical thresholds;
- referencing a security recommendation;
- citing a benchmark;
- naming a company incident;
- stating a best practice as universal.

Research may be skipped for:

- spelling;
- punctuation;
- heading normalization;
- paragraph splitting;
- unchanged wording moved between sections.

## 7.2 Source priority

Use:

1. Official documentation.
2. Specifications, standards, JEPs, RFCs and source code.
3. Maintainer documentation or release notes.
4. Peer-reviewed or primary research.
5. High-quality secondary explanations.
6. Community discussion only as evidence of a misconception, not of truth.

Do not treat Baeldung as more authoritative than official documentation.

## 7.3 Version-sensitive claims

A version-sensitive statement must specify enough context:

- product/library version;
- provider or implementation;
- configuration;
- runtime mode;
- date checked when the repository tracks freshness.

Avoid:

```text
currently
latest
always by default
since recently
```

without a concrete version.

## 7.4 No fabricated evidence

Never invent:

- outages;
- company architecture;
- financial losses;
- benchmark results;
- internal practices;
- dates;
- quotes.

Use a generic production scenario when a verified incident is unavailable.

## 7.5 Research notes

When the repository has a freshness sidecar or source-log convention, update it.

For JSON MCQ version-sensitive claims, follow `/mcq-quality-fixer`.

---

# 8. File naming and placement

Use:

```text
<topic>-interview.md
```

Rules:

- lowercase;
- hyphen-separated;
- meaningful topic name;
- no version suffix unless the topic itself is version-specific;
- place in the closest existing category;
- create a new category only when no current category is defensible.

Parallel JSON uses the same basename.

---

# 9. Frontmatter contract

Respect the repository metadata contract.

Typical shape:

```yaml
---
title: "Вопросы на собеседовании: <Topic>"
description: "<one-line summary>"
tags:
  - interview
  - <category>
  - <topic>
type: "interview"
difficulty: "intermediate"
aliases:
  - "<Topic> interview"
  - "<Topic> собеседование"
  - "<Topic> вопросы"
related:
  - "<relative or repository-approved reference>"
updated: "YYYY-MM-DD"
---
```

Do not blindly add fields unsupported by the current metadata schema.

Rules:

- valid YAML;
- no duplicate keys;
- lists are YAML lists;
- `updated` changes only when the file changes;
- title and description match the real scope;
- aliases help discovery rather than repeat punctuation variants;
- tags use established repository vocabulary.

When available:

```bash
python3 .claude/skills/obsidian-metadata/scripts/normalize_frontmatter.py --apply <file>
```

First inspect the command and supported arguments.

---

# 10. Document skeleton

Recommended structure:

```markdown
---
...
---

# Вопросы на собеседовании: `<Topic>`

Короткое введение в тему и ожидания на собеседовании.

Дата последнего обновления: YYYY-MM-DD

## Полезные ссылки

### Официальная документация

- [Название](https://...) — что именно там искать.

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [Q1. ...](#...)
- [Q2. ...](#...)
- [See also](#see-also)

## Q1. Что такое ...?

...

## Q2. (!) Почему ...?

...

---

## See also

- [Связанная тема](relative-path.md) — связь с текущей темой.
```

Do not add empty ornamental sections.

---

# 11. Question heading contract

Parser-safe heading:

```markdown
## Q<N>. <Question>
```

Important question:

```markdown
## Q<N>. (!) <Question>
```

Requirements:

- exactly `##`;
- uppercase `Q`;
- positive integer;
- dot after number;
- one space;
- optional `(!)` after number;
- clear question text;
- no trailing placeholder;
- no duplicate number.

The parser may accept looser syntax, but writers should emit the canonical form.

---

# 12. Stable question IDs

`Q<N>` is a stable public identifier.

## 12.1 New topic

Use sequential `Q1..Qn`.

## 12.2 Existing topic

Append after the current maximum ID.

Do not insert a new question into the middle by renumbering everything.

## 12.3 Existing gaps

Do not close gaps automatically.

A gap may be intentional because external links point to later IDs.

## 12.4 Deletion

Before deleting:

```bash
rg -n "<topic>(-interview)?#Q<N>|#Q<N>" cheatsheets modules docs
```

Then:

- remove the Markdown section;
- remove matching JSON `q_number`;
- update TOC;
- update inbound links;
- do not renumber unaffected questions.

If references cannot be safely repaired, prefer deprecation text over deletion.

## 12.5 Split

When splitting one question:

- preserve the original ID for the main concept;
- append new IDs for additional concepts;
- update JSON and links.

## 12.6 Merge

When merging:

- preserve the stronger or more referenced ID;
- retire the other explicitly;
- update JSON and inbound references;
- do not silently reuse an old number for a new meaning.

---

# 13. Question quality

A good question:

- tests one coherent concept;
- has clear scope;
- is grammatically complete;
- does not embed the answer;
- has an interview-relevant angle;
- can be answered without hidden assumptions;
- uses canonical terminology;
- states version/provider context when necessary.

Avoid:

- double-barrelled questions;
- “расскажите всё про…”;
- vague “как работает?” without scope;
- trivia without interview value;
- questions whose answer is purely subjective;
- multiple hidden subquestions that require a wall of text.

Split large questions into related questions.

---

# 14. Answer architecture

Use the minimum structure needed for clarity.

## 14.1 Definition question

Recommended flow:

1. One-sentence definition.
2. Mechanism.
3. Example.
4. Boundary or common confusion.
5. Takeaway.

## 14.2 Comparison question

Use:

- one short framing paragraph;
- a Markdown table;
- one example;
- a decision rule.

Table columns should reflect actual decision criteria, not generic “pros/cons”.

## 14.3 Behavioral question

Use:

- code;
- expected behavior;
- step-by-step mechanism;
- edge cases.

Do not bury the answer after a long introduction.

## 14.4 Architecture/system-design question

Use:

- scope and assumptions;
- components;
- request/event flow;
- failure modes;
- trade-offs;
- observability;
- scaling boundaries.

Prefer a sequence diagram when interaction order matters.

## 14.5 Operational question

Use:

- symptom;
- likely causes;
- diagnostic steps;
- safe mitigation;
- prevention.

Avoid presenting destructive commands without context.

## 14.6 Soft-skill question

Use:

- situational context;
- goals and constraints;
- recommended behavior;
- why it works;
- alternatives and risks.

Do not present one universal script as morally absolute.

---

# 15. Human-readable Russian

## 15.1 Style

Write like a strong engineer explaining to another engineer.

Prefer:

```text
`HashMap` хранит элементы в массиве bucket-ов. Индекс bucket-а вычисляется из hash ключа.
```

Avoid:

```text
Необходимо отметить, что вышеупомянутая структура данных является мощным и гибким инструментом.
```

## 15.2 Paragraphs

- 2–4 sentences by default;
- split long reasoning;
- one idea per paragraph;
- avoid repeated conclusions.

## 15.3 Lists

Use a list for 3+ parallel items.

Do not turn every answer into a nested checklist.

## 15.4 Terminology

- API names in `backticks`;
- one term per concept;
- define an acronym at first use;
- avoid random English/Russian switching;
- preserve standard English terms when translation harms precision.

## 15.5 Cognitive load

A file should be comprehensive but not exhausting.

Use:

- descriptive subheadings inside long answers;
- tables;
- code;
- short takeaways;
- progressive detail.

Do not repeat the same explanation in prose, table and list.

## 15.6 Filler blacklist

Remove phrases that add no content:

```text
важно отметить, что
стоит упомянуть
необходимо понимать
как известно
данный подход является мощным
в современном мире
в рамках данного вопроса
```

A phrase is not banned when it carries real contrast; remove generic use.

---

# 16. Examples and code

## 16.1 When examples are required

Use an example when the concept involves:

- API behavior;
- lifecycle;
- configuration;
- SQL;
- concurrency;
- serialization;
- failure handling;
- performance trade-offs.

A trivial definition may not need a full code block.

## 16.2 Code rules

- correct language fence;
- complete enough to understand;
- minimal irrelevant setup;
- compilable when presented as compilable;
- comments only where they teach;
- no secrets;
- no fabricated library methods;
- version assumptions stated when important.

## 16.3 Output and failure

For behavioral code, state:

- output;
- exception;
- transaction result;
- concurrency guarantee;
- whether behavior is deterministic.

Do not imply implementation-defined behavior is guaranteed.

---

# 17. Tables

Use tables for comparisons such as:

- API A vs API B;
- isolation levels;
- collection types;
- delivery semantics;
- consistency models.

Good table columns:

- property;
- mechanism;
- guarantee;
- cost;
- use case;
- trap.

Avoid wide tables with long paragraphs in every cell.

If a table becomes unreadable, split it or use subsections.

---

# 18. Diagrams

Use diagrams only when spatial or temporal relationships add value.

Prefer:

- sequence diagrams for actor/service interaction;
- state diagrams for lifecycle;
- small flow diagrams for decision logic;
- component diagrams for architecture.

Do not add a diagram that repeats the preceding paragraph.

Keep labels in Russian where practical and API/service names canonical.

For Mermaid:

- keep syntax compatible with the repository renderer;
- avoid excessive styling;
- validate rendering if a tool exists;
- use 1–3 meaningful diagrams per large file, not one per question.

---

# 19. Coverage planning

## 19.1 New topic plan

Before writing, build a coverage map:

1. Fundamentals.
2. Core mechanics.
3. API usage.
4. Failure modes.
5. Performance.
6. Security.
7. Operations/observability.
8. Version evolution.
9. Testing.
10. Production decisions.

Not every topic needs all ten.

## 19.2 Question count

Question count follows scope, not a fixed quota.

A new topic may need 12, 25 or 50 questions.

Do not pad to meet a number.

## 19.3 Difficulty progression

Order from basic to advanced:

- terminology;
- mechanism;
- application;
- edge cases;
- production trade-offs.

## 19.4 Important marker

Use `(!)` for questions that are:

- frequently asked;
- foundational;
- high-signal for level;
- easy to answer superficially but hard to explain correctly.

A typical target is 20–30%, but do not force an exact ratio.

---

# 20. Table of contents

The TOC must reflect actual headings.

Requirements:

- include `Полезные ссылки`;
- include every question or the repository-approved grouped form;
- include `See also`;
- preserve grouping by subtopic;
- update after additions/deletions/renames;
- avoid broken hand-written anchors.

When anchors are complex, generate or verify them with the repository's Markdown
renderer or helper.

Do not trust visual inspection alone for Cyrillic anchors.

---

# 21. Links and knowledge graph

## 21.1 Markdown links

Theory files use standard relative Markdown links:

```markdown
[Распределённые системы](../architecture/distributed-systems-interview.md)
```

Always include `.md` unless the repository explicitly requires another style.

## 21.2 `## See also`

Requirements:

- top-level `##`;
- final substantive section;
- separated by `---`;
- present in TOC;
- at least 5 useful links when the repository has enough related topics;
- same-category links first;
- each link includes a short reason.

Do not add irrelevant links just to reach a count.

## 21.3 Inline links

Add inline links only where they help the reader continue a concept.

Do not turn every technical term into a link.

## 21.4 Link validation

Verify target existence:

```bash
python3 - <<'PY'
from pathlib import Path
import re, sys

path = Path("<file>")
text = path.read_text(encoding="utf-8")
bad = []
for target in re.findall(r'\[[^\]]+\]\(([^)]+\.md)(?:#[^)]+)?\)', text):
    resolved = (path.parent / target).resolve()
    if not resolved.exists():
        bad.append(target)
print("\n".join(bad))
sys.exit(1 if bad else 0)
PY
```

Adapt the script if links contain anchors.

---

# 22. Useful links section

Prefer primary sources.

Structure:

```markdown
## Полезные ссылки

### Официальная документация

- [Reference](https://...) — API and guarantees.
- [Guide](https://...) — configuration and examples.

### Дополнительные материалы

- [Article](https://...) — focused explanation of a difficult edge case.
```

Do not create a link dump.

Every link should have a reason.

---

# 23. Theory ↔ JSON synchronization

## 23.1 Core identity

Markdown heading:

```text
Q17
```

must map to JSON:

```json
{"q_number": 17}
```

The meaning of the JSON question must match the theory question.

## 23.2 Cosmetic change

Examples:

- typo;
- punctuation;
- paragraph split;
- heading wording improved without semantic shift;
- code formatting.

Action:

- leave JSON unchanged;
- verify that question meaning still matches.

## 23.3 Semantic change

Examples:

- changed guarantee;
- changed default;
- changed version;
- added decisive condition;
- corrected mechanism;
- changed the question's tested concept.

Action:

1. Read `/mcq-quality-fixer`.
2. Read `docs/golden-examples.md`.
3. Update JSON `question_text`, options and/or sections as required.
4. Preserve one correct answer.
5. Run all MCQ gates.

## 23.4 New question

1. Add `## Q<N>`.
2. Add JSON `q_number = N`.
3. Use at least one MCQ block when the concept is objectively testable.
4. Open-ended design questions may have no MCQ only when a unique answer would be artificial.
5. Validate JSON.

## 23.5 Deleted question

1. Remove Markdown section.
2. Remove JSON entry.
3. Update links and TOC.
4. Leave other IDs unchanged.
5. Validate.

## 23.6 Renamed topic

1. Rename Markdown.
2. Rename JSON.
3. Update `topic_slug`.
4. Update TOC/README.
5. Update inbound Markdown links.
6. Update JSON `related` references.
7. Validate all affected JSON files.

## 23.7 No shadow JSON

Do not create a second JSON under a different basename for the same topic.

---

# 24. MCQ quality handoff

When JSON must change, `/mcq-quality-fixer` is authoritative.

At minimum enforce:

- exactly 4 options;
- exactly 1 correct;
- valid schema;
- mental-model-first distractors;
- Single-Delta where appropriate;
- Option Parity;
- Plausibility Parity;
- Russian Readability Parity;
- Visual Format Parity;
- **shared surface skeleton (`SKEL`) for all 4 options — hard gate**;
- no visually distinguishable correct option;
- no inflated caricature;
- no stamped clone;
- fact freshness for version-sensitive topics.

The test:

```text
If correct=true is hidden, the correct option must not be guessable by appearance.
All four options share one surface skeleton (opening/clauses/punctuation/backticks class).
```

This guide does not duplicate the full distractor canon.

---

# 25. Creating a new topic

## Step 1: discover the category

Inspect neighboring files and naming conventions.

## Step 2: research

Collect primary sources and version assumptions.

## Step 3: create a coverage plan

List subtopics and candidate questions.

## Step 4: assign stable IDs

Number from `Q1`.

## Step 5: draft theory

Write questions and answers in batches, reviewing each batch.

## Step 6: add examples/tables/diagrams

Only where useful.

## Step 7: build local TOC

Match the final headings.

## Step 8: add links

Useful links and `See also`.

## Step 9: create parallel JSON

Delegate option generation to `/mcq-quality-fixer`.

## Step 10: update registries

Update `TOC.md`, counters and `README.md` only where the repository requires it.

## Step 11: validate

Run Markdown, link and JSON checks.

---

# 26. Expanding an existing topic

## Step 1: read the file completely

Do not append based only on the last heading.

## Step 2: inventory current coverage

Record:

- question ID;
- subtopic;
- importance;
- answer depth;
- code/example;
- JSON presence.

## Step 3: identify real gaps

Avoid near-duplicate questions.

## Step 4: research gaps

Use primary sources.

## Step 5: append stable IDs

Use IDs after the current maximum.

## Step 6: integrate into TOC

Group by concept, even though IDs are appended.

## Step 7: create JSON entries

Use matching `q_number`.

## Step 8: validate and report

Preserve all existing questions unless explicitly correcting them.

---

# 27. Improving readability without semantic drift

Use `READABILITY_REFACTOR`.

Allowed:

- split paragraphs;
- simplify syntax;
- remove filler;
- improve headings;
- convert repeated prose to lists/tables;
- improve code formatting;
- fix punctuation;
- clarify existing examples without changing behavior.

Not allowed without reclassification as `SEMANTIC_UPDATE`:

- changing a guarantee;
- adding a new condition;
- changing defaults;
- changing API behavior;
- replacing a version;
- changing the recommended decision.

Before and after, compare the factual propositions.

JSON should remain unchanged.

---

# 28. Semantic update procedure

1. Identify the exact old claim.
2. Identify the corrected claim.
3. Gather primary evidence.
4. Mark affected question IDs.
5. Update theory.
6. Update JSON for those IDs.
7. Check cross-links and TOC.
8. Run validators.
9. Report the factual delta and source scope.

Do not mix unrelated modernization into the same edit.

---

# 29. Structure repair procedure

Audit:

- frontmatter;
- title;
- useful links;
- TOC;
- question headings;
- duplicate IDs;
- `See also`;
- inline MCQ;
- relative links;
- trailing malformed sections.

Fix structure before adding content.

Do not delete content merely because its placement is wrong.

---

# 30. Rename procedure

Before rename:

```bash
rg -n "<old-basename>|<old-title>" .
```

Then atomically update:

- Markdown filename;
- JSON filename;
- JSON `topic_slug`;
- local links;
- inbound links;
- JSON `related`;
- `TOC.md`;
- `README.md`;
- freshness sidecar names;
- scripts/config references.

Run a second repository-wide search for the old name.

---

# 31. Audit-only procedure

Produce an evidence-based report with:

- parser issues;
- metadata issues;
- coverage gaps;
- shallow answers;
- outdated claims;
- missing examples;
- missing or stale JSON;
- broken links;
- duplicate questions;
- cognitive-load problems;
- recommended priority.

Do not modify files.

Use severity:

- `CRITICAL` — parser/schema break, false fact, duplicate ID.
- `HIGH` — theory/JSON desync, broken links, ambiguous answer.
- `MEDIUM` — shallow answer, missing example, poor structure.
- `LOW` — style polish.

---

# 32. Validation

Discover available scripts first:

```bash
find scripts .claude/skills -type f | sort | rg 'interview|markdown|frontmatter|mcq|link|toc'
```

## 32.1 Heading audit

```bash
grep -n '^## Q[0-9]\+\.' <file>
```

Python duplicate check:

```bash
python3 - <<'PY'
from pathlib import Path
import re, sys

path = Path("<file>")
text = path.read_text(encoding="utf-8")
ids = [int(x) for x in re.findall(r'^## Q(\d+)\.\s+', text, flags=re.M)]
duplicates = sorted({x for x in ids if ids.count(x) > 1})
print({"count": len(ids), "duplicates": duplicates, "ids": ids})
sys.exit(1 if duplicates else 0)
PY
```

## 32.2 Inline MCQ ban

```bash
if rg -n '^> \[!mcq\]' <file>; then
  echo "Legacy inline MCQ found"
  exit 1
fi
```

## 32.3 JSON validation

When JSON changed:

```bash
bash scripts/verify-mcq-json.sh <json>
python3 scripts/mcq-answer-parity-gate.py <json>
```

Run additional gates defined by `/mcq-quality-fixer`.

## 32.4 Q-number sync

```bash
python3 - <<'PY'
from pathlib import Path
import json, re, sys

md = Path("<md>")
js = Path("<json>")

md_ids = set(map(int, re.findall(r'^## Q(\d+)\.\s+', md.read_text(encoding="utf-8"), re.M)))
data = json.loads(js.read_text(encoding="utf-8"))
json_ids = {int(item["q_number"]) for item in data.get("questions", [])}

dangling = sorted(json_ids - md_ids)
print({"md_only": sorted(md_ids - json_ids), "json_dangling": dangling})
sys.exit(1 if dangling else 0)
PY
```

`md_only` may be acceptable only for explicitly open-ended questions.

## 32.5 Links

Run the repository link checker when available.

At minimum verify local Markdown targets.

## 32.6 Frontmatter

Run the metadata validator/normalizer in check mode when supported.

## 32.7 Registry checks

Confirm `TOC.md` and counters when the file count or question count changes.

## 32.8 Diff review

```bash
git diff -- <affected files>
git status --short
```

Check that no unrelated content changed.

---

# 33. Safety and atomicity

A theory lifecycle change is one coherent unit:

```text
Markdown
+ JSON
+ links
+ registries
+ validation
```

Do not leave the repository in a half-synced state.

Rules:

- explicit paths only;
- bounded edits;
- preserve dirty unrelated files;
- no `git add -A`;
- no push without request;
- no destructive mass formatting;
- no automatic renumber;
- no deletion of open questions or important sections without user approval.

---

# 34. Anti-pattern catalog

## 34.1 Inline MCQ resurrection

Restoring historical `> [!mcq]` blocks.

## 34.2 Theory/JSON desync

Changing question meaning but leaving old MCQ.

## 34.3 Cosmetic churn

Reformatting the whole file while changing one answer.

## 34.4 Question inflation

Adding near-duplicates to increase count.

## 34.5 Textbook wall

Long academic prose without decision value.

## 34.6 Shallow definition

One sentence that does not explain mechanism or boundary.

## 34.7 Fake production authority

Unverified company or incident claims.

## 34.8 Universal best practice

A recommendation presented without conditions or trade-offs.

## 34.9 Hidden version dependency

Claim changes by version but the answer does not say which.

## 34.10 TOC drift

Headings changed but TOC not updated.

## 34.11 Broken `See also`

Nested `### See also`, wrong placement or nonexistent links.

## 34.12 Mass renumbering

Closing gaps and breaking references.

## 34.13 Link syntax mixing

Using wikilinks in theory while standard Markdown is required.

## 34.14 Mermaid decoration

Diagram added without explanatory value.

## 34.15 Mechanical answer template

Every answer has identical headings regardless of need.

---

# 35. Definition of Done

## 35.1 New topic

- [ ] Correct category and filename.
- [ ] Valid frontmatter.
- [ ] Complete but non-padded coverage.
- [ ] Canonical `Q<N>` headings.
- [ ] New IDs sequential from `Q1`.
- [ ] 20–30% important questions when natural.
- [ ] Human-readable Russian.
- [ ] Primary-source links.
- [ ] Useful examples/tables/diagrams.
- [ ] TOC matches headings.
- [ ] `See also` at end with valid links.
- [ ] No inline MCQ.
- [ ] Parallel JSON created where testable.
- [ ] JSON gates pass.
- [ ] Registries updated.
- [ ] Diff contains no unrelated changes.

## 35.2 Existing topic

- [ ] Existing useful content preserved.
- [ ] Stable IDs preserved.
- [ ] New IDs appended.
- [ ] Semantic changes researched.
- [ ] Readability improved without drift.
- [ ] TOC and links updated.
- [ ] JSON sync decision documented.
- [ ] Required JSON changes validated.
- [ ] No inline MCQ.
- [ ] All validators pass.

## 35.3 Audit-only

- [ ] No files changed.
- [ ] Findings cite concrete paths and IDs.
- [ ] Severity assigned.
- [ ] Recommendations are actionable.
- [ ] Uncertainty is explicit.

---

# 36. Final report template

```markdown
## Interview content update

### Mode

`CREATE_TOPIC` / `EXPAND_TOPIC` / `SEMANTIC_UPDATE` / ...

### Files

- Markdown:
- JSON:
- Registries:
- Other:

### Question lifecycle

- Added:
- Semantically changed:
- Readability-only:
- Deleted/retired:
- Stable IDs preserved:

### Research

- Primary sources checked:
- Version/config assumptions:

### JSON synchronization

- Required:
- Updated q_number:
- Skipped as cosmetic:

### Validation

- Frontmatter:
- Heading/parser:
- Duplicate IDs:
- Inline MCQ ban:
- TOC:
- Links:
- Q-number sync:
- JSON schema:
- Answer parity:
- Tests:

### Remaining risk

- ...
```

---

# 37. Related skills

- `/mcq-quality-fixer` — JSON MCQ and option quality.
- `/interview-options-writer` — legacy SQLite options only.
- `/obsidian-metadata` — metadata normalization.
- `skill-cross-linker` — generated related-skill blocks.
