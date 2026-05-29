# План аудита читабельности interview-файлов

> Цель: проверить все interview-файлы и вопросы к ним, улучшить читаемость и связность русского текста.

> Статусы: ⬜ не проверен · 🔄 в работе · ✅ проверен и улучшен · ➖ изменений не требует


## Чек-лист проверки каждого файла

1. Frontmatter полный (title, description, tags YAML-list, type, difficulty, aliases ≥3, updated)
2. Язык: только русский в пояснениях; англ. только в `backticks`/коде/именах
3. Короткие абзацы (2-4 предложения), нет «стен текста»
4. Bullet-списки для перечислений 3+ пунктов
5. Таблицы для сравнений (vs-вопросы)
6. Код в блоках с language tag
7. Нет воды/маркетинга/academic-tone («важно отметить», «в этом разделе мы рассмотрим»)
8. Примеры в концептуальных ответах
9. Явные выводы (**Итог:**/**Вывод:**) где уместно
10. Связность: плавные переходы, единый стиль, нет обрывков мысли
11. См. also ≥5 ссылок, корректные относительные пути
12. Нумерация Q без пропусков, формат `## Q<N>.`


## Прогресс: 4 / 305 файлов (⚠️ 21 файлов с инлайновыми MCQ — особый режим)


### ai-ml (25)

- ⬜ `ai-ml/agentic-patterns-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/ai-agents-interview.md` — 38 Q — _не проверен_
- ⬜ `ai-ml/ai-application-architecture-interview.md` — 32 Q — _не проверен_
- ⬜ `ai-ml/ai-compliance-governance-interview.md` — 32 Q — _не проверен_
- ⬜ `ai-ml/ai-observability-interview.md` — 28 Q — _не проверен_
- ⬜ `ai-ml/ai-safety-guardrails-interview.md` — 32 Q — _не проверен_
- ⬜ `ai-ml/code-agents-interview.md` — 31 Q — _не проверен_
- ⬜ `ai-ml/embeddings-interview.md` — 39 Q — _не проверен_
- ⬜ `ai-ml/fine-tuning-llm-interview.md` — 35 Q — _не проверен_
- ⬜ `ai-ml/function-calling-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/inference-optimization-interview.md` — 36 Q — _не проверен_
- ⬜ `ai-ml/llm-basics-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/llm-evaluation-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/llm-integration-patterns-interview.md` — 38 Q — _не проверен_
- ⬜ `ai-ml/long-context-vs-rag-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/mcp-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/mlops-interview.md` — 38 Q — _не проверен_
- ⬜ `ai-ml/model-serving-interview.md` — 38 Q — _не проверен_
- ⬜ `ai-ml/multi-agent-orchestration-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/multimodal-ai-interview.md` — 31 Q — _не проверен_
- ⬜ `ai-ml/open-source-llms-interview.md` — 34 Q — _не проверен_
- ⬜ `ai-ml/prompt-engineering-interview.md` — 38 Q — _не проверен_
- ⬜ `ai-ml/rag-interview.md` — 40 Q — _не проверен_
- ⬜ `ai-ml/reasoning-models-interview.md` — 30 Q — _не проверен_
- ⬜ `ai-ml/vector-databases-interview.md` — 28 Q — _не проверен_

### algorithms (18)

- ⬜ `algorithms/algorithmic-paradigms/backtracking-interview.md` — 24 Q — _не проверен_
- ⬜ `algorithms/algorithmic-paradigms/divide-and-conquer-interview.md` — 21 Q — _не проверен_
- ⬜ `algorithms/algorithmic-paradigms/dynamic-programming-interview.md` — 33 Q — _не проверен_
- ⬜ `algorithms/algorithmic-paradigms/greedy-algorithms-interview.md` — 1 Q — _не проверен_
- ⬜ `algorithms/algorithmic-paradigms/recursion-interview.md` — 27 Q — _не проверен_
- ⬜ `algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview.md` — 1 Q — _не проверен_
- ⬜ `algorithms/algorithms-interview.md` — 16 Q — _не проверен_
- ⬜ `algorithms/complexity/complexity-analysis-interview.md` — 31 Q — _не проверен_
- ⬜ `algorithms/data-structures/arrays-strings-interview.md` — 36 Q — _не проверен_
- ⬜ `algorithms/data-structures/graphs-interview.md` — 33 Q — _не проверен_
- ⬜ `algorithms/data-structures/hash-tables-interview.md` — 34 Q — _не проверен_
- ⬜ `algorithms/data-structures/heaps-interview.md` — 29 Q — _не проверен_
- ⬜ `algorithms/data-structures/linked-lists-interview.md` — 32 Q — _не проверен_
- ⬜ `algorithms/data-structures/stacks-queues-interview.md` — 25 Q — _не проверен_
- ⬜ `algorithms/data-structures/trees-interview.md` — 34 Q — _не проверен_
- ⬜ `algorithms/data-structures/tries-interview.md` — 28 Q — _не проверен_
- ⬜ `algorithms/sorting-searching/searching-algorithms-interview.md` — 8 Q — _не проверен_
- ⬜ `algorithms/sorting-searching/sorting-algorithms-interview.md` — 1 Q — _не проверен_

### api (8)

- ⚠️ `api/api-design-best-practices-interview.md` — 30 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⬜ `api/api-versioning-interview.md` — 20 Q — _не проверен_
- ⚠️ `api/graphql-interview.md` — 40 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `api/grpc-interview.md` — 40 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `api/http-rest-interview.md` — 43 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `api/openapi-swagger-interview.md` — 33 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ✅ `api/rest-maturity-interview.md` — 17 Q — _проверен и улучшен (wave 2): перевод англо-прозы, списки/таблицы, убран канцелярит, +Итог_
- ⚠️ `api/websocket-interview.md` — 38 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_

### architecture (24)

- ⬜ `architecture/api-gateway-interview.md` — 38 Q — _не проверен_
- ⬜ `architecture/bff-pattern-interview.md` — 17 Q — _не проверен_
- ⬜ `architecture/caching-strategies-interview.md` — 42 Q — _не проверен_
- ⬜ `architecture/cap-theorem-interview.md` — 41 Q — _не проверен_
- ⬜ `architecture/cdn-interview.md` — 30 Q — _не проверен_
- ⬜ `architecture/clean-architecture-interview.md` — 41 Q — _не проверен_
- ⬜ `architecture/consistency-patterns-interview.md` — 31 Q — _не проверен_
- ⬜ `architecture/cqrs-event-sourcing-interview.md` — 41 Q — _не проверен_
- ⬜ `architecture/ddd-interview.md` — 38 Q — _не проверен_
- ⬜ `architecture/distributed-systems-interview.md` — 40 Q — _не проверен_
- ⬜ `architecture/dns-interview.md` — 30 Q — _не проверен_
- ⬜ `architecture/edge-computing-interview.md` — 18 Q — _не проверен_
- ⬜ `architecture/event-driven-patterns-interview.md` — 40 Q — _не проверен_
- ⬜ `architecture/hexagonal-architecture-interview.md` — 45 Q — _не проверен_
- ⬜ `architecture/latency-numbers-interview.md` — 24 Q — _не проверен_
- ⬜ `architecture/load-balancing-interview.md` — 40 Q — _не проверен_
- ⬜ `architecture/microservices-interview.md` — 42 Q — _не проверен_
- ⬜ `architecture/networking-interview.md` — 43 Q — _не проверен_
- ⬜ `architecture/resilience-patterns-interview.md` — 43 Q — _не проверен_
- ⬜ `architecture/reverse-proxy-interview.md` — 30 Q — _не проверен_
- ⬜ `architecture/saga-pattern-interview.md` — 43 Q — _не проверен_
- ⬜ `architecture/scalability-patterns-interview.md` — 41 Q — _не проверен_
- ⬜ `architecture/service-discovery-interview.md` — 30 Q — _не проверен_
- ⬜ `architecture/strangler-fig-interview.md` — 18 Q — _не проверен_

### behavioral (6)

- ⚠️ `behavioral/behavioral-interview.md` — 38 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⬜ `behavioral/conflict-stories-interview.md` — 22 Q — _не проверен_
- ⬜ `behavioral/culture-fit-interview.md` — 22 Q — _не проверен_
- ⬜ `behavioral/failure-stories-interview.md` — 22 Q — _не проверен_
- ⬜ `behavioral/leadership-stories-interview.md` — 22 Q — _не проверен_
- ⬜ `behavioral/star-method-interview.md` — 22 Q — _не проверен_

### cicd (2)

- ⚠️ `cicd/deployment-strategies-interview.md` — 39 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `cicd/pipeline-design-interview.md` — 38 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_

### cloud (6)

- ⚠️ `cloud/aws-interview.md` — 16 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `cloud/aws-lambda-interview.md` — 1 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `cloud/azure-interview.md` — 25 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `cloud/cloud-native-patterns-interview.md` — 14 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `cloud/gcp-interview.md` — 1 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `cloud/serverless-interview.md` — 1 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_

### code-quality (7)

- ⬜ `code-quality/clean-code-practices-interview.md` — 1 Q — _не проверен_
- ⬜ `code-quality/code-coverage-interview.md` — 25 Q — _не проверен_
- ⬜ `code-quality/code-review-interview.md` — 1 Q — _не проверен_
- ⬜ `code-quality/code-smells-interview.md` — 1 Q — _не проверен_
- ⬜ `code-quality/refactoring-patterns-interview.md` — 1 Q — _не проверен_
- ⬜ `code-quality/static-analysis-interview.md` — 6 Q — _не проверен_
- ⬜ `code-quality/technical-debt-interview.md` — 1 Q — _не проверен_

### data-engineering (8)

- ⬜ `data-engineering/apache-airflow-interview.md` — 28 Q — _не проверен_
- ⬜ `data-engineering/apache-flink-interview.md` — 1 Q — _не проверен_
- ⬜ `data-engineering/apache-spark-interview.md` — 1 Q — _не проверен_
- ⬜ `data-engineering/data-lake-lakehouse-interview.md` — 28 Q — _не проверен_
- ⬜ `data-engineering/data-warehousing-interview.md` — 30 Q — _не проверен_
- ⬜ `data-engineering/dbt-interview.md` — 1 Q — _не проверен_
- ⬜ `data-engineering/kafka-streams-interview.md` — 1 Q — _не проверен_
- ⬜ `data-engineering/stream-processing-interview.md` — 1 Q — _не проверен_

### databases (20)

- ⬜ `databases/cassandra-interview.md` — 44 Q — _не проверен_
- ⬜ `databases/clickhouse-interview.md` — 28 Q — _не проверен_
- ⬜ `databases/cockroachdb-interview.md` — 24 Q — _не проверен_
- ⬜ `databases/database-architecture-interview.md` — 41 Q — _не проверен_
- ⬜ `databases/database-replication-interview.md` — 31 Q — _не проверен_
- ⬜ `databases/database-sharding-interview.md` — 34 Q — _не проверен_
- ⬜ `databases/database-transactions-interview.md` — 42 Q — _не проверен_
- ⬜ `databases/dynamodb-interview.md` — 30 Q — _не проверен_
- ⬜ `databases/elasticsearch-interview.md` — 44 Q — _не проверен_
- ⬜ `databases/flyway-liquibase-interview.md` — 42 Q — _не проверен_
- ⬜ `databases/hibernate-caching-interview.md` — 15 Q — _не проверен_
- ⬜ `databases/hibernate-interview.md` — 48 Q — _не проверен_
- ⬜ `databases/hibernate-jpql-criteria-interview.md` — 15 Q — _не проверен_
- ⬜ `databases/hibernate-relationships-interview.md` — 15 Q — _не проверен_
- ⬜ `databases/mongodb-interview.md` — 46 Q — _не проверен_
- ⬜ `databases/neo4j-interview.md` — 30 Q — _не проверен_
- ⬜ `databases/postgresql-interview.md` — 55 Q — _не проверен_
- ⬜ `databases/redis-interview.md` — 43 Q — _не проверен_
- ⬜ `databases/scylladb-interview.md` — 23 Q — _не проверен_
- ⬜ `databases/sql-interview.md` — 53 Q — _не проверен_

### design-patterns (1)

- ⚠️ `design-patterns/design-patterns-interview.md` — 48 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_

### devops (13)

- ⬜ `devops/ansible-interview.md` — 25 Q — _не проверен_
- ⬜ `devops/argocd-interview.md` — 42 Q — _не проверен_
- ⬜ `devops/consul-interview.md` — 24 Q — _не проверен_
- ⬜ `devops/docker-interview.md` — 41 Q — _не проверен_
- ⬜ `devops/git-interview.md` — 43 Q — _не проверен_
- ⬜ `devops/gradle-maven-interview.md` — 38 Q — _не проверен_
- ⬜ `devops/helm-interview.md` — 43 Q — _не проверен_
- ⬜ `devops/istio-service-mesh-interview.md` — 26 Q — _не проверен_
- ⬜ `devops/kubernetes-interview.md` — 45 Q — _не проверен_
- ⬜ `devops/linkerd-interview.md` — 20 Q — _не проверен_
- ⬜ `devops/linux-interview.md` — 33 Q — _не проверен_
- ⬜ `devops/terraform-interview.md` — 42 Q — _не проверен_
- ⬜ `devops/vault-interview.md` — 26 Q — _не проверен_

### frameworks (36)

- ⬜ `frameworks/jvm-alternatives/ktor-interview.md` — 1 Q — _не проверен_
- ⬜ `frameworks/jvm-alternatives/micronaut-interview.md` — 25 Q — _не проверен_
- ⬜ `frameworks/jvm-alternatives/quarkus-interview.md` — 1 Q — _не проверен_
- ⬜ `frameworks/jvm-alternatives/vertx-interview.md` — 1 Q — _не проверен_
- ⬜ `frameworks/spring/resilience4j-interview.md` — 20 Q — _не проверен_
- ⬜ `frameworks/spring/spring-ai-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-aop-interview.md` — 22 Q — _не проверен_
- ⬜ `frameworks/spring/spring-async-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-batch-interview.md` — 43 Q — _не проверен_
- ⬜ `frameworks/spring/spring-boot-3-migration-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-boot-actuator-interview.md` — 43 Q — _не проверен_
- ⬜ `frameworks/spring/spring-boot-interview.md` — 42 Q — _не проверен_
- ⬜ `frameworks/spring/spring-cache-interview.md` — 17 Q — _не проверен_
- ⬜ `frameworks/spring/spring-cloud-interview.md` — 43 Q — _не проверен_
- ⬜ `frameworks/spring/spring-data-jdbc-interview.md` — 16 Q — _не проверен_
- ⬜ `frameworks/spring/spring-data-jpa-interview.md` — 42 Q — _не проверен_
- ⬜ `frameworks/spring/spring-events-interview.md` — 16 Q — _не проверен_
- ⬜ `frameworks/spring/spring-framework-interview.md` — 40 Q — _не проверен_
- ⬜ `frameworks/spring/spring-graphql-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-integration-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-kafka-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-messaging-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-modulith-interview.md` — 2 Q — _не проверен_
- ⬜ `frameworks/spring/spring-mvc-interview.md` — 43 Q — _не проверен_
- ⬜ `frameworks/spring/spring-r2dbc-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-rest-client-interview.md` — 13 Q — _не проверен_
- ⬜ `frameworks/spring/spring-retry-interview.md` — 17 Q — _не проверен_
- ⬜ `frameworks/spring/spring-scheduling-interview.md` — 16 Q — _не проверен_
- ⬜ `frameworks/spring/spring-security-interview.md` — 43 Q — _не проверен_
- ⬜ `frameworks/spring/spring-session-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-state-machine-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-testing-interview.md` — 1 Q — _не проверен_
- ⬜ `frameworks/spring/spring-transaction-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-validation-interview.md` — 16 Q — _не проверен_
- ⬜ `frameworks/spring/spring-vault-interview.md` — 15 Q — _не проверен_
- ⬜ `frameworks/spring/spring-webflux-interview.md` — 43 Q — _не проверен_

### jvm (2)

- ⚠️ `jvm/graalvm-native-interview.md` — 15 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ⚠️ `jvm/jvm-interview.md` — 40 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_

### leadership (7)

- ⚠️ `leadership/code-review-practices-interview.md` — 1 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ✅ `leadership/conflict-resolution-interview.md` — 20 Q — _проверен и улучшен (wave 2): перевод англо-прозы, списки/таблицы, убран канцелярит, +Итог_
- ✅ `leadership/estimations-planning-interview.md` — 20 Q — _проверен и улучшен (wave 2): перевод англо-прозы, списки/таблицы, убран канцелярит, +Итог_
- ⬜ `leadership/mentoring-interview.md` — 1 Q — _не проверен_
- ⚠️ `leadership/team-leadership-interview.md` — 1 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_
- ✅ `leadership/tech-interviewing-interview.md` — 20 Q — _проверен и улучшен (wave 2): перевод англо-прозы, списки/таблицы, убран канцелярит, +Итог_
- ⬜ `leadership/technical-decisions-interview.md` — 1 Q — _не проверен_

### logging (1)

- ⚠️ `logging/logging-interview.md` — 9 Q — _содержит инлайновые MCQ (нет JSON-бэкапа) → править бережно, MCQ сохранять byte-for-byte_

### messaging (7)

- ⬜ `messaging/aws-sqs-sns-interview.md` — 1 Q — _не проверен_
- ⬜ `messaging/kafka-interview.md` — 50 Q — _не проверен_
- ⬜ `messaging/message-brokers-comparison-interview.md` — 1 Q — _не проверен_
- ⬜ `messaging/nats-interview.md` — 8 Q — _не проверен_
- ⬜ `messaging/pulsar-interview.md` — 1 Q — _не проверен_
- ⬜ `messaging/rabbitmq-interview.md` — 41 Q — _не проверен_
- ⬜ `messaging/redpanda-interview.md` — 20 Q — _не проверен_

### monitoring (9)

- ⬜ `monitoring/elk-stack-interview.md` — 5 Q — _не проверен_
- ⬜ `monitoring/jaeger-zipkin-interview.md` — 1 Q — _не проверен_
- ⬜ `monitoring/logging-strategies-interview.md` — 1 Q — _не проверен_
- ⬜ `monitoring/loki-grafana-interview.md` — 1 Q — _не проверен_
- ⬜ `monitoring/metrics-tracing-interview.md` — 6 Q — _не проверен_
- ⬜ `monitoring/micrometer-interview.md` — 1 Q — _не проверен_
- ⬜ `monitoring/observability-interview.md` — 15 Q — _не проверен_
- ⬜ `monitoring/opentelemetry-interview.md` — 9 Q — _не проверен_
- ⬜ `monitoring/prometheus-grafana-interview.md` — 39 Q — _не проверен_

### performance (7)

- ⬜ `performance/application-profiling-interview.md` — 42 Q — _не проверен_
- ⬜ `performance/caching-performance-interview.md` — 1 Q — _не проверен_
- ⬜ `performance/database-performance-interview.md` — 1 Q — _не проверен_
- ⬜ `performance/jvm-performance-tuning-interview.md` — 1 Q — _не проверен_
- ⬜ `performance/memory-management-interview.md` — 1 Q — _не проверен_
- ⬜ `performance/network-performance-interview.md` — 1 Q — _не проверен_
- ⬜ `performance/performance-testing-interview.md` — 1 Q — _не проверен_

### programming-languages (46)

- ⬜ `programming-languages/go/go-concurrency-interview.md` — 35 Q — _не проверен_
- ⬜ `programming-languages/go/go-generics-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/go/go-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/go/go-memory-gc-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/go/go-modules-interview.md` — 27 Q — _не проверен_
- ⬜ `programming-languages/go/go-stdlib-interview.md` — 30 Q — _не проверен_
- ⬜ `programming-languages/go/go-testing-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/java/java-17-21-interview.md` — 42 Q — _не проверен_
- ⬜ `programming-languages/java/java-8-interview.md` — 41 Q — _не проверен_
- ⬜ `programming-languages/java/java-annotations-interview.md` — 43 Q — _не проверен_
- ⬜ `programming-languages/java/java-collections-interview.md` — 46 Q — _не проверен_
- ⬜ `programming-languages/java/java-completable-future-interview.md` — 13 Q — _не проверен_
- ⬜ `programming-languages/java/java-concurrency-interview.md` — 56 Q — _не проверен_
- ⬜ `programming-languages/java/java-conditional-statements-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/java/java-core-interview.md` — 39 Q — _не проверен_
- ⬜ `programming-languages/java/java-exceptions-interview.md` — 42 Q — _не проверен_
- ⬜ `programming-languages/java/java-functional-interface-interview.md` — 14 Q — _не проверен_
- ⬜ `programming-languages/java/java-generics-interview.md` — 40 Q — _не проверен_
- ⬜ `programming-languages/java/java-initialization-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/java/java-io-nio-interview.md` — 40 Q — _не проверен_
- ⬜ `programming-languages/java/java-jackson-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/java/java-lombok-interview.md` — 5 Q — _не проверен_
- ⬜ `programming-languages/java/java-mapstruct-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/java/java-modules-interview.md` — 38 Q — _не проверен_
- ⬜ `programming-languages/java/java-oop-interview.md` — 43 Q — _не проверен_
- ⬜ `programming-languages/java/java-optional-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/java/java-pattern-matching-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/java/java-records-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/java/java-reflection-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/java/java-serialization-interview.md` — 40 Q — _не проверен_
- ⬜ `programming-languages/java/java-stream-interview.md` — 42 Q — _не проверен_
- ⬜ `programming-languages/java/java-string-interview.md` — 39 Q — _не проверен_
- ⬜ `programming-languages/java/java-types-interview.md` — 38 Q — _не проверен_
- ⬜ `programming-languages/java/java-virtual-threads-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-collections-interview.md` — 42 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-coroutines-interview.md` — 19 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-dsl-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-exceptions-interview.md` — 7 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-flow-interview.md` — 17 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-interop-java-interview.md` — 38 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-interview.md` — 27 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-sealed-classes-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-serialization-interview.md` — 1 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-spring-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/kotlin/kotlin-value-classes-interview.md` — 15 Q — _не проверен_
- ⬜ `programming-languages/scala/scala-interview.md` — 1 Q — _не проверен_

### reactive (6)

- ⬜ `reactive/project-reactor-interview.md` — 47 Q — _не проверен_
- ⬜ `reactive/reactive-patterns-interview.md` — 26 Q — _не проверен_
- ⬜ `reactive/reactive-streams-interview.md` — 30 Q — _не проверен_
- ⬜ `reactive/reactive-testing-interview.md` — 28 Q — _не проверен_
- ⬜ `reactive/rxjava-interview.md` — 46 Q — _не проверен_
- ⬜ `reactive/webflux-interview.md` — 28 Q — _не проверен_

### security (10)

- ⬜ `security/application-security-interview.md` — 45 Q — _не проверен_
- ⬜ `security/authentication-authorization-patterns-interview.md` — 45 Q — _не проверен_
- ⬜ `security/jwt-interview.md` — 43 Q — _не проверен_
- ⬜ `security/mtls-interview.md` — 20 Q — _не проверен_
- ⬜ `security/oauth2-interview.md` — 42 Q — _не проверен_
- ⬜ `security/owasp-top10-interview.md` — 45 Q — _не проверен_
- ⬜ `security/secrets-management-interview.md` — 22 Q — _не проверен_
- ⬜ `security/supply-chain-security-interview.md` — 24 Q — _не проверен_
- ⬜ `security/tls-ssl-interview.md` — 45 Q — _не проверен_
- ⬜ `security/zero-trust-interview.md` — 19 Q — _не проверен_

### system-design (22)

- ⬜ `system-design/design-chat-system-interview.md` — 21 Q — _не проверен_
- ⬜ `system-design/design-dropbox-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-ecommerce-delivery-interview.md` — 36 Q — _не проверен_
- ⬜ `system-design/design-elevator-oo-interview.md` — 26 Q — _не проверен_
- ⬜ `system-design/design-feed-system-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-google-maps-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-instagram-interview.md` — 27 Q — _не проверен_
- ⬜ `system-design/design-key-value-store-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-netflix-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-parking-lot-oo-interview.md` — 28 Q — _не проверен_
- ⬜ `system-design/design-pastebin-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-payment-system-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-rate-limiter-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-search-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-twitter-interview.md` — 27 Q — _не проверен_
- ⬜ `system-design/design-typeahead-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-uber-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-url-shortener-interview.md` — 30 Q — _не проверен_
- ⬜ `system-design/design-vending-machine-oo-interview.md` — 24 Q — _не проверен_
- ⬜ `system-design/design-web-crawler-interview.md` — 26 Q — _не проверен_
- ⬜ `system-design/design-youtube-interview.md` — 28 Q — _не проверен_
- ⬜ `system-design/system-design-interview.md` — 1 Q — _не проверен_

### testing (14)

- ⬜ `testing/chaos-engineering-interview.md` — 44 Q — _не проверен_
- ⬜ `testing/contract-testing-interview.md` — 20 Q — _не проверен_
- ⬜ `testing/integration-testing-interview.md` — 39 Q — _не проверен_
- ⬜ `testing/junit-interview.md` — 15 Q — _не проверен_
- ⬜ `testing/load-testing-interview.md` — 22 Q — _не проверен_
- ⬜ `testing/mockito-interview.md` — 45 Q — _не проверен_
- ⬜ `testing/mutation-testing-interview.md` — 20 Q — _не проверен_
- ⬜ `testing/property-based-testing-interview.md` — 1 Q — _не проверен_
- ⬜ `testing/rest-assured-interview.md` — 15 Q — _не проверен_
- ⬜ `testing/selenium-interview.md` — 15 Q — _не проверен_
- ⬜ `testing/test-automation-interview.md` — 7 Q — _не проверен_
- ⬜ `testing/test-strategies-interview.md` — 45 Q — _не проверен_
- ⬜ `testing/testcontainers-interview.md` — 40 Q — _не проверен_
- ⬜ `testing/unit-testing-interview.md` — 45 Q — _не проверен_
