---
title: "ClickHouse: Репликация и кластеры - Отказоустойчивость и масштабируемость"
description: "Комплексное руководство по репликации ClickHouse: ReplicatedMergeTree, кластеры, ZooKeeper, отказоустойчивость и распределенные запросы"
tags: ["clickhouse", "replication", "clusters", "zookeeper", "high-availability", "distributed", "replicatedmergetree"]
difficulty: "advanced"
prerequisites: ["databases/clickhouse-tables.md"]
updated: "2026-01-21"
related: ["databases/clickhouse-tables.md", "databases/clickhouse-materialized-views.md"]
---

# ClickHouse: Репликация и кластеры - Отказоустойчивость и масштабируемость

Комплексное руководство по репликации ClickHouse: ReplicatedMergeTree, кластеры, ZooKeeper, отказоустойчивость и распределенные запросы.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Replication](https://clickhouse.com/docs/engines/table-engines/mergetree-family/replication)
- [ReplicatedMergeTree](https://clickhouse.com/docs/engines/table-engines/mergetree-family/replicatedmergetree)
- [Distributed DDL](https://clickhouse.com/docs/sql-reference/distributed-ddl)

### Baeldung
- [ClickHouse Replication](https://www.baeldung.com/clickhouse-replication)

### См. также
- `databases/clickhouse-tables.md` - Движки таблиц
- `databases/clickhouse-materialized-views.md` - Материализованные представления в кластерах

## Содержание

- [Введение в репликацию ClickHouse](#введение-в-репликацию-clickhouse)
- [Архитектура репликации](#архитектура-репликации)
- [ZooKeeper для координации](#zookeeper-для-координации)
- [ReplicatedMergeTree движок](#replicatedmergetree-движок)
- [Настройка кластера](#настройка-кластера)
- [Распределенные таблицы](#распределенные-таблицы)
- [Отказоустойчивость](#отказоустойчивость)
- [Мониторинг кластера](#мониторинг-кластера)
- [Best Practices](#best-practices)
- [Заключение](#заключение)

## Введение в репликацию ClickHouse

Репликация в ClickHouse обеспечивает высокую доступность, отказоустойчивость и масштабируемость чтения. В отличие от традиционных СУБД, ClickHouse использует асинхронную репликацию на уровне партиций.

### Типы репликации

1. **Репликация таблиц** (ReplicatedMergeTree)
   - Синхронизация данных между узлами
   - Автоматическое восстановление после сбоев
   - Отказоустойчивость операций

2. **Распределенные запросы** (Distributed engine)
   - Распределение запросов по кластеру
   - Балансировка нагрузки
   - Масштабируемость чтения

### Преимущества репликации

- **Высокая доступность**: Продолжение работы при отказе узлов
- **Отказоустойчивость**: Защита от потери данных
- **Масштабируемость чтения**: Распределение нагрузки
- **Автоматическое восстановление**: Самовосстановление после сбоев

## Архитектура репликации

### Компоненты репликации

```
┌─────────────────────────────────────────────────────────────┐
│                     ClickHouse Cluster                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │ Replica 1   │    │ Replica 2   │    │ Replica 3   │      │
│  │ (Leader)    │    │             │    │             │      │
│  └─────────────┘    └─────────────┘    └─────────────┘      │
│         │                    │                    │         │
│         └────────────────────┼────────────────────┘         │
│                              │                              │
│                   ┌─────────────┐                            │
│                   │ ZooKeeper   │                            │
│                   │ Cluster     │                            │
│                   │ • Metadata  │                            │
│                   │ • Locks     │                            │
│                   │ • Elections │                            │
│                   └─────────────┘                            │
└─────────────────────────────────────────────────────────────┘
```

### Роли в репликации

#### Leader Replica
- Координирует слияния партиций
- Записывает логи операций в ZooKeeper
- Распределяет задачи между репликами

#### Follower Replicas
- Синхронизируют данные с leader
- Применяют операции из логов
- Могут обслуживать чтение

#### ZooKeeper Cluster
- Хранит метаданные репликации
- Координирует блокировки
- Обеспечивает консистентность

## ZooKeeper для координации

ZooKeeper — обязательный компонент для репликации ClickHouse. Он хранит состояние кластера и координирует действия реплик.

### Установка ZooKeeper

```bash
# На Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y zookeeper zookeeperd

# Настройка кластера (zoo.cfg)
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/var/lib/zookeeper
clientPort=2181
server.1=zoo1:2888:3888
server.2=zoo2:2888:3888
server.3=zoo3:2888:3888

# Запуск
sudo systemctl start zookeeper
sudo systemctl enable zookeeper
```

### Конфигурация ClickHouse для ZooKeeper

```xml
<!-- config.xml -->
<clickhouse>
    <zookeeper>
        <node>
            <host>zoo1</host>
            <port>2181</port>
        </node>
        <node>
            <host>zoo2</host>
            <port>2181</port>
        </node>
        <node>
            <host>zoo3</host>
            <port>2181</port>
        </node>
    </zookeeper>
</clickhouse>
```

### Структура данных в ZooKeeper

```
/clickhouse
├── /tables/{shard}/{database}/{table}
│   ├── /replicas/{replica_name}
│   │   ├── /host
│   │   ├── /port
│   │   ├── /log_pointer
│   │   ├── /queue
│   │   └── /parts
│   └── /leader_election
└── /blocks
```

## ReplicatedMergeTree движок

Основной движок для реплицированных таблиц. Обеспечивает синхронизацию данных между репликами.

### Создание реплицированной таблицы

```sql
CREATE TABLE events_replicated (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/{shard}/events',  -- Путь в ZooKeeper
    '{replica}'                           -- Имя реплики
)
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp)
SETTINGS index_granularity = 8192;
```

### Параметры ReplicatedMergeTree

```sql
ENGINE = ReplicatedMergeTree(
    'zk_path',           -- Путь в ZooKeeper (/clickhouse/tables/01/database/table)
    'replica_name',      -- Уникальное имя реплики (host1, host2, etc.)
    'auxiliary_path'     -- Дополнительный путь (опционально)
)
```

### Создание реплик

```sql
-- На первом узле (shard 01, replica host1)
CREATE TABLE events ON CLUSTER 'cluster_name' (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/01/events',
    'host1'
)
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);

-- На втором узле (shard 01, replica host2)
CREATE TABLE events (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/01/events',
    'host2'
)
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);

-- На третьем узле (shard 01, replica host3)
CREATE TABLE events (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/01/events',
    'host3'
)
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);
```

## Настройка кластера

### Конфигурация кластера

```xml
<!-- config.xml -->
<clickhouse>
    <remote_servers>
        <cluster_name>
            <shard>
                <internal_replication>true</internal_replication>
                <replica>
                    <host>clickhouse-01</host>
                    <port>9000</port>
                </replica>
                <replica>
                    <host>clickhouse-02</host>
                    <port>9000</port>
                </replica>
                <replica>
                    <host>clickhouse-03</host>
                    <port>9000</port>
                </replica>
            </shard>
            <shard>
                <internal_replication>true</internal_replication>
                <replica>
                    <host>clickhouse-04</host>
                    <port>9000</port>
                </replica>
                <replica>
                    <host>clickhouse-05</host>
                    <port>9000</port>
                </replica>
            </shard>
        </cluster_name>
    </remote_servers>
</clickhouse>
```

### Создание таблиц на кластере

```sql
-- DDL запрос на все узлы кластера
CREATE TABLE events ON CLUSTER 'cluster_name' (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/{shard}/events',
    '{replica}'
)
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);
```

### Управление шардами

```sql
-- Просмотр шардов
SELECT *
FROM system.clusters
WHERE cluster = 'cluster_name';

-- Проверка реплик
SELECT
    database,
    table,
    shard_num,
    replica_num,
    host_name,
    host_address,
    port,
    is_local,
    errors_count,
    estimated_recovery_time
FROM system.clusters
WHERE cluster = 'cluster_name';
```

## Распределенные таблицы

Распределенные таблицы позволяют выполнять запросы к данным на всех шардах кластера.

### Создание распределенной таблицы

```sql
-- Сначала создаем локальную реплицированную таблицу
CREATE TABLE events_local ON CLUSTER 'cluster_name' (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/{shard}/events_local',
    '{replica}'
)
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);

-- Создаем распределенную таблицу
CREATE TABLE events ON CLUSTER 'cluster_name' (
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
) ENGINE = Distributed(
    'cluster_name',        -- Имя кластера
    'default',            -- База данных
    'events_local',       -- Локальная таблица
    rand()                -- Функция шардирования
);
```

### Функции шардирования

```sql
-- Случайное распределение
ENGINE = Distributed('cluster', 'db', 'table', rand())

-- По хэшу от user_id
ENGINE = Distributed('cluster', 'db', 'table', sipHash64(user_id))

-- По остатку от деления
ENGINE = Distributed('cluster', 'db', 'table', user_id % 4)

-- Кастомная функция
ENGINE = Distributed('cluster', 'db', 'table', xxHash32(concat(user_id, event_type)))
```

### Запросы к распределенным таблицам

```sql
-- Запрос ко всем шардам
SELECT
    event_type,
    count() as events_count,
    uniq(user_id) as unique_users
FROM events
WHERE timestamp >= today()
GROUP BY event_type
ORDER BY events_count DESC;

-- INSERT в распределенную таблицу (автоматически распределяется)
INSERT INTO events VALUES
(1, 1001, 'login', now(), '{"ip": "192.168.1.1"}'),
(2, 1002, 'purchase', now(), '{"amount": 99.99}');

-- Глобальные агрегаты
SELECT
    sum(events_count) as total_events,
    uniqMerge(unique_users) as total_unique_users
FROM (
    SELECT
        count() as events_count,
        uniqState(user_id) as unique_users
    FROM events_local
    GROUP BY shard_num
);
```

## Отказоустойчивость

### Обработка отказов реплик

```sql
-- Проверка состояния реплик
SELECT
    database,
    table,
    replica_name,
    is_readonly,
    absolute_delay,
    queue_size,
    inserts_in_queue,
    merges_in_queue
FROM system.replicas
WHERE database = 'default';

-- Принудительное восстановление
SYSTEM RESTORE REPLICA events;

-- Проверка логов репликации
SELECT
    event_time,
    level,
    message
FROM system.text_log
WHERE message LIKE '%replica%' OR message LIKE '%merge%'
ORDER BY event_time DESC
LIMIT 20;
```

### Восстановление после сбоев

```sql
-- Остановка репликации (для обслуживания)
SYSTEM STOP REPLICATED SENDS events;

-- Возобновление репликации
SYSTEM START REPLICATED SENDS events;

-- Синхронизация реплики
SYSTEM SYNC REPLICA events;

-- Проверка синхронизации
SELECT
    database,
    table,
    replica_name,
    log_pointer,
    log_max_index
FROM system.replicas;
```

### Мониторинг отказов

```sql
-- Алерт на отставание реплики
SELECT
    database,
    table,
    replica_name,
    absolute_delay,
    (absolute_delay > 300) as is_lagging  -- Более 5 минут
FROM system.replicas
WHERE absolute_delay > 300;

-- Проверка доступности ZooKeeper
SELECT *
FROM system.zookeeper
WHERE path = '/clickhouse';

-- Мониторинг соединений с ZooKeeper
SELECT
    session_id,
    host,
    port,
    is_expired,
    connection_count
FROM system.zookeeper_connection;
```

## Мониторинг кластера

### Метрики репликации

```sql
-- Общее состояние реплик
SELECT
    database,
    table,
    replica_name,
    is_leader,
    is_readonly,
    parts_to_check,
    queue_size,
    inserts_in_queue,
    merges_in_queue,
    log_max_index,
    log_pointer,
    total_replicas,
    active_replicas
FROM system.replicas;

-- Статистика репликации
SELECT
    database,
    table,
    replica_name,
    bytes_sent,
    bytes_received,
    queries_sent,
    queries_received
FROM system.replication_queue;

-- Задержка репликации
SELECT
    database,
    table,
    replica_name,
    absolute_delay,
    relative_delay
FROM system.replicas
ORDER BY absolute_delay DESC;
```

### Мониторинг кластера

```sql
-- Статус кластера
SELECT
    cluster,
    shard_num,
    replica_num,
    host_name,
    host_address,
    port,
    is_local,
    errors_count,
    slowdowns_count,
    estimated_recovery_time
FROM system.clusters
WHERE cluster = 'cluster_name';

-- Распределение данных по шардам
SELECT
    database,
    table,
    shard_num,
    sum(rows) as total_rows,
    sum(bytes) as total_bytes,
    count() as parts_count
FROM system.parts
WHERE active AND database = 'default'
GROUP BY database, table, shard_num
ORDER BY table, shard_num;
```

### Диагностика проблем

```sql
-- Проверка очереди репликации
SELECT
    database,
    table,
    replica_name,
    position,
    node_name,
    type,
    create_time,
    is_currently_executing,
    num_tries,
    last_exception
FROM system.replication_queue
ORDER BY create_time DESC
LIMIT 10;

-- Логи репликации
SELECT
    event_time,
    level,
    logger_name,
    message,
    error
FROM system.text_log
WHERE logger_name LIKE '%replica%'
ORDER BY event_time DESC
LIMIT 50;

-- Статистика слияний
SELECT
    database,
    table,
    count() as merges_count,
    sum(rows_read) as total_rows_read,
    sum(rows_written) as total_rows_written,
    formatReadableSize(sum(bytes_read)) as bytes_read,
    formatReadableSize(sum(bytes_written)) as bytes_written
FROM system.merges
WHERE start_time >= now() - INTERVAL 1 HOUR
GROUP BY database, table;
```

## Best Practices

### Проектирование кластера

1. **Выбор размера шарда**
   ```sql
   -- Маленькие шарды: проще управление, но overhead
   -- Большие шарды: эффективнее, но сложнее восстановление

   -- Рекомендация: 2-4 шарда для начала
   -- Добавлять шарды по мере роста
   ```

2. **Количество реплик**
   ```sql
   -- Минимум: 2 реплики на шард
   -- Рекомендуется: 3 реплики на шард для высокой доступности
   -- Максимум: Ограничено ZooKeeper (обычно 3-5 реплик)
   ```

3. **Распределение данных**
   ```sql
   -- Равномерное распределение по шардам
   -- Избегать hot shards
   -- Мониторить skew данных
   ```

### Конфигурация ZooKeeper

1. **Кластер ZooKeeper**
   ```xml
   <!-- Минимум 3 узла для отказоустойчивости -->
   <zookeeper>
       <node><host>zk1</host><port>2181</port></node>
       <node><host>zk2</host><port>2181</port></node>
       <node><host>zk3</host><port>2181</port></node>
   </zookeeper>
   ```

2. **Настройки таймаутов**
   ```xml
   <!-- Оптимизация для ClickHouse -->
   <zookeeper>
       <session_timeout_ms>30000</session_timeout_ms>
       <operation_timeout_ms>10000</operation_timeout_ms>
   </zookeeper>
   ```

### Оптимизация репликации

1. **Настройки слияния**
   ```sql
   -- Оптимизация для реплицированных таблиц
   SETTINGS
       replicated_max_parallel_sends = 4,
       replicated_max_parallel_fetches = 4,
       max_replicated_merges_in_queue = 8
   ```

2. **Управление очередью**
   ```sql
   -- Мониторинг и оптимизация очереди
   SELECT count() as queue_length
   FROM system.replication_queue
   WHERE create_time < now() - INTERVAL 5 MINUTE;
   ```

### Безопасность

1. **Аутентификация ZooKeeper**
   ```xml
   <zookeeper>
       <identity>clickhouse:user:password</identity>
   </zookeeper>
   ```

2. **Сетевая безопасность**
   ```bash
   # Ограничить доступ к ZooKeeper
   iptables -A INPUT -p tcp --dport 2181 -s clickhouse-nodes -j ACCEPT
   iptables -A INPUT -p tcp --dport 2181 -j DROP
   ```

### Мониторинг и алерты

1. **Ключевые метрики для мониторинга**
   ```sql
   -- Задержка репликации > 5 минут
   -- Размер очереди репликации > 100
   -- Недоступность реплик
   -- Проблемы с ZooKeeper
   ```

2. **Регулярное обслуживание**
   ```sql
   -- Проверка консистентности
   -- Очистка старых логов
   -- Оптимизация таблиц
   -- Обновление ZooKeeper
   ```

## Заключение

Репликация ClickHouse обеспечивает высокую доступность и масштабируемость для аналитических нагрузок. Правильная настройка кластера критически важна для производительности и надежности.

### Ключевые компоненты:

1. **ZooKeeper**: Координация и метаданные
2. **ReplicatedMergeTree**: Реплицированные таблицы
3. **Distributed Engine**: Распределенные запросы
4. **Мониторинг**: Отслеживание состояния кластера

### Преимущества:

- **Автоматическая синхронизация** данных
- **Отказоустойчивость** при сбое узлов
- **Масштабируемость** чтения
- **Горизонтальное масштабирование**

### Следующие темы:

- **Производительность** - тюнинг кластеров
- **Интеграции** - подключение внешних систем
- **Мониторинг** - глубокий анализ метрик

Репликация ClickHouse позволяет создавать надежные и масштабируемые аналитические системы.

## Полезные ссылки

### Официальная документация
- [Replication](https://clickhouse.com/docs/engines/table-engines/mergetree-family/replication)
- [ReplicatedMergeTree](https://clickhouse.com/docs/engines/table-engines/mergetree-family/replicatedmergetree)
- [Distributed DDL](https://clickhouse.com/docs/sql-reference/distributed-ddl)

### Руководства
- [Cluster Setup](https://clickhouse.com/docs/getting-started/cluster-setup)
- [Replication Best Practices](https://clickhouse.com/docs/best-practices/replication)

### Инструменты
- [ClickHouse Keeper](https://clickhouse.com/docs/operations/clickhouse-keeper) - Альтернатива ZooKeeper
- [Cluster Monitoring](https://github.com/ClickHouse/clickhouse-monitoring)

---

**Следующие темы:**
- [Производительность](clickhouse-performance.md)
- [Интеграции и экосистема](clickhouse-integration.md)
