---
title: "Вопросы на собеседовании: Spring for GraphQL"
description: "Spring for GraphQL: @QueryMapping, @MutationMapping, @SchemaMapping, DataLoader для N+1, subscriptions, тестирование, Spring Security интеграция"
tags:
  - interview
  - spring
  - spring-graphql-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring for GraphQL"
  - "Spring GraphQL interview"
  - "Spring GraphQL собеседование"
prerequisites:
  - "[[spring-graphql]]"
next: []
updated: 2026-05-31
---
# Вопросы на собеседовании: `Spring for GraphQL`

`Spring for GraphQL` — официальная интеграция GraphQL в экосистему Spring (с Spring Boot 2.7+). Построена поверх `GraphQL Java`, поддерживает HTTP и WebSocket транспорты, `DataLoader` для решения N+1 проблемы. Часто спрашивается в контексте альтернатив REST.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring for GraphQL Docs](https://docs.spring.io/spring-graphql/docs/current/reference/html/) — официальная документация
- [GraphQL Java Docs](https://www.graphql-java.com/documentation/getting-started) — GraphQL Java документация
- [Baeldung: Spring GraphQL](https://www.baeldung.com/spring-graphql) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Spring GraphQL и в чём его преимущества перед REST?

**Spring GraphQL** — официальная интеграция GraphQL в экосистему Spring (с версии Spring Boot 2.7+). Построена поверх GraphQL Java, поддерживает HTTP и WebSocket транспорты и даёт аннотации (`@QueryMapping`, `@MutationMapping`, `@SchemaMapping`), чтобы привязывать поля схемы к методам контроллеров — как `@RequestMapping` в REST.

**Главное преимущество перед REST.** Клиент сам решает, какие поля и какое дерево связей ему нужны, одним запросом. Это убирает две хронические боли REST:

- **Over-fetching** — REST-endpoint возвращает фиксированный набор полей, и мобильному клиенту прилетает лишнее. В GraphQL клиент запрашивает только нужные поля.
- **Under-fetching** — чтобы собрать экран, по REST приходится дёргать несколько endpoint-ов (заказ, потом его позиции, потом клиента). GraphQL отдаёт всё дерево за один round-trip.

| Критерий | REST | GraphQL |
|----------|------|---------|
| Over-fetching | Частое | Нет — клиент запрашивает только нужные поля |
| Under-fetching | N+1 запросов | Одним запросом всё дерево |
| Версионирование API | `/v1/`, `/v2/` | Схема эволюционирует без версий |
| Типизация | OpenAPI/Swagger | Встроена в язык схемы (SDL) |
| Subscriptions | SSE/WebSocket вручную | Встроены в спецификацию |
| Кэширование | HTTP-кэш (легко) | Сложнее (один endpoint POST) |

**Компромисс.** Гибкость не бесплатна: HTTP-кэширование почти теряется (всё идёт одним `POST /graphql`), а гибкость клиента порождает N+1 на сервере (см. Q5). GraphQL выигрывает там, где много разнородных клиентов и связанных данных; для простого CRUD REST остаётся проще.

## Q2. Как настроить Spring GraphQL?

Пример рассчитан на Spring Boot 3.5 и Spring for GraphQL 1.4. Для запуска нужны стартер, схема и HTTP-транспорт. По умолчанию Boot ищет `*.graphqls` и `*.gqls` в `classpath:graphql/**/`, обычно это `src/main/resources/graphql/`.

**Шаг 1 — зависимости.** `spring-boot-starter-graphql` плюс web- или webflux-стартер для транспорта:

```xml
<!-- Spring Boot 3.5 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- или spring-boot-starter-webflux для реактивного стека -->
</dependency>
```

**Шаг 2 — конфигурация.** Включаем GraphiQL (встроенный UI для отладки запросов) и задаём endpoint:

```yaml
spring:
  graphql:
    graphiql:
      enabled: true       # UI для тестирования (/graphiql)
    schema:
      printer:
        enabled: true
    http:
      path: /graphql
```

При включённом `spring.graphql.schema.printer.enabled` Spring Boot отдаёт текст схемы по `GET /graphql/schema`. Маршрут строится добавлением `/schema` к `spring.graphql.http.path`. [Свойства Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/appendix/application-properties/).

**Шаг 3 — схема** (SDL) в `src/main/resources/graphql/`. Описывает контракт: точки входа `Query`/`Mutation` и доменные типы:

```graphql
# src/main/resources/graphql/schema.graphqls
type Query {
    order(id: ID!): Order
    ordersByCustomer(customerId: String!): [Order!]!
}

type Mutation {
    createOrder(input: CreateOrderInput!): Order!
    cancelOrder(id: ID!): Order!
}

type Order {
    id: ID!
    customerId: String!
    total: Float!
    status: OrderStatus!
    items: [OrderItem!]!
}

type OrderItem {
    id: ID!
    productId: String!
    quantity: Int!
    price: Float!
}

input CreateOrderInput {
    customerId: String!
    items: [OrderItemInput!]!
}

input OrderItemInput {
    productId: String!
    quantity: Int!
}

enum OrderStatus {
    PENDING
    CONFIRMED
    SHIPPED
    CANCELLED
}
```

## Q3. Как реализовать Query resolver?

Резолвер — это метод в `@Controller`, привязанный к полю схемы. Есть два уровня:

- **`@QueryMapping`** — точка входа: метод обрабатывает корневое поле `Query`. Аргументы запроса достаются через `@Argument`, имя метода по умолчанию совпадает с именем поля.
- **`@SchemaMapping`** — резолвер вложенного поля: вызывается, только если клиент запросил это поле, и получает родительский объект первым параметром. Так подгружаются связи (например, `items` у `Order`) — лениво, без `JOIN`.

```java
@Controller
public class OrderController {

    private final OrderService orderService;

    // @QueryMapping соответствует полю "order" в type Query
    @QueryMapping
    public Order order(@Argument String id) {
        return orderService.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @QueryMapping
    public List<Order> ordersByCustomer(@Argument String customerId) {
        return orderService.findByCustomer(customerId);
    }
}
```

```java
// @SchemaMapping для вложенных полей (field resolver)
@Controller
public class OrderItemResolver {

    private final OrderItemService itemService;

    // Разрешает поле "items" для типа "Order"
    @SchemaMapping(typeName = "Order", field = "items")
    public List<OrderItem> items(Order order) {
        return itemService.findByOrderId(order.getId());
        // ВНИМАНИЕ: N+1 проблема! Используйте BatchMapping или DataLoader
    }
}
```

**Подводный камень.** Резолвер `items` вызывается **отдельно для каждого** заказа в списке. Запросили 50 заказов — получили 50 запросов за позициями (плюс один за самими заказами). Это и есть N+1; как лечить — в Q5.

## Q4. Как реализовать Mutation?

Mutation — это операция с побочным эффектом (создать, изменить, удалить); в GraphQL она отделена от `Query` явно, на уровне схемы. Реализуется методом с `@MutationMapping` в `@Controller` — всё симметрично query, меняется только аннотация.

**Соглашение:** сложные аргументы заворачивают в `input`-тип. На схеме это `input CreateOrderInput`, на стороне Java удобно отразить его неизменяемым `record`. Spring сам десериализует переменные запроса в этот record по именам полей.

```java
@Controller
@RequiredArgsConstructor
public class OrderMutationController {
    private final OrderService orderService;

    @MutationMapping
    public Order createOrder(@Argument CreateOrderInput input) {
        return orderService.create(input);
    }

    @MutationMapping
    public Order cancelOrder(@Argument String id) {
        return orderService.cancel(id);
    }
}
```

```java
public record CreateOrderInput(
    String customerId,
    List<OrderItemInput> items
) {}

public record OrderItemInput(
    String productId,
    int quantity
) {}
```

## Q5. Что такое проблема N+1 в GraphQL и как её решить?

```java
// Клиент запрашивает заказы вместе с items.
// Корневой резолвер одним SQL-запросом возвращает 50 заказов с разными ID.
// Каждый вызов findByOrderId делает один SQL-запрос. Кэш выключен.
@Controller
@RequiredArgsConstructor
class OrderItemResolver {
    private final OrderItemService itemService;

    @SchemaMapping(typeName = "Order", field = "items")
    List<OrderItem> items(Order order) {
        return itemService.findByOrderId(order.getId());
    }
}
```

Для 50 заказов получаем 51 запрос: один за заказами и ещё по одному за позициями каждого заказа. Так возникает N+1. В GraphQL риск особенно заметен при выборке вложенных полей, состав которых определяет клиент.

Идея решения одна — **batching**: вместо N точечных запросов собрать все ключи и сделать один запрос «дай позиции для этих 50 заказов». Spring GraphQL даёт два способа.

**Решение 1 — `@BatchMapping`** (Spring-идиоматичный, высокоуровневый). Spring сам собирает все родительские объекты одного уровня и передаёт их списком; вы возвращаете `Map<родитель, значение>`:

```java
@Controller
@RequiredArgsConstructor
public class OrderBatchResolver {
    private final OrderItemService itemService;

    // Spring GraphQL сам группирует вызовы и передаёт List<Order>
    @BatchMapping(typeName = "Order", field = "items")
    public Map<Order, List<OrderItem>> items(List<Order> orders) {
        List<String> ids = orders.stream().map(Order::getId).toList();
        Map<String, List<OrderItem>> byOrderId = itemService.findByOrderIds(ids)
            .stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
        return orders.stream().collect(
            Collectors.toMap(o -> o, o -> byOrderId.getOrDefault(o.getId(), List.of()))
        );
    }
}
```

**Решение 2 — DataLoader** (низкоуровневый, из GraphQL Java). Регистрируете batch-loader по имени; движок откладывает вызовы, собирает все запрошенные ключи и зовёт loader один раз за «тик» выполнения:

```java
@Configuration
public class OrderDataLoaderConfig {

    // BatchLoaderRegistry создаёт автоконфигурация — его инжектят и в него регистрируются,
    // а не объявляют своим @Bean
    public OrderDataLoaderConfig(BatchLoaderRegistry registry, OrderItemService itemService) {
        registry.<String, List<OrderItem>>forName("orderItemsLoader")
            .registerMappedBatchLoader((orderIds, env) -> Mono.fromCallable(() ->
                itemService.findByOrderIds(List.copyOf(orderIds)).stream()
                    .collect(Collectors.groupingBy(OrderItem::getOrderId))));
    }
}
```

`@BatchMapping` сам регистрирует загрузчик и резолвер поля поверх DataLoader. Явная регистрация полезна, если один loader нужен нескольким полям или требуется отдельно настроить его параметры. Кэш DataLoader в обоих вариантах относится к конкретному запросу. [Маппинги контроллеров Spring for GraphQL](https://docs.spring.io/spring-graphql/reference/controllers.html).

## Q6. Как реализовать GraphQL Subscriptions?

Subscription — третий тип операции (после `Query` и `Mutation`): сервер пушит клиенту поток событий по мере их появления, а не отвечает разово. Идёт по WebSocket (поверх протокола `graphql-ws`), потому что нужно долгоживущее двунаправленное соединение.

В Spring GraphQL резолвер подписки возвращает реактивный `Flux<T>` — каждый элемент потока становится отдельным сообщением клиенту. Соединение живёт, пока поток не завершится или клиент не отпишется.

```graphql
type Subscription {
    orderStatusChanged(orderId: ID!): Order!
}
```

```java
// WebSocket transport (добавить spring-boot-starter-websocket)
@Controller
@RequiredArgsConstructor
public class OrderSubscriptionController {
    private final OrderEventPublisher eventPublisher;

    @SubscriptionMapping
    public Flux<Order> orderStatusChanged(@Argument String orderId) {
        return eventPublisher.getOrderEvents()
            .filter(order -> order.getId().equals(orderId));
    }
}
```

```yaml
spring:
  graphql:
    websocket:
      path: /graphql-ws
```

Клиент использует `graphql-ws` протокол для подписки через WebSocket.

## Q7. Как обрабатывать ошибки в Spring GraphQL?

Показанный адаптер обрабатывает исключение резолвера после валидации GraphQL-документа и начала выполнения. Ошибка поля попадает в `errors`. При штатной доставке результата выполнения HTTP-статус обычно равен 200, а тело может содержать и частичные `data`.

Резолвер исключений полей не получает ошибки запроса до выполнения. Например, при согласованном `application/graphql-response+json` ошибка разбора или валидации документа может дать HTTP 4xx. Для таких ошибок при согласованном `application/json` сохраняется прежнее поведение с HTTP 200. [HTTP-транспорт Spring for GraphQL](https://docs.spring.io/spring-graphql/reference/transports.html).

Есть два подхода — выбирают по охвату:

- **`DataFetcherExceptionResolver`** — глобальный `@Component`: ловит исключения из любого резолвера. Удобно для сквозных правил (`AccessDeniedException → FORBIDDEN`). `return null` означает «это не моё, передай дальше по цепочке».
- `@GraphQlExceptionHandler` в `@Controller` обрабатывает исключения его методов. Доступен с Spring GraphQL 1.2. В `@ControllerAdvice` обработчик может применяться к нескольким контроллерам.

```java
// 1. GraphQL DataFetcherExceptionResolver — перехватывает ошибки резолверов
@Component
public class OrderExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof OrderNotFoundException e) {
            return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.NOT_FOUND)
                .message("Order not found: " + e.getId())
                .build();
        }
        if (ex instanceof AccessDeniedException) {
            return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.FORBIDDEN)
                .message("Access denied")
                .build();
        }
        return null;
    }
}
```

```java
// 2. @GraphQlExceptionHandler (Spring GraphQL 1.2+)
@Controller
public class OrderController {

    @GraphQlExceptionHandler
    public GraphQLError handleOrderNotFound(OrderNotFoundException ex,
                                             DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
            .errorType(ErrorType.NOT_FOUND)
            .message(ex.getMessage())
            .build();
    }
}
```

## Q8. Как тестировать Spring GraphQL?

Два инструмента работают в паре:

- **`@GraphQlTest`** — слайс-аннотация: поднимает только GraphQL-слой (схему и указанные контроллеры), без полного контекста и без БД. Сервисы подменяются `@MockitoBean` (в Spring Boot до 3.4 — `@MockBean`, теперь эта аннотация объявлена устаревшей). Быстро, как `@WebMvcTest` для REST.
- **`GraphQlTester`** — fluent-клиент для запросов: шлёт документ, а проверки делает по JSON-пути результата (`path("order.id")`) и по массиву `errors()`.

Запрос задают двумя способами: `documentName("getOrder")` — вынести `.graphql`-файл в `src/test/resources`, либо `document("{ ... }")` — заинлайнить строку.

```java
// @GraphQlTest — тестовый слайс только для GraphQL
@GraphQlTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private GraphQlTester tester;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturnOrder() {
        when(orderService.findById("123")).thenReturn(
            Optional.of(new Order("123", "customer-1", new BigDecimal("99.99"),
                OrderStatus.PENDING))
        );

        tester.documentName("getOrder")  // src/test/resources/graphql-test/getOrder.graphql
            .variable("id", "123")
            .execute()
            .path("order.id").entity(String.class).isEqualTo("123")
            .path("order.status").entity(String.class).isEqualTo("PENDING");
    }

    @Test
    void shouldReturnErrorForMissingOrder() {
        when(orderService.findById("999")).thenReturn(Optional.empty());

        tester.document("{ order(id: \"999\") { id } }")
            .execute()
            .errors()
            .expect(e -> e.getErrorType() == ErrorType.NOT_FOUND);
    }
}
```

```graphql
# src/test/resources/graphql-test/getOrder.graphql
query GetOrder($id: ID!) {
    order(id: $id) {
        id
        customerId
        status
        total
    }
}
```

## Q9. Что такое GraphQL Directives и как их использовать?

Директива — это аннотация прямо в схеме (синтаксис `@name`), которая навешивает на поле или тип дополнительное поведение, не меняя сигнатуру. Встроенные — `@deprecated`, `@skip`, `@include`; можно объявлять и свои.

При сборке схемы GraphQL Java вызывает зарегистрированный `SchemaDirectiveWiring`. Для `@auth(role: ...)` он оборачивает исходный `DataFetcher` проверкой роли. Правило видно в схеме, а проверка применяется до вызова резолвера.

```graphql
directive @auth(role: String!) on FIELD_DEFINITION

type Query {
    orders: [Order!]! @deprecated(reason: "Use ordersByCustomer instead")
    ordersByCustomer(customerId: String!): [Order!]!
    adminReport: Report! @auth(role: "ADMIN")
}

type Report {
    id: ID!
}
```

В примере Spring MVC компонент `AuthDirectiveWiring` регистрируется под именем `auth` через бин `RuntimeWiringConfigurer`. Spring Boot передаёт configurer в сборщик схемы. Объявление директивы в SDL задаёт её имя и допустимые места применения. [RuntimeWiringConfigurer](https://docs.spring.io/spring-graphql/reference/request-execution.html#execution.graphqlsource.runtimewiringconfigurer).

```java
@Configuration(proxyBeanMethods = false)
class GraphQlConfig {
    @Bean
    RuntimeWiringConfigurer authDirective(AuthDirectiveWiring authWiring) {
        return builder -> builder.directive("auth", authWiring);
    }
}

// Обработка кастомной директивы
@Component
public class AuthDirectiveWiring implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
        String requiredRole = env.getAppliedDirective("auth")
            .getArgument("role").getValue();

        DataFetcher<?> original = env.getFieldDataFetcher();
        DataFetcher<?> authFetcher = context -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getAuthorities().stream()
                    .noneMatch(a -> a.getAuthority().equals("ROLE_" + requiredRole))) {
                throw new AccessDeniedException("Required role: " + requiredRole);
            }
            return original.get(context);
        };

        return env.setFieldDataFetcher(authFetcher);
    }
}
```

## Q10. Как реализовать пагинацию в GraphQL?

Для списков GraphQL часто используют курсорную пагинацию по спецификации Relay Connection. При keyset-пагинации граница страницы задаётся значениями полей сортировки. Вставки и удаления перед курсором не сдвигают эту границу. Обновления ключей сортировки и изменения состава выборки могут повлиять на обход между запросами.

Спецификация задаёт обёртку `Connection` с `edges` и `pageInfo`. У ребра есть узел и курсор. Для движения вперёд клиент может передать `endCursor` предыдущей страницы как `after`. Если нужен `totalCount`, добавьте его в схему и предусмотрите отдельное вычисление.

В Spring Boot 3.5 со Spring Data автоконфигурация подключает адаптеры `Window<T>` и `Slice<T>` для преобразования результата в Relay `Connection`, если используемая `CursorStrategy` поддерживает `ScrollPosition`. В этом примере `orderService.findPage` использует keyset-позицию и одинаковую сортировку на всех страницах. Поля сортировки ненулевые. Уникальный `id` определяет порядок заказов с одинаковым `created_at`. [Пагинация Spring for GraphQL](https://docs.spring.io/spring-graphql/reference/request-execution.html).

```graphql
# Листание только вперёд. orderService сортирует по created_at DESC, id DESC.
# created_at и id не содержат NULL, id уникален.
type Query {
    orders(first: Int, after: String): OrderConnection!
}

type OrderConnection {
    edges: [OrderEdge!]!
    pageInfo: PageInfo!
}

type OrderEdge {
    node: Order!
    cursor: String!
}

type PageInfo {
    hasNextPage: Boolean!
    hasPreviousPage: Boolean!
    startCursor: String
    endCursor: String
}
```

```java
@QueryMapping
public Window<Order> orders(ScrollSubrange subrange) {
    ScrollPosition position = subrange.position().orElse(ScrollPosition.keyset());
    int count = subrange.count().orElse(20);
    return orderService.findPage(count, position);
}
```

## Q11. Как Spring GraphQL интегрируется с Spring Security?

В примере Spring Boot 3.5 и Spring MVC HTTP-цепочка Spring Security аутентифицирует вызов и подготавливает контекст безопасности. Конфигурация с `@EnableMethodSecurity` включает перехват вызовов методов с `@PreAuthorize` на Spring-бинах.

На уровне `/graphql` можно ограничить доступ ко всему транспорту, а на уровне методов-резолверов задать отдельные правила для полей. Например, проверку роли можно поставить на mutation или на метод с `@SchemaMapping` для вложенного поля.

```java
// Spring MVC. HTTP-цепочка пропускает пользователя с ROLE_USER без ROLE_ADMIN.
// Пользователь аутентифицирован и вызывает deleteOrder.
// В GraphQL зарегистрирован SecurityDataFetcherExceptionResolver.
// Других обработчиков исключений безопасности в этом примере нет.
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
class MethodSecurityConfig {
}

@Controller
public class OrderController {

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Order> myOrders(Principal principal) {
        return orderService.findByCustomer(principal.getName());
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@Argument String id) {
        orderService.delete(id);
    }
}
```

```yaml
spring:
  graphql:
    schema:
      inspection:
        enabled: true  # сопоставляет поля схемы с маппингами приложения
```

В примере проверка `@PreAuthorize` запрещает вызов тела `deleteOrder`. Зарегистрированный `SecurityDataFetcherExceptionResolver` классифицирует отказ аутентифицированному пользователю как `FORBIDDEN`. Для анонимного пользователя тот же resolver может вернуть `UNAUTHORIZED`. Если ни security resolver, ни другой обработчик из Q7 не обработает исключение, GraphQL вернёт `INTERNAL_ERROR`. Отказ HTTP-цепочки до начала операции обрабатывается на уровне транспорта.

Boot 3.5 может зарегистрировать servlet resolver автоматически при выполнении условий `GraphQlWebMvcSecurityAutoConfiguration`. В примере его наличие зафиксировано явно. [Method Security](https://docs.spring.io/spring-security/reference/6.5/servlet/authorization/method-security.html) · [SecurityDataFetcherExceptionResolver](https://docs.spring.io/spring-graphql/docs/1.4.x/api/org/springframework/graphql/execution/SecurityDataFetcherExceptionResolver.html).

## Q12. Чем @SchemaMapping отличается от @QueryMapping и @MutationMapping?

```graphql
type Query {
    order(id: ID!): Order
}

type Mutation {
    createOrder(input: CreateOrderInput!): Order!
}

type Order {
    id: ID!
    customer: Customer!
}

input CreateOrderInput {
    customerId: ID!
}

type Customer {
    id: ID!
    name: String!
}
```

`@SchemaMapping` привязывает метод к полю `field` типа `typeName`. Три аннотации ниже задают `typeName` для соответствующих корневых типов:

- `@QueryMapping` ≡ `@SchemaMapping(typeName = "Query")`
- `@MutationMapping` ≡ `@SchemaMapping(typeName = "Mutation")`
- `@SubscriptionMapping` ≡ `@SchemaMapping(typeName = "Subscription")`

Если имя поля не указано, Spring берёт имя метода. `@SchemaMapping` подходит и для корневых полей. Для вложенного поля `Order.customer` можно явно указать тип `Order`. Параметр типа `Order` передаёт в резолвер родительский объект. [Маппинги контроллеров Spring for GraphQL](https://docs.spring.io/spring-graphql/reference/controllers.html).

```java
@QueryMapping
public Order order(@Argument("id") String id) {
    return orderService.findById(id);
}

@MutationMapping
public Order createOrder(@Argument("input") CreateOrderInput input) {
    return orderService.create(input);
}

@SubscriptionMapping
public Flux<Order> orderStatusChanged(@Argument("orderId") String orderId) {
    return orderEventPublisher.getOrderEvents()
        .filter(order -> order.getId().equals(orderId));
}

@SchemaMapping(typeName = "Order", field = "customer")
public Customer customer(Order order) {
    return customerService.findById(order.getCustomerId());
}
```

## Q13. Как работает introspection и когда его отключать?

Introspection — встроенная в спецификацию возможность опросить саму схему: специальными полями `__schema` и `__type` клиент узнаёт все типы, поля и аргументы. На этом держится тулинг: GraphiQL и Apollo Sandbox строят автодополнение и документацию именно через introspection.

**Когда отключать.** В production — как защита от разведки: introspection раскрывает потенциальному злоумышленнику полную карту API. Это снижает поверхность атаки (security through obscurity), но не заменяет настоящую авторизацию полей.

**Компромисс.** После отключения интроспекции перестают работать GraphiQL и Apollo Sandbox — поэтому их и так держат выключенными на проде.

```yaml
# Отключить introspection в production (защита от разведки схемы)
spring:
  graphql:
    schema:
      introspection:
        enabled: false  # доступно с Spring Boot 3.x
```

```java
// Или через GraphQL Java: убрать интроспекционные поля из видимости схемы
// import static graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY;
@Bean
public GraphQlSourceBuilderCustomizer introspectionCustomizer() {
    return builder -> builder.configureRuntimeWiring(wiring ->
        wiring.fieldVisibility(NO_INTROSPECTION_FIELD_VISIBILITY)
    );
}
```

Способов два: флаг `spring.graphql.schema.introspection.enabled: false` (Spring Boot 3.x) либо ограничение видимости полей средствами GraphQL Java через `GraphQlSourceBuilderCustomizer`.

## Q14. Как обрабатывать файловый upload в Spring GraphQL?

```graphql
# Spring MVC со штатным Spring for GraphQL. Дополнительной multipart-интеграции нет.
# Клиент отправляет multipart/form-data с файлом для аргумента file.
scalar Upload

type Mutation {
    uploadDocument(file: Upload!, orderId: ID!): Document!
}

type Document {
    id: ID!
}
```

`scalar Upload` задаёт контракт SDL. Для приёма файла нужны серверная реализация скаляра и транспортный обработчик. Штатный Spring for GraphQL не реализует GraphQL multipart request spec напрямую. Части `operations` и `map` описывают операцию и соответствие файлов её переменным. Остальные части содержат файлы. [Spring for GraphQL: File Upload](https://docs.spring.io/spring-graphql/reference/transports.html#server-transports.http.file-upload).

Вариант с `MultipartFile` ниже рассчитан на Spring MVC со сторонним `multipart-spring-graphql` 1.5.x, для которого автор приводит пример на Spring Boot 3.4.x. Интеграция принимает multipart-запрос и передаёт файл в GraphQL-переменную для `Upload`. [Документация multipart-spring-graphql](https://github.com/nkonev/multipart-spring-graphql#compatibility).

```java
@MutationMapping
public Document uploadDocument(
        @Argument("file") MultipartFile file,
        @Argument("orderId") String orderId) {
    return documentService.save(orderId, file);
}
```

В WebFlux с соответствующей интеграцией параметр файла может иметь тип `FilePart`. Другой вариант использует отдельный HTTP-эндпоинт загрузки, а GraphQL-мутация принимает идентификатор уже загруженного файла.

## Q15. Как отлаживать и мониторить GraphQL-запросы?

Главная сложность мониторинга: у GraphQL один endpoint `/graphql`, поэтому стандартные HTTP-метрики (по URL/статусу) бесполезны — все запросы выглядят одинаково. Наблюдаемость встраивают на уровне выполнения GraphQL, не транспорта.

Три уровня инструментов, от отладки к продакшену:

- **`Instrumentation`** — хук в жизненный цикл выполнения запроса (`beginExecution` и т.п.). Через него логируют сам текст запроса и время выполнения — то, чего не видно в HTTP-логах.
- **Логирование** — поднять уровень `org.springframework.graphql` и `graphql.execution` до `DEBUG`, чтобы видеть детали разбора и выполнения при отладке.
- **Метрики** — Spring Boot Actuator + Micrometer публикуют семейство `graphql.*` (время выполнения, число ошибок) для дашбордов и алертов в production.

```java
// Instrumentation для логирования запросов
@Bean
public Instrumentation loggingInstrumentation() {
    return new SimplePerformantInstrumentation() {
        @Override
        public @Nullable InstrumentationContext<ExecutionResult> beginExecution(
                InstrumentationExecutionParameters params,
                InstrumentationState state) {
            String query = params.getQuery();
            long start = System.currentTimeMillis();
            return SimpleInstrumentationContext.whenCompleted((result, ex) ->
                log.info("GraphQL query completed in {}ms: {}",
                    System.currentTimeMillis() - start, query)
            );
        }
    };
}
```

```yaml
# Включить logging для query execution
logging:
  level:
    org.springframework.graphql: DEBUG
    graphql.execution: DEBUG
```

Для production: Spring Boot Actuator выставляет метрики `graphql.*` (время выполнения, количество ошибок) через Micrometer.

## See also

- [GraphQL](../../api/graphql-interview.md) — основы GraphQL (schema, queries, resolvers, N+1 проблема)
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек для GraphQL subscriptions
- [Spring Boot](spring-boot-interview.md) — auto-configuration, starter dependencies
- [Spring REST Clients](spring-rest-client-interview.md) — REST как альтернатива GraphQL
- [Spring Security](spring-security-interview.md) — интеграция безопасности с GraphQL
- [Spring Testing](spring-testing-interview.md) — GraphQlTester для тестирования
- [Spring Data JPA](spring-data-jpa-interview.md) — репозитории, используемые в resolvers
- [Micrometer](../../monitoring/micrometer-interview.md) — метрики graphql.* через Actuator
- [HTTP & REST](../../api/http-rest-interview.md) — REST-обзор для сравнения
- [OpenAPI/Swagger](../../api/openapi-swagger-interview.md) — документирование REST в противовес GraphQL introspection
