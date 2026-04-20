---
title: "PostgreSQL: Полное руководство по основам и мониторингу"
description: "Комплексное руководство по PostgreSQL: установка, настройка, базовые операции, индексы, мониторинг производительности, оптимизация запросов"
tags:
  - postgresql
  - database
  - sql
  - rdbms
  - performance
  - monitoring
  - indexes
  - optimization
difficulty: "intermediate"
prerequisites: ["databases/postgres-design.md", "databases/postgres-types.md"]
next: ["databases/postgres-transactions.md", "databases/postgres-indexes.md", "databases/postgres-partitioning.md"]
updated: "2026-02-11"
related: ["databases/postgres-data-ops.md", "databases/postgres-joins.md", "databases/postgres-queries.md"]
---

# PostgreSQL: Полное руководство по основам и мониторингу

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/current/tutorial.html) — введение

### См. также
- [[postgres-design|postgres-design.md]] — проектирование БД
- [[postgres-queries|postgres-queries.md]] — запросы

## Содержание

- [Введение в **PostgreSQL**](#введение-в-postgresql)
  - [Основные возможности **PostgreSQL**](#основные-возможности-postgresql)
  - [Архитектура **PostgreSQL**](#архитектура-postgresql)
- [Установка и первоначальная настройка](#установка-и-первоначальная-настройка)
  - [Установка **PostgreSQL**](#установка-postgresql)
  - [Первоначальная настройка](#первоначальная-настройка)
  - [Подключение к **PostgreSQL**](#подключение-к-postgresql)
- [Лучшие практики](#лучшие-практики)

## Введение в PostgreSQL

**PostgreSQL** — это мощная, открытая объектно-реляционная система управления базами данных (ОРСУБД), которая использует и расширяет язык **SQL**. **PostgreSQL** известен своей надежностью, расширяемостью и соответствием стандартам **SQL**.

### Основные возможности PostgreSQL

- **Полная `ACID` совместимость**: Гарантии атомарности, согласованности, изолированности и долговечности
- **Расширяемость**: Поддержка пользовательских типов данных, функций, операторов
- **Мощный SQL**: Полная поддержка **SQL**:2003 и многих современных расширений
- **JSON и JSONB**: Нативная поддержка работы с **JSON** данными
- **Полнотекстовый поиск**: Встроенные возможности полнотекстового поиска
- **Географические данные**: Поддержка **PostGIS** для географических данных
- **Многовариантная конкуренция**: **MVCC** для высокой **concurrency**
- **Наследование таблиц**: Объектно-ориентированные возможности
- **Асинхронная репликация**: Поддержка различных типов репликации

### Архитектура PostgreSQL

Схема компонентов **PostgreSQL**: пул соединений, парсер, оптимизатор, исполнитель, слой хранения (буферы, `WAL`, блокировки, vacuum), ОС.

```text
# Архитектура: Connection Pool → Parser → Optimizer → Executor → Storage
┌─────────────────────────────────────────────────────────────┐
│                    PostgreSQL Server                        │
├─────────────────────────────────────────────────────────────┤
│  Connection Pool  │  Parser  │  Optimizer  │  Executor     │
├─────────────────────────────────────────────────────────────┤
│                 Storage Engine                              │
├─────────────────────────────────────────────────────────────┤
│  Buffer Manager  │  WAL  │  Lock Manager  │  Vacuum        │
├─────────────────────────────────────────────────────────────┤
│                    Operating System                         │
└─────────────────────────────────────────────────────────────┘
```

## Установка и первоначальная настройка

### Установка PostgreSQL

#### Linux (Ubuntu/Debian)

**Установка **PostgreSQL** на **Ubuntu**/**Debian**:**

```bash
# Обновление пакетов
sudo apt update

# Установка PostgreSQL
sudo apt install postgresql postgresql-contrib

# Проверка статуса
sudo systemctl status postgresql

# Запуск сервиса
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### macOS (с Homebrew)

```bash
# Установка Homebrew (если не установлен)
# /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Установка PostgreSQL
brew install postgresql

# Запуск сервиса
brew services start postgresql

# Инициализация базы данных (если необходимо)
initdb /usr/local/var/postgres
```

#### Docker

```bash
# Запуск PostgreSQL в Docker
docker run --name postgres \
  -e POSTGRES_PASSWORD=mypassword \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_DB=mydb \
  -p 5432:5432 \
  -d postgres:15

# Или с volume для персистентности
docker run --name postgres \
  -e POSTGRES_PASSWORD=mypassword \
  -v postgres_data:/var/lib/postgresql/data \
  -p 5432:5432 \
  -d postgres:15
```

### Первоначальная настройка

#### Создание пользователя и базы данных

```bash
# Переключение на пользователя postgres
sudo -u postgres psql

# В psql:
CREATE USER myuser WITH PASSWORD 'mypassword';
CREATE DATABASE mydb OWNER myuser;
GRANT ALL PRIVILEGES ON DATABASE mydb TO myuser;
\q
```

#### Настройка postgresql.conf

**Файл postgresql.conf** является основным конфигурационным файлом **PostgreSQL**. Он содержит сотни параметров, которые влияют на производительность, безопасность и функциональность сервера. Параметры логически сгруппированы по областям применения.

**Расположение файла:**
- **Linux**: например `/etc/postgresql/<ver>/main/postgresql.conf` или `$PGDATA/postgresql.conf`
- **macOS**: например `/usr/local/var/postgres/postgresql.conf` или см. документацию
- **Docker**: обычно `/var/lib/postgresql/data/postgresql.conf`

**Основные параметры производительности:**

**Сетевые настройки:**
```ini
# Интерфейсы для прослушивания подключений
# '*' - все интерфейсы, localhost - только локальные
listen_addresses = 'localhost,192.168.1.100'

# Порт PostgreSQL (стандартный 5432)
port = 5432

# Максимальное количество одновременных подключений
# Увеличивайте осторожно - каждое соединение использует ресурсы
max_connections = 100

# Таймауты для сетевых операций
tcp_keepalives_idle = 60      # Начало отправки keepalive пакетов
tcp_keepalives_interval = 10  # Интервал между keepalive пакетами
tcp_keepalives_count = 3      # Количество неудачных keepalive
```

**Настройки памяти (критично для производительности):**
```ini
# Shared buffers - основная кэш-память PostgreSQL
# Рекомендация: 25% от RAM для систем до 8GB RAM
# Для больших систем: 8GB-16GB в зависимости от нагрузки
shared_buffers = 256MB

# Effective cache size - оценка размера кэша ОС + shared_buffers
# Рекомендация: 75% от общей RAM системы
effective_cache_size = 1GB

# Work memory - память для операций сортировки, хэширования, материализации
# Рекомендация: 4MB-64MB на соединение в зависимости от запросов
work_mem = 4MB

# Maintenance work memory - память для операций обслуживания (VACUUM, CREATE INDEX и т.д.)
# Рекомендация: 64MB-1GB в зависимости от размера базы
maintenance_work_mem = 64MB

# Temp buffers - буферы для временных таблиц в сессии
# Увеличивайте если используете много временных таблиц
temp_buffers = 8MB
```

**Настройки `CPU` и параллелизма:**
```ini
# Максимальное количество worker процессов для параллельных операций
max_worker_processes = 8

# Максимальное количество процессов для параллельных запросов
max_parallel_workers_per_gather = 2

# Максимальное количество worker'ов для параллельного обслуживания
max_parallel_workers = 8

# Максимальное количество процессов для параллельной обработки
max_parallel_maintenance_workers = 2
```

**Настройки логирования:**
```ini
# Префикс для каждой строки лога
# %t - timestamp, %p - PID, %u - user, %d - database, %h - host
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '

# Уровень логирования (debug, info, notice, warning, error, log, fatal, panic)
log_min_messages = 'warning'

# Логировать длительность всех запросов
log_duration = on

# Логировать только медленные запросы (> 1 секунды)
log_min_duration_statement = 1000

# Какие операторы логировать (none, ddl, mod, all)
log_statement = 'ddl'

# Логировать checkpoints
log_checkpoints = on

# Логировать autovacuum операции
log_autovacuum_min_duration = 1000
```

**Настройки `WAL` (Write-Ahead Logging):**
```ini
# Размер сегмента WAL
wal_segment_size = 16MB

# Уровень WAL (minimal, replica, logical)
wal_level = 'replica'

# Размер буфера WAL
wal_buffers = 16MB

# Синхронизация WAL на диск (open_datasync, fdatasync, fsync, open_sync)
wal_sync_method = 'fdatasync'

# Checkpoint настройки
checkpoint_completion_target = 0.9  # Цель завершения checkpoint
checkpoint_timeout = 5min           # Максимальный интервал между checkpoints
max_wal_size = 1GB                  # Максимальный размер WAL
min_wal_size = 80MB                 # Минимальный размер WAL
```

**Настройки автовакуума (автоматическая очистка):**
```ini
# Включить автовакуум
autovacuum = on

# Максимальное количество автовакуум worker'ов
autovacuum_max_workers = 3

# Задержка между запусками автовакуума
autovacuum_naptime = 20s

# Порог для запуска автовакуума (процент измененных строк)
autovacuum_vacuum_threshold = 50

# Масштаб для порога автовакуума
autovacuum_vacuum_scale_factor = 0.02

# Аналогично для автоанализа
autovacuum_analyze_threshold = 50
autovacuum_analyze_scale_factor = 0.02
```

**Настройки статистики и мониторинга:**
```ini
# Включить сбор статистики
track_activities = on
track_counts = on
track_io_timing = on
track_functions = 'pl'  # Track procedure language functions

# Размер shared memory для статистики
stats_temp_directory = '/tmp'

# Включить pg_stat_statements для мониторинга запросов
shared_preload_libraries = 'pg_stat_statements'
pg_stat_statements.max = 10000
pg_stat_statements.track = 'all'
pg_stat_statements.track_utility = on
```

**Безопасность и аутентификация:**
```ini
# Парольная политика
password_encryption = 'scram-sha-256'

# SSL настройки (для шифрования соединений)
ssl = off  # Включайте для production
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'

# Настройки для row security
row_security = on
```

**Производительность для разных типов нагрузки:**

**OLTP системы (транзакционная нагрузка):**
```ini
# OLTP: кэш и память для транзакционной нагрузки
shared_buffers = 1GB
effective_cache_size = 3GB
work_mem = 8MB
maintenance_work_mem = 128MB
max_connections = 200
autovacuum_max_workers = 4
```

**OLAP системы (аналитическая нагрузка):**
```ini
shared_buffers = 2GB
effective_cache_size = 6GB
work_mem = 128MB
maintenance_work_mem = 512MB
max_parallel_workers_per_gather = 4
max_parallel_workers = 8
```

**Data warehouse:**
```ini
# Data warehouse: максимум параллелизма и памяти
shared_buffers = 4GB
effective_cache_size = 12GB
work_mem = 256MB
maintenance_work_mem = 1GB
max_parallel_workers_per_gather = 8
max_parallel_workers = 16
```

**Мониторинг изменений конфигурации:**
```bash
# Проверить текущие настройки
SHOW ALL;

# Проверить конкретный параметр
SHOW shared_buffers;

# Посмотреть параметры, требующие перезапуска
SELECT name, setting, unit, context
FROM pg_settings
WHERE context = 'postmaster';
```

#### Настройка pg_hba.conf

**Файл `pg_hba.conf` управляет аутентификацией:**

```conf
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# Локальные соединения
local   all             postgres                                peer
local   all             all                                     md5

# IPv4 локальные соединения
host    all             all             127.0.0.1/32            md5
host    all             all             192.168.1.0/24          md5

# IPv6 локальные соединения
host    all             all             ::1/128                 md5
```

### Подключение к PostgreSQL

#### Использование psql

```bash
# Подключение к базе данных
psql -h localhost -U myuser -d mydb

# Или для локального подключения
psql -U myuser -d mydb

# Основные команды psql
\dt          # Список таблиц
\di          # Список индексов
\dv          # Список представлений
\df          # Список функций
\l           # Список баз данных
\c dbname    # Переключение на базу данных
\q           # Выход
```

#### Программное подключение

##### Java (JDBC)

```java
// Подключение к PostgreSQL через JDBC (DriverManager.getConnection)
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to PostgreSQL!");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
```

##### Python (psycopg2)

```java
// Пример для Python (psycopg2) см. в документации; здесь заглушка для структуры
```

## Лучшие практики

- **Резервное копирование:** настраивать регулярный **pg_dump** или **pg_basebackup**; хранить копии вне сервера; проверять восстановление.
- **Мониторинг:** использовать **pg_stat_activity**, **pg_stat_statements**; отслеживать **long-running** запросы, блокировки, использование диска и **WAL**.
- **Память:** задавать **shared_buffers** (порядка 25% `RAM` для малых систем), **effective_cache_size**, **work_mem** в соответствии с нагрузкой; не завышать **max_connections**.
- **Безопасность:** использовать **scram-sha-256** для паролей; ограничивать доступ через **pg_hba.conf**; в **production** включать **SSL**.
- **Индексы:** создавать индексы под частые **WHERE**/**JOIN**/**ORDER** `BY`; избегать лишних индексов на часто обновляемых таблицах; использовать **EXPLAIN ANALYZE**.
- **VACUUM и автовакуум:** следить за **bloat**; при необходимости настраивать **autovacuum_vacuum_scale_factor** и пороги; для больших таблиц планировать **VACUUM ANALYZE**.
- **Версионирование:** планировать обновления мажорных версий; тестировать на копии; использовать логическую репликацию для миграций при необходимости.

