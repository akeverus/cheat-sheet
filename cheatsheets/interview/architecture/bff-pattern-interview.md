---
title: "Вопросы на собеседовании: BFF Pattern"
description: "Backend for Frontend (BFF) pattern: per-client API, aggregation, Netflix/SoundCloud examples, GraphQL as BFF, trade-offs, vs API Gateway, edge BFF"
tags:
  - interview
  - architecture
  - bff-pattern-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "BFF Pattern"
  - "BFF pattern interview"
  - "Backend for Frontend"
prerequisites: []
next: []
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
> - [ ] BFF = API Gateway с rate limiting — один BFF для всех клиентов | ❌ ПОСЛЕДСТВИЕ: один API для всех клиентов → overfetch на mobile, underfetch на web; BFF = отдельный бэкенд per client type
> - [ ] BFF = микросервис в domain (User BFF, Order BFF) | ❌ ПОСЛЕДСТВИЕ: BFF делится по client type (Web BFF, iOS BFF), не по домену; domain decomposition — это не BFF
> - [x] BFF = отдельный бэкенд per client type (Web/iOS/Android); агрегирует микросервисы, tailors response под UX каждого клиента | ✓ ПРИМЕНЯТЬ: разные клиенты нуждаются в разных формах данных 📋 ПРАВИЛО: один BFF = один client type = tailored API 🔗 См. Q2
> - [ ] BFF = GraphQL schema — все клиенты используют один BFF через разные queries | ❌ ПОСЛЕДСТВИЕ: один GraphQL = всё ещё shared API; BFF предполагает отдельные сервисы per client, GraphQL может реализовать BFF, но не заменяет разделение

## Q2. (!) Зачем BFF — проблема, которую решает?

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
> - [ ] Один BFF для всех клиентов устраняет overfetch через query parameters | ❌ ПОСЛЕДСТВИЕ: query params фильтруют поля, но не решают N+1 round trips; BFF агрегирует несколько сервисов за одним запросом
> - [x] Generic API → overfetch (mobile получает 30 полей вместо 3) + N+1 round trips + client coupling; BFF устраняет все три | ✓ ПРИМЕНЯТЬ: mobile + web + TV требуют разные data shapes 📋 ПРАВИЛО: BFF = no overfetch + single aggregation call + per-client ownership 🔗 См. Q1
> - [ ] BFF решает только overfetch, N+1 решается GraphQL DataLoader | ❌ ПОСЛЕДСТВИЕ: DataLoader в GraphQL — один из способов, но BFF решает оба через aggregation; DataLoader не устраняет client coupling
> - [ ] Версионирование API решает client coupling без BFF | ❌ ПОСЛЕДСТВИЕ: версионирование усиливает coupling — каждый клиент на своей версии → N версий на поддержке; BFF даёт independent evolution

## Q3. (!) BFF vs API Gateway?

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
> - [ ] API Gateway заменяет BFF — достаточно добавить transformation logic | ❌ ПОСЛЕДСТВИЕ: Gateway — cross-cutting (auth, rate limit) для всех; BFF — per-client aggregation; разные ответственности, разные owners
> - [ ] BFF должен содержать бизнес-логику домена (валидация, расчёт цен) | ❌ ПОСЛЕДСТВИЕ: domain logic в BFF → дублирование между BFF-ами; domain logic принадлежит микросервисам
> - [x] API Gateway = auth/routing/rate-limit для всех; BFF = per-client aggregation/shaping; могут сосуществовать: Client → Gateway → BFF → Services | ✓ ПРИМЕНЯТЬ: Gateway для cross-cutting; BFF когда нужна client-specific aggregation 📋 ПРАВИЛО: Gateway = generic edge; BFF = client-specific layer 🔗 См. Q4
> - [ ] BFF и API Gateway — взаимоисключающие паттерны | ❌ ПОСЛЕДСТВИЕ: Netflix и SoundCloud используют оба: Gateway снаружи для WAF/auth, BFF внутри для aggregation

## Q4. (!) Architecture с BFF?

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
> - [ ] Каждый BFF должен иметь собственную БД | ❌ ПОСЛЕДСТВИЕ: BFF — агрегационный слой без постоянного хранилища; БД принадлежат domain-микросервисам; BFF только агрегирует ответы
> - [x] BFF = HTTP сервер, параллельно вызывает несколько сервисов, агрегирует в один client-shaped response | ✓ ПРИМЕНЯТЬ: mobile home screen одним запросом агрегирует User+Feed+Notifications 📋 ПРАВИЛО: BFF = parallel fan-out → aggregate → single response 🔗 См. Q1
> - [ ] BFF синхронно вызывает сервисы по одному для надёжности | ❌ ПОСЛЕДСТВИЕ: последовательные calls = latency суммируется (100+200+150ms = 450ms); параллельный fan-out = max(100,200,150) = 200ms
> - [ ] BFF сохраняет состояние сессии и кэширует данные в памяти | ❌ ПОСЛЕДСТВИЕ: stateful BFF не масштабируется горизонтально; состояние в Redis/external cache; BFF stateless

## Q5. Сколько BFF нужно?

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
> - [ ] Один BFF для iOS и Android нельзя — всегда нужны отдельные | ❌ ПОСЛЕДСТВИЕ: если iOS и Android имеют одинаковые UX требования (например React Native) → один BFF оправдан; split только когда требования разошлись
> - [ ] Число BFF = число микросервисов | ❌ ПОСЛЕДСТВИЕ: BFF делится по client type, не по сервисам; один BFF агрегирует много сервисов
> - [x] Один BFF per client type с разными UX; start с одного, split когда требования расходятся | ✓ ПРИМЕНЯТЬ: Web BFF + Mobile BFF для начала; Partner BFF при B2B integration 📋 ПРАВИЛО: BFF = distinct UX = distinct team ownership 🔗 См. Q1
> - [ ] BFF нужно перезапускать при изменении любого микросервиса | ❌ ПОСЛЕДСТВИЕ: BFF независимо деплоится; изменение микросервиса не требует рестарта BFF если контракт не изменился

## Q6. Technology stack для BFF?

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
> - [ ] BFF должен быть написан на том же языке что и mobile app | ❌ ПОСЛЕДСТВИЕ: BFF — server-side; мобильные приложения на Swift/Kotlin, а BFF чаще Node.js или Kotlin backend; owned by mobile team но не тот же runtime
> - [ ] CPU-intensive BFF лучше на Java Spring MVC (thread-per-request) | ❌ ПОСЛЕДСТВИЕ: BFF I/O-heavy, не CPU; thread-per-request тратит threads на ожидание; reactive/async (Webflux, Node.js) эффективнее
> - [x] BFF lightweight I/O-heavy → Node.js/Kotlin/Go; async I/O critical; mobile team пишет свой BFF | ✓ ПРИМЕНЯТЬ: высокий throughput при параллельных backend calls 📋 ПРАВИЛО: BFF = fan-out I/O → async framework; team owns full stack 🔗 См. Q14
> - [ ] BFF нужна реляционная БД для хранения агрегированных ответов | ❌ ПОСЛЕДСТВИЕ: BFF stateless по природе; кэш если нужен — Redis; persistent state в domain services

## Q7. (!) Что должен делать BFF?

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
> - [ ] BFF должен валидировать бизнес-правила (например, минимальный заказ) | ❌ ПОСЛЕДСТВИЕ: бизнес-правила в BFF дублируются между iOS/Android/Web BFF; domain logic принадлежит сервисам
> - [x] BFF: aggregation, response shaping, protocol translation, client-specific feature flags, fallbacks | ✓ ПРИМЕНЯТЬ: всё что специфично для одного клиента принадлежит его BFF 📋 ПРАВИЛО: BFF = glue layer; domain logic in services 🔗 См. Q8
> - [ ] BFF всегда синхронно ждёт все backend calls перед ответом | ❌ ПОСЛЕДСТВИЕ: параллельный fan-out (Promise.all / coroutines) снижает latency; sequential calls суммируют latency
> - [ ] BFF должен аутентифицировать JWT токены | ❌ ПОСЛЕДСТВИЕ: auth обычно в API Gateway перед BFF; BFF может извлечь userId из validated token, но не должен валидировать сам

## Q8. (!) Что НЕ должен делать BFF?

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
> - [ ] BFF должен содержать auth логику (JWT validation, OAuth) | ❌ ПОСЛЕДСТВИЕ: auth в BFF дублируется; Gateway делает auth раньше; "fat BFF" anti-pattern начинается с этого
> - [ ] BFF должен сохранять ссылки на данные (FK в БД) для ускорения | ❌ ПОСЛЕДСТВИЕ: данные в domain сервисах; BFF без БД агрегирует на лету; denormalization в BFF = data inconsistency
> - [ ] BFF должен имплементировать сложный алгоритм рекомендаций | ❌ ПОСЛЕДСТВИЕ: CPU-intensive бизнес-логика в domain Recommendation Service; BFF только вызывает его и передаёт результат клиенту
> - [x] BFF = thin orchestrator: aggregation, shaping, protocol translation, client-specific flags; без domain logic, auth, persistence | ✓ ПРИМЕНЯТЬ: избегать "fat BFF" → рефакторить logic в domain services 📋 ПРАВИЛО: BFF = glue, not logic; если добавляешь бизнес-правило — не в BFF 🔗 См. Q7

## Q9. (!) GraphQL as BFF?

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
> - [ ] GraphQL заменяет BFF полностью — отдельные BFF не нужны | ❌ ПОСЛЕДСТВИЕ: один GraphQL для всех клиентов снова generic API; BFF per-client может быть GraphQL сервером, но разделение по клиентам сохраняется
> - [x] GraphQL = natural BFF: клиент запрашивает только нужные поля, resolvers агрегируют сервисы, DataLoader решает N+1 | ✓ ПРИМЕНЯТЬ: clients с разными data needs на одном endpoint 📋 ПРАВИЛО: GraphQL as BFF = client-driven shaping + resolver aggregation 🔗 См. Q2
> - [ ] GraphQL BFF кэшируется стандартным HTTP кэшем (CDN, browser) | ❌ ПОСЛЕДСТВИЕ: GraphQL обычно POST → CDN не кэширует; нужны persisted queries + GET для caching
> - [ ] GraphQL eliminates N+1 автоматически | ❌ ПОСЛЕДСТВИЕ: GraphQL без DataLoader → N+1 в resolvers; DataLoader нужно настраивать явно для batching + deduplication

## Q10. GraphQL Federation?

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
> - [ ] GraphQL Federation = один сервер с монолитной схемой | ❌ ПОСЛЕДСТВИЕ: Federation = distributed subgraphs, каждая team владеет своим subgraph; Gateway compose-ит в единый граф
> - [ ] GraphQL Federation заменяет BFF полностью | ❌ ПОСЛЕДСТВИЕ: Federation — domain decomposition (product team, user team); BFF — client-type decomposition; могут coexist: BFF → federated graph
> - [x] Federation: многие команды владеют subgraphs, Gateway компонует их в единый граф; каждый subgraph деплоится независимо (Apollo Federation, Netflix DGS) | ✓ ПРИМЕНЯТЬ: крупная организация, много команд, каждая владеет своим domain в GraphQL 📋 ПРАВИЛО: Federation = domain subgraphs; Gateway = query planner 🔗 См. Q9
> - [ ] Federation требует монолитного деплоя всех subgraphs | ❌ ПОСЛЕДСТВИЕ: Independent deployment — ключевое преимущество Federation; каждый subgraph CI/CD независимо

## Q11. (!) Преимущества BFF?

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
> - [ ] BFF не имеет недостатков — это всегда правильное решение | ❌ ПОСЛЕДСТВИЕ: BFF = operational overhead (N services), code duplication между BFF-ами, extra network hop; применять обдуманно
> - [x] Недостатки: code duplication, N сервисов деплоить, latency extra hop, риск "fat BFF", coordination при schema changes | ✓ ПРИМЕНЯТЬ: взвесить против выгод (overfetch, N+1, coupling) до решения о BFF 📋 ПРАВИЛО: BFF trade-off = client freedom vs operational cost 🔗 См. Q11
> - [ ] BFF увеличивает latency в 2x — всегда | ❌ ПОСЛЕДСТВИЕ: extra hop = 5-20ms, но BFF агрегирует N сервисных calls параллельно; net latency часто меньше чем N sequential calls
> - [ ] BFF code duplication неизбежна — нельзя переиспользовать код | ❌ ПОСЛЕДСТВИЕ: shared libraries (SDK для User Service client), gRPC generated clients, shared middleware packages решают дублирование

## Q12. (!) Недостатки BFF?

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
> - [ ] BFF code duplication исключительно бизнес-логика — её нужно централизовать в одном BFF | ❌ ПОСЛЕДСТВИЕ: централизация aggregation в одном BFF = возврат к generic API problem; дублирование aggregation оправдано
> - [ ] Один общий BFF для всех клиентов устраняет дублирование | ❌ ПОСЛЕДСТВИЕ: один общий BFF = generic API problem возвращается; дублирование aggregation между BFF — приемлемая цена per-client ownership
> - [x] Shared libraries (SDK per service), gRPC generated clients, shared middleware packages (auth, logging); aggregation logic per-client — ОК дублировать | ✓ ПРИМЕНЯТЬ: extract в lib что stable; дублировать что client-specific 📋 ПРАВИЛО: shared infra = lib; client aggregation = duplicate OK 🔗 См. Q12
> - [ ] gRPC клиенты не переиспользуются между BFF-ами — каждый пишет своё | ❌ ПОСЛЕДСТВИЕ: .proto файлы генерируют клиентский код для любого языка; сгенерированный client SDK переиспользуется во всех BFF

## Q13. Code duplication между BFFs?

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
> - [ ] Backend platform team должна писать все BFF-ы для консистентности | ❌ ПОСЛЕДСТВИЕ: BFF отражает UI requirements; backend team не знает UX нюансов → bottleneck; frontend ждёт каждого изменения
> - [x] Frontend/client team владеет своим BFF: знают UX requirements, независимо iterate, end-to-end ownership | ✓ ПРИМЕНЯТЬ: продуктовая команда = UI + BFF + ответственность за delivery 📋 ПРАВИЛО: BFF owner = client team = кто понимает UX 🔗 См. Q1
> - [ ] Отдельная "BFF team" владеет всеми BFF-ами | ❌ ПОСЛЕДСТВИЕ: BFF team = новый bottleneck; frontend снова ждёт; ownership не aligned с UX knowledge
> - [ ] Ownership BFF не важна — любая команда может менять любой BFF | ❌ ПОСЛЕДСТВИЕ: без ясного owner → конфликты, деградация quality, "fat BFF" никто не рефакторит

## Q14. (!) Ownership — кто пишет BFF?

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
> - [ ] Backend platform team должна всегда владеть BFF — разработчики фронтенда не знают backend | ❌ ПОСЛЕДСТВИЕ: BFF отражает UX требования; frontend team лучше понимает что нужно их клиенту; platform team = bottleneck
> - [ ] BFF может быть owned кем угодно — ownership не влияет на качество | ❌ ПОСЛЕДСТВИЕ: без ясного owner → "fat BFF" антипаттерн; UX-driven decisions принимаются неправильной командой
> - [ ] Только мобильная команда может писать Mobile BFF, только web — Web BFF (жёсткое разграничение) | ❌ ПОСЛЕДСТВИЕ: в стартапах один full-stack engineer часть обоих; важно что team понимает UX, а не название команды
> - [x] Client team (mobile/web/TV) пишет свой BFF: понимают UX, независимо iterate, end-to-end ownership | ✓ ПРИМЕНЯТЬ: product team = UI + BFF; platform team = microservices + shared infra 📋 ПРАВИЛО: who owns UX → owns BFF 🔗 См. Q8

## Q15. Caching в BFF?

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
> - [ ] BFF кэширует весь персонализированный ответ (user-specific) в CDN | ❌ ПОСЛЕДСТВИЕ: CDN кэширует публичный контент; персонализированный ответ нельзя кэшировать в CDN — только base feed без user context
> - [ ] Redis кэш в BFF не нужен — достаточно in-process cache | ❌ ПОСЛЕДСТВИЕ: in-process cache не переживает restart и не shared между instances; Redis = distributed, persistent, TTL-based
> - [x] BFF кэширует на трёх уровнях: upstream (ETag/Redis к сервисам), BFF-level (Redis, короткий TTL), CDN (публичный контент) | ✓ ПРИМЕНЯТЬ: hot data → in-proc; medium → Redis; public → CDN 📋 ПРАВИЛО: персонализированный ответ = не CDN; base feed = CDN OK 🔗 См. Q7
> - [ ] BFF не должен кэшировать — кэширование только в domain services | ❌ ПОСЛЕДСТВИЕ: агрегированные ответы из нескольких сервисов дорого собирать; BFF-level cache снижает backend load и latency

## Q16. BFF at edge (Cloudflare, Vercel)?

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
> - [ ] Edge BFF имеет полный доступ к БД — можно выполнять сложные queries | ❌ ПОСЛЕДСТВИЕ: edge compute (Cloudflare Workers, Vercel Edge) имеет ограниченный runtime (30s, 128MB, нет прямого DB доступа); BFF на edge — thin aggregator
> - [ ] Edge BFF снижает latency к origin services до нуля | ❌ ПОСЛЕДСТВИЕ: edge → origin = 100-150ms (cold path); выгода edge BFF = client ↔ edge = 20ms вместо client ↔ central = 100-200ms
> - [x] Edge BFF (Cloudflare Workers, Vercel Edge): BFF call 20ms вместо 150ms; heavy caching; thin layer; auth + feature flags | ✓ ПРИМЕНЯТЬ: globally distributed product требует low-latency BFF 📋 ПРАВИЛО: edge BFF = thin + cached; heavy logic → central 🔗 См. Q7
> - [ ] Edge BFF не может делать backend calls — только статический контент | ❌ ПОСЛЕДСТВИЕ: Edge Functions могут fetch() к origin services; cold path latency выше но работает

## Q17. Testing BFF?

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

> [!mcq]
> - [ ] Только E2E тесты нужны для BFF — unit тесты бесполезны | ❌ ПОСЛЕДСТВИЕ: E2E медленные и flaky; unit тесты resolvers с mock clients быстро покрывают aggregation logic
> - [ ] Contract тесты (Pact) не нужны — достаточно integration тестов | ❌ ПОСЛЕДСТВИЕ: integration тесты не предотвращают breaking changes в API сервисов; Pact → consumer-driven contracts → сервис знает что BFF ожидает
> - [x] Unit (mock service clients) + Contract (Pact ↔ backend) + Integration (WireMock) + E2E (Playwright) + Chaos (inject latency) | ✓ ПРИМЕНЯТЬ: testing pyramid для BFF: много unit → contract → мало E2E 📋 ПРАВИЛО: BFF testing = resolver unit + Pact contracts + synthetic smoke in prod 🔗 См. Q7
> - [ ] BFF не нужно тестировать — это только proxy | ❌ ПОСЛЕДСТВИЕ: BFF содержит aggregation logic, fallbacks, data shaping; без тестов: N+1 не обнаружен, fallback не работает

---

## See also

- [API Gateway](api-gateway-interview.md) — related but different
- [Microservices](microservices-interview.md) — BFF connects them
- [Edge Computing](edge-computing-interview.md) — BFF at edge
- [GraphQL](../api/graphql-interview.md) — often used as BFF
- [[rest-api-design-interview|REST API Design]] — BFF vs public API
- [Caching Strategies](caching-strategies-interview.md) — BFF caching
- [Resilience Patterns](resilience-patterns-interview.md) — fallbacks, timeouts
- [Clean Architecture](clean-architecture-interview.md) — boundary design
- [DDD](ddd-interview.md) — context mapping
- [Scalability Patterns](scalability-patterns-interview.md) — BFF as layer
