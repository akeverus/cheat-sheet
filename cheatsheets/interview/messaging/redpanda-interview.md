---
title: "Вопросы на собеседовании: Redpanda"
description: "Redpanda: Kafka-compatible streaming platform на C++, no JVM, no ZooKeeper, lower latency, easier ops, Raft replication, built-in HTTP proxy, vs Kafka"
tags:
  - interview
  - messaging
  - redpanda-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Redpanda"
  - "Redpanda interview"
  - "Redpanda собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Redpanda`

`Redpanda` — Kafka-совместимая стриминговая платформа, написанная на **C++** (в отличие от Kafka на Java). Создана **Vectorized.io** (ныне Redpanda Data, 2019). **Без JVM, без ZooKeeper**, единый бинарник. Обещает **меньшую задержку** и **более простую эксплуатацию**. Использует фреймворк **Seastar** (как ScyllaDB) для архитектуры shard-per-core.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Redpanda Documentation](https://docs.redpanda.com/)
- [Redpanda GitHub](https://github.com/redpanda-data/redpanda)
- [Redpanda vs Kafka Benchmarks](https://redpanda.com/blog/redpanda-vs-kafka-performance-benchmark)
- [Redpanda Cloud](https://redpanda.com/redpanda-cloud)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/) — тот же wire-протокол

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Redpanda?](#q1--что-такое-redpanda)
- [Q2. (!) Redpanda vs Kafka — отличия?](#q2--redpanda-vs-kafka--отличия)
- [Q3. Single binary — что значит?](#q3-single-binary--что-значит)

**Архитектура**
- [Q4. (!) C++ + Seastar (shard-per-core)?](#q4--c--seastar-shard-per-core)
- [Q5. No JVM, no GC pauses?](#q5-no-jvm-no-gc-pauses)
- [Q6. (!) No ZooKeeper — Raft консенсус?](#q6--no-zookeeper--raft-консенсус)
- [Q7. Tiered storage?](#q7-tiered-storage)

**Compatibility с Kafka**
- [Q8. (!) Kafka wire protocol compatibility?](#q8--kafka-wire-protocol-compatibility)
- [Q9. Kafka clients работают?](#q9-kafka-clients-работают)
- [Q10. Schema Registry, Connect?](#q10-schema-registry-connect)

**Performance**
- [Q11. (!) Performance claims (latency, throughput)?](#q11--performance-claims-latency-throughput)
- [Q12. (!) Why faster than Kafka?](#q12--why-faster-than-kafka)

**Features**
- [Q13. WASM transforms?](#q13-wasm-transforms)
- [Q14. Built-in HTTP proxy?](#q14-built-in-http-proxy)
- [Q15. Console (UI)?](#q15-console-ui)

**Editions**
- [Q16. (!) Open source vs Enterprise vs Cloud?](#q16--open-source-vs-enterprise-vs-cloud)
- [Q17. Source available license (BSL)?](#q17-source-available-license-bsl)

**Production**
- [Q18. (!) Когда выбрать Redpanda над Kafka?](#q18--когда-выбрать-redpanda-над-kafka)
- [Q19. Когда не выбирать Redpanda?](#q19-когда-не-выбирать-redpanda)
- [Q20. Migration Kafka → Redpanda?](#q20-migration-kafka--redpanda)

## Q1. (!) Что такое Redpanda?

**Redpanda** — Kafka-совместимая стриминговая платформа.

**Создана** компанией Vectorized.io (ныне Redpanda Data) в 2019.

**Ключевые преимущества:**
- **Совместима с Kafka API** (drop-in замена)
- **Без JVM, без ZooKeeper, без JVM-зависимости Kafka Streams**
- **Единый бинарник** — проще в эксплуатации
- **C++ + Seastar** — производительность
- **Меньшая задержка** (заявляют p99 в 10 раз ниже)
- **Тот же wire-протокол** — существующие Kafka-клиенты работают как есть

**Применения:** те же, что у Kafka — event streaming, микросервисы, аналитика в реальном времени, агрегация логов.

## Q2. (!) Redpanda vs Kafka — отличия?

| Критерий | Apache Kafka | Redpanda |
|----------|--------------|----------|
| Язык | Java (JVM) | C++ |
| ZooKeeper | Требуется (KRaft — новое) | **Не нужен** |
| Архитектура | Пулы потоков | **Shard-per-core** |
| Паузы GC | Да | **Нет** |
| Компоненты | Брокеры + ZK + Connect + Schema Registry | **Единый бинарник** |
| Задержка p99 | 10-100 мс | **2-10 мс** (заявлено) |
| Пропускная способность | Высокая | **Выше** на том же железе |
| Память | Тяжёлая (JVM heap) | **Ниже** |
| Сложность настройки | Сложная | **Простая** |
| Зрелость | с 2011, очень зрелая | с 2019, менее зрелая |
| Распространённость | Огромная | Растущая |
| Экосистема | Огромная | Совместима, но меньше нативных компонентов |

## Q3. Single binary — что значит?

**Развёртывание Apache Kafka:**
- Брокеры Kafka (Java)
- ZooKeeper (или KRaft-контроллеры)
- Schema Registry (отдельный Java-процесс)
- Kafka Connect (отдельно)
- MirrorMaker (отдельно)

**Развёртывание Redpanda:**
- Только **бинарник `redpanda`** + конфиг
- HTTP-прокси и Schema Registry **встроены**
- Один процесс на узел

**Эффект:** **намного проще в эксплуатации**. Развёртывание в контейнерах проще. Меньше движущихся частей.

## Q4. (!) C++ + Seastar (shard-per-core)?

**Seastar** — тот же фреймворк, что и у **ScyllaDB**.

**Shard-per-core:**
- Один shard на ядро CPU
- **Без блокировок** между ядрами (нет конкуренции за ресурсы)
- **Без разделяемой памяти** между ядрами
- Асинхронный I/O через Seastar
- Линейное масштабирование по ядрам CPU

**В отличие от пулов потоков Kafka:**
- Kafka использует модель пула потоков — блокировки, конкуренция
- Redpanda — shared-nothing на каждое ядро

**Результат:** лучшая утилизация CPU, меньшая задержка.

## Q5. No JVM, no GC pauses?

**Kafka** — JVM:
- Паузы GC (10-500 мс)
- Всплески p99-задержки
- Сложный тюнинг

**Redpanda** — C++:
- Ручное управление памятью
- **Без пауз GC**
- Предсказуемая задержка
- p99-задержка **в 5-10 раз ниже**

То же преимущество, что у **ScyllaDB vs Cassandra**.

## Q6. (!) No ZooKeeper — Raft консенсус?

**Kafka исторически** требовала ZooKeeper для:
- Метаданных кластера
- Выбора контроллера (controller election)
- Конфигурации

**Kafka KRaft** (с 2.8+, GA в 3.3) — заменяет ZK встроенным Raft. **Сейчас переходный период**.

**Redpanda с первого дня** — без ZooKeeper. **Raft на каждую партицию** — у каждой партиции своя Raft-группа для репликации и консенсуса.

**Эффект:**
- Проще эксплуатация
- Меньше компонентов
- Быстрее failover

## Q7. Tiered storage?

**Tiered storage** в Redpanda — старые данные выгружаются в **объектное хранилище (S3, GCS, Azure Blob)**.

```yaml
cloud_storage_enabled: true
cloud_storage_bucket: my-redpanda-bucket
cloud_storage_region: us-east-1
```

**Горячие данные:** локальный диск (быстро)
**Холодные данные:** S3 (дёшево)

**Чтение** из S3 прозрачно — медленнее, но дёшево.

**Экономия** при долгом хранении (месяцы, годы).

Та же идея, что у **Pulsar tiered storage** и **Kafka Tiered Storage** (KIP-405).

## Q8. (!) Kafka wire protocol compatibility?

**Redpanda реализует** wire-протокол Kafka.

**Kafka-клиенты** (на любом языке) общаются с Redpanda **без изменений**.

```python
# Same Kafka Python client
from kafka import KafkaProducer
producer = KafkaProducer(bootstrap_servers='redpanda:9092')
producer.send('my-topic', b'message')
```

**Уровень совместимости:** очень высокий. Поддерживается большинство Kafka API.

**Часть продвинутых возможностей** не поддерживается (Kafka-транзакции в Redpanda появились недавно).

## Q9. Kafka clients работают?

**Да** — все основные Kafka-клиенты:
- Java (kafka-clients)
- Python (kafka-python, confluent-kafka-python)
- Go (sarama, confluent-kafka-go, segmentio/kafka-go)
- Node.js (kafkajs)
- .NET, Ruby и т.д.

**CLI-инструменты Confluent** работают с Redpanda.

**ORM/коннекторы** (Debezium, Kafka Connect) — поддерживаются.

## Q10. Schema Registry, Connect?

**Schema Registry** — встроен в Redpanda (Avro, JSON Schema, Protobuf).

```bash
# Compatible с Confluent Schema Registry API
curl http://redpanda:8081/subjects
```

**Kafka Connect:** у Redpanda нет собственной версии. Используется **стандартный Kafka Connect** поверх Redpanda — работает.

**Redpanda Console** — UI для просмотра топиков, схем и consumer-ов.

## Q11. (!) Performance claims (latency, throughput)?

**Результаты бенчмарков Redpanda Data** (варьируются):
- **p50-задержка:** 2-3 мс (vs Kafka 10-15 мс)
- **p99-задержка:** 5-10 мс (vs Kafka 50-100 мс)
- **Пропускная способность:** в 10 раз выше на ядро CPU
- **Меньше CPU/памяти** при той же нагрузке

**Оговорки:**
- Бенчмарки от вендора (Redpanda Data) — могут быть предвзяты
- Результаты в реальных условиях варьируются
- Тюнинг Kafka имеет значение

**Независимые бенчмарки** в целом подтверждают, что Redpanda **быстрее**, но разрыв меньше заявленного вендором.

## Q12. (!) Why faster than Kafka?

1. **C++ vs Java** — нет накладных расходов JVM
2. **Без пауз GC** — предсказуемая задержка
3. **Shard-per-core** — нет конкуренции за блокировки
4. **Direct I/O** — минуя буферы ядра
5. **Опция DPDK** — минуя TCP-стек ядра
6. **Нет roundtrip-ов к ZooKeeper** за метаданными
7. **Оптимизированная раскладка памяти** под кэши CPU
8. **Всё асинхронно** через Seastar

**Результат:** обычно задержка в 3-10 раз ниже, выше пропускная способность на CPU.

## Q13. WASM transforms?

**Redpanda WASM Data Transforms** (с 2023) — выполнение WebAssembly-функций внутри брокера.

```rust
#[redpanda_transform_sdk::on_record_written]
fn process(event: WriteEvent, writer: RecordWriter) -> Result<()> {
    // Transform record before write
    let transformed = transform(event.record);
    writer.write(transformed)?;
    Ok(())
}
```

**Сценарии применения:**
- Миграция схемы (преобразование старого формата в новый)
- Фильтрация / маршрутизация
- Лёгкое обогащение данных

**Языки:** Rust, Go, JavaScript (через WASM).

Похоже на **Kafka Streams**, но **внутри брокера** (без отдельного процесса).

## Q14. Built-in HTTP proxy?

**Pandaproxy** — HTTP REST API для Kafka-топиков.

```bash
# Produce via HTTP
curl -X POST http://redpanda:8082/topics/my-topic \
  -H "Content-Type: application/vnd.kafka.json.v2+json" \
  -d '{"records":[{"value":{"hello":"world"}}]}'

# Consume via HTTP
curl http://redpanda:8082/consumers/my-group/instances/my-instance/records
```

**Сценарии применения:**
- IoT-устройства без Kafka-клиента
- Producer-ы на стороне браузера
- Простые интеграции

То же, что **Confluent REST Proxy**, но встроенный.

## Q15. Console (UI)?

**Redpanda Console** — веб-UI для:
- Просмотра топиков и партиций
- Инспекции сообщений
- Управления consumer-группами
- Просмотра схем
- Управления Connect-кластером
- Ролей, ACL

```bash
docker run -p 8080:8080 -e KAFKA_BROKERS=redpanda:9092 \
  docker.redpanda.com/redpandadata/console:latest
```

Аналог **AKHQ, Kafdrop, Kowl** (Kowl — предыдущая версия Console).

## Q16. (!) Open source vs Enterprise vs Cloud?

**Open source (бесплатно):**
- Source available (лицензия BSL)
- Базовые стриминговые возможности
- Tiered storage

**Enterprise (платно):**
- Аудит-логирование
- Продвинутая безопасность (SASL/OAuthbearer)
- Настраиваемый ролевой доступ на уровне кластера (более гранулярный)
- Поддержка 24/7

**Redpanda Cloud (managed):**
- Полностью управляемый в AWS, GCP, Azure
- Опция BYOC (Bring Your Own Cloud) — работает в **вашем** AWS-аккаунте
- Multi-region

В **2025** — растущая популярность managed Redpanda Cloud (альтернатива Confluent Cloud).

## Q17. Source available license (BSL)?

**Business Source License (BSL)** — та же, что у CockroachDB.

**Ограничения:**
- Нельзя **предлагать Redpanda как сервис** третьим лицам без коммерческой лицензии
- В остальном — бесплатно для self-host и модификации

**Переходит в Apache 2.0 через 4 года** (старые версии становятся полностью открытыми).

**Эффект:** AWS / GCP не могут предлагать «Redpanda as a Service». Managed-версию продаёт Redpanda Data.

Похоже на **Elastic License** и **Cockroach License**.

## Q18. (!) Когда выбрать Redpanda над Kafka?

**Выбирай Redpanda когда:**
- Критична **низкая задержка** (финансы, гейминг, реальное время)
- Важна **простота эксплуатации** (единый бинарник)
- Нужно **меньше узлов** под нагрузку (экономия)
- Cloud-native развёртывания
- Нужен **предсказуемый p99** (без GC)
- **Меньшая команда** на сопровождение messaging
- Современный стек, без legacy-зависимостей Kafka

**Оставайся с Kafka:**
- Уже глубоко вложились
- Нужна **зрелость экосистемы** (часть интеграций специфична для Kafka)
- Нужны **конкретные возможности Kafka**, которых нет в Redpanda
- Организация, избегающая рисков

## Q19. Когда не выбирать Redpanda?

1. **Нужны самые свежие фичи** — у Kafka новые возможности появляются раньше
2. **Специфичные инструменты** только под Kafka (редко)
3. **Огромное существующее развёртывание Kafka** — риск миграции
4. **Меньше проверена** на экстремальных масштабах
5. **Меньшее сообщество** — меньше ответов, блогов
6. **Опасения по лицензии** (BSL вызывает споры)
7. **Хочется полностью бесплатного, Apache-проекта** (тогда Kafka)

## Q20. Migration Kafka → Redpanda?

**Подходы:**

1. **MirrorMaker 2** — непрерывно реплицировать Kafka → Redpanda, затем переключить клиентов
2. **Dual-write** — приложения пишут в обе системы, постепенно переключают чтение
3. **Cut-over** — остановить, скопировать данные (rpk import), запустить

**Чаще всего:** MirrorMaker 2.

**Инструменты:** `rpk` (CLI Redpanda) для управления кластером.

```bash
# Mirror Kafka → Redpanda
rpk topic create my-topic
# Configure MirrorMaker 2 to mirror Kafka к Redpanda
```

**Тщательно тестируй** перед переключением в production.

В **2025** — Redpanda **растущая альтернатива** Kafka. Особенно привлекательна для новых **cloud-native** развёртываний.

---

## See also

- [Apache Kafka](kafka-interview.md) — main конкурент (Redpanda compatible)
- [Kafka Streams](../data-engineering/kafka-streams-interview.md) — works against Redpanda
- [NATS](nats-interview.md) — another lightweight alternative
- [Apache Pulsar](pulsar-interview.md) — another alternative
- [Message Brokers Comparison](message-brokers-comparison-interview.md) — overview
- [Event-driven Patterns](../architecture/event-driven-patterns-interview.md) — context
- [Микросервисы](../architecture/microservices-interview.md) — primary use case
- [Stream Processing](../data-engineering/stream-processing-interview.md) — context
- [ScyllaDB](../databases/scylladb-interview.md) — same Seastar framework
- [Распределённые системы](../architecture/distributed-systems-interview.md) — Raft, consensus
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — shard-per-core
- [Performance Testing](../performance/performance-testing-interview.md) — benchmarking
- [OpenTelemetry](../monitoring/opentelemetry-interview.md) — Redpanda metrics
- [AWS SQS и SNS](aws-sqs-sns-interview.md)
- [Сравнение Message Brokers](message-brokers-comparison-interview.md)
- [NATS](nats-interview.md)
- [Apache Pulsar](pulsar-interview.md)
- [RabbitMQ](rabbitmq-interview.md)
