---
title: "API Documentation"
description: "Точка входа в раздел документации API: OpenAPI как единый источник истины, инструменты и процесс."
tags:
  - meta
  - index
  - api
  - documentation
type: "index"
updated: "2026-04-17"
---
# API Documentation

API-документация — это контракт между командами, а не постскриптум к коду. Хорошая документация делает интеграцию предсказуемой (клиент знает формат, коды ошибок, пагинацию, rate-limits), плохая — превращает API в «угадай-что-отвечает». Раздел собирает инструменты и практики, которые позволяют держать документацию автоматически синхронной с кодом.

Для кого: инженеры, которые публикуют или потребляют внутренние/внешние API и хотят поставить lifecycle документации на поток — от OpenAPI-спеки в репозитории до визуальной версии в CI и contract-тестов.

## Полезные ссылки

### Основные документы в соседней папке
- [api-documentation-basics.md](../swagger/api-documentation-basics.md) — практика: lifecycle, versioning, CI-валидация
- [openapi-swagger.md](../swagger/openapi-swagger.md) — полный референс OpenAPI / Swagger

### Соседние разделы
- [Родительский раздел: API Tools](../README.md)
- [API Testing](../api-testing/README.md) — контрактные и интеграционные тесты
- [Swagger / OpenAPI](../swagger/README.md)
- [Postman](../postman/README.md) — сохранение запросов и примеров
- [REST API](../../rest/README.md) — проектирование API, которое документируем
- [GraphQL](../../graphql/README.md) — альтернативный подход к контракту (schema-first)
- [gRPC](../../grpc/README.md) — proto как источник контракта

### Внешние ресурсы
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html)
- [Redocly OpenAPI Visual Reference](https://redocly.com/docs/openapi-visual-reference/)
- [Stoplight Spectral](https://stoplight.io/open-source/spectral) — линтер OpenAPI
- [Google API Design Guide](https://cloud.google.com/apis/design)
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)

## Содержание

- [Что обязательно должно быть в API-документации](#что-обязательно-должно-быть-в-api-документации)
- [Инструменты](#инструменты)
- [Lifecycle и versioning](#lifecycle-и-versioning)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что обязательно должно быть в API-документации

- Базовый URL, схема аутентификации, rate-limits.
- Для каждого endpoint: метод, путь, параметры, request-body, все коды ответа (включая ошибки) с примерами.
- Форматы ошибок (RFC 7807 Problem Details — хороший дефолт).
- Pagination, sorting, filtering — их конвенции и лимиты.
- Idempotency, версионирование, deprecation-политика.
- Changelog и migration guides между версиями.

## Инструменты

| Инструмент | Роль |
|------------|------|
| OpenAPI (OAS 3.x) | Описание контракта в YAML/JSON |
| Swagger UI / Redoc | Визуализация спецификации для разработчиков |
| Swagger Editor | Редактирование с live-превью |
| Spectral | Линтер на стиль и полноту спецификации |
| OpenAPI Generator | Кодогенерация клиентов/серверов |
| springdoc-openapi | Автогенерация OAS из Spring Boot |
| Schemathesis / Dredd | Contract-тестирование против работающего API |

## Lifecycle и versioning

Минимальный lifecycle документации в CI:

1. Спецификация в репозитории (`api/openapi.yaml`), review вместе с кодом.
2. Линт Spectral + diff против предыдущей версии (detect breaking changes).
3. Автогенерация визуальной документации в pipeline.
4. Contract-тесты в PR: реальный API должен отвечать по спеке.
5. Publish в портал для потребителей (Confluence, Backstage, Redocly).

Версионирование: семантическое (MAJOR.MINOR.PATCH), breaking changes → новый MAJOR (`/v2`), backward-compatible → MINOR. Deprecation минимум 6 месяцев с `Sunset`-заголовком.

## Маршруты чтения

- **Первый API с документацией:** OpenAPI по шаблону → springdoc → Swagger UI → линт Spectral.
- **Зрелый lifecycle:** добавить diff против прошлой версии, contract-тесты, портал для потребителей.
- **Аудит legacy:** сгенерировать OAS из реального трафика (mitm) → привести в соответствие → интегрировать в CI.

## Куда идти дальше

- Практика работы с Swagger/OpenAPI — [../swagger/README.md](../swagger/README.md)
- Тестирование API по контракту — [../api-testing/README.md](../api-testing/README.md)
- Проектирование REST API — [../../rest/README.md](../../rest/README.md)
- GraphQL-схема как контракт — [../../graphql/README.md](../../graphql/README.md)
