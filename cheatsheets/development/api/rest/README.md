---
title: "REST API"
description: "Точка входа в раздел REST: дизайн ресурсов, HTTP-методы, версионирование, ошибки, безопасность."
tags:
  - meta
  - index
  - api
  - rest
type: "index"
updated: "2026-04-20"
---
# REST API

**REST** — архитектурный стиль для HTTP API: ресурсы, идентифицируемые URI, единый интерфейс (GET/POST/PUT/PATCH/DELETE), stateless, правильные статус-коды, кэширование через HTTP. REST остаётся дефолтом для публичных и внутренних API благодаря простоте, инструментам (Swagger, Postman) и совместимости с HTTP-инфраструктурой (прокси, CDN, LB).

Для кого: инженеры, проектирующие HTTP-ресурсы и ищущие консистентный набор правил для путей, ответов, ошибок и версионирования. Раздел собирает design-часть и practice-часть; также поможет при подготовке к собеседованию по API.

## Полезные ссылки

### Основные документы
- [rest-api-design](rest-api-design.md) — принципы REST, HTTP-методы, статус-коды, HATEOAS
- [rest-api-best-practices](rest-api-best-practices.md) — пагинация, фильтры, rate-limit, error handling, versioning

### Соседние разделы
- [Родительский раздел: API](../../../basics/README.md)
- [GraphQL](../../../basics/README.md) — альтернатива для разнородных клиентов
- [gRPC](../../../basics/README.md) — альтернатива для service-to-service
- [API Tools](../../../basics/README.md) — Postman, Insomnia, Swagger, тестирование
- [Swagger / OpenAPI](../../../basics/README.md)
- [API Testing](../../../basics/README.md)
- [Web Backend](../../../basics/README.md)
- [Spring Boot](../../../frameworks/java-frameworks/spring/)

### Внешние ресурсы
- [RFC 7231 — HTTP Semantics](https://tools.ietf.org/html/rfc7231)
- [RFC 9110 — HTTP Semantics (обновление)](https://www.rfc-editor.org/rfc/rfc9110.html)
- [REST API Tutorial](https://restfulapi.net/)
- [JSON:API Specification](https://jsonapi.org/)
- [RFC 7807 — Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc7807)
- [Google API Design Guide](https://cloud.google.com/apis/design)
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)

## Содержание

- [REST vs GraphQL vs gRPC](#rest-vs-graphql-vs-grpc)
- [Базовые правила REST](#базовые-правила-rest)
- [Карта тем](#карта-тем)
- [Чек-лист production REST API](#чек-лист-production-rest-api)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## REST vs GraphQL vs gRPC

| Критерий | REST | GraphQL | gRPC |
|----------|------|---------|------|
| Когда брать по умолчанию | Да (публичное API, CRUD) | Разнородные клиенты, BFF | Service-to-service, низкая латентность |
| Протокол / формат | HTTP/1.1+, JSON | HTTP, JSON | HTTP/2, protobuf |
| Контракт | OpenAPI (рекомендуется) | SDL (обязательно) | .proto (обязательно) |
| Кэширование | Лучшее (HTTP Cache-Control, ETag) | Сложнее (POST) | Нет на уровне HTTP |
| Стриминг | SSE / WebSocket | Subscriptions (WS) | Встроенный 4 вида |
| Debug | curl/Postman | GraphiQL | grpcurl |
| Over/under-fetching | Часто | Минимально | Контролируется |
| Типовая сложность | Низкая | Средняя | Средняя-высокая |

## Базовые правила REST

- **Ресурс** — существительное, множественное число: `/orders`, `/orders/{id}/items`. Глаголы только в исключениях (`/orders/{id}:cancel`).
- **HTTP-методы.** GET — безопасный идемпотентный; POST — создание (не идемпотентен); PUT — полная замена, идемпотентен; PATCH — частичное обновление; DELETE — удаление, идемпотентен.
- **Статус-коды.** 200 OK, 201 Created + Location, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 422 Unprocessable Entity, 429 Too Many Requests, 5xx только для серверных ошибок.
- **Ошибки.** RFC 7807 Problem Details: `type`, `title`, `status`, `detail`, `instance`.
- **Идемпотентность.** `Idempotency-Key` для POST платежей/заказов.
- **Версионирование.** `/v1/...` (URL) — простое и очевидное; `Accept: application/vnd.api+json;version=1` — «чистый» REST, но сложнее в инструментах.

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Принципы REST, уровни Ричардсона | [rest-api-design](rest-api-design.md#принципы-rest) |
| HTTP-методы и идемпотентность | [rest-api-design](rest-api-design.md#http-методы) |
| Статус-коды и Error handling | [rest-api-best-practices](rest-api-best-practices.md#error-handling) |
| Пагинация, фильтры, сортировка | [rest-api-best-practices](rest-api-best-practices.md#api-design-patterns) |
| Versioning стратегии | [rest-api-best-practices](rest-api-best-practices.md#versioning-strategies) |
| HATEOAS и гиперссылки | [rest-api-design](rest-api-design.md) |
| Безопасность (auth, rate-limit) | [rest-api-best-practices](rest-api-best-practices.md) |

## Чек-лист production REST API

- [ ] OpenAPI-спецификация в репо, линт Spectral в CI
- [ ] Единый формат ошибок (RFC 7807)
- [ ] Пагинация задана (cursor или offset + лимит)
- [ ] Rate-limit заголовки (`X-RateLimit-*`, `Retry-After`)
- [ ] Idempotency для критичных POST (payments, orders)
- [ ] ETag + Cache-Control для read-heavy endpoint-ов
- [ ] Версионирование и deprecation-политика задокументированы
- [ ] Security: OAuth2/JWT, HTTPS only, CSRF/CORS настроены
- [ ] Observability: correlation-id, метрики latency/error-rate, OpenAPI-based тесты
- [ ] Contract-тесты (Schemathesis/Pact) в PR-пайплайне

## Маршруты чтения

- **Первый REST-сервис (1 день):** `rest-api-design.md` `rest-api-best-practices.md` главы error handling, pagination, versioning OpenAPI Swagger UI.
- **Аудит существующего API:** чек-лист выше Spectral diff против прошлой версии исправление несовместимостей.
- **Собеседование по REST:** `rest-api-design.md` + `interview/api/http-rest-interview.md`.

## Куда идти дальше

- GraphQL как альтернатива — [README](../../../basics/README.md)
- gRPC для межсервисной коммуникации — [README](../../../basics/README.md)
- OpenAPI и документирование — [README](../../../basics/README.md)
- Тестирование API — [README](../../../basics/README.md)
- Backend-архитектура в целом — [README](../../../basics/README.md)
