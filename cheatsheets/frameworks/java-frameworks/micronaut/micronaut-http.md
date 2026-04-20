---
title: "Micronaut: HTTP - Controllers, Routing и Request Handling"
description: "Полное руководство по созданию HTTP контроллеров, роутинга, обработки запросов и ответов в Micronaut"
tags:
  - micronaut
  - http
  - controllers
  - routing
  - rest
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-data.md", "micronaut-security.md"]
updated: "2026-02-11"
related: ["micronaut-reactive.md", "micronaut-testing.md"]
---

# Micronaut: HTTP — Controllers, Routing и Request Handling

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: HTTP — Controllers, Routing и Request Handling](#micronaut-http-controllers-routing-и-request-handling)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Controllers](#controllers)
  - [Базовый Controller](#базовый-controller)
  - [HTTP Methods](#http-methods)
- [Routing](#routing)
  - [Path Variables](#path-variables)
  - [Query Parameters](#query-parameters)
  - [Request Headers](#request-headers)
  - [Request Body](#request-body)
- [Request и Response](#request-и-response)
  - [HttpResponse](#httpresponse)
  - [HttpRequest](#httprequest)
- [Content Negotiation](#content-negotiation)
  - [JSON Serialization](#json-serialization)
  - [XML Support](#xml-support)
  - [Custom Media Types](#custom-media-types)
- [Validation](#validation)
  - [Bean Validation](#bean-validation)
- [Filters](#filters)
  - [HTTP Server Filters](#http-server-filters)
  - [Authentication Filter](#authentication-filter)
- [Interceptors](#interceptors)
  - [Method Interceptors](#method-interceptors)
- [Error Handling](#error-handling)
  - [Global Exception Handler](#global-exception-handler)
  - [Controller-level Exception Handling](#controller-level-exception-handling)
- [Async Operations](#async-operations)
  - [Reactive Controllers](#reactive-controllers)
  - [CompletableFuture Support](#completablefuture-support)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте HttpResponse для явного контроля](#1-используйте-httpresponse-для-явного-контроля)
  - [2. Валидируйте входные данные](#2-валидируйте-входные-данные)
  - [3. Используйте Filters для Cross-cutting Concerns](#3-используйте-filters-для-cross-cutting-concerns)
  - [4. Централизованная обработка ошибок](#4-централизованная-обработка-ошибок)
  - [5. Используйте правильные HTTP статусы](#5-используйте-правильные-http-статусы)
- [Advanced Topics](#advanced-topics)
  - [Streaming Responses](#streaming-responses)
  - [Multipart File Upload](#multipart-file-upload)
  - [Custom Media Type Handlers](#custom-media-type-handlers)
  - [Request/Response Interceptors](#requestresponse-interceptors)
  - [CORS Configuration](#cors-configuration)
  - [Rate Limiting](#rate-limiting)
  - [WebSocket Support](#websocket-support)
- [Server-Sent Events (SSE)](#server-sent-events-sse)
  - [SSE Endpoint](#sse-endpoint)
- [HTTP Client](#http-client)
  - [Declarative HTTP Client](#declarative-http-client)
  - [Reactive HTTP Client](#reactive-http-client)
- [HTTP Client Configuration](#http-client-configuration)
  - [Client Configuration](#client-configuration)
  - [Custom HTTP Client](#custom-http-client)
- [HTTP/2 Support](#http2-support)
  - [HTTP/2 Configuration](#http2-configuration)
  - [Custom Interceptors](#custom-interceptors)
- [HTTP Compression](#http-compression)
  - [Compression Configuration](#compression-configuration)
- [HTTP Caching](#http-caching)
  - [Cache Headers](#cache-headers)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет мощный и гибкий **HTTP** сервер на основе **Netty**, который поддерживает как блокирующие, так и реактивные операции. **HTTP** контроллеры в **Micronaut** используют **compile-time** обработку, что обеспечивает высокую производительность и минимальное потребление памяти.

### Основные возможности

- **Type-safe routing**: Роутинг на основе аннотаций с проверкой типов
- **Request/Response handling**: гибкая обработка **HTTP** запросов и ответов
- **Content negotiation**: Автоматическая сериализация/десериализация
- **Filters и Interceptors**: **Middleware** для обработки запросов
- **Error handling**: Централизованная обработка ошибок
- **Validation**: Интеграция с **Bean Validation**
- **Async support**: Поддержка асинхронных операций

## Controllers

### Базовый Controller

```java
// Контроллер с возвратом HttpResponse (статус и тело)
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpResponse;

@Controller("/api/users")
public class UserController {

    @Get
    public HttpResponse<List<User>> getAllUsers() {
        List<User> users = Arrays.asList(
            new User(1L, "John", "john@example.com"),
            new User(2L, "Jane", "jane@example.com")
        );
        return HttpResponse.ok(users);
    }

    @Get("/{id}")
    public HttpResponse<User> getUser(Long id) {
        User user = new User(id, "John", "john@example.com");
        return HttpResponse.ok(user);
    }

    @Post
    public HttpResponse<User> createUser(@Body User user) {
        // Создание пользователя
        return HttpResponse.created(user);
    }

    @Put("/{id}")
    public HttpResponse<User> updateUser(Long id, @Body User user) {
        // Обновление пользователя
        return HttpResponse.ok(user);
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteUser(Long id) {
        // Удаление пользователя
        return HttpResponse.noContent();
    }
}
```

### HTTP Methods

**Micronaut** поддерживает все стандартные **HTTP** методы:**

```java
import io.micronaut.http.annotation.*;

@Controller("/api/products")
public class ProductController {

    @Get
    public List<Product> getAll() {
        return productService.findAll();
    }

    @Post
    public Product create(@Body Product product) {
        return productService.save(product);
    }

    @Put("/{id}")
    public Product update(Long id, @Body Product product) {
        return productService.update(id, product);
    }

    @Patch("/{id}")
    public Product patch(Long id, @Body Product product) {
        return productService.patch(id, product);
    }

    @Delete("/{id}")
    public void delete(Long id) {
        productService.delete(id);
    }

    @Head("/{id}")
    public HttpResponse<?> head(Long id) {
        if (productService.exists(id)) {
            return HttpResponse.ok();
        }
        return HttpResponse.notFound();
    }

    @Options
    public HttpResponse<?> options() {
        return HttpResponse.ok()
            .header("Allow", "GET, POST, PUT, DELETE, OPTIONS");
    }
}
```

## Routing

### Path Variables

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Get("/{id}")
    public User getUser(Long id) {
        return userService.findById(id);
    }

    @Get("/{userId}/orders/{orderId}")
    public Order getUserOrder(Long userId, Long orderId) {
        return orderService.findByUserAndOrder(userId, orderId);
    }

    @Get("/{id}/posts/{postId}/comments/{commentId}")
    public Comment getComment(Long id, Long postId, Long commentId) {
        return commentService.find(id, postId, commentId);
    }
}
```

### Query Parameters

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Get
    public List<User> search(
            @QueryValue String name,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "20") int size) {
        return userService.search(name, page, size);
    }

    @Get("/filter")
    public List<User> filter(
            @QueryValue Optional<String> name,
            @QueryValue Optional<String> email,
            @QueryValue Optional<Integer> age) {
        return userService.filter(name, email, age);
    }

    @Get("/search")
    public List<User> search(@QueryValue List<String> tags) {
        return userService.searchByTags(tags);
    }
}
```

### Request Headers

```java
@Controller("/api")
public class ApiController {

    @Get("/data")
    public String getData(@Header String authorization) {
        return "Data for: " + authorization;
    }

    @Get("/info")
    public HttpResponse<?> getInfo(@Header("User-Agent") String userAgent) {
        return HttpResponse.ok()
            .header("X-User-Agent", userAgent);
    }

    @Get("/custom")
    public String getCustom(@Header("X-Custom-Header") String custom) {
        return custom;
    }
}
```

### Request Body

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Post
    public User createUser(@Body User user) {
        return userService.save(user);
    }

    @Post("/batch")
    public List<User> createUsers(@Body List<User> users) {
        return userService.saveAll(users);
    }

    @Put("/{id}")
    public User updateUser(Long id, @Body User user) {
        return userService.update(id, user);
    }
}
```

## Request и Response

### HttpResponse

```java
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

@Controller("/api/users")
public class UserController {

    @Get("/{id}")
    public HttpResponse<User> getUser(Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return HttpResponse.ok(user)
                .header("X-Custom-Header", "value");
        }
        return HttpResponse.notFound();
    }

    @Post
    public HttpResponse<User> createUser(@Body User user) {
        User created = userService.save(user);
        return HttpResponse.created(created)
            .header("Location", "/api/users/" + created.getId());
    }

    @Put("/{id}")
    public HttpResponse<User> updateUser(Long id, @Body User user) {
        User updated = userService.update(id, user);
        return HttpResponse.ok(updated);
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteUser(Long id) {
        userService.delete(id);
        return HttpResponse.noContent();
    }

    @Get("/status")
    public HttpResponse<?> getStatus() {
        return HttpResponse.status(HttpStatus.ACCEPTED)
            .body("Processing");
    }
}
```

### HttpRequest

```java
import io.micronaut.http.HttpRequest;

@Controller("/api")
public class ApiController {

    @Get("/request-info")
    public Map<String, Object> getRequestInfo(HttpRequest<?> request) {
        Map<String, Object> info = new HashMap<>();
        info.put("method", request.getMethod());
        info.put("uri", request.getUri());
        info.put("headers", request.getHeaders().asMap());
        info.put("parameters", request.getParameters().asMap());
        return info;
    }

    @Post("/echo")
    public HttpResponse<?> echo(HttpRequest<String> request) {
        String body = request.getBody().orElse("");
        return HttpResponse.ok()
            .body(Map.of("echo", body))
            .header("X-Original-Method", request.getMethod().toString());
    }
}
```

## Content Negotiation

### JSON Serialization

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Get(produces = MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    public User createUser(@Body User user) {
        return userService.save(user);
    }
}
```

### XML Support

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Get(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<User> getAllUsers(HttpRequest<?> request) {
        List<User> users = userService.findAll();
        return users;
    }

    @Post(consumes = MediaType.APPLICATION_XML)
    public User createUser(@Body User user) {
        return userService.save(user);
    }
}
```

### Custom Media Types

```java
@Controller("/api/data")
public class DataController {

    @Get(produces = "application/vnd.api+json")
    public Map<String, Object> getData() {
        return Map.of(
            "data", userService.findAll(),
            "meta", Map.of("count", userService.count())
        );
    }
}
```

## Validation

### Bean Validation

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Controller("/api/users")
public class UserController {

    @Post
    public User createUser(@Valid @Body UserCreateRequest request) {
        return userService.create(request);
    }

    @Put("/{id}")
    public User updateUser(
            @Min(1) Long id,
            @Valid @Body UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @Get("/search")
    public List<User> search(
            @NotBlank @QueryValue String query,
            @Min(0) @QueryValue int page,
            @Min(1) @Max(100) @QueryValue int size) {
        return userService.search(query, page, size);
    }
}

public class UserCreateRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Min(18)
    @Max(120)
    private Integer age;

    // Getters and setters...
}
```

## Filters

### HTTP Server Filters

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

@Filter("/api/")
public class LoggingFilter implements HttpServerFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(
            HttpRequest<?> request,
            ServerFilterChain chain) {

        long startTime = System.currentTimeMillis();
        LOG.info("Request: {} {}", request.getMethod(), request.getUri());

        return Publishers.map(
            chain.proceed(request),
            response -> {
                long duration = System.currentTimeMillis() - startTime;
                LOG.info("Response: {} {} ({}ms)",
                    response.status(),
                    request.getUri(),
                    duration);
                return response.header("X-Response-Time", duration + "ms");
            }
        );
    }
}
```

### Authentication Filter

```java
@Filter("/api/")
public class AuthenticationFilter implements HttpServerFilter {

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(
            HttpRequest<?> request,
            ServerFilterChain chain) {

        String authHeader = request.getHeaders().get("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Publishers.just(
                HttpResponse.unauthorized()
                    .body(Map.of("error", "Missing or invalid authorization"))
            );
        }

        String token = authHeader.substring(7);
        if (!isValidToken(token)) {
            return Publishers.just(
                HttpResponse.unauthorized()
                    .body(Map.of("error", "Invalid token"))
            );
        }

        return chain.proceed(request);
    }

    private boolean isValidToken(String token) {
        // Валидация токена
        return true;
    }
}
```

## Interceptors

### Method Interceptors

```java
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;

@Singleton
public class TimingInterceptor implements MethodInterceptor<Object, Object> {

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        long startTime = System.currentTimeMillis();
        try {
            Object result = context.proceed();
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Method " + context.getMethodName() +
                " took " + duration + "ms");
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Method " + context.getMethodName() +
                " failed after " + duration + "ms");
            throw e;
        }
    }
}
```

## Error Handling

### Global Exception Handler

```java
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpResponse;

@Controller
public class ErrorHandler {

    @Error(global = true)
    public HttpResponse<?> handleException(Exception e) {
        return HttpResponse.serverError()
            .body(Map.of(
                "error", "Internal server error",
                "message", e.getMessage()
            ));
    }

    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<?> handleNotFound() {
        return HttpResponse.notFound()
            .body(Map.of("error", "Resource not found"));
    }

    @Error(status = HttpStatus.BAD_REQUEST)
    public HttpResponse<?> handleBadRequest(Exception e) {
        return HttpResponse.badRequest()
            .body(Map.of(
                "error", "Bad request",
                "message", e.getMessage()
            ));
    }
}
```

### Controller-level Exception Handling

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Error(exception = UserNotFoundException.class)
    public HttpResponse<?> handleUserNotFound(UserNotFoundException e) {
        return HttpResponse.notFound()
            .body(Map.of("error", e.getMessage()));
    }

    @Error(exception = ValidationException.class)
    public HttpResponse<?> handleValidation(ValidationException e) {
        return HttpResponse.badRequest()
            .body(Map.of(
                "error", "Validation failed",
                "details", e.getErrors()
            ));
    }
}
```

## Async Operations

### Reactive Controllers

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Controller("/api/users")
public class UserController {

    @Get("/{id}")
    public Mono<User> getUser(Long id) {
        return Mono.fromCallable(() -> userService.findById(id));
    }

    @Get
    public Flux<User> getAllUsers() {
        return Flux.fromIterable(userService.findAll());
    }

    @Get("/stream")
    public Flux<User> streamUsers() {
        return Flux.fromIterable(userService.findAll())
            .delayElements(Duration.ofMillis(100));
    }
}
```

### CompletableFuture Support

```java
// Контроллер пользователей с маппингом путей
@Controller("/api/users")
public class UserController {

    @Get("/{id}")
    public CompletableFuture<User> getUser(Long id) {
        return CompletableFuture.supplyAsync(() ->
            userService.findById(id)
        );
    }

    @Get
    public CompletableFuture<List<User>> getAllUsers() {
        return CompletableFuture.supplyAsync(() ->
            userService.findAll()
        );
    }
}
```

## Лучшие практики

### 1. Используйте HttpResponse для явного контроля

```java
// ✅ Хорошо
@Get("/{id}")
public HttpResponse<User> getUser(Long id) {
    User user = userService.findById(id);
    if (user != null) {
        return HttpResponse.ok(user);
    }
    return HttpResponse.notFound();
}

// ❌ Плохо - неявная обработка ошибок
@Get("/{id}")
public User getUser(Long id) {
    return userService.findById(id); // Может вернуть null
}
```

### 2. Валидируйте входные данные

```java
// ✅ Хорошо
@Post
public User createUser(@Valid @Body UserCreateRequest request) {
    return userService.create(request);
}
```

### 3. Используйте Filters для Cross-cutting Concerns

```java
// ✅ Хорошо - логирование в фильтре
@Filter("/api/")
public class LoggingFilter implements HttpServerFilter {
    // ...
}
```

### 4. Централизованная обработка ошибок

```java
// ✅ Хорошо
@Error(global = true)
public HttpResponse<?> handleException(Exception e) {
    // Централизованная обработка
}
```

### 5. Используйте правильные HTTP статусы

```java
// ✅ Хорошо
@Post
public HttpResponse<User> createUser(@Body User user) {
    User created = userService.save(user);
    return HttpResponse.created(created);
}

@Delete("/{id}")
public HttpResponse<Void> deleteUser(Long id) {
    userService.delete(id);
    return HttpResponse.noContent();
}
```

## Advanced Topics

### Streaming Responses

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import java.io.FileInputStream;
import java.io.InputStream;

@Controller("/api/files")
public class FileController {

    @Get("/download/{filename}")
    public StreamedFile downloadFile(String filename) {
        InputStream inputStream = new FileInputStream("/path/to/" + filename);
        return new StreamedFile(inputStream, MediaType.APPLICATION_OCTET_STREAM)
            .attach(filename);
    }

    @Get("/stream")
    public Publisher<byte[]> streamData() {
        return Flux.range(1, 100)
            .map(i -> ("Data chunk " + i + "\n").getBytes())
            .delayElements(Duration.ofMillis(100));
    }
}
```

### Multipart File Upload

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;

@Controller("/api/upload")
public class UploadController {

    @Post(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> uploadFile(CompletedFileUpload file) {
        String filename = file.getFilename();
        InputStream inputStream = file.getInputStream();

        // Сохранение файла
        saveFile(filename, inputStream);

        return HttpResponse.ok(Map.of(
            "filename", filename,
            "size", file.getSize(),
            "contentType", file.getContentType().orElse("unknown")
        ));
    }

    @Post(value = "/stream", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> uploadStream(StreamingFileUpload file) {
        return HttpResponse.accepted(Map.of(
            "message", "File upload started",
            "filename", file.getFilename()
        ));
    }
}
```

### Custom Media Type Handlers

```java
import io.micronaut.http.MediaType;
import io.micronaut.http.codec.MediaTypeCodec;
import io.micronaut.http.codec.CodecConfiguration;
import jakarta.inject.Singleton;

@Singleton
public class CustomMediaTypeCodec implements MediaTypeCodec {

    @Override
    public MediaType[] getMediaTypes() {
        return new MediaType[] {
            MediaType.of("application", "vnd.custom+json")
        };
    }

    @Override
    public <T> T decode(Class<T> type, InputStream inputStream) {
        // Custom decoding logic
        return null;
    }

    @Override
    public <T> void encode(T object, OutputStream outputStream) {
        // Custom encoding logic
    }
}
```

### Request/Response Interceptors

```java
import io.micronaut.http.annotation.ControllerAdvice;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;

@ControllerAdvice
public class GlobalResponseInterceptor {

    @AroundInvoke
    public Object intercept(HttpRequest<?> request, ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (result instanceof HttpResponse) {
                HttpResponse<?> response = (HttpResponse<?>) result;
                return response.header("X-Response-Time", duration + "ms");
            }

            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            // Log error with duration
            throw e;
        }
    }
}
```

### CORS Configuration

```yaml
micronaut:
  server:
    cors:
      enabled: true
      configurations:
        web:
          allowedOrigins:
            - http://localhost:3000
            - https://example.com
          allowedMethods:
            - GET
            - POST
            - PUT
            - DELETE
            - OPTIONS
          allowedHeaders:
            - "*"
          exposedHeaders:
            - X-Custom-Header
          allowCredentials: true
          maxAge: 3600
```

### Rate Limiting

```java
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

@Filter("/api/")
public class RateLimitingFilter implements HttpServerFilter {

    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(
            HttpRequest<?> request,
            ServerFilterChain chain) {

        String clientId = getClientId(request);
        RateLimiter limiter = rateLimiters.computeIfAbsent(
            clientId,
            k -> RateLimiter.create(10.0) // 10 requests per second
        );

        if (!limiter.tryAcquire()) {
            return Publishers.just(
                HttpResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit exceeded"))
            );
        }

        return chain.proceed(request);
    }

    private String getClientId(HttpRequest<?> request) {
        return request.getRemoteAddress()
            .map(addr -> addr.getAddress().getHostAddress())
            .orElse("unknown");
    }
}
```

### WebSocket Support

```java
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

@ServerWebSocket("/ws/{topic}")
public class WebSocketServer {

    @OnOpen
    public void onOpen(String topic, WebSocketSession session) {
        System.out.println("Client connected to topic: " + topic);
        session.send("Welcome to topic: " + topic);
    }

    @OnMessage
    public void onMessage(String topic, String message, WebSocketSession session) {
        System.out.println("Received message: " + message);
        session.send("Echo: " + message);
    }

    @OnClose
    public void onClose(String topic, WebSocketSession session) {
        System.out.println("Client disconnected from topic: " + topic);
    }
}
```

## Server-Sent Events (SSE)

### SSE Endpoint

```java
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.sse.Event;
import reactor.core.publisher.Flux;

@Controller("/events")
public class SSEController {

    @Get(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM)
    public Flux<Event<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> Event.of("Event " + i))
            .take(10);
    }
}
```

## HTTP Client

### Declarative HTTP Client

```java
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;

@Client("https://api.example.com")
public interface ExternalApiClient {

    @Get("/users/{id}")
    User getUser(Long id);

    @Get("/users")
    List<User> getAllUsers();

    @Post("/users")
    @Header(name = "Authorization", value = "Bearer ${token}")
    User createUser(@Body User user);
}
```

### Reactive HTTP Client

```java
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Client("https://api.example.com")
public interface ReactiveApiClient {

    @Get("/users/{id}")
    Mono<User> getUser(Long id);

    @Get("/users")
    Flux<User> getAllUsers();
}
```

## HTTP Client Configuration

### Client Configuration

**application.yml:**

```yaml
micronaut:
  http:
    client:
      read-timeout: 30s
      connect-timeout: 10s
      max-content-length: 10485760
      pool:
        max-connections: 100
        max-pending-requests: 50
```

### Custom HTTP Client

```java
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.HttpClientConfiguration;
import jakarta.inject.Singleton;

@Singleton
public class CustomHttpClient {
    private final HttpClient httpClient;

    public CustomHttpClient(HttpClientConfiguration configuration) {
        this.httpClient = HttpClient.create(
            URI.create("https://api.example.com"),
            configuration
        );
    }
}
```

## Content Negotiation

### Content Negotiation

```java
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Consumes;

@Controller("/api/users")
public class ContentNegotiationController {

    @Get("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User getUser(Long id) {
        return userService.findById(id);
    }

    @Get("/{id}/xml")
    @Produces(MediaType.APPLICATION_XML)
    public User getUserXml(Long id) {
        return userService.findById(id);
    }
}
```

## HTTP/2 Support

### HTTP/2 Configuration

**application.yml:**

```yaml
micronaut:
  server:
    http-version: HTTP_2_0
    ssl:
      enabled: true
      port: 8443
```

## Request/Response Interceptors

### Custom Interceptors

```java
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

@Filter("/api/")
public class CustomHttpFilter implements HttpServerFilter {

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(
            HttpRequest<?> request,
            ServerFilterChain chain) {
        // Логирование запроса
        log.info("Request: {} {}", request.getMethod(), request.getPath());

        return chain.proceed(request);
    }
}
```

## HTTP Compression

### Compression Configuration

**application.yml:**

```yaml
micronaut:
  server:
    compression:
      enabled: true
      min-threshold: 1024
```

## HTTP Caching

### Cache Headers

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.cache.CacheControl;

@Controller("/api/users")
public class UserController {

    @Get("/{id}")
    public HttpResponse<User> getUser(Long id) {
        User user = userService.findById(id);
        return HttpResponse.ok(user)
            .header("Cache-Control", "max-age=3600")
            .header("ETag", generateETag(user));
    }
}
```

## Заключение

**Micronaut HTTP** предоставляет мощный и гибкий **API** для создания **RESTful** приложений с поддержкой **type-safe routing**, валидации, фильтров, обработки ошибок, **WebSocket**, **SSE**, **HTTP** клиентов, конфигурации клиентов, **content negotiation**, **HTTP**/2, **interceptors**, **compression**, **caching** и других продвинутых возможностей. Использование **compile-time** обработки обеспечивает высокую производительность и минимальное потребление памяти.

## Дополнительные ресурсы

- [**Micronaut HTTP Server** Documentation](https://micronaut-projects.github.io/micronaut-servlet/latest/guide/)
- [**Micronaut Routing** Documentation](https://docs.micronaut.io/latest/guide/index.html#routing)
- [**Micronaut Filters** Documentation](https://docs.micronaut.io/latest/guide/index.html#filters)
- [**Micronaut HTTP** Client](https://docs.micronaut.io/latest/guide/index.html#httpClient)
- [**Jakarta Servlet** Specification](https://jakarta.ee/specifications/servlet/)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
