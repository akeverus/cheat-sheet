---
title: "SQL: Основы"
description: "Практическое руководство по SQL: DDL/DML/DQL, схема и нормализация, joins, CTE и оконные функции, UPSERT, NULL handling, пагинация, транзакции, индексы, performance и troubleshooting в production."
tags:
  - databases
  - sql
  - sql-basics
  - normalization
  - cte
  - upsert
difficulty: "intermediate"
prerequisites: []
next:
  - "sql-transactions-isolation.md"
  - "../relational/postgresql/postgres-indexes.md"
related:
  - "../relational/postgresql/postgres-queries.md"
updated: "2026-04-27"
---
# SQL: Основы

Практическое руководство по `SQL`: базовый синтаксис, рабочие паттерны запросов и эксплуатационные правила для production-систем.

## Полезные ссылки

### Официальная документация

- [SQL Standard ISO 9075](https://www.iso.org/standard/76583.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

### См. также

- [PostgreSQL Queries](../relational/postgresql/postgres-queries.md) — практики запросов в PostgreSQL
- [MySQL Queries](../relational/mysql/mysql-queries.md) — практики запросов в MySQL
- [PostgreSQL Indexes](../relational/postgresql/postgres-indexes.md) — проектирование индексов

- [Hibernate: JPQL, HQL и Criteria API](../orm/hibernate-jpql-criteria.md)
## Содержание

- [Что важно понимать до первого SELECT](#что-важно-понимать-до-первого-select)
- [Категории SQL-команд](#категории-sql-команд)
- [Схема и нормализация](#схема-и-нормализация)
  - [Нормальные формы](#нормальные-формы)
  - [Когда денормализовать](#когда-денормализовать)
- [Базовые запросы, которые нужны каждый день](#базовые-запросы-которые-нужны-каждый-день)
- [JOIN, GROUP BY и оконные функции](#join-group-by-и-оконные-функции)
- [Subquery и CTE](#subquery-и-cte)
- [UPSERT (INSERT ... ON CONFLICT)](#upsert-insert--on-conflict)
- [NULL и тернарная логика](#null-и-тернарная-логика)
- [Пагинация](#пагинация)
- [Транзакции и изоляция](#транзакции-и-изоляция)
- [Индексы и план выполнения](#индексы-и-план-выполнения)
- [SQL в Java-приложении](#sql-в-java-приложении)
- [Operational context: SQL в production](#operational-context-sql-в-production)
- [Антипаттерны](#антипаттерны)
- [Troubleshooting](#troubleshooting)

## Что важно понимать до первого SELECT

`SQL` — декларативный язык: вы описываете **что** нужно получить, а СУБД решает **как** выполнять запрос.

На практике это значит:
- одинаковый по смыслу SQL может выполняться с разной скоростью,
- структура данных и индексы критически важны,
- без анализа плана (`EXPLAIN`) оптимизация почти всегда угадывание.

## Категории SQL-команд

- `DDL`: структура (`CREATE`, `ALTER`, `DROP`)
- `DML`: изменение данных (`INSERT`, `UPDATE`, `DELETE`)
- `DQL`: выборка (`SELECT`)
- `TCL`: транзакции (`BEGIN`, `COMMIT`, `ROLLBACK`)
- `DCL`: права (`GRANT`, `REVOKE`)

Пример `DDL`:

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Схема и нормализация

Схема — фундамент. Хорошая схема ускоряет запросы, защищает от
data inconsistency, упрощает миграции. Плохая — заставляет переписывать
код через год.

### Нормальные формы

**1NF (атомарность).** Каждая колонка хранит одно значение, без массивов
или JSON в колонках, которые регулярно фильтруются.

```sql
-- Плохо: строка с разделителями
CREATE TABLE products (id BIGINT PRIMARY KEY, tags TEXT); -- "java,spring,boot"

-- Хорошо: связь many-to-many
CREATE TABLE product_tags (
    product_id BIGINT REFERENCES products(id),
    tag_id     BIGINT REFERENCES tags(id),
    PRIMARY KEY (product_id, tag_id)
);
```

**2NF (нет частичных зависимостей).** Все non-key атрибуты зависят от
ВСЕГО первичного ключа, не от его части.

```sql
-- Плохо: order_items со составным ключом, но product_name зависит только от product_id
CREATE TABLE order_items (
    order_id      BIGINT,
    product_id    BIGINT,
    product_name  VARCHAR(255),     -- ← дублируется, ломает 2NF
    quantity      INT,
    PRIMARY KEY (order_id, product_id)
);

-- Хорошо: product_name живёт в products
CREATE TABLE order_items (
    order_id   BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES products(id),
    quantity   INT,
    PRIMARY KEY (order_id, product_id)
);
```

**3NF (нет транзитивных зависимостей).** Non-key атрибуты не зависят
от других non-key атрибутов.

```sql
-- Плохо: city_country зависит от city, не от employee_id
CREATE TABLE employees (
    id           BIGINT PRIMARY KEY,
    city         VARCHAR(100),
    city_country VARCHAR(100)  -- ← транзитивная зависимость
);

-- Хорошо: вынести city → country связь в отдельную таблицу
CREATE TABLE cities (
    name    VARCHAR(100) PRIMARY KEY,
    country VARCHAR(100) NOT NULL
);
```

Большинство OLTP-схем целят в 3NF. BCNF / 4NF / 5NF — теоретически
интересны, на практике редко нужны.

### Когда денормализовать

- **Read-heavy с известными запросами.** Если 90% трафика — один и тот
  же агрегат, держи его в материализованном виде.
- **OLAP / отчёты.** Star/Snowflake schemas намеренно денормализованы
  для скорости агрегаций.
- **Latency-critical paths.** Один JOIN не страшен, но 5 JOINs на p99
  мобильного запроса — плохая идея.

Правило: **денормализуй после измерений**, не «на всякий случай».
Каждое дублирование данных требует синхронизации (триггеры, события,
batch-процессы) — это технический долг.

## Базовые запросы, которые нужны каждый день

```sql
-- Выборка с фильтром и сортировкой
SELECT id, email, created_at
FROM users
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY created_at DESC
LIMIT 100;

-- Безопасное обновление по ключу
UPDATE users
SET email = 'new@example.com'
WHERE id = 42;
```

Практический принцип: любое `UPDATE`/`DELETE` сначала проверяйте через `SELECT` с тем же `WHERE`.

## JOIN, GROUP BY и оконные функции

`JOIN`:

```sql
SELECT u.id, u.email, COUNT(o.id) AS orders_count
FROM users u
LEFT JOIN orders o ON o.user_id = u.id
GROUP BY u.id, u.email;
```

Оконные функции (часто нужны для аналитики и отчетов):

```sql
SELECT
    user_id,
    amount,
    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
FROM payments;
```

Полезные оконные функции:

| Функция | Назначение |
|---|---|
| `ROW_NUMBER()` | Уникальный номер строки в партиции (для дедупликации) |
| `RANK()` / `DENSE_RANK()` | Ранжирование с пропусками / без |
| `LAG()` / `LEAD()` | Значение из предыдущей / следующей строки |
| `SUM() OVER (...)` | Накопительная сумма / running total |
| `AVG() OVER (... ROWS BETWEEN N PRECEDING ...)` | Скользящее среднее |
| `FIRST_VALUE` / `LAST_VALUE` | Граничные значения партиции |

Пример running total:

```sql
SELECT
    transaction_date,
    amount,
    SUM(amount) OVER (ORDER BY transaction_date) AS running_total
FROM transactions;
```

## Subquery и CTE

**Subquery** — запрос внутри запроса. Бывают:

- **Скалярные** (одно значение): `WHERE salary > (SELECT AVG(salary) FROM employees)`
- **Multi-row** (`IN`, `EXISTS`): `WHERE id IN (SELECT user_id FROM ...)`
- **Correlated** (зависит от внешнего запроса):
  `WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)`

`EXISTS` обычно эффективнее `IN` на больших выборках —
оптимизатор останавливается на первой найденной строке.

**CTE (Common Table Expression)** — именованный временный результат
через `WITH`. Делает сложный запрос читаемым.

```sql
WITH active_users AS (
    SELECT id, email
    FROM users
    WHERE last_login > NOW() - INTERVAL '30 days'
),
user_orders AS (
    SELECT u.id, u.email, COUNT(o.id) AS orders_count
    FROM active_users u
    LEFT JOIN orders o ON o.user_id = u.id
    GROUP BY u.id, u.email
)
SELECT * FROM user_orders WHERE orders_count >= 5;
```

**Recursive CTE** для иерархических данных (дерево комментариев,
организационная структура):

```sql
WITH RECURSIVE comment_tree AS (
    -- anchor: корневые комментарии
    SELECT id, parent_id, content, 0 AS depth
    FROM comments
    WHERE parent_id IS NULL
    UNION ALL
    -- recursive: дочерние
    SELECT c.id, c.parent_id, c.content, ct.depth + 1
    FROM comments c
    JOIN comment_tree ct ON c.parent_id = ct.id
)
SELECT * FROM comment_tree ORDER BY depth, id;
```

Subquery vs CTE: CTE читаемее для сложных запросов. По производительности —
зависит от СУБД: PostgreSQL до 12 материализовала CTE безусловно
(можно было «спрятаться» от оптимизатора), с 12+ inlines по умолчанию.

## UPSERT (INSERT ... ON CONFLICT)

Idempotent insert: «вставить если нет, обновить если есть». Стандартный
паттерн для consume-once в очередях и для де-дупликации.

```sql
-- PostgreSQL
INSERT INTO users (email, name)
VALUES ('alice@example.com', 'Alice')
ON CONFLICT (email)
DO UPDATE SET name = EXCLUDED.name, updated_at = NOW();

-- Если конфликт игнорируется (просто сохранить если новый):
INSERT INTO events (id, payload)
VALUES (?, ?)
ON CONFLICT (id) DO NOTHING;
```

```sql
-- MySQL
INSERT INTO users (email, name)
VALUES ('alice@example.com', 'Alice')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Стандартный SQL (не везде):
MERGE INTO users target
USING (VALUES ('alice@example.com', 'Alice')) AS source(email, name)
ON target.email = source.email
WHEN MATCHED THEN UPDATE SET name = source.name
WHEN NOT MATCHED THEN INSERT (email, name) VALUES (source.email, source.name);
```

UPSERT требует UNIQUE-индекс на колонке конфликта. Без него
`ON CONFLICT` не сработает.

## NULL и тернарная логика

`NULL` — не значение, а отметка «значение неизвестно». Большинство
сравнений с `NULL` дают `NULL` (не `TRUE`, не `FALSE`).

```sql
SELECT NULL = NULL;       -- NULL (не TRUE!)
SELECT NULL <> NULL;      -- NULL
SELECT NULL = 'anything'; -- NULL
SELECT NULL IS NULL;      -- TRUE  ← правильный способ проверки
```

Логические операции с `NULL`:

| A | B | `A AND B` | `A OR B` |
|---|---|-----------|----------|
| TRUE | NULL | NULL | TRUE |
| FALSE | NULL | FALSE | NULL |
| NULL | NULL | NULL | NULL |

Практические следствия:

- `WHERE col != 'X'` НЕ выберет строки с `col IS NULL` — это часто
  баг. Используй `WHERE col != 'X' OR col IS NULL`.
- `COUNT(col)` игнорирует NULL, `COUNT(*)` считает все строки.
- `SUM`, `AVG`, `MIN`, `MAX` игнорируют NULL.
- `string1 || NULL = NULL` (для конкатенации).

Полезные функции:

```sql
COALESCE(col, 'default')   -- первое non-NULL значение
NULLIF(col, '')            -- NULL если col равно '' (превратить пустую строку в NULL)
col IS DISTINCT FROM other -- то же что <>, но NULL = NULL даёт FALSE (а не NULL)
```

## Пагинация

**OFFSET / LIMIT** — самый простой подход, но медленный на глубоких
страницах (БД должна перебрать `OFFSET` строк, прежде чем выдать `LIMIT`).

```sql
-- Страница 100 — БД перебирает 2000 строк впустую
SELECT id, title FROM posts ORDER BY created_at DESC LIMIT 20 OFFSET 2000;
```

**Keyset pagination (cursor-based)** — быстрая, использует индекс:

```sql
-- Первая страница
SELECT id, title, created_at FROM posts
ORDER BY created_at DESC, id DESC
LIMIT 20;

-- Следующая страница — передаём последний (created_at, id) с предыдущей
SELECT id, title, created_at FROM posts
WHERE (created_at, id) < (?, ?)   -- last_created_at, last_id
ORDER BY created_at DESC, id DESC
LIMIT 20;
```

Composite key `(created_at, id)` нужен потому, что `created_at` может
дублироваться (две записи в одну миллисекунду) — `id` обеспечивает
строгий порядок.

Когда какой подход:

| Подход | Когда | Минусы |
|---|---|---|
| OFFSET/LIMIT | Малые наборы (<1K строк всего), произвольный переход | Медленно на глубоких страницах |
| Keyset | Большие наборы, последовательная навигация | Нет «прыжка на страницу 50» |

## Транзакции и изоляция

> Подробная шпаргалка: [sql-transactions-isolation](sql-transactions-isolation.md) — ACID, уровни изоляции, аномалии, MVCC, блокировки, Spring @Transactional.

Транзакция нужна, когда несколько изменений должны быть атомарными.

```sql
BEGIN;

UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

COMMIT;
```

Ключевые риски:
- lost update,
- dirty/non-repeatable/phantom reads,
- дедлоки при конкурирующих обновлениях.

Выбор уровня изоляции — это trade-off между консистентностью и производительностью.

## Индексы и план выполнения

Индекс ускоряет чтение, но удорожает запись.

Базовый workflow:
1. измерить медленный запрос,
2. посмотреть `EXPLAIN`/`EXPLAIN ANALYZE`,
3. добавить/скорректировать индекс,
4. перепроверить план и latency.

Пример:

```sql
CREATE INDEX idx_orders_user_created
    ON orders (user_id, created_at DESC);
```

Антипаттерн: индексировать «на всякий случай» без реальной статистики запросов.

## SQL в Java-приложении

Всегда используйте параметризованные запросы:

```java
String sql = "SELECT id, email FROM users WHERE id = ?";
try (PreparedStatement ps = connection.prepareStatement(sql)) {
    ps.setLong(1, userId);
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            // map rows
        }
    }
}
```

Почему это важно:
- защита от SQL injection,
- повторное использование плана,
- предсказуемое поведение в production.

## Operational context: SQL в production

Что нужно документировать и мониторить:
- p95/p99 latency по классам запросов,
- error rate (timeouts, deadlocks, lock wait),
- slow query log,
- доля full table scan,
- saturation (CPU, IOPS, connection pool).

Практика для команд:
- лимит времени выполнения запроса,
- budget на количество round trips,
- отдельные SLO для OLTP и отчетных запросов.

## Антипаттерны

- `SELECT *` в горячих endpoint.
- Сложные `JOIN` без индексов по ключам соединения.
- Длинные транзакции «через весь бизнес-процесс».
- N+1 запросы на уровне приложения.
- Ручная конкатенация SQL-строк с пользовательским вводом.

## Troubleshooting

| Симптом | Частая причина | Что делать |
|--------|----------------|-----------|
| Запрос внезапно стал медленным | изменился план выполнения | проверить `EXPLAIN ANALYZE`, статистику и индексы |
| Периодические таймауты | блокировки или нехватка индексов | проверить lock wait, сократить транзакции, добавить индекс |
| Высокая нагрузка CPU на БД | full scans и тяжёлые сортировки | добавить покрывающие индексы, упростить запросы, ввести лимиты |
| Дедлоки при обновлениях | разный порядок захвата строк/таблиц | унифицировать порядок операций, сократить scope транзакций |
| Ошибки безопасности | строковая сборка SQL | перейти на prepared statements/ORM parameters |

SQL-база важна, но production-устойчивость определяется тем, как вы измеряете, документируете и эволюционируете запросы.

