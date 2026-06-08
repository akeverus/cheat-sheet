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
updated: 2026-05-31
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

**R2DBC (Reactive Relational Database Connectivity)** — реактивный стандарт доступа к реляционным БД поверх Reactive Streams (реализуется через Project Reactor / RxJava). Это полностью отдельный API, а не надстройка над JDBC.

Ключевое отличие — в I/O-модели. JDBC блокирует поток на всё время запроса (один поток на одно соединение), поэтому под высоким concurrency нужны сотни потоков, которые большую часть времени простаивают. R2DBC возвращает поток управления сразу, а строки доставляет асинхронно через `Publisher`, не держа поток занятым — поэтому небольшой пул потоков обслуживает тысячи одновременных запросов.

| Критерий | JDBC | R2DBC |
|----------|------|-------|
| I/O модель | Блокирующий | Неблокирующий |
| Тип ответа | `ResultSet` | `Publisher<Row>` (Flux/Mono) |
| Thread per connection | Да | Нет |
| Транзакции | `DataSourceTransactionManager` | `R2dbcTransactionManager` |
| Joins / lazy loading | JPA-стиль | Ручное маппирование |
| Идеален для | Традиционных Spring MVC | Spring WebFlux |

**Подводный камень:** реактивность не бесплатна. R2DBC не даёт lazy loading и ORM-маппинга связей — joins пишутся вручную, а для сложных реляционных моделей код получается заметно объёмнее, чем на JPA.

**Когда применять:** R2DBC оправдан только в полностью реактивном стеке (WebFlux), где узкое место — именно I/O и число одновременных соединений. Для классического CRUD на Spring MVC выигрыша нет — там проще и быстрее JDBC/JPA.

## Q2. Как настроить Spring Data R2DBC?

Минимально нужны две зависимости: стартер `spring-boot-starter-data-r2dbc` (репозитории, авто-конфигурация) и **неблокирующий драйвер** под конкретную БД (`r2dbc-postgresql`, `r2dbc-mysql`, `r2dbc-h2`). Обычный JDBC-драйвер сюда не подходит — у R2DBC своя реализация протокола.

Дальше есть два пути конфигурации:
- **Декларативный** — через `spring.r2dbc.*` в `application.yml`. Spring Boot сам соберёт `ConnectionFactory` и пул. Это путь по умолчанию.
- **Программный** — объявить бин `ConnectionFactory` вручную, когда нужны нестандартные опции драйвера или тонкая настройка пула.

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

Достаточно объявить интерфейс-наследник `ReactiveCrudRepository<T, ID>` — Spring Data сгенерирует реализацию на старте. Отличие от обычного `CrudRepository` в том, что все методы возвращают реактивные типы: `Mono<T>` для одной сущности, `Flux<T>` для коллекции. Ничего не выполняется, пока на результат не подпишутся.

Способы задать запрос (от простого к гибкому):
- **Derived query** — Spring выводит SQL из имени метода (`findByCustomerId`, `findByStatusOrderByCreatedAtDesc`). Подходит для простых выборок.
- **`@Query`** — собственный SQL с именованными параметрами (`:minTotal`), когда имя метода становится слишком громоздким.

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

`R2dbcEntityTemplate` — программный fluent-API для запросов, которые неудобно или невозможно выразить методом репозитория. В отличие от `@Query` с сырым SQL, он строит запрос типобезопасно через `query(where(...))`, `update(...)`, `select(Class)` и при этом сам маппит результат в сущность.

Главный плюс перед derived-методами — **динамика**: условия можно собирать в рантайме (по наличию фильтров, по правам пользователя), не плодя десятки именованных методов на все комбинации.

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

**Какой инструмент выбирать:**
- **Репозиторий** — стандартный CRUD и простые derived-запросы.
- **`R2dbcEntityTemplate`** — динамическая фильтрация и обновления с маппингом в сущность.
- **`DatabaseClient`** — сырой SQL, агрегаты и проекции, не ложащиеся на модель сущностей (см. Q11).

## Q5. Как работают реактивные транзакции?

`@Transactional` работает и в реактивном стеке — но устроен иначе. В классическом Spring транзакция привязана к потоку через `ThreadLocal`. В реактивном коде один поток обслуживает много запросов и операторы свободно перескакивают между потоками, поэтому `ThreadLocal` бесполезен. Вместо него контекст транзакции (соединение, статус) хранится в **`Reactor Context`** — он путешествует вместе с цепочкой `Mono`/`Flux`, а не с потоком.

Под капотом за это отвечают `ReactiveTransactionManager` (для R2DBC — `R2dbcTransactionManager`) и `TransactionalOperator`. Есть два способа управления:
- **Декларативный** — аннотация `@Transactional` на методе, возвращающем `Mono`/`Flux`. Откат происходит автоматически при ошибочном сигнале (`onError`).
- **Программный** — оборачивание цепочки в `TransactionalOperator` через `.as(operator::transactional)`, когда нужен точный контроль над границами транзакции.

**Важно:** граница транзакции — это граница реактивной цепочки. Откат срабатывает по сигналу ошибки, а не по выброшенному исключению из императивного кода. Поэтому ошибку нельзя «проглатывать» внутри цепочки до того, как сработает транзакционный оператор.

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

R2DBC **не поддерживает lazy loading** — нет JPA-прокси, который дозагрузил бы связь при обращении к полю. Каждую связь загружают явно, и здесь легко напороться на N+1: один запрос за списком заказов плюс по запросу за позициями каждого заказа.

Три способа избежать N+1:
- **JOIN в одном запросе** — самый прямой путь: тянем заказы и позиции одним SQL и сами склеиваем строки в объекты.
- **Batching через `IN`** — грузим заказы, собираем их id и одним запросом `findByOrderIdIn(ids)` подтягиваем все позиции, затем группируем в памяти. Получается ровно 2 запроса вместо N+1.
- **`collectMultimap`** — удобный способ сгруппировать позиции по `orderId` на стороне приложения.

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

Пул создаётся через `r2dbc-pool`: «голую» `ConnectionFactory` драйвера оборачивают в `ConnectionPool`, который переиспользует физические соединения вместо открытия нового на каждый запрос. Открытие TCP-соединения и аутентификация — дорогие операции, и без пула они съели бы выигрыш от неблокирующего I/O.

Размер пула в R2DBC обычно **меньше**, чем привычные для JDBC сотни соединений: один неблокирующий поток обслуживает много логических операций, и слишком большой пул лишь нагружает БД. Отталкивайтесь от числа ядер БД и реальной параллельности запросов.

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

Два уровня тестов под разные задачи:
- **`@DataR2dbcTest`** — срезовый тест: поднимает только R2DBC-слой (репозитории, `R2dbcEntityTemplate`), без `@Service` и `@Controller`. Быстрый, изолированно проверяет запросы и маппинг.
- **`@SpringBootTest` + Testcontainers** — полный контекст на настоящей БД в контейнере. Ловит то, что не видно на H2: специфику диалекта Postgres, реальные констрейнты, поведение под нагрузкой.

Ключевой инструмент проверки в обоих случаях — **`StepVerifier`** из reactor-test: он подписывается на `Mono`/`Flux`, по шагам сверяет эмитированные элементы (`assertNext`) и завершение (`verifyComplete`). Обычный `assertThat` на реактивном типе бесполезен — без подписки цепочка не выполнится.

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

Выбор определяется прежде всего стеком приложения и характером нагрузки, а не «модностью» реактивности. R2DBC выигрывает там, где узкое место — большое число одновременных I/O-операций и нужен неблокирующий доступ. JPA выигрывает в продуктивности разработки и на сложных доменных моделях со связями.

| Критерий | R2DBC | JPA/Hibernate |
|----------|-------|---------------|
| Стек приложения | WebFlux (реактивный) | Spring MVC (сервлетный) |
| Производительность под нагрузкой | Лучше при высоком I/O concurrency | Лучше для CRUD с небольшим числом потоков |
| Сложные маппинги (relations, lazy) | Ручное, нет proxy | Автоматическое через JPA |
| Простота кода | Сложнее (joins вручную) | Проще (JPQL, Criteria API) |
| Кэш первого/второго уровня | Нет | Есть (Hibernate) |
| Миграции схемы | Flyway (синхронный) | Flyway / Liquibase |

**Эмпирическое правило:** R2DBC — только если приложение полностью реактивное (WebFlux) и упирается в I/O-concurrency. JPA — если это Spring MVC, нужны сложные ORM-маппинги или важна скорость разработки. Главная ловушка — взять R2DBC «ради реактивности» и затем переписывать все связи вручную там, где JPA сделал бы это автоматически.

## Q10. Как обрабатывать ошибки в R2DBC?

Ошибки в реактивной цепочке обрабатываются не `try/catch`, а операторами Reactor — они работают с сигналом ошибки внутри потока:
- **`switchIfEmpty`** — превращает «пусто» в осмысленную ошибку (`findById` вернул пустой `Mono` → `OrderNotFoundException`). Без этого вызывающий код молча получит пустой результат.
- **`retryWhen`** — повтор при транзиентных сбоях (`R2dbcTransientResourceException`: разрыв соединения, дедлок) с экспоненциальной задержкой. Повторять имеет смысл только заведомо временные ошибки, а не нарушение констрейнтов.
- **`onErrorMap`** — перевод низкоуровневого исключения драйвера в доменное (`R2dbcDataIntegrityViolationException` → `DuplicateOrderException`), чтобы детали БД не протекали в верхние слои.
- **`@RestControllerAdvice`** — глобальный маппинг оставшихся исключений в HTTP-ответы (нарушение констрейнта → 409 Conflict).

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

`DatabaseClient` — самый низкоуровневый из трёх API: вы пишете SQL целиком, привязываете параметры через `.bind(...)` и сами маппите каждую строку в объект через `(row, meta) -> ...`. Это уровень ниже `R2dbcEntityTemplate`, где маппинг в сущность и построение запроса делаются за вас.

Берут его, когда результат **не ложится на модель сущности**: агрегаты (`COUNT`, `SUM`, `GROUP BY`), произвольные проекции, оконные функции, БД-специфичный SQL. В примере ниже запрос возвращает не `Order`, а сводку `OrderSummary` по покупателям — отдельный класс под форму ответа.

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

Конвертеры управляют тем, как тип Java превращается в значение колонки и обратно, когда стандартного маппинга недостаточно. Типичный повод — enum: по умолчанию R2DBC может сохранить его по порядковому индексу (ordinal), а это хрупко — добавление нового значения в середину enum «сдвинет» смысл уже записанных строк. Конвертер фиксирует хранение по имени (`OrderStatus.name()`), и порядок объявления перестаёт влиять на данные.

Конвертеры всегда идут парой и регистрируются вместе:
- **`@WritingConverter`** — Java → колонка (при сохранении).
- **`@ReadingConverter`** — колонка → Java (при чтении).
- Оба регистрируются в `getCustomConverters()` конфигурации `AbstractR2dbcConfiguration`.

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

Два подхода с разной эффективностью:
- **`repository.saveAll(...)`** — просто и читаемо, но под капотом отправляет **отдельный INSERT на каждую запись**. Для десятков строк нормально, для тысяч — много round-trip'ов к БД.
- **Нативный batch через `DatabaseClient`** — собирает несколько строк в один `Statement` через `.add()` и шлёт их одной командой. Это настоящий батчинг на уровне драйвера, дающий выигрыш на больших объёмах.

В батч-варианте записи бьют на блоки через `.buffer(500)`, чтобы не собирать в памяти один гигантский `Statement` и не упереться в лимиты протокола.

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

Главная проблема: Flyway и Liquibase — **синхронные, JDBC-based** инструменты, у них нет реактивного API. Поэтому в чисто реактивном приложении на R2DBC они «из коробки» не работают.

Стандартное решение — прагматичное: миграции выполняются один раз при старте через **обычный блокирующий JDBC `DataSource`**, а реактивная работа в рантайме идёт уже через R2DBC. Блокировка на старте безвредна — приложение ещё не обслуживает запросы. На практике держат два URL к одной БД: `jdbc:postgresql://...` для Flyway и `r2dbc:postgresql://...` для рантайма.

Порядок настройки:

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

R2DBC и WebFlux — естественная пара: оба говорят на языке `Mono`/`Flux`, поэтому поток данных проходит насквозь без блокировки. Контроллер возвращает `Flux<OrderDto>` прямо из сервиса — WebFlux сам подпишется на него и будет стримить элементы клиенту по мере готовности, не собирая весь результат в память.

Это раскрывает фишку, недоступную в блокирующем стеке — **streaming**. Например, `text/event-stream` (Server-Sent Events): соединение держится открытым, а новые заказы летят клиенту по мере появления в БД, без поллинга.

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

**Главный подводный камень:** нельзя вставлять блокирующий код (JPA, синхронный JDBC, `block()`, `Thread.sleep`) в цепочку, выполняющуюся на потоке event loop WebFlux. Таких потоков всего несколько на ядро — заблокировав один, вы подвешиваете обработку множества чужих запросов и убиваете backpressure. Если блокирующий вызов неизбежен, его выносят на отдельный пул через `subscribeOn(Schedulers.boundedElastic())`.

## See also

- [Spring WebFlux](spring-webflux-interview.md) — реактивный HTTP стек, идеально сочетается с R2DBC
- [Spring Data JPA](spring-data-jpa-interview.md) — блокирующая альтернатива для синхронных приложений
- [Spring Data JDBC](spring-data-jdbc-interview.md) — lightweight JDBC без реактивности
- [Spring Boot](spring-boot-interview.md) — auto-configuration для R2DBC
- [Project Reactor](../../reactive/project-reactor-interview.md) — Mono/Flux API используемый в R2DBC
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — спецификация backpressure
- [Virtual Threads](../../programming-languages/java/java-virtual-threads-interview.md) — альтернатива реактивного подхода
- [Database Transactions](../../databases/database-transactions-interview.md) — теория транзакций, изоляция
- [PostgreSQL](../../databases/postgresql-interview.md) — r2dbc-postgresql драйвер
- [Spring @Transactional](spring-transaction-interview.md) — реактивные транзакции через ReactiveTransactionManager
