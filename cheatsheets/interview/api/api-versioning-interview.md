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
> - [ ] Versioning нужен чтобы принудительно мигрировать всех клиентов на новую версию разом | ❌ ПОСЛЕДСТВИЕ: принудительная миграция невозможна — mobile apps не обновляются мгновенно, App Store review занимает дни; все клиенты на старой версии упадут
> - [x] `/v1/` и `/v2/` существуют параллельно — старые mobile clients продолжают работать с v1 пока не обновятся в своём темпе | ✓ ПРИМЕНЯТЬ: всегда для public APIs и любых APIs с external consumers 📋 ПРАВИЛО: versioning = параллельное сосуществование, а не принудительная миграция 🔗 См. Q9
> - [ ] Versioning автоматически гарантирует backwards compatibility | ❌ ПОСЛЕДСТВИЕ: versioning только разделяет контракты; `/v2/` сам по себе может иметь breaking changes — клиенты на v2 всё равно сломаются при следующем breaking change
> - [ ] Internal microservices APIs не нуждаются в versioning | ❌ ПОСЛЕДСТВИЕ: при несинхронном деплое новый producer сломает старого consumer — rolling deployments без версионирования создают window несовместимости

## Q2. (!) Что такое breaking change?

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
> - [ ] Добавление нового опционального поля к response — breaking change | ❌ ПОСЛЕДСТВИЕ: это не breaking change; по Tolerant Reader pattern клиенты игнорируют неизвестные поля — не требует новой версии
> - [ ] Исправление опечатки в имени поля (`username` → `userName`) — не breaking change | ❌ ПОСЛЕДСТВИЕ: это IS breaking change — все клиенты десериализующие старое имя получат null вместо значения → NullPointerException в runtime
> - [x] Удаление существующего поля из response — breaking change, требует инкремента major версии | ✓ ПРИМЕНЯТЬ: всегда при удалении/переименовании полей, смене типов, добавлении required полей 📋 ПРАВИЛО: breaking = clients must change code 🔗 См. Q3
> - [ ] Добавление нового endpoint без удаления старых — breaking change | ❌ ПОСЛЕДСТВИЕ: чистый additive change; старые clients продолжают работать с существующими endpoints без изменений

## Q3. (!) Backwards-compatible changes — examples?

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
> - [ ] Изменение типа поля с `string` на `integer` — backwards-compatible | ❌ ПОСЛЕДСТВИЕ: клиенты десериализующие строку в String-поле получат ClassCastException или parse error — явное breaking change
> - [ ] Добавление нового обязательного поля к request — backwards-compatible | ❌ ПОСЛЕДСТВИЕ: старые clients не передают новое поле → сервер вернёт 400 Bad Request на каждый запрос
> - [x] Добавление нового опционального поля к response — backwards-compatible: старые клиенты его просто игнорируют | ✓ ПРИМЕНЯТЬ: additive changes к response, новые endpoints, делать required поля опциональными 📋 ПРАВИЛО: Tolerant Reader = ignore unknown fields 🔗 См. Q2
> - [ ] Переименование существующего error code (404 → 422) для точности — backwards-compatible | ❌ ПОСЛЕДСТВИЕ: клиенты с `if status == 404` не сработают для нового кода → ошибка не обработана, silently ignored

## Q4. (!) URI versioning?

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
> - [ ] URI versioning нарушает RESTful принципы поэтому нельзя использовать в production | ❌ ПОСЛЕДСТВИЕ: неверно; GitHub, Twitter, Stripe использовали URI versioning годами — практичность важнее REST purity; отказ от него ограничивает инструментарий
> - [ ] URI versioning требует отдельных серверов для каждой версии | ❌ ПОСЛЕДСТВИЕ: не требует; API Gateway легко маршрутизирует `/v1/` и `/v2/` на разные handlers в одном сервисе или microservices
> - [x] `GET /v2/users/123` — версия видна в URL, легко тестируется в браузере, тривиально маршрутизируется в API Gateway | ✓ ПРИМЕНЯТЬ: public APIs, default choice для большинства REST APIs 📋 ПРАВИЛО: visible versioning > REST purity для production APIs 🔗 См. Q9
> - [ ] Header versioning предпочтительнее URI для всех случаев | ❌ ПОСЛЕДСТВИЕ: header versioning требует Vary header для корректного кэширования, не тестируется в браузере, сложнее документировать

## Q5. (!) Header versioning?

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
> - [ ] Header versioning автоматически корректно кэшируется CDN без дополнительных настроек | ❌ ПОСЛЕДСТВИЕ: без `Vary: Accept` CDN возвращает v1 response клиенту запросившему v2 — разные версии неразличимы для кэша
> - [x] `Accept: application/vnd.example.v2+json` — URI ресурса не меняется, REST-pure, нужен `Vary: Accept` для корректного кэширования | ✓ ПРИМЕНЯТЬ: когда важна REST-чистота, internal APIs, GitHub-style 📋 ПРАВИЛО: header version = скрытая от URL, видна только в headers 🔗 См. Q4
> - [ ] Header versioning проще тестировать чем URI versioning | ❌ ПОСЛЕДСТВИЕ: сложнее; нельзя открыть в браузере, нужен curl или Postman с explicit headers — onboarding сложнее
> - [ ] Header versioning поддерживается всеми HTTP clients из коробки | ❌ ПОСЛЕДСТВИЕ: custom headers (`X-API-Version`) нестандартны; не все HTTP frameworks автоматически пробрасывают custom headers при redirect

## Q6. (!) Query parameter versioning?

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
> - [ ] Query parameter versioning автоматически кэшируется CDN так же как URI versioning | ❌ ПОСЛЕДСТВИЕ: CDN может стриппить query params при кэшировании — нужно явно настраивать cache key включающий version param, иначе v1 response отдаётся v2 клиентам
> - [ ] Query parameter versioning нарушает idempotency GET-запросов | ❌ ПОСЛЕДСТВИЕ: неверно; добавление query param не меняет HTTP semantics — GET с `?version=2` остаётся idempotent
> - [x] `GET /users/123?api-version=2024-01-15` — версия видна, не меняет path, удобно для тестирования в браузере | ✓ ПРИМЕНЯТЬ: Azure-style APIs, когда date-based versioning, легкое тестирование важнее чистоты URI 📋 ПРАВИЛО: version as param = pollutes URI но удобен для debugging 🔗 См. Q11
> - [ ] Query parameter надёжнее header versioning для production | ❌ ПОСЛЕДСТВИЕ: header versioning надёжнее для content negotiation; query params легко потерять при редиректах или URL encoding issues

## Q7. Content negotiation (Accept header)?

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
> - [ ] Content negotiation versioning самый простой подход для понимания клиентами | ❌ ПОСЛЕДСТВИЕ: MIME типы вида `application/vnd.example.user.v2+json` сложны — разработчики путаются, documentation громоздкая, onboarding медленный
> - [ ] Content negotiation и URI versioning несовместимы — нельзя использовать оба | ❌ ПОСЛЕДСТВИЕ: несвязаны; можно комбинировать URI version (`/v2/`) с content type negotiation для формата ответа
> - [x] `Accept: application/vnd.example.user.v2+json` — HTTP-standard, URI ресурса не меняется, но MIME types сложные и нишевые | ✓ ПРИМЕНЯТЬ: нишевые случаи где строгое HTTP compliance критично; mostly academic 📋 ПРАВИЛО: content negotiation = pure HTTP но impractical для большинства 🔗 См. Q5
> - [ ] Content negotiation лучше URI versioning для routing в API Gateway | ❌ ПОСЛЕДСТВИЕ: routing по MIME type сложнее реализовать в API Gateway; URI routing тривиален (`/v1/` → service-v1)

## Q8. Hostname-based?

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
> - [ ] Hostname versioning бесплатно с точки зрения CDN кэширования | ❌ ПОСЛЕДСТВИЕ: разные hostnames = полностью изолированные CDN кэши; нет cross-version cache sharing; TLS certificates нужны для каждого hostname
> - [x] `api-v2.example.com` — полная инфраструктурная изоляция версий; разные DNS, разные деплои, разные TLS сертификаты | ✓ ПРИМЕНЯТЬ: когда нужна полная isolation per version, версии с разными SLA или compliance требованиями 📋 ПРАВИЛО: hostname version = max isolation, max operational cost 🔗 См. Q4
> - [ ] Hostname versioning проще URI versioning с точки зрения настройки | ❌ ПОСЛЕДСТВИЕ: требует DNS management, отдельных TLS сертификатов, CORS configuration на каждый hostname — operational overhead намного выше
> - [ ] Hostname versioning обязателен для high-load production APIs | ❌ ПОСЛЕДСТВИЕ: неверно; URI versioning с API Gateway scale так же хорошо без дополнительного DNS overhead

## Q9. (!) Какой подход выбрать?

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
> - [ ] Header versioning всегда лучше URI потому что не нарушает REST принципы | ❌ ПОСЛЕДСТВИЕ: header versioning требует Vary header для кэша, не тестируется в браузере, документация сложнее — REST purity редко окупает эти costs
> - [ ] GraphQL тоже нуждается в URI versioning как REST | ❌ ПОСЛЕДСТВИЕ: GraphQL использует deprecation без версий — добавляешь поля, помечаешь старые @deprecated; отдельные URI versions создают complexity без benefit
> - [x] URI versioning (`/v1/`) — default для public APIs; header versioning для internal REST; GraphQL через deprecation | ✓ ПРИМЕНЯТЬ: URI для public, header для internal, никакого versioning для GraphQL 📋 ПРАВИЛО: pragmatism over purity 🔗 См. Q14
> - [ ] Для microservices внутренних APIs versioning не нужен — можно обновлять одновременно | ❌ ПОСЛЕДСТВИЕ: при rolling deployments есть window несовместимости; без versioning producer v2 сломает consumer v1 на соседнем поде

## Q10. Semantic versioning (SemVer)?

`MAJOR.MINOR.PATCH` (e.g., `2.5.3`).

- **MAJOR** — breaking changes
- **MINOR** — new features (backwards compatible)
- **PATCH** — bug fixes

**API versioning ≠ SemVer обычно:**
- API URL: `/v1/` (just MAJOR)
- Documentation tracks MINOR/PATCH

**Library semver**: SemVer применим к SDK / client libraries.


> [!mcq]
> - [ ] В API URL используется полный SemVer: `/v2.5.3/users/123` | ❌ ПОСЛЕДСТВИЕ: clients вынуждены обновлять URL при каждом minor/patch — breaking change при каждом bugfix; URL bloat; не используется в production
> - [ ] MINOR version в API URL позволяет добавлять features без breaking clients | ❌ ПОСЛЕДСТВИЕ: minor version в URL засоряет namespace; minor changes backwards-compatible по определению — не требуют новой URL версии
> - [x] API URL использует только MAJOR (`/v1/`, `/v2/`) — MINOR/PATCH в документации и SDK; URL меняется только при breaking changes | ✓ ПРИМЕНЯТЬ: URL version = major только; SDK/libraries используют полный SemVer 📋 ПРАВИЛО: API major = URL; SemVer = libraries 🔗 См. Q2
> - [ ] SemVer PATCH нужен в API URL для hot fixes | ❌ ПОСЛЕДСТВИЕ: patch = implementation detail; clients не меняют код при hotfix; version в URL создаёт confusion без benefit

## Q11. (!) Date-based versioning (Stripe approach)?

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
> - [ ] Date-based versioning проще поддерживать чем URI versioning — просто меняешь дату | ❌ ПОСЛЕДСТВИЕ: Stripe поддерживает ВСЕ исторические версии — каждое изменение требует compatibility transform для всех прошлых дат; огромные инженерные инвестиции
> - [ ] При date-based versioning клиенты автоматически получают latest версию | ❌ ПОСЛЕДСТВИЕ: наоборот; клиент pinned к дате первой интеграции — никакого auto-upgrade без явного opt-in
> - [x] `Stripe-Version: 2024-04-19` в header — каждая дата snapshot поведения API; клиент pinned к конкретной дате навсегда | ✓ ПРИМЕНЯТЬ: платёжные API, финтех где stability критична; требует significant engineering 📋 ПРАВИЛО: date version = pinning к конкретному snapshot поведения 🔗 См. Q12
> - [ ] Date-based versioning нельзя использовать в query parameter — только в header | ❌ ПОСЛЕДСТВИЕ: неверно; `?api-version=2024-04-19` работает; Azure использует date-based в query params

## Q12. (!) Per-account version pinning?

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
> - [ ] Per-account pinning позволяет обновить все accounts одновременно без migration | ❌ ПОСЛЕДСТВИЕ: каждый account pinned к своей версии независимо; нет единого обновления — каждый клиент мигрирует индивидуально, иначе нарушишь их integration
> - [x] Каждый account закреплён за конкретной версией при первом вызове; breaking changes не затрагивают существующих clients | ✓ ПРИМЕНЯТЬ: payment APIs, fintech, любые APIs где breaking existing integrations критично 📋 ПРАВИЛО: per-account pinning = isolation at the account level 🔗 См. Q11
> - [ ] Version pinning требует хранения разных кодовых баз для каждой версии | ❌ ПОСЛЕДСТВИЕ: одна кодовая база с compatibility transform layer; разные кодовые базы — антипаттерн, нет cross-version bug fixes
> - [ ] Per-account pinning делает deprecation ненужным | ❌ ПОСЛЕДСТВИЕ: Stripe всё равно постепенно убирает старые версии через многолетние циклы; pinning только даёт буферное время для migration

## Q13. Webhook versioning?

**Webhooks** harder to version (server pushes, can't negotiate):

**Approaches:**
1. **Per-customer pinned version** (Stripe)
2. **Multiple webhook endpoints** для разных versions
3. **Header в webhook payload** indicating version
4. **Backwards-compatible only** (always add fields, never remove)

**Common pattern:** webhook payloads frozen forever, only add new event types.


> [!mcq]
> - [ ] Webhooks не нуждаются в versioning — клиент всегда получает latest format | ❌ ПОСЛЕДСТВИЕ: webhooks server-push; клиент не контролирует версию payload; изменение format сломает consumer который ожидает старую структуру
> - [ ] Webhook versioning проще чем API versioning | ❌ ПОСЛЕДСТВИЕ: сложнее; server push не позволяет negotiate версию per-request; клиент не может указать `Accept` header для incoming webhook
> - [x] Webhook payload format frozen per-account version; правило — только добавляй поля, никогда не удаляй | ✓ ПРИМЕНЯТЬ: Stripe-style per-account pinning или strict additive-only policy 📋 ПРАВИЛО: webhook = additive only; breaking change = новый event type 🔗 См. Q12
> - [ ] Лучший подход для webhook versioning — отдельные URL endpoints per version | ❌ ПОСЛЕДСТВИЕ: multiplies infrastructure; клиент должен настроить endpoint для каждой версии; Stripe-style per-account pinning элегантнее

## Q14. (!) Не versions в GraphQL?

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
> - [ ] GraphQL использует URI versioning как REST: `/graphql/v2` | ❌ ПОСЛЕДСТВИЕ: GraphQL purposely not versioned; additive schema evolution + @deprecated handles changes без URL fragmentation; отдельные URLs создают complexity без benefit
> - [ ] `@deprecated` немедленно удаляет поле из schema | ❌ ПОСЛЕДСТВИЕ: @deprecated только маркировка в schema; поле продолжает работать — clients продолжают его использовать пока не мигрируют; удаление — ручная операция после долгого периода
> - [x] GraphQL эволюционирует через additive changes + `@deprecated`; clients запрашивают только нужные поля — добавление новых не ломает ничего | ✓ ПРИМЕНЯТЬ: всегда для GraphQL; не добавляй URI versioning в GraphQL API 📋 ПРАВИЛО: GraphQL no-version = клиент сам выбирает поля 🔗 См. Q15
> - [ ] GraphQL `@deprecated` блокирует запросы к устаревшим полям | ❌ ПОСЛЕДСТВИЕ: поле продолжает отвечать данными; только tooling (IDE, linter) показывает предупреждения — breaking behavior не включается

## Q15. Field deprecation?

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
> - [ ] `@deprecated` автоматически удаляет поле через 30 дней | ❌ ПОСЛЕДСТВИЕ: @deprecated только декларативная маркировка; поле остаётся в schema пока разработчик вручную не удалит — нет автоматики
> - [ ] `@deprecated` доступен только в GraphQL; REST APIs не имеют эквивалента | ❌ ПОСЛЕДСТВИЕ: REST APIs используют `Deprecation` и `Sunset` HTTP headers (RFC 8594) — аналогичная механика, другой транспорт
> - [x] `@deprecated(reason: "Use firstName + lastName")` — IDE показывает strikethrough, linters предупреждают, docs помечают; поле остаётся работающим | ✓ ПРИМЕНЯТЬ: всегда при замене полей в GraphQL schema 📋 ПРАВИЛО: @deprecated = signal + reason, не removal 🔗 См. Q14
> - [ ] @deprecated останавливает GraphQL query если клиент запрашивает deprecated поле | ❌ ПОСЛЕДСТВИЕ: query выполняется успешно; @deprecated не влияет на runtime behaviour — только на tooling и documentation

## Q16. (!) Deprecation policy?

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
> - [ ] Deprecation policy применима только к breaking changes, не к endpoint removals | ❌ ПОСЛЕДСТВИЕ: endpoint removal — это и есть breaking change; без deprecation policy клиенты получают 404 в production без предупреждения
> - [x] Announce → Sunset header → 6-24 месяца deprecation period → migration guides → final removal | ✓ ПРИМЕНЯТЬ: все public API изменения требующие action от clients 📋 ПРАВИЛО: 6 месяцев minimum; communicate multiple times 🔗 См. Q17
> - [ ] Достаточно добавить Sunset header — клиенты сами найдут его | ❌ ПОСЛЕДСТВИЕ: клиенты не обязаны парсить Sunset header; без явного email/blog announcement большинство не узнает о deprecation
> - [ ] Stripe deprecates версии через 12 месяцев после анонса | ❌ ПОСЛЕДСТВИЕ: неверно; Stripe поддерживает ВСЕ исторические версии indefinitely — per-account pinning модель

## Q17. Sunset HTTP header?

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
> - [ ] Sunset header немедленно возвращает 410 Gone когда дата наступает | ❌ ПОСЛЕДСТВИЕ: Sunset header только предупреждает о будущем удалении; 410 Gone возвращается только после ручного отключения endpoint — автоматики нет
> - [ ] Sunset и Deprecation headers одно и то же | ❌ ПОСЛЕДСТВИЕ: разные назначения; `Deprecation` = когда endpoint устарел (past date); `Sunset` = когда будет удалён (future date); оба нужны для полной информации
> - [x] `Sunset: Sat, 31 Dec 2025 23:59:59 GMT` (RFC 8594) — сигнализирует о дате удаления; tooling парсит и нотифицирует разработчиков | ✓ ПРИМЕНЯТЬ: все deprecated endpoints; добавляй в каждый response 📋 ПРАВИЛО: Sunset = машиночитаемое предупреждение о дате удаления 🔗 См. Q16
> - [ ] Sunset header обязателен по HTTP стандарту | ❌ ПОСЛЕДСТВИЕ: RFC 8594 — informational RFC, не обязательный; Sunset header best practice, не requirement

## Q18. (!) Как gracefully deprecate API?

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
> - [ ] Graceful deprecation = просто вернуть 410 Gone через 1 месяц | ❌ ПОСЛЕДСТВИЕ: без предупреждения production системы клиентов упадут с 410; они не подготовились к migration — support nightmare и потеря доверия к API
> - [ ] Достаточно обновить docs и добавить Sunset header — разработчики найдут | ❌ ПОСЛЕДСТВИЕ: разработчики не читают docs каждый день; без email/changelog announcement большинство узнает о deprecation только когда API уже выключен
> - [x] Announce → Sunset header → track usage analytics → email reminders → migration code samples → 410 Gone | ✓ ПРИМЕНЯТЬ: все endpoint deprecations; communicate multiple times 📋 ПРАВИЛО: deprecation = active communication, не passive documentation 🔗 См. Q16
> - [ ] Redirect со старого на новый endpoint навсегда — лучшая стратегия | ❌ ПОСЛЕДСТВИЕ: redirect скрывает migration; клиенты не обновляют код; создаёт permanent dependency на translation layer; увеличивает latency

## Q19. (!) Versioning best practices?

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
> - [ ] Оптимально версионировать каждый minor change отдельным `/v1/`, `/v2/` | ❌ ПОСЛЕДСТВИЕ: version bloat; clients вынуждены обновляться постоянно; minor non-breaking changes не требуют новой версии — используй additive changes
> - [ ] Поддерживай все версии бесконечно — клиенты не любят migration | ❌ ПОСЛЕДСТВИЕ: операционная нагрузка растёт экспоненциально; каждый bugfix нужно применять ко всем версиям; Stripe-style бесконечная поддержка требует dedicated engineering team
> - [x] URI versioning по умолчанию + Sunset header + минимум 6 месяцев deprecation + API Gateway routing + limit 3-4 версии | ✓ ПРИМЕНЯТЬ: public REST APIs, любые APIs с external consumers 📋 ПРАВИЛО: pragmatic versioning = простота + clear deprecation policy 🔗 См. Q9
> - [ ] Для GraphQL нужна отдельная URI версия при major schema changes | ❌ ПОСЛЕДСТВИЕ: GraphQL специально не версионируется; schema evolution через deprecation + additive changes; `/graphql/v2` — антипаттерн

## Q20. Multiple versions параллельно — операционные расходы?

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

- [REST Maturity](rest-maturity-interview.md) — context
- [API Design Best Practices](api-design-best-practices-interview.md)
- [HTTP & REST](http-rest-interview.md) — основа
- [GraphQL](graphql-interview.md) — versioning approach differs
- [gRPC](grpc-interview.md) — versioning differs
- [OpenAPI / Swagger](openapi-swagger-interview.md) — documentation
- [Микросервисы](../architecture/microservices-interview.md) — versioning critical
- [API Gateway](../architecture/api-gateway-interview.md) — version routing
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — version rollouts
- [[backwards-compatibility-interview|Backwards Compatibility]] — если будем добавлять


> [!mcq]
> - [ ] API Gateway translation устраняет все operational costs поддержки multiple versions | ❌ ПОСЛЕДСТВИЕ: translation layer нужно поддерживать при каждом изменении schema; costs не нулевые — каждый новый тип данных требует transform для всех supported versions
> - [ ] Multiple versions не влияют на тестирование — достаточно тестировать только latest | ❌ ПОСЛЕДСТВИЕ: каждое изменение нужно верифицировать на всех supported versions; тест matrix растёт O(N) с числом версий; regression в старой версии обнаружится только в production
> - [x] Code complexity, testing matrix per version, bug fixes в multiple branches, deployment overhead — Stripe-style "all versions forever" исключение | ✓ ПРИМЕНЯТЬ: limit 3-4 parallel versions; aggressive sunset policy; API Gateway translation минимизирует code branching 📋 ПРАВИЛО: каждая дополнительная версия = постоянный operational tax 🔗 См. Q11
> - [ ] 10+ параллельных версий — норма для крупных компаний | ❌ ПОСЛЕДСТВИЕ: best practice — 3-4 версии max; 10+ создаёт неподъёмный testing matrix и code complexity; даже Stripe не рекомендует такой подход без специальной инфраструктуры
