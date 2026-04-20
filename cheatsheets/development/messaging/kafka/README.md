---
title: "Apache Kafka"
description: "Точка входа в раздел Kafka: распределённый лог для event-driven архитектур и стриминга данных."
tags:
  - meta
  - index
  - messaging
  - kafka
  - streaming
type: "index"
updated: "2026-04-20"
---
# Apache Kafka

**Apache Kafka** — распределённый лог сообщений, ставший де-факто стандартом для event-driven архитектур, потоковой обработки (Kafka Streams, Flink), интеграции микросервисов и аналитики. Ключевые отличия от классических JMS/AMQP-брокеров: партиционированный append-only лог, хранение сообщений по времени/размеру, replay по offset, очень высокий throughput.

Для кого: инженеры, проектирующие event-driven системы, CDC-пайплайны, стриминг для аналитики или межсервисные контракты на событиях. Раздел охватывает и базовые модели (producer/consumer/topic), и enterprise-темы (transactions, exactly-once, Kafka Streams, federation, tiered storage).

## Полезные ссылки

### Основные документы
- [kafka](kafka.md) — комплексное руководство: producers, consumers, Streams, Spring Kafka, кластер, мониторинг
- [kafka-advanced](kafka-advanced.md) — multi-region, tiered storage, transactional producers, Streams advanced, security

### Соседние разделы
- [Родительский раздел: Messaging](../../../basics/README.md)
- [ActiveMQ](../../../basics/README.md) — JMS-альтернатива
- [RabbitMQ](../rabbitmq/) — брокер с exchange-routing
- [NATS](../../../basics/README.md) — lightweight альтернатива
- [Event-Driven / Event Sourcing / CQRS](../../../architecture/enterprise-patterns/)
- [Spring Boot](../../../frameworks/java-frameworks/spring/) — Spring Kafka
- [Monitoring](../../../basics/README.md)

### Внешние ресурсы
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [Confluent Developer](https://developer.confluent.io/)
- [Kafka: The Definitive Guide](https://www.confluent.io/resources/kafka-the-definitive-guide/)
- [Designing Event-Driven Systems (Ben Stopford)](https://www.oreilly.com/library/view/designing-event-driven-systems/9781492038252/)
- [AKHQ — Kafka UI](https://akhq.io/)

## Содержание

- [Когда выбирать Kafka](#когда-выбирать-kafka)
- [Kafka vs ActiveMQ vs RabbitMQ vs NATS](#kafka-vs-activemq-vs-rabbitmq-vs-nats)
- [Карта тем](#карта-тем)
- [Production-каталог](#production-каталог)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда выбирать Kafka

Подходит:
- event-driven architecture и event sourcing;
- интеграция микросервисов через события, CDC (Debezium);
- потоковая обработка и аналитика (Kafka Streams, Flink, Spark);
- требования к replay сообщений, долгому хранению, высокому throughput (десятки-сотни тысяч msg/s на broker);
- log aggregation, metrics, audit-лог.

Не подходит:
- простой request-reply между двумя сервисами — overhead не оправдан;
- JMS-транзакции и XA — Kafka их не реализует в классическом виде (есть transactional producers, но модель другая);
- миллионы коротких очередей с per-message acknowledgment — RabbitMQ удобнее;
- очень маленькие приложения с минимальным footprint — NATS.

## Kafka vs ActiveMQ vs RabbitMQ vs NATS

| Критерий | Kafka | ActiveMQ | RabbitMQ | NATS |
|----------|-------|----------|----------|------|
| Модель | Partitioned log | JMS Queue/Topic | Exchange Queue | Subject / JetStream |
| Throughput | Очень высокий (100k-1M+/брокер) | Средний | Средний | Очень высокий |
| Latency | Низкая-средняя (ms) | Средняя | Низкая | Sub-ms |
| Хранение | Дни/недели/годы (tiered) | До потребления | До потребления | JetStream — persist |
| Replay | Да (offset) | Нет | Нет | JetStream — да |
| Ordering | Per-partition (строгий) | Per-queue | Per-queue | Subject order |
| Routing | Тема + ключ партиция | Селекторы JMS | Мощный routing (topic, direct, fanout) | Wildcard subjects |
| Транзакции | Idempotent + transactional producer | JMS XA | Publisher confirms | JetStream ack |
| Ops-сложность | Высокая (Zookeeper/KRaft, брокеры, репликация) | Средняя | Средняя | Низкая |
| Типовой кейс | Events, streaming, analytics | Enterprise JMS, request-reply | Сложный routing, RPC | IoT, microservices, low-latency |

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Топики, партиции, replication, leader | [kafka](kafka.md) |
| Producer (acks, idempotent, transactional) | [kafka](kafka.md), [kafka-advanced](kafka-advanced.md#idempotent-и-transactional-producers) |
| Consumer groups, rebalancing, offsets | [kafka](kafka.md), [kafka-advanced](kafka-advanced.md#consumer-groups-и-rebalancing) |
| Spring Kafka (`@KafkaListener`) | [kafka](kafka.md) |
| Kafka Streams: KStream, KTable, join, window | [kafka](kafka.md), [kafka-advanced](kafka-advanced.md#kafka-streams) |
| Schema Registry, Avro/Protobuf | [kafka-advanced](kafka-advanced.md) |
| Security: TLS, SASL, ACL | [kafka-advanced](kafka-advanced.md) |
| Multi-region, MirrorMaker 2, tiered storage | [kafka-advanced](kafka-advanced.md#multi-region-deployment) |
| Мониторинг (lag, Burrow, AKHQ) | [kafka](kafka.md) |

## Production-каталог

- [ ] Partition count проработан (по throughput и параллелизму)
- [ ] `acks=all`, `min.insync.replicas>=2`, replication `>=3`
- [ ] Idempotent producer включён; transactional где нужна exactly-once
- [ ] Consumer: ручной commit после обработки (or EOS semantics)
- [ ] DLT (Dead Letter Topic) для poison messages; retry-топики
- [ ] Schema Registry + контракт на события (Avro/Proto, backward-compat)
- [ ] Мониторинг consumer lag (Burrow, JMX, Prometheus)
- [ ] Security: TLS, SASL/SCRAM или mTLS, ACL по топикам
- [ ] Retention и compaction заданы осознанно (time/size/log compaction)
- [ ] KRaft (или Zookeeper) HA проверен на failover

## Маршруты чтения

- **Первый producer/consumer (1 день):** `kafka.md` — основы + Spring Kafka простой producer и `@KafkaListener`.
- **Event-driven миграция с REST:** `kafka.md` — топики как контракт schema registry idempotent producer DLT.
- **Streaming / analytics:** Kafka Streams или Flink state stores exactly-once `kafka-advanced.md`.
- **Production hardening:** security lag monitoring multi-region (MirrorMaker 2) tiered storage.

## Куда идти дальше

- Event-driven архитектура и паттерны — [../../../architecture/enterprise-patterns/](../../../architecture/enterprise-patterns/)
- ActiveMQ для JMS-сценариев — [README](../../../basics/README.md)
- NATS для низколатентной связи — [README](../../../basics/README.md)
- Мониторинг (lag, throughput, SLO) — [README](../../../basics/README.md)
