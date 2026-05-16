---
title: "Вопросы на собеседовании: API Design Best Practices"
description: "API design: naming conventions, resource modeling, pagination, filtering, sorting, error responses, idempotency, rate limiting, authentication, JSON conventions, OpenAPI"
tags:
  - interview
  - api
  - api-design-best-practices-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "API Design Best Practices"
  - "API design interview"
  - "API best practices interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `API Design Best Practices`

API design — критическое skill. Хороший API: **intuitive, consistent, evolvable, secure**. Плохой API — eternal pain. На интервью спрашивают: naming conventions, pagination, filtering, error format, idempotency, rate limiting, authentication. Коллекция best practices от Google, Microsoft, Stripe, GitHub.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Google API Design Guide](https://cloud.google.com/apis/design)
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)
- [Zalando RESTful API Guidelines](https://opensource.zalando.com/restful-api-guidelines/)
- [PayPal API Standards](https://github.com/paypal/api-standards)
- [JSON:API Specification](https://jsonapi.org/)
- [Adidas API Guidelines](https://adidas.gitbook.io/api-guidelines/)
- [The Design of Web APIs (book by Arnaud Lauret)](https://www.manning.com/books/the-design-of-web-apis)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Naming conventions**
- [Q1. (!) Naming resources (nouns, plural)?](#q1--naming-resources-nouns-plural)
- [Q2. (!) URL structure?](#q2--url-structure)
- [Q3. snake_case vs camelCase в JSON?](#q3-snake_case-vs-camelcase-в-json)
- [Q4. Date/time format?](#q4-datetime-format)

**Resource modeling**
- [Q5. (!) Resource hierarchy (parent/child)?](#q5--resource-hierarchy-parentchild)
- [Q6. Sub-resources vs flat structure?](#q6-sub-resources-vs-flat-structure)
- [Q7. (!) Bulk operations?](#q7--bulk-operations)
- [Q8. Custom actions (verbs)?](#q8-custom-actions-verbs)

**Pagination, filtering, sorting**
- [Q9. (!) Pagination strategies?](#q9--pagination-strategies)
- [Q10. (!) Cursor-based vs offset-based?](#q10--cursor-based-vs-offset-based)
- [Q11. (!) Filtering и sorting?](#q11--filtering-и-sorting)
- [Q12. Field selection (sparse fieldsets)?](#q12-field-selection-sparse-fieldsets)

**Error handling**
- [Q13. (!) Error response format?](#q13--error-response-format)
- [Q14. (!) Problem Details (RFC 7807)?](#q14--problem-details-rfc-7807)
- [Q15. Validation errors?](#q15-validation-errors)

**Idempotency**
- [Q16. (!) Idempotency keys?](#q16--idempotency-keys)
- [Q17. Optimistic concurrency (ETag)?](#q17-optimistic-concurrency-etag)

**Authentication**
- [Q18. (!) Auth strategies (API key, JWT, OAuth)?](#q18--auth-strategies-api-key-jwt-oauth)
- [Q19. API key best practices?](#q19-api-key-best-practices)

**Rate limiting**
- [Q20. (!) Rate limiting headers?](#q20--rate-limiting-headers)
- [Q21. Throttling vs hard limits?](#q21-throttling-vs-hard-limits)

**Documentation**
- [Q22. (!) OpenAPI / Swagger?](#q22--openapi--swagger)
- [Q23. Examples и SDKs?](#q23-examples-и-sdks)

**Security**
- [Q24. (!) HTTPS only?](#q24--https-only)
- [Q25. Input validation, output encoding?](#q25-input-validation-output-encoding)
- [Q26. CORS?](#q26-cors)

**Performance**
- [Q27. (!) Caching headers?](#q27--caching-headers)
- [Q28. Compression (gzip, brotli)?](#q28-compression-gzip-brotli)

**Other**
- [Q29. (!) HATEOAS — нужно?](#q29--hateoas--нужно)
- [Q30. Webhooks design?](#q30-webhooks-design)

## Q1. (!) Naming resources (nouns, plural)?

**Use nouns, plural**:

```
✓ /users
✓ /orders
✓ /products
✗ /getUsers       # verb
✗ /user           # singular
```

**Hierarchy:**
```
✓ /users/123/orders/456
```

**Avoid verbs в URLs** (use HTTP verbs instead):
```
✓ DELETE /users/123     # not POST /users/123/delete
✓ PUT /users/123        # not POST /users/123/update
```

**Exception:** **actions** не fitting CRUD (см. Q8).


> [!mcq]
> 
> **Вопрос:** Как правильно именовать ресурсы в REST API?
> 
> - [x] **A)** Plural nouns в URL (`/users`, `/orders`), действие выражается HTTP-методом (`GET /users/123`, `DELETE /users/123`), иерархия через слэши (`/users/123/orders/456`)
>   
>   **Развёрнутое объяснение:** REST трактует URL как идентификатор **ресурса** (существительное), а действие — как HTTP-метод (глагол). Plural-форма унифицирует коллекцию и элемент: `/users` (коллекция) и `/users/123` (один элемент той же коллекции). Это даёт consistent path-building на клиенте: `${base}/users/${id}` работает и для list, и для get-one.
>   
>   **Пример:**
>   ```
>   GET    /users           # список
>   POST   /users           # создать
>   GET    /users/123       # один
>   PUT    /users/123       # обновить
>   DELETE /users/123       # удалить
>   GET    /users/123/orders  # вложенная коллекция
>   ```
>   
>   **Когда применять:** любой REST/HTTP API, который описывает CRUD над доменными сущностями — пользователи, заказы, продукты, документы.
>   
>   **Подводные камни:** для не-CRUD действий (отправить письмо, перевыпустить токен) plural-noun плохо ложится — тогда используют sub-resource-as-action (`POST /users/123/password-resets`) или явный action-endpoint. Не злоупотреблять глубокой вложенностью (>2 уровней) — путь становится хрупким.
>   
>   **Связанные вопросы:** [[Q2]] (URL structure), [[Q5]] (resource hierarchy), [[Q8]] (non-CRUD actions)
> 
> - [ ] **B)** `GET /getUsers` — глагол в URL для ясности намерения
>   
>   **Что на самом деле:** действие в REST выражается HTTP-методом, а не словом в URL. `GET /getUsers` дублирует семантику `GET`, нарушает uniform interface и ломает routing/caching, которые опираются на «URL = ресурс».
>   
>   **Откуда путаница:** привычка из RPC/SOAP-стиля, где endpoint = имя удалённой процедуры. В REST эта модель заменена на «ресурс + метод».
>   
>   **Если бы это было правдой:** пришлось бы поддерживать `getUsers`, `listUsers`, `findUsers` как разные URL, caching по методу перестал бы работать, OpenAPI-генераторы создавали бы дублирующие операции.
> 
> - [ ] **C)** `/user/123` — singular form, потому что речь об одном пользователе
>   
>   **Что на самом деле:** правильно `/users/123` — элемент коллекции `/users`. Singular-форма разрывает связь между коллекцией и элементом и заставляет клиентов помнить две разные базы.
>   
>   **Откуда путаница:** грамматическая логика «один = singular» переносится с естественного языка на URL, но URL обозначает не объект, а **путь в коллекции**.
>   
>   **Если бы это было правдой:** SDK и path-helpers пришлось бы дублировать (`usersUrl()` и `userUrl(id)`), а вложенные ресурсы (`/user/123/orders` vs `/users/123/orders`) разъезжались бы по конвенции.
> 
> - [ ] **D)** `/Users/123` — uppercase первой буквы, как имя класса в коде
>   
>   **Что на самом деле:** стандарт — lowercase URLs (RFC 3986 + индустриальная практика Stripe/GitHub/Google). Часть инфраструктуры case-sensitive (Nginx по умолчанию), часть нет — это рождает разъезжающиеся редиректы и дубликаты в кэше/логах.
>   
>   **Откуда путаница:** перенос code style (PascalCase для классов) на URL-namespace.
>   
>   **Если бы это было правдой:** `/Users/123` и `/users/123` считались бы одним ресурсом — но CDN/прокси будут кэшировать их раздельно, а аналитика разойдётся по двум path-ам.

## Q2. (!) URL structure?

```
https://api.example.com/v1/users/123/orders?status=pending&limit=10
       └─────────┘└─┘└────────────────────┘└──────────────────────┘
       host       version  resource path     query string
```

**Best practices:**
- **API subdomain** (`api.example.com`)
- **Version в path** (`/v1/`)
- **Lowercase** URLs (`/users` not `/Users`)
- **Hyphens, not underscores** (`/order-items` not `/order_items`)
- **Plural nouns** для collections
- **Identifiers в path** (`/users/123`)
- **Filters в query string** (`?status=active`)


> [!mcq]
> 
> **Вопрос:** Какая структура URL для REST API корректна?
> 
> - [ ] **A)** `/users?version=1&status=active` — версия и фильтры одинаково передаются query-параметрами
>   
>   **Что на самом деле:** версия API относится к **routing/contract**, а не к фильтру выборки. Стандарт индустрии (Stripe, GitHub, Google Cloud) — version в path (`/v1/users`), потому что это позволяет API Gateway маршрутизировать на разные backend-ы, разделять контракты в OpenAPI и кэшировать ответы независимо.
>   
>   **Откуда путаница:** query string выглядит «универсальной свалкой» для любых необязательных параметров.
>   
>   **Если бы это было правдой:** один и тот же путь `/users` отвечал бы разными схемами в зависимости от `?version=`, а CDN кэшировал бы их в одну запись — клиенты v1 получали бы тело v2.
> 
> - [x] **B)** `https://api.example.com/v1/users/123/orders?status=pending&limit=10` — subdomain `api`, version в path `/v1`, resource path с идентификатором, фильтры в query string
>   
>   **Развёрнутое объяснение:** каждый компонент URL отвечает за свою роль. **`api.` subdomain** изолирует API от веб-сайта (отдельные cert, CORS, rate-limit). **`/v1/`** в path даёт явный контракт и удобный routing. **`/users/123/orders`** — иерархия ресурсов: путь однозначно идентифицирует, что именно мы запрашиваем. **`?status=pending&limit=10`** — фильтры, пагинация, сортировка: всё, что **не идентифицирует** ресурс, а уточняет выборку.
>   
>   **Пример:**
>   ```
>   GET https://api.example.com/v1/users/123/orders?status=pending&limit=10&sort=-created_at
>   ```
>   path = identity, query = projection/filter. Lowercase, hyphens для multi-word (`/order-items`), plural nouns.
>   
>   **Когда применять:** любой production REST API с явным versioning и filtering — public API, internal microservices с published contract.
>   
>   **Подводные камни:** не путать «фильтр» (query) с «sub-resource» (path) — `/users/123/orders` это коллекция заказов пользователя 123, а не фильтр. Если фильтров очень много (search) — рассмотреть `POST /users/search` с телом, чтобы не упереться в лимит длины URL (~2KB на прокси).
>   
>   **Связанные вопросы:** [[Q1]] (resource naming), [[Q4]] (date format), [[Q5]] (hierarchy)
> 
> - [ ] **C)** `/users/status/active` — фильтры выражаются как сегменты path для красоты URL
>   
>   **Что на самом деле:** path обозначает **идентичность ресурса**, query — **выбор подмножества**. `/users/status/active` ломает иерархию: непонятно, `status` это sub-resource или фильтр, и невозможно скомбинировать несколько фильтров (`status=active` + `country=ru`).
>   
>   **Откуда путаница:** SEO-friendly URLs на frontend (`/blog/category/tech`) переносятся на API.
>   
>   **Если бы это было правдой:** для двух фильтров пришлось бы изобретать `/users/status/active/country/ru`, и порядок сегментов стал бы значимым — две эквивалентные комбинации воспринимались бы как разные ресурсы.
> 
> - [ ] **D)** `/users_123_orders` — underscore-separated path для краткости
>   
>   **Что на самом деле:** RFC 3986 определяет `/` как разделитель иерархии — именно так роутеры, прокси и OpenAPI строят tree of resources. Underscore внутри сегмента допустим (хотя hyphen предпочтительнее для multi-word), но **между уровнями иерархии** нужен слэш.
>   
>   **Откуда путаница:** перенос snake_case-конвенции из имён переменных на структуру URL.
>   
>   **Если бы это было правдой:** path-matching на API Gateway/Spring `@PathVariable` сломался бы — нельзя извлечь `userId` и `orderId` без custom-парсера; OpenAPI не смог бы описать ресурсы древовидно.

## Q3. snake_case vs camelCase в JSON?

**Choose ONE convention, stick to it.**

**snake_case** (более common):
```json
{"first_name": "Alice", "created_at": "2025-04-19T10:00:00Z"}
```

**camelCase:**
```json
{"firstName": "Alice", "createdAt": "2025-04-19T10:00:00Z"}
```

**Used by:**
- snake_case: Stripe, Slack, Twitter, Python ecosystem
- camelCase: Google, Microsoft, JavaScript ecosystem

**В Java backend** — внутри `firstName` (camelCase), serialize с Jackson `@JsonProperty("first_name")` если API uses snake.


> [!mcq]
> 
> **Вопрос:** Как выбрать конвенцию именования JSON-полей в REST API?
> 
> - [ ] **A)** Смешивать snake_case и camelCase внутри одного response в зависимости от контекста поля (legacy vs new)
>   
>   **Что на самом деле:** клиентские SDK десериализуют JSON в типизированные структуры по **одному** правилу маппинга (`@JsonNaming(SnakeCaseStrategy.class)` в Java, `JSONDecoder.keyDecodingStrategy` в Swift). Смешение конвенций ломает дефолтный маппинг — придётся для каждого поля писать `@JsonProperty`.
>   
>   **Откуда путаница:** «временное решение» при миграции legacy → new часто превращается в постоянное.
>   
>   **Если бы это было правдой:** OpenAPI-генераторы создавали бы клиентов с разными конвенциями для разных полей одной модели — type-safe код становится невозможным без ручных переопределений.
> 
> - [ ] **B)** Разные конвенции для разных endpoints (`/v1/users` → snake_case, `/v1/products` → camelCase) — пусть каждая команда решает сама
>   
>   **Что на самом деле:** API — это **единый контракт**, и DX (developer experience) страдает в первую очередь от inconsistency. SDK-генераторы (OpenAPI Generator, NSwag) применяют конвенцию глобально; mixed-style ломает type safety на уровне всего клиента.
>   
>   **Откуда путаница:** микросервисная архитектура с отдельными командами выглядит как оправдание; на деле public-контракт нужно унифицировать на уровне API Gateway или style guide.
>   
>   **Если бы это было правдой:** на каждый вызов разработчику пришлось бы заглядывать в Swagger — DX скатывается к уровню недокументированного RPC.
> 
> - [x] **C)** Выбрать ОДНУ конвенцию (snake_case ИЛИ camelCase) и применять её во всём API; в Java — внутри классы в camelCase, наружу через `@JsonNaming` или `spring.jackson.property-naming-strategy`
>   
>   **Развёрнутое объяснение:** consistency > preference. Обе конвенции работают; что выбрать — определяется экосистемой клиентов. **snake_case**: Python/Ruby/Rails-клиенты, Stripe, Slack, Twitter API. **camelCase**: JavaScript-первые SDK, Google Cloud, Microsoft Graph. Внутри Java-кода поля всегда `camelCase` (PEP-конвенция языка), а сериализация настраивается централизованно одной аннотацией/проперти.
>   
>   **Пример:**
>   ```java
>   // application.yml
>   spring.jackson.property-naming-strategy: SNAKE_CASE
>   
>   // DTO
>   public record UserDto(String firstName, Instant createdAt) {}
>   
>   // JSON output
>   {"first_name": "Alice", "created_at": "2026-05-15T10:00:00Z"}
>   ```
>   
>   **Когда применять:** любой публичный/межсервисный REST API. Если клиенты — преимущественно JS/мобильные TypeScript-приложения, выбирай camelCase; если Python/Ruby/data-engineering — snake_case.
>   
>   **Подводные камни:** одна конвенция должна быть **enforced** — добавь ArchUnit/Checkstyle-правило или Spotless-форматтер, иначе через год в API наберётся «исключений». Конвенция в URL (`/order-items` — hyphens) и в JSON (`order_items` — underscores) **разные** и это нормально: это разные слои, у них разные RFC.
>   
>   **Связанные вопросы:** [[Q1]] (URL naming), [[Q4]] (date format), [[Q22]] (versioning strategy)
> 
> - [ ] **D)** Отказаться от любых конвенций — каждый разработчик выбирает имя поля сам по ситуации
>   
>   **Что на самом деле:** отсутствие конвенции = технический долг с первого дня. Code review будет тратить время на bikeshedding `firstName` vs `first_name` vs `FirstName` в каждом PR, и API превратится в лоскутное одеяло без shared mental model.
>   
>   **Откуда путаница:** «свобода = производительность» — но в командной разработке свобода без guard-rails означает несогласованность.
>   
>   **Если бы это было правдой:** автогенерация SDK и type-safe-клиентов стала бы невозможной; OpenAPI как контракт перестал бы выполнять свою функцию.

## Q4. Date/time format?

**ISO 8601** — international standard:

```json
"created_at": "2025-04-19T14:30:00Z"           // UTC
"created_at": "2025-04-19T14:30:00+02:00"       // with timezone
```

**Avoid:**
- Unix timestamps (use ISO для humans)
- Custom formats ("April 19, 2025")
- Local times без timezone

**В Java:** `Instant`, `OffsetDateTime`, `ZonedDateTime` (NOT `Date`).


> [!mcq]
> 
> **Вопрос:** Какой формат date/time использовать в REST API JSON?
> 
> - [ ] **A)** Unix timestamp (`"created_at": 1745068800`) — число короче и парсится быстрее
>   
>   **Что на самом деле:** Unix timestamp — числовая дельта от 1970 UTC, нечитаемая для людей. При debugging логов/JSON нужен конвертер, чтобы понять, когда именно произошло событие. Хуже того, **разные платформы используют разные единицы** — JS `new Date(timestamp)` ожидает миллисекунды, Linux/Java `Instant.ofEpochSecond` — секунды; ошибка масштаба даёт off-by-1000 bug.
>   
>   **Откуда путаница:** в БД и логах timestamps часто хранятся как `bigint` ради компактности, и это переносят в API contract.
>   
>   **Если бы это было правдой:** OpenAPI-схема показывала бы `type: integer`, и валидатор не отличил бы `created_at` от `user_id`; клиенты на разных языках интерпретировали бы единицу по-разному.
> 
> - [ ] **B)** Custom human-readable формат: `"April 19, 2025 14:30"`
>   
>   **Что на самом деле:** этот формат **не стандартизован** и зависит от locale (`"19 avril 2025"` для FR, `"19 апреля 2025"` для RU). Парсинг требует i18n-aware парсера, а сравнение двух дат становится строковой операцией без гарантий. OpenAPI-генераторы не валидируют такие поля как `date-time` и не маппят на `Instant`/`OffsetDateTime`.
>   
>   **Откуда путаница:** API «для людей» путают с UI: представление для пользователя — задача frontend, контракт API должен быть машинно-читаемым.
>   
>   **Если бы это было правдой:** один и тот же endpoint возвращал бы разные строки в зависимости от `Accept-Language`, и сервер с клиентом разъезжались бы в timezone.
> 
> - [ ] **C)** Local time без timezone: `"2025-04-19T14:30:00"` — клиент сам знает свой часовой пояс
>   
>   **Что на самом деле:** без timezone строка **неоднозначна** — `14:30` это UTC, MSK, NY или Tokyo? Между микросервисами в разных регионах это сразу даёт расхождение «событие из будущего». ISO 8601 без суффикса `Z` или `±HH:MM` формально допустим, но интерпретация остаётся на клиенте.
>   
>   **Откуда путаница:** legacy-системы возвращают local time в формате БД (`TIMESTAMP WITHOUT TIME ZONE`), и это просачивается в API.
>   
>   **Если бы это было правдой:** distributed system с серверами в разных регионах строила бы timeline событий с разбросом ±12 часов; alerts по `created_at > now() - 1h` ловили бы фантомные/пропавшие события.
> 
> - [x] **D)** ISO 8601 с timezone: `"2025-04-19T14:30:00Z"` (UTC) или `"2025-04-19T14:30:00+02:00"`; в Java — `Instant`/`OffsetDateTime`, не `Date`
>   
>   **Развёрнутое объяснение:** ISO 8601 — международный стандарт, явно описывающий дату, время и **timezone offset**. Это даёт три свойства одновременно: **машинно-читаемо** (один формат на все языки), **однозначно** (timezone в самой строке), **сортируемо лексикографически** (`"2025-04-19" < "2025-04-20"`). OpenAPI описывает такие поля как `type: string, format: date-time`, и генераторы автоматически маппят их на `Instant`/`OffsetDateTime` в Java, `Date`/`DateTime` в TS, `datetime` в Python.
>   
>   **Пример:**
>   ```json
>   {
>     "created_at": "2025-04-19T14:30:00Z",
>     "scheduled_for": "2025-04-19T14:30:00+02:00"
>   }
>   ```
>   В Java:
>   ```java
>   public record OrderDto(
>       Instant createdAt,         // → "2025-04-19T14:30:00Z"
>       OffsetDateTime scheduledFor // → "2025-04-19T14:30:00+02:00"
>   ) {}
>   ```
>   Jackson сериализует `Instant`/`OffsetDateTime` в ISO 8601 по умолчанию (с `jackson-datatype-jsr310`).
>   
>   **Когда применять:** все date/time поля в любом REST/GraphQL API. UTC (`Z`) — для timestamps событий (created_at, updated_at). Offset (`+02:00`) — когда важна локальная семантика (расписание в магазине, бронирование рейса).
>   
>   **Подводные камни:** **никогда** не использовать `java.util.Date` — он mutable и хранит UTC internally, но `toString()` показывает local time, что путает в логах. Для дат без времени (день рождения) — `LocalDate` и `format: date` (`"2025-04-19"`). Не путать `Instant` (UTC) с `LocalDateTime` (без timezone) — последний не годится для API.
>   
>   **Связанные вопросы:** [[Q3]] (JSON naming conventions), [[Q2]] (URL structure)

## Q5. (!) Resource hierarchy (parent/child)?

**Hierarchy via URL:**
```
/users/123/orders          # all orders для user 123
/users/123/orders/456      # specific order
/orders/456                # also valid (independent access)
```

**Pros nested:**
- Clear ownership
- Scoping built-in

**Cons nested:**
- Deep nesting confusing (`/users/123/orders/456/items/789/refunds/...`)
- Limited to two levels usually

**Best practice:** **2 levels max** в nested URLs. Beyond — flat structure с filters.


> [!mcq]
> 
> **Вопрос:** Какое правило выбрать для resource hierarchy (parent/child) в URL?
> 
> - [x] **A)** Max 2 уровня вложенности: `/users/123/orders` и `/users/123/orders/456`; глубже — flat structure с filter params
>   
>   **Развёрнутое объяснение:** 2-level rule — это компромисс между **выразительностью ownership** и **удобством работы с URL**. Два уровня позволяют ясно выразить parent-child relationship (`/users/{userId}/orders` — заказы конкретного пользователя), и при этом URL остаётся читаемым в логах, документации и client SDK. Глубже nesting даёт `/users/123/orders/456/items/789/refunds/1` — методы SDK получают 4-5 path-параметров, swagger-клиенты генерируют монструозные сигнатуры, а frontend конкатенирует строки руками. Beyond 2 levels — переход на flat с query (`/refunds?order_id=456`).
>   
>   **Пример:**
>   ```
>   GET  /users/123/orders                # OK — 2 levels, orders of user
>   GET  /users/123/orders/456            # OK — specific order
>   GET  /refunds?order_id=456            # NOT /users/123/orders/456/refunds/1 — flat с filter
>   ```
>   В Spring:
>   ```java
>   @GetMapping("/users/{userId}/orders/{orderId}")
>   public OrderDto getOrder(@PathVariable Long userId, @PathVariable Long orderId) { ... }
>   ```
>   Stripe следует тому же правилу: `/customers/{id}/sources` (2 levels), но refunds — flat (`/refunds`).
>   
>   **Когда применять:** **strong containment** (item существует только в контексте parent) — nesting; **independent entity** (refund имеет смысл сам по себе) — flat. Auth/scoping тоже выигрывает: API Gateway легко проверяет, что `{userId}` в path совпадает с `sub` в JWT.
>   
>   **Подводные камни:** даже при nested URL стоит дублировать flat-вариант для случаев, когда parent неизвестен (`GET /orders/{id}` без userId — полезно для admin/support). Stripe именно так и делает: `/customers/cus_123/charges` **и** `/charges?customer=cus_123` сосуществуют.
>   
>   **Связанные вопросы:** [[Q6]] (sub-resources vs flat), [[Q2]] (URL structure)
> 
> - [ ] **B)** Всегда делать 5+ уровней вложенности для максимально точного отражения иерархии данных
>   
>   **Что на самом деле:** глубокая вложенность ломает практическое использование API. URL `/users/123/orders/456/items/789/refunds/1` — нечитаем в логах, занимает много места в `access.log`, и SDK-генератор создаёт метод `getUserOrderItemRefund(userId, orderId, itemId, refundId)` с 4 path-параметрами, которые легко перепутать местами.
>   
>   **Откуда путаница:** ER-модель в БД переносится 1-в-1 в URL — кажется, что «правильно» отразить все foreign keys.
>   
>   **Если бы это было правдой:** каждое изменение в иерархии БД ломало бы public API; refactoring «вынести refunds в отдельный сервис» требовал бы breaking change для всех клиентов.
> 
> - [ ] **C)** Всегда flat: `/orders?user_id=123`, без иерархии в принципе
>   
>   **Что на самом деле:** теряется семантика **ownership** и **scoping**. API Gateway не может проверить, что пользователь имеет доступ к ресурсу, без парсинга query string (`?user_id=`) — это требует policy на уровне приложения, а не на уровне routing. Auth middleware сложнее писать и тестировать.
>   
>   **Откуда путаница:** GraphQL/REST-flat школа считает, что URL должен быть «плоским пространством имён ресурсов», а связи выражаются через filters.
>   
>   **Если бы это было правдой:** rate-limit per user (типа `/users/{userId}/*`) на API Gateway не реализуется без custom-plugin; logging/audit теряет parent context.
> 
> - [ ] **D)** Вложенность без ограничений — каждая команда определяет глубину по своим бизнес-объектам
>   
>   **Что на самом деле:** отсутствие конвенции = каждый endpoint уникален. Clients строят URL конкатенацией (template strings вместо SDK), нет единого стандарта в OpenAPI, и API Gateway/portal не может генерировать docs автоматически.
>   
>   **Откуда путаница:** «у нас микросервисы, каждый сам решает» — но API consumer один, и ему нужна предсказуемость.
>   
>   **Если бы это было правдой:** developer portal показывал бы 200 endpoints с разной структурой; onboarding нового клиента занимал бы недели вместо часов.

## Q6. Sub-resources vs flat structure?

**Sub-resources** (nested):
```
GET /users/123/orders     # orders for this user
POST /users/123/orders    # create order для user
```

**Flat:**
```
GET /orders?user_id=123
POST /orders {"user_id": 123, ...}
```

**Choice:**
- **Strong containment** (item belongs к user) → nested
- **Independent** entities → flat

**Both can co-exist.** Stripe uses both: `/customers/cus_123/charges` и `/charges?customer=cus_123`.


> [!mcq]
> 
> **Вопрос:** Когда выбирать sub-resources (nested) vs flat structure для API?
> 
> - [ ] **A)** Всегда использовать только nested URLs — это единственно правильный REST-стиль
>   
>   **Что на самом деле:** REST не требует nesting — он требует **resource-oriented** design. Многие ресурсы существуют независимо (charge, refund, invoice), и forced nesting создаёт искусственную иерархию. Например, `/users/123/orders/456` подразумевает, что order **существует только** в контексте user — но в реальности admin/support хочет открыть order по id без знания userId.
>   
>   **Откуда путаница:** учебники по REST часто показывают только nested примеры (`/posts/{id}/comments`), и это запоминается как «правильный REST».
>   
>   **Если бы это было правдой:** Stripe не предоставлял бы `/charges/{id}` отдельно от `/customers/{id}/charges`; admin-tools было бы невозможно построить без знания всей parent-chain.
> 
> - [x] **B)** Выбор по семантике: **strong containment** → nested, **independent entity** → flat; оба варианта могут co-exist
>   
>   **Развёрнутое объяснение:** правило простое: если ресурс **не существует без parent** (комментарий без поста, address без user) — nested. Если ресурс **имеет независимую identity** и используется в разных контекстах (charge, payment, order) — flat. И **ничто не мешает поддерживать оба варианта** одновременно: Stripe именно так и делает — `/customers/cus_123/charges` для list-по-customer и `/charges?customer=cus_123` для общего search/filter. Это даёт SDK удобные scoped-методы (`stripe.customers.charges.list(customerId)`) и одновременно flat-access для admin-tools.
>   
>   **Пример:**
>   ```
>   # Strong containment — nested:
>   GET  /users/123/addresses
>   POST /users/123/addresses          # address принадлежит user
>   
>   # Independent — flat:
>   GET  /orders?customer_id=123
>   POST /orders {"customer_id": 123}  # order — independent entity
>   
>   # Co-exist (Stripe pattern):
>   GET  /customers/cus_123/charges    # SDK convenience
>   GET  /charges?customer=cus_123     # general search
>   GET  /charges/ch_456               # direct access by ID
>   ```
>   В Spring:
>   ```java
>   @GetMapping("/users/{userId}/addresses")  // nested — strong containment
>   public List<AddressDto> listAddresses(@PathVariable Long userId) { ... }
>   
>   @GetMapping("/orders")                    // flat — independent
>   public List<OrderDto> listOrders(@RequestParam(required = false) Long customerId) { ... }
>   ```
>   
>   **Когда применять:** проверить вопрос «Имеет ли ресурс смысл без знания parent?». Если **нет** — nested (`/posts/{id}/comments`). Если **да** — flat (`/charges?customer=cus_123`). Для крупных API стоит сразу проектировать оба варианта при сильной containment, чтобы admin/integration scenarios не упирались в parent-only URLs.
>   
>   **Подводные камни:** дублирование endpoints стоит maintenance — но окупается удобством. Главный риск: разъезжающаяся валидация (`POST /users/123/orders` валидирует, что user существует; `POST /orders {"user_id": 123}` забывает). Решение: общий service-layer вызывается из обоих controllers, валидация — в нём.
>   
>   **Связанные вопросы:** [[Q5]] (resource hierarchy), [[Q7]] (bulk operations)
> 
> - [ ] **C)** Решать по производительности БД — что быстрее JOIN-ится, то и nested
>   
>   **Что на самом деле:** URL design определяется **API contract**, а не схемой БД. Behind the same URL может стоять JOIN, отдельный сервис, кэш или GraphQL-федерация — это деталь реализации. Если URL зависит от внутреннего storage, любая миграция БД (sharding, split table) ломает public API.
>   
>   **Откуда путаница:** в early-stage проектах API часто пишется как «обёртка над БД», и URL отражает структуру таблиц.
>   
>   **Если бы это было правдой:** разделение orders в отдельный сервис заставило бы менять `/users/{id}/orders` → `/orders?user_id={id}` — breaking change для всех клиентов из-за внутренней рефакторизации.
> 
> - [ ] **D)** Использовать только flat URLs, чтобы любой ресурс был доступен по одному и тому же шаблону
>   
>   **Что на самом деле:** flat-only теряет выразительность scoping в URL. Authorization-проверка «у user 123 есть доступ к order 456» становится сложнее: нужно достать order из БД, прочитать `customer_id`, сравнить с JWT — вместо простого matching `{userId}` из path с `sub` claim. Это antipattern для API Gateway-based auth.
>   
>   **Откуда путаница:** GraphQL-mindset переносится на REST: «единое flat-namespace ресурсов».
>   
>   **Если бы это было правдой:** policy-as-code в API Gateway (типа `path("/users/${user.id}/*")` в OPA/Cedar) не работала бы; вся authz переезжала бы в приложение и дублировалась между сервисами.

## Q7. (!) Bulk operations?

**Need:** create / update / delete multiple resources в one call.

**Approaches:**

**1. Send array:**
```http
POST /users/batch
[
  {"name": "Alice"},
  {"name": "Bob"}
]
```

**2. Wrap в object:**
```http
POST /users
{"users": [{"name": "Alice"}, ...]}
```

**3. Sub-resource:**
```http
POST /users/bulk-create
{"users": [...]}
```

**Considerations:**
- **Atomicity** — all-or-nothing? или partial success?
- **Response format** — array of results, errors per item?
- **Limits** — max items per request

**JSON:API spec** имеет conventions для bulk.


> [!mcq] Как корректно спроектировать bulk-операции в REST API?
>
> - [ ] A) Отправлять каждый объект отдельным запросом POST /users в цикле — это и есть «bulk».
>   - Почему неверно: пропадает атомарность и контроль над общим лимитом, клиент платит за N round-trip'ов и rate-limit вместо одного.
>   - Последствие: при загрузке 10k записей API упирается в rate limit и сетевой latency, операция занимает минуты вместо секунд.
>
> - [ ] B) Создать один endpoint POST /bulk который принимает JSON с типом ресурса и массивом — universal bulk для всего API.
>   - Почему неверно: «универсальный» bulk превращается в RPC-стиль, теряет ресурсную семантику REST и плохо валидируется.
>   - Последствие: невозможно описать в OpenAPI без `oneOf`-кашицы, клиенты и middleware (authz, audit) не понимают что именно создаётся.
>
> - [x] C) Выделить bulk-endpoint на ресурс (POST /users/batch или POST /users c {"users": [...]}) с явной политикой атомарности и форматом ответа per-item.
>   - Почему верно: сохраняет ресурсную модель REST, явно описывает контракт all-or-nothing vs partial success и формат массива результатов/ошибок.
>   - Когда применять: массовый импорт, batch-update в админке, синхронизация каталога — там, где N>1 объектов того же типа.
>   - Пример: `POST /users/batch` принимает массив, отвечает `{"results":[{"status":"ok","id":...},{"status":"error","reason":"duplicate"}]}` + общий 207 Multi-Status.
>   - Сравнение: JSON:API имеет conventions именно для bulk, не для произвольного RPC.
>
> - [ ] D) Использовать GET с массивом ID в query string (?ids=1,2,3,...) для bulk create — экономит сетевой трафик.
>   - Почему неверно: GET идемпотентен и кэшируем, не должен создавать ресурсы; URL ограничен по длине (~2-8KB).
>   - Последствие: при bulk на 1000 объектов запрос обрезается прокси, часть данных молча теряется без ошибки.

## Q8. Custom actions (verbs)?

Иногда action не CRUD (e.g., "publish article", "archive order").

**Options:**

**1. Sub-resource:**
```
POST /articles/123/publish
POST /orders/456/cancel
```

**2. State change через PATCH:**
```
PATCH /orders/456
{"status": "cancelled"}
```

**3. Action endpoint:**
```
POST /actions/cancel-order
{"order_id": 456}
```

**Best practice:** **sub-resource** часто naturally fit. Limit к few cases (don't go RPC-style).


> [!mcq] Как лучше всего моделировать non-CRUD action типа «publish article» в REST API?
>
> - [ ] A) Завести универсальный endpoint POST /actions с body {"type":"publish","article_id":123} — единая точка для всех действий.
>   - Почему неверно: это чистый RPC, ресурсная модель REST полностью теряется, URL не отражает что меняется.
>   - Последствие: невозможно навесить ресурсо-уровневую authz (`/articles/123/*`), нет URL-based кэширования и audit'а.
>
> - [ ] B) Расширить HTTP методами: PUBLISH /articles/123 — кастомный verb более выразителен чем POST.
>   - Почему неверно: кастомные HTTP методы не поддерживаются клиентами, прокси, CDN и API gateway'ями.
>   - Последствие: запросы блокируются на CloudFront/nginx с 405 Method Not Allowed, отладка занимает часы.
>
> - [ ] C) Только PATCH /articles/123 с {"status":"published"} — state machine через состояние ресурса единственно правильный путь.
>   - Почему неверно: подходит для простой смены поля, но скрывает side-effects (отправка нотификаций, индексация) за неявной семантикой PATCH.
>   - Последствие: клиент может случайно «опубликовать» статью передав status в общем PATCH-запросе с другими полями.
>
> - [x] D) Использовать sub-resource POST /articles/123/publish для action с побочными эффектами; PATCH — для простой смены состояния.
>   - Почему верно: sub-resource естественно выражает action как ресурс («publication» статьи), POST подчёркивает not-idempotent side-effects.
>   - Когда применять: publish/archive/cancel/approve — действия с workflow, нотификациями, аудитом, генерацией дочерних сущностей.
>   - Пример: `POST /orders/456/cancel` создаёт cancellation event, возвращает обновлённый order; `POST /articles/123/publish` триггерит indexation pipeline.
>   - Граница: ограничивать few cases — иначе API скатывается в RPC-стиль с десятками verb-endpoint'ов.

## Q9. (!) Pagination strategies?

**Always paginate** collection endpoints (don't return 1 million items).

**Approaches:**

**Offset/limit:**
```
GET /users?offset=20&limit=10
```

**Page/per_page:**
```
GET /users?page=3&per_page=10
```

**Cursor-based:**
```
GET /users?cursor=abc123&limit=10
```

**Time-based:**
```
GET /events?since=2025-04-19T10:00:00Z&limit=100
```

**Default limit** — 20-100 typical. Max — 100-1000.


> [!mcq] Какой подход к pagination коллекционных endpoint'ов корректен на старте проектирования API?
>
> - [x] A) Всегда пагинировать коллекции (default limit 20-100, max 100-1000), выбрав стратегию (offset/page/cursor/time) под характер данных.
>   - Почему верно: возврат всей коллекции деградирует latency и memory под нагрузкой; default + max лимиты защищают backend от случайных «вернуть всё».
>   - Когда применять: любой `GET /<collection>` независимо от ожидаемого размера — данные растут, и эндпоинт без пагинации становится миной замедленного действия.
>   - Пример: `GET /users?limit=20&cursor=abc123` — cursor-based для high-traffic; `GET /users?page=3&per_page=10` — page-based для админок с jumping.
>   - Стратегии: offset/limit (простота), page/per_page (UI), cursor (большие/реалтайм датасеты), time-based (event feed по дате).
>
> - [ ] B) Пагинировать только если коллекция превышает 10k строк — для маленьких таблиц это overhead.
>   - Почему неверно: «маленькие сегодня» вырастают через год, и менять контракт API после релиза с breaking change'ом — дорого.
>   - Последствие: через год прод падает на endpoint'е который раньше возвращал 50 записей, а сейчас 500k — клиенты не готовы к пагинации.
>
> - [ ] C) Использовать только offset/limit как единый стандарт — это самый понятный подход для всех команд.
>   - Почему неверно: offset деградирует на больших датасетах (`OFFSET 1000000` сканирует всю таблицу) и даёт inconsistent результат при конкурентных вставках.
>   - Последствие: на коллекции в 10M строк p99 latency растёт до секунд, клиенты получают дубликаты/пропуски при скролле.
>
> - [ ] D) Возвращать всю коллекцию, но добавить query param `?limit=100` чтобы клиент сам ограничивал — backend остаётся «прозрачным».
>   - Почему неверно: default без limit заставит backend готовить полный response, лимит на клиенте не спасает сервер от OOM/timeout.
>   - Последствие: один клиент без `?limit` укладывает базу и память JVM, остальные клиенты получают 503.

## Q10. (!) Cursor-based vs offset-based?

**Offset-based** (`?offset=20&limit=10`):
- ✅ **Easy to understand**
- ✅ Skip к page 5 directly
- ❌ **Slow на больших datasets** (`OFFSET 1000000` = scan all rows)
- ❌ **Inconsistent** if data changes (skipped/duplicate items)

**Cursor-based** (`?cursor=abc&limit=10`):
- ✅ **Fast** (uses indexed cursor)
- ✅ **Consistent** (no skip/dup)
- ❌ Cannot jump к arbitrary page
- ❌ More complex implementation

**Use cursor для:**
- Large datasets
- Real-time feeds
- High-traffic APIs

**Use offset для:**
- Small datasets
- Admin UIs requiring page jumping

**Modern APIs** (Stripe, Shopify, GraphQL Relay) — cursor-based.


> [!mcq]
> 
> **Вопрос:** Когда выбирать cursor-based pagination вместо offset-based для REST API?
> 
> - [ ] **A)** Всегда использовать offset-based — это проще и поддерживает прыжки на любую страницу
>   
>   **Что на самом деле:** `OFFSET N` в SQL заставляет БД **сканировать и отбрасывать** первые N строк перед возвратом результата. На миллионах строк `OFFSET 1000000 LIMIT 10` превращается в seq-scan ценой секунд CPU и read-IOPS. Дополнительно — при изменении данных между запросами клиент видит **дубликаты или пропуски** (новая запись смещает все offset-ы).
>   
>   **Откуда путаница:** на маленьких датасетах (до 10k строк) offset действительно работает быстро и удобно для admin-UI с произвольной навигацией.
>   
>   **Если бы это было правдой:** Stripe, Shopify, Twitter, GitHub API не переходили бы на cursor-based — а они все перешли именно из-за деградации offset на проде.
> 
> - [x] **B)** Cursor-based для большиx/растущих датасетов и real-time feeds; offset-based — для маленьких таблиц и admin-интерфейсов с прыжками по страницам
>   
>   **Развёрнутое объяснение:** cursor — это **opaque-указатель** на «последнюю отданную строку» (обычно `(sort_key, id)` в base64), и следующая страница достаётся через `WHERE (created_at, id) > (cursor_ts, cursor_id) ORDER BY created_at, id LIMIT N` — это **index range scan**, O(log N) независимо от глубины пагинации. Offset даёт O(offset) сканирование. Cursor также **стабилен** к вставкам/удалениям между запросами.
>   
>   **Пример:**
>   ```http
>   GET /events?cursor=eyJ0cyI6IjIwMjYtMDUtMTUiLCJpZCI6OTl9&limit=20
>   
>   200 OK
>   {
>     "data": [...],
>     "next_cursor": "eyJ0cyI6IjIwMjYtMDUtMTUiLCJpZCI6MTE5fQ==",
>     "has_more": true
>   }
>   ```
>   
>   **Когда применять:** event feeds, activity logs, infinite scroll, API с >100k строк в коллекции, любые public API где клиент не контролирует размер данных. Offset оставляй для внутренних admin-панелей с jump-to-page-42.
>   
>   **Подводные камни:** cursor должен быть **opaque** для клиента (не парсить, не модифицировать) — иначе ломается обратная совместимость при смене схемы. Если sort-ключ не уникален, добавляй tie-breaker (`(created_at, id)`). Cursor нельзя «отмотать назад» в чистом виде — для bidirectional нужно отдавать `prev_cursor` отдельно.
>   
>   **Связанные вопросы:** [[Q9]] (pagination strategies), [[Q11]] (filtering и sorting), [[Q16]] (idempotency)
> 
> - [ ] **C)** Использовать только page-based (`?page=5&size=10`) — это компромисс между offset и cursor
>   
>   **Что на самом деле:** page-based — это **синтаксический сахар над offset** (`offset = (page-1) * size`). Внутри БД выполняется тот же `OFFSET`, со всеми его проблемами производительности и нестабильности. Это не отдельная стратегия, а offset с другим API.
>   
>   **Откуда путаница:** Spring Data `Pageable` оперирует page/size и автоматически конвертирует в offset — выглядит как третий вариант, но физически это offset.
>   
>   **Если бы это было правдой:** Stripe оставался бы на `?page=N`, но они явно документируют переход на cursor-based из-за scale-проблем page-based.
> 
> - [ ] **D)** Cursor-based решает все проблемы и должен использоваться везде, включая admin-UI
>   
>   **Что на самом деле:** cursor **не поддерживает прыжки на произвольную страницу** — клиент обязан идти последовательно. Для admin-интерфейса с «Перейти на стр. 42» это unacceptable UX. Также cursor сложнее для total-count (`?include_total=true` требует отдельного запроса).
>   
>   **Откуда путаница:** «cursor быстрее → значит лучше всегда» — игнорирует UX-требования к page jumping.
>   
>   **Если бы это было правдой:** Jira, Confluence и любые админки не использовали бы page-based — а они используют, потому что юзеру нужно прыгать.

## Q11. (!) Filtering и sorting?

**Filtering:**
```
GET /users?status=active
GET /users?created_after=2025-01-01
GET /users?role=admin&country=US
```

**Sorting:**
```
GET /users?sort=created_at         # ascending
GET /users?sort=-created_at         # descending (- prefix)
GET /users?sort=name,-created_at   # multiple fields
```

**Advanced filtering** (RSQL, OData):
```
GET /users?filter=age=gt=18;status==active
```

**Best practice:**
- Simple filters via query params
- Complex filters → POST /search endpoint с JSON body


> [!mcq]
> 
> **Вопрос:** Как правильно реализовать filtering и sorting в REST API?
> 
> - [ ] **A)** Все фильтры (включая сложные boolean-выражения с AND/OR/NOT) передавать через path-параметры: `/users/status/active/role/admin/age-gte/18`
>   
>   **Что на самом деле:** path-сегменты — это **иерархия ресурсов**, а не фильтры. Кодирование атрибутов в path ломает routing-табли (каждая комбинация = отдельный route), невозможно описать в OpenAPI как «коллекция с фильтрами», и нарушает RFC 3986 (path = hierarchy, query = non-hierarchical data).
>   
>   **Откуда путаница:** clean-URLs движения вроде «убрать query-параметры ради красоты» — но это применимо только к идентификаторам ресурсов, не к выборкам.
>   
>   **Если бы это было правдой:** Spring `@PathVariable`/Express router имели бы exponential complexity по числу комбинаций; кэш CDN не смог бы нормализовать URL-ы для одной и той же выборки в другом порядке параметров.
> 
> - [ ] **B)** Использовать единый универсальный query-language (RSQL/OData) для **всех** endpoints, даже если фильтры простые
>   
>   **Что на самом деле:** RSQL (`?filter=age=gt=18;status==active`) и OData дают мощность SQL поверх REST, но платится **сложностью клиента** (парсер на каждой стороне), **рисками безопасности** (injection в фильтры → query) и **deopt** в БД (динамические WHERE без plan cache). Для 90% endpoints достаточно `?status=active&role=admin`.
>   
>   **Откуда путаница:** «один синтаксис на все случаи» выглядит как DRY, но de-facto переусложняет тривиальные endpoints.
>   
>   **Если бы это было правдой:** GitHub, Stripe API использовали бы RSQL/OData — а они оставили простые query-параметры и отдельный `POST /search` для сложных случаев.
> 
> - [x] **C)** Простые фильтры — через query-параметры (`?status=active&role=admin`), сортировка — через `?sort=field` с `-` префиксом для DESC и комой для многоуровневой; сложный поиск выносить в `POST /search` с JSON-body
>   
>   **Развёрнутое объяснение:** разделение по сложности даёт **простой happy-path** (GET-кэшируемый, идемпотентный, читаемый) и **отдельный мощный endpoint** для нестандартных нужд. Query-параметры остаются плоскими (`AND`-семантика по умолчанию), сортировка использует знакомую `ORDER BY`-конвенцию (`-created_at` = `ORDER BY created_at DESC`), а сложные булевы выражения, full-text-поиск и многомерные фильтры идут через `POST /search` с типизированным JSON-телом — это **не нарушает REST**, потому что `/search` — отдельный resource «поисковый запрос».
>   
>   **Пример:**
>   ```http
>   GET /users?status=active&role=admin&sort=-created_at,name&limit=20
>   
>   POST /users/search
>   Content-Type: application/json
>   {
>     "filter": {
>       "and": [
>         {"status": "active"},
>         {"or": [{"role": "admin"}, {"role": "owner"}]},
>         {"created_at": {"gte": "2026-01-01"}}
>       ]
>     },
>     "sort": [{"field": "created_at", "order": "desc"}]
>   }
>   ```
>   
>   **Когда применять:** любой REST API. Граница — когда число query-параметров > 5–7 или появляется потребность в OR/NOT — переноси на POST /search.
>   
>   **Подводные камни:** GET с query можно кэшировать (CDN, browser), POST /search — нет; не используй POST там где хватает GET. Документируй **точный** допустимый список filter-полей в OpenAPI — иначе клиент пробует случайные параметры. Sanitize/whitelist sort-поля → иначе ORDER BY injection через `?sort=password` или путь до полнотабличного скана по неиндексированному полю.
>   
>   **Связанные вопросы:** [[Q9]] (pagination), [[Q10]] (cursor-based), [[Q12]] (field selection), [[Q13]] (error format)
> 
> - [ ] **D)** Принимать произвольные SQL-фрагменты в query (`?where=age>18 AND status='active'`) — это максимально гибко
>   
>   **Что на самом деле:** это **SQL injection by design** — клиент диктует фрагмент WHERE напрямую БД. Даже с парсингом и whitelist-проверкой это re-implementation парсера SQL на стороне сервера, что эквивалентно построению полного query-builder-а — и всё равно с дырами безопасности.
>   
>   **Откуда путаница:** «гибкость = хорошо» без учёта security/cost — антипаттерн.
>   
>   **Если бы это было правдой:** PostgREST/Hasura имели бы prod-инциденты каждую неделю; они избегают этого через RLS и whitelisted-поля.

## Q12. Field selection (sparse fieldsets)?

**Return only requested fields:**
```
GET /users/123?fields=id,name,email
```

**Response:**
```json
{
  "id": 123,
  "name": "Alice",
  "email": "alice@example.com"
}
```

**Effect:** меньше bandwidth, faster для bandwidth-sensitive clients.

**JSON:API has standard:**
```
GET /users?fields[user]=name,email
```

**GraphQL** does this naturally.


> [!mcq]
> 
> **Вопрос:** Зачем в REST API нужен механизм sparse fieldsets (`?fields=id,name,email`) и когда его применять?
> 
> - [ ] **A)** Sparse fieldsets — это устаревший паттерн до появления HTTP-сжатия (gzip/brotli); современные API не нуждаются в нём
>   
>   **Что на самом деле:** gzip уменьшает **размер payload по проводу**, но **не размер payload до сжатия**: сервер всё равно генерирует полный JSON (CPU, memory, serialization-cost), а клиент **парсит** и держит в памяти все поля. Для мобильных клиентов с ограниченной RAM и медленным JSON-парсером (Android, low-end iOS) разница между 50KB и 5KB после парсинга — заметная.
>   
>   **Откуда путаница:** «gzip решает bandwidth» — но bandwidth ≠ единственная стоимость передачи и обработки.
>   
>   **Если бы это было правдой:** JSON:API, GitHub GraphQL, Stripe `expand` не имели бы field-selection в спецификации.
> 
> - [ ] **B)** Применять sparse fieldsets ко всем endpoints обязательно, даже если клиент всегда запрашивает один и тот же набор полей
>   
>   **Что на самом деле:** field-selection добавляет **сложность серверной логики** (dynamic projection в БД-query, проверка прав доступа на каждое поле, валидация имён полей против whitelist). Для внутренних API с фиксированной schema и контролируемыми клиентами это overhead без выгоды.
>   
>   **Откуда путаница:** «опциональная фича не помешает» — но `?fields=...` без явного use-case увеличивает поверхность атаки (information disclosure через сравнение времён ответа) и снижает кэшируемость (каждый набор полей = отдельный cache-key).
>   
>   **Если бы это было правдой:** все Spring/Rails-фреймворки имели бы field-projection встроенным, а не как edge-case.
> 
> - [ ] **C)** Sparse fieldsets — это альтернатива GraphQL; если используешь GraphQL, REST с `?fields=` не нужен
>   
>   **Что на самом деле:** GraphQL и REST sparse fieldsets — **разные слои абстракции**. GraphQL — это **отдельный protocol** (query language, schema, resolvers), а sparse fieldsets — **дополнение к REST** для случаев, когда полный переход на GraphQL неоправдан (один-два endpoint-а с тяжёлыми объектами, mobile-first API). Stripe использует REST с `expand` для on-demand расширения вложений — без GraphQL.
>   
>   **Откуда путаница:** оба механизма решают «верни только нужное», но GraphQL — это архитектурный выбор, sparse fieldsets — точечная оптимизация.
>   
>   **Если бы это было правдой:** GitHub не поддерживал бы одновременно REST API (с field-selection через media types) и GraphQL API.
> 
> - [x] **D)** Sparse fieldsets применять точечно — для тяжёлых ресурсов (с вложенными коллекциями, BLOB-ами, дорогими computed-полями) и mobile-клиентов; синтаксис JSON:API (`?fields[user]=name,email`) или простой (`?fields=name,email`); серверу обязательно whitelist допустимых полей и проброс проекции в SQL `SELECT`, иначе экономии нет
>   
>   **Развёрнутое объяснение:** ценность sparse fieldsets — в **трёх измерениях**: (1) **сетевой payload** (после gzip всё равно меньше при сильно разреженных объектах); (2) **serialization-cost** на сервере (не материализуем 50 полей если нужны 3); (3) **memory footprint** на клиенте. Реальная экономия достигается **только** если проекция доходит до БД: `SELECT id, name, email FROM users WHERE id = ?` вместо `SELECT *`. Если на сервере всё равно `findById()` грузит полную JPA-entity, а потом отфильтровывает поля при serialization — экономия минимальна, остаётся только bandwidth.
>   
>   **Пример:**
>   ```http
>   GET /users/123?fields=id,name,email
>   
>   200 OK
>   {"id": 123, "name": "Alice", "email": "alice@example.com"}
>   ```
>   ```java
>   // Server: dynamic projection в Spring Data
>   public interface UserRepository extends JpaRepository<User, Long> {
>       <T> T findById(Long id, Class<T> projection);  // interface-based projection
>   }
>   // Whitelist + interface mapping для каждого набора полей
>   Set<String> ALLOWED = Set.of("id", "name", "email", "created_at");
>   ```
>   
>   **Когда применять:** объекты > 20 полей, наличие вложенных коллекций (orders, attachments), mobile-API с константной полосой, public-API с большой долей repeat-запросов одного клиента. JSON:API `?fields[type]=...` — стандарт когда в response несколько типов ресурсов; простой `?fields=...` — когда тип один.
>   
>   **Подводные камни:** (1) cache-key должен включать набор полей, иначе CDN отдаст «не тот» вариант; (2) обязательно whitelist полей — иначе клиент пробует `?fields=password_hash` и получает leak; (3) обязательная проекция до БД, иначе только видимость оптимизации; (4) комбинация со sparse `?include=` (relations) ломает кэш окончательно — для таких случаев лучше GraphQL.
>   
>   **Связанные вопросы:** [[Q3]] (snake_case vs camelCase), [[Q9]] (pagination), [[Q11]] (filtering и sorting), GraphQL vs REST

## Q13. (!) Error response format?

**Consistent error format** critical.

**Common pattern:**
```json
{
  "error": {
    "code": "INVALID_PARAMETER",
    "message": "The 'email' parameter is invalid",
    "details": {
      "field": "email",
      "reason": "Not a valid email address"
    },
    "documentation_url": "https://docs.example.com/errors/invalid-parameter"
  }
}
```

**Best practices:**
- **HTTP status code** (4xx, 5xx)
- **Machine-readable code** (`INVALID_PARAMETER`)
- **Human-readable message**
- **Details** для programmatic handling
- **Documentation link**
- **Request ID** (для support)

**No stack traces** в production responses (security).


> [!mcq] Что обязательно должно быть в едином формате error response для production REST API?
>
> - [x] **A) Структурированный JSON с machine-readable `code`, human-readable `message`, опциональными `details` и request_id, без stack trace**
>
>   **Почему правильно:** клиенты пишут switch по `code` (стабильный контракт), `message` показывают пользователю, `details` парсят для подсветки полей, `request_id` нужен для саппорта при разборе инцидента. Отсутствие stack trace — security: трасса раскрывает внутреннюю структуру, имена классов, версии библиотек.
>
>   **Когда применять:** любой публичный или внутренний REST API; формат фиксируется на этапе дизайна и не меняется ретроактивно (breaking change для клиентов).
>
>   **Пример реализации:** `@RestControllerAdvice` + `ResponseEntityExceptionHandler` в Spring; для FastAPI — `exception_handler`. Тело: `{"error": {"code": "INVALID_PARAMETER", "message": "...", "request_id": "..."}}`.
>
>   **Подводные камни:** (1) логировать полный stack на сервере с тем же request_id — иначе саппорт не найдёт корреляцию; (2) `code` нельзя переименовывать после релиза — клиенты захардкодили; (3) i18n `message` — на стороне клиента по `code`, а не на сервере по Accept-Language (кэширование ломается).
>
> - [ ] **B) Только HTTP status code (4xx/5xx) без тела, чтобы не раскрывать внутреннюю структуру**
>
>   **Почему неправильно:** клиент не может различить «email невалиден» и «email уже занят» — оба 400. UX страдает: форма не подсветит конкретное поле, support не получит контекст. Безопасность — миф: правильный формат тоже не раскрывает внутренности, если не включает stack trace.
>
>   **Последствие:** пользователи видят generic «Bad Request», не понимают что чинить, нагрузка на support растёт; mobile-команда добавляет хаки парсинга `message` строки.
>
> - [ ] **C) Произвольный текст в `message` без `code`, чтобы не сковывать backend**
>
>   **Почему неправильно:** клиент не может стабильно реагировать — любая правка текста («email is invalid» → «invalid email») ломает switch на клиенте. i18n становится невозможной без duplication логики.
>
>   **Последствие:** mobile-приложение падает после релиза backend, который «всего лишь поправил опечатку» в сообщении; невозможно построить metrics по типам ошибок.
>
> - [ ] **D) Возвращать полный stack trace в production для упрощения debug**
>
>   **Почему неправильно:** security leak — раскрываются версии библиотек (CVE-таргетинг), внутренние пути, имена классов; payload раздувается на килобайты; клиент не умеет парсить stack.
>
>   **Последствие:** OWASP A05:2021 (Security Misconfiguration); атакующий получает карту внутренностей и подбирает известные эксплойты под конкретные версии.

## Q14. (!) Problem Details (RFC 7807)?

**RFC 7807** — standard error format.

```json
{
  "type": "https://example.com/probs/out-of-credit",
  "title": "You do not have enough credit.",
  "status": 403,
  "detail": "Your current balance is 30, but that costs 50.",
  "instance": "/account/12345/msgs/abc"
}
```

**Content-Type:** `application/problem+json`.

**Stripe, Spring Boot 3+** support this. Standardization simplifies tooling.


> [!mcq] Что даёт RFC 7807 Problem Details поверх собственного error-формата?
>
> - [ ] **A) Возможность не возвращать HTTP status code вообще, потому что `status` указан в теле**
>
>   **Почему неправильно:** RFC 7807 явно требует, чтобы значение поля `status` совпадало с HTTP status в response line. Прокси, CDN, retry-механики клиентов смотрят именно на HTTP status — игнорирование статуса сломает кэширование и observability.
>
>   **Последствие:** 200 OK с `"status": 403` в теле — клиентская библиотека (axios, OkHttp) посчитает запрос успешным; circuit breaker не сработает; mobile-аналитика не зафиксирует ошибку.
>
> - [x] **B) Стандартный media type `application/problem+json` и единый словарь полей (`type`, `title`, `status`, `detail`, `instance`) с возможностью расширения**
>
>   **Почему правильно:** RFC 7807 — IETF-стандарт error formats. Поле `type` — URI идентифицирующий категорию проблемы (обычно ссылка на документацию), `title` — human-readable краткое описание, `status` дублирует HTTP, `detail` — конкретика инстанса, `instance` — URI конкретного происшествия. Расширения добавляются как дополнительные ключи (например `errors[]` для validation). Spring Boot 3+ (`ProblemDetail`), ASP.NET Core, Stripe — поддерживают из коробки.
>
>   **Когда применять:** новый сервис без legacy error-формата; экосистема с множеством клиентов на разных стэках (универсальные библиотеки парсят RFC 7807); требование совместимости с API Gateway / observability tooling, которые знают `problem+json`.
>
>   **Пример реализации:** Spring 6: `ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Balance 30 < 50")`. Content-Type выставляется автоматически.
>
>   **Подводные камни:** (1) `type` URI должен реально открываться (документация) — иначе клиенты теряют пользу; (2) расширения именовать снейк-кейсом и фиксировать в OpenAPI; (3) RFC 7807 не описывает локализацию — i18n строить поверх через `type` как ключ.
>
> - [ ] **C) Шифрование тела ошибки для предотвращения information disclosure**
>
>   **Почему неправильно:** RFC 7807 не имеет отношения к шифрованию — это про структуру JSON. Транспортное шифрование решает TLS, а ограничение раскрываемой информации — это дисциплина наполнения `detail`, а не формат.
>
>   **Последствие:** разработчик ждёт «магической защиты» от RFC, при этом в `detail` пишет SQL-запрос с именами таблиц — leak состоится несмотря на формат.
>
> - [ ] **D) Автоматический retry со стороны клиента на любые 4xx ответы**
>
>   **Почему неправильно:** RFC 7807 — это формат тела, никакой retry-семантики он не задаёт. Retry на 4xx обычно вреден (400/401/403/404 не починятся повтором); правильный retry — на 408/429/5xx согласно `Retry-After`.
>
>   **Последствие:** клиент бомбардирует сервер повторами на 400, получает rate-limit, путает метрики error-rate и удлиняет latency пользователю.

## Q15. Validation errors?

**Multiple errors** в one response (don't fail-fast):

```json
{
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Request validation failed",
    "errors": [
      {"field": "email", "reason": "Invalid format"},
      {"field": "age", "reason": "Must be >= 18"},
      {"field": "country", "reason": "Required"}
    ]
  }
}
```

**Status:** 400 Bad Request или 422 Unprocessable Entity.

**Returns ALL errors** at once (better UX for forms).


> [!mcq] Как правильно возвращать ошибки валидации формы с несколькими невалидными полями?
>
> - [ ] **A) Fail-fast: остановиться на первой ошибке и вернуть только её, сэкономив CPU на проверке остальных полей**
>
>   **Почему неправильно:** для форм fail-fast ужасен для UX — пользователь чинит первое поле, шлёт запрос, узнаёт о втором, фиксит, узнаёт о третьем. Многошаговая «лестница» вместо одного цикла. CPU-экономия мнимая: bean-validation для одной формы — микросекунды.
>
>   **Последствие:** drop-off конверсии на формах регистрации/чекаута; жалобы UX-команды; пользователи бросают форму на 2-3 итерации.
>
> - [ ] **B) Возвращать 500 Internal Server Error с описанием первой найденной проблемы валидации**
>
>   **Почему неправильно:** 500 предназначен для серверных сбоев — отсутствует БД, NPE, OOM. Валидация — клиентская ошибка (клиент прислал плохие данные). 500 триггерит алерты SRE, попадает в error-rate dashboard и путает on-call.
>
>   **Последствие:** false-positive алерты будят дежурного среди ночи; SLO error-budget «горит» от пользовательских опечаток в email.
>
> - [x] **C) Собрать все валидационные ошибки в массив `errors[]` с полями `field` и `reason`, вернуть со статусом 400 Bad Request или 422 Unprocessable Entity**
>
>   **Почему правильно:** один запрос — полный список того, что чинить; UI рисует красным каждое невалидное поле одновременно. 400 — синтаксически плохой запрос (malformed body), 422 — синтаксически валидный JSON, но семантически нарушает правила домена (выбор зависит от вкуса/конвенции, оба широко используются). Bean Validation (`@Valid`) в Spring собирает все нарушения через `MethodArgumentNotValidException.getBindingResult().getFieldErrors()` — нужно только смаппить.
>
>   **Когда применять:** любой endpoint, принимающий форму или DTO с несколькими полями; особенно регистрация, profile-update, checkout — там UX-критично.
>
>   **Пример реализации:**
>   ```java
>   @ExceptionHandler(MethodArgumentNotValidException.class)
>   ResponseEntity<?> handle(MethodArgumentNotValidException ex) {
>     var errors = ex.getBindingResult().getFieldErrors().stream()
>       .map(e -> Map.of("field", e.getField(), "reason", e.getDefaultMessage()))
>       .toList();
>     return ResponseEntity.badRequest().body(Map.of(
>       "error", Map.of("code", "VALIDATION_FAILED", "errors", errors)));
>   }
>   ```
>
>   **Подводные камни:** (1) порядок полей в `errors[]` должен быть детерминированным (по порядку DTO), иначе тесты flaky; (2) сообщения `reason` локализовать на клиенте по коду правила, а не на сервере; (3) cross-field validation (например `password == confirmPassword`) выносить отдельным error c `field: "_global"` или `field: "confirmPassword"`.
>
> - [ ] **D) Возвращать 200 OK с флагом `success: false` и списком ошибок в теле, чтобы клиент не падал на HTTP-уровне**
>
>   **Почему неправильно:** анти-паттерн «SOAP-style over REST». Ломает CDN, прокси, retry-логику, observability — все смотрят на HTTP status. Клиентские библиотеки (axios `validateStatus`, OkHttp `isSuccessful`) посчитают ответ успешным.
>
>   **Последствие:** circuit-breaker не открывается на росте 400-х (потому что их «нет»); метрики error-rate показывают зелёный, реальный UX красный; интеграционные тесты ловят регрессию только в e2e.

## Q16. (!) Idempotency keys?

**Make POST idempotent** через client-supplied key.

```http
POST /payments HTTP/1.1
Idempotency-Key: 7f9c1d-...
{"amount": 100}
```

**Server logic:**
1. Check if key already processed
2. If yes — return same response (cached)
3. If no — process, store result

**Use case:** retry network failures без duplicate charges.

**Stripe** is canonical example.

**Storage:** Redis с TTL (24h typical).


> [!mcq]
>
> **Какая ключевая особенность отличает идемпотентный POST с Idempotency-Key от обычного POST?**
>
> - [ ] **A.** Сервер выполняет операцию заново при каждом retry, но возвращает первый успешный ответ
>
>     Это противоречит сути идемпотентности — повторное выполнение списало бы деньги дважды.
>
>     ❌ ПОСЛЕДСТВИЕ: при сетевом retry платёж дублируется, клиента списывают N раз вместо одного.
>
> - [ ] **B.** Idempotency-Key генерируется сервером и возвращается клиенту в Location header
>
>     Наоборот — ключ создаёт клиент (UUID), потому что только он знает, что это retry той же логической операции.
>
>     ❌ ПОСЛЕДСТВИЕ: серверный ключ не защищает от ретраев из-за timeout — клиент не знает, дошёл ли запрос.
>
> - [ ] **C.** Сервер хранит ключ навсегда в основной БД с уникальным индексом
>
>     Хранят ограниченное время (24h типично) в Redis/быстром storage — иначе таблица распухает, а старые ретраи никому не нужны.
>
>     ❌ ПОСЛЕДСТВИЕ: бесконечное хранение всех ключей раздувает БД и замедляет lookup на горячем пути платежей.
>
> - [x] **D.** Клиент передаёт уникальный ключ в Idempotency-Key, сервер при повторе возвращает закэшированный ответ
>
>     Это и есть классический паттерн Stripe: ключ — отпечаток операции на стороне клиента, сервер либо выполняет впервые и сохраняет результат, либо отдаёт сохранённый ответ.
>
>     ПРИМЕНИМОСТЬ: платежи, заказы, любые non-idempotent POST, которые клиент может ретраить при сетевых сбоях.
>
>     ПОЧЕМУ ВАЖНО: без этого retry на 5xx/timeout приводит к дублированию side-effects — двойные списания, дубль заказов.

## Q17. Optimistic concurrency (ETag)?

**ETag** — version identifier на resource.

```http
# Read
GET /users/123
ETag: "abc123"

# Modify
PUT /users/123
If-Match: "abc123"
{...}

# If resource changed → 412 Precondition Failed
```

**Прevent** lost updates (concurrent modifications).

**Effect:**
- Client A reads (ETag "abc123")
- Client B reads (ETag "abc123")
- Client A updates (succeeds, new ETag "def456")
- Client B updates (412 Precondition Failed — must reload)

**Alternative:** version fields (`{"version": 1, ...}`).


> [!mcq]
>
> **Как ETag в связке с If-Match защищает от потерянных обновлений (lost update)?**
>
> - [x] **A.** Клиент шлёт If-Match со старым ETag — сервер сравнивает с текущим и возвращает 412, если ресурс изменился
>
>     Это классический optimistic concurrency control: ETag — версия ресурса; при PUT с If-Match сервер атомарно проверяет «версия совпадает?» и либо применяет, либо отказывает 412 Precondition Failed.
>
>     ПРИМЕНИМОСТЬ: REST API с конкурентными редакторами одного ресурса (документы, профили, конфигурация).
>
>     ПОЧЕМУ ВАЖНО: без неё параллельные PUT затирают изменения друг друга, а клиент даже не узнаёт об этом.
>
> - [ ] **B.** Сервер блокирует ресурс при чтении (GET) и снимает блокировку только после PUT того же клиента
>
>     Это pessimistic locking, а не optimistic — ETag намеренно избегает блокировок, чтобы не держать долгоживущие транзакции и не блокировать читателей.
>
>     ❌ ПОСЛЕДСТВИЕ: брошенный клиент держит lock навсегда, остальные читатели/писатели стоят, deadlocks в распределённом окружении.
>
> - [ ] **C.** ETag вычисляется на клиенте и используется только для HTTP-кеширования через If-None-Match
>
>     Кеширование (If-None-Match → 304) — другое применение того же заголовка; для concurrency нужен If-Match, и считает ETag сервер (хэш/version).
>
>     ❌ ПОСЛЕДСТВИЕ: путаница между cache validation и concurrency control приводит к ложному ощущению защиты — lost updates не предотвращаются.
>
> - [ ] **D.** Клиент при конфликте автоматически повторяет PUT с новым ETag без участия пользователя
>
>     Автоматический replay перетрёт чужие изменения — суть optimistic concurrency в том, чтобы вернуть конфликт наверх и дать клиенту/пользователю смержить.
>
>     ❌ ПОСЛЕДСТВИЕ: тихое перезаписывание чужих правок без merge — те же lost updates, только замаскированные.

## Q18. (!) Auth strategies (API key, JWT, OAuth)?

**API Key:**
- Simple, common для server-to-server
- `Authorization: Bearer ak_test_...`
- No expiration usually
- Limited scope (or full access)

**JWT (JSON Web Token):**
- Self-contained (claims внутри)
- Stateless (no server lookup)
- Expiration built-in
- Suitable для user auth

**OAuth 2.0:**
- Standard для third-party access
- Authorization Code flow для users
- Client Credentials для service accounts
- Refresh tokens для long-lived sessions

**Choice:**
- **Internal services** — API keys или mTLS
- **User-facing** — OAuth + JWT
- **Public APIs** — API keys (simple) или OAuth (advanced)

Подробнее — в [OAuth2](../security/oauth2-interview.md), [JWT](../security/jwt-interview.md).


> [!mcq]
>
> **Какой механизм авторизации лучше подходит для пользовательского веб-приложения, делегирующего доступ к третьесторонним API?**
>
> - [ ] **A.** Долгоживущий API key, зашитый в JavaScript-бандл фронтенда
>
>     API key в публичном клиентском коде — это утечка секрета: любой может открыть DevTools и украсть ключ, обычно эти ключи не имеют expiration и не привязаны к пользователю.
>
>     ❌ ПОСЛЕДСТВИЕ: украденным ключом атакующий действует от имени сервиса без аудита и без возможности отозвать только для одного пользователя.
>
> - [x] **B.** OAuth 2.0 Authorization Code Flow с короткоживущим access token и refresh token
>
>     Это стандартный паттерн делегированного доступа: пользователь явно даёт согласие у провайдера, фронтенд получает scoped access token с TTL, refresh token продлевает сессию без повторного логина.
>
>     ПРИМЕНИМОСТЬ: «Войти через Google/GitHub», доступ к Google Drive/Slack API от имени пользователя, любой third-party access с consent screen.
>
>     ПОЧЕМУ ВАЖНО: scope ограничивает урон при компрометации, короткий TTL минимизирует окно атаки, отзыв refresh token мгновенно вырубает сессию.
>
> - [ ] **C.** Basic Auth с логином/паролем пользователя в каждом запросе к API
>
>     Передача plaintext пароля в каждом запросе означает, что любая утечка лога/прокси раскрывает пароль навсегда, плюс невозможно ограничить scope или отозвать только часть прав.
>
>     ❌ ПОСЛЕДСТВИЕ: компрометация одного логфайла = компрометация пароля пользователя ко всему сервису, без возможности отзыва без смены пароля.
>
> - [ ] **D.** Самоподписанный JWT, выпущенный фронтендом со своим секретом
>
>     JWT должен подписывать доверенный auth-сервер своим приватным ключом — фронт не имеет права выпускать токены, иначе любая XSS даёт атакующему фабрику валидных токенов.
>
>     ❌ ПОСЛЕДСТВИЕ: подделка identity с клиента, сервер не может отличить настоящего пользователя от поддельного — полный обход auth.

## Q19. API key best practices?

1. **Prefix keys** (`sk_live_`, `pk_test_`) — identify type at glance
2. **Long random** (32+ bytes)
3. **Scoped permissions** (read-only, specific resources)
4. **Revocable** (must be able to invalidate immediately)
5. **Rotatable** (allow renew без downtime)
6. **Per-environment** (test, live)
7. **Audit logging** (who used when)
8. **Hash в DB** (don't store plaintext)
9. **Show once** (на creation only)
10. **Rate limiting** per key

**Stripe pattern:** `sk_live_abc123...` — prefix immediately tells whether secret/publishable/test/live.


> [!mcq] Какой набор практик хранения API-ключей минимизирует риск утечки и облегчает ротацию?
>
> - [ ] Хранить ключи в БД в открытом виде, отдавать админу при любом запросе
>     - Почему: «show only on creation» — единственный способ гарантировать, что даже DB-админ не увидит plaintext-ключи у клиентов.
>     - Последствие: при компрометации БД утекают рабочие production-ключи всех клиентов одновременно.
>
> - [x] Хранить хеш ключа, показывать plaintext один раз при создании, давать revoke/rotate без downtime
>     - Почему: ключи — это secrets; хеш в БД защищает от утечки, одноразовый показ снимает риск повторного просмотра, revoke/rotate — обязательны для incident response.
>     - Как работает: на создание сервер генерирует случайный ключ (32+ байта), отдаёт клиенту один раз, в БД сохраняет SHA-256/HMAC. При запросе сравнивает хеш. Revoke = пометка `revoked_at`. Rotate = создание нового ключа со старым ещё активным N часов.
>     - Когда: любой публичный API с per-customer ключами (Stripe, GitHub, Twilio).
>     - Пример: `sk_live_abc123…` с prefix указывает на тип/среду; в БД лежит `sha256(key)`.
>
> - [ ] Использовать один общий API-ключ для всех клиентов и менять его раз в год
>     - Почему: shared secret = невозможно отозвать ключ одного скомпрометированного клиента без поломки остальных; раз в год — слишком редко для production.
>     - Последствие: утечка ключа у одного клиента форсит экстренную ротацию для всех, окно эксплуатации — до 365 дней.
>
> - [ ] Класть API-ключи в URL query string и логировать их в access-логах для audit
>     - Почему: URL попадают в access-логи, browser history, Referer-header, прокси-логи — это утечка secrets по дизайну. Audit делается отдельным `audit_log` с key_id, не plaintext.
>     - Последствие: ключи утекают через logs/Sentry/CDN-логи в десятки систем, ротация после инцидента стоит дни.


## Q20. (!) Rate limiting headers?

**Tell client about limits:**

```http
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 47
X-RateLimit-Reset: 1714579200    # Unix timestamp
```

**RFC 9239 (newer):**
```http
RateLimit-Limit: 100
RateLimit-Remaining: 47
RateLimit-Reset: 60
```

**На rate limit hit:**
```http
HTTP/1.1 429 Too Many Requests
Retry-After: 60
```

**Client should:**
- Monitor headers
- Backoff before hitting limit
- Honor `Retry-After` после 429


> [!mcq] Что должен вернуть сервер на rate-limited запрос, чтобы клиент мог корректно отступить?
>
> - [x] HTTP 429 Too Many Requests с заголовком `Retry-After` и текущими `RateLimit-*` headers
>     - Почему: 429 — стандартный статус для rate-limit, `Retry-After` (секунды или HTTP-date) даёт клиенту точный момент следующей попытки, `RateLimit-Limit/Remaining/Reset` (RFC 9239) сообщают окно лимита.
>     - Как работает: клиентский SDK видит 429, читает `Retry-After: 60`, ждёт 60 секунд и повторяет; в фоне `RateLimit-Remaining` позволяет prepare backoff до hit.
>     - Когда: любой API с rate-limiting (Stripe, GitHub, Twilio) — это де-факто стандарт.
>     - Пример: `HTTP/1.1 429 Too Many Requests\nRetry-After: 60\nRateLimit-Limit: 100\nRateLimit-Remaining: 0`.
>
> - [ ] HTTP 200 OK с пустым телом и заголовком `X-Rate-Limited: true`
>     - Почему: 200 говорит «успех» — клиент не поймёт что запрос отклонён и не retry; custom header заголовок не покрывается стандартными HTTP-клиентами.
>     - Последствие: клиент сохранит «пустые данные» как валидный ответ, потеряет реальные данные, ретраев не будет.
>
> - [ ] HTTP 503 Service Unavailable без заголовков
>     - Почему: 503 семантически означает «сервис недоступен» (downtime, перегрузка), не «лимит превышен для этого клиента». Без `Retry-After` клиент ретраит с фиксированным интервалом, провоцируя retry storm.
>     - Последствие: клиенты считают это инцидентом всего сервиса, эскалация в саппорт, retry-шторм усиливает нагрузку.
>
> - [ ] HTTP 403 Forbidden с описанием лимита в JSON body
>     - Почему: 403 означает «permission denied» — клиент решит что у него отозваны права и пойдёт re-auth/re-key, а не ждать. Сообщение в body не парсится стандартными middleware/прокси.
>     - Последствие: клиент инициирует key rotation/re-auth flow, поддержка перегружена ложными тикетами «отозвали ключ».


## Q21. Throttling vs hard limits?

**Throttling** (soft) — slow down requests.
**Hard limit** — reject (429).

**Implementation:**
- **Token bucket** (popular)
- **Sliding window**
- **Fixed window**

**Per:**
- API key
- IP address
- User
- Endpoint

**Stripe model:** different limits per endpoint (creating users vs reading).

**Tools:** API Gateway (Kong, Apigee), Nginx, Envoy, application code.


> [!mcq] В чём ключевая разница между throttling и hard rate limit, и когда какой применять?
>
> - [ ] Throttling и hard limit — это синонимы, оба отклоняют запросы с 429
>     - Почему: это разные механизмы — throttling замедляет (queue/delay), hard limit отклоняет; смешивание их понятий ведёт к неверной конфигурации API gateway.
>     - Последствие: клиент видит timeout вместо ясного 429, retry-логика ломается, observability фейлится.
>
> - [ ] Throttling используется на проде, hard limit — на dev/staging
>     - Почему: оба используются на проде в комбинации; среда не определяет тип ограничения. Hard limit (429) защищает от abuse, throttling — от нагрузки.
>     - Последствие: на dev отключают защиту и пропускают баги; на проде без hard limit одно-bad-actor консьюмер выжирает весь capacity.
>
> - [x] Throttling — soft, замедляет/queueing (token bucket с задержкой), hard limit — отклоняет с 429, применяются на разных уровнях
>     - Почему: throttling сохраняет SLA при кратковременных всплесках (smoothing), hard limit защищает от sustained abuse и переполнения; типичный prod использует оба: throttle до 80% capacity, hard limit на 100%.
>     - Как работает: token bucket с capacity=100 и refill=10/s; при превышении throttle добавляет delay, при превышении hard cap (например, 200%) возвращает 429.
>     - Когда: throttling — для предсказуемого degradation, hard limit — для abuse protection; per-endpoint и per-key.
>     - Пример: Stripe ограничивает создание users жёстче (10/s hard) чем чтение (100/s throttled).
>
> - [ ] Hard limit — это infrastructure-level (Nginx), throttling — application-level (код); они никогда не сочетаются
>     - Почему: оба механизма реализуются на любом уровне (Nginx умеет и `limit_req` с burst+delay = throttling, и `limit_req_zone` reject = hard); в проде их обычно сочетают на разных уровнях (Nginx + app).
>     - Последствие: defense-in-depth теряется — один уровень защиты обходится при misconfiguration или DDoS на нём.


## Q22. (!) OpenAPI / Swagger?

**OpenAPI** (formerly Swagger) — standard для API documentation.

```yaml
openapi: 3.0.0
info:
  title: My API
  version: 1.0
paths:
  /users/{id}:
    get:
      summary: Get user
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: User found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
```

**Tools:**
- **Swagger UI** — interactive docs
- **Redoc** — alternative UI
- **Code generators** — clients SDKs из spec
- **Validators** — request/response validation

**Best practice:** **API spec first, then implement**. OpenAPI = source of truth.

Подробнее — в [OpenAPI / Swagger](openapi-swagger-interview.md).


> [!mcq] Какой подход к OpenAPI-спецификации даёт максимум пользы команде и интеграторам?
>
> - [ ] Генерировать OpenAPI из работающего кода в проде через introspection раз в неделю
>     - Почему: «code-first auto-generation» приводит к тому, что спека всегда отстаёт; никто не ревьюит контракт до релиза, breaking changes уходят в прод.
>     - Последствие: интеграторы получают обновлённую спеку постфактум, ломаются их клиенты, нет процесса согласования API-changes.
>
> - [ ] Писать OpenAPI вручную в .md документации без машинной валидации
>     - Почему: markdown не валидируется тулингом, нет генерации SDK/клиентов, дрейф между документацией и реализацией — гарантирован.
>     - Последствие: документация устаревает за недели, integration partners жалуются на «non-working examples», поддержка тонет в вопросах.
>
> - [x] Spec-first: писать OpenAPI YAML/JSON до кода, валидировать в CI, генерировать клиенты и server-stubs
>     - Почему: спека — source of truth для контракта; review до реализации ловит проблемы дешёво; авто-генерация клиентов даёт интеграторам SDK «бесплатно»; CI-валидация ловит дрейф.
>     - Как работает: PR с изменением `openapi.yaml` ревьюится → генерируется server-stub (Spring, FastAPI) и client SDK → реализация заполняет stub → contract-tests проверяют runtime соответствие.
>     - Когда: любой public API, B2B-интеграции, microservices с несколькими консьюмерами.
>     - Пример: Stripe, GitHub — оба ведут OpenAPI как source-of-truth, генерируют 10+ SDK из неё.
>
> - [ ] Использовать Swagger UI только для внутренней разработки, без публикации спеки наружу
>     - Почему: Swagger UI — удобный front, но без публичной спеки интеграторы лишены машинной генерации клиентов и contract-testing; «внутреннее использование» теряет 90% value.
>     - Последствие: каждый интегратор пишет HTTP-клиент с нуля, contract-mismatches ловятся в проде, поддержка перегружена ad-hoc вопросами.


## Q23. Examples и SDKs?

**Always provide:**
- **Code examples** (curl, Python, JavaScript, Java, ...)
- **Postman collection** или Insomnia exports
- **SDKs** для major languages (auto-generated from OpenAPI)

**SDKs reduce friction:** developers don't write HTTP code from scratch.

**Auto-gen tools:** OpenAPI Generator, Speakeasy, Stainless.


> [!mcq] Что даёт интегратору авто-сгенерированный SDK сверх «голой» HTTP-документации?
>
> - [ ] Только улучшение DX без влияния на надёжность интеграции
>     - Почему: SDK даёт типизацию, retry-логику, обработку 429/5xx, аутентификацию из коробки — это не просто DX, а снижение integration bugs.
>     - Последствие: команда недооценивает SDK и не вкладывается в авто-генерацию, интеграторы пишут свои клиенты с багами retry/auth.
>
> - [x] Типизированный клиент с готовым auth, retry, pagination, error-handling — снижает integration bugs и time-to-first-call
>     - Почему: SDK инкапсулирует HTTP-детали (auth headers, retry на 429/5xx с backoff, pagination cursor, парсинг ошибок), интегратор работает с domain-объектами; авто-генерация из OpenAPI гарантирует синхронность с API.
>     - Как работает: OpenAPI Generator/Speakeasy/Stainless парсят spec → создают типизированные модели (`User`, `Order`) и методы (`client.users.get(id)`); SDK включает middleware для auth/retry/logging.
>     - Когда: любой public API с >5 эндпоинтами или 3+ языками интеграторов.
>     - Пример: `stripe.charges.create(amount=2000, currency='usd')` вместо ручного `POST /v1/charges` с form-encoded body.
>
> - [ ] Полную обратную совместимость SDK при breaking changes API
>     - Почему: SDK генерируется из spec — если API ломается, SDK тоже ломается (новая major version). SDK не «маскирует» breaking changes автоматически.
>     - Последствие: ложные ожидания «обновим API, клиенты не заметят» приводят к падению интеграций при релизе.
>
> - [ ] Защиту от утечки API-ключей через SDK-side encryption
>     - Почему: SDK не шифрует ключи — они в env vars/secrets manager у клиента; SDK только передаёт в Authorization header через HTTPS.
>     - Последствие: команда полагается на «волшебную защиту SDK», не вкладывается в нормальное хранение ключей у интеграторов (docs, examples с env vars).


## Q24. (!) HTTPS only?

**Always.** No HTTP в production.

```
HTTP/1.1 308 Permanent Redirect
Location: https://api.example.com/...
```

**Or fail с 400/426** (don't redirect — clients may leak secrets).

**HSTS header:**
```http
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

**TLS 1.2 minimum** (1.3 recommended).


> [!mcq] Как правильно поступить с HTTP-запросом к API в проде, который требует HTTPS?
>
> - [ ] Принять запрос на HTTP и обработать как обычно — клиент сам решит мигрировать
>     - Почему: на HTTP API-ключ и payload передаются в открытом виде, перехватываются на любом hop; «клиент сам мигрирует» — никогда не работает в реальности.
>     - Последствие: API-ключи утекают через wifi-сниффинг и compromised proxies, secrets компрометируются у интеграторов.
>
> - [ ] Сделать 301/302 redirect на HTTPS-версию URL
>     - Почему: при redirect клиент уже отправил secrets в HTTP-запросе — они утекли до того, как redirect случился. Стандартные HTTP-клиенты вроде curl автоматически следуют redirect и могут «потерять» Authorization header.
>     - Последствие: secret уже в открытом виде в логах, redirect не защищает; ложное чувство безопасности.
>
> - [x] Отклонить HTTP-запрос с 400/426 без redirect, на HTTPS отдавать HSTS-заголовок
>     - Почему: fail-fast предотвращает утечку secrets (клиент не отправит ключ повторно по https без явного решения); HSTS (`max-age=31536000; includeSubDomains; preload`) запоминается браузером и форсит HTTPS даже при попытке HTTP.
>     - Как работает: на :80 listen возвращает 400/426 «HTTPS required» с минимальным телом без обработки auth; на :443 отдаёт `Strict-Transport-Security` с большим max-age и preload-листингом.
>     - Когда: production API с secrets/auth, особенно B2B и financial APIs.
>     - Пример: Stripe API возвращает 400 на HTTP, не редиректит — это форсит разработчиков починить URL сразу.
>
> - [ ] Разрешить HTTP только для health-check эндпоинтов, остальное — HTTPS
>     - Почему: смешанная конфигурация усложняет defense; даже health-check может утекать internal-info (имена пулов, БД, версии). Современные load balancers и orchestrators (k8s) делают health checks HTTPS без проблем.
>     - Последствие: misconfigured proxy случайно отдаст другой эндпоинт через :80, secret утекает; security audit fail.


## Q25. Input validation, output encoding?

**Input validation:**
- Schema (OpenAPI или JSON Schema)
- Type checking
- Range checking
- Format (email, URL, UUID)
- Length limits
- SQL injection prevention (parameterized queries)

**Output encoding:**
- Escape HTML/JS если HTML rendering possible
- Sanitize file names в URLs

Подробнее — в [Application Security](../security/application-security-interview.md).


> [!mcq] Где должна происходить input validation в продакшен API, чтобы защитить от SQLi и malformed data?
>
> - [ ] Только на клиенте через JavaScript-валидаторы перед отправкой формы
>     - Почему: клиент-валидация — для UX (быстрый feedback), не security; клиент модифицируется злоумышленником (curl, postman, modified JS), и без серверной валидации SQL-injection пройдёт напрямую.
>     - Последствие: SQLi через ручной POST с любым payload, утечка/повреждение данных в БД.
>
> - [x] На границе API через schema (OpenAPI/JSON Schema) + типизированные queries (prepared statements/ORM)
>     - Почему: schema-валидация на entry-point режектит malformed JSON/типы до бизнес-логики; prepared statements/ORM с параметризованными запросами делают SQLi структурно невозможным (значения не интерполируются в SQL-строку).
>     - Как работает: контроллер парсит JSON по OpenAPI-схеме (type, range, format, length, pattern), невалидное отклоняется 400 с RFC 7807 detail; в БД-слое — `SELECT … WHERE id = ?` с bind-параметром, не string-concat.
>     - Когда: каждый API-endpoint с user input; обязательно для public/B2B API.
>     - Пример: Spring `@Valid @RequestBody UserDto` + JPA repository c named parameters; FastAPI Pydantic models + SQLAlchemy ORM.
>
> - [ ] Только в БД через CHECK constraints и triggers
>     - Почему: БД-constraints — последняя линия защиты, но они срабатывают после round-trip и не защищают от SQLi (если значения уходят через string-interpolation, constraints не помогают). Ошибка вылазит как DB exception вместо structured 400.
>     - Последствие: latency на каждом bad request, неинформативные 500 ошибки клиентам, SQLi всё ещё возможна.
>
> - [ ] Делать sanitize input удалением подозрительных символов (`'`, `;`, `--`) перед SQL
>     - Почему: blacklist-санитизация легко обходится (encoding, double-encoding, comment variants); правильный подход — параметризованные запросы, которые делают значение data, а не code. «Удаление кавычек» — антипаттерн.
>     - Последствие: ложное чувство безопасности, SQLi через `' OR 1=1/**/` или unicode-варианты обходит фильтр.


## Q26. CORS?

**Cross-Origin Resource Sharing** — browser security.

```http
Access-Control-Allow-Origin: https://myapp.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 3600
```

**Preflight OPTIONS** request before actual request.

**Best practice:** specify exact origins, не `*` для authenticated APIs.


> [!mcq] Как корректно настроить CORS для public API, который вызывается из browser-SPA авторизованных партнёров?
>
> - [ ] `Access-Control-Allow-Origin: *` и `Access-Control-Allow-Credentials: true`
>     - Почему: это невалидная комбинация — браузер отклонит response, если используется `withCredentials=true` (cookies, Authorization header) с wildcard origin. Это намеренное ограничение CORS spec.
>     - Последствие: запросы из SPA падают с CORS error, либо приходится отключать credentials и переходить на менее безопасную auth.
>
> - [ ] Полностью отключить CORS на сервере, проверяя Origin только в application-коде
>     - Почему: «отключение CORS» обычно означает echo Origin'a в `Allow-Origin`, что эквивалентно `*` и открывает CSRF-like атаки; правильная проверка — на CORS-уровне через allowlist.
>     - Последствие: любой malicious сайт может вызывать API от имени залогиненного пользователя через JavaScript, утечка данных через CSRF.
>
> - [x] Allowlist конкретных origin'ов (по партнёрам), `Allow-Credentials: true`, preflight кешируется через `Max-Age`
>     - Почему: явный список allowed origins (`https://partner1.com`, `https://partner2.com`) предотвращает CSRF, `Allow-Credentials` позволяет cookie/Authorization, `Max-Age: 3600` снижает количество preflight OPTIONS-запросов.
>     - Как работает: на каждый запрос сервер сравнивает `Origin` header с allowlist, echo обратно при match; на OPTIONS отвечает `Allow-Methods/Headers/Max-Age` без вызова бизнес-логики; для не-allowed возвращает response без CORS-headers — браузер блокирует.
>     - Когда: B2B API с авторизованными SPA-клиентами; partner integrations.
>     - Пример: Spring `CorsConfigurationSource` с `setAllowedOrigins(List.of("https://acme.com"))` и `setAllowCredentials(true)`.
>
> - [ ] Разрешить любой origin через regex `.*\.partner\.com` без allowlist
>     - Почему: regex-based matching часто содержит баги (`.` без escape матчит любой символ, missing anchors); subdomain takeover у партнёра делает атаку тривиальной. Явный список безопаснее.
>     - Последствие: skipped escape матчит `evil-partnerXcom.attacker.com`, атакующий получает доступ к API от имени партнёра.


## Q27. (!) Caching headers?

```http
# Tell client/CDN how long к cache
Cache-Control: public, max-age=3600

# ETag для conditional requests
ETag: "abc123"

# Last modified
Last-Modified: Sat, 19 Apr 2025 14:30:00 GMT

# Conditional GET
GET /users/123
If-None-Match: "abc123"
# → 304 Not Modified (если unchanged)
```

**Cache levels:**
- Browser cache
- CDN
- API gateway
- Application cache (Redis)

**Use case:** static-ish data (user profile, product catalog).


> [!mcq] Что даёт связка `ETag` + `If-None-Match` для GET-эндпоинта пользовательского профиля?
>
> - [ ] Защиту от concurrent updates через optimistic locking
>     - Почему: это use-case `If-Match` (на PUT/PATCH для optimistic concurrency), не `If-None-Match` (на GET для caching). Разные семантики, разные заголовки.
>     - Последствие: путаница между caching и concurrency приведёт к неверной реализации — либо потерянные updates, либо неработающее кеширование.
>
> - [x] Conditional GET: при unchanged ETag сервер возвращает 304 Not Modified без тела, экономя bandwidth и render-cost
>     - Почему: клиент посылает `If-None-Match: "abc123"`, сервер сравнивает с текущим ETag; если совпадает — 304 Not Modified (без body), клиент использует cached version. Экономит bandwidth, serialization, render.
>     - Как работает: на GET сервер считает ETag (hash от resource state), отдаёт в response; клиент кеширует `(URL, ETag, body)`; на повторный запрос шлёт `If-None-Match`; сервер сравнивает (часто только проверка version в БД, без полного fetch) и отдаёт 304.
>     - Когда: read-heavy ресурсы с редким обновлением (профиль, каталог, конфиг); особенно при больших payload.
>     - Пример: `GET /users/123` → `200 ETag: "v42"` + body; повторный `GET /users/123 If-None-Match: "v42"` → `304 Not Modified` (empty body, ~100 bytes vs ~5KB).
>
> - [ ] Автоматическую инвалидацию CDN-кеша при изменении ресурса
>     - Почему: ETag сам по себе не инвалидирует CDN-кеш; CDN использует TTL/purge API/Cache-Control. ETag — инструмент клиента для revalidation, не push-инвалидация для CDN.
>     - Последствие: расчёт на «автоинвалидацию» приводит к stale data в CDN после updates; нужен явный purge или короткий max-age.
>
> - [ ] Шифрование ответа на уровне HTTP-кеширования
>     - Почему: ETag — это hash для compare, не шифрование; шифрование — это TLS (транспорт). Кешированный ответ хранится в plaintext в браузере/CDN.
>     - Последствие: ложное чувство безопасности при размещении sensitive data в кешируемых endpoints — они доступны в browser cache/CDN.


## Q28. Compression (gzip, brotli)?

**Client requests:**
```http
Accept-Encoding: gzip, br
```

**Server responds:**
```http
Content-Encoding: gzip
```

**Effect:** 60-90% reduction для JSON/text.

**Brotli** > **gzip** (better compression, slightly more CPU).

**Most frameworks** auto-handle compression.


> [!mcq] Когда корректно включать compression (gzip/brotli) на API-эндпоинтах?
>
> - [ ] Только на статических файлах (CSS, JS) — для JSON это бессмысленно
>     - Почему: JSON — текстовый формат с высокой избыточностью (повторяющиеся ключи, whitespace), gzip даёт 60-90% reduction; именно для JSON-API compression наиболее эффективна.
>     - Последствие: bandwidth-расходы CDN х10 от необходимого; мобильные клиенты на медленных каналах долго грузят payload.
>
> - [x] На всех JSON/text-ответах когда клиент шлёт `Accept-Encoding: gzip, br`, кроме encrypted/уже-compressed payload
>     - Почему: text-формат (JSON/XML/HTML) сжимается в 5-10 раз; brotli (`br`) даёт лучше compression чем gzip при чуть большем CPU; для encrypted/binary (изображения, PDF) повторное сжатие даёт <5% эффекта и тратит CPU.
>     - Как работает: клиент шлёт `Accept-Encoding: gzip, br`; сервер выбирает поддерживаемый алгоритм, возвращает `Content-Encoding: br` + сжатый body. Negotiation через q-values: `gzip;q=0.5, br;q=1.0`.
>     - Когда: production API с JSON-ответами; включается на reverse proxy (Nginx) или framework middleware (Spring `server.compression.enabled=true`).
>     - Пример: `nginx`: `gzip on; gzip_types application/json text/plain; brotli on;`.
>
> - [ ] Compression нужно делать на уровне приложения вручную через `gzip.compress()` перед `response.write()`
>     - Почему: это работа reverse-proxy/framework; ручное сжатие не учитывает `Accept-Encoding` negotiation, ломает streaming, конфликтует с middleware и CDN.
>     - Последствие: double-compression (приложение + Nginx), ломаные клиенты которые не указали `br`, баги при HEAD-запросах с Content-Length.
>
> - [ ] Compression снижает latency на medium/высоких payload, но всегда тратит CPU больше чем экономит
>     - Почему: gzip-compression тратит микросекунды CPU при exhanged ratio 5-10x; на сетях с RTT >50ms compression-savings (меньше packets) сильно перевешивают CPU. Для cold mobile networks compression — критична для UX.
>     - Последствие: отключение compression «ради CPU» утяжеляет mobile UX и увеличивает egress-bill в облаке.


## Q29. (!) HATEOAS — нужно?

**В большинстве** API — **нет**.

**HATEOAS pure approach** rarely justifies complexity. **OpenAPI documentation** + URL conventions = почти все benefits.

**Когда useful:**
- State machines (workflow APIs)
- Self-discovering hypermedia clients
- Strict REST compliance (rare)

См. [REST Maturity](rest-maturity-interview.md).


> [!mcq] Когда полная HATEOAS-реализация (hypermedia, link relations, RMM L3) даёт реальный value, а когда — overengineering?
>
> - [ ] Всегда нужна — это требование REST и без неё API не REST
>     - Почему: чистый REST по Roy Fielding включает HATEOAS, но индустрия (Stripe, GitHub, AWS) повсеместно отказалась от полной HATEOAS в пользу OpenAPI + URL conventions; «не настоящий REST» — спор без практической ценности.
>     - Последствие: команда тратит спринты на реализацию `_links` и `application/hal+json`, клиенты их игнорируют, поддержка усложнена без пользы.
>
> - [ ] Никогда не нужна, OpenAPI закрывает 100% use-cases
>     - Почему: для state-machines (approval workflows, order lifecycle) HATEOAS даёт реальный value — клиент видит actual available actions без захардкоженных правил «если status=draft, показать кнопку publish».
>     - Последствие: для workflow API клиенты дублируют state-логику, расхождения с сервером ведут к показу недоступных действий и 403/409 ошибкам.
>
> - [x] Полезна для state-machine/workflow API, где доступные actions зависят от текущего state; для CRUD — overengineering
>     - Почему: HATEOAS-links динамически отражают transitions (`_links.publish`, `_links.cancel`), клиент рендерит UI по актуальным actions; для простого CRUD (users, products) `OpenAPI + URL conventions` дают 95% того же при 10% сложности.
>     - Как работает: в response поле `_links` с разрешёнными переходами: `{"_links": {"approve": {"href": "/orders/123/approve"}, "cancel": {"href": "/orders/123/cancel"}}}`; клиент рендерит кнопки по наличию ключей.
>     - Когда: order management, approval workflows, document lifecycle, payment state machines.
>     - Пример: PayPal API использует HATEOAS для payment-state transitions (`approve`, `capture`, `refund`).
>
> - [ ] HATEOAS нужна для discoverability — клиенты находят новые endpoints автоматически без обновления документации
>     - Почему: реальные клиенты не «discover» endpoints — они написаны под конкретный контракт; idea «self-discovering clients» популярна в академии, но в production-API не подтверждена. OpenAPI + SDK решает discoverability эффективнее.
>     - Последствие: команда вкладывается в discoverability-фичу, которая не востребована; реальные интеграторы используют SDK и OpenAPI explorer.


## Q30. Webhooks design?

**Webhook** — server sends events к customer-provided URL.

**Best practices:**
- **Sign payloads** (HMAC) — verify authenticity
- **Retry** на failures (with exponential backoff)
- **Dead letter** после max retries
- **Idempotency** — same event may be delivered multiple times
- **Event types** (`order.created`, `order.cancelled`)
- **Versioned payloads** (per-customer pinned)
- **Webhook management API** (subscribe, unsubscribe, list)
- **Documentation** с payload examples
- **Testing tools** (webhook tester, ngrok)

**Stripe webhooks** are gold standard — copy that design.


> [!mcq] Что обязательно должно быть на принимающей стороне webhook'а, чтобы интеграция была надёжной в production?
>
> - [ ] Только endpoint с публичным URL и логированием — retry и подпись делает сам отправитель
>     - Почему: подпись (HMAC) проверяется приёмником, иначе любой может подделать webhook и инициировать действия (создать платёж, изменить order); retry от отправителя бесполезен, если receiver не идемпотентен и обрабатывает event несколько раз.
>     - Последствие: атакующий шлёт fake webhook на публичный URL, инициирует business-action; duplicate delivery дублирует charges/orders.
>
> - [ ] Достаточно проверки IP-адреса отправителя в whitelist
>     - Почему: IP-whitelist хрупок (отправитель меняет инфру, NAT, CDN), не защищает от MITM, и не покрывает duplicate delivery. HMAC-подпись + идемпотентность — стандарт индустрии.
>     - Последствие: после миграции отправителя на новые IP интеграция тихо ломается; IP-spoofing на shared infra возможен.
>
> - [x] HMAC-проверка подписи + идемпотентность по `event_id` + быстрый 2xx ответ с фоновой обработкой
>     - Почему: HMAC доказывает отправителя (shared secret в `X-Signature`), идемпотентность по event_id защищает от duplicate delivery (отправитель retry'ит при non-2xx), быстрый 2xx (<5s) предотвращает timeout-retry'и; тяжёлая работа уходит в очередь.
>     - Как работает: receiver проверяет `HMAC_SHA256(secret, body) == header.X-Signature` (с timing-safe compare); сохраняет `event_id` в `processed_events` (INSERT IGNORE); если duplicate — 200 OK без обработки; иначе enqueue в Kafka/SQS и сразу 200.
>     - Когда: любой webhook-receiver (Stripe, GitHub, Twilio, custom B2B).
>     - Пример: Stripe `Stripe-Signature: t=1700000000,v1=abc…` + Idempotency-Key в downstream calls; receiver хранит event_id в Redis SET с TTL=30d.
>
> - [ ] Синхронная обработка прямо в request handler, без очереди, чтобы вернуть результат отправителю
>     - Почему: webhook не ожидает результат бизнес-обработки — только подтверждение получения; синхронная обработка приводит к timeout (отправитель ждёт ~5-10s) и retry-шторму при downstream-падениях.
>     - Последствие: при медленном downstream receiver отвечает >5s → 504/timeout → отправитель retry'ит несколько раз → duplicate processing + cascade failure.


---

## See also

- [REST Maturity](rest-maturity-interview.md) — RMM context
- [API Versioning](api-versioning-interview.md)
- [HTTP & REST](http-rest-interview.md) — основа
- [GraphQL](graphql-interview.md) — alternative
- [gRPC](grpc-interview.md) — alternative
- [OpenAPI / Swagger](openapi-swagger-interview.md) — documentation
- [OAuth2](../security/oauth2-interview.md) — auth
- [JWT](../security/jwt-interview.md) — auth tokens
- [Микросервисы](../architecture/microservices-interview.md) — APIs everywhere
- [API Gateway](../architecture/api-gateway-interview.md) — routing, rate limiting
- [Caching](../architecture/caching-strategies-interview.md) — HTTP caching
- [Application Security](../security/application-security-interview.md) — input validation, HTTPS
