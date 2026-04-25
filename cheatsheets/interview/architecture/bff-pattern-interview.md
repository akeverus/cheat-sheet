---
title: "Вопросы на собеседовании: BFF Pattern"
description: "Backend for Frontend (BFF) pattern: per-client API, aggregation, Netflix/SoundCloud examples, GraphQL as BFF, trade-offs, vs API Gateway, edge BFF"
tags:
  - interview
  - architecture
  - bff-pattern-interview
aliases:
  - "BFF pattern interview"
  - "Backend for Frontend"
  - "BFF architecture"
  - "BFF собеседование"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `BFF Pattern`

`BFF (Backend for Frontend)` — pattern: **separate backend per client type** (web, mobile, TV), tailored для specific UX requirements. Aggregates microservices, shapes responses, hides complexity. Popularized SoundCloud/Netflix. Contrasts с generic API Gateway.

## Полезные ссылки

- [Sam Newman — Backends for Frontends](https://samnewman.io/patterns/architectural/bff/)
- [SoundCloud BFF story](https://philcalcado.com/2015/09/18/the_back_end_for_front_end_pattern_bff.html)
- [Netflix GraphQL Federation](https://netflixtechblog.com/)
- [Microsoft Azure — BFF pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/backends-for-frontends)
- [ThoughtWorks technology radar — BFF](https://www.thoughtworks.com/radar/techniques/backend-for-frontends)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое BFF pattern?](#q1--что-такое-bff-pattern)
- [Q2. (!) Зачем BFF — проблема, которую решает?](#q2--зачем-bff--проблема-которую-решает)
- [Q3. (!) BFF vs API Gateway?](#q3--bff-vs-api-gateway)

**Architecture**
- [Q4. (!) Architecture с BFF?](#q4--architecture-с-bff)
- [Q5. Сколько BFF нужно?](#q5-сколько-bff-нужно)
- [Q6. Technology stack для BFF?](#q6-technology-stack-для-bff)

**Responsibilities**
- [Q7. (!) Что должен делать BFF?](#q7--что-должен-делать-bff)
- [Q8. (!) Что НЕ должен делать BFF?](#q8--что-не-должен-делать-bff)

**GraphQL**
- [Q9. (!) GraphQL as BFF?](#q9--graphql-as-bff)
- [Q10. GraphQL Federation?](#q10-graphql-federation)

**Trade-offs**
- [Q11. (!) Преимущества BFF?](#q11--преимущества-bff)
- [Q12. (!) Недостатки BFF?](#q12--недостатки-bff)
- [Q13. Code duplication между BFFs?](#q13-code-duplication-между-bffs)

**Production**
- [Q14. (!) Ownership — кто пишет BFF?](#q14--ownership--кто-пишет-bff)
- [Q15. Caching в BFF?](#q15-caching-в-bff)
- [Q16. BFF at edge (Cloudflare, Vercel)?](#q16-bff-at-edge-cloudflare-vercel)
- [Q17. Testing BFF?](#q17-testing-bff)

## Q1. (!) Что такое BFF pattern?

**BFF (Backend for Frontend):** dedicated API service tailored для specific client (web, iOS, Android, TV).

**Core idea:** instead of single generic API serving all clients, have **one BFF per client type**. Each BFF:
- Talks к same underlying microservices
- Aggregates / transforms data для что client needs
- Optimized под UI requirements of that client

**Diagram:**
```
[Web UI] ←→ [Web BFF]      ┐
[iOS App] ←→ [iOS BFF]      │→ [Microservices: User, Product, Order, Inventory, ...]
[Android] ←→ [Android BFF]  │
[Smart TV] ←→ [TV BFF]      ┘
```

**Vs monolithic API:**
- One API tried to serve all → clients make multiple calls, discard data они не использует

**Coined by SoundCloud engineers (2015):** solved problem where Android app needed different data shapes than web.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Зачем BFF — проблема, которую решает? Частая ошибка в реальном коде.

**Problems without BFF (generic API):**

**1. Over-fetching:**
- Mobile needs 3 fields; API returns 30
- Waste bandwidth (critical on mobile/slow networks)

**2. Under-fetching (N+1):**
- Client needs data from 5 services
- Makes 5 HTTP calls → 5 round trips (very slow on mobile)

**3. Client-specific features конфликтуют:**
- TV: needs simpler navigation, sparse data
- Web: rich interactivity
- One API can't satisfy both cleanly

**4. Client coupling:**
- Change API → break clients
- Versioning painful

**5. Generic API fat от all client needs:**
- Accumulates every field anyone asked for
- Unclear ownership

**BFF solves:**
- Each client gets tailored responses
- Single call aggregates backend data (fewer round trips)
- Changes к client = change only its BFF
- Clean contract per client


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) BFF vs API Gateway? Частая ошибка в реальном коде.

**API Gateway:** general-purpose edge proxy.
- Auth, rate limit, logging, routing
- Generic — serves all clients same way
- Cross-cutting concerns

**BFF:** client-specific aggregation layer.
- Tailored response shapes
- Business logic (composing услуг)
- Per-client

**Comparison:**

| Aspect | API Gateway | BFF |
|--------|-------------|-----|
| Scope | All clients | One client type |
| Logic | Cross-cutting (auth, rate) | Aggregation, shaping |
| Per client | No | Yes |
| Business logic | No | Some |
| Owner | Platform team | Client team |

**Can coexist:**
```
Client → API Gateway → BFF → Microservices
```

- Gateway: auth, WAF, rate limit
- BFF: assemble response

**Simplistic cases:**
- Just need routing/auth? API Gateway enough
- Different responses per client? Add BFF


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) Architecture с BFF? Частая ошибка в реальном коде.

```
Web browser ─────→ Web BFF (Node.js)
Mobile app (iOS) ─→ iOS BFF (Kotlin/Java) ─→ [Auth]
Mobile app (Android) → Android BFF         ─→ [User Service]
Third party API ──→ Partner BFF            ─→ [Product Service]
                                           ─→ [Order Service]
                                           ─→ [Inventory Service]
                                           ─→ [Recommendation Service]
```

**Each BFF:**
- HTTP server (REST или GraphQL)
- Calls multiple internal services (HTTP/gRPC)
- Aggregates, transforms
- Returns client-shaped response

**Internal services:**
- Focused на domain
- Don't know about clients
- Called by any BFF

**Example flow (mobile home screen):**
1. User opens app → BFF `GET /home`
2. Mobile BFF parallel calls:
   - User Service: profile
   - Feed Service: top 20 items
   - Notification Service: unread count
3. BFF composes single response: `{user, feed, notifications: {unread: 5}}`
4. Mobile renders; one round trip


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Сколько BFF нужно? Частая ошибка в реальном коде.

**Count:** per **client experience**, not per technology.

**Common splits:**
- Web + Mobile (2 BFFs)
- Web + iOS + Android (3)
- Web + Mobile + TV + Partner API (4)

**Guidelines:**
- Different UX → separate BFF
- Same UX, different tech (React native iOS+Android) → maybe one BFF
- Business partner integration → typically own BFF (different auth, rate)

**Anti-patterns:**
- Too many (each screen own BFF) → operational overhead
- Too few (one BFF for все) → same generic API problem

**Trick:** start с one BFF; split when diverging requirements cause friction.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Technology stack для BFF? Частая ошибка в реальном коде.

**BFF should be lightweight, I/O-heavy:**
- Node.js (typical — matches web/mobile team skills)
- Kotlin (typed, good for mobile teams)
- Go (performance)
- Typescript universally

**Framework choices:**
- Node.js: Express, Fastify, NestJS
- Java/Kotlin: Spring Boot (reactive preferred)
- GraphQL: Apollo Server, Netflix DGS

**Why I/O-heavy matters:**
- BFF mostly forwarding + aggregating
- CPU light
- Async I/O critical

**Same language as client team:**
- iOS team writes iOS BFF (usually Node или Kotlin)
- Team owns end-to-end stack


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) Что должен делать BFF? Частая ошибка в реальном коде.

**Core responsibilities:**

**1. API aggregation:**
- Compose responses from multiple microservices
- Single request to BFF → multiple backend calls

**2. Response shaping:**
- Exclude fields client doesn't need
- Rename для client conventions
- Flatten nested structures

**3. Protocol translation:**
- Internal: gRPC / Protobuf
- External: REST / JSON / GraphQL

**4. Client-specific adaptation:**
- Mobile: smaller payloads
- TV: simpler nav structure
- Web: rich metadata

**5. Client-specific logic:**
- Feature flags per platform
- Compatibility shims

**6. Resilience:**
- Fallbacks when backend fails
- Stale data serving

**7. Caching specific к client:**
- Session state
- Per-device preferences


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Что НЕ должен делать BFF? Частая ошибка в реальном коде.

**Should not:**

**1. Core business logic:**
- Stays в domain services
- BFF is glue, not logic holder

**2. Authentication (fully):**
- Often done in API Gateway before BFF
- BFF may extract user, но not validate tokens

**3. Data persistence:**
- No DB of its own (mostly)
- State в domain services

**4. Cross-client logic:**
- Shared code goes в services, not BFF
- Don't duplicate between BFFs

**5. Heavy compute:**
- CPU-intensive work → dedicated service

**BFF = thin orchestrator, not smart layer.**

**Anti-pattern: "fat BFF"** — BFF becomes monolith over time, absorbing business logic. Refactor back к services.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) GraphQL as BFF? Частая ошибка в реальном коде.

**GraphQL naturally fits BFF role:**
- Client specifies exact shape they want
- No over-fetch, no under-fetch
- Single endpoint

**Implementation:**
- GraphQL server = BFF
- Resolvers call microservices
- Schema defines client-view of domain

**Benefits:**
- No need for multiple endpoints per use case
- Clients evolve UI without backend changes (as long as data available)
- Strong typing

**Drawbacks:**
- Complexity (schema stitching, N+1 resolver problem)
- Caching harder (POST requests, dynamic queries)
- Learning curve

**Per-client BFF still needed?**
- If all clients similar → one GraphQL BFF enough
- If clients very divergent → per-client GraphQL schema

**Caching:**
- Persisted queries (hash) — caches identified queries
- DataLoader pattern — batches + dedupes backend calls


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. GraphQL Federation? Частая ошибка в реальном коде.

**Federation:** multiple teams own parts of single GraphQL schema; composed into federated graph.

**Netflix, GitHub use.**

**Architecture:**
```
[Client] → [GraphQL Gateway] → [User Subgraph] (User team)
                             → [Product Subgraph] (Product team)
                             → [Order Subgraph] (Order team)
```

**Each subgraph:**
- Own GraphQL server
- Owns part of schema
- Deployed independently

**Gateway:**
- Composes from subgraphs
- Plans query across services
- Apollo Federation, GraphQL Mesh

**Vs monolith GraphQL:**
- Federation scales to many teams
- Lower central bottleneck
- Schema evolution distributed

**Vs BFF:**
- BFF: client-driven tailoring
- Federation: domain-driven decomposition
- Can coexist (federated graph accessed by client-specific BFFs или directly)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) Преимущества BFF? Частая ошибка в реальном коде.

**1. Client-specific optimization:**
- Mobile: small payloads
- TV: simplified nav
- Performance per platform

**2. Faster client development:**
- Client team owns BFF → no waiting on backend team
- Iterate independently

**3. Reduced round trips:**
- 1 BFF call instead of 10 direct service calls
- Critical on mobile networks

**4. Clean domain services:**
- Services don't know about clients
- Domain-focused, reusable

**5. Independent evolution:**
- Change one client's BFF без affecting others
- API versioning simpler

**6. Resilience per client:**
- Fallbacks tailored
- Failure modes designed per UX

**7. Security scoping:**
- BFF filters what client can see
- Internal services expose more (trusted network)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Недостатки BFF? Частая ошибка в реальном коде.

**1. Code duplication:**
- Multiple BFFs may have similar aggregation
- Risk: drift (bugs on one BFF, not other)

**2. Operational overhead:**
- N services to deploy, monitor
- Each has own CI/CD, runtime

**3. Ownership confusion:**
- Who owns BFF when web+mobile teams share scope?
- Boundaries unclear

**4. Latency addition:**
- Extra hop (client→BFF→services) adds 5-20ms
- Usually offset by aggregation win

**5. Temptation to add logic:**
- BFF grows into mini-monolith

**6. Repetitive auth / error handling:**
- Need shared middleware libs

**7. Coordination при schema changes:**
- Add field → update all BFFs using it


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Code duplication между BFFs? Частая ошибка в реальном коде.

**Problem:** 3 BFFs make same call к User Service, handle errors same way.

**Solutions:**

**1. Shared libraries:**
- Internal SDK for User Service client
- Shared error handling, logging, retry
- Versioned (semver)

**2. gRPC + generated clients:**
- .proto definitions shared
- Code gen produces typed client
- DRY at call-site

**3. Shared middleware:**
- Auth, logging, tracing — as NPM / Maven package
- Apply universally

**4. Template / scaffold:**
- `create-bff` generator
- New BFF starts with standard middleware

**5. Extract common to gateway:**
- If auth, rate, logging same → do в gateway, BFFs stay thin

**Don't extract business logic:**
- Aggregation logic per-client = OK to duplicate
- Attempt to unify → one BFF again


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) Ownership — кто пишет BFF? Частая ошибка в реальном коде.

**Common pattern:** **frontend / client team owns BFF.**

**Rationale:**
- BFF serves their UI; they know requirements
- Frontend changes often require BFF changes
- Reduces cross-team dependency

**Anti-pattern: backend team owns BFF.**
- Frontend waits для changes
- Backend doesn't understand UX needs well
- Becomes bottleneck

**Skills needed:**
- Node.js / Kotlin / Go — pick stack familiar к team
- Understanding of I/O, async, HTTP/gRPC clients
- Monitoring / observability

**Organizational model:**
- "Full-stack" or "product" team: owns UI + BFF
- Domain teams: own microservices
- Platform team: gateway, shared libs


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Caching в BFF? Частая ошибка в реальном коде.

**Layers:**

**1. Upstream caching:**
- BFF calls services с caching (e.g., HTTP ETag, Redis)
- Reduces backend load

**2. BFF-level cache:**
- Aggregated responses cached (Redis)
- Per-user or anonymous
- TTL short (seconds-minutes)

**3. CDN cache:**
- Public content (no user context)
- Long TTL

**Example:**
- Home page BFF call combines feed + user
- Base feed (not personalized) cached 30s
- Per-user personalization added on top
- Total response не cached (personalized)

**Invalidation:**
- Event-driven (user post → invalidate user's feed cache)
- TTL (simplest)

**Hot vs cold data:**
- Hot (frequent): in-proc cache
- Medium: Redis
- Rarely: always call service


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. BFF at edge (Cloudflare, Vercel)? Частая ошибка в реальном коде.

**Trend:** run BFF at edge for globally low latency.

**Vercel Edge Functions:**
- Next.js app router API routes run at edge
- Server components / server actions

**Cloudflare Workers:**
- BFF in V8 isolate
- 300+ POPs

**Benefits:**
- BFF call к client ~20ms (vs central 100-200ms)
- Backend services called from edge (100-150ms round trip to origin)

**Trade-off:**
- Edge compute limited (30s, 128MB)
- Cold-path to origin services still slow
- Cache at edge amplifies benefit

**Typical edge BFF:**
- Small, thin
- Heavy caching
- Auth, feature flags
- Aggregation of already-cached data

**Pattern:**
```
Client ←20ms→ Edge BFF (cached) ←150ms→ Services (cold path)
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Testing BFF? Частая ошибка в реальном коде.

**Unit tests:**
- Resolver / handler functions
- Mock service clients

**Integration tests:**
- Real HTTP to test services (TestContainers)
- Or WireMock / Mock Service Worker

**Contract tests:**
- BFF ↔ backend services (Pact)
- Prevent breaking changes

**E2E:**
- Browser tests (Cypress, Playwright)
- Verify BFF → client flow works

**Smoke tests в prod:**
- Synthetic checks (hit BFF endpoints continuously)
- Alert on 5xx rise

**Chaos testing:**
- Inject latency / errors в backend
- Verify BFF fallbacks work

**Observability tests:**
- Spans created correctly
- Metrics exposed

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [API Gateway](api-gateway-interview.md) — related but different Частая ошибка в реальном коде.
- [Microservices](microservices-interview.md) — BFF connects them
- [Edge Computing](edge-computing-interview.md) — BFF at edge
- [GraphQL](../api/graphql-interview.md) — often used as BFF
- [[rest-api-design-interview|REST API Design]] — BFF vs public API
- [Caching Strategies](caching-strategies-interview.md) — BFF caching
- [Resilience Patterns](resilience-patterns-interview.md) — fallbacks, timeouts
- [Clean Architecture](clean-architecture-interview.md) — boundary design
- [DDD](ddd-interview.md) — context mapping
- [Scalability Patterns](scalability-patterns-interview.md) — BFF as layer
