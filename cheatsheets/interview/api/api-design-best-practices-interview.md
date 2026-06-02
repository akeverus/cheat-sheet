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

Проектирование API — критический навык. Хороший API: **интуитивный, консистентный, расширяемый, безопасный**. Плохой API — вечная боль. На интервью спрашивают: соглашения об именовании, пагинацию, фильтрацию, формат ошибок, идемпотентность, rate limiting, аутентификацию. Коллекция лучших практик от Google, Microsoft, Stripe, GitHub.

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

**Используйте существительные во множественном числе:**

```
✓ /users
✓ /orders
✓ /products
✗ /getUsers       # verb
✗ /user           # singular
```

**Иерархия:**
```
✓ /users/123/orders/456
```

**Избегайте глаголов в URL** (вместо них — HTTP-методы):
```
✓ DELETE /users/123     # not POST /users/123/delete
✓ PUT /users/123        # not POST /users/123/update
```

**Исключение:** **действия**, не укладывающиеся в CRUD (см. Q8).

## Q2. (!) URL structure?

```
https://api.example.com/v1/users/123/orders?status=pending&limit=10
       └─────────┘└─┘└────────────────────┘└──────────────────────┘
       host       version  resource path     query string
```

**Лучшие практики:**
- **Поддомен для API** (`api.example.com`)
- **Версия в пути** (`/v1/`)
- **URL в нижнем регистре** (`/users`, а не `/Users`)
- **Дефисы, а не подчёркивания** (`/order-items`, а не `/order_items`)
- **Существительные во множественном числе** для коллекций
- **Идентификаторы в пути** (`/users/123`)
- **Фильтры в query string** (`?status=active`)

## Q3. snake_case vs camelCase в JSON?

**Выберите ОДНО соглашение и придерживайтесь его.**

**snake_case** (более распространён):
```json
{"first_name": "Alice", "created_at": "2025-04-19T10:00:00Z"}
```

**camelCase:**
```json
{"firstName": "Alice", "createdAt": "2025-04-19T10:00:00Z"}
```

**Кто использует:**
- snake_case: Stripe, Slack, Twitter, экосистема Python
- camelCase: Google, Microsoft, экосистема JavaScript

**В Java-бэкенде** — внутри `firstName` (camelCase), сериализация через Jackson `@JsonProperty("first_name")`, если API использует snake_case.

## Q4. Date/time format?

**ISO 8601** — международный стандарт:

```json
"created_at": "2025-04-19T14:30:00Z"           // UTC
"created_at": "2025-04-19T14:30:00+02:00"       // with timezone
```

**Избегайте:**
- Unix-таймстампов (для людей используйте ISO)
- Кастомных форматов («April 19, 2025»)
- Локального времени без указания таймзоны

**В Java:** `Instant`, `OffsetDateTime`, `ZonedDateTime` (НЕ `Date`).

## Q5. (!) Resource hierarchy (parent/child)?

**Иерархия через URL:**
```
/users/123/orders          # all orders для user 123
/users/123/orders/456      # specific order
/orders/456                # also valid (independent access)
```

**Плюсы вложенности:**
- Понятная принадлежность (ownership)
- Скоупинг встроен «из коробки»

**Минусы вложенности:**
- Глубокая вложенность сбивает с толку (`/users/123/orders/456/items/789/refunds/...`)
- Обычно ограничена двумя уровнями

**Лучшая практика:** **максимум 2 уровня** во вложенных URL. Дальше — плоская структура с фильтрами.

## Q6. Sub-resources vs flat structure?

**Под-ресурсы** (вложенные):
```
GET /users/123/orders     # orders for this user
POST /users/123/orders    # create order для user
```

**Плоская структура:**
```
GET /orders?user_id=123
POST /orders {"user_id": 123, ...}
```

**Выбор:**
- **Сильная вложенность** (элемент принадлежит пользователю) → вложенные ресурсы
- **Независимые** сущности → плоская структура

**Оба варианта могут сосуществовать.** Stripe использует и то, и другое: `/customers/cus_123/charges` и `/charges?customer=cus_123`.

## Q7. (!) Bulk operations?

**Зачем:** создать / обновить / удалить несколько ресурсов за один вызов.

**Подходы:**

**1. Передать массив:**
```http
POST /users/batch
[
  {"name": "Alice"},
  {"name": "Bob"}
]
```

**2. Обернуть в объект:**
```http
POST /users
{"users": [{"name": "Alice"}, ...]}
```

**3. Под-ресурс:**
```http
POST /users/bulk-create
{"users": [...]}
```

**На что обратить внимание:**
- **Атомарность** — всё-или-ничего? или частичный успех (partial success)?
- **Формат ответа** — массив результатов, ошибки по каждому элементу?
- **Лимиты** — максимум элементов на один запрос

**Спецификация JSON:API** содержит соглашения для bulk-операций.

## Q8. Custom actions (verbs)?

Иногда действие не вписывается в CRUD (например, «опубликовать статью», «заархивировать заказ»).

**Варианты:**

**1. Под-ресурс:**
```
POST /articles/123/publish
POST /orders/456/cancel
```

**2. Смена состояния через PATCH:**
```
PATCH /orders/456
{"status": "cancelled"}
```

**3. Эндпоинт-действие:**
```
POST /actions/cancel-order
{"order_id": 456}
```

**Лучшая практика:** **под-ресурс** часто ложится естественно. Ограничивайтесь редкими случаями (не скатывайтесь в RPC-стиль).

## Q9. (!) Pagination strategies?

**Всегда пагинируйте** эндпоинты-коллекции (не возвращайте миллион элементов).

**Подходы:**

**Offset/limit:**
```
GET /users?offset=20&limit=10
```

**Page/per_page:**
```
GET /users?page=3&per_page=10
```

**Курсорная (cursor-based):**
```
GET /users?cursor=abc123&limit=10
```

**По времени (time-based):**
```
GET /events?since=2025-04-19T10:00:00Z&limit=100
```

**Лимит по умолчанию** — типично 20-100. Максимум — 100-1000.

## Q10. (!) Cursor-based vs offset-based?

**Offset-based** (`?offset=20&limit=10`):
- ✅ **Проста для понимания**
- ✅ Можно сразу перейти на 5-ю страницу
- ❌ **Медленна на больших датасетах** (`OFFSET 1000000` = скан всех строк)
- ❌ **Неконсистентна** при изменении данных (пропуски/дубликаты элементов)

**Cursor-based** (`?cursor=abc&limit=10`):
- ✅ **Быстра** (использует индексированный курсор)
- ✅ **Консистентна** (нет пропусков/дублей)
- ❌ Нельзя перейти на произвольную страницу
- ❌ Сложнее в реализации

**Курсор подходит для:**
- Больших датасетов
- Real-time-лент
- Высоконагруженных API

**Offset подходит для:**
- Небольших датасетов
- Админок, где нужны переходы по страницам

**Современные API** (Stripe, Shopify, GraphQL Relay) — курсорные.

## Q11. (!) Filtering и sorting?

**Фильтрация:**
```
GET /users?status=active
GET /users?created_after=2025-01-01
GET /users?role=admin&country=US
```

**Сортировка:**
```
GET /users?sort=created_at         # ascending
GET /users?sort=-created_at         # descending (- prefix)
GET /users?sort=name,-created_at   # multiple fields
```

**Продвинутая фильтрация** (RSQL, OData):
```
GET /users?filter=age=gt=18;status==active
```

**Лучшая практика:**
- Простые фильтры — через query-параметры
- Сложные фильтры → эндпоинт `POST /search` с JSON-телом

## Q12. Field selection (sparse fieldsets)?

**Возвращайте только запрошенные поля:**
```
GET /users/123?fields=id,name,email
```

**Ответ:**
```json
{
  "id": 123,
  "name": "Alice",
  "email": "alice@example.com"
}
```

**Эффект:** меньше трафика, быстрее для клиентов, чувствительных к ширине канала.

**В JSON:API для этого есть стандарт:**
```
GET /users?fields[user]=name,email
```

**GraphQL** делает это естественным образом.

## Q13. (!) Error response format?

**Консистентный формат ошибок** критически важен.

**Распространённый паттерн:**
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

**Лучшие практики:**
- **HTTP-статус** (4xx, 5xx)
- **Машиночитаемый код** (`INVALID_PARAMETER`)
- **Человекочитаемое сообщение** (message)
- **Details** для программной обработки
- **Ссылка на документацию**
- **Request ID** (для поддержки)

**Никаких stack trace** в ответах продакшена (безопасность).

## Q14. (!) Problem Details (RFC 7807)?

**RFC 7807** — стандартный формат ошибок.

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

**Stripe, Spring Boot 3+** это поддерживают. Стандартизация упрощает инструментарий.

## Q15. Validation errors?

**Несколько ошибок** в одном ответе (не fail-fast):

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

**Статус:** 400 Bad Request или 422 Unprocessable Entity.

**Возвращайте ВСЕ ошибки** сразу (лучше UX для форм).

## Q16. (!) Idempotency keys?

**Сделайте POST идемпотентным** через ключ, передаваемый клиентом.

```http
POST /payments HTTP/1.1
Idempotency-Key: 7f9c1d-...
{"amount": 100}
```

**Логика сервера:**
1. Проверить, не обработан ли ключ ранее
2. Если да — вернуть тот же ответ (из кэша)
3. Если нет — обработать, сохранить результат

**Сценарий:** повтор при сетевых сбоях без двойного списания.

**Stripe** — канонический пример.

**Хранилище:** Redis с TTL (типично 24 часа).

## Q17. Optimistic concurrency (ETag)?

**ETag** — идентификатор версии ресурса.

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

**Предотвращает** потерянные обновления (конкурентные изменения).

**Эффект:**
- Клиент A читает (ETag "abc123")
- Клиент B читает (ETag "abc123")
- Клиент A обновляет (успех, новый ETag "def456")
- Клиент B обновляет (412 Precondition Failed — надо перечитать)

**Альтернатива:** поля версии (`{"version": 1, ...}`).

## Q18. (!) Auth strategies (API key, JWT, OAuth)?

**API-ключ:**
- Прост, распространён для server-to-server
- `Authorization: Bearer ak_test_...`
- Обычно без срока истечения
- Ограниченный скоуп (или полный доступ)

**JWT (JSON Web Token):**
- Самодостаточен (claims внутри)
- Stateless (без обращения к серверу)
- Срок истечения встроен
- Подходит для аутентификации пользователей

**OAuth 2.0:**
- Стандарт для доступа сторонних приложений
- Authorization Code flow — для пользователей
- Client Credentials — для сервисных аккаунтов
- Refresh-токены — для долгоживущих сессий

**Выбор:**
- **Внутренние сервисы** — API-ключи или mTLS
- **Обращённые к пользователю** — OAuth + JWT
- **Публичные API** — API-ключи (просто) или OAuth (продвинуто)

Подробнее — в [OAuth2](../security/oauth2-interview.md), [JWT](../security/jwt-interview.md).

## Q19. API key best practices?

1. **Префиксы в ключах** (`sk_live_`, `pk_test_`) — тип виден с первого взгляда
2. **Длинные случайные** (32+ байт)
3. **Скоупированные права** (read-only, конкретные ресурсы)
4. **Отзываемые** (должна быть возможность мгновенно инвалидировать)
5. **Ротируемые** (обновление без простоя)
6. **На каждое окружение** (test, live)
7. **Аудит-логирование** (кто и когда использовал)
8. **Хеш в БД** (не хранить в открытом виде)
9. **Показывать один раз** (только при создании)
10. **Rate limiting** на каждый ключ

**Паттерн Stripe:** `sk_live_abc123...` — по префиксу сразу понятно secret/publishable и test/live.

## Q20. (!) Rate limiting headers?

**Сообщайте клиенту о лимитах:**

```http
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 47
X-RateLimit-Reset: 1714579200    # Unix timestamp
```

**RFC 9239 (новее):**
```http
RateLimit-Limit: 100
RateLimit-Remaining: 47
RateLimit-Reset: 60
```

**При срабатывании лимита:**
```http
HTTP/1.1 429 Too Many Requests
Retry-After: 60
```

**Клиент должен:**
- Следить за заголовками
- Делать backoff до достижения лимита
- Уважать `Retry-After` после 429

## Q21. Throttling vs hard limits?

**Троттлинг** (мягкий) — замедление запросов.
**Жёсткий лимит** — отказ (429).

**Реализация:**
- **Token bucket** (популярно)
- **Sliding window**
- **Fixed window**

**В разрезе:**
- API-ключа
- IP-адреса
- Пользователя
- Эндпоинта

**Модель Stripe:** разные лимиты на разные эндпоинты (создание пользователей против чтения).

**Инструменты:** API Gateway (Kong, Apigee), Nginx, Envoy, код приложения.

## Q22. (!) OpenAPI / Swagger?

**OpenAPI** (бывший Swagger) — стандарт для документирования API.

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

**Инструменты:**
- **Swagger UI** — интерактивная документация
- **Redoc** — альтернативный UI
- **Кодогенераторы** — клиентские SDK из спецификации
- **Валидаторы** — проверка запросов/ответов

**Лучшая практика:** **сначала спецификация API, потом реализация**. OpenAPI = источник истины.

Подробнее — в [OpenAPI / Swagger](openapi-swagger-interview.md).

## Q23. Examples и SDKs?

**Всегда предоставляйте:**
- **Примеры кода** (curl, Python, JavaScript, Java, ...)
- **Коллекцию Postman** или экспорты Insomnia
- **SDK** для основных языков (автогенерация из OpenAPI)

**SDK снижают порог входа:** разработчикам не нужно писать HTTP-код с нуля.

**Инструменты автогенерации:** OpenAPI Generator, Speakeasy, Stainless.

## Q24. (!) HTTPS only?

**Всегда.** Никакого HTTP в продакшене.

```
HTTP/1.1 308 Permanent Redirect
Location: https://api.example.com/...
```

**Или отдавайте 400/426** (не редиректьте — клиенты могут утечь секреты).

**Заголовок HSTS:**
```http
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
```

**TLS 1.2 минимум** (рекомендуется 1.3).

## Q25. Input validation, output encoding?

**Валидация входных данных:**
- Схема (OpenAPI или JSON Schema)
- Проверка типов
- Проверка диапазонов
- Формат (email, URL, UUID)
- Ограничения длины
- Защита от SQL-инъекций (параметризованные запросы)

**Кодирование вывода:**
- Экранируйте HTML/JS, если возможен HTML-рендеринг
- Санируйте имена файлов в URL

Подробнее — в [Application Security](../security/application-security-interview.md).

## Q26. CORS?

**Cross-Origin Resource Sharing** — браузерная безопасность.

```http
Access-Control-Allow-Origin: https://myapp.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 3600
```

**Preflight-запрос OPTIONS** перед фактическим запросом.

**Лучшая практика:** указывайте точные origin, а не `*` для API с аутентификацией.

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

**Уровни кэширования:**
- Кэш браузера
- CDN
- API gateway
- Кэш приложения (Redis)

**Сценарий:** условно статичные данные (профиль пользователя, каталог товаров).

## Q28. Compression (gzip, brotli)?

**Клиент запрашивает:**
```http
Accept-Encoding: gzip, br
```

**Сервер отвечает:**
```http
Content-Encoding: gzip
```

**Эффект:** сокращение на 60-90% для JSON/текста.

**Brotli** > **gzip** (лучше сжатие, чуть больше CPU).

**Большинство фреймворков** обрабатывают сжатие автоматически.

## Q29. (!) HATEOAS — нужно?

**В большинстве** API — **нет**.

**Чистый подход HATEOAS** редко оправдывает свою сложность. **Документация OpenAPI** + соглашения об URL дают почти все преимущества.

**Когда полезно:**
- Конечные автоматы (workflow-API)
- Самообнаруживающиеся гипермедиа-клиенты
- Строгое соответствие REST (редко)

См. [REST Maturity](rest-maturity-interview.md).

## Q30. Webhooks design?

**Webhook** — сервер отправляет события на URL, заданный клиентом.

**Лучшие практики:**
- **Подписывайте payload** (HMAC) — проверка подлинности
- **Повторяйте** при сбоях (с экспоненциальным backoff)
- **Dead letter** после максимума попыток
- **Идемпотентность** — одно событие может прийти несколько раз
- **Типы событий** (`order.created`, `order.cancelled`)
- **Версионированные payload** (закреплены за каждым клиентом)
- **API управления вебхуками** (subscribe, unsubscribe, list)
- **Документация** с примерами payload
- **Инструменты тестирования** (webhook tester, ngrok)

**Вебхуки Stripe** — золотой стандарт, копируйте этот дизайн.

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
