---
title: "PostgreSQL: Расширения"
description: "Полное руководство по расширениям PostgreSQL: установка, популярные расширения (pg_stat_statements, pg_trgm, PostGIS, pg_cron, TimescaleDB), создание собственных расширений"
tags: ["postgresql", "extensions", "postgis", "timescaledb", "pg_cron", "pg_stat_statements"]
difficulty: "intermediate"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-admin.md"]
next: ["databases/postgres-fulltext-search.md", "databases/postgres-monitoring.md"]
updated: "2026-01-16"
related: ["databases/postgres-performance-tuning.md", "databases/postgres-replication.md"]
---

# PostgreSQL: Расширения

## Введение в расширения PostgreSQL

Расширения (extensions) - это способ добавления дополнительной функциональности в PostgreSQL без изменения ядра системы. Расширения могут добавлять новые типы данных, функции, операторы, индексы и многое другое.

### Преимущества расширений

- **Модульность**: Функциональность изолирована в отдельных модулях
- **Управляемость**: Легко устанавливать и удалять
- **Совместимость**: Работают с различными версиями PostgreSQL
- **Расширяемость**: Можно создавать собственные расширения

---

## Управление расширениями

### Просмотр доступных расширений

```sql
-- Список всех доступных расширений
SELECT * FROM pg_available_extensions ORDER BY name;

-- Список установленных расширений
SELECT * FROM pg_extension;

-- Детальная информация об расширении
\dx
\dx+ extension_name
```

### Установка расширений

```sql
-- Установить расширение
CREATE EXTENSION extension_name;

-- Установить с указанием схемы
CREATE EXTENSION extension_name SCHEMA schema_name;

-- Установить с указанием версии
CREATE EXTENSION extension_name VERSION '1.0';

-- Установить, если не существует
CREATE EXTENSION IF NOT EXISTS extension_name;
```

### Обновление расширений

```sql
-- Обновить до последней версии
ALTER EXTENSION extension_name UPDATE;

-- Обновить до конкретной версии
ALTER EXTENSION extension_name UPDATE TO '2.0';

-- Проверить доступные версии
SELECT * FROM pg_available_extension_versions WHERE name = 'extension_name';
```

### Удаление расширений

```sql
-- Удалить расширение
DROP EXTENSION extension_name;

-- Удалить с зависимостями
DROP EXTENSION extension_name CASCADE;
```

---

## Популярные расширения

### pg_stat_statements

Расширение для отслеживания статистики выполнения SQL запросов.

#### Установка

```sql
CREATE EXTENSION pg_stat_statements;
```

#### Настройка

```conf
# В postgresql.conf
shared_preload_libraries = 'pg_stat_statements'
pg_stat_statements.track = all
pg_stat_statements.max = 10000
```

#### Использование

```sql
-- Топ запросов по времени выполнения
SELECT 
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time
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

### pg_trgm (Trigram)

Расширение для нечеткого поиска и сравнения текста.

#### Установка

```sql
CREATE EXTENSION pg_trgm;
```

#### Использование

```sql
-- Создать GIN индекс для триграмм
CREATE INDEX idx_users_name_trgm ON users USING gin(name gin_trgm_ops);

-- Поиск с похожестью
SELECT name, similarity(name, 'John') AS sim
FROM users
WHERE name % 'John'
ORDER BY sim DESC;

-- Поиск с оператором LIKE (быстрее с индексом)
SELECT name
FROM users
WHERE name LIKE '%John%';
```

### PostGIS

Расширение для работы с географическими данными.

#### Установка

```bash
# Ubuntu/Debian
sudo apt-get install postgis postgresql-14-postgis

# RHEL/CentOS
sudo yum install postgis postgresql14-postgis
```

```sql
CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
```

#### Использование

```sql
-- Создать таблицу с географическими данными
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    location GEOGRAPHY(POINT, 4326)
);

-- Вставить данные
INSERT INTO locations (name, location)
VALUES ('Moscow', ST_GeogFromText('POINT(37.6173 55.7558)'));

-- Найти ближайшие точки
SELECT name, ST_Distance(location, ST_GeogFromText('POINT(37.6173 55.7558)')) AS distance
FROM locations
ORDER BY location <-> ST_GeogFromText('POINT(37.6173 55.7558)')
LIMIT 10;
```

### pg_cron

Расширение для планирования задач (cron jobs) внутри PostgreSQL.

#### Установка

```bash
# Ubuntu/Debian
sudo apt-get install postgresql-14-cron

# RHEL/CentOS
sudo yum install postgresql14-cron
```

```sql
CREATE EXTENSION pg_cron;
```

#### Использование

```sql
-- Запланировать задачу (каждую минуту)
SELECT cron.schedule('update-stats', '* * * * *', 'ANALYZE users;');

-- Запланировать задачу (каждый день в 2:00)
SELECT cron.schedule('daily-backup', '0 2 * * *', 'SELECT pg_dump(...);');

-- Просмотр запланированных задач
SELECT * FROM cron.job;

-- Удалить задачу
SELECT cron.unschedule('update-stats');
```

### TimescaleDB

Расширение для работы с временными рядами.

#### Установка

```bash
# Добавить репозиторий TimescaleDB
# См. официальную документацию
```

```sql
CREATE EXTENSION IF NOT EXISTS timescaledb;
```

#### Использование

```sql
-- Создать таблицу временных рядов
CREATE TABLE metrics (
    time TIMESTAMPTZ NOT NULL,
    device_id INTEGER NOT NULL,
    temperature DOUBLE PRECISION NULL,
    humidity DOUBLE PRECISION NULL
);

-- Преобразовать в hypertable
SELECT create_hypertable('metrics', 'time');

-- Вставить данные
INSERT INTO metrics (time, device_id, temperature, humidity)
VALUES (NOW(), 1, 25.5, 60.0);

-- Запросы с агрегацией
SELECT 
    time_bucket('1 hour', time) AS hour,
    AVG(temperature) AS avg_temp
FROM metrics
WHERE time > NOW() - INTERVAL '24 hours'
GROUP BY hour
ORDER BY hour;
```

### uuid-ossp

Расширение для генерации UUID.

#### Установка

```sql
CREATE EXTENSION "uuid-ossp";
```

#### Использование

```sql
-- Генерировать UUID
SELECT uuid_generate_v4();

-- Использовать в таблице
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100)
);
```

### hstore

Расширение для хранения пар ключ-значение.

#### Установка

```sql
CREATE EXTENSION hstore;
```

#### Использование

```sql
-- Создать таблицу с hstore
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    attributes HSTORE
);

-- Вставить данные
INSERT INTO products (name, attributes)
VALUES ('Laptop', 'color => "black", weight => "2kg", price => "1000"');

-- Запросы
SELECT name, attributes->'color' AS color
FROM products
WHERE attributes ? 'color';

-- Индекс для hstore
CREATE INDEX idx_products_attributes ON products USING gin(attributes);
```

### pgcrypto

Расширение для криптографических функций.

#### Установка

```sql
CREATE EXTENSION pgcrypto;
```

#### Использование

```sql
-- Хеширование паролей
SELECT crypt('password', gen_salt('bf', 10));

-- Проверка пароля
SELECT crypt('password', stored_hash) = stored_hash AS password_match;

-- Шифрование данных
SELECT encrypt('sensitive data', 'key', 'aes');
SELECT decrypt(encrypted_data, 'key', 'aes');
```

### citext

Расширение для case-insensitive текстовых типов.

#### Установка

```sql
CREATE EXTENSION citext;
```

#### Использование

```sql
-- Создать таблицу с citext
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email CITEXT UNIQUE,
    username CITEXT
);

-- Поиск без учета регистра
SELECT * FROM users WHERE email = 'USER@EXAMPLE.COM';
```

---

## Создание собственных расширений

### Структура расширения

```
my_extension/
├── Makefile
├── my_extension.control
├── my_extension--1.0.sql
└── my_extension.c
```

### Создание простого расширения

#### 1. Создать файл my_extension.control

```ini
# my_extension.control
comment = 'My custom extension'
default_version = '1.0'
module_pathname = '$libdir/my_extension'
relocatable = true
```

#### 2. Создать файл my_extension--1.0.sql

```sql
-- my_extension--1.0.sql
CREATE FUNCTION my_function(text)
RETURNS text
AS '$libdir/my_extension', 'my_function'
LANGUAGE C STRICT;

COMMENT ON FUNCTION my_function(text) IS 'My custom function';
```

#### 3. Создать Makefile

```makefile
# Makefile
EXTENSION = my_extension
DATA = my_extension--1.0.sql
MODULES = my_extension

PG_CONFIG = pg_config
PGXS := $(shell $(PG_CONFIG) --pgxs)
include $(PGXS)
```

#### 4. Установить расширение

```bash
make
sudo make install
```

```sql
CREATE EXTENSION my_extension;
```

---

## Best Practices

### Управление расширениями

1. **Документировать используемые расширения**
2. **Проверять совместимость версий**
3. **Тестировать обновления расширений**
4. **Использовать версионирование**

### Производительность

1. **Использовать расширения для оптимизации**
2. **Мониторить влияние расширений на производительность**
3. **Обновлять расширения регулярно**

## Дополнительные популярные расширения

### pg_buffercache

Расширение для просмотра содержимого shared buffer cache.

```sql
-- Установить расширение
CREATE EXTENSION pg_buffercache;

-- Просмотр содержимого буфера
SELECT 
    c.relname,
    COUNT(*) AS buffers,
    pg_size_pretty(COUNT(*) * 8192) AS size
FROM pg_buffercache b
JOIN pg_class c ON b.relfilenode = pg_relation_filenode(c.oid)
WHERE b.reldatabase IN (0, (SELECT oid FROM pg_database WHERE datname = current_database()))
GROUP BY c.relname
ORDER BY buffers DESC
LIMIT 20;
```

### pg_freespacemap

Расширение для просмотра free space map.

```sql
-- Установить расширение
CREATE EXTENSION pg_freespacemap;

-- Просмотр свободного места
SELECT 
    c.relname,
    pg_size_pretty(pg_relation_size(c.oid)) AS relation_size,
    pg_size_pretty(SUM(fsm.avail)) AS free_space
FROM pg_freespacemap('public.users') fsm
JOIN pg_class c ON c.relname = 'users'
GROUP BY c.relname, c.oid;
```

### pg_prewarm

Расширение для предварительной загрузки данных в кэш.

```sql
-- Установить расширение
CREATE EXTENSION pg_prewarm;

-- Предзагрузить таблицу в кэш
SELECT pg_prewarm('users');

-- Предзагрузить с опциями
SELECT pg_prewarm('users', 'read', 'buffer');

-- Предзагрузить индексы
SELECT pg_prewarm('users', 'prefetch', 'main');
```

### pgstattuple

Расширение для анализа статистики кортежей.

```sql
-- Установить расширение
CREATE EXTENSION pgstattuple;

-- Статистика таблицы
SELECT * FROM pgstattuple('users');

-- Статистика индекса
SELECT * FROM pgstattuple('idx_users_email');
```

### pgrowlocks

Расширение для просмотра блокировок строк.

```sql
-- Установить расширение
CREATE EXTENSION pgrowlocks;

-- Просмотр блокировок
SELECT * FROM pgrowlocks('users');
```

### pg_trgm (расширенное использование)

```sql
-- Расширенное использование триграмм
CREATE EXTENSION pg_trgm;

-- Поиск с ранжированием
SELECT 
    name,
    similarity(name, 'John Doe') AS sim,
    word_similarity('John Doe', name) AS word_sim
FROM users
WHERE name % 'John Doe'
ORDER BY sim DESC;

-- Поиск с порогом похожести
SELECT name
FROM users
WHERE similarity(name, 'John') > 0.3
ORDER BY similarity(name, 'John') DESC;

-- Индекс для полнотекстового поиска
CREATE INDEX idx_users_name_trgm ON users USING gin(name gin_trgm_ops);
CREATE INDEX idx_users_name_trgm_gist ON users USING gist(name gist_trgm_ops);
```

### PostGIS (расширенное использование)

```sql
-- Расширенное использование PostGIS
CREATE EXTENSION postgis;
CREATE EXTENSION postgis_topology;
CREATE EXTENSION postgis_raster;

-- Работа с полигонами
CREATE TABLE regions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    boundary GEOMETRY(POLYGON, 4326)
);

-- Вставить полигон
INSERT INTO regions (name, boundary)
VALUES ('Moscow Region', 
    ST_GeomFromText('POLYGON((37.0 55.0, 38.0 55.0, 38.0 56.0, 37.0 56.0, 37.0 55.0))', 4326)
);

-- Проверить пересечение
SELECT r.name
FROM regions r
WHERE ST_Intersects(r.boundary, ST_GeomFromText('POINT(37.5 55.5)', 4326));

-- Работа с линиями
CREATE TABLE roads (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    path GEOMETRY(LINESTRING, 4326)
);

-- Вычислить длину дороги
SELECT name, ST_Length(path::geography) AS length_meters
FROM roads;

-- Работа с растрами
CREATE TABLE elevation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    raster RASTER
);
```

### pg_cron (расширенное использование)

```sql
-- Расширенное использование pg_cron
CREATE EXTENSION pg_cron;

-- Запланировать задачу с параметрами
SELECT cron.schedule(
    'update-stats',
    '*/5 * * * *',
    $$SELECT update_statistics();$$
);

-- Запланировать задачу с логированием
SELECT cron.schedule(
    'daily-report',
    '0 9 * * 1-5',
    $$INSERT INTO reports SELECT generate_daily_report();$$
);

-- Просмотр истории выполнения
SELECT * FROM cron.job_run_details
ORDER BY start_time DESC
LIMIT 20;

-- Управление задачами
-- Приостановить задачу
UPDATE cron.job SET active = false WHERE jobname = 'update-stats';

-- Возобновить задачу
UPDATE cron.job SET active = true WHERE jobname = 'update-stats';

-- Изменить расписание
SELECT cron.alter_job(
    job_id := (SELECT jobid FROM cron.job WHERE jobname = 'update-stats'),
    schedule := '*/10 * * * *'
);
```

### TimescaleDB (расширенное использование)

```sql
-- Расширенное использование TimescaleDB
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Создать hypertable с партиционированием
CREATE TABLE metrics (
    time TIMESTAMPTZ NOT NULL,
    device_id INTEGER NOT NULL,
    sensor_id INTEGER NOT NULL,
    value DOUBLE PRECISION
);

SELECT create_hypertable('metrics', 'time', 
    chunk_time_interval => INTERVAL '1 day',
    partitioning_column => 'device_id',
    number_partitions => 4
);

-- Continuous aggregates
CREATE MATERIALIZED VIEW metrics_hourly
WITH (timescaledb.continuous) AS
SELECT 
    time_bucket('1 hour', time) AS hour,
    device_id,
    AVG(value) AS avg_value,
    MAX(value) AS max_value,
    MIN(value) AS min_value
FROM metrics
GROUP BY hour, device_id;

-- Автоматическое обновление continuous aggregate
SELECT add_continuous_aggregate_policy('metrics_hourly',
    start_offset => INTERVAL '3 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour'
);

-- Retention policy
SELECT add_retention_policy('metrics', INTERVAL '90 days');

-- Compression
ALTER TABLE metrics SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'device_id',
    timescaledb.compress_orderby = 'time DESC'
);

SELECT add_compression_policy('metrics', INTERVAL '7 days');
```

### pg_partman

Расширение для автоматического партиционирования.

```sql
-- Установить расширение
CREATE EXTENSION pg_partman;

-- Создать партиционированную таблицу
CREATE TABLE events (
    id BIGSERIAL,
    event_time TIMESTAMPTZ NOT NULL,
    data JSONB
) PARTITION BY RANGE (event_time);

-- Настроить автоматическое создание партиций
SELECT partman.create_parent(
    p_parent_table => 'public.events',
    p_control => 'event_time',
    p_type => 'range',
    p_interval => 'daily',
    p_premake => 7
);

-- Настроить автоматическое удаление старых партиций
UPDATE partman.part_config
SET retention = '30 days',
    retention_keep_table = false
WHERE parent_table = 'public.events';
```

### pg_repack

Расширение для переупаковки таблиц без блокировок.

```bash
# Установить расширение
sudo apt-get install postgresql-14-repack

# Использование
pg_repack -d mydb -t users
pg_repack -d mydb --table users --only-indexes
```

### pg_audit

Расширение для аудита SQL операций.

```sql
-- Установить расширение
CREATE EXTENSION pgaudit;

-- Настроить аудит
ALTER SYSTEM SET pgaudit.log = 'all';
ALTER SYSTEM SET pgaudit.log_catalog = off;
ALTER SYSTEM SET pgaudit.log_parameter = on;
ALTER SYSTEM SET pgaudit.log_statement_once = off;
ALTER SYSTEM SET pgaudit.log_level = 'log';
SELECT pg_reload_conf();

-- Аудит конкретных таблиц
ALTER TABLE users SET (pgaudit.log = 'all');
```

### pg_similarity

Расширение для вычисления схожести строк.

```sql
-- Установить расширение
CREATE EXTENSION pg_similarity;

-- Jaccard similarity
SELECT jaccard('hello', 'hallo');

-- Levenshtein distance
SELECT levenshtein('hello', 'hallo');

-- Cosine similarity
SELECT cosine('hello world', 'hallo welt');
```

## Advanced Extension Development

### Создание расширения с типами данных

```sql
-- my_extension--1.0.sql
-- Создать базовый тип
CREATE TYPE my_type AS (
    id INTEGER,
    name TEXT
);

-- Создать функции для типа
CREATE FUNCTION my_type_in(cstring)
RETURNS my_type
AS '$libdir/my_extension', 'my_type_in'
LANGUAGE C IMMUTABLE STRICT;

CREATE FUNCTION my_type_out(my_type)
RETURNS cstring
AS '$libdir/my_extension', 'my_type_out'
LANGUAGE C IMMUTABLE STRICT;

-- Зарегистрировать тип
CREATE TYPE my_type (
    INPUT = my_type_in,
    OUTPUT = my_type_out,
    INTERNALLENGTH = VARIABLE
);
```

### Создание расширения с операторами

```sql
-- my_extension--1.0.sql
-- Создать оператор
CREATE OPERATOR = (
    LEFTARG = my_type,
    RIGHTARG = my_type,
    PROCEDURE = my_type_eq,
    COMMUTATOR = =,
    NEGATOR = <>
);

-- Создать операторный класс для индексов
CREATE OPERATOR CLASS my_type_ops
DEFAULT FOR TYPE my_type USING btree AS
    OPERATOR 1 <,
    OPERATOR 2 <=,
    OPERATOR 3 =,
    OPERATOR 4 >=,
    OPERATOR 5 >,
    FUNCTION 1 my_type_cmp(my_type, my_type);
```

### Создание расширения с функциями на C

```c
// my_extension.c
#include "postgres.h"
#include "fmgr.h"
#include "utils/builtins.h"

PG_MODULE_MAGIC;

PG_FUNCTION_INFO_V1(my_function);
Datum
my_function(PG_FUNCTION_ARGS)
{
    text *arg = PG_GETARG_TEXT_PP(0);
    char *result;
    int len;
    
    len = VARSIZE_ANY_EXHDR(arg);
    result = (char *) palloc(len + 1);
    memcpy(result, VARDATA_ANY(arg), len);
    result[len] = '\0';
    
    // Обработка данных
    // ...
    
    PG_RETURN_TEXT_P(cstring_to_text(result));
}
```

### Создание расширения с индексами

```sql
-- my_extension--1.0.sql
-- Создать метод доступа
CREATE FUNCTION my_am_handler(internal)
RETURNS index_am_handler
AS '$libdir/my_extension', 'my_am_handler'
LANGUAGE C;

-- Создать метод доступа
CREATE ACCESS METHOD my_am
TYPE INDEX
HANDLER my_am_handler;

-- Создать операторный класс
CREATE OPERATOR CLASS my_ops
FOR TYPE text USING my_am AS
    OPERATOR 1 <,
    OPERATOR 2 <=,
    OPERATOR 3 =,
    OPERATOR 4 >=,
    OPERATOR 5 >,
    FUNCTION 1 my_cmp(text, text);
```

## Extension Management

### Автоматическая установка расширений

```sql
-- Функция для автоматической установки расширений
CREATE OR REPLACE FUNCTION install_required_extensions()
RETURNS VOID AS $$
DECLARE
    ext_name TEXT;
    ext_list TEXT[] := ARRAY[
        'pg_stat_statements',
        'pg_trgm',
        'uuid-ossp',
        'hstore'
    ];
BEGIN
    FOREACH ext_name IN ARRAY ext_list
    LOOP
        BEGIN
            EXECUTE format('CREATE EXTENSION IF NOT EXISTS %I', ext_name);
            RAISE NOTICE 'Installed extension: %', ext_name;
        EXCEPTION
            WHEN OTHERS THEN
                RAISE WARNING 'Failed to install extension %: %', ext_name, SQLERRM;
        END;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Вызвать функцию
SELECT install_required_extensions();
```

### Мониторинг расширений

```sql
-- Просмотр всех установленных расширений
SELECT 
    e.extname AS extension_name,
    e.extversion AS version,
    n.nspname AS schema,
    COUNT(DISTINCT c.oid) AS objects_count
FROM pg_extension e
JOIN pg_namespace n ON e.extnamespace = n.oid
LEFT JOIN pg_depend d ON d.refobjid = e.oid
LEFT JOIN pg_class c ON d.objid = c.oid AND c.relkind IN ('r', 'v', 'm', 'S', 'f')
GROUP BY e.extname, e.extversion, n.nspname
ORDER BY e.extname;

-- Проверить доступные обновления
SELECT 
    e.extname,
    e.extversion AS current_version,
    a.version AS available_version
FROM pg_extension e
JOIN pg_available_extension_versions a ON e.extname = a.name
WHERE a.version > e.extversion::text
ORDER BY e.extname;
```

### Управление версиями расширений

```sql
-- Функция для обновления расширений
CREATE OR REPLACE FUNCTION update_extensions()
RETURNS TABLE(extension_name TEXT, old_version TEXT, new_version TEXT) AS $$
DECLARE
    ext_rec RECORD;
    new_ver TEXT;
BEGIN
    FOR ext_rec IN
        SELECT extname, extversion
        FROM pg_extension
    LOOP
        -- Получить последнюю версию
        SELECT version INTO new_ver
        FROM pg_available_extension_versions
        WHERE name = ext_rec.extname
        ORDER BY version DESC
        LIMIT 1;
        
        IF new_ver > ext_rec.extversion THEN
            -- Обновить расширение
            EXECUTE format('ALTER EXTENSION %I UPDATE TO %L', 
                ext_rec.extname, new_ver);
            
            RETURN QUERY SELECT 
                ext_rec.extname::TEXT,
                ext_rec.extversion::TEXT,
                new_ver;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;
```

## Best Practices Summary

### Установка расширений

1. **Проверять совместимость** с версией PostgreSQL
2. **Тестировать в dev окружении** перед production
3. **Документировать** используемые расширения
4. **Использовать версионирование** для отслеживания

### Управление расширениями

1. **Регулярно обновлять** расширения
2. **Мониторить производительность** после обновлений
3. **Создавать бэкапы** перед обновлениями
4. **Тестировать откат** обновлений

### Разработка расширений

1. **Следовать стандартам** PostgreSQL
2. **Документировать** код и функции
3. **Тестировать** на разных версиях PostgreSQL
4. **Использовать версионирование** для миграций

## Real-World Use Cases

### Use Case 1: Аналитика с pg_stat_statements

```sql
-- Создать представление для анализа запросов
CREATE VIEW slow_queries AS
SELECT 
    query,
    calls,
    total_exec_time,
    mean_exec_time,
    max_exec_time,
    stddev_exec_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS hit_percent
FROM pg_stat_statements
WHERE mean_exec_time > 100  -- Запросы медленнее 100ms
ORDER BY mean_exec_time DESC;

-- Найти проблемные запросы
SELECT * FROM slow_queries LIMIT 20;
```

### Use Case 2: Поиск с pg_trgm

```sql
-- Создать функцию для нечеткого поиска
CREATE OR REPLACE FUNCTION fuzzy_search(search_term TEXT, threshold REAL DEFAULT 0.3)
RETURNS TABLE(id INTEGER, name TEXT, similarity REAL) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        u.name,
        similarity(u.name, search_term) AS sim
    FROM users u
    WHERE u.name % search_term
    AND similarity(u.name, search_term) > threshold
    ORDER BY sim DESC;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM fuzzy_search('John Doe', 0.3);
```

### Use Case 3: Геолокация с PostGIS

```sql
-- Найти ближайшие точки
CREATE OR REPLACE FUNCTION find_nearby_locations(
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    radius_meters INTEGER DEFAULT 1000
)
RETURNS TABLE(id INTEGER, name TEXT, distance_meters DOUBLE PRECISION) AS $$
DECLARE
    point GEOGRAPHY;
BEGIN
    point := ST_GeogFromText(format('POINT(%s %s)', lon, lat), 4326);
    
    RETURN QUERY
    SELECT 
        l.id,
        l.name,
        ST_Distance(l.location, point) AS distance
    FROM locations l
    WHERE ST_DWithin(l.location, point, radius_meters)
    ORDER BY l.location <-> point
    LIMIT 10;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM find_nearby_locations(55.7558, 37.6173, 5000);
```

### Use Case 4: Автоматизация с pg_cron

```sql
-- Настроить автоматическое обслуживание
SELECT cron.schedule('vacuum-analyze', '0 3 * * *', 
    $$VACUUM ANALYZE users;$$);

SELECT cron.schedule('update-stats', '*/30 * * * *',
    $$SELECT update_statistics();$$);

SELECT cron.schedule('cleanup-old-data', '0 2 * * 0',
    $$DELETE FROM events WHERE created_at < NOW() - INTERVAL '90 days';$$);
```

### Use Case 5: Временные ряды с TimescaleDB

```sql
-- Создать систему мониторинга метрик
CREATE TABLE sensor_metrics (
    time TIMESTAMPTZ NOT NULL,
    sensor_id INTEGER NOT NULL,
    temperature DOUBLE PRECISION,
    humidity DOUBLE PRECISION,
    pressure DOUBLE PRECISION
);

SELECT create_hypertable('sensor_metrics', 'time');

-- Continuous aggregate для дашборда
CREATE MATERIALIZED VIEW sensor_metrics_hourly
WITH (timescaledb.continuous) AS
SELECT 
    time_bucket('1 hour', time) AS hour,
    sensor_id,
    AVG(temperature) AS avg_temp,
    AVG(humidity) AS avg_humidity,
    AVG(pressure) AS avg_pressure
FROM sensor_metrics
GROUP BY hour, sensor_id;

-- Автоматическое обновление
SELECT add_continuous_aggregate_policy('sensor_metrics_hourly',
    start_offset => INTERVAL '3 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour');
```

## Extension Troubleshooting

### Проблема: Расширение не устанавливается

```sql
-- Проверить доступность расширения
SELECT * FROM pg_available_extensions WHERE name = 'extension_name';

-- Проверить права доступа
SELECT has_database_privilege(current_user, current_database(), 'CREATE');

-- Проверить версию PostgreSQL
SELECT version();

-- Проверить установленные пакеты
-- Ubuntu/Debian
dpkg -l | grep postgresql

-- RHEL/CentOS
rpm -qa | grep postgresql
```

### Проблема: Расширение работает медленно

```sql
-- Проверить настройки расширения
SELECT * FROM pg_settings WHERE name LIKE '%extension%';

-- Проверить использование ресурсов
SELECT 
    pid,
    usename,
    application_name,
    state,
    query_start,
    query
FROM pg_stat_activity
WHERE query LIKE '%extension_function%';

-- Оптимизировать настройки
ALTER SYSTEM SET extension_setting = 'value';
SELECT pg_reload_conf();
```

### Проблема: Конфликты версий

```sql
-- Проверить версии расширений
SELECT 
    extname,
    extversion,
    (SELECT version FROM pg_available_extension_versions 
     WHERE name = extname ORDER BY version DESC LIMIT 1) AS latest_version
FROM pg_extension;

-- Обновить расширение
ALTER EXTENSION extension_name UPDATE;

-- Откатить расширение
ALTER EXTENSION extension_name UPDATE TO 'previous_version';
```

## Extension Performance Tuning

### Оптимизация pg_stat_statements

```conf
# postgresql.conf
shared_preload_libraries = 'pg_stat_statements'
pg_stat_statements.max = 10000
pg_stat_statements.track = all
pg_stat_statements.track_utility = on
pg_stat_statements.save = on
```

### Оптимизация pg_trgm

```sql
-- Выбрать правильный тип индекса
-- GIN для полнотекстового поиска
CREATE INDEX idx_name_gin ON users USING gin(name gin_trgm_ops);

-- GiST для поиска по диапазонам
CREATE INDEX idx_name_gist ON users USING gist(name gist_trgm_ops);

-- Настроить параметры
SET pg_trgm.similarity_threshold = 0.3;
SET pg_trgm.word_similarity_threshold = 0.4;
```

### Оптимизация PostGIS

```sql
-- Использовать правильные типы данных
-- GEOGRAPHY для больших расстояний
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    location GEOGRAPHY(POINT, 4326)
);

-- GEOMETRY для локальных операций
CREATE TABLE regions (
    id SERIAL PRIMARY KEY,
    boundary GEOMETRY(POLYGON, 4326)
);

-- Создать пространственные индексы
CREATE INDEX idx_locations_location ON locations USING gist(location);
CREATE INDEX idx_regions_boundary ON regions USING gist(boundary);
```

## Extension Security

### Безопасная установка расширений

```sql
-- Ограничить установку расширений
REVOKE CREATE ON DATABASE mydb FROM PUBLIC;
GRANT CREATE ON DATABASE mydb TO admin_role;

-- Ограничить доступ к функциям расширений
REVOKE EXECUTE ON FUNCTION extension_function() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION extension_function() TO app_role;
```

### Аудит использования расширений

```sql
-- Создать таблицу аудита
CREATE TABLE extension_audit (
    id SERIAL PRIMARY KEY,
    extension_name TEXT,
    action TEXT,
    user_name TEXT,
    timestamp TIMESTAMPTZ DEFAULT NOW()
);

-- Функция для аудита
CREATE OR REPLACE FUNCTION audit_extension_usage()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO extension_audit (extension_name, action, user_name)
    VALUES (TG_TABLE_NAME, TG_OP, current_user);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

## Extension Migration Strategies

### Стратегия 1: Постепенная миграция

```sql
-- Создать новую версию расширения
CREATE EXTENSION my_extension VERSION '2.0' FROM '1.0';

-- Тестировать новую версию
-- ...

-- Обновить все базы данных
ALTER EXTENSION my_extension UPDATE TO '2.0';
```

### Стратегия 2: Откат изменений

```sql
-- Сохранить текущую версию
SELECT extversion FROM pg_extension WHERE extname = 'my_extension';

-- Обновить расширение
ALTER EXTENSION my_extension UPDATE;

-- При необходимости откатить
ALTER EXTENSION my_extension UPDATE TO '1.0';
```

## Extension Development Best Practices

### Структура проекта

```
my_extension/
├── Makefile
├── my_extension.control
├── sql/
│   ├── my_extension--1.0.sql
│   ├── my_extension--1.0--1.1.sql
│   └── my_extension--1.1--2.0.sql
├── src/
│   ├── my_extension.c
│   └── my_functions.c
├── test/
│   └── sql/
│       └── test.sql
└── README.md
```

### Тестирование расширений

```sql
-- Создать тестовую базу данных
CREATE DATABASE test_extension;

-- Установить расширение
\c test_extension
CREATE EXTENSION my_extension;

-- Запустить тесты
\i test/sql/test.sql

-- Проверить результаты
SELECT * FROM test_results;
```

### Документирование расширений

```sql
-- Добавить комментарии
COMMENT ON EXTENSION my_extension IS 'My custom extension for PostgreSQL';

COMMENT ON FUNCTION my_function(TEXT) IS 
    'My function does something useful';

COMMENT ON TYPE my_type IS 
    'My custom type for storing data';
```

## Extension Packaging

### Создание пакета расширения

```bash
# Структура пакета
my_extension-1.0/
├── DEBIAN/
│   ├── control
│   ├── postinst
│   └── prerm
├── usr/
│   └── share/
│       └── postgresql/
│           └── extension/
│               ├── my_extension.control
│               └── my_extension--1.0.sql
└── usr/
    └── lib/
        └── postgresql/
            └── my_extension.so

# Создать пакет
dpkg-deb --build my_extension-1.0
```

### Распространение расширения

```bash
# Через GitHub
git clone https://github.com/user/my_extension.git
cd my_extension
make
sudo make install

# Через pgxn
pgxn install my_extension

# Через apt/yum
sudo apt-get install postgresql-14-my-extension
```

## Monitoring Extensions

### Мониторинг использования

```sql
-- Создать представление для мониторинга
CREATE VIEW extension_usage AS
SELECT 
    e.extname,
    COUNT(DISTINCT d.objid) AS objects_count,
    pg_size_pretty(SUM(pg_total_relation_size(c.oid))) AS total_size
FROM pg_extension e
JOIN pg_depend d ON d.refobjid = e.oid
LEFT JOIN pg_class c ON d.objid = c.oid AND c.relkind = 'r'
GROUP BY e.extname;

-- Использовать представление
SELECT * FROM extension_usage;
```

### Алерты для расширений

```sql
-- Функция для проверки расширений
CREATE OR REPLACE FUNCTION check_extensions()
RETURNS TABLE(extension_name TEXT, status TEXT, message TEXT) AS $$
DECLARE
    ext_rec RECORD;
BEGIN
    FOR ext_rec IN
        SELECT extname, extversion
        FROM pg_extension
    LOOP
        -- Проверить доступность обновлений
        IF EXISTS (
            SELECT 1 FROM pg_available_extension_versions
            WHERE name = ext_rec.extname
            AND version > ext_rec.extversion
        ) THEN
            RETURN QUERY SELECT 
                ext_rec.extname::TEXT,
                'UPDATE_AVAILABLE'::TEXT,
                format('Update available: %s -> %s', 
                    ext_rec.extversion,
                    (SELECT version FROM pg_available_extension_versions
                     WHERE name = ext_rec.extname ORDER BY version DESC LIMIT 1)
                )::TEXT;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;
```

## Extension Maintenance

### Регулярное обслуживание

```sql
-- Функция для обслуживания расширений
CREATE OR REPLACE FUNCTION maintain_extensions()
RETURNS VOID AS $$
DECLARE
    ext_rec RECORD;
BEGIN
    FOR ext_rec IN
        SELECT extname
        FROM pg_extension
    LOOP
        -- Обновить статистику для объектов расширения
        EXECUTE format('ANALYZE %I', ext_rec.extname);
        
        -- Логировать
        RAISE NOTICE 'Maintained extension: %', ext_rec.extname;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Запланировать через pg_cron
SELECT cron.schedule('maintain-extensions', '0 4 * * 0', 
    'SELECT maintain_extensions();');
```

### Очистка неиспользуемых расширений

```sql
-- Найти неиспользуемые расширения
SELECT 
    e.extname,
    COUNT(DISTINCT d.objid) AS objects_count
FROM pg_extension e
LEFT JOIN pg_depend d ON d.refobjid = e.oid
GROUP BY e.extname
HAVING COUNT(DISTINCT d.objid) = 0;

-- Удалить неиспользуемые расширения
DROP EXTENSION IF EXISTS unused_extension;
```

## Extension Comparison

### Сравнение расширений для похожих задач

#### UUID генерация

```sql
-- uuid-ossp (встроенное)
CREATE EXTENSION "uuid-ossp";
SELECT uuid_generate_v4();

-- pgcrypto (альтернатива)
CREATE EXTENSION pgcrypto;
SELECT gen_random_uuid();
```

#### Полнотекстовый поиск

```sql
-- pg_trgm (триграммы)
CREATE EXTENSION pg_trgm;
CREATE INDEX idx_name_trgm ON users USING gin(name gin_trgm_ops);

-- tsvector (встроенный)
CREATE INDEX idx_name_fts ON users USING gin(to_tsvector('english', name));
```

## Extension Dependencies

### Управление зависимостями

```sql
-- Проверить зависимости расширения
SELECT 
    e.extname AS extension,
    d.objid::regclass AS dependent_object,
    d.deptype AS dependency_type
FROM pg_extension e
JOIN pg_depend d ON d.refobjid = e.oid
WHERE e.extname = 'my_extension';

-- Найти расширения, зависящие от другого
SELECT 
    e.extname AS extension,
    d.refobjid::regclass AS depends_on
FROM pg_extension e
JOIN pg_depend d ON d.objid = e.oid
WHERE d.refobjid::regclass::text LIKE '%other_extension%';
```

## Extension Versioning

### Семантическое версионирование

```sql
-- Создать миграцию для обновления
-- my_extension--1.0--1.1.sql
ALTER TABLE my_table ADD COLUMN new_column TEXT;

-- my_extension--1.1--2.0.sql
CREATE TABLE new_table (...);
ALTER TABLE my_table DROP COLUMN old_column;
```

### Откат версий

```sql
-- Создать скрипт отката
-- my_extension--2.0--1.1.sql
ALTER TABLE my_table ADD COLUMN old_column TEXT;
DROP TABLE new_table;
```

## Extension Testing

### Unit тесты

```sql
-- test/sql/test.sql
BEGIN;

-- Тест функции
SELECT my_function('test') = 'expected_result';

-- Тест типа
SELECT (1, 'test')::my_type;

-- Тест оператора
SELECT my_type 'value1' = my_type 'value2';

ROLLBACK;
```

### Integration тесты

```sql
-- test/sql/integration_test.sql
BEGIN;

-- Установить расширение
CREATE EXTENSION my_extension;

-- Тест интеграции с другими расширениями
SELECT my_function('test') || ' ' || uuid_generate_v4();

ROLLBACK;
```

## Extension Performance Benchmarks

### Бенчмарк pg_trgm

```sql
-- Тест производительности поиска
\timing on

-- Без индекса
SELECT * FROM users WHERE name LIKE '%John%';

-- С GIN индексом
CREATE INDEX idx_name_gin ON users USING gin(name gin_trgm_ops);
SELECT * FROM users WHERE name % 'John';

-- С GiST индексом
CREATE INDEX idx_name_gist ON users USING gist(name gist_trgm_ops);
SELECT * FROM users WHERE name % 'John';

\timing off
```

### Бенчмарк PostGIS

```sql
-- Тест производительности геопространственных запросов
\timing on

-- Без индекса
SELECT * FROM locations 
WHERE ST_DWithin(location, ST_GeogFromText('POINT(37.6173 55.7558)', 4326), 1000);

-- С индексом
CREATE INDEX idx_locations_location ON locations USING gist(location);
SELECT * FROM locations 
WHERE ST_DWithin(location, ST_GeogFromText('POINT(37.6173 55.7558)', 4326), 1000);

\timing off
```

## Extension Security Best Practices

### Минимизация привилегий

```sql
-- Создать роль для расширения
CREATE ROLE extension_user;

-- Установить расширение от имени суперпользователя
CREATE EXTENSION my_extension;

-- Передать управление роли
ALTER EXTENSION my_extension OWNER TO extension_user;

-- Ограничить доступ
REVOKE ALL ON EXTENSION my_extension FROM PUBLIC;
GRANT USAGE ON EXTENSION my_extension TO app_role;
```

### Аудит расширений

```sql
-- Включить аудит для расширений
ALTER SYSTEM SET pgaudit.log = 'ddl,role';
ALTER SYSTEM SET pgaudit.log_extension = on;
SELECT pg_reload_conf();
```

## Extension Documentation

### Автоматическая документация

```sql
-- Создать функцию для генерации документации
CREATE OR REPLACE FUNCTION generate_extension_docs(ext_name TEXT)
RETURNS TABLE(
    object_type TEXT,
    object_name TEXT,
    description TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        'function'::TEXT,
        p.proname::TEXT,
        obj_description(p.oid, 'pg_proc')::TEXT
    FROM pg_proc p
    JOIN pg_namespace n ON p.pronamespace = n.oid
    JOIN pg_depend d ON d.objid = p.oid
    JOIN pg_extension e ON d.refobjid = e.oid
    WHERE e.extname = ext_name
    UNION ALL
    SELECT 
        'type'::TEXT,
        t.typname::TEXT,
        obj_description(t.oid, 'pg_type')::TEXT
    FROM pg_type t
    JOIN pg_namespace n ON t.typnamespace = n.oid
    JOIN pg_depend d ON d.objid = t.oid
    JOIN pg_extension e ON d.refobjid = e.oid
    WHERE e.extname = ext_name;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM generate_extension_docs('my_extension');
```

## Extension Deployment

### Автоматическое развертывание

```bash
#!/bin/bash
# deploy_extension.sh

EXTENSION_NAME="my_extension"
EXTENSION_VERSION="1.0"
DB_NAME="mydb"

# Установить расширение
psql -d $DB_NAME -c "CREATE EXTENSION IF NOT EXISTS $EXTENSION_NAME VERSION '$EXTENSION_VERSION';"

# Проверить установку
psql -d $DB_NAME -c "SELECT * FROM pg_extension WHERE extname = '$EXTENSION_NAME';"
```

### CI/CD для расширений

```yaml
# .github/workflows/test_extension.yml
name: Test Extension

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup PostgreSQL
        run: |
          sudo apt-get update
          sudo apt-get install postgresql-14
      - name: Build Extension
        run: make
      - name: Install Extension
        run: sudo make install
      - name: Run Tests
        run: |
          createdb test_db
          psql -d test_db -c "CREATE EXTENSION my_extension;"
          psql -d test_db -f test/sql/test.sql
```

---

## Полезные ссылки

- [PostgreSQL Extensions](https://www.postgresql.org/docs/current/extend-extensions.html)
- [PostGIS Documentation](https://postgis.net/documentation/)
- [TimescaleDB Documentation](https://docs.timescale.com/)
- [pg_cron Documentation](https://github.com/citusdata/pg_cron)

---

**Дата последнего обновления:** 2026-01-16

## Содержание

- [Введение в расширения PostgreSQL](#�-ведение-в-�-а�-�-и�-ения-postgresql)
  - [Преимущества расширений](#�-�-еим�-�-е�-�-ва-�-а�-�-и�-ений)
- [Управление расширениями](#�-п�-авление-�-а�-�-и�-ениями)
  - [Просмотр доступных расширений](#�-�-о�-мо�-�-до�-�-�-пн�-�-�-а�-�-и�-ений)



