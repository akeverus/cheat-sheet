---
title: "Spring Boot + OpenAPI (SpringDoc)"
description: "SpringDoc OpenAPI 2.x для Spring Boot 3.x: зависимости, конфигурация, аннотации, безопасность, WebFlux, кастомизация."
tags:
  - frameworks
  - spring
  - openapi
  - swagger
  - api-documentation
  - springdoc
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring Boot + OpenAPI (SpringDoc)

SpringDoc автоматически генерирует OpenAPI 3.x спецификацию из Spring Boot контроллеров.

## Полезные ссылки

### Официальная документация
- [Springdoc OpenAPI](https://springdoc.org/) — официальный сайт библиотеки springdoc-openapi

### См. также
- [Spring Boot](../../spring/spring-boot.md) — базовый фреймворк
- [Spring REST](spring-rest.md) — Spring MVC REST-контроллеры
- [Spring Security](spring-security.md) — защита API, интеграция с Swagger UI
- [Spring WebFlux](spring-webflux.md) — реактивный стек, отдельная зависимость springdoc

## Содержание

- [Зависимость](#зависимость)
- [Конфигурация (application.yml)](#конфигурация-applicationyml)
- [Глобальная конфигурация OpenAPI](#глобальная-конфигурация-openapi)
- [Аннотации контроллера](#аннотации-контроллера)
- [Аннотации моделей](#аннотации-моделей)
- [Безопасность (JWT Bearer)](#безопасность-jwt-bearer)
- [Группировка API](#группировка-api)
- [Spring Security интеграция](#spring-security-интеграция)
- [Отключение в production](#отключение-в-production)
- [Полезные приёмы](#полезные-приёмы)

## Зависимость

```xml
<!-- Spring Boot 3.x / Spring Framework 6.x -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.5.0</version>
</dependency>

<!-- Для WebFlux -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
  <version>2.5.0</version>
</dependency>
```

После добавления:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- JSON-спецификация: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

## Конфигурация (application.yml)

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method      # сортировка по HTTP-методу
    tags-sorter: alpha             # алфавитная сортировка тегов
    try-it-out-enabled: true
  packages-to-scan: com.example.api
  paths-to-match: /api/**
  # Отключить в production
  # enabled: false
```

## Глобальная конфигурация OpenAPI

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI applicationOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("My API")
                .version("v1.0")
                .description("REST API для управления заказами")
                .contact(new Contact().name("Team").email("dev@example.com"))
                .license(new License().name("Apache 2.0")))
            .addServersItem(new Server().url("https://api.example.com").description("Production"))
            .addServersItem(new Server().url("http://localhost:8080").description("Local"));
    }
}
```

## Аннотации контроллера

```java
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Управление заказами")
public class OrderController {

    @Operation(
        summary = "Создать заказ",
        description = "Создаёт новый заказ и возвращает его с присвоенным ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Заказ создан",
            content = @Content(schema = @Schema(implementation = OrderDto.class))),
        @ApiResponse(responseCode = "400", description = "Невалидные данные",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    @PostMapping
    public ResponseEntity<OrderDto> create(
            @RequestBody @Valid OrderCreateRequest request) {
        ...
    }

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/{id}")
    public OrderDto getById(
            @Parameter(description = "ID заказа", example = "42", required = true)
            @PathVariable Long id) {
        ...
    }

    @Operation(summary = "Список заказов", parameters = {
        @Parameter(name = "status", description = "Фильтр по статусу", example = "PENDING"),
        @Parameter(name = "page", description = "Номер страницы", example = "0")
    })
    @GetMapping
    public Page<OrderDto> list(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        ...
    }
}
```

## Аннотации моделей

```java
@Schema(description = "Данные для создания заказа")
public record OrderCreateRequest(

    @Schema(description = "ID клиента", example = "123", requiredMode = REQUIRED)
    @NotNull Long customerId,

    @Schema(description = "Товарные позиции", minItems = 1)
    @NotEmpty List<OrderItem> items,

    @Schema(description = "Примечание к заказу", maxLength = 500)
    String note
) {}

@Schema(description = "Статус заказа")
public enum OrderStatus {
    @Schema(description = "Принят, ожидает обработки") PENDING,
    @Schema(description = "В обработке")               PROCESSING,
    @Schema(description = "Отгружен")                  SHIPPED,
    @Schema(description = "Доставлен")                 DELIVERED
}
```

## Безопасность (JWT Bearer)

```java
@Configuration
public class OpenApiSecurityConfig {

    @Bean
    public OpenAPI securedOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}

// Исключить конкретный endpoint из требования авторизации
@Operation(security = {})
@PostMapping("/login")
public TokenResponse login(@RequestBody LoginRequest request) { ... }
```

## Группировка API

```java
@Configuration
public class OpenApiGroupConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/public/**")
            .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .pathsToMatch("/api/admin/**")
            .addOpenApiCustomizer(openApi ->
                openApi.info(new Info().title("Admin API")))
            .build();
    }
}
```

Swagger UI для групп: `/swagger-ui.html?configUrl=/v3/api-docs/swagger-config`

## Spring Security интеграция

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated())
        .build();
}
```

## Отключение в production

```yaml
# application-prod.yml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

## Полезные приёмы

**Скрыть endpoint из документации:**
```java
@Operation(hidden = true)
@GetMapping("/internal/health")
public String internalHealth() { ... }
```

**Deprecated endpoint:**
```java
@Operation(summary = "Старый метод", deprecated = true)
@GetMapping("/v1/orders")
public List<OrderDto> listV1() { ... }
```

**Пример тела запроса:**
```java
@io.swagger.v3.oas.annotations.parameters.RequestBody(
    content = @Content(examples = @ExampleObject(
        name = "Пример заказа",
        value = """
            {"customerId": 123, "items": [{"productId": 1, "qty": 2}]}
        """
    ))
)
@PostMapping
public OrderDto create(@RequestBody OrderCreateRequest request) { ... }
```

