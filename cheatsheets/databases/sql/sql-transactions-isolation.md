---
title: "SQL: транзакции и уровни изоляции"
description: "Шпаргалка по транзакциям, ACID, уровням изоляции, аномалиям конкурентного доступа. Примеры dirty read, non-repeatable read, phantom read, lost update. Сравнение поведения PostgreSQL, MySQL, Oracle."
tags:
  - databases
  - sql
  - transactions
  - acid
  - isolation
  - concurrency
difficulty: "intermediate"
prerequisites: ["sql-basics.md"]
next: ["../relational/postgresql/postgres-transactions.md"]
updated: "2026-04-20"
---

# SQL: транзакции и уровни изоляции

## Полезные ссылки

- [SQL Standard — Transaction Isolation (ISO 9075)](https://www.iso.org/standard/76583.html)
- [PostgreSQL — Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html)
- [MySQL — InnoDB Transaction Isolation Levels](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html)

См. также: [[sql-basics]] — [[postgres-transactions]].


### См. также
- [[spring-data-jpa|Spring Data JPA/Hibernate]]
- [[spring-data-jdbc|Spring Data JDBC: Полное руководство по работе с базами данных]]
- [[hibernate-jpql-criteria|Hibernate: JPQL, HQL и Criteria API]]
## Содержание

- [ACID](#acid)
- [Аномалии конкурентного доступа](#аномалии-конкурентного-доступа)
  - [Dirty Read](#dirty-read)
  - [Non-Repeatable Read](#non-repeatable-read)
  - [Phantom Read](#phantom-read)
  - [Lost Update](#lost-update)
  - [Write Skew](#write-skew)
- [Уровни изоляции по SQL стандарту](#уровни-изоляции-по-sql-стандарту)
- [Сравнение СУБД](#сравнение-субд)
- [MVCC vs блокировки](#mvcc-vs-блокировки)
- [Optimistic vs Pessimistic Locking](#optimistic-vs-pessimistic-locking)
- [Deadlock](#deadlock)
- [Spring @Transactional](#spring-transactional)
- [Практические рекомендации](#практические-рекомендации)
- [См. также](#см-также)

## ACID

| Свойство | Суть | Что нарушается без него |
|----------|------|-------------------------|
| **Atomicity** | Все операции транзакции выполняются или ни одна | Частичное обновление: деньги списаны, но не зачислены |
| **Consistency** | БД переходит из одного валидного состояния в другое | Нарушение бизнес-правил (отрицательный баланс) |
| **Isolation** | Параллельные транзакции не видят промежуточные состояния друг друга | Грязное чтение, потерянные обновления |
| **Durability** | Закоммиченные данные не теряются даже при сбое | Потеря данных после подтверждения пользователю |

**Atomicity** гарантируется механизмом UNDO (rollback segment в Oracle, undo log в MySQL, xmin/xmax в PostgreSQL).

**Durability** гарантируется WAL (Write-Ahead Log) — данные сначала пишутся в журнал, потом на диск.

## Аномалии конкурентного доступа

### Dirty Read

Транзакция читает данные, которые другая транзакция **ещё не закоммитила**.

```text
T1: UPDATE accounts SET balance = 0 WHERE id = 1;  -- не коммитит
T2: SELECT balance FROM accounts WHERE id = 1;      -- видит 0 (dirty!)
T1: ROLLBACK;                                        -- откат
-- T2 прочитала данные, которых никогда не было
```

**Где возможна:** только на уровне READ UNCOMMITTED.

### Non-Repeatable Read

Повторное чтение **той же строки** в рамках одной транзакции даёт разные значения.

```text
T1: SELECT balance FROM accounts WHERE id = 1;  -- 1000
T2: UPDATE accounts SET balance = 500 WHERE id = 1; COMMIT;
T1: SELECT balance FROM accounts WHERE id = 1;  -- 500 (значение изменилось!)
```

**Где возможна:** READ UNCOMMITTED, READ COMMITTED.

### Phantom Read

Повторный запрос с условием WHERE возвращает **другой набор строк** (появились/исчезли строки).

```text
T1: SELECT * FROM orders WHERE status = 'new';      -- 5 строк
T2: INSERT INTO orders (status) VALUES ('new'); COMMIT;
T1: SELECT * FROM orders WHERE status = 'new';      -- 6 строк (фантом!)
```

**Где возможна:** READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ (по стандарту, но PostgreSQL защищает через SSI).

### Lost Update

Две транзакции читают одно значение, обе модифицируют, вторая перезаписывает результат первой.

```text
T1: SELECT balance FROM accounts WHERE id = 1;  -- 1000
T2: SELECT balance FROM accounts WHERE id = 1;  -- 1000
T1: UPDATE accounts SET balance = 1000 + 100;   -- 1100
T2: UPDATE accounts SET balance = 1000 - 200;   -- 800 (потеряно +100 от T1!)
```

**Защита:** `SELECT ... FOR UPDATE` (pessimistic) или optimistic locking с version-колонкой.

### Write Skew

Две транзакции читают пересекающиеся данные и на основе чтения делают записи, которые вместе нарушают инвариант.

```text
-- Инвариант: хотя бы один врач дежурит
T1: SELECT count(*) FROM on_call WHERE hospital = 'A';  -- 2
T2: SELECT count(*) FROM on_call WHERE hospital = 'A';  -- 2
T1: DELETE FROM on_call WHERE doctor = 'Alice' AND hospital = 'A';  -- count=1, ок
T2: DELETE FROM on_call WHERE doctor = 'Bob' AND hospital = 'A';    -- count=1, ок
-- Обе закоммитили → count=0, инвариант нарушен!
```

**Защита:** только SERIALIZABLE или явные блокировки.

## Уровни изоляции по SQL стандарту

| Уровень | Dirty Read | Non-Repeatable Read | Phantom Read |
|---------|-----------|-------------------|-------------|
| **READ UNCOMMITTED** | Возможна | Возможна | Возможна |
| **READ COMMITTED** | Невозможна | Возможна | Возможна |
| **REPEATABLE READ** | Невозможна | Невозможна | Возможна* |
| **SERIALIZABLE** | Невозможна | Невозможна | Невозможна |

*В PostgreSQL REPEATABLE READ реализован через snapshot isolation и фактически защищает от фантомов тоже.

**Команды:**

```sql
-- Установить для текущей транзакции
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Установить для сессии
SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Проверить текущий уровень (PostgreSQL)
SHOW transaction_isolation;
```

## Сравнение СУБД

| Аспект | PostgreSQL | MySQL (InnoDB) | Oracle |
|--------|-----------|----------------|--------|
| **По умолчанию** | READ COMMITTED | REPEATABLE READ | READ COMMITTED |
| **Механизм** | MVCC (xmin/xmax) | MVCC (undo log) | MVCC (undo tablespace) |
| **READ UNCOMMITTED** | = READ COMMITTED (игнорирует) | Поддерживает | = READ COMMITTED |
| **REPEATABLE READ** | Snapshot Isolation (защита от фантомов) | Gap Locks (предотвращает фантомы) | Не поддерживает |
| **SERIALIZABLE** | SSI (Serializable Snapshot Isolation) | Shared locks на все чтения | Snapshot-based |
| **Deadlock detection** | Есть (wait-for graph) | Есть | Есть |

**PostgreSQL не реализует READ UNCOMMITTED** — всегда используется минимум READ COMMITTED.

**MySQL InnoDB REPEATABLE READ** использует gap locks, что может вызывать больше блокировок, чем в PostgreSQL.

## MVCC vs блокировки

**MVCC (Multi-Version Concurrency Control):**
- Каждая транзакция видит свой **снимок** данных (snapshot)
- Читатели не блокируют писателей и наоборот
- Старые версии строк хранятся до завершения всех транзакций, которым они нужны
- PostgreSQL: xmin/xmax в заголовке строки + VACUUM для очистки
- MySQL: undo log + purge thread

**Блокировки (Pessimistic):**
- `SELECT ... FOR UPDATE` — эксклюзивная блокировка строки
- `SELECT ... FOR SHARE` — разделяемая блокировка (позволяет другим читать, но не писать)
- `SELECT ... FOR UPDATE NOWAIT` — не ждать, вернуть ошибку если заблокировано
- `SELECT ... FOR UPDATE SKIP LOCKED` — пропустить заблокированные строки (очереди задач)

```sql
-- Заблокировать строку для обновления
BEGIN;
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
COMMIT;

-- Очередь задач: взять незаблокированную задачу
SELECT * FROM tasks
WHERE status = 'pending'
ORDER BY created_at
LIMIT 1
FOR UPDATE SKIP LOCKED;
```

## Optimistic vs Pessimistic Locking

| Аспект | Optimistic | Pessimistic |
|--------|-----------|------------|
| **Механизм** | version/timestamp колонка | `SELECT ... FOR UPDATE` |
| **Блокировки** | Нет (проверка при коммите) | Явные (держатся до коммита) |
| **Конфликт** | Exception при коммите | Ожидание или NOWAIT |
| **Когда лучше** | Редкие конфликты, высокий read | Частые конфликты, критичные данные |
| **JPA** | `@Version` поле | `LockModeType.PESSIMISTIC_WRITE` |

```sql
-- Optimistic: проверяем version при UPDATE
UPDATE accounts SET balance = 900, version = 2
WHERE id = 1 AND version = 1;
-- Если affected_rows = 0 → конфликт, повторить

-- Pessimistic: блокируем строку заранее
SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
UPDATE accounts SET balance = 900 WHERE id = 1;
```

## Deadlock

Deadlock — циклическая зависимость блокировок.

```text
T1: UPDATE accounts SET balance = 0 WHERE id = 1;  -- lock row 1
T2: UPDATE accounts SET balance = 0 WHERE id = 2;  -- lock row 2
T1: UPDATE accounts SET balance = 0 WHERE id = 2;  -- ждёт T2
T2: UPDATE accounts SET balance = 0 WHERE id = 1;  -- ждёт T1 → DEADLOCK
```

**Как СУБД обрабатывает:** обнаруживает цикл (wait-for graph), откатывает одну из транзакций.

**Как предотвратить:**
1. Блокировать строки в одном порядке (по ID)
2. Держать транзакции короткими
3. Использовать `NOWAIT` / `SKIP LOCKED` где возможно
4. Не делать `SELECT ... FOR UPDATE` на большие диапазоны

## Spring @Transactional

```java
// Дефолтный уровень изоляции (зависит от СУБД)
@Transactional
public void transfer(long from, long to, BigDecimal amount) { }

// Явный уровень изоляции
@Transactional(isolation = Isolation.REPEATABLE_READ)
public Report generateReport() { }

// Только чтение (подсказка оптимизатору, может использовать read-only реплику)
@Transactional(readOnly = true)
public List<Account> findAll() { }

// Rollback на checked exception (по умолчанию — только unchecked)
@Transactional(rollbackFor = InsufficientFundsException.class)
public void withdraw(long id, BigDecimal amount) throws InsufficientFundsException { }

// Timeout (в секундах)
@Transactional(timeout = 5)
public void longOperation() { }
```

**Propagation (наиболее важные):**

| Propagation | Поведение |
|-------------|-----------|
| `REQUIRED` (default) | Использовать текущую транзакцию или создать новую |
| `REQUIRES_NEW` | Всегда создавать новую, приостановить текущую |
| `NESTED` | Вложенная через savepoint (откат не затрагивает внешнюю) |
| `MANDATORY` | Требовать существующую, иначе exception |
| `NOT_SUPPORTED` | Выполнять без транзакции, приостановить текущую |

**Частая ошибка:** `@Transactional` не работает при вызове метода из того же класса (self-invocation), т.к. Spring AOP проксирует вызовы только извне.

## Практические рекомендации

1. **READ COMMITTED для большинства OLTP** — баланс между безопасностью и производительностью
2. **REPEATABLE READ для отчётов** — консистентный снимок данных
3. **SERIALIZABLE только когда необходимо** — серьёзные накладные расходы
4. **Короткие транзакции** — длинные транзакции держат блокировки и мешают VACUUM
5. **Не мешайте DDL и DML** — `ALTER TABLE` берёт `ACCESS EXCLUSIVE` lock
6. **Retry при serialization failure** — SERIALIZABLE и REPEATABLE READ могут откатить транзакцию
7. **`FOR UPDATE` вместо read-modify-write** — предотвращает lost update
8. **`SKIP LOCKED` для очередей** — вместо собственных механизмов блокировки
9. **`@Version` для optimistic locking в JPA** — минимальные блокировки при редких конфликтах
10. **Мониторить `pg_stat_activity` и `pg_locks`** — находить долгие транзакции и блокировки

## См. также

- [[sql-basics|SQL: Основы]]
