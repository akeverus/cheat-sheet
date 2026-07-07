# Interview Content Improvement Backlog

## Inventory summary

- Total categories: 24 `.md` categories, 24 MCQ categories
- Total interview files: 318
- Total MCQ JSON files: 318
- Files without MCQ: 0
- MCQ files without `.md`: 0
- Files with parser issues: 0 bad `## Q<N>.` headings, 0 JSON q_number mismatches
- Files missing TOC: 12
- Files missing `## See also`: 0
- Files with shallow answers: 0 by the current `<160 lines` heuristic
- Files with weak MCQ: 315 parity-flagged files from `scripts/audit-mcq-parity.py --top 5`
- Files with Russian readability flags: 265

## High priority

| Priority | Area | File | Problem | Proposed action | Impact | Risk | Status |
|---|---|---|---|---|---|---|---|
| P0 | Tooling | `scripts/audit-mcq-parity.py` | Audit had partial Russian Readability support but not canonical `RUS_READABILITY_*` signals; `--top` summaries were misleading in some modes. | Add canonical readability signals, schema-like robustness checks, explicit `--verbose-skip`, report-limit fixes, and selftests. | Makes future MCQ cleanup measurable and CI-ready. | Heuristics can over-report candidates; manual review is still required. | Done on 2026-07-01 |
| P1 | MCQ parity | `modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json` | Top parity candidate: correct option was longest in 100% of blocks and most technical in 91%; after five batches it is 89.1% longest with 35 flagged blocks left. | Continue manual rewrite from Q21 onward with Option Parity, Plausibility Parity, Russian Readability Parity, and mental-model workflow. | Reduces answer guessability in a core backend topic. | Large file; should be done in small validated batches. | In progress: Q1-Q20 cleaned on 2026-07-02 |
| P1 | MCQ parity | `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/java/java-concurrency-interview.json` | Critical parity candidate: 49 flagged blocks, correct longest rate 89%. | Improve distractor density and structure without changing `q_number` semantics. | High-value Java interview topic. | Existing user changes may already touch nearby MCQ files; check git status before editing. | Backlog |
| P1 | Russian readability | `modules/quiz-app/src/main/resources/seed/mcq/system-design/design-payment-system-interview.json` | Highest readability severity: 15 flagged blocks, 50% flagged rate. | Review `RUS_READABILITY_*` issues, remove machine chains/overstructured variants, keep plausible distractors. | Improves candidate-facing readability in system design practice. | Some flags may be acceptable algorithm descriptions; verify manually. | Backlog |
| P2 | Parser hygiene | 12 interview `.md` files | `## Содержание` is missing while question format and `## See also` are present. | Add or regenerate TOC without renumbering questions. | Improves navigation and parser consistency. | Low, but anchors must match generated headings. | Backlog |
| P2 | Caricature distractors | Multiple MCQ JSON files | `--caricature --top 5` shows 280 files with markers and 1672 candidate blocks. | Start with top files and replace caricatures with plausible wrong mental models. | Directly improves Plausibility Parity. | Marker-based audit has false positives; do not auto-fix. | Backlog |

## Iteration log

| Iteration | File/area | Changes | Validation | Next step |
|---|---|---|---|---|
| 1 | `scripts/audit-mcq-parity.py` | Added canonical `RUS_READABILITY_CLICHE`, `RUS_READABILITY_LONG_SENTENCE`, `RUS_READABILITY_TOO_MANY_ARROWS`, `RUS_READABILITY_TOO_MANY_SEMICOLONS`, `RUS_READABILITY_MACHINE_STYLE`, `RUS_READABILITY_OVERSTRUCTURED`, and `RUS_READABILITY_PUNCT`; added missing-options and label/order checks; removed direct `sys.argv` probing; added `--verbose-skip`; fixed `--top` behavior in readability/caricature modes and full-summary counts. | `python3 -m py_compile scripts/audit-mcq-parity.py`; `python3 scripts/audit-mcq-parity.py --selftest` with 10 fixtures; `python3 scripts/audit-mcq-parity.py --top 5`; `python3 scripts/audit-mcq-parity.py --readability --top 5`; `python3 scripts/audit-mcq-parity.py --caricature --top 5`; report generation to `/tmp/mcq-parity-report.{json,md}`. | Manually improve top parity candidate: PostgreSQL MCQ JSON. |
| 2 | `modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json` | Rewrote option `text` for Q1-Q4 so distractors are similar in length, structure, and technical density to the correct option; did not change `q_number`, correctness, or explanation sections. | `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; `python3 scripts/audit-mcq-parity.py -v --top 1 modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; Q1-Q4 no longer appear in parity issues; file flags reduced from 55 to 51. | Continue with PostgreSQL Q5-Q8 as the next small validated batch. |
| 3 | `modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json` | Applied the new mental-model workflow to Q5-Q8; rewrote weak distractors and trimmed over-detailed correct options where correct was visibly guessable by density. | `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; `python3 scripts/audit-mcq-parity.py -v --top 1 modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; Q5-Q8 no longer appear in parity issues; file flags reduced from 51 to 47. | Continue with PostgreSQL Q9-Q12. |
| 4 | `modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json` | Applied the mental-model workflow to Q9-Q12; rewrote option `text` so wrong answers carry plausible single-error reasoning instead of short placeholders. | `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; `python3 scripts/audit-mcq-parity.py -v --top 1 modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; Q9-Q12 no longer appear in parity issues; file flags reduced from 47 to 43. | Continue with PostgreSQL Q13-Q16. |
| 5 | `modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json` | Applied the mental-model workflow to Q13-Q16; balanced index-type answers across `GIN`, `GiST`, `BRIN`, and partial indexes without changing correctness or sections. | `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; `python3 scripts/audit-mcq-parity.py -v --top 1 modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; Q13-Q16 no longer appear in parity issues; file flags reduced from 43 to 39. | Continue with PostgreSQL Q17-Q20. |
| 6 | `modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json` | Applied the mental-model workflow to Q17-Q20; balanced expression, covering, concurrent index, and `EXPLAIN` answers while keeping the original correct labels and sections. | `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; `python3 scripts/audit-mcq-parity.py -v --top 1 modules/quiz-app/src/main/resources/seed/mcq/databases/postgresql-interview.json`; Q17-Q20 no longer appear in parity issues; file flags reduced from 39 to 35. | Continue with PostgreSQL Q21-Q24. |

## Open questions

| Question | Why it matters | Suggested owner |
|---|---|---|
| Should `RUS_READABILITY_TOO_MANY_ARROWS` be tuned stricter after first manual cleanup batch? | Current signal intentionally finds many candidates; after calibration it may need fewer false positives. | Maintainer |
| Should MCQ parity gates be allowed in CI with `--fail-on critical` immediately? | Current corpus has many critical candidates, so CI would fail until a baseline or allowlist exists. | Maintainer |
| Should `.md` TOC generation use `scripts/cheatsheet-regenerate-toc.py` or a dedicated interview-only script? | Interview files have their own stricter `## Q<N>.` structure and should not inherit incompatible generic formatting. | Maintainer |
