---
title: "PostgreSQL: типы индексов"
description: "Кратко: когда выбирать B-Tree, Hash, GIN, BRIN, GiST и как проверять план выполнения через EXPLAIN."
tags:
  - databases
  - relational
  - postgres-indexes
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# PostgreSQL: типы индексов

Кратко: когда выбирать **B-Tree**, **Hash**, **GIN**, **BRIN**, **GiST** и как проверять план выполнения через **EXPLAIN**.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)

### Обучающие материалы

- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)


См. также: [[postgres-basics]] — [[postgres-queries]].

## Содержание

- [PostgreSQL: типы индексов](#postgresql-типы-индексов)
- [B-Tree](#b-tree)
- [Hash](#hash)
- [GIN](#gin)
- [BRIN](#brin)
- [GiST / SP-GiST](#gist-sp-gist)
- [EXPLAIN для проверки](#explain-для-проверки)
- [Частичные и покрывающие индексы](#частичные-и-покрывающие-индексы)
- [Обслуживание индексов](#обслуживание-индексов)
- [Выбор типа индекса по задаче](#выбор-типа-индекса-по-задаче)
- [Хранение и порядок колонок](#хранение-и-порядок-колонок)
- [Когда индекс не поможет](#когда-индекс-не-поможет)
- [Подробное описание типов индексов](#подробное-описание-типов-индексов)
  - [B-Tree индекс — подробности](#b-tree-индекс-подробности)
  - [Hash индекс — подробности](#hash-индекс-подробности)
  - [GiST индекс — подробности](#gist-индекс-подробности)
  - [SP-GiST индекс — подробности](#sp-gist-индекс-подробности)
  - [GIN индекс — подробности](#gin-индекс-подробности)
  - [BRIN индекс — подробности](#brin-индекс-подробности)
- [Специальные техники работы с индексами](#специальные-техники-работы-с-индексами)
  - [Expression индексы](#expression-индексы)
  - [Функциональные индексы](#функциональные-индексы)
  - [Покрывающие индексы (Covering Indexes)](#покрывающие-индексы-covering-indexes)
  - [Частичные индексы (Partial Indexes)](#частичные-индексы-partial-indexes)
- [Мониторинг и обслуживание индексов](#мониторинг-и-обслуживание-индексов)
  - [Проверка использования индексов](#проверка-использования-индексов)
  - [Раздувание индексов (Index Bloat)](#раздувание-индексов-index-bloat)
  - [Удаление неиспользуемых индексов](#удаление-неиспользуемых-индексов)
- [Практические рекомендации](#практические-рекомендации)
  - [Когда создавать индексы](#когда-создавать-индексы)
  - [Порядок столбцов в составных индексах](#порядок-столбцов-в-составных-индексах)
  - [Избегание дублирования индексов](#избегание-дублирования-индексов)
  - [Мониторинг производительности индексов](#мониторинг-производительности-индексов)
  - [Оптимизация индексов для конкретных запросов](#оптимизация-индексов-для-конкретных-запросов)
- [Best Practices](#лучшие-практики)

## B-Tree
- По умолчанию: равенство и диапазоны (`=`, `<`, `BETWEEN`).
- Хорош для большинства **OLTP**-выборок.
```sql
-- B-Tree индекс по столбцу email для быстрого поиска по равенству и диапазону
CREATE INDEX idx_users_email ON users(email);
```

## Hash
- Только равенство, без диапазонов.
- Может быть быстрее на точных совпадениях, но менее универсален.

## GIN
- Для массивов, **JSONB**, полнотекста; быстрый поиск по множеству значений.
```sql
-- GIN-индекс по массиву тегов для быстрого поиска по элементам
CREATE INDEX idx_book_tags ON books USING GIN (tags);
```

## BRIN
- Хранит минимумы/максимумы по блокам страниц; эффективен для очень больших таблиц с данными, упорядоченными по колонке (время, ID).
```sql
-- BRIN-индекс по времени для логов с естественным порядком
CREATE INDEX idx_logs_ts ON logs USING BRIN (created_at);
```

## GiST / SP-GiST
- **GiST**: геоданные, диапазоны, **nearest neighbor**.
- **SP-GiST**: разреженные деревья (quad/oct/radix), эффективно на дискретных распределениях.

## EXPLAIN для проверки
- **Проверить, что индекс используется:**
```sql
-- Проверка использования индекса по email
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'a@b.com';
```
- Смотрите на **Index Scan** / **Bitmap Index Scan**, `rows`, `loops`, `cost`. Если `Seq Scan` — проверьте селективность, наличие индекса и условия запроса.

## Частичные и покрывающие индексы
- **Частичный индекс (фильтр по статусу):**
```sql
-- Частичный индекс только для оплаченных заказов
CREATE INDEX idx_orders_paid ON orders(status, created_at)
WHERE status = 'PAID';
```
- **Покрывающий (INCLUDE) — чтобы избежать доп. чтения таблицы:**
```sql
-- Покрывающий индекс с INCLUDE для избежания обращения к таблице
CREATE INDEX idx_orders_lookup ON orders(user_id, created_at DESC)
INCLUDE (status, total_amount);
```

## Обслуживание индексов
- **Перестроение (редко, при сильном блоате):**
```sql
-- Перестроение индекса при раздувании
REINDEX INDEX idx_users_email;
```
- **Анализ статистики:**
```sql
-- Статистика использования индексов по числу сканирований
SELECT relname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes ui
JOIN pg_class c ON ui.indexrelid = c.oid
ORDER BY idx_scan DESC;
```
- Если индекс не используется (`idx_scan` ≈ 0) и не нужен, удалите его, чтобы не тратить место и время на поддержание.

## Выбор типа индекса по задаче
- Равенство/диапазон (OLTP) — **B-Tree** (по умолчанию).
- Теги/массивы/**JSONB** — **GIN**.
- Полнотекст — **GIN**/**GiST** (tsvector).
- Геоданные/диапазоны — **GiST**.
- Лог-серии/время на огромных таблицах — **BRIN**.
- Точное равенство без диапазонов, немного строк — **Hash** (обычно `B-Tree` достаточно).

## Хранение и порядок колонок
- Составной индекс используют слева-направо: `(a,b)` покрывает `a=...` и `a=... AND b=...`, но не `b` без `a`.
- Для фильтров по времени и статусу полезно `(status, `created_at` DESC)`.
- **INCLUDE** помещает доп. колонки в индекс для покрытия, не влияя на порядок сортировки/поиска.
- Поддерживайте ширину индекса: не индексируйте огромные **TEXT** без нужды; при необходимости частичный индекс или выражение (**`lower(col)`).

## Когда индекс не поможет
- Низкая селективность (почти одинаковые значения) — **planner** может выбрать **Seq Scan**.
- **Фильтр с функцией без подходящего **expression**-индекса:**
```sql
-- Expression-индекс по lower(email) для поиска без учёта регистра
CREATE INDEX ON users (lower(email));
-- и запрос с lower(email) использует индекс
```
- **LIKE** с ведущим `%` не использует обычный **B-Tree** (`%foo` или расширение pg_trgm).

## Подробное описание типов индексов

### B-Tree индекс — подробности

**PostgreSQL** предоставляет несколько типов индексов: **B-tree**, **Hash**, **GiST**, **SP-GiST**, **GIN** и **BRIN**. Каждый тип индекса использует свой алгоритм, который лучше всего подходит для разных типов запросов. По умолчанию команда `CREATE INDEX` создает индексы в виде B-дерева, которые подходят для наиболее распространенных ситуаций.

**Основные характеристики `B-Tree`:**
- B-деревья могут обрабатывать запросы на равенство и диапазон данных, которые могут быть отсортированы в некоторый порядок.
- Планировщик запросов **PostgreSQL** будет рассматривать возможность использования индекса B-дерева всякий раз, когда индексируемый столбец участвует в сравнении с помощью одного из этих операторов: `<`, `<=`, `=`, `>=`, `>`

**Операторы, поддерживаемые `B-Tree`:**
```sql
-- Равенство
SELECT * FROM users WHERE email = 'user@example.com';

-- Диапазоны
SELECT * FROM orders WHERE order_date >= '2024-01-01' AND order_date < '2024-02-01';
SELECT * FROM products WHERE price BETWEEN 100 AND 500;

-- Сортировка
SELECT * FROM users ORDER BY created_at DESC;

-- IS NULL / IS NOT NULL
SELECT * FROM users WHERE email IS NULL;
SELECT * FROM users WHERE email IS NOT NULL;
```

**Конструкции, эквивалентные комбинациям этих операторов:**
- `BETWEEN` и `IN` также могут быть реализованы с поиском индекса B-дерева.
- Кроме того, с индексом B-дерева можно использовать условие `IS NULL` или `IS NOT NULL` для столбца индекса.

**Оптимизация `B-Tree` для паттернов:**
```sql
-- Индекс B-дерева может использоваться для LIKE, если паттерн привязан к началу строки
CREATE INDEX idx_users_email ON users(email);

-- Работает с ведущим паттерном
SELECT * FROM users WHERE email LIKE 'user%';  -- Использует индекс

-- Не работает с ведущим %
SELECT * FROM users WHERE email LIKE '%@example.com';  -- НЕ использует индекс!
```

**Для **ILIKE** и ~* (case-insensitive):**
- Индексы B-дерева могут использоваться для `ILIKE` и `~\*`, но только если шаблон начинается с неалфавитных символов, т. е. символов, на которые не влияет преобразование верхнего/нижнего регистра.
- **Для **case-insensitive** поиска лучше использовать **expression index**:**
```sql
CREATE INDEX idx_users_email_lower ON users(lower(email));
SELECT * FROM users WHERE lower(email) = 'user@example.com';
```

**Получение данных в отсортированном порядке:**
- Индексы B-дерева могут также использоваться для получения данных в отсортированном порядке.
- **Это не всегда быстрее, чем простое сканирование и сортировка, но часто бывает полезно, особенно когда требуется `LIMIT`:**
```sql
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- Быстрый поиск последних заказов
SELECT * FROM orders ORDER BY created_at DESC LIMIT 10;
```

### Hash индекс — подробности

Хеш-индексы хранят 32-битный хеш-код, полученный из значения индексируемого столбца. Следовательно, такие индексы могут обрабатывать только простые сравнения равенства.

**Основные характеристики `Hash`:**
- Обрабатывает только оператор равенства `=`.
- Не поддерживает диапазоны, сортировку или другие операторы сравнения.
- Может быть быстрее **B-Tree** для точных совпадений, но менее универсален.

**Создание `Hash` индекса:**
```sql
-- Синтаксис создания Hash-индекса (только равенство)
CREATE INDEX name ON table USING HASH (column);
```

**Пример использования:**
```sql
CREATE TABLE sessions (
    id UUID PRIMARY KEY,
    user_id INTEGER,
    expires_at TIMESTAMPTZ
);

-- Hash индекс для быстрого поиска по UUID
CREATE INDEX idx_sessions_id_hash ON sessions USING HASH (id);

-- Запрос использует Hash индекс
SELECT * FROM sessions WHERE id = '123e4567-e89b-12d3-a456-426614174000';
```

**Когда использовать `Hash`:**
- Очень точные поиски по равенству.
- Когда диапазоны и сортировка не нужны.
- Для часто используемых столбцов с высокой селективностью.

**Ограничения `Hash`:**
- Не поддерживает неравенства (`<`, `>`, `<=`, `>=`).
- Не поддерживает сортировку.
- Не поддерживает `BETWEEN`, `IN` (для больших списков).
- Требует **WAL**-логирования (PostgreSQL 10+), что может замедлить вставки.

### GiST индекс — подробности

Индексы **GiST** — это не единственный вид индекса, а скорее инфраструктура, в которой можно реализовать множество различных стратегий индексирования. Соответственно, конкретные операторы, с которыми может использоваться индекс **GiST**, различаются в зависимости от стратегии индексирования (класса операторов).

**Стандартные классы операторов `GiST` в `PostgreSQL`:**
Стандартный дистрибутив **PostgreSQL** включает классы операторов **GiST** для нескольких двумерных геометрических типов данных, которые поддерживают индексированные запросы с использованием следующих операторов: `<`, `<<`, `&&`, `>>`, `&<`, `&>`, `<<|`, `|>>`, `@>`, `<@`, `~=`, `&&`

**Геометрические операторы (примеры):**
```sql
-- Точки
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    name TEXT,
    location POINT
);

CREATE INDEX idx_locations_point ON locations USING GIST (location);

-- Поиск точек в пределах прямоугольника
SELECT * FROM locations
WHERE location <@ BOX '(0,0),(100,100)';

-- Поиск пересекающихся объектов
SELECT * FROM polygons
WHERE boundary && ST_MakeEnvelope(0, 0, 100, 100);
```

**Поиск ближайшего соседа (Nearest Neighbor):**
**Индексы **GiST** также могут оптимизировать поиск «ближайшего соседа», например:**
```sql
-- Поиск 10 ближайших точек к заданной (оператор <->)
SELECT * FROM places
ORDER BY location <-> point '(101,456)'
LIMIT 10;
```
Этот запрос находит десять мест, ближайших к заданной целевой точке. Возможность сделать это снова зависит от конкретного используемого класса операторов.

**Примеры использования `GiST`:**
- Геоданные (точки, линии, полигоны, круги).
- Диапазоны (range types): `int4range`, `tsrange`, `daterange`.
- Полнотекстовый поиск (в некоторых случаях).
- Массивы (для некоторых операций).

**Пример с диапазонами:**
```sql
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    room_id INTEGER,
    period TSRANGE
);

CREATE INDEX idx_reservations_period ON reservations USING GIST (period);

-- Поиск пересекающихся резерваций
SELECT * FROM reservations
WHERE period && '[2024-01-01, 2024-01-10)'::TSRANGE;
```

### SP-GiST индекс — подробности

Индексы **SP-GiST**, как и индексы **GiST**, предлагают инфраструктуру, которая поддерживает различные виды поиска. **SP-GiST** позволяет реализовать широкий спектр различных несбалансированных дисковых структур данных, таких как квадратные деревья, **k-d** деревья и радиекс-деревья (попытки).

**Стандартные классы операторов `SP-GiST`:**
Стандартное распределение **PostgreSQL** включает операторские классы **SP-GiST** для двумерных точек, которые поддерживают индексированные запросы с использованием этих операторов: `<`, `>>`, `~=`, `<@`, `<<|`, `|>>`

**Основные характеристики `SP-GiST`:**
- Эффективен для разреженных или неравномерно распределенных данных.
- Подходит для небалансированных структур данных.
- Как и **GiST**, **SP-GiST** поддерживает поиск «ближайшего соседа».

**Примеры использования `SP-GiST`:**
- Квадродеревья для двумерных точек.
- **K-d** деревья для многомерных данных.
- Радиекс-деревья (tries) для строковых данных.

**Пример с точками:**
```sql
-- Таблица точек и SP-GiST индекс для поиска ближайшего соседа
CREATE TABLE points (
    id SERIAL PRIMARY KEY,
    location POINT
);

CREATE INDEX idx_points_location ON points USING SPGIST (location);

-- Поиск ближайших точек
SELECT * FROM points
ORDER BY location <-> point '(100,200)'
LIMIT 10;
```

### GIN индекс — подробности

Индексы **GIN** — это «инвертированные индексы», которые подходят для значений данных, содержащих несколько значений компонентов, таких как массивы. Инвертированный индекс содержит отдельную запись для каждого значения компонента и может эффективно обрабатывать запросы, проверяющие наличие определенных значений компонентов.

**Как и `GiST` и `SP-GiST`, `GIN` может поддерживать множество различных пользовательских стратегий индексирования**, а конкретные операторы, с которыми можно использовать **GIN**-индекс, отличаются в зависимости от стратегии индексирования.

**Стандартные классы операторов `GIN`:**
Стандартная дистрибуция **PostgreSQL** включает в себя класс **GIN**-оператора для массивов, который поддерживает индексированные запросы с использованием этих операторов: `<@`, `@>`, `=`, `&&`

**Операторы для массивов:**
```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name TEXT,
    tags TEXT[]
);

CREATE INDEX idx_products_tags ON products USING GIN (tags);

-- Поиск товаров с определенными тегами
SELECT * FROM products WHERE tags @> ARRAY['electronics', 'sale'];

-- Поиск товаров, у которых есть хотя бы один из тегов
SELECT * FROM products WHERE tags && ARRAY['electronics', 'books'];
```

**GIN для `JSONB`:**
```sql
-- GIN-индекс по JSONB для поиска по ключам и значениям
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    data JSONB
);

CREATE INDEX idx_events_data ON events USING GIN (data);

-- Поиск по ключам в JSONB
SELECT * FROM events WHERE data @> '{"status": "active"}'::JSONB;

-- Поиск по значению ключа
SELECT * FROM events WHERE data -> 'user_id' = '123';
```

**GIN для полнотекстового поиска:**
```sql
-- Полнотекстовый поиск: tsvector и GIN-индекс
CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    title TEXT,
    content TEXT,
    search_vector TSVECTOR
);

-- Создать tsvector (можно автоматически через триггер)
UPDATE articles SET search_vector = to_tsvector('english', title || ' ' || content);

CREATE INDEX idx_articles_search ON articles USING GIN (search_vector);

-- Полнотекстовый поиск
SELECT * FROM articles
WHERE search_vector @@ to_tsquery('english', 'postgresql & performance');
```

**Оптимизация `GIN` индексов:**
- **GIN** индексы могут быть большими, особенно для массивов и **JSONB**.
- **Используйте частичные индексы, если применимо:**
```sql
-- Только для активных записей
CREATE INDEX idx_products_active_tags ON products USING GIN (tags)
WHERE status = 'active';
```

### BRIN индекс — подробности

Индексы **BRIN** (сокращение от `Block Range` INdexes) хранят сводки о значениях, хранящихся в последовательных диапазонах физических блоков таблицы. Таким образом, они наиболее эффективны для столбцов, значения которых хорошо коррелируют с физическим порядком строк таблицы.

**Основные характеристики `BRIN`:**
- Очень компактный размер (хранит только минимум/максимум по блокам).
- Эффективен для больших таблиц с данными, упорядоченными по ключу индекса (например, временные ряды).
- Для типов данных, имеющих линейный порядок сортировки, индексированные данные соответствуют минимальному и максимальному значениям значений в столбце для каждого диапазона блоков.

**Операторы, поддерживаемые `BRIN`:**
Поддерживает индексированные запросы с использованием таких операторов: `<`, `<=`, `=`, `>=`, `>`

**Создание `BRIN` индекса:**
```sql
CREATE INDEX idx_logs_created_at ON logs USING BRIN (created_at);
```

**Пример использования:**
```sql
-- Логи с временными метками (данные упорядочены по времени)
CREATE TABLE logs (
    id BIGSERIAL,
    created_at TIMESTAMPTZ NOT NULL,
    message TEXT,
    level TEXT
);

-- BRIN индекс эффективен для временных рядов
CREATE INDEX idx_logs_created_at ON logs USING BRIN (created_at);

-- Поиск по диапазону времени
SELECT * FROM logs
WHERE created_at >= '2024-01-01' AND created_at < '2024-02-01';
```

**Когда использовать `BRIN`:**
- Очень большие таблицы (сотни миллионов или миллиарды строк).
- Данные физически упорядочены по ключу индекса (например, время вставки).
- Минимальные требования к размеру индекса.
- Частые запросы по диапазонам значений.

**Ограничения `BRIN`:**
- Менее эффективен, если данные не упорядочены физически.
- Требует периодической оптимизации таблицы (CLUSTER) для поддержания порядка.
- Может быть медленнее **B-Tree** для точных совпадений.

**Настройка `BRIN`:**
```sql
-- Указать размер страниц (по умолчанию 128)
CREATE INDEX idx_logs_created_at ON logs USING BRIN (created_at)
WITH (pages_per_range = 64);
```

## Специальные техники работы с индексами

### Expression индексы

Индексы на выражениях позволяют индексировать результаты функций и вычислений.

**Примеры expression индексов:**
```sql
-- Case-insensitive поиск
CREATE INDEX idx_users_email_lower ON users(lower(email));
SELECT * FROM users WHERE lower(email) = 'user@example.com';

-- Индексация по части строки
CREATE INDEX idx_products_sku_prefix ON products(substring(sku FROM 1 FOR 3));

-- Индексация по вычисляемому значению
CREATE INDEX idx_orders_total ON orders((qty * price));
SELECT * FROM orders WHERE qty * price > 1000;

-- Индексация по JSONB полям
CREATE INDEX idx_events_user_id ON events((data->>'user_id'));
SELECT * FROM events WHERE data->>'user_id' = '123';
```

**Важные замечания:**
- Запрос должен использовать то же выражение, что и в индексе.
- **PostgreSQL** не может использовать индекс, если выражение отличается (даже если логически эквивалентно).

### Функциональные индексы

Функциональные индексы — это разновидность **expression** индексов, использующая функции.

**Примеры:**
```sql
-- Триграммный индекс для LIKE без ведущего паттерна
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_users_name_trgm ON users USING GIN (name gin_trgm_ops);

-- Теперь работает поиск по части строки
SELECT * FROM users WHERE name LIKE '%smith%';

-- Индекс на UUID для быстрого поиска
CREATE INDEX idx_sessions_token ON sessions USING HASH (encode(token, 'hex'));
```

### Покрывающие индексы (Covering Indexes)

Покрывающие индексы включают дополнительные столбцы через `INCLUDE`, что позволяет избежать чтения таблицы.

**Пример:**
```sql
-- Индекс покрывает все нужные столбцы
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at DESC)
INCLUDE (status, total_amount);

-- Запрос использует только индекс (Index Only Scan)
SELECT user_id, created_at, status, total_amount
FROM orders
WHERE user_id = 123
ORDER BY created_at DESC
LIMIT 10;
```

**Преимущества:**
- Быстрее для запросов, которые могут получить все данные из индекса.
- Меньше нагрузка на таблицу.
- Особенно полезно для «горячих» таблиц с высокой нагрузкой на чтение.

### Частичные индексы (Partial Indexes)

Частичные индексы индексируют только подмножество строк таблицы.

**Примеры:**
```sql
-- Индекс только для активных записей
CREATE INDEX idx_orders_active ON orders(created_at)
WHERE status = 'active';

-- Индекс только для недавних записей
CREATE INDEX idx_logs_recent ON logs(level, created_at)
WHERE created_at > NOW() - INTERVAL '30 days';

-- Индекс для уникальности только не удаленных записей
CREATE INDEX idx_users_email_unique ON users(email)
WHERE deleted_at IS NULL;
```

**Преимущества:**
- Меньший размер индекса.
- Быстрее обновление индекса при **INSERT**/**UPDATE**/**DELETE**.
- Более эффективное использование для фильтрованных запросов.

**Важно:**
- **PostgreSQL** может использовать частичный индекс только если условие **WHERE** в запросе совместимо с условием индекса.
- Планировщик должен понимать, что условие индекса истинно для всех строк, попадающих в запрос.

## Мониторинг и обслуживание индексов

### Проверка использования индексов

**Статистика использования индексов:**
```sql
-- Список всех индексов с их использованием
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan AS index_scans,
    idx_tup_read AS tuples_read,
    idx_tup_fetch AS tuples_fetched,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

**Неиспользуемые индексы:**
```sql
-- Найти индексы, которые никогда не использовались
SELECT
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0
  AND schemaname <> 'pg_toast'
  AND schemaname <> 'pg_catalog'
ORDER BY pg_relation_size(indexrelid) DESC;
```

**Индексы с низким использованием:**
```sql
-- Индексы, которые используются редко, но занимают много места
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
    round((pg_relation_size(indexrelid)::numeric /
           pg_total_relation_size(schemaname||'.'||tablename) * 100), 2) AS size_pct
FROM pg_stat_user_indexes
WHERE idx_scan < 10
  AND pg_relation_size(indexrelid) > 100 * 1024 * 1024  -- > 100MB
ORDER BY pg_relation_size(indexrelid) DESC;
```

### Раздувание индексов (Index Bloat)

Индексы также могут раздуваться, как и таблицы.

**Проверка раздувания индексов:**
```sql
-- Используйте расширение pgstattuple для точной оценки
CREATE EXTENSION IF NOT EXISTS pgstattuple;

SELECT * FROM pgstatindex('idx_users_email');
```

**Пересоздание индексов:**
```sql
-- REINDEX для пересоздания индекса
REINDEX INDEX idx_users_email;

-- REINDEX для всех индексов таблицы
REINDEX TABLE users;

-- REINDEX для всей базы данных (используйте осторожно!)
REINDEX DATABASE mydb;
```

**Онлайн пересоздание индексов (PostgreSQL 12+):**
```sql
-- REINDEX CONCURRENTLY не блокирует таблицу
REINDEX INDEX CONCURRENTLY idx_users_email;
```

### Удаление неиспользуемых индексов

**Перед удалением индекса:**
1. Убедитесь, что индекс не используется (проверьте `idx_scan`).
2. Убедитесь, что индекс не нужен для ограничений (UNIQUE, `PRIMARY` KEY).
3. Проверьте, не используется ли индекс для внешних ключей.

**Удаление индекса:**
```sql
DROP INDEX IF EXISTS idx_unused_index;
```

## Практические рекомендации

### Когда создавать индексы

**Создавайте индексы для:**
- Первичных ключей (автоматически).
- Внешних ключей (для ускорения JOIN).
- Столбцов, часто используемых в **WHERE**.
- Столбцов, используемых для сортировки (ORDER BY).
- Столбцов, используемых для группировки (GROUP BY).

**Не создавайте индексы для:**
- Маленьких таблиц (< 10,`000` строк в большинстве случаев).
- Столбцов с очень низкой селективностью (например, булевые с большинством `true`).
- Столбцов, которые редко используются в запросах.
- Столбцов, которые часто изменяются, но редко используются для поиска.

### Порядок столбцов в составных индексах

**Принципы:**
- Столбцы с высокой селективностью идут первыми.
- Столбцы, используемые в **WHERE**, идут перед столбцами для **ORDER** `BY`.
- Столбцы, используемые вместе, должны быть рядом.

**Примеры:**
```sql
-- Плохой порядок
CREATE INDEX idx_orders_bad ON orders(status, user_id);
-- status имеет низкую селективность, но идет первым

-- Хороший порядок
CREATE INDEX idx_orders_good ON orders(user_id, status, created_at DESC);
-- user_id имеет высокую селективность, идет первым
-- status и created_at используются для фильтрации и сортировки
```

### Избегание дублирования индексов

**Проверка дублирующихся индексов:**
```sql
-- Найти индексы с одинаковыми столбцами
SELECT
    pg_size_pretty(SUM(pg_relation_size(idx))::BIGINT) AS size,
    (array_agg(idx))[1] AS idx1, (array_agg(idx))[2] AS idx2
FROM (
    SELECT indexrelid::regclass AS idx,
           (indrelid::text ||E'\n'|| indclass::text ||E'\n'|| indkey::text ||E'\n'||
            COALESCE(indexprs::text,'')||E'\n' || COALESCE(indpred::text,'')) AS KEY
    FROM pg_index
) sub
GROUP BY KEY
HAVING COUNT(*) > 1
ORDER BY size DESC;
```

### Мониторинг производительности индексов

**Запросы, использующие индексы медленно:**
```sql
-- Используйте pg_stat_statements для поиска медленных запросов
SELECT query, calls, total_time, mean_time, rows
FROM pg_stat_statements
WHERE query LIKE '%users%email%'
ORDER BY mean_time DESC;
```

**EXPLAIN для проверки использования индексов:**
```sql
-- Проверить, используется ли индекс
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM users WHERE email = 'user@example.com';

-- Ищите: Index Scan, Index Only Scan, Bitmap Index Scan
-- Избегайте: Seq Scan (если таблица большая)
```

### Оптимизация индексов для конкретных запросов

**Анализ запросов:**
1. Найдите медленные запросы (pg_stat_statements).
2. Выполните **EXPLAIN ANALYZE** для них.
3. Определите, какие столбцы нужны для фильтрации/сортировки.
4. Создайте индексы для этих столбцов.
5. Проверьте, что индекс используется (EXPLAIN).

**Пример оптимизации:**
```sql
-- Исходный медленный запрос
SELECT * FROM orders
WHERE user_id = 123 AND status = 'pending'
ORDER BY created_at DESC
LIMIT 10;

-- Анализ показывает Seq Scan
EXPLAIN ANALYZE ...;

-- Создаем составной индекс
CREATE INDEX idx_orders_user_status_date ON orders(user_id, status, created_at DESC);

-- Повторный EXPLAIN показывает Index Scan
EXPLAIN ANALYZE ...;
```

## Лучшие практики

- **Выбор типа: B-Tree** для большинства **OLTP**; **GIN** для **JSONB**/полнотекста; **BRIN** для больших таблиц, упорядоченных по времени/`ID`.
- **Составные индексы:** порядок столбцов важен (равенство диапазон); учитывайте селективность.
- **Частичные индексы:** используйте **WHERE** для подмножества строк (например, только активные заказы).
- **EXPLAIN (ANALYZE):** всегда проверяйте использование индекса после создания; избегайте лишних индексов на часто обновляемых таблицах.
- **Обслуживание: REINDEX** при деградации; мониторинг **bloat**; **VACUUM** после массовых изменений.

## См. также

- [[postgres-admin|PostgreSQL: администрирование и обслуживание]]
- [[postgres-backup-restore|PostgreSQL: Резервное копирование и восстановление]]
- [[postgres-basics|PostgreSQL: Полное руководство по основам и мониторингу]]
- [[postgres-data-ops|PostgreSQL: операции с данными (CRUD)]]
- [[postgres-design|PostgreSQL: проектирование и нормализация]]
