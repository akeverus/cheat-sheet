---
title: "Вопросы на собеседовании: API Versioning"
description: "API versioning стратегии: URI versioning, header versioning, query parameter, content negotiation, semantic versioning, backwards compatibility, deprecation, sunset"
tags:
  - interview
  - api
  - api-versioning-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "API Versioning"
  - "API versioning interview"
  - "API versioning собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `API Versioning`

API versioning — стратегия evolve APIs без breaking existing clients. Главные подходы: **URI versioning** (`/v1/`), **header versioning**, **query parameter**, **content negotiation**. Также: backwards compatibility, deprecation policies, sunset, semantic versioning. Critical для public APIs и multi-team systems.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [API Versioning — Restful API Design](https://restfulapi.net/versioning/)
- [Stripe API Versioning](https://stripe.com/docs/api/versioning)
- [GitHub API Versioning](https://docs.github.com/en/rest/overview/api-versions)
- [Semantic Versioning](https://semver.org/)
- [Sunset HTTP Header (RFC 8594)](https://datatracker.ietf.org/doc/html/rfc8594)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Зачем нужен API versioning?](#q1--зачем-нужен-api-versioning)
- [Q2. (!) Что такое breaking change?](#q2--что-такое-breaking-change)
- [Q3. (!) Backwards-compatible changes — examples?](#q3--backwards-compatible-changes--examples)

**Стратегии**
- [Q4. (!) URI versioning?](#q4--uri-versioning)
- [Q5. (!) Header versioning?](#q5--header-versioning)
- [Q6. (!) Query parameter versioning?](#q6--query-parameter-versioning)
- [Q7. Content negotiation (Accept header)?](#q7-content-negotiation-accept-header)
- [Q8. Hostname-based?](#q8-hostname-based)
- [Q9. (!) Какой подход выбрать?](#q9--какой-подход-выбрать)

**Versioning schemes**
- [Q10. Semantic versioning (SemVer)?](#q10-semantic-versioning-semver)
- [Q11. (!) Date-based versioning (Stripe approach)?](#q11--date-based-versioning-stripe-approach)

**Stripe-style versioning**
- [Q12. (!) Per-account version pinning?](#q12--per-account-version-pinning)
- [Q13. Webhook versioning?](#q13-webhook-versioning)

**GraphQL versioning**
- [Q14. (!) Не versions в GraphQL?](#q14--не-versions-в-graphql)
- [Q15. Field deprecation?](#q15-field-deprecation)

**Deprecation**
- [Q16. (!) Deprecation policy?](#q16--deprecation-policy)
- [Q17. Sunset HTTP header?](#q17-sunset-http-header)
- [Q18. (!) Как gracefully deprecate API?](#q18--как-gracefully-deprecate-api)

**Best practices**
- [Q19. (!) Versioning best practices?](#q19--versioning-best-practices)
- [Q20. Multiple versions параллельно — операционные расходы?](#q20-multiple-versions-параллельно--операционные-расходы)

## Q1. (!) Зачем нужен API versioning?

**Проблема:** API consumers (mobile apps, third parties) **не upgrade синхронно**. Server-side изменения могут break clients.

**Без versioning:**
- Server changes API contract → existing clients fail
- Mobile apps особенно problematic (slow update cycles)
- Breaking changes → customer support nightmare

**С versioning:**
- Multiple versions live параллельно
- Old clients work с old version
- New clients use new version
- Smooth migration

**Public APIs обязательно** versioned. **Internal APIs** sometimes skip (faster iteration).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Что такое breaking change? Частая ошибка в реальном коде.

**Breaking change** — modification, требующая action от existing clients.

**Examples:**
- **Remove field** from response
- **Rename field**
- **Change field type** (string → number)
- **Make optional field required** в request
- **Add new required field** к request
- **Change response structure** (object → array)
- **Change error code semantics**
- **Change authentication scheme**
- **Change URL** (without redirect)

**При breaking change** → **new version** обязателен.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Backwards-compatible changes — examples? Частая ошибка в реальном коде.

**Не breaking (safe в same version):**
- **Add new optional field** к request (clients ignore)
- **Add new field** к response (old clients ignore extra fields)
- **Add new endpoint**
- **Add new optional query parameter**
- **Make required field optional**
- **Add new error codes** (clients should handle unknown codes)

**Tolerant Reader pattern** (Postel's Law):
> "Be conservative in what you send, liberal in what you accept."

Clients should:
- Ignore unknown fields
- Handle missing optional fields
- Handle new error codes gracefully


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) URI versioning? Частая ошибка в реальном коде.

```
https://api.example.com/v1/users/123
https://api.example.com/v2/users/123
```

**Pros:**
- **Simple, visible** in URL
- Easy to route (different versions → different services)
- Easy testing (browser-friendly)
- Clear к users

**Cons:**
- **Resource identity changes** with version (technically "users in v2" different from "users in v1")
- Violates REST purity (URI should identify resource, not version)
- Multiple URLs для same resource

**Most common** approach. Used by Twitter, GitHub (older), Stripe (older).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Header versioning? Частая ошибка в реальном коде.

```http
GET /users/123
Accept: application/vnd.example.v2+json
```

или custom header:
```http
GET /users/123
X-API-Version: 2
```

**Pros:**
- **URI doesn't change** (REST-pure)
- Resource identity preserved
- Easier к add new versions

**Cons:**
- **Less visible** (hard to test в browser)
- Documentation harder
- Cache complications (Vary header)

**Used by:** GitHub (newer API).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Query parameter versioning? Частая ошибка в реальном коде.

```
https://api.example.com/users/123?version=2
https://api.example.com/users/123?api-version=2024-01-15
```

**Pros:**
- Visible
- Easy to test

**Cons:**
- Pollutes query string
- Less convention than URI versioning

**Used by:** Azure APIs (often), Stripe (date-based как parameter).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Content negotiation (Accept header)? Частая ошибка в реальном коде.

```http
GET /users/123
Accept: application/vnd.example.user.v2+json
```

**MIME-type based.** Server returns appropriate version representation.

**Pros:**
- HTTP-standard mechanism
- Resource identity preserved

**Cons:**
- Complex MIME types
- Hard для humans
- Not as common

**Niche** — mostly academic.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Hostname-based? Частая ошибка в реальном коде.

```
https://api-v1.example.com/users/123
https://api-v2.example.com/users/123
```

**Pros:**
- Different deployments per version
- Easy infrastructure separation

**Cons:**
- DNS overhead
- CORS complications

**Used:** rare, when version-specific infra needed.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Какой подход выбрать? Частая ошибка в реальном коде.

**Recommendations:**

| Use case | Approach |
|----------|----------|
| Public API | **URI versioning** (`/v1/`) — simple, visible |
| Internal API | URI или header (consistency matters) |
| Stripe-style stability | Date versions через header |
| GraphQL | **No versions** (deprecation only) |
| gRPC | Package versioning (`MyServiceV2`) |

**Default 2025:** URI versioning (`/v1/`, `/v2/`) — most pragmatic для most cases.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Semantic versioning (SemVer)? Частая ошибка в реальном коде.

`MAJOR.MINOR.PATCH` (e.g., `2.5.3`).

- **MAJOR** — breaking changes
- **MINOR** — new features (backwards compatible)
- **PATCH** — bug fixes

**API versioning ≠ SemVer obычно:**
- API URL: `/v1/` (just MAJOR)
- Documentation tracks MINOR/PATCH

**Library semver**: SemVer применим к SDK / client libraries.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) Date-based versioning (Stripe approach)? Частая ошибка в реальном коде.

**Stripe** uses date-based versions:

```
2024-04-19, 2024-06-30, ...
```

**Header:**
```http
Stripe-Version: 2024-04-19
```

**Each date** = snapshot of API behavior. New dates introduced для breaking changes.

**Pros:**
- Granular versioning (любая мелочь = new date если breaks)
- No "v3" debates (just dates)

**Cons:**
- Many versions to maintain
- Confusing для clients (which date latest?)

**Stripe maintains** **all versions** ever published — clients pin к specific date, never broken.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Per-account version pinning? Частая ошибка в реальном коде.

**Stripe model:** каждый customer pinned к specific API version.

```
Account X: pinned к 2024-04-19
Account Y: pinned к 2024-06-30
Account Z: latest (auto-upgrade)
```

**Effect:**
- Version устанавливается при first API call (или manual)
- Account stays на same version forever (until explicit upgrade)
- New customers — latest by default

**Webhooks** also versioned per-account — webhook payload format consistent для тhat account.

**Result:** Stripe can introduce breaking changes constantly **без breaking anyone**.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Webhook versioning? Частая ошибка в реальном коде.

**Webhooks** harder to version (server pushes, can't negotiate):

**Approaches:**
1. **Per-customer pinned version** (Stripe)
2. **Multiple webhook endpoints** для разных versions
3. **Header в webhook payload** indicating version
4. **Backwards-compatible only** (always add fields, never remove)

**Common pattern:** webhook payloads frozen forever, only add new event types.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) Не versions в GraphQL? Частая ошибка в реальном коде.

**GraphQL philosophy:** **no versioning**.

**Instead:**
- **Add fields** (clients request only what need — additions don't break)
- **Deprecate fields** через `@deprecated`
- **Never remove** (or remove very long after deprecation)

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  username: String @deprecated(reason: "Use 'handle' instead")
  handle: String!  # new field
}
```

**Clients** continue using `username` (works), encouraged migrate к `handle`.

**Eventually** (years) — `username` removed (но disruption minimized — most clients moved).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Field deprecation? Частая ошибка в реальном коде.

```graphql
type Query {
  user(id: ID!): User
}

type User {
  id: ID!
  fullName: String @deprecated(reason: "Use firstName + lastName")
  firstName: String
  lastName: String
}
```

**Tooling:**
- IDE shows strikethrough на deprecated fields
- Linters warn
- API documentation shows deprecation notices


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. (!) Deprecation policy? Частая ошибка в реальном коде.

**Public API deprecation policy** обычно:

1. **Announce deprecation** (release notes, email, blog)
2. **Sunset header** в responses
3. **Deprecation period** — usually 6-24 months
4. **Reminders** к customers
5. **Migration guides** + tooling
6. **Final removal**

**Examples:**
- **Stripe:** never removes (versions live forever)
- **GitHub:** ~12-18 months deprecation
- **Twitter:** ~6 months deprecation


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Sunset HTTP header? Частая ошибка в реальном коде.

**RFC 8594** — `Sunset` header indicates resource will be removed.

```http
HTTP/1.1 200 OK
Sunset: Sat, 31 Dec 2025 23:59:59 GMT
Deprecation: Mon, 01 Jan 2024 00:00:00 GMT
Link: <https://api.example.com/v3/users/123>; rel="successor-version"
```

**Tools** can detect Sunset header → notify developers.

**Best practice:** include в every response для deprecated endpoints.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. (!) Как gracefully deprecate API? Частая ошибка в реальном коде.

**Steps:**

1. **Decide replacement** (новый endpoint / version exists)
2. **Document deprecation** (release notes, docs)
3. **Add Sunset header** к responses
4. **Email API consumers** (multiple times)
5. **Track usage** (analytics на deprecated endpoint)
6. **Provide migration tools** (script, code samples)
7. **Reminders** ближе к sunset date
8. **Disable** (return 410 Gone)
9. **Clean up** server code (eventually)

**Don't surprise** users. Communicate, communicate, communicate.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Versioning best practices? Частая ошибка в реальном коде.

1. **Default к backwards-compatible changes** (no new version needed)
2. **Versioning strategy decided early** (changing later painful)
3. **URI versioning** — simplest для most cases
4. **Document breaking change policy**
5. **Sunset announcements** at least 6 months
6. **OpenAPI spec per version**
7. **API gateway** для routing к correct version
8. **Tests across versions** (catch regressions)
9. **Limit number** of supported versions (3-4 max)
10. **Track adoption** of new versions
11. **Tolerant Reader** pattern на client side
12. **Don't version internal APIs** if possible (faster iteration)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. Multiple versions параллельно — операционные расходы? Частая ошибка в реальном коде.

**Cost of supporting multiple versions:**
- **Code complexity** (branches per version)
- **Testing matrix** (verify all versions)
- **Bug fixes** в multiple branches
- **Documentation maintenance**
- **Deployment complexity**
- **Performance overhead** (translation layer)

**Strategies:**
- **API Gateway translation** — newest version в backend, gateway translates older requests
- **Adapter pattern** — code separation per version
- **Limit** supported versions (sunset old aggressively)

**Stripe-style "all versions forever"** — exception, requires significant engineering investment.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [REST Maturity](rest-maturity-interview.md) — context Частая ошибка в реальном коде.
- [API Design Best Practices](api-design-best-practices-interview.md)
- [HTTP & REST](http-rest-interview.md) — основа
- [GraphQL](graphql-interview.md) — versioning approach differs
- [gRPC](grpc-interview.md) — versioning differs
- [OpenAPI / Swagger](openapi-swagger-interview.md) — documentation
- [Микросервисы](../architecture/microservices-interview.md) — versioning critical
- [API Gateway](../architecture/api-gateway-interview.md) — version routing
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — version rollouts
- [[backwards-compatibility-interview|Backwards Compatibility]] — если будем добавлять
