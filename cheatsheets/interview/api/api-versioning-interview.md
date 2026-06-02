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

API versioning — стратегия развития API без поломки существующих клиентов. Главные подходы: **URI versioning** (`/v1/`), **header versioning**, **query parameter**, **content negotiation**. Также сюда входят: обратная совместимость, политики устаревания (deprecation), sunset, semantic versioning. Критично для публичных API и систем с несколькими командами.

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

**Проблема:** потребители API (мобильные приложения, сторонние интеграции) **не обновляются синхронно**. Изменения на стороне сервера могут сломать клиентов.

**Без versioning:**
- Сервер меняет контракт API → существующие клиенты падают
- Мобильные приложения особенно проблемны (медленные циклы обновления)
- Breaking changes → кошмар для службы поддержки

**С versioning:**
- Несколько версий живут параллельно
- Старые клиенты работают со старой версией
- Новые клиенты используют новую версию
- Плавная миграция

**Публичные API обязательно** версионируются. **Внутренние API** иногда обходятся без этого (ради более быстрой итерации).

## Q2. (!) Что такое breaking change?

**Breaking change** — изменение, требующее действий от существующих клиентов.

**Примеры:**
- **Удаление поля** из ответа
- **Переименование поля**
- **Смена типа поля** (string → number)
- **Превращение опционального поля в обязательное** в запросе
- **Добавление нового обязательного поля** в запрос
- **Смена структуры ответа** (object → array)
- **Смена семантики кодов ошибок**
- **Смена схемы аутентификации**
- **Смена URL** (без редиректа)

**При breaking change** → **новая версия** обязательна.


## Q3. (!) Backwards-compatible changes — examples?

**Не breaking (безопасно в той же версии):**
- **Добавление нового опционального поля** в запрос (клиенты его игнорируют)
- **Добавление нового поля** в ответ (старые клиенты игнорируют лишние поля)
- **Добавление нового эндпоинта**
- **Добавление нового опционального query-параметра**
- **Превращение обязательного поля в опциональное**
- **Добавление новых кодов ошибок** (клиенты должны уметь обрабатывать неизвестные коды)

**Tolerant Reader pattern** (закон Постела):
> "Be conservative in what you send, liberal in what you accept."

Клиенты должны:
- Игнорировать неизвестные поля
- Корректно работать при отсутствии опциональных полей
- Аккуратно обрабатывать новые коды ошибок


## Q4. (!) URI versioning?

```
https://api.example.com/v1/users/123
https://api.example.com/v2/users/123
```

**Плюсы:**
- **Просто, видно** прямо в URL
- Легко маршрутизировать (разные версии → разные сервисы)
- Удобно тестировать (дружелюбно к браузеру)
- Понятно пользователям

**Минусы:**
- **Идентичность ресурса меняется** вместе с версией (формально «users в v2» — это не «users в v1»)
- Нарушает чистоту REST (URI должен идентифицировать ресурс, а не версию)
- Несколько URL для одного и того же ресурса

**Самый распространённый** подход. Используется в Twitter, GitHub (раньше), Stripe (раньше).


## Q5. (!) Header versioning?

```http
GET /users/123
Accept: application/vnd.example.v2+json
```

или кастомный заголовок:
```http
GET /users/123
X-API-Version: 2
```

**Плюсы:**
- **URI не меняется** (чистый REST)
- Идентичность ресурса сохраняется
- Проще добавлять новые версии

**Минусы:**
- **Менее заметно** (тяжело тестировать в браузере)
- Сложнее документировать
- Усложняется кэширование (заголовок Vary)

**Используется в:** GitHub (более новый API).


## Q6. (!) Query parameter versioning?

```
https://api.example.com/users/123?version=2
https://api.example.com/users/123?api-version=2024-01-15
```

**Плюсы:**
- Видно в URL
- Удобно тестировать

**Минусы:**
- Засоряет query string
- Менее устоявшаяся практика, чем URI versioning

**Используется в:** Azure API (часто), Stripe (date-based в виде параметра).


## Q7. Content negotiation (Accept header)?

```http
GET /users/123
Accept: application/vnd.example.user.v2+json
```

**На основе MIME-типов.** Сервер возвращает представление нужной версии.

**Плюсы:**
- Механизм из стандарта HTTP
- Идентичность ресурса сохраняется

**Минусы:**
- Сложные MIME-типы
- Тяжело воспринимается людьми
- Встречается реже

**Нишевый** подход — в основном академический.


## Q8. Hostname-based?

```
https://api-v1.example.com/users/123
https://api-v2.example.com/users/123
```

**Плюсы:**
- Отдельные деплои под каждую версию
- Простое разделение инфраструктуры

**Минусы:**
- Накладные расходы на DNS
- Усложнения с CORS

**Используется:** редко, когда нужна инфраструктура под конкретную версию.


## Q9. (!) Какой подход выбрать?

**Рекомендации:**

| Сценарий | Подход |
|----------|----------|
| Публичный API | **URI versioning** (`/v1/`) — просто, наглядно |
| Внутренний API | URI или header (важна консистентность) |
| Стабильность в стиле Stripe | Date-версии через заголовок |
| GraphQL | **Без версий** (только deprecation) |
| gRPC | Версионирование пакетов (`MyServiceV2`) |

**Дефолт на 2025:** URI versioning (`/v1/`, `/v2/`) — самое прагматичное для большинства случаев.


## Q10. Semantic versioning (SemVer)?

`MAJOR.MINOR.PATCH` (e.g., `2.5.3`).

- **MAJOR** — breaking changes
- **MINOR** — новые фичи (обратно совместимые)
- **PATCH** — исправления багов

**API versioning ≠ SemVer обычно:**
- URL API: `/v1/` (только MAJOR)
- Документация отслеживает MINOR/PATCH

**SemVer для библиотек:** SemVer применим к SDK и клиентским библиотекам.


## Q11. (!) Date-based versioning (Stripe approach)?

**Stripe** использует версии по датам:

```
2024-04-19, 2024-06-30, ...
```

**Заголовок:**
```http
Stripe-Version: 2024-04-19
```

**Каждая дата** = снимок поведения API. Новые даты вводятся при breaking changes.

**Плюсы:**
- Гранулярное версионирование (любая мелочь = новая дата, если ломает совместимость)
- Нет споров про «v3» (просто даты)

**Минусы:**
- Много версий, которые приходится поддерживать
- Запутывает клиентов (какая дата самая свежая?)

**Stripe поддерживает** **все версии**, когда-либо опубликованные — клиенты прикрепляются (pin) к конкретной дате и никогда не ломаются.


## Q12. (!) Per-account version pinning?

**Модель Stripe:** каждый клиент закреплён (pinned) за конкретной версией API.

```
Account X: pinned к 2024-04-19
Account Y: pinned к 2024-06-30
Account Z: latest (auto-upgrade)
```

**Эффект:**
- Версия фиксируется при первом вызове API (или вручную)
- Аккаунт остаётся на той же версии навсегда (до явного апгрейда)
- Новые клиенты — по умолчанию на последней версии

**Webhooks** тоже версионируются по аккаунтам — формат payload вебхука стабилен для данного аккаунта.

**Итог:** Stripe может постоянно вносить breaking changes **никого при этом не ломая**.


## Q13. Webhook versioning?

**Webhooks** сложнее версионировать (сервер сам шлёт данные, согласовать версию нельзя):

**Подходы:**
1. **Версия, закреплённая за клиентом** (Stripe)
2. **Несколько вебхук-эндпоинтов** под разные версии
3. **Заголовок в payload вебхука**, указывающий версию
4. **Только обратно совместимые изменения** (всегда добавлять поля, никогда не удалять)

**Частый паттерн:** payload вебхуков заморожен навсегда, добавляются только новые типы событий.


## Q14. (!) Не versions в GraphQL?

**Философия GraphQL:** **без версионирования**.

**Вместо этого:**
- **Добавлять поля** (клиенты запрашивают только нужное — добавления ничего не ломают)
- **Помечать поля устаревшими** через `@deprecated`
- **Никогда не удалять** (или удалять очень нескоро после deprecation)

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  username: String @deprecated(reason: "Use 'handle' instead")
  handle: String!  # new field
}
```

**Клиенты** продолжают использовать `username` (работает), их подталкивают мигрировать на `handle`.

**Со временем** (через годы) — `username` удаляется (но потрясений минимум — большинство клиентов уже перешли).


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

**Инструменты:**
- IDE показывает зачёркивание на устаревших полях
- Линтеры выдают предупреждения
- В документации API отображаются пометки об устаревании


## Q16. (!) Deprecation policy?

**Политика устаревания публичного API** обычно такая:

1. **Объявить об устаревании** (release notes, email, блог)
2. **Заголовок Sunset** в ответах
3. **Период устаревания** — обычно 6–24 месяца
4. **Напоминания** клиентам
5. **Гайды по миграции** + инструменты
6. **Окончательное удаление**

**Примеры:**
- **Stripe:** никогда не удаляет (версии живут вечно)
- **GitHub:** deprecation ~12–18 месяцев
- **Twitter:** deprecation ~6 месяцев


## Q17. Sunset HTTP header?

**RFC 8594** — заголовок `Sunset` сообщает, что ресурс будет удалён.

```http
HTTP/1.1 200 OK
Sunset: Sat, 31 Dec 2025 23:59:59 GMT
Deprecation: Mon, 01 Jan 2024 00:00:00 GMT
Link: <https://api.example.com/v3/users/123>; rel="successor-version"
```

**Инструменты** могут отлавливать заголовок Sunset → уведомлять разработчиков.

**Best practice:** включать в каждый ответ для устаревших эндпоинтов.


## Q18. (!) Как gracefully deprecate API?

**Шаги:**

1. **Определить замену** (новый эндпоинт / есть новая версия)
2. **Задокументировать устаревание** (release notes, docs)
3. **Добавить заголовок Sunset** в ответы
4. **Письма потребителям API** (несколько раз)
5. **Отслеживать использование** (аналитика по устаревшему эндпоинту)
6. **Предоставить инструменты миграции** (скрипт, примеры кода)
7. **Напоминания** ближе к sunset-дате
8. **Отключить** (отдавать 410 Gone)
9. **Вычистить** серверный код (в итоге)

**Не удивляйте** пользователей. Коммуницируйте, коммуницируйте и ещё раз коммуницируйте.


## Q19. (!) Versioning best practices?

1. **По умолчанию делайте обратно совместимые изменения** (новая версия не нужна)
2. **Стратегию версионирования выбирайте рано** (менять её позже больно)
3. **URI versioning** — самое простое для большинства случаев
4. **Документируйте политику breaking changes**
5. **Анонсы Sunset** минимум за 6 месяцев
6. **Отдельная OpenAPI-спека на каждую версию**
7. **API gateway** для маршрутизации к нужной версии
8. **Тесты на всех версиях** (ловят регрессии)
9. **Ограничивайте число** поддерживаемых версий (максимум 3–4)
10. **Отслеживайте переход** на новые версии
11. **Паттерн Tolerant Reader** на стороне клиента
12. **Не версионируйте внутренние API**, если возможно (более быстрая итерация)


## Q20. Multiple versions параллельно — операционные расходы?

**Стоимость поддержки нескольких версий:**
- **Сложность кода** (ветвления под каждую версию)
- **Матрица тестирования** (проверять все версии)
- **Исправления багов** в нескольких ветках
- **Поддержка документации**
- **Сложность деплоя**
- **Накладные расходы на производительность** (слой трансляции)

**Стратегии:**
- **Трансляция в API Gateway** — в бэкенде самая свежая версия, gateway транслирует старые запросы
- **Паттерн Adapter** — разделение кода по версиям
- **Ограничивайте** число поддерживаемых версий (агрессивно выводите старые из эксплуатации)

**Подход в стиле Stripe «все версии навсегда»** — исключение, требует существенных инженерных вложений.

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


