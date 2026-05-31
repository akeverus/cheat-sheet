# INTERVIEW-AUDIT-PLAN — аудит и доводка interview-шпаргалок

> Единый план работ по ВСЕМ interview-файлам. Источник истины для итеративной доводки.
> Прогон: `/interview-writer` (проза .md) + `/mcq-quality-fixer` (JSON-сидеры). Коммит после каждой темы, `/compact` после итерации.

**Дата построения:** 2026-05-30  ·  **Файлов:** 305  ·  **Без JSON-сидера:** 125  ·  **С inline MCQ в .md:** 219  ·  **Полностью чистый JSON:** 171

## Критерии проверки (каждый файл должен пройти ВСЕ)

### A. Оформление и структура (.md)
- [ ] **Frontmatter**: `title`, `description`, `tags` (YAML-список), `type`, `difficulty`, `aliases` (≥3), `updated` (актуальная дата).
- [ ] **Содержание (TOC)** сразу после frontmatter, ссылки рабочие, совпадают с `## Q<N>`.
- [ ] **`## See also`** в КОНЦЕ файла, ссылки на смежные темы существуют.
- [ ] **Заголовки вопросов** строго `## Q<N>. ...` (парсер: `## Q\d+`), нумерация без дыр.
- [ ] **Парные code-fence** (чётное число ```), таблицы валидны, mermaid-блоки закрыты.
- [ ] **НЕТ inline `> [!mcq]`** и legacy Tier-1 маркеров (политика v3: MCQ только в JSON).

### B. Человекочитаемость прозы (.md) — ГЛАВНОЕ
- [ ] Только русский в прозе (English — в `backticks`/коде/именах продуктов).
- [ ] Короткие абзацы (2–4 предложения), активный залог, без воды и academic-tone.
- [ ] Списки и сравнительные таблицы вместо «простыней», ключевые числа и единицы.
- [ ] Каждый ответ: суть → механика → пример → **Итог:**. Термины раскрыты при первом употреблении.
- [ ] Связный нарратив: вопросы идут логично, между блоками есть переходы.

### C. MCQ-JSON: наличие + ЧЕЛОВЕКОЧИТАЕМОСТЬ
- [ ] JSON-сидер существует: `seed/mcq/<category>/<topic>-interview.json`, `topic_slug` = имя файла.
- [ ] Проходит `scripts/verify-mcq-json.sh` (schema-valid): ровно 4 опции, 1 correct, секции по схеме.
- [ ] **Покрытие**: каждый `## Q<N>` из .md имеет блок с тем же `q_number`.
- [ ] **Нет legacy-маркеров** (`✓ПРИМЕНЯТЬ/📋ПРАВИЛО/🔗См/❌ПОСЛЕДСТВИЕ`) в секциях.
- [ ] **Нет stub-заглушек** (механические производные вроде «Типичные заблуждения…»).
- [ ] Секции содержательные: `explanation` 3–7 предложений, `example` с production-кейсом и числами,
      distractor = реальный misconception, баланс correct-позиций A≈B≈C≈D (max/min ≤ 2.0).

### D. Синхронизация .md ↔ JSON
- [ ] Номера вопросов совпадают; при правке .md-вопроса обновлён соответствующий блок JSON.
- [ ] Ссылки `related` в JSON ведут на существующие `[[<topic>#Q<N>]]`.

## Легенда статуса темы
- ⬜ не проверено  ·  🔄 в работе  ·  ✅ проверено и доведено  ·  ⚠️ есть замечания (см. колонку)
- Колонка **Оформл./Читаем./MCQ** — ⬜ до ручной проверки итерацией; автоданные в колонках Q/MCQ-inline/JSON.

## Структурная классификация (оформление .md, обновлено 2026-05-31)

Ключевой структурный дефект: во многих файлах число `## Q<N>` НЕ совпадает с числом inline `[!mcq]`,
из-за чего MCQ нельзя привязать к вопросам (`q_number`). Перед миграцией MCQ→JSON это надо чинить.

| Форма | Файлов | Что значит | Стратегия |
|---|---|---|---|
| `NO_INLINE` | 86 | inline MCQ нет (уже мигрированы в JSON или их не было) | проверить наличие/качество JSON-сидера |
| `CLEAN 1:1` | 111 | каждый `## Q<N>` ↔ ровно 1 `[!mcq]`, под Q есть теория | **прямая миграция** MCQ→JSON по q_number, затем strip inline |
| `MULTI` | 59 | есть структура `## Q`, но MCQ/вопрос ≠ 1 | разбор по блокам, маппинг block_idx, затем strip |
| `COLLAPSED` | 49 | `nq≤2`, а `[!mcq]` 15–43 — тело под одним `## Q1` | мигрировать как один Q с N блоками (`blocks[block_idx]`); реструктуризация позже |

*Итого с inline MCQ: 111 + 59 + 49 = 219. Всего форм: 86 + 219 = 305.*

**Порядок итераций:**
1. **CLEAN 1:1 (11)** — самый безопасный батч, делаем первым.
2. **MULTI (31)** — поблочная миграция.
3. **COLLAPSED (41)** — реструктуризация + миграция (самые трудоёмкие).
4. **NO_INLINE без JSON** — генерация MCQ с нуля по теории `.md`.
5. **NO_INLINE с JSON** — аудит качества/человекочитаемости существующих сидеров.

Источник истины inline-MCQ — git HEAD (всё восстановлено, 7216 блоков, потерь нет).

## Колонки таблицы
`Q` — число `## Q` в .md · `inMCQ` — inline `[!mcq]` в .md (должно стать 0) · `JSON` — состояние сидера · `Qj` — вопросов в JSON · далее ручные отметки.


## ai-ml  (25 тем · без JSON: 0)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `agentic-patterns` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `ai-agents` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `ai-application-architecture` | 32 | 0 | ✅ JSON | 32 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `ai-compliance-governance` | 32 | 0 | ✅ JSON | 32 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `ai-observability` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `ai-safety-guardrails` | 32 | 0 | ✅ JSON | 32 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `code-agents` | 31 | 0 | ✅ JSON | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `embeddings` | 29 | 0 | ✅ JSON | 29 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `fine-tuning-llm` | 35 | 0 | ✅ JSON | 35 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `function-calling` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `inference-optimization` | 36 | 0 | ✅ JSON | 36 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `llm-basics` | 30 | 27 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `llm-evaluation` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `llm-integration-patterns` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `long-context-vs-rag` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `mcp` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `mlops` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `model-serving` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 19 | `multi-agent-orchestration` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 20 | `multimodal-ai` | 31 | 0 | ✅ JSON | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 21 | `open-source-llms` | 34 | 0 | ✅ JSON | 34 | ⬜ | ⬜ | ⬜ | ⬜ |
| 22 | `prompt-engineering` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 23 | `rag` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 24 | `reasoning-models` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 25 | `vector-databases` | 28 | 28 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |

## algorithms  (18 тем · без JSON: 5)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `backtracking` | 24 | 0 | ⚠️ cover −1Q | 23 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `divide-and-conquer` | 21 | 0 | ✅ JSON | 21 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `dynamic-programming` | 33 | 0 | ✅ JSON | 33 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `greedy-algorithms` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `recursion` | 27 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `two-pointers-sliding-window` | 1 | 33 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `algorithms` | 16 | 0 | ✅ JSON | 16 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `complexity-analysis` | 31 | 0 | ✅ JSON | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `arrays-strings` | 36 | 0 | ✅ JSON | 36 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `graphs` | 33 | 0 | ⚠️ cover −2Q | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `hash-tables` | 34 | 0 | ⚠️ cover −2Q | 32 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `heaps` | 29 | 0 | ✅ JSON | 29 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `linked-lists` | 32 | 0 | ✅ JSON | 32 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `stacks-queues` | 25 | 0 | ✅ JSON | 25 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `trees` | 34 | 0 | ✅ JSON | 34 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `tries` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `searching-algorithms` | 8 | 31 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `sorting-algorithms` | 1 | 31 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## api  (8 тем · без JSON: 6)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `api-design-best-practices` | 30 | 30 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `api-versioning` | 20 | 0 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `graphql` | 40 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `grpc` | 40 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `http-rest` | 43 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `openapi-swagger` | 33 | 33 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `rest-maturity` | 17 | 0 | ✅ JSON | 17 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `websocket` | 38 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## architecture  (24 тем · без JSON: 0)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `api-gateway` | 38 | 38 | ✅ JSON | 38 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `bff-pattern` | 17 | 17 | ✅ JSON | 17 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `caching-strategies` | 42 | 42 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `cap-theorem` | 41 | 42 | ✅ JSON | 41 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `cdn` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `clean-architecture` | 41 | 41 | ✅ JSON | 41 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `consistency-patterns` | 31 | 42 | ✅ JSON | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `cqrs-event-sourcing` | 41 | 41 | ✅ JSON | 41 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `ddd` | 38 | 38 | ✅ JSON | 38 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `distributed-systems` | 40 | 50 | ✅ JSON | 40 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `dns` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `edge-computing` | 18 | 18 | ✅ JSON | 18 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `event-driven-patterns` | 40 | 40 | ✅ JSON | 40 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `hexagonal-architecture` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `latency-numbers` | 24 | 0 | ✅ JSON | 24 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `load-balancing` | 40 | 40 | ✅ JSON | 40 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `microservices` | 42 | 42 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `networking` | 43 | 41 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 19 | `resilience-patterns` | 43 | 43 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 20 | `reverse-proxy` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 21 | `saga-pattern` | 43 | 43 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 22 | `scalability-patterns` | 41 | 41 | ✅ JSON | 41 | ⬜ | ⬜ | ⬜ | ⬜ |
| 23 | `service-discovery` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 24 | `strangler-fig` | 18 | 18 | ✅ JSON | 18 | ⬜ | ⬜ | ⬜ | ⬜ |

## behavioral  (6 тем · без JSON: 1)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `behavioral` | 38 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `conflict-stories` | 22 | 0 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `culture-fit` | 22 | 0 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `failure-stories` | 22 | 0 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `leadership-stories` | 22 | 0 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `star-method` | 22 | 0 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |

## cicd  (2 тем · без JSON: 2)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `deployment-strategies` | 39 | 39 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `pipeline-design` | 38 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## cloud  (6 тем · без JSON: 6)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `aws` | 16 | 34 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `aws-lambda` | 1 | 32 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `azure` | 25 | 25 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `cloud-native-patterns` | 14 | 30 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `gcp` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `serverless` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## code-quality  (7 тем · без JSON: 7)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `clean-code-practices` | 1 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `code-coverage` | 25 | 25 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `code-review` | 1 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `code-smells` | 1 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `refactoring-patterns` | 1 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `static-analysis` | 6 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `technical-debt` | 1 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## data-engineering  (8 тем · без JSON: 8)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `apache-airflow` | 28 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `apache-flink` | 1 | 31 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `apache-spark` | 1 | 35 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `data-lake-lakehouse` | 28 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `data-warehousing` | 30 | 30 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `dbt` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `kafka-streams` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `stream-processing` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## databases  (20 тем · без JSON: 3)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `cassandra` | 44 | 68 | ✅ JSON | 44 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `clickhouse` | 28 | 38 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `cockroachdb` | 24 | 37 | ✅ JSON | 24 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `database-architecture` | 41 | 41 | ✅ JSON | 41 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `database-replication` | 31 | 0 | ✅ JSON | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `database-sharding` | 34 | 0 | ✅ JSON | 34 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `database-transactions` | 42 | 53 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `dynamodb` | 30 | 30 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `elasticsearch` | 44 | 50 | ✅ JSON | 44 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `flyway-liquibase` | 42 | 52 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `hibernate-caching` | 15 | 21 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `hibernate` | 48 | 49 | ✅ JSON | 48 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `hibernate-jpql-criteria` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `hibernate-relationships` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `mongodb` | 46 | 46 | ✅ JSON | 46 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `neo4j` | 30 | 30 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `postgresql` | 55 | 55 | ✅ JSON | 55 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `redis` | 43 | 53 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 19 | `scylladb` | 23 | 23 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 20 | `sql` | 53 | 55 | ✅ JSON | 53 | ⬜ | ⬜ | ⬜ | ⬜ |

## design-patterns  (1 тем · без JSON: 0)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `design-patterns` | 48 | 56 | ✅ JSON | 48 | ⬜ | ⬜ | ⬜ | ⬜ |

## devops  (13 тем · без JSON: 12)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `ansible` | 25 | 25 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `argocd` | 42 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `consul` | 24 | 24 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `docker` | 41 | 41 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `git` | 43 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `gradle-maven` | 38 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `helm` | 43 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `istio-service-mesh` | 26 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `kubernetes` | 45 | 48 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `linkerd` | 20 | 20 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `linux` | 33 | 33 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `terraform` | 42 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `vault` | 26 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## frameworks  (36 тем · без JSON: 5)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `ktor` | 1 | 32 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `micronaut` | 25 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `quarkus` | 1 | 31 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `vertx` | 1 | 31 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `resilience4j` | 20 | 20 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `spring-ai` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `spring-aop` | 22 | 22 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `spring-async` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `spring-batch` | 43 | 43 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `spring-boot-3-migration` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `spring-boot-actuator` | 43 | 54 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `spring-boot` | 42 | 44 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `spring-cache` | 17 | 18 | ✅ JSON | 17 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `spring-cloud` | 43 | 44 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `spring-data-jdbc` | 16 | 16 | ✅ JSON | 16 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `spring-data-jpa` | 42 | 48 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `spring-events` | 16 | 16 | ✅ JSON | 16 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `spring-framework` | 40 | 49 | ✅ JSON | 40 | ⬜ | ⬜ | ⬜ | ⬜ |
| 19 | `spring-graphql` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 20 | `spring-integration` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 21 | `spring-kafka` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 22 | `spring-messaging` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 23 | `spring-modulith` | 2 | 0 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 24 | `spring-mvc` | 43 | 57 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 25 | `spring-r2dbc` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 26 | `spring-rest-client` | 13 | 14 | ✅ JSON | 13 | ⬜ | ⬜ | ⬜ | ⬜ |
| 27 | `spring-retry` | 17 | 17 | ✅ JSON | 17 | ⬜ | ⬜ | ⬜ | ⬜ |
| 28 | `spring-scheduling` | 16 | 15 | ✅ JSON | 16 | ⬜ | ⬜ | ⬜ | ⬜ |
| 29 | `spring-security` | 43 | 43 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 30 | `spring-session` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 31 | `spring-state-machine` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 32 | `spring-testing` | 1 | 15 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 33 | `spring-transaction` | 15 | 22 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 34 | `spring-validation` | 16 | 15 | ✅ JSON | 16 | ⬜ | ⬜ | ⬜ | ⬜ |
| 35 | `spring-vault` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 36 | `spring-webflux` | 43 | 43 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |

## jvm  (2 тем · без JSON: 2)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `graalvm-native` | 15 | 14 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `jvm` | 40 | 53 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## leadership  (7 тем · без JSON: 2)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `code-review-practices` | 1 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `conflict-resolution` | 20 | 0 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `estimations-planning` | 20 | 0 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `mentoring` | 1 | 0 | ✅ JSON | 25 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `team-leadership` | 1 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `tech-interviewing` | 20 | 0 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `technical-decisions` | 1 | 0 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |

## logging  (1 тем · без JSON: 1)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `logging` | 9 | 41 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## messaging  (7 тем · без JSON: 5)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `aws-sqs-sns` | 1 | 22 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `kafka` | 50 | 50 | ✅ JSON | 50 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `message-brokers-comparison` | 1 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `nats` | 8 | 24 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `pulsar` | 1 | 22 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `rabbitmq` | 41 | 60 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `redpanda` | 20 | 0 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |

## monitoring  (9 тем · без JSON: 9)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `elk-stack` | 5 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `jaeger-zipkin` | 1 | 23 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `logging-strategies` | 1 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `loki-grafana` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `metrics-tracing` | 6 | 41 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `micrometer` | 1 | 20 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `observability` | 15 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `opentelemetry` | 9 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `prometheus-grafana` | 39 | 39 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## performance  (7 тем · без JSON: 7)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `application-profiling` | 42 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `caching-performance` | 1 | 23 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `database-performance` | 1 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `jvm-performance-tuning` | 1 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `memory-management` | 1 | 39 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `network-performance` | 1 | 24 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `performance-testing` | 1 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## programming-languages  (46 тем · без JSON: 36)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `go-concurrency` | 35 | 35 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `go-generics` | 1 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `go` | 1 | 36 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `go-memory-gc` | 1 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `go-modules` | 27 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `go-stdlib` | 30 | 30 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `go-testing` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `java-17-21` | 42 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `java-8` | 41 | 0 | ⚠️ cover −10Q | 31 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `java-annotations` | 43 | 53 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `java-collections` | 46 | 0 | ⚠️ cover −12Q | 34 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `java-completable-future` | 13 | 0 | ✅ JSON | 13 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `java-concurrency` | 56 | 57 | ✅ JSON | 56 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `java-conditional-statements` | 1 | 42 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `java-core` | 39 | 0 | ⚠️ cover −11Q | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `java-exceptions` | 42 | 0 | ⚠️ cover −35Q | 7 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `java-functional-interface` | 14 | 14 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `java-generics` | 40 | 0 | ⚠️ cover −33Q | 7 | ⬜ | ⬜ | ⬜ | ⬜ |
| 19 | `java-initialization` | 1 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 20 | `java-io-nio` | 40 | 51 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 21 | `java-jackson` | 1 | 31 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 22 | `java-lombok` | 5 | 27 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 23 | `java-mapstruct` | 1 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 24 | `java-modules` | 38 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 25 | `java-oop` | 43 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 26 | `java-optional` | 15 | 0 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 27 | `java-pattern-matching` | 15 | 15 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 28 | `java-records` | 15 | 15 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 29 | `java-reflection` | 1 | 16 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 30 | `java-serialization` | 40 | 39 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 31 | `java-stream` | 42 | 0 | ⚠️ cover −36Q | 6 | ⬜ | ⬜ | ⬜ | ⬜ |
| 32 | `java-string` | 39 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 33 | `java-types` | 38 | 37 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 34 | `java-virtual-threads` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 35 | `kotlin-collections` | 42 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 36 | `kotlin-coroutines` | 19 | 39 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 37 | `kotlin-dsl` | 1 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 38 | `kotlin-exceptions` | 7 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 39 | `kotlin-flow` | 17 | 17 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 40 | `kotlin-interop-java` | 38 | 38 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 41 | `kotlin` | 27 | 45 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 42 | `kotlin-sealed-classes` | 15 | 15 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 43 | `kotlin-serialization` | 1 | 43 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 44 | `kotlin-spring` | 15 | 15 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 45 | `kotlin-value-classes` | 15 | 15 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 46 | `scala` | 1 | 40 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## reactive  (6 тем · без JSON: 4)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `project-reactor` | 47 | 47 | ✅ JSON | 47 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `reactive-patterns` | 26 | 26 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `reactive-streams` | 30 | 30 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `reactive-testing` | 28 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `rxjava` | 46 | 46 | ✅ JSON | 46 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `webflux` | 28 | 28 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## security  (10 тем · без JSON: 0)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `application-security` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `authentication-authorization-patterns` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `jwt` | 43 | 43 | ✅ JSON | 43 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `mtls` | 20 | 20 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `oauth2` | 42 | 42 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `owasp-top10` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `secrets-management` | 22 | 22 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `supply-chain-security` | 24 | 24 | ✅ JSON | 24 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `tls-ssl` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `zero-trust` | 19 | 19 | ✅ JSON | 19 | ⬜ | ⬜ | ⬜ | ⬜ |

## system-design  (22 тем · без JSON: 2)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `design-chat-system` | 21 | 21 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `design-dropbox` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `design-ecommerce-delivery` | 36 | 0 | ✅ JSON | 36 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `design-elevator-oo` | 26 | 0 | ✅ JSON | 26 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `design-feed-system` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `design-google-maps` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `design-instagram` | 27 | 0 | ✅ JSON | 27 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `design-key-value-store` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `design-netflix` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `design-parking-lot-oo` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `design-pastebin` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `design-payment-system` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `design-rate-limiter` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `design-search` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 15 | `design-twitter` | 27 | 0 | ✅ JSON | 27 | ⬜ | ⬜ | ⬜ | ⬜ |
| 16 | `design-typeahead` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 17 | `design-uber` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 18 | `design-url-shortener` | 30 | 0 | ✅ JSON | 30 | ⬜ | ⬜ | ⬜ | ⬜ |
| 19 | `design-vending-machine-oo` | 24 | 0 | ✅ JSON | 24 | ⬜ | ⬜ | ⬜ | ⬜ |
| 20 | `design-web-crawler` | 26 | 0 | ✅ JSON | 26 | ⬜ | ⬜ | ⬜ | ⬜ |
| 21 | `design-youtube` | 28 | 0 | ✅ JSON | 28 | ⬜ | ⬜ | ⬜ | ⬜ |
| 22 | `system-design` | 1 | 41 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |

## testing  (14 тем · без JSON: 2)

| # | Тема (.md) | Q | inMCQ | JSON-сидер | Qj | Оформл. | Читаем. | MCQ-кач. | Статус |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `chaos-engineering` | 44 | 43 | ✅ JSON | 44 | ⬜ | ⬜ | ⬜ | ⬜ |
| 2 | `contract-testing` | 42 | 0 | ✅ JSON | 42 | ⬜ | ⬜ | ⬜ | ⬜ |
| 3 | `integration-testing` | 39 | 40 | ✅ JSON | 39 | ⬜ | ⬜ | ⬜ | ⬜ |
| 4 | `junit` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 5 | `load-testing` | 22 | 22 | ✅ JSON | 22 | ⬜ | ⬜ | ⬜ | ⬜ |
| 6 | `mockito` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 7 | `mutation-testing` | 20 | 19 | ✅ JSON | 20 | ⬜ | ⬜ | ⬜ | ⬜ |
| 8 | `property-based-testing` | 1 | 21 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 9 | `rest-assured` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 10 | `selenium` | 15 | 15 | ✅ JSON | 15 | ⬜ | ⬜ | ⬜ | ⬜ |
| 11 | `test-automation` | 7 | 50 | ❌ нет JSON | 0 | ⬜ | ⬜ | ⬜ | ⬜ |
| 12 | `test-strategies` | 45 | 45 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |
| 13 | `testcontainers` | 40 | 39 | ✅ JSON | 40 | ⬜ | ⬜ | ⬜ | ⬜ |
| 14 | `unit-testing` | 45 | 44 | ✅ JSON | 45 | ⬜ | ⬜ | ⬜ | ⬜ |

## Очередь работ (приоритет: сначала темы без JSON, затем с замечаниями)

### P1 — нет JSON-сидера (125)
- [ ] `algorithms/algorithmic-paradigms/greedy-algorithms-interview.md` — Q=1, inline MCQ=28
- [ ] `algorithms/algorithmic-paradigms/recursion-interview.md` — Q=27, inline MCQ=27
- [ ] `algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview.md` — Q=1, inline MCQ=33
- [ ] `algorithms/sorting-searching/searching-algorithms-interview.md` — Q=8, inline MCQ=31
- [ ] `algorithms/sorting-searching/sorting-algorithms-interview.md` — Q=1, inline MCQ=31
- [ ] `api/api-design-best-practices-interview.md` — Q=30, inline MCQ=30
- [ ] `api/graphql-interview.md` — Q=40, inline MCQ=40
- [ ] `api/grpc-interview.md` — Q=40, inline MCQ=40
- [ ] `api/http-rest-interview.md` — Q=43, inline MCQ=43
- [ ] `api/openapi-swagger-interview.md` — Q=33, inline MCQ=33
- [ ] `api/websocket-interview.md` — Q=38, inline MCQ=38
- [ ] `behavioral/behavioral-interview.md` — Q=38, inline MCQ=38
- [ ] `cicd/deployment-strategies-interview.md` — Q=39, inline MCQ=39
- [ ] `cicd/pipeline-design-interview.md` — Q=38, inline MCQ=38
- [ ] `cloud/aws-interview.md` — Q=16, inline MCQ=34
- [ ] `cloud/aws-lambda-interview.md` — Q=1, inline MCQ=32
- [ ] `cloud/azure-interview.md` — Q=25, inline MCQ=25
- [ ] `cloud/cloud-native-patterns-interview.md` — Q=14, inline MCQ=30
- [ ] `cloud/gcp-interview.md` — Q=1, inline MCQ=28
- [ ] `cloud/serverless-interview.md` — Q=1, inline MCQ=28
- [ ] `code-quality/clean-code-practices-interview.md` — Q=1, inline MCQ=27
- [ ] `code-quality/code-coverage-interview.md` — Q=25, inline MCQ=25
- [ ] `code-quality/code-review-interview.md` — Q=1, inline MCQ=40
- [ ] `code-quality/code-smells-interview.md` — Q=1, inline MCQ=27
- [ ] `code-quality/refactoring-patterns-interview.md` — Q=1, inline MCQ=42
- [ ] `code-quality/static-analysis-interview.md` — Q=6, inline MCQ=26
- [ ] `code-quality/technical-debt-interview.md` — Q=1, inline MCQ=40
- [ ] `data-engineering/apache-airflow-interview.md` — Q=28, inline MCQ=28
- [ ] `data-engineering/apache-flink-interview.md` — Q=1, inline MCQ=31
- [ ] `data-engineering/apache-spark-interview.md` — Q=1, inline MCQ=35
- [ ] `data-engineering/data-lake-lakehouse-interview.md` — Q=28, inline MCQ=28
- [ ] `data-engineering/data-warehousing-interview.md` — Q=30, inline MCQ=30
- [ ] `data-engineering/dbt-interview.md` — Q=1, inline MCQ=28
- [ ] `data-engineering/kafka-streams-interview.md` — Q=1, inline MCQ=28
- [ ] `data-engineering/stream-processing-interview.md` — Q=1, inline MCQ=28
- [ ] `databases/flyway-liquibase-interview.md` — Q=42, inline MCQ=52
- [ ] `databases/neo4j-interview.md` — Q=30, inline MCQ=30
- [ ] `databases/scylladb-interview.md` — Q=23, inline MCQ=23
- [ ] `devops/ansible-interview.md` — Q=25, inline MCQ=25
- [ ] `devops/argocd-interview.md` — Q=42, inline MCQ=42
- [ ] `devops/consul-interview.md` — Q=24, inline MCQ=24
- [ ] `devops/docker-interview.md` — Q=41, inline MCQ=41
- [ ] `devops/git-interview.md` — Q=43, inline MCQ=43
- [ ] `devops/gradle-maven-interview.md` — Q=38, inline MCQ=38
- [ ] `devops/helm-interview.md` — Q=43, inline MCQ=43
- [ ] `devops/istio-service-mesh-interview.md` — Q=26, inline MCQ=26
- [ ] `devops/linkerd-interview.md` — Q=20, inline MCQ=20
- [ ] `devops/linux-interview.md` — Q=33, inline MCQ=33
- [ ] `devops/terraform-interview.md` — Q=42, inline MCQ=42
- [ ] `devops/vault-interview.md` — Q=26, inline MCQ=26
- [ ] `frameworks/jvm-alternatives/ktor-interview.md` — Q=1, inline MCQ=32
- [ ] `frameworks/jvm-alternatives/micronaut-interview.md` — Q=25, inline MCQ=26
- [ ] `frameworks/jvm-alternatives/quarkus-interview.md` — Q=1, inline MCQ=31
- [ ] `frameworks/jvm-alternatives/vertx-interview.md` — Q=1, inline MCQ=31
- [ ] `frameworks/spring/spring-testing-interview.md` — Q=1, inline MCQ=15
- [ ] `jvm/graalvm-native-interview.md` — Q=15, inline MCQ=14
- [ ] `jvm/jvm-interview.md` — Q=40, inline MCQ=53
- [ ] `leadership/code-review-practices-interview.md` — Q=1, inline MCQ=40
- [ ] `leadership/team-leadership-interview.md` — Q=1, inline MCQ=40
- [ ] `logging/logging-interview.md` — Q=9, inline MCQ=41
- [ ] `messaging/aws-sqs-sns-interview.md` — Q=1, inline MCQ=22
- [ ] `messaging/message-brokers-comparison-interview.md` — Q=1, inline MCQ=26
- [ ] `messaging/nats-interview.md` — Q=8, inline MCQ=24
- [ ] `messaging/pulsar-interview.md` — Q=1, inline MCQ=22
- [ ] `messaging/rabbitmq-interview.md` — Q=41, inline MCQ=60
- [ ] `monitoring/elk-stack-interview.md` — Q=5, inline MCQ=26
- [ ] `monitoring/jaeger-zipkin-interview.md` — Q=1, inline MCQ=23
- [ ] `monitoring/logging-strategies-interview.md` — Q=1, inline MCQ=38
- [ ] `monitoring/loki-grafana-interview.md` — Q=1, inline MCQ=28
- [ ] `monitoring/metrics-tracing-interview.md` — Q=6, inline MCQ=41
- [ ] `monitoring/micrometer-interview.md` — Q=1, inline MCQ=20
- [ ] `monitoring/observability-interview.md` — Q=15, inline MCQ=40
- [ ] `monitoring/opentelemetry-interview.md` — Q=9, inline MCQ=28
- [ ] `monitoring/prometheus-grafana-interview.md` — Q=39, inline MCQ=39
- [ ] `performance/application-profiling-interview.md` — Q=42, inline MCQ=42
- [ ] `performance/caching-performance-interview.md` — Q=1, inline MCQ=23
- [ ] `performance/database-performance-interview.md` — Q=1, inline MCQ=27
- [ ] `performance/jvm-performance-tuning-interview.md` — Q=1, inline MCQ=38
- [ ] `performance/memory-management-interview.md` — Q=1, inline MCQ=39
- [ ] `performance/network-performance-interview.md` — Q=1, inline MCQ=24
- [ ] `performance/performance-testing-interview.md` — Q=1, inline MCQ=42
- [ ] `programming-languages/go/go-concurrency-interview.md` — Q=35, inline MCQ=35
- [ ] `programming-languages/go/go-generics-interview.md` — Q=1, inline MCQ=26
- [ ] `programming-languages/go/go-interview.md` — Q=1, inline MCQ=36
- [ ] `programming-languages/go/go-memory-gc-interview.md` — Q=1, inline MCQ=27
- [ ] `programming-languages/go/go-modules-interview.md` — Q=27, inline MCQ=27
- [ ] `programming-languages/go/go-stdlib-interview.md` — Q=30, inline MCQ=30
- [ ] `programming-languages/go/go-testing-interview.md` — Q=1, inline MCQ=28
- [ ] `programming-languages/java/java-17-21-interview.md` — Q=42, inline MCQ=42
- [ ] `programming-languages/java/java-annotations-interview.md` — Q=43, inline MCQ=53
- [ ] `programming-languages/java/java-conditional-statements-interview.md` — Q=1, inline MCQ=42
- [ ] `programming-languages/java/java-functional-interface-interview.md` — Q=14, inline MCQ=14
- [ ] `programming-languages/java/java-initialization-interview.md` — Q=1, inline MCQ=27
- [ ] `programming-languages/java/java-io-nio-interview.md` — Q=40, inline MCQ=51
- [ ] `programming-languages/java/java-jackson-interview.md` — Q=1, inline MCQ=31
- [ ] `programming-languages/java/java-lombok-interview.md` — Q=5, inline MCQ=27
- [ ] `programming-languages/java/java-mapstruct-interview.md` — Q=1, inline MCQ=28
- [ ] `programming-languages/java/java-modules-interview.md` — Q=38, inline MCQ=38
- [ ] `programming-languages/java/java-oop-interview.md` — Q=43, inline MCQ=43
- [ ] `programming-languages/java/java-pattern-matching-interview.md` — Q=15, inline MCQ=15
- [ ] `programming-languages/java/java-records-interview.md` — Q=15, inline MCQ=15
- [ ] `programming-languages/java/java-reflection-interview.md` — Q=1, inline MCQ=16
- [ ] `programming-languages/java/java-serialization-interview.md` — Q=40, inline MCQ=39
- [ ] `programming-languages/java/java-string-interview.md` — Q=39, inline MCQ=43
- [ ] `programming-languages/java/java-types-interview.md` — Q=38, inline MCQ=37
- [ ] `programming-languages/kotlin/kotlin-collections-interview.md` — Q=42, inline MCQ=43
- [ ] `programming-languages/kotlin/kotlin-coroutines-interview.md` — Q=19, inline MCQ=39
- [ ] `programming-languages/kotlin/kotlin-dsl-interview.md` — Q=1, inline MCQ=40
- [ ] `programming-languages/kotlin/kotlin-exceptions-interview.md` — Q=7, inline MCQ=40
- [ ] `programming-languages/kotlin/kotlin-flow-interview.md` — Q=17, inline MCQ=17
- [ ] `programming-languages/kotlin/kotlin-interop-java-interview.md` — Q=38, inline MCQ=38
- [ ] `programming-languages/kotlin/kotlin-interview.md` — Q=27, inline MCQ=45
- [ ] `programming-languages/kotlin/kotlin-sealed-classes-interview.md` — Q=15, inline MCQ=15
- [ ] `programming-languages/kotlin/kotlin-serialization-interview.md` — Q=1, inline MCQ=43
- [ ] `programming-languages/kotlin/kotlin-spring-interview.md` — Q=15, inline MCQ=15
- [ ] `programming-languages/kotlin/kotlin-value-classes-interview.md` — Q=15, inline MCQ=15
- [ ] `programming-languages/scala/scala-interview.md` — Q=1, inline MCQ=40
- [ ] `reactive/reactive-patterns-interview.md` — Q=26, inline MCQ=26
- [ ] `reactive/reactive-streams-interview.md` — Q=30, inline MCQ=30
- [ ] `reactive/reactive-testing-interview.md` — Q=28, inline MCQ=28
- [ ] `reactive/webflux-interview.md` — Q=28, inline MCQ=28
- [ ] `system-design/design-chat-system-interview.md` — Q=21, inline MCQ=21
- [ ] `system-design/system-design-interview.md` — Q=1, inline MCQ=41
- [ ] `testing/property-based-testing-interview.md` — Q=1, inline MCQ=21
- [ ] `testing/test-automation-interview.md` — Q=7, inline MCQ=50

### P2 — JSON есть, но с замечаниями (9)
- [ ] `algorithms/algorithmic-paradigms/backtracking-interview.md` — ⚠️ cover −1Q
- [ ] `algorithms/data-structures/graphs-interview.md` — ⚠️ cover −2Q
- [ ] `algorithms/data-structures/hash-tables-interview.md` — ⚠️ cover −2Q
- [ ] `programming-languages/java/java-8-interview.md` — ⚠️ cover −10Q
- [ ] `programming-languages/java/java-collections-interview.md` — ⚠️ cover −12Q
- [ ] `programming-languages/java/java-core-interview.md` — ⚠️ cover −11Q
- [ ] `programming-languages/java/java-exceptions-interview.md` — ⚠️ cover −35Q
- [ ] `programming-languages/java/java-generics-interview.md` — ⚠️ cover −33Q
- [ ] `programming-languages/java/java-stream-interview.md` — ⚠️ cover −36Q
