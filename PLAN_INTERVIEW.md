# PLAN_INTERVIEW — качество MCQ JSON-сидеров (Option Parity / Structural Parity)

Бесконечный `/loop 15m` цикл (cron `ae1e19ff`). Цель: правильный вариант в MCQ
**нельзя угадать по форме** — длине, структуре, насыщенности, стилю. Лечим не
сокращением correct, а **поднятием distractor-ов** до его уровня (плюс перенос
лишних деталей correct в `sections`, которые рендерятся после ответа).

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

## 7. Реестр всех сидеров (worst-first по severity)

Всего сидеров: **318**. Источник: `scripts/audit-mcq-parity.py` (2026-06-30). Колонки-«пункты» = число блоков в файле, триггерящих каждый сигнал угадываемости. Статус: ✅ закоммичен по Option Parity · ⏳ в очереди · 🚫 параллельная сессия (не трогать).

| # | Кат. | Сидер | Блок. | corr-long | LEN_AVG | LEN_SPR | UNIQ_MARK | COMMA | SHORT_D | Σфлаг | St | Commit |
|--:|---|---|--:|--:|--:|--:|--:|--:|--:|--:|:-:|---|
| 1 | databases | `postgresql-interview` | 55 | 100%⚠ | 55 | 55 | 41 | 8 | 52 | 55 | ⏳ |  |
| 2 | testing | `test-automation-interview` | 50 | 100%⚠ | 50 | 50 | 41 | 25 | 50 | 50 | ⏳ |  |
| 3 | reactive | `project-reactor-interview` | 48 | 98%⚠ | 47 | 47 | 37 | 8 | 47 | 47 | ⏳ |  |
| 4 | databases | `mongodb-interview` | 46 | 100%⚠ | 46 | 46 | 29 | 19 | 46 | 46 | ⏳ |  |
| 5 | programming-languages | `java-collections-interview` | 46 | 100%⚠ | 46 | 46 | 33 | 20 | 46 | 46 | ⏳ |  |
| 6 | programming-languages | `java-concurrency-interview` | 56 | 89%⚠ | 29 | 23 | 33 | 2 | 6 | 46 | ✅ | 7c013c0d |
| 7 | reactive | `rxjava-interview` | 46 | 100%⚠ | 46 | 46 | 39 | 16 | 46 | 46 | ⏳ |  |
| 8 | architecture | `hexagonal-architecture-interview` | 45 | 100%⚠ | 45 | 45 | 39 | 15 | 45 | 45 | ⏳ |  |
| 9 | programming-languages | `kotlin-interview` | 45 | 100%⚠ | 44 | 43 | 36 | 7 | 39 | 45 | ⏳ |  |
| 10 | security | `application-security-interview` | 45 | 100%⚠ | 45 | 45 | 27 | 23 | 45 | 45 | ⏳ |  |
| 11 | security | `authentication-authorization-patterns-interview` | 45 | 100%⚠ | 45 | 45 | 34 | 24 | 45 | 45 | ⏳ |  |
| 12 | security | `owasp-top10-interview` | 45 | 100%⚠ | 45 | 45 | 34 | 29 | 45 | 45 | ⏳ |  |
| 13 | security | `tls-ssl-interview` | 45 | 100%⚠ | 45 | 45 | 29 | 19 | 45 | 45 | ⏳ |  |
| 14 | testing | `mockito-interview` | 45 | 89%⚠ | 43 | 45 | 27 | 5 | 42 | 45 | ⏳ |  |
| 15 | testing | `test-strategies-interview` | 45 | 100%⚠ | 45 | 45 | 37 | 33 | 44 | 45 | ⏳ |  |
| 16 | testing | `unit-testing-interview` | 45 | 100%⚠ | 45 | 45 | 35 | 15 | 45 | 45 | ⏳ |  |
| 17 | databases | `cassandra-interview` | 44 | 100%⚠ | 44 | 44 | 44 | 35 | 44 | 44 | ⏳ |  |
| 18 | databases | `elasticsearch-interview` | 44 | 100%⚠ | 44 | 44 | 34 | 29 | 44 | 44 | ⏳ |  |
| 19 | design-patterns | `design-patterns-interview` | 48 | 100%⚠ | 43 | 37 | 31 | 7 | 27 | 44 | ⏳ |  |
| 20 | architecture | `networking-interview` | 43 | 100%⚠ | 43 | 43 | 36 | 31 | 43 | 43 | ⏳ |  |
| 21 | architecture | `resilience-patterns-interview` | 43 | 100%⚠ | 43 | 43 | 29 | 22 | 43 | 43 | ⏳ |  |
| 22 | architecture | `saga-pattern-interview` | 43 | 100%⚠ | 43 | 43 | 43 | 22 | 43 | 43 | ⏳ |  |
| 23 | databases | `hibernate-interview` | 50 | 92%⚠ | 43 | 43 | 18 | 2 | 38 | 43 | ⏳ |  |
| 24 | databases | `redis-interview` | 43 | 100%⚠ | 43 | 43 | 43 | 22 | 43 | 43 | ⏳ |  |
| 25 | devops | `helm-interview` | 43 | 100%⚠ | 43 | 43 | 26 | 17 | 43 | 43 | ⏳ |  |
| 26 | frameworks | `spring-batch-interview` | 43 | 100%⚠ | 43 | 43 | 40 | 17 | 43 | 43 | ⏳ |  |
| 27 | frameworks | `spring-boot-actuator-interview` | 43 | 100%⚠ | 43 | 43 | 36 | 15 | 43 | 43 | ⏳ |  |
| 28 | frameworks | `spring-cloud-interview` | 43 | 100%⚠ | 43 | 43 | 32 | 33 | 43 | 43 | ⏳ |  |
| 29 | frameworks | `spring-mvc-interview` | 43 | 100%⚠ | 43 | 43 | 43 | 31 | 43 | 43 | ⏳ |  |
| 30 | frameworks | `spring-security-interview` | 46 | 96%⚠ | 43 | 43 | 31 | 19 | 43 | 43 | ⏳ |  |
| 31 | frameworks | `spring-webflux-interview` | 43 | 100%⚠ | 43 | 43 | 40 | 25 | 43 | 43 | ⏳ |  |
| 32 | programming-languages | `java-oop-interview` | 43 | 100%⚠ | 43 | 43 | 35 | 18 | 43 | 43 | ⏳ |  |
| 33 | programming-languages | `kotlin-collections-interview` | 43 | 100%⚠ | 43 | 43 | 41 | 9 | 42 | 43 | ⏳ |  |
| 34 | programming-languages | `kotlin-serialization-interview` | 43 | 100%⚠ | 43 | 43 | 26 | 13 | 43 | 43 | ⏳ |  |
| 35 | security | `jwt-interview` | 43 | 100%⚠ | 43 | 43 | 35 | 25 | 43 | 43 | ⏳ |  |
| 36 | api | `openapi-swagger-interview` | 42 | 100%⚠ | 42 | 42 | 36 | 16 | 42 | 42 | ⏳ |  |
| 37 | architecture | `caching-strategies-interview` | 42 | 100%⚠ | 42 | 42 | 38 | 19 | 42 | 42 | ⏳ |  |
| 38 | architecture | `cap-theorem-interview` | 42 | 100%⚠ | 42 | 42 | 25 | 12 | 41 | 42 | ⏳ |  |
| 39 | architecture | `consistency-patterns-interview` | 42 | 100%⚠ | 42 | 42 | 31 | 7 | 41 | 42 | ⏳ |  |
| 40 | architecture | `microservices-interview` | 42 | 100%⚠ | 42 | 42 | 36 | 23 | 42 | 42 | ⏳ |  |
| 41 | databases | `database-transactions-interview` | 42 | 100%⚠ | 42 | 42 | 32 | 26 | 42 | 42 | ⏳ |  |
| 42 | databases | `flyway-liquibase-interview` | 42 | 100%⚠ | 42 | 42 | 35 | 25 | 42 | 42 | ⏳ |  |
| 43 | devops | `argocd-interview` | 42 | 100%⚠ | 42 | 42 | 34 | 26 | 42 | 42 | ⏳ |  |
| 44 | devops | `terraform-interview` | 42 | 100%⚠ | 42 | 42 | 34 | 21 | 42 | 42 | ⏳ |  |
| 45 | frameworks | `spring-data-jpa-interview` | 43 | 98%⚠ | 42 | 42 | 36 | 13 | 42 | 42 | ⏳ |  |
| 46 | performance | `application-profiling-interview` | 42 | 100%⚠ | 42 | 42 | 34 | 11 | 42 | 42 | ⏳ |  |
| 47 | performance | `performance-testing-interview` | 42 | 100%⚠ | 42 | 42 | 40 | 32 | 42 | 42 | ⏳ |  |
| 48 | programming-languages | `java-17-21-interview` | 42 | 100%⚠ | 42 | 42 | 37 | 12 | 42 | 42 | ⏳ |  |
| 49 | programming-languages | `java-8-interview` | 42 | 98%⚠ | 41 | 42 | 29 | 19 | 41 | 42 | ⏳ |  |
| 50 | programming-languages | `java-exceptions-interview` | 42 | 98%⚠ | 42 | 42 | 8 | 4 | 41 | 42 | ⏳ |  |
| 51 | programming-languages | `java-stream-interview` | 42 | 100%⚠ | 42 | 41 | 19 | 18 | 40 | 42 | ⏳ |  |
| 52 | security | `oauth2-interview` | 42 | 100%⚠ | 42 | 42 | 24 | 18 | 42 | 42 | ⏳ |  |
| 53 | testing | `contract-testing-interview` | 42 | 100%⚠ | 42 | 42 | 41 | 29 | 42 | 42 | ⏳ |  |
| 54 | architecture | `clean-architecture-interview` | 41 | 100%⚠ | 41 | 41 | 27 | 13 | 41 | 41 | ⏳ |  |
| 55 | architecture | `cqrs-event-sourcing-interview` | 41 | 100%⚠ | 41 | 41 | 39 | 31 | 41 | 41 | ⏳ |  |
| 56 | architecture | `scalability-patterns-interview` | 41 | 100%⚠ | 41 | 41 | 34 | 19 | 41 | 41 | ⏳ |  |
| 57 | databases | `database-architecture-interview` | 41 | 100%⚠ | 41 | 41 | 38 | 9 | 41 | 41 | ⏳ |  |
| 58 | devops | `docker-interview` | 41 | 100%⚠ | 41 | 41 | 14 | 9 | 41 | 41 | ⏳ |  |
| 59 | logging | `logging-interview` | 42 | 98%⚠ | 41 | 41 | 33 | 16 | 40 | 41 | ⏳ |  |
| 60 | monitoring | `metrics-tracing-interview` | 41 | 100%⚠ | 41 | 41 | 26 | 11 | 41 | 41 | ⏳ |  |
| 61 | system-design | `system-design-interview` | 41 | 100%⚠ | 41 | 41 | 38 | 24 | 38 | 41 | ⏳ |  |
| 62 | architecture | `distributed-systems-interview` | 40 | 100%⚠ | 40 | 40 | 30 | 12 | 40 | 40 | ⏳ |  |
| 63 | architecture | `load-balancing-interview` | 40 | 100%⚠ | 40 | 40 | 26 | 13 | 40 | 40 | ⏳ |  |
| 64 | code-quality | `technical-debt-interview` | 40 | 100%⚠ | 40 | 40 | 34 | 13 | 40 | 40 | ⏳ |  |
| 65 | frameworks | `spring-framework-interview` | 41 | 98%⚠ | 40 | 40 | 35 | 20 | 40 | 40 | ⏳ |  |
| 66 | leadership | `code-review-practices-interview` | 40 | 100%⚠ | 40 | 40 | 30 | 33 | 40 | 40 | ⏳ |  |
| 67 | leadership | `team-leadership-interview` | 40 | 100%⚠ | 40 | 40 | 24 | 26 | 40 | 40 | ⏳ |  |
| 68 | monitoring | `observability-interview` | 42 | 95%⚠ | 39 | 38 | 19 | 15 | 36 | 40 | ⏳ |  |
| 69 | programming-languages | `java-annotations-interview` | 43 | 98%⚠ | 38 | 37 | 17 | 4 | 34 | 40 | ⏳ |  |
| 70 | programming-languages | `java-generics-interview` | 40 | 100%⚠ | 40 | 40 | 22 | 15 | 40 | 40 | ⏳ |  |
| 71 | programming-languages | `java-io-nio-interview` | 40 | 100%⚠ | 40 | 40 | 29 | 3 | 40 | 40 | ⏳ |  |
| 72 | programming-languages | `java-serialization-interview` | 40 | 100%⚠ | 40 | 40 | 32 | 7 | 39 | 40 | ⏳ |  |
| 73 | programming-languages | `kotlin-dsl-interview` | 40 | 100%⚠ | 40 | 40 | 33 | 18 | 40 | 40 | ⏳ |  |
| 74 | programming-languages | `kotlin-exceptions-interview` | 40 | 100%⚠ | 40 | 40 | 30 | 11 | 39 | 40 | ⏳ |  |
| 75 | programming-languages | `scala-interview` | 40 | 100%⚠ | 40 | 40 | 32 | 29 | 40 | 40 | ⏳ |  |
| 76 | testing | `integration-testing-interview` | 40 | 100%⚠ | 40 | 40 | 33 | 17 | 40 | 40 | ⏳ |  |
| 77 | testing | `testcontainers-interview` | 40 | 100%⚠ | 40 | 40 | 21 | 15 | 38 | 40 | ⏳ |  |
| 78 | api | `grpc-interview` | 40 | 100%⚠ | 39 | 37 | 26 | 5 | 32 | 39 | ⏳ |  |
| 79 | cicd | `deployment-strategies-interview` | 39 | 100%⚠ | 39 | 39 | 32 | 20 | 39 | 39 | ⏳ |  |
| 80 | performance | `memory-management-interview` | 39 | 100%⚠ | 39 | 39 | 34 | 19 | 39 | 39 | ⏳ |  |
| 81 | programming-languages | `java-core-interview` | 39 | 100%⚠ | 39 | 38 | 37 | 3 | 33 | 39 | ⏳ |  |
| 82 | programming-languages | `kotlin-coroutines-interview` | 39 | 100%⚠ | 39 | 39 | 31 | 3 | 37 | 39 | ⏳ |  |
| 83 | api | `websocket-interview` | 38 | 100%⚠ | 38 | 38 | 33 | 12 | 37 | 38 | ⏳ |  |
| 84 | architecture | `api-gateway-interview` | 38 | 100%⚠ | 38 | 38 | 28 | 24 | 38 | 38 | ⏳ |  |
| 85 | cicd | `pipeline-design-interview` | 38 | 100%⚠ | 38 | 38 | 33 | 28 | 38 | 38 | ⏳ |  |
| 86 | databases | `sql-interview` | 53 | 91%⚠ | 35 | 33 | 13 | 2 | 20 | 38 | ⏳ |  |
| 87 | devops | `gradle-maven-interview` | 38 | 100%⚠ | 38 | 38 | 23 | 16 | 38 | 38 | ⏳ |  |
| 88 | monitoring | `logging-strategies-interview` | 38 | 100%⚠ | 38 | 38 | 33 | 14 | 38 | 38 | ⏳ |  |
| 89 | monitoring | `prometheus-grafana-interview` | 39 | 97%⚠ | 36 | 33 | 14 | 8 | 30 | 38 | ⏳ |  |
| 90 | performance | `jvm-performance-tuning-interview` | 38 | 100%⚠ | 38 | 38 | 21 | 25 | 38 | 38 | ⏳ |  |
| 91 | programming-languages | `java-modules-interview` | 38 | 100%⚠ | 38 | 38 | 35 | 8 | 38 | 38 | ⏳ |  |
| 92 | programming-languages | `java-types-interview` | 38 | 100%⚠ | 38 | 38 | 27 | 8 | 38 | 38 | ⏳ |  |
| 93 | programming-languages | `kotlin-interop-java-interview` | 38 | 100%⚠ | 38 | 38 | 36 | 3 | 38 | 38 | ⏳ |  |
| 94 | api | `graphql-interview` | 40 | 95%⚠ | 37 | 35 | 22 | 5 | 31 | 37 | ⏳ |  |
| 95 | api | `http-rest-interview` | 43 | 95%⚠ | 34 | 32 | 26 | 6 | 27 | 37 | ⏳ |  |
| 96 | devops | `git-interview` | 43 | 91%⚠ | 23 | 17 | 27 | 1 | 8 | 37 | 🚫 |  |
| 97 | messaging | `rabbitmq-interview` | 41 | 100%⚠ | 35 | 30 | 26 | 1 | 22 | 37 | ⏳ |  |
| 98 | ai-ml | `inference-optimization-interview` | 36 | 100%⚠ | 36 | 36 | 28 | 25 | 35 | 36 | ⏳ |  |
| 99 | algorithms | `arrays-strings-interview` | 36 | 100%⚠ | 36 | 36 | 29 | 6 | 36 | 36 | ⏳ |  |
| 100 | programming-languages | `go-interview` | 36 | 100%⚠ | 36 | 36 | 26 | 15 | 36 | 36 | ⏳ |  |
| 101 | ai-ml | `fine-tuning-llm-interview` | 35 | 100%⚠ | 35 | 35 | 34 | 30 | 35 | 35 | ⏳ |  |
| 102 | data-engineering | `apache-spark-interview` | 35 | 100%⚠ | 35 | 35 | 24 | 16 | 35 | 35 | ⏳ |  |
| 103 | programming-languages | `go-concurrency-interview` | 35 | 100%⚠ | 35 | 34 | 24 | 7 | 34 | 35 | ⏳ |  |
| 104 | ai-ml | `open-source-llms-interview` | 34 | 100%⚠ | 34 | 34 | 29 | 25 | 34 | 34 | ⏳ |  |
| 105 | algorithms | `hash-tables-interview` | 34 | 100%⚠ | 34 | 34 | 28 | 9 | 34 | 34 | ⏳ |  |
| 106 | algorithms | `trees-interview` | 34 | 100%⚠ | 34 | 34 | 32 | 12 | 34 | 34 | ⏳ |  |
| 107 | databases | `database-sharding-interview` | 34 | 100%⚠ | 34 | 34 | 31 | 27 | 34 | 34 | ⏳ |  |
| 108 | algorithms | `dynamic-programming-interview` | 33 | 100%⚠ | 33 | 33 | 30 | 6 | 33 | 33 | ⏳ |  |
| 109 | ai-ml | `ai-application-architecture-interview` | 32 | 100%⚠ | 32 | 32 | 30 | 20 | 32 | 32 | ⏳ |  |
| 110 | ai-ml | `ai-compliance-governance-interview` | 32 | 100%⚠ | 32 | 32 | 31 | 29 | 32 | 32 | ⏳ |  |
| 111 | ai-ml | `ai-safety-guardrails-interview` | 32 | 100%⚠ | 32 | 32 | 30 | 30 | 32 | 32 | ⏳ |  |
| 112 | frameworks | `ktor-interview` | 32 | 100%⚠ | 32 | 32 | 12 | 11 | 32 | 32 | ⏳ |  |
| 113 | testing | `chaos-engineering-interview` | 44 | 93%⚠ | 7 | 4 | 26 | 3 | 1 | 32 | 🚫 |  |
| 114 | ai-ml | `multimodal-ai-interview` | 31 | 100%⚠ | 31 | 31 | 26 | 20 | 31 | 31 | ⏳ |  |
| 115 | algorithms | `complexity-analysis-interview` | 31 | 100%⚠ | 31 | 31 | 23 | 8 | 31 | 31 | ⏳ |  |
| 116 | algorithms | `linked-lists-interview` | 32 | 100%⚠ | 31 | 31 | 28 | 2 | 31 | 31 | ⏳ |  |
| 117 | algorithms | `searching-algorithms-interview` | 31 | 100%⚠ | 31 | 31 | 26 | 12 | 31 | 31 | ⏳ |  |
| 118 | algorithms | `sorting-algorithms-interview` | 31 | 100%⚠ | 31 | 31 | 23 | 20 | 31 | 31 | ⏳ |  |
| 119 | behavioral | `behavioral-interview` | 38 | 92%⚠ | 27 | 26 | 13 | 1 | 17 | 31 | ⏳ |  |
| 120 | data-engineering | `apache-flink-interview` | 31 | 100%⚠ | 31 | 31 | 24 | 16 | 31 | 31 | ⏳ |  |
| 121 | databases | `database-replication-interview` | 31 | 100%⚠ | 31 | 31 | 25 | 17 | 31 | 31 | ⏳ |  |
| 122 | frameworks | `quarkus-interview` | 31 | 100%⚠ | 31 | 31 | 27 | 18 | 31 | 31 | ⏳ |  |
| 123 | programming-languages | `java-conditional-statements-interview` | 42 | 57%⚠ | 21 | 28 | 10 | 1 | 17 | 31 | ⏳ |  |
| 124 | programming-languages | `java-jackson-interview` | 31 | 100%⚠ | 31 | 31 | 22 | 9 | 31 | 31 | ⏳ |  |
| 125 | ai-ml | `llm-evaluation-interview` | 30 | 100%⚠ | 30 | 30 | 26 | 18 | 30 | 30 | ⏳ |  |
| 126 | ai-ml | `long-context-vs-rag-interview` | 30 | 100%⚠ | 30 | 30 | 28 | 18 | 30 | 30 | ⏳ |  |
| 127 | ai-ml | `mcp-interview` | 30 | 100%⚠ | 30 | 30 | 26 | 24 | 30 | 30 | ⏳ |  |
| 128 | ai-ml | `multi-agent-orchestration-interview` | 30 | 100%⚠ | 30 | 30 | 21 | 23 | 30 | 30 | ⏳ |  |
| 129 | ai-ml | `reasoning-models-interview` | 30 | 100%⚠ | 30 | 30 | 20 | 17 | 28 | 30 | ⏳ |  |
| 130 | architecture | `service-discovery-interview` | 30 | 100%⚠ | 30 | 30 | 25 | 23 | 30 | 30 | ⏳ |  |
| 131 | databases | `dynamodb-interview` | 30 | 100%⚠ | 30 | 30 | 23 | 20 | 30 | 30 | ⏳ |  |
| 132 | databases | `neo4j-interview` | 30 | 100%⚠ | 30 | 30 | 23 | 9 | 30 | 30 | ⏳ |  |
| 133 | programming-languages | `go-stdlib-interview` | 30 | 100%⚠ | 30 | 30 | 18 | 7 | 30 | 30 | ⏳ |  |
| 134 | programming-languages | `typescript-interview` | 33 | 97%⚠ | 29 | 26 | 15 | 3 | 22 | 30 | ⏳ |  |
| 135 | reactive | `reactive-streams-interview` | 30 | 100%⚠ | 30 | 30 | 14 | 5 | 30 | 30 | ⏳ |  |
| 136 | system-design | `design-feed-system-interview` | 30 | 100%⚠ | 30 | 30 | 29 | 23 | 30 | 30 | ⏳ |  |
| 137 | system-design | `design-key-value-store-interview` | 30 | 100%⚠ | 30 | 30 | 27 | 23 | 30 | 30 | ⏳ |  |
| 138 | system-design | `design-netflix-interview` | 30 | 100%⚠ | 30 | 30 | 27 | 23 | 30 | 30 | ⏳ |  |
| 139 | system-design | `design-pastebin-interview` | 30 | 100%⚠ | 30 | 30 | 27 | 20 | 30 | 30 | ⏳ |  |
| 140 | system-design | `design-payment-system-interview` | 30 | 100%⚠ | 30 | 30 | 27 | 26 | 30 | 30 | ⏳ |  |
| 141 | system-design | `design-rate-limiter-interview` | 30 | 100%⚠ | 30 | 30 | 28 | 21 | 30 | 30 | ⏳ |  |
| 142 | system-design | `design-url-shortener-interview` | 30 | 100%⚠ | 30 | 30 | 25 | 18 | 30 | 30 | ⏳ |  |
| 143 | ai-ml | `embeddings-interview` | 29 | 100%⚠ | 29 | 28 | 27 | 15 | 28 | 29 | ⏳ |  |
| 144 | ai-ml | `llm-basics-interview` | 30 | 100%⚠ | 28 | 27 | 16 | 4 | 22 | 29 | ⏳ |  |
| 145 | algorithms | `two-pointers-sliding-window-interview` | 33 | 61%⚠ | 4 | 11 | 25 | 2 | 3 | 29 | ✅ | 9b7f4fdf |
| 146 | algorithms | `graphs-interview` | 34 | 74%⚠ | 3 | 3 | 28 | 1 | 1 | 29 | ✅ | dcc1af28 |
| 147 | algorithms | `heaps-interview` | 29 | 100%⚠ | 29 | 29 | 26 | 8 | 29 | 29 | ⏳ |  |
| 148 | architecture | `cdn-interview` | 30 | 100%⚠ | 28 | 28 | 21 | 20 | 27 | 29 | ⏳ |  |
| 149 | ai-ml | `ai-agents-interview` | 28 | 100%⚠ | 28 | 28 | 24 | 22 | 28 | 28 | ⏳ |  |
| 150 | ai-ml | `ai-observability-interview` | 28 | 100%⚠ | 28 | 28 | 25 | 24 | 28 | 28 | ⏳ |  |
| 151 | ai-ml | `llm-integration-patterns-interview` | 28 | 100%⚠ | 28 | 28 | 26 | 13 | 28 | 28 | ⏳ |  |
| 152 | ai-ml | `mlops-interview` | 28 | 100%⚠ | 28 | 28 | 27 | 25 | 28 | 28 | ⏳ |  |
| 153 | ai-ml | `model-serving-interview` | 28 | 100%⚠ | 28 | 28 | 24 | 17 | 28 | 28 | ⏳ |  |
| 154 | ai-ml | `prompt-engineering-interview` | 28 | 100%⚠ | 28 | 28 | 21 | 11 | 27 | 28 | ⏳ |  |
| 155 | ai-ml | `vector-databases-interview` | 28 | 100%⚠ | 28 | 27 | 17 | 7 | 24 | 28 | ⏳ |  |
| 156 | algorithms | `greedy-algorithms-interview` | 28 | 100%⚠ | 28 | 28 | 25 | 6 | 28 | 28 | ⏳ |  |
| 157 | algorithms | `tries-interview` | 28 | 100%⚠ | 28 | 28 | 21 | 7 | 28 | 28 | ⏳ |  |
| 158 | api | `api-design-best-practices-interview` | 30 | 97%⚠ | 22 | 25 | 17 | 4 | 21 | 28 | ⏳ |  |
| 159 | cloud | `serverless-interview` | 28 | 100%⚠ | 28 | 28 | 23 | 24 | 28 | 28 | ⏳ |  |
| 160 | code-quality | `code-review-interview` | 40 | 55%⚠ | 7 | 11 | 17 | 10 | 3 | 28 | ✅ | b24efe14 |
| 161 | data-engineering | `data-lake-lakehouse-interview` | 28 | 100%⚠ | 28 | 28 | 25 | 23 | 28 | 28 | ⏳ |  |
| 162 | data-engineering | `dbt-interview` | 28 | 100%⚠ | 28 | 28 | 22 | 11 | 28 | 28 | ⏳ |  |
| 163 | data-engineering | `kafka-streams-interview` | 28 | 100%⚠ | 28 | 28 | 26 | 8 | 28 | 28 | ⏳ |  |
| 164 | data-engineering | `stream-processing-interview` | 28 | 100%⚠ | 28 | 28 | 24 | 12 | 28 | 28 | ⏳ |  |
| 165 | databases | `clickhouse-interview` | 28 | 100%⚠ | 28 | 28 | 16 | 12 | 28 | 28 | ⏳ |  |
| 166 | jvm | `jvm-interview` | 40 | 88%⚠ | 22 | 21 | 10 | 3 | 12 | 28 | ⏳ |  |
| 167 | monitoring | `opentelemetry-interview` | 28 | 100%⚠ | 28 | 28 | 26 | 18 | 28 | 28 | ⏳ |  |
| 168 | programming-languages | `go-testing-interview` | 28 | 100%⚠ | 28 | 28 | 21 | 11 | 28 | 28 | ⏳ |  |
| 169 | programming-languages | `java-mapstruct-interview` | 28 | 100%⚠ | 28 | 28 | 17 | 3 | 27 | 28 | ⏳ |  |
| 170 | reactive | `reactive-testing-interview` | 28 | 100%⚠ | 28 | 28 | 19 | 1 | 26 | 28 | ⏳ |  |
| 171 | reactive | `webflux-interview` | 28 | 100%⚠ | 28 | 28 | 18 | 6 | 28 | 28 | ⏳ |  |
| 172 | system-design | `design-parking-lot-oo-interview` | 28 | 100%⚠ | 28 | 28 | 25 | 20 | 28 | 28 | ⏳ |  |
| 173 | system-design | `design-youtube-interview` | 28 | 100%⚠ | 28 | 28 | 27 | 19 | 28 | 28 | ⏳ |  |
| 174 | algorithms | `backtracking-interview` | 27 | 100%⚠ | 27 | 27 | 20 | 13 | 27 | 27 | ⏳ |  |
| 175 | algorithms | `divide-and-conquer-interview` | 27 | 100%⚠ | 27 | 27 | 27 | 11 | 27 | 27 | ⏳ |  |
| 176 | algorithms | `recursion-interview` | 27 | 100%⚠ | 26 | 24 | 24 | 3 | 22 | 27 | ⏳ |  |
| 177 | code-quality | `clean-code-practices-interview` | 27 | 100%⚠ | 27 | 27 | 26 | 12 | 27 | 27 | ⏳ |  |
| 178 | code-quality | `code-smells-interview` | 27 | 100%⚠ | 27 | 27 | 13 | 10 | 26 | 27 | ⏳ |  |
| 179 | performance | `database-performance-interview` | 27 | 100%⚠ | 27 | 27 | 17 | 22 | 27 | 27 | ⏳ |  |
| 180 | programming-languages | `go-memory-gc-interview` | 27 | 100%⚠ | 27 | 27 | 17 | 9 | 27 | 27 | ⏳ |  |
| 181 | programming-languages | `go-modules-interview` | 27 | 100%⚠ | 27 | 27 | 18 | · | 27 | 27 | ⏳ |  |
| 182 | programming-languages | `java-initialization-interview` | 27 | 100%⚠ | 27 | 27 | 16 | 13 | 27 | 27 | ⏳ |  |
| 183 | programming-languages | `java-lombok-interview` | 27 | 100%⚠ | 27 | 27 | 14 | 3 | 26 | 27 | ⏳ |  |
| 184 | system-design | `design-instagram-interview` | 27 | 100%⚠ | 27 | 27 | 24 | 20 | 27 | 27 | ⏳ |  |
| 185 | system-design | `design-twitter-interview` | 27 | 100%⚠ | 27 | 27 | 19 | 16 | 27 | 27 | ⏳ |  |
| 186 | code-quality | `static-analysis-interview` | 26 | 100%⚠ | 26 | 26 | 16 | 18 | 26 | 26 | ⏳ |  |
| 187 | devops | `istio-service-mesh-interview` | 26 | 100%⚠ | 26 | 26 | 22 | 15 | 26 | 26 | ⏳ |  |
| 188 | devops | `vault-interview` | 26 | 100%⚠ | 26 | 26 | 23 | 8 | 26 | 26 | ⏳ |  |
| 189 | messaging | `message-brokers-comparison-interview` | 26 | 100%⚠ | 26 | 26 | 19 | 21 | 26 | 26 | ⏳ |  |
| 190 | monitoring | `elk-stack-interview` | 26 | 100%⚠ | 26 | 26 | 25 | 17 | 26 | 26 | ⏳ |  |
| 191 | programming-languages | `go-generics-interview` | 26 | 100%⚠ | 26 | 26 | 15 | 9 | 26 | 26 | ⏳ |  |
| 192 | reactive | `reactive-patterns-interview` | 26 | 100%⚠ | 26 | 26 | 21 | 2 | 25 | 26 | ⏳ |  |
| 193 | system-design | `design-dropbox-interview` | 30 | 100%⚠ | 9 | 8 | 24 | 5 | 4 | 26 | ✅ | ad3eb3bc |
| 194 | system-design | `design-elevator-oo-interview` | 26 | 100%⚠ | 26 | 26 | 24 | 18 | 26 | 26 | ⏳ |  |
| 195 | ai-ml | `function-calling-interview` | 30 | 63%⚠ | 15 | 21 | 17 | 2 | 13 | 25 | ✅ | cfe7b05a |
| 196 | algorithms | `stacks-queues-interview` | 25 | 100%⚠ | 25 | 25 | 17 | 7 | 25 | 25 | ⏳ |  |
| 197 | architecture | `ddd-interview` | 38 | 76%⚠ | 1 | 3 | 24 | 6 | · | 25 | ✅ | 2257299d |
| 198 | code-quality | `code-coverage-interview` | 25 | 100%⚠ | 25 | 25 | 17 | 10 | 25 | 25 | ⏳ |  |
| 199 | devops | `ansible-interview` | 25 | 100%⚠ | 25 | 25 | 24 | 15 | 25 | 25 | ⏳ |  |
| 200 | frameworks | `micronaut-interview` | 26 | 100%⚠ | 25 | 25 | 17 | 9 | 25 | 25 | ⏳ |  |
| 201 | frameworks | `spring-boot-interview` | 43 | 51%⚠ | 7 | 8 | 23 | 3 | 5 | 25 | ✅ | 9f65e55c |
| 202 | architecture | `latency-numbers-interview` | 24 | 100%⚠ | 24 | 24 | 21 | 12 | 22 | 24 | ⏳ |  |
| 203 | databases | `cockroachdb-interview` | 24 | 100%⚠ | 24 | 24 | 23 | 16 | 24 | 24 | ⏳ |  |
| 204 | devops | `consul-interview` | 24 | 100%⚠ | 24 | 24 | 20 | 13 | 24 | 24 | ⏳ |  |
| 205 | messaging | `nats-interview` | 24 | 100%⚠ | 24 | 24 | 19 | 9 | 24 | 24 | ⏳ |  |
| 206 | performance | `network-performance-interview` | 24 | 100%⚠ | 24 | 24 | 23 | 13 | 24 | 24 | ⏳ |  |
| 207 | programming-languages | `rust-interview` | 33 | 70%⚠ | 5 | 7 | 20 | 3 | 2 | 24 | ✅ | 28ba103b |
| 208 | security | `supply-chain-security-interview` | 24 | 100%⚠ | 24 | 24 | 21 | 19 | 24 | 24 | ⏳ |  |
| 209 | system-design | `design-search-interview` | 30 | 73%⚠ | 4 | 6 | 20 | 6 | 1 | 24 | ✅ | 61c76d03 |
| 210 | system-design | `design-vending-machine-oo-interview` | 24 | 100%⚠ | 24 | 24 | 23 | 16 | 24 | 24 | ⏳ |  |
| 211 | architecture | `dns-interview` | 30 | 93%⚠ | 12 | 11 | 21 | 2 | 10 | 23 | ✅ | 29ffdbbb |
| 212 | data-engineering | `apache-airflow-interview` | 28 | 82%⚠ | 1 | 3 | 23 | 3 | 1 | 23 | ✅ | 93220751 |
| 213 | databases | `scylladb-interview` | 23 | 100%⚠ | 23 | 23 | 22 | 12 | 23 | 23 | ⏳ |  |
| 214 | monitoring | `jaeger-zipkin-interview` | 23 | 100%⚠ | 23 | 23 | 15 | 14 | 23 | 23 | ⏳ |  |
| 215 | performance | `caching-performance-interview` | 23 | 100%⚠ | 23 | 23 | 22 | 14 | 23 | 23 | ⏳ |  |
| 216 | system-design | `design-google-maps-interview` | 30 | 70%⚠ | · | · | 22 | 7 | · | 23 | ✅ | fd6696d1 |
| 217 | system-design | `design-web-crawler-interview` | 26 | 92%⚠ | 22 | 22 | 12 | 5 | 21 | 23 | ⏳ |  |
| 218 | ai-ml | `agentic-patterns-interview` | 30 | 20% | 3 | 25 | 19 | 3 | 2 | 27 | 🚫 |  |
| 219 | architecture | `event-driven-patterns-interview` | 40 | 62%⚠ | 3 | 9 | 15 | 2 | 2 | 22 | ✅ | 8669087c |
| 220 | behavioral | `conflict-stories-interview` | 22 | 100%⚠ | 22 | 22 | 21 | 5 | 22 | 22 | ⏳ |  |
| 221 | behavioral | `culture-fit-interview` | 22 | 100%⚠ | 22 | 22 | 17 | 5 | 22 | 22 | ⏳ |  |
| 222 | behavioral | `failure-stories-interview` | 22 | 100%⚠ | 21 | 22 | 14 | 1 | 18 | 22 | ⏳ |  |
| 223 | behavioral | `leadership-stories-interview` | 22 | 100%⚠ | 22 | 22 | 20 | 9 | 22 | 22 | ⏳ |  |
| 224 | behavioral | `star-method-interview` | 22 | 100%⚠ | 22 | 22 | 19 | 12 | 22 | 22 | ⏳ |  |
| 225 | devops | `linux-interview` | 33 | 73%⚠ | · | 2 | 21 | 4 | · | 22 | ✅ | 4218b3af |
| 226 | frameworks | `spring-aop-interview` | 22 | 100%⚠ | 22 | 22 | 19 | 10 | 22 | 22 | ⏳ |  |
| 227 | leadership | `technical-decisions-interview` | 22 | 100%⚠ | 22 | 22 | 20 | 6 | 22 | 22 | ⏳ |  |
| 228 | messaging | `aws-sqs-sns-interview` | 22 | 100%⚠ | 22 | 22 | 17 | 9 | 22 | 22 | ⏳ |  |
| 229 | messaging | `pulsar-interview` | 22 | 100%⚠ | 22 | 22 | 15 | 11 | 22 | 22 | ⏳ |  |
| 230 | security | `secrets-management-interview` | 22 | 100%⚠ | 22 | 22 | 21 | 17 | 22 | 22 | ⏳ |  |
| 231 | testing | `load-testing-interview` | 22 | 100%⚠ | 22 | 22 | 20 | 16 | 22 | 22 | ⏳ |  |
| 232 | ai-ml | `code-agents-interview` | 31 | 58%⚠ | 2 | 7 | 14 | 5 | 3 | 21 | ✅ | 7a901ca1 |
| 233 | system-design | `design-chat-system-interview` | 21 | 100%⚠ | 21 | 21 | 21 | 7 | 21 | 21 | ⏳ |  |
| 234 | testing | `property-based-testing-interview` | 21 | 100%⚠ | 21 | 21 | 13 | 10 | 21 | 21 | ⏳ |  |
| 235 | api | `api-versioning-interview` | 20 | 100%⚠ | 20 | 20 | 18 | 9 | 20 | 20 | ⏳ |  |
| 236 | devops | `kubernetes-interview` | 45 | 56%⚠ | 3 | 6 | 15 | 1 | · | 20 | ⏳ |  |
| 237 | devops | `linkerd-interview` | 20 | 100%⚠ | 20 | 20 | 16 | 11 | 20 | 20 | ⏳ |  |
| 238 | frameworks | `resilience4j-interview` | 20 | 100%⚠ | 20 | 20 | 20 | 3 | 20 | 20 | ⏳ |  |
| 239 | leadership | `conflict-resolution-interview` | 20 | 100%⚠ | 20 | 20 | 9 | 16 | 20 | 20 | ⏳ |  |
| 240 | leadership | `estimations-planning-interview` | 20 | 100%⚠ | 20 | 20 | 15 | 16 | 20 | 20 | ⏳ |  |
| 241 | monitoring | `micrometer-interview` | 20 | 100%⚠ | 20 | 20 | 17 | 17 | 20 | 20 | ⏳ |  |
| 242 | security | `mtls-interview` | 20 | 100%⚠ | 20 | 20 | 20 | 15 | 20 | 20 | ⏳ |  |
| 243 | testing | `mutation-testing-interview` | 20 | 100%⚠ | 20 | 20 | 15 | 8 | 20 | 20 | ⏳ |  |
| 244 | leadership | `tech-interviewing-interview` | 20 | 100%⚠ | 19 | 19 | 3 | 2 | 18 | 19 | ⏳ |  |
| 245 | security | `zero-trust-interview` | 19 | 100%⚠ | 19 | 19 | 17 | 14 | 19 | 19 | ⏳ |  |
| 246 | architecture | `edge-computing-interview` | 18 | 100%⚠ | 18 | 18 | 17 | 17 | 18 | 18 | ⏳ |  |
| 247 | architecture | `strangler-fig-interview` | 18 | 100%⚠ | 18 | 18 | 11 | 1 | 13 | 18 | ⏳ |  |
| 248 | frameworks | `spring-cache-interview` | 18 | 100%⚠ | 18 | 17 | 11 | 3 | 15 | 18 | ⏳ |  |
| 249 | programming-languages | `java-string-interview` | 39 | 69%⚠ | 11 | 11 | 14 | 2 | 7 | 18 | ⏳ |  |
| 250 | api | `rest-maturity-interview` | 17 | 100%⚠ | 17 | 17 | 13 | 11 | 17 | 17 | ⏳ |  |
| 251 | architecture | `bff-pattern-interview` | 17 | 100%⚠ | 17 | 17 | 17 | 15 | 17 | 17 | ⏳ |  |
| 252 | frameworks | `spring-retry-interview` | 17 | 100%⚠ | 17 | 17 | 12 | 9 | 17 | 17 | ⏳ |  |
| 253 | messaging | `redpanda-interview` | 20 | 90%⚠ | 16 | 16 | 4 | 3 | 14 | 17 | ⏳ |  |
| 254 | programming-languages | `kotlin-flow-interview` | 17 | 100%⚠ | 17 | 17 | 11 | 2 | 17 | 17 | ⏳ |  |
| 255 | system-design | `design-uber-interview` | 30 | 37% | · | 1 | 19 | 5 | · | 22 | ⏳ |  |
| 256 | algorithms | `algorithms-interview` | 16 | 100%⚠ | 16 | 16 | 11 | 13 | 16 | 16 | ⏳ |  |
| 257 | frameworks | `vertx-interview` | 31 | 52%⚠ | 1 | 2 | 15 | 3 | · | 16 | ✅ | d46ca3ca |
| 258 | frameworks | `spring-ai-interview` | 18 | 89%⚠ | 15 | 15 | 9 | 6 | 15 | 16 | ⏳ |  |
| 259 | frameworks | `spring-data-jdbc-interview` | 16 | 100%⚠ | 16 | 16 | 15 | 7 | 16 | 16 | ⏳ |  |
| 260 | frameworks | `spring-events-interview` | 16 | 100%⚠ | 16 | 16 | 10 | 2 | 16 | 16 | ⏳ |  |
| 261 | frameworks | `spring-validation-interview` | 16 | 100%⚠ | 16 | 16 | 11 | 5 | 15 | 16 | ⏳ |  |
| 262 | programming-languages | `java-reflection-interview` | 16 | 100%⚠ | 16 | 16 | 10 | 4 | 16 | 16 | ⏳ |  |
| 263 | system-design | `design-typeahead-interview` | 30 | 27% | 1 | 8 | 18 | 2 | 2 | 21 | ✅ | f576dd94 |
| 264 | cloud | `cloud-native-patterns-interview` | 30 | 53%⚠ | · | 1 | 13 | 2 | · | 15 | ✅ | d8f91752 |
| 265 | code-quality | `refactoring-patterns-interview` | 42 | 36% | · | · | 20 | · | · | 20 | ✅ | ca05053c |
| 266 | databases | `hibernate-caching-interview` | 15 | 100%⚠ | 15 | 15 | 10 | 6 | 15 | 15 | ⏳ |  |
| 267 | databases | `hibernate-relationships-interview` | 15 | 100%⚠ | 15 | 15 | 9 | 4 | 15 | 15 | ⏳ |  |
| 268 | frameworks | `spring-async-interview` | 15 | 100%⚠ | 14 | 14 | 10 | · | 12 | 15 | ⏳ |  |
| 269 | frameworks | `spring-boot-3-migration-interview` | 15 | 100%⚠ | 15 | 15 | 9 | 6 | 15 | 15 | ⏳ |  |
| 270 | frameworks | `spring-graphql-interview` | 15 | 100%⚠ | 15 | 15 | 12 | 1 | 14 | 15 | ⏳ |  |
| 271 | frameworks | `spring-integration-interview` | 15 | 100%⚠ | 15 | 15 | 15 | 3 | 15 | 15 | ⏳ |  |
| 272 | frameworks | `spring-kafka-interview` | 15 | 100%⚠ | 15 | 15 | 14 | 2 | 15 | 15 | ⏳ |  |
| 273 | frameworks | `spring-messaging-interview` | 15 | 100%⚠ | 15 | 15 | 11 | 6 | 15 | 15 | ⏳ |  |
| 274 | frameworks | `spring-modulith-interview` | 15 | 100%⚠ | 15 | 14 | 9 | 1 | 14 | 15 | ⏳ |  |
| 275 | frameworks | `spring-r2dbc-interview` | 15 | 100%⚠ | 15 | 15 | 8 | 2 | 15 | 15 | ⏳ |  |
| 276 | frameworks | `spring-session-interview` | 15 | 100%⚠ | 15 | 15 | 9 | 1 | 15 | 15 | ⏳ |  |
| 277 | frameworks | `spring-state-machine-interview` | 15 | 100%⚠ | 15 | 15 | 11 | 5 | 15 | 15 | ⏳ |  |
| 278 | frameworks | `spring-testing-interview` | 16 | 94%⚠ | 15 | 15 | 11 | 7 | 15 | 15 | ⏳ |  |
| 279 | frameworks | `spring-transaction-interview` | 15 | 100%⚠ | 15 | 15 | 14 | 1 | 15 | 15 | ⏳ |  |
| 280 | frameworks | `spring-vault-interview` | 15 | 100%⚠ | 15 | 15 | 14 | 4 | 15 | 15 | ⏳ |  |
| 281 | jvm | `graalvm-native-interview` | 15 | 100%⚠ | 15 | 15 | 11 | 4 | 15 | 15 | ⏳ |  |
| 282 | programming-languages | `java-optional-interview` | 15 | 100%⚠ | 15 | 15 | 10 | 4 | 15 | 15 | ⏳ |  |
| 283 | programming-languages | `java-pattern-matching-interview` | 15 | 100%⚠ | 15 | 14 | 7 | 2 | 13 | 15 | ⏳ |  |
| 284 | programming-languages | `java-records-interview` | 15 | 100%⚠ | 15 | 15 | 9 | 4 | 15 | 15 | ⏳ |  |
| 285 | programming-languages | `java-virtual-threads-interview` | 15 | 100%⚠ | 15 | 15 | 13 | 3 | 15 | 15 | ⏳ |  |
| 286 | programming-languages | `kotlin-sealed-classes-interview` | 15 | 100%⚠ | 15 | 14 | 10 | 1 | 14 | 15 | ⏳ |  |
| 287 | testing | `junit-interview` | 15 | 100%⚠ | 15 | 15 | 9 | 3 | 14 | 15 | ⏳ |  |
| 288 | testing | `rest-assured-interview` | 15 | 100%⚠ | 15 | 15 | 8 | 7 | 14 | 15 | ⏳ |  |
| 289 | testing | `selenium-interview` | 15 | 100%⚠ | 15 | 15 | 14 | 7 | 15 | 15 | ⏳ |  |
| 290 | cloud | `aws-interview` | 34 | 9% | · | 3 | 13 | 4 | · | 19 | ✅ | b599c7b7 |
| 291 | frameworks | `spring-scheduling-interview` | 16 | 100%⚠ | 13 | 11 | 11 | · | 4 | 14 | ⏳ |  |
| 292 | programming-languages | `java-functional-interface-interview` | 14 | 100%⚠ | 14 | 14 | 11 | 6 | 14 | 14 | ⏳ |  |
| 293 | ai-ml | `rag-interview` | 30 | 43% | · | 1 | 14 | 5 | · | 18 | ✅ | 4f89aefc |
| 294 | cloud | `azure-interview` | 25 | 40% | 1 | 5 | 18 | 4 | · | 18 | ✅ | 1e42aa0c |
| 295 | data-engineering | `data-warehousing-interview` | 30 | 33% | · | · | 18 | 1 | · | 18 | ✅ | fe8d9137 |
| 296 | databases | `hibernate-jpql-criteria-interview` | 16 | 88%⚠ | 11 | 11 | 7 | · | 4 | 13 | ⏳ |  |
| 297 | frameworks | `spring-rest-client-interview` | 13 | 100%⚠ | 13 | 13 | 6 | 2 | 12 | 13 | ⏳ |  |
| 298 | programming-languages | `kotlin-value-classes-interview` | 15 | 93%⚠ | 13 | 13 | 7 | 1 | 10 | 13 | ⏳ |  |
| 299 | system-design | `design-ecommerce-delivery-interview` | 36 | 78%⚠ | · | · | 12 | 2 | · | 13 | ⏳ |  |
| 300 | architecture | `reverse-proxy-interview` | 30 | 47% | · | · | 16 | 2 | · | 17 | ✅ | 0133d253 |
| 301 | cloud | `aws-lambda-interview` | 32 | 38% | · | 1 | 12 | 4 | · | 17 | ✅ | 34b3e769 |
| 302 | monitoring | `loki-grafana-interview` | 28 | 43% | · | · | 16 | 3 | · | 17 | ✅ | 76135d34 |
| 303 | programming-languages | `kotlin-spring-interview` | 15 | 100%⚠ | 11 | 12 | 3 | 2 | 9 | 12 | ⏳ |  |
| 304 | cloud | `gcp-interview` | 28 | 36% | · | 3 | 14 | · | · | 16 | ✅ | 8b68c521 |
| 305 | messaging | `kafka-interview` | 50 | 70%⚠ | 8 | 5 | · | · | 1 | 9 | ⏳ |  |
| 306 | programming-languages | `java-completable-future-interview` | 15 | 80%⚠ | 5 | 3 | 1 | · | · | 7 | ⏳ |  |
| 307 | programming-languages | `java-22-25-interview` | 22 | 45% | · | · | 1 | 1 | · | 2 | ⏳ |  |
| 308 | ai-ml | `langchain4j-interview` | 18 | 28% | 1 | · | · | · | · | 1 | ⏳ |  |
| 309 | databases | `jooq-interview` | 18 | 33% | 1 | 1 | · | · | 1 | 1 | ⏳ |  |
| 310 | jvm | `crac-interview` | 16 | 38% | · | · | 1 | · | · | 1 | ⏳ |  |
| 311 | testing | `archunit-interview` | 15 | 20% | 1 | 1 | · | · | · | 1 | ⏳ |  |
| 312 | leadership | `mentoring-interview` | 25 | 40% | · | · | · | · | · | 0 | ✅ | fe1dbbf5 |
| 313 | messaging | `apache-camel-interview` | 16 | 12% | · | · | · | · | · | 0 | ⏳ |  |
| 314 | messaging | `jms-activemq-interview` | 16 | 19% | · | · | · | · | · | 0 | ⏳ |  |
| 315 | performance | `jmh-microbenchmarking-interview` | 18 | 22% | · | · | · | · | · | 0 | ⏳ |  |
| 316 | programming-languages | `kotlin-testing-interview` | 20 | 35% | · | · | · | · | · | 0 | ⏳ |  |
| 317 | programming-languages | `scala-effects-interview` | 16 | 25% | · | · | · | · | · | 0 | ⏳ |  |
| 318 | testing | `cucumber-bdd-interview` | 15 | 33% | · | · | · | · | · | 0 | ⏳ |  |

**Итоги по пунктам (блоков-триггеров во всём корпусе):** LEN_AVG=8301, LEN_SPREAD=8335, UNIQ_MARKER=6902, COMMA_GAP=3606, SHORT_DISTR=7986. Файлов с ⚠CORRECT_LONGEST_RATE: **294/318**. Закоммичено по Option Parity: **29**.

## 8. Прогресс (2026-06-30)

- ✅ Option Parity закреплён в 3 skills (+ structural/cross-block augmentation).
- ✅ Аудит-скрипт `scripts/audit-mcq-parity.py` создан, прогнан по корпусу (318 сидеров).
- ✅ Закоммичено по Option Parity: 27 сидеров (см. колонку Commit). slice-4: `azure` (1e42aa0c, +восстановлен Q14-факт), `code-agents` (7a901ca1).
- ✅ `leadership/mentoring-interview` (negative example) — **исправлен** (fe1dbbf5): все 25 блоков, дистракторы подняты до паритета, заглушки убраны, correct-longest 96%→40%, audit 0 флагов, ложность дистракторов проверена по .md.
- ⏳ reviewer-less, gate-clean: `ai-ml/rag` (43%), `system-design/design-uber` (37%), `devops/kubernetes` (56%).
- ⏳ untouched, re-queue: `model-serving`, `clickhouse`.

**Важно (честно):** дефект корпус-wide — **294/318** сидеров имеют ⚠CORRECT_LONGEST_RATE.
Даже ранее закоммиченные (`java-concurrency` 89%, `spring-boot`) чинились по СТАРОЙ линзе
(per-block length-vs-longest-distractor) и сохраняют structural/cross-block tell. «Все сидеры
исправлены» — цель бесконечного цикла, не одного тика. Worst-first по таблице §7.

## 9. Координация

Параллельная claude-сессия работает worst-first СВЕРХУ (frontier ranks 12–14, static:
`agentic-patterns`/`git`/`chaos-engineering`). Я беру нижний слой; перед каждым слайсом —
`git status seed/mcq` → skip их dirty-файлов.

