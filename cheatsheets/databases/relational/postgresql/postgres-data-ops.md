---
title: "PostgreSQL: операции с данными (CRUD)"
description: "Кратко: базовые примеры INSERT/SELECT/WHERE/UPDATE/DELETE для быстрого старта."
tags:
  - databases
  - relational
  - postgres-data-ops
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# PostgreSQL: операции с данными (CRUD)

Кратко: базовые примеры **INSERT**/**SELECT**/**WHERE**/**UPDATE**/**DELETE** для быстрого старта.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)

### Обучающие материалы

- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)


См. также: [postgres-queries](postgres-queries.md) — [postgres-structure](postgres-structure.md).

## Содержание

- [INSERT](#insert)
- [SELECT](#select)
- [WHERE и фильтры](#where-и-фильтры)
- [UPDATE](#update)
- [DELETE](#delete)
- [RETURNING](#returning)
- [Вставка батчами](#вставка-батчами)
- [UPSERT (ON CONFLICT)](#upsert-on-conflict)
- [Транзакции и безопасность данных](#транзакции-и-безопасность-данных)
- [COPY и массовая загрузка](#copy-и-массовая-загрузка)
- [Блокировки при DML](#блокировки-при-dml)
- [Пагинация и курсоры](#пагинация-и-курсоры)
- [Очереди и конкуренция](#очереди-и-конкуренция)
- [Подробное описание INSERT](#подробное-описание-insert)
  - [Базовый синтаксис INSERT](#базовый-синтаксис-insert)
  - [Вставка одной строки](#вставка-одной-строки)
  - [Вставка нескольких строк](#вставка-нескольких-строк)
  - [Использование RETURNING](#использование-returning)
- [Подробное описание SELECT](#подробное-описание-select)
  - [Базовый синтаксис SELECT](#базовый-синтаксис-select)
  - [Выборка всех столбцов](#выборка-всех-столбцов)
  - [Выборка конкретных столбцов](#выборка-конкретных-столбцов)
  - [Псевдонимы столбцов (AS)](#псевдонимы-столбцов-as)
- [Подробное описание WHERE](#подробное-описание-where)
  - [Операторы сравнения](#операторы-сравнения)
  - [Логические операторы](#логические-операторы)
  - [IS NULL и IS NOT NULL](#is-null-и-is-not-null)
- [Подробное описание UPDATE](#подробное-описание-update)
  - [Базовый синтаксис UPDATE](#базовый-синтаксис-update)
  - [Обновление нескольких столбцов](#обновление-нескольких-столбцов)
  - [Обновление на основе подзапроса](#обновление-на-основе-подзапроса)
  - [Использование CASE в UPDATE](#использование-case-в-update)
- [Подробное описание DELETE](#подробное-описание-delete)
  - [Базовый синтаксис DELETE](#базовый-синтаксис-delete)
  - [DELETE с подзапросами](#delete-с-подзапросами)
  - [CASCADE удаление](#cascade-удаление)
- [Расширенные техники работы с данными](#расширенные-техники-работы-с-данными)
  - [Массовые операции (Bulk Operations)](#массовые-операции-bulk-operations)
  - [Оптимизация производительности](#оптимизация-производительности)
  - [Обработка конфликтов](#обработка-конфликтов)
  - [Получение информации об операциях](#получение-информации-об-операциях)
  - [Безопасность и валидация](#безопасность-и-валидация)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## INSERT

```sql
-- Вставка одной строки в таблицу authors
INSERT INTO authors (name, city)
VALUES ('Ada', 'London');
```

## SELECT
```sql
SELECT id, name, city
FROM authors;
```

## WHERE и фильтры
```sql
SELECT * FROM authors
WHERE city = 'London' AND name LIKE 'A%';
```
- Операторы: `=`, `<>`, `<`, `>`, `BETWEEN`, `LIKE`, `IN`, `IS NULL`.

## UPDATE
```sql
UPDATE authors
SET city = 'Paris'
WHERE id = 1;
```

## DELETE
```sql
DELETE FROM authors
WHERE id = 1;
```
- Будьте осторожны: без **WHERE** удалятся все строки.

## RETURNING
- **Получить созданный id:**
```sql
INSERT INTO authors (name, city)
VALUES ('Ada', 'London')
RETURNING id;
```
- Аналогично работает с **UPDATE**/**DELETE** для получения изменённых строк.

## Вставка батчами
- **Одна команда с несколькими **VALUES**:**
```sql
INSERT INTO authors (name, city) VALUES
 ('Ada','London'),
 ('Bob','Paris'),
 ('Cato','Rome');
```
- Для очень больших объёмов используйте `COPY ... FROM STDIN` или **pg_dump** формат **CSV**.

## UPSERT (ON CONFLICT)
- **Уникальный ключ по **email**:**
```sql
CREATE UNIQUE INDEX ux_users_email ON users(email);

INSERT INTO users (email, name)
VALUES ('a@b.com','Ada')
ON CONFLICT (email)
DO UPDATE SET name = EXCLUDED.name, updated_at = now();
```
- Вариант без обновления: `DO NOTHING`.

## Транзакции и безопасность данных
- **Объединяйте связанные операции в транзакцию:**
```sql
BEGIN;
INSERT INTO orders(user_id, total) VALUES (1, 100);
UPDATE users SET balance = balance - 100 WHERE id = 1;
COMMIT;
```
- Для критичных изменений используйте `WHERE` с точным ключом и проверяйте, что `rowcount` > 0.

## COPY и массовая загрузка
- **Быстрый импорт из **CSV**:**
```sql
COPY authors(name, city)
FROM '/path/authors.csv' WITH (FORMAT csv, HEADER true);
```
- **Через **psql**:**
```text
\copy authors(name, city) FROM 'authors.csv' CSV HEADER
```
- Для **JSON**/бинарных форматов можно использовать `PROGRAM` или `STDIN`.

## Блокировки при DML
- `SELECT ... FOR UPDATE|FOR SHARE|FOR `NO` KEY UPDATE` — берёт блокировки строк.
- Избегайте долгих транзакций с `FOR UPDATE`, чтобы не блокировать других.
- Частичные обновления лучше делать с индексами, чтобы не сканировать всю таблицу.
- Проверяйте конфликт: `FOR UPDATE NOWAIT` или `SKIP LOCKED` для очередей.

## Пагинация и курсоры
- Классика **LIMIT**/**OFFSET** плохо масштабируется на большие **OFFSET**.
- **Используйте **keyset pagination**:**
```sql
SELECT * FROM orders
WHERE (created_at, id) < (:cursor_created_at, :cursor_id)
ORDER BY created_at DESC, id DESC
LIMIT 50;
```
- **Курсоры:**
```sql
BEGIN;
DECLARE cur NO SCROLL CURSOR FOR SELECT * FROM big_table;
FETCH FORWARD 100 FROM cur;
FETCH FORWARD 100 FROM cur;
CLOSE cur;
COMMIT;
```
- Для **web**-приложений курсоры редко нужны; лучше **keyset**.

## Очереди и конкуренция
- **Простейшая очередь на **SKIP LOCKED**:**
```sql
BEGIN;
SELECT id, payload FROM tasks
WHERE status = 'NEW'
FOR UPDATE SKIP LOCKED
LIMIT 10;
-- обработать и UPDATE status='DONE'
COMMIT;
```
- `SKIP LOCKED` помогает распределить работу между воркерами без взаимной блокировки.
- Следите за индексами на фильтры очереди (`status`, `created_at`).

## Подробное описание INSERT

### Базовый синтаксис INSERT

**Для добавления данных применяется команда `INSERT`, которая имеет следующий формальный синтаксис:**

```sql
INSERT INTO имя_таблицы (столбец1, столбец2, ... столбецN)
VALUES (значение1, значение2, ... значениеN)
```

После `INSERT INTO` идет имя таблицы, затем в скобках указываются все столбцы через запятую, в которые надо добавлять данные. И в конце после слова `VALUES` в скобках перечисляются добавляемые значения.

**Пример таблицы:**
```sql
CREATE TABLE Products (
    Id SERIAL PRIMARY KEY,
    ProductName VARCHAR(30) NOT NULL,
    Manufacturer VARCHAR(20) NOT NULL,
    ProductCount INTEGER DEFAULT 0,
    Price NUMERIC
);
```

### Вставка одной строки

**Добавим в нее одну строку:**
```sql
INSERT INTO Products VALUES (1, 'Galaxy S9', 'Samsung', 4, 63000);
```

**Важно:** Стоит учитывать, что значения для столбцов в скобках после ключевого слова `VALUES` передаются по порядку их объявления. Например, в выражении `CREATE TABLE` выше можно увидеть, что первым столбцом идет `Id`, поэтому этому столбцу передается число `1`. Второй столбец называется `ProductName`, поэтому второе значение — строка "**Galaxy S9**" будет передано именно этому столбцу и так далее.

**Явное указание столбцов:**
```sql
INSERT INTO Products (ProductName, Price, Manufacturer)
VALUES ('iPhone X', 71000, 'Apple');
```

**Здесь значение указывается только для трех столбцов. Причем теперь значения передаются в порядке следования столбцов:**
1. `ProductName`: '**iPhone** X'
2. `Manufacturer`: '**Apple**'
3. `Price`: `71000`

Для столбца `Id` значение будет генерироваться автоматически базой данных, так как он представляет тип `SERIAL`. То есть к значению из последней строки будет добавляться единица.

Для остальных столбцов будет добавляться значение по умолчанию, если задан атрибут `DEFAULT` (например, для столбца `ProductCount`), или значение `NULL`. При этом неуказанные столбцы (за исключением тех, которые имеют тип `Serial`) должны допускать значение `NULL` или иметь атрибут `DEFAULT`.

**Если конкретные столбцы не указываются, как в первом примере, тогда мы должны передать значения для всех столбцов в таблице.**

### Вставка нескольких строк

**Также мы можем добавить сразу несколько строк:**
```sql
INSERT INTO Products (ProductName, Manufacturer, ProductCount, Price)
VALUES
('iPhone 6', 'Apple', 3, 36000),
('Galaxy S8', 'Samsung', 2, 46000),
('Galaxy S8 Plus', 'Samsung', 1, 56000);
```

### Использование RETURNING

**Если мы добавляем значения только для части столбцов, то мы можем не знать, какие значения будут у других столбцов.** Например, какое значение получит столбец `Id` у товара. С помощью оператора `RETURNING` мы можем получить это значение:**

```sql
INSERT INTO Products
(ProductName, Manufacturer, ProductCount, Price)
VALUES('Desire 12', 'HTC', 8, 21000)
RETURNING id;
```

**RETURNING с несколькими столбцами:**
```sql
INSERT INTO Products (ProductName, Manufacturer, ProductCount, Price)
VALUES('New Product', 'Manufacturer', 10, 50000)
RETURNING id, productname, price;
```

**RETURNING с `UPDATE`:**
```sql
UPDATE Products
SET Price = Price + 1000
WHERE Manufacturer = 'Samsung'
RETURNING id, productname, price;
```

**RETURNING с `DELETE`:**
```sql
DELETE FROM Products
WHERE ProductCount = 0
RETURNING id, productname;
```

## Подробное описание SELECT

### Базовый синтаксис SELECT

**Для извлечения данных из БД применяется команда `SELECT`. В упрощенном виде она имеет следующий синтаксис:**

```sql
SELECT столбцы
FROM имя_таблицы
[WHERE условие]
[ORDER BY столбец]
[LIMIT количество]
```

### Выборка всех столбцов

**Получим все объекты из таблицы:**
```sql
SELECT * FROM Products;
```

Символ звездочка `*` указывает, что нам надо получить все столбцы. Однако использование символа звездочки `*` считается не очень хорошей практикой, так как, как правило, не все столбцы бывают нужны. И более оптимальный подход заключается в указании всех необходимых столбцов после слова `SELECT`. Исключение составляет тот случай, когда надо получить данные по абсолютно всем столбцам таблицы. Также использование символа `*` может быть предпочтительно в таких ситуациях, когда в точности не известны названия столбцов.

### Выборка конкретных столбцов

**Если нам надо получить данные не по всем, а по каким-то конкретным столбцам:**
```sql
SELECT ProductName, Price FROM Products;
```

**Спецификация столбца необязательно должна представлять его название. Это может быть любое выражение, например, результат арифметической операции:**

```sql
SELECT ProductCount, Manufacturer, Price * ProductCount
FROM Products;
```

Здесь при выборке будут создаваться три столбца. Причем третий столбец представляет значение столбца `Price`, умноженное на значение столбца `ProductCount`, то есть совокупную стоимость товара.

### Псевдонимы столбцов (AS)

**С помощью оператора `AS` можно изменить название выходного столбца или определить его псевдоним:**
```sql
SELECT ProductCount AS Title,
       Manufacturer,
       Price * ProductCount AS TotalSum
FROM Products;
```

В данном случае результатом выборки являются данные по 3-м столбцам. Для первого столбца определяется псевдоним `Title`, хотя в реальности он будет представлять столбец `ProductCount`. Второй столбец сохраняет свое название — `Manufacturer`. Третий столбец `TotalSum` хранит произведение столбцов `ProductCount` и `Price`.

## Подробное описание WHERE

### Операторы сравнения

Для фильтрации данных применяется оператор `WHERE`, после которого указывается условие, на основании которого производится фильтрация. Если условие истинно, то строка попадает в результирующую выборку.

**В **PostgreSQL** можно применять следующие операции сравнения:**
- `=`: сравнение на равенство
- `<>` или `!=`: сравнение на неравенство
- `<`: меньше чем
- `>`: больше чем
- `<=`: меньше чем или равно
- `>=`: больше чем или равно

**Примеры:**
```sql
-- Поиск по равенству
SELECT * FROM Products
WHERE Manufacturer = 'Apple';

-- Стоит отметить, что в данном случае большое значение имеет регистр символов,
-- к примеру, строка "Apple" не эквивалентна строке "APPLE" или "apple".

-- Поиск по неравенству
SELECT * FROM Products
WHERE Price < 39000;

-- Сложные выражения
SELECT * FROM Products
WHERE Price * ProductCount > 90000;
```

### Логические операторы

**Чтобы объединить нескольких условий в одно, в `PostgreSQL` можно использовать логические операторы:**

**AND** — операция логического И:**
```sql
выражение1 AND выражение2
```

Только если оба этих выражения одновременно истинны, то и общее условие оператора `AND` также будет истинно.

**Пример:**
```sql
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' AND Price > 50000;
```

**OR** — операция логического ИЛИ:**
```sql
выражение1 OR выражение2
```

Если хотя бы одно из этих выражений истинно, то общее условие оператора `OR` также будет истинно.

**Пример:**
```sql
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' OR Price > 50000;
```

**NOT** — операция логического отрицания:**
```sql
NOT выражение
```

Если выражение в этой операции ложно, то общее условие истинно.

**Пример:**
```sql
SELECT * FROM Products
WHERE NOT Manufacturer = 'Samsung';

-- Эквивалентно:
SELECT * FROM Products
WHERE Manufacturer <> 'Samsung';
```

**Комбинирование операторов:**
```sql
-- Использование нескольких операторов
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' OR Price > 30000 AND ProductCount > 2;

-- Порядок операций: AND имеет более высокий приоритет, чем OR
-- Эквивалентно:
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' OR (Price > 30000 AND ProductCount > 2);

-- Использование скобок для изменения порядка
SELECT * FROM Products
WHERE (Manufacturer = 'Samsung' OR Price > 30000) AND ProductCount > 2;
```

### `IS` NULL и `IS` NOT NULL

**Ряд столбцов может допускать значение `NULL`.** Это значение не эквивалентно пустой строке `''`. `NULL` представляет полное отсутствие какого-либо значения. И для проверки на наличие подобного значения применяется оператор `IS NULL`.

```sql
-- Найти строки с NULL значением
SELECT * FROM Products
WHERE ProductCount IS NULL;

-- Найти строки, где значение НЕ NULL
SELECT * FROM Products
WHERE ProductCount IS NOT NULL;
```

## Подробное описание UPDATE

### Базовый синтаксис UPDATE

**Для обновления данных в базе данных **PostgreSQL** применяется команда `UPDATE`. Она имеет следующий общий формальный синтаксис:**

```sql
UPDATE имя_таблицы
SET столбец1 = значение1, столбец2 = значение2, ... столбецN = значениеN
[WHERE условие_обновления]
```

**Пример:**
```sql
-- Увеличить цену у всех товаров на 3000
UPDATE Products
SET Price = Price + 3000;
```

В данном случае обновление касается всех строк. С помощью выражения `WHERE` можно с помощью условия конкретизировать обновляемые строки — если строка соответствует условию, то она будет обновляться.

**Обновление с условием:**
```sql
-- Изменить название производителя
UPDATE Products
SET Manufacturer = 'Samsung Inc.'
WHERE Manufacturer = 'Samsung';
```

### Обновление нескольких столбцов

**Также можно обновлять сразу несколько столбцов:**
```sql
UPDATE Products
SET Manufacturer = 'Samsung',
    ProductCount = ProductCount + 3
WHERE Manufacturer = 'Samsung Inc.';
```

### Обновление на основе подзапроса

**UPDATE с использованием подзапроса:**
```sql
-- Обновить цену товаров на основе средней цены
UPDATE Products
SET Price = (SELECT AVG(Price) FROM Products)
WHERE Price IS NULL;
```

**UPDATE с использованием `JOIN` (в PostgreSQL через FROM):**
```sql
-- Обновить товары на основе данных из другой таблицы
UPDATE Products p
SET Price = p2.price
FROM Products_Backup p2
WHERE p.Id = p2.Id;
```

### Использование CASE в UPDATE

```sql
-- Условное обновление
UPDATE Products
SET Price = CASE
    WHEN Price < 10000 THEN Price * 1.1
    WHEN Price < 50000 THEN Price * 1.05
    ELSE Price
END;
```

## Подробное описание DELETE

### Базовый синтаксис DELETE

**Для удаления данных в **PostgreSQL** применяется команда `DELETE`. Она имеет следующий синтаксис:**

```sql
DELETE FROM имя_таблицы
[WHERE условие_удаления]
```

**Пример:**
```sql
-- Удалить строки по условию
DELETE FROM Products
WHERE Manufacturer = 'Apple';
```

**Удаление с несколькими условиями:**
```sql
DELETE FROM Products
WHERE Manufacturer = 'HTC' AND Price < 15000;
```

**Важно:** Если необходимо вовсе удалить все строки вне зависимости от условия, то условие можно не указывать:

```sql
DELETE FROM Products;
```

**Однако, для удаления всех строк лучше использовать `TRUNCATE`:**
```sql
TRUNCATE TABLE Products;
-- Быстрее и эффективнее, чем DELETE без WHERE
```

### DELETE с подзапросами

**Удаление на основе подзапроса:**
```sql
-- Удалить товары, которых нет в заказах
DELETE FROM Products p
WHERE NOT EXISTS (
    SELECT 1 FROM OrderItems oi
    WHERE oi.ProductId = p.Id
);
```

**DELETE с использованием `JOIN` (через USING):**
```sql
DELETE FROM Products p
USING Products_Backup pb
WHERE p.Id = pb.Id
  AND pb.Status = 'deleted';
```

### CASCADE удаление

**При удалении строк из главной таблицы, если установлено `ON DELETE CASCADE`, автоматически удаляются связанные строки из зависимой таблицы:**

```sql
-- Если в OrderItems установлено ON DELETE CASCADE
DELETE FROM Products WHERE Id = 1;
-- Автоматически удалятся все связанные OrderItems
```

## Расширенные техники работы с данными

### Массовые операции (Bulk Operations)

**Вставка больших объемов данных:**
```sql
-- Использование множественных VALUES (до ~1000 строк за раз)
INSERT INTO Products (ProductName, Manufacturer, ProductCount, Price)
VALUES
('Product 1', 'M1', 10, 100),
('Product 2', 'M2', 20, 200),
-- ... много строк
('Product 100', 'M100', 100, 1000);

-- Для больших объемов используйте COPY
COPY Products (ProductName, Manufacturer, ProductCount, Price)
FROM '/path/to/file.csv' WITH (FORMAT csv, HEADER true);
```

**Массовое обновление:**
```sql
-- Обновление на основе временной таблицы
CREATE TEMP TABLE temp_updates (
    id INTEGER,
    new_price NUMERIC
);

INSERT INTO temp_updates VALUES
(1, 5000),
(2, 6000);

UPDATE Products p
SET Price = tu.new_price
FROM temp_updates tu
WHERE p.Id = tu.id;
```

**Массовое удаление:**
```sql
-- Удаление больших объемов данных (порциями)
DO $$
DECLARE
    rows_deleted INTEGER;
BEGIN
    LOOP
        DELETE FROM large_table
        WHERE id IN (
            SELECT id FROM large_table
            WHERE status = 'deleted'
            LIMIT 1000
        );
        GET DIAGNOSTICS rows_deleted = ROW_COUNT;
        EXIT WHEN rows_deleted = 0;
        COMMIT;
    END LOOP;
END $$;
```

### Оптимизация производительности

**Использование индексов для UPDATE / DELETE:**
```sql
-- Убедитесь, что в WHERE есть индексы
CREATE INDEX idx_products_manufacturer ON Products(Manufacturer);
-- Теперь этот UPDATE будет быстрее:
UPDATE Products SET Price = Price * 1.1 WHERE Manufacturer = 'Samsung';
```

**Минимизация блокировок:**
```sql
-- Используйте LIMIT для постепенного обновления
UPDATE Products
SET Price = Price * 1.1
WHERE Id IN (
    SELECT Id FROM Products
    WHERE Price < 1000
    LIMIT 100
);
```

### Обработка конфликтов

**UPSERT с `ON CONFLICT`:**
```sql
-- Вставка с обновлением при конфликте
INSERT INTO Products (Id, ProductName, Manufacturer, Price)
VALUES (1, 'New Product', 'M1', 1000)
ON CONFLICT (Id)
DO UPDATE SET
    ProductName = EXCLUDED.ProductName,
    Price = EXCLUDED.Price,
    UpdatedAt = now();

-- Вставка без обновления при конфликте
INSERT INTO Products (Id, ProductName, Manufacturer, Price)
VALUES (1, 'New Product', 'M1', 1000)
ON CONFLICT (Id) DO NOTHING;

-- ON CONFLICT с несколькими столбцами
INSERT INTO Products (ProductName, Manufacturer, Price)
VALUES ('Product', 'M1', 1000)
ON CONFLICT (ProductName, Manufacturer)
DO UPDATE SET Price = EXCLUDED.Price;
```

### Получение информации об операциях

**Проверка количества затронутых строк:**
```sql
-- В psql можно использовать GET DIAGNOSTICS
DO $$
DECLARE
    affected_rows INTEGER;
BEGIN
    UPDATE Products SET Price = Price * 1.1 WHERE Manufacturer = 'Samsung';
    GET DIAGNOSTICS affected_rows = ROW_COUNT;
    RAISE NOTICE 'Обновлено строк: %', affected_rows;
END $$;
```

**Использование `RETURNING` для отладки:**
```sql
-- Посмотреть, какие строки будут удалены
DELETE FROM Products
WHERE Price < 100
RETURNING Id, ProductName, Price;
```

### Безопасность и валидация

**Проверка перед обновлением:**
```sql
-- Проверить, что обновление не нарушит ограничения
BEGIN;
UPDATE Products SET Price = -100 WHERE Id = 1;
-- Если есть CHECK ограничение, будет ошибка
ROLLBACK; -- Откатить, если ошибка
```

**Транзакции для безопасности:**
```sql
BEGIN;
-- Критические операции
UPDATE Orders SET Status = 'paid' WHERE Id = 1;
UPDATE Users SET Balance = Balance - 100 WHERE Id = 1;
-- Проверка результата
SELECT * FROM Users WHERE Id = 1;
-- Если все ОК
COMMIT;
-- Если нет
-- ROLLBACK;
```

## Лучшие практики

1. **Всегда используйте WHERE** (кроме случаев, когда действительно нужно обновить все строки).
2. **Проверяйте количество затронутых строк** перед финализацией транзакции.
3. **Используйте транзакции** для критических операций.
4. **Делайте бэкапы** перед массовыми операциями.
5. **Используйте RETURNING** для получения измененных данных.
6. **Оптимизируйте запросы** — добавляйте индексы на столбцы в **WHERE**.
7. **Избегайте долгих транзакций** — делайте операции пакетами.
8. **Используйте COPY** для массовых вставок вместо множественных **INSERT**.

## См. также

- [PostgreSQL: администрирование и обслуживание](postgres-admin.md)
- [PostgreSQL: Резервное копирование и восстановление](postgres-backup-restore.md)
- [PostgreSQL: Полное руководство по основам и мониторингу](postgres-basics.md)
- [PostgreSQL: проектирование и нормализация](postgres-design.md)
- [PostgreSQL: Расширения](postgres-extensions.md)
