---
title: "Вопросы на собеседовании: Richardson Maturity Model (REST)"
description: "Richardson Maturity Model: 4 уровня REST API (0-3), HATEOAS, hypermedia, использование HTTP verbs и status codes, что значит truly RESTful, критика модели"
tags:
  - interview
  - api
  - rest-maturity-interview
aliases:
  - "REST maturity interview"
  - "Richardson Maturity Model interview"
  - "HATEOAS interview"
  - "REST levels interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Richardson Maturity Model (REST)`

`Richardson Maturity Model` (Leonard Richardson, 2008) — модель оценки **степени RESTful** API, 4 уровня (0-3). Уровень 3 (HATEOAS) — "truly RESTful" по Roy Fielding. На практике большинство "REST APIs" — Level 2. Модель помогает понимать что значит REST глубже, чем "JSON over HTTP".

Дата последнего обновления: 2026-04-19

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

**Levels**
- [Q3. (!) Level 0: The Swamp of POX?](#q3--level-0-the-swamp-of-pox)
- [Q4. (!) Level 1: Resources?](#q4--level-1-resources)
- [Q5. (!) Level 2: HTTP Verbs + Status Codes?](#q5--level-2-http-verbs--status-codes)
- [Q6. (!) Level 3: Hypermedia Controls (HATEOAS)?](#q6--level-3-hypermedia-controls-hateoas)

**HATEOAS deep dive**
- [Q7. (!) Что такое HATEOAS?](#q7--что-такое-hateoas)
- [Q8. (!) Hypermedia формат (HAL, JSON:API, Siren)?](#q8--hypermedia-формат-hal-jsonapi-siren)
- [Q9. Преимущества HATEOAS?](#q9-преимущества-hateoas)
- [Q10. (!) Почему HATEOAS редко применяется?](#q10--почему-hateoas-редко-применяется)

**HTTP details**
- [Q11. (!) HTTP verbs: GET, POST, PUT, PATCH, DELETE?](#q11--http-verbs-get-post-put-patch-delete)
- [Q12. (!) HTTP status codes по категориям?](#q12--http-status-codes-по-категориям)
- [Q13. Idempotency версов?](#q13-idempotency-версов)
- [Q14. Safe vs unsafe методы?](#q14-safe-vs-unsafe-методы)

**Critique**
- [Q15. (!) Критика модели (Roy Fielding)?](#q15--критика-модели-roy-fielding)
- [Q16. Pragmatic REST vs idealistic REST?](#q16-pragmatic-rest-vs-idealistic-rest)
- [Q17. (!) GraphQL, gRPC vs REST?](#q17--graphql-grpc-vs-rest)

## Q1. (!) Что такое Richardson Maturity Model?

**Richardson Maturity Model (RMM)** — модель оценки RESTful API через 4 уровня.

**Уровни:**

| Level | Name | Что добавляет |
|-------|------|---------------|
| **0** | The Swamp of POX | HTTP только как transport (RPC-style) |
| **1** | Resources | Multiple URIs (per resource) |
| **2** | HTTP Verbs | Use HTTP semantics (GET, POST, status codes) |
| **3** | Hypermedia Controls (HATEOAS) | Discoverable links в responses |

**Created by:** Leonard Richardson, popularized Martin Fowler.

## Q2. (!) Зачем модель нужна?

**Differentiates** "REST API" claims:
- Большинство называют REST даже Level 0-1
- **Level 2** — common pragmatic REST
- **Level 3** — academic "truly RESTful"

**Помогает understand depth:**
- Что значит "RESTful"
- Why HTTP verbs / status codes important
- HATEOAS implications

**Не предписывает** — описательная модель, не prescriptive.

## Q3. (!) Level 0: The Swamp of POX?

**POX = Plain Old XML.** RPC-style over HTTP.

```http
POST /endpoint HTTP/1.1

<request>
  <method>getUser</method>
  <id>123</id>
</request>
```

**Характеристики:**
- **One URL** (тunnel everything через POST)
- **Все POST** (no GET/PUT/DELETE)
- HTTP — только transport
- Method semantics в request body

**Examples:** SOAP, XML-RPC, JSON-RPC.

**Не RESTful at all** — но technically uses HTTP.

## Q4. (!) Level 1: Resources?

**Multiple URIs** для multiple resources.

```http
POST /users          # not /endpoint
POST /orders        # not /endpoint
POST /products      # not /endpoint
```

**Все ещё POST**, но **per-resource URLs**.

**Effect:**
- Better organization
- Caching доступен (resource URLs)
- API exploration easier

**Большой шаг forward** от Level 0.

## Q5. (!) Level 2: HTTP Verbs + Status Codes?

**Use HTTP semantics properly.**

```http
GET    /users/123     # retrieve
POST   /users         # create
PUT    /users/123     # full update / create
PATCH  /users/123     # partial update
DELETE /users/123     # delete

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
- **Caching** работает (GET cacheable)
- **Idempotency** — clients знают safe to retry PUT/DELETE
- **Standard tooling** (browsers, proxies, CDNs)

**Большинство "REST APIs"** = **Level 2**. Reasonable balance practicality + REST.

## Q6. (!) Level 3: Hypermedia Controls (HATEOAS)?

**HATEOAS = Hypermedia as the Engine of Application State.**

**Responses include links** — client discovers actions, не hardcodes URLs.

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

**Client navigation:**
1. Start с known entry point
2. Follow links к resources
3. Use links для actions

**Vs hardcoded URLs:** server controls navigation. Server can rename URLs, change resource layout — clients still work.

## Q7. (!) Что такое HATEOAS?

**HATEOAS** = client navigates application state through hypermedia links provided by server.

**Аналогия:** browsing web. You click links, не type full URLs. Server tells where to go next.

**Без HATEOAS (traditional API):**
- Client knows ALL URLs upfront (`/users/{id}`, `/users/{id}/orders`, ...)
- Tightly coupled
- Server changes break clients

**С HATEOAS:**
- Client knows only entry point (`/`)
- Discovers everything else through links
- Server can evolve без breaking clients

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

**Siren** — actions + entities:
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

**Spring HATEOAS, RestEasy** — Java implementations.

## Q9. Преимущества HATEOAS?

1. **Decoupling** — clients знают только entry point
2. **Server evolution** — change URLs без breaking clients
3. **Discoverability** — API self-documenting
4. **State machine encoded** в links (only valid actions present)
5. **Versioning easier** — links имеют version info

**Example state machine:**
```json
// Order in PENDING state
{
  "_links": {
    "approve": { "href": "/orders/123/approve" },
    "cancel": { "href": "/orders/123/cancel" }
  }
}

// Order in SHIPPED state
{
  "_links": {
    "track": { "href": "/orders/123/tracking" }
    // no approve/cancel — invalid actions
  }
}
```

**Client knows what's possible** через links, not business logic duplication.

## Q10. (!) Почему HATEOAS редко применяется?

**Реальность 2025:** **Level 2** dominates. HATEOAS rarely.

**Причины:**

1. **Client complexity** — clients сложнее (must парсить links)
2. **No standardization** — HAL vs JSON:API vs Siren — fragmentation
3. **Versioning convenience** — embedded links не reduce versioning need
4. **Tooling lacks** — clients (frontend) prefer hardcoded paths
5. **Performance** — extra payload (links добавить bytes)
6. **API consumers want OpenAPI** — schemas, не runtime discovery
7. **GraphQL** addresses some HATEOAS goals differently

**Consensus:** HATEOAS — academically pure, но pragmatic API design = Level 2 + OpenAPI docs.

## Q11. (!) HTTP verbs: GET, POST, PUT, PATCH, DELETE?

| Verb | Semantics | Idempotent | Safe |
|------|-----------|------------|------|
| `GET` | Retrieve | **Yes** | **Yes** |
| `POST` | Create / arbitrary | No | No |
| `PUT` | Full replace / create | **Yes** | No |
| `PATCH` | Partial update | Sometimes | No |
| `DELETE` | Remove | **Yes** | No |
| `HEAD` | Like GET без body | Yes | Yes |
| `OPTIONS` | Discover allowed methods | Yes | Yes |

**Idempotent** — multiple identical requests = same effect.
**Safe** — no server state change.

**PUT vs PATCH:**
- PUT: send **entire** resource, replaces
- PATCH: send **changes** only

## Q12. (!) HTTP status codes по категориям?

**1xx — Informational** (rare)

**2xx — Success:**
- 200 OK
- 201 Created (POST creating new resource)
- 202 Accepted (async processing started)
- 204 No Content (successful, no body)

**3xx — Redirection:**
- 301 Moved Permanently
- 304 Not Modified (cache valid)

**4xx — Client Error:**
- 400 Bad Request (validation failed)
- 401 Unauthorized (no auth)
- 403 Forbidden (auth ok but не allowed)
- 404 Not Found
- 405 Method Not Allowed
- 409 Conflict (e.g., resource version mismatch)
- 422 Unprocessable Entity (validation errors)
- 429 Too Many Requests (rate limit)

**5xx — Server Error:**
- 500 Internal Server Error
- 502 Bad Gateway
- 503 Service Unavailable
- 504 Gateway Timeout

**Don't use 200** для всего. Proper codes inform clients и proxies.

## Q13. Idempotency версов?

**Idempotent verbs:** GET, HEAD, OPTIONS, PUT, DELETE.
**Non-idempotent:** POST, (PATCH usually).

**Idempotent example:**
```
DELETE /users/123 (first time → 204 No Content)
DELETE /users/123 (second time → 404 OR 204) — same end state
```

**POST creates новый** every time → not idempotent.

**Workaround для POST idempotency:**
- **Idempotency keys** — header `Idempotency-Key: abc123`
- Server stores key, returns same response для duplicate request

```http
POST /payments HTTP/1.1
Idempotency-Key: 7f9c1d-...

{"amount": 100}
```

## Q14. Safe vs unsafe методы?

**Safe** — does not modify state. **GET, HEAD, OPTIONS**.

**Unsafe** — modifies state. **POST, PUT, PATCH, DELETE**.

**Эффект:**
- **CDNs / proxies cache** safe methods
- **Crawlers** can follow safe links
- **Browser pre-fetch** safe URLs

**Critical:** never use GET для actions с side effects (deleting, changing).

```
BAD:  GET /users/123/delete
GOOD: DELETE /users/123
```

## Q15. (!) Критика модели (Roy Fielding)?

**Roy Fielding** (REST creator, 2000 PhD thesis):
> "If the engine of application state (and hence the API) is not being driven by hypertext, then it cannot be RESTful."

**Только Level 3** — true REST по Fielding.

**Critique RMM:**
- Maturity ladder implies higher = better, не correct in all contexts
- Level 2 — pragmatic standard, not failure
- Levels не universal goals

**Most modern APIs** = Level 2 + OpenAPI/Swagger. Industry choose pragmatism over purity.

## Q16. Pragmatic REST vs idealistic REST?

**Pragmatic (RMM Level 2):**
- HTTP verbs + status codes
- Clean URIs
- JSON bodies
- OpenAPI documentation

**Idealistic (RMM Level 3):**
- All above + HATEOAS
- Hypermedia format (HAL, etc.)
- Self-discoverable

**Reality 2025:** **Pragmatic** dominates. **OpenAPI = de facto** API documentation standard, replaces some HATEOAS goals.

## Q17. (!) GraphQL, gRPC vs REST?

**REST (RMM Level 2):**
- HTTP-based
- Resource-oriented
- Multiple round trips (over-fetch / under-fetch)
- Caching natural (HTTP)

**GraphQL:**
- Single endpoint
- Client specifies data needs (no over/under-fetch)
- Strong schema
- Subscriptions (real-time)
- **Less HTTP semantics**

**gRPC:**
- Binary (Protobuf)
- HTTP/2
- Strong typing
- Streaming
- **Internal service-to-service** (often)
- **Not browser-native**

**Choice:**
- **Public APIs** — REST (familiar, cacheable)
- **Mobile / complex queries** — GraphQL
- **Internal microservices** — gRPC
- **Hybrid** common (REST public + gRPC internal)

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
