---
title: "Вопросы на собеседовании: OpenAPI / Swagger"
description: "Вопросы и ответы по OpenAPI Specification и Swagger для Java-разработчика: структура документа, springdoc-openapi, аннотации, contract-first vs code-first, генерация клиентов, OAuth2, линтинг."
tags:
  - interview
  - api
  - openapi-swagger-interview
aliases:
  - "OpenAPI interview"
  - "Swagger interview"
  - "OpenAPI собеседование"
  - "OpenAPI вопросы"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `OpenAPI` / `Swagger`

Практичные вопросы и ответы по `OpenAPI Specification` и экосистеме `Swagger` для Java-разработчика: структура документа, `springdoc-openapi`, аннотации, `contract-first` vs `code-first`, генерация клиентов, `OAuth2`, контрактное тестирование, обратная совместимость.

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и структура**
- [Q1. Что такое OpenAPI Specification (OAS)?](#q1-что-такое-openapi-specification-oas)
- [Q2. Версии OAS: 2.0 vs 3.0 vs 3.1 — ключевые отличия](#q2-версии-oas-20-vs-30-vs-31--ключевые-отличия)
- [Q3. (!) Swagger vs OpenAPI — в чём разница терминологии?](#q3--swagger-vs-openapi--в-чём-разница-терминологии)
- [Q4. Структура OpenAPI-документа: верхнеуровневые секции](#q4-структура-openapi-документа-верхнеуровневые-секции)
- [Q5. Секция paths: как описывается эндпоинт?](#q5-секция-paths-как-описывается-эндпоинт)
- [Q6. Секция components: для чего используется?](#q6-секция-components-для-чего-используется)

**Swagger UI и springdoc**
- [Q7. (!) Что такое Swagger UI и как он работает?](#q7--что-такое-swagger-ui-и-как-он-работает)
- [Q8. springdoc-openapi: зависимость и автоконфигурация](#q8-springdoc-openapi-зависимость-и-автоконфигурация)
- [Q9. (!) Как подключить springdoc-openapi к Spring Boot проекту?](#q9--как-подключить-springdoc-openapi-к-spring-boot-проекту)

**Аннотации springdoc**
- [Q10. Аннотация @Operation: назначение и параметры](#q10-аннотация-operation-назначение-и-параметры)
- [Q11. Аннотации @Parameter и @Schema](#q11-аннотации-parameter-и-schema)
- [Q12. (!) Аннотация @ApiResponse и @ApiResponses](#q12--аннотация-apiresponse-и-apiresponses)
- [Q13. Аннотации @Tag и @Tags для группировки](#q13-аннотации-tag-и-tags-для-группировки)
- [Q14. @SecurityScheme и @SecurityRequirement](#q14-securityscheme-и-securityrequirement)
- [Q15. (!) springdoc + Spring Security: как настроить доступ к документации?](#q15--springdoc--spring-security-как-настроить-доступ-к-документации)

**Contract-first vs code-first**
- [Q16. Contract-first подход: определение и преимущества](#q16-contract-first-подход-определение-и-преимущества)
- [Q17. (!) Code-first подход: определение и компромиссы](#q17--code-first-подход-определение-и-компромиссы)
- [Q18. Contract-first vs Code-first: когда что выбирать?](#q18-contract-first-vs-code-first-когда-что-выбирать)

**openapi-generator**
- [Q19. (!) openapi-generator: что это и как использовать?](#q19--openapi-generator-что-это-и-как-использовать)
- [Q20. Генерация серверных стабов через openapi-generator](#q20-генерация-серверных-стабов-через-openapi-generator)
- [Q35. (!) API-first workflow: практический пайплайн с openapi-generator](#q35--api-first-workflow-практический-пайплайн-с-openapi-generator)
- [Q36. Delegate pattern в openapi-generator: когда применять](#q36-delegate-pattern-в-openapi-generator-когда-применять)
- [Q37. Кастомизация Mustache-шаблонов openapi-generator](#q37-кастомизация-mustache-шаблонов-openapi-generator)

**Продвинутая конфигурация springdoc**
- [Q21. springdoc с Spring WebFlux / реактивным API](#q21-springdoc-с-spring-webflux--реактивным-api)
- [Q22. Кастомизация springdoc: OpenApiCustomizer](#q22-кастомизация-springdoc-openapicustomizer)
- [Q23. (!) GroupedOpenApi: разбивка документации на группы](#q23--groupedopenapi-разбивка-документации-на-группы)
- [Q24. Валидация входящих запросов по схеме: @Valid и @Validated](#q24-валидация-входящих-запросов-по-схеме-valid-и-validated)
- [Q40. @Hidden и скрытие операций из документации](#q40-hidden-и-скрытие-операций-из-документации)

**Проектирование спецификаций**
- [Q25. Spectral: линтинг OpenAPI-спецификаций](#q25-spectral-линтинг-openapi-спецификаций)
- [Q26. (!) Версионирование API: /v1/ vs заголовки](#q26--версионирование-api-v1-vs-заголовки)
- [Q27. discriminator и полиморфизм: oneOf / anyOf / allOf](#q27-discriminator-и-полиморфизм-oneof--anyof--allof)
- [Q28. $ref: переиспользование компонентов спецификации](#q28-ref-переиспользование-компонентов-спецификации)
- [Q34. Callbacks vs Webhooks в OpenAPI 3.1](#q34-callbacks-vs-webhooks-в-openapi-31)
- [Q39. Множественные примеры: examples vs example, переиспользуемые примеры](#q39-множественные-примеры-examples-vs-example-переиспользуемые-примеры)
- [Q41. (!) Пагинация в OpenAPI: query параметры, cursor, Link header](#q41--пагинация-в-openapi-query-параметры-cursor-link-header)
- [Q42. multipart/form-data и загрузка файлов в OpenAPI](#q42-multipartform-data-и-загрузка-файлов-в-openapi)

**CI/CD и контрактное тестирование**
- [Q29. (!) OpenAPI в CI/CD: generate-and-compare, contract testing](#q29--openapi-в-cicd-generate-and-compare-contract-testing)
- [Q30. Contract testing с Pact: как соотносится с OpenAPI?](#q30-contract-testing-с-pact-как-соотносится-с-openapi)
- [Q38. (!) Backward compatibility: openapi-diff и oasdiff в CI](#q38--backward-compatibility-openapi-diff-и-oasdiff-в-ci)

**Ошибки и безопасность**
- [Q31. (!) Документирование ошибок: ProblemDetail (RFC 7807)](#q31--документирование-ошибок-problemdetail-rfc-7807)
- [Q32. OAuth2 и Bearer token в Swagger UI](#q32-oauth2-и-bearer-token-в-swagger-ui)

**Практика**
- [Q33. Лучшие практики написания OpenAPI-спецификаций](#q33-лучшие-практики-написания-openapi-спецификаций)

---

## Q1. Что такое OpenAPI Specification (OAS)?

**OpenAPI Specification (OAS)** — стандарт описания REST API в машиночитаемом формате (`YAML` или `JSON`). Позволяет формально описать эндпоинты, параметры запросов, тела запросов и ответов, схемы данных, механизмы аутентификации.

Ключевые свойства:
- **Независимость от языка** — описание API не зависит от реализации
- **Инструментальная экосистема** — генерация клиентов, серверных стабов, документации
- **Контракт** — служит соглашением между командами frontend и backend
- **Валидация** — можно автоматически проверять запросы и ответы на соответствие спецификации

Формат спецификации определяется консорциумом **OpenAPI Initiative (OAI)** под эгидой Linux Foundation.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Версии OAS: 2.0 vs 3.0 vs 3.1 — ключевые отличия

| Аспект | OAS 2.0 (Swagger) | OAS 3.0.x | OAS 3.1.x |
|--------|-------------------|-----------|-----------|
| Тело запроса | `body`/`formData` параметры | `requestBody` объект | `requestBody` объект |
| Компоненты | `definitions`, `parameters` | `components` | `components` |
| Примеры | ограничены | `examples` объект | `examples` объект |
| Webhooks | нет | нет | `webhooks` секция |
| JSON Schema | подмножество draft-04 | подмножество draft-07 | полный draft 2020-12 |
| Multiple servers | нет (`host`, `basePath`) | `servers` массив | `servers` массив |
| Nullable | `x-nullable` (расширение) | `nullable: true` | `type: ["string", "null"]` |
| Links | нет | `links` объект | `links` объект |

**OAS 3.1** — наиболее актуальная версия, полностью совместима с **JSON Schema draft 2020-12**. Используется `springdoc-openapi` начиная с версии 2.x (Spring Boot 3).

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Swagger vs OpenAPI — в чём разница терминологии?

Исторически **Swagger** — это проект компании SmartBear, включавший:
1. **Swagger Specification** — формат описания API (версии 1.x, 2.0)
2. **Swagger UI** — визуализация и тестирование API
3. **Swagger Codegen** — генератор клиентов

В 2016 году SmartBear передал спецификацию в **OpenAPI Initiative**, и с версии 3.0 она называется **OpenAPI Specification**. Инструменты сохранили название Swagger:

- **OpenAPI** = спецификация (стандарт)
- **Swagger** = инструменты (UI, Codegen, Editor) и историческое название

Сегодня говорить "Swagger-спецификация" для OAS 3.x — технически некорректно, но повсеместно употребляется в обиходе.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Структура OpenAPI-документа: верхнеуровневые секции

```yaml
openapi: "3.1.0"          # версия спецификации (обязательно)
info:                      # метаданные API (обязательно)
  title: "My API"
  version: "1.0.0"
  description: "..."
  contact:
    name: "Team"
    email: "team@example.com"
servers:                   # базовые URL серверов
  - url: "https://api.example.com/v1"
paths:                     # эндпоинты (обязательно)
  /users:
    get: ...
components:                # переиспользуемые объекты
  schemas: ...
  parameters: ...
  responses: ...
  securitySchemes: ...
tags:                      # группировка операций
  - name: "Users"
security:                  # глобальная безопасность
  - BearerAuth: []
webhooks:                  # OAS 3.1: входящие запросы
  newOrder: ...
```

Обязательные поля: `openapi`, `info`, `paths`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Секция paths: как описывается эндпоинт?

```yaml
paths:
  /users/{id}:
    get:
      summary: "Получить пользователя"
      operationId: "getUserById"      # уникальный ID операции
      tags:
        - "Users"
      parameters:
        - name: id
          in: path                    # path | query | header | cookie
          required: true
          schema:
            type: integer
      responses:
        "200":
          description: "Успешный ответ"
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/User"
        "404":
          $ref: "#/components/responses/NotFound"
```

`operationId` — важен для генерации клиентов: становится именем метода.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Секция components: для чего используется?

`components` содержит переиспользуемые объекты, на которые можно ссылаться через `$ref`:

```yaml
components:
  schemas:           # модели данных
    User:
      type: object
      properties:
        id: { type: integer }
        name: { type: string }
  parameters:        # общие параметры
    PageParam:
      name: page
      in: query
      schema: { type: integer, default: 0 }
  responses:         # стандартные ответы
    NotFound:
      description: "Ресурс не найден"
  requestBodies:     # тела запросов
  headers:           # заголовки
  securitySchemes:   # схемы аутентификации
  links:             # связи между операциями
  callbacks:         # колбэки (webhooks в OAS 3.0)
  examples:          # примеры данных
  pathItems:         # OAS 3.1: переиспользуемые path items
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) Что такое Swagger UI и как он работает?

**Swagger UI** — веб-приложение, которое визуализирует OpenAPI-спецификацию в интерактивном формате:
- Отображает все эндпоинты с описаниями, параметрами и схемами ответов
- Позволяет выполнять HTTP-запросы прямо из браузера (Try it out)
- Поддерживает аутентификацию (Bearer token, OAuth2, Basic Auth)

Как работает:
1. Swagger UI загружает JSON/YAML из `/v3/api-docs` (`springdoc-openapi`)
2. Парсит спецификацию и строит интерактивный UI
3. Запросы из UI идут напрямую к API (не через прокси)

По умолчанию в `springdoc-openapi`:
- Документация: `GET /v3/api-docs`
- UI: `GET /swagger-ui.html` → редиректит на `/swagger-ui/index.html`

Настройка пути:
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. springdoc-openapi: зависимость и автоконфигурация

**springdoc-openapi** — библиотека для автоматической генерации OpenAPI 3.x документации из Spring Boot приложений.

Зависимости (Spring Boot 3.x / OAS 3.1):
```xml
<!-- Maven -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

```gradle
// Gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
```

Для WebFlux:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

Автоконфигурация сканирует `@RestController`, `@RequestMapping`, `@GetMapping` и строит спецификацию. Никакого дополнительного кода не требуется для базовой конфигурации.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Как подключить springdoc-openapi к Spring Boot проекту?

1. Добавить зависимость (см. Q8).
2. Опционально — описать метаданные API через бин `OpenAPI`:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("My API")
                .version("1.0.0")
                .description("Описание API")
                .contact(new Contact()
                    .name("Team")
                    .email("team@example.com")))
            .externalDocs(new ExternalDocumentation()
                .description("Wiki")
                .url("https://wiki.example.com"));
    }
}
```

3. Конфигурация через `application.yml`:
```yaml
springdoc:
  swagger-ui:
    enabled: true
    try-it-out-enabled: true
    operations-sorter: alpha
  api-docs:
    enabled: true
  show-actuator: false
  packages-to-scan: com.example.api
  paths-to-match: /api/**
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Аннотация @Operation: назначение и параметры

`@Operation` описывает одну HTTP-операцию (метод + путь):

```java
@Operation(
    summary = "Получить пользователя по ID",
    description = "Возвращает полные данные пользователя. Требует роль USER.",
    operationId = "getUserById",
    tags = {"Users"},
    deprecated = false,
    responses = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Не найден")
    }
)
@GetMapping("/users/{id}")
public UserDto getUser(@PathVariable Long id) { ... }
```

Ключевые параметры:
- `summary` — краткое описание (до 120 символов)
- `description` — подробное описание (Markdown поддерживается)
- `operationId` — уникальный идентификатор (важен для codegen)
- `tags` — группировка операций
- `hidden = true` — скрыть операцию из документации

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Аннотации @Parameter и @Schema

`@Parameter` описывает параметр запроса:

```java
@GetMapping("/users")
public Page<UserDto> getUsers(
    @Parameter(
        description = "Номер страницы (0-based)",
        example = "0",
        required = false,
        schema = @Schema(minimum = "0")
    )
    @RequestParam(defaultValue = "0") int page,

    @Parameter(hidden = true)  // скрыть из документации
    @AuthenticationPrincipal UserDetails user
) { ... }
```

`@Schema` описывает модель данных:

```java
@Schema(description = "Данные пользователя")
public class UserDto {

    @Schema(description = "Идентификатор пользователя", example = "42")
    private Long id;

    @Schema(description = "Email адрес", format = "email", maxLength = 255)
    private String email;

    @Schema(description = "Роль", allowableValues = {"USER", "ADMIN"})
    private String role;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;
}
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Аннотация @ApiResponse и @ApiResponses

`@ApiResponse` описывает возможный HTTP-ответ:

```java
@Operation(summary = "Создать пользователя")
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Пользователь создан",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(
                name = "example",
                value = """{"id": 1, "email": "user@example.com"}"""
            )
        ),
        headers = @Header(
            name = "Location",
            description = "URL созданного ресурса"
        )
    ),
    @ApiResponse(responseCode = "400", description = "Невалидные данные",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    @ApiResponse(responseCode = "409", description = "Email уже занят")
})
@PostMapping("/users")
public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequest req) { ... }
```

Для переиспользования ответов используют аннотацию на уровне класса или `@ControllerAdvice`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Аннотации @Tag и @Tags для группировки

`@Tag` группирует операции в Swagger UI:

```java
// На уровне контроллера
@Tag(name = "Users", description = "Операции с пользователями")
@RestController
@RequestMapping("/api/users")
public class UserController { ... }

// На уровне метода (переопределение)
@Tag(name = "Admin", description = "Административные операции")
@Operation(tags = {"Users", "Admin"})
@DeleteMapping("/users/{id}")
public void deleteUser(@PathVariable Long id) { ... }
```

Глобальные теги с описанием определяют порядок групп:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addTagsItem(new Tag().name("Users").description("Управление пользователями"))
        .addTagsItem(new Tag().name("Orders").description("Управление заказами"));
}
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. @SecurityScheme и @SecurityRequirement

Объявление схемы безопасности на уровне приложения:

```java
@Configuration
@SecurityScheme(
    name = "BearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT токен в заголовке Authorization"
)
@SecurityScheme(
    name = "OAuth2",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://auth.example.com/oauth/authorize",
            tokenUrl = "https://auth.example.com/oauth/token",
            scopes = {
                @OAuthScope(name = "read", description = "Чтение данных"),
                @OAuthScope(name = "write", description = "Запись данных")
            }
        )
    )
)
public class OpenApiSecurityConfig { ... }
```

Применение к операции:
```java
@SecurityRequirement(name = "BearerAuth")
@GetMapping("/users/me")
public UserDto getCurrentUser() { ... }
```

Глобально для всех операций:
```java
new OpenAPI().security(List.of(new SecurityRequirement().addList("BearerAuth")));
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. (!) springdoc + Spring Security: как настроить доступ к документации?

По умолчанию Spring Security блокирует `/v3/api-docs` и `/swagger-ui/**`. Нужно открыть эти пути:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            // Открыть документацию
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

Для production рекомендуется **отключить** документацию полностью:
```yaml
# application-prod.yml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

Или закрыть с базовой аутентификацией / ограничить по IP через nginx.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Contract-first подход: определение и преимущества

**Contract-first** (API-first): сначала пишем OpenAPI-спецификацию, затем генерируем код.

Процесс:
1. Команды согласуют `openapi.yaml`
2. Генерация серверных интерфейсов: `openapi-generator-maven-plugin`
3. Реализация интерфейсов в контроллерах
4. Генерация клиентских SDK для потребителей

Преимущества:
- **Параллельная разработка** — frontend и backend работают одновременно по контракту
- **Единый источник правды** — спецификация определяет API, а не наоборот
- **Стабильный контракт** — изменения API требуют явного изменения спецификации
- **Лучшее проектирование** — API проектируется осознанно, не "вырастает" из кода
- **Автоматическая валидация** — генераторы создают валидационный код

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Code-first подход: определение и компромиссы

**Code-first**: пишем код контроллеров, документация генерируется автоматически через `springdoc-openapi`.

Преимущества:
- **Скорость старта** — нет overhead на написание YAML
- **Синхронизация** — документация всегда актуальна (генерируется из кода)
- **Меньше дублирования** — одна кодовая база, одна правда

Недостатки:
- **Дизайн API диктует реализация** — плохой код → плохой API
- **Нет параллельной разработки** — клиент ждёт реализации
- **Сложнее версионирование** — изменения кода сразу ломают контракт
- **Качество документации** — зависит от аннотаций разработчика

Наиболее популярен в небольших командах и внутренних API. Для публичных API предпочтительнее contract-first.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Contract-first vs Code-first: когда что выбирать?

| Критерий | Contract-first | Code-first |
|----------|----------------|------------|
| Публичный API | предпочтительно | рискованно |
| Внутренний API | да | да |
| Параллельная разработка (FE+BE) | да | нет |
| Быстрый прототип / MVP | медленнее | да |
| Много потребителей API | да | с оговорками |
| Размер команды < 3 | overhead | да |
| Микросервисы | да | оба применимы |

Гибридный подход: начать с code-first → зафиксировать спецификацию → перейти на contract-first для стабильного API.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) openapi-generator: что это и как использовать?

**openapi-generator** — инструмент генерации кода из OpenAPI-спецификации. Поддерживает 50+ языков и фреймворков.

Установка и использование CLI:
```bash
# Через npm
npm install @openapitools/openapi-generator-cli -g

# Генерация Java-клиента
openapi-generator-cli generate \
  -i openapi.yaml \
  -g java \
  -o ./client \
  --library resttemplate \
  --api-package com.example.client.api \
  --model-package com.example.client.model
```

Доступные генераторы (`-g`):
- Клиенты: `java`, `kotlin`, `typescript-axios`, `python`, `go`
- Серверы: `spring`, `kotlin-spring`, `nodejs-express-server`

Maven плагин:
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.10.0</version>
    <executions>
        <execution>
            <goals><goal>generate</goal></goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <generateApiTests>false</generateApiTests>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. Генерация серверных стабов через openapi-generator

Генератор `spring` создаёт интерфейсы, которые нужно реализовать:

```bash
openapi-generator-cli generate \
  -i openapi.yaml \
  -g spring \
  -o ./server \
  --additional-properties \
    interfaceOnly=true,\
    useSpringBoot3=true,\
    useTags=true,\
    apiPackage=com.example.api,\
    modelPackage=com.example.model
```

Параметр `interfaceOnly=true` генерирует только интерфейсы без реализации.

Реализация:
```java
@RestController
public class UsersApiController implements UsersApi {

    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        // своя реализация
        return ResponseEntity.ok(userService.findById(id));
    }
}
```

При изменении спецификации — регенерируем, компилятор укажет на несоответствия в реализации.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. springdoc с Spring WebFlux / реактивным API

Для реактивных приложений используется отдельная зависимость:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

Реактивные типы автоматически распознаются:
- `Mono<T>` → обычный объект
- `Flux<T>` → массив
- `ServerSentEvent<T>` → `text/event-stream`

Functional endpoints (Router Functions) требуют дополнительной конфигурации:

```java
// Документирование через RouterOperation
@RouterOperations({
    @RouterOperation(
        path = "/users/{id}",
        method = RequestMethod.GET,
        operation = @Operation(operationId = "getUser", tags = "Users")
    )
})
@Bean
public RouterFunction<ServerResponse> routes(UserHandler handler) {
    return route()
        .GET("/users/{id}", handler::getUser)
        .POST("/users", handler::createUser)
        .build();
}
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. Кастомизация springdoc: OpenApiCustomizer

`OpenApiCustomizer` — интерфейс для программной модификации сгенерированного объекта `OpenAPI`:

```java
@Bean
public OpenApiCustomizer globalHeaderCustomizer() {
    return openApi -> {
        // Добавить глобальный заголовок ко всем операциям
        openApi.getPaths().values().forEach(pathItem ->
            pathItem.readOperations().forEach(operation ->
                operation.addParametersItem(
                    new Parameter()
                        .name("X-Request-ID")
                        .in("header")
                        .required(false)
                        .schema(new StringSchema())
                        .description("Идентификатор запроса для трейсинга")
                )
            )
        );
    };
}

@Bean
public OpenApiCustomizer sortTagsCustomizer() {
    return openApi -> {
        // Сортировать теги
        if (openApi.getTags() != null) {
            openApi.getTags().sort(Comparator.comparing(Tag::getName));
        }
    };
}
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. (!) GroupedOpenApi: разбивка документации на группы

`GroupedOpenApi` позволяет создать несколько разделов документации:

```java
@Configuration
public class OpenApiGroupConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .displayName("Public API")
            .pathsToMatch("/api/public/**")
            .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .displayName("Admin API")
            .pathsToMatch("/api/admin/**")
            .addOpenApiCustomizer(openApi ->
                openApi.info(new Info().title("Admin API").version("1.0"))
            )
            .build();
    }
}
```

В Swagger UI появится выпадающий список групп. Каждая группа доступна по своему URL: `/v3/api-docs/public`, `/v3/api-docs/admin`.

Настройка в `application.yml`:
```yaml
springdoc:
  swagger-ui:
    urls:
      - name: Public API
        url: /v3/api-docs/public
      - name: Admin API
        url: /v3/api-docs/admin
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. Валидация входящих запросов по схеме: @Valid и @Validated

**Bean Validation** в связке с OpenAPI-аннотациями:

```java
public class CreateUserRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    @Schema(description = "Имя пользователя", minLength = 2, maxLength = 100)
    private String name;

    @NotNull
    @Email
    @Schema(description = "Email", format = "email")
    private String email;

    @Min(18) @Max(120)
    @Schema(description = "Возраст", minimum = "18", maximum = "120")
    private Integer age;
}

@PostMapping("/users")
public ResponseEntity<UserDto> createUser(
    @RequestBody @Valid CreateUserRequest request
) { ... }
```

Зависимость Bean Validation в Spring Boot 3:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

`springdoc-openapi` автоматически читает `@NotNull`, `@Size`, `@Min`, `@Max`, `@Pattern` и переносит их в OpenAPI-схему.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Spectral: линтинг OpenAPI-спецификаций

**Spectral** — линтер для OpenAPI и AsyncAPI от Stoplight. Проверяет спецификацию по настраиваемым правилам.

Установка и запуск:
```bash
npm install -g @stoplight/spectral-cli

# Линтинг файла
spectral lint openapi.yaml

# С кастомным набором правил
spectral lint openapi.yaml --ruleset .spectral.yml
```

Пример правил в `.spectral.yml`:
```yaml
extends: ["spectral:oas"]  # встроенные правила OAS

rules:
  operation-summary:
    description: "Каждая операция должна иметь summary"
    severity: error
    given: "$.paths[*][*]"
    then:
      field: summary
      function: truthy

  no-x-internal:
    description: "Не использовать x-internal в публичном API"
    severity: warn
    given: "$..x-internal"
    then:
      function: undefined
```

Интеграция в CI:
```yaml
# GitHub Actions
- name: Lint OpenAPI
  run: spectral lint openapi.yaml --fail-severity=error
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. (!) Версионирование API: /v1/ vs заголовки

**Подходы к версионированию:**

**1. URI Path (наиболее распространён):**
```
GET /api/v1/users
GET /api/v2/users
```
- Просто, кэшируемо, явно видно в логах
- `springdoc` поддерживает через `GroupedOpenApi` с `pathsToMatch("/api/v1/**")`

**2. Заголовок запроса:**
```
GET /api/users
Accept-Version: v2
```
- Чистые URL, но сложнее в документировании
- В OpenAPI: параметр типа `header`

**3. Content Negotiation (Media Type):**
```
Accept: application/vnd.example.v2+json
```
- RESTful-пуристский подход
- Поддерживается через `produces` в контроллере

**4. Query Parameter:**
```
GET /api/users?version=2
```
- Не рекомендуется — засоряет URL, сложнее кэшировать

**Рекомендация**: URI Path для публичных API; заголовки для внутренних, где URL важен как ресурс.

Документирование в `springdoc`:
```java
@Bean
public GroupedOpenApi v1Api() {
    return GroupedOpenApi.builder()
        .group("v1").pathsToMatch("/api/v1/**").build();
}

@Bean
public GroupedOpenApi v2Api() {
    return GroupedOpenApi.builder()
        .group("v2").pathsToMatch("/api/v2/**").build();
}
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. discriminator и полиморфизм: oneOf / anyOf / allOf

OpenAPI поддерживает полиморфизм через композицию схем:

**`allOf`** — наследование/расширение:
```yaml
schemas:
  Animal:
    type: object
    properties:
      name: { type: string }
  Dog:
    allOf:
      - $ref: "#/components/schemas/Animal"
      - type: object
        properties:
          breed: { type: string }
```

**`oneOf`** — ровно одна из схем:
```yaml
schemas:
  Payment:
    oneOf:
      - $ref: "#/components/schemas/CardPayment"
      - $ref: "#/components/schemas/CashPayment"
    discriminator:
      propertyName: type        # поле-дискриминатор
      mapping:
        card: "#/components/schemas/CardPayment"
        cash: "#/components/schemas/CashPayment"
```

**`anyOf`** — одна или несколько схем.

`discriminator` сообщает клиентам, какое поле определяет тип объекта. Генераторы кода используют его для создания наследования.

В Java/springdoc:
```java
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPayment.class, name = "card"),
    @JsonSubTypes.Type(value = CashPayment.class, name = "cash")
})
@Schema(oneOf = {CardPayment.class, CashPayment.class})
public abstract class Payment { ... }
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. $ref: переиспользование компонентов спецификации

`$ref` — механизм ссылок в OpenAPI (основан на JSON Reference):

```yaml
paths:
  /users/{id}:
    get:
      parameters:
        - $ref: "#/components/parameters/IdParam"     # локальная ссылка
      responses:
        "200":
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/User"
        "404":
          $ref: "#/components/responses/NotFound"

  /orders/{id}:
    get:
      parameters:
        - $ref: "#/components/parameters/IdParam"     # повторное использование
```

Типы ссылок:
- **Локальная** (`#/components/...`) — внутри документа
- **Файловая** (`./schemas/user.yaml#/User`) — внешний файл, тот же сервер
- **URL** (`https://example.com/schemas/user.yaml`) — удалённый ресурс

Разбивка на файлы для больших спецификаций:
```
api/
  openapi.yaml          # главный файл
  paths/
    users.yaml
    orders.yaml
  schemas/
    user.yaml
    order.yaml
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. (!) OpenAPI в CI/CD: generate-and-compare, contract testing

**Стратегии использования OpenAPI в CI/CD:**

**1. Generate-and-compare:**
Генерируем спецификацию из кода, сравниваем с эталоном в репозитории:
```bash
# Генерация текущей спецификации
curl http://localhost:8080/v3/api-docs > current.json

# Сравнение с зафиксированной версией
diff expected-openapi.json current.json
# Если есть отличия — сборка падает
```

**2. Backward compatibility check с `openapi-diff`:**
```bash
docker run --rm \
  -v $(pwd):/specs openapitools/openapi-diff \
  /specs/old-openapi.yaml /specs/new-openapi.yaml \
  --fail-on-incompatible
```

**3. Lint в PR:**
```yaml
# GitHub Actions
- name: Lint OpenAPI spec
  run: spectral lint openapi.yaml --fail-severity=warn
```

**4. Генерация клиентов при изменении спецификации:**
```yaml
- name: Generate client
  if: steps.diff.outputs.changed == 'true'
  run: openapi-generator-cli generate ...
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. Contract testing с Pact: как соотносится с OpenAPI?

**Contract Testing** — тестирование контракта между потребителем (consumer) и поставщиком (provider) API.

**Pact** — наиболее популярный фреймворк:
```java
// Consumer side: определяем ожидания
@PactTestFor(providerName = "UserService")
@Pact(consumer = "OrderService")
public RequestResponsePact createPact(PactDslWithProvider builder) {
    return builder
        .given("user 1 exists")
        .uponReceiving("a request for user 1")
        .path("/api/users/1")
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(new PactDslJsonBody()
            .integerType("id", 1)
            .stringType("email", "test@example.com"))
        .toPact();
}
```

**Pact vs OpenAPI:**
- OpenAPI описывает все возможные запросы/ответы
- Pact описывает фактически используемые взаимодействия между конкретными сервисами
- Они дополняют друг друга: OpenAPI — документация, Pact — гарантия совместимости

Интеграция: можно генерировать Pact-контракты из OpenAPI через `pact-jvm-provider-spring` и `openapi-pact-provider`.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q31. (!) Документирование ошибок: ProblemDetail (RFC 7807)

**RFC 7807 (Problem Details for HTTP APIs)** — стандарт для описания ошибок в REST API. Spring Boot 3 поддерживает нативно через `ProblemDetail`.

Структура:
```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Email адрес обязателен",
  "instance": "/api/users",
  "errors": [{"field": "email", "message": "must not be blank"}]
}
```

Использование в Spring Boot 3:
```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        pd.setType(URI.create("https://example.com/problems/user-not-found"));
        pd.setTitle("User Not Found");
        return pd;
    }
}
```

Включить в Spring Boot:
```yaml
spring:
  mvc:
    problemdetails:
      enabled: true
```

Документирование в OpenAPI:
```java
@ApiResponse(
    responseCode = "400",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
```

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q32. OAuth2 и Bearer token в Swagger UI

Настройка OAuth2 в документации позволяет получать токены прямо из Swagger UI:

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("oauth2", new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                    .authorizationCode(new OAuthFlow()
                        .authorizationUrl("https://auth.example.com/oauth/authorize")
                        .tokenUrl("https://auth.example.com/oauth/token")
                        .scopes(new Scopes()
                            .addString("read", "Чтение данных")
                            .addString("write", "Запись данных")
                        )
                    )
                )
            )
            .addSecuritySchemes("bearer", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
            )
        )
        .security(List.of(new SecurityRequirement().addList("bearer")));
}
```

Конфигурация Swagger UI для OAuth2:
```yaml
springdoc:
  swagger-ui:
    oauth:
      client-id: swagger-client
      scopes: read,write
    oauth2-redirect-url: http://localhost:8080/swagger-ui/oauth2-redirect.html
```

`oauth2-redirect-url` нужно добавить как разрешённый redirect URI в OAuth2 сервере.

---


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q33. Лучшие практики написания OpenAPI-спецификаций

**Структура и организация:**
- Выносить переиспользуемые компоненты в `components`
- Использовать `$ref` вместо дублирования схем
- Разбивать большие спецификации на несколько файлов
- `operationId` — обязателен, уникален, в `camelCase`

**Описания:**
- Каждая операция должна иметь `summary` (краткое) и `description` (подробное)
- Каждое поле схемы — `description` и `example`
- Документировать все возможные коды ответов, включая ошибки

**Схемы данных:**
- Использовать `$ref` для общих моделей (не копировать)
- Указывать `format` для строк: `email`, `date-time`, `uuid`, `uri`
- Задавать `nullable: true` явно (OAS 3.0) или `type: ["string", "null"]` (OAS 3.1)
- Помечать поля только для чтения: `readOnly: true`

**Безопасность:**
- Документировать все схемы безопасности в `components/securitySchemes`
- Указывать `security` на уровне операций, а не только глобально
- Явно указывать `[]` (пустой массив) для публичных эндпоинтов при глобальной security

**CI/CD:**
- Линтинг Spectral в пайплайне
- Проверка backward compatibility при изменениях
- Версионирование спецификации вместе с кодом

---

## Полезные ссылки

### Официальная документация
- [OpenAPI Specification 3.1](https://spec.openapis.org/oas/v3.1.0)
- [springdoc-openapi документация](https://springdoc.org/)
- [openapi-generator](https://openapi-generator.tech/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

### Инструменты
- [Swagger Editor](https://editor.swagger.io/) — онлайн-редактор спецификаций
- [Stoplight Studio](https://stoplight.io/studio) — GUI-редактор OpenAPI
- [Spectral](https://stoplight.io/open-source/spectral) — линтер OpenAPI
- [openapi-diff](https://github.com/OpenAPITools/openapi-diff) — сравнение версий
- [Redoc](https://redocly.com/redoc/) — альтернатива Swagger UI

### Статьи
- [Documenting a Spring REST API Using OpenAPI 3.0 (Baeldung)](https://www.baeldung.com/spring-rest-openapi-documentation)
- [Spring Boot 3 и ProblemDetail (Baeldung)](https://www.baeldung.com/spring-boot-problem-details)

---

## See also

- [HTTP и REST](http-rest-interview.md) — семантика HTTP, REST-принципы, CORS, кэширование
- [gRPC](grpc-interview.md) — бинарный протокол, Protocol Buffers, стриминг, сравнение с REST
- [GraphQL](graphql-interview.md) — схема, резолверы, N+1 проблема, подписки
- [WebSocket](websocket-interview.md) — двунаправленная связь, STOMP, масштабирование
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — автоконфигурация, стартеры, Actuator
- [Spring Security](../frameworks/spring/spring-security-interview.md) — аутентификация, авторизация, OAuth2, JWT
- [Spring MVC](../frameworks/spring/spring-mvc-interview.md) — контроллеры, фильтры, перехватчики, DispatcherServlet
- [API Gateway](../architecture/api-gateway-interview.md) — маршрутизация, rate limiting, аутентификация на уровне шлюза


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [API Design Best Practices](api-design-best-practices-interview.md)
- [API Versioning](api-versioning-interview.md)
- [GraphQL](graphql-interview.md)
- [gRPC](grpc-interview.md)
- [HTTP и REST](http-rest-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
- [Шпаргалка: OpenAPI/Swagger](../../development/api/api-tools/swagger/openapi-swagger.md) — теория
