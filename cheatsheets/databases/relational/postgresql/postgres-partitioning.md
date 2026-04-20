---
title: "PostgreSQL: партиционирование"
description: "Комплексное руководство по партиционированию таблиц в PostgreSQL: стратегии, типы партиций, управление, производительность, обслуживание и лучшие практики для больших баз данных"
tags:
  - postgresql
  - database
  - partitioning
  - performance
  - scalability
  - maintenance
  - range
  - list
  - hash
  - pruning
  - sharding
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-design.md"]
next: ["databases/postgres-indexes.md"]
updated: "2026-02-06"
related: ["databases/postgres-basics.md", "databases/postgres-design.md", "databases/postgres-indexes.md", "databases/postgres-admin.md"]
---

# PostgreSQL: партиционирование

Это подробное руководство по партиционированию таблиц в **PostgreSQL** — мощной технике для управления большими объемами данных. Вы узнаете о различных стратегиях партиционирования, их реализации, оптимизации производительности и обслуживании партиционированных таблиц.

## Полезные ссылки

### Официальная документация PostgreSQL

- [PostgreSQL Partitioning](https://www.postgresql.org/docs/)
- [PostgreSQL CREATE TABLE - Partitioning](https://www.postgresql.org/docs/)
- [PostgreSQL Partitioning Best Practices](https://www.postgresql.org/docs/)
- [Partitioning and Constraint Exclusion](https://www.postgresql.org/docs/)

### Дополнительные ресурсы

- [PostgreSQL Partitioning Tutorial](https://www.postgresql.org/docs/)
- [Partitioning Strategies](https://www.postgresql.org/docs/)
- [Partitioning Performance](https://www.postgresql.org/docs/)
- [PG Partition Manager](https://www.postgresql.org/docs/)

См. также: [[postgres-basics]] — [[postgres-design]] — [[postgres-indexes]] — [[postgres-admin]].

## Содержание

- [PostgreSQL: партиционирование](#postgresql-партиционирование)
- [Зачем партиционировать](#зачем-партиционировать)
- [Виды партиционирования](#виды-партиционирования)
- [Создание партиционированной таблицы (RANGE)](#создание-партиционированной-таблицы-range)
- [Индексы на партициях](#индексы-на-партициях)
- [ATTACH/DETACH](#attachdetach)
- [Подсказки](#подсказки)
- [Проверка pruning](#проверка-pruning)
- [Обслуживание партиций](#обслуживание-партиций)
  - [Создание будущих партиций](#создание-будущих-партиций)
  - [Архивирование старых партиций](#архивирование-старых-партиций)
  - [Статистика и VACUUM по партициям](#статистика-и-vacuum-по-партициям)
- [Детальное описание видов партиционирования](#детальное-описание-видов-партиционирования)
  - [RANGE партиционирование](#range-партиционирование)
  - [LIST партиционирование](#list-партиционирование)
  - [HASH партиционирование](#hash-партиционирование)
  - [Композитное партиционирование (Sub-partitioning)](#композитное-партиционирование-sub-partitioning)
- [Оптимизация запросов к партиционированным таблицам](#оптимизация-запросов-к-партиционированным-таблицам)
  - [Partition Pruning (отсечение партиций)](#partition-pruning-отсечение-партиций)
  - [Constraint Exclusion](#constraint-exclusion)
  - [Оптимизация JOIN с партиционированными таблицами](#оптимизация-join-с-партиционированными-таблицами)
  - [Использование индексов на партициях](#использование-индексов-на-партициях)
- [Практические рекомендации](#практические-рекомендации)
  - [Когда использовать партиционирование](#когда-использовать-партиционирование)
  - [Выбор ключа партиционирования](#выбор-ключа-партиционирования)
  - [Размер партиций](#размер-партиций)
  - [Миграция существующих таблиц](#миграция-существующих-таблиц)
  - [Мониторинг партиционированных таблиц](#мониторинг-партиционированных-таблиц)
  - [Типичные проблемы и решения](#типичные-проблемы-и-решения)
- [Продвинутые стратегии партиционирования](#продвинутые-стратегии-партиционирования)
  - [Многоуровневое партиционирование](#многоуровневое-партиционирование)
  - [Партиционирование по нескольким ключам](#партиционирование-по-нескольким-ключам)
  - [Динамическое партиционирование](#динамическое-партиционирование)
- [Автоматизация управления партициями](#автоматизация-управления-партициями)
  - [Автоматическое создание партиций](#автоматическое-создание-партиций)
  - [pg_partman для автоматизации](#pg_partman-для-автоматизации)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Мониторинг производительности партиций](#мониторинг-производительности-партиций)
  - [Оптимизация физического хранения](#оптимизация-физического-хранения)
- [Интеграция с приложениями](#интеграция-с-приложениями)
  - [Spring Boot интеграция](#spring-boot-интеграция)
  - [Hibernate специфические настройки](#hibernate-специфические-настройки)
- [Мониторинг и алертинг](#мониторинг-и-алертинг)
  - [Метрики партиционирования](#метрики-партиционирования)
  - [Интеграция с Prometheus](#интеграция-с-prometheus)
- [prometheus.yml](#prometheusyml)
- [SQL запросы для экспорта метрик партиций](#sql-запросы-для-экспорта-метрик-партиций)
- [Размер каждой партиции](#размер-каждой-партиции)
- [Количество строк в партициях](#количество-строк-в-партициях)
- [Статистика операций](#статистика-операций)
  - [Grafana dashboards](#grafana-dashboards)
- [Лучшие практики и рекомендации](#лучшие-практики-и-рекомендации)
  - [Архитектурные решения](#архитектурные-решения)
  - [Производительность](#производительность)
  - [Безопасность](#безопасность)
  - [Мониторинг и обслуживание](#мониторинг-и-обслуживание)
- [Скрипт для обслуживания партиций](#скрипт-для-обслуживания-партиций)
- [partition_maintenance.sh](#partition_maintenancesh)
- [Заключение](#заключение)
  - [Ключевые стратегии партиционирования:](#ключевые-стратегии-партиционирования)
  - [Лучшие практики:](#лучшие-практики)
  - [Типичные ошибки и их избежание:](#типичные-ошибки-и-их-избежание)
- [Troubleshooting распространенных проблем](#решение-проблем-распространенных-проблем)
  - [Проблема: Partition pruning не работает](#проблема-partition-pruning-не-работает)
  - [Проблема: Неравномерное распределение данных](#проблема-неравномерное-распределение-данных)
  - [Проблема: Медленная вставка в партиционированные таблицы](#проблема-медленная-вставка-в-партиционированные-таблицы)
  - [Проблема: Блокировка партиций при DDL операциях](#проблема-блокировка-партиций-при-ddl-операциях)
- [Реальные примеры использования](#реальные-примеры-использования)
  - [Пример 1: Логи аудита с ротацией](#пример-1-логи-аудита-с-ротацией)
  - [Пример 2: Time-series данные IoT](#пример-2-time-series-данные-iot)
  - [Пример 3: Финансовые транзакции](#пример-3-финансовые-транзакции)
- [Миграция существующих таблиц на партиционирование](#миграция-существующих-таблиц-на-партиционирование)
  - [Онлайн миграция без downtime](#онлайн-миграция-без-downtime)
  - [Миграция с помощью логической репликации](#миграция-с-помощью-логической-репликации)
- [Производительность и масштабируемость](#производительность-и-масштабируемость)
  - [Бенчмаркинг партиционирования](#бенчмаркинг-партиционирования)
  - [Масштабирование партиционирования](#масштабирование-партиционирования)
- [Безопасность и комплаенс](#безопасность-и-комплаенс)
  - [Шифрование партиционированных данных](#шифрование-партиционированных-данных)
  - [Аудит и мониторинг доступа](#аудит-и-мониторинг-доступа)
  - [Ключевые принципы успешного партиционирования:](#ключевые-принципы-успешного-партиционирования)

## Зачем партиционировать
- Очень большие таблицы (десятки/сотни млн строк).
- Ускорить выборки по ключу партиционирования и сократить **VACUUM**/**ANALYZE**.
- Упростить архивирование (detach/drop старых партиций).

## Виды партиционирования
- `RANGE`: по диапазонам (чаще всего по дате/времени).
- `LIST`: по перечислению значений.
- `HASH`: для равномерного распределения при отсутствии хорошего диапазонного ключа.

## Создание партиционированной таблицы (RANGE)

```sql
-- Родительская таблица с партиционированием по диапазону дат и первая партиция
CREATE TABLE events (
  id        BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL,
  payload   JSONB
) PARTITION BY RANGE (created_at);

CREATE TABLE events_2024_q1 PARTITION OF events
  FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');

CREATE TABLE events_2024_q2 PARTITION OF events
  FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');
```

## Индексы на партициях
- **Индексы создаются на каждую партицию отдельно:**
```sql
CREATE INDEX ON events_2024_q1 (created_at);
```
- Для одинаковых индексов на всех партициях используйте `CREATE INDEX `ON` ONLY parent ...` в новых версиях (PostgreSQL 11+ создаёт индексы на дочерних автоматически).

## ATTACH/DETACH
- **Добавить новую партицию:**
```sql
CREATE TABLE events_2024_q3 PARTITION OF events
  FOR VALUES FROM ('2024-07-01') TO ('2024-10-01');
```
- **Отсоединить/архивировать:**
```sql
ALTER TABLE events DETACH PARTITION events_2024_q1;
```

## Подсказки
- Держите диапазоны непересекающимися и без дыр.
- Для **OLTP** чаще всего **RANGE** по времени; **LIST** полезен для «регионов/тенантов».
- Проверяйте `EXPLAIN` — **planner** должен отбрасывать лишние партиции (partition pruning).
- Периодически выполнять **VACUUM** по партициям с активной записью.

## Проверка pruning
```sql
EXPLAIN
SELECT * FROM events
WHERE created_at >= '2024-04-10' AND created_at < '2024-04-11';
```
- В плане должны остаться только релевантные партиции; если нет — проверьте границы партиций и тип данных.

## Обслуживание партиций

### Создание будущих партиций

**Автоматизация создания партиций:**
```sql
-- Создать партицию на следующий квартал
CREATE TABLE events_2024_q4 PARTITION OF events
  FOR VALUES FROM ('2024-10-01') TO ('2025-01-01');

-- Проверить существующие партиции
SELECT schemaname, tablename, partition_name
FROM pg_partitions
WHERE schemaname = 'public' AND tablename = 'events';
```

**Скрипт для автоматического создания партиций:**
```sql
-- Пример функции для создания квартальных партиций
CREATE OR REPLACE FUNCTION create_next_quarter_partition(
  parent_table TEXT,
  start_date DATE
) RETURNS VOID AS $$
DECLARE
  end_date DATE;
  partition_name TEXT;
BEGIN
  end_date := start_date + INTERVAL '3 months';
  partition_name := parent_table || '_' || to_char(start_date, 'YYYY_q') ||
                    EXTRACT(QUARTER FROM start_date);

  EXECUTE format(
    'CREATE TABLE IF NOT EXISTS %I PARTITION OF %I FOR VALUES FROM (%L) TO (%L)',
    partition_name, parent_table, start_date, end_date
  );
END;
$$ LANGUAGE plpgsql;
```

### Архивирование старых партиций

**Процесс архивирования:**

1. **Отсоединить партицию:**
   ```sql
   ALTER TABLE events DETACH PARTITION events_2024_q1;
   ```

2. **Экспортировать данные:**
   ```sql
   -- Вариант 1: COPY TO
   COPY events_2024_q1 TO '/backup/events_2024_q1.csv' WITH CSV HEADER;

   -- Вариант 2: pg_dump
   -- В shell:
   pg_dump -t events_2024_q1 mydb > events_2024_q1.dump
   ```

3. **Удалить партицию (после проверки бэкапа):**
   ```sql
   DROP TABLE events_2024_q1;
   ```

**Проверка перед удалением:**
```sql
-- Проверить размер партиции
SELECT pg_size_pretty(pg_total_relation_size('events_2024_q1')) AS size;

-- Проверить количество строк
SELECT COUNT(*) FROM events_2024_q1;

-- Проверить последнюю дату
SELECT MAX(created_at) FROM events_2024_q1;
```

### Индексы на партициях

**Автоматическое создание индексов (PostgreSQL 11+):**
```sql
-- Создать индекс на родительской таблице
CREATE INDEX idx_events_created_at ON events (created_at);

-- PostgreSQL 11+ автоматически создаст индекс на всех существующих и будущих партициях
```

**Ручное создание индексов (PostgreSQL 10 и ниже):**
```sql
-- Создать индекс на каждой партиции отдельно
CREATE INDEX idx_events_2024_q1_created_at ON events_2024_q1 (created_at);
CREATE INDEX idx_events_2024_q2_created_at ON events_2024_q2 (created_at);
CREATE INDEX idx_events_2024_q3_created_at ON events_2024_q3 (created_at);
```

**Проверка индексов на партициях:**
```sql
-- Список всех индексов на партициях
SELECT
    schemaname,
    tablename AS partition_name,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE tablename LIKE 'events_%'
ORDER BY tablename, indexname;
```

**Создание составных индексов:**
```sql
-- Составной индекс на партициях
CREATE INDEX idx_events_user_created ON events (user_id, created_at);

-- PostgreSQL 11+ создаст на всех партициях автоматически
```

### Статистика и VACUUM по партициям

**Обновление статистики:**
```sql
-- ANALYZE родительской таблицы (обновит статистику для всех партиций)
ANALYZE events;

-- ANALYZE конкретной партиции (для критических партиций)
ANALYZE events_2024_q3;
```

**VACUUM по партициям:**
```sql
-- VACUUM конкретной партиции
VACUUM (ANALYZE) events_2024_q3;

-- VACUUM всех партиций (PostgreSQL обработает каждую отдельно)
VACUUM (ANALYZE) events;
```

**Мониторинг статистики по партициям:**
```sql
SELECT
    schemaname,
    tablename AS partition_name,
    n_live_tup,
    n_dead_tup,
    last_vacuum,
    last_autovacuum,
    last_autoanalyze
FROM pg_stat_user_tables
WHERE tablename LIKE 'events_%'
ORDER BY tablename;
```

## Детальное описание видов партиционирования

### RANGE партиционирование

**RANGE** партиционирование делит таблицу на диапазоны значений ключа партиционирования. Это наиболее часто используемый тип для временных данных.

**Основные характеристики:**
- Данные распределяются по диапазонам значений.
- Каждая партиция имеет диапазон `FROM ... TO`.
- Диапазоны не должны пересекаться, но могут иметь дыры.
- Рекомендуется для временных данных (даты, временные метки).

**Пример `RANGE` по дате:**
```sql
CREATE TABLE orders (
    id BIGSERIAL,
    order_date DATE NOT NULL,
    customer_id INTEGER,
    total_amount NUMERIC(12,2),
    PRIMARY KEY (id, order_date)
) PARTITION BY RANGE (order_date);

-- Партиции по месяцам
CREATE TABLE orders_2024_01 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');
```

**Пример `RANGE` по числу:**
```sql
CREATE TABLE products (
    id BIGSERIAL,
    price NUMERIC(10,2) NOT NULL,
    name TEXT,
    PRIMARY KEY (id, price)
) PARTITION BY RANGE (price);

CREATE TABLE products_low PARTITION OF products
    FOR VALUES FROM (0) TO (100);

CREATE TABLE products_medium PARTITION OF products
    FOR VALUES FROM (100) TO (1000);

CREATE TABLE products_high PARTITION OF products
    FOR VALUES FROM (1000) TO (10000);
```

**Партиция по умолчанию (для всех остальных значений):**
```sql
-- Создать партицию для всех значений, не попадающих в другие партиции
CREATE TABLE orders_default PARTITION OF orders DEFAULT;
```

### LIST партиционирование

**LIST** партиционирование делит таблицу по списку конкретных значений ключа партиционирования.

**Основные характеристики:**
- Данные распределяются по списку значений.
- Каждая партиция содержит конкретные значения ключа.
- Значения в разных партициях не должны пересекаться.
- Рекомендуется для категориальных данных (регионы, статусы, страны).

**Пример `LIST` по региону:**
```sql
CREATE TABLE sales (
    id BIGSERIAL,
    region TEXT NOT NULL,
    sale_date DATE,
    amount NUMERIC(12,2),
    PRIMARY KEY (id, region)
) PARTITION BY LIST (region);

-- Партиции по регионам
CREATE TABLE sales_usa PARTITION OF sales
    FOR VALUES IN ('US', 'USA');

CREATE TABLE sales_europe PARTITION OF sales
    FOR VALUES IN ('UK', 'FR', 'DE', 'IT');

CREATE TABLE sales_asia PARTITION OF sales
    FOR VALUES IN ('CN', 'JP', 'KR', 'IN');
```

**Пример `LIST` по статусу:**
```sql
CREATE TABLE orders (
    id BIGSERIAL,
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ,
    PRIMARY KEY (id, status)
) PARTITION BY LIST (status);

CREATE TABLE orders_pending PARTITION OF orders
    FOR VALUES IN ('pending', 'processing');

CREATE TABLE orders_completed PARTITION OF orders
    FOR VALUES IN ('completed', 'shipped', 'delivered');

CREATE TABLE orders_cancelled PARTITION OF orders
    FOR VALUES IN ('cancelled', 'refunded');
```

**Партиция по умолчанию для `LIST`:**
```sql
-- Для значений, не входящих в списки других партиций
CREATE TABLE sales_other PARTITION OF sales DEFAULT;
```

### HASH партиционирование

**HASH** партиционирование делит таблицу на партиции по хеш-функции от ключа партиционирования.

**Основные характеристики:**
- Данные распределяются равномерно по партициям.
- Количество партиций должно быть указано заранее.
- Данные распределяются по остатку от деления хеша на количество партиций.
- Рекомендуется для равномерного распределения нагрузки.

**Пример `HASH` партиционирования:**
```sql
CREATE TABLE users (
    id BIGSERIAL,
    username TEXT,
    email TEXT,
    PRIMARY KEY (id)
) PARTITION BY HASH (id);

-- Создать 4 партиции
CREATE TABLE users_0 PARTITION OF users
    FOR VALUES WITH (modulus 4, remainder 0);

CREATE TABLE users_1 PARTITION OF users
    FOR VALUES WITH (modulus 4, remainder 1);

CREATE TABLE users_2 PARTITION OF users
    FOR VALUES WITH (modulus 4, remainder 2);

CREATE TABLE users_3 PARTITION OF users
    FOR VALUES WITH (modulus 4, remainder 3);
```

**Увеличение количества партиций при `HASH`:**
**HASH** партиционирование требует пересоздания таблицы для изменения количества партиций. Это ограничение, которое следует учитывать при проектировании.

### Композитное партиционирование (Sub-partitioning)

**PostgreSQL** поддерживает создание подпартиций — партиции партиций.

**Пример композитного партиционирования:**
```sql
-- Родительская таблица по RANGE (по году)
CREATE TABLE events (
    id BIGSERIAL,
    event_date DATE NOT NULL,
    event_type TEXT NOT NULL,
    payload JSONB,
    PRIMARY KEY (id, event_date, event_type)
) PARTITION BY RANGE (event_date);

-- Партиция по году, подпартиционированная по LIST (по типу события)
CREATE TABLE events_2024 PARTITION OF events
    FOR VALUES FROM ('2024-01-01') TO ('2025-01-01')
    PARTITION BY LIST (event_type);

CREATE TABLE events_2024_user_actions PARTITION OF events_2024
    FOR VALUES IN ('login', 'logout', 'signup');

CREATE TABLE events_2024_payments PARTITION OF events_2024
    FOR VALUES IN ('payment', 'refund', 'chargeback');

CREATE TABLE events_2024_other PARTITION OF events_2024 DEFAULT;
```

## Оптимизация запросов к партиционированным таблицам

### Partition Pruning (отсечение партиций)

**Partition pruning** — это оптимизация, при которой планировщик исключает из плана выполнения партиции, которые не содержат релевантных данных.

**Пример эффективного pruning:**
```sql
-- Запрос обращается только к одной партиции
EXPLAIN SELECT * FROM events
WHERE created_at >= '2024-04-10' AND created_at < '2024-04-11';

-- В плане должно быть:
-- Seq Scan on events_2024_q2  (только одна партиция, не все!)
```

**Условия эффективного pruning:**
- Условие фильтрации должно использовать ключ партиционирования.
- Условие должно быть простым (сравнение, `BETWEEN`, IN).
- Тип данных в условии должен соответствовать типу ключа партиционирования.

**Примеры запросов с эффективным pruning:**
```sql
-- Для RANGE партиционирования
SELECT * FROM events WHERE created_at = '2024-04-15';
SELECT * FROM events WHERE created_at BETWEEN '2024-04-01' AND '2024-04-30';
SELECT * FROM events WHERE created_at >= '2024-04-01' AND created_at < '2024-05-01';

-- Для LIST партиционирования
SELECT * FROM sales WHERE region = 'US';
SELECT * FROM sales WHERE region IN ('US', 'UK');

-- Для HASH партиционирования
SELECT * FROM users WHERE id = 12345;  -- id — ключ партиционирования
```

**Примеры запросов БЕЗ эффективного pruning:**
```sql
-- Функции на ключе партиционирования могут препятствовать pruning
SELECT * FROM events WHERE EXTRACT(MONTH FROM created_at) = 4;  -- НЕ эффективно!
SELECT * FROM events WHERE created_at::text LIKE '2024-04%';    -- НЕ эффективно!

-- Лучше переписать:
SELECT * FROM events
WHERE created_at >= '2024-04-01' AND created_at < '2024-05-01';
```

### Constraint Exclusion

**PostgreSQL** использует **constraint exclusion** для исключения партиций, которые не могут содержать релевантные данные.

**Проверка constraint exclusion:**
```sql
-- Включить constraint exclusion (включено по умолчанию)
SET constraint_exclusion = partition;

-- Проверить, какие партиции проверяются
EXPLAIN SELECT * FROM events WHERE created_at = '2024-04-15';
```

### Оптимизация JOIN с партиционированными таблицами

**JOIN с партиционированными таблицами:**
```sql
-- PostgreSQL может выполнить partition-wise join, если обе таблицы партиционированы одинаково
SELECT e.*, u.username
FROM events e
JOIN users u ON e.user_id = u.id
WHERE e.created_at >= '2024-04-01' AND e.created_at < '2024-05-01';

-- Если обе таблицы партиционированы по created_at, PostgreSQL может выполнить JOIN для каждой пары партиций отдельно
```

### Использование индексов на партициях

**Индексы на ключе партиционирования:**
```sql
-- Индекс на ключе партиционирования обычно не нужен для pruning, но может ускорить поиск внутри партиции
CREATE INDEX idx_events_created_at ON events (created_at);

-- Индексы на других столбцах
CREATE INDEX idx_events_user_id ON events (user_id);
CREATE INDEX idx_events_status ON events (status);
```

**Частичные индексы на партициях:**
```sql
-- Создать частичный индекс только для активных записей
CREATE INDEX idx_events_active ON events (user_id)
WHERE status = 'active';
```

## Практические рекомендации

### Когда использовать партиционирование

**Хорошие кандидаты для партиционирования:**
- Таблицы с десятками/сотнями миллионов строк.
- Таблицы с временными данными (логи, события, метрики).
- Таблицы с естественным ключом партиционирования (дата, регион, тип).
- Таблицы, требующие регулярного архивирования старых данных.

**Плохие кандидаты:**
- Маленькие таблицы (< 1 млн строк).
- Таблицы без естественного ключа партиционирования.
- Таблицы с часто изменяемой структурой.
- Таблицы, где большинство запросов обращаются ко всем партициям.

### Выбор ключа партиционирования

**Критерии выбора ключа:**
- Часто используется в **WHERE** для фильтрации.
- Позволяет эффективный **partition pruning**.
- Имеет ограниченное количество значений (для LIST) или четкие диапазоны (для RANGE).
- Не изменяется часто (UPDATE ключа партиционирования может переместить строку между партициями).

**Примеры хороших ключей:**
- Дата создания/время события (RANGE).
- Регион, страна (LIST).
- Идентификатор пользователя с равномерным распределением (HASH).
- Год/квартал/месяц (RANGE).

**Примеры плохих ключей:**
- Случайные значения (UUID без префикса).
- Значения, изменяющиеся часто.
- Столбцы, не используемые в фильтрах.

### Размер партиций

**Рекомендации по размеру партиций:**
- Для **RANGE** по времени: месячные или квартальные партиции.
- Для **LIST**: логически связанные группы значений.
- Для **HASH**: равномерное распределение (обычно 4-16 партиций).
- Размер партиции: от 1 млн до `100` млн строк (зависит от размера строк).

**Проверка размера партиций:**
```sql
SELECT
    schemaname,
    tablename AS partition_name,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size,
    n_live_tup AS row_count
FROM pg_stat_user_tables
WHERE tablename LIKE 'events_%'
ORDER BY tablename;
```

### Миграция существующих таблиц

**Процесс миграции:**
1. Создать партиционированную таблицу.
2. **Скопировать данные из старой таблицы:**
   ```sql
   INSERT INTO events_new SELECT * FROM events_old;
   ```
3. Создать индексы и ограничения.
4. **Заменить старую таблицу:**
   ```sql
   BEGIN;
   ALTER TABLE events_old RENAME TO events_old_backup;
   ALTER TABLE events_new RENAME TO events;
   COMMIT;
   ```

**Онлайн-миграция (с минимальным downtime):**
- Использовать логическую репликацию.
- Или использовать **pglogical** для синхронизации.

### Мониторинг партиционированных таблиц

**Статистика по партициям:**
```sql
-- Размеры партиций
SELECT
    schemaname,
    tablename AS partition_name,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) -
                   pg_relation_size(schemaname||'.'||tablename)) AS index_size,
    n_live_tup AS row_count,
    n_dead_tup AS dead_rows
FROM pg_stat_user_tables
WHERE tablename LIKE 'events_%'
ORDER BY tablename;
```

**Использование партиций в запросах:**
```sql
-- Проверить, какие партиции используются в запросах
SELECT
    schemaname,
    tablename AS partition_name,
    seq_scan,
    idx_scan,
    n_tup_ins AS inserts,
    n_tup_upd AS updates,
    n_tup_del AS deletes
FROM pg_stat_user_tables
WHERE tablename LIKE 'events_%'
ORDER BY tablename;
```

### Типичные проблемы и решения

**Проблема 1: Отсутствие partition pruning**
- **Решение:** Убедитесь, что условие использует ключ партиционирования напрямую, без функций.

**Проблема 2: Дыры в диапазонах партиций**
- **Решение:** Создайте партицию по умолчанию или заполните все диапазоны.

**Проблема 3: Неравномерное распределение данных**
- **Решение:** Для **HASH** увеличьте количество партиций или используйте другой ключ.

**Проблема 4: Медленные запросы к партиционированным таблицам**
- **Решение:** Проверьте **partition pruning** в **EXPLAIN**, добавьте индексы на нужные столбцы.

**Проблема 5: `UPDATE` ключа партиционирования**
- **Решение: UPDATE** ключа может переместить строку между партициями, что медленно. Избегайте обновления ключа или используйте **DELETE** + **INSERT**.

## Продвинутые стратегии партиционирования

### Многоуровневое партиционирование

**PostgreSQL** поддерживает субпартиционирование (sub-partitioning), позволяющее создавать иерархические структуры партиций.

```sql
-- Создание многоуровневой партиционированной таблицы
-- Уровень 1: по году (RANGE)
-- Уровень 2: по месяцу (RANGE)
CREATE TABLE sales_data (
    id SERIAL,
    sale_date DATE NOT NULL,
    region_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL
) PARTITION BY RANGE (EXTRACT(YEAR FROM sale_date));

-- Создание субпартиций
CREATE TABLE sales_2023 PARTITION OF sales_data
    FOR VALUES FROM (2023) TO (2024)
    PARTITION BY RANGE (EXTRACT(MONTH FROM sale_date));

CREATE TABLE sales_2024 PARTITION OF sales_data
    FOR VALUES FROM (2024) TO (2025)
    PARTITION BY RANGE (EXTRACT(MONTH FROM sale_date));

-- Создание месячных субпартиций для 2023
CREATE TABLE sales_2023_01 PARTITION OF sales_2023
    FOR VALUES FROM (1) TO (2);

CREATE TABLE sales_2023_02 PARTITION OF sales_2023
    FOR VALUES FROM (2) TO (3);

-- Аналогично для остальных месяцев...

-- Создание месячных субпартиций для 2024
CREATE TABLE sales_2024_01 PARTITION OF sales_2024
    FOR VALUES FROM (1) TO (2);

-- Запросы автоматически используют pruning на всех уровнях
SELECT * FROM sales_data
WHERE sale_date >= '2024-01-01' AND sale_date < '2024-02-01';
-- Будет обращаться только к sales_2024_01
```

### Партиционирование по нескольким ключам

```sql
-- Партиционирование по составному ключу
CREATE TABLE user_events (
    user_id INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_time TIMESTAMP NOT NULL,
    event_data JSONB
) PARTITION BY RANGE (user_id, event_time);

-- Создание партиций
CREATE TABLE user_events_1_2024 PARTITION OF user_events
    FOR VALUES FROM (1, '2024-01-01') TO (1000, '2024-02-01');

CREATE TABLE user_events_1_2024_q2 PARTITION OF user_events
    FOR VALUES FROM (1, '2024-04-01') TO (1000, '2024-07-01');

-- Это позволяет эффективно фильтровать по user_id и времени
SELECT * FROM user_events
WHERE user_id BETWEEN 1 AND 1000
  AND event_time >= '2024-04-01' AND event_time < '2024-07-01';
```

### Динамическое партиционирование

```sql
-- Функция для создания партиций на лету
CREATE OR REPLACE FUNCTION create_partition_if_not_exists(
    table_name TEXT,
    partition_name TEXT,
    start_date DATE,
    end_date DATE
) RETURNS void AS $$
DECLARE
    partition_exists BOOLEAN;
BEGIN
    -- Проверяем, существует ли партиция
    SELECT EXISTS (
        SELECT 1 FROM pg_inherits i
        JOIN pg_class c ON i.inhrelid = c.oid
        WHERE c.relname = partition_name
    ) INTO partition_exists;

    IF NOT partition_exists THEN
        -- Создаем партицию
        EXECUTE format(
            'CREATE TABLE %I PARTITION OF %I FOR VALUES FROM (%L) TO (%L)',
            partition_name, table_name, start_date, end_date
        );

        -- Создаем индексы на партиции
        EXECUTE format(
            'CREATE INDEX %I ON %I (created_at)',
            partition_name || '_created_at_idx', partition_name
        );

        RAISE NOTICE 'Created partition: %', partition_name;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Функция для автоматического создания партиций по месяцам
CREATE OR REPLACE FUNCTION ensure_monthly_partitions(
    base_table TEXT,
    for_date DATE DEFAULT CURRENT_DATE
) RETURNS void AS $$
DECLARE
    year_month TEXT;
    partition_name TEXT;
    start_date DATE;
    end_date DATE;
BEGIN
    -- Определяем границы месяца
    start_date := date_trunc('month', for_date);
    end_date := start_date + INTERVAL '1 month';
    year_month := to_char(for_date, 'YYYY_MM');
    partition_name := base_table || '_' || year_month;

    -- Создаем партицию если нужно
    PERFORM create_partition_if_not_exists(base_table, partition_name, start_date, end_date);

    -- Также создаем партицию для следующего месяца
    start_date := end_date;
    end_date := start_date + INTERVAL '1 month';
    year_month := to_char(start_date, 'YYYY_MM');
    partition_name := base_table || '_' || year_month;

    PERFORM create_partition_if_not_exists(base_table, partition_name, start_date, end_date);
END;
$$ LANGUAGE plpgsql;

-- Использование
SELECT ensure_monthly_partitions('events', CURRENT_DATE);
```

## Автоматизация управления партициями

### Автоматическое создание партиций

```sql
-- Создание функции для автоматического создания партиций
CREATE OR REPLACE FUNCTION create_time_partitions(
    base_table TEXT,
    partition_column TEXT,
    interval_unit TEXT,  -- 'month', 'day', 'week', etc.
    partitions_ahead INTEGER DEFAULT 3
) RETURNS void AS $$
DECLARE
    partition_date DATE;
    partition_name TEXT;
    start_date DATE;
    end_date DATE;
    interval_sql TEXT;
BEGIN
    -- Определяем интервал
    CASE interval_unit
        WHEN 'day' THEN interval_sql := '1 day';
        WHEN 'week' THEN interval_sql := '1 week';
        WHEN 'month' THEN interval_sql := '1 month';
        WHEN 'quarter' THEN interval_sql := '3 months';
        WHEN 'year' THEN interval_sql := '1 year';
        ELSE RAISE EXCEPTION 'Unsupported interval unit: %', interval_unit;
    END CASE;

    -- Создаем партиции на будущее
    FOR i IN 0..partitions_ahead-1 LOOP
        partition_date := CURRENT_DATE + (i || ' ' || interval_unit)::INTERVAL;
        start_date := date_trunc(interval_unit, partition_date);
        end_date := start_date + interval_sql::INTERVAL;

        partition_name := base_table || '_' || to_char(start_date, 'YYYY_MM_DD');

        -- Создаем партицию
        BEGIN
            EXECUTE format(
                'CREATE TABLE IF NOT EXISTS %I PARTITION OF %I FOR VALUES FROM (%L) TO (%L)',
                partition_name, base_table, start_date, end_date
            );

            -- Создаем индексы
            EXECUTE format(
                'CREATE INDEX IF NOT EXISTS %I ON %I (%I)',
                partition_name || '_idx', partition_name, partition_column
            );

            RAISE NOTICE 'Created partition: % for range % to %', partition_name, start_date, end_date;
        EXCEPTION
            WHEN duplicate_table THEN
                -- Партиция уже существует, пропускаем
                RAISE NOTICE 'Partition % already exists', partition_name;
        END;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Создание функции для автоматической очистки старых партиций
CREATE OR REPLACE FUNCTION drop_old_partitions(
    base_table TEXT,
    retention_period INTERVAL DEFAULT '1 year'
) RETURNS void AS $$
DECLARE
    partition_record RECORD;
    cutoff_date DATE;
BEGIN
    cutoff_date := CURRENT_DATE - retention_period;

    -- Находим и удаляем старые партиции
    FOR partition_record IN
        SELECT
            c.relname as partition_name,
            pg_get_expr(p.partstrat, p.partrelid) as partition_expr
        FROM pg_inherits i
        JOIN pg_class c ON i.inhrelid = c.oid
        JOIN pg_partitioned_table p ON p.partrelid = i.inhparent
        WHERE i.inhparent = (SELECT oid FROM pg_class WHERE relname = base_table)
          AND c.relname LIKE base_table || '_%'
    LOOP
        -- Проверяем, является ли партиция старой
        IF partition_record.partition_expr ~ 'from \((.*?)\)' THEN
            -- Извлекаем дату из выражения партиции (упрощенная логика)
            -- В реальности нужно более сложное парсинг

            BEGIN
                -- Отключаем партицию перед удалением
                EXECUTE format('ALTER TABLE %I DETACH PARTITION %I',
                    base_table, partition_record.partition_name);

                -- Удаляем партицию
                EXECUTE format('DROP TABLE %I', partition_record.partition_name);

                RAISE NOTICE 'Dropped old partition: %', partition_record.partition_name;
            EXCEPTION
                WHEN OTHERS THEN
                    RAISE WARNING 'Failed to drop partition %: %',
                        partition_record.partition_name, SQLERRM;
            END;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Настройка автоматического обслуживания
-- Создание задания для cron или pg_cron
SELECT cron.schedule(
    'create-partitions',
    '0 2 * * *',  -- Каждый день в 2:00
    'SELECT create_time_partitions(''events'', ''created_at'', ''month'', 3);'
);

SELECT cron.schedule(
    'cleanup-old-partitions',
    '0 3 1 * *',  -- Первое число каждого месяца в 3:00
    'SELECT drop_old_partitions(''events'', ''2 years''::INTERVAL);'
);
```

### pg_partman для автоматизации

```sql
-- Установка pg_partman
CREATE EXTENSION pg_partman;

-- Создание партиционированной таблицы с помощью pg_partman
SELECT partman.create_parent(
    p_parent_table => 'public.events',
    p_control => 'created_at',
    p_type => 'native',
    p_interval => 'monthly',
    p_premake => 3,  -- Создавать 3 партиции вперед
    p_start_partition => '2024-01-01'::timestamp
);

-- Настройка автоматического обслуживания
UPDATE partman.part_config
SET infinite_time_partitions = true,
    retention = '2 years',
    retention_keep_table = false
WHERE parent_table = 'public.events';

-- Ручное создание недостающих партиций
SELECT partman.run_maintenance(p_job_name => 'events');

-- Проверка статуса партиций
SELECT * FROM partman.check_default();

-- Просмотр информации о партициях
SELECT * FROM partman.show_partitions('public.events');

-- Анализ размера партиций
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM partman.show_partitions('public.events') p
JOIN pg_tables t ON t.tablename = p.partitionname
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## Производительность и оптимизация

### Оптимизация запросов к партиционированным таблицам

```sql
-- Создание индексов на партициях
CREATE INDEX CONCURRENTLY idx_events_created_at ON events (created_at);
CREATE INDEX CONCURRENTLY idx_events_user_id ON events (user_id);

-- Для каждой партиции будут созданы соответствующие индексы
-- Но можно создать их вручную для контроля

-- Оптимизация для часто используемых запросов
CREATE OR REPLACE VIEW recent_events AS
SELECT * FROM events
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
WITH LOCAL CHECK OPTION;

-- Использование частичных индексов на партициях
-- (автоматически создается для каждой партиции)
ALTER TABLE events ADD CONSTRAINT chk_recent_events
    CHECK (created_at >= CURRENT_DATE - INTERVAL '1 year');

-- Оптимизация агрегационных запросов
CREATE MATERIALIZED VIEW monthly_stats AS
SELECT
    date_trunc('month', created_at) as month,
    count(*) as total_events,
    count(DISTINCT user_id) as unique_users,
    avg(event_data->>'duration')::float as avg_duration
FROM events
WHERE created_at >= CURRENT_DATE - INTERVAL '1 year'
GROUP BY date_trunc('month', created_at)
ORDER BY month DESC;

-- Обновление материализованного представления
REFRESH MATERIALIZED VIEW CONCURRENTLY monthly_stats;
```

### Мониторинг производительности партиций

```sql
-- Статистика по партициям
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) as table_size,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) -
                   pg_relation_size(schemaname||'.'||tablename)) as index_size,
    n_tup_ins as inserts,
    n_tup_upd as updates,
    n_tup_del as deletes,
    n_live_tup as live_rows,
    n_dead_tup as dead_rows,
    CASE WHEN n_live_tup > 0 THEN
        round((n_dead_tup::float / n_live_tup) * 100, 2)
    ELSE 0 END as bloat_ratio
FROM pg_stat_user_tables
WHERE schemaname = 'public'
  AND tablename LIKE 'events_%'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Анализ эффективности партиционирования
SELECT
    'Partition pruning' as check_type,
    CASE
        WHEN count(*) > 0 THEN 'WORKING'
        ELSE 'NOT WORKING'
    END as status,
    string_agg(tablename, ', ') as partitions_used
FROM pg_stat_user_tables
WHERE schemaname = 'public'
  AND tablename LIKE 'events_%'
  AND (last_vacuum IS NOT NULL OR last_autovacuum IS NOT NULL);

-- Проверка на наличие "дыр" в партициях
WITH partition_ranges AS (
    SELECT
        tablename,
        (pg_get_expr(c.relpartbound, c.oid)) as partition_expr
    FROM pg_inherits i
    JOIN pg_class c ON i.inhrelid = c.oid
    WHERE i.inhparent = (SELECT oid FROM pg_class WHERE relname = 'events')
)
SELECT
    'Partition gaps' as check_type,
    CASE
        WHEN count(*) = 0 THEN 'NO GAPS'
        ELSE 'GAPS FOUND'
    END as status,
    string_agg(tablename, ', ') as problematic_partitions
FROM partition_ranges
WHERE partition_expr IS NULL OR partition_expr = '';

-- Мониторинг запросов к партициям
SELECT
    query,
    calls,
    total_time / 1000 as total_time_sec,
    mean_time / 1000 as mean_time_sec,
    rows,
    shared_blks_hit,
    shared_blks_read
FROM pg_stat_statements
WHERE query LIKE '%events%'
  AND query NOT LIKE '%pg_stat_statements%'
ORDER BY total_time DESC
LIMIT 10;
```

### Оптимизация физического хранения

```sql
-- Настройка хранения для партиций
ALTER TABLE events_2024_01 SET (
    autovacuum_vacuum_scale_factor = 0.1,
    autovacuum_analyze_scale_factor = 0.05,
    autovacuum_vacuum_threshold = 1000,
    autovacuum_analyze_threshold = 500
);

-- Установка таблиц в read-only режим для архивных партиций
ALTER TABLE events_2023_01 SET (
    autovacuum_enabled = false
);

-- Сжатие старых партиций (требует PostgreSQL 14+)
ALTER TABLE events_2023_01 SET (
    compress = 'pglz'
);

-- Оптимизация fillfactor для часто обновляемых партиций
ALTER TABLE events_current SET (
    fillfactor = 70
);

-- Настройка хранения для конкретных партиций
ALTER TABLE events_2024_01 SET (
    toast_tuple_target = 128  -- Оптимизация для больших JSON
);
```

## Интеграция с приложениями

### Spring Boot интеграция

```java
@Configuration
public class PartitioningConfig {

    @Bean
    public PartitionManager partitionManager(DataSource dataSource) {
        return new PartitionManager(dataSource);
    }

    @Bean
    public PartitionAwareRoutingDataSource dataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("replicaDataSource") DataSource replicaDataSource) {

        PartitionAwareRoutingDataSource routingDataSource = new PartitionAwareRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("replica", replicaDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }
}

@Service
public class PartitionManager {

    private final JdbcTemplate jdbcTemplate;

    public PartitionManager(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createMonthlyPartition(String tableName, LocalDate date) {
        String partitionName = tableName + "_" + date.format(DateTimeFormatter.ofPattern("yyyy_MM"));
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1);

        String sql = String.format(
            "CREATE TABLE IF NOT EXISTS %s PARTITION OF %s " +
            "FOR VALUES FROM ('%s') TO ('%s')",
            partitionName, tableName,
            startDate.toString(), endDate.toString()
        );

        jdbcTemplate.execute(sql);

        // Создание индексов
        createPartitionIndexes(partitionName);
    }

    private void createPartitionIndexes(String partitionName) {
        List<String> indexStatements = Arrays.asList(
            String.format("CREATE INDEX IF NOT EXISTS idx_%s_created_at ON %s (created_at)", partitionName, partitionName),
            String.format("CREATE INDEX IF NOT EXISTS idx_%s_user_id ON %s (user_id)", partitionName, partitionName)
        );

        for (String indexSql : indexStatements) {
            try {
                jdbcTemplate.execute(indexSql);
            } catch (Exception e) {
                // Логируем ошибку, но продолжаем
                System.err.println("Failed to create index: " + e.getMessage());
            }
        }
    }

    public void ensurePartitionsExist(String tableName, int monthsAhead) {
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < monthsAhead; i++) {
            LocalDate partitionDate = currentDate.plusMonths(i);
            createMonthlyPartition(tableName, partitionDate);
        }
    }

    public void dropOldPartitions(String tableName, int retainMonths) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(retainMonths);

        String findPartitionsSql = String.format(
            "SELECT tablename FROM pg_tables " +
            "WHERE tablename LIKE '%s_%%' " +
            "AND tablename ~ '%s_[0-9]{4}_[0-9]{2}'",
            tableName, tableName
        );

        List<String> partitions = jdbcTemplate.queryForList(findPartitionsSql, String.class);

        for (String partition : partitions) {
            // Извлекаем дату из имени партиции
            String dateStr = partition.substring(tableName.length() + 1);
            LocalDate partitionDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy_MM"));

            if (partitionDate.isBefore(cutoffDate)) {
                try {
                    // Сначала отключаем партицию
                    jdbcTemplate.execute(String.format(
                        "ALTER TABLE %s DETACH PARTITION %s", tableName, partition
                    ));

                    // Затем удаляем
                    jdbcTemplate.execute(String.format("DROP TABLE %s", partition));

                    System.out.println("Dropped old partition: " + partition);
                } catch (Exception e) {
                    System.err.println("Failed to drop partition " + partition + ": " + e.getMessage());
                }
            }
        }
    }
}

// Репозиторий с поддержкой партиций
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.createdAt >= :startDate AND e.createdAt < :endDate")
    List<Event> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.createdAt >= :startDate")
    long countEventsSince(@Param("startDate") LocalDateTime startDate);

    @Modifying
    @Query("DELETE FROM Event e WHERE e.createdAt < :cutoffDate")
    void deleteOldEvents(@Param("cutoffDate") LocalDateTime cutoffDate);
}

// Сервис для работы с партициями
@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PartitionManager partitionManager;

    @PostConstruct
    public void init() {
        // Создаем партиции на 3 месяца вперед
        partitionManager.ensurePartitionsExist("events", 3);
    }

    @Scheduled(cron = "0 0 1 * *") // Каждый месяц
    public void maintainPartitions() {
        // Создаем новые партиции
        partitionManager.ensurePartitionsExist("events", 3);

        // Удаляем старые партиции (старше 2 лет)
        partitionManager.dropOldPartitions("events", 24);
    }

    public List<Event> getEventsInDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return eventRepository.findByDateRange(start, end);
    }

    @Scheduled(cron = "0 30 2 * *") // Каждый день в 2:30
    @Transactional
    public void cleanupOldEvents() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(2);

        long deletedCount = eventRepository.countEventsSince(cutoffDate);
        if (deletedCount > 0) {
            eventRepository.deleteOldEvents(cutoffDate);
            System.out.println("Cleaned up " + deletedCount + " old events");
        }
    }
}
```

### Hibernate специфические настройки

```java
@Configuration
public class HibernatePartitionConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(jpaVendorAdapter);
        factory.setPackagesToScan("com.example.entity");

        Properties jpaProperties = new Properties();
        // Настройки для партиционированных таблиц
        jpaProperties.setProperty("hibernate.jdbc.batch_size", "50");
        jpaProperties.setProperty("hibernate.order_inserts", "true");
        jpaProperties.setProperty("hibernate.order_updates", "true");
        jpaProperties.setProperty("hibernate.jdbc.batch_versioned_data", "true");

        // Отключаем автоматическое создание DDL для партиций
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "validate");

        factory.setJpaProperties(jpaProperties);

        return factory;
    }
}

// Entity с поддержкой партиций
@Entity
@Table(name = "events")
@org.hibernate.annotations.Table(appliesTo = "events", comment = "Partitioned events table")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_data")
    private String eventData; // Или JsonNode для сложных структур

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    // Геттеры и сеттеры
}
```

## Мониторинг и алертинг

### Метрики партиционирования

```sql
-- Создание представления для мониторинга партиций
CREATE OR REPLACE VIEW partition_monitoring AS
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) as table_size,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) -
                   pg_relation_size(schemaname||'.'||tablename)) as index_size,
    n_tup_ins as inserts,
    n_tup_upd as updates,
    n_tup_del as deletes,
    n_live_tup as live_rows,
    n_dead_tup as dead_rows,
    last_vacuum,
    last_autovacuum,
    CASE
        WHEN n_live_tup > 0 THEN round((n_dead_tup::float / n_live_tup) * 100, 2)
        ELSE 0
    END as bloat_ratio,
    CASE
        WHEN last_vacuum IS NULL AND last_autovacuum IS NULL THEN 'NEVER'
        WHEN last_autovacuum > last_vacuum OR last_vacuum IS NULL THEN
            extract(epoch from now() - last_autovacuum)/86400 || ' days ago'
        ELSE
            extract(epoch from now() - last_vacuum)/86400 || ' days ago'
    END as last_maintenance
FROM pg_stat_user_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Мониторинг эффективности партиционирования
CREATE OR REPLACE VIEW partition_effectiveness AS
WITH partition_stats AS (
    SELECT
        schemaname,
        tablename,
        n_tup_ins + n_tup_upd + n_tup_del as total_operations,
        n_live_tup,
        pg_total_relation_size(schemaname||'.'||tablename) as total_bytes
    FROM pg_stat_user_tables
    WHERE schemaname = 'public'
      AND tablename LIKE 'events_%'
),
total_stats AS (
    SELECT
        sum(total_operations) as total_ops,
        sum(n_live_tup) as total_rows,
        sum(total_bytes) as total_bytes
    FROM partition_stats
),
partition_ratios AS (
    SELECT
        schemaname,
        tablename,
        total_operations::float / NULLIF((SELECT total_ops FROM total_stats), 0) as ops_ratio,
        n_live_tup::float / NULLIF((SELECT total_rows FROM total_stats), 0) as rows_ratio,
        total_bytes::float / NULLIF((SELECT total_bytes FROM total_stats), 0) as size_ratio
    FROM partition_stats
)
SELECT
    schemaname,
    tablename,
    round(ops_ratio * 100, 2) as operations_percent,
    round(rows_ratio * 100, 2) as rows_percent,
    round(size_ratio * 100, 2) as size_percent,
    CASE
        WHEN abs(ops_ratio - rows_ratio) > 0.1 THEN 'UNEVEN_LOAD'
        ELSE 'BALANCED'
    END as load_balance_status,
    CASE
        WHEN size_ratio > 0.2 THEN 'LARGE_PARTITION'
        ELSE 'NORMAL'
    END as size_status
FROM partition_ratios
ORDER BY ops_ratio DESC;

-- Функция для генерации алертов
CREATE OR REPLACE FUNCTION check_partition_health()
RETURNS TABLE (
    alert_level TEXT,
    alert_type TEXT,
    partition_name TEXT,
    description TEXT
) AS $$
BEGIN
    -- Проверка на переполненные партиции
    RETURN QUERY
    SELECT
        'WARNING'::TEXT,
        'LARGE_PARTITION'::TEXT,
        schemaname || '.' || tablename,
        'Partition size: ' || pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename))
    FROM pg_stat_user_tables
    WHERE schemaname = 'public'
      AND tablename LIKE 'events_%'
      AND pg_total_relation_size(schemaname||'.'||tablename) > 10 * 1024 * 1024 * 1024; -- 10GB

    -- Проверка на несбалансированные партиции
    RETURN QUERY
    SELECT
        'INFO'::TEXT,
        'UNBALANCED_PARTITION'::TEXT,
        pr.tablename,
        'Operations ratio: ' || round(ops_ratio * 100, 2) || '%'
    FROM partition_effectiveness pr
    WHERE load_balance_status = 'UNEVEN_LOAD';

    -- Проверка на отсутствие обслуживания
    RETURN QUERY
    SELECT
        'ERROR'::TEXT,
        'NO_MAINTENANCE'::TEXT,
        schemaname || '.' || tablename,
        'Last maintenance: ' || last_maintenance
    FROM partition_monitoring
    WHERE last_maintenance = 'NEVER'
       OR last_maintenance::interval > interval '7 days';

END;
$$ LANGUAGE plpgsql;

-- Регулярная проверка здоровья партиций
SELECT * FROM check_partition_health();
```

### Интеграция с Prometheus

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'postgresql-partitions'
    static_configs:
      - targets: ['postgres-exporter:9187']
    metrics_path: '/metrics'

# SQL запросы для экспорта метрик партиций
# Размер каждой партиции
SELECT
    schemaname || '.' || tablename as partition_name,
    pg_total_relation_size(schemaname||'.'||tablename) as size_bytes
FROM pg_stat_user_tables
WHERE schemaname = 'public' AND tablename LIKE 'events_%';

# Количество строк в партициях
SELECT
    schemaname || '.' || tablename as partition_name,
    n_live_tup as live_rows,
    n_dead_tup as dead_rows
FROM pg_stat_user_tables
WHERE schemaname = 'public' AND tablename LIKE 'events_%';

# Статистика операций
SELECT
    schemaname || '.' || tablename as partition_name,
    n_tup_ins as inserts,
    n_tup_upd as updates,
    n_tup_del as deletes
FROM pg_stat_user_tables
WHERE schemaname = 'public' AND tablename LIKE 'events_%';
```

### Grafana dashboards

```json
{
  "dashboard": {
    "title": "PostgreSQL Partitioning Dashboard",
    "panels": [
      {
        "title": "Partition Sizes",
        "type": "bargauge",
        "targets": [
          {
            "expr": "pg_partition_size_bytes{job='postgresql-partitions'}",
            "legendFormat": "{{partition_name}}",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Partition Row Counts",
        "type": "table",
        "targets": [
          {
            "expr": "pg_partition_live_rows{job='postgresql-partitions'}",
            "legendFormat": "{{partition_name}}",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Partition Operations Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(pg_partition_inserts_total[5m])",
            "legendFormat": "Inserts - {{partition_name}}",
            "refId": "A"
          },
          {
            "expr": "rate(pg_partition_updates_total[5m])",
            "legendFormat": "Updates - {{partition_name}}",
            "refId": "B"
          },
          {
            "expr": "rate(pg_partition_deletes_total[5m])",
            "legendFormat": "Deletes - {{partition_name}}",
            "refId": "C"
          }
        ]
      },
      {
        "title": "Partition Health Status",
        "type": "stat",
        "targets": [
          {
            "expr": "pg_partition_health_status{alert_level='ERROR'}",
            "refId": "A"
          }
        ]
      }
    ]
  }
}
```

## Лучшие практики и рекомендации

### Архитектурные решения

**1. Выбор стратегии партиционирования:**

```sql
-- Для временных данных - RANGE по времени
CREATE TABLE logs (
    id SERIAL,
    level VARCHAR(10),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

-- Для географических данных - LIST по регионам
CREATE TABLE sales (
    id SERIAL,
    region_id INTEGER,
    amount DECIMAL(10,2),
    sale_date DATE
) PARTITION BY LIST (region_id);

-- Для равномерного распределения - HASH
CREATE TABLE users (
    id SERIAL,
    username VARCHAR(50),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) PARTITION BY HASH (id);
```

**2. Именование партиций:**

```sql
-- Соглашение об именовании
-- {table_name}_{partition_key}_{date_pattern}
-- events_created_at_2024_01
-- sales_region_1
-- users_hash_001

-- Функция для генерации имен партиций
CREATE OR REPLACE FUNCTION generate_partition_name(
    base_table TEXT,
    partition_key TEXT,
    partition_value TEXT
) RETURNS TEXT AS $$
BEGIN
    -- Приводим к нижнему регистру и заменяем специальные символы
    RETURN lower(regexp_replace(
        base_table || '_' || partition_key || '_' || partition_value,
        '[^a-zA-Z0-9_]',
        '_',
        'g'
    ));
END;
$$ LANGUAGE plpgsql;
```

**3. Управление жизненным циклом партиций:**

```sql
-- Автоматическое создание партиций
CREATE OR REPLACE FUNCTION setup_partition_automation(
    base_table TEXT,
    partition_column TEXT,
    retention_period INTERVAL DEFAULT '1 year'
) RETURNS void AS $$
BEGIN
    -- Создание триггерной функции для автоматического создания партиций
    EXECUTE format('
        CREATE OR REPLACE FUNCTION create_%s_partition()
        RETURNS TRIGGER AS $func$
        BEGIN
            -- Логика создания партиции при вставке
            PERFORM ensure_partition_exists(''%s'', NEW.%s);
            RETURN NEW;
        END;
        $func$ LANGUAGE plpgsql;
    ', base_table, base_table, partition_column);

    -- Создание триггера
    EXECUTE format('
        DROP TRIGGER IF EXISTS %s_partition_trigger ON %s;
        CREATE TRIGGER %s_partition_trigger
            BEFORE INSERT ON %s
            FOR EACH ROW EXECUTE FUNCTION create_%s_partition();
    ', base_table, base_table, base_table, base_table, base_table);

    -- Настройка автоматической очистки
    PERFORM setup_partition_cleanup(base_table, retention_period);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION setup_partition_cleanup(
    base_table TEXT,
    retention_period INTERVAL
) RETURNS void AS $$
BEGIN
    -- Создание функции очистки
    EXECUTE format('
        CREATE OR REPLACE FUNCTION cleanup_%s_partitions()
        RETURNS void AS $func$
        BEGIN
            PERFORM drop_old_partitions(''%s'', ''%s''::INTERVAL);
        END;
        $func$ LANGUAGE plpgsql;
    ', base_table, base_table, retention_period);

    -- Настройка регулярного выполнения
    PERFORM cron.schedule(
        'cleanup_' || base_table || '_partitions',
        '0 2 1 * *',  -- Первое число каждого месяца
        format('SELECT cleanup_%s_partitions();', base_table)
    );
END;
$$ LANGUAGE plpgsql;
```

### Производительность

**Оптимизация индексов:**

```sql
-- Локальные индексы на партициях
CREATE INDEX idx_events_created_at_local ON events (created_at) LOCAL;

-- Глобальные индексы только при необходимости
CREATE INDEX idx_events_global_user_id ON events (user_id);

-- Частичные индексы для фильтрации
CREATE INDEX idx_active_events ON events (user_id, created_at)
WHERE status = 'active';

-- Покрывающие индексы
CREATE INDEX idx_events_covering ON events (user_id, created_at, event_type)
INCLUDE (event_data);
```

**Оптимизация запросов:**

```sql
-- Использование правильных условий для partition pruning
EXPLAIN SELECT * FROM events
WHERE created_at >= '2024-01-01' AND created_at < '2024-02-01';
-- Должен показать "Partitions: events_2024_01"

-- Избегать функций в условиях партиционирования
-- Правильно
SELECT * FROM events WHERE created_at >= '2024-01-01';

-- Неправильно (не использует pruning)
SELECT * FROM events WHERE date_trunc('month', created_at) = '2024-01-01';

-- Использование индексов на партициях
SELECT * FROM events
WHERE created_at >= '2024-01-01'
  AND user_id = 123
  AND event_type = 'login';
```

### Безопасность

**Защита партиционированных данных:**

```sql
-- Row Level Security для партиций
ALTER TABLE events ENABLE ROW LEVEL SECURITY;

-- Политика для пользователей
CREATE POLICY user_events_policy ON events
    FOR ALL
    USING (user_id = current_user_id() OR current_user_role() = 'admin');

-- Политика для регионов (при географическом партиционировании)
CREATE POLICY region_data_policy ON sales
    FOR ALL
    USING (region_id IN (
        SELECT region_id FROM user_regions
        WHERE user_id = current_user_id()
    ));

-- Аудит изменений в партиционированных таблицах
CREATE TABLE partition_audit (
    id SERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    partition_name TEXT NOT NULL,
    operation TEXT NOT NULL,
    record_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    user_id INTEGER,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Триггер для аудита
CREATE OR REPLACE FUNCTION audit_partition_changes()
RETURNS TRIGGER AS $$
DECLARE
    partition_name TEXT;
BEGIN
    -- Определяем имя партиции
    SELECT c.relname INTO partition_name
    FROM pg_inherits i
    JOIN pg_class c ON i.inhrelid = c.oid
    WHERE i.inhrelid = (SELECT c.oid FROM pg_class c WHERE c.relname = TG_TABLE_NAME);

    -- Записываем изменение
    INSERT INTO partition_audit (
        table_name, partition_name, operation,
        record_id, old_values, new_values, user_id
    ) VALUES (
        TG_TABLE_SCHEMA || '.' || TG_TABLE_NAME,
        partition_name,
        TG_OP,
        COALESCE(NEW.id, OLD.id),
        CASE WHEN TG_OP != 'INSERT' THEN row_to_json(OLD)::JSONB ELSE NULL END,
        CASE WHEN TG_OP != 'DELETE' THEN row_to_json(NEW)::JSONB ELSE NULL END,
        current_user_id()
    );

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Применяем ко всем партициям
DO $$
DECLARE
    partition_name TEXT;
BEGIN
    FOR partition_name IN
        SELECT c.relname
        FROM pg_inherits i
        JOIN pg_class c ON i.inhrelid = c.oid
        WHERE i.inhparent = (SELECT oid FROM pg_class WHERE relname = 'events')
    LOOP
        EXECUTE format('
            DROP TRIGGER IF EXISTS audit_trigger ON %I;
            CREATE TRIGGER audit_trigger
                AFTER INSERT OR UPDATE OR DELETE ON %I
                FOR EACH ROW EXECUTE FUNCTION audit_partition_changes();
        ', partition_name, partition_name);
    END LOOP;
END;
$$;
```

### Мониторинг и обслуживание

**Регулярные задачи обслуживания:**

```bash
# Скрипт для обслуживания партиций
#!/bin/bash

# partition_maintenance.sh

DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="mydb"
DB_USER="maintenance"

TABLES=("events" "logs" "metrics")

for table in "${TABLES[@]}"; do
    echo "Maintaining partitions for table: $table"

    # Анализ партиций
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "
        SELECT schemaname, tablename,
               pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size,
               n_live_tup, n_dead_tup
        FROM pg_stat_user_tables
        WHERE schemaname = 'public' AND tablename LIKE '${table}_%'
        ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
        LIMIT 5;
    "

    # VACUUM для партиций с высоким bloat
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "
        SELECT format('VACUUM ANALYZE %I.%I;', schemaname, tablename)
        FROM pg_stat_user_tables
        WHERE schemaname = 'public'
          AND tablename LIKE '${table}_%'
          AND n_live_tup > 0
          AND (n_dead_tup::float / n_live_tup) > 0.2
        LIMIT 3;
    " | while read vacuum_cmd; do
        if [ ! -z "$vacuum_cmd" ]; then
            echo "Running: $vacuum_cmd"
            psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "$vacuum_cmd"
        fi
    done

    # Проверка индексов
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "
        SELECT indexname, idx_scan, idx_tup_read, idx_tup_fetch
        FROM pg_stat_user_indexes
        WHERE schemaname = 'public' AND tablename LIKE '${table}_%'
        ORDER BY idx_scan DESC
        LIMIT 5;
    "

    echo "---"
done

echo "Partition maintenance completed"
```

**Партиционирование в **PostgreSQL** — это мощная техника для управления большими объемами данных, обеспечивающая:**

1. **Улучшенную производительность** запросов через **partition pruning**
2. **Упрощенное обслуживание** с возможностью работы с отдельными партициями
3. **Масштабируемость** для обработки растущих объемов данных
4. **Оптимизацию хранения** с возможностью разных стратегий для разных данных

### Ключевые стратегии партиционирования:

- **RANGE**: Для данных с естественным порядком (даты, числа)
- **LIST**: Для категориальных данных с ограниченным набором значений
- **HASH**: Для равномерного распределения и масштабирования

### Лучшие практики:

1. **Выбирайте правильный ключ партиционирования** — наиболее часто используемый в **WHERE**
2. **Планируйте партиции заранее** — учитывайте рост данных и паттерны запросов
3. **Автоматизируйте обслуживание** — создание и удаление партиций
4. **Мониторьте производительность** — анализируйте эффективность **partition pruning**
5. **Тестируйте стратегии** — сравнивайте производительность разных подходов
6. **Документируйте схему** — поддерживайте актуальную документацию партиций

### Типичные ошибки и их избежание:

- **Неправильный выбор ключа**: Приводит к неравномерному распределению данных
- **Отсутствие автоматизации**: Ручное управление партициями становится проблемой
- **Игнорирование индексов**: Каждая партиция нуждается в соответствующих индексах
- **Неправильное обслуживание**: Старые партиции накапливаются и замедляют систему

Партиционирование требует тщательного планирования и постоянного мониторинга, но при правильной реализации обеспечивает отличную производительность и управляемость для больших **PostgreSQL** баз данных.

## Решение проблем распространенных проблем

### Проблема: Partition pruning не работает

**Симптомы:**
- Запросы сканируют все партиции вместо одной
- Низкая производительность даже с индексами

**Возможные причины и решения:**

```sql
-- 1. Функции в условиях WHERE
-- ПЛОХО - pruning не работает
EXPLAIN SELECT * FROM events WHERE date_trunc('month', created_at) = '2024-01-01';

-- ХОРОШО - pruning работает
EXPLAIN SELECT * FROM events WHERE created_at >= '2024-01-01' AND created_at < '2024-02-01';

-- 2. Неявное преобразование типов
-- ПЛОХО - сравнение строки с timestamp
EXPLAIN SELECT * FROM events WHERE created_at = '2024-01-01';

-- ХОРОШО - правильные типы
EXPLAIN SELECT * FROM events WHERE created_at = '2024-01-01'::timestamp;

-- 3. Операторы, не поддерживающие pruning
-- ПЛОХО - LIKE не поддерживает pruning для range партиций
EXPLAIN SELECT * FROM events WHERE created_at::text LIKE '2024-01%';

-- ХОРОШО - правильные операторы
EXPLAIN SELECT * FROM events WHERE created_at >= '2024-01-01' AND created_at < '2024-02-01';

-- 4. Проверка настроек constraint_exclusion
SHOW constraint_exclusion;
-- Должно быть 'on' или 'partition'

SET constraint_exclusion = on;
```

### Проблема: Неравномерное распределение данных

**Симптомы:**
- Некоторые партиции огромные, другие почти пустые
- Несбалансированная нагрузка на диски

**Решения:**

```sql
-- Анализ распределения
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size,
    n_live_tup as rows_count,
    CASE WHEN n_live_tup > 0 THEN
        pg_total_relation_size(schemaname||'.'||tablename)::float / n_live_tup
    ELSE 0 END as bytes_per_row
FROM pg_stat_user_tables
WHERE schemaname = 'public'
  AND tablename LIKE 'events_%'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Для hash партиционирования - проверка равномерности
SELECT
    tablename,
    n_live_tup,
    round(n_live_tup::numeric / sum(n_live_tup) over () * 100, 2) as percent_of_total
FROM pg_stat_user_tables
WHERE schemaname = 'public'
  AND tablename LIKE 'users_hash_%'
ORDER BY n_live_tup DESC;

-- Перебалансировка hash партиций (при необходимости увеличить количество партиций)
-- 1. Создание новых партиций
ALTER TABLE users ATTACH PARTITION users_hash_005 FOR VALUES WITH (modulus 5, remainder 4);
ALTER TABLE users ATTACH PARTITION users_hash_006 FOR VALUES WITH (modulus 6, remainder 5);

-- 2. Пересоздание с новым modulus
-- Требуется полный rebuild таблицы
CREATE TABLE users_new (LIKE users INCLUDING ALL) PARTITION BY HASH (id);
-- Копирование данных и переименование
```

### Проблема: Медленная вставка в партиционированные таблицы

**Симптомы:**
- **INSERT** операции работают медленно
- Высокая нагрузка на **CPU** при вставке

**Оптимизации:**

```sql
-- 1. Правильный порядок столбцов в индексах
-- ПЛОХО - индекс не соответствует partition key
CREATE INDEX idx_events_user_created ON events (user_id, created_at);

-- ХОРОШО - partition key первым
CREATE INDEX idx_events_created_user ON events (created_at, user_id);

-- 2. Использование COPY вместо INSERT для массовой загрузки
COPY events (user_id, event_type, created_at, event_data)
FROM '/path/to/data.csv' WITH CSV HEADER;

-- 3. Отключение индексов при массовой загрузке
ALTER TABLE events DETACH PARTITION events_current;
-- Загрузка данных
ALTER TABLE events ATTACH PARTITION events_current FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- 4. Правильные настройки autovacuum для партиций
ALTER TABLE events SET (
    autovacuum_vacuum_scale_factor = 0.02,
    autovacuum_analyze_scale_factor = 0.01
);

-- 5. Использование unlogged таблиц для staging
CREATE UNLOGGED TABLE events_staging (LIKE events);
-- Быстрая загрузка
COPY events_staging FROM '/path/to/data.csv' WITH CSV;
-- Вставка в основную таблицу
INSERT INTO events SELECT * FROM events_staging;
DROP TABLE events_staging;
```

### Проблема: Блокировка партиций при DDL операциях

**Симптомы:**
- **ALTER TABLE** блокирует всю партиционированную таблицу
- Долгое время выполнения **DDL**

**Решения:**

```sql
-- 1. Использование pg_repack для online rebuild
-- Установка pg_repack
CREATE EXTENSION pg_repack;

-- Online rebuild индекса на партиции
SELECT pg_repack.repack_index('idx_events_created_at', 'events_2024_01');

-- Online rebuild всей партиции
SELECT pg_repack.repack_table('events_2024_01');

-- 2. Использование CREATE INDEX CONCURRENTLY для индексов
CREATE INDEX CONCURRENTLY idx_events_user_id ON events (user_id);
-- Не блокирует вставки

-- 3. Создание новых партиций без блокировки
-- Вместо ALTER TABLE ... ATTACH PARTITION
-- Используйте отдельную транзакцию с минимальной блокировкой

-- 4. Отложенное создание индексов
-- Создание индексов после загрузки данных
CREATE INDEX CONCURRENTLY idx_large_partition_data ON large_partition (data_col);
```

## Реальные примеры использования

### Пример 1: Логи аудита с ротацией

Таблица аудита с месячными партициями по `changed_at`; ротация — функция `rotate_audit_partitions()` (создание новой партиции, индексы, удаление старых); триггер пишет в `audit_log`. Рекомендуется настроить `cron` на ежемесячный вызов ротации.

```sql
CREATE TABLE audit_log (
    id BIGSERIAL,
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    old_values JSONB, new_values JSONB,
    user_id INTEGER, session_id TEXT, client_ip INET,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, changed_at)
) PARTITION BY RANGE (changed_at);

SELECT create_time_partitions('audit_log', 'changed_at', 'monthly', 12);
-- Триггер и rotate_audit_partitions() — по шаблону выше
```

### Пример 2: Time-series данные IoT

Таблица датчиков с партициями по дням; индексы по `(sensor_id, timestamp DESC)`, GIST для `location`, GIN для `metadata`; материализованное представление для почасовой агрегации; запросы — последние показания, средние за час, датчики с низким зарядом.

```sql
CREATE TABLE sensor_data (
    sensor_id INTEGER NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    temperature DECIMAL(5,2), humidity DECIMAL(5,2), pressure DECIMAL(7,2),
    battery_level INTEGER CHECK (battery_level BETWEEN 0 AND 100),
    location GEOMETRY(POINT, 4326), metadata JSONB,
    PRIMARY KEY (sensor_id, timestamp)
) PARTITION BY RANGE (timestamp);

SELECT create_time_partitions('sensor_data', 'timestamp', 'daily', 7);
CREATE INDEX idx_sensor_timestamp ON sensor_data (sensor_id, timestamp DESC);
-- hourly_sensor_stats (MATERIALIZED VIEW) и запросы — по шаблону выше
```

### Пример 3: Финансовые транзакции

Транзакции по счёту с месячными партициями по `transaction_date`; индексы по `(account_id, transaction_date DESC)`, по `reference_number`, частичный по `status`; функции `process_transaction`, `validate_transaction` и триггер — по шаблону; отчёты и поиск подозрительных — запросами по партиционированной таблице.

```sql
CREATE TABLE transactions (
    id BIGSERIAL,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('debit', 'credit', 'transfer')),
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) DEFAULT 'USD', description TEXT, reference_number VARCHAR(100) UNIQUE,
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'processed', 'failed', 'cancelled')),
    PRIMARY KEY (id, transaction_date)
) PARTITION BY RANGE (transaction_date);

SELECT create_time_partitions('transactions', 'transaction_date', 'monthly', 12);
CREATE INDEX idx_transactions_account_date ON transactions (account_id, transaction_date DESC);
-- process_transaction(), validate_transaction(), триггеры и запросы — по шаблону выше
```

## Миграция существующих таблиц на партиционирование

### Онлайн миграция без downtime

```sql
-- Шаг 1: Создание новой партиционированной таблицы
CREATE TABLE events_new (
    id BIGSERIAL,
    user_id INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
) PARTITION BY RANGE (created_at);

-- Шаг 2: Создание партиций для существующих данных
-- Определение диапазона данных
SELECT
    min(created_at) as min_date,
    max(created_at) as max_date,
    count(*) as total_rows
FROM events;

-- Создание партиций на основе существующих данных
SELECT create_time_partitions('events_new', 'created_at', 'monthly', 12);

-- Шаг 3: Копирование данных
-- Использование INSERT с ORDER BY для оптимального распределения
INSERT INTO events_new
SELECT * FROM events
ORDER BY created_at;

-- Шаг 4: Создание индексов и ограничений
CREATE INDEX idx_events_new_user_created ON events_new (user_id, created_at);
CREATE INDEX idx_events_new_type_created ON events_new (event_type, created_at);

-- Шаг 5: Переименование таблиц (атомарная операция)
BEGIN;
    -- Переименование старой таблицы
    ALTER TABLE events RENAME TO events_old;

    -- Переименование новой таблицы
    ALTER TABLE events_new RENAME TO events;

    -- Обновление зависимостей (views, functions, etc.)
    -- ALTER VIEW events_view RENAME TO events_view_old;
    -- CREATE VIEW events_view AS SELECT * FROM events WHERE ...;

COMMIT;

-- Шаг 6: Очистка (после проверки работоспособности)
-- DROP TABLE events_old CASCADE;
```

### Миграция с помощью логической репликации

```sql
-- Для больших таблиц - использование логической репликации
-- Шаг 1: Настройка публикации на исходной БД
CREATE PUBLICATION events_pub FOR TABLE events;

-- Шаг 2: Настройка подписки на целевой БД
CREATE SUBSCRIPTION events_sub
    CONNECTION 'host=source_host dbname=source_db user=replication_user'
    PUBLICATION events_pub;

-- Шаг 3: Синхронизация данных
-- PostgreSQL автоматически копирует существующие данные

-- Шаг 4: После полной синхронизации - переключение приложений
-- Остановка приложений
-- Переключение соединений на новую БД
-- Удаление подписки и публикации

-- Шаг 5: Преобразование в партиционированную таблицу на новой БД
-- (шаги 1-6 из предыдущего примера)
```

## Производительность и масштабируемость

### Бенчмаркинг партиционирования

```sql
-- Создание тестовой среды
CREATE TABLE benchmark_events (
    id BIGSERIAL,
    user_id INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    data TEXT
);

-- Заполнение тестовыми данными
INSERT INTO benchmark_events (user_id, event_type, data)
SELECT
    (random() * 10000)::integer + 1,
    CASE (random() * 4)::integer
        WHEN 0 THEN 'login'
        WHEN 1 THEN 'logout'
        WHEN 2 THEN 'click'
        WHEN 3 THEN 'view'
        ELSE 'other'
    END,
    md5(random()::text)
FROM generate_series(1, 10000000);  -- 10M строк

-- Бенчмарк запросов
-- Тест 1: Полное сканирование
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM benchmark_events;

-- Тест 2: Фильтр по диапазону дат
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM benchmark_events
WHERE created_at >= '2024-01-01' AND created_at < '2024-02-01';

-- Тест 3: Фильтр по user_id
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM benchmark_events
WHERE user_id = 1234
ORDER BY created_at DESC
LIMIT 100;

-- Тест 4: Агрегация
EXPLAIN (ANALYZE, BUFFERS)
SELECT
    date_trunc('day', created_at) as day,
    event_type,
    count(*) as events_count
FROM benchmark_events
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY date_trunc('day', created_at), event_type
ORDER BY day DESC, events_count DESC;

-- Теперь создаем партиционированную версию
CREATE TABLE benchmark_events_partitioned (
    id BIGSERIAL,
    user_id INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    data TEXT
) PARTITION BY RANGE (created_at);

-- Создание партиций
SELECT create_time_partitions('benchmark_events_partitioned', 'created_at', 'monthly', 12);

-- Копирование данных
INSERT INTO benchmark_events_partitioned
SELECT * FROM benchmark_events;

-- Повторяем тесты и сравниваем производительность
-- Тест 2 должен показать значительное улучшение благодаря partition pruning
-- Тест 4 также должен быть быстрее благодаря меньшему объему сканируемых данных
```

### Масштабирование партиционирования

```sql
-- Для очень больших объемов данных - композитное партиционирование
CREATE TABLE big_data_events (
    id BIGSERIAL,
    tenant_id INTEGER NOT NULL,  -- Multi-tenant
    user_id INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    data JSONB
) PARTITION BY LIST (tenant_id);

-- Создание партиций по tenant'ам
CREATE TABLE tenant_1 PARTITION OF big_data_events FOR VALUES IN (1) PARTITION BY RANGE (created_at);
CREATE TABLE tenant_2 PARTITION OF big_data_events FOR VALUES IN (2) PARTITION BY RANGE (created_at);

-- Субпартиции по времени
SELECT create_time_partitions('tenant_1', 'created_at', 'daily', 30);
SELECT create_time_partitions('tenant_2', 'created_at', 'daily', 30);

-- Для гео-распределенных систем - hash партиционирование
CREATE TABLE global_events (
    id BIGSERIAL,
    region_id INTEGER NOT NULL,
    event_data JSONB,
    created_at TIMESTAMP DEFAULT NOW()
) PARTITION BY HASH (id);

-- Создание партиций по количеству серверов
-- Например, для 8 серверов
CREATE TABLE global_events_0 PARTITION OF global_events FOR VALUES WITH (modulus 8, remainder 0);
CREATE TABLE global_events_1 PARTITION OF global_events FOR VALUES WITH (modulus 8, remainder 1);
-- ... и так далее до global_events_7

-- Каждая партиция может находиться на отдельном сервере
-- Используя foreign data wrappers или репликацию
```

## Безопасность и комплаенс

### Шифрование партиционированных данных

```sql
-- Использование pgcrypto для шифрования чувствительных данных
CREATE EXTENSION pgcrypto;

-- Таблица с зашифрованными данными
CREATE TABLE secure_events (
    id BIGSERIAL,
    user_id INTEGER NOT NULL,
    encrypted_data BYTEA,  -- Зашифрованные данные
    encryption_key_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
) PARTITION BY RANGE (created_at);

-- Функции для шифрования/дешифрования
CREATE OR REPLACE FUNCTION encrypt_event_data(
    data JSONB,
    key_id INTEGER
) RETURNS BYTEA AS $$
DECLARE
    encryption_key BYTEA;
BEGIN
    -- Получение ключа шифрования
    SELECT key_data INTO encryption_key
    FROM encryption_keys
    WHERE id = key_id;

    -- Шифрование данных
    RETURN pgp_sym_encrypt(data::text, encode(encryption_key, 'hex'));
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION decrypt_event_data(
    encrypted_data BYTEA,
    key_id INTEGER
) RETURNS JSONB AS $$
DECLARE
    encryption_key BYTEA;
BEGIN
    SELECT key_data INTO encryption_key
    FROM encryption_keys
    WHERE id = key_id;

    RETURN pgp_sym_decrypt(encrypted_data, encode(encryption_key, 'hex'))::jsonb;
END;
$$ LANGUAGE plpgsql;

-- Пример использования
INSERT INTO secure_events (user_id, encrypted_data, encryption_key_id, created_at)
VALUES (
    123,
    encrypt_event_data('{"sensitive": "data"}'::jsonb, 1),
    1,
    NOW()
);

-- Безопасный запрос
SELECT
    id,
    user_id,
    decrypt_event_data(encrypted_data, encryption_key_id) as data,
    created_at
FROM secure_events
WHERE user_id = 123;
```

### Аудит и мониторинг доступа

```sql
-- Расширенная таблица аудита
CREATE TABLE access_audit (
    id BIGSERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    partition_name TEXT,
    user_id INTEGER,
    action TEXT NOT NULL,  -- SELECT, INSERT, UPDATE, DELETE
    query_text TEXT,
    rows_affected INTEGER,
    client_ip INET,
    session_id TEXT,
    accessed_at TIMESTAMP DEFAULT NOW()
) PARTITION BY RANGE (accessed_at);

-- Функция для логирования доступа
CREATE OR REPLACE FUNCTION log_partition_access(
    p_table_name TEXT,
    p_partition_name TEXT,
    p_action TEXT,
    p_query_text TEXT,
    p_rows_affected INTEGER
) RETURNS void AS $$
BEGIN
    INSERT INTO access_audit (
        table_name, partition_name, user_id, action,
        query_text, rows_affected, client_ip, session_id
    ) VALUES (
        p_table_name, p_partition_name,
        current_user_id(), p_action,
        p_query_text, p_rows_affected,
        inet_client_addr(), current_session_id()
    );
END;
$$ LANGUAGE plpgsql;

-- Триггер для автоматического логирования (для чувствительных партиций)
CREATE OR REPLACE FUNCTION audit_partition_operations()
RETURNS TRIGGER AS $$
DECLARE
    partition_name TEXT;
    rows_affected INTEGER;
BEGIN
    -- Определение партиции
    SELECT c.relname INTO partition_name
    FROM pg_inherits i
    JOIN pg_class c ON i.inhrelid = c.oid
    WHERE i.inhrelid = (SELECT c.oid FROM pg_class c WHERE c.relname = TG_TABLE_NAME);

    -- Подсчет затронутых строк
    GET DIAGNOSTICS rows_affected = ROW_COUNT;

    -- Логирование
    PERFORM log_partition_access(
        TG_TABLE_SCHEMA || '.' || TG_TABLE_NAME,
        partition_name,
        TG_OP,
        current_query(),
        rows_affected
    );

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Применение аудита к чувствительным партициям
DO $$
DECLARE
    partition_name TEXT;
BEGIN
    FOR partition_name IN
        SELECT c.relname
        FROM pg_inherits i
        JOIN pg_class c ON i.inhrelid = c.oid
        WHERE i.inhparent = (SELECT oid FROM pg_class WHERE relname = 'secure_events')
    LOOP
        EXECUTE format('
            DROP TRIGGER IF EXISTS audit_trigger ON %I;
            CREATE TRIGGER audit_trigger
                AFTER INSERT OR UPDATE OR DELETE ON %I
                FOR EACH ROW EXECUTE FUNCTION audit_partition_operations();
        ', partition_name, partition_name);
    END LOOP;
END;
$$;

-- Запросы для compliance отчетов
-- Кто и когда обращался к данным пациентов
SELECT
    aa.user_id,
    u.username,
    aa.action,
    aa.table_name,
    aa.partition_name,
    aa.rows_affected,
    aa.accessed_at,
    aa.client_ip
FROM access_audit aa
JOIN users u ON aa.user_id = u.id
WHERE aa.table_name LIKE '%patient%'
  AND aa.accessed_at >= CURRENT_DATE - INTERVAL '90 days'
ORDER BY aa.accessed_at DESC;

-- Необычная активность
SELECT
    user_id,
    date_trunc('hour', accessed_at) as hour,
    count(*) as access_count,
    string_agg(DISTINCT action, ', ') as actions
FROM access_audit
WHERE accessed_at >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY user_id, date_trunc('hour', accessed_at)
HAVING count(*) > 100  -- Порог подозрительной активности
ORDER BY hour DESC;
```

## Заключение

**Партиционирование в **PostgreSQL** — это инструмент для управления большими объемами данных, обеспечивающий:**

- **Высокую производительность** через **partition pruning** и параллельную обработку
- **Масштабируемость** для обработки растущих объемов данных
- **Упрощенное обслуживание** с возможностью работы с отдельными партициями
- **Гибкость** в выборе стратегий (range, list, hash, композитное)
- **Безопасность** и **compliance** через изоляцию чувствительных данных

### Ключевые принципы успешного партиционирования:

1. **Правильный выбор ключа партиционирования** — наиболее селективный столбец
2. **Понимание паттернов запросов** — какие фильтры используются чаще всего
3. **Планирование роста** — создание партиций на будущее
4. **Автоматизация обслуживания** — регулярное создание и удаление партиций
5. **Мониторинг и оптимизация** — постоянный анализ эффективности
6. **Тестирование стратегий** — сравнение производительности разных подходов
7. **Документирование** — поддержание актуальной документации схемы партиций

Партиционирование требует тщательного планирования и постоянного мониторинга, но при правильной реализации обеспечивает отличную производительность и управляемость для больших **PostgreSQL** баз данных, поддерживая рост приложений от небольших проектов до **enterprise**-систем с миллионами записей.

## См. также

- [[postgres-admin|PostgreSQL: администрирование и обслуживание]]
- [[postgres-backup-restore|PostgreSQL: Резервное копирование и восстановление]]
- [[postgres-basics|PostgreSQL: Полное руководство по основам и мониторингу]]
- [[postgres-data-ops|PostgreSQL: операции с данными (CRUD)]]
- [[postgres-design|PostgreSQL: проектирование и нормализация]]
