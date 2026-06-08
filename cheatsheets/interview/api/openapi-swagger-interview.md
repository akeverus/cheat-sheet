---
title: "Вопросы на собеседовании: OpenAPI / Swagger"
description: "Вопросы и ответы по OpenAPI Specification и Swagger для Java-разработчика: структура документа, springdoc-openapi, аннотации, contract-first vs code-first, генерация клиентов, OAuth2, линтинг."
tags:
  - interview
  - api
  - openapi-swagger-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "OpenAPI / Swagger"
  - "OpenAPI interview"
  - "Swagger interview"
prerequisites:
  - "[[openapi-swagger]]"
next: []
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

**OpenAPI Specification (OAS)** — это стандарт формального описания REST API в машиночитаемом формате (`YAML` или `JSON`). Один файл описывает весь контракт API: эндпоинты, параметры запросов, тела запросов и ответов, схемы данных, коды ошибок, механизмы аутентификации.

Главная идея — описать API не текстом для человека, а структурой, которую может прочитать машина. Из этого вытекают все остальные свойства:

- **Независимость от языка** — описание отделено от реализации, поэтому один и тот же контракт обслуживает сервер на Java и клиента на TypeScript.
- **Инструментальная экосистема** — раз формат машиночитаем, по нему автоматически генерируют клиентов, серверные стабы и документацию (Swagger UI), а не пишут их руками.
- **Контракт** — спецификация становится единым соглашением между командами frontend и backend: обе стороны работают против неё, а не против чужого кода.
- **Валидация** — запросы и ответы можно автоматически проверять на соответствие спецификации (в тестах, на gateway, в CI).

Формат развивает консорциум **OpenAPI Initiative (OAI)** под эгидой Linux Foundation — поэтому это открытый отраслевой стандарт, а не продукт одного вендора.

---

## Q2. Версии OAS: 2.0 vs 3.0 vs 3.1 — ключевые отличия

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

Главные смысловые сдвиги между поколениями:

- **2.0 → 3.0** — тело запроса вынесли в отдельный `requestBody` (раньше оно мешалось среди параметров), несколько серверов стало можно описать массивом `servers` вместо одиночных `host`/`basePath`, а переиспользуемые объекты собрали под единый `components`.
- **3.0 → 3.1** — спецификацию привели к **полной совместимости с JSON Schema draft 2020-12**. До этого OAS использовал лишь подмножество JSON Schema, из-за чего схемы из других инструментов не подходили один-в-один. Заодно `nullable: true` заменили на честный `type: ["string", "null"]` и добавили секцию `webhooks`.

**OAS 3.1** — наиболее актуальная версия. Её использует `springdoc-openapi` начиная с версии 2.x (Spring Boot 3), поэтому именно с ней вы столкнётесь в современном Spring-проекте.

---

## Q3. (!) Swagger vs OpenAPI — в чём разница терминологии?

Коротко: **OpenAPI** — это сам стандарт (спецификация), а **Swagger** — набор инструментов вокруг него и историческое название. Разница чисто терминологическая, но на собеседовании её любят уточнять.

Как так вышло. Исторически **Swagger** — это проект компании SmartBear, включавший три части:
1. **Swagger Specification** — формат описания API (версии 1.x, 2.0)
2. **Swagger UI** — визуализация и тестирование API
3. **Swagger Codegen** — генератор клиентов

В 2016 году SmartBear передал спецификацию в **OpenAPI Initiative**, и с версии 3.0 она называется **OpenAPI Specification**. А вот инструменты остались под брендом Swagger. Отсюда и водораздел:

- **OpenAPI** = спецификация (стандарт), версии 3.x.
- **Swagger** = инструменты (UI, Codegen, Editor) и старое имя самого формата (версии 1.x–2.0).

Поэтому говорить «Swagger-спецификация» про OAS 3.x формально некорректно (это уже OpenAPI), хотя в обиходе так делают повсеместно.

---

## Q4. Структура OpenAPI-документа: верхнеуровневые секции

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

## Q5. Секция paths: как описывается эндпоинт?

`paths` — сердце спецификации: здесь перечислены все URL-шаблоны, а внутри каждого — операции по HTTP-методам (`get`, `post`, …). Один эндпоинт = путь + метод + его параметры, тело и возможные ответы.

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

Разбор по полям:
- `parameters` — каждый параметр привязан к месту: `in: path` (часть URL), `query`, `header` или `cookie`. Для path-параметров `required: true` обязателен.
- `responses` — карта по кодам ответа; тело описывается через `content` → media type → `schema`. Часть ответов вынесена в `components` и подключена через `$ref` (`404`), чтобы не дублировать.
- `operationId` — уникальный идентификатор операции. Важен для генерации клиентов: именно он становится именем сгенерированного метода (`getUserById(...)`), поэтому его стоит задавать осмысленно в `camelCase`.

---

## Q6. Секция components: для чего используется?

`components` — это «библиотека» переиспользуемых объектов спецификации. Сами по себе эти объекты ничего не описывают (они не привязаны к путям), их подключают по ссылке `$ref` из `paths`. Смысл — убрать дублирование: одну схему `User` или ответ `NotFound` определяют один раз, а используют во множестве операций.

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

## Q7. (!) Что такое Swagger UI и как он работает?

**Swagger UI** — это веб-приложение (статичная страница на JS), которое превращает «сырой» OpenAPI-документ в интерактивную страницу с живыми кнопками. Спецификацию в JSON неудобно читать человеку — Swagger UI делает её наглядной и сразу пригодной для ручного тестирования.

Что он даёт:
- отображает все эндпоинты с описаниями, параметрами и схемами ответов;
- позволяет выполнять реальные HTTP-запросы прямо из браузера (кнопка **Try it out**);
- поддерживает аутентификацию (Bearer token, OAuth2, Basic Auth) — токен можно ввести один раз и отправлять запросы от своего имени.

Как это работает под капотом:
1. Swagger UI **сам не знает** про ваше API — он подгружает спецификацию (JSON/YAML) по HTTP. В `springdoc-openapi` это `/v3/api-docs`.
2. Парсит документ и динамически строит интерактивный UI из его содержимого.
3. Запросы из UI идут **напрямую к вашему API** из браузера (не через прокси Swagger UI). Поэтому для cross-origin сценариев важны CORS-настройки.

Пути по умолчанию в `springdoc-openapi`:
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

## Q8. springdoc-openapi: зависимость и автоконфигурация

**springdoc-openapi** — библиотека, которая автоматически генерирует OpenAPI 3.x документацию из кода Spring Boot-приложения (подход code-first). Достаточно добавить стартер — и на старте приложения она просканирует контроллеры и поднимет Swagger UI, без отдельного YAML-файла.

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

Почему не нужен дополнительный код: автоконфигурация на старте сканирует `@RestController`, `@RequestMapping`, `@GetMapping` и т.п., читает типы параметров и возвращаемых значений (DTO) и строит спецификацию по ним. Аннотации (`@Operation`, `@Schema` и др.) нужны только чтобы **обогатить** документацию описаниями и примерами — для базовой работоспособности они не обязательны.

---

## Q9. (!) Как подключить springdoc-openapi к Spring Boot проекту?

Минимум — одна зависимость: после неё документация и Swagger UI уже работают. Остальное (бин `OpenAPI`, настройки в `application.yml`) — это тонкая настройка под себя.

1. Добавить зависимость (см. Q8) — это единственный обязательный шаг.
2. Опционально — описать метаданные API (title, версия, контакты) через бин `OpenAPI`. Так заголовок страницы и описание будут осмысленными, а не дефолтными:

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

3. Опционально — тонкая настройка через `application.yml`. Полезно ограничить область сканирования (`packages-to-scan`/`paths-to-match`), чтобы в документацию не попадало лишнее, и убрать actuator-эндпоинты:
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

## Q10. Аннотация @Operation: назначение и параметры

`@Operation` навешивают на метод контроллера, чтобы описать одну HTTP-операцию (метод + путь) человеческим языком: что она делает, как называется в codegen, в какую группу попадает. Без неё springdoc возьмёт лишь имя метода и типы — `@Operation` добавляет смысл поверх этого.

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

## Q11. Аннотации @Parameter и @Schema

Это две разные «зоны ответственности»: `@Parameter` описывает **входной параметр операции** (query/path/header), а `@Schema` — **поле модели данных** (DTO) или ограничения типа. Грубо: `@Parameter` — про сигнатуру эндпоинта, `@Schema` — про структуру JSON.

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

## Q12. (!) Аннотация @ApiResponse и @ApiResponses

`@ApiResponse` описывает один возможный исход операции — конкретный HTTP-код вместе с телом, схемой и заголовками. `@ApiResponses` — это просто контейнер для нескольких `@ApiResponse`, потому что у операции исходов обычно несколько (`201`, `400`, `409`). Главная польза — задокументировать **ошибочные** ответы, а не только happy path: springdoc сам угадает успешный код по возвращаемому типу, а 4xx/5xx нужно описывать явно.

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

## Q13. Аннотации @Tag и @Tags для группировки

`@Tag` объединяет операции в логические разделы Swagger UI (например, «Users», «Orders») — это визуальная группировка внутри одной спецификации, не путать с `GroupedOpenApi`, который делит документацию на отдельные документы (см. Q23). Обычно `@Tag` вешают на контроллер, и все его методы попадают в одну группу.

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

Зачем объявлять теги ещё и глобально (в бине `OpenAPI`): порядок групп в UI берётся из глобального списка тегов, плюс там удобно один раз задать описание группы, не повторяя его в каждом контроллере.

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addTagsItem(new Tag().name("Users").description("Управление пользователями"))
        .addTagsItem(new Tag().name("Orders").description("Управление заказами"));
}
```

---

## Q14. @SecurityScheme и @SecurityRequirement

Документирование безопасности — это два шага: сначала **объявить** способ аутентификации, потом **применить** его. `@SecurityScheme` объявляет схему (как именно клиент аутентифицируется: Bearer JWT, OAuth2, Basic), а `@SecurityRequirement` указывает, что конкретная операция (или всё API) требует эту схему. Без второй аннотации Swagger UI знает о существовании схемы, но не показывает у эндпоинта замок и не подставляет токен.

Шаг 1 — объявление схемы на уровне приложения:

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

Шаг 2 — применение схемы к конкретной операции (по `name` из объявления):
```java
@SecurityRequirement(name = "BearerAuth")
@GetMapping("/users/me")
public UserDto getCurrentUser() { ... }
```

Либо глобально — для всех операций сразу (удобно, если защищено почти всё; публичные эндпоинты потом помечают пустым `security: []`):
```java
new OpenAPI().security(List.of(new SecurityRequirement().addList("BearerAuth")));
```

**Важно:** обе аннотации влияют только на документацию. Реальную защиту обеспечивает Spring Security — `@SecurityScheme` лишь описывает, как клиенту авторизоваться.

---

## Q15. (!) springdoc + Spring Security: как настроить доступ к документации?

Суть проблемы: когда в проекте включён Spring Security, `anyRequest().authenticated()` закрывает в том числе служебные пути Swagger, и вместо документации вы получаете 401/редирект на логин. Решение — явно открыть пути документации в цепочке фильтров.

Открываем пути Swagger:

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

**Подводный камень:** открытая в production документация — это утечка карты атаки (полный список эндпоинтов, схем и параметров наружу). Поэтому в prod-профиле её обычно **отключают полностью**:
```yaml
# application-prod.yml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

Компромисс, если документация всё же нужна снаружи: закрыть её basic-аутентификацией или ограничить по IP/сети через nginx — тогда она доступна только команде, а не публике.

---

## Q16. Contract-first подход: определение и преимущества

**Contract-first** (он же API-first): сначала пишут OpenAPI-спецификацию (`openapi.yaml`) как первичный артефакт, и уже из неё генерируют код. Спецификация здесь — не побочный продукт, а отправная точка и единственный источник правды.

Процесс:
1. Команды согласуют `openapi.yaml` (ревьюят его как обычный код в PR).
2. Из спецификации генерируют серверные интерфейсы (`openapi-generator-maven-plugin`).
3. Эти интерфейсы реализуют в контроллерах.
4. Для потребителей генерируют клиентские SDK из той же спецификации.

Почему это выгодно:
- **Параллельная разработка** — как только контракт согласован, frontend и backend работают одновременно: фронт мокает по спецификации, не дожидаясь готового бэкенда.
- **Единый источник правды** — API определяет спецификация, а не текущее состояние кода; меньше расхождений между «как задумано» и «как реализовано».
- **Стабильный контракт** — изменить API нельзя случайно: нужно осознанно поправить спецификацию, что заметно в ревью.
- **Лучшее проектирование** — API сначала продумывают, а не позволяют ему стихийно «вырасти» из реализации.
- **Автоматическая валидация** — генераторы создают код проверки запросов по схеме «из коробки».

---

## Q17. (!) Code-first подход: определение и компромиссы

**Code-first** — обратный подход: первичен код контроллеров, а спецификация генерируется из него автоматически (`springdoc-openapi`). Спецификация здесь — производная от кода, а не наоборот.

Плюсы:
- **Скорость старта** — не нужно заранее писать YAML, начинаешь сразу с контроллеров.
- **Синхронизация** — документация генерируется из кода, поэтому не «протухает»: что в коде, то и в спецификации.
- **Меньше дублирования** — одна кодовая база, один источник правды; нет отдельного артефакта, который можно забыть обновить.

Минусы (по сути это зеркало плюсов contract-first):
- **Дизайн диктует реализация** — каким получился код, таким будет и API; неудачные технические решения протекают в контракт.
- **Нет параллельной разработки** — потребитель ждёт, пока бэкенд реализует эндпоинт, ведь спецификация появляется только из готового кода.
- **Хрупкий контракт** — изменение кода мгновенно меняет контракт, иногда ломая клиентов незаметно.
- **Качество документации зависит от дисциплины** — забыл аннотацию `@Schema`/`@ApiResponse` — описание неполное.

**Сценарий применения:** code-first хорош для небольших команд и внутренних API, где скорость важнее строгости контракта. Для публичных API с внешними потребителями предпочтительнее contract-first.

---

## Q18. Contract-first vs Code-first: когда что выбирать?

Эмпирическое правило одной фразой: чем больше **внешних** потребителей и чем критичнее стабильность контракта — тем сильнее перевешивает contract-first; чем меньше команда и важнее скорость — тем уместнее code-first.

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

## Q19. (!) openapi-generator: что это и как использовать?

**openapi-generator** — инструмент, который по OpenAPI-спецификации генерирует код: клиентские SDK, серверные стабы, модели данных. Это рабочая лошадка contract-first подхода (см. Q16): спецификация на входе, готовый код на выходе. Поддерживает 50+ языков и фреймворков, что и делает контракт по-настоящему language-agnostic.

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

На практике CLI используют редко — генерацию встраивают в сборку, чтобы код пересоздавался автоматически при каждом изменении спецификации. Maven-плагин:
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

## Q20. Генерация серверных стабов через openapi-generator

Ключевая идея серверной генерации: генератор создаёт не контроллеры целиком, а **интерфейсы** (с готовыми аннотациями `@RequestMapping` и сигнатурами методов из `operationId`), которые вы реализуете своим кодом. Так контракт (интерфейс) генерируется, а бизнес-логика остаётся вашей и не перезатирается.

Генератор `spring` создаёт интерфейсы под реализацию:

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

В этом и главное преимущество: при изменении спецификации интерфейс перегенерируется, и **компилятор сам укажет** на каждое место, где реализация больше не соответствует контракту (изменилась сигнатура, появился новый метод). Контракт перестаёт «расходиться» с кодом молча.

---

## Q21. springdoc с Spring WebFlux / реактивным API

springdoc поддерживает реактивный стек, но через **отдельный стартер** (`-webflux-ui` вместо `-webmvc-ui`) — это первое, что нужно поменять при переходе на WebFlux. Дальше работа аннотированных контроллеров почти не отличается от MVC; реактивные обёртки springdoc «разворачивает» сам.

Зависимость для реактивных приложений:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

Реактивные типы springdoc разворачивает в их «полезную нагрузку» — в схему попадает `T`, а не обёртка:
- `Mono<T>` → обычный объект `T`
- `Flux<T>` → массив `T`
- `ServerSentEvent<T>` → поток `text/event-stream`

**Подводный камень:** автоматика работает для аннотированных `@RestController`. А вот функциональные эндпоинты (Router Functions) springdoc не видит «бесплатно» — у них нет аннотаций, которые он сканирует, — поэтому их описывают вручную через `@RouterOperation`:

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

## Q22. Кастомизация springdoc: OpenApiCustomizer

`OpenApiCustomizer` — это хук, который springdoc вызывает **после** того, как построил спецификацию из аннотаций, давая вам код-доступ к готовому объекту `OpenAPI`. Нужен, когда что-то невозможно или неудобно выразить аннотациями: добавить общий заголовок ко всем операциям, отсортировать теги, дописать поля массово. Бин просто публикуют — springdoc подхватит его автоматически.

Сценарий применения — программная правка готового объекта `OpenAPI`:

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

## Q23. (!) GroupedOpenApi: разбивка документации на группы

`GroupedOpenApi` делит одно приложение на **несколько независимых OpenAPI-документов**, каждый со своим URL и своим набором путей. В отличие от `@Tag` (который лишь группирует операции внутри одного документа, см. Q13), здесь это физически разные спецификации — типичные кейсы: разделить public/admin API, версии v1/v2 (см. Q26), внутреннее и внешнее.

Пример — публичный и админский разделы:

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

## Q24. Валидация входящих запросов по схеме: @Valid и @Validated

Удобство springdoc в том, что **одни и те же** аннотации Bean Validation (`@NotNull`, `@Size`, `@Min`…) работают сразу на два фронта: в рантайме их проверяет Spring (отклоняет невалидный запрос с 400), а springdoc заодно переносит их в OpenAPI-схему как ограничения (`minLength`, `minimum` и т.п.). Одно ограничение — и валидация, и документация, без дублирования.

Bean Validation в связке с OpenAPI-аннотациями:

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

## Q25. Spectral: линтинг OpenAPI-спецификаций

**Spectral** — линтер OpenAPI/AsyncAPI от Stoplight: то же, что ESLint для кода, только для спецификации. Он проверяет документ по правилам (встроенным и своим) и помогает держать единый стиль во всех API: у каждой операции есть `summary`, нет «забытых» полей, соблюдены гайдлайны компании. Главное применение — гейт в CI, чтобы плохая спецификация не доезжала до мерджа.

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

## Q26. (!) Версионирование API: /v1/ vs заголовки

Версионирование нужно, чтобы вносить ломающие изменения, не убивая старых клиентов: вы поднимаете `v2`, а `v1` продолжает работать. Главный выбор — **где** хранить номер версии: в URL или в заголовке. Это компромисс между «явно и кэшируемо» (URL) и «чистый ресурсный URL» (заголовок).

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

## Q27. discriminator и полиморфизм: oneOf / anyOf / allOf

В OpenAPI нет ключевого слова «наследование» — полиморфизм собирают из трёх композиторов схем. Запомнить их проще через аналогию с логикой: `allOf` = И (объект удовлетворяет всем схемам — это наследование), `oneOf` = исключающее ИЛИ (ровно одна схема), `anyOf` = ИЛИ (одна или несколько).

**`allOf`** — наследование/расширение (объект = база + добавки):
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

**`anyOf`** — объект подходит под одну или несколько схем (валиден, если совпала хотя бы одна).

Зачем нужен `discriminator`: при `oneOf` клиенту иначе пришлось бы перебором угадывать, какой именно вариант пришёл. `discriminator` явно говорит: «смотри на поле `type` — его значение определяет конкретную схему». Генераторы кода используют это, чтобы построить иерархию классов и десериализовать JSON в правильный подтип.

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

## Q28. $ref: переиспользование компонентов спецификации

`$ref` — это «указатель» на другой кусок спецификации (на основе JSON Reference). Вместо того чтобы копировать одну и ту же схему или ответ в десяток мест, вы определяете объект один раз (обычно в `components`, см. Q6) и ссылаетесь на него. Это убирает дублирование и держит контракт согласованным: правка в одном месте применяется везде.

Пример — ссылки на параметр и схему:

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

## Q29. (!) OpenAPI в CI/CD: generate-and-compare, contract testing

Цель этих практик одна — **не дать контракту измениться незаметно**. Спецификацию встраивают в пайплайн как полноценный артефакт с гейтами: проверяют, что код не разошёлся с эталоном, что изменения не ломают клиентов и что стиль соблюдён.

**1. Generate-and-compare** — ловит расхождение кода и зафиксированной спецификации. Генерируем спецификацию из текущего кода и сравниваем с эталоном в репозитории; если разошлись — кто-то поменял API, не обновив контракт:
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

## Q30. Contract testing с Pact: как соотносится с OpenAPI?

**Contract Testing** проверяет, что потребитель (consumer) и поставщик (provider) API понимают друг друга, без поднятия обоих сервисов в одном тесте. Идея Pact — **consumer-driven**: потребитель записывает свои ожидания («я зову `GET /users/1` и жду такое тело»), а поставщик в своём пайплайне прогоняет эти ожидания против себя и падает, если сломал то, чем реально пользуются.

**Pact** — самый популярный фреймворк для этого:
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

**Pact vs OpenAPI — это не конкуренты, а разные срезы:**
- **OpenAPI** описывает **все возможные** запросы и ответы API — это полный контракт и документация.
- **Pact** фиксирует только те взаимодействия, что **реально используются** конкретной парой сервисов. Поставщик может безопасно удалить поле, которым никто не пользуется, — Pact этого не заметит, а вот OpenAPI-diff (Q38) заметит.
- Поэтому они дополняют друг друга: OpenAPI отвечает за документацию и проверку совместимости в целом, Pact — за гарантию, что конкретные интеграции не сломаются.

Интеграция: можно генерировать Pact-контракты из OpenAPI через `pact-jvm-provider-spring` и `openapi-pact-provider`.

---

## Q31. (!) Документирование ошибок: ProblemDetail (RFC 7807)

**RFC 7807 (Problem Details for HTTP APIs)** — стандартный машиночитаемый формат тела ошибки для REST API. Проблема, которую он решает: без стандарта каждый сервис придумывает свою форму ошибки (`{"error": ...}`, `{"message": ...}`, `{"code": ...}`), и клиентам приходится разбирать их по-разному. RFC 7807 задаёт единый набор полей (`type`, `title`, `status`, `detail`, `instance`) с фиксированным media type `application/problem+json`. Spring Boot 3 поддерживает его нативно через класс `ProblemDetail`.

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

## Q32. OAuth2 и Bearer token в Swagger UI

Зачем это: если описать схему OAuth2 в спецификации, Swagger UI сам проведёт пользователя через authorization code flow (кнопка **Authorize** → редирект на authorization server → токен) и будет подставлять полученный токен в кнопку **Try it out**. То есть тестировать защищённые эндпоинты можно прямо из браузера, не добывая токен вручную через curl/Postman.

Объявление схем безопасности (OAuth2 + Bearer) в бине `OpenAPI`:

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

## Q33. Лучшие практики написания OpenAPI-спецификаций

Общая идея всех правил ниже: спецификация — это контракт и документация для людей и инструментов, поэтому она должна быть DRY (без дублирования), самодокументированной (описания и примеры) и проверяемой в CI. Сгруппировано по областям.

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

## Q34. Callbacks vs Webhooks в OpenAPI 3.1

И `callbacks`, и `webhooks` описывают запросы, которые **сервер инициирует сам** (асинхронные out-of-band вызовы), но привязаны к разным точкам спецификации.

**`callbacks`** — описываются внутри конкретной операции и связаны с ней. Это запрос, который сервер пошлёт клиенту **в ответ на ранее сделанный вызов** (например, клиент подписался через `POST /subscribe`, передав свой `callbackUrl`):

```yaml
paths:
  /subscribe:
    post:
      requestBody:
        content:
          application/json:
            schema:
              properties:
                callbackUrl: { type: string, format: uri }
      callbacks:
        onData:
          "{$request.body#/callbackUrl}":   # runtime-выражение
            post:
              requestBody:
                content:
                  application/json:
                    schema: { $ref: "#/components/schemas/Event" }
              responses:
                "200": { description: "Клиент принял событие" }
```

**`webhooks`** — секция верхнего уровня (появилась в OAS 3.1). Описывает входящие запросы, которые API шлёт, **не привязываясь ни к какой операции** — у webhook нет предшествующего вызова и нет `callbackUrl`. Это полноценные эндпоинты «наоборот»:

```yaml
webhooks:
  newOrder:
    post:
      requestBody:
        content:
          application/json:
            schema: { $ref: "#/components/schemas/Order" }
      responses:
        "200": { description: "Получатель обработал заказ" }
```

Главное отличие: `callbacks` контекстно зависят от операции и используют runtime-выражения для URL; `webhooks` глобальны и описывают паттерн «provider шлёт — consumer слушает» без привязки к запросу-предшественнику.

---

## Q35. (!) API-first workflow: практический пайплайн с openapi-generator

**API-first** — спецификация `openapi.yaml` является единственным источником правды и хранится в репозитории; код сервера и клиентов генерируется из неё.

Практический пайплайн:
1. **Дизайн** — пишем/правим `openapi.yaml`, ревьюим как обычный код в PR.
2. **Линтинг** — Spectral проверяет стиль и обязательные поля (`spectral lint`).
3. **Backward-compat** — `oasdiff`/`openapi-diff` сравнивает с предыдущей версией, ломающие изменения валят сборку.
4. **Генерация** — `openapi-generator` создаёт серверные интерфейсы и клиентские SDK на этапе `generate-sources`.
5. **Реализация** — разработчик имплементирует сгенерированные интерфейсы; компилятор гарантирует соответствие контракту.

Maven-конфигурация генерации на каждой сборке:
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
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useSpringBoot3>true</useSpringBoot3>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Преимущество: спецификация не «отстаёт» от кода (как в code-first при забытых аннотациях), а frontend и backend стартуют параллельно сразу после согласования контракта.

---

## Q36. Delegate pattern в openapi-generator: когда применять

При генерации Spring-сервера `openapi-generator` по умолчанию создаёт интерфейс `XxxApi` (с аннотациями `@RequestMapping`) — реализуя его напрямую, вы при **регенерации перезаписываете** свои контроллеры или ловите конфликты.

**Delegate pattern** (`delegatePattern=true`) разрывает эту связь: генератор создаёт три артефакта:
- `XxxApi` — интерфейс с дефолтными методами и аннотациями (генерируется, не трогаем).
- `XxxApiController` — тонкий контроллер, делегирующий в `XxxApiDelegate` (генерируется).
- `XxxApiDelegate` — интерфейс **без** Spring-аннотаций, который вы реализуете в своём бине.

```xml
<configOptions>
    <delegatePattern>true</delegatePattern>
</configOptions>
```

```java
@Service
public class UsersApiDelegateImpl implements UsersApiDelegate {
    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
```

**Когда применять:** при API-first с регулярной регенерацией — ваша бизнес-логика живёт в `*DelegateImpl` и не зависит от сгенерированных классов. Если генерация однократная (сгенерировали и забыли), `interfaceOnly=true` проще; delegate оправдан именно при повторяющейся генерации в пайплайне.

---

## Q37. Кастомизация Mustache-шаблонов openapi-generator

`openapi-generator` рендерит код по **Mustache-шаблонам**. Когда дефолтного вывода не хватает (свои аннотации, лицензионный заголовок, нестандартный базовый класс), шаблоны переопределяют, не форкая генератор.

Шаги:
1. Выгрузить эталонные шаблоны нужного генератора:
   ```bash
   openapi-generator-cli author template -g spring -o ./templates
   ```
2. Отредактировать нужный `.mustache` (например, `model.mustache`, `api.mustache`).
3. Указать каталог при генерации:
   ```bash
   openapi-generator-cli generate -i openapi.yaml -g spring \
     -o ./server -t ./templates
   ```

В Maven — параметр `templateDirectory`:
```xml
<configuration>
    <templateDirectory>${project.basedir}/src/main/templates</templateDirectory>
</configuration>
```

Доступны переменные модели (`{{classname}}`, `{{#vars}}…{{/vars}}`, `{{#operations}}`). Переопределять стоит **только нужные** файлы — остальные генератор берёт встроенные, поэтому при обновлении версии генератора расхождений меньше. Альтернатива точечным правкам — `--additional-properties` и vendor-extensions (`x-*`), если хватает их.

---

## Q38. (!) Backward compatibility: openapi-diff и oasdiff в CI

Изменение спецификации может **сломать существующих клиентов** (удаление поля, сужение типа, новое обязательное поле в запросе). Чтобы ловить это автоматически, в CI сравнивают новую спецификацию со старой.

**`openapi-diff`** (OpenAPITools) — классифицирует изменения на breaking/non-breaking:
```bash
docker run --rm -v $(pwd):/specs openapitools/openapi-diff \
  /specs/old-openapi.yaml /specs/new-openapi.yaml \
  --fail-on-incompatible
```

**`oasdiff`** — более современный инструмент с богатым набором правил и уровнями (`ERR`/`WARN`/`INFO`):
```bash
oasdiff breaking old-openapi.yaml new-openapi.yaml --fail-on ERR
```

Что считается ломающим: удаление операции или поля ответа, добавление `required`-поля в запрос, сужение `enum`, изменение типа, ужесточение `minLength`/`maximum`. Не ломающим — добавление опционального поля, новой операции, нового `2xx`-ответа.

В пайплайне «эталон» берут из main-ветки или из задеплоенной версии, а проверку ставят обязательным гейтом PR — так контракт не деградирует незаметно.

---

## Q39. Множественные примеры: examples vs example, переиспользуемые примеры

OpenAPI 3.x различает два поля для примеров значений:

- **`example`** (единственное число) — один пример прямо в `schema` или `media type`. Простой случай.
- **`examples`** (множественное) — карта **именованных** примеров; в Swagger UI появляется выпадающий список. Доступно на уровне `media type`, параметра, заголовка (но **не** внутри `schema`).

```yaml
paths:
  /users:
    post:
      requestBody:
        content:
          application/json:
            schema: { $ref: "#/components/schemas/CreateUser" }
            examples:
              valid:
                summary: "Корректный запрос"
                value: { name: "Иван", email: "ivan@example.com" }
              missingEmail:
                summary: "Без email"
                value: { name: "Иван" }
```

Переиспользуемые примеры выносят в `components/examples` и ссылаются через `$ref`:
```yaml
components:
  examples:
    UserSample:
      value: { id: 1, name: "Иван" }
```

В springdoc — аннотация `@ExampleObject` внутри `@Content(examples = {...})`. Несколько `@ExampleObject` с разными `name` дают тот же выпадающий список. `example` и `examples` взаимоисключающи в одном месте — указывать оба нельзя.

---

## Q40. @Hidden и скрытие операций из документации

Иногда эндпоинт существует, но не должен попадать в публичную спецификацию (служебный, внутренний, технический).

**`@Hidden`** (`io.swagger.v3.oas.annotations.Hidden`) скрывает контроллер, метод или поле целиком:
```java
@Hidden
@GetMapping("/internal/metrics")
public MetricsDto internalMetrics() { ... }
```

Альтернативы для частичного скрытия:
- `@Operation(hidden = true)` — скрыть отдельную операцию.
- `@Parameter(hidden = true)` — убрать параметр (например, `@AuthenticationPrincipal`).
- `@Schema(hidden = true)` — скрыть поле модели из схемы.

Скрыть пакеты/пути на уровне конфигурации:
```yaml
springdoc:
  packages-to-exclude: com.example.internal
  paths-to-exclude: /internal/**
```

Важно: `@Hidden` влияет **только на документацию** — сам эндпоинт остаётся доступным по HTTP. Для реального ограничения доступа нужен Spring Security, а не скрытие из Swagger UI.

---

## Q41. (!) Пагинация в OpenAPI: query параметры, cursor, Link header

Пагинацию описывают явно через параметры и/или заголовки ответа. Два основных стиля:

**1. Offset/limit (page-based):**
```yaml
parameters:
  - name: page
    in: query
    schema: { type: integer, default: 0, minimum: 0 }
  - name: size
    in: query
    schema: { type: integer, default: 20, maximum: 100 }
```
Ответ — обёртка с метаданными (`content`, `totalElements`, `totalPages`) — стиль Spring Data `Page`. Просто, но дорог `OFFSET` на больших таблицах и нестабилен при вставках.

**2. Cursor-based (keyset):**
```yaml
parameters:
  - name: cursor
    in: query
    description: "Непрозрачный курсор последней записи"
    schema: { type: string }
  - name: limit
    in: query
    schema: { type: integer, default: 20 }
```
Клиент передаёт `cursor` из предыдущего ответа; стабилен и быстр на больших данных, но нельзя прыгнуть на произвольную страницу.

**3. Link header (RFC 8288):**
```yaml
responses:
  "200":
    headers:
      Link:
        schema: { type: string }
        description: '<https://api/users?page=2>; rel="next"'
```
Навигация (`next`, `prev`, `first`, `last`) живёт в заголовке — стиль GitHub API. В OpenAPI описывается через `headers` ответа.

Выбор: offset — для небольших списков и админок; cursor — для лент и больших таблиц; Link — когда хочется RESTful-навигацию без обёртки в теле.

---

## Q42. multipart/form-data и загрузка файлов в OpenAPI

Загрузка файлов описывается через `requestBody` с `content-type` `multipart/form-data`; бинарное поле задаётся как `type: string, format: binary`.

```yaml
paths:
  /avatar:
    post:
      requestBody:
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary       # сам файл
                description:
                  type: string         # сопутствующее поле
            encoding:
              file:
                contentType: image/png, image/jpeg
      responses:
        "201": { description: "Файл загружен" }
```

Ключевые моменты:
- `format: binary` — одиночный файл; массив файлов — `type: array, items: {type: string, format: binary}`.
- Секция `encoding` уточняет `contentType` и заголовки для отдельных частей.
- В OAS 3.1 для бинарных данных предпочтителен `contentMediaType`/`contentEncoding` (JSON Schema 2020-12), но `format: binary` остаётся совместимым.

В springdoc на стороне Spring:
```java
@PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Void> upload(
    @RequestPart("file") MultipartFile file,
    @RequestParam(required = false) String description) { ... }
```

`springdoc-openapi` распознаёт `MultipartFile` и автоматически рендерит его как `format: binary` с кнопкой выбора файла в Swagger UI.

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
- [API Design Best Practices](api-design-best-practices-interview.md)
- [API Versioning](api-versioning-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
- [Шпаргалка: OpenAPI/Swagger](../../development/api/api-tools/swagger/openapi-swagger.md) — теория
