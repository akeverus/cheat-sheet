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

Обзор и сравнение основных систем обмена сообщениями: **Apache Kafka, RabbitMQ, NATS, Apache Pulsar, Redpanda, AWS SQS/SNS/EventBridge, Apache ActiveMQ**. Часто на интервью спрашивают: «когда выбрать что и почему». Шпаргалка для быстрых решений и глубокого сравнения.

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

**Рекомендации по выбору**
- [Q18. (!) Когда выбрать Kafka?](#q18--когда-выбрать-kafka)
- [Q19. (!) Когда выбрать RabbitMQ?](#q19--когда-выбрать-rabbitmq)
- [Q20. (!) Когда выбрать NATS?](#q20--когда-выбрать-nats)
- [Q21. (!) Когда выбрать Pulsar?](#q21--когда-выбрать-pulsar)
- [Q22. (!) Когда выбрать SQS/EventBridge?](#q22--когда-выбрать-sqseventbridge)

**Паттерны**
- [Q23. (!) Use case: order processing pipeline?](#q23--use-case-order-processing-pipeline)
- [Q24. Use case: notification system?](#q24-use-case-notification-system)
- [Q25. Use case: clickstream analytics?](#q25-use-case-clickstream-analytics)
- [Q26. Use case: IoT с millions devices?](#q26-use-case-iot-с-millions-devices)

## Q1. (!) Pub/Sub vs Queue vs Stream — отличия?

(!) Pub/Sub vs Queue vs Stream — отличия?

**Queue (точка-точка):**
- Один producer → один consumer (или один из группы)
- Сообщение **удаляется** после обработки
- Обычно FIFO
- **Примеры:** очереди RabbitMQ, SQS, ActiveMQ

**Pub/Sub:**
- Один producer → **несколько подписчиков** (broadcast)
- Каждый подписчик получает копию
- **Примеры:** topic exchanges в RabbitMQ, SNS, NATS Core

**Stream (лог):**
- Append-only лог
- **Несколько consumer-ов** читают **независимо** (каждый со своим offset)
- Сообщения **НЕ удаляются** (хранение по retention)
- **Воспроизводимы** (любой consumer может перечитать)
- **Примеры:** Kafka, Pulsar, Kinesis, Redpanda

| Паттерн | Кто получает | Хранение |
|---------|-------------|-------------|
| Queue | Один (с балансировкой) | До обработки |
| Pub/Sub | Все подписчики | На подписчика или без хранения |
| Stream | Все consumer-ы (независимо) | По retention |

## Q2. (!) Message broker vs streaming platform?

**Message broker** (RabbitMQ, ActiveMQ, NATS Core):
- Рассчитан на **обмен сообщениями без долгого хранения**
- Оптимизирован под гибкость маршрутизации
- Обычно ниже throughput
- Часто **at-most-once** или **at-least-once**

**Streaming-платформа** (Kafka, Pulsar, Redpanda):
- Рассчитана на **persistent-лог**
- Высокий throughput
- **Возможность replay**
- **At-least-once** по умолчанию, **exactly-once** достижима
- Интеграция со stream processing (Kafka Streams, Flink)

**Гибрид** (NATS + JetStream, RabbitMQ Streams) — оба мира сразу.

## Q3. (!) Apache Kafka — характеристики?

| Критерий | Apache Kafka |
|-----------|--------------|
| Тип | Streaming-платформа |
| Язык | Java (JVM) |
| Throughput | Очень высокий (миллионы msg/sec) |
| Latency | Средняя (10–100 мс) |
| Хранение | Всегда (на основе лога) |
| Упорядоченность | На партицию |
| Replay | Отличный |
| Multi-region | MirrorMaker (отдельный инструмент) |
| Экосистема | **Огромная** (Connect, Streams, registry) |
| Зрелость | Самая зрелая (с 2011) |
| Распространённость | **Промышленный стандарт** |
| Сложность эксплуатации | Высокая (ZK/KRaft, брокеры, Connect) |

**Лучше всего для:** event streaming, big data-конвейеров, агрегации логов, event sourcing.

Подробнее — в [Apache Kafka](kafka-interview.md).

## Q4. (!) RabbitMQ — характеристики?

| Критерий | RabbitMQ |
|-----------|----------|
| Тип | Message broker |
| Язык | Erlang |
| Throughput | Средний (десятки тысяч msg/sec) |
| Latency | Низкая (мс) |
| Хранение | Опционально |
| Упорядоченность | На очередь |
| Replay | Ограниченный (плагин Streams, появился позднее) |
| Маршрутизация | **Отличная** (exchanges, bindings, topics) |
| Протоколы | AMQP, MQTT, STOMP |
| Зрелость | Очень зрелый (с 2007) |
| Распространённость | Широкая |
| Сложность эксплуатации | Средняя |

**Лучше всего для:** enterprise-обмена сообщениями, сложной маршрутизации, RPC, транзакционных нагрузок.

Подробнее — в [RabbitMQ](rabbitmq-interview.md).

## Q5. (!) NATS — характеристики?

| Критерий | NATS |
|----------|------|
| Тип | Обмен сообщениями + streaming (JetStream) |
| Язык | Go |
| Throughput | Очень высокий (миллионы msg/sec) |
| Latency | **Микросекунды** (самая низкая) |
| Хранение | Опционально через JetStream |
| Упорядоченность | На subject |
| Multi-region | Нативный (super-cluster, leaf nodes) |
| Потребление ресурсов | **Очень низкое** |
| Настройка | **Очень простая** |
| Зрелость | Зрелый, растущий (с 2014) |
| Распространённость | Нишевый, но растущий |

**Лучше всего для:** микросервисов, IoT, edge computing, низколатентного обмена сообщениями.

Подробнее — в [NATS](nats-interview.md).

## Q6. (!) Apache Pulsar — характеристики?

| Критерий | Apache Pulsar |
|----------|---------------|
| Тип | Streaming + обмен сообщениями |
| Язык | Java |
| Throughput | Очень высокий |
| Latency | Средняя |
| Архитектура | **Вычисления и хранилище разделены** (BookKeeper) |
| Multi-tenancy | **Встроенная** |
| Multi-region | **Нативная** geo-репликация |
| Типы подписок | 4 (Exclusive, Shared, Failover, Key_Shared) |
| Tiered storage | Встроенный |
| Распространённость | Нишевый |
| Сложность эксплуатации | Высокая |

**Лучше всего для:** multi-tenant SaaS, multi-region, cloud-native-архитектур.

Подробнее — в [Apache Pulsar](pulsar-interview.md).

## Q7. (!) Redpanda — характеристики?

| Критерий | Redpanda |
|----------|----------|
| Тип | Streaming (совместим с Kafka) |
| Язык | C++ |
| Throughput | Очень высокий |
| Latency | Ниже, чем у Kafka (нет GC) |
| Совместимость | **Kafka API** |
| ZooKeeper | Нет (собственный Raft) |
| Архитектура | Shard-per-core (Seastar) |
| Настройка | **Один бинарник** |
| Распространённость | Растёт |

**Лучше всего для:** пользователей Kafka, которым нужна более высокая производительность и проще эксплуатация.

Подробнее — в [Redpanda](redpanda-interview.md).

## Q8. AWS SQS/SNS/EventBridge?

**SQS** — управляемая очередь (точка-точка).
**SNS** — управляемый pub/sub.
**EventBridge** — шина событий со сложной маршрутизацией и интеграциями с SaaS.

**Лучше всего для:** AWS-native-приложений, serverless, развязки микросервисов.

Подробнее — в [AWS SQS/SNS](aws-sqs-sns-interview.md).

## Q9. Apache ActiveMQ?

| Критерий | Apache ActiveMQ |
|----------|-----------------|
| Тип | Message broker (JMS) |
| Язык | Java |
| Throughput | Ниже |
| Протоколы | JMS, AMQP, MQTT, STOMP |
| Зрелость | Очень зрелый (с 2004) |
| Распространённость | В основном legacy |

**Две разновидности:**
- **ActiveMQ Classic** — старая, менее популярная
- **Artemis** — новее, выше производительность

**Лучше всего для:** legacy Java enterprise-систем, где требуется совместимость с JMS.

В **2025** — на спаде, вытесняется Kafka и RabbitMQ.

## Q10. (!) Throughput comparison?

**Примерный throughput на узел** (один брокер):

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

**Оговорки:**
- Сильно **зависит от размера сообщения**
- **Важно железо** (NIC, диск, CPU)
- Настройки, replication factor

**Для большинства приложений** RabbitMQ / Kafka / NATS — избыточны по throughput. **Выбирай по фичам**, а не по сырому throughput.

## Q11. (!) Latency comparison?

**Типичная p99 latency** (без батчинга):

```
NATS Core:        < 1 ms
NATS JetStream:   2-5 ms
Redpanda:         5-10 ms
RabbitMQ:         10-50 ms
Pulsar:           10-50 ms
Kafka:            10-100 ms
SQS:              50-200 ms
```

**Latency Kafka** зависит от настроек батчинга — `linger.ms` обменивает latency на throughput.

**Для низколатентных** требований (gaming, trading) предпочтительнее NATS / Redpanda.

## Q12. (!) Ordering guarantees?

| Система | Упорядоченность |
|--------|----------|
| Kafka | На партицию |
| RabbitMQ | На очередь (один потребитель) |
| NATS Core | Нет (параллельная доставка) |
| NATS JetStream | На stream |
| Pulsar | На партицию или через подписку Key_Shared |
| Redpanda | На партицию |
| SQS Standard | По возможности (возможна перестановка) |
| SQS FIFO | **Строгая в пределах message-group** |
| EventBridge | По возможности |

**Строгая упорядоченность** обычно требует **одного consumer-а на партицию/очередь**.

**Для частичной упорядоченности** (по ключу) → используй **ключи партиционирования** (Kafka, Pulsar) или **message groups** (SQS FIFO).

## Q13. (!) Delivery semantics (at-least-once, exactly-once)?

| Система | По умолчанию | Возможно |
|--------|---------|----------|
| Kafka | At-least-once | Exactly-once (транзакционно) |
| RabbitMQ | At-most-once | At-least-once (с acks) |
| NATS Core | **At-most-once** | — |
| NATS JetStream | At-least-once | Exactly-once |
| Pulsar | At-least-once | Exactly-once |
| Redpanda | At-least-once | Exactly-once |
| SQS Standard | At-least-once | — |
| SQS FIFO | **Exactly-once** | — |

**Exactly-once** в распределённых системах — сложно. Обычно достигается через:
- Идемпотентные producer-ы
- Транзакционные записи
- Идемпотентные consumer-ы

На практике — **at-least-once + идемпотентные consumer-ы** = эффективная exactly-once.

## Q14. Persistence?

| Система | Хранение |
|--------|-------------|
| Kafka | Всегда (на основе лога) |
| RabbitMQ | Опционально (durable-очереди + persistent-сообщения) |
| NATS Core | Нет (без хранения) |
| NATS JetStream | Да |
| Pulsar | Всегда (BookKeeper) |
| Redpanda | Всегда |
| SQS | Да (управляемое) |
| ActiveMQ | Опционально |

**Хранимое (persistent)** — переживает рестарт брокера.
**Нехранимое (non-persistent)** — быстрее, но теряется при рестарте.

## Q15. Replay (consume historical messages)?

| Система | Replay |
|--------|--------|
| Kafka | **Отличный** (любой consumer, любой offset) |
| Pulsar | **Отличный** |
| Redpanda | **Отличный** |
| NATS JetStream | Да (ограничен по времени/размеру) |
| RabbitMQ | Ограниченный (плагин Streams) |
| SQS | **Нет** (удаляется после обработки) |
| EventBridge | Поддерживаются архив и replay |

**Replay** — ключевая фича для:
- Восстановления после бага
- Подключения новых consumer-ов
- A/B-тестирования на исторических данных
- Event sourcing

**Если replay критичен** → streaming-платформа (Kafka, Pulsar).

## Q16. Operational complexity?

**От самого простого к самому сложному:**

1. **NATS** — один бинарник, без зависимостей
2. **Redpanda** — один бинарник, без ZK
3. **AWS SQS/SNS/EventBridge** — полностью управляемые
4. **RabbitMQ** — умеренно (кластеры, плагины)
5. **Kafka** — высокая (брокеры, ZK/KRaft, Connect, Schema Registry)
6. **Pulsar** — самая высокая (брокеры + bookies + ZK)

**Управляемые сервисы** убирают сложность:
- **Confluent Cloud** (управляемая Kafka)
- **MSK** (управляемая Kafka от AWS)
- **CloudAMQP** (управляемый RabbitMQ)
- **Synadia** (управляемый NATS)
- **StreamNative** (управляемый Pulsar)
- **Redpanda Cloud**

## Q17. Multi-region?

| Система | Multi-region |
|--------|--------------|
| Kafka | MirrorMaker 2 (отдельный инструмент) |
| Pulsar | **Нативная** geo-репликация |
| NATS | **Нативный** super-cluster |
| RabbitMQ | Плагины Federation, Shovel |
| Redpanda | Cluster linking (появился позднее) |
| SQS | Один регион (требуется настройка межрегиональной репликации) |

**Pulsar и NATS** — лучший встроенный multi-region.

**Kafka** — возможен, но MirrorMaker сложен в эксплуатации.

## Q18. (!) Когда выбрать Kafka?

**Выбирай Kafka когда:**
- **Event streaming** — основной сценарий
- **Big data-конвейеры** (интеграция со Spark, Flink)
- **Event sourcing** + replay
- **Уже вложились** в экосистему Kafka
- Нужна **огромная экосистема** (Kafka Connect, Streams, registry)
- Требуется **долгое хранение**
- Промышленный стандарт, легко нанимать специалистов

**Не выбирай когда:**
- Нужна очень низкая latency (< 5 мс) — NATS / Redpanda
- Только простая очередь — RabbitMQ
- AWS-native serverless — SQS/EventBridge
- Multi-tenancy — Pulsar
- Не хочешь сложности эксплуатации — управляемые альтернативы

## Q19. (!) Когда выбрать RabbitMQ?

**Выбирай RabbitMQ когда:**
- **Сложная маршрутизация** (по exchange, по topic, по header)
- **Enterprise-паттерны обмена сообщениями** (RPC, рабочие очереди)
- **Несколько протоколов** (AMQP, MQTT, STOMP)
- Приемлем **более низкий throughput** (< 100K msg/sec)
- Java enterprise (JMS-подобные паттерны)
- Нужен **зрелый, проверенный в бою** брокер
- Dead letter exchanges, очереди приоритетов, TTL

**Не выбирай когда:**
- Высоконагруженный streaming — Kafka
- Долгое хранение / replay — Kafka
- Внутренняя коммуникация микросервисов — NATS часто лучше

## Q20. (!) Когда выбрать NATS?

**Выбирай NATS когда:**
- **Внутренняя коммуникация микросервисов**
- Критична **низкая latency** (< 5 мс)
- **IoT** — миллионы устройств
- **Edge computing** — leaf nodes
- Нужен нативный **multi-region**
- Хочешь **простую эксплуатацию** (один бинарник)
- Среды с ограниченными ресурсами

**Не выбирай когда:**
- Нужна огромная экосистема (Kafka)
- Сложный stream processing (Kafka Streams, Flink)
- Долгосрочное хранение (у Kafka дешевле)

## Q21. (!) Когда выбрать Pulsar?

**Выбирай Pulsar когда:**
- **Multi-tenant SaaS**-платформа
- Нужен нативный **multi-region**
- **Хранилище и вычисления** масштабируются раздельно
- **Долгое хранение** с tiered storage (S3)
- Нужны **разные паттерны подписок**
- Современная cloud-native-архитектура

**Не выбирай когда:**
- Нужна максимальная простота (Kafka или NATS проще)
- Уже глубоко в экосистеме Kafka
- Небольшая команда не справится со сложностью

## Q22. (!) Когда выбрать SQS/EventBridge?

**Выбирай SQS/SNS/EventBridge когда:**
- **AWS-native**-приложение
- **Serverless**-стек (много Lambda)
- Не хочешь **эксплуатировать инфраструктуру**
- Предпочтительна модель **pay-per-use**
- Интеграция **между AWS-сервисами**
- **SaaS-интеграции** (EventBridge для Stripe, Auth0, ...)

**Не выбирай когда:**
- Multi-cloud / on-prem
- Очень высокий throughput на постоянной основе (стоимость накапливается)
- Нужен streaming с replay
- Сложные требования к упорядоченности за пределами ограничений SQS FIFO

## Q23. (!) Use case: order processing pipeline?

**Требования:**
- Заказ оформлен → несколько последующих действий (списать оплату, собрать заказ, уведомить)
- Надёжная обработка
- Replay для восстановления

**Лучший выбор:** **Kafka** + **Kafka Streams** (или **Pulsar**).

```
Order Service → Kafka (orders topic) → 
  ├─ Charge Service (consumer)
  ├─ Fulfillment Service (consumer)
  ├─ Notification Service (consumer)
  └─ Analytics (Kafka Streams aggregate)
```

**Kafka:** упорядоченность на партицию (по customer_id), replay, несколько consumer-ов.

**Альтернатива:** Pulsar (похоже), AWS EventBridge (управляемый, AWS-native).

## Q24. Use case: notification system?

**Требования:**
- Отправка email/SMS/push пользователям
- Несколько каналов на уведомление
- Повторная отправка при сбое

**Лучший выбор:** **SNS** (AWS) или **RabbitMQ** + worker-очереди.

```
Trigger → SNS notification topic →
  ├─ SQS email queue → Email service
  ├─ SQS SMS queue → SMS service
  └─ SQS push queue → Push service
```

У каждого worker-а своя DLQ. Независимые стратегии повторов.

## Q25. Use case: clickstream analytics?

**Требования:**
- События web/приложений (миллионы/сек)
- Обработка в реальном времени + пакетная (batch)
- Долгое хранение для аналитики

**Лучший выбор:** **Kafka** или **Kinesis**.

```
Web/App → Kafka clickstream topic →
  ├─ Real-time: Flink processing → dashboards
  ├─ Batch: Spark ETL → DWH (BigQuery, Snowflake)
  └─ Storage: tiered к S3 (cold)
```

**Pulsar** — альтернатива для multi-region.

## Q26. Use case: IoT с millions devices?

**Требования:**
- Миллионы устройств шлют данные с датчиков
- Низкое потребление ресурсов на устройствах
- Обработка на границе сети (edge)

**Лучший выбор:** **MQTT-брокер** (HiveMQ, EMQX, AWS IoT Core) или **NATS** (с leaf nodes).

```
IoT devices → MQTT broker / NATS leaf → Central NATS / Kafka
                                          ↓
                                  Stream processing
```

**MQTT** — стандартный протокол для IoT.
**NATS** — современная альтернатива, более гибкая.

**Kafka** не идеален для IoT — тяжеловесен, нет поддержки граничных узлов (edge).

## See also

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
