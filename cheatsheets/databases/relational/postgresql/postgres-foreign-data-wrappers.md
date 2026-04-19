---
title: "PostgreSQL: Foreign Data Wrappers"
description: "Полное руководство по Foreign Data Wrappers в PostgreSQL: подключение внешних БД, postgres_fdw, file_fdw, создание собственных FDW"
tags:
  - postgresql
  - fdw
  - foreign-data-wrapper
  - postgres_fdw
  - file_fdw
  - external-data
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-admin.md"]
next: ["databases/postgres-extensions.md", "databases/postgres-replication.md"]
updated: "2026-02-06"
related: ["databases/postgres-queries.md", "databases/postgres-performance-tuning.md"]
---

# **PostgreSQL**: **Foreign Data Wrappers**

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL postgres_fdw](https://www.postgresql.org/docs/current/postgres-fdw.html) — расширение postgres_fdw

### См. также
- [postgres-basics.md](postgres-basics.md) — основы PostgreSQL
- [postgres-queries.md](postgres-queries.md) — запросы

## Содержание

- [Введение в **Foreign Data Wrappers**](#введение-в-foreign-data-wrappers)
  - [Преимущества **FDW**](#преимущества-fdw)
- [Установка и настройка **FDW**](#установка-и-настройка-fdw)
  - [Установка расширения](#установка-расширения)
  - [Базовые концепции](#базовые-концепции)
- [**postgres_fdw**](#postgresfdw)
  - [Настройка подключения](#настройка-подключения)
  - [Использование **Foreign Table**](#использование-foreign-table)
  - [Импорт схемы](#импорт-схемы)
  - [Оптимизация производительности](#оптимизация-производительности)
- [**file_fdw**](#filefdw)
  - [Настройка **file_fdw**](#настройка-filefdw)
  - [Поддерживаемые форматы](#поддерживаемые-форматы)
- [Другие популярные **FDW**](#другие-популярные-fdw)
  - [**mysql_fdw**](#mysqlfdw)
  - [**oracle_fdw**](#oraclefdw)
  - [**mongo_fdw**](#mongofdw)
- [Создание собственного **FDW**](#создание-собственного-fdw)
  - [Структура **FDW**](#структура-fdw)
  - [Реализация основных функций](#реализация-основных-функций)
  - [Создание расширения](#создание-расширения)
- [Лучшие практики](#лучшие-практики)
  - [Производительность](#производительность)
  - [Безопасность](#безопасность)
- [Решение проблем](#решение-проблем)
  - [Проблема: Медленные запросы к **foreign tables**](#проблема-медленные-запросы-к-foreign-tables)
  - [Проблема: Ошибки подключения](#проблема-ошибки-подключения)
- [**Advanced postgres_fdw**](#advanced-postgresfdw)
  - [Транзакции и изоляция](#транзакции-и-изоляция)
  - [**Bulk Operations**](#bulk-operations)
  - [Параллельные запросы](#параллельные-запросы)
  - [Оптимизация **JOIN**](#оптимизация-join)
  - [Мониторинг производительности](#мониторинг-производительности)
- [**Advanced file_fdw**](#advanced-filefdw)
  - [Работа с **JSON** файлами](#работа-с-json-файлами)
  - [Работа с **XML** файлами](#работа-с-xml-файлами)
  - [Работа с несколькими файлами](#работа-с-несколькими-файлами)
  - [Динамическое создание **foreign tables**](#динамическое-создание-foreign-tables)
- [Дополнительные **FDW**](#дополнительные-fdw)
  - [**redis_fdw**](#redisfdw)
  - [**clickhouse_fdw**](#clickhousefdw)
  - [**multicorn_fdw**](#multicornfdw)
- [Advanced Use Cases](#advanced-use-cases)
  - [Use Case 1: Multi-Database Reporting](#use-case-1-multi-database-reporting)
  - [Use Case 2: Data Migration](#use-case-2-data-migration)
  - [Use Case 3: Data Archiving](#use-case-3-data-archiving)
- [Performance Benchmarks](#performance-benchmarks)
  - [Benchmark 1: Simple Select](#benchmark-1-simple-select)
  - [Benchmark 2: Join Performance](#benchmark-2-join-performance)
  - [Benchmark 3: Aggregation](#benchmark-3-aggregation)
- [Configuration Management](#configuration-management)
  - [Управление конфигурацией через переменные](#управление-конфигурацией-через-переменные)
  - [Управление через конфигурационные файлы](#управление-через-конфигурационные-файлы)
- [Error Handling](#error-handling)
  - [Обработка ошибок подключения](#обработка-ошибок-подключения)
  - [Retry Logic](#retry-logic)
- [Documentation и Maintenance](#documentation-и-maintenance)
  - [Автоматическая документация](#автоматическая-документация)
  - [Автоматическое обслуживание](#автоматическое-обслуживание)
- [Migration Strategies](#migration-strategies)
  - [Стратегия 1: Big Bang Migration](#стратегия-1-big-bang-migration)
  - [Стратегия 2: Incremental Migration](#стратегия-2-incremental-migration)
  - [Стратегия 3: Dual Write](#стратегия-3-dual-write)
- [Чек-лист лучших практик](#чек-лист-лучших-практик)
  - [Перед использованием FDW](#перед-использованием-fdw)
  - [При использовании FDW](#при-использовании-fdw)
  - [При обслуживании FDW](#при-обслуживании-fdw)

## Введение в **Foreign Data Wrappers**

**Foreign Data Wrappers** (**FDW**) - это механизм **PostgreSQL** для доступа к данным, хранящимся во внешних источниках, как если бы они были обычными таблицами **PostgreSQL**.

### Преимущества **FDW**

- **Единый интерфейс**: Доступ к различным источникам данных через **SQL**
- **Прозрачность**: Внешние данные выглядят как обычные таблицы
- **Гибкость**: Поддержка различных источников данных
- **Расширяемость**: Возможность создания собственных **FDW**

---

## Установка и настройка **FDW**

### Установка расширения

**Установка расширений **postgres_fdw** и **file_fdw**:**

```sql
-- Установить расширение для FDW
CREATE EXTENSION postgres_fdw;
CREATE EXTENSION file_fdw;
```

### Базовые концепции

1. **Foreign Server**: Определение внешнего сервера
2. **User Mapping**: Сопоставление пользователей
3. **Foreign Table**: Таблица, представляющая внешние данные

---

## **postgres_fdw**

**postgres_fdw** позволяет подключаться к другим серверам **PostgreSQL**.

### Настройка подключения

#### 1. Создать **Foreign Server**

```sql
-- Создать foreign server
CREATE SERVER foreign_server
FOREIGN DATA WRAPPER postgres_fdw
OPTIONS (
    host 'remote_host',
    port '5432',
    dbname 'remote_db'
);
```

#### 2. Создать **User Mapping**

```sql
-- Создать user mapping
CREATE USER MAPPING FOR current_user
SERVER foreign_server
OPTIONS (
    user 'remote_user',
    password 'remote_password'
);
```

#### 3. Создать **Foreign Table**

```sql
-- Создать foreign table
CREATE FOREIGN TABLE foreign_users (
    id INTEGER,
    name VARCHAR(100),
    email VARCHAR(100)
)
SERVER foreign_server
OPTIONS (
    schema_name 'public',
    table_name 'users'
);
```

### Использование **Foreign Table**

```sql
-- Запросы к foreign table
SELECT * FROM foreign_users WHERE id = 1;

-- JOIN с локальными таблицами
SELECT 
    l.id AS local_id,
    f.name AS foreign_name
FROM local_table l
JOIN foreign_users f ON l.id = f.id;
```

### Импорт схемы

```sql
-- Импортировать все таблицы из схемы
IMPORT FOREIGN SCHEMA public
FROM SERVER foreign_server
INTO public;

-- Импортировать конкретные таблицы
IMPORT FOREIGN SCHEMA public
LIMIT TO (users, orders, products)
FROM SERVER foreign_server
INTO public;
```

### Оптимизация производительности

```sql
-- Включить pushdown для WHERE условий
ALTER FOREIGN TABLE foreign_users
OPTIONS (ADD use_remote_estimate 'true');

-- Настроить batch size
ALTER FOREIGN TABLE foreign_users
OPTIONS (ADD fetch_size '100');
```

---

## **file_fdw**

**file_fdw** позволяет читать данные из файлов как из таблиц.

### Настройка **file_fdw**

#### 1. Создать **Foreign Server**

```sql
-- Создать foreign server для файлов
CREATE SERVER file_server
FOREIGN DATA WRAPPER file_fdw;
```

#### 2. Создать **Foreign Table** для **CSV**

```sql
-- Создать foreign table для CSV файла
CREATE FOREIGN TABLE csv_data (
    id INTEGER,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at DATE
)
SERVER file_server
OPTIONS (
    filename '/path/to/data.csv',
    format 'csv',
    header 'true'
);
```

#### 3. Использование

```sql
-- Читать данные из CSV
SELECT * FROM csv_data WHERE id > 100;

-- Импортировать в локальную таблицу
INSERT INTO local_users (id, name, email)
SELECT id, name, email FROM csv_data;
```

### Поддерживаемые форматы

```sql
-- CSV формат
CREATE FOREIGN TABLE csv_data (...)
SERVER file_server
OPTIONS (format 'csv', header 'true');

-- Text формат
CREATE FOREIGN TABLE text_data (...)
SERVER file_server
OPTIONS (format 'text', delimiter E'\t');
```

---

## Другие популярные **FDW**

### **mysql_fdw**

Подключение к **MySQL** базам данных.

```sql
-- Установить расширение
CREATE EXTENSION mysql_fdw;

-- Создать server
CREATE SERVER mysql_server
FOREIGN DATA WRAPPER mysql_fdw
OPTIONS (
    host 'mysql_host',
    port '3306',
    dbname 'mysql_db'
);

-- Создать user mapping
CREATE USER MAPPING FOR current_user
SERVER mysql_server
OPTIONS (
    username 'mysql_user',
    password 'mysql_password'
);

-- Создать foreign table
CREATE FOREIGN TABLE mysql_users (
    id INTEGER,
    name VARCHAR(100)
)
SERVER mysql_server
OPTIONS (
    dbname 'mysql_db',
    table_name 'users'
);
```

### **oracle_fdw**

Подключение к **Oracle** базам данных.

```sql
-- Установить расширение
CREATE EXTENSION oracle_fdw;

-- Создать server
CREATE SERVER oracle_server
FOREIGN DATA WRAPPER oracle_fdw
OPTIONS (
    dbserver '//oracle_host:1521/orcl'
);

-- Создать user mapping
CREATE USER MAPPING FOR current_user
SERVER oracle_server
OPTIONS (
    user 'oracle_user',
    password 'oracle_password'
);
```

### **mongo_fdw**

Подключение к **MongoDB**.

```sql
-- Установить расширение
CREATE EXTENSION mongo_fdw;

-- Создать server
CREATE SERVER mongo_server
FOREIGN DATA WRAPPER mongo_fdw
OPTIONS (
    address 'mongodb://mongo_host:27017',
    database 'mongo_db'
);

-- Создать foreign table
CREATE FOREIGN TABLE mongo_users (
    _id TEXT,
    name TEXT,
    email TEXT
)
SERVER mongo_server
OPTIONS (
    database 'mongo_db',
    collection 'users'
);
```

---

## Создание собственного **FDW**

### Структура **FDW**

```c
// my_fdw.c
#include "postgres.h"
#include "foreign/fdwapi.h"

PG_MODULE_MAGIC;

// Функции FDW
PG_FUNCTION_INFO_V1(my_fdw_handler);
Datum my_fdw_handler(PG_FUNCTION_ARGS);

PG_FUNCTION_INFO_V1(my_fdw_validator);
Datum my_fdw_validator(PG_FUNCTION_ARGS);
```

### Реализация основных функций

```c
// Обработчик FDW
Datum my_fdw_handler(PG_FUNCTION_ARGS)
{
    FdwRoutine *fdwroutine = makeNode(FdwRoutine);
    
    fdwroutine->GetForeignRelSize = my_get_foreign_rel_size;
    fdwroutine->GetForeignPaths = my_get_foreign_paths;
    fdwroutine->GetForeignPlan = my_get_foreign_plan;
    fdwroutine->BeginForeignScan = my_begin_foreign_scan;
    fdwroutine->IterateForeignScan = my_iterate_foreign_scan;
    fdwroutine->ReScanForeignScan = my_rescan_foreign_scan;
    fdwroutine->EndForeignScan = my_end_foreign_scan;
    
    PG_RETURN_POINTER(fdwroutine);
}

// Валидатор опций
Datum my_fdw_validator(PG_FUNCTION_ARGS)
{
    List *options_list = untransformRelOptions(PG_GETARG_DATUM(0));
    // Валидация опций
    PG_RETURN_VOID();
}
```

### Создание расширения

```sql
-- my_fdw.control
comment = 'My custom FDW'
default_version = '1.0'
module_pathname = '$libdir/my_fdw'
relocatable = true

-- my_fdw--1.0.sql
CREATE FUNCTION my_fdw_handler()
RETURNS fdw_handler
AS '$libdir/my_fdw', 'my_fdw_handler'
LANGUAGE C STRICT;

CREATE FUNCTION my_fdw_validator(options array, catalog oid)
RETURNS void
AS '$libdir/my_fdw', 'my_fdw_validator'
LANGUAGE C STRICT;

CREATE FOREIGN DATA WRAPPER my_fdw
HANDLER my_fdw_handler
VALIDATOR my_fdw_validator;
```

---

## Лучшие практики

### Производительность

1. **Используйте pushdown** для фильтрации на удаленном сервере
2. **Настройте fetch_size** для оптимизации передачи данных
3. **Кэшируйте результаты** для часто используемых запросов
4. **Мониторьте производительность** запросов к **foreign tables**

### Безопасность

1. **Ограничивайте доступ** к **foreign servers**
2. **Используйте user mappings** для управления доступом
3. **Шифруйте соединения** с внешними серверами
4. **Валидируйте опции** в **custom FDW**

---

## Решение проблем

### Проблема: Медленные запросы к **foreign tables**

**Решение:**
```sql
-- Включить pushdown
ALTER FOREIGN TABLE foreign_users
OPTIONS (ADD use_remote_estimate 'true');

-- Увеличить fetch_size
ALTER FOREIGN TABLE foreign_users
OPTIONS (SET fetch_size '1000');
```

### Проблема: Ошибки подключения

**Решение:**
```sql
-- Проверить настройки server
SELECT * FROM pg_foreign_server WHERE srvname = 'foreign_server';

-- Проверить user mapping
SELECT * FROM pg_user_mappings WHERE srvname = 'foreign_server';

-- Протестировать подключение
SELECT * FROM foreign_users LIMIT 1;
```

## **Advanced postgres_fdw**

### Транзакции и изоляция

```sql
-- Настроить уровень изоляции для foreign server
ALTER SERVER foreign_server
OPTIONS (ADD transaction_isolation 'read committed');

-- Использовать транзакции
BEGIN;
INSERT INTO foreign_users (id, name, email) VALUES (1, 'John', 'john@example.com');
UPDATE foreign_users SET name = 'Jane' WHERE id = 1;
COMMIT;
```

### **Bulk Operations**

```sql
-- Массовая вставка
INSERT INTO foreign_users (id, name, email)
SELECT id, name, email FROM local_users
WHERE created_at > '2026-01-01';

-- Массовое обновление
UPDATE foreign_users
SET status = 'active'
WHERE id IN (SELECT id FROM local_users WHERE status = 'active');
```

### Параллельные запросы

```sql
-- Включить параллельные запросы
ALTER FOREIGN TABLE foreign_users
OPTIONS (ADD use_remote_estimate 'true');

-- Настроить количество параллельных воркеров
SET max_parallel_workers_per_gather = 4;

-- Параллельный запрос
SELECT * FROM foreign_users WHERE id > 1000;
```

### Оптимизация **JOIN**

```sql
-- JOIN с pushdown
EXPLAIN (VERBOSE, BUFFERS)
SELECT 
    l.id,
    l.name,
    f.email
FROM local_table l
JOIN foreign_users f ON l.id = f.id
WHERE l.status = 'active';

-- Использовать индексы на foreign server
CREATE INDEX idx_foreign_users_id ON foreign_users(id);
```

### Мониторинг производительности

```sql
-- Проверить статистику foreign tables
SELECT 
    schemaname,
    tablename,
    n_tup_ins AS inserts,
    n_tup_upd AS updates,
    n_tup_del AS deletes,
    n_live_tup AS live_tuples,
    n_dead_tup AS dead_tuples
FROM pg_stat_user_tables
WHERE schemaname = 'public'
AND tablename LIKE 'foreign_%';

-- Проверить размер foreign tables
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
AND tablename LIKE 'foreign_%';
```

## **Advanced file_fdw**

### Работа с **JSON** файлами

```sql
-- Создать foreign table для JSON
CREATE FOREIGN TABLE json_data (
    data JSONB
)
SERVER file_server
OPTIONS (
    filename '/path/to/data.json',
    format 'text'
);

-- Парсинг JSON
SELECT 
    data->>'id' AS id,
    data->>'name' AS name,
    data->>'email' AS email
FROM json_data
WHERE (data->>'status')::boolean = true;
```

### Работа с **XML** файлами

```sql
-- Создать foreign table для XML
CREATE FOREIGN TABLE xml_data (
    id INTEGER,
    name VARCHAR(100),
    email VARCHAR(100)
)
SERVER file_server
OPTIONS (
    filename '/path/to/data.xml',
    format 'text'
);

-- Использовать XML функции
SELECT 
    id,
    name,
    email,
    xpath('/user/@status', xml_content) AS status
FROM xml_data;
```

### Работа с несколькими файлами

```sql
-- Создать функцию для чтения нескольких файлов
CREATE OR REPLACE FUNCTION read_multiple_files()
RETURNS TABLE(id INTEGER, name VARCHAR, email VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT * FROM file_data_2026_01
    UNION ALL
    SELECT * FROM file_data_2026_02
    UNION ALL
    SELECT * FROM file_data_2026_03;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM read_multiple_files() WHERE id > 1000;
```

### Динамическое создание **foreign tables**

```sql
-- Функция для создания foreign table из файла
CREATE OR REPLACE FUNCTION create_file_fdw_table(
    table_name TEXT,
    file_path TEXT,
    format_type TEXT DEFAULT 'csv'
)
RETURNS VOID AS $$
DECLARE
    sql_text TEXT;
BEGIN
    sql_text := format('
        CREATE FOREIGN TABLE %I (
            id INTEGER,
            name VARCHAR(100),
            email VARCHAR(100)
        )
        SERVER file_server
        OPTIONS (
            filename %L,
            format %L,
            header ''true''
        )',
        table_name, file_path, format_type
    );
    
    EXECUTE sql_text;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT create_file_fdw_table('new_data', '/path/to/new_data.csv', 'csv');
```

## Дополнительные **FDW**

### **redis_fdw**

```sql
-- Установить расширение
CREATE EXTENSION redis_fdw;

-- Создать server
CREATE SERVER redis_server
FOREIGN DATA WRAPPER redis_fdw
OPTIONS (
    address '127.0.0.1',
    port '6379'
);

-- Создать foreign table
CREATE FOREIGN TABLE redis_data (
    key TEXT,
    value TEXT
)
SERVER redis_server
OPTIONS (
    database '0'
);

-- Использовать
SELECT * FROM redis_data WHERE key = 'user:1';
```

### **clickhouse_fdw**

```sql
-- Установить расширение
CREATE EXTENSION clickhouse_fdw;

-- Создать server
CREATE SERVER clickhouse_server
FOREIGN DATA WRAPPER clickhouse_fdw
OPTIONS (
    host 'clickhouse_host',
    port '9000',
    database 'default'
);

-- Создать foreign table
CREATE FOREIGN TABLE clickhouse_data (
    id INTEGER,
    timestamp TIMESTAMP,
    value DOUBLE PRECISION
)
SERVER clickhouse_server
OPTIONS (
    table 'metrics'
);

-- Использовать
SELECT 
    date_trunc('hour', timestamp) AS hour,
    AVG(value) AS avg_value
FROM clickhouse_data
WHERE timestamp > NOW() - INTERVAL '24 hours'
GROUP BY hour;
```

### **multicorn_fdw**

**Multicorn** - это **FDW framework** для создания **FDW** на **Python**.

```java
// PostgreSQL Python example replaced with Java Spring — использование FDW из Java приложения
import `java.sql`.*;

public class FDWExample {
    public static void main(`String`[] args) {
        `String url` = "jdbc:postgresql://localhost/mydb";
        `String user` = "postgres";
        `String password` = "password";
        
        try (`Connection conn` = `DriverManager`.`getConnection`(url, user, password)) {
            // Создать foreign server
            try (`Statement stmt` = conn.`createStatement`()) {
                `stmt.execute`("""
                    `CREATE SERVER IF NOT EXISTS java_server`
                    `FOREIGN DATA WRAPPER postgres_fdw`
                    `OPTIONS` (
                        host '`remote_host`',
                        port '5432',
                        dbname '`remote_db`'
                    )
                """);
            }
            
            // Использовать foreign table
            try (`PreparedStatement` pstmt = conn.`prepareStatement`(
                    "`SELECT` * `FROM java_data WHERE` id = ?")) {
                pstmt.`setInt`(1, 1);
                try (`ResultSet` rs = pstmt.`executeQuery`()) {
                    while (`rs.next`()) {
                        `System`.`out.println`(rs.`getString`("name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.`printStackTrace`();
        }
    }
}
```

## Advanced Use Cases

### Use Case 1: Multi-Database Reporting

```sql
-- Создать представление для отчетности из нескольких БД
`CREATE VIEW cross_database_report AS`
`SELECT` 
    'orders' `AS` source,
    `COUNT`(*) `AS record_count`,
    `SUM`(amount) `AS total_amount`
`FROM foreign_orders`
`WHERE created_at` > `NOW`() - `INTERVAL` '1 month'
`UNION ALL`
`SELECT` 
    'users' `AS` source,
    `COUNT`(*) `AS record_count`,
    `NULL AS total_amount`
`FROM foreign_users`
`WHERE created_at` > `NOW`() - `INTERVAL` '1 month';
```

### Use Case 2: Data Migration

```sql
-- Поэтапная миграция данных
`DO` $$
`DECLARE`
    `batch_size INTEGER` := `10000`;
    `offset_val INTEGER` := 0;
    `row_count INTEGER`;
`BEGIN`
    `LOOP`
        -- Копировать батч данных
        `INSERT INTO new_schema`.users (id, name, email)
        `SELECT` id, name, email
        `FROM old_schema`.users
        `ORDER BY` id
        `LIMIT batch_size OFFSET offset_val`;
        
        `GET DIAGNOSTICS row_count` = ROW_COUNT;
        `EXIT WHEN row_count` = 0;
        
        -- Логировать прогресс
        `RAISE NOTICE` '`Migrated` % rows', `offset_val` + `row_count`;
        
        `offset_val` := `offset_val` + `batch_size`;
        
        -- Небольшая пауза для снижения нагрузки
        `PERFORM pg_sleep`(`0.1`);
    `END LOOP`;
`END` $$;
```

### Use Case 3: Data Archiving

```sql
-- Архивировать старые данные
`CREATE FOREIGN TABLE archive_data` (
    id `INTEGER`,
    data `JSONB`,
    `created_at TIMESTAMP`
)
`SERVER archive_server`
`OPTIONS` (
    `schema_name` 'archive',
    `table_name` '`old_data`'
);

-- Переместить старые данные в архив
`INSERT INTO archive_data`
`SELECT` id, data, `created_at`
`FROM current_data`
`WHERE created_at` < `NOW`() - `INTERVAL` '1 year';

-- Удалить из текущей таблицы
`DELETE FROM current_data`
`WHERE created_at` < `NOW`() - `INTERVAL` '1 year';
```

## Performance Benchmarks

### Benchmark 1: Simple Select

```sql
-- Тест простого `SELECT`
\timing on
`SELECT` * `FROM foreign_users WHERE` id = 1;
\timing off
```

### Benchmark 2: Join Performance

```sql
-- Тест производительности `JOIN`
\timing on
`SELECT` 
    `l.id`,
    `l.name`,
    `f.email`
`FROM local_table` l
`JOIN foreign_users` f `ON l.id` = `f.id`
`WHERE l.status` = 'active';
\timing off
```

### Benchmark 3: Aggregation

```sql
-- Тест агрегации
\timing on
`SELECT` 
    `DATE`(`created_at`) `AS` date,
    `COUNT`(*) `AS` count,
    `AVG`(amount) `AS avg_amount`
`FROM foreign_orders`
`WHERE created_at` > `NOW`() - `INTERVAL` '1 month'
`GROUP BY DATE`(`created_at`)
`ORDER BY` date;
\timing off
```

## Configuration Management

### Управление конфигурацией через переменные

```sql
-- Использовать переменные для конфигурации
`SET` app.`foreign_host` = '`remote_host`';
`SET` app.`foreign_port` = '5432';
`SET` app.`foreign_db` = '`remote_db`';

-- Создать функцию для создания foreign server
`CREATE OR REPLACE FUNCTION create_foreign_server_dynamic`(`server_name TEXT`)
`RETURNS VOID AS` $$
`DECLARE`
    `host_val TEXT` := `current_setting`('app.`foreign_host`');
    `port_val TEXT` := `current_setting`('app.`foreign_port`');
    `db_val TEXT` := `current_setting`('app.`foreign_db`');
    `sql_text TEXT`;
`BEGIN`
    `sql_text` := format('
        `CREATE SERVER` %I
        `FOREIGN DATA WRAPPER postgres_fdw`
        `OPTIONS` (
            host %L,
            port %L,
            dbname %L
        )',
        `server_name`, `host_val`, `port_val`, `db_val`
    );
    
    `EXECUTE sql_text`;
`END`;
$$ `LANGUAGE` plpgsql;
```

### Управление через конфигурационные файлы

```sql
-- Загрузить конфигурацию из файла
`CREATE OR REPLACE FUNCTION load_fdw_config`(`config_file TEXT`)
`RETURNS TABLE`(`server_name TEXT`, host `TEXT`, port `INTEGER`, dbname `TEXT`) `AS` $$
`DECLARE`
    `config_line TEXT`;
`BEGIN`
    -- Читать конфигурацию из файла
    -- Парсить и возвращать настройки
    `RETURN QUERY`
    `SELECT` 
        'server1'::`TEXT`,
        'host1'::`TEXT`,
        `5432::INTEGER`,
        'db1'::`TEXT`;
`END`;
$$ `LANGUAGE` plpgsql;
```

## Error Handling

### Обработка ошибок подключения

```sql
-- Функция для безопасного подключения
`CREATE OR REPLACE FUNCTION safe_foreign_query`(
    `server_name TEXT`,
    `query_text TEXT`
)
`RETURNS TABLE`(result `TEXT`) `AS` $$
`DECLARE`
    `result_text TEXT`;
`BEGIN`
    `BEGIN`
        -- Выполнить запрос
        `EXECUTE` format('`SELECT` * `FROM` %I', `server_name`) `INTO result_text`;
        `RETURN QUERY SELECT result_text`;
    `EXCEPTION`
        `WHEN OTHERS THEN`
            -- Логировать ошибку
            `RAISE WARNING` '`Error querying foreign server` %: %', 
                `server_name`, `SQLERRM`;
            `RETURN`;
    `END`;
`END`;
$$ `LANGUAGE` plpgsql;
```

### Retry Logic

```sql
-- Функция с повторными попытками
`CREATE OR REPLACE FUNCTION retry_foreign_query`(
    `server_name TEXT`,
    `query_text TEXT`,
    `max_retries INTEGER DEFAULT 3`
)
`RETURNS TABLE`(result `TEXT`) `AS` $$
`DECLARE`
    attempt `INTEGER` := 0;
    `result_text TEXT`;
`BEGIN`
    `LOOP`
        `BEGIN`
            `EXECUTE` format('`SELECT` * `FROM` %I', `server_name`) `INTO result_text`;
            `RETURN QUERY SELECT result_text`;
            `EXIT`;
        `EXCEPTION`
            `WHEN OTHERS THEN`
                attempt := attempt + 1;
                `IF` attempt >= `max_retries THEN`
                    `RAISE`;
                `END IF`;
                `PERFORM pg_sleep`(1); -- Пауза перед повтором
        `END`;
    `END LOOP`;
`END`;
$$ `LANGUAGE` plpgsql;
```

## Documentation и Maintenance

### Автоматическая документация

```sql
-- Создать функцию для генерации документации
`CREATE OR REPLACE FUNCTION generate_fdw_docs`()
`RETURNS TABLE`(
    `server_name TEXT`,
    `server_type TEXT`,
    `tables_count INTEGER`,
    `last_used TIMESTAMP`
) `AS` $$
`BEGIN`
    `RETURN QUERY`
    `SELECT` 
        `s.srvname::TEXT`,
        `fdw.fdwname::TEXT`,
        `COUNT`(`ft.ftrelid`)::`INTEGER`,
        `MAX`(`pg_stat_get_last_activity`(`ft.ftrelid`))::`TIMESTAMP`
    `FROM pg_foreign_server` s
    `JOIN pg_foreign_data_wrapper` fdw `ON s.srvfdw` = `fdw.oid`
    `LEFT JOIN pg_foreign_table` ft `ON ft.ftserver` = `s.oid`
    `GROUP BY s.srvname`, `fdw.fdwname`
    `ORDER BY s.srvname`;
`END`;
$$ `LANGUAGE` plpgsql;
```

### Автоматическое обслуживание

```sql
-- Функция для обслуживания foreign tables
`CREATE OR REPLACE FUNCTION maintain_foreign_tables`()
`RETURNS VOID AS` $$
`DECLARE`
    `table_rec RECORD`;
`BEGIN`
    `FOR table_rec IN`
        `SELECT` 
            `n.nspname AS` schema,
            `c.relname AS` table
        `FROM pg_foreign_table` ft
        `JOIN pg_class` c `ON ft.ftrelid` = `c.oid`
        `JOIN pg_namespace` n `ON c.relnamespace` = `n.oid`
    `LOOP`
        -- Обновить статистику
        `EXECUTE` format('`ANALYZE` %I.%I', `table_rec`.schema, `table_rec`.table);
        
        -- Логировать
        `RAISE NOTICE` '`Maintained foreign table`: %.%', 
            `table_rec`.schema, `table_rec`.table;
    `END LOOP`;
`END`;
$$ `LANGUAGE` plpgsql;

-- Запланировать через `pg_cron`
`SELECT cron.schedule`('`maintain-fdw`', '0 2 * * *', 
    '`SELECT maintain_foreign_tables`();');
```

## Migration Strategies

### Стратегия 1: Big Bang Migration

```sql
-- Полная миграция всех данных сразу
`BEGIN`;
-- Создать foreign tables
`IMPORT FOREIGN SCHEMA` public `FROM SERVER old_db INTO old_schema`;
-- Копировать все данные
`INSERT INTO new_schema`.users `SELECT` * `FROM old_schema`.users;
`COMMIT`;
```

### Стратегия 2: Incremental Migration

```sql
-- Поэтапная миграция
`DO` $$
`DECLARE`
    `last_id INTEGER` := 0;
    `batch_size INTEGER` := `10000`;
`BEGIN`
    `LOOP`
        `INSERT INTO new_schema`.users
        `SELECT` * `FROM old_schema`.users
        `WHERE` id > `last_id`
        `ORDER BY` id
        `LIMIT batch_size`;
        
        `EXIT WHEN NOT FOUND`;
        
        `SELECT MAX`(id) `INTO last_id FROM new_schema`.users;
        
        `PERFORM pg_sleep`(1); -- Пауза между батчами
    `END LOOP`;
`END` $$;
```

### Стратегия 3: Dual Write

```sql
-- Запись в обе БД одновременно
`CREATE OR REPLACE FUNCTION dual_write_users`(
    `p_id INTEGER`,
    `p_name VARCHAR`,
    `p_email VARCHAR`
)
`RETURNS VOID AS` $$
`BEGIN`
    -- Записать в новую БД
    `INSERT INTO new_schema`.users (id, name, email)
    `VALUES` (`p_id`, `p_name`, `p_email`);
    
    -- Записать в старую БД через `FDW`
    `INSERT INTO old_schema`.users (id, name, email)
    `VALUES` (`p_id`, `p_name`, `p_email`);
`END`;
$$ `LANGUAGE` plpgsql;
```

## Чек-лист лучших практик

### Перед использованием FDW

- [ ] Оценить производительность внешнего источника
- [ ] Проверить сетевую связность
- [ ] Настроить безопасность соединений
- [ ] Создать необходимые индексы на удаленном сервере
- [ ] Настроить мониторинг

### При использовании FDW

- [ ] Использовать pushdown для оптимизации
- [ ] Настроить fetch_size для больших таблиц
- [ ] Кэшировать часто используемые данные
- [ ] Мониторить производительность запросов
- [ ] Регулярно обновлять статистику

### При обслуживании FDW

- [ ] Регулярно проверять состояние foreign servers
- [ ] Обновлять статистику foreign tables
- [ ] Проверять логи на ошибки
- [ ] Тестировать производительность
- [ ] Документировать изменения

---

- [PostgreSQL Foreign Data Wrappers](https://www.postgresql.org/docs/current/fdwhandler.html)
- [postgres_fdw Documentation](https://www.postgresql.org/docs/current/postgres-fdw.html)
- [file_fdw Documentation](https://www.postgresql.org/docs/current/file-fdw.html)

---


