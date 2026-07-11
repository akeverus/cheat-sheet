---
name: interview-writer
description: >-
  Создавать, расширять, структурно чинить и переписывать теоретические interview-cheatsheets
  в cheatsheets/interview/**: frontmatter, ## Q<N> секции, TOC, русскоязычные ответы,
  примеры, таблицы, диаграммы, ссылки и синхронизацию с JSON MCQ sidecar. Используй для
  работы с теорией и жизненным циклом вопросов. Для самостоятельной генерации или глубокой
  правки вариантов A/B/C/D в seed/mcq/** используй mcq-quality-fixer. Не добавляй inline
  > [!mcq] блоки в Markdown: с 2026-05-20 MCQ хранятся отдельно в JSON.
---

# Interview Writer

## Status

Authoritative skill for the **theory surface** of interview content:

```text
cheatsheets/interview/**/*.md
```

This skill owns:

- creation of new interview topics;
- addition and lifecycle of `## Q<N>` questions;
- factual and editorial quality of answers;
- parser-compatible Markdown structure;
- table of contents;
- frontmatter compliance;
- internal and external links;
- examples, tables, code and useful diagrams;
- synchronization decisions between theory and JSON MCQ.

It does **not** own deep option writing. Option quality, distractor generation,
schema repair, parity gates and JSON-only work belong to `/mcq-quality-fixer`.

## Hard routing rule

Classify the request before editing.

| Request | Owner |
|---|---|
| Create or improve theory in `cheatsheets/interview/**` | `/interview-writer` |
| Add, remove, rename or semantically change `## Q<N>` | `/interview-writer`, then sync JSON |
| Only rewrite A/B/C/D, explanations or sections in `seed/mcq/**` | `/mcq-quality-fixer` |
| Legacy SQLite `answer_options` / `option.txt` | `/interview-options-writer` |
| Normalize metadata across many files | `/obsidian-metadata`, while this skill preserves its contract |
| Audit only, with no writes | This skill in `AUDIT_ONLY` mode |

If a task touches both theory and JSON, this skill orchestrates the change, but
the JSON content must still follow `/mcq-quality-fixer`.

## Non-negotiable architecture

Since 2026-05-20 the two surfaces are separate:

```text
Theory:
cheatsheets/interview/<category>/<topic>-interview.md

MCQ:
modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json
```

Markdown contains theory only.

**Never add or restore inline `> [!mcq]` blocks.**

The two surfaces share stable `q_number` values and meaning, but have different
formatting and responsibilities.

## Progressive loading

Before any write:

1. Read `references/full-guide.md` completely.
2. Read the target `.md` completely.
3. Read the parallel JSON when it exists.
4. Read relevant repository instructions and parser code when the contract is unclear.
5. Read `/mcq-quality-fixer` before creating or semantically updating JSON.
6. Read `docs/golden-examples.md` before writing or reviewing MCQ options.

Do not rely on remembered rules when the repository contains an explicit contract.

## Execution modes

Choose exactly one primary mode:

- `CREATE_TOPIC`
- `EXPAND_TOPIC`
- `SEMANTIC_UPDATE`
- `READABILITY_REFACTOR`
- `STRUCTURE_REPAIR`
- `QUESTION_LIFECYCLE`
- `RENAME_TOPIC`
- `AUDIT_ONLY`

Do not mix broad restructuring, factual updates and mass option rewriting in one
unbounded pass.

## Authority order

When rules conflict:

1. User instruction.
2. Current parser/schema/runtime behavior.
3. Current repository instructions and validators.
4. Current source files.
5. `/mcq-quality-fixer` for JSON MCQ.
6. This skill and its full guide.
7. Historical examples.

Mark unresolved conflicts instead of inventing a contract.

## Research contract

For factual or semantic changes:

1. Prefer official documentation, specifications, JEPs, RFCs and primary sources.
2. Use high-quality secondary sources only to clarify or find examples.
3. Verify version-sensitive claims against current primary documentation.
4. Record assumptions about version, provider, configuration and scope.
5. Do not fabricate production incidents, benchmark numbers or company practices.
6. Do not use vague “latest/current” claims without a version and check date.

Cosmetic edits do not require fresh research when facts are unchanged.

## Stable question identity

`Q<N>` is a stable identifier, not decorative numbering.

- New file: number sequentially from `Q1`.
- Existing file: append new questions after the highest existing number.
- Do not renumber existing questions merely to close gaps.
- Delete or merge a question only when explicitly requested.
- Before deletion or rename, search all Markdown and JSON references.
- Update the parallel JSON and all inbound links atomically.

## Human-readable Russian

All explanatory prose must be natural Russian.

Use English only for:

- canonical technology/product names;
- API, classes, methods, annotations and configuration keys in `backticks`;
- code;
- file paths;
- terms without a stable Russian equivalent.

Write engineer-to-engineer:

- direct answer first;
- short paragraphs;
- lists for 3+ parallel items;
- tables for comparisons;
- examples where they improve understanding;
- explicit trade-offs and failure modes;
- no academic padding;
- no marketing language;
- no mechanically repeated template in every answer.

Readability is more important than forcing every answer into the same shape.

## Answer quality

A strong answer normally covers:

1. Direct answer or definition.
2. Mechanism or reason.
3. Practical example.
4. Trade-off, limitation or common trap.
5. Concise takeaway when useful.

Do not turn this into a rigid five-heading template. Use only the structure the
question needs.

The answer must help a Middle/Senior candidate explain the topic, not merely
recognize a definition.

## Formatting contract

Question headings:

```markdown
## Q1. Что такое ...?
## Q2. (!) Почему ...?
```

Rules:

- exact top-level question heading: `## Q<N>.`;
- `(!)` goes after the number and dot;
- question text is clear, grammatical and unambiguous;
- no placeholder headings;
- no duplicate `Q<N>`;
- `## See also` is a top-level section at the end;
- `## Содержание` reflects the actual questions;
- no inline MCQ blocks.

## Cross-link contract

Inside Markdown cheatsheets use standard relative Markdown links:

```markdown
[CAP-теорема](cap-theorem-interview.md)
[Kafka](../messaging/kafka-interview.md)
```

Do not use Obsidian wikilinks in theory files unless the repository contract
explicitly changes.

Inside JSON MCQ `related` sections, follow `/mcq-quality-fixer` and the JSON
schema; that surface may use a different link syntax.

Never mix the two link contracts.

## JSON synchronization matrix

| Markdown change | JSON action |
|---|---|
| Typo, punctuation, formatting only | No JSON change |
| Answer wording changed but meaning unchanged | Usually no JSON change; verify only |
| Question wording changed semantically | Update `question_text` and affected options/sections |
| New `Q<N>` | Add matching `q_number` |
| Delete `Q<N>` | Remove matching JSON entry; do not renumber others |
| Split one question into several | Add new stable IDs and update links |
| Merge questions | Preserve one ID, explicitly retire the other, update references |
| Rename topic/file | Rename JSON, update `topic_slug`, paths and inbound references |

For every JSON change:

```bash
bash scripts/verify-mcq-json.sh <json>
python3 scripts/mcq-answer-parity-gate.py <json>
```

Run additional structure/stamp/blind checks required by `/mcq-quality-fixer`.

## Validation contract

Discover repository validators first. At minimum validate:

1. Frontmatter parses.
2. Every question heading matches the parser contract.
3. No duplicate question numbers.
4. TOC and question headings agree.
5. `## See also` exists at the end and links resolve.
6. No inline `> [!mcq]` blocks exist.
7. Parallel JSON references only existing `Q<N>`.
8. JSON validation passes when JSON changed.
9. Registries/counters are updated when required.
10. No unrelated files changed.

Never claim success when a required check fails.

## Safety contract

- Read the complete file before editing.
- Preserve all useful existing content unless explicitly replacing it.
- Do not silently shorten or delete answers.
- Do not mass-renumber.
- Do not rewrite unrelated topics.
- Do not use `git add -A`.
- Do not commit or push unless explicitly requested.
- Do not overwrite dirty files without resolving the collision.
- Keep semantic changes and formatting-only changes distinguishable in the diff.
- Stop when the parser contract or source facts are ambiguous.

## Definition of Done

A topic is done only when:

- the Markdown is parser-compatible;
- the theory is accurate, current and useful;
- Russian is readable;
- stable IDs are preserved;
- TOC, links and frontmatter are coherent;
- no inline MCQ remains;
- JSON is synchronized when semantics changed;
- all required validators pass;
- the final report names files, question IDs, checks and remaining risk.

## Final response

Report:

1. Mode used.
2. Files changed.
3. `Q<N>` added, changed, deleted or preserved.
4. Whether JSON sync was required.
5. Validation commands and results.
6. Remaining assumptions or manual-review items.

<!-- skill-cross-linker:start -->
## Related skills

**Route to:**
- `/mcq-quality-fixer` — JSON MCQ, distractors, sections, parity and validation.
- `/interview-options-writer` — legacy SQLite `answer_options` only.
- `/obsidian-metadata` — bulk metadata normalization.

<!-- skill-cross-linker:end -->
