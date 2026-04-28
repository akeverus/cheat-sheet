---
title: "GraphQL"
description: "Точка входа в раздел GraphQL: схема, резолверы, Spring GraphQL, подписки, федерация."
tags:
  - meta
  - index
  - api
  - graphql
type: "index"
aliases:
  - "GraphQL"
prerequisites: []
next: []
updated: "2026-04-20"
---
# GraphQL

**GraphQL** — язык запросов и рантайм для API, где клиент описывает, какие данные ему нужны, а сервер отвечает ровно этим набором. Один endpoint (обычно `/graphql`), строгая схема, отсутствие over-fetching и under-fetching. Подходит для сложных доменных моделей с разнородными потребителями (веб, мобайл, внешние клиенты).

Для кого: инженеры, которые выбирают между REST/GraphQL/gRPC и хотят понять, когда и зачем тянуть GraphQL в стек. Раздел покрывает серверную и клиентскую сторону, Spring GraphQL как основную реализацию и паттерны (DataLoader, subscriptions, federation).

## Полезные ссылки

### Основные документы
- [graphql](graphql.md) — комплексное руководство: SDL, резолверы, Spring GraphQL, Apollo, subscriptions, federation

### Соседние разделы
- [Родительский раздел: API](../../../basics/README.md)
- [REST API](../../../basics/README.md) — ключевая альтернатива
- [gRPC](../../../basics/README.md) — альтернатива для service-to-service
- [API Tools](../../../basics/README.md) — Insomnia, Postman с поддержкой GraphQL
- [Spring Boot](../../../frameworks/java-frameworks/spring/) — Spring GraphQL
- [Web Backend](../../../basics/README.md)

### Внешние ресурсы
- [GraphQL Specification](https://spec.graphql.org/)
- [GraphQL Foundation](https://graphql.org/)
- [GraphQL Java](https://www.graphql-java.com/)
- [Spring GraphQL](https://spring.io/projects/spring-graphql)
- [Apollo Client](https://www.apollographql.com/docs/react/)
- [Netflix DGS Framework](https://netflix.github.io/dgs/)

## Содержание

- [REST vs GraphQL vs gRPC](#rest-vs-graphql-vs-grpc)
- [Когда выбирать GraphQL](#когда-выбирать-graphql)
- [Карта тем](#карта-тем)
- [Типичные подводные камни](#типичные-подводные-камни)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## REST vs GraphQL vs gRPC

| Критерий | REST | GraphQL | gRPC |
|----------|------|---------|------|
| Транспорт | HTTP/1.1+ | HTTP (обычно POST) | HTTP/2 |
| Формат | JSON (чаще всего) | JSON запрос/ответ, SDL-схема | Protocol Buffers (бинарь) |
| Схема / контракт | OpenAPI (опционально) | Обязательная SDL | Обязательный .proto |
| Over/Under-fetching | Часто встречается | Минимальны | Контролируется вручную |
| Кэширование на HTTP | Лёгкое (GET + Cache-Control) | Тяжёлое (POST) | Нет кэша на HTTP |
| Стриминг | SSE / WebSocket | Subscriptions (WS) | Встроенный стриминг |
| Инструменты | Swagger, Postman | GraphiQL, Apollo Studio | grpcurl, BloomRPC |
| Сложность | Низкая | Средняя | Средняя-высокая |
| Типичные кейсы | Публичные API, CRUD | BFF, разнородные клиенты | Service-to-service, низкая латентность |

## Когда выбирать GraphQL

Подходит:
- разные клиенты (web, iOS, Android) требуют разные представления одних и тех же данных;
- данные собираются из нескольких источников (БД, микросервисы, внешние API) — клиенту проще один запрос;
- BFF-слой, где фронт хочет управлять формой ответа.

Не подходит:
- простые CRUD с одним клиентом — REST проще и дешевле;
- service-to-service с высокой нагрузкой и низкой латентностью — gRPC;
- файловые аплоады, бинарные стримы — REST/gRPC.

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Schema Definition Language (SDL), типы, queries, mutations | [graphql](graphql.md#schema-definition-language-sdl) |
| Резолверы и data fetching | [graphql](graphql.md) |
| Spring GraphQL | [graphql](graphql.md#spring-boot-интеграция) |
| DataLoader и решение N+1 | [graphql](graphql.md) |
| Subscriptions (реактивные подписки) | [graphql](graphql.md#subscriptions) |
| Federation (Apollo Federation, DGS) | [graphql](graphql.md#federation) |
| Security: depth/complexity limit | [graphql](graphql.md) |

## Типичные подводные камни

- **N+1 запросы.** Каждый резолвер делает отдельный вызов в БД — решается DataLoader-ом (батчинг по id).
- **Запросы любой глубины.** DoS через вложенные `friends.friends.friends`. Защита: query depth + complexity analysis.
- **Кэш на HTTP.** GraphQL обычно POST нет HTTP-кэша. Использовать APQ (Automatic Persisted Queries) + CDN.
- **Версионирование.** В GraphQL нет `/v2`: эволюция через добавление полей и `@deprecated`.
- **Observability.** Нужно метрировать на уровне полей/резолверов, не только HTTP.

## Маршруты чтения

- **Первое знакомство (1 день):** `graphql.md` разделы SDL резолверы Spring GraphQL DataLoader.
- **Миграция с REST:** построить GraphQL поверх REST через резолверы постепенно переносить клиентов.
- **Federation / микросервисы:** Apollo Federation или Netflix DGS subgraphs единый gateway.

## Куда идти дальше

- REST как базовый стиль API — [README](../../../basics/README.md)
- gRPC для service-to-service — [README](../../../basics/README.md)
- Документирование контракта — [README](../../../basics/README.md)
- Тестирование API — [README](../../../basics/README.md)
