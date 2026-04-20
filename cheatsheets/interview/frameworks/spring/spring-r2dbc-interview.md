---
title: "Spring R2DBC — Interview"
description: "Вопросы на собеседовании по Spring Data R2DBC: реактивные репозитории, транзакции, connection pool, тестирование, R2DBC vs JPA."
tags:
  - interview
  - spring
  - r2dbc
  - reactive
  - database
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring R2DBC — Interview

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

## Q14. Что такое реактивные миграции и как их применять?

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

## Q15. Какова интеграция Spring Data R2DBC с WebFlux?

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

- [[spring-r2dbc|Spring Data R2DBC]] — полный cheatsheet
- [[spring-webflux-interview|Spring WebFlux Interview]] — реактивный стек
- [[spring-data-jpa-interview|Spring Data JPA Interview]] — альтернатива для блокирующих приложений
- [[kafka-streams-interview|Kafka Streams Interview]] — реактивная потоковая обработка
- [[spring-boot-interview|Spring Boot Interview]] — общие вопросы Spring Boot
