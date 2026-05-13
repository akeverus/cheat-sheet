---
title: "Вопросы на собеседовании: Data Lake и Lakehouse"
description: "Data Lake (S3, HDFS), форматы (Parquet, ORC, Avro), Lakehouse архитектура (Delta Lake, Apache Iceberg, Apache Hudi), ACID на S3, time travel, schema evolution, data mesh"
tags:
  - interview
  - data-engineering
  - data-lake-lakehouse-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Data Lake и Lakehouse"
  - "Data lake interview"
  - "Lakehouse interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Data Lake и Lakehouse`

**Data Lake** — хранилище **сырых** данных любого формата на cheap storage (S3, ADLS, HDFS). **Lakehouse** — комбинация: lake-простота + warehouse-фичи (ACID, schema, indexing). Реализуется через **Delta Lake** (Databricks), **Apache Iceberg** (Netflix → Apache), **Apache Hudi** (Uber → Apache).

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


> [!mcq]
> - [ ] Data Lake хранит только structured данные с pre-defined schema (schema-on-write) | ❌ ПОСЛЕДСТВИЕ: данные отклоняются при загрузке без схемы — ML training на raw logs невозможен, unstructured data теряется
> - [ ] Data Lake требует expensive compute постоянно включённым для хранения данных | ❌ ПОСЛЕДСТВИЕ: billing в десятки раз выше чем object storage; HDFS с data locality невозможен на managed cloud
> - [x] Data Lake = schema-on-read: сырые данные любого формата на cheap storage (S3 ~$0.023/GB), структура определяется при чтении, а не при записи | ✓ ПРИМЕНЯТЬ: raw archive, ML training data, data exploration без заранее известной схемы 📋 ПРАВИЛО: Lake = cheap storage + schema-on-read + любой формат 🔗 См. Q2
> - [ ] Data Lake = то же что Data Warehouse, но на Hadoop | ❌ ПОСЛЕДСТВИЕ: теряется ключевое преимущество — дешевизна и гибкость; HDFS требует кластеры vs S3 decoupled compute

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


> [!mcq]
> - [ ] Data Warehouse использует schema-on-read, а Data Lake — schema-on-write | ❌ ПОСЛЕДСТВИЕ: всё наоборот: загрузка в DWH требует заранее известной схемы (schema-on-write); Lake гибко читает raw данные при запросе
> - [ ] Data Lake быстрее Data Warehouse для BI analytics queries без дополнительной оптимизации | ❌ ПОСЛЕДСТВИЕ: plain Parquet на S3 без Lakehouse table format даёт slow full-scans; optimized DWH (Snowflake, Redshift) выигрывает по query speed
> - [x] Data Lake: raw данные + schema-on-read + cheap storage; DWH: structured + governed + schema-on-write + optimized query performance; Lake для ML/exploration, DWH для BI/reporting | ✓ ПРИМЕНЯТЬ: Lake для Data Scientists, DWH для Analysts 📋 ПРАВИЛО: Lake = дёшево + гибко; DWH = быстро + governed 🔗 См. Q3
> - [ ] Data Lake заменяет Data Warehouse для BI analysts и self-service reporting | ❌ ПОСЛЕДСТВИЕ: BI инструменты ожидают structured, governed данные; raw Lake без Gold layer и каталога не подходит для self-service analytics

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


> [!mcq]
> - [ ] Lakehouse — другое название managed Data Warehouse в cloud | ❌ ПОСЛЕДСТВИЕ: пропускается ключевая идея — Lakehouse реализован через table formats (Delta/Iceberg/Hudi) поверх cheap S3, а не через proprietary DWH storage
> - [ ] Lakehouse требует отдельного HDFS кластера для транзакционности | ❌ ПОСЛЕДСТВИЕ: весь Lakehouse — это table format metadata + обычный S3; HDFS устарел и не нужен
> - [x] Lakehouse = cheap object storage (как Lake) + ACID/schema/indexing (как DWH) через table formats Delta Lake, Iceberg, Hudi поверх Parquet; единый storage для batch + streaming | ✓ ПРИМЕНЯТЬ: когда нужен ACID + time travel на S3 без дублирования lake→DWH 📋 ПРАВИЛО: Lakehouse = cheap S3 + ACID + time travel поверх Parquet 🔗 См. Q10
> - [ ] Lakehouse жертвует ACID ради дешёвого хранилища | ❌ ПОСЛЕДСТВИЕ: ACID — ключевая фича Lakehouse через optimistic concurrency в transaction log; без ACID Lakehouse теряет смысл

## Q4. (!) S3, ADLS, GCS, HDFS — где хранят?

| Storage | Provider | Особенности |
|---------|----------|-------------|
| **S3** | AWS | Самое популярное, eventual consistency (с 2020 — strong) |
| **ADLS Gen2** | Azure | Hierarchical namespace |
| **GCS** | Google | Strong consistency |
| **HDFS** | On-prem (Hadoop) | Legacy, требует cluster |
| **MinIO** | Self-hosted (S3-compatible) | Open-source альтернатива |

**Cloud object storage** доминирует с **2010-х**. HDFS остаётся в legacy on-prem installations.


> [!mcq]
> - [ ] HDFS является предпочтительным storage для новых cloud Lakehouse проектов в 2024 | ❌ ПОСЛЕДСТВИЕ: HDFS требует выделенного Hadoop кластера с data locality; object storage (S3/ADLS/GCS) дешевле, масштабируется бесконечно и decoupled от compute
> - [ ] S3 до 2024 имел только eventual consistency, что делает его неприемлемым для ACID | ❌ ПОСЛЕДСТВИЕ: S3 имеет strong consistency с декабря 2020; Lakehouse table formats реализуют ACID поверх этой гарантии
> - [x] Cloud object storage (S3/ADLS/GCS) доминирует для Lakehouse: S3 — самый популярный с strong consistency с 2020, $0.023/GB; HDFS только в legacy on-prem | ✓ ПРИМЕНЯТЬ: S3 для AWS, ADLS Gen2 для Azure, GCS для GCP 📋 ПРАВИЛО: cloud object storage = бесконечный scale + managed + cheap; HDFS = legacy 🔗 См. Q5
> - [ ] MinIO — рекомендуемый production storage для Lakehouse в cloud | ❌ ПОСЛЕДСТВИЕ: MinIO — self-hosted S3-compatible, только без cloud или для dev/test; в production cloud первый выбор — native S3/ADLS/GCS

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


> [!mcq]
> - [ ] Object storage хуже HDFS потому что не поддерживает real directory hierarchy | ❌ ПОСЛЕДСТВИЕ: для Lakehouse directory hierarchy не нужна; key-value с эмулированными путями достаточно; преимущества object storage (infinite scale, cheap, managed) перевешивают
> - [ ] HDFS лучше для Spark queries из-за data locality (compute near data) | ❌ ПОСЛЕДСТВИЕ: cloud era опровергла это: decoupled storage+compute (S3 + Spark cluster) позволяет масштабировать независимо; data locality не критична при 10Gbps network
> - [x] Object storage (S3) выиграл у HDFS: decoupled compute+storage, infinite scale, $0.023/GB vs full HDFS cluster cost; HDFS в 2024 только в legacy on-prem | ✓ ПРИМЕНЯТЬ: S3 для всех новых data lake/lakehouse проектов 📋 ПРАВИЛО: S3 = дёшево + decoupled; HDFS = дорого + legacy 🔗 См. Q6
> - [ ] Object storage не поддерживает параллельное чтение несколькими Spark executors | ❌ ПОСЛЕДСТВИЕ: S3 поддерживает параллельные range reads; для этого Parquet разбивается на row groups — каждый executor читает свой range независимо

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


> [!mcq]
> - [ ] Parquet — row-oriented формат, оптимальный для streaming inserts в Kafka | ❌ ПОСЛЕДСТВИЕ: Parquet columnar; row-oriented = Avro или CSV; для streaming Parquet создаёт overhead — нужно читать всю строку при updates
> - [x] Parquet — columnar format: читает только нужные колонки, compression по колонкам, predicate pushdown через min/max в footer → быстрые analytics queries | ✓ ПРИМЕНЯТЬ: аналитические queries в Lake где выбираются конкретные колонки из широких таблиц 📋 ПРАВИЛО: Parquet = columnar + predicate pushdown = fast column scans 🔗 См. Q9
> - [ ] Parquet не поддерживает schema evolution — при изменении схемы нужно перезаписать все данные | ❌ ПОСЛЕДСТВИЕ: Parquet поддерживает добавление колонок; Lakehouse table formats (Iceberg) расширяют schema evolution ещё дальше
> - [ ] Parquet лучше Avro для Kafka сообщений из-за меньшего размера per message | ❌ ПОСЛЕДСТВИЕ: Avro row-oriented (меньше overhead per message), интегрирован с Confluent Schema Registry; Parquet хорош для batch analytics, плохо для per-message streaming

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


> [!mcq]
> - [ ] ORC более популярен чем Parquet в Spark/Databricks экосистеме в 2024 | ❌ ПОСЛЕДСТВИЕ: Parquet выиграл mind share и стал de facto стандартом; ORC используется преимущественно в Hive-centric проектах; Spark по умолчанию тоже пишет Parquet
> - [ ] ORC поддерживает schema evolution лучше чем Avro | ❌ ПОСЛЕДСТВИЕ: schema evolution у ORC ограничена как у Parquet; Avro специально разработан для excellent schema evolution в Kafka/streaming
> - [x] ORC (Optimized Row Columnar) создан для Hive; columnar как Parquet, чуть лучше compression в некоторых случаях, но меньше популярен в остальных экосистемах | ✓ ПРИМЕНЯТЬ: в Hive-centric проектах и Pig pipelines 📋 ПРАВИЛО: ORC = Hive ecosystem; для остального — Parquet 🔗 См. Q9
> - [ ] ORC поддерживает ACID транзакции сам по себе без Lakehouse table format | ❌ ПОСЛЕДСТВИЕ: Hive ACID поверх ORC ограничен; полноценный ACID поверх S3 — задача Delta/Iceberg/Hudi, не ORC формата

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


> [!mcq]
> - [ ] Avro — columnar формат, идеален для analytics queries с column scans | ❌ ПОСЛЕДСТВИЕ: Avro row-oriented; для analytics column scans нужен Parquet; Avro читает всю строку даже для одной колонки
> - [x] Avro — row-oriented format с excellent schema evolution; основное применение — Kafka messages с Confluent Schema Registry: producer может менять схему без breaking consumers | ✓ ПРИМЕНЯТЬ: Kafka messages, streaming pipelines, RPC через Avro IDL 📋 ПРАВИЛО: Avro = streaming + schema evolution; НЕ для analytics 🔗 См. Q9
> - [ ] Avro требует обязательного внешнего Schema Registry — без него работать не может | ❌ ПОСЛЕДСТВИЕ: Avro может embed schema в файл (self-describing); Schema Registry — опция для Kafka для centralized schema management, не обязательна
> - [ ] Avro лучше Parquet для batch analytics в Data Lake из-за меньшего размера | ❌ ПОСЛЕДСТВИЕ: Avro row-oriented — читает всю строку для каждой операции; для analytics (SELECT columns FROM 1TB) Parquet в 10-100x быстрее благодаря columnar layout

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


> [!mcq]
> - [ ] Avro лучше Parquet для batch analytics в Data Lake | ❌ ПОСЛЕДСТВИЕ: Avro row-oriented — читает всю строку даже для одной колонки; Parquet columnar в 10-100x быстрее для SELECT 3 из 100 колонок
> - [x] Parquet — columnar для analytics; Avro — row-oriented для Kafka+streaming с excellent schema evolution; ORC — columnar для Hive-centric проектов | ✓ ПРИМЕНЯТЬ: выбор формата по движку и use-case 📋 ПРАВИЛО: Parquet=analytics, Avro=streaming, ORC=Hive 🔗 См. Q7, Q8
> - [ ] ORC и Parquet взаимозаменяемы в любом стеке | ❌ ПОСЛЕДСТВИЕ: ORC оптимизирован для Hive; Parquet имеет лучшую поддержку в Spark, Trino, Pandas, DuckDB; замена ORC→Parquet в Hive потребует пересоздания таблиц
> - [ ] Для Kafka нужен Parquet — он поддерживает streaming schema evolution | ❌ ПОСЛЕДСТВИЕ: Parquet не поддерживает schema evolution без rewrite файлов; Avro с Confluent Schema Registry — стандарт для Kafka streaming

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


> [!mcq]
> - [ ] Delta Lake хранит транзакционный log в самих Parquet файлах | ❌ ПОСЛЕДСТВИЕ: Delta log хранится отдельно в `_delta_log/` как JSON-файлы операций; данные и log разделены
> - [ ] Delta Lake не поддерживает streaming — только batch Spark | ❌ ПОСЛЕДСТВИЕ: Delta Lake unified: поддерживает Structured Streaming (readStream/writeStream) наряду с batch; это ключевое преимущество над plain Parquet
> - [x] Delta Lake = ACID transactions + time travel + schema enforcement на S3/ADLS; _delta_log/ хранит JSON операций; MERGE/UPSERT без plain Parquet | ✓ ПРИМЕНЯТЬ: Data Lakehouse с Databricks или Open-Source Delta 📋 ПРАВИЛО: Delta = transactional Parquet с _delta_log journal 🔗 См. Q6
> - [ ] VACUUM в Delta Lake удаляет все исторические версии немедленно | ❌ ПОСЛЕДСТВИЕ: VACUUM удаляет файлы старше retention threshold (default 7 дней); time travel доступен в пределах retention; немедленное удаление требует threshold=0h (небезопасно)

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


> [!mcq]
> - [ ] Iceberg — это storage format для S3, заменяющий Parquet | ❌ ПОСЛЕДСТВИЕ: путаница уровней; Iceberg — table format поверх Parquet/ORC, не заменяет их
> - [x] Iceberg = open table format (Netflix→Apache) с hidden partitioning, snapshot isolation и multi-engine support (Spark, Trino, Flink, Snowflake) | ✓ ПРИМЕНЯТЬ: multi-vendor Lakehouse без lock-in на Databricks 📋 ПРАВИЛО: Iceberg = open standard для non-Databricks мира 🔗 См. Q12
> - [ ] Iceberg требует Databricks runtime для записи | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — Iceberg создан как vendor-neutral альтернатива Delta
> - [ ] Hidden partitioning в Iceberg означает что данные не партиционированы | ❌ ПОСЛЕДСТВИЕ: партиционирование есть, но скрыто от user — partition spec вычисляется автоматически (day(ts)), пользователь пишет фильтр по ts

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


> [!mcq]
> - [ ] Hudi и Delta — одно и то же, разные названия | ❌ ПОСЛЕДСТВИЕ: разные движки, разные форматы log; Hudi от Uber специализирован на UPSERT
> - [ ] Merge-on-Read (MoR) переписывает Parquet при каждом update | ❌ ПОСЛЕДСТВИЕ: это Copy-on-Write делает; MoR пишет в log, мержит при чтении — быстрая запись, медленнее чтение
> - [ ] Copy-on-Write в Hudi пропускает запись delta — оптимизация быстрой записи | ❌ ПОСЛЕДСТВИЕ: CoW наоборот переписывает Parquet файл целиком при update; быстрее чтение, медленнее запись
> - [x] Hudi (Uber, 2017) = UPSERT-первый table format с CoW и MoR storage types + индексами для streaming inserts/updates | ✓ ПРИМЕНЯТЬ: CDC pipelines, streaming aggregations с частыми update'ами 📋 ПРАВИЛО: Hudi = streaming-first table format с indexing 🔗 См. Q13

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


> [!mcq]
> - [ ] На Databricks лучше использовать Iceberg, на Snowflake — Delta | ❌ ПОСЛЕДСТВИЕ: ровно наоборот: Databricks → Delta (native), Snowflake → Iceberg (external tables since 2023)
> - [x] Databricks → Delta; multi-engine (Trino/Snowflake/Spark) → Iceberg; streaming с UPSERT (CDC) → Hudi | ✓ ПРИМЕНЯТЬ: при выборе table format под конкретный стек 📋 ПРАВИЛО: Delta=Databricks, Iceberg=open, Hudi=streaming-upsert 🔗 См. Q14
> - [ ] Все три формата идентичны по фичам — выбор не имеет значения | ❌ ПОСЛЕДСТВИЕ: разное partitioning (Iceberg hidden), разное merge (Hudi MoR), разный vendor-neutrality
> - [ ] Hudi — единственный с time travel | ❌ ПОСЛЕДСТВИЕ: все три поддерживают time travel; Delta через _delta_log, Iceberg через snapshots, Hudi через timeline

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


> [!mcq]
> - [x] ACID на S3 = atomic rename + optimistic concurrency control через version numbers в _delta_log; loser конфликта делает retry с новой версией | ✓ ПРИМЕНЯТЬ: concurrent writers на одну Delta/Iceberg таблицу 📋 ПРАВИЛО: OCC = read version → write next → conflict → retry 🔗 См. Q15
> - [ ] S3 поддерживает pessimistic locking — Delta использует это | ❌ ПОСЛЕДСТВИЕ: S3 не имеет locks; Delta использует optimistic concurrency (read-modify-write c version check)
> - [ ] ACID на S3 невозможен — eventually consistent | ❌ ПОСЛЕДСТВИЕ: с 2020 S3 strong consistency для read-after-write; ACID реализуется через transaction log
> - [ ] Concurrent writers в Delta всегда блокируют друг друга — нужен внешний lock | ❌ ПОСЛЕДСТВИЕ: optimistic concurrency без locks; конфликт детектится при commit, loser ретраит

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


> [!mcq]
> - [ ] Time travel хранит данные бесплатно — версии живут вечно | ❌ ПОСЛЕДСТВИЕ: каждая старая версия = дополнительный storage; VACUUM нужен для очистки после retention period
> - [ ] Time travel требует separate backup storage | ❌ ПОСЛЕДСТВИЕ: time travel работает на той же таблице через transaction log; backup не нужен
> - [ ] VERSION AS OF доступен только в Snowflake, в Delta/Iceberg нет | ❌ ПОСЛЕДСТВИЕ: оба Delta и Iceberg поддерживают VERSION AS OF и TIMESTAMP AS OF
> - [x] Time travel = SELECT ... VERSION AS OF N или TIMESTAMP AS OF '...' через transaction log; используется для audit, rollback, ML reproducibility | ✓ ПРИМЕНЯТЬ: восстановить случайно удалённое, тренировать ML на конкретной версии 📋 ПРАВИЛО: time travel = SQL поверх log + VACUUM управляет retention 🔗 См. Q16

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


> [!mcq]
> - [ ] Schema evolution в Lakehouse невозможен — нужно создавать новую таблицу | ❌ ПОСЛЕДСТВИЕ: ALTER TABLE ADD/RENAME/DROP COLUMN поддерживаются; пересоздание не нужно
> - [x] Schema evolution = ALTER TABLE ADD/RENAME/DROP/ALTER COLUMN; Iceberg даёт самую полную поддержку (reorder, type promotion); schema enforcement отклоняет несовместимые writes | ✓ ПРИМЕНЯТЬ: добавление новых полей в существующую таблицу без миграции 📋 ПРАВИЛО: Iceberg evolution > Delta > plain Parquet 🔗 См. Q17
> - [ ] Schema enforcement просто игнорирует лишние колонки — пишет что подошло | ❌ ПОСЛЕДСТВИЕ: enforcement отклоняет write с ошибкой; mergeSchema=true нужен для авто-слияния
> - [ ] RENAME COLUMN перезаписывает все данные на S3 | ❌ ПОСЛЕДСТВИЕ: в Iceberg/Delta это metadata-only операция — column ID не меняется

## Q17. (!) Z-Order и data clustering?

**Z-Order** — multi-dimensional clustering. Сортирует данные так, чтобы **близкие values по нескольким columns** хранились рядом.

```sql
OPTIMIZE my_table ZORDER BY (user_id, country);
```

**Эффект:** queries по `user_id` И/ИЛИ `country` будут быстрее (skip больше irrelevant files).

В **Delta Lake** — Z-Order. В **Iceberg** — sort orders.

Полезно для **highly selective queries** на нескольких columns.


> [!mcq]
> - [ ] Z-Order = просто сортировка по одной колонке | ❌ ПОСЛЕДСТВИЕ: ORDER BY делает single-column; Z-Order — multi-dimensional clustering (близкие значения по нескольким columns рядом)
> - [ ] Z-Order работает только на одной partition column | ❌ ПОСЛЕДСТВИЕ: партиционирование и Z-Order — разные вещи; Z-Order работает внутри партиций по non-partition columns
> - [x] Z-Order (Delta) / sort orders (Iceberg) = multi-dimensional clustering: queries по нескольким columns пропускают больше irrelevant файлов через data skipping | ✓ ПРИМЕНЯТЬ: highly selective queries по 2-3 columns на больших таблицах 📋 ПРАВИЛО: Z-Order = co-locate related rows для column pruning 🔗 См. Q18
> - [ ] Z-Order повышает write latency но не влияет на reads | ❌ ПОСЛЕДСТВИЕ: смысл именно в ускорении reads через file skipping; OPTIMIZE — фоновая операция

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


> [!mcq]
> - [ ] Compaction делается через DROP + INSERT всех данных | ❌ ПОСЛЕДСТВИЕ: это перепишет всё; OPTIMIZE мержит мелкие файлы инкрементально, VACUUM чистит старые версии после retention
> - [x] OPTIMIZE (Delta) / rewrite_data_files (Iceberg) объединяет много мелких файлов в большие; за streaming inserts → нужно регулярно | ✓ ПРИМЕНЯТЬ: после streaming-writes когда таблица фрагментируется на тысячи small files 📋 ПРАВИЛО: streaming → OPTIMIZE WHERE date >= ... + VACUUM 🔗 См. Q19
> - [ ] VACUUM удаляет файлы немедленно после compaction | ❌ ПОСЛЕДСТВИЕ: VACUUM уважает retention period (default 7 дней) — иначе time travel сломается
> - [ ] Small files не проблема — Parquet быстро открывается | ❌ ПОСЛЕДСТВИЕ: overhead на open/close, S3 LIST, Spark task per file; 10000 файлов по 1KB убивают performance

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


> [!mcq]
> - [ ] Bronze хранит готовые BI-отчёты, Gold — сырые данные | ❌ ПОСЛЕДСТВИЕ: ровно наоборот; Bronze = raw, Gold = business-aggregations
> - [x] Medallion = Bronze (raw, append-only) → Silver (cleaned, dedup, joined) → Gold (business aggregations, BI-ready); каждый слой — Delta таблицы | ✓ ПРИМЕНЯТЬ: structured Lakehouse pipeline в Databricks 📋 ПРАВИЛО: Bronze=raw, Silver=clean, Gold=marts (как dbt staging/intermediate/marts) 🔗 См. Q20
> - [ ] Silver и Gold — одно и то же, разные названия | ❌ ПОСЛЕДСТВИЕ: Silver — atomic cleaned entities, Gold — aggregated business metrics для BI
> - [ ] Medallion требует Databricks-only — на open Spark не работает | ❌ ПОСЛЕДСТВИЕ: это паттерн именования слоёв; работает на любом engine с Delta/Iceberg

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


> [!mcq]
> - [x] Data Mesh = decentralized data architecture с domain ownership, data as a product, self-serve platform и federated governance (Zhamak Dehghani, 2019) | ✓ ПРИМЕНЯТЬ: large org где central data team — bottleneck, domains зрелые 📋 ПРАВИЛО: Mesh = domain owns its data product + central platform 🔗 См. Q21
> - [ ] Data Mesh — это software для управления Lakehouse | ❌ ПОСЛЕДСТВИЕ: это socio-technical парадигма, не tool; реализуется на Iceberg/Delta + Unity/Glue + governance процессах
> - [ ] Data Mesh = одна central команда владеет всеми pipelines | ❌ ПОСЛЕДСТВИЕ: это data lake monolith — антипаттерн от которого Mesh отказывается
> - [ ] Data Mesh устраняет центральную инфраструктуру полностью | ❌ ПОСЛЕДСТВИЕ: остаётся self-serve platform team; federated governance задаёт стандарты

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


> [!mcq]
> - [ ] Storage tiers одинаковы по latency — отличаются только ценой | ❌ ПОСЛЕДСТВИЕ: Glacier Deep = 12h retrieval, Standard = ms; tier выбирается по access pattern + cost
> - [x] S3 storage classes (Standard, Standard-IA, Glacier Instant/Flexible/Deep) + lifecycle policies автоматом перемещают объекты по возрасту — экономия до 80% на cold data | ✓ ПРИМЕНЯТЬ: данные старше 30/90/365 дней → IA/Glacier/Deep Archive 📋 ПРАВИЛО: hot=ms+$$$, cold=hours+$ 🔗 См. Q22
> - [ ] Lifecycle policy надо триггерить руками из приложения | ❌ ПОСЛЕДСТВИЕ: S3 сам по cron перемещает объекты по правилам; код приложения не меняется
> - [ ] Glacier Deep Archive подходит для hot OLAP queries | ❌ ПОСЛЕДСТВИЕ: retrieval 12 часов — для compliance и rarely accessed, не для query

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


> [!mcq]
> - [ ] Только Spark может читать Delta/Iceberg/Hudi | ❌ ПОСЛЕДСТВИЕ: Trino, Flink, Snowflake, BigQuery, Athena, DuckDB — все поддерживают Iceberg; Delta поддерживается Spark/Trino/Athena
> - [x] Spark — все три; Databricks → Delta native; Snowflake/BigQuery → Iceberg external tables; Trino → все три; Flink → Iceberg+Hudi для streaming | ✓ ПРИМЕНЯТЬ: multi-engine архитектуры с одной physical таблицей 📋 ПРАВИЛО: Iceberg = широчайший engine support 🔗 См. Q23
> - [ ] DuckDB не поддерживает Lakehouse форматы | ❌ ПОСЛЕДСТВИЕ: DuckDB читает и Iceberg, и Delta — отлично для local analytics на cloud данных
> - [ ] BigQuery нельзя подключить к S3 — только GCS native tables | ❌ ПОСЛЕДСТВИЕ: BigQuery external tables работают с Iceberg на любом cloud storage

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


> [!mcq]
> - [ ] Trino — это столбцовая БД с custom storage | ❌ ПОСЛЕДСТВИЕ: Trino — distributed SQL query engine без storage; читает данные из external sources (S3, Postgres, Kafka)
> - [x] Trino (бывший PrestoSQL) = distributed SQL для federated queries: один JOIN через Postgres + S3 (Iceberg/Delta/Hudi) + Kafka в одном запросе | ✓ ПРИМЕНЯТЬ: ad-hoc analytics на huge datasets без ETL, multi-source JOIN 📋 ПРАВИЛО: Trino = SQL поверх любых connectors, no storage 🔗 См. Q24
> - [ ] Trino требует загрузки данных в свой кластер перед запросом | ❌ ПОСЛЕДСТВИЕ: Trino читает напрямую из source через connectors — никакого ETL не нужно
> - [ ] Trino поддерживает только Hive Metastore | ❌ ПОСЛЕДСТВИЕ: Iceberg, Delta, Hudi catalogs все работают; ~30+ connectors к разным data sources

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


> [!mcq]
> - [ ] Small files это не проблема — S3 не имеет open-overhead | ❌ ПОСЛЕДСТВИЕ: S3 LIST + GET на каждый файл создаёт network roundtrips; Spark per-task overhead умножается на число файлов
> - [ ] Решение — увеличить число executor cores | ❌ ПОСЛЕДСТВИЕ: не решает root cause; больше parallelism не помогает если bottleneck в metadata reads
> - [ ] Slow streaming inserts не создают small files | ❌ ПОСЛЕДСТВИЕ: каждый micro-batch пишет файл; короткий interval (1 min) → много мелких файлов в день
> - [x] Small files (1-10KB) = degrade performance из-за metadata overhead, S3 LIST и Spark per-file task; решение — OPTIMIZE/rewrite_data_files + правильное partitioning + buffering streaming writes | ✓ ПРИМЕНЯТЬ: после streaming-ingest когда число файлов растёт линейно 📋 ПРАВИЛО: target file size ~128MB-1GB; меньше = compaction, больше = repartition 🔗 См. Q25

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


> [!mcq]
> - [x] Data swamp = Data Lake без governance: schemas меняются хаотично, нет каталога/lineage/documentation, дубли/мусор/broken pipelines, compliance issues | ✓ ПРИМЕНЯТЬ: профилактика через Catalog (Glue/Unity) + Great Expectations + OpenLineage + data contracts 📋 ПРАВИЛО: Lake без governance = swamp; нужен catalog + lineage + tests 🔗 См. Q26
> - [ ] Data swamp — это новая архитектура замена Lakehouse | ❌ ПОСЛЕДСТВИЕ: swamp — антипаттерн, деградировавшее состояние Lake; не архитектура
> - [ ] Достаточно иметь S3 bucket — governance не нужна | ❌ ПОСЛЕДСТВИЕ: ровно это и приводит к swamp; нужен catalog, lineage, quality checks, contracts
> - [ ] Data lineage отключают чтобы не замедлять pipelines | ❌ ПОСЛЕДСТВИЕ: lineage (OpenLineage, Marquez, Datahub) не влияет на runtime; без него debugging и compliance ломаются

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


> [!mcq]
> - [ ] Hive Metastore — современный стандарт для Lakehouse | ❌ ПОСЛЕДСТВИЕ: Hive Metastore старый (Java, локальный); тренд — Iceberg REST Catalog (vendor-neutral)
> - [x] Catalog = registry схем/locations/partitions; варианты: Hive Metastore, AWS Glue (managed), Unity Catalog (Databricks), Iceberg REST Catalog (open), Apache Polaris, Nessie (git-like) | ✓ ПРИМЕНЯТЬ: catalog по экосистеме — Glue на AWS, Unity на Databricks, REST на multi-vendor 📋 ПРАВИЛО: catalog = single source of truth для tables 🔗 См. Q27
> - [ ] Unity Catalog работает только с Iceberg | ❌ ПОСЛЕДСТВИЕ: Unity native для Delta; добавляет support для Iceberg через Uniform
> - [ ] Catalog хранит данные сами — он заменяет S3 | ❌ ПОСЛЕДСТВИЕ: catalog хранит metadata (schema, location, partitions); данные остаются в object storage

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


> [!mcq]
> - [ ] Plain Parquet устарел — никогда не использовать в 2026 | ❌ ПОСЛЕДСТВИЕ: для simple batch analytics без UPSERT plain Parquet всё ещё актуален и дешевле table format overhead
> - [ ] Delta — единственный open standard | ❌ ПОСЛЕДСТВИЕ: Iceberg более open (multi-vendor support); Delta привязан к Databricks roadmap
> - [x] Databricks → Delta; multi-vendor (Trino+Snowflake+Spark+Flink) → Iceberg (de facto standard в 2026); streaming-CDC → Hudi; simple batch → plain Parquet | ✓ ПРИМЕНЯТЬ: при выборе нового формата под конкретный stack 📋 ПРАВИЛО: Iceberg = open default; Delta = Databricks; Hudi = streaming UPSERT 🔗 См. Q28
> - [ ] Hudi подходит для всего и устраняет нужду в Iceberg/Delta | ❌ ПОСЛЕДСТВИЕ: Hudi оптимизирован для UPSERT-heavy workloads; не лучший выбор для read-heavy multi-engine BI

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


> [!mcq]
> - [ ] Streaming в Lakehouse невозможен — только batch | ❌ ПОСЛЕДСТВИЕ: Delta/Iceberg/Hudi все поддерживают streaming reads/writes; Spark Structured Streaming + Delta стандарт
> - [ ] Streaming требует отдельной таблицы от batch | ❌ ПОСЛЕДСТВИЕ: главное преимущество Lakehouse — unified storage: одна таблица для streaming writes и batch reads
> - [ ] Hudi + Flink — самая слабая пара для CDC | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — Hudi+Flink самый зрелый для CDC благодаря MoR и indexing
> - [x] Lakehouse + streaming = unified storage: Delta Live Tables (Databricks), Iceberg+Flink, Hudi+Flink (CDC); один storage для streaming+batch, low latency reads, time travel | ✓ ПРИМЕНЯТЬ: real-time analytics поверх Lakehouse без отдельного hot store 📋 ПРАВИЛО: streaming-write → Lakehouse → batch+ad-hoc reads на той же таблице 🔗 См. See also

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
