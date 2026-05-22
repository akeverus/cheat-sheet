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

- [ ] Cycle 10 — Algorithmic Paradigms (DP, Backtracking, Greedy, D&C)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/algorithms/algorithmic-paradigms/backtracking-interview.md
    - [ ] cheatsheets/interview/algorithms/algorithmic-paradigms/divide-and-conquer-interview.md
    - [ ] cheatsheets/interview/algorithms/algorithmic-paradigms/dynamic-programming-interview.md
    - [ ] cheatsheets/interview/algorithms/algorithmic-paradigms/greedy-algorithms-interview.md
    - [ ] cheatsheets/interview/algorithms/algorithmic-paradigms/recursion-interview.md
    - [ ] cheatsheets/interview/algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview.md
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://visualgo.net/en
    - [ ] https://algorithm-visualizer.org/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=algorithms/algorithmic-paradigms ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/algorithms/algorithmic-paradigms/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 11 — Sorting & Searching + Big-O
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/algorithms/complexity/complexity-analysis-interview.md
    - [ ] cheatsheets/interview/algorithms/sorting-searching/searching-algorithms-interview.md
    - [ ] cheatsheets/interview/algorithms/sorting-searching/sorting-algorithms-interview.md
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://visualgo.net/en
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=algorithms ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/algorithms/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 12 — Data Structures II (Graphs, Hash, Heaps, Tries, Stacks)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/algorithms/data-structures/arrays-strings-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/graphs-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/hash-tables-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/heaps-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/linked-lists-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/stacks-queues-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/trees-interview.md
    - [ ] cheatsheets/interview/algorithms/data-structures/tries-interview.md
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://visualgo.net/en
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=algorithms/data-structures ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/algorithms/data-structures/
    - [ ] git commit learning(<topic>)

## Phase 3 — Java Core deep

- [ ] Cycle 13 — Java Concurrency deep
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/java/java-concurrency-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-virtual-threads-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/java ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/java/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 14 — Java Collections + Streams API
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/java/java-collections-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-stream-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/java ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/java/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 15 — Java Modern (Optional, CompletableFuture, Records, Sealed)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/java/java-completable-future-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-optional-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-pattern-matching-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-records-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/java ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/java/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 16 — Java Memory Model + I/O / NIO
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/java/java-annotations-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-collections-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-conditional-statements-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-exceptions-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-functional-interface-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-initialization-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-io-nio-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-optional-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-reflection-interview.md
    - [ ] cheatsheets/interview/programming-languages/java/java-serialization-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/java ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/java/
    - [ ] git commit learning(<topic>)

## Phase 4 — Kotlin

- [ ] Cycle 17 — Kotlin Language (sealed, data, scope, DSL)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-collections-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-coroutines-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-dsl-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-exceptions-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-flow-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-interop-java-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-sealed-classes-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-serialization-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-spring-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-value-classes-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/kotlin ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/kotlin/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 18 — Kotlin Coroutines + Flow
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-coroutines-interview.md
    - [ ] cheatsheets/interview/programming-languages/kotlin/kotlin-flow-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/kotlin ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/kotlin/
    - [ ] git commit learning(<topic>)

## Phase 5 — Go coverage

- [ ] Cycle 19 — Go fundamentals + testing + idioms
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/programming-languages/go/go-concurrency-interview.md
    - [ ] cheatsheets/interview/programming-languages/go/go-generics-interview.md
    - [ ] cheatsheets/interview/programming-languages/go/go-interview.md
    - [ ] cheatsheets/interview/programming-languages/go/go-memory-gc-interview.md
    - [ ] cheatsheets/interview/programming-languages/go/go-modules-interview.md
    - [ ] cheatsheets/interview/programming-languages/go/go-stdlib-interview.md
    - [ ] cheatsheets/interview/programming-languages/go/go-testing-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=programming-languages/go ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/programming-languages/go/
    - [ ] git commit learning(<topic>)

## Phase 6 — Spring deep

- [ ] Cycle 20 — Spring Core (IoC, AOP, Beans, profiles)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/frameworks/spring/spring-aop-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-async-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-boot-3-migration-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-boot-actuator-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-boot-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-framework-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=frameworks/spring ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/frameworks/spring/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 21 — Spring Data (JPA, R2DBC, transactions)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/frameworks/spring/spring-data-jdbc-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-data-jpa-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-r2dbc-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-transaction-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=frameworks/spring ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/frameworks/spring/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 22 — Spring Security (OAuth2, JWT, method security)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/frameworks/spring/spring-security-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=frameworks/spring ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/frameworks/spring/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 23 — Spring Batch / Modulith / WebFlux / Integration
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/frameworks/spring/spring-batch-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-integration-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-modulith-interview.md
    - [ ] cheatsheets/interview/frameworks/spring/spring-webflux-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=frameworks/spring ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/frameworks/spring/
    - [ ] git commit learning(<topic>)

## Phase 7 — Databases deep

- [ ] Cycle 24 — PostgreSQL deep (MVCC, indexes, query plans, partitioning)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/databases/postgresql-interview.md
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://www.sql-ex.ru/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=databases ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/databases/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 25 — SQL Patterns (joins, CTEs, window functions)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/databases/postgresql-interview.md
    - [ ] cheatsheets/interview/databases/sql-interview.md
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://leetcode.com/problemset/database/
    - [ ] https://www.sql-ex.ru/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=databases ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/databases/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 26 — NoSQL: MongoDB + Cassandra
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/databases/cassandra-interview.md
    - [ ] cheatsheets/interview/databases/mongodb-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=databases ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/databases/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 27 — Redis + Elasticsearch + Hibernate caching
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/databases/elasticsearch-interview.md
    - [ ] cheatsheets/interview/databases/hibernate-caching-interview.md
    - [ ] cheatsheets/interview/databases/hibernate-interview.md
    - [ ] cheatsheets/interview/databases/hibernate-jpql-criteria-interview.md
    - [ ] cheatsheets/interview/databases/hibernate-relationships-interview.md
    - [ ] cheatsheets/interview/databases/redis-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=databases ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/databases/
    - [ ] git commit learning(<topic>)

## Phase 8 — Architecture

- [ ] Cycle 28 — Hexagonal / Clean / DDD
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/architecture/clean-architecture-interview.md
    - [ ] cheatsheets/interview/architecture/ddd-interview.md
    - [ ] cheatsheets/interview/architecture/hexagonal-architecture-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=architecture ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/architecture/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 29 — CQRS / Event Sourcing / Saga
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/architecture/cqrs-event-sourcing-interview.md
    - [ ] cheatsheets/interview/architecture/saga-pattern-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=architecture ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/architecture/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 30 — Resilience Patterns (CB, retry, bulkhead)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/architecture/resilience-patterns-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=architecture ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/architecture/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 31 — Networking + API Gateway + Load Balancing
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/architecture/api-gateway-interview.md
    - [ ] cheatsheets/interview/architecture/load-balancing-interview.md
    - [ ] cheatsheets/interview/architecture/networking-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=architecture ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/architecture/
    - [ ] git commit learning(<topic>)

## Phase 9 — Data Engineering coverage

- [ ] Cycle 32 — Apache Spark
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/data-engineering/apache-spark-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=data-engineering ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/data-engineering/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 33 — Apache Flink
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/data-engineering/apache-flink-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=data-engineering ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/data-engineering/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 34 — Data Lake / Lakehouse
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/data-engineering/apache-airflow-interview.md
    - [ ] cheatsheets/interview/data-engineering/data-lake-lakehouse-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=data-engineering ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/data-engineering/
    - [ ] git commit learning(<topic>)

## Phase 10 — Messaging & Search

- [ ] Cycle 35 — Kafka core (brokers, partitions, EOS)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/messaging/kafka-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=messaging ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/messaging/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 36 — Messaging ecosystem (Redpanda, RabbitMQ, ActiveMQ, Solr)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/messaging/rabbitmq-interview.md
    - [ ] cheatsheets/interview/messaging/redpanda-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=messaging ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/messaging/
    - [ ] git commit learning(<topic>)

## Phase 11 — API

- [ ] Cycle 37 — REST + HTTP + REST Maturity
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/api/http-rest-interview.md
    - [ ] cheatsheets/interview/api/rest-maturity-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=api ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/api/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 38 — GraphQL + gRPC
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/api/graphql-interview.md
    - [ ] cheatsheets/interview/api/grpc-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=api ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/api/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 39 — API Versioning + OpenAPI + Design
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/api/api-design-best-practices-interview.md
    - [ ] cheatsheets/interview/api/api-versioning-interview.md
    - [ ] cheatsheets/interview/api/openapi-swagger-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=api ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/api/
    - [ ] git commit learning(<topic>)

## Phase 12 — Cloud

- [ ] Cycle 40 — AWS Core (EC2, S3, IAM, RDS, Lambda, VPC)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/cloud/aws-interview.md
    - [ ] cheatsheets/interview/cloud/aws-lambda-interview.md
    - [ ] cheatsheets/interview/cloud/azure-interview.md
    - [ ] cheatsheets/interview/cloud/cloud-native-patterns-interview.md
    - [ ] cheatsheets/interview/cloud/gcp-interview.md
    - [ ] cheatsheets/interview/cloud/serverless-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=cloud ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/cloud/
    - [ ] git commit learning(<topic>)

## Phase 13 — DevOps & Containers

- [ ] Cycle 41 — Docker / Containers / multi-stage
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/devops/docker-interview.md
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://labs.play-with-docker.com/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=devops ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/devops/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 42 — CI/CD
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=devops ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/devops/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 43 — Service Mesh + GitOps (Linkerd, Istio, ArgoCD)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/devops/argocd-interview.md
    - [ ] cheatsheets/interview/devops/istio-service-mesh-interview.md
    - [ ] cheatsheets/interview/devops/linkerd-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=devops ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/devops/
    - [ ] git commit learning(<topic>)

## Phase 14 — Observability

- [ ] Cycle 44 — Metrics (Prometheus, Grafana, Victoria Metrics)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/monitoring/loki-grafana-interview.md
    - [ ] cheatsheets/interview/monitoring/metrics-tracing-interview.md
    - [ ] cheatsheets/interview/monitoring/prometheus-grafana-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=monitoring ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/monitoring/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 45 — Logging (ELK, Loki, structured)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/logging/logging-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=logging ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/logging/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 46 — Tracing & APM (OpenTelemetry, Jaeger)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/monitoring/jaeger-zipkin-interview.md
    - [ ] cheatsheets/interview/monitoring/metrics-tracing-interview.md
    - [ ] cheatsheets/interview/monitoring/opentelemetry-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=monitoring ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/monitoring/
    - [ ] git commit learning(<topic>)

## Phase 15 — Security

- [ ] Cycle 47 — OWASP Top 10 + AppSec
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/security/application-security-interview.md
    - [ ] cheatsheets/interview/security/owasp-top10-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=security ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/security/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 48 — AuthN/Z (OAuth2, OIDC, JWT, SAML, RBAC/ABAC)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/security/authentication-authorization-patterns-interview.md
    - [ ] cheatsheets/interview/security/jwt-interview.md
    - [ ] cheatsheets/interview/security/oauth2-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=security ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/security/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 49 — TLS/SSL + Zero Trust
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/security/mtls-interview.md
    - [ ] cheatsheets/interview/security/tls-ssl-interview.md
    - [ ] cheatsheets/interview/security/zero-trust-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=security ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/security/
    - [ ] git commit learning(<topic>)

## Phase 16 — Testing

- [ ] Cycle 50 — Test Strategies (Pyramid, Trophy)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/testing/test-strategies-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=testing ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/testing/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 51 — Unit + Integration (JUnit, Mockito, Spring Test)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/testing/integration-testing-interview.md
    - [ ] cheatsheets/interview/testing/junit-interview.md
    - [ ] cheatsheets/interview/testing/mockito-interview.md
    - [ ] cheatsheets/interview/testing/unit-testing-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=testing ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/testing/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 52 — E2E + Performance + Chaos + Test Automation
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/testing/chaos-engineering-interview.md
    - [ ] cheatsheets/interview/testing/test-automation-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=testing ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/testing/
    - [ ] git commit learning(<topic>)

## Phase 17 — Performance

- [ ] Cycle 53 — JVM tuning & profiling (GC, JIT)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/performance/application-profiling-interview.md
    - [ ] cheatsheets/interview/performance/jvm-performance-tuning-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=performance ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/performance/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 54 — App performance (DB, caching, network)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/performance/caching-performance-interview.md
    - [ ] cheatsheets/interview/performance/database-performance-interview.md
    - [ ] cheatsheets/interview/performance/network-performance-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=performance ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/performance/
    - [ ] git commit learning(<topic>)

## Phase 18 — JVM + Alt Frameworks

- [ ] Cycle 55 — JVM internals + GraalVM Native
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/jvm/graalvm-native-interview.md
    - [ ] cheatsheets/interview/jvm/jvm-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=jvm ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/jvm/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 56 — Micronaut & Quarkus
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/frameworks/jvm-alternatives/ktor-interview.md
    - [ ] cheatsheets/interview/frameworks/jvm-alternatives/micronaut-interview.md
    - [ ] cheatsheets/interview/frameworks/jvm-alternatives/quarkus-interview.md
    - [ ] cheatsheets/interview/frameworks/jvm-alternatives/vertx-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=frameworks/jvm-alternatives ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/frameworks/jvm-alternatives/
    - [ ] git commit learning(<topic>)

## Phase 19 — Code Quality + Patterns

- [ ] Cycle 57 — Design Patterns (GoF)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/design-patterns/design-patterns-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=design-patterns ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/design-patterns/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 58 — Refactoring + SOLID + Code Review
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/code-quality/clean-code-practices-interview.md
    - [ ] cheatsheets/interview/code-quality/code-coverage-interview.md
    - [ ] cheatsheets/interview/code-quality/code-review-interview.md
    - [ ] cheatsheets/interview/code-quality/code-smells-interview.md
    - [ ] cheatsheets/interview/code-quality/refactoring-patterns-interview.md
    - [ ] cheatsheets/interview/code-quality/static-analysis-interview.md
    - [ ] cheatsheets/interview/code-quality/technical-debt-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=code-quality ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/code-quality/
    - [ ] git commit learning(<topic>)

## Phase 20 — Behavioral & Leadership

- [ ] Cycle 59 — Behavioral STAR (Conflict, Failure, Culture-fit)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/behavioral/behavioral-interview.md
    - [ ] cheatsheets/interview/behavioral/conflict-stories-interview.md
    - [ ] cheatsheets/interview/behavioral/culture-fit-interview.md
    - [ ] cheatsheets/interview/behavioral/failure-stories-interview.md
    - [ ] cheatsheets/interview/behavioral/leadership-stories-interview.md
    - [ ] cheatsheets/interview/behavioral/star-method-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=behavioral ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/behavioral/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 60 — Leadership (Mentoring, Tech-decisions, Estimations)
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/leadership/code-review-practices-interview.md
    - [ ] cheatsheets/interview/leadership/conflict-resolution-interview.md
    - [ ] cheatsheets/interview/leadership/estimations-planning-interview.md
    - [ ] cheatsheets/interview/leadership/mentoring-interview.md
    - [ ] cheatsheets/interview/leadership/team-leadership-interview.md
    - [ ] cheatsheets/interview/leadership/tech-interviewing-interview.md
    - [ ] cheatsheets/interview/leadership/technical-decisions-interview.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=leadership ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/leadership/
    - [ ] git commit learning(<topic>)

## Phase 21 — Trainers (hands-on)

- [ ] Cycle 61 — SQL Trainer (LeetCode SQL + analytical)
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://leetcode.com/problemset/database/
    - [ ] https://www.sql-ex.ru/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] Расширить cheatsheet в cheatsheets/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 62 — Git Trainer (rebase, bisect, reflog)
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://learngitbranching.js.org/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] Расширить cheatsheet в cheatsheets/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 63 — Docker Trainer
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://labs.play-with-docker.com/
    - [ ] https://docker-curriculum.com/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] Расширить cheatsheet в cheatsheets/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 64 — Kubernetes Trainer (kind/minikube)
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://www.katacoda.com/courses/kubernetes
    - [ ] https://kind.sigs.k8s.io/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] Расширить cheatsheet в cheatsheets/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 65 — DSA Visualizer (VisuAlgo + Algorithm Visualizer)
  - [ ] Тренажёры / онлайн-ресурсы
    - [ ] https://visualgo.net/en
    - [ ] https://algorithm-visualizer.org/
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] Расширить cheatsheet в cheatsheets/
    - [ ] git commit learning(<topic>)

## Phase 22 — Misc

- [ ] Cycle 66 — Interview preparation meta
  - [ ] Прочитать свои шпаргалки
    - [ ] cheatsheets/interview/preparation/interview-preparation.md
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] 20 MCQ topic=preparation ≥80%
    - [ ] Расширить cheatsheet в cheatsheets/preparation/
    - [ ] git commit learning(<topic>)

- [ ] Cycle 67 — Cheat-sheet maintenance & meta-skills
  - [ ] Прогнать букмарки Chrome по теме → выписать holes
  - [ ] Закрыть holes — точечное чтение
  - [ ] Hands-on
  - [ ] Quiz & commit
    - [ ] Расширить cheatsheet в cheatsheets/
    - [ ] git commit learning(<topic>)

