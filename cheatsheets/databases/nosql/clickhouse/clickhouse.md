---
title: "ClickHouse"
description: "Кратко: ClickHouse - колоночная СУБД для аналитики и обработки больших данных."
tags:
  - databases
  - nosql
  - clickhouse
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# ClickHouse

Кратко: **ClickHouse** — колоночная СУБД для аналитики и обработки больших данных.

## Полезные ссылки

### Официальная документация
- [ClickHouse Documentation](https://clickhouse.com/docs)
- [ClickHouse Tutorial](https://clickhouse.com/docs/en/getting-started/tutorial)
- [ClickHouse SQL Reference](https://clickhouse.com/docs/en/sql-reference)

## Содержание

- [Введение в ClickHouse](#введение-в-clickhouse)
  - [Основные характеристики:](#основные-характеристики)
  - [Варианты использования:](#варианты-использования)
- [Установка и запуск](#установка-и-запуск)
  - [Установка ClickHouse](#установка-clickhouse)
- [Установка ClickHouse из репозитория (apt, ключ, пакеты clickhouse-server, clickhouse-client)](#установка-clickhouse-из-репозитория-apt-ключ-пакеты-clickhouse-server-clickhouse-client)
  - [Запуск ClickHouse](#запуск-clickhouse)
- [Запуск сервера](#запуск-сервера)
- [Запуск клиента](#запуск-клиента)
- [Или через HTTP](#или-через-http)
  - [Подключение](#подключение)
- [Локальное подключение](#локальное-подключение)
- [Удаленное подключение](#удаленное-подключение)
- [С паролем](#с-паролем)
- [Основные концепции](#основные-концепции)
  - [Колоночное хранилище](#колоночное-хранилище)
  - [Основные компоненты](#основные-компоненты)
- [Работа с базами данных](#работа-с-базами-данных)
- [Создание таблиц](#создание-таблиц)
  - [Базовая структура](#базовая-структура)
  - [Пример с более сложной структурой](#пример-с-более-сложной-структурой)
- [Движки таблиц (Table Engines)](#движки-таблиц-table-engines)
  - [MergeTree](#mergetree)
  - [ReplacingMergeTree](#replacingmergetree)
  - [SummingMergeTree](#summingmergetree)
  - [AggregatingMergeTree](#aggregatingmergetree)
  - [Distributed](#distributed)
  - [Memory](#memory)
- [Типы данных](#типы-данных)
  - [Числовые типы](#числовые-типы)
  - [Строковые типы](#строковые-типы)
  - [Дата и время](#дата-и-время)
  - [Сложные типы](#сложные-типы)
- [Вставка данных](#вставка-данных)
- [Запросы SELECT](#запросы-select)
  - [Базовые запросы](#базовые-запросы)
  - [Операторы WHERE](#операторы-where)
- [Агрегация](#агрегация)
  - [Агрегатные функции](#агрегатные-функции)
  - [Продвинутые агрегаты](#продвинутые-агрегаты)
- [Оконные функции](#оконные-функции)
- [Индексы](#индексы)
  - [Первичный ключ](#первичный-ключ)
  - [Вторичные индексы](#вторичные-индексы)
- [Партиционирование](#партиционирование)
- [Материализованные представления](#материализованные-представления)
- [Оптимизация запросов](#оптимизация-запросов)
  - [Рекомендации по оптимизации](#рекомендации-по-оптимизации)
  - [Анализ производительности](#анализ-производительности)
- [Best Practices](#лучшие-практики)

## Введение в ClickHouse

**ClickHouse** — это колоночная система управления базами данных (СУБД) с открытым исходным кодом, разработанная компанией **Yandex**. **ClickHouse** оптимизирован для аналитических запросов и обработки больших объемов данных.

### Основные характеристики:

- **Колоночное хранилище**: Данные хранятся по столбцам, что обеспечивает высокую скорость аналитических запросов
- **Высокая производительность**: Очень быстрые агрегации и аналитические запросы
- **Горизонтальная масштабируемость**: Поддержка кластеризации и шардирования
- **Сжатие данных**: Эффективное сжатие данных для экономии места
- **Векторизация**: Использование **SIMD** инструкций для ускорения вычислений
- **SQL-подобный синтаксис**: Привычный **SQL** синтаксис

### Варианты использования:

- **Аналитика в реальном времени**: Анализ больших объемов данных в реальном времени
- **Data Warehousing**: Хранилище данных для бизнес-аналитики
- **Логирование и мониторинг**: Хранение и анализ логов и метрик
- **IoT аналитика**: Обработка данных с устройств **IoT**
- **Анализ поведения пользователей**: Анализ событий и действий пользователей
- **Financial analytics**: Финансовая аналитика и отчетность

## Установка и запуск

### Установка ClickHouse

**Linux (**Ubuntu**/**Debian**):**
```bash
# Установка ClickHouse из репозитория (apt, ключ, пакеты clickhouse-server, clickhouse-client)
sudo apt-get install -y apt-transport-https ca-certificates dirmngr
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv E0C56BD4

echo "deb https://repo.clickhouse.com/deb stable main" | sudo tee /etc/apt/sources.list.d/clickhouse.list
sudo apt-get update
sudo apt-get install -y clickhouse-server clickhouse-client
```

**macOS:**
```bash
brew install clickhouse
```

**Docker:**
```bash
docker run -d -p 8123:8123 -p 9000:9000 --name clickhouse-server clickhouse/clickhouse-server
```

### Запуск ClickHouse

```bash
# Запуск сервера
sudo service clickhouse-server start

# Запуск клиента
clickhouse-client

# Или через HTTP
curl 'http://localhost:8123/?query=SELECT 1'
```

### Подключение

```bash
# Локальное подключение
clickhouse-client

# Удаленное подключение
clickhouse-client --host hostname --port 9000

# С паролем
clickhouse-client --host hostname --password
```

## Основные концепции

### Колоночное хранилище

**В отличие от строковых СУБД, **ClickHouse** хранит данные по столбцам:**

- **Преимущества**: Быстрые агрегации, эффективное сжатие, быстрые сканирования
- **Недостатки**: Медленные обновления и удаления (не предназначен для OLTP)

### Основные компоненты

- **Таблицы**: Хранят данные
- **Движки таблиц**: Определяют способ хранения и обработки данных
- **Партиции**: Логическое разделение данных
- **Индексы**: Ускоряют запросы (первичный ключ, вторичные индексы)
- **Материализованные представления**: Предварительно вычисленные агрегаты

## Работа с базами данных

```sql
-- Создание базы данных
CREATE DATABASE IF NOT EXISTS mydatabase;

-- Использование базы данных
USE mydatabase;

-- Просмотр баз данных
SHOW DATABASES;

-- Удаление базы данных
DROP DATABASE mydatabase;
```

## Создание таблиц

### Базовая структура

```sql
CREATE TABLE mytable
(
    id UInt32,
    name String,
    created_at DateTime,
    value Float64
)
ENGINE = MergeTree()
ORDER BY id;
```

### Пример с более сложной структурой

```sql
CREATE TABLE events
(
    event_id UUID,
    user_id UInt64,
    event_type String,
    event_time DateTime,
    properties String  -- JSON строка
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (user_id, event_time)
SETTINGS index_granularity = 8192;
```

## Движки таблиц (Table Engines)

Движок таблицы определяет способ хранения данных.

### MergeTree

**Основной движок для аналитики:**

```sql
CREATE TABLE merge_tree_table
(
    id UInt64,
    date Date,
    value String
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (id, date);
```

### ReplacingMergeTree

**Автоматически удаляет дубликаты при слиянии:**

```sql
CREATE TABLE replacing_table
(
    id UInt64,
    name String,
    version UInt64
)
ENGINE = ReplacingMergeTree(version)
ORDER BY id;
```

### SummingMergeTree

**Суммирует значения при слиянии:**

```sql
CREATE TABLE summing_table
(
    date Date,
    category String,
    amount UInt64
)
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (date, category);
```

### AggregatingMergeTree

**Хранит агрегированные состояния:**

```sql
CREATE TABLE aggregating_table
(
    date Date,
    key String,
    value AggregateFunction(sum, UInt64),
    count AggregateFunction(count)
)
ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (date, key);
```

### Distributed

**Распределенная таблица для кластера:**

```sql
CREATE TABLE distributed_table AS local_table
ENGINE = Distributed(cluster_name, database, local_table, rand());
```

### Memory

**Хранит данные в памяти (временные таблицы):**

```sql
CREATE TABLE memory_table
(
    id UInt32,
    value String
)
ENGINE = Memory;
```

## Типы данных

### Числовые типы

```sql
-- Целые числа
UInt8, UInt16, UInt32, UInt64  -- беззнаковые
Int8, Int16, Int32, Int64      -- знаковые

-- Числа с плавающей точкой
Float32, Float64

-- Фиксированная точка
Decimal32(s), Decimal64(s), Decimal128(s), Decimal256(s)
-- s - количество знаков после запятой

-- Примеры
CREATE TABLE numbers
(
    tiny UInt8,
    small UInt16,
    normal UInt32,
    big UInt64,
    price Decimal(10, 2)
);
```

### Строковые типы

```sql
-- Строки
String        -- произвольная длина
FixedString(N) -- фиксированная длина N

-- UUID
UUID

-- Примеры
CREATE TABLE strings
(
    name String,
    code FixedString(10),
    id UUID
);
```

### Дата и время

```sql
-- Дата и время
Date          -- дата (до дня)
Date32        -- расширенный диапазон дат
DateTime      -- дата и время (до секунды)
DateTime64    -- высокая точность (до наносекунд)

-- Примеры
CREATE TABLE dates
(
    date_col Date,
    datetime_col DateTime,
    precise_col DateTime64(3)  -- миллисекунды
);
```

### Сложные типы

```sql
-- Массивы
Array(T)      -- массив элементов типа T

-- Кортежи
Tuple(T1, T2, ...)

-- Nullable
Nullable(T)   -- значение может быть NULL

-- Enum
Enum8('value1' = 1, 'value2' = 2)
Enum16('value1' = 1, 'value2' = 2)

-- Map
Map(key, value)

-- Nested
Nested(name String, value Int32)

-- Примеры
CREATE TABLE complex
(
    tags Array(String),
    coordinates Tuple(Float64, Float64),
    status Nullable(String),
    properties Map(String, String)
);
```

## Вставка данных

```sql
-- Вставка одной строки
INSERT INTO mytable VALUES (1, 'Alice', now(), 100.5);

-- Вставка нескольких строк
INSERT INTO mytable VALUES
    (1, 'Alice', now(), 100.5),
    (2, 'Bob', now(), 200.3),
    (3, 'Charlie', now(), 150.7);

-- Вставка из SELECT
INSERT INTO mytable
SELECT id, name, created_at, value
FROM source_table
WHERE value > 100;

-- Вставка из файла
INSERT INTO mytable FROM INFILE '/path/to/file.csv'
FORMAT CSV;

-- Массовая вставка (рекомендуется)
INSERT INTO mytable FORMAT CSV
1,Alice,2024-01-11 10:00:00,100.5
2,Bob,2024-01-11 11:00:00,200.3
```

## Запросы SELECT

### Базовые запросы

```sql
-- Простой SELECT
SELECT * FROM mytable;

-- Выбор конкретных столбцов
SELECT id, name, value FROM mytable;

-- WHERE условия
SELECT * FROM mytable WHERE value > 100;

-- LIMIT
SELECT * FROM mytable LIMIT 10;

-- OFFSET
SELECT * FROM mytable LIMIT 10 OFFSET 20;

-- DISTINCT
SELECT DISTINCT name FROM mytable;

-- ORDER BY
SELECT * FROM mytable ORDER BY value DESC;

-- GROUP BY
SELECT category, SUM(amount)
FROM transactions
GROUP BY category;
```

### Операторы WHERE

```sql
-- Сравнение
WHERE value > 100
WHERE value >= 100
WHERE value < 100
WHERE value <= 100
WHERE value = 100
WHERE value != 100
WHERE value <> 100

-- IN / NOT IN
WHERE id IN (1, 2, 3)
WHERE id NOT IN (1, 2, 3)

-- LIKE
WHERE name LIKE '%Alice%'
WHERE name NOT LIKE '%Bob%'

-- BETWEEN
WHERE value BETWEEN 100 AND 200

-- IS NULL / IS NOT NULL
WHERE name IS NULL
WHERE name IS NOT NULL

-- Логические операторы
WHERE value > 100 AND category = 'A'
WHERE value > 100 OR category = 'B'
WHERE NOT (value < 50)
```

## Агрегация

### Агрегатные функции

```sql
-- Основные агрегаты
SELECT
    COUNT(*) as total,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount,
    MIN(amount) as min_amount,
    MAX(amount) as max_amount
FROM transactions;

-- Статистика
SELECT
    quantile(0.5)(value) as median,
    quantile(0.95)(value) as p95,
    stddevPop(value) as stddev
FROM mytable;

-- Уникальные значения
SELECT uniq(user_id) FROM events;
SELECT uniqExact(user_id) FROM events;  -- точный подсчет

-- Группировка
SELECT
    category,
    COUNT(*) as count,
    SUM(amount) as total
FROM transactions
GROUP BY category;

-- HAVING (фильтрация после GROUP BY)
SELECT
    category,
    SUM(amount) as total
FROM transactions
GROUP BY category
HAVING total > 1000;
```

### Продвинутые агрегаты

```sql
-- Группировка по нескольким полям
SELECT
    date,
    category,
    SUM(amount) as total
FROM transactions
GROUP BY date, category;

-- WITH ROLLUP
SELECT
    category,
    SUM(amount) as total
FROM transactions
GROUP BY category
WITH ROLLUP;

-- WITH CUBE
SELECT
    category,
    region,
    SUM(amount) as total
FROM transactions
GROUP BY category, region
WITH CUBE;

-- WITH TOTALS
SELECT
    category,
    SUM(amount) as total
FROM transactions
GROUP BY category
WITH TOTALS;
```

## Оконные функции

```sql
-- ROW_NUMBER
SELECT
    id,
    name,
    value,
    ROW_NUMBER() OVER (ORDER BY value DESC) as rank
FROM mytable;

-- RANK и DENSE_RANK
SELECT
    id,
    value,
    RANK() OVER (ORDER BY value DESC) as rank,
    DENSE_RANK() OVER (ORDER BY value DESC) as dense_rank
FROM mytable;

-- LAG и LEAD
SELECT
    date,
    value,
    LAG(value) OVER (ORDER BY date) as prev_value,
    LEAD(value) OVER (ORDER BY date) as next_value
FROM mytable;

-- PARTITION BY
SELECT
    category,
    date,
    value,
    SUM(value) OVER (PARTITION BY category ORDER BY date) as running_total
FROM transactions;
```

## Индексы

### Первичный ключ

**Первичный ключ определяется через **ORDER** `BY` в **MergeTree**:**

```sql
CREATE TABLE indexed_table
(
    id UInt64,
    date Date,
    value String
)
ENGINE = MergeTree()
ORDER BY (id, date);  -- первичный ключ
```

### Вторичные индексы

```sql
-- Индекс для скалярных значений
ALTER TABLE mytable ADD INDEX idx_value value TYPE minmax GRANULARITY 4;

-- Индекс для массивов
ALTER TABLE mytable ADD INDEX idx_tags tags TYPE bloom_filter GRANULARITY 1;

-- Индекс для строк (full-text search)
ALTER TABLE mytable ADD INDEX idx_name name TYPE tokenbf_v1(256, 2, 0) GRANULARITY 1;

-- Активация индексов
ALTER TABLE mytable MATERIALIZE INDEX idx_value;
```

## Партиционирование

```sql
-- Партиционирование по дате
CREATE TABLE partitioned_table
(
    id UInt64,
    date Date,
    value String
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(date)  -- партиция по году-месяцу
ORDER BY (id, date);

-- Партиционирование по выражению
CREATE TABLE partitioned_table2
(
    id UInt64,
    category String,
    value String
)
ENGINE = MergeTree()
PARTITION BY category
ORDER BY id;

-- Просмотр партиций
SELECT * FROM system.parts
WHERE table = 'partitioned_table';
```

## Материализованные представления

```sql
-- Создание материализованного представления
CREATE MATERIALIZED VIEW daily_stats_mv
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (date, category)
AS SELECT
    toDate(event_time) as date,
    category,
    count() as events_count,
    sum(value) as total_value
FROM events
GROUP BY date, category;

-- Данные автоматически агрегируются при вставке в events
```

## Оптимизация запросов

### Рекомендации по оптимизации

```sql
-- Используйте WHERE для фильтрации перед JOIN
SELECT * FROM large_table
WHERE date >= '2024-01-01'  -- фильтрация сначала
JOIN small_table USING (id);

-- Используйте LIMIT когда возможно
SELECT * FROM large_table
ORDER BY value DESC
LIMIT 100;

-- Используйте APPROX для больших данных
SELECT approx_percentile(0.95)(value) FROM huge_table;

-- Избегайте SELECT *
SELECT id, name FROM mytable;  -- вместо SELECT *

-- Используйте правильные типы данных
-- UInt32 вместо UInt64 когда достаточно
```

### Анализ производительности

```sql
-- Включение логирования запросов
SET send_logs_level = 'trace';

-- EXPLAIN
EXPLAIN SELECT * FROM mytable WHERE value > 100;

-- EXPLAIN синтаксис
EXPLAIN SYNTAX SELECT * FROM mytable WHERE value > 100;

-- План выполнения
EXPLAIN PLAN SELECT * FROM mytable WHERE value > 100;
```

## Лучшие практики

- **Движки таблиц: MergeTree** для основных данных; **ReplacingMergeTree** для дедупликации; выбирайте движок под сценарий (вставки, запросы, TTL).
- **Партиционирование и порядок:** партиционируйте по дате/времени для управления жизненным циклом; **ORDER** `BY` — по часто используемым фильтрам и группировкам; первичный ключ влияет на эффективность запросов.
- **Типы данных:** используйте наиболее узкие типы (UInt8, UInt16) где достаточно; **LowCardinality** для строк с малым числом уникальных значений; избегайте избыточных **String**.
- **Вставка данных:** батчируйте вставки (тысячи строк за раз); используйте асинхронные вставки и буферные таблицы для высокой нагрузки; не делайте частые мелкие вставки.
- **Запросы:** используйте фильтры по партициям и ключу; агрегируйте на уровне движка где возможно; мониторьте **slow query log** и оптимизируйте тяжёлые запросы.

> **Примечание**: Это базовая информация о **ClickHouse**. Для более детального изучения см. официальную документацию **ClickHouse**.

## См. также

- [[clickhouse-basics|ClickHouse: Основы колоночной аналитической базы данных]]
- [[clickhouse-indexes|ClickHouse: Индексы и оптимизация — Полное руководство по индексации и партиционированию]]
- [[clickhouse-integration|ClickHouse: Интеграции и экосистема — Подключение внешних систем и инструментов]]
- [[clickhouse-materialized-views|ClickHouse: Материализованные представления — Предварительно вычисленные агрегаты и трансформации]]
- [[clickhouse-performance|ClickHouse: Производительность — Полное руководство по оптимизации и тюнингу]]
