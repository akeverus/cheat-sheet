---
title: "PostgreSQL: типы данных (массивы, enum, JSONB, домены)"
description: "Комплексное руководство по расширенным типам данных в PostgreSQL: массивы, перечисления, JSONB, домены, диапазоны, полнотекстовый поиск, UUID и пользовательские типы"
tags:
  - postgresql
  - database
  - types
  - arrays
  - json
  - enum
  - domains
  - ranges
  - full-text-search
  - uuid
  - custom-types
difficulty: "intermediate"
prerequisites: ["databases/postgres-basics.md"]
next: ["databases/postgres-design.md"]
updated: "2026-02-06"
related: ["databases/postgres-basics.md", "databases/postgres-design.md", "databases/postgres-joins.md", "databases/postgres-indexes.md"]
---

# PostgreSQL: типы данных (массивы, enum, `JSONB`, домены)

Это подробное руководство по расширенным типам данных в **PostgreSQL**. Вы узнаете о массивах, перечислениях (enum), **JSONB**, доменах, диапазонах, полнотекстовом поиске, **UUID** и создании пользовательских типов данных.

## Полезные ссылки

### Официальная документация PostgreSQL

- [PostgreSQL Data Types](https://www.postgresql.org/docs/)
- [PostgreSQL Arrays](https://www.postgresql.org/docs/)
- [PostgreSQL JSON](https://www.postgresql.org/docs/)
- [PostgreSQL Enum Types](https://www.postgresql.org/docs/)
- [PostgreSQL Domains](https://www.postgresql.org/docs/)
- [PostgreSQL Full Text Search](https://www.postgresql.org/docs/)
- [PostgreSQL Range Types](https://www.postgresql.org/docs/)

### Дополнительные ресурсы

- [PostgreSQL Advanced Data Types](https://www.postgresql.org/docs/)
- [JSONB Performance Tips](https://www.postgresql.org/docs/)
- [Full Text Search Tutorial](https://www.postgresql.org/docs/)
- [PostgreSQL Extensions](https://www.postgresql.org/docs/)

См. также: [[postgres-basics]] — [[postgres-design]] — [[postgres-joins]].

## Содержание

- [PostgreSQL: типы данных (массивы, enum, JSONB, домены)](#postgresql-типы-данных-массивы-enum-jsonb-домены)
- [Массивы](#массивы)
- [Enum](#enum)
- [JSONB](#jsonb)
- [Domain](#domain)
- [Композиция типов и CAST](#композиция-типов-и-cast)
- [Диапазоны и EXCLUDE](#диапазоны-и-exclude)
- [Полнотекстовый поиск](#полнотекстовый-поиск)
- [UUID и время](#uuid-и-время)
- [Подробное описание массивов](#подробное-описание-массивов)
  - [Создание таблиц с массивами](#создание-таблиц-с-массивами)
  - [Вставка данных в массивы](#вставка-данных-в-массивы)
  - [Извлечение данных из массивов](#извлечение-данных-из-массивов)
  - [Обновление массивов](#обновление-массивов)
  - [Индексы для массивов](#индексы-для-массивов)
- [Подробное описание Enum](#подробное-описание-enum)
  - [Создание Enum типа](#создание-enum-типа)
  - [Использование Enum в таблицах](#использование-enum-в-таблицах)
  - [Вставка данных в Enum столбцы](#вставка-данных-в-enum-столбцы)
  - [Обновление Enum значений](#обновление-enum-значений)
  - [Изменение Enum типов](#изменение-enum-типов)
  - [Преимущества и недостатки Enum](#преимущества-и-недостатки-enum)
  - [Альтернативы Enum](#альтернативы-enum)
  - [Практические примеры](#практические-примеры)
- [Расширенные типы данных PostgreSQL](#расширенные-типы-данных-postgresql)
  - [Геометрические типы](#геометрические-типы)
  - [Сетевые типы](#сетевые-типы)
  - [XML тип](#xml-тип)
  - [Дополнительные функции для работы с типами](#дополнительные-функции-для-работы-с-типами)
- [Пользовательские типы данных](#пользовательские-типы-данных)
  - [Составные типы (Composite Types)](#составные-типы-composite-types)
  - [Перечисления с дополнительными атрибутами](#перечисления-с-дополнительными-атрибутами)
- [Расширения PostgreSQL](#расширения-postgresql)
  - [Установка и управление расширениями](#установка-и-управление-расширениями)
  - [Создание пользовательских расширений](#создание-пользовательских-расширений)
- [Продвинутые техники работы с массивами](#продвинутые-техники-работы-с-массивами)
  - [Многомерные массивы](#многомерные-массивы)
  - [Массивы с пользовательскими типами](#массивы-с-пользовательскими-типами)
  - [Индексация массивов](#индексация-массивов)
- [Продвинутые техники работы с JSONB](#продвинутые-техники-работы-с-jsonb)
  - [JSON Schema валидация](#json-schema-валидация)
  - [JSONB в OLAP кубах](#jsonb-в-olap-кубах)
  - [JSONB для поиска и фильтрации](#jsonb-для-поиска-и-фильтрации)
- [Диапазоны и временные ряды](#диапазоны-и-временные-ряды)
  - [Продвинутые диапазонные типы](#продвинутые-диапазонные-типы)
  - [Интеллектуальный полнотекстовый поиск](#интеллектуальный-полнотекстовый-поиск)
    - [Настройка поиска на русском языке](#настройка-поиска-на-русском-языке)
    - [Расширенный полнотекстовый поиск](#расширенный-полнотекстовый-поиск)
- [UUID и генерация идентификаторов](#uuid-и-генерация-идентификаторов)
  - [Продвинутые техники работы с UUID](#продвинутые-техники-работы-с-uuid)
  - [Генерация последовательностей и серий](#генерация-последовательностей-и-серий)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Оптимизация JSONB](#оптимизация-jsonb)
  - [Оптимизация массивов](#оптимизация-массивов)
  - [Кэширование и материализованные представления](#кэширование-и-материализованные-представления)
- [Интеграция с приложениями](#интеграция-с-приложениями)
  - [Spring Boot интеграция](#spring-boot-интеграция)
  - [Hibernate типы](#hibernate-типы)
  - [JDBC работа с расширенными типами](#jdbc-работа-с-расширенными-типами)
  - [Основные принципы:](#основные-принципы)
  - [Лучшие практики](#лучшие-практики)
  - [Производительность:](#производительность)
  - [Безопасность:](#безопасность)
- [Решение проблем](#решение-проблем)

## Массивы
- **Объявление столбца-массива:**
```sql
-- Таблица с массивом тегов (TEXT[])
CREATE TABLE books (
  id    SERIAL PRIMARY KEY,
  title TEXT,
  tags  TEXT[]
);
```
- **Вставка и поиск:**
```sql
INSERT INTO books (title, tags) VALUES ('Algo 101', ARRAY['algorithms','cs']);
SELECT * FROM books WHERE 'algorithms' = ANY(tags);
```
- Используйте массивы для коротких списков значений без отдельной таблицы; для сложных связей лучше `M:N` через таблицу.

## Enum
- **Объявление типа:**
```sql
CREATE TYPE mood AS ENUM ('sad','ok','happy');
```
- **Использование:**
```sql
CREATE TABLE users (
  id   SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  mood mood NOT NULL DEFAULT 'ok'
);
```
- Плюсы: валидация на уровне типа. Минусы: изменение списка значений требует `ALTER TYPE ... ADD VALUE`.

## JSONB
- **Гибкий формат для документов:**
```sql
CREATE TABLE events (
  id BIGSERIAL PRIMARY KEY,
  payload JSONB NOT NULL
);
```
- **Поиск по ключу/значению:**
```sql
SELECT * FROM events
WHERE payload->>'type' = 'login';
```
- **Индекс для **JSONB**:**
```sql
CREATE INDEX idx_events_payload_gin ON events USING GIN (payload);
```
- **Проверка наличия ключа/значения:**
```sql
SELECT * FROM events WHERE payload ? 'user_id';
SELECT * FROM events WHERE payload @> '{"type":"login"}';
```

## Domain
- **Создание домена для повторно используемого ограничения:**
```sql
CREATE DOMAIN non_empty_text AS TEXT CHECK (length(trim(VALUE)) > 0);
```
- **Применение:**
```sql
CREATE TABLE tags (
  id   SERIAL PRIMARY KEY,
  name non_empty_text NOT NULL UNIQUE
);
```

## Композиция типов и CAST
- Пользовательские типы можно использовать в функциях, схемах и столбцах.
- **Приведение типов:**
```sql
SELECT '2024-01-01'::date;
SELECT payload::jsonb FROM events;
```
- Храните даты/время как `timestamptz`, деньги как `numeric(p,s)`, **UUID** как `uuid`, не злоупотребляйте `TEXT` там, где нужен явный тип.

## Диапазоны и EXCLUDE
- **Range**-тип:**
```sql
CREATE TABLE bookings (
  id serial PRIMARY KEY,
  room_id int,
  period tsrange NOT NULL
);
```
- **Ограничение перекрытий (EXCLUDE):**
```sql
CREATE INDEX ON bookings USING GIST (room_id, period);
ALTER TABLE bookings
  ADD CONSTRAINT no_overlap EXCLUDE USING GIST
  (room_id WITH =, period WITH &&);
```

## Полнотекстовый поиск
- **Конфигурация:**
```sql
CREATE INDEX idx_docs_fts ON docs USING GIN (to_tsvector('russian', content));
SELECT * FROM docs WHERE to_tsvector('russian', content) @@ plainto_tsquery('russian', 'postgres');
```
- **Выделение фрагментов:**
```sql
SELECT ts_headline('russian', content, plainto_tsquery('russian','postgres')) FROM docs;
```

## UUID и время
- **UUID**:**
```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
SELECT uuid_generate_v4();
```
- **Время:**
  - Для глобальных событий — `timestamptz`.
  - Не храните локальное время без `TZ`, если событие глобальное.
  - Для логов: индекс `(created_at DESC)` и партиционирование по времени на больших объёмах.

## Подробное описание массивов

### Создание таблиц с массивами

Столбцы в таблице в **PostgreSQL** могут представлять массивы, например, массивы чисел **INTEGER** или массивы строк **VARCHAR** или массивы других типов данных.

**Базовый пример:**
```sql
CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(30),
    body TEXT,
    tags VARCHAR(10)[]
);
```

Таблица `posts` будет хранить условные статьи, где столбец `title` хранит заголовок статьи, `body` — ее текст, а `tags` — список тегов статьи. Столбец `tags` представляет массив данных `VARCHAR(10)`, то есть строк. Для определения массива после названия типа указываются квадратные скобки.

### Вставка данных в массивы

**Добавление статьи с набором тегов:**
```sql
INSERT INTO posts (title, body, tags)
VALUES ('Post Title', 'Post Text', '{"sql", "postgres", "database", "plsql"}');
```

Массив определяется в кавычках, как и строка, но внутри кавычек все элементы массива помещаются в фигурные скобки. Каждый отдельный элемент массива заключается в двойные кавычки. То есть в данном случае в массиве четыре элемента: "**sql**", "**postgres**", "**database**" и "**plsql**".

**Альтернативный синтаксис:**
```sql
-- Использование ARRAY конструктора
INSERT INTO posts (title, body, tags)
VALUES ('Post Title', 'Post Text', ARRAY['sql', 'postgres', 'database', 'plsql']);

-- Массив чисел
INSERT INTO products (name, prices)
VALUES ('Product 1', ARRAY[100, 200, 300]::INTEGER[]);

-- Многомерные массивы
CREATE TABLE matrices (
    id SERIAL PRIMARY KEY,
    matrix INTEGER[][]
);

INSERT INTO matrices (matrix)
VALUES ('{{1,2,3},{4,5,6},{7,8,9}}'::INTEGER[][]);
```

### Извлечение данных из массивов

**Получение всего массива:**
```sql
SELECT tags FROM posts;
```

**Получение конкретных элементов массива:**
```sql
-- Получить первый элемент (индексация с 1)
SELECT tags[1] FROM posts;

-- Получить диапазон элементов (с 1 по 3)
SELECT tags[1:3] FROM posts;

-- Индексы указываются в скобках через двоеточие
-- Вначале идет индекс начала диапазона, а затем индекс конца диапазона
```

**Поиск в массивах:**
```sql
-- Проверить, содержит ли массив значение
SELECT * FROM posts WHERE 'sql' = ANY(tags);

-- Проверить, содержит ли массив все указанные значения
SELECT * FROM posts WHERE tags @> ARRAY['sql', 'postgres'];

-- Проверить, пересекается ли массив с другим массивом
SELECT * FROM posts WHERE tags && ARRAY['database', 'nosql'];

-- Получить длину массива
SELECT array_length(tags, 1) FROM posts;

-- Проверить, пуст ли массив
SELECT * FROM posts WHERE tags = '{}' OR tags IS NULL;
```

### Обновление массивов

**Обновление всего массива:**
```sql
-- Удалить все теги
UPDATE posts
SET tags = '{}'
WHERE id = 1;

-- Установить новый массив
UPDATE posts
SET tags = '{"sql", "postgres", "database"}'
WHERE id = 1;
```

**Обновление конкретных элементов:**
```sql
-- Обновить второй элемент
UPDATE posts
SET tags[2] = 'system'
WHERE id = 1;

-- Добавить элемент в конец массива
UPDATE posts
SET tags = array_append(tags, 'newtag')
WHERE id = 1;

-- Удалить элемент из массива
UPDATE posts
SET tags = array_remove(tags, 'oldtag')
WHERE id = 1;

-- Объединить массивы
UPDATE posts
SET tags = tags || ARRAY['additional', 'tags']
WHERE id = 1;
```

**Массивные функции:**
```sql
-- Объединить массивы
SELECT ARRAY[1, 2, 3] || ARRAY[4, 5, 6];  -- {1,2,3,4,5,6}

-- Развернуть массив в строки (unnest)
SELECT unnest(tags) AS tag FROM posts;

-- Преобразовать массив в строку
SELECT array_to_string(tags, ', ') FROM posts;

-- Преобразовать строку в массив
SELECT string_to_array('sql,postgres,database', ',');
```

### Индексы для массивов

**GIN индекс для массивов:**
```sql
-- Создать GIN индекс для быстрого поиска в массивах
CREATE INDEX idx_posts_tags ON posts USING GIN (tags);

-- Теперь запросы с ANY, @>, && будут использовать индекс
SELECT * FROM posts WHERE 'sql' = ANY(tags);
```

**Частичный индекс на массивах:**
```sql
-- Индекс только для постов с определенными тегами
CREATE INDEX idx_posts_sql_tags ON posts USING GIN (tags)
WHERE 'sql' = ANY(tags);
```

## Подробное описание Enum

**PostgreSQL** имеет специальный тип данных, который называется **enum** и который представляет набор констант. Столбец подобного типа может в качестве значения принимать одну из этих констант.

### Создание Enum типа

**Для создания перечисления используется команда `CREATE TYPE`:**
```sql
CREATE TYPE request_state AS ENUM ('created', 'approved', 'finished');
```

Данное перечисление называется "**request_state**". После слова `enum` в скобках указывается через запятую список констант, которые составляют данное перечисление. То есть в данном случае перечисление `request_state` может принимать три значения: '**created**', '**approved**', '**finished**'.

### Использование Enum в таблицах

**После создания перечисления мы можем использовать его в качестве типа столбца:**
```sql
CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(30),
    status request_state
);
```

Здесь столбец `status` представляет перечисление `request_state` и может принимать одно из трех выше указанных значений.

### Вставка данных в Enum столбцы

**При добавлении данных нужно указать для данного столбца одно из этих трех значений:**
```sql
INSERT INTO requests (title, status)
VALUES ('Request 1', 'created');
```

**Важные особенности:**
- Столбец может иметь только одно из этих трех значений, а не какие-то произвольные значения.
- Большую роль играет регистр символов, например, "**created**" не эквивалентно "**Created**" или "**CREATED**".
- Значение должно точно соответствовать одному из элементов **enum** (с учетом регистра).

### Обновление Enum значений

**При обновлении данных также необходимо предоставить одно из значений перечисления:**
```sql
UPDATE requests
SET status = 'approved'
WHERE id = 1;
```

### Изменение Enum типов

**Добавление нового значения:**
```sql
ALTER TYPE request_state ADD VALUE 'blocked';
```

После добавления нового значения оно сразу становится доступным для использования в столбцах.

**Важно:** К сожалению, удалить так просто уже имеющееся значение из перечисления не получится. В этом случае мы можем создать новое перечисление и указать, чтобы таблица использовала именно новое перечисление:

```sql
-- Создать новое перечисление
CREATE TYPE status_enum AS ENUM('created', 'approved', 'done');

-- Изменить столбец на новое перечисление
ALTER TABLE requests
ALTER COLUMN status TYPE status_enum
USING status::text::status_enum;
```

Это преобразует существующие значения через текстовое представление в новое перечисление.

**Удаление `Enum` типа:**
```sql
-- Если перечисление больше не нужно
DROP TYPE request_state;
```

**Важно:** Удаление возможно только если тип не используется ни в одной таблице.

### Преимущества и недостатки Enum

**Преимущества:**
- Валидация на уровне базы данных — невозможно вставить недопустимое значение.
- Компактное хранение — **enum** хранится как 4 байта (или меньше для небольшого количества значений).
- Быстрый поиск и сравнение.
- Читаемость в запросах.

**Недостатки:**
- Сложно добавлять новые значения (требует `ALTER` TYPE).
- Невозможно удалить значения без пересоздания.
- Сложно мигрировать между базами данных.
- Нельзя изменить порядок значений без пересоздания.

### Альтернативы Enum

**CHECK ограничение:**
```sql
CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(30),
    status TEXT CHECK (status IN ('created', 'approved', 'finished'))
);
```

**Таблица справочник:**
```sql
CREATE TABLE request_statuses (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

INSERT INTO request_statuses (name) VALUES
    ('created'), ('approved'), ('finished');

CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(30),
    status_id INTEGER REFERENCES request_statuses(id)
);
```

**Когда использовать `Enum`:**
- Небольшой фиксированный набор значений, который редко меняется.
- Когда валидация на уровне БД критична.
- Когда важна компактность хранения.

**Когда НЕ использовать `Enum`:**
- Значения часто добавляются/удаляются.
- Нужна гибкость и расширяемость.
- Требуется многоязычная поддержка (имена значений фиксированы).

### Практические примеры

**Enum для статусов заказа:**
```sql
CREATE TYPE order_status AS ENUM (
    'pending',
    'processing',
    'shipped',
    'delivered',
    'cancelled',
    'refunded'
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    status order_status NOT NULL DEFAULT 'pending',
    created_at TIMESTAMPTZ DEFAULT now()
);
```

**Enum для ролей пользователей:**
```sql
CREATE TYPE user_role AS ENUM ('admin', 'moderator', 'user', 'guest');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    role user_role NOT NULL DEFAULT 'user'
);
```

**Поиск по `Enum`:**
```sql
-- Прямое сравнение
SELECT * FROM orders WHERE status = 'pending';

-- Проверка на несколько значений
SELECT * FROM orders WHERE status IN ('pending', 'processing');

-- Сравнение с помощью операторов (если важен порядок)
-- Enum значения сравниваются по порядку их определения
SELECT * FROM orders WHERE status > 'processing';  -- shipped, delivered, cancelled, refunded
```

**Индексы на `Enum`:**
```sql
-- B-Tree индекс на Enum (работает эффективно)
CREATE INDEX idx_orders_status ON orders(status);

-- Частичный индекс на Enum
CREATE INDEX idx_orders_active ON orders(created_at)
WHERE status IN ('pending', 'processing');
```

## Расширенные типы данных PostgreSQL

### Геометрические типы

**PostgreSQL** поддерживает богатый набор геометрических типов данных:**

**Point (точка):**
```sql
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    name TEXT,
    location POINT
);

INSERT INTO locations (name, location)
VALUES ('Office', '(10, 20)');

-- Поиск ближайших точек
SELECT name, location <-> POINT(15, 25) AS distance
FROM locations
ORDER BY distance
LIMIT 5;
```

**Line, `Lseg`, Box, `Path`, `Polygon`, `Circle`:**
```sql
-- Линия
CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    route LINE  -- Формат: {A,B,C}
);

-- Отрезок
CREATE TABLE segments (
    id SERIAL PRIMARY KEY,
    segment LSEG  -- Формат: ((x1,y1),(x2,y2))
);

-- Прямоугольник
CREATE TABLE areas (
    id SERIAL PRIMARY KEY,
    area BOX  -- Формат: ((x1,y1),(x2,y2))
);

-- Путь (открытый или закрытый)
CREATE TABLE paths (
    id SERIAL PRIMARY KEY,
    path PATH  -- Формат: [(x1,y1),...] (открытый) или ((x1,y1),...) (закрытый)
);

-- Многоугольник
CREATE TABLE polygons (
    id SERIAL PRIMARY KEY,
    shape POLYGON  -- Формат: ((x1,y1),...)
);

-- Круг
CREATE TABLE circles (
    id SERIAL PRIMARY KEY,
    circle CIRCLE  -- Формат: <(x,y),r>
);
```

### Сетевые типы

**CIDR и `INET`:**
```sql
CREATE TABLE ip_addresses (
    id SERIAL PRIMARY KEY,
    ip INET,      -- Формат: 192.168.0.1/24 или 2001:4f8:3:ba::1/64
    network CIDR  -- Только сеть: 192.168.0.0/24
);

INSERT INTO ip_addresses (ip, network)
VALUES ('192.168.1.100', '192.168.0.0/16');

-- Поиск по сети
SELECT * FROM ip_addresses WHERE ip <<= '192.168.0.0/24'::INET;
```

**MAC `Address`:**
```sql
CREATE TABLE devices (
    id SERIAL PRIMARY KEY,
    mac_address MACADDR,      -- Формат: 08:00:2b:01:02:03
    mac_address8 MACADDR8     -- Формат EUI-64: 08:00:2b:ff:fe:01:02:03
);
```

### XML тип

```sql
CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    content XML
);

INSERT INTO documents (content)
VALUES ('<root><title>Test</title></root>');

-- Поиск в XML
SELECT * FROM documents
WHERE content::text LIKE '%Test%';

-- Использование XPath (требует расширение)
SELECT xpath('/root/title/text()', content)
FROM documents;
```

### Дополнительные функции для работы с типами

**Приведение типов (CAST):**
```sql
-- Явное приведение
SELECT '123'::INTEGER;
SELECT '2024-01-01'::DATE;
SELECT '{"key":"value"}'::JSONB;

-- Функция CAST
SELECT CAST('123' AS INTEGER);
SELECT CAST('2024-01-01' AS DATE);
```

**Проверка типа:**
```sql
-- Проверить тип значения
SELECT pg_typeof(123);  -- integer
SELECT pg_typeof('text');  -- text
SELECT pg_typeof(ARRAY[1,2,3]);  -- integer[]
```

**Получение информации о типе:**
```sql
-- Информация о типе
SELECT typname, typlen, typtype
FROM pg_type
WHERE typname = 'integer';

-- Получить все доступные типы
SELECT typname, typlen, typtype
FROM pg_type
WHERE typtype IN ('b', 'c', 'd', 'e', 'p', 'r')
ORDER BY typname;

## Пользовательские типы данных

### Составные типы (Composite Types)

Создание составного типа:
```sql
-- Создание типа для адреса
`CREATE TYPE` address `AS` (
    street `VARCHAR`(`100`),
    city `VARCHAR`(50),
    state `VARCHAR`(2),
    `zip_code VARCHAR`(10),
    country `VARCHAR`(50) `DEFAULT` '`USA`'
);

-- Создание типа для контакта
`CREATE TYPE contact_info AS` (
    email `VARCHAR`(`255`),
    phone `VARCHAR`(20),
    address address
);

-- Использование в таблице
`CREATE TABLE` customers (
    id `SERIAL PRIMARY KEY`,
    name `VARCHAR`(`100`) `NOT NULL`,
    contact `contact_info`,
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- Вставка данных
`INSERT INTO` customers (name, contact) `VALUES` (
    '`John Doe`',
    `ROW`(
        'john`@example`.com',
        '+1-555-0123',
        `ROW`('123 `Main St`', '`Anytown`', '`CA`', '12345', '`USA`')
    )::`contact_info`
);

-- Доступ к полям составного типа
`SELECT`
    name,
    (contact).email,
    (contact).phone,
    ((contact).address).city,
    ((contact).address).`zip_code`
`FROM` customers;

-- Обновление полей
`UPDATE` customers
`SET contact.email` = 'newemail`@example`.com'
`WHERE` id = 1;

`UPDATE` customers
`SET` contact = `ROW`(
    'updated`@example`.com',
    '+1-555-9999',
    `ROW`('456 `Oak St`', '`Newtown`', '`NY`', '67890', '`USA`')
)::`contact_info`
`WHERE` id = 1;
```text

Функции для работы с составными типами:
```sql
-- Функция для создания адреса
`CREATE OR REPLACE FUNCTION create_address`(
    `p_street VARCHAR`,
    `p_city VARCHAR`,
    `p_state VARCHAR`,
    `p_zip VARCHAR`,
    `p_country VARCHAR DEFAULT` '`USA`'
) `RETURNS` address `AS` $$
`BEGIN`
    `RETURN ROW`(`p_street`, `p_city`, `p_state`, `p_zip`, `p_country`)::address;
`END`;
$$ `LANGUAGE` plpgsql;

-- Функция для форматирования адреса
`CREATE OR REPLACE FUNCTION format_address`(addr address) `RETURNS TEXT AS` $$
`BEGIN`
    `RETURN addr.street` || ', ' || `addr.city` || ', ' ||
           `addr.state` || ' ' || addr.`zip_code` ||
           `CASE WHEN addr.country` != '`USA`' `THEN` ', ' || `addr.country ELSE` '' `END`;
`END`;
$$ `LANGUAGE` plpgsql;

-- Использование функций
`SELECT` name, `format_address`((contact).address)
`FROM` customers;
```text

### Перечисления с дополнительными атрибутами

Расширенные enum с использованием доменов:
```sql
-- Создание базового enum
`CREATE TYPE user_status AS ENUM` ('active', 'inactive', 'suspended', 'banned');

-- Создание домена с дополнительной логикой
`CREATE DOMAIN user_status_domain AS user_status`
    `CHECK` (`VALUE IN` ('active', 'inactive', 'suspended', 'banned'))
    `DEFAULT` 'active';

-- Функция для проверки статуса
`CREATE OR REPLACE FUNCTION is_active_status`(status `user_status`) `RETURNS BOOLEAN AS` $$
`BEGIN`
    `RETURN` status = 'active';
`END`;
$$ `LANGUAGE` plpgsql;

-- Использование в таблице
`CREATE TABLE` users (
    id `SERIAL PRIMARY KEY`,
    username `VARCHAR`(50) `UNIQUE NOT NULL`,
    status `user_status_domain DEFAULT` 'active',
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP,
    `updated_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- Функция для изменения статуса с проверками
`CREATE OR REPLACE FUNCTION change_user_status`(
    `p_user_id INTEGER`,
    `p_new_status user_status`,
    `p_reason TEXT DEFAULT NULL`
) `RETURNS` void `AS` $$
`DECLARE`
    `old_status user_status`;
`BEGIN`
    — Получаем текущий статус
    `SELECT` status `INTO old_status FROM` users `WHERE` id = `p_user_id`;

    `IF NOT FOUND THEN`
        `RAISE EXCEPTION` '`User with ID` % not found', `p_user_id`;
    `END IF`;

    — Бизнес-логика валидации
    `IF old_status` = 'banned' `AND p_new_status` != 'banned' `THEN`
        `RAISE EXCEPTION` '`Cannot change status from` banned';
    `END IF`;

    `IF p_new_status` = 'suspended' `AND p_reason IS NULL THEN`
        `RAISE EXCEPTION` '`Reason required for suspension`';
    `END IF`;

    — Обновляем статус
    `UPDATE` users `SET` status = `p_new_status`, `updated_at` = CURRENT_TIMESTAMP
    `WHERE` id = `p_user_id`;

    — Логируем изменение
    `INSERT INTO user_status_log` (`user_id`, `old_status`, `new_status`, reason, `changed_at`)
    `VALUES` (`p_user_id`, `old_status`, `p_new_status`, `p_reason`, CURRENT_TIMESTAMP);
`END`;
$$ `LANGUAGE` plpgsql;
```text

## Расширения PostgreSQL

### Установка и управление расширениями

Базовые операции с расширениями:
```sql
-- Просмотр доступных расширений
`SELECT` name, `default_version`, `installed_version`, comment
`FROM pg_available_extensions`
`ORDER BY` name;

-- Установка расширения
`CREATE EXTENSION IF NOT EXISTS` "`uuid-ossp`";

-- Удаление расширения
`DROP EXTENSION IF EXISTS` "`uuid-ossp`";

-- Обновление расширения
`ALTER EXTENSION` "`uuid-ossp`" `UPDATE TO` '1.1';

-- Просмотр установленных расширений
`SELECT` extname, extversion, extrelocatable
`FROM pg_extension`
`ORDER BY` extname;
```text

Расширения для работы с типами данных:

```sql
-- hstore для хранения пар ключ-значение
`CREATE EXTENSION` hstore;

`CREATE TABLE user_preferences` (
    `user_id INTEGER PRIMARY KEY`,
    preferences hstore
);

`INSERT INTO user_preferences VALUES`
(1, 'theme => dark, language => en, notifications => on'::hstore);

`SELECT` * `FROM user_preferences`
`WHERE` preferences -> 'theme' = 'dark';

-- ltree для иерархических структур
`CREATE EXTENSION` ltree;

`CREATE TABLE` categories (
    id `SERIAL PRIMARY KEY`,
    name `VARCHAR`(`100`),
    path ltree
);

`INSERT INTO` categories (name, path) `VALUES`
('`Electronics`', 'electronics'),
('`Laptops`', '`electronics.laptops`'),
('`Gaming Laptops`', '`electronics.laptops.gaming`');

`SELECT` * `FROM` categories
`WHERE` path <@ '`electronics.laptops`'; — Потомки

-- intarray для массивов целых чисел
`CREATE EXTENSION` intarray;

`CREATE TABLE` articles (
    id `SERIAL PRIMARY KEY`,
    title `TEXT`,
    tags integer[]
);

`CREATE INDEX idx_articles_tags ON` articles `USING GIN` (tags gin__int_ops);

`SELECT` * `FROM` articles
`WHERE` tags && `ARRAY`[1,3,5]; — Пересечение массивов
```text

### Создание пользовательских расширений

Структура расширения:
```
**my_extension**/
├── **my_extension.control**
├── **my_extension.sql**
├── **README.md**
└── **test**/
    └── **sql**/
        └── **my_extension_test.sql**
```text

Пример простого расширения:
```sql
-- `my_extension`.control
comment = '`My custom PostgreSQL extension`'
`default_version` = '1.0'
`module_pathname` = `'my_extension'`
relocatable = `true`
```text

```sql
-- `my_extension`.sql
-- Создание типа
`CREATE TYPE` rgb `AS` (
    r integer,
    g integer,
    b integer
);

-- Функция для создания `RGB`
`CREATE OR REPLACE FUNCTION make_rgb`(r int, g int, b int) `RETURNS` rgb `AS` $$
`BEGIN`
    `IF` r < 0 `OR` r > `255 OR` g < 0 `OR` g > `255 OR` b < 0 `OR` b > `255 THEN`
        `RAISE EXCEPTION` '`RGB` values must be `between 0 and 255`';
    `END IF`;
    `RETURN` (r, g, b)::rgb;
`END`;
$$ `LANGUAGE` plpgsql `IMMUTABLE`;

-- Оператор для сложения цветов
`CREATE OR REPLACE FUNCTION rgb_add`(rgb, rgb) `RETURNS` rgb `AS` $$
`BEGIN`
    `RETURN` (
        `LEAST`(($1).r + ($2).r, `255`),
        `LEAST`(($1).g + ($2).g, `255`),
        `LEAST`(($1).b + ($2).b, `255`)
    )::rgb;
`END`;
$$ `LANGUAGE` plpgsql `IMMUTABLE`;

-- Регистрация оператора
`CREATE OPERATOR` + (
    `FUNCTION` = `rgb_add`,
    `LEFTARG` = rgb,
    `RIGHTARG` = rgb
);
```text

Установка расширения:
```sql
-- Создание расширения в базе данных
`CREATE EXTENSION my_extension`;

-- Использование
`SELECT make_rgb`(`255`, 0, 0) + `make_rgb`(0, `255`, 0); — (`255`, `255`, 0)
```text

## Продвинутые техники работы с массивами

### Многомерные массивы

```sql
-- Создание таблицы с многомерным массивом
`CREATE TABLE matrix_data` (
    id `SERIAL PRIMARY KEY`,
    `matrix_name VARCHAR`(50),
    data integer[][],  — Двумерный массив
    dimensions integer[]  — Размеры [rows, cols]
);

-- Вставка матрицы 3x3
`INSERT INTO matrix_data` (`matrix_name`, data, dimensions) `VALUES` (
    '`identity_3x3`',
    `ARRAY`[
        [1, 0, 0],
        [0, 1, 0],
        [0, 0, 1]
    ],
    `ARRAY`[3, 3]
);

-- Доступ к элементам
`SELECT`
    `matrix_name`,
    data[1][1] as `top_left`,
    data[2][3] as `middle_right`,
    `array_length`(data, 1) as rows,
    `array_length`(data, 2) as cols
`FROM matrix_data`;

-- Транспонирование матрицы
`CREATE OR REPLACE FUNCTION transpose_matrix`(matrix integer[][])
`RETURNS` integer[][] `AS` $$
`DECLARE`
    result integer[][];
    rows integer := `array_length`(matrix, 1);
    cols integer := `array_length`(matrix, 2);
    i integer;
    j integer;
`BEGIN`
    — Инициализация результата
    result := `ARRAY`[]::integer[][];

    `FOR` j `IN 1`..cols `LOOP`
        result[j] := `ARRAY`[]::integer[];
        `FOR` i `IN 1`..rows `LOOP`
            result[j][i] := matrix[i][j];
        `END LOOP`;
    `END LOOP`;

    `RETURN` result;
`END`;
$$ `LANGUAGE` plpgsql;

-- Умножение матриц
`CREATE OR REPLACE FUNCTION multiply_matrices`(a integer[][], b integer[][])
`RETURNS` integer[][] `AS` $$
`DECLARE`
    result integer[][];
    `a_rows` integer := `array_length`(a, 1);
    `a_cols` integer := `array_length`(a, 2);
    `b_cols` integer := `array_length`(b, 2);
    i integer;
    j integer;
    k integer;
    sum integer;
`BEGIN`
    `IF array_length`(b, 1) != `a_cols THEN`
        `RAISE EXCEPTION` '`Matrix dimensions do not` match for multiplication';
    `END IF`;

    result := `ARRAY`[]::integer[][];

    `FOR` i `IN 1`..`a_rows LOOP`
        result[i] := `ARRAY`[]::integer[];
        `FOR` j `IN 1`..`b_cols LOOP`
            sum := 0;
            `FOR` k `IN 1`..`a_cols LOOP`
                sum := sum + a[i][k] * b[k][j];
            `END LOOP`;
            result[i][j] := sum;
        `END LOOP`;
    `END LOOP`;

    `RETURN` result;
`END`;
$$ `LANGUAGE` plpgsql;
```text

### Массивы с пользовательскими типами

```sql
-- Создание типа для точки
`CREATE TYPE` point `AS` (
    x double precision,
    y double precision
);

-- Массив точек для полигона
`CREATE TABLE` polygons (
    id `SERIAL PRIMARY KEY`,
    name `VARCHAR`(`100`),
    vertices point[]
);

-- Вставка полигона
`INSERT INTO` polygons (name, vertices) `VALUES` (
    'triangle',
    `ARRAY`[
        (0, 0)::point,
        (1, 0)::point,
        (`0.5`, 1)::point
    ]
);

-- Вычисление площади полигона
`CREATE OR REPLACE FUNCTION polygon_area`(vertices point[])
`RETURNS` double precision `AS` $$
`DECLARE`
    n integer := `array_length`(vertices, 1);
    area double precision := 0;
    i integer;
    j integer;
`BEGIN`
    `IF` n < 3 `THEN`
        `RETURN 0`;
    `END IF`;

    `FOR` i `IN 1`..n `LOOP`
        j := i % n + 1;
        area := area + (vertices[i]).x * (vertices[j]).y;
        area := area — (vertices[j]).x * (vertices[i]).y;
    `END LOOP`;

    `RETURN` abs(area) / 2;
`END`;
$$ `LANGUAGE` plpgsql;

-- Использование
`SELECT` name, `polygon_area`(vertices) as area
`FROM` polygons;
```text

### Индексация массивов

```sql
-- `GIN` индекс для массивов
`CREATE INDEX idx_tags_gin ON` articles `USING GIN` (tags);

-- `GiST` индекс для массивов (для поиска подмножеств)
`CREATE INDEX idx_tags_gist ON` articles `USING GIST` (tags);

-- Запросы с использованием индексов
-- Пересечение (любые общие элементы)
`SELECT` * `FROM` articles `WHERE` tags && `ARRAY`['postgresql', 'database'];

-- Содержит все элементы
`SELECT` * `FROM` articles `WHERE` tags @> `ARRAY`['postgresql'];

-- Содержится в (является подмножеством)
`SELECT` * `FROM` articles `WHERE` tags <@ `ARRAY`['postgresql', 'database', 'sql'];

-- Расстояние между массивами (количество общих элементов)
`SELECT` *,
       tags && `ARRAY`['postgresql', 'database'] as `has_intersection`,
       `array_length`(`array_intersect`(tags, `ARRAY`['postgresql', 'database']), 1) as `common_count`
`FROM` articles
`ORDER BY common_count DESC`;
```text

## Продвинутые техники работы с JSONB

### JSON Schema валидация

```sql
-- Установка расширения для `JSON Schema`
`CREATE EXTENSION IF NOT EXISTS pg_jsonschema`;

-- Создание таблицы с `JSON Schema` валидацией
`CREATE TABLE user_profiles` (
    id `SERIAL PRIMARY KEY`,
    `user_id INTEGER REFERENCES` users(id),
    `profile_data JSONB`,
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP,

    — Валидация по схеме
    `CONSTRAINT valid_profile`
        `CHECK` (`jsonb_matches_schema`(
            '{
                "type": "object",
                "properties": {
                    "name": {"type": "string", "`minLength`": 1},
                    "age": {"type": "integer", "minimum": 0, "maximum": `150`},
                    "email": {"type": "string", "format": "email"},
                    "preferences": {
                        "type": "object",
                        "properties": {
                            "theme": {"enum": ["light", "dark"]},
                            "notifications": {"type": "boolean"}
                        }
                    }
                },
                "required": ["name", "email"]
            }'::json,
            `profile_data`
        ))
);

-- Вставка валидных данных
`INSERT INTO user_profiles` (`user_id`, `profile_data`) `VALUES` (
    1,
    '{
        "name": "`John Doe`",
        "age": 30,
        "email": "john`@example`.com",
        "preferences": {
            "theme": "dark",
            "notifications": `true`
        }
    }'::jsonb
);
```text

### JSONB в OLAP кубах

```sql
-- Создание `OLAP` куба на `JSONB`
`CREATE TABLE sales_cube` (
    id `SERIAL PRIMARY KEY`,
    dimensions `JSONB`,  — {"date": "2024-01", "region": "`US`", "product": "`Widget`"}
    measures `JSONB`,    — {"revenue": `1000.50`, "quantity": 10, "discount": `0.05`}
    metadata `JSONB`,    — {"source": "web", "currency": "`USD`"}
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- Индексы для `OLAP` запросов
`CREATE INDEX idx_dimensions_gin ON sales_cube USING GIN` (dimensions);
`CREATE INDEX idx_measures_gin ON sales_cube USING GIN` (measures);

-- Вставка данных
`INSERT INTO sales_cube` (dimensions, measures, metadata) `VALUES`
(
    '{"date": "2024-01", "region": "`US`", "product": "`Widget A`"}'::jsonb,
    '{"revenue": `1250.00`, "quantity": 25, "discount": `0.10`}'::jsonb,
    '{"source": "web", "currency": "`USD`"}'::jsonb
),
(
    '{"date": "2024-01", "region": "`EU`", "product": "`Widget B`"}'::jsonb,
    '{"revenue": `980.50`, "quantity": 15, "discount": `0.05`}'::jsonb,
    '{"source": "mobile", "currency": "`EUR`"}'::jsonb
);

-- `OLAP` запросы
-- Продажи по регионам
`SELECT`
    dimensions->>'region' as region,
    sum((measures->>'revenue')::numeric) as `total_revenue`,
    sum((measures->>'quantity')::integer) as `total_quantity`,
    avg((measures->>'discount')::numeric) as `avg_discount`
`FROM sales_cube`
`WHERE` dimensions->>'date' = '2024-01'
`GROUP BY` dimensions->>'region';

-- Продажи по продуктам с фильтрами
`SELECT`
    dimensions->>'product' as product,
    measures->>'revenue' as revenue
`FROM sales_cube`
`WHERE` dimensions @> '{"region": "`US`"}'::jsonb
  `AND` (measures->>'revenue')::numeric > `1000`
`ORDER BY` (measures->>'revenue')::numeric `DESC`;

-- Сводная статистика
`SELECT`
    count(*) as `total_records`,
    count(`DISTINCT` dimensions->>'region') as `regions_count`,
    count(`DISTINCT` dimensions->>'product') as `products_count`,
    sum((measures->>'revenue')::numeric) as `total_revenue`,
    avg((measures->>'revenue')::numeric) as `avg_revenue`,
    min((measures->>'revenue')::numeric) as `min_revenue`,
    max((measures->>'revenue')::numeric) as `max_revenue`
`FROM sales_cube`;
```text

### JSONB для поиска и фильтрации

```sql
-- Продвинутые `JSONB` запросы
`CREATE TABLE products_catalog` (
    id `SERIAL PRIMARY KEY`,
    name `VARCHAR`(`255`),
    attributes `JSONB`,  — Гибкие атрибуты продукта
    tags `TEXT`[],
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- `GIN` индекс для `JSONB`
`CREATE INDEX idx_attributes_gin ON products_catalog USING GIN` (attributes);

-- Вставка тестовых данных
`INSERT INTO products_catalog` (name, attributes, tags) `VALUES`
('`Laptop Pro`', '{
    "brand": "`TechCorp`",
    "category": "electronics",
    "specs": {
        "cpu": "`Intel i7`",
        "ram": "16GB",
        "storage": "512GB `SSD`",
        "screen": "15.6 inch"
    },
    "price": `1299.99`,
    "`in_stock`": `true`,
    "rating": `4.5`,
    "reviews": [
        {"user": "john", "rating": 5, "comment": "`Great laptop`!"},
        {"user": "jane", "rating": 4, "comment": "`Good value`"}
    ]
}'::jsonb, `ARRAY`['electronics', 'laptop', 'premium']),
('`Wireless Headphones`', '{
    "brand": "`AudioMax`",
    "category": "electronics",
    "specs": {
        "`battery_life`": "30h",
        "connectivity": "`Bluetooth 5.0`",
        "`noise_cancelling`": `true`
    },
    "price": `199.99`,
    "`in_stock`": `true`,
    "rating": `4.2`
}'::jsonb, `ARRAY`['electronics', 'audio', 'wireless']);

-- Продвинутые запросы
-- Поиск по вложенным атрибутам
`SELECT` name, attributes->'specs'->>'cpu' as cpu
`FROM products_catalog`
`WHERE` attributes @> '{"specs": {"cpu": "`Intel i7`"}}'::jsonb;

-- Фильтрация по диапазону цен
`SELECT` name, (attributes->>'price')::numeric as price
`FROM products_catalog`
`WHERE` (attributes->>'price')::numeric `BETWEEN 100 AND 500`;

-- Поиск в массивах внутри `JSON`
`SELECT` name, `jsonb_array_elements`(attributes->'reviews')->>'user' as reviewer
`FROM products_catalog`
`WHERE` attributes ? 'reviews';

-- Агрегация по `JSON` полям
`SELECT`
    attributes->>'brand' as brand,
    count(*) as `product_count`,
    avg((attributes->>'price')::numeric) as `avg_price`,
    min((attributes->>'price')::numeric) as `min_price`,
    max((attributes->>'price')::numeric) as `max_price`
`FROM products_catalog`
`WHERE` attributes->>'category' = 'electronics'
`GROUP BY` attributes->>'brand';

-- Полнотекстовый поиск в `JSON`
`SELECT` name,
       `jsonb_object_keys`(attributes) as `top_level_keys`
`FROM products_catalog`
`WHERE` attributes @@ '`specs.cpu`:* & price < 1500';

-- Обновление `JSON` полей
`UPDATE products_catalog`
`SET` attributes = attributes || '{"`discounted_price`": `999.99`}'::jsonb
`WHERE` name = '`Laptop Pro`' `AND` (attributes->>'price')::numeric > `1000`;

-- Удаление ключей из `JSON`
`UPDATE products_catalog`
`SET` attributes = attributes - '`old_field`'
`WHERE` attributes ? '`old_field`';
```text

## Диапазоны и временные ряды

### Продвинутые диапазонные типы

```sql
-- Создание таблицы с диапазонами
`CREATE TABLE room_bookings` (
    id `SERIAL PRIMARY KEY`,
    `room_id INTEGER NOT NULL`,
    `booking_period` tsrange `NOT NULL`,
    `guest_name VARCHAR`(`100`),
    `booking_status VARCHAR`(20) `DEFAULT` 'confirmed',

    — Исключение пересекающихся бронирований
    `EXCLUDE` (`room_id WITH` =, `booking_period WITH` &&)
    `WHERE` (`booking_status` = 'confirmed')
);

-- Вставка бронирований
`INSERT INTO room_bookings` (`room_id`, `booking_period`, `guest_name`) `VALUES`
(`101`, '[2024-01-15 14:00, 2024-01-17 11:00)', '`John Doe`'),
(`102`, '[2024-01-16 15:00, 2024-01-18 10:00)', '`Jane Smith`');

-- Запросы с диапазонами
-- Найти доступные комнаты на определенную дату
`SELECT DISTINCT` r.`room_id`, r.`room_name`
`FROM` rooms r
`WHERE NOT EXISTS` (
    `SELECT 1 FROM room_bookings` rb
    `WHERE` rb.`room_id` = r.`room_id`
      `AND` rb.`booking_period` && '[2024-01-16, 2024-01-17)'::tsrange
      `AND` rb.`booking_status` = 'confirmed'
);

-- Найти пересекающиеся бронирования
`SELECT rb1.id`, `rb2.id`, rb1.`guest_name`, rb2.`guest_name`
`FROM room_bookings` rb1
`JOIN room_bookings` rb2 `ON` rb1.`room_id` = rb2.`room_id`
`WHERE rb1.id` < `rb2.id`
  `AND` rb1.`booking_period` && rb2.`booking_period`
  `AND` rb1.`booking_status` = 'confirmed'
  `AND` rb2.`booking_status` = 'confirmed';

-- Статистика загрузки
`SELECT`
    `room_id`,
    count(*) as `total_bookings`,
    sum(upper(`booking_period`) — lower(`booking_period`)) as `total_booked_hours`
`FROM room_bookings`
`WHERE booking_status` = 'confirmed'
  `AND booking_period` && '[2024-01-01, 2024-02-01)'::tsrange
`GROUP BY room_id`
`ORDER BY total_booked_hours DESC`;
```text

### Интеллектуальный полнотекстовый поиск

#### Настройка поиска на русском языке

```sql
-- Создание конфигурации для русского языка
`CREATE TEXT SEARCH CONFIGURATION russian_config` (`COPY` = russian);

-- Добавление синонимов
`CREATE TEXT SEARCH DICTIONARY russian_synonyms` (
    `TEMPLATE` = synonym,
    `SYNONYMS` = `russian_synonyms`
);

-- Настройка конфигурации
`ALTER TEXT SEARCH CONFIGURATION russian_config`
    `ALTER MAPPING FOR` asciiword, asciihword, `hword_asciipart`,
                      word, hword, `hword_part`
    `WITH russian_stem`, `russian_synonyms`;

-- Создание таблицы с полнотекстовым поиском
`CREATE TABLE` articles (
    id `SERIAL PRIMARY KEY`,
    title `VARCHAR`(`255`),
    content `TEXT`,
    tags `TEXT`[],
    `search_vector TSVECTOR`,
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- Функция для обновления поискового вектора
`CREATE OR REPLACE FUNCTION update_search_vector`()
`RETURNS TRIGGER AS` $$
`BEGIN`
    `NEW`.`search_vector` :=
        setweight(`to_tsvector`('`russian_config`', `COALESCE`(`NEW`.title, '')), 'A') ||
        setweight(`to_tsvector`('`russian_config`', `COALESCE`(`NEW`.content, '')), 'B') ||
        setweight(`to_tsvector`('`russian_config`', `array_to_string`(`NEW`.tags, ' ')), 'C');

    `RETURN NEW`;
`END`;
$$ `LANGUAGE` plpgsql;

-- Триггер для автоматического обновления
`CREATE TRIGGER articles_search_update`
    `BEFORE INSERT OR UPDATE ON` articles
    `FOR EACH ROW EXECUTE FUNCTION update_search_vector`();

-- Индекс для полнотекстового поиска
`CREATE INDEX idx_articles_search ON` articles `USING GIN` (`search_vector`);

-- Вставка тестовых данных
`INSERT INTO` articles (title, content, tags) `VALUES`
('`PostgreSQL` Основы', '`PostgreSQL` является мощной объектно-реляционной системой управления базами данных...', `ARRAY`['postgresql', 'database', 'sql']),
('Индексы в `PostgreSQL`', 'Индексы являются важной частью оптимизации производительности базы данных...', `ARRAY`['postgresql', 'indexes', 'performance']);

-- Продвинутые поисковые запросы
-- Базовый поиск
`SELECT` title, `ts_rank`(`search_vector`, `plainto_tsquery`('`russian_config`', 'postgresql')) as rank
`FROM` articles
`WHERE search_vector` @@ `plainto_tsquery`('`russian_config`', 'postgresql')
`ORDER BY` rank `DESC`;

-- Поиск с морфологией
`SELECT` title,
       `ts_headline`('`russian_config`', content, `plainto_tsquery`('`russian_config`', 'индекс'))
`FROM` articles
`WHERE search_vector` @@ `plainto_tsquery`('`russian_config`', 'индекс');

-- Поиск по фразе
`SELECT` title
`FROM` articles
`WHERE search_vector` @@ `phraseto_tsquery`('`russian_config`', 'система управления');

-- Поиск с весами
`SELECT` title,
       `ts_rank_cd`(`search_vector`, `plainto_tsquery`('`russian_config`', 'база данных'), 32) as rank
`FROM` articles
`WHERE search_vector` @@ `plainto_tsquery`('`russian_config`', 'база данных')
`ORDER BY` rank `DESC`;

-- Статистика поиска
`SELECT` word, ndoc, nentry
`FROM ts_stat`('`SELECT search_vector FROM` articles')
`ORDER BY` ndoc `DESC`, nentry `DESC`
`LIMIT 10`;
```text

#### Расширенный полнотекстовый поиск

```sql
-- Создание таблицы для поиска по документам
`CREATE TABLE` documents (
    id `SERIAL PRIMARY KEY`,
    title `TEXT`,
    content `TEXT`,
    metadata `JSONB`,
    `search_vector TSVECTOR`,
    `last_indexed TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- Продвинутая функция индексации
`CREATE OR REPLACE FUNCTION advanced_search_index`(`doc_title TEXT`, `doc_content TEXT`, `doc_metadata JSONB`)
`RETURNS TSVECTOR AS` $$
`DECLARE`
    result `TSVECTOR`;
`BEGIN`
    — Основной контент
    result := setweight(`to_tsvector`('`russian_config`', `COALESCE`(`doc_title`, '')), 'A');

    — Содержимое с меньшим весом
    result := result || setweight(`to_tsvector`('`russian_config`', `COALESCE`(`doc_content`, '')), 'B');

    — Метаданные
    `IF doc_metadata IS NOT NULL THEN`
        result := result || setweight(`to_tsvector`('`russian_config`',
            `COALESCE`(`doc_metadata`->>'keywords', '') || ' ' ||
            `COALESCE`(`doc_metadata`->>'category', '') || ' ' ||
            `COALESCE`(`doc_metadata`->>'author', '')
        ), 'C');
    `END IF`;

    `RETURN` result;
`END`;
$$ `LANGUAGE` plpgsql `IMMUTABLE`;

-- Триггер для индексации
`CREATE TRIGGER documents_index_trigger`
    `BEFORE INSERT OR UPDATE ON` documents
    `FOR EACH ROW EXECUTE FUNCTION`
        (`lambda` (`NEW`.title, `NEW`.content, `NEW`.metadata));

-- Функция для поиска с подсветкой
`CREATE OR REPLACE FUNCTION search_with_highlights`(
    `search_query TEXT`,
    `limit_results INTEGER DEFAULT 10`
)
`RETURNS TABLE` (
    id `INTEGER`,
    title `TEXT`,
    highlight `TEXT`,
    rank `REAL`
) `AS` $$
`BEGIN`
    `RETURN QUERY`
    `SELECT`
        `d.id`,
        `d.title`,
        `ts_headline`('`russian_config`', `d.content`,
                   `plainto_tsquery`('`russian_config`', `search_query`),
                   'StartSel=<mark>, StopSel=</mark>, MaxWords=50, MinWords=10'),
        `ts_rank`(d.`search_vector`, `plainto_tsquery`('`russian_config`', `search_query`))::`REAL`
    `FROM` documents d
    `WHERE` d.`search_vector` @@ `plainto_tsquery`('`russian_config`', `search_query`)
    `ORDER BY ts_rank`(d.`search_vector`, `plainto_tsquery`('`russian_config`', `search_query`)) `DESC`
    `LIMIT limit_results`;
`END`;
$$ `LANGUAGE` plpgsql;

-- Использование
`SELECT` * `FROM search_with_highlights`('postgresql индексы');
```text

## UUID и генерация идентификаторов

### Продвинутые техники работы с UUID

```sql
-- Создание таблицы с `UUID`
`CREATE TABLE distributed_entities` (
    id `UUID PRIMARY KEY DEFAULT gen_random_uuid`(),
    `tenant_id UUID NOT NULL`,
    `entity_type VARCHAR`(50) `NOT NULL`,
    data `JSONB`,
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP,
    `created_by UUID REFERENCES` users(id)
);

-- Генерация `UUID` версии 1 (время + `MAC` адрес)
`CREATE EXTENSION` "`uuid-ossp`";

`SELECT uuid_generate_v1`();

-- Генерация `UUID` версии 4 (случайный)
`SELECT uuid_generate_v4`();

-- Генерация `UUID` версии 3 (на основе имени)
`SELECT uuid_generate_v3`(`uuid_ns_dns()`, '`example.com`');

-- Генерация `UUID` версии 5 (на основе имени, улучшенная)
`SELECT uuid_generate_v5(uuid_ns_dns(), 'example.com');`

-- Функция для создания префиксных `UUID`
`CREATE OR REPLACE FUNCTION prefixed_uuid`(prefix `TEXT`)
`RETURNS UUID AS` $$
`DECLARE`
    `base_uuid UUID` := `gen_random_uuid()`;
    `prefix_bytes BYTEA`;
    `uuid_bytes BYTEA`;
`BEGIN`
    — Преобразуем префикс в байты (первые 4 символа)
    `prefix_bytes` := substring(prefix, 1, 4)::bytea;

    — Получаем байты `UUID`
    `uuid_bytes` := `uuid_send`(`base_uuid`);

    — Заменяем первые байты
    `uuid_bytes` := overlay(`uuid_bytes PLACING prefix_bytes FROM 1`);

    `RETURN uuid_recv`(`uuid_bytes`);
`END`;
$$ `LANGUAGE` plpgsql;

-- Использование префиксных `UUID`
`SELECT prefixed_uuid`('user'), `prefixed_uuid`('post'), `prefixed_uuid`('comm');

-- Анализ `UUID`
`CREATE OR REPLACE FUNCTION analyze_uuid`(`target_uuid UUID`)
`RETURNS TABLE` (
    version `INTEGER`,
    variant `INTEGER`,
    timestamp `TIMESTAMP`,
    `node_mac TEXT`
) `AS` $$
`DECLARE`
    `uuid_bytes BYTEA` := `uuid_send`(`target_uuid`);
    version `INTEGER`;
    variant `INTEGER`;
    `timestamp_microsecs BIGINT`;
`BEGIN`
    — Извлекаем версию (биты 48-51 первого октета)
    version := (`get_byte`(`uuid_bytes`, 6) >> 4) & 15;

    — Извлекаем вариант (биты 60-63 второго октета)
    variant := `get_byte`(`uuid_bytes`, 8) >> 4;

    — Для версии 1 вычисляем timestamp
    `IF` version = 1 `THEN`
        `timestamp_microsecs` :=
            ((`get_byte`(`uuid_bytes`, 6) & 15) << 24) |
            (`get_byte`(`uuid_bytes`, 7) << 16) |
            (`get_byte`(`uuid_bytes`, 4) << 8) |
            `get_byte`(`uuid_bytes`, 5);

        — Корректировка эпохи (`UUID` epoch = 1582-10-15, `Unix epoch` = 1970-01-01)
        `timestamp_microsecs` := `timestamp_microsecs` + `122192928000000000`;
    `END IF`;

    `RETURN QUERY SELECT`
        version,
        variant,
        `CASE WHEN` version = 1 `THEN`
            `to_timestamp`(timestamp_microsecs / 1000000.0)
        `ELSE NULL END`,
        `CASE WHEN` version = 1 `THEN`
            encode(substring(`uuid_bytes`, 10, 6), 'hex')
        `ELSE NULL END`;
`END`;
$$ `LANGUAGE` plpgsql;

-- Анализ `UUID`
`SELECT` * `FROM analyze_uuid`(`gen_random_uuid()`);
`SELECT` * `FROM analyze_uuid`(`uuid_generate_v1()`);
```text

### Генерация последовательностей и серий

```sql
-- Создание умной последовательности
`CREATE OR REPLACE FUNCTION generate_smart_id`(`entity_type TEXT`)
`RETURNS BIGINT AS` $$
`DECLARE`
    `current_year INTEGER` := `EXTRACT`(`YEAR FROM` CURRENT_DATE);
    `sequence_value INTEGER`;
    result `BIGINT`;
`BEGIN`
    — Получаем следующее значение последовательности для типа
    `EXECUTE` format('`SELECT` nextval(''%`s_seq`'')', `entity_type`) `INTO sequence_value`;

    — Формируем `ID`: `YYYY000000` (год + 6-значный номер)
    result := (`current_year` * `1000000`) + `sequence_value`;

    `RETURN` result;
`END`;
$$ `LANGUAGE` plpgsql;

-- Создание последовательностей для разных типов
`CREATE SEQUENCE user_seq START 1`;
`CREATE SEQUENCE order_seq START 1`;
`CREATE SEQUENCE product_seq START 1`;

-- Использование
`SELECT generate_smart_id`('user'), `generate_smart_id`('order'), `generate_smart_id`('product');

-- Генерация `SKU` для продуктов
`CREATE OR REPLACE FUNCTION generate_sku`(`category_code TEXT`, `subcategory_code TEXT DEFAULT` '')
`RETURNS TEXT AS` $$
`DECLARE`
    `sequence_value INTEGER`;
    sku `TEXT`;
`BEGIN`
    — Получаем последовательность для категории
    `EXECUTE` format('`SELECT` nextval(''%`s_seq`'')', lower(`category_code`)) `INTO sequence_value`;

    — Формируем `SKU`: `CAT-`SUB`-XXXXXX`
    sku := upper(`category_code`);
    `IF subcategory_code` != '' `THEN`
        sku := sku || '-' || upper(`subcategory_code`);
    `END IF`;
    sku := sku || '-' || lpad(`sequence_value::TEXT`, 6, '0');

    `RETURN` sku;
`END`;
$$ `LANGUAGE` plpgsql;

-- Создание последовательностей для `SKU`
`CREATE SEQUENCE electronics_seq START 1`;
`CREATE SEQUENCE clothing_seq START 1`;

-- Генерация `SKU`
`SELECT generate_sku`('electronics', 'laptop'), `generate_sku`('clothing');
```text

## Производительность и оптимизация

### Оптимизация JSONB

```sql
-- Создание эффективных индексов для `JSONB`
`CREATE TABLE user_events` (
    id `SERIAL PRIMARY KEY`,
    `user_id INTEGER NOT NULL`,
    `event_type VARCHAR`(50) `NOT NULL`,
    `event_data JSONB NOT NULL`,
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- `GIN` индекс для общих операций
`CREATE INDEX idx_user_events_gin ON user_events USING GIN` (`event_data`);

-- `B-tree` индекс для конкретных полей
`CREATE INDEX idx_user_events_type ON user_events` ((`event_data`->>'type'));
`CREATE INDEX idx_user_events_timestamp ON user_events` ((`event_data`->>'timestamp'::timestamp));

-- `GiST` индекс для поиска расстояний
`CREATE INDEX idx_user_events_location ON user_events`
`USING GIST` ((`event_data`->'location'->'coordinates'));

-- Запросы с использованием индексов
-- Поиск по типу события
`SELECT` * `FROM user_events`
`WHERE event_data` @> '{"type": "login"}'::jsonb;

-- Поиск по диапазону дат
`SELECT` * `FROM user_events`
`WHERE` (`event_data`->>'timestamp')::timestamp `BETWEEN` '2024-01-01' `AND` '2024-01-31';

-- Поиск по геолокации (в радиусе)
`SELECT` *,
       (`event_data`->'location'->'coordinates') as coords
`FROM user_events`
`WHERE event_data` @@ '`location.coordinates` <@ point(`37.7749`, -122.4194) <-> point(?coordinates) < 1000';

-- Агрегация по `JSON` полям
`SELECT`
    `event_data`->>'type' as `event_type`,
    count(*) as `event_count`,
    avg((`event_data`->>'value')::numeric) as `avg_value`,
    `jsonb_object_agg`(`event_data`->>'category', count()) as `category_counts`
`FROM user_events`
`WHERE created_at` >= CURRENT_DATE - `INTERVAL` '7 days'
`GROUP BY event_data`->>'type'
`ORDER BY event_count DESC`;
```text

### Оптимизация массивов

```sql
-- Создание таблицы с массивами
`CREATE TABLE` articles (
    id `SERIAL PRIMARY KEY`,
    title `TEXT NOT NULL`,
    tags `TEXT`[] `NOT NULL DEFAULT` '{}',
    categories `TEXT`[] `NOT NULL DEFAULT` '{}',
    keywords `TEXT`[] `NOT NULL DEFAULT` '{}',
    published `BOOLEAN DEFAULT FALSE`,
    `created_at TIMESTAMP DEFAULT` CURRENT_TIMESTAMP
);

-- Индексы для массивов
`CREATE INDEX idx_articles_tags_gin ON` articles `USING GIN` (tags);
`CREATE INDEX idx_articles_categories_gin ON` articles `USING GIN` (categories);

-- `GIN` индекс для полнотекстового поиска в массивах
`CREATE INDEX idx_articles_keywords_gin ON` articles
`USING GIN` (`string_to_array`(lower(`array_to_string`(keywords, ' ')), ' '));

-- Эффективные запросы с массивами
-- Статьи с определенными тегами
`SELECT` * `FROM` articles
`WHERE` tags && `ARRAY`['postgresql', 'database'];

-- Статьи содержащие все указанные категории
`SELECT` * `FROM` articles
`WHERE` categories @> `ARRAY`['tutorial', 'advanced'];

-- Статьи с любым из ключевых слов
`SELECT` * `FROM` articles
`WHERE` keywords && `ARRAY`['optimization', 'performance'];

-- Поиск статей по релевантности тегов
`SELECT` *,
       `array_length`(`array_intersect`(tags, `ARRAY`['sql', 'postgres', 'query']), 1) as `relevance_score`
`FROM` articles
`WHERE` tags && `ARRAY`['sql', 'postgres', 'query']
`ORDER BY relevance_score DESC`;

-- Агрегация по массивам
`SELECT`
    unnest(tags) as tag,
    count(*) as `article_count`
`FROM` articles
`WHERE` published = `true`
`GROUP BY` tag
`ORDER BY article_count DESC`
`LIMIT 10`;

-- Обновление массивов
`UPDATE` articles
`SET` tags = `array_append`(tags, 'featured')
`WHERE` id = `123`;

`UPDATE` articles
`SET` tags = `array_remove`(tags, 'draft')
`WHERE` id = `123`;

-- Функции для работы с массивами
`CREATE OR REPLACE FUNCTION array_intersect`(anyarray, anyarray)
`RETURNS` anyarray `AS` $$
    `SELECT ARRAY`(
        `SELECT` unnest($1)
        `INTERSECT`
        `SELECT` unnest($2)
    );
$$ `LANGUAGE` sql `IMMUTABLE`;

`CREATE OR REPLACE FUNCTION array_union`(anyarray, anyarray)
`RETURNS` anyarray `AS` $$
    `SELECT ARRAY`(
        `SELECT` unnest($1)
        `UNION`
        `SELECT` unnest($2)
    );
$$ `LANGUAGE` sql `IMMUTABLE`;
```text

### Кэширование и материализованные представления

```sql
-- Материализованное представление для статистики
`CREATE MATERIALIZED VIEW article_stats AS`
`SELECT`
    `date_trunc`('day', `created_at`) as date,
    count(*) as `total_articles`,
    count(*) `FILTER` (`WHERE` published = `true`) as `published_articles`,
    `array_agg`(`DISTINCT` unnest(tags)) `FILTER` (`WHERE` published = `true`) as `active_tags`,
    `jsonb_object_agg`(
        category,
        count(*)
    ) as `category_stats`
`FROM` articles
`WHERE created_at` >= CURRENT_DATE - `INTERVAL` '30 days'
`GROUP BY date_trunc`('day', `created_at`)
`ORDER BY` date `DESC`;

-- Индекс для быстрого доступа
`CREATE INDEX idx_article_stats_date ON article_stats` (date);

-- Функция для обновления статистики
`CREATE OR REPLACE FUNCTION refresh_article_stats`()
`RETURNS` void `AS` $$
`BEGIN`
    `REFRESH MATERIALIZED VIEW CONCURRENTLY article_stats`;

    — Логируем обновление
    `INSERT INTO refresh_log` (`view_name`, `refreshed_at`, duration)
    `VALUES` ('`article_stats`', CURRENT_TIMESTAMP, `clock_timestamp()` - CURRENT_TIMESTAMP);
`END`;
$$ `LANGUAGE` plpgsql;

-- Планировщик для автоматического обновления
`SELECT cron.schedule`(
    '`refresh-article-stats`',
    '0 */6 * * *',  — Каждые 6 часов
    '`SELECT refresh_article_stats`();'
);
```text

## Интеграция с приложениями

### Spring Boot интеграция

```java
`@Configuration`
public class `DatabaseConfig` {

    `@Bean`
    public `DataSource dataSource`() {
        // Настройка для работы с расширенными типами `PostgreSQL`
        `HikariDataSource dataSource` = new `HikariDataSource()`;
        `dataSource`.`setJdbcUrl`("jdbc:postgresql://localhost:5432/mydb");
        `dataSource`.`setUsername`("user");
        `dataSource`.`setPassword`("password");

        // Регистрация типов `PostgreSQL`
        `dataSource`.`setDataSourceProperties`(`getPostgreSQLProperties()`);

        return `dataSource`;
    }

    private `Properties getPostgreSQLProperties`() {
        `Properties props` = new `Properties()`;
        // Включаем поддержку массивов и `JSONB`
        props.`setProperty`("stringtype", "unspecified");
        return props;
    }
}

// Репозиторий для работы с `JSONB`
`@Repository`
public interface `ArticleRepository` extends `JpaRepository<Article, Long>` {

    // Поиск по `JSONB` полям
    `@Query("SELECT a FROM Article a WHERE a.metadata ->> 'status' = :status")`
    `List<Article> findByStatus(@Param("status") String status);`

    `@Query("SELECT a FROM Article a WHERE a.metadata -> 'tags' ? :tag")`
    `List<Article> findByTag(@Param("tag") String tag);`

    `@Query("SELECT a FROM Article a WHERE (a.metadata ->> 'rating')::float > :minRating")`
    `List<Article> findByMinRating(@Param("minRating") float minRating);`

    // Поиск в массивах
    `@Query("SELECT a FROM Article a WHERE :tag = ANY(a.tags)")`
    `List<Article> findByTagsContaining(@Param("tag") String tag);`
}

// Конвертер для массивов
`@Component`
`@Converter`
public class `StringArrayConverter` implements `AttributeConverter<String[], String>` {

    `@Override`
    public `String convertToDatabaseColumn(String[] attribute)` {
        return attribute != null ? "{" + String.join(",", attribute) + "}" : null;
    }

    `@Override`
    public `String[] convertToEntityAttribute(String dbData)` {
        return dbData != null ?
            dbData.substring(1, dbData.length() — 1).split(","):
            new String[0];
    }
}

// `Entity` с расширенными типами
`@Entity`
`@Table(name = "articles")`
public class `Article` {

    `@Id`
    `@GeneratedValue(strategy = GenerationType.IDENTITY)`
    private `Long id`;

    private `String title`;

    `@Column(columnDefinition = "jsonb")`
    private String metadata; // или JsonNode

    ``@Convert`(converter = `StringArrayConverter`.class)`
    ``@Column`(`columnDefinition` = "text[]")`
    private `String`[] tags;

    ``@Type`(type = "`pg-uuid`")`
    private `UUID authorId`;

    // Геттеры и сеттеры
}
```text

### Hibernate типы

```java
// Пользовательский тип для `JSONB`
``@TypeDef`(name = "jsonb", `typeClass` = `JsonBinaryType`.class)`

`@Entity`
``@Table`(name = "`user_profiles`")`
``@TypeDef`(name = "jsonb", `typeClass` = `JsonBinaryType`.class)`
public class `UserProfile` {

    `@Id`
    `@GeneratedValue`
    private `Long id`;

    private `String username`;

    ``@Type`(type = "jsonb")`
    ``@Column`(`columnDefinition` = "jsonb")`
    private Map<`String`, `Object`> preferences;

    ``@Type`(type = "jsonb")`
    ``@Column`(`columnDefinition` = "jsonb")`
    private `List`<`String`> `recentActions`;

    // Для работы с `PostgreSQL` массивами
    ``@Column`(`columnDefinition` = "text[]")`
    private `String`[] roles;

    ``@Type`(type = "`pg-uuid`")`
    private `UUID tenantId`;
}

// Пользовательский тип для перечислений
public enum `UserStatus` {
    `ACTIVE`, `INACTIVE`, `SUSPENDED`, `BANNED`
}

`@Converter`
public class `UserStatusConverter` implements `AttributeConverter`<`UserStatus`, `String`> {

    `@Override`
    public `String convertToDatabaseColumn`(`UserStatus` status) {
        return status != `null` ? `status.name`().`toLowerCase()` : `null`;
    }

    `@Override`
    public `UserStatus convertToEntityAttribute`(`String dbData`) {
        return `dbData` != `null` ? `UserStatus`.`valueOf`(`dbData`.`toUpperCase()`) : `null`;
    }
}
```text

### JDBC работа с расширенными типами

```java
public class `PostgreSQLAdvancedTypesDemo` {

    private final `DataSource dataSource`;

    public void `insertArticleWithJsonb()` throws SQLException {
        `String sql` = """
            `INSERT INTO` articles (title, content, metadata, tags)
            `VALUES` (?, ?, ?::jsonb, ?)
            """;

        try (`Connection conn` = `dataSource`.`getConnection()`;
             `PreparedStatement` stmt = conn.`prepareStatement`(sql)) {

            stmt.`setString`(1, "`Advanced PostgreSQL Types`");
            stmt.`setString`(2, "`Content about PostgreSQL types`...");

            // `JSONB` объект
            `String jsonMetadata` = """
                {
                    "author": "`John Doe`",
                    "published": `true`,
                    "rating": `4.5`,
                    "categories": ["postgresql", "database", "tutorial"]
                }
                """;
            stmt.`setString`(3, `jsonMetadata`);

            // Массив
            `Array tagsArray` = conn.`createArrayOf`("`TEXT`",
                new `String`[]{"postgresql", "types", "tutorial"});
            stmt.`setArray`(4, `tagsArray`);

            stmt.`executeUpdate()`;
        }
    }

    public void `queryWithJsonb()` throws SQLException {
        `String sql` = """
            `SELECT` id, title, metadata->>'author' as author,
                   metadata->>'rating' as rating,
                   tags
            `FROM` articles
            `WHERE` metadata @> '{"published": `true`}'::jsonb
              `AND` metadata->>'rating' > '4.0'
              `AND` 'tutorial' = `ANY`(tags)
            """;

        try (`Connection conn` = `dataSource`.`getConnection()`;
             `PreparedStatement` stmt = conn.`prepareStatement`(sql);
             `ResultSet` rs = stmt.`executeQuery()`) {

            while (`rs.next`()) {
                `System`.`out.printf`("`Article`: %s by %s (rating: %s)%n",
                    rs.`getString`("title"),
                    rs.`getString`("author"),
                    rs.`getString`("rating"));

                // Работа с массивами
                `Array tagsArray` = rs.`getArray`("tags");
                `String`[] tags = (`String`[]) `tagsArray`.`getArray()`;
                `System`.`out.println`("`Tags`: " + `Arrays`.`toString`(tags));
            }
        }
    }

    public void `workWithRanges()` throws SQLException {
        `String sql` = """
            `SELECT` id, `room_name`, `booking_period`
            `FROM room_bookings`
            `WHERE booking_period` && '[2024-01-15, 2024-01-16]'::tsrange
            """;

        try (`Connection conn` = `dataSource`.`getConnection()`;
             `PreparedStatement` stmt = conn.`prepareStatement`(sql);
             `ResultSet` rs = stmt.`executeQuery()`) {

            while (`rs.next`()) {
                PGobject range = (PGobject) rs.`getObject`("`booking_period`");
                `System`.`out.printf`("`Room` %s booked: %s%n",
                    rs.`getString`("`room_name`"), range.`getValue()`);
            }
        }
    }
}
```text

Расширенные типы данных PostgreSQL предоставляют мощные возможности для создания гибких и эффективных схем баз данных. Ключевые аспекты успешного использования:

### Основные принципы:

1. Выбор подходящего типа: JSONB для гибких данных, массивы для простых списков, enum для ограниченных наборов значений
2. Индексация: GIN для JSONB и массивов, специализированные индексы для конкретных паттернов запросов
3. Валидация: Использование доменов и ограничений для обеспечения целостности данных
4. Производительность: Понимание накладных расходов каждого типа и оптимизация запросов

## Лучшие практики

- JSONB: Отлично подходит для метаданных, пользовательских настроек, динамических атрибутов
- Массивы: Идеальны для тегов, категорий, простых списков фиксированного размера
- Enum: Для статусов, категорий, других ограниченных наборов значений
- Диапазоны: Для временных интервалов, числовых диапазонов, дат
- UUID: Для распределенных систем, предотвращения конфликтов ID

### Производительность:

- Индексы: GIN для JSONB и массивов, B-tree для конкретных полей
- Кэширование: Материализованные представления для сложных агрегатов
- Партиционирование: Для больших таблиц с JSONB или массивами
- Оптимизация запросов: Использование соответствующих операторов (@>, &&, etc.)

### Безопасность:

- Валидация: Домены и CHECK ограничения для контроля вводимых данных
- RLS: Row Level Security для мультиарендных приложений
- Аудит: Триггеры для отслеживания изменений в JSONB полях

## Решение проблем

**Ошибки приведения типов (cast):** явно приводите значения к нужному типу (`::type`, `CAST`) при вставке и сравнении. Для JSONB проверяйте структуру и используйте операторы `->`, `->>`, `@>` в соответствии с типом поля. Используйте домены и CHECK для валидации на уровне БД.

**Медленные запросы по JSONB/массивам:** создайте GIN-индекс для полей, по которым часто фильтруют или ищут. Избегайте полнотекстового поиска по большим JSONB без индекса. Для агрегаций по элементам массивов рассмотрите `unnest` и индексы по выражению.

**Проблемы с датами и таймзонами:** храните в `TIMESTAMPTZ`, задавайте таймзону сессии при необходимости. При сравнении с `date`/`timestamp` учитывайте приведение и границы суток. Ошибки округления или формата — используйте единый формат (например, ISO 8601) в приложении.

**Переполнение или неточность числовых типов:** для денег и высокой точности используйте `NUMERIC`; для целых — `BIGINT` при риске переполнения. Проверяйте диапазоны при миграции с других СУБД.

PostgreSQL предоставляет богатый набор типов данных, которые позволяют создавать современные, гибкие и производительные приложения. Правильное использование этих типов в сочетании с хорошим проектированием схемы обеспечивает высокую эффективность и надежность систем.
```

## См. также

- [[postgres-admin|PostgreSQL: администрирование и обслуживание]]
- [[postgres-backup-restore|PostgreSQL: Резервное копирование и восстановление]]
- [[postgres-basics|PostgreSQL: Полное руководство по основам и мониторингу]]
- [[postgres-data-ops|PostgreSQL: операции с данными (CRUD)]]
- [[postgres-design|PostgreSQL: проектирование и нормализация]]
