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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Как реализовать GraphQL Subscriptions? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как обрабатывать ошибки в Spring GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как тестировать Spring GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Что такое GraphQL Directives и как их использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как реализовать пагинацию в GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как Spring GraphQL интегрируется с Spring Security? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Чем @SchemaMapping отличается от @QueryMapping и @MutationMapping? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Как работает introspection и когда его отключать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
