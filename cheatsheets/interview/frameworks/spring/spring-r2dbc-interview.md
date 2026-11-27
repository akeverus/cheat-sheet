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

Примеры рассчитаны на Spring Boot 3.5, Spring Framework 6.2, Spring Data Relational 3.5 и R2DBC SPI 1.0.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Data R2DBC](https://docs.spring.io/spring-data/relational/reference/3.5/r2dbc.html): официальная документация
- [R2DBC Spec](https://r2dbc.io/spec/1.0.0.RELEASE/spec/html/): спецификация Reactive Relational Database Connectivity
- [Spring Framework 6.2: доступ к данным через R2DBC](https://docs.spring.io/spring-framework/reference/6.2/data-access/r2dbc.html)
- [Spring Boot 3.5: инициализация БД](https://docs.spring.io/spring-boot/3.5/how-to/data-initialization.html)
- [Baeldung: Spring R2DBC](https://www.baeldung.com/spring-data-r2dbc) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое R2DBC и чем он отличается от JDBC?

```text
Сервис A обращается к PostgreSQL через JDBC, сервис B выполняет такой же SELECT через R2DBC.
Ответ сервера задерживается. В обоих случаях вызывающий код уже инициировал чтение.
Сравните, что происходит с вызывающим потоком и как приложение получает строки.
```

**R2DBC (Reactive Relational Database Connectivity)** задаёт отдельный SPI реактивного доступа к реляционным БД на основе Reactive Streams. Драйвер предоставляет `Publisher`, а Spring обычно адаптирует его к `Mono` и `Flux`. Например, `Statement.execute()` публикует объекты `Result`, из которых приложение получает строки или счётчики изменений.

| Критерий | JDBC | R2DBC |
|----------|------|-------|
| Ожидание I/O | Синхронный вызов занимает вызывающий поток до возврата | Неблокирующий драйвер доставляет результат сигналами |
| Чтение строк | `ResultSet` | `Result.map(...)` возвращает `Publisher` |
| Менеджер локальной транзакции Spring | `DataSourceTransactionManager` | `R2dbcTransactionManager` |

JDBC-соединение не закрепляет за собой поток на всё время жизни. В R2DBC отсутствие блокировки вызывающего потока не увеличивает пропускную способность самой БД: ёмкость пула, планы запросов и блокировки остаются важны.

Spring Data R2DBC маппит строки в объекты. JOIN и сборку результата со связями приложение может выполнить явно (Q6). JPA предоставляет ORM, включая ленивую загрузку и persistence context. Выбор зависит от стека, нагрузки и требований к маппингу (Q9).

## Q2. Как настроить Spring Data R2DBC?

```text
Spring Boot 3.5, Maven, PostgreSQL на localhost:5432, база mydb.
У приложения пока нет зависимостей и настроек доступа к ней.
Требуется получить ConnectionFactory и подключить реактивные репозитории. Что добавить?
```

Минимально нужны две зависимости: стартер `spring-boot-starter-data-r2dbc` (репозитории, авто-конфигурация) и **неблокирующий драйвер** под конкретную БД (`r2dbc-postgresql`, `r2dbc-mysql`, `r2dbc-h2`). Обычный JDBC-драйвер сюда не подходит — у R2DBC своя реализация протокола.

Дальше есть два пути конфигурации:
- **Декларативный**: через `spring.r2dbc.*` в `application.yml`. При наличии подходящего драйвера Boot создаёт `ConnectionFactory`. Пул используется, если `r2dbc-pool` доступен и pooling включён. Явный бин фабрики позволяет взять её настройку под свой контроль.
- **Программный** — объявить бин `ConnectionFactory` вручную, когда нужны нестандартные опции драйвера или тонкая настройка пула.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
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
    return new ConnectionPool(
        ConnectionPoolConfiguration.builder(factory)
            .maxSize(20)
            .initialSize(5)
            .maxIdleTime(Duration.ofMinutes(30))
            .build()
    );
}
```

## Q3. Как создать реактивный репозиторий?

```text
Таблица orders отображается на Order с идентификатором Long.
Нужны CRUD, поиск заказов по customerId и запрос по total > minTotal и совпадению status.
Для последнего запроса команда хочет задать SQL вручную. Объявите контракт репозитория.
```

Достаточно объявить интерфейс-наследник `ReactiveCrudRepository<T, ID>`: Spring Data создаст прокси репозитория. Для выборки одного объекта репозиторий обычно возвращает `Mono<T>`, для нескольких объектов `Flux<T>`. Операция с БД запускается при подписке.

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

    Flux<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);

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
        return repository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING);
    }
}
```

## Q4. Что такое R2dbcEntityTemplate и когда его использовать?

```text
Нужно выбирать Order по произвольной комбинации minTotal, status и customerId.
Каждый фильтр опционален, результатом должны быть объекты Order.
В том же сервисе нужно изменить status по id. Какой API подходит для обеих операций?
```

`R2dbcEntityTemplate`: программный fluent-API для запросов, которые неудобно или невозможно выразить методом репозитория. В отличие от `@Query` с сырым SQL, он строит запрос через `query(where(...))`, `update(...)`, `select(Class)` и маппит выбранные строки в сущность. Имена свойств в критериях передаются строками, поэтому опечатки компилятор не поймает.

Главный плюс перед derived-методами — **динамика**: условия можно собирать в рантайме (по наличию фильтров, по правам пользователя), не плодя десятки именованных методов на все комбинации.

```java
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final R2dbcEntityTemplate template;

    public Flux<Order> findOrders(BigDecimal minTotal, OrderStatus status, String customerId) {
        Criteria criteria = Criteria.empty();
        if (minTotal != null) {
            criteria = criteria.and("total").greaterThan(minTotal);
        }
        if (status != null) {
            criteria = criteria.and("status").is(status);
        }
        if (customerId != null) {
            criteria = criteria.and("customerId").is(customerId);
        }
        return template.select(Order.class).matching(query(criteria)).all();
    }

    public Mono<Order> updateStatus(Long id, OrderStatus newStatus) {
        return template.update(Order.class)
            .matching(query(where("id").is(id)))
            .apply(update("status", newStatus))
            .flatMap(updated -> updated == 0
                ? Mono.empty()
                : template.selectOne(query(where("id").is(id)), Order.class));
    }

    // DELETE с условием
    public Mono<Long> deleteOldCancelledOrders(LocalDateTime before) {
        return template.delete(Order.class)
            .matching(query(where("status").is(OrderStatus.CANCELLED)
                .and("createdAt").lessThan(before)))
            .all();
    }
}
```

Обновление и последующее чтение в примере являются двумя SQL-операциями. Для согласованного результата при конкуренции учитывайте транзакцию и выбранный уровень изоляции.

**Какой инструмент выбирать:**
- **Репозиторий** — стандартный CRUD и простые derived-запросы.
- **`R2dbcEntityTemplate`** — динамическая фильтрация и обновления с маппингом в сущность.
- **`DatabaseClient`** — сырой SQL, агрегаты и проекции, не ложащиеся на модель сущностей (см. Q11).

## Q5. Как работают реактивные транзакции?

```text
Spring Framework 6.2. Внешней транзакции нет, вызов processOrder идёт через Spring-прокси.
Метод возвращает Mono<Order>, сначала сохраняет заказ, затем вызывает reserve в flatMap.
Обе операции используют одну ConnectionFactory, метод помечен @Transactional,
выбран R2dbcTransactionManager. reserve завершился RuntimeException.
Что произойдёт с заказом? Что изменится при отдельном subscribe() для reserve?
```

При работе с `R2dbcTransactionManager` соединение соответствующей `ConnectionFactory` связано с контекстом подписчика Reactor. Смена потока сама по себе не разрывает транзакцию. Репозиторий и `DatabaseClient` могут участвовать в одной транзакции, если используют ту же фабрику и работают в одной составной цепочке. Отдельный ручной `subscribe()` создаёт отдельную подписку, а прямой `ConnectionFactory.create()` не использует привязку Spring.

- **Декларативный вариант.** В proxy-режиме вызов `@Transactional`-метода должен пройти через Spring-прокси. Используется реактивный менеджер транзакций. `onError` приводит к откату, если ошибка подходит под правила rollback. По умолчанию это `RuntimeException` и `Error`. Для checked-исключений правило можно задать явно.
- **Программный вариант.** `TransactionalOperator` оборачивает составную цепочку. Оператор откатывает свою транзакцию при ошибке или отмене подписки. Если внутри границы заменить ошибку успешным завершением, транзакционное управление увидит успех. Состояние самой транзакции при этом может не позволить коммит, например после ошибки SQL в PostgreSQL.

Здесь `reserve` возвращает `Mono<Void>`, а `OrderProcessingException` наследуется от `RuntimeException`. При ошибке резервирования исключение выходит из составной цепочки, поэтому вставка заказа откатывается. Если запустить резервирование через отдельный `subscribe()`, его ошибка не попадёт в цепочку сохранения заказа и сама по себе не отменит её коммит.

```java
@Service
@RequiredArgsConstructor
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Mono<Order> processOrder(PlaceOrderCommand cmd) {
        return orderRepository.save(new Order(null, cmd.customerId(), cmd.total(),
                OrderStatus.PENDING, LocalDateTime.now()))
            .flatMap(order -> paymentRepository.reserve(order.id(), order.total())
                .thenReturn(order))
            .onErrorMap(OrderProcessingException::new);
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
    return orderRepository.save(new Order(null, cmd.customerId(), cmd.total(),
                OrderStatus.PENDING, LocalDateTime.now()))
        .flatMap(order -> paymentRepository.reserve(order.id(), order.total())
            .thenReturn(order))
        .as(transactionalOperator::transactional);
}
```

## Q6. Как решается проблема N+1 в Spring Data R2DBC?

```text
Загрузка заказов вернула 12 записей. Для каждой записи код вызывает findByOrderId(order.id()).
Каждый вызов репозитория выполняет один SQL-запрос. Кэша нет.
Нужно сохранить также заказы без позиций. Сколько запросов происходит и как сократить их число?
```

Spring Data R2DBC не создаёт JPA-прокси для ленивой загрузки. При одном запросе списка заказов и отдельном запросе позиций для каждого из `N` заказов выходит `1 + N` SQL-запросов, то есть 13 для 12 заказов. `flatMap` может менять степень конкурентности, а число запросов остаётся прежним.

- **JOIN.** Один SQL-запрос возвращает строки заказа и позиций. При `LEFT JOIN` сохраняются и заказы без позиций. Соберите результат по ID заказа, пропуская отсутствующую позицию с `NULL` в `item_id`.
- **Пакетная выборка через `IN`.** Один запрос заказов и один запрос позиций, если список заказов непуст и умещается в один набор параметров. Пустой список требует только первого запроса. Если ID приходится разбивать на порции, запросов позиций будет несколько. `collectMultimap` группирует загруженные позиции в памяти и сам по себе число запросов не меняет.

```java
// Проблема: N+1 при загрузке заказов с позициями
public Flux<OrderWithItems> loadOrdersNaively(String customerId) {
    return orderRepository.findByCustomerId(customerId)
        .flatMap(order -> itemRepository.findByOrderId(order.id())
            .collectList()
            .map(items -> new OrderWithItems(order, items)));
}

// Пакетная выборка через репозиторий позиций
public Flux<OrderWithItems> loadOrdersBatched(String customerId) {
    return orderRepository.findByCustomerId(customerId)
        .collectList()
        .flatMapMany(orders -> {
            if (orders.isEmpty()) {
                return Flux.empty();
            }
            List<Long> ids = orders.stream().map(Order::id).toList();
            return itemRepository.findByOrderIdIn(ids)
                .collectMultimap(OrderItem::orderId)
                .flatMapMany(itemsByOrder ->
                    Flux.fromIterable(orders)
                        .map(o -> new OrderWithItems(o,
                            List.copyOf(itemsByOrder.getOrDefault(o.id(), List.of()))))
                );
        });
}
```

Запрос с JOIN можно выполнить через `DatabaseClient`. Ниже SQL для привязки `customerId` через `bind(...)`, после которого строки группируются по `order_id`.

```sql
SELECT o.id AS order_id, o.customer_id, o.total, o.status, o.created_at,
       i.id AS item_id, i.product_id, i.quantity, i.price
FROM orders o
LEFT JOIN order_items i ON i.order_id = o.id
WHERE o.customer_id = :customerId
```

## Q7. Как настроить Connection Pool?

```text
Шесть экземпляров приложения обращаются к PostgreSQL. При всплеске запросов
каждый экземпляр должен держать не более 20 соединений и ждать свободное до 5 секунд.
Соединение можно закрыть после 30 минут простоя. Какие настройки задают эти ограничения?
```

Пул создаётся через `r2dbc-pool`: «голую» `ConnectionFactory` драйвера оборачивают в `ConnectionPool`, который переиспользует физические соединения вместо открытия нового на каждый запрос. Переиспользование сокращает накладные расходы на открытие соединения и аутентификацию.

Пул ограничивает число физических соединений, а число потоков определяется отдельно. Размер выбирают по лимиту БД, числу реплик приложения, длительности операций и измеренному ожиданию соединения. Больший пул может усилить конкуренцию в БД.

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
- `initialSize`: начальный целевой размер пула. Если нужна явная предварительная инициализация, можно подписаться на `ConnectionPool.warmup()`
- `maxSize` — максимальный размер пула
- `maxIdleTime` — время простоя до закрытия соединения
- `maxAcquireTime` — максимальное время ожидания соединения из пула

## Q8. Как тестировать Spring Data R2DBC?

```text
Spring Boot 3.5. Требуется проверить запрос и маппинг OrderRepository на PostgreSQL,
без загрузки контроллеров и сервисов. Контейнер и схема доступны.
Как запустить такой тест и проверить, что сохранённый заказ читается и Mono успешно завершается?
```

Срез или полный контекст определяют охват Spring-теста. Тестовую БД выбирают отдельно:
- `@DataR2dbcTest`: срезовый тест: поднимает только R2DBC-слой (репозитории, `R2dbcEntityTemplate`), без `@Service` и `@Controller`. Позволяет изолированно проверить запросы и маппинг. В срез можно подключить PostgreSQL через Testcontainers. Аннотация сама не выбирает H2. Схему готовят миграциями или тестовым SQL. Для варианта с PostgreSQL можно применить настройки контейнера из второго примера в таком же срезе.
- **`@SpringBootTest` + Testcontainers**: полный контекст на настоящей БД в контейнере. Ловит то, что не видно на H2: специфику диалекта Postgres, ограничения и особенности маппинга конкретного драйвера.

Ключевой инструмент проверки в обоих случаях: `StepVerifier` из reactor-test: он подписывается на `Mono`/`Flux`, по шагам сверяет эмитированные элементы (`assertNext`) и завершение (`verifyComplete`). Обычный `assertThat` на реактивном типе бесполезен: без подписки цепочка не выполнится.

```java
// В test runtime есть r2dbc-h2, а src/test/resources/schema.sql создаёт orders.
@DataR2dbcTest
@TestPropertySource(properties = {
    "spring.r2dbc.url=r2dbc:h2:mem:///orders_test",
    "spring.sql.init.mode=always"
})
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
```

```java
// Альтернативная конфигурация полного контекста с PostgreSQL.
// Предполагается совместимый schema.sql и отсутствие Flyway/Liquibase в этом тесте.
@SpringBootTest
@Testcontainers
class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url",
            () -> "r2dbc:postgresql://" + postgres.getHost() + ":"
                + postgres.getMappedPort(5432) + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "always");
    }
}
```

## Q9. Когда использовать R2DBC, а когда JPA?

```text
Сервис A использует WebFlux и выполняет простые запросы с высокой конкуренцией I/O.
Сервис B обслуживает синхронный CRUD со сложными связями и изменением графа сущностей.
Какими свойствами доступа к данным руководствоваться при выборе для каждого сервиса?
```

Выбор зависит от I/O-модели приложения, требований к маппингу и измеренной нагрузки. R2DBC подходит для неблокирующей цепочки доступа к данным. JPA даёт persistence context, dirty checking, связи, ленивую загрузку и средства ORM-запросов.

| Критерий | Spring Data R2DBC | JPA / Hibernate ORM |
|----------|-------------------|--------------------|
| Типичный стек | WebFlux и другие потребители Reactive Streams | Spring MVC и другие императивные сервисы |
| Маппинг | Маппинг строк, явная загрузка связанных данных | ORM-связи, JPQL и Criteria API |
| Кэш | У приложения или инфраструктуры | Persistence context, опциональный кэш второго уровня |
| Миграции схемы | Возможны Flyway или Liquibase через отдельную JDBC-конфигурацию | Возможны Flyway или Liquibase через JDBC |

Измерьте время запроса, насыщение пула и БД. Высокая конкурентность сама по себе не обещает прироста. R2DBC можно использовать вне WebFlux. При доступе к JPA из реактивного сервиса синхронный вызов требуется вынести с event loop.

## Q10. Как обрабатывать ошибки в R2DBC?

```text
Поиск заказа может завершиться без значения, временной ошибкой доступа или выдать заказ.
Разрешено до трёх повторов только при временной ошибке. Отсутствие заказа должно стать
OrderNotFoundException. Как различить сигналы и в каком месте применить повтор?
```

`DatabaseClient` переводит исключения SPI `R2dbcException` в иерархию Spring `DataAccessException`. Конкретный подтип зависит от ошибки и данных драйвера. Прямое использование SPI обходит это преобразование.

- `switchIfEmpty` обрабатывает успешное завершение без значения. Здесь оно превращается в доменную ошибку отсутствия заказа.
- `retryWhen` повторно подписывается на источник. Ниже повторяется чтение при `TransientDataAccessException`, максимум три раза после первой попытки. Для записи нужно оценивать идемпотентность и возможность того, что изменение уже принято БД. Повтор всей транзакционной операции можно разместить снаружи её границы, чтобы новая подписка начала новую транзакцию.
- `onErrorMap` переводит известное исключение в доменное. Дубликат определяют по конкретному ограничению или коду ошибки.
- `@RestControllerAdvice` может сопоставить доменный конфликт с HTTP 409. В ответе достаточно стабильного сообщения без деталей драйвера.

```java
public Mono<Order> findOrThrow(Long id) {
    return orderRepository.findById(id)
        .switchIfEmpty(Mono.error(new OrderNotFoundException(id)));
}

public Mono<Order> findWithRetry(Long id) {
    return Mono.defer(() -> orderRepository.findById(id))
        .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
            .filter(TransientDataAccessException.class::isInstance)
            .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
        .switchIfEmpty(Mono.error(new OrderNotFoundException(id)));
}

// Метод в @RestControllerAdvice
@ExceptionHandler(DuplicateOrderException.class)
public ResponseEntity<ErrorDto> handleDuplicateOrder(DuplicateOrderException e) {
    return ResponseEntity.status(409).body(new ErrorDto("Заказ уже существует"));
}
```

## Q11. Как работает DatabaseClient для raw SQL?

```text
PostgreSQL. В таблице orders есть customer_id, total и created_at (timestamp без зоны).
Отчёт содержит customerId, число заказов и сумму по каждому покупателю.
Обе граничные даты входят в диапазон. Как выполнить параметризованный SQL
и отобразить строки в OrderSummary без отображения отчёта на таблицу?
```

`DatabaseClient` — самый низкоуровневый из трёх API: вы пишете SQL целиком, привязываете параметры через `.bind(...)` и сами маппите каждую строку в объект через `(row, meta) -> ...`. Это уровень ниже `R2dbcEntityTemplate`, где маппинг в сущность и построение запроса делаются за вас.

В примере PostgreSQL поле `created_at` имеет тип `timestamp without time zone`. Обе даты включены. SQL выбирает полуинтервал от начала `from` до начала дня после `to`.

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
                WHERE created_at >= :from AND created_at < :toExclusive
                GROUP BY customer_id
                ORDER BY total_amount DESC
                """)
            .bind("from", from.atStartOfDay())
            .bind("toExclusive", to.plusDays(1).atStartOfDay())
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

```text
У Order есть поле status типа OrderStatus со значениями PENDING, CONFIRMED и CANCELLED.
Колонка VARCHAR хранит коды P, C и X соответственно. Этот формат менять нельзя.
Как настроить чтение и запись через Spring Data R2DBC 3.5?
```

Конвертеры управляют тем, как тип Java превращается в значение колонки и обратно, когда стандартного маппинга недостаточно. С enum важно не перепутать дефолт: Spring Data R2DBC ради переносимости пишет enum **строкой** (`name()`), а не по порядковому индексу. Собственное преобразование при записи нужно, когда колонка объявлена нативным enum-типом БД (тогда пишущий конвертер наследуют от `EnumWriteSupport`, при этом нативный тип PostgreSQL настраивают также в драйвере) или когда в колонке лежит своё представление вроде короткого кода.

Конвертеры регистрируются по необходимости. Для двустороннего маппинга короткого кода нужны оба:
- **`@WritingConverter`** — Java → колонка (при сохранении).
- **`@ReadingConverter`** — колонка → Java (при чтении).
- При автоконфигурации Boot можно объявить бин `R2dbcCustomConversions`. При ручной настройке через `AbstractR2dbcConfiguration` можно переопределить `getCustomConverters()`.

```java
// Своё представление в колонке: короткий код вместо name()
@WritingConverter
public class OrderStatusWriteConverter implements Converter<OrderStatus, String> {
    @Override
    public String convert(OrderStatus source) {
        return source.getCode();   // "P", "C", "X"
    }
}

@ReadingConverter
public class OrderStatusReadConverter implements Converter<String, OrderStatus> {
    @Override
    public OrderStatus convert(String source) {
        return OrderStatus.byCode(source);
    }
}

@Configuration
public class R2dbcConfig {
    @Bean
    R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory factory) {
        return R2dbcCustomConversions.of(
            DialectResolver.getDialect(factory),
            new OrderStatusWriteConverter(), new OrderStatusReadConverter());
    }
}
```

## Q13. Как выполнить batch insert?

```text
PostgreSQL. Нужно вставить 1200 новых заказов порциями до 500 на одном соединении.
У каждой записи есть customer_id, total и status. Все результаты должны быть обработаны.
Как переиспользовать один SQL в пределах порции и выполнить порции последовательно?
```

`repository.saveAll(...)` вызывает сохранение отдельных сущностей. Для новых это отдельные INSERT, для существующих возможны UPDATE. Операция не превращает список в один SQL INSERT со множеством строк.

В PostgreSQL можно привязать несколько наборов параметров к одному `Statement` через `add()`. После последнего набора `add()` не нужен. Число сетевых обменов зависит от драйвера. Для массовой вставки также можно сравнить многорядный INSERT и возможности конкретной БД.

Пример выполняет порции последовательно на одном соединении и потребляет счётчики из каждого `Result` внутри `inConnectionMany`. Здесь `status` хранит имя enum. Для атомарности всех порций оберните цепочку в транзакцию на той же `ConnectionFactory`.

```java
// Сохранение через репозиторий
public Flux<Order> saveAll(List<Order> orders) {
    return orderRepository.saveAll(orders);
}

// Батч через R2DBC SPI на соединении DatabaseClient
public Mono<Void> batchInsert(List<Order> orders) {
    return client.inConnectionMany(conn ->
        Flux.fromIterable(orders)
            .buffer(500)  // батч по 500 записей
            .concatMap(batch -> {
                Statement stmt = conn.createStatement(
                    "INSERT INTO orders(customer_id, total, status) VALUES($1, $2, $3)");
                for (Iterator<Order> it = batch.iterator(); it.hasNext(); ) {
                    Order o = it.next();
                    stmt.bind(0, o.customerId())
                        .bind(1, o.total())
                        .bind(2, o.status().name());
                    if (it.hasNext()) {
                        stmt.add();   // add() разделяет наборы, после последнего его не зовут
                    }
                }
                return Flux.from(stmt.execute())
                    .concatMap(result -> Flux.from(result.getRowsUpdated()));
            })
    ).then();
}
```

## Q14. Как выполнять миграции схемы в приложении на R2DBC?

```text
Spring Boot 3.5. Приложение получает рабочие соединения к PostgreSQL через R2DBC.
Перед началом обслуживания запросов оно должно применить миграции Flyway к той же БД.
Для миграций выделена отдельная учётная запись. Есть ли необходимость менять рабочий драйвер?
```

Flyway и Liquibase используют JDBC. В Spring Boot 3.5 можно выполнить миграции при старте через отдельное JDBC-подключение, а R2DBC использовать для рабочих запросов. Для PostgreSQL добавьте `flyway-core`, `flyway-database-postgresql` и JDBC-драйвер `org.postgresql:postgresql`. Оба URL указывают на одну БД. Скрипты лежат в `classpath:db/migration`, а мигратор имеет права менять схему.

Boot создаёт подключение для Flyway по `spring.flyway.url` и вызывает `migrate()`. Свойство `spring.datasource.url` само по себе не создаёт JDBC-бин `DataSource` при наличии `ConnectionFactory`: стандартная JDBC-автоконфигурация отступает. Миграции можно запустить отдельным шагом деплоя до запуска приложения.

```yaml
spring:
  flyway:
    url: jdbc:postgresql://localhost:5432/mydb
    user: ${MIGRATION_DB_USER}
    password: ${MIGRATION_DB_PASSWORD}
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/mydb
    username: ${APP_DB_USER}
    password: ${APP_DB_PASSWORD}
```

## Q15. Какова интеграция Spring Data R2DBC с WebFlux?

```text
WebFlux. GET /api/orders/{customerId} отдаёт результаты выборки заказов.
GET /api/orders/stream должен сообщать о заказах, которые появятся после подключения клиента.
Сейчас сервис располагает только методом репозитория для обычного SELECT.
Что требуется для обновлений и как передать их по HTTP?
```

WebFlux подписывается на `Publisher`, возвращённый контроллером. Обычный `Flux` из репозитория доставляет строки текущей выборки и завершается. Способ передачи в HTTP зависит от медиатипа и кодека. Например, `text/event-stream` позволяет отправлять отдельные события по мере поступления. SSE также можно обслуживать в Spring MVC с Servlet async.

Для непрерывных обновлений нужен источник событий. В примере `streamNewOrders()` читает отдельную шину событий заказов. В другом дизайне источником могут быть CDC, уведомления PostgreSQL или периодический опрос. Простая подписка на результат `SELECT` не отслеживает будущие изменения таблицы.

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

Блокирующий вызов на event loop задерживает и другие запросы этого потока. Если его нельзя заменить, оберните сам вызов в `Mono.fromCallable(() -> legacyClient.load())` и назначьте источнику `subscribeOn(Schedulers.boundedElastic())`. Вызов остаётся блокирующим и расходует ёмкость отдельного планировщика.

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
