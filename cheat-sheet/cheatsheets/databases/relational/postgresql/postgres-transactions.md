---
title: "PostgreSQL: транзакции, MVCC и блокировки"
description: "Полное руководство по транзакциям, MVCC, уровням изоляции и блокировкам в PostgreSQL. Включает ACID, команды транзакций, диагностику блокировок, дедлоки и практические советы по оптимизации"
tags: ["postgresql", "database", "transactions", "mvcc", "acid", "locks", "isolation", "concurrency", "deadlocks", "performance"]
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md"]
next: ["databases/postgres-design.md"]
updated: "2025-01-11"
related: ["databases/postgres-basics.md", "databases/postgres-design.md", "databases/postgres-indexes.md", "databases/postgres-admin.md"]
---

# PostgreSQL: транзакции, MVCC и блокировки

Это подробное руководство по транзакциям, Multi-Version Concurrency Control (MVCC), уровням изоляции и блокировкам в PostgreSQL. Вы узнаете о принципах ACID, различных типах блокировок, стратегиях предотвращения конфликтов и оптимизации производительности.

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

### Официальная документация PostgreSQL

- [PostgreSQL Concurrency Control](https://www.postgresql.org/docs/current/mvcc.html)
- [PostgreSQL Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html)
- [PostgreSQL Locking](https://www.postgresql.org/docs/current/explicit-locking.html)
- [PostgreSQL Monitoring](https://www.postgresql.org/docs/current/monitoring.html)

### Дополнительные ресурсы

- [PostgreSQL Wiki - Locking](https://wiki.postgresql.org/wiki/Lock_Monitoring)
- [Baeldung - PostgreSQL Transactions](https://www.baeldung.com/postgresql)
- [PGCon - Advanced PostgreSQL Internals](https://www.pgcon.org/)

См. также: `postgres-basics.md`, `postgres-design.md`, `postgres-indexes.md`, `postgres-admin.md`.

## Содержание
- [ACID и транзакции](#acid-и-транзакции)
- [Базовые команды транзакций](#базовые-команды-транзакций)
- [MVCC и снимки данных](#mvcc-и-снимки-данных)
- [Уровни изоляции транзакций](#уровни-изоляции-транзакций)
- [Savepoint и откат частично](#savepoint-и-откат-частично)
- [Блокировки данных — основы](#блокировки-данных-основы)
- [Типы блокировок](#типы-блокировок)
- [Табличные блокировки](#табличные-блокировки)
- [Строковые блокировки](#строковые-блокировки)
- [Блокировки транзакций и advisory locks](#блокировки-транзакций-и-advisory-locks)
- [Диагностика блокировок](#диагностика-блокировок)
- [Устранение блокировок и дедлоков](#устранение-блокировок-и-дедлоков)
- [Аномалии изоляции](#аномалии-изоляции)
- [Практические советы по блокировкам](#практические-советы-по-блокировкам)

## ACID и транзакции
- Atomicity, Consistency, Isolation, Durability — всё изменение данных делается внутри транзакций.
- В PostgreSQL транзакция начинается при `BEGIN` или при первом запросе в режиме autocommit off.

## Базовые команды
```sql
BEGIN;
-- изменения
COMMIT;  -- зафиксировать
-- или
ROLLBACK;  -- откатить
```

## MVCC и снимки
- Каждая транзакция видит снимок данных на момент старта (snapshot).
- Изменения пишутся как новые версии строк; старые версии собирает VACUUM.
- Это уменьшает блокировки на чтение, но требует контроля «долгих» транзакций.

## Уровни изоляции
- `READ COMMITTED` (по умолчанию): видит только зафиксированные данные к моменту запроса.
- `REPEATABLE READ`: фиксирует снимок на весь транзакционный блок.
- `SERIALIZABLE`: пытается обеспечить эквивалентность последовательному выполнению, возможны ошибки сериализации.
```sql
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

## Savepoint и откат частично
```sql
BEGIN;
SAVEPOINT sp1;
-- шаг 1
ROLLBACK TO sp1; -- откатить до сохранённой точки
COMMIT;
```

## Советы по блокировкам
- Держите транзакции короткими; не оставляйте открытыми в idle in transaction.
- Пишите индексы под частые фильтры, чтобы уменьшить `FOR UPDATE` сканирование.
- Для массовых обновлений используйте батчи и контролируйте autovacuum (не выключайте надолго).

## Типы блокировок
- Табличные режимы: ACCESS SHARE (чтение), ROW SHARE, ROW EXCLUSIVE, SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, ACCESS EXCLUSIVE (DDL).
- Строковые блокировки: при `SELECT ... FOR UPDATE/SHARE`.
- Лок на уровень транзакции: блокировки ресурсов (например, advisory locks).

## Диагностика блокировок
- Активные блокировки:
```sql
SELECT locktype, relation::regclass, mode, granted, pid, query
FROM pg_locks l
JOIN pg_stat_activity a ON a.pid = l.pid
WHERE relation IS NOT NULL
ORDER BY granted DESC;
```
- Очередь ожидания (кто кого блокирует):
```sql
SELECT blocked.pid AS blocked_pid,
       blocker.pid AS blocker_pid,
       blocked.query AS blocked_query,
       blocker.query AS blocker_query
FROM pg_locks bl
JOIN pg_stat_activity blocked ON blocked.pid = bl.pid
JOIN pg_locks kl ON kl.locktype = bl.locktype
                AND kl.database IS NOT DISTINCT FROM bl.database
                AND kl.relation IS NOT DISTINCT FROM bl.relation
                AND kl.page IS NOT DISTINCT FROM bl.page
                AND kl.tuple IS NOT DISTINCT FROM bl.tuple
                AND kl.virtualxid IS NOT DISTINCT FROM bl.virtualxid
                AND kl.transactionid IS NOT DISTINCT FROM bl.transactionid
                AND kl.classid IS NOT DISTINCT FROM bl.classid
                AND kl.objid IS NOT DISTINCT FROM bl.objid
                AND kl.objsubid IS NOT DISTINCT FROM bl.objsubid
                AND kl.pid <> bl.pid
JOIN pg_stat_activity blocker ON blocker.pid = kl.pid
WHERE NOT bl.granted AND kl.granted;
```
- Снять блокирующего (осторожно):
```sql
SELECT pg_cancel_backend(<pid>);
```

## Блокировки данных — основы

В PostgreSQL блокировки (locks) используются для обеспечения согласованного доступа к данным в многопользовательской среде. Блокировки позволяют контролировать одновременное изменение данных несколькими параллельными транзакциями и предотвращают конфликты при работе с данными.

Когда транзакция устанавливает блокировку на определенный ресурс, она блокирует другие транзакции от выполнения операций, которые могут повлиять на этот ресурс до тех пор, пока блокировка не будет снята. Блокировки в PostgreSQL существуют на разных уровнях, включая блокировки таблиц, строк, страниц и даже отдельных полей.

**Основные принципы блокировок:**
- Блокировки обеспечивают целостность данных при параллельной работе.
- Блокировки могут приводить к дедлокам (deadlocks) при неправильном использовании.
- Важно выбирать правильный уровень изоляции транзакций для минимизации блокировок.
- Долгие транзакции удерживают блокировки дольше, что может замедлять работу БД.

## Типы блокировок

### Табличные блокировки

Табличные блокировки управляют доступом к целым таблицам. Они используются при операциях DDL (CREATE, ALTER, DROP) и при некоторых операциях DML.

**Режимы табличных блокировок (от самого слабого к самому сильному):**

1. **ACCESS SHARE** — Самая слабая блокировка. Используется при операциях `SELECT`. Совместима с ACCESS SHARE, ROW SHARE, ROW EXCLUSIVE. Не блокирует чтение, но блокирует ALTER TABLE, DROP TABLE.

2. **ROW SHARE** — Используется при `SELECT ... FOR UPDATE/SHARE`. Совместима с ACCESS SHARE, ROW SHARE, ROW EXCLUSIVE. Блокирует EXCLUSIVE и ACCESS EXCLUSIVE.

3. **ROW EXCLUSIVE** — Используется при `INSERT`, `UPDATE`, `DELETE`. Совместима с ACCESS SHARE, ROW SHARE. Блокирует SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, ACCESS EXCLUSIVE.

4. **SHARE UPDATE EXCLUSIVE** — Используется при `VACUUM`, `CREATE INDEX CONCURRENTLY`, `ALTER TABLE ... VALIDATE CONSTRAINT`. Блокирует SHARE UPDATE EXCLUSIVE, SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, ACCESS EXCLUSIVE.

5. **SHARE** — Используется при `CREATE INDEX` (без CONCURRENTLY). Совместима с ACCESS SHARE, ROW SHARE. Блокирует ROW EXCLUSIVE и выше.

6. **SHARE ROW EXCLUSIVE** — Используется при `CREATE UNIQUE INDEX` (без CONCURRENTLY). Блокирует SHARE и ниже, а также EXCLUSIVE, ACCESS EXCLUSIVE.

7. **EXCLUSIVE** — Блокирует все операции записи. Совместима только с ACCESS SHARE. Используется при некоторых видах ALTER TABLE.

8. **ACCESS EXCLUSIVE** — Самая сильная блокировка. Блокирует все операции, включая SELECT. Используется при `DROP TABLE`, `TRUNCATE`, `ALTER TABLE` (многие операции), `REINDEX`, `VACUUM FULL`.

**Пример автоматической блокировки:**
```sql
-- Транзакция 1 устанавливает ACCESS EXCLUSIVE при TRUNCATE
BEGIN;
TRUNCATE TABLE orders;
-- Другие транзакции не могут читать/писать в orders
COMMIT;
```

### Строковые блокировки

Строковые блокировки управляют доступом к отдельным строкам таблицы. Они используются при операциях `SELECT ... FOR UPDATE/SHARE` и автоматически при `UPDATE`/`DELETE`.

**Типы строковых блокировок:**
- **FOR UPDATE** — Эксклюзивная блокировка строки для обновления. Блокирует другие `FOR UPDATE` и `FOR SHARE`.
- **FOR SHARE** — Разделяемая блокировка строки для чтения. Совместима с другими `FOR SHARE`, блокирует `FOR UPDATE`.
- **FOR NO KEY UPDATE** — Более слабая версия `FOR UPDATE`, которая не блокирует `SELECT ... FOR KEY SHARE`.

**Пример строковых блокировок:**
```sql
-- Транзакция 1
BEGIN;
SELECT * FROM orders WHERE id = 1 FOR UPDATE;
-- Строка заблокирована для обновления
UPDATE orders SET status = 'processed' WHERE id = 1;
COMMIT;

-- Транзакция 2 (блокируется до завершения T1)
BEGIN;
SELECT * FROM orders WHERE id = 1 FOR UPDATE;
-- Ожидает завершения транзакции 1
COMMIT;
```

**Автоматические строковые блокировки:**
PostgreSQL автоматически блокирует строки при `UPDATE` и `DELETE`. Эти блокировки удерживаются до конца транзакции:
```sql
BEGIN;
UPDATE orders SET status = 'processed' WHERE customer_id = 123;
-- Все обновленные строки заблокированы
COMMIT;
```

### Блокировки транзакций и advisory locks

**Блокировки транзакций (transaction locks):**
- Используются для блокировки идентификаторов транзакций при конфликтах MVCC.
- Автоматически устанавливаются при обнаружении конфликтов сериализации.
- Не могут быть установлены вручную, только автоматически.

**Advisory locks (консультативные блокировки):**
Консультативные блокировки — это блокировки на уровне приложения, управляемые пользователем. Они не связаны с таблицами или строками, а используются для синхронизации на уровне бизнес-логики.

**Типы advisory locks:**
- **Session-level** — удерживаются до конца сессии.
- **Transaction-level** — удерживаются до конца транзакции (автоматически снимаются при COMMIT/ROLLBACK).

**Пример использования advisory locks:**
```sql
-- Заблокировать ресурс по ID пользователя
SELECT pg_advisory_lock(12345);
-- Выполнить критическую операцию
UPDATE user_accounts SET balance = balance - 100 WHERE user_id = 12345;
-- Разблокировать
SELECT pg_advisory_unlock(12345);

-- Transaction-level lock (автоматически снимается при COMMIT)
BEGIN;
SELECT pg_advisory_xact_lock(12345);
-- Операции
COMMIT; -- Блокировка автоматически снимается
```

**Проверка advisory locks:**
```sql
SELECT * FROM pg_locks WHERE locktype = 'advisory';
```

## Диагностика блокировок

### Поиск активных блокировок

**Все активные блокировки:**
```sql
SELECT locktype,
       relation::regclass AS table_name,
       mode,
       granted,
       pid,
       query
FROM pg_locks l
JOIN pg_stat_activity a ON a.pid = l.pid
WHERE relation IS NOT NULL
ORDER BY granted DESC, relation;
```

**Блокировки на конкретной таблице:**
```sql
SELECT locktype,
       mode,
       granted,
       pid,
       query,
       age(clock_timestamp(), query_start) AS duration
FROM pg_locks l
JOIN pg_stat_activity a ON a.pid = l.pid
WHERE relation = 'orders'::regclass
ORDER BY granted DESC;
```

### Поиск блокировок и очередей ожидания

**Кто кого блокирует (полная диагностика):**
```sql
SELECT COALESCE(blockingl.relation::regclass::text, blockingl.locktype) AS locked_item,
       now() - blockeda.query_start AS waiting_duration,
       blockeda.pid AS blocked_pid,
       blockeda.query AS blocked_query,
       blockedl.mode AS blocked_mode,
       blockinga.pid AS blocking_pid,
       blockinga.query AS blocking_query,
       blockingl.mode AS blocking_mode,
       blockinga.state AS blocking_state
FROM pg_locks blockedl
JOIN pg_stat_activity blockeda ON blockedl.pid = blockeda.pid
JOIN pg_locks blockingl
  ON (blockingl.transactionid = blockedl.transactionid
      OR (blockingl.relation = blockedl.relation 
          AND blockingl.locktype = blockedl.locktype))
 AND blockedl.pid <> blockingl.pid
JOIN pg_stat_activity blockinga 
  ON blockingl.pid = blockinga.pid 
 AND blockinga.datid = blockeda.datid
WHERE NOT blockedl.granted
  AND blockinga.datname = current_database()
ORDER BY waiting_duration DESC;
```

**Упрощенный запрос для поиска блокировок:**
```sql
SELECT blocked.pid AS blocked_pid,
       blocker.pid AS blocker_pid,
       blocked.query AS blocked_query,
       blocker.query AS blocker_query,
       age(clock_timestamp(), blocked.query_start) AS blocked_duration,
       age(clock_timestamp(), blocker.query_start) AS blocker_duration
FROM pg_locks bl
JOIN pg_stat_activity blocked ON blocked.pid = bl.pid
JOIN pg_locks kl 
  ON kl.locktype = bl.locktype
 AND kl.database IS NOT DISTINCT FROM bl.database
 AND kl.relation IS NOT DISTINCT FROM bl.relation
 AND kl.page IS NOT DISTINCT FROM bl.page
 AND kl.tuple IS NOT DISTINCT FROM bl.tuple
 AND kl.virtualxid IS NOT DISTINCT FROM bl.virtualxid
 AND kl.transactionid IS NOT DISTINCT FROM bl.transactionid
 AND kl.classid IS NOT DISTINCT FROM bl.classid
 AND kl.objid IS NOT DISTINCT FROM bl.objid
 AND kl.objsubid IS NOT DISTINCT FROM bl.objsubid
 AND kl.pid <> bl.pid
JOIN pg_stat_activity blocker ON blocker.pid = kl.pid
WHERE NOT bl.granted AND kl.granted;
```

**Долгие ожидания блокировок (> 1 минуты):**
```sql
SELECT now() - query_start AS waiting_duration,
       pid,
       query,
       state
FROM pg_stat_activity
WHERE wait_event_type = 'Lock'
  AND now() - query_start > interval '1 minute'
ORDER BY waiting_duration DESC;
```

### Поиск дедлоков

**Детектирование дедлоков:**
PostgreSQL автоматически обнаруживает дедлоки и откатывает одну из транзакций. Информация о дедлоках записывается в лог при настройке `log_lock_waits = on` и `deadlock_timeout`.

**Проверка логов на дедлоки:**
```sql
-- Настройка в postgresql.conf:
-- log_lock_waits = on
-- deadlock_timeout = 1s
```

**Мониторинг возможных дедлоков:**
```sql
-- Транзакции, которые могут участвовать в дедлоках
SELECT pid,
       usename,
       datname,
       query,
       state,
       age(clock_timestamp(), query_start) AS duration
FROM pg_stat_activity
WHERE state = 'active'
  AND pid <> pg_backend_pid()
ORDER BY duration DESC;
```

## Устранение блокировок и дедлоков

### Мягкое снятие блокировки

**Отменить запрос (не убивает сессию):**
```sql
SELECT pg_cancel_backend(<PID>);
```

Эта команда отправляет сигнал SIGINT процессу, который выполняет запрос. Запрос будет отменен, но соединение останется открытым.

### Жесткое снятие блокировки

**Завершить сессию (убивает соединение):**
```sql
SELECT pg_terminate_backend(<PID>);
```

Эта команда отправляет сигнал SIGTERM процессу, что завершает всю сессию и закрывает соединение. Используйте осторожно, так как это прервет все активные транзакции в этой сессии.

**Массовое завершение "висящих" сессий:**
```sql
-- Завершить все idle in transaction сессии старше 10 минут
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE state = 'idle in transaction'
  AND now() - state_change > interval '10 minutes'
  AND pid <> pg_backend_pid();
```

### Предотвращение блокировок

**Короткие транзакции:**
- Держите транзакции максимально короткими.
- Не выполняйте долгие операции внутри транзакций (например, вызовы внешних API).
- Используйте `COMMIT` как можно чаще.

**Правильный порядок блокировок:**
- Всегда блокируйте таблицы в одном и том же порядке, чтобы избежать дедлоков.
- Например, если транзакция 1 блокирует `orders`, затем `customers`, то транзакция 2 должна делать то же самое.

**Индексы для уменьшения блокировок:**
- Создавайте индексы на столбцах, используемых в `WHERE` для `UPDATE`/`DELETE`.
- Это уменьшает время сканирования и, следовательно, время удержания блокировок.

**Использование `SELECT ... FOR UPDATE SKIP LOCKED`:**
```sql
-- Пропустить заблокированные строки
SELECT * FROM jobs 
WHERE status = 'pending'
ORDER BY created_at
FOR UPDATE SKIP LOCKED
LIMIT 10;
```

Это полезно для очередей задач, где несколько воркеров обрабатывают разные строки параллельно.

## Аномалии изоляции

### Dirty Read (грязное чтение)

**Описание:** Чтение незафиксированных данных из другой транзакции.

**Статус в PostgreSQL:** Исключено. PostgreSQL не поддерживает уровень изоляции `READ UNCOMMITTED`.

**Пример (не возможен в PostgreSQL):**
```
T1: BEGIN; UPDATE accounts SET balance = 1000 WHERE id = 1; -- НЕ COMMIT
T2: BEGIN; SELECT balance FROM accounts WHERE id = 1; -- Видит 1000 (грязное чтение)
T1: ROLLBACK;
T2: -- Видел несуществующие данные
```

### Non-Repeatable Read (неповторяющееся чтение)

**Описание:** Разные значения одного и того же столбца в пределах одной транзакции.

**Статус в PostgreSQL:** Возможно в `READ COMMITTED` (по умолчанию).

**Пример:**
```sql
-- Транзакция 1
BEGIN;
SELECT balance FROM accounts WHERE id = 1;
-- Результат: 1000

-- Транзакция 2
BEGIN;
UPDATE accounts SET balance = 2000 WHERE id = 1;
COMMIT;

-- Транзакция 1 (продолжение)
SELECT balance FROM accounts WHERE id = 1;
-- Результат: 2000 (изменилось!)
COMMIT;
```

**Предотвращение:** Используйте `REPEATABLE READ` или `SERIALIZABLE`.

### Phantom Read (фантомное чтение)

**Описание:** Появление новых строк в результатах запроса в пределах одной транзакции.

**Статус в PostgreSQL:** Возможно в `READ COMMITTED`.

**Пример:**
```sql
-- Транзакция 1
BEGIN;
SELECT COUNT(*) FROM orders WHERE customer_id = 1;
-- Результат: 5

-- Транзакция 2
BEGIN;
INSERT INTO orders (customer_id, amount) VALUES (1, 100);
COMMIT;

-- Транзакция 1 (продолжение)
SELECT COUNT(*) FROM orders WHERE customer_id = 1;
-- Результат: 6 (появилась новая строка!)
COMMIT;
```

**Предотвращение:** Используйте `REPEATABLE READ` или `SERIALIZABLE`.

### Serialization Anomaly (аномалия сериализации)

**Описание:** Результат параллельного выполнения транзакций не эквивалентен никакому последовательному выполнению.

**Статус в PostgreSQL:** Возможна в `SERIALIZABLE`, но обнаруживается и предотвращается.

**Пример:**
```sql
-- Транзакция 1
BEGIN ISOLATION LEVEL SERIALIZABLE;
SELECT SUM(balance) FROM accounts;
-- Результат: 10000
UPDATE accounts SET balance = balance + 1000 WHERE id = 1;
COMMIT; -- Ошибка: serialization failure

-- Транзакция 2
BEGIN ISOLATION LEVEL SERIALIZABLE;
SELECT SUM(balance) FROM accounts;
-- Результат: 10000
UPDATE accounts SET balance = balance + 1000 WHERE id = 2;
COMMIT; -- Ошибка: serialization failure
```

**Обработка:** При ошибке сериализации перезапустите транзакцию:
```sql
BEGIN ISOLATION LEVEL SERIALIZABLE;
LOOP
  BEGIN
    -- Операции
    COMMIT;
    EXIT;
  EXCEPTION
    WHEN serialization_failure THEN
      ROLLBACK;
      -- Повторить попытку
  END;
END LOOP;
```

## Практические советы по блокировкам

### Рекомендации по проектированию

1. **Используйте индексы:**
   - Индексы на столбцах, используемых в `WHERE` для `UPDATE`/`DELETE`, уменьшают время блокировки.
   - Это особенно важно для таблиц с большим количеством строк.

2. **Минимизируйте время блокировки:**
   - Выполняйте все вычисления до начала транзакции.
   - Избегайте долгих операций внутри транзакций (сетевые вызовы, файловые операции).

3. **Используйте правильный уровень изоляции:**
   - По умолчанию `READ COMMITTED` подходит для большинства случаев.
   - Используйте `REPEATABLE READ` только при необходимости.
   - `SERIALIZABLE` используйте только при критических операциях.

4. **Правильный порядок блокировок:**
   - Всегда блокируйте таблицы в одном и том же порядке во всех транзакциях.
   - Это предотвращает дедлоки.

5. **Избегайте долгих транзакций:**
   - Держите транзакции короткими.
   - Не оставляйте транзакции в состоянии `idle in transaction`.
   - Используйте таймауты для автоматического отката долгих транзакций.

### Мониторинг и диагностика

1. **Регулярно проверяйте блокировки:**
   - Используйте запросы из раздела "Диагностика блокировок" для мониторинга.
   - Настройте алерты на долгие ожидания блокировок.

2. **Логирование блокировок:**
   - Включите `log_lock_waits = on` в `postgresql.conf`.
   - Установите `deadlock_timeout = 1s` для быстрого обнаружения дедлоков.

3. **Используйте расширения:**
   - `pg_stat_statements` для поиска медленных запросов, которые могут удерживать блокировки.
   - `auto_explain` для анализа планов запросов.

### Оптимизация производительности

1. **Batch операции:**
   - Для массовых обновлений используйте батчи вместо циклов.
   - Это уменьшает общее время удержания блокировок.

2. **Контроль autovacuum:**
   - Не отключайте autovacuum надолго.
   - Настройте `autovacuum_vacuum_cost_limit` для "горячих" таблиц.

3. **Использование `SKIP LOCKED`:**
   - Для очередей задач используйте `SELECT ... FOR UPDATE SKIP LOCKED`.
   - Это позволяет нескольким воркерам обрабатывать разные строки параллельно.

4. **Минимизация блокировок при DDL:**
   - Используйте `CREATE INDEX CONCURRENTLY` вместо `CREATE INDEX`.
   - Используйте `ALTER TABLE ... ADD COLUMN` с `DEFAULT` для быстрого добавления столбцов (в новых версиях PostgreSQL).

### Типичные проблемы и решения

1. **Проблема:** Долгие транзакции в `idle in transaction`.
   - **Решение:** Используйте таймауты, мониторьте и завершайте такие сессии.

2. **Проблема:** Дедлоки при обновлении нескольких таблиц.
   - **Решение:** Всегда блокируйте таблицы в одном порядке.

3. **Проблема:** Блокировки при массовых обновлениях.
   - **Решение:** Используйте батчи, добавляйте индексы, контролируйте autovacuum.

4. **Проблема:** Блокировки при создании индексов.
   - **Решение:** Используйте `CREATE INDEX CONCURRENTLY`.

5. **Проблема:** Блокировки при VACUUM FULL.
   - **Решение:** Используйте обычный `VACUUM` с `ANALYZE`, запускайте `VACUUM FULL` в окно обслуживания.

## Продвинутые техники работы с транзакциями

### Двухфазная фиксация (Two-Phase Commit)

**Двухфазная фиксация** позволяет координировать транзакции между несколькими базами данных или серверами PostgreSQL.

**Подготовка транзакции:**
```sql
-- Начать распределенную транзакцию
BEGIN;

-- Выполнить изменения
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

-- Подготовить транзакцию (фаза 1)
PREPARE TRANSACTION 'transfer_tx_123';

-- На втором сервере/базе данных
PREPARE TRANSACTION 'transfer_tx_123';
```

**Фиксация или откат:**
```sql
-- Фиксировать на всех участниках (фаза 2)
COMMIT PREPARED 'transfer_tx_123';

-- Или откатить
ROLLBACK PREPARED 'transfer_tx_123';
```

**Мониторинг подготовленных транзакций:**
```sql
-- Посмотреть подготовленные транзакции
SELECT * FROM pg_prepared_xacts;

-- Детальная информация
SELECT gid, prepared, owner, database, transaction
FROM pg_prepared_xacts;
```

### Вложенные транзакции и автономные транзакции

**Savepoints для вложенных транзакций:**
```sql
BEGIN;

-- Основная транзакция
INSERT INTO orders (customer_id, amount) VALUES (1, 100.00);

SAVEPOINT sp1;

-- Вложенная "транзакция"
INSERT INTO order_items (order_id, product_id, quantity)
VALUES (currval('orders_id_seq'), 1, 2);

-- Если что-то пошло не так
ROLLBACK TO SAVEPOINT sp1;

-- Продолжить основную транзакцию
INSERT INTO order_items (order_id, product_id, quantity)
VALUES (currval('orders_id_seq'), 2, 1);

COMMIT;
```

**Автономные транзакции в функциях:**
```sql
CREATE OR REPLACE FUNCTION log_audit_event(event_type text, event_data jsonb)
RETURNS void AS $$
BEGIN
    -- Автономная транзакция - изменения сохраняются независимо от основной транзакции
    PERFORM dblink('dbname=audit_db',
        'INSERT INTO audit_log (event_type, event_data, logged_at) ' ||
        'VALUES (' || quote_literal(event_type) || ', ' || quote_literal(event_data::text) || ', now())'
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Логируем ошибку, но не прерываем основную транзакцию
        RAISE WARNING 'Failed to log audit event: %', SQLERRM;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

### Транзакции и курсоры

**Использование курсоров в транзакциях:**
```sql
BEGIN;

-- Создать курсор
DECLARE order_cursor CURSOR FOR
    SELECT id, customer_id, total_amount
    FROM orders
    WHERE status = 'pending'
    ORDER BY created_at
    FOR UPDATE;

-- Обработать записи по одной
FETCH NEXT FROM order_cursor INTO order_id, customer_id, amount;

WHILE FOUND LOOP
    -- Проверить баланс
    IF (SELECT balance FROM customers WHERE id = customer_id) >= amount THEN
        -- Обновить статус заказа
        UPDATE orders SET status = 'processed' WHERE CURRENT OF order_cursor;

        -- Списать средства
        UPDATE customers SET balance = balance - amount WHERE id = customer_id;
    ELSE
        -- Отметить как отклоненный
        UPDATE orders SET status = 'rejected' WHERE CURRENT OF order_cursor;
    END IF;

    FETCH NEXT FROM order_cursor INTO order_id, customer_id, amount;
END LOOP;

CLOSE order_cursor;

COMMIT;
```

### Транзакции и временные таблицы

**Временные таблицы в транзакциях:**
```sql
BEGIN;

-- Создать временную таблицу (видна только в этой сессии)
CREATE TEMP TABLE temp_order_summary (
    customer_id int,
    total_orders int,
    total_amount numeric
) ON COMMIT DROP;  -- Удалить при завершении транзакции

-- Заполнить временную таблицу
INSERT INTO temp_order_summary
SELECT
    customer_id,
    count(*) as total_orders,
    sum(amount) as total_amount
FROM orders
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY customer_id;

-- Использовать данные для обновления
UPDATE customers
SET order_summary = row_to_json(tos)::jsonb
FROM temp_order_summary tos
WHERE customers.id = tos.customer_id;

COMMIT;  -- Временная таблица будет удалена автоматически
```

### Read-Only транзакции

**Оптимизация для чтения:**
```sql
-- Начать read-only транзакцию
BEGIN READ ONLY;

-- Любые попытки изменения данных вызовут ошибку
-- UPDATE customers SET name = 'New Name' WHERE id = 1;  -- ERROR!

-- Только чтение разрешено
SELECT * FROM customers WHERE active = true;

COMMIT;
```

**Использование в приложениях:**
```sql
-- Функция для безопасного чтения
CREATE OR REPLACE FUNCTION get_customer_balance(customer_id int)
RETURNS numeric AS $$
DECLARE
    balance numeric;
BEGIN
    -- Начать read-only транзакцию
    SET TRANSACTION READ ONLY;

    SELECT balance INTO balance
    FROM customers
    WHERE id = customer_id;

    RETURN balance;
END;
$$ LANGUAGE plpgsql;
```

## MVCC - глубокое погружение

### Внутренняя структура версий строк

**Структура строки в PostgreSQL:**
```
tuple header (23 bytes):
  - t_xmin: XID транзакции, создавшей эту версию
  - t_xmax: XID транзакции, удалившей эту версию (0 если не удалена)
  - t_cid: command ID в транзакции
  - t_ctid: TID текущей версии (для цепочек обновлений)

user data:
  - actual column values
```

**Анализ версий строк:**
```sql
-- Включить расширенный вывод
SET enable_indexscan = off;
SET enable_bitmapscan = off;

-- Посмотреть все версии строки
SELECT id, xmin, xmax, ctid, *
FROM customers
WHERE id = 1;

-- Использование pageinspect для глубокого анализа
CREATE EXTENSION pageinspect;

-- Посмотреть содержимое страницы
SELECT * FROM page_header(get_raw_page('customers', 0));

-- Посмотреть все кортежи на странице
SELECT * FROM heap_page_items(get_raw_page('customers', 0));
```

### Visibility и Snapshot

**Алгоритм определения видимости:**
```sql
-- Строка видна для транзакции T, если:
-- 1. xmin <= T.xid (создана до или в T)
-- 2. xmax = 0 ИЛИ xmax >= T.xid (не удалена в T)
-- 3. xmin не aborted ИЛИ T.xid видел abort
-- 4. xmax = 0 ИЛИ xmax aborted ИЛИ T.xid не видел commit xmax

-- Функция для проверки видимости
CREATE OR REPLACE FUNCTION is_tuple_visible(t_xmin xid, t_xmax xid, current_xid xid)
RETURNS boolean AS $$
BEGIN
    -- Упрощенная проверка (без учета abort/commit статусов)
    RETURN (t_xmin <= current_xid) AND
           (t_xmax = 0 OR t_xmax > current_xid);
END;
$$ LANGUAGE plpgsql;
```

**Анализ snapshot:**
```sql
-- Посмотреть текущий snapshot
SELECT txid_current(), pg_snapshot_xmin(txid_current_snapshot());

-- Детальный анализ snapshot
SELECT xmin, xmax, xmin_status, xmax_status
FROM pg_stat_activity
WHERE pid = pg_backend_pid();

-- Использование pgrowlocks для анализа блокировок строк
CREATE EXTENSION pgrowlocks;

SELECT * FROM pgrowlocks('customers');
```

### Влияние MVCC на производительность

**Bloat (раздувание таблиц):**
```sql
-- Найти таблицы с bloat
SELECT
    schemaname, tablename,
    n_tup_ins as inserts,
    n_tup_upd as updates,
    n_tup_del as deletes,
    n_live_tup as live,
    n_dead_tup as dead,
    CASE WHEN n_live_tup > 0 THEN round((n_dead_tup::float / n_live_tup) * 100, 2) ELSE 0 END as bloat_ratio
FROM pg_stat_user_tables
WHERE n_dead_tup > 0
ORDER BY bloat_ratio DESC;

-- Оценка размера bloat
SELECT
    schemaname, tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) as table_size,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) -
                   pg_relation_size(schemaname||'.'||tablename)) as index_size
FROM pg_stat_user_tables
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

**Оптимизация autovacuum:**
```sql
-- Настройки autovacuum для конкретной таблицы
ALTER TABLE customers SET (
    autovacuum_vacuum_scale_factor = 0.02,
    autovacuum_vacuum_threshold = 100,
    autovacuum_analyze_scale_factor = 0.01,
    autovacuum_analyze_threshold = 50
);

-- Мониторинг autovacuum
SELECT schemaname, tablename,
       last_vacuum, last_autovacuum,
       last_analyze, last_autoanalyze,
       vacuum_count, autovacuum_count,
       analyze_count, autoanalyze_count
FROM pg_stat_user_tables
ORDER BY last_autovacuum DESC NULLS LAST;

-- Ручной vacuum с анализом
VACUUM (VERBOSE, ANALYZE) customers;
```

## Продвинутые блокировки

### Advisory Locks

**Пользовательские блокировки для координации:**
```sql
-- Session-level advisory lock
SELECT pg_advisory_lock(12345);

-- Transaction-level advisory lock
SELECT pg_advisory_xact_lock(67890);

-- Try lock (не блокирует)
SELECT pg_try_advisory_lock(11111);

-- Unlock
SELECT pg_advisory_unlock(12345);

-- Проверить наличие блокировки
SELECT * FROM pg_locks WHERE locktype = 'advisory';
```

**Использование в приложениях:**
```sql
-- Функция для безопасного обновления с advisory lock
CREATE OR REPLACE FUNCTION update_inventory_safely(product_id int, quantity_change int)
RETURNS void AS $$
BEGIN
    -- Захватить advisory lock для товара
    PERFORM pg_advisory_xact_lock(product_id);

    -- Проверить, что достаточно товара
    IF (SELECT stock_quantity FROM products WHERE id = product_id) + quantity_change >= 0 THEN
        UPDATE products
        SET stock_quantity = stock_quantity + quantity_change,
            updated_at = now()
        WHERE id = product_id;
    ELSE
        RAISE EXCEPTION 'Insufficient stock for product %', product_id;
    END IF;
END;
$$ LANGUAGE plpgsql;
```

### Conditional и Skip Locks

**Условные блокировки:**
```sql
-- Использовать SKIP LOCKED для обработки очередей
UPDATE job_queue
SET status = 'processing', started_at = now()
WHERE id = (
    SELECT id FROM job_queue
    WHERE status = 'pending'
    ORDER BY priority DESC, created_at ASC
    FOR UPDATE SKIP LOCKED
    LIMIT 1
)
RETURNING id;
```

**NOWAIT для немедленного отклонения:**
```sql
-- Попытаться заблокировать без ожидания
BEGIN;

SELECT * FROM customers WHERE id = 1 FOR UPDATE NOWAIT;

-- Если строка заблокирована, получим ошибку:
-- ERROR:  could not obtain lock on row in relation "customers"

COMMIT;
```

### Row-level security и блокировки

**Row Security Policies:**
```sql
-- Включить RLS
ALTER TABLE sensitive_data ENABLE ROW LEVEL SECURITY;

-- Создать политику
CREATE POLICY user_data_policy ON sensitive_data
    FOR ALL
    USING (user_id = current_user_id());

-- Проверить политики
SELECT * FROM pg_policies WHERE tablename = 'sensitive_data';
```

## Мониторинг и диагностика

### Расширенная диагностика блокировок

**Подробный анализ pg_locks:**
```sql
-- Все текущие блокировки
SELECT
    locktype,
    database,
    relation::regclass,
    page,
    tuple,
    virtualxid,
    transactionid,
    classid,
    objid,
    objsubid,
    virtualtransaction,
    pid,
    mode,
    granted,
    fastpath
FROM pg_locks
ORDER BY pid;

-- Заблокированные запросы
SELECT
    blocked_locks.pid AS blocked_pid,
    blocked_activity.usename AS blocked_user,
    blocking_locks.pid AS blocking_pid,
    blocking_activity.usename AS blocking_user,
    blocked_activity.query AS blocked_statement,
    blocking_activity.query AS current_statement_in_blocking_process
FROM pg_catalog.pg_locks blocked_locks
JOIN pg_catalog.pg_stat_activity blocked_activity ON blocked_activity.pid = blocked_locks.pid
JOIN pg_catalog.pg_locks blocking_locks
    ON blocking_locks.locktype = blocked_locks.locktype
    AND blocking_locks.database IS NOT DISTINCT FROM blocked_locks.database
    AND blocking_locks.relation IS NOT DISTINCT FROM blocked_locks.relation
    AND blocking_locks.page IS NOT DISTINCT FROM blocked_locks.page
    AND blocking_locks.tuple IS NOT DISTINCT FROM blocked_locks.tuple
    AND blocking_locks.virtualxid IS NOT DISTINCT FROM blocked_locks.virtualxid
    AND blocking_locks.transactionid IS NOT DISTINCT FROM blocked_locks.transactionid
    AND blocking_locks.classid IS NOT DISTINCT FROM blocked_locks.classid
    AND blocking_locks.objid IS NOT DISTINCT FROM blocked_locks.objid
    AND blocking_locks.objsubid IS NOT DISTINCT FROM blocked_locks.objsubid
    AND blocking_locks.pid != blocked_locks.pid
JOIN pg_catalog.pg_stat_activity blocking_activity ON blocking_activity.pid = blocking_locks.pid
WHERE NOT blocked_locks.granted;
```

**Анализ ожидающих транзакций:**
```sql
-- Транзакции, ожидающие блокировок
SELECT
    psa.pid,
    psa.usename,
    psa.client_addr,
    psa.client_port,
    psa.query,
    psa.state,
    psa.wait_event_type,
    psa.wait_event,
    psa.xact_start,
    now() - psa.xact_start as duration
FROM pg_stat_activity psa
WHERE psa.state = 'active'
ORDER BY duration DESC;
```

### Профилирование транзакций

**Анализ производительности транзакций:**
```sql
-- Транзакции с наибольшим временем выполнения
SELECT
    pid,
    usename,
    client_addr,
    xact_start,
    now() - xact_start as duration,
    query
FROM pg_stat_activity
WHERE state = 'active' AND xact_start IS NOT NULL
ORDER BY duration DESC;

-- Статистика по типам блокировок
SELECT
    mode,
    count(*) as count,
    count(*) * 100.0 / sum(count(*)) over() as percentage
FROM pg_locks
GROUP BY mode
ORDER BY count DESC;

-- Анализ конфликтов блокировок
SELECT
    datname,
    usename,
    client_addr,
    query,
    wait_event_type,
    wait_event
FROM pg_stat_activity
WHERE wait_event_type = 'Lock'
ORDER BY query_start;
```

### Инструменты для мониторинга

**pg_stat_statements для анализа запросов:**
```sql
-- Включить расширение
CREATE EXTENSION pg_stat_statements;

-- Анализ запросов по блокировкам
SELECT
    query,
    calls,
    total_time,
    mean_time,
    rows,
    shared_blks_hit,
    shared_blks_read,
    shared_blks_dirtied,
    shared_blks_written
FROM pg_stat_statements
WHERE query LIKE '%FOR UPDATE%' OR query LIKE '%SELECT ... FOR%'
ORDER BY total_time DESC
LIMIT 10;
```

**pg_buffercache для анализа кэша:**
```sql
CREATE EXTENSION pg_buffercache;

-- Анализ использования буферного кэша
SELECT
    c.relname,
    count(*) as buffers,
    count(*) * 100.0 / (SELECT count(*) FROM pg_buffercache) as buffer_percent
FROM pg_buffercache b
JOIN pg_class c ON b.relfilenode = c.relfilenode
GROUP BY c.relname
ORDER BY buffers DESC
LIMIT 10;
```

## Оптимизация производительности

### Настройка параметров PostgreSQL

**Оптимизация для высоконагруженных систем:**
```sql
-- Увеличить количество соединений
ALTER SYSTEM SET max_connections = 200;

-- Настроить shared buffers (обычно 25% RAM)
ALTER SYSTEM SET shared_buffers = '2GB';

-- Настроить work_mem для сложных запросов
ALTER SYSTEM SET work_mem = '64MB';

-- Настроить maintenance_work_mem
ALTER SYSTEM SET maintenance_work_mem = '512MB';

-- Настроить checkpoint_segments (или max_wal_size в новых версиях)
ALTER SYSTEM SET max_wal_size = '2GB';

-- Настроить autovacuum
ALTER SYSTEM SET autovacuum_max_workers = 4;
ALTER SYSTEM SET autovacuum_naptime = '20s';

-- Перезагрузить конфигурацию
SELECT pg_reload_conf();
```

**Оптимизация уровней изоляции:**
```sql
-- Для аналитических запросов использовать READ ONLY
BEGIN READ ONLY;
SELECT * FROM large_table WHERE created_at > '2024-01-01';
COMMIT;

-- Для массовых обновлений использовать REPEATABLE READ
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

UPDATE products SET price = price * 1.1
WHERE category = 'electronics';

COMMIT;
```

### Индексы и блокировки

**Оптимизация индексов для уменьшения блокировок:**
```sql
-- Создать индекс для часто блокирующихся запросов
CREATE INDEX CONCURRENTLY idx_orders_status_created
ON orders (status, created_at DESC);

-- Частичные индексы для уменьшения размера
CREATE INDEX idx_active_orders
ON orders (customer_id, created_at)
WHERE status = 'active';

-- Покрывающие индексы
CREATE INDEX idx_order_summary
ON orders (customer_id, total_amount, created_at)
WHERE status = 'completed';
```

### Partitioning для больших таблиц

**Партиционирование для снижения блокировок:**
```sql
-- Создать партиционированную таблицу
CREATE TABLE orders (
    id serial,
    customer_id int,
    order_date date,
    total_amount numeric,
    status text
) PARTITION BY RANGE (order_date);

-- Создать партиции
CREATE TABLE orders_2024_01 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

-- Создать индексы на партициях
CREATE INDEX idx_orders_2024_01_status ON orders_2024_01 (status);
CREATE INDEX idx_orders_2024_02_status ON orders_2024_02 (status);
```

### Connection Pooling

**Настройка PgBouncer:**
```ini
# pgbouncer.ini
[databases]
mydb = host=127.0.0.1 port=5432 dbname=mydb

[pgbouncer]
listen_port = 6432
listen_addr = 127.0.0.1
auth_type = md5
auth_file = /etc/pgbouncer/userlist.txt
pool_mode = transaction
max_client_conn = 1000
default_pool_size = 20
min_pool_size = 5
reserve_pool_size = 5
```

**Использование в приложениях:**
```java
// HikariCP конфигурация для PostgreSQL
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:6432/mydb");
config.setUsername("myuser");
config.setPassword("mypass");
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.setConnectionTimeout(30000);
config.setIdleTimeout(600000);
config.setMaxLifetime(1800000);

DataSource ds = new HikariDataSource(config);
```

## Распределенные транзакции

### Использование с Foreign Data Wrappers

**Настройка postgres_fdw:**
```sql
-- Создать расширение
CREATE EXTENSION postgres_fdw;

-- Создать сервер
CREATE SERVER shard1
FOREIGN DATA WRAPPER postgres_fdw
OPTIONS (host 'shard1.example.com', port '5432', dbname 'mydb');

-- Создать user mapping
CREATE USER MAPPING FOR app_user
SERVER shard1
OPTIONS (user 'remote_user', password 'remote_pass');

-- Создать foreign table
CREATE FOREIGN TABLE remote_orders (
    id int,
    customer_id int,
    amount numeric
)
SERVER shard1
OPTIONS (table_name 'orders');
```

**Распределенные транзакции:**
```sql
-- Начать распределенную транзакцию
BEGIN;

-- Локальные изменения
UPDATE local_inventory SET quantity = quantity - 1 WHERE product_id = 1;

-- Удаленные изменения
UPDATE remote_orders SET status = 'shipped' WHERE id = 123;

-- Фиксировать все изменения
COMMIT;
```

### Logical Replication и транзакции

**Настройка логической репликации:**
```sql
-- На publisher
CREATE PUBLICATION orders_pub FOR TABLE orders;

-- На subscriber
CREATE SUBSCRIPTION orders_sub
    CONNECTION 'host=publisher.example.com port=5432 user=replicator dbname=mydb'
    PUBLICATION orders_pub;

-- Мониторинг репликации
SELECT * FROM pg_stat_subscription;
SELECT * FROM pg_stat_replication;
```

## Безопасность транзакций

### Безопасность на уровне строк (RLS)

**Row Level Security:**
```sql
-- Включить RLS
ALTER TABLE user_data ENABLE ROW LEVEL SECURITY;

-- Создать политику для пользователей
CREATE POLICY user_policy ON user_data
    FOR ALL
    USING (user_id = current_setting('app.user_id')::int);

-- Создать политику для администраторов
CREATE POLICY admin_policy ON user_data
    FOR ALL
    USING (current_setting('app.role') = 'admin');

-- Проверить политики
SELECT * FROM pg_policies WHERE tablename = 'user_data';
```

### Аудит транзакций

**Настройка аудита:**
```sql
-- Создать таблицу аудита
CREATE TABLE audit_log (
    id serial PRIMARY KEY,
    table_name text,
    operation text,
    old_values jsonb,
    new_values jsonb,
    user_id int,
    timestamp timestamp DEFAULT now()
);

-- Функция аудита
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS trigger AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, operation, new_values, user_id)
        VALUES (TG_TABLE_NAME, 'INSERT', row_to_json(NEW)::jsonb, NEW.user_id);
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, operation, old_values, new_values, user_id)
        VALUES (TG_TABLE_NAME, 'UPDATE', row_to_json(OLD)::jsonb, row_to_json(NEW)::jsonb, NEW.user_id);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, operation, old_values, user_id)
        VALUES (TG_TABLE_NAME, 'DELETE', row_to_json(OLD)::jsonb, OLD.user_id);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Создать триггер
CREATE TRIGGER audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON sensitive_table
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
```

## Интеграция с приложениями

### Spring Framework интеграция

**Spring @Transactional:**
```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryService inventoryService;

    @Transactional(isolation = Isolation.READ_COMMITTED,
                  propagation = Propagation.REQUIRED,
                  rollbackFor = Exception.class)
    public void processOrder(OrderRequest request) {
        // Проверить наличие товара
        inventoryService.checkAvailability(request.getProductId(), request.getQuantity());

        // Создать заказ
        Order order = createOrder(request);

        // Списать товар
        inventoryService.deductStock(request.getProductId(), request.getQuantity());

        // Сохранить заказ
        orderRepository.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditOrder(Long orderId) {
        // Автономная транзакция для аудита
        auditService.logOrderCreation(orderId);
    }
}
```

### JDBC транзакции

**Ручное управление транзакциями:**
```java
public void transferMoney(long fromAccount, long toAccount, BigDecimal amount) throws SQLException {
    Connection conn = dataSource.getConnection();
    conn.setAutoCommit(false);

    try {
        // Проверить баланс отправителя
        BigDecimal senderBalance = getBalance(conn, fromAccount);
        if (senderBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        // Списать средства
        updateBalance(conn, fromAccount, senderBalance.subtract(amount));

        // Зачислить средства
        BigDecimal receiverBalance = getBalance(conn, toAccount);
        updateBalance(conn, toAccount, receiverBalance.add(amount));

        // Записать в историю
        logTransfer(conn, fromAccount, toAccount, amount);

        conn.commit();

    } catch (SQLException e) {
        conn.rollback();
        throw e;
    } finally {
        conn.setAutoCommit(true);
        conn.close();
    }
}
```

### ORM интеграция (Hibernate)

**Hibernate транзакции:**
```java
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditService auditService;

    public void updateProductPrice(Long productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        BigDecimal oldPrice = product.getPrice();
        product.setPrice(newPrice);
        product.setUpdatedAt(Instant.now());

        productRepository.save(product);

        // Аудит в отдельной транзакции
        auditService.logPriceChange(productId, oldPrice, newPrice);
    }

    @Transactional(readOnly = true)
    public List<Product> findProductsByCategory(String category) {
        return productRepository.findByCategoryAndActiveTrue(category);
    }
}
```

## Мониторинг и алертинг

### Настройка алертов

**Bash скрипт для мониторинга блокировок:**
```bash
#!/bin/bash

# monitor_locks.sh - Мониторинг блокировок PostgreSQL

DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="mydb"
DB_USER="monitor"
THRESHOLD=10  # Максимальное количество ожидающих блокировок

LOCKED_QUERIES=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "
SELECT count(*)
FROM pg_stat_activity
WHERE wait_event_type = 'Lock' AND state = 'active';
")

if [ "$LOCKED_QUERIES" -gt "$THRESHOLD" ]; then
    echo "WARNING: High lock contention detected!"
    echo "Locked queries: $LOCKED_QUERIES"

    # Отправить email
    echo "PostgreSQL High Lock Contention Alert

Database: $DB_NAME
Host: $DB_HOST
Locked Queries: $LOCKED_QUERIES

Top blocking queries:
$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "
SELECT pid, usename, query
FROM pg_stat_activity
WHERE wait_event_type = 'Lock'
ORDER BY query_start
LIMIT 5;
")" | mail -s "PostgreSQL Lock Alert" admin@example.com

    exit 1
fi

echo "Lock monitoring OK. Locked queries: $LOCKED_QUERIES"
```

**Prometheus метрики:**
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']

# SQL запросы для экспорта метрик
# pg_stat_activity для активных транзакций
# pg_locks для количества блокировок
# pg_stat_database для статистики баз данных
```

### Автоматизация обслуживания

**Автоматическая очистка long-running транзакций:**
```sql
-- Функция для завершения idle транзакций
CREATE OR REPLACE FUNCTION terminate_idle_transactions(max_age interval)
RETURNS integer AS $$
DECLARE
    terminated_count integer := 0;
    rec record;
BEGIN
    FOR rec IN
        SELECT pid, usename, client_addr, xact_start, now() - xact_start as age
        FROM pg_stat_activity
        WHERE state = 'idle in transaction'
        AND now() - xact_start > max_age
    LOOP
        RAISE NOTICE 'Terminating idle transaction: PID=%, User=%, Age=%',
            rec.pid, rec.usename, rec.age;

        PERFORM pg_terminate_backend(rec.pid);
        terminated_count := terminated_count + 1;
    END LOOP;

    RETURN terminated_count;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Использование
SELECT terminate_idle_transactions('1 hour');
```

## Лучшие практики и рекомендации

### Архитектурные решения

**1. Разделение чтения и записи:**
```sql
-- Создать реплику для чтения
-- Настройка потоковой репликации
-- Использовать read-only транзакции для отчетов

-- В приложении
@Transactional(readOnly = true)
public List<Order> getOrderHistory(Long customerId) {
    // Чтение из реплики
    return orderRepository.findByCustomerId(customerId);
}

@Transactional
public void createOrder(OrderRequest request) {
    // Запись в мастер
    orderService.createOrder(request);
}
```

**2. Оптимистичные блокировки:**
```sql
-- Добавить поле версии
ALTER TABLE products ADD COLUMN version integer DEFAULT 1;

-- Обновление с проверкой версии
UPDATE products
SET name = 'New Name',
    version = version + 1,
    updated_at = now()
WHERE id = $1 AND version = $2;

-- В приложении
public void updateProductOptimistic(Long id, String newName, int expectedVersion) {
    int updated = jdbcTemplate.update(
        "UPDATE products SET name = ?, version = version + 1, updated_at = now() WHERE id = ? AND version = ?",
        newName, id, expectedVersion);

    if (updated == 0) {
        throw new OptimisticLockException("Product was modified by another transaction");
    }
}
```

**3. Event sourcing паттерн:**
```sql
-- Таблица событий
CREATE TABLE product_events (
    id bigserial PRIMARY KEY,
    product_id int NOT NULL,
    event_type text NOT NULL,
    event_data jsonb NOT NULL,
    created_at timestamp DEFAULT now(),
    version int NOT NULL
);

-- Функция применения событий
CREATE OR REPLACE FUNCTION apply_product_events(p_product_id int)
RETURNS void AS $$
DECLARE
    event record;
    current_state jsonb := '{}';
BEGIN
    FOR event IN
        SELECT * FROM product_events
        WHERE product_id = p_product_id
        ORDER BY version
    LOOP
        -- Применить логику события
        CASE event.event_type
            WHEN 'PRODUCT_CREATED' THEN
                current_state := event.event_data;
            WHEN 'PRICE_CHANGED' THEN
                current_state := jsonb_set(current_state, '{price}', event.event_data->'new_price');
            WHEN 'NAME_CHANGED' THEN
                current_state := jsonb_set(current_state, '{name}', event.event_data->'new_name');
        END CASE;
    END LOOP;

    -- Сохранить текущее состояние
    INSERT INTO product_current_state (product_id, state, version)
    VALUES (p_product_id, current_state, (SELECT max(version) FROM product_events WHERE product_id = p_product_id))
    ON CONFLICT (product_id) DO UPDATE SET
        state = EXCLUDED.state,
        version = EXCLUDED.version;
END;
$$ LANGUAGE plpgsql;
```

### Производительность

**Индексы для транзакций:**
```sql
-- Индекс для внешних ключей
CREATE INDEX idx_orders_customer_id ON orders (customer_id);

-- Частичный индекс для активных записей
CREATE INDEX idx_active_orders ON orders (created_at) WHERE status = 'active';

-- Покрывающий индекс для часто запрашиваемых полей
CREATE INDEX idx_order_summary ON orders (customer_id, total_amount, status);

-- GiST индекс для диапазонных запросов
CREATE INDEX idx_orders_date_range ON orders USING gist (daterange(created_at, created_at + interval '1 day'));
```

**Оптимизация запросов:**
```sql
-- Использовать CTE для сложных обновлений
WITH updated_orders AS (
    UPDATE orders
    SET status = 'processed'
    WHERE status = 'pending' AND created_at < now() - interval '1 hour'
    RETURNING id, customer_id
)
UPDATE customers
SET last_order_processed = now()
WHERE id IN (SELECT customer_id FROM updated_orders);

-- Использовать оконные функции для аналитики
SELECT
    customer_id,
    order_date,
    total_amount,
    sum(total_amount) OVER (PARTITION BY customer_id ORDER BY order_date) as running_total,
    avg(total_amount) OVER (PARTITION BY customer_id ORDER BY order_date ROWS 2 PRECEDING) as moving_avg
FROM orders
WHERE order_date >= '2024-01-01';
```

### Безопасность

**Защита от SQL injection в транзакциях:**
```java
// Правильный способ с prepared statements
@Transactional
public void transferMoneySecure(Long fromId, Long toId, BigDecimal amount) {
    // Использовать named parameters
    jdbcTemplate.update(
        "UPDATE accounts SET balance = balance - ? WHERE id = ?",
        amount, fromId);

    jdbcTemplate.update(
        "UPDATE accounts SET balance = balance + ? WHERE id = ?",
        amount, toId);

    auditService.logTransfer(fromId, toId, amount);
}

// Неправильный способ (уязвимый)
@Transactional
public void transferMoneyInsecure(Long fromId, Long toId, BigDecimal amount) {
    // Никогда не делайте так!
    String sql = "UPDATE accounts SET balance = balance - " + amount +
                 " WHERE id = " + fromId;
    jdbcTemplate.execute(sql);
}
```

**Валидация данных в транзакциях:**
```sql
CREATE OR REPLACE FUNCTION validate_order()
RETURNS trigger AS $$
BEGIN
    -- Проверить положительную сумму
    IF NEW.total_amount <= 0 THEN
        RAISE EXCEPTION 'Order amount must be positive';
    END IF;

    -- Проверить существование клиента
    IF NOT EXISTS (SELECT 1 FROM customers WHERE id = NEW.customer_id) THEN
        RAISE EXCEPTION 'Customer does not exist';
    END IF;

    -- Проверить лимит кредита
    IF (SELECT credit_limit FROM customers WHERE id = NEW.customer_id) < NEW.total_amount THEN
        RAISE EXCEPTION 'Order exceeds customer credit limit';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_order_trigger
    BEFORE INSERT ON orders
    FOR EACH ROW EXECUTE FUNCTION validate_order();
```

### Мониторинг и обслуживание

**Регулярные задачи обслуживания:**
```bash
# Ежедневный vacuum и analyze
0 2 * * * postgres vacuumdb --all --analyze

# Еженедельный reindex
0 3 * * 0 postgres reindexdb --all

# Мониторинг размера баз данных
0 */4 * * * postgres psql -c "SELECT datname, pg_size_pretty(pg_database_size(datname)) FROM pg_database;"

# Ротация WAL файлов
0 1 * * * postgres pg_ctl promote  # Для реплики, если нужно
```

**Автоматизированная диагностика:**
```sql
CREATE OR REPLACE FUNCTION diagnose_transaction_issues()
RETURNS TABLE (
    issue_type text,
    severity text,
    description text,
    recommendation text
) AS $$
BEGIN
    -- Долгие транзакции
    RETURN QUERY
    SELECT
        'Long Running Transaction'::text,
        'WARNING'::text,
        'Transaction running for ' || extract(epoch from (now() - xact_start)) || ' seconds'::text,
        'Consider terminating with pg_terminate_backend(' || pid || ')'::text
    FROM pg_stat_activity
    WHERE state = 'active'
    AND xact_start < now() - interval '5 minutes';

    -- Idle in transaction
    RETURN QUERY
    SELECT
        'Idle in Transaction'::text,
        'INFO'::text,
        'Session idle in transaction for ' || extract(epoch from (now() - xact_start)) || ' seconds'::text,
        'Monitor for potential locks'::text
    FROM pg_stat_activity
    WHERE state = 'idle in transaction'
    AND xact_start < now() - interval '1 minute';

    -- Высокое количество блокировок
    RETURN QUERY
    SELECT
        'High Lock Count'::text,
        CASE WHEN count(*) > 100 THEN 'CRITICAL' ELSE 'WARNING' END,
        'Found ' || count(*) || ' active locks'::text,
        'Check pg_locks for details'::text
    FROM pg_locks
    HAVING count(*) > 50;

END;
$$ LANGUAGE plpgsql;

-- Использование
SELECT * FROM diagnose_transaction_issues();
```

## Заключение

PostgreSQL предоставляет мощную систему транзакций на основе MVCC, которая обеспечивает высокую производительность, надежность и гибкость. Понимание принципов ACID, уровней изоляции, типов блокировок и стратегий оптимизации критически важно для разработки высокопроизводительных приложений.

### Ключевые takeaways:

1. **ACID** — фундамент транзакций, обеспечивающий надежность данных
2. **MVCC** — позволяет одновременный доступ без блокировок чтения-записи
3. **Уровни изоляции** — баланс между согласованностью и производительностью
4. **Блокировки** — необходимы для защиты от конфликтов, но требуют осторожного использования
5. **Мониторинг** — регулярный анализ блокировок и производительности
6. **Оптимизация** — правильные индексы, настройки и архитектурные решения

### Рекомендации по использованию:

- **Начинайте с READ COMMITTED** для большинства приложений
- **Избегайте SERIALIZABLE** без необходимости
- **Используйте advisory locks** для координации приложений
- **Мониторьте pg_locks** и pg_stat_activity регулярно
- **Оптимизируйте autovacuum** для больших баз данных
- **Тестируйте** блокировки и дедлоки в нагрузочных тестах

Эффективное использование транзакций PostgreSQL требует глубокого понимания механизмов MVCC, стратегий блокировок и принципов оптимизации. Правильное применение этих техник обеспечивает высокую производительность и надежность приложений.

