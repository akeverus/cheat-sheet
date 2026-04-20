---
title: "Вопросы на собеседовании: Database Performance"
description: "Database performance tuning: indexes, query plans, N+1, connection pooling, EXPLAIN ANALYZE, partitioning, stats, slow query log, pgbench, Aurora"
tags:
  - interview
  - performance
  - database-performance-interview
aliases:
  - "Database Performance interview"
  - "Query optimization"
  - "SQL tuning"
  - "Database Performance собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Database Performance`

`Database Performance` — **топ-1 причина медленных приложений**. В 90% случаев slow responses = bad query, missing index, N+1, connection pool. Знание EXPLAIN, pg_stat_statements, execution plan — must-have senior backend.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

### Официальная документация

- [PostgreSQL Performance Tuning](https://wiki.postgresql.org/wiki/Performance_Optimization)
- [MySQL Performance Schema](https://dev.mysql.com/doc/refman/8.0/en/performance-schema.html)
- [Use the Index, Luke](https://use-the-index-luke.com/) — Markus Winand
- [pgtune](https://pgtune.leopard.in.ua/) — config calculator
- [PostgreSQL EXPLAIN](https://www.postgresql.org/docs/current/sql-explain.html)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Диагностика**
- [Q1. (!) Как диагностировать slow query?](#q1--как-диагностировать-slow-query)
- [Q2. (!) EXPLAIN vs EXPLAIN ANALYZE?](#q2--explain-vs-explain-analyze)
- [Q3. (!) Как читать execution plan?](#q3--как-читать-execution-plan)
- [Q4. Seq Scan vs Index Scan vs Bitmap Scan?](#q4-seq-scan-vs-index-scan-vs-bitmap-scan)

**Индексы**
- [Q5. (!) Когда index помогает, когда не помогает?](#q5--когда-index-помогает-когда-не-помогает)
- [Q6. (!) Composite index column order?](#q6--composite-index-column-order)
- [Q7. Covering index (INCLUDE)?](#q7-covering-index-include)
- [Q8. Partial index?](#q8-partial-index)
- [Q9. Index bloat и REINDEX?](#q9-index-bloat-и-reindex)

**N+1 и ORM**
- [Q10. (!) N+1 problem — как обнаружить и исправить?](#q10--n1-problem--как-обнаружить-и-исправить)
- [Q11. JOIN FETCH vs subselect vs batch size?](#q11-join-fetch-vs-subselect-vs-batch-size)

**Connection management**
- [Q12. (!) Connection pooling — зачем?](#q12--connection-pooling--зачем)
- [Q13. (!) HikariCP settings?](#q13--hikaricp-settings)
- [Q14. PgBouncer transaction vs session pooling?](#q14-pgbouncer-transaction-vs-session-pooling)

**Партицирование и шардирование**
- [Q15. (!) Partitioning — когда применять?](#q15--partitioning--когда-применять)
- [Q16. Range / List / Hash partitioning?](#q16-range--list--hash-partitioning)

**Статистика и vacuum**
- [Q17. (!) ANALYZE и статистика оптимизатора?](#q17--analyze-и-статистика-оптимизатора)
- [Q18. VACUUM, autovacuum, bloat?](#q18-vacuum-autovacuum-bloat)

**Конфигурация**
- [Q19. (!) shared_buffers, work_mem, effective_cache_size?](#q19--shared_buffers-work_mem-effective_cache_size)
- [Q20. WAL и checkpoint tuning?](#q20-wal-и-checkpoint-tuning)

**Оптимизация запросов**
- [Q21. (!) LIMIT + OFFSET проблема pagination?](#q21--limit--offset-проблема-pagination)
- [Q22. JOIN vs subquery vs EXISTS?](#q22-join-vs-subquery-vs-exists)
- [Q23. Window functions performance?](#q23-window-functions-performance)
- [Q24. Materialized views vs views?](#q24-materialized-views-vs-views)

**Production**
- [Q25. (!) Read replicas — когда и как?](#q25--read-replicas--когда-и-как)
- [Q26. (!) Как находить slow queries в prod?](#q26--как-находить-slow-queries-в-prod)
- [Q27. Database load test (pgbench, sysbench)?](#q27-database-load-test-pgbench-sysbench)

## Q1. (!) Как диагностировать slow query?

**Систематический approach:**

**1. Identify:**
- APM tool (DataDog, New Relic) показывает slow endpoint
- `pg_stat_statements` — top-N queries by total/mean time
- Slow query log (threshold, например 1s)

**2. Reproduce:**
- Get query + parameters
- Run в test environment с prod-like data volume

**3. EXPLAIN ANALYZE:**
```sql
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE user_id = 123 AND status = 'PAID';
```

**4. Identify bottleneck:**
- **Seq Scan** большой таблицы → missing index?
- **Sort** с disk → work_mem too low?
- **Nested Loop** многомиллионных tables → wrong plan; join order?
- **High Buffers: read** → cache miss; cold data

**5. Fix hypothesis:**
- Add index
- Rewrite query
- Adjust work_mem / config
- Partition table

**6. Verify:**
- EXPLAIN ANALYZE после change
- Compare: planning time, execution time, rows, buffers

**7. Monitor:**
- Deploy to prod
- Watch metrics (latency, query time)

**Checklist first-touch:**
- Index на WHERE columns?
- Statistics up-to-date (`ANALYZE`)?
- Query returns reasonable row count (not SELECT *)?
- Type mismatches (function on indexed col: `WHERE LOWER(email) =` breaks index)?

## Q2. (!) EXPLAIN vs EXPLAIN ANALYZE?

**EXPLAIN:** shows **planned** execution plan (estimates).
- Fast, no actual execution
- Estimated costs / rows
- Hypothetical

**EXPLAIN ANALYZE:** **actually executes** query + measures.
- Real timings, real row counts
- Detects **estimate vs actual mismatch** (bad stats!)
- Slow queries take same time to run (ANALYZE не bypass execution)
- **Warning:** DML executes! Use `BEGIN; EXPLAIN ANALYZE UPDATE ...; ROLLBACK;`

**Useful options:**
```sql
EXPLAIN (ANALYZE, BUFFERS, VERBOSE, FORMAT JSON)
SELECT ...;
```

- **BUFFERS:** shows shared buffers hit / read (cache effectiveness)
- **VERBOSE:** output column lists
- **FORMAT JSON/YAML/XML:** for tooling (e.g., pev2 visualizer)
- **SETTINGS:** show non-default config values
- **WAL:** WAL records written (PG 13+)

**Read output:**
- `actual rows=100` vs `rows=10000` → estimate 100× off → stats stale, `ANALYZE`
- `Buffers: shared hit=X read=Y` — read = disk, hit = cache; high read = cold data
- `Execution Time: 234 ms` — real duration

**Visualizers:**
- **pev2** / **explain.depesz.com** — color-coded bottlenecks
- **Dalibo Visual Explain**

**Gotcha:** cold run != warm. Run 2x — first hits disk, second from cache. Use `BUFFERS` to see.

## Q3. (!) Как читать execution plan?

**Plan — tree of operations**, bottom-up execution:

```
Nested Loop  (cost=0.42..8.46 rows=1 width=128) (actual time=0.05..0.12 rows=1 loops=1)
  -> Index Scan using users_pkey on users  (cost=0.28..8.29 rows=1 width=64)
       Index Cond: (id = 123)
  -> Index Scan using orders_user_id_idx on orders  (cost=0.14..0.16 rows=1 width=64)
       Index Cond: (user_id = users.id)
       Filter: (status = 'PAID')
```

**Читать:**
- **Indentation:** deeper = earlier (leaves first)
- Each node: operation, cost, rows, width, actual time
- `cost=startup..total` (not ms, abstract units; ratio matters)
- `rows`: estimated; `actual time`: real measurement
- `loops`: execute count (multiply actual time × loops for real total)

**Key node types:**
- **Seq Scan:** full table scan
- **Index Scan:** follows index pointers to rows
- **Index Only Scan:** index contains all needed cols (fast)
- **Bitmap Heap Scan + Bitmap Index Scan:** batch reads sorted by disk page
- **Hash Join:** builds hash of smaller table
- **Merge Join:** both sorted; merges
- **Nested Loop:** for each row outer, scan inner
- **Sort:** orders result; disk spill bad
- **Aggregate / HashAggregate:** GROUP BY
- **Gather / Gather Merge:** parallel workers combine

**Warning signs:**
- `rows=1 (actual rows=1000)` — huge mis-estimate
- `Sort Method: external merge Disk: 500MB` — work_mem too small
- `Seq Scan` big table без filter → missing index
- `Nested Loop` с large outer → bad; expected Hash Join
- High `Rows Removed by Filter` — filter должен быть в index condition

## Q4. Seq Scan vs Index Scan vs Bitmap Scan?

**Seq Scan (Sequential Scan):**
- Read table page-by-page от start to end
- **Fast для:** small table, reading ≥ ~30% rows
- **Slow для:** large table, needle-in-haystack
- Optimal для `COUNT(*)` без WHERE

**Index Scan:**
- Walk index → fetch rows from table (random I/O)
- **Fast для:** selective queries (few rows)
- **Slow для:** non-selective (many random page reads)
- Returns rows in **index order**

**Index Only Scan:**
- All needed columns в index → no table visit
- Visibility map checked (PostgreSQL MVCC)
- Fastest

**Bitmap Index Scan + Bitmap Heap Scan:**
- Step 1: scan index → bitmap of TIDs
- Step 2: sort bitmap by page, read heap **sequentially**
- **Fast для:** medium selectivity (10-30% rows)
- Not ordered output

**When planner chooses what:**
- Selectivity via statistics
- Random vs sequential I/O costs (`random_page_cost`, `seq_page_cost`)
- Default: `random_page_cost=4` — assumption HDD; для SSD set `=1.1` (major perf improvement!)

**Example:**
```sql
-- 1M rows, returning 100 rows → Index Scan
EXPLAIN SELECT * FROM orders WHERE user_id = 42;

-- 1M rows, returning 500K → Seq Scan
EXPLAIN SELECT * FROM orders WHERE amount > 10;
```

## Q5. (!) Когда index помогает, когда не помогает?

**Index помогает:**
- `WHERE col = value` (equality)
- `WHERE col > X` (range) — B-tree ordered
- `ORDER BY col` — avoid sort
- `GROUP BY col` — clustering
- `JOIN ON a.col = b.col`

**Index НЕ помогает:**

**1. Function on column:**
```sql
WHERE LOWER(email) = 'x@y.com'  -- normal index не используется!
```
Fix: functional index: `CREATE INDEX ON t (LOWER(email));`

**2. Leading wildcard:**
```sql
WHERE name LIKE '%john%'  -- no index (B-tree prefix only)
```
Fix: trigram index (`pg_trgm`) or full-text search

**3. Type mismatch:**
```sql
-- col is VARCHAR, passing INT
WHERE phone = 123456  -- implicit cast breaks index
```

**4. Non-selective query:**
- Returning > 30% rows → Seq Scan win
- e.g., `WHERE active = true` когда 90% rows active — index useless

**5. OR без indexes на обе cols:**
```sql
WHERE a = 1 OR b = 2  -- if only a indexed, partial
```
Fix: index на оба OR UNION queries

**6. NOT IN / != (in some cases):**
- Planner может choose seq scan

**7. Data distribution skew:**
- 99% `status='ACTIVE'`, 1% `status='DELETED'`
- Query `WHERE status='DELETED'` wants index
- Query `WHERE status='ACTIVE'` wants seq scan
- Solution: **partial index** для `WHERE status='DELETED'`

**Check index usage:**
```sql
SELECT * FROM pg_stat_user_indexes WHERE idx_scan = 0;  -- unused indexes
```

## Q6. (!) Composite index column order?

**Rule:** **most selective first** is myth — **real rule: query access pattern.**

**Left-prefix rule:**
Index `(a, b, c)` can be used для:
- `WHERE a = ?`
- `WHERE a = ? AND b = ?`
- `WHERE a = ? AND b = ? AND c = ?`
- NOT для `WHERE b = ?` (skips leading col!)
- NOT для `WHERE a = ? AND c = ?` (uses только `a`)

**Order by query pattern:**
1. **Equality columns first** (leftmost)
2. **Range last** (breaks prefix for subsequent)
3. **Sort columns** after equality

**Example:**
Queries:
- `WHERE user_id = ? AND created_at > ?`
- `WHERE user_id = ? ORDER BY created_at DESC`

Index: `(user_id, created_at)` — both queries helped.

**Bad:** `(created_at, user_id)` — first query does range scan, second can't leverage sort.

**Multi-workload:**
Если different queries touch different columns, может понадобиться multiple indexes. Но cost:
- Write overhead (INSERT/UPDATE updates all indexes)
- Disk space
- Maintenance

**Rule of thumb:** < 10 indexes per table; more = review patterns.

**Index-only scan bonus:**
Include frequently selected cols:
```sql
CREATE INDEX idx ON orders (user_id, created_at) INCLUDE (amount, status);
```

## Q7. Covering index (INCLUDE)?

**Covering index** — contains all columns query needs (SELECT + WHERE), позволяя **Index Only Scan**.

**Old way (pre-PG 11):**
```sql
CREATE INDEX ON orders (user_id, amount, status);
```
- All cols в B-tree key → larger index, affects ordering

**Modern way (PG 11+):**
```sql
CREATE INDEX ON orders (user_id) INCLUDE (amount, status);
```
- `user_id` в key (sorted, searchable)
- `amount, status` в **leaf pages** (not sorted, just stored)
- Smaller than full multi-col index
- Index Only Scan still works

**Benefit:** avoid heap visit = faster.

**Query:**
```sql
SELECT amount, status FROM orders WHERE user_id = 42;
```
Plan:
```
Index Only Scan using idx on orders
  Index Cond: (user_id = 42)
  Heap Fetches: 0
```

**Gotcha MVCC:**
- Visibility map must show page all-visible (after VACUUM)
- If recently updated → "Heap Fetches: N" → not pure index-only

**Trade-off:**
- Size growth
- Write overhead
- Best для hot read paths

## Q8. Partial index?

**Partial index** — index только subset rows (`WHERE` clause в CREATE INDEX).

```sql
CREATE INDEX idx_pending_orders ON orders (created_at)
WHERE status = 'PENDING';
```

**Use cases:**
- Skewed data (99% рows one value) → index только rare values
- Soft delete: `WHERE deleted_at IS NULL` — index only active rows
- Hot query pattern

**Benefits:**
- **Smaller** (subset of rows)
- **Faster** writes (only matching rows update)
- **Faster** reads (less data to traverse)

**Example savings:**
- Table 100M rows, 99M completed, 1M pending
- Full index: 100M entries
- Partial (`WHERE status='PENDING'`): 1M entries — 100× smaller

**Query must match predicate exactly:**
```sql
-- Works (matches WHERE)
SELECT ... FROM orders WHERE status='PENDING' AND created_at > ...;

-- Doesn't use index (no status filter)
SELECT ... FROM orders WHERE created_at > ...;
```

**Planner проверяет:** query predicate implied by index predicate → use.

**Common patterns:**
- `WHERE enabled = true`
- `WHERE deleted_at IS NULL`
- `WHERE region = 'US'` for region-specific queries

## Q9. Index bloat и REINDEX?

**Index bloat** — pages частично заполнены (UPDATE/DELETE оставляют dead tuples). Index grows beyond data size → slower scans, more I/O.

**Причины:**
- UPDATE = MVCC insert + mark old dead
- DELETE mark dead; VACUUM removes eventually
- Long transactions prevent cleanup

**Detection:**
```sql
SELECT schemaname, tablename, indexname,
       pg_size_pretty(pg_relation_size(indexrelid)) AS size
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC;
```

Или extension `pgstattuple`:
```sql
SELECT * FROM pgstatindex('idx_name');
-- leaf_fragmentation, avg_leaf_density
```

Low `avg_leaf_density` (< 50%) → bloat.

**Fix:**

**REINDEX:**
```sql
REINDEX INDEX idx_name;          -- locks writes (PG <12)
REINDEX INDEX CONCURRENTLY ...;  -- non-blocking (PG 12+)
```

**CREATE + DROP (pre-12):**
```sql
CREATE INDEX CONCURRENTLY idx_new ON t (...);
DROP INDEX idx_old;
ALTER INDEX idx_new RENAME TO idx_old;
```

**pg_repack / pg_squeeze:** online table + index rebuild без long lock.

**Prevention:**
- Regular autovacuum (correctly configured)
- Avoid very long transactions (xmin horizon blocks cleanup)
- **HOT updates** (no indexed col changed) don't grow indexes

## Q10. (!) N+1 problem — как обнаружить и исправить?

**N+1:** 1 query для list + N queries (one per item) для related entity.

**Example (JPA/Hibernate):**
```java
List<Order> orders = orderRepo.findAll();  // 1 query
for (Order o : orders) {
    System.out.println(o.getUser().getName());  // N queries lazy load!
}
```
→ 1 + 100 = 101 DB roundtrips for 100 orders.

**Detection:**
- **APM** (DataDog tracing) — spans показывают bulk similar queries
- **Hibernate statistics:** `hibernate.generate_statistics=true`; log `queryExecutionCount`
- **p6spy** — log all SQL
- **JPA Buddy / QuickPerf** — tests assert no N+1

**Fixes:**

**JOIN FETCH:**
```java
@Query("SELECT o FROM Order o JOIN FETCH o.user WHERE ...")
```
One query with JOIN → 1 query vs 101.

**EntityGraph:**
```java
@EntityGraph(attributePaths = {"user", "items"})
List<Order> findAll();
```

**Batch fetch size:**
```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
```
Groups N queries into IN (...) batches of 20.

**DTO projection:**
```java
@Query("SELECT new com.OrderDto(o.id, u.name) FROM Order o JOIN o.user u")
```
Direct flat query, no entity graph.

**Which choice:**
- Small collection: JOIN FETCH
- Many-to-many pagination: batch fetch (JOIN FETCH duplicates)
- Read-only: DTO projection (fastest)

**Not only Hibernate:** any ORM + loops has this.

## Q11. JOIN FETCH vs subselect vs batch size?

**Сценарий:** load `Order` + `OrderItems`.

**JOIN FETCH:**
```sql
SELECT o.*, i.*
FROM orders o
LEFT JOIN order_items i ON i.order_id = o.id
```
- **1 query**
- Cartesian explosion при multiple collections
- **Pagination broken** (Hibernate loads all, paginates in memory — warn)

**Batch (subselect) fetching:**
```properties
hibernate.batch_fetch_style=dynamic
hibernate.default_batch_fetch_size=16
```
Hibernate issues:
```sql
SELECT * FROM orders WHERE id IN (1, 2, ..., 16);
SELECT * FROM order_items WHERE order_id IN (1, 2, ..., 16);
```
- 2 queries для 16 orders
- No cartesian
- Works with pagination

**Subselect fetch:**
```java
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.SUBSELECT)
Collection<Item> items;
```
Runs original query как subquery:
```sql
SELECT * FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE ...)
```

**Compare:**

| Strategy | Queries | Cartesian | Pagination |
|----------|---------|-----------|------------|
| JOIN FETCH | 1 | Yes (bad for multi-collection) | Broken for collections |
| Batch | ~N/batch_size | No | OK |
| Subselect | 2 | No | OK but replays filter |

**Rule of thumb:**
- Single collection, few parents: JOIN FETCH
- Multiple collections: batch_fetch_size=20 default
- Very large: DTO projection

## Q12. (!) Connection pooling — зачем?

**Connection open** = expensive:
- TCP handshake
- TLS handshake (~100+ ms на WAN)
- DB authentication
- Backend process fork (PostgreSQL — `connection_pid`)

**Без pool:** каждый request opens + closes — **serial bottleneck**, and DB limits total connections.

**Pool:**
- Maintain N open connections
- Request borrows; returns when done
- Queue если все busy

**Benefits:**
- Eliminate connection overhead (ms saved)
- Limit concurrent DB connections (backend stability)
- Faster (connect latency 0)
- Resource control

**Типичные pools:**
- **HikariCP** (Java) — fastest, default Spring Boot
- **pgbouncer** (standalone) — in front of PostgreSQL
- **RDS Proxy** (AWS) — managed
- **Node pg-pool**, **Python psycopg2 pool**

**Architecture decision:**
- **App-side pool:** easy, in-process
- **External pool (pgbouncer, RDS Proxy):** между app и DB; можно pool across multiple apps/instances
- **Both:** app pool → external pool → DB (common for serverless)

**Size:**
- Rule: `pool = cores × 2 + spindles` (old rule)
- Modern: benchmark; too many = context switching hurts
- Typical backend: 10-30 per instance

**Warning:** serverless (Lambda) without pooling = DB connection explosion; always use RDS Proxy.

## Q13. (!) HikariCP settings?

**HikariCP** — default в Spring Boot. Minimal config, fast.

**Key properties (`spring.datasource.hikari.*`):**

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000    # 30s wait for connection
      idle-timeout: 600000         # 10 min evict idle
      max-lifetime: 1800000        # 30 min recycle connections
      keepalive-time: 120000       # 2 min ping idle conn (Hikari 4.0+)
      leak-detection-threshold: 60000  # 60s — warn if not returned
```

**Sizing guidelines:**
- **maximum-pool-size:** start с 10, tune up based on load + DB max_connections
- **minimum-idle:** usually = max (keep warm) for consistent latency
- **Formula:** total connections across all instances ≤ DB `max_connections × 0.8`

**Common issue:**
- App 10 instances × 20 pool = 200 connections
- DB max = 100 → crashes

**Connection timeout:** app doesn't hang waiting forever — fails fast, returns 503.

**max-lifetime:** важно! Prevents stale connections (firewalls kill idle, DB restart).

**leak-detection-threshold:** logs stack trace if connection held > threshold → finds missing `try-with-resources`.

**Metrics:** expose via Micrometer:
```
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.acquire  # time histogram
```

**Alerts:** `pending > 0` sustained → pool undersized.

## Q14. PgBouncer transaction vs session pooling?

**PgBouncer** — lightweight PostgreSQL connection pooler (proxy).

**Modes:**

**Session pooling (default):**
- Client gets DB connection for **entire session** (connect → disconnect)
- Like no pooling (except connection reuse after disconnect)
- Safe for all features (prepared statements, temp tables, listen/notify)
- Low efficiency gain

**Transaction pooling:**
- Connection issued **per transaction**, returned on COMMIT/ROLLBACK
- **Huge efficiency:** 1000 clients can share 20 DB connections
- **Restrictions:**
  - No prepared statements (session-scoped) — unless PG 14+ и PgBouncer 1.22+ protocol-level support
  - No `SET` (session-scoped)
  - No temp tables
  - No `LISTEN/NOTIFY`
  - Cursor limitations

**Statement pooling:**
- Per statement (rare, extreme restrictions)

**Выбор:**
- **Session:** legacy apps, features used
- **Transaction:** modern apps, high throughput, stateless handlers
- **ORMs:** Hibernate by default uses prepared statements — disable или use PG14+

**Spring Boot + PgBouncer transaction mode:**
```yaml
spring:
  datasource:
    hikari:
      # Critical! Disable prepared statements
      data-source-properties:
        prepareThreshold: 0
    # Or use PgBouncer 1.22+ with PG 14+
```

**Architecture:**
```
[App instances × N] → [PgBouncer pool: 500 client conns / 30 DB conns] → [PostgreSQL]
```

## Q15. (!) Partitioning — когда применять?

**Partitioning** — split large table на smaller physical chunks (partitions) by criterion (range, list, hash).

**Когда:**

**1. Very large tables (> 100M rows / > 100GB):**
- Queries scanning recent data → partition on date, query prunes old partitions
- Index fit в memory (smaller per partition)

**2. Time-series data:**
- Logs, events, metrics — range partition by month/week
- Drop old partition = fast (vs DELETE millions rows)

**3. Delete-heavy workload:**
- Dropping partition = instant; DELETE + VACUUM = slow

**4. Tenant isolation:**
- Hash/list partition by tenant_id

**Когда НЕ применять:**
- Small tables (< 10M rows)
- Queries don't benefit (no pruning)
- Add complexity без proportional benefit
- Foreign keys across partitions complicated

**Benefits:**
- Partition pruning (scan only relevant partitions)
- Parallel operations на partitions
- Maintenance per-partition (VACUUM, REINDEX)
- DROP partition for retention

**Costs:**
- Complexity (migrations, constraints)
- Cross-partition queries slower
- Partition key must be в all unique indexes (or workaround)

**PostgreSQL partitioning (10+):**
```sql
CREATE TABLE logs (ts TIMESTAMP, msg TEXT)
PARTITION BY RANGE (ts);

CREATE TABLE logs_2024_01 PARTITION OF logs
  FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

**Automation:** `pg_partman` extension для auto-creation.

## Q16. Range / List / Hash partitioning?

**Range:**
- Values within range go to partition
- Best для: timestamps, sequential IDs
```sql
PARTITION BY RANGE (created_at);
-- partitions: 2024_01, 2024_02, ...
```

**List:**
- Discrete values → partition
- Best для: region, category, status
```sql
PARTITION BY LIST (country);
-- partitions: us, eu, apac, other
```

**Hash:**
- `hash(key) % N` → partition
- Best для: even load distribution (no natural key)
- Can't do range queries efficiently
```sql
PARTITION BY HASH (user_id);
-- 16 partitions
```

**Composite:**
- Range → List sub-partitions: by date, then by region

**Comparison:**

| Type | Pruning | Use Case | Growth |
|------|---------|----------|--------|
| Range | Range queries | Time-series | Add new partition per period |
| List | Equality | Region/tenant | Add per new value |
| Hash | Equality on key | Even distribution | Fixed count, plan ahead |

**Pruning:** planner excludes partitions при query WHERE matches partition key.

**Hash: can't re-partition easily** — choose count carefully (power of 2 for easy doubling later).

## Q17. (!) ANALYZE и статистика оптимизатора?

**Statistics** — summary data о table/column distribution used by planner для estimates:
- Number of rows
- Distinct values per column
- Most common values + frequencies
- Histogram of value distribution
- Null fraction
- Average width

**Хранилище:** `pg_stats` view.

**ANALYZE:**
```sql
ANALYZE orders;        -- update stats
ANALYZE;              -- all tables
```

- Samples rows (default 30000 * `default_statistics_target`)
- Updates `pg_statistic`

**Auto-analyze (autovacuum):**
- Triggered when > `autovacuum_analyze_scale_factor * rows` changed (default 10%)

**When stats stale:**
- Bulk INSERT/UPDATE
- Data distribution shifts
- New week/month (time-series)

**Detect stale stats:**
- `EXPLAIN ANALYZE` shows `rows=X` vs `actual rows=Y` huge mismatch

**default_statistics_target:**
- 100 default (OK most)
- Increase to 1000 для column with complex distribution
- `ALTER TABLE t ALTER COLUMN c SET STATISTICS 1000;`

**Extended statistics (PG 10+):**
```sql
CREATE STATISTICS s_name (dependencies, ndistinct)
  ON col_a, col_b FROM t;
ANALYZE t;
```
Helps planner for **correlated columns** (city + zip).

**Production issue:** после major data change (migration, restore) → `ANALYZE` immediately; planning time drops, queries faster.

## Q18. VACUUM, autovacuum, bloat?

**VACUUM:** reclaim dead tuples from UPDATE/DELETE (MVCC).

**Zachyy:**
- UPDATE = mark old dead + insert new
- Dead tuples waste space
- Prevent transaction ID wraparound (critical!)

**Types:**

**VACUUM (standard):**
- Marks dead space for reuse
- Doesn't release to OS (table size stays)
- Non-blocking (unless VACUUM FULL)

**VACUUM FULL:**
- Rewrites entire table (rebuilds)
- Releases space to OS
- **Blocks writes** — emergency only
- Use `pg_repack` instead (online)

**Autovacuum:**
- Background worker, auto-triggered
- Config: `autovacuum_vacuum_scale_factor` (default 20%)
- Aggressive settings for high-write tables:
```sql
ALTER TABLE t SET (autovacuum_vacuum_scale_factor = 0.05);
```

**Bloat:**
- Dead tuples not vacuumed fast enough
- Or long-running transaction prevents cleanup (`xmin horizon`)
- Detect: `pgstattuple`, `pg_stat_user_tables`

**Long transactions = bloat enemy:**
- Running tx prevents VACUUM of newer deleted rows
- Check: `SELECT * FROM pg_stat_activity WHERE state = 'idle in transaction';`

**TXID wraparound:**
- PostgreSQL tx IDs are 32-bit
- Every row has `xmin`/`xmax`
- Wraparound = data corruption!
- VACUUM "freezes" old rows, prevents
- Ignore = **DB shuts down** at 2B tx

**Monitoring:**
- `pg_stat_user_tables.n_dead_tup`
- `age(relfrozenxid)` per table — warn > 1B

**Tune:** `autovacuum_max_workers=6`, `autovacuum_naptime=10s` for busy DBs.

## Q19. (!) shared_buffers, work_mem, effective_cache_size?

**shared_buffers:** PostgreSQL's block cache (shared memory).
- Default: 128MB (way too small!)
- Recommend: **25% of RAM** (up to 8-16GB — diminishing returns)
- Large values benefit OLAP; OLTP capped by OS cache overlap

**work_mem:** per-operation memory (sort, hash).
- Default: 4MB
- **Per operation, per connection** — careful!
- 10 connections × 3 operations × work_mem = 30× memory
- Recommend: 16-64MB typical; higher for analytical
- Can set per-query: `SET LOCAL work_mem = '256MB';`

**maintenance_work_mem:** CREATE INDEX, VACUUM.
- Default: 64MB
- Increase to 1GB for fast index builds
- Per session, but maintenance sessions rare

**effective_cache_size:** hint to planner about total available cache (OS + shared_buffers).
- Default: 4GB
- Recommend: **50-75% of RAM**
- Doesn't allocate, just influences planner (prefer index scans when effective_cache_size high)

**wal_buffers:** buffer for WAL writes.
- Default: auto (min 1/32 shared_buffers, max 16MB)
- Usually fine

**example (32GB RAM server):**
```
shared_buffers = 8GB
effective_cache_size = 24GB
work_mem = 32MB
maintenance_work_mem = 1GB
```

**Tool:** `pgtune` — config calculator by workload type.

## Q20. WAL и checkpoint tuning?

**WAL (Write-Ahead Log):** all changes logged first → can recover.

**Checkpoint:** flush dirty buffers to data files; WAL older than checkpoint can be recycled.

**Key config:**

**wal_level:** `replica` (default) / `logical` (for logical replication).

**max_wal_size:** target WAL between checkpoints. Default 1GB.
- Larger = fewer checkpoints = better write throughput, but longer recovery
- Busy DB: 8-16GB

**checkpoint_timeout:** max time between checkpoints. Default 5min.
- Larger = better throughput, longer recovery
- 15-30 min для write-heavy

**checkpoint_completion_target:** spread checkpoint I/O over this fraction of interval. Default 0.9.
- Higher = smoother I/O; rarely need to change

**min_wal_size:** keep at least this much recycled. Default 80MB.

**Sync commit:**

**synchronous_commit = on (default):** wait for WAL fsync before ACK. Durable.
**= off:** ACK before fsync — possible to lose few ms on crash, but faster.
**= remote_apply / remote_write:** for replicas.

**For batch loads:** set `synchronous_commit = off` in session → faster, risk acceptable для bulk import.

**Replication:**
- `wal_keep_size` (PG 13+): keep WAL for replicas
- `archive_mode + archive_command`: for PITR
- Replication slots: guarantee WAL kept для subscriber

**Monitor:**
- `pg_stat_bgwriter`: checkpoints, buffers
- High `checkpoints_req` vs `checkpoints_timed` → max_wal_size too small

## Q21. (!) LIMIT + OFFSET проблема pagination?

**Problem:**
```sql
SELECT * FROM orders ORDER BY created_at DESC LIMIT 20 OFFSET 10000;
```
- Must scan 10020 rows, throw 10000, return 20
- **Each page further = slower**
- Page 500 (offset 10000) → 100x slower than page 1

**Solution: keyset pagination (seek method):**

```sql
-- Page 1
SELECT * FROM orders
ORDER BY created_at DESC, id DESC
LIMIT 20;
-- Last row returns (created_at='2024-01-15 10:00', id=1234)

-- Page 2 (using last row as bookmark)
SELECT * FROM orders
WHERE (created_at, id) < ('2024-01-15 10:00', 1234)
ORDER BY created_at DESC, id DESC
LIMIT 20;
```

**Benefits:**
- Constant time **each page** (index seek)
- No "row moved" inconsistency (OFFSET skips data if rows added)

**Index needed:** `(created_at DESC, id DESC)`.

**Caveat:**
- Can't jump to arbitrary page (only next/prev)
- UX often OK — infinite scroll, "load more"

**Alternative — Count rows once, then OFFSET:**
- Total count: `SELECT COUNT(*)` (slow on big tables)
- Use approximate count: `pg_class.reltuples`

**Best practice:**
- Infinite scroll / "next" → keyset
- "Jump to page 500" → accept slowness или rethink UX

## Q22. JOIN vs subquery vs EXISTS?

**Semantic equivalent examples:**

**JOIN:**
```sql
SELECT o.* FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.country = 'US';
```

**IN subquery:**
```sql
SELECT * FROM orders
WHERE user_id IN (SELECT id FROM users WHERE country = 'US');
```

**EXISTS:**
```sql
SELECT * FROM orders o
WHERE EXISTS (SELECT 1 FROM users u WHERE u.id = o.user_id AND u.country = 'US');
```

**Modern PostgreSQL planner:**
- Usually rewrites все three to equivalent plan
- Minor differences в edge cases

**Differences historically:**
- **JOIN** with `SELECT *` from users → may duplicate orders if multiple users match (rare)
- **EXISTS** — returns once per outer row regardless inner count; often optimal для semi-join
- **IN** — deprecated for NULL semantics (NULL in list → UNKNOWN, tricky)

**Performance tips:**
- `NOT IN` с subquery returning NULL breaks (always false) — use `NOT EXISTS` or `LEFT JOIN ... WHERE ... IS NULL`
- `EXISTS` often faster когда inner table large (short-circuits)
- `JOIN` better if need columns from both

**EXPLAIN ANALYZE — check plan:**
- Often planner converts `IN` → `Semi Hash Join` = ~= `EXISTS`
- Use whichever reads clearest; measure.

## Q23. Window functions performance?

**Window functions** — compute per-row using "window" of rows (not aggregate that collapses).

**Examples:**
```sql
SELECT user_id, amount,
  ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn,
  SUM(amount) OVER (PARTITION BY user_id) AS total
FROM orders;
```

**Performance:**

- **Sort needed** unless index matches `PARTITION BY ... ORDER BY`
- Index `(user_id, created_at)` → avoids sort для above

**Cost:**
- Typically requires **sorting** of entire dataset
- Work_mem spill = disk → slow
- Better than correlated subquery equivalent almost always

**Frame clause:**
```sql
SUM(amount) OVER (ORDER BY dt ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)
```
- Default: `RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW` — cumulative
- Custom frames can be expensive

**Tips:**
- Index support для ORDER BY
- Avoid running window function на huge aggregate (materialize intermediate)
- `DISTINCT ON` PostgreSQL-specific — often faster alternative для "first per group":
```sql
SELECT DISTINCT ON (user_id) *
FROM orders
ORDER BY user_id, created_at DESC;
```

**Materialized CTE (PG 12+):**
```sql
WITH aggregated AS MATERIALIZED (...)
SELECT ... FROM aggregated;
```

## Q24. Materialized views vs views?

**View:** stored query; executed every time.
```sql
CREATE VIEW user_summary AS
SELECT user_id, SUM(amount) AS total FROM orders GROUP BY user_id;
```
- Pro: always current
- Con: reruns query each access → slow для complex aggregates

**Materialized view:** **query result stored** like table.
```sql
CREATE MATERIALIZED VIEW user_summary_mv AS
SELECT user_id, SUM(amount) AS total FROM orders GROUP BY user_id;

CREATE UNIQUE INDEX ON user_summary_mv (user_id);
```
- Pro: fast read (just SELECT from table)
- Con: **stale** until REFRESH

**REFRESH:**
```sql
REFRESH MATERIALIZED VIEW user_summary_mv;  -- locks reads
REFRESH MATERIALIZED VIEW CONCURRENTLY user_summary_mv;  -- needs unique index
```

**Incremental refresh:** not in PostgreSQL core; extensions (`pg_ivm`) или logic-level solutions (CDC → update).

**Use cases:**
- Dashboards (nightly refresh)
- Reporting (complex joins)
- Denormalization for read path
- Search indexes

**Alternatives:**
- **Cache** (Redis): app-level; similar trade-off
- **Read replica**: query replica с slightly stale data

**Watch:** refresh time can become bottleneck — if view takes 10 min, how often refresh? May need partition-based incremental rebuild.

## Q25. (!) Read replicas — когда и как?

**Read replica:** copy of DB following primary's WAL, servicing read queries.

**Why:**
- Scale read throughput (most apps 80%+ reads)
- Isolate heavy analytics from OLTP
- HA failover (some configs)

**PostgreSQL:**
- **Streaming replication** (physical) — byte-for-byte copy; read-only
- **Logical replication** — row-level changes; select tables; writeable (но careful)

**Lag:**
- Typical: ms-seconds
- Bulk operations на primary spike lag
- Monitor: `pg_replication_slots`, `replay_lsn` delta

**Read-your-writes problem:**
- User updates record, immediately reads
- May hit replica → stale data
- Solutions:
  - Route writes+immediate reads → primary
  - Session sticky to primary for X seconds post-write
  - Read from primary during "session after write"

**Architecture patterns:**
- App routes queries: `@Transactional(readOnly=true)` → replica; else → primary
- Spring `AbstractRoutingDataSource` for dynamic routing
- **Proxy-based:** ProxySQL, pgpool, RDS Proxy

**Trade-offs:**
- Eventual consistency (lag)
- Failover complexity
- Connection doubles (app connects к both)

**Consistency modes:**
- Async replication (default, fast, may lose data on crash)
- Sync replication (`synchronous_standby_names`) — slower writes, zero data loss

**Cloud:** RDS/Aurora make replicas trivial; Aurora — shared storage, minimal lag.

## Q26. (!) Как находить slow queries в prod?

**PostgreSQL:**

**pg_stat_statements (must-have extension):**
```sql
CREATE EXTENSION pg_stat_statements;

SELECT query,
       calls,
       total_exec_time / 1000 AS total_sec,
       mean_exec_time AS mean_ms,
       rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;
```
- Tracks by normalized query (params replaced by `$1`)
- Total time, mean, calls
- **Start here always** — top 10 usually explain 80% load

**Slow query log:**
```sql
log_min_duration_statement = 1000  -- log queries > 1s
```
- Logs to PostgreSQL log file
- Grep / ship to ELK / Loki

**Active queries right now:**
```sql
SELECT pid, now() - query_start AS duration, state, query
FROM pg_stat_activity
WHERE state != 'idle' AND now() - query_start > interval '5 seconds'
ORDER BY duration DESC;
```
Use для "what's currently slow".

**APM (DataDog, New Relic):**
- Auto-captures SQL from traces
- Tags with service/endpoint
- P95/P99 latencies per query

**auto_explain:**
```sql
LOAD 'auto_explain';
SET auto_explain.log_min_duration = 1000;
SET auto_explain.log_analyze = true;  -- adds runtime overhead!
```
Logs full EXPLAIN for slow queries → rich diagnosis.

**pganalyze / pgwatch2:** dashboards on top of pg_stat_statements.

**Workflow:**
1. pg_stat_statements → top-N
2. Pick highest-impact query (total_exec_time)
3. Get example args from log
4. EXPLAIN ANALYZE in test
5. Fix (index, rewrite, config)
6. Deploy, measure

## Q27. Database load test (pgbench, sysbench)?

**pgbench** (PostgreSQL bundled):
- Built-in TPC-B-like benchmark
- Custom scripts
```bash
pgbench -i -s 100 testdb   # init scale 100 (~1.5GB)
pgbench -c 50 -j 4 -T 60 testdb  # 50 clients, 4 threads, 60s
```

Output: TPS (transactions/sec), latency.

**Custom script:**
```sql
-- my_bench.sql
\set uid random(1, 10000)
SELECT * FROM users WHERE id = :uid;
```
```bash
pgbench -c 50 -T 60 -f my_bench.sql testdb
```

**sysbench:**
- Multi-DB (MySQL, PostgreSQL)
- Standard OLTP workloads
```bash
sysbench --db-driver=pgsql oltp_read_write prepare
sysbench --db-driver=pgsql oltp_read_write run --threads=64 --time=60
```

**Targeted scenarios:**
- Write-heavy (INSERT workload)
- Read-heavy (SELECT workload)
- Mixed (70/30)
- Latency-sensitive (P99)

**Best practices:**
- Prod-like data volume (scale factor ~ prod size)
- Prod-like config (shared_buffers, work_mem)
- Prod-like network (can bottleneck тест)
- Warmup: few minutes burn-in (fill caches)
- Measure: TPS, mean, P50, P95, P99 latency
- Iterate: change one parameter, rerun

**Tools beyond:**
- **HammerDB** — OLTP/TPC-C, TPC-H
- **jmeter** / **k6** — application-level (end-to-end, not pure DB)
- **pg_bench_tools** (community scripts)

**Capacity planning:** extrapolate "at 5k TPS load, p99 = 50ms" — compare against SLO.

---

## See also

- [JVM Performance Tuning](jvm-performance-tuning-interview.md) — application side
- [Application Profiling](application-profiling-interview.md) — JFR, async-profiler
- [Caching Strategies](../architecture/caching-strategies-interview.md) — reduce DB load
- [PostgreSQL](../databases/postgresql-interview.md) — deeper DB-specific questions
- [Hibernate](../databases/hibernate-interview.md) — ORM performance patterns (N+1)
- [Database Architecture](../databases/database-architecture-interview.md) — replication, sharding
- [Caching Performance](caching-performance-interview.md) — cache tuning
- [Performance Testing](performance-testing-interview.md) — methodology, tools
- [Memory Management](memory-management-interview.md) — JVM ↔ DB interplay
- [Consistency Patterns](../architecture/consistency-patterns-interview.md) — read replicas trade-offs

- [[application-profiling-interview|Application Profiling]]
- [[caching-performance-interview|Caching Performance]]
- [[jvm-performance-tuning-interview|JVM Performance Tuning]]
- [[memory-management-interview|Memory Management]]
- [[network-performance-interview|Network Performance]]
- [[performance-testing-interview|Performance Testing]]
