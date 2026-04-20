---
title: "Вопросы на собеседовании: SQL"
description: "Краткие ответы по SQL: DDL/DML, ограничения, индексы, JOIN, оконные функции, транзакции, нормализация, оптимизация запросов, JDBC/JPA."
tags:
  - interview
  - databases
  - sql-interview
aliases:
  - "SQL"
  - "SQL interview"
  - "SQL собеседование"
  - "Базы данных SQL"
  - "Реляционные БД"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `SQL`

Краткие ответы по `SQL`: `DDL`/`DML`, ограничения, индексы, `JOIN`, оконные функции, транзакции, нормализация, оптимизация запросов, `JDBC`/`JPA`.

Краткое введение: **SQL** — основа работы с реляционными БД. На собеседовании проверяют знание `DDL`/`DML`, индексов, транзакций, оконных функций, нормализации, оптимизации запросов и отличий диалектов (`PostgreSQL`, `MySQL` и др.). Также часто спрашивают про взаимодействие из Java через `JDBC`, `JPA` и `Spring Data`.

## Полезные ссылки

### Официальная документация

- [SQL Tutorial (PostgreSQL)](https://www.postgresql.org/docs/current/tutorial.html) — официальный туториал PostgreSQL
- [SQL Standard (Wikipedia)](https://en.wikipedia.org/wiki/SQL) — стандарт SQL
- [PostgreSQL Window Functions](https://www.postgresql.org/docs/current/tutorial-window.html) — оконные функции в PostgreSQL
- [PostgreSQL CTEs](https://www.postgresql.org/docs/current/queries-with.html) — Common Table Expressions

### Статьи Baeldung

- [Types of SQL Joins with Java Examples](https://www.baeldung.com/sql-joins) — типы JOIN с примерами
- [Transaction Propagation and Isolation in Spring @Transactional](https://www.baeldung.com/spring-transactional-propagation-isolation) — транзакции в Spring
- [Defining Indexes in JPA](https://www.baeldung.com/jpa-indexes) — индексы в JPA
- [A Comparison Between JPA and JDBC](https://www.baeldung.com/jpa-vs-jdbc) — сравнение JPA и JDBC
- [Why Window Functions Cannot Be Used in WHERE Clause](https://www.baeldung.com/sql/window-functions-condition-where-clauses) — ограничения оконных функций
- [Database Sharding vs. Partitioning](https://www.baeldung.com/cs/database-sharding-vs-partitioning) — шардирование vs партиционирование

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы SQL и типы операторов**
- [Q1. Что такое SQL и какие подмножества языка существуют?](#q1-что-такое-sql-и-какие-подмножества-языка-существуют)
- [Q2. (!) Какие ограничения на целостность данных существуют в SQL?](#q2--какие-ограничения-на-целостность-данных-существуют-в-sql)
- [Q3. (!) В чём разница между PRIMARY KEY и UNIQUE?](#q3--в-чём-разница-primary-key-и-unique)
- [Q4. (!) Может ли FOREIGN KEY равняться NULL?](#q4--может-ли-foreign-key-равняться-null)
- [Q5. Что означает NULL в SQL и как с ним работать?](#q5-что-означает-null-в-sql-и-как-с-ним-работать)
- [Q6. В чём разница между DATETIME и TIMESTAMP?](#q6-в-чём-разница-между-datetime-и-timestamp)

**Нормализация и проектирование**
- [Q7. (!) Что такое нормализация и какие нормальные формы существуют?](#q7--что-такое-нормализация-и-какие-нормальные-формы-существуют)
- [Q8. Что такое денормализация и когда она оправдана?](#q8-что-такое-денормализация-и-когда-она-оправдана)

**Объекты БД**
- [Q9. Что такое View и Materialized View?](#q9-что-такое-view-и-materialized-view)
- [Q10. Что такое Stored Procedure и Function?](#q10-что-такое-stored-procedure-и-function)
- [Q11. Что такое Trigger?](#q11-что-такое-trigger)
- [Q12. Что такое Temporary table?](#q12-что-такое-temporary-table)
- [Q13. Что такое Sequence?](#q13-что-такое-sequence)

**SELECT и операции с данными**
- [Q14. Порядок выполнения SELECT-запроса](#q14-порядок-выполнения-select-запроса)
- [Q15. (!) В чём разница между DELETE, TRUNCATE и DROP?](#q15--в-чём-разница-между-delete-truncate-и-drop)
- [Q16. (!) Что такое MERGE (UPSERT)?](#q16--что-такое-merge-upsert)
- [Q17. В чём разница между IN, EXISTS и JOIN для фильтрации?](#q17-в-чём-разница-между-in-exists-и-join-для-фильтрации)
- [Q18. Что такое CASE-выражение?](#q18-что-такое-case-выражение)
- [Q19. (!) Что такое CTE (Common Table Expression)?](#q19--что-такое-cte-common-table-expression)
- [Q20. Что такое RETURNING?](#q20-что-такое-returning)

**JOIN**
- [Q21. (!) Какие существуют типы JOIN?](#q21--какие-существуют-типы-join)
- [Q22. В чём разница между WHERE и ON в JOIN?](#q22-в-чём-разница-между-where-и-on-в-join)
- [Q23. (!) Что лучше — JOIN или подзапросы?](#q23--что-лучше--join-или-подзапросы)
- [Q24. Что такое UNION и UNION ALL?](#q24-что-такое-union-и-union-all)
- [Q25. Что такое CROSS JOIN и когда он нужен?](#q25-что-такое-cross-join-и-когда-он-нужен)
- [Q26. Что такое SELF JOIN?](#q26-что-такое-self-join)

**GROUP BY и агрегация**
- [Q27. Что такое GROUP BY и HAVING?](#q27-что-такое-group-by-и-having)
- [Q28. В чём разница между GROUP BY и DISTINCT?](#q28-в-чём-разница-между-group-by-и-distinct)
- [Q29. В чём разница между COUNT(*), COUNT(column) и COUNT(DISTINCT column)?](#q29-в-чём-разница-между-count-countcolumn-и-countdistinct-column)

**Оконные функции**
- [Q30. (!) Что такое оконные функции (Window Functions)?](#q30--что-такое-оконные-функции-window-functions)
- [Q31. (!) В чём разница между ROW_NUMBER, RANK и DENSE_RANK?](#q31--в-чём-разница-между-row_number-rank-и-dense_rank)
- [Q32. Что такое LAG, LEAD и другие навигационные функции?](#q32-что-такое-lag-lead-и-другие-навигационные-функции)
- [Q33. Почему оконные функции нельзя использовать в WHERE?](#q33-почему-оконные-функции-нельзя-использовать-в-where)

**Индексы**
- [Q34. (!) Что такое индекс и как он устроен?](#q34--что-такое-индекс-и-как-он-устроен)
- [Q35. (!) В чём разница между Clustered и Non-clustered индексами?](#q35--в-чём-разница-между-clustered-и-non-clustered-индексами)
- [Q36. Что такое Covering Index и Partial Index?](#q36-что-такое-covering-index-и-partial-index)
- [Q37. Когда индекс НЕ помогает?](#q37-когда-индекс-не-помогает)

**Транзакции и конкурентный доступ**
- [Q38. (!) Что такое ACID?](#q38--что-такое-acid)
- [Q39. (!) Какие уровни изоляции транзакций существуют?](#q39--какие-уровни-изоляции-транзакций-существуют)
- [Q40. Что такое deadlock и как его избежать?](#q40-что-такое-deadlock-и-как-его-избежать)
- [Q41. В чём разница между оптимистичной и пессимистичной блокировкой?](#q41-в-чём-разница-между-оптимистичной-и-пессимистичной-блокировкой)

**Оптимизация запросов**
- [Q42. (!) Что такое EXPLAIN и как читать план выполнения?](#q42--что-такое-explain-и-как-читать-план-выполнения)
- [Q43. Какие основные стратегии оптимизации SQL-запросов существуют?](#q43-какие-основные-стратегии-оптимизации-sql-запросов-существуют)
- [Q44. Что такое N+1 проблема?](#q44-что-такое-n1-проблема)

**SQL в Java (JDBC, JPA, Spring Data)**
- [Q45. (!) В чём разница между JDBC и JPA?](#q45--в-чём-разница-между-jdbc-и-jpa)
- [Q46. Как определить индексы в JPA?](#q46-как-определить-индексы-в-jpa)
- [Q47. Как управлять транзакциями в Spring?](#q47-как-управлять-транзакциями-в-spring)

**Продвинутые темы**
- [Q48. (!) Что такое рекурсивные CTE и когда их применять?](#q48--что-такое-рекурсивные-cte-и-когда-их-применять)
- [Q49. (!) Как читать и интерпретировать план выполнения (EXPLAIN)?](#q49--как-читать-и-интерпретировать-план-выполнения-explain)
- [Q50. Что такое партиционирование таблиц и зачем оно нужно?](#q50-что-такое-партиционирование-таблиц-и-зачем-оно-нужно)
- [Q51. (!) Какие виды индексов существуют в PostgreSQL?](#q51--какие-виды-индексов-существуют-в-postgresql)
- [Q52. Что такое VACUUM и ANALYZE в PostgreSQL?](#q52-что-такое-vacuum-и-analyze-в-postgresql)
- [Q53. Как работает MVCC (Multi-Version Concurrency Control)?](#q53-как-работает-mvcc-multi-version-concurrency-control)

---

## Q1. Что такое `SQL` и какие подмножества языка существуют?

`SQL` (`Structured Query Language`) — стандартный язык для работы с реляционными базами данных. Он разделяется на несколько подмножеств:

| Подмножество | Назначение | Основные операторы |
|---|---|---|
| `DDL` (Data Definition Language) | Определение структуры | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| `DML` (Data Manipulation Language) | Манипуляция данными | `SELECT`, `INSERT`, `UPDATE`, `DELETE` |
| `DCL` (Data Control Language) | Управление доступом | `GRANT`, `REVOKE` |
| `TCL` (Transaction Control Language) | Управление транзакциями | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

```sql
-- DDL: создание таблицы
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT REFERENCES departments(id),
    salary NUMERIC(10, 2) CHECK (salary > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DML: вставка данных
INSERT INTO employees (name, department_id, salary)
VALUES ('Иванов', 1, 150000.00);

-- DCL: выдача прав
GRANT SELECT, INSERT ON employees TO app_user;

-- TCL: управление транзакцией
BEGIN;
UPDATE employees SET salary = salary * 1.1 WHERE department_id = 1;
SAVEPOINT after_raise;
-- если что-то пошло не так:
ROLLBACK TO after_raise;
COMMIT;
```

## Q2. (!) Какие ограничения на целостность данных существуют в `SQL`?

В `SQL` существуют следующие ограничения (`constraints`), гарантирующие корректность данных:

| Ограничение | Назначение |
|---|---|
| `PRIMARY KEY` | Уникальный идентификатор строки. Не допускает `NULL` и дубликатов |
| `FOREIGN KEY` | Ссылочная целостность — связь с `PK` другой таблицы |
| `UNIQUE` | Уникальность значений (допускает `NULL`) |
| `NOT NULL` | Запрет пустых значений |
| `CHECK` | Произвольное условие на значение |
| `DEFAULT` | Значение по умолчанию, если не указано явно |
| `EXCLUDE` | Запрет пересекающихся диапазонов (PostgreSQL) |

```sql
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,                         -- PK
    customer_id INT NOT NULL                       -- NOT NULL
        REFERENCES customers(id)                   -- FK
        ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'NEW'               -- DEFAULT
        CHECK (status IN ('NEW','PAID','SHIPPED')),-- CHECK
    order_number VARCHAR(50) UNIQUE,               -- UNIQUE
    valid_from TSRANGE,
    EXCLUDE USING GIST (valid_from WITH &&)        -- EXCLUDE (PostgreSQL)
);
```

Стратегии ссылочных действий `FOREIGN KEY`: `CASCADE`, `SET NULL`, `SET DEFAULT`, `RESTRICT`, `NO ACTION`.

## Q3. (!) В чём разница `PRIMARY KEY` и `UNIQUE`?

| Характеристика | `PRIMARY KEY` | `UNIQUE` |
|---|---|---|
| Количество на таблицу | Один | Неограничено |
| `NULL` | Не допускает | Допускает (обычно один `NULL`) |
| Кластеризация | Создаёт кластерный индекс (SQL Server, MySQL InnoDB) | Некластерный индекс |
| Ссылка `FK` | Может быть целью `FOREIGN KEY` | Тоже может быть целью `FK` |
| Назначение | Идентификация строки | Бизнес-уникальность (email, ИНН) |

> **Замечание**: в `PostgreSQL` ограничение `UNIQUE` допускает несколько `NULL`-значений (они считаются различными). В `SQL Server` — только один `NULL` по умолчанию.

## Q4. (!) Может ли `FOREIGN KEY` равняться `NULL`?

Да, столбец с ограничением `FOREIGN KEY` может содержать `NULL`, если на нём не задано `NOT NULL`. Значение `NULL` означает «связь не установлена» — строка не ссылается ни на одну запись в родительской таблице.

```sql
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    manager_id INT REFERENCES employees(id) -- может быть NULL (CEO)
);

INSERT INTO employees (name, manager_id) VALUES ('CEO', NULL);     -- OK
INSERT INTO employees (name, manager_id) VALUES ('Manager', 1);   -- OK
INSERT INTO employees (name, manager_id) VALUES ('Dev', 999);     -- ОШИБКА: нет id=999
```

## Q5. Что означает `NULL` в `SQL` и как с ним работать?

`NULL` — это маркер отсутствия значения. Он не равен ни нулю, ни пустой строке. Основные правила:

- Любое сравнение с `NULL` даёт `UNKNOWN` (не `TRUE` и не `FALSE`)
- Для проверки используются `IS NULL` / `IS NOT NULL`
- Арифметика с `NULL` даёт `NULL`: `5 + NULL = NULL`
- Агрегатные функции (`SUM`, `AVG`, `COUNT(column)`) игнорируют `NULL`

```sql
-- Неправильно:
SELECT * FROM users WHERE email = NULL;       -- всегда пусто!

-- Правильно:
SELECT * FROM users WHERE email IS NULL;

-- Обработка NULL:
SELECT
    name,
    COALESCE(phone, 'не указан') AS phone,   -- замена NULL
    NULLIF(discount, 0) AS discount            -- 0 → NULL
FROM customers;
```

Трёхзначная логика `NULL`:

| `A` | `B` | `A AND B` | `A OR B` |
|---|---|---|---|
| TRUE | NULL | UNKNOWN | TRUE |
| FALSE | NULL | FALSE | UNKNOWN |
| NULL | NULL | UNKNOWN | UNKNOWN |

## Q6. В чём разница между `DATETIME` и `TIMESTAMP`?

| Характеристика | `DATETIME` (MySQL) | `TIMESTAMP` (MySQL) | `TIMESTAMPTZ` (PostgreSQL) |
|---|---|---|---|
| Диапазон | 1000-01-01 — 9999-12-31 | 1970-01-01 — 2038-01-19 | 4713 BC — 294276 AD |
| Хранение | Как есть (без зоны) | Конвертируется в UTC | Конвертируется в UTC |
| Размер | 8 байт | 4 байта | 8 байт |
| Часовой пояс | Не учитывает | Автоматически конвертирует | Автоматически конвертирует |

> **Рекомендация**: в `PostgreSQL` используйте `TIMESTAMPTZ` для корректной работы с часовыми поясами. Подробнее о работе с датами в Java — в [[hibernate-interview|вопросах по Hibernate]].

## Q7. (!) Что такое нормализация и какие нормальные формы существуют?

**Нормализация** — процесс организации данных, минимизирующий избыточность и аномалии (вставки, обновления, удаления).

```mermaid
graph TD
    UNF["Ненормализованная форма<br/>(UNF)"] --> 1NF["1NF: Атомарные значения"]
    1NF --> 2NF["2NF: Нет частичных<br/>зависимостей"]
    2NF --> 3NF["3NF: Нет транзитивных<br/>зависимостей"]
    3NF --> BCNF["BCNF: Каждый детерминант<br/>— кандидат-ключ"]
    BCNF --> 4NF["4NF: Нет многозначных<br/>зависимостей"]
    4NF --> 5NF["5NF: Нет зависимостей<br/>соединения"]
```

| Нормальная форма | Требование | Пример нарушения |
|---|---|---|
| **1NF** | Атомарные значения, нет повторяющихся групп | `phones = '111,222'` в одном столбце |
| **2NF** | 1NF + нет частичных зависимостей от составного PK | Имя студента зависит только от `student_id`, а не от `(student_id, course_id)` |
| **3NF** | 2NF + нет транзитивных зависимостей | `city → country` через `zip_code` |
| **BCNF** | Каждый детерминант — кандидат-ключ | Редкие случаи с перекрывающимися кандидат-ключами |

На практике большинство OLTP-систем нормализуют до **3NF** или **BCNF**.

## Q8. Что такое денормализация и когда она оправдана?

**Денормализация** — намеренное внесение избыточности для повышения скорости чтения:

- **Дублирование столбцов** — добавление `customer_name` в таблицу `orders`, чтобы избежать `JOIN`
- **Предвычисленные агрегаты** — столбец `total_orders` в таблице `customers`
- **Материализованные представления** — денормализованные снимки данных

Денормализация оправдана:
- В **OLAP/DWH** (аналитические хранилища, схемы «звезда» и «снежинка»)
- Когда `JOIN` по большому числу таблиц создаёт узкое место
- При необходимости крайне низкого `latency` на чтение

Цена — усложнение записи и риск рассинхронизации данных.

## Q9. Что такое `View` и `Materialized View`?

**View** (представление) — виртуальная таблица, определяемая запросом. Данные не хранятся физически, запрос выполняется каждый раз:

```sql
CREATE VIEW active_employees AS
SELECT e.id, e.name, d.name AS department
FROM employees e
JOIN departments d ON e.department_id = d.id
WHERE e.status = 'ACTIVE';

-- Использование как обычная таблица:
SELECT * FROM active_employees WHERE department = 'IT';
```

**Materialized View** — представление, результат которого хранится физически и обновляется по команде:

```sql
-- PostgreSQL
CREATE MATERIALIZED VIEW monthly_sales AS
SELECT date_trunc('month', order_date) AS month,
       SUM(total) AS revenue,
       COUNT(*) AS order_count
FROM orders
GROUP BY 1;

-- Обновление:
REFRESH MATERIALIZED VIEW CONCURRENTLY monthly_sales;
```

| | `View` | `Materialized View` |
|---|---|---|
| Хранит данные | Нет | Да |
| Скорость чтения | Зависит от запроса | Быстро (данные готовы) |
| Актуальность | Всегда актуально | Нужен `REFRESH` |
| Поддержка индексов | Нет | Да |

## Q10. Что такое `Stored Procedure` и `Function`?

**Stored Procedure** — набор скомпилированных SQL-инструкций на сервере БД. **Function** — то же, но обязательно возвращает значение и может использоваться в `SELECT`.

```sql
-- PostgreSQL: функция
CREATE OR REPLACE FUNCTION get_employee_count(dept_id INT)
RETURNS INT AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM employees WHERE department_id = dept_id);
END;
$$ LANGUAGE plpgsql;

SELECT get_employee_count(1);

-- PostgreSQL: процедура (с COMMIT/ROLLBACK)
CREATE OR REPLACE PROCEDURE transfer_employee(emp_id INT, new_dept INT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE employees SET department_id = new_dept WHERE id = emp_id;
    INSERT INTO audit_log (action, employee_id) VALUES ('TRANSFER', emp_id);
    COMMIT;
END;
$$;

CALL transfer_employee(42, 3);
```

| | Procedure | Function |
|---|---|---|
| Возвращает значение | Не обязательно | Обязательно |
| Использование в `SELECT` | Нет | Да |
| `COMMIT`/`ROLLBACK` внутри | Да (PostgreSQL 11+) | Нет |

## Q11. Что такое `Trigger`?

**Триггер** — объект БД, автоматически выполняющий код при `INSERT`, `UPDATE` или `DELETE`.

```sql
-- Аудит изменений зарплаты
CREATE OR REPLACE FUNCTION log_salary_change()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO salary_audit (employee_id, old_salary, new_salary, changed_at)
    VALUES (OLD.id, OLD.salary, NEW.salary, NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER salary_change_trigger
    BEFORE UPDATE OF salary ON employees
    FOR EACH ROW
    WHEN (OLD.salary IS DISTINCT FROM NEW.salary)
    EXECUTE FUNCTION log_salary_change();
```

Типы триггеров: `BEFORE` / `AFTER` / `INSTEAD OF` (для `View`); `FOR EACH ROW` / `FOR EACH STATEMENT`.

> **Совет**: избегайте сложной бизнес-логики в триггерах — это усложняет отладку и может вызвать каскадные проблемы. Подробнее о событийном подходе — в [[database-architecture-interview|вопросах по архитектуре БД]].

## Q12. Что такое `Temporary` table?

Временная таблица существует только в пределах текущей сессии (или транзакции) и автоматически удаляется:

```sql
-- PostgreSQL
CREATE TEMP TABLE temp_report (
    department VARCHAR(100),
    total_salary NUMERIC
);

INSERT INTO temp_report
SELECT d.name, SUM(e.salary)
FROM employees e JOIN departments d ON e.department_id = d.id
GROUP BY d.name;

-- После отключения сессии таблица исчезнет
```

В `PostgreSQL` используется `CREATE TEMP TABLE`, в `MySQL` — `CREATE TEMPORARY TABLE`, в `SQL Server` — префикс `#` (локальная) или `##` (глобальная).

## Q13. Что такое `Sequence`?

**Sequence** — объект БД, генерирующий уникальные числовые последовательности. В отличие от `AUTO_INCREMENT`, `SERIAL` и `IDENTITY` — это самостоятельный объект:

```sql
CREATE SEQUENCE order_seq START 1000 INCREMENT 1;

-- Получение следующего значения:
SELECT nextval('order_seq');  -- 1000, 1001, 1002...

-- Использование в INSERT:
INSERT INTO orders (id, customer_id) VALUES (nextval('order_seq'), 42);
```

`Sequence` не откатывается при `ROLLBACK` — это by design, чтобы не блокировать параллельные транзакции.

## Q14. Порядок выполнения `SELECT`-запроса

Логический порядок обработки отличается от порядка записи:

```mermaid
graph TD
    A["1. FROM / JOIN"] --> B["2. WHERE"]
    B --> C["3. GROUP BY"]
    C --> D["4. HAVING"]
    D --> E["5. SELECT / агрегаты"]
    E --> F["6. DISTINCT"]
    F --> G["7. ORDER BY"]
    G --> H["8. LIMIT / OFFSET"]
```

```sql
SELECT department, COUNT(*) AS cnt    -- 5. Вычисление выражений
FROM employees                         -- 1. Источник данных
JOIN departments ON ...                -- 1. Соединение
WHERE salary > 50000                   -- 2. Фильтрация строк
GROUP BY department                    -- 3. Группировка
HAVING COUNT(*) > 5                   -- 4. Фильтрация групп
ORDER BY cnt DESC                      -- 7. Сортировка
LIMIT 10;                              -- 8. Ограничение
```

Понимание этого порядка объясняет, почему нельзя использовать алиас из `SELECT` в `WHERE`, но можно в `ORDER BY`.

## Q15. (!) В чём разница между `DELETE`, `TRUNCATE` и `DROP`?

| | `DELETE` | `TRUNCATE` | `DROP` |
|---|---|---|---|
| Что делает | Удаляет строки | Удаляет все строки | Удаляет таблицу целиком |
| `WHERE` | Да | Нет | Нет |
| Откат (`ROLLBACK`) | Да | Зависит от СУБД | Зависит от СУБД |
| Триггеры | Срабатывают | Не срабатывают | Не срабатывают |
| Сброс `IDENTITY` | Нет | Да | — |
| Скорость | Медленнее (построчно) | Быстрее | Быстрее |
| Подмножество | `DML` | `DDL` | `DDL` |

```sql
DELETE FROM orders WHERE status = 'CANCELLED';  -- удаляет часть строк
TRUNCATE TABLE temp_data;                        -- быстрая очистка
DROP TABLE IF EXISTS old_archive;                -- удаляет таблицу
```

> В `PostgreSQL` `TRUNCATE` можно откатить внутри транзакции, в `MySQL` (InnoDB) — нельзя.

## Q16. (!) Что такое `MERGE` (`UPSERT`)?

`MERGE` объединяет `INSERT`, `UPDATE` и `DELETE` в одном атомарном операторе. В `PostgreSQL` используется альтернативный синтаксис `INSERT ... ON CONFLICT`:

```sql
-- Стандартный SQL (SQL Server, Oracle):
MERGE INTO target_table t
USING source_table s ON (t.id = s.id)
WHEN MATCHED THEN
    UPDATE SET t.name = s.name, t.updated_at = NOW()
WHEN NOT MATCHED THEN
    INSERT (id, name) VALUES (s.id, s.name);

-- PostgreSQL (UPSERT):
INSERT INTO products (sku, name, price)
VALUES ('ABC-123', 'Widget', 29.99)
ON CONFLICT (sku) DO UPDATE
SET name = EXCLUDED.name,
    price = EXCLUDED.price,
    updated_at = NOW();
```

В Java с `JPA` / `Spring Data` upsert реализуется через `saveAndFlush()` или нативный запрос:

```java
@Modifying
@Query(value = """
    INSERT INTO products (sku, name, price)
    VALUES (:sku, :name, :price)
    ON CONFLICT (sku) DO UPDATE SET name = :name, price = :price
    """, nativeQuery = true)
void upsertProduct(@Param("sku") String sku,
                    @Param("name") String name,
                    @Param("price") BigDecimal price);
```

## Q17. В чём разница между `IN`, `EXISTS` и `JOIN` для фильтрации?

```sql
-- IN: простой список или подзапрос
SELECT * FROM orders WHERE customer_id IN (1, 2, 3);
SELECT * FROM orders WHERE customer_id IN (SELECT id FROM vip_customers);

-- EXISTS: проверка существования
SELECT * FROM orders o
WHERE EXISTS (SELECT 1 FROM vip_customers v WHERE v.id = o.customer_id);

-- JOIN: объединение
SELECT o.* FROM orders o
JOIN vip_customers v ON v.id = o.customer_id;
```

| | `IN` | `EXISTS` | `JOIN` |
|---|---|---|---|
| Семантика | Сравнение со списком | Проверка существования | Объединение наборов |
| `NULL`-безопасность | Проблемы с `NOT IN` + `NULL` | Безопасен | Безопасен |
| Большой подзапрос | Медленнее | Быстрее (ранний выход) | Зависит от индексов |
| Дубликаты в результате | Нет | Нет | Возможны |

> **Важно**: `NOT IN` с подзапросом, который может вернуть `NULL`, всегда даёт пустой результат! Используйте `NOT EXISTS`.

## Q18. Что такое `CASE`-выражение?

`CASE` — условное выражение, аналог `if-else` в SQL:

```sql
SELECT name, salary,
    CASE
        WHEN salary >= 200000 THEN 'Senior'
        WHEN salary >= 100000 THEN 'Middle'
        ELSE 'Junior'
    END AS grade
FROM employees;

-- Простая форма:
SELECT order_id,
    CASE status
        WHEN 'NEW'     THEN 'Новый'
        WHEN 'PAID'    THEN 'Оплачен'
        WHEN 'SHIPPED' THEN 'Отправлен'
    END AS status_name
FROM orders;
```

## Q19. (!) Что такое `CTE` (`Common Table Expression`)?

`CTE` — именованный временный набор данных в рамках одного запроса. Определяется через `WITH`:

```sql
-- Обычный CTE:
WITH department_stats AS (
    SELECT department_id,
           AVG(salary) AS avg_salary,
           COUNT(*) AS emp_count
    FROM employees
    GROUP BY department_id
)
SELECT d.name, ds.avg_salary, ds.emp_count
FROM departments d
JOIN department_stats ds ON d.id = ds.department_id
WHERE ds.emp_count > 5;

-- Рекурсивный CTE (иерархия):
WITH RECURSIVE org_tree AS (
    SELECT id, name, manager_id, 1 AS level
    FROM employees WHERE manager_id IS NULL
    UNION ALL
    SELECT e.id, e.name, e.manager_id, ot.level + 1
    FROM employees e
    JOIN org_tree ot ON e.manager_id = ot.id
)
SELECT * FROM org_tree ORDER BY level, name;
```

`CTE` улучшает читаемость сложных запросов и позволяет реализовать рекурсию (деревья, графы).

## Q20. Что такое `RETURNING`?

`RETURNING` позволяет получить данные изменённых строк без дополнительного `SELECT`:

```sql
-- Получить id вставленной записи:
INSERT INTO users (name, email)
VALUES ('Иванов', 'ivanov@mail.ru')
RETURNING id, created_at;

-- Получить старые и новые значения при UPDATE:
UPDATE products SET price = price * 1.1
WHERE category = 'electronics'
RETURNING id, name, price;

-- Удаление с возвратом:
DELETE FROM sessions WHERE expires_at < NOW()
RETURNING user_id;
```

Поддерживается в `PostgreSQL`, `Oracle`, `SQL Server` (через `OUTPUT`). **Не поддерживается в MySQL**.

## Q21. (!) Какие существуют типы `JOIN`?

```mermaid
graph LR
    subgraph "INNER JOIN"
        direction LR
        A1((A ∩ B))
    end
    subgraph "LEFT JOIN"
        direction LR
        A2((A)) --- A3((A ∩ B))
    end
    subgraph "RIGHT JOIN"
        direction LR
        A4((A ∩ B)) --- A5((B))
    end
    subgraph "FULL OUTER JOIN"
        direction LR
        A6((A)) --- A7((A ∩ B)) --- A8((B))
    end
```

```sql
-- Пример данных:
-- employees: (1, 'Иванов', dept_id=1), (2, 'Петров', dept_id=2), (3, 'Сидоров', dept_id=NULL)
-- departments: (1, 'IT'), (2, 'HR'), (4, 'Finance')

-- INNER JOIN: только совпадения (2 строки: Иванов+IT, Петров+HR)
SELECT e.name, d.name FROM employees e
INNER JOIN departments d ON e.department_id = d.id;

-- LEFT JOIN: все из левой + совпадения (3 строки, Сидоров с NULL)
SELECT e.name, d.name FROM employees e
LEFT JOIN departments d ON e.department_id = d.id;

-- RIGHT JOIN: все из правой + совпадения (3 строки, Finance с NULL)
SELECT e.name, d.name FROM employees e
RIGHT JOIN departments d ON e.department_id = d.id;

-- FULL OUTER JOIN: всё из обеих таблиц (4 строки)
SELECT e.name, d.name FROM employees e
FULL OUTER JOIN departments d ON e.department_id = d.id;

-- CROSS JOIN: декартово произведение (3×3 = 9 строк)
SELECT e.name, d.name FROM employees e
CROSS JOIN departments d;
```

## Q22. В чём разница между `WHERE` и `ON` в `JOIN`?

Для `INNER JOIN` разницы нет — оптимизатор объединяет условия. Для `LEFT`/`RIGHT JOIN` разница принципиальна:

```sql
-- ON: фильтрация при соединении (NULL-строки остаются)
SELECT e.name, d.name
FROM employees e
LEFT JOIN departments d ON d.id = e.department_id AND d.active = true;
-- Сотрудники без отдела или с неактивным отделом → d.name = NULL

-- WHERE: фильтрация после соединения (превращает LEFT JOIN в INNER)
SELECT e.name, d.name
FROM employees e
LEFT JOIN departments d ON d.id = e.department_id
WHERE d.active = true;
-- Сотрудники без отдела ИСКЛЮЧЕНЫ
```

## Q23. (!) Что лучше — `JOIN` или подзапросы?

| Критерий | `JOIN` | Подзапрос |
|---|---|---|
| Читаемость | Лучше для простых связей | Лучше для изолированных вычислений |
| Производительность | Обычно эффективнее | Коррелированный — может быть O(n²) |
| Дубликаты | Возможны при 1:N | Нет (если в `WHERE`) |
| Оптимизатор | Хорошо оптимизирует | Может не «развернуть» подзапрос |

Современные оптимизаторы (`PostgreSQL`, `MySQL 8+`) часто преобразуют подзапросы в `JOIN`. Но **коррелированный подзапрос** (зависимый от внешней строки) может выполняться для каждой строки — это главное узкое место.

```sql
-- Коррелированный подзапрос (потенциально медленный):
SELECT e.name,
    (SELECT d.name FROM departments d WHERE d.id = e.department_id) AS dept
FROM employees e;

-- Лучше переписать как JOIN:
SELECT e.name, d.name AS dept
FROM employees e
LEFT JOIN departments d ON d.id = e.department_id;
```

## Q24. Что такое `UNION` и `UNION ALL`?

`UNION` объединяет результаты нескольких `SELECT` в один набор:

```sql
-- UNION: удаляет дубликаты (дороже — требует сортировку)
SELECT name, email FROM employees
UNION
SELECT name, email FROM contractors;

-- UNION ALL: сохраняет дубликаты (быстрее)
SELECT name, 'employee' AS type FROM employees
UNION ALL
SELECT name, 'contractor' AS type FROM contractors;
```

Требования: одинаковое количество столбцов, совместимые типы. Если дубликаты не важны — всегда используйте `UNION ALL`.

## Q25. Что такое `CROSS JOIN` и когда он нужен?

`CROSS JOIN` (декартово произведение) — каждая строка A комбинируется с каждой строкой B:

```sql
-- Генерация всех комбинаций (матрица размеров × цветов):
SELECT s.size_name, c.color_name
FROM sizes s CROSS JOIN colors c;

-- Генерация календаря:
SELECT d.date, h.hour
FROM generate_series('2026-01-01'::date, '2026-12-31'::date, '1 day') d(date)
CROSS JOIN generate_series(0, 23) h(hour);
```

Используется для генерации комбинаций, заполнения пропусков в данных, построения матриц.

## Q26. Что такое `SELF JOIN`?

`SELF JOIN` — соединение таблицы с самой собой. Используется для иерархий и сравнений внутри одной таблицы:

```sql
-- Иерархия сотрудников:
SELECT e.name AS employee, m.name AS manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;

-- Найти сотрудников с одинаковой зарплатой:
SELECT a.name, b.name, a.salary
FROM employees a
JOIN employees b ON a.salary = b.salary AND a.id < b.id;
```

## Q27. Что такое `GROUP BY` и `HAVING`?

`GROUP BY` группирует строки для агрегации. `HAVING` фильтрует группы (в отличие от `WHERE`, который фильтрует строки до группировки):

```sql
SELECT department,
       COUNT(*) AS emp_count,
       AVG(salary) AS avg_salary,
       MAX(salary) AS max_salary
FROM employees
WHERE status = 'ACTIVE'          -- фильтрация строк (до GROUP BY)
GROUP BY department
HAVING COUNT(*) > 3              -- фильтрация групп (после GROUP BY)
ORDER BY avg_salary DESC;
```

`GROUP BY` объединяет `NULL`-значения в одну группу. Все столбцы в `SELECT`, не входящие в агрегатные функции, должны быть в `GROUP BY`.

## Q28. В чём разница между `GROUP BY` и `DISTINCT`?

| | `GROUP BY` | `DISTINCT` |
|---|---|---|
| Назначение | Группировка + агрегация | Удаление дубликатов |
| Агрегатные функции | Да (`SUM`, `COUNT`, ...) | Нет |
| Производительность | Зависит от реализации | Аналогична `GROUP BY` без агрегатов |

```sql
-- Эквивалентные запросы:
SELECT DISTINCT department FROM employees;
SELECT department FROM employees GROUP BY department;

-- Но GROUP BY может больше:
SELECT department, COUNT(*) FROM employees GROUP BY department;
```

## Q29. В чём разница между `COUNT(*)`, `COUNT(column)` и `COUNT(DISTINCT column)`?

```sql
-- Пример: orders (id, customer_id, discount)
-- Строки: (1,10,5), (2,10,NULL), (3,20,10), (4,20,5)

SELECT
    COUNT(*) AS all_rows,                 -- 4 (все строки)
    COUNT(discount) AS non_null,          -- 3 (без NULL)
    COUNT(DISTINCT customer_id) AS unique_customers,  -- 2
    COUNT(DISTINCT discount) AS unique_discounts       -- 2 (5 и 10)
FROM orders;
```

## Q30. (!) Что такое оконные функции (`Window Functions`)?

Оконные функции выполняют вычисления над набором строк, связанных с текущей строкой, **без группировки** результата. Определяются через `OVER()`:

```sql
SELECT
    name,
    department,
    salary,
    -- Ранжирование внутри отдела:
    ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS rank_in_dept,
    -- Средняя зарплата по отделу (без GROUP BY!):
    AVG(salary) OVER (PARTITION BY department) AS dept_avg,
    -- Нарастающий итог:
    SUM(salary) OVER (ORDER BY hire_date) AS running_total,
    -- Скользящее среднее (3 строки):
    AVG(salary) OVER (ORDER BY hire_date ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS moving_avg
FROM employees;
```

```mermaid
graph LR
    A["OVER()"] --> B["PARTITION BY<br/>Деление на группы"]
    A --> C["ORDER BY<br/>Порядок внутри окна"]
    A --> D["ROWS/RANGE<br/>Рамка окна"]
```

Оконные функции не уменьшают количество строк — каждая строка сохраняется в результате, но получает вычисленное значение по «окну».

## Q31. (!) В чём разница между `ROW_NUMBER`, `RANK` и `DENSE_RANK`?

```sql
-- Данные: зарплаты 100, 100, 90, 80
SELECT name, salary,
    ROW_NUMBER() OVER (ORDER BY salary DESC) AS row_num,   -- 1, 2, 3, 4
    RANK()       OVER (ORDER BY salary DESC) AS rank,      -- 1, 1, 3, 4
    DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank -- 1, 1, 2, 3
FROM employees;
```

| Функция | Дубликаты | Пропуски | Типичный use-case |
|---|---|---|---|
| `ROW_NUMBER()` | Уникальный номер каждой строке | Нет | Пагинация, дедупликация |
| `RANK()` | Одинаковый ранг для одинаковых значений | Да (пропуск) | Рейтинги, top-N |
| `DENSE_RANK()` | Одинаковый ранг для одинаковых значений | Нет | Группировка по позициям |

Практический пример — дедупликация (оставить последнюю запись для каждого пользователя):

```sql
DELETE FROM user_events
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn
        FROM user_events
    ) sub WHERE rn > 1
);
```

## Q32. Что такое `LAG`, `LEAD` и другие навигационные функции?

```sql
SELECT
    order_date,
    revenue,
    LAG(revenue, 1) OVER (ORDER BY order_date)  AS prev_day_revenue,  -- предыдущая строка
    LEAD(revenue, 1) OVER (ORDER BY order_date) AS next_day_revenue,  -- следующая строка
    FIRST_VALUE(revenue) OVER (ORDER BY order_date) AS first_revenue, -- первое значение
    NTH_VALUE(revenue, 3) OVER (ORDER BY order_date) AS third_revenue -- N-е значение
FROM daily_sales;

-- Процент изменения по сравнению с предыдущим днём:
SELECT order_date, revenue,
    ROUND((revenue - LAG(revenue) OVER (ORDER BY order_date)) * 100.0
        / LAG(revenue) OVER (ORDER BY order_date), 2) AS pct_change
FROM daily_sales;
```

## Q33. Почему оконные функции нельзя использовать в `WHERE`?

Потому что `WHERE` выполняется **до** вычисления оконных функций (см. Q14 — порядок выполнения запроса). Решение — обернуть в подзапрос или `CTE`:

```sql
-- Ошибка:
SELECT * FROM employees
WHERE ROW_NUMBER() OVER (ORDER BY salary DESC) <= 10;  -- НЕ РАБОТАЕТ

-- Правильно (через CTE):
WITH ranked AS (
    SELECT *, ROW_NUMBER() OVER (ORDER BY salary DESC) AS rn
    FROM employees
)
SELECT * FROM ranked WHERE rn <= 10;
```

## Q34. (!) Что такое индекс и как он устроен?

**Индекс** — структура данных для ускорения поиска строк. Большинство СУБД используют **B+tree** — сбалансированное дерево, где данные хранятся только в листовых узлах:

```mermaid
graph TD
    R["Корень<br/>[30 | 60]"] --> L1["[10 | 20]"]
    R --> L2["[40 | 50]"]
    R --> L3["[70 | 80 | 90]"]
    L1 --> D1["→ строки 1-10"]
    L1 --> D2["→ строки 11-20"]
    L2 --> D3["→ строки 31-40"]
    L2 --> D4["→ строки 41-50"]
    L3 --> D5["→ строки 61-70"]
    L3 --> D6["→ строки 71-80"]
    L3 --> D7["→ строки 81-90"]
```

```sql
-- Создание индекса:
CREATE INDEX idx_emp_department ON employees (department_id);

-- Составной индекс (порядок столбцов важен!):
CREATE INDEX idx_emp_dept_salary ON employees (department_id, salary);

-- Уникальный индекс:
CREATE UNIQUE INDEX idx_users_email ON users (email);
```

Составной индекс `(A, B, C)` работает для запросов: `WHERE A`, `WHERE A AND B`, `WHERE A AND B AND C`, но **не** для `WHERE B` или `WHERE C` (правило «левого префикса»).

Типы индексов в `PostgreSQL`: `B-tree` (по умолчанию), `Hash`, `GiST`, `GIN` (полнотекстовый, JSONB), `BRIN` (для больших таблиц с монотонными данными).

## Q35. (!) В чём разница между `Clustered` и `Non-clustered` индексами?

| | Clustered | Non-clustered |
|---|---|---|
| Физический порядок данных | Определяет порядок строк на диске | Не влияет на порядок |
| Количество на таблицу | Один | Неограничено |
| Хранение | Данные в листьях B+tree | Указатели на строки |
| Скорость range-запросов | Быстрее (данные рядом) | Медленнее (random I/O) |

В `MySQL InnoDB` `PRIMARY KEY` = кластерный индекс. В `PostgreSQL` нет кластерных индексов в классическом смысле, но есть команда `CLUSTER` для физической перегруппировки.

```sql
-- PostgreSQL: физическая перегруппировка по индексу (разовая операция):
CLUSTER employees USING idx_emp_department;

-- MySQL InnoDB: PK — это кластерный индекс, вторичные индексы
-- хранят значение PK → поэтому компактный PK (INT) лучше UUID
```

> Подробнее о хранении данных — в [[database-architecture-interview|вопросах по архитектуре БД]].

## Q36. Что такое `Covering Index` и `Partial Index`?

**Covering Index** (покрывающий) — индекс, содержащий все столбцы, нужные запросу. СУБД может ответить только из индекса, не обращаясь к таблице (Index-Only Scan):

```sql
-- Покрывающий индекс:
CREATE INDEX idx_orders_covering
    ON orders (customer_id, status)
    INCLUDE (total, created_at);  -- PostgreSQL INCLUDE

-- Запрос полностью покрыт индексом:
SELECT status, total, created_at FROM orders WHERE customer_id = 42;
```

**Partial Index** (частичный) — индексирует только подмножество строк:

```sql
-- Индексировать только активные заказы:
CREATE INDEX idx_active_orders ON orders (customer_id)
    WHERE status = 'ACTIVE';
-- Меньше размер → быстрее обновление → быстрее поиск
```

## Q37. Когда индекс НЕ помогает?

Индекс может быть проигнорирован оптимизатором:

1. **Низкая селективность** — столбец с 2-3 уникальными значениями (пол, статус). `Seq Scan` будет быстрее
2. **Функция на столбце** — `WHERE UPPER(name) = 'ИВАНОВ'` (нужен функциональный индекс)
3. **`LIKE '%text'`** — поиск по суффиксу не использует B-tree индекс
4. **Маленькая таблица** — полный скан быстрее, чем работа с индексом
5. **Большой процент возвращаемых строк** (>10-15%) — `Seq Scan` эффективнее
6. **Устаревшая статистика** — `ANALYZE table_name` для обновления

```sql
-- НЕ использует индекс на name:
SELECT * FROM users WHERE UPPER(name) = 'ИВАНОВ';

-- Решение — функциональный индекс:
CREATE INDEX idx_users_name_upper ON users (UPPER(name));
```

Индексы замедляют `INSERT`/`UPDATE`/`DELETE` — каждое изменение данных требует обновления всех затронутых индексов. Оптимальное количество индексов — компромисс между скоростью чтения и записи.

## Q38. (!) Что такое `ACID`?

`ACID` — четыре свойства, гарантирующие надёжность транзакций:

| Свойство | Описание | Пример нарушения |
|---|---|---|
| **A**tomicity (Атомарность) | Все операции транзакции выполняются или ни одна | Деньги списались, но не зачислились |
| **C**onsistency (Согласованность) | БД переходит из одного валидного состояния в другое | Нарушение `CHECK`-ограничения |
| **I**solation (Изоляция) | Параллельные транзакции не видят промежуточных состояний друг друга | Чтение «грязных» данных |
| **D**urability (Долговечность) | После `COMMIT` данные сохранены даже при сбое | Потеря данных после перезагрузки |

```sql
BEGIN;
    UPDATE accounts SET balance = balance - 1000 WHERE id = 1;
    UPDATE accounts SET balance = balance + 1000 WHERE id = 2;

    -- Если здесь произойдёт сбой — обе операции откатятся (Atomicity)
COMMIT;
```

> Подробнее о `ACID` в распределённых системах — в [[database-architecture-interview|вопросах по архитектуре БД]].

## Q39. (!) Какие уровни изоляции транзакций существуют?

| Уровень | Dirty Read | Non-repeatable Read | Phantom Read | Производительность |
|---|---|---|---|---|
| `READ UNCOMMITTED` | Да | Да | Да | Максимальная |
| `READ COMMITTED` | Нет | Да | Да | Высокая |
| `REPEATABLE READ` | Нет | Нет | Да* | Средняя |
| `SERIALIZABLE` | Нет | Нет | Нет | Низкая |

> *В `PostgreSQL` `REPEATABLE READ` также предотвращает phantom reads (реализация через MVCC/SSI).

**Аномалии**:
- **Dirty Read** — чтение незакоммиченных данных другой транзакции
- **Non-repeatable Read** — повторное чтение той же строки даёт другой результат
- **Phantom Read** — повторный запрос возвращает новые строки

```sql
-- Установка уровня изоляции:
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

BEGIN;
    SELECT balance FROM accounts WHERE id = 1;  -- 1000
    -- Другая транзакция: UPDATE accounts SET balance = 500 WHERE id = 1; COMMIT;
    SELECT balance FROM accounts WHERE id = 1;  -- всё ещё 1000 (REPEATABLE READ)
COMMIT;
```

В Java/Spring:

```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void processPayment(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId).orElseThrow();
    account.setBalance(account.getBalance().subtract(amount));
    accountRepository.save(account);
}
```

По умолчанию: `PostgreSQL` = `READ COMMITTED`, `MySQL InnoDB` = `REPEATABLE READ`.

## Q40. Что такое `deadlock` и как его избежать?

**Deadlock** — взаимная блокировка, когда две транзакции ждут друг друга:

```mermaid
graph LR
    T1["Транзакция 1<br/>Захватила A<br/>Ждёт B"] -->|ждёт| T2["Транзакция 2<br/>Захватила B<br/>Ждёт A"]
    T2 -->|ждёт| T1
```

```sql
-- Транзакция 1:
BEGIN; UPDATE accounts SET balance = 100 WHERE id = 1;  -- блокирует строку 1
       UPDATE accounts SET balance = 200 WHERE id = 2;  -- ждёт строку 2

-- Транзакция 2 (параллельно):
BEGIN; UPDATE accounts SET balance = 300 WHERE id = 2;  -- блокирует строку 2
       UPDATE accounts SET balance = 400 WHERE id = 1;  -- ждёт строку 1 → DEADLOCK!
```

**Стратегии предотвращения**:
1. **Единый порядок блокировки** — всегда блокировать ресурсы в одном порядке (по ID)
2. **Короткие транзакции** — минимизировать время удержания блокировок
3. **`SELECT ... FOR UPDATE NOWAIT`** — немедленная ошибка вместо ожидания
4. **Retry-логика** — СУБД автоматически откатывает одну из транзакций, приложение повторяет

## Q41. В чём разница между оптимистичной и пессимистичной блокировкой?

| | Пессимистичная | Оптимистичная |
|---|---|---|
| Механизм | `SELECT ... FOR UPDATE` | Столбец `version` / `modified_at` |
| Конфликт | Блокирует других | Обнаруживает при `COMMIT` |
| Throughput | Ниже (ожидание блокировок) | Выше (нет ожидания) |
| Deadlock-риск | Высокий | Нет |
| Когда использовать | Высокая конкуренция за одни данные | Низкая вероятность конфликтов |

```sql
-- Пессимистичная блокировка:
BEGIN;
SELECT * FROM products WHERE id = 42 FOR UPDATE;
UPDATE products SET stock = stock - 1 WHERE id = 42;
COMMIT;
```

```java
// JPA: оптимистичная блокировка
@Entity
public class Product {
    @Id private Long id;
    @Version private Integer version;  // автоматический контроль версий
    private Integer stock;
}

// При конкурентном UPDATE → OptimisticLockException
```

Подробнее о блокировках в ORM — в [[hibernate-interview|вопросах по Hibernate]].

## Q42. (!) Что такое `EXPLAIN` и как читать план выполнения?

`EXPLAIN` показывает, как СУБД будет выполнять запрос. `EXPLAIN ANALYZE` выполняет запрос и показывает реальное время:

```sql
EXPLAIN ANALYZE
SELECT e.name, d.name
FROM employees e
JOIN departments d ON e.department_id = d.id
WHERE e.salary > 100000;

-- Пример вывода (PostgreSQL):
-- Hash Join  (cost=1.09..2.19 rows=3 width=64) (actual time=0.025..0.027 rows=3 loops=1)
--   Hash Cond: (e.department_id = d.id)
--   -> Seq Scan on employees e (cost=0.00..1.05 rows=3 width=40)
--         Filter: (salary > 100000)
--         Rows Removed by Filter: 7
--   -> Hash  (cost=1.04..1.04 rows=4 width=36)
--         -> Seq Scan on departments d (cost=0.00..1.04 rows=4 width=36)
```

Ключевые операции в плане:

| Операция | Значение |
|---|---|
| `Seq Scan` | Полный скан таблицы (нет подходящего индекса) |
| `Index Scan` | Поиск по индексу + обращение к таблице |
| `Index Only Scan` | Все данные из индекса (covering index) |
| `Bitmap Index Scan` | Построение битовой карты по индексу |
| `Nested Loop` | Вложенный цикл (для маленьких таблиц) |
| `Hash Join` | Хеш-соединение (для средних/больших) |
| `Merge Join` | Слияние отсортированных наборов |
| `Sort` | Сортировка (может быть узким местом) |

`cost` — условная стоимость; `actual time` — реальное время в мс; `rows` — количество строк.

## Q43. Какие основные стратегии оптимизации SQL-запросов существуют?

1. **Индексы** — создавайте индексы для столбцов в `WHERE`, `JOIN`, `ORDER BY`
2. **Покрывающие индексы** — `INCLUDE` дополнительных столбцов для Index-Only Scan
3. **Избегайте `SELECT *`** — запрашивайте только нужные столбцы
4. **Пагинация через keyset** — вместо `OFFSET` используйте `WHERE id > last_id`
5. **Batch-операции** — `INSERT ... VALUES (...), (...), (...)` вместо цикла
6. **`EXISTS` вместо `IN`** — для больших подзапросов
7. **Анализ планов** — `EXPLAIN ANALYZE` + `pg_stat_statements`
8. **Статистика** — регулярный `ANALYZE` для актуальных планов
9. **Партиционирование** — разделение больших таблиц по дате/ключу
10. **Connection pooling** — `HikariCP`, `PgBouncer` для переиспользования соединений

```sql
-- Плохо: OFFSET для глубокой пагинации
SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 100000;  -- сканирует 100020 строк

-- Хорошо: keyset pagination
SELECT * FROM orders WHERE id > 100000 ORDER BY id LIMIT 20;  -- сканирует 20 строк
```

## Q44. Что такое `N+1` проблема?

`N+1` — антипаттерн, когда для N записей выполняется N дополнительных запросов вместо одного `JOIN`:

```java
// N+1 проблема в JPA:
List<Order> orders = orderRepository.findAll();  // 1 запрос
for (Order order : orders) {
    order.getCustomer().getName();  // N запросов (lazy loading)
}
```

Решения:

```java
// 1. JOIN FETCH (JPQL):
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllWithCustomers();

// 2. @EntityGraph:
@EntityGraph(attributePaths = {"customer"})
List<Order> findAll();

// 3. @BatchSize (Hibernate):
@BatchSize(size = 50)
@ManyToOne(fetch = FetchType.LAZY)
private Customer customer;
```

Подробнее о решении `N+1` — в [[hibernate-interview|вопросах по Hibernate]].

## Q45. (!) В чём разница между `JDBC` и `JPA`?

| | `JDBC` | `JPA` / `Hibernate` |
|---|---|---|
| Уровень абстракции | Низкий (SQL напрямую) | Высокий (ORM, объекты) |
| SQL | Пишется вручную | Генерируется автоматически |
| Маппинг | Ручной (`ResultSet` → объект) | Автоматический (аннотации) |
| Кэширование | Нет | L1 (сессия) + L2 (shared) |
| Производительность | Максимальная (контроль) | Может быть overhead (N+1, lazy) |
| Миграции | Вручную | `ddl-auto` + Flyway/Liquibase |

```java
// JDBC: низкоуровневый доступ
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement(
         "SELECT id, name, salary FROM employees WHERE department_id = ?")) {
    ps.setInt(1, departmentId);
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Employee emp = new Employee(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("salary")
            );
            employees.add(emp);
        }
    }
}

// JPA: высокоуровневый доступ
@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}

// Spring Data JPA: ещё проще
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartmentId(Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.salary > :minSalary")
    List<Employee> findHighEarners(@Param("minSalary") BigDecimal minSalary);
}
```

> `Spring Data JDBC` — альтернатива JPA без lazy loading, кэшей и proxy-объектов. Проще и предсказуемее для простых сценариев.

## Q46. Как определить индексы в `JPA`?

```java
@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_emp_department", columnList = "department_id"),
    @Index(name = "idx_emp_email", columnList = "email", unique = true),
    @Index(name = "idx_emp_dept_salary", columnList = "department_id, salary")
})
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "department_id")
    private Long departmentId;

    private BigDecimal salary;
}
```

> **Важно**: JPA-аннотации создают индексы только при `ddl-auto = create/update`. В продакшене индексы управляются через миграции (`Flyway` / `Liquibase`):

```sql
-- Flyway-миграция:
CREATE INDEX CONCURRENTLY idx_emp_department ON employees (department_id);
```

## Q47. Как управлять транзакциями в `Spring`?

**Декларативный** подход (рекомендуется):

```java
@Service
public class OrderService {

    @Transactional  // READ_COMMITTED по умолчанию
    public void placeOrder(OrderDto dto) {
        Order order = orderRepository.save(new Order(dto));
        inventoryService.reserve(dto.getItems());  // в той же транзакции
        notificationService.notify(order);
    }

    @Transactional(readOnly = true)  // оптимизация для read-only
    public List<Order> getOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(
        isolation = Isolation.REPEATABLE_READ,
        propagation = Propagation.REQUIRES_NEW,
        timeout = 5,
        rollbackFor = BusinessException.class
    )
    public void processPayment(Long orderId) {
        // ...
    }
}
```

**Программный** подход:

```java
@Autowired
private TransactionTemplate transactionTemplate;

public void complexOperation() {
    transactionTemplate.execute(status -> {
        // операции в транзакции
        if (somethingWrong) {
            status.setRollbackOnly();
        }
        return result;
    });
}
```

Ключевые параметры `@Transactional`: `propagation` (REQUIRED, REQUIRES_NEW, NESTED...), `isolation`, `readOnly`, `timeout`, `rollbackFor`.

> **Ловушка**: `@Transactional` не работает при вызове метода внутри того же класса (self-invocation), потому что Spring AOP работает через proxy. Решение — вынести метод в отдельный бин или использовать `TransactionTemplate`.

## Q48. (!) Что такое рекурсивные `CTE` и когда их применять?

Рекурсивное `CTE` (`WITH RECURSIVE`) позволяет запросу ссылаться на себя, обрабатывая иерархические и графовые структуры данных.

**Структура рекурсивного CTE:**

```sql
WITH RECURSIVE cte_name AS (
    -- Базовый случай (якорь) — начальные строки
    SELECT ... FROM table WHERE condition

    UNION ALL  -- или UNION (с дедупликацией)

    -- Рекурсивная часть — ссылается на cte_name
    SELECT ... FROM table JOIN cte_name ON ...
)
SELECT * FROM cte_name;
```

**Пример 1: Иерархия сотрудников (дерево менеджеров)**

```sql
-- Структура: employees(id, name, manager_id)
WITH RECURSIVE org_chart AS (
    -- Якорь: топ-менеджер без руководителя
    SELECT id, name, manager_id, 1 AS level, ARRAY[id] AS path
    FROM employees
    WHERE manager_id IS NULL

    UNION ALL

    -- Рекурсия: подчинённые
    SELECT e.id, e.name, e.manager_id, oc.level + 1, oc.path || e.id
    FROM employees e
    JOIN org_chart oc ON e.manager_id = oc.id
    WHERE NOT e.id = ANY(oc.path)  -- защита от циклов
)
SELECT
    REPEAT('  ', level - 1) || name AS hierarchy,
    level,
    path
FROM org_chart
ORDER BY path;
```

**Пример 2: Все категории дерева (e-commerce)**

```sql
WITH RECURSIVE category_tree AS (
    -- Корневые категории
    SELECT id, name, parent_id, name::TEXT AS full_path
    FROM categories
    WHERE parent_id IS NULL

    UNION ALL

    SELECT c.id, c.name, c.parent_id,
           ct.full_path || ' > ' || c.name
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT id, name, full_path FROM category_tree ORDER BY full_path;
```

**Пример 3: Числовые последовательности (генерация дат)**

```sql
-- Генерация всех дат за квартал
WITH RECURSIVE date_series AS (
    SELECT DATE '2026-01-01' AS dt
    UNION ALL
    SELECT dt + INTERVAL '1 day'
    FROM date_series
    WHERE dt < DATE '2026-03-31'
)
SELECT dt, COALESCE(SUM(o.amount), 0) AS daily_revenue
FROM date_series ds
LEFT JOIN orders o ON DATE(o.created_at) = ds.dt
GROUP BY ds.dt
ORDER BY ds.dt;
```

**Когда применять:**
- Деревья и иерархии (категории, оргструктура, комментарии)
- Поиск путей в графах (маршруты, зависимости)
- Генерация последовательностей (серии дат, чисел)
- Обход связанных списков

**Защита от бесконечных циклов:**

```sql
-- Ограничение глубины
WHERE level < 10

-- Или через массив пути
WHERE NOT id = ANY(path)
```

## Q49. (!) Как читать и интерпретировать план выполнения (`EXPLAIN`)?

`EXPLAIN` показывает план выполнения запроса без его фактического выполнения. `EXPLAIN ANALYZE` — выполняет запрос и показывает реальную статистику.

**Базовые команды:**

```sql
-- Только план (без выполнения)
EXPLAIN SELECT * FROM orders WHERE user_id = 42;

-- План + реальное время + строки
EXPLAIN ANALYZE SELECT * FROM orders WHERE user_id = 42;

-- Максимально подробный вывод
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) SELECT * FROM orders WHERE user_id = 42;
```

**Чтение плана (PostgreSQL):**

```
Nested Loop  (cost=0.43..85.23 rows=12 width=142) (actual time=0.082..1.234 rows=8 loops=1)
  ->  Index Scan using idx_orders_user on orders  (cost=0.43..28.10 rows=12 width=142)
           Index Cond: (user_id = 42)
  ->  Index Scan using products_pkey on products  (cost=0.43..4.76 rows=1 width=67)
           Index Cond: (id = orders.product_id)
Planning Time: 0.523 ms
Execution Time: 1.456 ms
```

**Что означают числа:**

| Поле | Значение |
|------|----------|
| `cost=0.43..85.23` | Оценочная стоимость: старт..финиш (в относительных единицах) |
| `rows=12` | Оценка строк (от планировщика, может сильно отличаться от факта) |
| `width=142` | Средний размер строки в байтах |
| `actual time=0.082..1.234` | Реальное время: первая строка..все строки (мс) |
| `rows=8` | Реальное число строк |
| `loops=1` | Сколько раз выполнялся этот узел |

**Ключевые операции в плане:**

```sql
-- Seq Scan — полное сканирование таблицы (плохо для больших таблиц)
Seq Scan on orders (cost=0.00..15234.00 rows=500000 width=72)
  Filter: (status = 'PENDING')

-- Index Scan — использует B-tree индекс (хорошо)
Index Scan using idx_orders_user on orders
  Index Cond: (user_id = 42)

-- Index Only Scan — данные только из индекса (лучший вариант для covering index)
Index Only Scan using idx_orders_user_status on orders
  Index Cond: ((user_id = 42) AND (status = 'PENDING'))
  Heap Fetches: 0  -- данные не читались из heap, всё в индексе

-- Bitmap Heap Scan — для IN/range запросов
Bitmap Heap Scan on orders
  Recheck Cond: (user_id = ANY ('{1,2,3}'::integer[]))
  ->  Bitmap Index Scan on idx_orders_user
        Index Cond: (user_id = ANY ('{1,2,3}'::integer[]))

-- Hash Join — соединение через хэш (хорошо для больших таблиц)
Hash Join  (cost=...)
  Hash Cond: (orders.user_id = users.id)
  ->  Seq Scan on orders
  ->  Hash
       ->  Seq Scan on users

-- Nested Loop — хорошо когда внешний цикл мал
-- Merge Join — хорошо когда оба набора отсортированы
```

**Диагностика проблем:**

```sql
-- Проблема: rows estimate сильно расходится с actual rows
-- Решение: обновить статистику
ANALYZE orders;

-- Проблема: Seq Scan вместо Index Scan
-- Причины:
-- 1. Индекса нет
-- 2. Таблица маленькая (планировщик считает seq scan дешевле)
-- 3. Функция над колонкой: WHERE LOWER(email) = '...' — индекс не используется
-- 4. LIKE '%suffix%' — B-tree не помогает

-- Принудительный анализ с Buffers:
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM orders WHERE created_at > NOW() - INTERVAL '7 days';
-- shared hit=X — данные из page cache
-- shared read=X — данные читались с диска
```

**Полезные инструменты:** [explain.depesz.com](https://explain.depesz.com) и [explain.dalibo.com](https://explain.dalibo.com) — визуализация плана выполнения.

## Q50. Что такое партиционирование таблиц и зачем оно нужно?

**Партиционирование** — разбиение большой таблицы на физически отдельные части (партиции), которые логически остаются единой таблицей. PostgreSQL поддерживает декларативное партиционирование с версии 10.

**Типы партиционирования:**

```sql
-- 1. RANGE — по диапазону значений (чаще всего по дате)
CREATE TABLE orders (
    id         BIGINT,
    created_at DATE NOT NULL,
    amount     DECIMAL,
    user_id    BIGINT
) PARTITION BY RANGE (created_at);

-- Создание партиций
CREATE TABLE orders_2025 PARTITION OF orders
    FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

CREATE TABLE orders_2026 PARTITION OF orders
    FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

-- Default partition — для значений вне всех диапазонов
CREATE TABLE orders_default PARTITION OF orders DEFAULT;
```

```sql
-- 2. LIST — по дискретному набору значений
CREATE TABLE events (
    id     BIGINT,
    region TEXT NOT NULL,
    data   JSONB
) PARTITION BY LIST (region);

CREATE TABLE events_ru PARTITION OF events FOR VALUES IN ('ru', 'ru_KZ', 'ru_BY');
CREATE TABLE events_en PARTITION OF events FOR VALUES IN ('en', 'en_US', 'en_GB');
```

```sql
-- 3. HASH — по хэшу (равномерное распределение)
CREATE TABLE users (
    id   BIGINT,
    name TEXT
) PARTITION BY HASH (id);

CREATE TABLE users_0 PARTITION OF users FOR VALUES WITH (modulus 4, remainder 0);
CREATE TABLE users_1 PARTITION OF users FOR VALUES WITH (modulus 4, remainder 1);
CREATE TABLE users_2 PARTITION OF users FOR VALUES WITH (modulus 4, remainder 2);
CREATE TABLE users_3 PARTITION OF users FOR VALUES WITH (modulus 4, remainder 3);
```

**Преимущества партиционирования:**

1. **Partition Pruning** — планировщик исключает неподходящие партиции:
   ```sql
   -- Запрос только за 2026 год — сканируется только партиция orders_2026
   SELECT * FROM orders WHERE created_at >= '2026-01-01';
   -- EXPLAIN покажет: Append -> Seq Scan on orders_2026
   ```

2. **Быстрое удаление старых данных**:
   ```sql
   -- Вместо DELETE FROM orders WHERE created_at < '2024-01-01' (медленно, tombstones)
   DROP TABLE orders_2024;  -- мгновенно!
   ```

3. **Параллельное сканирование** партиций

4. **Локальные индексы** — каждая партиция имеет собственные индексы меньшего размера

**Когда партиционировать:**
- Таблица > 10-50 млн строк и продолжает расти
- Чёткая логика по дате/региону/типу
- Регулярное удаление старых данных (retention policy)
- Нужно архивировать исторические данные

## Q51. (!) Какие виды индексов существуют в `PostgreSQL`?

PostgreSQL поддерживает несколько типов индексов с разными алгоритмами, каждый оптимален для своего класса задач.

**1. B-tree (по умолчанию) — универсальный:**

```sql
CREATE INDEX idx_orders_user ON orders (user_id);
-- Поддерживает: =, <, <=, >, >=, BETWEEN, IN, IS NULL, LIKE 'prefix%'
-- Использует: большинство запросов с WHERE и ORDER BY
```

**2. Hash — только точное равенство:**

```sql
CREATE INDEX idx_sessions_token ON sessions USING HASH (token);
-- Поддерживает: только =
-- Быстрее B-tree для точных совпадений, но не поддерживает диапазоны
```

**3. GIN (Generalized Inverted Index) — для составных типов:**

```sql
-- Для JSONB
CREATE INDEX idx_products_attrs ON products USING GIN (attributes);
SELECT * FROM products WHERE attributes @> '{"color": "red"}';

-- Для полнотекстового поиска
CREATE INDEX idx_articles_fts ON articles USING GIN (to_tsvector('russian', body));
SELECT * FROM articles WHERE to_tsvector('russian', body) @@ to_tsquery('поиск');

-- Для массивов
CREATE INDEX idx_tags ON posts USING GIN (tags);
SELECT * FROM posts WHERE tags @> ARRAY['postgres', 'sql'];
```

**4. GiST (Generalized Search Tree) — геоданные и диапазоны:**

```sql
-- Для геоданных (PostGIS)
CREATE INDEX idx_locations_geo ON locations USING GIST (coords);
SELECT * FROM locations WHERE coords <-> '(55.75, 37.62)' < 10;  -- в радиусе 10 км

-- Для range-типов
CREATE INDEX idx_events_period ON events USING GIST (period);
SELECT * FROM events WHERE period && '[2026-01-01, 2026-12-31]';  -- пересечение
```

**5. BRIN (Block Range Index) — для огромных таблиц с корреляцией:**

```sql
-- Эффективен если значения физически упорядочены (напр., created_at SERIAL)
CREATE INDEX idx_logs_created BRIN (created_at);
-- Очень маленький (128 байт на блок vs B-tree с его размером)
-- Только для данных с высокой физической корреляцией с порядком вставки
```

**6. Partial Index — индекс на подмножество строк:**

```sql
-- Индекс только по активным пользователям
CREATE INDEX idx_active_users ON users (email)
WHERE status = 'ACTIVE';

-- Запрос использует индекс только если WHERE совпадает
SELECT * FROM users WHERE email = 'ivan@example.com' AND status = 'ACTIVE';
```

**7. Expression Index — индекс по выражению:**

```sql
-- Поиск без учёта регистра
CREATE INDEX idx_users_email_lower ON users (LOWER(email));
SELECT * FROM users WHERE LOWER(email) = 'ivan@example.com';

-- Индекс по году для DATE поля
CREATE INDEX idx_orders_year ON orders (EXTRACT(YEAR FROM created_at));
```

**8. Covering Index (INCLUDE) — включение доп. колонок:**

```sql
-- Index Only Scan — данные только из индекса, heap не читается
CREATE INDEX idx_orders_covering ON orders (user_id) INCLUDE (status, amount);
SELECT status, amount FROM orders WHERE user_id = 42;
-- EXPLAIN: Index Only Scan, Heap Fetches: 0
```

**Выбор типа индекса:**

| Тип | Когда |
|-----|-------|
| B-tree | По умолчанию, диапазоны, ORDER BY |
| Hash | Только точное равенство, высокий throughput |
| GIN | JSONB, массивы, full-text search |
| GiST | Геоданные, overlap-запросы, диапазоны |
| BRIN | Огромные таблицы с хронологическими данными |
| Partial | Небольшое подмножество (active records) |
| Expression | WHERE содержит функцию над колонкой |

## Q52. Что такое `VACUUM` и `ANALYZE` в `PostgreSQL`?

Обе команды критически важны для здоровья PostgreSQL и часто спрашиваются на собеседованиях.

**VACUUM — борьба с мёртвыми строками (bloat):**

PostgreSQL использует MVCC: при UPDATE и DELETE старые версии строк не удаляются физически, а помечаются как "мёртвые" (`dead tuples`). Это создаёт **table bloat** — раздутие таблицы.

```sql
-- Базовый VACUUM — помечает мёртвые строки как доступные для повторного использования
VACUUM orders;

-- VACUUM FULL — физически перезаписывает таблицу (блокирует чтение!)
-- Использовать только в maintenance window
VACUUM FULL orders;

-- VACUUM ANALYZE — сразу и очистка, и обновление статистики
VACUUM ANALYZE orders;

-- Мониторинг bloat и autovacuum
SELECT relname,
       n_live_tup,
       n_dead_tup,
       last_vacuum,
       last_autovacuum,
       last_analyze
FROM pg_stat_user_tables
WHERE relname = 'orders';
```

**Autovacuum** — автоматически запускается PostgreSQL на основе порогов:
- `autovacuum_vacuum_threshold` (по умолчанию 50 мёртвых строк)
- `autovacuum_vacuum_scale_factor` (по умолчанию 20% таблицы)

```sql
-- Настройка autovacuum для «горячих» таблиц
ALTER TABLE orders SET (
    autovacuum_vacuum_scale_factor = 0.01,  -- 1% вместо 20%
    autovacuum_analyze_scale_factor = 0.005
);
```

**ANALYZE — обновление статистики для планировщика:**

Планировщик PostgreSQL использует статистику (`pg_statistic`) для выбора оптимального плана. Устаревшая статистика → плохие планы.

```sql
-- Обновить статистику по таблице
ANALYZE orders;

-- Или по конкретной колонке
ANALYZE orders (user_id, created_at);

-- Проверить статистику
SELECT attname, n_distinct, correlation
FROM pg_stats
WHERE tablename = 'orders';
-- correlation близко к 1/-1 → BRIN эффективен
-- n_distinct < 0 → доля от общего числа строк (напр. -0.5 = 50% уникальных)
```

**Связь VACUUM и transaction ID wraparound:**

PostgreSQL хранит `xid` (transaction ID) как 32-bit integer. После ~2 млрд транзакций происходит wraparound. VACUUM предотвращает это, замораживая (`freeze`) старые строки:

```sql
-- Мониторинг опасного приближения к wraparound
SELECT datname, age(datfrozenxid) AS xid_age,
       2000000000 - age(datfrozenxid) AS xid_remaining
FROM pg_database
ORDER BY xid_age DESC;
-- Если xid_age > 1.5 млрд — опасная зона!
```

## Q53. Как работает `MVCC` (`Multi-Version Concurrency Control`)?

**MVCC** — механизм управления конкурентным доступом без блокировок читателей пишущими транзакциями. Читатели не блокируют писателей и наоборот.

**Принцип работы:**

```
Каждая строка в PostgreSQL содержит скрытые системные колонки:
- xmin — ID транзакции, создавшей строку
- xmax — ID транзакции, удалившей/обновившей строку (0 = актуальная)

При SELECT: видны только строки, где xmin <= current_xid < xmax
(с учётом снимка — snapshot)
```

**Визуализация UPDATE:**

```sql
-- Исходное состояние:
-- (id=1, name='Иван', xmin=100, xmax=0)

UPDATE users SET name = 'Ivan' WHERE id = 1;
-- Транзакция txid=200:
-- (id=1, name='Иван', xmin=100, xmax=200)  ← старая версия (мёртвая после commit)
-- (id=1, name='Ivan', xmin=200, xmax=0)    ← новая версия

-- Другие транзакции со snapshot < 200 всё ещё видят 'Иван'
-- После VACUUM: мёртвая строка удаляется
```

**Снимок (Snapshot) и уровни изоляции:**

```sql
-- READ COMMITTED: новый snapshot на каждый оператор
BEGIN;
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT name FROM users WHERE id = 1;  -- видит 'Иван'
-- другая транзакция делает UPDATE и COMMIT
SELECT name FROM users WHERE id = 1;  -- видит 'Ivan' (новый snapshot!)
COMMIT;

-- REPEATABLE READ: один snapshot на всю транзакцию
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT name FROM users WHERE id = 1;  -- видит 'Иван', snapshot зафиксирован
-- другая транзакция делает UPDATE и COMMIT
SELECT name FROM users WHERE id = 1;  -- всё равно видит 'Иван'!
COMMIT;

-- SERIALIZABLE: полная изоляция через SSI (Serializable Snapshot Isolation)
```

**Преимущества MVCC:**
- Читатели не блокируют писателей — высокий параллелизм
- Писатели не блокируют читателей
- Каждая транзакция видит согласованный снимок данных

**Недостатки:**
- **Table bloat** — мёртвые строки занимают место → нужен VACUUM
- **XID wraparound** — ограничение на ~2 млрд транзакций
- Overhead на хранение дополнительных версий строк

**Практическое следствие — почему DELETE медленнее TRUNCATE:**

```sql
-- DELETE: помечает каждую строку как мёртвую (MVCC), WAL-запись на каждую строку
DELETE FROM orders;  -- медленно, можно откатить

-- TRUNCATE: создаёт новую пустую таблицу, WAL на уровне метаданных
TRUNCATE orders;     -- быстро, но нельзя откатить частично (DDL в транзакции можно)
```

---

## See also

- [[mongodb-interview|MongoDB]] — вопросы по NoSQL базе MongoDB
- [[hibernate-interview|Hibernate]] — ORM-фреймворк для работы с SQL из Java
- [[database-architecture-interview|Архитектура БД]] — вопросы по архитектуре баз данных
- [[redis-interview|Redis]] — кэширование и in-memory хранилища
- [[elasticsearch-interview|Elasticsearch]] — полнотекстовый поиск
- [[database-transactions-interview|Транзакции и уровни изоляции]] — ACID, MVCC, блокировки

- [[cassandra-interview|Apache Cassandra]]
- [[clickhouse-interview|ClickHouse]]
- [[cockroachdb-interview|CockroachDB]]
- [[database-architecture-interview|Database Architecture]]
- [[database-transactions-interview|Транзакции и уровни изоляции]]
- [[dynamodb-interview|DynamoDB]]
