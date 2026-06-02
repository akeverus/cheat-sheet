---
title: "Вопросы на собеседовании: ClickHouse"
description: "ClickHouse: columnar OLAP DB от Yandex, MergeTree engines, partitioning, sparse index, materialized views, distributed tables, ReplicatedMergeTree, vs Snowflake/BigQuery"
tags:
  - interview
  - databases
  - clickhouse-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "ClickHouse"
  - "ClickHouse interview"
  - "ClickHouse собеседование"
prerequisites:
  - "[[clickhouse]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `ClickHouse`

`ClickHouse` — open-source колоночная OLAP-СУБД от **Yandex** (открыта в 2016). Известна **очень высокой скоростью** на аналитических запросах (миллиарды строк за секунды). Применяется в observability (логи, метрики, трейсы), real-time-аналитике, ad-tech. Компания **ClickHouse Inc.** (с 2021) предоставляет managed-облако.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [ClickHouse Documentation](https://clickhouse.com/docs)
- [ClickHouse GitHub](https://github.com/ClickHouse/ClickHouse)
- [ClickHouse Cloud](https://clickhouse.cloud/)
- [Awesome ClickHouse](https://github.com/ClickHouse/awesome-clickhouse)
- [ClickHouse Best Practices](https://clickhouse.com/docs/en/operations/tips/)
- [Altinity Knowledge Base](https://altinity.com/blog/) — эксперты по open-source ClickHouse

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое ClickHouse?](#q1--что-такое-clickhouse)
- [Q2. (!) Почему ClickHouse такой быстрый?](#q2--почему-clickhouse-такой-быстрый)
- [Q3. (!) ClickHouse vs Snowflake/BigQuery?](#q3--clickhouse-vs-snowflakebigquery)
- [Q4. ClickHouse vs PostgreSQL для аналитики?](#q4-clickhouse-vs-postgresql-для-аналитики)

**MergeTree engines**
- [Q5. (!) Что такое MergeTree?](#q5--что-такое-mergetree)
- [Q6. (!) Семейство MergeTree (ReplacingMergeTree, SummingMergeTree, AggregatingMergeTree)?](#q6--семейство-mergetree-replacingmergetree-summingmergetree-aggregatingmergetree)
- [Q7. CollapsingMergeTree?](#q7-collapsingmergetree)
- [Q8. ReplicatedMergeTree?](#q8-replicatedmergetree)

**Partitioning и indexing**
- [Q9. (!) Partitioning в ClickHouse?](#q9--partitioning-в-clickhouse)
- [Q10. (!) Sparse index — что это?](#q10--sparse-index--что-это)
- [Q11. ORDER BY vs PRIMARY KEY?](#q11-order-by-vs-primary-key)
- [Q12. Skip indexes (data skipping)?](#q12-skip-indexes-data-skipping)

**Запросы**
- [Q13. (!) ClickHouse SQL — особенности?](#q13--clickhouse-sql--особенности)
- [Q14. Array functions, higher-order functions?](#q14-array-functions-higher-order-functions)
- [Q15. (!) JOINs в ClickHouse — особенности?](#q15--joins-в-clickhouse--особенности)
- [Q16. Window functions?](#q16-window-functions)

**Materialized views и projections**
- [Q17. (!) Materialized views в ClickHouse?](#q17--materialized-views-в-clickhouse)
- [Q18. Projections?](#q18-projections)

**Distributed**
- [Q19. (!) Distributed table?](#q19--distributed-table)
- [Q20. Replication через ZooKeeper / Keeper?](#q20-replication-через-zookeeper--keeper)
- [Q21. ClickHouse Keeper vs ZooKeeper?](#q21-clickhouse-keeper-vs-zookeeper)

**Storage**
- [Q22. (!) Storage engine оптимизации (compression, sparse data)?](#q22--storage-engine-оптимизации-compression-sparse-data)
- [Q23. Tiered storage (hot/cold)?](#q23-tiered-storage-hotcold)

**Integration**
- [Q24. (!) Kafka engine для ingestion?](#q24--kafka-engine-для-ingestion)
- [Q25. Other table engines (S3, MySQL, PostgreSQL)?](#q25-other-table-engines-s3-mysql-postgresql)

**Production**
- [Q26. (!) Какие use cases ClickHouse в production?](#q26--какие-use-cases-clickhouse-в-production)
- [Q27. (!) Какие ограничения / минусы ClickHouse?](#q27--какие-ограничения--минусы-clickhouse)
- [Q28. Common pitfalls в ClickHouse?](#q28-common-pitfalls-в-clickhouse)

## Q1. (!) Что такое ClickHouse?

**ClickHouse** — open-source колоночная OLAP-СУБД, созданная в **Yandex** для **Yandex.Metrica** (аналог Google Analytics). Открыта в **2016**.

**Особенности:**
- **Колоночное хранение** + векторизованное выполнение
- **Очень быстрая** на аналитических запросах (скан миллиардов строк/сек)
- **Высокое сжатие** (типично 5-10x)
- **SQL** (в основном стандартный + расширения)
- **Распределённость** (шардирование, репликация)
- **Real-time-приём данных** (миллионы вставок/сек)

**Применения:**
- Агрегация логов (вместо ELK)
- Временные ряды (вместо Prometheus, InfluxDB)
- Веб-аналитика
- Ad-tech-аналитика
- Бэкенд observability (Tempo, SigNoz)

## Q2. (!) Почему ClickHouse такой быстрый?

**Ключевые оптимизации:**

1. **Колоночное хранение** — читаются только колонки, нужные в запросе
2. **Векторизованное выполнение** — обработка батчами (не построчно), SIMD-инструкции
3. **Сжатие** — лучше используется кэш
4. **Разреженный первичный индекс** — эффективные range-сканы
5. **Data skipping** — min/max-индексы, bloom-фильтры
6. **Асинхронные параллельные вставки** — несколько вставок одновременно
7. **Распределённый запрос** — параллельный скан по шардам
8. **Оптимизированный I/O** — прямое чтение с диска, асинхронный I/O
9. **Нативный протокол** — бинарный, сжатый
10. **JIT-компиляция** запросов (в более новых версиях)

**Бенчмарки:** часто в **10-100x** быстрее PostgreSQL на аналитических запросах.

## Q3. (!) ClickHouse vs Snowflake/BigQuery?

| Критерий | ClickHouse | Snowflake | BigQuery |
|-----------|-----------|-----------|----------|
| Тип | Self-hosted / облако | Только managed | Serverless |
| Архитектура | Shared-nothing (шардирование) | Общее хранилище + виртуальные warehouse | Serverless |
| Стоимость | Самый дешёвый | $$$ | $$ (за запрос) |
| Скорость | Очень высокая | Высокая | Высокая |
| Параллельность | Сотни | Тысячи | Тысячи |
| Настройка | Ручная конфигурация | Click-ops | Zero-config |
| SQL | Диалект ClickHouse | ANSI SQL | ANSI SQL |
| Экосистема | Меньше | Зрелая | Привязана к GCP |

**Когда ClickHouse:**
- Чувствительность к стоимости
- Уже есть собственный хостинг
- Real-time-приём данных (миллионы/сек)
- Sub-second-задержка запросов на огромных данных

**Когда Snowflake/BigQuery:**
- Нужен полностью managed-сервис
- Multi-tenant-хранилище данных
- Сложные схемы
- Не хочется заниматься эксплуатацией

## Q4. ClickHouse vs PostgreSQL для аналитики?

| Критерий | ClickHouse | PostgreSQL |
|----------|-----------|------------|
| Хранение | Колоночное | Построчное |
| Скорость аналитики | **Очень высокая** | Медленно на больших сканах |
| OLTP (точечные выборки) | Медленно | Быстро |
| Обновления / удаления | Ограниченно | Полный ACID |
| JOIN-ы | Ограниченно (broadcast только небольших таблиц) | Полноценные |
| Гибкость схемы | Ограниченная | Полная |

## Q5. (!) Что такое MergeTree?

**MergeTree** — основной storage-движок ClickHouse. Лежит в основе всех таблиц ClickHouse.

**Принцип:**
- Данные разбиты на **части (parts)** — неизменяемые чанки
- Фоновый **merge** объединяет части в более крупные (как в LSM-дереве)
- Внутри части данные отсортированы по **ключу ORDER BY**
- Разреженный первичный индекс хранится в памяти

```sql
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64,
    event_type String,
    data String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (event_time, user_id);
```

## Q6. (!) Семейство MergeTree (ReplacingMergeTree, SummingMergeTree, AggregatingMergeTree)?

**ReplacingMergeTree** — дедуплицирует строки с одинаковым ключом ORDER BY (в итоге побеждает последняя вставка, eventually).

```sql
CREATE TABLE users (
    id UInt64,
    email String,
    updated_at DateTime
) ENGINE = ReplacingMergeTree(updated_at)
ORDER BY id;
```

**SummingMergeTree** — суммирует числовые колонки при merge для одинаковых значений ORDER BY.

```sql
CREATE TABLE daily_stats (
    date Date,
    user_id UInt64,
    visits UInt64,
    clicks UInt64
) ENGINE = SummingMergeTree()
ORDER BY (date, user_id);
-- При merge: visits и clicks суммируются
```

**AggregatingMergeTree** — для произвольных агрегаций через `AggregateFunction`.

**MaterializedView** часто использует AggregatingMergeTree для real-time-агрегаций.

## Q7. CollapsingMergeTree?

**CollapsingMergeTree** — для взаимного «погашения» строк (знак sign +1 / -1).

```sql
CREATE TABLE events (
    user_id UInt64,
    sign Int8,
    activity_count UInt64
) ENGINE = CollapsingMergeTree(sign)
ORDER BY user_id;

-- Insert
INSERT INTO events VALUES (1, 1, 100);  -- "old state"

-- Update: cancel old + add new
INSERT INTO events VALUES (1, -1, 100);  -- cancel
INSERT INTO events VALUES (1, 1, 150);   -- new value
```

При merge — противоположные знаки взаимно гасятся → остаётся итоговое состояние.

**Сценарий:** обновления в модели, которая в остальном неизменяема.

## Q8. ReplicatedMergeTree?

**ReplicatedMergeTree** — добавляет репликацию к MergeTree.

```sql
CREATE TABLE events (...) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/{shard}/events',  -- ZK/Keeper path
    '{replica}'                            -- replica identifier
)
ORDER BY (event_time);
```

**Репликация:**
- **Multi-master** (любая реплика может принимать записи)
- **Eventually consistent** (асинхронно)
- **Координация через ZooKeeper / ClickHouse Keeper**
- Автовосстановление после сбоев

**Рекомендация:** в production — всегда **ReplicatedMergeTree** (для HA).

## Q9. (!) Partitioning в ClickHouse?

```sql
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)  -- partition by month
ORDER BY event_time;
```

**Партиция** = подмножество данных, хранящееся отдельно.

**Эффект:**
- **Partition pruning** — запрос «WHERE event_time = '2025-04-19'» читает только 1 партицию
- **Независимые операции** — drop / detach / optimize по отдельной партиции
- **Лучшая параллельность запросов**

**Рекомендация:**
- Не дробите слишком сильно (≤ ~1000 партиций на таблицу)
- Типично: по месяцу / неделе (для временных рядов)
- Не партиционируйте по колонке с высокой кардинальностью

## Q10. (!) Sparse index — что это?

**ClickHouse** не индексирует каждую строку. Вместо этого — **разреженный индекс (sparse index)**: одна запись на **8192 строки** (по умолчанию).

```
Primary index (sparse):
[key=10, granule=0]
[key=100, granule=1]
[key=200, granule=2]
...
```

**Гранула (granule)** = блок из 8192 строк.

**Запрос:** WHERE key = 150 → бинарный поиск → гранула 1 → скан 8192 строк → фильтрация.

**Компромиссы:**
- **Крошечный индекс** (помещается в память)
- **Range-сканы эффективны**
- **Точечная выборка** медленнее (читаем целую гранулу)

ClickHouse предназначен **не для точечных выборок**, а для **сканов**.

## Q11. ORDER BY vs PRIMARY KEY?

```sql
ORDER BY (user_id, event_time)  -- physical sorting в parts
PRIMARY KEY user_id              -- prefix of ORDER BY (для index)
```

Если PRIMARY KEY не указан — он **равен ORDER BY**.

**ORDER BY:** определяет физическую раскладку данных.
**PRIMARY KEY:** префикс ORDER BY для первичного индекса.

**Рекомендация:** обычно `PRIMARY KEY = ORDER BY` (по умолчанию).

**Настройка ORDER BY** = главный рычаг производительности:
- Упорядочивайте колонки по кардинальности: от низкой → к высокой
- Самые частые колонки-фильтры — первыми

## Q12. Skip indexes (data skipping)?

**Skip-индексы** — вторичные индексы для **пропуска гранул** (без их чтения).

**Типы:**
- `minmax` — min/max-значение по грануле
- `set` — набор уникальных значений
- `bloom_filter` — bloom-фильтр для проверки существования
- `tokenbf_v1` — токены для поиска в духе full-text
- `ngrambf_v1` — n-граммы для подстрок

```sql
CREATE TABLE logs (
    timestamp DateTime,
    message String,
    INDEX message_idx message TYPE tokenbf_v1(8192, 3, 0) GRANULARITY 4
) ENGINE = MergeTree()
ORDER BY timestamp;
```

**Эффект:** запросы `WHERE message LIKE '%error%'` пропускают нерелевантные гранулы.

## Q13. (!) ClickHouse SQL — особенности?

SQL в ClickHouse **в основном стандартный**, но с расширениями:

```sql
-- Standard SQL
SELECT user_id, count() FROM events WHERE date = '2025-04-19' GROUP BY user_id;
```

```sql
-- Higher-order functions
SELECT arraySum(prices), arrayAvg(prices) FROM orders;

-- Approximate functions (faster, slightly inaccurate)
SELECT uniqHLL12(user_id) FROM events;  -- ~uniqExact

-- Specific functions
SELECT toStartOfHour(event_time), count() FROM events GROUP BY 1;
```

**Приближённые агрегаты** — `uniqHLL12`, `quantileTDigest` — **намного быстрее** точных, ценой небольшой неточности.

## Q14. Array functions, higher-order functions?

```sql
-- Arrays — first-class type
CREATE TABLE products (
    id UInt64,
    tags Array(String),
    prices Array(Float64)
) ENGINE = MergeTree() ORDER BY id;

-- Filter array
SELECT id, arrayFilter(x -> x > 10, prices) FROM products;

-- Map
SELECT id, arrayMap(x -> x * 1.2, prices) FROM products;

-- Sum
SELECT id, arraySum(prices) FROM products;

-- Has element
SELECT id FROM products WHERE has(tags, 'discount');

-- ARRAY JOIN — explode array
SELECT id, tag FROM products ARRAY JOIN tags AS tag;
```

**Мощный инструмент** для денормализованных данных — держите массивы вместо нормализации.

## Q15. (!) JOINs в ClickHouse — особенности?

**JOIN-ы в ClickHouse устроены иначе, чем в PostgreSQL:**

- **По умолчанию:** правая таблица рассылается (broadcast) на все узлы (правая таблица должна быть небольшой)
- **Ограничение по памяти** — может упасть с OOM на больших JOIN-ах
- **Нет сброса хэша на диск** (в старых версиях)
- Медленнее, чем сканы по колонкам

**Рекомендации:**
- **Избегайте JOIN-ов**, если возможно (используйте денормализацию)
- **Небольшая правая таблица** для JOIN-ов (broadcast)
- **Используйте `IN` вместо JOIN** для фильтрации:
```sql
-- Slower
SELECT * FROM events JOIN users ON events.user_id = users.id WHERE users.country = 'US';

-- Faster
SELECT * FROM events WHERE user_id IN (SELECT id FROM users WHERE country = 'US');
```

- **Стратегии распределённых JOIN:** GLOBAL, ALLOW_EXPERIMENTAL_PARALLEL_REPLICAS

К **2025** JOIN-ы улучшились (parallel hash join, grace hash), но в ClickHouse всё равно выигрывает **денормализация**.

## Q16. Window functions?

```sql
SELECT
    user_id,
    event_time,
    sum(amount) OVER (PARTITION BY user_id ORDER BY event_time) AS running_total,
    rank() OVER (PARTITION BY user_id ORDER BY amount DESC) AS rnk
FROM transactions;
```

ClickHouse поддерживает оконные функции с **2021** (есть некоторые ограничения по сравнению с PostgreSQL).

## Q17. (!) Materialized views в ClickHouse?

**MV в ClickHouse устроены иначе, чем в PostgreSQL:**

- **Обновляются инкрементально** при INSERT в исходную таблицу
- **Предагрегации** в реальном времени
- **Без refresh** — всегда актуальны
- **Срабатывают только на INSERT**

```sql
-- Source table
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64,
    amount Float64
) ENGINE = MergeTree() ORDER BY event_time;

-- MV для hourly aggregations
CREATE MATERIALIZED VIEW hourly_stats
ENGINE = SummingMergeTree()
ORDER BY (hour, user_id)
AS SELECT
    toStartOfHour(event_time) AS hour,
    user_id,
    count() AS event_count,
    sum(amount) AS total_amount
FROM events
GROUP BY hour, user_id;
```

При **каждом INSERT** в `events` → автоматически обновляется `hourly_stats`.

**Сценарий:** real-time-дашборды без тяжёлых запросов к сырым данным.

## Q18. Projections?

**Projections** (с 2020) — альтернатива MV, работают автоматически.

```sql
ALTER TABLE events ADD PROJECTION events_by_user (
    SELECT *
    ORDER BY user_id
);
```

ClickHouse **автоматически** выбирает проекцию, если запрос от неё выигрывает.

**В сравнении с MV:**
- Projections — несколько порядков сортировки для одной и той же таблицы
- MV — предвычисленные агрегаты / отдельная таблица

## Q19. (!) Distributed table?

**Distributed-таблица** — таблица-прокси, выполняющая запросы по всем **шардам**.

```sql
-- Local table on each node
CREATE TABLE events_local (...) ENGINE = ReplicatedMergeTree(...);

-- Distributed table (no data of its own, queries across shards)
CREATE TABLE events_distributed AS events_local
ENGINE = Distributed(my_cluster, default, events_local, rand());
```

**Вставка в distributed** → маршрутизируется по шардам по ключу шардирования.
**Запрос к distributed** → распараллеливается по шардам, результаты объединяются.

**Определение кластера** в конфиге:
```xml
<remote_servers>
  <my_cluster>
    <shard><replica><host>node1</host></replica></shard>
    <shard><replica><host>node2</host></replica></shard>
  </my_cluster>
</remote_servers>
```

## Q20. Replication через ZooKeeper / Keeper?

**ReplicatedMergeTree** требует **сервис координации**:
- **Apache ZooKeeper** (старый вариант по умолчанию)
- **ClickHouse Keeper** (более новый, нативный)

Координация нужна для:
- Регистрации реплик
- Упорядочивания вставок (очередь)
- Синхронизации merge
- Выбора лидера (leader election)
- Распространения DDL-запросов

## Q21. ClickHouse Keeper vs ZooKeeper?

**ClickHouse Keeper** (с 2021) — drop-in-замена ZooKeeper, написан на C++.

**Преимущества:**
- **Быстрее** (в 10x на некоторых операциях)
- **Меньше памяти**
- Может встраиваться в процесс ClickHouse
- Тот же консенсус на основе Raft
- Совместим с протоколом ZooKeeper

К **2025** — Keeper рекомендуется для новых развёртываний.

## Q22. (!) Storage engine оптимизации (compression, sparse data)?

**Кодеки сжатия:**
- LZ4 (по умолчанию, быстрый)
- ZSTD (лучшее соотношение сжатия)
- Специализированные: Delta, DoubleDelta (для временных рядов), Gorilla (для float)

```sql
CREATE TABLE metrics (
    timestamp DateTime CODEC(DoubleDelta, ZSTD),
    value Float64 CODEC(Gorilla, ZSTD)
) ENGINE = MergeTree() ORDER BY timestamp;
```

**Эффект:** типично **сжатие 5-10x**, иногда до 50x.

**LowCardinality(String)** — для колонок с низкой кардинальностью (status, country):
```sql
status LowCardinality(String)  -- dictionary encoding
```

## Q23. Tiered storage (hot/cold)?

```sql
-- Storage policy в config
<storage_configuration>
  <policies>
    <hot_cold>
      <volumes>
        <hot><disk>fast_ssd</disk></hot>
        <cold><disk>s3</disk></cold>
      </volumes>
      <move_factor>0.2</move_factor>
    </hot_cold>
  </policies>
</storage_configuration>

-- Apply to table
CREATE TABLE events (...) ENGINE = MergeTree()
SETTINGS storage_policy = 'hot_cold';
```

**TTL** для auto-move к cold storage:
```sql
ALTER TABLE events MODIFY TTL event_time + INTERVAL 30 DAY TO VOLUME 'cold';
```

**Экономия** — старые данные хранятся на дешёвом S3.

## Q24. (!) Kafka engine для ingestion?

**Kafka Engine** — таблица читает данные из топика Kafka.

```sql
CREATE TABLE events_kafka (
    event_time DateTime,
    user_id UInt64,
    data String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka:9092',
    kafka_topic_list = 'events',
    kafka_group_name = 'clickhouse-consumer',
    kafka_format = 'JSONEachRow';

-- MV для materialization Kafka → MergeTree table
CREATE MATERIALIZED VIEW events_consumer
TO events  -- target table (MergeTree)
AS SELECT * FROM events_kafka;
```

**Непрерывный приём данных** Kafka → ClickHouse, без внешнего pipeline.

**Аналогично:** RabbitMQ Engine, NATS Engine, S3 Queue Engine.

## Q25. Other table engines (S3, MySQL, PostgreSQL)?

**Табличные функции / внешние движки:**
- **S3** — чтение/запись Parquet/CSV/JSON в S3
```sql
SELECT * FROM s3('s3://bucket/data.parquet', 'Parquet')
```
- **PostgreSQL** — федеративные запросы
```sql
CREATE TABLE pg_users ENGINE = PostgreSQL('host:5432', 'db', 'users', 'user', 'pass');
```
- **MySQL** — аналогично
- Движки **HDFS, URL, File**

**Сценарий:** ETL без внешнего pipeline — `INSERT INTO local SELECT FROM s3(...)`.

## Q26. (!) Какие use cases ClickHouse в production?

**Кто использует:**
- **Yandex** (создатели) — Metrica
- **Cloudflare** — аналитика
- **Uber** — observability (логи, метрики, трейсы)
- **Spotify** — аналитика
- **Mercedes-Benz, eBay, GitLab, Lyft**

**Типичные сценарии:**
- **Observability** — логи (дешевле ELK), метрики (замена Prometheus), трейсы (бэкенд Jaeger)
- **Real-time-аналитика** — дашборды
- **Ad-tech** — анализ ставок, атрибуция
- **Веб-аналитика** — clickstream
- **Временные ряды** — IoT, мониторинг
- **Хранилище данных** для real-time-аналитики

## Q27. (!) Какие ограничения / минусы ClickHouse?

1. **Нет транзакций** (только точечные вставки)
2. **Ограниченные UPDATE/DELETE** (мутации медленные, асинхронные)
3. **JOIN-ы** слабее, чем в PostgreSQL
4. **Schema-on-write** (жёсткие схемы)
5. **Нет внешних ключей и ограничений (constraints)**
6. **Разреженная индексация** — плохо для точечных запросов
7. **Крутая кривая обучения** при тюнинге
8. **Сложная эксплуатация кластера** (настройка шардирования, репликации)
9. **Прожорлив к памяти** (запросы могут падать с OOM)
10. **Байты на строку** — широкие таблицы могут быть неэффективны

## Q28. Common pitfalls в ClickHouse production?

1. **Неудачный ORDER BY** — медленные запросы, большие индексы
2. **Ключ партиционирования с высокой кардинальностью** — слишком много частей, merge не справляется
3. **OOM на JOIN-ах** — broadcast больших таблиц
4. **Асинхронные вставки** — возможна небольшая потеря данных (настраивается)
5. **Мутации** — медленные, блокируют части
6. **Нет TTL для очистки** — диск переполняется
7. **Слишком много мелких вставок** — перегрузка ZK/Keeper
8. **Нет репликации** — потеря данных при отказе диска
9. **Нет бэкапов** (`ALTER TABLE FREEZE` → резервное копирование частей)
10. **Неправильный выбор движка** (`MergeTree` vs `ReplacingMergeTree` и т. д.)

**Рекомендация:** внимательно прочитайте [ClickHouse docs Tips and Tricks](https://clickhouse.com/docs/en/operations/tips/).

---

## See also

- [PostgreSQL](postgresql-interview.md) — OLTP comparison
- [Data Warehousing](../data-engineering/data-warehousing-interview.md) — context
- [Apache Spark](../data-engineering/apache-spark-interview.md) — alternative для batch analytics
- [Apache Flink](../data-engineering/apache-flink-interview.md) — для stream processing → ClickHouse
- [Apache Kafka](../messaging/kafka-interview.md) — Kafka engine ingestion
- [Stream Processing](../data-engineering/stream-processing-interview.md) — context
- [Loki + Grafana](../monitoring/loki-grafana-interview.md) — alternative для logs
- [ELK Stack](../monitoring/elk-stack-interview.md) — alternative для logs
- [OpenTelemetry](../monitoring/opentelemetry-interview.md) — observability data → ClickHouse
- [Микросервисы](../architecture/microservices-interview.md) — analytics backend
- [Caching](../architecture/caching-strategies-interview.md) — для acceleration
- [Database Architecture](database-architecture-interview.md) — OLAP context
- [SQL](sql-interview.md) — общие основы

- [Apache Cassandra](cassandra-interview.md)
- [CockroachDB](cockroachdb-interview.md)
- [Database Architecture](database-architecture-interview.md)
- [Транзакции и уровни изоляции](database-transactions-interview.md)
- [DynamoDB](dynamodb-interview.md)
- [Elasticsearch](elasticsearch-interview.md)
- [Шпаргалка: ClickHouse](../../databases/nosql/clickhouse/clickhouse.md) — теория
