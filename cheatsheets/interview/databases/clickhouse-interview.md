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


> [!mcq]
> - [x] CH — open-source columnar OLAP DB от Yandex (open-sourced 2016), сильна на append-heavy analytics, миллиарды rows/sec scan, 5-10x compression | Создана для Yandex.Metrica, оптимизирована под scan-heavy workloads. ✓ ПРИМЕНЯТЬ: observability-стек Uber на CH вместо ELK сэкономил 5x storage. 📋 ПРАВИЛО: «CH = columnar OLAP, append-heavy, scan-fast». 🔗 См. Q2.
> - [ ] ClickHouse — это NoSQL document store со схемой на чтение, как MongoDB | CH — **schema-on-write columnar SQL OLAP**: типы колонок жёсткие, JSON/dynamic-поля ограничены. ❌ ПОСЛЕДСТВИЕ: команда хранит сырой event JSON без типизации, queries со `JSONExtract()` на каждой row → CPU 100%, latency 30s.
> - [ ] ClickHouse — fork PostgreSQL с columnar storage от Yandex | Это **independent C++ codebase** с нуля, не fork Postgres; SQL диалект свой (не ANSI). ❌ ПОСЛЕДСТВИЕ: команда ожидает MVCC/CTE/RETURNING как в Postgres → переписывает миграцию когда обнаруживает что `UPDATE` это async mutation.
> - [ ] ClickHouse Cloud — это open-source self-hosted edition с теми же fичами что и community | **ClickHouse Cloud** — managed paid SaaS от ClickHouse Inc., open-source бинарь — отдельный community-edition. ❌ ПОСЛЕДСТВИЕ: команда планирует «бесплатно как Snowflake» через Cloud, в первый месяц получает счёт сравнимый с DWH.

> [!mcq]
> - [ ] ClickHouse — row-store с B-tree индексами, оптимизированный под OLTP point lookups | CH — **column store** с sparse index и vectorized execution, OLTP сценарии (point lookup, UPDATE) — слабое место. ❌ ПОСЛЕДСТВИЕ: команда ставит CH под checkout-сервис, point query по `WHERE id=X` читает 8192-row granule, latency 100ms+, RPS падает.
> - [x] CH — column store с vectorized query execution: данные одной колонки лежат подряд, batch обрабатывается SIMD-инструкциями | Cache locality + SIMD дают 10-100x ускорение на analytical scans vs row-store. ✓ ПРИМЕНЯТЬ: Yandex.Metrica сканирует миллиарды rows/sec на dashboard'ах за счёт columnar+vectorized пайплайна. 📋 ПРАВИЛО: «column store + vectorized = OLAP scan speed». 🔗 См. Q2.
> - [ ] Vectorized execution в CH означает GPU-ускорение всех запросов через CUDA | Vectorized — это **CPU SIMD** (AVX2/AVX-512) на batches по тысячам rows, никакого GPU. ❌ ПОСЛЕДСТВИЕ: команда покупает GPU-ноды «для CH», в реальности упирается в CPU/RAM/IO, бюджет потрачен впустую.
> - [ ] Columnar storage хорош и для OLTP point queries, и для wide UPDATE'ов | Columnar выигрывает на scan малого числа колонок из миллиардов rows; для point lookup и UPDATE строки — antipattern (нужно читать/переписывать все колонки). ❌ ПОСЛЕДСТВИЕ: миграция Postgres-OLTP на CH «для скорости» ломает per-row UPDATE сценарий, mutations лагают часами.

## Q2. (!) Почему ClickHouse такой быстрый?

**Ключевые оптимизации:**


> [!mcq]
> - [ ] CH быстрый только потому, что использует in-memory storage и держит всё в RAM | CH использует **disk-based** хранение с агрессивной компрессией и async I/O, RAM — только для индекса/cache, не для данных. ❌ ПОСЛЕДСТВИЕ: команда сайзит ноды по объёму данных = объём RAM, нагрузка на бюджет 10x, при этом disk SSD достаточно.
> - [ ] CH быстрее Postgres исключительно из-за отсутствия ACID и MVCC | Скорость идёт от **columnar layout + vectorized execution + sparse index**, не от отсутствия транзакций (которые в обоих БД ортогональны scan-perf). ❌ ПОСЛЕДСТВИЕ: команда «отключает MVCC в Postgres ради скорости» — Postgres всё равно медленный на scan, потому что row-store.
> - [x] Скорость = columnar + vectorized SIMD batches + sparse index + data skipping (minmax/bloom) + async parallel I/O + JIT | Каждый компонент даёт фактор 2-10x, в сумме 10-100x vs Postgres на analytical scans. ✓ ПРИМЕНЯТЬ: миграция analytical workload Postgres→CH типично даёт 50x speedup на full-scan dashboards. 📋 ПРАВИЛО: «скорость = stack оптимизаций, не один трюк». 🔗 См. Q1.
> - [ ] CH быстрее за счёт хранения только агрегатов через materialized views, raw data не хранится | MV — опциональная оптимизация поверх raw MergeTree; CH быстрый и без MV за счёт columnar+vectorized. ❌ ПОСЛЕДСТВИЕ: команда строит 50 MV «для скорости» вместо одной правильной ORDER BY → write amplification x50, INSERT throughput падает.

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

> [!mcq]
> - [x] `uniqHLL12`/`quantileTDigest` — approximate aggregates: compact sketch вместо всех значений, константная память | HyperLogLog/t-digest parallelizable, ~2% error. ✓ ПРИМЕНЯТЬ: dashboard «уникальные visitors/день» на 1B событий через `uniqHLL12` — секунды вместо минут. 📋 ПРАВИЛО: «approximate для dashboards, exact для billing». 🔗 См. Q13.
> - [ ] Async inserts (`async_insert=1`) ускоряют CH тем, что коммитят данные в RAM без записи на диск | Async insert буферизует поступающие batches на сервере и **флашит на диск** через `async_insert_busy_timeout_ms`/`async_insert_max_data_size`; durability сохраняется (`wait_for_async_insert=1` по умолчанию), но данные могут потеряться при crash до flush. ❌ ПОСЛЕДСТВИЕ: команда включает async insert и `wait_for_async_insert=0` ради latency, при kill -9 теряет 200мс трафика — billing-события исчезают, реконсиляция вручную часами.
> - [ ] Approximate aggregates типа `uniqHLL12` дают точно тот же результат что `uniqExact`, но быстрее | HLL — **probabilistic**, ~2% error tolerance; `uniqExact` точный но медленный и memory-hungry. ❌ ПОСЛЕДСТВИЕ: финансовый отчёт по unique users считается через `uniqHLL12`, расхождение 2% = миллионы рублей в выручке, аудит провален.
> - [ ] JIT compilation запросов в CH работает только в ClickHouse Cloud, не в open-source | JIT (compile_expressions) — часть open-source ядра с 21.x, доступна везде. ❌ ПОСЛЕДСТВИЕ: команда платит за Cloud «ради JIT», на самом деле та же фича работает в self-hosted бесплатно.

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

> [!mcq]
> - [ ] CH self-hosted и Snowflake тарифицируются одинаково — за объём хранимых данных | Разные модели: CH self-hosted **бесплатен** (платишь только за железо), Snowflake — credits за compute-time + storage, BigQuery — per-query bytes scanned. ❌ ПОСЛЕДСТВИЕ: финплан «как Snowflake, но self-host» — недооценка ops costs или переплата на BigQuery при ad-hoc heavy scan'ах.
> - [x] CH self-hosted = только cost железа; Snowflake = warehouse credits per second; BigQuery = per-query bytes scanned (serverless) | Три фундаментально разные pricing model'и определяют, что выгоднее под конкретный workload. ✓ ПРИМЕНЯТЬ: при стабильно высоком QPS analytics-нагрузки CH self-hosted в 5-10x дешевле; при редких ad-hoc — BigQuery serverless экономит idle cost. 📋 ПРАВИЛО: «steady high-load → CH; spiky ad-hoc → BigQuery». 🔗 См. Q26.
> - [ ] BigQuery дешевле всегда, потому что serverless — нет idle cost | Serverless экономит на простое, но per-query model штрафует «full scan» паттерны: один SELECT * на 10TB стоит десятки долларов. ❌ ПОСЛЕДСТВИЕ: BI-аналитик делает SELECT * для исследования, счёт за месяц улетает в $50k, FinOps бьёт тревогу.
> - [ ] CH одинаково хорошо тянет тысячи concurrent queries как Snowflake/BigQuery | CH рассчитан на **сотни** concurrent users (по умолчанию `max_concurrent_queries=100`); Snowflake/BigQuery scale до тысяч через elastic warehouses/serverless slots. ❌ ПОСЛЕДСТВИЕ: BI-tool на 2000 аналитиков идёт через CH, queue растёт, p99 timeout, дашборды красные — миграция назад на Snowflake через квартал.

## Q4. ClickHouse vs PostgreSQL для аналитики?

> [!mcq]
> - [ ] Postgres с расширением `cstore_fdw` или `citus columnar` — полноценная замена CH для аналитики | Citus columnar решает часть проблем, но без vectorized execution и sparse-index уступает CH в 10-50x на больших scan'ах. ❌ ПОСЛЕДСТВИЕ: команда «остаётся на Postgres+columnar» вместо CH, dashboard p95 = 30s vs 1s в CH, BI-юзеры жалуются.
> - [ ] CH полностью заменяет Postgres в современном стеке, OLTP больше не нужен | CH — **OLAP only**: нет нормальных транзакций, JOIN'ы слабые, UPDATE/DELETE async через mutations. ❌ ПОСЛЕДСТВИЕ: миграция чек-аут/payment сервиса на CH ломает идемпотентность UPDATE, race conditions на остатках товаров.
> - [ ] Главное отличие — Postgres платный enterprise, CH бесплатный open-source | Оба open-source с похожими лицензиями (PostgreSQL License vs Apache 2.0); ClickHouse Cloud — отдельный paid SaaS. ❌ ПОСЛЕДСТВИЕ: финансовая команда мотивирует переезд «экономией на лицензии Postgres», которой не существует.
> - [x] CH — для **append-heavy analytics** (logs, metrics, events), Postgres — для **OLTP transactions**; типичный паттерн: Postgres+CDC→CH | Разные движки под разные нагрузки, не «или-или». ✓ ПРИМЕНЯТЬ: e-commerce — orders/users в Postgres, events/clickstream в CH; Debezium стримит CDC изменений. 📋 ПРАВИЛО: «Postgres = source of truth, CH = analytical replica». 🔗 См. Q26.

| Критерий | ClickHouse | PostgreSQL |
|----------|-----------|------------|
| Storage | Columnar | Row-based |
| Analytics speed | **Very fast** | Slow на больших scans |
| OLTP (point lookups) | Slow | Fast |
| Updates / deletes | Limited | Full ACID |
| Joins | Limited (broadcast только small tables) | Full |
| Schema flexibility | Limited | Full |


> [!mcq]
> - [ ] CDC из Postgres в CH делается стандартным `pg_dump` каждые 5 минут | Cron `pg_dump` не даёт near-realtime и не масштабируется на гигабайтные таблицы. ❌ ПОСЛЕДСТВИЕ: dump 100GB БД 30 минут, lag дашбордов 30+ минут, alerting на freshness постоянно красный.
> - [x] Pipeline: Postgres → Debezium (WAL replication slot) → Kafka → CH Kafka engine + MV в MergeTree | Near-realtime CDC, Kafka буферизует. ✓ ПРИМЕНЯТЬ: GitLab analytics — Postgres OLTP стримится в CH через Debezium для product-аналитики. 📋 ПРАВИЛО: «Debezium+Kafka = prod-grade CDC PG→CH». 🔗 См. Q24.
> - [ ] CH сам через `PostgreSQL` engine автоматически синхронизирует изменения из Postgres | PG engine — federated **read-through**, не CDC; для CDC нужен `MaterializedPostgreSQL` (experimental). ❌ ПОСЛЕДСТВИЕ: PG engine как «CDC» → каждый дашборд-SELECT шлёт запросы в Postgres, OLTP встаёт под locks.
> - [ ] При CDC из Postgres в CH достаточно `INSERT INTO ch_table SELECT * FROM postgres_table` раз в день | Snapshot full-refresh для миллиардных таблиц требует перетягивать всё каждый раз — гигабайты bandwidth впустую. ❌ ПОСЛЕДСТВИЕ: nightly job 6 часов копирует 500GB, окно для analytics-обновления съедено, дашборды показывают вчерашние данные.

## Q5. (!) Что такое MergeTree?

**MergeTree** — main storage engine ClickHouse. Лежит в основе всех ClickHouse tables.

> [!mcq]
> - [ ] MergeTree — это B-tree с merge-операциями раз в час, как в InnoDB | MergeTree — **LSM-tree-подобный**: parts immutable, background merges объединяют их в большие, никаких B-tree. ❌ ПОСЛЕДСТВИЕ: команда тюнит buffer pool/page size как в InnoDB, эффекта нет, реальные параметры — `max_parts_in_total`, `merge_max_size`.
> - [ ] MergeTree гарантирует strong consistency на уровне строки сразу после INSERT | Read-after-write на single replica работает, но мульти-репликация и dedup (ReplacingMergeTree) — eventually consistent после merges. ❌ ПОСЛЕДСТВИЕ: команда ждёт что после INSERT с дублирующим ключом сразу будет одна row, читает старое значение часами, метрики «прыгают».
> - [ ] MergeTree подходит как универсальная замена любому row-store движку, включая OLTP | MergeTree — **append-optimized OLAP engine**: нет per-row UPDATE, JOIN'ы слабые, нет foreign keys. ❌ ПОСЛЕДСТВИЕ: команда выбирает MergeTree под cart-сервис вместо Postgres, обнаруживает что `ALTER TABLE...UPDATE` идёт часами.
> - [x] MergeTree = LSM-like: каждый INSERT создаёт immutable part, background-thread сливает parts в larger; в части — данные отсортированы по ORDER BY | Append-friendly архитектура для high-throughput ingestion + быстрых scan'ов. ✓ ПРИМЕНЯТЬ: Yandex.Metrica — миллионы INSERT/sec через batched parts с background merge'ами. 📋 ПРАВИЛО: «MergeTree = parts + background merge + sorted within part». 🔗 См. Q9.

**Принцип:**
- Data разбита на **parts** (immutable chunks)
- Background **merge** parts в larger ones (LSM-tree-like)
- Within part: data sorted by **ORDER BY key**
- Sparse primary index в memory

> [!mcq]
> - [ ] `PARTITION BY toYYYYMM(event_time) ORDER BY (event_time, user_id)` — partition и ORDER BY одно и то же, дубликат настройки | PARTITION BY = **физическая нарезка** по месяцам (директории на диске); ORDER BY = **сортировка внутри** part'а. Разные функции, обе нужны. ❌ ПОСЛЕДСТВИЕ: команда удаляет ORDER BY «дубль PARTITION BY», теряет sparse index, range scan'ы становятся full-scan.
> - [ ] `ORDER BY (event_time, user_id)` означает что indexes создаются на обе колонки независимо | ORDER BY — **composite tuple**, prefix-index: эффективен для `WHERE event_time = ...` или `WHERE event_time = ... AND user_id = ...`, но **не** для `WHERE user_id = ...` без event_time. ❌ ПОСЛЕДСТВИЕ: команда ожидает что фильтр по user_id будет fast, на деле full scan, latency 30s+.
> - [x] `PARTITION BY toYYYYMM(event_time)` режет данные по месяцам, `ORDER BY (event_time, user_id)` сортирует внутри parts по composite tuple | Партиции для pruning по месяцу + sort key для range scan'ов внутри партиции. ✓ ПРИМЕНЯТЬ: time-series events — query «april 2025 для user_id=42» сначала pruning до 1 партиции, потом sparse index по time+user. 📋 ПРАВИЛО: «PARTITION BY = where to skip, ORDER BY = how to find». 🔗 См. Q9, Q11.
> - [ ] `event_time DateTime` не нужно компрессировать дополнительно — MergeTree сам подбирает codec | Default compression — общий **LZ4**; для DateTime/timestamps optimal — `CODEC(DoubleDelta, ZSTD)`, экономит 5-20x места. ❌ ПОСЛЕДСТВИЕ: 10TB time-series без codec'а вместо 1TB → лишний disk, дольше I/O, выше cost; явная настройка codec обязательна на больших таблицах.

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

> [!mcq]
> - [ ] `ReplacingMergeTree` мгновенно заменяет старую row при INSERT | Дедупликация **eventual**: происходит при background merge'ах, до этого видны обе версии — нужен `FINAL` или `OPTIMIZE` для немедленного результата. ❌ ПОСЛЕДСТВИЕ: команда мигрирует с upsert-логики Postgres, в первые часы после INSERT дубли видны в дашборде, аналитики паникуют.
> - [ ] `ReplacingMergeTree` без указания version-колонки выбирает row с максимальным timestamp вставки автоматически | Без version выбирается **последний по факту merge order** — недетерминированно при concurrent INSERT'ах. ❌ ПОСЛЕДСТВИЕ: команда полагается на «последний INSERT wins» без version, при rerun ETL получает разные победившие строки, отчёты не воспроизводятся.
> - [ ] `ReplacingMergeTree` подходит для частых UPDATE с TPS 10k+ | Каждый «UPDATE» = новый INSERT, parts взрываются (`Too many parts`); подходит для **slow-changing dimensions**, не для high-TPS UPDATE-нагрузки. ❌ ПОСЛЕДСТВИЕ: try mirror Postgres UPDATE-pattern в ReplacingMergeTree → 10k INSERT/sec → too_many_parts через час, ingestion freeze.
> - [x] `ReplacingMergeTree(updated_at)` оставляет row с **максимальным** значением version-колонки при merge; до merge — все версии видны | Idempotent upsert pattern в OLAP контексте без полноценного UPDATE. ✓ ПРИМЕНЯТЬ: user-profile dimension table — каждый change стримится как INSERT, при merge остаётся latest. 📋 ПРАВИЛО: «ReplacingMergeTree = idempotent upsert, читай через FINAL или GROUP BY». 🔗 См. Q5.

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

> [!mcq]
> - [ ] `SummingMergeTree` суммирует **все** numeric колонки по primary key, даже метаданные типа user_id | По умолчанию суммируются все non-key numeric, можно указать список явно: `SummingMergeTree((visits, clicks))`. ❌ ПОСЛЕДСТВИЕ: команда добавляет numeric `user_age` без указания списка → suммируется в мусор: `user_age = 25 + 25 = 50` после merge.
> - [ ] `AggregatingMergeTree` хранит сырые rows и агрегирует только при SELECT | AMT хранит **partial aggregates** через `AggregateFunction(sum, ...)` / `AggregateFunction(uniq, ...)`, при merge сворачивает в один state. ❌ ПОСЛЕДСТВИЕ: команда ожидает что `SELECT raw_value FROM amt_table` вернёт исходник — получает binary blob `AggregateFunction state`, парсер падает.
> - [ ] `SummingMergeTree` гарантирует что после INSERT в SELECT мгновенно одна row на ключ | Сворачивание происходит **только при merge**, до этого `SELECT` видит несколько rows; нужен `GROUP BY` или `OPTIMIZE FINAL` для гарантированного результата. ❌ ПОСЛЕДСТВИЕ: дашборд после ingestion показывает суммы x2-x3 (несвёрнутые duplicates), recovery — ждать background merge или `OPTIMIZE`.
> - [x] `SummingMergeTree` для pre-sum по фиксированному набору колонок (visits, clicks); `AggregatingMergeTree` для arbitrary aggregations через `AggregateFunction` (sum, uniq, quantile) | SMT — узкий case (только сумма), AMT — универсальный (любые aggregate states). ✓ ПРИМЕНЯТЬ: hourly stats — SMT для count/sum, AMT для unique users (`uniqState`) + p99 latency (`quantileState`). 📋 ПРАВИЛО: «SMT = только сумма; AMT = любая агрегация». 🔗 См. Q17.

## Q7. CollapsingMergeTree?

**CollapsingMergeTree** — для cancelling out rows (sign +1 / -1).

```sql
CREATE TABLE events (
    user_id UInt64,
    sign Int8,
    activity_count UInt64
) ENGINE = CollapsingMergeTree(sign)
ORDER BY user_id;

> [!mcq]
> - [ ] `CollapsingMergeTree` автоматически отслеживает changes и сам генерирует sign=-1 для отмены | Приложение **обязано само** вставлять старую версию с sign=-1 перед новой sign=+1. CH ничего не отслеживает. ❌ ПОСЛЕДСТВИЕ: команда забывает отправлять cancel-rows, отменённые состояния не схлопываются, sum() возвращает удвоенные значения, метрики «прыгают».
> - [ ] Чтение `SELECT count() FROM events` в CollapsingMergeTree гарантирует точный результат после INSERT | До merge видны и +1 и -1 строки → `count()` завышен; нужен `SELECT sum(sign)` или `SELECT ... GROUP BY ... HAVING sum(sign) > 0`. ❌ ПОСЛЕДСТВИЕ: дашборд через `count()` показывает x2 от реальности, FINAL даёт правильный результат но full scan.
> - [ ] `VersionedCollapsingMergeTree` идентичен CollapsingMergeTree, лишний синоним | VCMT добавляет колонку `version` для **out-of-order INSERT'ов**: если cancel приходит раньше original — VCMT всё равно правильно схлопнёт по version. CMT в этом случае ломается. ❌ ПОСЛЕДСТВИЕ: при retry/replay из Kafka cancel приходит до original → CMT оставляет «висящие» строки, метрики неверны навсегда.
> - [x] CollapsingMergeTree схлопывает rows с противоположными `sign` (+1/-1) и одинаковым ORDER BY при merge — упрощает «отмену» row без UPDATE | Pattern для tracking mutable state в immutable storage: cancel old + insert new = update. ✓ ПРИМЕНЯТЬ: Yandex.Metrica session tracking — отмена старого session + новая запись с обновлённым durationom. 📋 ПРАВИЛО: «Collapsing = отмена через sign=-1, не UPDATE». 🔗 См. Q6.

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

> [!mcq]
> - [x] `ReplicatedMergeTree('/clickhouse/tables/{shard}/events','{replica}')` — multi-master eventually-consistent replication через ZooKeeper, обязательна для prod HA | Любая replica принимает writes, ZK хранит metadata. ✓ ПРИМЕНЯТЬ: prod = ReplicatedMergeTree, минимум 2 replicas; macro `{shard}`/`{replica}` из config. 📋 ПРАВИЛО: «prod = Replicated*, raw MergeTree = dev only». 🔗 См. Q20.
> - [ ] `ReplicatedMergeTree` использует master-slave: только одна реплика принимает writes, остальные read-only | RMT — **multi-master**: любая реплика принимает writes, ZooKeeper координирует очередь и порядок merge'ей. ❌ ПОСЛЕДСТВИЕ: команда настраивает HAProxy «только на master», потеря node = downtime, хотя любая replica могла принять INSERT.
> - [ ] Replication в RMT — synchronous: INSERT возвращает OK только после реплики на все replicas | RMT replication **eventually consistent**, async; `insert_quorum=2` опционально включает quorum-write. ❌ ПОСЛЕДСТВИЕ: команда ожидает strong consistency сразу после INSERT, читает с другой реплики и видит stale data, метрики расходятся между BI-инстансами.
> - [ ] ReplicatedMergeTree пишет каждую row дважды — в local part и в ZooKeeper, отсюда performance hit | ZK хранит **только metadata** (parts list, queue, leader info), сами данные на disk нод CH; нагрузка на ZK ≪ нагрузка на CH. ❌ ПОСЛЕДСТВИЕ: команда «отказывается от replication ради скорости», теряет HA, при disk failure теряют данные.

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

> [!mcq]
> - [x] Партиции дают `ALTER TABLE DROP/DETACH PARTITION` для быстрой очистки старых данных + partition pruning по WHERE | Декларативный retention + быстрое удаление через metadata-only operation. ✓ ПРИМЕНЯТЬ: nightly job делает `ALTER TABLE events DROP PARTITION '202401'` для ретенции 90 дней — секунды вместо часов mutation. 📋 ПРАВИЛО: «retention через DROP PARTITION, не DELETE». 🔗 См. Q22.
> - [ ] `DROP PARTITION '202504'` идентичен `DELETE FROM events WHERE month=4` по скорости и стоимости | `DROP PARTITION` — мгновенная **metadata operation**: удаляется ссылка на каталог parts; `DELETE` запускает mutation, переписывает все parts. ❌ ПОСЛЕДСТВИЕ: команда чистит старые месяцы через `ALTER...DELETE`, mutations queue растёт, replication лагает на часы вместо секунд.
> - [ ] Partition key обязан совпадать с первой колонкой в ORDER BY | Partition и ORDER BY **независимы**: PARTITION BY toYYYYMM(date) + ORDER BY (user_id, event_time) — валидно. ❌ ПОСЛЕДСТВИЕ: команда ставит ORDER BY (date, ...) «потому что partition by date», теряет sparse index по user_id для 90% запросов.
> - [ ] Партиции не нужны если уже есть `TTL DELETE WHERE date < now()-30d` | TTL DELETE — это всё равно mutation; правильный pattern — `TTL ... DELETE` **по partition key** (например `TTL date + INTERVAL 30 DAY DELETE`), что превращается в drop partition. ❌ ПОСЛЕДСТВИЕ: TTL без partition key переписывает parts построчно, write amplification 5x на retention.

**Эффект:**
- **Partition pruning** — query "WHERE event_time = '2025-04-19'" reads только 1 partition
- **Independent operations** — drop / detach / optimize per partition
- **Better query parallelism**

**Best practice:**
- Don't over-partition (≤ ~1000 partitions per table)
- Common: by month / week (для time-series)
- Don't partition by high-cardinality column

> [!mcq]
> - [ ] Партишены влияют только на хранение, query engine всё равно сканирует все parts | Partition pruning — ключевая оптимизация: `WHERE event_time = '2025-04-19'` читает **только 1 partition**, остальные skip'аются на этапе планирования. ❌ ПОСЛЕДСТВИЕ: команда не использует фильтр по partition key в дашбордах, scan 24 месяцев данных вместо одного, latency 30s+.
> - [x] `PARTITION BY toYYYYMM(date)` + фильтр по date в WHERE = partition pruning, читается только нужный месяц | Партиции в CH — physical separation parts, planner skip'ает целые партиции по WHERE. Лимит ~1000 parts через `max_partitions_per_insert_block` (default 1000). ✓ ПРИМЕНЯТЬ: Cloudflare логи partition by day, query «вчера» читает 1/365 данных. 📋 ПРАВИЛО: «PARTITION BY = pruning, не storage layout». 🔗 См. Q5.
> - [ ] Можно партиционировать по `user_id` или `request_id` для распараллеливания запросов | Partition by high-cardinality колонке (UUID, user_id) ломает кластер: `Too many partitions for single INSERT block` (default лимит 1000), merges не успевают. ❌ ПОСЛЕДСТВИЕ: insert блокируется ошибкой `max_partitions_per_insert_block exceeded`, ingestion freeze, batch'и теряются.
> - [ ] `PARTITION BY toYYYYMM(date)` с retention 10 лет создаст оптимальное число партиций | 10 лет × 12 месяцев = 120 партиций — нормально. Но `toYYYYMMDD` за 10 лет = 3650 партиций — выше рекомендованного лимита ~1000, merges деградируют. ❌ ПОСЛЕДСТВИЕ: переход с monthly на daily без анализа retention взрывает число parts, кластер тормозит на metadata operations.

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

> [!mcq]
> - [ ] Sparse index — это reverse index типа Lucene для full-text поиска | Sparse — это **разреженный B-tree-like primary index** на отсортированных granules; full-text — отдельные `tokenbf_v1`/`ngrambf_v1` skip indexes. ❌ ПОСЛЕДСТВИЕ: команда ожидает full-text search «из коробки» через ORDER BY на String column — `WHERE message LIKE '%error%'` делает full scan.
> - [ ] Чтобы ускорить point lookup, нужно поставить `index_granularity=1` — каждая row в индексе | Index раздувается в 8192x, не влезает в RAM, теряются преимущества sparse layout — а point lookup всё равно медленнее B-tree. ❌ ПОСЛЕДСТВИЕ: команда копирует «index per row как в Postgres», после рестарта OOM на загрузке индекса миллиардной таблицы.
> - [ ] Sparse index не работает если данные не упорядочены по PRIMARY KEY полностью | Sparse index работает на **prefix** ORDER BY (PRIMARY KEY = prefix of ORDER BY), необязательно на всех колонках tuple. ❌ ПОСЛЕДСТВИЕ: команда указывает PRIMARY KEY = ORDER BY с десятком колонок, индекс распухает, RAM footprint x10 без выигрыша latency.
> - [x] Sparse index хранит одну entry per `index_granularity` (default 8192) rows; ищем granule binary search → читаем 8192 rows → фильтруем точно | Index ~1MB на миллиарды rows, fits в RAM, range scans efficient. ✓ ПРИМЕНЯТЬ: 100B-row table → primary index ~12M entries × 16 bytes = 200MB, легко в RAM на каждой ноде. 📋 ПРАВИЛО: «sparse = одна entry на 8192 rows, range-friendly». 🔗 См. Q11.

**Query:** WHERE key = 150 → binary search → granule 1 → scan 8192 rows → filter.

**Trade-offs:**
- **Tiny index** (fits в memory)
- **Range scans efficient**
- **Point lookup** medленнее (читаем granule)

ClickHouse **не для point lookups**, для **scans**.

> [!mcq]
> - [ ] `index_granularity` по умолчанию 1, как B-tree в Postgres — каждая row в primary index | Default `index_granularity=8192`: одна entry на 8192 rows, primary index влезает в RAM целиком даже для миллиардных таблиц. ❌ ПОСЛЕДСТВИЕ: команда тюнит `index_granularity=1` «для скорости point query», index раздувается в 8000x, не влезает в RAM, OOM на старте.
> - [x] Default `index_granularity=8192`, и поверх sparse index есть skip indexes (`bloom_filter`, `set`, `minmax`) для дополнительного skipping | Sparse index → определяет granule; skip indexes → дают min/max или bloom внутри granule, чтобы пропускать целые granules без чтения. ✓ ПРИМЕНЯТЬ: `INDEX message_idx message TYPE bloom_filter GRANULARITY 4` ускоряет `WHERE message LIKE '%error%'` на logs-таблице x10. 📋 ПРАВИЛО: «sparse index = granule lookup, skip index = granule skipping». 🔗 См. Q12.
> - [ ] Skip indexes (`bloom_filter`) заменяют primary sparse index и индексируют каждую row | Skip indexes — это **secondary**, дополнительная оптимизация поверх sparse primary index. Granularity 4 означает 1 entry на 4 granules = 32k rows. ❌ ПОСЛЕДСТВИЕ: команда удаляет ORDER BY «потому что есть bloom_filter», range scan'ы становятся full table scan, queries деградируют в 100x.
> - [ ] `set` skip index подходит для high-cardinality колонок типа UUID | `set` index хранит **уникальные значения per granule** — для high-cardinality (UUID) набор фактически = всему granule, никакой экономии. Подходит для low-cardinality enum-like (status, country). ❌ ПОСЛЕДСТВИЕ: `INDEX uuid_set uuid TYPE set(1000)` на UUID колонке — индекс размером с данные, дисковое пространство x2 без выигрыша latency.

## Q11. ORDER BY vs PRIMARY KEY?

> [!mcq]
> - [x] ORDER BY = physical sort key в части; PRIMARY KEY = prefix ORDER BY для sparse index | Если PRIMARY KEY не указан — равен ORDER BY; короче PK = меньше RAM на индекс. ✓ ПРИМЕНЯТЬ: ORDER BY (date, user_id, event_id), PRIMARY KEY (date, user_id) — индекс ~30% от полного. 📋 ПРАВИЛО: «PK = prefix of ORDER BY, не uniqueness». 🔗 См. Q10.
> - [ ] PRIMARY KEY в CH — uniqueness constraint, как в Postgres | В CH PRIMARY KEY **не уникален**, не enforced — это просто prefix ORDER BY для **sparse primary index**. ❌ ПОСЛЕДСТВИЕ: команда полагается на uniqueness PRIMARY KEY, дубли проникают, fact-table ломает join'ы и метрики.
> - [ ] PRIMARY KEY обязан быть равным ORDER BY, иначе INSERT падает | PRIMARY KEY может быть **prefix** ORDER BY (короче), для экономии RAM на индексе. ❌ ПОСЛЕДСТВИЕ: команда дублирует длинный tuple в обоих, primary index раздут до 1GB на ноду, OOM на старте.
> - [ ] ORDER BY можно менять без переписывания таблицы через `ALTER TABLE ... MODIFY ORDER BY` | Изменение ORDER BY = **физическая пересортировка**, требует пересоздания таблицы (`INSERT INTO new SELECT FROM old ORDER BY ...`). ❌ ПОСЛЕДСТВИЕ: `ALTER` падает с `Cannot modify ORDER BY` или работает только для extend-prefix; миграция занимает часы простоя.

```sql
ORDER BY (user_id, event_time)  -- physical sorting в parts
PRIMARY KEY user_id              -- prefix of ORDER BY (для index)
```

Если PRIMARY KEY не указан — **=ORDER BY**.

> [!mcq]
> - [ ] Если ORDER BY = (a, b, c), то фильтр `WHERE c = X` использует sparse index | Sparse index работает только на **prefix tuple**: `WHERE a = X` или `WHERE a = X AND b = Y` — да; `WHERE c = X` без a/b — full scan. ❌ ПОСЛЕДСТВИЕ: команда ставит важную фильтр-колонку в конец tuple, queries deging до full scan, latency 30s+.
> - [ ] Можно поставить ORDER BY tuple любой длины — всегда быстрее | Длинный ORDER BY (10+ колонок) раздувает primary index в RAM и замедляет binary search; рекомендуется 2-4 колонки. ❌ ПОСЛЕДСТВИЕ: ORDER BY (a,b,c,d,e,f,g,h) → primary index в 5x больше, на старте долгая загрузка, RAM footprint избыточный.
> - [ ] ORDER BY (high_card, low_card) лучше чем (low_card, high_card) для compression | Для compression выгоднее **low → high cardinality** (одинаковые значения подряд лучше жмутся), для filter-эффективности — самые частые фильтр-колонки в начале. ❌ ПОСЛЕДСТВИЕ: ORDER BY (uuid, country) → плохое сжатие country (значения разбросаны), 2x storage vs ORDER BY (country, uuid).
> - [x] ORDER BY определяет physical layout part'а; запрос с prefix-фильтром (`WHERE a=X`) использует sparse index → читает только нужные granules | Если фильтр не префикс — нужен skip index или другой ORDER BY. ✓ ПРИМЕНЯТЬ: dashboards events по user → ORDER BY (user_id, event_time); по time-window → ORDER BY (event_time, user_id). 📋 ПРАВИЛО: «ORDER BY = prefix самого частого фильтра». 🔗 См. Q12.

**ORDER BY:** определяет physical layout.
**PRIMARY KEY:** prefix ORDER BY для primary index.

**Best practice:** обычно `PRIMARY KEY = ORDER BY` (default).

> [!mcq]
> - [x] Best practice: ORDER BY tuple от low cardinality (status, country) к high (timestamp, user_id), при этом prefix = частый WHERE | Low-cardinality в начале = better compression + cheap binary search; частый фильтр в prefix = pruning. ✓ ПРИМЕНЯТЬ: events table → ORDER BY (event_type, date, user_id) если основные фильтры — type+date. 📋 ПРАВИЛО: «low cardinality первая, частый фильтр в prefix». 🔗 См. Q22.
> - [ ] Order BY columns надо ставить от high cardinality к low — UUID первой, country последней | Наоборот: low cardinality первой улучшает compression и filter-cardinality; UUID в начале даёт почти random sort, compression падает. ❌ ПОСЛЕДСТВИЕ: ORDER BY (uuid, ...) на 1B rows → 4TB вместо 1TB, дисковое пространство x4, дольше I/O.
> - [ ] ORDER BY tuning неважно — vectorized execution компенсирует любую конфигурацию | Vectorized ускоряет scan, но **не отменяет** необходимость skip granules; плохой ORDER BY = full scan = миллиарды rows вместо тысяч. ❌ ПОСЛЕДСТВИЕ: команда экономит на ORDER BY tuning «vectorized всё ускорит», dashboard p95 = 30s, тюнинг даёт 50x speedup.
> - [ ] ORDER BY следует ставить по чисто алфавитному порядку колонок для читаемости | ORDER BY определяет I/O-оптимизацию, не codestyle; алфавит ≠ оптимально по cardinality/filter pattern. ❌ ПОСЛЕДСТВИЕ: ORDER BY (a_uuid, b_country, ...) — uuid первой, ужасное compression и filter pruning, latency деградирует.

**ORDER BY tuning** = main performance lever:
- Order columns by cardinality: low → high
- Most common filter columns first

## Q12. Skip indexes (data skipping)?

**Skip indexes** — secondary indexes для **skipping granules** (без чтения).

> [!mcq]
> - [ ] Skip index типа `bloom_filter` подходит для всех колонок включая числовые ranges | `bloom_filter` — для **equality checks** (`WHERE x = 'value'`); для ranges нужен `minmax`. ❌ ПОСЛЕДСТВИЕ: `INDEX latency_bloom latency TYPE bloom_filter` для запроса `WHERE latency > 1000` — индекс игнорируется, full scan.
> - [ ] Skip index с `GRANULARITY 1` — лучшая практика, читай каждый granule | `GRANULARITY N` означает **1 entry на N granules** (не «индекс per granule»); GRANULARITY 1 = индекс per granule = размер примерно как primary index, RAM-нагрузка x1, минимум выигрыша. ❌ ПОСЛЕДСТВИЕ: команда копирует example с GRANULARITY 1, индекс в RAM раздут, query speed не выросла.
> - [ ] Skip index автоматически создаётся при `INSERT`, ничего настраивать не надо | Skip indexes — explicit DDL: `INDEX name col TYPE bloom_filter GRANULARITY 4`; ничего автоматически не создаётся. ❌ ПОСЛЕДСТВИЕ: команда ждёт «авто-индекс на нужные колонки», queries `LIKE '%error%'` остаются 30s, после явного добавления tokenbf_v1 — секунды.
> - [x] `minmax` для ranges (timestamps, numeric); `set(N)` для low-cardinality enum; `bloom_filter` для equality на high-cardinality; `tokenbf_v1`/`ngrambf_v1` для substring search | Каждый skip index решает свой паттерн фильтра. ✓ ПРИМЕНЯТЬ: logs `WHERE level = 'ERROR'` → `set(10)` на level; `WHERE message LIKE '%timeout%'` → `tokenbf_v1`. 📋 ПРАВИЛО: «range → minmax, equality → bloom, enum → set, text → tokenbf». 🔗 См. Q10.

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
```

> [!mcq]
> - [ ] CH SQL — это полный ANSI SQL, любой Postgres-запрос работает без изменений | CH SQL — **own dialect**, частично пересекается с ANSI: нет `RETURNING`, нет нормальных recursive CTE, ограниченные UPDATE/DELETE. ❌ ПОСЛЕДСТВИЕ: миграция Postgres-приложения 1:1 ломается на CTE, UPDATE, MERGE — переписывать половину запросов.
> - [x] CH SQL — own dialect: SELECT/WHERE/GROUP BY как стандарт + extensions (`arrayJoin`, `WITH FILL`, `LIMIT BY`); UPDATE/DELETE через mutations, JOIN'ы слабее | Знакомый core + добавки + опущения. ✓ ПРИМЕНЯТЬ: dashboard SELECT работает; ETL с MERGE/UPDATE — переписать в `INSERT into ReplacingMergeTree`. 📋 ПРАВИЛО: «CH ≠ ANSI: SELECT да, MERGE/UPDATE нет». 🔗 См. Q14.
> - [ ] `count(*)` в CH равен `count(column)` независимо от NULL | `count(column)` исключает NULL, `count()` (или `count(*)`) считает все rows; в CH NULL опционален и часто отсутствует, но семантика та же что в SQL. ❌ ПОСЛЕДСТВИЕ: метрика «записей с user_id» через `count()` в Nullable column пропускает NULL-кейсы, отчёт занижает.
> - [ ] CH SQL не поддерживает GROUP BY с несколькими колонками, только одну | Полная поддержка multi-column GROUP BY и GROUPING SETS, ROLLUP, CUBE с 21.x. ❌ ПОСЛЕДСТВИЕ: команда переписывает GROUP BY (a, b) в JOIN-self-aggregate «потому что не работает», теряет 10x perf.

> [!mcq]
> - [ ] Лямбда-функции типа `arrayMap(x -> x*2, prices)` исполняются построчно через interpreter | Они **векторизованы**: компилируются в SIMD batch-операции, обрабатывают тысячи rows за раз. ❌ ПОСЛЕДСТВИЕ: преждевременная оптимизация через rewrite в `arrayJoin + WHERE` режет throughput в 5x — лямбды быстрее.
> - [ ] `arrayMap` возвращает scalar — сумму элементов | `arrayMap` возвращает **массив той же длины** с применённой функцией; для суммы нужен `arraySum`. ❌ ПОСЛЕДСТВИЕ: `SELECT arrayMap(x -> x*0.2, prices) AS tax FROM orders` ожидание скаляра → получает Array, ORM падает на parsing.
> - [x] `arrayMap(x -> x*2, prices)` — векторизованная higher-order функция: новый Array той же длины, immutable, batch SIMD | First-class arrays + lambdas для denormalized analytics. ✓ ПРИМЕНЯТЬ: e-commerce — `arrayMap(p -> p*1.2, prices)` для VAT по всему order'у одной операцией. 📋 ПРАВИЛО: «arrayMap = vectorized map, immutable». 🔗 См. Q14.
> - [ ] `arrayMap` мутирует исходный столбец `prices` для экономии памяти | Все array-функции **immutable**, возвращают новый array, исходные данные не меняются. ❌ ПОСЛЕДСТВИЕ: ожидание side-effect в `INSERT...SELECT arrayMap(...)` ломает идемпотентность ETL, дубли на reprocessing.

```sql
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

> [!mcq]
> - [ ] `arrayMap` мутирует исходный массив in-place для экономии памяти | Все array-функции в ClickHouse возвращают новый массив, immutable. ❌ ПОСЛЕДСТВИЕ: ожидание side-effect в `UPDATE` ломает идемпотентность мутации, дубли при reprocessing.
> - [x] `ARRAY JOIN tags` разворачивает массив в строки, fan-out 1:N как `unnest` в Postgres | Каждый элемент `Array(String)` становится отдельной row, удобно для tag-analytics без normalize. ✓ ПРИМЕНЯТЬ: Cloudflare DNS-аналитика взрывает массив доменов в строки для top-N запросов. 📋 ПРАВИЛО: «`ARRAY JOIN` = explode массива в строки». 🔗 См. Q13.
> - [ ] `has(tags, 'x')` использует bloom filter из коробки и не требует индекса | `has()` — линейный скан элементов массива, для ускорения нужен явный `INDEX ... TYPE bloom_filter`. ❌ ПОСЛЕДСТВИЕ: запрос по 1B rows без skip-индекса читает весь столбец, latency 30s+.
> - [ ] Higher-order функции типа `arrayFilter(x -> x > 10, prices)` исполняются построчно через JIT loop | Они векторизованы, обрабатывают тысячи строк batch'ем — это часть быстродействия CH. ❌ ПОСЛЕДСТВИЕ: преждевременный rewrite в `arrayJoin` + WHERE «для оптимизации» режет throughput в 5x.

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

> [!mcq]
> - [ ] CH JOIN'ы по производительности equal Postgres — hash join + spill to disk | До 22.x JOIN'ы держались **в памяти** (broadcast hash join), spill to disk появился позже (`join_algorithm='grace_hash'`); даже сейчас слабее Postgres на больших dataset'ах. ❌ ПОСЛЕДСТВИЕ: команда мигрирует joinful BI-запросы из Postgres «всё ускорится», получает OOM на JOIN 100M × 10M.
> - [ ] `WHERE id IN (SELECT id FROM users WHERE country='US')` всегда медленнее JOIN | Чаще наоборот: `IN`-subquery даёт CH передавать только filter set между shards (cheaper), JOIN broadcast'ит всю right table. ❌ ПОСЛЕДСТВИЕ: команда переписывает IN в JOIN «для читаемости», query latency 2x, distributed traffic растёт.
> - [ ] `GLOBAL JOIN` не нужен в Distributed-таблицах — обычный JOIN сам распараллелится | Без `GLOBAL`, JOIN на каждой ноде делает локальный JOIN, теряя rows из других shards; `GLOBAL` сначала собирает right table на coordinator, потом broadcast. ❌ ПОСЛЕДСТВИЕ: JOIN двух Distributed без `GLOBAL` → результаты разные на разных запросах, инконсистентность дашборда.
> - [x] CH default = **broadcast hash join** (правая таблица копируется на все ноды), для small right table OK; для large — `IN`-subquery, denormalize, или `join_algorithm='grace_hash'` | Архитектура CH ставит на denormalized model, JOIN — fallback. ✓ ПРИМЕНЯТЬ: events с user-info → денормализуй user_country/user_segment в events table при ingestion, не делай JOIN на dashboard. 📋 ПРАВИЛО: «JOIN small right OK, large right → IN или denormalize». 🔗 См. Q14.

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

> [!mcq]
> - [ ] Window functions работают с самого первого релиза ClickHouse и полностью эквивалентны PostgreSQL | Появились только с **2021** (v21.x), часть `RANGE BETWEEN` и pattern frames до сих пор limited. ❌ ПОСЛЕДСТВИЕ: `RANGE INTERVAL '1 day' PRECEDING` падает с `NOT_IMPLEMENTED` на старом 20.x кластере, миграция блокируется.
> - [ ] `ROW_NUMBER() OVER (...)` гарантирует stable ordering между рестартами без `ORDER BY` | Без явного `ORDER BY` внутри `OVER()` порядок недетерминированный из-за parallel parts. ❌ ПОСЛЕДСТВИЕ: дедупликация по `rn=1` дает разные победившие строки на повторе, нарушает идемпотентность ETL.
> - [x] `sum() OVER (PARTITION BY user_id ORDER BY event_time)` считает running total по пользователю в одном проходе | Window function partitioned + ordered рассчитывает кумулятив без self-join. ✓ ПРИМЕНЯТЬ: Uber observability — running latency p99 по сервису во времени. 📋 ПРАВИЛО: «`OVER PARTITION BY` = группа без коллапса строк». 🔗 См. Q13.
> - [ ] Window functions всегда быстрее чем GROUP BY с self-join, поэтому надо переписывать всё | Для простых aggregates `GROUP BY` + ARRAY JOIN часто быстрее window-варианта в CH. ❌ ПОСЛЕДСТВИЕ: переход с `GROUP BY` на `OVER()` без бенчмарка добавляет 2x latency на dashboard query.

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

> [!mcq]
> - [ ] MV в ClickHouse периодически делает `REFRESH MATERIALIZED VIEW` как в PostgreSQL | Классический CH MV — **incremental trigger** на INSERT в source, никакого периодического REFRESH (Refreshable MV — отдельная фича с 2024). ❌ ПОСЛЕДСТВИЕ: расчёт на cron'е забивает кластер, а dashboard всё равно читает stale data до следующего refresh.
> - [ ] MV видит весь historical state source-таблицы при каждом INSERT и пересчитывает всё | MV видит **только новый INSERT-блок**, не полную таблицу — поэтому нельзя делать оконные функции по всей истории. ❌ ПОСЛЕДСТВИЕ: `SELECT count() FROM events` в MV считает только rows нового batch'а, дашборд показывает «100 events/час» вместо «1M total».
> - [ ] MV с `SummingMergeTree` на target будет уникализировать ключ во время INSERT | `SummingMergeTree` сворачивает строки **только при merge parts** в фоне, не сразу. ❌ ПОСЛЕДСТВИЕ: query без `GROUP BY` или `FINAL` видит несвёрнутые duplicates, метрики «прыгают» x2-x3 первые часы после ingestion.
> - [x] MV срабатывает на каждом INSERT в source и инкрементально дописывает aggregate в target | Trigger-based, обновление realtime, идеально для pre-aggregated dashboards. ✓ ПРИМЕНЯТЬ: Yandex.Metrica — minutely/hourly агрегаты visits на десятках миллиардов событий/день. 📋 ПРАВИЛО: «MV в CH = INSERT trigger, не VIEW». 🔗 См. Q5.

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

> [!mcq]
> - [ ] Projection — это синоним materialized view, разницы нет, выбирай по вкусу | Projection хранится **внутри** parts source-таблицы, MV — отдельная таблица. Семантика другая. ❌ ПОСЛЕДСТВИЕ: путаница приводит к двойному хранению (MV+projection) и x2 disk usage без выигрыша latency.
> - [x] Projection — alternate sort order внутри той же таблицы, оптимизатор сам выбирает её по предикату | Один INSERT даёт два отсортированных представления внутри parts; CH сам решает какое читать. ✓ ПРИМЕНЯТЬ: Cloudflare логи с ORDER BY (timestamp) + projection ORDER BY (domain) для drill-down по домену без full scan. 📋 ПРАВИЛО: «projection = второй ORDER BY in-place». 🔗 См. Q11.
> - [ ] Projection нужно явно указывать в `SELECT ... USE PROJECTION`, иначе не используется | Оптимизатор сам подбирает projection по WHERE/GROUP BY, hint опциональный. ❌ ПОСЛЕДСТВИЕ: команда добавляет projection и думает что заработало, но без hint оптимизатор не подключился из-за неправильного `ORDER BY` — дашборд так же тормозит.
> - [ ] Projection не увеличивает write amplification, потому что хранит только diff к main sort order | Projection хранит **полную копию данных** в другом порядке (и кодеки), запись становится дороже. ❌ ПОСЛЕДСТВИЕ: добавили 3 projection на 1B-row таблицу — INSERT throughput упал в 4x, kafka consumer лагает.

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

> [!mcq]
> - [x] `Distributed` — proxy без своих данных, INSERT раскидывает по shard'ам по sharding key, SELECT параллелит и мержит | Логический view над физическими `ReplicatedMergeTree` на нодах. ✓ ПРИМЕНЯТЬ: Uber observability шардит по `service_id` для linear scale на сотнях нод. 📋 ПРАВИЛО: «`Distributed` = router, `Replicated*` = storage». 🔗 См. Q8.
> - [ ] `Distributed` сама хранит копии данных всех shard'ов и поэтому медленная | Это **stateless proxy**, никаких локальных данных, кроме асинхронной insert-очереди. ❌ ПОСЛЕДСТВИЕ: команда расширяет диск под Distributed-таблицу, реальный bottleneck (shard storage) игнорируется, кластер встаёт.
> - [ ] JOIN между двумя Distributed-таблицами по умолчанию делает distributed-merge с shuffle | По умолчанию правую таблицу broadcast'ят в полном виде на каждый node, shuffle нет — нужен `GLOBAL JOIN` или parallel replicas. ❌ ПОСЛЕДСТВИЕ: JOIN большой dim-таблицы на каждой ноде → coordinator OOM на single node.
> - [ ] Sharding key `rand()` гарантирует равномерное распределение и не ломает запросы | `rand()` ломает locality: запросы по `user_id` бьют по всем shard'ам, fan-out максимальный. ❌ ПОСЛЕДСТВИЕ: latency p99 растёт линейно с числом shard'ов, дашборды по конкретному пользователю timeout'ят.

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

> [!mcq]
> - [ ] `ReplicatedMergeTree` синхронизируется напрямую через TCP между репликами без посредника | CH использует **внешний consensus** (ZooKeeper или Keeper) для координации очереди INSERT и merge. ❌ ПОСЛЕДСТВИЕ: команда удаляет ZooKeeper «он не нужен» — replication ломается, реплики расходятся, читаются разные данные.
> - [ ] ZooKeeper хранит сами data parts и обслуживает SELECT-запросы | ZK хранит **только metadata** (replica list, queue, parts list), данные — на disk нод CH. ❌ ПОСЛЕДСТВИЕ: команда выделяет ZK-кластер на дисках под full data — узлы падают по disk full при росте трафика.
> - [x] ZooKeeper/Keeper хранит metadata replica queue, parts list и leader election; данные едут peer-to-peer | Координация лёгкая, актуальные blocks тянутся между репликами напрямую. ✓ ПРИМЕНЯТЬ: 3-node ZK ensemble обслуживает CH-кластер с десятками shard'ов в Cloudflare. 📋 ПРАВИЛО: «ZK = метаданные, реплики = данные». 🔗 См. Q8.
> - [ ] Тысячи мелких INSERT'ов в секунду не нагружают ZooKeeper, нагрузка только на CH | Каждый INSERT block регистрируется в ZK — high QPS убивает ZK быстрее чем сам CH. ❌ ПОСЛЕДСТВИЕ: одиночные INSERT по 1 row → too_many_parts через минуту, ZK в 100% CPU, cluster-wide replication freeze.

## Q21. ClickHouse Keeper vs ZooKeeper?

**ClickHouse Keeper** (с 2021) — drop-in replacement ZooKeeper, написан на C++.

**Преимущества:**
- **Faster** (10x для some operations)
- **Less memory**
- Embeddable в ClickHouse process
- Same Raft-based consensus
- Compatible с ZooKeeper protocol

В **2025** — Keeper recommended для new deployments.

> [!mcq]
> - [ ] ClickHouse Keeper — это форк ZooKeeper на Java с патчами производительности | Keeper — **C++** с нуля, использует Raft (через NuRaft), не Java и не форк ZK. ❌ ПОСЛЕДСТВИЕ: команда тюнит JVM/heap для Keeper по гайдам ZooKeeper, никакого эффекта, диагностика затягивается.
> - [ ] Keeper использует Paxos consensus, поэтому несовместим с ZK на уровне протокола | Использует **Raft**, протокол клиента совместим с ZK (drop-in для CH). ❌ ПОСЛЕДСТВИЕ: команда ставит отдельный ZK «потому что несовместим», дублирует ops без необходимости.
> - [ ] Запускать Keeper в одном процессе с clickhouse-server — это рекомендованная prod-конфигурация | Embedded mode — для dev/test; в prod Keeper выносят в **отдельные ноды** (3 или 5) для изоляции failure domain. ❌ ПОСЛЕДСТВИЕ: OOM в clickhouse-server валит и Keeper, кворум теряется, весь кластер read-only.
> - [x] Keeper — C++ Raft-replacement ZooKeeper, ZK-wire-compatible, меньше памяти, в 2025 — default | Drop-in для existing setups, проще ops, нет Java GC. ✓ ПРИМЕНЯТЬ: ClickHouse Cloud перешёл на Keeper для всех managed tenants. 📋 ПРАВИЛО: «Keeper = C++ ZK на Raft». 🔗 См. Q20.

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

> [!mcq]
> - [ ] `LowCardinality(String)` подходит для UUID и user_id, потому что строки короткие | LC эффективен только для **малой cardinality** (десятки-тысячи unique values), для UUID dictionary распухает и хуже plain. ❌ ПОСЛЕДСТВИЕ: dictionary 100M+ entries — RAM на старте, INSERT throughput падает в 10x, рекомендуется откатить тип.
> - [x] `Delta`+`ZSTD` для timestamps и `Gorilla`+`ZSTD` для floats — стандартный recipe для time-series | Specialized codecs пакуют монотонные/похожие значения до 50x; LZ4 default менее эффективен на TS. ✓ ПРИМЕНЯТЬ: Cloudflare metrics — DoubleDelta+ZSTD дают 20x compression на DateTime столбцах. 📋 ПРАВИЛО: «time-series → DoubleDelta+ZSTD, floats → Gorilla». 🔗 См. Q5.
> - [ ] ZSTD сжимает в 10x быстрее LZ4 при том же ratio | LZ4 быстрее по CPU, ZSTD даёт лучший ratio но дороже по compute. ❌ ПОСЛЕДСТВИЕ: смена на ZSTD level 22 на горячих ingestion-таблицах увеличивает CPU usage в 4x, kafka consumer лагает на пиках.
> - [ ] Codec задаётся на уровне таблицы и применяется ко всем колонкам сразу | Codec задаётся **per-column**, можно смешивать (Delta для time, Gorilla для float, default для остального). ❌ ПОСЛЕДСТВИЕ: глобальный `CODEC(ZSTD(22))` на всю таблицу — high-cardinality string столбцы становятся CPU bottleneck без выигрыша размера.

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

> [!mcq]
> - [ ] TTL `MOVE TO VOLUME 'cold'` физически удаляет старые parts | `TO VOLUME` **перемещает** между volumes; для удаления нужен `TTL ... DELETE`. ❌ ПОСЛЕДСТВИЕ: команда ждёт освобождения hot SSD, но parts всё ещё existуют на cold S3 — запросы FINAL по-прежнему full-scan, latency не улучшается.
> - [x] Storage policy с volumes hot=SSD/cold=S3 + TTL `TO VOLUME 'cold'` автоматически вытесняет старые parts | Декларативный tiering без ручного перемещения. ✓ ПРИМЕНЯТЬ: Uber observability — 7 дней logs на NVMe, 90 дней на S3, query layer прозрачно ходит в оба. 📋 ПРАВИЛО: «hot SSD + cold S3 + TTL = автоматический tiering». 🔗 См. Q22.
> - [ ] Запросы по cold S3 partition'ам имеют ту же latency что hot SSD за счёт кэширования | S3 read latency на порядки выше; даже с локальным кешем cold queries в 10-100x медленнее. ❌ ПОСЛЕДСТВИЕ: дашборд за «прошлый квартал» внезапно отвечает 60s вместо 2s, alerting спит, BI users жалуются.
> - [ ] `move_factor` 0.2 означает что на cold уходит 20% самых старых parts независимо от disk usage | `move_factor` — порог **свободного места** на hot volume; при < 20% free CH начинает мигрировать в cold. ❌ ПОСЛЕДСТВИЕ: команда ждёт пропорционального переноса, hot SSD заполняется до 99%, INSERT блокируется по `not_enough_space`.

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

> [!mcq]
> - [ ] Можно делать `SELECT` напрямую из `Kafka` engine таблицы как из обычной MergeTree | Kafka engine — **streaming consumer**: каждый SELECT consumer'ит и коммитит offset, повторное чтение тех же данных невозможно. ❌ ПОСЛЕДСТВИЕ: BI tool делает периодические SELECT'ы по Kafka-таблице, ворует messages из MV-pipeline, в target MergeTree пропадают батчи событий.
> - [x] Kafka engine + MV `TO target_table` — паттерн «read once, materialize to MergeTree» для continuous ingestion | Consumer читает топик, MV срабатывает на каждый poll и пишет в target. ✓ ПРИМЕНЯТЬ: Cloudflare events pipeline — Kafka → CH без отдельного consumer service. 📋 ПРАВИЛО: «Kafka engine + MV = native ingestion pipeline». 🔗 См. Q17.
> - [ ] `kafka_max_block_size` не влияет на too_many_parts, это чисто consumer-buffer setting | Как раз влияет: маленький block → частые INSERT'ы → too_many_parts; рекомендуют 65k+ rows/block. ❌ ПОСЛЕДСТВИЕ: дефолт 65536 уменьшен «чтобы быстрее» → MergeTree через 5 минут падает с `Too many parts (300)`, ingestion freeze.
> - [ ] При перезапуске CH Kafka engine читает топик с самого начала, идемпотентно | Offset хранится в `__consumer_offsets` Kafka на основе `kafka_group_name`, рестарт продолжает с последнего commit. ❌ ПОСЛЕДСТВИЕ: команда удаляет всю target table «для replay» и ждёт что данные перельются — данные не приходят, нужен явный reset через `system.kafka` или новая group.

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

> [!mcq]
> - [ ] `PostgreSQL` engine реплицирует postgres-таблицу в локальный MergeTree, как logical replication | Это **federated read-through**, каждый запрос — live round-trip в Postgres, без локального хранения. ❌ ПОСЛЕДСТВИЕ: BI dashboard на CH-таблице делает 1000 RPS, реальный Postgres получает весь load и встаёт под locks.
> - [ ] Table function `s3('s3://bucket/file.parquet', 'Parquet')` всегда быстрее чем INSERT в MergeTree + SELECT | s3() читает напрямую с object storage без локальных skip-индексов и кодеков — по аналитике медленнее MergeTree. ❌ ПОСЛЕДСТВИЕ: команда оставляет всю аналитику на s3() table function для экономии диска, latency dashboard'а — 30s+, рекомендуется ETL в MergeTree.
> - [x] `INSERT INTO local SELECT FROM s3(...)` — встроенный ETL без Spark/Airflow для одноразовых импортов Parquet | `s3()` table function умеет читать Parquet/CSV/JSON напрямую, поток в MergeTree. ✓ ПРИМЕНЯТЬ: GitLab analytics импортирует исторические Parquet snapshot'ы из S3 одной командой без data pipeline. 📋 ПРАВИЛО: «s3() table function = read-through ETL source». 🔗 См. Q24.
> - [ ] External engine для MySQL хранит копию данных и использует CDC через binlog | Это обычный JDBC-style proxy, **не CDC**; для CDC нужен MaterializedMySQL engine (отдельная фича). ❌ ПОСЛЕДСТВИЕ: команда ждёт автоматической синхронизации updates, но MySQL engine читает live при каждом запросе — производительность не улучшилась.

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

> [!mcq]
> - [ ] ClickHouse подходит как primary OLTP-база для checkout сервиса с per-row UPDATE | OLAP, нет нормальных transactions, нет точечного UPDATE без mutations — для OLTP неподходящ. ❌ ПОСЛЕДСТВИЕ: попытка использовать как базу заказов даёт race conditions при `ALTER TABLE...UPDATE`, неконсистентный остаток на складе.
> - [x] Прод use cases — observability (logs/metrics/traces), real-time analytics, ad-tech, clickstream | Сильные стороны CH — append-heavy, columnar, time-series workloads. ✓ ПРИМЕНЯТЬ: Uber заменил Elasticsearch на CH для observability, экономия 5x по storage. 📋 ПРАВИЛО: «CH = append-heavy analytics, не OLTP». 🔗 См. Q4.
> - [ ] Cloudflare использует ClickHouse как session store для Workers KV | Cloudflare использует CH для **DNS analytics и edge logs**, не как KV. ❌ ПОСЛЕДСТВИЕ: команда копирует «архитектуру Cloudflare» и ставит CH под session storage с TTL по 30s — partition mess, too_many_parts через час.
> - [ ] Yandex использует ClickHouse только для логов разработчиков, не для production analytics | Yandex.Metrica — флагманский use case, **>100B событий/день**, исторически основной потребитель. ❌ ПОСЛЕДСТВИЕ: недооценка scale на собеседовании, команда боится ставить CH на «настоящий» трафик и берёт более дорогой Snowflake.

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

> [!mcq]
> - [ ] Mutations (`ALTER TABLE...UPDATE`) выполняются синхронно построчно, как в Postgres | Mutations — **async**, переписывают целые parts, могут идти часами. ❌ ПОСЛЕДСТВИЕ: GDPR-запрос «удалить пользователя» возвращает OK мгновенно, но данные ещё несколько часов видны в SELECT — compliance breach.
> - [ ] Sparse primary index хорош для point queries по конкретному ID | Sparse index хорош для **range scans** по sort key; точечный `WHERE id=X` всё равно читает gran 8192 rows. ❌ ПОСЛЕДСТВИЕ: команда строит CH под ID-lookup сценарий, latency 100ms+ на point query, RPS падает на «лёгких» эндпоинтах.
> - [x] Нет full ACID transactions, mutations async, JOIN'ы слабее Postgres, foreign keys/constraints отсутствуют | Это базовые trade-offs columnar OLAP-движка. ✓ ПРИМЕНЯТЬ: для read-heavy analytics OK; для финансовых транзакций — оставить Postgres, CH только для аналитической витрины. 📋 ПРАВИЛО: «нет ACID = read-heavy only». 🔗 См. Q4.
> - [ ] Wide rows (200+ колонок) одинаково эффективны как в Postgres за счёт columnar storage | Columnar помогает читать 5 из 200 колонок, но **per-part overhead** растёт с числом колонок: 200 файлов на part, merge stress, метаданные раздуваются. ❌ ПОСЛЕДСТВИЕ: 500-колоночная wide-таблица для feature store — too_many_parts, merges не успевают, кластер деградирует.

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

> [!mcq]
> - [x] `Too many parts` — главный production-incident, чинится batching INSERT'ов и нормальным partition key | Каждый INSERT = part, мелкие/частые INSERT'ы плодят parts быстрее merges. ✓ ПРИМЕНЯТЬ: batch 10k+ rows на INSERT или async_insert; partition by месяц/день, не по hour/UUID. 📋 ПРАВИЛО: «10k rows/INSERT, low-cardinality PARTITION BY». 🔗 См. Q9.
> - [ ] Mutations (`ALTER TABLE...DELETE`) безопасно использовать для регулярной чистки старых данных | Mutations переписывают целые parts, дорогие; для retention есть **TTL DELETE** который дешевле и автоматический. ❌ ПОСЛЕДСТВИЕ: cron вызывает `ALTER TABLE...DELETE WHERE ts < now()-30d` каждый час → mutations queue растёт, replication лагает на часы.
> - [ ] FINAL keyword в SELECT по `ReplacingMergeTree` — стандартный способ читать без дублей в prod | `FINAL` форсит merge на лету = full table scan, неприменим к большим таблицам. ❌ ПОСЛЕДСТВИЕ: FINAL запрос на ReplacingMergeTree → full table scan, latency 30s+ для 1B rows, dashboard timeout'ит.
> - [ ] Резервное копирование делается через `pg_dump`-подобную утилиту `clickhouse-dump` | Backup в CH — `ALTER TABLE FREEZE` (snapshot parts) + sync в S3, либо `BACKUP TABLE` (с 22.x), отдельной `clickhouse-dump` нет. ❌ ПОСЛЕДСТВИЕ: команда полагается на «стандартный dump», бэкапы не делаются годами, при disk failure теряют год данных.

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
