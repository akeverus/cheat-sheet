---
title: "SQL: Основы"
description: "Практическое руководство по SQL: DDL/DML/DQL, joins, агрегаты, транзакции, индексы, производительность и troubleshooting в production."
tags:
  - databases
  - sql
  - sql-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
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

## Содержание

- [Что важно понимать до первого SELECT](#что-важно-понимать-до-первого-select)
- [Категории SQL-команд](#категории-sql-команд)
- [Базовые запросы, которые нужны каждый день](#базовые-запросы-которые-нужны-каждый-день)
- [JOIN, GROUP BY и оконные функции](#join-group-by-и-оконные-функции)
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

Антипаттерн: индексировать "на всякий случай" без реальной статистики запросов.

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
- Длинные транзакции "через весь бизнес-процесс".
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

