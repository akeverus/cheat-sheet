---
title: "Вопросы на собеседовании: Spring Data R2DBC"
description: "Spring Data R2DBC для реактивной работы с БД: ReactiveCrudRepository, R2dbcEntityTemplate, реактивные транзакции, DatabaseClient, миграции, интеграция с WebFlux"
tags:
  - interview
  - spring
  - spring-r2dbc-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Data R2DBC"
  - "Spring R2DBC interview"
  - "Spring R2DBC собеседование"
prerequisites:
  - "[[spring-r2dbc]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Data R2DBC`

`Spring Data R2DBC` — реактивная альтернатива JDBC для доступа к реляционным БД. Использует non-blocking драйверы (`r2dbc-postgresql`, `r2dbc-mysql`, `r2dbc-h2`), интегрируется с Spring WebFlux и возвращает `Mono`/`Flux` вместо синхронных типов. Часто спрашивается вместе с WebFlux.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Data R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/) — официальная документация
- [R2DBC Spec](https://r2dbc.io/) — спецификация Reactive Relational Database Connectivity
- [Baeldung: Spring R2DBC](https://www.baeldung.com/spring-data-r2dbc) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое R2DBC и чем он отличается от JDBC?

**R2DBC (Reactive Relational Database Connectivity)** — реактивный стандарт доступа к реляционным БД, основанный на Reactive Streams (Project Reactor / RxJava).

| Критерий | JDBC | R2DBC |
|----------|------|-------|
| I/O модель | Блокирующий | Неблокирующий |
| Тип ответа | `ResultSet` | `Publisher<Row>` (Flux/Mono) |
| Thread per connection | Да | Нет |
| Транзакции | `DataSourceTransactionManager` | `R2dbcTransactionManager` |
| Joins / lazy loading | JPA-стиль | Ручное маппирование |
| Идеален для | Традиционных Spring MVC | Spring WebFlux |

R2DBC не является надстройкой над JDBC — это полностью отдельный API с нативной реактивностью.


> [!mcq]
> - [ ] R2DBC — это обёртка над JDBC с async адаптером | ❌ ПОСЛЕДСТВИЕ: R2DBC полностью отдельный API с нативными non-blocking драйверами; смешивание JDBC и R2DBC в одном потоке приводит к thread blocking
> - [ ] R2DBC поддерживает JPA-стиль с @Entity, @OneToMany и lazy loading | ❌ ПОСЛЕДСТВИЕ: R2DBC не поддерживает JPA lazy loading и joins автоматически; нужно ручное маппирование или @Query
> - [x] Non-blocking API поверх Reactive Streams; Publisher<Row> вместо ResultSet; R2dbcTransactionManager; нет JPA lazy loading | ✓ ПРИМЕНЯТЬ: Spring WebFlux + реактивный доступ к БД без thread-per-connection 📋 ПРАВИЛО: R2DBC = Reactive Streams + SQL; не замена JPA — другой уровень абстракции 🔗 См. Q2
> - [ ] R2DBC возвращает CompletableFuture<List<Row>> как стандартный async тип | ❌ ПОСЛЕДСТВИЕ: R2DBC возвращает Publisher<Row> (Flux/Mono); CompletableFuture — Java concurrent, не Reactive Streams

## Q2. Как настроить Spring Data R2DBC?

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
</dependency>
```

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/mydb
    username: user
    password: secret
    pool:
      initial-size: 5
      max-size: 20
```

```java
// Кастомная конфигурация с пулом соединений
@Bean
public ConnectionFactory connectionFactory() {
    ConnectionFactory factory = ConnectionFactories.get(
        ConnectionFactoryOptions.builder()
            .option(DRIVER, "postgresql")
            .option(HOST, "localhost")
            .option(PORT, 5432)
            .option(DATABASE, "mydb")
            .option(USER, "user")
            .option(PASSWORD, "secret")
            .build()
    );
    return ConnectionPoolConfiguration.builder(factory)
        .maxSize(20)
        .initialSize(5)
        .maxIdleTime(Duration.ofMinutes(30))
        .build();
}
```


> [!mcq]
> - [ ] spring.datasource.url вместо spring.r2dbc.url для PostgreSQL | ❌ ПОСЛЕДСТВИЕ: datasource.url = JDBC URL (jdbc:postgresql://...); r2dbc URL формат другой (r2dbc:postgresql://...); приложение не стартует
> - [x] spring-boot-starter-data-r2dbc + r2dbc-postgresql стартер; spring.r2dbc.url=r2dbc:postgresql://...; ConnectionPoolConfiguration для кастомного pool | ✓ ПРИМЕНЯТЬ: реактивный доступ к PostgreSQL с connection pooling 📋 ПРАВИЛО: r2dbc URL = r2dbc:<driver>://<host>/<db>; pool через ConnectionPoolConfiguration 🔗 См. Q3
> - [ ] R2DBC автоматически использует JDBC connection pool (HikariCP) | ❌ ПОСЛЕДСТВИЕ: R2DBC использует свой reactive connection pool (r2dbc-pool), несовместимый с HikariCP blocking API
> - [ ] max-size в r2dbc pool равен числу CPU cores по умолчанию | ❌ ПОСЛЕДСТВИЕ: default max-size = 10 не зависит от CPU; нужно явно настраивать под нагрузку

## Q3. Как создать реактивный репозиторий?

```java
@Table("orders")
public record Order(
    @Id Long id,
    String customerId,
    BigDecimal total,
    OrderStatus status,
    LocalDateTime createdAt
) {}

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    // Derived query method → автогенерация SQL
    Flux<Order> findByCustomerId(String customerId);

    Flux<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    // Кастомный запрос
    @Query("SELECT * FROM orders WHERE total > :minTotal AND status = :status")
    Flux<Order> findByMinTotalAndStatus(BigDecimal minTotal, OrderStatus status);

    Mono<Long> countByCustomerIdAndStatus(String customerId, OrderStatus status);
}
```

```java
// Использование
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public Flux<Order> getPendingOrders(String customerId) {
        return repository.findByCustomerId(customerId)
            .filter(o -> o.status() == OrderStatus.PENDING);
    }
}
```


> [!mcq]
> - [ ] ReactiveCrudRepository поддерживает только findById/findAll — нет derived query methods | ❌ ПОСЛЕДСТВИЕ: derived queries (findByCustomerId, findByStatusOrderByCreatedAtDesc) полностью поддерживаются аналогично JPA
> - [ ] @Query в R2DBC репозитории использует JPQL вместо SQL | ❌ ПОСЛЕДСТВИЕ: R2DBC @Query принимает native SQL; JPQL = JPA; смешивание вызовет syntax error или UnsupportedOperationException
> - [x] ReactiveCrudRepository<T, ID> + derived query methods → Flux/Mono; @Query для кастомного SQL; @Table/@Id для маппирования | ✓ ПРИМЕНЯТЬ: стандартный реактивный CRUD без boilerplate 📋 ПРАВИЛО: extends ReactiveCrudRepository → Spring Data генерирует реактивный SQL 🔗 См. Q4
> - [ ] Reactive репозиторий не поддерживает @Transactional — только TransactionalOperator | ❌ ПОСЛЕДСТВИЕ: @Transactional работает через AOP и на reactive методах при правильной конфигурации R2dbcTransactionManager

## Q4. Что такое R2dbcEntityTemplate и когда его использовать?

`R2dbcEntityTemplate` — низкоуровневый API для сложных запросов, которые не выражаются через методы репозитория.

```java
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final R2dbcEntityTemplate template;

    public Flux<Order> findHighValueOrders(BigDecimal threshold) {
        return template.select(Order.class)
            .matching(query(where("total").greaterThan(threshold)
                .and("status").is(OrderStatus.CONFIRMED)))
            .all();
    }

    public Mono<Order> updateStatus(Long id, OrderStatus newStatus) {
        return template.update(Order.class)
            .matching(query(where("id").is(id)))
            .apply(update("status", newStatus))
            .thenReturn(id)
            .flatMap(template.selectOne(query(where("id").is(id)), Order.class));
    }

    // DELETE с условием
    public Mono<Long> deleteOldCancelledOrders(LocalDateTime before) {
        return template.delete(Order.class)
            .matching(query(where("status").is(OrderStatus.CANCELLED)
                .and("created_at").lessThan(before)))
            .all();
    }
}
```

Репозиторий — для стандартных CRUD, `R2dbcEntityTemplate` — для сложной фильтрации/обновлений, `DatabaseClient` — для raw SQL.


> [!mcq]
> - [ ] R2dbcEntityTemplate заменяет репозиторий — нужно выбрать только одно | ❌ ПОСЛЕДСТВИЕ: оба используются вместе: репозиторий для стандартных операций, R2dbcEntityTemplate для сложных условий/bulk updates
> - [ ] R2dbcEntityTemplate требует написания SQL строками как DatabaseClient | ❌ ПОСЛЕДСТВИЕ: R2dbcEntityTemplate использует fluent API (query(where(...))) — type-safe; DatabaseClient — raw SQL; это разные уровни абстракции
> - [x] Fluent API: template.select(T.class).matching(query(where(...))).all() для чтения; template.update(T.class).apply(update(...)) для partial updates | ✓ ПРИМЕНЯТЬ: сложная фильтрация + partial update без raw SQL 📋 ПРАВИЛО: Repository=CRUD; R2dbcEntityTemplate=complex criteria; DatabaseClient=raw SQL 🔗 См. Q3
> - [ ] R2dbcEntityTemplate не поддерживает DELETE с условием — только deleteById | ❌ ПОСЛЕДСТВИЕ: template.delete(T.class).matching(query(...)).all() — поддерживает DELETE с произвольным критерием

## Q5. Как работают реактивные транзакции?

В реактивном стеке контекст транзакции хранится не в `ThreadLocal`, а в `Reactor Context`. `@Transactional` работает через AOP + `TransactionalOperator`.

```java
@Service
@RequiredArgsConstructor
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // @Transactional работает с Spring WebFlux без изменений
    @Transactional
    public Mono<Order> processOrder(PlaceOrderCommand cmd) {
        return orderRepository.save(new Order(cmd))
            .flatMap(order -> paymentRepository.reserve(order.getId(), order.total())
                .thenReturn(order))
            .onErrorResume(e -> Mono.error(new OrderProcessingException(e)));
        // При ошибке — автоматический rollback
    }
}
```

```java
// Программный контроль транзакций
@Bean
public TransactionalOperator transactionalOperator(ReactiveTransactionManager txm) {
    return TransactionalOperator.create(txm);
}

public Mono<Order> processWithOperator(PlaceOrderCommand cmd) {
    return orderRepository.save(new Order(cmd))
        .flatMap(order -> paymentRepository.reserve(order.getId(), order.total())
            .thenReturn(order))
        .as(transactionalOperator::transactional);
}
```


> [!mcq]
> - [ ] @Transactional в реактивном коде использует ThreadLocal для хранения контекста | ❌ ПОСЛЕДСТВИЕ: ThreadLocal не работает в reactive scheduler; R2DBC @Transactional использует Reactor Context для propagation
> - [ ] TransactionalOperator требует ручного beginTransaction/commit в коде | ❌ ПОСЛЕДСТВИЕ: .as(transactionalOperator::transactional) автоматически управляет begin/commit/rollback; ручное управление не нужно
> - [x] @Transactional работает через Reactor Context (не ThreadLocal); контекст передаётся через Mono chain; при ошибке — автоматический rollback | ✓ ПРИМЕНЯТЬ: декларативные транзакции в WebFlux без изменений API 📋 ПРАВИЛО: Reactor Context = reactive ThreadLocal; @Transactional работает через AOP 🔗 См. Q5
> - [ ] R2dbcTransactionManager несовместим с @Transactional — нужен только TransactionalOperator | ❌ ПОСЛЕДСТВИЕ: R2dbcTransactionManager полностью совместим с @Transactional через Spring AOP; это стандартный подход

## Q6. Как решается проблема N+1 в Spring Data R2DBC?

R2DBC **не поддерживает lazy loading** (нет JPA-прокси). Каждое связанное поле нужно загружать явно.

```java
// Проблема: N+1 при загрузке заказов с позициями
// НЕПРАВИЛЬНО — N запросов к БД
public Flux<OrderWithItems> loadOrdersNaively(String customerId) {
    return orderRepository.findByCustomerId(customerId)
        .flatMap(order -> itemRepository.findByOrderId(order.id())
            .collectList()
            .map(items -> new OrderWithItems(order, items)));
}

// ПРАВИЛЬНО — JOIN в одном запросе
@Query("""
    SELECT o.id, o.customer_id, o.total, o.status,
           i.id as item_id, i.product_id, i.quantity, i.price
    FROM orders o
    LEFT JOIN order_items i ON i.order_id = o.id
    WHERE o.customer_id = :customerId
    """)
Flux<Map<String, Object>> findOrdersWithItems(String customerId);

// Или batching через DatabaseClient
public Flux<OrderWithItems> loadOrdersBatched(String customerId) {
    return orderRepository.findByCustomerId(customerId)
        .collectList()
        .flatMapMany(orders -> {
            List<Long> ids = orders.stream().map(Order::id).toList();
            return itemRepository.findByOrderIdIn(ids)
                .collectMultimap(OrderItem::orderId)
                .flatMapMany(itemsByOrder ->
                    Flux.fromIterable(orders)
                        .map(o -> new OrderWithItems(o,
                            itemsByOrder.getOrDefault(o.id(), List.of())))
                );
        });
}
```


> [!mcq]
> - [ ] R2DBC с flatMap(order → itemRepository.findByOrderId()) не создаёт N+1 — всё реактивное | ❌ ПОСЛЕДСТВИЕ: реактивность не устраняет N+1; flatMap создаёт N отдельных DB-запросов; нужен JOIN или batch-загрузка
> - [ ] JPA @OneToMany решает N+1 автоматически через JOIN FETCH | ❌ ПОСЛЕДСТВИЕ: без @EntityGraph или JOIN FETCH → LazyInitializationException или N+1; в R2DBC это проблема решается явно
> - [x] JOIN в @Query для join-запроса; или batch-загрузка через collectList() → findByOrderIdIn(ids) → group by orderId | ✓ ПРИМЕНЯТЬ: избежать N+1 при загрузке сущностей с отношениями 📋 ПРАВИЛО: R2DBC нет lazy loading → JOIN в @Query ИЛИ batch-load с IN clause 🔗 См. Q3
> - [ ] DatabaseClient.sql(JOIN запрос) не работает с маппированием в entity | ❌ ПОСЛЕДСТВИЕ: DatabaseClient возвращает Map<String, Object>; маппирование в entity делается вручную или через RowMapper

## Q7. Как настроить Connection Pool?

```java
@Bean
public ConnectionFactory connectionFactory() {
    PostgresqlConnectionFactory pgFactory = new PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host("localhost").port(5432)
            .database("mydb")
            .username("user").password("secret")
            .build()
    );

    return new ConnectionPool(
        ConnectionPoolConfiguration.builder(pgFactory)
            .initialSize(5)
            .maxSize(20)
            .maxIdleTime(Duration.ofMinutes(30))
            .maxAcquireTime(Duration.ofSeconds(5))  // таймаут ожидания соединения
            .validationQuery("SELECT 1")
            .build()
    );
}
```

Основные параметры пула:
- `initialSize` — соединения при старте
- `maxSize` — максимальный размер пула
- `maxIdleTime` — время простоя до закрытия соединения
- `maxAcquireTime` — максимальное время ожидания соединения из пула


> [!mcq]
> - [ ] maxAcquireTime — максимальное время удержания соединения в транзакции | ❌ ПОСЛЕДСТВИЕ: maxAcquireTime = ожидание свободного соединения из пула; maxIdleTime = время простоя до закрытия соединения
> - [ ] ConnectionPool использует HikariCP внутри для управления соединениями | ❌ ПОСЛЕДСТВИЕ: HikariCP = blocking JDBC pool; r2dbc-pool — отдельная реактивная реализация, несовместимая с HikariCP
> - [x] ConnectionPoolConfiguration: initialSize, maxSize, maxIdleTime (закрытие idle), maxAcquireTime (timeout ожидания), validationQuery | ✓ ПРИМЕНЯТЬ: production-ready connection pooling для R2DBC 📋 ПРАВИЛО: maxSize ≤ DB max_connections; maxAcquireTime → ConnectionTimeoutException при overflow 🔗 См. Q2
> - [ ] R2DBC без ConnectionPool — stateless, создаёт новое соединение на каждый запрос | ❌ ПОСЛЕДСТВИЕ: без пула каждый запрос = новый TCP handshake + authentication; latency spike при высокой нагрузке

## Q8. Как тестировать Spring Data R2DBC?

```java
// @DataR2dbcTest загружает только R2DBC-слой, без @Service/@Controller
@DataR2dbcTest
@Import(TestDatabaseConfig.class)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Test
    void shouldSaveAndFindOrder() {
        Order order = new Order(null, "customer-1", new BigDecimal("99.99"),
            OrderStatus.PENDING, LocalDateTime.now());

        StepVerifier.create(
                repository.save(order)
                    .flatMap(saved -> repository.findById(saved.id()))
            )
            .assertNext(found -> {
                assertThat(found.customerId()).isEqualTo("customer-1");
                assertThat(found.status()).isEqualTo(OrderStatus.PENDING);
            })
            .verifyComplete();
    }
}
```

```java
// Интеграционный тест с Testcontainers
@SpringBootTest
@Testcontainers
class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url",
            () -> "r2dbc:postgresql://localhost:" + postgres.getMappedPort(5432) + "/test");
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }
}
```


> [!mcq]
> - [ ] @DataR2dbcTest загружает полный Spring Boot контекст с @Service и @Controller | ❌ ПОСЛЕДСТВИЕ: @DataR2dbcTest — slice test: только R2DBC beans; нет @Service/@Controller; для полного контекста — @SpringBootTest
> - [ ] StepVerifier не нужен — можно использовать обычный assert на Mono.block() | ❌ ПОСЛЕДСТВИЕ: .block() в тесте блокирует reactor scheduler; StepVerifier — правильный способ тестировать Publisher; block() в production коде — антипаттерн
> - [x] @DataR2dbcTest для repository slice; StepVerifier.create(publisher).assertNext().verifyComplete(); Testcontainers + @DynamicPropertySource для integration тестов | ✓ ПРИМЕНЯТЬ: изолированное тестирование реактивных репозиториев 📋 ПРАВИЛО: StepVerifier = правильный способ тестировать Flux/Mono; block() только в тестах крайней необходимости 🔗 См. Q3
> - [ ] @DynamicPropertySource поддерживает только JDBC URL — не r2dbc | ❌ ПОСЛЕДСТВИЕ: @DynamicPropertySource инжектирует любые properties включая spring.r2dbc.*; это generic механизм без ограничений на тип URL

## Q9. Когда использовать R2DBC, а когда JPA?

| Критерий | R2DBC | JPA/Hibernate |
|----------|-------|---------------|
| Стек приложения | WebFlux (реактивный) | Spring MVC (сервлетный) |
| Производительность под нагрузкой | Лучше при высоком I/O concurrency | Лучше для CRUD с небольшим числом потоков |
| Сложные маппинги (relations, lazy) | Ручное, нет proxy | Автоматическое через JPA |
| Простота кода | Сложнее (joins вручную) | Проще (JPQL, Criteria API) |
| Кэш первого/второго уровня | Нет | Есть (Hibernate) |
| Миграции схемы | Flyway (синхронный) | Flyway / Liquibase |

**Правило**: R2DBC — если приложение полностью реактивное (WebFlux). JPA — если Spring MVC или нужны сложные ORM-маппинги.


> [!mcq]
> - [ ] R2DBC лучше JPA во всех случаях из-за non-blocking I/O | ❌ ПОСЛЕДСТВИЕ: при небольшом числе concurrent requests JDBC+HikariCP одинаково эффективен; JPA даёт преимущества в сложных ORM-маппингах
> - [x] R2DBC: WebFlux + высокий I/O concurrency + нет нужды в JPA lazy loading; JPA: Spring MVC + сложные relations + кэш первого/второго уровня | ✓ ПРИМЕНЯТЬ: R2DBC только в полностью реактивном стеке 📋 ПРАВИЛО: R2DBC + JPA в одном приложении нежелательно; выбери один подход 🔗 См. Q1
> - [ ] JPA и R2DBC можно смешивать: JPA для reads, R2DBC для writes | ❌ ПОСЛЕДСТВИЕ: смешивание создаёт проблемы с transaction management и schema migration; лучше выбрать единый подход
> - [ ] R2DBC поддерживает second-level cache через Hibernate | ❌ ПОСЛЕДСТВИЕ: Hibernate/JPA second-level cache несовместим с R2DBC; для кэширования в реактивном стеке — Caffeine или Redis

## Q10. Как обрабатывать ошибки в R2DBC?

```java
public Mono<Order> findOrThrow(Long id) {
    return orderRepository.findById(id)
        .switchIfEmpty(Mono.error(new OrderNotFoundException(id)));
}

public Mono<Order> saveWithRetry(Order order) {
    return orderRepository.save(order)
        .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
            .filter(e -> e instanceof R2dbcTransientResourceException))
        .onErrorMap(R2dbcDataIntegrityViolationException.class,
            e -> new DuplicateOrderException(order.id()));
}

// Глобальный обработчик в @RestControllerAdvice
@ExceptionHandler(R2dbcDataIntegrityViolationException.class)
public ResponseEntity<ErrorDto> handleR2dbcError(R2dbcDataIntegrityViolationException e) {
    return ResponseEntity.status(409).body(new ErrorDto("Constraint violation: " + e.getMessage()));
}
```


> [!mcq]
> - [ ] switchIfEmpty(Mono.error(...)) выполняется всегда, даже если значение есть | ❌ ПОСЛЕДСТВИЕ: Mono.error() внутри switchIfEmpty ленивый — выполняется только если upstream empty; нет лишних исключений
> - [ ] retryWhen без filter повторяет при всех ошибках включая non-transient | ❌ ПОСЛЕДСТВИЕ: retry без filter повторяет при constraint violation, auth error и т.д. — лишние retries; нужен .filter(e → e instanceof R2dbcTransientResourceException)
> - [x] switchIfEmpty(Mono.error()) для 404; onErrorMap для маппирования R2DBC exceptions; retryWhen(Retry.backoff().filter()) для transient errors | ✓ ПРИМЕНЯТЬ: reactive error handling без блокирующих try/catch 📋 ПРАВИЛО: switchIfEmpty = 404; onErrorMap = exception translation; retryWhen = transient retry 🔗 См. Q5
> - [ ] @ExceptionHandler не работает с Mono/Flux — нужен WebExceptionHandler | ❌ ПОСЛЕДСТВИЕ: @ExceptionHandler в @RestControllerAdvice работает с reactive методами через WebFlux exception resolution

## Q11. Как работает DatabaseClient для raw SQL?

```java
@Service
@RequiredArgsConstructor
public class ReportService {
    private final DatabaseClient client;

    public Flux<OrderSummary> getOrderSummary(LocalDate from, LocalDate to) {
        return client.sql("""
                SELECT customer_id,
                       COUNT(*) as order_count,
                       SUM(total) as total_amount
                FROM orders
                WHERE created_at BETWEEN :from AND :to
                GROUP BY customer_id
                ORDER BY total_amount DESC
                """)
            .bind("from", from.atStartOfDay())
            .bind("to", to.plusDays(1).atStartOfDay())
            .map((row, meta) -> new OrderSummary(
                row.get("customer_id", String.class),
                row.get("order_count", Long.class),
                row.get("total_amount", BigDecimal.class)
            ))
            .all();
    }
}
```


> [!mcq]
>
> **Вопрос:** Зачем нужен `DatabaseClient` если уже есть `R2dbcRepository`, и в чём ключевое различие подходов?
>
> ---
>
> #### A) `DatabaseClient` — это устаревший API, замена для `R2dbcRepository` в новых проектах не нужна — ❌ Неверно
>
> **Что на самом деле:** `DatabaseClient` и `R2dbcRepository` — **дополняющие** API, не конкурирующие. Repository даёт CRUD-абстракцию для entity-based операций (`save`, `findById`, `findAll`). DatabaseClient — низкоуровневый клиент для произвольного SQL с reactive типизированным mapping. Оба активно развиваются в Spring Data R2DBC.
>
> **Откуда путаница:** в Spring Data JPA есть `JdbcTemplate` как «легаси» альтернатива JPA. По аналогии можно подумать что `DatabaseClient` — такой же old-school. На деле он именно reactive-first, не legacy.
>
> **Если бы это было правдой:** пришлось бы делать все aggregation-запросы и сложные JOIN-ы через `@Query` в Repository, что лишало бы гибкости (например, динамический WHERE/GROUP BY в зависимости от runtime-параметров). На сложной аналитике это блокирует фичу.
>
> ---
>
> #### B) `DatabaseClient` нужен для произвольного SQL с reactive mapping когда Repository слишком ограничен (агрегаты, JOIN-ы, dynamic queries) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `R2dbcRepository<T, ID>` хорош для CRUD по сущностям: `save`, `findById`, `findAll`, derived queries (`findByCustomerId`), `@Query("SELECT ...")`. Но он ограничен **типом result** — возвращает `Flux<T>` где T — entity. Для агрегатов (`SUM`, `COUNT`, `GROUP BY`), JOIN-ов с проекциями, динамических queries он неудобен.
>
> `DatabaseClient` — это reactive `JdbcTemplate`: пишешь raw SQL, биндишь параметры через `.bind()`, маппишь row → DTO через `.map((row, meta) -> ...)`, возвращаешь `Flux<DTO>`/`Mono<DTO>`. Это даёт полный контроль над SQL и проекциями.
>
> Под капотом оба используют один `ConnectionFactory` — это просто разные API над одним пулом.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class ReportService {
>     private final DatabaseClient client;
>
>     public Flux<OrderSummary> getOrderSummary(LocalDate from, LocalDate to) {
>         return client.sql("""
>                 SELECT customer_id,
>                        COUNT(*) as order_count,
>                        SUM(total) as total_amount
>                 FROM orders
>                 WHERE created_at BETWEEN :from AND :to
>                 GROUP BY customer_id
>                 ORDER BY total_amount DESC
>                 """)
>             .bind("from", from.atStartOfDay())
>             .bind("to", to.plusDays(1).atStartOfDay())
>             .map((row, meta) -> new OrderSummary(
>                 row.get("customer_id", String.class),
>                 row.get("order_count", Long.class),
>                 row.get("total_amount", BigDecimal.class)
>             ))
>             .all();   // или .one() / .first()
>     }
> }
> ```
>
> **Когда применять:**
> - **Аналитические запросы** в Spring WebFlux endpoints — отчёты, dashboards, метрики, где результат не маппируется на entity.
> - **Динамические WHERE/ORDER BY** на основе runtime параметров — Repository `@Query` требует статический SQL.
> - **Batch insert** — `client.inConnectionMany(conn -> ...)` даёт доступ к `Statement.add()` для накопления параметров перед `execute()` (см. Q13).
> - **Сложные SQL constructions**: CTE, window functions, recursive queries. В Repository это `@Query` со строкой; в DatabaseClient — текстовый блок Java 15+ с подсветкой синтаксиса.
>
> **Подводные камни:**
> - **Schema-less mapping**: `row.get("col", Class)` — runtime типизация. Опечатка в имени колонки = `IllegalArgumentException` только при выполнении, не при компиляции. Спасает интеграционный тест.
> - **N+1 проблема всё ещё актуальна**: `flatMap` запросов в цикле даст N+1 даже в реактивном стеке. Решение — JOIN в SQL, не N reactive calls.
> - **Параметры через `:name` (named) или `$1`/`$2` (positional)**: R2DBC native — positional `$N`, Spring добавляет named как обёртку. На некоторых драйверах (например, MS SQL Server R2DBC) named параметры не работают без `.bind("name", value)`.
> - **No automatic transaction management для raw SQL**: `@Transactional` работает с DatabaseClient методами через `TransactionalOperator`, но требует явной обёртки если транзакция нужна между несколькими SQL вызовами.
>
> **Связанные вопросы:** [[Q12]] — custom converters для R2DBC mapping; [[Q13]] — batch insert через `DatabaseClient.inConnectionMany`; [[Q5]] — реактивные транзакции с `TransactionalOperator`.
>
> ---
>
> #### C) `DatabaseClient` работает синхронно (блокирует поток) — поэтому его не нужно использовать в WebFlux — ❌ Неверно
>
> **Что на самом деле:** `DatabaseClient` полностью **reactive**. Возвращает `Mono`/`Flux`, использует non-blocking R2DBC драйверы (например, `r2dbc-postgresql`). Поток не блокируется на ожидании БД.
>
> **Откуда путаница:** имя «`DatabaseClient`» похоже на `JdbcTemplate`, который синхронный. Но R2DBC — Reactive Relational Database Connectivity, и весь его стек non-blocking.
>
> **Если бы это было правдой:** использование `DatabaseClient` в WebFlux endpoint блокировало бы event-loop поток, что нивелировало бы все преимущества reactive стека. На практике с DatabaseClient в WebFlux держат тысячи concurrent connections на одной JVM.
>
> ---
>
> #### D) `DatabaseClient` нужен только для миграций — Flyway/Liquibase не работают с R2DBC — ❌ Неверно
>
> **Что на самом деле:** `DatabaseClient` — для **runtime SQL запросов** приложения, не для миграций. Миграции (Flyway/Liquibase) запускаются через **JDBC** (синхронный) при старте приложения — отдельный datasource, ничего общего с DatabaseClient (см. Q14).
>
> **Откуда путаница:** оба касаются SQL и БД. Но миграции — однократная операция при boot (когда блокирующий I/O не критичен), а DatabaseClient — для тысяч RPS reactive запросов.
>
> **Если бы это было правдой:** миграции в R2DBC-приложении пришлось бы писать reactive — что нереально (Flyway/Liquibase synchronous by design). Реальный подход: JDBC для миграций + R2DBC для runtime.

## Q12. Как работают custom converters в R2DBC?

```java
// Для хранения enum как строки (по умолчанию — по индексу)
@WritingConverter
public class OrderStatusWriteConverter implements Converter<OrderStatus, String> {
    @Override
    public String convert(OrderStatus source) {
        return source.name();
    }
}

@ReadingConverter
public class OrderStatusReadConverter implements Converter<String, OrderStatus> {
    @Override
    public OrderStatus convert(String source) {
        return OrderStatus.valueOf(source);
    }
}

@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    @Override
    protected List<Object> getCustomConverters() {
        return List.of(new OrderStatusWriteConverter(), new OrderStatusReadConverter());
    }
}
```


> [!mcq]
>
> **Вопрос:** Почему для хранения enum как строки в R2DBC нужны custom converters, и что произойдёт без них?
>
> ---
>
> #### A) Без converter R2DBC сохранит enum как `null` — поле станет пустым — ❌ Неверно
>
> **Что на самом деле:** R2DBC по умолчанию маппит enum **по индексу** (`ordinal()`), не как `null`. Т.е. `OrderStatus.PENDING` (индекс 0) → в БД сохранится число `0`. Это работает, но создаёт ловушку: если кто-то добавит новый статус в середину enum, все существующие записи сдвинутся.
>
> **Откуда путаница:** в JPA с `@Enumerated` без аргумента используется `ORDINAL` (по индексу), но многие думают что без аннотации значение `null`. R2DBC аналогично — есть дефолтное поведение, не null.
>
> **Если бы это было правдой:** статус не сохранялся бы вообще, и приложение падало бы на `NOT NULL constraint violation`. На практике сохраняется, но как число — что плохо для эволюции схемы.
>
> ---
>
> #### B) Custom converters нужны только для типов которых нет в стандартной библиотеке Java — ❌ Неверно
>
> **Что на самом деле:** custom converters нужны для **любой нестандартной семантики маппинга**, не только для exotic типов. Enum → String, BigDecimal → custom precision, UUID ↔ String/binary, MoneyAmount (custom value object) — всё это enum-в-string, primitive-в-class, не только «отсутствующие типы».
>
> **Откуда путаница:** Spring документация часто показывает converters как «extension point для новых типов». Но это лишь один из use-cases — реальная цель шире.
>
> **Если бы это было правдой:** мы не могли бы переопределить дефолтное поведение для enum-а, BigDecimal precision, UUID format — пришлось бы манипулировать схемой или DTO для каждого запроса.
>
> ---
>
> #### C) Custom converters переопределяют дефолтное маппирование (enum по индексу → enum как строка) через `@WritingConverter` + `@ReadingConverter`, регистрируются в `AbstractR2dbcConfiguration.getCustomConverters()` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Data R2DBC использует `MappingR2dbcConverter`, который применяет цепочку Converter-ов при чтении (DB → Java) и записи (Java → DB). Дефолтный converter для enum-а использует `ordinal()` — это компактно, но опасно при изменении порядка enum значений в коде.
>
> Альтернативы:
> 1. **Custom converter с String** — сохраняем `name()`, что устойчиво к перестановкам в enum (но не к переименованию).
> 2. **Database CHECK constraint** на колонке — типобезопасность на уровне БД.
> 3. **PostgreSQL ENUM type** — нативный enum в Postgres + custom converter Java enum ↔ Postgres enum.
>
> Регистрация — через расширение `AbstractR2dbcConfiguration` и метод `getCustomConverters()`. `@WritingConverter` — Java → DB, `@ReadingConverter` — DB → Java. Без аннотации Spring не понимает направление и игнорирует converter.
>
> **Пример:**
> ```java
> @WritingConverter
> public class OrderStatusWriteConverter implements Converter<OrderStatus, String> {
>     @Override
>     public String convert(OrderStatus source) {
>         return source.name();              // PENDING, PAID, SHIPPED — в БД как строка
>     }
> }
>
> @ReadingConverter
> public class OrderStatusReadConverter implements Converter<String, OrderStatus> {
>     @Override
>     public OrderStatus convert(String source) {
>         return OrderStatus.valueOf(source); // строка → enum value
>     }
> }
>
> @Configuration
> public class R2dbcConfig extends AbstractR2dbcConfiguration {
>     @Override
>     protected List<Object> getCustomConverters() {
>         return List.of(
>             new OrderStatusWriteConverter(),
>             new OrderStatusReadConverter()
>         );
>     }
>     // Также нужен @Override connectionFactory()
> }
> ```
>
> **Когда применять:**
> - **Enum-ы стабильные по name, нестабильные по порядку** — добавляете новый статус в середину, не ломается production.
> - **Custom value objects** (`Money`, `Email`, `UserId`) — маппинг в varchar/number.
> - **JSON-колонки** (`JSONB` в Postgres) — custom converter `Map<String, Object> ↔ String`.
> - **Encrypted fields** — converter с шифрованием/дешифровкой на лету.
> - **Hibernate-проекты, мигрирующие в R2DBC** — портирование `@Converter` логики на R2DBC API.
>
> **Подводные камни:**
> - **Аннотация обязательна**: без `@WritingConverter` / `@ReadingConverter` Spring не зарегистрирует converter правильно — silent no-op.
> - **`@JdbcTypeCode`** работает в Spring Data JDBC, не в R2DBC — синтаксис другой.
> - **Spring Boot autoconfig**: если есть `@EnableR2dbcRepositories`, custom converters не подхватываются автоматически — нужен `AbstractR2dbcConfiguration`.
> - **Direction sensitivity**: один и тот же класс не может быть и Reading, и Writing converter; нужно два разных класса (или anonymous inner classes).
>
> **Связанные вопросы:** [[Q11]] — DatabaseClient использует те же converters; [[Q15]] — интеграция с Spring Security UserDetailsService для UserStatus enum; [[Q4]] — entity mapping в Repository.
>
> ---
>
> #### D) Spring автоматически распознаёт enum по аннотации `@Enumerated(STRING)` как в JPA — ❌ Неверно
>
> **Что на самом деле:** `@Enumerated` — это **JPA-аннотация**, R2DBC её НЕ поддерживает. В R2DBC нет ничего эквивалентного — единственный способ переопределить дефолтное поведение через custom converters.
>
> **Откуда путаница:** разработчики приходят из Spring Data JPA и ожидают `@Enumerated(EnumType.STRING)`. R2DBC — отдельный стек с другой моделью маппинга, никаких JPA-аннотаций.
>
> **Если бы это было правдой:** мы могли бы избежать boilerplate с двумя converter-классами. На практике пишем converter + регистрацию = ~20 строк кода, к сожалению.

## Q13. Как выполнить batch insert?

```java
// Вариант 1 — через Repository (по одному, но асинхронно)
public Flux<Order> saveAll(List<Order> orders) {
    return orderRepository.saveAll(orders);
}

// Вариант 2 — DatabaseClient batch
public Mono<Void> batchInsert(List<Order> orders) {
    return client.inConnectionMany(conn ->
        Flux.fromIterable(orders)
            .buffer(500)  // батч по 500 записей
            .flatMap(batch -> {
                Statement stmt = conn.createStatement(
                    "INSERT INTO orders(customer_id, total, status) VALUES($1, $2, $3)");
                batch.forEach(o -> stmt
                    .bind(0, o.customerId())
                    .bind(1, o.total())
                    .bind(2, o.status().name())
                    .add());
                return stmt.execute();
            })
    ).then();
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Что такое реактивные миграции и как их применять? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

R2DBC-приложение не может использовать Flyway напрямую в реактивном режиме при старте. Решение — запустить Flyway через блокирующий JDBC при инициализации:

```java
@Bean(initMethod = "migrate")
public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
        .dataSource(dataSource)  // обычный JDBC DataSource для миграций
        .locations("classpath:db/migration")
        .load();
}

// application.yml: отключить автомиграцию Spring Boot
spring:
  flyway:
    enabled: false  # управляем вручную через bean выше
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/mydb
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb  # только для Flyway
```

Проект `r2dbc-migrate` — альтернативный инструмент с нативной поддержкой R2DBC.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Какова интеграция Spring Data R2DBC с WebFlux? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{customerId}")
    public Flux<OrderDto> getOrders(@PathVariable String customerId) {
        return orderService.findByCustomer(customerId)
            .map(OrderMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDto> createOrder(@RequestBody @Valid CreateOrderRequest req) {
        return orderService.create(req)
            .map(OrderMapper::toDto);
    }

    // Server-Sent Events — стриминг обновлений статусов
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<OrderDto> streamOrders() {
        return orderService.streamNewOrders()
            .map(OrderMapper::toDto);
    }
}
```

Нельзя смешивать R2DBC с блокирующим кодом (JPA) в одном потоке WebFlux — это уничтожит реактивный backpressure и заблокирует event loop.

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring WebFlux](spring-webflux-interview.md) — реактивный HTTP стек, идеально сочетается с R2DBC ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring Data JPA](spring-data-jpa-interview.md) — блокирующая альтернатива для синхронных приложений
- [Spring Data JDBC](spring-data-jdbc-interview.md) — lightweight JDBC без реактивности
- [Spring Boot](spring-boot-interview.md) — auto-configuration для R2DBC
- [Project Reactor](../../reactive/project-reactor-interview.md) — Mono/Flux API используемый в R2DBC
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — спецификация backpressure
- [Virtual Threads](../../programming-languages/java/java-virtual-threads-interview.md) — альтернатива реактивного подхода
- [Database Transactions](../../databases/database-transactions-interview.md) — теория транзакций, изоляция
- [PostgreSQL](../../databases/postgresql-interview.md) — r2dbc-postgresql драйвер
- [Spring @Transactional](spring-transaction-interview.md) — реактивные транзакции через ReactiveTransactionManager
