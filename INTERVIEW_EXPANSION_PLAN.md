---
title: "План расширения cheatsheets/interview/"
description: "Архитектурный разбор текущего состояния, выявленные пробелы и поэтапный план добавления новых тем и реорганизации структуры"
status: "approved — работа идёт"
updated: "2026-04-18"
---

## Согласованные решения (2026-04-18)

1. **Frontend и Mobile — пропускаем.** Не делаем `frontend/` и `mobile/`.
2. **Разбиение `algorithms/` — делаем сейчас.** Это самый первый этап.
3. **Топ-10 тем Этапа 1 — подтверждены**, но порядок изменён: JVM-related темы идут первыми, Python в самом конце.
4. **Размер пачки — 3-5 файлов** (можно больше, если контент получается ровным и качество не падает).
5. **Коммиты — не делаем.** Работаем без коммитов, пользователь сам решит когда и как коммитить.
6. **README.md — обновляем сразу** при появлении новой категории/подкатегории.
7. **Источники — baeldung.com в приоритете**, затем официальные доки и другие репутабельные ресурсы.
8. **Python — в самом конце.** Основной фокус на JVM-экосистему (Scala, Ktor, Quarkus, Micronaut, Vert.x, Kafka Streams, Spark, Flink) и Go.

### Прогресс

- **[x] Этап A — ВЫПОЛНЕН (2026-04-18):** 17 новых файлов в `algorithms/` (см. структуру ниже), монолит переписан как обзорная карта раздела. TOC.md и README.md обновлены.
- **[x] Этап B — ВЫПОЛНЕН (2026-04-18):** 4 файла в `frameworks/jvm-alternatives/` — Ktor, Quarkus, Micronaut, Vert.x. TOC.md и README.md обновлены.
- **[x] Этап C — ВЫПОЛНЕН (2026-04-18):** 1 файл в `programming-languages/scala/` — Scala (обзор, 40 вопросов).
- **[x] Этап D — ВЫПОЛНЕН (2026-04-18):** 7 файлов в `programming-languages/go/` — Go (базовый), concurrency, memory & GC, generics, stdlib, testing, modules. TOC.md и README.md обновлены.
- **[x] Этап E — ВЫПОЛНЕН (2026-04-18):** 8 файлов в новом разделе `data-engineering/` — Apache Spark, Apache Flink, Kafka Streams, Apache Airflow, dbt, Data Warehousing, Data Lake/Lakehouse, Stream Processing. TOC.md и README.md обновлены.
- **[x] Этап F — ВЫПОЛНЕН (2026-04-19):** 8 файлов в новом разделе `ai-ml/` — LLM Basics, RAG, Vector Databases, Embeddings, Prompt Engineering, LLM Integration Patterns, AI Agents, MLOps, Model Serving. TOC.md и README.md обновлены.
- **[x] Этап G — ВЫПОЛНЕН (2026-04-19):** 6 файлов в новом разделе `cloud/` — AWS, AWS Lambda, GCP, Azure, Serverless, Cloud-native Patterns. TOC.md и README.md обновлены.
- **[x] Этап H — ВЫПОЛНЕН (2026-04-19):** 4 файла в существующем `monitoring/` — OpenTelemetry, Jaeger/Zipkin, ELK Stack, Loki/Grafana. TOC.md обновлён.
- **[x] Этап I — ВЫПОЛНЕН (2026-04-19):** 5 файлов в существующем `databases/` — ClickHouse, DynamoDB, Neo4j, CockroachDB, ScyllaDB. TOC.md обновлён.
- **[x] Этап J1 — ВЫПОЛНЕН (2026-04-19):** 5 файлов в `messaging/` — NATS, Apache Pulsar, AWS SQS/SNS, Redpanda, Message Brokers Comparison. TOC.md обновлён.
- **[x] Этап J2 — ВЫПОЛНЕН (2026-04-19):** 5 файлов в `devops/` — Vault, Consul, Ansible, Istio, Linkerd. TOC.md обновлён.
- **[x] Этап J3 — ВЫПОЛНЕН (2026-04-19):** 3 файла в `api/` — REST Maturity (Richardson), API Versioning, API Design Best Practices. TOC.md обновлён.
- **[x] Этап J4 — ВЫПОЛНЕН (2026-04-19):** 3 файла в `testing/` — Mutation Testing, Property-based Testing, Load Testing. TOC.md обновлён.
- **[x] Этап J5 — ВЫПОЛНЕН (2026-04-19):** 4 файла в `security/` — Zero Trust, mTLS, Secrets Management, Supply Chain Security. TOC.md обновлён.
- **[x] Этап J6 — ВЫПОЛНЕН (2026-04-19):** 3 файла в `performance/` — Database Performance, Caching Performance, Network Performance. TOC.md обновлён.
- **[x] Этап J7 — ВЫПОЛНЕН (2026-04-19):** 6 файлов в `system-design/` — URL Shortener, Rate Limiter, Chat System, Feed System, Search, Payment System. TOC.md обновлён.
- **[x] Этап J8 — ВЫПОЛНЕН (2026-04-19):** 3 файла в `architecture/` — Edge Computing, BFF Pattern, Strangler Fig. TOC.md обновлён.

**Итого добавлено:** 92 файла. Размер раздела вырос со **106 → 198** файлов.

### Этап J полностью завершён.

### Новый порядок этапов (обновлён)

1. **Этап A. Разбиение `algorithms/`** — 17 новых файлов, монолит остаётся как обзор ✅
2. **Этап B. JVM-фреймворки (не-Spring)** — Ktor, Quarkus, Micronaut, Vert.x
3. **Этап C. Scala** — отдельная JVM-тема
4. **Этап D. Go** — базовый Go, concurrency, memory/GC, generics, stdlib, testing, modules
5. **Этап E. Data Engineering (JVM-мир)** — Spark, Flink, Kafka Streams, Airflow
6. **Этап F. AI/ML** — LLM basics, RAG, vector DBs, embeddings, prompt engineering, agents, MLOps, model serving
7. **Этап G. Cloud** — AWS, Lambda, GCP, Azure, Serverless, Cloud-native patterns
8. **Этап H. Monitoring / Observability** — OpenTelemetry, Jaeger/Zipkin, ELK, Loki
9. **Этап I. Databases (новые)** — ClickHouse, DynamoDB, Neo4j, CockroachDB, ScyllaDB
10. **Этап J. Точечные добавления** — messaging (NATS, Pulsar), devops (Vault, Istio), api, testing, security, performance, system-design кейс-стади
11. **Этап K. TypeScript + Rust** — как остатки языков
12. **Этап L. Python** — в самом конце



# План расширения `cheatsheets/interview/`

## 1. Текущее состояние

- **22 категории, 106 файлов** (средний размер — 40-45 вопросов на файл)
- Сильные стороны: Java/Spring/JVM/Kotlin-экосистема, архитектурные паттерны, БД, security, testing
- Слабые стороны:
  - `algorithms/` — один плоский файл ~95 KB (сложно читать, сложно линковать, сложно расширять)
  - `programming-languages/` — только Java и Kotlin (пустой мир Go, Python, TypeScript, Rust, Scala)
  - `frameworks/` — только Spring, нет Quarkus, Micronaut, Ktor, Node.js, Python и Go-фреймворков
  - Нет разделов AI/ML, Cloud, Data Engineering, Frontend, Mobile — критичных для 2026
  - В `databases/` нет ClickHouse, DynamoDB, Neo4j, CockroachDB, ScyllaDB
  - В `messaging/` нет NATS, Apache Pulsar, AWS SQS/SNS, Redpanda
  - В `devops/` нет Vault, Consul, Ansible, Nomad, Packer
  - В `monitoring/` нет отдельных материалов по Jaeger/Zipkin, ELK/Loki, OpenTelemetry

## 2. Предлагаемая реорганизация структуры

### 2.1. Разделить `algorithms/` на подкатегории

```
algorithms/
├── algorithms-interview.md       # оставить как overview (сократить до 15-20 Q)
├── data-structures/
│   ├── arrays-strings-interview.md
│   ├── linked-lists-interview.md
│   ├── trees-interview.md
│   ├── heaps-interview.md
│   ├── hash-tables-interview.md
│   ├── graphs-interview.md
│   └── tries-interview.md
├── sorting-searching/
│   ├── sorting-algorithms-interview.md
│   └── searching-algorithms-interview.md
├── algorithmic-paradigms/
│   ├── dynamic-programming-interview.md
│   ├── greedy-algorithms-interview.md
│   ├── divide-and-conquer-interview.md
│   ├── backtracking-interview.md
│   └── two-pointers-sliding-window-interview.md
└── complexity/
    └── complexity-analysis-interview.md
```

### 2.2. Реорганизовать `programming-languages/`

Сейчас: `java/`, `kotlin/`. Добавить:

```
programming-languages/
├── java/              # как есть (16 файлов) — сильная база
├── kotlin/            # как есть (7 файлов)
├── go/                # НОВОЕ — Go стал обязательным для бэкенда 2026
│   ├── go-interview.md
│   ├── go-concurrency-interview.md
│   ├── go-memory-gc-interview.md
│   ├── go-generics-interview.md
│   ├── go-stdlib-interview.md
│   ├── go-testing-interview.md
│   └── go-modules-interview.md
├── python/            # НОВОЕ — доминирует в AI/ML и data
│   ├── python-interview.md
│   ├── python-async-interview.md
│   ├── python-typing-interview.md
│   ├── python-gil-interview.md
│   ├── python-memory-interview.md
│   └── python-testing-interview.md
├── typescript/        # НОВОЕ — фронт и Node.js
│   ├── typescript-interview.md
│   ├── typescript-types-interview.md
│   ├── typescript-generics-interview.md
│   └── typescript-configs-interview.md
├── rust/              # НОВОЕ — системные бэкенды, blockchain
│   ├── rust-interview.md
│   ├── rust-ownership-borrowing-interview.md
│   ├── rust-concurrency-interview.md
│   └── rust-async-interview.md
└── scala/             # НОВОЕ — опционально, но встречается в data
    └── scala-interview.md
```

### 2.3. Расширить `frameworks/`

```
frameworks/
├── spring/            # как есть (9 файлов)
├── jvm-alternatives/  # НОВОЕ
│   ├── quarkus-interview.md
│   ├── micronaut-interview.md
│   ├── ktor-interview.md
│   └── vertx-interview.md
├── go-frameworks/     # НОВОЕ
│   ├── gin-interview.md
│   ├── fiber-interview.md
│   └── echo-interview.md
├── python-frameworks/ # НОВОЕ
│   ├── fastapi-interview.md
│   ├── django-interview.md
│   └── flask-interview.md
└── nodejs-frameworks/ # НОВОЕ
    ├── nestjs-interview.md
    └── express-interview.md
```

### 2.4. Новые top-level категории

#### `cloud/` — облака и serverless

```
cloud/
├── aws-interview.md             # EC2/S3/RDS/Lambda/DynamoDB/SQS/SNS/IAM
├── aws-lambda-interview.md      # deeper dive в serverless
├── gcp-interview.md
├── azure-interview.md
├── serverless-interview.md      # общие концепции
├── cloud-native-patterns-interview.md
└── multi-cloud-interview.md
```

#### `ai-ml/` — AI/ML для бэкендеров в 2026

```
ai-ml/
├── llm-basics-interview.md                # что такое LLM, prompt engineering
├── rag-interview.md                       # Retrieval-Augmented Generation
├── vector-databases-interview.md          # Pinecone, Qdrant, Weaviate, pgvector
├── embeddings-interview.md
├── prompt-engineering-interview.md
├── llm-integration-patterns-interview.md  # caching, streaming, tool use
├── ai-agents-interview.md                 # агенты, MCP, tool calling
├── mlops-interview.md
└── model-serving-interview.md             # как деплоить, как мониторить
```

#### `data-engineering/` — работа с данными и потоками

```
data-engineering/
├── apache-spark-interview.md
├── apache-flink-interview.md
├── apache-airflow-interview.md
├── dbt-interview.md
├── kafka-streams-interview.md
├── data-lake-lakehouse-interview.md       # Delta Lake, Iceberg, Hudi
├── data-warehousing-interview.md          # Snowflake, BigQuery, Redshift
├── etl-elt-interview.md
└── stream-processing-interview.md
```

#### `frontend/` — минимальный набор для full-stack интервью

```
frontend/
├── react-interview.md
├── nextjs-interview.md
├── state-management-interview.md          # Redux, Zustand, TanStack Query
├── browser-internals-interview.md         # event loop, rendering, CORS
├── web-performance-interview.md
└── frontend-testing-interview.md
```

#### `mobile/` — мобильная разработка

```
mobile/
├── android-interview.md
├── ios-interview.md
├── kotlin-multiplatform-interview.md
└── react-native-interview.md
```

### 2.5. Точечные добавления в существующие категории

**`databases/`:** `clickhouse-interview.md`, `dynamodb-interview.md`, `neo4j-interview.md`, `cockroachdb-interview.md`, `scylladb-interview.md`, `database-sharding-interview.md`, `database-replication-interview.md`

**`messaging/`:** `nats-interview.md`, `pulsar-interview.md`, `aws-sqs-sns-interview.md`, `redpanda-interview.md`, `message-brokers-comparison-interview.md`

**`devops/`:** `vault-interview.md`, `consul-interview.md`, `ansible-interview.md`, `istio-service-mesh-interview.md`, `linkerd-interview.md`

**`monitoring/`:** `opentelemetry-interview.md`, `jaeger-zipkin-interview.md`, `elk-stack-interview.md`, `loki-grafana-interview.md`

**`api/`:** `rest-maturity-interview.md` (Richardson), `api-versioning-interview.md`, `api-design-best-practices-interview.md`

**`testing/`:** `mutation-testing-interview.md`, `property-based-testing-interview.md`, `load-testing-interview.md` (JMeter/k6/Gatling)

**`security/`:** `zero-trust-interview.md`, `mTLS-interview.md`, `secrets-management-interview.md`, `supply-chain-security-interview.md`

**`performance/`:** `database-performance-interview.md`, `caching-performance-interview.md`, `network-performance-interview.md`

**`system-design/`:** отдельные кейс-стади — `design-url-shortener-interview.md`, `design-rate-limiter-interview.md`, `design-chat-system-interview.md`, `design-feed-system-interview.md`, `design-search-interview.md`, `design-payment-system-interview.md`

**`architecture/`:** `edge-computing-interview.md`, `bff-pattern-interview.md`, `strangler-fig-interview.md`

## 3. Приоритизация и порционный план работы

### Этап 1. Фундамент (топ-10 тем по востребованности) — высокий приоритет

Темы, которые встречаются на каждом втором интервью в 2026 и отсутствуют у нас:

1. **`go-interview.md`** — базовый Go
2. **`go-concurrency-interview.md`** — горутины, каналы, select
3. **`python-interview.md`** — базовый Python
4. **`python-async-interview.md`** — asyncio
5. **`llm-basics-interview.md`** — базовый LLM
6. **`rag-interview.md`** — RAG паттерн
7. **`vector-databases-interview.md`** — vector DBs
8. **`aws-interview.md`** — AWS базовый
9. **`clickhouse-interview.md`** — ClickHouse
10. **`opentelemetry-interview.md`** — OpenTelemetry

### Этап 2. Разбиение `algorithms/` — средний приоритет

Разнести существующий монолитный файл на 10-12 подтем. Можно сделать частично руками, частично адаптировав существующий контент.

11. `data-structures/arrays-strings-interview.md`
12. `data-structures/linked-lists-interview.md`
13. `data-structures/trees-interview.md`
14. `data-structures/graphs-interview.md`
15. `data-structures/hash-tables-interview.md`
16. `data-structures/heaps-interview.md`
17. `sorting-searching/sorting-algorithms-interview.md`
18. `sorting-searching/searching-algorithms-interview.md`
19. `algorithmic-paradigms/dynamic-programming-interview.md`
20. `algorithmic-paradigms/greedy-algorithms-interview.md`
21. `algorithmic-paradigms/backtracking-interview.md`
22. `algorithmic-paradigms/two-pointers-sliding-window-interview.md`

### Этап 3. Расширение `programming-languages/` — средний приоритет

23-28. Оставшиеся Go-файлы (memory, generics, stdlib, testing, modules)
29-33. Оставшиеся Python-файлы (typing, GIL, memory, testing)
34-37. TypeScript-файлы (4 файла)
38-41. Rust-файлы (4 файла)

### Этап 4. AI/ML и Cloud — средний приоритет

42. `embeddings-interview.md`
43. `prompt-engineering-interview.md`
44. `llm-integration-patterns-interview.md`
45. `ai-agents-interview.md`
46. `mlops-interview.md`
47. `model-serving-interview.md`
48. `aws-lambda-interview.md`
49. `gcp-interview.md`
50. `azure-interview.md`
51. `serverless-interview.md`
52. `cloud-native-patterns-interview.md`

### Этап 5. Data Engineering — средний приоритет

53. `apache-spark-interview.md`
54. `apache-flink-interview.md`
55. `apache-airflow-interview.md`
56. `dbt-interview.md`
57. `kafka-streams-interview.md`
58. `data-lake-lakehouse-interview.md`
59. `data-warehousing-interview.md`
60. `stream-processing-interview.md`

### Этап 6. Frameworks (не-Spring) — низкий приоритет

61. `quarkus-interview.md`
62. `micronaut-interview.md`
63. `ktor-interview.md`
64. `vertx-interview.md`
65-67. Go frameworks (Gin, Fiber, Echo)
68-70. Python frameworks (FastAPI, Django, Flask)
71-72. Node.js frameworks (NestJS, Express)

### Этап 7. Точечные добавления — низкий приоритет, по запросу

Все остальные темы из пункта 2.5 (databases, messaging, devops, monitoring, api, testing, security, performance, system-design, architecture) — примерно 40-50 файлов.

### Этап 8. Frontend и Mobile — низкий приоритет (опционально)

Если пользователь заинтересован в full-stack подготовке. Иначе можно пропустить.

## 4. Порядок работы (по договорённости)

**Предлагается:** делать пачками по 3-5 файлов за итерацию, чтобы:
- каждый файл был глубокий (30-50 Q, с кодом, диаграммами, ссылками на baeldung/docs)
- был шанс проревьюить и поправить курс
- не загружать git одним гигантским коммитом

**После каждой пачки:**
- обновлять `TOC.md` (количество файлов, новые entries)
- обновлять `README.md` (при появлении новой категории)
- проверять формат (парсер `## Q<N>.`)
- проверять wikilinks в `## See also`
- коммитить отдельно с понятным сообщением

## 5. Метрики готовности

- После Этапа 1: +10 файлов (+9%) — закрыты самые востребованные пробелы 2026
- После Этапа 2: +12 файлов, `algorithms/` реорганизован
- После Этапа 3: +19 файлов, `programming-languages/` покрывает топ-5 языков
- После Этапа 4-5: +19 файлов, AI/ML и Data Engineering представлены
- После Этапа 6: +12 файлов, `frameworks/` выходит за Spring
- После Этапа 7: +40-50 файлов, точечные глубокие темы
- **Итого после полного плана:** ~115-120 новых файлов (удвоение объёма)

## 6. Вопросы к согласованию

1. **Scope:** нужны ли тебе Frontend и Mobile, или это точно пропускаем?
2. **Этап 2 (разбиение algorithms):** разбивать прямо сейчас или отложить? Это требует разнесения существующего контента, не только дописывания нового.
3. **Приоритет Этапа 1:** согласен с топ-10 тем (Go, Python, LLM, RAG, AWS, ClickHouse, OpenTelemetry)? Или поменять порядок?
4. **Размер пачки:** 3-5 файлов за итерацию OK, или хочешь больше/меньше?
5. **Коммиты:** один коммит на пачку, или один коммит на файл?
6. **README стратегии:** обновить `README.md` с описанием новых категорий сразу как только появится первый файл в категории, или в конце?
