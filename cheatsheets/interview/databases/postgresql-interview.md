---
title: "Вопросы на собеседовании: PostgreSQL"
description: "Подробные ответы по PostgreSQL: архитектура, типы данных, индексы, EXPLAIN, транзакции, MVCC, VACUUM, партиционирование, оконные функции, CTE, репликация, full-text search, JSONB, PgBouncer, блокировки, производительность, расширения."
tags:
  - interview
  - databases
  - postgresql-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "PostgreSQL"
  - "PostgreSQL interview"
  - "PostgreSQL собеседование"
prerequisites: []
next: []
updated: "2026-05-07"
---
# Вопросы на собеседовании: `PostgreSQL`

Подробные ответы по `PostgreSQL`: архитектура, типы данных, индексы, `EXPLAIN`, транзакции, `MVCC`, `VACUUM`, партиционирование, оконные функции, `CTE`, репликация, `full-text search`, `JSONB`, `PgBouncer`, блокировки, производительность, расширения.

Краткое введение: **PostgreSQL** — объектно-реляционная СУБД с открытым кодом, известная надёжностью, расширяемостью и полным соответствием стандарту `SQL`. Отличается богатой системой типов (`JSONB`, массивы, `UUID`, `enum`, пользовательские типы), мощным планировщиком запросов, `MVCC`-изоляцией и развитой инфраструктурой для репликации и горизонтального масштабирования. На собеседованиях проверяют понимание внутреннего устройства (процессы, `WAL`, `shared buffers`), умение читать планы запросов (`EXPLAIN ANALYZE`), знание индексов, уровней изоляции и типичных приёмов оптимизации.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Documentation](https://www.postgresql.org/docs/current/) — официальная документация
- [PostgreSQL Indexes](https://www.postgresql.org/docs/current/indexes.html) — типы индексов
- [EXPLAIN Documentation](https://www.postgresql.org/docs/current/sql-explain.html) — команда EXPLAIN
- [MVCC Documentation](https://www.postgresql.org/docs/current/mvcc.html) — изоляция транзакций и MVCC
- [PgBouncer Documentation](https://www.pgbouncer.org/usage.html) — пул соединений PgBouncer

### Статьи Baeldung

- [PostgreSQL with Spring Boot](https://www.baeldung.com/spring-boot-postgresql) — интеграция PostgreSQL со Spring Boot
- [Storing PostgreSQL JSONB Using Spring Boot and JPA](https://www.baeldung.com/spring-boot-jpa-storing-postgresql-jsonb) — хранение JSONB через JPA
- [Querying JSONB Columns Using Spring Data JPA](https://www.baeldung.com/spring-data-jpa-querying-jsonb-columns) — запросы по JSONB
- [Differences Between JSON and JSONB Data Types in PostgreSQL](https://www.baeldung.com/sql/postgresql-json-vs-jsonb-data-types) — JSON vs JSONB
- [Mapping PostgreSQL Array With Hibernate](https://www.baeldung.com/java-hibernate-map-postgresql-array) — маппинг массивов PostgreSQL

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Архитектура PostgreSQL**
- [Q1. (!) Какова архитектура процессов PostgreSQL?](#q1--какова-архитектура-процессов-postgresql)
- [Q2. Что такое `shared_buffers` и как он влияет на производительность?](#q2-что-такое-shared_buffers-и-как-он-влияет-на-производительность)
- [Q3. (!) Что такое `WAL` и зачем он нужен?](#q3--что-такое-wal-и-зачем-он-нужен)
- [Q4. Как работает `checkpoint` в PostgreSQL?](#q4-как-работает-checkpoint-в-postgresql)
- [Q5. Что такое `pg_hba.conf` и для чего он используется?](#q5-что-такое-pg_hbaconf-и-для-чего-он-используется)

**Типы данных**
- [Q6. (!) Какие специальные типы данных поддерживает PostgreSQL?](#q6--какие-специальные-типы-данных-поддерживает-postgresql)
- [Q7. В чём разница между `JSON` и `JSONB`?](#q7-в-чём-разница-между-json-и-jsonb)
- [Q8. Как работают массивы (`arrays`) в PostgreSQL?](#q8-как-работают-массивы-arrays-в-postgresql)
- [Q9. Что такое `hstore` и когда его использовать?](#q9-что-такое-hstore-и-когда-его-использовать)
- [Q10. Как работают пользовательские типы (`enum`, `composite`, `domain`)?](#q10-как-работают-пользовательские-типы-enum-composite-domain)

**Индексы**
- [Q11. (!) Какие типы индексов существуют в PostgreSQL?](#q11--какие-типы-индексов-существуют-в-postgresql)
- [Q12. (!) Как работает `B-tree` индекс и когда он применяется?](#q12--как-работает-b-tree-индекс-и-когда-он-применяется)
- [Q13. Что такое `GIN` индекс и для каких данных он нужен?](#q13-что-такое-gin-индекс-и-для-каких-данных-он-нужен)
- [Q14. Что такое `GiST` индекс и чем он отличается от `GIN`?](#q14-что-такое-gist-индекс-и-чем-он-отличается-от-gin)
- [Q15. Что такое `BRIN` индекс и когда его выгодно использовать?](#q15-что-такое-brin-индекс-и-когда-его-выгодно-использовать)
- [Q16. Что такое частичный (`partial`) индекс?](#q16-что-такое-частичный-partial-индекс)
- [Q17. Что такое функциональный (`expression`) индекс?](#q17-что-такое-функциональный-expression-индекс)
- [Q18. (!) Что такое покрывающий (`covering`) индекс и как его создать в PostgreSQL?](#q18--что-такое-покрывающий-covering-индекс-и-как-его-создать-в-postgresql)
- [Q19. Что такое `CONCURRENTLY` при создании индекса?](#q19-что-такое-concurrently-при-создании-индекса)

**EXPLAIN и планировщик запросов**
- [Q20. (!) Как читать вывод `EXPLAIN`?](#q20--как-читать-вывод-explain)
- [Q21. (!) В чём разница между `EXPLAIN` и `EXPLAIN ANALYZE`?](#q21--в-чём-разница-между-explain-и-explain-analyze)
- [Q22. (!) Что такое `Seq Scan` и `Index Scan` и когда планировщик выбирает каждый?](#q22--что-такое-seq-scan-и-index-scan-и-когда-планировщик-выбирает-каждый)
- [Q23. Что такое `Bitmap Index Scan` и `Index Only Scan`?](#q23-что-такое-bitmap-index-scan-и-index-only-scan)
- [Q24. Что такое `cost` в плане запроса?](#q24-что-такое-cost-в-плане-запроса)
- [Q25. Как работает статистика в PostgreSQL (`pg_statistics`)?](#q25-как-работает-статистика-в-postgresql-pg_statistics)

**Транзакции и MVCC**
- [Q26. (!) Какие уровни изоляции транзакций поддерживает PostgreSQL?](#q26--какие-уровни-изоляции-транзакций-поддерживает-postgresql)
- [Q27. (!) Как работает `MVCC` в PostgreSQL?](#q27--как-работает-mvcc-в-postgresql)
- [Q28. Что такое `xmin` и `xmax` в кортежах PostgreSQL?](#q28-что-такое-xmin-и-xmax-в-кортежах-postgresql)
- [Q29. Что такое `phantom read`, `non-repeatable read` и `dirty read`?](#q29-что-такое-phantom-read-non-repeatable-read-и-dirty-read)
- [Q30. Как работает `SAVEPOINT`?](#q30-как-работает-savepoint)

**VACUUM и AUTOVACUUM**
- [Q31. (!) Зачем нужен `VACUUM` и что такое `dead tuples`?](#q31--зачем-нужен-vacuum-и-что-такое-dead-tuples)
- [Q32. В чём разница между `VACUUM` и `VACUUM FULL`?](#q32-в-чём-разница-между-vacuum-и-vacuum-full)
- [Q33. Как работает `AUTOVACUUM` и как его настроить?](#q33-как-работает-autovacuum-и-как-его-настроить)
- [Q34. Что такое `table bloat` и как его измерить?](#q34-что-такое-table-bloat-и-как-его-измерить)

**Партиционирование**
- [Q35. (!) Какие виды партиционирования поддерживает PostgreSQL?](#q35--какие-виды-партиционирования-поддерживает-postgresql)
- [Q36. Как работает `partition pruning`?](#q36-как-работает-partition-pruning)
- [Q37. Когда партиционирование не даёт выигрыша?](#q37-когда-партиционирование-не-даёт-выигрыша)

**Оконные функции**
- [Q38. (!) Что такое оконные функции и как они работают?](#q38--что-такое-оконные-функции-и-как-они-работают)
- [Q39. Как работают `ROW_NUMBER`, `RANK` и `DENSE_RANK`?](#q39-как-работают-row_number-rank-и-dense_rank)
- [Q40. Как работают `LAG` и `LEAD`?](#q40-как-работают-lag-и-lead)
- [Q41. Как использовать `SUM OVER` и скользящие агрегаты?](#q41-как-использовать-sum-over-и-скользящие-агрегаты)

**CTE и рекурсия**
- [Q42. (!) Что такое `CTE` и чем он отличается от подзапроса?](#q42--что-такое-cte-и-чем-он-отличается-от-подзапроса)
- [Q43. (!) Как работают рекурсивные `CTE`?](#q43--как-работают-рекурсивные-cte)

**Репликация**
- [Q44. (!) Что такое `streaming replication` в PostgreSQL?](#q44--что-такое-streaming-replication-в-postgresql)
- [Q45. В чём разница между физической и логической репликацией?](#q45-в-чём-разница-между-физической-и-логической-репликацией)
- [Q46. Что такое `replication slot` и зачем он нужен?](#q46-что-такое-replication-slot-и-зачем-он-нужен)

**Full-text search**
- [Q47. Как работает `full-text search` в PostgreSQL?](#q47-как-работает-full-text-search-в-postgresql)

**JSONB операторы и индексирование**
- [Q48. (!) Какие операторы и функции доступны для `JSONB`?](#q48--какие-операторы-и-функции-доступны-для-jsonb)

**Connection pooling**
- [Q49. (!) Что такое `PgBouncer` и зачем он нужен?](#q49--что-такое-pgbouncer-и-зачем-он-нужен)
- [Q50. Какие режимы пулинга поддерживает `PgBouncer`?](#q50-какие-режимы-пулинга-поддерживает-pgbouncer)

**Блокировки и deadlock**
- [Q51. (!) Какие типы блокировок существуют в PostgreSQL?](#q51--какие-типы-блокировок-существуют-в-postgresql)
- [Q52. Что такое `deadlock` и как PostgreSQL его обнаруживает?](#q52-что-такое-deadlock-и-как-postgresql-его-обнаруживает)

**Производительность и конфигурация**
- [Q53. Какие ключевые параметры конфигурации влияют на производительность?](#q53-какие-ключевые-параметры-конфигурации-влияют-на-производительность)
- [Q54. Как анализировать медленные запросы?](#q54-как-анализировать-медленные-запросы)

**Расширения**
- [Q55. Какие популярные расширения PostgreSQL стоит знать?](#q55-какие-популярные-расширения-postgresql-стоит-знать)

---

## Q1. (!) Какова архитектура процессов PostgreSQL?

PostgreSQL использует **мультипроцессную архитектуру**: каждое клиентское соединение обслуживается отдельным серверным процессом (`backend process`). Главные компоненты:

| Процесс | Назначение |
|---------|-----------|
| `postmaster` | Главный процесс, принимает входящие соединения, порождает дочерние процессы |
| `backend` | Один на соединение, выполняет запросы клиента |
| `checkpointer` | Записывает грязные страницы из `shared_buffers` на диск при `checkpoint` |
| `background writer` | Упреждающая запись грязных страниц, снижает нагрузку на `checkpoint` |
| `WAL writer` | Сбрасывает буферы `WAL` на диск |
| `autovacuum launcher` | Запускает рабочие процессы `autovacuum` |
| `stats collector` | Собирает статистику использования таблиц и индексов |
| `wal sender/receiver` | Репликация: отправка/приём WAL на реплику |

**Shared memory** содержит: `shared_buffers` (кэш страниц данных), `WAL buffers`, `lock table`, кэш планов и другое.

```sql
-- Посмотреть активные серверные процессы
SELECT pid, usename, application_name, state, query
FROM pg_stat_activity
WHERE state <> 'idle';
```

---

## Q2. Что такое `shared_buffers` и как он влияет на производительность?

**`shared_buffers`** — общий кэш страниц данных в памяти, разделяемый между всеми серверными процессами. Прежде чем читать данные с диска, PostgreSQL проверяет, есть ли нужная страница в `shared_buffers`.

**Рекомендации по настройке:**
- Типичное значение: **25% RAM** для выделенного сервера (но не более 8–16 GB, так как ОС-кэш (`page cache`) тоже важен).
- Увеличение `shared_buffers` снижает число операций дискового ввода-вывода.
- Совместно работает с `effective_cache_size` (подсказка планировщику о размере ОС-кэша).

```sql
-- Посмотреть текущие значения
SHOW shared_buffers;
SHOW effective_cache_size;

-- Статистика попаданий в кэш (должна быть > 99%)
SELECT sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) AS cache_hit_ratio
FROM pg_statio_user_tables;
```

---

## Q3. (!) Что такое `WAL` и зачем он нужен?

**`WAL` (Write-Ahead Log)** — журнал предзаписи: все изменения данных сначала записываются в `WAL`-файлы на диск, и только потом применяются к страницам данных.

**Зачем:**
- **Надёжность (durability):** при сбое базу можно восстановить из `WAL`, проиграв журнал с последнего `checkpoint`.
- **Производительность:** последовательная запись в журнал быстрее случайных операций записи страниц данных.
- **Репликация:** `WAL`-сегменты отправляются на реплики (`streaming replication`).
- **Point-in-Time Recovery (PITR):** архивированные `WAL`-файлы позволяют восстановить базу на произвольный момент времени.

**Ключевые параметры:**
| Параметр | Описание |
|---------|---------|
| `wal_level` | Уровень детализации: `minimal`, `replica`, `logical` |
| `synchronous_commit` | Синхронная/асинхронная запись: `on`, `off`, `local`, `remote_write`, `remote_apply` |
| `wal_compression` | Сжатие WAL-сегментов |
| `archive_mode` | Включить архивирование WAL |

```sql
SHOW wal_level;
SHOW synchronous_commit;
```

---

## Q4. Как работает `checkpoint` в PostgreSQL?

**`Checkpoint`** — точка согласованности: PostgreSQL сбрасывает все «грязные» страницы (`dirty pages`) из `shared_buffers` на диск и записывает отметку в `WAL`. При восстановлении после сбоя достаточно воспроизвести `WAL` только начиная с последнего `checkpoint`.

**Параметры:**
- `checkpoint_timeout` (по умолчанию `5min`) — максимальный интервал между `checkpoint`.
- `max_wal_size` — размер накопленного `WAL`, при превышении которого принудительно инициируется `checkpoint`.
- `checkpoint_completion_target` (по умолчанию `0.9`) — доля периода `checkpoint_timeout`, в течение которой допускается размазывать запись, чтобы снизить пиковую нагрузку на ввод-вывод.

---

## Q5. Что такое `pg_hba.conf` и для чего он используется?

**`pg_hba.conf`** (`host-based authentication`) — файл конфигурации аутентификации клиентов. Каждая строка описывает правило: тип соединения, база данных, пользователь, адрес клиента и метод аутентификации.

```
# TYPE  DATABASE  USER    ADDRESS         METHOD
host    mydb      myuser  192.168.1.0/24  md5
local   all       all                     peer
host    all       all     0.0.0.0/0       scram-sha-256
```

**Методы аутентификации:** `trust`, `peer`, `md5`, `scram-sha-256`, `ldap`, `cert`, `radius`.

---

## Q6. (!) Какие специальные типы данных поддерживает PostgreSQL?

PostgreSQL предоставляет богатую систему типов, выходящую далеко за рамки стандартного `SQL`:

| Тип | Описание |
|-----|---------|
| `JSONB` | Бинарный `JSON` с индексированием |
| `JSON` | Текстовый `JSON`, сохраняет порядок ключей |
| `UUID` | 128-битный универсальный идентификатор |
| `ARRAY` | Массив любого типа (`int[]`, `text[]`, ...) |
| `HSTORE` | Хранилище пар ключ-значение |
| `ENUM` | Перечислимый тип |
| `INET`, `CIDR` | IP-адреса и подсети |
| `TSRANGE`, `DATERANGE` | Диапазоны дат/временных меток |
| `TSVECTOR` | Full-text search вектор |
| `BYTEA` | Бинарные данные |
| `XML` | XML-данные |
| `POINT`, `LINE`, `POLYGON` | Геометрические типы |
| `MONEY` | Денежная сумма |

---

## Q7. В чём разница между `JSON` и `JSONB`?

| Характеристика | `JSON` | `JSONB` |
|---------------|--------|---------|
| Хранение | Текст (оригинальная строка) | Бинарный формат |
| Порядок ключей | Сохраняется | Не гарантируется |
| Дубли ключей | Сохраняются | Последнее значение |
| Индексирование | Нет (только функциональный) | `GIN`-индекс |
| Производительность записи | Быстрее | Медленнее (парсинг при вставке) |
| Производительность чтения | Медленнее (парсинг при выборке) | Быстрее |

**Вывод:** для большинства задач следует использовать `JSONB` — он поддерживает индексирование и эффективный поиск.

```sql
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    payload JSONB
);

-- GIN-индекс для JSONB
CREATE INDEX idx_events_payload ON events USING GIN (payload);
```

---

## Q8. Как работают массивы (`arrays`) в PostgreSQL?

Массивы позволяют хранить несколько значений одного типа в одном поле.

```sql
-- Создание таблицы с массивом
CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    tags TEXT[]
);

-- Вставка
INSERT INTO articles (tags) VALUES ('{"postgresql","database","sql"}');
INSERT INTO articles (tags) VALUES (ARRAY['performance', 'indexing']);

-- Запрос: содержит ли массив элемент
SELECT * FROM articles WHERE 'postgresql' = ANY(tags);

-- Запрос: все элементы массива присутствуют
SELECT * FROM articles WHERE tags @> ARRAY['postgresql', 'sql'];

-- unnest: развернуть массив в строки
SELECT id, unnest(tags) AS tag FROM articles;
```

Для ускорения поиска по массиву используется `GIN`-индекс:

```sql
CREATE INDEX idx_articles_tags ON articles USING GIN (tags);
```

---

## Q9. Что такое `hstore` и когда его использовать?

**`hstore`** — расширение PostgreSQL для хранения пар `ключ => значение` в одном поле (оба — строки). Подходит для разреженных атрибутов объектов с непредсказуемым набором свойств.

```sql
CREATE EXTENSION IF NOT EXISTS hstore;

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name TEXT,
    attributes HSTORE
);

INSERT INTO products (name, attributes)
VALUES ('Shirt', 'color => "red", size => "L", material => "cotton"');

-- Получить значение по ключу
SELECT attributes -> 'color' FROM products;

-- Фильтр по ключу-значению
SELECT * FROM products WHERE attributes @> 'color => "red"';
```

**Когда использовать:** если набор атрибутов непредсказуем и не требует вложенных структур. Для сложных структур предпочтителен `JSONB`.

---

## Q10. Как работают пользовательские типы (`enum`, `composite`, `domain`)?

**`ENUM`** — перечислимый тип, значения фиксированы при создании:

```sql
CREATE TYPE mood AS ENUM ('happy', 'sad', 'neutral');

CREATE TABLE persons (
    name TEXT,
    current_mood mood
);
```

**Composite type** — составной тип (аналог структуры):

```sql
CREATE TYPE address AS (
    street TEXT,
    city TEXT,
    zip_code VARCHAR(10)
);

CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    addr address
);

SELECT (addr).city FROM customers;
```

**`DOMAIN`** — тип на основе существующего с дополнительными ограничениями:

```sql
CREATE DOMAIN positive_int AS INTEGER
    CHECK (VALUE > 0);

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    quantity positive_int
);
```

---

## Q11. (!) Какие типы индексов существуют в PostgreSQL?

| Тип | Структура | Применение |
|-----|-----------|-----------|
| `B-tree` | Сбалансированное дерево | По умолчанию. Равенство, диапазон, `IS NULL`, сортировка |
| `Hash` | Хэш-таблица | Только проверка равенства (`=`) |
| `GIN` | Inverted index | `JSONB`, массивы, `full-text search`, `hstore` |
| `GiST` | Обобщённое дерево поиска | Геометрические типы, `full-text`, диапазоны, `pg_trgm` |
| `SP-GiST` | Space-partitioned GiST | IP-адреса, телефонные номера, точки |
| `BRIN` | Block Range Index | Монотонные данные (временные метки, последовательности) |

---

## Q12. (!) Как работает `B-tree` индекс и когда он применяется?

**`B-tree`** (Balanced Tree) — индекс по умолчанию в PostgreSQL. Хранит данные в отсортированном дереве, поддерживает эффективный поиск по значению или диапазону.

**Применяется при:**
- Операторах `=`, `<`, `<=`, `>`, `>=`, `BETWEEN`
- `ORDER BY` (может устранить сортировку)
- `IS NULL` / `IS NOT NULL`
- Операциях `LIKE 'prefix%'` (только с префиксом, не `'%suffix'`)

```sql
-- Обычный B-tree
CREATE INDEX idx_orders_created_at ON orders (created_at);

-- Составной B-tree
CREATE INDEX idx_orders_user_status ON orders (user_id, status);

-- Descending
CREATE INDEX idx_orders_created_desc ON orders (created_at DESC);
```

**Составной индекс:** оптимизирует запросы с фильтром по первому (или первым нескольким) столбцам. Порядок столбцов важен — наиболее селективный или часто фильтруемый ставят первым.

---

## Q13. Что такое `GIN` индекс и для каких данных он нужен?

**`GIN` (Generalized Inverted Index)** — инвертированный индекс: для каждого элемента хранится список строк, его содержащих. Оптимален для типов, где одно поле содержит несколько ключей.

**Применяется для:**
- `JSONB` (операторы `@>`, `?`, `?|`, `?&`)
- Массивов (`@>`, `<@`, `&&`)
- `full-text search` (`@@`)
- `hstore`
- `pg_trgm` (поиск по подстроке)

```sql
-- GIN для JSONB
CREATE INDEX idx_events_payload ON events USING GIN (payload);

-- GIN для full-text search
CREATE INDEX idx_articles_tsv ON articles USING GIN (to_tsvector('russian', body));

-- GIN для pg_trgm (поиск LIKE '%term%')
CREATE EXTENSION pg_trgm;
CREATE INDEX idx_products_name_trgm ON products USING GIN (name gin_trgm_ops);
```

**Недостатки:** `GIN` медленнее обновляется по сравнению с `B-tree`. Для ускорения обновлений используется `fastupdate`.

---

## Q14. Что такое `GiST` индекс и чем он отличается от `GIN`?

**`GiST` (Generalized Search Tree)** — обобщённое дерево поиска, расширяемое: логика индексирования выносится в классы операторов.

| Характеристика | `GIN` | `GiST` |
|---------------|-------|--------|
| Оптимален для | Точный поиск элементов | Приближённые запросы, диапазоны |
| Размер | Обычно больше | Меньше |
| Обновление | Медленнее | Быстрее |
| Ложные срабатывания | Нет | Возможны (требует перепроверки) |

**Применяется для:** геометрических данных (`PostGIS`), диапазонов (`tsrange`), `full-text search`, `pg_trgm`.

```sql
-- GiST для диапазонов дат
CREATE INDEX idx_reservations_period ON reservations USING GiST (period);

-- GiST для PostGIS
CREATE INDEX idx_locations_geom ON locations USING GiST (geom);
```

---

## Q15. Что такое `BRIN` индекс и когда его выгодно использовать?

**`BRIN` (Block Range Index)** — индекс на основе диапазонов блоков. Хранит мин/макс значения для каждого диапазона физических блоков таблицы (по умолчанию 128 блоков на диапазон).

**Применяется, когда:**
- Данные физически упорядочены по индексируемому столбцу (монотонные `id`, временные метки вставки).
- Таблица очень большая, а `B-tree` индекс занял бы неприемлемо много места.

```sql
CREATE INDEX idx_logs_created_brin ON logs USING BRIN (created_at);
```

**Ограничения:** подходит только для коррелированных данных (высокая корреляция значений с физическим порядком строк). Для неупорядоченных данных эффективность стремится к нулю.

---

## Q16. Что такое частичный (`partial`) индекс?

**Частичный индекс** строится только по подмножеству строк, удовлетворяющих условию `WHERE`. Меньший размер и быстрое обновление, так как не все строки индексируются.

```sql
-- Индексировать только незакрытые заказы
CREATE INDEX idx_orders_open ON orders (user_id)
WHERE status = 'open';

-- Индексировать только ненулевые значения
CREATE INDEX idx_users_email ON users (email)
WHERE email IS NOT NULL;
```

**Когда использовать:** для запросов с фиксированным фильтром (`WHERE status = 'active'`), когда нужна выборка из небольшого подмножества строк.

---

## Q17. Что такое функциональный (`expression`) индекс?

**Функциональный индекс** строится по результату выражения или функции, применённой к столбцу.

```sql
-- Поиск без учёта регистра
CREATE INDEX idx_users_lower_email ON users (LOWER(email));

-- Запрос, использующий этот индекс
SELECT * FROM users WHERE LOWER(email) = 'user@example.com';

-- Индекс по extracted части JSONB
CREATE INDEX idx_events_type ON events ((payload->>'event_type'));

-- Индекс по части даты
CREATE INDEX idx_orders_year ON orders (EXTRACT(YEAR FROM created_at));
```

---

## Q18. (!) Что такое покрывающий (`covering`) индекс и как его создать в PostgreSQL?

**Покрывающий индекс** содержит все столбцы, необходимые для выполнения запроса, — планировщик может использовать `Index Only Scan` без обращения к основной таблице.

В PostgreSQL (с версии 11) покрывающий индекс создаётся с помощью `INCLUDE`:

```sql
-- Основной индекс по user_id, дополнительно хранит status и total
CREATE INDEX idx_orders_covering ON orders (user_id) INCLUDE (status, total);

-- Запрос использует Index Only Scan
SELECT status, total FROM orders WHERE user_id = 42;
```

**До PostgreSQL 11** покрывающий эффект достигался составным индексом (все нужные столбцы включались в ключ).

**Преимущества:** устраняет обращение к основной таблице (`heap fetch`), снижает ввод-вывод. **Недостатки:** увеличивает размер индекса.

---

## Q19. Что такое `CONCURRENTLY` при создании индекса?

При обычном `CREATE INDEX` таблица блокируется на запись на время построения индекса. `CONCURRENTLY` позволяет строить индекс без блокировки записи:

```sql
CREATE INDEX CONCURRENTLY idx_orders_user_id ON orders (user_id);
```

**Особенности:**
- Занимает больше времени (два прохода по таблице).
- Нельзя использовать внутри транзакции.
- Если прервать, оставит «неправильный» (`invalid`) индекс — его нужно удалить вручную.
- Аналог при удалении: `DROP INDEX CONCURRENTLY`.

---

## Q20. (!) Как читать вывод `EXPLAIN`?

`EXPLAIN` показывает **план выполнения** запроса. Вывод — дерево узлов, каждый из которых описывает операцию. Читается **снизу вверх** (листья выполняются первыми).

```sql
EXPLAIN SELECT * FROM orders WHERE user_id = 42;
```

```
Index Scan using idx_orders_user_id on orders  (cost=0.43..8.45 rows=5 width=128)
  Index Cond: (user_id = 42)
```

**Основные поля:**
- `cost=X..Y` — стоимость запуска узла (`X`) и общая стоимость (`Y`) в условных единицах.
- `rows=N` — оценочное число строк.
- `width=N` — средний размер строки в байтах.
- `actual time=X..Y` (только `EXPLAIN ANALYZE`) — реальное время.
- `actual rows=N loops=N` — реальное число строк и число запусков узла.

**Типичные узлы:**
| Узел | Описание |
|------|---------|
| `Seq Scan` | Полное сканирование таблицы |
| `Index Scan` | Сканирование по индексу с возвратом в таблицу |
| `Index Only Scan` | Сканирование только по индексу (покрывающий) |
| `Bitmap Index Scan` | Построение битовой карты по индексу |
| `Bitmap Heap Scan` | Чтение строк по битовой карте |
| `Hash Join` | Соединение хэш-таблицей |
| `Nested Loop` | Вложенные циклы |
| `Merge Join` | Соединение слиянием |
| `Sort` | Сортировка |
| `Aggregate` | Агрегация |

---

## Q21. (!) В чём разница между `EXPLAIN` и `EXPLAIN ANALYZE`?

| Команда | Описание |
|---------|---------|
| `EXPLAIN` | Показывает **оценочный** план без выполнения запроса |
| `EXPLAIN ANALYZE` | **Выполняет** запрос и показывает реальное время и число строк |
| `EXPLAIN (ANALYZE, BUFFERS)` | Добавляет информацию о попаданиях в кэш (`shared hit`, `read`) |
| `EXPLAIN (ANALYZE, FORMAT JSON)` | Вывод в формате `JSON` |

```sql
-- Полный анализ с информацией о буферах
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT o.id, u.name
FROM orders o
JOIN users u ON o.user_id = u.id
WHERE o.status = 'open';
```

**Важно:** `EXPLAIN ANALYZE` реально выполняет запрос, включая `INSERT`/`UPDATE`/`DELETE`. Для деструктивных запросов оборачивать в `BEGIN`/`ROLLBACK`:

```sql
BEGIN;
EXPLAIN ANALYZE DELETE FROM old_logs WHERE created_at < NOW() - INTERVAL '1 year';
ROLLBACK;
```

---

## Q22. (!) Что такое `Seq Scan` и `Index Scan` и когда планировщик выбирает каждый?

**`Seq Scan` (Sequential Scan):**
- Читает все страницы таблицы последовательно.
- Выбирается, когда нужно прочитать **большую долю строк** (нет смысла прыгать по индексу).
- Выбирается, если **индекса нет** для данного условия.
- Иногда выгоднее `Index Scan` для маленьких таблиц (накладные расходы на индекс не окупаются).

**`Index Scan`:**
- Сначала находит значения в индексе, затем обращается к основной таблице за строкой.
- Выбирается при **высокой селективности** условия (фильтрует большую часть строк).
- Стоимость зависит от числа `heap fetch` (случайных обращений к таблице).

**`Index Only Scan`:**
- Если покрывающий индекс содержит все нужные столбцы, обращение к таблице не нужно.

**Планировщик переключается на `Seq Scan`, если:**
- Условие фильтрует < 5–10% строк (зависит от `random_page_cost` / `seq_page_cost`).
- Статистика устарела.
- Принудительно: `SET enable_indexscan = off;`

---

## Q23. Что такое `Bitmap Index Scan` и `Index Only Scan`?

**`Bitmap Index Scan`:**

1. Сканирует индекс и строит **битовую карту** (bitmap) страниц, содержащих подходящие строки.
2. `Bitmap Heap Scan` читает эти страницы в порядке физического расположения, снижая случайный ввод-вывод.

Применяется при умеренной селективности (больше строк, чем при `Index Scan`, но меньше, чем при `Seq Scan`). Позволяет объединять несколько индексов (`BitmapAnd`, `BitmapOr`).

```
Bitmap Heap Scan on orders
  Recheck Cond: (status = 'open')
  ->  Bitmap Index Scan on idx_orders_status
        Index Cond: (status = 'open')
```

**`Index Only Scan`:** читает данные **только из индекса**, не обращаясь к таблице. Возможен только при использовании покрывающего индекса. Требует, чтобы visibility map показывала, что страницы таблицы не содержат «видимых» мёртвых кортежей — иначе PostgreSQL вынужден проверить таблицу.

---

## Q24. Что такое `cost` в плане запроса?

**`cost`** — условная оценка стоимости выполнения, выраженная в единицах, соответствующих стоимости чтения одной дисковой страницы последовательно.

**Параметры, влияющие на стоимость:**
| Параметр | По умолчанию | Описание |
|---------|------------|---------|
| `seq_page_cost` | `1.0` | Стоимость последовательного чтения страницы |
| `random_page_cost` | `4.0` | Стоимость случайного чтения страницы |
| `cpu_tuple_cost` | `0.01` | Стоимость обработки одной строки |
| `cpu_index_tuple_cost` | `0.005` | Стоимость обработки записи индекса |
| `cpu_operator_cost` | `0.0025` | Стоимость вычисления оператора |

Для SSD рекомендуется снизить `random_page_cost` до `1.1–2.0`, так как случайное чтение на SSD дешевле.

```sql
SET random_page_cost = 1.1;
```

---

## Q25. Как работает статистика в PostgreSQL (`pg_statistics`)?

Планировщик PostgreSQL использует **статистику** о распределении данных для оценки количества строк. Статистика обновляется командой `ANALYZE` (или автоматически `autovacuum`).

```sql
-- Ручной запуск анализа
ANALYZE orders;

-- Посмотреть статистику столбца
SELECT * FROM pg_stats WHERE tablename = 'orders' AND attname = 'status';
```

**Ключевые поля `pg_stats`:**
- `n_distinct` — число уникальных значений
- `most_common_vals` — наиболее частые значения
- `most_common_freqs` — их частоты
- `histogram_bounds` — границы гистограммы равночастотного распределения

Параметр `default_statistics_target` (по умолчанию `100`) определяет детализацию. Для столбцов с высокой кардинальностью можно увеличить:

```sql
ALTER TABLE orders ALTER COLUMN status SET STATISTICS 500;
```

---

## Q26. (!) Какие уровни изоляции транзакций поддерживает PostgreSQL?

PostgreSQL поддерживает 4 стандартных уровня изоляции `SQL`:

| Уровень | Dirty Read | Non-Repeatable Read | Phantom Read |
|---------|-----------|--------------------|-|
| `READ UNCOMMITTED` | Невозможен¹ | Возможен | Возможен |
| `READ COMMITTED` (по умолч.) | Невозможен | Возможен | Возможен |
| `REPEATABLE READ` | Невозможен | Невозможен | Невозможен² |
| `SERIALIZABLE` | Невозможен | Невозможен | Невозможен |

¹ PostgreSQL не реализует `dirty read` даже на `READ UNCOMMITTED`.  
² За счёт `MVCC` `REPEATABLE READ` в PostgreSQL также предотвращает `phantom read`.

```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- ... операции ...
COMMIT;

-- Или через SET
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

**`SERIALIZABLE`** использует `SSI` (Serializable Snapshot Isolation) — обнаруживает конфликты сериализации и откатывает транзакцию с `SQLSTATE 40001`.

---

## Q27. (!) Как работает `MVCC` в PostgreSQL?

**`MVCC` (Multi-Version Concurrency Control)** — механизм изоляции транзакций: вместо блокировок читателей и писателей PostgreSQL хранит несколько версий одной и той же строки.

**Принцип:**
- При `UPDATE` старая версия строки не удаляется сразу — помечается как «удалённая» в поле `xmax`.
- Создаётся новая версия строки с новым `xmin`.
- Каждая транзакция видит **снимок** (snapshot) данных: набор версий строк, зафиксированных до начала транзакции.

**Преимущества:**
- Читатели не блокируют писателей, писатели не блокируют читателей.
- Высокий параллелизм.

**Недостатки:**
- Накапливаются мёртвые кортежи (`dead tuples`), которые нужно периодически убирать (`VACUUM`).
- Индексы также накапливают мёртвые записи.

```sql
-- Посмотреть xmin/xmax
SELECT xmin, xmax, * FROM orders WHERE id = 1;
```

---

## Q28. Что такое `xmin` и `xmax` в кортежах PostgreSQL?

Каждый кортеж (строка) содержит системные поля:

| Поле | Описание |
|------|---------|
| `xmin` | `XID` транзакции, создавшей кортеж (`INSERT` или `UPDATE`) |
| `xmax` | `XID` транзакции, удалившей или заменившей кортеж (`DELETE` или `UPDATE`) |
| `ctid` | Физический адрес кортежа (`блок`, `смещение`) |

**Видимость кортежа для транзакции T:**
- `xmin` должен быть зафиксирован и входить в снимок.
- `xmax` не должен быть зафиксирован или должен быть больше XID снимка (кортеж не удалён в видимом диапазоне).

```sql
SELECT xmin, xmax, ctid, id, status FROM orders WHERE id = 1;
```

---

## Q29. Что такое `phantom read`, `non-repeatable read` и `dirty read`?

| Аномалия | Описание |
|----------|---------|
| **Dirty read** | Транзакция читает незафиксированные изменения другой транзакции |
| **Non-repeatable read** | Повторный `SELECT` возвращает другой результат из-за зафиксированного `UPDATE`/`DELETE` |
| **Phantom read** | Повторный `SELECT` возвращает новые строки из-за зафиксированного `INSERT` |
| **Serialization anomaly** | Результат параллельных транзакций невозможен ни при каком последовательном порядке их выполнения |

В PostgreSQL благодаря `MVCC` даже `READ COMMITTED` исключает `dirty read`. `REPEATABLE READ` исключает `non-repeatable` и `phantom read`. `SERIALIZABLE` исключает все аномалии.

---

## Q30. Как работает `SAVEPOINT`?

**`SAVEPOINT`** позволяет создать точку внутри транзакции, к которой можно откатиться без отмены всей транзакции.

```sql
BEGIN;

INSERT INTO orders (user_id, total) VALUES (1, 100);

SAVEPOINT before_discount;

UPDATE orders SET total = total * 0.9 WHERE user_id = 1;

-- Отменить только операцию после SAVEPOINT
ROLLBACK TO SAVEPOINT before_discount;

-- Первый INSERT сохранён, UPDATE отменён
COMMIT;
```

Удалить `SAVEPOINT`: `RELEASE SAVEPOINT before_discount;`

---

## Q31. (!) Зачем нужен `VACUUM` и что такое `dead tuples`?

**`Dead tuples`** — устаревшие версии строк, оставшиеся после `UPDATE`/`DELETE` из-за `MVCC`. Физически строки не удаляются сразу — они остаются в таблице до тех пор, пока ни одна активная транзакция их не видит.

**Проблемы от накопления `dead tuples`:**
- Увеличение размера таблицы (`table bloat`).
- Замедление `Seq Scan` (больше страниц для чтения).
- Замедление `Index Scan` (больше записей в индексах).

**`VACUUM`** помечает пространство `dead tuples` как доступное для повторного использования (но **не возвращает** его ОС — лишь делает доступным для новых строк внутри таблицы).

```sql
-- Ручной запуск
VACUUM orders;

-- Посмотреть статистику dead tuples
SELECT relname, n_dead_tup, last_vacuum, last_autovacuum
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;
```

---

## Q32. В чём разница между `VACUUM` и `VACUUM FULL`?

| Команда | Поведение |
|---------|---------|
| `VACUUM` | Помечает пространство мёртвых кортежей как свободное внутри таблицы. Не блокирует читателей/писателей |
| `VACUUM FULL` | Переписывает всю таблицу в новый файл, **реально возвращая** место ОС. Устанавливает эксклюзивную блокировку на таблицу |
| `VACUUM ANALYZE` | Выполняет `VACUUM` + обновляет статистику (`ANALYZE`) |

```sql
VACUUM ANALYZE orders;
VACUUM FULL orders;  -- осторожно: блокирует таблицу!
```

Альтернатива `VACUUM FULL` без длительной блокировки — расширение `pg_repack`.

---

## Q33. Как работает `AUTOVACUUM` и как его настроить?

**`AUTOVACUUM`** — фоновый процесс, автоматически запускающий `VACUUM` и `ANALYZE` на таблицах, накопивших достаточно `dead tuples`.

**Ключевые параметры:**

```sql
-- Порог запуска VACUUM = autovacuum_vacuum_threshold + autovacuum_vacuum_scale_factor * n_live_tup
autovacuum_vacuum_threshold = 50       -- минимум dead tuples
autovacuum_vacuum_scale_factor = 0.2   -- 20% от числа живых строк

-- Для больших таблиц лучше снижать scale_factor
ALTER TABLE orders SET (
    autovacuum_vacuum_scale_factor = 0.01,
    autovacuum_vacuum_threshold = 1000
);
```

**Мониторинг:**
```sql
SELECT relname, last_autovacuum, last_autoanalyze, n_dead_tup,
       autovacuum_count, autoanalyze_count
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;
```

---

## Q34. Что такое `table bloat` и как его измерить?

**`Table bloat`** — разница между реальным физическим размером таблицы/индекса и объёмом, фактически занятым живыми данными. Возникает из-за накопления `dead tuples` и незаполненных страниц.

```sql
-- Размер таблицы и индексов
SELECT
    relname,
    pg_size_pretty(pg_total_relation_size(relid)) AS total_size,
    pg_size_pretty(pg_relation_size(relid)) AS table_size,
    pg_size_pretty(pg_indexes_size(relid)) AS indexes_size
FROM pg_catalog.pg_statio_user_tables
ORDER BY pg_total_relation_size(relid) DESC;
```

Для детального анализа `bloat` используют расширение `pgstattuple` или запросы из набора `check_postgres`.

---

## Q35. (!) Какие виды партиционирования поддерживает PostgreSQL?

PostgreSQL (с версии 10+) поддерживает **декларативное партиционирование**:

| Тип | Описание | Пример использования |
|-----|---------|---------------------|
| `RANGE` | По диапазону значений | Партиционирование по дате (`created_at`) |
| `LIST` | По списку значений | По стране, статусу, категории |
| `HASH` | По хэшу значения | Равномерное распределение по модулю |

```sql
-- RANGE партиционирование по дате
CREATE TABLE orders (
    id BIGSERIAL,
    user_id INT,
    total NUMERIC,
    created_at TIMESTAMP NOT NULL
) PARTITION BY RANGE (created_at);

CREATE TABLE orders_2025 PARTITION OF orders
    FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE orders_2026 PARTITION OF orders
    FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

-- LIST партиционирование
CREATE TABLE orders_by_region (
    id BIGSERIAL,
    region TEXT NOT NULL,
    total NUMERIC
) PARTITION BY LIST (region);

CREATE TABLE orders_ru PARTITION OF orders_by_region FOR VALUES IN ('RU');
CREATE TABLE orders_kz PARTITION OF orders_by_region FOR VALUES IN ('KZ');
```

**Подпартиционирование** (вложенное) также поддерживается: `RANGE + HASH`, `RANGE + LIST` и т. д.

---

## Q36. Как работает `partition pruning`?

**`Partition pruning`** — оптимизация планировщика: при наличии условия на ключ партиционирования PostgreSQL исключает нерелевантные партиции из плана запроса.

```sql
-- Планировщик прочитает только orders_2026
SELECT * FROM orders
WHERE created_at >= '2026-01-01' AND created_at < '2027-01-01';
```

**Динамический `pruning`** (PostgreSQL 11+): исключает партиции даже при параметризованных запросах или на этапе выполнения.

Проверить в плане: если партиция не упоминается в `EXPLAIN`, она была отсечена.

---

## Q37. Когда партиционирование не даёт выигрыша?

- Запрос не фильтрует по ключу партиционирования — все партиции сканируются.
- Таблица небольшая — накладные расходы на `partition routing` перевешивают выгоду.
- Большое количество партиций (>1000) может замедлить планирование.
- `JOIN` между партиционированными и обычными таблицами без фильтра по ключу.
- Глобальные индексы не поддерживаются (только локальные — на каждой партиции).

---

## Q38. (!) Что такое оконные функции и как они работают?

**Оконные функции** выполняют вычисления по **набору строк, связанных с текущей строкой** (окну), не сворачивая результат в одну строку, как `GROUP BY`.

```sql
SELECT
    user_id,
    order_date,
    total,
    SUM(total) OVER (PARTITION BY user_id ORDER BY order_date) AS running_total,
    AVG(total) OVER (PARTITION BY user_id) AS avg_per_user,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY order_date DESC) AS rn
FROM orders;
```

**Синтаксис:**
```sql
function_name(...) OVER (
    [PARTITION BY col1, col2]
    [ORDER BY col3 [ASC|DESC]]
    [ROWS|RANGE BETWEEN ... AND ...]
)
```

**Виды функций:**
- **Ранжирование:** `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()`, `NTILE(n)`
- **Смещение:** `LAG()`, `LEAD()`, `FIRST_VALUE()`, `LAST_VALUE()`, `NTH_VALUE()`
- **Агрегатные:** `SUM()`, `AVG()`, `COUNT()`, `MIN()`, `MAX()`

---

## Q39. Как работают `ROW_NUMBER`, `RANK` и `DENSE_RANK`?

| Функция | Описание | Поведение при дублях |
|---------|---------|---------------------|
| `ROW_NUMBER()` | Уникальный порядковый номер | Всегда уникален (произвольный порядок дублей) |
| `RANK()` | Ранг с пропуском при дублях | 1, 2, 2, 4 |
| `DENSE_RANK()` | Ранг без пропуска | 1, 2, 2, 3 |

```sql
SELECT
    name,
    score,
    ROW_NUMBER() OVER (ORDER BY score DESC) AS row_num,
    RANK()       OVER (ORDER BY score DESC) AS rnk,
    DENSE_RANK() OVER (ORDER BY score DESC) AS dense_rnk
FROM leaderboard;
```

**Распространённый паттерн:** топ-N записей на группу:

```sql
SELECT * FROM (
    SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
    FROM orders
) t
WHERE rn = 1; -- последний заказ каждого пользователя
```

---

## Q40. Как работают `LAG` и `LEAD`?

- **`LAG(col, offset, default)`** — значение столбца из строки **выше** в окне на `offset` строк.
- **`LEAD(col, offset, default)`** — значение столбца из строки **ниже** в окне на `offset` строк.

```sql
SELECT
    order_date,
    total,
    LAG(total, 1, 0)  OVER (PARTITION BY user_id ORDER BY order_date) AS prev_total,
    LEAD(total, 1, 0) OVER (PARTITION BY user_id ORDER BY order_date) AS next_total,
    total - LAG(total) OVER (PARTITION BY user_id ORDER BY order_date) AS diff_from_prev
FROM orders;
```

Полезны для расчёта дельт, разниц между соседними строками, определения «предыдущего статуса».

---

## Q41. Как использовать `SUM OVER` и скользящие агрегаты?

```sql
-- Нарастающий итог (running total)
SELECT
    order_date,
    total,
    SUM(total) OVER (ORDER BY order_date) AS running_total
FROM orders;

-- Скользящее среднее за 7 дней
SELECT
    order_date,
    total,
    AVG(total) OVER (
        ORDER BY order_date
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_avg_7d
FROM daily_revenue;

-- Итог в рамках группы (без ORDER BY — агрегат по всей группе)
SELECT
    user_id,
    total,
    SUM(total) OVER (PARTITION BY user_id) AS user_total
FROM orders;
```

**Фреймы:**
- `ROWS BETWEEN N PRECEDING AND CURRENT ROW` — фиксированное число строк.
- `RANGE BETWEEN INTERVAL '7 days' PRECEDING AND CURRENT ROW` — диапазон значений.

---

## Q42. (!) Что такое `CTE` и чем он отличается от подзапроса?

**`CTE` (Common Table Expression)** — именованный временный результат, объявляемый в `WITH`-блоке и используемый в основном запросе.

```sql
WITH active_users AS (
    SELECT id, name FROM users WHERE status = 'active'
),
recent_orders AS (
    SELECT user_id, SUM(total) AS total_spent
    FROM orders
    WHERE created_at > NOW() - INTERVAL '30 days'
    GROUP BY user_id
)
SELECT u.name, ro.total_spent
FROM active_users u
JOIN recent_orders ro ON u.id = ro.user_id
ORDER BY ro.total_spent DESC;
```

**`CTE` vs подзапрос:**
| Характеристика | `CTE` | Подзапрос |
|---------------|-------|-----------|
| Читаемость | Высокая, можно именовать | Может быть сложным при вложении |
| Повторное использование | Да (одно определение — несколько ссылок) | Нет |
| Оптимизация (PostgreSQL < 12) | `CTE` — «забор оптимизации» (materializes) | Встраивается в план |
| Оптимизация (PostgreSQL 12+) | По умолчанию встраивается, если не рекурсивный и без побочных эффектов | Встраивается |
| `MATERIALIZED` / `NOT MATERIALIZED` | Явное управление материализацией | Нет |

```sql
-- Принудительная материализация (PostgreSQL 12+)
WITH stats AS MATERIALIZED (
    SELECT AVG(total) AS avg_total FROM orders
)
SELECT * FROM orders, stats WHERE total > stats.avg_total;
```

---

## Q43. (!) Как работают рекурсивные `CTE`?

**Рекурсивный `CTE`** позволяет выполнять иерархические или графовые запросы (обход дерева, поиск путей).

```sql
-- Обход иерархии сотрудников
WITH RECURSIVE emp_hierarchy AS (
    -- Базовый случай: корневые узлы
    SELECT id, name, manager_id, 1 AS depth
    FROM employees
    WHERE manager_id IS NULL

    UNION ALL

    -- Рекурсивный шаг
    SELECT e.id, e.name, e.manager_id, eh.depth + 1
    FROM employees e
    JOIN emp_hierarchy eh ON e.manager_id = eh.id
)
SELECT id, name, depth FROM emp_hierarchy ORDER BY depth, name;
```

**Структура:**
1. **Базовый запрос** — начальные строки (без рекурсии).
2. **`UNION ALL`** — объединение с рекурсивной частью.
3. **Рекурсивный запрос** — ссылается на имя `CTE`.

Итерации продолжаются, пока рекурсивный запрос не вернёт пустой результат. Для предотвращения бесконечных циклов можно ограничить глубину:

```sql
WHERE eh.depth < 10
```

---

## Q44. (!) Что такое `streaming replication` в PostgreSQL?

**`Streaming replication`** — механизм физической репликации: первичный сервер (`primary`) передаёт `WAL`-записи на реплики (`standby`) в режиме реального времени через TCP-соединение.

**Режимы синхронизации:**
| Режим `synchronous_commit` | Описание |
|--------------------------|---------|
| `off` | Возврат клиенту до записи `WAL` на диск (риск потери данных) |
| `local` | Запись на диск `primary`, без ожидания реплики |
| `remote_write` | Ожидание записи `WAL` на диск реплики (без `fsync`) |
| `remote_apply` | Ожидание применения изменений на реплике |
| `on` | Синхронный: ожидание `fsync` на реплике |

**Настройка на `primary`:**

```sql
-- postgresql.conf
wal_level = replica
max_wal_senders = 5
```

**Настройка на `standby`:**

```
# recovery.conf / postgresql.conf (PG 12+)
primary_conninfo = 'host=primary-host port=5432 user=replication_user'
```

Реплика может использоваться для **read-only** запросов (`hot standby`).

---

## Q45. В чём разница между физической и логической репликацией?

| Характеристика | Физическая репликация | Логическая репликация |
|---------------|----------------------|----------------------|
| Единица | Блоки данных (`WAL`) | Строки (`INSERT`/`UPDATE`/`DELETE`) |
| Версия PostgreSQL | Должна совпадать | Может отличаться |
| Платформа | Должна совпадать | Может отличаться |
| Избирательность | Вся база данных | Выбранные таблицы |
| Применение | DR, read-реплики | Миграции, разнородные БД, CDC |

**Логическая репликация** (PostgreSQL 10+): работает через **Publication** (на primary) и **Subscription** (на replica).

```sql
-- На primary
CREATE PUBLICATION my_pub FOR TABLE orders, users;

-- На standby
CREATE SUBSCRIPTION my_sub
CONNECTION 'host=primary dbname=mydb user=repuser'
PUBLICATION my_pub;
```

---

## Q46. Что такое `replication slot` и зачем он нужен?

**`Replication slot`** — механизм, гарантирующий, что `primary` не удалит `WAL`-сегменты, пока реплика (или клиент логической репликации) их не получила.

```sql
-- Посмотреть слоты
SELECT * FROM pg_replication_slots;

-- Создать физический слот
SELECT pg_create_physical_replication_slot('standby1_slot');

-- Создать логический слот
SELECT pg_create_logical_replication_slot('my_slot', 'pgoutput');
```

**Риск:** если реплика надолго отстанет или отключится, `WAL`-сегменты будут накапливаться, что может привести к исчерпанию дискового пространства. Для мониторинга:

```sql
SELECT slot_name, pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), restart_lsn)) AS lag
FROM pg_replication_slots;
```

---

## Q47. Как работает `full-text search` в PostgreSQL?

PostgreSQL поддерживает встроенный полнотекстовый поиск через типы `TSVECTOR` и `TSQUERY`.

```sql
-- TSVECTOR: нормализованный вектор слов
SELECT to_tsvector('russian', 'Быстрая коричневая лиса прыгает');

-- TSQUERY: запрос
SELECT to_tsquery('russian', 'лиса & прыгает');

-- Поиск
SELECT title FROM articles
WHERE to_tsvector('russian', body) @@ to_tsquery('russian', 'postgresql & индексы');

-- Ранжирование результатов
SELECT title, ts_rank(to_tsvector('russian', body), query) AS rank
FROM articles, to_tsquery('russian', 'postgresql') query
WHERE to_tsvector('russian', body) @@ query
ORDER BY rank DESC;
```

**Оптимизация:** хранить `TSVECTOR` в отдельном столбце и индексировать `GIN`:

```sql
ALTER TABLE articles ADD COLUMN tsv TSVECTOR
    GENERATED ALWAYS AS (to_tsvector('russian', title || ' ' || body)) STORED;

CREATE INDEX idx_articles_tsv ON articles USING GIN (tsv);
```

---

## Q48. (!) Какие операторы и функции доступны для `JSONB`?

**Операторы доступа:**
| Оператор | Описание | Пример |
|----------|---------|--------|
| `->` | Получить поле как `JSON` | `payload->'name'` |
| `->>` | Получить поле как `TEXT` | `payload->>'name'` |
| `#>` | Путь как `JSON` | `payload#>'{address,city}'` |
| `#>>` | Путь как `TEXT` | `payload#>>'{address,city}'` |

**Операторы поиска:**
| Оператор | Описание |
|----------|---------|
| `@>` | Содержит (правый в левом) |
| `<@` | Содержится (левый в правом) |
| `?` | Ключ существует |
| `?\|` | Хотя бы один ключ из массива существует |
| `?&` | Все ключи из массива существуют |

```sql
-- Поиск по вложенному полю
SELECT * FROM events WHERE payload @> '{"event_type": "purchase"}';

-- Проверка существования ключа
SELECT * FROM events WHERE payload ? 'discount_code';

-- Модификация JSONB
UPDATE events
SET payload = payload || '{"processed": true}'
WHERE id = 1;

-- Удаление ключа
UPDATE events SET payload = payload - 'temp_field' WHERE id = 1;

-- jsonb_set: обновить вложенное поле
UPDATE events
SET payload = jsonb_set(payload, '{user,name}', '"Alice"')
WHERE id = 1;
```

---

## Q49. (!) Что такое `PgBouncer` и зачем он нужен?

**`PgBouncer`** — легковесный пул соединений для PostgreSQL. PostgreSQL создаёт отдельный процесс на каждое соединение (~5–10 MB памяти), поэтому тысячи одновременных соединений приводят к деградации производительности.

**Проблема без пула:**
- Каждое соединение — отдельный `backend` процесс.
- Затраты на установку соединения: ~1–5 мс.
- `fork()` при каждом новом соединении.

**Решение с `PgBouncer`:**
- Приложения подключаются к `PgBouncer`, а не напрямую к PostgreSQL.
- `PgBouncer` поддерживает пул из N реальных соединений с `PostgreSQL`.
- Тысячи клиентских соединений → десятки реальных соединений.

```ini
# pgbouncer.ini
[databases]
mydb = host=localhost port=5432 dbname=mydb

[pgbouncer]
pool_mode = transaction
max_client_conn = 1000
default_pool_size = 20
```

---

## Q50. Какие режимы пулинга поддерживает `PgBouncer`?

| Режим | Описание | Ограничения |
|-------|---------|------------|
| `session` | Соединение держится на время сессии клиента | Минимальная экономия |
| `transaction` | Соединение возвращается в пул после каждой транзакции | Нельзя использовать `SET`, `LISTEN`, `PREPARE` между транзакциями |
| `statement` | Соединение возвращается после каждого оператора | Нельзя использовать многооператорные транзакции |

**Рекомендация:** `transaction` — оптимальный баланс между экономией соединений и совместимостью. `session` — если приложение активно использует `SESSION`-уровня настройки.

---

## Q51. (!) Какие типы блокировок существуют в PostgreSQL?

PostgreSQL использует многоуровневую систему блокировок:

**Блокировки таблиц (`table-level locks`):**

| Блокировка | Команды | Конфликтует с |
|-----------|---------|--------------|
| `ACCESS SHARE` | `SELECT` | `ACCESS EXCLUSIVE` |
| `ROW SHARE` | `SELECT FOR UPDATE/SHARE` | `EXCLUSIVE`, `ACCESS EXCLUSIVE` |
| `ROW EXCLUSIVE` | `INSERT`, `UPDATE`, `DELETE` | `SHARE`, `SHARE ROW EXCLUSIVE`, `EXCLUSIVE`, `ACCESS EXCLUSIVE` |
| `SHARE UPDATE EXCLUSIVE` | `VACUUM`, `ANALYZE`, `CREATE INDEX CONCURRENTLY` | — |
| `SHARE` | `CREATE INDEX` | `ROW EXCLUSIVE` и выше |
| `SHARE ROW EXCLUSIVE` | — | `ROW EXCLUSIVE` и выше |
| `EXCLUSIVE` | — | Всё кроме `ACCESS SHARE` |
| `ACCESS EXCLUSIVE` | `DROP TABLE`, `TRUNCATE`, `ALTER TABLE` | Всё |

**Блокировки строк (`row-level locks`):**
- `FOR UPDATE` — эксклюзивная блокировка строки.
- `FOR NO KEY UPDATE` — не блокирует `SELECT FOR KEY SHARE`.
- `FOR SHARE` — разделяемая блокировка.
- `FOR KEY SHARE` — минимальная блокировка (для внешних ключей).

```sql
-- Посмотреть активные блокировки
SELECT pid, locktype, relation::regclass, mode, granted
FROM pg_locks
WHERE NOT granted;
```

---

## Q52. Что такое `deadlock` и как PostgreSQL его обнаруживает?

**`Deadlock`** — ситуация, когда две (или более) транзакции ожидают блокировки, удерживаемые друг другом, и ни одна не может продолжить выполнение.

```
Транзакция A: держит блокировку строки 1, ждёт строку 2
Транзакция B: держит блокировку строки 2, ждёт строку 1
```

PostgreSQL автоматически обнаруживает `deadlock` через периодический анализ графа ожидания (раз в `deadlock_timeout`, по умолчанию `1s`). При обнаружении одна из транзакций откатывается с ошибкой `ERROR: deadlock detected`.

**Предотвращение:**
- Всегда обновлять/блокировать строки в одном порядке.
- Использовать `SELECT ... FOR UPDATE` явно и последовательно.
- Минимизировать время жизни транзакций.
- Использовать `NOWAIT` или `SKIP LOCKED` для неблокирующих операций.

```sql
-- Попытка захватить блокировку без ожидания
SELECT * FROM orders WHERE id = 1 FOR UPDATE NOWAIT;

-- Пропустить заблокированные строки
SELECT * FROM jobs WHERE status = 'pending' FOR UPDATE SKIP LOCKED LIMIT 1;
```

---

## Q53. Какие ключевые параметры конфигурации влияют на производительность?

| Параметр | Рекомендация | Описание |
|---------|-------------|---------|
| `shared_buffers` | 25% RAM | Кэш страниц данных |
| `effective_cache_size` | 75% RAM | Подсказка планировщику об ОС-кэше |
| `work_mem` | 4–64 MB | Память для сортировки/хэш-операций на запрос |
| `maintenance_work_mem` | 256–1024 MB | Память для `VACUUM`, `CREATE INDEX` |
| `max_connections` | 100–200 | Максимум соединений (лучше с `PgBouncer`) |
| `wal_buffers` | 16–64 MB | Буфер `WAL` в памяти |
| `random_page_cost` | 1.1–2.0 для SSD | Относительная стоимость случайного чтения |
| `effective_io_concurrency` | 200 для SSD | Параллелизм ввода-вывода |
| `checkpoint_completion_target` | 0.9 | Сглаживание нагрузки `checkpoint` |
| `autovacuum_vacuum_cost_delay` | 2ms | Задержка `autovacuum` для снижения нагрузки |
| `log_min_duration_statement` | 1000 (мс) | Логировать медленные запросы |

---

## Q54. Как анализировать медленные запросы?

**1. Включить логирование медленных запросов:**

```sql
-- postgresql.conf
log_min_duration_statement = 1000  -- запросы дольше 1 секунды
log_line_prefix = '%t [%p] %d %u '
```

**2. Использовать `pg_stat_statements`:**

```sql
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Топ-10 запросов по суммарному времени
SELECT query, calls, total_exec_time, mean_exec_time, rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;

-- Сбросить статистику
SELECT pg_stat_statements_reset();
```

**3. Анализировать конкретный запрос:**

```sql
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) SELECT ...;
```

**4. Проверить `pg_stat_activity`:**

```sql
SELECT pid, query, state, wait_event_type, wait_event, now() - query_start AS duration
FROM pg_stat_activity
WHERE state <> 'idle'
ORDER BY duration DESC;
```

---

## Q55. Какие популярные расширения PostgreSQL стоит знать?

| Расширение | Назначение |
|-----------|-----------|
| `pg_stat_statements` | Статистика выполнения SQL-запросов |
| `pg_trgm` | Поиск по подстроке (`LIKE '%term%'`), `GIN`/`GiST` |
| `PostGIS` | Геопространственные данные и функции |
| `uuid-ossp` / `gen_random_uuid()` | Генерация `UUID` (встроена с PG 13) |
| `hstore` | Хранилище пар ключ-значение |
| `pgcrypto` | Функции шифрования и хэширования |
| `tablefunc` | `crosstab` — сводные таблицы |
| `pg_partman` | Автоматическое управление партиционированием |
| `timescaledb` | Time-series данные на основе PostgreSQL |
| `pg_repack` | Перепаковка таблиц без длительной блокировки (альтернатива `VACUUM FULL`) |
| `pgaudit` | Аудит SQL-операций |
| `pg_bouncer` | Пул соединений (отдельный процесс, не расширение) |

```sql
-- Установить расширение
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Посмотреть установленные расширения
SELECT name, default_version, installed_version, comment
FROM pg_available_extensions
WHERE installed_version IS NOT NULL;
```

---

## See also

- [SQL](sql-interview.md)
- [Database Architecture](database-architecture-interview.md)
- [Транзакции и уровни изоляции](database-transactions-interview.md)
- [Hibernate](hibernate-interview.md)
- [Apache Cassandra](cassandra-interview.md)
- [Redis](redis-interview.md)
- [MongoDB](mongodb-interview.md)
- [Elasticsearch](elasticsearch-interview.md)
- [Flyway и Liquibase](flyway-liquibase-interview.md)
- [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md)
- [ClickHouse](clickhouse-interview.md)
- [CockroachDB](cockroachdb-interview.md)
- [DynamoDB](dynamodb-interview.md)
