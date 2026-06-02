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

**Data Lake** — централизованное хранилище для **сырых** данных в любом формате (структурированные, полуструктурированные, неструктурированные) на дешёвом object storage.

**Принципы:**
- **Schema-on-read** — структура определяется при чтении (в отличие от schema-on-write в DWH)
- **Любые форматы** — JSON, CSV, Parquet, Avro, изображения, видео
- **Дешёвое хранилище** — S3 ~$0.023/GB в месяц против Snowflake ~$23/TB
- **Разделение хранения и вычислений** — храним долго, обрабатываем когда нужно

**Применения:**
- Архив сырых данных (на случай, если понадобится)
- Данные для обучения ML (большие неструктурированные датасеты)
- Исследование данных (data scientists)
- Промежуточный слой для ETL (staging area)

## Q2. (!) Data Lake vs Data Warehouse?

| Критерий | Data Lake | Data Warehouse |
|----------|-----------|----------------|
| Схема | При чтении | При записи |
| Данные | Сырые, любые форматы | Структурированные, преобразованные |
| Стоимость хранения | Очень дёшево ($/TB) | Дорого ($/TB) |
| Скорость запросов | Медленно без оптимизации | Быстро (оптимизировано) |
| Пользователи | Data scientists, ML-инженеры | Аналитики, BI |
| Обновления | Чаще append-only | INSERT/UPDATE/DELETE |
| Управление (governance) | Сложно | Легко |
| Инструменты | Spark, Trino, Athena | Snowflake, BQ, Redshift |
| Эволюция схемы | Гибко | Жёстко структурно |

**В 2024** — сближение: Lake может быть DWH (через Lakehouse), DWH может читать external tables на S3.

## Q3. (!) Что такое Lakehouse?

**Lakehouse** (термин от Databricks, 2020) — комбинация:
- **Хранилище** — дешёвый object storage (как Data Lake)
- **Возможности** — ACID, схема, индексы (как DWH)

**Реализация:** **table formats** поверх Parquet-файлов:
- **Delta Lake** (Databricks)
- **Apache Iceberg** (Netflix)
- **Apache Hudi** (Uber)

**Преимущества:**
- Одно хранилище для всего (нет дублирования lake → DWH)
- Дешёвое хранилище
- ACID, time travel, эволюция схемы
- Streaming и batch в одной системе

**Недостатки:**
- Меньше зрелости, чем у чистого DWH
- Производительность ниже, чем у оптимизированного DWH (но близко)

## Q4. (!) S3, ADLS, GCS, HDFS — где хранят?

| Хранилище | Провайдер | Особенности |
|---------|----------|-------------|
| **S3** | AWS | Самое популярное, eventual consistency (с 2020 — strong) |
| **ADLS Gen2** | Azure | Иерархическое пространство имён (hierarchical namespace) |
| **GCS** | Google | Strong consistency |
| **HDFS** | On-prem (Hadoop) | Legacy, требует кластер |
| **MinIO** | Self-hosted (S3-совместимое) | Open-source альтернатива |

**Облачный object storage** доминирует с **2010-х**. HDFS остаётся в legacy on-prem инсталляциях.

## Q5. Object storage vs HDFS — отличия?

| Критерий | Object Storage (S3) | HDFS |
|----------|---------------------|------|
| Тип | Key-value (объекты) | Файловая система |
| Иерархия | Эмулируется через ключи | Настоящие директории |
| API | REST | POSIX-подобный |
| Масштабирование | Безграничное (managed) | Через HDFS DataNodes |
| Стоимость | Низкая ($0.023/GB) | Высокая (серверы + обслуживание) |
| Операции | Стиль append-only (кладём новую версию) | Полный набор файловых операций |
| Связь с вычислениями | Разделены | Часто связаны (data locality) |

**Object storage победил.** HDFS в 2024 — только legacy.

## Q6. (!) Parquet — что это и зачем?

**Apache Parquet** — колоночный (columnar) формат хранения. Оптимизирован для аналитических запросов.

**Особенности:**
- **Колоночная организация** — лучше сжатие, быстрее сканирование колонок
- **Схема встроена** в файл
- **Predicate pushdown** — min/max-статистика в footer для пропуска блоков
- **Сжатие** — Snappy (по умолчанию), Gzip, Zstd, Brotli
- **Вложенные типы** — structs, arrays, maps

```python
import pyarrow.parquet as pq
table = pq.read_table('data.parquet', columns=['id', 'name'])
# читает только эти колонки → быстро
```

**De facto стандарт** для аналитических файлов в Lake.

## Q7. ORC?

**Apache ORC** (Optimized Row Columnar) — ещё один колоночный формат. Создан для **Hive**.

**Похож на Parquet:**
- Колоночный
- Сжатие
- Predicate pushdown
- Встроенная схема

**Отличия:**
- В отдельных случаях чуть лучше сжатие и производительность
- Менее популярен (Parquet выиграл по узнаваемости)

В **экосистеме Hive** — ORC. **Везде остальном** — Parquet.

## Q8. Avro?

**Apache Avro** — **строчно-ориентированный** (row-oriented) формат с **эволюцией схемы**.

**Особенности:**
- **Строчная организация** (в отличие от колоночных Parquet/ORC)
- **Схема встроена** или хранится в Schema Registry
- **Эволюция схемы** — добавлять/удалять поля без перекомпиляции
- **Компактное бинарное** представление

**Применения:**
- **Сообщения Kafka** (с Confluent Schema Registry) — основное применение
- Потоковые данные
- RPC (Avro IDL)

**Не для аналитических** запросов — строчная организация плохо подходит для сканирования колонок.

## Q9. (!) Сравнение Parquet vs ORC vs Avro?

| Критерий | Parquet | ORC | Avro |
|----------|---------|-----|------|
| Раскладка | Колоночная | Колоночная | Строчная |
| Сценарий | Аналитика | Аналитика в Hive | Streaming, RPC |
| Сжатие | Отличное | Отличное | Хорошее |
| Эволюция схемы | Ограничена | Ограничена | **Отличная** |
| Скорость чтения (колонка) | Быстро | Быстро | Медленно (нужна вся строка) |
| Скорость чтения (строка) | Медленно | Медленно | Быстро |
| Экосистема | Spark, Pandas, Trino, ... | Hive, Pig | Kafka, Flink |

**Правило:**
- **Parquet** для аналитики в Lake
- **Avro** для Kafka и streaming
- **ORC** в проектах, завязанных на Hive

## Q10. (!) Delta Lake — что это?

**Delta Lake** — open-source table format от Databricks. Превращает S3/ADLS в **транзакционное хранилище**.

**Особенности:**
- **ACID-транзакции** на object storage
- **Time travel** — запросы к прошлым версиям
- **Эволюция схемы** + её проверка (enforcement)
- **MERGE / UPSERT** — нет в обычном Parquet
- **Streaming и batch** в единой модели

**Структура:**
```
table/
  ├── _delta_log/             ← transaction log
  │   ├── 00000000.json       ← версия 0
  │   ├── 00000001.json       ← версия 1
  │   └── 00000010.checkpoint.parquet
  └── part-00000.parquet     ← actual data files
```

`_delta_log` — JSON-файлы с операциями. При запросе читаем лог + относящиеся к делу Parquet-файлы.

## Q11. (!) Apache Iceberg — отличия?

**Apache Iceberg** (от Netflix, top-level в Apache с 2020) — конкурент Delta.

**Особенности:**
- **Скрытое партиционирование (hidden partitioning)** — партиция по `day` без сохранения в самих данных
- **Snapshot isolation** — каждое изменение = новый snapshot
- **Богатые метаданные** — много avro-файлов со статистикой
- **Эволюция схемы** — лучшая в классе
- **Множество движков** — Spark, Trino, Flink, Snowflake (с 2024)

**Внедрение:** Apple, Netflix, Stripe, Adobe, Pinterest.

В **2024** Iceberg — лидер по внедрению в мире **вне Databricks**.

## Q12. (!) Apache Hudi?

**Apache Hudi** (от Uber, в Apache с 2017) — самый старый из трёх.

**Особенности:**
- **Оптимизирован под streaming inserts/updates** (UPSERT в первую очередь)
- **Два типа хранения:**
  - **Copy-on-Write (CoW)** — переписывает Parquet-файлы целиком (как Delta/Iceberg)
  - **Merge-on-Read (MoR)** — log + base files (быстрая запись, чтение медленнее)
- **Индексирование** для UPSERT

**Применения:** там, где много обновлений — CDC-пайплайны, потоковые агрегации.

Менее популярен, чем Delta/Iceberg, в **2024**, но силён в специфических сценариях (стриминг с upserts).

## Q13. (!) Сравнение Delta vs Iceberg vs Hudi?

| Критерий | Delta Lake | Apache Iceberg | Apache Hudi |
|----------|------------|----------------|-------------|
| Создатель | Databricks | Netflix | Uber |
| Тип | Open-source | Apache | Apache |
| Главный движок | Spark / Databricks | Trino, Spark, Flink | Spark, Flink |
| ACID | Да | Да | Да |
| Time travel | Да | Да (snapshots) | Да |
| Эволюция схемы | Хорошая | **Лучшая** | Хорошая |
| Скрытое партиционирование | Нет | **Да** | — |
| Streaming inserts | Хорошо | Хорошо | **Лучше** |
| Поддержка многих движков | Растёт | **Отличная** | Хорошая |
| Внедрение | Огромное (Databricks) | Сильно растёт (Netflix, Apple) | Среднее (стек Uber) |

**В 2024:**
- Если используешь Databricks — **Delta**
- Если нужно много движков (Trino, Snowflake, Spark) — **Iceberg**
- Если упор на стриминг с upserts — **Hudi**

## Q14. (!) ACID transactions на S3 — как?

S3 был eventually consistent (с 2020 — strong consistency для чтений после записей).

**Как реализуется ACID:**

1. **Атомарные записи** — запись новых файлов + атомарное переименование записи в _delta_log
2. **Конкурентность** — оптимистичные блокировки через номера версий в логах
3. **Согласованность** — все читатели видят один snapshot
4. **Изоляция** — snapshot isolation
5. **Долговечность** — сам S3 даёт 11 девяток

**Конфликт двух писателей:**
```
Writer A: read version 5, hace changes → tries write version 6
Writer B: read version 5, hace changes → tries write version 6
Только один win'ит. Loser должен retry с new version.
```

Это **optimistic concurrency control** — без блокировок.

## Q15. (!) Time travel?

```sql
-- Delta Lake / Iceberg
SELECT * FROM my_table VERSION AS OF 5;
SELECT * FROM my_table TIMESTAMP AS OF '2025-04-15 10:00:00';

-- Spark
spark.read.format("delta").option("versionAsOf", 5).load(path)
```

**Применения:**
- **Аудит** — что было вчера
- **Откат** — случайно удалили данные
- **Воспроизводимость** — обучение ML на той же версии данных
- **A/B-сравнение** — текущая версия против предыдущей

**Цена:** хранение старых версий = больше места в хранилище. Регулярный `VACUUM` для очистки.

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

**В Iceberg** — самая полная поддержка (включая переупорядочивание, удаление, повышение типа). **В Delta** — добавлялась постепенно.

**Schema enforcement** — отклоняет записи несовместимых данных:

```python
df.write.mode("append").save("table")
# Если schema df ≠ table schema → error
```

## Q17. (!) Z-Order и data clustering?

**Z-Order** — многомерная кластеризация. Сортирует данные так, чтобы **близкие значения по нескольким колонкам** хранились рядом.

```sql
OPTIMIZE my_table ZORDER BY (user_id, country);
```

**Эффект:** запросы по `user_id` И/ИЛИ `country` станут быстрее (пропускается больше нерелевантных файлов).

В **Delta Lake** — Z-Order. В **Iceberg** — sort orders.

Полезно для **высокоселективных запросов** по нескольким колонкам.

## Q18. Compaction (small files problem)?

Streaming inserts = много маленьких файлов → плохо для запросов (накладные расходы на open/close).

**Compaction (уплотнение):**

```sql
OPTIMIZE my_table
WHERE date >= '2025-04-01';

-- Compact + Z-Order
OPTIMIZE my_table
WHERE date >= '2025-04-01'
ZORDER BY (user_id);
```

Создаёт меньшее число **крупных** файлов из множества мелких.

`VACUUM` — удаляет старые версии после уплотнения (по истечении `retention period`).

## Q19. (!) Medallion architecture (Bronze, Silver, Gold)?

**Databricks Medallion** — типовая архитектура Lakehouse:

```
[Source] → Bronze (raw, append-only)
           ↓ cleaning, dedupe
           Silver (validated, deduplicated)
           ↓ business logic, joins
           Gold (aggregated, BI-ready)
```

| Слой | Содержание |
|-------|-----------|
| **Bronze** | Сырые данные как есть. Минимум преобразований. |
| **Silver** | Очищенные, дедуплицированные, объединённые. Атомарные сущности. |
| **Gold** | Бизнес-агрегаты, витрины для BI. |

Аналог dbt staging/intermediate/marts, но в контексте Lakehouse.

## Q20. (!) Data Mesh — что это?

**Data Mesh** (Zhamak Dehghani, 2019) — социотехническая парадигма для **децентрализованной** архитектуры данных.

**4 принципа:**
1. **Владение доменом** — каждый бизнес-домен владеет своими данными
2. **Данные как продукт** — доменная команда отвечает за качество, документацию, SLA
3. **Self-serve data platform** — централизованная инфраструктура для доменных команд
4. **Федеративное управление (governance)** — общие стандарты, но локальный контроль

**Контраст с традиционным подходом:**
- **Монолитный Data Lake** — всё делает одна центральная команда
- **Data Mesh** — распределённое владение, центральная платформа

**Плюсы:** масштабируемость, доменная экспертиза, ответственность.
**Минусы:** сложно внедрить, требует зрелости, легко превратить в data swamp.

В **2024** — модный термин, но **на практике реализуется сложно**.

## Q21. Storage tier optimization (hot/warm/cold)?

**S3 storage classes:**

| Класс | Задержка | Стоимость (за GB/месяц) | Сценарий |
|-------|---------|---------------------|----------|
| **S3 Standard** | мс | $0.023 | Горячие данные |
| **S3 Standard-IA** | мс | $0.0125 | Менее частый доступ |
| **S3 Glacier Instant** | мс | $0.004 | Архив с быстрым извлечением |
| **S3 Glacier Flexible** | мин-часы | $0.0036 | Архив |
| **S3 Glacier Deep Archive** | 12 ч | $0.00099 | Комплаенс, доступ крайне редко |

**Lifecycle policies** — автоматическое перемещение между классами:

```json
{
  "Rules": [
    {"Days": 30, "StorageClass": "STANDARD_IA"},
    {"Days": 90, "StorageClass": "GLACIER_IR"},
    {"Days": 365, "StorageClass": "DEEP_ARCHIVE"}
  ]
}
```

Может сэкономить **80%** на холодных данных.

## Q22. (!) Какие engines работают с Lakehouse?

**Spark** — поддерживает все три (Delta, Iceberg, Hudi)
**Databricks** — нативно Delta
**Snowflake** — внешние таблицы Iceberg (с 2023+)
**BigQuery** — external tables на Iceberg
**Trino / Presto** — Iceberg, Delta, Hudi
**Flink** — Iceberg, Hudi (для стриминга)
**Athena** (AWS) — Iceberg, Delta
**ClickHouse** — Iceberg (с 2024)
**DuckDB** — Iceberg, Delta

**Работа с многими движками** — главное преимущество Iceberg (самый «открытый» из трёх).

## Q23. Trino / Presto — для query на lake?

**Trino** (бывший PrestoSQL) — распределённый SQL-движок для **федеративных запросов**.

```sql
-- Один query через несколько data sources
SELECT u.name, COUNT(o.id)
FROM postgres.public.users u
JOIN s3.warehouse.orders o ON u.id = o.user_id
GROUP BY u.name;
```

**Применения:**
- Запросы к Lake (Iceberg, Delta, Hudi, обычный Parquet)
- Федеративные запросы (смесь БД, S3, Kafka)
- Ad-hoc анализ на огромных датасетах

Используют: Netflix, LinkedIn, Pinterest, Slack.

**Starburst** — managed-версия Trino.

## Q24. (!) Small files problem?

Если в Lake много мелких файлов (1-10 KB) — **деградация производительности**:

- Каждый файл требует чтения метаданных
- Накладные расходы на Spark-таску для каждого файла
- Накладные расходы на S3 LIST-операции

**Причины:**
- Стриминг с короткими интервалами
- Слишком детальное партиционирование (по часу × user_id)
- Удаления и обновления порождают мелкие файлы

**Решения:**
- **Compaction** (`OPTIMIZE` в Delta, `rewrite_data_files` в Iceberg)
- **Стратегия партиционирования** — не слишком детальная
- **Буферизация** при стриминге (накапливать пакеты ~по часу)

## Q25. Data swamp — что это?

**Data swamp** — Data Lake без управления (governance), превратившийся в неуправляемое болото.

**Симптомы:**
- Никто не знает, что в каких файлах
- Схемы меняются бесконтрольно
- Дубли, мусор, сломанные пайплайны
- Нет lineage, нет документации
- Проблемы с комплаенсом (где персональные данные?)

**Профилактика:**
- **Каталог** (Hive Metastore, AWS Glue, Unity Catalog) — реестр таблиц и схем
- **Проверки качества данных** (Great Expectations, dbt tests)
- **Отслеживание lineage** (OpenLineage, Marquez, Datahub)
- **Контракты данных** между производителями и потребителями
- **Документация** (не опциональна)

## Q26. (!) Метаданные и каталоги (AWS Glue, Hive Metastore, Unity)?

**Каталог** — реестр таблиц/баз с их схемами, расположением, партициями.

| Каталог | Вендор | Особенности |
|---------|--------|-------------|
| **Hive Metastore** | Apache | Старый стандарт, Java |
| **AWS Glue** | AWS | Managed, интеграция с S3 |
| **Unity Catalog** | Databricks | Современный, governance, lineage |
| **Iceberg REST Catalog** | Apache | Vendor-neutral спецификация |
| **Apache Polaris** | Snowflake | Каталог Iceberg (с 2024) |
| **Nessie** | Project Nessie | Git-подобный для данных |

```sql
-- Через catalog query
SELECT * FROM glue_catalog.my_db.my_table;
```

В **2024** — сближение вокруг Iceberg REST Catalog как открытого стандарта.

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

**Тренд 2024+:** **Iceberg** становится de facto стандартом в мульти-вендорном мире. Snowflake, BigQuery, AWS Athena, Databricks — все добавили поддержку Iceberg.

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
- **Delta Live Tables** (Databricks) — декларативные потоковые пайплайны
- **Iceberg + Flink** — сильная пара для стриминга
- **Hudi + Flink** — самый зрелый вариант для CDC

**Преимущества:**
- Одно хранилище для streaming и batch
- Низколатентное чтение из потоковых таблиц
- Time travel в потоковом контексте

Это **главное направление развития** Lakehouse в **2024-2025**.

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
