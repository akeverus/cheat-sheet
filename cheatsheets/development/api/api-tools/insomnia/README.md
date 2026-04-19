---
title: "Insomnia"
description: "Точка входа в раздел Insomnia: лёгкий REST/GraphQL/gRPC-клиент для разработки и тестирования API."
tags:
  - meta
  - index
  - api
  - insomnia
type: "index"
updated: "2026-04-17"
---
# Insomnia

**Insomnia** — настольный клиент для ручного тестирования REST, GraphQL и gRPC. Отличается минималистичным UI, удобной работой с окружениями и плагинами. По сравнению с Postman — проще, легче, без облачной привязки (есть Git-синхронизация через «Insomnia Sync» или плагины).

Для кого: разработчики, которые хотят быстро собрать запрос, отладить auth-flow или GraphQL-запрос без тяжёлого клиента. Для коллекций и автотестов в CI чаще используется Postman/Newman или кодовые клиенты — см. соседние разделы.

## Полезные ссылки

### Основные документы
- [insomnia-basics.md](insomnia-basics.md) — основы: запросы, окружения, коллекции

### Соседние разделы
- [Родительский раздел: API Tools](../README.md)
- [Postman](../postman/README.md) — более богатый конкурент
- [API Testing](../api-testing/README.md) — виды тестов и инструменты
- [Swagger / OpenAPI](../swagger/README.md) — импорт спецификаций
- [REST API](../../rest/README.md)
- [GraphQL](../../graphql/README.md)
- [gRPC](../../grpc/README.md)

### Внешние ресурсы
- [Insomnia Documentation](https://docs.insomnia.rest/)
- [Insomnia GitHub](https://github.com/Kong/insomnia)
- [Insomnia Plugins](https://insomnia.rest/plugins)

## Содержание

- [Когда выбрать Insomnia](#когда-выбрать-insomnia)
- [Основные возможности](#основные-возможности)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда выбрать Insomnia

| Сценарий | Подходит |
|----------|----------|
| Быстрый ручной запрос, отладка ответа | Да |
| GraphQL с автодополнением по интроспекции | Да |
| gRPC-запросы к proto-файлам | Да |
| Большие коллекции и прогон в CI | Лучше Postman + Newman |
| Контрактные и нагрузочные тесты | Нет — см. `api-testing/` |
| Командная работа с облаком | Ограниченно, Postman сильнее |

Insomnia — «ежедневный нож» инженера, не замена автоматизации.

## Основные возможности

- HTTP/REST, GraphQL (с интроспекцией), gRPC, WebSocket.
- Окружения и переменные (`{{ _.base_url }}`), шаблоны.
- OAuth 2.0, AWS Signature, Bearer, Basic, Digest — без настройки скриптами.
- Импорт OpenAPI/Swagger, WSDL, Postman-коллекций.
- Плагины (`insomnia-plugin-*`) для кастомных шаблонизаторов, авто-auth, форматтеров.
- Git-синхронизация через локальный репозиторий.

## Маршруты чтения

- **Первый запрос (15 мин):** установка → создать Request → GET запрос → parse ответа.
- **Отладка auth-flow:** окружение с `base_url`, `token` → OAuth2 code flow → сохранить токен в env.
- **GraphQL-разработка:** импорт схемы → автодополнение → variables в окне Variables.

## Куда идти дальше

- Postman-альтернатива с более богатой автоматизацией — [../postman/README.md](../postman/README.md)
- Как тестировать API систематически — [../api-testing/README.md](../api-testing/README.md)
- Документирование API — [../api-documentation/README.md](../api-documentation/README.md)
- REST, GraphQL, gRPC — [../../rest/README.md](../../rest/README.md), [../../graphql/README.md](../../graphql/README.md), [../../grpc/README.md](../../grpc/README.md)
