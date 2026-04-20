---
title: "Вопросы на собеседовании: Spring for GraphQL"
description: "Spring for GraphQL: @QueryMapping, @MutationMapping, @SchemaMapping, DataLoader для N+1, subscriptions, тестирование, Spring Security интеграция"
tags:
  - interview
  - spring
  - spring-graphql-interview
aliases:
  - "Spring GraphQL interview"
  - "Spring GraphQL собеседование"
  - "Spring for GraphQL вопросы"
  - "GraphQL Spring Boot interview"
difficulty: "intermediate"
updated: "2026-04-20"
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

**Spring GraphQL** — официальная интеграция GraphQL в экосистему Spring (с версии Spring Boot 2.7+). Строится поверх GraphQL Java и поддерживает HTTP и WebSocket транспорты.

| Критерий | REST | GraphQL |
|----------|------|---------|
| Over-fetching | Частое | Нет — клиент запрашивает только нужные поля |
| Under-fetching | N+1 запросов | Одним запросом всё дерево |
| Версионирование API | `/v1/`, `/v2/` | Схема эволюционирует без версий |
| Типизация | OpenAPI/Swagger | Встроена в язык схемы (SDL) |
| Subscriptions | SSE/WebSocket вручную | Встроены в спецификацию |
| Кэширование | HTTP-кэш (легко) | Сложнее (один endpoint POST) |

Spring GraphQL добавляет аннотации `@QueryMapping`, `@MutationMapping`, `@SchemaMapping` поверх GraphQL Java.

## Q2. Как настроить Spring GraphQL?

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

## Q4. Как реализовать Mutation?

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

**N+1** — классическая проблема: при запросе N заказов, для каждого вызывается отдельный SQL за позициями → N+1 запросов к БД.

**Решение 1 — `@BatchMapping`** (Spring GraphQL):

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

**Решение 2 — DataLoader** (низкий уровень):

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

`@BatchMapping` — Spring-идиоматичный подход; `DataLoader` — более гибкий (поддерживает кэширование).

## Q6. Как реализовать GraphQL Subscriptions?

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

Директивы изменяют поведение схемы или выполнения запросов.

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

Рекомендованный стиль — Cursor-based (Relay Connection spec):

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
public Connection<Order> orders(
        @Argument int first,
        @Argument String after) {
    // Spring GraphQL поддерживает ScrollPosition для cursor pagination
    ScrollPosition position = after != null
        ? ScrollPosition.forward(CursorEncoder.decode(after))
        : ScrollPosition.keyset();

    Window<Order> window = orderService.findPage(first, position);
    return DefaultConnection.create(window, CursorStrategy.withEncoder(CursorEncoder.base64()));
}
```

## Q11. Как Spring GraphQL интегрируется с Spring Security?

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

Spring Security перехватывает запросы до их попадания в GraphQL резолвер через SecurityContextHolder.

## Q12. Чем @SchemaMapping отличается от @QueryMapping и @MutationMapping?

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

Introspection — встроенный механизм GraphQL для получения информации о схеме (`__schema`, `__type`).

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

GraphiQL и Apollo Sandbox используют introspection — при отключении они перестают работать.

## Q14. Как обрабатывать файловый upload в Spring GraphQL?

GraphQL multipart upload spec:

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

Стандарт multipart upload требует специальных клиентских библиотек (`apollo-upload-client`). Альтернатива — отдельный REST-endpoint для загрузки файлов.

## Q15. Как отлаживать и мониторить GraphQL-запросы?

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

- [[graphql-interview|GraphQL]] — основы GraphQL (schema, queries, resolvers, N+1 проблема)
- [[spring-webflux-interview|Spring WebFlux]] — реактивный стек для GraphQL subscriptions
- [[spring-boot-interview|Spring Boot]] — auto-configuration, starter dependencies
- [[spring-rest-client-interview|Spring REST Clients]] — REST как альтернатива GraphQL
- [[spring-security-interview|Spring Security]] — интеграция безопасности с GraphQL
- [[spring-testing-interview|Spring Testing]] — GraphQlTester для тестирования
- [[spring-data-jpa-interview|Spring Data JPA]] — репозитории, используемые в resolvers
- [[micrometer-interview|Micrometer]] — метрики graphql.* через Actuator
- [[http-rest-interview|HTTP & REST]] — REST-обзор для сравнения
- [[openapi-swagger-interview|OpenAPI/Swagger]] — документирование REST в противовес GraphQL introspection
