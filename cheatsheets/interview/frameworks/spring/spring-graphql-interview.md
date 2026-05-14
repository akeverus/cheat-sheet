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
updated: "2026-04-25"
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


> [!mcq]
>
> **Вопрос:** Команда мигрирует мобильный backend с REST на GraphQL. Какое из утверждений о Spring GraphQL и его преимуществах перед REST является ТОЧНЫМ для production-сценария?
>
> ---
>
> #### A) GraphQL автоматически решает N+1-проблему — об этом заботится сам Spring GraphQL без дополнительной настройки — ❌ Неверно
>
> **Что на самом деле:** Spring GraphQL ровно наоборот — провоцирует N+1, потому что каждый вложенный resolver вызывается для каждой строки родителя. Решение требует явного `@BatchMapping` или `DataLoader` — фреймворк не угадывает batch-loading сам.
>
> **Откуда путаница:** Маркетинг GraphQL говорит «один запрос вместо нескольких» — но это про количество HTTP round-trips клиент↔сервер, а не про SQL на стороне сервера.
>
> **Если бы это было правдой:** Команды не писали бы `BatchMapping` и не было бы знаменитой статьи Shopify «How we tamed N+1». На практике первый production-инцидент после миграции на GraphQL — это именно N+1, который выжигает DB pool.
>
> ---
>
> #### B) Spring GraphQL требует отдельного endpoint на каждый тип query, как REST — `/graphql/order`, `/graphql/user` — ❌ Неверно
>
> **Что на самом деле:** В GraphQL ровно ОДИН endpoint (`POST /graphql` по умолчанию). Все queries, mutations и subscriptions передаются в теле запроса. Это фундаментальная особенность протокола — клиент сам решает, что запросить.
>
> **Откуда путаница:** Перенос REST-привычек «ресурс = URL» на GraphQL. В REST `/orders/{id}` — URL описывает ресурс; в GraphQL поле `order(id)` описывается в SDL.
>
> **Если бы это было правдой:** Терялось бы главное преимущество GraphQL — единая точка входа. Невозможно было бы написать query с join'ом разных типов в одном запросе (`{ order { items, customer } }`).
>
> ---
>
> #### C) Spring GraphQL — это полный замены REST: после миграции REST endpoints больше не нужны — ❌ Неверно
>
> **Что на самом деле:** GraphQL и REST сосуществуют. File upload, CDN-кэшируемые public endpoints, webhooks, OAuth-callbacks — всё это лучше оставить на REST. Netflix, GitHub, Shopify держат оба стека параллельно.
>
> **Откуда путаница:** Trade-press преподносит GraphQL как «next-gen REST». На деле это другой инструмент с другими trade-off'ами: лучше для сложных join'ов клиента, хуже для CDN-кэша (POST не кэшируется), хуже для бинарных данных.
>
> **Если бы это было правдой:** Никто бы не сохранял REST для health-checks, file uploads (`apollo-upload-client` — отдельная история), prometheus scraping endpoints.
>
> ---
>
> #### D) GraphQL устраняет over-fetching (клиент запрашивает только нужные поля) и under-fetching (всё дерево одним запросом), но требует ручной заботы о кэшировании — ✓ Верно
>
> **Развёрнутое объяснение:** Главное преимущество GraphQL — selective field selection: клиент указывает `{ order { id, total } }` и получает ровно эти поля, без `customer`, `items`, `address`. Это решает over-fetching типичный для REST. Для under-fetching клиент может объединить запрос: `{ order { items { product { name } } } }` — одним round-trip получит всё дерево. Но HTTP-кэш CDN/Varnish работает с `GET /resource` и его URL-ключом; в GraphQL все запросы — `POST /graphql` с разным телом, поэтому CDN не кэширует. Решения: Apollo Cache на клиенте, persisted queries (превращают сложные queries в `GET` с hash), Automatic Persisted Queries (APQ).
>
> **Пример:**
> ```graphql
> # Один GraphQL-запрос вместо 3-х REST-вызовов
> query OrderDetails($id: ID!) {
>   order(id: $id) {
>     id
>     total
>     customer { name email }
>     items { product { name price } quantity }
>   }
> }
> ```
> ```java
> // Spring GraphQL добавляет аннотации поверх GraphQL Java
> @Controller
> public class OrderController {
>     @QueryMapping
>     public Order order(@Argument String id) {
>         return orderService.findById(id).orElseThrow();
>     }
> }
> ```
>
> **Когда применять:** Mobile/SPA-клиенты с разнородными view (разные экраны = разные поля), BFF для микросервисов (агрегация под клиент), публичный API с разными потребителями (GitHub v4, Shopify Storefront API). Netflix Federation объединяет ~700 микросервисов одной схемой.
>
> **Подводные камни:** N+1 на сервере (нужны DataLoader/BatchMapping), сложность кэширования (нет URL-key), query complexity attacks (нужны depth/complexity limits), отсутствие HTTP-status semantics (все ошибки приходят в `errors[]` с HTTP 200).
>
> **Связанные вопросы:** [[Q5]] — N+1 и DataLoader, [[Q11]] — Spring Security, [[Q13]] — introspection.

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


> [!mcq]
>
> **Вопрос:** Вы добавили `spring-boot-starter-graphql` и положили `schema.graphqls` в `src/main/resources/`, но при старте Spring Boot не видит схему. В чём наиболее вероятная причина?
>
> ---
>
> #### A) Spring GraphQL — это code-first фреймворк: схема должна генерироваться из Java-классов через аннотации `@Type`, а не из `.graphqls` файла — ❌ Неверно
>
> **Что на самом деле:** Spring GraphQL — **schema-first**. SDL-файл (`schema.graphqls`) — единственный источник правды. Аннотации `@QueryMapping` и `@SchemaMapping` лишь связывают Java-методы с уже определёнными в SDL полями.
>
> **Откуда путаница:** Существуют code-first библиотеки (Netflix DGS, до некоторой степени; GraphQL Kickstart с `@GraphQLQuery`), и разработчики, пришедшие из JAX-RS, ожидают аннотационного подхода.
>
> **Если бы это было правдой:** Не нужно было бы писать SDL вообще — но тогда теряется ключевое преимущество schema-first: schema как контракт между frontend/backend командами, который можно версионировать отдельно от кода.
>
> ---
>
> #### B) Spring GraphQL ищет схемы только в `src/main/resources/graphql/` (по умолчанию), а не в корне resources — ✓ Верно
>
> **Развёрнутое объяснение:** Auto-configuration сканирует папку `classpath:graphql/**/` для файлов с расширениями `.graphqls`, `.gqls`, `.graphql`. Если положить `schema.graphqls` прямо в `src/main/resources/`, Spring его не найдёт. Можно переопределить через `spring.graphql.schema.locations=classpath:my-schemas/`. Можно иметь несколько файлов (`order.graphqls`, `customer.graphqls`) — они мерджатся в одну схему. Это распространённая ошибка при первой настройке.
>
> **Пример:**
> ```
> src/main/resources/
>   graphql/                        ← обязательная папка
>     schema.graphqls
>     order.graphqls
>     customer.graphqls
> ```
> ```yaml
> spring:
>   graphql:
>     graphiql:
>       enabled: true            # UI на /graphiql
>     schema:
>       printer:
>         enabled: true          # вывод схемы при старте
>       locations: classpath:graphql/**/   # переопределение по необходимости
>     path: /graphql             # endpoint (по умолчанию /graphql)
> ```
>
> **Когда применять:** Любой проект на Spring GraphQL — папка `graphql/` это конвенция. Большие проекты делят на `schema.graphqls` + `directives.graphqls` + per-module файлы.
>
> **Подводные камни:** Если schema-файл не найден, контекст стартует, но при запросе вернётся `Schema is not configured`. Включите `spring.graphql.schema.printer.enabled=true` — Spring выведет загруженную схему в логи при старте, это упрощает диагностику.
>
> ---
>
> #### C) Нужно вручную регистрировать `GraphQlSource` bean — Spring Boot не делает этого автоматически — ❌ Неверно
>
> **Что на самом деле:** `GraphQlAutoConfiguration` создаёт `GraphQlSource` автоматически при наличии starter'а. Ручная регистрация нужна только для специфичных кейсов (federation, custom scalar wiring).
>
> **Откуда путаница:** Опыт работы с «голым» GraphQL Java, где `GraphQLSchema` собирался руками через `SchemaParser` и `RuntimeWiring`.
>
> **Если бы это было правдой:** Smoke-test Spring GraphQL занимал бы 100+ строк boilerplate'а, а documentation tutorial Pivotal начинался бы со схемного wiring'а — на деле он начинается с `@QueryMapping`.
>
> ---
>
> #### D) `spring-boot-starter-graphql` уже включает HTTP-транспорт — добавлять `spring-boot-starter-web` не нужно — ❌ Неверно
>
> **Что на самом деле:** Starter содержит только GraphQL-engine и infrastructure. HTTP-транспорт требует `spring-boot-starter-web` (MVC) ИЛИ `spring-boot-starter-webflux` (reactive). WebSocket-транспорт для subscriptions — отдельный `spring-boot-starter-websocket`.
>
> **Откуда путаница:** Аналогия со `spring-boot-starter-data-jpa`, который включает всё нужное. Но GraphQL-starter design отличается — он транспорт-агностичен.
>
> **Если бы это было правдой:** Невозможно было бы выбирать между блокирующим (Tomcat+MVC) и реактивным (Netty+WebFlux) транспортом для GraphQL — но Spring специально оставил эту гибкость.
>
> ---
>
> **Связанные вопросы:** [[Q1]] — что такое Spring GraphQL, [[Q3]] — реализация Query resolver, [[Q6]] — subscriptions через WebSocket.

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


> [!mcq]
>
> **Вопрос:** В контроллере вы пишете `@SchemaMapping(typeName="Order", field="customer") public Customer customer(Order order)`. В чём ключевое отличие `@SchemaMapping` от `@QueryMapping` и когда его использовать?
>
> ---
>
> #### A) `@QueryMapping` — для root-полей корневого `type Query`, `@SchemaMapping` — для полей вложенных типов (subobject resolver), который получает родительский объект как параметр — ✓ Верно
>
> **Развёрнутое объяснение:** `@QueryMapping` — синтаксический сахар для `@SchemaMapping(typeName="Query")`. `@MutationMapping` — то же для `Mutation`, `@SubscriptionMapping` — для `Subscription`. А `@SchemaMapping` с указанием `typeName="Order"` навешивается на метод-резолвер, который запускается, когда клиент запрашивает поле вложенного типа. Первый аргумент метода — родительский объект (`Order`), что позволяет лениво загрузить связанные данные только если они запрошены в query. Это основа подхода «resolve on demand» в GraphQL.
>
> **Пример:**
> ```java
> @Controller
> public class OrderController {
>     @QueryMapping                            // = @SchemaMapping(typeName="Query", field="order")
>     public Order order(@Argument String id) {
>         return orderService.findById(id).orElseThrow();
>     }
>
>     // Резолвер для поля Order.customer — вызывается только если клиент его запросил
>     @SchemaMapping(typeName = "Order", field = "customer")
>     public Customer customer(Order order) {
>         return customerService.findById(order.getCustomerId());
>     }
>
>     // Если имя метода совпадает с именем поля, field можно опустить
>     @SchemaMapping  // typeName выводится из типа аргумента Order
>     public List<OrderItem> items(Order order) {
>         return itemService.findByOrderId(order.getId());
>     }
> }
> ```
>
> **Когда применять:** Federation, BFF, любая схема с вложенными типами, где не каждое поле нужно грузить всегда. Cinque-эффективно с `@BatchMapping` (Q5).
>
> **Подводные камни:** Без batching `@SchemaMapping` создаёт N+1: для списка из 100 заказов resolver `customer` вызывается 100 раз. Spring GraphQL это видит и пишет warning в логах — игнорировать нельзя.
>
> ---
>
> #### B) `@SchemaMapping` нужен только для миграции legacy-кода — в новых проектах достаточно `@QueryMapping` для всего — ❌ Неверно
>
> **Что на самом деле:** `@SchemaMapping` — основной примитив для вложенных полей; `@QueryMapping`/`@MutationMapping`/`@SubscriptionMapping` — лишь сокращения для трёх root-операций. Без `@SchemaMapping` (или DataLoader) невозможно правильно резолвить вложенные типы без жадной загрузки всего дерева в root-resolver.
>
> **Откуда путаница:** Tutorials часто показывают только `@QueryMapping`, потому что start-up examples используют плоские DTO. Реальные схемы со связями требуют `@SchemaMapping`.
>
> **Если бы это было правдой:** В корневом resolver `order(id)` приходилось бы жадно загружать всё (`customer`, `items`, `address`) — даже если клиент запросил только `id`. Это уничтожает основное преимущество GraphQL.
>
> ---
>
> #### C) `@SchemaMapping` работает только с реактивным стеком (WebFlux) и возвращает `Mono`/`Flux` — ❌ Неверно
>
> **Что на самом деле:** `@SchemaMapping` работает в обоих стеках. В MVC возвращает обычные типы (`Customer`, `List<OrderItem>`); в WebFlux — `Mono<Customer>`, `Flux<OrderItem>`. Spring адаптирует возврат к нужному типу через `ReactiveAdapterRegistry`.
>
> **Откуда путаница:** Subscriptions реально требуют `Flux` (стриминг событий), но это специфика `@SubscriptionMapping`, не `@SchemaMapping`.
>
> **Если бы это было правдой:** Невозможно было бы использовать GraphQL c JPA-репозиториями в blocking-режиме — но именно так начинают 80% проектов.
>
> ---
>
> #### D) `@SchemaMapping(typeName="Query", field="order")` и `@QueryMapping public Order order()` дают разное поведение при выполнении — ❌ Неверно
>
> **Что на самом деле:** Это полностью эквивалентные формы. `@QueryMapping` — алиас, расшифровывается в `@SchemaMapping(typeName="Query")`. Field выводится из имени метода (если не указан явно). Никакой runtime-разницы нет.
>
> **Откуда путаница:** Кажется, что специализированная аннотация должна иметь специальное поведение, но это лишь сахар для читаемости.
>
> **Если бы это было правдой:** В Spring GraphQL были бы скрытые «волшебные» отличия между алиасами и базовой аннотацией — это нарушило бы принцип least surprise, на котором держится Spring.
>
> ---
>
> **Связанные вопросы:** [[Q5]] — N+1 при `@SchemaMapping`, [[Q12]] — детальное сравнение аннотаций, [[Q4]] — `@MutationMapping`.

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


> [!mcq]
>
> **Вопрос:** Mutation `createOrder` принимает `CreateOrderInput` — где правильно располагается логика валидации и какой механизм аннотаций гарантирует, что Spring GraphQL правильно проиграет `@Argument` на `input`-тип?
>
> ---
>
> #### A) В GraphQL валидация не нужна — schema-first гарантирует корректность типов на уровне SDL, бизнес-проверки делает БД через constraints — ❌ Неверно
>
> **Что на самом деле:** SDL проверяет только структуру (типы, required vs nullable). Бизнес-правила («сумма не больше лимита», «дата в будущем», «email уникален в рамках организации») не выражаются SDL и должны проверяться в Java. Опираться только на DB constraints — антипаттерн: ошибки приходят слишком поздно (после открытия транзакции), плохо мапятся в GraphQL errors.
>
> **Откуда путаница:** Schema валидация даёт ложное чувство безопасности. Действительно — GraphQL не пропустит `customerId: 123` где ожидается `String`, но проверка «order.total > 0» — забота приложения.
>
> **Если бы это было правдой:** Никто не использовал бы Bean Validation (`@Valid`, `@NotBlank`, `@Min`) в GraphQL-резолверах — но Spring GraphQL прямо поддерживает их интеграцию с `@Validated`.
>
> ---
>
> #### B) `@MutationMapping` — это семантический алиас `@SchemaMapping(typeName="Mutation")`, а `@Argument` биндит SDL input на Java DTO; для валидации применяется Bean Validation (`@Valid`/`@Validated`) — ✓ Верно
>
> **Развёрнутое объяснение:** `@MutationMapping` отличается от Query только по семантике GraphQL: операции в `mutation { ... }` выполняются последовательно (в отличие от Query, где исполнение полей может быть параллельным). На уровне Spring это просто sugar для `@SchemaMapping(typeName="Mutation")`. `@Argument` биндит SDL-параметр на Java-объект — `CreateOrderInput` в SDL должен быть `input` типом, в Java это POJO/record с совпадающими полями. Для валидации добавьте `@Validated` на класс-контроллер и `@Valid` перед `@Argument` — Spring применит Bean Validation и при нарушении выбросит `ConstraintViolationException`, который можно поймать в `DataFetcherExceptionResolver`.
>
> **Пример:**
> ```graphql
> input CreateOrderInput {
>     customerId: String!
>     items: [OrderItemInput!]!
> }
>
> type Mutation {
>     createOrder(input: CreateOrderInput!): Order!
> }
> ```
> ```java
> @Controller
> @Validated
> @RequiredArgsConstructor
> public class OrderMutationController {
>     private final OrderService orderService;
>
>     @MutationMapping
>     public Order createOrder(@Valid @Argument CreateOrderInput input) {
>         return orderService.create(input);
>     }
> }
>
> public record CreateOrderInput(
>     @NotBlank String customerId,
>     @NotEmpty @Valid List<OrderItemInput> items
> ) {}
>
> public record OrderItemInput(
>     @NotBlank String productId,
>     @Min(1) int quantity
> ) {}
> ```
>
> **Когда применять:** Любая mutation, изменяющая данные. Yandex.Market, Shopify Storefront API используют именно такой паттерн `mutationName(input: SomeInput!)` (input-объект, а не список аргументов) — это даёт обратную совместимость при добавлении полей.
>
> **Подводные камни:** `@Argument(name="input")` нужно указывать явно, если имя SDL-аргумента не совпадает с именем Java-параметра. Mutations выполняются СЕРИЙНО в рамках одного запроса (если клиент шлёт `mutation { a, b, c }` — выполнятся `a → b → c`), но между разными запросами параллелизм есть — учитывайте при операциях с общим стейтом.
>
> ---
>
> #### C) Mutations в Spring GraphQL должны возвращать `void` или `boolean` — клиент сам перезапросит данные через Query — ❌ Неверно
>
> **Что на самом деле:** Best practice — возвращать изменённый объект (или payload-тип `{ order, errors }`). Это экономит round-trip: клиент сразу обновит свой кэш данными из mutation response.
>
> **Откуда путаница:** REST-паттерн `POST /orders` иногда возвращает только `Location` header и пустое тело. В GraphQL это антипаттерн.
>
> **Если бы это было правдой:** Apollo Client не мог бы делать optimistic updates и cache normalization после mutation — но это его ключевая фича.
>
> ---
>
> #### D) `@MutationMapping` нельзя комбинировать с `@PreAuthorize` — авторизация в GraphQL делается только через directive `@auth` — ❌ Неверно
>
> **Что на самом деле:** Method-level security (`@PreAuthorize`, `@Secured`) работает с `@MutationMapping` out-of-the-box. Spring Security перехватывает вызов до того, как Spring GraphQL отдаст результат. Directives — дополнительный механизм для декларативной авторизации на уровне SDL (см. Q9).
>
> **Откуда путаница:** В Apollo Server (JS-мире) есть популярный паттерн авторизации через `directive @auth`. В Spring привычнее `@PreAuthorize` на методах — оба подхода легитимны.
>
> **Если бы это было правдой:** Невозможно было бы переиспользовать существующую Security-конфигурацию из REST-стека — но Spring специально сделал интеграцию seamless.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — Query resolvers, [[Q7]] — error handling валидации, [[Q11]] — Spring Security.

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


> [!mcq]
>
> **Вопрос:** Production-сервис на Spring GraphQL: запрос `{ orders(first: 100) { customer { name } items { product { name } } } }` создаёт 200+ SQL-запросов. В чём корень проблемы и какое решение Spring GraphQL даёт идиоматичнее всего?
>
> ---
>
> #### A) Это поведение GraphQL по дизайну — клиенту нужно ограничить запросы через `query complexity`; на уровне резолверов ничего не сделать — ❌ Неверно
>
> **Что на самом деле:** Query complexity лимиты — это защита от DoS, а не от N+1. Они отрезают слишком глубокие/широкие queries, но не решают batching. На стороне resolver'а как раз и надо чинить N+1 — иначе любой допустимый query будет медленным.
>
> **Откуда путаница:** Смешение двух разных проблем: complexity attack (злоумышленник присылает огромный query) vs N+1 (легитимный query генерирует много SQL).
>
> **Если бы это было правдой:** Нельзя было бы оптимизировать GraphQL — но Shopify, Netflix, GitHub публиковали кейсы где DataLoader снижал нагрузку на DB в 50-100 раз.
>
> ---
>
> #### B) `@BatchMapping` — Spring-идиоматичный подход: один метод получает `List<Parent>` и возвращает `Map<Parent, Child>` (или `Collection<Child>`), Spring сам группирует вызовы — ✓ Верно
>
> **Развёрнутое объяснение:** Когда query запрашивает поле `items` для 100 заказов, без batching Spring вызвал бы resolver `items(Order)` 100 раз — каждый со своим SQL. `@BatchMapping` меняет сигнатуру: метод принимает `List<Order>` (весь батч сразу) и возвращает `Map<Order, List<OrderItem>>`. Spring собирает все 100 родителей, вызывает resolver ОДИН раз, и распределяет результат обратно по каждому Order. Под капотом это работает через GraphQL Java DataLoader, но без явной регистрации. Возможны два возврата: `Map<Parent, Child>` (1:1) или `Map<Parent, List<Child>>` (1:N).
>
> **Пример:**
> ```java
> @Controller
> @RequiredArgsConstructor
> public class OrderBatchResolver {
>     private final OrderItemService itemService;
>
>     // Один SQL вместо N — Spring сам собирает батч
>     @BatchMapping(typeName = "Order", field = "items")
>     public Map<Order, List<OrderItem>> items(List<Order> orders) {
>         List<String> ids = orders.stream().map(Order::getId).toList();
>         Map<String, List<OrderItem>> byOrderId = itemService.findByOrderIds(ids)
>             .stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
>         return orders.stream().collect(
>             Collectors.toMap(o -> o, o -> byOrderId.getOrDefault(o.getId(), List.of()))
>         );
>     }
> }
> ```
>
> **Когда применять:** Любые 1:N или N:1 связи в схеме (`Order.customer`, `Order.items`, `Product.category`). Альтернатива — `DataLoader` через `BatchLoaderRegistry`, более гибкая (есть кэширование, асинхронность, custom keys), но требует больше boilerplate.
>
> **Подводные камни:** Batch резолверы выполняются АСИНХРОННО к основной query — Spring дожидается завершения parent-резолвера и собирает все child-запросы в один tick event loop'а. В реактивном стеке возвращайте `Mono<Map<Order, List<OrderItem>>>` или `Flux<Map.Entry<...>>`. Если у разных Order одинаковый key, dedupe должен быть в SQL — `findByOrderIds(distinctIds)`.
>
> ---
>
> #### C) Достаточно использовать `JOIN FETCH` в JPA-репозитории — N+1 исчезает на уровне SQL — ❌ Неверно
>
> **Что на самом деле:** `JOIN FETCH` помогает в REST, где сервер знает, какие поля нужны. В GraphQL клиент решает динамически — иногда нужно `items`, иногда нет. Жадный `JOIN FETCH` всегда грузит всё, теряя главное преимущество GraphQL.
>
> **Откуда путаница:** Опыт JPA-оптимизации в REST. Там стратегия «eager fetch при необходимости» работает, а в GraphQL — нет.
>
> **Если бы это было правдой:** Не существовало бы DataLoader как концепции — но Facebook изобрёл его именно для GraphQL и эта библиотека портирована во все языки.
>
> ---
>
> #### D) Spring GraphQL автоматически детектит N+1 и применяет batch — нужно лишь включить `spring.graphql.batch.auto=true` — ❌ Неверно
>
> **Что на самом деле:** Такого свойства не существует. Spring GraphQL не имеет magic auto-batching: разработчик явно объявляет `@BatchMapping` или регистрирует `BatchLoader`. Магия Hibernate `@BatchSize` или `default_batch_fetch_size` — это про JPA, не про GraphQL.
>
> **Откуда путаница:** Кажется, что современный фреймворк должен «угадывать» — но в GraphQL Spring не знает, какие resolver'ы относятся к одному root-запросу без явной декларации.
>
> **Если бы это было правдой:** N+1 был бы решённой проблемой в индустрии — но он остаётся #1 причиной production-инцидентов после миграции на GraphQL.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — `@SchemaMapping` создаёт N+1, [[Q15]] — мониторинг и обнаружение медленных queries, [[Q10]] — пагинация.

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


> [!mcq]
>
> **Вопрос:** Команда добавляет real-time-обновление статуса заказа через GraphQL Subscriptions. Какой транспорт необходимо настроить и какой тип резолвер должен вернуть?
>
> ---
>
> #### A) Subscriptions работают поверх HTTP long-polling — резолвер возвращает обычный объект, Spring сам организует polling — ❌ Неверно
>
> **Что на самом деле:** GraphQL spec для subscriptions требует push-семантики через WebSocket (стандарт `graphql-ws`) или SSE. Long-polling — это вообще не subscription, это antipattern имитации. Spring GraphQL никогда не делает polling за разработчика.
>
> **Откуда путаница:** Опыт реализации «realtime» через polling в legacy REST. Это работало, но было дорого и неэффективно — для того и нужны subscriptions.
>
> **Если бы это было правдой:** Не было бы отдельной зависимости `spring-boot-starter-websocket` для GraphQL subscriptions — но она прямо документирована в Spring GraphQL reference.
>
> ---
>
> #### B) Резолвер возвращает `Flux<T>` (Project Reactor), транспорт — WebSocket по протоколу `graphql-ws`, который необходимо включить через `spring.graphql.websocket.path` — ✓ Верно
>
> **Развёрнутое объяснение:** `@SubscriptionMapping` обязан вернуть `Flux<T>` или `Publisher<T>` — это поток событий, который Spring отдаёт клиенту через WebSocket. Внутри метода обычно используется `Sinks.Many` или интеграция с Kafka/Reactor для получения событий. Транспорт `graphql-ws` (наследник `subscriptions-transport-ws`) включается отдельно через `spring.graphql.websocket.path=/graphql-ws`. На клиенте библиотеки Apollo Client, Relay, graphql-ws управляют WebSocket-соединением. RSocket — опциональный транспорт для backend-to-backend интеграций (`spring.graphql.rsocket.mapping=graphql`). HTTP/SSE-транспорт для subscriptions появился в Spring GraphQL 1.3+ как альтернатива WebSocket, удобная для прохождения через прокси/firewalls.
>
> **Пример:**
> ```graphql
> type Subscription {
>     orderStatusChanged(orderId: ID!): Order!
> }
> ```
> ```java
> @Controller
> @RequiredArgsConstructor
> public class OrderSubscriptionController {
>     private final Sinks.Many<Order> sink = Sinks.many().multicast().onBackpressureBuffer();
>
>     @SubscriptionMapping
>     public Flux<Order> orderStatusChanged(@Argument String orderId) {
>         return sink.asFlux().filter(o -> o.getId().equals(orderId));
>     }
>
>     @EventListener
>     public void onOrderUpdated(OrderUpdatedEvent event) {
>         sink.tryEmitNext(event.getOrder());
>     }
> }
> ```
> ```yaml
> spring:
>   graphql:
>     websocket:
>       path: /graphql-ws
>       connection-init-timeout: 60s
> ```
>
> **Когда применять:** Live-обновления (статусы заказов, котировки бирж, чаты, dashboards). Yandex Trading использует subscriptions для тикеров; GitHub — для уведомлений; Hasura встроен на subscriptions поверх Postgres LISTEN/NOTIFY.
>
> **Подводные камни:** Sticky-сессии для WebSocket (load balancer), backpressure при медленных клиентах (`Sinks.Many.onBackpressureBuffer` имеет лимит), масштабирование с несколькими инстансами (нужен Redis Pub/Sub или Kafka для дистрибуции событий), аутентификация (token в `connection_init`-сообщении, не в HTTP-header).
>
> ---
>
> #### C) Достаточно `@QueryMapping public Order orderStatusChanged()` — Spring сам распознает subscription по имени поля в SDL — ❌ Неверно
>
> **Что на самом деле:** `@QueryMapping` маппит на `type Query`, `@SubscriptionMapping` — на `type Subscription`. Это разные операционные типы GraphQL. Spring не угадывает по имени поля — он смотрит на аннотацию.
>
> **Откуда путаница:** Кажется, что schema-first означает «фреймворк всё выводит из схемы», но связь Java↔SDL устанавливается аннотациями явно.
>
> **Если бы это было правдой:** Schema linker не мог бы валидировать, что для каждого поля `Subscription` есть резолвер, возвращающий `Flux`.
>
> ---
>
> #### D) В Spring GraphQL subscriptions ОБЯЗАТЕЛЬНО используют RSocket — WebSocket не поддерживается — ❌ Неверно
>
> **Что на самом деле:** Главный транспорт subscriptions — WebSocket по протоколу `graphql-ws`. RSocket — опциональный альтернативный транспорт (полезен для service-to-service, не для браузеров). SSE добавлен в 1.3+.
>
> **Откуда путаница:** Spring продвигает RSocket как часть реактивной экосистемы, и его упоминание в Spring GraphQL может создать впечатление обязательности.
>
> **Если бы это было правдой:** Невозможно было бы интегрировать Spring GraphQL subscriptions с Apollo Client/Relay — самыми популярными GraphQL-клиентами в JS-мире, которые работают по `graphql-ws`.
>
> ---
>
> **Связанные вопросы:** [[Q1]] — обзор Spring GraphQL и WebFlux, [[Q11]] — auth для WebSocket, [[Q15]] — мониторинг подписок.

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


> [!mcq]
>
> **Вопрос:** В REST вы возвращаете `HTTP 404` для не найденного заказа. В GraphQL фронтенд жалуется, что вместо `404` приходит `200 OK` с пустым `data`. Как правильно обрабатывать ошибки в Spring GraphQL?
>
> ---
>
> #### A) В GraphQL ошибки передаются через HTTP-статусы — настройте `@ResponseStatus(HttpStatus.NOT_FOUND)` на исключении и фронтенд увидит 404 — ❌ Неверно
>
> **Что на самом деле:** В GraphQL HTTP-статус почти всегда `200 OK`, даже при ошибках в резолвере. Ошибки передаются в JSON-поле `errors[]` вместе с (опциональным) `data`. Это фундаментальная часть спецификации: один запрос может вернуть и данные, и ошибки одновременно (партиальные ошибки в дереве).
>
> **Откуда путаница:** REST-привычки. `@ResponseStatus` не имеет эффекта в GraphQL-резолверах — Spring GraphQL не использует его.
>
> **Если бы это было правдой:** Не работали бы partial responses: один query с десятью полями, где одно недоступно — клиент получал бы 5xx и терял остальные 9 полей. В реальности GraphQL отдаст `data: {...9 полей...}` + `errors: [{path: [...10-е поле]}]`.
>
> ---
>
> #### B) Ошибки возвращаются в `errors[]`-массиве JSON-ответа; для маппинга exception → `GraphQLError` используется `DataFetcherExceptionResolver` (глобально) или `@GraphQlExceptionHandler` (в контроллере) с указанием `ErrorType` (NOT_FOUND, FORBIDDEN, BAD_REQUEST и т.д.) — ✓ Верно
>
> **Развёрнутое объяснение:** Spring GraphQL предоставляет два механизма обработки ошибок. **Глобально** — реализуйте `DataFetcherExceptionResolverAdapter` как `@Component`: его `resolveToSingleError(Throwable, DataFetchingEnvironment)` маппит конкретное исключение в `GraphQLError` с полем `errorType` (NOT_FOUND, FORBIDDEN, UNAUTHORIZED, BAD_REQUEST, INTERNAL_ERROR). **Локально** — Spring GraphQL 1.2+ поддерживает `@GraphQlExceptionHandler` внутри контроллера, аналогично `@ExceptionHandler` в MVC. Возврат `null` из resolver'а означает «передать дальше по цепочке». `path` в ошибке указывает на конкретное поле в дереве query — фронтенд показывает локальную ошибку, не ломая остальные поля.
>
> **Пример:**
> ```java
> @Component
> public class OrderExceptionResolver extends DataFetcherExceptionResolverAdapter {
>     @Override
>     protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
>         if (ex instanceof OrderNotFoundException e) {
>             return GraphQLError.newError()
>                 .errorType(ErrorType.NOT_FOUND)
>                 .message("Order not found: " + e.getId())
>                 .path(env.getExecutionStepInfo().getPath())
>                 .build();
>         }
>         return null; // передать default handler'у
>     }
> }
>
> // Альтернатива — handler в контроллере
> @Controller
> public class OrderController {
>     @GraphQlExceptionHandler
>     public GraphQLError handleNotFound(OrderNotFoundException ex) {
>         return GraphQLError.newError().errorType(ErrorType.NOT_FOUND)
>             .message(ex.getMessage()).build();
>     }
> }
> ```
>
> **Когда применять:** Все production-проекты. GitHub GraphQL API использует `errors[]` с типизированными codes (`UNPROCESSABLE`, `FORBIDDEN`); Shopify — собственные `userErrors` payload-типы для бизнес-ошибок (валидация формы), оставляя `errors[]` для технических сбоев.
>
> **Подводные камни:** Не выкидывайте детали стектрейса в `message` (security). Различайте «expected» бизнес-ошибки (валидация — в payload-тип `userErrors`) от unexpected (404, 500 — в `errors[]`). HTTP-статус `400 Bad Request` GraphQL возвращает только для синтаксически невалидного query (parse/validate ошибка); runtime-ошибки идут в 200 + errors[].
>
> ---
>
> #### C) В GraphQL ошибки нужно ВСЕГДА возвращать как payload-тип (`{ order, errors: [UserError!]! }`) — `errors[]` использовать запрещено — ❌ Неверно
>
> **Что на самом деле:** Это два разных уровня. `errors[]` — для технических ошибок (NOT_FOUND, network, security). Payload-тип `userErrors`/`userErrors` — для бизнес-валидации (email уже занят, недостаточно товара на складе). Обе техники легитимны и часто используются вместе (паттерн «Errors as Data» от Shopify).
>
> **Откуда путаница:** Доклад Shopify «Errors as Data» популяризировал payload-подход, и многие посчитали его единственно правильным.
>
> **Если бы это было правдой:** Невозможно было бы отделить «ваш запрос невалиден» от «такого ресурса нет» — оба попадали бы в одну корзину.
>
> ---
>
> #### D) `@GraphQlExceptionHandler` поддерживает только реактивный стек — для MVC нужен только `DataFetcherExceptionResolverAdapter` — ❌ Неверно
>
> **Что на самом деле:** Оба механизма работают в обоих стеках. `@GraphQlExceptionHandler` появился в Spring GraphQL 1.2 как удобный аналог `@ExceptionHandler` MVC и работает одинаково в Servlet и Reactive.
>
> **Откуда путаница:** Spring GraphQL изначально (1.0) поддерживал только `DataFetcherExceptionResolver`, аннотация добавлена позже.
>
> **Если бы это было правдой:** Документация Spring GraphQL содержала бы ограничение «только для WebFlux» — но оно отсутствует.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — `OrderNotFoundException` в Query resolver, [[Q11]] — `AccessDeniedException` от Spring Security, [[Q8]] — тестирование ошибок через `GraphQlTester`.

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


> [!mcq]
>
> **Вопрос:** Какой тестовый slice и инструмент Spring GraphQL даёт для unit-тестирования резолверов без полного контекста приложения?
>
> ---
>
> #### A) Использовать `@SpringBootTest` с реальным HTTP-клиентом (`TestRestTemplate`/`WebTestClient`), отправляя JSON `{"query": "..."}` через `POST /graphql` — ❌ Неверно (можно, но неоптимально)
>
> **Что на самом деле:** Это полноценный integration-тест, который запускает весь контекст и реальный HTTP-сервер. Долго (секунды на тест), хрупко, не даёт удобных DSL для проверки GraphQL-ответов (приходится парсить JSON руками). Spring предоставляет специализированный `@GraphQlTest` slice + `GraphQlTester` для этого.
>
> **Откуда путаница:** REST-привычка тестировать через HTTP. В GraphQL есть лучший инструмент.
>
> **Если бы это было правдой:** Не существовало бы `GraphQlTester` и `@GraphQlTest` — но они есть и являются основным способом тестирования.
>
> ---
>
> #### B) `@GraphQlTest(OrderController.class)` поднимает test slice только с GraphQL-инфраструктурой и указанными контроллерами; `GraphQlTester` — fluent DSL для query/mutation/subscription с JSON-path ассерциями — ✓ Верно
>
> **Развёрнутое объяснение:** `@GraphQlTest` — это test slice аналогичный `@WebMvcTest`. Поднимает только GraphQL-bean'ы (схема, резолверы, instrumentation, exception resolvers), без полного контекста. `GraphQlTester` — fluent API: `tester.document("...")` для inline-query, `tester.documentName("getOrder")` для загрузки из `src/test/resources/graphql-test/getOrder.graphql`, `.variable(...)` для переменных, `.execute()` запускает, `.path("...").entity(Class).isEqualTo(...)` — ассерции по полям результата. Для проверки ошибок — `.errors().expect(predicate)`. `HttpGraphQlTester` — вариант для интеграционных тестов через реальный HTTP. `WebSocketGraphQlTester` — для subscriptions. Зависимости (репозитории, сервисы) мокаются через `@MockBean`.
>
> **Пример:**
> ```java
> @GraphQlTest(OrderController.class)
> class OrderControllerTest {
>     @Autowired private GraphQlTester tester;
>     @MockBean private OrderService orderService;
>
>     @Test
>     void shouldReturnOrder() {
>         when(orderService.findById("123")).thenReturn(
>             Optional.of(new Order("123", "customer-1",
>                 new BigDecimal("99.99"), OrderStatus.PENDING)));
>
>         tester.documentName("getOrder")  // src/test/resources/graphql-test/getOrder.graphql
>             .variable("id", "123")
>             .execute()
>             .path("order.id").entity(String.class).isEqualTo("123")
>             .path("order.status").entity(String.class).isEqualTo("PENDING");
>     }
>
>     @Test
>     void shouldReturnErrorForMissingOrder() {
>         when(orderService.findById("999")).thenReturn(Optional.empty());
>         tester.document("{ order(id: \"999\") { id } }")
>             .execute()
>             .errors()
>             .expect(e -> e.getErrorType() == ErrorType.NOT_FOUND);
>     }
> }
> ```
>
> **Когда применять:** Любой Spring GraphQL проект. Документы храните в `src/test/resources/graphql-test/` — это конвенция и идеально для code review. Для контракт-тестов фронта и бэка можно поделиться `.graphql`-файлами между проектами.
>
> **Подводные камни:** `@GraphQlTest` не поднимает Spring Security автоматически — нужно явно `@Import(SecurityConfig.class)` или `@MockBean SecurityFilterChain`. Не поднимает JPA — нужны мок сервисов. Документы по умолчанию ищутся в `src/test/resources/graphql-test/<name>.graphql`.
>
> ---
>
> #### C) Достаточно стандартного `@WebMvcTest` — GraphQL endpoint работает через тот же `DispatcherServlet` — ❌ Неверно
>
> **Что на самом деле:** `@WebMvcTest` не поднимает GraphQL-инфраструктуру (`GraphQlSource`, `SchemaResource`). Тест упадёт с `No GraphQlTester bean`. `@GraphQlTest` — специально созданный slice с правильным набором auto-configurations.
>
> **Откуда путаница:** Spring MVC-привычки. Хотя endpoint `/graphql` действительно проходит через `DispatcherServlet`, инфраструктура для GraphQL отдельная.
>
> **Если бы это было правдой:** Не было бы отдельной аннотации в Spring Boot.
>
> ---
>
> #### D) Тестировать резолверы можно только через GraphQL Java напрямую: создать `GraphQL` объект и вызвать `.execute(query)` — ❌ Неверно (слишком низкоуровнево)
>
> **Что на самом деле:** Так можно, но это потеря всех Spring-специфичных моментов: `@Argument` binding, `@SchemaMapping` discovery, security filter chain, exception resolvers. `GraphQlTester` интегрирует это всё.
>
> **Откуда путаница:** Опыт работы с голым GraphQL Java до Spring GraphQL. Тогда это был единственный способ.
>
> **Если бы это было правдой:** Spring GraphQL не предоставлял бы testing-модуль — но он предоставляет, см. `spring-graphql-test`.
>
> ---
>
> **Связанные вопросы:** [[Q7]] — error testing через `.errors()`, [[Q11]] — Security в тестах, [[Q15]] — мониторинг как альтернатива тестам в production.

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


> [!mcq]
>
> **Вопрос:** В схеме объявлена кастомная директива `directive @auth(role: String!) on FIELD_DEFINITION`. На уровне Spring GraphQL для её обработки нужно реализовать какой компонент и как он подключается?
>
> ---
>
> #### A) Достаточно поставить аннотацию `@AuthDirective` на резолвер — Spring сам свяжет её с SDL-директивой по имени — ❌ Неверно
>
> **Что на самом деле:** SDL-директивы и Java-аннотации — разные миры. Связать их можно только через `SchemaDirectiveWiring`, который оборачивает `DataFetcher` поля декорированным поведением. Никакого автоматического mapping по имени не существует.
>
> **Откуда путаница:** В Spring MVC аннотация и runtime-обработчик связаны через `BeanPostProcessor`. Хочется такого же магического связывания, но в GraphQL дизайн другой.
>
> **Если бы это было правдой:** Не было бы необходимости в `SchemaDirectiveWiring` интерфейсе — но это центральный API GraphQL Java для директив.
>
> ---
>
> #### B) Реализовать `SchemaDirectiveWiring` — `onField()` оборачивает оригинальный `DataFetcher` декоратором, который выполняется при вызове поля; компонент регистрируется через `RuntimeWiringConfigurer` — ✓ Верно
>
> **Развёрнутое объяснение:** `SchemaDirectiveWiring` — стандартный интерфейс GraphQL Java для обработки директив. Метод `onField(SchemaDirectiveWiringEnvironment)` вызывается ОДИН раз при построении схемы для каждого поля с этой директивой. Внутри вы получаете оригинальный `DataFetcher` (`env.getFieldDataFetcher()`) и оборачиваете его в декоратор, который выполняется при каждом GraphQL-запросе. Регистрация в Spring GraphQL делается через `RuntimeWiringConfigurer` bean — Spring передаст его в строитель схемы. Директивы бывают двух родов: **schema directives** (применяются при построении схемы, например `@auth`, `@deprecated`) и **query directives** (применяются клиентом в запросе, например `@include`, `@skip`).
>
> **Пример:**
> ```graphql
> directive @auth(role: String!) on FIELD_DEFINITION
>
> type Query {
>     adminReport: Report! @auth(role: "ADMIN")
> }
> ```
> ```java
> @Component
> public class AuthDirectiveWiring implements SchemaDirectiveWiring {
>     @Override
>     public GraphQLFieldDefinition onField(
>             SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
>         String requiredRole = (String) env.getAppliedDirective("auth")
>             .getArgument("role").getValue();
>         DataFetcher<?> original = env.getFieldDataFetcher();
>         DataFetcher<?> authFetcher = ctx -> {
>             Authentication auth = SecurityContextHolder.getContext().getAuthentication();
>             boolean ok = auth.getAuthorities().stream()
>                 .anyMatch(a -> a.getAuthority().equals("ROLE_" + requiredRole));
>             if (!ok) throw new AccessDeniedException("Required role: " + requiredRole);
>             return original.get(ctx);
>         };
>         return env.setFieldDataFetcher(authFetcher);
>     }
> }
>
> @Configuration
> class GraphQlConfig {
>     @Bean
>     RuntimeWiringConfigurer wiringConfigurer(AuthDirectiveWiring authWiring) {
>         return wiring -> wiring.directive("auth", authWiring);
>     }
> }
> ```
>
> **Когда применять:** Cross-cutting concerns на уровне SDL: авторизация (`@auth`), кэширование (`@cacheControl`), форматирование (`@uppercase`), маскирование (`@masked` для PII), rate-limiting (`@rateLimit`). Apollo Federation использует директивы (`@key`, `@external`) для определения federated schema.
>
> **Подводные камни:** `onField()` вызывается при старте, не на каждый запрос — там нельзя ссылаться на текущий security context. Декоратор `DataFetcher` — это место, где есть доступ к runtime-контексту. Альтернатива в Spring — `@PreAuthorize` на резолвере (проще, но без декларации в SDL).
>
> ---
>
> #### C) Директивы можно использовать только встроенные (`@deprecated`, `@include`, `@skip`) — кастомные не поддерживаются Spring GraphQL — ❌ Неверно
>
> **Что на самом деле:** Spring GraphQL полностью поддерживает кастомные директивы через `SchemaDirectiveWiring` (это API GraphQL Java, на котором Spring построен). Кастомные директивы — стандартная часть GraphQL spec.
>
> **Откуда путаница:** Документация Spring GraphQL фокусируется на основных кейсах и редко показывает примеры с директивами — кажется, что их «нет».
>
> **Если бы это было правдой:** Невозможно было бы построить Apollo Federation на Spring GraphQL — но Netflix DGS и сам Spring GraphQL поддерживают её.
>
> ---
>
> #### D) Директивы обрабатываются через GraphQL `Instrumentation`, регистрируемую как `@Bean` — ❌ Неверно
>
> **Что на самом деле:** `Instrumentation` — это hook для перехвата ЭТАПОВ выполнения query (parse, validate, execute, fetch field). Она знает «было ли поле запрошено», но не понимает SDL-директивы напрямую. Для директив нужен `SchemaDirectiveWiring`. Instrumentation полезна для логирования, метрик, query complexity — Q15 как раз о ней.
>
> **Откуда путаница:** Оба механизма про cross-cutting concerns, и легко перепутать.
>
> **Если бы это было правдой:** GraphQL Java не имел бы отдельного API для директив.
>
> ---
>
> **Связанные вопросы:** [[Q11]] — auth через `@PreAuthorize` vs `@auth` directive, [[Q13]] — `@deprecated` для introspection, [[Q15]] — Instrumentation.

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


> [!mcq]
>
> **Вопрос:** Для списка заказов нужно реализовать пагинацию по 20 элементов с возможностью бесконечного скролла. Какой стиль пагинации в GraphQL рекомендован спецификацией Relay и почему cursor-based лучше offset-based для production?
>
> ---
>
> #### A) Offset-based (`orders(limit: 20, offset: 40)`) — самый простой и подходит для любого размера данных — ❌ Неверно
>
> **Что на самом деле:** Offset работает только на маленьких/статичных датасетах. На большом наборе `OFFSET 100000` означает «прочитать 100000 строк и выбросить» — линейная деградация. При добавлении новых записей в начало (новый заказ) страница «сдвигается» — пользователь видит дубли или пропуски при скролле.
>
> **Откуда путаница:** Привычка SQL-пагинации с `LIMIT/OFFSET`. Работает для админ-панели на 1000 записей; ломается на бесконечном скролле с миллионами.
>
> **Если бы это было правдой:** Никто не разработал бы Relay Connection spec — но он существует именно для решения проблем offset.
>
> ---
>
> #### B) Cursor-based по спецификации Relay Connection (`first`, `after`, `last`, `before` + `Connection`/`Edge`/`PageInfo` типы); Spring GraphQL поддерживает её через `ScrollPosition` и `Window` — ✓ Верно
>
> **Развёрнутое объяснение:** Cursor — это opaque-токен (обычно base64-encoded id или составной ключ), указывающий позицию в наборе данных. Запрос `orders(first: 20, after: "cursor123")` означает «20 элементов СТРОГО ПОСЛЕ позиции cursor123». Стабильно к вставкам/удалениям, не требует считывать пропущенные строки в БД (можно сделать `WHERE id > cursor LIMIT 20` с индексом). Relay-спецификация требует обёртки в `Connection` тип с `edges[{ node, cursor }]` и `pageInfo { hasNextPage, hasPreviousPage, startCursor, endCursor }`. Spring GraphQL 1.2+ имеет первоклассную поддержку: возвращайте `Window<T>` из Spring Data Commons, и `DefaultConnection.create(window, CursorStrategy)` обернёт в Relay-формат.
>
> **Пример:**
> ```graphql
> type Query {
>     orders(first: Int, after: String, last: Int, before: String): OrderConnection!
> }
> type OrderConnection {
>     edges: [OrderEdge!]!
>     pageInfo: PageInfo!
>     totalCount: Int!
> }
> type OrderEdge { node: Order!  cursor: String! }
> type PageInfo {
>     hasNextPage: Boolean!  hasPreviousPage: Boolean!
>     startCursor: String   endCursor: String
> }
> ```
> ```java
> @QueryMapping
> public Connection<Order> orders(@Argument int first, @Argument String after) {
>     ScrollPosition position = after != null
>         ? ScrollPosition.forward(CursorEncoder.decode(after))
>         : ScrollPosition.keyset();
>     Window<Order> window = orderService.findPage(first, position);
>     return DefaultConnection.create(window,
>         CursorStrategy.withEncoder(CursorEncoder.base64()));
> }
> ```
>
> **Когда применять:** Любая публичная схема (GitHub, Shopify, Facebook GraphQL APIs все используют Relay Connection). Mobile-приложения с infinite scroll. Аналитические дашборды.
>
> **Подводные камни:** `totalCount` бывает дорогим (`SELECT COUNT(*)` на большой таблице — full scan); часто его вычисляют отдельно или приближённо (`estimated_count`). Cursor должен быть стабилен к ORDER BY — если сортируете по `updatedAt`, в cursor нужны и `updatedAt`, и `id` (для tiebreaker). Не путайте cursor с offset — раскрытие сырых offset в cursor сводит к нулю преимущество.
>
> ---
>
> #### C) Page-based (`orders(page: 3, size: 20)`) — Spring Data-style, идеально интегрируется с `Pageable` — ❌ Неверно
>
> **Что на самом деле:** Это лишь обёртка над offset (`offset = page * size`) и имеет все его проблемы. Хотя Spring GraphQL может маппить на `Pageable`, для production-схем GraphQL это не рекомендуется.
>
> **Откуда путаница:** Spring Data привычки. В REST это работает; в GraphQL с быстрорастущими данными — нет.
>
> **Если бы это было правдой:** Apollo Client не выпускал бы специальный helper `relayStylePagination` — но это его рекомендованный паттерн.
>
> ---
>
> #### D) Spring GraphQL не поддерживает Relay-пагинацию из коробки — приходится писать `Connection`-тип руками — ❌ Неверно (для 1.2+)
>
> **Что на самом деле:** Spring GraphQL 1.2+ поставляет `org.springframework.graphql.data.pagination.*` — встроенную поддержку через `Window<T>`, `ScrollPosition`, `CursorStrategy`. До 1.2 действительно приходилось вручную, теперь — нет.
>
> **Откуда путаница:** Старые tutorials написаны под 1.0/1.1, и часть Stack Overflow ответов устарели.
>
> **Если бы это было правдой:** В docs Spring GraphQL не было бы раздела «Pagination» — но он есть.
>
> ---
>
> **Связанные вопросы:** [[Q5]] — `@BatchMapping` для items, [[Q13]] — introspection и поля Connection, [[Q15]] — мониторинг slow queries.

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


> [!mcq]
>
> **Вопрос:** Аутентификация JWT уже настроена в `SecurityFilterChain` для REST. GraphQL endpoint `/graphql` должен использовать ту же auth. Что нужно сделать для корректной работы?
>
> ---
>
> #### A) Нужно отключить Spring Security для `/graphql` и реализовать собственную аутентификацию через `Instrumentation` — ❌ Неверно
>
> **Что на самом деле:** `/graphql` — обычный HTTP POST endpoint, к нему применяется стандартный `SecurityFilterChain`. Никакой специальной аутентификации не требуется — Spring Security валидирует JWT до того, как запрос дойдёт до GraphQL-резолверов.
>
> **Откуда путаница:** Мысль «GraphQL = специальный мир, нужны специальные механизмы». На самом деле для HTTP-транспорта всё работает прозрачно. Специфика возникает только для WebSocket (subscriptions) — там auth идёт в `connection_init`-сообщении.
>
> **Если бы это было правдой:** Spring GraphQL имел бы свой security-модуль — но он использует Spring Security без модификаций.
>
> ---
>
> #### B) Spring Security интегрируется out-of-the-box: `SecurityContextHolder` доступен в резолверах, `@PreAuthorize`/`@PostAuthorize` работают на методах `@QueryMapping`/`@MutationMapping`, `Principal` инжектится как параметр — ✓ Верно
>
> **Развёрнутое объяснение:** Spring GraphQL построен поверх Spring MVC/WebFlux, поэтому Security Filter Chain отрабатывает до резолверов. `Principal`, `Authentication` инжектятся в параметры методов автоматически (Spring GraphQL resolver argument resolvers). Method-level security (`@EnableMethodSecurity`) с `@PreAuthorize`/`@PostAuthorize` навешивается на резолверы как на обычные Spring-бины. Для WebFlux используйте `ReactiveSecurityContextHolder` и `Mono<Authentication>`. Важная деталь: при `AccessDeniedException` Spring GraphQL не вернёт HTTP 403 — он попадёт в `errors[]` через `DataFetcherExceptionResolver` с `ErrorType.FORBIDDEN` (см. Q7).
>
> **Пример:**
> ```java
> @Configuration
> @EnableMethodSecurity
> class SecurityConfig {
>     @Bean
>     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
>         return http
>             .authorizeHttpRequests(a -> a
>                 .requestMatchers("/graphql").authenticated()
>                 .anyRequest().permitAll())
>             .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
>             .build();
>     }
> }
>
> @Controller
> public class OrderController {
>     @QueryMapping
>     @PreAuthorize("isAuthenticated()")
>     public List<Order> myOrders(Principal principal) {
>         return orderService.findByCustomer(principal.getName());
>     }
>
>     @MutationMapping
>     @PreAuthorize("hasRole('ADMIN')")
>     public void deleteOrder(@Argument String id) {
>         orderService.delete(id);
>     }
> }
> ```
>
> **Когда применять:** Любой production GraphQL API. Для WebSocket subscriptions добавьте `WebSocketGraphQlInterceptor`, чтобы валидировать token из `connection_init` и пробрасывать `SecurityContext` в `Flux`.
>
> **Подводные камни:** `@PreAuthorize` на `@SchemaMapping` для вложенного поля будет выполняться ДЛЯ КАЖДОГО элемента — может сильно замедлить large lists. Лучше проверять авторизацию в root-резолвере. Subscriptions через WebSocket — `SecurityContextHolder` (ThreadLocal) НЕ работает в `Flux`-цепочке: используйте `ReactiveSecurityContextHolder` или явно пробрасывайте Authentication через context.
>
> ---
>
> #### C) Spring Security несовместим со Spring GraphQL — нужно использовать GraphQL-нативные библиотеки (`graphql-java-tools-security`) — ❌ Неверно
>
> **Что на самом деле:** Spring GraphQL специально интегрирован со Spring Security; в `spring-graphql` есть классы `SecurityContextThreadLocalAccessor` для пробрасывания контекста в реактив. Сторонние библиотеки не нужны.
>
> **Откуда путаница:** В Apollo Server (Node.js) популярны context-based решения; перенос мышления приводит к ложному выводу.
>
> **Если бы это было правдой:** Не было бы документации Spring GraphQL «Security» — но она есть.
>
> ---
>
> #### D) Авторизация в GraphQL возможна только через директивы `@auth` в SDL — Spring Method Security игнорируется — ❌ Неверно
>
> **Что на самом деле:** Оба подхода легитимны. Method Security (`@PreAuthorize`) — рекомендованный default. Directive-based — для декларативной авторизации в SDL (см. Q9). Они даже комбинируются.
>
> **Откуда путаница:** Доклады по GraphQL-only авторизации (например, Hasura) убеждают, что SDL — единственное правильное место. В Spring-мире — нет.
>
> **Если бы это было правдой:** В тестах нельзя было бы мокать security с `@WithMockUser` — но это работает.
>
> ---
>
> **Связанные вопросы:** [[Q7]] — `AccessDeniedException` → `ErrorType.FORBIDDEN`, [[Q9]] — `@auth` directive, [[Q6]] — auth для WebSocket subscriptions.

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


> [!mcq]
>
> **Вопрос:** Метод `customer(Order order)` помечен `@SchemaMapping(typeName="Order", field="customer")`. Если убрать аннотацию `@SchemaMapping` и оставить `@QueryMapping`, что произойдёт?
>
> ---
>
> #### A) Ничего не изменится — Spring угадывает тип по сигнатуре метода — ❌ Неверно
>
> **Что на самом деле:** `@QueryMapping` строго маппится на `type Query`, не на вложенные типы. С `@QueryMapping public Customer customer(Order order)` Spring попытается найти поле `customer` в корневом `type Query` — оно не существует, контекст упадёт при schema-mapping validation или вернёт `null` для query.
>
> **Откуда путаница:** Spring славится «умной» autodetection — кажется, что и здесь магия. Но `typeName` — фундаментальная информация, её нельзя угадать.
>
> **Если бы это было правдой:** Не было бы смысла в существовании отдельной аннотации `@SchemaMapping`.
>
> ---
>
> #### B) `@QueryMapping` и `@MutationMapping`/`@SubscriptionMapping` — синтаксические алиасы `@SchemaMapping` с фиксированным `typeName` ("Query"/"Mutation"/"Subscription"); для вложенных типов нужен только `@SchemaMapping` — ✓ Верно
>
> **Развёрнутое объяснение:** Аннотации `@QueryMapping`/`@MutationMapping`/`@SubscriptionMapping` — это `@SchemaMapping`, с pre-заданным `typeName`. Их единственная цель — читаемость кода: «эта функция — Query root». Для всего остального (вложенные resolver'ы, federation, custom types) нужен `@SchemaMapping(typeName="...")`. `field` можно опустить, если имя метода совпадает с полем в SDL. `typeName` можно опустить, если родительский тип однозначно выводится из первого аргумента (например, метод принимает `Order` → typeName="Order"). Это объясняет, почему `@SchemaMapping public Customer customer(Order order)` работает без аргументов.
>
> **Пример:**
> ```java
> // Все четыре эквивалентны
> @QueryMapping public Order order(@Argument String id) { ... }
> @SchemaMapping(typeName = "Query") public Order order(@Argument String id) { ... }
> @SchemaMapping(typeName = "Query", field = "order") public Order findOrder(@Argument String id) { ... }
>
> // Для Mutation
> @MutationMapping public Order createOrder(@Argument CreateOrderInput input) { ... }
> @SchemaMapping(typeName = "Mutation") public Order createOrder(@Argument CreateOrderInput input) { ... }
>
> // Для Subscription
> @SubscriptionMapping public Flux<Order> orderStatusChanged(@Argument String orderId) { ... }
>
> // Для вложенного поля — только @SchemaMapping
> @SchemaMapping(typeName = "Order", field = "customer")
> public Customer customer(Order order) { ... }
>
> // typeName выводится из аргумента
> @SchemaMapping public Customer customer(Order order) { ... }  // typeName="Order"
> ```
>
> **Когда применять:** Используйте специализированные аннотации для root-операций (читаемее в code review), `@SchemaMapping` — для вложенных полей. Если в одном контроллере смешиваются разные `typeName`, лучше явно указывать.
>
> **Подводные камни:** Если в SDL есть несколько `type X` (например, унаследованных через interfaces), нужно явно указать `typeName`. Имя поля в SDL должно совпадать с именем метода — иначе Spring ругнётся `No DataFetcher for field`. Spring GraphQL логирует все обнаруженные маппинги при старте с `spring.graphql.schema.printer.enabled=true`.
>
> ---
>
> #### C) `@MutationMapping` отличается от `@SchemaMapping(typeName="Mutation")` тем, что включает транзакционность — ❌ Неверно
>
> **Что на самом деле:** Транзакции в Spring управляются `@Transactional`, не GraphQL-аннотациями. `@MutationMapping` — чистый sugar, никакой transactional семантики не добавляет. Хотя GraphQL-spec гарантирует серийное выполнение mutations, это не транзакция БД.
>
> **Откуда путаница:** Слово «mutation» наводит на «изменение состояния» → транзакция. Но связь только семантическая.
>
> **Если бы это было правдой:** Нужно было бы конфигурировать transaction manager через GraphQL-настройки — этого нет.
>
> ---
>
> #### D) `@SchemaMapping` работает только в WebFlux, в MVC доступны только `@QueryMapping`/`@MutationMapping` — ❌ Неверно
>
> **Что на самом деле:** Все четыре аннотации работают одинаково в обоих стеках. Различия касаются типа возврата (`Mono`/`Flux` vs обычные типы) и thread-context propagation, не аннотаций.
>
> **Откуда путаница:** Reactive-tutorials часто показывают `@SchemaMapping` с `Mono` примерами, создавая ощущение привязки.
>
> **Если бы это было правдой:** Документация Spring GraphQL имела бы оговорку — но её нет.
>
> ---
>
> **Связанные вопросы:** [[Q3]] — Query resolver pattern, [[Q4]] — Mutation pattern, [[Q5]] — `@BatchMapping` для оптимизации `@SchemaMapping`.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как обрабатывать файловый upload в Spring GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Как отлаживать и мониторить GraphQL-запросы? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [GraphQL](../../api/graphql-interview.md) — основы GraphQL (schema, queries, resolvers, N+1 проблема) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек для GraphQL subscriptions
- [Spring Boot](spring-boot-interview.md) — auto-configuration, starter dependencies
- [Spring REST Clients](spring-rest-client-interview.md) — REST как альтернатива GraphQL
- [Spring Security](spring-security-interview.md) — интеграция безопасности с GraphQL
- [Spring Testing](spring-testing-interview.md) — GraphQlTester для тестирования
- [Spring Data JPA](spring-data-jpa-interview.md) — репозитории, используемые в resolvers
- [Micrometer](../../monitoring/micrometer-interview.md) — метрики graphql.* через Actuator
- [HTTP & REST](../../api/http-rest-interview.md) — REST-обзор для сравнения
- [OpenAPI/Swagger](../../api/openapi-swagger-interview.md) — документирование REST в противовес GraphQL introspection
