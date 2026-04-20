---
title: "PostgreSQL: структура БД и таблиц"
description: "Кратко: создание/удаление БД, базовый синтаксис CREATE TABLE, типы данных и ключевые ограничения. Полезно как справочник при моделировании."
tags:
  - databases
  - relational
  - postgres-structure
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# PostgreSQL: структура БД и таблиц

Кратко: создание/удаление БД, базовый синтаксис **CREATE TABLE**, типы данных и ключевые ограничения. Полезно как справочник при моделировании.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)

### Обучающие материалы

- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)


См. также: [[postgres-design]] — [[postgres-data-ops]] — [[postgres-indexes]].

## Содержание

- [PostgreSQL: структура БД и таблиц](#postgresql-структура-бд-и-таблиц)
- [Создание и удаление базы](#создание-и-удаление-базы)
- [Создание таблиц](#создание-таблиц)
- [Типы данных](#типы-данных)
- [Ограничения столбцов и таблиц](#ограничения-столбцов-и-таблиц)
- [Изменение таблиц](#изменение-таблиц)
- [Системные схемы и namespaces](#системные-схемы-и-namespaces)
- [Полезные DDL-паттерны](#полезные-ddl-паттерны)
- [Последовательности и IDENTITY](#последовательности-и-identity)
- [Сгенерированные столбцы](#сгенерированные-столбцы)
- [Ограничения откладываемые и каскады](#ограничения-откладываемые-и-каскады)
- [Наследование таблиц](#наследование-таблиц)
- [Подробное описание типов данных PostgreSQL](#подробное-описание-типов-данных-postgresql)
  - [Числовые типы](#числовые-типы)
  - [Символьные типы](#символьные-типы)
  - [Бинарные типы](#бинарные-типы)
  - [Типы даты и времени](#типы-даты-и-времени)
  - [Логический тип](#логический-тип)
  - [Сетевые типы](#сетевые-типы)
  - [Геометрические типы](#геометрические-типы)
  - [JSON и XML типы](#json-и-xml-типы)
  - [UUID тип](#uuid-тип)
- [Подробное описание ограничений столбцов и таблиц](#подробное-описание-ограничений-столбцов-и-таблиц)
  - [PRIMARY KEY (первичный ключ)](#primary-key-первичный-ключ)
  - [Составной первичный ключ](#составной-первичный-ключ)
  - [UNIQUE (уникальность)](#unique-уникальность)
  - [NULL и NOT NULL](#null-и-not-null)
  - [DEFAULT (значение по умолчанию)](#default-значение-по-умолчанию)
  - [CHECK (проверочное ограничение)](#check-проверочное-ограничение)
- [Подробное описание ALTER TABLE](#подробное-описание-alter-table)
  - [Добавление столбцов](#добавление-столбцов)
  - [Удаление столбцов](#удаление-столбцов)
  - [Изменение типов данных](#изменение-типов-данных)
  - [Изменение ограничений столбцов](#изменение-ограничений-столбцов)
  - [Добавление ограничений таблицы](#добавление-ограничений-таблицы)
  - [Удаление ограничений](#удаление-ограничений)
  - [Переименование столбцов и таблиц](#переименование-столбцов-и-таблиц)
- [Работа с ограничениями и индексами](#работа-с-ограничениями-и-индексами)
  - [Управление индексами через ALTER TABLE](#управление-индексами-через-alter-table)
  - [Включение/отключение ограничений](#включениеотключение-ограничений)
  - [Проверка целостности данных](#проверка-целостности-данных)
- [Расширенные техники работы с таблицами](#расширенные-техники-работы-с-таблицами)
  - [Создание таблиц как копии](#создание-таблиц-как-копии)
  - [Временные таблицы](#временные-таблицы)
  - [Таблицы без OID](#таблицы-без-oid)
  - [Unlogged таблицы](#unlogged-таблицы)
- [Практические рекомендации по структуре](#практические-рекомендации-по-структуре)
  - [Выбор типов данных](#выбор-типов-данных)
  - [Ограничения и производительность](#ограничения-и-производительность)
  - [Миграции структуры](#миграции-структуры)
  - [Проверка структуры таблиц](#проверка-структуры-таблиц)
- [Best Practices](#лучшие-практики)
- [Решение проблем](#решение-проблем)

## Создание и удаление базы
- **Создать БД:**
```sql
-- Создание и удаление базы (подключений к удаляемой БД быть не должно)
CREATE DATABASE usersdb;
```
- **Удалить БД (подключений быть не должно):**
```sql
DROP DATABASE usersdb;
```

## Создание таблиц
- **Общий шаблон:**
```sql
CREATE TABLE имя (
  column1 data_type [column_constraint],
  column2 data_type [column_constraint],
  ...
  [table_constraints]
);
```
- **Пример:**
```sql
CREATE TABLE authors (
  id   SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  city TEXT
);
```

## Типы данных
- Числа: `SMALLINT`, `INT`, `BIGINT`, `SERIAL`, `NUMERIC(p,s)`.
- Строки: `VARCHAR(n)`, `TEXT`.
- Даты/время: `DATE`, `TIMESTAMP [WITH] TIME ZONE`, `TIME`.
- Логические: `BOOLEAN`.
- Прочее: `UUID`, `JSONB`, `BYTEA`.

## Ограничения столбцов и таблиц
- Столбцы: `NOT NULL`, `UNIQUE`, `DEFAULT`, `CHECK`, `REFERENCES`.
- Первичный ключ: `PRIMARY KEY (col1 [, col2])`.
- **Внешний ключ:**
```sql
ALTER TABLE articles
ADD CONSTRAINT fk_articles_authors
FOREIGN KEY (author_id) REFERENCES authors(id)
ON DELETE SET NULL;
```
- Индексы для ускорения выборок добавляйте отдельно (см. [[postgres-indexes]]).

## Изменение таблиц
- Добавить столбец: `ALTER TABLE t ADD COLUMN col TEXT;`
- Изменить тип: `ALTER TABLE t ALTER COLUMN col TYPE INTEGER;`
- Удалить столбец: `ALTER TABLE t DROP COLUMN col;`
- Переименовать: `ALTER TABLE t RENAME COLUMN old `TO` new;`

## Системные схемы и namespaces
- **Пользовательские объекты лучше держать не в `public`, а в отдельной схеме:**
```sql
CREATE SCHEMA app AUTHORIZATION appuser;
SET search_path = app, public;
```
- Системные схемы: `pg_catalog`, `information_schema`, `pg_toast` — не трогаем.

## Полезные DDL-паттерны
- **Добавить `created_at` / `updated_at` с автоподстановкой:**
```sql
ALTER TABLE t
  ADD COLUMN created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN updated_at timestamptz NOT NULL DEFAULT now();
```
- **Триггер на **auto-update updated_at**:**
```sql
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_updated_at
BEFORE UPDATE ON t
FOR EACH ROW EXECUTE PROCEDURE set_updated_at();
```
- **Снятие/выдача прав по схеме:**
```sql
GRANT USAGE ON SCHEMA app TO appuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app TO appuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA app
  GRANT SELECT ON TABLES TO appuser;
```

## Последовательности и IDENTITY
- **Классика:**
```sql
CREATE SEQUENCE users_id_seq;
CREATE TABLE users (
  id bigint PRIMARY KEY DEFAULT nextval('users_id_seq'),
  name text
);
```
- **Современный **IDENTITY**:**
```sql
CREATE TABLE users (
  id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name text
);
```
- Если нужно вставлять свои id, используйте `GENERATED `BY` DEFAULT `AS` IDENTITY`.

## Сгенерированные столбцы
- **Хранимое вычисляемое поле:**
```sql
CREATE TABLE invoices (
  qty int,
  price numeric(10,2),
  total numeric(12,2) GENERATED ALWAYS AS (qty * price) STORED
);
```

## Ограничения откладываемые и каскады
- **DEFERRABLE** (проверка в конце транзакции):**
```sql
ALTER TABLE orders
  ADD CONSTRAINT fk_orders_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  DEFERRABLE INITIALLY DEFERRED;
```
- Каскады: `ON DELETE CASCADE | SET NULL | RESTRICT`. Используйте осознанно — проверяйте, сколько строк может удалиться.

## Наследование таблиц
- **Старый способ «партиционирования» и **polymorphic** таблиц:**
```sql
CREATE TABLE vehicles (
  id serial PRIMARY KEY,
  name text
);
CREATE TABLE cars (
  doors int
) INHERITS (vehicles);
```
- Помните: права, индексы и ограничения не всегда наследуются; чаще используйте декларативное партиционирование.

## Подробное описание типов данных PostgreSQL

При определении таблицы для всех ее столбцов необходимо указать тип данных. Тип данных определяет диапазон значений, которые могут храниться в столбце, сколько они будут занимать места в памяти. **PostgreSQL** поддерживает богатую палитру различных типов данных, среди которые условно можно разделить на подгруппы: числовые, символьные, логические, дата и время, бинарные и ряд других.

### Числовые типы

**Автоинкрементирующиеся типы:**
1. **serial**: представляет автоинкрементирующееся числовое значение, которое занимает 4 байта и может хранить числа от 1 до `2147483647`. Значение данного типа образуется путем автоинкремента значения предыдущей строки. Поэтому, как правило, данный тип используется для определения идентификаторов строки.

2. **smallserial**: представляет автоинкрементирующееся числовое значение, которое занимает 2 байта и может хранить числа от 1 до `32767`. Аналог типа **serial** для небольших чисел.

3. **bigserial**: представляет автоинкрементирующееся числовое значение, которое занимает 8 байт и может хранить числа от 1 до `9223372036854775807`. Аналог типа **serial** для больших чисел.

**Целочисленные типы:**
4. **smallint**: хранит числа от -32768 до +32767. Занимает 2 байта. Имеет псевдоним **int2**.

5. **integer**: хранит числа от -2147483648 до +2147483647. Занимает 4 байта. Имеет псевдонимы **int** и **int4**.

6. **bigint**: хранит числа от -9223372036854775808 до +9223372036854775807. Занимает 8 байт. Имеет псевдоним **int8**.

**Типы с плавающей точкой:**
7. **numeric**: хранит числа с фиксированной точностью, которые могут иметь до `131072` знаков в целой части и до `16383` знаков после запятой. Данный тип может принимать два параметра **precision** и **scale**: `numeric(precision, scale)`. Параметр **precision** указывает на максимальное количество цифр, которые может хранить число. Параметр **scale** представляет максимальное количество цифр, которые может содержать число после запятой. Это значение должно находиться в диапазоне от 0 до значения параметра **precision**. По умолчанию оно равно 0. Например, для числа `23.5141` **precision** равно 6, а **scale** — 4.

8. **decimal**: хранит числа с фиксированной точностью, которые могут иметь до `131072` знаков в целой части и до `16383` знаков в дробной части. То же самое, что и **numeric**.

9. **real**: хранит числа с плавающей точкой из диапазона от 1E-37 до 1E+37. Занимает 4 байта. Имеет псевдоним **float4**.

10. **double precision**: хранит числа с плавающей точкой из диапазона от 1E-307 до 1E+308. Занимает 8 байт. Имеет псевдоним **float8**.

11. Для работы с денежными единицами определен тип **money**, который может принимать значения в диапазоне от -92233720368547758.08 до +92233720368547758.07 и занимает 8 байт.

**Примеры числовых типов:**
```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,           -- serial (автоинкремент)
    price NUMERIC(10, 2),            -- точные деньги
    discount REAL,                   -- процент скидки
    quantity INTEGER                 -- количество
);
```

### Символьные типы

12. **character(n)**: представляет строку из фиксированного количества символов. С помощью параметра задается количество символов в строке. Имеет псевдоним **char(n)**.

13. **character varying(n)**: представляет строку из переменной длины. С помощью параметра задается максимальное количество символов в строке. Имеет псевдоним **varchar(n)**.

14. **text**: представляет текст произвольной длины.

**Примеры символьных типов:**
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    code CHAR(10),                  -- фиксированная длина
    email VARCHAR(255),             -- переменная длина до 255
    bio TEXT                        -- произвольная длина
);
```

### Бинарные типы

15. Для хранения бинарных данных определён тип **bytea**. Он хранит данные в виде бинарных строк, которые представляют последовательность октетов или байт.

**Пример:**
```sql
CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255),
    content BYTEA
);
```

### Типы даты и времени

16. **timestamp**: хранит дату и время. Занимает 8 байт. Для дат самое нижнее значение - `4713` г до н.э., самое верхнее значение - `294276` г н.э.

17. **timestamp with time zone** (или timestamptz): то же самое, что и **timestamp**, только добавляет данные о часовом поясе.

18. **date**: представляет дату от `4713` г. до н.э. до `5874897` г н.э. Занимает 4 байта.

19. **time**: хранит время с точностью до 1 микросекунды без указания часового пояса. Принимает значения от 00:00:00 до 24:00:00. Занимает 8 байт.

20. **time with time zone** (или timetz): хранит время с точностью до 1 микросекунды с указанием часового пояса. Принимает значения от 00:00:00+1459 до 24:00:00-1459. Занимает 12 байт.

21. **interval**: представляет временной интервал. Занимает 16 байт.

**Примеры:**
```sql
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    event_date DATE,
    start_time TIME,
    created_at TIMESTAMPTZ DEFAULT now(),  -- рекомендуется timestamptz
    duration INTERVAL
);
```

**Рекомендации по использованию типов даты и времени:**
- Используйте `timestamptz` для глобальных событий (автоматическая конвертация в локальное время).
- Используйте `date` для календарных дат без времени.
- Используйте `interval` для длительности событий.

### Логический тип

22. Тип **boolean** может хранить одно из двух значений: **true** или **false**.

**Допустимые значения:**
- Вместо **true** можно указывать: **TRUE**, 't', '**true**', 'y', '**yes**', 'on', '1'.
- Вместо **false** можно указывать: **FALSE**, 'f', '**false**', 'n', 'no', '**off**', '0'.

**Пример:**
```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    is_deleted BOOLEAN DEFAULT false
);
```

### Сетевые типы

23. **cidr**: интернет-адрес в формате **IPv4** и **IPv6**. Например, `192.168`.0.1. Занимает от 7 до 19 байт.

24. **inet**: интернет-адрес в формате **cidr**/y, где **cidr** — адрес в формате **IPv4** или **IPv6**, а /y — количество бит в адресе (если этот параметр не указан, то используется 32 для `IPv4`, `128` для IPv6). Например, `192.168.0.0` или `2001:4f8:3:ba:2e0:81ff:fe22:d1f1/128`. Занимает от 7 до 19 байт.

25. **macaddr**: хранит **MAC**-адрес. Занимает 6 байт.

26. **macaddr8**: хранит **MAC**-адрес в формате **EUI-64**. Занимает 8 байт.

**Примеры:**
```sql
CREATE TABLE network_devices (
    id SERIAL PRIMARY KEY,
    ip_address INET,
    mac_address MACADDR,
    network CIDR
);

INSERT INTO network_devices (ip_address, mac_address, network)
VALUES ('192.168.1.100', '08:00:2b:01:02:03', '192.168.0.0/24');
```

### Геометрические типы

27. **point**: представляет точку на плоскости в формате (x,y). Занимает 16 байт.

28. **line**: представляет линию неопределённой длины в формате {A,B,C}. Занимает 32 байта.

29. **lseg**: представляет отрезок в формате (**(x1,y1),(x2,y2)). Занимает 32 байта.

30. **box**: представляет прямоугольник в формате (**(x1,y1),(x2,y2)). Занимает 32 байта.

31. **path**: представляет набор соединенных точек. В формате (**(x1,y1),...) путь является закрытым (первая и последняя точка соединяются линией) и фактически представляет многоугольник. В формате [(x1,y1),...] путь является открытым. Занимает 16+16n байт.

32. **polygon**: представляет многоугольник в формате (**(x1,y1),...). Занимает 40+16n байт.

33. **circle**: представляет окружность в формате <(x,y),r>. Занимает 24 байта.

**Примеры:**
```sql
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    coordinates POINT,
    area POLYGON
);

INSERT INTO locations (name, coordinates, area)
VALUES ('Office', '(10, 20)', '((0,0),(10,0),(10,10),(0,10))');
```

### JSON и XML типы

34. **json**: хранит данные **json** в текстовом виде.

35. **jsonb**: хранит данные **json** в бинарном формате. Рекомендуется использовать вместо **json** для лучшей производительности и возможности индексирования.

36. **xml**: хранит данные в формате **XML**.

**Примеры:**
```sql
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    payload JSONB,  -- рекомендуется jsonb
    metadata XML
);

INSERT INTO events (payload)
VALUES ('{"type": "login", "user_id": 123, "timestamp": "2024-01-01T10:00:00Z"}'::jsonb);
```

**Рекомендации:**
- Используйте `jsonb` вместо `json` для лучшей производительности и возможности индексирования.
- Используйте **GIN** индексы для `jsonb` для быстрого поиска по ключам и значениям.

### UUID тип

37. **uuid**: хранит универсальный уникальный идентификатор (UUID), например, **a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11**. Занимает 16 байт (в текстовом представлении 32 символа + дефисы).

**Пример:**
```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id INTEGER,
    expires_at TIMESTAMPTZ
);
```

**Рекомендации:**
- Используйте **UUID** для распределенных систем.
- Используйте **UUID** для внешних идентификаторов (API, интеграции).
- Для внутренних связей используйте `SERIAL` / `BIGSERIAL` (более эффективно).

## Подробное описание ограничений столбцов и таблиц

При определении таблиц и их столбцов в **SQL** мы можем использовать ряд атрибутов, которые накладывают определённые ограничения. Рассмотрим эти атрибуты.

### PRIMARY KEY (первичный ключ)

С помощью выражения `PRIMARY KEY` столбец можно сделать первичным ключом.

**На уровне столбца:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(30),
    LastName CHARACTER VARYING(30),
    Email CHARACTER VARYING(30),
    Age INTEGER
);
```

Первичный ключ уникально идентифицирует строку в таблице. В качестве первичного ключа необязательно должны выступать столбцы с типом `SERIAL`, они могут представлять любой другой тип.

**На уровне таблицы:**
```sql
CREATE TABLE Customers (
    Id SERIAL,
    FirstName CHARACTER VARYING(30),
    LastName CHARACTER VARYING(30),
    Email CHARACTER VARYING(30),
    Age INTEGER,
    PRIMARY KEY(Id)
);
```

### Составной первичный ключ

Первичный ключ может быть составным (compound Key). Такой ключ может потребоваться, если у нас сразу два столбца должны уникально идентифицировать строку в таблице.

**Пример:**
```sql
CREATE TABLE OrderLines (
    OrderId INTEGER,
    ProductId INTEGER,
    Quantity INTEGER,
    Price MONEY,
    PRIMARY KEY(OrderId, ProductId)
);
```

Здесь поля `OrderId` и `ProductId` вместе выступают как составной первичный ключ. То есть в таблице `OrderLines` не может быть двух строк, где для обоих из этих полей одновременно были бы одни и те же значения.

### UNIQUE (уникальность)

Если мы хотим, чтобы столбец имел только уникальные значения, то для него можно определить атрибут `UNIQUE`.

**На уровне столбца:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(20),
    LastName CHARACTER VARYING(20),
    Email CHARACTER VARYING(30) UNIQUE,
    Phone CHARACTER VARYING(30) UNIQUE,
    Age INTEGER
);
```

В данном случае столбцы, которые представляют электронный адрес и телефон, будут иметь уникальные значения. И мы не сможем добавить в таблицу две строки, у которых значения для этих столбцов будет совпадать.

**На уровне таблицы:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(20),
    LastName CHARACTER VARYING(20),
    Email CHARACTER VARYING(30),
    Phone CHARACTER VARYING(30),
    Age INTEGER,
    UNIQUE(Email, Phone)  -- Составной уникальный ключ
);
```

**Или так:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(20),
    LastName CHARACTER VARYING(20),
    Email CHARACTER VARYING(30),
    Phone CHARACTER VARYING(30),
    Age INTEGER,
    UNIQUE(Email),
    UNIQUE(Phone)
);
```

### NULL и NOT NULL

Чтобы указать, может ли столбец принимать значение **NULL**, при определении столбца ему можно задать атрибут `NULL` или `NOT NULL`. Если этот атрибут явным образом не будет использован, то по умолчанию столбец будет допускать значение `NULL`. Исключением является тот случай, когда столбец выступает в роли первичного ключа — в этом случае по умолчанию столбец имеет значение `NOT NULL`.

**Пример:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(20) NOT NULL,
    LastName CHARACTER VARYING(20) NOT NULL,
    Age INTEGER  -- допускает NULL по умолчанию
);
```

### DEFAULT (значение по умолчанию)

Атрибут `DEFAULT` определяет значение по умолчанию для столбца. Если при добавлении данных для столбца не будет предусмотрено значение, то для него будет использоваться значение по умолчанию.

**Пример:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(20),
    LastName CHARACTER VARYING(20),
    Age INTEGER DEFAULT 18
);
```

Здесь для столбца `Age` предусмотрено значение по умолчанию 18.

**Использование функций в `DEFAULT`:**
```sql
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    status VARCHAR(50) DEFAULT 'pending'
);
```

### CHECK (проверочное ограничение)

Ключевое слово `CHECK` задает ограничение для диапазона значений, которые могут храниться в столбце. Для этого после слова `CHECK` указывается в скобках условие, которому должен соответствовать столбец или несколько столбцов.

**Пример на уровне столбца:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName CHARACTER VARYING(20),
    LastName CHARACTER VARYING(20),
    Age INTEGER DEFAULT 18 CHECK(Age > 0 AND Age < 100),
    Email CHARACTER VARYING(30) UNIQUE CHECK(Email != ''),
    Phone CHARACTER VARYING(20) UNIQUE CHECK(Phone != '')
);
```

Здесь также указывается, что столбцы `Email` и `Phone` не могут иметь пустую строку в качестве значения (пустая строка не эквивалентна значению NULL).

**CHECK на уровне таблицы:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    Age INTEGER DEFAULT 18,
    FirstName CHARACTER VARYING(20),
    LastName CHARACTER VARYING(20),
    Email CHARACTER VARYING(30) UNIQUE,
    Phone CHARACTER VARYING(20) UNIQUE,
    CHECK((Age > 0 AND Age < 100) AND (Email != '') AND (Phone != ''))
);
```

**Использование `CONSTRAINT` с именем:**
С помощью ключевого слова `CONSTRAINT` можно задать имя для ограничений. В качестве ограничений могут использоваться `PRIMARY KEY`, `UNIQUE`, `CHECK`.

**Имена ограничений можно задать на уровне столбцов:**
```sql
CREATE TABLE Customers (
    Id SERIAL CONSTRAINT customer_Id PRIMARY KEY,
    Age INTEGER CONSTRAINT customers_age_check CHECK(Age > 0 AND Age < 100),
    FirstName CHARACTER VARYING(20) NOT NULL,
    LastName CHARACTER VARYING(20) NOT NULL,
    Email CHARACTER VARYING(30) CONSTRAINT customers_email_key UNIQUE,
    Phone CHARACTER VARYING(20) CONSTRAINT customers_phone_key UNIQUE
);
```

**И также можно задать все имена ограничений через атрибуты таблицы:**
```sql
CREATE TABLE Customers (
    Id SERIAL,
    Age INTEGER,
    FirstName CHARACTER VARYING(20) NOT NULL,
    LastName CHARACTER VARYING(20) NOT NULL,
    Email CHARACTER VARYING(30),
    Phone CHARACTER VARYING(20),
    CONSTRAINT customer_Id PRIMARY KEY(Id),
    CONSTRAINT customers_age_check CHECK(Age > 0 AND Age < 100),
    CONSTRAINT customers_email_key UNIQUE(Email),
    CONSTRAINT customers_phone_key UNIQUE(Phone)
);
```

В принципе необязательно задавать имена ограничений, при установке соответствующих атрибутов **PostgreSQL** автоматически определяет их имена. Но, зная имя ограничения, мы можем к нему обращаться, например, для его удаления.

## Подробное описание ALTER TABLE

Нередко возникает необходимость изменить уже имеющуюся таблицу, в частности, добавить или удалить столбцы, изменить тип столбцов и т.д. То есть потребуется изменить определение таблицы. Для этого применяется выражение `ALTER TABLE`.

### Добавление столбцов

**Добавим в таблицу `Customers` новый столбец `Phone`:**
```sql
ALTER TABLE Customers
ADD Phone CHARACTER VARYING(20) NULL;
```

Здесь столбец `Phone` имеет тип `CHARACTER VARYING (20)`, и для него определен атрибут `NULL`, то есть столбец допускает отсутствие значения.

**Важно:** Если нам надо добавить столбец, который не должен принимать значения `NULL`, то следующая команда не будет выполнена, если в таблице есть данные:

```sql
ALTER TABLE Customers
ADD Address CHARACTER VARYING(30) NOT NULL;
-- ОШИБКА, если в таблице есть строки!
```

**Поэтому в данном случае решение состоит в установке значения по умолчанию через атрибут `DEFAULT`:**
```sql
ALTER TABLE Customers
ADD Address CHARACTER VARYING(30) NOT NULL DEFAULT 'Неизвестно';
```

После добавления столбца с `DEFAULT`, можно обновить существующие строки и удалить значение по умолчанию, если необходимо.

### Удаление столбцов

**Удалим столбец `Address` из таблицы `Customers`:**
```sql
ALTER TABLE Customers
DROP COLUMN Address;
```

**Удаление столбца с ограничениями:**
```sql
-- Сначала удалить ограничение, затем столбец
ALTER TABLE Customers
DROP CONSTRAINT constraint_name;

ALTER TABLE Customers
DROP COLUMN Address;
```

**CASCADE для удаления зависимостей:**
```sql
ALTER TABLE Customers
DROP COLUMN Address CASCADE;
-- Автоматически удалит зависимые ограничения и индексы
```

### Изменение типов данных

**Для изменения типа применяется ключевое слово `TYPE`. Изменим в таблице `Customers` тип данных у столбца `FirstName` на `VARCHAR(50)`:**

```sql
ALTER TABLE Customers
ALTER COLUMN FirstName TYPE VARCHAR(50);
```

**Важно:** Изменение типа может не удаться, если данные не могут быть преобразованы в новый тип. В таких случаях используйте `USING`:

```sql
ALTER TABLE Customers
ALTER COLUMN Age TYPE TEXT
USING Age::TEXT;
```

### Изменение ограничений столбцов

**Для добавления ограничения применяется оператор `SET`:**
```sql
-- Установить NOT NULL
ALTER TABLE Customers
ALTER COLUMN FirstName
SET NOT NULL;

-- Установить DEFAULT
ALTER TABLE Customers
ALTER COLUMN Age
SET DEFAULT 18;

-- Удалить DEFAULT
ALTER TABLE Customers
ALTER COLUMN Age
DROP DEFAULT;

-- Удалить NOT NULL
ALTER TABLE Customers
ALTER COLUMN FirstName
DROP NOT NULL;
```

### Добавление ограничений таблицы

**Добавление ограничения `CHECK`:**
```sql
ALTER TABLE Customers
ADD CHECK (Age > 0);
```

**Добавление первичного ключа:**
```sql
ALTER TABLE Customers
ADD PRIMARY KEY (Id);
```

В данном случае предполагается, что в таблице уже есть столбец `Id`, который не имеет ограничения `PRIMARY KEY`. А с помощью вышеуказанного скрипта устанавливается ограничение `PRIMARY KEY`.

**Добавление ограничения `UNIQUE`:**
```sql
ALTER TABLE Customers
ADD UNIQUE (Email);
```

**При добавлении ограничения каждому из них даётся определённое имя. Например, выше добавленное ограничение для `CHECK` будет называться `customers_age_check`. Имена ограничений можно посмотреть в таблице через **pgAdmin** или запросом:**

```sql
SELECT constraint_name, constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'customers';
```

**Также мы можем явным образом назначить ограничению при добавлении имя с помощью оператора `CONSTRAINT`:**
```sql
ALTER TABLE Customers
ADD CONSTRAINT phone_unique UNIQUE (Phone);
```

В данном случае ограничение будет называться "**phone_unique**".

### Удаление ограничений

**Чтобы удалить ограничение, надо знать его имя, которое указывается после выражения `DROP CONSTRAINT`:**

```sql
ALTER TABLE Customers
DROP CONSTRAINT phone_unique;
```

### Переименование столбцов и таблиц

**Переименуем столбец `Address` в `City`:**
```sql
ALTER TABLE Customers
RENAME COLUMN Address TO City;
```

**Переименуем таблицу `Customers` в `Users`:**
```sql
ALTER TABLE Customers
RENAME TO Users;
```

## Работа с ограничениями и индексами

### Управление индексами через ALTER TABLE

**Хотя индексы обычно создаются отдельной командой `CREATE INDEX`, некоторые операции можно выполнить через `ALTER TABLE`:**

**Добавление индекса:**
```sql
-- Создать индекс (лучше через CREATE INDEX)
CREATE INDEX idx_customers_email ON Customers(Email);

-- Но можно и через ALTER TABLE добавить ограничение UNIQUE, которое создаст индекс
ALTER TABLE Customers
ADD CONSTRAINT customers_email_unique UNIQUE (Email);
```

### Включение/отключение ограничений

**Временное отключение ограничения (не все ограничения можно отключить):**
```sql
-- Включить ограничение
ALTER TABLE Customers
ENABLE CONSTRAINT customers_email_key;

-- Отключить ограничение
ALTER TABLE Customers
DISABLE CONSTRAINT customers_email_key;
```

**Важно:** Не все ограничения можно отключать в **PostgreSQL**. `PRIMARY KEY` и `FOREIGN KEY` нельзя отключить напрямую.

### Проверка целостности данных

**Проверка ограничений (для DEFERRABLE ограничений):**
```sql
-- Создать откладываемое ограничение
ALTER TABLE Orders
ADD CONSTRAINT fk_orders_customer
FOREIGN KEY (customer_id) REFERENCES Customers(id)
DEFERRABLE INITIALLY DEFERRED;

-- В транзакции можно изменить режим проверки
BEGIN;
SET CONSTRAINTS ALL DEFERRED;
-- Операции, которые могут временно нарушить ограничения
COMMIT;  -- Проверка произойдет при COMMIT
```

## Расширенные техники работы с таблицами

### Создание таблиц как копии

**Создать таблицу как копию существующей:**
```sql
-- Копия структуры
CREATE TABLE customers_backup (LIKE customers INCLUDING ALL);

-- Копия структуры и данных
CREATE TABLE customers_backup AS
SELECT * FROM customers;
```

**Копирование с выборкой:**
```sql
CREATE TABLE customers_active AS
SELECT * FROM customers
WHERE is_active = true;
```

### Временные таблицы

**Временные таблицы существуют только в рамках сессии:**
```sql
CREATE TEMP TABLE temp_orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    total NUMERIC
);
```

**Глобальные временные таблицы (видны всем сессиям):**
```sql
CREATE TEMP TABLE temp_orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    total NUMERIC
) ON COMMIT DROP;  -- Удаляется при COMMIT

-- Или
CREATE TEMP TABLE temp_orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    total NUMERIC
) ON COMMIT PRESERVE ROWS;  -- Сохраняет данные после COMMIT
```

### Таблицы без OID

**PostgreSQL 12+ не использует `OID` по умолчанию, но для старых версий:**
```sql
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100)
) WITHOUT OIDS;
```

### Unlogged таблицы

**Unlogged таблицы не пишутся в `WAL` (быстрее, но теряются при сбое):**
```sql
CREATE UNLOGGED TABLE temp_data (
    id SERIAL PRIMARY KEY,
    data TEXT
);

-- Используйте для временных данных, кэшей
-- ВАЖНО: данные теряются при сбое сервера!
```

## Практические рекомендации по структуре

### Выбор типов данных

**Рекомендации:**
- Используйте `BIGSERIAL` / `BIGINT` для первичных ключей в больших таблицах (> 2 млрд строк).
- Используйте `NUMERIC` для денег, а не `REAL` или `DOUBLE PRECISION` (точность).
- Используйте `TEXT` вместо `VARCHAR(n)` без лимита (одинаковая производительность в PostgreSQL).
- Используйте `TIMESTAMPTZ` для глобальных событий, а не `TIMESTAMP`.
- Используйте `JSONB` вместо `JSON` для лучшей производительности.

### Ограничения и производительность

**Рекомендации:**
- Добавляйте `NOT NULL` для часто используемых столбцов (оптимизация).
- Используйте `CHECK` ограничения для валидации на уровне БД.
- Добавляйте `UNIQUE` ограничения для предотвращения дублей.
- Используйте составные индексы для составных ограничений.

### Миграции структуры

**Безопасные изменения:**
- Добавление столбца с `DEFAULT` — безопасно.
- Добавление столбца `NULL` — безопасно.
- Добавление ограничения с проверкой существующих данных.

**Опасные изменения:**
- Изменение типа столбца — требует проверки данных.
- Удаление столбца — потеря данных.
- Изменение `NOT NULL` на `NULL` — обычно безопасно.
- Изменение `NULL` на `NOT NULL` — требует заполнения существующих данных.

**Практика безопасных миграций:**
```sql
-- 1. Добавить новый столбец с NULL
ALTER TABLE customers ADD COLUMN new_email VARCHAR(255);

-- 2. Заполнить данные
UPDATE customers SET new_email = old_email WHERE old_email IS NOT NULL;

-- 3. Добавить NOT NULL с DEFAULT (для новых строк)
ALTER TABLE customers
ALTER COLUMN new_email SET NOT NULL,
ALTER COLUMN new_email SET DEFAULT '';

-- 4. Переименовать старый столбец (опционально)
ALTER TABLE customers RENAME COLUMN old_email TO old_email_backup;

-- 5. Переименовать новый столбец
ALTER TABLE customers RENAME COLUMN new_email TO email;

-- 6. Удалить старый столбец (после проверки)
-- ALTER TABLE customers DROP COLUMN old_email_backup;
```

### Проверка структуры таблиц

**Просмотр структуры таблицы:**
```sql
-- Описание таблицы
\d customers

-- Структура таблицы через SQL
SELECT
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'customers'
ORDER BY ordinal_position;
```

**Просмотр ограничений:**
```sql
SELECT
    constraint_name,
    constraint_type,
    table_name
FROM information_schema.table_constraints
WHERE table_name = 'customers';
```

**Просмотр индексов:**
```sql
SELECT
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'customers';
```

## Лучшие практики

- **Именование:** единый стиль (snake_case); осмысленные имена таблиц и столбцов; не использовать зарезервированные слова без кавычек.
- **Первичные ключи:** у каждой таблицы должен быть первичный ключ (SERIAL/`BIGSERIAL` или IDENTITY); для составного ключа — стабильный и компактный набор столбцов.
- **Внешние ключи:** задавайте **REFERENCES** с `ON UPDATE` по смыслу (CASCADE, `SET NULL`, RESTRICT); индексируйте столбцы `FK` для быстрых **JOIN**.
- **Типы данных:** выбирайте подходящий размер (BIGINT для счётчиков, `NUMERIC` для денег); используйте **TIMESTAMP WITH TIME ZONE** для времени; **JSONB** для полуструктурированных данных.
- **Миграции и изменения:** версионируйте **DDL**; тестируйте **ALTER** на копии; при тяжёлых изменениях — создание новой таблицы, копирование, переключение.

## Решение проблем

**Ошибки при создании таблицы (syntax / already exists):** проверьте синтаксис **DDL**, имена объектов и права доступа. При «relation already exists» используйте `CREATE TABLE IF NOT EXISTS` или переименуйте объект. Проверьте зарезервированные слова и при необходимости заключайте идентификаторы в кавычки.

**Блокировки при ALTER TABLE:** длительные **ALTER** (добавление колонки с default, изменение типа) могут блокировать таблицу. В PostgreSQL 11+ добавление колонки с default без значения часто выполняется без полной перезаписи. Для тяжёлых изменений используйте создание новой таблицы, копирование и переключение с минимальным downtime.

**Ошибки целостности (foreign key violation):** при вставке/обновлении убедитесь, что ссылочные значения существуют в родительской таблице. При удалении родителя задайте `ON DELETE` (CASCADE, SET NULL, RESTRICT) по смыслу. Временно отключение ограничений не рекомендуется — исправьте данные и включите снова.

**Нехватка места при создании индекса:** убедитесь в достаточном месте в табличном пространстве; большие индексы требуют места и времени. Используйте `CONCURRENTLY` для создания индекса без эксклюзивной блокировки таблицы (только для создания индекса, не для всех ALTER).

## См. также

- [[postgres-admin|PostgreSQL: администрирование и обслуживание]]
- [[postgres-backup-restore|PostgreSQL: Резервное копирование и восстановление]]
- [[postgres-basics|PostgreSQL: Полное руководство по основам и мониторингу]]
- [[postgres-data-ops|PostgreSQL: операции с данными (CRUD)]]
- [[postgres-design|PostgreSQL: проектирование и нормализация]]
