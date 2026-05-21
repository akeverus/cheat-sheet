# Roadmap Delta draft v2 для T-84507082

**Цель:** заменить пустой `[{"insert":"\n"}]` в `N-T-84507082-aa58-4a6c-b5e7-af3df685f100` на полный иерархический Roadmap.

## Изменения v1 → v2

- Глубокая вложенность (3 уровня) через `indent` атрибут на list-items
- Циклы без привязки к датам
- Phase 1 (top-9) — детальные подпункты с конкретными источниками, holes, hands-on
- Phase 2-22 — список конкретных файлов из `interview/<category>/` + generic шаблон

## Параметры

- Размер: 111008 байт (~108 КБ)
- Операций Delta: 1848
- Чек-боксов всего: 865
- Иерархия: Phase (bold heading) → Cycle (level 0) → Section (level 1) → Item (level 2)

## ASCII preview (полный)

```
План профессионального развития




━━━ Phase 1 — Priority (диагностика) ━━━

☐ Cycle 1 — System Design & Highload (DDIA)
  ☐ Источники: свои шпаргалки
    ☐ system-design-interview.md (повтор базы, 2956 строк)
    ☐ design-payment-system-interview.md
    ☐ design-chat-system-interview.md
    ☐ design-feed-system-interview.md
    ☐ design-rate-limiter-interview.md
    ☐ design-search-interview.md
    ☐ design-url-shortener-interview.md
  ☐ Источники: внешние / закладки
    ☐ DDIA глава 1 — Reliable / Scalable / Maintainable
    ☐ DDIA глава 2 — Data Models and Query Languages
    ☐ DDIA глава 3 — Storage and Retrieval
    ☐ Закладка: Backend Roadmap
    ☐ Закладка: backend-cheats README
  ☐ Выписать holes (что непонятно)
    ☐ CAP / PACELC trade-offs в моих сервисах
    ☐ Sharding: hash vs range — когда применять
    ☐ Replication: sync vs async — последствия
    ☐ Caching: cache-aside vs write-through vs write-behind
    ☐ Event sourcing: когда оправдано
  ☐ Hands-on
    ☐ Mermaid: URL shortener для 10k QPS
    ☐ Mermaid: Rate limiter (token bucket + sliding window)
    ☐ Writeup: load estimation шаблон
  ☐ Quiz & commit
    ☐ 20 MCQ из quiz-app topic=system-design ≥80%
    ☐ Расширить cheatsheets/system-design/<тема>.md ≥300 строк
    ☐ git commit learning(system-design)

☐ Cycle 2 — Algorithms — Data Structures I (Trees focus)
  ☐ Источники: свои шпаргалки
    ☐ trees-interview.md
    ☐ graphs-interview.md
    ☐ linked-lists-interview.md
    ☐ hash-tables-interview.md
  ☐ Источники: внешние / закладки
    ☐ Закладка: VisuAlgo (визуализация)
    ☐ Закладка: Algorithm Visualizer
    ☐ Закладка: Big O Cheat Sheet
    ☐ LeetCode Medium по Trees (25 задач)
  ☐ Выписать holes (что непонятно)
    ☐ Self-balancing trees: AVL vs Red-Black trade-offs
    ☐ B-tree vs B+ tree (где какие индексы)
    ☐ Trie применение в реальных задачах
    ☐ Heap: max-heap vs min-heap, операции
  ☐ Hands-on
    ☐ Реализовать BST с балансировкой
    ☐ LeetCode: 25 задач Medium на Trees
    ☐ Решение через VisuAlgo (визуальный разбор 5 алгоритмов)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=algorithms/data-structures/trees-interview ≥85%
    ☐ Расширить cheatsheets/algorithms/<...>.md
    ☐ git commit learning(algorithms-ds)

☐ Cycle 3 — AI/LLM Foundations
  ☐ Источники: свои шпаргалки
    ☐ ai-agents-interview.md
    ☐ embeddings-interview.md
    ☐ llm-basics-interview.md
    ☐ llm-integration-patterns-interview.md
    ☐ mlops-interview.md
    ☐ model-serving-interview.md
    ☐ prompt-engineering-interview.md
    ☐ rag-interview.md
    ☐ vector-databases-interview.md
  ☐ Источники: внешние / закладки
    ☐ Закладка: awesome-ai-memory
    ☐ Закладка: system-prompts-and-models
    ☐ Закладка: awesome-cursorrules
    ☐ Закладка: Claude Code Docs
    ☐ Закладка: backend / claude-skills (GitLab)
  ☐ Выписать holes (что непонятно)
    ☐ Токенизация: BPE vs WordPiece vs SentencePiece
    ☐ Attention: self vs cross, MHA, GQA
    ☐ Context window и его расширение (RoPE, sliding window)
    ☐ Embeddings: dense vs sparse, cosine vs dot product
    ☐ RAG: chunking strategies, reranking
    ☐ Prompt engineering: few-shot, CoT, structured outputs
  ☐ Hands-on
    ☐ Написать RAG-pipeline на Python (50 строк)
    ☐ Свой prompt-template для конкретного use-case
  ☐ Quiz & commit
    ☐ 30 MCQ topic=ai-ml ≥80%
    ☐ Создать cheatsheets/ai-ml/llm-fundamentals.md ≥500 строк
    ☐ git commit feat(ai-ml): llm-fundamentals

☐ Cycle 4 — Kubernetes deep
  ☐ Источники: свои шпаргалки
    ☐ kubernetes-interview.md
  ☐ Источники: внешние / закладки
    ☐ Закладка: Тренажёр Kubernetes
    ☐ Закладки DevOps > Kubernetes (6 ссылок)
    ☐ Закладки DevOps > Argo CD (5 ссылок)
  ☐ Выписать holes (что непонятно)
    ☐ Controllers: Deployment vs StatefulSet vs DaemonSet
    ☐ Scheduling: affinity, anti-affinity, taints
    ☐ Networking: Service types, Ingress, NetworkPolicy
    ☐ RBAC: Roles, ClusterRoles, ServiceAccounts
    ☐ Storage: PV, PVC, StorageClass, CSI
  ☐ Hands-on
    ☐ Запустить kind/minikube локально
    ☐ Развернуть 5 манифестов разных типов
    ☐ Debug упавшего pod'а (kubectl describe, logs, exec)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=devops/kubernetes ≥80%
    ☐ cheatsheets/devops/kubernetes-deep.md ≥500 строк
    ☐ git commit learning(k8s)

☐ Cycle 5 — Data Engineering — Kafka Streams
  ☐ Источники: свои шпаргалки
    ☐ kafka-streams-interview.md
  ☐ Источники: внешние / закладки
    ☐ Apache Kafka docs (закладка)
    ☐ Kafka Streams DSL guide
  ☐ Выписать holes (что непонятно)
    ☐ Streams vs Consumer API: когда что
    ☐ Windowing: tumbling, hopping, session
    ☐ State stores: in-memory vs RocksDB
    ☐ Exactly-once семантика
  ☐ Hands-on
    ☐ Минимальное Streams-приложение на Java
    ☐ Windowed aggregation пример
  ☐ Quiz & commit
    ☐ 20 MCQ topic=data-engineering/kafka-streams ≥80%
    ☐ Расширить kafka-streams-interview.md ≥800 строк
    ☐ git commit learning(de-kafka-streams)

☐ Cycle 6 — Go Concurrency
  ☐ Источники: свои шпаргалки
    ☐ go-concurrency-interview.md
    ☐ go-generics-interview.md
    ☐ go-interview.md
    ☐ go-memory-gc-interview.md
    ☐ go-modules-interview.md
    ☐ go-stdlib-interview.md
    ☐ go-testing-interview.md
  ☐ Источники: внешние / закладки
    ☐ Закладки Documentation > Go
  ☐ Выписать holes (что непонятно)
    ☐ Goroutines vs threads — модель
    ☐ Channels: buffered vs unbuffered
    ☐ sync package: WaitGroup, Mutex, Once
    ☐ Context для cancellation
    ☐ Race conditions: race detector
  ☐ Hands-on
    ☐ CLI-утилита на Go для парсинга diagnostic/*.tsv
    ☐ Pipeline через channels (3 stage)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/go ≥80%
    ☐ go-concurrency-interview.md ≥500 строк
    ☐ git commit learning(go-concurrency)

☐ Cycle 7 — Reactive Streams (Reactor + RxJava)
  ☐ Источники: свои шпаргалки
    ☐ project-reactor-interview.md
    ☐ reactive-patterns-interview.md
    ☐ reactive-streams-interview.md
    ☐ reactive-testing-interview.md
    ☐ rxjava-interview.md
    ☐ webflux-interview.md
  ☐ Источники: внешние / закладки
    ☐ Project Reactor docs
    ☐ RxJava wiki
  ☐ Выписать holes (что непонятно)
    ☐ Backpressure стратегии (DROP, BUFFER, LATEST)
    ☐ Hot vs Cold publishers
    ☐ Schedulers: when to use which
    ☐ Error handling в reactive chain
    ☐ Testing reactive code (StepVerifier)
  ☐ Hands-on
    ☐ 5 pipeline-задач Reactor
    ☐ Конвертация callback-API в Mono/Flux
  ☐ Quiz & commit
    ☐ 20 MCQ topic=reactive ≥80%
    ☐ project-reactor-interview.md ≥600 строк
    ☐ git commit learning(reactive)

☐ Cycle 8 — Database Architecture deep
  ☐ Источники: свои шпаргалки
    ☐ database-architecture-interview.md
    ☐ postgresql-interview.md
    ☐ postgresql-interview.md
    ☐ sql-interview.md
  ☐ Источники: внешние / закладки
    ☐ Закладки Documentation > Databases (5)
  ☐ Выписать holes (что непонятно)
    ☐ MVCC: PostgreSQL подробно
    ☐ Indexes: B-tree, hash, GIN, GiST — когда что
    ☐ Query plans: EXPLAIN ANALYZE интерпретация
    ☐ Partitioning: declarative vs inheritance
    ☐ Replication: physical vs logical
  ☐ Hands-on
    ☐ Анализ EXPLAIN на 5 типах запросов
    ☐ Локальный postgres с partitioning setup
  ☐ Quiz & commit
    ☐ 20 MCQ topic=databases/database-architecture ≥85%
    ☐ database-architecture-interview.md ≥700 строк
    ☐ git commit learning(db-arch)

☐ Cycle 9 — Spring Cloud / Microservices
  ☐ Источники: свои шпаргалки
    ☐ spring-cloud-interview.md
  ☐ Источники: внешние / закладки
    ☐ Закладки Documentation > Spring Framework (17)
  ☐ Выписать holes (что непонятно)
    ☐ Service Discovery: Eureka vs Consul vs K8s native
    ☐ Config Server: refresh strategies
    ☐ Circuit Breaker: Resilience4j patterns
    ☐ API Gateway: routing, filters
    ☐ Distributed Tracing: Sleuth/Micrometer integration
  ☐ Hands-on
    ☐ Минимальный микросервисный стенд (Gateway + 2 service + Eureka)
    ☐ Circuit breaker demo с симуляцией отказа
  ☐ Quiz & commit
    ☐ 20 MCQ topic=frameworks/spring/spring-cloud ≥80%
    ☐ spring-cloud-interview.md ≥600 строк
    ☐ git commit learning(spring-cloud)

━━━ Phase 2 — Algorithms coverage ━━━

☐ Cycle 10 — Algorithmic Paradigms (DP, Backtracking, Greedy, D&C)
  ☐ Источники: свои шпаргалки (algorithms/algorithmic-paradigms/)
    ☐ backtracking-interview.md
    ☐ divide-and-conquer-interview.md
    ☐ dynamic-programming-interview.md
    ☐ greedy-algorithms-interview.md
    ☐ recursion-interview.md
    ☐ two-pointers-sliding-window-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=algorithms/algorithmic-paradigms ≥80%
    ☐ Расширить cheatsheet в cheatsheets/algorithms/algorithmic-paradigms/
    ☐ git commit learning(<topic>)

☐ Cycle 11 — Sorting & Searching + Big-O
  ☐ Источники: свои шпаргалки (algorithms/)
    ☐ complexity/complexity-analysis-interview.md
    ☐ sorting-searching/searching-algorithms-interview.md
    ☐ sorting-searching/sorting-algorithms-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=algorithms ≥80%
    ☐ Расширить cheatsheet в cheatsheets/algorithms/
    ☐ git commit learning(<topic>)

☐ Cycle 12 — Data Structures II (Graphs, Hash, Heaps, Tries, Stacks)
  ☐ Источники: свои шпаргалки (algorithms/data-structures/)
    ☐ arrays-strings-interview.md
    ☐ graphs-interview.md
    ☐ hash-tables-interview.md
    ☐ heaps-interview.md
    ☐ linked-lists-interview.md
    ☐ stacks-queues-interview.md
    ☐ trees-interview.md
    ☐ tries-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=algorithms/data-structures ≥80%
    ☐ Расширить cheatsheet в cheatsheets/algorithms/data-structures/
    ☐ git commit learning(<topic>)

━━━ Phase 3 — Java Core deep ━━━

☐ Cycle 13 — Java Concurrency deep
  ☐ Источники: свои шпаргалки (programming-languages/java/)
    ☐ java-concurrency-interview.md
    ☐ java-virtual-threads-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/java/java-concurrency ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/java/
    ☐ git commit learning(<topic>)

☐ Cycle 14 — Java Collections + Streams API
  ☐ Источники: свои шпаргалки (programming-languages/java/)
    ☐ java-collections-interview.md
    ☐ java-stream-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/java/java-collections ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/java/
    ☐ git commit learning(<topic>)

☐ Cycle 15 — Java Modern (Optional, CompletableFuture, Records, Sealed)
  ☐ Источники: свои шпаргалки (programming-languages/java/)
    ☐ java-completable-future-interview.md
    ☐ java-optional-interview.md
    ☐ java-pattern-matching-interview.md
    ☐ java-records-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/java ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/java/
    ☐ git commit learning(<topic>)

☐ Cycle 16 — Java Memory Model + I/O / NIO
  ☐ Источники: свои шпаргалки (programming-languages/java/)
    ☐ java-annotations-interview.md
    ☐ java-collections-interview.md
    ☐ java-conditional-statements-interview.md
    ☐ java-exceptions-interview.md
    ☐ java-functional-interface-interview.md
    ☐ java-initialization-interview.md
    ☐ java-io-nio-interview.md
    ☐ java-optional-interview.md
    ☐ java-reflection-interview.md
    ☐ java-serialization-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/java ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/java/
    ☐ git commit learning(<topic>)

━━━ Phase 4 — Kotlin ━━━

☐ Cycle 17 — Kotlin Language (sealed, data, scope, DSL)
  ☐ Источники: свои шпаргалки (programming-languages/kotlin/)
    ☐ kotlin-collections-interview.md
    ☐ kotlin-coroutines-interview.md
    ☐ kotlin-dsl-interview.md
    ☐ kotlin-exceptions-interview.md
    ☐ kotlin-flow-interview.md
    ☐ kotlin-interop-java-interview.md
    ☐ kotlin-interview.md
    ☐ kotlin-sealed-classes-interview.md
    ☐ kotlin-serialization-interview.md
    ☐ kotlin-spring-interview.md
    ☐ kotlin-value-classes-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/kotlin ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/kotlin/
    ☐ git commit learning(<topic>)

☐ Cycle 18 — Kotlin Coroutines + Flow
  ☐ Источники: свои шпаргалки (programming-languages/kotlin/)
    ☐ kotlin-coroutines-interview.md
    ☐ kotlin-flow-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/kotlin ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/kotlin/
    ☐ git commit learning(<topic>)

━━━ Phase 5 — Go coverage ━━━

☐ Cycle 19 — Go fundamentals + testing + idioms
  ☐ Источники: свои шпаргалки (programming-languages/go/)
    ☐ go-concurrency-interview.md
    ☐ go-generics-interview.md
    ☐ go-interview.md
    ☐ go-memory-gc-interview.md
    ☐ go-modules-interview.md
    ☐ go-stdlib-interview.md
    ☐ go-testing-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=programming-languages/go ≥80%
    ☐ Расширить cheatsheet в cheatsheets/programming-languages/go/
    ☐ git commit learning(<topic>)

━━━ Phase 6 — Spring deep ━━━

☐ Cycle 20 — Spring Core (IoC, AOP, Beans, profiles)
  ☐ Источники: свои шпаргалки (frameworks/spring/)
    ☐ spring-aop-interview.md
    ☐ spring-async-interview.md
    ☐ spring-boot-3-migration-interview.md
    ☐ spring-boot-actuator-interview.md
    ☐ spring-boot-interview.md
    ☐ spring-framework-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=frameworks/spring ≥80%
    ☐ Расширить cheatsheet в cheatsheets/frameworks/spring/
    ☐ git commit learning(<topic>)

☐ Cycle 21 — Spring Data (JPA, R2DBC, transactions)
  ☐ Источники: свои шпаргалки (frameworks/spring/)
    ☐ spring-data-jdbc-interview.md
    ☐ spring-data-jpa-interview.md
    ☐ spring-r2dbc-interview.md
    ☐ spring-transaction-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=frameworks/spring ≥80%
    ☐ Расширить cheatsheet в cheatsheets/frameworks/spring/
    ☐ git commit learning(<topic>)

☐ Cycle 22 — Spring Security (OAuth2, JWT, method security)
  ☐ Источники: свои шпаргалки (frameworks/spring/)
    ☐ spring-security-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=frameworks/spring ≥80%
    ☐ Расширить cheatsheet в cheatsheets/frameworks/spring/
    ☐ git commit learning(<topic>)

☐ Cycle 23 — Spring Batch / Modulith / WebFlux / Integration
  ☐ Источники: свои шпаргалки (frameworks/spring/)
    ☐ spring-batch-interview.md
    ☐ spring-integration-interview.md
    ☐ spring-modulith-interview.md
    ☐ spring-webflux-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=frameworks/spring ≥80%
    ☐ Расширить cheatsheet в cheatsheets/frameworks/spring/
    ☐ git commit learning(<topic>)

━━━ Phase 7 — Databases deep ━━━

☐ Cycle 24 — PostgreSQL deep (MVCC, indexes, query plans, partitioning)
  ☐ Источники: свои шпаргалки (databases/)
    ☐ postgresql-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=databases/postgresql ≥80%
    ☐ Расширить cheatsheet в cheatsheets/databases/
    ☐ git commit learning(<topic>)

☐ Cycle 25 — SQL Patterns (joins, CTEs, window functions)
  ☐ Источники: свои шпаргалки (databases/)
    ☐ postgresql-interview.md
    ☐ sql-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=databases/sql ≥80%
    ☐ Расширить cheatsheet в cheatsheets/databases/
    ☐ git commit learning(<topic>)

☐ Cycle 26 — NoSQL: MongoDB + Cassandra
  ☐ Источники: свои шпаргалки (databases/)
    ☐ cassandra-interview.md
    ☐ mongodb-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=databases ≥80%
    ☐ Расширить cheatsheet в cheatsheets/databases/
    ☐ git commit learning(<topic>)

☐ Cycle 27 — Redis + Elasticsearch + Hibernate caching
  ☐ Источники: свои шпаргалки (databases/)
    ☐ elasticsearch-interview.md
    ☐ hibernate-caching-interview.md
    ☐ hibernate-interview.md
    ☐ hibernate-jpql-criteria-interview.md
    ☐ hibernate-relationships-interview.md
    ☐ redis-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=databases ≥80%
    ☐ Расширить cheatsheet в cheatsheets/databases/
    ☐ git commit learning(<topic>)

━━━ Phase 8 — Architecture ━━━

☐ Cycle 28 — Hexagonal / Clean / DDD
  ☐ Источники: свои шпаргалки (architecture/)
    ☐ clean-architecture-interview.md
    ☐ ddd-interview.md
    ☐ hexagonal-architecture-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=architecture ≥80%
    ☐ Расширить cheatsheet в cheatsheets/architecture/
    ☐ git commit learning(<topic>)

☐ Cycle 29 — CQRS / Event Sourcing / Saga
  ☐ Источники: свои шпаргалки (architecture/)
    ☐ cqrs-event-sourcing-interview.md
    ☐ saga-pattern-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=architecture ≥80%
    ☐ Расширить cheatsheet в cheatsheets/architecture/
    ☐ git commit learning(<topic>)

☐ Cycle 30 — Resilience Patterns (CB, retry, bulkhead)
  ☐ Источники: свои шпаргалки (architecture/)
    ☐ resilience-patterns-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=architecture ≥80%
    ☐ Расширить cheatsheet в cheatsheets/architecture/
    ☐ git commit learning(<topic>)

☐ Cycle 31 — Networking + API Gateway + Load Balancing
  ☐ Источники: свои шпаргалки (architecture/)
    ☐ api-gateway-interview.md
    ☐ load-balancing-interview.md
    ☐ networking-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=architecture ≥80%
    ☐ Расширить cheatsheet в cheatsheets/architecture/
    ☐ git commit learning(<topic>)

━━━ Phase 9 — Data Engineering coverage ━━━

☐ Cycle 32 — Apache Spark
  ☐ Источники: свои шпаргалки (data-engineering/)
    ☐ apache-spark-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=data-engineering ≥80%
    ☐ Расширить cheatsheet в cheatsheets/data-engineering/
    ☐ git commit learning(<topic>)

☐ Cycle 33 — Apache Flink
  ☐ Источники: свои шпаргалки (data-engineering/)
    ☐ apache-flink-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=data-engineering ≥80%
    ☐ Расширить cheatsheet в cheatsheets/data-engineering/
    ☐ git commit learning(<topic>)

☐ Cycle 34 — Data Lake / Lakehouse
  ☐ Источники: свои шпаргалки (data-engineering/)
    ☐ apache-airflow-interview.md
    ☐ data-lake-lakehouse-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=data-engineering ≥80%
    ☐ Расширить cheatsheet в cheatsheets/data-engineering/
    ☐ git commit learning(<topic>)

━━━ Phase 10 — Messaging & Search ━━━

☐ Cycle 35 — Kafka core (brokers, partitions, EOS)
  ☐ Источники: свои шпаргалки (messaging/)
    ☐ kafka-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=messaging ≥80%
    ☐ Расширить cheatsheet в cheatsheets/messaging/
    ☐ git commit learning(<topic>)

☐ Cycle 36 — Messaging ecosystem (Redpanda, RabbitMQ, ActiveMQ, Solr)
  ☐ Источники: свои шпаргалки (messaging/)
    ☐ rabbitmq-interview.md
    ☐ redpanda-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=messaging ≥80%
    ☐ Расширить cheatsheet в cheatsheets/messaging/
    ☐ git commit learning(<topic>)

━━━ Phase 11 — API ━━━

☐ Cycle 37 — REST + HTTP + REST Maturity
  ☐ Источники: свои шпаргалки (api/)
    ☐ http-rest-interview.md
    ☐ rest-maturity-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=api ≥80%
    ☐ Расширить cheatsheet в cheatsheets/api/
    ☐ git commit learning(<topic>)

☐ Cycle 38 — GraphQL + gRPC
  ☐ Источники: свои шпаргалки (api/)
    ☐ graphql-interview.md
    ☐ grpc-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=api ≥80%
    ☐ Расширить cheatsheet в cheatsheets/api/
    ☐ git commit learning(<topic>)

☐ Cycle 39 — API Versioning + OpenAPI + Design
  ☐ Источники: свои шпаргалки (api/)
    ☐ api-design-best-practices-interview.md
    ☐ api-versioning-interview.md
    ☐ openapi-swagger-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=api ≥80%
    ☐ Расширить cheatsheet в cheatsheets/api/
    ☐ git commit learning(<topic>)

━━━ Phase 12 — Cloud ━━━

☐ Cycle 40 — AWS Core (EC2, S3, IAM, RDS, Lambda, VPC)
  ☐ Источники: свои шпаргалки (cloud/)
    ☐ aws-interview.md
    ☐ aws-lambda-interview.md
    ☐ azure-interview.md
    ☐ cloud-native-patterns-interview.md
    ☐ gcp-interview.md
    ☐ serverless-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=cloud ≥80%
    ☐ Расширить cheatsheet в cheatsheets/cloud/
    ☐ git commit learning(<topic>)

━━━ Phase 13 — DevOps & Containers ━━━

☐ Cycle 41 — Docker / Containers / multi-stage
  ☐ Источники: свои шпаргалки (devops/)
    ☐ docker-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=devops ≥80%
    ☐ Расширить cheatsheet в cheatsheets/devops/
    ☐ git commit learning(<topic>)

☐ Cycle 42 — CI/CD
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=cicd ≥80%
    ☐ Расширить cheatsheet в cheatsheets/devops/
    ☐ git commit learning(<topic>)

☐ Cycle 43 — Service Mesh + GitOps (Linkerd, Istio, ArgoCD)
  ☐ Источники: свои шпаргалки (devops/)
    ☐ argocd-interview.md
    ☐ istio-service-mesh-interview.md
    ☐ linkerd-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=devops ≥80%
    ☐ Расширить cheatsheet в cheatsheets/devops/
    ☐ git commit learning(<topic>)

━━━ Phase 14 — Observability ━━━

☐ Cycle 44 — Metrics (Prometheus, Grafana, Victoria Metrics)
  ☐ Источники: свои шпаргалки (monitoring/)
    ☐ loki-grafana-interview.md
    ☐ metrics-tracing-interview.md
    ☐ prometheus-grafana-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=monitoring ≥80%
    ☐ Расширить cheatsheet в cheatsheets/monitoring/
    ☐ git commit learning(<topic>)

☐ Cycle 45 — Logging (ELK, Loki, structured)
  ☐ Источники: свои шпаргалки (logging/)
    ☐ logging-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=logging ≥80%
    ☐ Расширить cheatsheet в cheatsheets/logging/
    ☐ git commit learning(<topic>)

☐ Cycle 46 — Tracing & APM (OpenTelemetry, Jaeger)
  ☐ Источники: свои шпаргалки (monitoring/)
    ☐ jaeger-zipkin-interview.md
    ☐ metrics-tracing-interview.md
    ☐ opentelemetry-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=monitoring ≥80%
    ☐ Расширить cheatsheet в cheatsheets/monitoring/
    ☐ git commit learning(<topic>)

━━━ Phase 15 — Security ━━━

☐ Cycle 47 — OWASP Top 10 + AppSec
  ☐ Источники: свои шпаргалки (security/)
    ☐ application-security-interview.md
    ☐ owasp-top10-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=security ≥80%
    ☐ Расширить cheatsheet в cheatsheets/security/
    ☐ git commit learning(<topic>)

☐ Cycle 48 — AuthN/Z (OAuth2, OIDC, JWT, SAML, RBAC/ABAC)
  ☐ Источники: свои шпаргалки (security/)
    ☐ authentication-authorization-patterns-interview.md
    ☐ jwt-interview.md
    ☐ oauth2-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=security ≥80%
    ☐ Расширить cheatsheet в cheatsheets/security/
    ☐ git commit learning(<topic>)

☐ Cycle 49 — TLS/SSL + Zero Trust
  ☐ Источники: свои шпаргалки (security/)
    ☐ mtls-interview.md
    ☐ tls-ssl-interview.md
    ☐ zero-trust-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=security ≥80%
    ☐ Расширить cheatsheet в cheatsheets/security/
    ☐ git commit learning(<topic>)

━━━ Phase 16 — Testing ━━━

☐ Cycle 50 — Test Strategies (Pyramid, Trophy)
  ☐ Источники: свои шпаргалки (testing/)
    ☐ test-strategies-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=testing ≥80%
    ☐ Расширить cheatsheet в cheatsheets/testing/
    ☐ git commit learning(<topic>)

☐ Cycle 51 — Unit + Integration (JUnit, Mockito, Spring Test)
  ☐ Источники: свои шпаргалки (testing/)
    ☐ integration-testing-interview.md
    ☐ junit-interview.md
    ☐ mockito-interview.md
    ☐ unit-testing-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=testing ≥80%
    ☐ Расширить cheatsheet в cheatsheets/testing/
    ☐ git commit learning(<topic>)

☐ Cycle 52 — E2E + Performance + Chaos + Test Automation
  ☐ Источники: свои шпаргалки (testing/)
    ☐ chaos-engineering-interview.md
    ☐ test-automation-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=testing ≥80%
    ☐ Расширить cheatsheet в cheatsheets/testing/
    ☐ git commit learning(<topic>)

━━━ Phase 17 — Performance ━━━

☐ Cycle 53 — JVM tuning & profiling (GC, JIT)
  ☐ Источники: свои шпаргалки (performance/)
    ☐ application-profiling-interview.md
    ☐ jvm-performance-tuning-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=performance ≥80%
    ☐ Расширить cheatsheet в cheatsheets/performance/
    ☐ git commit learning(<topic>)

☐ Cycle 54 — App performance (DB, caching, network)
  ☐ Источники: свои шпаргалки (performance/)
    ☐ caching-performance-interview.md
    ☐ database-performance-interview.md
    ☐ network-performance-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=performance ≥80%
    ☐ Расширить cheatsheet в cheatsheets/performance/
    ☐ git commit learning(<topic>)

━━━ Phase 18 — JVM + Alt Frameworks ━━━

☐ Cycle 55 — JVM internals + GraalVM Native
  ☐ Источники: свои шпаргалки (jvm/)
    ☐ graalvm-native-interview.md
    ☐ jvm-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=jvm ≥80%
    ☐ Расширить cheatsheet в cheatsheets/jvm/
    ☐ git commit learning(<topic>)

☐ Cycle 56 — Micronaut & Quarkus
  ☐ Источники: свои шпаргалки (frameworks/jvm-alternatives/)
    ☐ ktor-interview.md
    ☐ micronaut-interview.md
    ☐ quarkus-interview.md
    ☐ vertx-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=frameworks/jvm-alternatives ≥80%
    ☐ Расширить cheatsheet в cheatsheets/frameworks/jvm-alternatives/
    ☐ git commit learning(<topic>)

━━━ Phase 19 — Code Quality + Patterns ━━━

☐ Cycle 57 — Design Patterns (GoF)
  ☐ Источники: свои шпаргалки (design-patterns/)
    ☐ design-patterns-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=design-patterns ≥80%
    ☐ Расширить cheatsheet в cheatsheets/design-patterns/
    ☐ git commit learning(<topic>)

☐ Cycle 58 — Refactoring + SOLID + Code Review
  ☐ Источники: свои шпаргалки (code-quality/)
    ☐ clean-code-practices-interview.md
    ☐ code-coverage-interview.md
    ☐ code-review-interview.md
    ☐ code-smells-interview.md
    ☐ refactoring-patterns-interview.md
    ☐ static-analysis-interview.md
    ☐ technical-debt-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=code-quality ≥80%
    ☐ Расширить cheatsheet в cheatsheets/code-quality/
    ☐ git commit learning(<topic>)

━━━ Phase 20 — Behavioral & Leadership ━━━

☐ Cycle 59 — Behavioral STAR (Conflict, Failure, Culture-fit)
  ☐ Источники: свои шпаргалки (behavioral/)
    ☐ behavioral-interview.md
    ☐ conflict-stories-interview.md
    ☐ culture-fit-interview.md
    ☐ failure-stories-interview.md
    ☐ leadership-stories-interview.md
    ☐ star-method-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=behavioral ≥80%
    ☐ Расширить cheatsheet в cheatsheets/behavioral/
    ☐ git commit learning(<topic>)

☐ Cycle 60 — Leadership (Mentoring, Tech-decisions, Estimations)
  ☐ Источники: свои шпаргалки (leadership/)
    ☐ code-review-practices-interview.md
    ☐ conflict-resolution-interview.md
    ☐ estimations-planning-interview.md
    ☐ mentoring-interview.md
    ☐ team-leadership-interview.md
    ☐ tech-interviewing-interview.md
    ☐ technical-decisions-interview.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=leadership ≥80%
    ☐ Расширить cheatsheet в cheatsheets/leadership/
    ☐ git commit learning(<topic>)

━━━ Phase 21 — Trainers (hands-on) ━━━

☐ Cycle 61 — SQL Trainer (LeetCode SQL + analytical)
  ☐ Источники: внешние / закладки
    ☐ Закладка: Тренажер SQL
    ☐ LeetCode SQL (50 задач)
    ☐ PostgreSQL window functions docs
  ☐ Выписать holes после первого прохода
  ☐ Hands-on
    ☐ Решить 50 задач LeetCode SQL
    ☐ 5 аналитических запросов с CTEs
  ☐ Quiz & commit
    ☐ 20 MCQ topic=databases/sql ≥80%
    ☐ Записать заметки в cheatsheets/ (papka на выбор)
    ☐ git commit learning(<topic>)

☐ Cycle 62 — Git Trainer (rebase, bisect, reflog)
  ☐ Источники: внешние / закладки
    ☐ Закладка: Тренажер Git
    ☐ git-rebase, git-bisect, git-reflog манов
  ☐ Выписать holes после первого прохода
  ☐ Hands-on
    ☐ Rebase interactive: squash 5 коммитов
    ☐ Bisect для поиска bad commit
    ☐ Recover потерянный коммит через reflog
  ☐ Quiz & commit
    ☐ 20 MCQ ≥80% (если есть в quiz-app)
    ☐ Записать заметки в cheatsheets/ (papka на выбор)
    ☐ git commit learning(<topic>)

☐ Cycle 63 — Docker Trainer
  ☐ Источники: внешние / закладки
    ☐ Закладка: Тренажер Docker
    ☐ Docker docs
  ☐ Выписать holes после первого прохода
  ☐ Hands-on
    ☐ Multi-stage build с уменьшением размера в 5×
    ☐ Docker network: bridge vs host vs overlay
    ☐ docker-compose с healthcheck'ами
  ☐ Quiz & commit
    ☐ 20 MCQ topic=devops ≥80%
    ☐ Записать заметки в cheatsheets/ (papka на выбор)
    ☐ git commit learning(<topic>)

☐ Cycle 64 — Kubernetes Trainer (kind/minikube)
  ☐ Источники: внешние / закладки
    ☐ Закладка: Тренажёр Kubernetes
    ☐ Закладки DevOps > Kubernetes
  ☐ Выписать holes после первого прохода
  ☐ Hands-on
    ☐ Запустить kind/minikube
    ☐ Deploy 3 приложения с Ingress
    ☐ Debug упавшего pod'а
  ☐ Quiz & commit
    ☐ 20 MCQ topic=devops/kubernetes ≥80%
    ☐ Записать заметки в cheatsheets/ (papka на выбор)
    ☐ git commit learning(<topic>)

☐ Cycle 65 — DSA Visualizer (VisuAlgo + Algorithm Visualizer)
  ☐ Источники: внешние / закладки
    ☐ Закладка: VisuAlgo
    ☐ Закладка: Algorithm Visualizer
  ☐ Выписать holes после первого прохода
  ☐ Hands-on
    ☐ Визуальный разбор 10 алгоритмов
    ☐ Воспроизвести 5 алгоритмов на Java/Python
  ☐ Quiz & commit
    ☐ 20 MCQ topic=algorithms ≥80%
    ☐ Записать заметки в cheatsheets/ (papka на выбор)
    ☐ git commit learning(<topic>)

━━━ Phase 22 — Misc ━━━

☐ Cycle 66 — Interview preparation meta
  ☐ Источники: свои шпаргалки (preparation/)
    ☐ interview-preparation.md
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ topic=preparation ≥80%
    ☐ Расширить cheatsheet в cheatsheets/preparation/
    ☐ git commit learning(<topic>)

☐ Cycle 67 — Cheat-sheet maintenance & meta-skills
  ☐ Выписать holes после первого прохода
  ☐ Hands-on (определить при старте цикла)
  ☐ Quiz & commit
    ☐ 20 MCQ ≥80% (если есть в quiz-app)
    ☐ Записать заметки в cheatsheets/ (papka на выбор)
    ☐ git commit learning(<topic>)
```

## Delta JSON (для заливки через updateNote)

```json
[{"insert": "План профессионального развития\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Бюджет: 5ч/нед (1ч × Пн-Пт). 67 циклов, без дат. Берём следующий когда закрыт текущий.\n"}, {"insert": "Детали и источники: docs/learning-plan/2026-05-20-self-learning-plan.md (в репо)\n"}, {"insert": "\n"}, {"insert": "━━━ Phase 1 — Priority (диагностика) ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 1 — System Design & Highload (DDIA)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "system-design-interview.md (повтор базы, 2956 строк)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "design-payment-system-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "design-chat-system-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "design-feed-system-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "design-rate-limiter-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "design-search-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "design-url-shortener-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "DDIA глава 1 — Reliable / Scalable / Maintainable"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "DDIA глава 2 — Data Models and Query Languages"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "DDIA глава 3 — Storage and Retrieval"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: Backend Roadmap"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: backend-cheats README"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "CAP / PACELC trade-offs в моих сервисах"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Sharding: hash vs range — когда применять"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Replication: sync vs async — последствия"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Caching: cache-aside vs write-through vs write-behind"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Event sourcing: когда оправдано"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Mermaid: URL shortener для 10k QPS"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Mermaid: Rate limiter (token bucket + sliding window)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Writeup: load estimation шаблон"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ из quiz-app topic=system-design ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheets/system-design/<тема>.md ≥300 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(system-design)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 2 — Algorithms — Data Structures I (Trees focus)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "trees-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "graphs-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "linked-lists-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hash-tables-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: VisuAlgo (визуализация)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: Algorithm Visualizer"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: Big O Cheat Sheet"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "LeetCode Medium по Trees (25 задач)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Self-balancing trees: AVL vs Red-Black trade-offs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "B-tree vs B+ tree (где какие индексы)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Trie применение в реальных задачах"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Heap: max-heap vs min-heap, операции"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Реализовать BST с балансировкой"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "LeetCode: 25 задач Medium на Trees"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Решение через VisuAlgo (визуальный разбор 5 алгоритмов)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=algorithms/data-structures/trees-interview ≥85%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheets/algorithms/<...>.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(algorithms-ds)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 3 — AI/LLM Foundations"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "ai-agents-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "embeddings-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "llm-basics-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "llm-integration-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "mlops-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "model-serving-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "prompt-engineering-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "rag-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "vector-databases-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: awesome-ai-memory"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: system-prompts-and-models"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: awesome-cursorrules"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: Claude Code Docs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: backend / claude-skills (GitLab)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Токенизация: BPE vs WordPiece vs SentencePiece"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Attention: self vs cross, MHA, GQA"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Context window и его расширение (RoPE, sliding window)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Embeddings: dense vs sparse, cosine vs dot product"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "RAG: chunking strategies, reranking"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Prompt engineering: few-shot, CoT, structured outputs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Написать RAG-pipeline на Python (50 строк)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Свой prompt-template для конкретного use-case"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "30 MCQ topic=ai-ml ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Создать cheatsheets/ai-ml/llm-fundamentals.md ≥500 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit feat(ai-ml): llm-fundamentals"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 4 — Kubernetes deep"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "kubernetes-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: Тренажёр Kubernetes"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладки DevOps > Kubernetes (6 ссылок)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладки DevOps > Argo CD (5 ссылок)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Controllers: Deployment vs StatefulSet vs DaemonSet"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Scheduling: affinity, anti-affinity, taints"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Networking: Service types, Ingress, NetworkPolicy"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "RBAC: Roles, ClusterRoles, ServiceAccounts"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Storage: PV, PVC, StorageClass, CSI"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Запустить kind/minikube локально"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Развернуть 5 манифестов разных типов"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Debug упавшего pod'а (kubectl describe, logs, exec)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=devops/kubernetes ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "cheatsheets/devops/kubernetes-deep.md ≥500 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(k8s)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 5 — Data Engineering — Kafka Streams"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "kafka-streams-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Apache Kafka docs (закладка)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Kafka Streams DSL guide"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Streams vs Consumer API: когда что"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Windowing: tumbling, hopping, session"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "State stores: in-memory vs RocksDB"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Exactly-once семантика"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Минимальное Streams-приложение на Java"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Windowed aggregation пример"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=data-engineering/kafka-streams ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить kafka-streams-interview.md ≥800 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(de-kafka-streams)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 6 — Go Concurrency"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "go-concurrency-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-generics-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-memory-gc-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-modules-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-stdlib-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-testing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладки Documentation > Go"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Goroutines vs threads — модель"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Channels: buffered vs unbuffered"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "sync package: WaitGroup, Mutex, Once"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Context для cancellation"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Race conditions: race detector"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "CLI-утилита на Go для парсинга diagnostic/*.tsv"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Pipeline через channels (3 stage)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/go ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-concurrency-interview.md ≥500 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(go-concurrency)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 7 — Reactive Streams (Reactor + RxJava)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "project-reactor-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "reactive-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "reactive-streams-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "reactive-testing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "rxjava-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "webflux-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Project Reactor docs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "RxJava wiki"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Backpressure стратегии (DROP, BUFFER, LATEST)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hot vs Cold publishers"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Schedulers: when to use which"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Error handling в reactive chain"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Testing reactive code (StepVerifier)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "5 pipeline-задач Reactor"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Конвертация callback-API в Mono/Flux"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=reactive ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "project-reactor-interview.md ≥600 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(reactive)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 8 — Database Architecture deep"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "database-architecture-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "postgresql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "postgresql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "sql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладки Documentation > Databases (5)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "MVCC: PostgreSQL подробно"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Indexes: B-tree, hash, GIN, GiST — когда что"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Query plans: EXPLAIN ANALYZE интерпретация"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Partitioning: declarative vs inheritance"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Replication: physical vs logical"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Анализ EXPLAIN на 5 типах запросов"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Локальный postgres с partitioning setup"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=databases/database-architecture ≥85%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "database-architecture-interview.md ≥700 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(db-arch)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 9 — Spring Cloud / Microservices"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "spring-cloud-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладки Documentation > Spring Framework (17)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes (что непонятно)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Service Discovery: Eureka vs Consul vs K8s native"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Config Server: refresh strategies"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Circuit Breaker: Resilience4j patterns"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "API Gateway: routing, filters"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Distributed Tracing: Sleuth/Micrometer integration"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Минимальный микросервисный стенд (Gateway + 2 service + Eureka)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Circuit breaker demo с симуляцией отказа"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=frameworks/spring/spring-cloud ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-cloud-interview.md ≥600 строк"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(spring-cloud)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 2 — Algorithms coverage ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 10 — Algorithmic Paradigms (DP, Backtracking, Greedy, D&C)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (algorithms/algorithmic-paradigms/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "backtracking-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "divide-and-conquer-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "dynamic-programming-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "greedy-algorithms-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "recursion-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "two-pointers-sliding-window-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=algorithms/algorithmic-paradigms ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/algorithms/algorithmic-paradigms/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 11 — Sorting & Searching + Big-O"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (algorithms/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "complexity/complexity-analysis-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "sorting-searching/searching-algorithms-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "sorting-searching/sorting-algorithms-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=algorithms ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/algorithms/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 12 — Data Structures II (Graphs, Hash, Heaps, Tries, Stacks)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (algorithms/data-structures/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "arrays-strings-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "graphs-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hash-tables-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "heaps-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "linked-lists-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "stacks-queues-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "trees-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "tries-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=algorithms/data-structures ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/algorithms/data-structures/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 3 — Java Core deep ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 13 — Java Concurrency deep"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/java/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "java-concurrency-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-virtual-threads-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/java/java-concurrency ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/java/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 14 — Java Collections + Streams API"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/java/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "java-collections-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-stream-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/java/java-collections ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/java/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 15 — Java Modern (Optional, CompletableFuture, Records, Sealed)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/java/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "java-completable-future-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-optional-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-pattern-matching-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-records-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/java ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/java/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 16 — Java Memory Model + I/O / NIO"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/java/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "java-annotations-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-collections-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-conditional-statements-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-exceptions-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-functional-interface-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-initialization-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-io-nio-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-optional-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-reflection-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "java-serialization-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/java ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/java/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 4 — Kotlin ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 17 — Kotlin Language (sealed, data, scope, DSL)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/kotlin/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "kotlin-collections-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-coroutines-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-dsl-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-exceptions-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-flow-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-interop-java-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-sealed-classes-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-serialization-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-spring-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-value-classes-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/kotlin ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/kotlin/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 18 — Kotlin Coroutines + Flow"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/kotlin/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "kotlin-coroutines-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "kotlin-flow-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/kotlin ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/kotlin/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 5 — Go coverage ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 19 — Go fundamentals + testing + idioms"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (programming-languages/go/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "go-concurrency-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-generics-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-memory-gc-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-modules-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-stdlib-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "go-testing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=programming-languages/go ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/programming-languages/go/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 6 — Spring deep ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 20 — Spring Core (IoC, AOP, Beans, profiles)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (frameworks/spring/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "spring-aop-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-async-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-boot-3-migration-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-boot-actuator-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-boot-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-framework-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=frameworks/spring ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/frameworks/spring/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 21 — Spring Data (JPA, R2DBC, transactions)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (frameworks/spring/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "spring-data-jdbc-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-data-jpa-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-r2dbc-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-transaction-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=frameworks/spring ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/frameworks/spring/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 22 — Spring Security (OAuth2, JWT, method security)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (frameworks/spring/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "spring-security-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=frameworks/spring ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/frameworks/spring/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 23 — Spring Batch / Modulith / WebFlux / Integration"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (frameworks/spring/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "spring-batch-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-integration-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-modulith-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "spring-webflux-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=frameworks/spring ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/frameworks/spring/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 7 — Databases deep ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 24 — PostgreSQL deep (MVCC, indexes, query plans, partitioning)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (databases/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "postgresql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=databases/postgresql ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/databases/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 25 — SQL Patterns (joins, CTEs, window functions)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (databases/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "postgresql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "sql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=databases/sql ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/databases/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 26 — NoSQL: MongoDB + Cassandra"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (databases/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "cassandra-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "mongodb-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=databases ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/databases/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 27 — Redis + Elasticsearch + Hibernate caching"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (databases/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "elasticsearch-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hibernate-caching-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hibernate-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hibernate-jpql-criteria-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hibernate-relationships-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "redis-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=databases ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/databases/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 8 — Architecture ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 28 — Hexagonal / Clean / DDD"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (architecture/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "clean-architecture-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "ddd-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "hexagonal-architecture-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=architecture ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/architecture/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 29 — CQRS / Event Sourcing / Saga"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (architecture/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "cqrs-event-sourcing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "saga-pattern-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=architecture ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/architecture/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 30 — Resilience Patterns (CB, retry, bulkhead)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (architecture/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "resilience-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=architecture ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/architecture/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 31 — Networking + API Gateway + Load Balancing"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (architecture/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "api-gateway-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "load-balancing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "networking-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=architecture ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/architecture/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 9 — Data Engineering coverage ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 32 — Apache Spark"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (data-engineering/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "apache-spark-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=data-engineering ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/data-engineering/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 33 — Apache Flink"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (data-engineering/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "apache-flink-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=data-engineering ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/data-engineering/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 34 — Data Lake / Lakehouse"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (data-engineering/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "apache-airflow-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "data-lake-lakehouse-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=data-engineering ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/data-engineering/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 10 — Messaging & Search ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 35 — Kafka core (brokers, partitions, EOS)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (messaging/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "kafka-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=messaging ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/messaging/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 36 — Messaging ecosystem (Redpanda, RabbitMQ, ActiveMQ, Solr)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (messaging/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "rabbitmq-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "redpanda-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=messaging ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/messaging/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 11 — API ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 37 — REST + HTTP + REST Maturity"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (api/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "http-rest-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "rest-maturity-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=api ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/api/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 38 — GraphQL + gRPC"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (api/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "graphql-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "grpc-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=api ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/api/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 39 — API Versioning + OpenAPI + Design"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (api/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "api-design-best-practices-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "api-versioning-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "openapi-swagger-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=api ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/api/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 12 — Cloud ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 40 — AWS Core (EC2, S3, IAM, RDS, Lambda, VPC)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (cloud/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "aws-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "aws-lambda-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "azure-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "cloud-native-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "gcp-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "serverless-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=cloud ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/cloud/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 13 — DevOps & Containers ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 41 — Docker / Containers / multi-stage"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (devops/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "docker-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=devops ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/devops/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 42 — CI/CD"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=cicd ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/devops/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 43 — Service Mesh + GitOps (Linkerd, Istio, ArgoCD)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (devops/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "argocd-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "istio-service-mesh-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "linkerd-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=devops ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/devops/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 14 — Observability ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 44 — Metrics (Prometheus, Grafana, Victoria Metrics)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (monitoring/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "loki-grafana-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "metrics-tracing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "prometheus-grafana-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=monitoring ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/monitoring/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 45 — Logging (ELK, Loki, structured)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (logging/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "logging-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=logging ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/logging/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 46 — Tracing & APM (OpenTelemetry, Jaeger)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (monitoring/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "jaeger-zipkin-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "metrics-tracing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "opentelemetry-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=monitoring ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/monitoring/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 15 — Security ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 47 — OWASP Top 10 + AppSec"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (security/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "application-security-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "owasp-top10-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=security ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/security/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 48 — AuthN/Z (OAuth2, OIDC, JWT, SAML, RBAC/ABAC)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (security/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "authentication-authorization-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "jwt-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "oauth2-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=security ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/security/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 49 — TLS/SSL + Zero Trust"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (security/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "mtls-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "tls-ssl-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "zero-trust-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=security ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/security/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 16 — Testing ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 50 — Test Strategies (Pyramid, Trophy)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (testing/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "test-strategies-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=testing ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/testing/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 51 — Unit + Integration (JUnit, Mockito, Spring Test)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (testing/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "integration-testing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "junit-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "mockito-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "unit-testing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=testing ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/testing/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 52 — E2E + Performance + Chaos + Test Automation"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (testing/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "chaos-engineering-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "test-automation-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=testing ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/testing/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 17 — Performance ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 53 — JVM tuning & profiling (GC, JIT)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (performance/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "application-profiling-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "jvm-performance-tuning-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=performance ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/performance/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 54 — App performance (DB, caching, network)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (performance/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "caching-performance-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "database-performance-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "network-performance-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=performance ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/performance/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 18 — JVM + Alt Frameworks ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 55 — JVM internals + GraalVM Native"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (jvm/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "graalvm-native-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "jvm-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=jvm ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/jvm/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 56 — Micronaut & Quarkus"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (frameworks/jvm-alternatives/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "ktor-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "micronaut-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "quarkus-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "vertx-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=frameworks/jvm-alternatives ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/frameworks/jvm-alternatives/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 19 — Code Quality + Patterns ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 57 — Design Patterns (GoF)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (design-patterns/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "design-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=design-patterns ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/design-patterns/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 58 — Refactoring + SOLID + Code Review"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (code-quality/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "clean-code-practices-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "code-coverage-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "code-review-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "code-smells-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "refactoring-patterns-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "static-analysis-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "technical-debt-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=code-quality ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/code-quality/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 20 — Behavioral & Leadership ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 59 — Behavioral STAR (Conflict, Failure, Culture-fit)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (behavioral/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "behavioral-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "conflict-stories-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "culture-fit-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "failure-stories-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "leadership-stories-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "star-method-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=behavioral ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/behavioral/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 60 — Leadership (Mentoring, Tech-decisions, Estimations)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (leadership/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "code-review-practices-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "conflict-resolution-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "estimations-planning-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "mentoring-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "team-leadership-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "tech-interviewing-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "technical-decisions-interview.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=leadership ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/leadership/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 21 — Trainers (hands-on) ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 61 — SQL Trainer (LeetCode SQL + analytical)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: Тренажер SQL"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "LeetCode SQL (50 задач)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "PostgreSQL window functions docs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Решить 50 задач LeetCode SQL"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "5 аналитических запросов с CTEs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=databases/sql ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Записать заметки в cheatsheets/ (papka на выбор)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 62 — Git Trainer (rebase, bisect, reflog)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: Тренажер Git"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git-rebase, git-bisect, git-reflog манов"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Rebase interactive: squash 5 коммитов"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Bisect для поиска bad commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Recover потерянный коммит через reflog"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ ≥80% (если есть в quiz-app)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Записать заметки в cheatsheets/ (papka на выбор)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 63 — Docker Trainer"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: Тренажер Docker"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Docker docs"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Multi-stage build с уменьшением размера в 5×"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Docker network: bridge vs host vs overlay"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "docker-compose с healthcheck'ами"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=devops ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Записать заметки в cheatsheets/ (papka на выбор)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 64 — Kubernetes Trainer (kind/minikube)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: Тренажёр Kubernetes"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладки DevOps > Kubernetes"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Запустить kind/minikube"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Deploy 3 приложения с Ingress"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Debug упавшего pod'а"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=devops/kubernetes ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Записать заметки в cheatsheets/ (papka на выбор)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 65 — DSA Visualizer (VisuAlgo + Algorithm Visualizer)"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: внешние / закладки"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Закладка: VisuAlgo"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Закладка: Algorithm Visualizer"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Визуальный разбор 10 алгоритмов"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Воспроизвести 5 алгоритмов на Java/Python"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=algorithms ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Записать заметки в cheatsheets/ (papka на выбор)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "━━━ Phase 22 — Misc ━━━\n", "attributes": {"bold": true}}, {"insert": "\n"}, {"insert": "Cycle 66 — Interview preparation meta"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Источники: свои шпаргалки (preparation/)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "interview-preparation.md"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ topic=preparation ≥80%"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Расширить cheatsheet в cheatsheets/preparation/"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Cycle 67 — Cheat-sheet maintenance & meta-skills"}, {"insert": "\n", "attributes": {"list": "unchecked"}}, {"insert": "Выписать holes после первого прохода"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Hands-on (определить при старте цикла)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "Quiz & commit"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 1}}, {"insert": "20 MCQ ≥80% (если есть в quiz-app)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "Записать заметки в cheatsheets/ (papka на выбор)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "git commit learning(<topic>)"}, {"insert": "\n", "attributes": {"list": "unchecked", "indent": 2}}, {"insert": "\n"}, {"insert": "Прогресс ведётся отметкой чек-боксов. Следующий цикл = первый незачёкнутый.\n"}, {"insert": "Habit «Учусь сегодня (1ч)» отмечать каждый будний день после реальной сессии.\n"}]
```
