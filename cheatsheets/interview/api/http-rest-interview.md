---
title: "Вопросы на собеседовании: HTTP и REST"
description: "Практичные вопросы и ответы по HTTP/REST для Java-разработчика: семантика методов, статусы, заголовки, кэширование, CORS, версионирование, HATEOAS, безопасность, идемпотентность и тестирование."
tags:
  - interview
  - api
  - http-rest-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "HTTP и REST"
prerequisites: []
next: []
updated: "2026-11-05"
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
- [Q1. Как коротко объяснить REST и его ограничения?](#q1)
- [Q2. Что такое ресурс и как проектировать URI?](#q2)
- [Q3. Как правильно выбирать HTTP-методы?](#q3)
- [Q4. В чем разница между безопасностью и идемпотентностью методов?](#q4)
- [Q5. В чем реальная разница между PUT, PATCH и POST?](#q5)

**HTTP-заголовки**
- [Q6. Какие HTTP-заголовки должен знать каждый backend-разработчик?](#q6)
- [Q7. Как работает Content Negotiation?](#q7)
- [Q8. Что такое Transfer-Encoding и чем chunked отличается от Content-Length?](#q8)

**Статусы и ошибки**
- [Q9. Как выбирать HTTP status code без хаоса?](#q9)
- [Q10. Какие status codes чаще всего путают на собеседованиях?](#q10)
- [Q11. Как должен выглядеть хороший error response?](#q11)

**Кэширование**
- [Q12. Как работает HTTP-кэширование?](#q12)
- [Q13. Как работают ETag и conditional requests?](#q13)
- [Q14. В чем разница между Cache-Control директивами?](#q14)

**CORS**
- [Q15. Что такое CORS и зачем он нужен?](#q15)
- [Q16. Что такое preflight-запрос и когда он отправляется?](#q16)

**Версионирование и эволюция API**
- [Q17. Какие стратегии версионирования REST API существуют?](#q17)
- [Q18. Зачем API-контракт и как его поддерживать?](#q18)
- [Q19. Что такое HATEOAS и нужен ли он на практике?](#q19)

**Идемпотентность и надежность**
- [Q20. Как обеспечить идемпотентность на практике?](#q20)
- [Q21. Как реализовать пагинацию в REST API?](#q21)

**Аутентификация и безопасность**
- [Q22. Какие схемы аутентификации используются в HTTP?](#q22)
- [Q23. Как работает JWT в контексте REST API?](#q23)
- [Q24. Как защищать REST API в production?](#q24)
- [Q25. Какие anti-patterns в безопасности API встречаются чаще всего?](#q25)

**Rate Limiting и устойчивость**
- [Q26. Как реализовать rate limiting в REST API?](#q26)
- [Q27. Что делает REST API масштабируемым на практике?](#q27)

**HTTP/2 и современные протоколы**
- [Q28. Чем HTTP/2 отличается от HTTP/1.1?](#q28)
- [Q29. Когда REST лучше SOAP, а когда наоборот?](#q29)
- [Q30. Когда REST хуже GraphQL/gRPC/WebSocket?](#q30)

**Cookies и управление состоянием**
- [Q31. Как работают cookies в HTTP и когда их используют в API?](#q31)

**Тестирование и диагностика**
- [Q32. Как тестировать REST API системно?](#q32)
- [Q33. Как диагностировать проблемы REST API в production?](#q33)

**Дизайн и лучшие практики**
- [Q34. Как проектировать bulk-операции в REST API?](#q34)
- [Q35. Как обрабатывать long-running операции в REST?](#q35)
- [Q36. Как отвечать на HTTP/REST вопросы сильно на senior-раунде?](#q36)

**Современные протоколы и расширенные темы**
- [Q37. Что такое HTTP/3 и QUIC и чем они отличаются от HTTP/2?](#q37)
- [Q38. Что такое Server-Sent Events (SSE) и чем они отличаются от WebSocket?](#q38)
- [Q39. Как устроен content negotiation: Accept, Content-Type и реализация в Spring MVC?](#q39)
- [Q40. Как работают conditional requests (ETag, If-None-Match, If-Modified-Since)?](#q40)
- [Q41. Версионирование REST API: URI, header или Content-Type — что выбрать?](#q41)
- [Q42. Какие HTTP-методы идемпотентны и почему?](#q42)
- [Q43. Что такое Problem Details (RFC 9457) и как он реализован в Spring 6?](#q43)

## Как отвечать про HTTP/REST (короткий шаблон)

На интервью обычно ждут один и тот же каркас: сначала вы задаете контекст, то есть тип API, профиль нагрузки, SLA/SLO и требования безопасности, затем объясняете семантику решения через методы, статусы и контракт, после этого проговариваете trade-off и возможные альтернативы вроде `GraphQL`, `WebSocket`, `gRPC` или `SOAP`, и в конце показываете, как решение валидируется через тесты, метрики и наблюдаемость.

---

<a id="q1"></a>

## Q1. Как коротко объяснить REST и его ограничения?

`REST` — это архитектурный стиль для сетевых систем, который Рой Филдинг описал в 2000 году. Суть: клиент работает с **ресурсами** (бизнес-сущностями), а не вызывает удалённые процедуры, и взаимодействие подчиняется набору ограничений (constraints). Именно эти ограничения, а не «JSON поверх HTTP», и делают API настоящим REST.

Шесть архитектурных ограничений:

| Ограничение | Суть |
|---|---|
| **Client-Server** | Разделение ответственности: клиент отвечает за UI, сервер — за данные и логику |
| **Stateless** | Каждый запрос содержит всю информацию для обработки, сервер не хранит сессию |
| **Cacheable** | Ответы должны явно указывать, можно ли их кэшировать |
| **Uniform Interface** | Единый интерфейс: ресурсы, представления, self-descriptive messages, HATEOAS |
| **Layered System** | Клиент не знает, общается ли он с конечным сервером или с прокси |
| **Code on Demand** | (опциональное) Сервер может отправить исполняемый код клиенту |

**Где REST слабее.** На собеседовании важно проговорить границы стиля, а не хвалить его безусловно:
- **Сложные графы данных** — клиент либо тянет лишнее (over-fetching), либо делает много запросов (under-fetching); здесь выигрывает `GraphQL`.
- **Двунаправленный real-time** — REST синхронен и однонаправлен; для этого есть `WebSocket`.
- **Длинные stateful-сценарии** с явным протоколом состояния — ложатся плохо, потому что REST намеренно stateless.

**Вывод для интервью:** сильный ответ звучит не как «REST всегда лучший», а как «REST — удачный базовый стиль, пока требования не уводят в другой протокол».

<a id="q2"></a>

## Q2. Что такое ресурс и как проектировать URI?

Ресурс — это адресуемая бизнес-сущность (`/users`, `/orders/{orderId}`), а не операция (`/createUser`). Главный принцип: `URI` отвечает на вопрос **«что это»** (существительное), а **«что сделать»** задаёт HTTP-метод (глагол). Поэтому действие не выносят в путь — действие выражает `GET`/`POST`/`DELETE`, а не сегмент URL.

Сравните правильный и неправильный дизайн:

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
- **Существительные во множественном числе** для коллекций (`/users`, а не `/user` или `/getUsers`).
- **Иерархия только при реальной вложенности** — `/orders/{id}/items` оправдан, если item не существует без заказа.
- **Стабильные `URI`** — путь не меняют на лету; эволюцию API ведут через версионирование и контракт.
- **Фильтрация, сортировка, пагинация — в query-параметрах:** `/users?role=admin&sort=name&page=2`. Это переменные выборки, а не часть идентичности ресурса.
- **Не глубже двух уровней вложенности:** `/users/{id}/orders` — нормально; `/users/{id}/orders/{oid}/items/{iid}/reviews` — перебор, такие ресурсы лучше адресовать напрямую (`/items/{iid}/reviews`).

<a id="q3"></a>

## Q3. Как правильно выбирать HTTP-методы?

Метод выбирают по **семантике операции**, а не по тому, как удобнее клиенту. Каждый метод несёт три обещания контракта: меняет ли он состояние (safe), можно ли его безопасно повторить (idempotent) и нужно ли тело. Таблица сводит это вместе:

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

**Практический критерий** для проверки выбора: представьте, что запрос отвалился по network timeout и клиент повторил его. Если итог стал непредсказуемым (создался дубль, баланс списался дважды) — метод или контракт выбраны неверно.

<a id="q4"></a>

## Q4. В чем разница между безопасностью и идемпотентностью методов?

Safe-метод не просит сервер изменить состояние ресурса. Например, GET и HEAD предназначены для чтения. При этом сервер может записывать логи и метрики: safe не означает полного отсутствия побочных эффектов.

Идемпотентность означает, что намеренный эффект нескольких одинаковых запросов совпадает с эффектом одного. PUT и DELETE идемпотентны по семантике HTTP, хотя могут изменять ресурс. Ответы на первый и повторный DELETE могут иметь разные статусы. Все safe-методы идемпотентны, но не каждый идемпотентный метод safe.

---


<a id="q5"></a>

## Q5. В чем реальная разница между PUT, PATCH и POST?

Метод выбирают по семантике операции:

- **PUT** задаёт состояние целевого ресурса по известному URI; обычно заменяет его представление или создаёт ресурс по этому адресу. Повтор одинакового PUT идемпотентен.
- **PATCH** применяет документ изменений к ресурсу. Семантика и идемпотентность зависят от формата патча и операции.
- **POST** просит целевой ресурс обработать представление по его правилам: это может быть создание, команда или другая операция. POST не обязан быть идемпотентным.

Для защиты повтора неидемпотентного POST можно использовать ключ идемпотентности с документированным сроком действия.

---


<a id="q6"></a>

## Q6. Какие HTTP-заголовки должен знать каждый backend-разработчик?

Заголовки — это метаданные запроса и ответа, и их удобно держать в голове по функциональным категориям, а не списком. Достаточно понимать, кто их ставит и зачем.

**Заголовки запроса (клиент → сервер):**

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

**Заголовки ответа (сервер → клиент):**

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

<a id="q7"></a>

## Q7. Как работает Content Negotiation?

`Content Negotiation` — это механизм, которым клиент и сервер договариваются о формате данных (тип контента, язык, кодировка) **без смены URL**. Один и тот же ресурс может быть отдан как JSON или XML — выбор определяется заголовками, а не разными адресами. Есть два варианта:

**Server-driven** (основной) — клиент указывает предпочтения в заголовках, а решение принимает сервер:

```http
GET /api/v1/users/42 HTTP/1.1
Accept: application/json, application/xml;q=0.5
Accept-Language: ru-RU, en;q=0.3
Accept-Encoding: gzip, deflate
```

Значение `q` (quality factor, от 0 до 1) задаёт приоритет: чем выше, тем желаннее формат. В примере выше JSON предпочтительнее XML (`q=0.5`). Если сервер не может отдать ни один из запрошенных форматов, он возвращает `406 Not Acceptable`.

**Agent-driven** — сервер возвращает список доступных вариантов, а клиент выбирает сам. На практике почти не используется из-за лишнего round-trip.

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

<a id="q8"></a>

## Q8. Что такое Transfer-Encoding и чем chunked отличается от Content-Length?

В HTTP/1.1 это два способа обозначить границу тела ответа. Content-Length заранее сообщает общую длину, а chunked маркирует части и конец потока.

**`Content-Length`** — сервер знает итоговый размер и сообщает его одним числом. Получатель читает ровно столько байт:

```http
HTTP/1.1 200 OK
Content-Length: 1234
Content-Type: application/json

{"data": "...1234 байт..."}
```

**`Transfer-Encoding: chunked`** — сервер отдаёт данные порциями (chunks) без объявления общей длины. Это удобно, когда итоговый размер неизвестен или ответ начинают отдавать до полной готовности:

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

Каждый chunk начинается со своего размера в hex, а конец потока обозначается chunk-ом размера `0`.

**Когда нужен chunked** — везде, где размер неизвестен заранее или ответ начинают отдавать до его полной готовности:
- Стриминг больших ответов (экспорт CSV, генерация отчётов).
- Server-Sent Events.
- Любые ответы, чей размер вычисляется только по завершении обработки.

**Подводный камень:** `Content-Length` и `Transfer-Encoding: chunked` взаимоисключающие — одновременно их использовать нельзя.

<a id="q9"></a>

## Q9. Как выбирать HTTP status code без хаоса?

Чтобы коды не превращались в хаос, нужна одна короткая и стабильная политика на весь API. Главное — выбирать код по смыслу ответа (успех/ошибка, чья ошибка), а не «как привыкли в этом контроллере». Схема ниже сводит самые частые коды к простому дереву решений:

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

**Золотое правило:** один и тот же тип ошибки всегда маппится в один и тот же status code — тогда клиент может реагировать на код, а не парсить текст. Самый вредный антипаттерн — отдавать `200 OK` и прятать ошибку в теле (`{"success": false}`): мониторинг видит «всё хорошо», retry-логика клиента ломается, а кэши и прокси могут закэшировать ошибочный ответ как успешный.

<a id="q10"></a>

## Q10. Какие status codes чаще всего путают на собеседованиях?

`401 Unauthorized` связан с HTTP-аутентификацией: запрос не содержит подходящих учётных данных, а ответ включает `WWW-Authenticate` challenge. `403 Forbidden` означает, что сервер понял запрос, но отказывает в его выполнении; клиент не обязан быть уже аутентифицирован.

`404 Not Found` не раскрывает, существует ли ресурс навсегда; `410 Gone` сообщает, что он был удалён и это состояние предположительно постоянно. `400 Bad Request` часто используют для ошибок запроса, а `422 Unprocessable Content` — для синтаксически корректного тела, которое нельзя обработать по смыслу. Выбирайте коды по контракту и не меняйте их значение от эндпоинта к эндпоинту.

---


<a id="q11"></a>

## Q11. Как должен выглядеть хороший error response?

Хороший error response служит сразу двум читателям: **машине** (стабильные поля `type`/`status`/`code`, по которым клиент ветвит логику) и **человеку** (понятный `detail` плюс `traceId` для поддержки). Не нужно изобретать свой формат — есть стандарт RFC 9457 (Problem Details, бывший RFC 7807):

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

**Подводный камень безопасности:** в production из тела ошибки убирают всё, что раскрывает внутренности — `stacktrace`, текст SQL-запроса, имена внутренних сервисов и таблиц. Детали для разбора оставляют в логах, а клиенту дают `traceId`, по которому поддержка эту запись найдёт.

<a id="q12"></a>

## Q12. Как работает HTTP-кэширование?

HTTP-кэш может хранить подходящий ответ в браузере, прокси или CDN. Пока ответ свежий, его можно использовать без нового обращения к серверу. Срок свежести задают директивы `Cache-Control`, например `max-age`.

После потери свежести кэш иногда проверяет сохранённое представление условным запросом: `If-None-Match` с `ETag` или `If-Modified-Since` с `Last-Modified`. Ответ `304 Not Modified` позволяет обновить метаданные без пересылки тела. Не каждому ответу нужны одновременно свежесть и ревалидация: поведение зависит от статуса, заголовков и политики кэша. `no-cache` допускает хранение, но требует проверки перед повторным использованием; `no-store` запрещает хранение.

---


<a id="q13"></a>

## Q13. Как работают ETag и conditional requests?

`ETag` — непрозрачный валидатор представления, который задаёт сервер. Он может быть основан на версии или содержимом, но клиент не должен выводить его устройство из строки. Сильный валидатор пригоден для точного сравнения, слабый `W/` — для семантической эквивалентности.

`If-None-Match` позволяет условно читать сохранённое представление: при совпадении сервер обычно отвечает `304 Not Modified`. `If-Match` применяют к изменению ресурса и сравнивают **сильно**: при несовпадении обычно получают `412 Precondition Failed`. Это помогает избежать потерянного обновления.

---


<a id="q14"></a>

## Q14. В чем разница между Cache-Control директивами?

Директивы `Cache-Control` отвечают на два вопроса: **кто** может кэшировать (только браузер или ещё CDN/прокси) и **как долго**. Сочетая их, описывают политику для конкретного ресурса:

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

**Частая ошибка** (и любимый вопрос на собеседовании) — путать `no-cache` и `no-store`. Несмотря на название, `no-cache` **разрешает** хранить ответ, но обязывает ревалидировать его перед каждым использованием. А вот `no-store` запрещает сохранять ответ вообще — это директива для по-настоящему чувствительных данных.

<a id="q15"></a>

## Q15. Что такое CORS и зачем он нужен?

`CORS` (Cross-Origin Resource Sharing) — это механизм, которым сервер **разрешает** браузеру читать ответы с другого origin (origin = протокол + домен + порт). Важно понимать причинно-следственную связь: по умолчанию браузер действует по **Same-Origin Policy** и блокирует доступ к ответам с чужого origin; CORS — это набор заголовков, которыми сервер явно ослабляет это ограничение для доверенных origin. То есть CORS не «защищает API» — он, наоборот, аккуратно открывает доступ. И это правило браузера: серверные клиенты (curl, backend-to-backend) про CORS ничего не знают.

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

**Подводный камень:** **никогда** не сочетайте `Access-Control-Allow-Origin: *` с `allowCredentials(true)`. Это означало бы «любой сайт может слать запросы с cookie/токеном пользователя и читать ответ» — прямой путь к краже данных. Браузеры даже запрещают такую комбинацию, поэтому в production всегда перечисляйте конкретные origin в whitelist.

<a id="q16"></a>

## Q16. Что такое preflight-запрос и когда он отправляется?

Preflight — это `OPTIONS`-запрос, который браузер шлёт **сам**, перед основным cross-origin запросом, чтобы заранее спросить сервер: «мне можно так?». Смысл — не отправлять потенциально опасный запрос (например, `DELETE` с токеном), пока сервер явно не подтвердил, что метод и заголовки разрешены. Поэтому всё делится на «простые» запросы, которые браузер пропускает напрямую, и «непростые», которые требуют предварительной проверки.

**Простые запросы** (без preflight) — то, что обычная HTML-форма могла отправить и до эпохи CORS:
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

**Оптимизация:** `Access-Control-Max-Age` говорит браузеру, сколько секунд держать результат preflight в кэше. Без него `OPTIONS` улетал бы перед каждым запросом, удваивая число round-trip; с разумным значением (часы) браузер проверяет разрешения один раз и дальше шлёт основные запросы напрямую.

<a id="q17"></a>

## Q17. Какие стратегии версионирования REST API существуют?

Версию можно передать в пути (`/v1/users`), query-параметре, отдельном заголовке или media type в `Accept`. Путь виден клиенту и маршрутизатору; заголовок сохраняет URI, но требует явной обработки кэшем через `Vary` или отдельный ключ.

Универсально лучшего и обязательного способа нет. Стратегию выбирают с учётом клиентов, кэширования, маршрутизации и срока поддержки старого контракта. Совместимые изменения обычно вносят без новой версии; несовместимые требуют плана миграции.

---


<a id="q18"></a>

## Q18. Зачем API-контракт и как его поддерживать?

Опубликованное описание `OpenAPI` может служить общим машиночитаемым контрактом для backend, frontend и интеграторов, если его поддерживают в соответствии с реальным API. Зачем он нужен:
- **Синхронизация команд** — все смотрят в один документ, а не в чужой код или устные договорённости.
- **Генерация** — из контракта автоматически собирают клиентские SDK, серверные стабы и документацию.
- **Безопасность релизов** — contract testing и проверка обратной совместимости в CI ловят breaking changes до того, как они дойдут до клиентов.

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

**Рабочая модель поддержки.** Контракт версионируют как код: правки проходят review, CI валидирует схему и проверяет обратную совместимость (`openapi-diff`, `spectral`), и если изменение всё же breaking — его выпускают только через новую версию с заранее оговоренным миграционным периодом, а не «по-тихому» в текущей.

<a id="q19"></a>

## Q19. Что такое HATEOAS и нужен ли он на практике?

`HATEOAS` (Hypermedia as the Engine of Application State) — это ограничение `REST`, при котором сервер вместе с данными возвращает **ссылки на доступные дальнейшие действия**. Идея: клиент не хардкодит URL-ы, а обнаруживает их прямо из ответа — как человек ходит по сайту, кликая по ссылкам, а не вводя адреса вручную. Доступность действия диктует сам ответ: например, ссылка `deactivate` появляется, только если пользователя сейчас можно деактивировать.

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

<a id="q20"></a>

## Q20. Как обеспечить идемпотентность на практике?

После timeout клиент не знает, успел ли сервер выполнить POST. Клиент передаёт уникальный `Idempotency-Key` и повторяет **тот же запрос с тем же ключом**.

Сервер атомарно связывает ключ с параметрами и состоянием операции. Повтор в пределах объявленного окна не должен создавать второй эффект: клиент получает сохранённый результат или согласованный статус выполняющейся операции. Повтор ключа с другими параметрами отклоняют по контракту. Срок хранения ограничен; после него прежний ключ может считаться новым. Для внешних эффектов дополнительно нужна согласованная запись результата.

---


<a id="q21"></a>

## Q21. Как реализовать пагинацию в REST API?

Пагинация разбивает большую выборку на страницы. Три подхода отличаются тем, как обозначается «откуда продолжать», и это напрямую влияет на стабильность при изменении данных и на производительность на больших таблицах.

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

**Проблема:** offset считает «пропусти N записей», но если между запросами страниц кто-то вставил или удалил запись, всё за ней сдвигается — на следующей странице можно пропустить элемент или увидеть его дважды. Плюс на больших offset БД всё равно вынуждена пройти и отбросить N строк — отсюда деградация `O(n)`.

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

**Эмпирическое правило:** для UI с нумерацией страниц и небольших наборов — offset (нужен произвольный доступ); для бесконечных лент, фидов и больших таблиц — cursor/keyset (стабильность и постоянная скорость важнее, чем «прыжок на страницу 50»).

<a id="q22"></a>

## Q22. Какие схемы аутентификации используются в HTTP?

Аутентификацию в HTTP несёт заголовок `Authorization`, и его первое слово (`Basic`, `Bearer`, `Digest`) задаёт схему — то есть формат и способ проверки credentials. API-ключи традиционно кладут в отдельный custom-заголовок. Основные схемы:

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

**Эмпирическое правило:** для пользовательских клиентов (SPA, мобильные) — `Bearer` (JWT/OAuth2); для машинного server-to-server в production — `mTLS` или `OAuth2 Client Credentials`, где взаимное доверие выстроено на сертификатах, а не на разделяемых паролях.

<a id="q23"></a>

## Q23. Как работает JWT в контексте REST API?

Подписанный JWT (JWS) в компактной форме состоит из header, payload и подписи. Сервер может проверить подпись локально по доверенному ключу и затем проверить issuer, audience, срок действия и нужные claims.

Локальная проверка подписи не означает, что все сведения всегда актуальны. Отзыв токена, смена прав и политика сессий могут требовать отдельного хранилища или короткого срока жизни. Payload JWT обычно лишь кодирован, а не зашифрован; чувствительные данные в него не кладут без отдельной защиты.

---


<a id="q24"></a>

## Q24. Как защищать REST API в production?

Защита REST API — это несколько слоёв (defence in depth): транспорт, аутентификация, валидация ввода, ограничение нагрузки и операционная гигиена. Для браузерных клиентов HTTP security-заголовки выбирают по типу ответа и модели угроз, а не считают один набор обязательным для каждого API-ответа. Например, для HTTPS-страницы приложения могут подойти:

```http
Strict-Transport-Security: max-age=31536000
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Content-Security-Policy: default-src 'self'
Referrer-Policy: strict-origin-when-cross-origin
```

`HSTS` браузер принимает только из доверенного HTTPS-ответа; `includeSubDomains` и `preload` добавляют после проверки всего охвата и необходимости предзагрузки (см. [TLS Q40](../security/tls-ssl-interview.md#q40--что-такое-hsts-и-зачем-он-нужен)). `CSP` задают под конкретную страницу, а не копируют одинаково в каждый JSON-ответ. Для небраузерного API важнее корректный TLS, авторизация и проверка входных данных; браузерные заголовки могут им не использоваться.

Чеклист безопасности:

1. **Transport**: `TLS` на всех участках, запрет слабых cipher/protocol версий
2. **AuthN/AuthZ**: `OAuth2`, JWT/introspection или `mTLS` по контексту
3. **Input validation**: строгая server-side валидация, sanitization
4. **Rate limiting**: `429 Too Many Requests` + `Retry-After`
5. **Secrets**: vault / secret manager, ротация ключей
6. **Least privilege**: минимальные scopes, короткоживущие токены
7. **Logging**: аудит доступа без логирования секретов и PII
8. **SDLC**: threat modeling, SAST/DAST, dependency scanning

<a id="q25"></a>

## Q25. Какие anti-patterns в безопасности API встречаются чаще всего?

На интервью сильнее звучит не сам список ошибок, а связка **«ошибка → чем она опасна → конкретная контрмера»**. Поэтому держите её именно в таком виде:

| Anti-pattern | Риск | Контрмера |
|---|---|---|
| Долгоживущие токены без ротации | Украденный токен действует месяцами | Короткий `exp` + `refresh_token` |
| Слишком широкие scopes/roles | Любой пользователь = admin | Least privilege, RBAC/ABAC |
| Отсутствие `429` | DDoS, credential stuffing | Rate limiting + backpressure |
| Секреты и PII в логах | Утечка credentials | Маскирование, structured logging |
| Доверие client input | SQL injection, XSS | Server-side validation + sanitization |
| Verbose error messages | Раскрытие архитектуры | Generic messages в prod, details в traceId |
| `CORS: *` + credentials | CSRF-подобные атаки | Whitelist конкретных origins |

<a id="q26"></a>

## Q26. Как реализовать rate limiting в REST API?

`Rate limiting` ограничивает число запросов от одного клиента за интервал времени. Цель двойная: защита от перегрузки и злоупотреблений (DDoS, credential stuffing, скрейпинг) и честное распределение ресурсов между клиентами. Превышение лимита сервер сообщает кодом `429 Too Many Requests` и заголовком `Retry-After` — чтобы клиент знал, когда повторить, а не долбил вслепую.

**Заголовки rate limiting (draft-стандарт IETF):** они говорят клиенту, сколько запросов осталось и когда счётчик обнулится:

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

**Где размещать.** В production rate limiting обычно выносят на API Gateway (`Kong`, `Envoy`, `nginx`): так лимит применяется централизованно, ещё до того как запрос дошёл до приложения, и работает корректно при нескольких инстансах сервиса (счётчик общий, обычно в Redis).

<a id="q27"></a>

## Q27. Что делает REST API масштабируемым на практике?

Масштабируемость REST держится на одном фундаменте — **stateless** — и нескольких механизмах, которые на нём строятся. Без хранения сессии на сервере любой запрос обрабатывает любой инстанс, и систему можно масштабировать горизонтально, просто добавляя инстансы за балансировщиком. Остальное снижает нагрузку на эти инстансы:

1. **Stateless** — нет server session affinity, любой инстанс обработает любой запрос (это и делает горизонтальное масштабирование возможным).
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

**Не забудьте про эксплуатацию.** Архитектура — половина дела; вторая половина в том, что масштабируемость надо *видеть*: метрики `RPS`, error rate и latency p95/p99, распределённая трассировка и алерты по SLO. Без них деградация обнаруживается уже по жалобам пользователей, а не по графику.

<a id="q28"></a>

## Q28. Чем HTTP/2 отличается от HTTP/1.1?

Главное отличие — **мультиплексирование**: HTTP/2 гоняет много параллельных запросов по одному TCP-соединению, тогда как HTTP/1.1 на соединение обрабатывает запросы по очереди. Из этой смены модели вытекает и всё остальное (бинарный формат, сжатие заголовков):

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

<a id="q29"></a>

## Q29. Когда REST лучше SOAP, а когда наоборот?

REST — архитектурный стиль для ресурсных HTTP API, а SOAP — протокол обмена сообщениями с собственной экосистемой спецификаций. REST часто удобен для веб-клиентов и простых ресурсных операций. SOAP сохраняют или выбирают, когда стороны требуют SOAP/WSDL либо конкретные WS-* соглашения.

Строгий контракт сам по себе не требует SOAP: для HTTP API можно использовать OpenAPI. Нельзя утверждать, что SOAP нужен только для транзакций или что REST всегда лучше. Оценивайте требования конкретной интеграции и стоимость поддержки.

---


<a id="q30"></a>

## Q30. Когда REST хуже GraphQL/gRPC/WebSocket?

GraphQL удобен, если клиентам нужны разные наборы связанных полей. gRPC полезен для типизированных RPC и потоков между сервисами. WebSocket даёт двусторонний канал для длительного обмена, а SSE часто достаточно для событий только от сервера к клиенту.

REST хорошо подходит для ресурсных HTTP операций. Выбор зависит от нагрузки, клиентов и требуемого поведения; замена REST другим подходом не является целью сама по себе.

---


<a id="q31"></a>

## Q31. Как работают cookies в HTTP и когда их используют в API?

`Cookies` — пары «ключ-значение», которые сервер задаёт через `Set-Cookie`. Браузер прикладывает cookie к подходящим запросам с учётом `Domain`, `Path`, `SameSite`, `Secure` и режима credentials. Это удобно для сессий, но требует защиты от CSRF в применимых сценариях.

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

<a id="q32"></a>

## Q32. Как тестировать REST API системно?

Системное тестирование REST API строят как **пирамиду**: много быстрых дешёвых тестов снизу, мало медленных дорогих сверху. Каждый уровень проверяет своё, и дублировать проверки между уровнями не нужно:

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

**Помимо функциональной пирамиды** проверяют и нефункциональные свойства: нагрузочные тесты (latency, `RPS` под нагрузкой), security-тесты (обход аутентификации, инъекции) и resilience-тесты (поведение при timeout и retry storm). Их легко забыть, но именно они отделяют «работает на демо» от «выдержит production».

<a id="q33"></a>

## Q33. Как диагностировать проблемы REST API в production?

Диагностика в production опирается на три столпа наблюдаемости (observability), и каждый отвечает на свой вопрос: метрики — **что** сломалось, логи — **что именно** произошло, трейсинг — **где** в цепочке вызовов. Двигаются обычно сверху вниз: от алерта по метрике к конкретной записи лога и трейсу.

**1. Метрики (Metrics) — что сломалось:**
- `RPS` — requests per second по endpoint
- Error rate — процент 4xx/5xx
- Latency — p50, p95, p99 по endpoint
- Saturation — CPU, memory, thread pool, connection pool

**2. Логирование (Logging) — что именно произошло:**

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

**3. Трейсинг (Distributed Tracing) — где в цепочке проблема:**

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

<a id="q34"></a>

## Q34. Как проектировать bulk-операции в REST API?

Bulk API обрабатывает несколько элементов за один вызов. До реализации задают максимальный размер пакета, атомарность и формат результата.

Если операция атомарна, ошибка может откатить весь пакет; ответ должен объяснить общий исход. Если допускается частичный успех, клиенту нужны результаты отдельных элементов. Длинные импорты лучше оформлять как фоновую задачу с отдельным статусом. Универсального требования возвращать статус каждого элемента при любой модели bulk нет.

---


<a id="q35"></a>

## Q35. Как обрабатывать long-running операции в REST?

REST по природе синхронный — запрос/ответ в рамках одного соединения, — но long-running операции (генерация отчётов, обработка файлов, ML-инференс) в эту модель не влезают: держать HTTP-соединение открытым минутами нельзя, оно отвалится по таймауту. Решение — **отвязать приём задачи от её выполнения**.

**Паттерн Async Request-Reply.** Сервер принимает задачу, сразу отвечает `202 Accepted` со ссылкой на «ресурс-задачу», выполняет работу в фоне, а клиент опрашивает статус по этой ссылке:

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

Опрос статуса (polling) прост, но шлёт лишние запросы. Если это критично, его заменяют push-механизмами:
- **Webhook** — сервер сам дёргает callback URL клиента, когда задача готова (клиент ничего не опрашивает).
- **SSE** — server-sent events: однонаправленный поток обновлений прогресса в реальном времени.
- **WebSocket** — двунаправленный канал, когда нужна ещё и интерактивность (например, отмена задачи).

<a id="q36"></a>

## Q36. Как отвечать на HTTP/REST вопросы сильно на senior-раунде?

На senior-раунде ценят не перечисление фактов, а **инженерное суждение**: контекст → решение → осознанный trade-off → проверяемый результат. Рабочий шаблон ответа звучит как короткая история по этим четырём пунктам:

1. **Контекст**: публичный API с нагрузкой 6k RPS и p99 ниже 250 ms в multi-tenant среде
2. **Решение**: resource-oriented endpoints, единая error model и `OpenAPI` с contract tests в CI
3. **Trade-off**: почему для real-time событий оставили `WebSocket`, а для чтения справочников — `REST` с кэшированием
4. **Результат**: снижение интеграционных дефектов, MTTR и стабилизация p99

Пример ответа на вопрос "как бы вы спроектировали API для e-commerce":

> Я бы начал с определения bounded contexts: каталог, корзина, заказы, платежи. Каждый контекст — отдельный API с `OpenAPI`-контрактом. Каталог — `GET`-heavy с агрессивным `CDN`-кэшированием (`Cache-Control: public, s-maxage=300`). Корзина — stateless с `ETag` для optimistic locking. Платежи — `POST` с `Idempotency-Key` и async-обработкой (`202 Accepted`). Между сервисами — `gRPC` для синхронных вызовов, `Kafka` для событий. Версионирование через URI path (`/v1/`), backward compatibility проверяется в CI через `openapi-diff`.

Сильный senior-ответ всегда сочетает корректную семантику `HTTP`, осознанные trade-offs и операционно проверяемый эффект.

<a id="q37"></a>

## Q37. Что такое HTTP/3 и QUIC и чем они отличаются от HTTP/2?

**HTTP/3** — третья версия протокола HTTP. Главное в ней — смена транспорта: вместо TCP она работает поверх **QUIC** (UDP). Всё остальное вытекает из этого решения.

**Зачем это понадобилось — проблема HTTP/2.** Его мультиплексирование убрало head-of-line blocking на уровне HTTP, но не на уровне TCP. TCP гарантирует строгий порядок байт, поэтому при потере одного пакета он притормаживает **все** мультиплексированные потоки, пока пакет не переотправят, — хотя логически потоки независимы.

**QUIC** (Quick UDP Internet Connections) решает это, перенеся управление потоками с TCP на свой уровень: потери в одном потоке больше не тормозят остальные.

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

**Connection migration** — главная фишка для мобильных. При переключении с Wi-Fi на LTE меняется IP, и TCP-соединение (привязанное к паре IP:port) рвётся. QUIC идентифицирует соединение по собственному **Connection ID**, не зависящему от IP, поэтому сессия переживает смену сети без переустановки.

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

<a id="q38"></a>

## Q38. Что такое Server-Sent Events (SSE) и чем они отличаются от WebSocket?

**SSE (Server-Sent Events)** — это поток сообщений **только от сервера к клиенту** поверх обычного долгоживущего HTTP-соединения. Ключевое отличие от WebSocket — однонаправленность: клиент подписывается и слушает, но обратный канал у него один — обычные HTTP-запросы.

**Когда чего хватает.** SSE проще и работает там, где данные идут в одну сторону (уведомления, прогресс, live-лента). WebSocket нужен, когда клиент тоже должен слать данные в реальном времени (чат, игры, совместное редактирование). Сравнение по ключевым осям:

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

<a id="q39"></a>

## Q39. Как устроен content negotiation: Accept, Content-Type и реализация в Spring MVC?

**Content negotiation** — механизм, которым клиент и сервер договариваются о формате ответа (тип, язык, кодировка) **не меняя URL**: один и тот же ресурс отдаётся как JSON, XML или CSV в зависимости от заголовков запроса.

**Ключевые заголовки** — и главное помнить, кто их ставит и в какую сторону они «смотрят»:

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

<a id="q40"></a>

## Q40. Как работают conditional requests (ETag, If-None-Match, If-Modified-Since)?

Условный GET позволяет проверить сохранённую копию без обязательной пересылки всего тела. `ETag` — непрозрачный валидатор представления, а `Last-Modified` — время последнего изменения по версии сервера.

Клиент передаёт `If-None-Match` или `If-Modified-Since`. При подтверждении актуальности сервер отвечает `304 Not Modified`; иначе возвращает новое представление. Для предотвращения потерянной записи используют `If-Match` с сильным сравнением, обычно получая `412 Precondition Failed` при несовпадении. ETag не обязан быть хешем содержимого.

---


<a id="q41"></a>

## Q41. Версионирование REST API: URI, header или Content-Type — что выбрать?

URI path (`/v1/users`), заголовок версии и media type в `Accept` — разные способы указать контракт. Путь виден в адресе и просто участвует в ключе кэша. Заголовок сохраняет путь, но если от него зависит представление, кэш должен учитывать его через `Vary` или настройку ключа.

CDN не обязан игнорировать заголовки: корректность зависит от ответа и конфигурации. Не существует универсально лучшей стратегии. Выберите её до публикации API и документируйте срок поддержки старых версий.

---


<a id="q42"></a>

## Q42. Какие HTTP-методы идемпотентны и почему?

**Идемпотентность** — свойство операции: повтор с теми же параметрами оставляет систему в том же состоянии, что и единственный вызов. Это про **итоговое состояние сервера**, а не про идентичность ответов (статусы могут отличаться). Свойство привязано к семантике метода, поэтому его можно зафиксировать таблицей:

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

<a id="q43"></a>

## Q43. Что такое Problem Details (RFC 9457) и как он реализован в Spring 6?

[RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html) описывает Problem Details для HTTP API и заменяет прежний RFC 7807. Тело JSON ошибки имеет media type `application/problem+json`. Стандартные члены `type`, `title`, `status`, `detail`, `instance` имеют определённый смысл, но не обязаны все присутствовать; расширения разрешены.

Spring Framework 6 предоставляет `ProblemDetail` и `ErrorResponse`. В приложении можно вернуть стандартное тело через обработчик ошибок, сохраняя подходящий HTTP-статус и не выдавая stack trace клиенту.

```json
{"type":"https://api.example.com/problems/not-found","title":"Not Found","status":404,"detail":"Заказ не найден"}
```

---

## See also

- [OAuth2 и авторизация](../security/oauth2-interview.md) — аутентификация и авторизация в REST API
- [GraphQL](graphql-interview.md) — альтернатива REST: гибкие запросы и типизация
- [gRPC](grpc-interview.md) — высокопроизводительный RPC-протокол vs REST
- [Микросервисы](../architecture/microservices-interview.md) — REST как основа межсервисного взаимодействия
- [Интеграционное тестирование](../testing/integration-testing-interview.md) — тестирование REST API
- [Безопасность приложений](../security/application-security-interview.md) — HTTPS, CORS, rate limiting
- [API Design Best Practices](api-design-best-practices-interview.md)
- [API Versioning](api-versioning-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
