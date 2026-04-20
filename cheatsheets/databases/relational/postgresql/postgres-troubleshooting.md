---
title: "PostgreSQL: Решение проблем"
description: "Руководство по диагностике и решению типичных проблем в PostgreSQL, включая блокировки, медленные запросы, проблемы с памятью и диском."
tags:
  - databases
  - relational
  - postgres-troubleshooting
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **PostgreSQL**: Решение проблем

Руководство по диагностике и решению типичных проблем в **PostgreSQL**, включая блокировки, медленные запросы, проблемы с памятью и диском.

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Wiki — Don't Do This](https://wiki.postgresql.org/wiki/Don't_Do_This) — типичные ошибки

### См. также
- [[postgres-basics|postgres-basics.md]] — основы PostgreSQL
- [[postgres-monitoring|postgres-monitoring.md]] — мониторинг


## Содержание

- [**PostgreSQL**: Решение проблем](#postgresql-решение-проблем)
- [Введение](#введение)
- [Блокировки и **Deadlocks**](#блокировки-и-deadlocks)
  - [Обнаружение блокировок](#обнаружение-блокировок)
  - [Типы блокировок](#типы-блокировок)
  - [Завершение блокирующих процессов](#завершение-блокирующих-процессов)
  - [**Deadlocks**](#deadlocks)
  - [Предотвращение блокировок](#предотвращение-блокировок)
- [Медленные запросы](#медленные-запросы)
  - [Обнаружение медленных запросов](#обнаружение-медленных-запросов)
  - [Анализ плана выполнения](#анализ-плана-выполнения)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Проблемы с **JOIN**](#проблемы-с-join)
- [Проблемы с памятью](#проблемы-с-памятью)
  - [Мониторинг использования памяти](#мониторинг-использования-памяти)
  - [Настройка памяти](#настройка-памяти)
  - [Проблемы с **OOM** (**Out of Memory**)](#проблемы-с-oom-out-of-memory)
  - [Оптимизация использования памяти](#оптимизация-использования-памяти)
- [Проблемы с диском](#проблемы-с-диском)
  - [Мониторинг использования диска](#мониторинг-использования-диска)
  - [Проблемы с **WAL**](#проблемы-с-wal)
  - [Очистка диска](#очистка-диска)
- [Проблемы с соединениями](#проблемы-с-соединениями)
  - [Мониторинг соединений](#мониторинг-соединений)
  - [Проблемы с **max_connections**](#проблемы-с-max_connections)
  - [Долгие **idle** соединения](#долгие-idle-соединения)
- [Проблемы с репликацией](#проблемы-с-репликацией)
  - [Проверка статуса репликации](#проверка-статуса-репликации)
  - [Проблемы с лагом](#проблемы-с-лагом)
- [Проблемы с автовакуумом](#проблемы-с-автовакуумом)
  - [Мониторинг автовакуума](#мониторинг-автовакуума)
  - [Настройка автовакуума](#настройка-автовакуума)
- [Диагностика производительности](#диагностика-производительности)
  - [Системные метрики](#системные-метрики)
  - [Анализ производительности](#анализ-производительности)
  - [Инструменты диагностики](#инструменты-диагностики)
- [**Best Practices**](#лучшие-практики)
  - [Регулярная диагностика](#регулярная-диагностика)
  - [Предотвращение проблем](#предотвращение-проблем)
- [**Advanced Troubleshooting Techniques**](#advanced-troubleshooting-techniques)
  - [Диагностические скрипты](#диагностические-скрипты)
    - [Комплексная диагностика](#комплексная-диагностика)
  - [Детальный анализ блокировок](#детальный-анализ-блокировок)
  - [Анализ медленных запросов](#анализ-медленных-запросов)
- [**Common Issues and Solutions**](#common-issues-and-solutions)
  - [**Issue** 1: **Database Won**'t **Start**](#issue-1-database-wont-start)
- [Проверить логи](#проверить-логи)
- [Проверить права на директорию данных](#проверить-права-на-директорию-данных)
- [Проверить доступность порта](#проверить-доступность-порта)
- [Проверить конфигурацию](#проверить-конфигурацию)
  - [**Issue** 2: **Connection Refused**](#issue-2-connection-refused)
  - [**Issue** 3: **Out** of **Disk Space**](#issue-3-out-of-disk-space)
  - [**Issue** 4: **High CPU Usage**](#issue-4-high-cpu-usage)
  - [**Issue** 5: **Memory Leaks**](#issue-5-memory-leaks)
- [**Performance Bottleneck Identification**](#performance-bottleneck-identification)
  - [**CPU Bottlenecks**](#cpu-bottlenecks)
  - [I/O **Bottlenecks**](#io-bottlenecks)
  - [**Network Bottlenecks**](#network-bottlenecks)
- [**Diagnostic Tools and Scripts**](#diagnostic-tools-and-scripts)
  - [**Health Check Script**](#health-check-script)
  - [**Automated Problem Detection**](#automated-problem-detection)
  - [**Workflow** 1: **Database Performance Degradation**](#workflow-1-database-performance-degradation)
  - [**Workflow** 2: **Connection Issues**](#workflow-2-connection-issues)
  - [**Workflow** 3: **Disk Space Issues**](#workflow-3-disk-space-issues)
- [**Real-World Troubleshooting Scenarios**](#real-world-troubleshooting-scenarios)
  - [**Scenario** 1: **Sudden Performance Drop**](#scenario-1-sudden-performance-drop)
  - [**Scenario** 2: **Database Corruption**](#scenario-2-database-corruption)
- [Проверить целостность данных](#проверить-целостность-данных)
- [Проверить логи на ошибки](#проверить-логи-на-ошибки)
- [Выполнить VACUUM FULL для восстановления](#выполнить-vacuum-full-для-восстановления)
  - [**Scenario** 3: **Replication Failure**](#scenario-3-replication-failure)
  - [Регулярный мониторинг](#регулярный-мониторинг)
  - [Проактивное решение проблем](#проактивное-решение-проблем)
  - [Документация проблем](#документация-проблем)
- [**Advanced Diagnostic Queries**](#advanced-diagnostic-queries)
  - [**Query Performance Analysis**](#query-performance-analysis)
  - [**Index Usage Analysis**](#index-usage-analysis)
  - [**Table Bloat Analysis**](#table-bloat-analysis)
- [**Specific Problem Solutions**](#specific-problem-solutions)
  - [**Solution** 1: **Resolving Deadlocks**](#solution-1-resolving-deadlocks)
  - [**Solution** 2: **Fixing Slow Queries**](#solution-2-fixing-slow-queries)
  - [**Solution** 3: **Resolving Connection Exhaustion**](#solution-3-resolving-connection-exhaustion)
  - [**Solution** 4: **Fixing Replication Lag**](#solution-4-fixing-replication-lag)
- [**Monitoring and Alerting**](#monitoring-and-alerting)
  - [**Automated Monitoring Queries**](#automated-monitoring-queries)
  - [**Alerting Configuration**](#alerting-configuration)
- [**Performance Regression Detection**](#performance-regression-detection)
  - [**Baseline Comparison**](#baseline-comparison)
- [**Emergency Procedures**](#emergency-procedures)
  - [**Database Unresponsive**](#database-unresponsive)
- [Шаг 1: Проверить процесс PostgreSQL](#шаг-1-проверить-процесс-postgresql)
- [Шаг 2: Проверить логи](#шаг-2-проверить-логи)
- [Шаг 3: Проверить использование ресурсов](#шаг-3-проверить-использование-ресурсов)
- [Шаг 4: Принудительно завершить проблемные процессы](#шаг-4-принудительно-завершить-проблемные-процессы)
- [Шаг 5: Перезапустить PostgreSQL](#шаг-5-перезапустить-postgresql)
  - [**Data Corruption Recovery**](#data-corruption-recovery)
  - [**Emergency Maintenance Mode**](#emergency-maintenance-mode)
  - [**Daily Checks**](#daily-checks)
  - [**Weekly Checks**](#weekly-checks)
  - [**Monthly Checks**](#monthly-checks)

## Введение

**Типичные проблемы в **PostgreSQL**:**
- Блокировки и **deadlocks**
- Медленные запросы
- Недостаток памяти
- Переполнение диска
- Проблемы с соединениями
- Проблемы с репликацией


## Блокировки и **Deadlocks**

### Обнаружение блокировок

**Запрос для просмотра текущих блокировок и блокирующих сессий:**

```sql
-- Текущие блокировки
SELECT
    blocked_locks.pid AS blocked_pid,
    blocked_activity.usename AS blocked_user,
    blocking_locks.pid AS blocking_pid,
    blocking_activity.usename AS blocking_user,
    blocked_activity.query AS blocked_statement,
    blocking_activity.query AS blocking_statement,
    blocked_activity.application_name AS blocked_app,
    blocking_activity.application_name AS blocking_app
FROM pg_catalog.pg_locks blocked_locks
JOIN pg_catalog.pg_stat_activity blocked_activity
    ON blocked_activity.pid = blocked_locks.pid
JOIN pg_catalog.pg_locks blocking_locks
    ON blocking_locks.locktype = blocked_locks.locktype
    AND blocking_locks.database IS NOT DISTINCT FROM blocked_locks.database
    AND blocking_locks.relation IS NOT DISTINCT FROM blocked_locks.relation
    AND blocking_locks.page IS NOT DISTINCT FROM blocked_locks.page
    AND blocking_locks.tuple IS NOT DISTINCT FROM blocked_locks.tuple
    AND blocking_locks.virtualxid IS NOT DISTINCT FROM blocked_locks.virtualxid
    AND blocking_locks.transactionid IS NOT DISTINCT FROM blocked_locks.transactionid
    AND blocking_locks.classid IS NOT DISTINCT FROM blocked_locks.classid
    AND blocking_locks.objid IS NOT DISTINCT FROM blocked_locks.objid
    AND blocking_locks.objsubid IS NOT DISTINCT FROM blocked_locks.objsubid
    AND blocking_locks.pid != blocked_locks.pid
JOIN pg_catalog.pg_stat_activity blocking_activity
    ON blocking_activity.pid = blocking_locks.pid
WHERE NOT blocked_locks.granted;
```

### Типы блокировок

```sql
-- Все текущие блокировки с типами
SELECT
    locktype,
    database,
    relation::regclass,
    page,
    tuple,
    virtualxid,
    transactionid,
    classid,
    objid,
    objsubid,
    virtualtransaction,
    pid,
    mode,
    granted
FROM pg_locks
ORDER BY locktype, relation;
```

### Завершение блокирующих процессов

```sql
-- Завершение процесса (мягкое)
SELECT pg_cancel_backend(pid);

-- Завершение процесса (жесткое)
SELECT pg_terminate_backend(pid);

-- Завершение всех процессов пользователя
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE usename = 'problematic_user';
```

### **Deadlocks**

```sql
-- Проверка deadlocks в логах
-- Настройка в postgresql.conf:
-- log_lock_waits = on
-- deadlock_timeout = 1s

-- Мониторинг deadlocks
SELECT
    datname,
    deadlocks
FROM pg_stat_database
WHERE deadlocks > 0;
```

### Предотвращение блокировок

1. **Используйте короткие транзакции**
2. **Избегайте долгих операций в транзакциях**
3. **Используйте правильный порядок блокировок**
4. **Используйте `SELECT FOR UPDATE NOWAIT` для проверки блокировок**


## Медленные запросы

### Обнаружение медленных запросов

```sql
-- Включение логирования медленных запросов
-- В postgresql.conf:
-- log_min_duration_statement = 1000  -- логировать запросы > 1 секунды
-- log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '

-- Топ медленных запросов из pg_stat_statements
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time,
    min_exec_time,
    stddev_exec_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS hit_percent
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 20;
```

### Анализ плана выполнения

```sql
-- EXPLAIN для анализа плана
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'test@example.com';

-- Детальный EXPLAIN
EXPLAIN (ANALYZE, BUFFERS, VERBOSE, COSTS, TIMING)
SELECT * FROM users WHERE email = 'test@example.com';
```

### Оптимизация запросов

```sql
-- Проверка использования индексов
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan;

-- Поиск таблиц без индексов на часто используемых колонках
SELECT
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats
WHERE schemaname = 'public'
  AND n_distinct > 100
  AND correlation < 0.1;
```

### Проблемы с **JOIN**

```sql
-- Проверка статистики для JOIN
ANALYZE table1;
ANALYZE table2;

-- Использование правильных индексов для JOIN
CREATE INDEX idx_table1_fk ON table1(foreign_key_id);
CREATE INDEX idx_table2_pk ON table2(id);
```


## Проблемы с памятью

### Мониторинг использования памяти

```sql
-- Размер shared_buffers
SHOW shared_buffers;

-- Размер work_mem
SHOW work_mem;

-- Текущее использование памяти
SELECT
    name,
    setting,
    unit,
    source
FROM pg_settings
WHERE name IN ('shared_buffers', 'work_mem', 'maintenance_work_mem', 'effective_cache_size');
```

### Настройка памяти

```sql
-- Рекомендуемые настройки в postgresql.conf
-- shared_buffers = 25% от RAM (но не более 8GB)
-- effective_cache_size = 50-75% от RAM
-- work_mem = (RAM - shared_buffers) / (max_connections * 3)
-- maintenance_work_mem = 1-2GB

-- Проверка текущих настроек
SELECT name, setting, unit
FROM pg_settings
WHERE name LIKE '%mem%' OR name LIKE '%buffer%'
ORDER BY name;
```

### Проблемы с **OOM** (**Out of Memory**)

```sql
-- Мониторинг использования памяти процессами
SELECT
    pid,
    usename,
    datname,
    application_name,
    state,
    query,
    pg_size_pretty(pg_backend_memory_contexts()) AS memory_contexts
FROM pg_stat_activity
WHERE state != 'idle';
```

### Оптимизация использования памяти

1. **Уменьшите `work_mem` для параллельных запросов**
2. **Используйте `LIMIT` в запросах**
3. **Избегайте больших сортировок в памяти**
4. **Используйте индексы для уменьшения работы в памяти**


## Проблемы с диском

### Мониторинг использования диска

```sql
-- Размер всех баз данных
SELECT
    datname,
    pg_size_pretty(pg_database_size(datname)) AS size
FROM pg_database
ORDER BY pg_database_size(datname) DESC;

-- Размер таблиц
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_indexes_size(schemaname||'.'||tablename)) AS indexes_size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Проблемы с **WAL**

```sql
-- Размер WAL файлов
SELECT
    pg_size_pretty(sum(size)) AS total_wal_size
FROM pg_ls_waldir();

-- Настройка WAL в postgresql.conf
-- wal_buffers = 16MB
-- max_wal_size = 1GB
-- min_wal_size = 80MB
-- checkpoint_timeout = 5min
```

### Очистка диска

```sql
-- VACUUM для освобождения места
VACUUM ANALYZE;

-- VACUUM FULL для полной очистки (блокирует таблицу)
VACUUM FULL table_name;

-- Проверка мертвых кортежей
SELECT
    schemaname,
    relname,
    n_live_tup,
    n_dead_tup,
    round(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2) AS dead_tuple_percent
FROM pg_stat_user_tables
WHERE n_dead_tup > 0
ORDER BY n_dead_tup DESC;
```


## Проблемы с соединениями

### Мониторинг соединений

```sql
-- Текущие соединения
SELECT
    count(*) AS total_connections,
    count(*) FILTER (WHERE state = 'active') AS active_connections,
    count(*) FILTER (WHERE state = 'idle') AS idle_connections,
    count(*) FILTER (WHERE state = 'idle in transaction') AS idle_in_transaction
FROM pg_stat_activity
WHERE datname = current_database();

-- Соединения по пользователям
SELECT
    usename,
    count(*) AS connection_count,
    count(*) FILTER (WHERE state = 'active') AS active
FROM pg_stat_activity
WHERE datname = current_database()
GROUP BY usename
ORDER BY connection_count DESC;
```

### Проблемы с **max_connections**

```sql
-- Текущее значение max_connections
SHOW max_connections;

-- Настройка в postgresql.conf
-- max_connections = 100
-- superuser_reserved_connections = 3

-- Использование connection pooling
-- PgBouncer или pgpool-II для управления соединениями
```

### Долгие **idle** соединения

```sql
-- Соединения в состоянии idle in transaction
SELECT
    pid,
    usename,
    datname,
    application_name,
    state,
    state_change,
    now() - state_change AS idle_duration,
    query
FROM pg_stat_activity
WHERE state = 'idle in transaction'
  AND now() - state_change > interval '5 minutes'
ORDER BY state_change;
```


## Проблемы с репликацией

### Проверка статуса репликации

```sql
-- Статус репликации
SELECT
    client_addr,
    state,
    sent_lsn,
    write_lsn,
    flush_lsn,
    replay_lsn,
    sync_state,
    sync_priority
FROM pg_stat_replication;

-- Лаг репликации
SELECT
    client_addr,
    state,
    pg_wal_lsn_diff(pg_current_wal_lsn(), sent_lsn) AS sent_lag,
    pg_wal_lsn_diff(pg_current_wal_lsn(), write_lsn) AS write_lag,
    pg_wal_lsn_diff(pg_current_wal_lsn(), flush_lsn) AS flush_lag,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS replay_lag
FROM pg_stat_replication;
```

### Проблемы с лагом

```sql
-- Большой лаг репликации может быть вызван:
-- 1. Медленной сетью
-- 2. Медленным диском на реплике
-- 3. Большим количеством WAL данных
-- 4. Медленной обработкой на реплике

-- Настройка для уменьшения лага
-- wal_compression = on
-- max_wal_senders = 10
-- wal_keep_segments = 32
```


## Проблемы с автовакуумом

### Мониторинг автовакуума

```sql
-- Текущие процессы автовакуума
SELECT
    pid,
    datname,
    usename,
    application_name,
    state,
    query_start,
    state_change,
    query
FROM pg_stat_activity
WHERE query LIKE '%autovacuum%';

-- Статистика автовакуума
SELECT
    schemaname,
    relname,
    last_vacuum,
    last_autovacuum,
    last_analyze,
    last_autoanalyze,
    vacuum_count,
    autovacuum_count,
    analyze_count,
    autoanalyze_count
FROM pg_stat_user_tables
ORDER BY last_autovacuum NULLS FIRST;
```

### Настройка автовакуума

```sql
-- Настройки в postgresql.conf
-- autovacuum = on
-- autovacuum_max_workers = 3
-- autovacuum_naptime = 1min
-- autovacuum_vacuum_threshold = 50
-- autovacuum_analyze_threshold = 50
-- autovacuum_vacuum_scale_factor = 0.2
-- autovacuum_analyze_scale_factor = 0.1

-- Настройка для конкретной таблицы
ALTER TABLE large_table SET (
    autovacuum_vacuum_scale_factor = 0.05,
    autovacuum_analyze_scale_factor = 0.02
);
```


## Диагностика производительности

### Системные метрики

```sql
-- Проверка системных ограничений
SELECT
    name,
    setting,
    unit,
    source,
    context
FROM pg_settings
WHERE name IN (
    'max_connections',
    'shared_buffers',
    'effective_cache_size',
    'work_mem',
    'maintenance_work_mem',
    'checkpoint_completion_target',
    'wal_buffers',
    'default_statistics_target'
)
ORDER BY name;
```

### Анализ производительности

```sql
-- Общая статистика производительности
SELECT
    datname,
    numbackends,
    xact_commit,
    xact_rollback,
    blks_read,
    blks_hit,
    round(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2) AS cache_hit_ratio,
    tup_returned,
    tup_fetched,
    tup_inserted,
    tup_updated,
    tup_deleted,
    temp_files,
    temp_bytes,
    deadlocks,
    blk_read_time,
    blk_write_time
FROM pg_stat_database
WHERE datname = current_database();
```

### Инструменты диагностики

1. **pg_stat_statements** — статистика по запросам
2. **pg_stat_activity** — активные соединения
3. **EXPLAIN ANALYZE** — анализ планов выполнения
4. **pgBadger** — анализ логов
5. **pgAdmin** — графический интерфейс


## Лучшие практики

### Регулярная диагностика

1. **Мониторьте ключевые метрики**
   - Количество соединений
   - Коэффициент попадания в кэш
   - Количество **deadlocks**
   - Размер базы данных

2. **Регулярно анализируйте медленные запросы**
   - Используйте **pg_stat_statements**
   - Анализируйте планы выполнения
   - Оптимизируйте индексы

3. **Настройте автовакуум**
   - Регулярно проверяйте статистику
   - Настройте параметры для больших таблиц
   - Мониторьте процессы автовакуума

### Предотвращение проблем

1. **Используйте connection pooling**
2. **Настройте правильные лимиты памяти**
3. **Регулярно выполняйте VACUUM**
4. **Мониторьте размер WAL**
5. **Используйте индексы правильно**

## **Advanced Troubleshooting Techniques**

### Диагностические скрипты

#### Комплексная диагностика

```sql
-- Создать функцию для комплексной диагностики
CREATE OR REPLACE FUNCTION comprehensive_diagnostics()
RETURNS TABLE(
    category TEXT,
    metric_name TEXT,
    value TEXT,
    status TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Соединения
    SELECT
        'Connections'::TEXT,
        'Total connections'::TEXT,
        COUNT(*)::TEXT,
        CASE
            WHEN COUNT(*) > (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections')
            THEN 'WARNING'
            ELSE 'OK'
        END
    FROM pg_stat_activity

    UNION ALL

    -- Блокировки
    SELECT
        'Locks'::TEXT,
        'Blocked queries'::TEXT,
        COUNT(*)::TEXT,
        CASE WHEN COUNT(*) > 0 THEN 'WARNING' ELSE 'OK' END
    FROM pg_locks
    WHERE NOT granted

    UNION ALL

    -- Кэш
    SELECT
        'Cache'::TEXT,
        'Cache hit ratio'::TEXT,
        ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)::TEXT || '%',
        CASE
            WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90 THEN 'WARNING'
            ELSE 'OK'
        END
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Deadlocks
    SELECT
        'Deadlocks'::TEXT,
        'Total deadlocks'::TEXT,
        deadlocks::TEXT,
        CASE WHEN deadlocks > 0 THEN 'WARNING' ELSE 'OK' END
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Размер БД
    SELECT
        'Database Size'::TEXT,
        'Total size'::TEXT,
        pg_size_pretty(pg_database_size(current_database())),
        'INFO'::TEXT
    FROM pg_database
    WHERE datname = current_database();
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM comprehensive_diagnostics();
```

### Детальный анализ блокировок

```sql
-- Расширенный анализ блокировок
CREATE VIEW lock_analysis AS
SELECT
    l.locktype,
    l.database,
    l.relation::regclass AS table_name,
    l.page,
    l.tuple,
    l.virtualxid,
    l.transactionid,
    l.classid,
    l.objid,
    l.objsubid,
    l.virtualtransaction,
    l.pid,
    l.mode,
    l.granted,
    a.usename,
    a.datname,
    a.application_name,
    a.state,
    a.query,
    a.query_start,
    a.state_change,
    now() - a.query_start AS query_duration
FROM pg_locks l
LEFT JOIN pg_stat_activity a ON l.pid = a.pid
ORDER BY l.granted, l.pid;

-- Использовать представление
SELECT * FROM lock_analysis WHERE NOT granted;
```

### Анализ медленных запросов

```sql
-- Детальный анализ медленных запросов
CREATE VIEW slow_queries_analysis AS
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time,
    min_exec_time,
    stddev_exec_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS cache_hit_ratio,
    shared_blks_hit,
    shared_blks_read,
    shared_blks_dirtied,
    shared_blks_written,
    local_blks_hit,
    local_blks_read,
    local_blks_dirtied,
    local_blks_written,
    temp_blks_read,
    temp_blks_written,
    blk_read_time,
    blk_write_time
FROM pg_stat_statements
WHERE mean_exec_time > 100  -- Запросы медленнее 100ms
ORDER BY mean_exec_time DESC;

-- Использовать представление
SELECT * FROM slow_queries_analysis LIMIT 20;
```

## **Common Issues and Solutions**

### **Issue** 1: **Database Won**'t **Start**

```bash
# Проверить логи
tail -f /var/log/postgresql/postgresql-*.log

# Проверить права на директорию данных
ls -la /var/lib/postgresql/data

# Проверить доступность порта
netstat -tuln | grep 5432

# Проверить конфигурацию
postgres --check-config -D /var/lib/postgresql/data
```

### **Issue** 2: **Connection Refused**

```sql
-- Проверить настройки подключения
SHOW listen_addresses;
SHOW port;

-- Проверить pg_hba.conf
SELECT * FROM pg_hba_file_rules;

-- Проверить firewall
sudo iptables -L -n | grep 5432
```

### **Issue** 3: **Out** of **Disk Space**

```sql
-- Найти большие таблицы
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_indexes_size(schemaname||'.'||tablename)) AS indexes_size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 20;

-- Найти большие индексы
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC
LIMIT 20;

-- Очистить старые данные
DELETE FROM old_table WHERE created_at < NOW() - INTERVAL '1 year';
VACUUM FULL old_table;
```

### **Issue** 4: **High CPU Usage**

```sql
-- Найти запросы, использующие CPU
SELECT
    pid,
    usename,
    datname,
    application_name,
    state,
    query,
    query_start,
    now() - query_start AS duration
FROM pg_stat_activity
WHERE state = 'active'
AND query NOT LIKE '%pg_stat_activity%'
ORDER BY query_start;

-- Найти запросы с большим количеством вызовов
SELECT
    query,
    calls,
    mean_exec_time,
    total_exec_time
FROM pg_stat_statements
ORDER BY calls DESC
LIMIT 20;
```

### **Issue** 5: **Memory Leaks**

```sql
-- Мониторинг использования памяти
SELECT
    pid,
    usename,
    datname,
    application_name,
    state,
    pg_size_pretty(pg_backend_memory_contexts()) AS memory_usage
FROM pg_stat_activity
WHERE state != 'idle'
ORDER BY pg_backend_memory_contexts() DESC;

-- Проверить настройки памяти
SELECT
    name,
    setting,
    unit,
    context
FROM pg_settings
WHERE name IN (
    'shared_buffers',
    'work_mem',
    'maintenance_work_mem',
    'effective_cache_size'
);
```

## **Performance Bottleneck Identification**

### **CPU Bottlenecks**

```sql
-- Запросы, использующие CPU
SELECT
    pid,
    usename,
    datname,
    state,
    wait_event_type,
    wait_event,
    query,
    query_start,
    now() - query_start AS duration
FROM pg_stat_activity
WHERE state = 'active'
AND wait_event_type IS NULL
ORDER BY query_start;
```

### I/O **Bottlenecks**

```sql
-- Запросы с большим I/O
SELECT
    query,
    calls,
    mean_exec_time,
    shared_blks_read,
    shared_blks_hit,
    temp_blks_read,
    temp_blks_written,
    blk_read_time,
    blk_write_time
FROM pg_stat_statements
WHERE shared_blks_read > 1000
   OR temp_blks_read > 100
ORDER BY shared_blks_read DESC
LIMIT 20;
```

### **Network Bottlenecks**

```sql
-- Проверить репликацию (может указывать на сетевые проблемы)
SELECT
    client_addr,
    state,
    pg_wal_lsn_diff(pg_current_wal_lsn(), sent_lsn) AS sent_lag_bytes,
    pg_wal_lsn_diff(pg_current_wal_lsn(), write_lsn) AS write_lag_bytes,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS replay_lag_bytes
FROM pg_stat_replication;
```

## **Diagnostic Tools and Scripts**

### **Health Check Script**

```sql
-- Комплексный health check
CREATE OR REPLACE FUNCTION health_check()
RETURNS TABLE(
    check_name TEXT,
    status TEXT,
    message TEXT,
    value TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Проверка соединений
    SELECT
        'Connections'::TEXT,
        CASE
            WHEN COUNT(*) > (SELECT setting::INTEGER * 0.9 FROM pg_settings WHERE name = 'max_connections')
            THEN 'CRITICAL'
            WHEN COUNT(*) > (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections')
            THEN 'WARNING'
            ELSE 'OK'
        END,
        format('Current: %s, Max: %s',
            COUNT(*),
            (SELECT setting FROM pg_settings WHERE name = 'max_connections')
        ),
        COUNT(*)::TEXT
    FROM pg_stat_activity

    UNION ALL

    -- Проверка блокировок
    SELECT
        'Locks'::TEXT,
        CASE WHEN COUNT(*) > 10 THEN 'WARNING' ELSE 'OK' END,
        format('%s queries are blocked', COUNT(*)),
        COUNT(*)::TEXT
    FROM pg_locks
    WHERE NOT granted

    UNION ALL

    -- Проверка кэша
    SELECT
        'Cache Hit Ratio'::TEXT,
        CASE
            WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90 THEN 'WARNING'
            WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 95 THEN 'INFO'
            ELSE 'OK'
        END,
        format('Cache hit ratio: %s%%',
            ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
        ),
        ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)::TEXT || '%'
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Проверка deadlocks
    SELECT
        'Deadlocks'::TEXT,
        CASE WHEN deadlocks > 0 THEN 'WARNING' ELSE 'OK' END,
        format('%s deadlocks detected', deadlocks),
        deadlocks::TEXT
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Проверка репликации
    SELECT
        'Replication'::TEXT,
        CASE
            WHEN COUNT(*) = 0 THEN 'INFO'
            WHEN MAX(pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn)) > 104857600 THEN 'WARNING'
            ELSE 'OK'
        END,
        format('%s replicas, max lag: %s bytes',
            COUNT(*),
            COALESCE(MAX(pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn)), 0)
        ),
        COUNT(*)::TEXT
    FROM pg_stat_replication;
END;
$$ LANGUAGE plpgsql;
```

### **Automated Problem Detection**

```sql
-- Функция для автоматического обнаружения проблем
CREATE OR REPLACE FUNCTION detect_problems()
RETURNS TABLE(
    problem_type TEXT,
    severity TEXT,
    description TEXT,
    recommendation TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Проблема: Слишком много соединений
    SELECT
        'High Connection Count'::TEXT,
        'WARNING'::TEXT,
        format('Current connections: %s (Max: %s)',
            (SELECT COUNT(*) FROM pg_stat_activity),
            (SELECT setting FROM pg_settings WHERE name = 'max_connections')
        ),
        'Consider using connection pooling (PgBouncer)'::TEXT
    WHERE (SELECT COUNT(*) FROM pg_stat_activity) >
          (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections')

    UNION ALL

    -- Проблема: Низкий cache hit ratio
    SELECT
        'Low Cache Hit Ratio'::TEXT,
        'WARNING'::TEXT,
        format('Cache hit ratio: %s%%',
            ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
        ),
        'Consider increasing shared_buffers'::TEXT
    FROM pg_stat_database
    WHERE datname = current_database()
    AND 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90

    UNION ALL

    -- Проблема: Много блокировок
    SELECT
        'High Lock Count'::TEXT,
        'WARNING'::TEXT,
        format('%s queries are blocked', COUNT(*)),
        'Review transaction duration and lock usage'::TEXT
    FROM pg_locks
    WHERE NOT granted
    GROUP BY 1
    HAVING COUNT(*) > 10

    UNION ALL

    -- Проблема: Большой lag репликации
    SELECT
        'High Replication Lag'::TEXT,
        'WARNING'::TEXT,
        format('Replication lag: %s bytes',
            MAX(pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn))
        ),
        'Check network and disk performance on replica'::TEXT
    FROM pg_stat_replication
    WHERE pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) > 104857600;
END;
$$ LANGUAGE plpgsql;
```

## **Troubleshooting Workflows**

### **Workflow** 1: **Database Performance Degradation**

```sql
-- Шаг 1: Проверить активные запросы
SELECT * FROM pg_stat_activity WHERE state = 'active';

-- Шаг 2: Проверить медленные запросы
SELECT * FROM slow_queries_analysis LIMIT 10;

-- Шаг 3: Проверить блокировки
SELECT * FROM lock_analysis WHERE NOT granted;

-- Шаг 4: Проверить использование ресурсов
SELECT * FROM comprehensive_diagnostics();

-- Шаг 5: Анализировать планы выполнения проблемных запросов
EXPLAIN ANALYZE <problematic_query>;
```

### **Workflow** 2: **Connection Issues**

```sql
-- Шаг 1: Проверить текущие соединения
SELECT
    count(*) AS total,
    count(*) FILTER (WHERE state = 'active') AS active,
    count(*) FILTER (WHERE state = 'idle') AS idle,
    count(*) FILTER (WHERE state = 'idle in transaction') AS idle_in_transaction
FROM pg_stat_activity;

-- Шаг 2: Проверить настройки
SHOW max_connections;
SHOW superuser_reserved_connections;

-- Шаг 3: Проверить долгие idle соединения
SELECT
    pid,
    usename,
    state,
    state_change,
    now() - state_change AS idle_duration
FROM pg_stat_activity
WHERE state = 'idle in transaction'
ORDER BY state_change;

-- Шаг 4: Завершить проблемные соединения
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE state = 'idle in transaction'
AND now() - state_change > interval '1 hour';
```

### **Workflow** 3: **Disk Space Issues**

```sql
-- Шаг 1: Проверить размер БД
SELECT pg_size_pretty(pg_database_size(current_database()));

-- Шаг 2: Найти большие таблицы
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 10;

-- Шаг 3: Проверить мертвые кортежи
SELECT
    schemaname,
    relname,
    n_live_tup,
    n_dead_tup,
    round(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2) AS dead_pct
FROM pg_stat_user_tables
WHERE n_dead_tup > 0
ORDER BY n_dead_tup DESC;

-- Шаг 4: Выполнить VACUUM
VACUUM ANALYZE;

-- Шаг 5: Проверить WAL размер
SELECT pg_size_pretty(sum(size)) AS total_wal_size
FROM pg_ls_waldir();
```

## **Real-World Troubleshooting Scenarios**

### **Scenario** 1: **Sudden Performance Drop**

```sql
-- Диагностика внезапного падения производительности
-- 1. Проверить активные запросы
SELECT
    pid,
    usename,
    query,
    state,
    wait_event_type,
    wait_event,
    query_start,
    now() - query_start AS duration
FROM pg_stat_activity
WHERE state != 'idle'
ORDER BY query_start;

-- 2. Проверить блокировки
SELECT * FROM lock_analysis WHERE NOT granted;

-- 3. Проверить статистику
SELECT * FROM pg_stat_database WHERE datname = current_database();

-- 4. Проверить автовакуум
SELECT * FROM pg_stat_activity WHERE query LIKE '%autovacuum%';
```

### **Scenario** 2: **Database Corruption**

```bash
# Проверить целостность данных
pg_checksums -D /var/lib/postgresql/data --check

# Проверить логи на ошибки
grep -i "corrupt\|error\|fatal" /var/log/postgresql/postgresql-*.log

# Выполнить VACUUM FULL для восстановления
psql -d mydb -c "VACUUM FULL VERBOSE;"
```

### **Scenario** 3: **Replication Failure**

```sql
-- Диагностика проблем с репликацией
-- 1. Проверить статус репликации
SELECT * FROM pg_stat_replication;

-- 2. Проверить replication slots
SELECT * FROM pg_replication_slots;

-- 3. Проверить WAL файлы
SELECT * FROM pg_ls_waldir() ORDER BY modification DESC LIMIT 10;

-- 4. Проверить лаг
SELECT
    client_addr,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS lag_bytes
FROM pg_stat_replication;
```

## **Best Practices Summary**

### Регулярный мониторинг

1. **Настройте автоматические проверки** через **cron** или **pg_cron**
2. **Используйте health check функции** для регулярной диагностики
3. **Мониторьте ключевые метрики** через **Prometheus**/**Grafana**
4. **Настройте алерты** на критические проблемы

### Проактивное решение проблем

1. **Регулярно анализируйте медленные запросы**
2. **Оптимизируйте индексы** на основе статистики использования
3. **Настройте автовакуум** для ваших таблиц
4. **Мониторьте размер БД** и планируйте очистку

### Документация проблем

1. **Ведите журнал инцидентов**
2. **Документируйте решения** для будущих проблем
3. **Создавайте runbooks** для типичных проблем
4. **Обучайте команду** процедурам **troubleshooting**

## **Advanced Diagnostic Queries**

### **Query Performance Analysis**

```sql
-- Детальный анализ производительности запросов
CREATE VIEW query_performance_detail AS
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time,
    min_exec_time,
    stddev_exec_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS cache_hit_ratio,
    shared_blks_hit,
    shared_blks_read,
    shared_blks_dirtied,
    shared_blks_written,
    temp_blks_read,
    temp_blks_written,
    blk_read_time,
    blk_write_time,
    local_blks_hit,
    local_blks_read,
    local_blks_dirtied,
    local_blks_written
FROM pg_stat_statements
ORDER BY mean_exec_time DESC;

-- Анализ запросов по типам операций
SELECT
    CASE
        WHEN query ILIKE '%SELECT%' THEN 'SELECT'
        WHEN query ILIKE '%INSERT%' THEN 'INSERT'
        WHEN query ILIKE '%UPDATE%' THEN 'UPDATE'
        WHEN query ILIKE '%DELETE%' THEN 'DELETE'
        ELSE 'OTHER'
    END AS operation_type,
    COUNT(*) AS query_count,
    SUM(calls) AS total_calls,
    SUM(total_exec_time) AS total_time,
    AVG(mean_exec_time) AS avg_time
FROM pg_stat_statements
GROUP BY operation_type
ORDER BY total_time DESC;
```

### **Index Usage Analysis**

```sql
-- Анализ использования индексов
CREATE VIEW index_usage_analysis AS
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    CASE
        WHEN idx_scan = 0 THEN 'UNUSED'
        WHEN idx_scan < 100 THEN 'LOW_USAGE'
        WHEN idx_scan < 1000 THEN 'MEDIUM_USAGE'
        ELSE 'HIGH_USAGE'
    END AS usage_category
FROM pg_stat_user_indexes
ORDER BY idx_scan, pg_relation_size(indexrelid) DESC;

-- Найти неиспользуемые индексы
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE idx_scan = 0
AND schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;
```

### **Table Bloat Analysis**

```sql
-- Анализ раздувания таблиц
CREATE VIEW table_bloat_analysis AS
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    n_live_tup AS live_tuples,
    n_dead_tup AS dead_tuples,
    round(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2) AS dead_tuple_percent,
    last_vacuum,
    last_autovacuum,
    CASE
        WHEN n_dead_tup > n_live_tup THEN 'HIGH_BLOAT'
        WHEN n_dead_tup > n_live_tup * 0.5 THEN 'MEDIUM_BLOAT'
        WHEN n_dead_tup > 0 THEN 'LOW_BLOAT'
        ELSE 'NO_BLOAT'
    END AS bloat_level
FROM pg_stat_user_tables
WHERE schemaname = 'public'
ORDER BY n_dead_tup DESC;
```

## **Specific Problem Solutions**

### **Solution** 1: **Resolving Deadlocks**

```sql
-- Шаг 1: Найти deadlocks в логах
-- Включить логирование deadlocks
ALTER SYSTEM SET log_lock_waits = on;
ALTER SYSTEM SET deadlock_timeout = 1s;
SELECT pg_reload_conf();

-- Шаг 2: Анализ паттернов deadlocks
SELECT
    datname,
    deadlocks,
    (SELECT COUNT(*) FROM pg_stat_activity WHERE state = 'active') AS active_queries
FROM pg_stat_database
WHERE deadlocks > 0;

-- Шаг 3: Предотвращение deadlocks
-- Использовать SELECT FOR UPDATE NOWAIT
BEGIN;
SELECT * FROM table1 WHERE id = 1 FOR UPDATE NOWAIT;
SELECT * FROM table2 WHERE id = 2 FOR UPDATE NOWAIT;
COMMIT;
```

### **Solution** 2: **Fixing Slow Queries**

```sql
-- Шаг 1: Найти проблемные запросы
SELECT
    query,
    calls,
    mean_exec_time,
    total_exec_time
FROM pg_stat_statements
WHERE mean_exec_time > 1000
ORDER BY mean_exec_time DESC
LIMIT 10;

-- Шаг 2: Анализ плана выполнения
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
<problematic_query>;

-- Шаг 3: Создать недостающие индексы
-- На основе анализа плана выполнения

-- Шаг 4: Обновить статистику
ANALYZE <table_name>;

-- Шаг 5: Проверить улучшение
EXPLAIN (ANALYZE, BUFFERS, VERBOSE)
<problematic_query>;
```

### **Solution** 3: **Resolving Connection Exhaustion**

```sql
-- Шаг 1: Проверить использование соединений
SELECT
    count(*) AS total,
    count(*) FILTER (WHERE state = 'active') AS active,
    count(*) FILTER (WHERE state = 'idle') AS idle,
    count(*) FILTER (WHERE state = 'idle in transaction') AS idle_in_transaction
FROM pg_stat_activity;

-- Шаг 2: Завершить idle in transaction соединения
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE state = 'idle in transaction'
AND now() - state_change > interval '5 minutes';

-- Шаг 3: Настроить connection pooling
-- Использовать PgBouncer или pgpool-II

-- Шаг 4: Увеличить max_connections (если необходимо)
ALTER SYSTEM SET max_connections = 200;
SELECT pg_reload_conf();
```

### **Solution** 4: **Fixing Replication Lag**

```sql
-- Шаг 1: Проверить лаг
SELECT
    client_addr,
    state,
    pg_wal_lsn_diff(pg_current_wal_lsn(), sent_lsn) AS sent_lag_bytes,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS replay_lag_bytes
FROM pg_stat_replication;

-- Шаг 2: Оптимизировать настройки WAL
ALTER SYSTEM SET wal_compression = on;
ALTER SYSTEM SET max_wal_senders = 10;
ALTER SYSTEM SET wal_keep_segments = 32;
SELECT pg_reload_conf();

-- Шаг 3: Проверить производительность реплики
-- Проверить I/O на реплике
-- Проверить сеть между primary и replica

-- Шаг 4: Использовать pg_rewind для синхронизации
pg_rewind --target-pgdata=/var/lib/postgresql/data \
  --source-server="host=primary_host port=5432 user=postgres"
```

## **Monitoring and Alerting**

### **Automated Monitoring Queries**

```sql
-- Создать функцию для мониторинга
CREATE OR REPLACE FUNCTION monitor_database_health()
RETURNS TABLE(
    metric TEXT,
    value NUMERIC,
    threshold NUMERIC,
    status TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Соединения
    SELECT
        'connections'::TEXT,
        COUNT(*)::NUMERIC,
        (SELECT setting::NUMERIC * 0.8 FROM pg_settings WHERE name = 'max_connections'),
        CASE
            WHEN COUNT(*) > (SELECT setting::NUMERIC * 0.8 FROM pg_settings WHERE name = 'max_connections')
            THEN 'WARNING'
            ELSE 'OK'
        END
    FROM pg_stat_activity

    UNION ALL

    -- Cache hit ratio
    SELECT
        'cache_hit_ratio'::TEXT,
        ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2),
        90.0,
        CASE
            WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90 THEN 'WARNING'
            ELSE 'OK'
        END
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Deadlocks
    SELECT
        'deadlocks'::TEXT,
        deadlocks::NUMERIC,
        0.0,
        CASE WHEN deadlocks > 0 THEN 'WARNING' ELSE 'OK' END
    FROM pg_stat_database
    WHERE datname = current_database();
END;
$$ LANGUAGE plpgsql;
```

### **Alerting Configuration**

```sql
-- Создать таблицу для алертов
CREATE TABLE database_alerts (
    id SERIAL PRIMARY KEY,
    alert_type TEXT NOT NULL,
    severity TEXT NOT NULL,
    message TEXT,
    detected_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ,
    resolved_by TEXT
);

-- Функция для создания алертов
CREATE OR REPLACE FUNCTION create_alert(
    p_type TEXT,
    p_severity TEXT,
    p_message TEXT
)
RETURNS INTEGER AS $$
DECLARE
    alert_id INTEGER;
BEGIN
    INSERT INTO database_alerts (alert_type, severity, message)
    VALUES (p_type, p_severity, p_message)
    RETURNING id INTO alert_id;

    -- Отправить уведомление
    PERFORM pg_notify('database_alert',
        format('Alert: %s - %s', p_severity, p_message));

    RETURN alert_id;
END;
$$ LANGUAGE plpgsql;
```

## **Performance Regression Detection**

### **Baseline Comparison**

```sql
-- Создать таблицу для baseline метрик
CREATE TABLE performance_baseline (
    id SERIAL PRIMARY KEY,
    metric_name TEXT NOT NULL,
    metric_value NUMERIC NOT NULL,
    recorded_at TIMESTAMPTZ DEFAULT NOW()
);

-- Функция для записи baseline
CREATE OR REPLACE FUNCTION record_baseline()
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

-- Функция для сравнения с baseline
CREATE OR REPLACE FUNCTION compare_with_baseline()
RETURNS TABLE(
    metric_name TEXT,
    current_value NUMERIC,
    baseline_value NUMERIC,
    difference_percent NUMERIC,
    status TEXT
) AS $$
BEGIN
    RETURN QUERY
    WITH current_metrics AS (
        SELECT
            'cache_hit_ratio' AS metric_name,
            ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2) AS metric_value
        FROM pg_stat_database
        WHERE datname = current_database()
    ),
    baseline_metrics AS (
        SELECT
            metric_name,
            AVG(metric_value) AS avg_value
        FROM performance_baseline
        WHERE recorded_at > NOW() - INTERVAL '7 days'
        GROUP BY metric_name
    )
    SELECT
        c.metric_name,
        c.metric_value,
        b.avg_value,
        ROUND((c.metric_value - b.avg_value) / b.avg_value * 100, 2),
        CASE
            WHEN ABS((c.metric_value - b.avg_value) / b.avg_value * 100) > 10 THEN 'WARNING'
            ELSE 'OK'
        END
    FROM current_metrics c
    JOIN baseline_metrics b ON c.metric_name = b.metric_name;
END;
$$ LANGUAGE plpgsql;
```

## **Emergency Procedures**

### **Database Unresponsive**

```bash
# Шаг 1: Проверить процесс PostgreSQL
ps aux | grep postgres

# Шаг 2: Проверить логи
tail -f /var/log/postgresql/postgresql-*.log

# Шаг 3: Проверить использование ресурсов
top -p $(pgrep -f postgres)

# Шаг 4: Принудительно завершить проблемные процессы
sudo kill -9 <problematic_pid>

# Шаг 5: Перезапустить PostgreSQL
sudo systemctl restart postgresql
```

### **Data Corruption Recovery**

```sql
-- Шаг 1: Проверить целостность
SELECT * FROM pg_stat_database WHERE datname = current_database();

-- Шаг 2: Выполнить VACUUM FULL
VACUUM FULL VERBOSE;

-- Шаг 3: Проверить логи на ошибки
-- grep -i "corrupt\|error" /var/log/postgresql/postgresql-*.log

-- Шаг 4: Восстановить из бэкапа (если необходимо)
-- pg_restore -d mydb backup.dump
```

### **Emergency Maintenance Mode**

```sql
-- Перевести в режим обслуживания
ALTER DATABASE mydb SET default_transaction_read_only = on;

-- Выполнить обслуживание
VACUUM FULL;
REINDEX DATABASE mydb;

-- Вернуть в нормальный режим
ALTER DATABASE mydb SET default_transaction_read_only = off;
```

## **Troubleshooting Checklist**

### **Daily Checks**

- [ ] Проверить количество соединений
- [ ] Проверить **cache hit ratio**
- [ ] Проверить наличие блокировок
- [ ] Проверить медленные запросы
- [ ] Проверить размер БД

### **Weekly Checks**

- [ ] Анализ использования индексов
- [ ] Проверка раздувания таблиц
- [ ] Анализ производительности запросов
- [ ] Проверка репликации
- [ ] Обзор логов на ошибки

### **Monthly Checks**

- [ ] Полный аудит производительности
- [ ] Оптимизация индексов
- [ ] Очистка старых данных
- [ ] Обновление статистики
- [ ] Проверка конфигурации


- [PostgreSQL Troubleshooting](https://www.postgresql.org/docs/)
- [PostgreSQL Performance Tuning](https://www.postgresql.org/docs/)
- [pgBadger](https://www.postgresql.org/docs/)
- [PostgreSQL Wiki](https://www.postgresql.org/docs/)

