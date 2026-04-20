---
title: "PostgreSQL: Мониторинг"
description: "Материал по теме PostgreSQL: Мониторинг в разделе cheatsheets."
tags:
  - databases
  - relational
  - postgres-monitoring
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# PostgreSQL: Мониторинг

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Monitoring](https://www.postgresql.org/docs/current/monitoring.html) — мониторинг

### См. также
- [[postgres-basics|postgres-basics.md]] — основы PostgreSQL
- [[postgres-performance-tuning|postgres-performance-tuning.md]] — тюнинг производительности

Мониторинг **PostgreSQL** включает отслеживание производительности, использование ресурсов, активность базы данных и метрики для обеспечения оптимальной работы и раннего обнаружения проблем.


- [[postgres-indexes|PostgreSQL: типы индексов]]
- [[postgres-admin|PostgreSQL: администрирование и обслуживание]]
- [[postgres-queries|PostgreSQL: запросы и агрегаты]]
## Содержание

- [Введение](#введение)
- [Встроенные статистические представления](#встроенные-статистические-представления)
  - [pg_stat_activity](#pg_stat_activity)
  - [pg_stat_database](#pg_stat_database)
  - [pg_stat_user_tables](#pg_stat_user_tables)
  - [pg_stat_user_indexes](#pg_stat_user_indexes)
- [pg_stat_statements](#pg_stat_statements)
  - [Установка](#установка)
  - [Использование](#использование)
  - [Сброс статистики](#сброс-статистики)
- [Мониторинг активности](#мониторинг-активности)
  - [Активные запросы](#активные-запросы)
  - [Блокировки](#блокировки)
  - [Долго выполняющиеся транзакции](#долго-выполняющиеся-транзакции)
- [Мониторинг базы данных](#мониторинг-базы-данных)
  - [Размер базы данных](#размер-базы-данных)
  - [Размер таблиц](#размер-таблиц)
  - [Использование дискового пространства](#использование-дискового-пространства)
- [Мониторинг таблиц и индексов](#мониторинг-таблиц-и-индексов)
  - [Статистика по таблицам](#статистика-по-таблицам)
  - [Статистика по индексам](#статистика-по-индексам)
  - [Неиспользуемые индексы](#неиспользуемые-индексы)
- [Prometheus и Grafana](#prometheus-и-grafana)
  - [PostgreSQL Exporter](#postgresql-exporter)
  - [Конфигурация](#конфигурация)
  - [Prometheus Configuration](#prometheus-configuration)
  - [Grafana Dashboard](#grafana-dashboard)
- [Алерты и уведомления](#алерты-и-уведомления)
  - [Prometheus Alert Rules](#prometheus-alert-rules)
  - [Настройка Alertmanager](#настройка-alertmanager)
  - [Регулярный мониторинг](#регулярный-мониторинг)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [Мониторинг репликации](#мониторинг-репликации)
- [Advanced Monitoring Techniques](#advanced-monitoring-techniques)
  - [Custom Monitoring Views](#custom-monitoring-views)
  - [Query Performance Monitoring](#query-performance-monitoring)
  - [Real-Time Monitoring Dashboard](#real-time-monitoring-dashboard)
- [Historical Monitoring](#historical-monitoring)
  - [Creating Monitoring Tables](#creating-monitoring-tables)
  - [Analyzing Historical Trends](#analyzing-historical-trends)
- [Advanced Alerting](#advanced-alerting)
  - [Custom Alert Functions](#custom-alert-functions)
- [Performance Metrics Collection](#performance-metrics-collection)
  - [Comprehensive Metrics View](#comprehensive-metrics-view)
- [Лучшие практики мониторинга](#лучшие-практики-мониторинга)
  - [Regular Monitoring Tasks](#regular-monitoring-tasks)
  - [Monitoring Dashboard Queries](#monitoring-dashboard-queries)
- [Integration with External Tools](#integration-with-external-tools)
  - [pgBadger Integration](#pgbadger-integration)
  - [Custom Exporter for Prometheus](#custom-exporter-for-prometheus)
- [Решение проблем](#решение-проблем)

## Введение

**Мониторинг **PostgreSQL** необходим для:**
- Отслеживания производительности запросов
- Выявления узких мест
- Мониторинга использования ресурсов
- Обнаружения проблем до их критического состояния
- Планирования масштабирования


## Встроенные статистические представления

**PostgreSQL** предоставляет множество представлений для мониторинга.

### pg_stat_activity

Отслеживает текущие активные соединения и запросы.

**Запросы для просмотра активных соединений и долгих запросов:**

```sql
-- Все активные соединения
SELECT
    pid,
    usename,
    application_name,
    client_addr,
    state,
    query_start,
    state_change,
    wait_event_type,
    wait_event,
    query
FROM pg_stat_activity
WHERE state = 'active';

-- Долго выполняющиеся запросы
SELECT
    pid,
    now() - query_start AS duration,
    state,
    query
FROM pg_stat_activity
WHERE state != 'idle'
  AND now() - query_start > interval '5 minutes'
ORDER BY duration DESC;
```

### pg_stat_database

Статистика на уровне базы данных.

```sql
-- Статистика по всем базам данных
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
    tup_deleted,
    temp_files,
    temp_bytes,
    deadlocks,
    blk_read_time,
    blk_write_time
FROM pg_stat_database
WHERE datname NOT IN ('template0', 'template1', 'postgres');

-- Коэффициент попадания в кэш
SELECT
    datname,
    round(100.0 * blks_hit / (blks_hit + blks_read), 2) AS cache_hit_ratio
FROM pg_stat_database
WHERE blks_hit + blks_read > 0
ORDER BY cache_hit_ratio;
```

### pg_stat_user_tables

Статистика по пользовательским таблицам.

```sql
-- Статистика по таблицам
SELECT
    schemaname,
    relname,
    seq_scan,
    seq_tup_read,
    idx_scan,
    idx_tup_fetch,
    n_tup_ins,
    n_tup_upd,
    n_tup_del,
    n_live_tup,
    n_dead_tup,
    last_vacuum,
    last_autovacuum,
    last_analyze,
    last_autoanalyze
FROM pg_stat_user_tables
ORDER BY n_live_tup DESC;

-- Таблицы с большим количеством последовательных сканирований
SELECT
    schemaname,
    relname,
    seq_scan,
    idx_scan,
    CASE
        WHEN seq_scan + idx_scan > 0
        THEN round(100.0 * seq_scan / (seq_scan + idx_scan), 2)
        ELSE 0
    END AS seq_scan_percent
FROM pg_stat_user_tables
WHERE seq_scan + idx_scan > 0
ORDER BY seq_scan_percent DESC;
```

### pg_stat_user_indexes

Статистика по индексам.

```sql
-- Статистика по индексам
SELECT
    schemaname,
    relname,
    indexrelname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;

-- Неиспользуемые индексы
SELECT
    schemaname,
    relname,
    indexrelname,
    idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0
ORDER BY pg_relation_size(indexrelid) DESC;
```


## pg_stat_statements

Расширение `pg_stat_statements` предоставляет детальную статистику по выполненным запросам.

### Установка

```sql
-- Создание расширения
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Настройка в postgresql.conf
-- shared_preload_libraries = 'pg_stat_statements'
-- pg_stat_statements.max = 10000
-- pg_stat_statements.track = all
```

### Использование

```sql
-- Топ запросов по времени выполнения
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time,
    min_exec_time,
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

-- Запросы с наибольшим временем ввода-вывода
SELECT
    query,
    calls,
    shared_blks_hit,
    shared_blks_read,
    shared_blks_dirtied,
    shared_blks_written,
    temp_blks_read,
    temp_blks_written,
    blk_read_time,
    blk_write_time
FROM pg_stat_statements
WHERE blk_read_time + blk_write_time > 0
ORDER BY blk_read_time + blk_write_time DESC
LIMIT 10;
```

### Сброс статистики

```sql
-- Сброс всей статистики
SELECT pg_stat_statements_reset();

-- Сброс статистики для конкретного запроса
SELECT pg_stat_statements_reset(userid, dbid, queryid);
```


## Мониторинг активности

### Активные запросы

```sql
-- Текущие активные запросы
SELECT
    pid,
    usename,
    datname,
    application_name,
    client_addr,
    state,
    wait_event_type,
    wait_event,
    query_start,
    state_change,
    now() - query_start AS query_duration,
    query
FROM pg_stat_activity
WHERE state != 'idle'
ORDER BY query_start;
```

### Блокировки

```sql
-- Текущие блокировки
SELECT
    blocked_locks.pid AS blocked_pid,
    blocked_activity.usename AS blocked_user,
    blocking_locks.pid AS blocking_pid,
    blocking_activity.usename AS blocking_user,
    blocked_activity.query AS blocked_statement,
    blocking_activity.query AS blocking_statement
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

### Долго выполняющиеся транзакции

```sql
-- Долгие транзакции
SELECT
    pid,
    usename,
    datname,
    application_name,
    state,
    xact_start,
    now() - xact_start AS xact_duration,
    query_start,
    now() - query_start AS query_duration,
    query
FROM pg_stat_activity
WHERE xact_start IS NOT NULL
  AND now() - xact_start > interval '5 minutes'
ORDER BY xact_start;
```


## Мониторинг базы данных

### Размер базы данных

```sql
-- Размер всех баз данных
SELECT
    datname,
    pg_size_pretty(pg_database_size(datname)) AS size
FROM pg_database
ORDER BY pg_database_size(datname) DESC;

-- Размер текущей базы данных
SELECT pg_size_pretty(pg_database_size(current_database()));
```

### Размер таблиц

```sql
-- Размер таблиц
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_indexes_size(schemaname||'.'||tablename)) AS indexes_size
FROM pg_tables
WHERE schemaname NOT IN ('pg_catalog', 'information_schema')
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Использование дискового пространства

```sql
-- Использование дискового пространства
SELECT
    pg_size_pretty(sum(pg_database_size(datname))) AS total_size
FROM pg_database;
```


## Мониторинг таблиц и индексов

### Статистика по таблицам

```sql
-- Детальная статистика по таблицам
SELECT
    schemaname,
    relname,
    n_live_tup,
    n_dead_tup,
    round(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2) AS dead_tuple_percent,
    last_vacuum,
    last_autovacuum,
    last_analyze,
    last_autoanalyze,
    vacuum_count,
    autovacuum_count,
    analyze_count,
    autoanalyze_count
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;
```

### Статистика по индексам

```sql
-- Детальная статистика по индексам
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC;
```

### Неиспользуемые индексы

```sql
-- Индексы, которые никогда не использовались
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE idx_scan = 0
  AND schemaname NOT IN ('pg_catalog', 'information_schema')
ORDER BY pg_relation_size(indexrelid) DESC;
```


## Prometheus и Grafana

### PostgreSQL Exporter

Установка и настройка **PostgreSQL Exporter** для **Prometheus**.

```bash
# Установка PostgreSQL Exporter
wget https://github.com/prometheus-community/postgres_exporter/releases/download/v0.10.1/postgres_exporter-0.10.1.linux-amd64.tar.gz
tar xvfz postgres_exporter-0.10.1.linux-amd64.tar.gz
cd postgres_exporter-0.10.1.linux-amd64

# Создание пользователя для мониторинга
sudo -u postgres psql -c "CREATE USER postgres_exporter WITH PASSWORD 'password';"
sudo -u postgres psql -c "GRANT pg_monitor TO postgres_exporter;"
```

### Конфигурация

```yaml
# postgres_exporter.yml
auth_modules:
  default:
    type: userpass
    userpass:
      username: postgres_exporter
      password: password

queries:
  - name: "pg_stat_database"
    help: "PostgreSQL database statistics"
    values:
      - datname
    query: |
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
        tup_deleted,
        temp_files,
        temp_bytes,
        deadlocks,
        blk_read_time,
        blk_write_time
      FROM pg_stat_database
      WHERE datname NOT IN ('template0', 'template1', 'postgres')
    master: true
    timeout: 0.5
```

### Prometheus Configuration

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'postgres'
    static_configs:
      - targets:
        - localhost:9187
```

### Grafana Dashboard

Импорт готового дашборда для **PostgreSQL** или создание собственного.

```json
{
  "dashboard": {
    "title": "PostgreSQL Database Metrics",
    "panels": [
      {
        "title": "Database Size",
        "targets": [
          {
            "expr": "pg_database_size_bytes"
          }
        ]
      },
      {
        "title": "Active Connections",
        "targets": [
          {
            "expr": "pg_stat_database_numbackends"
          }
        ]
      }
    ]
  }
}
```


## Алерты и уведомления

### Prometheus Alert Rules

```yaml
# alerts.yml
groups:
  - name: postgres_alerts
    interval: 30s
    rules:
      - alert: PostgreSQLDown
        expr: up{job="postgres"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "PostgreSQL instance is down"
          description: "PostgreSQL instance {{ $labels.instance }} is down"

      - alert: PostgreSQLTooManyConnections
        expr: pg_stat_database_numbackends > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Too many connections"
          description: "Database {{ $labels.datname }} has {{ $value }} connections"

      - alert: PostgreSQLSlowQueries
        expr: pg_stat_statements_mean_exec_time > 1000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Slow queries detected"
          description: "Query {{ $labels.query }} has mean execution time {{ $value }}ms"

      - alert: PostgreSQLHighDeadTuples
        expr: |
          (
            pg_stat_user_tables_n_dead_tup
            / (pg_stat_user_tables_n_live_tup + pg_stat_user_tables_n_dead_tup)
          ) > 0.2
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High dead tuple ratio"
          description: "Table {{ $labels.relname }} has {{ $value }} dead tuple ratio"
```

### Настройка Alertmanager

```yaml
# alertmanager.yml
route:
  group_by: ['alertname', 'cluster', 'service']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 12h
  receiver: 'web.hook'
  routes:
    - match:
        severity: critical
      receiver: 'pagerduty'
    - match:
        severity: warning
      receiver: 'email'

receivers:
  - name: 'web.hook'
    webhook_configs:
      - url: 'http://localhost:5001/'

  - name: 'pagerduty'
    pagerduty_configs:
      - service_key: '<pagerduty_key>'

  - name: 'email'
    email_configs:
      - to: 'admin@example.com'
        from: 'alerts@example.com'
        smarthost: 'smtp.example.com:587'
        auth_username: 'alerts@example.com'
        auth_password: 'password'
```

### Регулярный мониторинг

1. **Настройте автоматический сбор метрик**
   - Используйте **Prometheus** для сбора метрик
   - Настройте регулярные дампы статистики

2. **Мониторьте ключевые метрики**
   - Количество соединений
   - Производительность запросов
   - Использование дискового пространства
   - Коэффициент попадания в кэш

3. **Настройте алерты**
   - Критические алерты для критических проблем
   - Предупреждающие алерты для потенциальных проблем

### Оптимизация запросов

```sql
-- Регулярный анализ медленных запросов
SELECT
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time
FROM pg_stat_statements
WHERE mean_exec_time > 1000
ORDER BY total_exec_time DESC
LIMIT 20;
```

### Мониторинг репликации

```sql
-- Проверка лага репликации
SELECT
    client_addr,
    state,
    sent_lsn,
    write_lsn,
    flush_lsn,
    replay_lsn,
    sync_state
FROM pg_stat_replication;
```

## Advanced Monitoring Techniques

### Custom Monitoring Views

```sql
-- Создать комплексное представление для мониторинга
CREATE VIEW database_health_overview AS
SELECT
    'connections' AS metric_category,
    COUNT(*) AS current_value,
    (SELECT setting::INTEGER FROM pg_settings WHERE name = 'max_connections') AS max_value,
    ROUND(100.0 * COUNT(*) / (SELECT setting::INTEGER FROM pg_settings WHERE name = 'max_connections'), 2) AS usage_percent,
    CASE
        WHEN COUNT(*) > (SELECT setting::INTEGER * 0.9 FROM pg_settings WHERE name = 'max_connections') THEN 'CRITICAL'
        WHEN COUNT(*) > (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections') THEN 'WARNING'
        ELSE 'OK'
    END AS status
FROM pg_stat_activity

UNION ALL

SELECT
    'cache_hit_ratio' AS metric_category,
    ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2) AS current_value,
    95.0 AS max_value,
    ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2) AS usage_percent,
    CASE
        WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90 THEN 'WARNING'
        ELSE 'OK'
    END AS status
FROM pg_stat_database
WHERE datname = current_database()

UNION ALL

SELECT
    'deadlocks' AS metric_category,
    deadlocks AS current_value,
    0 AS max_value,
    0 AS usage_percent,
    CASE WHEN deadlocks > 0 THEN 'WARNING' ELSE 'OK' END AS status
FROM pg_stat_database
WHERE datname = current_database();

-- Использовать представление
SELECT * FROM database_health_overview;
```

### Query Performance Monitoring

```sql
-- Создать представление для мониторинга производительности запросов
CREATE VIEW query_performance_monitoring AS
SELECT
    LEFT(query, 100) AS query_preview,
    COUNT(*) AS query_variants,
    SUM(calls) AS total_calls,
    SUM(total_exec_time) AS total_time,
    AVG(mean_exec_time) AS avg_time,
    MAX(max_exec_time) AS max_time,
    SUM(rows) AS total_rows,
    AVG(100.0 * shared_blks_hit / NULLIF(shared_blks_hit + shared_blks_read, 0)) AS avg_cache_hit_ratio
FROM pg_stat_statements
GROUP BY LEFT(query, 100)
HAVING AVG(mean_exec_time) > 100
ORDER BY total_time DESC;

-- Использовать представление
SELECT * FROM query_performance_monitoring LIMIT 20;
```

### Real-Time Monitoring Dashboard

```sql
-- Создать функцию для real-time дашборда
CREATE OR REPLACE FUNCTION monitoring_dashboard()
RETURNS TABLE(
    metric_name TEXT,
    metric_value TEXT,
    status TEXT,
    recommendation TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Соединения
    SELECT
        'Active Connections'::TEXT,
        format('%s / %s',
            COUNT(*) FILTER (WHERE state = 'active'),
            (SELECT setting FROM pg_settings WHERE name = 'max_connections')
        ),
        CASE
            WHEN COUNT(*) > (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections')
            THEN 'WARNING'
            ELSE 'OK'
        END,
        CASE
            WHEN COUNT(*) > (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections')
            THEN 'Consider using connection pooling'
            ELSE 'OK'
        END
    FROM pg_stat_activity

    UNION ALL

    -- Cache hit ratio
    SELECT
        'Cache Hit Ratio'::TEXT,
        format('%.2f%%',
            ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
        ),
        CASE
            WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90 THEN 'WARNING'
            ELSE 'OK'
        END,
        CASE
            WHEN 100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0) < 90
            THEN 'Consider increasing shared_buffers'
            ELSE 'OK'
        END
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Deadlocks
    SELECT
        'Deadlocks'::TEXT,
        deadlocks::TEXT,
        CASE WHEN deadlocks > 0 THEN 'WARNING' ELSE 'OK' END,
        CASE
            WHEN deadlocks > 0 THEN 'Review transaction logic and lock usage'
            ELSE 'OK'
        END
    FROM pg_stat_database
    WHERE datname = current_database()

    UNION ALL

    -- Database size
    SELECT
        'Database Size'::TEXT,
        pg_size_pretty(pg_database_size(current_database())),
        'INFO'::TEXT,
        'Monitor growth trends'::TEXT
    FROM pg_database
    WHERE datname = current_database()

    UNION ALL

    -- Long running queries
    SELECT
        'Long Running Queries'::TEXT,
        COUNT(*)::TEXT,
        CASE WHEN COUNT(*) > 0 THEN 'WARNING' ELSE 'OK' END,
        CASE
            WHEN COUNT(*) > 0 THEN 'Review and optimize slow queries'
            ELSE 'OK'
        END
    FROM pg_stat_activity
    WHERE state = 'active'
    AND now() - query_start > interval '5 minutes';
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM monitoring_dashboard();
```

## Historical Monitoring

### Creating Monitoring Tables

```sql
-- Создать таблицу для хранения исторических метрик
CREATE TABLE monitoring_history (
    id BIGSERIAL PRIMARY KEY,
    metric_name TEXT NOT NULL,
    metric_value NUMERIC NOT NULL,
    recorded_at TIMESTAMPTZ DEFAULT NOW()
);

-- Создать индекс для быстрого поиска
CREATE INDEX idx_monitoring_history_metric_time
ON monitoring_history(metric_name, recorded_at DESC);

-- Функция для записи метрик
CREATE OR REPLACE FUNCTION record_metrics()
RETURNS VOID AS $$
BEGIN
    -- Записать cache hit ratio
    INSERT INTO monitoring_history (metric_name, metric_value)
    SELECT
        'cache_hit_ratio',
        ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
    FROM pg_stat_database
    WHERE datname = current_database();

    -- Записать количество соединений
    INSERT INTO monitoring_history (metric_name, metric_value)
    SELECT
        'active_connections',
        COUNT(*)::NUMERIC
    FROM pg_stat_activity
    WHERE state = 'active';

    -- Записать среднее время выполнения запросов
    INSERT INTO monitoring_history (metric_name, metric_value)
    SELECT
        'avg_query_time',
        AVG(mean_exec_time)
    FROM pg_stat_statements;
END;
$$ LANGUAGE plpgsql;

-- Запланировать через pg_cron
SELECT cron.schedule('record-metrics', '* * * * *',
    'SELECT record_metrics();');
```

### Analyzing Historical Trends

```sql
-- Анализ трендов метрик
CREATE VIEW metric_trends AS
SELECT
    metric_name,
    AVG(metric_value) AS avg_value,
    MIN(metric_value) AS min_value,
    MAX(metric_value) AS max_value,
    STDDEV(metric_value) AS stddev_value,
    COUNT(*) AS sample_count,
    MIN(recorded_at) AS first_recorded,
    MAX(recorded_at) AS last_recorded
FROM monitoring_history
WHERE recorded_at > NOW() - INTERVAL '24 hours'
GROUP BY metric_name;

-- Использовать представление
SELECT * FROM metric_trends;
```

## Advanced Alerting

### Custom Alert Functions

```sql
-- Создать таблицу для алертов
CREATE TABLE monitoring_alerts (
    id SERIAL PRIMARY KEY,
    alert_type TEXT NOT NULL,
    severity TEXT NOT NULL,
    message TEXT,
    metric_value NUMERIC,
    threshold_value NUMERIC,
    detected_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ,
    resolved_by TEXT
);

-- Функция для проверки и создания алертов
CREATE OR REPLACE FUNCTION check_and_alert()
RETURNS TABLE(
    alert_type TEXT,
    severity TEXT,
    message TEXT
) AS $$
DECLARE
    cache_hit_ratio NUMERIC;
    connection_count INTEGER;
    deadlock_count BIGINT;
BEGIN
    -- Проверить cache hit ratio
    SELECT ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
    INTO cache_hit_ratio
    FROM pg_stat_database
    WHERE datname = current_database();

    IF cache_hit_ratio < 90 THEN
        RETURN QUERY
        SELECT
            'low_cache_hit_ratio'::TEXT,
            'WARNING'::TEXT,
            format('Cache hit ratio is %.2f%%, below threshold of 90%%', cache_hit_ratio);

        -- Записать в таблицу алертов
        INSERT INTO monitoring_alerts (alert_type, severity, message, metric_value, threshold_value)
        VALUES ('low_cache_hit_ratio', 'WARNING',
            format('Cache hit ratio is %.2f%%', cache_hit_ratio),
            cache_hit_ratio, 90);
    END IF;

    -- Проверить количество соединений
    SELECT COUNT(*) INTO connection_count
    FROM pg_stat_activity;

    IF connection_count > (SELECT setting::INTEGER * 0.8 FROM pg_settings WHERE name = 'max_connections') THEN
        RETURN QUERY
        SELECT
            'high_connection_count'::TEXT,
            'WARNING'::TEXT,
            format('Connection count is %s, above 80%% of max_connections', connection_count);

        INSERT INTO monitoring_alerts (alert_type, severity, message, metric_value, threshold_value)
        VALUES ('high_connection_count', 'WARNING',
            format('Connection count is %s', connection_count),
            connection_count::NUMERIC,
            (SELECT setting::NUMERIC * 0.8 FROM pg_settings WHERE name = 'max_connections'));
    END IF;

    -- Проверить deadlocks
    SELECT deadlocks INTO deadlock_count
    FROM pg_stat_database
    WHERE datname = current_database();

    IF deadlock_count > 0 THEN
        RETURN QUERY
        SELECT
            'deadlocks_detected'::TEXT,
            'WARNING'::TEXT,
            format('%s deadlocks detected', deadlock_count);

        INSERT INTO monitoring_alerts (alert_type, severity, message, metric_value, threshold_value)
        VALUES ('deadlocks_detected', 'WARNING',
            format('%s deadlocks detected', deadlock_count),
            deadlock_count::NUMERIC, 0);
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Запланировать проверку
SELECT cron.schedule('check-alerts', '*/5 * * * *',
    'SELECT * FROM check_and_alert();');
```

## Performance Metrics Collection

### Comprehensive Metrics View

```sql
-- Создать комплексное представление метрик
CREATE VIEW comprehensive_metrics AS
SELECT
    'database' AS metric_category,
    'size' AS metric_name,
    pg_size_pretty(pg_database_size(current_database())) AS metric_value,
    NOW() AS recorded_at
FROM pg_database
WHERE datname = current_database()

UNION ALL

SELECT
    'performance' AS metric_category,
    'cache_hit_ratio' AS metric_name,
    format('%.2f%%',
        ROUND(100.0 * blks_hit / NULLIF(blks_hit + blks_read, 0), 2)
    ) AS metric_value,
    NOW() AS recorded_at
FROM pg_stat_database
WHERE datname = current_database()

UNION ALL

SELECT
    'performance' AS metric_category,
    'avg_query_time' AS metric_name,
    format('%.2f ms', AVG(mean_exec_time)) AS metric_value,
    NOW() AS recorded_at
FROM pg_stat_statements

UNION ALL

SELECT
    'connections' AS metric_category,
    'active_connections' AS metric_name,
    COUNT(*)::TEXT AS metric_value,
    NOW() AS recorded_at
FROM pg_stat_activity
WHERE state = 'active'

UNION ALL

SELECT
    'replication' AS metric_category,
    'replica_count' AS metric_name,
    COUNT(*)::TEXT AS metric_value,
    NOW() AS recorded_at
FROM pg_stat_replication;

-- Использовать представление
SELECT * FROM comprehensive_metrics;
```

## Лучшие практики мониторинга

### Regular Monitoring Tasks

```sql
-- Создать функцию для регулярных проверок
CREATE OR REPLACE FUNCTION daily_monitoring_check()
RETURNS TABLE(
    check_name TEXT,
    status TEXT,
    details TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Проверка размера БД
    SELECT
        'Database Size Check'::TEXT,
        CASE
            WHEN pg_database_size(current_database()) > 1000000000000 THEN 'WARNING'
            ELSE 'OK'
        END,
        format('Database size: %s',
            pg_size_pretty(pg_database_size(current_database()))
        )
    FROM pg_database
    WHERE datname = current_database()

    UNION ALL

    -- Проверка неиспользуемых индексов
    SELECT
        'Unused Indexes Check'::TEXT,
        CASE WHEN COUNT(*) > 10 THEN 'WARNING' ELSE 'OK' END,
        format('%s unused indexes found', COUNT(*))
    FROM pg_stat_user_indexes
    WHERE idx_scan = 0

    UNION ALL

    -- Проверка раздувания таблиц
    SELECT
        'Table Bloat Check'::TEXT,
        CASE
            WHEN MAX(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0)) > 20 THEN 'WARNING'
            ELSE 'OK'
        END,
        format('Max dead tuple ratio: %.2f%%',
            MAX(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0))
        )
    FROM pg_stat_user_tables;
END;
$$ LANGUAGE plpgsql;

-- Запланировать ежедневную проверку
SELECT cron.schedule('daily-monitoring', '0 9 * * *',
    'SELECT * FROM daily_monitoring_check();');
```

### Monitoring Dashboard Queries

```sql
-- Запросы для дашборда мониторинга

-- 1. Общая статистика
SELECT
    'Total Databases' AS metric,
    COUNT(*)::TEXT AS value
FROM pg_database
WHERE datistemplate = false

UNION ALL

SELECT
    'Total Tables' AS metric,
    COUNT(*)::TEXT AS value
FROM pg_tables
WHERE schemaname = 'public'

UNION ALL

SELECT
    'Total Indexes' AS metric,
    COUNT(*)::TEXT AS value
FROM pg_indexes
WHERE schemaname = 'public'

UNION ALL

SELECT
    'Active Connections' AS metric,
    COUNT(*)::TEXT AS value
FROM pg_stat_activity
WHERE state = 'active';

-- 2. Топ таблиц по размеру
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 10;

-- 3. Топ запросов по времени выполнения
SELECT
    LEFT(query, 100) AS query_preview,
    calls,
    mean_exec_time,
    total_exec_time
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;
```

## Integration with External Tools

### pgBadger Integration

```bash
# Настройка логирования для pgBadger
# В postgresql.conf:
log_min_duration_statement = 1000
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
log_checkpoints = on
log_connections = on
log_disconnections = on
log_lock_waits = on
log_temp_files = 0
log_autovacuum_min_duration = 0

# Генерация отчета
pgbadger /var/log/postgresql/postgresql-*.log -o report.html
```

### Custom Exporter for Prometheus

```java
// PostgreSQL Python example replaced with Java Spring
```

## Решение проблем

**Метрики не собираются или экспортёр не виден в Prometheus:** проверьте сетевое соединение до PostgreSQL (порт 5432, pg_hba.conf), корректность строки подключения и прав пользователя. Убедитесь, что целевой экспортёр указан в `prometheus.yml` и Prometheus перезагрузил конфиг.

**Высокий overhead мониторинга:** отключите или увеличьте интервал сбора тяжёлых запросов (pg_stat_statements), ограничьте число метрик в кастомных экспортёрах. Используйте выборку или агрегацию на стороне экспортёра при большом количестве баз/таблиц.

**Ложные алерты:** настройте пороги с учётом нагрузки (например, connections в пике), используйте дебаунс и группировку уведомлений. Проверьте корректность выражений в правилах (единицы, метки). Документируйте процедуры реагирования.

**Нет данных в дашбордах:** проверьте источник данных (Prometheus, имя инстанса), временной диапазон и наличие метрик в Prometheus. Убедитесь, что запросы к БД из экспортёра не блокируются файрволом или лимитами.

