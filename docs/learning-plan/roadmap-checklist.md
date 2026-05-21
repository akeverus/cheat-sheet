# Roadmap — полный чек-лист самообучения

> Источник правды для прогресса по 67 циклам. Singularity T-84507082 содержит только high-level (1 чекбокс на цикл), детали — здесь.
> Контекст и стратегия: [2026-05-20-self-learning-plan.md](2026-05-20-self-learning-plan.md)

## Phase 1 — Priority (диагностика)

- [ ] Cycle 1 — System Design & Highload (DDIA)
  - [ ] Источники: свои шпаргалки
    - [ ] system-design-interview.md (повтор базы, 2956 строк)
    - [ ] design-payment-system-interview.md
    - [ ] design-chat-system-interview.md
    - [ ] design-feed-system-interview.md
    - [ ] design-rate-limiter-interview.md
    - [ ] design-search-interview.md
    - [ ] design-url-shortener-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] DDIA глава 1 — Reliable / Scalable / Maintainable
    - [ ] DDIA глава 2 — Data Models and Query Languages
    - [ ] DDIA глава 3 — Storage and Retrieval
    - [ ] Закладка: Backend Roadmap
    - [ ] Закладка: backend-cheats README
  - [ ] Выписать holes (что непонятно)
    - [ ] CAP / PACELC trade-offs в моих сервисах
    - [ ] Sharding: hash vs range — когда применять
    - [ ] Replication: sync vs async — последствия
    - [ ] Caching: cache-aside vs write-through vs write-behind
    - [ ] Event sourcing: когда оправдано
  - [ ] Hands-on
    - [ ] Mermaid: URL shortener для 10k QPS
    - [ ] Mermaid: Rate limiter (token bucket + sliding window)
    - [ ] Writeup: load estimation шаблон
  - [ ] Quiz & commit
    - [ ] 20 MCQ из quiz-app topic=system-design ≥80%
    - [ ] Расширить cheatsheets/system-design/<тема>.md ≥300 строк
    - [ ] git commit learning(system-design)

- [ ] Cycle 2 — Algorithms — Data Structures I (Trees focus)
  - [ ] Источники: свои шпаргалки
    - [ ] trees-interview.md
    - [ ] graphs-interview.md
    - [ ] linked-lists-interview.md
    - [ ] hash-tables-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Закладка: VisuAlgo (визуализация)
    - [ ] Закладка: Algorithm Visualizer
    - [ ] Закладка: Big O Cheat Sheet
    - [ ] LeetCode Medium по Trees (25 задач)
  - [ ] Выписать holes (что непонятно)
    - [ ] Self-balancing trees: AVL vs Red-Black trade-offs
    - [ ] B-tree vs B+ tree (где какие индексы)
    - [ ] Trie применение в реальных задачах
    - [ ] Heap: max-heap vs min-heap, операции
  - [ ] Hands-on
    - [ ] Реализовать BST с балансировкой
    - [ ] LeetCode: 25 задач Medium на Trees
    - [ ] Решение через VisuAlgo (визуальный разбор 5 алгоритмов)
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=algorithms/data-structures/trees-interview ≥85%
    - [ ] Расширить cheatsheets/algorithms/<...>.md
    - [ ] git commit learning(algorithms-ds)

- [ ] Cycle 3 — AI/LLM Foundations
  - [ ] Источники: свои шпаргалки
    - [ ] ai-agents-interview.md
    - [ ] embeddings-interview.md
    - [ ] llm-basics-interview.md
    - [ ] llm-integration-patterns-interview.md
    - [ ] mlops-interview.md
    - [ ] model-serving-interview.md
    - [ ] prompt-engineering-interview.md
    - [ ] rag-interview.md
    - [ ] vector-databases-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Закладка: awesome-ai-memory
    - [ ] Закладка: system-prompts-and-models
    - [ ] Закладка: awesome-cursorrules
    - [ ] Закладка: Claude Code Docs
    - [ ] Закладка: backend / claude-skills (GitLab)
  - [ ] Выписать holes (что непонятно)
    - [ ] Токенизация: BPE vs WordPiece vs SentencePiece
    - [ ] Attention: self vs cross, MHA, GQA
    - [ ] Context window и его расширение (RoPE, sliding window)
    - [ ] Embeddings: dense vs sparse, cosine vs dot product
    - [ ] RAG: chunking strategies, reranking
    - [ ] Prompt engineering: few-shot, CoT, structured outputs
  - [ ] Hands-on
    - [ ] Написать RAG-pipeline на Python (50 строк)
    - [ ] Свой prompt-template для конкретного use-case
  - [ ] Quiz & commit
    - [ ] 30 MCQ topic=ai-ml ≥80%
    - [ ] Создать cheatsheets/ai-ml/llm-fundamentals.md ≥500 строк
    - [ ] git commit feat(ai-ml): llm-fundamentals

- [ ] Cycle 4 — Kubernetes deep
  - [ ] Источники: свои шпаргалки
    - [ ] kubernetes-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Закладка: Тренажёр Kubernetes
    - [ ] Закладки DevOps > Kubernetes (6 ссылок)
    - [ ] Закладки DevOps > Argo CD (5 ссылок)
  - [ ] Выписать holes (что непонятно)
    - [ ] Controllers: Deployment vs StatefulSet vs DaemonSet
    - [ ] Scheduling: affinity, anti-affinity, taints
    - [ ] Networking: Service types, Ingress, NetworkPolicy
    - [ ] RBAC: Roles, ClusterRoles, ServiceAccounts
    - [ ] Storage: PV, PVC, StorageClass, CSI
  - [ ] Hands-on
    - [ ] Запустить kind/minikube локально
    - [ ] Развернуть 5 манифестов разных типов
    - [ ] Debug упавшего pod'а (kubectl describe, logs, exec)
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=devops/kubernetes ≥80%
    - [ ] cheatsheets/devops/kubernetes-deep.md ≥500 строк
    - [ ] git commit learning(k8s)

- [ ] Cycle 5 — Data Engineering — Kafka Streams
  - [ ] Источники: свои шпаргалки
    - [ ] kafka-streams-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Apache Kafka docs (закладка)
    - [ ] Kafka Streams DSL guide
  - [ ] Выписать holes (что непонятно)
    - [ ] Streams vs Consumer API: когда что
    - [ ] Windowing: tumbling, hopping, session
    - [ ] State stores: in-memory vs RocksDB
    - [ ] Exactly-once семантика
  - [ ] Hands-on
    - [ ] Минимальное Streams-приложение на Java
    - [ ] Windowed aggregation пример
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=data-engineering/kafka-streams ≥80%
    - [ ] Расширить kafka-streams-interview.md ≥800 строк
    - [ ] git commit learning(de-kafka-streams)

- [ ] Cycle 6 — Go Concurrency
  - [ ] Источники: свои шпаргалки
    - [ ] go-concurrency-interview.md
    - [ ] go-generics-interview.md
    - [ ] go-interview.md
    - [ ] go-memory-gc-interview.md
    - [ ] go-modules-interview.md
    - [ ] go-stdlib-interview.md
    - [ ] go-testing-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Закладки Documentation > Go
  - [ ] Выписать holes (что непонятно)
    - [ ] Goroutines vs threads — модель
    - [ ] Channels: buffered vs unbuffered
    - [ ] sync package: WaitGroup, Mutex, Once
    - [ ] Context для cancellation
    - [ ] Race conditions: race detector
  - [ ] Hands-on
    - [ ] CLI-утилита на Go для парсинга diagnostic/*.tsv
    - [ ] Pipeline через channels (3 stage)
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/go ≥80%
    - [ ] go-concurrency-interview.md ≥500 строк
    - [ ] git commit learning(go-concurrency)

- [ ] Cycle 7 — Reactive Streams (Reactor + RxJava)
  - [ ] Источники: свои шпаргалки
    - [ ] project-reactor-interview.md
    - [ ] reactive-patterns-interview.md
    - [ ] reactive-streams-interview.md
    - [ ] reactive-testing-interview.md
    - [ ] rxjava-interview.md
    - [ ] webflux-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Project Reactor docs
    - [ ] RxJava wiki
  - [ ] Выписать holes (что непонятно)
    - [ ] Backpressure стратегии (DROP, BUFFER, LATEST)
    - [ ] Hot vs Cold publishers
    - [ ] Schedulers: when to use which
    - [ ] Error handling в reactive chain
    - [ ] Testing reactive code (StepVerifier)
  - [ ] Hands-on
    - [ ] 5 pipeline-задач Reactor
    - [ ] Конвертация callback-API в Mono/Flux
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=reactive ≥80%
    - [ ] project-reactor-interview.md ≥600 строк
    - [ ] git commit learning(reactive)

- [ ] Cycle 8 — Database Architecture deep
  - [ ] Источники: свои шпаргалки
    - [ ] database-architecture-interview.md
    - [ ] postgresql-interview.md
    - [ ] postgresql-interview.md
    - [ ] sql-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Закладки Documentation > Databases (5)
  - [ ] Выписать holes (что непонятно)
    - [ ] MVCC: PostgreSQL подробно
    - [ ] Indexes: B-tree, hash, GIN, GiST — когда что
    - [ ] Query plans: EXPLAIN ANALYZE интерпретация
    - [ ] Partitioning: declarative vs inheritance
    - [ ] Replication: physical vs logical
  - [ ] Hands-on
    - [ ] Анализ EXPLAIN на 5 типах запросов
    - [ ] Локальный postgres с partitioning setup
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=databases/database-architecture ≥85%
    - [ ] database-architecture-interview.md ≥700 строк
    - [ ] git commit learning(db-arch)

- [ ] Cycle 9 — Spring Cloud / Microservices
  - [ ] Источники: свои шпаргалки
    - [ ] spring-cloud-interview.md
  - [ ] Источники: внешние / закладки
    - [ ] Закладки Documentation > Spring Framework (17)
  - [ ] Выписать holes (что непонятно)
    - [ ] Service Discovery: Eureka vs Consul vs K8s native
    - [ ] Config Server: refresh strategies
    - [ ] Circuit Breaker: Resilience4j patterns
    - [ ] API Gateway: routing, filters
    - [ ] Distributed Tracing: Sleuth/Micrometer integration
  - [ ] Hands-on
    - [ ] Минимальный микросервисный стенд (Gateway + 2 service + Eureka)
    - [ ] Circuit breaker demo с симуляцией отказа
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=frameworks/spring/spring-cloud ≥80%
    - [ ] spring-cloud-interview.md ≥600 строк
    - [ ] git commit learning(spring-cloud)

## Phase 2 — Algorithms coverage

- [ ] Cycle 10 — Algorithmic Paradigms (DP, Backtracking, Greedy, D&C) (6 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/algorithms/algorithmic-paradigms/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=algorithms/algorithmic-paradigms ≥80% + commit

- [ ] Cycle 11 — Sorting & Searching + Big-O (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/algorithms/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=algorithms ≥80% + commit

- [ ] Cycle 12 — Data Structures II (Graphs, Hash, Heaps, Tries, Stacks) (8 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/algorithms/data-structures/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=algorithms/data-structures ≥80% + commit

## Phase 3 — Java Core deep

- [ ] Cycle 13 — Java Concurrency deep (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/java/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/java/java-concurrency ≥80% + commit

- [ ] Cycle 14 — Java Collections + Streams API (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/java/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/java/java-collections ≥80% + commit

- [ ] Cycle 15 — Java Modern (Optional, CompletableFuture, Records, Sealed) (4 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/java/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/java ≥80% + commit

- [ ] Cycle 16 — Java Memory Model + I/O / NIO (10 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/java/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/java ≥80% + commit

## Phase 4 — Kotlin

- [ ] Cycle 17 — Kotlin Language (sealed, data, scope, DSL) (11 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/kotlin/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/kotlin ≥80% + commit

- [ ] Cycle 18 — Kotlin Coroutines + Flow (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/kotlin/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/kotlin ≥80% + commit

## Phase 5 — Go coverage

- [ ] Cycle 19 — Go fundamentals + testing + idioms (7 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/programming-languages/go/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=programming-languages/go ≥80% + commit

## Phase 6 — Spring deep

- [ ] Cycle 20 — Spring Core (IoC, AOP, Beans, profiles) (6 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/frameworks/spring/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=frameworks/spring ≥80% + commit

- [ ] Cycle 21 — Spring Data (JPA, R2DBC, transactions) (4 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/frameworks/spring/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=frameworks/spring ≥80% + commit

- [ ] Cycle 22 — Spring Security (OAuth2, JWT, method security) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/frameworks/spring/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=frameworks/spring ≥80% + commit

- [ ] Cycle 23 — Spring Batch / Modulith / WebFlux / Integration (4 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/frameworks/spring/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=frameworks/spring ≥80% + commit

## Phase 7 — Databases deep

- [ ] Cycle 24 — PostgreSQL deep (MVCC, indexes, query plans, partitioning) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/databases/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=databases/postgresql ≥80% + commit

- [ ] Cycle 25 — SQL Patterns (joins, CTEs, window functions) (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/databases/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=databases/sql ≥80% + commit

- [ ] Cycle 26 — NoSQL: MongoDB + Cassandra (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/databases/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=databases ≥80% + commit

- [ ] Cycle 27 — Redis + Elasticsearch + Hibernate caching (6 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/databases/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=databases ≥80% + commit

## Phase 8 — Architecture

- [ ] Cycle 28 — Hexagonal / Clean / DDD (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/architecture/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=architecture ≥80% + commit

- [ ] Cycle 29 — CQRS / Event Sourcing / Saga (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/architecture/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=architecture ≥80% + commit

- [ ] Cycle 30 — Resilience Patterns (CB, retry, bulkhead) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/architecture/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=architecture ≥80% + commit

- [ ] Cycle 31 — Networking + API Gateway + Load Balancing (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/architecture/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=architecture ≥80% + commit

## Phase 9 — Data Engineering coverage

- [ ] Cycle 32 — Apache Spark (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/data-engineering/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=data-engineering ≥80% + commit

- [ ] Cycle 33 — Apache Flink (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/data-engineering/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=data-engineering ≥80% + commit

- [ ] Cycle 34 — Data Lake / Lakehouse (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/data-engineering/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=data-engineering ≥80% + commit

## Phase 10 — Messaging & Search

- [ ] Cycle 35 — Kafka core (brokers, partitions, EOS) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/messaging/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=messaging ≥80% + commit

- [ ] Cycle 36 — Messaging ecosystem (Redpanda, RabbitMQ, ActiveMQ, Solr) (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/messaging/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=messaging ≥80% + commit

## Phase 11 — API

- [ ] Cycle 37 — REST + HTTP + REST Maturity (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/api/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=api ≥80% + commit

- [ ] Cycle 38 — GraphQL + gRPC (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/api/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=api ≥80% + commit

- [ ] Cycle 39 — API Versioning + OpenAPI + Design (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/api/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=api ≥80% + commit

## Phase 12 — Cloud

- [ ] Cycle 40 — AWS Core (EC2, S3, IAM, RDS, Lambda, VPC) (6 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/cloud/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=cloud ≥80% + commit

## Phase 13 — DevOps & Containers

- [ ] Cycle 41 — Docker / Containers / multi-stage (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/devops/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=devops ≥80% + commit

- [ ] Cycle 42 — CI/CD
  - [ ] 1. шпаргалка (cheatsheets/interview/devops/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=cicd ≥80% + commit

- [ ] Cycle 43 — Service Mesh + GitOps (Linkerd, Istio, ArgoCD) (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/devops/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=devops ≥80% + commit

## Phase 14 — Observability

- [ ] Cycle 44 — Metrics (Prometheus, Grafana, Victoria Metrics) (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/monitoring/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=monitoring ≥80% + commit

- [ ] Cycle 45 — Logging (ELK, Loki, structured) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/logging/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=logging ≥80% + commit

- [ ] Cycle 46 — Tracing & APM (OpenTelemetry, Jaeger) (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/monitoring/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=monitoring ≥80% + commit

## Phase 15 — Security

- [ ] Cycle 47 — OWASP Top 10 + AppSec (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/security/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=security ≥80% + commit

- [ ] Cycle 48 — AuthN/Z (OAuth2, OIDC, JWT, SAML, RBAC/ABAC) (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/security/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=security ≥80% + commit

- [ ] Cycle 49 — TLS/SSL + Zero Trust (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/security/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=security ≥80% + commit

## Phase 16 — Testing

- [ ] Cycle 50 — Test Strategies (Pyramid, Trophy) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/testing/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=testing ≥80% + commit

- [ ] Cycle 51 — Unit + Integration (JUnit, Mockito, Spring Test) (4 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/testing/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=testing ≥80% + commit

- [ ] Cycle 52 — E2E + Performance + Chaos + Test Automation (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/testing/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=testing ≥80% + commit

## Phase 17 — Performance

- [ ] Cycle 53 — JVM tuning & profiling (GC, JIT) (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/performance/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=performance ≥80% + commit

- [ ] Cycle 54 — App performance (DB, caching, network) (3 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/performance/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=performance ≥80% + commit

## Phase 18 — JVM + Alt Frameworks

- [ ] Cycle 55 — JVM internals + GraalVM Native (2 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/jvm/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=jvm ≥80% + commit

- [ ] Cycle 56 — Micronaut & Quarkus (4 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/frameworks/jvm-alternatives/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=frameworks/jvm-alternatives ≥80% + commit

## Phase 19 — Code Quality + Patterns

- [ ] Cycle 57 — Design Patterns (GoF) (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/design-patterns/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=design-patterns ≥80% + commit

- [ ] Cycle 58 — Refactoring + SOLID + Code Review (7 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/code-quality/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=code-quality ≥80% + commit

## Phase 20 — Behavioral & Leadership

- [ ] Cycle 59 — Behavioral STAR (Conflict, Failure, Culture-fit) (6 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/behavioral/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=behavioral ≥80% + commit

- [ ] Cycle 60 — Leadership (Mentoring, Tech-decisions, Estimations) (7 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/leadership/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=leadership ≥80% + commit

## Phase 21 — Trainers (hands-on)

- [ ] Cycle 61 — SQL Trainer (LeetCode SQL + analytical)
  - [ ] 1. шпаргалка
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=databases/sql ≥80% + commit

- [ ] Cycle 62 — Git Trainer (rebase, bisect, reflog)
  - [ ] 1. шпаргалка
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. MCQ + commit

- [ ] Cycle 63 — Docker Trainer
  - [ ] 1. шпаргалка
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=devops ≥80% + commit

- [ ] Cycle 64 — Kubernetes Trainer (kind/minikube)
  - [ ] 1. шпаргалка
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=devops/kubernetes ≥80% + commit

- [ ] Cycle 65 — DSA Visualizer (VisuAlgo + Algorithm Visualizer)
  - [ ] 1. шпаргалка
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=algorithms ≥80% + commit

## Phase 22 — Misc

- [ ] Cycle 66 — Interview preparation meta (1 файлов)
  - [ ] 1. шпаргалка (cheatsheets/interview/preparation/)
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. 20 MCQ topic=preparation ≥80% + commit

- [ ] Cycle 67 — Cheat-sheet maintenance & meta-skills
  - [ ] 1. шпаргалка
  - [ ] 2. закладки + holes
  - [ ] 3. закрыть holes
  - [ ] 4. hands-on
  - [ ] 5. MCQ + commit

