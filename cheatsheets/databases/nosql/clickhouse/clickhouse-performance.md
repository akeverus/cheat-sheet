---
title: "ClickHouse: Производительность - Полное руководство по оптимизации и тюнингу"
description: "Комплексное руководство по оптимизации производительности ClickHouse: конфигурация, запросы, оборудование, мониторинг и best practices"
tags:
  - clickhouse
  - performance
  - optimization
  - tuning
  - monitoring
  - hardware
  - configuration
difficulty: "advanced"
prerequisites: ["databases/clickhouse-replication.md"]
updated: "2026-02-06"
related: ["databases/clickhouse-replication.md", "databases/clickhouse-indexes.md"]
---

# **ClickHouse**: Производительность — Полное руководство по оптимизации и тюнингу

Комплексное руководство по оптимизации производительности **ClickHouse**: конфигурация, запросы, оборудование, мониторинг и **best practices**.

## Полезные ссылки

### Официальная документация
- [Performance Optimization](https://clickhouse.com/docs/en/operations/optimizing-performance)
- [Configuration Parameters](https://clickhouse.com/docs/en/operations/configuration-files)
- [System Tables](https://clickhouse.com/docs/en/operations/system-tables)

### Обучающие материалы
- [ClickHouse Performance Tuning](https://www.baeldung.com/clickhouse-performance)

### См. также
- [[clickhouse-replication|Репликация]] — кластеры и репликация
- [[clickhouse-indexes|Индексы]] — индексы и партиционирование

## Содержание

- [Введение в оптимизацию производительности](#введение-в-оптимизацию-производительности)
  - [Основные метрики производительности](#основные-метрики-производительности)
  - [Профилирование производительности](#профилирование-производительности)
- [Аппаратное обеспечение](#аппаратное-обеспечение)
  - [Рекомендации по оборудованию](#рекомендации-по-оборудованию)
    - [**CPU**](#cpu)
- [Оптимальная конфигурация CPU](#оптимальная-конфигурация-cpu)
- [Проверка CPU](#проверка-cpu)
    - [Память (**RAM**)](#память-ram)
- [Минимум: 32GB для небольших кластеров](#минимум-32gb-для-небольших-кластеров)
- [Рекомендуется: 128GB+ для production](#рекомендуется-128gb-для-production)
- [Максимум: Ограничено только ОС](#максимум-ограничено-только-ос)
- [Проверка памяти](#проверка-памяти)
    - [Дисковая подсистема](#дисковая-подсистема)
- [Рекомендации:](#рекомендации)
- [Проверка дисков](#проверка-дисков)
    - [Сеть](#сеть)
- [Для кластеров:](#для-кластеров)
- [Проверка сети](#проверка-сети)
  - [Оптимизация **Linux**](#оптимизация-linux)
- [Отключение SWAP (критично для ClickHouse)](#отключение-swap-критично-для-clickhouse)
- [Настройка vm.max_map_count](#настройка-vmmax_map_count)
- [Настройка ulimits](#настройка-ulimits)
- [Отключение transparent huge pages](#отключение-transparent-huge-pages)
- [Настройка I/O scheduler](#настройка-io-scheduler)
- [Настройка dirty pages](#настройка-dirty-pages)
- [Конфигурация сервера](#конфигурация-сервера)
  - [Основные параметры конфигурации](#основные-параметры-конфигурации)
  - [Оптимизация **MergeTree**](#оптимизация-mergetree)
  - [Настройки для высоких нагрузок](#настройки-для-высоких-нагрузок)
- [Оптимизация запросов](#оптимизация-запросов)
  - [Основные принципы оптимизации](#основные-принципы-оптимизации)
  - [Оптимизация агрегаций](#оптимизация-агрегаций)
  - [Оптимизация оконных функций](#оптимизация-оконных-функций)
  - [Кэширование результатов](#кэширование-результатов)
- [Оптимизация хранения](#оптимизация-хранения)
  - [Выбор формата сжатия](#выбор-формата-сжатия)
  - [Оптимизация партиционирования](#оптимизация-партиционирования)
  - [Настройка **TTL**](#настройка-ttl)
- [Оптимизация вставки данных](#оптимизация-вставки-данных)
  - [**Batch** вставка](#batch-вставка)
  - [Оптимизация для высоких нагрузок](#оптимизация-для-высоких-нагрузок)
  - [Асинхронная вставка](#асинхронная-вставка)
- [Мониторинг производительности](#мониторинг-производительности)
  - [Системные метрики](#системные-метрики)
  - [Метрики запросов](#метрики-запросов)
  - [Мониторинг таблиц](#мониторинг-таблиц)
- [Диагностика проблем](#диагностика-проблем)
  - [Высокая латентность запросов](#высокая-латентность-запросов)
  - [Высокое использование **CPU**](#высокое-использование-cpu)
  - [Проблемы с памятью](#проблемы-с-памятью)
  - [Дисковые проблемы](#дисковые-проблемы)
- [**Best Practices**](#лучшие-практики)
  - [Мониторинг и обслуживание](#мониторинг-и-обслуживание)
  - [Производственные настройки](#производственные-настройки)
  - [Ключевые факторы успеха:](#ключевые-факторы-успеха)
  - [Основные метрики для мониторинга:](#основные-метрики-для-мониторинга)
  - [Следующие темы:](#следующие-темы)

## Введение в оптимизацию производительности

Производительность **ClickHouse** зависит от множества факторов: аппаратного обеспечения, конфигурации, структуры данных и паттернов запросов. Правильная оптимизация может дать прирост производительности в десятки и сотни раз.

### Основные метрики производительности

1. **Throughput**: Количество операций в секунду
2. **Latency**: Время отклика запросов
3. **Resource Utilization**: Использование **CPU**, памяти, диска
4. **Query Performance**: Время выполнения запросов
5. **Data `Ingestion` Rate**: Скорость загрузки данных

### Профилирование производительности

```sql
-- Включение логирования запросов
SET log_queries = 1;

-- Установка минимального времени логирования (мс)
SET log_queries_min_query_duration_ms = 100;

-- Просмотр медленных запросов
SELECT
    query,
    query_duration_ms,
    read_rows,
    read_bytes,
    result_rows,
    memory_usage
FROM system.query_log
WHERE query_duration_ms > 1000
ORDER BY query_duration_ms DESC
LIMIT 10;

-- Анализ использования ресурсов
SELECT
    query,
    formatReadableSize(read_bytes) as data_read,
    formatReadableSize(memory_usage) as memory_used,
    read_rows / query_duration_ms * 1000 as rows_per_second
FROM system.query_log
WHERE type = 'QueryFinish'
ORDER BY read_bytes DESC
LIMIT 10;
```

## Аппаратное обеспечение

### Рекомендации по оборудованию

#### **CPU**
```bash
# Оптимальная конфигурация CPU
# - 8+ ядер для аналитических нагрузок
# - Высокая частота процессора (3.0+ GHz)
# - Поддержка SIMD инструкций (AVX-512)

# Проверка CPU
lscpu | grep -E "(Architecture|CPU\(s\)|Model name|CPU MHz)"
```

#### Память (**RAM**)
```bash
# Минимум: 32GB для небольших кластеров
# Рекомендуется: 128GB+ для production
# Максимум: Ограничено только ОС

# Проверка памяти
free -h
cat /proc/meminfo | grep MemTotal
```

#### Дисковая подсистема
```bash
# Рекомендации:
# - NVMe SSD для максимальной производительности
# - RAID 10 для отказоустойчивости
# - Отдельные диски для данных и логов

# Проверка дисков
lsblk -d
df -h
fio --name=randread --rw=randread --bs=4k --size=1g --numjobs=4 --runtime=60 --time_based
```

#### Сеть
```bash
# Для кластеров:
# - 10GbE минимум
# - 40GbE/100GbE для больших кластеров

# Проверка сети
ethtool eth0 | grep Speed
iperf3 -c target_host
```

### Оптимизация **Linux**

```bash
# Отключение SWAP (критично для ClickHouse)
sudo swapoff -a
sudo sed -i '/swap/d' /etc/fstab

# Настройка vm.max_map_count
sudo sysctl -w vm.max_map_count=262144

# Настройка ulimits
echo "* soft nofile 262144" | sudo tee -a /etc/security/limits.conf
echo "* hard nofile 262144" | sudo tee -a /etc/security/limits.conf

# Отключение transparent huge pages
echo never | sudo tee /sys/kernel/mm/transparent_hugepage/enabled
echo never | sudo tee /sys/kernel/mm/transparent_hugepage/defrag

# Настройка I/O scheduler
echo mq-deadline | sudo tee /sys/block/nvme0n1/queue/scheduler

# Настройка dirty pages
sudo sysctl -w vm.dirty_ratio=80
sudo sysctl -w vm.dirty_background_ratio=5
```

## Конфигурация сервера

### Основные параметры конфигурации

```xml
<!-- config.xml -->
<clickhouse>
    <!-- Максимальное количество соединений -->
    <max_connections>4096</max_connections>

    <!-- Размер пула потоков -->
    <max_thread_pool_size>10000</max_thread_pool_size>

    <!-- Максимальный размер запроса (bytes) -->
    <max_query_size>104857600</max_query_size> <!-- 100MB -->

    <!-- Таймауты -->
    <max_execution_time>300</max_execution_time> <!-- 5 минут -->
    <interactive_delay>100000</interactive_delay> <!-- 100ms -->

    <!-- Ограничения памяти -->
    <max_memory_usage>10737418240</max_memory_usage> <!-- 10GB -->
    <max_memory_usage_for_user>8589934592</max_memory_usage_for_user> <!-- 8GB -->

    <!-- Настройки сжатия -->
    <compression>
        <case>
            <method>lz4</method> <!-- Быстрое сжатие -->
        </case>
        <case>
            <min_part_size>1000000000</min_part_size> <!-- 1GB -->
            <method>zstd</method> <!-- Лучшее сжатие для больших партиций -->
        </case>
    </compression>
</clickhouse>
```

### Оптимизация **MergeTree**

```xml
<!-- merge_tree.xml -->
<clickhouse>
    <merge_tree>
        <!-- Максимальное количество слияний в очереди -->
        <max_parts_in_total>10000</max_parts_in_total>

        <!-- Размер блока для сжатия -->
        <min_compress_block_size>65536</min_compress_block_size>

        <!-- Максимальный размер партиции -->
        <max_part_loading_threads>8</max_part_loading_threads>

        <!-- Настройки репликации -->
        <replicated_max_parallel_sends>4</replicated_max_parallel_sends>
        <replicated_max_parallel_fetches>4</replicated_max_parallel_fetches>
    </merge_tree>
</clickhouse>
```

### Настройки для высоких нагрузок

```xml
<!-- high_load.xml -->
<clickhouse>
    <!-- Оптимизация для CPU -->
    <background_pool_size>16</background_pool_size>
    <background_merges_max_threads>8</background_merges_max_threads>

    <!-- Оптимизация памяти -->
    <max_memory_usage>53687091200</max_memory_usage> <!-- 50GB -->
    <max_memory_usage_for_all_queries>42949672960</max_memory_usage_for_all_queries> <!-- 40GB -->

    <!-- Оптимизация I/O -->
    <max_read_buffer_size>1048576</max_read_buffer_size> <!-- 1MB -->
    <prefer_localhost_replica>1</prefer_localhost_replica>

    <!-- Оптимизация сети -->
    <max_threads_for_new_connections>100</max_threads_for_new_connections>
    <max_queries_for_new_connections>100</max_queries_for_new_connections>
</clickhouse>
```

## Оптимизация запросов

### Основные принципы оптимизации

```sql
-- 1. Используйте PREWHERE для фильтрации
SELECT user_id, sum(amount)
FROM transactions
PREWHERE date >= '2024-01-01'  -- Фильтрация перед чтением
WHERE amount > 100
GROUP BY user_id;

-- 2. Оптимизируйте порядок JOIN
SELECT t.user_id, u.name, sum(t.amount) as total
FROM transactions t
JOIN users u ON t.user_id = u.user_id  -- Маленькая таблица слева
WHERE t.date >= '2024-01-01'
GROUP BY t.user_id, u.name;

-- 3. Используйте LIMIT для ограничения результатов
SELECT user_id, total_spent
FROM (
    SELECT user_id, sum(amount) as total_spent
    FROM transactions
    GROUP BY user_id
    ORDER BY total_spent DESC
    LIMIT 100  -- Ограничение перед финальной сортировкой
);

-- 4. Избегайте функций в WHERE
-- Плохо
SELECT * FROM events WHERE toDate(timestamp) = today();

-- Хорошо
SELECT * FROM events WHERE timestamp >= today() AND timestamp < tomorrow();
```

### Оптимизация агрегаций

```sql
-- Используйте двухэтапную агрегацию для больших данных
SELECT user_id, sum(total) as grand_total
FROM (
    SELECT user_id, sum(amount) as total
    FROM transactions
    GROUP BY user_id, toStartOfMonth(date)  -- Предварительная агрегация по месяцам
)
GROUP BY user_id;

-- Используйте приближенные функции
SELECT
    uniq(user_id) as exact_unique_users,
    uniqCombined(user_id) as approx_unique_users,  -- Быстрее для больших наборов
    count() as total_events
FROM events
SAMPLE 0.1;  -- На 10% данных
```

### Оптимизация оконных функций

```sql
-- Ограничьте рамку окна
SELECT
    date,
    user_id,
    amount,
    sum(amount) OVER (
        PARTITION BY user_id
        ORDER BY date
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW  -- Явная рамка
    ) as running_total
FROM transactions;

-- Используйте PARTITION BY для параллельной обработки
SELECT
    category,
    product_id,
    sales,
    row_number() OVER (
        PARTITION BY category  -- Параллельная обработка по категориям
        ORDER BY sales DESC
    ) as rank_in_category
FROM product_sales;
```

### Кэширование результатов

```sql
-- Создание кэширующего слоя
CREATE DICTIONARY user_cache (
    user_id UInt64,
    name String,
    last_login DateTime
)
PRIMARY KEY user_id
SOURCE(CLICKHOUSE(HOST 'localhost' PORT 9000 TABLE 'users'))
LIFETIME(MIN 300 MAX 3600)  -- Кэш на 5-60 минут
LAYOUT(FLAT());

-- Использование кэша
SELECT
    d.name,
    t.amount,
    d.last_login
FROM transactions t
JOIN user_cache d ON t.user_id = d.user_id;
```

## Оптимизация хранения

### Выбор формата сжатия

```xml
<!-- Разные алгоритмы сжатия для разных сценариев -->
<compression>
    <!-- LZ4 для скорости -->
    <case>
        <method>lz4</method>
    </case>

    <!-- ZSTD для лучшего сжатия -->
    <case>
        <min_part_size>1000000000</min_part_size> <!-- > 1GB -->
        <method>zstd</method>
        <level>3</level>
    </case>
</compression>
```

### Оптимизация партиционирования

```sql
-- Партиционирование по времени
CREATE TABLE events (
    timestamp DateTime,
    user_id UInt64,
    event_type String,
    data String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)  -- 12 партиций в год
ORDER BY (user_id, timestamp);

-- Партиционирование по значению
CREATE TABLE logs (
    level String,
    service String,
    message String,
    timestamp DateTime
) ENGINE = MergeTree()
PARTITION BY (toYYYYMM(timestamp), level)  -- Партиция + уровень
ORDER BY (service, timestamp);

-- Оптимизация размера партиций
ALTER TABLE events MODIFY TTL
DELETE WHERE timestamp < now() - INTERVAL 1 YEAR,
TO DISK 'hdd' WHERE timestamp < now() - INTERVAL 1 MONTH;  -- Перемещение на HDD
```

### Настройка **TTL**

```sql
-- Многоуровневое TTL
CREATE TABLE user_sessions (
    user_id UInt64,
    session_start DateTime,
    session_data String
) ENGINE = MergeTree()
ORDER BY (user_id, session_start)
TTL session_start + INTERVAL 1 DAY DELETE,  -- Удаление через день
    session_start + INTERVAL 1 HOUR TO VOLUME 'hot',     -- Горячее хранилище
    session_start + INTERVAL 12 HOUR TO VOLUME 'warm',   -- Теплое хранилище
    session_start + INTERVAL 1 DAY TO VOLUME 'cold';     -- Холодное хранилище
```

## Оптимизация вставки данных

### **Batch** вставка

```sql
-- Вставка больших батчей
INSERT INTO events FORMAT JSONEachRow
{"timestamp": "2024-01-01 12:00:00", "user_id": 1, "event_type": "click"}
{"timestamp": "2024-01-01 12:00:01", "user_id": 2, "event_type": "view"}
-- ... тысячи строк ...

-- Использование буферных таблиц
CREATE TABLE events_buffer (
    timestamp DateTime,
    user_id UInt64,
    event_type String,
    data String
) ENGINE = Buffer(events, 16, 10, 100, 10000, 1000000, 10000000);
-- Буфер: 16 блоков, 10 секунд, 100 строк, 10k строк, 1M байт, 10M байт

-- Вставка в буфер (быстрая)
INSERT INTO events_buffer VALUES (...);

-- Автоматическая flush в основную таблицу
```

### Оптимизация для высоких нагрузок

```xml
<!-- Настройки для высокой частоты вставок -->
<clickhouse>
    <merge_max_block_size>8192</merge_max_block_size>
    <min_compress_block_size>65536</min_compress_block_size>
    <max_compress_block_size>1048576</max_compress_block_size>
</clickhouse>
```

### Асинхронная вставка

```sql
-- Асинхронная вставка с настройками
INSERT INTO events
SETTINGS
    async_insert = 1,                    -- Включить асинхронную вставку
    wait_for_async_insert = 0,           -- Не ждать подтверждения
    async_insert_max_data_size = 100000, -- Максимальный размер батча
    async_insert_poll_timeout_ms = 100   -- Таймаут опроса
VALUES (...);
```

## Мониторинг производительности

### Системные метрики

```sql
-- Общая статистика сервера
SELECT
    uptime() as server_uptime,
    formatReadableSize(total_bytes) as total_memory,
    formatReadableSize(used_bytes) as used_memory,
    formatReadableSize(free_bytes) as free_memory
FROM system.asof;

-- Статистика по дискам
SELECT
    name,
    path,
    formatReadableSize(total_space) as total,
    formatReadableSize(available_space) as available,
    formatReadableSize(used_space) as used
FROM system.disks;

-- Статистика по процессам
SELECT
    query_id,
    user,
    client_hostname,
    client_name,
    query,
    elapsed,
    read_rows,
    read_bytes,
    written_rows,
    written_bytes,
    memory_usage,
    peak_memory_usage
FROM system.processes
ORDER BY elapsed DESC
LIMIT 10;
```

### Метрики запросов

```sql
-- Анализ производительности запросов
SELECT
    query,
    query_duration_ms / 1000 as duration_sec,
    formatReadableSize(read_bytes) as data_read,
    formatReadableSize(result_bytes) as result_size,
    read_rows,
    result_rows,
    memory_usage / 1024 / 1024 as memory_mb
FROM system.query_log
WHERE type = 'QueryFinish'
    AND query_duration_ms > 1000
ORDER BY query_duration_ms DESC
LIMIT 20;

-- Статистика по типам запросов
SELECT
    toHour(event_time) as hour,
    countIf(query LIKE 'SELECT%') as select_queries,
    countIf(query LIKE 'INSERT%') as insert_queries,
    avg(query_duration_ms) as avg_duration_ms,
    quantile(0.95)(query_duration_ms) as p95_duration_ms
FROM system.query_log
WHERE event_time >= today()
GROUP BY hour
ORDER BY hour;
```

### Мониторинг таблиц

```sql
-- Статистика по таблицам
SELECT
    database,
    table,
    engine,
    formatReadableSize(total_bytes) as total_size,
    formatReadableSize(total_rows) as total_rows,
    parts_count,
    lifetime_rows / lifetime_seconds as avg_insert_rate
FROM system.tables
WHERE database = 'default'
ORDER BY total_bytes DESC;

-- Анализ партиций
SELECT
    database,
    table,
    partition,
    count() as parts_count,
    sum(rows) as total_rows,
    formatReadableSize(sum(bytes_on_disk)) as size_on_disk,
    min(modification_time) as oldest_part,
    max(modification_time) as newest_part
FROM system.parts
WHERE active
GROUP BY database, table, partition
ORDER BY total_rows DESC
LIMIT 20;
```

## Диагностика проблем

### Высокая латентность запросов

```sql
-- Поиск медленных запросов
SELECT
    query,
    query_duration_ms,
    read_rows,
    read_bytes,
    result_rows,
    memory_usage
FROM system.query_log
WHERE query_duration_ms > 5000  -- Более 5 секунд
ORDER BY query_duration_ms DESC
LIMIT 10;

-- Анализ использования индексов
EXPLAIN SELECT * FROM large_table WHERE date = '2024-01-01';
-- Проверить использование первичного ключа и партиций
```

### Высокое использование **CPU**

```sql
-- Мониторинг CPU
SELECT
    metric,
    value
FROM system.metrics
WHERE metric LIKE '%CPU%' OR metric LIKE '%Thread%';

-- Анализ запросов с высокой CPU нагрузкой
SELECT
    query,
    query_duration_ms,
    thread_numbers,
    peak_threads_usage
FROM system.query_log
WHERE peak_threads_usage > 8
ORDER BY peak_threads_usage DESC;
```

### Проблемы с памятью

```sql
-- Мониторинг памяти
SELECT
    metric,
    value
FROM system.metrics
WHERE metric LIKE '%Memory%';

-- Запросы с переполнением памяти
SELECT
    query,
    memory_usage / 1024 / 1024 as memory_mb,
    peak_memory_usage / 1024 / 1024 as peak_memory_mb
FROM system.query_log
WHERE memory_usage > 1000000000  -- Более 1GB
ORDER BY memory_usage DESC;
```

### Дисковые проблемы

```sql
-- Мониторинг I/O
SELECT
    name,
    read_bytes,
    write_bytes,
    read_ops,
    write_ops
FROM system.disks;

-- Медленные запросы с высоким I/O
SELECT
    query,
    read_bytes,
    written_bytes,
    query_duration_ms,
    read_bytes / query_duration_ms * 1000 as bytes_per_sec
FROM system.query_log
WHERE read_bytes > 100000000  -- Более 100MB чтения
ORDER BY read_bytes DESC;
```

## Лучшие практики

### Конфигурация сервера

1. **Масштабируйте память**
   ```xml
   <!-- Выделите 80% RAM под ClickHouse -->
   <max_memory_usage>68719476736</max_memory_usage> <!-- 64GB -->
   <max_memory_usage_for_user>54975581388</max_memory_usage_for_user> <!-- 51GB -->
   ```

2. **Оптимизируйте CPU**
   ```xml
   <!-- Используйте все ядра -->
   <max_threads>32</max_threads>
   <background_pool_size>16</background_pool_size>
   ```

3. **Настройте сжатие**
   ```xml
   <!-- LZ4 для скорости, ZSTD для размера -->
   <compression>
       <case><method>lz4</method></case>
       <case><min_part_size>1000000000</min_part_size><method>zstd</method></case>
   </compression>
   ```

### Оптимизация запросов

1. **Используйте PREWHERE**
   ```sql
   SELECT columns
   FROM large_table
   PREWHERE indexed_column = 'value'  -- Фильтрация перед чтением
   WHERE other_conditions;
   ```

2. **Оптимизируйте агрегации**
   ```sql
   -- Двухэтапная агрегация
   SELECT key, sum(total) FROM (
       SELECT key, sum(value) as total
       FROM table
       GROUP BY key, toStartOfHour(timestamp)  -- Предварительная агрегация
   ) GROUP BY key;
   ```

3. **Используйте SAMPLE**
   ```sql
   -- Приближенные расчеты
   SELECT uniq(user_id) FROM events SAMPLE 0.01;  -- 1% данных
   ```

### Мониторинг и обслуживание

1. **Регулярный мониторинг**
   ```sql
   -- Ежечасная проверка
   SELECT
       count() as slow_queries
   FROM system.query_log
   WHERE query_duration_ms > 10000
       AND event_time >= now() - INTERVAL 1 HOUR;
   ```

2. **Обслуживание таблиц**
   ```sql
   -- Еженедельная оптимизация
   OPTIMIZE TABLE large_table;

   -- Очистка старых данных
   ALTER TABLE logs DROP PARTITION '202301';
   ```

3. **Масштабирование**
   ```sql
   -- Добавление реплик для чтения
   -- Добавление шардов для записи
   -- Балансировка нагрузки
   ```

### Производственные настройки

1. **Отказоустойчивость**
   ```xml
   <!-- Репликация и кластеры -->
   <replication>
       <replicated_max_parallel_sends>8</replicated_max_parallel_sends>
       <replicated_max_parallel_fetches>8</replicated_max_parallel_fetches>
   </replication>
   ```

2. **Безопасность**
   ```xml
   <!-- Ограничения ресурсов -->
   <max_query_size>104857600</max_query_size> <!-- 100MB -->
   <max_execution_time>3600</max_execution_time> <!-- 1 час -->
   ```

3. **Логирование**
   ```xml
   <!-- Детальное логирование для отладки -->
   <logger>
       <level>information</level>
       <log>logs/clickhouse-server.log</log>
       <errorlog>logs/clickhouse-server.err.log</errorlog>
   </logger>
   ```

Оптимизация производительности **ClickHouse** — комплексная задача, требующая внимания к аппаратному обеспечению, конфигурации, запросам и мониторингу. Правильная настройка может обеспечить производительность на уровне миллионов строк в секунду.

### Ключевые факторы успеха:

1. **Аппаратное обеспечение**: **NVMe SSD**, достаточная **RAM**, многопроцессорность
2. **Конфигурация**: Оптимизация под нагрузку и ресурсы
3. **Структура данных**: Правильное партиционирование и индексы
4. **Оптимизация запросов**: Использование **PREWHERE**, **LIMIT**, **SAMPLE**
5. **Мониторинг**: Постоянный контроль метрик и производительности

### Основные метрики для мониторинга:

- **Query Performance**: Время выполнения, использование ресурсов
- **System Resources**: **CPU**, память, диск I/O
- **Data Ingestion**: Скорость вставки, размер очередей
- **Cluster Health**: Статус реплик, балансировка нагрузки

### Следующие темы:

- **Интеграции** — подключение внешних систем
- **Экосистема** — инструменты и расширения
- **Расширенные возможности** — специализированные движки

**ClickHouse** — высокопроизводительная система, требующая тщательной настройки для достижения максимальной производительности.

## Полезные ссылки

### Официальная документация
- [Performance Optimization](https://clickhouse.com/docs/en/operations/optimizing-performance)
- [Configuration Parameters](https://clickhouse.com/docs/en/operations/configuration-files)
- [System Tables](https://clickhouse.com/docs/en/operations/system-tables)

### Руководства
- [Hardware Requirements](https://clickhouse.com/docs/en/operations/requirements)
- [Tuning Guide](https://clickhouse.com/docs/en/operations/optimizing-performance)

### Инструменты
- [ClickHouse Benchmark](https://clickhouse.com/docs/en/operations/utilities/clickhouse-benchmark)
- [Performance Monitoring](https://clickhouse.com/docs/en/operations/monitoring)


**Следующие темы:**
- [Интеграции и экосистема](clickhouse-integration.md)

