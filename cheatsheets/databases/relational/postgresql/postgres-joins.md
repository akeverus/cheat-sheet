---
title: "PostgreSQL: соединения и операции над множествами"
description: "Кратко: примеры INNER/LEFT/RIGHT/FULL JOIN, а также UNION/EXCEPT/INTERSECT. Без лишнего Java-кода — только SQL."
tags:
  - databases
  - relational
  - postgres-joins
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **PostgreSQL**: соединения и операции над множествами

Кратко: примеры **INNER**/**LEFT**/**RIGHT**/**FULL JOIN**, а также **UNION**/**EXCEPT**/**INTERSECT**. Без лишнего **Java**-кода — только **SQL**.



## Полезные ссылки

### Официальная документация

- [`PostgreSQL Documentation`](https://www.postgresql.org/docs/)
- [`PostgreSQL Tutorial`](https://www.postgresql.org/docs/)

### **Baeldung**

- [`PostgreSQL Tutorial`](https://www.postgresql.org/docs/)


См. также: [`postgres-queries`](postgres-queries.md) — [`postgres-design`](postgres-design.md).

## Содержание

- [**PostgreSQL**: соединения и операции над множествами](#postgresql-соединения-и-операции-над-множествами)
- [Схема данных для примеров](#схема-данных-для-примеров)
- [**INNER JOIN**](#inner-join)
- [**LEFT JOIN** / **RIGHT JOIN**](#left-join-right-join)
- [**FULL JOIN**](#full-join)
- [**CROSS JOIN**](#cross-join)
- [Операции над множествами](#операции-над-множествами)
- [Группировка после **JOIN**](#группировка-после-join)
- [Паттерны и анти-паттерны](#паттерны-и-анти-паттерны)
- [Лучшие практики](#лучшие-практики)
- [План выполнения и индексы](#план-выполнения-и-индексы)
- [**USING**, **NATURAL** и работа с **NULL**](#using-natural-и-работа-с-null)
- [Полусоединения и анти-соединения](#полусоединения-и-анти-соединения)
- [**LATERAL** и **UNNEST**](#lateral-и-unnest)
- [**JOIN** + агрегации](#join-агрегации)
- [Многие-ко-многим и взрывы строк](#многие-ко-многим-и-взрывы-строк)
- [Практические кейсы](#практические-кейсы)
- [Отладка планов и настройки](#отладка-планов-и-настройки)
- [Подробное описание типов **JOIN**](#подробное-описание-типов-join)
  - [Неявное соединение таблиц](#неявное-соединение-таблиц)
- [Подробное описание **INNER JOIN**](#подробное-описание-inner-join)
- [Подробное описание **OUTER JOIN**](#подробное-описание-outer-join)
  - [**LEFT JOIN**](#left-join)
  - [**RIGHT JOIN**](#right-join)
- [Подробное описание **CROSS JOIN**](#подробное-описание-cross-join)
- [Подробное описание операций над множествами](#подробное-описание-операций-над-множествами)
  - [**UNION** (**объединение**)](#union-объединение)
  - [**EXCEPT** (**разность**)](#except-разность)
  - [**INTERSECT** (**пересечение**)](#intersect-пересечение)
- [Комбинирование различных типов **JOIN**](#комбинирование-различных-типов-join)
  - [Множественные **JOIN**](#множественные-join)
- [Дополнительные техники **JOIN**](#дополнительные-техники-join)
  - [**NATURAL JOIN** (**не рекомендуется**)](#natural-join-не-рекомендуется)
  - [Условия соединения с множественными столбцами](#условия-соединения-с-множественными-столбцами)
  - [Оптимизация **JOIN** с индексами](#оптимизация-join-с-индексами)
  - [Оптимизация больших **JOIN**](#оптимизация-больших-join)
- [Расширенные примеры **JOIN**](#расширенные-примеры-join)
  - [Иерархические запросы (**рекурсивные JOIN**)](#иерархические-запросы-рекурсивные-join)
  - [Само-соединения (**Self-Joins**)](#само-соединения-self-joins)
  - [Оптимизация производительности **JOIN**](#оптимизация-производительности-join)
  - [Типичные проблемы и решения **JOIN**](#типичные-проблемы-и-решения-join)

## Схема данных для примеров

```sql
-- Таблицы author и article для примеров JOIN
CREATE TABLE author (
  id   INT PRIMARY KEY,
  first_name TEXT,
  last_name  TEXT
);

CREATE TABLE article (
  id   INT PRIMARY KEY,
  title TEXT NOT NULL,
  author_id INT REFERENCES author(id)
);
```

## **INNER JOIN**
Выбирает пересечение.
```sql
SELECT a.title, au.first_name, au.last_name
FROM article a
INNER JOIN author au ON au.id = a.author_id;
```

## **LEFT JOIN** / **RIGHT JOIN**
Сохраняет все строки из левой (**или правой**) таблицы, пропуская несовпадения как **NULL**.
```sql
-- left
SELECT a.title, au.first_name
FROM article a
LEFT JOIN author au ON au.id = a.author_id;

-- right
SELECT a.title, au.first_name
FROM article a
RIGHT JOIN author au ON au.id = a.author_id;
```

## **FULL JOIN**
Объединяет все строки обеих таблиц.
```sql
SELECT a.title, au.first_name
FROM article a
FULL JOIN author au ON au.id = a.author_id;
```

## **CROSS JOIN**
Декартово произведение (**будьте осторожны с размером**).
```sql
SELECT * FROM author CROSS JOIN article;
```

## Операции над множествами
- `**UNION**` — объединение без дублей.
- `**UNION ALL**` — объединение с дубликатами.
- `**EXCEPT**` — разность.
- `**INTERSECT**` — пересечение.

**Примеры:**
```sql
-- Статьи, которых нет у автора 1
SELECT id FROM article
EXCEPT
SELECT id FROM article WHERE author_id = 1;

-- Общие id из двух выборок
SELECT id FROM article WHERE title ILIKE '%java%'
INTERSECT
SELECT id FROM article WHERE author_id IS NOT NULL;
```

## Группировка после **JOIN**
```sql
SELECT au.last_name, COUNT(*) AS articles
FROM article a
LEFT JOIN author au ON au.id = a.author_id
GROUP BY au.last_name;
```

## Паттерны и анти-паттерны
- Делайте **JOIN** по ключам, а не по текстам/**LIKE**.
- Явно указывайте нужные колонки вместо `**SELECT** *`.
- При **LEFT JOIN** фильтры по правой таблице ставьте в `ON`, если хотите сохранить строки без совпадений.
- Не используйте **FULL JOIN** без необходимости: часто достаточно **UNION** двух выборок.
- Избегайте **JOIN** больших таблиц без фильтров и индексов — получите **Hash Join** с большим потреблением памяти.

## Лучшие практики

При работе с **JOIN** в **PostgreSQL** придерживайтесь принципов: выполняйте соединения по ключам с индексами, избегайте `SELECT *`, всегда проверяйте план выполнения перед оптимизацией. Подробные рекомендации см. в разделах [Паттерны и анти-паттерны](#паттерны-и-анти-паттерны) и [План выполнения и индексы](#план-выполнения-и-индексы).

## План выполнения и индексы
- **Перед оптимизацией смотрите план:**
```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT a.title, au.first_name
FROM article a
LEFT JOIN author au ON au.id = a.author_id
WHERE a.title ILIKE '%java%';
```
- Для **join** по `**author_id**` нужен индекс на `**author(**id**)` (**PK есть**) и индекс на `**article(**author_id**)`.
- **Planner** выбирает **Hash**/**Merge**/**Nested Loop** в зависимости от объёмов. Если есть фильтр по дате — составной индекс `(**author_id, created_at**)` поможет.
- Если **FULL JOIN** неизбежен, проверяйте, что объём подходит под **Hash**; иначе подумайте о разбиении запроса.

## **USING**, **NATURAL** и работа с **NULL**
- `**USING(**col**)` эквивалентно `ON **t1.col** = **t2.col**`, убирает дублирующиеся имена в результате; хорош для явных одинаковых **PK/FK**.
- `**NATURAL JOIN**` связывает по совпадающим именам столбцов — избегайте, может внезапно «схлопнуть» при добавлении колонки.
- **При `ON **t1.col** = **t2.col**` строки с **NULL** не совпадают; если нужно «считать **NULL**=**NULL**», используйте `IS **NOT DISTINCT FROM**`:**
```sql
ON (t1.col IS NOT DISTINCT FROM t2.col)
```
- **После **LEFT JOIN** фильтры по колонкам правой таблицы должны учитывать **NULL**:**
```sql
WHERE t2.id IS NULL -- антисоединение через LEFT JOIN
```

## Полусоединения и анти-соединения
- **Проверка существования (семиджойн) — `EXISTS` / `IN`:**
```sql
SELECT a.id, a.title
FROM article a
WHERE EXISTS (SELECT 1 FROM author au WHERE au.id = a.author_id);
```
- **Анти-соединение — `NOT EXISTS` / `NOT IN` (осторожно с `NULL` в `NOT IN`):**
```sql
SELECT a.id, a.title
FROM article a
WHERE NOT EXISTS (
  SELECT 1 FROM author au WHERE au.id = a.author_id
);
```
- Часто **semijoin** быстрее, чем **JOIN**+**DISTINCT**, если нужны только строки из одной таблицы.

## **LATERAL** и **UNNEST**
- **LATERAL** даёт доступ к предыдущим таблицам в **FROM**:**
```sql
SELECT u.id, p.title
FROM users u
CROSS JOIN LATERAL (
  SELECT title FROM posts p
  WHERE p.user_id = u.id
  ORDER BY p.created_at DESC
  LIMIT 1
) p;
```
- **Раскрутка массивов через `**unnest**()`:**
```sql
SELECT b.id, tag
FROM books b
CROSS JOIN LATERAL unnest(b.tags) AS tag;
```
- Старайтесь ставить фильтры в подзапрос **LATERAL** и индексировать соответствующие поля.

## **JOIN** + агрегации
- **Агрегируйте до соединения, если нужно уменьшить объём:**
```sql
WITH per_author AS (
  SELECT author_id, COUNT(*) AS cnt
  FROM article
  GROUP BY author_id
)
SELECT au.id, au.first_name, pa.cnt
FROM author au
LEFT JOIN per_author pa ON pa.author_id = au.id;
```
- Если объединяете агрегаты из разных источников, используйте `GROUPING SETS` / `ROLLUP` при необходимости.
- Фильтрация после **LEFT JOIN** на агрегаты — в **HAVING**/**WHERE** c учётом **NULL**.

## Многие-ко-многим и взрывы строк
- M:N соединение может сильно раздувать результат. Часто нужно предварительно агрегировать или ограничить:
```sql
SELECT u.id, COUNT(*) AS tags
FROM users u
JOIN user_tags ut ON ut.user_id = u.id
GROUP BY u.id;
```
- Для поиска «все теги из набора присутствуют» используйте `**array_agg**` + сравнение, или `**HAVING COUNT(**DISTINCT ...**) = N`.
- Проверяйте планы: **Hash Join** на огромные таблицы без фильтров = много памяти. Добавляйте условия диапазона/статуса и индексы.

## Практические кейсы
- **Найти авторов без статей:**
```sql
SELECT au.id, au.first_name
FROM author au
LEFT JOIN article a ON a.author_id = au.id
WHERE a.id IS NULL;
```
- **Последняя статья автора (**LATERAL**):**
```sql
SELECT au.id, p.title
FROM author au
CROSS JOIN LATERAL (
  SELECT title
  FROM article a
  WHERE a.author_id = au.id
  ORDER BY a.id DESC
  LIMIT 1
) p;
```
- **Топ авторов за последние 30 дней:**
```sql
WITH recent AS (
  SELECT author_id, COUNT(*) AS cnt
  FROM article
  WHERE created_at >= now() - interval '30 days'
  GROUP BY author_id
)
SELECT au.id, au.first_name, r.cnt
FROM recent r
JOIN author au ON au.id = r.author_id
ORDER BY r.cnt DESC
LIMIT 10;
```
- **Антиджойн через **NOT EXISTS** (**предпочтительнее, чем `NOT IN` с NULL**):**
```sql
SELECT * FROM orders o
WHERE NOT EXISTS (
  SELECT 1 FROM refunds r
  WHERE r.order_id = o.id
);
```

## Отладка планов и настройки
- Включайте буферы и время: `**EXPLAIN** (**ANALYZE, `BUFFERS`, TIMING**)`.
- **Временно отключить вид узла для диагностики:**
```sql
SET enable_hashjoin TO off; -- посмотреть, станет ли план лучше/хуже
RESET enable_hashjoin;
```
- Контроль порядка соединений: `**join_collapse_limit**` и `**from_collapse_limit**` (**осторожно, влияет на planner**).
- Если оценка кардинальности неверна, проверьте статистику (**`ANALYZE`**) и создайте **extended stats** `**CREATE STATISTICS** ... **DEPENDENCIES**` для связанных колонок.
- Следите за `**work_mem**`: большие **Hash Join** могут упасть во временные файлы; увеличивайте точечно в сессии: `**SET work_mem**='128MB';`

## Подробное описание типов **JOIN**

### Неявное соединение таблиц

Нередко возникает ситуация, когда нам надо получить данные из нескольких таблиц. Для соединения данных из разных таблиц можно использовать команду `**SELECT**`.

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

**Неявное соединение (**декартово произведение**):**
```sql
SELECT * FROM Orders, Customers;
```

При такой выборке каждая строка из таблицы `**Orders**` будет совмещаться с каждой строкой из таблицы `**Customers**`. То есть получится перекрестное соединение. Например, в `**Orders**` три строки, а в `**Customers**` то же три строки, значит мы получим 3 * 3 = 9 строк.

То есть в данном случае мы получаем прямое (**декартово**) произведение двух групп. Однако вряд ли такой результат можно назвать желаемым. Тем более каждый заказ из `**Orders**` связан с конкретным покупателем из `**Customers**`, а не со всеми возможными покупателями.

**Для решения этой задачи необходимо использовать выражение `WHERE` и фильтровать строки при условии, что поле `CustomerId` из `Orders` соответствует полю `Id` из `Customers`:**
```sql
SELECT * FROM Orders, Customers
WHERE Orders.CustomerId = Customers.Id;
```

**Соединение трех таблиц:**
```sql
SELECT Customers.FirstName, Products.ProductName, Orders.CreatedAt
FROM Orders, Customers, Products
WHERE Orders.CustomerId = Customers.Id AND Orders.ProductId=Products.Id;
```

Так как здесь соединяются три таблицы, то необходимо применить как минимум два условия. Ключевой таблицей остается `**Orders**`, из которой извлекаются все заказы, а затем к ней подсоединяются данные по клиенту по условию `**Orders.CustomerId** = **Customers.Id**` и данные по товару по условию `**Orders.ProductId**=**Products.Id**`.

**Использование псевдонимов:**
**Поскольку в данном случае названия таблиц сильно увеличивают код, то мы его можем сократить за счет использования псевдонимов таблиц:**

```sql
SELECT C.FirstName, P.ProductName, O.CreatedAt
FROM Orders AS O, Customers AS C, Products AS P
WHERE O.CustomerId = C.Id AND O.ProductId=P.Id;
```

**Если необходимо при использовании псевдонима выбрать все столбцы из определенной таблицы, то можно использовать звездочку:**
```sql
SELECT C.FirstName, P.ProductName, O.*
FROM Orders AS O, Customers AS C, Products AS P
WHERE O.CustomerId = C.Id AND O.ProductId=P.Id;
```

## Подробное описание **INNER JOIN**

**INNER JOIN** представляет так называемое внутреннее соединение. Его формальный синтаксис:**

```sql
SELECT столбцы
FROM таблица1
[INNER] JOIN таблица2
ON условие1
[[INNER] JOIN таблица3
ON условие2]
```

После оператора `**JOIN**` идет название второй таблицы, данные которой надо добавить в выборку. Перед `**JOIN**` можно указывать необязательный оператор `**INNER**`. Его наличие или отсутствие ни на что не влияет. Далее после ключевого слова `ON` указывается условие соединения. Это условие устанавливает, как две таблицы будут сравниваться. Как правило, для соединения применяется первичный ключ главной таблицы и внешний ключ зависимой таблицы.

**Пример использования `JOIN`:**
```sql
SELECT Orders.CreatedAt, Orders.ProductCount, Products.ProductName
FROM Orders
JOIN Products ON Products.Id = Orders.ProductId;
```

Поскольку таблицы могут содержать столбцы с одинаковыми названиями, то при указании столбцов для выборки указывается их полное имя вместе с именем таблицы, например, "**Orders.ProductCount**".

**С помощью псевдонимов, определяемых через оператор `AS`, можно сократить код:**
```sql
SELECT O.CreatedAt, O.ProductCount, P.ProductName
FROM Orders AS O
JOIN Products AS P
ON P.Id = O.ProductId;
```

**Подобным образом мы можем присоединять и другие таблицы:**
```sql
SELECT Orders.CreatedAt, Customers.FirstName, Products.ProductName
FROM Orders
JOIN Products ON Products.Id = Orders.ProductId
JOIN Customers ON Customers.Id=Orders.CustomerId;
```

**Благодаря соединению таблиц мы можем использовать их столбцы для фильтрации выборки или ее сортировки:**
```sql
SELECT Orders.CreatedAt, Customers.FirstName, Products.ProductName
FROM Orders
JOIN Products ON Products.Id = Orders.ProductId
JOIN Customers ON Customers.Id=Orders.CustomerId
WHERE Products.Price > 45000
ORDER BY Customers.FirstName;
```

**Условия после ключевого слова `ON` могут быть более сложными по составу:**
```sql
SELECT Orders.CreatedAt, Customers.FirstName, Products.ProductName
FROM Orders
JOIN Products ON Products.Id = Orders.ProductId AND Products.Company='Apple'
JOIN Customers ON Customers.Id=Orders.CustomerId
ORDER BY Customers.FirstName;
```

Выбираем все заказы на товары, производителем которых является **Apple**.

**Как мы упоминали ранее, `INNER JOIN` выбирает только общие строки по заданному условию.** Глядя на наши вставки, мы видим, что у нас одна статья без автора и один автор без статьи. Эти строки пропускаются, поскольку они не удовлетворяют заданному условию. В результате мы получаем четыре объединенных результата, и ни один из них не имеет пустых данных об авторах или пустого заголовка.

## Подробное описание **OUTER JOIN**

**OUTER JOIN** или внешнее соединение позволяет возвратить все строки одной или двух таблиц, которые участвуют в соединении.

**Формальный синтаксис `OUTER JOIN`:**
```sql
SELECT столбцы
FROM таблица1
{LEFT|RIGHT|FULL}[OUTER] JOIN таблица2 ON условие1
[ {LEFT|RIGHT|FULL}[OUTER] JOIN таблица3 ON условие2]...
```

**Перед оператором `**JOIN**` указывается одно из ключевых слов `**LEFT**`, `**RIGHT**` или `**FULL**`, которые определяют тип соединения:**

1. **LEFT**: выборка будет содержать все строки из первой или левой таблицы
2. **RIGHT**: выборка будет содержать все строки из второй или правой таблицы
3. **FULL**: выборка будет содержать все строки из обеих таблиц

Перед оператором `**JOIN**` может указываться ключевое слово `**OUTER**`, но его применение необязательно. После `**JOIN**` указывается присоединяемая таблица, а затем идет условие соединения после оператора `ON`.

### **LEFT JOIN**

**LEFT JOIN** выбирает все строки из первой таблицы и сопоставляет соответствующие строки из второй таблицы. При отсутствии совпадения столбцы заполняются нулевыми значениями.

**Пример `LEFT JOIN`:**
```sql
SELECT FirstName, CreatedAt, ProductCount, Price, ProductId
FROM Orders LEFT JOIN Customers
ON Orders.CustomerId = Customers.Id;
```

Таблица `**Orders**` является первой или левой таблицей, а таблица `**Customers**` — правой таблицей. Поэтому, так как здесь используется выборка по левой таблице, то вначале будут выбираться все строки из `**Orders**`, а затем к ним по условию `**Orders.CustomerId** = **Customers.Id**` будут добавляться связанные строки из `**Customers**`.

**По результату может показаться, что левостороннее соединение аналогично `INNER Join`, но это не так. Inner Join** объединяет строки из двух таблиц при соответствии условию. Если одна из таблиц содержит строки, которые не соответствуют этому условию, то данные строки не включаются в выходную выборку. **Left Join** выбирает все строки первой таблицы и затем присоединяет к ним строки правой таблицы.

**Пример различия `INNER JOIN` и `LEFT JOIN`:**
```sql
-- INNER JOIN — только строки с совпадениями
SELECT FirstName, CreatedAt, ProductCount, Price
FROM Customers JOIN Orders
ON Orders.CustomerId = Customers.Id;

-- LEFT JOIN — все строки из Customers, даже без заказов
SELECT FirstName, CreatedAt, ProductCount, Price
FROM Customers LEFT JOIN Orders
ON Orders.CustomerId = Customers.Id;
```

**INNER join** vs **left join** в **PostgreSQL**: первый запрос вернет только тех покупателей, у которых есть заказы. Второй запрос вернет всех покупателей, даже если у них нет заказов (**для таких покупателей столбцы из `Orders` будут NULL**).

### **RIGHT JOIN**

**RIGHT JOIN** очень похож на **LEFT JOIN**, но он возвращает все строки из второй таблицы и сопоставляет строки из первой таблицы. Как и в случае **LEFT JOIN**, пустые совпадения заменяются нулевыми значениями.

**Пример `RIGHT JOIN`:**
```sql
SELECT FirstName, CreatedAt, ProductCount, Price, ProductId
FROM Orders RIGHT JOIN Customers
ON Orders.CustomerId = Customers.Id;
```

Теперь будут выбираться все строки из `**Customers**`, а к ним уже будет присоединяться связанные по условию строки из таблицы `**Orders**`. Поскольку один из покупателей из таблицы `**Customers**` не имеет связанных заказов из `**Orders**`, то соответствующие столбцы, которые берутся из `**Orders**`, будут иметь значение **NULL**.

### **FULL JOIN**

**FULL JOIN** (**полное внешнее соединение**) объединяет обе таблицы:**

```sql
SELECT FirstName, CreatedAt, ProductCount, Price, ProductId
FROM Orders FULL JOIN Customers
ON Orders.CustomerId = Customers.Id;
```

**FULL JOIN** выбирает все строки как из первой, так и из второй таблицы независимо от того, выполняется ли условие или нет.

**Используем левостороннее соединение для добавления к заказам информации о пользователях и товарах:**
```sql
SELECT Customers.FirstName, Orders.CreatedAt,
       Products.ProductName, Products.Company
FROM Orders
LEFT JOIN Customers ON Orders.CustomerId = Customers.Id
LEFT JOIN Products ON Orders.ProductId = Products.Id;
```

**И также можно применять более комплексные условия с фильтрацией и сортировкой:**
```sql
SELECT Customers.FirstName, Orders.CreatedAt,
       Products.ProductName, Products.Company
FROM Orders
LEFT JOIN Customers ON Orders.CustomerId = Customers.Id
LEFT JOIN Products ON Orders.ProductId = Products.Id
WHERE Products.Price > 55000
ORDER BY Orders.CreatedAt;
```

**Или выберем всех пользователей из `Customers`, у которых нет заказов в таблице `Orders`:**
```sql
SELECT FirstName FROM Customers
LEFT JOIN Orders ON Customers.Id = Orders.CustomerId
WHERE Orders.CustomerId IS NULL;
```

**Также можно комбинировать `Inner Join` и `Outer Join`:**
```sql
SELECT Customers.FirstName, Orders.CreatedAt,
       Products.ProductName, Products.Company
FROM Orders
JOIN Products ON Orders.ProductId = Products.Id AND Products.Price > 45000
LEFT JOIN Customers ON Orders.CustomerId = Customers.Id
ORDER BY Orders.CreatedAt;
```

Вначале по условию к таблице `**Orders**` через `**Inner Join**` присоединяется связанная информация из `**Products**`, затем через `**Outer Join**` добавляется информация из таблицы `**Customers**`.

## Подробное описание **CROSS JOIN**

**CROSS JOIN** или перекрестное соединение создает набор строк, где каждая строка из одной таблицы соединяется с каждой строкой из второй таблицы.

**Пример:**
```sql
SELECT * FROM Orders CROSS JOIN Customers;
```

Если в таблице `**Orders**` 3 строки, а в таблице `**Customers**` то же три строки, то в результате перекрестного соединения создается 3 * 3 = 9 строк вне зависимости, связаны ли данные строки или нет.

**При неявном перекрестном соединении можно опустить оператор `CROSS JOIN` и просто перечислить все получаемые таблицы:**
```sql
SELECT * FROM Orders, Customers;
```

Это эквивалентно `**CROSS JOIN**`, но для явности рекомендуется использовать `**CROSS JOIN**`.

## Подробное описание операций над множествами

### **UNION** (**объединение**)

Оператор `**UNION**` позволяет объединить два множества (**условно две таблицы**). Но в отличие от **inner**/**outer join** объединения соединяют не столбцы разных таблиц, а два однотипных набора в один.

**Формальный синтаксис объединения:**
```sql
SELECT_выражение1
UNION [ALL] SELECT_выражение2
[UNION [ALL] SELECT_выражениеN]
```

**Пример таблиц:**
```sql
CREATE TABLE Customers (
    Id SERIAL PRIMARY KEY,
    FirstName VARCHAR(20) NOT NULL,
    LastName VARCHAR(20) NOT NULL,
    AccountSum NUMERIC DEFAULT 0
);

CREATE TABLE Employees (
    Id SERIAL PRIMARY KEY,
    FirstName VARCHAR(20) NOT NULL,
    LastName VARCHAR(20) NOT NULL
);
```

**Выберем сразу всех клиентов банка и его сотрудников из обеих таблиц:**
```sql
SELECT FirstName, LastName
FROM Customers
UNION SELECT FirstName, LastName FROM Employees;
```

В данном случае из первой таблицы выбираются два значения — имя и фамилия клиента. Из второй таблицы `**Employees**` также выбираются два значения — имя и фамилия сотрудников. То есть при объединении количество выбираемых столбцов и их тип совпадают для обеих выборок.

**Если оба объединяемых набора содержат в строках идентичные значения, то при объединении повторяющиеся строки удаляются.** В случае с таблицами `**Customers**` и `**Employees**` сотрудники банка могут быть одновременно его клиентами и содержаться в обеих таблицах. При объединении в примерах выше всех дублирующиеся строки удалялись. Например, исходя из начальных данных, мы видим, что два человека: **Tom Smith** и **Mark Adams** располагаются в обеих таблицах. Однако при объединении дубли не считаются, поэтому один человек учитывается только один раз.

**Если же необходимо при объединении сохранить все, в том числе повторяющиеся строки, то для этого необходимо использовать оператор `ALL`:**
```sql
SELECT FirstName, LastName
FROM Customers
UNION ALL SELECT FirstName, LastName
FROM Employees;
```

**При этом названия столбцов объединенной выборки будут совпадать с названиями столбцов первой выборки. И если мы захотим при этом еще произвести сортировку, то в выражениях `**ORDER** BY` необходимо ориентироваться именно на названия столбцов первой выборки:**

```sql
SELECT FirstName || ' ' || LastName AS FullName
FROM Customers
UNION SELECT FirstName || ' ' || LastName AS EmployeeName
FROM Employees
ORDER BY FullName;
```

В данном случае каждая выборка имеет по одному столбцу, который представляет объединение имени и фамилии клиента или сотрудника. Для объединения строк применяется оператор ||. Но в случае с клиентами столбец будет называться `**FullName**`, а в случае с сотрудниками — `**EmployeeName**`. Тем не менее для сортировки применяется название столбца из первой выборки и он же будет в результирующей выборке.

**Объединять выборки можно и из одной и той же таблицы:**
```sql
SELECT FirstName, LastName, AccountSum + AccountSum * 0.1 AS TotalSum
FROM Customers WHERE AccountSum < 3000
UNION SELECT FirstName, LastName, AccountSum + AccountSum * 0.3 AS TotalSum
FROM Customers WHERE AccountSum >= 3000;
```

В данном случае если сумма меньше `3000`, то начисляются проценты в размере 10% от суммы на счете. Если на счете больше `3000`, то проценты увеличиваются до 30%.

### **EXCEPT** (**разность**)

Оператор `**EXCEPT**` в **PostgreSQL** позволяет найти разность двух выборок, то есть те строки которые есть в первой выборке, но которых нет во второй.

**Формальный синтаксис:**
```sql
SELECT_выражение1
EXCEPT SELECT_выражение2
```

**Пример:**
**Таблица `**Employees**` содержит данные обо всех сотрудниках банка, а таблица `**Customers**` — обо всех клиентах. Но сотрудники банка могут также быть его клиентами. И допустим, нам надо найти всех клиентов банка, которые не являются его сотрудниками:**

```sql
SELECT FirstName, LastName
FROM Customers
EXCEPT SELECT FirstName, LastName
FROM Employees;
```

**Подобным образом можно получить всех сотрудников банка, которые не являются его клиентами:**
```sql
SELECT FirstName, LastName
FROM Employees
EXCEPT SELECT FirstName, LastName
FROM Customers;
```

### **INTERSECT** (**пересечение**)

Оператор `**INTERSECT**` позволяет найти общие строки для двух выборок, то есть данный оператор выполняет операцию пересечения множеств.

**Формальный синтаксис:**
```sql
SELECT_выражение1
INTERSECT SELECT_выражение2
```

**Пример:**
**В таблице `**Customers**` хранятся все клиенты банка, а в таблице `**Employees**` — все его сотрудники. При этом сотрудники банка могут быть одновременно и клиентами этого банка, поэтому их данные могут храниться сразу в двух таблицах. Найдем всех сотрудников банка, которые одновременно являются его клиентами. То есть нам надо найти общие элементы двух выборок:**

```sql
SELECT FirstName, LastName
FROM Employees
INTERSECT SELECT FirstName, LastName
FROM Customers;
```

## Комбинирование различных типов **JOIN**

### Множественные **JOIN**

**Комбинирование различных типов `JOIN` в одном запросе:**
```sql
SELECT 
    c.FirstName,
    o.CreatedAt,
    p.ProductName,
    p.Company
FROM Orders o
INNER JOIN Products p ON p.Id = o.ProductId
LEFT JOIN Customers c ON c.Id = o.CustomerId
WHERE p.Price > 45000
ORDER BY o.CreatedAt;
```

Вначале по условию к таблице `**Orders**` через `**INNER JOIN**` присоединяется связанная информация из `**Products**`, затем через `**LEFT JOIN**` добавляется информация из таблицы `**Customers**`.

### Группировка после **JOIN**

Более сложным вариантом использования соединений **INNER**/**OUTER JOIN** представляет их сочетание с выражениями группировки, в частности, с оператором `**GROUP** BY`.

**Пример:**
```sql
-- Выведем для каждого покупателя количество заказов, которые он сделал
SELECT FirstName, COUNT(Orders.Id)
FROM Customers JOIN Orders
ON Orders.CustomerId = Customers.Id
GROUP BY Customers.Id, Customers.FirstName;
```

Критерием группировки выступают `Id` и имя покупателя. Выражение `**SELECT**` выбирает имя покупателя и количество заказов, используя столбец `Id` из таблицы `**Orders**`.

Так как это `**INNER JOIN**`, то в группах будут только те покупатели, у которых есть заказы.

**Если нужно получить также и тех покупателей, у которых нет заказов, то можно использовать `OUTER JOIN`:**
```sql
SELECT FirstName, COUNT(Orders.Id)
FROM Customers LEFT JOIN Orders
ON Orders.CustomerId = Customers.Id
GROUP BY Customers.Id, Customers.FirstName;
```

**Или выведем товары с общей суммой сделанных заказов:**
```sql
SELECT Products.ProductName, Products.Company,
       SUM(Orders.ProductCount * Orders.Price) AS TotalSum
FROM Products LEFT JOIN Orders
ON Orders.ProductId = Products.Id
GROUP BY Products.Id, Products.ProductName, Products.Company;
```

## Дополнительные техники **JOIN**

### **NATURAL JOIN** (**не рекомендуется**)

**NATURAL JOIN** автоматически связывает таблицы по столбцам с одинаковыми именами:**

```sql
-- Не рекомендуется: может привести к неожиданным результатам
SELECT * FROM Orders
NATURAL JOIN Customers;

-- Эквивалентно:
SELECT * FROM Orders o
JOIN Customers c ON o.CustomerId = c.Id AND o.Id = c.Id; -- Неправильно!
```

**Проблемы `NATURAL JOIN`:**
- Может неожиданно связать столбцы с одинаковыми именами, которые не должны быть связаны.
- При добавлении столбцов может измениться поведение запроса.
- Лучше использовать явные условия **JOIN** с `ON`.

### Условия соединения с множественными столбцами

**JOIN по нескольким столбцам:**
```sql
SELECT *
FROM Orders o
JOIN OrderItems oi ON o.Id = oi.OrderId AND o.CustomerId = oi.CustomerId;
```

**Использование `USING` для столбцов с одинаковыми именами:**
```sql
-- USING эквивалентно ON o.CustomerId = c.Id
SELECT *
FROM Orders o
JOIN Customers c USING (CustomerId);

-- Для нескольких столбцов
SELECT *
FROM Orders o
JOIN OrderDetails od USING (OrderId, ProductId);
```

**Преимущества `USING`:**
- Короче и читабельнее при одинаковых именах столбцов.
- Столбец появляется только один раз в результате.
- Упрощает запросы с несколькими таблицами.

### Оптимизация **JOIN** с индексами

**Для эффективного `JOIN` необходимы индексы:**
```sql
-- Индекс на внешнем ключе (ускоряет JOIN)
CREATE INDEX idx_orders_customer_id ON Orders(CustomerId);
CREATE INDEX idx_orders_product_id ON Orders(ProductId);

-- Составной индекс для сложных условий JOIN
CREATE INDEX idx_orders_customer_date ON Orders(CustomerId, CreatedAt);

-- Индекс на первичном ключе создается автоматически
-- Но для составных ключей может понадобиться дополнительный индекс
```

### Оптимизация больших **JOIN**

**Для больших таблиц используйте фильтрацию перед `JOIN`:**
```sql
-- Плохо: JOIN больших таблиц без фильтрации
SELECT * FROM large_table1 t1
JOIN large_table2 t2 ON t1.id = t2.id;

-- Хорошо: фильтрация перед JOIN
SELECT * FROM large_table1 t1
JOIN large_table2 t2 ON t1.id = t2.id
WHERE t1.created_at >= '2024-01-01' AND t2.status = 'active';
```

**Или используйте подзапросы для предварительной фильтрации:**
```sql
WITH filtered_orders AS (
    SELECT * FROM Orders
    WHERE created_at >= '2024-01-01'
),
filtered_customers AS (
    SELECT * FROM Customers
    WHERE is_active = true
)
SELECT fo.*, fc.FirstName
FROM filtered_orders fo
JOIN filtered_customers fc ON fo.CustomerId = fc.Id;
```

## Расширенные примеры **JOIN**

### Иерархические запросы (**рекурсивные JOIN**)

**Использование рекурсивных `CTE` для иерархических данных:**
```sql
WITH RECURSIVE category_tree AS (
    -- Начальная часть
    SELECT id, name, parent_id, 1 AS level
    FROM categories
    WHERE parent_id IS NULL
    
    UNION ALL
    
    -- Рекурсивная часть
    SELECT c.id, c.name, c.parent_id, ct.level + 1
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT * FROM category_tree;
```

### Само-соединения (**Self-Joins**)

**Соединение таблицы с самой собой:**
```sql
-- Найти менеджеров и их подчиненных
SELECT 
    e1.name AS manager,
    e2.name AS employee
FROM employees e1
JOIN employees e2 ON e2.manager_id = e1.id;
```

**Или найти пары сотрудников с одинаковой должностью:**
```sql
SELECT 
    e1.name AS employee1,
    e2.name AS employee2,
    e1.position
FROM employees e1
JOIN employees e2 ON e1.position = e2.position
WHERE e1.id < e2.id;  -- Избегаем дублей
```

### Оптимизация производительности **JOIN**

**Использование индексов для ускорения `JOIN`:**
```sql
-- Индексы на внешних ключах критически важны для производительности
CREATE INDEX idx_orders_customer_id ON Orders(CustomerId);
CREATE INDEX idx_orders_product_id ON Orders(ProductId);
CREATE INDEX idx_customers_email ON Customers(Email);

-- Составные индексы для составных условий JOIN
CREATE INDEX idx_orders_customer_date ON Orders(CustomerId, CreatedAt DESC);
```

**Мониторинг производительности `JOIN`:**
```sql
-- Проверить использование индексов в JOIN
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.FirstName, o.CreatedAt, p.ProductName
FROM Orders o
JOIN Customers c ON c.Id = o.CustomerId
JOIN Products p ON p.Id = o.ProductId
WHERE o.CreatedAt >= '2024-01-01';
```

**Ищите в плане выполнения:**
- `**Index Scan**` или `**Index Only Scan**` — хорошо
- `**Hash Join**` — нормально для средних таблиц
- `**Merge Join**` — хорошо для отсортированных данных
- `**Nested Loop**` — может быть медленно для больших таблиц
- `**Seq Scan**` — плохо для больших таблиц

### Типичные проблемы и решения **JOIN**

**Проблема 1: Дублирующиеся строки после JOIN**

**Симптомы:**
- Количество строк в результате больше ожидаемого.
- Одни и те же данные повторяются.

**Решение:**
- **Используйте `**DISTINCT**` если необходимо:**
```sql
SELECT DISTINCT c.FirstName, p.ProductName
FROM Customers c
JOIN Orders o ON c.Id = o.CustomerId
JOIN Products p ON p.Id = o.ProductId;
```

- **Или агрегируйте данные:**
```sql
SELECT c.FirstName, COUNT(DISTINCT p.Id) AS products_count
FROM Customers c
JOIN Orders o ON c.Id = o.CustomerId
JOIN Products p ON p.Id = o.ProductId
GROUP BY c.Id, c.FirstName;
```

**Проблема 2: Медленные `JOIN` на больших таблицах**

**Симптомы:**
- Запрос выполняется долго (**> 1 секунды**).
- Высокая загрузка **CPU**/памяти.

**Решение:**
- Добавьте индексы на ключи соединения.
- Фильтруйте данные перед **JOIN**.
- Используйте частичные индексы для часто используемых фильтров.
- Рассмотрите партиционирование для очень больших таблиц.

**Проблема 3: Неправильные результаты JOIN**

**Симптомы:**
- Отсутствуют ожидаемые строки.
- Появляются неожиданные строки.

**Решение:**
- Проверьте условия **JOIN** (**ON**).
- Убедитесь, что используете правильный тип **JOIN** (**INNER vs LEFT**).
- Проверьте **NULL** значения в ключах соединения.
- Используйте `IS **NOT DISTINCT FROM**` для сравнения с **NULL**.

**Проблема 4: Декартово произведение**

**Симптомы:**
- Огромное количество строк в результате (**тысячи/миллионы**).
- Запрос выполняется очень долго.

**Решение:**
- Проверьте условия **JOIN** — возможно, забыто условие.
- Убедитесь, что все таблицы соединены правильно.
- Проверьте использование **CROSS JOIN** — возможно, нужен другой тип **JOIN**.


