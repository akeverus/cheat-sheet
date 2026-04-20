---
title: "MySQL: Производительность и тюнинг — Полное руководство по оптимизации"
description: "Комплексное руководство по оптимизации производительности MySQL: конфигурация, индексы, запросы, мониторинг и тюнинг."
tags:
  - databases
  - relational
  - mysql-performance
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# MySQL: Производительность и тюнинг — Полное руководство по оптимизации

Комплексное руководство по оптимизации производительности **MySQL**: конфигурация, индексы, запросы, мониторинг и тюнинг.

## Полезные ссылки

### Официальная документация
- [Server Tuning](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
- [Performance Tuning](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
- [Optimization](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)

### Инструменты и анализ
- [MySQL Performance Schema](https://dev.mysql.com/doc/refman/8.0/en/performance-schema.html)
- [sys Schema](https://dev.mysql.com/doc/refman/8.0/en/sys-schema.html)
- [Percona Toolkit](https://docs.percona.com/percona-toolkit/)

### Мониторинг
- [MySQL Enterprise Monitor](https://dev.mysql.com/doc/refman/8.0/en/enterprise-monitor.html)
- [PMM (Percona Monitoring and Management)](https://docs.percona.com/percona-monitoring-and-management/)
- [Grafana + Prometheus](https://grafana.com/docs/grafana/latest/datasources/prometheus/)

### См. также
- [[mysql-basics|Основы]] — **MySQL**
- [[mysql-indexes|Индексы]] — индексы и их оптимизация
- [[mysql-queries|Запросы]] — оптимизация запросов
- [[mysql-replication|Репликация]] — репликация и высокая доступность

## Содержание

- [Методология оптимизации](#методология-оптимизации)
  - [Шаги оптимизации производительности](#шаги-оптимизации-производительности)
    - [1. Установление базовой линии](#1-установление-базовой-линии)
    - [2. Идентификация узких мест](#2-идентификация-узких-мест)
    - [3. Приоритизация оптимизаций](#3-приоритизация-оптимизаций)
    - [4. Тестирование изменений](#4-тестирование-изменений)
- [Анализ производительности](#анализ-производительности)
  - [Системные метрики](#системные-метрики)
    - [CPU и память](#cpu-и-память)
    - [Диск I/O](#диск-io)
    - [Сетевая активность](#сетевая-активность)
  - [Метрики MySQL](#метрики-mysql)
    - [Общая статистика](#общая-статистика)
    - [Индексная статистика](#индексная-статистика)
- [Оптимизация конфигурации](#оптимизация-конфигурации)
  - [Оптимизация InnoDB](#оптимизация-innodb)
    - [Буферный пул](#буферный-пул)
    - [Лог файлы](#лог-файлы)
    - [Прочие настройки InnoDB](#прочие-настройки-innodb)
  - [Оптимизация подключений](#оптимизация-подключений)
    - [Thread pool](#thread-pool)
    - [Connection pool](#connection-pool)
  - [Оптимизация кэшей](#оптимизация-кэшей)
    - [Query Cache (MySQL 5.7)](#query-cache-mysql-57)
    - [MySQL 8.0 - Query Cache удален](#mysql-80-query-cache-удален)
  - [Оптимизация MyISAM (если используется)](#оптимизация-myisam-если-используется)
- [Оптимизация запросов](#оптимизация-запросов)
  - [Анализ плана выполнения](#анализ-плана-выполнения)
    - [EXPLAIN для отдельных запросов](#explain-для-отдельных-запросов)
    - [Оптимизация JOIN](#оптимизация-join)
  - [Оптимизация подзапросов](#оптимизация-подзапросов)
    - [Преобразование подзапросов](#преобразование-подзапросов)
    - [Устранение зависимых подзапросов](#устранение-зависимых-подзапросов)
  - [Оптимизация агрегатных запросов](#оптимизация-агрегатных-запросов)
    - [Эффективные агрегаты](#эффективные-агрегаты)
    - [Loose Index Scan](#loose-index-scan)
  - [Оптимизация сортировки](#оптимизация-сортировки)
    - [Файловая сортировка vs индексная](#файловая-сортировка-vs-индексная)
    - [Оптимизация LIMIT с ORDER BY](#оптимизация-limit-с-order-by)
- [Оптимизация индексов](#оптимизация-индексов)
  - [Выбор правильных индексов](#выбор-правильных-индексов)
    - [Анализ паттернов запросов](#анализ-паттернов-запросов)
    - [Создание покрывающих индексов](#создание-покрывающих-индексов)
  - [Перестройка индексов](#перестройка-индексов)
    - [Когда перестраивать](#когда-перестраивать)
    - [Удаление неиспользуемых индексов](#удаление-неиспользуемых-индексов)
  - [Индексные стратегии](#индексные-стратегии)
    - [Composite indexes](#composite-indexes)
    - [Partial indexes](#partial-indexes)
- [Оптимизация хранения](#оптимизация-хранения)
  - [Выбор движка хранения](#выбор-движка-хранения)
    - [InnoDB vs MyISAM](#innodb-vs-myisam)
    - [MEMORY таблицы](#memory-таблицы)
  - [Оптимизация структуры таблиц](#оптимизация-структуры-таблиц)
    - [Нормализация vs денормализация](#нормализация-vs-денормализация)
    - [Архивация старых данных](#архивация-старых-данных)
  - [Партиционирование](#партиционирование)
    - [Range partitioning](#range-partitioning)
    - [Hash partitioning](#hash-partitioning)
- [Кэширование](#кэширование)
  - [Query Cache (MySQL 5.7)](#query-cache-mysql-57-1)
    - [Настройка и мониторинг](#настройка-и-мониторинг)
  - [Application-level caching](#application-level-caching)
    - [Spring Cache](#spring-cache)
    - [Redis для кэширования](#redis-для-кэширования)
  - [Кэширование результатов запросов](#кэширование-результатов-запросов)
    - [Материализованные представления](#материализованные-представления)
- [Мониторинг и алерты](#мониторинг-и-алерты)
  - [Performance Schema](#performance-schema)
    - [Включение мониторинга](#включение-мониторинга)
    - [Сбор метрик](#сбор-метрик)
  - [Системы мониторинга](#системы-мониторинга)
    - [PMM (Percona Monitoring and Management)](#pmm-percona-monitoring-and-management)
    - [Пользовательские алерты](#пользовательские-алерты)
- [Инструменты профилирования](#инструменты-профилирования)
  - [Percona Toolkit](#percona-toolkit)
    - [Анализ медленных запросов](#анализ-медленных-запросов)
    - [Анализ индексов](#анализ-индексов)
  - [MySQL Enterprise Monitor](#mysql-enterprise-monitor)
    - [Настройка мониторинга](#настройка-мониторинга)
    - [Автоматические отчеты](#автоматические-отчеты)
  - [SHOW PROCESSLIST и анализ](#show-processlist-и-анализ)
- [Автоматическая оптимизация](#автоматическая-оптимизация)
  - [MySQL 8.0 Invisible Indexes](#mysql-80-invisible-indexes)
    - [Создание невидимых индексов](#создание-невидимых-индексов)
  - [Автоматический анализ](#автоматический-анализ)
    - [Регулярный анализ таблиц](#регулярный-анализ-таблиц)
  - [Query Rewrite Plugin](#query-rewrite-plugin)
    - [Автоматическая перезапись запросов](#автоматическая-перезапись-запросов)
- [Масштабирование](#масштабирование)
  - [Read/Write Splitting](#readwrite-splitting)
    - [Настройка репликации для масштабирования](#настройка-репликации-для-масштабирования)
  - [Шардинг](#шардинг)
    - [Разделение данных по шардам](#разделение-данных-по-шардам)
  - [Кэширование и CDN](#кэширование-и-cdn)
    - [Multi-level caching](#multi-level-caching)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы производительности](#распространенные-проблемы-производительности)
    - [Высокая загрузка CPU](#высокая-загрузка-cpu)
    - [Высокое использование памяти](#высокое-использование-памяти)
    - [Проблемы с дисками](#проблемы-с-дисками)
  - [Диагностика медленных запросов](#диагностика-медленных-запросов)
    - [Анализ конкретного запроса](#анализ-конкретного-запроса)
    - [Оптимизация конкретного случая](#оптимизация-конкретного-случая)
  - [Проблемы с репликацией](#проблемы-с-репликацией)
    - [Диагностика задержки репликации](#диагностика-задержки-репликации)
    - [Исправление распространенных проблем](#исправление-распространенных-проблем)
- [Лучшие практики](#лучшие-практики)
  - [Проектирование для производительности](#проектирование-для-производительности)
    - [1. Нормализация vs производительность](#1-нормализация-vs-производительность)
    - [2. Выбор типов данных](#2-выбор-типов-данных)
    - [3. Индексация](#3-индексация)
  - [Оптимизация запросов](#оптимизация-запросов-1)
    - [1. Написание эффективных запросов](#1-написание-эффективных-запросов)
    - [2. Анализ и мониторинг](#2-анализ-и-мониторинг)
    - [3. Кэширование](#3-кэширование)
  - [Конфигурация сервера](#конфигурация-сервера)
    - [1. InnoDB оптимизация](#1-innodb-оптимизация)
    - [2. Подключения и потоки](#2-подключения-и-потоки)
    - [3. Мониторинг и обслуживание](#3-мониторинг-и-обслуживание)
  - [Масштабирование](#масштабирование-1)
    - [1. Вертикальное масштабирование](#1-вертикальное-масштабирование)
    - [2. Горизонтальное масштабирование](#2-горизонтальное-масштабирование)
    - [3. Архитектурные решения](#3-архитектурные-решения)
  - [Безопасность и производительность](#безопасность-и-производительность)
    - [1. Защищенные запросы](#1-защищенные-запросы)
    - [2. Профилирование и отладка](#2-профилирование-и-отладка)
  - [Автоматизация и DevOps](#автоматизация-и-devops)
    - [1. Автоматизированное тестирование](#1-автоматизированное-тестирование)
    - [2. Мониторинг и алерты](#2-мониторинг-и-алерты)
    - [3. CI/CD для баз данных](#3-cicd-для-баз-данных)
  - [Непрерывная оптимизация](#непрерывная-оптимизация)
    - [1. Цикл оптимизации](#1-цикл-оптимизации)
    - [2. Документирование решений](#2-документирование-решений)
  - [Выбор правильных инструментов](#выбор-правильных-инструментов)
    - [Для разных сценариев](#для-разных-сценариев)
    - [Мониторинг и инструменты](#мониторинг-и-инструменты)

## Методология оптимизации

### Шаги оптимизации производительности

#### 1. Установление базовой линии

Пример запросов для установления базовой линии производительности (SQL).

```sql
-- Измерение текущей производительности
SELECT
    NOW() AS measurement_time,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status
     WHERE VARIABLE_NAME = 'Queries') AS total_queries,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status
     WHERE VARIABLE_NAME = 'Innodb_buffer_pool_read_requests') AS buffer_read_requests,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status
     WHERE VARIABLE_NAME = 'Innodb_buffer_pool_reads') AS buffer_reads;

-- Создание таблицы для исторических данных
CREATE TABLE performance_baseline (
    id INT AUTO_INCREMENT PRIMARY KEY,
    measured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metric_name VARCHAR(100),
    metric_value BIGINT,
    server_load DECIMAL(5,2)
);
```

#### 2. Идентификация узких мест
```sql
-- Топ медленных запросов
SELECT
    sql_text,
    exec_count,
    avg_timer_wait / 1000000000 AS avg_time_sec,
    (sum_timer_wait / sum_timer_wait_total) * 100 AS pct_of_total_time
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 1000000000
ORDER BY sum_timer_wait DESC
LIMIT 10;

-- Анализ ожиданий
SELECT
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec,
    avg_timer_wait / 1000000000 AS avg_time_sec
FROM performance_schema.events_waits_summary_global_by_event_name
WHERE event_name NOT LIKE 'wait/synch/%'
ORDER BY sum_timer_wait DESC
LIMIT 10;
```

#### 3. Приоритизация оптимизаций
```sql
-- Расчет потенциального улучшения
SELECT
    'Slow Queries' AS category,
    COUNT(*) AS count,
    AVG(avg_timer_wait) / 1000000000 AS avg_time_sec,
    SUM(sum_timer_wait) / SUM(sum_timer_wait) OVER () * 100 AS pct_total_time
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 5000000000; -- > 5 секунд

-- Оценка индексов
SELECT
    table_name,
    index_name,
    cardinality,
    pages,
    ROUND(cardinality / NULLIF(table_rows, 0) * 100, 2) AS selectivity_pct
FROM information_schema.statistics s
JOIN information_schema.tables t ON s.table_schema = t.table_schema
    AND s.table_name = t.table_name
WHERE s.table_schema = DATABASE()
ORDER BY selectivity_pct ASC;
```

#### 4. Тестирование изменений
```sql
-- A/B тестирование оптимизаций
DELIMITER //

CREATE PROCEDURE performance_test(
    IN test_name VARCHAR(100),
    IN query_to_test TEXT,
    OUT execution_time DECIMAL(10,6)
)
BEGIN
    DECLARE start_time DECIMAL(20,6);
    DECLARE end_time DECIMAL(20,6);

    SET start_time = NOW(6);

    -- Выполнение тестового запроса
    SET @query = query_to_test;
    PREPARE stmt FROM @query;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET end_time = NOW(6);
    SET execution_time = end_time - start_time;

    -- Сохранение результатов
    INSERT INTO performance_tests (test_name, execution_time, tested_at)
    VALUES (test_name, execution_time, NOW());
END //

DELIMITER ;

-- Таблица для результатов тестирования
CREATE TABLE performance_tests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_name VARCHAR(100),
    execution_time DECIMAL(10,6),
    tested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Анализ производительности

### Системные метрики

#### CPU и память
```sql
-- Загрузка CPU
SELECT
    variable_name,
    variable_value
FROM performance_schema.global_status
WHERE variable_name LIKE 'cpu%';

-- Использование памяти
SELECT
    'Total Memory' AS metric,
    ROUND((@@innodb_buffer_pool_size + @@innodb_log_buffer_size +
           @@key_buffer_size + @@query_cache_size) / 1024 / 1024 / 1024, 2) AS value_gb
FROM dual

UNION ALL

SELECT
    'InnoDB Buffer Pool',
    ROUND(@@innodb_buffer_pool_size / 1024 / 1024 / 1024, 2)
FROM dual

UNION ALL

SELECT
    'Available Memory',
    ROUND((@@global.max_connections * @@session.thread_stack +
           @@innodb_buffer_pool_size) / 1024 / 1024 / 1024, 2)
FROM dual;
```

#### Диск I/O
```sql
-- Статистика I/O
SELECT
    file_name,
    count_read,
    count_write,
    sum_number_of_bytes_read / 1024 / 1024 AS read_mb,
    sum_number_of_bytes_write / 1024 / 1024 AS write_mb
FROM performance_schema.file_summary_by_instance
WHERE file_name LIKE '%.ibd'
ORDER BY sum_number_of_bytes_read + sum_number_of_bytes_write DESC
LIMIT 10;

-- Ожидания I/O
SELECT
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec,
    avg_timer_wait / 1000000000 AS avg_time_sec
FROM performance_schema.events_waits_summary_global_by_event_name
WHERE event_name LIKE 'wait/io/%'
ORDER BY sum_timer_wait DESC;
```

#### Сетевая активность
```sql
-- Сетевая статистика
SELECT
    'Bytes Received' AS metric,
    VARIABLE_VALUE / 1024 / 1024 AS value_mb
FROM performance_schema.global_status
WHERE VARIABLE_NAME = 'Bytes_received'

UNION ALL

SELECT
    'Bytes Sent',
    VARIABLE_VALUE / 1024 / 1024
FROM performance_schema.global_status
WHERE VARIABLE_NAME = 'Bytes_sent'

UNION ALL

SELECT
    'Connections',
    VARIABLE_VALUE
FROM performance_schema.global_status
WHERE VARIABLE_NAME = 'Connections';
```

### Метрики MySQL

#### Общая статистика
```sql
-- Основные метрики производительности
SELECT
    NOW() AS timestamp,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Queries') AS queries,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Com_select') AS selects,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Com_insert') AS inserts,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Com_update') AS updates,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Com_delete') AS deletes,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Slow_queries') AS slow_queries,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Innodb_buffer_pool_hit_rate') AS buffer_hit_rate
FROM dual;
```

#### Индексная статистика
```sql
-- Эффективность индексов
SELECT
    object_schema,
    object_name,
    index_name,
    count_read,
    count_write,
    count_fetch,
    (count_read + count_write) AS total_access
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = DATABASE()
ORDER BY total_access DESC
LIMIT 20;

-- Неиспользуемые индексы
SELECT
    table_name,
    index_name,
    'Consider dropping' AS recommendation
FROM information_schema.statistics
WHERE table_schema = DATABASE()
  AND index_name NOT IN (
      SELECT index_name
      FROM performance_schema.table_io_waits_summary_by_index_usage
      WHERE object_schema = DATABASE()
        AND count_read > 0
  )
  AND index_name != 'PRIMARY';
```

## Оптимизация конфигурации

### Оптимизация InnoDB

#### Буферный пул
```ini
# Рекомендуемая конфигурация буферного пула
[mysqld]
# 70-80% от доступной памяти для выделенных серверов
innodb_buffer_pool_size = 4G

# Размер экземпляра (для больших пулов)
innodb_buffer_pool_instances = 8

# Размер чанка (должен быть кратен instances)
innodb_buffer_pool_chunk_size = 128M

# Предварительная загрузка страниц
innodb_buffer_pool_load_at_startup = ON
innodb_buffer_pool_dump_at_shutdown = ON

# Очистка буфера
innodb_max_dirty_pages_pct = 75
innodb_max_dirty_pages_pct_lwm = 0
```

#### Лог файлы
```ini
# Оптимизация redo logs
[mysqld]
# Размер лог файла (25% от buffer pool)
innodb_log_file_size = 1G

# Количество лог файлов
innodb_log_files_in_group = 2

# Синхронизация логов
innodb_flush_log_at_trx_commit = 1
sync_binlog = 1

# Буфер логов
innodb_log_buffer_size = 16M
```

#### Прочие настройки InnoDB
```ini
# Дополнительные оптимизации
[mysqld]
# Flush метод
innodb_flush_method = O_DIRECT

# Не флашить соседей (для SSD)
innodb_flush_neighbors = 0

# Размер страницы
innodb_page_size = 16384

# Адаптивный хэш индекс
innodb_adaptive_hash_index = ON

# Read ahead
innodb_read_ahead_threshold = 56
innodb_random_read_ahead = OFF
```

### Оптимизация подключений

#### Thread pool
```ini
# Настройки подключений
[mysqld]
# Максимальное количество подключений
max_connections = 200

# Размер стека потока
thread_stack = 256K

# Кэш таблиц
table_open_cache = 4096
table_open_cache_instances = 16

# Временные таблицы
tmp_table_size = 128M
max_heap_table_size = 128M
```

#### Connection pool
```java
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Основные настройки
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Оптимизации
        config.setLeakDetectionThreshold(60000);
        config.setValidationTimeout(5000);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }
}
```

### Оптимизация кэшей

#### Query Cache (MySQL 5.7)
```ini
# Настройки query cache
[mysqld]
# Размер кэша запросов
query_cache_size = 256M

# Тип кэширования
query_cache_type = ON

# Ограничения
query_cache_limit = 1M
query_cache_min_res_unit = 4096

# Статистика
SHOW STATUS LIKE 'Qcache%';
```

#### MySQL `8.0` - Query Cache удален
```sql
-- В MySQL 8.0 используйте кэширование на уровне приложения
-- Spring Cache, Redis, или другие решения

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("queries", "entities");
    }
}
```

### Оптимизация MyISAM (если используется)

```ini
# Настройки MyISAM
[mysqld]
# Ключевой буфер
key_buffer_size = 512M

# Размер буфера для чтения
read_buffer_size = 2M

# Размер буфера для сортировки
sort_buffer_size = 4M

# Буфер случайного чтения
read_rnd_buffer_size = 1M

# Кэш таблиц
table_open_cache = 2048
```

## Оптимизация запросов

### Анализ плана выполнения

#### EXPLAIN для отдельных запросов
```sql
-- Базовый EXPLAIN
EXPLAIN SELECT
    u.name,
    COUNT(o.id) AS order_count,
    AVG(o.total_amount) AS avg_amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name;

-- Детальный анализ
EXPLAIN FORMAT=JSON SELECT ...;

-- Анализ с выполнением
EXPLAIN ANALYZE SELECT ...;
```

#### Оптимизация JOIN
```sql
-- Неоптимальный запрос
SELECT u.*, p.*, o.*
FROM users u
CROSS JOIN products p
LEFT JOIN orders o ON u.id = o.user_id;
-- type: ALL, rows: millions

-- Оптимизированный запрос
SELECT
    u.id, u.name, u.email,
    p.id, p.name, p.price,
    o.id, o.order_date, o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id
WHERE u.active = true
  AND o.order_date >= '2024-01-01';
-- type: ref/eq_ref, rows: thousands
```

### Оптимизация подзапросов

#### Преобразование подзапросов
```sql
-- Неоптимальный подзапрос
SELECT *
FROM products
WHERE category_id IN (
    SELECT id FROM categories
    WHERE parent_id = 1
);
-- Может выполняться медленно для больших наборов

-- Оптимизация с JOIN
SELECT p.*
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE c.parent_id = 1;
-- Обычно быстрее

-- Оптимизация с EXISTS
SELECT p.*
FROM products p
WHERE EXISTS (
    SELECT 1 FROM categories c
    WHERE c.id = p.category_id
      AND c.parent_id = 1
);
-- Хорошо для проверки существования
```

#### Устранение зависимых подзапросов
```sql
-- Зависимый подзапрос (выполняется для каждой строки)
SELECT u.name,
       (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count
FROM users u;

-- Оптимизация с JOIN
SELECT u.name, COUNT(o.id) AS order_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name;

-- Оптимизация с предварительным агрегированием
WITH user_order_counts AS (
    SELECT user_id, COUNT(*) AS order_count
    FROM orders
    GROUP BY user_id
)
SELECT u.name, COALESCE(uoc.order_count, 0) AS order_count
FROM users u
LEFT JOIN user_order_counts uoc ON u.id = uoc.user_id;
```

### Оптимизация агрегатных запросов

#### Эффективные агрегаты
```sql
-- Неоптимальный запрос
SELECT
    DATE(order_date) AS date,
    COUNT(*) AS orders,
    SUM(total_amount) AS revenue,
    AVG(total_amount) AS avg_order
FROM orders
GROUP BY DATE(order_date)
ORDER BY date DESC;
-- Полное сканирование без индекса

-- Оптимизация с индексом
CREATE INDEX idx_orders_date_amount ON orders(order_date, total_amount);

-- Оптимизированный запрос использует индекс
EXPLAIN SELECT
    DATE(order_date) AS date,
    COUNT(*) AS orders,
    SUM(total_amount) AS revenue,
    AVG(total_amount) AS avg_order
FROM orders
WHERE order_date >= '2024-01-01'
GROUP BY DATE(order_date)
ORDER BY date DESC;
```

#### Loose Index Scan
```sql
-- Использование loose index scan для MIN/MAX
SELECT
    category,
    MIN(price) AS min_price,
    MAX(price) AS max_price
FROM products
GROUP BY category;
-- Использует индекс по (category, price)

-- Создание подходящего индекса
CREATE INDEX idx_products_category_price ON products(category, price);
```

### Оптимизация сортировки

#### Файловая сортировка vs индексная
```sql
-- Файловая сортировка (Using filesort)
EXPLAIN SELECT * FROM users ORDER BY last_login DESC;
-- Extra: Using filesort

-- Индексная сортировка
CREATE INDEX idx_users_last_login ON users(last_login DESC);
EXPLAIN SELECT * FROM users ORDER BY last_login DESC;
-- Extra: (пусто - использует индекс)
```

#### Оптимизация LIMIT с ORDER `BY`
```sql
-- Неоптимальный запрос
SELECT * FROM orders
ORDER BY total_amount DESC
LIMIT 10;
-- Сканирует всю таблицу

-- Оптимизация с индексом
CREATE INDEX idx_orders_amount_date ON orders(total_amount DESC, order_date DESC);

SELECT * FROM orders
ORDER BY total_amount DESC, order_date DESC
LIMIT 10;
-- Использует индекс для быстрого нахождения топ 10
```

## Оптимизация индексов

### Выбор правильных индексов

#### Анализ паттернов запросов
```sql
-- Поиск часто используемых условий WHERE
SELECT
    'WHERE condition' AS pattern,
    COUNT(*) AS usage_count,
    GROUP_CONCAT(DISTINCT table_name) AS tables
FROM query_log ql
WHERE ql.query LIKE '%WHERE%'
  AND ql.execution_time > 1000000  -- > 1ms
GROUP BY pattern
ORDER BY usage_count DESC
LIMIT 20;
```

#### Создание покрывающих индексов
```sql
-- Анализ для покрывающего индекса
EXPLAIN SELECT user_id, order_date, total_amount
FROM orders
WHERE user_id = ? AND order_date >= ?
ORDER BY order_date;

-- Создание покрывающего индекса
CREATE INDEX idx_orders_covering
ON orders(user_id, order_date, total_amount);

-- Теперь запрос полностью обслуживается индексом
EXPLAIN SELECT user_id, order_date, total_amount
FROM orders
WHERE user_id = ? AND order_date >= ?
ORDER BY order_date;
-- Extra: Using index
```

### Перестройка индексов

#### Когда перестраивать
```sql
-- После массовых изменений данных
ANALYZE TABLE large_table;

-- Проверка фрагментации
SELECT
    table_name,
    data_free / 1024 / 1024 AS fragmentation_mb
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND data_free > 10 * 1024 * 1024; -- > 10MB

-- Перестройка фрагментированных таблиц
OPTIMIZE TABLE fragmented_table;
```

#### Удаление неиспользуемых индексов
```sql
-- Поиск неиспользуемых индексов
SELECT
    'DROP INDEX ' + i.index_name + ' ON ' + i.table_name + ';' AS drop_statement,
    t.table_rows,
    i.cardinality
FROM information_schema.statistics i
JOIN information_schema.tables t ON i.table_schema = t.table_schema
    AND i.table_name = t.table_name
LEFT JOIN performance_schema.table_io_waits_summary_by_index_usage u
    ON i.table_schema = u.object_schema
    AND i.table_name = u.object_name
    AND i.index_name = u.index_name
WHERE i.table_schema = DATABASE()
  AND i.index_name != 'PRIMARY'
  AND u.index_name IS NULL; -- Не используется
```

### Индексные стратегии

#### Composite indexes
```sql
-- Правильный порядок столбцов
CREATE INDEX idx_orders_user_date_amount
ON orders(user_id, order_date, total_amount);

-- Эффективные запросы:
SELECT * FROM orders WHERE user_id = 1;
SELECT * FROM orders WHERE user_id = 1 AND order_date >= '2024-01-01';
SELECT * FROM orders WHERE user_id = 1 AND order_date >= '2024-01-01' AND total_amount > 100;

-- ORDER BY использует индекс:
SELECT * FROM orders WHERE user_id = 1 ORDER BY order_date, total_amount;
```

#### Partial indexes
```sql
-- Индекс только для активных записей
CREATE INDEX idx_active_users_email
ON users(email)
WHERE active = true;

-- Индекс для недавних данных
CREATE INDEX idx_recent_orders_date
ON orders(order_date, total_amount)
WHERE order_date >= '2024-01-01';
```

## Оптимизация хранения

### Выбор движка хранения

#### InnoDB vs MyISAM
```sql
-- InnoDB (рекомендуется)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- MyISAM (для специфических случаев)
CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM;
```

#### MEMORY таблицы
```sql
-- Таблицы в памяти для кэширования
CREATE TABLE cache_data (
    cache_key VARCHAR(255) PRIMARY KEY,
    cache_value TEXT,
    expires_at TIMESTAMP
) ENGINE=MEMORY;

-- Автоматическая очистка истекших записей
CREATE EVENT cleanup_expired_cache
ON SCHEDULE EVERY 1 HOUR
DO
    DELETE FROM cache_data WHERE expires_at < NOW();
```

### Оптимизация структуры таблиц

#### Нормализация vs денормализация
```sql
-- Нормализованная структура (лучше для обновлений)
CREATE TABLE orders (
    id INT PRIMARY KEY,
    user_id INT,
    product_id INT,
    quantity INT,
    unit_price DECIMAL(10,2),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Денормализованная структура (лучше для чтения)
CREATE TABLE order_summary (
    id INT PRIMARY KEY,
    user_name VARCHAR(100),
    product_name VARCHAR(100),
    quantity INT,
    total_price DECIMAL(10,2),
    order_date DATE
);
```

#### Архивация старых данных
```sql
-- Перемещение старых данных в архив
CREATE TABLE orders_archive LIKE orders;

INSERT INTO orders_archive
SELECT * FROM orders
WHERE order_date < '2020-01-01';

DELETE FROM orders WHERE order_date < '2020-01-01';

-- Оптимизация после очистки
OPTIMIZE TABLE orders;
```

### Партиционирование

#### Range partitioning
```sql
-- Партиционирование по датам
CREATE TABLE orders (
    id INT AUTO_INCREMENT,
    user_id INT,
    order_date DATE,
    total_amount DECIMAL(10,2),
    PRIMARY KEY (id, order_date)
)
PARTITION BY RANGE (YEAR(order_date)) (
    PARTITION p2020 VALUES LESS THAN (2021),
    PARTITION p2021 VALUES LESS THAN (2022),
    PARTITION p2022 VALUES LESS THAN (2023),
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- Добавление новой партиции
ALTER TABLE orders ADD PARTITION (
    PARTITION p2024 VALUES LESS THAN (2025)
);
```

#### Hash partitioning
```sql
-- Равномерное распределение
CREATE TABLE user_sessions (
    session_id VARCHAR(36) PRIMARY KEY,
    user_id INT,
    data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
PARTITION BY HASH(user_id) PARTITIONS 8;
```

## Кэширование

### Query Cache (MySQL 5.7)

#### Настройка и мониторинг
```ini
# Конфигурация query cache
[mysqld]
query_cache_size = 256M
query_cache_type = ON
query_cache_limit = 1M
query_cache_min_res_unit = 4096
```

```sql
-- Мониторинг эффективности
SHOW STATUS LIKE 'Qcache%';

-- Qcache_hits / Qcache_inserts - hit rate
SELECT
    'Hit Rate' AS metric,
    ROUND((@@Qcache_hits / (@@Qcache_hits + @@Qcache_inserts)) * 100, 2) AS value
FROM dual;
```

### Application-level caching

#### Spring Cache
```java
@Service
public class CachedUserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "userStats", key = "#userId")
    public UserStats getUserStats(Long userId) {
        // Дорогой запрос
        return calculateUserStats(userId);
    }

    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @CacheEvict(value = {"users", "userStats"}, key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

#### Redis для кэширования
```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Сериализация
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)))
            .build();
    }
}
```

### Кэширование результатов запросов

#### Материализованные представления
```sql
-- Материализованное представление для статистики
CREATE TABLE user_stats_cache (
    user_id INT PRIMARY KEY,
    total_orders INT,
    total_spent DECIMAL(10,2),
    last_order_date DATE,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Процедура обновления кэша
DELIMITER //

CREATE PROCEDURE refresh_user_stats()
BEGIN
    REPLACE INTO user_stats_cache
    SELECT
        u.id AS user_id,
        COUNT(o.id) AS total_orders,
        COALESCE(SUM(o.total_amount), 0) AS total_spent,
        MAX(o.order_date) AS last_order_date,
        NOW() AS last_updated
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    GROUP BY u.id;
END //

DELIMITER ;

-- Автоматическое обновление
CREATE EVENT refresh_user_stats_event
ON SCHEDULE EVERY 1 HOUR
DO CALL refresh_user_stats();
```

## Мониторинг и алерты

### Performance Schema

#### Включение мониторинга
```sql
-- Включение Performance Schema
UPDATE performance_schema.setup_instruments
SET ENABLED = 'YES', TIMED = 'YES'
WHERE NAME LIKE 'statement/%'
   OR NAME LIKE 'wait/%';

-- Создание сводного отчета
CREATE TABLE performance_report (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_date DATE,
    total_queries BIGINT,
    slow_queries BIGINT,
    avg_query_time DECIMAL(10,6),
    cache_hit_rate DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Сбор метрик
```sql
-- Процедура сбора метрик производительности
DELIMITER //

CREATE PROCEDURE collect_performance_metrics()
BEGIN
    DECLARE total_q BIGINT;
    DECLARE slow_q BIGINT;
    DECLARE avg_time DECIMAL(10,6);
    DECLARE cache_hits DECIMAL(5,2);

    -- Общее количество запросов
    SELECT VARIABLE_VALUE INTO total_q
    FROM performance_schema.global_status
    WHERE VARIABLE_NAME = 'Queries';

    -- Медленные запросы
    SELECT VARIABLE_VALUE INTO slow_q
    FROM performance_schema.global_status
    WHERE VARIABLE_NAME = 'Slow_queries';

    -- Среднее время запроса
    SELECT AVG(avg_timer_wait) / 1000000000 INTO avg_time
    FROM performance_schema.events_statements_summary_by_digest
    WHERE avg_timer_wait > 0;

    -- Эффективность кэша
    SELECT
        CASE
            WHEN (@@Qcache_hits + @@Qcache_inserts) > 0
            THEN (@@Qcache_hits / (@@Qcache_hits + @@Qcache_inserts)) * 100
            ELSE 0
        END INTO cache_hits;

    -- Сохранение отчета
    INSERT INTO performance_report
    (report_date, total_queries, slow_queries, avg_query_time, cache_hit_rate)
    VALUES (CURDATE(), total_q, slow_q, avg_time, cache_hits);
END //

DELIMITER ;
```

### Системы мониторинга

#### PMM (Percona `Monitoring and` Management)
```bash
# Установка PMM Client
wget https://www.percona.com/downloads/pmm2/2.0.0/binary/tarball/pmm2-client-2.0.0.tar.gz
tar -xzf pmm2-client-2.0.0.tar.gz
cd pmm2-client-2.0.0
./install

# Регистрация сервера
pmm-admin config --server pmm-server:443
pmm-admin add mysql --username root --password secret mysql-server
```

#### Пользовательские алерты
```sql
-- Создание таблицы алертов
CREATE TABLE system_alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alert_type VARCHAR(50),
    severity ENUM('low', 'medium', 'high', 'critical'),
    message TEXT,
    metric_value DECIMAL(10,2),
    threshold_value DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Процедура проверки алертов
DELIMITER //

CREATE PROCEDURE check_system_alerts()
BEGIN
    DECLARE connection_count INT;
    DECLARE slow_query_pct DECIMAL(5,2);

    -- Проверка количества подключений
    SELECT VARIABLE_VALUE INTO connection_count
    FROM performance_schema.global_status
    WHERE VARIABLE_NAME = 'Threads_connected';

    IF connection_count > 80 THEN
        INSERT INTO system_alerts (alert_type, severity, message, metric_value, threshold_value)
        VALUES ('connections', 'high', 'High connection count', connection_count, 80);
    END IF;

    -- Проверка процента медленных запросов
    SELECT (slow_queries / queries) * 100 INTO slow_query_pct
    FROM (
        SELECT
            (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Slow_queries') AS slow_queries,
            (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Queries') AS queries
    ) stats;

    IF slow_query_pct > 5.0 THEN
        INSERT INTO system_alerts (alert_type, severity, message, metric_value, threshold_value)
        VALUES ('slow_queries', 'medium', 'High slow query percentage', slow_query_pct, 5.0);
    END IF;
END //

DELIMITER ;
```

## Инструменты профилирования

### Percona Toolkit

#### Анализ медленных запросов
```bash
# Анализ slow log
pt-query-digest /var/log/mysql/mysql-slow.log

# Вывод топ запросов по времени выполнения
pt-query-digest --top 10 --report /var/log/mysql/mysql-slow.log

# Анализ по времени
pt-query-digest --since '2024-01-01' --until '2024-01-02' /var/log/mysql/mysql-slow.log
```

#### Анализ индексов
```bash
# Поиск дублированных индексов
pt-duplicate-key-checker --host localhost --user root --password

# Анализ использования индексов
pt-index-usage /var/log/mysql/mysql.log --host localhost

# Предложения по индексам
pt-query-advisor /var/log/mysql/mysql-slow.log
```

### MySQL Enterprise Monitor

#### Настройка мониторинга
```sql
-- Создание пользователя для мониторинга
CREATE USER 'monitor'@'%' IDENTIFIED BY 'monitor_password';
GRANT PROCESS, REPLICATION CLIENT, SELECT ON *.* TO 'monitor'@'%';

-- Предоставление доступа к performance_schema
GRANT SELECT ON performance_schema.* TO 'monitor'@'%';
```

#### Автоматические отчеты
```sql
-- Создание отчета о состоянии сервера
SELECT
    NOW() AS report_time,
    VERSION() AS mysql_version,
    @@innodb_buffer_pool_size / 1024 / 1024 / 1024 AS buffer_pool_gb,
    @@max_connections AS max_connections,
    (SELECT COUNT(*) FROM information_schema.processlist) AS active_connections,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status
     WHERE VARIABLE_NAME = 'Slow_queries') AS slow_queries
FROM dual;
```

### SHOW PROCESSLIST и анализ

```sql
-- Анализ активных подключений
SELECT
    id,
    user,
    host,
    db,
    command,
    time AS time_seconds,
    state,
    LEFT(info, 100) AS query_preview
FROM information_schema.processlist
WHERE command != 'Sleep'
ORDER BY time DESC
LIMIT 10;

-- Поиск долго выполняющихся запросов
SELECT
    id,
    user,
    time,
    LEFT(info, 200) AS query
FROM information_schema.processlist
WHERE time > 30  -- > 30 секунд
  AND command = 'Query'
ORDER BY time DESC;
```

## Автоматическая оптимизация

### MySQL `8.0` Invisible Indexes

#### Создание невидимых индексов
```sql
-- Создание невидимого индекса для тестирования
CREATE INDEX idx_test_invisible ON large_table(column) INVISIBLE;

-- Тестирование влияния
EXPLAIN SELECT * FROM large_table WHERE column = 'test';

-- Сделать видимым если улучшает производительность
ALTER TABLE large_table ALTER INDEX idx_test_invisible VISIBLE;

-- Удалить если не помогает
DROP INDEX idx_test_invisible ON large_table;
```

### Автоматический анализ

#### Регулярный анализ таблиц
```sql
-- Создание события для автоматического анализа
DELIMITER //

CREATE EVENT weekly_table_analysis
ON SCHEDULE EVERY 1 WEEK STARTS '2024-01-01 02:00:00'
DO
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_name VARCHAR(255);
    DECLARE cur CURSOR FOR
        SELECT TABLE_NAME
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_TYPE = 'BASE TABLE';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    analyze_loop: LOOP
        FETCH cur INTO table_name;
        IF done THEN
            LEAVE analyze_loop;
        END IF;

        -- Анализ таблицы
        SET @sql = CONCAT('ANALYZE TABLE ', table_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;

    CLOSE cur;
END //

DELIMITER ;
```

### Query Rewrite Plugin

#### Автоматическая перезапись запросов
```sql
-- Включение query rewrite plugin
INSTALL PLUGIN rewriter SONAME 'rewriter.so';

-- Создание правил перезаписи
INSERT INTO query_rewrite.rewrite_rules
(pattern, replacement, enabled)
VALUES (
    'SELECT * FROM users WHERE id = ?',
    'SELECT id, name, email FROM users WHERE id = ?',
    'Y'
);

-- Перезагрузка правил
CALL query_rewrite.flush_rewrite_rules();
```

## Масштабирование

### Read/Write Splitting

#### Настройка репликации для масштабирования
```java
@Configuration
public class ReadWriteDataSourceConfig {

    @Bean
    @Qualifier("masterDataSource")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://master:3306/myapp")
            .username("write_user")
            .password("password")
            .build();
    }

    @Bean
    @Qualifier("slaveDataSource")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://slave:3306/myapp")
            .username("read_user")
            .password("password")
            .build();
    }

    @Bean
    public DataSource routingDataSource(
        @Qualifier("masterDataSource") DataSource master,
        @Qualifier("slaveDataSource") DataSource slave) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", master);
        targetDataSources.put("slave", slave);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(master);

        return routingDataSource;
    }
}
```

### Шардинг

#### Разделение данных по шардам
```java
@Service
public class ShardingService {

    private final Map<String, DataSource> shardDataSources;

    public DataSource getShardDataSource(String shardKey) {
        // Определение шарда по ключу
        int shardId = Math.abs(shardKey.hashCode()) % shardDataSources.size();
        return shardDataSources.get("shard_" + shardId);
    }

    public void saveUser(User user) {
        DataSource shard = getShardDataSource(user.getEmail());
        // Использование соответствующего шарда
        JdbcTemplate jdbcTemplate = new JdbcTemplate(shard);
        // Сохранение данных
    }
}
```

### Кэширование и CDN

#### Multi-level caching
```java
@Service
@CacheConfig(cacheNames = "users")
public class MultiLevelCacheService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, User> redisTemplate;

    @Cacheable(key = "#id")
    public User getUser(Long id) {
        // Сначала проверяем Redis
        User user = redisTemplate.opsForValue().get("user:" + id);
        if (user != null) {
            return user;
        }

        // Если нет в Redis, читаем из БД
        user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Кэшируем в Redis на 10 минут
            redisTemplate.opsForValue().set("user:" + id, user, 10, TimeUnit.MINUTES);
        }

        return user;
    }
}
```

## Решение проблем

### Распространенные проблемы производительности

#### Высокая загрузка CPU
```sql
-- Поиск причин высокой загрузки CPU
SELECT
    thread_id,
    processlist_user,
    processlist_command,
    processlist_time,
    LEFT(processlist_info, 100) AS query
FROM performance_schema.threads
WHERE processlist_command = 'Query'
ORDER BY processlist_time DESC
LIMIT 10;

-- Анализ ожиданий
SELECT
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec
FROM performance_schema.events_waits_summary_global_by_event_name
WHERE event_name LIKE 'wait/%'
ORDER BY sum_timer_wait DESC
LIMIT 10;
```

#### Высокое использование памяти
```sql
-- Анализ использования памяти
SELECT
    'Buffer Pool' AS component,
    @@innodb_buffer_pool_size / 1024 / 1024 / 1024 AS size_gb
FROM dual

UNION ALL

SELECT
    'Query Cache',
    @@query_cache_size / 1024 / 1024 / 1024
FROM dual

UNION ALL

SELECT
    'Thread Stack',
    @@max_connections * @@thread_stack / 1024 / 1024 / 1024
FROM dual;
```

#### Проблемы с дисками
```sql
-- Анализ I/O производительности
SELECT
    file_name,
    count_read,
    count_write,
    sum_number_of_bytes_read / 1024 / 1024 AS read_mb,
    sum_number_of_bytes_write / 1024 / 1024 AS write_mb,
    (sum_timer_wait / 1000000000) / count_star AS avg_wait_sec
FROM performance_schema.file_summary_by_instance
WHERE event_name LIKE 'wait/io/file/%'
ORDER BY sum_timer_wait DESC
LIMIT 10;
```

### Диагностика медленных запросов

#### Анализ конкретного запроса
```sql
-- Детальный анализ медленного запроса
EXPLAIN FORMAT=JSON
SELECT u.name, COUNT(o.id), AVG(o.total_amount)
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name;

-- Проверка индексов
SHOW INDEXES FROM users;
SHOW INDEXES FROM orders;

-- Анализ статистики
ANALYZE TABLE users, orders;
```

#### Оптимизация конкретного случая
```sql
-- Создание покрывающего индекса
CREATE INDEX idx_users_orders_covering
ON users(id, name, created_at);

CREATE INDEX idx_orders_user_amount
ON orders(user_id, total_amount);

-- Повторный анализ
EXPLAIN FORMAT=JSON
SELECT u.name, COUNT(o.id), AVG(o.total_amount)
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name;
```

### Проблемы с репликацией

#### Диагностика задержки репликации
```sql
-- Проверка статуса на master
SHOW MASTER STATUS;

-- Проверка статуса на slave
SHOW SLAVE STATUS\G

-- Если есть задержка, проверка причин
SHOW PROCESSLIST; -- Долго выполняющиеся запросы
SHOW ENGINE INNODB STATUS; -- Блокировки
```

#### Исправление распространенных проблем
```sql
-- Пропуск ошибок репликации (осторожно!)
STOP SLAVE;
SET GLOBAL sql_slave_skip_counter = 1;
START SLAVE;

-- Перестройка репликации
STOP SLAVE;
RESET SLAVE ALL;
CHANGE MASTER TO MASTER_AUTO_POSITION = 1;
START SLAVE;
```

## Лучшие практики

### Проектирование для производительности

#### 1. Нормализация vs производительность
- **Начинайте с нормализованной схемы**
- **Денормализуйте только при необходимости**
- **Используйте материализованные представления**
- **Документируйте причины денормализации**

#### 2. Выбор типов данных
- **Используйте минимально достаточные типы**
- **Учитывайте `NULL` значения**
- **Оптимизируйте для частых операций**
- **Планируйте рост данных**

#### 3. Индексация
- **Анализируйте паттерны запросов**
- **Создавайте составные индексы**
- **Используйте покрывающие индексы**
- **Мониторьте использование индексов**

### Оптимизация запросов

#### 1. Написание эффективных запросов
- **Избегайте `SELECT` **
- **Используйте `LIMIT` для больших наборов**
- **Оптимизируйте условия WHERE**
- **Выбирайте правильные типы JOIN**

#### 2. Анализ и мониторинг
- **Регулярно анализируйте EXPLAIN**
- **Мониторьте slow queries**
- **Используйте `Performance` Schema**
- **Автоматизируйте сбор метрик**

#### 3. Кэширование
- **Используйте `Query Cache` (MySQL 5.7)**
- **Внедряйте `application-level` caching**
- **Рассматривайте `Redis`/Memcached**
- **Кэшируйте вычисляемые данные**

### Конфигурация сервера

#### 1. InnoDB оптимизация
- **Настройте buffer pool (70-80% памяти)**
- **Оптимизируйте log files**
- **Настройте flush параметры**
- **Используйте `SSD` оптимизации**

#### 2. Подключения и потоки
- **Настройте connection pool**
- **Оптимизируйте `thread` settings**
- **Настройте table cache**
- **Управляйте temporary tables**

#### 3. Мониторинг и обслуживание
- **Регулярно анализируйте таблицы**
- **Перестраивайте индексы**
- **Мониторьте фрагментацию**
- **Оптимизируйте на основе метрик**

### Масштабирование

#### 1. Вертикальное масштабирование
- **Увеличивайте ресурсы сервера**
- **Оптимизируйте конфигурацию**
- **Используйте SSD**
- **Настройте RAID**

#### 2. Горизонтальное масштабирование
- **Read replicas для чтения**
- **Sharding для больших объемов**
- **Кэширование для горячих данных**
- **CDN для статического контента**

#### 3. Архитектурные решения
- **Микросервисы для разделения нагрузки**
- **API `Gateway` для балансировки**
- **Message queues для асинхронной обработки**
- **Event sourcing для сложных систем**

### Безопасность и производительность

#### 1. Защищенные запросы
- **Используйте prepared statements**
- **Валидируйте входные данные**
- **Ограничивайте права пользователей**
- **Мониторьте подозрительную активность**

#### 2. Профилирование и отладка
- **Ведите логи медленных запросов**
- **Используйте `EXPLAIN` для анализа**
- **Профилируйте приложение**
- **Мониторьте системные ресурсы**

### Автоматизация и DevOps

#### 1. Автоматизированное тестирование
- **Нагрузочное тестирование**
- **Тестирование производительности**
- **Регрессионное тестирование**
- **A/B тестирование оптимизаций**

#### 2. Мониторинг и алерты
- **Устанавливайте базовые метрики**
- **Настраивайте алерты**
- **Автоматизируйте отчеты**
- **Интегрируйте с системами мониторинга**

#### 3. CI/CD для баз данных
- **Миграции как код**
- **Автоматизированное тестирование схемы**
- **Роллбэк планы**
- **Документирование изменений**

### Непрерывная оптимизация

#### 1. Цикл оптимизации
- **Мониторинг Анализ Оптимизация Тестирование Мониторинг**
- **Регулярные аудиты производительности**
- **Планирование capacity**
- **Подготовка к росту нагрузки**

#### 2. Документирование решений
- **Записывайте причины изменений**
- **Документируйте влияние на производительность**
- **Ведите changelog оптимизаций**
- **Делитесь знаниями с командой**

### Выбор правильных инструментов

#### Для разных сценариев
- **MySQL 8.0** для новых проектов
- **Percona Server** для высокой производительности
- **MariaDB** для совместимости и инноваций
- **AWS `RDS`/Aurora** для облачных решений

#### Мониторинг и инструменты
- **PMM** для комплексного мониторинга
- **Grafana + Prometheus** для визуализации
- **Percona Toolkit** для анализа и оптимизации
- **sysbench** для нагрузочного тестирования

Производительность **MySQL** — это непрерывный процесс оптимизации, требующий глубокого понимания системы, тщательного мониторинга и систематического подхода к улучшениям.

