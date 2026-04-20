---
title: "gRPC"
description: "Точка входа в раздел gRPC: Protocol Buffers, HTTP/2, стриминг, Spring Boot интеграция."
tags:
  - meta
  - index
  - api
  - grpc
type: "index"
updated: "2026-04-17"
---
# gRPC

**gRPC** — высокопроизводительный RPC-фреймворк поверх HTTP/2 с контрактом в Protocol Buffers и поддержкой четырёх видов стриминга (unary, server, client, bidirectional). Основная ниша — service-to-service коммуникация внутри бэкенда, где важны низкая латентность, строгий контракт и бинарная эффективность.

Для кого: инженеры, которые строят межсервисную коммуникацию и выбирают между REST, GraphQL и gRPC. Раздел описывает модель gRPC, его плюсы/минусы относительно REST и показывает интеграцию со Spring Boot.

## Полезные ссылки

### Основные документы
- [[grpc]] — полное руководство: Protocol Buffers, виды сервисов, Spring Boot интеграция

### Соседние разделы
- [[README|Родительский раздел: API]]
- [[README|REST API]] — альтернатива для внешних/публичных API
- [[README|GraphQL]] — альтернатива для разнородных клиентов
- [[README|Messaging]] — асинхронная альтернатива RPC
- [Spring Boot](../../../frameworks/java-frameworks/spring/)

### Внешние ресурсы
- [gRPC Documentation](https://grpc.io/docs/)
- [Protocol Buffers](https://protobuf.dev/)
- [gRPC Java](https://grpc.io/docs/languages/java/)
- [grpc-spring-boot-starter (LogNet)](https://github.com/LogNet/grpc-spring-boot-starter)
- [grpcurl](https://github.com/fullstorydev/grpcurl) — CLI для ручных запросов

## Содержание

- [REST vs gRPC vs GraphQL](#rest-vs-grpc-vs-graphql)
- [Когда выбирать gRPC](#когда-выбирать-grpc)
- [Карта тем](#карта-тем)
- [Подводные камни](#подводные-камни)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## REST vs gRPC vs GraphQL

| Критерий | REST | gRPC | GraphQL |
|----------|------|------|---------|
| Протокол | HTTP/1.1+ | HTTP/2 (HTTP/3 в roadmap) | HTTP (обычно 1.1) |
| Формат | JSON | Protocol Buffers (бинарь) | JSON |
| Контракт | OpenAPI (опционально) | .proto обязательно | SDL обязательно |
| Типизация | Слабая | Строгая, кодогенерация | Строгая |
| Стриминг | SSE, WebSocket | Встроенный 4 вида | Subscriptions (WS) |
| Браузер напрямую | Да | Нужен gRPC-Web | Да |
| Производительность | Средняя | Высокая (бинарь + HTTP/2) | Средняя |
| Debug без инструментов | curl | Нужен grpcurl | curl с GraphQL-query |
| Типичный кейс | Публичное API, CRUD | Service-to-service | BFF, разнородные клиенты |

## Когда выбирать gRPC

Подходит:
- межсервисная коммуникация в микросервисах с высокой нагрузкой и жёсткими SLA на latency;
- стриминг (server-streaming — подписка на обновления, bidirectional — чаты, realtime-обработка);
- язык-нейтральные контракты: proto → клиенты на Java, Go, Python, C++ без расхождений;
- мобильные клиенты, где важен размер payload и батарея.

Не подходит:
- публичные API для сторонних разработчиков — REST проще в освоении;
- браузер напрямую — нужно поднимать gRPC-Web прокси (Envoy);
- команды без опыта protobuf — долгая кривая обучения.

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Protocol Buffers: сообщения, сервисы, кодогенерация | [[grpc#protocol-buffers]] |
| 4 вида RPC (unary, server-stream, client-stream, bidi) | [[grpc#типы-сервисов]] |
| Spring Boot интеграция | [[grpc]] |
| Interceptors, metadata, deadline, retries | [[grpc]] |
| TLS, auth, mTLS | [[grpc]] |
| gRPC-Web для браузера | [[grpc]] |

## Подводные камни

- **Breaking changes в proto.** Удаление поля или смена тега ломает клиентов. Правила: не переиспользовать номера, пометить `reserved`, добавлять поля через новые теги.
- **Debug сложнее, чем REST.** Нужен `grpcurl` или BloomRPC; логи часто бинарные.
- **Network-friendly балансировка.** Стандартный L4 LB не видит gRPC-стримов; нужен gRPC-aware LB (Envoy) или клиентская балансировка.
- **Deadlines.** Без deadline клиент может висеть вечно; задавать на каждый вызов.

## Маршруты чтения

- **Первый сервис (1 день):** `grpc.md` — proto → кодогенерация → unary service → клиент.
- **Production-ready:** interceptors (logging, metrics, auth) → TLS → deadline/retries → gRPC-aware LB.
- **Streaming-use case:** выбрать тип (server/bidi) → backpressure → тайм-ауты.

## Куда идти дальше

- REST для внешних API — [[README]]
- GraphQL для разнородных клиентов — [[README]]
- Асинхронная интеграция через брокеры — [[README]]
- Observability и мониторинг — [[README]]
