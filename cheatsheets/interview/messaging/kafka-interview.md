---
title: "Вопросы на собеседовании: Apache Kafka"
description: "Полное покрытие Apache Kafka: архитектура, топики, партиции, consumer groups, репликация, гарантии доставки, Spring Kafka, Kafka Streams, KRaft, мониторинг."
tags:
  - interview
  - messaging
  - kafka-interview
aliases:
  - "Apache Kafka"
  - "Kafka interview"
  - "Kafka собеседование"
  - "Spring Kafka"
  - "Kafka Streams"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Apache Kafka`

Полное покрытие `Apache Kafka` для интервью: архитектура кластера, топики и партиции, `Producer` / `Consumer`, `Consumer Groups`, репликация и отказоустойчивость, гарантии доставки, `Spring Kafka`, `Kafka Streams`, `KRaft`, мониторинг и администрирование.

**`Apache Kafka`** — распределённая платформа потоковой обработки данных, де-факто стандарт для event-driven архитектур. Знание Kafka — обязательное требование для backend/platform-инженеров, особенно в контексте [микросервисов](../architecture/microservices-interview.md) и [event-driven паттернов](../architecture/event-driven-patterns-interview.md).

## Полезные ссылки

### Официальная документация

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/) — полная документация
- [Kafka APIs](https://kafka.apache.org/documentation/#api) — описание всех API
- [KRaft Overview](https://developer.confluent.io/learn/kraft/) — Kafka без ZooKeeper
- [Confluent Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html) — Schema Registry документация

### Статьи Baeldung

- [Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka) — Spring Kafka интеграция
- [Kafka Streams With Spring Boot](https://www.baeldung.com/spring-boot-kafka-streams) — Kafka Streams + Spring Boot
- [Kafka Streams vs. Kafka Consumer](https://www.baeldung.com/java-kafka-streams-vs-kafka-consumer) — сравнение подходов обработки
- [Exactly Once Processing in Kafka with Java](https://www.baeldung.com/kafka-exactly-once) — exactly-once семантика
- [Manage Kafka Consumer Groups](https://www.baeldung.com/kafka-manage-consumer-groups) — управление consumer groups
- [Understanding Kafka Consumer Offset](https://www.baeldung.com/kafka-consumer-offset) — управление оффсетами
- [Ensuring Message Ordering in Kafka](https://www.baeldung.com/kafka-message-ordering) — гарантии порядка сообщений

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Kafka**
- [Q1. (!) Что такое Apache Kafka и для чего он используется?](#q1--что-такое-apache-kafka-и-для-чего-он-используется)
- [Q2. (!) Какие основные компоненты Apache Kafka?](#q2--какие-основные-компоненты-apache-kafka)
- [Q3. Что такое Topic?](#q3-что-такое-topic)
- [Q4. Что такое ZooKeeper и какова его роль в Kafka?](#q4-что-такое-zookeeper-и-какова-его-роль-в-kafka)
- [Q5. (!) Что такое KRaft и зачем он нужен?](#q5--что-такое-kraft-и-зачем-он-нужен)

**Partitions и Offsets**
- [Q6. (!) Что такое Partition и как она устроена?](#q6--что-такое-partition-и-как-она-устроена)
- [Q7. Что такое Offset?](#q7-что-такое-offset)
- [Q8. (!) Как обеспечить порядок сообщений в Kafka?](#q8--как-обеспечить-порядок-сообщений-в-kafka)
- [Q9. Как выбирается Partition при отправке сообщения?](#q9-как-выбирается-partition-при-отправке-сообщения)

**Producer**
- [Q10. (!) Что такое Producer и как он работает?](#q10--что-такое-producer-и-как-он-работает)
- [Q11. (!) Что означает параметр acks у Producer?](#q11--что-означает-параметр-acks-у-producer)
- [Q12. Что такое идемпотентный Producer?](#q12-что-такое-идемпотентный-producer)
- [Q13. Как настроить батчинг и сжатие у Producer?](#q13-как-настроить-батчинг-и-сжатие-у-producer)

**Consumer и Consumer Groups**
- [Q14. (!) Что такое Consumer и Consumer Group?](#q14--что-такое-consumer-и-consumer-group)
- [Q15. (!) Как происходит Rebalance?](#q15--как-происходит-rebalance)
- [Q16. В чём разница между auto-commit и manual commit?](#q16-в-чём-разница-между-auto-commit-и-manual-commit)
- [Q17. Что такое Cooperative Sticky Assignor?](#q17-что-такое-cooperative-sticky-assignor)

**Репликация и отказоустойчивость**
- [Q18. (!) Как работает репликация в Kafka?](#q18--как-работает-репликация-в-kafka)
- [Q19. (!) Что такое ISR, OSR и High Watermark?](#q19--что-такое-isr-osr-и-high-watermark)
- [Q20. Что произойдёт при падении Leader-брокера?](#q20-что-произойдёт-при-падении-leader-брокера)
- [Q21. Что такое Unclean Leader Election?](#q21-что-такое-unclean-leader-election)

**Гарантии доставки**
- [Q22. (!) Какие семантики доставки поддерживает Kafka?](#q22--какие-семантики-доставки-поддерживает-kafka)
- [Q23. (!) Как реализовать Exactly-Once семантику?](#q23--как-реализовать-exactly-once-семантику)
- [Q24. Что такое транзакции в Kafka?](#q24-что-такое-транзакции-в-kafka)

**Spring Kafka**
- [Q25. (!) Как интегрировать Kafka со Spring Boot?](#q25--как-интегрировать-kafka-со-spring-boot)
- [Q26. Как настроить Producer в Spring Kafka?](#q26-как-настроить-producer-в-spring-kafka)
- [Q27. (!) Как настроить Consumer с @KafkaListener?](#q27--как-настроить-consumer-с-kafkalistener)
- [Q28. Как обрабатывать ошибки в Spring Kafka?](#q28-как-обрабатывать-ошибки-в-spring-kafka)
- [Q29. Как тестировать Kafka в Spring Boot?](#q29-как-тестировать-kafka-в-spring-boot)

**Kafka Streams**
- [Q30. (!) Что такое Kafka Streams?](#q30--что-такое-kafka-streams)
- [Q31. Какие основные абстракции в Kafka Streams?](#q31-какие-основные-абстракции-в-kafka-streams)
- [Q32. Как использовать Kafka Streams в Spring Boot?](#q32-как-использовать-kafka-streams-в-spring-boot)

**Kafka Connect**
- [Q33. Что такое Kafka Connect?](#q33-что-такое-kafka-connect)
- [Q34. В чём разница между Source и Sink Connector?](#q34-в-чём-разница-между-source-и-sink-connector)

**Schema Registry**
- [Q35. (!) Зачем нужен Schema Registry?](#q35--зачем-нужен-schema-registry)

**Производительность и мониторинг**
- [Q36. (!) Что такое Consumer Lag и как его измерить?](#q36--что-такое-consumer-lag-и-как-его-измерить)
- [Q37. Какие ключевые метрики Kafka нужно мониторить?](#q37-какие-ключевые-метрики-kafka-нужно-мониторить)
- [Q38. Как масштабировать Kafka-кластер?](#q38-как-масштабировать-kafka-кластер)

**Безопасность**
- [Q39. Как обеспечить безопасность в Kafka?](#q39-как-обеспечить-безопасность-в-kafka)

**Паттерны и практики**
- [Q40. (!) Что такое Dead Letter Queue (DLQ) в Kafka?](#q40--что-такое-dead-letter-queue-dlq-в-kafka)
- [Q41. (!) Как спроектировать правильную топологию топиков?](#q41--как-спроектировать-правильную-топологию-топиков)
- [Q42. В чём разница между Kafka и RabbitMQ?](#q42-в-чём-разница-между-kafka-и-rabbitmq)
- [Q43. Какие паттерны retry применяются с Kafka?](#q43-какие-паттерны-retry-применяются-с-kafka)
- [Q44. (!) Какие практические советы для production-использования Kafka?](#q44--какие-практические-советы-для-production-использования-kafka)

**Продвинутые темы**
- [Q45. (!) Как работает Consumer Group Rebalancing и что такое Static Membership?](#q45--как-работает-consumer-group-rebalancing-и-что-такое-static-membership)
- [Q46. (!) Что такое Log Compaction и как работает __consumer_offsets?](#q46--что-такое-log-compaction-и-как-работает-__consumer_offsets)
- [Q47. Как Schema Registry хранит схемы и обеспечивает совместимость?](#q47-как-schema-registry-хранит-схемы-и-обеспечивает-совместимость)
- [Q48. (!) Как реализовать Exactly-Once с помощью транзакций Kafka и idempotent producer?](#q48--как-реализовать-exactly-once-с-помощью-транзакций-kafka-и-idempotent-producer)
- [Q49. Что такое Kafka Streams State Store и как реализовать stateful-обработку?](#q49-что-такое-kafka-streams-state-store-и-как-реализовать-stateful-обработку)
- [Q50. Как организовать мониторинг __consumer_offsets и Consumer Lag алертинг?](#q50-как-организовать-мониторинг-__consumer_offsets-и-consumer-lag-алертинг)

---

## Q1. (!) Что такое `Apache Kafka` и для чего он используется?

`Apache Kafka` — это распределённая платформа потоковой обработки данных, работающая по модели `publish-subscribe`. Kafka обеспечивает высокую пропускную способность, отказоустойчивость и низкую задержку при передаче сообщений.

**Основные сценарии использования:**
- **Event Streaming** — обмен событиями между [микросервисами](../architecture/microservices-interview.md)
- **Data Pipeline** — перенос данных между системами (CDC, ETL)
- **Log Aggregation** — централизованный сбор логов (см. [Observability](../monitoring/observability-interview.md))
- **Stream Processing** — обработка данных в реальном времени (`Kafka Streams`, `Apache Flink`)
- **Event Sourcing** — хранение потока событий как источника истины

```mermaid
graph LR
    P1[Producer 1] --> K[Apache Kafka Cluster]
    P2[Producer 2] --> K
    K --> C1[Consumer 1]
    K --> C2[Consumer 2]
    K --> C3[Kafka Streams App]
    K --> C4[Kafka Connect → DB]
```

На собеседовании важно упомянуть, что Kafka — это не просто очередь сообщений, а **распределённый коммит-лог** с гарантиями хранения и воспроизведения.

## Q2. (!) Какие основные компоненты `Apache Kafka`?

| Компонент | Описание |
|-----------|----------|
| `Broker` | Сервер Kafka, хранит данные и обслуживает клиентов |
| `Topic` | Логический канал для категоризации сообщений |
| `Partition` | Физическое разделение топика, обеспечивает параллелизм |
| `Producer` | Отправляет сообщения в топики |
| `Consumer` | Читает сообщения из топиков |
| `Consumer Group` | Группа потребителей, распределяющих между собой партиции |
| `ZooKeeper` / `KRaft Controller` | Координация кластера и хранение метаданных |
| `Kafka Connect` | Фреймворк интеграции с внешними системами |
| `Kafka Streams` | Библиотека потоковой обработки данных |
| `Schema Registry` | Управление схемами данных (`Avro`, `Protobuf`, `JSON Schema`) |

```mermaid
graph TB
    subgraph Kafka Cluster
        B1[Broker 1]
        B2[Broker 2]
        B3[Broker 3]
    end
    subgraph Coordination
        KC[KRaft Controller Quorum]
    end
    KC --> B1
    KC --> B2
    KC --> B3
    P[Producers] --> B1
    P --> B2
    B1 --> CG[Consumer Group]
    B2 --> CG
    B3 --> CG
```

## Q3. Что такое `Topic`?

`Topic` — логическая категория (канал), в которую `Producer` публикует сообщения, а `Consumer` из неё читает. Это аналог таблицы в базе данных.

**Ключевые характеристики:**
- Имя топика — уникальный строковый идентификатор
- Топик делится на одну или несколько `Partition`
- Сообщения в топике хранятся ограниченное время (`retention.ms`, по умолчанию 7 дней) или до достижения лимита по размеру (`retention.bytes`)
- Топик может быть `compacted` — Kafka хранит только последнее значение по каждому ключу

```bash
# Создание топика
kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic orders \
  --partitions 12 --replication-factor 3

# Просмотр информации о топике
kafka-topics.sh --bootstrap-server localhost:9092 \
  --describe --topic orders
```

**Naming convention:** используйте чёткую структуру, например `<domain>.<entity>.<event>`: `shop.orders.created`.

## Q4. Что такое `ZooKeeper` и какова его роль в `Kafka`?

`ZooKeeper` — распределённый координатор, который в классической архитектуре Kafka отвечал за:
- Хранение метаданных кластера (список брокеров, топиков, партиций)
- Выборы лидера контроллера
- Управление `ACL` и конфигурацией
- Хранение информации о `Consumer Group` (в старых версиях)

**Важно:** начиная с Kafka 3.3+ `ZooKeeper` является deprecated, а с Kafka 4.0 полностью заменён на `KRaft` (см. Q5).

## Q5. (!) Что такое `KRaft` и зачем он нужен?

`KRaft` (`Kafka Raft`) — новый встроенный механизм консенсуса, заменяющий `ZooKeeper`. Метаданные кластера хранятся в специальном внутреннем топике `__cluster_metadata` и реплицируются по протоколу `Raft`.

**Преимущества `KRaft` над `ZooKeeper`:**
- Отсутствие внешней зависимости — не нужен отдельный ZooKeeper-кластер
- Ускоренное восстановление при сбоях — метаданные лежат в event log
- Лучшая масштабируемость — поддержка миллионов партиций
- Единый механизм безопасности
- Упрощение операций (меньше компонентов для мониторинга)

```mermaid
graph LR
    subgraph "KRaft Controller Quorum"
        C1[Controller 1<br/>Active]
        C2[Controller 2<br/>Standby]
        C3[Controller 3<br/>Standby]
    end
    C1 -->|Raft replication| C2
    C1 -->|Raft replication| C3
    C1 -->|metadata updates| B1[Broker 1]
    C1 -->|metadata updates| B2[Broker 2]
    C1 -->|metadata updates| B3[Broker 3]
```

## Q6. (!) Что такое `Partition` и как она устроена?

`Partition` — основная единица параллелизма и хранения в Kafka. Каждая партиция — это упорядоченная, неизменяемая последовательность записей (append-only log), хранящаяся на диске брокера.

```mermaid
graph LR
    subgraph "Topic: orders (3 partitions)"
        P0["Partition 0<br/>offset: 0,1,2,3,4"]
        P1["Partition 1<br/>offset: 0,1,2,3"]
        P2["Partition 2<br/>offset: 0,1,2"]
    end
    subgraph Brokers
        B1[Broker 1<br/>Leader P0, Follower P1]
        B2[Broker 2<br/>Leader P1, Follower P2]
        B3[Broker 3<br/>Leader P2, Follower P0]
    end
```

**Ключевые свойства:**
- Порядок гарантируется **только внутри одной партиции**
- Каждая партиция имеет одного `Leader` и 0+ `Follower`
- Максимальный параллелизм `Consumer Group` = количество партиций
- Количество партиций можно увеличить, но **нельзя уменьшить**

**Как выбрать число партиций?**
- Формула: `max(T/P, T/C)`, где T — целевой throughput, P — throughput одного producer, C — throughput одного consumer
- Типичный диапазон: 6–30 партиций для среднего топика
- Больше партиций = больше файловых дескрипторов и время rebalance

## Q7. Что такое `Offset`?

`Offset` — уникальный последовательный номер каждого сообщения внутри партиции. Offset — это позиция `Consumer` в логе, по которой он отслеживает прогресс чтения.

**Типы offset:**
- **Current offset** — позиция следующего сообщения, которое вернёт `poll()`
- **Committed offset** — позиция, подтверждённая consumer'ом (сохранена в `__consumer_offsets`)
- **Log-end offset (LEO)** — позиция последнего записанного сообщения
- **High Watermark (HW)** — позиция последнего сообщения, реплицированного на все ISR

**Стратегии начала чтения** (`auto.offset.reset`):
- `earliest` — с самого начала
- `latest` — только новые сообщения
- `none` — ошибка, если нет сохранённого offset

## Q8. (!) Как обеспечить порядок сообщений в `Kafka`?

Порядок гарантируется **только внутри одной партиции**. Способы обеспечения порядка:

1. **Один ключ → одна партиция:** сообщения с одинаковым ключом всегда попадают в одну партицию
2. **Одна партиция на топик:** строгий глобальный порядок, но нет параллелизма
3. **`max.in.flight.requests.per.connection = 1`** — предотвращает переупорядочивание при retry (или используйте идемпотентный producer)

```java
// Все заказы одного клиента попадут в одну партицию
kafkaTemplate.send("orders", customerId, orderEvent);
```

> На собеседовании часто спрашивают: «Можно ли обеспечить глобальный порядок?» — ответ: только при одной партиции, что убивает масштабируемость.

## Q9. Как выбирается `Partition` при отправке сообщения?

Стратегии партиционирования:

| Сценарий | Стратегия |
|----------|-----------|
| Указан ключ | `hash(key) % numPartitions` (по умолчанию `Murmur2`) |
| Нет ключа (Kafka < 2.4) | Round-robin |
| Нет ключа (Kafka ≥ 2.4) | Sticky partitioning (в рамках batch) |
| Указан partition явно | Используется указанная партиция |
| Custom Partitioner | Реализация интерфейса `Partitioner` |

```java
public class OrderPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        // Своя логика распределения
        return Math.abs(key.hashCode()) % partitions.size();
    }
}
```

## Q10. (!) Что такое `Producer` и как он работает?

`Producer` — компонент, отправляющий сообщения в топики Kafka. Внутренняя архитектура producer'а:

```mermaid
graph LR
    App[Application] -->|send| S[Serializer]
    S --> P[Partitioner]
    P --> RB[RecordAccumulator<br/>батчи по партициям]
    RB -->|batch.size / linger.ms| Sender[Sender Thread]
    Sender -->|Network I/O| B[Broker]
    B -->|ack| Sender
```

**Ключевые параметры:**

| Параметр | Описание | Рекомендация |
|----------|----------|--------------|
| `acks` | Кол-во подтверждений | `all` для надёжности |
| `retries` | Число повторов | `Integer.MAX_VALUE` |
| `batch.size` | Размер батча (bytes) | 16384–65536 |
| `linger.ms` | Задержка перед отправкой | 5–100 |
| `compression.type` | Сжатие | `lz4` или `zstd` |
| `enable.idempotence` | Идемпотентность | `true` (по умолчанию c Kafka 3.0) |

## Q11. (!) Что означает параметр `acks` у `Producer`?

Параметр `acks` определяет, сколько брокеров должны подтвердить запись, прежде чем producer получит ответ:

| Значение | Поведение | Надёжность | Задержка |
|----------|-----------|------------|----------|
| `acks=0` | Не ждёт подтверждения | Минимальная (fire-and-forget) | Минимальная |
| `acks=1` | Ждёт подтверждения от Leader | Средняя (потеря при сбое Leader) | Средняя |
| `acks=all` (или `-1`) | Ждёт подтверждения от всех ISR | Максимальная | Максимальная |

**Рекомендация для production:** `acks=all` + `min.insync.replicas=2` + `replication.factor=3`.

Это гарантирует, что даже при падении одного брокера данные не потеряются.

## Q12. Что такое идемпотентный `Producer`?

Идемпотентный producer гарантирует, что повторная отправка сообщения (retry) не создаст дубликат. Kafka присваивает каждому producer уникальный `Producer ID` (PID) и отслеживает sequence number для каждой партиции.

```java
// Включение идемпотентности
Properties props = new Properties();
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // по умолчанию true с Kafka 3.0
props.put(ProducerConfig.ACKS_CONFIG, "all");              // обязательно для идемпотентности
props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
```

**Как это работает:**
1. Producer получает `PID` при инициализации
2. Каждое сообщение получает `sequence number`
3. Broker отклоняет дубликаты (одинаковые PID + sequence)
4. Гарантируется порядок даже при `max.in.flight.requests.per.connection = 5`

## Q13. Как настроить батчинг и сжатие у `Producer`?

**Батчинг** — Producer накапливает сообщения в буфере и отправляет их пачками:

```java
props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);     // 32 KB
props.put(ProducerConfig.LINGER_MS_CONFIG, 20);          // ждать до 20 мс
props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // 64 MB буфер
```

**Сжатие** — уменьшает объём данных при передаче и хранении:

| Алгоритм | CPU | Степень сжатия | Скорость |
|----------|-----|----------------|----------|
| `none` | — | — | Максимальная |
| `gzip` | Высокая | Лучшая | Медленная |
| `snappy` | Низкая | Хорошая | Быстрая |
| `lz4` | Низкая | Хорошая | Очень быстрая |
| `zstd` | Средняя | Отличная | Быстрая |

**Рекомендация:** `lz4` для баланса скорости и сжатия, `zstd` для максимального сжатия.

## Q14. (!) Что такое `Consumer` и `Consumer Group`?

`Consumer` читает сообщения из партиций топика. `Consumer Group` — группа потребителей с общим `group.id`, которые **совместно** читают из топика. Каждая партиция назначается **ровно одному** consumer внутри группы.

```mermaid
graph TB
    subgraph "Topic: orders (4 partitions)"
        P0[Partition 0]
        P1[Partition 1]
        P2[Partition 2]
        P3[Partition 3]
    end
    subgraph "Consumer Group A"
        CA1[Consumer 1<br/>P0, P1]
        CA2[Consumer 2<br/>P2, P3]
    end
    subgraph "Consumer Group B"
        CB1[Consumer 1<br/>P0, P1, P2, P3]
    end
    P0 --> CA1
    P1 --> CA1
    P2 --> CA2
    P3 --> CA2
    P0 -.-> CB1
    P1 -.-> CB1
    P2 -.-> CB1
    P3 -.-> CB1
```

**Правила:**
- Если consumers > partitions → лишние consumers простаивают
- Если consumers < partitions → один consumer обрабатывает несколько партиций
- Разные `Consumer Group` читают **независимо** (каждая получает все сообщения)

## Q15. (!) Как происходит `Rebalance`?

`Rebalance` — процесс перераспределения партиций между потребителями группы. Запускается при:
- Добавлении / удалении consumer из группы
- Подписке consumer на новый топик
- Добавлении партиций в топик
- Превышении `session.timeout.ms` / `max.poll.interval.ms`

**Протоколы rebalance:**

| Протокол | Описание | Плюсы | Минусы |
|----------|----------|-------|--------|
| Eager | Все consumer отпускают все партиции, затем переназначение | Простота | Stop-the-world |
| Cooperative (Incremental) | Постепенный rebalance, отпускаются только перемещаемые партиции | Минимальный downtime | Сложнее |

**Стратегии назначения (Assignor):**
- `RangeAssignor` — по диапазонам (по умолчанию)
- `RoundRobinAssignor` — равномерное распределение
- `StickyAssignor` — минимизация перемещений
- `CooperativeStickyAssignor` — cooperative + sticky (рекомендуемый)

## Q16. В чём разница между auto-commit и manual commit?

| Аспект | Auto-commit | Manual commit |
|--------|-------------|---------------|
| Настройка | `enable.auto.commit=true` | `enable.auto.commit=false` |
| Когда коммит | По таймеру (`auto.commit.interval.ms`) | Вызовом `commitSync()` / `commitAsync()` |
| Гарантия | At-most-once (может потерять) | At-least-once (при коммите после обработки) |
| Контроль | Минимальный | Полный |

```java
// Manual commit — at-least-once
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        processRecord(record);
    }
    consumer.commitSync(); // коммит после успешной обработки
}
```

## Q17. Что такое `Cooperative Sticky Assignor`?

`CooperativeStickyAssignor` — стратегия назначения партиций, которая:
1. Минимизирует количество перемещений партиций при rebalance
2. Использует cooperative протокол — consumer не отпускает партиции, которые остаются за ним
3. Результат: **zero downtime rebalance** для стабильных partition assignments

```yaml
# application.yml для Spring Kafka
spring:
  kafka:
    consumer:
      properties:
        partition.assignment.strategy: org.apache.kafka.clients.consumer.CooperativeStickyAssignor
```

## Q18. (!) Как работает репликация в `Kafka`?

Каждая партиция реплицируется на несколько брокеров. Одна реплика — `Leader`, остальные — `Followers`.

```mermaid
graph LR
    subgraph "Partition 0 (replication-factor=3)"
        L["Broker 1<br/>Leader<br/>offset: 0-100"]
        F1["Broker 2<br/>Follower (ISR)<br/>offset: 0-99"]
        F2["Broker 3<br/>Follower (ISR)<br/>offset: 0-98"]
    end
    Producer -->|write| L
    L -->|replicate| F1
    L -->|replicate| F2
    L -->|read| Consumer
```

**Правила:**
- **Только Leader** обслуживает запись и чтение (Kafka < 2.4; с 2.4 followers могут обслуживать чтение при настройке `replica.selector.class`)
- Followers постоянно fetch'ат данные у Leader
- Если Follower отстаёт — он выпадает из ISR

## Q19. (!) Что такое `ISR`, `OSR` и `High Watermark`?

**`ISR` (In-Sync Replicas)** — набор реплик, которые полностью синхронизированы с Leader. Kafka считает сообщение committed, когда оно записано на все ISR.

**`OSR` (Out-of-Sync Replicas)** — реплики, отставшие от Leader более чем на `replica.lag.time.max.ms` (по умолчанию 30 сек).

**`High Watermark (HW)`** — offset последнего сообщения, реплицированного на все ISR. Consumer может читать только до HW.

```
Leader:     [0] [1] [2] [3] [4] [5] [6]    ← LEO = 7
Follower 1: [0] [1] [2] [3] [4] [5]        ← LEO = 6 (ISR)
Follower 2: [0] [1] [2] [3]                ← LEO = 4 (OSR — отстал)
                              ↑
                        High Watermark = 6
```

**`min.insync.replicas`** — минимальное число ISR для принятия записи при `acks=all`. Если ISR < `min.insync.replicas`, broker вернёт `NotEnoughReplicasException`.

## Q20. Что произойдёт при падении Leader-брокера?

1. `Controller` обнаруживает, что брокер недоступен (через heartbeat)
2. Controller выбирает нового Leader из ISR
3. Метаданные обновляются в metadata log (KRaft) или ZooKeeper
4. Producers и Consumers получают обновлённые метаданные и переключаются на нового Leader
5. Если ISR пуст, поведение зависит от `unclean.leader.election.enable`

**Время восстановления** (failover) обычно составляет несколько секунд.

## Q21. Что такое `Unclean Leader Election`?

`Unclean Leader Election` — выбор нового Leader из реплик, **не входящих в ISR** (т.е. не полностью синхронизированных). Управляется параметром `unclean.leader.election.enable`.

| Значение | Поведение |
|----------|-----------|
| `true` | Допускается выбор отставшей реплики → доступность выше, но **возможна потеря данных** |
| `false` (по умолчанию) | Партиция остаётся недоступной, пока ISR-реплика не вернётся → **данные не теряются** |

**Рекомендация:** `false` для финансовых и критичных данных, `true` для систем, где доступность важнее целостности.

## Q22. (!) Какие семантики доставки поддерживает `Kafka`?

| Семантика | Описание | Реализация |
|-----------|----------|------------|
| **At-most-once** | Сообщение может потеряться, но не дублируется | `acks=0` или auto-commit до обработки |
| **At-least-once** | Сообщение не теряется, но может дублироваться | `acks=all` + commit после обработки |
| **Exactly-once** | Сообщение доставлено и обработано ровно один раз | Идемпотентный producer + транзакции |

**Выбор семантики** зависит от требований:
- Метрики, логи → `at-most-once` (допустимы потери)
- Заказы, платежи → `at-least-once` + идемпотентная обработка на стороне consumer
- Финансовые транзакции → `exactly-once` (Kafka Transactions)

## Q23. (!) Как реализовать `Exactly-Once` семантику?

`Exactly-once` в Kafka реализуется через комбинацию:
1. **Идемпотентный Producer** (`enable.idempotence=true`) — предотвращает дубликаты на стороне producer
2. **Транзакционный Producer** (`transactional.id`) — атомарные операции чтения-обработки-записи
3. **Consumer с `isolation.level=read_committed`** — читает только committed сообщения

```java
// Транзакционный producer — exactly-once
Properties props = new Properties();
props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-processor-1");
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();

try {
    producer.beginTransaction();
    producer.send(new ProducerRecord<>("output-topic", key, value));
    // Коммит consumer offsets внутри транзакции
    producer.sendOffsetsToTransaction(offsets, consumerGroupMetadata);
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

**Ограничения:** Exactly-once работает только **внутри Kafka** (read-process-write). Для внешних систем нужна идемпотентность на стороне приёмника.

## Q24. Что такое транзакции в `Kafka`?

Транзакции позволяют атомарно записывать сообщения в несколько топиков/партиций и коммитить consumer offsets.

**Компоненты:**
- `Transaction Coordinator` — брокер, управляющий транзакцией
- `__transaction_state` — внутренний топик для хранения состояния транзакций
- `transactional.id` — уникальный идентификатор producer'а (сохраняется при рестартах)

**Жизненный цикл:**
1. `initTransactions()` — регистрация в координаторе
2. `beginTransaction()` — начало транзакции
3. `send()` — отправка сообщений
4. `sendOffsetsToTransaction()` — привязка consumer offsets
5. `commitTransaction()` / `abortTransaction()` — завершение

## Q25. (!) Как интегрировать `Kafka` со `Spring Boot`?

**Зависимость:**
```groovy
// build.gradle
implementation 'org.springframework.kafka:spring-kafka'
```

**Минимальная конфигурация** в `application.yml`:
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: my-service
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.myapp.events"
```

**Основные абстракции Spring Kafka:**
- `KafkaTemplate` — отправка сообщений (обёртка над `KafkaProducer`)
- `@KafkaListener` — аннотация для consumer'ов
- `ProducerFactory` / `ConsumerFactory` — фабрики для конфигурации
- `KafkaListenerContainerFactory` — управление контейнером listener'ов

## Q26. Как настроить `Producer` в `Spring Kafka`?

```java
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, OrderEvent>> send(OrderEvent event) {
        return kafkaTemplate.send("orders", event.getOrderId(), event);
    }
}
```

## Q27. (!) Как настроить `Consumer` с `@KafkaListener`?

```java
@Component
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(
        topics = "orders",
        groupId = "order-processor",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received order {} from partition {} at offset {}",
                event.getOrderId(), partition, offset);
        processOrder(event);
    }

    // Batch listener
    @KafkaListener(topics = "events", groupId = "batch-processor")
    public void consumeBatch(List<ConsumerRecord<String, String>> records) {
        log.info("Received batch of {} records", records.size());
        records.forEach(this::processRecord);
    }
}
```

**Настройка `ConsumerFactory`:**
```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, OrderEvent>
        kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(3); // количество потоков
    factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
    return factory;
}
```

## Q28. Как обрабатывать ошибки в `Spring Kafka`?

**`DefaultErrorHandler`** (Spring Kafka 2.8+) — стандартный обработчик ошибок:

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
    // Retry 3 раза с backoff 1 секунда
    var backoff = new FixedBackOff(1000L, 3);

    // Dead Letter Topic для необрабатываемых сообщений
    var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

    var handler = new DefaultErrorHandler(recoverer, backoff);

    // Не ретраить для определённых исключений
    handler.addNotRetryableExceptions(
        DeserializationException.class,
        ValidationException.class
    );

    return handler;
}
```

**Стратегии обработки ошибок:**
- **Retry** — повторная обработка с backoff
- **DLT (Dead Letter Topic)** — отправка в отдельный топик для анализа
- **Skip** — пропуск проблемного сообщения
- **Stop** — остановка consumer

## Q29. Как тестировать `Kafka` в `Spring Boot`?

Два подхода:

**1. `EmbeddedKafka`** (spring-kafka-test):
```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"orders"})
class OrderEventConsumerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldConsumeOrderEvent() throws Exception {
        kafkaTemplate.send("orders", "order-1", "{\"id\": \"order-1\"}");

        // Verify consumer received the message
        await().atMost(10, TimeUnit.SECONDS)
               .untilAsserted(() -> verify(orderService).processOrder(any()));
    }
}
```

**2. `Testcontainers`** (более production-like):
```java
@SpringBootTest
@Testcontainers
class KafkaIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
    );

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

## Q30. (!) Что такое `Kafka Streams`?

`Kafka Streams` — клиентская библиотека для потоковой обработки данных, встроенная в Kafka. В отличие от `Apache Flink` или `Spark Streaming`, не требует отдельного кластера — работает как обычное Java-приложение.

**Ключевые особенности:**
- **Без внешней инфраструктуры** — только Kafka
- **Exactly-once** обработка из коробки
- **Stateful** операции с локальным хранилищем (`RocksDB`)
- **Горизонтальное масштабирование** — запускаем больше инстансов
- **Fault tolerance** — state-store реплицируется через changelog-топики

| Kafka Consumer API | Kafka Streams |
|-------------------|---------------|
| Низкоуровневый poll-loop | Высокоуровневый DSL |
| Stateless | Stateful (KTable, state stores) |
| Ручное управление offset | Автоматическое |
| Нет join/aggregate | Полноценные join, aggregate, windowing |

## Q31. Какие основные абстракции в `Kafka Streams`?

- **`KStream`** — бесконечный поток записей (insert-only)
- **`KTable`** — changelog-таблица (upsert по ключу)
- **`GlobalKTable`** — KTable, реплицированная на все инстансы
- **`State Store`** — локальное хранилище состояния (по умолчанию RocksDB)
- **Windowing** — группировка по времени (`Tumbling`, `Hopping`, `Sliding`, `Session`)

```java
StreamsBuilder builder = new StreamsBuilder();

KStream<String, OrderEvent> orders = builder.stream("orders");

// Фильтрация + трансформация
KStream<String, OrderEvent> highValue = orders
    .filter((key, order) -> order.getAmount() > 1000)
    .mapValues(order -> order.withStatus("HIGH_PRIORITY"));

// Агрегация — подсчёт заказов по клиенту
KTable<String, Long> orderCounts = orders
    .groupBy((key, order) -> order.getCustomerId())
    .count(Materialized.as("order-counts-store"));

// Запись результатов
highValue.to("high-value-orders");
orderCounts.toStream().to("order-statistics");
```

## Q32. Как использовать `Kafka Streams` в `Spring Boot`?

```java
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-stream-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream("input-topic");

        stream.filter((key, value) -> value != null && !value.isEmpty())
              .mapValues(value -> value.toUpperCase())
              .to("output-topic");

        return stream;
    }
}
```

## Q33. Что такое `Kafka Connect`?

`Kafka Connect` — фреймворк для интеграции Kafka с внешними системами без написания кода. Работает через **коннекторы** — плагины для чтения/записи данных.

**Режимы работы:**
- **Standalone** — один процесс (для разработки)
- **Distributed** — кластер воркеров (для production)

**Популярные коннекторы:**
- `Debezium` — CDC из баз данных (`PostgreSQL`, `MySQL`, `MongoDB`)
- `JDBC Source/Sink` — чтение/запись через JDBC
- `Elasticsearch Sink` — индексация в [Elasticsearch](../databases/elasticsearch-interview.md)
- `S3 Sink` — сохранение в S3
- `FileStream` — работа с файлами

## Q34. В чём разница между `Source` и `Sink Connector`?

| Тип | Направление | Пример |
|-----|------------|--------|
| **Source Connector** | Внешняя система → Kafka | Debezium CDC, JDBC Source |
| **Sink Connector** | Kafka → Внешняя система | Elasticsearch Sink, S3 Sink |

```mermaid
graph LR
    DB[(PostgreSQL)] -->|Debezium Source| KC[Kafka Connect]
    KC -->|topic: db.orders| K[Kafka]
    K -->|Elasticsearch Sink| ES[(Elasticsearch)]
    K -->|S3 Sink| S3[(Amazon S3)]
```

## Q35. (!) Зачем нужен `Schema Registry`?

`Schema Registry` — сервис для хранения и валидации схем данных (`Avro`, `Protobuf`, `JSON Schema`). Решает проблему совместимости между producer'ами и consumer'ами при эволюции схемы.

**Режимы совместимости:**
- `BACKWARD` — новая схема может читать данные старой (можно удалять поля, добавлять optional)
- `FORWARD` — старая схема может читать данные новой (можно добавлять поля, удалять optional)
- `FULL` — обе стороны совместимы
- `NONE` — без проверок

**Зачем на собеседовании:** показывает понимание проблем эволюции контрактов в [event-driven архитектуре](../architecture/event-driven-patterns-interview.md).

## Q36. (!) Что такое `Consumer Lag` и как его измерить?

`Consumer Lag` — разница между последним записанным offset (LEO) и последним committed offset consumer'а. Показывает, насколько consumer отстаёт от producer'а.

```
LEO (Log End Offset):        150
Committed Offset:            120
Consumer Lag:                 30 сообщений
```

**Способы измерения:**
1. **CLI:** `kafka-consumer-groups.sh --describe --group my-group`
2. **JMX-метрики:** `kafka.consumer:type=consumer-fetch-manager-metrics,client-id=*`
3. **Burrow** — LinkedIn tool для мониторинга lag
4. **Prometheus + kafka-exporter** — экспорт метрик для [Grafana](../monitoring/metrics-tracing-interview.md)

**Что делать при растущем lag:**
- Увеличить количество consumer'ов (до числа партиций)
- Оптимизировать обработку сообщений
- Увеличить `max.poll.records`
- Проверить backpressure и GC-паузы

## Q37. Какие ключевые метрики `Kafka` нужно мониторить?

| Метрика | Что показывает | Порог тревоги |
|---------|---------------|---------------|
| Consumer Lag | Отставание consumer'а | Растёт > 5 мин |
| `UnderReplicatedPartitions` | Партиции с недостаточной репликацией | > 0 |
| `ActiveControllerCount` | Количество активных контроллеров | ≠ 1 |
| `OfflinePartitionsCount` | Недоступные партиции | > 0 |
| Request latency (p99) | Задержка запросов | > 100 мс |
| `BytesInPerSec` / `BytesOutPerSec` | Throughput брокера | Зависит от SLA |
| `ISRShrinks` / `ISRExpands` | Изменения ISR | Частые shrink = проблема |
| JVM Heap Usage | Память брокера | > 80% |

**Инструменты мониторинга:**
- `Prometheus` + `Grafana` + `kafka-exporter`
- `Confluent Control Center`
- `AKHQ` / `Kafka UI` / `Kafdrop`

## Q38. Как масштабировать `Kafka`-кластер?

**Горизонтальное масштабирование:**
1. **Добавление брокеров** — увеличивает ёмкость, но требует reassignment партиций
2. **Увеличение партиций** — повышает параллелизм consumer'ов
3. **Добавление consumer'ов** — до числа партиций

**Вертикальное масштабирование:**
- Увеличение RAM (page cache)
- Быстрые SSD-диски
- Увеличение сетевой пропускной способности

```bash
# Reassignment партиций на новые брокеры
kafka-reassign-partitions.sh --bootstrap-server localhost:9092 \
  --reassignment-json-file reassignment.json \
  --execute
```

**Ограничения:**
- Партиции нельзя уменьшить
- Reassignment — ресурсоёмкий процесс (throttle через `--throttle`)
- Больше партиций → дольше rebalance и leader election

## Q39. Как обеспечить безопасность в `Kafka`?

| Уровень | Механизм | Описание |
|---------|----------|----------|
| Транспорт | `SSL/TLS` | Шифрование данных в пути |
| Аутентификация | `SASL` (PLAIN, SCRAM, GSSAPI, OAUTHBEARER) | Проверка личности клиента |
| Авторизация | `ACL` | Контроль доступа к топикам/группам |
| Шифрование данных | Encryption at rest | Шифрование на уровне дисков |

```yaml
# Spring Boot — SSL + SASL
spring:
  kafka:
    properties:
      security.protocol: SASL_SSL
      sasl.mechanism: SCRAM-SHA-256
      sasl.jaas.config: >
        org.apache.kafka.common.security.scram.ScramLoginModule required
        username="myuser" password="mypassword";
    ssl:
      trust-store-location: classpath:kafka.truststore.jks
      trust-store-password: changeit
```

## Q40. (!) Что такое `Dead Letter Queue` (DLQ) в `Kafka`?

`Dead Letter Queue` (DLQ) или `Dead Letter Topic` (DLT) — специальный топик, куда отправляются сообщения, которые не удалось обработать после всех попыток retry.

```mermaid
graph LR
    T[Topic: orders] --> C[Consumer]
    C -->|success| DB[(Database)]
    C -->|retry 1,2,3 fail| DLT[orders.DLT]
    DLT --> Alert[Alerting / Manual Review]
```

**Реализация в Spring Kafka:**
```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
    var recoverer = new DeadLetterPublishingRecoverer(template);
    return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
}
```

**Best practices:**
- Сохранять оригинальные headers (topic, partition, offset, exception)
- Мониторить DLT — алерт при появлении сообщений
- Реализовать механизм replay из DLT

## Q41. (!) Как спроектировать правильную топологию топиков?

**Принципы проектирования:**

1. **Один event-type — один топик** (не мешать разные события в один топик)
2. **Naming convention:** `<domain>.<entity>.<event>`, например `shop.order.created`
3. **Ключ сообщения = entity ID** для порядка и партиционирования
4. **Retention** — подбирается под SLA (7 дней для обычных, бесконечный для event-sourcing)
5. **Compacted topics** — для справочников и current state

**Анти-паттерны:**
- Один «mega-topic» для всех событий
- Использование topic-per-consumer
- Частая смена числа партиций
- Отсутствие schema evolution стратегии

## Q42. В чём разница между `Kafka` и `RabbitMQ`?

| Аспект | Kafka | RabbitMQ |
|--------|-------|----------|
| Модель | Distributed commit log | Message broker (AMQP) |
| Хранение | На диске, retention-based | Удаление после ACK |
| Порядок | В пределах партиции | В пределах очереди |
| Throughput | Очень высокий (млн msg/sec) | Средний (тыс. msg/sec) |
| Consumer model | Pull (poll) | Push + Pull |
| Replay | Да (перемотка offset) | Нет (сообщение удаляется) |
| Routing | По ключу → партиция | Exchange → routing key → queue |
| Протокол | Kafka Protocol (binary) | AMQP, MQTT, STOMP |
| Use case | Event streaming, data pipeline | Task queue, RPC, routing |

**Когда Kafka:** высокий throughput, event-sourcing, data pipeline, replay нужен.
**Когда RabbitMQ:** task queues, сложный routing, request-reply, низкие объёмы.

## Q43. Какие паттерны retry применяются с `Kafka`?

**1. Blocking Retry** — повторная обработка в том же consumer'е:
```java
@RetryableTopic(
    attempts = "3",
    backoff = @Backoff(delay = 1000, multiplier = 2),
    dltStrategy = DltStrategy.FAIL_ON_ERROR
)
@KafkaListener(topics = "orders")
public void consume(OrderEvent event) {
    processOrder(event); // может бросить исключение
}
```

**2. Non-Blocking Retry (Retry Topics)** — Spring Kafka создаёт отдельные топики для каждой попытки:
```
orders → orders-retry-0 → orders-retry-1 → orders-DLT
```

**3. Manual Retry** — отправка в retry-топик с задержкой:
- Используйте `ScheduledExecutorService` или Kafka timestamp для delayed retry

**Рекомендация:** `@RetryableTopic` в Spring Kafka — самый удобный вариант для non-blocking retry.

## Q44. (!) Какие практические советы для production-использования `Kafka`?

**Конфигурация кластера:**
- `replication.factor = 3`, `min.insync.replicas = 2`
- Минимум 3 брокера (лучше нечётное число)
- Выделенные диски для Kafka log (не shared с OS)
- Page cache — основной механизм производительности, выделяйте RAM

**Producer:**
- `acks=all` + `enable.idempotence=true`
- Используйте сжатие (`lz4` / `zstd`)
- Всегда указывайте ключ для порядка

**Consumer:**
- `enable.auto.commit=false` + manual commit после обработки
- `CooperativeStickyAssignor` для минимизации rebalance
- Настройте DLT для необрабатываемых сообщений
- `max.poll.records` и `max.poll.interval.ms` под ваш SLA

**Мониторинг:**
- Consumer lag — главная метрика
- `UnderReplicatedPartitions` → алерт
- Мониторьте JVM-метрики брокеров
- Используйте [distributed tracing](../monitoring/observability-interview.md) с `spring-kafka` + `Micrometer`

**Операции:**
- Автоматизируйте partition reassignment при добавлении брокеров
- Тестируйте failover-сценарии
- Используйте `KRaft` (Kafka 4.0+) — избавьтесь от ZooKeeper
- Schema Registry для эволюции контрактов

## Q45. (!) Как работает `Consumer Group Rebalancing` и что такое `Static Membership`?

**Consumer Group Rebalancing** — процесс перераспределения партиций между потребителями в группе. Происходит при:
- Добавлении нового consumer в группу
- Падении или уходе consumer (heartbeat timeout)
- Изменении числа партиций топика
- Изменении подписки consumer-а

**Фазы Rebalance (Eager Rebalance — старый алгоритм):**

```mermaid
sequenceDiagram
    participant C1 as Consumer 1
    participant C2 as Consumer 2
    participant GC as Group Coordinator

    C1->>GC: JoinGroup Request
    C2->>GC: JoinGroup Request
    GC-->>C1: JoinGroup Response (leader)
    GC-->>C2: JoinGroup Response (follower)
    C1->>GC: SyncGroup (с assignment)
    C2->>GC: SyncGroup
    GC-->>C1: SyncGroup Response (partitions)
    GC-->>C2: SyncGroup Response (partitions)
    Note over C1,C2: Все остановили обработку на время rebalance!
```

**Eager Rebalance проблема:** все consumers останавливают обработку и освобождают партиции. "Stop-the-world" для всей группы.

**Cooperative Sticky Rebalance (Incremental Rebalance):**
- Только затронутые партиции перераспределяются
- Незатронутые consumers продолжают обработку
- Конфигурация: `partition.assignment.strategy=CooperativeStickyAssignor`

**Static Membership (`group.instance.id`):**

```yaml
spring:
  kafka:
    consumer:
      group-id: my-group
      properties:
        group.instance.id: "consumer-instance-1"  # уникальный статический ID
        session.timeout.ms: 60000   # даём больше времени на рестарт
```

При использовании статического ID:
- Consumer при перезапуске **переиспользует** предыдущий membership
- Rebalance не происходит если consumer вернулся до `session.timeout.ms`
- Критично для stateful Kafka Streams приложений (сохраняет state store assignment)

**Параметры влияющие на rebalance:**

| Параметр | Описание | Типичное значение |
|----------|----------|-------------------|
| `heartbeat.interval.ms` | Частота heartbeat consumer-а | 3000 |
| `session.timeout.ms` | Таймаут до объявления consumer мёртвым | 30000–60000 |
| `max.poll.interval.ms` | Макс. время между poll() вызовами | 300000 (5 мин) |
| `group.instance.id` | Статический ID для Static Membership | Имя pod/instance |

## Q46. (!) Что такое `Log Compaction` и как работает `__consumer_offsets`?

**Log Compaction** — механизм очистки Kafka, при котором сохраняется только **последнее значение для каждого ключа**. В отличие от `retention.ms` (удаление по времени), compaction удаляет старые версии по ключу.

```
До compaction:
[K1:v1] [K2:v1] [K1:v2] [K3:v1] [K2:v2] [K1:v3]

После compaction:
[K2:v2] [K3:v1] [K1:v3]
```

**Настройка:**

```bash
# Создать топик с compaction
kafka-topics.sh --create \
  --topic user-profiles \
  --config cleanup.policy=compact \
  --config min.cleanable.dirty.ratio=0.5 \
  --config segment.ms=3600000

# Tombstone: null-value удаляет ключ из compacted log
kafka-console-producer.sh --topic user-profiles \
  --property "parse.key=true" \
  --property "key.separator=:" <<< "user:123:null"
```

**Применение:**
- `Event Sourcing` snapshot — только последнее состояние объекта
- Справочники (конфигурации, курсы валют) — всегда актуальное значение
- CDC (Change Data Capture) — полный snapshot + дельта изменений

**`__consumer_offsets` топик:**

Специальный внутренний топик Kafka для хранения committed offsets всех Consumer Groups.

```
Topic: __consumer_offsets
Partitions: 50 (по умолчанию)
Cleanup policy: compact

Ключ: [group_id, topic, partition]
Значение: [offset, metadata, timestamp]
```

**Как Kafka определяет партицию для группы:**

```
partition = hash(group_id) % 50
```

Coordinator брокер — тот, кто является лидером соответствующей партиции `__consumer_offsets`.

```bash
# Просмотр committed offsets напрямую
kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group my-group \
  --describe

# GROUP  TOPIC     PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
# my-grp orders    0          1500            1510            10
# my-grp orders    1          2300            2300            0
```

**Compaction для `__consumer_offsets`:** при commit нового offset старый удаляется из топика — хранится только актуальный offset для каждой `(group, topic, partition)` тройки.

## Q47. Как `Schema Registry` хранит схемы и обеспечивает совместимость?

`Schema Registry` (Confluent) — централизованное хранилище схем сообщений для обеспечения контрактной совместимости между продюсерами и консьюмерами.

**Архитектура:**

```mermaid
graph LR
    Producer["Producer\n(Java)"] -->|"1. Регистрировать схему"| SR["Schema Registry"]
    SR -->|"2. Вернуть schema_id"| Producer
    Producer -->|"3. [magic_byte][schema_id][avro_bytes]"| Kafka["Kafka Topic"]
    Kafka --> Consumer["Consumer\n(Java)"]
    Consumer -->|"4. Запросить схему по schema_id"| SR
    SR -->|"5. Вернуть схему"| Consumer
    Consumer -->|"6. Десериализовать"| Consumer
```

**Формат сообщения с Avro:**

```
Bytes: [0x00][schema_id 4 bytes][avro encoded payload]
```

**Регистрация схемы:**

```java
// Maven: confluent avro-serializer
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
</dependency>

// application.yml
spring:
  kafka:
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    consumer:
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
    properties:
      schema.registry.url: http://schema-registry:8081
      specific.avro.reader: true
```

**Уровни совместимости:**

| Уровень | Описание | Пример допустимого изменения |
|---------|----------|------------------------------|
| `BACKWARD` | Новая схема читает данные старой | Добавить поле с default |
| `FORWARD` | Старая схема читает данные новой | Удалить опциональное поле |
| `FULL` | Оба направления | Только безопасные изменения |
| `NONE` | Без проверок | Любые изменения |
| `BACKWARD_TRANSITIVE` | Backward относительно всех версий | Строгий контроль |

```bash
# Установить совместимость для субъекта
curl -X PUT http://schema-registry:8081/config/orders-value \
  -H "Content-Type: application/json" \
  -d '{"compatibility": "BACKWARD"}'

# Проверить совместимость перед деплоем
curl -X POST http://schema-registry:8081/compatibility/subjects/orders-value/versions/latest \
  -H "Content-Type: application/json" \
  -d '{"schema": "{\"type\":\"record\",\"name\":\"Order\",...}"}'
```

**Именование субъектов:**
- `TopicNameStrategy` (default): `<topic>-key`, `<topic>-value`
- `RecordNameStrategy`: по имени Avro record
- `TopicRecordNameStrategy`: `<topic>-<record_name>`

## Q48. (!) Как реализовать `Exactly-Once` с помощью транзакций `Kafka` и `idempotent producer`?

**Три уровня гарантий:**

| Уровень | Конфигурация | Риск |
|---------|-------------|------|
| At-most-once | `acks=0`, auto-commit | Потеря сообщений |
| At-least-once | `acks=all`, manual commit | Дубликаты |
| Exactly-once | Транзакции + `enable.idempotence` | Overhead |

**Idempotent Producer:**

```yaml
spring:
  kafka:
    producer:
      properties:
        enable.idempotence: true   # автоматически: acks=all, retries=MAX_INT
        max.in.flight.requests.per.connection: 5  # до 5 in-flight
```

Брокер присваивает Producer `PID` (Producer ID) и `sequence number`. Дублированные записи с одинаковым PID+sequence отвергаются.

**Транзакционный Producer (Exactly-Once Semantics):**

```java
// Конфигурация
props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transactional-id");
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
producer.initTransactions();

try {
    producer.beginTransaction();

    // Читаем из одного топика, пишем в другой (consume-transform-produce)
    producer.send(new ProducerRecord<>("output-topic", key, processedValue));

    // Commit offsets как часть транзакции
    producer.sendOffsetsToTransaction(currentOffsets, consumerGroupMetadata);

    producer.commitTransaction();
} catch (ProducerFencedException | OutOfOrderSequenceException e) {
    producer.close(); // non-recoverable
} catch (KafkaException e) {
    producer.abortTransaction();
}
```

**Consumer-side: читать только committed:**

```yaml
spring:
  kafka:
    consumer:
      properties:
        isolation.level: read_committed  # пропускать uncommitted записи
```

**EOS в Kafka Streams:**

```yaml
spring:
  kafka:
    streams:
      properties:
        processing.guarantee: exactly_once_v2  # Kafka 2.5+
```

`exactly_once_v2` использует одну транзакцию на партицию (vs одна на task в v1) — меньше overhead.

## Q49. Что такое `Kafka Streams State Store` и как реализовать `stateful`-обработку?

**State Store** — локальное хранилище (RocksDB или in-memory), привязанное к экземпляру Kafka Streams приложения. Позволяет накапливать состояние между сообщениями.

**Типы State Store:**

| Тип | Реализация | Персистентность |
|-----|-----------|----------------|
| Persistent (default) | RocksDB | Да (changelog топик) |
| In-memory | ConcurrentHashMap | Нет |
| Versioned | RocksDB + версии | Да |

**Changelog топик:** Kafka Streams автоматически создаёт backing топик для каждого State Store с `cleanup.policy=compact`. При рестарте приложение восстанавливает state из changelog.

**Пример: подсчёт заказов по категории (aggregation):**

```java
@Bean
public KStream<String, Order> orderStream(StreamsBuilder builder) {
    // Читаем из топика
    KStream<String, Order> orders = builder.stream("orders");

    // Группируем по категории и считаем
    KTable<String, Long> ordersByCategory = orders
        .groupBy((key, order) -> order.getCategory())
        .count(Materialized.as("orders-by-category-store"));

    // Интерактивный запрос к State Store (из другого потока/REST)
    ordersByCategory.toStream().to("orders-count-output");

    return orders;
}

// REST endpoint для interactive query
@RestController
public class StateQueryController {
    @Autowired
    private KafkaStreams kafkaStreams;

    @GetMapping("/count/{category}")
    public Long getCategoryCount(@PathVariable String category) {
        ReadOnlyKeyValueStore<String, Long> store =
            kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                    "orders-by-category-store",
                    QueryableStoreTypes.keyValueStore()
                )
            );
        return store.get(category);
    }
}
```

**Windowed Aggregation:**

```java
// Скользящее окно: заказы за последние 5 минут
KTable<Windowed<String>, Long> windowedCounts = orders
    .groupBy((k, v) -> v.getCategory())
    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
    .count(Materialized.as("windowed-orders-store"));
```

**Standby Replicas:** для High Availability State Store реплицируется на другие экземпляры:

```yaml
spring:
  kafka:
    streams:
      properties:
        num.standby.replicas: 1  # 1 standby replica для каждого state store
```

## Q50. Как организовать мониторинг `__consumer_offsets` и `Consumer Lag` алертинг?

**Consumer Lag** = `log-end-offset - committed-offset` — ключевая метрика здоровья Kafka-системы.

**Инструменты мониторинга:**

```bash
# 1. kafka-consumer-groups.sh (базовый)
kafka-consumer-groups.sh \
  --bootstrap-server kafka:9092 \
  --group my-group \
  --describe

# 2. Программный мониторинг через AdminClient
AdminClient admin = AdminClient.create(props);
Map<TopicPartition, OffsetAndMetadata> offsets =
    admin.listConsumerGroupOffsets("my-group")
         .partitionsToOffsetAndMetadata().get();

// Получить log-end-offsets
Map<TopicPartition, Long> endOffsets =
    admin.listOffsets(offsets.keySet().stream()
        .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.latest())))
    .all().get()
    .entrySet().stream()
    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().offset()));

// Lag = end - committed
offsets.forEach((tp, meta) -> {
    long lag = endOffsets.get(tp) - meta.offset();
    System.out.printf("Partition %s: lag=%d%n", tp, lag);
});
```

**Micrometer + Spring Kafka метрики:**

```yaml
management:
  metrics:
    tags:
      application: my-service
```

Метрики доступны автоматически:
- `kafka.consumer.fetch.manager.records.lag` — текущий lag
- `kafka.consumer.fetch.manager.records.lag.max` — максимальный lag
- `kafka.producer.record.send.rate` — скорость отправки

**Алертинг (пример для Victoria Metrics / Prometheus):**

```yaml
# PrometheusRule / alertmanager rule
groups:
  - name: kafka-consumer-lag
    rules:
      - alert: KafkaConsumerHighLag
        expr: kafka_consumer_fetch_manager_records_lag_max > 10000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Consumer lag высокий ({{ $value }} messages)"

      - alert: KafkaConsumerCriticalLag
        expr: kafka_consumer_fetch_manager_records_lag_max > 100000
        for: 2m
        labels:
          severity: critical
```

**AKHQ** — веб-интерфейс для просмотра лага в реальном времени: `http://akhq.<stand>.detmir-infra.ru`

**Причины роста лага:**
1. Consumer слишком медленно обрабатывает — увеличьте параллелизм (больше partition + consumer-инстансов)
2. `max.poll.records` слишком велик — уменьшите или оптимизируйте обработку
3. Downstream-зависимость (БД, внешний API) — circuit breaker, async processing
4. GC паузы в JVM — настройка heap и GC-алгоритма

---

## See also

- [Распределённые системы](../architecture/distributed-systems-interview.md) — CAP, репликация, отказоустойчивость, консенсус
- [Event-driven паттерны](../architecture/event-driven-patterns-interview.md) — Outbox, Saga, Event Sourcing, CQRS
- [Микросервисы](../architecture/microservices-interview.md) — асинхронная коммуникация, сервисная шина событий
- [Spring Cloud](../frameworks/spring/spring-cloud-interview.md) — Spring Cloud Stream, интеграция с Kafka в облачных приложениях
- [Стратегии кэширования](../architecture/caching-strategies-interview.md) — взаимодействие кэша и event-driven пайплайнов
- [CQRS и Event Sourcing](../architecture/cqrs-event-sourcing-interview.md) — применение Kafka как журнала событий в event sourcing
- [RxJava / Reactive](../reactive/rxjava-interview.md) — сравнение реактивных потоков и Kafka Streams
- [Kubernetes](../devops/kubernetes-interview.md) — деплой Kafka в Kubernetes, Strimzi Operator
- [Docker и контейнеризация](../devops/docker-interview.md) — контейнеризация Kafka-брокеров

- [AWS SQS и SNS](aws-sqs-sns-interview.md)
- [Сравнение Message Brokers](message-brokers-comparison-interview.md)
- [NATS](nats-interview.md)
- [Apache Pulsar](pulsar-interview.md)
- [RabbitMQ](rabbitmq-interview.md)
- [Redpanda](redpanda-interview.md)
- [Шпаргалка: Apache Kafka для Java](../../development/messaging/kafka/kafka.md) — теория
