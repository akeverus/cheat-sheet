---
title: "ClickHouse: Таблицы и движки - Полное руководство по созданию и управлению таблицами"
description: "Комплексное руководство по таблицам ClickHouse: движки таблиц, типы данных, создание и оптимизация таблиц"
tags:
  - clickhouse
  - tables
  - engines
  - data-types
  - mergetree
  - schema
difficulty: "intermediate"
prerequisites: ["databases/clickhouse-basics.md"]
updated: "2026-02-06"
related: ["databases/clickhouse-basics.md", "databases/clickhouse-queries.md"]
---

# **ClickHouse**: Таблицы и движки — Полное руководство по созданию и управлению таблицами

Комплексное руководство по таблицам **ClickHouse**: движки таблиц, типы данных, создание и оптимизация таблиц.

## Полезные ссылки

### Официальная документация
- [Table Engines](https://clickhouse.com/docs/en/engines/table-engines)
- [Data Types](https://clickhouse.com/docs/en/sql-reference/data-types)
- [Creating Tables](https://clickhouse.com/docs/en/sql-reference/statements/create/table)

### Обучающие материалы
- [ClickHouse Table Engines](https://www.baeldung.com/clickhouse-table-engines)

### См. также
- [[clickhouse-basics|Основы]] — **ClickHouse**
- [[clickhouse-queries|Запросы]] — работа с данными

## Содержание

- [Создание таблиц](#создание-таблиц)
  - [Синтаксис **CREATE TABLE**](#синтаксис-create-table)
  - [Базовая структура таблицы](#базовая-структура-таблицы)
  - [Продвинутая структура](#продвинутая-структура)
- [Движки таблиц](#движки-таблиц)
  - [**MergeTree Family**](#mergetree-family)
    - [**MergeTree** (**основной движок**)](#mergetree-основной-движок)
    - [**ReplacingMergeTree**](#replacingmergetree)
    - [**SummingMergeTree**](#summingmergetree)
    - [**AggregatingMergeTree**](#aggregatingmergetree)
    - [**CollapsingMergeTree**](#collapsingmergetree)
    - [**VersionedCollapsingMergeTree**](#versionedcollapsingmergetree)
  - [Специализированные движки](#специализированные-движки)
    - [**Memory**](#memory)
    - [**Distributed**](#distributed)
    - [**MaterializedView**](#materializedview)
- [Типы данных](#типы-данных)
  - [Числовые типы](#числовые-типы)
    - [Целые числа](#целые-числа)
    - [Числа с плавающей точкой](#числа-с-плавающей-точкой)
    - [**Decimal** (**фиксированная точность**)](#decimal-фиксированная-точность)
  - [Строковые типы](#строковые-типы)
  - [Дата и время](#дата-и-время)
  - [Сложные типы данных](#сложные-типы-данных)
    - [Массивы](#массивы)
    - [Кортежи (**Tuples**)](#кортежи-tuples)
    - [**Nullable** типы](#nullable-типы)
    - [**Enum** типы](#enum-типы)
    - [**Map** типы](#map-типы)
    - [**Nested** типы](#nested-типы)
- [Оптимизация таблиц](#оптимизация-таблиц)
  - [Выбор подходящего движка](#выбор-подходящего-движка)
  - [Настройка партиционирования](#настройка-партиционирования)
  - [Настройка первичного ключа](#настройка-первичного-ключа)
  - [Настройки производительности](#настройки-производительности)
- [Управление таблицами](#управление-таблицами)
  - [Просмотр информации о таблицах](#просмотр-информации-о-таблицах)
  - [Модификация таблиц](#модификация-таблиц)
  - [Очистка и обслуживание](#очистка-и-обслуживание)
- [**Best Practices**](#лучшие-практики)
  - [Выбор типов данных](#выбор-типов-данных)
  - [Проектирование схемы](#проектирование-схемы)
  - [Производительность](#производительность)
  - [Ключевые решения:](#ключевые-решения)
  - [Рекомендации:](#рекомендации)
  - [Следующие шаги:](#следующие-шаги)

## Создание таблиц

### Синтаксис **CREATE TABLE**

```sql
CREATE TABLE [IF NOT EXISTS] table_name
(
    column1 column_type [options],
    column2 column_type [options],
    ...
    INDEX index_name expression TYPE index_type(...) GRANULARITY granularity_value,
    ...
)
ENGINE = engine_name([parameters])
[PARTITION BY partition_expression]
[ORDER BY (column1, column2, ...)]
[PRIMARY KEY (column1, column2, ...)]
[SAMPLE BY sample_expression]
[TTL ttl_expression]
[SETTINGS setting_name = setting_value, ...]
[COMMENT 'table comment']
```

### Базовая структура таблицы

```sql
-- Простая таблица
CREATE TABLE users
(
    id UInt64,
    name String,
    email String,
    age UInt8,
    created_at DateTime
)
ENGINE = MergeTree()
ORDER BY id;

-- С комментариями
CREATE TABLE products
(
    product_id UInt64 COMMENT 'Уникальный идентификатор товара',
    name String COMMENT 'Название товара',
    price Decimal(10, 2) COMMENT 'Цена товара',
    category String COMMENT 'Категория товара',
    in_stock UInt32 COMMENT 'Количество на складе'
)
ENGINE = MergeTree()
ORDER BY product_id
COMMENT 'Таблица товаров интернет-магазина';
```

### Продвинутая структура

```sql
CREATE TABLE user_events
(
    event_id UUID DEFAULT generateUUIDv4(),
    user_id UInt64,
    event_type String,
    event_time DateTime DEFAULT now(),
    event_date Date MATERIALIZED toDate(event_time),
    session_id String,
    url String,
    user_agent String,
    ip_address String,
    properties String,  -- JSON строка с дополнительными свойствами

    -- Индексы для ускорения запросов
    INDEX idx_event_type event_type TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_url url TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_session_id session_id TYPE bloom_filter(0.01) GRANULARITY 1
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (user_id, event_time)
TTL event_time + INTERVAL 1 YEAR  -- Автоматическое удаление старых данных
SETTINGS index_granularity = 8192;
```

## Движки таблиц

### **MergeTree Family**

#### **MergeTree** (**основной движок**)

Основной движок для аналитических данных с поддержкой партиционирования и индексов.

```sql
CREATE TABLE analytics.events
(
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime,
    data String
)
ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)  -- Партиционирование по месяцам
ORDER BY (user_id, timestamp)     -- Сортировка по пользователю и времени
SETTINGS index_granularity = 8192;  -- Размер гранулы индекса
```

**Особенности `MergeTree`:**
- Поддержка партиционирования
- Первичный ключ для сортировки
- Вторичные индексы (**skip indexes**)
- **TTL** для автоматической очистки
- Оптимизация с помощью слияния

#### **ReplacingMergeTree**

Автоматически удаляет дубликаты при слиянии партиций.

```sql
CREATE TABLE user_profiles
(
    user_id UInt64,
    name String,
    email String,
    updated_at DateTime,
    version UInt32  -- Версия записи для разрешения конфликтов
)
ENGINE = ReplacingMergeTree(updated_at)  -- Поле для разрешения дубликатов
ORDER BY user_id
PARTITION BY user_id % 100;  -- Распределение по партициям
```

**Когда использовать:**
- Данные с частыми обновлениями
- Необходимость дедупликации
- Потоковая обработка данных

#### **SummingMergeTree**

Автоматически суммирует значения при слиянии партиций.

```sql
CREATE TABLE metrics.hourly_stats
(
    date Date,
    hour UInt8,
    metric_name String,
    value UInt64,
    count UInt64
)
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (metric_name, date, hour);

-- При вставке данных с одинаковыми ключами они будут суммироваться
INSERT INTO metrics.hourly_stats VALUES
('2024-01-01', 10, 'page_views', 100, 50),
('2024-01-01', 10, 'page_views', 200, 100);
-- Результат: value=300, count=150
```

**Когда использовать:**
- Агрегационные данные (**статистики, метрики**)
- Частые вставки с одинаковыми ключами
- Необходимость автоматического суммирования

#### **AggregatingMergeTree**

Хранит состояния агрегатных функций для последующей агрегации.

```sql
CREATE TABLE user_stats
(
    date Date,
    user_id UInt64,
    page_views AggregateFunction(count, UInt64),
    total_time AggregateFunction(sum, UInt64),
    unique_pages AggregateFunction(uniq, String)
)
ENGINE = AggregatingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (user_id, date);

-- Вставка состояний агрегатов
INSERT INTO user_stats
SELECT
    toDate(created_at) as date,
    user_id,
    countState() as page_views,
    sumState(session_time) as total_time,
    uniqState(page_url) as unique_pages
FROM user_sessions
GROUP BY date, user_id;
```

**Когда использовать:**
- Предварительно агрегированные данные
- Инкрементальная агрегация
- Сложные аналитические расчеты

#### **CollapsingMergeTree**

Обрабатывает состояния "**collapsed**" записей.

```sql
CREATE TABLE inventory
(
    item_id UInt64,
    location String,
    quantity Int64,  -- Положительное для прихода, отрицательное для расхода
    sign Int8,       -- 1 для прихода, -1 для расхода
    version UInt64
)
ENGINE = CollapsingMergeTree(sign, version)
ORDER BY (item_id, location);

-- Вставка операций
INSERT INTO inventory VALUES
(1, 'warehouse1', 100, 1, 1),    -- Приход
(1, 'warehouse1', -20, -1, 2);   -- Расход

-- После слияния останется одна запись с quantity = 80
```

**Когда использовать:**
- Данные с отменяющимися операциями
- Финансовые транзакции
- Инвентаризационные системы

#### **VersionedCollapsingMergeTree**

Расширенная версия **CollapsingMergeTree** с поддержкой версий.

```sql
CREATE TABLE orders
(
    order_id UInt64,
    item_id UInt64,
    quantity Int64,
    sign Int8,
    version UInt64
)
ENGINE = VersionedCollapsingMergeTree(sign, version)
ORDER BY (order_id, item_id);
```

### Специализированные движки

#### **Memory**

Хранит данные в оперативной памяти. Быстрый, но данные теряются при перезапуске.

```sql
CREATE TABLE cache.lookup_table
(
    key String,
    value String,
    ttl DateTime
)
ENGINE = Memory();
```

**Когда использовать:**
- Кэширование данных
- Временные результаты
- Тестирование и разработка

#### **Distributed**

Распределенная таблица, объединяющая данные с нескольких серверов.

```sql
CREATE TABLE distributed_events
(
    event_id UInt64,
    user_id UInt64,
    event_type String,
    timestamp DateTime
)
ENGINE = Distributed('cluster_name', 'database_name', 'local_table_name', rand());
```

**Когда использовать:**
- Запросы к кластеру
- Объединение данных с разных шардов
- Горизонтальное масштабирование

#### **MaterializedView**

Материализованное представление, автоматически обновляемое при вставке данных.

```sql
-- Базовая таблица
CREATE TABLE raw_events (
    timestamp DateTime,
    user_id UInt64,
    event_type String,
    data String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);

-- Материализованное представление
CREATE MATERIALIZED VIEW daily_stats
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (user_id, date)
AS SELECT
    toDate(timestamp) as date,
    user_id,
    event_type,
    count() as events_count,
    sum(length(data)) as data_size
FROM raw_events
GROUP BY date, user_id, event_type;
```

**Когда использовать:**
- Предварительно вычисленные агрегаты
- Денормализация данных
- Ускорение часто используемых запросов

## Типы данных

### Числовые типы

#### Целые числа

```sql
-- Беззнаковые целые числа (только положительные)
UInt8   -- 0 to 255 (1 байт)
UInt16  -- 0 to 65535 (2 байта)
UInt32  -- 0 to 4294967295 (4 байта)
UInt64  -- 0 to 18446744073709551615 (8 байт)

-- Знаковые целые числа
Int8    -- -128 to 127 (1 байт)
Int16   -- -32768 to 32767 (2 байта)
Int32   -- -2147483648 to 2147483647 (4 байта)
Int64   -- -9223372036854775808 to 9223372036854775807 (8 байт)

-- Примеры использования
CREATE TABLE measurements
(
    sensor_id UInt16,
    temperature Int16,     -- -32768 to 32767 (достаточно для температуры)
    humidity UInt8,        -- 0 to 100 (влажность в процентах)
    pressure UInt32,       -- атмосферное давление
    timestamp UInt64       -- Unix timestamp
);
```

#### Числа с плавающей точкой

```sql
Float32  -- 4 байта, ~7 значащих цифр
Float64  -- 8 байт, ~15 значащих цифр

-- Примеры
CREATE TABLE coordinates
(
    latitude Float64,   -- широта с высокой точностью
    longitude Float64,  -- долгота с высокой точностью
    altitude Float32    -- высота (меньшая точность достаточна)
);
```

#### **Decimal** (**фиксированная точность**)

```sql
-- Decimal32(S) - 4 байта, S - знаки после запятой (макс 9)
-- Decimal64(S) - 8 байт, S - знаки после запятой (макс 18)
-- Decimal128(S) - 16 байт, S - знаки после запятой (макс 38)
-- Decimal256(S) - 32 байта, S - знаки после запятой (макс 76)

-- Финансовые расчеты
CREATE TABLE transactions
(
    amount Decimal64(2),     -- сумма с копейками
    fee Decimal32(4),        -- комиссия с высокой точностью
    balance Decimal128(2)    -- баланс с очень высокой точностью
);
```

### Строковые типы

```sql
-- Произвольной длины
String  -- UTF-8 строка, неограниченная длина

-- Фиксированной длины (устаревший, используйте String)
FixedString(N)  -- Строка фиксированной длины N байт

-- Специальные типы
UUID    -- 16-байтовый UUID

-- Примеры
CREATE TABLE users
(
    user_id UUID,
    username String,
    email String,
    phone FixedString(15),  -- +7(999)999-99-99
    bio String              -- Произвольная длина
);
```

### Дата и время

```sql
-- Дата (без времени)
Date        -- 2 байта, диапазон 1970-01-01 до 2149-06-06
Date32      -- 4 байта, расширенный диапазон 1900-01-01 до 2299-12-31

-- Дата и время
DateTime    -- 4 байта, секунды с 1970-01-01 00:00:00 UTC
DateTime64  -- 8 байт, наносекунды (указывается точность)

-- Примеры
CREATE TABLE logs
(
    date_only Date,                    -- Только дата
    timestamp DateTime,                -- Дата и время
    precise_time DateTime64(3),        -- Миллисекунды
    nano_time DateTime64(9)            -- Наносекунды
);
```

### Сложные типы данных

#### Массивы

```sql
Array(T)  -- Массив элементов типа T

-- Примеры
CREATE TABLE products
(
    product_id UInt64,
    name String,
    tags Array(String),           -- Массив тегов
    prices Array(Decimal64(2)),   -- Массив цен по регионам
    images Array(String)          -- Массив URL изображений
);

-- Работа с массивами
INSERT INTO products VALUES
(1, 'Laptop', ['electronics', 'computer'], [999.99, 1099.99], ['img1.jpg', 'img2.jpg']);

-- Запросы к массивам
SELECT * FROM products
WHERE arrayExists(x -> x = 'electronics', tags);  -- Есть ли элемент в массиве

SELECT arrayCount(x -> x > 1000, prices) as expensive_count  -- Количество дорогих цен
FROM products;
```

#### Кортежи (**Tuples**)

```sql
Tuple(T1, T2, ...)  -- Фиксированный набор разнотипных значений

-- Примеры
CREATE TABLE coordinates
(
    point Tuple(Float64, Float64),        -- (latitude, longitude)
    bounds Tuple(Float64, Float64, Float64, Float64),  -- (min_lat, max_lat, min_lng, max_lng)
    person Tuple(String, UInt8, String)   -- (name, age, city)
);

-- Доступ к элементам кортежа
SELECT
    point.1 as latitude,    -- Первый элемент (индексация с 1)
    point.2 as longitude,
    person.1 as name,
    person.2 as age
FROM coordinates;
```

#### **Nullable** типы

```sql
Nullable(T)  -- Значение типа T или NULL

-- Примеры
CREATE TABLE user_profiles
(
    user_id UInt64,
    name String,
    email Nullable(String),        -- Email может быть NULL
    phone Nullable(String),        -- Телефон может быть NULL
    age Nullable(UInt8),           -- Возраст может быть NULL
    last_login Nullable(DateTime)  -- Последний вход может быть NULL
);

-- Работа с NULL
SELECT * FROM user_profiles
WHERE email IS NOT NULL;  -- Исключить записи с NULL email

SELECT
    count() as total_users,
    count(email) as users_with_email,    -- Считает только не-NULL
    count(phone) as users_with_phone
FROM user_profiles;
```

#### **Enum** типы

```sql
Enum8('value1' = 1, 'value2' = 2, ...)   -- 1 байт, до 256 значений
Enum16('value1' = 1, 'value2' = 2, ...)  -- 2 байта, больше значений

-- Примеры
CREATE TABLE orders
(
    order_id UInt64,
    status Enum8(
        'pending' = 1,
        'confirmed' = 2,
        'shipped' = 3,
        'delivered' = 4,
        'cancelled' = 5
    ),
    priority Enum8(
        'low' = 1,
        'normal' = 2,
        'high' = 3,
        'urgent' = 4
    )
);

-- Использование
INSERT INTO orders VALUES
(1, 'confirmed', 'high'),
(2, 'pending', 'normal');
```

#### **Map** типы

```sql
Map(key_type, value_type)  -- Ассоциативный массив

-- Примеры
CREATE TABLE analytics
(
    event_id UInt64,
    user_id UInt64,
    timestamp DateTime,
    properties Map(String, String),        -- Произвольные свойства
    metrics Map(String, Float64),          -- Метрики с плавающей точкой
    counters Map(String, UInt64)           -- Счетчики
);

-- Работа с Map
INSERT INTO analytics VALUES
(1, 123, now(), {'source': 'web', 'browser': 'chrome'}, {'load_time': 2.5}, {'clicks': 10});

-- Запросы к Map
SELECT
    event_id,
    properties['source'] as source,
    properties['browser'] as browser,
    metrics['load_time'] as load_time
FROM analytics
WHERE has(properties, 'source');  -- Проверка наличия ключа
```

#### **Nested** типы

```sql
Nested(name String, value Int32)  -- Вложенная структура

-- Примеры
CREATE TABLE sensors
(
    sensor_id UInt64,
    location String,
    readings Nested(
        timestamp DateTime,
        temperature Float32,
        humidity UInt8
    )
);

-- Nested представляет собой массивы
-- readings.timestamp - Array(DateTime)
-- readings.temperature - Array(Float32)
-- readings.humidity - Array(UInt8)
```

## Оптимизация таблиц

### Выбор подходящего движка

```sql
-- Для аналитики с агрегацией
CREATE TABLE sales_daily
(
    date Date,
    product_id UInt64,
    category String,
    sales_amount Decimal64(2),
    quantity UInt32
)
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (category, product_id, date);

-- Для событий с дедупликацией
CREATE TABLE user_actions
(
    user_id UInt64,
    action_type String,
    timestamp DateTime,
    session_id String,
    version UInt64
)
ENGINE = ReplacingMergeTree(version)
PARTITION BY toYYYYMMDD(timestamp)
ORDER BY (user_id, timestamp);

-- Для кэширования
CREATE TABLE cache.user_sessions
(
    session_id String,
    user_id UInt64,
    data String,
    expires_at DateTime
)
ENGINE = MergeTree()
ORDER BY session_id
TTL expires_at;  -- Автоматическая очистка
```

### Настройка партиционирования

```sql
-- Партиционирование по времени
PARTITION BY toYYYYMM(timestamp)      -- По месяцам
PARTITION BY toYYYYMMDD(timestamp)    -- По дням
PARTITION BY toWeek(timestamp)        -- По неделям

-- Партиционирование по значению
PARTITION BY user_id % 100            -- По диапазонам пользователей
PARTITION BY city                     -- По городам

-- Составное партиционирование
PARTITION BY (toYYYYMM(date), region) -- По месяцу и региону
```

### Настройка первичного ключа

```sql
-- Простой первичный ключ
ORDER BY id

-- Составной первичный ключ
ORDER BY (date, user_id, timestamp)

-- Оптимальный порядок для запросов
ORDER BY (tenant_id, date, category)  -- tenant_id для фильтрации, date для диапазонов

-- С сортировкой для аналитики
ORDER BY (country, city, date, category)
```

### Настройки производительности

```sql
-- Размер гранулы индекса
SETTINGS index_granularity = 8192      -- По умолчанию
SETTINGS index_granularity = 16384     -- Для больших таблиц

-- Сжатие
SETTINGS compression_codec = 'LZ4'     -- Быстрое сжатие
SETTINGS compression_codec = 'ZSTD(3)' -- Лучшее сжатие

-- TTL настройки
TTL timestamp + INTERVAL 90 DAY         -- Хранение 90 дней
TTL timestamp + INTERVAL 1 YEAR DELETE, -- Удаление через год
    timestamp + INTERVAL 30 DAY TO DISK 'ssd',  -- Перемещение на SSD
    timestamp + INTERVAL 90 DAY TO VOLUME 'cold' -- Перемещение в холодное хранилище
```

## Управление таблицами

### Просмотр информации о таблицах

```sql
-- Просмотр всех таблиц
SHOW TABLES;

-- Детальная информация
DESCRIBE TABLE table_name;

-- Структура таблицы
SHOW CREATE TABLE table_name;

-- Размер таблицы
SELECT
    table,
    formatReadableSize(sum(bytes)) as size,
    formatReadableSize(sum(bytes_on_disk)) as compressed_size,
    count() as parts_count
FROM system.parts
WHERE database = 'mydb' AND table = 'mytable'
GROUP BY table;
```

### Модификация таблиц

```sql
-- Добавление столбца
ALTER TABLE users ADD COLUMN last_login DateTime;

-- Удаление столбца
ALTER TABLE users DROP COLUMN old_column;

-- Изменение типа столбца
ALTER TABLE users MODIFY COLUMN age UInt16;

-- Добавление индекса
ALTER TABLE events ADD INDEX idx_type event_type TYPE bloom_filter(0.01) GRANULARITY 1;

-- Изменение TTL
ALTER TABLE logs MODIFY TTL timestamp + INTERVAL 30 DAY;

-- Изменение партиционирования (требуется пересоздание)
-- CREATE TABLE new_table AS old_table с новыми настройками
```

### Очистка и обслуживание

```sql
-- Очистка таблицы
TRUNCATE TABLE table_name;

-- Удаление таблицы
DROP TABLE IF EXISTS table_name;

-- Оптимизация таблицы (слияние партиций)
OPTIMIZE TABLE table_name;

-- Восстановление после сбоя
SYSTEM RESTORE REPLICA table_name;
```

## Лучшие практики

### Выбор типов данных

1. **Используйте минимально достаточный тип**
   ```sql
   -- Вместо UInt64 для возраста
   age UInt8,  -- 0-255 лет достаточно

   -- Вместо String для категорий
   category Enum8('A'=1, 'B'=2, 'C'=3)
   ```

2. **Предпочитайте `Nullable` только когда нужно**
   ```sql
   -- Вместо nullable полей используйте отдельные таблицы или значения по умолчанию
   email String DEFAULT '',  -- Пустая строка вместо NULL
   ```

3. **Оптимизируйте для сжатия**
   ```sql
   -- Группируйте похожие столбцы для лучшего сжатия
   -- UInt32 поля вместе, String поля вместе
   ```

### Проектирование схемы

1. **Денормализация для аналитики**
   ```sql
   -- В ClickHouse нормально дублировать данные
   -- для ускорения запросов
   CREATE TABLE user_events_denormalized
   (
       user_id UInt64,
       user_name String,        -- Дублирование из таблицы users
       event_type String,
       event_data String
   );
   ```

2. **Правильное партиционирование**
   ```sql
   -- Партиционируйте по времени для временных данных
   PARTITION BY toYYYYMM(timestamp)

   -- Ограничьте количество партиций
   -- Слишком много партиций = overhead
   ```

3. **Эффективные первичные ключи**
   ```sql
   -- Ключ должен соответствовать паттернам запросов
   ORDER BY (tenant_id, date, category)  -- tenant_id для фильтрации, date для диапазонов
   ```

### Производительность

1. **Используйте подходящие движки**
   ```sql
   -- SummingMergeTree для счетчиков
   -- ReplacingMergeTree для профилей
   -- AggregatingMergeTree для предварительных агрегатов
   ```

2. **Оптимизируйте вставку**
   ```sql
   -- Вставляйте данные батчами
   INSERT INTO table SELECT ... FROM source

   -- Используйте буферные таблицы для частых вставок
   ```

3. **Мониторьте и оптимизируйте**
   ```sql
   -- Проверяйте эффективность запросов
   SELECT * FROM system.query_log WHERE query_duration_ms > 1000

   -- Анализируйте использование диска
   SELECT * FROM system.parts WHERE table = 'mytable'
   ```

**Выбор правильной структуры таблиц и движков критически важен для производительности **ClickHouse**. Основные принципы:**

### Ключевые решения:

1. **Движок таблицы** определяет поведение и возможности
2. **Типы данных** влияют на хранение и производительность
3. **Партиционирование** определяет масштабируемость
4. **Первичный ключ** влияет на скорость запросов

### Рекомендации:

- **MergeTree** для большинства аналитических задач
- **Партиционирование по времени** для временных данных
- **Минимально достаточные типы данных**
- **Денормализация** для ускорения запросов
- **Регулярное обслуживание** таблиц

### Следующие шаги:

- **Изучите запросы** для выбора оптимальной схемы
- **Тестируйте производительность** разных конфигураций
- **Мониторьте использование** ресурсов
- **Планируйте рост** данных

## Полезные ссылки

### Официальная документация
- [Table Engines](https://clickhouse.com/docs/en/engines/table-engines)
- [Data Types](https://clickhouse.com/docs/en/sql-reference/data-types)
- [Table Design](https://clickhouse.com/docs/en/guides/sre/configuring-schema)

### Руководства
- [Schema Design](https://clickhouse.com/docs/en/guides/best-practices)
- [Performance Optimization](https://clickhouse.com/docs/en/operations/optimizing-performance)

### Примеры
- [ClickHouse Examples](https://clickhouse.com/docs/en/getting-started/tutorial)
- [Real-world Schemas](https://clickhouse.com/docs/en/guides/best-practices)


**Следующие темы:**
- [Запросы и аналитика](clickhouse-queries.md)
- [Индексы и оптимизация](clickhouse-indexes.md)

