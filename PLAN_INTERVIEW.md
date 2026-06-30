# PLAN_INTERVIEW — качество MCQ JSON-сидеров (Option Parity / Structural Parity)

Бесконечный `/loop 15m` цикл (cron `ae1e19ff`). Цель: правильный вариант в MCQ
**нельзя угадать по форме** — длине, структуре, насыщенности, стилю. Лечим не
сокращением correct, а **поднятием distractor-ов** до его уровня (плюс перенос
лишних деталей correct в `sections`, которые рендерятся после ответа).

> ## 🔄 RESET — 2026-07-01 (выполнен один раз)
>
> Добавлена вторая линза качества — **Plausibility Parity**: дистрактор должен быть не только
> сопоставим по длине/структуре, но и **правдоподобен** (реалистичная ошибочная модель middle/senior,
> а не раздутая карикатура с токсичными/категоричными маркерами — анти-паттерн **Inflated Caricature
> Distractor**). Бар расширился → прогресс round 2 (Option Parity) в таблице §7 **сброшен**: все
> статусы → `QUEUED`, файлы переоткрыты под объединённый критерий. Коммиты round 2 остаются в
> git-истории (не теряются). **Это единственный сброс**; повторно не сбрасывать (guard: `plan_reset`
> в `ledger.json`).

## 1. Правило (закреплено в skills)

| Skill | Где | Что закреплено |
|---|---|---|
| `mcq-quality-fixer` (канон) | `### Option Parity (MANDATORY — the strongest rule)` | Бар по длине/форме/claim-count/structure-mirror; «raise distractors, don't shrink correct»; **Structural-signature tell** (correct не единственный с `→`/`:`/`;`/backtick-перечислением); **Cross-block consistency** (correct не систематически длиннейший — цель ~25%, не ~90%) |
| `interview-writer` | секция MCQ-quality-rules (~line 314) | «Option Parity (обязательно)» + ссылка на канон при MCQ-sync |
| `interview-options-writer` (deprecated) | `### Length-ratio cap` (~line 631) | Синхронизировано для legacy-аудита; для нового контента НЕ использовать |

## 2. Признаки «угадываемого» correct (что ищем)

- `LEN_AVG` — len(correct) / avg(len(wrong)) > 1.3;
- `LEN_SPREAD` — max(len опций) / min(len опций) > 1.5;
- `UNIQ_MARKER` — correct **единственный** несёт `→` / «X: a; b» / `;` / ≥2 `backtick` / шаг-список;
- `COMMA_GAP` — у correct заметно больше запятых/смысловых частей;
- `SHORT_DISTR` — есть wrong короче 60% длины correct (заглушка);
- `CORRECT_LONGEST_RATE` (file-level) — доля блоков, где correct самый длинный; >50% = файл угадывается «самый длинный = правильный».

**Plausibility-линза (round 3, 2026-07-01):**
- `CARICATURE` — дистрактор несёт маркеры токсичности/абсурда/категоричности (группы: absolute / toxic_management / absurd_action / dismissive / fake_reasoning) — отбрасывается по тону, а не по знанию;
- `INFLATED_CARICATURE` — дистрактор раздут до длины/формы correct, но всё равно карикатурен (главный новый кейс);
- технич. плотность: `TECH_DENSITY_GAP` / `BACKTICK_GAP` / `NUMBER_GAP` / `STRUCTURE_DENSITY_GAP` / `WORD_COUNT_GAP` / `SENTENCE_COUNT_GAP` — correct заметно «технически насыщеннее»;
- структурная симметрия: `ONLY_ONE_SEQUENCE_OPTION` / `ONLY_ONE_ALGORITHM_OPTION` / `ONLY_ONE_ENUMERATION_OPTION` / `ONLY_ONE_CAUSAL_OPTION` — форму несёт ровно один вариант;
- severity блока: `LOW` / `MEDIUM` / `HIGH` / `CRITICAL` (`scripts/audit-mcq-parity.py`, `--json-report` / `--markdown-report` / `--fail-on`).

## 3. Как чинить (направление важно)

НЕ сокращать correct до уровня заглушек. **Поднимать distractor-ы**: каждый wrong —
правдоподобная ОШИБОЧНАЯ модель той же формы (своя неверная `→`-цепочка / шаг-список /
сравнение ролей), не заглушка. Лишние детали correct → в его `sections`
(`explanation`/`example`/`edge_cases` — рендер после ответа). Ровно 1 `correct: true`,
факты 1:1, дистрактор остаётся ложным (сверять с `.md`).

Пример для встречи-структуры (как mentoring Q2):
| Опция | Модель |
|---|---|
| A | одна структура встречи, но НЕВЕРНАЯ роль владельца повестки |
| B | другая структура, но неверная частота и цель |
| C | верная частота, но неверное распределение блоков |
| D | правильная структура (correct) |

## 4. Тулчейн

| Инструмент | Назначение |
|---|---|
| `scripts/audit-mcq-parity.py` | Аудит-скрипт: находит кандидатов (per-block + file-level). `--top N`, `-v`. Решение за человеком |
| `scratchpad/goal_loop/diagnose.py` | Строгий per-block детектор (LENGTH/SURFACE/THIN/DUP) → tells=0 |
| `scratchpad/goal_loop/gate_struct.py` | HEAD-vs-current: q_number/question_text/order/label/correct неизменны |
| `scratchpad/goal_loop/apply_patch.py` | Безопасный ассемблер (меняет ТОЛЬКО text/sections) |
| `scratchpad/goal_loop/wf_mcq_chunked.js` + `harvest_chunked.py` + `gate_commit.py` | Chunked-воркфлоу + сбор вердиктов + гейт коммита |
| `scripts/verify-mcq-json.sh` | Schema-валидатор |

## 5. Инварианты (НЕЛЬЗЯ нарушать)

1. Правим ТОЛЬКО `seed/mcq/**/*.json` (НЕ `.md` — pre-commit банит MCQ-маркеры).
2. Никогда не менять `correct`/`label`/`order`/`q_number`/`question_text` — только `text`/`sections`.
3. Поле `correct` (НЕ `is_correct`). JSON: `ensure_ascii=False, indent=2`, без trailing-newline.
4. Ровно 1 `correct: true` на блок. Факты 1:1 (перенос в sections, не потеря).
5. Explicit pathspec при коммите. ≤3 параллельных субагента (≥10 → rate-limit).
6. Не трогать файлы параллельной сессии: `agentic-patterns`, `git`, `chaos-engineering`.

## 6. Рецепт итерации

1. `audit-mcq-parity.py --top N` → worst-first кандидаты.
2. editor-агент на файл (раскрывает distractor-ы, убирает stubs, даёт им свою ложную структуру; лишнее correct → sections).
3. Гейты: `diagnose` (tells=0) → `gate_struct` (PASS) → `verify-mcq-json` (OK) → cross-section dup (0) → `audit-mcq-parity` (UNIQ/SHORT/LEN сняты, longest≤~45%).
4. Адверс-ревьюер (fidelity_ok + parity_ok).
5. Атомарный per-file коммит: `pedago(mcq): <topic> — single-delta паритет опций (N блоков)`.

## 7. Реестр всех сидеров (re-аудит под СБРОС, worst-first по severity)

Всего сидеров: **318**. Источник: `scripts/audit-mcq-parity.py` (re-аудит 2026-07-01, round 3).
Severity = ParityФлаги + 2×Caric-блоки + (⚠CORRECT_LONGEST_RATE ? 5 : 0) — карикатуры весомее (новый акцент раунда).
Колонки: **Блоков** — блоков в файле; **corr-long%** — доля блоков, где correct самый длинный (⚠ если >50%);
**ParityФл** — блоков с length/structure-tell; **Caric-бл** — блоков с маркерами Inflated Caricature Distractor;
**Статус** все сброшены в `QUEUED` (см. RESET-баннер вверху). Раунд: `—` ещё не брали в round 3, `R3✅` сделан.

<!-- Сводка re-аудита 2026-07-01: 318 файлов; ParityФлагов(блоков)=8980; Caricature-блоков=2204 в 308 файлах; ⚠CORRECT_LONGEST_RATE=293 файла. -->

| # | Кат. | Сидер | Блоков | corr-long% | ParityФл | Caric-бл | Σsev | Статус | Раунд |
|---|---|---|---:|---:|---:|---:|---:|---|---|
| 1 | system-design | system-design-interview | 41 | 100%⚠ | 41 | 24 | 94 | QUEUED | — |
| 2 | algorithms | complexity-analysis-interview | 31 | 100%⚠ | 31 | 27 | 90 | QUEUED | — |
| 3 | databases | mongodb-interview | 46 | 100%⚠ | 46 | 17 | 85 | QUEUED | — |
| 4 | programming-languages | java-8-interview | 42 | 98%⚠ | 42 | 17 | 81 | QUEUED | — |
| 5 | architecture | cqrs-event-sourcing-interview | 41 | 100%⚠ | 41 | 17 | 80 | QUEUED | — |
| 6 | architecture | scalability-patterns-interview | 41 | 100%⚠ | 41 | 17 | 80 | QUEUED | — |
| 7 | architecture | caching-strategies-interview | 42 | 100%⚠ | 42 | 16 | 79 | QUEUED | — |
| 8 | architecture | cap-theorem-interview | 42 | 100%⚠ | 42 | 16 | 79 | QUEUED | — |
| 9 | architecture | consistency-patterns-interview | 42 | 100%⚠ | 42 | 16 | 79 | QUEUED | — |
| 10 | cicd | pipeline-design-interview | 38 | 100%⚠ | 38 | 18 | 79 | QUEUED | — |
| 11 | programming-languages | java-collections-interview | 46 | 100%⚠ | 46 | 14 | 79 | QUEUED | — |
| 12 | performance | jvm-performance-tuning-interview | 38 | 100%⚠ | 38 | 17 | 77 | QUEUED | — |
| 13 | programming-languages | java-stream-interview | 42 | 100%⚠ | 42 | 15 | 77 | QUEUED | — |
| 14 | databases | database-architecture-interview | 41 | 100%⚠ | 41 | 15 | 76 | QUEUED | — |
| 15 | security | jwt-interview | 43 | 100%⚠ | 43 | 14 | 76 | QUEUED | — |
| 16 | programming-languages | kotlin-collections-interview | 43 | 100%⚠ | 43 | 13 | 74 | QUEUED | — |
| 17 | programming-languages | kotlin-coroutines-interview | 39 | 100%⚠ | 39 | 15 | 74 | QUEUED | — |
| 18 | design-patterns | design-patterns-interview | 48 | 100%⚠ | 44 | 12 | 73 | QUEUED | — |
| 19 | architecture | microservices-interview | 42 | 100%⚠ | 42 | 12 | 71 | QUEUED | — |
| 20 | frameworks | spring-data-jpa-interview | 43 | 98%⚠ | 42 | 12 | 71 | QUEUED | — |
| 21 | leadership | code-review-practices-interview | 40 | 100%⚠ | 40 | 13 | 71 | QUEUED | — |
| 22 | architecture | networking-interview | 43 | 100%⚠ | 43 | 11 | 70 | QUEUED | — |
| 23 | databases | postgresql-interview | 55 | 100%⚠ | 55 | 5 | 70 | QUEUED | — |
| 24 | performance | database-performance-interview | 27 | 100%⚠ | 27 | 19 | 70 | QUEUED | — |
| 25 | security | authentication-authorization-patterns-interview | 45 | 100%⚠ | 45 | 10 | 70 | QUEUED | — |
| 26 | architecture | distributed-systems-interview | 40 | 100%⚠ | 40 | 12 | 69 | QUEUED | — |
| 27 | code-quality | technical-debt-interview | 40 | 100%⚠ | 40 | 12 | 69 | QUEUED | — |
| 28 | databases | sql-interview | 53 | 91%⚠ | 38 | 13 | 69 | QUEUED | — |
| 29 | devops | argocd-interview | 42 | 100%⚠ | 42 | 11 | 69 | QUEUED | — |
| 30 | programming-languages | java-concurrency-interview | 56 | 89%⚠ | 46 | 9 | 69 | QUEUED | — |
| 31 | reactive | rxjava-interview | 46 | 100%⚠ | 46 | 9 | 69 | QUEUED | — |
| 32 | data-engineering | apache-spark-interview | 35 | 100%⚠ | 35 | 14 | 68 | QUEUED | — |
| 33 | frameworks | spring-batch-interview | 43 | 100%⚠ | 43 | 10 | 68 | QUEUED | — |
| 34 | security | tls-ssl-interview | 45 | 100%⚠ | 45 | 9 | 68 | QUEUED | — |
| 35 | testing | test-automation-interview | 50 | 80%⚠ | 45 | 9 | 68 | QUEUED | — |
| 36 | architecture | load-balancing-interview | 40 | 100%⚠ | 40 | 11 | 67 | QUEUED | — |
| 37 | performance | application-profiling-interview | 42 | 100%⚠ | 42 | 10 | 67 | QUEUED | — |
| 38 | programming-languages | kotlin-exceptions-interview | 40 | 100%⚠ | 40 | 11 | 67 | QUEUED | — |
| 39 | testing | integration-testing-interview | 40 | 100%⚠ | 40 | 11 | 67 | QUEUED | — |
| 40 | testing | testcontainers-interview | 40 | 100%⚠ | 40 | 11 | 67 | QUEUED | — |
| 41 | api | graphql-interview | 40 | 95%⚠ | 37 | 12 | 66 | QUEUED | — |
| 42 | performance | memory-management-interview | 39 | 100%⚠ | 39 | 11 | 66 | QUEUED | — |
| 43 | programming-languages | kotlin-interview | 45 | 100%⚠ | 45 | 8 | 66 | QUEUED | — |
| 44 | programming-languages | kotlin-serialization-interview | 43 | 100%⚠ | 43 | 9 | 66 | QUEUED | — |
| 45 | testing | mockito-interview | 45 | 89%⚠ | 45 | 8 | 66 | QUEUED | — |
| 46 | algorithms | trees-interview | 34 | 100%⚠ | 34 | 13 | 65 | QUEUED | — |
| 47 | databases | cassandra-interview | 44 | 100%⚠ | 44 | 8 | 65 | QUEUED | — |
| 48 | databases | elasticsearch-interview | 44 | 100%⚠ | 44 | 8 | 65 | QUEUED | — |
| 49 | algorithms | dynamic-programming-interview | 33 | 100%⚠ | 33 | 13 | 64 | QUEUED | — |
| 50 | algorithms | heaps-interview | 29 | 100%⚠ | 29 | 15 | 64 | QUEUED | — |
| 51 | databases | database-replication-interview | 31 | 100%⚠ | 31 | 14 | 64 | QUEUED | — |
| 52 | devops | git-interview | 43 | 91%⚠ | 37 | 11 | 64 | QUEUED | — |
| 53 | devops | helm-interview | 43 | 100%⚠ | 43 | 8 | 64 | QUEUED | — |
| 54 | frameworks | spring-mvc-interview | 43 | 100%⚠ | 43 | 8 | 64 | QUEUED | — |
| 55 | programming-languages | java-oop-interview | 43 | 100%⚠ | 43 | 8 | 64 | QUEUED | — |
| 56 | security | owasp-top10-interview | 45 | 100%⚠ | 45 | 7 | 64 | QUEUED | — |
| 57 | monitoring | observability-interview | 42 | 95%⚠ | 40 | 9 | 63 | QUEUED | — |
| 58 | programming-languages | java-exceptions-interview | 42 | 98%⚠ | 42 | 8 | 63 | QUEUED | — |
| 59 | programming-languages | java-types-interview | 38 | 100%⚠ | 38 | 10 | 63 | QUEUED | — |
| 60 | ai-ml | embeddings-interview | 29 | 100%⚠ | 29 | 14 | 62 | QUEUED | — |
| 61 | ai-ml | fine-tuning-llm-interview | 35 | 100%⚠ | 35 | 11 | 62 | QUEUED | — |
| 62 | algorithms | sorting-algorithms-interview | 31 | 100%⚠ | 31 | 13 | 62 | QUEUED | — |
| 63 | architecture | saga-pattern-interview | 43 | 100%⚠ | 43 | 7 | 62 | QUEUED | — |
| 64 | frameworks | spring-boot-actuator-interview | 43 | 100%⚠ | 43 | 7 | 62 | QUEUED | — |
| 65 | frameworks | spring-security-interview | 46 | 96%⚠ | 43 | 7 | 62 | QUEUED | — |
| 66 | monitoring | metrics-tracing-interview | 41 | 100%⚠ | 41 | 8 | 62 | QUEUED | — |
| 67 | reactive | project-reactor-interview | 48 | 98%⚠ | 47 | 5 | 62 | QUEUED | — |
| 68 | security | application-security-interview | 45 | 100%⚠ | 45 | 6 | 62 | QUEUED | — |
| 69 | testing | test-strategies-interview | 45 | 100%⚠ | 45 | 6 | 62 | QUEUED | — |
| 70 | ai-ml | ai-application-architecture-interview | 32 | 100%⚠ | 32 | 12 | 61 | QUEUED | — |
| 71 | ai-ml | reasoning-models-interview | 30 | 100%⚠ | 30 | 13 | 61 | QUEUED | — |
| 72 | algorithms | arrays-strings-interview | 36 | 100%⚠ | 36 | 10 | 61 | QUEUED | — |
| 73 | algorithms | hash-tables-interview | 34 | 100%⚠ | 34 | 11 | 61 | QUEUED | — |
| 74 | databases | database-sharding-interview | 34 | 100%⚠ | 34 | 11 | 61 | QUEUED | — |
| 75 | devops | terraform-interview | 42 | 100%⚠ | 42 | 7 | 61 | QUEUED | — |
| 76 | programming-languages | java-generics-interview | 40 | 100%⚠ | 40 | 8 | 61 | QUEUED | — |
| 77 | programming-languages | java-io-nio-interview | 40 | 100%⚠ | 40 | 8 | 61 | QUEUED | — |
| 78 | programming-languages | scala-interview | 40 | 100%⚠ | 40 | 8 | 61 | QUEUED | — |
| 79 | algorithms | divide-and-conquer-interview | 27 | 100%⚠ | 27 | 14 | 60 | QUEUED | — |
| 80 | algorithms | graphs-interview | 34 | 74%⚠ | 29 | 13 | 60 | QUEUED | — |
| 81 | api | http-rest-interview | 43 | 95%⚠ | 37 | 9 | 60 | QUEUED | — |
| 82 | cicd | deployment-strategies-interview | 39 | 100%⚠ | 39 | 8 | 60 | QUEUED | — |
| 83 | databases | hibernate-interview | 50 | 92%⚠ | 43 | 6 | 60 | QUEUED | — |
| 84 | databases | redis-interview | 43 | 100%⚠ | 43 | 6 | 60 | QUEUED | — |
| 85 | testing | unit-testing-interview | 45 | 100%⚠ | 45 | 5 | 60 | QUEUED | — |
| 86 | architecture | api-gateway-interview | 38 | 100%⚠ | 38 | 8 | 59 | QUEUED | — |
| 87 | databases | flyway-liquibase-interview | 42 | 100%⚠ | 42 | 6 | 59 | QUEUED | — |
| 88 | performance | performance-testing-interview | 42 | 100%⚠ | 42 | 6 | 59 | QUEUED | — |
| 89 | security | oauth2-interview | 42 | 100%⚠ | 42 | 6 | 59 | QUEUED | — |
| 90 | system-design | design-search-interview | 30 | 73%⚠ | 24 | 15 | 59 | QUEUED | — |
| 91 | architecture | hexagonal-architecture-interview | 45 | 100%⚠ | 45 | 4 | 58 | QUEUED | — |
| 92 | architecture | resilience-patterns-interview | 43 | 100%⚠ | 43 | 5 | 58 | QUEUED | — |
| 93 | programming-languages | java-core-interview | 39 | 100%⚠ | 39 | 7 | 58 | QUEUED | — |
| 94 | ai-ml | long-context-vs-rag-interview | 30 | 100%⚠ | 30 | 11 | 57 | QUEUED | — |
| 95 | ai-ml | multi-agent-orchestration-interview | 30 | 100%⚠ | 30 | 11 | 57 | QUEUED | — |
| 96 | algorithms | greedy-algorithms-interview | 28 | 100%⚠ | 28 | 12 | 57 | QUEUED | — |
| 97 | databases | database-transactions-interview | 42 | 100%⚠ | 42 | 5 | 57 | QUEUED | — |
| 98 | devops | gradle-maven-interview | 38 | 100%⚠ | 38 | 7 | 57 | QUEUED | — |
| 99 | programming-languages | java-17-21-interview | 42 | 100%⚠ | 42 | 5 | 57 | QUEUED | — |
| 100 | programming-languages | kotlin-interop-java-interview | 38 | 100%⚠ | 38 | 7 | 57 | QUEUED | — |
| 101 | testing | contract-testing-interview | 42 | 100%⚠ | 42 | 5 | 57 | QUEUED | — |
| 102 | ai-ml | multimodal-ai-interview | 31 | 100%⚠ | 31 | 10 | 56 | QUEUED | — |
| 103 | algorithms | searching-algorithms-interview | 31 | 100%⚠ | 31 | 10 | 56 | QUEUED | — |
| 104 | architecture | clean-architecture-interview | 41 | 100%⚠ | 41 | 5 | 56 | QUEUED | — |
| 105 | devops | docker-interview | 41 | 100%⚠ | 41 | 5 | 56 | QUEUED | — |
| 106 | frameworks | spring-cloud-interview | 43 | 100%⚠ | 43 | 4 | 56 | QUEUED | — |
| 107 | programming-languages | go-concurrency-interview | 35 | 100%⚠ | 35 | 8 | 56 | QUEUED | — |
| 108 | ai-ml | llm-evaluation-interview | 30 | 100%⚠ | 30 | 10 | 55 | QUEUED | — |
| 109 | api | openapi-swagger-interview | 42 | 100%⚠ | 42 | 4 | 55 | QUEUED | — |
| 110 | programming-languages | go-interview | 36 | 100%⚠ | 36 | 7 | 55 | QUEUED | — |
| 111 | programming-languages | java-annotations-interview | 43 | 98%⚠ | 40 | 5 | 55 | QUEUED | — |
| 112 | programming-languages | kotlin-dsl-interview | 40 | 100%⚠ | 40 | 5 | 55 | QUEUED | — |
| 113 | programming-languages | typescript-interview | 33 | 97%⚠ | 30 | 10 | 55 | QUEUED | — |
| 114 | system-design | design-pastebin-interview | 30 | 100%⚠ | 30 | 10 | 55 | QUEUED | — |
| 115 | algorithms | recursion-interview | 27 | 100%⚠ | 27 | 11 | 54 | QUEUED | — |
| 116 | algorithms | two-pointers-sliding-window-interview | 33 | 61%⚠ | 29 | 10 | 54 | QUEUED | — |
| 117 | performance | caching-performance-interview | 23 | 100%⚠ | 23 | 13 | 54 | QUEUED | — |
| 118 | system-design | design-instagram-interview | 27 | 100%⚠ | 27 | 11 | 54 | QUEUED | — |
| 119 | ai-ml | prompt-engineering-interview | 28 | 100%⚠ | 28 | 10 | 53 | QUEUED | — |
| 120 | frameworks | ktor-interview | 32 | 100%⚠ | 32 | 8 | 53 | QUEUED | — |
| 121 | leadership | team-leadership-interview | 40 | 100%⚠ | 40 | 4 | 53 | QUEUED | — |
| 122 | programming-languages | java-serialization-interview | 40 | 100%⚠ | 40 | 4 | 53 | QUEUED | — |
| 123 | system-design | design-dropbox-interview | 30 | 100%⚠ | 26 | 11 | 53 | QUEUED | — |
| 124 | system-design | design-key-value-store-interview | 30 | 100%⚠ | 30 | 9 | 53 | QUEUED | — |
| 125 | testing | chaos-engineering-interview | 44 | 93%⚠ | 32 | 8 | 53 | QUEUED | — |
| 126 | algorithms | backtracking-interview | 27 | 100%⚠ | 27 | 10 | 52 | QUEUED | — |
| 127 | architecture | cdn-interview | 30 | 100%⚠ | 29 | 9 | 52 | QUEUED | — |
| 128 | data-engineering | apache-flink-interview | 31 | 100%⚠ | 31 | 8 | 52 | QUEUED | — |
| 129 | logging | logging-interview | 42 | 98%⚠ | 41 | 3 | 52 | QUEUED | — |
| 130 | programming-languages | java-conditional-statements-interview | 42 | 57%⚠ | 31 | 8 | 52 | QUEUED | — |
| 131 | ai-ml | ai-agents-interview | 28 | 100%⚠ | 28 | 9 | 51 | QUEUED | — |
| 132 | ai-ml | inference-optimization-interview | 36 | 100%⚠ | 36 | 5 | 51 | QUEUED | — |
| 133 | ai-ml | mcp-interview | 30 | 100%⚠ | 30 | 8 | 51 | QUEUED | — |
| 134 | api | websocket-interview | 38 | 100%⚠ | 38 | 4 | 51 | QUEUED | — |
| 135 | architecture | service-discovery-interview | 30 | 100%⚠ | 30 | 8 | 51 | QUEUED | — |
| 136 | frameworks | spring-framework-interview | 41 | 98%⚠ | 40 | 3 | 51 | QUEUED | — |
| 137 | monitoring | opentelemetry-interview | 28 | 100%⚠ | 28 | 9 | 51 | QUEUED | — |
| 138 | programming-languages | rust-interview | 33 | 70%⚠ | 24 | 11 | 51 | QUEUED | — |
| 139 | ai-ml | rag-interview | 30 | 43% | 18 | 16 | 50 | QUEUED | — |
| 140 | api | grpc-interview | 40 | 100%⚠ | 39 | 3 | 50 | QUEUED | — |
| 141 | architecture | ddd-interview | 38 | 76%⚠ | 25 | 10 | 50 | QUEUED | — |
| 142 | messaging | rabbitmq-interview | 41 | 100%⚠ | 37 | 4 | 50 | QUEUED | — |
| 143 | programming-languages | java-jackson-interview | 31 | 100%⚠ | 31 | 7 | 50 | QUEUED | — |
| 144 | programming-languages | java-lombok-interview | 27 | 100%⚠ | 27 | 9 | 50 | QUEUED | — |
| 145 | ai-ml | llm-integration-patterns-interview | 28 | 100%⚠ | 28 | 8 | 49 | QUEUED | — |
| 146 | ai-ml | model-serving-interview | 28 | 100%⚠ | 28 | 8 | 49 | QUEUED | — |
| 147 | ai-ml | vector-databases-interview | 28 | 100%⚠ | 28 | 8 | 49 | QUEUED | — |
| 148 | code-quality | code-review-interview | 40 | 55%⚠ | 28 | 8 | 49 | QUEUED | — |
| 149 | data-engineering | stream-processing-interview | 28 | 100%⚠ | 28 | 8 | 49 | QUEUED | — |
| 150 | databases | neo4j-interview | 30 | 100%⚠ | 30 | 7 | 49 | QUEUED | — |
| 151 | programming-languages | go-stdlib-interview | 30 | 100%⚠ | 30 | 7 | 49 | QUEUED | — |
| 152 | system-design | design-parking-lot-oo-interview | 28 | 100%⚠ | 28 | 8 | 49 | QUEUED | — |
| 153 | behavioral | behavioral-interview | 38 | 92%⚠ | 31 | 6 | 48 | QUEUED | — |
| 154 | programming-languages | go-memory-gc-interview | 27 | 100%⚠ | 27 | 8 | 48 | QUEUED | — |
| 155 | ai-ml | ai-compliance-governance-interview | 32 | 100%⚠ | 32 | 5 | 47 | QUEUED | — |
| 156 | ai-ml | mlops-interview | 28 | 100%⚠ | 28 | 7 | 47 | QUEUED | — |
| 157 | ai-ml | open-source-llms-interview | 34 | 100%⚠ | 34 | 4 | 47 | QUEUED | — |
| 158 | api | api-design-best-practices-interview | 30 | 97%⚠ | 28 | 7 | 47 | QUEUED | — |
| 159 | architecture | event-driven-patterns-interview | 40 | 62%⚠ | 22 | 10 | 47 | QUEUED | — |
| 160 | architecture | latency-numbers-interview | 24 | 100%⚠ | 24 | 9 | 47 | QUEUED | — |
| 161 | behavioral | culture-fit-interview | 22 | 100%⚠ | 22 | 10 | 47 | QUEUED | — |
| 162 | cloud | serverless-interview | 28 | 100%⚠ | 28 | 7 | 47 | QUEUED | — |
| 163 | databases | clickhouse-interview | 28 | 100%⚠ | 28 | 7 | 47 | QUEUED | — |
| 164 | jvm | jvm-interview | 40 | 88%⚠ | 28 | 7 | 47 | QUEUED | — |
| 165 | monitoring | prometheus-grafana-interview | 39 | 97%⚠ | 38 | 2 | 47 | QUEUED | — |
| 166 | programming-languages | go-generics-interview | 26 | 100%⚠ | 26 | 8 | 47 | QUEUED | — |
| 167 | programming-languages | java-modules-interview | 38 | 100%⚠ | 38 | 2 | 47 | QUEUED | — |
| 168 | reactive | reactive-streams-interview | 30 | 100%⚠ | 30 | 6 | 47 | QUEUED | — |
| 169 | system-design | design-payment-system-interview | 30 | 100%⚠ | 30 | 6 | 47 | QUEUED | — |
| 170 | system-design | design-rate-limiter-interview | 30 | 100%⚠ | 30 | 6 | 47 | QUEUED | — |
| 171 | code-quality | clean-code-practices-interview | 27 | 100%⚠ | 27 | 7 | 46 | QUEUED | — |
| 172 | devops | ansible-interview | 25 | 100%⚠ | 25 | 8 | 46 | QUEUED | — |
| 173 | frameworks | quarkus-interview | 31 | 100%⚠ | 31 | 5 | 46 | QUEUED | — |
| 174 | ai-ml | ai-observability-interview | 28 | 100%⚠ | 28 | 6 | 45 | QUEUED | — |
| 175 | cloud | aws-lambda-interview | 32 | 38% | 17 | 14 | 45 | QUEUED | — |
| 176 | databases | dynamodb-interview | 30 | 100%⚠ | 30 | 5 | 45 | QUEUED | — |
| 177 | devops | linux-interview | 33 | 73%⚠ | 22 | 9 | 45 | QUEUED | — |
| 178 | monitoring | logging-strategies-interview | 38 | 100%⚠ | 38 | 1 | 45 | QUEUED | — |
| 179 | programming-languages | go-testing-interview | 28 | 100%⚠ | 28 | 6 | 45 | QUEUED | — |
| 180 | system-design | design-youtube-interview | 28 | 100%⚠ | 28 | 6 | 45 | QUEUED | — |
| 181 | code-quality | code-smells-interview | 27 | 100%⚠ | 27 | 6 | 44 | QUEUED | — |
| 182 | frameworks | spring-webflux-interview | 43 | 58%⚠ | 27 | 6 | 44 | QUEUED | — |
| 183 | programming-languages | java-initialization-interview | 27 | 100%⚠ | 27 | 6 | 44 | QUEUED | — |
| 184 | databases | cockroachdb-interview | 24 | 100%⚠ | 24 | 7 | 43 | QUEUED | — |
| 185 | programming-languages | java-mapstruct-interview | 28 | 100%⚠ | 28 | 5 | 43 | QUEUED | — |
| 186 | system-design | design-netflix-interview | 30 | 100%⚠ | 30 | 4 | 43 | QUEUED | — |
| 187 | ai-ml | function-calling-interview | 30 | 63%⚠ | 25 | 6 | 42 | QUEUED | — |
| 188 | ai-ml | llm-basics-interview | 30 | 100%⚠ | 29 | 4 | 42 | QUEUED | — |
| 189 | code-quality | refactoring-patterns-interview | 42 | 36% | 20 | 11 | 42 | QUEUED | — |
| 190 | system-design | design-google-maps-interview | 30 | 70%⚠ | 23 | 7 | 42 | QUEUED | — |
| 191 | testing | property-based-testing-interview | 21 | 100%⚠ | 21 | 8 | 42 | QUEUED | — |
| 192 | data-engineering | dbt-interview | 28 | 100%⚠ | 28 | 4 | 41 | QUEUED | — |
| 193 | data-engineering | kafka-streams-interview | 28 | 100%⚠ | 28 | 4 | 41 | QUEUED | — |
| 194 | programming-languages | java-string-interview | 39 | 69%⚠ | 18 | 9 | 41 | QUEUED | — |
| 195 | reactive | reactive-testing-interview | 28 | 100%⚠ | 28 | 4 | 41 | QUEUED | — |
| 196 | system-design | design-feed-system-interview | 30 | 100%⚠ | 30 | 3 | 41 | QUEUED | — |
| 197 | testing | load-testing-interview | 22 | 100%⚠ | 22 | 7 | 41 | QUEUED | — |
| 198 | ai-ml | ai-safety-guardrails-interview | 32 | 31% | 22 | 9 | 40 | QUEUED | — |
| 199 | algorithms | stacks-queues-interview | 25 | 100%⚠ | 25 | 5 | 40 | QUEUED | — |
| 200 | code-quality | code-coverage-interview | 25 | 100%⚠ | 25 | 5 | 40 | QUEUED | — |
| 201 | frameworks | micronaut-interview | 26 | 100%⚠ | 25 | 5 | 40 | QUEUED | — |
| 202 | leadership | mentoring-interview | 25 | 40% | 0 | 20 | 40 | QUEUED | — |
| 203 | algorithms | algorithms-interview | 16 | 100%⚠ | 16 | 9 | 39 | QUEUED | — |
| 204 | algorithms | tries-interview | 28 | 100%⚠ | 28 | 3 | 39 | QUEUED | — |
| 205 | architecture | reverse-proxy-interview | 30 | 47% | 17 | 11 | 39 | QUEUED | — |
| 206 | cloud | aws-interview | 34 | 9% | 19 | 10 | 39 | QUEUED | — |
| 207 | code-quality | static-analysis-interview | 26 | 100%⚠ | 26 | 4 | 39 | QUEUED | — |
| 208 | frameworks | spring-aop-interview | 22 | 100%⚠ | 22 | 6 | 39 | QUEUED | — |
| 209 | leadership | estimations-planning-interview | 20 | 100%⚠ | 20 | 7 | 39 | QUEUED | — |
| 210 | monitoring | elk-stack-interview | 26 | 100%⚠ | 26 | 4 | 39 | QUEUED | — |
| 211 | reactive | webflux-interview | 28 | 100%⚠ | 28 | 3 | 39 | QUEUED | — |
| 212 | system-design | design-typeahead-interview | 30 | 27% | 21 | 9 | 39 | QUEUED | — |
| 213 | system-design | design-uber-interview | 30 | 0% | 25 | 7 | 39 | QUEUED | — |
| 214 | system-design | design-url-shortener-interview | 30 | 100%⚠ | 30 | 2 | 39 | QUEUED | — |
| 215 | system-design | design-vending-machine-oo-interview | 24 | 100%⚠ | 24 | 5 | 39 | QUEUED | — |
| 216 | algorithms | linked-lists-interview | 32 | 100%⚠ | 31 | 1 | 38 | QUEUED | — |
| 217 | architecture | dns-interview | 30 | 93%⚠ | 23 | 5 | 38 | QUEUED | — |
| 218 | data-engineering | apache-airflow-interview | 28 | 82%⚠ | 23 | 5 | 38 | QUEUED | — |
| 219 | frameworks | spring-boot-interview | 43 | 51%⚠ | 25 | 4 | 38 | QUEUED | — |
| 220 | system-design | design-twitter-interview | 27 | 100%⚠ | 27 | 3 | 38 | QUEUED | — |
| 221 | behavioral | leadership-stories-interview | 22 | 100%⚠ | 22 | 5 | 37 | QUEUED | — |
| 222 | data-engineering | data-lake-lakehouse-interview | 28 | 100%⚠ | 28 | 2 | 37 | QUEUED | — |
| 223 | devops | istio-service-mesh-interview | 26 | 100%⚠ | 26 | 3 | 37 | QUEUED | — |
| 224 | leadership | conflict-resolution-interview | 20 | 100%⚠ | 20 | 6 | 37 | QUEUED | — |
| 225 | messaging | message-brokers-comparison-interview | 26 | 100%⚠ | 26 | 3 | 37 | QUEUED | — |
| 226 | messaging | pulsar-interview | 22 | 100%⚠ | 22 | 5 | 37 | QUEUED | — |
| 227 | performance | network-performance-interview | 24 | 100%⚠ | 24 | 4 | 37 | QUEUED | — |
| 228 | security | secrets-management-interview | 22 | 100%⚠ | 22 | 5 | 37 | QUEUED | — |
| 229 | system-design | design-elevator-oo-interview | 26 | 100%⚠ | 26 | 3 | 37 | QUEUED | — |
| 230 | databases | hibernate-relationships-interview | 15 | 100%⚠ | 15 | 8 | 36 | QUEUED | — |
| 231 | messaging | redpanda-interview | 20 | 90%⚠ | 17 | 7 | 36 | QUEUED | — |
| 232 | ai-ml | agentic-patterns-interview | 30 | 20% | 27 | 4 | 35 | QUEUED | — |
| 233 | architecture | edge-computing-interview | 18 | 100%⚠ | 18 | 6 | 35 | QUEUED | — |
| 234 | behavioral | failure-stories-interview | 22 | 100%⚠ | 22 | 4 | 35 | QUEUED | — |
| 235 | behavioral | star-method-interview | 22 | 100%⚠ | 22 | 4 | 35 | QUEUED | — |
| 236 | devops | vault-interview | 26 | 100%⚠ | 26 | 2 | 35 | QUEUED | — |
| 237 | frameworks | spring-cache-interview | 18 | 100%⚠ | 18 | 6 | 35 | QUEUED | — |
| 238 | messaging | nats-interview | 24 | 100%⚠ | 24 | 3 | 35 | QUEUED | — |
| 239 | architecture | bff-pattern-interview | 17 | 100%⚠ | 17 | 6 | 34 | QUEUED | — |
| 240 | monitoring | jaeger-zipkin-interview | 23 | 100%⚠ | 23 | 3 | 34 | QUEUED | — |
| 241 | programming-languages | go-modules-interview | 27 | 100%⚠ | 27 | 1 | 34 | QUEUED | — |
| 242 | programming-languages | java-optional-interview | 15 | 100%⚠ | 15 | 7 | 34 | QUEUED | — |
| 243 | system-design | design-ecommerce-delivery-interview | 36 | 78%⚠ | 13 | 8 | 34 | QUEUED | — |
| 244 | system-design | design-web-crawler-interview | 26 | 92%⚠ | 23 | 3 | 34 | QUEUED | — |
| 245 | api | api-versioning-interview | 20 | 100%⚠ | 20 | 4 | 33 | QUEUED | — |
| 246 | architecture | strangler-fig-interview | 18 | 100%⚠ | 18 | 5 | 33 | QUEUED | — |
| 247 | behavioral | conflict-stories-interview | 22 | 100%⚠ | 22 | 3 | 33 | QUEUED | — |
| 248 | devops | kubernetes-interview | 45 | 56%⚠ | 20 | 4 | 33 | QUEUED | — |
| 249 | leadership | technical-decisions-interview | 22 | 100%⚠ | 22 | 3 | 33 | QUEUED | — |
| 250 | reactive | reactive-patterns-interview | 26 | 100%⚠ | 26 | 1 | 33 | QUEUED | — |
| 251 | security | mtls-interview | 20 | 100%⚠ | 20 | 4 | 33 | QUEUED | — |
| 252 | security | supply-chain-security-interview | 24 | 100%⚠ | 24 | 2 | 33 | QUEUED | — |
| 253 | testing | mutation-testing-interview | 20 | 100%⚠ | 20 | 4 | 33 | QUEUED | — |
| 254 | cloud | cloud-native-patterns-interview | 30 | 53%⚠ | 15 | 6 | 32 | QUEUED | — |
| 255 | devops | consul-interview | 24 | 100%⚠ | 24 | 1 | 31 | QUEUED | — |
| 256 | frameworks | vertx-interview | 31 | 52%⚠ | 16 | 5 | 31 | QUEUED | — |
| 257 | monitoring | loki-grafana-interview | 28 | 43% | 17 | 7 | 31 | QUEUED | — |
| 258 | ai-ml | code-agents-interview | 31 | 58%⚠ | 21 | 2 | 30 | QUEUED | — |
| 259 | databases | hibernate-caching-interview | 15 | 100%⚠ | 15 | 5 | 30 | QUEUED | — |
| 260 | databases | hibernate-jpql-criteria-interview | 16 | 88%⚠ | 13 | 6 | 30 | QUEUED | — |
| 261 | frameworks | spring-kafka-interview | 15 | 100%⚠ | 15 | 5 | 30 | QUEUED | — |
| 262 | frameworks | spring-retry-interview | 17 | 100%⚠ | 17 | 4 | 30 | QUEUED | — |
| 263 | frameworks | spring-testing-interview | 16 | 94%⚠ | 15 | 5 | 30 | QUEUED | — |
| 264 | frameworks | spring-transaction-interview | 15 | 100%⚠ | 15 | 5 | 30 | QUEUED | — |
| 265 | leadership | tech-interviewing-interview | 20 | 100%⚠ | 19 | 3 | 30 | QUEUED | — |
| 266 | programming-languages | kotlin-flow-interview | 17 | 100%⚠ | 17 | 4 | 30 | QUEUED | — |
| 267 | messaging | aws-sqs-sns-interview | 22 | 100%⚠ | 22 | 1 | 29 | QUEUED | — |
| 268 | api | rest-maturity-interview | 17 | 100%⚠ | 17 | 3 | 28 | QUEUED | — |
| 269 | databases | scylladb-interview | 23 | 100%⚠ | 23 | 0 | 28 | QUEUED | — |
| 270 | frameworks | spring-integration-interview | 15 | 100%⚠ | 15 | 4 | 28 | QUEUED | — |
| 271 | frameworks | spring-state-machine-interview | 15 | 100%⚠ | 15 | 4 | 28 | QUEUED | — |
| 272 | jvm | graalvm-native-interview | 15 | 100%⚠ | 15 | 4 | 28 | QUEUED | — |
| 273 | security | zero-trust-interview | 19 | 100%⚠ | 19 | 2 | 28 | QUEUED | — |
| 274 | devops | linkerd-interview | 20 | 100%⚠ | 20 | 1 | 27 | QUEUED | — |
| 275 | frameworks | resilience4j-interview | 20 | 100%⚠ | 20 | 1 | 27 | QUEUED | — |
| 276 | frameworks | spring-ai-interview | 18 | 89%⚠ | 16 | 3 | 27 | QUEUED | — |
| 277 | frameworks | spring-data-jdbc-interview | 16 | 100%⚠ | 16 | 3 | 27 | QUEUED | — |
| 278 | frameworks | spring-events-interview | 16 | 100%⚠ | 16 | 3 | 27 | QUEUED | — |
| 279 | frameworks | spring-validation-interview | 16 | 100%⚠ | 16 | 3 | 27 | QUEUED | — |
| 280 | monitoring | micrometer-interview | 20 | 100%⚠ | 20 | 1 | 27 | QUEUED | — |
| 281 | programming-languages | java-functional-interface-interview | 14 | 100%⚠ | 14 | 4 | 27 | QUEUED | — |
| 282 | programming-languages | java-reflection-interview | 16 | 100%⚠ | 16 | 3 | 27 | QUEUED | — |
| 283 | cloud | azure-interview | 25 | 40% | 18 | 4 | 26 | QUEUED | — |
| 284 | data-engineering | data-warehousing-interview | 30 | 33% | 18 | 4 | 26 | QUEUED | — |
| 285 | frameworks | spring-graphql-interview | 15 | 100%⚠ | 15 | 3 | 26 | QUEUED | — |
| 286 | frameworks | spring-modulith-interview | 15 | 100%⚠ | 15 | 3 | 26 | QUEUED | — |
| 287 | programming-languages | kotlin-sealed-classes-interview | 15 | 100%⚠ | 15 | 3 | 26 | QUEUED | — |
| 288 | programming-languages | kotlin-value-classes-interview | 15 | 93%⚠ | 13 | 4 | 26 | QUEUED | — |
| 289 | system-design | design-chat-system-interview | 21 | 100%⚠ | 21 | 0 | 26 | QUEUED | — |
| 290 | frameworks | spring-scheduling-interview | 16 | 100%⚠ | 14 | 3 | 25 | QUEUED | — |
| 291 | cloud | gcp-interview | 28 | 36% | 16 | 4 | 24 | QUEUED | — |
| 292 | frameworks | spring-async-interview | 15 | 100%⚠ | 15 | 2 | 24 | QUEUED | — |
| 293 | frameworks | spring-messaging-interview | 15 | 100%⚠ | 15 | 2 | 24 | QUEUED | — |
| 294 | frameworks | spring-r2dbc-interview | 15 | 100%⚠ | 15 | 2 | 24 | QUEUED | — |
| 295 | frameworks | spring-vault-interview | 15 | 100%⚠ | 15 | 2 | 24 | QUEUED | — |
| 296 | programming-languages | java-records-interview | 15 | 100%⚠ | 15 | 2 | 24 | QUEUED | — |
| 297 | testing | junit-interview | 15 | 100%⚠ | 15 | 2 | 24 | QUEUED | — |
| 298 | frameworks | spring-boot-3-migration-interview | 15 | 100%⚠ | 15 | 1 | 22 | QUEUED | — |
| 299 | frameworks | spring-session-interview | 15 | 100%⚠ | 15 | 1 | 22 | QUEUED | — |
| 300 | programming-languages | java-pattern-matching-interview | 15 | 100%⚠ | 15 | 1 | 22 | QUEUED | — |
| 301 | programming-languages | java-virtual-threads-interview | 15 | 100%⚠ | 15 | 1 | 22 | QUEUED | — |
| 302 | testing | rest-assured-interview | 15 | 100%⚠ | 15 | 1 | 22 | QUEUED | — |
| 303 | testing | selenium-interview | 15 | 100%⚠ | 15 | 1 | 22 | QUEUED | — |
| 304 | programming-languages | kotlin-spring-interview | 15 | 100%⚠ | 12 | 2 | 21 | QUEUED | — |
| 305 | frameworks | spring-rest-client-interview | 13 | 100%⚠ | 13 | 1 | 20 | QUEUED | — |
| 306 | messaging | kafka-interview | 50 | 70%⚠ | 9 | 2 | 18 | QUEUED | — |
| 307 | programming-languages | java-completable-future-interview | 15 | 80%⚠ | 7 | 3 | 18 | QUEUED | — |
| 308 | ai-ml | langchain4j-interview | 18 | 28% | 1 | 2 | 5 | QUEUED | — |
| 309 | programming-languages | java-22-25-interview | 22 | 45% | 2 | 1 | 4 | QUEUED | — |
| 310 | testing | cucumber-bdd-interview | 15 | 33% | 0 | 1 | 2 | QUEUED | — |
| 311 | databases | jooq-interview | 18 | 33% | 1 | 0 | 1 | QUEUED | — |
| 312 | jvm | crac-interview | 16 | 38% | 1 | 0 | 1 | QUEUED | — |
| 313 | testing | archunit-interview | 15 | 20% | 1 | 0 | 1 | QUEUED | — |
| 314 | messaging | apache-camel-interview | 16 | 12% | 0 | 0 | 0 | QUEUED | — |
| 315 | messaging | jms-activemq-interview | 16 | 19% | 0 | 0 | 0 | QUEUED | — |
| 316 | performance | jmh-microbenchmarking-interview | 18 | 22% | 0 | 0 | 0 | QUEUED | — |
| 317 | programming-languages | kotlin-testing-interview | 20 | 35% | 0 | 0 | 0 | QUEUED | — |
| 318 | programming-languages | scala-effects-interview | 16 | 25% | 0 | 0 | 0 | QUEUED | — |

## 8. Прогресс (round 3 — Plausibility, старт 2026-07-01)

**RESET выполнен 2026-07-01** (см. баннер). Прогресс round 2 (Option Parity) обнулён в таблице;
коммиты round 2 остаются в git-истории (~30 сидеров: `java-concurrency` 7c013c0d … `design-uber` f0119a42,
`kubernetes` 00bf1489). Под объединённым баром Option Parity + **Plausibility Parity** файлы переоткрыты.

- ✅ Plausibility Parity + анти-паттерн **Inflated Caricature Distractor** закреплены в `mcq-quality-fixer` и `interview-writer`.
- ✅ Детектор `scripts/audit-mcq-parity.py --caricature` добавлен; прогнан по корпусу (2204 блока-кандидата в 308 файлах).
- ⏳ Идёт workflow `wn86pui6x` (round 2 parity): `ai-safety-guardrails` (issues→0), `spring-webflux`, `test-automation` — добиваем length-tell, на завершении harvest+commit, затем Plausibility-проход.
- ⏳ Следующая цель round 3: `leadership/mentoring-interview` (28 caric-маркеров в 25 блоках — приоритетный negative example), далее soft-skill/leadership-файлы, затем worst-first по §7.

**Честно:** round 3 объединяет две линзы. Length/structure-tell (ParityФл) и caricature-tell (Caric-бл)
чинятся вместе: дистрактор поднимается до паритета по длине/форме И остаётся правдоподобной ошибочной
моделью (без токсичных/категоричных маркеров). «Все сидеры исправлены» — цель цикла, не одного тика.

## 9. Координация

Параллельная claude-сессия работает worst-first СВЕРХУ (frontier ranks 12–14, static:
`agentic-patterns`/`git`/`chaos-engineering`). Я беру нижний слой; перед каждым слайсом —
`git status seed/mcq` → skip их dirty-файлов.

