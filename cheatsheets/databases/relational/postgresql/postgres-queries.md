---
title: "PostgreSQL: запросы и агрегаты"
description: "Кратко: выборка, сортировка, пагинация, агрегаты, группировка и подзапросы."
tags:
  - databases
  - relational
  - postgres-queries
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# PostgreSQL: запросы и агрегаты

Кратко: выборка, сортировка, пагинация, агрегаты, группировка и подзапросы.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)

### Обучающие материалы

- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)


См. также: [postgres-data-ops](postgres-data-ops.md) — [postgres-joins](postgres-joins.md) — [postgres-indexes](postgres-indexes.md).

## Содержание

- [DISTINCT](#distinct)
- [ORDER BY](#order-by)
- [LIMIT / OFFSET](#limit-offset)
- [Фильтрация](#фильтрация)
- [Агрегаты и группировка](#агрегаты-и-группировка)
- [Подзапросы](#подзапросы)
- [Оконные функции](#оконные-функции)
- [Фильтры по времени](#фильтры-по-времени)
- [CTE (WITH)](#cte-with)
- [JOIN порядок и оптимизация](#join-порядок-и-оптимизация)
- [Параллельные запросы](#параллельные-запросы)
- [Статистика и селективность](#статистика-и-селективность)
- [Подробное описание DISTINCT](#подробное-описание-distinct)
  - [Базовое использование DISTINCT](#базовое-использование-distinct)
  - [DISTINCT с несколькими столбцами](#distinct-с-несколькими-столбцами)
  - [DISTINCT ON (только PostgreSQL)](#distinct-on-только-postgresql)
- [Подробное описание ORDER BY](#подробное-описание-order-by)
  - [Базовое использование ORDER BY](#базовое-использование-order-by)
  - [Сортировка по нескольким столбцам](#сортировка-по-нескольким-столбцам)
  - [Сортировка по выражениям](#сортировка-по-выражениям)
  - [NULL значения в сортировке](#null-значения-в-сортировке)
- [Подробное описание LIMIT и OFFSET](#подробное-описание-limit-и-offset)
  - [Базовое использование LIMIT](#базовое-использование-limit)
  - [Использование OFFSET](#использование-offset)
  - [Keyset Pagination (рекомендуется)](#keyset-pagination-рекомендуется)
- [Подробное описание операторов фильтрации](#подробное-описание-операторов-фильтрации)
  - [Оператор IN](#оператор-in)
  - [Оператор BETWEEN](#оператор-between)
  - [Оператор LIKE](#оператор-like)
- [Подробное описание агрегатных функций](#подробное-описание-агрегатных-функций)
  - [Основные агрегатные функции](#основные-агрегатные-функции)
  - [Примеры использования агрегатных функций](#примеры-использования-агрегатных-функций)
- [Подробное описание GROUP BY и HAVING](#подробное-описание-group-by-и-having)
  - [Базовое использование GROUP BY](#базовое-использование-group-by)
  - [Группировка по нескольким столбцам](#группировка-по-нескольким-столбцам)
  - [Использование HAVING](#использование-having)
  - [Комбинирование WHERE и HAVING](#комбинирование-where-и-having)
  - [Расширенные возможности группировки](#расширенные-возможности-группировки)
- [Подробное описание подзапросов](#подробное-описание-подзапросов)
  - [Скалярные подзапросы](#скалярные-подзапросы)
  - [Подзапросы в INSERT](#подзапросы-в-insert)
  - [Коррелирующие подзапросы](#коррелирующие-подзапросы)
  - [Подзапросы в FROM](#подзапросы-в-from)
  - [Подзапросы с EXISTS и NOT EXISTS](#подзапросы-с-exists-и-not-exists)
- [Дополнительные техники запросов](#дополнительные-техники-запросов)
  - [Оконные функции (Window Functions)](#оконные-функции-window-functions)
  - [Common Table Expressions (CTE)](#common-table-expressions-cte)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [См. также](#см-также)

## DISTINCT

```sql
-- Уникальные значения столбца city в таблице authors
SELECT DISTINCT city FROM authors;
```

## ORDER `BY`
```sql
-- Сортировка по городу по возрастанию, по имени по убыванию
SELECT * FROM authors
ORDER BY city ASC, name DESC;
```

## LIMIT / OFFSET
```sql
-- Выборка с пагинацией: 10 записей, начиная с 21-й
SELECT * FROM authors
ORDER BY id
LIMIT 10 OFFSET 20;
```

## Фильтрация
- Базовые операторы: `=`, `<>`, `<`, `>`, **BETWEEN**, **LIKE**, `IN`, `IS NULL`.
- **Пример:**
```sql
-- Фильтрация заказов: оплаченные, сумма > 100, за последние 30 дней
SELECT * FROM orders
WHERE status = 'PAID'
  AND total_amount > 100
  AND created_at >= now() - interval '30 days';
```

## Агрегаты и группировка
```sql
-- Подсчёт и суммы по статусу заказа с фильтром HAVING
SELECT status,
       COUNT(*)        AS cnt,
       SUM(total_amount) AS total_sum,
       AVG(total_amount) AS avg_sum
FROM orders
GROUP BY status
HAVING COUNT(*) > 10
ORDER BY cnt DESC;
```
- Частые агрегаты: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`.
- `HAVING` фильтрует уже сгруппированные данные.

## Подзапросы
```sql
-- Заказы с суммой выше средней по всем заказам
SELECT *
FROM orders o
WHERE total_amount > (
  SELECT AVG(total_amount)
  FROM orders
);
```
- Подзапросы могут быть скалярными, табличными, использоваться в `WHERE`, `FROM`, `SELECT`.

## Оконные функции
- **Ранжирование:**
```sql
-- Нумерация заказов пользователя по убыванию суммы
SELECT id, user_id, total_amount,
       ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY total_amount DESC) AS rn
FROM orders;
```
- **Скользящие суммы/средние:**
```sql
-- Скользящая сумма по последним 4 строкам для каждой даты
SELECT created_at, total_amount,
       SUM(total_amount) OVER (ORDER BY created_at
                               ROWS BETWEEN 3 PRECEDING AND CURRENT ROW) AS sum_last_4
FROM orders;
```

## Фильтры по времени
- **Последние N дней:**
```sql
-- Заказы за последние 30 дней
SELECT * FROM orders
WHERE created_at >= now() - interval '30 days';
```
- **По дате без времени:**
```sql
-- Условие по текущей дате (без времени)
WHERE created_at::date = current_date
```

## CTE (WITH)
- **Улучшает читаемость сложных запросов:**
```sql
-- Пользователи с общей суммой заказов > 1000 через CTE
WITH top_users AS (
  SELECT user_id, SUM(total_amount) AS total
  FROM orders
  GROUP BY user_id
  HAVING SUM(total_amount) > 1000
)
SELECT u.id, u.name, t.total
FROM users u
JOIN top_users t ON t.user_id = u.id;
```
- Помните: в старых версиях **PostgreSQL CTE** материализуются, что может замедлять; в новых (12+) **planner** может встраивать (inlining).

## JOIN порядок и оптимизация
- **Planner** может менять порядок **JOIN**, если нет **JOIN LATERAL** / **ORDER BY** / **LIMIT** внутри.
- Подсказки (неофициальные) доступны через `enable_hashjoin`, `enable_mergejoin`, `enable_nestloop` (временно отключать для диагностики).
- Составные индексы по условиям соединения и фильтрам помогают уйти от **Hash Join** на большие таблицы.

## Параллельные запросы
- Включите параллелизм: `max_parallel_workers_per_gather > 0`.
- Признаки параллельного плана: узлы `Gather`, `Parallel Seq Scan`.
- Параллелизм полезен на больших таблицах с фильтрацией/агрегатами; может не помочь на маленьких.

## Статистика и селективность
- **Если план ошибается по **cardinality**:**
```sql
-- Обновление статистики и увеличение точности по колонке status
ANALYZE my_table;
ALTER TABLE my_table ALTER COLUMN status SET STATISTICS 200;
```
- **Для сложных выражений используйте **expression indexes** и `CREATE STATISTICS (ndistinct, dependencies)` на несколько колонок (v10+):**
```sql
-- Расширенная статистика по паре колонок для лучших планов
CREATE STATISTICS st_orders_status_created ON status, created_at FROM orders;
ANALYZE orders;
```

## Подробное описание DISTINCT

### Базовое использование DISTINCT

Оператор `DISTINCT` позволяет выбрать уникальные данные по определенным столбцам.

**Пример таблицы:**
```sql
-- Таблица товаров для примеров с DISTINCT
CREATE TABLE Products (
    Id SERIAL PRIMARY KEY,
    ProductName VARCHAR(30) NOT NULL,
    Manufacturer VARCHAR(20) NOT NULL,
    ProductCount INTEGER DEFAULT 0,
    Price NUMERIC
);

INSERT INTO Products (ProductName, Manufacturer, ProductCount, Price)
VALUES
('iPhone X', 'Apple', 2, 71000),
('iPhone 8', 'Apple', 3, 56000),
('Galaxy S9', 'Samsung', 6, 56000),
('Galaxy S8 Plus', 'Samsung', 2, 46000),
('Desire 12', 'HTC', 3, 26000);
```

**Выберем всех производителей:**
```sql
-- Список уникальных производителей
SELECT DISTINCT Manufacturer FROM Products;
```

### DISTINCT с несколькими столбцами

**Уникальные комбинации столбцов:**
```sql
-- Уникальные пары производитель–количество
SELECT DISTINCT Manufacturer, ProductCount
FROM Products;
```

Это вернет уникальные комбинации производителя и количества товаров.

### DISTINCT `ON` (только PostgreSQL)

`DISTINCT ON` позволяет выбрать первую строку для каждой уникальной комбинации указанных столбцов. Требует **ORDER** `BY`.

**Пример:**
```sql
-- Получить самый дорогой товар каждого производителя
SELECT DISTINCT ON (Manufacturer)
    Manufacturer, ProductName, Price
FROM Products
ORDER BY Manufacturer, Price DESC;
```

## Подробное описание ORDER `BY`

### Базовое использование ORDER `BY`

Оператор `ORDER BY` позволяет отсортировать значения по определенному столбцу.

**Пример сортировки:**
```sql
-- Сортировка по количеству товара по возрастанию
SELECT * FROM Products
ORDER BY ProductCount;
```

### Сортировка по нескольким столбцам

**Сортировка по нескольким столбцам:**
```sql
-- Сначала по производителю, затем по названию
SELECT ProductName, Price, Manufacturer
FROM Products
ORDER BY Manufacturer, ProductName;
```

В этом случае сначала строки сортируются по столбцу `Manufacturer` по возрастанию. Затем если есть две строки, в которых столбец `Manufacturer` имеет одинаковое значение, то они сортируются по столбцу `ProductName` также по возрастанию.

**Комбинирование `ASC` и `DESC`:**
```sql
-- Производитель по возрастанию, название по убыванию
SELECT ProductName, Price, Manufacturer
FROM Products
ORDER BY Manufacturer ASC, ProductName DESC;
```

### Сортировка по выражениям

**Сортировка по вычисляемому выражению:**
```sql
-- Сортировка по произведению количества на цену
SELECT ProductName, Price, ProductCount
FROM Products
ORDER BY ProductCount * Price;
```

**Сортировка по псевдониму столбца:**
```sql
-- Упорядочивание по вычисляемому псевдониму TotalSum
SELECT ProductName, ProductCount * Price AS TotalSum
FROM Products
ORDER BY TotalSum;
```

Также можно производить упорядочивание данных по псевдониму столбца, который определяется с помощью оператора `AS`.

**По умолчанию используется `ASC` (по возрастанию):**
```sql
SELECT ProductName, Manufacturer
FROM Products
ORDER BY Manufacturer ASC;  -- Явно указывать ASC не обязательно
```

**По убыванию:**
```sql
-- Сортировка производителя по убыванию
SELECT ProductName, Manufacturer
FROM Products
ORDER BY Manufacturer DESC;
```

### NULL значения в сортировке

**Управление положением `NULL`:**
```sql
-- NULL в конце (по умолчанию)
SELECT * FROM Products
ORDER BY Price NULLS LAST;

-- NULL в начале
SELECT * FROM Products
ORDER BY Price NULLS FIRST;
```

## Подробное описание LIMIT и OFFSET

### Базовое использование LIMIT

**Оператор `LIMIT` позволяет извлечь определенное количество строк:**

```sql
-- Первые 4 товара по названию
SELECT * FROM Products
ORDER BY ProductName
LIMIT 4;
```

### Использование OFFSET

**Оператор `OFFSET` позволяет указать, с какой строки надо начинать выборку. Например, выберем 3 строки, начиная со 2-й:**

```sql
-- Три строки, пропустив первые две
SELECT * FROM Products
ORDER BY ProductName
LIMIT 3 OFFSET 2;
```

**Если нам надо выбрать вообще все строки, начиная с какой-то определенной, то оператор `LIMIT` можно опустить:**
```sql
-- Все строки, кроме первых двух
SELECT * FROM Products
ORDER BY ProductName
OFFSET 2;
```

**Либо после `LIMIT` указать ключевое слово `ALL`:**
```sql
-- Эквивалент только OFFSET: все строки с пропуском двух
SELECT * FROM Products
ORDER BY ProductName
LIMIT ALL OFFSET 2;
```

### Keyset Pagination (рекомендуется)

**Классика **LIMIT**/**OFFSET** плохо масштабируется на большие **OFFSET** (PostgreSQL должен пропустить все предыдущие строки). Используйте **keyset pagination**:**

```sql
-- Первая страница
SELECT * FROM orders
WHERE created_at >= '2024-01-01'
ORDER BY created_at DESC, id DESC
LIMIT 50;

-- Следующая страница (используя последние значения с предыдущей страницы)
SELECT * FROM orders
WHERE created_at >= '2024-01-01'
  AND (created_at, id) < ('2024-01-15 10:30:00', 12345)
ORDER BY created_at DESC, id DESC
LIMIT 50;
```

**Преимущества keyset pagination:**
- Постоянная производительность независимо от позиции в результатах
- Работает эффективно даже для миллионных записей
- Не пропускает строки при добавлении новых данных во время пагинации

## Подробное описание операторов фильтрации

### Оператор `IN`

**Оператор `IN` позволяет определить набор значений, которые должны иметь столбцы:**

```sql
WHERE выражение [NOT] IN (выражение)
```

Выражение в скобках после `IN` определяет набор значений. Этот набор может вычисляться динамически на основании, например, еще одного запроса, либо это могут быть константные значения.

**Пример:**
```sql
SELECT * FROM Products
WHERE Manufacturer IN ('Samsung', 'HTC', 'Huawei');
```

**В качестве альтернативы можно было бы проверить все эти значения через оператор `OR`:**
```sql
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' OR Manufacturer = 'HTC' OR Manufacturer = 'Huawei';
```

Однако использование оператора `IN` гораздо удобнее, особенно если подобных значений очень много.

**IN с подзапросом:**
```sql
SELECT * FROM Products
WHERE Manufacturer IN (
    SELECT DISTINCT Company FROM Suppliers
);
```

**NOT `IN`:**
```sql
SELECT * FROM Products
WHERE Manufacturer NOT IN ('Samsung', 'HTC', 'Huawei');
```

**Важно:** `NOT IN` с подзапросом может вернуть неожиданные результаты, если подзапрос возвращает **NULL**. Используйте `NOT EXISTS` в таких случаях.

### Оператор BETWEEN

**Оператор `BETWEEN` определяет диапазон значений с помощью начального и конечного значения:**

```sql
WHERE выражение [NOT] BETWEEN начальное_значение AND конечное_значение
```

**Пример:**
```sql
SELECT * FROM Products
WHERE Price BETWEEN 20000 AND 50000;
```

Начальное и конечное значения также включаются в диапазон (включительно).

**NOT `BETWEEN`:**
```sql
SELECT * FROM Products
WHERE Price NOT BETWEEN 20000 AND 50000;
```

**BETWEEN с выражениями:**
```sql
SELECT * FROM Products
WHERE Price * ProductCount BETWEEN 90000 AND 150000;
```

**BETWEEN с датами:**
```sql
SELECT * FROM Orders
WHERE created_at BETWEEN '2024-01-01' AND '2024-01-31 23:59:59';
```

### Оператор LIKE

**Оператор `LIKE` принимает шаблон строки, которому должно соответствовать выражение:**

```sql
WHERE выражение [NOT] LIKE шаблон_строки
```

**Для определения шаблона могут применяться ряд специальных символов подстановки:**

1. **%**: соответствует любой подстроке, которая может иметь любое количество символов, при этом подстрока может и не содержать ни одного символа. Например, выражение `WHERE ProductName LIKE 'Galaxy%'` соответствует таким значениям как "**Galaxy Ace** 2" или "**Galaxy S7**".

2. **\_**: соответствует любому одиночному символу. Например, выражение `WHERE ProductName LIKE 'Galaxy S_'` соответствует таким значениям как "**Galaxy S7**" или "**Galaxy S8**".

**Примеры:**
```sql
-- Поиск с ведущим паттерном (может использовать индекс)
SELECT * FROM Products
WHERE ProductName LIKE 'iPhone%';

-- Поиск с завершающим паттерном (НЕ использует индекс!)
SELECT * FROM Products
WHERE ProductName LIKE '%S9';

-- Поиск с ведущим и завершающим паттерном
SELECT * FROM Products
WHERE ProductName LIKE '%Galaxy%';

-- Поиск с одним символом
SELECT * FROM Products
WHERE ProductName LIKE 'Galaxy S_';
```

**ILIKE (case-insensitive):**
```sql
SELECT * FROM Products
WHERE ProductName ILIKE 'iphone%';
```

**ESCAPE для специальных символов:**
```sql
-- Поиск символов % или _
SELECT * FROM Products
WHERE ProductName LIKE '%\%%' ESCAPE '\';
SELECT * FROM Products
WHERE ProductName LIKE '%\_%' ESCAPE '\';
```

**Для полнотекстового поиска используйте триграммы:**
```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_products_name_trgm ON products USING GIN (productname gin_trgm_ops);

SELECT * FROM Products
WHERE ProductName LIKE '%Galaxy%';  -- Будет использовать GIN индекс
```

## Подробное описание агрегатных функций

### Основные агрегатные функции

**Агрегатные функции вычисляют одно значение над некоторым набором строк. В **PostgreSQL** имеются следующие агрегатные функции:**

1. **AVG**: находит среднее значение. Входной параметр должен представлять один из следующих типов: **smallint**, **int**, **bigint**, **real**, **double precision**, **numeric**, **interval**. Для целочисленных параметров результатом будет значение типа **numeric**, для параметров, которые представляют число с плавающей точкой, — значение типа **double precision**.

2. **BIT_AND**: выполняет операцию побитового умножения (операции логического И) для чисел следующих типов: **smallint**, **int**, **bigint**, **bit**. Если параметр содержит значение **NULL**, то возвращается **NULL**.

3. **BIT_OR**: выполняет операцию побитового сложения (операции логического ИЛИ) для чисел следующих типов: **smallint**, **int**, **bigint**, **bit**. Если параметр содержит значение **NULL**, то возвращается **NULL**.

4. **BOOL_AND**: выполняет операцию логического умножения для значений типа **bool**. Если входные все значения равны **true**, то возвращается **true**, иначе возвращается **false**.

5. **BOOL_OR**: выполняет операцию логического сложения для значений типа **bool**. Если входные хотя бы одно из значений равно **true**, то возвращается **true**, иначе возвращается **false**.

6. **COUNT(*): находит количество строк в запросе.

7. **COUNT(expression)**: находит количество строк в запросе, для которых **expression** не содержит значение **NULL**.

8. **SUM**: находит сумму значений.

9. **MIN**: находит наименьшее значение.

10. **MAX**: находит наибольшее значение.

11. **STRING_AGG(expression, delimiter)**: соединяет с помощью **delimiter** все текстовые значения из **expression** в одну строку.

В качестве параметра все агрегатные функции принимают выражение, которое представляет критерий для определения значений. Зачастую, в качестве выражения выступает название столбца, над значениями которого надо проводить вычисления.

**Если в наборе нет строк, то все агрегатные функции за исключением `COUNT (*)` возвращают значение **NULL**.**

### Примеры использования агрегатных функций

**Пример таблицы:**
```sql
CREATE TABLE Products (
    Id SERIAL PRIMARY KEY,
    ProductName VARCHAR(30) NOT NULL,
    Company VARCHAR(20) NOT NULL,
    ProductCount INT DEFAULT 0,
    Price NUMERIC NOT NULL,
    IsDiscounted BOOL
);

INSERT INTO Products (ProductName, Company, ProductCount, Price, IsDiscounted)
VALUES
('iPhone X', 'Apple', 3, 76000, false),
('iPhone 8', 'Apple', 2, 71000, true),
('iPhone 7', 'Apple', 5, 42000, true),
('Galaxy S9', 'Samsung', 2, 46000, false),
('Galaxy S8 Plus', 'Samsung', 1, 56000, true),
('Desire 12', 'HTC', 5, 28000, true),
('Nokia 9', 'HMD Global', 6, 38000, true);
```

**AVG (среднее значение):**
```sql
-- Средняя цена товаров
SELECT AVG(Price) AS Average_Price FROM Products;

-- Средняя цена для определенного производителя
SELECT AVG(Price) FROM Products
WHERE Company='Apple';

-- Среднее значение для сложного выражения
SELECT AVG(Price * ProductCount) FROM Products;
```

**COUNT (количество):**
```sql
-- Количество всех строк
SELECT COUNT(*) FROM Products;

-- Количество строк по столбцу (игнорирует NULL)
SELECT COUNT(Price) FROM Products;

-- Количество уникальных значений
SELECT COUNT(DISTINCT Company) FROM Products;
```

**MIN и `MAX` (минимальное и максимальное):**
```sql
-- Минимальная цена
SELECT MIN(Price) FROM Products;

-- Максимальная цена
SELECT MAX(Price) FROM Products;

-- MIN и MAX игнорируют NULL значения
```

**SUM (сумма):**
```sql
-- Общее количество товаров
SELECT SUM(ProductCount) FROM Products;

-- Общая стоимость всех товаров
SELECT SUM(ProductCount * Price) FROM Products;
```

**BOOL_AND и BOOL_OR:**
```sql
-- Есть ли товары со скидкой (хотя бы один)
SELECT BOOL_OR(IsDiscounted) FROM Products;

-- Все ли товары со скидкой
SELECT BOOL_AND(IsDiscounted) FROM Products;
```

**STRING_AGG (объединение строк):**
```sql
-- Объединить названия всех товаров
SELECT STRING_AGG(ProductName, ', ') FROM Products;

-- Объединить уникальных производителей
SELECT STRING_AGG(DISTINCT Company, ', ') FROM Products;
-- Результат: Apple, HMD Global, HTC, Samsung

-- Объединить с сортировкой
SELECT STRING_AGG(ProductName, ', ' ORDER BY ProductName) FROM Products;
```

**Комбинирование агрегатных функций:**
```sql
SELECT
    COUNT(*) AS ProdCount,
    SUM(ProductCount) AS TotalCount,
    MIN(Price) AS MinPrice,
    MAX(Price) AS MaxPrice,
    AVG(Price) AS AvgPrice
FROM Products;
```

## Подробное описание GROUP `BY` и HAVING

### Базовое использование GROUP `BY`

**Для группировки данных в **PostgreSQL** применяются операторы `GROUP BY` и `HAVING`. Формальный синтаксис:**

```sql
SELECT столбцы
FROM таблица
[WHERE условие_фильтрации_строк]
[GROUP BY столбцы_для_группировки]
[HAVING условие_фильтрации_групп]
[ORDER BY столбцы_для_сортировки]
```

Оператор `GROUP BY` определяет, как строки будут группироваться.

**Пример:**
```sql
SELECT Company, COUNT(*) AS ModelsCount
FROM Products
GROUP BY Company;
```

Первый столбец в выражении `SELECT` — `Company` представляет название группы, а второй столбец — `ModelsCount` представляет результат функции `Count`, которая вычисляет количество строк в группе.

**Важно:** Стоит учитывать, что любой столбец, который используется в выражении `SELECT` (не считая столбцов, которые хранят результат агрегатных функций), должны быть указаны после оператора `GROUP BY`. Так, например, в случае выше столбец `Company` указан и в выражении `SELECT`, и в выражении `GROUP BY`.

**Если в выражении `SELECT` производится выборка по одному или нескольким столбцам и также используются агрегатные функции, то необходимо использовать выражение `GROUP BY`.**

**Следующий пример работать не будет, так как он не содержит выражение группировки:**
```sql
SELECT Company, COUNT(*) AS ModelsCount
FROM Products;  -- ОШИБКА! Нужен GROUP BY
```

### Группировка по нескольким столбцам

**Группировка по нескольким столбцам:**
```sql
SELECT Company, ProductCount, COUNT(*) AS ModelsCount
FROM Products
GROUP BY Company, ProductCount;
```

Оператор `GROUP BY` может выполнять группировку по множеству столбцов.

**Если столбец, по которому производится группировка, содержит значение `NULL`, то строки со значением `NULL` составят отдельную группу.**

### Использование HAVING

Оператор `HAVING` указывает, какие группы будут включены в выходной результат, то есть выполняет фильтрацию групп. Его использование аналогично применению оператора `WHERE`.

**Пример:**
```sql
SELECT Company, COUNT(*) AS ModelsCount
FROM Products
GROUP BY Company
HAVING COUNT(*) > 1;
```

Сгруппируем по производителям и найдем все группы, для которых определено более 1 модели.

### Комбинирование WHERE и HAVING

**При этом в одной команде мы можем использовать выражения `WHERE` и `HAVING`:**
```sql
SELECT Company, COUNT(*) AS ModelsCount
FROM Products
WHERE Price * ProductCount > 80000
GROUP BY Company
HAVING COUNT(*) > 1;
```

То есть в данном случае сначала фильтруются строки: выбираются те товары, общая стоимость которых больше `80000`. Затем выбранные товары группируются по производителям. И далее фильтруются сами группы — выбираются те группы, которые содержат больше 1 модели.

**Стоит учитывать, что выражение `GROUP BY` должно идти после выражения `WHERE`, но до выражения `ORDER BY`.**

**Если при этом необходимо провести сортировку, то выражение `ORDER BY` идет после выражения `HAVING`:**
```sql
SELECT Company, COUNT(*) AS Models, SUM(ProductCount) AS Units
FROM Products
WHERE Price * ProductCount > 80000
GROUP BY Company
HAVING SUM(ProductCount) > 2
ORDER BY Units DESC;
```

### Расширенные возможности группировки

**GROUPING `SETS`:**
```sql
SELECT Company, COUNT(*) AS Models, ProductCount
FROM Products
GROUP BY GROUPING SETS(Company, ProductCount);
```

Оператор `GROUPING SETS` группирует полученные наборы отдельно. В выражении `SELECT` производится выборка компаний, количества моделей и количества товаров. Оператор `GROUPING SETS` производит группировку по двум столбцам — `Company` и `ProductCount`. В итоге будет создаваться две группы: 1) компании и количество моделей и 2) количество моделей и количество товаров.

**ROLLUP:**
```sql
SELECT Company, COUNT(*) AS Models, SUM(ProductCount) AS Units
FROM Products
GROUP BY ROLLUP(Company);
```

Оператор `ROLLUP` добавляет суммирующую строку в результирующий набор. В конце таблицы будет добавлена дополнительная строка, которая суммирует значение столбцов.

**При группировке по нескольким критериям `ROLLUP` будет создавать суммирующую строку для каждой из подгрупп:**
```sql
SELECT Company, COUNT(*) AS Models, SUM(ProductCount) AS Units
FROM Products
GROUP BY ROLLUP(Company, ProductCount)
ORDER BY Company;
```

**CUBE:**
```sql
SELECT Company, COUNT(*) AS Models, SUM(ProductCount) AS Units
FROM Products
GROUP BY CUBE(Company, ProductCount);
```

`CUBE` похож на `ROLLUP` за тем исключением, что `CUBE` добавляет суммирующие строки для каждой комбинации групп.

## Подробное описание подзапросов

Подзапросы (subquery) представляют такие запросы, которые могут быть встроены в другие запросы.

**Пример таблиц:**
```sql
CREATE TABLE Products (
    Id SERIAL PRIMARY KEY,
    ProductName VARCHAR(30) NOT NULL,
    Company VARCHAR(20) NOT NULL,
    ProductCount INTEGER DEFAULT 0,
    Price NUMERIC NOT NULL
);

CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName VARCHAR(30) NOT NULL
);

CREATE TABLE Orders (
    Id SERIAL PRIMARY KEY,
    ProductId INTEGER NOT NULL REFERENCES Products(Id) ON DELETE CASCADE,
    CustomerId INTEGER NOT NULL REFERENCES Customers(Id) ON DELETE CASCADE,
    CreatedAt DATE NOT NULL,
    ProductCount INTEGER DEFAULT 1,
    Price NUMERIC NOT NULL
);
```

### Скалярные подзапросы

**Скалярные подзапросы возвращают одно значение:**
```sql
-- Товары с минимальной ценой
SELECT *
FROM Products
WHERE Price = (SELECT MIN(Price) FROM Products);

-- Товары, цена которых выше средней
SELECT *
FROM Products
WHERE Price > (SELECT AVG(Price) FROM Products);
```

### Подзапросы в INSERT

**При добавлении данных в таблицу `Orders` могут использоваться подзапросы:**
```sql
INSERT INTO Orders(ProductId, CustomerId, CreatedAt, ProductCount, Price)
VALUES
(
    (SELECT Id FROM Products WHERE ProductName='Galaxy S9'),
    (SELECT Id FROM Customers WHERE FirstName='Tom'),
    '2017-07-11',
    2,
    (SELECT Price FROM Products WHERE ProductName='Galaxy S9')
);
```

Подзапрос представляет команду `SELECT` и заключается в скобки. В данном случае при добавлении одного товара выполняется три подзапроса. Каждый подзапрос возвращает одно скалярное значение, например, идентификатор товара или покупателя.

### Коррелирующие подзапросы

**В примерах выше команды `SELECT` выполняли фактически один подзапрос для всей команды.** Такой подзапрос выполняется один раз для всего внешнего запроса.

**Но кроме того есть коррелирующие подзапросы (correlated subquery), результаты которых зависят от строк, которые извлекаются в основном запросе.**

**Пример:**
```sql
SELECT CreatedAt,
       Price,
       (SELECT ProductName FROM Products
        WHERE Products.Id = Orders.ProductId) AS Product
FROM Orders;
```

Здесь для каждой строки из таблицы `Orders` будет выполняться подзапрос, результат которого зависит от столбца `ProductId`. И каждый подзапрос может возвращать различные данные.

**Коррелирующий подзапрос может выполняться и для той же таблицы:**
```sql
SELECT ProductName,
       Company,
       Price,
       (SELECT AVG(Price) FROM Products AS SubProds
        WHERE SubProds.Company=Prods.Company) AS AvgPrice
FROM Products AS Prods
WHERE Price >
    (SELECT AVG(Price) FROM Products AS SubProds
     WHERE SubProds.Company=Prods.Company);
```

В данном случае определено два коррелирующих подзапроса. Первый подзапрос определяет спецификацию столбца `AvgPrice`. Он будет выполняться для каждой строки, извлекаемой из таблицы `Products`. В подзапрос передается производитель товара и на его основе выбирается средняя цена для товаров именно этого производителя.

Второй подзапрос аналогичен, только он используется для фильтрации извлекаемых из таблицы `Products`. И также он будет выполняться для каждой строки.

**Чтобы избежать двойственности при фильтрации в подзапросе при сравнении производителей (`SubProds.Company=Prods.Company`) для внешней выборки установлен псевдоним `Prods`, а для выборки из подзапросов определен псевдоним `SubProds`.**

### Подзапросы в FROM

**Подзапросы могут использоваться в `FROM` как таблицы:**
```sql
SELECT sub.*
FROM (
    SELECT Company, COUNT(*) AS cnt
    FROM Products
    GROUP BY Company
) AS sub
WHERE sub.cnt > 2;
```

### Подзапросы с EXISTS и NOT EXISTS

**EXISTS проверяет существование строк:**
```sql
-- Товары, которые есть в заказах
SELECT *
FROM Products p
WHERE EXISTS (
    SELECT 1 FROM Orders o
    WHERE o.ProductId = p.Id
);
```

**NOT `EXISTS` проверяет отсутствие строк:**
```sql
-- Товары, которых нет в заказах
SELECT *
FROM Products p
WHERE NOT EXISTS (
    SELECT 1 FROM Orders o
    WHERE o.ProductId = p.Id
);
```

**Важно:** `NOT EXISTS` работает правильно даже при наличии **NULL** в подзапросе, в отличие от `NOT IN`.

## Дополнительные техники запросов

### Оконные функции (Window Functions)

Оконные функции выполняют вычисления над набором строк, связанных с текущей строкой. В отличие от агрегатных функций, они не группируют строки в одну результирующую строку.

**Базовый синтаксис:**
```sql
function_name(expression) OVER (
    [PARTITION BY столбец1, столбец2, ...]
    [ORDER BY столбец1 [ASC|DESC], ...]
    [ROWS|RANGE BETWEEN ... AND ...]
)
```

**Примеры:**
```sql
-- Ранжирование
SELECT id, user_id, total_amount,
       ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY total_amount DESC) AS rn,
       RANK() OVER (PARTITION BY user_id ORDER BY total_amount DESC) AS rank,
       DENSE_RANK() OVER (PARTITION BY user_id ORDER BY total_amount DESC) AS dense_rank
FROM orders;

-- Скользящие суммы
SELECT created_at, total_amount,
       SUM(total_amount) OVER (
           ORDER BY created_at
           ROWS BETWEEN 3 PRECEDING AND CURRENT ROW
       ) AS sum_last_4
FROM orders;

-- LAG и LEAD (предыдущее и следующее значение)
SELECT created_at, total_amount,
       LAG(total_amount) OVER (ORDER BY created_at) AS prev_amount,
       LEAD(total_amount) OVER (ORDER BY created_at) AS next_amount
FROM orders;
```

### Common Table Expressions (CTE)

**CTE** улучшает читаемость сложных запросов:**

```sql
WITH top_users AS (
    SELECT user_id, SUM(total_amount) AS total
    FROM orders
    GROUP BY user_id
    HAVING SUM(total_amount) > 1000
)
SELECT u.id, u.name, t.total
FROM users u
JOIN top_users t ON t.user_id = u.id;
```

**Рекурсивные `CTE`:**
```sql
WITH RECURSIVE tree AS (
    -- Начальная часть
    SELECT id, name, parent_id, 1 AS level
    FROM categories
    WHERE parent_id IS NULL

    UNION ALL

    -- Рекурсивная часть
    SELECT c.id, c.name, c.parent_id, t.level + 1
    FROM categories c
    JOIN tree t ON c.parent_id = t.id
)
SELECT * FROM tree;
```

**Важно:** В **PostgreSQL** 12+ **planner** может встраивать (inline) **CTE** для оптимизации. В более старых версиях **CTE** могут материализоваться, что может замедлять запросы.

## Лучшие практики

- **Индексы:** используйте индексы для столбцов в `WHERE`, `JOIN`, `ORDER BY`; избегайте функций над столбцами в условиях.
- **LIMIT:** всегда ограничивайте выборку при пагинации; для больших смещений предпочтительны ключевые курсоры.
- **Агрегаты:** фильтруйте до группировки в `WHERE`, после — в `HAVING`; для тяжёлых агрегатов рассмотрите материализованные представления.
- **Оконные функции:** при больших партициях учитывайте память; при необходимости разбивайте запросы.
- **CTE:** в версиях до 12 учитывайте материализацию **CTE**; для рекурсии задавайте ограничение глубины.
- **EXPLAIN (ANALYZE):** проверяйте планы запросов на узких местах; следите за **Seq Scan** на больших таблицах.

## Решение проблем

**Медленные запросы:** используйте `EXPLAIN (ANALYZE, BUFFERS)` для анализа плана; добавляйте индексы под предикаты и сортировки. Избегайте Seq Scan по большим таблицам без фильтра. Сокращайте время транзакций и блокировок.

**Блокировки и дедлоки:** проверьте `pg_locks`, `pg_stat_activity` и логи. Сократите время удержания блокировок, придерживайтесь единого порядка блокировки таблиц. Для очередей используйте `SELECT ... FOR UPDATE SKIP LOCKED`.

**Ошибки памяти (out of memory / could not extend):** увеличьте `work_mem`, `maintenance_work_mem` при необходимости для тяжёлых сортировок и хэш-джойнов. Разбейте запрос на части или упростите агрегации. Проверьте настройки `temp_file_limit`.

**Неожиданные или пустые результаты:** проверьте условия в `WHERE` (NULL, типы, регистр), уровень изоляции транзакций и влияние видимости строк (MVCC). При чтении с реплики учитывайте задержку репликации.

## См. также

- [PostgreSQL: администрирование и обслуживание](postgres-admin.md)
- [PostgreSQL: Резервное копирование и восстановление](postgres-backup-restore.md)
- [PostgreSQL: Полное руководство по основам и мониторингу](postgres-basics.md)
- [PostgreSQL: операции с данными (CRUD)](postgres-data-ops.md)
- [PostgreSQL: проектирование и нормализация](postgres-design.md)
