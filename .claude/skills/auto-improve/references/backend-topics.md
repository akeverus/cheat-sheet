# Целевое покрытие тем для backend-разработчика

Список тем, которые должны быть покрыты в `cheatsheets/` с достаточной глубиной.
Приоритет: ★★★ = критично, ★★ = важно, ★ = полезно.

## Java / JVM ★★★

- Java Core: коллекции, Stream API, Optional, concurrency (locks, atomics, executors)
- Java Memory Model: happens-before, volatile, synchronized
- JVM: GC алгоритмы (G1, ZGC, Shenandoah), JIT, class loading
- Java 17-21+: records, sealed classes, pattern matching, virtual threads

## Spring Framework ★★★

- Spring Core: IoC, DI, ApplicationContext, аннотации, profiles
- Spring Boot: auto-configuration, starters, actuator, properties
- Spring MVC: controllers, exception handling, validation, filters
- Spring Security: authentication, authorization, OAuth2, JWT
- Spring Data: JPA, JDBC, repositories, transactions (@Transactional)
- Spring WebFlux / Reactive: Project Reactor, Flux/Mono, backpressure
- Spring Testing: @SpringBootTest, MockMvc, TestContainers, @DataJpaTest

## Базы данных ★★★

- SQL: joins, subqueries, window functions, CTE, explain analyze
- PostgreSQL: индексы (B-tree, GIN, GiST, BRIN), partitioning, MVCC, vacuum, WAL
- Транзакции: ACID, уровни изоляции, deadlocks, optimistic/pessimistic locking
- Миграции: Flyway, Liquibase
- NoSQL: Redis (структуры данных, pub/sub, Lua), MongoDB (агрегации, индексы)
- Кэширование: стратегии (write-through, write-behind, cache-aside), инвалидация

## Архитектура ★★★

- Паттерны: SOLID, GoF (наиболее используемые), DDD tactical patterns
- Микросервисы: декомпозиция, saga, circuit breaker, service mesh
- Event-driven: Kafka (partitions, consumer groups, exactly-once), RabbitMQ
- API Design: REST (Richardson maturity), GraphQL, gRPC, versioning
- CQRS, Event Sourcing
- Clean Architecture, Hexagonal Architecture

## Алгоритмы и структуры данных ★★

- Сложность: Big-O, амортизированный анализ
- Структуры: hash tables, trees (BST, AVL, Red-Black, B-tree), heaps, graphs
- Алгоритмы: сортировки, поиск, BFS/DFS, Dijkstra, динамическое программирование
- Строки: KMP, Rabin-Karp, trie

## DevOps / Infrastructure ★★

- Docker: Dockerfile best practices, multi-stage, docker-compose
- Kubernetes: pods, deployments, services, ingress, configmaps, secrets, HPA
- CI/CD: Jenkins, GitLab CI, GitHub Actions
- Мониторинг: Prometheus, Grafana, ELK/EFK, distributed tracing (Jaeger)
- Логирование: structured logging, SLF4J, Logback, log levels

## Тестирование ★★

- Unit testing: JUnit 5, Mockito, AssertJ
- Integration testing: TestContainers, @SpringBootTest, WireMock
- TDD, BDD
- Нагрузочное: JMeter, Gatling, k6
- Contract testing: Pact

## Сети и протоколы ★★

- HTTP/HTTPS: методы, коды, headers, cookies, CORS
- TCP/IP: handshake, congestion control, keep-alive
- WebSocket, SSE, Long Polling
- DNS, load balancing (L4 vs L7)
- TLS: handshake, certificates, mutual TLS

## Безопасность ★★

- OWASP Top 10
- Аутентификация: OAuth2 flows, OpenID Connect, JWT (claims, signing)
- Авторизация: RBAC, ABAC
- Криптография: hashing (bcrypt, argon2), encryption (AES, RSA), digital signatures
- Secrets management: Vault, env vars, sealed secrets

## Системный дизайн ★★

- Scaling: горизонтальное vs вертикальное, sharding, replication
- CAP теорема, eventual consistency
- Rate limiting, throttling
- Distributed systems: consensus (Raft, Paxos), leader election
- Message queues vs event streams

## Kotlin ★

- Kotlin основы: null-safety, coroutines, extension functions, data classes
- Kotlin + Spring Boot
- Kotlin vs Java (когда что)

## Инструменты ★

- Git: branching strategies, rebase vs merge, bisect, cherry-pick
- Gradle: Kotlin DSL, dependency management, version catalogs
- Maven: POM, plugins, profiles
- IDE: IntelliJ shortcuts и приёмы
