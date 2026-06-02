---
title: "Вопросы на собеседовании: ScyllaDB"
description: "ScyllaDB: drop-in Cassandra replacement в C++, shared-nothing, shard-per-core, lower latency, higher throughput, Cassandra-compatible (CQL), Seastar framework, vs Cassandra"
tags:
  - interview
  - databases
  - scylladb-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "ScyllaDB"
  - "ScyllaDB interview"
  - "ScyllaDB собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `ScyllaDB`

`ScyllaDB` — open-source NoSQL wide-column база данных. **Drop-in замена Cassandra**, написана на **C++** (в отличие от Cassandra на Java) с архитектурой **shard-per-core**. Обещает **в 10 раз меньшую задержку** и более высокую пропускную способность. Использует фреймворк **Seastar** для асинхронного I/O. Создана в 2014 году выходцами из Cloudius (KVM).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [ScyllaDB Documentation](https://docs.scylladb.com/)
- [ScyllaDB GitHub](https://github.com/scylladb/scylladb)
- [Seastar Framework](https://seastar.io/)
- [ScyllaDB University](https://university.scylladb.com/)
- [ScyllaDB Cloud](https://cloud.scylladb.com/)
- [Cassandra Documentation](https://cassandra.apache.org/doc/) — same data model

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое ScyllaDB?](#q1--что-такое-scylladb)
- [Q2. (!) ScyllaDB vs Cassandra — основные отличия?](#q2--scylladb-vs-cassandra--основные-отличия)
- [Q3. (!) Что такое shard-per-core архитектура?](#q3--что-такое-shard-per-core-архитектура)
- [Q4. Seastar framework?](#q4-seastar-framework)

**Data model (= Cassandra)**
- [Q5. (!) Wide-column data model?](#q5--wide-column-data-model)
- [Q6. Partition key, clustering key?](#q6-partition-key-clustering-key)
- [Q7. CQL (Cassandra Query Language)?](#q7-cql-cassandra-query-language)

**Architecture**
- [Q8. (!) Shared-nothing distributed?](#q8--shared-nothing-distributed)
- [Q9. Replication, consistency levels?](#q9-replication-consistency-levels)
- [Q10. Tunable consistency (R + W > N)?](#q10-tunable-consistency-r--w--n)
- [Q11. Tokens, virtual nodes (vnodes)?](#q11-tokens-virtual-nodes-vnodes)
- [Q12. (!) LSM-tree storage?](#q12--lsm-tree-storage)

**Performance**
- [Q13. (!) Why is Scylla faster than Cassandra?](#q13--why-is-scylla-faster-than-cassandra)
- [Q14. No JVM = no GC pauses?](#q14-no-jvm--no-gc-pauses)
- [Q15. Workload prioritization?](#q15-workload-prioritization)

**Compatibility**
- [Q16. (!) Cassandra drop-in replacement — насколько true?](#q16--cassandra-drop-in-replacement--насколько-true)
- [Q17. (!) DynamoDB API (Alternator)?](#q17--dynamodb-api-alternator)

**Use cases**
- [Q18. (!) Когда выбрать Scylla?](#q18--когда-выбрать-scylla)
- [Q19. Time-series workloads?](#q19-time-series-workloads)
- [Q20. IoT?](#q20-iot)

**Editions**
- [Q21. (!) Open Source vs Enterprise vs ScyllaDB Cloud?](#q21--open-source-vs-enterprise-vs-scylladb-cloud)

**Migration и operations**
- [Q22. (!) Migration Cassandra → Scylla?](#q22--migration-cassandra--scylla)
- [Q23. Какие частые проблемы Scylla в production?](#q23-какие-частые-проблемы-scylla-в-production)

## Q1. (!) Что такое ScyllaDB?

**ScyllaDB** — open-source NoSQL wide-column база данных. **API-совместима с Apache Cassandra**, но переписана с нуля на **C++** ради существенно более высокой производительности.

**Заявленные метрики против Cassandra:**
- **В 10 раз меньшая задержка**
- **В 3-5 раз выше пропускная способность**
- **Та же модель данных** (CQL, репликация, согласованность)

**Создана в 2014 году** выходцами из проекта KVM (Cloudius Systems), коммерческая компания — **ScyllaDB Inc.**

**Применения:** те же, что у Cassandra:
- Временные ряды (time-series)
- IoT
- Ad-tech (ставки в реальном времени)
- Платформы обмена сообщениями (Discord использует Scylla)
- Высокопроизводительное логирование

## Q2. (!) ScyllaDB vs Cassandra — основные отличия?

| Критерий | Apache Cassandra | ScyllaDB |
|----------|------------------|----------|
| Язык | Java (JVM) | C++ |
| Архитектура | Пул потоков | **Shard-per-core (Seastar)** |
| GC | Да (паузы JVM GC) | Нет GC |
| Задержка p99 | ~5-50 мс | ~1-5 мс |
| Пропускная способность | Высокая | **В 3-5 раз выше** |
| Потребление памяти | Высокое | Ниже |
| Эффективность CPU | Ниже | **Существенно выше** |
| Совместимость | Оригинал | API-совместима |
| Зрелость | с 2008 | с 2014 |
| Распространённость | Шире | Растёт |

**Производительность** — главное отличие. На **том же железе** Scylla обычно в 3-10 раз быстрее.

## Q3. (!) Что такое shard-per-core архитектура?

**Cassandra:**
- Пулы потоков (work-stealing)
- Блокировки, конкуренция между ядрами
- Накладные расходы JVM

**ScyllaDB (shared-nothing на каждое ядро):**
- **Один shard на ядро CPU**
- Каждый shard владеет своим подмножеством данных
- **Нет блокировок** между ядрами (нет конкуренции)
- **Нет разделяемой памяти** между ядрами
- Сетевые запросы маршрутизируются к нужному ядру

```
8-core node:
  Core 0 → shard 0 (range A-D)
  Core 1 → shard 1 (range E-H)
  Core 2 → shard 2 (range I-L)
  ...
```

**Эффект:**
- **Линейное масштабирование с числом ядер** (у Cassandra рост выходит на плато)
- Нет пауз GC
- Предсказуемая задержка

## Q4. Seastar framework?

**Seastar** — C++ фреймворк, созданный авторами ScyllaDB. Спроектирован под **современное железо** (многоядерные CPU, быстрые сетевые карты).

**Принципы:**
- **Shared-nothing на каждое ядро**
- **Асинхронный I/O** (без блокировок)
- **Сеть в user-space** (опционально DPDK)
- Абстракция **Future/Promise**

Используется не только в ScyllaDB — также в Redis-подобном проекте и сетевых приложениях.

## Q5. (!) Wide-column data model?

**Та же, что у Cassandra** (отличается только производительность).

```sql
CREATE TABLE users (
    user_id UUID,
    timestamp TIMESTAMP,
    event_type TEXT,
    data TEXT,
    PRIMARY KEY (user_id, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);
```

**Понятия:**
- **Keyspace** = база данных / схема
- **Table** = набор строк
- **Partition key** — определяет, на каком узле размещается строка
- **Clustering key** — сортировка внутри партиции
- **Columns** — задаются в схеме

Подробнее — в [Apache Cassandra](cassandra-interview.md).

## Q6. Partition key, clustering key?

```sql
PRIMARY KEY ((partition_key), clustering_key1, clustering_key2)
```

**Partition key** — хешируется → определяет, какой узел владеет строкой.
**Clustering key** — упорядочивает строки внутри партиции.

**Составной partition key:**
```sql
PRIMARY KEY ((user_id, date), timestamp)
-- partition by combination, ordered by timestamp
```

**Лучшие практики:**
- Partition key высокой кардинальности (чтобы избежать горячих партиций)
- Размер партиции < 100 МБ
- Упорядочивать clustering keys под паттерны запросов

## Q7. CQL (Cassandra Query Language)?

**CQL** — SQL-подобный язык для Cassandra/Scylla.

```sql
-- DDL
CREATE KEYSPACE mykeyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};

CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    name TEXT,
    email TEXT
);

-- DML
INSERT INTO users (user_id, name, email) VALUES (uuid(), 'Alice', 'alice@example.com');

SELECT * FROM users WHERE user_id = uuid_value;

UPDATE users SET email = 'new@example.com' WHERE user_id = uuid_value;

DELETE FROM users WHERE user_id = uuid_value;
```

**Ограничения по сравнению с SQL:**
- **Нет JOIN-ов** (данные денормализуют)
- **Нет агрегаций между партициями** (ограниченно)
- **WHERE только по partition key + clustering key** (или по вторичному индексу)
- **Нет подзапросов**

Расширения ScyllaDB:
- **CDC (Change Data Capture)**
- **Materialized Views**

## Q8. (!) Shared-nothing distributed?

**Shared-nothing** — каждый узел независим, общего состояния нет.

```
Node 1: own data, own CPU, own memory, own disk
Node 2: same
Node 3: same
```

**Координация через gossip-протокол** (peer-to-peer). Мастера нет.

**Преимущества:**
- Линейное масштабирование
- Нет единой точки отказа
- Легко добавлять/удалять узлы

**Компромисс:** по умолчанию eventual consistency (настраивается).

## Q9. Replication, consistency levels?

**Replication factor (RF):** сколько копий данных хранится.
```sql
CREATE KEYSPACE mykeyspace WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': 3, 'dc2': 3};
```

**Уровни согласованности (на каждый запрос):**
- `ANY` — хотя бы одна реплика (включая hinted handoff)
- `ONE`, `TWO`, `THREE` — N реплик
- `QUORUM` — большинство (N/2 + 1)
- `ALL` — все реплики
- `LOCAL_QUORUM` — кворум в локальном DC
- `EACH_QUORUM` — кворум в каждом DC

```sql
SELECT * FROM users WHERE user_id = ? USING CONSISTENCY QUORUM;
```

## Q10. Tunable consistency (R + W > N)?

**Формула строгой согласованности:**
```
R + W > N
```
- R = уровень согласованности чтения
- W = уровень согласованности записи
- N = replication factor

**Пример:** RF=3
- W=QUORUM (2) + R=QUORUM (2): 2+2 > 3 — строгая согласованность
- W=ONE (1) + R=ONE (1): 1+1 < 3 — eventual

**Компромисс по производительности:**
- Выше согласованность → выше задержка, ниже доступность
- Ниже согласованность → быстрее, но согласованность eventual

**Частый выбор:** `LOCAL_QUORUM` для обоих — баланс.

## Q11. Tokens, virtual nodes (vnodes)?

**Token ring** — пространство хешей (от `-2^63` до `2^63-1`).

Partition key каждой строки → хешируется → токен → отображается на узел, владеющий этим диапазоном токенов.

**vnodes** — каждый физический узел владеет несколькими диапазонами **виртуальных узлов** (virtual node).

```
Without vnodes:
  Node 1: tokens 0-1000
  Node 2: tokens 1001-2000

With 256 vnodes per node:
  Node 1: 256 random ranges
  Node 2: 256 random ranges
```

**Преимущества vnodes:**
- **Лучшая балансировка нагрузки**
- **Быстрее восстановление** (параллельная передача данных со многих узлов)
- **Проще масштабирование** (новый узел подтягивает данные сразу с многих)

ScyllaDB поддерживает vnodes (а также обычные токены).

## Q12. (!) LSM-tree storage?

**LSM-tree (Log-Structured Merge-tree)** — структура хранения, используемая в Cassandra/Scylla, RocksDB, ClickHouse и др.

**Архитектура:**
```
Write → MemTable (in-memory sorted)
       ↓ flush when full
       SSTable (immutable on-disk file)
       ↓ background compaction
       Larger SSTables
```

**Преимущества:**
- **Быстрые записи** (последовательные, батчами)
- **Быстрые чтения при попадании в кэш** — MemTable / Bloom filter

**Компромисс:**
- **Read amplification** — приходится проверять несколько SSTable
- **Накладные расходы на compaction** — фоновая работа

**Стратегии compaction:**
- **STCS (SizeTieredCompactionStrategy)** — по умолчанию
- **LCS (LeveledCompactionStrategy)** — лучше для read-heavy нагрузки
- **TWCS (TimeWindowCompactionStrategy)** — для временных рядов

## Q13. (!) Why is Scylla faster than Cassandra?

1. **C++ против Java** — нет накладных расходов JVM, нет пауз GC
2. **Shard-per-core** — нет конкуренции за блокировки
3. **Асинхронный I/O Seastar** — эффективный ввод-вывод
4. **Прямой дисковый I/O** — обход буферов ядра (в части конфигураций)
5. **Лучшая утилизация CPU** — спроектировано под современные многоядерные процессоры
6. **Нет пауз GC** — предсказуемая задержка p99
7. **Более плотное управление памятью** — нет накладных расходов на Java heap
8. **Своя сетевая реализация** (опционально DPDK) — обход TCP-стека ядра

**Итог:** на том же железе Scylla часто в 3-10 раз быстрее Cassandra.

## Q14. No JVM = no GC pauses?

**Паузы GC в Cassandra:**
- Stop-the-world паузы (10-500 мс)
- Всплески задержки p99
- Сложный тюнинг (правка G1GC, ZGC)

**Scylla:**
- Нет GC (ручное управление памятью в C++)
- Предсказуемая задержка
- Задержка p99 в 5-10 раз ниже

**Главная причина**, по которой Discord, Comcast и другие **мигрировали** с Cassandra на Scylla.

## Q15. Workload prioritization?

Фича ScyllaDB Enterprise: **приоритизация нагрузок** — разные нагрузки получают разные доли CPU/IO.

```
Critical OLTP queries: 80% resources
Background analytics: 20% resources
```

**Сценарий:** смешанные нагрузки (OLTP + отчётность) на одном кластере без взаимного влияния.

В Cassandra нет встроенного аналога — обычно поднимают отдельные кластеры.

## Q16. (!) Cassandra drop-in replacement — насколько true?

ScyllaDB **высоко совместима**:
- Тот же CQL
- Тот же wire-протокол (клиенты Cassandra работают)
- Та же модель данных
- Та же репликация
- Те же уровни согласованности

**Миграция приложений без изменения кода** в большинстве случаев.

**Отличия (не 100% drop-in):**
- Часть продвинутых фич Cassandra отсутствует (или реализована иначе)
- Отличаются эксплуатация и мониторинг
- Отличаются параметры тюнинга
- Версии со временем расходятся

**Лучшая практика:** тщательно тестировать. Использовать **Scylla Migration Tools**.

## Q17. (!) DynamoDB API (Alternator)?

**Scylla Alternator** — DynamoDB API поверх ScyllaDB.

```python
# Standard boto3 DynamoDB client works
import boto3
dynamodb = boto3.resource('dynamodb', endpoint_url='http://scylla:8000')
table = dynamodb.Table('users')
table.put_item(Item={'PK': 'user#123', 'name': 'Alice'})
```

**Зачем:**
- Self-hosted и DynamoDB-совместимо (нет vendor lock-in на AWS)
- Запуск DynamoDB-нагрузок on-premise / multi-cloud
- Дешевле DynamoDB на больших масштабах

В **2025 году** — жизнеспособная альтернатива для приложений, уже спроектированных под DynamoDB.

## Q18. (!) Когда выбрать Scylla?

**Выбирай Scylla когда:**
- Уже работаешь на Cassandra и хочешь больше производительности
- Нужна **предсказуемо низкая задержка** (p99 < 10 мс)
- Высокая пропускная способность (10K+ записей/сек на узел)
- Важна экономия (меньше узлов на ту же пропускную способность)
- Смешанным нагрузкам нужна изоляция
- IoT, временные ряды, ad-tech (типичные сценарии Cassandra)

**Не выбирай когда:**
- Нужен полноценный SQL (джойны, агрегации, транзакции)
- Небольшой масштаб (хватает PostgreSQL)
- Нужны строгие ACID-гарантии
- У команды нет опыта с NoSQL

## Q19. Time-series workloads?

**Временные ряды идеальны для Scylla/Cassandra:**
- Только добавление (append-only)
- Высокая пропускная способность записи
- Чтения по временным окнам

**Схема:**
```sql
CREATE TABLE sensor_data (
    sensor_id UUID,
    bucket DATE,  -- partition by day to limit partition size
    timestamp TIMESTAMP,
    value DOUBLE,
    PRIMARY KEY ((sensor_id, bucket), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC)
  AND compaction = {'class': 'TimeWindowCompactionStrategy', 'compaction_window_size': '1', 'compaction_window_unit': 'DAYS'};
```

**TWCS** эффективно автоматически уплотняет старые данные. Старые SSTable можно удалять через TTL.

## Q20. IoT?

IoT = миллионы устройств, непрерывно отправляющих данные.

**Scylla здесь хороша:**
- Высокая пропускная способность записи (миллионы записей/сек на кластер)
- Хранение временных рядов
- Геораспределённость (multi-DC)
- Автоматическое истечение по TTL

**Кто использует:** Comcast, Tubi, Discord, Numberly.

## Q21. (!) Open Source vs Enterprise vs ScyllaDB Cloud?

**Open Source (бесплатно):**
- Лицензия Apache 2.0 (очень либеральная)
- Базовые возможности
- Self-managed

**ScyllaDB Enterprise (платно):**
- Приоритизация нагрузок
- LDAP, шифрование, compliance
- Более быстрые стратегии compaction
- Поддержка 24/7

**ScyllaDB Cloud (managed):**
- Полностью управляемый сервис на AWS, GCP, Azure
- Multi-region
- Автоматические бэкапы
- Оплата по факту (pay-as-you-go)

В **2025 году** — Open Source отлично подходит для большинства нагрузок. Enterprise — там, где много требований к безопасности и compliance.

## Q22. (!) Migration Cassandra → Scylla?

**Шаги:**
1. **Проверка совместимости** — совпадают ли возможности версии Cassandra?
2. **Поднять кластер Scylla** (параллельно)
3. **Dual-write** — приложение пишет в оба (или через CDC-поток)
4. **Массовое копирование исторических данных** — Scylla Migrator (на базе Spark) или sstableloader
5. **Проверить паритет данных**
6. **Переключить чтения** на Scylla (по одному узлу за раз)
7. **Остановить dual-write**
8. **Вывести Cassandra из эксплуатации**

**Зачастую** **прозрачно для приложения** — тот же CQL.

**Инструменты:** Scylla Migrator, sstableloader, собственный CDC.

## Q23. Какие частые проблемы Scylla в production?

1. **Горячие партиции** (как и в Cassandra) — неверный выбор PK
2. **Tombstones** — обилие удалений перегружает чтения
3. **Отставание compaction** — скорость записи > скорости compaction
4. **Узкое место по дисковому I/O** — медленные диски
5. **Неверный уровень согласованности** — слишком строгий = медленно, слишком слабый = несогласованности
6. **Слишком мало узлов** — масштабирование за пределами доступного
7. **Нет бэкапов** — нужны Scylla Manager / снапшоты
8. **Недостаточный мониторинг** — стек Scylla Monitoring обязателен
9. **Миграции схемы** — медленны на больших таблицах
10. **Непонимание shard-per-core** — критичен тюнинг пула соединений

**Лучшая практика:** использовать **Scylla Manager** для бэкапов, repair-ов и управления схемой.

---

## See also

- [Apache Cassandra](cassandra-interview.md) — original same data model
- [PostgreSQL](postgresql-interview.md) — для сравнения
- [MongoDB](mongodb-interview.md) — alternative NoSQL
- [DynamoDB](dynamodb-interview.md) — Alternator API
- [ClickHouse](clickhouse-interview.md) — для analytics (Scylla — OLTP)
- [CockroachDB](cockroachdb-interview.md) — distributed SQL (different paradigm)
- [Redis](redis-interview.md) — caching layer
- [Database Architecture](database-architecture-interview.md) — NoSQL context
- [Распределённые системы](../architecture/distributed-systems-interview.md) — gossip, consensus
- [CAP Theorem](../architecture/cap-theorem-interview.md) — Scylla = AP
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — horizontal scaling
- [Микросервисы](../architecture/microservices-interview.md) — Scylla per service
- [Stream Processing](../data-engineering/stream-processing-interview.md) — Kafka → Scylla pattern
- [[apache-kafka-interview|Apache Kafka]] — common ingestion path
