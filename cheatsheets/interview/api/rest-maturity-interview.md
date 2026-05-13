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
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Richardson Maturity Model (REST)`

`Richardson Maturity Model` (Leonard Richardson, 2008) — модель оценки **степени RESTful** API, 4 уровня (0-3). Уровень 3 (HATEOAS) — "truly RESTful" по Roy Fielding. На практике большинство "REST APIs" — Level 2. Модель помогает понимать что значит REST глубже, чем "JSON over HTTP".

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


> [!mcq]
> - [ ] RMM описывает 5 уровней зрелости, от Level 0 до Level 4 (REST + WebSub) | ❌ ПОСЛЕДСТВИЕ: ровно 4 уровня (0-3); Level 3 = HATEOAS и есть «truly RESTful» по Fielding; никаких WebSub или Level 4 в модели нет
> - [ ] Level 0 — это API с правильными HTTP-verbs но без HATEOAS | ❌ ПОСЛЕДСТВИЕ: Level 0 (POX) — это RPC через HTTP с одним URL и одним глаголом (обычно POST); правильные verbs появляются только на Level 2
> - [ ] Это prescriptive-модель: API должен быть Level 3, иначе не RESTful | ❌ ПОСЛЕДСТВИЕ: RMM — descriptive (описательная), не prescriptive; большинство production API на Level 2 и это нормальный pragmatic REST
> - [x] RMM — 4 уровня (0: POX/RPC через HTTP → 1: Resources/multiple URIs → 2: HTTP Verbs+Status Codes → 3: HATEOAS); descriptive | ✓ ПРИМЕНЯТЬ: оценить свой API по уровням; Level 2 — pragmatic target; Level 3 — для public/hypermedia-driven API 📋 ПРАВИЛО: 0=POX, 1=URIs, 2=Verbs, 3=Hypermedia 🔗 См. Q2

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


> [!mcq]
> - [ ] Модель prescriptive — без Level 3 API не считается REST | ❌ ПОСЛЕДСТВИЕ: модель descriptive — описывает реальность, не предписывает; большинство production APIs работают на Level 2 без проблем
> - [ ] RMM нужна чтобы выбрать между REST и SOAP | ❌ ПОСЛЕДСТВИЕ: RMM сравнивает уровни RESTful-ности, а не REST vs SOAP; для сравнения с другими стилями см. GraphQL/gRPC vs REST
> - [ ] Модель — это OAS/OpenAPI спецификация | ❌ ПОСЛЕДСТВИЕ: OpenAPI — формат описания API (контракт); RMM — оценка архитектурного стиля; ортогональны
> - [x] Дифференцирует уровни «RESTful» (большинство «REST APIs» — на самом деле Level 1-2); помогает понимать смысл HTTP-verbs/status-codes/HATEOAS; descriptive (не prescriptive) | ✓ ПРИМЕНЯТЬ: использовать как teaching-tool для команды; обсуждать pragmatic Level 2 vs idealistic Level 3 при design review 📋 ПРАВИЛО: RMM = язык для разговора о REST, не правило 🔗 См. Q3

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


> [!mcq]
> - [ ] Level 0 — это «правильный REST», просто с одним endpoint'ом | ❌ ПОСЛЕДСТВИЕ: Level 0 — это RPC через HTTP, не REST; HTTP используется только как транспорт, method/resource — в теле запроса
> - [ ] SOAP/XML-RPC относятся к Level 2 в RMM | ❌ ПОСЛЕДСТВИЕ: SOAP и XML-RPC — классические примеры Level 0 (POX): один URL, всё POST, semantics в body
> - [ ] На Level 0 можно использовать GET для чтения данных | ❌ ПОСЛЕДСТВИЕ: формально можно, но традиционно весь POX-трафик идёт через POST; именно поэтому caching/idempotency не работают
> - [x] Level 0 (Swamp of POX): один URL, всё через POST, HTTP только как транспорт; method и resource в request body; примеры — SOAP, XML-RPC, JSON-RPC | ✓ ПРИМЕНЯТЬ: миграция Level 0 → Level 1+ начинается с выделения отдельных URL per resource 📋 ПРАВИЛО: Level 0 = RPC tunnel через HTTP 🔗 См. Q4

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


> [!mcq]
> - [ ] На Level 1 уже используются все HTTP-методы (GET/POST/PUT/DELETE) | ❌ ПОСЛЕДСТВИЕ: HTTP-методы используются правильно только начиная с Level 2; Level 1 — всё ещё POST, но per-resource URLs
> - [ ] Level 1 = добавление status codes 2xx/4xx/5xx | ❌ ПОСЛЕДСТВИЕ: правильные status codes — это уже Level 2; Level 1 — только URL-структура (multiple resources)
> - [ ] Кэширование на Level 1 невозможно потому что всё POST | ❌ ПОСЛЕДСТВИЕ: caching ограничен на Level 1 (POST не кэшируется), но появляется resource-based URL который можно использовать в keys; полноценный cache начинается с Level 2 (GET)
> - [x] Level 1 (Resources): per-resource URLs (`/users`, `/orders`, `/products`), но всё ещё POST; разделение endpoint'ов улучшает organization, monitoring, partial caching | ✓ ПРИМЕНЯТЬ: первый шаг при миграции Level 0 → REST; помогает разделить ownership разных ресурсов по командам 📋 ПРАВИЛО: Level 1 = «один URL на ресурс», без правильных verbs 🔗 См. Q5

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


> [!mcq]
> - [ ] Level 2 = Level 1 + HATEOAS (links в responses) | ❌ ПОСЛЕДСТВИЕ: HATEOAS — это Level 3; Level 2 — правильные HTTP verbs (GET/POST/PUT/PATCH/DELETE) и status codes (2xx/4xx/5xx) без links
> - [ ] PATCH введён в HTTP 2.0, недоступен на Level 2 | ❌ ПОСЛЕДСТВИЕ: PATCH есть в HTTP/1.1 с RFC 5789 (2010); используется для partial update; HTTP 2.0 не существует, есть HTTP/2 — это binary framing, не новые методы
> - [ ] 201 Created должен возвращаться только из PUT, не из POST | ❌ ПОСЛЕДСТВИЕ: 201 возвращается из любого метода, создавшего ресурс (POST `/users` → 201 + Location header); 200 OK — для read; 204 No Content — для DELETE/PUT без body
> - [x] Level 2: правильные HTTP-verbs (GET=read, POST=create, PUT=replace, PATCH=partial update, DELETE=remove) + правильные status codes (2xx/3xx/4xx/5xx); кэширование, idempotency, стандартные tooling работают | ✓ ПРИМЕНЯТЬ: target для большинства production API; balance practicality + REST; OpenAPI docs дополняют 📋 ПРАВИЛО: Level 2 = HTTP semantics, без hypermedia 🔗 См. Q6

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


> [!mcq]
> - [ ] HATEOAS требует HAL формата — другие не поддерживаются | ❌ ПОСЛЕДСТВИЕ: есть несколько форматов: HAL (`_links`), JSON:API (`links` + `relationships`), Siren (`actions`), Collection+JSON; HATEOAS — концепция, не привязана к одному формату
> - [ ] Level 3 = HTTPS + JWT + rate limiting | ❌ ПОСЛЕДСТВИЕ: всё это орthogonal с RMM; security/transport не входят в модель maturity; Level 3 — про hypermedia controls
> - [ ] Linking в responses избыточен — клиент уже знает API через OpenAPI | ❌ ПОСЛЕДСТВИЕ: OpenAPI = design-time контракт (клиент знает URLs упfront, tight coupling); HATEOAS = runtime discovery (server может менять URLs без breaking changes); цели разные
> - [x] Level 3: ответы содержат hypermedia links (`_links`, `actions`) для discoverable navigation; клиент следует links вместо hardcoded URLs; server может evolve API без breaking changes | ✓ ПРИМЕНЯТЬ: для public APIs с длинным lifecycle; для state-machine endpoint'ов (только valid actions в links); Spring HATEOAS для Java 📋 ПРАВИЛО: Level 3 = «follow your nose» через links 🔗 См. Q7

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


> [!mcq]
> - [ ] HATEOAS = HTTP Authentication, Encryption и Origin Access Standard | ❌ ПОСЛЕДСТВИЕ: HATEOAS — Hypermedia As The Engine Of Application State; не имеет отношения к auth/encryption (это transport-level concern)
> - [ ] HATEOAS — это server-side rendering (как Server-Side Includes) | ❌ ПОСЛЕДСТВИЕ: HATEOAS — про runtime navigation через ссылки в API-responses; SSR — про HTML-рендеринг на сервере для browser
> - [ ] Клиент при HATEOAS жёстко зашивает все URLs из документации | ❌ ПОСЛЕДСТВИЕ: суть HATEOAS как раз в обратном — клиент знает только entry-point URL, остальное обнаруживает через `_links` в ответах сервера
> - [x] HATEOAS = клиент navigate state через hypermedia links от сервера; аналогия с web browsing (follow links вместо typing URLs); клиент знает только entry point, остальное discover через links | ✓ ПРИМЕНЯТЬ: для долгоживущих public API с независимой эволюцией сервера; client SDK генерируется на основе link relations 📋 ПРАВИЛО: HATEOAS = «browsing the API» вместо «hardcoding the API» 🔗 См. Q8

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


> [!mcq]
> - [ ] HAL и JSON:API — синонимы | ❌ ПОСЛЕДСТВИЕ: разные spec'ы: HAL (Mike Kelly, draft-kelly-json-hal) — простой, `_links`/`_embedded`; JSON:API — opinionated, более тяжёлый, jsonapi.org spec
> - [ ] Siren такой же как HAL, только в XML | ❌ ПОСЛЕДСТВИЕ: Siren — JSON-format (не XML); главное отличие — наличие `actions` (с method/href/fields для каждого), что покрывает state-machine лучше чем HAL
> - [ ] Spring HATEOAS поддерживает только HAL | ❌ ПОСЛЕДСТВИЕ: основной — HAL, но Spring HATEOAS также поддерживает HAL-FORMS, Collection+JSON, UBER; через `HypermediaType.HAL_FORMS_JSON` и `MediaTypes`
> - [x] HAL (`_links`/`_embedded`) — minimalistic; JSON:API (`data`/`relationships`/`links`) — opinionated full spec; Siren (`class`/`actions`/`links`) — action-oriented для state machines; Spring HATEOAS реализует HAL + HAL-FORMS | ✓ ПРИМЕНЯТЬ: HAL для простых API; JSON:API для стандартизации между командами; Siren когда важны actions/transitions 📋 ПРАВИЛО: выбор формата = trade-off простота vs выразительность 🔗 См. Q9

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


> [!mcq]
> - [ ] HATEOAS уменьшает payload размер по сравнению с традиционным REST | ❌ ПОСЛЕДСТВИЕ: HATEOAS УВЕЛИЧИВАЕТ payload (добавляет `_links` к каждому ответу); это один из недостатков; преимущества — decoupling и discoverability, а не размер
> - [ ] Главное преимущество — авторизация: links отображаются только для permitted actions | ❌ ПОСЛЕДСТВИЕ: это побочное преимущество, не главное; основное — decoupling между клиентом и сервером (server может менять URLs)
> - [ ] HATEOAS убирает необходимость в API versioning | ❌ ПОСЛЕДСТВИЕ: упрощает evolution (URL-changes без breaking), но не заменяет versioning при breaking changes в schema/семантике
> - [x] Преимущества: decoupling (server может менять URLs), server-driven evolution, discoverability/self-documenting, state machine encoded в links (только valid actions), упрощённый client navigation от entry point | ✓ ПРИМЕНЯТЬ: для public APIs, workflow/state-machine endpoint'ов (order:PENDING→approve/cancel; SHIPPED→track), долгоживущих integration scenarios 📋 ПРАВИЛО: HATEOAS = price больше payload + complexity, ценность = evolvability 🔗 См. Q10

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


> [!mcq]
> - [ ] HATEOAS не применяется потому что слишком сложен для реализации на сервере | ❌ ПОСЛЕДСТВИЕ: основная проблема — на клиенте (логика парсинга links, action selection); серверные библиотеки (Spring HATEOAS) достаточно зрелые
> - [ ] HATEOAS не работает с современными SPA (React, Vue) | ❌ ПОСЛЕДСТВИЕ: технически работает; проблема — frontend devs привыкли к hardcoded paths и type-safe SDK; UX не требует runtime discovery
> - [ ] HATEOAS заменён HTTP/2 PUSH | ❌ ПОСЛЕДСТВИЕ: HTTP/2 Server Push — про предварительную доставку response (deprecated в 2022); HATEOAS — про hypermedia content semantics; ортогональны
> - [x] HATEOAS редок из-за: client complexity (нужно парсить links), отсутствие стандарта (HAL/JSON:API/Siren), OpenAPI документации достаточно для design-time типизации, performance overhead, frontend developers предпочитают hardcoded routes | ✓ ПРИМЕНЯТЬ: pragmatic выбор — Level 2 + OpenAPI; HATEOAS оправдан только для public/long-lived API с независимыми консьюмерами 📋 ПРАВИЛО: HATEOAS = academic ideal, Level 2 = market reality 🔗 См. Q11

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


> [!mcq]
> - [ ] PUT и PATCH полностью взаимозаменяемы | ❌ ПОСЛЕДСТВИЕ: PUT отправляет ВЕСЬ ресурс и заменяет (отсутствующие поля обнуляются); PATCH — только изменения; путать → данные затираются на PUT
> - [ ] POST идемпотентен если использовать UUID на клиенте | ❌ ПОСЛЕДСТВИЕ: POST по семантике не идемпотентен; client-generated UUID + dedupe-логика на сервере даёт effective idempotency, но HTTP semantics остаются «non-idempotent» (см. Idempotency-Key header)
> - [ ] GET может изменять состояние если это side-effect логирование | ❌ ПОСЛЕДСТВИЕ: GET = safe (no state change observable клиентом); logging — internal; но action-style `GET /users/123/delete` ломает все правила: CDN/proxy/crawler могут вызвать delete случайно
> - [x] GET (safe, idempotent, читает), POST (unsafe, non-idempotent, создаёт), PUT (unsafe, idempotent, full replace), PATCH (unsafe, может быть idempotent, partial update), DELETE (unsafe, idempotent, удаляет); HEAD = GET без body, OPTIONS = discover методов | ✓ ПРИМЕНЯТЬ: PATCH с JSON Merge Patch (RFC 7396) или JSON Patch (RFC 6902); PUT для full update; Idempotency-Key header для POST когда нужна safe retry 📋 ПРАВИЛО: правильный verb = правильный contract с caches/proxies/clients 🔗 См. Q12

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


> [!mcq]
> - [ ] Все ошибки можно возвращать как 200 OK с error в body | ❌ ПОСЛЕДСТВИЕ: caching/retry/monitoring не работают (200 = success для proxies/CDN); breaks client retry logic (нет distinction между ok и fail); HTTP semantics нарушены
> - [ ] 401 Unauthorized = пользователь известен, но нет доступа | ❌ ПОСЛЕДСТВИЕ: путаница — 401 = «нет/неправильная аутентификация»; «известен но нет доступа» = 403 Forbidden; разные действия для клиента (401 → re-login, 403 → contact admin)
> - [ ] 503 Service Unavailable и 504 Gateway Timeout — синонимы | ❌ ПОСЛЕДСТВИЕ: разные семантики — 503 = upstream сам отвечает «я не доступен» (обычно с Retry-After header); 504 = gateway не дождался ответа от upstream
> - [x] Категории: 1xx info, 2xx success (200/201/204/202), 3xx redirect (301/304), 4xx client error (400/401/403/404/409/422/429), 5xx server error (500/502/503/504); правильные коды → caching, retry, monitoring работают | ✓ ПРИМЕНЯТЬ: 201 + Location на create, 204 на delete без body, 422 для validation errors, 429 с Retry-After для rate-limiting, 503 при maintenance с Retry-After 📋 ПРАВИЛО: status code — это контракт с инфраструктурой (LB/CDN/retry-clients) 🔗 См. Q13

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


> [!mcq]
> - [ ] Idempotent = метод можно вызывать только один раз | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — idempotent означает можно вызвать N раз с тем же результатом (final state одинаков); важно для retry-логики при сетевых сбоях
> - [ ] POST с одинаковым body идемпотентен | ❌ ПОСЛЕДСТВИЕ: POST по умолчанию создаёт новый ресурс каждый вызов → не idempotent; для idempotent semantics нужен Idempotency-Key header (Stripe, RFC 9110 draft)
> - [ ] PATCH всегда идемпотентен | ❌ ПОСЛЕДСТВИЕ: зависит от типа — JSON Merge Patch (RFC 7396) идемпотентен; JSON Patch с array-операциями (`add at index 0`) НЕ идемпотентен (повтор сдвигает массив)
> - [x] Idempotent: GET/HEAD/OPTIONS/PUT/DELETE; non-idempotent: POST, PATCH (зависит); workaround для POST idempotency = `Idempotency-Key` header, сервер хранит ключ и возвращает same response для duplicate request | ✓ ПРИМЕНЯТЬ: для payment/order POST обязательно Idempotency-Key чтобы network retry не создал дубли; key TTL 24h, хранить в Redis 📋 ПРАВИЛО: idempotent verbs → safe to retry на network failure 🔗 См. Q14

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


> [!mcq]
> - [ ] Safe = быстрый/легковесный метод | ❌ ПОСЛЕДСТВИЕ: safe = no state change (читать-only); GET может быть тяжёлым (full report), но всё равно safe; safe-ness про семантику, не perf
> - [ ] PUT и DELETE — safe потому что idempotent | ❌ ПОСЛЕДСТВИЕ: путаница понятий: safe = no state change; idempotent = same result on retry; PUT/DELETE — unsafe (меняют state), но idempotent
> - [ ] `GET /users/123/delete` приемлемо если URL содержит auth-token | ❌ ПОСЛЕДСТВИЕ: GET с side-effects — фундаментально неверно; CDN/proxy/browser-prefetch/crawler вызовут случайно; auth не защищает от этого
> - [x] Safe: GET, HEAD, OPTIONS (read-only). Unsafe: POST, PUT, PATCH, DELETE (modify state). CDN/proxy кэшируют safe, browser prefetch'ит safe URLs; never use GET для side-effect actions | ✓ ПРИМЕНЯТЬ: страницы admin/CRUD UI должны вызывать DELETE/POST через AJAX, не GET-ссылки; правильное соответствие verb-semantics 📋 ПРАВИЛО: safe ≠ idempotent (concepts ортогональны) 🔗 См. Q15

## Q15. (!) Критика модели (Roy Fielding)?

**Roy Fielding** (REST creator, 2000 PhD thesis):
> "If the engine of application state (and hence the API) is not being driven by hypertext, then it cannot be RESTful."

**Только Level 3** — true REST по Fielding.

**Critique RMM:**
- Maturity ladder implies higher = better, не correct in all contexts
- Level 2 — pragmatic standard, not failure
- Levels не universal goals

**Most modern APIs** = Level 2 + OpenAPI/Swagger. Industry choose pragmatism over purity.


> [!mcq]
> - [ ] Fielding похвалил RMM как точное описание его REST | ❌ ПОСЛЕДСТВИЕ: наоборот — Fielding критиковал API без hypermedia за неправомерное использование термина «RESTful»; его 2008 пост — манифест против Level 2 как «достаточный»
> - [ ] Fielding считает Level 2 правильным компромиссом | ❌ ПОСЛЕДСТВИЕ: Fielding жёстко: без HATEOAS API «не RESTful»; «If the engine of application state is not being driven by hypertext, then it cannot be RESTful»
> - [ ] Критика Fielding устарела с появлением OpenAPI | ❌ ПОСЛЕДСТВИЕ: критика об архитектурном стиле, OpenAPI = design-time контракт; ортогонально; Fielding бы сказал что OpenAPI не заменяет runtime discoverability
> - [x] Roy Fielding (REST creator, 2000 thesis): только Level 3 — true REST; Level 2 без HATEOAS не является RESTful; цитата «if not driven by hypertext, not REST». RMM критикуют за «ladder» — industry pragmatic Level 2 не есть failure | ✓ ПРИМЕНЯТЬ: понимать что «truly RESTful» в академическом смысле редок; pragmatic REST — индустриальная норма с OpenAPI документацией 📋 ПРАВИЛО: Fielding strict vs industry pragmatic — оба валидны для разных контекстов 🔗 См. Q16

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


> [!mcq]
> - [ ] Pragmatic REST = REST без HTTP-методов | ❌ ПОСЛЕДСТВИЕ: Pragmatic = Level 2 (правильные HTTP-методы есть!); без методов — это Level 0/1, не «pragmatic», это «недоделанный»
> - [ ] Idealistic REST избегает OpenAPI | ❌ ПОСЛЕДСТВИЕ: idealistic Level 3 как раз больше нуждается в documentation (link relations описать где-то надо); OpenAPI + HATEOAS не противоречат
> - [ ] Pragmatic REST = JSON-only, idealistic = XML-only | ❌ ПОСЛЕДСТВИЕ: формат body ортогонален maturity; и Pragmatic, и Idealistic могут быть JSON или XML; разница в hypermedia/HATEOAS
> - [x] Pragmatic (Level 2): HTTP verbs + status codes + clean URIs + JSON + OpenAPI docs. Idealistic (Level 3): всё выше + HATEOAS + hypermedia format (HAL/JSON:API). Реальность 2025: pragmatic доминирует, OpenAPI = de-facto standard | ✓ ПРИМЕНЯТЬ: pragmatic для team-internal/SaaS API; idealistic для public/long-lived API с независимыми консьюмерами 📋 ПРАВИЛО: choose maturity level based on consumer-coupling tolerance 🔗 См. Q17

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

> [!mcq]
> - [ ] GraphQL заменяет REST во всех случаях — REST устарел | ❌ ПОСЛЕДСТВИЕ: каждый стиль для своего случая; REST — для public API с HTTP caching; GraphQL — для mobile/complex queries с over-fetching; не «лучше», а «другое»
> - [ ] gRPC работает в браузере нативно без proxy | ❌ ПОСЛЕДСТВИЕ: gRPC использует HTTP/2 features (trailers, binary framing) недоступные в browser fetch API; нужен gRPC-Web + proxy (Envoy) для браузерного клиента
> - [ ] GraphQL имеет лучший HTTP caching чем REST | ❌ ПОСЛЕДСТВИЕ: HTTP caching работает на уровне URL+method (GET); GraphQL обычно POST на `/graphql` с query в body → HTTP cache не работает; кэширование на client (Apollo Cache, Relay) или server (Persisted Queries)
> - [x] REST: HTTP-based, resource-oriented, natural HTTP caching, может over/under-fetch. GraphQL: single endpoint, client picks fields, schema-driven, subscriptions, слабее HTTP semantics. gRPC: binary Protobuf, HTTP/2, strong typing, streaming, для internal service-to-service | ✓ ПРИМЕНЯТЬ: public API → REST; mobile/complex queries → GraphQL; internal microservices → gRPC; hybrid (public REST + internal gRPC) — типовой паттерн 📋 ПРАВИЛО: каждый стиль для своего use-case, не competitor 🔗 См. See also

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
