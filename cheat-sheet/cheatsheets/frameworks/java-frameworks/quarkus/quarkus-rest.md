---
title: "Quarkus: REST - RESTEasy Reactive и JAX-RS"
description: "Полное руководство по REST в Quarkus: RESTEasy Reactive, JAX-RS, controllers, filters, exception handling и best practices"
tags: ["quarkus", "rest", "resteasy", "jax-rs", "controllers", "java"]
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-reactive.md"]
updated: "2025-01-16"
related: ["quarkus-core.md", "quarkus-reactive.md"]
---

# Quarkus: REST - RESTEasy Reactive и JAX-RS

## Введение

Quarkus предоставляет два варианта для создания REST API: RESTEasy Reactive (рекомендуется) и RESTEasy Classic. RESTEasy Reactive построен на Vert.x и обеспечивает лучшую производительность.

### Основные возможности

- **RESTEasy Reactive**: Асинхронный REST framework
- **JAX-RS**: Стандарт JAX-RS 3.0
- **Filters и Interceptors**: Обработка запросов и ответов
- **Exception Handling**: Централизованная обработка ошибок
- **Validation**: Bean Validation интеграция

## RESTEasy Reactive

### Basic Resource

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello Quarkus";
    }
}
```

### Reactive Resource

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import io.smallrye.mutiny.Uni;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/{id}")
    public Uni<User> getUser(Long id) {
        return Uni.createFrom().item(() -> userService.findById(id));
    }
}
```

## JAX-RS Annotations

### HTTP Methods

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
public class UserResource {
    
    @GET
    public List<User> getAllUsers() {
        return userService.findAll();
    }
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
    
    @POST
    public Response createUser(User user) {
        User created = userService.create(user);
        return Response.status(201).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public User updateUser(@PathParam("id") Long id, User user) {
        return userService.update(id, user);
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.delete(id);
        return Response.noContent().build();
    }
}
```

## Filters и Interceptors

### Request Filter

```java
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoggingFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        System.out.println("Request: " + requestContext.getMethod() + 
                          " " + requestContext.getUriInfo().getPath());
    }
}
```

### Response Filter

```java
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    }
}
```

## Exception Handling

### Exception Mapper

```java
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserNotFoundExceptionMapper 
        implements ExceptionMapper<UserNotFoundException> {
    
    @Override
    public Response toResponse(UserNotFoundException exception) {
        return Response.status(404)
            .entity(Map.of("error", exception.getMessage()))
            .build();
    }
}
```

## Best Practices

### 1. Используйте RESTEasy Reactive

```java
// ✅ Хорошо
@Path("/users")
public class UserResource {
    @GET
    public Uni<List<User>> getUsers() {
        // Reactive API
    }
}
```

### 2. Обрабатывайте ошибки централизованно

```java
// ✅ Хорошо
@Provider
public class ExceptionMapper implements ExceptionMapper<Exception> {
    // Централизованная обработка
}
```

### 3. Используйте validation

```java
// ✅ Хорошо
@POST
public Response createUser(@Valid User user) {
    // Валидация автоматически
}
```

## Path Parameters

### Path Variables

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
    
    @GET
    @Path("/{userId}/orders/{orderId}")
    public Order getUserOrder(
            @PathParam("userId") Long userId,
            @PathParam("orderId") Long orderId) {
        return orderService.findUserOrder(userId, orderId);
    }
}
```

### Path Parameter Validation

```java
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") @Min(1) Long id) {
        return userService.findById(id);
    }
}
```

## Query Parameters

### Query Parameters

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.DefaultValue;

@Path("/users")
public class UserResource {
    
    @GET
    public List<User> getUsers(
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("20") Integer size,
            @QueryParam("sort") @DefaultValue("name") String sort) {
        return userService.findAll(page, size, sort);
    }
}
```

### Multiple Query Parameters

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/search")
    public List<User> searchUsers(
            @QueryParam("name") String name,
            @QueryParam("email") String email,
            @QueryParam("roles") List<String> roles) {
        return userService.search(name, email, roles);
    }
}
```

## Request/Response Entities

### JSON Serialization

```java
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(User user) {
        return userService.create(user);
    }
}
```

### XML Support

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
}
```

## Async Processing

### Async Response

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import java.util.concurrent.CompletableFuture;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/{id}")
    public void getUserAsync(
            @PathParam("id") Long id,
            @Suspended AsyncResponse asyncResponse) {
        CompletableFuture.supplyAsync(() -> userService.findById(id))
            .thenApply(asyncResponse::resume)
            .exceptionally(asyncResponse::resume);
    }
}
```

## Content Negotiation

### Accept Header

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {
    
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUser(@PathParam("id") Long id) {
        User user = userService.findById(id);
        return Response.ok(user).build();
    }
}
```

## Server-Sent Events (SSE)

### SSE Endpoint

Создание SSE endpoint для streaming данных:

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import jakarta.ws.rs.sse.OutboundSseEvent;

@Path("/events")
public class SSEResource {
    
    @Inject
    Sse sse;
    
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void streamEvents(@Context SseEventSink eventSink) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            for (int i = 0; i < 10; i++) {
                OutboundSseEvent event = sse.newEventBuilder()
                    .name("message")
                    .data(String.class, "Event " + i)
                    .build();
                eventSink.send(event);
                Thread.sleep(1000);
            }
            eventSink.close();
        });
    }
}
```

## Multipart Support

### File Upload

Загрузка файлов:

```java
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/upload")
public class FileUploadResource {
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @MultipartForm FileUploadForm form) {
        FileUpload file = form.file;
        // Обработка файла
        return Response.ok().build();
    }
    
    public static class FileUploadForm {
        @FormParam("file")
        public FileUpload file;
        
        @FormParam("description")
        public String description;
    }
}
```

### File Download

Скачивание файлов:

```java
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Path("/download")
public class FileDownloadResource {
    
    @GET
    @Path("/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("filename") String filename) {
        File file = new File("/path/to/" + filename);
        
        StreamingOutput stream = output -> {
            try (FileInputStream input = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        };
        
        return Response.ok(stream)
            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
            .build();
    }
}
```

## CORS Configuration

### CORS Filter

Настройка CORS:

```java
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", 
            "Content-Type, Authorization");
    }
}
```

### CORS через конфигурацию

```properties
# application.properties
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:3000,https://example.com
quarkus.http.cors.headers=accept,authorization,content-type
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
```

## Request/Response Interceptors

### Method Interceptor

Перехват вызовов методов:

```java
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TimingInterceptor implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        long startTime = System.currentTimeMillis();
        requestContext.setProperty("startTime", startTime);
    }
}

@Provider
public class TimingResponseFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) {
        Long startTime = (Long) requestContext.getProperty("startTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            responseContext.getHeaders().add("X-Response-Time", duration + "ms");
        }
    }
}
```

## Validation

### Bean Validation

Использование Bean Validation:

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/users")
public class ValidatedResource {
    
    @POST
    public Response createUser(@Valid User user) {
        // Валидация выполняется автоматически
        return Response.ok(userService.create(user)).build();
    }
    
    public static class User {
        @NotNull
        @Size(min = 3, max = 50)
        public String name;
        
        @NotNull
        @Email
        public String email;
    }
}
```

### Custom Validators

Создание кастомных валидаторов:

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PhoneNumber.Validator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
    
    class Validator implements ConstraintValidator<PhoneNumber, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.matches("^\\+?[1-9]\\d{1,14}$");
        }
    }
}
```

## OpenAPI Integration

### OpenAPI Configuration

Настройка OpenAPI:

```properties
# application.properties
quarkus.smallrye-openapi.info-title=My API
quarkus.smallrye-openapi.info-version=1.0.0
quarkus.smallrye-openapi.info-description=My API Description
```

### OpenAPI Annotations

Использование OpenAPI аннотаций:

```java
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/users")
public class OpenAPIResource {
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a user by ID")
    @APIResponse(
        responseCode = "200",
        description = "User found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = User.class)
        )
    )
    @APIResponse(responseCode = "404", description = "User not found")
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
}
```

## Best Practices

### 1. Используйте RESTEasy Reactive

```java
// ✅ Хорошо
@Path("/users")
public class UserResource {
    @GET
    public Uni<List<User>> getUsers() {
        // Reactive API
    }
}
```

### 2. Обрабатывайте ошибки централизованно

```java
// ✅ Хорошо
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        // Централизованная обработка
        return Response.status(500)
            .entity(Map.of("error", exception.getMessage()))
            .build();
    }
}
```

### 3. Используйте validation

```java
// ✅ Хорошо
@POST
public Response createUser(@Valid User user) {
    // Валидация автоматически
}
```

### 4. Используйте правильные HTTP методы

```java
// ✅ Хорошо
@GET    // Для получения данных
@POST   // Для создания
@PUT    // Для обновления
@DELETE // Для удаления
```

### 5. Используйте правильные статус коды

```java
// ✅ Хорошо
return Response.status(201).entity(created).build();  // Created
return Response.status(204).build();                  // No Content
return Response.status(404).build();                  // Not Found
```

## REST Client

### Declarative REST Client

Декларативный REST клиент:

```java
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@RegisterRestClient
@Path("/api")
public interface UserServiceClient {
    
    @GET
    @Path("/users/{id}")
    User getUser(@PathParam("id") Long id);
    
    @GET
    @Path("/users")
    List<User> getAllUsers();
}
```

Использование:

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/proxy")
public class ProxyResource {
    
    @Inject
    @RestClient
    UserServiceClient userService;
    
    @GET
    @Path("/users/{id}")
    public User proxyGetUser(@PathParam("id") Long id) {
        return userService.getUser(id);
    }
}
```

### Reactive REST Client

Реактивный REST клиент:

```java
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api")
public interface ReactiveUserServiceClient {
    
    @GET
    @Path("/users/{id}")
    Uni<User> getUser(@PathParam("id") Long id);
}
```

## Response Caching

### HTTP Caching

HTTP кеширование:

```java
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;

@Path("/cached")
public class CachedResource {
    
    @GET
    @Path("/data")
    public Response getCachedData() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);  // 1 hour
        cacheControl.setPrivate(false);
        
        return Response.ok(data)
            .cacheControl(cacheControl)
            .build();
    }
}
```

## Best Practices

### 1. Используйте RESTEasy Reactive

```java
// ✅ Хорошо
@Path("/users")
public class UserResource {
    @GET
    public Uni<List<User>> getUsers() {
        // Reactive API
    }
}
```

### 2. Обрабатывайте ошибки централизованно

```java
// ✅ Хорошо
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        // Централизованная обработка
        return Response.status(500)
            .entity(Map.of("error", exception.getMessage()))
            .build();
    }
}
```

### 3. Используйте validation

```java
// ✅ Хорошо
@POST
public Response createUser(@Valid User user) {
    // Валидация автоматически
}
```

### 4. Используйте правильные HTTP методы

```java
// ✅ Хорошо
@GET    // Для получения данных
@POST   // Для создания
@PUT    // Для обновления
@DELETE // Для удаления
```

### 5. Используйте правильные статус коды

```java
// ✅ Хорошо
return Response.status(201).entity(created).build();  // Created
return Response.status(204).build();                  // No Content
return Response.status(404).build();                  // Not Found
```

### 6. Используйте REST Client для внешних API

```java
// ✅ Хорошо
@Inject
@RestClient
ExternalServiceClient client;
```

## Advanced REST Patterns

### Rate Limiting

Ограничение частоты запросов:

```java
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RateLimited {
    int value() default 100;
    int window() default 60;
}

@Provider
@RateLimited
public class RateLimitFilter implements ContainerRequestFilter {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Override
    public void filter(ContainerRequestContext context) {
        String key = getClientKey(context);
        RateLimiter limiter = limiters.computeIfAbsent(key, 
            k -> RateLimiter.create(100.0 / 60.0));
        
        if (!limiter.tryAcquire()) {
            throw new TooManyRequestsException();
        }
    }
}
```

### Request Caching

Кеширование запросов:

```java
@Path("/cache")
public class CacheableResource {
    
    @GET
    @Path("/{id}")
    @CacheControl(maxAge = 3600)
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
}
```

### Response Compression

Сжатие ответов:

```properties
quarkus.http.enable-compression=true
quarkus.http.compression.level=6
```

## REST Performance Optimization

### Connection Pooling

Оптимизация пула соединений:

```properties
quarkus.rest-client.connection-pool-size=50
quarkus.rest-client.keep-alive-time=30s
```

### Async Processing Optimization

Оптимизация асинхронной обработки:

```java
@Path("/users")
public class OptimizedResource {
    
    @GET
    @Path("/{id}")
    public Uni<User> getUser(@PathParam("id") Long id) {
        return Uni.createFrom().item(() -> userService.findById(id))
            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
```

## REST API Versioning

### URL Versioning

Версионирование через URL:

```java
@Path("/v1/users")
public class UserResourceV1 {
    // Version 1 implementation
}

@Path("/v2/users")
public class UserResourceV2 {
    // Version 2 implementation
}
```

### Header Versioning

Версионирование через заголовки:

```java
@Path("/users")
public class VersionedUserResource {
    
    @GET
    @HeaderParam("API-Version")
    public Response getUsers(@HeaderParam("API-Version") String version) {
        if ("v2".equals(version)) {
            return Response.ok(getUsersV2()).build();
        }
        return Response.ok(getUsersV1()).build();
    }
}
```

## Advanced Response Handling

### Conditional Requests

Условные запросы:

```java
@GET
@Path("/{id}")
public Response getUser(@PathParam("id") Long id,
                       @HeaderParam("If-None-Match") String ifNoneMatch) {
    User user = userService.findById(id);
    String etag = generateETag(user);
    
    if (etag.equals(ifNoneMatch)) {
        return Response.notModified().build();
    }
    
    return Response.ok(user)
        .header("ETag", etag)
        .build();
}
```

### Partial Content

Частичный контент:

```java
@GET
@Path("/{id}")
public Response getUserPartial(@PathParam("id") Long id,
                              @HeaderParam("Range") String range) {
    User user = userService.findById(id);
    // Обработка Range заголовка
    return Response.status(206)
        .entity(getPartialContent(user, range))
        .header("Content-Range", getContentRange(range))
        .build();
}
```

## REST Client Advanced Features

### Client Filters

Фильтры клиента:

```java
@Provider
public class ClientLoggingFilter implements ClientRequestFilter, ClientResponseFilter {
    
    @Override
    public void filter(ClientRequestContext requestContext) {
        log.info("Request: {} {}", requestContext.getMethod(), requestContext.getUri());
    }
    
    @Override
    public void filter(ClientRequestContext requestContext, 
                      ClientResponseContext responseContext) {
        log.info("Response: {}", responseContext.getStatus());
    }
}
```

### Async REST Client

Асинхронный REST клиент:

```java
@RegisterRestClient
public interface AsyncUserService {
    
    @GET
    @Path("/users/{id}")
    CompletionStage<User> getUserAsync(@PathParam("id") Long id);
}
```

## Заключение

Quarkus REST предоставляет мощные инструменты для создания REST API. Поддержка RESTEasy Reactive, JAX-RS, filters, interceptors, exception handling, validation, path/query parameters, async processing, content negotiation, SSE, multipart, CORS, OpenAPI, REST Client, response caching и других продвинутых возможностей позволяет создавать эффективные RESTful приложения. Правильное использование REST паттернов, обработка ошибок, валидация, документация и интеграция с внешними сервисами являются ключевыми аспектами создания качественных REST API.

## Дополнительные ресурсы

- [Quarkus RESTEasy Reactive Guide](https://quarkus.io/guides/resteasy-reactive)
- [JAX-RS Specification](https://jakarta.ee/specifications/restful-ws/)
- [RESTEasy Documentation](https://resteasy.dev/)
- [REST API Best Practices](https://restfulapi.net/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [MicroProfile REST Client](https://microprofile.io/project/eclipse/microprofile-rest-client)

