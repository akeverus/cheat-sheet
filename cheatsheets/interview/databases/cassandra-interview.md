---
title: "Вопросы на собеседовании: Apache Cassandra"
description: "Вопросы и ответы по Apache Cassandra: архитектура, модель данных, CQL, Primary Key, репликация, консистентность, LWT, compaction, Spring Data Cassandra, Java Driver."
tags:
  - interview
  - databases
  - cassandra-interview
aliases:
  - "Apache Cassandra"
  - "Cassandra interview"
  - "Cassandra собеседование"
  - "NoSQL Cassandra"
  - "CQL"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Apache Cassandra`

Вопросы и ответы по `Apache Cassandra`: архитектура кластера, модель данных, `CQL`, `Primary Key`, репликация, уровни консистентности, `LWT`, стратегии компакции, `Spring Data Cassandra`, `Java Driver`.

Дата последнего обновления: 2026-04-13

**Apache Cassandra** — распределённая `NoSQL` БД с линейной масштабируемостью и высокой доступностью, спроектированная для обработки больших объёмов данных на множестве серверов без единой точки отказа. На собеседованиях спрашивают про архитектуру кластера, моделирование данных под запросы, `CQL`, уровни консистентности и сценарии использования.

## Полезные ссылки

### Официальная документация

- [Apache Cassandra Documentation](https://cassandra.apache.org/doc/latest/) — официальная документация
- [DataStax Java Driver](https://docs.datastax.com/en/developer/java-driver/) — драйвер для Java
- [Spring Data Cassandra Reference](https://docs.spring.io/spring-data/cassandra/docs/current/reference/html/) — Spring Data Cassandra

### Статьи Baeldung

- [Introduction to Spring Data Cassandra](https://www.baeldung.com/spring-data-cassandra-tutorial) — настройка и базовые операции
- [A Guide to Cassandra with Java](https://www.baeldung.com/cassandra-with-java) — работа через Java Driver
- [Cassandra Batch in CQL and Java](https://www.baeldung.com/java-cql-cassandra-batch) — пакетные операции
- [Cassandra Query Cheat Sheet](https://www.baeldung.com/cassandra-query-cheat-sheet) — справочник по CQL
- [Spring Data with Reactive Cassandra](https://www.baeldung.com/spring-data-cassandra-reactive) — реактивный стек
- [Secondary Indexes in Cassandra](https://www.baeldung.com/cassandra-secondary-indexes) — вторичные индексы
- [Data Modeling in Cassandra](https://www.baeldung.com/cassandra-data-modeling) — моделирование данных под запросы
- [Consistency Levels in Cassandra](https://www.baeldung.com/cassandra-consistency-levels) — уровни консистентности
- [Cassandra Partition Key, Composite Key, and Clustering Key](https://www.baeldung.com/cassandra-keys) — устройство ключей

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Архитектура и основные концепции**
- [Q1. (!) Что такое Apache Cassandra и когда её выбирать?](#q1--что-такое-apache-cassandra-и-когда-её-выбирать)
- [Q2. (!) Какова архитектура кластера Cassandra?](#q2--какова-архитектура-кластера-cassandra)
- [Q3. (!) Что такое Gossip-протокол?](#q3--что-такое-gossip-протокол)
- [Q4. Что такое Snitch и какие типы существуют?](#q4-что-такое-snitch-и-какие-типы-существуют)
- [Q5. Как работает Consistent Hashing в Cassandra?](#q5-как-работает-consistent-hashing-в-cassandra)
- [Q6. Что такое Virtual Nodes (vnodes)?](#q6-что-такое-virtual-nodes-vnodes)

**Модель данных и Primary Key**
- [Q7. (!) Какую модель данных использует Cassandra?](#q7--какую-модель-данных-использует-cassandra)
- [Q8. (!) Что такое Primary Key, Partition Key и Clustering Key?](#q8--что-такое-primary-key-partition-key-и-clustering-key)
- [Q9. (!) Как правильно моделировать данные в Cassandra?](#q9--как-правильно-моделировать-данные-в-cassandra)
- [Q10. Какие факторы влияют на равномерное распределение данных?](#q10-какие-факторы-влияют-на-равномерное-распределение-данных)
- [Q11. Что такое Wide Rows и какие ограничения на размер партиции?](#q11-что-такое-wide-rows-и-какие-ограничения-на-размер-партиции)

**CQL и работа с данными**
- [Q12. (!) Основные команды CQL](#q12--основные-команды-cql)
- [Q13. Что такое Secondary Index и когда его использовать?](#q13-что-такое-secondary-index-и-когда-его-использовать)
- [Q14. Что такое Materialized Views?](#q14-что-такое-materialized-views)
- [Q15. Как выполнять агрегации и аналитические запросы?](#q15-как-выполнять-агрегации-и-аналитические-запросы)

**Запись и чтение данных**
- [Q16. (!) Как работает путь записи (Write Path)?](#q16--как-работает-путь-записи-write-path)
- [Q17. (!) Как работает путь чтения (Read Path)?](#q17--как-работает-путь-чтения-read-path)
- [Q18. Что такое Tombstone и как они влияют на производительность?](#q18-что-такое-tombstone-и-как-они-влияют-на-производительность)
- [Q19. Как работает механизм сжатия данных (Compression)?](#q19-как-работает-механизм-сжатия-данных-compression)

**Репликация и консистентность**
- [Q20. (!) Что такое Replication и какие стратегии существуют?](#q20--что-такое-replication-и-какие-стратегии-существуют)
- [Q21. (!) Какие уровни консистентности (Consistency Levels) существуют?](#q21--какие-уровни-консистентности-consistency-levels-существуют)
- [Q22. (!) Что означает формула R + W > N?](#q22--что-означает-формула-r--w--n)
- [Q23. Что такое Hinted Handoff?](#q23-что-такое-hinted-handoff)
- [Q24. Что такое Read Repair?](#q24-что-такое-read-repair)
- [Q25. (!) Что такое Anti-Entropy Repair и зачем он нужен?](#q25--что-такое-anti-entropy-repair-и-зачем-он-нужен)

**Компакция и TTL**
- [Q26. (!) Какие стратегии компакции (Compaction) существуют?](#q26--какие-стратегии-компакции-compaction-существуют)
- [Q27. Как управлять жизненным циклом данных через TTL?](#q27-как-управлять-жизненным-циклом-данных-через-ttl)

**Транзакции и Batch**
- [Q28. (!) Что такое Lightweight Transactions (LWT)?](#q28--что-такое-lightweight-transactions-lwt)
- [Q29. Что такое Batch и какие ограничения у Batch?](#q29-что-такое-batch-и-какие-ограничения-у-batch)
- [Q30. Как в Cassandra реализовать счётчики (Counters)?](#q30-как-в-cassandra-реализовать-счётчики-counters)

**Spring Data Cassandra и Java Driver**
- [Q31. (!) Как подключить Spring Data Cassandra к проекту?](#q31--как-подключить-spring-data-cassandra-к-проекту)
- [Q32. Как определить Entity и Repository в Spring Data Cassandra?](#q32-как-определить-entity-и-repository-в-spring-data-cassandra)
- [Q33. Как Cassandra Java Driver работает с кластером?](#q33-как-cassandra-java-driver-работает-с-кластером)
- [Q34. Как выполнять миграции схемы в Cassandra?](#q34-как-выполнять-миграции-схемы-в-cassandra)

**Операционные вопросы**
- [Q35. Как обеспечить безопасность кластера Cassandra?](#q35-как-обеспечить-безопасность-кластера-cassandra)
- [Q36. Какие метрики мониторить в production?](#q36-какие-метрики-мониторить-в-production)
- [Q37. Как масштабировать кластер Cassandra?](#q37-как-масштабировать-кластер-cassandra)
- [Q38. Cassandra vs другие NoSQL базы данных](#q38-cassandra-vs-другие-nosql-базы-данных)

**Анти-паттерны и лучшие практики**
- [Q39. Какие основные анти-паттерны при работе с Cassandra?](#q39-какие-основные-анти-паттерны-при-работе-с-cassandra)
- [Q40. (!) Как правильно обрабатывать коллекции в CQL?](#q40--как-правильно-обрабатывать-коллекции-в-cql)
- [Q41. Как реализовать pagination в Cassandra?](#q41-как-реализовать-pagination-в-cassandra)
- [Q42. (!) Что такое Token-aware routing и почему он важен?](#q42--что-такое-token-aware-routing-и-почему-он-важен)
- [Q43. Как выполнять агрегации и аналитику поверх Cassandra?](#q43-как-выполнять-агрегации-и-аналитику-поверх-cassandra)
- [Q44. Что такое CDC (Change Data Capture) в Cassandra?](#q44-что-такое-cdc-change-data-capture-в-cassandra)

---

## Q1. (!) Что такое `Apache Cassandra` и когда её выбирать?

**Apache Cassandra** — распределённая `NoSQL` база данных, спроектированная для обработки больших объёмов данных с высокой доступностью и линейной горизонтальной масштабируемостью. Не имеет единой точки отказа (masterless architecture).

**Ключевые характеристики:**
- **Peer-to-peer** архитектура — все узлы равноправны
- **Линейная масштабируемость** — удвоение узлов удваивает пропускную способность
- **Tunable consistency** — настраиваемый уровень консистентности per-query
- **AP-система** в терминах [CAP-теоремы](../architecture/cap-theorem-interview.md) (Availability + Partition tolerance)
- Оптимизирована для **записи** (write-optimized, `LSM-tree`)

**Когда выбирать Cassandra:**
- Высокая интенсивность записи (IoT, логи, метрики, активность пользователей)
- Требуется горизонтальное масштабирование до петабайт
- Мульти-датацентровая репликация
- Доступность важнее строгой консистентности
- Паттерн доступа: запись >> чтение, чтение по ключу

**Когда НЕ выбирать:**
- Нужны сложные JOIN-запросы и ad-hoc аналитика
- Требуются ACID-транзакции на множестве записей
- Маленький объём данных (< 100 GB) — overhead кластера не оправдан
- Часто меняющиеся паттерны запросов

## Q2. (!) Какова архитектура кластера `Cassandra`?

`Cassandra` использует **peer-to-peer** (masterless) архитектуру — в отличие от `MongoDB` или `HBase`, здесь нет master-узла. Все узлы равноправны и могут принимать запросы на чтение и запись.

```mermaid
graph TB
    subgraph "Cassandra Cluster (Ring)"
        N1[Node 1<br/>Token: 0-24]
        N2[Node 2<br/>Token: 25-49]
        N3[Node 3<br/>Token: 50-74]
        N4[Node 4<br/>Token: 75-99]
    end

    C[Client] -->|"Любой узел =<br/>координатор"| N1
    N1 <-->|Gossip| N2
    N2 <-->|Gossip| N3
    N3 <-->|Gossip| N4
    N4 <-->|Gossip| N1
    N1 <-->|Gossip| N3
    N2 <-->|Gossip| N4
```

**Основные компоненты кластера:**

| Компонент | Описание |
|-----------|----------|
| `Node` | Один экземпляр Cassandra, хранящий часть данных |
| `Rack` | Логическая группа узлов (обычно — стойка в ДЦ) |
| `Data Center` | Набор rack-ов, обычно соответствует физическому ДЦ |
| `Cluster` | Полный набор узлов, реплицирующих данные |
| `Coordinator` | Узел, принявший запрос клиента и координирующий ответ |

**Роль координатора:** любой узел может быть координатором. Клиент отправляет запрос на любой узел, тот определяет, какие узлы хранят нужные данные (по `partition key`), пересылает запрос и собирает ответы в соответствии с заданным `Consistency Level`.

## Q3. (!) Что такое `Gossip`-протокол?

**Gossip** — это peer-to-peer протокол обмена состоянием между узлами кластера `Cassandra`. Каждую секунду каждый узел обменивается информацией о себе и других известных ему узлах с 1-3 случайно выбранными узлами.

```mermaid
sequenceDiagram
    participant A as Node A
    participant B as Node B (случайный)
    participant C as Node C

    Note over A: Каждую секунду
    A->>B: SYN (мои данные + дайджест о других)
    B->>A: ACK (новые данные от B + запрос недостающих)
    A->>B: ACK2 (недостающие данные)

    Note over A,C: Через несколько раундов<br/>информация распространяется<br/>по всему кластеру
```

**Что передаётся через Gossip:**
- Состояние узла (UP / DOWN)
- Нагрузка на узел (load)
- Схема данных (schema version)
- Token ranges
- Информация о датацентре и rack

**Failure Detection:** Cassandra использует `Phi Accrual Failure Detector`, который на основании истории heartbeat-ов вычисляет вероятность отказа узла. Порог настраивается через `phi_convict_threshold` в `cassandra.yaml`.

## Q4. Что такое `Snitch` и какие типы существуют?

**Snitch** определяет, к какому датацентру и rack-у принадлежит каждый узел. Эта информация используется для оптимизации маршрутизации запросов и размещения реплик.

**Основные типы:**

| Snitch | Описание |
|--------|----------|
| `SimpleSnitch` | Один ДЦ, один rack — для разработки |
| `GossipingPropertyFileSnitch` | Читает ДЦ/rack из `cassandra-rackdc.properties`, рекомендуется для production |
| `PropertyFileSnitch` | Читает топологию из файла `cassandra-topology.properties` |
| `Ec2Snitch` | Для AWS: region = DC, availability zone = rack |
| `GoogleCloudSnitch` | Для GCP: project = DC, zone = rack |

Пример `cassandra-rackdc.properties`:

```properties
dc=dc-moscow
rack=rack1
```

## Q5. Как работает `Consistent Hashing` в `Cassandra`?

`Consistent Hashing` — механизм распределения данных по узлам кластера. Каждому узлу назначается один или несколько **token** (токенов) на кольце хэшей (диапазон: -2^63 до 2^63-1).

```mermaid
graph LR
    subgraph "Hash Ring"
        direction LR
        T0["Token 0"] --> T25["Token 25"]
        T25 --> T50["Token 50"]
        T50 --> T75["Token 75"]
        T75 --> T0
    end

    P1["partition_key='user_123'<br/>hash = 37"] -.->|"Попадает в<br/>диапазон 25-50"| T50
```

**Алгоритм:**
1. Значение `partition_key` хэшируется функцией `Murmur3` (по умолчанию)
2. Полученный хэш определяет позицию на кольце
3. Данные записываются на узел, ответственный за следующий по часовой стрелке токен
4. Реплики размещаются на следующих узлах по кольцу (по стратегии репликации)

**Преимущество:** при добавлении/удалении узла перераспределяется только часть данных, а не все.

## Q6. Что такое `Virtual Nodes` (vnodes)?

**Vnodes** — механизм, при котором каждый физический узел отвечает за множество небольших диапазонов токенов вместо одного большого. По умолчанию `num_tokens = 256` (настраивается в `cassandra.yaml`).

**Преимущества vnodes:**
- Более равномерное распределение данных
- Быстрое перераспределение при добавлении/удалении узлов
- Ребалансировка происходит автоматически
- Узлы с разной мощностью могут получить разное число токенов

**Без vnodes:** при добавлении узла нужно вручную рассчитывать токены и выполнять `nodetool move`.

## Q7. (!) Какую модель данных использует `Cassandra`?

`Cassandra` использует модель **wide-column store** (хранилище широких столбцов). Данные организованы в виде таблиц с строками и столбцами, но в отличие от реляционных БД:

- Каждая строка может иметь **разный набор столбцов**
- Строки группируются в **партиции** по `partition key`
- Внутри партиции строки упорядочены по `clustering key`
- Нет JOIN, нет referential integrity
- Схема гибкая — столбцы можно добавлять без миграции существующих данных

**Иерархия структур данных:**

```
Cluster
  └── Keyspace (аналог schema/database)
       └── Table (аналог таблицы)
            └── Partition (группа строк с одним partition key)
                 └── Row (одна строка, определяется clustering key)
                      └── Column (пара name:value с timestamp)
```

**Пример создания keyspace и таблицы:**

```cql
CREATE KEYSPACE IF NOT EXISTS ecommerce
  WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'dc-moscow': 3,
    'dc-spb': 2
  };

CREATE TABLE ecommerce.orders (
    user_id    UUID,
    order_date TIMESTAMP,
    order_id   UUID,
    total      DECIMAL,
    status     TEXT,
    PRIMARY KEY ((user_id), order_date, order_id)
) WITH CLUSTERING ORDER BY (order_date DESC, order_id ASC);
```

## Q8. (!) Что такое `Primary Key`, `Partition Key` и `Clustering Key`?

`Primary Key` в `Cassandra` состоит из двух частей и определяет как уникальность строки, так и физическое размещение данных.

```mermaid
graph LR
    PK["PRIMARY KEY ((user_id, region), order_date, order_id)"]
    PK --> PartK["Partition Key<br/>(user_id, region)<br/>→ Определяет узел"]
    PK --> ClustK["Clustering Key<br/>(order_date, order_id)<br/>→ Сортировка внутри партиции"]
```

| Понятие | Назначение | Пример |
|---------|-----------|--------|
| `Partition Key` | Определяет, на каком узле хранятся данные (хэшируется через `Murmur3`) | `(user_id)` или `(user_id, region)` |
| `Clustering Key` | Определяет порядок строк внутри партиции | `order_date DESC` |
| `Primary Key` | `Partition Key` + `Clustering Key` — уникально идентифицирует строку | `((user_id), order_date, order_id)` |

**Варианты Primary Key:**

```cql
-- 1. Простой PK (одна колонка = partition key, нет clustering)
PRIMARY KEY (user_id)

-- 2. Составной PK (partition key + clustering key)
PRIMARY KEY (user_id, created_at)

-- 3. Composite Partition Key (несколько колонок в partition key)
PRIMARY KEY ((user_id, region), created_at, event_id)
```

**Правила выбора Partition Key:**
- Высокая кардинальность (много уникальных значений)
- Равномерное распределение запросов
- Размер партиции < 100 MB (рекомендация) и < 100 000 строк

## Q9. (!) Как правильно моделировать данные в `Cassandra`?

В отличие от реляционных БД, моделирование в `Cassandra` идёт **от запросов, а не от сущностей** (query-driven design). Сначала определяются запросы приложения, затем под них проектируются таблицы. Подробнее о проектировании распределённых систем — в [вопросах по распределённым системам](../architecture/distributed-systems-interview.md).

**Принципы моделирования:**

1. **Одна таблица — один запрос** (денормализация)
2. **Partition Key** соответствует WHERE-условию запроса
3. **Clustering Key** определяет сортировку результата
4. Дублирование данных — норма, а не проблема
5. Избегать больших партиций (> 100 MB)

**Пример: пользователь и его заказы**

```cql
-- Запрос: "Показать последние 20 заказов пользователя"
CREATE TABLE orders_by_user (
    user_id    UUID,
    order_date TIMESTAMP,
    order_id   UUID,
    total      DECIMAL,
    items      LIST<FROZEN<order_item>>,
    PRIMARY KEY ((user_id), order_date)
) WITH CLUSTERING ORDER BY (order_date DESC);

-- Запрос: "Показать заказы по статусу за период"
CREATE TABLE orders_by_status (
    status     TEXT,
    order_date TIMESTAMP,
    order_id   UUID,
    user_id    UUID,
    total      DECIMAL,
    PRIMARY KEY ((status), order_date, order_id)
) WITH CLUSTERING ORDER BY (order_date DESC);
```

**Анти-паттерны:**
- Использование `ALLOW FILTERING` — полное сканирование кластера
- Слишком маленький `partition key` (все данные на одном узле)
- Слишком большие партиции (hot spots, OOM)
- Моделирование "как в PostgreSQL" с нормализацией

## Q10. Какие факторы влияют на равномерное распределение данных?

Для равномерного распределения данных по узлам кластера необходимо учитывать:

1. **Кардинальность Partition Key** — значения должны быть высококардинальными (UUID, user_id), а не низкокардинальными (статус, страна)
2. **Размер партиции** — рекомендуется < 100 MB и < 100 000 строк
3. **Partitioner** — `Murmur3Partitioner` (по умолчанию) даёт равномерное распределение
4. **Vnodes** — включённые vnodes улучшают балансировку
5. **Паттерн запросов** — если 90% запросов идут к 10% ключей, возникают hot spots

**Пример hot spot:** если `partition_key = country_code`, то партиция `RU` будет в разы больше партиции `KZ`. Решение — добавить `bucket` (например, день или hash):

```cql
-- Плохо: hot spot на популярных странах
PRIMARY KEY (country_code, event_time)

-- Хорошо: bucket по дню для распределения
PRIMARY KEY ((country_code, event_day), event_time)
```

## Q11. Что такое `Wide Rows` и какие ограничения на размер партиции?

**Wide Row** (широкая строка) — партиция с большим количеством строк (по clustering key). В `Cassandra` все строки одной партиции хранятся вместе на диске.

**Ограничения:**
- Теоретический лимит: **2 млрд** столбцов на партицию
- Практический лимит: **100 MB** на партицию (рекомендация DataStax)
- Большие партиции вызывают: медленные чтения, GC pressure, проблемы при компакции

**Борьба с большими партициями — bucketing:**

```cql
-- Вместо одной партиции на user_id навсегда:
PRIMARY KEY ((user_id), event_time)

-- Разбиваем по месяцу:
PRIMARY KEY ((user_id, month), event_time)
```

## Q12. (!) Основные команды `CQL`

`CQL` (`Cassandra Query Language`) — SQL-подобный язык запросов. Основные отличия от SQL: нет JOIN, нет подзапросов, WHERE ограничен по ключам.

**DDL:**

```cql
-- Keyspace
CREATE KEYSPACE shop WITH replication = {
  'class': 'NetworkTopologyStrategy', 'dc1': 3
};

-- Таблица
CREATE TABLE shop.products (
    category TEXT,
    product_id UUID,
    name TEXT,
    price DECIMAL,
    tags SET<TEXT>,
    PRIMARY KEY ((category), product_id)
);

-- Индекс
CREATE INDEX ON shop.products (name);

-- Изменение таблицы
ALTER TABLE shop.products ADD description TEXT;

-- Удаление
DROP TABLE IF EXISTS shop.products;
```

**DML:**

```cql
-- Вставка (INSERT = UPSERT в Cassandra)
INSERT INTO shop.products (category, product_id, name, price, tags)
VALUES ('electronics', uuid(), 'Laptop', 999.99, {'new', 'sale'})
USING TTL 86400;

-- Чтение
SELECT * FROM shop.products
WHERE category = 'electronics'
AND product_id = some_uuid;

-- Обновление
UPDATE shop.products
SET price = 899.99, tags = tags + {'discount'}
WHERE category = 'electronics' AND product_id = some_uuid;

-- Удаление
DELETE FROM shop.products
WHERE category = 'electronics' AND product_id = some_uuid;

-- Пакетная операция
BEGIN BATCH
  INSERT INTO shop.products (...) VALUES (...);
  INSERT INTO shop.products (...) VALUES (...);
APPLY BATCH;
```

**Важные особенности:**
- `INSERT` и `UPDATE` — по сути одно и то же (upsert)
- `DELETE` создаёт tombstone, а не сразу удаляет
- `WHERE` может содержать только ключевые колонки (partition key обязателен)
- `ALLOW FILTERING` — позволяет запросы без ключа, но **крайне неэффективно** в production

## Q13. Что такое `Secondary Index` и когда его использовать?

**Secondary Index** (SI) позволяет выполнять запросы по не-ключевым колонкам без `ALLOW FILTERING`.

```cql
CREATE INDEX idx_email ON users (email);

SELECT * FROM users WHERE email = 'user@example.com';
```

**Когда использовать:**
- Низкая кардинальность (статусы, типы) — хорошо
- Фильтрация уже в рамках партиции
- Не для основного паттерна доступа

**Когда НЕ использовать:**
- Высокая кардинальность (email, UUID) — scatter-gather по всем узлам
- High-throughput запросы — каждый запрос проходит по всем узлам
- Как замена правильному моделированию данных

**Альтернативы:**
- **SASI** (`SSTable Attached Secondary Index`) — более эффективен для text search
- **SAI** (`Storage Attached Index`, Cassandra 4.0+) — рекомендуемый подход
- Отдельная таблица с нужным partition key (предпочтительно)

## Q14. Что такое `Materialized Views`?

**Materialized View** — автоматически обновляемая таблица-проекция с другим Primary Key.

```cql
CREATE MATERIALIZED VIEW orders_by_status AS
    SELECT * FROM orders
    WHERE status IS NOT NULL AND order_id IS NOT NULL
    PRIMARY KEY (status, order_id);
```

**Преимущества:** автоматическая синхронизация, без дублирования логики в приложении.

**Проблемы:**
- Задержка синхронизации (eventual consistency)
- Дополнительная нагрузка на запись
- Известные баги в некоторых версиях (вплоть до потери данных)
- **DataStax рекомендует избегать MV** в production — лучше денормализованные таблицы + запись из приложения

## Q15. Как выполнять агрегации и аналитические запросы?

`Cassandra` — **не OLAP** система. Встроенные агрегатные функции (`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`) работают только в рамках одной партиции и не масштабируются.

**Подходы к аналитике:**

1. **Предварительная агрегация** — отдельные таблицы со счётчиками, обновляемые при записи
2. **Apache Spark + Cassandra Connector** — batch/streaming аналитика
3. **DSE Analytics** (коммерческая версия) — встроенный Spark
4. **CDC (Change Data Capture)** → `Kafka` → аналитическая БД (`ClickHouse`, `Elasticsearch`)

```cql
-- Встроенная агрегация (только внутри партиции!)
SELECT COUNT(*), AVG(price) FROM orders
WHERE user_id = some_uuid;

-- User-Defined Aggregate (UDA) для кастомной логики
CREATE FUNCTION avg_state(state tuple<int, double>, val double)
  CALLED ON NULL INPUT RETURNS tuple<int, double>
  LANGUAGE java AS 'return state;';
```

## Q16. (!) Как работает путь записи (`Write Path`)?

Путь записи — критически важная тема для собеседований. `Cassandra` оптимизирована для записи благодаря `LSM-tree` архитектуре.

```mermaid
graph TD
    Client[Client] -->|1. Write Request| Coord[Coordinator Node]
    Coord -->|2. Forward| Replica1[Replica 1]
    Coord -->|2. Forward| Replica2[Replica 2]
    Coord -->|2. Forward| Replica3[Replica 3]

    subgraph "На каждой реплике"
        CL[3. Commit Log<br/>sequential write] --> MT[4. Memtable<br/>in-memory]
        MT -->|5. Flush при<br/>пороге памяти| SST[SSTable<br/>immutable file on disk]
    end
```

**Шаги записи:**
1. Клиент отправляет запрос на **координатор**
2. Координатор определяет реплики и отправляет запрос
3. На каждой реплике запись идёт в **Commit Log** (WAL, sequential I/O — быстро)
4. Данные записываются в **Memtable** (in-memory)
5. Когда Memtable заполняется — **flush** на диск в **SSTable** (immutable)
6. Координатор отвечает клиенту после получения подтверждений от нужного числа реплик (по `Consistency Level`)

**Ключевые свойства:**
- Запись **всегда sequential I/O** — поэтому Cassandra быстра на запись
- SSTables **immutable** — никогда не модифицируются
- Обновление = новая запись с большим timestamp
- Удаление = запись tombstone

## Q17. (!) Как работает путь чтения (`Read Path`)?

Чтение в `Cassandra` сложнее записи, так как данные могут быть разбросаны по нескольким `SSTable` и `Memtable`.

```mermaid
graph TD
    Client[Client] -->|1. Read Request| Coord[Coordinator]
    Coord -->|2. Запрос к репликам| R1[Replica 1]
    Coord -->|2. Digest request| R2[Replica 2]

    subgraph "На реплике"
        BF[3. Bloom Filter<br/>SSTable содержит ключ?]
        BF -->|Возможно да| PC[4. Partition Key Cache]
        PC --> CI[5. Compression Info]
        CI --> SST[6. Чтение SSTable]
        MT[Memtable] --> Merge[7. Merge результатов<br/>по timestamp]
        SST --> Merge
        RC[Row Cache] -.->|Cache hit| Merge
    end
```

**Шаги чтения:**
1. Координатор определяет реплики и отправляет запросы
2. Полный запрос — к ближайшей реплике, digest — к остальным (для сверки)
3. **Bloom Filter** — вероятностная структура, быстро исключает SSTables, не содержащие ключ
4. **Partition Key Cache** — кэш смещений ключей на диске
5. Чтение из **SSTable** + **Memtable** + merge по timestamp (latest wins)
6. При расхождении digest — **Read Repair**

**Оптимизации:**
- **Key Cache** — кэш позиций ключей в SSTable
- **Row Cache** — кэш целых строк (для hot data)
- **Bloom Filter** — false positive rate настраивается (`bloom_filter_fp_chance`)

## Q18. Что такое `Tombstone` и как они влияют на производительность?

**Tombstone** — специальный маркер удаления в `Cassandra`. Поскольку `SSTables` immutable, удалённые данные не могут быть сразу удалены с диска.

```cql
-- Эта операция создаёт tombstone
DELETE FROM orders WHERE user_id = ? AND order_id = ?;

-- TTL-expired данные тоже становятся tombstones
INSERT INTO events (...) VALUES (...) USING TTL 3600;
```

**Жизненный цикл tombstone:**
1. `DELETE` → создаётся tombstone с timestamp
2. Tombstone реплицируется на все реплики
3. По прошествии `gc_grace_seconds` (по умолчанию 10 дней) tombstone может быть удалён при компакции

**Проблемы:**
- Накопление tombstones замедляет чтение (надо сканировать все)
- `TombstoneOverwhelmingException` при > 100 000 tombstones на запрос
- Частые DELETE + INSERT на один ключ — анти-паттерн

**Решения:**
- Моделировать данные так, чтобы минимизировать DELETE
- Использовать TTL вместо явного DELETE
- Настроить `gc_grace_seconds` под нагрузку (но не слишком маленьким — иначе воскреснут удалённые данные)

## Q19. Как работает механизм сжатия данных (`Compression`)?

`Cassandra` поддерживает прозрачное сжатие данных на уровне `SSTable`.

```cql
CREATE TABLE logs (...)
WITH compression = {
  'class': 'LZ4Compressor',    -- алгоритм
  'chunk_length_in_kb': 64      -- размер блока
};
```

**Поддерживаемые алгоритмы:**

| Алгоритм | Характеристика |
|----------|---------------|
| `LZ4Compressor` | Быстрый, умеренное сжатие (по умолчанию) |
| `SnappyCompressor` | Быстрый, немного лучше сжатие |
| `DeflateCompressor` | Медленнее, лучшее сжатие |
| `ZstdCompressor` | Хорошее сжатие + скорость (Cassandra 4.0+) |
| `NoopCompressor` | Без сжатия |

Данные сжимаются блоками (по умолчанию 64 KB) — чтение одного значения требует распаковки только одного блока. Сжатие снижает I/O за счёт CPU. Для SSD-дисков часто предпочтительнее `LZ4` из-за минимальной латентности.

## Q20. (!) Что такое `Replication` и какие стратегии существуют?

**Репликация** — автоматическое создание копий данных на нескольких узлах для обеспечения отказоустойчивости и доступности. Фактор репликации (`RF`) задаётся при создании keyspace.

```mermaid
graph LR
    subgraph "RF = 3, 6 узлов"
        N1["Node 1<br/>Data A, F"] 
        N2["Node 2<br/>Data A, B"]
        N3["Node 3<br/>Data A, B, C"]
        N4["Node 4<br/>Data B, C, D"]
        N5["Node 5<br/>Data C, D, E"]
        N6["Node 6<br/>Data D, E, F"]
    end

    style N1 fill:#f9f
    style N2 fill:#f9f
    style N3 fill:#f9f
    
    Note["Data A реплицирована<br/>на Node 1, 2, 3"]
```

**Стратегии репликации:**

| Стратегия | Описание | Использование |
|-----------|----------|---------------|
| `SimpleStrategy` | Реплики на следующих по кольцу узлах | Только для одного ДЦ, разработки |
| `NetworkTopologyStrategy` | RF задаётся per-DC, учитывает rack-и | **Production**, multi-DC |

```cql
-- SimpleStrategy (НЕ для production)
CREATE KEYSPACE dev_keyspace WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 3
};

-- NetworkTopologyStrategy (рекомендуется)
CREATE KEYSPACE prod_keyspace WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'dc-moscow': 3,
  'dc-spb': 2
};
```

**Рекомендация:** RF = 3 на датацентр — позволяет пережить потерю одного узла при `QUORUM` чтении/записи.

## Q21. (!) Какие уровни консистентности (`Consistency Levels`) существуют?

`Consistency Level` (CL) определяет, сколько реплик должны подтвердить операцию чтения/записи, чтобы она считалась успешной. Это механизм **tunable consistency** — подробнее в [CAP-теореме](../architecture/cap-theorem-interview.md).

| CL | Запись: сколько подтверждений | Чтение: сколько ответов | Когда использовать |
|----|------------------------------|------------------------|--------------------|
| `ANY` | 1 (включая hinted handoff) | — | Максимальная доступность записи |
| `ONE` | 1 реплика | 1 реплика | Низкая латентность, eventual consistency |
| `TWO` | 2 реплики | 2 реплики | Компромисс |
| `QUORUM` | ⌊RF/2⌋ + 1 | ⌊RF/2⌋ + 1 | Strong consistency при R+W > N |
| `LOCAL_QUORUM` | Кворум в локальном ДЦ | Кворум в локальном ДЦ | **Production multi-DC** |
| `EACH_QUORUM` | Кворум в каждом ДЦ | — | Строгая консистентность cross-DC |
| `ALL` | Все реплики | Все реплики | Максимальная консистентность, минимальная доступность |

**Типичная production-конфигурация:** `LOCAL_QUORUM` для чтения и записи с RF = 3.

## Q22. (!) Что означает формула `R + W > N`?

Это формула **strong consistency** (линеаризуемости чтения) в распределённых системах:
- **R** — число реплик для чтения
- **W** — число реплик для записи
- **N** — фактор репликации

```
RF = 3
QUORUM = ⌊3/2⌋ + 1 = 2

Write QUORUM (W=2) + Read QUORUM (R=2) = 4 > 3 (N)
→ Гарантия: хотя бы одна реплика содержит latest данные
```

**Примеры:**

| Конфигурация | R + W > N? | Консистентность |
|--------------|-----------|-----------------|
| W=QUORUM, R=QUORUM | 2+2=4 > 3 | Strong |
| W=ALL, R=ONE | 3+1=4 > 3 | Strong |
| W=ONE, R=ONE | 1+1=2 < 3 | Eventual |
| W=ONE, R=ALL | 1+3=4 > 3 | Strong |

## Q23. Что такое `Hinted Handoff`?

**Hinted Handoff** — механизм временного хранения записей, предназначенных для недоступной реплики.

**Как работает:**
1. Координатор пытается записать данные на реплику, но она недоступна
2. Координатор сохраняет **hint** (подсказку) локально
3. Когда реплика восстанавливается, координатор передаёт накопленные hints
4. Hint хранится ограниченное время (`max_hint_window` = 3 часа по умолчанию)

**Ограничения:**
- Не заменяет полноценный `repair`
- При длительном простое реплики hints истекают и данные теряются
- CL = `ANY` позволяет считать hint подтверждением записи (опасно)

**Настройка в `cassandra.yaml`:**
```yaml
hinted_handoff_enabled: true
max_hint_window_in_ms: 10800000  # 3 часа
```

## Q24. Что такое `Read Repair`?

**Read Repair** — механизм восстановления согласованности данных при чтении.

**Как работает:**
1. Координатор запрашивает данные у нескольких реплик
2. Сравнивает ответы (digest или полные данные)
3. Если ответы расходятся — отправляет актуальную версию (по timestamp) на отстающие реплики

**Типы:**
- **Blocking Read Repair** — координатор ждёт исправления перед ответом клиенту (при CL > ONE)
- **Background Read Repair** — исправление после ответа клиенту (настраивается `read_repair_chance`, в Cassandra 4.0+ убран)

## Q25. (!) Что такое `Anti-Entropy Repair` и зачем он нужен?

**Repair** (`nodetool repair`) — процесс полной синхронизации данных между репликами. Это основной механизм обеспечения eventual consistency.

**Зачем нужен:**
- Hinted handoff может не покрыть длительные простои
- Read Repair работает только для читаемых данных
- Без регулярного repair — риск чтения устаревших данных
- После `gc_grace_seconds` tombstones удаляются — без repair могут «воскреснуть» удалённые данные

**Типы repair:**
- **Full repair** — сравнивает все данные через `Merkle Tree`
- **Incremental repair** — только данные, изменённые с последнего repair
- **Subrange repair** — repair части token range

**Рекомендации:**
- Запускать repair минимум раз в `gc_grace_seconds` (по умолчанию 10 дней)
- Использовать инструменты автоматизации: `Reaper` (от DataStax)
- Планировать repair в периоды низкой нагрузки

```bash
# Full repair keyspace
nodetool repair ecommerce

# Incremental repair
nodetool repair -inc ecommerce

# Repair конкретной таблицы
nodetool repair ecommerce orders
```

## Q26. (!) Какие стратегии компакции (`Compaction`) существуют?

**Compaction** — процесс слияния `SSTable` для удаления устаревших данных, tombstones и оптимизации чтения.

```mermaid
graph LR
    SST1[SSTable 1] --> Comp[Compaction]
    SST2[SSTable 2] --> Comp
    SST3[SSTable 3] --> Comp
    Comp --> NewSST[Новый SSTable<br/>без дубликатов,<br/>без tombstones]
```

| Стратегия | Когда использовать | Характеристика |
|-----------|-------------------|----------------|
| `STCS` (SizeTiered) | **Write-heavy** нагрузка | Сливает SSTable похожего размера; может временно удвоить дисковое пространство |
| `LCS` (Leveled) | **Read-heavy** нагрузка | SSTable организованы по уровням; 90% чтений — 1 SSTable; больше I/O при записи |
| `TWCS` (TimeWindow) | **Time-series** данные | Группирует SSTable по временным окнам; эффективен с TTL |
| `UCS` (Unified, 4.0+) | Универсальная | Объединяет идеи STCS и LCS, автоадаптация |

```cql
-- Задание стратегии при создании таблицы
CREATE TABLE sensor_data (...)
WITH compaction = {
  'class': 'TimeWindowCompactionStrategy',
  'compaction_window_unit': 'DAYS',
  'compaction_window_size': 1
};

-- Изменение стратегии
ALTER TABLE orders WITH compaction = {
  'class': 'LeveledCompactionStrategy',
  'sstable_size_in_mb': 160
};
```

## Q27. Как управлять жизненным циклом данных через `TTL`?

**TTL** (`Time To Live`) — время жизни данных в секундах. По истечении TTL данные становятся tombstone и удаляются при компакции.

```cql
-- TTL на уровне записи
INSERT INTO sessions (session_id, user_id, data)
VALUES (uuid(), some_uuid, 'payload')
USING TTL 3600;  -- 1 час

-- TTL на уровне обновления
UPDATE sessions USING TTL 7200
SET data = 'updated'
WHERE session_id = some_uuid;

-- Проверить оставшийся TTL
SELECT TTL(data) FROM sessions WHERE session_id = some_uuid;

-- TTL по умолчанию для таблицы
CREATE TABLE temp_data (...)
WITH default_time_to_live = 86400;  -- 1 день
```

**Важно:**
- TTL = 0 означает "без TTL" (данные живут вечно)
- TTL нельзя задать для ключевых колонок (partition/clustering key)
- Expired TTL данные = tombstones → планируйте компакцию
- Для time-series с TTL рекомендуется `TWCS`

## Q28. (!) Что такое `Lightweight Transactions` (`LWT`)?

**LWT** — механизм условной записи с проверкой через `Paxos`-консенсус, обеспечивающий linearizable consistency для одной партиции.

```cql
-- Вставка только если записи нет (регистрация пользователя)
INSERT INTO users (user_id, email, name)
VALUES (uuid(), 'user@mail.ru', 'Иван')
IF NOT EXISTS;

-- Обновление с проверкой условия (оптимистичная блокировка)
UPDATE accounts
SET balance = 900
WHERE account_id = ?
IF balance = 1000;
```

**Результат операции LWT:**
```
 [applied] | balance
-----------+---------
     False |     500   -- условие не выполнено, текущее значение = 500
```

**Как работает LWT (Paxos):**

```mermaid
sequenceDiagram
    participant C as Coordinator
    participant R1 as Replica 1
    participant R2 as Replica 2
    participant R3 as Replica 3

    C->>R1: Prepare (ballot)
    C->>R2: Prepare (ballot)
    C->>R3: Prepare (ballot)
    R1-->>C: Promise
    R2-->>C: Promise
    C->>R1: Propose (value)
    C->>R2: Propose (value)
    R1-->>C: Accept
    R2-->>C: Accept
    C->>R1: Commit
    C->>R2: Commit
    C->>R3: Commit
```

**Ограничения LWT:**
- Латентность в **4-6 раз выше** обычной записи (4 round-trips вместо 1)
- Работает только в рамках **одной партиции**
- Не использовать в горячих путях и массовых операциях
- Не смешивать LWT и обычные записи на одних данных — race condition

## Q29. Что такое `Batch` и какие ограничения у `Batch`?

**Batch** — группировка нескольких CQL-операций в один запрос.

| Тип | Гарантия | Использование |
|-----|----------|---------------|
| `LOGGED` (по умолчанию) | Атомарность (все или ничего) | Операции в **разных** партициях (с overhead) |
| `UNLOGGED` | Нет атомарности | Операции в **одной** партиции (оптимально) |
| `COUNTER` | Для counter-таблиц | Только counter-операции |

```cql
-- UNLOGGED batch для одной партиции (рекомендуется)
BEGIN UNLOGGED BATCH
  INSERT INTO user_events (user_id, event_time, type)
  VALUES ('user1', now(), 'login');
  INSERT INTO user_events (user_id, event_time, type)
  VALUES ('user1', now(), 'page_view');
APPLY BATCH;
```

**Ограничения и анти-паттерны:**
- Размер batch: **~5 MB** по умолчанию
- LOGGED batch на разных партициях = **координатор становится bottleneck**
- Batch — **не транзакция** в RDBMS-смысле (нет изоляции)
- Для массовой загрузки: **асинхронные одиночные записи** с пулом, а не batch

## Q30. Как в `Cassandra` реализовать счётчики (`Counters`)?

**Counter** — специальный тип колонки для атомарного инкремента/декремента.

```cql
CREATE TABLE page_views (
    page_url TEXT,
    date     DATE,
    views    COUNTER,
    PRIMARY KEY ((page_url), date)
);

-- Инкремент
UPDATE page_views SET views = views + 1
WHERE page_url = '/home' AND date = '2026-04-12';

-- Декремент
UPDATE page_views SET views = views - 5
WHERE page_url = '/home' AND date = '2026-04-12';
```

**Ограничения counter-таблиц:**
- Таблица может содержать **только counter и ключевые** колонки
- Нет `INSERT`, только `UPDATE` (инкремент/декремент)
- Нет TTL для counter-колонок
- Eventual consistency — при concurrent обновлениях возможна временная рассогласованность
- Не использовать для финансовых расчётов — используйте LWT или внешнюю БД

## Q31. (!) Как подключить `Spring Data Cassandra` к проекту?

**Spring Data Cassandra** — модуль Spring Data для работы с `Cassandra` через репозитории и `CassandraTemplate`. Подробнее о Spring Data — в [вопросах по Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) (общие концепции репозиториев).

**Зависимость (Gradle):**

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-cassandra'
```

**Конфигурация `application.yml`:**

```yaml
spring:
  cassandra:
    keyspace-name: ecommerce
    contact-points: cassandra-node1,cassandra-node2,cassandra-node3
    port: 9042
    local-datacenter: dc-moscow
    schema-action: create_if_not_exists  # none для production
    request:
      timeout: 5s
      consistency: LOCAL_QUORUM
```

**Конфигурация через Java (для тонкой настройки):**

```java
@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "ecommerce";
    }

    @Override
    protected String getContactPoints() {
        return "cassandra-node1";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }
}
```

## Q32. Как определить `Entity` и `Repository` в `Spring Data Cassandra`?

**Entity (модель):**

```java
@Table("orders")
public class Order {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0,
                       type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "order_date", ordinal = 1,
                       type = PrimaryKeyType.CLUSTERED,
                       ordering = Ordering.DESCENDING)
    private Instant orderDate;

    @PrimaryKeyColumn(name = "order_id", ordinal = 2,
                       type = PrimaryKeyType.CLUSTERED)
    private UUID orderId;

    @Column("total")
    private BigDecimal total;

    @Column("status")
    private String status;

    // getters, setters
}
```

**Repository:**

```java
public interface OrderRepository
        extends CassandraRepository<Order, UUID> {

    // Spring Data автоматически генерирует CQL
    List<Order> findByUserId(UUID userId);

    @Query("SELECT * FROM orders WHERE user_id = ?0 AND order_date > ?1")
    List<Order> findRecentOrders(UUID userId, Instant since);
}
```

**CassandraTemplate (для сложных запросов):**

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CassandraTemplate cassandraTemplate;

    public List<Order> findByUser(UUID userId) {
        return cassandraTemplate.select(
            Query.query(Criteria.where("user_id").is(userId))
                 .limit(20),
            Order.class
        );
    }

    public void saveWithTtl(Order order, int ttlSeconds) {
        InsertOptions options = InsertOptions.builder()
            .ttl(Duration.ofSeconds(ttlSeconds))
            .build();
        cassandraTemplate.insert(order, options);
    }
}
```

## Q33. Как `Cassandra Java Driver` работает с кластером?

**DataStax Java Driver** (v4.x) — основной драйвер для взаимодействия с `Cassandra` из Java.

**Ключевые компоненты:**

| Компонент | Описание |
|-----------|----------|
| `CqlSession` | Основной объект, пул соединений, **singleton** на приложение |
| `PreparedStatement` | Подготовленный запрос, кэшируется на узлах |
| `Load Balancing Policy` | Выбор координатора (`DCAwareRoundRobinPolicy`) |
| `Retry Policy` | Стратегия повтора при сбоях |
| `Reconnection Policy` | Стратегия переподключения |

**Пример использования:**

```java
// Создание сессии (один раз на приложение)
CqlSession session = CqlSession.builder()
    .addContactPoint(new InetSocketAddress("cassandra-host", 9042))
    .withLocalDatacenter("dc-moscow")
    .withKeyspace("ecommerce")
    .build();

// Prepared Statement (подготовить один раз, использовать много)
PreparedStatement ps = session.prepare(
    "INSERT INTO orders (user_id, order_date, order_id, total) " +
    "VALUES (?, ?, ?, ?)"
);

// Bind и выполнение
BoundStatement bs = ps.bind(userId, Instant.now(), orderId, total)
    .setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
session.execute(bs);

// Асинхронное выполнение
CompletionStage<AsyncResultSet> future = session.executeAsync(bs);
```

**Настройка в `application.conf`:**

```hocon
datastax-java-driver {
  basic {
    contact-points = ["cassandra-node1:9042", "cassandra-node2:9042"]
    load-balancing-policy.local-datacenter = "dc-moscow"
    request.timeout = 5 seconds
    request.consistency = LOCAL_QUORUM
  }
  advanced {
    connection.pool.local.size = 3
    retry-policy.class = DefaultRetryPolicy
    reconnection-policy {
      class = ExponentialReconnectionPolicy
      base-delay = 1 second
      max-delay = 60 seconds
    }
  }
}
```

## Q34. Как выполнять миграции схемы в `Cassandra`?

`Cassandra` не имеет встроенного механизма миграций (как `Flyway` для RDBMS). Подходы:

**1. Скрипты CQL + таблица версий:**

```cql
-- Таблица для отслеживания миграций
CREATE TABLE IF NOT EXISTS schema_migrations (
    version INT PRIMARY KEY,
    description TEXT,
    applied_at TIMESTAMP
);

-- V001__create_orders.cql
CREATE TABLE IF NOT EXISTS orders (
    user_id UUID,
    order_date TIMESTAMP,
    order_id UUID,
    total DECIMAL,
    PRIMARY KEY ((user_id), order_date, order_id)
);

-- V002__add_status_column.cql
ALTER TABLE orders ADD status TEXT;
```

**2. Инструменты:**
- **cassandra-migration** (библиотека для Java)
- **cqlmigrate** (Sky UK)
- Собственный скрипт при деплое, читающий `schema_migrations` и применяющий новые

**Важно:**
- Миграции должны быть **идемпотентными** (`CREATE TABLE IF NOT EXISTS`, `ALTER TABLE ... ADD ...`)
- Изменение `partition key` невозможно без создания новой таблицы + перенос данных
- Тестировать миграции на копии кластера перед production

## Q35. Как обеспечить безопасность кластера `Cassandra`?

**Аутентификация и авторизация:**

```yaml
# cassandra.yaml
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer
```

```cql
-- Создание пользователя
CREATE ROLE app_user WITH PASSWORD = 'strong_password'
    AND LOGIN = true;

-- Назначение прав
GRANT SELECT ON KEYSPACE ecommerce TO app_user;
GRANT MODIFY ON TABLE ecommerce.orders TO app_user;
```

**Шифрование:**

```yaml
# cassandra.yaml — шифрование между узлами
server_encryption_options:
  internode_encryption: all
  keystore: /path/to/keystore.jks
  keystore_password: changeit

# Шифрование клиент-узел
client_encryption_options:
  enabled: true
  keystore: /path/to/keystore.jks
  keystore_password: changeit
```

**Рекомендации:**
- Отдельные учётные записи для каждого приложения (least privilege)
- Регулярная ротация паролей
- `TLS` для межузлового и клиентского трафика
- Аудит доступа (в enterprise-версиях)

## Q36. Какие метрики мониторить в production?

**Ключевые метрики `Cassandra` (доступны через JMX, `Prometheus` exporter):**

| Категория | Метрика | Что смотреть |
|-----------|---------|-------------|
| Латентность | `ReadLatency`, `WriteLatency` | p99 < целевого SLA |
| Throughput | `ReadThroughput`, `WriteThroughput` | Тренды роста |
| Ошибки | `Timeouts`, `Unavailables`, `Failures` | Всплески |
| Compaction | `PendingCompactions` | Не должно расти постоянно |
| Memory | `HeapUsage`, `OffHeapUsage` | GC pressure |
| Disk | `LiveDiskSpaceUsed`, `TotalDiskSpaceUsed` | Планирование ёмкости |
| Thread Pools | `PendingTasks` по пулам | Blocked/Dropped — проблема |
| SSTable | `SSTableCount` per table | Рост = нужна компакция |
| Tombstones | `TombstoneScannedHistogram` | Много = проблема моделирования |

**Инструменты:**
- `nodetool status/tablestats/tpstats` — CLI
- `Prometheus` + `Grafana` (через JMX exporter или Metrics reporter)
- `DataStax OpsCenter` (коммерческий)

## Q37. Как масштабировать кластер `Cassandra`?

**Горизонтальное масштабирование (добавление узлов):**

1. Установить `Cassandra` на новый сервер с той же конфигурацией
2. Указать seed-ноды кластера в `cassandra.yaml`
3. Запустить — узел автоматически присоединяется (bootstrap)
4. `nodetool status` — проверить статус
5. `nodetool cleanup` на существующих узлах (удалить данные, перемещённые на новый узел)

```bash
# Проверка статуса кластера
nodetool status

# Декомиссия узла (корректное удаление)
nodetool decommission

# Перемещение данных после изменения топологии
nodetool cleanup
```

**Масштабирование без vnodes:** ручной расчёт токенов и `nodetool move`.
**С vnodes (рекомендуется):** автоматическая ребалансировка.

**Вертикальное масштабирование:**
- Увеличение RAM (больше данных в page cache)
- SSD вместо HDD (ускорение чтения)
- Больше CPU (компакция, сжатие)

## Q38. `Cassandra` vs другие `NoSQL` базы данных

| Критерий | `Cassandra` | `MongoDB` | `Redis` | `HBase` |
|----------|-------------|-----------|---------|---------|
| Модель данных | Wide-column | Document | Key-Value | Wide-column |
| Архитектура | Masterless (P2P) | Replica Set (Primary-Secondary) | Master-Replica | Master (HMaster) |
| CAP | AP | CP | CP/AP | CP |
| Запись | Очень быстрая (LSM) | Быстрая | Очень быстрая (in-memory) | Быстрая (LSM) |
| Чтение | Быстрая по ключу | Гибкие запросы | Очень быстрая | Быстрая по ключу |
| Консистентность | Tunable | Strong (по умолчанию) | Eventual (replicas) | Strong |
| Multi-DC | Нативная | Ограниченная | Ограниченная | Через HDFS |
| Аналитика | Через Spark | Aggregation Pipeline | Нет | Через MapReduce/Spark |
| Сценарий | IoT, логи, time-series | CRUD, каталоги | Кэш, сессии | Большие таблицы на HDFS |

Подробнее о [MongoDB](mongodb-interview.md) и [Redis](redis-interview.md) — в соответствующих разделах.

## Q39. Какие основные анти-паттерны при работе с `Cassandra`?

На собеседовании важно продемонстрировать понимание ограничений Cassandra и умение их избегать.

**1. `ALLOW FILTERING` — антипаттерн №1**

```cql
-- ПЛОХО: полное сканирование всех партиций кластера
SELECT * FROM orders WHERE status = 'PENDING' ALLOW FILTERING;

-- ХОРОШО: создать отдельную таблицу под этот запрос
CREATE TABLE orders_by_status (
    status     TEXT,
    created_at TIMESTAMP,
    order_id   UUID,
    PRIMARY KEY ((status), created_at, order_id)
) WITH CLUSTERING ORDER BY (created_at DESC);
```

**2. Неограниченные широкие партиции (Unbounded Wide Partitions)**

```cql
-- ПЛОХО: все события пользователя за всё время — партиция растёт вечно
CREATE TABLE user_events (
    user_id  UUID,
    event_ts TIMESTAMP,
    data     TEXT,
    PRIMARY KEY ((user_id), event_ts)
);

-- ХОРОШО: bucketing по месяцу
CREATE TABLE user_events (
    user_id  UUID,
    month    TEXT,      -- '2026-04'
    event_ts TIMESTAMP,
    data     TEXT,
    PRIMARY KEY ((user_id, month), event_ts)
) WITH CLUSTERING ORDER BY (event_ts DESC);
```

**3. Использование Cassandra как реляционной БД**
- Нормализация данных → JOIN не существует → неэффективные multiple roundtrips
- Решение: денормализация, дублирование данных, моделирование под запросы

**4. Большое количество `Tombstone`**
- Частые DELETE + низкий `gc_grace_seconds` → накопление tombstone → degraded reads
- Решение: TTL вместо DELETE там, где применимо; регулярный repair; мониторинг tombstone

**5. Hot Spots из-за неудачного Partition Key**

```cql
-- ПЛОХО: status='ACTIVE' — 95% данных, один узел перегружен
PRIMARY KEY ((status), user_id)

-- ХОРОШО: добавить bucket для распределения
PRIMARY KEY ((status, shard), user_id)
-- shard = user_id % 10 (вычисляется на стороне приложения)
```

**6. Синхронный `LWT` везде**
- `IF NOT EXISTS` / `IF` условия — Paxos-консенсус → в 5-10 раз медленнее обычной записи
- Использовать только там, где действительно нужна compare-and-set семантика

**7. Unbounded IN clause**

```cql
-- ПЛОХО: IN с сотнями значений — запросы ко многим партициям
SELECT * FROM products WHERE category_id IN (1, 2, 3, ..., 500);

-- ХОРОШО: параллельные отдельные запросы через executeAsync
```

## Q40. (!) Как правильно обрабатывать коллекции в `CQL`?

`Cassandra` поддерживает три типа коллекций как тип колонки: `LIST`, `SET`, `MAP`. Также есть `FROZEN`-обёртка и пользовательские типы (`UDT`).

**Основные типы коллекций:**

```cql
CREATE TABLE user_profile (
    user_id  UUID PRIMARY KEY,
    emails   SET<TEXT>,          -- уникальные значения, неупорядоченные
    tags     LIST<TEXT>,         -- упорядоченные, дубли допустимы
    metadata MAP<TEXT, TEXT>,    -- ключ-значение
    address  FROZEN<address_type> -- UDT как атомарный тип
);

-- Пользовательский тип
CREATE TYPE address_type (
    street TEXT,
    city   TEXT,
    zip    TEXT
);
```

**Операции с коллекциями:**

```cql
-- SET: добавление/удаление элементов
UPDATE user_profile SET emails = emails + {'new@example.com'} WHERE user_id = ?;
UPDATE user_profile SET emails = emails - {'old@example.com'} WHERE user_id = ?;

-- LIST: append/prepend/удаление по индексу
UPDATE user_profile SET tags = tags + ['kotlin'] WHERE user_id = ?;
UPDATE user_profile SET tags[0] = 'java' WHERE user_id = ?;  -- по индексу

-- MAP: добавление/обновление/удаление ключей
UPDATE user_profile SET metadata['key'] = 'value' WHERE user_id = ?;
DELETE metadata['old_key'] FROM user_profile WHERE user_id = ?;
```

**Ограничения и рекомендации:**
- Коллекции **читаются целиком** — нельзя прочитать один элемент без всей коллекции
- Максимальный размер коллекции: **65 535** элементов (рекомендуется < 100)
- `FROZEN` — коллекция сериализуется как blob, изменения заменяют весь объект целиком
- Если нужны частые обновления отдельных элементов большой коллекции — лучше отдельная таблица
- `SET` предпочтительнее `LIST` (нет проблем с дублированием при partial failures)

**Когда использовать FROZEN:**

```cql
-- FROZEN нужен для вложенных коллекций и UDT как part of PRIMARY KEY
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    items    LIST<FROZEN<order_item>>,  -- список UDT
    tags     FROZEN<SET<TEXT>>          -- frozen set как часть PK или для компакности
);
```

## Q41. Как реализовать `pagination` в `Cassandra`?

Из-за отсутствия `OFFSET` стандартный SQL-пейджинг неприменим. В `Cassandra` используется **cursor-based pagination** через `paging state`.

**Автоматический пейджинг через Java Driver:**

```java
// Driver автоматически разбивает результаты на страницы
SimpleStatement stmt = SimpleStatement.newInstance(
    "SELECT * FROM events WHERE user_id = ?", userId)
    .setPageSize(100);  // размер страницы

ResultSet rs = session.execute(stmt);

// Итерация — driver запрашивает следующую страницу автоматически
for (Row row : rs) {
    processRow(row);
}
```

**Ручной пейджинг (для REST API):**

```java
// Первая страница
SimpleStatement stmt = SimpleStatement.newInstance(
    "SELECT * FROM events WHERE user_id = ?", userId)
    .setPageSize(20);

ResultSet rs = session.execute(stmt);
List<Row> page1 = rs.currentPage();

// Сохраняем paging state для следующей страницы
ByteBuffer pagingState = rs.getExecutionInfo().getPagingState();
String token = Base64.getEncoder().encodeToString(pagingState.array());

// API отдаёт: {"data": [...], "nextPageToken": "abc123=="}

// Следующая страница по токену
ByteBuffer savedState = ByteBuffer.wrap(Base64.getDecoder().decode(token));
SimpleStatement stmt2 = stmt.copy().setPagingState(savedState);
ResultSet rs2 = session.execute(stmt2);
```

**Пейджинг по clustering key (Token-based):**

```cql
-- Для time-series: используем clustering key как cursor
SELECT * FROM events WHERE user_id = ? AND event_ts < ? ORDER BY event_ts DESC LIMIT 20;
-- event_ts = значение последнего элемента предыдущей страницы
```

**Ограничения paging state:**
- `PagingState` привязан к конкретному запросу и кластеру
- Нельзя перейти сразу на страницу N (только последовательно)
- Состояние может устареть при изменении схемы или данных

## Q42. (!) Что такое `Token-aware routing` и почему он важен?

**Token-aware routing** — политика выбора координатора, при которой Java Driver отправляет запрос **напрямую на узел**, хранящий нужную партицию, минуя лишние сетевые хопы.

```mermaid
graph LR
    Client -->|"Without token-aware: любой узел"| N1[Node 1 координатор]
    N1 -->|"внутренний hops"| N3[Node 3 данные]

    Client2[Client] -->|"With token-aware: напрямую"| N3
```

**Настройка в Java Driver v4:**

```java
CqlSession session = CqlSession.builder()
    .addContactPoint(new InetSocketAddress("cassandra-host", 9042))
    .withLocalDatacenter("dc1")
    // TokenAwarePolicy включён по умолчанию в Driver v4
    // Дополнительно: DC-aware для мульти-датацентровых кластеров
    .withConfigLoader(DriverConfigLoader.fromClasspath("application.conf"))
    .build();
```

**application.conf (Typesafe Config):**

```hocon
datastax-java-driver {
  basic.load-balancing-policy {
    class = DefaultLoadBalancingPolicy
    local-datacenter = dc1
  }
  advanced.load-balancing-policy {
    # Token-awareness включена по умолчанию
    # Fallback при недоступности: другой реплика в том же DC
  }
}
```

**Почему важен:**
- Снижает задержку на 1 сетевой хоп (с ~5 мс до ~1 мс в одном датацентре)
- Уменьшает нагрузку на координирующие узлы
- При `CL=ONE` запрос обрабатывается без координации — максимальная скорость
- Требует корректного расчёта `routing key` (partition key в prepared statement)

**Пример с Prepared Statement (обязателен для token-awareness):**

```java
// Prepared statement — driver знает binding variables и вычисляет token
PreparedStatement prepared = session.prepare(
    "SELECT * FROM orders WHERE user_id = ? AND order_date > ?");

BoundStatement bound = prepared.bind(userId, fromDate);
// Driver автоматически вычисляет token(userId) и маршрутизирует к нужному узлу
ResultSet rs = session.execute(bound);
```

## Q43. Как выполнять агрегации и аналитику поверх `Cassandra`?

`Cassandra` намеренно ограничивает аналитические возможности (нет JOIN, GROUP BY, HAVING). Для аналитики используются внешние инструменты.

**Встроенные агрегаты CQL (ограниченно):**

```cql
-- Только по одной партиции, не по кластеру
SELECT COUNT(*), SUM(amount), AVG(amount), MIN(amount), MAX(amount)
FROM orders
WHERE user_id = 'd7c5db80-cf5e-4df1-8523-ebe9a8aa0b81';

-- User-defined aggregate (UDA) для кастомной логики
CREATE FUNCTION state_add(state DOUBLE, val DOUBLE)
    CALLED ON NULL INPUT RETURNS DOUBLE
    LANGUAGE java AS 'return state + val;';
```

**Apache Spark + Cassandra (основной подход):**

```scala
// Spark Cassandra Connector
val sc = new SparkContext(conf)
val ordersRDD = sc.cassandraTable("shop", "orders")

// Аналитика поверх всего кластера
val revenueByCategory = ordersRDD
  .filter(_.getDouble("amount") > 0)
  .keyBy(_.getString("category"))
  .aggregateByKey(0.0)(
    (acc, row) => acc + row.getDouble("amount"),
    _ + _
  )
  .collect()
```

**Apache Flink для stream аналитики:**

```java
// Flink Cassandra Sink — запись результатов обратно в Cassandra
CassandraSink.addSink(stream)
    .setQuery("INSERT INTO analytics.category_stats (category, total) VALUES (?, ?)")
    .setClusterBuilder(clusterBuilder)
    .build();
```

**Materialized Views vs Отдельные таблицы:**

```cql
-- Materialized View (осторожно — experimental feature, лучше избегать в prod)
CREATE MATERIALIZED VIEW orders_by_status AS
    SELECT * FROM orders WHERE status IS NOT NULL AND order_id IS NOT NULL
    PRIMARY KEY ((status), created_at, order_id);

-- Лучше: явная денормализация через приложение или Kafka streams
```

**Рекомендации:**
- `Spark` + `Cassandra Connector` — для batch-аналитики, ETL
- `Apache Flink` / `Kafka Streams` — для real-time агрегаций
- `Presto` / `Trino` — для SQL-запросов ad-hoc поверх Cassandra
- Избегать `ALLOW FILTERING` и cross-partition запросов в prod

## Q44. Что такое `CDC` (`Change Data Capture`) в `Cassandra`?

**CDC** (`Change Data Capture`) — механизм захвата изменений данных в реальном времени. Позволяет отслеживать INSERT/UPDATE/DELETE и публиковать их в другие системы (Kafka, Elasticsearch).

**Как работает CDC в Cassandra:**

```mermaid
graph LR
    App -->|"write"| Cassandra
    Cassandra -->|"CommitLog"| CDC_Raw[cdc_raw директория]
    CDC_Raw -->|"Debezium Connector"| Kafka
    Kafka -->|"consume"| ES[Elasticsearch]
    Kafka -->|"consume"| DW[Data Warehouse]
```

**Включение CDC для таблицы:**

```cql
-- Включить CDC при создании таблицы
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    status   TEXT,
    amount   DECIMAL
) WITH cdc = true;

-- Включить для существующей таблицы
ALTER TABLE orders WITH cdc = true;
```

**cassandra.yaml настройки:**

```yaml
cdc_enabled: true
cdc_raw_directory: /var/lib/cassandra/cdc_raw
cdc_total_space_in_mb: 4096    # максимум места под CDC logs
cdc_free_space_check_interval_ms: 250
```

**Debezium Connector для Cassandra (популярное решение):**

```json
{
  "name": "cassandra-cdc-connector",
  "config": {
    "connector.class": "io.debezium.connector.cassandra.CassandraConnector",
    "cassandra.config": "/etc/cassandra/cassandra.yaml",
    "cassandra.hosts": "cassandra-host",
    "kafka.producer.bootstrap.servers": "kafka:9092",
    "topic.prefix": "cassandra",
    "snapshot.mode": "initial"
  }
}
```

**Сценарии использования CDC:**
- Синхронизация Cassandra → Elasticsearch для поиска
- Построение event-driven архитектуры (Cassandra как event store)
- Репликация данных между кластерами
- Аудит изменений
- Cache invalidation при изменении данных

**Ограничения:**
- CDC только для мутаций — не захватывает TTL-удаления и compaction
- Нагрузка на диск (`cdc_raw` нужно мониторить)
- Нет встроенного гарантированного порядка между партициями

---

## See also

- [MongoDB](mongodb-interview.md) — другая NoSQL БД, документная модель
- [Redis](redis-interview.md) — in-memory хранилище, часто используется вместе с Cassandra как кэш
- [Распределённые системы](../architecture/distributed-systems-interview.md) — фундаментальные концепции, на которых построена Cassandra
- [CAP-теорема](../architecture/cap-theorem-interview.md) — Cassandra как AP-система
- [Архитектура баз данных](database-architecture-interview.md) — сравнение подходов к хранению данных
- [Apache Kafka](../messaging/kafka-interview.md) — интеграция через Kafka Connect для CDC и потоковой обработки
- [Elasticsearch](elasticsearch-interview.md) — часто используется вместе с Cassandra для полнотекстового поиска
