---
title: "Вопросы на собеседовании: Richardson Maturity Model (REST)"
description: "Richardson Maturity Model: 4 уровня REST API (0-3), HATEOAS, hypermedia, использование HTTP verbs и status codes, что значит truly RESTful, критика модели"
tags:
  - interview
  - api
  - rest-maturity-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Richardson Maturity Model"
  - "REST"
  - "REST maturity interview"
prerequisites: []
next: []
updated: "2026-05-29"
---
# Вопросы на собеседовании: `Richardson Maturity Model (REST)`

`Richardson Maturity Model` (Leonard Richardson, 2008) — модель оценки **степени соответствия REST** для API, 4 уровня (0-3). Уровень 3 (HATEOAS) — «truly RESTful» по Roy Fielding. На практике большинство «REST API» застревают на Level 2.

Модель помогает понять, что значит REST глубже, чем просто «JSON поверх HTTP».

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Richardson Maturity Model — Martin Fowler](https://martinfowler.com/articles/richardsonMaturityModel.html)
- [REST APIs must be hypertext-driven (Roy Fielding)](https://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven)
- [HATEOAS Spec](https://en.wikipedia.org/wiki/HATEOAS)
- [HAL Specification](https://datatracker.ietf.org/doc/html/draft-kelly-json-hal)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Richardson Maturity Model?](#q1--что-такое-richardson-maturity-model)
- [Q2. (!) Зачем модель нужна?](#q2--зачем-модель-нужна)

**Уровни**
- [Q3. (!) Level 0: The Swamp of POX?](#q3--level-0-the-swamp-of-pox)
- [Q4. (!) Level 1: Resources?](#q4--level-1-resources)
- [Q5. (!) Level 2: HTTP Verbs + Status Codes?](#q5--level-2-http-verbs--status-codes)
- [Q6. (!) Level 3: Hypermedia Controls (HATEOAS)?](#q6--level-3-hypermedia-controls-hateoas)

**HATEOAS подробно**
- [Q7. (!) Что такое HATEOAS?](#q7--что-такое-hateoas)
- [Q8. (!) Hypermedia формат (HAL, JSON:API, Siren)?](#q8--hypermedia-формат-hal-jsonapi-siren)
- [Q9. Преимущества HATEOAS?](#q9-преимущества-hateoas)
- [Q10. (!) Почему HATEOAS редко применяется?](#q10--почему-hateoas-редко-применяется)

**Детали HTTP**
- [Q11. (!) HTTP verbs: GET, POST, PUT, PATCH, DELETE?](#q11--http-verbs-get-post-put-patch-delete)
- [Q12. (!) HTTP status codes по категориям?](#q12--http-status-codes-по-категориям)
- [Q13. Idempotency версов?](#q13-idempotency-версов)
- [Q14. Safe vs unsafe методы?](#q14-safe-vs-unsafe-методы)

**Критика**
- [Q15. (!) Критика модели (Roy Fielding)?](#q15--критика-модели-roy-fielding)
- [Q16. Pragmatic REST vs idealistic REST?](#q16-pragmatic-rest-vs-idealistic-rest)
- [Q17. (!) GraphQL, gRPC vs REST?](#q17--graphql-grpc-vs-rest)

## Q1. (!) Что такое Richardson Maturity Model?

**Richardson Maturity Model (RMM)** — модель оценки RESTful API через 4 уровня.

**Уровни:**

| Level | Название | Что добавляет |
|-------|----------|---------------|
| **0** | The Swamp of POX | HTTP только как транспорт (RPC-стиль) |
| **1** | Resources | Отдельный URI на каждый ресурс |
| **2** | HTTP Verbs | Семантика HTTP (GET, POST, коды статусов) |
| **3** | Hypermedia Controls (HATEOAS) | Ссылки для навигации прямо в ответах |

**Автор:** Leonard Richardson, популяризировал Martin Fowler.

## Q2. (!) Зачем модель нужна?

Разводит разные утверждения о «REST API»:

- многие называют REST даже Level 0-1;
- **Level 2** — типичный прагматичный REST;
- **Level 3** — академически «truly RESTful».

Помогает глубже понять предмет:

- что вообще значит «RESTful»;
- почему важны HTTP-глаголы и коды статусов;
- какие следствия даёт HATEOAS.

**Ничего не предписывает** — это описательная, а не нормативная модель.

## Q3. (!) Level 0: The Swamp of POX?

**POX = Plain Old XML.** RPC-стиль поверх HTTP.

```http
POST /endpoint HTTP/1.1

<request>
  <method>getUser</method>
  <id>123</id>
</request>
```

**Характеристики:**
- **один URL** (всё туннелируется через POST);
- **все запросы POST** (нет GET/PUT/DELETE);
- HTTP — только транспорт;
- семантика метода зашита в тело запроса.

**Примеры:** SOAP, XML-RPC, JSON-RPC.

**Совсем не RESTful** — но технически использует HTTP.

## Q4. (!) Level 1: Resources?

**Отдельные URI** под каждый ресурс.

```http
POST /users          # не /endpoint
POST /orders         # не /endpoint
POST /products       # не /endpoint
```

**По-прежнему всё POST**, но **URL на каждый ресурс**.

**Эффект:**
- лучше организация API;
- появляется кэширование (у ресурсов есть URL);
- проще исследовать API.

**Большой шаг вперёд** относительно Level 0.

## Q5. (!) Level 2: HTTP Verbs + Status Codes?

**Используем семантику HTTP правильно.**

```http
GET    /users/123     # получить
POST   /users         # создать
PUT    /users/123     # полное обновление / создание
PATCH  /users/123     # частичное обновление
DELETE /users/123     # удалить

# Status codes
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

**Эффект:**
- **кэширование** работает (GET кэшируется);
- **идемпотентность** — клиент знает, что PUT/DELETE безопасно повторять;
- **стандартный инструментарий** (браузеры, прокси, CDN).

**Большинство «REST API»** = **Level 2**. Разумный баланс практичности и REST.

## Q6. (!) Level 3: Hypermedia Controls (HATEOAS)?

**HATEOAS = Hypermedia as the Engine of Application State.**

**Ответы содержат ссылки** — клиент обнаруживает действия по ним, а не хардкодит URL.

```json
{
  "id": 123,
  "name": "Alice",
  "_links": {
    "self": { "href": "/users/123" },
    "orders": { "href": "/users/123/orders" },
    "update": { "href": "/users/123", "method": "PUT" },
    "delete": { "href": "/users/123", "method": "DELETE" }
  }
}
```

**Навигация клиента:**
1. стартует с известной точки входа;
2. переходит по ссылкам к ресурсам;
3. использует ссылки для действий.

**В отличие от хардкода URL:** навигацией управляет сервер. Он может переименовывать URL и менять структуру ресурсов — клиенты продолжают работать.

## Q7. (!) Что такое HATEOAS?

**HATEOAS** — клиент перемещается по состояниям приложения через гипермедиа-ссылки, которые отдаёт сервер.

**Аналогия:** просмотр веба. Вы кликаете по ссылкам, а не набираете полные URL руками. Сервер сам подсказывает, куда идти дальше.

**Без HATEOAS (обычный API):**
- клиент заранее знает ВСЕ URL (`/users/{id}`, `/users/{id}/orders`, ...);
- сильная связанность;
- изменения на сервере ломают клиентов.

**С HATEOAS:**
- клиент знает только точку входа (`/`);
- всё остальное обнаруживает по ссылкам;
- сервер может эволюционировать, не ломая клиентов.

## Q8. (!) Hypermedia формат (HAL, JSON:API, Siren)?

**HAL (Hypertext Application Language):**
```json
{
  "id": 123,
  "name": "Alice",
  "_links": {
    "self": { "href": "/users/123" },
    "orders": { "href": "/users/123/orders" }
  },
  "_embedded": {
    "address": { ... }
  }
}
```

**JSON:API:**
```json
{
  "data": {
    "type": "user",
    "id": "123",
    "attributes": { "name": "Alice" },
    "relationships": {
      "orders": {
        "links": { "related": "/users/123/orders" }
      }
    }
  }
}
```

**Siren** — действия и сущности:
```json
{
  "class": ["user"],
  "properties": { "name": "Alice" },
  "actions": [
    { "name": "update", "method": "PUT", "href": "/users/123" }
  ],
  "links": [...]
}
```

**Spring HATEOAS, RestEasy** — реализации на Java.

## Q9. Преимущества HATEOAS?

1. **Слабая связанность** — клиенты знают только точку входа.
2. **Эволюция сервера** — можно менять URL, не ломая клиентов.
3. **Обнаруживаемость** — API самодокументируется.
4. **Машина состояний прямо в ссылках** — присутствуют только допустимые действия.
5. **Проще версионирование** — ссылки несут информацию о версии.

**Пример машины состояний:**
```json
// Заказ в состоянии PENDING
{
  "_links": {
    "approve": { "href": "/orders/123/approve" },
    "cancel": { "href": "/orders/123/cancel" }
  }
}

// Заказ в состоянии SHIPPED
{
  "_links": {
    "track": { "href": "/orders/123/tracking" }
    // нет approve/cancel — эти действия недопустимы
  }
}
```

**Клиент видит доступные действия** прямо по ссылкам, без дублирования бизнес-логики.

## Q10. (!) Почему HATEOAS редко применяется?

**Реальность 2025:** доминирует **Level 2**, HATEOAS встречается редко.

**Причины:**

1. **Сложность клиента** — клиенту приходится парсить ссылки.
2. **Нет единого стандарта** — HAL vs JSON:API vs Siren, фрагментация.
3. **Версионирование** — встроенные ссылки не отменяют необходимость версий.
4. **Не хватает инструментов** — фронтенд предпочитает хардкодить пути.
5. **Производительность** — ссылки раздувают payload.
6. **Потребители хотят OpenAPI** — схемы, а не обнаружение в рантайме.
7. **GraphQL** решает часть задач HATEOAS иначе.

**Консенсус:** HATEOAS академически чист, но прагматичный дизайн API = Level 2 + документация OpenAPI.

## Q11. (!) HTTP verbs: GET, POST, PUT, PATCH, DELETE?

| Метод | Семантика | Идемпотентный | Безопасный |
|-------|-----------|---------------|------------|
| `GET` | Получить | **Да** | **Да** |
| `POST` | Создать / произвольное действие | Нет | Нет |
| `PUT` | Полная замена / создание | **Да** | Нет |
| `PATCH` | Частичное обновление | Иногда | Нет |
| `DELETE` | Удалить | **Да** | Нет |
| `HEAD` | Как GET, но без тела | Да | Да |
| `OPTIONS` | Узнать разрешённые методы | Да | Да |

**Идемпотентный** — несколько одинаковых запросов дают тот же эффект.
**Безопасный** — не меняет состояние на сервере.

**PUT vs PATCH:**
- PUT: отправляем **весь** ресурс, он замещается целиком;
- PATCH: отправляем **только изменения**.

## Q12. (!) HTTP status codes по категориям?

**1xx — информационные** (редко)

**2xx — успех:**
- 200 OK
- 201 Created (POST создал новый ресурс)
- 202 Accepted (запущена асинхронная обработка)
- 204 No Content (успех, тела нет)

**3xx — перенаправления:**
- 301 Moved Permanently
- 304 Not Modified (кэш актуален)

**4xx — ошибка клиента:**
- 400 Bad Request (не прошла валидация)
- 401 Unauthorized (нет аутентификации)
- 403 Forbidden (аутентификация есть, но доступ запрещён)
- 404 Not Found
- 405 Method Not Allowed
- 409 Conflict (например, несовпадение версии ресурса)
- 422 Unprocessable Entity (ошибки валидации)
- 429 Too Many Requests (превышен лимит)

**5xx — ошибка сервера:**
- 500 Internal Server Error
- 502 Bad Gateway
- 503 Service Unavailable
- 504 Gateway Timeout

**Не используйте 200** на всё подряд. Правильные коды информируют клиентов и прокси.

## Q13. Idempotency версов?

**Идемпотентные методы:** GET, HEAD, OPTIONS, PUT, DELETE.
**Неидемпотентные:** POST (и обычно PATCH).

**Пример идемпотентности:**
```
DELETE /users/123 (первый раз → 204 No Content)
DELETE /users/123 (второй раз → 404 или 204) — конечное состояние то же
```

**POST создаёт новый ресурс** каждый раз → не идемпотентен.

**Как сделать POST идемпотентным:**
- **ключи идемпотентности** — заголовок `Idempotency-Key: abc123`;
- сервер сохраняет ключ и возвращает тот же ответ на повторный запрос.

```http
POST /payments HTTP/1.1
Idempotency-Key: 7f9c1d-...

{"amount": 100}
```

## Q14. Safe vs unsafe методы?

**Safe (безопасные)** — не меняют состояние: **GET, HEAD, OPTIONS**.

**Unsafe (небезопасные)** — меняют состояние: **POST, PUT, PATCH, DELETE**.

**Эффект:**
- **CDN и прокси кэшируют** безопасные методы;
- **краулеры** могут переходить по безопасным ссылкам;
- **браузер предзагружает** безопасные URL.

**Критично:** никогда не используйте GET для действий с побочными эффектами (удаление, изменение).

```
ПЛОХО:   GET /users/123/delete
ХОРОШО:  DELETE /users/123
```

## Q15. (!) Критика модели (Roy Fielding)?

**Roy Fielding** (создатель REST, диссертация 2000 года):
> "If the engine of application state (and hence the API) is not being driven by hypertext, then it cannot be RESTful."

**Только Level 3** — настоящий REST по Fielding.

**Критика самой RMM:**
- лестница зрелости намекает «выше = лучше», что верно не всегда;
- Level 2 — прагматичный стандарт, а не провал;
- уровни не являются универсальной целью.

**Большинство современных API** = Level 2 + OpenAPI/Swagger. Индустрия выбирает прагматику, а не чистоту.

## Q16. Pragmatic REST vs idealistic REST?

**Прагматичный (RMM Level 2):**
- HTTP-глаголы и коды статусов;
- чистые URI;
- тела в JSON;
- документация OpenAPI.

**Идеалистичный (RMM Level 3):**
- всё перечисленное выше + HATEOAS;
- гипермедиа-формат (HAL и т. п.);
- самообнаружение.

**Реальность 2025:** доминирует **прагматичный** подход. **OpenAPI = де-факто** стандарт документации API, закрывающий часть задач HATEOAS.

## Q17. (!) GraphQL, gRPC vs REST?

**REST (RMM Level 2):**
- на основе HTTP;
- ресурсо-ориентированный;
- много round-trip-ов (over-fetch / under-fetch);
- кэширование естественное (через HTTP).

**GraphQL:**
- единственный эндпоинт;
- клиент сам задаёт, какие данные нужны (нет over/under-fetch);
- строгая схема;
- подписки (реальное время);
- **меньше семантики HTTP**.

**gRPC:**
- бинарный (Protobuf);
- HTTP/2;
- строгая типизация;
- стриминг;
- чаще для **взаимодействия сервис-сервис**;
- **не нативен для браузера**.

**Как выбрать:**
- **публичные API** — REST (привычно, кэшируется);
- **мобильные / сложные запросы** — GraphQL;
- **внутренние микросервисы** — gRPC;
- часто встречается **гибрид** (REST наружу + gRPC внутри).

**Итог:** RMM описывает 4 уровня зрелости REST: Level 0 — RPC поверх HTTP, Level 1 — ресурсы с отдельными URI, Level 2 — корректная семантика HTTP-глаголов и кодов, Level 3 — HATEOAS. На практике стандарт — Level 2 + OpenAPI; HATEOAS академически «чист», но в индустрии редок.

---

## See also

- [HTTP & REST](http-rest-interview.md) — основа
- [GraphQL](graphql-interview.md) — alternative
- [gRPC](grpc-interview.md) — alternative
- [API Versioning](api-versioning-interview.md) — versioning concerns
- [API Design Best Practices](api-design-best-practices-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md) — documentation
- [Микросервисы](../architecture/microservices-interview.md) — APIs context
- [API Gateway](../architecture/api-gateway-interview.md) — context
- [Caching](../architecture/caching-strategies-interview.md) — HTTP caching
