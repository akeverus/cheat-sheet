---
title: "PostgreSQL: Репликация"
description: "Полное руководство по настройке и управлению репликацией в PostgreSQL: Streaming Replication, Logical Replication, Master-Slave, Master-Master, мониторинг и failover"
tags: ["postgresql", "replication", "high-availability", "streaming-replication", "logical-replication", "failover"]
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-admin.md"]
next: ["postgres-high-availability.md", "postgres-backup-restore.md"]
updated: "2026-02-06"
related: ["databases/postgres-monitoring.md", "databases/postgres-performance-tuning.md"]
---

# **PostgreSQL**: Репликация

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Replication](https://www.postgresql.org/docs/current/warm-standby.html) — репликация и standby

### См. также
- [postgres-basics.md](postgres-basics.md) — основы PostgreSQL
- [postgres-high-availability.md](postgres-high-availability.md) — высокая доступность

## Содержание

- [Введение в репликацию **PostgreSQL**](#введение-в-репликацию-postgresql)
  - [Типы репликации](#типы-репликации)
  - [Преимущества репликации](#преимущества-репликации)
- [**Streaming Replication** (**физическая репликация**)](#streaming-replication-физическая-репликация)
  - [Архитектура **Streaming Replication**](#архитектура-streaming-replication)
  - [Настройка **Primary** сервера](#настройка-primary-сервера)
  - [Создание базовой реплики](#создание-базовой-реплики)
  - [Создание **replication slot**](#создание-replication-slot)
  - [Запуск репликации](#запуск-репликации)
  - [Мониторинг репликации](#мониторинг-репликации)
  - [Синхронная репликация](#синхронная-репликация)
- [**Logical Replication**](#logical-replication)
  - [Преимущества **Logical Replication**](#преимущества-logical-replication)
  - [Настройка **Logical Replication**](#настройка-logical-replication)
  - [Мониторинг **Logical Replication**](#мониторинг-logical-replication)
  - [Управление подписками](#управление-подписками)
- [Настройка **Master-Slave**](#настройка-master-slave)
  - [Архитектура **Master-Slave**](#архитектура-master-slave)
  - [Настройка нескольких **Standby** серверов](#настройка-нескольких-standby-серверов)
  - [Проверка всех реплик](#проверка-всех-реплик)
- [Настройка **Master-Master** (**Bidirectional Replication**)](#настройка-master-master-bidirectional-replication)
  - [Архитектура **Master-Master**](#архитектура-master-master)
  - [Настройка двунаправленной репликации](#настройка-двунаправленной-репликации)
  - [Обработка конфликтов](#обработка-конфликтов)
- [Мониторинг репликации](#мониторинг-репликации-1)
  - [Ключевые метрики](#ключевые-метрики)
  - [Алерты и мониторинг](#алерты-и-мониторинг)
- [**Failover** и автоматическое переключение](#failover-и-автоматическое-переключение)
  - [Ручной **failover**](#ручной-failover)
  - [Автоматический **failover** с **Patroni**](#автоматический-failover-с-patroni)
  - [Автоматический **failover** с **pg_auto_failover**](#автоматический-failover-с-pgautofailover)
- [Оптимизация репликации](#оптимизация-репликации)
  - [Настройка производительности](#настройка-производительности)
  - [Мониторинг производительности](#мониторинг-производительности)
- [Лучшие практики](#лучшие-практики)
  - [Безопасность](#безопасность)
  - [Мониторинг](#мониторинг)
  - [Резервное копирование](#резервное-копирование)
  - [Производительность](#производительность)
- [Решение проблем](#решение-проблем)
  - [Проблема: Репликация не запускается](#проблема-репликация-не-запускается)
  - [Проблема: Большой **lag** репликации](#проблема-большой-lag-репликации)
  - [Проблема: **Replication slot** переполнен](#проблема-replication-slot-переполнен)
- [**Advanced Replication Scenarios**](#advanced-replication-scenarios)
  - [**Cascading Replication**](#cascading-replication)
  - [**Delayed Replication**](#delayed-replication)
  - [**Selective Replication**](#selective-replication)
- [**Replication Monitoring and Management**](#replication-monitoring-and-management)
  - [**Comprehensive Replication Dashboard**](#comprehensive-replication-dashboard)
  - [**Automated Replication Health Checks**](#automated-replication-health-checks)
  - [**Replication Slot Management**](#replication-slot-management)
- [**Replication Performance Tuning**](#replication-performance-tuning)
  - [**Optimizing WAL Generation**](#optimizing-wal-generation)
  - [**Network Optimization**](#network-optimization)
  - [**Standby Server Optimization**](#standby-server-optimization)
- [**Advanced Failover Strategies**](#advanced-failover-strategies)
  - [**Automated Failover Script**](#automated-failover-script)
  - [**Failover with pg_rewind**](#failover-with-pgrewind)
- [**Logical Replication Advanced**](#logical-replication-advanced)
  - [**Filtered Logical Replication**](#filtered-logical-replication)
  - [**Cross-Version Logical Replication**](#cross-version-logical-replication)
  - [**Logical Replication Monitoring**](#logical-replication-monitoring)
- [**Replication Best Practices Summary**](#replication-best-practices-summary)
  - [**Setup**](#setup)
  - [**Monitoring**](#monitoring)
  - [**Maintenance**](#maintenance)
  - [**Performance**](#performance)

## Введение в репликацию **PostgreSQL**

Репликация в **PostgreSQL** позволяет создавать копии базы данных на других серверах для обеспечения высокой доступности, распределения нагрузки и резервного копирования. **PostgreSQL** поддерживает несколько типов репликации, каждый из которых подходит для различных сценариев использования.

### Типы репликации

1. **Streaming `Replication` (**физическая репликация**)**: Синхронная или асинхронная репликация на уровне файлов **WAL**
2. **Logical Replication**: Репликация на уровне логических изменений данных
3. **Cascading Replication**: Многоуровневая репликация через промежуточные серверы
4. **Synchronous Replication**: Синхронная репликация с гарантией консистентности

### Преимущества репликации

- **Высокая доступность**: Автоматический **failover** при сбое основного сервера
- **Масштабирование чтения**: Распределение запросов на чтение между репликами
- **Резервное копирование**: Реплики могут использоваться для бэкапов без нагрузки на основной сервер
- **Географическое распределение**: Реплики в разных регионах для снижения задержек

---

## **Streaming Replication** (**физическая репликация**)

**Streaming Replication** - это метод физической репликации, при котором изменения передаются в реальном времени через **WAL** (**Write-`Ahead` Log**) файлы.

### Архитектура **Streaming Replication**

```text
┌─────────────────┐         ┌─────────────────┐
│   Primary       │────────▶│   Standby        │
│   Server        │  WAL    │   Server        │
│                 │  Stream │                 │
└─────────────────┘         └─────────────────┘
       │                           │
       │                           │
       ▼                           ▼
   WAL Files                  Apply WAL
```

### Настройка **Primary** сервера

#### 1. Настройка **postgresql.conf**

**Основные параметры для **WAL** и репликации:**

```conf
# Включить WAL архивирование
wal_level = replica

# Минимальное количество WAL сегментов для репликации
max_wal_senders = 3

# Максимальное пространство для WAL файлов
max_wal_size = 1GB

# Минимальное пространство для WAL файлов
min_wal_size = 80MB

# Время хранения WAL файлов
wal_keep_segments = 32

# Включить hot standby
hot_standby = on
```

#### 2. Настройка **pg_hba.conf**

```conf
# Разрешить подключение для репликации
host    replication     replicator     192.168.1.0/24    md5
```

#### 3. Создание пользователя для репликации

```sql
-- Создать пользователя для репликации
CREATE USER replicator WITH REPLICATION PASSWORD 'secure_password';

-- Проверить права
\du replicator
```

### Создание базовой реплики

#### 1. Остановка **PostgreSQL** на **Standby** сервере

```bash
sudo systemctl stop postgresql
```

#### 2. Резервное копирование с **Primary** сервера

```bash
# На Primary сервере
pg_basebackup -h primary_host -D /var/lib/postgresql/data -U replicator -v -P -W
```

#### 3. Настройка **recovery.conf** (**PostgreSQL 12+**)

В **PostgreSQL** 12+ файл `**recovery.conf**` был удален. Настройки переносятся в `**postgresql.conf**` и `**postgresql.auto.conf**`.

**Создайте файл `**standby.signal**`:**

```bash
touch /var/lib/postgresql/data/standby.signal
```

**Настройте `**postgresql.conf**` на **Standby**:**

```conf
# Настройки репликации
primary_conninfo = 'host=primary_host port=5432 user=replicator password=secure_password'
primary_slot_name = 'standby_slot'
```

#### 4. Альтернативный способ (**PostgreSQL < 12**)

**Для старых версий создайте `**recovery.conf**`:**

```conf
standby_mode = 'on'
primary_conninfo = 'host=primary_host port=5432 user=replicator password=secure_password'
primary_slot_name = 'standby_slot'
trigger_file = '/tmp/postgresql.trigger'
```

### Создание **replication slot**

**Replication slots** предотвращают удаление **WAL** файлов, необходимых для репликации.

```sql
-- На Primary сервере
SELECT pg_create_physical_replication_slot('standby_slot');

-- Проверить слоты
SELECT * FROM pg_replication_slots;
```

### Запуск репликации

```bash
# На Standby сервере
sudo systemctl start postgresql

# Проверить статус репликации
sudo -u postgres psql -c "SELECT * FROM pg_stat_replication;"
```

### Мониторинг репликации

#### На **Primary** сервере

```sql
-- Проверить статус репликации
SELECT 
    pid,
    usename,
    application_name,
    client_addr,
    state,
    sync_state,
    sync_priority,
    pg_wal_lsn_diff(pg_current_wal_lsn(), sent_lsn) AS sent_lag,
    pg_wal_lsn_diff(sent_lsn, write_lsn) AS write_lag,
    pg_wal_lsn_diff(write_lsn, flush_lsn) AS flush_lag,
    pg_wal_lsn_diff(flush_lsn, replay_lsn) AS replay_lag
FROM pg_stat_replication;

-- Проверить replication slots
SELECT 
    slot_name,
    slot_type,
    active,
    pg_wal_lsn_diff(pg_current_wal_lsn(), restart_lsn) AS lag_bytes
FROM pg_replication_slots;
```

#### На **Standby** сервере

```sql
-- Проверить статус репликации
SELECT 
    pg_is_in_recovery() AS is_standby,
    pg_last_wal_receive_lsn() AS receive_lsn,
    pg_last_wal_replay_lsn() AS replay_lsn,
    pg_wal_lsn_diff(pg_last_wal_receive_lsn(), pg_last_wal_replay_lsn()) AS lag_bytes;

-- Проверить задержку репликации
SELECT 
    EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp())) AS lag_seconds;
```

### Синхронная репликация

**Для синхронной репликации настройте на **Primary**:**

```conf
# В postgresql.conf
synchronous_standby_names = 'standby1,standby2'
synchronous_commit = on
```

**Или для одной синхронной реплики:**

```conf
synchronous_standby_names = 'FIRST 1 (standby1, standby2)'
```

**Проверка синхронной репликации:**

```sql
-- На Primary
SELECT 
    application_name,
    sync_state,
    sync_priority
FROM pg_stat_replication
WHERE sync_state = 'sync';
```

---

## **Logical Replication**

**Logical Replication** позволяет реплицировать данные на уровне таблиц, а не на уровне файлов. Это полезно для выборочной репликации, миграций и обновлений.

### Преимущества **Logical Replication**

- Выборочная репликация таблиц
- Репликация между разными версиями **PostgreSQL**
- Возможность фильтрации данных
- Репликация в другие системы

### Настройка **Logical Replication**

#### 1. Настройка **Primary** сервера

```conf
# В postgresql.conf
wal_level = logical
max_replication_slots = 4
max_wal_senders = 4
```

#### 2. Создание публикации

```sql
-- Создать публикацию для всех таблиц
CREATE PUBLICATION my_publication FOR ALL TABLES;

-- Или для конкретных таблиц
CREATE PUBLICATION my_publication FOR TABLE users, orders, products;

-- С публикацией изменений по умолчанию
CREATE PUBLICATION my_publication 
FOR TABLE users, orders 
WITH (publish = 'insert,update,delete');
```

#### 3. Создание подписки на **Standby**

```sql
-- На Standby сервере создать таблицы (структура должна совпадать)
CREATE TABLE users (...);
CREATE TABLE orders (...);

-- Создать подписку
CREATE SUBSCRIPTION my_subscription
CONNECTION 'host=primary_host port=5432 dbname=mydb user=replicator password=secure_password'
PUBLICATION my_publication;

-- Проверить статус подписки
SELECT * FROM pg_subscription;
SELECT * FROM pg_subscription_rel;
```

### Мониторинг **Logical Replication**

```sql
-- На Primary сервере
SELECT 
    pubname,
    puballtables,
    pubinsert,
    pubupdate,
    pubdelete
FROM pg_publication;

-- На Standby сервере
SELECT 
    subname,
    subenabled,
    subslotname,
    subpublications
FROM pg_subscription;

-- Статистика репликации
SELECT 
    subname,
    apply_lag,
    sync_state
FROM pg_stat_subscription;
```

### Управление подписками

```sql
-- Отключить подписку
ALTER SUBSCRIPTION my_subscription DISABLE;

-- Включить подписку
ALTER SUBSCRIPTION my_subscription ENABLE;

-- Обновить подключение
ALTER SUBSCRIPTION my_subscription 
CONNECTION 'host=new_primary port=5432 dbname=mydb user=replicator password=secure_password';

-- Удалить подписку
DROP SUBSCRIPTION my_subscription;
```

---

## Настройка **Master-Slave**

### Архитектура **Master-Slave**

```
┌─────────────┐
│   Master    │
│  (Primary)  │
└──────┬──────┘
       │
       ├──────────────┬──────────────┐
       │              │              │
       ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Slave 1 │   │  Slave 2 │   │  Slave 3 │
│ (Standby)│   │ (Standby) │   │ (Standby)│
└──────────┘   └──────────┘   └──────────┘
```

### Настройка нескольких **Standby** серверов

#### 1. На **Primary** сервере

```conf
# Увеличить количество слотов
max_replication_slots = 5
max_wal_senders = 5

# Настройки для нескольких реплик
wal_level = replica
```

#### 2. Создание слотов для каждой реплики

```sql
SELECT pg_create_physical_replication_slot('standby1_slot');
SELECT pg_create_physical_replication_slot('standby2_slot');
SELECT pg_create_physical_replication_slot('standby3_slot');
```

#### 3. Настройка каждой **Standby** реплики

**На каждой **Standby** сервере настройте уникальный `**primary_slot_name**`:**

```conf
# Standby 1
primary_conninfo = 'host=primary_host port=5432 user=replicator password=secure_password'
primary_slot_name = 'standby1_slot'

# Standby 2
primary_conninfo = 'host=primary_host port=5432 user=replicator password=secure_password'
primary_slot_name = 'standby2_slot'
```

### Проверка всех реплик

```sql
-- На Primary сервере
SELECT 
    application_name,
    client_addr,
    state,
    sync_state,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS lag_bytes
FROM pg_stat_replication
ORDER BY application_name;
```

---

## Настройка **Master-Master** (**Bidirectional Replication**)

**PostgreSQL** не поддерживает нативную **Master-Master** репликацию, но можно использовать логическую репликацию для двунаправленной синхронизации.

### Архитектура **Master-Master**

```
┌─────────────┐         ┌─────────────┐
│  Server A   │◀───────▶│  Server B   │
│  (Primary)  │  Logical│  (Primary)   │
└─────────────┘  Repl   └─────────────┘
```

### Настройка двунаправленной репликации

#### 1. Настройка **Server** A

```sql
-- Создать публикацию
CREATE PUBLICATION pub_server_a FOR TABLE users, orders;

-- Создать подписку на Server B
CREATE SUBSCRIPTION sub_to_server_b
CONNECTION 'host=server_b port=5432 dbname=mydb user=replicator password=secure_password'
PUBLICATION pub_server_b;
```

#### 2. Настройка **Server** B

```sql
-- Создать публикацию
CREATE PUBLICATION pub_server_b FOR TABLE users, orders;

-- Создать подписку на Server A
CREATE SUBSCRIPTION sub_to_server_a
CONNECTION 'host=server_a port=5432 dbname=mydb user=replicator password=secure_password'
PUBLICATION pub_server_a;
```

### Обработка конфликтов

**При двунаправленной репликации возможны конфликты. Используйте триггеры для их разрешения:**

```sql
-- Функция разрешения конфликтов
CREATE OR REPLACE FUNCTION resolve_replication_conflict()
RETURNS TRIGGER AS $$
BEGIN
    -- Логика разрешения конфликтов
    -- Например, использовать последнее изменение
    IF NEW.updated_at > OLD.updated_at THEN
        RETURN NEW;
    ELSE
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Применить к таблице
CREATE TRIGGER conflict_resolver
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION resolve_replication_conflict();
```

---

## Мониторинг репликации

### Ключевые метрики

#### 1. **Lag** (**задержка репликации**)

```sql
-- Задержка в байтах
SELECT 
    application_name,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS lag_bytes,
    pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn)) AS lag_pretty
FROM pg_stat_replication;

-- Задержка во времени
SELECT 
    application_name,
    EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp())) AS lag_seconds
FROM pg_stat_replication;
```

#### 2. Статус репликации

```sql
-- Детальная информация о репликации
SELECT 
    pid,
    usename,
    application_name,
    client_addr,
    state,
    sync_state,
    sync_priority,
    pg_wal_lsn_diff(pg_current_wal_lsn(), sent_lsn) AS sent_lag,
    pg_wal_lsn_diff(sent_lsn, write_lsn) AS write_lag,
    pg_wal_lsn_diff(write_lsn, flush_lsn) AS flush_lag,
    pg_wal_lsn_diff(flush_lsn, replay_lsn) AS replay_lag,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS total_lag
FROM pg_stat_replication;
```

#### 3. **Replication Slots**

```sql
-- Информация о слотах
SELECT 
    slot_name,
    slot_type,
    database,
    active,
    pg_wal_lsn_diff(pg_current_wal_lsn(), restart_lsn) AS lag_bytes,
    pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), restart_lsn)) AS lag_pretty
FROM pg_replication_slots;
```

### Алерты и мониторинг

#### Скрипт проверки репликации

```bash
#!/bin/bash
# check_replication.sh

PRIMARY_HOST="primary_host"
STANDBY_HOST="standby_host"
MAX_LAG_BYTES=104857600  # 100MB
MAX_LAG_SECONDS=30

# Проверить lag в байтах
LAG_BYTES=$(psql -h $STANDBY_HOST -U postgres -t -c "
    SELECT pg_wal_lsn_diff(
        pg_last_wal_receive_lsn(),
        pg_last_wal_replay_lsn()
    );
")

# Проверить lag во времени
LAG_SECONDS=$(psql -h $STANDBY_HOST -U postgres -t -c "
    SELECT EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp()));
")

if [ "$LAG_BYTES" -gt "$MAX_LAG_BYTES" ]; then
    echo "ALERT: Replication lag is ${LAG_BYTES} bytes"
    exit 1
fi

if [ "$LAG_SECONDS" -gt "$MAX_LAG_SECONDS" ]; then
    echo "ALERT: Replication lag is ${LAG_SECONDS} seconds"
    exit 1
fi

echo "OK: Replication is healthy"
exit 0
```

---

## **Failover** и автоматическое переключение

### Ручной **failover**

#### 1. Промоутинг **Standby** в **Primary**

```bash
# На Standby сервере
sudo -u postgres pg_ctl promote -D /var/lib/postgresql/data

# Или создать trigger файл
touch /tmp/postgresql.trigger
```

#### 2. Обновление подключений

После промоутинга обновите подключения приложений на новый **Primary** сервер.

### Автоматический **failover** с **Patroni**

**Patroni** - это решение для автоматического управления репликацией и **failover**.

#### Установка **Patroni**

```bash
pip install patroni[etcd]
# или
pip install patroni[consul]
# или
pip install patroni[zookeeper]
```

#### Конфигурация **Patroni**

```yaml
# patroni.yml
scope: postgres
namespace: /db/
name: postgresql1

restapi:
  listen: 127.0.0.1:8008
  connect_address: 127.0.0.1:8008

etcd:
  host: 127.0.0.1:2379

bootstrap:
  dcs:
    ttl: 30
    loop_wait: 10
    retry_timeout: 30
    maximum_lag_on_failover: 1048576
    postgresql:
      use_pg_rewind: true
      parameters:
        wal_level: replica
        hot_standby: "on"
        max_connections: 100
        max_wal_senders: 10
        max_replication_slots: 10
        wal_keep_segments: 8

postgresql:
  listen: 127.0.0.1:5432
  connect_address: 127.0.0.1:5432
  data_dir: /var/lib/postgresql/data
  pgpass: /var/lib/postgresql/.pgpass
  authentication:
    replication:
      username: replicator
      password: secure_password
    superuser:
      username: postgres
      password: secure_password
  parameters:
    unix_socket_directories: '/var/run/postgresql'

tags:
  nofailover: false
  noloadbalance: false
  clonefrom: false
  nosync: false
```

#### Запуск **Patroni**

```bash
patroni patroni.yml
```

### Автоматический **failover** с **pg_auto_failover**

**pg_auto_failover** - это расширение **PostgreSQL** для автоматического **failover**.

#### Установка

```bash
# На Ubuntu/Debian
sudo apt-get install postgresql-14-auto-failover

# На RHEL/CentOS
sudo yum install postgresql14-auto-failover
```

#### Настройка **Monitor**

```bash
# Создать monitor
pg_auto_failover create monitor \
  --hostname monitor_host \
  --pgport 5432 \
  --pgdata /var/lib/postgresql/data/monitor
```

#### Настройка **Primary**

```bash
pg_auto_failover create postgres \
  --hostname primary_host \
  --pgport 5432 \
  --pgdata /var/lib/postgresql/data \
  --monitor 'postgres://autoctl_node@monitor_host:5432/pg_auto_failover'
```

#### Настройка **Standby**

```bash
pg_auto_failover create postgres \
  --hostname standby_host \
  --pgport 5432 \
  --pgdata /var/lib/postgresql/data \
  --monitor 'postgres://autoctl_node@monitor_host:5432/pg_auto_failover'
```

#### Проверка статуса

```bash
pg_auto_failover status --monitor 'postgres://autoctl_node@monitor_host:5432/pg_auto_failover'
```

---

## Оптимизация репликации

### Настройка производительности

#### 1. Оптимизация **WAL**

```conf
# Увеличить размер WAL буфера
wal_buffers = 16MB

# Настройка checkpoint
checkpoint_timeout = 15min
max_wal_size = 4GB
min_wal_size = 1GB

# Компрессия WAL
wal_compression = on
```

#### 2. Оптимизация сети

```conf
# Увеличить размер TCP буферов
tcp_keepalives_idle = 600
tcp_keepalives_interval = 30
tcp_keepalives_count = 3
```

#### 3. Параллельная репликация

```conf
# На Standby сервере
max_parallel_workers_per_gather = 4
max_parallel_workers = 8
max_worker_processes = 8
```

### Мониторинг производительности

```sql
-- Статистика репликации
SELECT 
    application_name,
    state,
    sync_state,
    pg_wal_lsn_diff(pg_current_wal_lsn(), sent_lsn) AS sent_lag_bytes,
    pg_wal_lsn_diff(sent_lsn, write_lsn) AS write_lag_bytes,
    pg_wal_lsn_diff(write_lsn, flush_lsn) AS flush_lag_bytes,
    pg_wal_lsn_diff(flush_lsn, replay_lsn) AS replay_lag_bytes
FROM pg_stat_replication;

-- Производительность WAL
SELECT 
    pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), '0/0')) AS total_wal_size,
    pg_size_pretty(pg_current_wal_lsn() - '0/0') AS current_wal_position;
```

---

## Лучшие практики

### Безопасность

1. **Использовать отдельного пользователя для репликации**
   ```sql
   CREATE USER replicator WITH REPLICATION PASSWORD 'strong_password';
   ```

2. **Ограничить доступ в `pg_hba`.conf**
   ```conf
   host    replication     replicator     192.168.1.0/24    md5
   ```

3. **Использовать `SSL` для репликации**
   ```conf
   # В postgresql.conf на Primary
   ssl = on
   ssl_cert_file = 'server.crt'
   ssl_key_file = 'server.key'
   
   # В primary_conninfo на Standby
   primary_conninfo = '... sslmode=require'
   ```

### Мониторинг

1. **Настроить алерты на lag**
2. **Мониторить replication slots**
3. **Отслеживать статус репликации**
4. **Логировать ошибки репликации**

### Резервное копирование

1. **Использовать реплики для бэкапов**
2. **Тестировать восстановление регулярно**
3. **Хранить `WAL` архивы**

### Производительность

1. **Использовать replication slots**
2. **Настроить правильный wal_level**
3. **Оптимизировать сетевые настройки**
4. **Использовать синхронную репликацию только при необходимости**

---

## Решение проблем

### Проблема: Репликация не запускается

**Причины:**
- Неправильные настройки в **postgresql.conf**
- Проблемы с правами доступа
- Неправильная настройка **pg_hba.conf**

**Решение:**
```bash
# Проверить логи
tail -f /var/log/postgresql/postgresql-*.log

# Проверить настройки
psql -c "SHOW wal_level;"
psql -c "SHOW max_wal_senders;"

# Проверить права
psql -c "\du replicator"
```

### Проблема: Большой **lag** репликации

**Причины:**
- Медленная сеть
- Недостаточно ресурсов на **Standby**
- Много транзакций на **Primary**

**Решение:**
```sql
-- Проверить lag
SELECT pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS lag_bytes;

-- Увеличить ресурсы на Standby
-- Увеличить max_parallel_workers
-- Оптимизировать запросы на Primary
```

### Проблема: **Replication slot** переполнен

**Причины:**
- **Standby** сервер долго не подключался
- **WAL** файлы не удаляются

**Решение:**
```sql
-- Проверить слоты
SELECT * FROM pg_replication_slots;

-- Удалить неактивный слот
SELECT pg_drop_replication_slot('old_slot_name');
```

## **Advanced Replication Scenarios**

### **Cascading Replication**

**Cascading Replication** позволяет создавать цепочку реплик, где одна реплика может быть источником для другой.

#### Настройка **Cascading Replication**

```bash
# Primary -> Standby1 -> Standby2

# На Standby1 (который будет источником для Standby2)
# В postgresql.conf
wal_level = replica
max_wal_senders = 3
hot_standby = on

# Создать replication slot для Standby2
SELECT pg_create_physical_replication_slot('standby2_slot');

# На Standby2
# В postgresql.conf
primary_conninfo = 'host=standby1_host port=5432 user=replicator password=secure_password'
primary_slot_name = 'standby2_slot'
```

### **Delayed Replication**

**Delayed Replication** позволяет задержать применение изменений на реплике, что полезно для защиты от ошибок.

```conf
# На Standby сервере в postgresql.conf
recovery_min_apply_delay = '1h'  # Задержка 1 час
```

### **Selective Replication**

**Использование **Logical Replication** для выборочной репликации таблиц:**

```sql
-- Создать публикацию только для определенных таблиц
CREATE PUBLICATION selective_pub 
FOR TABLE users, orders 
WHERE (region = 'US');

-- Или с фильтрацией по схеме
CREATE PUBLICATION schema_pub 
FOR ALL TABLES IN SCHEMA public;
```

## **Replication Monitoring and Management**

### **Comprehensive Replication Dashboard**

```sql
-- Создать представление для комплексного мониторинга репликации
CREATE VIEW replication_dashboard AS
SELECT 
    r.application_name,
    r.client_addr,
    r.state,
    r.sync_state,
    r.sync_priority,
    pg_wal_lsn_diff(pg_current_wal_lsn(), r.sent_lsn) AS sent_lag_bytes,
    pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), r.sent_lsn)) AS sent_lag_pretty,
    pg_wal_lsn_diff(r.sent_lsn, r.write_lsn) AS write_lag_bytes,
    pg_wal_lsn_diff(r.write_lsn, r.flush_lsn) AS flush_lag_bytes,
    pg_wal_lsn_diff(r.flush_lsn, r.replay_lsn) AS replay_lag_bytes,
    pg_wal_lsn_diff(pg_current_wal_lsn(), r.replay_lsn) AS total_lag_bytes,
    pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), r.replay_lsn)) AS total_lag_pretty,
    r.backend_start,
    r.state_change
FROM pg_stat_replication r;

-- Использовать представление
SELECT * FROM replication_dashboard;
```

### **Automated Replication Health Checks**

```sql
-- Функция для проверки здоровья репликации
CREATE OR REPLACE FUNCTION check_replication_health()
RETURNS TABLE(
    replica_name TEXT,
    status TEXT,
    lag_bytes BIGINT,
    lag_seconds NUMERIC,
    issues TEXT[]
) AS $$
DECLARE
    replica_rec RECORD;
    issues_list TEXT[];
BEGIN
    FOR replica_rec IN
        SELECT 
            application_name,
            client_addr,
            state,
            pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS lag_bytes,
            EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp())) AS lag_seconds
        FROM pg_stat_replication
    LOOP
        issues_list := ARRAY[]::TEXT[];
        
        -- Проверить состояние
        IF replica_rec.state != 'streaming' THEN
            issues_list := array_append(issues_list, format('State is %s, expected streaming', replica_rec.state));
        END IF;
        
        -- Проверить lag в байтах
        IF replica_rec.lag_bytes > 104857600 THEN  -- 100MB
            issues_list := array_append(issues_list, format('Lag is %s bytes (max: 100MB)', replica_rec.lag_bytes));
        END IF;
        
        -- Проверить lag во времени
        IF replica_rec.lag_seconds > 30 THEN
            issues_list := array_append(issues_list, format('Lag is %.2f seconds (max: 30s)', replica_rec.lag_seconds));
        END IF;
        
        RETURN QUERY
        SELECT 
            replica_rec.application_name,
            CASE 
                WHEN array_length(issues_list, 1) IS NULL THEN 'OK'
                ELSE 'WARNING'
            END,
            replica_rec.lag_bytes,
            replica_rec.lag_seconds,
            issues_list;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Использовать функцию
SELECT * FROM check_replication_health();
```

### **Replication Slot Management**

```sql
-- Функция для управления replication slots
CREATE OR REPLACE FUNCTION manage_replication_slots()
RETURNS TABLE(
    slot_name TEXT,
    slot_type TEXT,
    active BOOLEAN,
    lag_bytes BIGINT,
    lag_pretty TEXT,
    status TEXT,
    recommendation TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.slot_name,
        s.slot_type,
        s.active,
        pg_wal_lsn_diff(pg_current_wal_lsn(), s.restart_lsn) AS lag_bytes,
        pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), s.restart_lsn)) AS lag_pretty,
        CASE 
            WHEN NOT s.active AND pg_wal_lsn_diff(pg_current_wal_lsn(), s.restart_lsn) > 1073741824 THEN 'CRITICAL'
            WHEN NOT s.active THEN 'WARNING'
            ELSE 'OK'
        END AS status,
        CASE 
            WHEN NOT s.active AND pg_wal_lsn_diff(pg_current_wal_lsn(), s.restart_lsn) > 1073741824 THEN 
                format('Consider dropping slot %s - inactive and lag > 1GB', s.slot_name)
            WHEN NOT s.active THEN 
                format('Slot %s is inactive - verify if still needed', s.slot_name)
            ELSE 'OK'
        END AS recommendation
    FROM pg_replication_slots s;
END;
$$ LANGUAGE plpgsql;
```

## **Replication Performance Tuning**

### **Optimizing WAL Generation**

```conf
# Оптимизация генерации WAL
# В postgresql.conf на Primary

# Минимизировать WAL для определенных операций
wal_level = replica  # Минимальный уровень для streaming replication

# Оптимизация checkpoint
checkpoint_timeout = 15min
max_wal_size = 4GB
min_wal_size = 1GB
checkpoint_completion_target = 0.9

# Компрессия WAL
wal_compression = on

# Оптимизация для больших транзакций
commit_delay = 0
commit_siblings = 5
```

### **Network Optimization**

```conf
# Оптимизация сетевых параметров для репликации
# В postgresql.conf

# TCP keepalive настройки
tcp_keepalives_idle = 600
tcp_keepalives_interval = 30
tcp_keepalives_count = 3

# Размер буферов для репликации
wal_sender_timeout = 60s
wal_receiver_timeout = 60s
```

### **Standby Server Optimization**

```conf
# Оптимизация Standby сервера
# В postgresql.conf на Standby

# Параллельная репликация
max_parallel_workers_per_gather = 4
max_parallel_workers = 8
max_worker_processes = 8

# Оптимизация применения WAL
hot_standby = on
hot_standby_feedback = on
max_standby_streaming_delay = 30s
max_standby_archive_delay = 300s

# Оптимизация памяти
shared_buffers = 4GB
effective_cache_size = 12GB
work_mem = 64MB
```

## **Advanced Failover Strategies**

### **Automated Failover Script**

```bash
#!/bin/bash
# automated_failover.sh

PRIMARY_HOST="primary_host"
STANDBY_HOST="standby_host"
MAX_LAG_SECONDS=60
HEALTH_CHECK_TIMEOUT=10

# Проверить доступность Primary
if ! timeout $HEALTH_CHECK_TIMEOUT psql -h $PRIMARY_HOST -U postgres -c "SELECT 1;" > /dev/null 2>&1; then
    echo "Primary server is down. Initiating failover..."
    
    # Промоутить Standby
    ssh $STANDBY_HOST "sudo -u postgres pg_ctl promote -D /var/lib/postgresql/data"
    
    # Обновить DNS или load balancer
    # update_dns_or_lb $STANDBY_HOST
    
    # Уведомить администраторов
    echo "Failover completed. New primary: $STANDBY_HOST" | \
        mail -s "PostgreSQL Failover Alert" admin@example.com
    
    exit 0
fi

# Проверить lag
LAG_SECONDS=$(psql -h $STANDBY_HOST -U postgres -t -c "
    SELECT EXTRACT(EPOCH FROM (now() - pg_last_xact_replay_timestamp()));
")

if [ -z "$LAG_SECONDS" ] || [ "$(echo "$LAG_SECONDS > $MAX_LAG_SECONDS" | bc)" -eq 1 ]; then
    echo "WARNING: Replication lag is ${LAG_SECONDS} seconds"
    exit 1
fi

echo "OK: Replication is healthy"
exit 0
```

### **Failover with pg_rewind**

**pg_rewind** позволяет быстро синхронизировать старый **Primary** с новым **Primary** после **failover**.

```bash
# После failover, когда старый Primary снова доступен
# На старом Primary сервере

# Остановить PostgreSQL
sudo systemctl stop postgresql

# Выполнить pg_rewind
pg_rewind \
    --target-pgdata=/var/lib/postgresql/data \
    --source-server="host=new_primary port=5432 user=postgres"

# Настроить как Standby
touch /var/lib/postgresql/data/standby.signal

# В postgresql.conf
primary_conninfo = 'host=new_primary port=5432 user=replicator password=secure_password'

# Запустить PostgreSQL
sudo systemctl start postgresql
```

## **Logical Replication Advanced**

### **Filtered Logical Replication**

```sql
-- Репликация с фильтрацией данных
-- Создать публикацию с условием
CREATE PUBLICATION filtered_pub 
FOR TABLE orders 
WHERE (status = 'active');

-- Или использовать функции для фильтрации
CREATE OR REPLACE FUNCTION should_replicate()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'active' THEN
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
```

### **Cross-Version Logical Replication**

```sql
-- Репликация между разными версиями PostgreSQL
-- PostgreSQL 12 -> PostgreSQL 14

-- На PostgreSQL 12 (Primary)
CREATE PUBLICATION cross_version_pub FOR ALL TABLES;

-- На PostgreSQL 14 (Subscriber)
CREATE SUBSCRIPTION cross_version_sub
CONNECTION 'host=pg12_host port=5432 dbname=mydb user=replicator password=secure_password'
PUBLICATION cross_version_pub;
```

### **Logical Replication Monitoring**

```sql
-- Детальный мониторинг Logical Replication
CREATE VIEW logical_replication_status AS
SELECT 
    s.subname AS subscription_name,
    s.subenabled AS enabled,
    s.subslotname AS slot_name,
    s.subpublications AS publications,
    st.apply_lag,
    st.sync_state,
    st.sync_subid
FROM pg_subscription s
LEFT JOIN pg_stat_subscription st ON s.oid = st.subid;

-- Использовать представление
SELECT * FROM logical_replication_status;
```

## **Replication Best Practices Summary**

### **Setup**

1. **Использовать replication slots** для предотвращения потери **WAL** файлов
2. **Настроить правильный wal_level** (**replica для streaming, logical для logical replication**)
3. **Создать отдельного пользователя** для репликации
4. **Использовать SSL** для защищенной репликации

### **Monitoring**

1. **Мониторить lag** регулярно (**байты и время**)
2. **Проверять replication slots** на переполнение
3. **Отслеживать статус репликации** автоматически
4. **Настроить алерты** на проблемы

### **Maintenance**

1. **Регулярно тестировать failover** процедуры
2. **Обновлять реплики** при обновлении **Primary**
3. **Очищать старые replication slots** при необходимости
4. **Документировать конфигурацию** репликации

### **Performance**

1. **Оптимизировать `WAL` генерацию** на **Primary**
2. **Настроить параллельную репликацию** на **Standby**
3. **Использовать синхронную репликацию** только при необходимости
4. **Мониторить производительность** сети между серверами

---

- [`PostgreSQL Streaming Replication`](https://www.postgresql.org/docs/)
- [`PostgreSQL Logical Replication`](https://www.postgresql.org/docs/)
- [`Patroni Documentation`](https://www.postgresql.org/docs/)
- [`pg_auto_failover Documentation`](https://www.postgresql.org/docs/)

---

**Дата последнего обновления:** 2026-02-06


