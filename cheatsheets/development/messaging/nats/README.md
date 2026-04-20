---
title: "NATS"
description: "Точка входа в раздел NATS: облачно-нативная low-latency messaging-система с JetStream для персистентности."
tags:
  - meta
  - index
  - messaging
  - nats
type: "index"
updated: "2026-04-20"
---
# NATS

**NATS** — высокопроизводительная, облачно-нативная система обмена сообщениями с минимальным footprint (бинарь ~15 MB, sub-ms latency). Ядро — lightweight pub/sub по subject-ам с wildcards; слой **JetStream** добавляет persistence, acknowledgments, replay, key-value и object store. NATS хорошо подходит для микросервисов, edge и IoT-сценариев.

Для кого: инженеры, выбирающие лёгкий брокер для микросервисов или low-latency задач и не готовые тянуть вес Kafka/ActiveMQ. Раздел помогает понять, когда NATS — правильный выбор, а когда стоит остаться на Kafka или RabbitMQ.

## Полезные ссылки

### Основные документы
- [nats](nats.md) — архитектура, Core pub-sub, JetStream, KV/Object store, Java-клиент, мониторинг

### Соседние разделы
- [Родительский раздел: Messaging](../../../basics/README.md)
- [Kafka](../../../basics/README.md) — альтернатива для event log / streaming
- [ActiveMQ](../../../basics/README.md) — JMS-альтернатива
- [RabbitMQ](../rabbitmq/) — классический AMQP-брокер
- [Spring Boot](../../../frameworks/java-frameworks/spring/)
- [Microservices](../../../basics/README.md)

### Внешние ресурсы
- [NATS Documentation](https://docs.nats.io/)
- [NATS JetStream](https://docs.nats.io/nats-concepts/jetstream)
- [NATS by Example](https://docs.nats.io/developing-with-nats/by_example)
- [nats.java (GitHub)](https://github.com/nats-io/nats.java)
- [Synadia (коммерческая поддержка)](https://www.synadia.com/)

## Содержание

- [Когда выбирать NATS](#когда-выбирать-nats)
- [NATS vs Kafka vs RabbitMQ vs ActiveMQ](#nats-vs-kafka-vs-rabbitmq-vs-activemq)
- [Карта тем](#карта-тем)
- [JetStream: когда он нужен](#jetstream-когда-он-нужен)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда выбирать NATS

Подходит:
- service-to-service коммуникация в микросервисах с требованием к низкой латентности;
- request-reply паттерн (встроен в ядро, в отличие от Kafka);
- IoT / edge-сценарии: маленький бинарь, низкий расход памяти;
- pub/sub с wildcards (`orders.*.created`, `sensors.>`);
- Key-Value / Object Store поверх JetStream как замена Redis/S3 для лёгких кейсов.

Не подходит:
- event sourcing со сложным replay на годы — Kafka лучше;
- тяжёлый streaming/analytics (Kafka Streams, Flink) — Kafka;
- JMS/XA-транзакции — ActiveMQ;
- сложный exchange/binding-routing (AMQP-style) — RabbitMQ.

## NATS vs Kafka vs RabbitMQ vs ActiveMQ

| Критерий | NATS | Kafka | RabbitMQ | ActiveMQ |
|----------|------|-------|----------|----------|
| Модель | Subject pub-sub + JetStream | Partitioned log | Exchange Queue | JMS Queue/Topic |
| Latency | Sub-ms (самый быстрый) | Низкая-средняя | Низкая | Средняя |
| Footprint | ~15 MB бинарь, минимум ресурсов | Большой (JVM, brokers) | Средний (Erlang VM) | Средний (JVM) |
| Persistence | JetStream (отдельный слой) | Встроенный append-log | Встроенный | Встроенный |
| Replay | JetStream — да | Да (offset) | Нет | Нет |
| Routing | Wildcard subjects | Partition key | Sophisticated routing | JMS селекторы |
| Request-Reply | Встроенный | Нет native | Через RPC-паттерн | Встроенный |
| Operations | Очень простые | Сложные | Средние | Средние |
| Типовой кейс | Microservices, IoT, low-latency | Event log, streaming, analytics | Сложный routing, task queues | Enterprise JMS |

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Архитектура кластера, самотчистый протокол | [nats](nats.md#архитектура) |
| Core Pub/Sub и Request-Reply | [nats](nats.md#core-messaging-patterns) |
| JetStream: streams, consumers, ack, replay | [nats](nats.md#jetstream-persistent-messaging) |
| Key-Value Store | [nats](nats.md#key-value-store) |
| Object Store | [nats](nats.md#object-store) |
| Java-клиент | [nats](nats.md#java-клиент) |
| Monitoring / Observability | [nats](nats.md#monitoring-и-observability) |
| Service Mesh интеграция | [nats](nats.md#service-mesh-integration) |

## JetStream: когда он нужен

Core NATS — «fire and forget» (at-most-once): нет подтверждений, сообщение может быть потеряно, если подписчик offline. JetStream превращает NATS в полноценный брокер с гарантиями:

- at-least-once и exactly-once-подобные семантики (deduplication);
- ack/nack с retry и max deliver;
- replay по последовательному номеру или времени;
- KV / Object Store поверх того же механизма streams.

Если приложение не терпит потерю сообщений — всегда JetStream. Для metrics-event-shower или ephemeral-уведомлений достаточно Core NATS.

## Маршруты чтения

- **Первое приложение (1 день):** `nats.md` — pub/sub и request-reply Java-клиент локальный `nats-server`.
- **Переход от потерь к гарантиям:** Core JetStream durable consumer с ack.
- **Замена Redis для KV:** NATS KV сравнить SLA, latency, persistence vs Redis — принять решение по нагрузке.

## Куда идти дальше

- Kafka для event log и streaming — [README](../../../basics/README.md)
- RabbitMQ для гибкого routing — [../rabbitmq/](../rabbitmq/)
- ActiveMQ для JMS-сценариев — [README](../../../basics/README.md)
- Микросервисная архитектура — [README](../../../basics/README.md)
