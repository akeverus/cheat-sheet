---
title: "Вопросы на собеседовании: GraphQL"
description: "Вопросы и ответы по GraphQL для Java-разработчика: схема, типы, запросы, мутации, подписки, резолверы, DataLoader, пагинация, безопасность, федерация, Spring for GraphQL."
tags:
  - interview
  - api
  - graphql-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "GraphQL"
  - "GraphQL interview"
  - "GraphQL собеседование"
prerequisites:
  - "[[graphql]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `GraphQL`

Практичные вопросы и ответы по `GraphQL`: от базовых концепций схемы и типов до продвинутых тем -- `DataLoader`, пагинация, федерация, безопасность и интеграция с `Spring Boot`.

## Полезные ссылки

### Официальная документация

- [GraphQL — Official Docs](https://graphql.org/learn/) — основная документация по спецификации
- [GraphQL Specification](https://spec.graphql.org/draft/) — актуальная спецификация
- [Spring for GraphQL Reference](https://docs.spring.io/spring-graphql/reference/) — Spring-интеграция
- [Getting Started with GraphQL and Spring Boot — Baeldung](https://www.baeldung.com/spring-graphql) — пошаговое руководство
- [Introduction to GraphQL — Baeldung](https://www.baeldung.com/graphql) — обзорная статья
- [GraphQL vs REST — Baeldung](https://www.baeldung.com/graphql-vs-rest) — сравнение подходов
- [Error Handling in GraphQL — Baeldung](https://www.baeldung.com/spring-graphql-error-handling) — обработка ошибок
- [Pagination in Spring Boot GraphQL — Baeldung](https://www.baeldung.com/spring-boot-graphql-pagination) — пагинация

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы GraphQL**
- [Q1. (!) Что такое GraphQL и чем он отличается от REST?](#q1-что-такое-graphql-и-чем-он-отличается-от-rest)
- [Q2. Что такое Schema Definition Language (SDL)?](#q2-что-такое-schema-definition-language-sdl)
- [Q3. (!) Какие типы данных существуют в GraphQL?](#q3-какие-типы-данных-существуют-в-graphql)
- [Q4. Что такое Input-типы и зачем они нужны?](#q4-что-такое-input-типы-и-зачем-они-нужны)

**Запросы, мутации и подписки**
- [Q5. (!) Как устроены Query, Mutation и Subscription?](#q5-как-устроены-query-mutation-и-subscription)
- [Q6. Что такое переменные и директивы в GraphQL?](#q6-что-такое-переменные-и-директивы-в-graphql)
- [Q7. Что такое фрагменты и зачем они нужны?](#q7-что-такое-фрагменты-и-зачем-они-нужны)
- [Q8. Как работают Inline Fragments и Union-типы?](#q8-как-работают-inline-fragments-и-union-типы)

**Резолверы и выполнение запросов**
- [Q9. (!) Что такое резолверы и как они работают?](#q9-что-такое-резолверы-и-как-они-работают)
- [Q10. Как GraphQL выполняет запрос (execution model)?](#q10-как-graphql-выполняет-запрос-execution-model)
- [Q11. (!) Что такое проблема N+1 в GraphQL и как её решить?](#q11-что-такое-проблема-n1-в-graphql-и-как-её-решить)
- [Q12. Как работает DataLoader?](#q12-как-работает-dataloader)

**Пагинация**
- [Q13. (!) Какие подходы к пагинации существуют в GraphQL?](#q13-какие-подходы-к-пагинации-существуют-в-graphql)
- [Q14. Что такое Relay-спецификация для пагинации (Connections)?](#q14-что-такое-relay-спецификация-для-пагинации-connections)

**Обработка ошибок и интроспекция**
- [Q15. Как устроена обработка ошибок в GraphQL?](#q15-как-устроена-обработка-ошибок-в-graphql)
- [Q16. Что такое интроспекция и зачем она нужна?](#q16-что-такое-интроспекция-и-зачем-она-нужна)

**GraphQL vs REST**
- [Q17. (!) Когда выбирать GraphQL, а когда REST?](#q17-когда-выбирать-graphql-а-когда-rest)
- [Q18. Какие основные проблемы REST решает GraphQL?](#q18-какие-основные-проблемы-rest-решает-graphql)

**Spring for GraphQL**
- [Q19. (!) Как работает Spring for GraphQL?](#q19-как-работает-spring-for-graphql)
- [Q20. Как определить контроллер в Spring for GraphQL?](#q20-как-определить-контроллер-в-spring-for-graphql)
- [Q21. Как подключить DataLoader в Spring for GraphQL?](#q21-как-подключить-dataloader-в-spring-for-graphql)

**Безопасность**
- [Q22. (!) Какие угрозы безопасности специфичны для GraphQL?](#q22-какие-угрозы-безопасности-специфичны-для-graphql)
- [Q23. Как ограничить глубину и сложность запросов?](#q23-как-ограничить-глубину-и-сложность-запросов)

**Кэширование и производительность**
- [Q24. Почему кэширование в GraphQL сложнее, чем в REST?](#q24-почему-кэширование-в-graphql-сложнее-чем-в-rest)
- [Q25. Что такое Persisted Queries?](#q25-что-такое-persisted-queries)

**Масштабирование и архитектура**
- [Q26. (!) Что такое GraphQL Federation?](#q26-что-такое-graphql-federation)
- [Q27. Чем Federation отличается от Schema Stitching?](#q27-чем-federation-отличается-от-schema-stitching)
- [Q28. Schema-first vs Code-first: в чём разница?](#q28-schema-first-vs-code-first-в-чём-разница)

**Продвинутые темы**
- [Q29. Как реализовать загрузку файлов в GraphQL?](#q29-как-реализовать-загрузку-файлов-в-graphql)
- [Q30. (!) Как организовать батчинг запросов?](#q30-как-организовать-батчинг-запросов)

**Продвинутые темы**
- [Q31. (!) Как работают GraphQL Subscriptions и как их реализовать в Spring?](#q31--как-работают-graphql-subscriptions-и-как-их-реализовать-в-spring)
- [Q32. Что такое @defer и @stream директивы в GraphQL?](#q32-что-такое-defer-и-stream-директивы-в-graphql)
- [Q33. (!) Как решить проблему N+1 с помощью DataLoader в Spring for GraphQL?](#q33--как-решить-проблему-n1-с-помощью-dataloader-в-spring-for-graphql)

**Дополнительные темы**
- [Q34. Что такое Persisted Queries и зачем они нужны?](#q34-что-такое-persisted-queries-и-зачем-они-нужны)
- [Q35. Как масштабировать GraphQL Subscriptions через WebSocket?](#q35-как-масштабировать-graphql-subscriptions-через-websocket)
- [Q36. Чем Schema Stitching отличается от Federation и почему Federation предпочтительнее?](#q36-чем-schema-stitching-отличается-от-federation-и-почему-federation-предпочтительнее)
- [Q37. Как реализовать rate limiting в GraphQL через ограничение сложности запроса?](#q37-как-реализовать-rate-limiting-в-graphql-через-ограничение-сложности-запроса)
- [Q38. Как устроена обработка ошибок в GraphQL: partial responses и поле errors?](#q38-как-устроена-обработка-ошибок-в-graphql-partial-responses-и-поле-errors)
- [Q39. (!) Как работает Apollo Federation 2 и что такое subgraph schemas?](#q39--как-работает-apollo-federation-2-и-что-такое-subgraph-schemas)
- [Q40. GraphQL vs REST vs gRPC: когда что выбирать?](#q40-graphql-vs-rest-vs-grpc-когда-что-выбирать)

---

## Q1. (!) Что такое GraphQL и чем он отличается от REST?

**`GraphQL`** -- это язык запросов для API и среда выполнения этих запросов, разработанная Facebook в 2012 году и опубликованная в 2015 году. В отличие от `REST`, клиент сам определяет, какие данные ему нужны.

**Ключевые отличия:**

| Критерий | REST | GraphQL |
|----------|------|---------|
| Эндпоинт | Множество (`/users`, `/posts`) | Один (`/graphql`) |
| Формат ответа | Фиксирован сервером | Определяется клиентом |
| Over-fetching | Часто -- сервер возвращает всё | Нет -- клиент берёт только нужное |
| Under-fetching | Часто -- нужно несколько запросов | Нет -- один запрос на граф данных |
| Версионирование | Через URL или заголовки | Эволюция схемы без версий |
| Кэширование | Простое (HTTP-кэш по URL) | Сложное (нужны спец. решения) |
| Типизация | Нет стандарта (OpenAPI опционально) | Строгая типизация через схему |

```mermaid
graph LR
    subgraph REST
        C1[Client] -->|GET /users/1| S1[Server]
        C1 -->|GET /users/1/posts| S1
        C1 -->|GET /users/1/followers| S1
    end

    subgraph GraphQL
        C2[Client] -->|POST /graphql<br/>query user, posts, followers| S2[Server]
    end
```

**Что хочет услышать интервьюер:** GraphQL решает проблемы over-fetching и under-fetching, но вводит собственные сложности -- кэширование, безопасность запросов, learning curve. Это не замена REST, а альтернативный подход, оптимальный для графовых данных и разнородных клиентов (мобильные, веб, IoT).

---


> [!mcq]
> **Вопрос:** В чём ключевое архитектурное отличие GraphQL от REST с точки зрения формирования ответа сервера?
>
> - [x] **A) Клиент в теле запроса декларативно указывает нужные поля, и сервер возвращает ровно эту проекцию — устраняя over-fetching и under-fetching за один round-trip**
>
>     ✓ ПОЧЕМУ ВЕРНО: это и есть фундаментальная идея GraphQL — shape ответа определяется запросом клиента, а не контрактом эндпоинта.
>
>     МЕХАНИЗМ: запрос приходит на единственный `POST /graphql`, парсится в AST, валидируется по schema, и executor вызывает резолверы только для запрошенных полей.
>
>     ```graphql
>     query { user(id: "1") { name posts { title } } }
>     ```
>
>     ```java
>     @SchemaMapping(typeName = "Query")
>     public User user(@Argument String id) {
>         return userRepo.findById(id).orElseThrow();
>     }
>     ```
>
>     ПРИМЕНИМОСТЬ: мобильные клиенты с тонким каналом, BFF-агрегация разнородных бэкендов, экраны со сложными зависимыми данными.
>
> - [ ] **B) GraphQL полностью заменяет REST во всех сценариях и всегда быстрее**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: это не замена, а альтернатива; для file upload, простого CRUD под HTTP-кэш и public webhooks REST проще и эффективнее.
>
>     ПОСЛЕДСТВИЕ: команда выбирает GraphQL «потому что модно», получает лишний tooling, проблемы с кэшированием на CDN, security overhead (depth/complexity limiting) — и теряет скорость доставки.
>
> - [ ] **C) GraphQL обязательно использует WebSocket вместо HTTP для всех операций**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: Query и Mutation работают по обычному `HTTP POST`; WebSocket/SSE нужен только для Subscriptions.
>
>     ПОСЛЕДСТВИЕ: инфраструктура поднимает WebSocket-шлюз там, где достаточно `POST /graphql` через обычный Ingress; усложняется балансировка, sticky sessions, наблюдаемость.
>
> - [ ] **D) GraphQL кэшируется HTTP-кэшем (Cache-Control + URL) так же прозрачно, как REST**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: все запросы идут `POST` на один URL — HTTP-кэш по URL/методу бесполезен; нужны persisted queries + GET или клиентский cache (Apollo, Relay) по normalized entities.
>
>     ПОСЛЕДСТВИЕ: ожидание «CDN сам закэширует» приводит к пустому hit-rate, нагрузка падает на origin, latency растёт.

## Q2. Что такое Schema Definition Language (SDL)?

**`SDL`** (Schema Definition Language) -- язык описания схемы `GraphQL`. Схема является контрактом между клиентом и сервером, определяя типы данных, операции и их связи.

```graphql
type Author {
    id: ID!
    name: String!
    books: [Book!]!
}

type Book {
    id: ID!
    title: String!
    publishedYear: Int
    author: Author!
}

type Query {
    book(id: ID!): Book
    books(limit: Int = 10): [Book!]!
    author(id: ID!): Author
}

type Mutation {
    createBook(input: CreateBookInput!): Book!
    deleteBook(id: ID!): Boolean!
}

input CreateBookInput {
    title: String!
    publishedYear: Int
    authorId: ID!
}
```

**Основные элементы SDL:**
- **Скалярные типы:** `Int`, `Float`, `String`, `Boolean`, `ID` (+ пользовательские: `DateTime`, `BigDecimal`)
- **`!`** -- non-null модификатор (поле обязательно)
- **`[Type]`** -- список, `[Type!]!` -- обязательный список обязательных элементов
- **`input`** -- специальный тип для входных данных мутаций
- **`enum`** -- перечисления
- **`interface`** и **`union`** -- полиморфизм

---


> [!mcq]
> **Вопрос:** Что такое Schema Definition Language (SDL) в GraphQL и какова его роль в проекте?
>
> - [ ] **A) `!` в SDL обозначает «список» (array) элементов**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: `!` — модификатор non-null (поле гарантированно не `null`); квадратные скобки `[...]` обозначают список; `[String!]!` — non-null список non-null строк.
>
>     ПОСЛЕДСТВИЕ: разработчик пишет `users: User!` ожидая массив, получает один объект; клиент падает на `.map()`, схема не отражает domain model.
>
> - [x] **B) SDL — текстовый язык описания типизированной схемы GraphQL: типов, полей, операций и связей; это единый контракт между клиентом и сервером, по которому валидируются запросы и генерируется tooling**
>
>     ✓ ПОЧЕМУ ВЕРНО: schema-first подход — SDL хранится в `.graphqls`, парсится при старте в `GraphQLSchema`, по нему валидируются входящие запросы и генерируются типы для клиента (Apollo Codegen, graphql-codegen).
>
>     МЕХАНИЗМ: spring-graphql читает `*.graphqls` из `classpath:graphql/`, биндит каждый `type/Query/Mutation` к `@SchemaMapping`-методам контроллера.
>
>     ```graphql
>     type Book { id: ID! title: String! author: Author! }
>     type Query { book(id: ID!): Book }
>     ```
>
>     ```java
>     @Controller
>     class BookController {
>         @QueryMapping public Book book(@Argument String id) { return service.find(id); }
>         @SchemaMapping public Author author(Book book) { return authorService.byId(book.authorId()); }
>     }
>     ```
>
>     ПРИМЕНИМОСТЬ: общий source of truth для backend, frontend, мобильных клиентов; автогенерация TS/Kotlin типов исключает дрейф между API и UI.
>
> - [ ] **C) `input` типы могут содержать поля с кастомными резолверами**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: `input` — чисто структуры данных без логики и резолверов; они нужны как «DTO для аргументов», нельзя ссылаться на Object type как input.
>
>     ПОСЛЕДСТВИЕ: попытка `input X { y: User }`, где `User` — Object type, падает на schema validation при старте: «field type must be Input type».
>
> - [ ] **D) SDL одинаков для GraphQL и gRPC — оба описывают API одинаковым языком**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: gRPC использует Protocol Buffers (`.proto`) с другой семантикой (RPC-методы, бинарный формат, отсутствие проекций полей); SDL — специфично только для GraphQL.
>
>     ПОСЛЕДСТВИЕ: команда пытается переиспользовать `.proto`-файлы как `.graphqls`, теряет tooling, не понимает где field selection и `input` против `message`.

## Q3. (!) Какие типы данных существуют в GraphQL?

`GraphQL` имеет строгую систему типов, которая является основой схемы:

**1. Скалярные типы (Scalar):**
```graphql
scalar DateTime    # пользовательский скаляр

type Example {
    id: ID!         # уникальный идентификатор (сериализуется как String)
    name: String!   # строка в UTF-8
    age: Int        # 32-битное целое
    rating: Float   # число с плавающей точкой (double)
    active: Boolean # true/false
    createdAt: DateTime  # пользовательский
}
```

**2. Object-типы** -- основной строительный блок:
```graphql
type User {
    id: ID!
    email: String!
    posts: [Post!]!
}
```

**3. Enum-типы:**
```graphql
enum Role {
    ADMIN
    USER
    MODERATOR
}
```

**4. Interface и Union:**
```graphql
interface Node {
    id: ID!
}

type User implements Node {
    id: ID!
    name: String!
}

union SearchResult = User | Post | Comment
```

**5. Input-типы** (для аргументов мутаций):
```graphql
input UserFilter {
    role: Role
    active: Boolean
}
```

**Модификаторы типов:**
- `String` -- nullable строка
- `String!` -- non-null строка
- `[String]` -- nullable список nullable строк
- `[String!]!` -- non-null список non-null строк

---


> [!mcq]
> **Вопрос:** Чем отличаются `interface` и `union` в системе типов GraphQL и как они применяются для полиморфизма?
>
> - [ ] **A) `ID` — это числовой тип `Integer`, и при сериализации сравнивается как число**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: `ID` — это уникальный идентификатор, который сериализуется как `String` (хотя клиент может прислать число — оно будет приведено к строке); сравнение «как число» некорректно для UUID и составных ключей.
>
>     ПОСЛЕДСТВИЕ: схема ломается, как только в БД появляются UUID (`"a1b2-..."`) или префиксные ID (`"user:42"`) — `Integer.parseInt` бросает `NumberFormatException`.
>
> - [ ] **B) `union` расширяет `interface` и добавляет к нему реализацию по умолчанию**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: это две разные конструкции. `interface` объявляет общие поля, которые Object types обязаны реализовать; `union` — это «один из» нескольких Object types БЕЗ общих полей.
>
>     ПОСЛЕДСТВИЕ: попытка `union X implements Y` — schema parse error; разработчик путает selection set (для union нужны `... on Type { ... }`-фрагменты) с обычным наследованием.
>
> - [x] **C) `interface` — общий контракт полей, который реализуют Object types (`implements`); `union` — set возможных Object types без общих полей; в обоих случаях клиент использует inline-фрагменты `... on Type` для выбора type-specific полей**
>
>     ✓ ПОЧЕМУ ВЕРНО: это и есть штатный механизм полиморфизма в GraphQL — interface для «все Node имеют id», union для «search возвращает User или Post или Comment».
>
>     МЕХАНИЗМ: на сервере нужен `TypeResolver`, который по runtime-объекту возвращает GraphQL type name; иначе executor не знает, какие резолверы вызвать.
>
>     ```graphql
>     interface Node { id: ID! }
>     type User implements Node { id: ID! name: String! }
>     union SearchResult = User | Post | Comment
>     type Query { search(q: String!): [SearchResult!]! }
>     ```
>
>     ```java
>     @Bean
>     RuntimeWiringConfigurer wiring() {
>         return b -> b.type("SearchResult", t -> t.typeResolver(env -> {
>             Object o = env.getObject();
>             if (o instanceof User)    return env.getSchema().getObjectType("User");
>             if (o instanceof Post)    return env.getSchema().getObjectType("Post");
>             return env.getSchema().getObjectType("Comment");
>         }));
>     }
>     ```
>
>     ```graphql
>     query { search(q: "java") { __typename ... on User { name } ... on Post { title } } }
>     ```
>
>     ПРИМЕНИМОСТЬ: feed/timeline с разнотипными сущностями, поиск по нескольким коллекциям, activity log.
>
> - [ ] **D) `Float` в GraphQL — это 32-битное число, эквивалентное Java `float`**
>
>     ✗ ПОЧЕМУ НЕВЕРНО: GraphQL `Float` по спецификации — IEEE 754 double precision (64-bit), маппится на Java `Double`.
>
>     ПОСЛЕДСТВИЕ: маппинг резолвера в `float` теряет точность для финансовых/научных значений; для денежных сумм всё равно нужен custom scalar (`BigDecimal`) — `Float` здесь не подходит ни в каком виде.

## Q4. Что такое Input-типы и зачем они нужны?

**`Input`-типы** -- специальные типы для передачи структурированных данных в аргументы запросов и мутаций. Они отличаются от обычных Object-типов: input-типы не могут содержать поля с аргументами и не могут реализовывать интерфейсы.

```graphql
input CreateUserInput {
    name: String!
    email: String!
    role: Role = USER    # значение по умолчанию
}

input UpdateUserInput {
    name: String
    email: String
    role: Role
}

type Mutation {
    createUser(input: CreateUserInput!): User!
    updateUser(id: ID!, input: UpdateUserInput!): User!
}
```

**Почему нельзя использовать обычные типы как аргументы:** обычные Object-типы могут содержать циклические ссылки и поля с резолверами, что делает их непригодными для входных данных. `Input`-типы -- это чистые структуры данных без логики.

**Паттерн на практике:** для каждой мутации создают свой input-тип (`CreateXInput`, `UpdateXInput`), что позволяет различать обязательные поля для создания и опциональные поля для обновления.

---


> [!mcq] Какое утверждение об `input`-типах в GraphQL верно?
>
> - [ ] **A.** Обычный `type User { ... }` можно передавать как аргумент мутации, если у него нет резолверов
>
>     НЕВЕРНО. GraphQL spec явно запрещает использовать Object types в позиции аргумента. Schema validation на старте упадёт с ошибкой `The type of <Mutation>.<arg> must be Input Type but got: User`.
>
>     ПОСЛЕДСТВИЕ: приложение не стартует — `SchemaProblem` в `GraphQLSchema.newSchema().build()`; даже «чистый» Object без резолверов отвергается парсером схемы.
>
> - [ ] **B.** `input`-типы могут содержать циклические ссылки друг на друга, как Object types
>
>     НЕВЕРНО. По спецификации GraphQL (June 2018, §3.10) циклы во `input` запрещены: `input A { b: B } input B { a: A }` — невалидная схема.
>
>     ПОСЛЕДСТВИЕ: `graphql-java` бросит `InvalidSchemaException` на этапе сборки; цикл невозможно сериализовать из JSON (бесконечная вложенность переменных).
>
> - [ ] **C.** Один универсальный `UserInput` достаточен и для `createUser`, и для `updateUser`
>
>     НЕВЕРНО. Для `createUser` поля обязательны (`name: String!`), для `updateUser` — опциональны (`name: String`). Один тип ломает domain invariants: либо create пропустит `null`, либо update потребует все поля.
>
>     ПОСЛЕДСТВИЕ: на проде клиент получит `User` с `name=null` после create (NOT NULL constraint в БД), либо вынужден слать все поля при update — лишний трафик + race condition при concurrent edit.
>
> - [x] **D.** `input`-типы — это чистые структуры данных без резолверов и интерфейсов, отдельные от Object types
>
>     ВЕРНО. Спецификация разделяет Input/Output types: `input` не имеет резолверов, не реализует `interface`, не содержит union/object-полей с аргументами. Это сериализуемый DTO для аргументов.
>
>     ```graphql
>     input CreateUserInput {
>         name: String!
>         email: String!
>         role: Role = USER
>     }
>     type Mutation {
>         createUser(input: CreateUserInput!): User!
>     }
>     ```
>
>     ```java
>     @Component
>     public class UserMutationResolver {
>         @MutationMapping
>         public User createUser(@Argument CreateUserInput input) {
>             return userService.create(input.name(), input.email(), input.role());
>         }
>     }
>     public record CreateUserInput(String name, String email, Role role) {}
>     ```
>
>     ПРИМЕНЕНИЕ: разделение `CreateXInput`/`UpdateXInput`/`FilterXInput` фиксирует контракт — обязательность полей выражена на уровне схемы, клиент валидируется до резолвера. Введение default value (`role: Role = USER`) убирает boilerplate из клиентского кода.

## Q5. (!) Как устроены Query, Mutation и Subscription?

Три корневых типа определяют входные точки в `GraphQL` API:

**`Query`** -- чтение данных (аналог `GET` в REST):
```graphql
type Query {
    user(id: ID!): User
    users(filter: UserFilter, page: Int, size: Int): [User!]!
    searchUsers(term: String!): [User!]!
}
```

**`Mutation`** -- изменение данных (аналог `POST`/`PUT`/`DELETE`):
```graphql
type Mutation {
    createUser(input: CreateUserInput!): User!
    updateUser(id: ID!, input: UpdateUserInput!): User!
    deleteUser(id: ID!): Boolean!
}
```

**`Subscription`** -- подписка на события в реальном времени (через `WebSocket`):
```graphql
type Subscription {
    userCreated: User!
    messageAdded(chatId: ID!): Message!
}
```

**Важные различия:**

| Аспект | Query | Mutation | Subscription |
|--------|-------|----------|--------------|
| Выполнение | Параллельное | Последовательное | Потоковое |
| Побочные эффекты | Нет | Да (только top-level) | Нет (получение событий) |
| Транспорт | HTTP | HTTP | WebSocket / SSE |

**Ключевой момент по спецификации:** мутации на верхнем уровне выполняются **последовательно** (в отличие от query, где поля могут выполняться параллельно). Это гарантирует предсказуемый порядок побочных эффектов.

---


> [!mcq] Какое утверждение о Query/Mutation/Subscription корректно?
>
> - [x] **A.** Top-level поля `Mutation` выполняются последовательно, top-level поля `Query` — параллельно
>
>     ВЕРНО. GraphQL spec §6.2.2 (Mutation): «If the operation is a mutation, the result of the operation is the result of executing the operation’s top level selection set on the mutation root object type. This selection set should be executed serially». Для Query — `executeSelectionSet` параллельно.
>
>     ```graphql
>     mutation BatchOps {
>         a: createUser(input: {name: "A"}) { id }   # выполняется ПЕРВЫМ
>         b: createUser(input: {name: "B"}) { id }   # ПОСЛЕ a, видит её результат
>     }
>     ```
>
>     ```java
>     // graphql-java: AsyncExecutionStrategy для Query, AsyncSerialExecutionStrategy для Mutation
>     GraphQL.newGraphQL(schema)
>         .queryExecutionStrategy(new AsyncExecutionStrategy())
>         .mutationExecutionStrategy(new AsyncSerialExecutionStrategy())
>         .build();
>     ```
>
>     ПРИМЕНЕНИЕ: позволяет клиенту батчить связанные мутации в одном запросе (`createUser` → `createPost` для нового user.id) с гарантией порядка. Внутри одного резолвера sub-selection всё равно параллелен — последовательность только на корне.
>
> - [ ] **B.** Top-level mutations выполняются параллельно, как и Query fields
>
>     НЕВЕРНО. Спецификация требует serial execution для мутаций. Параллельная реализация — нарушение spec, race conditions на одних и тех же сущностях.
>
>     ПОСЛЕДСТВИЕ: batch-мутация `deleteAccount → transferFunds` может выполнить transfer на удалённый аккаунт (или наоборот); никакие гарантии порядка — потеря данных, неконсистентный state.
>
> - [ ] **C.** `Subscription` работает через HTTP long-polling без persistent connection
>
>     НЕВЕРНО. Subscription требует двусторонний канал: WebSocket (`graphql-ws`/`graphql-transport-ws`) или SSE. HTTP stateless не поддерживает server push без переподключения.
>
>     ПОСЛЕДСТВИЕ: polling = высокая задержка (1-5s), N×RPS на сервер, нет гарантии «получено единожды»; для chat/notifications даёт UX как у REST `GET /messages?since=...` — теряется смысл Subscription.
>
> - [ ] **D.** `Query` со side effects семантически эквивалентен `Mutation`
>
>     НЕВЕРНО. Клиенты (Apollo Client, Relay), CDN и gateway-кэши агрессивно кэшируют `Query` (`@cacheControl`, `GET`-запросы, persisted queries).
>
>     ПОСЛЕДСТВИЕ: `query incrementCounter { ... }` выполнится один раз и закэшируется — последующие вызовы вернут старый результат без вызова резолвера; реальный inc не произойдёт. Нарушение контракта Query = pure read.

## Q6. Что такое переменные и директивы в GraphQL?

### Переменные

Переменные позволяют параметризировать запросы, отделяя данные от структуры запроса:

```graphql
query GetUser($userId: ID!, $withPosts: Boolean!) {
    user(id: $userId) {
        name
        email
        posts @include(if: $withPosts) {
            title
        }
    }
}
```

Переменные передаются отдельно как JSON:
```json
{
    "userId": "42",
    "withPosts": true
}
```

### Директивы

**Встроенные директивы:**
- **`@include(if: Boolean)`** -- включить поле, если условие `true`
- **`@skip(if: Boolean)`** -- пропустить поле, если условие `true`
- **`@deprecated(reason: String)`** -- пометить поле как устаревшее (на стороне схемы)

```graphql
type User {
    id: ID!
    name: String!
    username: String @deprecated(reason: "Используйте поле name")
}
```

**Пользовательские директивы** (на уровне схемы):
```graphql
directive @auth(role: Role!) on FIELD_DEFINITION

type Query {
    users: [User!]! @auth(role: ADMIN)
    publicPosts: [Post!]!
}
```

---


> [!mcq] Какое утверждение о переменных и директивах GraphQL верно?
>
> - [ ] **A.** `@skip(if: true)` включает поле, а `@include(if: true)` — пропускает
>
>     НЕВЕРНО. Семантика прямо противоположная: `@skip(if: true)` ПРОПУСКАЕТ поле, `@include(if: true)` ВКЛЮЧАЕТ. Они дублируют друг друга через инверсию условия (`@skip(if: $x)` == `@include(if: !$x)`).
>
>     ПОСЛЕДСТВИЕ: клиент получает пустой ответ там, где ожидает данные; UI рендерит «No data» при `withPosts=true`; отладка через сетевые логи показывает корректный запрос, но мозг разработчика ищет баг не там.
>
> - [x] **B.** Кастомная директива на схеме требует server-side реализации через `SchemaDirectiveWiring` или DataFetcher
>
>     ВЕРНО. Объявление `directive @auth(role: Role!) on FIELD_DEFINITION` — это только декларация в SDL. Без обработчика runtime просто пропускает её.
>
>     ```graphql
>     directive @auth(role: Role!) on FIELD_DEFINITION
>     type Query {
>         adminUsers: [User!]! @auth(role: ADMIN)
>     }
>     ```
>
>     ```java
>     public class AuthDirective implements SchemaDirectiveWiring {
>         @Override
>         public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {
>             GraphQLDirective d = env.getDirective();
>             Role required = Role.valueOf((String) d.getArgument("role").getValue());
>             GraphQLFieldDefinition field = env.getElement();
>             DataFetcher<?> original = env.getCodeRegistry().getDataFetcher(env.getFieldsContainer(), field);
>             DataFetcher<?> guarded = e -> {
>                 User u = e.getGraphQlContext().get("user");
>                 if (u == null || !u.hasRole(required)) throw new AccessDeniedException("Need " + required);
>                 return original.get(e);
>             };
>             env.getCodeRegistry().dataFetcher(env.getFieldsContainer(), field, guarded);
>             return field;
>         }
>     }
>     RuntimeWiring.newRuntimeWiring().directive("auth", new AuthDirective()).build();
>     ```
>
>     ПРИМЕНЕНИЕ: декларативная авторизация на уровне схемы — `@auth(role: ADMIN)` рядом с полем читаемее, чем разбросанные проверки в резолверах. То же для `@upper`, `@deprecated(reason: ...)` (поведение в introspection), `@cost` (rate limiting).
>
> - [ ] **C.** GraphQL переменные нужно вручную экранировать для защиты от injection в schema
>
>     НЕВЕРНО. Переменные передаются отдельным JSON, не интерполируются в query string. Парсер `graphql-java` строго типизирует их по схеме: `$id: ID!` отвергнет non-string.
>
>     ПОСЛЕДСТВИЕ: ложное чувство безопасности заставляет писать ручной escaping вместо параметризованных SQL-запросов в резолверах — injection всё равно случится через `String.format("SELECT ... WHERE name = '%s'", input.name())`. Защищать надо downstream вызовы (JDBC PreparedStatement), не GraphQL слой.
>
> - [ ] **D.** `@deprecated` на поле блокирует его выполнение в runtime
>
>     НЕВЕРНО. `@deprecated(reason: "...")` — это маркер для introspection (отображается в IDE/Graphiql и в `__schema`). Поле работает как раньше, резолвер вызывается.
>
>     ПОСЛЕДСТВИЕ: команда добавляет `@deprecated` и удаляет код через месяц — все клиенты, не обновившие запросы, начинают получать ошибки в проде. Нужен реальный мониторинг использования deprecated полей (`fieldExecutionListener`) перед удалением.

## Q7. Что такое фрагменты и зачем они нужны?

**Фрагменты** -- переиспользуемые наборы полей, которые устраняют дублирование в запросах:

```graphql
fragment UserBasicInfo on User {
    id
    name
    email
    avatarUrl
}

query {
    currentUser {
        ...UserBasicInfo
        role
    }
    teamMembers {
        ...UserBasicInfo
        joinedAt
    }
}
```

**Когда использовать фрагменты:**
- Одинаковые наборы полей в нескольких запросах
- Компонентный подход на фронтенде: каждый UI-компонент объявляет свой фрагмент с нужными данными
- Сокращение размера запросов

**Важно:** фрагмент всегда привязан к конкретному типу (`on User`), что даёт проверку типов на этапе валидации запроса.

---


> [!mcq] Какое утверждение о GraphQL-фрагментах верно?
>
> - [ ] **A.** Фрагмент можно объявить без указания типа (`fragment X { ... }`), он автоматически применится к любому объекту в запросе.
>
>     **Почему неверно:** объявление фрагмента БЕЗ `on <Type>` — синтаксическая ошибка в SDL. Спецификация GraphQL требует обязательного указания типа: `fragment UserBasicInfo on User { ... }`.
>
>     **Последствие:** запрос отклонится на этапе валидации с `Syntax Error: Expected "on", found "{"`, клиент получит `errors` вместо `data` и UI сломается.
>
> - [ ] **B.** Фрагменты раскрываются на стороне клиента ДО отправки на сервер — сервер получает уже плоский запрос без `...FragmentName`.
>
>     **Почему неверно:** фрагменты — часть спецификации языка запросов и отправляются на сервер AS-IS. Сервер их парсит и разворачивает в AST при выполнении. Клиентские библиотеки (Apollo, Relay) могут оптимизировать, но базовое поведение — отправка фрагментов на сервер.
>
>     **Последствие:** разработчик ошибочно считает фрагменты «синтаксическим сахаром» уровня клиента и не понимает, как они влияют на серверный план выполнения и кэширование Apollo-нормализованного store.
>
> - [x] **C.** Фрагмент привязан к конкретному типу через `on <Type>`, что даёт type-checking на этапе валидации запроса и позволяет переиспользовать набор полей в разных queries.
>
>     **Почему верно:** ключевое свойство фрагментов — типизация и переиспользование. Пример из шпаргалки:
>
>     ```graphql
>     fragment UserBasicInfo on User {
>         id
>         name
>         email
>     }
>
>     query {
>         currentUser { ...UserBasicInfo role }
>         teamMembers { ...UserBasicInfo joinedAt }
>     }
>     ```
>
>     Если в `UserBasicInfo` указать поле, отсутствующее в `User`, валидация упадёт ещё до выполнения резолверов.
>
>     **Последствие:** компонентный подход на фронте (каждый UI-компонент объявляет свой fragment) + DRY на сервере + раннее обнаружение ошибок при изменении схемы.
>
> - [ ] **D.** Фрагменты работают только в `Query`, в `Mutation` и `Subscription` они запрещены спецификацией.
>
>     **Почему неверно:** спецификация GraphQL разрешает фрагменты во всех трёх корневых операциях. Можно объявить `fragment OrderResult on Order { id status }` и использовать его как в Query, так и в Mutation response.
>
>     **Последствие:** разработчик дублирует один и тот же набор полей в Query и Mutation response, нарушая DRY и создавая drift между ними при эволюции схемы.

## Q8. Как работают Inline Fragments и Union-типы?

**Union-типы** позволяют полю возвращать один из нескольких типов:

```graphql
union SearchResult = User | Post | Comment

type Query {
    search(term: String!): [SearchResult!]!
}
```

Для работы с union/interface используют **inline fragments**:

```graphql
query {
    search(term: "java") {
        ... on User {
            name
            email
        }
        ... on Post {
            title
            content
        }
        ... on Comment {
            text
            author {
                name
            }
        }
        __typename   # мета-поле: возвращает имя типа
    }
}
```

**`__typename`** -- мета-поле, доступное для любого Object-типа, которое возвращает имя конкретного типа. Клиентские библиотеки (Apollo, Relay) используют его для нормализации кэша.

---


> [!mcq] Какое утверждение об inline fragments и union-типах в GraphQL верно?
>
> - [ ] **A.** Union-тип может включать как Object-типы, так и скалярные типы (`String`, `Int`), что упрощает поиск разнородных результатов.
>
>     **Почему неверно:** спецификация GraphQL запрещает включать в union скаляры, enum, input-типы и интерфейсы. Union содержит ТОЛЬКО Object-типы: `union SearchResult = User | Post | Comment` — корректно, `union X = String | Int` — ошибка схемы.
>
>     **Последствие:** schema-валидатор (`graphql-java`, `graphql-tools`) откажется стартовать приложение с ошибкой `Union types can only contain Object types`. Запуск сервиса упадёт ещё до приёма первого запроса.
>
> - [ ] **B.** Для извлечения полей из union-результата достаточно перечислить поля напрямую: `search(term: "java") { name email title }` — engine сам разберётся, какие поля от какого типа.
>
>     **Почему неверно:** на union/interface-полях нельзя селектить «общие» поля без `... on <Type>`. Спецификация требует inline fragment для каждого возможного типа: `... on User { name }`, `... on Post { title }`.
>
>     **Последствие:** валидатор вернёт `Field "name" cannot be queried on union type "SearchResult"`. Запрос отклонится, клиент получит пустой `data` и `errors` — типичная ошибка джунов при первом использовании union.
>
> - [ ] **C.** Мета-поле `__typename` доступно только для union- и interface-полей; на обычных Object-типах оно вызывает ошибку валидации.
>
>     **Почему неверно:** `__typename` — универсальное мета-поле, доступное на ЛЮБОМ Object-типе, не только на union/interface. Apollo Client автоматически добавляет `__typename` в каждый selection set для нормализации кэша.
>
>     **Последствие:** разработчик отключает `__typename` для «обычных» типов, ломает кэш-нормализацию Apollo (по умолчанию `dataIdFromObject` использует `__typename + id`), получает stale data в UI после мутаций.
>
> - [x] **D.** Inline fragments `... on <Type>` обязательны для union/interface, позволяют запросить разный набор полей для каждого возможного типа; `__typename` помогает клиенту определить конкретный тип результата для рендеринга.
>
>     **Почему верно:** это и есть штатный механизм работы с polymorphic-результатами. Пример из шпаргалки:
>
>     ```graphql
>     query {
>         search(term: "java") {
>             __typename
>             ... on User    { name email }
>             ... on Post    { title content }
>             ... on Comment { text author { name } }
>         }
>     }
>     ```
>
>     Клиент по `__typename` решает, какой React-компонент рендерить (`UserCard`, `PostCard`, `CommentCard`).
>
>     **Последствие:** type-safe работа с разнородными результатами, корректная нормализация Apollo-кэша, возможность строить полиморфные UI без дублирования endpoints.

## Q9. (!) Что такое резолверы и как они работают?

**Резолвер** -- это функция, которая отвечает за получение данных для конкретного поля в схеме. Каждое поле в `GraphQL`-схеме имеет соответствующий резолвер.

```mermaid
graph TD
    Q[Query: user id=1] --> R1[Resolver: Query.user]
    R1 -->|User| R2[Resolver: User.name]
    R1 -->|User| R3[Resolver: User.posts]
    R3 -->|List Post| R4[Resolver: Post.title]
    R3 -->|List Post| R5[Resolver: Post.author]
```

**Аргументы резолвера** (четыре стандартных):

1. **`parent`** (root) -- результат родительского резолвера
2. **`args`** -- аргументы, переданные полю
3. **`context`** -- общий контекст запроса (аутентификация, DataLoader, соединения с БД)
4. **`info`** -- метаданные о запросе (дерево полей, фрагменты)

**Пример на Java (Spring for GraphQL):**

```java
@Controller
public class BookController {

    @QueryMapping
    public Book book(@Argument Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @SchemaMapping(typeName = "Book", field = "author")
    public Author author(Book book) {
        return authorRepository.findById(book.getAuthorId());
    }
}
```

**Default resolvers:** если для поля не определён явный резолвер, `GraphQL` использует тривиальный резолвер, который просто читает одноимённое свойство из parent-объекта (аналог геттера в Java).

---


> [!mcq] Какое утверждение о резолверах в GraphQL корректно?
>
> - [x] **A.** Резолвер — функция, разрешающая значение конкретного поля; получает четыре стандартных аргумента `parent / args / context / info`, причём если резолвер не определён явно, используется тривиальный (читает одноимённое свойство из `parent`).
>
>     **Почему верно:** это базовая модель резолверов в любой GraphQL-имплементации. Spring for GraphQL автоматически генерирует тривиальные резолверы для полей-геттеров:
>
>     ```java
>     @Controller
>     public class BookController {
>
>         @QueryMapping
>         public Book book(@Argument Long id) {
>             return bookRepository.findById(id).orElse(null);
>         }
>
>         // Для поля Book.title тривиальный резолвер вызовет book.getTitle() автоматически
>         @SchemaMapping(typeName = "Book", field = "author")
>         public Author author(Book book) {        // book — это parent
>             return authorRepository.findById(book.getAuthorId());
>         }
>     }
>     ```
>
>     `parent` = результат родительского резолвера, `args` = аргументы поля, `context` = per-request контекст (auth, DataLoader), `info` = AST-метаданные запроса.
>
>     **Последствие:** не нужно писать boilerplate-резолверы для каждого поля — только для тех, что требуют отдельной логики (связи, вычисляемые поля, авторизация).
>
> - [ ] **B.** Все резолверы выполняются строго последовательно сверху вниз — engine не может распараллелить даже независимые поля корневого `Query`.
>
>     **Почему неверно:** для `Query` engine ИМЕЕТ ПРАВО выполнять поля верхнего уровня параллельно (и `graphql-java` делает это через `CompletableFuture`). Последовательное выполнение — только для `Mutation` (чтобы избежать race condition между сайд-эффектами).
>
>     **Последствие:** разработчик пишет блокирующие резолверы вместо реактивных, теряет ~N-кратное ускорение на запросах с независимыми top-level полями, latency p99 растёт линейно от количества полей в Query.
>
> - [ ] **C.** Аргумент `parent` всегда равен `null` — резолвер не имеет доступа к данным родительского уровня и должен заново загружать всё из базы.
>
>     **Почему неверно:** `parent` — это РЕЗУЛЬТАТ родительского резолвера. Для `Query.user` parent = `null`, но для `User.posts` parent = объект `User`, возвращённый из `Query.user`. Именно через parent резолверы строят цепочки навигации по графу.
>
>     **Последствие:** разработчик игнорирует parent и в каждом nested-резолвере делает повторный SELECT из БД, превращая запрос в worst-case N+1 даже там, где DataLoader не нужен (данные уже есть в parent).
>
> - [ ] **D.** Резолвер обязан возвращать примитивные типы (`String`, `Int`, `Boolean`); вернуть объект и продолжить обход engine не умеет — для вложенных полей нужно делать отдельный запрос с фронта.
>
>     **Почему неверно:** ровно наоборот — engine продолжает обход рекурсивно: если резолвер возвращает Object, движок берёт это значение как `parent` и вызывает резолверы вложенных полей. Это и есть суть «разрешения дерева до листьев».
>
>     **Последствие:** разработчик строит «плоский» API в стиле REST поверх GraphQL, теряет ключевое преимущество — один запрос вместо waterfall’а, фронт делает N round-trips там, где должен быть один.

## Q10. Как GraphQL выполняет запрос (execution model)?

Выполнение `GraphQL`-запроса -- это обход дерева полей сверху вниз:

```mermaid
graph TD
    A[1. Parsing: текст → AST] --> B[2. Validation: проверка по схеме]
    B --> C[3. Execution: обход дерева резолверов]
    C --> D[4. Serialization: формирование JSON-ответа]
```

**Детали этапа Execution:**

1. Начинается с корневого типа (`Query`, `Mutation`, `Subscription`)
2. Для каждого поля вызывается соответствующий резолвер
3. Результат резолвера передаётся как `parent` дочерним резолверам
4. Скалярные поля (листья) возвращают конечные значения
5. Для `Query` поля верхнего уровня могут выполняться **параллельно**
6. Для `Mutation` поля верхнего уровня выполняются **последовательно**

**Обход до листьев:** `GraphQL` engine гарантирует, что каждое запрошенное поле будет разрешено до скалярного значения. Если резолвер возвращает объект, engine продолжает обход по вложенным полям.

---


> [!mcq] Какой порядок выполнения полей верхнего уровня гарантирует GraphQL execution model?
>
> - [ ] **A.** Поля `Query` выполняются последовательно, поля `Mutation` — параллельно для ускорения записи.
>
>     **Что на самом деле:** ровно наоборот — `Query` параллельно, `Mutation` последовательно. Спецификация требует sequential execution для мутаций именно потому, что записи имеют side-effects и порядок важен.
>
>     **Откуда путаница:** интуитивно «запись надо быстро, чтение можно подождать», и кажется что параллелить нужно мутации. Но GraphQL оптимизирует под безопасность, а не скорость записи.
>
>     **Если бы это было правдой:** две мутации в одном запросе `createOrder` + `chargePayment` могли бы выполниться в произвольном порядке — платёж мог бы пройти до создания заказа.
>
> - [ ] **B.** Все поля любого типа выполняются строго последовательно сверху вниз по тексту запроса.
>
>     **Что на самом деле:** для `Query` и `Subscription` engine волен выполнять top-level поля параллельно. Только `Mutation` гарантирует sequential execution.
>
>     **Откуда путаница:** «обход дерева сверху вниз» из mermaid-диаграммы воспринимается как линейная последовательность. На самом деле «сверху вниз» — про уровни вложенности, не про порядок сиблингов.
>
>     **Если бы это было правдой:** теряется главное преимущество GraphQL — невозможно было бы параллельно загружать несвязанные ветки графа (`user` и `products` в одном query).
>
> - [ ] **C.** Резолверы запускаются снизу вверх: сначала листья-скаляры, потом родительские объекты получают агрегаты.
>
>     **Что на самом деле:** обход идёт сверху вниз. Родительский резолвер выполняется первым, его результат передаётся в `parent` дочерним резолверам. Без `parent` дочерние резолверы не знают, что разрешать.
>
>     **Откуда путаница:** в SQL/ORM есть паттерн bottom-up агрегации (`COUNT`, `SUM`). В GraphQL модель противоположная — top-down traversal.
>
>     **Если бы это было правдой:** резолвер `book.author` не знал бы, какую книгу разрешать, потому что `parent: Book` ещё не существует на момент его вызова.
>
> - [x] **D.** Query/Subscription поля верхнего уровня могут выполняться параллельно, Mutation — строго последовательно; обход дерева идёт сверху вниз с передачей `parent` в дочерние резолверы.
>
>     **Развёрнутое объяснение:** execution model — это 4 этапа (parse → validate → execute → serialize). На этапе execute engine стартует с корневого типа, для каждого выбранного поля вызывает резолвер и рекурсивно спускается к детям, передавая результат как `parent`. Для `Query` спецификация разрешает параллелизм top-level полей (нет side-effects), для `Mutation` запрещает (порядок мутаций — часть контракта).
>
>     **Пример:** запрос `mutation { a: createUser(...) { id } b: deleteUser(...) }` — сначала полностью отрабатывает `a` (включая все вложенные резолверы), только потом стартует `b`. А `query { users { name } products { title } }` может тянуть `users` и `products` параллельно.
>
>     **Когда применять:** знание модели критично при дизайне резолверов — нельзя полагаться на shared mutable state между сиблингами в `Query` (могут выполниться одновременно). В `Mutation` можно — порядок гарантирован.
>
>     **Подводные камни:** многие engine (graphql-java по умолчанию) выполняют `Query`-сиблинги последовательно из соображений простоты — параллелизм опционален. Если код полагается на параллельность, проверяй конфигурацию `ExecutionStrategy`. Также: `DataLoader` дедуплицирует запросы именно за счёт того, что все вложенные резолверы успевают зарегистрировать ключи до начала batch.
>
>     **Связанные вопросы:** [[Q9]], [[Q11]], [[Q12]].

## Q11. (!) Что такое проблема N+1 в GraphQL и как её решить?

**Проблема N+1** -- классическая проблема производительности, когда для загрузки списка из N элементов выполняется 1 запрос на список + N запросов на связанные данные.

**Пример:**
```graphql
query {
    books {          # 1 запрос: SELECT * FROM books
        title
        author {     # N запросов: SELECT * FROM authors WHERE id = ?
            name     #   для каждой книги отдельный запрос
        }
    }
}
```

Если в базе 100 книг, будет выполнено 1 + 100 = 101 SQL-запрос.

**Решения:**

| Подход | Описание | Когда использовать |
|--------|----------|--------------------|
| `DataLoader` | Батчинг + кэширование запросов | Стандартный подход |
| `JOIN FETCH` | Загрузка в одном SQL-запросе | Простые случаи, один уровень |
| `@BatchMapping` | Spring for GraphQL батч-маппинг | Spring-проекты |
| Look-ahead | Анализ запроса до выполнения | Продвинутая оптимизация |

**DataLoader превращает N+1 в 1+1:**
```
Без DataLoader:
SELECT * FROM books                    -- 1 запрос
SELECT * FROM authors WHERE id = 1     -- N запросов
SELECT * FROM authors WHERE id = 2
SELECT * FROM authors WHERE id = 3
...

С DataLoader:
SELECT * FROM books                    -- 1 запрос
SELECT * FROM authors WHERE id IN (1, 2, 3, ...)  -- 1 запрос
```

---


> [!mcq] В чём суть проблемы N+1 в GraphQL и почему `DataLoader` её решает?
>
> - [x] **A.** Каждая вложенная связь резолвится отдельным запросом (1 на список + N на детей); `DataLoader` собирает ключи в один tick и делает batch-запрос `WHERE id IN (...)`, превращая N+1 в 1+1.
>
>     **Развёрнутое объяснение:** GraphQL обходит дерево полей рекурсивно — для списка из N книг резолвер `book.author` вызывается N раз, каждый раз делая отдельный SELECT по `author_id`. `DataLoader` встаёт прокси-слоем: вместо немедленного запроса он собирает все запрошенные ключи в текущем event-loop tick, а в конце tick'а вызывает batch-функцию, которая делает один SQL `WHERE id IN (1,2,...,N)`. Результаты раздаются по исходным промисам.
>
>     **Пример:** `books { author { name } }` без DataLoader — 1+100=101 SQL-запрос на список из 100 книг. С DataLoader — 2 запроса: `SELECT * FROM books` и `SELECT * FROM authors WHERE id IN (...)`. Плюс per-request кэш: если две разные книги ссылаются на одного автора, второй раз ключ не пойдёт в batch.
>
>     **Когда применять:** всегда для вложенных связей one-to-many и many-to-one. Особенно критично, если резолвер дёргает внешний сервис (REST/gRPC) — там 100 round-trip'ов вместо одного убивают latency.
>
>     **Подводные камни:** `DataLoader` обязан создаваться per-request (через `@RequestScope` или `BatchLoaderRegistry`), иначе кэш «потечёт» между пользователями и вернёт чужие данные. Также batch-функция должна сохранять порядок ключей — если возвращаете `Map<Long, Author>`, отсутствующие ключи дадут `null` в соответствующих позициях, что нужно явно обрабатывать. И помните: DataLoader не решает over-fetching — только лишние round-trip'ы.
>
>     **Связанные вопросы:** [[Q10]], [[Q12]], [[Q9]].
>
> - [ ] **B.** N+1 — это про слишком большой объём данных в ответе; `DataLoader` сжимает payload и стримит данные клиенту чанками.
>
>     **Что на самом деле:** N+1 — про количество round-trip'ов к источнику данных (БД/сервису), а не размер payload. `DataLoader` не занимается ни сжатием, ни стримингом — он чисто про батчинг запросов на бэкенде.
>
>     **Откуда путаница:** «много данных» и «много запросов» субъективно ощущаются как одна и та же проблема производительности. Плюс есть отдельный паттерн `@defer/@stream` для стриминга — его легко перепутать с DataLoader.
>
>     **Если бы это было правдой:** N+1 проявлялся бы только на больших объектах, а на крошечных списках с тяжёлыми связями (100 коротких книг с 100 отдельными SQL за авторами) проблема была бы незаметна — а на практике именно тут она самая болезненная.
>
> - [ ] **C.** N+1 — проблема параллельного выполнения мутаций; решается переводом всех мутаций в последовательный режим через `DataLoader`.
>
>     **Что на самом деле:** N+1 возникает на чтениях (Query) при обходе связей, мутации тут ни при чём. Мутации и так выполняются последовательно по спецификации (см. [[Q10]]). `DataLoader` — про батчинг, не про сериализацию.
>
>     **Откуда путаница:** обе темы — performance + execution model, легко смешать в голове. На собеседовании это типичная путаница «слышал звон».
>
>     **Если бы это было правдой:** проблема исчезала бы при отключении параллелизма Query-сиблингов, но реальный N+1 на 100 авторов одинаково плох и при последовательном, и при параллельном выполнении — потому что 100 round-trip'ов всё равно остаются.
>
> - [ ] **D.** N+1 решается JOIN-ом всех таблиц в один SQL; `DataLoader` под капотом строит огромный JOIN всех связанных сущностей за один запрос.
>
>     **Что на самом деле:** `DataLoader` делает отдельный batch-запрос на каждый тип связи (один `WHERE id IN` на авторов, другой на издателей и т.д.), а не один большой JOIN. JOIN — это альтернативный подход (`JOIN FETCH`), но у него свои проблемы — декартово произведение, дублирование строк.
>
>     **Откуда путаница:** для разработчиков из мира JPA «решить N+1» рефлекторно означает `JOIN FETCH`. В GraphQL подход другой — батчинг плюс per-request кэш.
>
>     **Если бы это было правдой:** при запросе `books { author publisher reviews }` `DataLoader` строил бы один SQL с тремя JOIN'ами — но в коде batch-функции явно видны независимые `repo.findAllById(ids)` для каждой связи. JOIN — выбор разработчика, не магия DataLoader'а.

## Q12. Как работает DataLoader?

**`DataLoader`** -- это утилита, созданная Facebook, которая решает проблему N+1 через два механизма: **батчинг** и **кэширование**.

**Принцип работы:**

```mermaid
graph LR
    R1[Resolver 1<br/>load id=1] --> DL[DataLoader<br/>собирает id]
    R2[Resolver 2<br/>load id=2] --> DL
    R3[Resolver 3<br/>load id=1] --> DL
    DL -->|batch: 1,2| DB[(Database)]
    DL -->|id=1 из кэша| R3
```

1. Резолверы вызывают `dataLoader.load(key)` -- запрос откладывается
2. В конце "тика" (event loop tick) все накопленные ключи передаются в batch-функцию
3. Batch-функция делает один запрос за все ключи (например, `WHERE id IN (...)`)
4. Результаты раздаются по исходным промисам
5. Повторный запрос с тем же ключом отдаётся из кэша (per-request)

**Реализация на Java:**

```java
@Configuration
public class DataLoaderConfig {

    @Bean
    public BatchLoaderRegistry batchLoaderRegistry(AuthorRepository repo) {
        return registry -> registry
            .forTypePair(Long.class, Author.class)
            .registerMappedBatchLoader((authorIds, env) -> {
                // Один запрос вместо N
                Map<Long, Author> authors = repo.findAllById(authorIds)
                    .stream()
                    .collect(Collectors.toMap(Author::getId, a -> a));
                return Mono.just(authors);
            });
    }
}
```

**Важно:** `DataLoader` создаётся **per-request**, чтобы кэш не утекал между запросами разных пользователей.

---


> [!mcq] Почему `DataLoader` обязательно должен создаваться per-request, а не быть синглтоном?
>
> - [ ] **A.** Из-за лимита Java на количество объектов в heap — синглтон копит все ключи за время жизни приложения и упирается в OutOfMemoryError.
>
>     **Что на самом деле:** причина не в утечке памяти как таковой, а в логическом смешивании данных разных пользователей. Кэш DataLoader'а не освобождается между запросами, и пользователь A может получить данные, закэшированные при запросе пользователя B.
>
>     **Откуда путаница:** «не делай синглтоном» автоматически ассоциируется с memory leak — это самый частый аргумент против singleton-кэшей. В случае DataLoader проблема глубже — это data isolation, а не resource management.
>
>     **Если бы это было правдой:** проблема решалась бы простым `cache.maximumSize(N)` или TTL. Но даже с ограниченным кэшем шаринг между пользователями остаётся фатальным багом безопасности.
>
> - [x] **B.** Кэш DataLoader'а хранит результаты по ключу без учёта пользователя — синглтон бы возвращал одному пользователю данные, закэшированные при запросе другого, что нарушает изоляцию и приводит к утечке чужих данных.
>
>     **Развёрнутое объяснение:** `DataLoader` имеет два механизма — батчинг (склейка запросов в один tick) и per-request кэш (одинаковый ключ в рамках запроса не дёргает источник дважды). Кэш — это `Map<Key, CompletableFuture<Value>>`. Если DataLoader живёт дольше одного запроса, этот Map становится глобальным: пользователь A запросил `user(id=5)`, и его данные осели в кэше; пользователь B (с другими правами) запрашивает того же `user(id=5)` — и получает данные из кэша, минуя проверки авторизации и фильтрацию в репозитории.
>
>     **Пример:** в Spring for GraphQL `BatchLoaderRegistry` автоматически создаёт DataLoader'ы per-request через `DataLoaderRegistry` в `GraphQlContext`. Если вручную обернуть всё в `@Bean` без скоупа — получишь синглтон со всеми описанными выше последствиями. Правильно: `@Bean @RequestScope` или регистрация через `BatchLoaderRegistry`.
>
>     **Когда применять:** правило железное — DataLoader всегда per-request, без исключений для read-only данных. Даже «публичный каталог товаров» может иметь поля, которые зависят от роли пользователя (цены, доступность, скидки).
>
>     **Подводные камни:** при использовании `WebFlux` и `Mono`/`Flux` важно, чтобы DataLoader жил в правильном reactive context — иначе батчинг не сработает (резолверы выполнятся в разных tick'ах). В `graphql-java` есть `DataLoaderRegistry` который привязан к `ExecutionInput` — это и есть граница per-request. Тестирование: проверяй не только функциональность, но и изоляцию (один и тот же ключ в двух параллельных запросах не должен возвращать одинаковый объект из shared кэша).
>
>     **Связанные вопросы:** [[Q11]], [[Q10]], [[Q15]].
>
> - [ ] **C.** Spring требует регистрировать все `BatchLoader`-бины как prototype, иначе контекст не стартует — это техническое ограничение фреймворка, не более.
>
>     **Что на самом деле:** Spring не имеет такого требования. Технически можно зарегистрировать DataLoader как singleton — контекст поднимется, тесты пройдут, а на production проявится утечка данных между пользователями. Это требование архитектурное (изоляция), а не инфраструктурное.
>
>     **Откуда путаница:** Spring действительно строг к скоупам в некоторых местах (например, `@RequestScope` для HTTP-фильтров). Кажется, что и здесь фреймворк бы «остановил».
>
>     **Если бы это было правдой:** старт приложения с singleton-DataLoader падал бы, и баг с утечкой данных был бы невозможен. На практике приложение стартует нормально — баг проявится только под нагрузкой от разных пользователей.
>
> - [ ] **D.** Per-request скоуп нужен только для DataLoader'ов с мутациями, а для чтений (Query) синглтон даже предпочтительнее ради переиспользования кэша между запросами.
>
>     **Что на самом деле:** DataLoader не используется в мутациях — там нет проблемы N+1 (мутация одна на запрос). Per-request скоуп нужен именно для чтений, и именно для того чтобы кэш НЕ переиспользовался между запросами.
>
>     **Откуда путаница:** интуиция «кэш — это про скорость, чем шире — тем лучше» работает для stateless-кэшей (Redis с TTL, CDN), но ломается для in-memory кэшей с user-specific данными.
>
>     **Если бы это было правдой:** долгоживущий кэш для read-only данных давал бы быстрее ответы — но за счёт потери авторизации на уровне резолверов. Для глобального read-only кэширования есть отдельные инструменты (Caffeine с ключом `(userId, entityId)`, либо APQ — Automatic Persisted Queries).

## Q13. (!) Какие подходы к пагинации существуют в GraphQL?

В `GraphQL` применяются два основных подхода к пагинации:

### 1. Offset-based (страничная)

```graphql
type Query {
    books(page: Int = 0, size: Int = 10): BookPage!
}

type BookPage {
    content: [Book!]!
    totalElements: Int!
    totalPages: Int!
    hasNext: Boolean!
}
```

**Плюсы:** простота, возможность перехода на произвольную страницу.
**Минусы:** нестабильна при вставке/удалении (можно пропустить или продублировать элементы), плохо масштабируется (OFFSET в SQL дорогой).

### 2. Cursor-based (курсорная, Relay-стиль)

```graphql
type Query {
    books(first: Int, after: String, last: Int, before: String): BookConnection!
}

type BookConnection {
    edges: [BookEdge!]!
    pageInfo: PageInfo!
}

type BookEdge {
    node: Book!
    cursor: String!   # opaque cursor (обычно base64 от ID)
}

type PageInfo {
    hasNextPage: Boolean!
    hasPreviousPage: Boolean!
    startCursor: String
    endCursor: String
}
```

**Плюсы:** стабильна при мутациях данных, эффективна для бесконечной прокрутки, хорошо масштабируется.
**Минусы:** нельзя "прыгнуть" на произвольную страницу, сложнее в реализации.

**Рекомендация:** для публичных API и мобильных приложений предпочтительна cursor-based пагинация. Для админ-панелей с таблицами -- offset-based может быть удобнее.

---


> [!mcq] Чем cursor-based пагинация выигрывает у offset-based в `GraphQL`?
>
> - [ ] **A) Cursor-пагинация позволяет перейти на произвольную страницу по номеру, тогда как offset требует обхода всех предыдущих страниц.**
>   - Это перевёрнутая семантика: именно offset-пагинация даёт «прыжок» на страницу по номеру.
>   - Cursor — это opaque-указатель на конкретную позицию, по нему нельзя посчитать «страницу 47».
>   - ПОСЛЕДСТВИЕ: проектируете UI с прыжком на страницу и удивляетесь, что cursor-API такого не поддерживает.
>
> - [x] **B) Cursor-пагинация устойчива к вставкам и удалениям между запросами и не выполняет дорогой SQL OFFSET.**
>   - Cursor (обычно base64 от ID/timestamp) указывает на конкретную запись — добавление новых строк не «сдвигает окно».
>   - Запрос `WHERE id > :cursor LIMIT :n` использует индекс и работает за O(log N), в отличие от `OFFSET N` (O(N)).
>   - МЕХАНИЗМ: Relay Connection возвращает `edges[].cursor` и `pageInfo.endCursor`, который клиент передаёт обратно в `after`.
>   - USE-CASE: бесконечная прокрутка ленты, мобильные приложения, публичные API с активными мутациями.
>   - КОНТЕКСТ: офсетная пагинация уместна только в админ-панелях с относительно статичными данными.
>
> - [ ] **C) Offset-пагинация эффективна при больших N, потому что СУБД использует индекс OFFSET для пропуска строк.**
>   - Индекса OFFSET в РСУБД не существует — `LIMIT 10 OFFSET 100000` всегда читает и отбрасывает 100000 строк.
>   - Это главная причина деградации offset-пагинации на хвосте: latency растёт линейно от номера страницы.
>   - ПОСЛЕДСТВИЕ: на проде с миллионом записей пользователь на 1000-й странице ждёт несколько секунд.
>
> - [ ] **D) Offset-пагинация устойчивее к параллельным вставкам, так как номер страницы фиксирован.**
>   - Прямо противоположно: вставка в начало списка сдвигает все элементы, и пользователь увидит дубликаты или пропуски.
>   - Стабильность даёт именно cursor, который указывает на конкретный объект, а не на абстрактную «страницу».
>   - ПОСЛЕДСТВИЕ: лента новостей с offset-пагинацией показывает один пост дважды при скролле.

## Q14. Что такое Relay-спецификация для пагинации (Connections)?

**Relay Connection Specification** -- стандарт пагинации, разработанный Facebook для библиотеки Relay, который стал де-факто стандартом в `GraphQL`.

**Требования спецификации:**

1. **Connection** -- тип с полями `edges` и `pageInfo`
2. **Edge** -- содержит `node` (сам элемент) и `cursor`
3. **PageInfo** -- содержит `hasNextPage`, `hasPreviousPage`, `startCursor`, `endCursor`
4. **Аргументы:** `first`/`after` (вперёд) и `last`/`before` (назад)

**Реализация в Spring for GraphQL:**

```java
@Controller
public class BookController {

    @QueryMapping
    public Connection<Book> books(
            @Argument int first,
            @Argument String after) {
        // Spring for GraphQL поддерживает scroll-based пагинацию
        ScrollSubrange subrange = ScrollSubrange.create(
            ScrollPosition.forward(after), first, true);
        Window<Book> window = bookRepository.findBy(subrange);
        return ConnectionAdapter.from(window);
    }
}
```

**Global Object Identification** -- ещё одна часть Relay-спецификации: каждый объект должен иметь глобально уникальный `id` и быть доступен через поле `node(id: ID!)`.

---


> [!mcq] Какие три обязательных компонента описывает Relay Connection Specification?
>
> - [ ] **A) `items`, `total`, `currentPage` — стандартный набор полей для постраничного ответа.**
>   - Это типичная offset-пагинация (как в Spring `Page<T>`), но Relay-спецификация устроена иначе.
>   - Relay не оперирует понятиями «страница» и «total» — он работает с курсорами и ребрами графа.
>   - ПОСЛЕДСТВИЕ: клиент Relay/Apollo не сможет автоматически дозагружать данные, потому что не найдёт `pageInfo`.
>
> - [ ] **B) `data`, `errors`, `extensions` — три корневых поля любого GraphQL-ответа.**
>   - Это структура любого GraphQL-response верхнего уровня, а не Connection-типа.
>   - Вопрос про пагинацию внутри `data`, а не про конверт ответа.
>   - ПОСЛЕДСТВИЕ: путаница между транспортным форматом GraphQL и доменной моделью пагинации.
>
> - [x] **C) `Connection` (с `edges` и `pageInfo`), `Edge` (с `node` и `cursor`), `PageInfo` (с `hasNextPage`/`endCursor` и др.).**
>   - `Connection` — это тип-обёртка, `edges: [Edge!]!` — список рёбер, каждое ребро несёт сам объект (`node`) и его курсор.
>   - `PageInfo` сообщает клиенту, есть ли следующая/предыдущая страница и какими курсорами начинать/заканчивать.
>   - МЕХАНИЗМ: аргументы `first`/`after` для прямого обхода и `last`/`before` — для обратного.
>   - USE-CASE: Spring for GraphQL предоставляет `ConnectionAdapter.from(Window<T>)` для конвертации Spring Data `Window` в Relay Connection.
>   - КОНТЕКСТ: спецификация также требует Global Object Identification — каждый node должен иметь уникальный `id` и быть доступен через корневой `node(id: ID!)`.
>
> - [ ] **D) `query`, `variables`, `operationName` — обязательные поля запроса для пагинации.**
>   - Это поля HTTP-запроса GraphQL (тело POST), а не Connection-типа.
>   - Relay не определяет транспортный формат — только структуру типов в схеме.
>   - ПОСЛЕДСТВИЕ: смешивание уровня запроса (transport) и уровня схемы (типы) приведёт к некорректной реализации резолвера.

## Q15. Как устроена обработка ошибок в GraphQL?

В отличие от `REST`, `GraphQL` **всегда возвращает HTTP 200**, даже при ошибках. Ошибки передаются в отдельном массиве `errors`:

```json
{
    "data": {
        "user": null
    },
    "errors": [
        {
            "message": "User not found",
            "locations": [{"line": 2, "column": 3}],
            "path": ["user"],
            "extensions": {
                "classification": "NOT_FOUND",
                "code": "USER_NOT_FOUND"
            }
        }
    ]
}
```

**Типы ошибок:**

| Тип | Когда | HTTP-аналог |
|-----|-------|-------------|
| Синтаксическая | Некорректный запрос | 400 |
| Валидация | Поле не существует в схеме | 400 |
| Runtime | Ошибка в резолвере | 500 |
| Бизнес-логика | Нет прав, не найден | 4xx |

**Spring for GraphQL -- обработка ошибок:**

```java
@Controller
public class BookController {

    @QueryMapping
    public Book book(@Argument Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
    }
}

// Кастомный exception resolver
@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(
            Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof BookNotFoundException) {
            return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.NOT_FOUND)
                .build();
        }
        return null; // вернёт стандартную ошибку
    }
}
```

**Частичные ответы** -- уникальная особенность `GraphQL`: ответ может содержать и `data`, и `errors` одновременно. Успешно разрешённые поля вернут данные, а сломавшиеся -- `null` с ошибкой.

---


> [!mcq] В чём ключевое отличие модели ошибок GraphQL от REST?
>
> - [ ] **A) GraphQL возвращает HTTP-код в зависимости от типа ошибки (404, 500, 403) — точно как REST.**
>   - GraphQL почти всегда отвечает HTTP 200 — даже при бизнес-ошибках и runtime-исключениях.
>   - Не-200 коды используются только при сбоях транспортного уровня (400 — невалидный JSON, 415 — неверный Content-Type).
>   - ПОСЛЕДСТВИЕ: мониторинг по HTTP-статусам в Grafana покажет «всё зелёное», пока 100% запросов возвращают ошибки в `errors`.
>
> - [ ] **B) Каждая ошибка завершает выполнение всего запроса — клиент получает либо `data`, либо `errors`, но не оба.**
>   - Это REST-семантика: либо успешный ответ, либо ошибочный.
>   - В GraphQL частичные ответы — фундаментальная особенность: ошибка в одном резолвере не отменяет уже разрешённые поля.
>   - ПОСЛЕДСТВИЕ: клиент, проверяющий только `if (response.errors) throw`, теряет валидные данные из `data`.
>
> - [ ] **C) Ошибки нельзя кастомизировать — спецификация GraphQL запрещает добавлять свои поля.**
>   - Спецификация явно предусматривает поле `extensions` для произвольных метаданных (классификация, код, traceId).
>   - Spring for GraphQL предлагает `DataFetcherExceptionResolverAdapter` для маппинга exception → GraphQLError с нужным `errorType` и `extensions`.
>   - ПОСЛЕДСТВИЕ: команда отказывается от типизированных бизнес-ошибок и теряет UX-возможности (показать «email уже занят» вместо «Internal Error»).
>
> - [x] **D) GraphQL поддерживает частичные ответы: успешные поля попадают в `data`, а сломавшиеся — в `errors` с `path`, указывающим точное место.**
>   - Ответ может одновременно содержать `data` с null-значениями для сломавшихся узлов и массив `errors` с `path: ["user", "posts", 2, "author"]`.
>   - МЕХАНИЗМ: каждая ошибка несёт `message`, `locations` (координаты в запросе), `path` (путь в ответе) и `extensions.classification` (NOT_FOUND, FORBIDDEN и т.д.).
>   - USE-CASE: дашборд с виджетами — если один виджет упал, остальные всё равно отрисуются, потому что их данные пришли в `data`.
>   - КОНТЕКСТ: в Spring for GraphQL для частичного ответа резолвер должен бросать исключение, а не возвращать null с побочной записью в errors-buffer.
>   - HTTP всегда 200 — это сознательное архитектурное решение: транспорт отделён от семантики бизнес-ошибок.

## Q16. Что такое интроспекция и зачем она нужна?

**Интроспекция** -- возможность запрашивать у `GraphQL`-сервера информацию о его схеме. Это встроенная функциональность, позволяющая клиентам динамически узнавать структуру API.

```graphql
# Запрос всех типов в схеме
{
    __schema {
        types {
            name
            kind
        }
    }
}

# Запрос полей конкретного типа
{
    __type(name: "User") {
        name
        fields {
            name
            type {
                name
                kind
            }
        }
    }
}
```

**Применения интроспекции:**
- **IDE и инструменты:** GraphiQL, Apollo Studio, Insomnia используют интроспекцию для автодополнения
- **Генерация кода:** клиентские генераторы (codegen) строят типы по схеме
- **Документация:** автоматическая генерация документации API

**Безопасность:** в production интроспекцию **рекомендуется отключать**, так как она раскрывает всю структуру API потенциальному злоумышленнику.

**Spring for GraphQL -- отключение интроспекции:**
```yaml
spring:
  graphql:
    schema:
      introspection:
        enabled: false
```

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. (!) Когда выбирать GraphQL, а когда REST? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Выбирайте `GraphQL`, когда:**
- Разнородные клиенты (мобильное приложение, SPA, IoT) с разными потребностями в данных
- Сложные связанные данные (графовая модель)
- Частые изменения требований к API на стороне фронтенда
- Нужна строгая типизация и самодокументирующийся API
- Проблемы over-fetching/under-fetching критичны (ограниченный трафик на мобильных)

**Выбирайте `REST`, когда:**
- Простой CRUD без сложных связей
- Критично HTTP-кэширование (CDN, прокси)
- Загрузка/скачивание файлов как основной сценарий
- Команда не имеет опыта с `GraphQL`
- Микросервисная коммуникация (здесь лучше подходит [gRPC](grpc-interview.md))
- Публичный API с широкой аудиторией (REST более распространён)

**Гибридный подход** на практике распространён: `REST` для простых CRUD-операций и загрузки файлов, `GraphQL` для сложных запросов к данным, [gRPC](grpc-interview.md) для inter-service коммуникации.

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Какие основные проблемы REST решает GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**1. Over-fetching** -- сервер возвращает избыточные данные:
```
REST:  GET /users/1  →  { id, name, email, address, phone, avatar, settings, ... }
GraphQL: query { user(id: 1) { name, email } }  →  только нужные поля
```

**2. Under-fetching** -- для одного экрана нужно несколько запросов:
```
REST:  GET /users/1  →  GET /users/1/posts  →  GET /users/1/followers
GraphQL: один запрос — все данные за раз
```

**3. Версионирование** -- в `REST` приходится создавать `/v1/`, `/v2/`, в `GraphQL` схема эволюционирует:
```graphql
type User {
    name: String!       # новое поле
    fullName: String @deprecated(reason: "Используйте name")  # старое помечено
}
```

**4. Документация** -- схема `GraphQL` является самодокументирующимся контрактом, инструменты автоматически генерируют документацию через интроспекцию.

**Но `GraphQL` создаёт новые проблемы:** сложность кэширования, риски безопасности (произвольные запросы), сложность мониторинга (один эндпоинт), необходимость защиты от злонамеренных запросов.

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Как работает Spring for GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**`Spring for GraphQL`** -- официальный проект Spring для интеграции `GraphQL` в Spring-приложения. Пришёл на замену сторонним библиотекам (GraphQL Java Tools, GraphQL SPQR).

**Подключение:**
```groovy
// build.gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-graphql'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // Для WebSocket subscriptions:
    // implementation 'org.springframework.boot:spring-boot-starter-websocket'
}
```

**Структура проекта:**
```
src/main/resources/
  graphql/
    schema.graphqls      # основная схема
    book.graphqls        # дополнительные файлы схемы (автоматически объединяются)
```

**Конфигурация:**
```yaml
spring:
  graphql:
    graphiql:
      enabled: true         # GraphiQL UI по /graphiql
    schema:
      locations: classpath:graphql/
      printer:
        enabled: true       # endpoint для получения SDL
    websocket:
      path: /graphql        # путь для WebSocket subscriptions
```

```mermaid
graph TD
    C[HTTP Client] -->|POST /graphql| SC[Spring Controller Layer]
    SC --> GE[GraphQL Engine]
    GE --> QM["@QueryMapping"]
    GE --> MM["@MutationMapping"]
    GE --> SM["@SchemaMapping"]
    GE --> BM["@BatchMapping"]
    QM --> S[Service Layer]
    MM --> S
    SM --> S
    BM --> S
    S --> R[(Repository)]
```

**Ключевые аннотации:**
- `@QueryMapping` -- резолвер для Query-полей
- `@MutationMapping` -- резолвер для Mutation-полей
- `@SubscriptionMapping` -- резолвер для Subscription-полей
- `@SchemaMapping` -- резолвер для поля произвольного типа
- `@BatchMapping` -- батч-резолвер (решает N+1)
- `@Argument` -- привязка аргументов GraphQL к параметрам метода

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. Как определить контроллер в Spring for GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Аннотированный контроллер (основной подход):**

```java
@Controller
public class BookController {

    private final BookService bookService;

    // Query: books(page: Int, size: Int): BookPage!
    @QueryMapping
    public List<Book> books(@Argument int page, @Argument int size) {
        return bookService.findAll(PageRequest.of(page, size));
    }

    // Query: book(id: ID!): Book
    @QueryMapping
    public Book book(@Argument Long id) {
        return bookService.findById(id);
    }

    // Mutation: createBook(input: CreateBookInput!): Book!
    @MutationMapping
    public Book createBook(@Argument CreateBookInput input) {
        return bookService.create(input);
    }

    // Поле Book.author (вместо отдельного резолвера)
    @SchemaMapping(typeName = "Book")
    public Author author(Book book) {
        return authorService.findById(book.getAuthorId());
    }

    // Batch-вариант для решения N+1
    @BatchMapping(typeName = "Book")
    public Map<Book, Author> author(List<Book> books) {
        List<Long> authorIds = books.stream()
            .map(Book::getAuthorId)
            .distinct()
            .toList();
        Map<Long, Author> authors = authorService.findAllById(authorIds)
            .stream()
            .collect(Collectors.toMap(Author::getId, a -> a));
        return books.stream()
            .collect(Collectors.toMap(b -> b, b -> authors.get(b.getAuthorId())));
    }
}
```

**Subscription (с WebSocket):**

```java
@Controller
public class MessageController {

    @SubscriptionMapping
    public Flux<Message> messageAdded(@Argument Long chatId) {
        return messageService.subscribe(chatId);
    }
}
```

**Контекст безопасности** -- Spring Security интегрируется прозрачно:

```java
@QueryMapping
public User currentUser(@AuthenticationPrincipal UserDetails user) {
    return userService.findByUsername(user.getUsername());
}
```

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Как подключить DataLoader в Spring for GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**`Spring for GraphQL`** предоставляет два подхода:

### 1. @BatchMapping (рекомендуемый)

Самый простой способ -- Spring автоматически создаёт `DataLoader`:

```java
@Controller
public class BookController {

    @BatchMapping(typeName = "Book", field = "author")
    public Mono<Map<Book, Author>> author(List<Book> books) {
        Set<Long> ids = books.stream()
            .map(Book::getAuthorId)
            .collect(Collectors.toSet());
        return authorRepository.findAllById(ids)
            .collectMap(Author::getId)
            .map(authorsById -> books.stream()
                .collect(Collectors.toMap(
                    b -> b,
                    b -> authorsById.get(b.getAuthorId())
                )));
    }
}
```

### 2. Ручная регистрация через BatchLoaderRegistry

Для большего контроля:

```java
@Configuration
public class DataLoaderConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(
            BatchLoaderRegistry registry) {
        registry.forTypePair(Long.class, Author.class)
            .registerMappedBatchLoader((authorIds, env) ->
                Flux.fromIterable(authorRepository.findAllById(authorIds))
                    .collectMap(Author::getId));
        return wiringBuilder -> {};
    }
}
```

Использование в контроллере:

```java
@SchemaMapping(typeName = "Book")
public CompletableFuture<Author> author(
        Book book,
        DataLoader<Long, Author> authorDataLoader) {
    return authorDataLoader.load(book.getAuthorId());
}
```

**`@BatchMapping` предпочтительнее**, так как Spring сам управляет жизненным циклом `DataLoader` и не требует ручной конфигурации.

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. (!) Какие угрозы безопасности специфичны для GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`GraphQL` API имеет уникальный набор угроз, связанных с гибкостью запросов:

**1. Глубоко вложенные запросы (Depth Attack):**
```graphql
# Злоумышленник может создать запрос, который рекурсивно обходит связи
query {
    user(id: 1) {
        friends {
            friends {
                friends {
                    friends { ... }  # бесконечная вложенность
                }
            }
        }
    }
}
```

**2. Широкие запросы (Breadth Attack):**
```graphql
query {
    a1: users(first: 1000) { name }
    a2: users(first: 1000) { name }
    # ... тысячи алиасов
    a100: users(first: 1000) { name }
}
```

**3. Интроспекция** -- раскрытие всей схемы API злоумышленнику.

**4. Injection** -- если аргументы не валидируются, возможны SQL/NoSQL injection.

**5. Batching attack** -- множество мутаций (например, попыток входа) в одном запросе.

**Меры защиты:**

| Мера | Описание |
|------|----------|
| Depth limiting | Ограничение максимальной глубины запроса |
| Query complexity | Оценка стоимости запроса по весам полей |
| Rate limiting | Ограничение частоты запросов |
| Persisted queries | Белый список разрешённых запросов |
| Timeout | Ограничение времени выполнения |
| Отключение интроспекции | В production |
| Валидация входных данных | Для всех аргументов |
| Аутентификация/авторизация | На уровне резолверов |

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. Как ограничить глубину и сложность запросов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

### Ограничение глубины (Max Depth)

```java
@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer configurer() {
        return builder -> {};
    }

    @Bean
    public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
        return builder -> builder.configureGraphQl(graphQlBuilder ->
            graphQlBuilder.executionIdProvider(ExecutionIdProvider.DEFAULT)
        );
    }
}
```

С использованием `graphql-java` instrumentation:

```java
@Bean
public Instrumentation maxQueryDepthInstrumentation() {
    return new MaxQueryDepthInstrumentation(10); // макс. глубина = 10
}

@Bean
public Instrumentation maxQueryComplexityInstrumentation() {
    return new MaxQueryComplexityInstrumentation(200); // макс. сложность = 200
}
```

### Анализ сложности (Query Complexity)

Каждому полю назначается "вес". Суммарная сложность запроса не должна превышать лимит:

```graphql
# Пример весов:
# скалярное поле = 1
# объектное поле = 2
# список = 5 * кол-во элементов

query {
    users(first: 100) {  # 5 * 100 = 500
        name             # 1
        posts {          # 2
            title        # 1
        }
    }
}
# Итого: 500 + 100*(1 + 2 + 1) = 900
```

**Директивы `@cost` и `@listSize`** (из спецификации GraphQL Demand Control):
```graphql
type Query {
    users(first: Int): [User!]! @listSize(assumedSize: 50)
}

type User {
    name: String!
    profile: Profile! @cost(weight: 3)  # дорогая операция
}
```

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Почему кэширование в GraphQL сложнее, чем в REST? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В `REST` каждый ресурс имеет уникальный URL, что позволяет использовать стандартный HTTP-кэш (браузер, CDN, прокси). В `GraphQL` все запросы идут на один эндпоинт `POST /graphql`, и HTTP-кэш не работает.

**Проблемы:**
- Один URL для всех запросов -- HTTP-кэш по URL бесполезен
- `POST`-запросы не кэшируются по умолчанию
- Каждый клиент запрашивает разный набор полей
- Один объект может быть частью разных запросов

**Решения:**

**1. Client-side нормализованный кэш** (Apollo Client, Relay):
- Каждый объект с `id` + `__typename` кэшируется отдельно
- При повторном запросе данные берутся из кэша
- При мутации автоматически обновляются связанные запросы

**2. Server-side кэширование по полям:**
```java
@QueryMapping
@Cacheable("books")
public List<Book> books() {
    return bookRepository.findAll();
}
```

**3. Persisted Queries** -- позволяют использовать `GET`-запросы и HTTP-кэш.

**4. CDN-кэширование** -- возможно с Automatic Persisted Queries (APQ), где запрос идентифицируется хэшем.

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Что такое Persisted Queries? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Persisted Queries** -- механизм, при котором `GraphQL`-запросы регистрируются на сервере заранее, а клиент отправляет только идентификатор (хэш) запроса вместо полного текста.

**Процесс:**
```mermaid
sequenceDiagram
    participant C as Client
    participant S as Server

    Note over C,S: Automatic Persisted Queries (APQ)
    C->>S: GET /graphql?extensions={"persistedQuery":{"sha256Hash":"abc123"}}
    S-->>C: PersistedQueryNotFound
    C->>S: POST /graphql {query: "...", extensions: {"persistedQuery":{"sha256Hash":"abc123"}}}
    S-->>C: {data: {...}} + сохраняет хэш
    C->>S: GET /graphql?extensions={"persistedQuery":{"sha256Hash":"abc123"}}
    S-->>C: {data: {...}} из кэша
```

**Преимущества:**
- **Безопасность:** сервер принимает только зарегистрированные запросы (белый список)
- **Производительность:** меньший размер запросов, возможность `GET`-запросов
- **HTTP-кэширование:** `GET`-запросы можно кэшировать на CDN
- **Защита от DoS:** произвольные запросы отклоняются

**Режимы:**
- **Automatic Persisted Queries (APQ)** -- клиент отправляет хэш, при промахе -- полный запрос (Apollo)
- **Registered Operations** -- только заранее зарегистрированные запросы, произвольные запрещены (строгий режим для production)

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Что такое GraphQL Federation? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**`GraphQL Federation`** -- архитектурный подход для построения единого `GraphQL` API из нескольких микросервисов, каждый из которых управляет своей частью схемы.

```mermaid
graph TD
    C[Client] --> GW[GraphQL Gateway<br/>Apollo Router / Supergraph]
    GW --> US[Users Service<br/>User, Profile]
    GW --> PS[Products Service<br/>Product, Category]
    GW --> OS[Orders Service<br/>Order, Payment]

    style GW fill:#f9f,stroke:#333
```

**Ключевые концепции:**

**1. Subgraph** -- отдельный `GraphQL`-сервис со своей частью схемы:
```graphql
# Users Subgraph
type User @key(fields: "id") {
    id: ID!
    name: String!
    email: String!
}
```

**2. Entity** -- тип, который может быть расширен другими subgraph'ами. Директива `@key` определяет уникальный идентификатор:
```graphql
# Orders Subgraph — расширяет User
type User @key(fields: "id") {
    id: ID!
    orders: [Order!]!  # добавляет поле orders к User
}
```

**3. Gateway (Router)** -- точка входа, которая объединяет subgraph'ы в единую схему (supergraph), маршрутизирует запросы и собирает ответы.

**Преимущества:**
- Каждая команда владеет своим subgraph (team ownership)
- Независимый деплой сервисов
- Единый API для клиентов
- Инкрементальная миграция с монолита

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. Чем Federation отличается от Schema Stitching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**`Schema Stitching`** и **`Federation`** решают одну задачу -- объединение нескольких `GraphQL`-сервисов в один API, но принципиально разными способами.

| Аспект | Schema Stitching | Federation |
|--------|-----------------|------------|
| Где логика объединения | В gateway (централизованно) | В subgraph'ах (декларативно) |
| Связи между типами | Gateway знает, как связать | `@key`, `@extends` в схемах сервисов |
| Независимость сервисов | Низкая (gateway всё контролирует) | Высокая (каждый сервис самодостаточен) |
| Масштабируемость | Плохая (gateway = bottleneck) | Хорошая (gateway тонкий) |
| Команда разработки | Нужна централизованная | Каждая команда независимо |
| Зрелость | Старый подход | Современный стандарт (Apollo) |

**Schema Stitching** -- gateway загружает схемы удалённых сервисов и вручную "сшивает" их:
```javascript
// Gateway должен знать, как связать User с Orders
const schema = stitchSchemas({
    subschemas: [usersSchema, ordersSchema],
    typeMergingConfig: { ... }  // ручная конфигурация
});
```

**Federation** -- сервисы декларативно описывают, как они расширяют общие типы:
```graphql
# Каждый subgraph самодостаточен, gateway разбирается автоматически
type User @key(fields: "id") {
    id: ID!
    orders: [Order!]!
}
```

**Рекомендация:** Federation -- предпочтительный подход для новых проектов. Schema Stitching оправдан для интеграции legacy-сервисов.

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. Schema-first vs Code-first: в чём разница? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Два подхода к разработке `GraphQL` API:

### Schema-first (SDL-first)

Сначала пишется SDL-схема, затем реализуются резолверы:

```graphql
# schema.graphqls -- пишется первым
type Query {
    book(id: ID!): Book
}

type Book {
    id: ID!
    title: String!
    author: Author!
}
```

```java
// Контроллер привязывается к схеме
@Controller
public class BookController {
    @QueryMapping
    public Book book(@Argument Long id) { ... }
}
```

**Плюсы:** схема как контракт, удобно для командной работы, фронтенд может начать работу до реализации бэкенда.
**Минусы:** дублирование (схема + Java-классы), ошибки рассинхронизации.

### Code-first

Схема генерируется из аннотированного Java-кода:

```java
// Схема генерируется автоматически из аннотаций
@GraphQLApi
public class BookApi {
    @Query
    public Book book(@Argument Long id) { ... }
}

@Type
public class Book {
    private Long id;
    private String title;
    private Author author;
}
```

**Плюсы:** единственный источник правды -- код, нет рассинхронизации, IDE-поддержка.
**Минусы:** схема менее читаема для не-Java разработчиков, сложнее использовать как контракт.

| Критерий | Schema-first | Code-first |
|----------|-------------|------------|
| Spring for GraphQL | Да (основной подход) | Нет (нужна SPQR или DGS) |
| Netflix DGS | Да | Да |
| GraphQL SPQR | Нет | Да (основной подход) |
| Команда | Кросс-функциональная | Java-only |

**Spring for GraphQL** использует **schema-first** подход, что считается рекомендуемой практикой.

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. Как реализовать загрузку файлов в GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Спецификация `GraphQL` не определяет механизм загрузки файлов. Существует несколько подходов:

### 1. Multipart Request (graphql-multipart-request-spec)

```graphql
scalar Upload

type Mutation {
    uploadFile(file: Upload!): FileInfo!
}
```

```java
// Spring for GraphQL не поддерживает Upload из коробки
// Используется с DGS или кастомным скаляром
@MutationMapping
public FileInfo uploadFile(@Argument MultipartFile file) {
    String path = storageService.store(file);
    return new FileInfo(path, file.getOriginalFilename(), file.getSize());
}
```

### 2. Signed URL (рекомендуемый подход)

```graphql
type Mutation {
    createUploadUrl(filename: String!, contentType: String!): UploadUrl!
}

type UploadUrl {
    uploadUrl: String!     # pre-signed URL для PUT
    fileId: String!
}
```

```mermaid
sequenceDiagram
    participant C as Client
    participant G as GraphQL API
    participant S3 as Object Storage

    C->>G: mutation createUploadUrl(filename: "photo.jpg")
    G-->>C: {uploadUrl: "https://s3.../presigned", fileId: "abc"}
    C->>S3: PUT uploadUrl + файл
    S3-->>C: 200 OK
    C->>G: mutation attachFile(fileId: "abc", postId: "1")
    G-->>C: {success: true}
```

**Рекомендация:** используйте signed URL для файлов. `GraphQL` оптимизирован для структурированных данных, а не для бинарных потоков. Подробнее о работе с REST-эндпоинтами для загрузки файлов -- в [вопросах по HTTP & REST](http-rest-interview.md).

---


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. (!) Как организовать батчинг запросов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Батчинг** позволяет клиенту отправить несколько `GraphQL`-операций в одном HTTP-запросе:

```json
[
    {
        "query": "query { user(id: 1) { name } }",
        "operationName": "GetUser"
    },
    {
        "query": "query { books { title } }",
        "operationName": "GetBooks"
    }
]
```

Сервер возвращает массив ответов в том же порядке:
```json
[
    { "data": { "user": { "name": "Alice" } } },
    { "data": { "books": [{ "title": "GraphQL in Action" }] } }
]
```

**Преимущества:**
- Уменьшение количества HTTP round-trips
- Снижение overhead на установку соединений
- Особенно полезно для мобильных клиентов с высоким latency

**Риски и ограничения:**
- **Безопасность:** злоумышленник может отправить сотни операций в одном запросе (batching attack)
- **Timeout:** одна медленная операция блокирует весь batch
- **Сложность мониторинга:** сложнее отслеживать производительность отдельных операций

**Защита:**
```java
// Ограничение количества операций в batch
@Bean
public WebGraphQlInterceptor batchLimitInterceptor() {
    return (request, chain) -> {
        // Проверка размера batch (для кастомного batch endpoint)
        return chain.next(request);
    };
}
```

**Альтернатива батчингу:** `@defer` и `@stream` директивы (экспериментальные), которые позволяют серверу отправлять части ответа по мере готовности, не дожидаясь завершения всех резолверов. Подробнее об оптимизации API-запросов -- в [вопросах по микросервисам](../architecture/microservices-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q31. (!) Как работают GraphQL Subscriptions и как их реализовать в Spring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**GraphQL Subscriptions** — механизм получения данных в реальном времени. Клиент подписывается на событие, сервер отправляет обновления при каждом наступлении события.

**Транспорт:** обычно `WebSocket` (протокол `graphql-ws` или устаревший `subscriptions-transport-ws`).

**Схема:**

```graphql
type Subscription {
    orderStatusChanged(orderId: ID!): OrderStatusEvent!
    newOrderPlaced: Order!
}

type OrderStatusEvent {
    orderId: ID!
    oldStatus: OrderStatus!
    newStatus: OrderStatus!
    timestamp: String!
}
```

**Реализация в Spring for GraphQL:**

```java
@Controller
public class OrderSubscriptionController {

    private final Sinks.Many<OrderStatusEvent> orderEventSink =
        Sinks.many().multicast().onBackpressureBuffer();

    // Resolver для подписки
    @SubscriptionMapping
    public Flux<OrderStatusEvent> orderStatusChanged(@Argument String orderId) {
        return orderEventSink.asFlux()
            .filter(event -> event.orderId().equals(orderId))
            .timeout(Duration.ofMinutes(30));  // авто-отключение через 30 мин
    }

    // Публикация события при изменении статуса заказа
    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        orderEventSink.tryEmitNext(new OrderStatusEvent(
            event.orderId(), event.oldStatus(), event.newStatus(),
            Instant.now().toString()
        ));
    }
}
```

**Конфигурация WebSocket в Spring:**

```yaml
spring:
  graphql:
    websocket:
      path: /graphql-ws
      connection-init-timeout: 60s
```

**Клиентский запрос (через `graphql-ws` протокол):**

```javascript
const client = createClient({ url: 'ws://localhost:8080/graphql-ws' });

client.subscribe(
  {
    query: `subscription OnOrderStatus($orderId: ID!) {
      orderStatusChanged(orderId: $orderId) {
        oldStatus newStatus timestamp
      }
    }`,
    variables: { orderId: '123' }
  },
  {
    next: (data) => console.log('Status update:', data),
    error: (err) => console.error('Subscription error:', err),
    complete: () => console.log('Subscription complete')
  }
);
```

**Subscription vs SSE vs WebSocket:**

| Критерий | GraphQL Subscription | SSE | WebSocket |
|----------|---------------------|-----|-----------|
| Направление | Двунаправленное (WS) | Сервер → клиент | Двунаправленное |
| Типизация | Да (схема GraphQL) | Нет | Нет |
| Сложность | Средняя | Низкая | Средняя |
| Применение | Real-time UI обновления | Уведомления, feeds | Чат, игры |


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q32. Что такое @defer и @stream директивы в GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**`@defer`** и **`@stream`** — экспериментальные директивы, позволяющие серверу отправлять части ответа **инкрементально** по мере готовности, не дожидаясь полного выполнения всех резолверов.

**`@defer` — отложить фрагмент:**

```graphql
query GetOrder {
  order(id: "123") {
    id
    status           # ← возвращается сразу

    ... @defer {
      customer {     # ← возвращается позже (медленный сервис)
        name
        email
      }
    }

    ... @defer(label: "analytics") {
      analytics {   # ← возвращается ещё позже
        viewCount
        lastViewed
      }
    }
  }
}
```

**Ответ сервера (multipart/mixed):**

```
--graphql
Content-Type: application/json

{"data": {"order": {"id": "123", "status": "SHIPPED"}}, "hasNext": true}

--graphql
Content-Type: application/json

{"incremental": [{"path": ["order"], "data": {"customer": {"name": "Alice"}}}], "hasNext": true}

--graphql
Content-Type: application/json

{"incremental": [{"path": ["order"], "label": "analytics", "data": {"analytics": {...}}}], "hasNext": false}
--graphql--
```

**`@stream` — потоковые списки:**

```graphql
query GetOrders {
  orders @stream {  # каждый элемент отправляется по мере готовности
    id
    status
  }
}
```

**Статус поддержки:**
- Директивы в статусе **RFC** в GraphQL spec (не финализированы)
- Spring for GraphQL 1.2+ имеет экспериментальную поддержку
- Требует HTTP/2 или chunked transfer encoding

**Когда полезно:**
- Страница со множеством независимых виджетов (dashboard)
- Длинные списки данных
- Когда часть данных доступна быстро, часть — медленно


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q33. (!) Как решить проблему N+1 с помощью DataLoader в Spring for GraphQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Проблема N+1:** при загрузке N заказов с клиентами — N отдельных SQL-запросов к таблице пользователей.

```graphql
query {
  orders {           # 1 запрос → 10 заказов
    id
    customer {       # 10 запросов → 10 пользователей (N+1!)
      name
    }
  }
}
```

**DataLoader** — батчинг загрузчик: собирает все `customerId` за один "тик" event loop и делает один запрос `IN (id1, id2, ..., idN)`.

**Полная реализация в Spring for GraphQL:**

```java
// 1. Определяем BatchLoaderRegistry
@Configuration
public class DataLoaderConfig {

    @Bean
    public BatchLoaderRegistry batchLoaderRegistry(CustomerRepository repo) {
        return BatchLoaderRegistry.newInstance()
            .forTypePair(Long.class, CustomerDto.class)
            .withName("customerLoader")
            .registerMappedBatchLoader((customerIds, env) -> {
                // Один запрос вместо N
                List<Customer> customers = repo.findAllById(customerIds);
                return Mono.just(customers.stream()
                    .collect(Collectors.toMap(
                        c -> c.getId(),
                        c -> new CustomerDto(c.getId(), c.getName(), c.getEmail())
                    )));
            });
    }
}

// 2. Используем в резолвере
@Controller
public class OrderController {

    @QueryMapping
    public List<Order> orders() {
        return orderRepository.findAll();
    }

    // Резолвер поля customer — вызывается для каждого Order
    @SchemaMapping(typeName = "Order", field = "customer")
    public CompletableFuture<CustomerDto> customer(
            Order order,
            DataLoader<Long, CustomerDto> customerLoader) {
        // DataLoader накапливает IDs и делает batch-запрос
        return customerLoader.load(order.getCustomerId());
    }
}
```

**Что происходит под капотом:**

```mermaid
sequenceDiagram
    participant GQL as GraphQL Engine
    participant DL as DataLoader
    participant DB as Database

    GQL->>DL: load(customerId=1)
    GQL->>DL: load(customerId=2)
    GQL->>DL: load(customerId=3)
    Note over DL: Batch dispatch (конец тика)
    DL->>DB: SELECT * FROM customers WHERE id IN (1, 2, 3)
    DB-->>DL: [Customer1, Customer2, Customer3]
    DL-->>GQL: Customer1
    DL-->>GQL: Customer2
    DL-->>GQL: Customer3
```

**Результат:** вместо 1 + N запросов — ровно 2 запроса (1 на заказы + 1 батч на клиентов).

**Кэширование в DataLoader:** по умолчанию DataLoader кэширует результаты в рамках одного запроса — повторные `load(id)` возвращают закэшированный результат без повторного обращения к БД.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q34. Что такое Persisted Queries и зачем они нужны? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Persisted Queries** — механизм, при котором GraphQL-запрос сохраняется на сервере под хешем (APQ, Automatic Persisted Queries). Клиент отправляет только хеш; сервер возвращает `PERSISTED_QUERY_NOT_FOUND`, если не знает запрос, и тогда клиент досылает полный текст.

**Зачем нужны:**
- **Безопасность** — разрешить только заранее зарегистрированные запросы (whitelist), запретить произвольный GraphQL
- **Производительность** — экономия трафика: вместо 5 KB текста запроса — 64-байтный SHA-256
- **Кэширование** — GET-запрос с хешем можно кэшировать на CDN как обычный REST-ресурс

**Два варианта реализации:**

| Вариант | Описание |
|---------|----------|
| APQ (Automatic) | Клиент посылает хеш, сервер кэширует текст; Apollo Client поддерживает из коробки |
| Static Persisted Queries | Запросы компилируются во время сборки клиента и регистрируются на сервере; в проде клиент шлёт только ID |

**Пример APQ-запроса:**
```http
GET /graphql?extensions={"persistedQuery":{"version":1,"sha256Hash":"abc123..."}}
```

**В Spring for GraphQL:** поддержки APQ из коробки нет — реализуют через кастомный `WebGraphQlInterceptor`, кэширующий запросы в Redis по хешу.

**Когда использовать:** публичные GraphQL API с высоким трафиком; приложения с требованиями безопасности (финтех); CDN-кэширование read-only запросов.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q35. Как масштабировать GraphQL Subscriptions через WebSocket? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**GraphQL Subscriptions** работают поверх `WebSocket` (реже — `SSE`). Масштабирование сложнее, чем у обычных запросов, так как `WebSocket` — stateful соединение.

**Проблема горизонтального масштабирования:**

```
Client --WS--> Node1   \
                        > Разные ноды не знают о подписках друг друга
Client --WS--> Node2   /
```

**Решения:**

1. **Sticky sessions** — все WebSocket-соединения клиента попадают на одну ноду (nginx `ip_hash`). Простое решение, но неравномерная нагрузка.

2. **Pub/Sub через Redis** — каждый сервер публикует события в Redis-канал, все серверы подписаны и рассылают нужным клиентам:

```java
// Spring WebSocket + Redis pub/sub
@Service
public class SubscriptionService {
    private final RedisTemplate<String, Object> redisTemplate;

    public Flux<OrderStatus> orderUpdates(String orderId) {
        return Flux.create(sink -> {
            MessageListenerAdapter adapter = new MessageListenerAdapter(
                (MessageListener) (message, pattern) -> {
                    OrderStatus status = deserialize(message.getBody());
                    sink.next(status);
                });
            redisTemplate.execute(connection -> {
                connection.subscribe(adapter, ("order:" + orderId).getBytes());
                return null;
            }, true);
        });
    }
}
```

3. **Dedicated WebSocket Gateway** — специализированный сервис для Subscriptions (например, Hasura, Ably); основной сервис публикует события через Kafka/Redis.

**Рекомендации:**
- Ограничивать количество активных подписок на пользователя
- Реализовать heartbeat (`keep-alive`) для детектирования разорванных соединений
- Мониторить активные WebSocket-соединения и память
- Рассмотреть SSE как более простую альтернативу для односторонних обновлений


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q36. Чем Schema Stitching отличается от Federation и почему Federation предпочтительнее? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Schema Stitching** — старый подход: шлюз сам собирает единую схему из нескольких GraphQL API, делегируя запросы в нужный сервис. Реализован в `graphql-tools`.

**Apollo Federation** — современный подход: каждый сервис (subgraph) публикует собственную схему с аннотациями (`@key`, `@extends`); шлюз (Router) автоматически строит federated schema.

**Сравнение:**

| Критерий | Schema Stitching | Apollo Federation |
|----------|-----------------|-------------------|
| Координация | На шлюзе — шлюз знает всё | Распределённая — каждый subgraph декларирует свои типы |
| Добавление нового сервиса | Изменение конфига шлюза | Subgraph регистрируется в Registry |
| Отказоустойчивость | Шлюз — единая точка сложности | Supergraph schema строится автоматически |
| Поддержка | Deprecated в пользу Federation | Активно развивается, CNCF-проект |
| Независимые деплои | Сложно | Да, каждый subgraph деплоится отдельно |

**Пример Federation 2:**

```graphql
# users subgraph
type User @key(fields: "id") {
  id: ID!
  name: String!
  email: String!
}

# orders subgraph — расширяет тип User
type User @key(fields: "id") @extends {
  id: ID! @external
  orders: [Order!]!
}

type Order {
  id: ID!
  total: Float!
}
```

**Почему Federation предпочтительнее:** каждая команда владеет своим subgraph и деплоит независимо; Router (Apollo Router) обеспечивает query planning; есть Schema Registry для управления версиями контракта.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q37. Как реализовать rate limiting в GraphQL через ограничение сложности запроса? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В GraphQL нет стандартного механизма rate limiting по URL. Ограничение строится на двух уровнях:

**1. Query Depth — ограничение глубины вложенности:**

```graphql
# Потенциально опасный запрос (глубина 10+)
{ user { friends { friends { friends { friends { ... } } } } } }
```

```java
// Spring for GraphQL — кастомный Instrumentation
@Component
public class MaxDepthInstrumentation extends SimplePerformantInstrumentation {
    private static final int MAX_DEPTH = 7;

    @Override
    public DocumentAndVariables instrumentDocumentAndVariables(
            DocumentAndVariables dav, InstrumentationDocumentParameters params,
            InstrumentationState state) {
        int depth = calculateDepth(dav.getDocument());
        if (depth > MAX_DEPTH) {
            throw new QueryDepthException("Query depth " + depth + " exceeds max " + MAX_DEPTH);
        }
        return dav;
    }
}
```

**2. Query Complexity — штрафные очки за поля:**

```java
// Назначаем веса полям схемы
FieldComplexityCalculator calculator = (env, childComplexity) -> {
    String field = env.getFieldDefinition().getName();
    return switch (field) {
        case "users" -> 10 + childComplexity * 2;  // дорогой список
        case "user"  -> 1 + childComplexity;
        default      -> 1 + childComplexity;
    };
};

GraphQL graphQL = GraphQL.newGraphQL(schema)
    .queryExecutionStrategy(new AsyncExecutionStrategy())
    .instrumentation(new ChainedInstrumentation(List.of(
        new MaxQueryComplexityInstrumentation(100),   // макс. 100 очков
        new MaxQueryDepthInstrumentation(7)
    )))
    .build();
```

**3. Rate limiting по IP / токену через API Gateway:**

```yaml
# Kong / nginx — rate limit на /graphql endpoint
rate_limit:
  per_minute: 60
  per_second: 10
```

**Best practice:** комбинировать все три уровня: depth limit (7-10), complexity limit (100-200), и token bucket на уровне Gateway. Персистированные запросы дополнительно позволяют разрешить только известные запросы.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q38. Как устроена обработка ошибок в GraphQL: partial responses и поле errors? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Ключевое отличие от REST:** GraphQL всегда возвращает `HTTP 200`, даже при ошибках. Ошибки идут в поле `errors`, данные — в поле `data`.

**Структура ответа с ошибкой:**

```json
{
  "data": {
    "user": {
      "id": "1",
      "name": "Alice",
      "orders": null
    }
  },
  "errors": [
    {
      "message": "Failed to load orders",
      "locations": [{"line": 4, "column": 5}],
      "path": ["user", "orders"],
      "extensions": {
        "code": "SERVICE_UNAVAILABLE",
        "classification": "DataFetchingException"
      }
    }
  ]
}
```

**Partial response:** если один резолвер упал, остальные поля возвращаются успешно. `data.user.orders = null`, но `data.user.name` есть — это частичный ответ. Клиент должен проверять и `data`, и `errors`.

**Типы ошибок в GraphQL:**

| Тип | Когда | Поведение |
|-----|-------|-----------|
| Syntax error | Невалидный запрос | `data: null`, errors |
| Validation error | Запрос не соответствует схеме | `data: null`, errors |
| Resolver error | Ошибка при выполнении | Частичный `data`, errors |
| Network error | Недоступен сервер | HTTP 5xx |

**Обработка в Spring for GraphQL:**

```java
@Component
public class CustomExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable ex,
            DataFetchingEnvironment env) {
        if (ex instanceof NotFoundException e) {
            GraphQLError error = GraphqlErrorBuilder.newError(env)
                .message(e.getMessage())
                .errorType(ErrorType.NOT_FOUND)
                .extensions(Map.of("code", "NOT_FOUND"))
                .build();
            return Mono.just(List.of(error));
        }
        // Для неизвестных исключений — скрыть детали от клиента
        GraphQLError error = GraphqlErrorBuilder.newError(env)
            .message("Internal error")
            .errorType(ErrorType.INTERNAL_ERROR)
            .build();
        return Mono.just(List.of(error));
    }
}
```

**Best practices:**
- Никогда не пробрасывать стектрейс в поле `message` в проде
- Использовать `extensions.code` для машиночитаемых кодов ошибок
- Не злоупотреблять partial responses — если данные критичны, пробрасывать ошибку выше


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q39. (!) Как работает Apollo Federation 2 и что такое subgraph schemas? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Apollo Federation 2** — архитектурный подход для построения распределённых GraphQL API из нескольких независимых сервисов.

**Ключевые компоненты:**

```
Client → Apollo Router (Supergraph) → subgraph-1 (Users)
                                    → subgraph-2 (Orders)
                                    → subgraph-3 (Products)
```

**Директивы Federation 2:**

| Директива | Назначение |
|-----------|-----------|
| `@key` | Объявить тип как entity с уникальным ключом |
| `@shareable` | Разрешить определение поля в нескольких subgraphs |
| `@external` | Поле определено в другом subgraph |
| `@requires` | Резолвер требует поля из другого subgraph |
| `@provides` | Subgraph может предоставить поля другого типа |
| `@override` | Переопределить поле из другого subgraph |

**Пример: Users subgraph:**

```graphql
# users subgraph schema
extend schema @link(url: "https://specs.apollo.dev/federation/v2.0",
                    import: ["@key", "@shareable"])

type Query {
  me: User
}

type User @key(fields: "id") {
  id: ID!
  username: String! @shareable
  email: String!
}
```

**Orders subgraph расширяет User:**

```graphql
# orders subgraph schema
type User @key(fields: "id") {
  id: ID!
  orders(first: Int = 10): [Order!]!
}

type Order {
  id: ID!
  total: Float!
  status: OrderStatus!
}

# Resolver для __resolveReference
@Component
public class UserResolver {
    @SchemaMapping(typeName = "User", field = "orders")
    public List<Order> orders(User user) {
        return orderRepository.findByUserId(user.getId());
    }
}
```

**Query Planning:** Router получает запрос клиента, строит plan выполнения, делает параллельные запросы в нужные subgraphs и сшивает ответы.

**Schema Registry (Apollo Studio):** хранит схемы всех subgraphs, проверяет совместимость при деплое нового subgraph (schema checks), генерирует supergraph schema.

**На собеседовании:** Federation решает проблему владения схемой — каждая команда владеет своим subgraph и деплоит независимо. Это критично при 10+ микросервисах с GraphQL API.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q40. GraphQL vs REST vs gRPC: когда что выбирать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Сравнительная таблица:**

| Критерий | REST | GraphQL | gRPC |
|----------|------|---------|------|
| Клиент | Браузер, мобильный, сторонний | Браузер, мобильный (множество клиентов) | Backend-to-backend |
| Формат | JSON/XML | JSON | Protobuf (бинарный) |
| Контракт | OpenAPI (опционально) | Schema (SDL) | `.proto` (обязательный) |
| Streaming | Ограниченный (SSE) | Subscriptions (WebSocket) | Нативный (4 типа) |
| Кэширование | Простое (HTTP-кэш) | Сложное (APQ, CDN) | Нет нативного |
| Типизация | Нет стандарта | Строгая | Строгая |
| Производительность | Средняя | Средняя | Высокая |
| Браузерная поддержка | Отличная | Отличная | gRPC-Web (через proxy) |
| Learning curve | Низкий | Средний | Средний |

**Когда выбирать REST:**
- Публичный API для сторонних разработчиков
- Простые CRUD-операции
- Необходимо HTTP-кэширование из коробки
- Нет строгих требований к производительности

**Когда выбирать GraphQL:**
- Разнородные клиенты с разными потребностями в данных (мобильный vs веб)
- Сложный граф данных с множеством связей
- BFF (Backend for Frontend) — один API для нескольких клиентов
- Нужна гибкость: клиент сам решает, какие поля запрашивать

**Когда выбирать gRPC:**
- Высоконагруженное межсервисное взаимодействие внутри кластера
- Streaming данных (телеметрия, события)
- Строгий контракт и генерация кода обязательны
- Latency критична (protobuf на 5-10x компактнее JSON)

**Гибридный подход (рекомендуемый):**

```
Mobile/Web Client → GraphQL API → (внутри) gRPC → Microservices
External Partners → REST API  → (внутри) gRPC → Microservices
```

GraphQL или REST — на внешнем слое (developer experience), gRPC — внутри инфраструктуры (производительность).

---

## See also

- [HTTP и REST](http-rest-interview.md) — сравнение REST и GraphQL: когда использовать каждый подход, over-fetching vs under-fetching, версионирование
- [gRPC](grpc-interview.md) — альтернатива GraphQL для высокопроизводительного межсервисного взаимодействия с типизированными контрактами
- [Микросервисная архитектура](../architecture/microservices-interview.md) — GraphQL Federation как способ объединить несколько GraphQL-сервисов за одним endpoint
- [API Gateway](../architecture/api-gateway-interview.md) — GraphQL-слой часто размещается за Gateway: аутентификация, rate limiting, маршрутизация
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — интеграция Spring for GraphQL: аннотации `@QueryMapping`, `@MutationMapping`, контроллеры и DataLoader


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [API Design Best Practices](api-design-best-practices-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [API Versioning](api-versioning-interview.md)
- [gRPC](grpc-interview.md)
- [HTTP и REST](http-rest-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
- [Шпаргалка: GraphQL для Java](../../development/api/graphql/graphql.md) — теория
