---
title: "Swagger / OpenAPI"
description: "Точка входа в раздел OpenAPI/Swagger: спецификация, инструменты, lifecycle API-документации."
tags:
  - meta
  - index
  - api
  - openapi
  - swagger
type: "index"
updated: "2026-04-20"
---
# Swagger / OpenAPI

**OpenAPI Specification (OAS)** — стандарт описания REST API в машиночитаемом виде (YAML/JSON). Исторически известна как Swagger. Из одной спецификации генерируются клиенты, серверы, мок-серверы, визуальная документация, контракт-тесты. Swagger — это экосистема инструментов (UI, Editor, Codegen) вокруг OAS.

Для кого: разработчики, которые хотят сделать OpenAPI единым источником истины для API-контракта, наладить автогенерацию документации и контракт-тесты. Раздел обобщает практики и даёт полный референс по структуре спеки.

## Полезные ссылки

### Основные документы
- [openapi-swagger](openapi-swagger.md) — полный референс OpenAPI 3.x: структура, объекты, security
- [api-documentation-basics](api-documentation-basics.md) — практика: lifecycle, versioning, CI-валидация

### Соседние разделы
- [Родительский раздел: API Tools](../../../../basics/README.md)
- [API Documentation](../../../../basics/README.md) — общая точка входа в документирование
- [API Testing](../../../../basics/README.md) — контрактные и интеграционные тесты
- [Postman](../../../../basics/README.md) — импорт спецификации в коллекцию
- [REST API](../../../../basics/README.md) — дизайн API, который описываем
- [Spring Boot](../../../../frameworks/java-frameworks/spring/) — springdoc-openapi для Spring

### Внешние ресурсы
- [OpenAPI Specification (spec.openapis.org)](https://spec.openapis.org/oas/latest.html)
- [Swagger Tools](https://swagger.io/tools/)
- [Swagger Editor](https://editor.swagger.io/)
- [OpenAPI Generator](https://openapi-generator.tech/)
- [Redoc](https://redocly.com/docs/redoc/)
- [Spectral Linter](https://stoplight.io/open-source/spectral)
- [springdoc-openapi](https://springdoc.org/)

## Содержание

- [Что даёт OpenAPI](#что-даёт-openapi)
- [Ключевые инструменты экосистемы](#ключевые-инструменты-экосистемы)
- [Lifecycle API-контракта](#lifecycle-api-контракта)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что даёт OpenAPI

- **Единое описание контракта.** Путь, метод, параметры, схемы request/response, коды, security.
- **Кодогенерация.** Клиенты (Java, TypeScript, Go) и серверные стабы из одной спеки.
- **Автодокументация.** Swagger UI и Redoc рендерят человекочитаемую страницу.
- **Контракт-тесты.** Schemathesis и Dredd проверяют, что API реально соответствует спеке.
- **Mock-сервер.** Prism, Stoplight и OpenAPI Generator поднимают мок по спеке.
- **Версионирование.** Diff между версиями OAS детекция breaking changes.

## Ключевые инструменты экосистемы

| Инструмент | Роль |
|------------|------|
| Swagger Editor | Редактирование OAS с live-превью и валидацией |
| Swagger UI | Интерактивная документация и «try it out» |
| Redoc | Красивая статичная документация для внешних потребителей |
| OpenAPI Generator | Кодогенерация клиентов и серверов (60+ языков) |
| Spectral | Линтер стиля и полноты OAS |
| springdoc-openapi | Автогенерация OAS из кода Spring Boot |
| Prism | Mock-сервер из OAS |
| Schemathesis | Property-based contract-тесты |

## Lifecycle API-контракта

1. **Design-first** или **code-first**: YAML-файл в репо либо генерация из кода (springdoc).
2. Review OAS в PR (линт Spectral).
3. Diff против `main` — обнаружение breaking changes.
4. Генерация клиентов/документации в CI.
5. Публикация: Swagger UI для dev, Redoc/портал для продуктовых потребителей.
6. При релизе — семантическое версионирование и changelog.

## Маршруты чтения

- **Первое знакомство (1 ч):** `api-documentation-basics.md` `openapi-swagger.md` — структура и Info/Paths.
- **Встраивание в Spring Boot:** `springdoc-openapi` Swagger UI ручной тюнинг аннотаций.
- **Строгий contract-first:** OAS в репо Spectral diff в CI Schemathesis кодогенерация клиентов.

## Куда идти дальше

- Тестирование API по OpenAPI — [README](../../../../basics/README.md)
- Процесс и практика документации — [README](../../../../basics/README.md)
- REST API design — [README](../../../../basics/README.md)
- GraphQL как альтернативная модель контракта — [README](../../../../basics/README.md)
