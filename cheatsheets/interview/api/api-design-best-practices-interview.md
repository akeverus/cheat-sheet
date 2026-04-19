---
title: "Вопросы на собеседовании: API Design Best Practices"
description: "API design: naming conventions, resource modeling, pagination, filtering, sorting, error responses, idempotency, rate limiting, authentication, JSON conventions, OpenAPI"
tags:
  - interview
  - api
  - api-design-best-practices-interview
aliases:
  - "API design interview"
  - "API best practices interview"
  - "REST API design"
  - "API patterns interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `API Design Best Practices`

API design — критическое skill. Хороший API: **intuitive, consistent, evolvable, secure**. Плохой API — eternal pain. На интервью спрашивают: naming conventions, pagination, filtering, error format, idempotency, rate limiting, authentication. Коллекция best practices от Google, Microsoft, Stripe, GitHub.

Дата последнего обновления: 2026-04-19

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

## Q23. Examples и SDKs?

**Always provide:**
- **Code examples** (curl, Python, JavaScript, Java, ...)
- **Postman collection** или Insomnia exports
- **SDKs** для major languages (auto-generated from OpenAPI)

**SDKs reduce friction:** developers don't write HTTP code from scratch.

**Auto-gen tools:** OpenAPI Generator, Speakeasy, Stainless.

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

## Q29. (!) HATEOAS — нужно?

**В большинстве** API — **нет**.

**HATEOAS pure approach** rarely justifies complexity. **OpenAPI documentation** + URL conventions = почти все benefits.

**Когда useful:**
- State machines (workflow APIs)
- Self-discovering hypermedia clients
- Strict REST compliance (rare)

См. [REST Maturity](rest-maturity-interview.md).

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
