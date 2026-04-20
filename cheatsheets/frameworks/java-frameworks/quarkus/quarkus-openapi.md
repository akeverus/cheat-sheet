---
title: "Quarkus: OpenAPI - API Documentation"
description: "Полное руководство по OpenAPI в Quarkus: API documentation, Swagger UI, code generation, security schemes и best practices"
tags:
  - quarkus
  - openapi
  - swagger
  - api-documentation
  - rest
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-rest.md"]
next: ["quarkus-rest.md", "quarkus-security.md"]
updated: "2026-02-11"
related: ["quarkus-rest.md", "quarkus-security.md"]
---

# Quarkus: OpenAPI - API Documentation

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Configuration](#configuration)
  - [Basic Configuration](#basic-configuration)
- [OpenAPI Annotations](#openapi-annotations)
  - [Basic Annotations](#basic-annotations)
  - [Request Body Documentation](#request-body-documentation)
  - [Parameter Documentation](#parameter-documentation)
- [Security Schemes](#security-schemes)
  - [API Key Security](#api-key-security)
  - [OAuth2 Security](#oauth2-security)
- [Code Generation](#code-generation)
  - [Client Generation](#client-generation)
- [Генерация клиента из OpenAPI спецификации](#генерация-клиента-из-openapi-спецификации)
- [Лучшие практики](#лучшие-практики)
  - [1. Документируйте все endpoints](#1-документируйте-все-endpoints)
  - [2. Используйте аннотации для параметров](#2-используйте-аннотации-для-параметров)
  - [3. Определяйте security schemes](#3-определяйте-security-schemes)
- [Advanced OpenAPI Features](#advanced-openapi-features)
  - [Custom Schema Definitions](#custom-schema-definitions)
  - [Response Examples](#response-examples)
  - [OpenAPI Filters](#openapi-filters)
  - [Tag Organization](#tag-organization)
- [OpenAPI Code Generation](#openapi-code-generation)
  - [Server Code Generation](#server-code-generation)
  - [Client Code Generation](#client-code-generation)
  - [TypeScript Client Generation](#typescript-client-generation)
- [Swagger UI Customization](#swagger-ui-customization)
  - [Custom Theme](#custom-theme)
  - [Custom Configuration](#custom-configuration)
  - [4. Используйте примеры](#4-используйте-примеры)
  - [5. Организуйте endpoints по тегам](#5-организуйте-endpoints-по-тегам)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет полную поддержку **OpenAPI** через **SmallRye OpenAPI**. Это позволяет автоматически генерировать документацию **API**, предоставлять **Swagger** `UI` и создавать клиентские библиотеки.

### Основные возможности

- **OpenAPI Generation**: Автоматическая генерация **OpenAPI** спецификации
- **Swagger UI**: Интерактивная документация **API**
- **Code Generation**: Генерация клиентского кода
- **Security Schemes**: Определение схем безопасности
- **Annotations**: Аннотации для документирования **API**

## Configuration

### Basic Configuration

**application.properties:**

```properties
quarkus.smallrye-openapi.info-title=My API
quarkus.smallrye-openapi.info-version=1.0.0
quarkus.smallrye-openapi.info-description=My API Description
quarkus.smallrye-openapi.path=/openapi
quarkus.swagger-ui.enable=true
quarkus.swagger-ui.path=/swagger-ui
```

## OpenAPI Annotations

### Basic Annotations

**Базовые аннотации:**

```java
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/users")
public class UserResource {

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Returns a user by ID"
    )
    @APIResponse(
        responseCode = "200",
        description = "User found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = User.class)
        )
    )
    @APIResponse(
        responseCode = "404",
        description = "User not found"
    )
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
}
```

### Request Body Documentation

**Документирование **request body**:**

```java
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import jakarta.ws.rs.POST;

@POST
@Operation(summary = "Create user")
@RequestBody(
    description = "User to create",
    required = true,
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = User.class)
    )
)
public Response createUser(@Valid User user) {
    User created = userService.create(user);
    return Response.status(201).entity(created).build();
}
```

### Parameter Documentation

**Документирование параметров:**

```java
import org.eclipse.microprofile.openapi.annotations.Parameter;
import jakarta.ws.rs.QueryParam;

@GET
@Operation(summary = "List users")
public List<User> getUsers(
        @Parameter(description = "Page number", required = false)
        @QueryParam("page") @DefaultValue("0") Integer page,
        @Parameter(description = "Page size", required = false)
        @QueryParam("size") @DefaultValue("20") Integer size) {
    return userService.findAll(page, size);
}
```

## Security Schemes

### API Key Security

**Определение **API Key security**:**

```java
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

@SecurityScheme(
    securitySchemeName = "apiKey",
    type = SecuritySchemeType.APIKEY,
    apiKeyName = "X-API-Key",
    in = SecuritySchemeIn.HEADER
)
@Path("/api")
public class SecureResource {

    @GET
    @SecurityRequirement(name = "apiKey")
    public String getSecureData() {
        return "Secure data";
    }
}
```

### OAuth2 Security

**Определение **OAuth2 security**:**

```java
@SecurityScheme(
    securitySchemeName = "oauth2",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://example.com/oauth/authorize",
            tokenUrl = "https://example.com/oauth/token",
            scopes = @OAuthScope(name = "read", description = "Read access")
        )
    )
)
```

## Code Generation

### Client Generation

**Генерация клиентского кода:**

```bash
# Генерация клиента из OpenAPI спецификации
openapi-generator generate -i openapi.yaml -g java -o client
```

## Лучшие практики
### 1. Документируйте все endpoints

```java
// ✅ Хорошо
@Operation(summary = "Get user", description = "Returns user by ID")
@APIResponse(responseCode = "200", description = "User found")
public User getUser(@PathParam("id") Long id) {
    // ...
}
```

### 2. Используйте аннотации для параметров

```java
// ✅ Хорошо
@Parameter(description = "User ID", required = true)
@PathParam("id") Long id
```

### 3. Определяйте security schemes

```java
// ✅ Хорошо
@SecurityScheme(securitySchemeName = "apiKey", type = SecuritySchemeType.APIKEY)
```

## Advanced OpenAPI Features

### Custom Schema Definitions

**Определение кастомных схем:**

```java
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User information", example = "{\"id\": 1, \"name\": \"John\"}")
public class User {
    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User name", example = "John Doe", required = true)
    private String name;

    @Schema(description = "User email", example = "john@example.com", format = "email")
    private String email;
}
```

### Response Examples

**Примеры ответов:**

```java
@GET
@Path("/{id}")
@Operation(summary = "Get user")
@APIResponse(
    responseCode = "200",
    description = "User found",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = User.class),
        examples = @ExampleObject(
            name = "User Example",
            value = "{\"id\": 1, \"name\": \"John\", \"email\": \"john@example.com\"}"
        )
    )
)
public User getUser(@PathParam("id") Long id) {
    return userService.findById(id);
}
```

### OpenAPI Filters

**Фильтры **OpenAPI**:**

```java
@ApplicationScoped
public class OpenAPIFilter implements OASFilter {

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        // Кастомизация OpenAPI документации
        openAPI.getInfo().setTitle("My Custom API");
        openAPI.getInfo().setVersion("2.0.0");
    }
}
```

### Tag Organization

**Организация тегов:**

```java
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Users", description = "User management operations")
@Path("/users")
public class UserResource {
    // ...
}

@Tag(name = "Orders", description = "Order management operations")
@Path("/orders")
public class OrderResource {
    // ...
}
```

## OpenAPI Code Generation

### Server Code Generation

**Генерация серверного кода:**

```bash
openapi-generator generate \
    -i openapi.yaml \
    -g jaxrs-spec \
    -o server-code
```

### Client Code Generation

**Генерация клиентского кода:**

```bash
openapi-generator generate \
    -i openapi.yaml \
    -g java \
    -o client-code \
    --library jersey2
```

### TypeScript Client Generation

**Генерация **TypeScript** клиента:**

```bash
openapi-generator generate \
    -i openapi.yaml \
    -g typescript-axios \
    -o client-ts
```

## Swagger `UI` Customization

### Custom Theme

**Кастомная тема:**

```properties
quarkus.swagger-ui.theme=dark
quarkus.swagger-ui.theme.css-path=/custom-theme.css
```

### Custom Configuration

**Кастомная конфигурация:**

```properties
quarkus.swagger-ui.config-url=/swagger-config.json
quarkus.swagger-ui.oauth-client-id=my-client-id
quarkus.swagger-ui.oauth-client-secret=my-client-secret
```


## Заключение

**Quarkus OpenAPI** предоставляет мощные инструменты для документирования **API**. Поддержка автоматической генерации **OpenAPI** спецификации, **Swagger** `UI`, **code generation**, **security schemes** и других возможностей позволяет создавать качественную документацию **API**.

## Дополнительные ресурсы

- [**Quarkus OpenAPI** Guide](https://quarkus.io/guides/openapi-swaggerui)
- [**OpenAPI** Specification](https://spec.openapis.org/oas/latest.html)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
