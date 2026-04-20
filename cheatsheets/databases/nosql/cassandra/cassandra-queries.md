---
title: "Cassandra: CQL запросы и оптимизация — Полное руководство по языку запросов"
description: "Комплексное руководство по Cassandra Query Language (CQL): синтаксис, оптимизация запросов, индексы и best practices для эффективной работы с данными."
tags:
  - databases
  - nosql
  - cassandra-queries
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Cassandra: CQL запросы и оптимизация — Полное руководство по языку запросов

Комплексное руководство по **Cassandra Query Language** (CQL): синтаксис, оптимизация запросов, индексы и **best practices** для эффективной работы с данными.

## Полезные ссылки

### Официальная документация
- [CQL Reference](https://cassandra.apache.org/doc/latest/cql/)
- [CQL Commands](https://cassandra.apache.org/doc/latest/cql/ddl.html)
- [Querying Data](https://cassandra.apache.org/doc/latest/cql/dml.html)

### Дизайн и оптимизация
- [Query Best Practices](https://cassandra.apache.org/doc/latest/cassandra/operating/read_tuning.html)
- [CQL Performance Tuning](https://cassandra.apache.org/doc/latest/cassandra/operating/read_tuning.html)
- [Cassandra Query Patterns](https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html)

### Инструменты
- [DataStax Studio](https://docs.datastax.com/en/studio/)
- [Cassandra Query Language Shell (cqlsh)](https://cassandra.apache.org/doc/latest/cassandra/tools/cqlsh.html)
- [Cassandra Python Driver](https://docs.datastax.com/en/developer/python-driver/)

### См. также
- [[cassandra-basics|Основы]] — **Cassandra**
- [[cassandra-data-modeling|Моделирование]] — моделирование данных
- [[cassandra-clustering|Кластеризация]] — кластеризация и масштабирование
- [[cassandra-performance|Производительность]] — производительность и оптимизация

## Содержание

- [Основы CQL](#основы-cql)
  - [Структура CQL запросов](#структура-cql-запросов)
    - [Синтаксис и команды](#синтаксис-и-команды)
    - [Case sensitivity](#case-sensitivity)
  - [Подключение и сессия](#подключение-и-сессия)
    - [Через cqlsh](#через-cqlsh)
    - [Конфигурация cqlsh](#конфигурация-cqlsh)
- [Типы данных и литералы](#типы-данных-и-литералы)
  - [Встроенные типы данных](#встроенные-типы-данных)
    - [Примитивные типы](#примитивные-типы)
    - [Коллекции](#коллекции)
    - [Пользовательские типы (UDT)](#пользовательские-типы-udt)
  - [Литералы и константы](#литералы-и-константы)
    - [Строковые литералы](#строковые-литералы)
    - [Числовые литералы](#числовые-литералы)
    - [Специальные значения](#специальные-значения)
- [Операции CRUD](#операции-crud)
  - [Create (INSERT)](#create-insert)
    - [Базовые вставки](#базовые-вставки)
    - [Вставка коллекций](#вставка-коллекций)
  - [Read (SELECT)](#read-select)
    - [Базовые запросы](#базовые-запросы)
    - [Фильтрация по первичному ключу](#фильтрация-по-первичному-ключу)
  - [Update (UPDATE)](#update-update)
    - [Базовые обновления](#базовые-обновления)
    - [Обновление коллекций](#обновление-коллекций)
    - [Условные обновления](#условные-обновления)
  - [Delete (DELETE)](#delete-delete)
    - [Базовые удаления](#базовые-удаления)
    - [Условные удаления](#условные-удаления)
- [Запросы SELECT](#запросы-select)
  - [Продвинутые SELECT запросы](#продвинутые-select-запросы)
    - [Сортировка и ограничения](#сортировка-и-ограничения)
    - [Агрегации](#агрегации)
    - [Функции даты и времени](#функции-даты-и-времени)
  - [Работа с JSON](#работа-с-json)
    - [JSON вставка и запросы](#json-вставка-и-запросы)
- [Фильтрация и условия WHERE](#фильтрация-и-условия-where)
  - [Операторы сравнения](#операторы-сравнения)
    - [Базовые операторы](#базовые-операторы)
    - [Текстовые операторы](#текстовые-операторы)
  - [Логические операторы](#логические-операторы)
    - [AND/OR/NOT](#andornot)
  - [Специальные операторы](#специальные-операторы)
    - [Для коллекций](#для-коллекций)
    - [Для UDT](#для-udt)
- [Сортировка ORDER BY](#сортировка-order-by)
  - [Порядок сортировки](#порядок-сортировки)
    - [По clustering keys](#по-clustering-keys)
    - [Сортировка по не-clustering колонкам](#сортировка-по-не-clustering-колонкам)
  - [Практические примеры сортировки](#практические-примеры-сортировки)
    - [Временные ряды](#временные-ряды)
    - [Ранжирование](#ранжирование)
- [Ограничения LIMIT и пейджинг](#ограничения-limit-и-пейджинг)
  - [LIMIT оператор](#limit-оператор)
    - [Базовое использование](#базовое-использование)
    - [Практические примеры](#практические-примеры)
  - [Пейджинг (Paging)](#пейджинг-paging)
    - [Автоматический пейджинг](#автоматический-пейджинг)
    - [Ручной пейджинг с состоянием](#ручной-пейджинг-с-состоянием)
- [Агрегации и функции](#агрегации-и-функции)
  - [Встроенные агрегатные функции](#встроенные-агрегатные-функции)
    - [Базовые агрегации](#базовые-агрегации)
    - [Агрегации с группировкой](#агрегации-с-группировкой)
  - [Математические функции](#математические-функции)
    - [Арифметические операции](#арифметические-операции)
    - [Специализированные функции](#специализированные-функции)
  - [Пользовательские агрегаты](#пользовательские-агрегаты)
    - [Создание кастомных агрегатов](#создание-кастомных-агрегатов)
- [Работа с коллекциями](#работа-с-коллекциями)
  - [Операции со списками (LIST)](#операции-со-списками-list)
    - [Создание и вставка](#создание-и-вставка)
    - [Запросы со списками](#запросы-со-списками)
  - [Операции с множествами (SET)](#операции-с-множествами-set)
    - [Работа с множествами](#работа-с-множествами)
    - [Запросы с множествами](#запросы-с-множествами)
  - [Операции со словарями (MAP)](#операции-со-словарями-map)
    - [Работа со словарями](#работа-со-словарями)
    - [Запросы со словарями](#запросы-со-словарями)
- [Пользовательские функции и агрегаты](#пользовательские-функции-и-агрегаты)
  - [Создание функций](#создание-функций)
    - [Скалярные функции](#скалярные-функции)
    - [Функции агрегации](#функции-агрегации)
    - [Функции для работы с коллекциями](#функции-для-работы-с-коллекциями)
- [Пакетные операции](#пакетные-операции)
  - [BATCH запросы](#batch-запросы)
    - [Базовые пакеты](#базовые-пакеты)
    - [Условные пакеты](#условные-пакеты)
  - [Логгированные и не логгированные пакеты](#логгированные-и-не-логгированные-пакеты)
    - [Типы пакетов](#типы-пакетов)
  - [Практическое использование пакетов](#практическое-использование-пакетов)
- [TTL и временные данные](#ttl-и-временные-данные)
  - [Time To Live (TTL)](#time-to-live-ttl)
    - [Установка TTL](#установка-ttl)
    - [Проверка TTL](#проверка-ttl)
    - [Управление TTL](#управление-ttl)
  - [Временные ряды с TTL](#временные-ряды-с-ttl)
    - [Автоматическая очистка старых данных](#автоматическая-очистка-старых-данных)
    - [Продление TTL](#продление-ttl)
- [Оптимизация запросов](#оптимизация-запросов)
  - [EXPLAIN план запроса](#explain-план-запроса)
    - [Анализ плана выполнения](#анализ-плана-выполнения)
    - [Интерпретация результатов EXPLAIN](#интерпретация-результатов-explain)
  - [Оптимизация производительности](#оптимизация-производительности)
    - [1. Использование первичных ключей](#1-использование-первичных-ключей)
    - [2. Избегание ALLOW FILTERING](#2-избегание-allow-filtering)
    - [3. Оптимизация LIMIT и пейджинга](#3-оптимизация-limit-и-пейджинга)
  - [Кэширование и материализованные представления](#кэширование-и-материализованные-представления)
    - [Создание MV для оптимизации](#создание-mv-для-оптимизации)
- [Индексы и поиск](#индексы-и-поиск)
  - [Вторичные индексы](#вторичные-индексы)
    - [Создание и использование](#создание-и-использование)
    - [SASI индексы для текста](#sasi-индексы-для-текста)
  - [Поисковые паттерны](#поисковые-паттерны)
    - [Таблицы для поиска](#таблицы-для-поиска)
    - [Геопоиск](#геопоиск)
- [Мониторинг и профилирование](#мониторинг-и-профилирование)
  - [Метрики запросов](#метрики-запросов)
    - [Сбор статистики](#сбор-статистики)
    - [Инструменты мониторинга](#инструменты-мониторинга)
- [Лучшие практики](#лучшие-практики)
  - [Проектирование запросов](#проектирование-запросов)
    - [1. Query-First Design](#1-query-first-design)
    - [2. Оптимизация первичных ключей](#2-оптимизация-первичных-ключей)
    - [3. Управление индексами](#3-управление-индексами)
  - [Производительность и масштабируемость](#производительность-и-масштабируемость)
    - [1. Размер данных](#1-размер-данных)
    - [2. Пейджинг и ограничения](#2-пейджинг-и-ограничения)
    - [3. Пакетные операции](#3-пакетные-операции)
  - [Безопасность и надежность](#безопасность-и-надежность)
    - [1. Управление доступом](#1-управление-доступом)
    - [2. Обработка ошибок](#2-обработка-ошибок)
    - [3. Резервное копирование](#3-резервное-копирование)
  - [Мониторинг и поддержка](#мониторинг-и-поддержка)
    - [1. Ключевые метрики](#1-ключевые-метрики)
    - [2. Регулярное обслуживание](#2-регулярное-обслуживание)
    - [3. Профилирование](#3-профилирование)
  - [Основные принципы CQL:](#основные-принципы-cql)
  - [Ключевые возможности:](#ключевые-возможности)
  - [Стратегии оптимизации:](#стратегии-оптимизации)
  - [Вызовы и решения:](#вызовы-и-решения)
  - [Лучшие практики:](#лучшие-практики-1)
- [Решение проблем](#решение-проблем)

## Основы CQL

### Структура CQL запросов

#### Синтаксис и команды

Примеры базовых команд **CQL** для работы с ключевыми пространствами и таблицами.

```cql
-- Комментарии в CQL
-- Однострочный комментарий

/*
Многострочный
комментарий
*/

-- Основные команды CQL
CREATE KEYSPACE myapp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
USE myapp;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email TEXT,
    name TEXT,
    created_at TIMESTAMP
);

-- Вставка данных
INSERT INTO users (id, email, name, created_at) VALUES (uuid(), 'user@example.com', 'John Doe', toTimestamp(now()));

-- Запрос данных
SELECT id, email, name FROM users WHERE id = uuid();

-- Обновление
UPDATE users SET name = 'Jane Doe' WHERE id = uuid();

-- Удаление
DELETE FROM users WHERE id = uuid();

-- Удаление всей таблицы
TRUNCATE users;

-- Удаление таблицы
DROP TABLE users;

-- Удаление keyspace
DROP KEYSPACE myapp;
```

#### Case sensitivity
```cql
-- CQL регистронезависимый для ключевых слов
select * from users;
SELECT * FROM USERS;
Select * From Users;

-- Но регистрозависимый для имен колонок и таблиц
CREATE TABLE "UserData" (
    "UserID" UUID PRIMARY KEY,
    "userName" TEXT
);

-- Запрос с учетом регистра
SELECT "UserID", "userName" FROM "UserData";
```

### Подключение и сессия

#### Через cqlsh
```bash
# Подключение к локальному кластеру
cqlsh

# Подключение к удаленному кластеру
cqlsh 192.168.1.100 9042 -u cassandra -p password

# Выполнение файла с запросами
cqlsh -f schema.cql

# Выполнение одиночного запроса
cqlsh -e "SELECT * FROM users LIMIT 5;"

# Подключение с SSL
cqlsh --ssl --cqlshrc ~/.cassandra/cqlshrc
```

#### Конфигурация cqlsh
```bash
# Создание конфигурационного файла ~/.cassandra/cqlshrc
[authentication]
username = cassandra
password = cassandra

[connection]
hostname = 127.0.0.1
port = 9042

[ssl]
certfile = ~/ssl/cassandra.crt
keyfile = ~/ssl/cassandra.key
validate = true
```

## Типы данных и литералы

### Встроенные типы данных

#### Примитивные типы
```cql
CREATE TABLE data_types_example (
    id UUID PRIMARY KEY,

    -- Целые числа
    small_int SMALLINT,           -- 16-bit signed integer (-32,768 to 32,767)
    regular_int INT,              -- 32-bit signed integer
    big_int BIGINT,               -- 64-bit signed integer
    var_int VARINT,               -- Variable-precision integer

    -- Числа с плавающей точкой
    float_num FLOAT,              -- 32-bit IEEE-754 floating point
    double_num DOUBLE,            -- 64-bit IEEE-754 floating point
    decimal_num DECIMAL,          -- Variable-precision decimal

    -- Логический тип
    is_active BOOLEAN,            -- true/false

    -- Строки
    name TEXT,                    -- UTF-8 encoded string (unlimited)
    short_name VARCHAR,           -- Alias for TEXT
    ascii_name ASCII,             -- ASCII character string

    -- Время и дата
    created_at TIMESTAMP,         -- Date and time with millisecond precision
    birth_date DATE,              -- Date without time
    event_time TIME,              -- Time without date

    -- UUID и идентификаторы
    user_id UUID,                 -- Standard UUID (36 characters)
    time_uuid TIMEUUID,           -- Version 1 UUID with timestamp

    -- Бинарные данные
    avatar BLOB,                  -- Arbitrary bytes

    -- Длительность
    duration DURATION             -- Duration of time
);
```

#### Коллекции
```cql
CREATE TABLE collections_example (
    id UUID PRIMARY KEY,

    -- Список (упорядоченный)
    tags LIST<TEXT>,
    scores LIST<INT>,

    -- Множество (неупорядоченное, уникальные значения)
    categories SET<TEXT>,
    permissions SET<TEXT>,

    -- Словарь (ключ-значение)
    metadata MAP<TEXT, TEXT>,
    settings MAP<TEXT, BOOLEAN>,
    counters MAP<TEXT, INT>,

    -- Вложенные коллекции
    user_preferences MAP<TEXT, SET<TEXT>>,
    product_specs MAP<TEXT, LIST<TEXT>>
);
```

#### Пользовательские типы (UDT)
```cql
-- Создание пользовательского типа
CREATE TYPE address (
    street TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    country TEXT
);

CREATE TYPE contact_info (
    email TEXT,
    phone TEXT,
    website TEXT
);

-- Использование UDT в таблице
CREATE TABLE companies (
    company_id UUID PRIMARY KEY,
    name TEXT,
    hq_address FROZEN<address>,
    contacts FROZEN<contact_info>,
    offices LIST<FROZEN<address>>
);

-- Вставка данных с UDT
INSERT INTO companies (
    company_id, name, hq_address, contacts, offices
) VALUES (
    uuid(),
    'TechCorp',
    {street: '123 Main St', city: 'Anytown', state: 'CA', zip_code: '12345', country: 'USA'},
    {email: 'info@techcorp.com', phone: '+1-555-0123', website: 'www.techcorp.com'},
    [
        {street: '456 Office Blvd', city: 'Anytown', state: 'CA', zip_code: '12346', country: 'USA'}
    ]
);
```

### Литералы и константы

#### Строковые литералы
```cql
-- Одинарные кавычки
INSERT INTO users (name) VALUES ('John Doe');

-- Экранирование одинарных кавычек
INSERT INTO users (name) VALUES ('John''s Doe');

-- Многострочные строки (не поддерживаются в CQL)
-- Используйте конкатенацию или подготовленные запросы
```

#### Числовые литералы
```cql
-- Целые числа
INSERT INTO metrics (value) VALUES (42);
INSERT INTO metrics (value) VALUES (-123);

-- Числа с плавающей точкой
INSERT INTO sensors (temperature) VALUES (23.5);
INSERT INTO sensors (temperature) VALUES (-15.7e2);  -- Научная нотация

-- Десятичные числа
INSERT INTO finances (amount) VALUES (123.45);
INSERT INTO finances (amount) VALUES (999999.99);
```

#### Специальные значения
```cql
-- NULL значения
INSERT INTO users (name, email) VALUES ('John', null);

-- Булевы значения
INSERT INTO settings (enabled) VALUES (true);
INSERT INTO settings (enabled) VALUES (false);

-- UUID и TIMEUUID
INSERT INTO events (id, user_id) VALUES (uuid(), uuid());
INSERT INTO events (id, user_id) VALUES (now(), uuid());  -- TIMEUUID

-- Текущая дата и время
INSERT INTO logs (timestamp) VALUES (toTimestamp(now()));
INSERT INTO logs (date_only) VALUES (toDate(now()));
INSERT INTO logs (time_only) VALUES (toTime(now()));
```

## Операции CRUD

### Create (INSERT)

#### Базовые вставки
```cql
-- Полная вставка
INSERT INTO users (id, email, name, created_at)
VALUES (uuid(), 'john@example.com', 'John Doe', toTimestamp(now()));

-- Частичная вставка (остальные колонки получат NULL)
INSERT INTO users (id, email) VALUES (uuid(), 'jane@example.com');

-- Вставка с TTL (Time To Live)
INSERT INTO sessions (session_id, user_id, data)
VALUES (uuid(), uuid(), 'session_data')
USING TTL 3600;  -- Автоматическое удаление через 1 час

-- Вставка с TIMESTAMP
INSERT INTO audit_log (id, action, user_id, timestamp)
VALUES (uuid(), 'login', uuid(), toTimestamp(now()))
USING TIMESTAMP 1640995200000000;  -- Конкретное время в микросекундах
```

#### Вставка коллекций
```cql
-- Вставка списка
INSERT INTO products (id, tags) VALUES (uuid(), ['electronics', 'gadgets']);

-- Вставка множества
INSERT INTO users (id, roles) VALUES (uuid(), {'admin', 'user'});

-- Вставка словаря
INSERT INTO configs (id, settings) VALUES (
    uuid(),
    {'theme': 'dark', 'notifications': 'true', 'language': 'en'}
);

-- Добавление элементов в коллекции
UPDATE products SET tags = tags + ['new_tag'] WHERE id = uuid();
UPDATE users SET roles = roles + {'moderator'} WHERE id = uuid();
UPDATE configs SET settings = settings + {'auto_save': 'false'} WHERE id = uuid();
```

### Read (SELECT)

#### Базовые запросы
```cql
-- Выбор всех колонок
SELECT * FROM users;

-- Выбор конкретных колонок
SELECT id, email, name FROM users;

-- С псевдонимом таблицы
SELECT u.id, u.email FROM users u;

-- С псевдонимами колонок
SELECT id AS user_id, email AS user_email FROM users;
```

#### Фильтрация по первичному ключу
```cql
-- Запрос по partition key
SELECT * FROM users WHERE id = uuid();

-- Запрос по composite partition key
SELECT * FROM user_posts WHERE user_id = uuid();

-- Запрос по clustering columns
SELECT * FROM user_posts
WHERE user_id = uuid() AND post_id > uuid();

-- Диапазонный запрос по clustering key
SELECT * FROM sensor_readings
WHERE sensor_id = uuid()
AND timestamp >= '2023-01-01' AND timestamp <= '2023-12-31';
```

### Update (UPDATE)

#### Базовые обновления
```cql
-- Простое обновление
UPDATE users SET name = 'Jane Smith' WHERE id = uuid();

-- Обновление нескольких колонок
UPDATE users SET
    name = 'Jane Smith',
    email = 'jane.smith@example.com'
WHERE id = uuid();

-- Условное обновление (только если колонка имеет определенное значение)
UPDATE counters SET value = 10 WHERE id = uuid() IF value = 5;
```

#### Обновление коллекций
```cql
-- Замена всего списка
UPDATE products SET tags = ['new', 'tags'] WHERE id = uuid();

-- Добавление элементов в список
UPDATE products SET tags = tags + ['additional_tag'] WHERE id = uuid();

-- Удаление элементов из списка
UPDATE products SET tags = tags - ['old_tag'] WHERE id = uuid();

-- Обновление элементов по индексу
UPDATE products SET tags[0] = 'first_tag' WHERE id = uuid();

-- Обновление словаря
UPDATE configs SET settings['theme'] = 'light' WHERE id = uuid();
UPDATE configs SET settings = settings + {'new_setting': 'value'} WHERE id = uuid();
UPDATE configs SET settings = settings - {'old_setting'} WHERE id = uuid();
```

#### Условные обновления
```cql
-- Обновление только если условие истинно
UPDATE inventory
SET quantity = 50
WHERE product_id = uuid() IF quantity > 10;

-- Обновление с несколькими условиями
UPDATE user_profiles
SET last_login = toTimestamp(now()), login_count = login_count + 1
WHERE user_id = uuid()
IF last_login < toTimestamp(now()) - 1d;  -- Только если последний логин был более дня назад
```

### Delete (DELETE)

#### Базовые удаления
```cql
-- Удаление всей строки
DELETE FROM users WHERE id = uuid();

-- Удаление конкретных колонок (установка в NULL)
DELETE email FROM users WHERE id = uuid();

-- Удаление нескольких колонок
DELETE email, phone FROM contacts WHERE id = uuid();
```

#### Условные удаления
```cql
-- Удаление только если условие истинно
DELETE FROM sessions WHERE session_id = uuid() IF ttl(data) < 300;

-- Удаление элементов коллекций
DELETE tags[1] FROM products WHERE id = uuid();  -- Удаление по индексу
DELETE tags FROM products WHERE id = uuid() IF tags CONTAINS 'deprecated';

-- Удаление из словарей
DELETE settings['old_key'] FROM configs WHERE id = uuid();
```

## Запросы SELECT

### Продвинутые SELECT запросы

#### Сортировка и ограничения
```cql
-- Сортировка по clustering key (только в порядке, определенном в таблице)
SELECT * FROM user_posts
WHERE user_id = uuid()
ORDER BY post_id DESC;

-- Ограничение количества результатов
SELECT * FROM products LIMIT 10;

-- Комбинация сортировки и ограничения
SELECT * FROM user_activity
WHERE user_id = uuid()
ORDER BY timestamp DESC
LIMIT 20;
```

#### Агрегации
```cql
-- Подсчет количества записей
SELECT COUNT(*) FROM users;

-- Агрегации с группировкой (нужен ALLOW FILTERING для не-key колонок)
SELECT category, COUNT(*) as count
FROM products
WHERE category IN ('electronics', 'books')
GROUP BY category ALLOW FILTERING;

-- Математические функции
SELECT
    COUNT(*) as total,
    MAX(price) as max_price,
    MIN(price) as min_price,
    AVG(price) as avg_price,
    SUM(quantity) as total_quantity
FROM products;
```

#### Функции даты и времени
```cql
-- Функции работы с датой/временем
SELECT
    id,
    toDate(created_at) as date_only,
    toTime(created_at) as time_only,
    dateOf(created_at) as date_part,
    timeOf(created_at) as time_part,
    unixTimestampOf(created_at) as unix_ts
FROM events;

-- Вычисление возраста
SELECT id, name, toTimestamp(now()) - created_at as account_age
FROM users;

-- Форматирование дат
SELECT id,
    toTimestamp(created_at) as timestamp,
    formatdate(created_at, 'yyyy-MM-dd HH:mm:ss') as formatted
FROM logs;
```

### Работа с JSON

#### JSON вставка и запросы
```cql
-- Вставка JSON данных
INSERT INTO user_profiles JSON '{
    "user_id": "550e8400-e29b-41d4-a716-446655440000",
    "profile": {
        "name": "John Doe",
        "age": 30,
        "preferences": {
            "theme": "dark",
            "notifications": true
        }
    }
}';

-- Запрос с JSON выводом
SELECT JSON * FROM user_profiles WHERE user_id = uuid();

-- Запрос конкретных полей в JSON
SELECT JSON profile FROM user_profiles WHERE user_id = uuid();

-- Обновление через JSON
UPDATE user_profiles SET profile = fromJson('{
    "name": "Jane Doe",
    "age": 31,
    "preferences": {
        "theme": "light",
        "notifications": false
    }
}') WHERE user_id = uuid();
```

## Фильтрация и условия WHERE

### Операторы сравнения

#### Базовые операторы
```cql
-- Равенство (только для partition keys и некоторых clustering keys)
SELECT * FROM users WHERE id = uuid();

-- Неравенство (требует ALLOW FILTERING)
SELECT * FROM products WHERE price > 100 ALLOW FILTERING;

-- Диапазоны (для clustering keys)
SELECT * FROM sensor_data
WHERE sensor_id = uuid()
AND timestamp >= '2023-01-01 00:00:00'
AND timestamp < '2023-02-01 00:00:00';

-- IN оператор
SELECT * FROM users WHERE id IN (uuid(), uuid(), uuid());

-- CONTAINS для коллекций (требует ALLOW FILTERING)
SELECT * FROM products WHERE tags CONTAINS 'electronics' ALLOW FILTERING;
```

#### Текстовые операторы
```cql
-- LIKE оператор (требует SASI индекса)
SELECT * FROM products WHERE name LIKE 'Laptop%' ALLOW FILTERING;

-- Текстовый поиск с SASI
CREATE CUSTOM INDEX product_name_sasi
ON products(name)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {'mode': 'PREFIX'};

SELECT * FROM products WHERE name LIKE 'Laptop%';
```

### Логические операторы

#### AND/`OR`/NOT
```cql
-- AND оператор (неявный в WHERE)
SELECT * FROM orders
WHERE user_id = uuid()
AND status = 'completed'
AND total_amount > 50;

-- IN с несколькими колонками
SELECT * FROM user_permissions
WHERE user_id = uuid()
AND permission IN ('read', 'write', 'admin');

-- Комплексные условия (требует ALLOW FILTERING)
SELECT * FROM products
WHERE category = 'electronics'
AND price BETWEEN 100 AND 500
AND tags CONTAINS 'wireless'
ALLOW FILTERING;
```

### Специальные операторы

#### Для коллекций
```cql
-- CONTAINS для списков и множеств
SELECT * FROM products WHERE tags CONTAINS 'wireless' ALLOW FILTERING;

-- CONTAINS KEY для словарей
SELECT * FROM configs WHERE settings CONTAINS KEY 'theme' ALLOW FILTERING;

-- CONTAINS для значений в словарях (требует ALLOW FILTERING)
SELECT * FROM configs WHERE settings CONTAINS 'dark' ALLOW FILTERING;
```

#### Для UDT
```cql
-- Доступ к полям UDT
SELECT * FROM companies
WHERE hq_address.city = 'San Francisco' ALLOW FILTERING;

-- Вложенные UDT
SELECT * FROM complex_data
WHERE nested_udt.field.subfield = 'value' ALLOW FILTERING;
```

## Сортировка ORDER `BY`

### Порядок сортировки

#### По clustering keys
```cql
-- Сортировка в порядке, определенном в таблице
CREATE TABLE user_posts (
    user_id UUID,
    post_id TIMEUUID,
    content TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id), post_id, created_at)
) WITH CLUSTERING ORDER BY (post_id DESC, created_at DESC);

-- Запрос будет использовать порядок таблицы
SELECT * FROM user_posts WHERE user_id = uuid();

-- Явная сортировка (должна совпадать с определением таблицы)
SELECT * FROM user_posts
WHERE user_id = uuid()
ORDER BY post_id DESC, created_at DESC;
```

#### Сортировка по не-clustering колонкам
```cql
-- Требует ALLOW FILTERING и может быть неэффективным
SELECT * FROM products
WHERE category = 'electronics'
ORDER BY price DESC
LIMIT 10 ALLOW FILTERING;
```

### Практические примеры сортировки

#### Временные ряды
```cql
-- Последние события пользователя
SELECT * FROM user_events
WHERE user_id = uuid()
ORDER BY event_time DESC
LIMIT 50;

-- Старейшие записи для очистки
SELECT * FROM audit_log
WHERE date = '2023-01-01'
ORDER BY timestamp ASC
LIMIT 100;
```

#### Ранжирование
```cql
-- Топ продуктов по цене
SELECT product_id, name, price
FROM products
WHERE category = 'electronics'
ORDER BY price DESC
LIMIT 10 ALLOW FILTERING;

-- Самые активные пользователи
SELECT user_id, COUNT(*) as activity_count
FROM user_activity
GROUP BY user_id
ORDER BY activity_count DESC
LIMIT 20 ALLOW FILTERING;
```

## Ограничения LIMIT и пейджинг

### LIMIT оператор

#### Базовое использование
```cql
-- Ограничение количества результатов
SELECT * FROM users LIMIT 100;

-- Комбинация с WHERE и ORDER BY
SELECT * FROM user_posts
WHERE user_id = uuid()
ORDER BY created_at DESC
LIMIT 20;

-- Ограничение для производительности
SELECT * FROM large_table LIMIT 1000 ALLOW FILTERING;
```

#### Практические примеры
```cql
-- Последние сообщения
SELECT * FROM messages
WHERE chat_id = uuid()
ORDER BY timestamp DESC
LIMIT 50;

-- Топ результатов поиска
SELECT * FROM search_results
WHERE query = 'laptop'
ORDER BY relevance_score DESC
LIMIT 20 ALLOW FILTERING;

-- Случайная выборка
SELECT * FROM users LIMIT 100; -- Примерная случайная выборка
```

### Пейджинг (Paging)

#### Автоматический пейджинг
```cql
-- Cassandra автоматически разбивает результаты на страницы
-- Размер страницы по умолчанию: 5000 строк
SELECT * FROM large_table;

-- Явное указание размера страницы
SELECT * FROM users
WHERE user_id > uuid()
LIMIT 100 PER PARTITION;  -- Ограничение на партицию

-- Использование TOKEN для пейджинга
SELECT * FROM users
WHERE token(id) > token(uuid())
LIMIT 100;
```

#### Ручной пейджинг с состоянием

```java
@Service
public class PagedQueryService {

    @Autowired
    private CqlSession session;

    public List<User> getUsersWithPaging(UUID lastUserId, int pageSize) {
        String query = "SELECT id, email, name FROM users WHERE id > ? LIMIT ?";

        ResultSet rs = session.execute(query, lastUserId, pageSize);

        return rs.all().stream()
            .map(row -> new User(
                row.getUuid("id"),
                row.getString("email"),
                row.getString("name")
            ))
            .collect(Collectors.toList());
    }

    public PagedResult<User> getUsersPaged(ByteBuffer pagingState, int pageSize) {
        SimpleStatement statement = SimpleStatement.newInstance(
            "SELECT id, email, name FROM users"
        ).setPageSize(pageSize);

        if (pagingState != null) {
            statement = statement.setPagingState(pagingState);
        }

        ResultSet rs = session.execute(statement);

        List<User> users = rs.all().stream()
            .map(row -> new User(
                row.getUuid("id"),
                row.getString("email"),
                row.getString("name")
            ))
            .collect(Collectors.toList());

        return new PagedResult<>(users, rs.getExecutionInfo().getPagingState());
    }

    // Пейджинг по токенам для больших таблиц
    public List<User> getUsersByTokenRange(long startToken, long endToken, int limit) {
        String query = "SELECT id, email, name FROM users " +
                      "WHERE token(id) >= ? AND token(id) < ? LIMIT ?";

        ResultSet rs = session.execute(query, startToken, endToken, limit);

        return rs.all().stream()
            .map(row -> new User(
                row.getUuid("id"),
                row.getString("email"),
                row.getString("name")
            ))
            .collect(Collectors.toList());
    }

    // Расчет токенов для равномерного распределения
    public List<TokenRange> calculateTokenRanges(int numRanges) {
        List<TokenRange> ranges = new ArrayList<>();
        long minToken = Long.MIN_VALUE;
        long maxToken = Long.MAX_VALUE;
        long rangeSize = (maxToken - minToken) / numRanges;

        for (int i = 0; i < numRanges; i++) {
            long start = minToken + (i * rangeSize);
            long end = (i == numRanges - 1) ? maxToken : start + rangeSize;
            ranges.add(new TokenRange(start, end));
        }

        return ranges;
    }
}

class PagedResult<T> {
    private List<T> data;
    private ByteBuffer pagingState;
    private boolean hasMorePages;

    // constructor, getters, setters
}

class TokenRange {
    private long startToken;
    private long endToken;

    // constructor, getters, setters
}
```

## Агрегации и функции

### Встроенные агрегатные функции

#### Базовые агрегации
```cql
-- Количество записей
SELECT COUNT(*) FROM users;

-- Статистические функции
SELECT
    COUNT(*) as total_count,
    COUNT(price) as price_count,  -- Игнорирует NULL
    MAX(price) as max_price,
    MIN(price) as min_price,
    AVG(price) as avg_price,
    SUM(quantity) as total_quantity
FROM products;

-- Агрегации с условиями
SELECT
    AVG(price) as avg_price,
    MAX(price) as max_price,
    MIN(price) as min_price
FROM products
WHERE category = 'electronics';
```

#### Агрегации с группировкой
```cql
-- Группировка по partition key
SELECT category, COUNT(*) as product_count
FROM products_by_category
GROUP BY category;

-- Группировка по composite key
SELECT user_id, date, COUNT(*) as daily_posts
FROM user_posts_by_date
WHERE user_id = uuid()
GROUP BY user_id, date;

-- Множественные агрегации
SELECT
    category,
    COUNT(*) as total_products,
    AVG(price) as avg_price,
    MAX(price) as max_price
FROM products_by_category
GROUP BY category;
```

### Математические функции

#### Арифметические операции
```cql
-- Математические вычисления в SELECT
SELECT
    product_id,
    price,
    price * 1.2 as price_with_tax,
    price * quantity as total_value,
    ROUND(price, 2) as rounded_price
FROM order_items;

-- Вычисления с датами
SELECT
    user_id,
    created_at,
    toTimestamp(now()) - created_at as account_age_days,
    dateOf(toTimestamp(now()) - created_at) as age_date
FROM users;
```

#### Специализированные функции
```cql
-- Функции для работы с коллекциями
SELECT
    product_id,
    tags,
    size(tags) as tag_count,           -- Размер коллекции
    tags[0] as first_tag,              -- Доступ по индексу
    contains(tags, 'electronics') as is_electronic
FROM products;

-- Функции для работы с текстом
SELECT
    user_id,
    name,
    length(name) as name_length,
    substring(name, 1, 3) as name_prefix
FROM users;
```

### Пользовательские агрегаты

#### Создание кастомных агрегатов
```cql
-- Создание функции для расчета
CREATE OR REPLACE FUNCTION average_state(state tuple<int, bigint>, val int)
CALLED ON NULL INPUT
RETURNS tuple<int, bigint>
LANGUAGE java AS '
    if (val == null) return state;
    int count = state.getInt(0) + 1;
    long sum = state.getLong(1) + val;
    return tuple.of(count, sum);
';

-- Создание агрегата
CREATE OR REPLACE AGGREGATE average(int)
SFUNC average_state
STYPE tuple<int, bigint>
FINALFUNC average_final
INITCOND (0, 0);

-- Финальная функция
CREATE OR REPLACE FUNCTION average_final(state tuple<int, bigint>)
CALLED ON NULL INPUT
RETURNS double
LANGUAGE java AS '
    int count = state.getInt(0);
    if (count == 0) return 0.0;
    long sum = state.getLong(1);
    return (double) sum / count;
';

-- Использование кастомного агрегата
SELECT category, average(rating) as avg_rating
FROM product_ratings
GROUP BY category;
```

## Работа с коллекциями

### Операции со списками (LIST)

#### Создание и вставка
```cql
-- Создание таблицы со списком
CREATE TABLE user_skills (
    user_id UUID PRIMARY KEY,
    skills LIST<TEXT>,
    skill_levels LIST<INT>
);

-- Вставка списка
INSERT INTO user_skills (user_id, skills, skill_levels)
VALUES (uuid(), ['Java', 'Python', 'SQL'], [9, 7, 8]);

-- Добавление элементов
UPDATE user_skills SET skills = skills + ['JavaScript'] WHERE user_id = uuid();
UPDATE user_skills SET skills = ['C++'] + skills WHERE user_id = uuid();  -- В начало

-- Удаление элементов
UPDATE user_skills SET skills = skills - ['Python'] WHERE user_id = uuid();
```

#### Запросы со списками
```cql
-- Проверка наличия элемента
SELECT * FROM user_skills WHERE skills CONTAINS 'Java' ALLOW FILTERING;

-- Доступ по индексу
SELECT user_id, skills[0] as primary_skill FROM user_skills;

-- Размер списка
SELECT user_id, size(skills) as skill_count FROM user_skills;

-- Срез списка
SELECT user_id, skills[1..3] as middle_skills FROM user_skills;  -- Элементы 1-3
```

### Операции с множествами (SET)

#### Работа с множествами
```cql
-- Создание таблицы с множеством
CREATE TABLE user_permissions (
    user_id UUID PRIMARY KEY,
    permissions SET<TEXT>,
    roles SET<TEXT>
);

-- Вставка множества
INSERT INTO user_permissions (user_id, permissions, roles)
VALUES (uuid(), {'read', 'write', 'delete'}, {'admin', 'user'});

-- Добавление элементов
UPDATE user_permissions SET permissions = permissions + {'execute'} WHERE user_id = uuid();

-- Удаление элементов
UPDATE user_permissions SET roles = roles - {'user'} WHERE user_id = uuid();
```

#### Запросы с множествами
```cql
-- Проверка наличия элемента
SELECT * FROM user_permissions WHERE permissions CONTAINS 'write' ALLOW FILTERING;

-- Размер множества
SELECT user_id, size(permissions) as permission_count FROM user_permissions;

-- Проверка пересечения множеств
SELECT user_id FROM user_permissions
WHERE permissions CONTAINS 'read'
AND roles CONTAINS 'admin' ALLOW FILTERING;
```

### Операции со словарями (MAP)

#### Работа со словарями
```cql
-- Создание таблицы со словарем
CREATE TABLE user_settings (
    user_id UUID PRIMARY KEY,
    preferences MAP<TEXT, TEXT>,
    counters MAP<TEXT, INT>
);

-- Вставка словаря
INSERT INTO user_settings (user_id, preferences, counters)
VALUES (
    uuid(),
    {'theme': 'dark', 'language': 'en', 'timezone': 'UTC'},
    {'logins': 42, 'page_views': 1250}
);

-- Добавление/обновление элементов
UPDATE user_settings SET preferences = preferences + {'notifications': 'enabled'} WHERE user_id = uuid();
UPDATE user_settings SET counters['logins'] = 43 WHERE user_id = uuid();

-- Удаление элементов
UPDATE user_settings SET preferences = preferences - {'timezone'} WHERE user_id = uuid();
```

#### Запросы со словарями
```cql
-- Проверка наличия ключа
SELECT * FROM user_settings WHERE preferences CONTAINS KEY 'theme' ALLOW FILTERING;

-- Проверка наличия значения
SELECT * FROM user_settings WHERE preferences CONTAINS 'dark' ALLOW FILTERING;

-- Доступ к элементам
SELECT user_id, preferences['theme'] as theme FROM user_settings;

-- Ключи и значения
SELECT user_id, keys(preferences) as setting_keys FROM user_settings;
SELECT user_id, values(preferences) as setting_values FROM user_settings;
```

## Пользовательские функции и агрегаты

### Создание функций

#### Скалярные функции
```cql
-- Функция для форматирования имени
CREATE OR REPLACE FUNCTION format_name(first_name TEXT, last_name TEXT)
CALLED ON NULL INPUT
RETURNS TEXT
LANGUAGE java AS '
    if (first_name == null || last_name == null) return null;
    return first_name + " " + last_name;
';

-- Использование функции
SELECT user_id, format_name(first_name, last_name) as full_name FROM users;
```

#### Функции агрегации
```cql
-- Функция состояния для агрегации
CREATE OR REPLACE FUNCTION concat_state(state TEXT, val TEXT)
CALLED ON NULL INPUT
RETURNS TEXT
LANGUAGE java AS '
    if (val == null) return state;
    if (state == null) return val;
    return state + ", " + val;
';

-- Агрегат для конкатенации строк
CREATE OR REPLACE AGGREGATE concat_agg(TEXT)
SFUNC concat_state
STYPE TEXT
INITCOND '';

-- Использование агрегата
SELECT category, concat_agg(product_name) as product_list
FROM products
GROUP BY category;
```

#### Функции для работы с коллекциями
```cql
-- Функция для подсчета элементов в списке по условию
CREATE OR REPLACE FUNCTION count_tags_with_prefix(tags LIST<TEXT>, prefix TEXT)
CALLED ON NULL INPUT
RETURNS INT
LANGUAGE java AS '
    if (tags == null || prefix == null) return 0;
    int count = 0;
    for (String tag : tags) {
        if (tag != null && tag.startsWith(prefix)) {
            count++;
        }
    }
    return count;
';

-- Использование
SELECT product_id, count_tags_with_prefix(tags, 'tech') as tech_tags_count
FROM products;
```

## Пакетные операции

### BATCH запросы

#### Базовые пакеты
```cql
-- Пакетная вставка
BEGIN BATCH
    INSERT INTO users (id, email, name) VALUES (uuid(), 'user1@example.com', 'User 1');
    INSERT INTO users (id, email, name) VALUES (uuid(), 'user2@example.com', 'User 2');
    INSERT INTO users (id, email, name) VALUES (uuid(), 'user3@example.com', 'User 3');
APPLY BATCH;

-- Смешанный пакет
BEGIN BATCH
    INSERT INTO orders (id, user_id, total) VALUES (uuid(), uuid(), 100.00);
    UPDATE inventory SET quantity = quantity - 1 WHERE product_id = uuid();
    DELETE FROM cart WHERE user_id = uuid() AND product_id = uuid();
APPLY BATCH;
```

#### Условные пакеты
```cql
-- Пакет с условиями
BEGIN BATCH
    UPDATE accounts SET balance = balance - 100 WHERE id = uuid() IF balance >= 100;
    UPDATE accounts SET balance = balance + 100 WHERE id = uuid();
APPLY BATCH;

-- Если любое условие не выполнится, весь пакет будет отменен
```

### Логгированные и не логгированные пакеты

#### Типы пакетов
```cql
-- Логгированный пакет (по умолчанию) - atomic
BEGIN BATCH
    INSERT INTO audit_log (id, action) VALUES (uuid(), 'transfer_started');
    UPDATE accounts SET balance = balance - 100 WHERE id = uuid();
    UPDATE accounts SET balance = balance + 100 WHERE id = uuid();
    INSERT INTO audit_log (id, action) VALUES (uuid(), 'transfer_completed');
APPLY BATCH;

-- Нелоггированный пакет - быстрее, но не atomic
BEGIN UNLOGGED BATCH
    INSERT INTO metrics (timestamp, value) VALUES (toTimestamp(now()), 42.5);
    INSERT INTO metrics (timestamp, value) VALUES (toTimestamp(now()), 38.1);
APPLY BATCH;

-- Пакет с счетчиком
BEGIN COUNTER BATCH
    UPDATE page_views SET views = views + 1 WHERE page_id = uuid();
    UPDATE user_stats SET total_views = total_views + 1 WHERE user_id = uuid();
APPLY BATCH;
```

### Практическое использование пакетов

```java
@Service
public class BatchOperationService {

    @Autowired
    private CqlSession session;

    // Пакетная вставка пользователей
    public void batchInsertUsers(List<User> users) {
        BatchStatementBuilder batchBuilder = BatchStatement.builder(DefaultBatchType.LOGGED);

        for (User user : users) {
            PreparedStatement insertUser = session.prepare(
                "INSERT INTO users (id, email, name, created_at) VALUES (?, ?, ?, ?)"
            );

            batchBuilder.addStatement(insertUser.bind(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt()
            ));
        }

        session.execute(batchBuilder.build());
    }

    // Атомарный перевод денег
    public boolean transferMoney(UUID fromAccount, UUID toAccount, BigDecimal amount) {
        PreparedStatement checkBalance = session.prepare(
            "SELECT balance FROM accounts WHERE id = ?"
        );

        Row fromRow = session.execute(checkBalance.bind(fromAccount)).one();
        if (fromRow == null || fromRow.getDecimal("balance").compareTo(amount) < 0) {
            return false; // Недостаточно средств
        }

        // Атомарный перевод
        BatchStatement batch = BatchStatement.builder(DefaultBatchType.LOGGED)
            .addStatement(session.prepare("UPDATE accounts SET balance = balance - ? WHERE id = ?")
                .bind(amount, fromAccount))
            .addStatement(session.prepare("UPDATE accounts SET balance = balance + ? WHERE id = ?")
                .bind(amount, toAccount))
            .addStatement(session.prepare("INSERT INTO transfers (id, from_account, to_account, amount, timestamp) " +
                "VALUES (?, ?, ?, ?, toTimestamp(now()))")
                .bind(UUID.randomUUID(), fromAccount, toAccount, amount))
            .build();

        session.execute(batch);
        return true;
    }

    // Пакетное обновление метрик
    public void batchUpdateMetrics(Map<String, Object> metrics) {
        BatchStatementBuilder batch = BatchStatement.builder(DefaultBatchType.UNLOGGED);

        PreparedStatement insertMetric = session.prepare(
            "INSERT INTO metrics (name, value, timestamp) VALUES (?, ?, toTimestamp(now()))"
        );

        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            batch.addStatement(insertMetric.bind(
                entry.getKey(),
                entry.getValue()
            ));
        }

        session.execute(batch.build());
    }

    // Очистка старых данных пакетами
    public void batchDeleteOldData(String tableName, LocalDate cutoffDate) {
        // Получение партиций для удаления
        PreparedStatement selectPartitions = session.prepare(
            "SELECT partition_key FROM " + tableName + " WHERE date < ? LIMIT 1000 ALLOW FILTERING"
        );

        ResultSet rs = session.execute(selectPartitions.bind(cutoffDate));

        List<UUID> partitionsToDelete = rs.all().stream()
            .map(row -> row.getUuid("partition_key"))
            .collect(Collectors.toList());

        // Пакетное удаление
        BatchStatementBuilder batch = BatchStatement.builder(DefaultBatchType.UNLOGGED);

        PreparedStatement deleteStmt = session.prepare(
            "DELETE FROM " + tableName + " WHERE partition_key = ?"
        );

        for (UUID partitionKey : partitionsToDelete) {
            batch.addStatement(deleteStmt.bind(partitionKey));
        }

        if (!partitionsToDelete.isEmpty()) {
            session.execute(batch.build());
        }
    }
}
```

## TTL и временные данные

### Time To Live (TTL)

#### Установка TTL
```cql
-- TTL при вставке
INSERT INTO sessions (session_id, user_id, data)
VALUES (uuid(), uuid(), 'session_data')
USING TTL 3600;  -- 1 час

-- TTL при обновлении
UPDATE user_cache SET data = 'new_data'
WHERE user_id = uuid()
USING TTL 1800;  -- 30 минут

-- TTL для коллекций
UPDATE temp_data SET items = items + ['new_item']
WHERE id = uuid()
USING TTL 86400;  -- 24 часа
```

#### Проверка TTL
```cql
-- Проверка оставшегося времени жизни
SELECT id, data, ttl(data) as time_to_live_seconds
FROM sessions WHERE session_id = uuid();

-- Проверка времени истечения
SELECT id, data, writetime(data) + ttl(data) * 1000000 as expires_at
FROM sessions WHERE session_id = uuid();
```

#### Управление TTL
```cql
-- Удаление TTL (данные становятся постоянными)
UPDATE sessions SET data = data WHERE session_id = uuid();

-- Изменение TTL существующих данных
UPDATE sessions USING TTL 7200 SET data = 'extended_data'
WHERE session_id = uuid();

-- TTL по умолчанию для таблицы
CREATE TABLE notifications (
    user_id UUID,
    notification_id TIMEUUID,
    message TEXT,
    PRIMARY KEY ((user_id), notification_id)
) WITH default_time_to_live = 604800;  -- 7 дней по умолчанию
```

### Временные ряды с TTL

#### Автоматическая очистка старых данных
```cql
-- Таблица логов с автоматической очисткой
CREATE TABLE application_logs (
    application_id TEXT,
    date DATE,
    log_id TIMEUUID,
    level TEXT,
    message TEXT,
    PRIMARY KEY ((application_id, date), log_id)
) WITH default_time_to_live = 2592000;  -- 30 дней

-- Вставка логов (автоматически получат TTL)
INSERT INTO application_logs (application_id, date, level, message)
VALUES ('web-app', '2023-01-01', 'INFO', 'Application started');

-- Таблица сессий с коротким TTL
CREATE TABLE user_sessions (
    session_id UUID PRIMARY KEY,
    user_id UUID,
    data TEXT,
    last_access TIMESTAMP
) WITH default_time_to_live = 3600;  -- 1 час
```

#### Продление TTL
```java
@Service
public class SessionService {

    @Autowired
    private CqlSession session;

    // Продление сессии
    public void extendSession(UUID sessionId) {
        PreparedStatement extend = session.prepare("""
            UPDATE user_sessions USING TTL ? SET last_access = toTimestamp(now())
            WHERE session_id = ?
            """);

        session.execute(extend.bind(3600, sessionId)); // Продлить на 1 час
    }

    // Очистка истекших сессий (обычно не требуется, Cassandra делает это автоматически)
    public void cleanupExpiredSessions() {
        // Cassandra автоматически удаляет данные по истечении TTL
        // Этот метод может понадобиться для принудительной очистки
        session.execute("SELECT * FROM user_sessions LIMIT 1 ALLOW FILTERING");
    }

    // Получение активных сессий
    public List<Session> getActiveSessions(UUID userId) {
        PreparedStatement selectActive = session.prepare("""
            SELECT session_id, data, ttl(data) as remaining_ttl
            FROM user_sessions
            WHERE user_id = ? AND ttl(data) > 0
            ALLOW FILTERING
            """);

        return session.execute(selectActive.bind(userId)).all().stream()
            .map(row -> new Session(
                row.getUuid("session_id"),
                row.getString("data"),
                row.getInt("remaining_ttl")
            ))
            .collect(Collectors.toList());
    }
}
```

## Оптимизация запросов

### EXPLAIN план запроса

#### Анализ плана выполнения
```cql
-- Получение плана выполнения запроса
EXPLAIN SELECT * FROM users WHERE id = uuid();

-- Результат покажет:
-- - Используемые индексы
-- - Количество проверяемых партиций
-- - Оценку стоимости
-- - Тип операции (LOCAL_QUORUM, ONE, etc.)

EXPLAIN SELECT * FROM products
WHERE category = 'electronics'
ORDER BY price DESC
LIMIT 10 ALLOW FILTERING;

-- Для сложных запросов с JOIN-подобными операциями
EXPLAIN SELECT * FROM user_orders
WHERE user_id = uuid()
ORDER BY created_at DESC
LIMIT 20;
```

#### Интерпретация результатов EXPLAIN
```cql
-- Хороший план: использование первичного ключа
EXPLAIN SELECT * FROM users WHERE id = uuid();
-- Result: IndexScan on users.id (partition key access)

-- Плохой план: полное сканирование
EXPLAIN SELECT * FROM users WHERE age > 25 ALLOW FILTERING;
-- Result: Full table scan, high cost

-- Оптимизированный план с индексом
CREATE INDEX idx_users_age ON users(age);
EXPLAIN SELECT * FROM users WHERE age > 25 ALLOW FILTERING;
-- Result: IndexScan on users.age, lower cost
```

### Оптимизация производительности

#### 1. Использование первичных ключей
```cql
-- Эффективно: доступ по partition key
SELECT * FROM users WHERE id = uuid();

-- Эффективно: диапазон по clustering key
SELECT * FROM user_posts
WHERE user_id = uuid()
AND created_at >= '2023-01-01';

-- Неэффективно: фильтрация по не-key колонке
SELECT * FROM users WHERE city = 'New York' ALLOW FILTERING;
```

#### 2. Избегание ALLOW FILTERING
```cql
-- Вместо неэффективного запроса
SELECT * FROM products WHERE category = 'electronics' ALLOW FILTERING;

-- Создать отдельную таблицу
CREATE TABLE products_by_category (
    category TEXT,
    product_id UUID,
    name TEXT,
    price DECIMAL,
    PRIMARY KEY ((category), product_id)
);

-- Эффективный запрос
SELECT * FROM products_by_category WHERE category = 'electronics';
```

#### 3. Оптимизация LIMIT и пейджинга
```cql
-- Хороший пейджинг: использование токенов
SELECT * FROM large_table
WHERE token(id) > ? AND token(id) <= ?
LIMIT 1000;

-- Плохой пейджинг: offset (не поддерживается эффективно)
-- SELECT * FROM large_table LIMIT 1000 OFFSET 5000; -- Не делайте так!

-- Хороший пейджинг: курсор-based
SELECT * FROM user_posts
WHERE user_id = ? AND post_id > ?
ORDER BY post_id DESC
LIMIT 50;
```

### Кэширование и материализованные представления

#### Создание `MV` для оптимизации
```cql
-- Основная таблица
CREATE TABLE orders (
    order_id UUID,
    user_id UUID,
    status TEXT,
    total DECIMAL,
    created_at TIMESTAMP,
    PRIMARY KEY ((order_id), created_at)
);

-- MV для запросов по пользователю
CREATE MATERIALIZED VIEW orders_by_user AS
SELECT user_id, order_id, status, total, created_at
FROM orders
WHERE user_id IS NOT NULL AND order_id IS NOT NULL
PRIMARY KEY ((user_id), created_at, order_id)
WITH CLUSTERING ORDER BY (created_at DESC, order_id DESC);

-- MV для запросов по статусу
CREATE MATERIALIZED VIEW orders_by_status AS
SELECT status, order_id, user_id, total, created_at
FROM orders
WHERE status IS NOT NULL AND order_id IS NOT NULL
PRIMARY KEY ((status), created_at, order_id);
```

## Индексы и поиск

### Вторичные индексы

#### Создание и использование
```cql
-- Простой вторичный индекс
CREATE INDEX idx_users_email ON users(email);

-- Индекс на статус (низкая кардинальность)
CREATE INDEX idx_orders_status ON orders(status);

-- Использование индексов
SELECT * FROM users WHERE email = 'user@example.com' ALLOW FILTERING;
SELECT * FROM orders WHERE status = 'pending' ALLOW FILTERING;
```

#### SASI индексы для текста
```cql
-- Префиксный поиск
CREATE CUSTOM INDEX product_name_prefix
ON products(name)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {'mode': 'PREFIX'};

-- Полнотекстовый поиск
CREATE CUSTOM INDEX product_desc_fulltext
ON products(description)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
    'mode': 'CONTAINS',
    'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer'
};

-- Поиск без ALLOW FILTERING
SELECT * FROM products WHERE name LIKE 'Laptop%';
SELECT * FROM products WHERE description LIKE '%wireless%';
```

### Поисковые паттерны

#### Таблицы для поиска
```cql
-- Таблица для поиска пользователей
CREATE TABLE users_search (
    search_term TEXT,
    user_id UUID,
    email TEXT,
    name TEXT,
    PRIMARY KEY ((search_term), user_id)
);

-- Заполнение поисковой таблицы
INSERT INTO users_search (search_term, user_id, email, name)
VALUES ('john', uuid(), 'john@example.com', 'John Doe');

INSERT INTO users_search (search_term, user_id, email, name)
VALUES ('doe', uuid(), 'john@example.com', 'John Doe');

-- Поиск
SELECT * FROM users_search WHERE search_term = 'john';
```

#### Геопоиск
```cql
-- Таблица для геопоиска
CREATE TABLE places_search (
    geohash TEXT,
    place_id UUID,
    name TEXT,
    latitude DOUBLE,
    longitude DOUBLE,
    PRIMARY KEY ((geohash), place_id)
);

-- Поиск в географической области
SELECT * FROM places_search
WHERE geohash >= 'u4pru' AND geohash < 'u4prv';
```

## Мониторинг и профилирование

### Метрики запросов

#### Сбор статистики
```java
@Service
public class QueryMetricsCollector {

    @Autowired
    private CqlSession session;

    public QueryStats collectQueryStats(String queryName, String cql, Object... params) {
        long startTime = System.nanoTime();

        Statement<?> statement = SimpleStatement.newInstance(cql, params);

        ResultSet rs = session.execute(statement);
        List<Row> results = rs.all();

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        ExecutionInfo info = rs.getExecutionInfo();

        return QueryStats.builder()
            .queryName(queryName)
            .executionTimeMs(executionTimeMs)
            .resultCount(results.size())
            .coordinator(info.getCoordinator())
            .speculativeExecutions(info.getSpeculativeExecutionCount())
            .triedHosts(info.getTriedHosts().size())
            .successfulHosts(info.getSuccessfulHosts().size())
            .errors(info.getErrors().size())
            .hasPagingState(info.getPagingState() != null)
            .build();
    }

    public List<QueryStats> profileCommonQueries() {
        List<QueryStats> stats = new ArrayList<>();

        // Профилирование основных запросов
        stats.add(collectQueryStats("find_user", "SELECT * FROM users WHERE id = ?", UUID.randomUUID()));
        stats.add(collectQueryStats("find_user_orders", "SELECT * FROM user_orders WHERE user_id = ? LIMIT 10", UUID.randomUUID()));
        stats.add(collectQueryStats("search_products", "SELECT * FROM products WHERE category = ? LIMIT 20 ALLOW FILTERING", "electronics"));

        return stats.stream()
            .sorted(Comparator.comparingLong(QueryStats::getExecutionTimeMs).reversed())
            .collect(Collectors.toList());
    }

    public Map<String, Object> analyzeQueryPatterns() {
        Map<String, Object> analysis = new HashMap<>();

        // Анализ частоты запросов по типам
        analysis.put("select_queries", countQueriesByType("SELECT"));
        analysis.put("insert_queries", countQueriesByType("INSERT"));
        analysis.put("update_queries", countQueriesByType("UPDATE"));
        analysis.put("delete_queries", countQueriesByType("DELETE"));

        // Анализ использования ALLOW FILTERING
        analysis.put("filtering_queries", countFilteringQueries());

        // Анализ производительности по времени
        analysis.put("slow_queries", findSlowQueries(1000)); // > 1 сек

        return analysis;
    }

    private int countQueriesByType(String type) {
        // Логика подсчета запросов по типам
        return 0; // placeholder
    }

    private int countFilteringQueries() {
        // Логика подсчета запросов с ALLOW FILTERING
        return 0; // placeholder
    }

    private List<String> findSlowQueries(long thresholdMs) {
        // Логика поиска медленных запросов
        return new ArrayList<>();
    }
}

@Builder
class QueryStats {
    private String queryName;
    private long executionTimeMs;
    private int resultCount;
    private InetAddress coordinator;
    private int speculativeExecutions;
    private int triedHosts;
    private int successfulHosts;
    private int errors;
    private boolean hasPagingState;

    // getters
}
```

#### Инструменты мониторинга
```cql
-- Системные таблицы для мониторинга
SELECT * FROM system_schema.keyspaces;
SELECT * FROM system_schema.tables;
SELECT * FROM system_schema.columns;

-- Статистика таблицы
SELECT keyspace_name, table_name, memtable_data_size, sstable_count
FROM system_schema.table_stats;

-- Мониторинг запросов
SELECT * FROM system_traces.sessions LIMIT 10;
SELECT * FROM system_traces.events WHERE session_id = ?;

-- Производительность узлов
SELECT peer, data_center, rack, tokens
FROM system.peers;
```

## Лучшие практики

### Проектирование запросов

#### 1. Query-First Design
- **Определяйте запросы до схемы**
- **Создавайте таблицы для конкретных паттернов**
- **Избегайте сложных `JOIN`-подобных операций**
- **Используйте денормализацию для производительности**

#### 2. Оптимизация первичных ключей
- **Выбирайте partition key с высокой кардинальностью**
- **Ограничьте размер партиций (100MB — 300MB)**
- **Используйте composite keys для равномерного распределения**
- **Определяйте clustering keys для естественной сортировки**

#### 3. Управление индексами
- **Создавайте индексы только при необходимости**
- **Используйте `SASI` для текстового поиска**
- **Мониторьте эффективность индексов**
- **Регулярно перестраивайте индексы**

### Производительность и масштабируемость

#### 1. Размер данных
- **Ограничьте размер строк (< 1MB)**
- **Используйте `TTL` для временных данных**
- **Архивируйте старые данные**
- **Планируйте рост данных**

#### 2. Пейджинг и ограничения
- **Всегда используйте LIMIT**
- **Реализуйте курсор-based пейджинг**
- **Избегайте больших result sets**
- **Используйте `token-based` пейджинг для больших таблиц**

#### 3. Пакетные операции
- **Группируйте логически связанные операции**
- **Используйте `UNLOGGED` batches для вставок**
- **Ограничьте размер batch (максимум 50KB)**
- **Мониторьте latency batch операций**

### Безопасность и надежность

#### 1. Управление доступом
- **Используйте roles и permissions**
- **Ограничьте доступ к системным таблицам**
- **Шифруйте чувствительные данные**
- **Регулярно обновляйте credentials**

#### 2. Обработка ошибок
- **Обрабатывайте timeouts и unavailable exceptions**
- **Реализуйте retry logic с exponential backoff**
- **Мониторьте и логируйте ошибки**
- **Используйте circuit breaker pattern**

#### 3. Резервное копирование
- **Регулярные snapshots**
- **Тестирование восстановления**
- **Хранение бэкапов в нескольких локациях**
- **Шифрование бэкапов**

### Мониторинг и поддержка

#### 1. Ключевые метрики
- **Latency запросов (< 100ms для большинства)**
- **Throughput (запросов в секунду)**
- **Размер партиций и SSTables**
- **Использование дискового пространства**

#### 2. Регулярное обслуживание
- **nodetool repair** — еженедельно
- **nodetool cleanup** — после добавления узлов
- **nodetool compact** — при необходимости
- **Мониторинг `GC` и `heap` usage**

#### 3. Профилирование
- **EXPLAIN для медленных запросов**
- **Tracing для анализа bottleneck**
- **Мониторинг system_traces**
- **Анализ access patterns**

**Cassandra Query Language** (CQL) предоставляет мощный и гибкий интерфейс для работы с данными в **Apache Cassandra**, но требует понимания распределенной природы базы данных. Ключевые особенности:**

### Основные принципы CQL:

1. **Декларативный синтаксис** — похож на **SQL**, но адаптирован для распределенных операций
2. **Query-`First` Design** — проектирование схемы на основе паттернов запросов
3. **Ограничения производительности** — некоторые операции требуют специального подхода
4. **Гибкость типов данных** — поддержка коллекций, **UDT** и **JSON**

### Ключевые возможности:

- **CRUD операции** — вставка, чтение, обновление, удаление
- **Фильтрация и условия** — **WHERE** с ограничениями для производительности
- **Сортировка и пейджинг** — **ORDER** `BY` и курсор-**based** навигация
- **Агрегации** — встроенные и пользовательские функции
- **Коллекции** — работа со списками, множествами и словарями
- **TTL** — автоматическая очистка временных данных

### Стратегии оптимизации:

1. **Первичные ключи** — эффективный доступ через **partition** и **clustering keys**
2. **Вторичные индексы** — **SASI** для сложного поиска
3. **Материализованные представления** — денормализация для производительности
4. **Пейджинг** — курсор-**based** для больших наборов данных
5. **Батчинг** — группировка операций для атомарности

### Вызовы и решения:

1. **ALLOW FILTERING** — избегать через правильное моделирование данных
2. **Hot partitions** — балансировка через выбор **partition key**
3. **Сложные запросы** — денормализация и дополнительные таблицы
4. **Производительность** — мониторинг и оптимизация на основе метрик

### Лучшие практики:

- **Мониторинг производительности** запросов
- **Использование EXPLAIN** для анализа планов
- **Регулярное обслуживание** кластера
- **Тестирование** на реальных данных и нагрузке

## Решение проблем

**Таймауты запросов:** уменьшите объём данных на запрос (ограничьте партицию, используйте пейджинацию), проверьте размер партиций и при необходимости денормализуйте. Увеличьте таймауты на клиенте только после оптимизации. Избегайте запросов по непартиционному ключу.

**Read/Write timeout или Unavailable:** проверьте консистентность (ONE, QUORUM, ALL) и количество реплик. При сетевых сбоях временно снизьте уровень консистентности или добавьте retry с отступом. Убедитесь, что узлы в кластере здоровы (`nodetool status`).

**Высокая задержка:** проверьте большие партиции (например, через `nodetool tablestats`), компакцию и нагрузку на диск. Добавьте индексы для вторичных предикатов, но не злоупотребляйте ими. Рассмотрите кэширование на стороне приложения.

**Ошибки валидации или неверный CQL:** проверьте типы данных, порядок ключей в первичном ключе и ограничения (WHERE только по ключевым полям). Читайте сообщения об ошибках и документацию по CQL для вашей версии.

Эффективное использование **CQL** требует глубокого понимания **Cassandra** архитектуры и принципов распределенных систем. Правильное проектирование запросов и схем — ключ к высокой производительности и масштабируемости приложений.


