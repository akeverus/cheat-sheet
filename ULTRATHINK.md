---
title: "ULTRATHINK v4 — пофайловый audit-план (прогресс сброшен)"
description: "Полная таблица всех 304 interview-файлов. Прогресс сброшен — каждый файл должен быть проверен с нуля по всем критериям A-H."
updated: "2026-05-27"
status: "active"
audit_version: 4
progress: "RESET — 0/304"
---

# ULTRATHINK v4 — пофайловый audit-план

**Прогресс сброшен.** Все 304 файла помечены как `⬜ TODO`. Каждый файл должен пройти полный checklist A-H **с нуля** в текущей сессии работы, независимо от предыдущего состояния.

## Критерии проверки каждого файла (CHECKLIST A-H)

Каждый файл должен пройти ВСЕ 8 критериев. Только после этого его статус меняется на `✅ DONE`.

### A. Frontmatter (6 обязательных полей)

- [ ] `title` — `"Вопросы на собеседовании: <Topic>"`.
- [ ] `description` — одна строка summary.
- [ ] `tags` — YAML list с `interview`, `<category>`, `<topic>`.
- [ ] `aliases` — массив ≥ 3 терминов (англ. + рус.).
- [ ] `difficulty` — `intermediate` или `advanced`.
- [ ] `updated` — YYYY-MM-DD, current date.

### B. Структура файла

- [ ] `## Полезные ссылки` сразу после intro.
- [ ] `## Содержание` — TOC с anchor-ссылками `#qN-text` на каждый Q.
- [ ] Заголовки вопросов: `## Q<N>. <text>?` (точка после номера; `(!)` для важных).
- [ ] Q-нумерация без gaps (1, 2, 3, ... без пропусков).
- [ ] `## See also` — standalone top-level section в конце файла.
- [ ] ≥ 5 markdown-ссылок в See also (relative paths, `.md` extension).

### C. Content quality (человекочитаемость) — критично

- [ ] Только **русский** для пояснений; англ. только в `backticks` (термины), коде, путях, именах продуктов.
- [ ] **Короткие абзацы** (2-4 предложения максимум).
- [ ] **Bullet-списки** вместо стен текста (для 3+ пунктов).
- [ ] **Таблицы** для `vs` сравнений (с явными колонками).
- [ ] **Код в блоках** с правильным language tag (` ```java `, ` ```sql `, ` ```yaml `).
- [ ] **Mermaid диаграммы** для архитектуры / flow (1-3 на файл).
- [ ] **Жирное** для ключевых выводов (1-2 раза на абзац, не больше).
- [ ] **Примеры** в каждом концептуальном ответе (код / сценарий / цифры).
- [ ] Без воды: нет «важно отметить что», «стоит упомянуть», «необходимо понимать».
- [ ] Без маркетинга: нет «мощный инструмент», «гибкое решение».
- [ ] Без академического тона: пишем как инженер инженеру.

### D. Чистота markdown

- [ ] **НЕТ** `> [!mcq]` callout-блоков (запрещены с 2026-05-20, ломают pre-commit).
- [ ] **НЕТ** Tier 1 эмодзи-маркеров: `❌ ПОСЛЕДСТВИЕ:`, `✓ ПРИМЕНЯТЬ:`, `📋 ПРАВИЛО:`, `🔗 См. Q<N>`.
- [ ] **НЕТ** `[[wikilinks]]` — только markdown `[text](path.md)`.
- [ ] `bash scripts/verify-md-no-mcq.sh <file>` → exit 0 (`OK`).

### E. Q count и важность

- [ ] **Минимум 20 Q** для базовой темы.
- [ ] **25-30 Q** для широких тем (Spring, Java, K8s, AI/ML).
- [ ] **30+ Q** для system-design кейсов.
- [ ] 20-30% Q помечены `(!)` как важные.

### F. Парный JSON-сидер (MCQ)

- [ ] Файл существует: `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json`.
- [ ] Q count в JSON == Q count в MD (no mismatch).
- [ ] Schema валидирована: `jsonschema --spec=draft7` → 0 errors.
- [ ] Структура: `topic_slug`, `questions[]`, `blocks[]`, `options[]` (ровно 4).
- [ ] Каждый block: 1 correct + 3 wrong.
- [ ] Rotation позиций A/B/C/D балансированный (max/min ≤ 2.0).

### G. MCQ content (человекочитаемость JSON)

- [ ] **Correct sections** (5 ключей): `explanation`, `example`, `when_to_apply`, `edge_cases`, `related`.
- [ ] **Wrong sections** (4 ключа): `what_actually`, `source_of_confusion`, `if_it_were_true`, `how_it_should_be`.
- [ ] Все секции на русском, термины в `backticks`.
- [ ] `text` опций — 1-3 предложения, info-equivalent (max/min length ratio ≤ 1.4).
- [ ] **Distractors правдоподобные** — middle-разработчик мог бы выбрать.
- [ ] `example` содержит реальные production-кейсы (имена компаний, post-mortems).
- [ ] `related` — plain text refs (`Q5; Q12`), БЕЗ `[[wikilink]]`.
- [ ] Без `**bold**` в `text`.
- [ ] Без эмодзи (❌📋✓🔗) — это legacy v1 формат.
- [ ] Без banned-фраз: «частая ошибка в реальном коде», «это антипаттерн или неправильный выбор в production», «правильный ответ описывает основную концепцию», «противоположное направление».

### H. Smoke verification

- [ ] `./gradlew :quiz-app:test --tests "*McqJsonLoader*"` → BUILD SUCCESSFUL.

## Условные обозначения

| Метка | Значение |
|---|---|
| `⬜ TODO` | Не проверялся в текущей итерации. По умолчанию для всех. |
| `🔄 IN PROGRESS` | Работа в процессе. |
| `✅ DONE` | Все критерии A-H пройдены. |
| `❌ BLOCKED` | Найдена системная проблема, требует решения. |

## Workflow на каждый файл

1. **Read** существующего файла (md + парный json если есть).
2. **Прогнать checklist A-H** — отметить нарушения.
3. **Исправить** нарушения через `/interview-writer` (для md) и `/mcq-quality-fixer` (для json).
4. **Verification**: verify-md-no-mcq + jsonschema + ./gradlew test.
5. **Commit** с описанием изменений (логический коммит на 1-3 файла).
6. **Update** статус в этой таблице ⬜ TODO → ✅ DONE.

## Пофайловая таблица (все 304 файла)

| # | Category | File | Q in MD | JSON exists | Status |
|---|---|---|---|---|---|
| 1 | `ai-ml` | `agentic-patterns` | 30 | — | ⬜ TODO |
| 2 | `ai-ml` | `ai-agents` | 38 | — | ⬜ TODO |
| 3 | `ai-ml` | `ai-application-architecture` | 32 | — | ⬜ TODO |
| 4 | `ai-ml` | `ai-compliance-governance` | 32 | — | ⬜ TODO |
| 5 | `ai-ml` | `ai-observability` | 28 | — | ⬜ TODO |
| 6 | `ai-ml` | `ai-safety-guardrails` | 32 | — | ⬜ TODO |
| 7 | `ai-ml` | `code-agents` | 31 | — | ⬜ TODO |
| 8 | `ai-ml` | `embeddings` | 39 | — | ⬜ TODO |
| 9 | `ai-ml` | `fine-tuning-llm` | 35 | — | ⬜ TODO |
| 10 | `ai-ml` | `function-calling` | 30 | — | ⬜ TODO |
| 11 | `ai-ml` | `inference-optimization` | 36 | — | ⬜ TODO |
| 12 | `ai-ml` | `llm-basics` | 30 | — | ⬜ TODO |
| 13 | `ai-ml` | `llm-evaluation` | 30 | — | ⬜ TODO |
| 14 | `ai-ml` | `llm-integration-patterns` | 38 | — | ⬜ TODO |
| 15 | `ai-ml` | `long-context-vs-rag` | 30 | — | ⬜ TODO |
| 16 | `ai-ml` | `mcp` | 30 | — | ⬜ TODO |
| 17 | `ai-ml` | `mlops` | 38 | — | ⬜ TODO |
| 18 | `ai-ml` | `model-serving` | 38 | — | ⬜ TODO |
| 19 | `ai-ml` | `multi-agent-orchestration` | 30 | — | ⬜ TODO |
| 20 | `ai-ml` | `multimodal-ai` | 31 | — | ⬜ TODO |
| 21 | `ai-ml` | `open-source-llms` | 34 | — | ⬜ TODO |
| 22 | `ai-ml` | `prompt-engineering` | 38 | — | ⬜ TODO |
| 23 | `ai-ml` | `rag` | 40 | — | ⬜ TODO |
| 24 | `ai-ml` | `reasoning-models` | 30 | — | ⬜ TODO |
| 25 | `ai-ml` | `vector-databases` | 28 | — | ⬜ TODO |
| 26 | `algorithms` | `algorithms` | 16 | — | ⬜ TODO |
| 27 | `algorithms/algorithmic-paradigms` | `backtracking` | 24 | — | ⬜ TODO |
| 28 | `algorithms/algorithmic-paradigms` | `divide-and-conquer` | 21 | — | ⬜ TODO |
| 29 | `algorithms/algorithmic-paradigms` | `dynamic-programming` | 33 | — | ⬜ TODO |
| 30 | `algorithms/algorithmic-paradigms` | `greedy-algorithms` | 1 | — | ⬜ TODO |
| 31 | `algorithms/algorithmic-paradigms` | `recursion` | 27 | — | ⬜ TODO |
| 32 | `algorithms/algorithmic-paradigms` | `two-pointers-sliding-window` | 1 | — | ⬜ TODO |
| 33 | `algorithms/complexity` | `complexity-analysis` | 31 | — | ⬜ TODO |
| 34 | `algorithms/data-structures` | `arrays-strings` | 36 | — | ⬜ TODO |
| 35 | `algorithms/data-structures` | `graphs` | 33 | — | ⬜ TODO |
| 36 | `algorithms/data-structures` | `hash-tables` | 34 | — | ⬜ TODO |
| 37 | `algorithms/data-structures` | `heaps` | 29 | — | ⬜ TODO |
| 38 | `algorithms/data-structures` | `linked-lists` | 32 | — | ⬜ TODO |
| 39 | `algorithms/data-structures` | `stacks-queues` | 25 | — | ⬜ TODO |
| 40 | `algorithms/data-structures` | `trees` | 34 | — | ⬜ TODO |
| 41 | `algorithms/data-structures` | `tries` | 28 | — | ⬜ TODO |
| 42 | `algorithms/sorting-searching` | `searching-algorithms` | 8 | — | ⬜ TODO |
| 43 | `algorithms/sorting-searching` | `sorting-algorithms` | 1 | — | ⬜ TODO |
| 44 | `api` | `api-design-best-practices` | 30 | — | ⬜ TODO |
| 45 | `api` | `api-versioning` | 20 | — | ⬜ TODO |
| 46 | `api` | `graphql` | 40 | — | ⬜ TODO |
| 47 | `api` | `grpc` | 40 | — | ⬜ TODO |
| 48 | `api` | `http-rest` | 43 | — | ⬜ TODO |
| 49 | `api` | `openapi-swagger` | 33 | — | ⬜ TODO |
| 50 | `api` | `rest-maturity` | 17 | — | ⬜ TODO |
| 51 | `api` | `websocket` | 38 | — | ⬜ TODO |
| 52 | `architecture` | `api-gateway` | 38 | — | ⬜ TODO |
| 53 | `architecture` | `bff-pattern` | 17 | — | ⬜ TODO |
| 54 | `architecture` | `caching-strategies` | 42 | — | ⬜ TODO |
| 55 | `architecture` | `cap-theorem` | 41 | — | ⬜ TODO |
| 56 | `architecture` | `cdn` | 30 | — | ⬜ TODO |
| 57 | `architecture` | `clean-architecture` | 41 | — | ⬜ TODO |
| 58 | `architecture` | `consistency-patterns` | 31 | — | ⬜ TODO |
| 59 | `architecture` | `cqrs-event-sourcing` | 41 | — | ⬜ TODO |
| 60 | `architecture` | `ddd` | 38 | — | ⬜ TODO |
| 61 | `architecture` | `distributed-systems` | 40 | — | ⬜ TODO |
| 62 | `architecture` | `dns` | 30 | — | ⬜ TODO |
| 63 | `architecture` | `edge-computing` | 18 | — | ⬜ TODO |
| 64 | `architecture` | `event-driven-patterns` | 40 | — | ⬜ TODO |
| 65 | `architecture` | `hexagonal-architecture` | 45 | — | ⬜ TODO |
| 66 | `architecture` | `latency-numbers` | 24 | — | ⬜ TODO |
| 67 | `architecture` | `load-balancing` | 40 | — | ⬜ TODO |
| 68 | `architecture` | `microservices` | 42 | — | ⬜ TODO |
| 69 | `architecture` | `networking` | 43 | — | ⬜ TODO |
| 70 | `architecture` | `resilience-patterns` | 43 | — | ⬜ TODO |
| 71 | `architecture` | `reverse-proxy` | 30 | — | ⬜ TODO |
| 72 | `architecture` | `saga-pattern` | 43 | — | ⬜ TODO |
| 73 | `architecture` | `scalability-patterns` | 41 | — | ⬜ TODO |
| 74 | `architecture` | `service-discovery` | 30 | — | ⬜ TODO |
| 75 | `architecture` | `strangler-fig` | 18 | — | ⬜ TODO |
| 76 | `behavioral` | `behavioral` | 38 | — | ⬜ TODO |
| 77 | `behavioral` | `conflict-stories` | 22 | — | ⬜ TODO |
| 78 | `behavioral` | `culture-fit` | 22 | — | ⬜ TODO |
| 79 | `behavioral` | `failure-stories` | 22 | — | ⬜ TODO |
| 80 | `behavioral` | `leadership-stories` | 22 | — | ⬜ TODO |
| 81 | `behavioral` | `star-method` | 22 | — | ⬜ TODO |
| 82 | `cicd` | `deployment-strategies` | 39 | — | ⬜ TODO |
| 83 | `cicd` | `pipeline-design` | 38 | — | ⬜ TODO |
| 84 | `cloud` | `aws` | 16 | — | ⬜ TODO |
| 85 | `cloud` | `aws-lambda` | 1 | — | ⬜ TODO |
| 86 | `cloud` | `azure` | 25 | — | ⬜ TODO |
| 87 | `cloud` | `cloud-native-patterns` | 14 | — | ⬜ TODO |
| 88 | `cloud` | `gcp` | 1 | — | ⬜ TODO |
| 89 | `cloud` | `serverless` | 1 | — | ⬜ TODO |
| 90 | `code-quality` | `clean-code-practices` | 1 | — | ⬜ TODO |
| 91 | `code-quality` | `code-coverage` | 25 | — | ⬜ TODO |
| 92 | `code-quality` | `code-review` | 1 | — | ⬜ TODO |
| 93 | `code-quality` | `code-smells` | 1 | — | ⬜ TODO |
| 94 | `code-quality` | `refactoring-patterns` | 1 | — | ⬜ TODO |
| 95 | `code-quality` | `static-analysis` | 6 | — | ⬜ TODO |
| 96 | `code-quality` | `technical-debt` | 1 | — | ⬜ TODO |
| 97 | `data-engineering` | `apache-airflow` | 28 | — | ⬜ TODO |
| 98 | `data-engineering` | `apache-flink` | 1 | — | ⬜ TODO |
| 99 | `data-engineering` | `apache-spark` | 1 | — | ⬜ TODO |
| 100 | `data-engineering` | `data-lake-lakehouse` | 28 | — | ⬜ TODO |
| 101 | `data-engineering` | `data-warehousing` | 30 | — | ⬜ TODO |
| 102 | `data-engineering` | `dbt` | 1 | — | ⬜ TODO |
| 103 | `data-engineering` | `kafka-streams` | 1 | — | ⬜ TODO |
| 104 | `data-engineering` | `stream-processing` | 1 | — | ⬜ TODO |
| 105 | `databases` | `cassandra` | 44 | — | ⬜ TODO |
| 106 | `databases` | `clickhouse` | 28 | — | ⬜ TODO |
| 107 | `databases` | `cockroachdb` | 24 | — | ⬜ TODO |
| 108 | `databases` | `database-architecture` | 41 | — | ⬜ TODO |
| 109 | `databases` | `database-replication` | 31 | — | ⬜ TODO |
| 110 | `databases` | `database-sharding` | 34 | — | ⬜ TODO |
| 111 | `databases` | `database-transactions` | 42 | — | ⬜ TODO |
| 112 | `databases` | `dynamodb` | 30 | — | ⬜ TODO |
| 113 | `databases` | `elasticsearch` | 44 | — | ⬜ TODO |
| 114 | `databases` | `flyway-liquibase` | 42 | — | ⬜ TODO |
| 115 | `databases` | `hibernate-caching` | 15 | — | ⬜ TODO |
| 116 | `databases` | `hibernate` | 48 | — | ⬜ TODO |
| 117 | `databases` | `hibernate-jpql-criteria` | 15 | — | ⬜ TODO |
| 118 | `databases` | `hibernate-relationships` | 15 | — | ⬜ TODO |
| 119 | `databases` | `mongodb` | 46 | — | ⬜ TODO |
| 120 | `databases` | `neo4j` | 30 | — | ⬜ TODO |
| 121 | `databases` | `postgresql` | 55 | — | ⬜ TODO |
| 122 | `databases` | `redis` | 43 | — | ⬜ TODO |
| 123 | `databases` | `scylladb` | 23 | — | ⬜ TODO |
| 124 | `databases` | `sql` | 53 | — | ⬜ TODO |
| 125 | `design-patterns` | `design-patterns` | 48 | — | ⬜ TODO |
| 126 | `devops` | `ansible` | 25 | — | ⬜ TODO |
| 127 | `devops` | `argocd` | 42 | — | ⬜ TODO |
| 128 | `devops` | `consul` | 24 | — | ⬜ TODO |
| 129 | `devops` | `docker` | 41 | — | ⬜ TODO |
| 130 | `devops` | `git` | 43 | — | ⬜ TODO |
| 131 | `devops` | `gradle-maven` | 38 | — | ⬜ TODO |
| 132 | `devops` | `helm` | 43 | — | ⬜ TODO |
| 133 | `devops` | `istio-service-mesh` | 26 | — | ⬜ TODO |
| 134 | `devops` | `kubernetes` | 45 | — | ⬜ TODO |
| 135 | `devops` | `linkerd` | 20 | — | ⬜ TODO |
| 136 | `devops` | `linux` | 33 | — | ⬜ TODO |
| 137 | `devops` | `terraform` | 42 | — | ⬜ TODO |
| 138 | `devops` | `vault` | 26 | — | ⬜ TODO |
| 139 | `frameworks/jvm-alternatives` | `ktor` | 1 | — | ⬜ TODO |
| 140 | `frameworks/jvm-alternatives` | `micronaut` | 25 | — | ⬜ TODO |
| 141 | `frameworks/jvm-alternatives` | `quarkus` | 1 | — | ⬜ TODO |
| 142 | `frameworks/jvm-alternatives` | `vertx` | 1 | — | ⬜ TODO |
| 143 | `frameworks/spring` | `resilience4j` | 20 | — | ⬜ TODO |
| 144 | `frameworks/spring` | `spring-ai` | 15 | — | ⬜ TODO |
| 145 | `frameworks/spring` | `spring-aop` | 22 | — | ⬜ TODO |
| 146 | `frameworks/spring` | `spring-async` | 15 | — | ⬜ TODO |
| 147 | `frameworks/spring` | `spring-batch` | 43 | — | ⬜ TODO |
| 148 | `frameworks/spring` | `spring-boot-3-migration` | 15 | — | ⬜ TODO |
| 149 | `frameworks/spring` | `spring-boot-actuator` | 43 | — | ⬜ TODO |
| 150 | `frameworks/spring` | `spring-boot` | 42 | — | ⬜ TODO |
| 151 | `frameworks/spring` | `spring-cache` | 17 | — | ⬜ TODO |
| 152 | `frameworks/spring` | `spring-cloud` | 43 | — | ⬜ TODO |
| 153 | `frameworks/spring` | `spring-data-jdbc` | 16 | — | ⬜ TODO |
| 154 | `frameworks/spring` | `spring-data-jpa` | 42 | — | ⬜ TODO |
| 155 | `frameworks/spring` | `spring-events` | 16 | — | ⬜ TODO |
| 156 | `frameworks/spring` | `spring-framework` | 40 | — | ⬜ TODO |
| 157 | `frameworks/spring` | `spring-graphql` | 15 | — | ⬜ TODO |
| 158 | `frameworks/spring` | `spring-integration` | 15 | — | ⬜ TODO |
| 159 | `frameworks/spring` | `spring-kafka` | 15 | — | ⬜ TODO |
| 160 | `frameworks/spring` | `spring-messaging` | 15 | — | ⬜ TODO |
| 161 | `frameworks/spring` | `spring-modulith` | 2 | — | ⬜ TODO |
| 162 | `frameworks/spring` | `spring-mvc` | 43 | — | ⬜ TODO |
| 163 | `frameworks/spring` | `spring-r2dbc` | 15 | — | ⬜ TODO |
| 164 | `frameworks/spring` | `spring-rest-client` | 13 | — | ⬜ TODO |
| 165 | `frameworks/spring` | `spring-retry` | 17 | — | ⬜ TODO |
| 166 | `frameworks/spring` | `spring-scheduling` | 16 | — | ⬜ TODO |
| 167 | `frameworks/spring` | `spring-security` | 43 | — | ⬜ TODO |
| 168 | `frameworks/spring` | `spring-session` | 15 | — | ⬜ TODO |
| 169 | `frameworks/spring` | `spring-state-machine` | 15 | — | ⬜ TODO |
| 170 | `frameworks/spring` | `spring-testing` | 1 | — | ⬜ TODO |
| 171 | `frameworks/spring` | `spring-transaction` | 15 | — | ⬜ TODO |
| 172 | `frameworks/spring` | `spring-validation` | 16 | — | ⬜ TODO |
| 173 | `frameworks/spring` | `spring-vault` | 15 | — | ⬜ TODO |
| 174 | `frameworks/spring` | `spring-webflux` | 43 | — | ⬜ TODO |
| 175 | `jvm` | `graalvm-native` | 15 | — | ⬜ TODO |
| 176 | `jvm` | `jvm` | 40 | — | ⬜ TODO |
| 177 | `leadership` | `code-review-practices` | 1 | — | ⬜ TODO |
| 178 | `leadership` | `conflict-resolution` | 20 | — | ⬜ TODO |
| 179 | `leadership` | `estimations-planning` | 20 | — | ⬜ TODO |
| 180 | `leadership` | `mentoring` | 1 | — | ⬜ TODO |
| 181 | `leadership` | `team-leadership` | 1 | — | ⬜ TODO |
| 182 | `leadership` | `teching` | 20 | — | ⬜ TODO |
| 183 | `leadership` | `technical-decisions` | 1 | — | ⬜ TODO |
| 184 | `logging` | `logging` | 9 | — | ⬜ TODO |
| 185 | `messaging` | `aws-sqs-sns` | 1 | — | ⬜ TODO |
| 186 | `messaging` | `kafka` | 50 | — | ⬜ TODO |
| 187 | `messaging` | `message-brokers-comparison` | 1 | — | ⬜ TODO |
| 188 | `messaging` | `nats` | 8 | — | ⬜ TODO |
| 189 | `messaging` | `pulsar` | 1 | — | ⬜ TODO |
| 190 | `messaging` | `rabbitmq` | 41 | — | ⬜ TODO |
| 191 | `messaging` | `redpanda` | 20 | — | ⬜ TODO |
| 192 | `monitoring` | `elk-stack` | 5 | — | ⬜ TODO |
| 193 | `monitoring` | `jaeger-zipkin` | 1 | — | ⬜ TODO |
| 194 | `monitoring` | `logging-strategies` | 1 | — | ⬜ TODO |
| 195 | `monitoring` | `loki-grafana` | 1 | — | ⬜ TODO |
| 196 | `monitoring` | `metrics-tracing` | 6 | — | ⬜ TODO |
| 197 | `monitoring` | `micrometer` | 1 | — | ⬜ TODO |
| 198 | `monitoring` | `observability` | 15 | — | ⬜ TODO |
| 199 | `monitoring` | `opentelemetry` | 9 | — | ⬜ TODO |
| 200 | `monitoring` | `prometheus-grafana` | 39 | — | ⬜ TODO |
| 201 | `performance` | `application-profiling` | 42 | — | ⬜ TODO |
| 202 | `performance` | `caching-performance` | 1 | — | ⬜ TODO |
| 203 | `performance` | `database-performance` | 1 | — | ⬜ TODO |
| 204 | `performance` | `jvm-performance-tuning` | 1 | — | ⬜ TODO |
| 205 | `performance` | `memory-management` | 1 | — | ⬜ TODO |
| 206 | `performance` | `network-performance` | 1 | — | ⬜ TODO |
| 207 | `performance` | `performance-testing` | 1 | — | ⬜ TODO |
| 208 | `programming-languages/go` | `go-concurrency` | 35 | — | ⬜ TODO |
| 209 | `programming-languages/go` | `go-generics` | 1 | — | ⬜ TODO |
| 210 | `programming-languages/go` | `go` | 1 | — | ⬜ TODO |
| 211 | `programming-languages/go` | `go-memory-gc` | 1 | — | ⬜ TODO |
| 212 | `programming-languages/go` | `go-modules` | 27 | — | ⬜ TODO |
| 213 | `programming-languages/go` | `go-stdlib` | 30 | — | ⬜ TODO |
| 214 | `programming-languages/go` | `go-testing` | 1 | — | ⬜ TODO |
| 215 | `programming-languages/java` | `java-17-21` | 42 | — | ⬜ TODO |
| 216 | `programming-languages/java` | `java-8` | 41 | — | ⬜ TODO |
| 217 | `programming-languages/java` | `java-annotations` | 43 | — | ⬜ TODO |
| 218 | `programming-languages/java` | `java-collections` | 46 | — | ⬜ TODO |
| 219 | `programming-languages/java` | `java-completable-future` | 13 | — | ⬜ TODO |
| 220 | `programming-languages/java` | `java-concurrency` | 56 | — | ⬜ TODO |
| 221 | `programming-languages/java` | `java-conditional-statements` | 1 | — | ⬜ TODO |
| 222 | `programming-languages/java` | `java-core` | 39 | — | ⬜ TODO |
| 223 | `programming-languages/java` | `java-exceptions` | 42 | — | ⬜ TODO |
| 224 | `programming-languages/java` | `java-functional-interface` | 14 | — | ⬜ TODO |
| 225 | `programming-languages/java` | `java-generics` | 40 | — | ⬜ TODO |
| 226 | `programming-languages/java` | `java-initialization` | 1 | — | ⬜ TODO |
| 227 | `programming-languages/java` | `java-io-nio` | 40 | — | ⬜ TODO |
| 228 | `programming-languages/java` | `java-jackson` | 1 | — | ⬜ TODO |
| 229 | `programming-languages/java` | `java-lombok` | 5 | — | ⬜ TODO |
| 230 | `programming-languages/java` | `java-mapstruct` | 1 | — | ⬜ TODO |
| 231 | `programming-languages/java` | `java-modules` | 38 | — | ⬜ TODO |
| 232 | `programming-languages/java` | `java-oop` | 43 | — | ⬜ TODO |
| 233 | `programming-languages/java` | `java-optional` | 15 | — | ⬜ TODO |
| 234 | `programming-languages/java` | `java-pattern-matching` | 15 | — | ⬜ TODO |
| 235 | `programming-languages/java` | `java-records` | 15 | — | ⬜ TODO |
| 236 | `programming-languages/java` | `java-reflection` | 1 | — | ⬜ TODO |
| 237 | `programming-languages/java` | `java-serialization` | 40 | — | ⬜ TODO |
| 238 | `programming-languages/java` | `java-stream` | 42 | — | ⬜ TODO |
| 239 | `programming-languages/java` | `java-string` | 39 | — | ⬜ TODO |
| 240 | `programming-languages/java` | `java-types` | 38 | — | ⬜ TODO |
| 241 | `programming-languages/java` | `java-virtual-threads` | 15 | — | ⬜ TODO |
| 242 | `programming-languages/kotlin` | `kotlin-collections` | 42 | — | ⬜ TODO |
| 243 | `programming-languages/kotlin` | `kotlin-coroutines` | 19 | — | ⬜ TODO |
| 244 | `programming-languages/kotlin` | `kotlin-dsl` | 1 | — | ⬜ TODO |
| 245 | `programming-languages/kotlin` | `kotlin-exceptions` | 7 | — | ⬜ TODO |
| 246 | `programming-languages/kotlin` | `kotlin-flow` | 17 | — | ⬜ TODO |
| 247 | `programming-languages/kotlin` | `kotlin-interop-java` | 38 | — | ⬜ TODO |
| 248 | `programming-languages/kotlin` | `kotlin` | 27 | — | ⬜ TODO |
| 249 | `programming-languages/kotlin` | `kotlin-sealed-classes` | 15 | — | ⬜ TODO |
| 250 | `programming-languages/kotlin` | `kotlin-serialization` | 1 | — | ⬜ TODO |
| 251 | `programming-languages/kotlin` | `kotlin-spring` | 15 | — | ⬜ TODO |
| 252 | `programming-languages/kotlin` | `kotlin-value-classes` | 15 | — | ⬜ TODO |
| 253 | `programming-languages/scala` | `scala` | 1 | — | ⬜ TODO |
| 254 | `reactive` | `project-reactor` | 47 | — | ⬜ TODO |
| 255 | `reactive` | `reactive-patterns` | 26 | — | ⬜ TODO |
| 256 | `reactive` | `reactive-streams` | 30 | — | ⬜ TODO |
| 257 | `reactive` | `reactive-testing` | 28 | — | ⬜ TODO |
| 258 | `reactive` | `rxjava` | 46 | — | ⬜ TODO |
| 259 | `reactive` | `webflux` | 28 | — | ⬜ TODO |
| 260 | `security` | `application-security` | 45 | — | ⬜ TODO |
| 261 | `security` | `authentication-authorization-patterns` | 45 | — | ⬜ TODO |
| 262 | `security` | `jwt` | 43 | — | ⬜ TODO |
| 263 | `security` | `mtls` | 20 | — | ⬜ TODO |
| 264 | `security` | `oauth2` | 42 | — | ⬜ TODO |
| 265 | `security` | `owasp-top10` | 45 | — | ⬜ TODO |
| 266 | `security` | `secrets-management` | 22 | — | ⬜ TODO |
| 267 | `security` | `supply-chain-security` | 24 | — | ⬜ TODO |
| 268 | `security` | `tls-ssl` | 45 | — | ⬜ TODO |
| 269 | `security` | `zero-trust` | 19 | — | ⬜ TODO |
| 270 | `system-design` | `design-chat-system` | 21 | — | ⬜ TODO |
| 271 | `system-design` | `design-dropbox` | 30 | — | ⬜ TODO |
| 272 | `system-design` | `design-elevator-oo` | 26 | — | ⬜ TODO |
| 273 | `system-design` | `design-feed-system` | 30 | — | ⬜ TODO |
| 274 | `system-design` | `design-google-maps` | 30 | — | ⬜ TODO |
| 275 | `system-design` | `design-instagram` | 27 | — | ⬜ TODO |
| 276 | `system-design` | `design-key-value-store` | 30 | — | ⬜ TODO |
| 277 | `system-design` | `design-netflix` | 30 | — | ⬜ TODO |
| 278 | `system-design` | `design-parking-lot-oo` | 28 | — | ⬜ TODO |
| 279 | `system-design` | `design-pastebin` | 30 | — | ⬜ TODO |
| 280 | `system-design` | `design-payment-system` | 30 | — | ⬜ TODO |
| 281 | `system-design` | `design-rate-limiter` | 30 | — | ⬜ TODO |
| 282 | `system-design` | `design-search` | 30 | — | ⬜ TODO |
| 283 | `system-design` | `design-twitter` | 27 | — | ⬜ TODO |
| 284 | `system-design` | `design-typeahead` | 30 | — | ⬜ TODO |
| 285 | `system-design` | `design-uber` | 30 | — | ⬜ TODO |
| 286 | `system-design` | `design-url-shortener` | 30 | — | ⬜ TODO |
| 287 | `system-design` | `design-vending-machine-oo` | 24 | — | ⬜ TODO |
| 288 | `system-design` | `design-web-crawler` | 26 | — | ⬜ TODO |
| 289 | `system-design` | `design-youtube` | 28 | — | ⬜ TODO |
| 290 | `system-design` | `system-design` | 1 | — | ⬜ TODO |
| 291 | `testing` | `chaos-engineering` | 44 | — | ⬜ TODO |
| 292 | `testing` | `contract-testing` | 20 | — | ⬜ TODO |
| 293 | `testing` | `integration-testing` | 39 | — | ⬜ TODO |
| 294 | `testing` | `junit` | 15 | — | ⬜ TODO |
| 295 | `testing` | `load-testing` | 22 | — | ⬜ TODO |
| 296 | `testing` | `mockito` | 45 | — | ⬜ TODO |
| 297 | `testing` | `mutation-testing` | 20 | — | ⬜ TODO |
| 298 | `testing` | `property-based-testing` | 1 | — | ⬜ TODO |
| 299 | `testing` | `rest-assured` | 15 | — | ⬜ TODO |
| 300 | `testing` | `selenium` | 15 | — | ⬜ TODO |
| 301 | `testing` | `test-automation` | 7 | — | ⬜ TODO |
| 302 | `testing` | `test-strategies` | 45 | — | ⬜ TODO |
| 303 | `testing` | `testcontainers` | 40 | — | ⬜ TODO |
| 304 | `testing` | `unit-testing` | 45 | — | ⬜ TODO |


**Всего файлов:** 304 · **Проверено:** 0 · **TODO:** 304.

---

## Workflow execution

### Фаза 1 — пройти ВЕСЬ список сверху вниз

Каждый файл по очереди. Не пропускать. Не предполагать «уже проверено».

### Phase split по категориям (для параллельного processing)

При множественных subagent-ах можно делить таблицу на batches по category:

- `ai-ml/` (25 файлов).
- `algorithms/` (18 файлов с под-каталогами).
- `api/` (8 файлов).
- `architecture/` (24 файла).
- `behavioral/` (6 файлов).
- `cicd/` (2 файла).
- `cloud/` (6 файлов).
- `code-quality/` (7 файлов).
- `data-engineering/` (8 файлов).
- `databases/` (20 файлов).
- `design-patterns/` (1 файл).
- `devops/` (13 файлов).
- `frameworks/jvm-alternatives/` (4 файла).
- `frameworks/spring/` (32 файла).
- `jvm/` (2 файла).
- `leadership/` (7 файлов).
- `logging/` (1 файл).
- `messaging/` (7 файлов).
- `monitoring/` (9 файлов).
- `performance/` (7 файлов).
- `programming-languages/go/` (7 файлов).
- `programming-languages/java/` (24 файла).
- `programming-languages/kotlin/` (11 файлов).
- `programming-languages/scala/` (1 файл).
- `reactive/` (6 файлов).
- `security/` (10 файлов).
- `system-design/` (21 файл).
- `testing/` (14 файлов).

## Note про `/compact`

`/compact` — это CLI slash-command (pure UI), недоступная через Skill/Tool API. После каждой итерации делается `git commit` как функциональный эквивалент: история сохраняется, не теряется при context compaction. Пользователь сам запускает `/compact` в подходящие моменты.

## Tooling

### Аудит-снимок
```bash
python3 << 'EOF'
# полный re-audit, генерирует /tmp/audit_post.json
EOF
```

### Mass cleanup (если потребуется)
```bash
python3 /tmp/cleanup_md.py
```

### Per-file verification
```bash
bash scripts/verify-md-no-mcq.sh <file.md>
python3 -c "import jsonschema, json; jsonschema.validate(...)"
./gradlew :quiz-app:test --tests '*McqJsonLoader*' -q
```
