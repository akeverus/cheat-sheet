---
title: "Micronaut: OpenAPI - API Documentation и Swagger"
description: "Полное руководство по OpenAPI в Micronaut: API documentation, Swagger UI, code generation и best practices"
tags: ["micronaut", "openapi", "swagger", "api-documentation", "rest", "java", "kotlin"]
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-http.md", "micronaut-testing.md"]
updated: "2025-01-16"
related: ["micronaut-http.md", "micronaut-testing.md"]
---

# Micronaut: OpenAPI - API Documentation и Swagger

## Введение

Micronaut предоставляет отличную поддержку OpenAPI для автоматической генерации API документации. Это позволяет создавать интерактивную документацию с использованием Swagger UI.

### Основные возможности

- **OpenAPI Generation**: Автоматическая генерация OpenAPI спецификации
- **Swagger UI**: Интерактивный UI для API документации
- **Code Generation**: Генерация клиентского кода
- **API Documentation**: Автоматическая документация API

## Настройка OpenAPI

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi-annotations")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  router:
    static-resources:
      swagger:
        paths: classpath:META-INF/swagger
        mapping: /swagger/**
  openapi:
    enabled: true
    api-doc-path: /swagger/api-doc.yml
```

## OpenAPI Annotations

### Controller Documentation

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/users")
@Tag(name = "Users", description = "User management API")
public class UserController {
    
    @Get("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Returns a user by their unique identifier"
    )
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public User getUser(Long id) {
        return userService.findById(id);
    }
}
```

## Schema Definitions

### Model Documentation

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User entity")
public class User {
    
    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "User name", example = "John Doe", required = true)
    private String name;
    
    @Schema(description = "User email", example = "john@example.com", required = true)
    private String email;
    
    // Getters and setters
}
```

## Swagger UI

### Swagger UI Configuration

**application.yml:**

```yaml
swagger:
  ui:
    enabled: true
    path: /swagger-ui
    try-it-out-enabled: true
```

### Accessing Swagger UI

```
http://localhost:8080/swagger-ui
```

## Code Generation

### Client Generation

```bash
# Генерация клиентского кода из OpenAPI спецификации
openapi-generator generate -i api-doc.yml -g java -o client
```

## Best Practices

### 1. Документируйте все endpoints

```java
// ✅ Хорошо
@Operation(summary = "Get user", description = "Returns user by ID")
@ApiResponse(responseCode = "200", description = "User found")
public User getUser(Long id) {
    // ...
}
```

### 2. Используйте примеры в схемах

```java
// ✅ Хорошо
@Schema(example = "john@example.com")
private String email;
```

### 3. Группируйте endpoints по тегам

```java
// ✅ Хорошо
@Tag(name = "Users")
public class UserController {
    // ...
}
```

## Security Schemes

### API Security Documentation

```java
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@Controller("/api/users")
public class SecureUserController {
    
    @Get("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public User getUser(Long id) {
        return userService.findById(id);
    }
}
```

## Request/Response Examples

### Example Documentation

```java
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Post
@Operation(summary = "Create user")
@ApiResponse(
    responseCode = "201",
    description = "User created",
    content = @Content(
        mediaType = "application/json",
        examples = @ExampleObject(
            name = "User example",
            value = "{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john@example.com\"}"
        )
    )
)
public User createUser(@Body User user) {
    return userService.create(user);
}
```

## OpenAPI Servers

### Server Configuration

```java
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    servers = {
        @Server(url = "https://api.example.com", description = "Production"),
        @Server(url = "https://staging-api.example.com", description = "Staging"),
        @Server(url = "http://localhost:8080", description = "Local")
    }
)
public class Application {
}
```

## OpenAPI Tags

### Tag Organization

```java
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller("/api/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {
    // ...
}

@Controller("/api/orders")
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {
    // ...
}
```

## OpenAPI Components

### Reusable Components

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response")
public class ErrorResponse {
    
    @Schema(description = "Error code", example = "404")
    private String code;
    
    @Schema(description = "Error message", example = "Not found")
    private String message;
    
    // Getters and setters
}
```

## OpenAPI Extensions

### Custom Extensions

```java
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;

@Operation(
    summary = "Get user",
    extensions = {
        @Extension(
            name = "x-rate-limit",
            properties = {
                @ExtensionProperty(name = "limit", value = "100"),
                @ExtensionProperty(name = "period", value = "1h")
            }
        )
    }
)
public User getUser(Long id) {
    return userService.findById(id);
}
```

## OpenAPI Configuration

### Advanced Configuration

**application.yml:**

```yaml
micronaut:
  openapi:
    enabled: true
    api-doc-path: /swagger/api-doc.yml
    swagger-ui:
      enabled: true
      path: /swagger-ui
      try-it-out-enabled: true
      deep-linking: true
      display-request-duration: true
```

## Заключение

Micronaut OpenAPI предоставляет мощные инструменты для API документации. Поддержка автоматической генерации OpenAPI спецификации, Swagger UI, code generation, annotations, security schemes, examples, servers, tags, components, extensions, advanced configuration и других продвинутых возможностей позволяет создавать профессиональную API документацию.

## Дополнительные ресурсы

- [Micronaut OpenAPI Documentation](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Generator](https://openapi-generator.tech/)
- [OpenAPI Extensions](https://swagger.io/specification/#specification-extensions)

