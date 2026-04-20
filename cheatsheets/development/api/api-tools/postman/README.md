---
title: "Postman"
description: "Точка входа в раздел Postman: коллекции, окружения, автоматизация через Newman и контрактные тесты."
tags:
  - meta
  - index
  - api
  - postman
type: "index"
updated: "2026-04-20"
---
# Postman

**Postman** — самый распространённый клиент для разработки, тестирования и документирования API. Силён командными фичами: общие воркспейсы, коллекции, моки, мониторы. Для автоматизации в CI есть CLI-раннер Newman. Postman становится полноценным инструментом API-lifecycle, а не просто клиентом запросов.

Для кого: инженеры, которые собирают коллекции запросов, тесты и моки и хотят использовать их и в разработке, и в CI. Для одиночных быстрых запросов часто легче Insomnia; для контрактных тестов по OpenAPI — Schemathesis/Dredd.

## Полезные ссылки

### Основные документы
- [[postman-basics]] — запросы, коллекции, переменные, автоматизация

### Соседние разделы
- [[README|Родительский раздел: API Tools]]
- [[README|Insomnia]] — лёгкая альтернатива
- [[README|API Testing]] — виды тестов и место Postman в них
- [[README|Swagger / OpenAPI]] — импорт/экспорт спецификаций
- [[README|API Documentation]]
- [[README|REST API]]

### Внешние ресурсы
- [Postman Learning Center](https://learning.postman.com/docs/getting-started/introduction/)
- [Newman (Postman CLI)](https://github.com/postmanlabs/newman)
- [Postman API](https://documenter.getpostman.com/view/631643/JsLs9mWL)
- [Chai Assertion Library (in Postman tests)](https://www.chaijs.com/)

## Содержание

- [Когда использовать Postman](#когда-использовать-postman)
- [Ключевые возможности](#ключевые-возможности)
- [Автоматизация и CI через Newman](#автоматизация-и-ci-через-newman)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда использовать Postman

| Задача | Подходит |
|--------|----------|
| Ручной запрос и отладка | Да (можно и Insomnia) |
| Коллекции сценариев для команды | Да |
| Тесты ответов на JS (pre-request + test scripts) | Да |
| Прогон коллекций в CI через Newman | Да |
| Mock-сервер по коллекции | Да |
| Мониторинг endpoint-ов из облака | Да (платно) |
| Contract-тесты strictly по OpenAPI | Лучше Schemathesis/Pact |
| Нагрузочное тестирование | Нет — k6/Gatling |

## Ключевые возможности

- **Коллекции.** Дерево запросов со сценариями, переменными и тестами.
- **Окружения.** `{{base_url}}`, `{{token}}`, разные для dev/stage/prod.
- **Pre-request и test scripts.** JS-код на `pm.*` API (assertions, работа с переменными, chain-запросы).
- **Mock servers.** Публикация коллекции как мока для фронтенда.
- **Моniторы.** Плановый запуск коллекции, уведомления при падении.
- **Runner / Newman.** Прогон коллекции локально или в CI, репорты junit/html.
- **API Builder.** OpenAPI/GraphQL-спецификации, линк с коллекциями.

## Автоматизация и CI через Newman

```bash
# Запуск коллекции с окружением и junit-репортом
newman run api.postman_collection.json \
  -e staging.postman_environment.json \
  -r cli,junit --reporter-junit-export results.xml
```

Типичная интеграция: экспорт коллекций и окружений в git прогон Newman в PR-пайплайне падение PR при провале теста.

## Маршруты чтения

- **Первое использование (30 мин):** установка коллекция окружение 5 запросов с тестами.
- **Командный процесс:** workspace shared окружения секреты через variables типа secret Newman в CI.
- **Mock-first разработка:** коллекция с примерами Mock server фронтенд работает параллельно с бэком.

## Куда идти дальше

- Insomnia как альтернатива — [[README]]
- Систематическое тестирование API — [[README]]
- OpenAPI и импорт спецификаций — [[README]]
- REST API design — [[README]]
