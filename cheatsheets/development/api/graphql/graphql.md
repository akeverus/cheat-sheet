---
title: "GraphQL для Java"
description: "Комплексное руководство по использованию GraphQL в Java-приложениях: Schema Definition Language (SDL), resolvers, data fetching, Spring Boot интеграция, Apollo Client, subscriptions, federation и best practices."
tags:
  - development
  - api
  - graphql
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# GraphQL для Java

Комплексное руководство по использованию **GraphQL** в **Java**-приложениях: **Schema Definition Language** (**SDL**), **resolvers**, **data fetching**, **Spring Boot** интеграция, **Apollo Client**, **subscriptions**, **federation** и **best practices**.

## Полезные ссылки

### Официальная документация
- [GraphQL Specification](https://spec.graphql.org/) — официальная спецификация **GraphQL**
- [GraphQL Foundation](https://graphql.org/) — сайт **GraphQL**
- [GraphQL Java](https://www.graphql-java.com/) — **Java** реализация **GraphQL**

### **Java** интеграции
- [Spring GraphQL](https://spring.io/projects/spring-graphql) — **Spring Boot** интеграция
- [GraphQL Java Tools](https://www.graphql-java-kickstart.com/tools/) — schema-first подход
- [Netflix DGS](https://netflix.github.io/dgs/) — **Domain Graph Service**

### Клиентские библиотеки
- [Apollo Client](https://www.apollographql.com/docs/react/) — клиенты **React** / **Vue**
- [GraphQL Request](https://github.com/jasonkuhrt/graphql-request) — простой **JS** клиент
- [Spring WebClient](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html) — реактивный **HTTP** клиент

### **Best practices**
- [GraphQL Best Practices](https://graphql.org/learn/best-practices/) — рекомендации **GraphQL**
- [GraphQL Schema Design](https://graphql.org/learn/schema/) — дизайн схем
- [GraphQL Security](https://graphql.org/learn/authorization/) — безопасность

### См. также
- [[rest-api-design|rest-api-design.md]] — дизайн **REST API**
- [[grpc|grpc.md]] — **gRPC**
- [[spring-graphql|Spring GraphQL]] — интеграция **Spring GraphQL**
- [PostgreSQL](../../../databases/relational/postgresql/README.md) — базы данных для **GraphQL**
- [Мониторинг](../../../monitoring/README.md) — мониторинг и **observability**

## Содержание

- [Введение в **GraphQL**](#введение-в-graphql)
  - [Почему **GraphQL**?](#почему-graphql)
  - [Основные концепции](#основные-концепции)
    - [**Schema** (**Схема**)](#schema-схема)
    - [**Query** (**Запросы**)](#query-запросы)
    - [**Mutation** (**Мутации**)](#mutation-мутации)
    - [**Subscription** (**Подписки**)](#subscription-подписки)
- [**GraphQL** vs **REST**](#graphql-vs-rest)
  - [Сравнение архитектур](#сравнение-архитектур)
    - [**REST API** типичный сценарий](#rest-api-типичный-сценарий)
    - [**GraphQL** решение](#graphql-решение)
  - [Когда использовать **GraphQL**?](#когда-использовать-graphql)
    - [✅ Идеально подходит для:](#идеально-подходит-для)
    - [❌ Не подходит для:](#не-подходит-для)
- [**Schema Definition Language**](#schema-definition-language)
  - [Основы **SDL**](#основы-sdl)
    - [Типы данных](#типы-данных)
- [Скалярные типы](#скалярные-типы)
- [Перечисления](#перечисления)
- [Объекты](#объекты)
- [Интерфейсы](#интерфейсы)
- [Union типы](#union-типы)
- [Input типы](#input-типы)
  - [**Schema directives**](#schema-directives)
    - [Встроенные директивы](#встроенные-директивы)
- [Deprecated поля](#deprecated-поля)
- [Include/Skip директивы](#includeskip-директивы)
- [SpecifiedBy для custom скаляров](#specifiedby-для-custom-скаляров)
- [OneOf input типы (GraphQL 2.1+)](#oneof-input-типы-graphql-21)
  - [**Schema stitching** и **composition**](#schema-stitching-и-composition)
    - [**Schema composition**](#schema-composition)
- [user-schema.graphql](#user-schemagraphql)
- [post-schema.graphql](#post-schemagraphql)
- [Мета-схема](#мета-схема)
- [**GraphQL Java**](#graphql-java)
  - [**GraphQL Java** основы](#graphql-java-основы)
    - [**Maven** зависимости](#maven-зависимости)
    - [Базовая настройка](#базовая-настройка)
  - [**Data fetchers**](#data-fetchers)
    - [Реализация **data fetchers**](#реализация-data-fetchers)
  - [**Schema-first** подход](#schema-first-подход)
    - [**GraphQL Java Tools**](#graphql-java-tools)
- [**Spring Boot** интеграция](#spring-boot-интеграция)
  - [**Spring GraphQL**](#spring-graphql)
    - [**Configuration**](#configuration)
  - [**Controller approach**](#controller-approach)
    - [@**Controller** для **GraphQL**](#controller-для-graphql)
  - [**Input**/**Output types**](#inputoutput-types)
    - [**DTO** классы](#dto-классы)
- [**Resolvers** и **Data Fetching**](#resolvers-и-data-fetching)
  - [**Field resolvers**](#field-resolvers)
    - [Разрешение полей](#разрешение-полей)
  - [**DataLoader** для **batch loading**](#dataloader-для-batch-loading)
    - [Оптимизация N+1 проблемы](#оптимизация-n1-проблемы)
  - [**Custom scalars**](#custom-scalars)
    - [Пользовательские скаляры](#пользовательские-скаляры)
- [**Apollo Client**](#apollo-client)
  - [**JavaScript**/**TypeScript client**](#javascripttypescript-client)
    - [**Apollo Client setup**](#apollo-client-setup)
  - [**React hooks**](#react-hooks)
    - [**Apollo Client hooks**](#apollo-client-hooks)
  - [**Java GraphQL client**](#java-graphql-client)
    - [**GraphQL Java client**](#graphql-java-client)
- [**Subscriptions**](#subscriptions)
  - [**WebSocket subscriptions**](#websocket-subscriptions)
    - [**GraphQL over WebSocket**](#graphql-over-websocket)
  - [**Apollo Client subscriptions**](#apollo-client-subscriptions)
    - [**React subscription hooks**](#react-subscription-hooks)
- [**Federation**](#federation)
  - [**Apollo Federation**](#apollo-federation)
    - [**Federation setup**](#federation-setup)
    - [**Federation gateway**](#federation-gateway)
- [**Security**](#security)
  - [**Authentication**](#authentication)
    - [**JWT authentication**](#jwt-authentication)
  - [**Authorization**](#authorization)
    - [**Field-level authorization**](#field-level-authorization)
  - [**Rate limiting**](#rate-limiting)
    - [**Rate limiting** для **GraphQL**](#rate-limiting-для-graphql)
- [**Performance optimization**](#performance-optimization)
  - [**Query complexity**](#query-complexity)
    - [**Complexity analysis**](#complexity-analysis)
  - [**Caching**](#caching)
    - [**GraphQL caching**](#graphql-caching)
  - [**Query optimization**](#query-optimization)
    - [**Automatic persisted queries**](#automatic-persisted-queries)
- [**Testing**](#testing)
  - [**Unit testing resolvers**](#unit-testing-resolvers)
    - [Тестирование **data fetchers**](#тестирование-data-fetchers)
  - [**Integration testing**](#integration-testing)
    - [Тестирование **GraphQL endpoints**](#тестирование-graphql-endpoints)
  - [**E2E testing**](#e2e-testing)
    - [**End-to-end GraphQL testing**](#end-to-end-graphql-testing)
  - [**Schema design**](#schema-design)
    - [**Schema design principles**](#schema-design-principles)
- [Good: Clear, focused schema](#good-clear-focused-schema)
- [Good: Consistent naming](#good-consistent-naming)
- [Good: Input types for mutations](#good-input-types-for-mutations)
- [Good: Pagination](#good-pagination)
  - [**Error handling**](#error-handling)
    - [**Error handling best practices**](#error-handling-best-practices)
    - [**Performance best practices**](#performance-best-practices)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [**Schema validation errors**](#schema-validation-errors)
    - [N+1 **query problem**](#n1-query-problem)
    - [**Introspection security**](#introspection-security)
  - [**Debug techniques**](#debug-techniques)
    - [**Query analysis**](#query-analysis)
- [Заключение](#заключение)
  - [Ключевые возможности:](#ключевые-возможности)
  - [Архитектурные преимущества:](#архитектурные-преимущества)
    - [**API Design**:](#api-design)
    - [**Developer Experience**:](#developer-experience)
  - [Когда НЕ использовать:](#когда-не-использовать)
  - [Типы **Operations** по назначению:](#типы-operations-по-назначению)
    - [**Queries** (**Чтение**):](#queries-чтение)
    - [**Mutations** (**Изменение**):](#mutations-изменение)
    - [**Subscriptions** (**Подписки**):](#subscriptions-подписки)

## Введение в **GraphQL**

**GraphQL** — это **query language** для **API**, разработанный **Facebook** в `2012` году и открытый в `2015`. **GraphQL** предоставляет более эффективный, мощный и гибкий подход к разработке **API** по сравнению с **REST**.

### Почему **GraphQL**?

**GraphQL** решает основные проблемы традиционных **REST API**:**

1. **Over-fetching и `Under`-fetching** — Клиенты получают ровно те данные, которые им нужны
2. **Множество endpoints** — Один **endpoint** вместо множества **REST** ресурсов
3. **Versioning** — Эволюция **API** без **breaking changes**
4. **Type safety** — Строго типизированная схема
5. **Introspection** — **API** может описывать само себя
6. **Real-time updates** — **Built-in** поддержка **subscriptions**
7. **Developer experience** — Отличные инструменты разработки

### Основные концепции

#### **Schema** (**Схема**)

Схема **GraphQL** определяет структуру данных и доступные операции (**Query, `Mutation`, Subscription**).

Пример определения корневой схемы (**Query**) с типами **User**.

```graphql
# Корневая схема: Query, Mutation, Subscription и типы User, Post
type Query {
  users: [User!]!
  user(id: ID!): User
}

type Mutation {
  createUser(input: CreateUserInput!): User!
  updateUser(id: ID!, input: UpdateUserInput!): User!
}

type Subscription {
  userCreated: User!
  userUpdated: User!
}

type User {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
}

type Post {
  id: ID!
  title: String!
  content: String!
  author: User!
}
```

#### **Query** (**Запросы**)
**Клиенты запрашивают только нужные данные:**

```graphql
# Пример запроса списка пользователей с вложенными постами
query GetUsers {
  users {
    id
    name
    email
    posts {
      id
      title
    }
  }
}
```

#### **Mutation** (**Мутации**)
**Изменение данных через **typed** операции:**

```graphql
# Мутация создания пользователя с переменной $input
mutation CreateUser($input: CreateUserInput!) {
  createUser(input: $input) {
    id
    name
    email
  }
}
```

#### **Subscription** (**Подписки**)
**Real-time** обновления через **WebSocket**:**

```graphql
# Подписка на событие создания пользователя
subscription OnUserCreated {
  userCreated {
    id
    name
    email
  }
}
```

## **GraphQL** vs **REST**

### Сравнение архитектур

#### **REST API** типичный сценарий
```text
# Типичный REST ответ: пользователь и все посты с автором (over-fetching)
GET /api/users/1
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "posts": [
    {
      "id": 1,
      "title": "My Post",
      "content": "Post content",
      "author": {
        "id": 1,
        "name": "John Doe"
      }
    }
  ]
}
```

**Проблемы `REST`:**
- **Over-fetching**: Клиент получает все поля, даже ненужные
- **Under-fetching**: Нужно делать дополнительные запросы для связанных данных
- Множество **endpoints** для разных представлений данных
- Сложности с **versioning**

#### **GraphQL** решение
```graphql
# Запрос одного пользователя с постами — только нужные поля
query GetUserWithPosts($userId: ID!) {
  user(id: $userId) {
    id
    name
    email
    posts {
      id
      title
      # content не запрашиваем, если не нужен
    }
  }
}
```

**Преимущества `GraphQL`:**
- Один **endpoint** для всех запросов
- Клиент контролирует структуру ответа
- Нет **over**/**under fetching**
- **Type-safe** контракты
- **Built-in documentation** через **introspection**

### Когда использовать **GraphQL**?

#### ✅ Идеально подходит для:
- **Mobile приложения** — Снижение сетевого трафика
- **Microservices архитектура** — Единый **API gateway**
- **Complex data relationships** — Связанные данные
- **Rapidly evolving APIs** — Частые изменения требований
- **Multiple clients** — Разные представления данных

#### ❌ Не подходит для:
- **`Simple CRUD APIs`** — **Overhead** не оправдан
- **File uploads** — Лучше использовать **REST**
- **Real-time messaging** — Использовать **WebSocket**/**STOMP**
- **Caching на CDN** — **GraphQL** сложнее кешировать
- **Legacy системы** — Требует значительных изменений

## **Schema Definition Language**

### Основы **SDL**

#### Типы данных
```graphql
# Скалярные типы
scalar DateTime
scalar Email
scalar URL

# Перечисления
enum UserRole {
  ADMIN
  USER
  GUEST
}

enum PostStatus {
  DRAFT
  PUBLISHED
  ARCHIVED
}

# Объекты
type User {
  id: ID!
  username: String!
  email: Email!
  role: UserRole!
  createdAt: DateTime!
  updatedAt: DateTime!
  posts: [Post!]!
  profile: Profile
}

type Profile {
  firstName: String
  lastName: String
  avatar: URL
  bio: String
}

# Интерфейсы
interface Node {
  id: ID!
}

interface Timestamped {
  createdAt: DateTime!
  updatedAt: DateTime!
}

type Post implements Node & Timestamped {
  id: ID!
  title: String!
  content: String!
  status: PostStatus!
  createdAt: DateTime!
  updatedAt: DateTime!
  author: User!
  tags: [String!]!
  comments: [Comment!]!
}

# Union типы
union SearchResult = User | Post | Comment

# Input типы
input CreateUserInput {
  username: String!
  email: Email!
  password: String!
  role: UserRole = USER
}

input UpdateUserInput {
  username: String
  email: Email
  role: UserRole
}

input PostFilter {
  status: PostStatus
  authorId: ID
  tags: [String!]
  createdAfter: DateTime
  createdBefore: DateTime
}
```

### **Schema directives**

#### Встроенные директивы
```graphql
# Deprecated поля
type User {
  id: ID!
  name: String! @deprecated(reason: "Use firstName and lastName")
  firstName: String!
  lastName: String!

  # External поля для federation
  email: String! @external
}

# Include/Skip директивы
type Query {
  users(includeInactive: Boolean = false): [User!]!
    @include(if: $includeUsers)
    @skip(if: $skipUsers)
}

# SpecifiedBy для custom скаляров
scalar Email @specifiedBy(url: "https://tools.ietf.org/html/rfc5322")

# OneOf input типы (GraphQL 2.1+)
input PetInput @oneOf {
  cat: CatInput
  dog: DogInput
}
```

### **Schema stitching** и **composition**

#### **Schema composition**
```graphql
# user-schema.graphql
type User @key(fields: "id") {
  id: ID!
  username: String!
  email: String!
}

extend type Query {
  user(id: ID!): User @resolve
  users: [User!]! @resolve
}

# post-schema.graphql
type Post @key(fields: "id") {
  id: ID!
  title: String!
  content: String!
  authorId: ID!
  author: User @resolve
}

extend type Query {
  post(id: ID!): Post @resolve
  posts: [Post!]! @resolve
}

# Мета-схема
type Query {
  # Составные запросы
  userWithPosts(id: ID!): UserWithPosts @resolve
}

type UserWithPosts {
  user: User!
  posts: [Post!]!
}
```

## **GraphQL Java**

### **GraphQL Java** основы

#### **Maven** зависимости
```xml
<!-- Зависимости GraphQL Java и Spring Boot starter -->
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-java</artifactId>
    <version>20.4</version>
</dependency>

<!-- Для Spring Boot -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>15.0.0</version>
</dependency>

<!-- Для тестирования -->
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-spring-boot-starter-test</artifactId>
    <version>15.0.0</version>
    <scope>test</scope>
</dependency>
```

#### Базовая настройка
```java
// Точка входа и программная сборка схемы (Query, Mutation, Subscription)
@SpringBootApplication
public class GraphQLApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLApplication.class, args);
    }

    @Bean
    public GraphQLSchema schema() {
        return GraphQLSchema.newSchema()
            .query(queryType())
            .mutation(mutationType())
            .subscription(subscriptionType())
            .build();
    }

    private GraphQLObjectType queryType() {
        return GraphQLObjectType.newObject()
            .name("Query")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("users")
                .type(GraphQLList(GraphQLTypeReference.typeRef("User")))
                .dataFetcher(userDataFetcher()))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("user")
                .type(GraphQLTypeReference.typeRef("User"))
                .argument(GraphQLArgument.newArgument()
                    .name("id")
                    .type(Scalars.GraphQLID))
                .dataFetcher(userByIdDataFetcher()))
            .build();
    }

    private GraphQLObjectType mutationType() {
        return GraphQLObjectType.newObject()
            .name("Mutation")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("createUser")
                .type(GraphQLTypeReference.typeRef("User"))
                .argument(GraphQLArgument.newArgument()
                    .name("input")
                    .type(GraphQLTypeReference.typeRef("CreateUserInput")))
                .dataFetcher(createUserDataFetcher()))
            .build();
    }

    private GraphQLObjectType subscriptionType() {
        return GraphQLObjectType.newObject()
            .name("Subscription")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("userCreated")
                .type(GraphQLTypeReference.typeRef("User"))
                .dataFetcher(userCreatedDataFetcher()))
            .build();
    }
}
```

### **Data fetchers**

#### Реализация **data fetchers**
```java
// Data fetcher для списка пользователей (реализация DataFetcher)
@Component
public class UserDataFetcher implements DataFetcher<List<User>> {

    private final UserRepository userRepository;

    public UserDataFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> get(DataFetchingEnvironment environment) {
        // Получить аргументы запроса
        Boolean includeInactive = environment.getArgument("includeInactive");
        String role = environment.getArgument("role");

        // Построить запрос
        Specification<User> spec = Specification.where(null);

        if (includeInactive != null && !includeInactive) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("active"), true));
        }

        if (role != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("role"), UserRole.valueOf(role)));
        }

        return userRepository.findAll(spec);
    }
}

@Component
public class UserByIdDataFetcher implements DataFetcher<User> {

    private final UserRepository userRepository;

    public UserByIdDataFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User get(DataFetchingEnvironment environment) {
        String id = environment.getArgument("id");

        return userRepository.findById(Long.valueOf(id))
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

@Component
public class CreateUserDataFetcher implements DataFetcher<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserDataFetcher(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User get(DataFetchingEnvironment environment) {
        Map<String, Object> input = environment.getArgument("input");

        User user = new User();
        user.setUsername((String) input.get("username"));
        user.setEmail((String) input.get("email"));
        user.setPassword(passwordEncoder.encode((String) input.get("password")));

        String roleStr = (String) input.get("role");
        user.setRole(roleStr != null ? UserRole.valueOf(roleStr) : UserRole.USER);

        user.setActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }
}
```

### **Schema-first** подход

#### **GraphQL Java Tools**
```java
// Конфигурация схемы и словарь типов для schema-first (User, Post, Comment)
@Configuration
public class GraphQLConfig {

    @Bean
    public GraphQLSchemaProvider graphQLSchemaProvider() {
        return new GraphQLSchemaProvider();
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary() {
        return new SchemaParserDictionary()
            .add("User", User.class)
            .add("Post", Post.class)
            .add("Comment", Comment.class);
    }
}

// schema.graphqls
type Query {
  users(includeInactive: Boolean = false): [User!]!
  user(id: ID!): User
  posts(userId: ID, limit: Int = 10): [Post!]!
}

type Mutation {
  createUser(input: CreateUserInput!): User!
  createPost(input: CreatePostInput!): Post!
}

type Subscription {
  postCreated: Post!
  userCreated: User!
}

type User {
  id: ID!
  username: String!
  email: String!
  role: UserRole!
  active: Boolean!
  createdAt: DateTime!
  posts: [Post!]!
}

type Post {
  id: ID!
  title: String!
  content: String!
  author: User!
  createdAt: DateTime!
}

enum UserRole {
  ADMIN
  USER
  GUEST
}

input CreateUserInput {
  username: String!
  email: String!
  password: String!
  role: UserRole = USER
}

input CreatePostInput {
  title: String!
  content: String!
  authorId: ID!
}

// Query resolver
@Component
public class QueryResolver implements GraphQLQueryResolver {

    private final UserService userService;
    private final PostService postService;

    public QueryResolver(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public List<User> users(Boolean includeInactive) {
        return userService.findAll(includeInactive != null ? includeInactive : false);
    }

    public User user(Long id) {
        return userService.findById(id);
    }

    public List<Post> posts(Long userId, Integer limit) {
        if (userId != null) {
            return postService.findByUserId(userId, limit != null ? limit : 10);
        }
        return postService.findAll(limit != null ? limit : 10);
    }
}

// Mutation resolver
@Component
public class MutationResolver implements GraphQLMutationResolver {

    private final UserService userService;
    private final PostService postService;

    public MutationResolver(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public User createUser(CreateUserInput input) {
        return userService.createUser(input);
    }

    public Post createPost(CreatePostInput input) {
        return postService.createPost(input);
    }
}

// Subscription resolver
@Component
public class SubscriptionResolver implements GraphQLSubscriptionResolver {

    private final PostService postService;

    public SubscriptionResolver(PostService postService) {
        this.postService = postService;
    }

    public Publisher<Post> postCreated() {
        return postService.getPostCreatedPublisher();
    }

    public Publisher<User> userCreated() {
        return userService.getUserCreatedPublisher();
    }
}
```

## **Spring Boot** интеграция

### **Spring GraphQL**

#### **Maven** зависимости
```xml
<!-- Spring Boot GraphQL starter и тестовый модуль -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<!-- Для тестирования -->
<dependency>
    <groupId>org.springframework.graphql</groupId>
    <artifactId>spring-graphql-test</artifactId>
    <scope>test</scope>
</dependency>
```

#### **Configuration**
```java
// Регистрация скаляров, data fetchers и подписок для Spring GraphQL
@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            // Register custom scalars
            .scalar(ExtendedScalars.DateTime)
            .scalar(ExtendedScalars.Email)

            // Register data fetchers
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("users", userDataFetcher())
                .dataFetcher("user", userByIdDataFetcher()))
            .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                .dataFetcher("createUser", createUserDataFetcher()))
            .type(TypeRuntimeWiring.newTypeWiring("Subscription")
                .dataFetcher("userCreated", userCreatedSubscription()));
    }

    @Bean
    public DataFetcher<List<User>> userDataFetcher() {
        return environment -> {
            Boolean includeInactive = environment.getArgument("includeInactive");
            return userService.findAll(includeInactive != null ? includeInactive : false);
        };
    }

    @Bean
    public DataFetcher<User> userByIdDataFetcher() {
        return environment -> {
            Long id = Long.valueOf(environment.getArgument("id"));
            return userService.findById(id);
        };
    }

    @Bean
    public DataFetcher<User> createUserDataFetcher() {
        return environment -> {
            CreateUserInput input = environment.getArgument("input");
            return userService.createUser(input);
        };
    }

    @Bean
    public DataFetcher<Publisher<User>> userCreatedSubscription() {
        return environment -> userService.getUserCreatedPublisher();
    }
}
```

### **Controller approach**

#### @**Controller** для **GraphQL**
```java
// Маппинг Query/Mutation/Subscription на методы сервиса через аннотации
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<User> users(@Argument Boolean includeInactive) {
        return userService.findAll(includeInactive != null ? includeInactive : false);
    }

    @QueryMapping
    public User user(@Argument Long id) {
        return userService.findById(id);
    }

    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        return userService.createUser(input);
    }

    @SubscriptionMapping
    public Publisher<User> userCreated() {
        return userService.getUserCreatedPublisher();
    }
}

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @QueryMapping
    public List<Post> posts(@Argument Long userId, @Argument Integer limit) {
        if (userId != null) {
            return postService.findByUserId(userId, limit != null ? limit : 10);
        }
        return postService.findAll(limit != null ? limit : 10);
    }

    @QueryMapping
    public Post post(@Argument Long id) {
        return postService.findById(id);
    }

    @MutationMapping
    public Post createPost(@Argument CreatePostInput input) {
        return postService.createPost(input);
    }

    @SubscriptionMapping
    public Publisher<Post> postCreated() {
        return postService.getPostCreatedPublisher();
    }
}
```

### **Input**/**Output types**

#### **DTO** классы
```java
// Input types
public class CreateUserInput {
    private String username;
    private String email;
    private String password;
    private UserRole role = UserRole.USER;

    // Constructors, getters, setters
}

public class UpdateUserInput {
    private String username;
    private String email;
    private UserRole role;

    // Constructors, getters, setters
}

public class CreatePostInput {
    private String title;
    private String content;
    private Long authorId;

    // Constructors, getters, setters
}

public class PostFilter {
    private PostStatus status;
    private Long authorId;
    private List<String> tags;
    private Instant createdAfter;
    private Instant createdBefore;

    // Constructors, getters, setters
}

// Output types (DTOs)
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private List<PostDto> posts;

    // Constructors, getters, setters
}

public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private UserDto author;
    private List<String> tags;

    // Constructors, getters, setters
}
```

## **Resolvers** и **Data Fetching**

### **Field resolvers**

#### Разрешение полей
```java
// Резолвер полей User: posts и profile (N+1 без DataLoader)
@Component
public class UserResolver implements GraphQLResolver<User> {

    private final PostService postService;
    private final ProfileService profileService;

    public UserResolver(PostService postService, ProfileService profileService) {
        this.postService = postService;
        this.profileService = profileService;
    }

    public List<Post> posts(User user, DataFetchingEnvironment env) {
        // Lazy loading posts for user
        Integer limit = env.getArgument("limit");
        Boolean publishedOnly = env.getArgument("publishedOnly");

        List<Post> posts = postService.findByAuthorId(user.getId());

        if (publishedOnly != null && publishedOnly) {
            posts = posts.stream()
                .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
                .collect(Collectors.toList());
        }

        if (limit != null && limit > 0) {
            posts = posts.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }

        return posts;
    }

    public Profile profile(User user) {
        return profileService.findByUserId(user.getId()).orElse(null);
    }

    public Integer postCount(User user) {
        // Computed field
        return postService.countByAuthorId(user.getId());
    }

    public Boolean isActive(User user) {
        // Computed field based on business logic
        return user.getStatus() == UserStatus.ACTIVE &&
               user.getDeactivatedAt() == null;
    }
}

@Component
public class PostResolver implements GraphQLResolver<Post> {

    private final UserService userService;
    private final CommentService commentService;

    public PostResolver(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    public User author(Post post) {
        return userService.findById(post.getAuthorId());
    }

    public List<Comment> comments(Post post, DataFetchingEnvironment env) {
        Integer limit = env.getArgument("limit");
        Integer offset = env.getArgument("offset");

        return commentService.findByPostId(post.getId(),
            limit != null ? limit : 10,
            offset != null ? offset : 0);
    }

    public Integer commentCount(Post post) {
        return commentService.countByPostId(post.getId());
    }

    public List<String> tags(Post post) {
        // Parse tags from content or metadata
        return parseTags(post.getContent());
    }

    public String summary(Post post) {
        // Computed field - first 100 characters
        String content = post.getContent();
        if (content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    private List<String> parseTags(String content) {
        // Simple tag parsing - look for #hashtags
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);

        List<String> tags = new ArrayList<>();
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        return tags;
    }
}
```

### **DataLoader** для **batch loading**

#### Оптимизация N+1 проблемы
```java
// Регистрация DataLoader для user и post — пакетная загрузка
@Configuration
public class DataLoaderConfig {

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();

        // User DataLoader
        registry.register("userLoader",
            DataLoader.newDataLoader(userIds ->
                CompletableFuture.supplyAsync(() ->
                    userService.findByIds(userIds)
                )
            )
        );

        // Post DataLoader
        registry.register("postLoader",
            DataLoader.newDataLoader(postIds ->
                CompletableFuture.supplyAsync(() ->
                    postService.findByIds(postIds)
                )
            )
        );

        return registry;
    }
}

@Component
public class BatchUserResolver implements GraphQLResolver<Post> {

    @Autowired
    private DataLoader<Long, User> userDataLoader;

    public CompletableFuture<User> author(Post post) {
        return userDataLoader.load(post.getAuthorId());
    }
}

@Component
public class BatchCommentResolver implements GraphQLResolver<Post> {

    @Autowired
    private DataLoader<Long, List<Comment>> commentsDataLoader;

    public CompletableFuture<List<Comment>> comments(Post post) {
        return commentsDataLoader.load(post.getId());
    }
}

// Service with batch loading
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findByIds(Collection<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public Map<Long, User> findByIdsAsMap(Collection<Long> ids) {
        return userRepository.findAllById(ids)
            .stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
```

### **Custom scalars**

#### Пользовательские скаляры
```java
// Регистрация кастомных скаляров (DateTime, Email и др.)
@Configuration
public class ScalarConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            .scalar(dateTimeScalar())
            .scalar(emailScalar())
            .scalar(urlScalar());
    }

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("ISO 8601 Date Time")
            .coercing(new Coercing<Instant, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof Instant) {
                        return ((Instant) dataFetcherResult).toString();
                    }
                    throw new CoercingSerializeException("Invalid DateTime value");
                }

                @Override
                public Instant parseValue(Object input) {
                    if (input instanceof String) {
                        return Instant.parse((String) input);
                    }
                    throw new CoercingParseValueException("Invalid DateTime input");
                }

                @Override
                public Instant parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        return Instant.parse(((StringValue) input).getValue());
                    }
                    throw new CoercingParseLiteralException("Invalid DateTime literal");
                }
            })
            .build();
    }

    @Bean
    public GraphQLScalarType emailScalar() {
        return GraphQLScalarType.newScalar()
            .name("Email")
            .description("Email address")
            .coercing(new Coercing<String, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    return dataFetcherResult.toString();
                }

                @Override
                public String parseValue(Object input) {
                    String email = input.toString();
                    if (isValidEmail(email)) {
                        return email;
                    }
                    throw new CoercingParseValueException("Invalid email format");
                }

                @Override
                public String parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        String email = ((StringValue) input).getValue();
                        if (isValidEmail(email)) {
                            return email;
                        }
                    }
                    throw new CoercingParseLiteralException("Invalid email literal");
                }

                private boolean isValidEmail(String email) {
                    return email != null && email.contains("@");
                }
            })
            .build();
    }

    @Bean
    public GraphQLScalarType urlScalar() {
        return ExtendedScalars.Url; // From graphql-java-extended-scalars
    }
}
```

## **Apollo Client**

### **JavaScript**/**TypeScript client**

#### **Apollo Client setup**
```typescript
// React application with Apollo Client
import { ApolloClient, InMemoryCache, gql } from '@apollo/client';

// Apollo Client setup
const client = new ApolloClient({
  uri: 'http://localhost:8080/graphql',
  cache: new InMemoryCache()
});

// Query hook
const GET_USERS = gql`
  query GetUsers($includeInactive: Boolean) {
    users(includeInactive: $includeInactive) {
      id
      username
      email
      role
      posts {
        id
        title
        createdAt
      }
    }
  }
`;

function UsersList() {
  const { loading, error, data } = useQuery(GET_USERS, {
    variables: { includeInactive: false }
  });

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error.message}</p>;

  return (
    <ul>
      {data.users.map(user => (
        <li key={user.id}>
          {user.username} ({user.email}) - {user.posts.length} posts
        </li>
      ))}
    </ul>
  );
}

// Mutation hook
const CREATE_USER = gql`
  mutation CreateUser($input: CreateUserInput!) {
    createUser(input: $input) {
      id
      username
      email
      role
    }
  }
`;

function CreateUserForm() {
  const [createUser, { loading, error }] = useMutation(CREATE_USER);

  const handleSubmit = async (formData) => {
    try {
      const result = await createUser({
        variables: { input: formData },
        // Update cache after mutation
        update: (cache, { data: { createUser } }) => {
          const existingData = cache.readQuery({ query: GET_USERS });
          cache.writeQuery({
            query: GET_USERS,
            data: {
              users: [...existingData.users, createUser]
            }
          });
        }
      });
      console.log('User created:', result.data.createUser);
    } catch (err) {
      console.error('Error creating user:', err);
    }
  };

  // Form implementation...
}
```

### **React hooks**

#### **Apollo Client hooks**
```typescript
// Custom hooks for GraphQL operations
import { useQuery, useMutation, useSubscription } from '@apollo/client';
import { GET_USER, UPDATE_USER, USER_UPDATED } from './queries';

export function useUser(id: string) {
  return useQuery(GET_USER, {
    variables: { id },
    pollInterval: 30000, // Refetch every 30 seconds
    notifyOnNetworkStatusChange: true
  });
}

export function useUpdateUser() {
  return useMutation(UPDATE_USER, {
    // Optimistic updates
    optimisticResponse: (variables) => ({
      updateUser: {
        id: variables.id,
        __typename: 'User',
        ...variables.input
      }
    }),

    // Error handling
    onError: (error) => {
      console.error('Update failed:', error);
      // Show user-friendly error message
    },

    // Update cache
    update: (cache, { data: { updateUser } }) => {
      const query = GET_USER;
      const variables = { id: updateUser.id };

      const existingData = cache.readQuery({ query, variables });
      if (existingData) {
        cache.writeQuery({
          query,
          variables,
          data: { user: updateUser }
        });
      }
    }
  });
}

export function useUserUpdates(id: string) {
  return useSubscription(USER_UPDATED, {
    variables: { userId: id },
    onData: ({ data }) => {
      console.log('User updated:', data.userUpdated);
      // Update UI in real-time
    },
    onError: (error) => {
      console.error('Subscription error:', error);
    }
  });
}

// Apollo Client configuration with error handling
import { ApolloClient, InMemoryCache, from } from '@apollo/client';
import { onError } from '@apollo/client/link/error';
import { setContext } from '@apollo/client/link/context';

const errorLink = onError(({ graphQLErrors, networkError }) => {
  if (graphQLErrors) {
    graphQLErrors.forEach(({ message, locations, path }) => {
      console.error(`GraphQL error: Message: ${message}, Location: ${locations}, Path: ${path}`);
    });
  }

  if (networkError) {
    console.error(`Network error: ${networkError}`);
  }
});

const authLink = setContext((_, { headers }) => {
  const token = localStorage.getItem('authToken');
  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : '',
    }
  };
});

export const client = new ApolloClient({
  link: from([errorLink, authLink, httpLink]),
  cache: new InMemoryCache({
    typePolicies: {
      User: {
        fields: {
          posts: {
            merge(existing = [], incoming) {
              return [...existing, ...incoming];
            }
          }
        }
      }
    }
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
      errorPolicy: 'all'
    },
    query: {
      fetchPolicy: 'network-only',
      errorPolicy: 'all'
    },
    mutate: {
      errorPolicy: 'all'
    }
  }
});
```

### **Java GraphQL client**

#### **GraphQL Java client**
```java
// Клиент GraphQL на WebClient: запросы, мутации и подписки
@Service
public class GraphQLClientService {

    private final WebClient webClient;

    public GraphQLClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8080/graphql")
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    public Mono<List<User>> getUsers(Boolean includeInactive) {
        String query = """
            query GetUsers($includeInactive: Boolean) {
                users(includeInactive: $includeInactive) {
                    id
                    username
                    email
                    role
                    active
                }
            }
            """;

        Map<String, Object> variables = Map.of("includeInactive",
            includeInactive != null ? includeInactive : false);

        GraphQLRequest request = new GraphQLRequest(query, variables);

        return webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GraphQLResponse.class)
            .map(response -> {
                if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                    throw new GraphQLException(response.getErrors());
                }
                return response.getData().getUsers();
            });
    }

    public Mono<User> createUser(CreateUserInput input) {
        String mutation = """
            mutation CreateUser($input: CreateUserInput!) {
                createUser(input: $input) {
                    id
                    username
                    email
                    role
                    active
                    createdAt
                }
            }
            """;

        Map<String, Object> variables = Map.of("input", input);
        GraphQLRequest request = new GraphQLRequest(mutation, variables);

        return webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GraphQLResponse.class)
            .map(response -> {
                if (response.getErrors() != null && !response.getErrors().isEmpty()) {
                    throw new GraphQLException(response.getErrors());
                }
                return response.getData().getCreateUser();
            });
    }

    public Flux<User> subscribeToUserCreations() {
        String subscription = """
            subscription OnUserCreated {
                userCreated {
                    id
                    username
                    email
                    role
                }
            }
            """;

        GraphQLRequest request = new GraphQLRequest(subscription, Map.of());

        return webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToFlux(GraphQLResponse.class)
            .map(response -> response.getData().getUserCreated());
    }
}

// DTO classes for GraphQL responses
public class GraphQLRequest {
    private String query;
    private Map<String, Object> variables;

    // Constructors, getters, setters
}

public class GraphQLResponse<T> {
    private T data;
    private List<GraphQLError> errors;

    // Constructors, getters, setters
}

public class GraphQLError {
    private String message;
    private List<String> path;
    private Map<String, Object> extensions;

    // Constructors, getters, setters
}
```

## **Subscriptions**

### **WebSocket subscriptions**

#### **GraphQL over WebSocket**
```java
// Регистрация WebSocket-эндпоинта для GraphQL подписок
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(graphQLWebSocketHandler(), "/graphql-ws")
            .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler graphQLWebSocketHandler() {
        return new GraphQLWebSocketHandler(graphQLExecutor());
    }

    @Bean
    public GraphQLExecutor graphQLExecutor() {
        return new DefaultGraphQLExecutor();
    }
}

@Component
public class SubscriptionPublisher {

    private final Sinks.Many<User> userCreatedSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Post> postCreatedSink = Sinks.many().multicast().onBackpressureBuffer();

    public void publishUserCreated(User user) {
        userCreatedSink.tryEmitNext(user);
    }

    public void publishPostCreated(Post post) {
        postCreatedSink.tryEmitNext(post);
    }

    public Flux<User> getUserCreatedFlux() {
        return userCreatedSink.asFlux();
    }

    public Flux<Post> getPostCreatedFlux() {
        return postCreatedSink.asFlux();
    }
}

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionPublisher subscriptionPublisher;

    public UserService(UserRepository userRepository,
                      SubscriptionPublisher subscriptionPublisher) {
        this.userRepository = userRepository;
        this.subscriptionPublisher = subscriptionPublisher;
    }

    @Transactional
    public User createUser(CreateUserInput input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(input.getRole() != null ? input.getRole() : UserRole.USER);
        user.setActive(true);
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        // Publish to subscriptions
        subscriptionPublisher.publishUserCreated(savedUser);

        return savedUser;
    }

    public Flux<User> getUserCreatedPublisher() {
        return subscriptionPublisher.getUserCreatedFlux();
    }
}
```

### **Apollo Client subscriptions**

#### **React subscription hooks**
```typescript
// Subscription hook
import { useSubscription, gql } from '@apollo/client';

const USER_CREATED_SUBSCRIPTION = gql`
  subscription OnUserCreated {
    userCreated {
      id
      username
      email
      role
      createdAt
    }
  }
`;

const POST_CREATED_SUBSCRIPTION = gql`
  subscription OnPostCreated {
    postCreated {
      id
      title
      content
      author {
        id
        username
      }
      createdAt
    }
  }
`;

function UserListWithSubscription() {
  const { data: usersData, loading: usersLoading } = useQuery(GET_USERS);
  const { data: newUserData } = useSubscription(USER_CREATED_SUBSCRIPTION);

  const [users, setUsers] = useState([]);

  useEffect(() => {
    if (usersData?.users) {
      setUsers(usersData.users);
    }
  }, [usersData]);

  useEffect(() => {
    if (newUserData?.userCreated) {
      setUsers(prevUsers => [...prevUsers, newUserData.userCreated]);
    }
  }, [newUserData]);

  if (usersLoading) return <p>Loading users...</p>;

  return (
    <div>
      <h2>Users ({users.length})</h2>
      <ul>
        {users.map(user => (
          <li key={user.id}>
            {user.username} - {user.email}
            <span style={{ color: 'green', marginLeft: '10px' }}>
              {newUserData?.userCreated?.id === user.id ? 'NEW!' : ''}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}

function PostFeed() {
  const { data: postsData, loading: postsLoading } = useQuery(GET_POSTS);
  const { data: newPostData } = useSubscription(POST_CREATED_SUBSCRIPTION);

  const [posts, setPosts] = useState([]);

  useEffect(() => {
    if (postsData?.posts) {
      setPosts(postsData.posts);
    }
  }, [postsData]);

  useEffect(() => {
    if (newPostData?.postCreated) {
      setPosts(prevPosts => [newPostData.postCreated, ...prevPosts]);
    }
  }, [newPostData]);

  if (postsLoading) return <p>Loading posts...</p>;

  return (
    <div>
      <h2>Recent Posts</h2>
      {posts.map(post => (
        <article key={post.id} style={{
          border: '1px solid #ccc',
          padding: '10px',
          margin: '10px 0',
          backgroundColor: newPostData?.postCreated?.id === post.id ? '#e8f5e8' : 'white'
        }}>
          <h3>{post.title}</h3>
          <p>By {post.author.username}</p>
          <p>{post.content.substring(0, 100)}...</p>
          <small>{new Date(post.createdAt).toLocaleString()}</small>
        </article>
      ))}
    </div>
  );
}
```

## **Federation**

### **Apollo Federation**

#### **Federation setup**
```java
// Подграф федерации: схема сервиса User
@Configuration
public class FederationConfig {

    @Bean
    public GraphQLSchema graphQLSchema() {
        return FederatedSchemaBuilder.builder()
            .schema(fetchSchema())
            .resolvers(createResolvers())
            .link(fetchLinks())
            .compose()
            .build();
    }

    private String fetchSchema() {
        return """
            extend schema
              @link(url: "https://specs.apollo.dev/federation/v2.3",
                    import: ["@key", "@shareable", "@external"])

            type User @key(fields: "id") {
              id: ID!
              username: String!
              email: String!
              profile: Profile
            }

            type Profile @shareable {
              firstName: String
              lastName: String
              avatarUrl: String
            }

            extend type Post @key(fields: "authorId") {
              authorId: ID! @external
              author: User @requires(fields: "authorId")
            }

            type Query {
              users: [User!]!
              user(id: ID!): User
            }
            """;
    }

    private List<GraphQLResolver<?>> createResolvers() {
        return List.of(
            new UserResolver(),
            new PostResolver()
        );
    }

    private List<Link> fetchLinks() {
        return List.of(
            Link.newLink()
                .url("https://specs.apollo.dev/federation/v2.3")
                .imports(List.of("@key", "@shareable", "@external", "@requires"))
                .build()
        );
    }
}

@Component
public class UserResolver {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @QueryMapping
    public List<User> users() {
        return userService.findAll();
    }

    @QueryMapping
    public User user(@Argument String id) {
        return userService.findById(Long.valueOf(id));
    }

    @SchemaMapping(typeName = "User", field = "profile")
    public Profile profile(User user) {
        return profileService.findByUserId(user.getId()).orElse(null);
    }

    // Federation entity resolver
    @EntityMapping
    public User resolveUser(@Argument String id, @Argument Map<String, Object> representations) {
        return userService.findById(Long.valueOf(id));
    }
}

@Component
public class PostResolver {

    @Autowired
    private UserService userService;

    // Federation reference resolver
    @SchemaMapping(typeName = "Post", field = "author")
    public User author(Post post) {
        return userService.findById(post.getAuthorId());
    }
}
```

### **Schema composition**

#### **Federation gateway**
```java
// Шлюз федерации: агрегация подграфов (User, Post)
@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> graphQLRouter() {
        return RouterFunctions.route()
            .POST("/graphql", this::handleGraphQLRequest)
            .build();
    }

    private Mono<ServerResponse> handleGraphQLRequest(ServerRequest request) {
        return request.bodyToMono(GraphQLRequest.class)
            .flatMap(graphQLRequest -> {
                // Route to appropriate subgraph based on operation
                if (isUserOperation(graphQLRequest)) {
                    return routeToUserService(graphQLRequest);
                } else if (isPostOperation(graphQLRequest)) {
                    return routeToPostService(graphQLRequest);
                } else {
                    return routeToGateway(graphQLRequest);
                }
            });
    }

    private boolean isUserOperation(GraphQLRequest request) {
        String query = request.getQuery();
        return query.contains("users") || query.contains("User");
    }

    private boolean isPostOperation(GraphQLRequest request) {
        String query = request.getQuery();
        return query.contains("posts") || query.contains("Post");
    }

    private Mono<ServerResponse> routeToUserService(GraphQLRequest request) {
        return WebClient.create("http://user-service:8080/graphql")
            .post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GraphQLResponse.class)
            .flatMap(response -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response));
    }

    private Mono<ServerResponse> routeToPostService(GraphQLRequest request) {
        return WebClient.create("http://post-service:8080/graphql")
            .post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GraphQLResponse.class)
            .flatMap(response -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response));
    }

    private Mono<ServerResponse> routeToGateway(GraphQLRequest request) {
        // For composed queries, route to federation gateway
        return WebClient.create("http://federation-gateway:8080/graphql")
            .post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GraphQLResponse.class)
            .flatMap(response -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response));
    }
}
```

## **Security**

### **Authentication**

#### **JWT authentication**
```java
// Настройка безопасности: JWT и CORS для GraphQL
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .requestMatchers("/graphql").authenticated()
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}

@Component
public class GraphQLSecurityService {

    public boolean hasPermission(String userId, String resource, String action) {
        // Implement permission checking logic
        User user = userService.findById(Long.valueOf(userId));
        return user.getRole().hasPermission(resource, action);
    }

    public boolean isOwner(String userId, String resourceId, String resourceType) {
        // Check if user owns the resource
        switch (resourceType) {
            case "Post":
                Post post = postService.findById(Long.valueOf(resourceId));
                return post.getAuthorId().equals(Long.valueOf(userId));
            case "Comment":
                Comment comment = commentService.findById(Long.valueOf(resourceId));
                return comment.getAuthorId().equals(Long.valueOf(userId));
            default:
                return false;
        }
    }
}

@Component
public class SecurityDataFetcher implements DataFetcher<Object> {

    private final DataFetcher<?> originalDataFetcher;
    private final String requiredPermission;

    public SecurityDataFetcher(DataFetcher<?> originalDataFetcher, String requiredPermission) {
        this.originalDataFetcher = originalDataFetcher;
        this.requiredPermission = requiredPermission;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GraphQLException("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();

        if (!hasPermission(user, requiredPermission)) {
            throw new GraphQLException("Forbidden");
        }

        return originalDataFetcher.get(environment);
    }

    private boolean hasPermission(User user, String permission) {
        // Implement permission checking
        return user.getRole().hasPermission(permission);
    }
}
```

### **Authorization**

#### **Field-level authorization**
```java
// Проверка прав доступа к полям User на уровне резолвера
@Component
public class UserFieldSecurityResolver implements GraphQLResolver<User> {

    @Autowired
    private GraphQLSecurityService securityService;

    public String email(User user, DataFetchingEnvironment env) {
        User currentUser = getCurrentUser(env);

        // Users can see their own email, admins can see all emails
        if (currentUser.getId().equals(user.getId()) ||
            currentUser.getRole() == UserRole.ADMIN) {
            return user.getEmail();
        }

        throw new GraphQLException("Access denied");
    }

    public List<Post> posts(User user, DataFetchingEnvironment env) {
        User currentUser = getCurrentUser(env);

        // Users can see their own posts, admins can see all posts
        if (currentUser.getId().equals(user.getId()) ||
            currentUser.getRole() == UserRole.ADMIN) {
            return postService.findByAuthorId(user.getId());
        }

        // Others can only see published posts
        return postService.findPublishedByAuthorId(user.getId());
    }

    private User getCurrentUser(DataFetchingEnvironment env) {
        // Extract user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}

@Component
public class PostFieldSecurityResolver implements GraphQLResolver<Post> {

    public String content(Post post, DataFetchingEnvironment env) {
        User currentUser = getCurrentUser(env);

        // Check if user can view post content
        if (post.getStatus() == PostStatus.PUBLISHED ||
            post.getAuthorId().equals(currentUser.getId()) ||
            currentUser.getRole() == UserRole.ADMIN) {
            return post.getContent();
        }

        throw new GraphQLException("Access denied");
    }

    public Boolean canEdit(Post post, DataFetchingEnvironment env) {
        User currentUser = getCurrentUser(env);
        return post.getAuthorId().equals(currentUser.getId()) ||
               currentUser.getRole() == UserRole.ADMIN;
    }

    private User getCurrentUser(DataFetchingEnvironment env) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
```

### **Rate limiting**

#### **Rate limiting** для **GraphQL**
```java
// Ограничение частоты запросов через DataFetcher
@Component
public class RateLimitDataFetcher implements DataFetcher<Object> {

    private final DataFetcher<?> originalDataFetcher;
    private final RateLimiter rateLimiter;

    public RateLimitDataFetcher(DataFetcher<?> originalDataFetcher, String operation) {
        this.originalDataFetcher = originalDataFetcher;
        this.rateLimiter = RateLimiter.create(10.0); // 10 requests per second
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        if (!rateLimiter.tryAcquire()) {
            throw new GraphQLException("Rate limit exceeded. Try again later.");
        }

        return originalDataFetcher.get(environment);
    }
}

@Configuration
public class RateLimitConfig {

    @Bean
    public RuntimeWiringConfigurer rateLimitConfigurer() {
        return wiringBuilder -> wiringBuilder
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("users", rateLimitedDataFetcher(userDataFetcher(), "users"))
                .dataFetcher("posts", rateLimitedDataFetcher(postDataFetcher(), "posts")))
            .type(TypeRuntimeWiring.newTypeWiring("Mutation")
                .dataFetcher("createUser", rateLimitedDataFetcher(createUserDataFetcher(), "createUser"))
                .dataFetcher("createPost", rateLimitedDataFetcher(createPostDataFetcher(), "createPost")));
    }

    private DataFetcher<?> rateLimitedDataFetcher(DataFetcher<?> original, String operation) {
        return new RateLimitDataFetcher(original, operation);
    }
}
```

## **Performance optimization**

### **Query complexity**

#### **Complexity analysis**
```java
// Анализ сложности запроса и отклонение при превышении лимита
@Component
public class QueryComplexityAnalyzer implements Instrumentation {

    @Override
    public ExecutionResult instrumentExecutionResult(ExecutionResult executionResult,
                                                   InstrumentationExecutionParameters parameters) {
        int complexity = calculateComplexity(parameters.getQuery());
        int maxComplexity = 100; // Configure max allowed complexity

        if (complexity > maxComplexity) {
            throw new GraphQLException("Query complexity exceeds maximum allowed: " + maxComplexity);
        }

        // Add complexity to extensions
        Map<String, Object> extensions = executionResult.getExtensions();
        if (extensions == null) {
            extensions = new HashMap<>();
            executionResult = executionResult.transform(builder ->
                builder.extensions(extensions).build());
        }
        extensions.put("complexity", complexity);

        return executionResult;
    }

    private int calculateComplexity(Document document) {
        ComplexityCalculator calculator = new ComplexityCalculator();
        return calculator.calculate(document);
    }
}

public class ComplexityCalculator {

    public int calculate(Document document) {
        AtomicInteger complexity = new AtomicInteger(0);

        document.getDefinitions().forEach(definition -> {
            if (definition instanceof OperationDefinition) {
                traverseSelectionSet(((OperationDefinition) definition).getSelectionSet(), complexity, 1);
            }
        });

        return complexity.get();
    }

    private void traverseSelectionSet(SelectionSet selectionSet, AtomicInteger complexity, int multiplier) {
        selectionSet.getSelections().forEach(selection -> {
            if (selection instanceof Field) {
                Field field = (Field) selection;
                complexity.addAndGet(multiplier);

                // Nested selections increase complexity
                if (field.getSelectionSet() != null) {
                    traverseSelectionSet(field.getSelectionSet(), complexity, multiplier * 2);
                }
            }
        });
    }
}
```

### **Caching**

#### **GraphQL caching**
```java
// Кэширование ответов по запросу и переменным
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("graphql");
    }
}

@Component
public class CachedDataFetcher implements DataFetcher<Object> {

    private final DataFetcher<?> originalDataFetcher;
    private final Cache cache;

    public CachedDataFetcher(DataFetcher<?> originalDataFetcher, Cache cache) {
        this.originalDataFetcher = originalDataFetcher;
        this.cache = cache;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        String cacheKey = generateCacheKey(environment);

        return cache.get(cacheKey, () -> {
            Object result = originalDataFetcher.get(environment);

            // Cache for 5 minutes
            cache.put(cacheKey, result);

            return result;
        });
    }

    private String generateCacheKey(DataFetchingEnvironment environment) {
        StringBuilder key = new StringBuilder();
        key.append(environment.getField().getName());

        // Include arguments in cache key
        environment.getArguments().forEach((argName, argValue) -> {
            key.append(":").append(argName).append("=").append(argValue);
        });

        return key.toString();
    }
}

@Configuration
public class GraphQLCacheConfig {

    @Bean
    public RuntimeWiringConfigurer cacheConfigurer(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache("graphql");

        return wiringBuilder -> wiringBuilder
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("users", new CachedDataFetcher(userDataFetcher(), cache))
                .dataFetcher("posts", new CachedDataFetcher(postDataFetcher(), cache)));
    }
}
```

### **Query optimization**

#### **Automatic persisted queries**
```java
// Регистрация persisted-запросов по хэшу
@Configuration
public class APQConfig {

    @Bean
    public GraphQLSchema graphQLSchema() {
        return GraphQLSchema.newSchema()
            .query(queryType())
            .additionalType(buildMutationType())
            .build();
    }

    @Bean
    public ExecutionStrategy executionStrategy() {
        return new AsyncExecutionStrategy();
    }

    @Bean
    public PreparsedDocumentProvider preparsedDocumentProvider() {
        return new InMemoryPreparsedDocumentProvider();
    }
}

@Service
public class APQService {

    private final Map<String, Document> queryCache = new ConcurrentHashMap<>();
    private final Map<String, String> hashToQueryMap = new ConcurrentHashMap<>();

    public String registerQuery(String query) {
        String hash = generateHash(query);

        // Parse and cache the query
        Document document = parseQuery(query);
        queryCache.put(hash, document);
        hashToQueryMap.put(hash, query);

        return hash;
    }

    public Document getQuery(String hash) {
        return queryCache.get(hash);
    }

    public String getOriginalQuery(String hash) {
        return hashToQueryMap.get(hash);
    }

    private String generateHash(String query) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(query.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate hash", e);
        }
    }

    private Document parseQuery(String query) {
        GraphQLParser parser = new GraphQLParser();
        return parser.parseDocument(query);
    }
}

@RestController
public class GraphQLController {

    @Autowired
    private GraphQL graphQL;

    @Autowired
    private APQService apqService;

    @PostMapping("/graphql")
    public Mono<Map<String, Object>> handleGraphQLRequest(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        String hash = (String) request.get("hash");

        if (hash != null && query == null) {
            // APQ request - get query by hash
            query = apqService.getOriginalQuery(hash);
            if (query == null) {
                return Mono.just(Map.of("error", "Persisted query not found"));
            }
        } else if (query != null && hash == null) {
            // Register new APQ
            hash = apqService.registerQuery(query);
        }

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables((Map<String, Object>) request.get("variables"))
            .build();

        return Mono.fromFuture(graphQL.executeAsync(executionInput))
            .map(result -> {
                Map<String, Object> response = new HashMap<>();
                if (result.getData() != null) {
                    response.put("data", result.getData());
                }
                if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                    response.put("errors", result.getErrors());
                }
                if (hash != null) {
                    response.put("hash", hash);
                }
                return response;
            });
    }
}
```

## **Testing**

### **Unit testing resolvers**

#### Тестирование **data fetchers**
```java
// Юнит-тест data fetcher с моками сервисов
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserResolverTest {

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private UserResolver userResolver;

    @Test
    void testUserPostsResolver() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<Post> expectedPosts = Arrays.asList(
            createPost(1L, "Post 1"),
            createPost(2L, "Post 2")
        );

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getArgument("limit")).thenReturn(10);
        when(env.getArgument("publishedOnly")).thenReturn(false);
        when(postService.findByAuthorId(userId)).thenReturn(expectedPosts);

        // When
        List<Post> result = userResolver.posts(user, env);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("title").contains("Post 1", "Post 2");
        verify(postService).findByAuthorId(userId);
    }

    @Test
    void testUserPostsWithPublishedOnly() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<Post> allPosts = Arrays.asList(
            createPost(1L, "Published Post", PostStatus.PUBLISHED),
            createPost(2L, "Draft Post", PostStatus.DRAFT)
        );

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getArgument("limit")).thenReturn(null);
        when(env.getArgument("publishedOnly")).thenReturn(true);
        when(postService.findByAuthorId(userId)).thenReturn(allPosts);

        // When
        List<Post> result = userResolver.posts(user, env);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Published Post");
        assertThat(result.get(0).getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    private Post createPost(Long id, String title) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setStatus(PostStatus.PUBLISHED);
        return post;
    }

    private Post createPost(Long id, String title, PostStatus status) {
        Post post = createPost(id, title);
        post.setStatus(status);
        return post;
    }
}
```

### **Integration testing**

#### Тестирование **GraphQL endpoints**
```java
// Интеграционный тест с GraphQLTester и случайным портом
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQLTester
public class GraphQLIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        // Clean up data
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Create test data
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(UserRole.USER);
        user.setActive(true);
        userRepository.save(user);
    }

    @Test
    void testGetUsersQuery() {
        graphQlTester
            .document("""
                query GetUsers {
                    users {
                        id
                        username
                        email
                        role
                        active
                    }
                }
                """)
            .execute()
            .path("users")
            .entityList(User.class)
            .hasSizeGreaterThan(0)
            .satisfies(users -> {
                User firstUser = users.get(0);
                assertThat(firstUser.getUsername()).isEqualTo("testuser");
                assertThat(firstUser.getEmail()).isEqualTo("test@example.com");
                assertThat(firstUser.getRole()).isEqualTo(UserRole.USER);
                assertThat(firstUser.isActive()).isTrue();
            });
    }

    @Test
    void testCreateUserMutation() {
        graphQlTester
            .document("""
                mutation CreateUser($input: CreateUserInput!) {
                    createUser(input: $input) {
                        id
                        username
                        email
                        role
                        active
                    }
                }
                """)
            .variable("input", Map.of(
                "username", "newuser",
                "email", "new@example.com",
                "password", "password123",
                "role", "USER"
            ))
            .execute()
            .path("createUser")
            .hasValue()
            .satisfies(user -> {
                assertThat(user.get("username")).isEqualTo("newuser");
                assertThat(user.get("email")).isEqualTo("new@example.com");
                assertThat(user.get("role")).isEqualTo("USER");
                assertThat(user.get("active")).isEqualTo(true);
            });
    }

    @Test
    void testUserValidation() {
        graphQlTester
            .document("""
                mutation CreateUser($input: CreateUserInput!) {
                    createUser(input: $input) {
                        id
                        username
                    }
                }
                """)
            .variable("input", Map.of(
                "username", "",  // Invalid: empty username
                "email", "invalid-email",  // Invalid: bad email format
                "password", "123",  // Invalid: too short
                "role", "USER"
            ))
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).isNotEmpty();
                assertThat(errors.get(0).getMessage()).contains("validation");
            });
    }

    @Test
    void testComplexQueryWithFragments() {
        // Create test data with posts
        User user = userRepository.findByUsername("testuser").get();

        Post post1 = new Post();
        post1.setTitle("Test Post 1");
        post1.setContent("Content 1");
        post1.setAuthorId(user.getId());
        post1.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Test Post 2");
        post2.setContent("Content 2");
        post2.setAuthorId(user.getId());
        post2.setStatus(PostStatus.PUBLISHED);
        postRepository.save(post2);

        graphQlTester
            .document("""
                fragment UserDetails on User {
                    id
                    username
                    email
                    role
                }

                fragment PostDetails on Post {
                    id
                    title
                    content
                    status
                }

                query GetUserWithPosts($userId: ID!) {
                    user(id: $userId) {
                        ...UserDetails
                        posts {
                            ...PostDetails
                        }
                    }
                }
                """)
            .variable("userId", user.getId())
            .execute()
            .path("user")
            .hasValue()
            .satisfies(userData -> {
                assertThat(userData.get("username")).isEqualTo("testuser");
                assertThat(userData.get("posts")).asList().hasSize(2);
            });
    }
}
```

### **E2E testing**

#### **End-to-end GraphQL testing**
```java
// E2E-тест: запрос к реальному эндпоинту и проверка ответа
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GraphQLE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String graphQLEndpoint;

    @BeforeEach
    void setUp() {
        graphQLEndpoint = "http://localhost:" + port + "/graphql";
    }

    @Test
    void testCompleteUserWorkflow() {
        // 1. Create user
        String createUserMutation = """
            mutation CreateUser($input: CreateUserInput!) {
                createUser(input: $input) {
                    id
                    username
                    email
                    role
                }
            }
            """;

        Map<String, Object> variables = Map.of("input", Map.of(
            "username", "e2euser",
            "email", "e2e@example.com",
            "password", "password123",
            "role", "USER"
        ));

        Map<String, Object> request = Map.of(
            "query", createUserMutation,
            "variables", variables
        );

        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
            graphQLEndpoint, request, Map.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> createData = (Map<String, Object>) createResponse.getBody().get("data");
        Map<String, Object> createdUser = (Map<String, Object>) createData.get("createUser");
        String userId = createdUser.get("id").toString();

        // 2. Query the created user
        String getUserQuery = """
            query GetUser($id: ID!) {
                user(id: $id) {
                    id
                    username
                    email
                    role
                    active
                }
            }
            """;

        Map<String, Object> getRequest = Map.of(
            "query", getUserQuery,
            "variables", Map.of("id", userId)
        );

        ResponseEntity<Map> getResponse = restTemplate.postForEntity(
            graphQLEndpoint, getRequest, Map.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> getData = (Map<String, Object>) getResponse.getBody().get("data");
        Map<String, Object> retrievedUser = (Map<String, Object>) getData.get("user");

        assertThat(retrievedUser.get("username")).isEqualTo("e2euser");
        assertThat(retrievedUser.get("email")).isEqualTo("e2e@example.com");
        assertThat(retrievedUser.get("role")).isEqualTo("USER");
        assertThat(retrievedUser.get("active")).isEqualTo(true);
    }

    @Test
    void testErrorHandling() {
        // Try to get non-existent user
        String getUserQuery = """
            query GetUser($id: ID!) {
                user(id: $id) {
                    id
                    username
                    email
                }
            }
            """;

        Map<String, Object> request = Map.of(
            "query", getUserQuery,
            "variables", Map.of("id", "99999")
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
            graphQLEndpoint, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assertThat(body).containsKey("errors");

        List<Map<String, Object>> errors = (List<Map<String, Object>>) body.get("errors");
        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).get("message")).contains("not found");
    }
}
```

## **Best practices**

### **Schema design**

#### **Schema design principles**
```graphql
# Good: Clear, focused schema
type User {
  id: ID!
  username: String!
  email: Email!
  profile: Profile
  posts: [Post!]!
  createdAt: DateTime!
  updatedAt: DateTime!
}

type Post {
  id: ID!
  title: String!
  content: String!
  author: User!
  tags: [String!]!
  publishedAt: DateTime
  status: PostStatus!
}

# Good: Consistent naming
enum PostStatus {
  DRAFT
  PUBLISHED
  ARCHIVED
}

# Good: Input types for mutations
input CreatePostInput {
  title: String!
  content: String!
  tags: [String!]
}

input UpdatePostInput {
  title: String
  content: String
  tags: [String!]
  status: PostStatus
}

# Good: Pagination
type PostConnection {
  edges: [PostEdge!]!
  pageInfo: PageInfo!
}

type PostEdge {
  node: Post!
  cursor: String!
}

type PageInfo {
  hasNextPage: Boolean!
  hasPreviousPage: Boolean!
  startCursor: String
  endCursor: String
}
```

### **Error handling**

#### **Error handling best practices**
```java
// Глобальная обработка ошибок GraphQL и маппинг в расширения
@Configuration
public class GraphQLErrorHandler {

    @Bean
    public GraphQLErrorCustomizer errorCustomizer() {
        return (error, env) -> {
            if (error instanceof ValidationError) {
                return ValidationError.newValidationError()
                    .message("Validation error: " + error.getMessage())
                    .extensions(Map.of("code", "VALIDATION_ERROR"))
                    .build();
            }

            if (error instanceof UserNotFoundException) {
                return GraphqlErrorBuilder.newError()
                    .message("User not found")
                    .extensions(Map.of("code", "USER_NOT_FOUND"))
                    .errorType(ErrorType.DataFetchingException)
                    .build();
            }

            if (error instanceof AccessDeniedException) {
                return GraphqlErrorBuilder.newError()
                    .message("Access denied")
                    .extensions(Map.of("code", "ACCESS_DENIED"))
                    .errorType(ErrorType.DataFetchingException)
                    .build();
            }

            // Default error
            return GraphqlErrorBuilder.newError()
                .message("Internal server error")
                .extensions(Map.of("code", "INTERNAL_ERROR"))
                .errorType(ErrorType.DataFetchingException)
                .build();
        };
    }
}

@Service
public class ErrorHandlingService {

    public <T> T executeWithErrorHandling(Supplier<T> operation, String operationName) {
        try {
            return operation.get();
        } catch (UserNotFoundException e) {
            throw new GraphQLException("User not found: " + e.getUserId());
        } catch (ValidationException e) {
            throw new GraphQLException("Validation failed: " + e.getMessage());
        } catch (DataAccessException e) {
            // Log the error
            logger.error("Database error in {}: {}", operationName, e.getMessage());
            throw new GraphQLException("Database error occurred");
        } catch (Exception e) {
            // Log unexpected errors
            logger.error("Unexpected error in {}: {}", operationName, e.getMessage(), e);
            throw new GraphQLException("An unexpected error occurred");
        }
    }
}
```

### **Performance optimization**

#### **Performance best practices**
```java
// Настройка кэширования и лимитов для производительности
@Configuration
public class GraphQLPerformanceConfig {

    @Bean
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();

        // Batch user loading
        registry.register("userLoader",
            DataLoader.newDataLoader((List<Long> userIds) ->
                CompletableFuture.supplyAsync(() ->
                    userService.findByIds(userIds))));

        // Batch post loading
        registry.register("postLoader",
            DataLoader.newDataLoader((List<Long> postIds) ->
                CompletableFuture.supplyAsync(() ->
                    postService.findByIds(postIds))));

        return registry;
    }

    @Bean
    public Instrumentation queryComplexityInstrumentation() {
        return new QueryComplexityInstrumentation(100); // Max complexity
    }

    @Bean
    public Instrumentation tracingInstrumentation() {
        return new TracingInstrumentation();
    }

    @Bean
    public Instrumentation cacheInstrumentation(CacheManager cacheManager) {
        return new CacheInstrumentation(cacheManager.getCache("graphql"));
    }
}

@Service
public class OptimizedUserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable("users")
    public List<User> findAll(Boolean includeInactive) {
        // Cached method
        return userRepository.findAll(buildSpec(includeInactive));
    }

    @Cacheable("user")
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> findByIds(Collection<Long> ids) {
        return userRepository.findAllById(ids);
    }

    private Specification<User> buildSpec(Boolean includeInactive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (includeInactive == null || !includeInactive) {
                predicates.add(cb.equal(root.get("active"), true));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

## Решение проблем

### Распространенные проблемы

#### **Schema validation errors**

**Symptoms:**
- **Schema parsing fails with validation errors**

**Solutions:**
```java
// Валидация схемы при старте приложения
@Configuration
public class SchemaValidationConfig {

    @Bean
    public GraphQLSchema schema() {
        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();

        try {
            // Validate schema before building
            String schemaString = loadSchemaFile();
            TypeDefinitionRegistry registry = new SchemaParser().parse(schemaString);
            RuntimeWiring wiring = buildRuntimeWiring();

            // Check for missing resolvers
            SchemaValidationErrorCollector collector = new SchemaValidationErrorCollector();
            SchemaValidator validator = new SchemaValidator();
            validator.validateSchema(collector, registry, wiring);

            if (collector.getErrors().size() > 0) {
                System.err.println("Schema validation errors:");
                collector.getErrors().forEach(error ->
                    System.err.println("  - " + error.getDescription()));
                throw new RuntimeException("Schema validation failed");
            }

            return schemaBuilder
                .query(queryType())
                .mutation(mutationType())
                .subscription(subscriptionType())
                .additionalTypes(buildAdditionalTypes(registry))
                .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to build GraphQL schema", e);
        }
    }

    private String loadSchemaFile() {
        // Load schema from classpath
        try (InputStream is = getClass().getResourceAsStream("/schema.graphqls")) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema file", e);
        }
    }
}
```

#### N+1 **query problem**

**Symptoms:**
- **GraphQL queries cause excessive database queries**

**Solutions:**
```java
// DataLoader для пакетной загрузки и устранения N+1
@Configuration
public class DataLoaderConfig {

    @Bean
    public DataLoaderRegistry dataLoaderRegistry(
            UserDataLoader userDataLoader,
            PostDataLoader postDataLoader,
            CommentDataLoader commentDataLoader) {

        DataLoaderRegistry registry = new DataLoaderRegistry();

        registry.register("user", userDataLoader);
        registry.register("post", postDataLoader);
        registry.register("comment", commentDataLoader);

        return registry;
    }
}

@Component
public class UserDataLoader extends DataLoader<Long, User> {

    private final UserService userService;

    public UserDataLoader(UserService userService) {
        super(DataLoaderOptions.newOptions()
            .setMaxBatchSize(100)  // Batch up to 100 IDs
            .setBatchingEnabled(true));

        this.userService = userService;
    }

    @Override
    protected CompletionStage<List<User>> load(List<Long> keys) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<User> users = userService.findByIds(keys);
                // Sort results to match key order
                Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));

                return keys.stream()
                    .map(userMap::get)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                // Handle errors gracefully
                logger.error("Failed to batch load users", e);
                return keys.stream().map(id -> null).collect(Collectors.toList());
            }
        });
    }
}

@Component
public class UserResolver implements GraphQLResolver<Post> {

    @Autowired
    private DataLoader<Long, User> userDataLoader;

    public CompletableFuture<User> author(Post post) {
        return userDataLoader.load(post.getAuthorId());
    }
}
```

#### **Introspection security**

**Symptoms:**
- **Schema information** is **exposed** in **production**

**Solutions:**
```java
// Отключение introspection в prod для безопасности
@Configuration
public class SecurityConfig {

    @Bean
    public GraphQL graphQL(GraphQLSchema schema) {
        return GraphQL.newGraphQL(schema)
            .instrumentation(new IntrospectionProtectionInstrumentation())
            .build();
    }
}

public class IntrospectionProtectionInstrumentation implements Instrumentation {

    @Override
    public ExecutionResult instrumentExecutionResult(
            ExecutionResult executionResult,
            InstrumentationExecutionParameters parameters) {

        // Check if this is an introspection query
        Document document = parameters.getQuery();
        if (isIntrospectionQuery(document)) {
            // Allow introspection only in development
            String environment = System.getProperty("environment", "production");
            if (!"development".equals(environment)) {
                throw new GraphQLException("Introspection queries are not allowed in production");
            }
        }

        return executionResult;
    }

    private boolean isIntrospectionQuery(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .anyMatch(op -> containsIntrospectionFields(op.getSelectionSet()));
    }

    private boolean containsIntrospectionFields(SelectionSet selectionSet) {
        return selectionSet.getSelections().stream()
            .filter(sel -> sel instanceof Field)
            .map(sel -> (Field) sel)
            .anyMatch(field -> "__schema".equals(field.getName()) ||
                              "__type".equals(field.getName()));
    }
}
```

### **Debug techniques**

#### **Query analysis**
```java
// Логирование запросов и ошибок для отладки
@Configuration
public class DebugConfig {

    @Bean
    public Instrumentation debugInstrumentation() {
        return new SimpleInstrumentation() {
            @Override
            public ExecutionResult instrumentExecutionResult(
                    ExecutionResult executionResult,
                    InstrumentationExecutionParameters parameters) {

                // Log query execution time
                long startTime = System.currentTimeMillis();
                ExecutionResult result = super.instrumentExecutionResult(executionResult, parameters);
                long executionTime = System.currentTimeMillis() - startTime;

                if (executionTime > 1000) { // Log slow queries
                    System.out.println("Slow GraphQL query: " + executionTime + "ms");
                    System.out.println("Query: " + parameters.getQuery());
                }

                // Add debugging info to extensions
                Map<String, Object> extensions = result.getExtensions();
                if (extensions == null) {
                    extensions = new HashMap<>();
                }
                extensions.put("executionTime", executionTime + "ms");

                return result.transform(builder -> builder.extensions(extensions).build());
            }
        };
    }
}

@RestController
public class GraphQLDebugController {

    @Autowired
    private GraphQL graphQL;

    @PostMapping("/graphql/debug")
    public Map<String, Object> debugGraphQL(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");

        System.out.println("=== GraphQL Debug Request ===");
        System.out.println("Query: " + query);
        System.out.println("Variables: " + variables);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables)
            .build();

        ExecutionResult result = graphQL.execute(executionInput);

        System.out.println("=== GraphQL Debug Response ===");
        System.out.println("Data: " + result.getData());
        System.out.println("Errors: " + result.getErrors());

        Map<String, Object> response = new HashMap<>();
        if (result.getData() != null) {
            response.put("data", result.getData());
        }
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
            response.put("errors", result.getErrors());
        }

        return response;
    }
}
```


## Заключение

**GraphQL** — это мощная альтернатива **REST API**, которая решает многие проблемы традиционных подходов к разработке **API**. **GraphQL** предоставляет гибкий, эффективный и **type-safe** способ коммуникации между клиентом и сервером.

### Ключевые возможности:

1. **Schema-first development** — декларативное определение **API** структуры
2. **Type safety** — строго типизированные запросы и ответы
3. **Client-driven queries** — клиенты запрашивают только нужные данные
4. **Real-time subscriptions** — встроенная поддержка **WebSocket**
5. **Introspection** — **API** может описывать само себя
6. **Federation** — композиция схем из多个 сервисов
7. **Rich tooling** — отличная экосистема инструментов

### Архитектурные преимущества:

#### **API Design**:
- **Single endpoint** — один **endpoint** для всех операций
- **Versionless API** — эволюция без **breaking changes**
- **Declarative schemas** — четкие контракты данных
- **Built-in documentation** — **schema** as **documentation**
- **Type validation** — **compile-time** проверка запросов

#### **Developer Experience**:
- **IntelliSense** — автодополнение в **IDE**
- **Static analysis** — проверка запросов на этапе разработки
- **Mocking** — легкое создание **mock** данных
- **Testing** — мощные инструменты для тестирования
- **Debugging** — детальная информация об ошибках

### Когда использовать **GraphQL**:

✅ **Mobile applications** — Снижение сетевого трафика
✅ **Microservices architecture** — Единый **API gateway**
✅ **Complex data relationships** — Связи между сущностями
✅ **Rapidly evolving APIs** — Частые изменения требований
✅ **Multiple clients** — Разные представления данных
✅ **Real-time features** — **Subscriptions** для **live updates**
✅ **API composition** — **Federation** для **distributed systems**
✅ **Strong typing** — **Type-safe API contracts**

### Когда НЕ использовать:

❌ **`Simple CRUD APIs`** — **Overhead** не оправдан
❌ **File uploads** — Лучше использовать **REST**
❌ **Real-time messaging** — Использовать **WebSocket**/**STOMP**
❌ **Caching at CDN** — **GraphQL** сложнее кешировать
❌ **Legacy systems** — Требует значительных изменений
❌ **Simple clients** — **REST** проще для **basic needs**
❌ **Rate limiting** — Сложнее реализовать на уровне запросов

### **Best practices**:

1. **Schema design** — четкие, **focused** типы и отношения
2. **Query optimization** — **DataLoader** для решения N+1 проблемы
3. **Error handling** — понятные, **structured** ошибки
4. **Security** — аутентификация и авторизация
5. **Performance** — **caching**, **complexity limits**, **monitoring**
6. **Testing** — **unit**, **integration**, **E2E** тесты
7. **Documentation** — **schema** как **living documentation**
8. **Evolution** — **backward-compatible** изменения

### Типы **Operations** по назначению:

#### **Queries** (**Чтение**):
- **Single entity** — получение одного объекта по `ID`
- **List with filters** — список с фильтрацией и пагинацией
- **Related data** — связанные объекты и отношения
- **Aggregated data** — статистика и агрегации

#### **Mutations** (**Изменение**):
- **Create operations** — создание новых сущностей
- **Update operations** — изменение существующих данных
- **Delete operations** — удаление данных
- **Bulk operations** — массовые операции

#### **Subscriptions** (**Подписки**):
- **Entity changes** — уведомления об изменениях
- **Real-time updates** — **live** данные для `UI`
- **Event streams** — потоки событий
- **Collaboration** — **multi-user interactions**

**GraphQL** представляет собой значительный шаг вперед в эволюции **API design**. Он сочетает преимущества **REST** с гибкостью и мощью современных подходов, обеспечивая отличную **developer experience** и эффективную коммуникацию между **frontend** и **backend**. 🚀

**Далее: `Kafka` (**message streaming**)**
