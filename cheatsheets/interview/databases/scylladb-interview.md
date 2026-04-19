---
title: "Вопросы на собеседовании: ScyllaDB"
description: "ScyllaDB: drop-in Cassandra replacement в C++, shared-nothing, shard-per-core, lower latency, higher throughput, Cassandra-compatible (CQL), Seastar framework, vs Cassandra"
tags:
  - interview
  - databases
  - scylladb-interview
aliases:
  - "ScyllaDB interview"
  - "ScyllaDB собеседование"
  - "Scylla vs Cassandra"
  - "Wide-column NoSQL interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `ScyllaDB`

`ScyllaDB` — open-source NoSQL wide-column database. **Drop-in replacement Cassandra**, написан на **C++** (vs Cassandra Java) с **shard-per-core** architecture. Обещает **10x lower latency** и higher throughput. Использует **Seastar framework** для async I/O. Создан 2014 ex-Cloudius (KVM).

Дата последнего обновления: 2026-04-19

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

**ScyllaDB** — open-source NoSQL wide-column database. **API-compatible с Apache Cassandra**, но переписан с нуля на **C++** для significantly better performance.

**Заявленные метрики vs Cassandra:**
- **10x lower latency**
- **3-5x higher throughput**
- **Same data model** (CQL, replication, consistency)

**Created в 2014** by ex-KVM (Cloudius Systems), commercial company **ScyllaDB Inc.**

**Применения:** Same as Cassandra:
- Time-series data
- IoT
- Ad-tech (real-time bidding)
- Messaging platforms (Discord использует Scylla)
- High-throughput logging

## Q2. (!) ScyllaDB vs Cassandra — основные отличия?

| Критерий | Apache Cassandra | ScyllaDB |
|----------|------------------|----------|
| Language | Java (JVM) | C++ |
| Architecture | Thread-pool | **Shard-per-core (Seastar)** |
| GC | Yes (JVM GC pauses) | No GC |
| Latency p99 | ~5-50 ms | ~1-5 ms |
| Throughput | High | **3-5x higher** |
| Memory usage | High | Lower |
| CPU efficiency | Lower | **Much higher** |
| Compatibility | Original | API-compatible |
| Maturity | 2008+ | 2014+ |
| Adoption | Wider | Growing |

**Performance** — main differentiator. На **same hardware** Scylla typically 3-10x faster.

## Q3. (!) Что такое shard-per-core архитектура?

**Cassandra:**
- Thread pools (work-stealing)
- Locks, contention между cores
- JVM overhead

**ScyllaDB (shared-nothing per core):**
- **One shard per CPU core**
- Each shard owns subset data
- **No locks** между cores (no contention)
- **No shared memory** между cores
- Network requests routed к correct core

```
8-core node:
  Core 0 → shard 0 (range A-D)
  Core 1 → shard 1 (range E-H)
  Core 2 → shard 2 (range I-L)
  ...
```

**Effect:**
- **Linear scaling с cores** (Cassandra plateaus)
- No GC pauses
- Predictable latency

## Q4. Seastar framework?

**Seastar** — C++ framework created by ScyllaDB authors. Designed для **modern hardware** (multi-core CPUs, fast NICs).

**Principles:**
- **Shared-nothing per-core**
- **Asynchronous I/O** (no blocking)
- **User-space networking** (DPDK option)
- **Future/Promise** abstraction

Used не только для ScyllaDB — also Redis-like project, network apps.

## Q5. (!) Wide-column data model?

**Same as Cassandra** (just performance differs).

```sql
CREATE TABLE users (
    user_id UUID,
    timestamp TIMESTAMP,
    event_type TEXT,
    data TEXT,
    PRIMARY KEY (user_id, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);
```

**Concepts:**
- **Keyspace** = database/schema
- **Table** = collection of rows
- **Partition key** — determines node placement
- **Clustering key** — sorting within partition
- **Columns** — defined в schema

Подробнее — в [Apache Cassandra](cassandra-interview.md).

## Q6. Partition key, clustering key?

```sql
PRIMARY KEY ((partition_key), clustering_key1, clustering_key2)
```

**Partition key** — hashed → which node owns row.
**Clustering key** — orders rows within partition.

**Composite partition key:**
```sql
PRIMARY KEY ((user_id, date), timestamp)
-- partition by combination, ordered by timestamp
```

**Best practices:**
- High cardinality partition key (avoid hot partitions)
- Partition size < 100 MB
- Order clustering keys для query patterns

## Q7. CQL (Cassandra Query Language)?

**CQL** — SQL-like language для Cassandra/Scylla.

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

**Limitations vs SQL:**
- **No JOINs** (denormalize данные)
- **No aggregations across partitions** (limited)
- **WHERE only on partition key + clustering key** (or secondary index)
- **No subqueries**

ScyllaDB extensions:
- **CDC (Change Data Capture)**
- **Materialized Views**

## Q8. (!) Shared-nothing distributed?

**Shared-nothing** — каждый node independent, no shared state.

```
Node 1: own data, own CPU, own memory, own disk
Node 2: same
Node 3: same
```

**Coordination via gossip protocol** (peer-to-peer). No master.

**Benefits:**
- Linear scaling
- No single point of failure
- Easy add/remove nodes

**Trade-off:** eventual consistency by default (tunable).

## Q9. Replication, consistency levels?

**Replication factor (RF):** how many copies of data.
```sql
CREATE KEYSPACE mykeyspace WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': 3, 'dc2': 3};
```

**Consistency levels (per-query):**
- `ANY` — at least one replica (включая hinted handoff)
- `ONE`, `TWO`, `THREE` — N replicas
- `QUORUM` — majority (N/2 + 1)
- `ALL` — все replicas
- `LOCAL_QUORUM` — quorum в local DC
- `EACH_QUORUM` — quorum в каждом DC

```sql
SELECT * FROM users WHERE user_id = ? USING CONSISTENCY QUORUM;
```

## Q10. Tunable consistency (R + W > N)?

**Strong consistency formula:**
```
R + W > N
```
- R = read consistency level
- W = write consistency level
- N = replication factor

**Example:** RF=3
- W=QUORUM (2) + R=QUORUM (2): 2+2 > 3 ✓ strong
- W=ONE (1) + R=ONE (1): 1+1 < 3 ✗ eventual

**Performance trade-off:**
- Higher consistency → higher latency, lower availability
- Lower consistency → faster, eventually consistent

**Common choice:** `LOCAL_QUORUM` для both — balance.

## Q11. Tokens, virtual nodes (vnodes)?

**Token ring** — hash space (`-2^63` to `2^63-1`).

Each row's partition key → hashed → token → mapped к node owning that token range.

**vnodes** — каждая physical node owns multiple **virtual node** ranges.

```
Without vnodes:
  Node 1: tokens 0-1000
  Node 2: tokens 1001-2000

With 256 vnodes per node:
  Node 1: 256 random ranges
  Node 2: 256 random ranges
```

**Benefits vnodes:**
- **Better load balancing**
- **Faster recovery** (parallel data transfer from many nodes)
- **Easier scaling** (adding new node — pulls data from many)

ScyllaDB supports vnodes (also tokens).

## Q12. (!) LSM-tree storage?

**LSM-tree (Log-Structured Merge-tree)** — storage structure used by Cassandra/Scylla, RocksDB, ClickHouse, etc.

**Architecture:**
```
Write → MemTable (in-memory sorted)
       ↓ flush when full
       SSTable (immutable on-disk file)
       ↓ background compaction
       Larger SSTables
```

**Benefits:**
- **Fast writes** (sequential, batched)
- **Fast reads if cached** в MemTable / Bloom filter

**Trade-off:**
- **Read amplification** — need check several SSTables
- **Compaction overhead** — background work

**Compaction strategies:**
- **STCS (SizeTieredCompactionStrategy)** — default
- **LCS (LeveledCompactionStrategy)** — better for read-heavy
- **TWCS (TimeWindowCompactionStrategy)** — для time-series

## Q13. (!) Why is Scylla faster than Cassandra?

1. **C++ vs Java** — no JVM overhead, no GC pauses
2. **Shard-per-core** — no lock contention
3. **Seastar async I/O** — efficient I/O
4. **Direct disk I/O** — bypasses kernel buffers (in some configs)
5. **Better CPU utilization** — designed для modern multi-core
6. **No GC pauses** — predictable p99 latency
7. **Tighter memory management** — no Java heap overhead
8. **Custom networking** (DPDK option) — bypass kernel TCP stack

**Result:** на same hardware, Scylla often 3-10x faster Cassandra.

## Q14. No JVM = no GC pauses?

**Cassandra GC pauses:**
- Stop-the-world pauses (10-500 ms)
- Spike в p99 latency
- Hard tuning (G1GC, ZGC tweaks)

**Scylla:**
- No GC (manual memory management в C++)
- Predictable latency
- p99 latency 5-10x lower

**Major reason** Discord, Comcast, и других **migrated** Cassandra → Scylla.

## Q15. Workload prioritization?

ScyllaDB Enterprise feature: **workload prioritization** — different workloads get different shares CPU/IO.

```
Critical OLTP queries: 80% resources
Background analytics: 20% resources
```

**Use case:** mixed workloads (OLTP + reporting) на одном cluster без impact.

В Cassandra нет native equivalent — обычно separate clusters.

## Q16. (!) Cassandra drop-in replacement — насколько true?

ScyllaDB **highly compatible**:
- Same CQL
- Same wire protocol (Cassandra clients works)
- Same data model
- Same replication
- Same consistency levels

**Migrate apps без code changes** в большинстве случаев.

**Diferences (not 100% drop-in):**
- Some advanced Cassandra features missing (или differently implemented)
- Operations / monitoring differs
- Tuning parameters differ
- Versions diverge over time

**Best practice:** test thoroughly. Use **Scylla Migration Tools**.

## Q17. (!) DynamoDB API (Alternator)?

**Scylla Alternator** — DynamoDB API на ScyllaDB.

```python
# Standard boto3 DynamoDB client works
import boto3
dynamodb = boto3.resource('dynamodb', endpoint_url='http://scylla:8000')
table = dynamodb.Table('users')
table.put_item(Item={'PK': 'user#123', 'name': 'Alice'})
```

**Зачем:**
- Self-hosted DynamoDB-compatible (no AWS lock-in)
- Run DynamoDB workloads on-premise / multi-cloud
- Cheaper than DynamoDB at scale

В **2025** — viable alternative для apps already designed для DynamoDB.

## Q18. (!) Когда выбрать Scylla?

**Выбирай Scylla когда:**
- Already running Cassandra, want better performance
- **Predictable low latency** required (p99 < 10ms)
- High throughput (10K+ writes/sec per node)
- Cost-conscious (fewer nodes for same throughput)
- Mixed workloads need isolation
- IoT, time-series, ad-tech (typical Cassandra use cases)

**Не выбирай когда:**
- Need full SQL (joins, aggregations, transactions)
- Smaller scale (PostgreSQL достаточен)
- Strong ACID needs
- Team has no NoSQL experience

## Q19. Time-series workloads?

**Time-series ideal для Scylla/Cassandra:**
- Append-only
- High write throughput
- Time-windowed reads

**Schema:**
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

**TWCS** auto-compacts старые data efficiently. Old SSTables можно drop через TTL.

## Q20. IoT?

IoT = millions of devices sending data continuously.

**Scylla хорош:**
- High write throughput (millions writes/sec per cluster)
- Time-series storage
- Geo-distributed (multi-DC)
- Auto-expire с TTL

**Adopters:** Comcast, Tubi, Discord, Numberly.

## Q21. (!) Open Source vs Enterprise vs ScyllaDB Cloud?

**Open Source (free):**
- Apache 2.0 (very permissive)
- Core features
- Self-managed

**ScyllaDB Enterprise (paid):**
- Workload prioritization
- LDAP, encryption, compliance
- Faster compaction strategies
- 24/7 support

**ScyllaDB Cloud (managed):**
- Fully managed на AWS, GCP, Azure
- Multi-region
- Auto backups
- Pay-as-you-go

В **2025** — Open Source отлично для most workloads. Enterprise для security/compliance heavy.

## Q22. (!) Migration Cassandra → Scylla?

**Steps:**
1. **Compatibility check** — same Cassandra version features?
2. **Provision Scylla cluster** (parallel)
3. **Dual-write** — application writes к both (или CDC stream)
4. **Bulk-copy historical data** — Scylla Migrator (Spark-based) или sstableloader
5. **Validate data parity**
6. **Switch reads** к Scylla (one node at a time)
7. **Stop dual-write**
8. **Decommission Cassandra**

**Зачастую** **transparent для app** — same CQL.

**Tools:** Scylla Migrator, sstableloader, custom CDC.

## Q23. Какие частые проблемы Scylla в production?

1. **Hot partitions** (same as Cassandra) — wrong PK choice
2. **Tombstones** — high deletes overload reads
3. **Compaction backlog** — write rate > compaction speed
4. **Disk I/O bottleneck** — slow disks
5. **Wrong consistency level** — too strong = slow, too weak = inconsistencies
6. **Too few nodes** — scaling за reach
7. **No backups** — Scylla Manager / snapshots needed
8. **Not enough monitoring** — Scylla Monitoring stack obligatory
9. **Schema migrations** — slow on large tables
10. **Not understanding shard-per-core** — connection pool tuning critical

**Best practice:** use **Scylla Manager** для backups, repairs, schema management.

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
