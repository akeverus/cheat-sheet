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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Custom actions (verbs)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) Pagination strategies? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. (!) Cursor-based vs offset-based? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. (!) Filtering и sorting? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Field selection (sparse fieldsets)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. (!) Error response format? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Problem Details (RFC 7807)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Validation errors? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. (!) Idempotency keys? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Optimistic concurrency (ETag)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. (!) Auth strategies (API key, JWT, OAuth)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. API key best practices? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. (!) Rate limiting headers? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Throttling vs hard limits? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. (!) OpenAPI / Swagger? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. Examples и SDKs? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Always provide:**
- **Code examples** (curl, Python, JavaScript, Java, ...)
- **Postman collection** или Insomnia exports
- **SDKs** для major languages (auto-generated from OpenAPI)

**SDKs reduce friction:** developers don't write HTTP code from scratch.

**Auto-gen tools:** OpenAPI Generator, Speakeasy, Stainless.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. (!) HTTPS only? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Input validation, output encoding? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. CORS? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Cross-Origin Resource Sharing** — browser security.

```http
Access-Control-Allow-Origin: https://myapp.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 3600
```

**Preflight OPTIONS** request before actual request.

**Best practice:** specify exact origins, не `*` для authenticated APIs.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. (!) Caching headers? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. Compression (gzip, brotli)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. (!) HATEOAS — нужно? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**В большинстве** API — **нет**.

**HATEOAS pure approach** rarely justifies complexity. **OpenAPI documentation** + URL conventions = почти все benefits.

**Когда useful:**
- State machines (workflow APIs)
- Self-discovering hypermedia clients
- Strict REST compliance (rare)

См. [REST Maturity](rest-maturity-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. Webhooks design? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [API Versioning](api-versioning-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [GraphQL](graphql-interview.md)
- [gRPC](grpc-interview.md)
- [HTTP и REST](http-rest-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
