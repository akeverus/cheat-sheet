---
title: "PostgreSQL: Тюнинг производительности"
description: "Полное руководство по оптимизации производительности PostgreSQL: настройка postgresql.conf, shared_buffers, work_mem, checkpoint, autovacuum, connection pooling, оптимизация запросов, EXPLAIN, индексы"
tags:
  - postgresql
  - performance
  - tuning
  - optimization
  - query-optimization
  - indexes
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-indexes.md"]
next: ["databases/postgres-monitoring.md", "databases/postgres-troubleshooting.md"]
updated: "2026-04-20"
related: ["databases/postgres-replication.md", "databases/postgres-admin.md"]
---

# PostgreSQL: Тюнинг производительности

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Performance Tips](https://www.postgresql.org/docs/current/performance-tips.html) — рекомендации по производительности

### См. также
- [[postgres-basics|postgres-basics.md]] — основы PostgreSQL
- [[postgres-monitoring|postgres-monitoring.md]] — мониторинг

## Содержание

- [Введение в оптимизацию PostgreSQL](#введение-в-оптимизацию-postgresql)
  - [Основные области оптимизации](#основные-области-оптимизации)
- [Настройка postgresql.conf](#настройка-postgresqlconf)
  - [Память (Memory Settings)](#память-memory-settings)
    - [shared_buffers](#shared_buffers)
    - [effective_cache_size](#effective_cache_size)
    - [work_mem](#work_mem)
    - [maintenance_work_mem](#maintenance_work_mem)
  - [WAL (Write-Ahead Log) Settings](#wal-write-ahead-log-settings)
    - [wal_buffers](#wal_buffers)
    - [checkpoint настройки](#checkpoint-настройки)
  - [Autovacuum настройки](#autovacuum-настройки)
  - [Connection Settings](#connection-settings)
- [Connection Pooling](#connection-pooling)
  - [PgBouncer](#pgbouncer)
    - [Установка PgBouncer](#установка-pgbouncer)
    - [Конфигурация PgBouncer](#конфигурация-pgbouncer)
    - [Режимы pooling](#режимы-pooling)
  - [pgpool-II](#pgpool-ii)
- [Оптимизация запросов](#оптимизация-запросов)
  - [EXPLAIN и EXPLAIN ANALYZE](#explain-и-explain-analyze)
    - [Базовое использование](#базовое-использование)
    - [Анализ плана выполнения](#анализ-плана-выполнения)
  - [Типы планов выполнения](#типы-планов-выполнения)
    - [1. Seq Scan (Sequential Scan)](#1-seq-scan-sequential-scan)
    - [2. Index Scan](#2-index-scan)
    - [3. Index Only Scan](#3-index-only-scan)
    - [4. Bitmap Heap Scan](#4-bitmap-heap-scan)
    - [5. Nested Loop](#5-nested-loop)
  - [Оптимизация JOIN](#оптимизация-join)
    - [Hash Join](#hash-join)
    - [Merge Join](#merge-join)
  - [Статистика и ANALYZE](#статистика-и-analyze)
    - [Обновление статистики](#обновление-статистики)
    - [Расширенная статистика](#расширенная-статистика)
  - [Оптимизация конкретных запросов](#оптимизация-конкретных-запросов)
    - [1. Оптимизация WHERE условий](#1-оптимизация-where-условий)
    - [2. Оптимизация ORDER BY](#2-оптимизация-order-by)
    - [3. Оптимизация LIMIT](#3-оптимизация-limit)
- [Индексы для производительности](#индексы-для-производительности)
  - [Типы индексов](#типы-индексов)
    - [1. B-tree (по умолчанию)](#1-b-tree-по-умолчанию)
    - [2. Hash](#2-hash)
    - [3. GIN (Generalized Inverted Index)](#3-gin-generalized-inverted-index)
    - [4. GiST (Generalized Search Tree)](#4-gist-generalized-search-tree)
    - [5. BRIN (Block Range Index)](#5-brin-block-range-index)
  - [Составные индексы](#составные-индексы)
  - [Частичные индексы](#частичные-индексы)
  - [Индексы на выражениях](#индексы-на-выражениях)
  - [Covering Indexes (PostgreSQL 11+)](#covering-indexes-postgresql-11)
- [Мониторинг производительности](#мониторинг-производительности)
  - [pg_stat_statements](#pg_stat_statements)
    - [Установка](#установка)
    - [Использование](#использование)
  - [pg_stat_activity](#pg_stat_activity)
  - [pg_stat_database](#pg_stat_database)
- [Лучшие практики](#лучшие-практики)
  - [Настройка сервера](#настройка-сервера)
  - [Оптимизация запросов](#оптимизация-запросов-1)
  - [Мониторинг](#мониторинг)
- [Решение проблем](#решение-проблем)
  - [Проблема: Медленные запросы](#проблема-медленные-запросы)
  - [Проблема: Высокое использование памяти](#проблема-высокое-использование-памяти)
  - [Проблема: Блокировки](#проблема-блокировки)
- [Advanced Performance Tuning](#advanced-performance-tuning)
  - [Query Optimization Techniques](#query-optimization-techniques)
    - [Materialized Views](#materialized-views)
    - [Common Table Expressions (CTE) Optimization](#common-table-expressions-cte-optimization)
    - [Window Functions Optimization](#window-functions-optimization)
  - [Parallel Query Execution](#parallel-query-execution)
    - [Настройка параллелизма](#настройка-параллелизма)
    - [Использование параллельных запросов](#использование-параллельных-запросов)
  - [Index Optimization Strategies](#index-optimization-strategies)
    - [Partial Indexes for Performance](#partial-indexes-for-performance)
    - [Expression Indexes](#expression-indexes)
    - [Composite Index Column Order](#composite-index-column-order)
  - [Connection Pooling Optimization](#connection-pooling-optimization)
    - [PgBouncer Advanced Configuration](#pgbouncer-advanced-configuration)
  - [Query Plan Optimization](#query-plan-optimization)
    - [Forcing Query Plans](#forcing-query-plans)
    - [Plan Hints (через расширения)](#plan-hints-через-расширения)
- [System-Level Optimization](#system-level-optimization)
  - [OS-Level Tuning](#os-level-tuning)
    - [Linux Kernel Parameters](#linux-kernel-parameters)
    - [I/O Scheduler](#io-scheduler)
  - [PostgreSQL Configuration Optimization](#postgresql-configuration-optimization)
    - [Memory Configuration Calculator](#memory-configuration-calculator)
- [Query Rewriting for Performance](#query-rewriting-for-performance)
  - [Optimizing Subqueries](#optimizing-subqueries)
  - [Optimizing EXISTS vs IN](#optimizing-exists-vs-in)
  - [Optimizing DISTINCT](#optimizing-distinct)
- [Advanced Indexing Strategies](#advanced-indexing-strategies)
  - [Index Maintenance](#index-maintenance)
  - [Index Bloat Management](#index-bloat-management)
  - [Index Usage Monitoring](#index-usage-monitoring)
- [Performance Testing](#performance-testing)
  - [Benchmarking Queries](#benchmarking-queries)
  - [Load Testing](#load-testing)
- [Real-World Optimization Examples](#real-world-optimization-examples)
  - [Example 1: E-Commerce Query Optimization](#example-1-e-commerce-query-optimization)
  - [Example 2: Reporting Query Optimization](#example-2-reporting-query-optimization)
- [Best Practices Summary](#best-practices-summary)
  - [Configuration](#configuration)
  - [Query Optimization](#query-optimization)
  - [Monitoring](#monitoring)
- [Advanced Query Optimization](#advanced-query-optimization)
  - [Partitioning for Performance](#partitioning-for-performance)
  - [Query Plan Caching](#query-plan-caching)
  - [Batch Processing Optimization](#batch-processing-optimization)
- [Storage Optimization](#storage-optimization)
  - [Tablespace Management](#tablespace-management)
  - [TOAST Optimization](#toast-optimization)
- [Advanced Monitoring](#advanced-monitoring)
  - [Custom Performance Metrics](#custom-performance-metrics)
  - [Performance Regression Detection](#performance-regression-detection)
- [Optimization Workflows](#optimization-workflows)
  - [Workflow 1: Optimizing Slow Query](#workflow-1-optimizing-slow-query)
  - [Workflow 2: Database-Wide Optimization](#workflow-2-database-wide-optimization)
- [Performance Tuning Checklist](#performance-tuning-checklist)
  - [Initial Setup](#initial-setup)
  - [Regular Maintenance](#regular-maintenance)
  - [Optimization Process](#optimization-process)

## Введение в оптимизацию PostgreSQL

Оптимизация производительности **PostgreSQL** — это комплексный процесс, включающий настройку параметров сервера, оптимизацию запросов, правильное использование индексов и мониторинг производительности.

### Основные области оптимизации

1. **Настройка сервера**: Параметры **postgresql.conf**
2. **Оптимизация запросов**: Анализ и улучшение **SQL** запросов
3. **Индексы**: Правильное создание и использование индексов
4. **Connection Pooling**: Управление подключениями
5. **Мониторинг**: Отслеживание производительности


## Настройка postgresql.conf

### Память (Memory Settings)

#### shared_buffers

`shared_buffers` определяет объем памяти, используемый **PostgreSQL** для кэширования данных.

**Примеры настройки **shared_buffers** для разных объёмов **RAM**:**

```conf
# Рекомендуемое значение: 25% от RAM для выделенного сервера
# Минимум: 128MB, Максимум: несколько GB
shared_buffers = 4GB

# Для сервера с 16GB RAM
shared_buffers = 4GB

# Для сервера с 8GB RAM
shared_buffers = 2GB
```

**Рекомендации:**
- Для выделенного сервера: 25% от **RAM**
- Для сервера с другими приложениями: 15-20% от **RAM**
- Не устанавливать более 40% от **RAM**

#### effective_cache_size

`effective_cache_size` - оценка памяти, доступной для кэширования операционной системой.

```conf
# Рекомендуемое значение: 50-75% от RAM
effective_cache_size = 12GB

# Для сервера с 16GB RAM
effective_cache_size = 12GB
```

#### work_mem

`work_mem` определяет память для операций сортировки и хеш-таблиц.

```conf
# Рекомендуемое значение: (RAM - shared_buffers) / (max_connections * 2)
# Пример: (16GB - 4GB) / (100 * 2) = 12GB / 200 = 60MB
work_mem = 64MB
```

**Важно:** `work_mem` умножается на количество операций в запросе, поэтому не устанавливайте слишком большое значение.

#### maintenance_work_mem

`maintenance_work_mem` используется для операций обслуживания (VACUUM, `CREATE` INDEX).

```conf
# Рекомендуемое значение: 1-2GB
maintenance_work_mem = 1GB
```

### WAL (Write-`Ahead` Log) Settings

#### wal_buffers

```conf
# Автоматическое определение (рекомендуется)
wal_buffers = -1

# Или явно указать (16MB обычно достаточно)
wal_buffers = 16MB
```

#### checkpoint настройки

```conf
# Интервал между checkpoint
checkpoint_timeout = 15min

# Максимальный размер WAL между checkpoint
max_wal_size = 4GB

# Минимальный размер WAL
min_wal_size = 1GB

# Компрессия WAL
wal_compression = on
```

### Autovacuum настройки

```conf
# Включить autovacuum
autovacuum = on

# Задержка между autovacuum запусками
autovacuum_naptime = 1min

# Порог для запуска autovacuum
autovacuum_vacuum_threshold = 50
autovacuum_analyze_threshold = 50

# Масштабный фактор
autovacuum_vacuum_scale_factor = 0.2
autovacuum_analyze_scale_factor = 0.1

# Параметры для autovacuum workers
autovacuum_max_workers = 3
autovacuum_work_mem = 256MB
```

### Connection Settings

```conf
# Максимальное количество подключений
max_connections = 100

# Для высоконагруженных систем используйте connection pooling
# и уменьшите max_connections
max_connections = 50
```

**Рекомендация:** Используйте **connection pooling** (PgBouncer) вместо увеличения `max_connections`.


## Connection Pooling

### PgBouncer

**PgBouncer** — легковесный **connection pooler** для **PostgreSQL**.

#### Установка PgBouncer

```bash
# Ubuntu/Debian
sudo apt-get install pgbouncer

# RHEL/CentOS
sudo yum install pgbouncer
```

#### Конфигурация PgBouncer

```ini
# /etc/pgbouncer/pgbouncer.ini
[databases]
mydb = host=localhost port=5432 dbname=mydb

[pgbouncer]
listen_addr = 127.0.0.1
listen_port = 6432
auth_type = md5
auth_file = /etc/pgbouncer/userlist.txt

# Pooling режимы
pool_mode = transaction  # или session, statement

# Размер пула
max_client_conn = 1000
default_pool_size = 25
min_pool_size = 5
reserve_pool_size = 5
```

#### Режимы pooling

1. **Session**: Подключение на всю сессию клиента
2. **Transaction**: Подключение на транзакцию (рекомендуется)
3. **Statement**: Подключение на каждый **statement**

### pgpool-II

**pgpool-II** — более функциональный **connection pooler** с дополнительными возможностями.


## Оптимизация запросов

### EXPLAIN и EXPLAIN ANALYZE

#### Базовое использование

```sql
-- Простой EXPLAIN
EXPLAIN SELECT * FROM users WHERE email = 'user@example.com';

-- EXPLAIN ANALYZE (выполняет запрос)
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'user@example.com';

-- EXPLAIN с форматом JSON
EXPLAIN (FORMAT JSON) SELECT * FROM users WHERE email = 'user@example.com';

-- EXPLAIN с буферами
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM users WHERE email = 'user@example.com';
```

#### Анализ плана выполнения

```sql
EXPLAIN ANALYZE
SELECT u.name, o.total
FROM users u
JOIN orders o ON u.id = o.user_id
WHERE u.email = 'user@example.com'
ORDER BY o.created_at DESC
LIMIT 10;
```

**Ключевые метрики:**
- **cost**: Стоимость операции (ниже = лучше)
- **actual time**: Фактическое время выполнения
- **rows**: Количество обработанных строк
- **loops**: Количество итераций

### Типы планов выполнения

#### 1. Seq Scan (Sequential Scan)

```sql
EXPLAIN SELECT * FROM users WHERE age > 30;
```

**Проблема:** Полное сканирование таблицы

**Решение:** Создать индекс

```sql
CREATE INDEX idx_users_age ON users(age);
```

#### 2. Index Scan

```sql
EXPLAIN SELECT * FROM users WHERE email = 'user@example.com';
```

**Хорошо:** Используется индекс

#### 3. Index Only Scan

```sql
EXPLAIN SELECT email FROM users WHERE email = 'user@example.com';
```

**Отлично:** Данные берутся только из индекса

#### 4. Bitmap Heap Scan

```sql
EXPLAIN SELECT * FROM users WHERE age > 30 AND city = 'Moscow';
```

**Хорошо:** Используется **bitmap** для объединения условий

#### 5. Nested Loop

```sql
EXPLAIN
SELECT u.name, o.total
FROM users u
JOIN orders o ON u.id = o.user_id;
```

**Проблема:** Может быть медленным для больших таблиц

**Решение:** Использовать **Hash Join** или **Merge Join**

### Оптимизация JOIN

#### Hash Join

```sql
-- Принудительно использовать Hash Join
SET enable_nestloop = off;
SET enable_mergejoin = off;

EXPLAIN ANALYZE
SELECT u.name, o.total
FROM users u
JOIN orders o ON u.id = o.user_id;
```

#### Merge Join

```sql
-- Принудительно использовать Merge Join
SET enable_nestloop = off;
SET enable_hashjoin = off;

EXPLAIN ANALYZE
SELECT u.name, o.total
FROM users u
JOIN orders o ON u.id = o.user_id
ORDER BY u.id;
```

### Статистика и ANALYZE

#### Обновление статистики

```sql
-- ANALYZE для всей базы данных
ANALYZE;

-- ANALYZE для конкретной таблицы
ANALYZE users;

-- ANALYZE с подробным выводом
ANALYZE VERBOSE users;

-- Увеличить точность статистики
ALTER TABLE users ALTER COLUMN email SET STATISTICS 1000;
ANALYZE users;
```

#### Расширенная статистика

```sql
-- Статистика для нескольких столбцов
CREATE STATISTICS user_order_stats (dependencies)
ON user_id, order_date
FROM orders;

ANALYZE orders;
```

### Оптимизация конкретных запросов

#### 1. Оптимизация WHERE условий

```sql
-- Плохо: функция в WHERE
SELECT * FROM users WHERE UPPER(email) = 'USER@EXAMPLE.COM';

-- Хорошо: индекс на выражение
CREATE INDEX idx_users_email_upper ON users(UPPER(email));
SELECT * FROM users WHERE UPPER(email) = 'USER@EXAMPLE.COM';
```

#### 2. Оптимизация ORDER `BY`

```sql
-- Плохо: сортировка без индекса
SELECT * FROM users ORDER BY created_at DESC LIMIT 10;

-- Хорошо: индекс для сортировки
CREATE INDEX idx_users_created_at ON users(created_at DESC);
SELECT * FROM users ORDER BY created_at DESC LIMIT 10;
```

#### 3. Оптимизация LIMIT

```sql
-- Использовать индекс для быстрого LIMIT
CREATE INDEX idx_users_created_at ON users(created_at DESC);
SELECT * FROM users ORDER BY created_at DESC LIMIT 10;
```


## Индексы для производительности

### Типы индексов

#### 1. B-tree (по умолчанию)

```sql
CREATE INDEX idx_users_email ON users(email);
```

**Использование:**
- Равенство и диапазоны
- Сортировка
- Уникальность

#### 2. Hash

```sql
CREATE INDEX idx_users_email_hash ON users USING hash(email);
```

**Использование:**
- Только равенство
- Быстрее **B-tree** для равенства

#### 3. GIN (Generalized `Inverted` Index)

```sql
CREATE INDEX idx_users_tags_gin ON users USING gin(tags);
```

**Использование:**
- Массивы
- Полнотекстовый поиск
- **JSONB**

#### 4. GiST (Generalized `Search` Tree)

```sql
CREATE INDEX idx_users_location_gist ON users USING gist(location);
```

**Использование:**
- Географические данные
- Полнотекстовый поиск

#### 5. BRIN (Block `Range` Index)

```sql
CREATE INDEX idx_orders_created_at_brin ON orders USING brin(created_at);
```

**Использование:**
- Большие таблицы с коррелированными данными
- Временные ряды

### Составные индексы

```sql
-- Индекс для нескольких столбцов
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);

-- Порядок столбцов важен!
-- Используется для: WHERE user_id = ? AND created_at > ?
-- НЕ используется для: WHERE created_at > ?
```

### Частичные индексы

```sql
-- Индекс только для активных пользователей
CREATE INDEX idx_users_active_email ON users(email) WHERE active = true;

-- Индекс только для недавних заказов
CREATE INDEX idx_orders_recent ON orders(created_at)
WHERE created_at > '2024-01-01';
```

### Индексы на выражениях

```sql
-- Индекс на выражение
CREATE INDEX idx_users_email_lower ON users(LOWER(email));

-- Использование
SELECT * FROM users WHERE LOWER(email) = 'user@example.com';
```

### Covering Indexes (PostgreSQL 11+)

```sql
-- Индекс, включающий дополнительные столбцы
CREATE INDEX idx_orders_user_date_total
ON orders(user_id, created_at)
INCLUDE (total);
```


## Мониторинг производительности

### pg_stat_statements

#### Установка

```sql
CREATE EXTENSION pg_stat_statements;
```

#### Использование

```sql
-- Топ запросов по времени выполнения
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time,
    stddev_exec_time
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;

-- Топ запросов по количеству вызовов
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time
FROM pg_stat_statements
ORDER BY calls DESC
LIMIT 10;

-- Сброс статистики
SELECT pg_stat_statements_reset();
```

### pg_stat_activity

```sql
-- Активные запросы
SELECT
    pid,
    usename,
    application_name,
    state,
    query_start,
    state_change,
    wait_event_type,
    wait_event,
    query
FROM pg_stat_activity
WHERE state = 'active';

-- Долгие запросы
SELECT
    pid,
    now() - query_start AS duration,
    query
FROM pg_stat_activity
WHERE state = 'active'
AND now() - query_start > interval '5 minutes';
```

### pg_stat_database

```sql
-- Статистика по базам данных
SELECT
    datname,
    numbackends,
    xact_commit,
    xact_rollback,
    blks_read,
    blks_hit,
    tup_returned,
    tup_fetched,
    tup_inserted,
    tup_updated,
    tup_deleted
FROM pg_stat_database
WHERE datname = 'mydb';
```


## Лучшие практики

### Настройка сервера

1. **Начните с базовых настроек**
2. **Мониторьте производительность**
3. **Корректируйте настройки постепенно**
4. **Тестируйте изменения**

### Оптимизация запросов

1. **Всегда используйте `EXPLAIN` ANALYZE**
2. **Создавайте индексы осознанно**
3. **Обновляйте статистику регулярно**
4. **Избегайте N+1 запросов**

### Мониторинг

1. **Настройте pg_stat_statements**
2. **Мониторьте активные запросы**
3. **Отслеживайте метрики базы данных**
4. **Настройте алерты**


## Решение проблем

### Проблема: Медленные запросы

**Диагностика:**
```sql
-- Найти медленные запросы
SELECT query, mean_exec_time, calls
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

**Решение:**
- Создать индексы
- Оптимизировать запросы
- Обновить статистику

### Проблема: Высокое использование памяти

**Диагностика:**
```sql
-- Проверить использование памяти
SELECT * FROM pg_stat_activity
WHERE state = 'active';
```

**Решение:**
- Уменьшить **work_mem**
- Использовать **connection pooling**
- Оптимизировать запросы

### Проблема: Блокировки

**Диагностика:**
```sql
-- Проверить блокировки
SELECT * FROM pg_locks WHERE NOT granted;
```

**Решение:**
- Оптимизировать транзакции
- Использовать меньшие транзакции
- Проверить **deadlocks**

## Advanced Performance Tuning

### Query Optimization Techniques

#### Materialized Views

```sql
-- Создать материализованное представление
CREATE MATERIALIZED VIEW user_order_summary AS
SELECT
    u.id AS user_id,
    u.name,
    COUNT(o.id) AS order_count,
    SUM(o.total) AS total_amount,
    MAX(o.created_at) AS last_order_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;

-- Создать индекс на материализованном представлении
CREATE INDEX idx_user_order_summary_user_id ON user_order_summary(user_id);

-- Обновить материализованное представление
REFRESH MATERIALIZED VIEW CONCURRENTLY user_order_summary;

-- Использовать в запросах
SELECT * FROM user_order_summary WHERE user_id = 1;
```

#### Common Table Expressions (CTE) Optimization

```sql
-- CTE может быть материализован
WITH RECURSIVE user_hierarchy AS (
    SELECT id, name, parent_id, 1 AS level
    FROM users
    WHERE parent_id IS NULL

    UNION ALL

    SELECT u.id, u.name, u.parent_id, uh.level + 1
    FROM users u
    JOIN user_hierarchy uh ON u.parent_id = uh.id
)
SELECT * FROM user_hierarchy;

-- Использовать MATERIALIZED для больших CTE
WITH MATERIALIZED large_cte AS (
    SELECT * FROM large_table WHERE condition = true
)
SELECT * FROM large_cte;
```

#### Window Functions Optimization

```sql
-- Оптимизированные window functions
SELECT
    id,
    name,
    total,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
FROM orders
WHERE user_id = 1;

-- Создать индекс для window functions
CREATE INDEX idx_orders_user_created ON orders(user_id, created_at DESC);
```

### Parallel Query Execution

#### Настройка параллелизма

```conf
# postgresql.conf
max_parallel_workers_per_gather = 4
max_parallel_workers = 8
max_parallel_maintenance_workers = 2
parallel_setup_cost = 1000.0
parallel_tuple_cost = 0.01
min_parallel_table_scan_size = 8MB
min_parallel_index_scan_size = 512KB
```

#### Использование параллельных запросов

```sql
-- Запрос с параллельным выполнением
EXPLAIN ANALYZE
SELECT
    user_id,
    SUM(total) AS total_amount
FROM orders
WHERE created_at > NOW() - INTERVAL '1 month'
GROUP BY user_id;

-- Принудительно использовать параллелизм
SET max_parallel_workers_per_gather = 4;
SET parallel_setup_cost = 0;
```

### Index Optimization Strategies

#### Partial Indexes for Performance

```sql
-- Индекс только для активных записей
CREATE INDEX idx_users_active_email ON users(email)
WHERE active = true AND deleted_at IS NULL;

-- Индекс для недавних данных
CREATE INDEX idx_orders_recent_user ON orders(user_id, created_at)
WHERE created_at > NOW() - INTERVAL '1 year';
```

#### Expression Indexes

```sql
-- Индекс на функцию
CREATE INDEX idx_users_email_lower ON users(LOWER(email));

-- Индекс на выражение с условием
CREATE INDEX idx_users_name_trgm ON users USING gin(name gin_trgm_ops)
WHERE name IS NOT NULL;
```

#### Composite Index Column Order

```sql
-- Правильный порядок столбцов в составном индексе
-- Используется для: WHERE user_id = ? AND created_at > ?
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);

-- НЕ используется для: WHERE created_at > ?
-- Для этого нужен отдельный индекс:
CREATE INDEX idx_orders_created_at ON orders(created_at);
```

### Connection Pooling Optimization

#### PgBouncer Advanced Configuration

```ini
# /etc/pgbouncer/pgbouncer.ini
[databases]
mydb = host=localhost port=5432 dbname=mydb

[pgbouncer]
listen_addr = 0.0.0.0
listen_port = 6432
auth_type = md5
auth_file = /etc/pgbouncer/userlist.txt

# Pool settings
pool_mode = transaction
max_client_conn = 1000
default_pool_size = 25
min_pool_size = 5
reserve_pool_size = 5
reserve_pool_timeout = 3

# Performance
server_round_robin = 1
server_lifetime = 3600
server_idle_timeout = 600
query_wait_timeout = 120
query_timeout = 0
client_idle_timeout = 0
client_idle_timeout = 0

# Logging
log_connections = 1
log_disconnections = 1
log_pooler_errors = 1
stats_period = 60
```

### Query Plan Optimization

#### Forcing Query Plans

```sql
-- Отключить определенные типы планов
SET enable_nestloop = off;
SET enable_hashjoin = on;
SET enable_mergejoin = on;

-- Выполнить запрос
EXPLAIN ANALYZE
SELECT u.name, o.total
FROM users u
JOIN orders o ON u.id = o.user_id;

-- Вернуть настройки
RESET enable_nestloop;
RESET enable_hashjoin;
RESET enable_mergejoin;
```

#### Plan Hints (через расширения)

```sql
-- Использование pg_hint_plan
CREATE EXTENSION pg_hint_plan;

-- Указать план выполнения
/*+ HashJoin(u o) SeqScan(u) */
SELECT u.name, o.total
FROM users u
JOIN orders o ON u.id = o.user_id;
```

## System-Level Optimization

### OS-Level Tuning

#### Linux Kernel Parameters

```bash
# /etc/sysctl.conf
# Увеличить shared memory
kernel.shmmax = 68719476736
kernel.shmall = 4294967296

# Увеличить количество файловых дескрипторов
fs.file-max = 65536

# Оптимизация сетевых параметров
net.core.rmem_max = 16777216
net.core.wmem_max = 16777216
net.ipv4.tcp_rmem = 4096 87380 16777216
net.ipv4.tcp_wmem = 4096 65536 16777216

# Применить изменения
sysctl -p
```

#### I/O Scheduler

```bash
# Проверить текущий scheduler
cat /sys/block/sda/queue/scheduler

# Установить deadline scheduler (лучше для БД)
echo deadline > /sys/block/sda/queue/scheduler
```

### PostgreSQL Configuration Optimization

#### Memory Configuration Calculator

```sql
-- Функция для расчета оптимальных настроек памяти
CREATE OR REPLACE FUNCTION calculate_memory_settings(total_ram_gb NUMERIC)
RETURNS TABLE(
    setting_name TEXT,
    recommended_value TEXT,
    description TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        'shared_buffers'::TEXT,
        format('%sGB', ROUND(total_ram_gb * 0.25, 2)),
        '25% of total RAM'::TEXT

    UNION ALL

    SELECT
        'effective_cache_size'::TEXT,
        format('%sGB', ROUND(total_ram_gb * 0.75, 2)),
        '75% of total RAM'::TEXT

    UNION ALL

    SELECT
        'work_mem'::TEXT,
        format('%sMB', ROUND((total_ram_gb * 1024 - total_ram_gb * 0.25 * 1024) / 200, 0)),
        'Based on max_connections = 100'::TEXT

    UNION ALL

    SELECT
        'maintenance_work_mem'::TEXT,
        format('%sGB', LEAST(ROUND(total_ram_gb * 0.1, 2), 2)),
        '10% of RAM or 2GB max'::TEXT;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM calculate_memory_settings(16);
```

## Query Rewriting for Performance

### Optimizing Subqueries

```sql
-- Плохо: коррелированный подзапрос
SELECT
    id,
    name,
    (SELECT COUNT(*) FROM orders WHERE user_id = users.id) AS order_count
FROM users;

-- Хорошо: JOIN с агрегацией
SELECT
    u.id,
    u.name,
    COALESCE(o.order_count, 0) AS order_count
FROM users u
LEFT JOIN (
    SELECT user_id, COUNT(*) AS order_count
    FROM orders
    GROUP BY user_id
) o ON u.id = o.user_id;
```

### Optimizing EXISTS vs `IN`

```sql
-- EXISTS обычно быстрее для больших наборов
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.total > 1000
);

-- IN может быть быстрее для маленьких наборов
SELECT * FROM users
WHERE id IN (1, 2, 3, 4, 5);
```

### Optimizing DISTINCT

```sql
-- Плохо: DISTINCT на большом результате
SELECT DISTINCT user_id FROM orders;

-- Хорошо: GROUP BY (может использовать индекс)
SELECT user_id FROM orders GROUP BY user_id;

-- Или использовать EXISTS
SELECT id FROM users u
WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id);
```

## Advanced Indexing Strategies

### Index Maintenance

```sql
-- Перестроить индекс для оптимизации
REINDEX INDEX CONCURRENTLY idx_users_email;

-- Перестроить все индексы таблицы
REINDEX TABLE CONCURRENTLY users;

-- Перестроить все индексы базы данных
REINDEX DATABASE CONCURRENTLY mydb;
```

### Index Bloat Management

```sql
-- Проверить раздувание индексов
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    idx_scan AS index_scans
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;

-- Перестроить раздутые индексы
REINDEX INDEX CONCURRENTLY idx_large_index;
```

### Index Usage Monitoring

```sql
-- Найти неиспользуемые индексы
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    idx_scan AS index_scans
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
AND idx_scan = 0
ORDER BY pg_relation_size(indexrelid) DESC;

-- Найти индексы с низким использованием
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    ROUND(100.0 * idx_scan / NULLIF(pg_relation_size(indexrelid), 0), 2) AS usage_ratio
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
AND idx_scan > 0
ORDER BY usage_ratio;
```

## Performance Testing

### Benchmarking Queries

```sql
-- Функция для бенчмарка запросов
CREATE OR REPLACE FUNCTION benchmark_query(
    query_text TEXT,
    iterations INTEGER DEFAULT 10
)
RETURNS TABLE(
    iteration INTEGER,
    execution_time_ms NUMERIC
) AS $$
DECLARE
    i INTEGER;
    start_time TIMESTAMP;
    end_time TIMESTAMP;
BEGIN
    FOR i IN 1..iterations LOOP
        start_time := clock_timestamp();
        EXECUTE query_text;
        end_time := clock_timestamp();

        RETURN QUERY
        SELECT
            i,
            EXTRACT(EPOCH FROM (end_time - start_time)) * 1000;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT
    AVG(execution_time_ms) AS avg_time,
    MIN(execution_time_ms) AS min_time,
    MAX(execution_time_ms) AS max_time,
    STDDEV(execution_time_ms) AS stddev_time
FROM benchmark_query('SELECT * FROM users WHERE email = ''test@example.com''', 100);
```

### Load Testing

```sql
-- Использовать pgbench для нагрузочного тестирования
-- Создать тестовую базу данных
createdb pgbench_test

-- Инициализировать данные
pgbench -i -s 100 pgbench_test

-- Запустить тест
pgbench -c 10 -j 2 -T 60 pgbench_test

-- Тест с записанными скриптами
pgbench -c 10 -j 2 -T 60 -f custom_script.sql pgbench_test
```

## Real-World Optimization Examples

### Example 1: E-Commerce Query Optimization

```sql
-- Исходный медленный запрос
SELECT
    u.id,
    u.name,
    u.email,
    COUNT(o.id) AS order_count,
    SUM(o.total) AS total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at > '2024-01-01'
GROUP BY u.id, u.name, u.email
HAVING COUNT(o.id) > 5
ORDER BY total_spent DESC
LIMIT 100;

-- Оптимизированная версия
-- 1. Создать материализованное представление
CREATE MATERIALIZED VIEW user_order_stats AS
SELECT
    user_id,
    COUNT(*) AS order_count,
    SUM(total) AS total_spent
FROM orders
GROUP BY user_id;

CREATE INDEX idx_user_order_stats_user_id ON user_order_stats(user_id);
CREATE INDEX idx_user_order_stats_total ON user_order_stats(total_spent DESC);

-- 2. Оптимизированный запрос
SELECT
    u.id,
    u.name,
    u.email,
    COALESCE(s.order_count, 0) AS order_count,
    COALESCE(s.total_spent, 0) AS total_spent
FROM users u
LEFT JOIN user_order_stats s ON u.id = s.user_id
WHERE u.created_at > '2024-01-01'
AND s.order_count > 5
ORDER BY s.total_spent DESC NULLS LAST
LIMIT 100;
```

### Example 2: Reporting Query Optimization

```sql
-- Исходный запрос для отчета
SELECT
    DATE(created_at) AS date,
    COUNT(*) AS order_count,
    SUM(total) AS total_revenue,
    AVG(total) AS avg_order_value
FROM orders
WHERE created_at >= NOW() - INTERVAL '1 year'
GROUP BY DATE(created_at)
ORDER BY date;

-- Оптимизация: создать индекс для группировки
CREATE INDEX idx_orders_created_at_date ON orders(DATE(created_at));

-- Или использовать частичный индекс
CREATE INDEX idx_orders_recent_created ON orders(created_at)
WHERE created_at >= NOW() - INTERVAL '1 year';
```

## Best Practices Summary

### Configuration

1. **Начните с базовых настроек** и корректируйте постепенно
2. **Мониторьте метрики** после каждого изменения
3. **Тестируйте изменения** на **staging** окружении
4. **Документируйте изменения** и их влияние

### Query Optimization

1. **Всегда используйте `EXPLAIN` ANALYZE** перед оптимизацией
2. **Создавайте индексы осознанно** — каждый индекс замедляет записи
3. **Обновляйте статистику регулярно** с помощью **ANALYZE**
4. **Используйте материализованные представления** для сложных запросов

### Monitoring

1. **Настройте pg_stat_statements** для отслеживания запросов
2. **Мониторьте активные запросы** регулярно
3. **Отслеживайте использование индексов** и удаляйте неиспользуемые
4. **Настройте алерты** на критические метрики

## Advanced Query Optimization

### Partitioning for Performance

```sql
-- Создать партиционированную таблицу
CREATE TABLE orders (
    id BIGSERIAL,
    user_id INTEGER,
    total DECIMAL(10,2),
    created_at TIMESTAMPTZ NOT NULL
) PARTITION BY RANGE (created_at);

-- Создать партиции
CREATE TABLE orders_2024_q1 PARTITION OF orders
FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');

CREATE TABLE orders_2024_q2 PARTITION OF orders
FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');

CREATE TABLE orders_2024_q3 PARTITION OF orders
FOR VALUES FROM ('2024-07-01') TO ('2024-10-01');

CREATE TABLE orders_2024_q4 PARTITION OF orders
FOR VALUES FROM ('2024-10-01') TO ('2025-01-01');

-- Создать индексы на партициях
CREATE INDEX idx_orders_2024_q1_user_id ON orders_2024_q1(user_id);
CREATE INDEX idx_orders_2024_q1_created_at ON orders_2024_q1(created_at);
```

### Query Plan Caching

```sql
-- Использовать prepared statements для кэширования планов
PREPARE get_user_orders(INTEGER) AS
SELECT * FROM orders WHERE user_id = $1 ORDER BY created_at DESC LIMIT 10;

-- Выполнить prepared statement
EXECUTE get_user_orders(1);

-- Prepared statements автоматически кэшируют план выполнения
```

### Batch Processing Optimization

```sql
-- Обработка больших объемов данных батчами
DO $$
DECLARE
    batch_size INTEGER := 10000;
    offset_val INTEGER := 0;
    row_count INTEGER;
BEGIN
    LOOP
        -- Обработать батч
        WITH batch AS (
            SELECT * FROM large_table
            ORDER BY id
            LIMIT batch_size OFFSET offset_val
        )
        UPDATE large_table
        SET processed = true
        FROM batch
        WHERE large_table.id = batch.id;

        GET DIAGNOSTICS row_count = ROW_COUNT;
        EXIT WHEN row_count = 0;

        offset_val := offset_val + batch_size;

        -- Небольшая пауза для снижения нагрузки
        PERFORM pg_sleep(0.1);
    END LOOP;
END $$;
```

## Storage Optimization

### Tablespace Management

```sql
-- Создать tablespace на быстром диске
CREATE TABLESPACE fast_disk LOCATION '/mnt/fast_disk/postgres';

-- Создать таблицу в tablespace
CREATE TABLE fast_table (
    id SERIAL PRIMARY KEY,
    data TEXT
) TABLESPACE fast_disk;

-- Переместить существующую таблицу
ALTER TABLE large_table SET TABLESPACE fast_disk;
```

### TOAST Optimization

```sql
-- Проверить использование TOAST
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) -
                   pg_relation_size(schemaname||'.'||tablename)) AS toast_size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Настроить TOAST для больших столбцов
ALTER TABLE large_table ALTER COLUMN large_text SET STORAGE EXTENDED;
```

## Advanced Monitoring

### Custom Performance Metrics

```sql
-- Создать представление для ключевых метрик
CREATE VIEW performance_metrics AS
SELECT
    'cache_hit_ratio' AS metric_name,
    ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2) AS metric_value,
    CASE
        WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90 THEN 'WARNING'
        ELSE 'OK'
    END AS status
FROM pg_stat_database
WHERE datname = current_database()

UNION ALL

SELECT
    'avg_query_time' AS metric_name,
    AVG(mean_exec_time) AS metric_value,
    CASE
        WHEN AVG(mean_exec_time) > 1000 THEN 'WARNING'
        ELSE 'OK'
    END AS status
FROM pg_stat_statements

UNION ALL

SELECT
    'connection_usage' AS metric_name,
    ROUND(100.0 * COUNT(*) / (SELECT setting::NUMERIC FROM pg_settings WHERE name = 'max_connections'), 2) AS metric_value,
    CASE
        WHEN 100.0 * COUNT(*) / (SELECT setting::NUMERIC FROM pg_settings WHERE name = 'max_connections') > 80 THEN 'WARNING'
        ELSE 'OK'
    END AS status
FROM pg_stat_activity;
```

### Performance Regression Detection

```sql
-- Создать таблицу для baseline метрик
CREATE TABLE performance_baseline (
    id SERIAL PRIMARY KEY,
    metric_name TEXT NOT NULL,
    metric_value NUMERIC NOT NULL,
    recorded_at TIMESTAMPTZ DEFAULT NOW()
);

-- Функция для записи baseline
CREATE OR REPLACE FUNCTION record_performance_baseline()
RETURNS VOID AS $$
BEGIN
    -- Записать текущие метрики
    INSERT INTO performance_baseline (metric_name, metric_value)
    SELECT
        'cache_hit_ratio',
        ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
    FROM pg_stat_database
    WHERE datname = current_database();

    INSERT INTO performance_baseline (metric_name, metric_value)
    SELECT
        'avg_query_time',
        AVG(mean_exec_time)
    FROM pg_stat_statements;
END;
$$ LANGUAGE plpgsql;

-- Запланировать через pg_cron
SELECT cron.schedule('record-baseline', '0 * * * *',
    'SELECT record_performance_baseline();');
```

## Optimization Workflows

### Workflow 1: Optimizing Slow Query

```sql
-- Шаг 1: Найти медленный запрос
SELECT
    query,
    mean_exec_time,
    calls
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 1;

-- Шаг 2: Анализ плана выполнения
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
<slow_query>;

-- Шаг 3: Создать недостающие индексы
CREATE INDEX idx_optimization ON table_name(column_name);

-- Шаг 4: Обновить статистику
ANALYZE table_name;

-- Шаг 5: Проверить улучшение
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
<slow_query>;

-- Шаг 6: Сравнить метрики
SELECT
    query,
    mean_exec_time,
    calls
FROM pg_stat_statements
WHERE query = '<slow_query>';
```

### Workflow 2: Database-Wide Optimization

```sql
-- Шаг 1: Анализ всех медленных запросов
CREATE VIEW slow_queries_summary AS
SELECT
    LEFT(query, 100) AS query_preview,
    COUNT(*) AS query_count,
    AVG(mean_exec_time) AS avg_time,
    SUM(calls) AS total_calls,
    SUM(total_exec_time) AS total_time
FROM pg_stat_statements
WHERE mean_exec_time > 100
GROUP BY LEFT(query, 100)
ORDER BY total_time DESC;

-- Шаг 2: Анализ использования индексов
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE idx_scan = 0
ORDER BY pg_relation_size(indexrelid) DESC;

-- Шаг 3: Оптимизация конфигурации
-- Обновить настройки на основе анализа

-- Шаг 4: Перестроить индексы
REINDEX DATABASE CONCURRENTLY mydb;

-- Шаг 5: Обновить статистику
ANALYZE;
```

## Performance Tuning Checklist

### Initial Setup

- [ ] Настроить базовые параметры памяти
- [ ] Настроить **WAL** параметры
- [ ] Настроить **autovacuum**
- [ ] Настроить **connection pooling**
- [ ] Создать необходимые индексы
- [ ] Настроить мониторинг

### Regular Maintenance

- [ ] Анализировать медленные запросы еженедельно
- [ ] Проверять использование индексов ежемесячно
- [ ] Обновлять статистику регулярно
- [ ] Перестраивать индексы при необходимости
- [ ] Мониторить метрики производительности
- [ ] Оптимизировать конфигурацию на основе метрик

### Optimization Process

- [ ] Измерять производительность до изменений
- [ ] Вносить изменения постепенно
- [ ] Тестировать каждое изменение
- [ ] Измерять производительность после изменений
- [ ] Документировать результаты
- [ ] Откатывать изменения, если нет улучшения


- [PostgreSQL Performance Tuning](https://www.postgresql.org/docs/)
- [PostgreSQL Configuration](https://www.postgresql.org/docs/)
- [EXPLAIN Documentation](https://www.postgresql.org/docs/)
- [pg_stat_statements](https://www.postgresql.org/docs/)

