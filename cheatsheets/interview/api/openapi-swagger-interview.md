---
title: "Вопросы на собеседовании: OpenAPI / Swagger"
description: "Вопросы и ответы по OpenAPI Specification и Swagger для Java-разработчика: структура документа, springdoc-openapi, аннотации, contract-first vs code-first, генерация клиентов, OAuth2, линтинг."
tags:
  - interview
  - api
  - openapi-swagger-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "OpenAPI / Swagger"
  - "OpenAPI interview"
  - "Swagger interview"
prerequisites:
  - "[[openapi-swagger]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `OpenAPI` / `Swagger`

Практичные вопросы и ответы по `OpenAPI Specification` и экосистеме `Swagger` для Java-разработчика: структура документа, `springdoc-openapi`, аннотации, `contract-first` vs `code-first`, генерация клиентов, `OAuth2`, контрактное тестирование, обратная совместимость.

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и структура**
- [Q1. Что такое OpenAPI Specification (OAS)?](#q1-что-такое-openapi-specification-oas)
- [Q2. Версии OAS: 2.0 vs 3.0 vs 3.1 — ключевые отличия](#q2-версии-oas-20-vs-30-vs-31--ключевые-отличия)
- [Q3. (!) Swagger vs OpenAPI — в чём разница терминологии?](#q3--swagger-vs-openapi--в-чём-разница-терминологии)
- [Q4. Структура OpenAPI-документа: верхнеуровневые секции](#q4-структура-openapi-документа-верхнеуровневые-секции)
- [Q5. Секция paths: как описывается эндпоинт?](#q5-секция-paths-как-описывается-эндпоинт)
- [Q6. Секция components: для чего используется?](#q6-секция-components-для-чего-используется)

**Swagger UI и springdoc**
- [Q7. (!) Что такое Swagger UI и как он работает?](#q7--что-такое-swagger-ui-и-как-он-работает)
- [Q8. springdoc-openapi: зависимость и автоконфигурация](#q8-springdoc-openapi-зависимость-и-автоконфигурация)
- [Q9. (!) Как подключить springdoc-openapi к Spring Boot проекту?](#q9--как-подключить-springdoc-openapi-к-spring-boot-проекту)

**Аннотации springdoc**
- [Q10. Аннотация @Operation: назначение и параметры](#q10-аннотация-operation-назначение-и-параметры)
- [Q11. Аннотации @Parameter и @Schema](#q11-аннотации-parameter-и-schema)
- [Q12. (!) Аннотация @ApiResponse и @ApiResponses](#q12--аннотация-apiresponse-и-apiresponses)
- [Q13. Аннотации @Tag и @Tags для группировки](#q13-аннотации-tag-и-tags-для-группировки)
- [Q14. @SecurityScheme и @SecurityRequirement](#q14-securityscheme-и-securityrequirement)
- [Q15. (!) springdoc + Spring Security: как настроить доступ к документации?](#q15--springdoc--spring-security-как-настроить-доступ-к-документации)

**Contract-first vs code-first**
- [Q16. Contract-first подход: определение и преимущества](#q16-contract-first-подход-определение-и-преимущества)
- [Q17. (!) Code-first подход: определение и компромиссы](#q17--code-first-подход-определение-и-компромиссы)
- [Q18. Contract-first vs Code-first: когда что выбирать?](#q18-contract-first-vs-code-first-когда-что-выбирать)

**openapi-generator**
- [Q19. (!) openapi-generator: что это и как использовать?](#q19--openapi-generator-что-это-и-как-использовать)
- [Q20. Генерация серверных стабов через openapi-generator](#q20-генерация-серверных-стабов-через-openapi-generator)
- [Q35. (!) API-first workflow: практический пайплайн с openapi-generator](#q35--api-first-workflow-практический-пайплайн-с-openapi-generator)
- [Q36. Delegate pattern в openapi-generator: когда применять](#q36-delegate-pattern-в-openapi-generator-когда-применять)
- [Q37. Кастомизация Mustache-шаблонов openapi-generator](#q37-кастомизация-mustache-шаблонов-openapi-generator)

**Продвинутая конфигурация springdoc**
- [Q21. springdoc с Spring WebFlux / реактивным API](#q21-springdoc-с-spring-webflux--реактивным-api)
- [Q22. Кастомизация springdoc: OpenApiCustomizer](#q22-кастомизация-springdoc-openapicustomizer)
- [Q23. (!) GroupedOpenApi: разбивка документации на группы](#q23--groupedopenapi-разбивка-документации-на-группы)
- [Q24. Валидация входящих запросов по схеме: @Valid и @Validated](#q24-валидация-входящих-запросов-по-схеме-valid-и-validated)
- [Q40. @Hidden и скрытие операций из документации](#q40-hidden-и-скрытие-операций-из-документации)

**Проектирование спецификаций**
- [Q25. Spectral: линтинг OpenAPI-спецификаций](#q25-spectral-линтинг-openapi-спецификаций)
- [Q26. (!) Версионирование API: /v1/ vs заголовки](#q26--версионирование-api-v1-vs-заголовки)
- [Q27. discriminator и полиморфизм: oneOf / anyOf / allOf](#q27-discriminator-и-полиморфизм-oneof--anyof--allof)
- [Q28. $ref: переиспользование компонентов спецификации](#q28-ref-переиспользование-компонентов-спецификации)
- [Q34. Callbacks vs Webhooks в OpenAPI 3.1](#q34-callbacks-vs-webhooks-в-openapi-31)
- [Q39. Множественные примеры: examples vs example, переиспользуемые примеры](#q39-множественные-примеры-examples-vs-example-переиспользуемые-примеры)
- [Q41. (!) Пагинация в OpenAPI: query параметры, cursor, Link header](#q41--пагинация-в-openapi-query-параметры-cursor-link-header)
- [Q42. multipart/form-data и загрузка файлов в OpenAPI](#q42-multipartform-data-и-загрузка-файлов-в-openapi)

**CI/CD и контрактное тестирование**
- [Q29. (!) OpenAPI в CI/CD: generate-and-compare, contract testing](#q29--openapi-в-cicd-generate-and-compare-contract-testing)
- [Q30. Contract testing с Pact: как соотносится с OpenAPI?](#q30-contract-testing-с-pact-как-соотносится-с-openapi)
- [Q38. (!) Backward compatibility: openapi-diff и oasdiff в CI](#q38--backward-compatibility-openapi-diff-и-oasdiff-в-ci)

**Ошибки и безопасность**
- [Q31. (!) Документирование ошибок: ProblemDetail (RFC 7807)](#q31--документирование-ошибок-problemdetail-rfc-7807)
- [Q32. OAuth2 и Bearer token в Swagger UI](#q32-oauth2-и-bearer-token-в-swagger-ui)

**Практика**
- [Q33. Лучшие практики написания OpenAPI-спецификаций](#q33-лучшие-практики-написания-openapi-спецификаций)

---

## Q1. Что такое OpenAPI Specification (OAS)?

**OpenAPI Specification (OAS)** — стандарт описания REST API в машиночитаемом формате (`YAML` или `JSON`). Позволяет формально описать эндпоинты, параметры запросов, тела запросов и ответов, схемы данных, механизмы аутентификации.

Ключевые свойства:
- **Независимость от языка** — описание API не зависит от реализации
- **Инструментальная экосистема** — генерация клиентов, серверных стабов, документации
- **Контракт** — служит соглашением между командами frontend и backend
- **Валидация** — можно автоматически проверять запросы и ответы на соответствие спецификации

Формат спецификации определяется консорциумом **OpenAPI Initiative (OAI)** под эгидой Linux Foundation.

---


> [!mcq]
> - [ ] OpenAPI Specification — это runtime-фреймворк для генерации REST API на Java, аналог Spring Boot | ❌ ПОСЛЕДСТВИЕ: разработчик ищет в OAS возможности запуска приложения, не находит; реально OAS — это только формат описания API в YAML/JSON, не исполняемый код.
> - [ ] OAS привязан к Java/Spring — для Node.js/Python/Go спецификация не работает | ❌ ПОСЛЕДСТВИЕ: команда выбирает альтернативный формат вместо OAS для микросервисов на разных языках; реально OAS — language-agnostic, openapi-generator поддерживает 50+ языков.
> - [x] **OpenAPI Specification (OAS)** — стандарт описания REST API в машиночитаемом формате (YAML или JSON): эндпоинты, параметры, тела запросов/ответов, схемы данных, аутентификация; ключевые свойства — независимость от языка, инструментальная экосистема (генерация клиентов, серверных стабов, документации), служит контрактом между frontend и backend, позволяет автоматически валидировать запросы/ответы; формат определяется консорциумом OpenAPI Initiative под Linux Foundation | ✓ ПРИМЕНЯТЬ: для документирования любого REST API, генерации SDK, контрактного тестирования 📋 ПРАВИЛО: OAS = язык-агностичный контракт + tooling-экосистема 🔗 См. Q2
> - [ ] OAS требует обязательного использования JSON — YAML-формат не поддерживается официальными парсерами | ❌ ПОСЛЕДСТВИЕ: разработчик переписывает удобные для чтения YAML-спеки в JSON; реально OAS поддерживает оба формата, YAML предпочтительнее для чтения, JSON — для машинной обработки.

## Q2. Версии OAS: 2.0 vs 3.0 vs 3.1 — ключевые отличия

| Аспект | OAS 2.0 (Swagger) | OAS 3.0.x | OAS 3.1.x |
|--------|-------------------|-----------|-----------|
| Тело запроса | `body`/`formData` параметры | `requestBody` объект | `requestBody` объект |
| Компоненты | `definitions`, `parameters` | `components` | `components` |
| Примеры | ограничены | `examples` объект | `examples` объект |
| Webhooks | нет | нет | `webhooks` секция |
| JSON Schema | подмножество draft-04 | подмножество draft-07 | полный draft 2020-12 |
| Multiple servers | нет (`host`, `basePath`) | `servers` массив | `servers` массив |
| Nullable | `x-nullable` (расширение) | `nullable: true` | `type: ["string", "null"]` |
| Links | нет | `links` объект | `links` объект |

**OAS 3.1** — наиболее актуальная версия, полностью совместима с **JSON Schema draft 2020-12**. Используется `springdoc-openapi` начиная с версии 2.x (Spring Boot 3).

---


> [!mcq]
> - [ ] OAS 3.0 и 3.1 полностью совместимы по JSON Schema — оба используют draft 2020-12 | ❌ ПОСЛЕДСТВИЕ: разработчик ожидает работающие type unions `type: ["string", "null"]` в OAS 3.0 spec, но валидатор падает; реально только 3.1 поддерживает полный JSON Schema draft 2020-12, 3.0 использует подмножество draft-07.
> - [x] **OAS 2.0 (Swagger)**: `body`/`formData` параметры, `definitions`+`parameters`, нет `servers` (только `host`/`basePath`), `x-nullable` как расширение, JSON Schema подмножество draft-04. **OAS 3.0.x**: `requestBody`, `components`, `servers` массив, `nullable: true`, `links`, draft-07. **OAS 3.1.x**: всё из 3.0 + `webhooks` секция, type unions (`type: ["string", "null"]`), полный JSON Schema draft 2020-12 | ✓ ПРИМЕНЯТЬ: для новых проектов — OAS 3.1 (springdoc 2.x на Spring Boot 3); для legacy миграции — знать различия 📋 ПРАВИЛО: 2.0=definitions, 3.x=components, 3.1=webhooks+JSON Schema 2020-12 🔗 См. Q3
> - [ ] `webhooks` появились в OAS 3.0 как способ описывать колбэки от провайдера к потребителю | ❌ ПОСЛЕДСТВИЕ: разработчик пытается использовать верхнеуровневую `webhooks` секцию в spec с `openapi: "3.0.0"`, валидатор её не распознаёт; реально верхнеуровневые `webhooks` появились только в 3.1, в 3.0 для этого использовались `callbacks` внутри operation.
> - [ ] OAS 2.0 поддерживает несколько серверов через `servers` массив, OAS 3.x от этой возможности отказался | ❌ ПОСЛЕДСТВИЕ: разработчик ищет `servers` в OAS 2.0 spec, не находит, либо использует одинокий `host` в OAS 3.x; реально всё наоборот — в OAS 2.0 был только `host`+`basePath`, multiple servers появились в 3.0.

## Q3. (!) Swagger vs OpenAPI — в чём разница терминологии?

Исторически **Swagger** — это проект компании SmartBear, включавший:
1. **Swagger Specification** — формат описания API (версии 1.x, 2.0)
2. **Swagger UI** — визуализация и тестирование API
3. **Swagger Codegen** — генератор клиентов

В 2016 году SmartBear передал спецификацию в **OpenAPI Initiative**, и с версии 3.0 она называется **OpenAPI Specification**. Инструменты сохранили название Swagger:

- **OpenAPI** = спецификация (стандарт)
- **Swagger** = инструменты (UI, Codegen, Editor) и историческое название

Сегодня говорить "Swagger-спецификация" для OAS 3.x — технически некорректно, но повсеместно употребляется в обиходе.

---


> [!mcq]
> - [ ] Swagger и OpenAPI — полные синонимы, оба термина означают одну и ту же спецификацию | ❌ ПОСЛЕДСТВИЕ: разработчик использует Swagger 2.0 spec в проекте, ожидающем OAS 3.x, теряет поддержку `requestBody`/`components`/`servers`, инструменты не парсят корректно.
> - [x] **OpenAPI** = название спецификации (стандарт) с 2017 года (OAS 3.0+), которое управляется OpenAPI Initiative под Linux Foundation; **Swagger** = (1) историческое название спеки до 2017 (Swagger Spec 1.x/2.0), (2) бренд инструментов SmartBear (Swagger UI, Swagger Editor, Swagger Codegen, SwaggerHub), сохранённый после передачи стандарта; в обиходе "Swagger" для OAS 3.x некорректно но распространено | ✓ ПРИМЕНЯТЬ: говорить "OpenAPI spec" для документа, "Swagger UI" для инструмента 📋 ПРАВИЛО: OpenAPI=стандарт, Swagger=tooling+history 🔗 См. Q4
> - [ ] OpenAPI — это новое название Swagger UI после ребрендинга SmartBear в 2017 году | ❌ ПОСЛЕДСТВИЕ: разработчик ищет инструмент "OpenAPI UI", не находит, путается; реально OpenAPI — это название самой спеки, а UI остался Swagger UI.
> - [ ] OpenAPI Specification версии 4 — текущий стандарт, поддерживается всеми современными инструментами | ❌ ПОСЛЕДСТВИЕ: разработчик использует версию `openapi: "4.0.0"` в spec, парсеры падают с unknown version; реально актуальная версия OAS 3.1 (или работа над Moonwalk/3.2).

## Q4. Структура OpenAPI-документа: верхнеуровневые секции

```yaml
openapi: "3.1.0"          # версия спецификации (обязательно)
info:                      # метаданные API (обязательно)
  title: "My API"
  version: "1.0.0"
  description: "..."
  contact:
    name: "Team"
    email: "team@example.com"
servers:                   # базовые URL серверов
  - url: "https://api.example.com/v1"
paths:                     # эндпоинты (обязательно)
  /users:
    get: ...
components:                # переиспользуемые объекты
  schemas: ...
  parameters: ...
  responses: ...
  securitySchemes: ...
tags:                      # группировка операций
  - name: "Users"
security:                  # глобальная безопасность
  - BearerAuth: []
webhooks:                  # OAS 3.1: входящие запросы
  newOrder: ...
```

Обязательные поля: `openapi`, `info`, `paths`.

---


> [!mcq]
> - [ ] Обязательные поля: `openapi`, `info`, `paths`, `servers` — без `servers` Swagger UI не сможет отправить запросы | ❌ ПОСЛЕДСТВИЕ: при экспорте spec без `servers` валидатор ругается ошибочно, разработчик добавляет fake-URL; реально `servers` опционален (по умолчанию `/`), а Swagger UI шлёт запросы на тот же origin, где загружена документация.
> - [x] Обязательные верхнеуровневые поля: `openapi` (версия спеки, "3.1.0"), `info` (метаданные — title, version, description, contact), `paths` (эндпоинты); опциональные: `servers` (базовые URL), `components` (переиспользуемые объекты), `tags` (группировка), `security` (глобальная аутентификация), `webhooks` (OAS 3.1 — входящие запросы от сервера к клиенту) | ✓ ПРИМЕНЯТЬ: каждая spec должна иметь минимум openapi+info+paths и осмысленные info.title/version 📋 ПРАВИЛО: openapi + info + paths = валидный минимум 🔗 См. Q5
> - [ ] `webhooks` — это секция в OAS 3.0, в 3.1 её переименовали в `callbacks` | ❌ ПОСЛЕДСТВИЕ: разработчик использует `callbacks` для описания исходящих webhook-запросов от своего сервиса, парсеры не валидируют корректно; реально наоборот — `callbacks` есть в обеих версиях (привязаны к operation), а `webhooks` — новая верхнеуровневая секция в 3.1 для серверо-инициированных запросов.
> - [ ] `info.version` означает версию OpenAPI Specification (3.1.0) — она должна совпадать с `openapi` | ❌ ПОСЛЕДСТВИЕ: разработчик ставит `info.version: "3.1.0"`, дублируя поле `openapi`; реально `info.version` — это версия API ("1.0.0", "2.3.1"), а `openapi` — версия самого формата spec.

## Q5. Секция paths: как описывается эндпоинт?

```yaml
paths:
  /users/{id}:
    get:
      summary: "Получить пользователя"
      operationId: "getUserById"      # уникальный ID операции
      tags:
        - "Users"
      parameters:
        - name: id
          in: path                    # path | query | header | cookie
          required: true
          schema:
            type: integer
      responses:
        "200":
          description: "Успешный ответ"
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/User"
        "404":
          $ref: "#/components/responses/NotFound"
```

`operationId` — важен для генерации клиентов: становится именем метода.

---


> [!mcq]
> - [ ] Параметры path указываются `in: query` — разница между path и query параметрами только в синтаксисе URL | ❌ ПОСЛЕДСТВИЕ: `{id}` в шаблоне пути остаётся пустым, валидация ОAS падает с "path parameter 'id' is not declared", spec невалидна.
> - [x] Каждый эндпоинт в `paths` описывается путём (`/users/{id}`) и HTTP-методом (get/post/...) с полями: `summary`, `operationId` (уникальный ID, становится именем метода в openapi-generator), `tags` (группировка), `parameters` с `in: path|query|header|cookie` и `schema`, `responses` с HTTP-кодом и `content.<media-type>.schema` (часто через `$ref` на `components/schemas`) | ✓ ПРИМЕНЯТЬ: для описания всех эндпоинтов с явными operationId и $ref на общие модели 📋 ПРАВИЛО: operationId + tags + parameters[in] + responses[$ref] 🔗 См. Q6
> - [ ] `operationId` опционален и нужен только для UI — на генерацию клиентов он не влияет | ❌ ПОСЛЕДСТВИЕ: openapi-generator сам генерирует имена методов вида `getUsersIdGet`, нечитаемые и нестабильные между версиями spec.
> - [ ] Ответы в `responses` можно указать одним кодом — оставшиеся (4xx, 5xx) Swagger UI добавит автоматически | ❌ ПОСЛЕДСТВИЕ: клиенты не получают типизированных моделей для ошибок, в SDK обработка 400/404/500 идёт через `Exception` без структуры.

## Q6. Секция components: для чего используется?

`components` содержит переиспользуемые объекты, на которые можно ссылаться через `$ref`:

```yaml
components:
  schemas:           # модели данных
    User:
      type: object
      properties:
        id: { type: integer }
        name: { type: string }
  parameters:        # общие параметры
    PageParam:
      name: page
      in: query
      schema: { type: integer, default: 0 }
  responses:         # стандартные ответы
    NotFound:
      description: "Ресурс не найден"
  requestBodies:     # тела запросов
  headers:           # заголовки
  securitySchemes:   # схемы аутентификации
  links:             # связи между операциями
  callbacks:         # колбэки (webhooks в OAS 3.0)
  examples:          # примеры данных
  pathItems:         # OAS 3.1: переиспользуемые path items
```

---


> [!mcq]
> - [ ] `components` в OAS 3.x содержит только схемы (`schemas`) — параметры и ответы остаются inline в `paths` | ❌ ПОСЛЕДСТВИЕ: разработчик дублирует общие параметры пагинации в каждой операции, ответы 404/500 копируются в десятках мест, изменение в одном — рассинхрон.
> - [ ] `components` это секция OAS 2.0 (Swagger) — в OAS 3.x её заменили на `definitions` | ❌ ПОСЛЕДСТВИЕ: разработчик использует `definitions` в OAS 3.x spec, парсеры её не распознают, $ref не резолвится, openapi-generator падает; реально всё наоборот — OAS 2.0 использовал `definitions`/`parameters`, OAS 3.x всё объединил в `components`.
> - [x] `components` содержит переиспользуемые объекты, на которые ссылаются через `$ref`: `schemas` (модели), `parameters` (общие параметры вроде `PageParam`), `responses` (стандартные ответы NotFound/Unauthorized), `requestBodies`, `headers`, `securitySchemes`, `links`, `callbacks`, `examples`; в OAS 3.1 добавились `pathItems` (переиспользуемые path items для callbacks/webhooks) | ✓ ПРИМЕНЯТЬ: для общих DTO (User, ErrorResponse), параметров пагинации, стандартных ответов 4xx/5xx, schemas безопасности 📋 ПРАВИЛО: повторяется → выноси в components + $ref 🔗 См. Q7
> - [ ] Объекты в `components` загружаются Swagger UI отдельно — если не указать в `paths`, они в документации не появятся | ❌ ПОСЛЕДСТВИЕ: разработчик считает что схемы в `components/schemas`, не используемые в `paths`, не попадают в spec; реально они генерируются в spec в любом случае (для использования через external $ref).

## Q7. (!) Что такое Swagger UI и как он работает?

**Swagger UI** — веб-приложение, которое визуализирует OpenAPI-спецификацию в интерактивном формате:
- Отображает все эндпоинты с описаниями, параметрами и схемами ответов
- Позволяет выполнять HTTP-запросы прямо из браузера (Try it out)
- Поддерживает аутентификацию (Bearer token, OAuth2, Basic Auth)

Как работает:
1. Swagger UI загружает JSON/YAML из `/v3/api-docs` (`springdoc-openapi`)
2. Парсит спецификацию и строит интерактивный UI
3. Запросы из UI идут напрямую к API (не через прокси)

По умолчанию в `springdoc-openapi`:
- Документация: `GET /v3/api-docs`
- UI: `GET /swagger-ui.html` → редиректит на `/swagger-ui/index.html`

Настройка пути:
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---


> [!mcq]
> - [ ] Swagger UI проксирует все запросы из браузера через свой backend, добавляя CORS-заголовки автоматически | ❌ ПОСЛЕДСТВИЕ: разработчик ждёт что CORS-проблемы решатся сами, не настраивает CORS на API — Try-it-out из браузера падает с `CORS policy` ошибкой; реально UI шлёт запросы напрямую к API.
> - [x] Swagger UI — статическое веб-приложение (HTML/JS), которое загружает OpenAPI spec из `/v3/api-docs` (для springdoc), парсит её и строит интерактивный UI; запросы из "Try it out" идут напрямую из браузера к API (не через прокси), поддерживая Bearer, OAuth2, BasicAuth; путь UI настраивается через `springdoc.swagger-ui.path`, путь spec — через `springdoc.api-docs.path` | ✓ ПРИМЕНЯТЬ: для интерактивной документации с возможностью протестировать API из браузера 📋 ПРАВИЛО: UI = static + fetch spec + direct API calls 🔗 См. Q8
> - [ ] Swagger UI требует серверную часть на Java для рендеринга — это не статика, а server-side rendered приложение | ❌ ПОСЛЕДСТВИЕ: разработчик не может развернуть Swagger UI как статику в S3/CDN, либо думает что нужен отдельный сервис; реально это чистый JS/HTML, который грузится springdoc'ом как ресурсы.
> - [ ] Swagger UI и `/v3/api-docs` это один эндпоинт — Swagger UI рендерится прямо на `/v3/api-docs` | ❌ ПОСЛЕДСТВИЕ: разработчик пытается открыть `http://localhost:8080/v3/api-docs` в браузере, получает JSON вместо UI; реально UI отдельно (`/swagger-ui.html` → `/swagger-ui/index.html`), а `/v3/api-docs` отдаёт JSON spec.

## Q8. springdoc-openapi: зависимость и автоконфигурация

**springdoc-openapi** — библиотека для автоматической генерации OpenAPI 3.x документации из Spring Boot приложений.

Зависимости (Spring Boot 3.x / OAS 3.1):
```xml
<!-- Maven -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

```gradle
// Gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
```

Для WebFlux:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

Автоконфигурация сканирует `@RestController`, `@RequestMapping`, `@GetMapping` и строит спецификацию. Никакого дополнительного кода не требуется для базовой конфигурации.

---


> [!mcq]
> - [ ] springdoc-openapi включает Swagger UI как отдельный модуль, который нужно подключать `springdoc-openapi-ui` дополнительно к основному стартеру | ❌ ПОСЛЕДСТВИЕ: разработчик использует два стартера 1.x-эры (springdoc-openapi-ui и springdoc-openapi-data-rest), получает конфликты версий и зависимостей с Spring Boot 3; реально в 2.x — единый `springdoc-openapi-starter-webmvc-ui`/`-webflux-ui`.
> - [x] springdoc-openapi — библиотека для автоматической генерации OpenAPI 3.x документации; для Spring Boot 3 / OAS 3.1 подключается стартер `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x` (или `-webflux-ui` для реактива); автоконфигурация сканирует `@RestController`, `@RequestMapping`, `@GetMapping/@PostMapping/...` и строит spec — никакого дополнительного кода не нужно для базовой конфигурации | ✓ ПРИМЕНЯТЬ: для добавления Swagger UI в любой Spring Boot REST-сервис 📋 ПРАВИЛО: один стартер = автоконфиг + spec + UI 🔗 См. Q9
> - [ ] springdoc-openapi работает только с XML-конфигурацией Spring — для Spring Boot нужно использовать `springfox` | ❌ ПОСЛЕДСТВИЕ: разработчик выбирает устаревший springfox (не поддерживает Spring Boot 3), сталкивается с несовместимостью и переписывает аннотации; реально springdoc предназначен именно для Spring Boot.
> - [ ] Для генерации spec нужно вручную перечислить все `@RestController` классы через `springdoc.controllers-to-include` — автоматическое сканирование не работает | ❌ ПОСЛЕДСТВИЕ: разработчик добавляет каждый новый контроллер в список вручную, забывает — операции пропадают из доки; реально автосканирование работает по умолчанию.

## Q9. (!) Как подключить springdoc-openapi к Spring Boot проекту?

1. Добавить зависимость (см. Q8).
2. Опционально — описать метаданные API через бин `OpenAPI`:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("My API")
                .version("1.0.0")
                .description("Описание API")
                .contact(new Contact()
                    .name("Team")
                    .email("team@example.com")))
            .externalDocs(new ExternalDocumentation()
                .description("Wiki")
                .url("https://wiki.example.com"));
    }
}
```

3. Конфигурация через `application.yml`:
```yaml
springdoc:
  swagger-ui:
    enabled: true
    try-it-out-enabled: true
    operations-sorter: alpha
  api-docs:
    enabled: true
  show-actuator: false
  packages-to-scan: com.example.api
  paths-to-match: /api/**
```

---


> [!mcq]
> - [x] (1) Подключить стартер `springdoc-openapi-starter-webmvc-ui` (для MVC) или `-webflux-ui` (для WebFlux); (2) опционально объявить `@Bean OpenAPI` с метаданными `info(title/version/description)`, `contact`, `externalDocs`; (3) настроить `application.yml` — `springdoc.swagger-ui.enabled`, `paths-to-match: /api/**`, `packages-to-scan` для фильтрации сканирования | ✓ ПРИМЕНЯТЬ: стандартное подключение в любом Spring Boot 3 проекте 📋 ПРАВИЛО: starter + опциональный @Bean OpenAPI + application.yml настройки 🔗 См. Q10
> - [ ] Достаточно добавить `springfox-boot-starter` — это официальная замена springdoc | ❌ ПОСЛЕДСТВИЕ: springfox не поддерживает Spring Boot 3 (последний релиз 2020 года), при старте получаем `ClassNotFoundException` или несовместимые версии классов; современный путь — springdoc.
> - [ ] Без `@Bean OpenAPI` спецификация не генерируется — это обязательный шаг | ❌ ПОСЛЕДСТВИЕ: разработчик не запускает приложение до создания бина; реально автоконфигурация работает с дефолтным `Info` (title из artifactId), а `@Bean OpenAPI` только кастомизирует метаданные.
> - [ ] `packages-to-scan` нужно указывать всегда, иначе springdoc сканирует только корневой пакет приложения | ❌ ПОСЛЕДСТВИЕ: разработчик пишет лишний `packages-to-scan: com.example` дублируя package главного класса; реально по умолчанию сканируется всё, что находит Spring (через `@SpringBootApplication`), параметр нужен только для сужения.

## Q10. Аннотация @Operation: назначение и параметры

`@Operation` описывает одну HTTP-операцию (метод + путь):

```java
@Operation(
    summary = "Получить пользователя по ID",
    description = "Возвращает полные данные пользователя. Требует роль USER.",
    operationId = "getUserById",
    tags = {"Users"},
    deprecated = false,
    responses = {
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "404", description = "Не найден")
    }
)
@GetMapping("/users/{id}")
public UserDto getUser(@PathVariable Long id) { ... }
```

Ключевые параметры:
- `summary` — краткое описание (до 120 символов)
- `description` — подробное описание (Markdown поддерживается)
- `operationId` — уникальный идентификатор (важен для codegen)
- `tags` — группировка операций
- `hidden = true` — скрыть операцию из документации

---


> [!mcq]
> - [ ] `@Operation` обязательна для каждого метода контроллера — без неё springdoc не включит метод в spec | ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `@Operation` к каждому методу ради видимости; реально springdoc автоматически собирает все `@GetMapping`/`@PostMapping`/`@RequestMapping` методы, аннотация нужна для кастомизации описаний.
> - [x] `@Operation` кастомизирует одну HTTP-операцию: `summary` (до 120 символов), `description` (подробное, Markdown поддерживается), `operationId` (уникальный идентификатор, критичен для openapi-generator — формирует имена методов клиента), `tags` (группировка), `hidden = true` (скрыть из доки), `deprecated`, `responses` для inline-описания ответов | ✓ ПРИМЕНЯТЬ: на public API методах с явным operationId + summary + description 📋 ПРАВИЛО: operationId уникален, в camelCase, стабилен между релизами 🔗 См. Q11
> - [ ] `operationId` springdoc генерирует автоматически из имени метода — указывать его в `@Operation` бессмысленно | ❌ ПОСЛЕДСТВИЕ: при переименовании метода контроллера operationId меняется, openapi-generator перегенерирует клиента с новыми именами методов, ломая всех потребителей.
> - [ ] `description` в `@Operation` поддерживает только plain text — Markdown и переносы строк игнорируются Swagger UI | ❌ ПОСЛЕДСТВИЕ: разработчик пишет описания одной строкой без форматирования, нечитаемо для пользователей; реально Swagger UI рендерит Markdown в `description`.

## Q11. Аннотации @Parameter и @Schema

`@Parameter` описывает параметр запроса:

```java
@GetMapping("/users")
public Page<UserDto> getUsers(
    @Parameter(
        description = "Номер страницы (0-based)",
        example = "0",
        required = false,
        schema = @Schema(minimum = "0")
    )
    @RequestParam(defaultValue = "0") int page,

    @Parameter(hidden = true)  // скрыть из документации
    @AuthenticationPrincipal UserDetails user
) { ... }
```

`@Schema` описывает модель данных:

```java
@Schema(description = "Данные пользователя")
public class UserDto {

    @Schema(description = "Идентификатор пользователя", example = "42")
    private Long id;

    @Schema(description = "Email адрес", format = "email", maxLength = 255)
    private String email;

    @Schema(description = "Роль", allowableValues = {"USER", "ADMIN"})
    private String role;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;
}
```

---


> [!mcq]
> - [ ] `@Parameter` и `@Schema` — обязательные аннотации, без них springdoc выбрасывает `IllegalStateException` при старте | ❌ ПОСЛЕДСТВИЕ: разработчик загромождает контроллеры аннотациями ради старта; реально springdoc генерирует spec и без них, аннотации нужны только для кастомизации описаний, examples, format, allowableValues.
> - [x] `@Parameter(description, example, required, schema = @Schema(...))` кастомизирует описание параметра запроса (path/query/header/cookie), `@Parameter(hidden = true)` скрывает из доки (например, `@AuthenticationPrincipal`); `@Schema(description, format, allowableValues, accessMode = READ_ONLY/WRITE_ONLY, minLength/maxLength)` описывает модель DTO и её поля — `format: email`/`uuid`, `readOnly` для полей, заполняемых сервером | ✓ ПРИМЕНЯТЬ: для добавления примеров, форматов и человеко-читаемых описаний 📋 ПРАВИЛО: @Schema на каждое поле DTO + example + format 🔗 См. Q12
> - [ ] `@Schema(accessMode = READ_ONLY)` делает поле обязательным при создании ресурса | ❌ ПОСЛЕДСТВИЕ: разработчик считает что `readOnly` обязывает клиента передавать значение, но фактически readOnly означает что поле возвращается сервером, не принимается в запросе — например, `createdAt`, `id` после создания.
> - [ ] `@Parameter(hidden = true)` нужно ставить на каждый внутренний параметр — без него springdoc документирует `HttpServletRequest`, `BindingResult` и подобные технические объекты | ❌ ПОСЛЕДСТВИЕ: разработчик добавляет hidden=true к каждому параметру руками; реально springdoc умеет автоматически скрывать `HttpServletRequest`/`Principal`/`BindingResult` и подобные spring-типы.

## Q12. (!) Аннотация @ApiResponse и @ApiResponses

`@ApiResponse` описывает возможный HTTP-ответ:

```java
@Operation(summary = "Создать пользователя")
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Пользователь создан",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(
                name = "example",
                value = """{"id": 1, "email": "user@example.com"}"""
            )
        ),
        headers = @Header(
            name = "Location",
            description = "URL созданного ресурса"
        )
    ),
    @ApiResponse(responseCode = "400", description = "Невалидные данные",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    @ApiResponse(responseCode = "409", description = "Email уже занят")
})
@PostMapping("/users")
public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUserRequest req) { ... }
```

Для переиспользования ответов используют аннотацию на уровне класса или `@ControllerAdvice`.

---


> [!mcq]
> - [ ] Достаточно указать `@ApiResponse(responseCode = "200")` — остальные коды springdoc автоматически выводит из типа возвращаемого значения и `ResponseEntity` | ❌ ПОСЛЕДСТВИЕ: ошибочные коды (400, 404, 409, 500) не документированы, клиенты не знают, какие ошибки ожидать, обработка ошибок в SDK неполная.
> - [ ] `@ApiResponses` нужен только если в методе несколько `@ApiResponse` — для одного ответа аннотация не нужна, springdoc сам сгенерирует код 200 | ❌ ПОСЛЕДСТВИЕ: для `POST` методов springdoc по умолчанию ставит 200 вместо 201, клиенты ожидают неверный код, проверки в integration-тестах падают.
> - [x] `@ApiResponses({@ApiResponse(responseCode, description, content = @Content(schema = @Schema(implementation = ...), examples = @ExampleObject), headers = @Header)})` описывает все возможные HTTP-ответы операции, включая ошибки; для переиспользования ответов между методами — выносить в `@ControllerAdvice` или per-class аннотацию | ✓ ПРИМЕНЯТЬ: для каждой операции документировать 2xx + все возможные 4xx/5xx 📋 ПРАВИЛО: success + validation + auth + not-found + conflict + server-error 🔗 См. Q13
> - [ ] `headers` в `@ApiResponse` нужно объявлять только для CORS-заголовков — `Location`, `ETag` и `Cache-Control` обрабатываются автоматически | ❌ ПОСЛЕДСТВИЕ: клиенты не знают, что POST возвращает `Location` с URL созданного ресурса, не используют его, делают лишний запрос на чтение по id.

## Q13. Аннотации @Tag и @Tags для группировки

`@Tag` группирует операции в Swagger UI:

```java
// На уровне контроллера
@Tag(name = "Users", description = "Операции с пользователями")
@RestController
@RequestMapping("/api/users")
public class UserController { ... }

// На уровне метода (переопределение)
@Tag(name = "Admin", description = "Административные операции")
@Operation(tags = {"Users", "Admin"})
@DeleteMapping("/users/{id}")
public void deleteUser(@PathVariable Long id) { ... }
```

Глобальные теги с описанием определяют порядок групп:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addTagsItem(new Tag().name("Users").description("Управление пользователями"))
        .addTagsItem(new Tag().name("Orders").description("Управление заказами"));
}
```

---


> [!mcq]
> - [ ] `@Tag` нужен только в OAS 2.0 (Swagger) — в OAS 3.x группировка делается через `paths` сегменты, аннотация не работает | ❌ ПОСЛЕДСТВИЕ: разработчик не использует `@Tag` в проекте на springdoc 2.x, все операции попадают в группу "default" в Swagger UI, навигация невозможна для API из десятков эндпоинтов.
> - [ ] Глобальные теги через `OpenAPI.addTagsItem(...)` создаются автоматически — указывать их в `@Bean OpenAPI` не требуется | ❌ ПОСЛЕДСТВИЕ: порядок групп в Swagger UI становится случайным (на основе порядка контроллеров), пользователи видят неестественную сортировку, и теги без описания.
> - [x] `@Tag(name = "Users", description = "...")` на контроллере группирует все его операции в Swagger UI; на уровне метода переопределяет тег контроллера; `@Operation(tags = {"Users", "Admin"})` назначает операцию в несколько групп; глобально через `OpenAPI.addTagsItem(new Tag().name(...).description(...))` задаётся порядок и описания групп | ✓ ПРИМЕНЯТЬ: для API с десятками эндпоинтов чтобы Swagger UI был навигабелен 📋 ПРАВИЛО: один контроллер = один основной тег + опционально override на методах 🔗 См. Q14
> - [ ] `@Tag` на методе игнорируется springdoc — теги работают только на уровне класса-контроллера | ❌ ПОСЛЕДСТВИЕ: разработчик не может переназначить отдельную операцию в другую группу (например, `DELETE /users/{id}` в группу "Admin" вместо "Users"), теряет гибкость организации документации.

## Q14. @SecurityScheme и @SecurityRequirement

Объявление схемы безопасности на уровне приложения:

```java
@Configuration
@SecurityScheme(
    name = "BearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT токен в заголовке Authorization"
)
@SecurityScheme(
    name = "OAuth2",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://auth.example.com/oauth/authorize",
            tokenUrl = "https://auth.example.com/oauth/token",
            scopes = {
                @OAuthScope(name = "read", description = "Чтение данных"),
                @OAuthScope(name = "write", description = "Запись данных")
            }
        )
    )
)
public class OpenApiSecurityConfig { ... }
```

Применение к операции:
```java
@SecurityRequirement(name = "BearerAuth")
@GetMapping("/users/me")
public UserDto getCurrentUser() { ... }
```

Глобально для всех операций:
```java
new OpenAPI().security(List.of(new SecurityRequirement().addList("BearerAuth")));
```

---


> [!mcq]
> - [x] `@SecurityScheme` (на классе конфигурации) объявляет схему безопасности — Bearer JWT (`type = HTTP, scheme = "bearer"`), OAuth2 с flows, API Key, Basic Auth; `@SecurityRequirement(name = "BearerAuth")` на методе/контроллере применяет схему к конкретной операции; глобально применяется через `OpenAPI.security(List.of(...))` | ✓ ПРИМЕНЯТЬ: декларация security в одном месте + точечное применение к закрытым операциям 📋 ПРАВИЛО: SecurityScheme=декларация, SecurityRequirement=применение 🔗 См. Q15
> - [ ] `@SecurityScheme` и `@SecurityRequirement` автоматически создают Spring Security фильтры и защищают эндпоинты | ❌ ПОСЛЕДСТВИЕ: разработчик считает, что аннотации заменяют SecurityFilterChain, не настраивает Spring Security, эндпоинты остаются полностью открытыми; реально аннотации только документируют, защита настраивается отдельно.
> - [ ] `@SecurityRequirement` на классе контроллера не наследуется методами — для каждого метода нужно повторять аннотацию | ❌ ПОСЛЕДСТВИЕ: разработчик дублирует `@SecurityRequirement(name = "BearerAuth")` над 30 методами одного контроллера, забывает в одном — операция показывается как публичная в Swagger UI.
> - [ ] Глобальный `SecurityRequirement` через `OpenAPI.security()` не работает с `@SecurityRequirement` на методе — нужно выбирать один подход | ❌ ПОСЛЕДСТВИЕ: команда дублирует requirements в каждой операции вместо использования глобального defaults; реально per-operation requirement переопределяет глобальный — можно задать default + точечные исключения.

## Q15. (!) springdoc + Spring Security: как настроить доступ к документации?

По умолчанию Spring Security блокирует `/v3/api-docs` и `/swagger-ui/**`. Нужно открыть эти пути:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            // Открыть документацию
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

Для production рекомендуется **отключить** документацию полностью:
```yaml
# application-prod.yml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

Или закрыть с базовой аутентификацией / ограничить по IP через nginx.

---


> [!mcq]
> - [ ] springdoc автоматически прописывает permitAll для `/v3/api-docs/**` и `/swagger-ui/**` — настраивать SecurityFilterChain не нужно | ❌ ПОСЛЕДСТВИЕ: при добавлении Spring Security в проект Swagger UI начинает возвращать 401, разработчик не понимает причину, тратит время на дебаг.
> - [x] По умолчанию Spring Security блокирует пути документации; нужно явно разрешить `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html` через `requestMatchers(...).permitAll()` в SecurityFilterChain; для production рекомендуется отключить полностью через `springdoc.api-docs.enabled=false` + `springdoc.swagger-ui.enabled=false` или закрыть BasicAuth / nginx IP-allowlist | ✓ ПРИМЕНЯТЬ: dev=permitAll, prod=disabled или BasicAuth 📋 ПРАВИЛО: открытая Swagger UI в prod = leakage attack surface 🔗 См. Q16
> - [ ] Открыть только `/swagger-ui.html` достаточно — `/v3/api-docs` Swagger UI читает из браузера, а не сам сервер | ❌ ПОСЛЕДСТВИЕ: Swagger UI грузится, но падает с ошибкой "Failed to load API definition" из-за 401 на `/v3/api-docs/**`, документация не отображается.
> - [ ] В production можно оставить Swagger UI открытым — он не раскрывает чувствительную информацию | ❌ ПОСЛЕДСТВИЕ: spec содержит internal эндпоинты, имена параметров, описания бизнес-логики, security schemes — атакующий получает карту внутреннего API для разведки.

## Q16. Contract-first подход: определение и преимущества

**Contract-first** (API-first): сначала пишем OpenAPI-спецификацию, затем генерируем код.

Процесс:
1. Команды согласуют `openapi.yaml`
2. Генерация серверных интерфейсов: `openapi-generator-maven-plugin`
3. Реализация интерфейсов в контроллерах
4. Генерация клиентских SDK для потребителей

Преимущества:
- **Параллельная разработка** — frontend и backend работают одновременно по контракту
- **Единый источник правды** — спецификация определяет API, а не наоборот
- **Стабильный контракт** — изменения API требуют явного изменения спецификации
- **Лучшее проектирование** — API проектируется осознанно, не "вырастает" из кода
- **Автоматическая валидация** — генераторы создают валидационный код

---


> [!mcq]
> - [ ] Contract-first означает, что spec генерируется из кода контроллеров и фиксируется как контракт после реализации | ❌ ПОСЛЕДСТВИЕ: это описание code-first; команда называет подход contract-first, но не получает его плюсов (параллельная разработка, единый источник правды до кода), spec остаётся реактивной к коду.
> - [x] Contract-first (API-first): сначала пишется `openapi.yaml`, затем через openapi-generator-maven-plugin генерируются серверные интерфейсы и клиентские SDK, контроллеры реализуют сгенерированные интерфейсы; плюсы — параллельная разработка FE+BE по контракту, spec как единый источник правды, стабильный контракт (изменения API = явное изменение spec), осознанное проектирование | ✓ ПРИМЕНЯТЬ: для публичных API, проектов с независимыми FE/BE командами, многими потребителями 📋 ПРАВИЛО: spec → generate → implement, never code → reverse-engineer spec 🔗 См. Q17
> - [ ] Contract-first требует, чтобы spec писался на YAML — JSON-формат не поддерживается openapi-generator | ❌ ПОСЛЕДСТВИЕ: команда отказывается от JSON-spec (которые часто экспортируются из других инструментов), теряет совместимость с pipelines, хотя openapi-generator поддерживает оба формата.
> - [ ] Contract-first работает только с openapi-generator — Maven-плагин swagger-codegen или Spring-стартеры не подходят | ❌ ПОСЛЕДСТВИЕ: команда исключает альтернативные инструменты вроде swagger-codegen или собственные Mustache-шаблоны, теряя гибкость; реально contract-first это методология, а не конкретный инструмент.

## Q17. (!) Code-first подход: определение и компромиссы

**Code-first**: пишем код контроллеров, документация генерируется автоматически через `springdoc-openapi`.

Преимущества:
- **Скорость старта** — нет overhead на написание YAML
- **Синхронизация** — документация всегда актуальна (генерируется из кода)
- **Меньше дублирования** — одна кодовая база, одна правда

Недостатки:
- **Дизайн API диктует реализация** — плохой код → плохой API
- **Нет параллельной разработки** — клиент ждёт реализации
- **Сложнее версионирование** — изменения кода сразу ломают контракт
- **Качество документации** — зависит от аннотаций разработчика

Наиболее популярен в небольших командах и внутренних API. Для публичных API предпочтительнее contract-first.

---


> [!mcq]
> - [ ] Code-first гарантирует, что документация всегда актуальна и полна — клиенты могут полагаться на её точность для генерации SDK | ❌ ПОСЛЕДСТВИЕ: документация генерируется из аннотаций, но качество зависит от разработчика — если @Operation/@ApiResponse не проставлены или содержат ошибки, клиенты получают неточную/неполную spec.
> - [ ] Code-first не подходит для микросервисов — для них единственный валидный подход contract-first | ❌ ПОСЛЕДСТВИЕ: команда отказывается от code-first для внутренних микросервисов, тратит время на проектирование spec вручную там, где параллельная разработка FE+BE не критична.
> - [ ] При code-first контракт стабилен, потому что аннотации не меняются между релизами | ❌ ПОСЛЕДСТВИЕ: разработчик переименовывает поле DTO или меняет тип — spec автоматически меняется, клиенты ломаются без warning'а; контроль обратной совместимости требует отдельной проверки openapi-diff.
> - [x] Code-first: пишем код контроллеров, документация генерируется автоматически через springdoc-openapi из аннотаций; плюсы — скорость старта, синхронизация (всегда актуальна), нет дублирования; минусы — дизайн API диктуется реализацией, нет параллельной разработки FE+BE (клиент ждёт), сложнее версионирование (изменения кода сразу ломают контракт), качество доки зависит от аннотаций | ✓ ПРИМЕНЯТЬ: для внутренних API в небольших командах, MVP, быстрых прототипов 📋 ПРАВИЛО: code-first = trade speed for upfront design 🔗 См. Q18

## Q18. Contract-first vs Code-first: когда что выбирать?

| Критерий | Contract-first | Code-first |
|----------|----------------|------------|
| Публичный API | предпочтительно | рискованно |
| Внутренний API | да | да |
| Параллельная разработка (FE+BE) | да | нет |
| Быстрый прототип / MVP | медленнее | да |
| Много потребителей API | да | с оговорками |
| Размер команды < 3 | overhead | да |
| Микросервисы | да | оба применимы |

Гибридный подход: начать с code-first → зафиксировать спецификацию → перейти на contract-first для стабильного API.

---


> [!mcq]
> - [ ] Contract-first всегда лучше code-first — в современных командах нет причин выбирать code-first | ❌ ПОСЛЕДСТВИЕ: маленькая команда (1-2 разработчика) с MVP получает overhead от поддержки spec вручную, замедляет прототипирование, тратит время на синхронизацию.
> - [ ] Выбор не зависит от размера команды и типа API — главное использовать openapi-generator | ❌ ПОСЛЕДСТВИЕ: проект внутреннего инструмента с одним потребителем тратит время на ревью spec в PR, публичный API с десятками внешних клиентов разрабатывается без зафиксированного контракта.
> - [x] Contract-first — для публичных API, параллельной разработки FE+BE и проектов с многими потребителями; code-first — для маленьких команд (< 3), быстрых прототипов/MVP, внутренних API; для микросервисов применимы оба; гибридный подход — начать code-first, зафиксировать spec, перейти на contract-first после стабилизации | ✓ ПРИМЕНЯТЬ: контракт публикуется наружу = contract-first; короткий внутренний контур = code-first 📋 ПРАВИЛО: public/multi-consumer=contract-first, MVP/small=code-first 🔗 См. Q19
> - [ ] Contract-first и code-first взаимоисключаемы — гибридный подход невозможен без потери спецификации | ❌ ПОСЛЕДСТВИЕ: команда не может стартовать прототип в code-first и потом перейти к contract-first, теряет гибкость; реально миграция возможна через фиксацию сгенерированной spec и перевод spec в источник правды.

## Q19. (!) openapi-generator: что это и как использовать?

**openapi-generator** — инструмент генерации кода из OpenAPI-спецификации. Поддерживает 50+ языков и фреймворков.

Установка и использование CLI:
```bash
# Через npm
npm install @openapitools/openapi-generator-cli -g

# Генерация Java-клиента
openapi-generator-cli generate \
  -i openapi.yaml \
  -g java \
  -o ./client \
  --library resttemplate \
  --api-package com.example.client.api \
  --model-package com.example.client.model
```

Доступные генераторы (`-g`):
- Клиенты: `java`, `kotlin`, `typescript-axios`, `python`, `go`
- Серверы: `spring`, `kotlin-spring`, `nodejs-express-server`

Maven плагин:
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.10.0</version>
    <executions>
        <execution>
            <goals><goal>generate</goal></goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <generateApiTests>false</generateApiTests>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---


> [!mcq]
> - [x] **openapi-generator** — мультиязычный инструмент генерации кода из OpenAPI-spec (50+ генераторов): клиенты (`java`, `kotlin`, `typescript-axios`, `python`, `go`), серверы (`spring`, `kotlin-spring`, `nodejs-express-server`); устанавливается через `npm` или используется как Maven/Gradle-плагин на этапе сборки; запуск: `openapi-generator-cli generate -i openapi.yaml -g java -o ./client --library resttemplate` | ✓ ПРИМЕНЯТЬ: для генерации клиентов из spec во всех потребителях API 📋 ПРАВИЛО: spec → openapi-generator → синхронные модели у всех 🔗 См. Q20
> - [ ] openapi-generator — fork Swagger Codegen, поддерживает только Java и JavaScript | ❌ ПОСЛЕДСТВИЕ: разработчик ищет генератор для Python/Go отдельно (например, swagger-codegen), теряет единый pipeline, разные сервисы получают разные стили генерации.
> - [ ] Запуск openapi-generator во время сборки замедляет билд, поэтому его всегда нужно запускать вручную и коммитить сгенерированный код | ❌ ПОСЛЕДСТВИЕ: разработчики забывают перегенерировать после изменения spec, сгенерированный код устаревает в репозитории, ломается синхронизация контракта.
> - [ ] Для генерации клиента из OpenAPI 3.x нужен отдельный инструмент — openapi-generator поддерживает только OAS 2.0 / Swagger | ❌ ПОСЛЕДСТВИЕ: команда выбирает swagger-codegen вместо openapi-generator (который поддерживает все версии OAS, включая 3.1) и сталкивается с устаревшими шаблонами.

## Q20. Генерация серверных стабов через openapi-generator

Генератор `spring` создаёт интерфейсы, которые нужно реализовать:

```bash
openapi-generator-cli generate \
  -i openapi.yaml \
  -g spring \
  -o ./server \
  --additional-properties \
    interfaceOnly=true,\
    useSpringBoot3=true,\
    useTags=true,\
    apiPackage=com.example.api,\
    modelPackage=com.example.model
```

Параметр `interfaceOnly=true` генерирует только интерфейсы без реализации.

Реализация:
```java
@RestController
public class UsersApiController implements UsersApi {

    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        // своя реализация
        return ResponseEntity.ok(userService.findById(id));
    }
}
```

При изменении спецификации — регенерируем, компилятор укажет на несоответствия в реализации.

---


> [!mcq]
> - [ ] Генератор `spring` создаёт готовые контроллеры с реализацией — нужно только написать сервисный слой и БД-репозитории | ❌ ПОСЛЕДСТВИЕ: разработчик не реализует методы или правит сгенерированные классы вручную, при следующей генерации правки затираются.
> - [x] Генератор `spring` с `interfaceOnly=true` создаёт интерфейсы (`UsersApi`), которые реализует `@RestController implements UsersApi` через `@Override` — при изменении spec регенерируется интерфейс, компилятор укажет на несоответствия в реализации, что обеспечивает синхронизацию контракта и кода | ✓ ПРИМЕНЯТЬ: для contract-first проектов на Spring Boot — spec единственный источник правды 📋 ПРАВИЛО: interfaceOnly=true + implements API + useSpringBoot3 🔗 См. Q21
> - [ ] `interfaceOnly=false` (по умолчанию) — лучший подход: генератор создаёт работающие контроллеры, экономит время | ❌ ПОСЛЕДСТВИЕ: при регенерации сгенерированные контроллеры перезаписывают custom-логику, либо разработчик ставит контроллеры под VCS и теряет смысл генерации.
> - [ ] Опция `useTags=true` группирует сгенерированные контроллеры в один общий интерфейс — для разделения нужны отдельные spec-файлы на каждый тег | ❌ ПОСЛЕДСТВИЕ: разработчик создаёт N spec-файлов вместо одного, теряет $ref-совместность; реально useTags=true создаёт отдельные `XxxApi` интерфейсы (UsersApi, OrdersApi) по тегам.

## Q21. springdoc с Spring WebFlux / реактивным API

Для реактивных приложений используется отдельная зависимость:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

Реактивные типы автоматически распознаются:
- `Mono<T>` → обычный объект
- `Flux<T>` → массив
- `ServerSentEvent<T>` → `text/event-stream`

Functional endpoints (Router Functions) требуют дополнительной конфигурации:

```java
// Документирование через RouterOperation
@RouterOperations({
    @RouterOperation(
        path = "/users/{id}",
        method = RequestMethod.GET,
        operation = @Operation(operationId = "getUser", tags = "Users")
    )
})
@Bean
public RouterFunction<ServerResponse> routes(UserHandler handler) {
    return route()
        .GET("/users/{id}", handler::getUser)
        .POST("/users", handler::createUser)
        .build();
}
```

---


> [!mcq]
> - [ ] springdoc автоматически работает с WebFlux через основной стартер `springdoc-openapi-starter-webmvc-ui` — отдельная зависимость не нужна | ❌ ПОСЛЕДСТВИЕ: при старте reactive-приложения springdoc не находит DispatcherServlet, эндпоинт `/v3/api-docs` отсутствует или возвращает 404, документация не доступна.
> - [ ] `Mono<T>` springdoc документирует как массив (`type: array`), а `Flux<T>` — как одиночный объект | ❌ ПОСЛЕДСТВИЕ: сгенерированный клиент через openapi-generator получает обратные типы, десериализация падает с `JsonMappingException`.
> - [ ] Functional endpoints (Router Functions) полностью несовместимы со springdoc — для WebFlux нужны только аннотированные `@RestController` | ❌ ПОСЛЕДСТВИЕ: команда отказывается от функционального стиля или теряет документацию для RouterFunction-эндпоинтов; реально работают `@RouterOperation`/`@RouterOperations` аннотации над `@Bean RouterFunction`.
> - [x] Подключить `springdoc-openapi-starter-webflux-ui` — отдельный стартер для реактивных приложений; реактивные типы распознаются автоматически (`Mono<T>` → объект, `Flux<T>` → массив, `ServerSentEvent<T>` → `text/event-stream`); для Router Functions используется аннотация `@RouterOperation`/`@RouterOperations` над `@Bean` метода с описанием path, method и `@Operation` | ✓ ПРИМЕНЯТЬ: при использовании Spring WebFlux вместо Spring MVC 📋 ПРАВИЛО: webflux-ui стартер + @RouterOperation для функциональных эндпоинтов 🔗 См. Q22

## Q22. Кастомизация springdoc: OpenApiCustomizer

`OpenApiCustomizer` — интерфейс для программной модификации сгенерированного объекта `OpenAPI`:

```java
@Bean
public OpenApiCustomizer globalHeaderCustomizer() {
    return openApi -> {
        // Добавить глобальный заголовок ко всем операциям
        openApi.getPaths().values().forEach(pathItem ->
            pathItem.readOperations().forEach(operation ->
                operation.addParametersItem(
                    new Parameter()
                        .name("X-Request-ID")
                        .in("header")
                        .required(false)
                        .schema(new StringSchema())
                        .description("Идентификатор запроса для трейсинга")
                )
            )
        );
    };
}

@Bean
public OpenApiCustomizer sortTagsCustomizer() {
    return openApi -> {
        // Сортировать теги
        if (openApi.getTags() != null) {
            openApi.getTags().sort(Comparator.comparing(Tag::getName));
        }
    };
}
```

---


> [!mcq]
> - [x] `OpenApiCustomizer` — функциональный интерфейс (`@Bean`), который принимает уже сгенерированный объект `OpenAPI` и позволяет программно его модифицировать: добавить глобальный заголовок ко всем операциям (через `getPaths().values().forEach(...)`), отсортировать теги, дописать `info`, добавить `servers` или дополнительные `securitySchemes` | ✓ ПРИМЕНЯТЬ: для глобальных модификаций которые невозможно/неудобно делать аннотациями (трейсинг-заголовки, сортировка, custom extensions) 📋 ПРАВИЛО: @Bean OpenApiCustomizer = post-processing шаг 🔗 См. Q23
> - [ ] `OpenApiCustomizer` запускается ДО генерации spec из аннотаций — поэтому изменения в нём затирают `@Operation` и `@ApiResponse` | ❌ ПОСЛЕДСТВИЕ: разработчик ставит summary в OpenApiCustomizer для всех путей, контроллерные `@Operation(summary = "...")` исчезают, документация унифицируется некорректно.
> - [ ] Для кастомизации `info`/`servers` нужно создавать собственный `@Bean OpenAPI` — `OpenApiCustomizer` не имеет доступа к этим полям | ❌ ПОСЛЕДСТВИЕ: разработчик пишет полный `OpenAPI` бин, перекрывая всю автоконфигурацию springdoc, теряет автогенерацию paths и components.
> - [ ] Несколько `OpenApiCustomizer` бинов конфликтуют — нужно объединять всю кастомизацию в один бин | ❌ ПОСЛЕДСТВИЕ: команда городит один монолитный бин на 200 строк (заголовки + сортировка + servers + security), вместо чтобы разделить ответственности по нескольким maintainable бинам.

## Q23. (!) GroupedOpenApi: разбивка документации на группы

`GroupedOpenApi` позволяет создать несколько разделов документации:

```java
@Configuration
public class OpenApiGroupConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .displayName("Public API")
            .pathsToMatch("/api/public/**")
            .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .displayName("Admin API")
            .pathsToMatch("/api/admin/**")
            .addOpenApiCustomizer(openApi ->
                openApi.info(new Info().title("Admin API").version("1.0"))
            )
            .build();
    }
}
```

В Swagger UI появится выпадающий список групп. Каждая группа доступна по своему URL: `/v3/api-docs/public`, `/v3/api-docs/admin`.

Настройка в `application.yml`:
```yaml
springdoc:
  swagger-ui:
    urls:
      - name: Public API
        url: /v3/api-docs/public
      - name: Admin API
        url: /v3/api-docs/admin
```

---


> [!mcq]
> - [ ] `GroupedOpenApi` нужен только для разделения public/private API в одном Swagger UI и не работает с `pathsToMatch` — для path-фильтрации нужно использовать `@Tag` | ❌ ПОСЛЕДСТВИЕ: разработчик загромождает контроллеры тегами, public API смешивается с admin в одном документе, наглядной разбивки в Swagger UI нет.
> - [ ] Создание нескольких `GroupedOpenApi` бинов приводит к конфликту — нужно использовать один глобальный бин с условным включением путей | ❌ ПОСЛЕДСТВИЕ: команда городит сложную логику ветвления в одном бине, теряет возможность отдельных URL `/v3/api-docs/public` и `/v3/api-docs/admin` для CDN/auth-фильтрации.
> - [x] `GroupedOpenApi` (несколько бинов) создаёт отдельные разделы документации с фильтрацией по `pathsToMatch("/api/public/**")` / `packagesToScan(...)`; каждый доступен по своему URL (`/v3/api-docs/public`, `/v3/api-docs/admin`), Swagger UI показывает выпадающий список через `springdoc.swagger-ui.urls`, можно добавить per-group `OpenApiCustomizer` для разной `info` | ✓ ПРИМЕНЯТЬ: для разделения public/admin API, разных версий, разных модулей в монолите 📋 ПРАВИЛО: бин-на-группу + pathsToMatch + swagger-ui.urls 🔗 См. Q24
> - [ ] `displayName` в `GroupedOpenApi.builder()` обязательно равен `group()` — иначе springdoc выбросит `IllegalStateException` при старте | ❌ ПОСЛЕДСТВИЕ: разработчик не использует читаемое имя в Swagger UI (`"Public API"` вместо `"public"`), интерфейс выглядит непрофессионально для конечных пользователей.

## Q24. Валидация входящих запросов по схеме: @Valid и @Validated

**Bean Validation** в связке с OpenAPI-аннотациями:

```java
public class CreateUserRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    @Schema(description = "Имя пользователя", minLength = 2, maxLength = 100)
    private String name;

    @NotNull
    @Email
    @Schema(description = "Email", format = "email")
    private String email;

    @Min(18) @Max(120)
    @Schema(description = "Возраст", minimum = "18", maximum = "120")
    private Integer age;
}

@PostMapping("/users")
public ResponseEntity<UserDto> createUser(
    @RequestBody @Valid CreateUserRequest request
) { ... }
```

Зависимость Bean Validation в Spring Boot 3:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

`springdoc-openapi` автоматически читает `@NotNull`, `@Size`, `@Min`, `@Max`, `@Pattern` и переносит их в OpenAPI-схему.

---


> [!mcq]
> - [ ] Bean Validation работает в Spring Boot 3 из коробки — стартер `spring-boot-starter-validation` не нужен, валидация активируется автоматически | ❌ ПОСЛЕДСТВИЕ: `@Valid` на параметрах игнорируется без стартера, невалидные запросы проходят без 400 Bad Request, ошибки данных доходят до сервисного слоя или БД.
> - [ ] springdoc-openapi игнорирует Bean Validation аннотации — `@NotNull`, `@Size`, `@Min` нужно дублировать в `@Schema(minimum, maximum, required)` вручную | ❌ ПОСЛЕДСТВИЕ: разработчик дублирует ограничения в двух местах, рассинхронизация ведёт к тому, что генерируемый клиент валидирует не то, что сервер.
> - [x] `@Valid` на параметре контроллера (`@RequestBody @Valid CreateUserRequest`) запускает Bean Validation; springdoc-openapi автоматически читает аннотации `@NotNull`, `@Size`, `@Min`, `@Max`, `@Pattern`, `@Email` и переносит их в OpenAPI-схему (`required`, `minLength`, `maximum`, `pattern`, `format: email`) — клиенты получают валидную модель, openapi-generator создаёт DTO с теми же ограничениями | ✓ ПРИМЕНЯТЬ: для входящих DTO в REST-контроллерах 📋 ПРАВИЛО: @Valid + jakarta.validation = автоматический OpenAPI constraint 🔗 См. Q25
> - [ ] `@Validated` на классе контроллера — единственный способ включить валидацию для тел запроса, `@Valid` работает только для path/query параметров | ❌ ПОСЛЕДСТВИЕ: разработчик ставит `@Validated` на контроллер для тел и `@Valid` для путей, в результате метод-уровень валидация (`@Min` на параметре) работает, но `@RequestBody` остаётся невалидированным.

## Q25. Spectral: линтинг OpenAPI-спецификаций

**Spectral** — линтер для OpenAPI и AsyncAPI от Stoplight. Проверяет спецификацию по настраиваемым правилам.

Установка и запуск:
```bash
npm install -g @stoplight/spectral-cli

# Линтинг файла
spectral lint openapi.yaml

# С кастомным набором правил
spectral lint openapi.yaml --ruleset .spectral.yml
```

Пример правил в `.spectral.yml`:
```yaml
extends: ["spectral:oas"]  # встроенные правила OAS

rules:
  operation-summary:
    description: "Каждая операция должна иметь summary"
    severity: error
    given: "$.paths[*][*]"
    then:
      field: summary
      function: truthy

  no-x-internal:
    description: "Не использовать x-internal в публичном API"
    severity: warn
    given: "$..x-internal"
    then:
      function: undefined
```

Интеграция в CI:
```yaml
# GitHub Actions
- name: Lint OpenAPI
  run: spectral lint openapi.yaml --fail-severity=error
```

---


> [!mcq]
> - [x] Spectral от Stoplight — настраиваемый линтер OpenAPI/AsyncAPI: подключается `npm install -g @stoplight/spectral-cli`, использует JSONPath-выражения (`given: "$.paths[*][*]"`) и встроенные функции (`truthy`, `pattern`, `length`) для проверки правил вроде "у каждой операции должен быть summary"; в CI запускается `spectral lint openapi.yaml --fail-severity=error` | ✓ ПРИМЕНЯТЬ: для enforcement API-стиля (operation-id naming, обязательные описания, запрет x-internal) в команде 📋 ПРАВИЛО: extends spectral:oas + custom rules + fail-severity 🔗 См. Q26
> - [ ] Spectral проверяет только синтаксис OpenAPI (валидный YAML/JSON, типы полей) — кастомные правила добавить нельзя | ❌ ПОСЛЕДСТВИЕ: команда не может настроить enforcement стилевых правил (обязательный operationId, формат имен, запрет x-* расширений), стиль API расходится между микросервисами.
> - [ ] Spectral встроен в springdoc и запускается автоматически при старте приложения | ❌ ПОСЛЕДСТВИЕ: разработчик ищет настройки Spectral в application.yml, не находит, делает вывод что линтинга нет; реально Spectral — отдельный Node.js-инструмент, который нужно подключать через CI/pre-commit hook.
> - [ ] `extends: ["spectral:oas"]` в `.spectral.yml` включает все правила, но severity всех правил всегда `warn` — изменить уровень нельзя | ❌ ПОСЛЕДСТВИЕ: команда не может сделать критичные правила блокирующими, билд проходит даже при breaking violations, ценность линтера теряется.

## Q26. (!) Версионирование API: /v1/ vs заголовки

**Подходы к версионированию:**

**1. URI Path (наиболее распространён):**
```
GET /api/v1/users
GET /api/v2/users
```
- Просто, кэшируемо, явно видно в логах
- `springdoc` поддерживает через `GroupedOpenApi` с `pathsToMatch("/api/v1/**")`

**2. Заголовок запроса:**
```
GET /api/users
Accept-Version: v2
```
- Чистые URL, но сложнее в документировании
- В OpenAPI: параметр типа `header`

**3. Content Negotiation (Media Type):**
```
Accept: application/vnd.example.v2+json
```
- RESTful-пуристский подход
- Поддерживается через `produces` в контроллере

**4. Query Parameter:**
```
GET /api/users?version=2
```
- Не рекомендуется — засоряет URL, сложнее кэшировать

**Рекомендация**: URI Path для публичных API; заголовки для внутренних, где URL важен как ресурс.

Документирование в `springdoc`:
```java
@Bean
public GroupedOpenApi v1Api() {
    return GroupedOpenApi.builder()
        .group("v1").pathsToMatch("/api/v1/**").build();
}

@Bean
public GroupedOpenApi v2Api() {
    return GroupedOpenApi.builder()
        .group("v2").pathsToMatch("/api/v2/**").build();
}
```

---


> [!mcq]
> - [ ] Версионирование через query parameter (`?version=2`) — лучший подход: явный, легко документировать, не требует менять URL | ❌ ПОСЛЕДСТВИЕ: версия становится частью query string, кэшируется хуже (CDN видит каждую версию как новый URL с параметрами), засоряет логи и хуже воспринимается клиентами как ресурс.
> - [x] URI Path (`/api/v1/users`) — наиболее распространённый подход: явная видимость в логах и CDN, хорошо кэшируется, легко документируется через `GroupedOpenApi` с `pathsToMatch("/api/v1/**")`; альтернативы — заголовок `Accept-Version` или Media Type `application/vnd.example.v2+json` (RESTful-пуристский) для внутренних API | ✓ ПРИМЕНЯТЬ: URI Path для публичных API, заголовки для внутренних 📋 ПРАВИЛО: public=URI path, internal=headers, никогда query param 🔗 См. Q27
> - [ ] Версии нужно класть в один OpenAPI-документ через теги (`tags: [v1, v2]`) — отдельные spec для каждой версии нарушают принцип единого источника правды | ❌ ПОСЛЕДСТВИЕ: спецификация раздувается дублирующимися схемами User_v1/User_v2, клиенты получают неоднозначные модели, разные команды правят один файл одновременно — конфликты в PR.
> - [ ] Content Negotiation через `Accept: application/vnd.example.v2+json` не поддерживается springdoc — для multimedia-version нужна ручная фильтрация контроллеров | ❌ ПОСЛЕДСТВИЕ: разработчик отказывается от RESTful media-type версионирования или пишет boilerplate-обвязку, хотя `produces` в контроллере и `@Operation` корректно отображают эти варианты.

## Q27. discriminator и полиморфизм: oneOf / anyOf / allOf

OpenAPI поддерживает полиморфизм через композицию схем:

**`allOf`** — наследование/расширение:
```yaml
schemas:
  Animal:
    type: object
    properties:
      name: { type: string }
  Dog:
    allOf:
      - $ref: "#/components/schemas/Animal"
      - type: object
        properties:
          breed: { type: string }
```

**`oneOf`** — ровно одна из схем:
```yaml
schemas:
  Payment:
    oneOf:
      - $ref: "#/components/schemas/CardPayment"
      - $ref: "#/components/schemas/CashPayment"
    discriminator:
      propertyName: type        # поле-дискриминатор
      mapping:
        card: "#/components/schemas/CardPayment"
        cash: "#/components/schemas/CashPayment"
```

**`anyOf`** — одна или несколько схем.

`discriminator` сообщает клиентам, какое поле определяет тип объекта. Генераторы кода используют его для создания наследования.

В Java/springdoc:
```java
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPayment.class, name = "card"),
    @JsonSubTypes.Type(value = CashPayment.class, name = "cash")
})
@Schema(oneOf = {CardPayment.class, CashPayment.class})
public abstract class Payment { ... }
```

---


> [!mcq]
> - [ ] `allOf` означает "одна из перечисленных схем" (как dispatch по типу), а `oneOf` — "наследование/расширение существующей схемы" | ❌ ПОСЛЕДСТВИЕ: разработчик описывает наследование через oneOf, openapi-generator создаёт sealed-классы вместо подклассов, клиенты не могут extend Animal через Dog.
> - [x] `allOf` — композиция/наследование (схема включает все указанные), `oneOf` — строго одна из схем (dispatch по типу), `anyOf` — одна или несколько; `discriminator.propertyName` + `mapping` указывает поле, по значению которого клиенты определяют конкретный подтип, что позволяет openapi-generator сгенерировать полиморфные классы | ✓ ПРИМЕНЯТЬ: для иерархий типов (Payment → CardPayment/CashPayment), наследования базовых полей через allOf 📋 ПРАВИЛО: allOf=extends, oneOf=sealed dispatch, discriminator=type-field 🔗 См. Q28
> - [ ] `discriminator` не нужен при использовании `oneOf` — клиенты сами определят тип по уникальным полям каждого варианта | ❌ ПОСЛЕДСТВИЕ: при пересечении полей между вариантами (например, у CardPayment и CashPayment есть `amount`) десериализатор не может однозначно выбрать тип, openapi-generator падает с `Unable to determine concrete type`.
> - [ ] `anyOf` идентичен `oneOf` — оба означают "выбор одного из вариантов" | ❌ ПОСЛЕДСТВИЕ: при использовании anyOf вместо oneOf JSON Schema валидатор разрешает объекты, удовлетворяющие нескольким схемам сразу, что приводит к runtime-ошибкам десериализации в полиморфных DTO.

## Q28. $ref: переиспользование компонентов спецификации

`$ref` — механизм ссылок в OpenAPI (основан на JSON Reference):

```yaml
paths:
  /users/{id}:
    get:
      parameters:
        - $ref: "#/components/parameters/IdParam"     # локальная ссылка
      responses:
        "200":
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/User"
        "404":
          $ref: "#/components/responses/NotFound"

  /orders/{id}:
    get:
      parameters:
        - $ref: "#/components/parameters/IdParam"     # повторное использование
```

Типы ссылок:
- **Локальная** (`#/components/...`) — внутри документа
- **Файловая** (`./schemas/user.yaml#/User`) — внешний файл, тот же сервер
- **URL** (`https://example.com/schemas/user.yaml`) — удалённый ресурс

Разбивка на файлы для больших спецификаций:
```
api/
  openapi.yaml          # главный файл
  paths/
    users.yaml
    orders.yaml
  schemas/
    user.yaml
    order.yaml
```

---


> [!mcq]
> - [ ] `$ref` поддерживает только локальные ссылки внутри одного файла — внешние файлы или URL не работают | ❌ ПОСЛЕДСТВИЕ: команда копирует общие схемы в каждый сервис вместо общего репозитория, изменения не распространяются, контракты расходятся.
> - [x] `$ref` (на основе JSON Reference) ссылается на компонент тремя способами: локально (`#/components/schemas/User`), на внешний файл (`./schemas/user.yaml#/User`) или на удалённый URL (`https://example.com/schemas/user.yaml`) — это позволяет переиспользовать схемы, параметры и ответы и разбивать большие спецификации на несколько файлов | ✓ ПРИМЕНЯТЬ: для общих моделей (User, ErrorResponse), параметров пагинации, типовых ответов 4xx/5xx 📋 ПРАВИЛО: одна схема — один $ref, никакого копипаста 🔗 См. Q29
> - [ ] При генерации клиентов через openapi-generator `$ref` нужно вручную раскрывать в inline-схемы, иначе генератор создаст пустые классы | ❌ ПОСЛЕДСТВИЕ: разработчик раскрывает $ref в YAML, теряет переиспользование, при изменении схемы приходится править её в нескольких местах вручную.
> - [ ] `$ref` можно совмещать с другими полями в том же объекте (например, добавить description рядом с $ref) — это валидно в OAS 3.0 | ❌ ПОСЛЕДСТВИЕ: в OAS 3.0 это `Sibling properties` запрещены и игнорируются, разработчик ожидает что description применится, но Swagger UI/генератор его не видят (исправлено только в OAS 3.1).

## Q29. (!) OpenAPI в CI/CD: generate-and-compare, contract testing

**Стратегии использования OpenAPI в CI/CD:**

**1. Generate-and-compare:**
Генерируем спецификацию из кода, сравниваем с эталоном в репозитории:
```bash
# Генерация текущей спецификации
curl http://localhost:8080/v3/api-docs > current.json

# Сравнение с зафиксированной версией
diff expected-openapi.json current.json
# Если есть отличия — сборка падает
```

**2. Backward compatibility check с `openapi-diff`:**
```bash
docker run --rm \
  -v $(pwd):/specs openapitools/openapi-diff \
  /specs/old-openapi.yaml /specs/new-openapi.yaml \
  --fail-on-incompatible
```

**3. Lint в PR:**
```yaml
# GitHub Actions
- name: Lint OpenAPI spec
  run: spectral lint openapi.yaml --fail-severity=warn
```

**4. Генерация клиентов при изменении спецификации:**
```yaml
- name: Generate client
  if: steps.diff.outputs.changed == 'true'
  run: openapi-generator-cli generate ...
```

---


> [!mcq]
> - [ ] Достаточно линтинга через Spectral в CI — этого хватает, чтобы spec не ломал backward compatibility | ❌ ПОСЛЕДСТВИЕ: Spectral проверяет только синтаксис и стилистические правила (operationId, описания), но не отлавливает удаление эндпоинта или обязательное поле, ломающие клиентов.
> - [ ] Generate-and-compare надёжен только если генерировать spec во время `bootRun` — собирать его на этапе сборки без запуска приложения нельзя | ❌ ПОСЛЕДСТВИЕ: pipeline поднимает БД и контейнеры ради генерации spec, время билда увеличивается в разы, либо команда пропускает шаг и забывает обновлять зафиксированную spec.
> - [x] Стратегия: (1) generate-and-compare — генерируем spec из кода (`/v3/api-docs`) и сравниваем с зафиксированной в репозитории, (2) `openapi-diff` / `oasdiff` для проверки backward compatibility и фейла билда при breaking changes, (3) Spectral для линтинга, (4) regenerate клиенты при изменении spec | ✓ ПРИМЕНЯТЬ: для API с внешними потребителями где важна стабильность контракта 📋 ПРАВИЛО: diff + openapi-diff + Spectral + regen-clients 🔗 См. Q30
> - [ ] `openapi-diff` нужен только при мажорных релизах — для минорных изменений он избыточен и замедляет CI | ❌ ПОСЛЕДСТВИЕ: разработчик случайно удаляет необязательное поле или переименовывает enum-значение в патч-релизе, клиенты ломаются, backward compatibility теряется незаметно.

## Q30. Contract testing с Pact: как соотносится с OpenAPI?

**Contract Testing** — тестирование контракта между потребителем (consumer) и поставщиком (provider) API.

**Pact** — наиболее популярный фреймворк:
```java
// Consumer side: определяем ожидания
@PactTestFor(providerName = "UserService")
@Pact(consumer = "OrderService")
public RequestResponsePact createPact(PactDslWithProvider builder) {
    return builder
        .given("user 1 exists")
        .uponReceiving("a request for user 1")
        .path("/api/users/1")
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(new PactDslJsonBody()
            .integerType("id", 1)
            .stringType("email", "test@example.com"))
        .toPact();
}
```

**Pact vs OpenAPI:**
- OpenAPI описывает все возможные запросы/ответы
- Pact описывает фактически используемые взаимодействия между конкретными сервисами
- Они дополняют друг друга: OpenAPI — документация, Pact — гарантия совместимости

Интеграция: можно генерировать Pact-контракты из OpenAPI через `pact-jvm-provider-spring` и `openapi-pact-provider`.

---


> [!mcq]
> - [x] Pact описывает фактически используемые consumer-driven взаимодействия (запросы/ответы между конкретными сервисами), OpenAPI описывает все возможные операции API — они дополняют друг друга: OpenAPI это документация и проверка валидности схемы, Pact гарантирует, что текущие зависимости не сломаются | ✓ ПРИМЕНЯТЬ: для микросервисов где важна обратная совместимость с конкретными потребителями 📋 ПРАВИЛО: OpenAPI = catalog, Pact = consumer contract 🔗 См. Q31
> - [ ] Pact полностью заменяет OpenAPI — если есть Pact-контракты, документация в OpenAPI не нужна | ❌ ПОСЛЕДСТВИЕ: новые потребители не знают, какие операции существуют (Pact описывает только используемое), не могут самостоятельно интегрироваться, exploratory testing невозможен.
> - [ ] Pact и OpenAPI несовместимы — нужно выбирать одно: либо contract testing, либо документация | ❌ ПОСЛЕДСТВИЕ: команда отказывается от Pact, теряя проверку совместимости при изменениях, или отказывается от OpenAPI, теряя автогенерацию клиентов и Swagger UI.
> - [ ] Pact — это provider-driven подход (provider определяет контракт, consumers подстраиваются) и работает идентично openapi-diff | ❌ ПОСЛЕДСТВИЕ: команда ожидает что Pact ловит любое изменение API, но на самом деле он проверяет только consumer-described expectations, новые поля и неиспользуемые операции остаются без покрытия.

## Q31. (!) Документирование ошибок: ProblemDetail (RFC 7807)

**RFC 7807 (Problem Details for HTTP APIs)** — стандарт для описания ошибок в REST API. Spring Boot 3 поддерживает нативно через `ProblemDetail`.

Структура:
```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Email адрес обязателен",
  "instance": "/api/users",
  "errors": [{"field": "email", "message": "must not be blank"}]
}
```

Использование в Spring Boot 3:
```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        pd.setType(URI.create("https://example.com/problems/user-not-found"));
        pd.setTitle("User Not Found");
        return pd;
    }
}
```

Включить в Spring Boot:
```yaml
spring:
  mvc:
    problemdetails:
      enabled: true
```

Документирование в OpenAPI:
```java
@ApiResponse(
    responseCode = "400",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
```

---


> [!mcq]
> - [ ] `ProblemDetail` нужно реализовать самостоятельно — Spring Boot 3 не предоставляет встроенного класса для RFC 7807 | ❌ ПОСЛЕДСТВИЕ: команда пишет свой `ApiError` DTO, теряет совместимость с клиентами ожидающими RFC 7807, и нет нативной интеграции с `ResponseEntityExceptionHandler`.
> - [ ] Достаточно вернуть `ProblemDetail` из контроллера — `spring.mvc.problemdetails.enabled` не нужен, обработка стандартных Spring-исключений автоматически использует ProblemDetail | ❌ ПОСЛЕДСТВИЕ: исключения вроде `MethodArgumentNotValidException` возвращают legacy-формат ошибки, клиенты видят разный JSON для своих и фреймворковых ошибок.
> - [ ] RFC 7807 требует обязательное поле `code` с числовым кодом ошибки приложения, иначе спецификация невалидна | ❌ ПОСЛЕДСТВИЕ: разработчик добавляет фейковое поле `code`, клиенты, реализующие чистый RFC 7807, его игнорируют, описание ошибки не унифицируется между сервисами.
> - [x] Использовать `org.springframework.http.ProblemDetail` (Spring Boot 3+), включить `spring.mvc.problemdetails.enabled=true`, обрабатывать исключения в `@ControllerAdvice`, и документировать в OpenAPI через `@ApiResponse(content = @Content(schema = @Schema(implementation = ProblemDetail.class)))` | ✓ ПРИМЕНЯТЬ: при стандартизации формата ошибок в новом REST-сервисе на Spring Boot 3 📋 ПРАВИЛО: ProblemDetail + problemdetails.enabled + @Schema(implementation) 🔗 См. Q32

## Q32. OAuth2 и Bearer token в Swagger UI

Настройка OAuth2 в документации позволяет получать токены прямо из Swagger UI:

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("oauth2", new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                    .authorizationCode(new OAuthFlow()
                        .authorizationUrl("https://auth.example.com/oauth/authorize")
                        .tokenUrl("https://auth.example.com/oauth/token")
                        .scopes(new Scopes()
                            .addString("read", "Чтение данных")
                            .addString("write", "Запись данных")
                        )
                    )
                )
            )
            .addSecuritySchemes("bearer", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
            )
        )
        .security(List.of(new SecurityRequirement().addList("bearer")));
}
```

Конфигурация Swagger UI для OAuth2:
```yaml
springdoc:
  swagger-ui:
    oauth:
      client-id: swagger-client
      scopes: read,write
    oauth2-redirect-url: http://localhost:8080/swagger-ui/oauth2-redirect.html
```

`oauth2-redirect-url` нужно добавить как разрешённый redirect URI в OAuth2 сервере.

---


> [!mcq]
> - [ ] Достаточно объявить `SecurityScheme.Type.OAUTH2` в `OpenAPI` бине — Swagger UI сам подберёт client-id и redirect URI из application.yml без дополнительной настройки | ❌ ПОСЛЕДСТВИЕ: при попытке «Authorize» в Swagger UI не отправляется client_id, OAuth2-провайдер возвращает `invalid_client` и кнопка авторизации не работает.
> - [ ] `oauth2-redirect-url` springdoc можно указать произвольным — OAuth2-сервер примет любой URL, если совпадает client_id | ❌ ПОСЛЕДСТВИЕ: OAuth2-провайдер возвращает `redirect_uri_mismatch`, авторизация падает, пользователи не могут протестировать защищённые эндпоинты через UI.
> - [x] Объявить `SecurityScheme` (OAUTH2 с flows или HTTP bearer) в `components`, добавить `SecurityRequirement` глобально или per-operation, прописать `springdoc.swagger-ui.oauth.client-id` и зарегистрировать `/swagger-ui/oauth2-redirect.html` как разрешённый redirect URI в OAuth2-сервере | ✓ ПРИМЕНЯТЬ: когда нужно тестировать защищённые эндпоинты прямо из Swagger UI без отдельного Postman 📋 ПРАВИЛО: SecurityScheme + client-id + redirect-uri-в-allowlist 🔗 См. Q33
> - [ ] Bearer-токен в Swagger UI работает только если в `SecurityScheme` указать `type: HTTP` без `scheme: bearer` — `scheme` нужен только для Basic Auth | ❌ ПОСЛЕДСТВИЕ: Swagger UI не показывает поле для ввода JWT, токен не подставляется в Authorization-заголовок, запросы возвращают 401.

## Q33. Лучшие практики написания OpenAPI-спецификаций

**Структура и организация:**
- Выносить переиспользуемые компоненты в `components`
- Использовать `$ref` вместо дублирования схем
- Разбивать большие спецификации на несколько файлов
- `operationId` — обязателен, уникален, в `camelCase`

**Описания:**
- Каждая операция должна иметь `summary` (краткое) и `description` (подробное)
- Каждое поле схемы — `description` и `example`
- Документировать все возможные коды ответов, включая ошибки

**Схемы данных:**
- Использовать `$ref` для общих моделей (не копировать)
- Указывать `format` для строк: `email`, `date-time`, `uuid`, `uri`
- Задавать `nullable: true` явно (OAS 3.0) или `type: ["string", "null"]` (OAS 3.1)
- Помечать поля только для чтения: `readOnly: true`

**Безопасность:**
- Документировать все схемы безопасности в `components/securitySchemes`
- Указывать `security` на уровне операций, а не только глобально
- Явно указывать `[]` (пустой массив) для публичных эндпоинтов при глобальной security

**CI/CD:**
- Линтинг Spectral в пайплайне
- Проверка backward compatibility при изменениях
- Версионирование спецификации вместе с кодом


> [!mcq]
> - [ ] Описывать только успешные ответы (2xx) — клиенты сами должны догадываться о возможных ошибках, чтобы спецификация оставалась компактной | ❌ ПОСЛЕДСТВИЕ: openapi-generator не создаст модели ошибок, клиенты ловят 4xx/5xx без типизированных DTO, обработка ошибок дублируется в каждом проекте.
> - [x] Выносить переиспользуемые компоненты в `components` через `$ref`, давать каждой операции `operationId` (camelCase, уникальный), `summary` + `description`, документировать все коды ответов включая ошибки, помечать `readOnly`/`writeOnly` поля и валидировать spec через Spectral в CI | ✓ ПРИМЕНЯТЬ: на старте любого нового API и при ревью pull request с изменениями spec 📋 ПРАВИЛО: $ref + operationId + все коды ответов + Spectral 🔗 См. See also
> - [ ] Лучше копировать схемы между путями вместо `$ref` — это упрощает чтение спецификации и убирает зависимости между файлами | ❌ ПОСЛЕДСТВИЕ: при изменении схемы User в одном месте остальные копии остаются устаревшими, генерируемые клиенты получают рассогласованные модели, появляются runtime-ошибки десериализации.
> - [ ] `operationId` не нужен — openapi-generator сам сгенерирует имена методов из HTTP-метода и пути | ❌ ПОСЛЕДСТВИЕ: имена методов получаются вида `getUsersIdGet`, нечитаемые и нестабильные между версиями spec, рефакторинг ломает клиентский код.

---

## Полезные ссылки

### Официальная документация
- [OpenAPI Specification 3.1](https://spec.openapis.org/oas/v3.1.0)
- [springdoc-openapi документация](https://springdoc.org/)
- [openapi-generator](https://openapi-generator.tech/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

### Инструменты
- [Swagger Editor](https://editor.swagger.io/) — онлайн-редактор спецификаций
- [Stoplight Studio](https://stoplight.io/studio) — GUI-редактор OpenAPI
- [Spectral](https://stoplight.io/open-source/spectral) — линтер OpenAPI
- [openapi-diff](https://github.com/OpenAPITools/openapi-diff) — сравнение версий
- [Redoc](https://redocly.com/redoc/) — альтернатива Swagger UI

### Статьи
- [Documenting a Spring REST API Using OpenAPI 3.0 (Baeldung)](https://www.baeldung.com/spring-rest-openapi-documentation)
- [Spring Boot 3 и ProblemDetail (Baeldung)](https://www.baeldung.com/spring-boot-problem-details)

---

## See also

- [HTTP и REST](http-rest-interview.md) — семантика HTTP, REST-принципы, CORS, кэширование
- [gRPC](grpc-interview.md) — бинарный протокол, Protocol Buffers, стриминг, сравнение с REST
- [GraphQL](graphql-interview.md) — схема, резолверы, N+1 проблема, подписки
- [WebSocket](websocket-interview.md) — двунаправленная связь, STOMP, масштабирование
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — автоконфигурация, стартеры, Actuator
- [Spring Security](../frameworks/spring/spring-security-interview.md) — аутентификация, авторизация, OAuth2, JWT
- [Spring MVC](../frameworks/spring/spring-mvc-interview.md) — контроллеры, фильтры, перехватчики, DispatcherServlet
- [API Gateway](../architecture/api-gateway-interview.md) — маршрутизация, rate limiting, аутентификация на уровне шлюза
- [API Design Best Practices](api-design-best-practices-interview.md)
- [API Versioning](api-versioning-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
- [Шпаргалка: OpenAPI/Swagger](../../development/api/api-tools/swagger/openapi-swagger.md) — теория
