---
title: "Вопросы на собеседовании: HTTP и REST"
description: "Практичные вопросы и ответы по HTTP/REST для Java-разработчика: семантика методов, статусы, заголовки, кэширование, CORS, версионирование, HATEOAS, безопасность, идемпотентность и тестирование."
tags:
  - interview
  - api
  - http-rest-interview
aliases:
  - "HTTP и REST"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `HTTP` и `REST`

Практичные вопросы и ответы по `HTTP`/`REST`: как проектировать API, выбирать семантику методов, настраивать кэширование и `CORS`, держать безопасность и диагностировать ошибки в production.

## Полезные ссылки

### Официальная документация

- [HTTP — MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP)
- [RFC 9110: HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110)
- [RFC 9111: HTTP Caching](https://www.rfc-editor.org/rfc/rfc9111)
- [RFC 6749: OAuth 2.0](https://www.rfc-editor.org/rfc/rfc6749)
- [REST Resource Naming Guide](https://restfulapi.net/resource-naming/)
- [REST with Spring Series](https://www.baeldung.com/rest-with-spring-series) — серия статей по построению REST API в Spring
- [Build a REST API with Spring and Java Config](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration) — построение REST API в Spring
- [Documenting a Spring REST API Using OpenAPI 3.0](https://www.baeldung.com/spring-rest-openapi-documentation) — документирование REST API через OpenAPI
- [A Guide to RestClient in Spring Boot](https://www.baeldung.com/spring-boot-restclient) — HTTP-клиент RestClient в Spring Boot

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [Как отвечать про HTTP/REST (короткий шаблон)](#как-отвечать-про-httprest-короткий-шаблон)
- [See also](#see-also)

**База HTTP/REST**
- [Q1. (!) Как коротко объяснить REST и его ограничения?](#q1-как-коротко-объяснить-rest-и-его-ограничения)
- [Q2. Что такое ресурс и как проектировать URI?](#q2-что-такое-ресурс-и-как-проектировать-uri)
- [Q3. (!) Как правильно выбирать HTTP-методы?](#q3-как-правильно-выбирать-http-методы)
- [Q4. В чем разница между безопасностью и идемпотентностью методов?](#q4-в-чем-разница-между-безопасностью-и-идемпотентностью-методов)
- [Q5. В чем реальная разница между PUT, PATCH и POST?](#q5-в-чем-реальная-разница-между-put-patch-и-post)

**HTTP-заголовки**
- [Q6. (!) Какие HTTP-заголовки должен знать каждый backend-разработчик?](#q6-какие-http-заголовки-должен-знать-каждый-backend-разработчик)
- [Q7. Как работает Content Negotiation?](#q7-как-работает-content-negotiation)
- [Q8. Что такое Transfer-Encoding и чем chunked отличается от Content-Length?](#q8-что-такое-transfer-encoding-и-чем-chunked-отличается-от-content-length)

**Статусы и ошибки**
- [Q9. (!) Как выбирать HTTP status code без хаоса?](#q9-как-выбирать-http-status-code-без-хаоса)
- [Q10. Какие status codes чаще всего путают на собеседованиях?](#q10-какие-status-codes-чаще-всего-путают-на-собеседованиях)
- [Q11. Как должен выглядеть хороший error response?](#q11-как-должен-выглядеть-хороший-error-response)

**Кэширование**
- [Q12. (!) Как работает HTTP-кэширование?](#q12-как-работает-http-кэширование)
- [Q13. Как работают ETag и conditional requests?](#q13-как-работают-etag-и-conditional-requests)
- [Q14. В чем разница между Cache-Control директивами?](#q14-в-чем-разница-между-cache-control-директивами)

**CORS**
- [Q15. (!) Что такое CORS и зачем он нужен?](#q15-что-такое-cors-и-зачем-он-нужен)
- [Q16. Что такое preflight-запрос и когда он отправляется?](#q16-что-такое-preflight-запрос-и-когда-он-отправляется)

**Версионирование и эволюция API**
- [Q17. (!) Какие стратегии версионирования REST API существуют?](#q17-какие-стратегии-версионирования-rest-api-существуют)
- [Q18. Зачем API-контракт и как его поддерживать?](#q18-зачем-api-контракт-и-как-его-поддерживать)
- [Q19. Что такое HATEOAS и нужен ли он на практике?](#q19-что-такое-hateoas-и-нужен-ли-он-на-практике)

**Идемпотентность и надежность**
- [Q20. (!) Как обеспечить идемпотентность на практике?](#q20-как-обеспечить-идемпотентность-на-практике)
- [Q21. Как реализовать пагинацию в REST API?](#q21-как-реализовать-пагинацию-в-rest-api)

**Аутентификация и безопасность**
- [Q22. (!) Какие схемы аутентификации используются в HTTP?](#q22-какие-схемы-аутентификации-используются-в-http)
- [Q23. Как работает JWT в контексте REST API?](#q23-как-работает-jwt-в-контексте-rest-api)
- [Q24. Как защищать REST API в production?](#q24-как-защищать-rest-api-в-production)
- [Q25. Какие anti-patterns в безопасности API встречаются чаще всего?](#q25-какие-anti-patterns-в-безопасности-api-встречаются-чаще-всего)

**Rate Limiting и устойчивость**
- [Q26. Как реализовать rate limiting в REST API?](#q26-как-реализовать-rate-limiting-в-rest-api)
- [Q27. Что делает REST API масштабируемым на практике?](#q27-что-делает-rest-api-масштабируемым-на-практике)

**HTTP/2 и современные протоколы**
- [Q28. (!) Чем HTTP/2 отличается от HTTP/1.1?](#q28-чем-http2-отличается-от-http11)
- [Q29. Когда REST лучше SOAP, а когда наоборот?](#q29-когда-rest-лучше-soap-а-когда-наоборот)
- [Q30. Когда REST хуже GraphQL/gRPC/WebSocket?](#q30-когда-rest-хуже-graphqlgrpcwebsocket)

**Cookies и управление состоянием**
- [Q31. Как работают cookies в HTTP и когда их используют в API?](#q31-как-работают-cookies-в-http-и-когда-их-используют-в-api)

**Тестирование и диагностика**
- [Q32. (!) Как тестировать REST API системно?](#q32-как-тестировать-rest-api-системно)
- [Q33. Как диагностировать проблемы REST API в production?](#q33-как-диагностировать-проблемы-rest-api-в-production)

**Дизайн и лучшие практики**
- [Q34. Как проектировать bulk-операции в REST API?](#q34-как-проектировать-bulk-операции-в-rest-api)
- [Q35. Как обрабатывать long-running операции в REST?](#q35-как-обрабатывать-long-running-операции-в-rest)
- [Q36. Как отвечать на HTTP/REST вопросы сильно на senior-раунде?](#q36-как-отвечать-на-httprest-вопросы-сильно-на-senior-раунде)

**Современные протоколы и расширенные темы**
- [Q37. Что такое HTTP/3 и QUIC? Ключевые отличия от HTTP/2](#q37-что-такое-http3-и-quic-ключевые-отличия-от-http2)
- [Q38. Что такое Server-Sent Events (SSE) и чем отличается от WebSocket?](#q38-что-такое-server-sent-events-sse-и-чем-отличается-от-websocket)
- [Q39. Content negotiation — Accept/Content-Type, media types, Spring MVC](#q39-content-negotiation--acceptcontent-type-media-types-spring-mvc)
- [Q40. Conditional requests — ETag, If-None-Match, If-Modified-Since и кэширование](#q40-conditional-requests--etag-if-none-match-if-modified-since-и-кэширование)
- [Q41. REST API versioning — URI vs Header vs Content-Type стратегии](#q41-rest-api-versioning--uri-vs-header-vs-content-type-стратегии)
- [Q42. Идемпотентность в REST — какие методы идемпотентны и почему?](#q42-идемпотентность-в-rest--какие-методы-идемпотентны-и-почему)
- [Q43. RFC 7807 Problem Details — структура и Spring 6 ProblemDetail](#q43-rfc-7807-problem-details--структура-и-spring-6-problemdetail)

## Как отвечать про HTTP/REST (короткий шаблон)

На интервью обычно ждут один и тот же каркас: сначала вы задаете контекст, то есть тип API, профиль нагрузки, SLA/SLO и требования безопасности, затем объясняете семантику решения через методы, статусы и контракт, после этого проговариваете trade-off и возможные альтернативы вроде `GraphQL`, `WebSocket`, `gRPC` или `SOAP`, и в конце показываете, как решение валидируется через тесты, метрики и наблюдаемость.

---

## Q1. (!) Как коротко объяснить REST и его ограничения?

`REST` (Representational State Transfer) — это архитектурный стиль для сетевых систем, определенный Роем Филдингом в 2000 году. Основные ограничения (constraints):

| Ограничение | Суть |
|---|---|
| **Client-Server** | Разделение ответственности: клиент отвечает за UI, сервер — за данные и логику |
| **Stateless** | Каждый запрос содержит всю информацию для обработки, сервер не хранит сессию |
| **Cacheable** | Ответы должны явно указывать, можно ли их кэшировать |
| **Uniform Interface** | Единый интерфейс: ресурсы, представления, self-descriptive messages, HATEOAS |
| **Layered System** | Клиент не знает, общается ли он с конечным сервером или с прокси |
| **Code on Demand** | (опциональное) Сервер может отправить исполняемый код клиенту |

Ключевая идея: клиент работает с **ресурсами**, а не с RPC-методами.

Ограничения тоже важно проговорить явно: `REST` неудобен для сложных графов данных из-за over/under-fetching, не оптимален для двунаправленного real-time обмена и хуже подходит для длинных stateful workflow с явным протоколом состояния. Сильный ответ звучит не как "REST всегда лучший", а как "REST хорош как базовый стиль, пока требования не уводят в другой протокол".


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Что такое ресурс и как проектировать URI? Частая ошибка в реальном коде.

Ресурс — это адресуемая бизнес-сущность (`/users`, `/orders/{orderId}`), а не операция (`/createUser`).  
`URI` должен описывать **что это**, а не **что сделать**.

Базовые правила:

```
# Хорошо — существительные, множественное число
GET  /api/v1/users
GET  /api/v1/users/42
GET  /api/v1/users/42/orders
POST /api/v1/users/42/orders

# Плохо — глаголы, действия в URI
POST /api/v1/createUser
GET  /api/v1/getUserOrders?userId=42
POST /api/v1/approveOrder
```

Правила проектирования:
- Используем существительные и множественное число для коллекций
- Строим иерархию только когда есть реальная вложенность (`/orders/{id}/items`)
- Держим `URI` стабильными и развиваем API через версионирование и контракт
- Фильтрацию, сортировку и пагинацию выносим в query-параметры: `/users?role=admin&sort=name&page=2`
- Не более двух уровней вложенности: `/users/{id}/orders` — ок, `/users/{id}/orders/{oid}/items/{iid}/reviews` — перебор


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Как правильно выбирать HTTP-методы? Частая ошибка в реальном коде.

Метод выбирают по семантике операции, а не по удобству клиента:

| Метод | Семантика | Безопасный | Идемпотентный | Тело запроса |
|---|---|---|---|---|
| `GET` | Чтение ресурса | да | да | нет |
| `POST` | Создание / неидемпотентная команда | нет | нет | да |
| `PUT` | Полная замена ресурса | нет | да | да |
| `PATCH` | Частичное обновление | нет | нет* | да |
| `DELETE` | Удаление ресурса | нет | да | редко |
| `HEAD` | Метаданные (как GET, но без тела) | да | да | нет |
| `OPTIONS` | Доступные методы / CORS preflight | да | да | нет |

*`PATCH` может быть идемпотентным в зависимости от реализации (JSON Merge Patch — да, JSON Patch — не всегда).

```http
GET /api/v1/users/42 HTTP/1.1
Host: api.example.com
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json

{"id": 42, "name": "Иван", "email": "ivan@example.com"}
```

Практический критерий: если повтор запроса после network timeout меняет итог непредсказуемо, то метод или контракт выбраны неверно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. В чем разница между безопасностью и идемпотентностью методов? Частая ошибка в реальном коде.

`Safe` означает, что запрос не должен менять состояние сервера — сюда относятся `GET`, `HEAD` и `OPTIONS`. `Idempotent` означает другое: повтор одинакового запроса приводит к тому же итоговому состоянию.

```mermaid
graph TD
    A[HTTP-методы] --> B[Safe + Idempotent]
    A --> C[Idempotent, но не Safe]
    A --> D[Не Safe, не Idempotent]
    B --> B1["GET, HEAD, OPTIONS"]
    C --> C1["PUT, DELETE"]
    D --> D1["POST, PATCH*"]
```

Ключевой момент: идемпотентность задается не названием метода, а реальной реализацией, включая retry и дедупликацию команд. Например, `DELETE /users/42` идемпотентен — повторный вызов вернет `404`, но состояние сервера не изменится. А `POST /payments` без idempotency key не идемпотентен — повтор может создать дубль платежа.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. В чем реальная разница между PUT, PATCH и POST? Частая ошибка в реальном коде.

| Аспект | `PUT` | `PATCH` | `POST` |
|---|---|---|---|
| Семантика | Полная замена | Частичное обновление | Создание / команда |
| Идемпотентность | Да | Зависит от формата | Нет |
| Тело запроса | Полное представление | Только изменения | Произвольное |
| Типичный ответ | `200` или `204` | `200` или `204` | `201` с `Location` |

```http
# PUT — полная замена
PUT /api/v1/users/42 HTTP/1.1
Content-Type: application/json

{"id": 42, "name": "Иван", "email": "ivan@new.com", "role": "admin"}

# PATCH — частичное обновление (JSON Merge Patch)
PATCH /api/v1/users/42 HTTP/1.1
Content-Type: application/merge-patch+json

{"email": "ivan@new.com"}

# POST — создание
POST /api/v1/users HTTP/1.1
Content-Type: application/json

{"name": "Иван", "email": "ivan@example.com"}

HTTP/1.1 201 Created
Location: /api/v1/users/42
```

На практике `PUT` проще поддерживать для идемпотентных retry, `PATCH` экономит трафик, но усложняет валидацию конфликтов, а `POST` опасно превращать в универсальный метод, потому что теряется предсказуемость контракта.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Какие HTTP-заголовки должен знать каждый backend-разработчик? Частая ошибка в реальном коде.

Заголовки делятся на несколько категорий:

**Запрос (Request headers):**

```http
GET /api/v1/users HTTP/1.1
Host: api.example.com
Accept: application/json          # желаемый формат ответа
Accept-Language: ru-RU, en;q=0.5  # предпочитаемый язык
Authorization: Bearer eyJhbGc...  # аутентификация
Content-Type: application/json    # формат тела запроса
If-None-Match: "abc123"           # conditional request (кэш)
X-Request-Id: 550e8400-e29b...    # трейсинг
```

**Ответ (Response headers):**

```http
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
Cache-Control: max-age=3600, public
ETag: "abc123"
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1618884000
Access-Control-Allow-Origin: https://app.example.com
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

**Категории заголовков:**

| Категория | Примеры |
|---|---|
| Контент | `Content-Type`, `Content-Length`, `Content-Encoding` |
| Кэширование | `Cache-Control`, `ETag`, `If-None-Match`, `Last-Modified` |
| Аутентификация | `Authorization`, `WWW-Authenticate` |
| CORS | `Origin`, `Access-Control-Allow-*` |
| Безопасность | `Strict-Transport-Security`, `X-Content-Type-Options`, `X-Frame-Options` |
| Трейсинг | `X-Request-Id`, `X-Correlation-Id`, `traceparent` (W3C) |
| Rate Limiting | `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `Retry-After` |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как работает Content Negotiation? Частая ошибка в реальном коде.

`Content Negotiation` — это механизм, позволяющий клиенту и серверу договориться о формате данных. Бывает двух видов:

**Server-driven** (основной) — клиент указывает предпочтения в заголовках:

```http
GET /api/v1/users/42 HTTP/1.1
Accept: application/json, application/xml;q=0.5
Accept-Language: ru-RU, en;q=0.3
Accept-Encoding: gzip, deflate
```

Значение `q` (quality factor, от 0 до 1) определяет приоритет. Если сервер не может удовлетворить ни один из форматов, он возвращает `406 Not Acceptable`.

**Agent-driven** — сервер возвращает список вариантов, клиент выбирает сам (используется редко).

В `Spring Boot` content negotiation настраивается через `ContentNegotiationConfigurer`:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Что такое Transfer-Encoding и чем chunked отличается от Content-Length? Частая ошибка в реальном коде.

Два подхода к передаче тела HTTP-ответа:

**`Content-Length`** — сервер заранее знает размер и сообщает его:

```http
HTTP/1.1 200 OK
Content-Length: 1234
Content-Type: application/json

{"data": "...1234 байт..."}
```

**`Transfer-Encoding: chunked`** — сервер отправляет данные порциями, не зная итоговый размер заранее:

```http
HTTP/1.1 200 OK
Transfer-Encoding: chunked
Content-Type: application/json

1a
{"users": [{"id": 1},
1c
{"id": 2}, {"id": 3}]}
0

```

Каждый chunk начинается с его размера в hex, заканчивается chunk размером `0`.

`Chunked` незаменим для:
- Стриминга больших ответов (экспорт CSV, генерация отчетов)
- Server-Sent Events
- Ответов, где размер неизвестен до завершения обработки

Важно: `Content-Length` и `Transfer-Encoding: chunked` взаимоисключающие — нельзя использовать оба одновременно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Как выбирать HTTP status code без хаоса? Частая ошибка в реальном коде.

Нужна короткая и стабильная политика. Вот шпаргалка по самым используемым кодам:

```mermaid
graph TD
    R[Запрос] --> S{Успешно?}
    S -->|Да| S2{Что вернуть?}
    S2 -->|Есть тело| 200["200 OK"]
    S2 -->|Создали ресурс| 201["201 Created"]
    S2 -->|Нет тела| 204["204 No Content"]
    S2 -->|Не изменилось| 304["304 Not Modified"]
    S -->|Нет| E{Чья ошибка?}
    E -->|Клиента| C{Какая?}
    C -->|Плохой запрос| 400["400 Bad Request"]
    C -->|Не аутентифицирован| 401["401 Unauthorized"]
    C -->|Нет прав| 403["403 Forbidden"]
    C -->|Не найдено| 404["404 Not Found"]
    C -->|Метод не разрешен| 405["405 Method Not Allowed"]
    C -->|Конфликт| 409["409 Conflict"]
    C -->|Слишком много запросов| 429["429 Too Many Requests"]
    E -->|Сервера| 500["500 Internal Server Error"]
```

**Золотое правило**: один и тот же тип ошибки всегда должен маппиться в один и тот же status code. Самый вредный антипаттерн — возвращать `200 OK` и прятать ошибку в теле ответа.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Какие status codes чаще всего путают на собеседованиях? Частая ошибка в реальном коде.

| Путаница | Разница |
|---|---|
| `401` vs `403` | `401 Unauthorized` — не аутентифицирован (нет токена или он невалиден). `403 Forbidden` — аутентифицирован, но нет прав |
| `404` vs `410` | `404` — ресурс не найден (может появиться). `410 Gone` — ресурс удален навсегда |
| `301` vs `302` vs `308` | `301` — перманентный редирект (метод может измениться на `GET`). `302` — временный (метод может измениться). `308` — перманентный с сохранением метода |
| `200` vs `204` | `200` — успех с телом ответа. `204` — успех без тела (например, после `DELETE`) |
| `400` vs `422` | `400` — синтаксически некорректный запрос. `422 Unprocessable Entity` — синтаксис верный, но семантически невалидный |
| `502` vs `503` vs `504` | `502` — upstream вернул невалидный ответ. `503` — сервис временно недоступен. `504` — upstream не ответил вовремя |

```http
# 401 — нет токена
GET /api/v1/users HTTP/1.1

HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer realm="api"

# 403 — токен есть, но прав нет
GET /api/v1/admin/settings HTTP/1.1
Authorization: Bearer eyJ...user_role...

HTTP/1.1 403 Forbidden
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как должен выглядеть хороший error response? Частая ошибка в реальном коде.

Хороший error response одновременно полезен машине и человеку. Рекомендуемый формат (на базе RFC 9457 — Problem Details):

```json
{
  "type": "https://api.example.com/errors/insufficient-funds",
  "title": "Insufficient Funds",
  "status": 422,
  "detail": "Недостаточно средств на счете. Баланс: 100.00, запрошено: 250.00",
  "instance": "/api/v1/payments/abc-123",
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-04-11T10:30:00Z",
  "errors": [
    {
      "field": "amount",
      "code": "INSUFFICIENT_BALANCE",
      "message": "Сумма превышает доступный баланс"
    }
  ]
}
```

В `Spring Boot` это реализуется через `@ControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Validation Error");
        problem.setProperty("errors", ex.getFieldErrors().stream()
            .map(e -> Map.of(
                "field", e.getField(),
                "message", e.getDefaultMessage()))
            .toList());
        return ResponseEntity.badRequest().body(problem);
    }
}
```

Для production обязательно исключают утечку внутренних деталей: `stacktrace`, SQL-текст, внутренние имена сервисов.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Как работает HTTP-кэширование? Частая ошибка в реальном коде.

HTTP-кэширование снижает нагрузку и latency. Существует несколько уровней кэша:

```mermaid
graph LR
    Client[Клиент<br/>Browser Cache] --> CDN[CDN<br/>Edge Cache]
    CDN --> GW[API Gateway<br/>Reverse Proxy]
    GW --> App[Приложение<br/>Application Cache]
    App --> DB[(База данных)]
```

Основные заголовки кэширования:

```http
# Ответ с кэшированием
HTTP/1.1 200 OK
Cache-Control: public, max-age=3600
ETag: "v1-abc123"
Last-Modified: Thu, 10 Apr 2026 12:00:00 GMT
Vary: Accept, Accept-Encoding
```

**Два механизма валидации кэша:**

1. **Time-based** — `Cache-Control: max-age=3600` (кэш валиден 1 час)
2. **Conditional** — клиент спрашивает сервер, изменились ли данные:

```http
# Клиент: "у меня есть версия abc123, она актуальна?"
GET /api/v1/products/42 HTTP/1.1
If-None-Match: "v1-abc123"

# Сервер: "да, данные не изменились"
HTTP/1.1 304 Not Modified
ETag: "v1-abc123"
```

Кэш экономит bandwidth и снижает latency, но требует продуманной стратегии инвалидации — одна из самых сложных задач в distributed systems.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как работают ETag и conditional requests? Частая ошибка в реальном коде.

`ETag` (Entity Tag) — это идентификатор версии ресурса. Бывает двух видов:

- **Strong ETag** (`"abc123"`) — байт-в-байт идентичность. Подходит для `If-Match` и range requests
- **Weak ETag** (`W/"abc123"`) — семантическая эквивалентность. Подходит для `If-None-Match`

```http
# 1. Первый запрос — получаем ETag
GET /api/v1/products/42 HTTP/1.1

HTTP/1.1 200 OK
ETag: "v2-def456"
Content-Type: application/json

{"id": 42, "name": "Товар", "price": 999}

# 2. Повторный запрос — проверяем актуальность
GET /api/v1/products/42 HTTP/1.1
If-None-Match: "v2-def456"

HTTP/1.1 304 Not Modified

# 3. Optimistic Locking при обновлении
PUT /api/v1/products/42 HTTP/1.1
If-Match: "v2-def456"
Content-Type: application/json

{"id": 42, "name": "Товар обновленный", "price": 1099}

# Если кто-то уже изменил ресурс:
HTTP/1.1 412 Precondition Failed
```

`ETag` + `If-Match` — это стандартный способ реализации **optimistic locking** через HTTP без собственного протокола версионирования.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. В чем разница между Cache-Control директивами? Это антипаттерн или неправильный выбор в production.

| Директива | Значение |
|---|---|
| `public` | Может кэшироваться CDN и прокси |
| `private` | Только браузерный кэш (персональные данные) |
| `no-cache` | Кэшировать можно, но перед использованием **обязательна** ревалидация |
| `no-store` | **Не кэшировать** вообще (чувствительные данные) |
| `max-age=N` | Кэш валиден N секунд |
| `s-maxage=N` | То же, но только для shared caches (CDN, прокси) |
| `must-revalidate` | После истечения `max-age` обязательно ревалидировать |
| `immutable` | Ресурс никогда не изменится (для versioned assets) |

Типичные комбинации:

```http
# Публичный API — кэшировать на CDN 5 минут
Cache-Control: public, max-age=300, s-maxage=600

# Персональные данные — только браузерный кэш
Cache-Control: private, max-age=60

# Статические ассеты с хэшем в имени
Cache-Control: public, max-age=31536000, immutable

# Чувствительные данные (банковский баланс)
Cache-Control: no-store

# Данные меняются часто, но кэш полезен для 304
Cache-Control: no-cache
ETag: "xyz789"
```

Частая ошибка: путать `no-cache` и `no-store`. `no-cache` разрешает кэширование, но требует ревалидации. `no-store` запрещает кэширование полностью.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. (!) Что такое CORS и зачем он нужен? Частая ошибка в реальном коде.

`CORS` (Cross-Origin Resource Sharing) — это механизм, позволяющий браузеру делать запросы к другому origin (домен + порт + протокол). Без `CORS` браузер блокирует cross-origin запросы из-за Same-Origin Policy.

```mermaid
sequenceDiagram
    participant B as Браузер<br/>(https://app.com)
    participant S as API-сервер<br/>(https://api.com)
    
    Note over B,S: Простой запрос (GET, без custom headers)
    B->>S: GET /api/users<br/>Origin: https://app.com
    S->>B: 200 OK<br/>Access-Control-Allow-Origin: https://app.com
    
    Note over B,S: Запрос с preflight (PUT, custom headers)
    B->>S: OPTIONS /api/users/42<br/>Origin: https://app.com<br/>Access-Control-Request-Method: PUT<br/>Access-Control-Request-Headers: Authorization
    S->>B: 204 No Content<br/>Access-Control-Allow-Origin: https://app.com<br/>Access-Control-Allow-Methods: GET, PUT, DELETE<br/>Access-Control-Allow-Headers: Authorization<br/>Access-Control-Max-Age: 3600
    B->>S: PUT /api/users/42<br/>Origin: https://app.com<br/>Authorization: Bearer eyJ...
    S->>B: 200 OK
```

В `Spring Boot`:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://app.example.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("Authorization", "Content-Type")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

Важно: **никогда не используйте** `Access-Control-Allow-Origin: *` в production с `allowCredentials(true)` — это уязвимость.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Что такое preflight-запрос и когда он отправляется? Частая ошибка в реальном коде.

Preflight — это `OPTIONS`-запрос, который браузер отправляет **автоматически** перед "непростыми" cross-origin запросами.

**Простые запросы** (без preflight):
- Методы: `GET`, `HEAD`, `POST`
- Заголовки: только `Accept`, `Accept-Language`, `Content-Language`, `Content-Type` (с ограничениями)
- `Content-Type`: только `application/x-www-form-urlencoded`, `multipart/form-data`, `text/plain`

**Непростые запросы** (с preflight):
- Любой метод кроме `GET`/`HEAD`/`POST`
- Custom заголовки (например, `Authorization`, `X-Request-Id`)
- `Content-Type: application/json`

```http
# Preflight-запрос (отправляет браузер автоматически)
OPTIONS /api/v1/users HTTP/1.1
Host: api.example.com
Origin: https://app.example.com
Access-Control-Request-Method: PUT
Access-Control-Request-Headers: Authorization, Content-Type

# Ответ сервера
HTTP/1.1 204 No Content
Access-Control-Allow-Origin: https://app.example.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Authorization, Content-Type
Access-Control-Max-Age: 86400
```

`Access-Control-Max-Age` позволяет браузеру кэшировать результат preflight и не отправлять `OPTIONS` при каждом запросе.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Какие стратегии версионирования REST API существуют? Частая ошибка в реальном коде.

| Стратегия | Пример | Плюсы | Минусы |
|---|---|---|---|
| **URI path** | `/api/v1/users` | Простота, наглядность, кэшируемость | Загрязняет URI, ломает HATEOAS |
| **Query parameter** | `/api/users?version=1` | Не меняет path | Легко забыть, проблемы с кэшированием |
| **Custom header** | `Api-Version: 1` | Чистый URI | Не видна в URL, сложнее тестировать |
| **Accept header** | `Accept: application/vnd.api.v1+json` | Семантически правильно | Сложнее, труднее для документации |

На практике **URI path** (`/api/v1/`) — самая распространенная стратегия. Она проста, очевидна и хорошо работает с кэшированием и документацией.

```http
# URI path versioning (самый популярный)
GET /api/v1/users/42 HTTP/1.1
GET /api/v2/users/42 HTTP/1.1

# Accept header versioning (семантически чище)
GET /api/users/42 HTTP/1.1
Accept: application/vnd.myapi.v2+json

# Custom header versioning
GET /api/users/42 HTTP/1.1
Api-Version: 2
```

Стратегия обратной совместимости:
1. Новые поля добавляем без breaking change
2. Старые поля помечаем как deprecated, но не удаляем сразу
3. Breaking changes — только с новой версией и миграционным периодом
4. Документируем sunset-даты через заголовок `Sunset`


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Зачем API-контракт и как его поддерживать? Частая ошибка в реальном коде.

Контракт в формате `OpenAPI` — это источник истины о поведении API, который синхронизирует backend, frontend и внешних интеграторов, упрощает генерацию SDK и документации, а также делает contract testing и релизы безопаснее.

```yaml
# openapi.yaml (фрагмент)
openapi: 3.1.0
info:
  title: User API
  version: 1.0.0
paths:
  /api/v1/users/{id}:
    get:
      operationId: getUserById
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Пользователь найден
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
        '404':
          description: Пользователь не найден
```

Рабочая модель: изменения контракта проходят review, CI проверяет схему и backward compatibility (инструменты: `openapi-diff`, `spectral`), а breaking changes выпускают через версионирование и заранее оговоренный миграционный период.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. Что такое HATEOAS и нужен ли он на практике? Частая ошибка в реальном коде.

`HATEOAS` (Hypermedia as the Engine of Application State) — это ограничение `REST`, при котором сервер в ответе предоставляет ссылки на возможные следующие действия. Клиент не должен "знать" URL-ы заранее — он обнаруживает их из ответов.

```json
{
  "id": 42,
  "name": "Иван",
  "email": "ivan@example.com",
  "status": "active",
  "_links": {
    "self": {"href": "/api/v1/users/42"},
    "orders": {"href": "/api/v1/users/42/orders"},
    "deactivate": {"href": "/api/v1/users/42/deactivate", "method": "POST"},
    "update": {"href": "/api/v1/users/42", "method": "PUT"}
  }
}
```

В `Spring Boot` реализуется через `Spring HATEOAS`:

```java
@GetMapping("/users/{id}")
public EntityModel<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return EntityModel.of(user,
        linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
        linkTo(methodOn(UserController.class).getUserOrders(id)).withRel("orders"));
}
```

**На практике полный HATEOAS используется редко.** Причины:
- Большинство клиентов (SPA, мобильные приложения) всё равно хардкодят URL-ы
- Добавляет overhead к размеру ответа
- Усложняет контракт

Частичное использование (ссылки пагинации `next`/`prev`, `self`) встречается чаще и практически полезно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Как обеспечить идемпотентность на практике? Частая ошибка в реальном коде.

Идемпотентность критична для надежности: при network timeout клиент не знает, дошел ли запрос, и должен иметь возможность безопасно повторить его.

**Паттерн Idempotency Key:**

```mermaid
sequenceDiagram
    participant C as Клиент
    participant S as Сервер
    participant DB as БД
    
    C->>S: POST /payments<br/>Idempotency-Key: abc-123<br/>{"amount": 1000}
    S->>DB: Проверить ключ abc-123
    DB-->>S: Ключ не найден
    S->>DB: Создать платеж + сохранить ключ
    S->>C: 201 Created
    
    Note over C,S: Timeout — клиент повторяет
    C->>S: POST /payments<br/>Idempotency-Key: abc-123<br/>{"amount": 1000}
    S->>DB: Проверить ключ abc-123
    DB-->>S: Ключ найден, результат: 201
    S->>C: 201 Created (из кэша)
```

Реализация:

```java
@PostMapping("/payments")
public ResponseEntity<Payment> createPayment(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody PaymentRequest request) {
    
    // Проверяем, был ли запрос с таким ключом
    return idempotencyService.executeIdempotent(idempotencyKey, () -> {
        Payment payment = paymentService.create(request);
        return ResponseEntity.created(
            URI.create("/api/v1/payments/" + payment.getId()))
            .body(payment);
    });
}
```

Правила:
- `Idempotency-Key` генерирует **клиент** (обычно `UUID`)
- Ключ хранится на сервере вместе с результатом (TTL — 24-48 часов)
- При повторном запросе с тем же ключом возвращается **сохраненный результат**, а не выполняется повторно


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Как реализовать пагинацию в REST API? Частая ошибка в реальном коде.

Три основных подхода:

**1. Offset-based (самый простой):**

```http
GET /api/v1/users?page=2&size=20 HTTP/1.1

HTTP/1.1 200 OK
Content-Type: application/json

{
  "content": [...],
  "page": 2,
  "size": 20,
  "totalElements": 156,
  "totalPages": 8
}
```

Проблема: при вставке/удалении записей элементы "сдвигаются" — можно пропустить или получить дубли.

**2. Cursor-based (надежнее):**

```http
GET /api/v1/users?cursor=eyJpZCI6NDJ9&size=20 HTTP/1.1

HTTP/1.1 200 OK
Content-Type: application/json

{
  "content": [...],
  "nextCursor": "eyJpZCI6NjJ9",
  "hasMore": true
}
```

`Cursor` — это закодированная позиция последнего элемента. Работает стабильно при изменении данных, но не позволяет "прыгнуть" на произвольную страницу.

**3. Keyset-based:**

```http
GET /api/v1/users?after_id=42&size=20 HTTP/1.1
```

Использует значение ключа последнего элемента. Эффективно для больших таблиц (не требует `OFFSET` в SQL), но работает только с упорядоченными данными.

| Подход | Произвольный доступ | Стабильность | Производительность |
|---|---|---|---|
| Offset | Да | Низкая | O(n) на больших offset |
| Cursor | Нет | Высокая | O(1) |
| Keyset | Нет | Высокая | O(1) |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Какие схемы аутентификации используются в HTTP? Частая ошибка в реальном коде.

Заголовок `Authorization` поддерживает несколько стандартных схем:

```http
# Basic (base64 login:password) — только через HTTPS!
Authorization: Basic dXNlcjpwYXNzd29yZA==

# Bearer (OAuth2 / JWT токен)
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...

# API Key (через custom header)
X-API-Key: sk-abc123def456

# Digest (challenge-response, редко)
Authorization: Digest username="user", realm="api", nonce="..."
```

| Схема | Когда использовать | Безопасность |
|---|---|---|
| `Basic` | Внутренние сервисы, service-to-service (через HTTPS) | Низкая (пароль при каждом запросе) |
| `Bearer` (JWT) | SPA, мобильные приложения, микросервисы | Средняя (нужна ротация, revocation) |
| `Bearer` (opaque + introspection) | Когда важна немедленная отзывность | Высокая (проверка на auth-сервере) |
| `API Key` | Публичные API, интеграции | Средняя (нужна ротация) |
| `mTLS` | Service mesh, zero-trust | Высокая |

Для server-to-server взаимодействия в production предпочитают `mTLS` или `OAuth2 Client Credentials`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Как работает JWT в контексте REST API? Частая ошибка в реальном коде.

`JWT` (JSON Web Token) — это self-contained токен, состоящий из трех частей:

```
eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyNDIiLCJyb2xlcyI6WyJBRE1JTiJdLCJleHAiOjE3MTg4ODQwMDB9.подпись
|---- Header ----|-------------- Payload -------------------------|-- Signature --|
```

```json
// Header
{"alg": "RS256", "typ": "JWT"}

// Payload
{
  "sub": "user42",
  "roles": ["ADMIN"],
  "iss": "auth.example.com",
  "exp": 1718884000,
  "iat": 1718880400
}
```

```mermaid
sequenceDiagram
    participant C as Клиент
    participant Auth as Auth Server
    participant API as API Server

    C->>Auth: POST /oauth/token<br/>(credentials)
    Auth->>C: 200 OK<br/>{"access_token": "eyJ...", "expires_in": 3600}
    C->>API: GET /api/users<br/>Authorization: Bearer eyJ...
    API->>API: Проверить подпись<br/>Проверить exp, iss, aud
    API->>C: 200 OK
```

**Плюсы JWT**: не нужна БД для валидации, подходит для микросервисов.  
**Минусы**: нельзя отозвать до истечения `exp` (без дополнительного blacklist), размер (vs opaque token).

Правила безопасности:
- Используйте `RS256` или `ES256` (асимметричные алгоритмы), не `HS256` в распределенных системах
- Короткий `exp` (5-15 минут) + `refresh_token`
- Не храните чувствительные данные в payload — он лишь base64-encoded, не зашифрован


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. Как защищать REST API в production? Частая ошибка в реальном коде.

Минимальный production baseline:

```http
# Обязательные security-заголовки в каждом ответе
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Content-Security-Policy: default-src 'self'
Referrer-Policy: strict-origin-when-cross-origin
```

Чеклист безопасности:

1. **Transport**: `TLS` на всех участках, запрет слабых cipher/protocol версий
2. **AuthN/AuthZ**: `OAuth2`, JWT/introspection или `mTLS` по контексту
3. **Input validation**: строгая server-side валидация, sanitization
4. **Rate limiting**: `429 Too Many Requests` + `Retry-After`
5. **Secrets**: vault / secret manager, ротация ключей
6. **Least privilege**: минимальные scopes, короткоживущие токены
7. **Logging**: аудит доступа без логирования секретов и PII
8. **SDLC**: threat modeling, SAST/DAST, dependency scanning


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Какие anti-patterns в безопасности API встречаются чаще всего? Частая ошибка в реальном коде.

| Anti-pattern | Риск | Контрмера |
|---|---|---|
| Долгоживущие токены без ротации | Украденный токен действует месяцами | Короткий `exp` + `refresh_token` |
| Слишком широкие scopes/roles | Любой пользователь = admin | Least privilege, RBAC/ABAC |
| Отсутствие `429` | DDoS, credential stuffing | Rate limiting + backpressure |
| Секреты и PII в логах | Утечка credentials | Маскирование, structured logging |
| Доверие client input | SQL injection, XSS | Server-side validation + sanitization |
| Verbose error messages | Раскрытие архитектуры | Generic messages в prod, details в traceId |
| `CORS: *` + credentials | CSRF-подобные атаки | Whitelist конкретных origins |

На интервью сильнее звучит не просто список проблем, а связка "риск + конкретная контрмера".


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Как реализовать rate limiting в REST API? Это антипаттерн или неправильный выбор в production.

`Rate limiting` ограничивает количество запросов от клиента за период времени. Реализуется на уровне API Gateway или в приложении.

**Заголовки rate limiting (draft стандарт IETF):**

```http
HTTP/1.1 200 OK
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 742
X-RateLimit-Reset: 1618884000

# При превышении лимита
HTTP/1.1 429 Too Many Requests
Retry-After: 60
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 0
```

**Основные алгоритмы:**

| Алгоритм | Принцип | Когда использовать |
|---|---|---|
| Fixed Window | N запросов за фиксированное окно | Простые случаи |
| Sliding Window | Скользящее окно, плавнее | Большинство API |
| Token Bucket | Токены накапливаются, позволяя burst | Burst-трафик допустим |
| Leaky Bucket | Фиксированная скорость обработки | Строгое ограничение скорости |

В `Spring Boot` можно реализовать через `Bucket4j` или `Resilience4j`:

```java
@Bean
public Bucket createBucket() {
    Bandwidth limit = Bandwidth.classic(100, 
        Refill.greedy(100, Duration.ofMinutes(1)));
    return Bucket.builder().addLimit(limit).build();
}
```

В production rate limiting обычно выносят на уровень API Gateway (`Kong`, `Envoy`, `nginx`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. Что делает REST API масштабируемым на практике? Частая ошибка в реальном коде.

Масштабируемость в `REST` строится на нескольких уровнях:

1. **Stateless** — нет server session affinity, любой инстанс обработает любой запрос
2. **Кэширование** — `Cache-Control`, `ETag`, conditional requests, CDN
3. **Пагинация и фильтрация** — вместо попытки "отдать все"
4. **Идемпотентные retry** — с контролем timeout/retry budget
5. **Rate limiting** — quotas и backpressure на границе системы
6. **Async для heavy operations** — `202 Accepted` + polling / webhook

```mermaid
graph TB
    C[Clients] --> LB[Load Balancer]
    LB --> CDN[CDN / Edge Cache]
    CDN --> GW[API Gateway<br/>Rate Limiting, Auth]
    GW --> S1[Service Instance 1]
    GW --> S2[Service Instance 2]
    GW --> S3[Service Instance 3]
    S1 --> Cache[(Redis Cache)]
    S2 --> Cache
    S3 --> Cache
    Cache --> DB[(Database<br/>Read Replicas)]
```

Эксплуатационная часть не менее важна: метрики `RPS`, error rate и latency p95/p99, трассировка и SLO-алерты.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. (!) Чем HTTP/2 отличается от HTTP/1.1? Частая ошибка в реальном коде.

| Аспект | HTTP/1.1 | HTTP/2 |
|---|---|---|
| Формат | Текстовый | Бинарный (frames) |
| Мультиплексирование | Нет (1 запрос/соединение или pipelining) | Да (множество потоков в 1 соединении) |
| Сжатие заголовков | Нет | HPACK |
| Server Push | Нет | Да (сервер отправляет ресурсы проактивно) |
| Приоритизация | Нет | Да (stream priorities) |
| TLS | Опционально | Фактически обязателен (все браузеры) |

```mermaid
graph LR
    subgraph "HTTP/1.1"
        C1[Клиент] -->|"Соединение 1: GET /api"| S1[Сервер]
        C1 -->|"Соединение 2: GET /img"| S1
        C1 -->|"Соединение 3: GET /css"| S1
    end
    
    subgraph "HTTP/2"
        C2[Клиент] -->|"1 соединение:<br/>Stream 1: GET /api<br/>Stream 2: GET /img<br/>Stream 3: GET /css"| S2[Сервер]
    end
```

Для REST API переход на HTTP/2 дает:
- Снижение latency за счет мультиплексирования (нет head-of-line blocking на уровне HTTP)
- Экономию ресурсов (меньше TCP-соединений)
- Сжатие повторяющихся заголовков (`Authorization`, `Content-Type`)

**HTTP/3** (QUIC) идет дальше: устраняет head-of-line blocking на уровне TCP, использует UDP + собственный congestion control.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. Когда REST лучше SOAP, а когда наоборот? Частая ошибка в реальном коде.

| Критерий | REST | SOAP |
|---|---|---|
| Формат данных | JSON, XML, любой | Только XML |
| Контракт | OpenAPI (опционально) | WSDL (обязательно) |
| Транспорт | HTTP | HTTP, JMS, SMTP |
| Стандарты безопасности | OAuth2, JWT, TLS | WS-Security, WS-Trust |
| Транзакции | Нет стандарта | WS-AtomicTransaction |
| Простота интеграции | Высокая | Низкая |
| Типичное применение | Web/mobile API | Enterprise, банки, SOAP-legacy |

`REST` обычно лучше, когда важны простая интеграция, скорость разработки и web/mobile-экосистема. `SOAP` разумен, когда требуется строгий формальный контракт, enterprise governance и специфичные WS-* требования. Сильный ответ всегда привязан к требованиям и стоимости сопровождения.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. Когда REST хуже GraphQL/gRPC/WebSocket? Частая ошибка в реальном коде.

| Сценарий | Лучший выбор | Почему |
|---|---|---|
| Сложные графы данных, гибкий выбор полей | `GraphQL` | Решает over/under-fetching |
| Real-time двунаправленный обмен | `WebSocket` | Постоянное соединение, push |
| Высокопроизводительный service-to-service | `gRPC` | Бинарный протокол, streaming, code-gen |
| Server-Sent Events (одностороний push) | `SSE` | Проще WebSocket, работает через HTTP |
| Простой CRUD с понятной ресурсной моделью | `REST` | Стандартность, кэширование, инструменты |

```http
# Server-Sent Events (SSE) — когда нужен push без WebSocket
GET /api/v1/events/stream HTTP/1.1
Accept: text/event-stream

HTTP/1.1 200 OK
Content-Type: text/event-stream

data: {"event": "order_created", "orderId": 42}

data: {"event": "order_shipped", "orderId": 42}
```

Зрелый trade-off: не заменять `REST` везде, а комбинировать протоколы по типу нагрузки.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q31. Как работают cookies в HTTP и когда их используют в API? Частая ошибка в реальном коде.

`Cookies` — механизм хранения состояния на стороне клиента, который автоматически отправляется с каждым запросом к серверу.

```http
# Сервер устанавливает cookie
HTTP/1.1 200 OK
Set-Cookie: session_id=abc123; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=3600

# Браузер отправляет cookie автоматически
GET /api/v1/profile HTTP/1.1
Cookie: session_id=abc123
```

**Атрибуты безопасности:**

| Атрибут | Назначение |
|---|---|
| `HttpOnly` | Недоступна из JavaScript (защита от XSS) |
| `Secure` | Отправляется только по HTTPS |
| `SameSite=Strict` | Не отправляется при cross-site запросах (защита от CSRF) |
| `SameSite=Lax` | Отправляется при навигации, но не при cross-site POST |
| `Max-Age` / `Expires` | Время жизни cookie |
| `Domain` | Домен, для которого действует cookie |

**Cookies vs Bearer tokens в API:**
- `Cookies` — подходят для browser-based приложений (SSR, MPA), автоматически обрабатываются браузером, но привязаны к домену и подвержены CSRF
- `Bearer tokens` — подходят для SPA, мобильных приложений, service-to-service; более гибкие, но требуют ручного управления в клиентском коде


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q32. (!) Как тестировать REST API системно? Частая ошибка в реальном коде.

Системное тестирование REST API строится как пирамида:

```mermaid
graph TB
    E2E["E2E / Smoke Tests<br/>(мало, критические пути)"]
    INT["Integration Tests<br/>(API-граница, БД, брокеры)"]
    CONTRACT["Contract Tests<br/>(OpenAPI compliance)"]
    UNIT["Unit Tests<br/>(бизнес-логика, валидация)"]
    
    E2E --> INT --> CONTRACT --> UNIT
    
    style UNIT fill:#4CAF50,color:white
    style CONTRACT fill:#8BC34A,color:white
    style INT fill:#FF9800,color:white
    style E2E fill:#F44336,color:white
```

**Примеры тестирования с `curl`:**

```bash
# Тест GET — успешный запрос
curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  https://api.example.com/api/v1/users/42
# Ожидается: 200

# Тест POST — создание ресурса
curl -s -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Иван", "email": "ivan@example.com"}' \
  https://api.example.com/api/v1/users
# Ожидается: 201 + Location header

# Тест 404
curl -s -o /dev/null -w "%{http_code}" \
  https://api.example.com/api/v1/users/999999
# Ожидается: 404

# Тест rate limiting
for i in $(seq 1 110); do
  curl -s -o /dev/null -w "%{http_code}\n" \
    https://api.example.com/api/v1/health
done
# На 101-м запросе ожидается: 429
```

В `Spring Boot` — через `MockMvc` или `WebTestClient`:

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200WhenUserExists() throws Exception {
        mockMvc.perform(get("/api/v1/users/42")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").value("not-found"));
    }
}
```

Дополняют пирамиду: нагрузочные тесты (`latency`, `RPS`), security tests (auth bypass, injection), resilience tests (timeout, retry storm).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q33. Как диагностировать проблемы REST API в production? Частая ошибка в реальном коде.

Три столпа наблюдаемости:

**1. Метрики (Metrics):**
- `RPS` — requests per second по endpoint
- Error rate — процент 4xx/5xx
- Latency — p50, p95, p99 по endpoint
- Saturation — CPU, memory, thread pool, connection pool

**2. Логирование (Logging):**

```json
{
  "timestamp": "2026-04-11T10:30:00Z",
  "level": "ERROR",
  "traceId": "550e8400-e29b-41d4",
  "method": "POST",
  "path": "/api/v1/payments",
  "status": 500,
  "duration_ms": 2340,
  "error": "Connection refused: payment-gateway:8443"
}
```

**3. Трейсинг (Distributed Tracing):**

```mermaid
graph LR
    GW[API Gateway<br/>12ms] --> US[User Service<br/>45ms]
    US --> PS[Payment Service<br/>2300ms ⚠️]
    PS --> PG[Payment Gateway<br/>TIMEOUT ❌]
    US --> NS[Notification Service<br/>15ms]
```

Практический подход к диагностике:
1. Алерт по SLO (например, p99 > 500ms)
2. Dashboard — какой endpoint деградировал?
3. Логи — фильтр по `traceId`, поиск ошибок
4. Трейс — где bottleneck? (обычно внешний вызов или БД)
5. Fix + postmortem


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q34. Как проектировать bulk-операции в REST API? Частая ошибка в реальном коде.

Bulk-операции — частый запрос в enterprise API. Варианты дизайна:

**1. Batch endpoint:**

```http
POST /api/v1/users/batch HTTP/1.1
Content-Type: application/json

{
  "operations": [
    {"method": "POST", "body": {"name": "Иван"}},
    {"method": "POST", "body": {"name": "Петр"}},
    {"method": "POST", "body": {"name": "Мария"}}
  ]
}

HTTP/1.1 200 OK

{
  "results": [
    {"status": 201, "body": {"id": 1, "name": "Иван"}},
    {"status": 201, "body": {"id": 2, "name": "Петр"}},
    {"status": 400, "body": {"error": "duplicate email"}}
  ]
}
```

**2. Bulk delete с фильтром:**

```http
DELETE /api/v1/users?status=inactive&created_before=2025-01-01 HTTP/1.1

HTTP/1.1 200 OK
{"deletedCount": 42}
```

**3. Async bulk (для больших объемов):**

```http
POST /api/v1/imports HTTP/1.1
Content-Type: multipart/form-data

HTTP/1.1 202 Accepted
Location: /api/v1/imports/job-123

# Проверка статуса
GET /api/v1/imports/job-123 HTTP/1.1

HTTP/1.1 200 OK
{"status": "processing", "progress": 75, "total": 1000}
```

Правило: если bulk-операция занимает больше нескольких секунд — используйте async-паттерн (`202 Accepted`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q35. Как обрабатывать long-running операции в REST? Частая ошибка в реальном коде.

`REST` по природе синхронный, но long-running операции (генерация отчетов, обработка файлов, ML-инференс) требуют async-подхода.

**Паттерн Async Request-Reply:**

```mermaid
sequenceDiagram
    participant C as Клиент
    participant API as API Server
    participant Q as Очередь
    participant W as Worker

    C->>API: POST /api/v1/reports<br/>{"type": "annual", "year": 2025}
    API->>Q: Поставить задачу в очередь
    API->>C: 202 Accepted<br/>Location: /api/v1/reports/jobs/abc-123

    W->>Q: Взять задачу
    W->>W: Обработка (5 минут)

    C->>API: GET /api/v1/reports/jobs/abc-123
    API->>C: 200 OK<br/>{"status": "processing", "progress": 60}

    C->>API: GET /api/v1/reports/jobs/abc-123
    API->>C: 200 OK<br/>{"status": "completed",<br/>"resultUrl": "/api/v1/reports/abc-123"}

    C->>API: GET /api/v1/reports/abc-123
    API->>C: 200 OK<br/>(файл отчета)
```

Альтернативы polling:
- **Webhook** — сервер сам уведомляет клиента по callback URL
- **SSE** — server-sent events для real-time обновлений прогресса
- **WebSocket** — двунаправленный канал, если нужна интерактивность


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q36. Как отвечать на HTTP/REST вопросы сильно на senior-раунде? Частая ошибка в реальном коде.

Рабочий шаблон senior-ответа звучит как короткая история:

1. **Контекст**: публичный API с нагрузкой 6k RPS и p99 ниже 250 ms в multi-tenant среде
2. **Решение**: resource-oriented endpoints, единая error model и `OpenAPI` с contract tests в CI
3. **Trade-off**: почему для real-time событий оставили `WebSocket`, а для чтения справочников — `REST` с кэшированием
4. **Результат**: снижение интеграционных дефектов, MTTR и стабилизация p99

Пример ответа на вопрос "как бы вы спроектировали API для e-commerce":

> Я бы начал с определения bounded contexts: каталог, корзина, заказы, платежи. Каждый контекст — отдельный API с `OpenAPI`-контрактом. Каталог — `GET`-heavy с агрессивным `CDN`-кэшированием (`Cache-Control: public, s-maxage=300`). Корзина — stateless с `ETag` для optimistic locking. Платежи — `POST` с `Idempotency-Key` и async-обработкой (`202 Accepted`). Между сервисами — `gRPC` для синхронных вызовов, `Kafka` для событий. Версионирование через URI path (`/v1/`), backward compatibility проверяется в CI через `openapi-diff`.

Сильный senior-ответ всегда сочетает корректную семантику `HTTP`, осознанные trade-offs и операционно проверяемый эффект.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q37. Что такое HTTP/3 и QUIC? Ключевые отличия от HTTP/2 Частая ошибка в реальном коде.

**HTTP/3** — третья версия протокола HTTP, работающая поверх **QUIC** вместо TCP.

**Ключевая проблема HTTP/2:** multiplexing устраняет head-of-line blocking на уровне HTTP, но не на уровне TCP. При потере одного пакета TCP блокирует все потоки, пока не получит подтверждение.

**QUIC** (Quick UDP Internet Connections) решает это:

```
HTTP/1.1  TCP  TLS  ──  последовательные запросы, HOL blocking
HTTP/2    TCP  TLS  ──  multiplexing, но TCP HOL blocking остаётся
HTTP/3   QUIC  ──────   QUIC = UDP + встроенный TLS 1.3 + мультиплексирование без HOL blocking
```

**Ключевые отличия HTTP/3 / QUIC от HTTP/2:**

| Характеристика | HTTP/2 | HTTP/3 / QUIC |
|---|---|---|
| Транспорт | TCP | UDP (QUIC) |
| HOL blocking | TCP-уровень | Нет |
| TLS | Отдельный слой | Встроен в QUIC |
| Handshake | TCP (1 RTT) + TLS (1-2 RTT) | 0-1 RTT (QUIC объединяет) |
| Connection migration | Нет | Да (смена IP без разрыва) |
| Поддержка | Широкая | Растёт (Chrome, Nginx 1.25+) |

**Connection migration** — killer feature для мобильных устройств: при переключении с Wi-Fi на LTE соединение не разрывается, т.к. QUIC идентифицирует соединение по Connection ID, а не IP:port.

**Когда HTTP/3 особенно полезен:**
- Мобильные пользователи с нестабильным соединением
- Геораспределённые CDN (Cloudflare, Google уже используют)
- Real-time API с низкой latency требованиями

```java
// Spring Boot поддержка HTTP/3 (через Tomcat 11 / Netty)
// application.yml
server:
  http3:
    enabled: true
```

> В 2024 HTTP/3 обрабатывает ~30% трафика Cloudflare. Для backend-разработчика ключевое — понимать когда UDP-основанный транспорт выгоднее TCP.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q38. Что такое Server-Sent Events (SSE) и чем отличается от WebSocket? Частая ошибка в реальном коде.

**SSE (Server-Sent Events)** — механизм односторонней передачи данных от сервера к клиенту по обычному HTTP-соединению.

**Сравнение SSE vs WebSocket:**

| Характеристика | SSE | WebSocket |
|---|---|---|
| Направление | Только сервер → клиент | Двунаправленный |
| Протокол | HTTP | ws:// / wss:// |
| Reconnect | Автоматически | Вручную |
| Формат | `text/event-stream` | Бинарный или текст |
| Прокси / firewall | Работает (HTTP) | Могут блокировать |
| Поддержка браузером | EventSource API | WebSocket API |
| Когда использовать | Уведомления, live feed | Чат, игры, коллаборация |

**Формат SSE:**

```
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

data: {"event": "order_created", "id": 42}

event: order_shipped
data: {"orderId": 42, "trackingId": "TRK-123"}
id: 15

: это комментарий (heartbeat)

retry: 3000
data: {"message": "reconnect после 3 сек при обрыве"}
```

**Spring WebFlux SSE:**

```java
@GetMapping(value = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<OrderEvent>> streamEvents() {
    return orderEventPublisher.asFlux()
        .map(event -> ServerSentEvent.<OrderEvent>builder()
            .id(String.valueOf(event.getId()))
            .event(event.getType())
            .data(event)
            .retry(Duration.ofSeconds(3))
            .build());
}
```

**Spring MVC SSE (через SseEmitter):**

```java
@GetMapping("/notifications/stream")
public SseEmitter streamNotifications(@PathVariable Long userId) {
    SseEmitter emitter = new SseEmitter(30_000L); // 30 сек timeout
    notificationService.subscribe(userId, emitter);
    return emitter;
}
```

> SSE — правильный выбор для дашбордов, лент новостей, прогресс-баров, системных уведомлений. WebSocket нужен только когда клиент тоже отправляет данные в реальном времени.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q39. Content negotiation — Accept/Content-Type, media types, Spring MVC Частая ошибка в реальном коде.

**Content negotiation** — механизм, позволяющий клиенту и серверу договориться о формате данных без изменения URL.

**Ключевые заголовки:**

| Заголовок | Кто ставит | Смысл |
|---|---|---|
| `Content-Type` | Отправитель (клиент или сервер) | Формат тела запроса/ответа |
| `Accept` | Клиент | Форматы, которые клиент может принять |
| `Accept-Language` | Клиент | Предпочтительный язык ответа |
| `Accept-Encoding` | Клиент | Поддерживаемые алгоритмы сжатия (gzip, br) |
| `Content-Language` | Сервер | Язык возвращённого ресурса |

**Пример Content negotiation:**

```http
# Клиент хочет JSON, но примет XML если JSON недоступен
GET /api/v1/users/42 HTTP/1.1
Accept: application/json;q=1.0, application/xml;q=0.8

# Сервер возвращает JSON (q=quality factor, 0-1)
HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
```

**q-factor (weight):** 1.0 = максимальный приоритет, 0.0 = неприемлемо.

**Spring MVC Content Negotiation:**

```java
// Один контроллер — несколько форматов ответа
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    // produces указывает, какие форматы поддерживает endpoint
    @GetMapping(value = "/{id}", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        "text/csv"
    })
    public Report getReport(@PathVariable Long id) {
        return reportService.findById(id);
    }
}

// Конфигурация стратегий content negotiation
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(true)          // /reports/1?format=json
            .parameterName("format")
            .favorPathExtension(false)     // deprecated
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
```

**406 Not Acceptable:** сервер возвращает этот статус, если не может предоставить ни один из запрошенных форматов.

> На собеседованиях часто путают `Content-Type` (описывает тело _этого_ запроса/ответа) и `Accept` (описывает что клиент _хочет_ получить). Это разные заголовки с разными направлениями.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q40. Conditional requests — ETag, If-None-Match, If-Modified-Since и кэширование Частая ошибка в реальном коде.

**Conditional requests** — механизм HTTP, позволяющий клиенту проверить актуальность кэша без полной загрузки ресурса.

**Два механизма валидации:**

| Механизм | Заголовок сервера | Заголовок клиента | Сравнение |
|---|---|---|---|
| **ETag** (entity tag) | `ETag: "abc123"` | `If-None-Match: "abc123"` | По содержимому |
| **Last-Modified** | `Last-Modified: Tue, 01 Apr 2026 12:00:00 GMT` | `If-Modified-Since: Tue, 01 Apr 2026 12:00:00 GMT` | По времени |

**Поток ETag:**

```
1. GET /api/v1/users/42
   ← 200 OK, ETag: "v42-hash", body: {...}

2. GET /api/v1/users/42
   → If-None-Match: "v42-hash"
   ← 304 Not Modified (тело пустое — экономия трафика!)

3. PUT /api/v1/users/42 (optimistic locking)
   → If-Match: "v42-hash"  ← "обновить только если версия совпадает"
   ← 412 Precondition Failed (кто-то изменил ресурс раньше нас)
```

**Spring MVC: управление ETag и Last-Modified:**

```java
@GetMapping("/api/v1/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id, WebRequest request) {
    User user = userService.findById(id);
    
    String etag = "\"" + user.getVersion() + "\"";
    long lastModified = user.getUpdatedAt().toEpochMilli();
    
    // Spring проверяет If-None-Match / If-Modified-Since и вернёт 304 если не изменилось
    if (request.checkNotModified(etag, lastModified)) {
        return null;  // Spring вернёт 304 автоматически
    }
    
    return ResponseEntity.ok()
        .eTag(etag)
        .lastModified(lastModified)
        .body(user);
}
```

**ShallowEtagHeaderFilter — автоматические ETag на уровне фильтра:**

```java
@Bean
public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
    return new ShallowEtagHeaderFilter();
}
// Фильтр автоматически вычисляет MD5 тела и проставляет ETag
// Минус: тело всё равно генерируется на сервере
```

**Weak vs Strong ETag:**
- `ETag: "abc123"` — strong: побайтовое совпадение
- `ETag: W/"abc123"` — weak: семантически эквивалентный (допустимы minor различия)

> Conditional requests критически важны для API с агрессивным кэшированием. ETag + `If-None-Match` — основа optimistic locking в REST без транзакций.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q41. REST API versioning — URI vs Header vs Content-Type стратегии Частая ошибка в реальном коде.

Версионирование API — решение с долгосрочными последствиями. Нет единственно правильного ответа, но есть trade-offs.

**Стратегия 1: URI versioning (самая распространённая)**

```http
GET /api/v1/users/42
GET /api/v2/users/42
```

Плюсы: простота, видно в логах/URL, легко тестировать в браузере.
Минусы: нарушает принцип REST (URI должен идентифицировать ресурс, не версию API).

**Стратегия 2: Header versioning**

```http
GET /api/users/42
Api-Version: 2
# или
X-API-Version: 2
```

Плюсы: чистые URI, соответствует REST.
Минусы: сложнее тестировать, не виден в браузере, кэши CDN часто игнорируют кастомные заголовки.

**Стратегия 3: Content-Type versioning (Accept header)**

```http
GET /api/users/42
Accept: application/vnd.myapp.v2+json
```

Плюсы: формально корректно по REST, CDN-дружелюбно через `Vary: Accept`.
Минусы: сложность, мало кто делает правильно.

**Spring MVC реализация URI versioning:**

```java
@RestController
public class UserController {

    @GetMapping("/api/v1/users/{id}")
    public UserV1Response getUserV1(@PathVariable Long id) {
        return mapperV1.toResponse(userService.findById(id));
    }

    @GetMapping("/api/v2/users/{id}")
    public UserV2Response getUserV2(@PathVariable Long id) {
        return mapperV2.toResponse(userService.findById(id));
    }
}
```

**Spring MVC реализация Header versioning:**

```java
@GetMapping(value = "/api/users/{id}", headers = "Api-Version=1")
public UserV1Response getUserV1(@PathVariable Long id) { ... }

@GetMapping(value = "/api/users/{id}", headers = "Api-Version=2")
public UserV2Response getUserV2(@PathVariable Long id) { ... }
```

**Рекомендации:**

| Контекст | Рекомендация |
|---|---|
| Публичный API (внешние партнёры) | URI versioning — понятнее для интеграторов |
| Внутренние сервисы (мобильное приложение) | Header versioning |
| Высокий API maturity (контракты, OpenAPI) | Content-Type versioning |

> Самое важное — **поддерживать обратную совместимость** как можно дольше (additive changes) и **своевременно deprecate** старые версии с чёткими сроками.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q42. Идемпотентность в REST — какие методы идемпотентны и почему? Частая ошибка в реальном коде.

**Идемпотентность** — свойство операции: повторный вызов с теми же параметрами даёт тот же результат, что и первый.

**Таблица методов:**

| Метод | Безопасный | Идемпотентный | Обоснование |
|---|---|---|---|
| `GET` | Да | Да | Только чтение, без изменений |
| `HEAD` | Да | Да | Как GET, только заголовки |
| `OPTIONS` | Да | Да | Метаинформация о ресурсе |
| `PUT` | Нет | Да | Заменяет ресурс полностью — N раз = тот же результат |
| `DELETE` | Нет | Да | Второй DELETE на отсутствующий ресурс → 404, ресурс всё равно удалён |
| `POST` | Нет | Нет | Создаёт новый ресурс каждый раз |
| `PATCH` | Нет | Нет* | Зависит от реализации |

*PATCH может быть идемпотентным (если задаёт абсолютное значение), но не обязан.

**Почему DELETE идемпотентен, но возвращает разные статусы:**

```http
DELETE /api/v1/users/42 HTTP/1.1
← 200 OK  (первый вызов — удалён)

DELETE /api/v1/users/42 HTTP/1.1
← 404 Not Found  (повторный — уже не существует)
```

Разные статусы, но **состояние системы одинаково** — пользователь 42 удалён. Это и есть идемпотентность.

**Практические последствия для надёжности:**

```java
// Клиент безопасно повторяет GET/PUT/DELETE при timeout:
// GET  → повторить всегда безопасно
// PUT  → повторить безопасно
// DELETE → повторить безопасно (404 — ожидаемо)
// POST → повторять НЕЛЬЗЯ без Idempotency-Key
```

**Обеспечение идемпотентности POST:**

```java
@PostMapping("/api/v1/payments")
public ResponseEntity<Payment> createPayment(
        @RequestHeader("Idempotency-Key") UUID idempotencyKey,
        @RequestBody PaymentRequest request) {
    return idempotencyService.executeIdempotent(
        idempotencyKey,
        () -> paymentService.create(request)
    );
}
```

> На собеседованиях часто путают "безопасность" (safe = no side effects) и "идемпотентность" (idempotent = повторный вызов = тот же результат). PUT не безопасен (изменяет ресурс), но идемпотентен. GET безопасен и идемпотентен.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q43. RFC 7807 Problem Details — структура и Spring 6 ProblemDetail Частая ошибка в реальном коде.

**RFC 7807 "Problem Details for HTTP APIs"** (обновлён в RFC 9457) — стандарт структурированных ошибок для HTTP API. Устраняет несовместимость между разными форматами ошибок.

**Структура Problem Details:**

```json
{
  "type": "https://api.example.com/errors/insufficient-funds",
  "title": "Insufficient Funds",
  "status": 400,
  "detail": "Account balance is 50.00, but transaction requires 200.00",
  "instance": "/api/v1/payments/transactions/abc-123",
  "balance": 50.00,
  "required": 200.00
}
```

| Поле | Обязательность | Описание |
|---|---|---|
| `type` | Рекомендуется | URI-идентификатор типа ошибки (документация) |
| `title` | Рекомендуется | Краткое человекочитаемое описание |
| `status` | Рекомендуется | HTTP-статус код |
| `detail` | Опционально | Детальное описание для этого случая |
| `instance` | Опционально | URI конкретного экземпляра ошибки |
| Расширения | Опционально | Любые дополнительные поля |

**Content-Type:** `application/problem+json` (или `application/problem+xml`)

**Spring 6 / Spring Boot 3 — встроенная поддержка:**

```java
// Spring 6 добавил класс ProblemDetail
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problem.setType(URI.create("https://api.example.com/errors/insufficient-funds"));
        problem.setTitle("Insufficient Funds");
        problem.setProperty("balance", ex.getBalance());
        problem.setProperty("required", ex.getRequired());
        return problem;
    }

    // Spring Boot 3 включает RFC 7807 для стандартных ошибок автоматически
    // через spring.mvc.problemdetails.enabled=true
}
```

**Включение в application.yml:**

```yaml
spring:
  mvc:
    problemdetails:
      enabled: true  # Spring Boot 3+ — автоматически для 4xx/5xx
```

**ErrorResponse интерфейс (Spring 6):**

```java
// Собственные исключения могут реализовывать ErrorResponse
public class InsufficientFundsException extends RuntimeException 
        implements ErrorResponse {

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ProblemDetail getBody() {
        ProblemDetail detail = ProblemDetail.forStatus(getStatusCode());
        detail.setTitle("Insufficient Funds");
        detail.setDetail(getMessage());
        return detail;
    }
}
```

> RFC 7807/9457 — де-факто стандарт для enterprise REST API. Spring Boot 3 поддерживает его из коробки. На собеседовании упомяните, что `application/problem+json` отличается от `application/json` — это сигнализирует о зрелости подхода к API design.

---

## See also

- [OAuth2 и авторизация](../security/oauth2-interview.md) — аутентификация и авторизация в REST API
- [GraphQL](graphql-interview.md) — альтернатива REST: гибкие запросы и типизация
- [gRPC](grpc-interview.md) — высокопроизводительный RPC-протокол vs REST
- [Микросервисы](../architecture/microservices-interview.md) — REST как основа межсервисного взаимодействия
- [Интеграционное тестирование](../testing/integration-testing-interview.md) — тестирование REST API
- [Безопасность приложений](../security/application-security-interview.md) — HTTPS, CORS, rate limiting


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [API Design Best Practices](api-design-best-practices-interview.md) Частая ошибка в реальном коде.
- [API Versioning](api-versioning-interview.md)
- [GraphQL](graphql-interview.md)
- [gRPC](grpc-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
