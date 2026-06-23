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

Нужны три вещи: стартер, немного YAML и файл схемы. По соглашению Spring GraphQL сам подхватывает все `*.graphqls` из `src/main/resources/graphql/` — отдельно регистрировать схему не нужно.

**Шаг 1 — зависимости.** `spring-boot-starter-graphql` плюс web- или webflux-стартер для транспорта:

```xml
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
        enabled: true     # вывод схемы при старте
    path: /graphql        # endpoint
```

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

**N+1** — самая частая проблема производительности GraphQL. Запросили список из N заказов (1 запрос), и резолвер вложенного поля `items` вызывается отдельно для каждого заказа → ещё N запросов к БД. Итого N+1. В GraphQL она острее, чем в REST: клиент сам решает, какие вложенные поля раскрывать, и сервер заранее не знает глубину дерева.

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
@Bean
public BatchLoaderRegistry batchLoaderRegistry(OrderItemService itemService) {
    return registrar -> registrar
        .<String, List<OrderItem>>forName("orderItemsLoader")
        .registerBatchLoader((orderIds, env) ->
            Flux.fromIterable(itemService.findByOrderIds(orderIds))
                .collectMultimap(OrderItem::getOrderId)
                .map(m -> orderIds.stream()
                    .map(id -> m.getOrDefault(id, List.of()))
                    .toList())
        );
}
```

**Что выбрать.** `@BatchMapping` — по умолчанию: меньше кода, читается как обычный резолвер. `DataLoader` берут, когда нужна гибкость — например, per-request кэширование загруженных значений или один и тот же loader для разных полей.

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

Исключение из резолвера нельзя просто пробросить как в REST: у GraphQL свой формат ответа — успех и массив `errors` в одном теле, всегда HTTP 200. Задача обработчика — превратить Java-исключение в `GraphQLError` с понятным `errorType` (`NOT_FOUND`, `FORBIDDEN`, ...), иначе клиент получит безликое `INTERNAL_ERROR` и скрытый текст.

Есть два подхода — выбирают по охвату:

- **`DataFetcherExceptionResolver`** — глобальный `@Component`: ловит исключения из любого резолвера. Удобно для сквозных правил (`AccessDeniedException → FORBIDDEN`). `return null` означает «это не моё, передай дальше по цепочке».
- **`@GraphQlExceptionHandler`** — локальный метод внутри `@Controller` (как `@ExceptionHandler` в MVC): обрабатывает исключения только этого контроллера. Доступен с Spring GraphQL 1.2+.

```java
// 1. GraphQL DataFetcherExceptionResolver — перехватывает ошибки резолверов
@Component
public class OrderExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof OrderNotFoundException e) {
            return GraphQLError.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message("Order not found: " + e.getId())
                .path(env.getExecutionStepInfo().getPath())
                .build();
        }
        if (ex instanceof AccessDeniedException) {
            return GraphQLError.newError()
                .errorType(ErrorType.FORBIDDEN)
                .message("Access denied")
                .build();
        }
        return null; // передать дальше в default handler
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
        return GraphQLError.newError()
            .errorType(ErrorType.NOT_FOUND)
            .message(ex.getMessage())
            .build();
    }
}
```

## Q8. Как тестировать Spring GraphQL?

Два инструмента работают в паре:

- **`@GraphQlTest`** — слайс-аннотация: поднимает только GraphQL-слой (схему и указанные контроллеры), без полного контекста и без БД. Сервисы подменяются `@MockBean`. Быстро, как `@WebMvcTest` для REST.
- **`GraphQlTester`** — fluent-клиент для запросов: шлёт документ, а проверки делает по JSON-пути результата (`path("order.id")`) и по массиву `errors()`.

Запрос задают двумя способами: `documentName("getOrder")` — вынести `.graphql`-файл в `src/test/resources`, либо `document("{ ... }")` — заинлайнить строку.

```java
// @GraphQlTest — тестовый слайс только для GraphQL
@GraphQlTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private GraphQlTester tester;

    @MockBean
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

Кастомная директива на сервере реализуется через `SchemaDirectiveWiring`: при сборке схемы Spring оборачивает оригинальный `DataFetcher` поля своей логикой. Классический пример — `@auth(role: ...)`: обёртка проверяет роль до вызова поля и кидает `AccessDeniedException`, если прав не хватает. Плюс подхода — правило авторизации видно прямо в схеме и не дублируется по резолверам.

```graphql
# Встроенные директивы
type Query {
    orders: [Order!]! @deprecated(reason: "Use ordersByCustomer instead")
}

# Кастомная директива для авторизации
directive @auth(role: String!) on FIELD_DEFINITION

type Query {
    adminReport: Report! @auth(role: "ADMIN")
}
```

```java
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
            if (!auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + requiredRole))) {
                throw new AccessDeniedException("Required role: " + requiredRole);
            }
            return original.get(context);
        };

        return env.setFieldDataFetcher(authFetcher);
    }
}
```

## Q10. Как реализовать пагинацию в GraphQL?

Стандарт де-факто — **cursor-based** пагинация по спецификации Relay Connection. В отличие от offset/limit, она устойчива к вставкам и удалениям между страницами: курсор указывает на конкретную позицию в наборе, а не на номер строки, который «съезжает».

Спецификация задаёт стандартную обёртку **Connection**: `edges` (узел + его курсор), `pageInfo` (`hasNextPage`, `endCursor` для подгрузки следующей страницы) и опционально `totalCount`. Клиент листает вперёд аргументами `first` + `after`.

Spring GraphQL поддерживает это нативно: резолвер возвращает напрямую `Window<T>` (или `Slice<T>`), а Spring сам оборачивает результат в Relay `Connection` через сконфигурированный `ConnectionFieldTypeVisitor` и `CursorStrategy` — вручную строить `Connection` в контроллере не нужно. Под капотом данные берутся через Spring Data `ScrollPosition`/`Window` (keyset-пагинация), курсоры кодирует/декодирует `CursorStrategy`.

```graphql
type Query {
    orders(first: Int, after: String, last: Int, before: String): OrderConnection!
}

type OrderConnection {
    edges: [OrderEdge!]!
    pageInfo: PageInfo!
    totalCount: Int!
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
    // ScrollSubrange собирается Spring GraphQL из аргументов first/after.
    // Возвращаем Window<Order> напрямую — Spring сам обернёт его
    // в Relay Connection через сконфигурированный CursorStrategy.
    ScrollPosition position = subrange.position().orElse(ScrollPosition.keyset());
    int count = subrange.count().orElse(20);
    return orderService.findPage(count, position);
}
```

## Q11. Как Spring GraphQL интегрируется с Spring Security?

Аутентификация навешивается на транспорт (HTTP/WebSocket) обычной Spring Security цепочкой фильтров — она и наполняет `SecurityContextHolder` до того, как запрос дойдёт до резолвера. Дальше на каждое поле работает **method-level security**: `@PreAuthorize` на методах-резолверах применяется так же, как на любом Spring-бине, out-of-the-box.

Тонкость GraphQL: один HTTP-запрос может дёргать много полей разных типов. Поэтому авторизацию вешают **на резолверы, а не на endpoint** — у `/graphql` он один. Можно ограничивать и точечно (`@PreAuthorize("hasRole('ADMIN')")` на конкретной mutation), и на вложенных полях через `@SchemaMapping`.

```java
// Method-level security работает out-of-the-box
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
        enabled: true  # проверяет, что все типы определены при старте
```

**Граничный случай.** `@PreAuthorize` бросает `AccessDeniedException` — её нужно явно превратить в `GraphQLError` с `errorType = FORBIDDEN` (см. Q7), иначе клиент получит безликий `INTERNAL_ERROR`.

## Q12. Чем @SchemaMapping отличается от @QueryMapping и @MutationMapping?

`@SchemaMapping` — базовая аннотация: привязывает метод к полю `field` любого типа `typeName`. Остальные три — её удобные сокращения с зафиксированным `typeName` для трёх корневых типов:

- `@QueryMapping` ≡ `@SchemaMapping(typeName = "Query")`
- `@MutationMapping` ≡ `@SchemaMapping(typeName = "Mutation")`
- `@SubscriptionMapping` ≡ `@SchemaMapping(typeName = "Subscription")`

Во всех случаях имя поля по умолчанию берётся из имени метода. `@SchemaMapping` нужен напрямую там, где целевой тип — **не** корневой: для резолверов вложенных полей доменных типов (`Order.customer`, `Order.items`). Признак такого резолвера — первым параметром идёт родительский объект.

```java
// @QueryMapping — сокращение для @SchemaMapping(typeName = "Query")
@QueryMapping           // ≡ @SchemaMapping(typeName = "Query", field = "order")
public Order order(@Argument String id) { ... }

// @MutationMapping — сокращение для @SchemaMapping(typeName = "Mutation")
@MutationMapping        // ≡ @SchemaMapping(typeName = "Mutation", field = "createOrder")
public Order createOrder(@Argument CreateOrderInput input) { ... }

// @SubscriptionMapping — сокращение для @SchemaMapping(typeName = "Subscription")
@SubscriptionMapping
public Flux<Order> orderStatusChanged(@Argument String orderId) { ... }

// @SchemaMapping — для вложенных типов (не только Query/Mutation/Subscription)
@SchemaMapping(typeName = "Order", field = "customer")
public Customer customer(Order order) { ... }
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
// Или через конфигурацию GraphQL Java
@Bean
public GraphQlSourceBuilderCustomizer customizer() {
    return builder -> builder.configureGraphQl(graphQL ->
        graphQL.queryExecutionStrategy(new DisableIntrospectionStrategy())
    );
}
```

Способов два: флаг `spring.graphql.schema.introspection.enabled: false` (Spring Boot 3.x) либо ручная стратегия в GraphQL Java через `GraphQlSourceBuilderCustomizer`.

## Q14. Как обрабатывать файловый upload в Spring GraphQL?

В самом GraphQL бинарных файлов нет — запросы это JSON. Загрузку добавляют через неофициальную **GraphQL multipart request spec**: объявляют кастомный скаляр `Upload`, а тело шлют как `multipart/form-data`, где первая часть — обычный GraphQL-запрос, остальные — файлы.

**Подводный камень.** Спецификация не входит в стандарт, поэтому требует особых клиентских библиотек (`apollo-upload-client`) и серверной обвязки. На практике часто проще обойти GraphQL: сделать отдельный REST-endpoint для загрузки, а в мутацию передавать уже полученный ID/URL файла.

```graphql
scalar Upload

type Mutation {
    uploadDocument(file: Upload!, orderId: ID!): Document!
}
```

```java
@MutationMapping
public Document uploadDocument(
        @Argument Part file,   // Spring WebFlux Part / MultipartFile
        @Argument String orderId) {
    return documentService.save(orderId, file);
}
```

На стороне Java аргумент со скаляром `Upload` приходит как `MultipartFile` (MVC) или `Part` (WebFlux) — дальше работаете с ним как с обычной загрузкой.

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
