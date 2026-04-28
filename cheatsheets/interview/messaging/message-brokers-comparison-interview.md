---
title: "Вопросы на собеседовании: Сравнение Message Brokers"
description: "Comparison Apache Kafka, RabbitMQ, NATS, Apache Pulsar, Redpanda, AWS SQS/SNS, EventBridge, ActiveMQ. Когда что выбрать, throughput, latency, ordering, persistence, ops complexity"
tags:
  - interview
  - messaging
  - message-brokers-comparison-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Сравнение Message Brokers"
  - "Kafka vs RabbitMQ vs NATS"
  - "Choose message broker"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Сравнение Message Brokers`

Обзор и сравнение основных messaging systems: **Apache Kafka, RabbitMQ, NATS, Apache Pulsar, Redpanda, AWS SQS/SNS/EventBridge, Apache ActiveMQ**. Часто на интервью спрашивают: "когда выбрать что и почему". Cheatsheet для quick decisions + deep сравнения.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Kafka vs RabbitMQ — Confluent](https://www.confluent.io/learn/rabbitmq-vs-apache-kafka/)
- [NATS vs Kafka vs RabbitMQ](https://nats.io/)
- [Designing Data-Intensive Applications (Kleppmann) — Chapter 11](https://dataintensive.net/)
- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовая классификация**
- [Q1. (!) Pub/Sub vs Queue vs Stream — отличия?](#q1--pubsub-vs-queue-vs-stream--отличия)
- [Q2. (!) Message broker vs streaming platform?](#q2--message-broker-vs-streaming-platform)

**Главные системы**
- [Q3. (!) Apache Kafka — характеристики?](#q3--apache-kafka--характеристики)
- [Q4. (!) RabbitMQ — характеристики?](#q4--rabbitmq--характеристики)
- [Q5. (!) NATS — характеристики?](#q5--nats--характеристики)
- [Q6. (!) Apache Pulsar — характеристики?](#q6--apache-pulsar--характеристики)
- [Q7. (!) Redpanda — характеристики?](#q7--redpanda--характеристики)
- [Q8. AWS SQS/SNS/EventBridge?](#q8-aws-sqssnseventbridge)
- [Q9. Apache ActiveMQ?](#q9-apache-activemq)

**Сравнения по критериям**
- [Q10. (!) Throughput comparison?](#q10--throughput-comparison)
- [Q11. (!) Latency comparison?](#q11--latency-comparison)
- [Q12. (!) Ordering guarantees?](#q12--ordering-guarantees)
- [Q13. (!) Delivery semantics (at-least-once, exactly-once)?](#q13--delivery-semantics-at-least-once-exactly-once)
- [Q14. Persistence?](#q14-persistence)
- [Q15. Replay (consume historical messages)?](#q15-replay-consume-historical-messages)
- [Q16. Operational complexity?](#q16-operational-complexity)
- [Q17. Multi-region?](#q17-multi-region)

**Decision guidelines**
- [Q18. (!) Когда выбрать Kafka?](#q18--когда-выбрать-kafka)
- [Q19. (!) Когда выбрать RabbitMQ?](#q19--когда-выбрать-rabbitmq)
- [Q20. (!) Когда выбрать NATS?](#q20--когда-выбрать-nats)
- [Q21. (!) Когда выбрать Pulsar?](#q21--когда-выбрать-pulsar)
- [Q22. (!) Когда выбрать SQS/EventBridge?](#q22--когда-выбрать-sqseventbridge)

**Patterns**
- [Q23. (!) Use case: order processing pipeline?](#q23--use-case-order-processing-pipeline)
- [Q24. Use case: notification system?](#q24-use-case-notification-system)
- [Q25. Use case: clickstream analytics?](#q25-use-case-clickstream-analytics)
- [Q26. Use case: IoT с millions devices?](#q26-use-case-iot-с-millions-devices)

## Q1. (!) Pub/Sub vs Queue vs Stream — отличия?

**Queue (point-to-point):**
- One producer → one consumer (or one из group)
- Message **deleted** after consumption
- FIFO usually
- **Examples:** RabbitMQ queues, SQS, ActiveMQ

**Pub/Sub:**
- One producer → **multiple subscribers** (broadcast)
- Each subscriber gets copy
- **Examples:** RabbitMQ topic exchanges, SNS, NATS Core

**Stream (log):**
- Append-only log
- **Multiple consumers** read **independently** (own offset)
- Messages **NOT deleted** (retention-based)
- **Replayable** (any consumer can re-read)
- **Examples:** Kafka, Pulsar, Kinesis, Redpanda

| Pattern | Кто получает | Persistence |
|---------|-------------|-------------|
| Queue | Один (load balanced) | Until consumed |
| Pub/Sub | Все subscribers | Per subscriber или transient |
| Stream | Все consumers (independent) | Retention-based |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Message broker vs streaming platform? Частая ошибка в реальном коде.

**Message broker** (RabbitMQ, ActiveMQ, NATS Core):
- Designed для **transient messaging**
- Optimized для routing flexibility
- Lower throughput obычно
- Often **at-most-once** или **at-least-once**

**Streaming platform** (Kafka, Pulsar, Redpanda):
- Designed для **persistent log**
- High throughput
- **Replay capability**
- **At-least-once** standard, **exactly-once** possible
- Stream processing integration (Kafka Streams, Flink)

**Hybrid** (NATS + JetStream, RabbitMQ Streams) — both worlds.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Apache Kafka — характеристики? Частая ошибка в реальном коде.

| Critterion | Apache Kafka |
|-----------|--------------|
| Type | Streaming platform |
| Language | Java (JVM) |
| Throughput | Very high (millions msg/sec) |
| Latency | Medium (10-100 ms) |
| Persistence | Always (log-based) |
| Ordering | Per-partition |
| Replay | Excellent |
| Multi-region | MirrorMaker (separate tool) |
| Ecosystem | **Massive** (Connect, Streams, registry) |
| Maturity | Most mature (2011+) |
| Adoption | **Industry standard** |
| Operational complexity | High (ZK/KRaft, brokers, Connect) |

**Best for:** event streaming, big data pipelines, log aggregation, event sourcing.

Подробнее — в [Apache Kafka](kafka-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) RabbitMQ — характеристики? Частая ошибка в реальном коде.

| Critterion | RabbitMQ |
|-----------|----------|
| Type | Message broker |
| Language | Erlang |
| Throughput | Medium (tens of K msg/sec) |
| Latency | Low (ms) |
| Persistence | Optional |
| Ordering | Per-queue |
| Replay | Limited (Streams plugin newer) |
| Routing | **Excellent** (exchanges, bindings, topics) |
| Protocols | AMQP, MQTT, STOMP |
| Maturity | Very mature (2007+) |
| Adoption | Wide |
| Operational complexity | Medium |

**Best for:** enterprise messaging, complex routing, RPC, transactional workloads.

Подробнее — в [RabbitMQ](rabbitmq-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) NATS — характеристики? Частая ошибка в реальном коде.

| Критерий | NATS |
|----------|------|
| Type | Messaging + streaming (JetStream) |
| Language | Go |
| Throughput | Very high (millions msg/sec) |
| Latency | **Microseconds** (lowest) |
| Persistence | JetStream optional |
| Ordering | Per-subject |
| Multi-region | Native (super-cluster, leaf nodes) |
| Resource usage | **Very low** |
| Setup | **Very simple** |
| Maturity | Mature, growing (2014+) |
| Adoption | Niche but growing |

**Best for:** microservices, IoT, edge computing, low-latency messaging.

Подробнее — в [NATS](nats-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Apache Pulsar — характеристики? Частая ошибка в реальном коде.

| Критерий | Apache Pulsar |
|----------|---------------|
| Type | Streaming + messaging |
| Language | Java |
| Throughput | Very high |
| Latency | Medium |
| Architecture | **Compute + Storage separated** (BookKeeper) |
| Multi-tenancy | **Built-in** |
| Multi-region | **Native** geo-replication |
| Subscription types | 4 (Exclusive, Shared, Failover, Key_Shared) |
| Tiered storage | Built-in |
| Adoption | Niche |
| Operational complexity | High |

**Best for:** multi-tenant SaaS, multi-region, cloud-native architectures.

Подробнее — в [Apache Pulsar](pulsar-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) Redpanda — характеристики? Частая ошибка в реальном коде.

| Критерий | Redpanda |
|----------|----------|
| Type | Streaming (Kafka-compatible) |
| Language | C++ |
| Throughput | Very high |
| Latency | Lower than Kafka (no GC) |
| Compatibility | **Kafka API** |
| ZooKeeper | None (own Raft) |
| Architecture | Shard-per-core (Seastar) |
| Setup | **Single binary** |
| Adoption | Growing |

**Best for:** Kafka users wanting better performance and easier ops.

Подробнее — в [Redpanda](redpanda-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. AWS SQS/SNS/EventBridge? Частая ошибка в реальном коде.

**SQS** — managed queue (point-to-point).
**SNS** — managed pub/sub.
**EventBridge** — event bus с complex routing, SaaS integrations.

**Best for:** AWS-native apps, serverless, decoupling microservices.

Подробнее — в [AWS SQS/SNS](aws-sqs-sns-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Apache ActiveMQ? Частая ошибка в реальном коде.

| Критерий | Apache ActiveMQ |
|----------|-----------------|
| Type | Message broker (JMS) |
| Language | Java |
| Throughput | Lower |
| Protocols | JMS, AMQP, MQTT, STOMP |
| Maturity | Very mature (2004+) |
| Adoption | Legacy mostly |

**Two flavors:**
- **ActiveMQ Classic** — old, less popular
- **Artemis** — newer, better performance

**Best for:** Java enterprise legacy systems, JMS compliance required.

В **2025** — declining, replaced by Kafka, RabbitMQ.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Throughput comparison? Частая ошибка в реальном коде.

**Approximate throughput per node** (single broker):

```
NATS Core:        10M+ msg/sec (small messages)
Redpanda:         5M+ msg/sec
Kafka:            1-2M msg/sec
NATS JetStream:   500K msg/sec
Pulsar:           500K-1M msg/sec
RabbitMQ:         50K-100K msg/sec
ActiveMQ:         20K msg/sec
SQS:              No limit (managed scale)
```

**Caveats:**
- Highly **dependent на message size**
- **Hardware matters** (NIC, disk, CPU)
- Settings, replication factor

**For most apps** RabbitMQ / Kafka / NATS — overkill в throughput. **Choose by features**, not raw throughput.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) Latency comparison? Частая ошибка в реальном коде.

**Typical p99 latency** (no batching):

```
NATS Core:        < 1 ms
NATS JetStream:   2-5 ms
Redpanda:         5-10 ms
RabbitMQ:         10-50 ms
Pulsar:           10-50 ms
Kafka:            10-100 ms
SQS:              50-200 ms
```

**Kafka latency** depends на batch settings — `linger.ms` trades latency for throughput.

**For low-latency** требования (gaming, trading) — NATS / Redpanda preferred.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Ordering guarantees? Частая ошибка в реальном коде.

| System | Ordering |
|--------|----------|
| Kafka | Per-partition |
| RabbitMQ | Per-queue (single consumer) |
| NATS Core | None (parallel delivery) |
| NATS JetStream | Per-stream |
| Pulsar | Per-partition или Key_Shared subscription |
| Redpanda | Per-partition |
| SQS Standard | Best-effort (may reorder) |
| SQS FIFO | **Strict per-message-group** |
| EventBridge | Best-effort |

**Strict ordering** обычно requires **single consumer per partition/queue**.

**For partial ordering** (per-key) → use **partition keys** (Kafka, Pulsar) или **message groups** (SQS FIFO).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) Delivery semantics (at-least-once, exactly-once)? Частая ошибка в реальном коде.

| System | Default | Possible |
|--------|---------|----------|
| Kafka | At-least-once | Exactly-once (transactional) |
| RabbitMQ | At-most-once | At-least-once (с acks) |
| NATS Core | **At-most-once** | — |
| NATS JetStream | At-least-once | Exactly-once |
| Pulsar | At-least-once | Exactly-once |
| Redpanda | At-least-once | Exactly-once |
| SQS Standard | At-least-once | — |
| SQS FIFO | **Exactly-once** | — |

**Exactly-once** в distributed systems — hard. Обычно achieved через:
- Idempotent producers
- Transactional writes
- Idempotent consumers

В практике — **at-least-once + idempotent consumers** = effective exactly-once.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Persistence? Частая ошибка в реальном коде.

| System | Persistence |
|--------|-------------|
| Kafka | Always (log-based) |
| RabbitMQ | Optional (durable queues + persistent messages) |
| NATS Core | None (transient) |
| NATS JetStream | Yes |
| Pulsar | Always (BookKeeper) |
| Redpanda | Always |
| SQS | Yes (managed) |
| ActiveMQ | Optional |

**Persistent** — survives broker restart.
**Non-persistent** — faster but lost on restart.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Replay (consume historical messages)? Частая ошибка в реальном коде.

| System | Replay |
|--------|--------|
| Kafka | **Excellent** (any consumer, any offset) |
| Pulsar | **Excellent** |
| Redpanda | **Excellent** |
| NATS JetStream | Yes (limited time/size) |
| RabbitMQ | Limited (Streams plugin) |
| SQS | **No** (deleted после consumption) |
| EventBridge | Archive + replay supported |

**Replay** ключевая фича для:
- Recovery after bug
- Bootstrapping new consumers
- A/B testing с historical data
- Event sourcing

**Если replay критичен** → streaming platform (Kafka, Pulsar).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Operational complexity? Частая ошибка в реальном коде.

**Easiest → Hardest:**

1. **NATS** — single binary, no dependencies
2. **Redpanda** — single binary, no ZK
3. **AWS SQS/SNS/EventBridge** — fully managed
4. **RabbitMQ** — moderate (clusters, plugins)
5. **Kafka** — high (brokers, ZK/KRaft, Connect, Schema Registry)
6. **Pulsar** — highest (brokers + bookies + ZK)

**Managed services** убирают complexity:
- **Confluent Cloud** (managed Kafka)
- **MSK** (AWS managed Kafka)
- **CloudAMQP** (managed RabbitMQ)
- **Synadia** (managed NATS)
- **StreamNative** (managed Pulsar)
- **Redpanda Cloud**


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Multi-region? Частая ошибка в реальном коде.

| System | Multi-region |
|--------|--------------|
| Kafka | MirrorMaker 2 (separate tool) |
| Pulsar | **Native** geo-replication |
| NATS | **Native** super-cluster |
| RabbitMQ | Federation, Shovel plugins |
| Redpanda | Cluster linking (newer) |
| SQS | Single-region (cross-region replication setup) |

**Pulsar и NATS** — best built-in multi-region.

**Kafka** — possible но MirrorMaker complex ops.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. (!) Когда выбрать Kafka? Частая ошибка в реальном коде.

**Выбирай Kafka когда:**
- **Event streaming** — main use case
- **Big data pipelines** (Spark, Flink integration)
- **Event sourcing** + replay
- **Already invested** в Kafka ecosystem
- Need **massive ecosystem** (Kafka Connect, Streams, registry)
- **Long retention** required
- Industry standard, hire-friendly

**Не выбирай когда:**
- Need very low latency (< 5 ms) — NATS / Redpanda
- Simple queueing only — RabbitMQ
- AWS-native serverless — SQS/EventBridge
- Multi-tenancy — Pulsar
- Don't want ops complexity — managed alternatives


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Когда выбрать RabbitMQ? Частая ошибка в реальном коде.

**Выбирай RabbitMQ когда:**
- **Complex routing** (exchanges, topic-based, header-based)
- **Enterprise messaging** patterns (RPC, work queues)
- **Multiple protocols** (AMQP, MQTT, STOMP)
- **Lower throughput** (< 100K msg/sec) acceptable
- Java enterprise (JMS-like patterns)
- Need **mature, battle-tested** broker
- Dead letter exchanges, priority queues, TTL

**Не выбирай когда:**
- High throughput streaming — Kafka
- Long retention / replay — Kafka
- Microservices internal — NATS often better


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Когда выбрать NATS? Частая ошибка в реальном коде.

**Выбирай NATS когда:**
- **Microservices** internal communication
- **Low latency** critical (< 5 ms)
- **IoT** — millions devices
- **Edge computing** — leaf nodes
- **Multi-region** native required
- Want **simple ops** (single binary)
- Resource-constrained environments

**Не выбирай когда:**
- Need huge ecosystem (Kafka)
- Complex stream processing (Kafka Streams, Flink)
- Long-term retention (Kafka cheaper)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. (!) Когда выбрать Pulsar? Частая ошибка в реальном коде.

**Выбирай Pulsar когда:**
- **Multi-tenant SaaS** platform
- **Multi-region** native required
- **Storage и compute** scale separately
- **Long retention** с tiered storage (S3)
- **Multiple subscription patterns** needed
- Modern cloud-native architecture

**Не выбирай когда:**
- Need maximum simplicity (Kafka or NATS easier)
- Already deep in Kafka ecosystem
- Smaller team can't handle complexity


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Когда выбрать SQS/EventBridge? Частая ошибка в реальном коде.

**Выбирай SQS/SNS/EventBridge когда:**
- **AWS-native** application
- **Serverless** stack (Lambda heavy)
- Don't want **infrastructure ops**
- **Pay-per-use** preferable
- **Cross-AWS-services** integration
- **SaaS integrations** (EventBridge для Stripe, Auth0, ...)

**Не выбирай когда:**
- Multi-cloud / on-prem
- Очень high throughput на consistent basis (cost adds up)
- Need streaming с replay
- Complex ordering requirements outside SQS FIFO limits


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. (!) Use case: order processing pipeline? Частая ошибка в реальном коде.

**Requirements:**
- Order placed → multiple downstream actions (charge, fulfill, notify)
- Reliable processing
- Replay for recovery

**Best fit:** **Kafka** + **Kafka Streams** (или **Pulsar**).

```
Order Service → Kafka (orders topic) → 
  ├─ Charge Service (consumer)
  ├─ Fulfillment Service (consumer)
  ├─ Notification Service (consumer)
  └─ Analytics (Kafka Streams aggregate)
```

**Kafka:** ordering per partition (по customer_id), replay, multiple consumers.

**Alternative:** Pulsar (similar), AWS EventBridge (managed AWS-native).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. Use case: notification system? Частая ошибка в реальном коде.

**Requirements:**
- Send email/SMS/push к users
- Multiple channels per notification
- Retry on failure

**Best fit:** **SNS** (AWS) или **RabbitMQ** + worker queues.

```
Trigger → SNS notification topic →
  ├─ SQS email queue → Email service
  ├─ SQS SMS queue → SMS service
  └─ SQS push queue → Push service
```

Каждый worker has own DLQ. Independent retry strategies.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Use case: clickstream analytics? Частая ошибка в реальном коде.

**Requirements:**
- Web/app events (millions/sec)
- Real-time + batch processing
- Long retention для analytics

**Best fit:** **Kafka** или **Kinesis**.

```
Web/App → Kafka clickstream topic →
  ├─ Real-time: Flink processing → dashboards
  ├─ Batch: Spark ETL → DWH (BigQuery, Snowflake)
  └─ Storage: tiered к S3 (cold)
```

**Pulsar** alternative для multi-region.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Use case: IoT с millions devices? Частая ошибка в реальном коде.

**Requirements:**
- Millions devices sending sensor data
- Low resource usage on devices
- Edge processing

**Best fit:** **MQTT broker** (HiveMQ, EMQX, AWS IoT Core) или **NATS** (с leaf nodes).

```
IoT devices → MQTT broker / NATS leaf → Central NATS / Kafka
                                          ↓
                                  Stream processing
```

**MQTT** — IoT standard protocol.
**NATS** — modern alternative, more flexible.

**Kafka** не ideal для IoT — heavyweight, no edge support.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление Частая ошибка в реальном коде.
- [Apache Kafka](kafka-interview.md)
- [RabbitMQ](rabbitmq-interview.md)
- [NATS](nats-interview.md)
- [Apache Pulsar](pulsar-interview.md)
- [Redpanda](redpanda-interview.md)
- [AWS SQS/SNS](aws-sqs-sns-interview.md)
- [Event-driven Patterns](../architecture/event-driven-patterns-interview.md)
- [Микросервисы](../architecture/microservices-interview.md)
- [Stream Processing](../data-engineering/stream-processing-interview.md)
- [Saga Pattern](../architecture/saga-pattern-interview.md)
- [Распределённые системы](../architecture/distributed-systems-interview.md)
- [CAP Theorem](../architecture/cap-theorem-interview.md)
- [Scalability Patterns](../architecture/scalability-patterns-interview.md)
