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

Всего сидеров: **318**. Источник метрик: `scripts/audit-mcq-parity.py` (2026-06-30).

| # | Категория | Сидер | Блоков | Флагов | correct-longest | Статус | Commit |
|---|---|---|---:|---:|---:|---|---|
| 1 | databases | `databases/postgresql-interview` | 55 | 55 | 100% ⚠ | ⏳ queued |  |
| 2 | testing | `testing/test-automation-interview` | 50 | 50 | 100% ⚠ | ⏳ queued |  |
| 3 | reactive | `reactive/project-reactor-interview` | 48 | 47 | 98% ⚠ | ⏳ queued |  |
| 4 | databases | `databases/mongodb-interview` | 46 | 46 | 100% ⚠ | ⏳ queued |  |
| 5 | programming-languages | `programming-languages/java/java-collections-interview` | 46 | 46 | 100% ⚠ | ⏳ queued |  |
| 6 | programming-languages | `programming-languages/java/java-concurrency-interview` | 56 | 46 | 89% ⚠ | ✅ committed | 7c013c0d |
| 7 | reactive | `reactive/rxjava-interview` | 46 | 46 | 100% ⚠ | ⏳ queued |  |
| 8 | architecture | `architecture/hexagonal-architecture-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 9 | programming-languages | `programming-languages/kotlin/kotlin-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 10 | security | `security/application-security-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 11 | security | `security/authentication-authorization-patterns-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 12 | security | `security/owasp-top10-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 13 | security | `security/tls-ssl-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 14 | testing | `testing/mockito-interview` | 45 | 45 | 89% ⚠ | ⏳ queued |  |
| 15 | testing | `testing/test-strategies-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 16 | testing | `testing/unit-testing-interview` | 45 | 45 | 100% ⚠ | ⏳ queued |  |
| 17 | databases | `databases/cassandra-interview` | 44 | 44 | 100% ⚠ | ⏳ queued |  |
| 18 | databases | `databases/elasticsearch-interview` | 44 | 44 | 100% ⚠ | ⏳ queued |  |
| 19 | design-patterns | `design-patterns/design-patterns-interview` | 48 | 44 | 100% ⚠ | ⏳ queued |  |
| 20 | architecture | `architecture/networking-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 21 | architecture | `architecture/resilience-patterns-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 22 | architecture | `architecture/saga-pattern-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 23 | databases | `databases/hibernate-interview` | 50 | 43 | 92% ⚠ | ⏳ queued |  |
| 24 | databases | `databases/redis-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 25 | devops | `devops/helm-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 26 | frameworks | `frameworks/spring/spring-batch-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 27 | frameworks | `frameworks/spring/spring-boot-actuator-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 28 | frameworks | `frameworks/spring/spring-cloud-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 29 | frameworks | `frameworks/spring/spring-mvc-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 30 | frameworks | `frameworks/spring/spring-security-interview` | 46 | 43 | 96% ⚠ | ⏳ queued |  |
| 31 | frameworks | `frameworks/spring/spring-webflux-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 32 | programming-languages | `programming-languages/java/java-oop-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 33 | programming-languages | `programming-languages/kotlin/kotlin-collections-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 34 | programming-languages | `programming-languages/kotlin/kotlin-serialization-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 35 | security | `security/jwt-interview` | 43 | 43 | 100% ⚠ | ⏳ queued |  |
| 36 | api | `api/openapi-swagger-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 37 | architecture | `architecture/caching-strategies-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 38 | architecture | `architecture/cap-theorem-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 39 | architecture | `architecture/consistency-patterns-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 40 | architecture | `architecture/microservices-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 41 | databases | `databases/database-transactions-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 42 | databases | `databases/flyway-liquibase-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 43 | devops | `devops/argocd-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 44 | devops | `devops/terraform-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 45 | frameworks | `frameworks/spring/spring-data-jpa-interview` | 43 | 42 | 98% ⚠ | ⏳ queued |  |
| 46 | performance | `performance/application-profiling-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 47 | performance | `performance/performance-testing-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 48 | programming-languages | `programming-languages/java/java-17-21-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 49 | programming-languages | `programming-languages/java/java-8-interview` | 42 | 42 | 98% ⚠ | ⏳ queued |  |
| 50 | programming-languages | `programming-languages/java/java-exceptions-interview` | 42 | 42 | 98% ⚠ | ⏳ queued |  |
| 51 | programming-languages | `programming-languages/java/java-stream-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 52 | security | `security/oauth2-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 53 | testing | `testing/contract-testing-interview` | 42 | 42 | 100% ⚠ | ⏳ queued |  |
| 54 | architecture | `architecture/clean-architecture-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 55 | architecture | `architecture/cqrs-event-sourcing-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 56 | architecture | `architecture/scalability-patterns-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 57 | databases | `databases/database-architecture-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 58 | devops | `devops/docker-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 59 | logging | `logging/logging-interview` | 42 | 41 | 98% ⚠ | ⏳ queued |  |
| 60 | monitoring | `monitoring/metrics-tracing-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 61 | system-design | `system-design/system-design-interview` | 41 | 41 | 100% ⚠ | ⏳ queued |  |
| 62 | architecture | `architecture/distributed-systems-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 63 | architecture | `architecture/load-balancing-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 64 | code-quality | `code-quality/technical-debt-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 65 | frameworks | `frameworks/spring/spring-framework-interview` | 41 | 40 | 98% ⚠ | ⏳ queued |  |
| 66 | leadership | `leadership/code-review-practices-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 67 | leadership | `leadership/team-leadership-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 68 | monitoring | `monitoring/observability-interview` | 42 | 40 | 95% ⚠ | ⏳ queued |  |
| 69 | programming-languages | `programming-languages/java/java-annotations-interview` | 43 | 40 | 98% ⚠ | ⏳ queued |  |
| 70 | programming-languages | `programming-languages/java/java-generics-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 71 | programming-languages | `programming-languages/java/java-io-nio-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 72 | programming-languages | `programming-languages/java/java-serialization-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 73 | programming-languages | `programming-languages/kotlin/kotlin-dsl-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 74 | programming-languages | `programming-languages/kotlin/kotlin-exceptions-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 75 | programming-languages | `programming-languages/scala/scala-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 76 | testing | `testing/integration-testing-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 77 | testing | `testing/testcontainers-interview` | 40 | 40 | 100% ⚠ | ⏳ queued |  |
| 78 | api | `api/grpc-interview` | 40 | 39 | 100% ⚠ | ⏳ queued |  |
| 79 | cicd | `cicd/deployment-strategies-interview` | 39 | 39 | 100% ⚠ | ⏳ queued |  |
| 80 | performance | `performance/memory-management-interview` | 39 | 39 | 100% ⚠ | ⏳ queued |  |
| 81 | programming-languages | `programming-languages/java/java-core-interview` | 39 | 39 | 100% ⚠ | ⏳ queued |  |
| 82 | programming-languages | `programming-languages/kotlin/kotlin-coroutines-interview` | 39 | 39 | 100% ⚠ | ⏳ queued |  |
| 83 | api | `api/websocket-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 84 | architecture | `architecture/api-gateway-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 85 | cicd | `cicd/pipeline-design-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 86 | databases | `databases/sql-interview` | 53 | 38 | 91% ⚠ | ⏳ queued |  |
| 87 | devops | `devops/gradle-maven-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 88 | monitoring | `monitoring/logging-strategies-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 89 | monitoring | `monitoring/prometheus-grafana-interview` | 39 | 38 | 97% ⚠ | ⏳ queued |  |
| 90 | performance | `performance/jvm-performance-tuning-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 91 | programming-languages | `programming-languages/java/java-modules-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 92 | programming-languages | `programming-languages/java/java-types-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 93 | programming-languages | `programming-languages/kotlin/kotlin-interop-java-interview` | 38 | 38 | 100% ⚠ | ⏳ queued |  |
| 94 | api | `api/graphql-interview` | 40 | 37 | 95% ⚠ | ⏳ queued |  |
| 95 | api | `api/http-rest-interview` | 43 | 37 | 95% ⚠ | ⏳ queued |  |
| 96 | devops | `devops/git-interview` | 43 | 37 | 91% ⚠ | 🚫 др.сессия |  |
| 97 | messaging | `messaging/rabbitmq-interview` | 41 | 37 | 100% ⚠ | ⏳ queued |  |
| 98 | ai-ml | `ai-ml/inference-optimization-interview` | 36 | 36 | 100% ⚠ | ⏳ queued |  |
| 99 | algorithms | `algorithms/data-structures/arrays-strings-interview` | 36 | 36 | 100% ⚠ | ⏳ queued |  |
| 100 | programming-languages | `programming-languages/go/go-interview` | 36 | 36 | 100% ⚠ | ⏳ queued |  |
| 101 | ai-ml | `ai-ml/fine-tuning-llm-interview` | 35 | 35 | 100% ⚠ | ⏳ queued |  |
| 102 | data-engineering | `data-engineering/apache-spark-interview` | 35 | 35 | 100% ⚠ | ⏳ queued |  |
| 103 | programming-languages | `programming-languages/go/go-concurrency-interview` | 35 | 35 | 100% ⚠ | ⏳ queued |  |
| 104 | ai-ml | `ai-ml/open-source-llms-interview` | 34 | 34 | 100% ⚠ | ⏳ queued |  |
| 105 | algorithms | `algorithms/data-structures/hash-tables-interview` | 34 | 34 | 100% ⚠ | ⏳ queued |  |
| 106 | algorithms | `algorithms/data-structures/trees-interview` | 34 | 34 | 100% ⚠ | ⏳ queued |  |
| 107 | databases | `databases/database-sharding-interview` | 34 | 34 | 100% ⚠ | ⏳ queued |  |
| 108 | algorithms | `algorithms/algorithmic-paradigms/dynamic-programming-interview` | 33 | 33 | 100% ⚠ | ⏳ queued |  |
| 109 | ai-ml | `ai-ml/ai-application-architecture-interview` | 32 | 32 | 100% ⚠ | ⏳ queued |  |
| 110 | ai-ml | `ai-ml/ai-compliance-governance-interview` | 32 | 32 | 100% ⚠ | ⏳ queued |  |
| 111 | ai-ml | `ai-ml/ai-safety-guardrails-interview` | 32 | 32 | 100% ⚠ | ⏳ queued |  |
| 112 | frameworks | `frameworks/jvm-alternatives/ktor-interview` | 32 | 32 | 100% ⚠ | ⏳ queued |  |
| 113 | testing | `testing/chaos-engineering-interview` | 44 | 32 | 93% ⚠ | 🚫 др.сессия |  |
| 114 | ai-ml | `ai-ml/multimodal-ai-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 115 | algorithms | `algorithms/complexity/complexity-analysis-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 116 | algorithms | `algorithms/data-structures/linked-lists-interview` | 32 | 31 | 100% ⚠ | ⏳ queued |  |
| 117 | algorithms | `algorithms/sorting-searching/searching-algorithms-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 118 | algorithms | `algorithms/sorting-searching/sorting-algorithms-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 119 | behavioral | `behavioral/behavioral-interview` | 38 | 31 | 92% ⚠ | ⏳ queued |  |
| 120 | data-engineering | `data-engineering/apache-flink-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 121 | databases | `databases/database-replication-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 122 | frameworks | `frameworks/jvm-alternatives/quarkus-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 123 | programming-languages | `programming-languages/java/java-conditional-statements-interview` | 42 | 31 | 57% ⚠ | ⏳ queued |  |
| 124 | programming-languages | `programming-languages/java/java-jackson-interview` | 31 | 31 | 100% ⚠ | ⏳ queued |  |
| 125 | ai-ml | `ai-ml/llm-evaluation-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 126 | ai-ml | `ai-ml/long-context-vs-rag-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 127 | ai-ml | `ai-ml/mcp-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 128 | ai-ml | `ai-ml/multi-agent-orchestration-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 129 | ai-ml | `ai-ml/reasoning-models-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 130 | architecture | `architecture/service-discovery-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 131 | databases | `databases/dynamodb-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 132 | databases | `databases/neo4j-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 133 | programming-languages | `programming-languages/go/go-stdlib-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 134 | programming-languages | `programming-languages/typescript/typescript-interview` | 33 | 30 | 97% ⚠ | ⏳ queued |  |
| 135 | reactive | `reactive/reactive-streams-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 136 | system-design | `system-design/design-feed-system-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 137 | system-design | `system-design/design-key-value-store-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 138 | system-design | `system-design/design-netflix-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 139 | system-design | `system-design/design-pastebin-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 140 | system-design | `system-design/design-payment-system-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 141 | system-design | `system-design/design-rate-limiter-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 142 | system-design | `system-design/design-url-shortener-interview` | 30 | 30 | 100% ⚠ | ⏳ queued |  |
| 143 | ai-ml | `ai-ml/embeddings-interview` | 29 | 29 | 100% ⚠ | ⏳ queued |  |
| 144 | ai-ml | `ai-ml/llm-basics-interview` | 30 | 29 | 100% ⚠ | ⏳ queued |  |
| 145 | algorithms | `algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview` | 33 | 29 | 61% ⚠ | ✅ committed | 9b7f4fdf |
| 146 | algorithms | `algorithms/data-structures/graphs-interview` | 34 | 29 | 74% ⚠ | ✅ committed | dcc1af28 |
| 147 | algorithms | `algorithms/data-structures/heaps-interview` | 29 | 29 | 100% ⚠ | ⏳ queued |  |
| 148 | architecture | `architecture/cdn-interview` | 30 | 29 | 100% ⚠ | ⏳ queued |  |
| 149 | ai-ml | `ai-ml/ai-agents-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 150 | ai-ml | `ai-ml/ai-observability-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 151 | ai-ml | `ai-ml/llm-integration-patterns-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 152 | ai-ml | `ai-ml/mlops-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 153 | ai-ml | `ai-ml/model-serving-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 154 | ai-ml | `ai-ml/prompt-engineering-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 155 | ai-ml | `ai-ml/vector-databases-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 156 | algorithms | `algorithms/algorithmic-paradigms/greedy-algorithms-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 157 | algorithms | `algorithms/data-structures/tries-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 158 | api | `api/api-design-best-practices-interview` | 30 | 28 | 97% ⚠ | ⏳ queued |  |
| 159 | cloud | `cloud/serverless-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 160 | code-quality | `code-quality/code-review-interview` | 40 | 28 | 55% ⚠ | ✅ committed | b24efe14 |
| 161 | data-engineering | `data-engineering/data-lake-lakehouse-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 162 | data-engineering | `data-engineering/dbt-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 163 | data-engineering | `data-engineering/kafka-streams-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 164 | data-engineering | `data-engineering/stream-processing-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 165 | databases | `databases/clickhouse-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 166 | jvm | `jvm/jvm-interview` | 40 | 28 | 88% ⚠ | ⏳ queued |  |
| 167 | monitoring | `monitoring/opentelemetry-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 168 | programming-languages | `programming-languages/go/go-testing-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 169 | programming-languages | `programming-languages/java/java-mapstruct-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 170 | reactive | `reactive/reactive-testing-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 171 | reactive | `reactive/webflux-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 172 | system-design | `system-design/design-parking-lot-oo-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 173 | system-design | `system-design/design-youtube-interview` | 28 | 28 | 100% ⚠ | ⏳ queued |  |
| 174 | algorithms | `algorithms/algorithmic-paradigms/backtracking-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 175 | algorithms | `algorithms/algorithmic-paradigms/divide-and-conquer-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 176 | algorithms | `algorithms/algorithmic-paradigms/recursion-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 177 | code-quality | `code-quality/clean-code-practices-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 178 | code-quality | `code-quality/code-smells-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 179 | performance | `performance/database-performance-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 180 | programming-languages | `programming-languages/go/go-memory-gc-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 181 | programming-languages | `programming-languages/go/go-modules-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 182 | programming-languages | `programming-languages/java/java-initialization-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 183 | programming-languages | `programming-languages/java/java-lombok-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 184 | system-design | `system-design/design-instagram-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 185 | system-design | `system-design/design-twitter-interview` | 27 | 27 | 100% ⚠ | ⏳ queued |  |
| 186 | code-quality | `code-quality/static-analysis-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 187 | devops | `devops/istio-service-mesh-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 188 | devops | `devops/vault-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 189 | messaging | `messaging/message-brokers-comparison-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 190 | monitoring | `monitoring/elk-stack-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 191 | programming-languages | `programming-languages/go/go-generics-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 192 | reactive | `reactive/reactive-patterns-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 193 | system-design | `system-design/design-dropbox-interview` | 30 | 26 | 100% ⚠ | ✅ committed | ad3eb3bc |
| 194 | system-design | `system-design/design-elevator-oo-interview` | 26 | 26 | 100% ⚠ | ⏳ queued |  |
| 195 | ai-ml | `ai-ml/function-calling-interview` | 30 | 25 | 63% ⚠ | ✅ committed | cfe7b05a |
| 196 | algorithms | `algorithms/data-structures/stacks-queues-interview` | 25 | 25 | 100% ⚠ | ⏳ queued |  |
| 197 | architecture | `architecture/ddd-interview` | 38 | 25 | 76% ⚠ | ✅ committed | 2257299d |
| 198 | code-quality | `code-quality/code-coverage-interview` | 25 | 25 | 100% ⚠ | ⏳ queued |  |
| 199 | devops | `devops/ansible-interview` | 25 | 25 | 100% ⚠ | ⏳ queued |  |
| 200 | frameworks | `frameworks/jvm-alternatives/micronaut-interview` | 26 | 25 | 100% ⚠ | ⏳ queued |  |
| 201 | frameworks | `frameworks/spring/spring-boot-interview` | 43 | 25 | 51% ⚠ | ✅ committed | 9f65e55c |
| 202 | architecture | `architecture/latency-numbers-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 203 | databases | `databases/cockroachdb-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 204 | devops | `devops/consul-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 205 | messaging | `messaging/nats-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 206 | performance | `performance/network-performance-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 207 | programming-languages | `programming-languages/rust/rust-interview` | 33 | 24 | 70% ⚠ | ✅ committed | 28ba103b |
| 208 | security | `security/supply-chain-security-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 209 | system-design | `system-design/design-search-interview` | 30 | 24 | 73% ⚠ | ✅ committed | 61c76d03 |
| 210 | system-design | `system-design/design-vending-machine-oo-interview` | 24 | 24 | 100% ⚠ | ⏳ queued |  |
| 211 | architecture | `architecture/dns-interview` | 30 | 23 | 93% ⚠ | ✅ committed | 29ffdbbb |
| 212 | data-engineering | `data-engineering/apache-airflow-interview` | 28 | 23 | 82% ⚠ | ✅ committed | 93220751 |
| 213 | databases | `databases/scylladb-interview` | 23 | 23 | 100% ⚠ | ⏳ queued |  |
| 214 | monitoring | `monitoring/jaeger-zipkin-interview` | 23 | 23 | 100% ⚠ | ⏳ queued |  |
| 215 | performance | `performance/caching-performance-interview` | 23 | 23 | 100% ⚠ | ⏳ queued |  |
| 216 | system-design | `system-design/design-google-maps-interview` | 30 | 23 | 70% ⚠ | ✅ committed | fd6696d1 |
| 217 | system-design | `system-design/design-web-crawler-interview` | 26 | 23 | 92% ⚠ | ⏳ queued |  |
| 218 | ai-ml | `ai-ml/agentic-patterns-interview` | 30 | 27 | 20% | 🚫 др.сессия |  |
| 219 | architecture | `architecture/event-driven-patterns-interview` | 40 | 22 | 62% ⚠ | ✅ committed | 8669087c |
| 220 | behavioral | `behavioral/conflict-stories-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 221 | behavioral | `behavioral/culture-fit-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 222 | behavioral | `behavioral/failure-stories-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 223 | behavioral | `behavioral/leadership-stories-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 224 | behavioral | `behavioral/star-method-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 225 | devops | `devops/linux-interview` | 33 | 22 | 73% ⚠ | ✅ committed | 4218b3af |
| 226 | frameworks | `frameworks/spring/spring-aop-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 227 | leadership | `leadership/technical-decisions-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 228 | messaging | `messaging/aws-sqs-sns-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 229 | messaging | `messaging/pulsar-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 230 | security | `security/secrets-management-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 231 | testing | `testing/load-testing-interview` | 22 | 22 | 100% ⚠ | ⏳ queued |  |
| 232 | ai-ml | `ai-ml/code-agents-interview` | 31 | 21 | 58% ⚠ | ✅ committed | 7a901ca1 |
| 233 | system-design | `system-design/design-chat-system-interview` | 21 | 21 | 100% ⚠ | ⏳ queued |  |
| 234 | testing | `testing/property-based-testing-interview` | 21 | 21 | 100% ⚠ | ⏳ queued |  |
| 235 | api | `api/api-versioning-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 236 | devops | `devops/kubernetes-interview` | 45 | 20 | 56% ⚠ | ⏳ queued |  |
| 237 | devops | `devops/linkerd-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 238 | frameworks | `frameworks/spring/resilience4j-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 239 | leadership | `leadership/conflict-resolution-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 240 | leadership | `leadership/estimations-planning-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 241 | monitoring | `monitoring/micrometer-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 242 | security | `security/mtls-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 243 | testing | `testing/mutation-testing-interview` | 20 | 20 | 100% ⚠ | ⏳ queued |  |
| 244 | leadership | `leadership/tech-interviewing-interview` | 20 | 19 | 100% ⚠ | ⏳ queued |  |
| 245 | security | `security/zero-trust-interview` | 19 | 19 | 100% ⚠ | ⏳ queued |  |
| 246 | architecture | `architecture/edge-computing-interview` | 18 | 18 | 100% ⚠ | ⏳ queued |  |
| 247 | architecture | `architecture/strangler-fig-interview` | 18 | 18 | 100% ⚠ | ⏳ queued |  |
| 248 | frameworks | `frameworks/spring/spring-cache-interview` | 18 | 18 | 100% ⚠ | ⏳ queued |  |
| 249 | programming-languages | `programming-languages/java/java-string-interview` | 39 | 18 | 69% ⚠ | ⏳ queued |  |
| 250 | api | `api/rest-maturity-interview` | 17 | 17 | 100% ⚠ | ⏳ queued |  |
| 251 | architecture | `architecture/bff-pattern-interview` | 17 | 17 | 100% ⚠ | ⏳ queued |  |
| 252 | frameworks | `frameworks/spring/spring-retry-interview` | 17 | 17 | 100% ⚠ | ⏳ queued |  |
| 253 | messaging | `messaging/redpanda-interview` | 20 | 17 | 90% ⚠ | ⏳ queued |  |
| 254 | programming-languages | `programming-languages/kotlin/kotlin-flow-interview` | 17 | 17 | 100% ⚠ | ⏳ queued |  |
| 255 | system-design | `system-design/design-uber-interview` | 30 | 22 | 37% | ⏳ queued |  |
| 256 | algorithms | `algorithms/algorithms-interview` | 16 | 16 | 100% ⚠ | ⏳ queued |  |
| 257 | frameworks | `frameworks/jvm-alternatives/vertx-interview` | 31 | 16 | 52% ⚠ | ✅ committed | d46ca3ca |
| 258 | frameworks | `frameworks/spring/spring-ai-interview` | 18 | 16 | 89% ⚠ | ⏳ queued |  |
| 259 | frameworks | `frameworks/spring/spring-data-jdbc-interview` | 16 | 16 | 100% ⚠ | ⏳ queued |  |
| 260 | frameworks | `frameworks/spring/spring-events-interview` | 16 | 16 | 100% ⚠ | ⏳ queued |  |
| 261 | frameworks | `frameworks/spring/spring-validation-interview` | 16 | 16 | 100% ⚠ | ⏳ queued |  |
| 262 | programming-languages | `programming-languages/java/java-reflection-interview` | 16 | 16 | 100% ⚠ | ⏳ queued |  |
| 263 | system-design | `system-design/design-typeahead-interview` | 30 | 21 | 27% | ✅ committed | f576dd94 |
| 264 | cloud | `cloud/cloud-native-patterns-interview` | 30 | 15 | 53% ⚠ | ✅ committed | d8f91752 |
| 265 | code-quality | `code-quality/refactoring-patterns-interview` | 42 | 20 | 36% | ✅ committed | ca05053c |
| 266 | databases | `databases/hibernate-caching-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 267 | databases | `databases/hibernate-relationships-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 268 | frameworks | `frameworks/spring/spring-async-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 269 | frameworks | `frameworks/spring/spring-boot-3-migration-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 270 | frameworks | `frameworks/spring/spring-graphql-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 271 | frameworks | `frameworks/spring/spring-integration-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 272 | frameworks | `frameworks/spring/spring-kafka-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 273 | frameworks | `frameworks/spring/spring-messaging-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 274 | frameworks | `frameworks/spring/spring-modulith-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 275 | frameworks | `frameworks/spring/spring-r2dbc-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 276 | frameworks | `frameworks/spring/spring-session-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 277 | frameworks | `frameworks/spring/spring-state-machine-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 278 | frameworks | `frameworks/spring/spring-testing-interview` | 16 | 15 | 94% ⚠ | ⏳ queued |  |
| 279 | frameworks | `frameworks/spring/spring-transaction-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 280 | frameworks | `frameworks/spring/spring-vault-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 281 | jvm | `jvm/graalvm-native-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 282 | programming-languages | `programming-languages/java/java-optional-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 283 | programming-languages | `programming-languages/java/java-pattern-matching-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 284 | programming-languages | `programming-languages/java/java-records-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 285 | programming-languages | `programming-languages/java/java-virtual-threads-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 286 | programming-languages | `programming-languages/kotlin/kotlin-sealed-classes-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 287 | testing | `testing/junit-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 288 | testing | `testing/rest-assured-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 289 | testing | `testing/selenium-interview` | 15 | 15 | 100% ⚠ | ⏳ queued |  |
| 290 | cloud | `cloud/aws-interview` | 34 | 19 | 9% | ✅ committed | b599c7b7 |
| 291 | frameworks | `frameworks/spring/spring-scheduling-interview` | 16 | 14 | 100% ⚠ | ⏳ queued |  |
| 292 | programming-languages | `programming-languages/java/java-functional-interface-interview` | 14 | 14 | 100% ⚠ | ⏳ queued |  |
| 293 | ai-ml | `ai-ml/rag-interview` | 30 | 18 | 43% | ⏳ queued |  |
| 294 | cloud | `cloud/azure-interview` | 25 | 18 | 40% | ✅ committed | 1e42aa0c |
| 295 | data-engineering | `data-engineering/data-warehousing-interview` | 30 | 18 | 33% | ✅ committed | fe8d9137 |
| 296 | databases | `databases/hibernate-jpql-criteria-interview` | 16 | 13 | 88% ⚠ | ⏳ queued |  |
| 297 | frameworks | `frameworks/spring/spring-rest-client-interview` | 13 | 13 | 100% ⚠ | ⏳ queued |  |
| 298 | programming-languages | `programming-languages/kotlin/kotlin-value-classes-interview` | 15 | 13 | 93% ⚠ | ⏳ queued |  |
| 299 | system-design | `system-design/design-ecommerce-delivery-interview` | 36 | 13 | 78% ⚠ | ⏳ queued |  |
| 300 | architecture | `architecture/reverse-proxy-interview` | 30 | 17 | 47% | ✅ committed | 0133d253 |
| 301 | cloud | `cloud/aws-lambda-interview` | 32 | 17 | 38% | ✅ committed | 34b3e769 |
| 302 | monitoring | `monitoring/loki-grafana-interview` | 28 | 17 | 43% | ✅ committed | 76135d34 |
| 303 | programming-languages | `programming-languages/kotlin/kotlin-spring-interview` | 15 | 12 | 100% ⚠ | ⏳ queued |  |
| 304 | cloud | `cloud/gcp-interview` | 28 | 16 | 36% | ✅ committed | 8b68c521 |
| 305 | messaging | `messaging/kafka-interview` | 50 | 9 | 70% ⚠ | ⏳ queued |  |
| 306 | programming-languages | `programming-languages/java/java-completable-future-interview` | 15 | 7 | 80% ⚠ | ⏳ queued |  |
| 307 | programming-languages | `programming-languages/java/java-22-25-interview` | 22 | 2 | 45% | ⏳ queued |  |
| 308 | ai-ml | `ai-ml/langchain4j-interview` | 18 | 1 | 28% | ⏳ queued |  |
| 309 | databases | `databases/jooq-interview` | 18 | 1 | 33% | ⏳ queued |  |
| 310 | jvm | `jvm/crac-interview` | 16 | 1 | 38% | ⏳ queued |  |
| 311 | testing | `testing/archunit-interview` | 15 | 1 | 20% | ⏳ queued |  |
| 312 | leadership | `leadership/mentoring-interview` | 25 | 0 | 40% | ⏳ queued |  |
| 313 | messaging | `messaging/apache-camel-interview` | 16 | 0 | 12% | ⏳ queued |  |
| 314 | messaging | `messaging/jms-activemq-interview` | 16 | 0 | 19% | ⏳ queued |  |
| 315 | performance | `performance/jmh-microbenchmarking-interview` | 18 | 0 | 22% | ⏳ queued |  |
| 316 | programming-languages | `programming-languages/kotlin/kotlin-testing-interview` | 20 | 0 | 35% | ⏳ queued |  |
| 317 | programming-languages | `programming-languages/scala/scala-effects-interview` | 16 | 0 | 25% | ⏳ queued |  |
| 318 | testing | `testing/cucumber-bdd-interview` | 15 | 0 | 33% | ⏳ queued |  |

**Итоги аудита:** файлов с ⚠CORRECT_LONGEST_RATE (>50% блоков correct-longest): **294/318**; суммарно флагов блоков: **9008**; закоммичено по Option Parity: **27**.

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

