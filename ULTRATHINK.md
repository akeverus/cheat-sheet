---
title: "ULTRATHINK v3 — пофайловый audit-план (304 файла)"
description: "Полная таблица всех interview-файлов с критериями проверки, статусом каждого. Прогресс сброшен 2026-05-27."
updated: "2026-05-27"
status: "active"
audit_version: 3
---

# ULTRATHINK v3 — пофайловый audit-план

**Прогресс сброшен 2026-05-27.** Цель: довести **все 304 interview-файла** до production-grade качества.

Критерии проверки на каждый файл — см. секцию [Критерии](#критерии-проверки-checklist) ниже.

## Snapshot текущего состояния

| Метрика | Значение |
|---|---|
| Всего .md файлов | **304** |
| ✅ **READY** (все критерии пройдены) | **62** (20%) |
| ❌ Требуют работы | **242** (80%) |
| С парным JSON-сидером | 85 |
| **БЕЗ JSON** | 219 |
| С MCQ-callouts `> [!mcq]` в .md | 220 |
| С Tier 1 эмодзи (❌📋✓🔗) в .md | 195 |
| С `[[wikilink]]` | 145 |
| Q < 5 («заглушки») | ~70 |
| Q 5-19 («тонкие») | ~38 |
| JSON Q count mismatch с MD | 12 |
| See-also < 5 ссылок | 4 |
| Frontmatter incomplete | 0 |

## Критерии проверки (CHECKLIST)

Каждый файл должен пройти **все** критерии. Статус в таблице ниже сводит их в одну колонку.

### A. Frontmatter (6 полей)
- `title`, `description`, `tags` (YAML list), `aliases` (≥ 3), `difficulty`, `updated`.

### B. Структура
- `## Полезные ссылки` → `## Содержание` (TOC) → `## Q1..Q<N>` (no gaps) → `## See also` standalone в конце.
- ≥ 5 markdown-ссылок в See also.

### C. Content quality (человекочитаемость)
- Только русский; англ. термины в `backticks`.
- Короткие абзацы (2-4 предложения).
- Bullet-списки для 3+ пунктов; таблицы для `vs`.
- Код-блоки с language tag; mermaid-диаграммы для архитектуры/flow.
- Конкретные примеры (числа, имена продуктов, post-mortems).
- Без воды и маркетинга.

### D. Чистота markdown
- НЕТ `> [!mcq]` блоков.
- НЕТ Tier 1 эмодзи-маркеров.
- НЕТ `[[wikilinks]]` — только `[text](path.md)`.
- `bash scripts/verify-md-no-mcq.sh` → OK.

### E. Q count
- ≥ 20 Q для базовой темы; ≥ 25 Q для широких; ≥ 30 Q для system-design.
- ≥ 20-30% Q помечены `(!)`.

### F. JSON-сидер (MCQ)
- Файл `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json` существует.
- Q count в JSON == Q count в MD.
- jsonschema draft-07 → 0 errors.
- 30 Q × 4 опции (A/B/C/D), 1 correct + 3 wrong на блок.
- Rotation A/B/C/D ratio ≤ 2.0.

### G. MCQ content quality
- Correct sections (5): `explanation`, `example`, `when_to_apply`, `edge_cases`, `related`.
- Wrong sections (4): `what_actually`, `source_of_confusion`, `if_it_were_true`, `how_it_should_be`.
- Русский; info-equivalent length (ratio ≤ 1.4); правдоподобные distractors.
- `related` plain text refs (БЕЗ `[[wikilink]]`).
- НЕТ `**bold**` в `text`; НЕТ эмодзи (❌📋✓🔗); НЕТ banned-фраз.

### H. Smoke
- `./gradlew :quiz-app:test --tests "*McqJsonLoader*"` → BUILD SUCCESSFUL.

## Условные обозначения в таблице

| Метка | Значение |
|---|---|
| `✅ READY` | Все критерии A-H пройдены |
| `NO-JSON` | Нет парного JSON-сидера |
| `STUB` | Q < 5 (заглушка, нужно расширять) |
| `THIN` | Q ∈ [5, 19] (тонкий, нужно добить до 20-30) |
| `MCQ-DIRTY` | Содержит `> [!mcq]` callouts в .md |
| `TIER1-EMOJI` | Содержит Tier 1 эмодзи ❌📋✓🔗 |
| `WIKILINKS` | Содержит `[[X]]` wikilinks |
| `JSON-MISMATCH` | Q count в JSON ≠ Q count в .md |
| `SEE-ALSO-WEAK` | See-also < 5 ссылок |

Колонки таблицы:
- **Q** — кол-во Q в .md.
- **JSON Q** — кол-во Q в .json (— если файла нет).
- **MD clean** — ✅ если no MCQ и no Tier1; иначе `❌(Nm/Ne)` — N MCQ блоков / N эмодзи-вхождений.
- **WL** — wikilinks (✅ если 0).
- **See-also** — ≥ 5 ссылок.
- **Status** — список нарушенных критериев или `✅ READY`.

## Пофайловая таблица

| # | Category | Slug | Q | JSON Q | MD clean | WL | See-also | Status |
|---|---|---|---|---|---|---|---|---|
| 1 | `ai-ml` | `agentic-patterns` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 2 | `ai-ml` | `ai-agents` | 38 | 38 | ✅ | ✅ | ✅ | ✅ READY |
| 3 | `ai-ml` | `ai-application-architecture` | 32 | 32 | ✅ | ✅ | ✅ | ✅ READY |
| 4 | `ai-ml` | `ai-compliance-governance` | 32 | 32 | ✅ | ✅ | ✅ | ✅ READY |
| 5 | `ai-ml` | `ai-observability` | 28 | 28 | ✅ | ✅ | ✅ | ✅ READY |
| 6 | `ai-ml` | `ai-safety-guardrails` | 32 | 32 | ✅ | ✅ | ✅ | ✅ READY |
| 7 | `ai-ml` | `code-agents` | 31 | 31 | ✅ | ✅ | ✅ | ✅ READY |
| 8 | `ai-ml` | `embeddings` | 39 | 39 | ✅ | ✅ | ✅ | ✅ READY |
| 9 | `ai-ml` | `fine-tuning-llm` | 35 | 35 | ✅ | ✅ | ✅ | ✅ READY |
| 10 | `ai-ml` | `function-calling` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 11 | `ai-ml` | `inference-optimization` | 36 | 36 | ✅ | ✅ | ✅ | ✅ READY |
| 12 | `ai-ml` | `llm-basics` | 30 | 30 | ❌(27m/0e) | ❌(18) | ✅ | MCQ-DIRTY / WIKILINKS |
| 13 | `ai-ml` | `llm-evaluation` | 30 | 30 | ✅ | ❌(1) | ✅ | WIKILINKS |
| 14 | `ai-ml` | `llm-integration-patterns` | 38 | 38 | ✅ | ✅ | ✅ | ✅ READY |
| 15 | `ai-ml` | `long-context-vs-rag` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 16 | `ai-ml` | `mcp` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 17 | `ai-ml` | `mlops` | 38 | 38 | ✅ | ✅ | ✅ | ✅ READY |
| 18 | `ai-ml` | `model-serving` | 38 | 38 | ✅ | ✅ | ✅ | ✅ READY |
| 19 | `ai-ml` | `multi-agent-orchestration` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 20 | `ai-ml` | `multimodal-ai` | 31 | 31 | ✅ | ✅ | ✅ | ✅ READY |
| 21 | `ai-ml` | `open-source-llms` | 34 | 34 | ✅ | ✅ | ✅ | ✅ READY |
| 22 | `ai-ml` | `prompt-engineering` | 38 | 38 | ✅ | ✅ | ✅ | ✅ READY |
| 23 | `ai-ml` | `rag` | 40 | 40 | ✅ | ✅ | ✅ | ✅ READY |
| 24 | `ai-ml` | `reasoning-models` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 25 | `ai-ml` | `vector-databases` | 28 | 28 | ❌(28m/0e) | ❌(80) | ✅ | MCQ-DIRTY / WIKILINKS |
| 26 | `algorithms` | `algorithms` | 16 | 16 | ✅ | ❌(1) | ✅ | THIN / WIKILINKS |
| 27 | `algorithms/algorithmic-paradigms` | `backtracking` | 24 | 23 | ✅ | ❌(2) | ✅ | WIKILINKS / JSON-MISMATCH |
| 28 | `algorithms/algorithmic-paradigms` | `divide-and-conquer` | 21 | 21 | ✅ | ❌(1) | ❌(0) | WIKILINKS / SEE-ALSO-WEAK |
| 29 | `algorithms/algorithmic-paradigms` | `dynamic-programming` | 33 | 33 | ✅ | ❌(1) | ✅ | WIKILINKS |
| 30 | `algorithms/algorithmic-paradigms` | `greedy-algorithms` | 1 | — | ❌(28m/84e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 31 | `algorithms/algorithmic-paradigms` | `recursion` | 27 | — | ❌(27m/162e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 32 | `algorithms/algorithmic-paradigms` | `two-pointers-sliding-window` | 1 | — | ❌(33m/99e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 33 | `algorithms/complexity` | `complexity-analysis` | 31 | 31 | ✅ | ✅ | ✅ | ✅ READY |
| 34 | `algorithms/data-structures` | `arrays-strings` | 36 | 36 | ✅ | ✅ | ✅ | ✅ READY |
| 35 | `algorithms/data-structures` | `graphs` | 33 | 31 | ✅ | ❌(2) | ✅ | WIKILINKS / JSON-MISMATCH |
| 36 | `algorithms/data-structures` | `hash-tables` | 34 | 32 | ✅ | ✅ | ✅ | JSON-MISMATCH |
| 37 | `algorithms/data-structures` | `heaps` | 29 | 29 | ✅ | ✅ | ✅ | ✅ READY |
| 38 | `algorithms/data-structures` | `linked-lists` | 32 | 32 | ✅ | ✅ | ✅ | ✅ READY |
| 39 | `algorithms/data-structures` | `stacks-queues` | 25 | 25 | ✅ | ✅ | ✅ | ✅ READY |
| 40 | `algorithms/data-structures` | `trees` | 34 | 34 | ✅ | ✅ | ✅ | ✅ READY |
| 41 | `algorithms/data-structures` | `tries` | 28 | 28 | ✅ | ✅ | ✅ | ✅ READY |
| 42 | `algorithms/sorting-searching` | `searching-algorithms` | 8 | — | ❌(31m/114e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 43 | `algorithms/sorting-searching` | `sorting-algorithms` | 1 | — | ❌(31m/93e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 44 | `api` | `api-design-best-practices` | 30 | — | ❌(30m/9e) | ❌(25) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 45 | `api` | `api-versioning` | 20 | 20 | ✅ | ❌(1) | ✅ | WIKILINKS |
| 46 | `api` | `graphql` | 40 | — | ❌(40m/9e) | ❌(67) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 47 | `api` | `grpc` | 40 | — | ❌(40m/0e) | ❌(29) | ✅ | NO-JSON / MCQ-DIRTY / WIKILINKS |
| 48 | `api` | `http-rest` | 43 | — | ❌(43m/18e) | ❌(9) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 49 | `api` | `openapi-swagger` | 33 | — | ❌(33m/198e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 50 | `api` | `rest-maturity` | 17 | 17 | ✅ | ✅ | ✅ | THIN |
| 51 | `api` | `websocket` | 38 | — | ❌(38m/228e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 52 | `architecture` | `api-gateway` | 38 | — | ❌(38m/198e) | ❌(16) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 53 | `architecture` | `bff-pattern` | 17 | — | ❌(17m/102e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 54 | `architecture` | `caching-strategies` | 42 | — | ❌(42m/251e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 55 | `architecture` | `cap-theorem` | 41 | — | ❌(42m/252e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 56 | `architecture` | `cdn` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 57 | `architecture` | `clean-architecture` | 41 | — | ❌(41m/0e) | ❌(7) | ✅ | NO-JSON / MCQ-DIRTY / WIKILINKS |
| 58 | `architecture` | `consistency-patterns` | 31 | — | ❌(42m/252e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 59 | `architecture` | `cqrs-event-sourcing` | 41 | — | ❌(41m/69e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 60 | `architecture` | `ddd` | 38 | — | ❌(38m/196e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 61 | `architecture` | `distributed-systems` | 40 | — | ❌(50m/421e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 62 | `architecture` | `dns` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 63 | `architecture` | `edge-computing` | 18 | — | ❌(18m/107e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 64 | `architecture` | `event-driven-patterns` | 40 | — | ❌(40m/240e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 65 | `architecture` | `hexagonal-architecture` | 45 | — | ❌(45m/270e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 66 | `architecture` | `latency-numbers` | 24 | 24 | ✅ | ✅ | ✅ | ✅ READY |
| 67 | `architecture` | `load-balancing` | 40 | — | ❌(40m/0e) | ❌(80) | ✅ | NO-JSON / MCQ-DIRTY / WIKILINKS |
| 68 | `architecture` | `microservices` | 42 | — | ❌(42m/305e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 69 | `architecture` | `networking` | 43 | — | ❌(41m/246e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 70 | `architecture` | `resilience-patterns` | 43 | — | ❌(43m/256e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 71 | `architecture` | `reverse-proxy` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 72 | `architecture` | `saga-pattern` | 43 | — | ❌(43m/168e) | ❌(30) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 73 | `architecture` | `scalability-patterns` | 41 | — | ❌(41m/246e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 74 | `architecture` | `service-discovery` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 75 | `architecture` | `strangler-fig` | 18 | — | ❌(18m/108e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 76 | `behavioral` | `behavioral` | 38 | — | ❌(38m/228e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 77 | `behavioral` | `conflict-stories` | 22 | 22 | ✅ | ✅ | ✅ | ✅ READY |
| 78 | `behavioral` | `culture-fit` | 22 | 22 | ✅ | ✅ | ✅ | ✅ READY |
| 79 | `behavioral` | `failure-stories` | 22 | 22 | ✅ | ✅ | ✅ | ✅ READY |
| 80 | `behavioral` | `leadership-stories` | 22 | 22 | ✅ | ✅ | ✅ | ✅ READY |
| 81 | `behavioral` | `star-method` | 22 | 22 | ✅ | ✅ | ✅ | ✅ READY |
| 82 | `cicd` | `deployment-strategies` | 39 | — | ❌(39m/234e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 83 | `cicd` | `pipeline-design` | 38 | — | ❌(38m/228e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 84 | `cloud` | `aws` | 16 | — | ❌(34m/57e) | ❌(34) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 85 | `cloud` | `aws-lambda` | 1 | — | ❌(32m/96e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 86 | `cloud` | `azure` | 25 | — | ❌(25m/150e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 87 | `cloud` | `cloud-native-patterns` | 14 | — | ❌(30m/129e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 88 | `cloud` | `gcp` | 1 | — | ❌(28m/84e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 89 | `cloud` | `serverless` | 1 | — | ❌(28m/84e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 90 | `code-quality` | `clean-code-practices` | 1 | — | ❌(27m/81e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 91 | `code-quality` | `code-coverage` | 25 | — | ❌(25m/150e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 92 | `code-quality` | `code-review` | 1 | — | ❌(40m/120e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 93 | `code-quality` | `code-smells` | 1 | — | ❌(27m/81e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 94 | `code-quality` | `refactoring-patterns` | 1 | — | ❌(42m/126e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 95 | `code-quality` | `static-analysis` | 6 | — | ❌(26m/93e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 96 | `code-quality` | `technical-debt` | 1 | — | ❌(40m/120e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 97 | `data-engineering` | `apache-airflow` | 28 | — | ❌(28m/120e) | ❌(28) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 98 | `data-engineering` | `apache-flink` | 1 | — | ❌(31m/93e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 99 | `data-engineering` | `apache-spark` | 1 | — | ❌(35m/105e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 100 | `data-engineering` | `data-lake-lakehouse` | 28 | — | ❌(28m/168e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 101 | `data-engineering` | `data-warehousing` | 30 | — | ❌(30m/180e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 102 | `data-engineering` | `dbt` | 1 | — | ❌(28m/84e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 103 | `data-engineering` | `kafka-streams` | 1 | — | ❌(28m/84e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 104 | `data-engineering` | `stream-processing` | 1 | — | ❌(28m/84e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 105 | `databases` | `cassandra` | 44 | — | ❌(68m/408e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 106 | `databases` | `clickhouse` | 28 | — | ❌(38m/228e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 107 | `databases` | `cockroachdb` | 24 | — | ❌(37m/222e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 108 | `databases` | `database-architecture` | 41 | — | ❌(41m/123e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 109 | `databases` | `database-replication` | 31 | 31 | ✅ | ✅ | ✅ | ✅ READY |
| 110 | `databases` | `database-sharding` | 34 | 34 | ✅ | ❌(11) | ❌(0) | WIKILINKS / SEE-ALSO-WEAK |
| 111 | `databases` | `database-transactions` | 42 | — | ❌(53m/318e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 112 | `databases` | `dynamodb` | 30 | — | ❌(30m/90e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 113 | `databases` | `elasticsearch` | 44 | — | ❌(50m/36e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 114 | `databases` | `flyway-liquibase` | 42 | — | ❌(52m/312e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 115 | `databases` | `hibernate-caching` | 15 | — | ❌(21m/125e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 116 | `databases` | `hibernate` | 48 | — | ❌(49m/92e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 117 | `databases` | `hibernate-jpql-criteria` | 15 | — | ❌(15m/45e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 118 | `databases` | `hibernate-relationships` | 15 | — | ❌(15m/45e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 119 | `databases` | `mongodb` | 46 | — | ❌(46m/249e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 120 | `databases` | `neo4j` | 30 | — | ❌(30m/90e) | ❌(3) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 121 | `databases` | `postgresql` | 55 | — | ❌(55m/211e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 122 | `databases` | `redis` | 43 | — | ❌(53m/317e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 123 | `databases` | `scylladb` | 23 | — | ❌(23m/69e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 124 | `databases` | `sql` | 53 | — | ❌(55m/91e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 125 | `design-patterns` | `design-patterns` | 48 | — | ❌(56m/197e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 126 | `devops` | `ansible` | 25 | — | ❌(25m/75e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 127 | `devops` | `argocd` | 42 | — | ❌(42m/126e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 128 | `devops` | `consul` | 24 | — | ❌(24m/72e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 129 | `devops` | `docker` | 41 | — | ❌(41m/280e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 130 | `devops` | `git` | 43 | — | ❌(43m/207e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 131 | `devops` | `gradle-maven` | 38 | — | ❌(38m/114e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 132 | `devops` | `helm` | 43 | — | ❌(43m/129e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 133 | `devops` | `istio-service-mesh` | 26 | — | ❌(26m/78e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 134 | `devops` | `kubernetes` | 45 | — | ❌(48m/183e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 135 | `devops` | `linkerd` | 20 | — | ❌(20m/60e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 136 | `devops` | `linux` | 33 | — | ❌(33m/99e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 137 | `devops` | `terraform` | 42 | — | ❌(42m/126e) | ❌(2) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 138 | `devops` | `vault` | 26 | — | ❌(26m/78e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 139 | `frameworks/jvm-alternatives` | `ktor` | 1 | — | ❌(32m/96e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 140 | `frameworks/jvm-alternatives` | `micronaut` | 25 | — | ❌(26m/42e) | ❌(10) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 141 | `frameworks/jvm-alternatives` | `quarkus` | 1 | — | ❌(31m/93e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 142 | `frameworks/jvm-alternatives` | `vertx` | 1 | — | ❌(31m/93e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 143 | `frameworks/spring` | `resilience4j` | 20 | — | ❌(20m/42e) | ❌(39) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 144 | `frameworks/spring` | `spring-ai` | 15 | — | ❌(15m/90e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 145 | `frameworks/spring` | `spring-aop` | 22 | — | ❌(22m/132e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 146 | `frameworks/spring` | `spring-async` | 15 | — | ❌(15m/0e) | ❌(40) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 147 | `frameworks/spring` | `spring-batch` | 43 | — | ❌(43m/258e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 148 | `frameworks/spring` | `spring-boot-3-migration` | 15 | — | ❌(15m/0e) | ❌(43) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 149 | `frameworks/spring` | `spring-boot-actuator` | 43 | — | ❌(54m/324e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 150 | `frameworks/spring` | `spring-boot` | 42 | — | ❌(44m/264e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 151 | `frameworks/spring` | `spring-cache` | 17 | — | ❌(18m/0e) | ❌(55) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 152 | `frameworks/spring` | `spring-cloud` | 43 | — | ❌(44m/264e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 153 | `frameworks/spring` | `spring-data-jdbc` | 16 | — | ❌(16m/96e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 154 | `frameworks/spring` | `spring-data-jpa` | 42 | — | ❌(48m/288e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 155 | `frameworks/spring` | `spring-events` | 16 | — | ❌(16m/0e) | ❌(51) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 156 | `frameworks/spring` | `spring-framework` | 40 | — | ❌(49m/251e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 157 | `frameworks/spring` | `spring-graphql` | 15 | — | ❌(15m/0e) | ❌(46) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 158 | `frameworks/spring` | `spring-integration` | 15 | — | ❌(15m/0e) | ❌(47) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 159 | `frameworks/spring` | `spring-kafka` | 15 | — | ❌(15m/0e) | ❌(46) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 160 | `frameworks/spring` | `spring-messaging` | 15 | — | ❌(15m/90e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 161 | `frameworks/spring` | `spring-modulith` | 2 | 15 | ✅ | ❌(1) | ✅ | STUB / WIKILINKS / JSON-MISMATCH |
| 162 | `frameworks/spring` | `spring-mvc` | 43 | — | ❌(57m/342e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 163 | `frameworks/spring` | `spring-r2dbc` | 15 | — | ❌(15m/60e) | ❌(16) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 164 | `frameworks/spring` | `spring-rest-client` | 13 | — | ❌(14m/0e) | ❌(30) | ❌(0) | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS / SEE-ALSO-WEAK |
| 165 | `frameworks/spring` | `spring-retry` | 17 | — | ❌(17m/9e) | ❌(36) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 166 | `frameworks/spring` | `spring-scheduling` | 16 | — | ❌(15m/0e) | ❌(17) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 167 | `frameworks/spring` | `spring-security` | 43 | — | ❌(43m/7e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 168 | `frameworks/spring` | `spring-session` | 15 | — | ❌(15m/0e) | ❌(46) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 169 | `frameworks/spring` | `spring-state-machine` | 15 | — | ❌(15m/0e) | ❌(36) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 170 | `frameworks/spring` | `spring-testing` | 1 | — | ❌(15m/0e) | ❌(44) | ✅ | NO-JSON / STUB / MCQ-DIRTY / WIKILINKS |
| 171 | `frameworks/spring` | `spring-transaction` | 15 | — | ❌(22m/132e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 172 | `frameworks/spring` | `spring-validation` | 16 | — | ❌(15m/9e) | ❌(20) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 173 | `frameworks/spring` | `spring-vault` | 15 | — | ❌(15m/0e) | ❌(46) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 174 | `frameworks/spring` | `spring-webflux` | 43 | — | ❌(43m/258e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 175 | `jvm` | `graalvm-native` | 15 | — | ❌(14m/84e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 176 | `jvm` | `jvm` | 40 | — | ❌(53m/318e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 177 | `leadership` | `code-review-practices` | 1 | — | ❌(40m/120e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 178 | `leadership` | `conflict-resolution` | 20 | 20 | ✅ | ✅ | ✅ | ✅ READY |
| 179 | `leadership` | `estimations-planning` | 20 | 20 | ✅ | ✅ | ✅ | ✅ READY |
| 180 | `leadership` | `mentoring` | 1 | 25 | ✅ | ✅ | ✅ | STUB / JSON-MISMATCH |
| 181 | `leadership` | `team-leadership` | 1 | — | ❌(40m/120e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 182 | `leadership` | `teching` | 20 | 20 | ✅ | ✅ | ✅ | ✅ READY |
| 183 | `leadership` | `technical-decisions` | 1 | 22 | ✅ | ✅ | ✅ | STUB / JSON-MISMATCH |
| 184 | `logging` | `logging` | 9 | — | ❌(41m/147e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 185 | `messaging` | `aws-sqs-sns` | 1 | — | ❌(22m/66e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 186 | `messaging` | `kafka` | 50 | — | ❌(50m/300e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 187 | `messaging` | `message-brokers-comparison` | 1 | — | ❌(26m/78e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 188 | `messaging` | `nats` | 8 | — | ❌(24m/93e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 189 | `messaging` | `pulsar` | 1 | — | ❌(22m/66e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 190 | `messaging` | `rabbitmq` | 41 | — | ❌(60m/342e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 191 | `messaging` | `redpanda` | 20 | 20 | ✅ | ✅ | ✅ | ✅ READY |
| 192 | `monitoring` | `elk-stack` | 5 | — | ❌(26m/90e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 193 | `monitoring` | `jaeger-zipkin` | 1 | — | ❌(23m/69e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 194 | `monitoring` | `logging-strategies` | 1 | — | ❌(38m/114e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 195 | `monitoring` | `loki-grafana` | 1 | — | ❌(28m/84e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 196 | `monitoring` | `metrics-tracing` | 6 | — | ❌(41m/138e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 197 | `monitoring` | `micrometer` | 1 | — | ❌(20m/60e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 198 | `monitoring` | `observability` | 15 | — | ❌(40m/150e) | ❌(45) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 199 | `monitoring` | `opentelemetry` | 9 | — | ❌(28m/108e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 200 | `monitoring` | `prometheus-grafana` | 39 | — | ❌(39m/71e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 201 | `performance` | `application-profiling` | 42 | — | ❌(42m/174e) | ❌(40) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 202 | `performance` | `caching-performance` | 1 | — | ❌(23m/69e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 203 | `performance` | `database-performance` | 1 | — | ❌(27m/81e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 204 | `performance` | `jvm-performance-tuning` | 1 | — | ❌(38m/114e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 205 | `performance` | `memory-management` | 1 | — | ❌(39m/117e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 206 | `performance` | `network-performance` | 1 | — | ❌(24m/72e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 207 | `performance` | `performance-testing` | 1 | — | ❌(42m/126e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 208 | `programming-languages/go` | `go-concurrency` | 35 | — | ❌(35m/210e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 209 | `programming-languages/go` | `go-generics` | 1 | — | ❌(26m/78e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 210 | `programming-languages/go` | `go` | 1 | — | ❌(36m/108e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 211 | `programming-languages/go` | `go-memory-gc` | 1 | — | ❌(27m/81e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 212 | `programming-languages/go` | `go-modules` | 27 | — | ❌(27m/162e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 213 | `programming-languages/go` | `go-stdlib` | 30 | — | ❌(30m/180e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 214 | `programming-languages/go` | `go-testing` | 1 | — | ❌(28m/84e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 215 | `programming-languages/java` | `java-17-21` | 42 | — | ❌(42m/228e) | ❌(12) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 216 | `programming-languages/java` | `java-8` | 41 | 31 | ✅ | ❌(1) | ✅ | WIKILINKS / JSON-MISMATCH |
| 217 | `programming-languages/java` | `java-annotations` | 43 | — | ❌(53m/318e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 218 | `programming-languages/java` | `java-collections` | 46 | 34 | ✅ | ✅ | ✅ | JSON-MISMATCH |
| 219 | `programming-languages/java` | `java-completable-future` | 13 | 13 | ✅ | ✅ | ✅ | THIN |
| 220 | `programming-languages/java` | `java-concurrency` | 56 | — | ❌(57m/342e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 221 | `programming-languages/java` | `java-conditional-statements` | 1 | — | ❌(42m/126e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 222 | `programming-languages/java` | `java-core` | 39 | 28 | ✅ | ✅ | ✅ | JSON-MISMATCH |
| 223 | `programming-languages/java` | `java-exceptions` | 42 | 7 | ✅ | ❌(1) | ✅ | WIKILINKS / JSON-MISMATCH |
| 224 | `programming-languages/java` | `java-functional-interface` | 14 | — | ❌(14m/0e) | ❌(39) | ❌(0) | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS / SEE-ALSO-WEAK |
| 225 | `programming-languages/java` | `java-generics` | 40 | 7 | ✅ | ✅ | ✅ | JSON-MISMATCH |
| 226 | `programming-languages/java` | `java-initialization` | 1 | — | ❌(27m/81e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 227 | `programming-languages/java` | `java-io-nio` | 40 | — | ❌(51m/306e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 228 | `programming-languages/java` | `java-jackson` | 1 | — | ❌(31m/92e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 229 | `programming-languages/java` | `java-lombok` | 5 | — | ❌(27m/162e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 230 | `programming-languages/java` | `java-mapstruct` | 1 | — | ❌(28m/83e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 231 | `programming-languages/java` | `java-modules` | 38 | — | ❌(38m/162e) | ❌(46) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 232 | `programming-languages/java` | `java-oop` | 43 | — | ❌(43m/59e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 233 | `programming-languages/java` | `java-optional` | 15 | 15 | ✅ | ✅ | ✅ | THIN |
| 234 | `programming-languages/java` | `java-pattern-matching` | 15 | — | ❌(15m/0e) | ❌(45) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 235 | `programming-languages/java` | `java-records` | 15 | — | ❌(15m/90e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 236 | `programming-languages/java` | `java-reflection` | 1 | — | ❌(16m/48e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 237 | `programming-languages/java` | `java-serialization` | 40 | — | ❌(39m/150e) | ❌(46) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 238 | `programming-languages/java` | `java-stream` | 42 | 6 | ✅ | ❌(7) | ✅ | WIKILINKS / JSON-MISMATCH |
| 239 | `programming-languages/java` | `java-string` | 39 | — | ❌(43m/193e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 240 | `programming-languages/java` | `java-types` | 38 | — | ❌(37m/219e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 241 | `programming-languages/java` | `java-virtual-threads` | 15 | — | ❌(15m/0e) | ❌(52) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 242 | `programming-languages/kotlin` | `kotlin-collections` | 42 | — | ❌(43m/174e) | ❌(52) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 243 | `programming-languages/kotlin` | `kotlin-coroutines` | 19 | — | ❌(39m/171e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 244 | `programming-languages/kotlin` | `kotlin-dsl` | 1 | — | ❌(40m/120e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 245 | `programming-languages/kotlin` | `kotlin-exceptions` | 7 | — | ❌(40m/138e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 246 | `programming-languages/kotlin` | `kotlin-flow` | 17 | — | ❌(17m/72e) | ❌(15) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 247 | `programming-languages/kotlin` | `kotlin-interop-java` | 38 | — | ❌(38m/138e) | ❌(43) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 248 | `programming-languages/kotlin` | `kotlin` | 27 | — | ❌(45m/213e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 249 | `programming-languages/kotlin` | `kotlin-sealed-classes` | 15 | — | ❌(15m/0e) | ❌(46) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 250 | `programming-languages/kotlin` | `kotlin-serialization` | 1 | — | ❌(43m/129e) | ✅ | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI |
| 251 | `programming-languages/kotlin` | `kotlin-spring` | 15 | — | ❌(15m/0e) | ❌(32) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 252 | `programming-languages/kotlin` | `kotlin-value-classes` | 15 | — | ❌(15m/0e) | ❌(46) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 253 | `programming-languages/scala` | `scala` | 1 | — | ❌(40m/120e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 254 | `reactive` | `project-reactor` | 47 | — | ❌(47m/216e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 255 | `reactive` | `reactive-patterns` | 26 | — | ❌(26m/78e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 256 | `reactive` | `reactive-streams` | 30 | — | ❌(30m/90e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 257 | `reactive` | `reactive-testing` | 28 | — | ❌(28m/84e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 258 | `reactive` | `rxjava` | 46 | — | ❌(46m/156e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 259 | `reactive` | `webflux` | 28 | — | ❌(28m/84e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 260 | `security` | `application-security` | 45 | — | ❌(45m/180e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 261 | `security` | `authentication-authorization-patterns` | 45 | — | ❌(45m/223e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 262 | `security` | `jwt` | 43 | — | ❌(43m/66e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 263 | `security` | `mtls` | 20 | — | ❌(20m/60e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 264 | `security` | `oauth2` | 42 | — | ❌(42m/60e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 265 | `security` | `owasp-top10` | 45 | — | ❌(45m/150e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 266 | `security` | `secrets-management` | 22 | — | ❌(22m/66e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 267 | `security` | `supply-chain-security` | 24 | — | ❌(24m/72e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 268 | `security` | `tls-ssl` | 45 | — | ❌(45m/151e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 269 | `security` | `zero-trust` | 19 | — | ❌(19m/57e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 270 | `system-design` | `design-chat-system` | 21 | — | ❌(21m/36e) | ❌(47) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 271 | `system-design` | `design-dropbox` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 272 | `system-design` | `design-elevator-oo` | 26 | 26 | ✅ | ✅ | ✅ | ✅ READY |
| 273 | `system-design` | `design-feed-system` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 274 | `system-design` | `design-google-maps` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 275 | `system-design` | `design-instagram` | 27 | 27 | ✅ | ✅ | ✅ | ✅ READY |
| 276 | `system-design` | `design-key-value-store` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 277 | `system-design` | `design-netflix` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 278 | `system-design` | `design-parking-lot-oo` | 28 | 28 | ✅ | ✅ | ✅ | ✅ READY |
| 279 | `system-design` | `design-pastebin` | 30 | — | ✅ | ✅ | ✅ | NO-JSON |
| 280 | `system-design` | `design-payment-system` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 281 | `system-design` | `design-rate-limiter` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 282 | `system-design` | `design-search` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 283 | `system-design` | `design-twitter` | 27 | 27 | ✅ | ✅ | ✅ | ✅ READY |
| 284 | `system-design` | `design-typeahead` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 285 | `system-design` | `design-uber` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 286 | `system-design` | `design-url-shortener` | 30 | 30 | ✅ | ✅ | ✅ | ✅ READY |
| 287 | `system-design` | `design-vending-machine-oo` | 24 | 24 | ✅ | ✅ | ✅ | ✅ READY |
| 288 | `system-design` | `design-web-crawler` | 26 | 26 | ✅ | ✅ | ✅ | ✅ READY |
| 289 | `system-design` | `design-youtube` | 28 | 28 | ✅ | ✅ | ✅ | ✅ READY |
| 290 | `system-design` | `system-design` | 1 | — | ❌(41m/123e) | ❌(1) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 291 | `testing` | `chaos-engineering` | 44 | — | ❌(43m/252e) | ❌(5) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 292 | `testing` | `contract-testing` | 20 | — | ❌(42m/183e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 293 | `testing` | `integration-testing` | 39 | — | ❌(40m/240e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 294 | `testing` | `junit` | 15 | — | ❌(15m/90e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 295 | `testing` | `load-testing` | 22 | — | ❌(22m/132e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 296 | `testing` | `mockito` | 45 | — | ❌(45m/270e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 297 | `testing` | `mutation-testing` | 20 | — | ❌(19m/114e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 298 | `testing` | `property-based-testing` | 1 | — | ❌(21m/63e) | ❌(2) | ✅ | NO-JSON / STUB / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 299 | `testing` | `rest-assured` | 15 | — | ❌(15m/90e) | ❌(1) | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 300 | `testing` | `selenium` | 15 | — | ❌(15m/0e) | ❌(36) | ✅ | NO-JSON / THIN / MCQ-DIRTY / WIKILINKS |
| 301 | `testing` | `test-automation` | 7 | — | ❌(50m/168e) | ✅ | ✅ | NO-JSON / THIN / MCQ-DIRTY / TIER1-EMOJI |
| 302 | `testing` | `test-strategies` | 45 | — | ❌(45m/270e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |
| 303 | `testing` | `testcontainers` | 40 | — | ❌(39m/234e) | ❌(1) | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI / WIKILINKS |
| 304 | `testing` | `unit-testing` | 45 | — | ❌(44m/264e) | ✅ | ✅ | NO-JSON / MCQ-DIRTY / TIER1-EMOJI |


**Summary:** 62/304 файлов готовы; 242 требуют работы.

---

## Фазы работы (приоритет по impact)

### Фаза 1 — markdown cleanup массовый (220 файлов, batch)
**Действие:** Python-скрипт по всем «грязным» .md:
1. Strip `> [!mcq]` callout-блоков.
2. Strip Tier 1 эмодзи-маркеров (❌📋✓🔗).
3. Replace wikilinks → markdown с relative path.
4. Run `verify-md-no-mcq.sh` → OK.
5. Batch commit по категориям (10-20 файлов на коммит).

### Фаза 2 — заглушки Q < 5 (~70 файлов, индивидуально)
**Действие на каждый:** `/interview-writer` → 25-30 Q + парный JSON через `/mcq-quality-fixer`.

### Фаза 3 — тонкие Q 5..19 (~38 файлов)
**Действие:** Достроить теорию до 25-30 Q; добавить новые Q в JSON.

### Фаза 4 — JSON Q mismatch (12 файлов)
**Действие:** `/mcq-quality-fixer` дополняет JSON недостающими Q.

### Фаза 5 — JSON content audit (85 файлов)
**Действие:** Скрипт проверяет JSON на: banned-фразы, эмодзи в sections, `**bold**` в text, length ratio, rotation balance.

### Фаза 6 — генерация JSON для готовых MD без JSON (~30 файлов)
Файлы которые после Фазы 1 имеют чистый MD, но не имеют JSON.
**Действие:** `/mcq-quality-fixer` создаёт JSON 25-30 Q.

## Done (текущая сессия 2026-05-26 → 2026-05-27)

10 закрытых полных итераций (30 Q + 30 MCQ JSON + BUILD SUCCESSFUL):

1. ✅ `design-feed-system` — fan-out, celebrity, hybrid.
2. ✅ `design-payment-system` — idempotency, ledger, saga, 3DS, PCI.
3. ✅ `design-rate-limiter` — token bucket, sliding window, Redis Lua.
4. ✅ `design-url-shortener` (17→30) — Base62, CAS dedup, multi-region.
5. ✅ `design-typeahead` (25→30) — trie + ML, multilanguage.
6. ✅ `design-search` (20→30) — BM25, hybrid + RRF, geo, multi-tenancy.
7. ✅ `design-netflix` (new) — Open Connect, ABR, encoding, chaos, DRM.
8. ✅ `design-dropbox` (new) — chunking, CAS, rsync delta, Magic Pocket.
9. ✅ `design-google-maps` (new) — S2/H3, tile pyramid, CH/CRP, ETA.
10. ⚠️ `design-pastebin` (new) — MD готов 30 Q, **JSON pending** (subagent failed 3×).

11 логических коммитов в master. BUILD SUCCESSFUL на всех.

## Note о `/compact`

`/compact` — это CLI slash-command (pure UI). Не доступен через Skill/Tool API. После каждой итерации делается git commit — история сохраняется в git, не теряется при context compaction.

## Tooling

### Mass MCQ-callout strip
```python
import re
from pathlib import Path
for md in Path('cheatsheets/interview').rglob('*-interview.md'):
    text = md.read_text()
    lines = text.split('\n')
    out = []
    i = 0
    while i < len(lines):
        if lines[i].strip().startswith('> [!mcq]'):
            i += 1
            while i < len(lines) and lines[i].startswith('>'):
                i += 1
            continue
        out.append(lines[i])
        i += 1
    text = '\n'.join(out)
    text = re.sub(r'\n{3,}', '\n\n', text)
    md.write_text(text)
```

### Wikilinks → markdown
```python
import re
from pathlib import Path
for md in Path('cheatsheets/interview').rglob('*-interview.md'):
    text = md.read_text()
    text = re.sub(r'\[\[([^|\]]+)\|([^\]]+)\]\]', r'\2', text)
    text = re.sub(r'\[\[([^\]]+)\]\]', r'\1', text)
    md.write_text(text)
```

### Re-audit
Полный re-audit запускается через Python-скрипт (см. /tmp/audit.txt в проекте). Перезапустить при любом значительном изменении статуса.

## Текущий focus

**Immediate:** добить `design-pastebin` JSON (Фаза 0 — закрыть pending iteration).
**Next:** Фаза 1 — batch cleanup markdown 220 файлов.
**Затем:** Фаза 2 — заглушки Q < 5.
