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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) Apache Hudi? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. (!) Сравнение Delta vs Iceberg vs Hudi? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) ACID transactions на S3 — как? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. (!) Time travel? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Schema evolution? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. (!) Z-Order и data clustering? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Z-Order** — multi-dimensional clustering. Сортирует данные так, чтобы **близкие values по нескольким columns** хранились рядом.

```sql
OPTIMIZE my_table ZORDER BY (user_id, country);
```

**Эффект:** queries по `user_id` И/ИЛИ `country` будут быстрее (skip больше irrelevant files).

В **Delta Lake** — Z-Order. В **Iceberg** — sort orders.

Полезно для **highly selective queries** на нескольких columns.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Compaction (small files problem)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Medallion architecture (Bronze, Silver, Gold)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. (!) Data Mesh — что это? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Storage tier optimization (hot/warm/cold)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. (!) Какие engines работают с Lakehouse? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. Trino / Presto — для query на lake? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. (!) Small files problem? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Data swamp — что это? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Метаданные и каталоги (AWS Glue, Hive Metastore, Unity)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. (!) Какой формат выбрать в 2026? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. Streaming + Lakehouse? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Apache Airflow](apache-airflow-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Apache Flink](apache-flink-interview.md)
- [Apache Spark](apache-spark-interview.md)
- [Data Warehousing](data-warehousing-interview.md)
- [dbt](dbt-interview.md)
- [Kafka Streams](kafka-streams-interview.md)
