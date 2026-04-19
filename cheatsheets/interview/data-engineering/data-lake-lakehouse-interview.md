---
title: "Вопросы на собеседовании: Data Lake и Lakehouse"
description: "Data Lake (S3, HDFS), форматы (Parquet, ORC, Avro), Lakehouse архитектура (Delta Lake, Apache Iceberg, Apache Hudi), ACID на S3, time travel, schema evolution, data mesh"
tags:
  - interview
  - data-engineering
  - data-lake-lakehouse-interview
aliases:
  - "Data lake interview"
  - "Lakehouse interview"
  - "Delta Lake interview"
  - "Apache Iceberg interview"
  - "Apache Hudi interview"
  - "Data mesh interview"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Data Lake и Lakehouse`

**Data Lake** — хранилище **сырых** данных любого формата на cheap storage (S3, ADLS, HDFS). **Lakehouse** — комбинация: lake-простота + warehouse-фичи (ACID, schema, indexing). Реализуется через **Delta Lake** (Databricks), **Apache Iceberg** (Netflix → Apache), **Apache Hudi** (Uber → Apache).

Дата последнего обновления: 2026-04-18

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Delta Lake Documentation](https://docs.delta.io/)
- [Apache Iceberg Documentation](https://iceberg.apache.org/docs/latest/)
- [Apache Hudi Documentation](https://hudi.apache.org/docs/overview/)
- [What is a Data Lake — AWS](https://aws.amazon.com/big-data/datalakes-and-analytics/what-is-a-data-lake/)
- [Lakehouse paper (Databricks)](https://databricks.com/wp-content/uploads/2020/12/cidr_lakehouse.pdf)
- [Data Mesh (Zhamak Dehghani)](https://martinfowler.com/articles/data-mesh-principles.html)
- [Apache Parquet](https://parquet.apache.org/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Data Lake?](#q1--что-такое-data-lake)
- [Q2. (!) Data Lake vs Data Warehouse?](#q2--data-lake-vs-data-warehouse)
- [Q3. (!) Что такое Lakehouse?](#q3--что-такое-lakehouse)

**Storage**
- [Q4. (!) S3, ADLS, GCS, HDFS — где хранят?](#q4--s3-adls-gcs-hdfs--где-хранят)
- [Q5. Object storage vs HDFS — отличия?](#q5-object-storage-vs-hdfs--отличия)

**Форматы файлов**
- [Q6. (!) Parquet — что это и зачем?](#q6--parquet--что-это-и-зачем)
- [Q7. ORC?](#q7-orc)
- [Q8. Avro?](#q8-avro)
- [Q9. (!) Сравнение Parquet vs ORC vs Avro?](#q9--сравнение-parquet-vs-orc-vs-avro)

**Lakehouse форматы (table formats)**
- [Q10. (!) Delta Lake — что это?](#q10--delta-lake--что-это)
- [Q11. (!) Apache Iceberg — отличия?](#q11--apache-iceberg--отличия)
- [Q12. (!) Apache Hudi?](#q12--apache-hudi)
- [Q13. (!) Сравнение Delta vs Iceberg vs Hudi?](#q13--сравнение-delta-vs-iceberg-vs-hudi)

**Lakehouse фичи**
- [Q14. (!) ACID transactions на S3 — как?](#q14--acid-transactions-на-s3--как)
- [Q15. (!) Time travel?](#q15--time-travel)
- [Q16. Schema evolution?](#q16-schema-evolution)
- [Q17. (!) Z-Order и data clustering?](#q17--z-order-и-data-clustering)
- [Q18. Compaction (small files problem)?](#q18-compaction-small-files-problem)

**Архитектура**
- [Q19. (!) Medallion architecture (Bronze, Silver, Gold)?](#q19--medallion-architecture-bronze-silver-gold)
- [Q20. (!) Data Mesh — что это?](#q20--data-mesh--что-это)
- [Q21. Storage tier optimization (hot/warm/cold)?](#q21-storage-tier-optimization-hotwarmcold)

**Compute engines**
- [Q22. (!) Какие engines работают с Lakehouse?](#q22--какие-engines-работают-с-lakehouse)
- [Q23. Trino / Presto — для query на lake?](#q23-trino--presto--для-query-на-lake)

**Подводные камни**
- [Q24. (!) Small files problem?](#q24--small-files-problem)
- [Q25. Data swamp — что это?](#q25-data-swamp--что-это)
- [Q26. (!) Метаданные и каталоги (AWS Glue, Hive Metastore, Unity)?](#q26--метаданные-и-каталоги-aws-glue-hive-metastore-unity)

**Тренды**
- [Q27. (!) Какой формат выбрать в 2026?](#q27--какой-формат-выбрать-в-2026)
- [Q28. Streaming + Lakehouse?](#q28-streaming--lakehouse)

## Q1. (!) Что такое Data Lake?

**Data Lake** — централизованное хранилище для **сырых** данных в любом формате (structured, semi-structured, unstructured) на cheap object storage.

**Принципы:**
- **Schema-on-read** — структура определяется при чтении (vs schema-on-write в DWH)
- **Любые форматы** — JSON, CSV, Parquet, Avro, изображения, видео
- **Cheap storage** — S3 ~$0.023/GB/month vs Snowflake ~$23/TB
- **Decoupled storage and compute** — храним долго, обрабатываем когда нужно

**Применения:**
- Raw data archive (на случай если понадобится)
- ML training data (большие unstructured datasets)
- Data exploration (data scientists)
- ETL staging area

## Q2. (!) Data Lake vs Data Warehouse?

| Критерий | Data Lake | Data Warehouse |
|----------|-----------|----------------|
| Schema | On read | On write |
| Data | Raw, любые форматы | Structured, transformed |
| Storage cost | Очень дёшево ($/TB) | Дорого ($/TB) |
| Query speed | Slow без оптимизации | Fast (optimized) |
| Users | Data scientists, ML engineers | Analysts, BI |
| Updates | Часто append-only | INSERT/UPDATE/DELETE |
| Governance | Сложно | Easy |
| Tools | Spark, Trino, Athena | Snowflake, BQ, Redshift |
| Schema evolution | Гибко | Структурно |

**В 2024** — convergence: Lake может быть DWH (через Lakehouse), DWH может читать external tables на S3.

## Q3. (!) Что такое Lakehouse?

**Lakehouse** (термин от Databricks, 2020) — комбинация:
- **Storage** — cheap object storage (как Data Lake)
- **Features** — ACID, schema, indexing (как DWH)

**Реализация:** **table formats** поверх Parquet файлов:
- **Delta Lake** (Databricks)
- **Apache Iceberg** (Netflix)
- **Apache Hudi** (Uber)

**Преимущества:**
- Один storage для всего (нет дублирования lake → DWH)
- Cheap storage
- ACID, time travel, schema evolution
- Streaming + batch в одной системе

**Недостатки:**
- Меньше зрелости чем pure DWH
- Performance меньше чем optimized DWH (но близко)

## Q4. (!) S3, ADLS, GCS, HDFS — где хранят?

| Storage | Provider | Особенности |
|---------|----------|-------------|
| **S3** | AWS | Самое популярное, eventual consistency (с 2020 — strong) |
| **ADLS Gen2** | Azure | Hierarchical namespace |
| **GCS** | Google | Strong consistency |
| **HDFS** | On-prem (Hadoop) | Legacy, требует cluster |
| **MinIO** | Self-hosted (S3-compatible) | Open-source альтернатива |

**Cloud object storage** доминирует с **2010-х**. HDFS остаётся в legacy on-prem installations.

## Q5. Object storage vs HDFS — отличия?

| Критерий | Object Storage (S3) | HDFS |
|----------|---------------------|------|
| Тип | Key-value (object) | Файловая система |
| Hierarchy | Эмулируется через keys | Real directories |
| API | REST | POSIX-like |
| Scaling | Infinite (managed) | Через HDFS DataNodes |
| Cost | Low ($0.023/GB) | High (servers + maintenance) |
| Operations | append-only style (put new version) | full FS ops |
| Compute coupling | Decoupled | Часто coupled (data locality) |

**Object storage победил.** HDFS в 2024 — только legacy.

## Q6. (!) Parquet — что это и зачем?

**Apache Parquet** — columnar storage format. Оптимизирован для analytics queries.

**Особенности:**
- **Column-oriented** — лучше сжатие, быстрее scans колонок
- **Schema embedded** в файл
- **Predicate pushdown** — min/max statistics в footer для skip blocks
- **Compression** — Snappy (default), Gzip, Zstd, Brotli
- **Nested types** — structs, arrays, maps

```python
import pyarrow.parquet as pq
table = pq.read_table('data.parquet', columns=['id', 'name'])
# читает только эти колонки → быстро
```

**De facto стандарт** для analytical файлов в Lake.

## Q7. ORC?

**Apache ORC** (Optimized Row Columnar) — другой columnar format. Создан для **Hive**.

**Похож на Parquet:**
- Columnar
- Compression
- Predicate pushdown
- Schema embedded

**Отличия:**
- Чуть лучше compression и performance в некоторых случаях
- Меньше популярен (Parquet выиграл mind share)

В **Hive ecosystem** — ORC. **Везде остальном** — Parquet.

## Q8. Avro?

**Apache Avro** — **row-oriented** format с **schema evolution**.

**Особенности:**
- **Row-oriented** (vs Parquet/ORC columnar)
- **Schema embedded** или в Schema Registry
- **Schema evolution** — добавлять/удалять поля без перекомпиляции
- **Compact binary** representation

**Применения:**
- **Kafka messages** (с Confluent Schema Registry) — основное применение
- Streaming data
- RPC (Avro IDL)

**Не для analytics** queries — row-oriented плохо для column scans.

## Q9. (!) Сравнение Parquet vs ORC vs Avro?

| Критерий | Parquet | ORC | Avro |
|----------|---------|-----|------|
| Layout | Columnar | Columnar | Row |
| Use case | Analytics | Hive analytics | Streaming, RPC |
| Compression | Excellent | Excellent | Good |
| Schema evolution | Limited | Limited | **Excellent** |
| Read speed (column) | Fast | Fast | Slow (need full row) |
| Read speed (row) | Slow | Slow | Fast |
| Ecosystem | Spark, Pandas, Trino, ... | Hive, Pig | Kafka, Flink |

**Правило:**
- **Parquet** для analytics в Lake
- **Avro** для Kafka и streaming
- **ORC** в Hive-centric проектах

## Q10. (!) Delta Lake — что это?

**Delta Lake** — open-source table format от Databricks. Превращает S3/ADLS в **transactional store**.

**Особенности:**
- **ACID transactions** на object storage
- **Time travel** — query прошлые версии
- **Schema evolution** + enforcement
- **MERGE / UPSERT** — нет в plain Parquet
- **Streaming + batch** unified

**Структура:**
```
table/
  ├── _delta_log/             ← transaction log
  │   ├── 00000000.json       ← версия 0
  │   ├── 00000001.json       ← версия 1
  │   └── 00000010.checkpoint.parquet
  └── part-00000.parquet     ← actual data files
```

`_delta_log` — JSON-файлы операций. При query — читаем log + relevant Parquet.

## Q11. (!) Apache Iceberg — отличия?

**Apache Iceberg** (от Netflix, Apache top с 2020) — конкурент Delta.

**Особенности:**
- **Hidden partitioning** — partition по `day` без сохранения в data
- **Snapshot isolation** — каждое изменение = новый snapshot
- **Богатая metadata** — много avro файлов с stats
- **Schema evolution** — лучшая в classe
- **Multiple engines** — Spark, Trino, Flink, Snowflake (с 2024)

**Adoption:** Apple, Netflix, Stripe, Adobe, Pinterest.

В **2024** Iceberg — лидер по adoption в **non-Databricks** мире.

## Q12. (!) Apache Hudi?

**Apache Hudi** (от Uber, Apache с 2017) — самый старый из трёх.

**Особенности:**
- **Optimized для streaming inserts/updates** (UPSERT-первый)
- **Two storage types:**
  - **Copy-on-Write (CoW)** — переписывает Parquet файлы целиком (как Delta/Iceberg)
  - **Merge-on-Read (MoR)** — log + base files (быстрая запись, чтение медленнее)
- **Indexing** для UPSERT

**Применения:** где много update'ов — CDC pipelines, streaming aggregations.

Менее популярен, чем Delta/Iceberg в **2024**, но сильный в специфических use cases (стриминг с upserts).

## Q13. (!) Сравнение Delta vs Iceberg vs Hudi?

| Критерий | Delta Lake | Apache Iceberg | Apache Hudi |
|----------|------------|----------------|-------------|
| Создатель | Databricks | Netflix | Uber |
| Тип | Open-source | Apache | Apache |
| Главный engine | Spark / Databricks | Trino, Spark, Flink | Spark, Flink |
| ACID | Да | Да | Да |
| Time travel | Да | Да (snapshots) | Да |
| Schema evolution | Хорошее | **Лучшее** | Хорошее |
| Hidden partitioning | Нет | **Да** | — |
| Streaming inserts | Хорошо | Хорошо | **Лучше** |
| Multi-engine | Improving | **Excellent** | Хорошо |
| Adoption | Огромное (Databricks) | Очень растёт (Netflix, Apple) | Среднее (Uber-стек) |

**В 2024:**
- Если используешь Databricks — **Delta**
- Если multi-engine (Trino, Snowflake, Spark) — **Iceberg**
- Если streaming-heavy с upserts — **Hudi**

## Q14. (!) ACID transactions на S3 — как?

S3 — eventually consistent (был, с 2020 — strong consistency для reads после writes).

**Как ACID реализуется:**

1. **Atomic writes** — write новых файлов + atomic rename _delta_log entry
2. **Concurrency** — optimistic locking через version numbers в logs
3. **Consistency** — все readers видят один snapshot
4. **Isolation** — snapshot isolation
5. **Durability** — S3 itself даёт 11 nines

**Конфликт двух writers:**
```
Writer A: read version 5, hace changes → tries write version 6
Writer B: read version 5, hace changes → tries write version 6
Только один win'ит. Loser должен retry с new version.
```

Это **optimistic concurrency control** — без locks.

## Q15. (!) Time travel?

```sql
-- Delta Lake / Iceberg
SELECT * FROM my_table VERSION AS OF 5;
SELECT * FROM my_table TIMESTAMP AS OF '2025-04-15 10:00:00';

-- Spark
spark.read.format("delta").option("versionAsOf", 5).load(path)
```

**Применения:**
- **Audit** — что было вчера
- **Rollback** — accidentally удалил данные
- **Reproducibility** — ML training на той же версии
- **A/B сравнение** — текущая vs previous

**Цена:** хранение старых версий = больше storage. Регулярный `VACUUM` для cleanup.

## Q16. Schema evolution?

```sql
-- Add column
ALTER TABLE my_table ADD COLUMN new_field STRING;

-- Rename
ALTER TABLE my_table RENAME COLUMN old TO new;

-- Drop
ALTER TABLE my_table DROP COLUMN deprecated;

-- Type change
ALTER TABLE my_table ALTER COLUMN amount TYPE DECIMAL(20, 4);
```

**В Iceberg** — самая полная поддержка (включая reorder, drop, type promotion). **В Delta** — добавлено постепенно.

**Schema enforcement** — отклоняет writes несовместимых данных:

```python
df.write.mode("append").save("table")
# Если schema df ≠ table schema → error
```

## Q17. (!) Z-Order и data clustering?

**Z-Order** — multi-dimensional clustering. Сортирует данные так, чтобы **близкие values по нескольким columns** хранились рядом.

```sql
OPTIMIZE my_table ZORDER BY (user_id, country);
```

**Эффект:** queries по `user_id` И/ИЛИ `country` будут быстрее (skip больше irrelevant files).

В **Delta Lake** — Z-Order. В **Iceberg** — sort orders.

Полезно для **highly selective queries** на нескольких columns.

## Q18. Compaction (small files problem)?

Streaming inserts = много маленьких файлов → плохо для query (overhead на open/close).

**Compaction:**

```sql
OPTIMIZE my_table
WHERE date >= '2025-04-01';

-- Compact + Z-Order
OPTIMIZE my_table
WHERE date >= '2025-04-01'
ZORDER BY (user_id);
```

Создаёт меньшее число **больших** файлов из множества маленьких.

`VACUUM` — удаляет старые версии после compaction (после `retention period`).

## Q19. (!) Medallion architecture (Bronze, Silver, Gold)?

**Databricks Medallion** — стандартная архитектура Lakehouse:

```
[Source] → Bronze (raw, append-only)
           ↓ cleaning, dedupe
           Silver (validated, deduplicated)
           ↓ business logic, joins
           Gold (aggregated, BI-ready)
```

| Layer | Содержание |
|-------|-----------|
| **Bronze** | Raw данные, как пришли. Минимум transformations. |
| **Silver** | Cleaned, deduplicated, joined. Атомарные entities. |
| **Gold** | Business-level aggregations, BI marts. |

Аналог dbt staging/intermediate/marts, но в Lakehouse контексте.

## Q20. (!) Data Mesh — что это?

**Data Mesh** (Zhamak Dehghani, 2019) — sociotechnical парадигма для **decentralized** data architecture.

**4 принципа:**
1. **Domain ownership** — каждый business domain владеет своими данными
2. **Data as a product** — domain команда отвечает за качество, документацию, SLA
3. **Self-serve data platform** — централизованная инфраструктура для domain teams
4. **Federated computational governance** — общие стандарты, но local control

**Контраст с традиционным:**
- **Data Lake monolith** — одна центральная команда всё делает
- **Data Mesh** — distributed ownership, central platform

**Pros:** scaling, domain expertise, ownership.
**Cons:** сложно implement, требует maturity, легко превратить в data swamp.

В **2024** — модный термин, но **сложно delivered** на практике.

## Q21. Storage tier optimization (hot/warm/cold)?

**S3 storage classes:**

| Class | Latency | Cost (per GB/month) | Use case |
|-------|---------|---------------------|----------|
| **S3 Standard** | ms | $0.023 | Hot data |
| **S3 Standard-IA** | ms | $0.0125 | Less frequent |
| **S3 Glacier Instant** | ms | $0.004 | Archive с быстрым retrieval |
| **S3 Glacier Flexible** | min-hr | $0.0036 | Archive |
| **S3 Glacier Deep Archive** | 12 hr | $0.00099 | Compliance, rarely accessed |

**Lifecycle policies** — автоматическое перемещение между classes:

```json
{
  "Rules": [
    {"Days": 30, "StorageClass": "STANDARD_IA"},
    {"Days": 90, "StorageClass": "GLACIER_IR"},
    {"Days": 365, "StorageClass": "DEEP_ARCHIVE"}
  ]
}
```

Может сэкономить **80%** на cold data.

## Q22. (!) Какие engines работают с Lakehouse?

**Spark** — поддерживает все три (Delta, Iceberg, Hudi)
**Databricks** — нативная Delta
**Snowflake** — внешние tables Iceberg (с 2023+)
**BigQuery** — external tables на Iceberg
**Trino / Presto** — Iceberg, Delta, Hudi
**Flink** — Iceberg, Hudi (для streaming)
**Athena** (AWS) — Iceberg, Delta
**ClickHouse** — Iceberg (с 2024)
**DuckDB** — Iceberg, Delta

**Multi-engine** — главное преимущество Iceberg (самый "open" из трёх).

## Q23. Trino / Presto — для query на lake?

**Trino** (бывший PrestoSQL) — distributed SQL engine для **federated queries**.

```sql
-- Один query через несколько data sources
SELECT u.name, COUNT(o.id)
FROM postgres.public.users u
JOIN s3.warehouse.orders o ON u.id = o.user_id
GROUP BY u.name;
```

**Применения:**
- Query на Lake (Iceberg, Delta, Hudi, plain Parquet)
- Federated queries (mix BD, S3, Kafka)
- Ad-hoc analysis на huge datasets

Используется: Netflix, LinkedIn, Pinterest, Slack.

**Starburst** — managed Trino.

## Q24. (!) Small files problem?

Если в Lake много маленьких файлов (1-10 KB) — **performance degrade**:

- Каждый file требует metadata read
- Spark task overhead на каждый file
- Overhead на S3 LIST operations

**Причины:**
- Streaming с короткими intervals
- Partitioning слишком детальное (по часу × user_id)
- Delete updates создают small files

**Решения:**
- **Compaction** (`OPTIMIZE` в Delta, `rewrite_data_files` в Iceberg)
- **Partition стратегия** — не слишком granular
- **Buffering** при streaming (collect ~hour batches)

## Q25. Data swamp — что это?

**Data swamp** — Data Lake без governance, превратившийся в неуправляемое болото.

**Симптомы:**
- Никто не знает что в каких файлах
- Schemas меняются неконтролируемо
- Дубли, мусор, broken pipelines
- Нет lineage, нет documentation
- Compliance issues (где personal data?)

**Профилактика:**
- **Catalog** (Hive Metastore, AWS Glue, Unity Catalog) — registry tables и schemas
- **Data quality checks** (Great Expectations, dbt tests)
- **Lineage tracking** (OpenLineage, Marquez, Datahub)
- **Data contracts** между producers и consumers
- **Documentation** (не optional)

## Q26. (!) Метаданные и каталоги (AWS Glue, Hive Metastore, Unity)?

**Catalog** — registry таблиц/баз с их schemas, locations, partitions.

| Catalog | Vendor | Особенности |
|---------|--------|-------------|
| **Hive Metastore** | Apache | Старый стандарт, Java |
| **AWS Glue** | AWS | Managed, integration с S3 |
| **Unity Catalog** | Databricks | Modern, governance, lineage |
| **Iceberg REST Catalog** | Apache | Vendor-neutral spec |
| **Apache Polaris** | Snowflake | Iceberg catalog (с 2024) |
| **Nessie** | Project Nessie | Git-like для data |

```sql
-- Через catalog query
SELECT * FROM glue_catalog.my_db.my_table;
```

В **2024** — convergence на Iceberg REST Catalog как open standard.

## Q27. (!) Какой формат выбрать в 2026?

**Decision tree:**

```
Используешь Databricks?
  → Delta Lake

Хочешь max vendor-neutrality (Trino, Snowflake, Spark, Flink)?
  → Iceberg

Сильный focus на streaming upserts (CDC pipelines)?
  → Hudi

Pure batch analytics, simple use case?
  → Plain Parquet (ещё актуален!)
```

**Тренд 2024+:** **Iceberg** становится de facto standard в multi-vendor мирe. Snowflake, BigQuery, AWS Athena, Databricks — все добавили Iceberg support.

## Q28. Streaming + Lakehouse?

```python
# Spark Structured Streaming → Delta Lake
spark.readStream.format("kafka")...load() \
     .writeStream \
     .format("delta") \
     .option("checkpointLocation", "...") \
     .outputMode("append") \
     .start("delta://my_table")
```

**Lakehouse + Streaming:**
- **Delta Live Tables** (Databricks) — declarative streaming pipelines
- **Iceberg + Flink** — сильная пара для streaming
- **Hudi + Flink** — самый зрелый для CDC

**Преимущества:**
- Один storage для streaming + batch
- Low latency reads на streaming таблицах
- Time travel в streaming контексте

Это направление **главного развития** Lakehouse в **2024-2025**.

---

## See also

- [Data Warehousing](data-warehousing-interview.md) — alternative storage
- [Apache Spark](apache-spark-interview.md) — primary engine для Lakehouse
- [Apache Flink](apache-flink-interview.md) — streaming + Iceberg/Hudi
- [Apache Kafka](../messaging/kafka-interview.md) — source для streaming
- [dbt](dbt-interview.md) — может работать в Lakehouse контексте
- [Apache Airflow](apache-airflow-interview.md) — orchestration
- [Stream Processing](stream-processing-interview.md) — стрим данные → lake
- [PostgreSQL](../databases/postgresql-interview.md) — source для CDC
- [Микросервисы](../architecture/microservices-interview.md) — produce events → lake
- [Caching](../architecture/caching-strategies-interview.md) — для acceleration
- [Распределённые системы](../architecture/distributed-systems-interview.md) — concepts
