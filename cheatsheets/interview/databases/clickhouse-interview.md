---
title: "Вопросы на собеседовании: ClickHouse"
description: "ClickHouse: columnar OLAP DB от Yandex, MergeTree engines, partitioning, sparse index, materialized views, distributed tables, ReplicatedMergeTree, vs Snowflake/BigQuery"
tags:
  - interview
  - databases
  - clickhouse-interview
aliases:
  - "ClickHouse interview"
  - "ClickHouse собеседование"
  - "ClickHouse OLAP interview"
  - "MergeTree interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `ClickHouse`

`ClickHouse` — open-source columnar OLAP database от **Yandex** (open-source с 2016). Известен **очень высокой скоростью** на analytical queries (миллиарды rows за секунды). Используется в observability (logs, metrics, traces), real-time analytics, ad-tech. Компания **ClickHouse Inc.** (с 2021) — managed cloud.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [ClickHouse Documentation](https://clickhouse.com/docs)
- [ClickHouse GitHub](https://github.com/ClickHouse/ClickHouse)
- [ClickHouse Cloud](https://clickhouse.cloud/)
- [Awesome ClickHouse](https://github.com/ClickHouse/awesome-clickhouse)
- [ClickHouse Best Practices](https://clickhouse.com/docs/en/operations/tips/)
- [Altinity Knowledge Base](https://altinity.com/blog/) — open-source ClickHouse experts

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

**ClickHouse** — open-source columnar OLAP database, созданный **Yandex** для **Yandex.Metrica** (аналог Google Analytics). Open-sourced в **2016**.

**Особенности:**
- **Columnar storage** + vectorized execution
- **Очень быстрый** для analytical queries (миллиарды rows/sec scan)
- **High compression** (5-10x typical)
- **SQL** (mostly standard + extensions)
- **Distributed** (sharding, replication)
- **Real-time ingestion** (миллионы insertions/sec)

**Применения:**
- Logs aggregation (vs ELK)
- Time-series (vs Prometheus, InfluxDB)
- Web analytics
- Ad-tech analytics
- Observability backend (Tempo, SigNoz)

## Q2. (!) Почему ClickHouse такой быстрый?

**Ключевые оптимизации:**

1. **Columnar storage** — only read columns в query
2. **Vectorized execution** — обработка batches (не row-by-row), SIMD instructions
3. **Compression** — лучше cache utilization
4. **Sparse primary index** — efficient range scans
5. **Data skipping** — min/max indexes, bloom filters
6. **Async parallel inserts** — multiple inserts concurrent
7. **Distributed query** — parallel scan на shards
8. **Optimized I/O** — direct disk reads, async I/O
9. **Native protocol** — binary, compressed
10. **JIT compilation** для queries (newer versions)

**Benchmarks:** часто **10-100x** быстрее PostgreSQL для analytical queries.

## Q3. (!) ClickHouse vs Snowflake/BigQuery?

| Critterion | ClickHouse | Snowflake | BigQuery |
|-----------|-----------|-----------|----------|
| Тип | Self-hosted / Cloud | Managed only | Serverless |
| Architecture | Shared-nothing (sharded) | Shared storage + virtual warehouses | Serverless |
| Cost | Самый дешёвый | $$$ | $$ (per query) |
| Speed | Very fast | Fast | Fast |
| Concurrency | Hundreds | Thousands | Thousands |
| Setup | Manual config | Click-ops | Zero-config |
| SQL | ClickHouse dialect | ANSI SQL | ANSI SQL |
| Ecosystem | Smaller | Mature | Tied to GCP |

**Когда ClickHouse:**
- Cost-sensitive
- Already self-hosting
- Real-time ingestion (millions/sec)
- Sub-second query latency на huge data

**Когда Snowflake/BigQuery:**
- Want fully managed
- Multi-tenant DWH
- Complex schemas
- Don't want ops

## Q4. ClickHouse vs PostgreSQL для аналитики?

| Критерий | ClickHouse | PostgreSQL |
|----------|-----------|------------|
| Storage | Columnar | Row-based |
| Analytics speed | **Very fast** | Slow на больших scans |
| OLTP (point lookups) | Slow | Fast |
| Updates / deletes | Limited | Full ACID |
| Joins | Limited (broadcast только small tables) | Full |
| Schema flexibility | Limited | Full |

**PostgreSQL** для **transactional** workloads.
**ClickHouse** для **analytical** workloads.

Часто **combined:** PostgreSQL для transactions → CDC → ClickHouse для analytics.

## Q5. (!) Что такое MergeTree?

**MergeTree** — main storage engine ClickHouse. Лежит в основе всех ClickHouse tables.

**Принцип:**
- Data разбита на **parts** (immutable chunks)
- Background **merge** parts в larger ones (LSM-tree-like)
- Within part: data sorted by **ORDER BY key**
- Sparse primary index в memory

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

**ReplacingMergeTree** — deduplicates rows с одинаковым ORDER BY key (последняя insert wins, eventually).

```sql
CREATE TABLE users (
    id UInt64,
    email String,
    updated_at DateTime
) ENGINE = ReplacingMergeTree(updated_at)
ORDER BY id;
```

**SummingMergeTree** — sums numeric columns при merge для одинаковых ORDER BY.

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

**AggregatingMergeTree** — для arbitrary aggregations через `AggregateFunction`.

**MaterializedView** часто использует AggregatingMergeTree для real-time aggregations.

## Q7. CollapsingMergeTree?

**CollapsingMergeTree** — для cancelling out rows (sign +1 / -1).

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

При merge — opposite signs cancel out → final state remains.

**Use case:** updates в otherwise immutable model.

## Q8. ReplicatedMergeTree?

**ReplicatedMergeTree** — adds replication к MergeTree.

```sql
CREATE TABLE events (...) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/{shard}/events',  -- ZK/Keeper path
    '{replica}'                            -- replica identifier
)
ORDER BY (event_time);
```

**Replication:**
- **Multi-master** (any replica can accept writes)
- **Eventually consistent** (async)
- **Coordinated через ZooKeeper / ClickHouse Keeper**
- Auto-recovery после failures

**Best practice:** в production — always **ReplicatedMergeTree** (HA).

## Q9. (!) Partitioning в ClickHouse?

```sql
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)  -- partition by month
ORDER BY event_time;
```

**Partition** = subset data, stored separately.

**Эффект:**
- **Partition pruning** — query "WHERE event_time = '2025-04-19'" reads только 1 partition
- **Independent operations** — drop / detach / optimize per partition
- **Better query parallelism**

**Best practice:**
- Don't over-partition (≤ ~1000 partitions per table)
- Common: by month / week (для time-series)
- Don't partition by high-cardinality column

## Q10. (!) Sparse index — что это?

**ClickHouse** не индексирует каждую row. Вместо — **sparse index**: одна entry per **8192 rows** (default).

```
Primary index (sparse):
[key=10, granule=0]
[key=100, granule=1]
[key=200, granule=2]
...
```

**Granule** = block 8192 rows.

**Query:** WHERE key = 150 → binary search → granule 1 → scan 8192 rows → filter.

**Trade-offs:**
- **Tiny index** (fits в memory)
- **Range scans efficient**
- **Point lookup** medленнее (читаем granule)

ClickHouse **не для point lookups**, для **scans**.

## Q11. ORDER BY vs PRIMARY KEY?

```sql
ORDER BY (user_id, event_time)  -- physical sorting в parts
PRIMARY KEY user_id              -- prefix of ORDER BY (для index)
```

Если PRIMARY KEY не указан — **=ORDER BY**.

**ORDER BY:** определяет physical layout.
**PRIMARY KEY:** prefix ORDER BY для primary index.

**Best practice:** обычно `PRIMARY KEY = ORDER BY` (default).

**ORDER BY tuning** = main performance lever:
- Order columns by cardinality: low → high
- Most common filter columns first

## Q12. Skip indexes (data skipping)?

**Skip indexes** — secondary indexes для **skipping granules** (без чтения).

**Types:**
- `minmax` — min/max value per granule
- `set` — set unique values
- `bloom_filter` — bloom filter для existence checks
- `tokenbf_v1` — tokens для full-text-like search
- `ngrambf_v1` — n-grams для substrings

```sql
CREATE TABLE logs (
    timestamp DateTime,
    message String,
    INDEX message_idx message TYPE tokenbf_v1(8192, 3, 0) GRANULARITY 4
) ENGINE = MergeTree()
ORDER BY timestamp;
```

**Эффект:** queries `WHERE message LIKE '%error%'` skip irrelevant granules.

## Q13. (!) ClickHouse SQL — особенности?

ClickHouse SQL **mostly standard**, но с extensions:

```sql
-- Standard SQL
SELECT user_id, count() FROM events WHERE date = '2025-04-19' GROUP BY user_id;

-- Array functions
SELECT user_id, arrayMap(x -> x*2, prices) FROM orders;

-- Higher-order functions
SELECT arraySum(prices), arrayAvg(prices) FROM orders;

-- Approximate functions (faster, slightly inaccurate)
SELECT uniqHLL12(user_id) FROM events;  -- ~uniqExact

-- Specific functions
SELECT toStartOfHour(event_time), count() FROM events GROUP BY 1;
```

**Approximate aggregates** — `uniqHLL12`, `quantileTDigest` — **намного быстрее** чем exact, slight inaccuracy.

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

**Powerful** для denormalized data — keep arrays вместо normalize.

## Q15. (!) JOINs в ClickHouse — особенности?

**ClickHouse JOINs different от PostgreSQL:**

- **Default:** broadcast right table → all nodes (small right table)
- **Limited memory** — can OOM на large joins
- **No hash spill to disk** (older versions)
- Slower than column scans

**Best practices:**
- **Avoid joins** if possible (use denormalized)
- **Small right table** for joins (broadcast)
- **Use `IN` instead of JOIN** для filter:
```sql
-- Slower
SELECT * FROM events JOIN users ON events.user_id = users.id WHERE users.country = 'US';

-- Faster
SELECT * FROM events WHERE user_id IN (SELECT id FROM users WHERE country = 'US');
```

- **Distributed JOIN strategies:** GLOBAL, ALLOW_EXPERIMENTAL_PARALLEL_REPLICAS

В **2025** joins improved (parallel hash join, grace hash), но ClickHouse всё equal **denormalized** wins.

## Q16. Window functions?

```sql
SELECT
    user_id,
    event_time,
    sum(amount) OVER (PARTITION BY user_id ORDER BY event_time) AS running_total,
    rank() OVER (PARTITION BY user_id ORDER BY amount DESC) AS rnk
FROM transactions;
```

ClickHouse поддерживает window functions с **2021** (некоторые limitations vs PostgreSQL).

## Q17. (!) Materialized views в ClickHouse?

**ClickHouse MV — different от PostgreSQL:**

- **Updated incrementally** при INSERT в source
- **Pre-aggregations** в realtime
- **No refresh** — always up-to-date
- **Triggered on INSERT** только

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

При **каждом INSERT** в `events` → автоматически update `hourly_stats`.

**Use case:** real-time dashboards без heavy queries на raw data.

## Q18. Projections?

**Projections** (с 2020) — alternative MVs, automatic.

```sql
ALTER TABLE events ADD PROJECTION events_by_user (
    SELECT *
    ORDER BY user_id
);
```

ClickHouse **automatically** chooses projection если query benefits.

**Vs MVs:**
- Projections — multiple sort orders для same table
- MVs — pre-computed aggregates / different table

## Q19. (!) Distributed table?

**Distributed table** — proxy table querying across **shards**.

```sql
-- Local table on each node
CREATE TABLE events_local (...) ENGINE = ReplicatedMergeTree(...);

-- Distributed table (no data of its own, queries across shards)
CREATE TABLE events_distributed AS events_local
ENGINE = Distributed(my_cluster, default, events_local, rand());
```

**Insert into distributed** → routed к shards based on sharding key.
**Query distributed** → parallelized across shards, merged.

**Cluster definition** в config:
```xml
<remote_servers>
  <my_cluster>
    <shard><replica><host>node1</host></replica></shard>
    <shard><replica><host>node2</host></replica></shard>
  </my_cluster>
</remote_servers>
```

## Q20. Replication через ZooKeeper / Keeper?

**ReplicatedMergeTree** требует **coordination service**:
- **Apache ZooKeeper** (старый default)
- **ClickHouse Keeper** (newer, native)

Coordination для:
- Replica registration
- Insertion ordering (queue)
- Merge synchronization
- Leader election
- DDL queries propagation

## Q21. ClickHouse Keeper vs ZooKeeper?

**ClickHouse Keeper** (с 2021) — drop-in replacement ZooKeeper, написан на C++.

**Преимущества:**
- **Faster** (10x для some operations)
- **Less memory**
- Embeddable в ClickHouse process
- Same Raft-based consensus
- Compatible с ZooKeeper protocol

В **2025** — Keeper recommended для new deployments.

## Q22. (!) Storage engine оптимизации (compression, sparse data)?

**Compression codecs:**
- LZ4 (default, fast)
- ZSTD (better ratio)
- Specialized: Delta, DoubleDelta (for time-series), Gorilla (floats)

```sql
CREATE TABLE metrics (
    timestamp DateTime CODEC(DoubleDelta, ZSTD),
    value Float64 CODEC(Gorilla, ZSTD)
) ENGINE = MergeTree() ORDER BY timestamp;
```

**Effect:** **5-10x compression** typical, sometimes 50x.

**LowCardinality(String)** — for low-cardinality columns (status, country):
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

**Cost saving** — old data на cheap S3.

## Q24. (!) Kafka engine для ingestion?

**Kafka Engine** — table reads from Kafka topic.

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

**Continuous ingestion** Kafka → ClickHouse, без external pipeline.

**Aналогично:** RabbitMQ Engine, NATS Engine, S3 Queue Engine.

## Q25. Other table engines (S3, MySQL, PostgreSQL)?

**Table functions / external engines:**
- **S3** — read/write Parquet/CSV/JSON в S3
```sql
SELECT * FROM s3('s3://bucket/data.parquet', 'Parquet')
```
- **PostgreSQL** — federated queries
```sql
CREATE TABLE pg_users ENGINE = PostgreSQL('host:5432', 'db', 'users', 'user', 'pass');
```
- **MySQL** — same
- **HDFS, URL, File** engines

**Use case:** ETL без external pipeline — `INSERT INTO local SELECT FROM s3(...)`.

## Q26. (!) Какие use cases ClickHouse в production?

**Adopters:**
- **Yandex** (founders) — Metrica
- **Cloudflare** — analytics
- **Uber** — observability (logs, metrics, traces)
- **Spotify** — analytics
- **Mercedes-Benz, eBay, GitLab, Lyft**

**Common use cases:**
- **Observability** — logs (cheaper ELK), metrics (Prometheus replacement), traces (Jaeger backend)
- **Real-time analytics** — dashboards
- **Ad-tech** — bid analysis, attribution
- **Web analytics** — clickstream
- **Time-series** — IoT, monitoring
- **Data warehouse** для real-time analytics

## Q27. (!) Какие ограничения / минусы ClickHouse?

1. **No transactions** (point inserts only)
2. **Limited UPDATE/DELETE** (mutations slow, async)
3. **Joins** weaker чем PostgreSQL
4. **Schema-on-write** (rigid schemas)
5. **No foreign keys, no constraints**
6. **Sparse indexing** — bad для point queries
7. **Steep learning curve** для tuning
8. **Cluster ops complex** (sharding, replication setup)
9. **Memory hungry** (queries can OOM)
10. **Bytes per row** — wide tables can be inefficient

## Q28. Common pitfalls в ClickHouse production?

1. **Wrong ORDER BY** — slow queries, big indices
2. **High-cardinality partition key** — too many parts, merges fail
3. **OOM on joins** — broadcast large tables
4. **Async inserts** — small data loss possible (configurable)
5. **Mutations** — slow, lock parts
6. **No retention TTL** — disk full
7. **Too many small inserts** — overload ZK/Keeper
8. **No replication** — data loss on disk failure
9. **No backups** (ALTER TABLE FREEZE → backup parts)
10. **Wrong engine choice** (MergeTree vs ReplacingMergeTree etc)

**Best practice:** read [ClickHouse docs Tips and Tricks](https://clickhouse.com/docs/en/operations/tips/) carefully.

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
