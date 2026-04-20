---
title: "REST API Design"
description: "Лучшие практики проектирования REST API: методы, статусы, версионирование"
tags:
  - rest
  - api
  - http
  - design
  - best-practices
difficulty: "intermediate"
prerequisites: ["java-basics.md"]
next: ["spring/spring-rest.md"]
updated: "2026-04-20"
---

# REST API Design

Кратко: Лучшие практики проектирования **REST API**. **HTTP** методы, статус коды, версионирование, пагинация, **HATEOAS**, документация, безопасность.

## Полезные ссылки

### Спецификации
- [RFC 7231 — Semantics](https://tools.ietf.org/html/rfc7231)
- [RFC 3986 — URI Generic Syntax](https://tools.ietf.org/html/rfc3986)
- [JSON:API Specification](https://jsonapi.org/)

### Обучающие материалы
- [REST API Design (Baeldung)](https://www.baeldung.com/rest-api-design-maturity-model)

### См. также
- [GraphQL](https://graphql.org/) — **GraphQL**
- [gRPC](https://grpc.io/) — **gRPC**
- [[spring-rest|Spring REST]] — **Spring REST**

- [[micronaut-http|Micronaut: HTTP — Controllers, Routing и Request Handling]]
- [[java-http-clients|HTTP-клиенты в Java]]
- [[spring-boot|Spring Boot — Полное руководство]]
- [[quarkus-openapi|Quarkus: OpenAPI — API Documentation]]
## Содержание

- [Принципы REST](#принципы-rest)
  - [Основные принципы](#основные-принципы)
  - [Uniform Interface](#uniform-interface)
- [HTTP методы](#http-методы)
  - [Основные методы](#основные-методы)
  - [Идемпотентность](#идемпотентность)
  - [Безопасные методы](#безопасные-методы)
- [Статус коды](#статус-коды)
  - [1xx Informational](#1xx-informational)
  - [2xx Success](#2xx-success)
  - [3xx Redirection](#3xx-redirection)
  - [4xx Client Error](#4xx-client-error)
  - [5xx Server Error](#5xx-server-error)
- [URI дизайн](#uri-дизайн)
  - [Правила именования](#правила-именования)
  - [Иерархия ресурсов](#иерархия-ресурсов)
  - [Query параметры](#query-параметры)
  - [Matrix параметры](#matrix-параметры)
- [Версионирование](#версионирование)
  - [Способы версионирования](#способы-версионирования)
    - [1. URI Versioning](#1-uri-versioning)
    - [2. Query Parameter Versioning](#2-query-parameter-versioning)
    - [3. Header Versioning](#3-header-versioning)
    - [4. Content Negotiation](#4-content-negotiation)
    - [5. Media Type Versioning](#5-media-type-versioning)
  - [Рекомендации](#рекомендации)
- [Пагинация](#пагинация)
  - [Offset-based Pagination](#offset-based-pagination)
  - [Cursor-based Pagination](#cursor-based-pagination)
  - [Keyset Pagination](#keyset-pagination)
- [Фильтрация и сортировка](#фильтрация-и-сортировка)
  - [Фильтры](#фильтры)
  - [Сортировка](#сортировка)
  - [Реализация в Spring Boot](#реализация-в-spring-boot)
- [HATEOAS](#hateoas)
  - [Spring HATEOAS](#spring-hateoas)
- [Обработка ошибок](#обработка-ошибок)
  - [Структурированные ошибки](#структурированные-ошибки)
  - [RFC 7807 Problem Details](#rfc-7807-problem-details)
  - [Spring Boot Error Handling](#spring-boot-error-handling)
- [Документация](#документация)
  - [OpenAPI/Swagger](#openapiswagger)
  - [Spring Boot OpenAPI](#spring-boot-openapi)
- [Безопасность](#безопасность)
  - [Authentication](#authentication)
  - [Rate Limiting](#rate-limiting)
  - [CORS](#cors)
  - [Input Validation](#input-validation)
- [Производительность](#производительность)
  - [Кэширование](#кэширование)
  - [HTTP Caching](#http-caching)
  - [Compression](#compression)
- [Тестирование](#тестирование)
  - [Unit Testing](#unit-testing)
  - [Integration Testing](#integration-testing)
  - [API Testing with REST Assured](#api-testing-with-rest-assured)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
  - [404 на корректных URI](#404-на-корректных-uri)
  - [Нестабильное кэширование ETag/Last-Modified](#нестабильное-кэширование-etaglast-modified)
  - [Ошибки валидации без деталей](#ошибки-валидации-без-деталей)
  - [Просадки при offset-based пагинации](#просадки-при-offset-based-пагинации)
  - [Клиент не знает доступные действия](#клиент-не-знает-доступные-действия)
- [Примеры](#примеры)
  - [Полный REST API с Spring Boot](#полный-rest-api-с-spring-boot)
  - [DTO классы](#dto-классы)
  - [Service класс](#service-класс)
  - [Exception классы](#exception-классы)
  - [Configuration классы](#configuration-классы)

## Принципы REST

**REST** (Representational State Transfer) — архитектурный стиль для проектирования сетевых приложений.

### Основные принципы

1. **Client-Server**: Разделение клиентской и серверной логики
2. **Stateless**: Каждый запрос содержит всю необходимую информацию
3. **Cacheable**: Ответы могут кэшироваться
4. **Uniform Interface**: Единообразный интерфейс
5. **Layered System**: Многоуровневая архитектура
6. **Code on Demand**: Код может передаваться по запросу (опционально)

### Uniform Interface

- **Resource Identification**: Ресурсы идентифицируются **URI**
- **Resource Manipulation through Representations**: ресурсы манипулируются через представления
- **Self-descriptive Messages**: сообщения самоописательны
- **Hypermedia as the Engine of Application State**: **HATEOAS**

## HTTP методы

### Основные методы

Пример использования **HTTP** методов для ресурса (REST).

```http
GET    /users       # Получить список пользователей
GET    /users/123   # Получить пользователя с ID 123
POST   /users       # Создать нового пользователя
PUT    /users/123   # Обновить пользователя с ID 123
PATCH  /users/123   # Частично обновить пользователя
DELETE /users/123   # Удалить пользователя
```

### Идемпотентность

- **GET**: Идемпотентен (повтор безопасен)
- **PUT**: Идемпотентен
- **DELETE**: Идемпотентен
- **POST**: Не идемпотентен
- **PATCH**: Может быть идемпотентен

### Безопасные методы

- **GET**: Безопасен (не изменяет состояние)
- **HEAD**: Безопасен
- **OPTIONS**: Безопасен
- **POST** / **PUT** / **PATCH** / **DELETE**: не идемпотентны (кроме PUT на один и тот же URI)

## Статус коды

### 1xx Informational

```http
# Информационные коды: запрос принят, продолжение или смена протокола
100 Continue
101 Switching Protocols
```

### 2xx Success

```http
200 OK                    # Успешный запрос
201 Created              # Ресурс создан
202 Accepted             # Запрос принят в обработку
204 No Content           # Нет содержимого в ответе
```

### 3xx Redirection

```http
301 Moved Permanently    # Ресурс перемещен навсегда
302 Found               # Ресурс найден по другому URI
304 Not Modified        # Ресурс не изменялся
```

### 4xx Client Error

```http
400 Bad Request         # Неверный запрос
401 Unauthorized        # Не авторизован
403 Forbidden           # Доступ запрещен
404 Not Found           # Ресурс не найден
409 Conflict            # Конфликт с текущим состоянием
422 Unprocessable Entity # Невалидные данные
429 Too Many Requests   # Слишком много запросов
```

### 5xx Server Error

```http
500 Internal Server Error  # Внутренняя ошибка сервера
502 Bad Gateway           # Плохой шлюз
503 Service Unavailable   # Сервис недоступен
504 Gateway Timeout       # Таймаут шлюза
```

## URI дизайн

### Правила именования

```http
# ✅ Правильно
GET /users
GET /users/123
GET /users/123/posts
POST /users
PUT /users/123
DELETE /users/123

# ❌ Неправильно
GET /getUsers
GET /user/123/posts/get
POST /createUser
PUT /updateUser/123
```

### Иерархия ресурсов

```http
# Плоские ресурсы
/users
/products

# Вложенные ресурсы
/users/123/posts
/products/456/reviews

# Не вложенные - отдельные ресурсы
/users/123/profile  # Профиль пользователя
/products/456/images # Изображения продукта
```

### Query параметры

```http
# Фильтрация
GET /users?status=active
GET /products?category=electronics&price_lt=1000

# Сортировка
GET /users?sort=name,asc
GET /products?sort=price,desc&sort=name,asc

# Пагинация
GET /users?page=1&size=20
GET /products?offset=0&limit=50

# Поиск
GET /users?q=john
GET /products?search=laptop
```

### Matrix параметры

```http
# Для сложных запросов
GET /products;color=red,blue;size=large
GET /cars;make=toyota;model=camry;year=2020
```

## Версионирование

### Способы версионирования

#### 1. URI Versioning

```http
# Версия в пути
GET /v1/users
GET /v2/users

# Префикс API
GET /api/v1/users
GET /api/v2/users
```

#### 2. Query Parameter Versioning

```http
# Версия в query-параметре
GET /users?version=1
GET /users?version=2
```

#### 3. Header Versioning

```http
# Версия в заголовке Accept (vendor MIME)
GET /users
Accept: application/vnd.company.users.v1+json

GET /users
Accept: application/vnd.company.users.v2+json
```

#### 4. Content Negotiation

```http
# Согласование контента через Accept и Content-Type
GET /users
Accept: application/vnd.company.users.v1+json

POST /users
Content-Type: application/vnd.company.users.v1+json
```

#### 5. Media Type Versioning

```http
# Версия в параметре media type
GET /users
Accept: application/vnd.company.users+json; version=1.0

POST /users
Content-Type: application/vnd.company.users+json; version=1.0
```

### Рекомендации

1. **URI versioning** — наиболее распространенный
2. **Не используйте query parameters** для версий
3. **Поддерживайте** несколько версий одновременно
4. **Документируйте** изменения между версиями
5. **Используйте semantic versioning** (MAJOR.`MINOR`.PATCH)

## Пагинация

### Offset-based Pagination

```http
# Offset-пагинация: page и size
GET /users?page=1&size=20
GET /users?page=2&size=20
```

```json
// Ответ с массивом данных и метаданными пагинации
{
  "data": [...],
  "pagination": {
    "page": 1,
    "size": 20,
    "total": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrev": false
  }
}
```

### Cursor-based Pagination

```http
# Пагинация по курсору (opaque token)
GET /users?cursor=eyJpZCI6MTIzfQ==&limit=20
GET /users?cursor=eyJpZCI6MTQzfQ==&limit=20
```

```json
// Ответ с курсором для следующей страницы
{
  "data": [...],
  "pagination": {
  "nextCursor": "eyJpZCI6MTQzfQ==",
    "hasNext": true,
    "limit": 20
  }
}
```

### Keyset Pagination

```http
# Пагинация по ключу (ID) — стабильная при вставках
GET /users?after=123&limit=20
GET /users?after=143&limit=20
```

```json
// Ответ с идентификатором для следующей страницы
{
  "data": [...],
  "pagination": {
    "after": 143,
    "hasNext": true,
    "limit": 20
  }
}
```

## Фильтрация и сортировка

### Фильтры

```http
# Простые фильтры
GET /users?status=active
GET /products?category=electronics

# Диапазоны
GET /products?price_gte=100&price_lte=500
GET /users?age_gt=18&age_lt=65

# Массивы
GET /products?tags=laptop,gaming
GET /users?roles=admin,moderator

# Поиск
GET /users?q=john
GET /products?search=laptop&category=electronics
```

### Сортировка

```http
# Одиночная сортировка
GET /users?sort=name
GET /users?sort=-createdAt  # desc

# Множественная сортировка
GET /users?sort=name,createdAt
GET /products?sort=price,-rating,name
```

### Реализация в Spring Boot

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String[] sort) {

        // Создание спецификации для фильтров
        Specification<User> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), UserStatus.valueOf(status.toUpperCase())));
        }

        if (search != null) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + search.toLowerCase() + "%")
                ));
        }

        // Сортировка
        List<Order> orders = new ArrayList<>();
        for (String sortField : sort) {
            if (sortField.startsWith("-")) {
                orders.add(Order.desc(sortField.substring(1)));
            } else {
                orders.add(Order.asc(sortField));
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<UserDto> userDtoPage = userPage.map(UserMapper::toDto);

        return ResponseEntity.ok(userDtoPage);
    }
}
```

## HATEOAS

**HATEOAS** (Hypermedia as the `Engine of Application` State) — принцип, где **API** предоставляет ссылки для навигации.

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "_links": {
    "self": {
      "href": "http://api.example.com/users/1"
    },
    "update": {
      "href": "http://api.example.com/users/1",
      "method": "PUT"
    },
    "delete": {
      "href": "http://api.example.com/users/1",
      "method": "DELETE"
    },
    "posts": {
      "href": "http://api.example.com/users/1/posts"
    }
  }
}
```

### Spring HATEOAS

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public EntityModel<UserDto> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDto userDto = UserMapper.toDto(user);

        EntityModel<UserDto> model = EntityModel.of(userDto);
        model.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserPosts(id)).withRel("posts"));
        model.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));

        return model;
    }

    @GetMapping("/{id}/posts")
    public CollectionModel<PostDto> getUserPosts(@PathVariable Long id) {
        List<Post> posts = postService.findByUserId(id);
        List<PostDto> postDtos = posts.stream()
            .map(PostMapper::toDto)
            .collect(Collectors.toList());

        CollectionModel<PostDto> model = CollectionModel.of(postDtos);
        model.add(linkTo(methodOn(UserController.class).getUserPosts(id)).withSelfRel());

        return model;
    }
}
```

## Обработка ошибок

### Структурированные ошибки

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid"
      },
      {
        "field": "password",
        "message": "Password must be at least 8 characters"
      }
    ]
  }
}
```

### RFC `7807` Problem Details

```json
{
  "type": "https://api.example.com/errors/validation-error",
  "title": "Validation Error",
  "detail": "One or more validation errors occurred",
  "instance": "/api/users",
  "status": 400,
  "errors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    }
  ]
}
```

### Spring Boot Error Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .code("USER_NOT_FOUND")
            .message(ex.getMessage())
            .status(404)
            .build();

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(fieldError -> FieldError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build())
            .collect(Collectors.toList());

        ValidationErrorResponse error = ValidationErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message("Validation failed")
            .status(400)
            .fieldErrors(fieldErrors)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .status(500)
            .build();

        return ResponseEntity.status(500).body(error);
    }
}
```

## Документация

### OpenAPI/Swagger

```yaml
openapi: 3.0.3
info:
  title: User API
  version: 1.0.0
  description: API for managing users

servers:
  - url: http://api.example.com
    description: Production server

paths:
  /users:
    get:
      summary: Get users
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserPage'

  /users/{id}:
    get:
      summary: Get user by ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
        '404':
          description: User not found

components:
  schemas:
    User:
      type: object
      properties:
        id:
          type: integer
        name:
          type: string
        email:
          type: string

    UserPage:
      type: object
      properties:
        content:
          type: array
          items:
            $ref: '#/components/schemas/User'
        totalElements:
          type: integer
        totalPages:
          type: integer
        size:
          type: integer
        number:
          type: integer
```

### Spring Boot OpenAPI

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("User API")
                .version("1.0.0")
                .description("API for managing users")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@example.com")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Development server"),
                new Server().url("https://api.example.com").description("Production server")
            ));
    }
}
```

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Operations with users")
public class UserController {

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {
        // implementation
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Valid CreateUserRequest request) {
        // implementation
    }
}
```

## Безопасность

### Authentication

```http
# Basic Authentication
GET /api/users
Authorization: Basic dXNlcjpwYXNzd29yZA==

# Bearer Token
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# API Key
GET /api/users
X-API-Key: your-api-key
```

### Rate Limiting

```yaml
# application.yml
resilience4j:
  ratelimiter:
    instances:
      api:
        limitForPeriod: 10
        limitRefreshPeriod: 60s
        timeoutDuration: 0s
```

```java
@RestController
@RequestMapping("/api")
@RateLimiter(name = "api")
public class ApiController {
    // endpoints
}
```

### CORS

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/")
            .allowedOrigins("http://localhost:3000", "https://example.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

### Input Validation

```java
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    // getters and setters
}
```

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        // validation happens automatically
    }
}
```

## Производительность

### Кэширование

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    @Cacheable(value = "users", key = "#id")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        // implementation
    }

    @PostMapping
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
        // implementation
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                             @RequestBody UpdateUserRequest request) {
        // implementation
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "users", key = "#id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // implementation
    }
}
```

### HTTP Caching

```java
@GetMapping("/{id}")
public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
    UserDto user = userService.findById(id);

    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .eTag(generateETag(user))
        .lastModified(user.getLastModified())
        .body(user);
}
```

### Compression

```yaml
# application.yml
server:
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
    min-response-size: 1024
```

## Тестирование

### Unit Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser_ShouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    public void getUser_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }
}
```

### Integration Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void fullUserLifecycle() {
        // Create user
        CreateUserRequest request = new CreateUserRequest("Jane", "jane@example.com");
        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity(
            "/api/users", request, UserDto.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        UserDto createdUser = createResponse.getBody();

        // Get user
        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity(
            "/api/users/" + createdUser.getId(), UserDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Jane", getResponse.getBody().getName());
    }
}
```

### API Testing with REST Assured

```java
public class UserApiTest {

    @Test
    public void testUserApi() {
        // Create user
        given()
            .contentType(ContentType.JSON)
            .body(new CreateUserRequest("Bob", "bob@example.com"))
        .when()
            .post("/api/users")
        .then()
            .statusCode(200)
            .body("name", equalTo("Bob"))
            .body("email", equalTo("bob@example.com"));

        // Get all users
        given()
        .when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }
}
```

## Лучшие практики

1. **Используйте правильные `HTTP` методы** — **GET** для чтения, **POST** для создания, **PUT**/**PATCH** для обновления, **DELETE** для удаления
2. **Возвращайте правильные статус коды** - `200` для успеха, `404` для не найденных ресурсов, `400` для ошибок валидации
3. **Дизайн URI иерархично** — `/resources/id` вместо плоских идентификаторов
4. **Версионируйте API** — используйте **URI versioning** для **breaking changes**
5. **Пагинируйте большие списки** — не возвращайте тысячи записей за раз
6. **Фильтруйте и сортируйте** — предоставьте параметры для фильтрации и сортировки
7. **Документируйте API** — используйте **OpenAPI**/**Swagger**
8. **Обработайте ошибки правильно** — возвращайте структурированные ошибки
9. **Валидируйте входные данные** — на уровне контроллеров и **DTO**
10. **Кэшируйте ответы** — используйте **HTTP caching** и **application-level caching**
11. **Защищайте API** — используйте **authentication** и **authorization**
12. **Мониторьте производительность** — логируйте медленные запросы
13. **Тестируйте API** — **unit**, **integration** и **API** тесты
14. **Следуйте принципам REST** — ресурсы, **stateless**, **uniform interface**
15. **Используйте HATEOAS** — для **discoverable API**

## Решение проблем

### 404 на корректных URI

**Проблема:** Клиент получает `404` на запросы к валидным ресурсам (например, `GET /users/123`).

**Причины:** Несогласованность trailing slash (`/users/123` vs `/users/123/`); неправильная настройка роутинга; конфликт с catch-all route.

**Решение:** Выбрать единый стиль (без trailing slash — рекомендуется); настроить редирект `301` с другого варианта; проверять порядок объявления маршрутов; использовать `@GetMapping` с явным `path` вместо `/`.

### Нестабильное кэширование ETag/Last-Modified

**Проблема:** Клиент не получает `304 Not Modified` при повторных запросах, хотя данные не изменились.

**Причины:** **ETag** вычисляется по разным полям (например, без `updatedAt`); `Last-Modified` имеет разрешение только в секундах; middleware или прокси перезаписывают заголовки.

**Решение:** Вычислять **ETag** детерминированно по всем полям представления; использовать `Instant`/`ZonedDateTime` для `Last-Modified`; убедиться, что `Cache-Control`, `ETag`, `Last-Modified` не перезаписываются после установки.

### Ошибки валидации без деталей

**Проблема:** Ответ `400` или `422` содержит только `"message": "Validation failed"` без указания полей и причин.

**Причины:** Обработка `MethodArgumentNotValidException` без маппинга `FieldError`; централизованный handler возвращает обобщённый текст.

**Решение:** Возвращать структурированные ошибки (RFC 7807 или формат с `field`, `message`); использовать `@Valid` и `BindingResult`; в `@ControllerAdvice` собирать `fieldErrors` из `ex.getBindingResult().getFieldErrors()`.

### Просадки при offset-based пагинации

**Проблема:** Запросы `?page=100&size=20` выполняются медленно (десятки секунд).

**Причины:** `LIMIT x OFFSET y` заставляет БД сканировать и пропускать все предыдущие строки; на больших смещениях возрастает нагрузка.

**Решение:** Использовать **cursor-based** или **keyset** пагинацию (`?after=lastId&limit=20`); для read-only списков — материализованные представления или кэш; ограничить максимальный `page` или `offset`.

### Клиент не знает доступные действия

**Проблема:** Клиент хардкодит **URI** и методы; при изменении API ломается интеграция.

**Причины:** Отсутствие **HATEOAS**; документация не синхронизирована с реализацией.

**Решение:** Включать `_links` в ответы (Spring HATEOAS, HAL); документировать ссылки в **OpenAPI**; возвращать `Allow` в ответах `OPTIONS` и `405`.

## Примеры

### Полный REST API с Spring Boot

```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        Page<User> userPage = userService.findUsers(status, search, pageable);

        PagedModel<EntityModel<UserDto>> pagedModel =
            pagedResourcesAssembler.toModel(userPage.map(userMapper::toDto));

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
            .body(userPage.map(userMapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDto userDto = userMapper.toDto(user);

        EntityModel<UserDto> model = EntityModel.of(userDto);
        model.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        model.add(linkTo(methodOn(UserController.class).getUserPosts(id)).withRel("posts"));

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
            .eTag(generateETag(user))
            .lastModified(user.getUpdatedAt())
            .body(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        User user = userService.createUser(request);
        UserDto userDto = userMapper.toDto(user);

        EntityModel<UserDto> model = EntityModel.of(userDto);
        model.add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());

        return ResponseEntity.created(
                linkTo(methodOn(UserController.class).getUser(user.getId())).toUri())
            .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = userService.updateUser(id, request);
        UserDto userDto = userMapper.toDto(user);

        EntityModel<UserDto> model = EntityModel.of(userDto);
        model.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String[] sortParams) {
        List<Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            if (param.startsWith("-")) {
                orders.add(Order.desc(param.substring(1)));
            } else {
                orders.add(Order.asc(param));
            }
        }
        return Sort.by(orders);
    }

    private String generateETag(User user) {
        return String.valueOf(user.hashCode());
    }
}
```

### DTO классы

```java
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // getters and setters
}

public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // getters and setters
}

public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    // getters and setters
}
```

### Service класс

```java
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> findUsers(String status, String search, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), UserStatus.valueOf(status.toUpperCase())));
        }

        if (search != null) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + search.toLowerCase() + "%")
                ));
        }

        return userRepository.findAll(spec, pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = findById(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
```

### Exception классы

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("User with email already exists: " + email);
    }
}
```

### Configuration классы

```java
@Configuration
public class ApiConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "posts");
    }
}
```

Этот файл содержит детальное описание лучших практик проектирования **REST API**: от основных принципов **HTTP** до полной реализации с **Spring Boot**, включая версионирование, пагинацию, **HATEOAS**, обработку ошибок, документацию, безопасность и тестирование. Он охватывает все ключевые аспекты создания качественных **REST API**.
