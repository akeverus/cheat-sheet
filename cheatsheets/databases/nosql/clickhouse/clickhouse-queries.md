---
title: "ClickHouse: Запросы и аналитика — Полное руководство по SQL запросам"
description: "Комплексное руководство по запросам ClickHouse: SELECT, агрегация, оконные функции и аналитические возможности"
tags:
  - clickhouse
  - sql
  - queries
  - analytics
  - aggregation
  - window-functions
type: "overview"
difficulty: "intermediate"
aliases:
  - "ClickHouse"
  - "clickhouse queries"
prerequisites:
  - "[[clickhouse-tables]]"
related:
  - "[[clickhouse-tables]]"
  - "[[clickhouse-indexes]]"
next: []
updated: "2026-04-20"
---

# ClickHouse: Запросы и аналитика — Полное руководство по SQL запросам

Комплексное руководство по запросам **ClickHouse**: **SELECT**, агрегация, оконные функции и аналитические возможности.

## Полезные ссылки

### Официальная документация
- [SQL Reference](https://clickhouse.com/docs/en/sql-reference)
- [Query Syntax](https://clickhouse.com/docs/en/sql-reference/statements/select)
- [Functions](https://clickhouse.com/docs/en/sql-reference/functions)
- [Query Optimization](https://clickhouse.com/docs/en/operations/optimizing-performance)
- [SQL for Analytics](https://clickhouse.com/docs/en/guides/best-practices)

### Обучающие материалы
- [ClickHouse SQL Queries](https://www.baeldung.com/clickhouse-queries)
- [ClickHouse Examples](https://clickhouse.com/docs/en/getting-started/tutorial)
- [Playground](https://play.clickhouse.com/)

### См. также
- [Таблицы](clickhouse-tables.md) — создание таблиц
- [Индексы](clickhouse-indexes.md) — оптимизация запросов

- [ClickHouse: Основы колоночной аналитической базы данных](clickhouse-basics.md)
- [ClickHouse: Репликация и кластеры — Отказоустойчивость и масштабируемость](clickhouse-replication.md)
## Содержание

- [Вставка данных](#вставка-данных)
  - [INSERT синтаксис](#insert-синтаксис)
  - [Примеры вставки](#примеры-вставки)
- [Базовые SELECT запросы](#базовые-select-запросы)
  - [Простые запросы](#простые-запросы)
  - [DISTINCT — уникальные значения](#distinct-уникальные-значения)
- [Условия WHERE](#условия-where)
  - [Операторы сравнения](#операторы-сравнения)
  - [Логические операторы](#логические-операторы)
  - [IN и NOT IN](#in-и-not-in)
  - [LIKE для строк](#like-для-строк)
  - [Работа с NULL](#работа-с-null)
  - [Диапазоны и BETWEEN](#диапазоны-и-between)
- [Сортировка и пагинация](#сортировка-и-пагинация)
  - [ORDER BY](#order-by)
  - [LIMIT и OFFSET](#limit-и-offset)
- [Агрегатные функции](#агрегатные-функции)
  - [Основные агрегаты](#основные-агрегаты)
  - [Агрегаты с условиями](#агрегаты-с-условиями)
- [Группировка данных](#группировка-данных)
  - [GROUP BY](#group-by)
  - [HAVING — фильтрация групп](#having-фильтрация-групп)
  - [WITH ROLLUP, CUBE, TOTALS](#with-rollup-cube-totals)
- [Оконные функции](#оконные-функции)
  - [ROW_NUMBER, RANK, DENSE_RANK](#row_number-rank-dense_rank)
  - [LAG и LEAD](#lag-и-lead)
  - [FIRST_VALUE, LAST_VALUE](#first_value-last_value)
  - [NTH_VALUE](#nth_value)
  - [NTILE — разделение на группы](#ntile-разделение-на-группы)
  - [Скользящие агрегаты](#скользящие-агрегаты)
- [JOIN операции](#join-операции)
  - [INNER JOIN](#inner-join)
  - [LEFT JOIN](#left-join)
  - [RIGHT JOIN и FULL JOIN](#right-join-и-full-join)
  - [CROSS JOIN](#cross-join)
- [Подзапросы](#подзапросы)
  - [Скалярные подзапросы](#скалярные-подзапросы)
  - [Подзапросы в FROM](#подзапросы-в-from)
  - [EXISTS и IN с подзапросами](#exists-и-in-с-подзапросами)
- [Работа с массивами](#работа-с-массивами)
  - [Создание таблицы с массивами](#создание-таблицы-с-массивами)
  - [Функции для работы с массивами](#функции-для-работы-с-массивами)
  - [Преобразование массивов](#преобразование-массивов)
- [Аналитические функции](#аналитические-функции)
  - [Ранжирование и нумерация](#ранжирование-и-нумерация)
  - [Статистические функции](#статистические-функции)
  - [Корреляционный анализ](#корреляционный-анализ)
- [Оптимизация запросов](#оптимизация-запросов)
  - [Использование индексов](#использование-индексов)
  - [PREWHERE для фильтрации](#prewhere-для-фильтрации)
  - [SAMPLE для приближенных расчетов](#sample-для-приближенных-расчетов)
  - [UNION ALL для объединения](#union-all-для-объединения)
- [Лучшие практики](#лучшие-практики)
  - [Производительность запросов](#производительность-запросов)
  - [Аналитические запросы](#аналитические-запросы)
  - [Мониторинг и отладка](#мониторинг-и-отладка)
- [Продвинутые аналитические возможности](#продвинутые-аналитические-возможности)
  - [Работа с временными рядами](#работа-с-временными-рядами)
  - [Геопространственный анализ](#геопространственный-анализ)
  - [Статистический анализ](#статистический-анализ)
- [Оптимизация сложных запросов](#оптимизация-сложных-запросов)
  - [Стратегии оптимизации](#стратегии-оптимизации)
  - [Мониторинг производительности запросов](#мониторинг-производительности-запросов)
  - [Основные возможности:](#основные-возможности)
  - [Рекомендации по использованию:](#рекомендации-по-использованию)
  - [Продвинутые возможности:](#продвинутые-возможности)
  - [Следующие темы:](#следующие-темы)
- [Решение проблем](#решение-проблем)

## Вставка данных

### INSERT синтаксис

```sql
-- Вставка одной строки
INSERT INTO table_name VALUES (value1, value2, value3);

-- Вставка с указанием столбцов
INSERT INTO table_name (column1, column2, column3) VALUES (value1, value2, value3);

-- Вставка нескольких строк
INSERT INTO table_name VALUES
    (value1_1, value2_1, value3_1),
    (value1_2, value2_2, value3_2),
    (value1_3, value2_3, value3_3);

-- Вставка из SELECT
INSERT INTO target_table
SELECT column1, column2, column3
FROM source_table
WHERE condition;

-- Вставка с указанием формата
INSERT INTO table_name FORMAT CSV
id,name,value
1,"Alice",100.5
2,"Bob",200.3
3,"Charlie",150.7
```

### Примеры вставки

```sql
-- Создание тестовой таблицы
CREATE TABLE sales (
    date Date,
    product_id UInt64,
    customer_id UInt64,
    quantity UInt32,
    price Decimal64(2),
    total Decimal64(2)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (product_id, date);

-- Вставка данных
INSERT INTO sales VALUES
    ('2024-01-15', 101, 1001, 2, 29.99, 59.98),
    ('2024-01-15', 102, 1002, 1, 49.99, 49.99),
    ('2024-01-16', 101, 1003, 3, 29.99, 89.97);

-- Вставка из другой таблицы
INSERT INTO sales_archive
SELECT * FROM sales
WHERE date < '2024-01-01';

-- Массовая вставка с расчетами
INSERT INTO sales
SELECT
    today() as date,
    product_id,
    customer_id,
    quantity,
    price,
    quantity * price as total
FROM order_items
WHERE order_date = today();
```

## Базовые SELECT запросы

### Простые запросы

```sql
-- Все столбцы
SELECT * FROM sales;

-- Конкретные столбцы
SELECT product_id, quantity, total FROM sales;

-- С вычислениями
SELECT
    product_id,
    quantity,
    price,
    quantity * price as calculated_total
FROM sales;

-- С псевдонимами
SELECT
    product_id as product,
    customer_id as customer,
    quantity as qty,
    total as amount
FROM sales;
```

### DISTINCT — уникальные значения

```sql
-- Уникальные продукты
SELECT DISTINCT product_id FROM sales;

-- Уникальные комбинации
SELECT DISTINCT product_id, customer_id FROM sales;

-- Количество уникальных значений
SELECT
    uniq(product_id) as unique_products,
    uniq(customer_id) as unique_customers
FROM sales;
```

## Условия WHERE

### Операторы сравнения

```sql
-- Равенство
SELECT * FROM sales WHERE product_id = 101;

-- Неравенство
SELECT * FROM sales WHERE quantity != 1;
SELECT * FROM sales WHERE quantity <> 1;

-- Сравнение
SELECT * FROM sales WHERE total > 50;
SELECT * FROM sales WHERE total >= 50;
SELECT * FROM sales WHERE total < 100;
SELECT * FROM sales WHERE total <= 100;
```

### Логические операторы

```sql
-- AND
SELECT * FROM sales
WHERE product_id = 101 AND quantity > 1;

-- OR
SELECT * FROM sales
WHERE product_id = 101 OR product_id = 102;

-- NOT
SELECT * FROM sales
WHERE NOT (product_id = 101);

-- Комбинированные условия
SELECT * FROM sales
WHERE (product_id = 101 OR product_id = 102)
  AND quantity >= 2
  AND total > 50;
```

### `IN` и NOT `IN`

```sql
-- В списке значений
SELECT * FROM sales
WHERE product_id IN (101, 102, 103);

-- Не в списке
SELECT * FROM sales
WHERE product_id NOT IN (101, 102, 103);

-- Подзапрос с IN
SELECT * FROM sales
WHERE customer_id IN (
    SELECT customer_id FROM customers
    WHERE status = 'active'
);
```

### LIKE для строк

```sql
-- Создание таблицы с именами
CREATE TABLE users (
    id UInt64,
    name String,
    email String
) ENGINE = MergeTree() ORDER BY id;

INSERT INTO users VALUES
    (1, 'John Doe', 'john@example.com'),
    (2, 'Jane Smith', 'jane@example.com'),
    (3, 'Bob Johnson', 'bob@example.com');

-- Поиск по шаблону
SELECT * FROM users WHERE name LIKE 'John%';      -- Начинается с "John"
SELECT * FROM users WHERE name LIKE '%Smith';     -- Заканчивается "Smith"
SELECT * FROM users WHERE name LIKE '% Doe %';    -- Содержит " Doe "
SELECT * FROM users WHERE name NOT LIKE '%Bob%';  -- Не содержит "Bob"
```

### Работа с NULL

```sql
-- Создание таблицы с nullable полями
CREATE TABLE products (
    id UInt64,
    name String,
    description Nullable(String),
    price Nullable(Decimal64(2))
) ENGINE = MergeTree() ORDER BY id;

-- Проверка на NULL
SELECT * FROM products WHERE description IS NULL;
SELECT * FROM products WHERE description IS NOT NULL;

-- Функции для работы с NULL
SELECT
    id,
    name,
    ifNull(description, 'No description') as description,
    coalesce(price, 0) as price
FROM products;
```

### Диапазоны и BETWEEN

```sql
-- BETWEEN для диапазонов
SELECT * FROM sales
WHERE total BETWEEN 50 AND 100;

-- Эквивалентно
SELECT * FROM sales
WHERE total >= 50 AND total <= 100;

-- Для дат
SELECT * FROM sales
WHERE date BETWEEN '2024-01-01' AND '2024-01-31';
```

## Сортировка и пагинация

### ORDER `BY`

```sql
-- Сортировка по возрастанию (по умолчанию)
SELECT * FROM sales ORDER BY total;

-- Сортировка по убыванию
SELECT * FROM sales ORDER BY total DESC;

-- Сортировка по нескольким полям
SELECT * FROM sales
ORDER BY product_id ASC, total DESC;

-- Сортировка с NULL значениями
SELECT * FROM products
ORDER BY price NULLS FIRST;  -- NULL значения первыми

SELECT * FROM products
ORDER BY price NULLS LAST;   -- NULL значения последними
```

### LIMIT и OFFSET

```sql
-- Ограничение количества результатов
SELECT * FROM sales ORDER BY total DESC LIMIT 10;

-- Пагинация (неэффективно для больших таблиц)
SELECT * FROM sales ORDER BY total DESC LIMIT 10 OFFSET 20;

-- Лучшая пагинация для больших таблиц
SELECT * FROM sales
WHERE total < 1000  -- Используем значение из предыдущей страницы
ORDER BY total DESC
LIMIT 10;
```

## Агрегатные функции

### Основные агрегаты

```sql
-- Количество записей
SELECT count() FROM sales;              -- Все строки
SELECT count(*) FROM sales;             -- Все строки (эквивалентно)
SELECT count(product_id) FROM sales;    -- Не-NULL значения

-- Сумма и среднее
SELECT
    sum(total) as total_sum,
    avg(total) as average_total,
    min(total) as min_total,
    max(total) as max_total
FROM sales;

-- Статистические функции
SELECT
    stddevPop(total) as std_deviation,
    varPop(total) as variance,
    quantile(0.5)(total) as median,
    quantile(0.95)(total) as percentile_95
FROM sales;

-- Уникальные значения
SELECT
    uniq(customer_id) as unique_customers,
    uniqExact(product_id) as exact_unique_products
FROM sales;
```

### Агрегаты с условиями

```sql
-- Условные агрегаты
SELECT
    countIf(total > 100) as expensive_orders,
    sumIf(total, total > 100) as expensive_total,
    avgIf(total, product_id = 101) as avg_product_101
FROM sales;

-- Агрегаты по группам с условиями
SELECT
    product_id,
    count() as total_orders,
    countIf(quantity > 1) as bulk_orders,
    sumIf(total, quantity > 1) as bulk_total
FROM sales
GROUP BY product_id;
```

## Группировка данных

### GROUP `BY`

```sql
-- Группировка по продукту
SELECT
    product_id,
    count() as orders_count,
    sum(quantity) as total_quantity,
    sum(total) as total_amount
FROM sales
GROUP BY product_id;

-- Группировка по нескольким полям
SELECT
    date,
    product_id,
    count() as orders_count,
    sum(total) as daily_total
FROM sales
GROUP BY date, product_id
ORDER BY date, product_id;

-- Группировка с вычислениями
SELECT
    toStartOfMonth(date) as month,
    product_id,
    count() as monthly_orders,
    sum(total) as monthly_revenue,
    avg(total) as avg_order_value
FROM sales
GROUP BY month, product_id
ORDER BY month, monthly_revenue DESC;
```

### HAVING — фильтрация групп

```sql
-- Фильтрация после группировки
SELECT
    product_id,
    sum(total) as total_revenue
FROM sales
GROUP BY product_id
HAVING total_revenue > 1000
ORDER BY total_revenue DESC;

-- HAVING с несколькими условиями
SELECT
    customer_id,
    count() as orders_count,
    sum(total) as total_spent
FROM sales
GROUP BY customer_id
HAVING orders_count >= 3 AND total_spent > 500
ORDER BY total_spent DESC;
```

### WITH ROLLUP, CUBE, TOTALS

```sql
-- WITH ROLLUP - промежуточные итоги
SELECT
    toStartOfMonth(date) as month,
    product_id,
    sum(total) as monthly_total
FROM sales
GROUP BY month, product_id
WITH ROLLUP
ORDER BY month, product_id;

-- WITH CUBE - все комбинации
SELECT
    toStartOfMonth(date) as month,
    product_id,
    sum(total) as total
FROM sales
GROUP BY month, product_id
WITH CUBE
ORDER BY month, product_id;

-- WITH TOTALS - общие итоги
SELECT
    product_id,
    sum(total) as total
FROM sales
GROUP BY product_id
WITH TOTALS
ORDER BY total DESC;
```

## Оконные функции

### ROW_NUMBER, RANK, DENSE_RANK

```sql
-- Нумерация строк
SELECT
    product_id,
    total,
    row_number() OVER (ORDER BY total DESC) as row_num
FROM sales;

-- Рейтинг по продуктам
SELECT
    product_id,
    date,
    total,
    rank() OVER (PARTITION BY product_id ORDER BY total DESC) as product_rank,
    dense_rank() OVER (PARTITION BY product_id ORDER BY total DESC) as dense_product_rank
FROM sales
ORDER BY product_id, product_rank;
```

### LAG и LEAD

```sql
-- Предыдущие и следующие значения
SELECT
    date,
    total,
    lag(total) OVER (ORDER BY date) as prev_day_total,
    lead(total) OVER (ORDER BY date) as next_day_total,
    total - lag(total) OVER (ORDER BY date) as day_over_day_change
FROM (
    SELECT
        date,
        sum(total) as total
    FROM sales
    GROUP BY date
    ORDER BY date
) t;
```

### FIRST_VALUE, LAST_VALUE

```sql
-- Первое и последнее значение в окне
SELECT
    date,
    product_id,
    total,
    first_value(total) OVER (PARTITION BY product_id ORDER BY date) as first_sale,
    last_value(total) OVER (PARTITION BY product_id ORDER BY date) as latest_sale
FROM sales
ORDER BY product_id, date;
```

### NTH_VALUE

```sql
-- N-е значение в окне
SELECT
    date,
    total,
    nth_value(total, 2) OVER (ORDER BY total DESC) as second_highest
FROM sales
ORDER BY total DESC;
```

### NTILE — разделение на группы

```sql
-- Разделение на квартили
SELECT
    product_id,
    total,
    ntile(4) OVER (ORDER BY total DESC) as quartile
FROM sales
ORDER BY total DESC;

-- Разделение на процентили
SELECT
    customer_id,
    total_spent,
    ntile(100) OVER (ORDER BY total_spent DESC) as percentile
FROM (
    SELECT
        customer_id,
        sum(total) as total_spent
    FROM sales
    GROUP BY customer_id
) t
ORDER BY total_spent DESC;
```

### Скользящие агрегаты

```sql
-- Скользящее среднее
SELECT
    date,
    total,
    avg(total) OVER (ORDER BY date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) as moving_avg_3days
FROM (
    SELECT
        date,
        sum(total) as total
    FROM sales
    GROUP BY date
) t
ORDER BY date;

-- Кумулятивная сумма
SELECT
    date,
    total,
    sum(total) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as cumulative_total
FROM (
    SELECT
        date,
        sum(total) as total
    FROM sales
    GROUP BY date
) t
ORDER BY date;
```

## JOIN операции

### INNER JOIN

```sql
-- Создание таблиц для примера
CREATE TABLE customers (
    customer_id UInt64,
    name String,
    city String
) ENGINE = MergeTree() ORDER BY customer_id;

CREATE TABLE orders (
    order_id UInt64,
    customer_id UInt64,
    order_date Date,
    total Decimal64(2)
) ENGINE = MergeTree() ORDER BY customer_id;

-- INNER JOIN
SELECT
    c.customer_id,
    c.name,
    c.city,
    o.order_id,
    o.order_date,
    o.total
FROM customers c
INNER JOIN orders o ON c.customer_id = o.customer_id
ORDER BY c.customer_id, o.order_date;
```

### LEFT JOIN

```sql
-- LEFT JOIN (все клиенты, даже без заказов)
SELECT
    c.customer_id,
    c.name,
    c.city,
    count(o.order_id) as orders_count,
    sum(o.total) as total_spent
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name, c.city
ORDER BY total_spent DESC NULLS LAST;
```

### RIGHT JOIN и FULL JOIN

```sql
-- RIGHT JOIN (все заказы, даже без клиентов - редко используется)
SELECT
    c.name,
    o.order_id,
    o.total
FROM customers c
RIGHT JOIN orders o ON c.customer_id = o.customer_id;

-- FULL JOIN (все записи из обеих таблиц)
SELECT
    c.customer_id,
    c.name,
    o.order_id,
    o.total
FROM customers c
FULL JOIN orders o ON c.customer_id = o.customer_id;
```

### CROSS JOIN

```sql
-- Создание таблицы размеров
CREATE TABLE sizes (size String) ENGINE = MergeTree() ORDER BY size;
INSERT INTO sizes VALUES ('S'), ('M'), ('L'), ('XL');

-- CROSS JOIN (декартово произведение)
SELECT
    p.name,
    s.size
FROM products p
CROSS JOIN sizes s;
```

## Подзапросы

### Скалярные подзапросы

```sql
-- Максимальная сумма заказа
SELECT
    customer_id,
    total
FROM orders
WHERE total = (SELECT max(total) FROM orders);

-- Подзапрос в SELECT
SELECT
    customer_id,
    total,
    (SELECT avg(total) FROM orders) as avg_order_total
FROM orders
WHERE total > (SELECT avg(total) FROM orders);
```

### Подзапросы в FROM

```sql
-- Агрегация по клиентам
SELECT
    customer_id,
    orders_count,
    total_spent,
    avg_order_value
FROM (
    SELECT
        customer_id,
        count() as orders_count,
        sum(total) as total_spent,
        avg(total) as avg_order_value
    FROM orders
    GROUP BY customer_id
) t
WHERE orders_count >= 3
ORDER BY total_spent DESC;
```

### EXISTS и `IN` с подзапросами

```sql
-- Клиенты с заказами
SELECT * FROM customers c
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.customer_id = c.customer_id
);

-- Клиенты с дорогими заказами
SELECT * FROM customers
WHERE customer_id IN (
    SELECT customer_id FROM orders
    WHERE total > 1000
);
```

## Работа с массивами

### Создание таблицы с массивами

```sql
CREATE TABLE user_events (
    user_id UInt64,
    event_time DateTime,
    event_type String,
    tags Array(String),
    metrics Array(Float64),
    properties Map(String, String)
) ENGINE = MergeTree()
ORDER BY (user_id, event_time);

INSERT INTO user_events VALUES
(1, now(), 'page_view', ['web', 'mobile'], [1.5, 2.3], {'source': 'google', 'campaign': 'summer'}),
(2, now(), 'purchase', ['web', 'checkout'], [99.99, 1.0], {'payment': 'card', 'currency': 'USD'});
```

### Функции для работы с массивами

```sql
-- Длина массива
SELECT user_id, length(tags) as tags_count FROM user_events;

-- Доступ к элементам
SELECT
    user_id,
    tags[1] as first_tag,      -- Первый элемент (индексация с 1)
    tags[-1] as last_tag,      -- Последний элемент
    arraySlice(tags, 1, 2) as first_two_tags  -- Срез массива
FROM user_events;

-- Поиск в массиве
SELECT * FROM user_events
WHERE has(tags, 'web');  -- Есть ли элемент 'web'

SELECT * FROM user_events
WHERE arrayExists(x -> x = 'checkout', tags);  -- Есть ли элемент удовлетворяющий условию

-- Фильтрация массива
SELECT
    user_id,
    arrayFilter(x -> x > 1.0, metrics) as high_metrics
FROM user_events;

-- Агрегация массивов
SELECT
    user_id,
    arraySum(metrics) as total_metrics,
    arrayAvg(metrics) as avg_metric,
    arrayMax(metrics) as max_metric,
    arrayMin(metrics) as min_metric
FROM user_events;
```

### Преобразование массивов

```sql
-- Объединение массивов
SELECT arrayConcat(tags, ['new_tag']) as extended_tags
FROM user_events;

-- Уникальные значения
SELECT arrayDistinct(tags) as unique_tags
FROM user_events;

-- Сортировка
SELECT arraySort(tags) as sorted_tags
FROM user_events;

-- Пересечение и разность
SELECT
    user_id,
    arrayIntersect(tags, ['web', 'mobile']) as common_tags,
    arrayDifference(tags, ['web']) as other_tags
FROM user_events;
```

## Аналитические функции

### Ранжирование и нумерация

```sql
-- Топ продуктов по продажам
SELECT
    product_id,
    total_sales,
    row_number() OVER (ORDER BY total_sales DESC) as sales_rank,
    rank() OVER (ORDER BY total_sales DESC) as sales_rank_with_ties,
    dense_rank() OVER (ORDER BY total_sales DESC) as dense_sales_rank
FROM (
    SELECT
        product_id,
        sum(total) as total_sales
    FROM sales
    GROUP BY product_id
) t;

-- Процентильные ранги
SELECT
    customer_id,
    total_spent,
    ntile(4) OVER (ORDER BY total_spent DESC) as quartile,
    ntile(10) OVER (ORDER BY total_spent DESC) as decile
FROM (
    SELECT
        customer_id,
        sum(total) as total_spent
    FROM sales
    GROUP BY customer_id
) t;
```

### Статистические функции

```sql
-- Скользящие статистики
SELECT
    date,
    daily_sales,
    avg(daily_sales) OVER (ORDER BY date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) as week_avg,
    min(daily_sales) OVER (ORDER BY date ROWS BETWEEN 29 PRECEDING AND CURRENT ROW) as month_min,
    max(daily_sales) OVER (ORDER BY date ROWS BETWEEN 29 PRECEDING AND CURRENT ROW) as month_max
FROM (
    SELECT
        date,
        sum(total) as daily_sales
    FROM sales
    GROUP BY date
) t
ORDER BY date;
```

### Корреляционный анализ

```sql
-- Корреляция между ценой и количеством продаж
SELECT
    corr(price, quantity) as price_quantity_correlation,
    corr(price, total) as price_total_correlation,
    corr(quantity, total) as quantity_total_correlation
FROM sales;

-- Ковариация
SELECT
    covarPop(price, quantity) as price_quantity_covar,
    covarSamp(price, total) as price_total_covar
FROM sales;
```

## Оптимизация запросов

### Использование индексов

```sql
-- Создание индексов для ускорения запросов
CREATE TABLE optimized_sales (
    date Date,
    product_id UInt64,
    customer_id UInt64,
    quantity UInt32,
    price Decimal64(2),
    total Decimal64(2),

    -- Индексы для фильтрации
    INDEX idx_product_date product_id TYPE minmax GRANULARITY 1,
    INDEX idx_customer customer_id TYPE bloom_filter(0.01) GRANULARITY 1,
    INDEX idx_date date TYPE minmax GRANULARITY 1
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(date)
ORDER BY (date, product_id, customer_id);

-- Запросы будут использовать индексы автоматически
SELECT * FROM optimized_sales
WHERE product_id = 101 AND date >= '2024-01-01';
```

### PREWHERE для фильтрации

```sql
-- PREWHERE для предварительной фильтрации (быстрее WHERE для больших таблиц)
SELECT
    product_id,
    sum(total) as revenue
FROM sales
PREWHERE date >= '2024-01-01'  -- Фильтрация перед чтением данных
WHERE product_id IN (101, 102, 103)  -- Дополнительная фильтрация
GROUP BY product_id;
```

### SAMPLE для приближенных расчетов

```sql
-- Приближенные расчеты на сэмпле данных
SELECT
    product_id,
    sum(total) as approx_revenue,
    uniq(customer_id) as approx_unique_customers
FROM sales
SAMPLE 0.1  -- 10% данных
GROUP BY product_id;

-- Стратифицированный сэмпл
SELECT
    product_id,
    avg(price) as avg_price
FROM sales
SAMPLE 10000  -- Точное количество строк
GROUP BY product_id;
```

### UNION ALL для объединения

```sql
-- Объединение результатов из разных таблиц
SELECT product_id, 'current_month' as period, sum(total) as revenue
FROM sales_current_month
GROUP BY product_id

UNION ALL

SELECT product_id, 'last_month' as period, sum(total) as revenue
FROM sales_last_month
GROUP BY product_id
ORDER BY product_id, period;
```

## Лучшие практики

### Производительность запросов

1. **Избегайте `SELECT` \** для больших таблиц
   ```sql
   -- Плохо
   SELECT * FROM large_table;

   -- Хорошо
   SELECT needed_column1, needed_column2 FROM large_table;
   ```

2. **Используйте `PREWHERE` для фильтрации**
   ```sql
   -- Предварительная фильтрация по индексированным полям
   SELECT columns FROM table
   PREWHERE indexed_column = 'value'
   WHERE other_conditions;
   ```

3. **Оптимизируйте порядок JOIN**
   ```sql
   -- Маленькая таблица слева, большая справа
   SELECT * FROM small_table s
   JOIN large_table l ON s.id = l.small_id;
   ```

4. **Используйте подходящие типы данных**
   ```sql
   -- UInt32 вместо Int64 для положительных чисел
   -- Date вместо DateTime если не нужна точность до секунд
   -- Enum вместо String для ограниченного набора значений
   ```

### Аналитические запросы

1. **Используйте оконные функции вместо подзапросов**
   ```sql
   -- Эффективнее оконных функций
   SELECT
       product_id,
       total,
       sum(total) OVER (PARTITION BY category) as category_total
   FROM sales;
   ```

2. **Кэшируйте часто используемые агрегаты**
   ```sql
   -- Материализованные представления для частых запросов
   CREATE MATERIALIZED VIEW daily_stats
   ENGINE = SummingMergeTree()
   ORDER BY date
   AS SELECT
       date,
       count() as orders_count,
       sum(total) as revenue
   FROM sales
   GROUP BY date;
   ```

3. **Используйте приближенные функции для больших данных**
   ```sql
   -- uniq вместо uniqExact для больших наборов
   SELECT uniq(customer_id) FROM sales;  -- Приближенно
   SELECT uniqExact(customer_id) FROM sales;  -- Точно, но медленнее
   ```

### Мониторинг и отладка

```sql
-- Включение логирования медленных запросов
SET log_queries = 1;
SET log_queries_min_type = 'QUERY_FINISH';

-- Просмотр системных метрик
SELECT * FROM system.query_log
WHERE query_duration_ms > 1000
ORDER BY query_duration_ms DESC
LIMIT 10;

-- Статистика по таблицам
SELECT
    database,
    table,
    formatReadableSize(bytes) as size,
    formatReadableSize(bytes_on_disk) as compressed_size
FROM system.parts
WHERE database = 'default';
```

## Продвинутые аналитические возможности

### Работа с временными рядами

**ClickHouse** отлично подходит для анализа временных рядов благодаря оптимизациям для временных данных.

```sql
-- Анализ трендов по часам
SELECT
    toStartOfHour(timestamp) as hour,
    count() as events_count,
    quantileExact(0.95)(response_time) as p95_response_time,
    avg(response_time) as avg_response_time,
    min(response_time) as min_response_time,
    max(response_time) as max_response_time
FROM api_logs
WHERE timestamp >= now() - INTERVAL 7 DAY
GROUP BY hour
ORDER BY hour;

-- Скользящие средние и тренды
SELECT
    date,
    daily_sales,
    -- Простая скользящая средняя (7 дней)
    avg(daily_sales) OVER (
        ORDER BY date
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) as sma_7_days,

    -- Экспоненциальная скользящая средняя (примерная)
    daily_sales * 0.1 +
    lag(daily_sales, 1) OVER (ORDER BY date) * 0.9 as ema_approx
FROM (
    SELECT
        toDate(timestamp) as date,
        sum(amount) as daily_sales
    FROM sales
    WHERE timestamp >= '2024-01-01'
    GROUP BY date
    ORDER BY date
) t;

-- Сезонный анализ
SELECT
    toDayOfWeek(date) as day_of_week,
    toHour(timestamp) as hour,
    count() as events_count,
    avg(value) as avg_value
FROM events
WHERE timestamp >= now() - INTERVAL 30 DAY
GROUP BY day_of_week, hour
ORDER BY day_of_week, hour;
```

### Геопространственный анализ

**ClickHouse** поддерживает геопространственные функции для анализа геоданных.

```sql
-- Создание таблицы с геоданными
CREATE TABLE locations (
    id UInt64,
    name String,
    latitude Float64,
    longitude Float64,
    timestamp DateTime
) ENGINE = MergeTree()
ORDER BY (id, timestamp);

-- Вычисление расстояний
SELECT
    id,
    name,
    latitude,
    longitude,
    -- Расстояние до точки (55.7558, 37.6173) - Москва
    geoDistance(latitude, longitude, 55.7558, 37.6173) / 1000 as distance_km
FROM locations
ORDER BY distance_km;

-- Группировка по географическим регионам
SELECT
    floor(latitude, 1) as lat_group,
    floor(longitude, 1) as lng_group,
    count() as points_count,
    avg(geoDistance(latitude, longitude, 55.7558, 37.6173) / 1000) as avg_distance_km
FROM locations
GROUP BY lat_group, lng_group
ORDER BY points_count DESC;
```

### Статистический анализ

**ClickHouse** имеет богатый набор статистических функций.

```sql
-- Расширенная статистика
SELECT
    metric_name,
    count() as measurements_count,
    avg(value) as mean,
    quantileExact(0.5)(value) as median,
    quantileExact(0.25)(value) as q1,
    quantileExact(0.75)(value) as q3,
    stddevPop(value) as std_dev,
    skewnessPop(value) as skewness,
    kurtosisPop(value) as kurtosis,
    min(value) as min_value,
    max(value) as max_value
FROM metrics
WHERE timestamp >= now() - INTERVAL 1 DAY
GROUP BY metric_name;

-- Корреляционный анализ
SELECT
    corr(x, y) as correlation_xy,
    covarPop(x, y) as covariance_xy
FROM (
    SELECT
        measurement_a as x,
        measurement_b as y
    FROM sensor_data
    WHERE timestamp >= '2024-01-01'
) t;
```

## Оптимизация сложных запросов

### Стратегии оптимизации

1. **Предварительная фильтрация**
```sql
-- Хорошо: фильтрация перед агрегацией
SELECT category, sum(amount)
FROM transactions
WHERE date >= '2024-01-01'  -- Фильтр использует индекс
GROUP BY category;

-- Плохо: фильтрация после агрегации
SELECT category, total
FROM (
    SELECT category, sum(amount) as total
    FROM transactions
    GROUP BY category
) t
WHERE total > 1000;  -- Фильтр применяется к результатам
```

2. **Оптимизация `JOIN` операций**
```sql
-- Хорошо: JOIN с предварительной фильтрацией
SELECT p.name, c.category_name, p.price
FROM products p
JOIN categories c ON p.category_id = c.id
WHERE p.price > 100  -- Фильтр по основной таблице
  AND c.active = true;

-- Хорошо: использование индексов
CREATE TABLE products (
    id UInt64,
    category_id UInt64,
    name String,
    price Decimal64(2)
) ENGINE = MergeTree()
ORDER BY (category_id, price);  -- Индекс для JOIN и фильтрации
```

### Мониторинг производительности запросов

```sql
-- Анализ медленных запросов
SELECT
    query,
    query_duration_ms,
    read_rows,
    read_bytes,
    memory_usage,
    formatReadableSize(read_bytes) as readable_read_bytes
FROM system.query_log
WHERE query_duration_ms > 1000
  AND event_time >= now() - INTERVAL 1 HOUR
ORDER BY query_duration_ms DESC
LIMIT 10;

-- Статистика по использованию таблиц
SELECT
    database,
    table,
    sum(read_rows) as total_read_rows,
    sum(read_bytes) as total_read_bytes,
    count() as queries_count,
    avg(query_duration_ms) as avg_duration
FROM system.query_log
WHERE event_time >= now() - INTERVAL 1 DAY
  AND type = 'QueryFinish'
GROUP BY database, table
ORDER BY total_read_bytes DESC;
```

**ClickHouse** предоставляет мощный и эффективный **SQL**-интерфейс для аналитических запросов к большим данным. Ключевые особенности:**

### Основные возможности:

1. **Стандартный SQL** с расширениями для аналитики
2. **Высокая производительность** агрегаций и сортировок
3. **Оконные функции** для сложного анализа
4. **Работа с массивами и сложными типами**
5. **Оптимизации** для больших наборов данных
6. **Временные ряды** и геоанализ
7. **Статистические функции** для глубокого анализа

### Рекомендации по использованию:

- **Проектируйте схему** с учетом паттернов запросов
- **Используйте подходящие движки** для разных сценариев
- **Оптимизируйте запросы** с помощью индексов и **PREWHERE**
- **Мониторьте производительность** и используйте приближенные функции
- **Кэшируйте результаты** с помощью материализованных представлений

### Продвинутые возможности:

- **Анализ временных рядов** с оконными функциями
- **Геопространственный анализ** с геофункциями
- **Статистический анализ** с квантилями и распределениями
- **Анализ сессий** и последовательностей
- **Funnel анализ** для маркетинговых исследований

### Следующие темы:

- **Индексы и оптимизация** — глубокое погружение в индексы
- **Материализованные представления** — автоматизация расчетов
- **Репликация и кластеры** — масштабирование и отказоустойчивость
- **Производительность** — тюнинг и мониторинг

**ClickHouse** продолжает развиваться и становится стандартом для аналитики больших данных в современной инфраструктуре.

## Решение проблем

**Медленные запросы:** убедитесь, что в `WHERE` используются ключевые столбцы и партиции. Добавьте индексы (в т.ч. по выражению), уменьшите объём читаемых данных. Проверьте `EXPLAIN` и метрики (read rows, elapsed). Избегайте тяжёлых `JOIN` без фильтров.

**Таймауты (timeout exceeded):** увеличьте `max_execution_time` или настройки на стороне клиента для тяжёлых запросов. В приоритете — оптимизация: ограничьте диапазоны дат, используйте предагрегированные данные (материализованные представления, предрасчёт).

**Нехватка памяти (Memory limit exceeded):** уменьшите размер джойнов или агрегаций, увеличьте `max_memory_usage` на сервере. Разбейте запрос на части (по партициям или подвыборкам). Проверьте настройки для больших `GROUP BY` и `DISTINCT`.

**Пустые или неверные результаты:** проверьте типы данных и приведение (например, даты и таймзоны). Учитывайте семантику `FINAL` для движков типа ReplacingMergeTree. При работе с NULL убедитесь в корректности условий в `WHERE` и `JOIN`.


**Следующие темы:**
- [Индексы и оптимизация](clickhouse-indexes.md)
- [Материализованные представления](clickhouse-materialized-views.md)

