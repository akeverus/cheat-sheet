---
title: "Вопросы на собеседовании: Database Performance"
description: "Database performance tuning: indexes, query plans, N+1, connection pooling, EXPLAIN ANALYZE, partitioning, stats, slow query log, pgbench, Aurora"
tags:
  - interview
  - performance
  - database-performance-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Database Performance"
  - "Database Performance interview"
  - "Query optimization"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Database Performance`

`Database Performance` — **топ-1 причина медленных приложений**. В 90% случаев медленные ответы = плохой запрос, отсутствующий индекс, N+1, исчерпанный connection pool. Знание EXPLAIN, pg_stat_statements, execution plan — обязательный навык senior-backend.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Performance Tuning](https://wiki.postgresql.org/wiki/Performance_Optimization)
- [MySQL Performance Schema](https://dev.mysql.com/doc/refman/8.0/en/performance-schema.html)
- [Use the Index, Luke](https://use-the-index-luke.com/) — Markus Winand
- [pgtune](https://pgtune.leopard.in.ua/) — config calculator
- [PostgreSQL EXPLAIN](https://www.postgresql.org/docs/current/sql-explain.html)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Диагностика**
- [Q1. (!) Как диагностировать slow query?](#q1--как-диагностировать-slow-query)
- [Q2. (!) EXPLAIN vs EXPLAIN ANALYZE?](#q2--explain-vs-explain-analyze)
- [Q3. (!) Как читать execution plan?](#q3--как-читать-execution-plan)
- [Q4. Seq Scan vs Index Scan vs Bitmap Scan?](#q4-seq-scan-vs-index-scan-vs-bitmap-scan)

**Индексы**
- [Q5. (!) Когда index помогает, когда не помогает?](#q5--когда-index-помогает-когда-не-помогает)
- [Q6. (!) Composite index column order?](#q6--composite-index-column-order)
- [Q7. Covering index (INCLUDE)?](#q7-covering-index-include)
- [Q8. Partial index?](#q8-partial-index)
- [Q9. Index bloat и REINDEX?](#q9-index-bloat-и-reindex)

**N+1 и ORM**
- [Q10. (!) N+1 problem — как обнаружить и исправить?](#q10--n1-problem--как-обнаружить-и-исправить)
- [Q11. JOIN FETCH vs subselect vs batch size?](#q11-join-fetch-vs-subselect-vs-batch-size)

**Connection management**
- [Q12. (!) Connection pooling — зачем?](#q12--connection-pooling--зачем)
- [Q13. (!) HikariCP settings?](#q13--hikaricp-settings)
- [Q14. PgBouncer transaction vs session pooling?](#q14-pgbouncer-transaction-vs-session-pooling)

**Партицирование и шардирование**
- [Q15. (!) Partitioning — когда применять?](#q15--partitioning--когда-применять)
- [Q16. Range / List / Hash partitioning?](#q16-range--list--hash-partitioning)

**Статистика и vacuum**
- [Q17. (!) ANALYZE и статистика оптимизатора?](#q17--analyze-и-статистика-оптимизатора)
- [Q18. VACUUM, autovacuum, bloat?](#q18-vacuum-autovacuum-bloat)

**Конфигурация**
- [Q19. (!) shared_buffers, work_mem, effective_cache_size?](#q19--shared_buffers-work_mem-effective_cache_size)
- [Q20. WAL и checkpoint tuning?](#q20-wal-и-checkpoint-tuning)

**Оптимизация запросов**
- [Q21. (!) LIMIT + OFFSET проблема pagination?](#q21--limit--offset-проблема-pagination)
- [Q22. JOIN vs subquery vs EXISTS?](#q22-join-vs-subquery-vs-exists)
- [Q23. Window functions performance?](#q23-window-functions-performance)
- [Q24. Materialized views vs views?](#q24-materialized-views-vs-views)

**Production**
- [Q25. (!) Read replicas — когда и как?](#q25--read-replicas--когда-и-как)
- [Q26. (!) Как находить slow queries в prod?](#q26--как-находить-slow-queries-в-prod)
- [Q27. Database load test (pgbench, sysbench)?](#q27-database-load-test-pgbench-sysbench)

## Q1. (!) Как диагностировать slow query?

(!) Как диагностировать slow query?

**Систематический подход:**

**1. Найти проблемный запрос:**
- APM-инструмент (DataDog, New Relic) показывает медленный endpoint
- `pg_stat_statements` — топ-N запросов по суммарному / среднему времени
- Slow query log (порог, например 1 с)

**2. Воспроизвести:**
- Получить запрос + параметры
- Прогнать в тестовом окружении на объёме данных, близком к прод

**3. EXPLAIN ANALYZE:**
```sql
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE user_id = 123 AND status = 'PAID';
```

**4. Найти узкое место:**
- **Seq Scan** большой таблицы → отсутствует индекс?
- **Sort** с уходом на диск → слишком маленький work_mem?
- **Nested Loop** на многомиллионных таблицах → неверный план; порядок join?
- **Высокий Buffers: read** → промах кэша; холодные данные

**5. Сформулировать гипотезу фикса:**
- Добавить индекс
- Переписать запрос
- Подкрутить work_mem / конфиг
- Партицировать таблицу

**6. Проверить:**
- EXPLAIN ANALYZE после изменения
- Сравнить: planning time, execution time, rows, buffers

**7. Мониторить:**
- Выкатить в прод
- Следить за метриками (latency, время запроса)

**Чек-лист на первом касании:**
- Есть индекс на колонках WHERE?
- Статистика актуальна (`ANALYZE`)?
- Запрос возвращает вменяемое число строк (не SELECT *)?
- Нет ли несовпадения типов (функция на индексируемой колонке: `WHERE LOWER(email) =` ломает индекс)?

## Q2. (!) EXPLAIN vs EXPLAIN ANALYZE?

**EXPLAIN:** показывает **планируемый** план выполнения (оценки).
- Быстро, без реального выполнения
- Оценочные costs / rows
- Гипотетический

**EXPLAIN ANALYZE:** **реально выполняет** запрос и замеряет.
- Реальные тайминги, реальное число строк
- Выявляет **расхождение оценки и факта** (плохая статистика!)
- Медленные запросы выполняются столько же (ANALYZE не пропускает выполнение)
- **Внимание:** DML реально выполняется! Используйте `BEGIN; EXPLAIN ANALYZE UPDATE ...; ROLLBACK;`

**Полезные опции:**
```sql
EXPLAIN (ANALYZE, BUFFERS, VERBOSE, FORMAT JSON)
SELECT ...;
```

- **BUFFERS:** показывает shared buffers hit / read (эффективность кэша)
- **VERBOSE:** выводит списки колонок
- **FORMAT JSON/YAML/XML:** для инструментов (например, визуализатор pev2)
- **SETTINGS:** показывает не-дефолтные значения конфига
- **WAL:** число записанных WAL-записей (PG 13+)

**Как читать вывод:**
- `actual rows=100` против `rows=10000` → оценка ошибается в 100× → статистика устарела, нужен `ANALYZE`
- `Buffers: shared hit=X read=Y` — read = диск, hit = кэш; высокий read = холодные данные
- `Execution Time: 234 ms` — реальная длительность

**Визуализаторы:**
- **pev2** / **explain.depesz.com** — узкие места с цветовой подсветкой
- **Dalibo Visual Explain**

**Подвох:** холодный прогон не равен тёплому. Прогоните 2 раза — первый бьёт по диску, второй из кэша. Чтобы это увидеть, используйте `BUFFERS`.

## Q3. (!) Как читать execution plan?

**План — это дерево операций**, выполнение снизу вверх:

```
Nested Loop  (cost=0.42..8.46 rows=1 width=128) (actual time=0.05..0.12 rows=1 loops=1)
  -> Index Scan using users_pkey on users  (cost=0.28..8.29 rows=1 width=64)
       Index Cond: (id = 123)
  -> Index Scan using orders_user_id_idx on orders  (cost=0.14..0.16 rows=1 width=64)
       Index Cond: (user_id = users.id)
       Filter: (status = 'PAID')
```

**Как читать:**
- **Отступ:** глубже = выполняется раньше (сначала листья)
- Каждый узел: операция, cost, rows, width, actual time
- `cost=startup..total` (не мс, абстрактные единицы; важно соотношение)
- `rows`: оценка; `actual time`: реальный замер
- `loops`: число выполнений (умножьте actual time × loops для реального итога)

**Ключевые типы узлов:**
- **Seq Scan:** полное сканирование таблицы
- **Index Scan:** идёт по указателям индекса к строкам
- **Index Only Scan:** индекс содержит все нужные колонки (быстро)
- **Bitmap Heap Scan + Bitmap Index Scan:** пакетное чтение, отсортированное по страницам диска
- **Hash Join:** строит хэш меньшей таблицы
- **Merge Join:** обе стороны отсортированы; сливает их
- **Nested Loop:** для каждой строки внешней таблицы сканирует внутреннюю
- **Sort:** сортирует результат; уход на диск — плохо
- **Aggregate / HashAggregate:** GROUP BY
- **Gather / Gather Merge:** объединение результатов параллельных воркеров

**Тревожные признаки:**
- `rows=1 (actual rows=1000)` — грубый просчёт оценки
- `Sort Method: external merge Disk: 500MB` — слишком маленький work_mem
- `Seq Scan` большой таблицы без фильтра → отсутствует индекс
- `Nested Loop` с большой внешней таблицей → плохо; ожидался Hash Join
- Высокий `Rows Removed by Filter` — фильтр должен быть в index condition

## Q4. Seq Scan vs Index Scan vs Bitmap Scan?

**Seq Scan (Sequential Scan):**
- Читает таблицу страница за страницей от начала до конца
- **Быстро для:** маленькая таблица, выборка ≥ ~30% строк
- **Медленно для:** большая таблица, поиск иголки в стоге сена
- Оптимален для `COUNT(*)` без WHERE

**Index Scan:**
- Проходит по индексу → достаёт строки из таблицы (случайный I/O)
- **Быстро для:** селективных запросов (мало строк)
- **Медленно для:** неселективных (много случайных чтений страниц)
- Возвращает строки **в порядке индекса**

**Index Only Scan:**
- Все нужные колонки есть в индексе → обращения к таблице нет
- Проверяется visibility map (MVCC в PostgreSQL)
- Самый быстрый

**Bitmap Index Scan + Bitmap Heap Scan:**
- Шаг 1: сканирует индекс → bitmap из TID
- Шаг 2: сортирует bitmap по страницам, читает heap **последовательно**
- **Быстро для:** средней селективности (10-30% строк)
- Результат не упорядочен

**Как планировщик выбирает:**
- Селективность по статистике
- Стоимость случайного и последовательного I/O (`random_page_cost`, `seq_page_cost`)
- По умолчанию `random_page_cost=4` — расчёт на HDD; для SSD ставьте `=1.1` (большой прирост производительности!)

**Пример:**
```sql
-- 1M rows, returning 100 rows → Index Scan
EXPLAIN SELECT * FROM orders WHERE user_id = 42;

-- 1M rows, returning 500K → Seq Scan
EXPLAIN SELECT * FROM orders WHERE amount > 10;
```

## Q5. (!) Когда index помогает, когда не помогает?

**Индекс помогает:**
- `WHERE col = value` (равенство)
- `WHERE col > X` (диапазон) — B-tree упорядочен
- `ORDER BY col` — избегаем сортировки
- `GROUP BY col` — кластеризация
- `JOIN ON a.col = b.col`

**Индекс НЕ помогает:**

**1. Функция над колонкой:**
```sql
WHERE LOWER(email) = 'x@y.com'  -- normal index не используется!
```
Решение: функциональный индекс: `CREATE INDEX ON t (LOWER(email));`

**2. Wildcard в начале:**
```sql
WHERE name LIKE '%john%'  -- no index (B-tree prefix only)
```
Решение: триграммный индекс (`pg_trgm`) или full-text search

**3. Несовпадение типов:**
```sql
-- col is VARCHAR, passing INT
WHERE phone = 123456  -- implicit cast breaks index
```

**4. Неселективный запрос:**
- Возвращает > 30% строк → выигрывает Seq Scan
- например, `WHERE active = true`, когда 90% строк активны — индекс бесполезен

**5. OR без индексов на обе колонки:**
```sql
WHERE a = 1 OR b = 2  -- if only a indexed, partial
```
Решение: индекс на обе колонки ИЛИ запросы через UNION

**6. NOT IN / != (в некоторых случаях):**
- Планировщик может выбрать seq scan

**7. Перекос распределения данных:**
- 99% `status='ACTIVE'`, 1% `status='DELETED'`
- Запросу `WHERE status='DELETED'` нужен индекс
- Запросу `WHERE status='ACTIVE'` нужен seq scan
- Решение: **partial index** для `WHERE status='DELETED'`

**Проверить использование индексов:**
```sql
SELECT * FROM pg_stat_user_indexes WHERE idx_scan = 0;  -- unused indexes
```

## Q6. (!) Composite index column order?

**Правило:** «самая селективная колонка первой» — миф; **реальное правило: паттерн доступа запросов.**

**Правило левого префикса:**
Индекс `(a, b, c)` может использоваться для:
- `WHERE a = ?`
- `WHERE a = ? AND b = ?`
- `WHERE a = ? AND b = ? AND c = ?`
- НЕ для `WHERE b = ?` (пропускает ведущую колонку!)
- НЕ для `WHERE a = ? AND c = ?` (использует только `a`)

**Порядок по паттерну запроса:**
1. **Сначала колонки равенства** (слева)
2. **Диапазон в конце** (ломает префикс для следующих колонок)
3. **Колонки сортировки** после равенства

**Пример:**
Запросы:
- `WHERE user_id = ? AND created_at > ?`
- `WHERE user_id = ? ORDER BY created_at DESC`

Индекс: `(user_id, created_at)` — помогает обоим запросам.

**Плохо:** `(created_at, user_id)` — первый запрос делает range scan, второй не может опереться на сортировку.

**Несколько нагрузок:**
Если разные запросы затрагивают разные колонки, может понадобиться несколько индексов. Но это стоит:
- Накладные расходы на запись (INSERT/UPDATE обновляют все индексы)
- Место на диске
- Сопровождение

**Эвристика:** < 10 индексов на таблицу; больше — пересмотрите паттерны.

**Бонус index-only scan:**
Включите часто выбираемые колонки:
```sql
CREATE INDEX idx ON orders (user_id, created_at) INCLUDE (amount, status);
```

## Q7. Covering index (INCLUDE)?

**Covering index** — содержит все колонки, нужные запросу (SELECT + WHERE), что позволяет сделать **Index Only Scan**.

**Старый способ (до PG 11):**
```sql
CREATE INDEX ON orders (user_id, amount, status);
```
- Все колонки в ключе B-tree → индекс больше, влияет на упорядочивание

**Современный способ (PG 11+):**
```sql
CREATE INDEX ON orders (user_id) INCLUDE (amount, status);
```
- `user_id` в ключе (отсортирован, по нему ищут)
- `amount, status` в **листовых страницах** (не отсортированы, просто хранятся)
- Меньше, чем полный многоколоночный индекс
- Index Only Scan по-прежнему работает

**Выгода:** избегаем обращения к heap = быстрее.

**Запрос:**
```sql
SELECT amount, status FROM orders WHERE user_id = 42;
```
Plan:
```
Index Only Scan using idx on orders
  Index Cond: (user_id = 42)
  Heap Fetches: 0
```

**Подвох MVCC:**
- Visibility map должна показывать страницу как all-visible (после VACUUM)
- Если строки недавно обновлялись → "Heap Fetches: N" → это не чистый index-only

**Компромисс:**
- Рост размера
- Накладные расходы на запись
- Лучше всего для горячих путей чтения

## Q8. Partial index?

**Partial index** — индекс только по подмножеству строк (clause `WHERE` в CREATE INDEX).

```sql
CREATE INDEX idx_pending_orders ON orders (created_at)
WHERE status = 'PENDING';
```

**Сценарии:**
- Перекошенные данные (99% строк имеют одно значение) → индексируем только редкие значения
- Soft delete: `WHERE deleted_at IS NULL` — индексируем только активные строки
- Горячий паттерн запросов

**Выгоды:**
- **Меньше** (подмножество строк)
- **Быстрее** запись (обновляются только совпадающие строки)
- **Быстрее** чтение (меньше данных для обхода)

**Пример экономии:**
- Таблица 100M строк, 99M завершённых, 1M в ожидании
- Полный индекс: 100M записей
- Частичный (`WHERE status='PENDING'`): 1M записей — в 100× меньше

**Запрос должен точно совпадать с предикатом:**
```sql
-- Works (matches WHERE)
SELECT ... FROM orders WHERE status='PENDING' AND created_at > ...;

-- Doesn't use index (no status filter)
SELECT ... FROM orders WHERE created_at > ...;
```

**Планировщик проверяет:** предикат запроса вытекает из предиката индекса → индекс используется.

**Частые паттерны:**
- `WHERE enabled = true`
- `WHERE deleted_at IS NULL`
- `WHERE region = 'US'` для запросов по конкретному региону

## Q9. Index bloat и REINDEX?

**Index bloat** — страницы заполнены частично (UPDATE/DELETE оставляют dead tuples). Индекс разрастается сверх объёма данных → сканирование медленнее, больше I/O.

**Причины:**
- UPDATE = MVCC-вставка + пометка старой версии мёртвой
- DELETE помечает строку мёртвой; VACUUM удаляет её со временем
- Длинные транзакции мешают очистке

**Как обнаружить:**
```sql
SELECT schemaname, tablename, indexname,
       pg_size_pretty(pg_relation_size(indexrelid)) AS size
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC;
```

Или расширение `pgstattuple`:
```sql
SELECT * FROM pgstatindex('idx_name');
-- leaf_fragmentation, avg_leaf_density
```

Низкий `avg_leaf_density` (< 50%) → bloat.

**Исправление:**

**REINDEX:**
```sql
REINDEX INDEX idx_name;          -- locks writes (PG <12)
REINDEX INDEX CONCURRENTLY ...;  -- non-blocking (PG 12+)
```

**CREATE + DROP (до 12):**
```sql
CREATE INDEX CONCURRENTLY idx_new ON t (...);
DROP INDEX idx_old;
ALTER INDEX idx_new RENAME TO idx_old;
```

**pg_repack / pg_squeeze:** онлайн-перестроение таблицы и индексов без долгого lock.

**Профилактика:**
- Регулярный autovacuum (правильно настроенный)
- Избегайте очень длинных транзакций (xmin horizon блокирует очистку)
- **HOT-обновления** (индексируемая колонка не менялась) не раздувают индексы

## Q10. (!) N+1 problem — как обнаружить и исправить?

**N+1:** 1 запрос на список + N запросов (по одному на элемент) на связанную сущность.

**Пример (JPA/Hibernate):**
```java
List<Order> orders = orderRepo.findAll();  // 1 query
for (Order o : orders) {
    System.out.println(o.getUser().getName());  // N queries lazy load!
}
```
→ 1 + 100 = 101 обращений к БД для 100 заказов.

**Как обнаружить:**
- **APM** (трассировка DataDog) — спаны показывают пачку однотипных запросов
- **Статистика Hibernate:** `hibernate.generate_statistics=true`; логируем `queryExecutionCount`
- **p6spy** — логирует весь SQL
- **JPA Buddy / QuickPerf** — тесты утверждают отсутствие N+1

**Исправления:**

**JOIN FETCH:**
```java
@Query("SELECT o FROM Order o JOIN FETCH o.user WHERE ...")
```
Один запрос с JOIN → 1 запрос вместо 101.

**EntityGraph:**
```java
@EntityGraph(attributePaths = {"user", "items"})
List<Order> findAll();
```

**Batch fetch size:**
```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
```
Группирует N запросов в пачки IN (...) по 20.

**DTO-проекция:**
```java
@Query("SELECT new com.OrderDto(o.id, u.name) FROM Order o JOIN o.user u")
```
Прямой плоский запрос, без графа сущностей.

**Что выбрать:**
- Маленькая коллекция: JOIN FETCH
- Пагинация many-to-many: batch fetch (JOIN FETCH даёт дубликаты)
- Только чтение: DTO-проекция (самое быстрое)

**Не только Hibernate:** так ведёт себя любой ORM в связке с циклами.

## Q11. JOIN FETCH vs subselect vs batch size?

**Сценарий:** загрузить `Order` + `OrderItems`.

**JOIN FETCH:**
```sql
SELECT o.*, i.*
FROM orders o
LEFT JOIN order_items i ON i.order_id = o.id
```
- **1 запрос**
- Декартов взрыв при нескольких коллекциях
- **Пагинация ломается** (Hibernate грузит всё и пагинирует в памяти — выдаёт предупреждение)

**Batch (subselect) fetching:**
```properties
hibernate.batch_fetch_style=dynamic
hibernate.default_batch_fetch_size=16
```
Hibernate выполняет:
```sql
SELECT * FROM orders WHERE id IN (1, 2, ..., 16);
SELECT * FROM order_items WHERE order_id IN (1, 2, ..., 16);
```
- 2 запроса на 16 заказов
- Без декартова произведения
- Работает с пагинацией

**Subselect fetch:**
```java
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.SUBSELECT)
Collection<Item> items;
```
Выполняет исходный запрос как подзапрос:
```sql
SELECT * FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE ...)
```

**Сравнение:**

| Стратегия | Запросов | Декартово произведение | Пагинация |
|----------|---------|-----------|------------|
| JOIN FETCH | 1 | Да (плохо для нескольких коллекций) | Ломается для коллекций |
| Batch | ~N/batch_size | Нет | OK |
| Subselect | 2 | Нет | OK, но переигрывает фильтр |

**Эвристика:**
- Одна коллекция, мало родителей: JOIN FETCH
- Несколько коллекций: batch_fetch_size=20 по умолчанию
- Очень большой объём: DTO-проекция

## Q12. (!) Connection pooling — зачем?

**Открытие соединения** — дорого:
- TCP handshake
- TLS handshake (~100+ мс по WAN)
- Аутентификация в БД
- Форк backend-процесса (в PostgreSQL — `connection_pid`)

**Без пула:** каждый запрос открывает + закрывает соединение — **последовательное узкое место**, плюс БД ограничивает общее число соединений.

**Пул:**
- Держит N открытых соединений
- Запрос берёт соединение; возвращает по завершении
- Ставит в очередь, если все заняты

**Выгоды:**
- Убираем накладные расходы на соединение (экономия мс)
- Ограничиваем число одновременных соединений к БД (стабильность бэкенда)
- Быстрее (latency подключения = 0)
- Контроль ресурсов

**Типичные пулы:**
- **HikariCP** (Java) — самый быстрый, дефолт в Spring Boot
- **pgbouncer** (отдельный) — перед PostgreSQL
- **RDS Proxy** (AWS) — управляемый
- **Node pg-pool**, **Python psycopg2 pool**

**Архитектурное решение:**
- **Пул на стороне приложения:** просто, in-process
- **Внешний пул (pgbouncer, RDS Proxy):** между приложением и БД; можно делить пул между несколькими приложениями/инстансами
- **Оба:** пул приложения → внешний пул → БД (типично для serverless)

**Размер:**
- Правило: `pool = cores × 2 + spindles` (старое правило)
- Современный подход: бенчмарк; слишком много = вредит переключение контекста
- Типичный бэкенд: 10-30 на инстанс

**Внимание:** serverless (Lambda) без пулинга = взрыв числа соединений к БД; всегда используйте RDS Proxy.

## Q13. (!) HikariCP settings?

**HikariCP** — дефолт в Spring Boot. Минимум конфига, быстрый.

**Ключевые свойства (`spring.datasource.hikari.*`):**

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000    # 30s wait for connection
      idle-timeout: 600000         # 10 min evict idle
      max-lifetime: 1800000        # 30 min recycle connections
      keepalive-time: 120000       # 2 min ping idle conn (Hikari 4.0+)
      leak-detection-threshold: 60000  # 60s — warn if not returned
```

**Рекомендации по размеру:**
- **maximum-pool-size:** начните с 10, подкручивайте по нагрузке + DB max_connections
- **minimum-idle:** обычно = max (держать прогретым) для стабильного latency
- **Формула:** суммарное число соединений по всем инстансам ≤ DB `max_connections × 0.8`

**Частая проблема:**
- Приложение из 10 инстансов × пул 20 = 200 соединений
- DB max = 100 → падение

**Connection timeout:** приложение не виснет в бесконечном ожидании — быстро падает и возвращает 503.

**max-lifetime:** важно! Предотвращает устаревшие соединения (фаерволы убивают idle, рестарт БД).

**leak-detection-threshold:** логирует stack trace, если соединение держится дольше порога → находит пропущенный `try-with-resources`.

**Метрики:** экспонируем через Micrometer:
```
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.acquire  # time histogram
```

**Алерты:** устойчивое `pending > 0` → пул недостаточного размера.

## Q14. PgBouncer transaction vs session pooling?

**PgBouncer** — лёгкий пулер соединений для PostgreSQL (прокси).

**Режимы:**

**Session pooling (по умолчанию):**
- Клиент получает соединение с БД на **всю сессию** (connect → disconnect)
- Почти как без пулинга (кроме переиспользования соединения после disconnect)
- Безопасен для всех возможностей (prepared statements, temp tables, listen/notify)
- Малый прирост эффективности

**Transaction pooling:**
- Соединение выдаётся **на транзакцию**, возвращается на COMMIT/ROLLBACK
- **Огромная эффективность:** 1000 клиентов могут делить 20 соединений к БД
- **Ограничения:**
  - Нет prepared statements (привязаны к сессии) — кроме PG 14+ и PgBouncer 1.22+ с поддержкой на уровне протокола
  - Нет `SET` (привязан к сессии)
  - Нет temp tables
  - Нет `LISTEN/NOTIFY`
  - Ограничения по курсорам

**Statement pooling:**
- На каждый statement (редко, крайне жёсткие ограничения)

**Выбор:**
- **Session:** легаси-приложения, где используются эти возможности
- **Transaction:** современные приложения, высокий throughput, stateless-обработчики
- **ORM:** Hibernate по умолчанию использует prepared statements — отключите их или используйте PG14+

**Spring Boot + PgBouncer в режиме transaction:**
```yaml
spring:
  datasource:
    hikari:
      # Critical! Disable prepared statements
      data-source-properties:
        prepareThreshold: 0
    # Or use PgBouncer 1.22+ with PG 14+
```

**Architecture:**
```
[App instances × N] → [PgBouncer pool: 500 client conns / 30 DB conns] → [PostgreSQL]
```

## Q15. (!) Partitioning — когда применять?

**Partitioning** — разбиение большой таблицы на меньшие физические части (партиции) по критерию (range, list, hash).

**Когда:**

**1. Очень большие таблицы (> 100M строк / > 100GB):**
- Запросы сканируют свежие данные → партицируем по дате, запрос отсекает старые партиции
- Индекс помещается в память (меньше на партицию)

**2. Временные ряды:**
- Логи, события, метрики — range-партицирование по месяцу/неделе
- Дроп старой партиции = быстро (против DELETE миллионов строк)

**3. Нагрузка с большим числом удалений:**
- Дроп партиции = мгновенно; DELETE + VACUUM = медленно

**4. Изоляция арендаторов:**
- Hash/list-партицирование по tenant_id

**Когда НЕ применять:**
- Маленькие таблицы (< 10M строк)
- Запросы не выигрывают (нет отсечения)
- Добавляет сложность без соразмерной выгоды
- Внешние ключи между партициями сложны

**Выгоды:**
- Отсечение партиций (сканируем только релевантные)
- Параллельные операции по партициям
- Обслуживание по партициям (VACUUM, REINDEX)
- DROP партиции для retention

**Издержки:**
- Сложность (миграции, ограничения)
- Кросс-партиционные запросы медленнее
- Ключ партиционирования должен быть во всех уникальных индексах (или обходной путь)

**Партицирование в PostgreSQL (10+):**
```sql
CREATE TABLE logs (ts TIMESTAMP, msg TEXT)
PARTITION BY RANGE (ts);

CREATE TABLE logs_2024_01 PARTITION OF logs
  FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

**Автоматизация:** расширение `pg_partman` для авто-создания партиций.

## Q16. Range / List / Hash partitioning?

**Range:**
- Значения в пределах диапазона попадают в партицию
- Лучше для: меток времени, последовательных ID
```sql
PARTITION BY RANGE (created_at);
-- partitions: 2024_01, 2024_02, ...
```

**List:**
- Дискретные значения → партиция
- Лучше для: региона, категории, статуса
```sql
PARTITION BY LIST (country);
-- partitions: us, eu, apac, other
```

**Hash:**
- `hash(key) % N` → партиция
- Лучше для: равномерного распределения нагрузки (нет естественного ключа)
- Не умеет эффективно выполнять range-запросы
```sql
PARTITION BY HASH (user_id);
-- 16 partitions
```

**Composite:**
- Range → List суб-партиции: сначала по дате, затем по региону

**Сравнение:**

| Тип | Отсечение | Сценарий | Рост |
|------|---------|----------|--------|
| Range | Range-запросы | Временные ряды | Добавляем новую партицию на период |
| List | Равенство | Регион/арендатор | Добавляем на каждое новое значение |
| Hash | Равенство по ключу | Равномерное распределение | Фиксированное число, планируйте заранее |

**Отсечение:** планировщик исключает партиции, когда WHERE запроса совпадает с ключом партиционирования.

**Hash: нелегко перепартицировать** — выбирайте число партиций аккуратно (степень 2 для удобного удвоения в будущем).

## Q17. (!) ANALYZE и статистика оптимизатора?

**Статистика** — сводные данные о распределении таблицы/колонок, которые планировщик использует для оценок:
- Число строк
- Число уникальных значений на колонку
- Наиболее частые значения + их частоты
- Гистограмма распределения значений
- Доля NULL
- Средняя ширина

**Хранилище:** view `pg_stats`.

**ANALYZE:**
```sql
ANALYZE orders;        -- update stats
ANALYZE;              -- all tables
```

- Сэмплирует строки (по умолчанию 30000 * `default_statistics_target`)
- Обновляет `pg_statistic`

**Auto-analyze (autovacuum):**
- Срабатывает, когда изменилось > `autovacuum_analyze_scale_factor * rows` (по умолчанию 10%)

**Когда статистика устаревает:**
- Массовый INSERT/UPDATE
- Сдвиги распределения данных
- Новая неделя/месяц (временные ряды)

**Как обнаружить устаревшую статистику:**
- `EXPLAIN ANALYZE` показывает грубое расхождение `rows=X` против `actual rows=Y`

**default_statistics_target:**
- 100 по умолчанию (подходит в большинстве случаев)
- Поднимите до 1000 для колонки со сложным распределением
- `ALTER TABLE t ALTER COLUMN c SET STATISTICS 1000;`

**Расширенная статистика (PG 10+):**
```sql
CREATE STATISTICS s_name (dependencies, ndistinct)
  ON col_a, col_b FROM t;
ANALYZE t;
```
Помогает планировщику с **коррелированными колонками** (city + zip).

**Прод-кейс:** после крупного изменения данных (миграция, восстановление) → сразу выполните `ANALYZE`; planning time снижается, запросы быстрее.

## Q18. VACUUM, autovacuum, bloat?

**VACUUM:** освобождает dead tuples после UPDATE/DELETE (MVCC).

**Зачем:**
- UPDATE = пометить старую версию мёртвой + вставить новую
- Dead tuples занимают место впустую
- Предотвращает transaction ID wraparound (критично!)

**Типы:**

**VACUUM (стандартный):**
- Помечает мёртвое место для повторного использования
- Не отдаёт место ОС (размер таблицы остаётся)
- Не блокирует (кроме VACUUM FULL)

**VACUUM FULL:**
- Переписывает всю таблицу (перестраивает)
- Отдаёт место ОС
- **Блокирует запись** — только для экстренных случаев
- Вместо него используйте `pg_repack` (онлайн)

**Autovacuum:**
- Фоновый воркер, запускается автоматически
- Конфиг: `autovacuum_vacuum_scale_factor` (по умолчанию 20%)
- Агрессивные настройки для таблиц с большим числом записей:
```sql
ALTER TABLE t SET (autovacuum_vacuum_scale_factor = 0.05);
```

**Bloat:**
- Dead tuples вычищаются недостаточно быстро
- Или долгая транзакция мешает очистке (`xmin horizon`)
- Обнаружить: `pgstattuple`, `pg_stat_user_tables`

**Длинные транзакции = враг bloat:**
- Работающая транзакция мешает VACUUM удалять более новые удалённые строки
- Проверка: `SELECT * FROM pg_stat_activity WHERE state = 'idle in transaction';`

**TXID wraparound:**
- ID транзакций в PostgreSQL — 32-битные
- У каждой строки есть `xmin`/`xmax`
- Wraparound = повреждение данных!
- VACUUM «замораживает» старые строки и предотвращает это
- Игнорировать = **БД останавливается** на 2 млрд транзакций

**Мониторинг:**
- `pg_stat_user_tables.n_dead_tup`
- `age(relfrozenxid)` по таблице — предупреждение при > 1 млрд

**Тюнинг:** `autovacuum_max_workers=6`, `autovacuum_naptime=10s` для нагруженных БД.

## Q19. (!) shared_buffers, work_mem, effective_cache_size?

**shared_buffers:** блочный кэш PostgreSQL (разделяемая память).
- По умолчанию: 128MB (намного меньше нужного!)
- Рекомендация: **25% RAM** (до 8-16GB — дальше отдача падает)
- Большие значения выгодны OLAP; для OLTP упирается в пересечение с кэшем ОС

**work_mem:** память на операцию (sort, hash).
- По умолчанию: 4MB
- **На операцию, на соединение** — осторожно!
- 10 соединений × 3 операции × work_mem = 30× памяти
- Рекомендация: типично 16-64MB; больше для аналитики
- Можно задать на запрос: `SET LOCAL work_mem = '256MB';`

**maintenance_work_mem:** для CREATE INDEX, VACUUM.
- По умолчанию: 64MB
- Поднимите до 1GB для быстрого построения индексов
- На сессию, но maintenance-сессии редки

**effective_cache_size:** подсказка планировщику об общем доступном кэше (ОС + shared_buffers).
- По умолчанию: 4GB
- Рекомендация: **50-75% RAM**
- Ничего не выделяет, только влияет на планировщик (он предпочитает index scan при высоком effective_cache_size)

**wal_buffers:** буфер для записи WAL.
- По умолчанию: auto (минимум 1/32 shared_buffers, максимум 16MB)
- Обычно подходит как есть

**пример (сервер с 32GB RAM):**
```
shared_buffers = 8GB
effective_cache_size = 24GB
work_mem = 32MB
maintenance_work_mem = 1GB
```

**Инструмент:** `pgtune` — калькулятор конфига по типу нагрузки.

## Q20. WAL и checkpoint tuning?

**WAL (Write-Ahead Log):** все изменения сначала логируются → можно восстановиться.

**Checkpoint:** сброс грязных буферов в файлы данных; WAL старше checkpoint можно переиспользовать.

**Ключевой конфиг:**

**wal_level:** `replica` (по умолчанию) / `logical` (для логической репликации).

**max_wal_size:** целевой объём WAL между checkpoint. По умолчанию 1GB.
- Больше = меньше checkpoint = выше throughput записи, но дольше восстановление
- Нагруженная БД: 8-16GB

**checkpoint_timeout:** максимум времени между checkpoint. По умолчанию 5 мин.
- Больше = лучше throughput, дольше восстановление
- 15-30 мин для нагрузки с большим числом записей

**checkpoint_completion_target:** размазывает I/O checkpoint по этой доле интервала. По умолчанию 0.9.
- Выше = более плавный I/O; менять нужно редко

**min_wal_size:** держать переработанным как минимум столько. По умолчанию 80MB.

**Синхронный commit:**

**synchronous_commit = on (по умолчанию):** ждать fsync WAL перед ACK. Долговечно.
**= off:** ACK до fsync — при падении можно потерять несколько мс, но быстрее.
**= remote_apply / remote_write:** для реплик.

**Для пакетных загрузок:** поставьте `synchronous_commit = off` в сессии → быстрее, риск приемлем для bulk-импорта.

**Репликация:**
- `wal_keep_size` (PG 13+): хранить WAL для реплик
- `archive_mode + archive_command`: для PITR
- Слоты репликации: гарантируют, что WAL сохранится для подписчика

**Мониторинг:**
- `pg_stat_bgwriter`: checkpoints, buffers
- Высокий `checkpoints_req` против `checkpoints_timed` → max_wal_size слишком мал

## Q21. (!) LIMIT + OFFSET проблема pagination?

**Проблема:**
```sql
SELECT * FROM orders ORDER BY created_at DESC LIMIT 20 OFFSET 10000;
```
- Приходится сканировать 10020 строк, отбросить 10000, вернуть 20
- **Чем дальше страница, тем медленнее**
- Страница 500 (offset 10000) → в 100 раз медленнее страницы 1

**Решение: keyset-пагинация (метод seek):**

```sql
-- Page 1
SELECT * FROM orders
ORDER BY created_at DESC, id DESC
LIMIT 20;
-- Last row returns (created_at='2024-01-15 10:00', id=1234)

-- Page 2 (using last row as bookmark)
SELECT * FROM orders
WHERE (created_at, id) < ('2024-01-15 10:00', 1234)
ORDER BY created_at DESC, id DESC
LIMIT 20;
```

**Выгоды:**
- Константное время **на каждую страницу** (index seek)
- Нет несогласованности «строка сдвинулась» (OFFSET пропускает данные при добавлении строк)

**Нужен индекс:** `(created_at DESC, id DESC)`.

**Оговорка:**
- Нельзя прыгнуть на произвольную страницу (только next/prev)
- Для UX часто ок — бесконечная прокрутка, «load more»

**Альтернатива — посчитать строки один раз, затем OFFSET:**
- Полный подсчёт: `SELECT COUNT(*)` (медленно на больших таблицах)
- Используйте приблизительный подсчёт: `pg_class.reltuples`

**Лучшая практика:**
- Бесконечная прокрутка / «next» → keyset
- «Перейти на страницу 500» → смиритесь с медленностью или переосмыслите UX

## Q22. JOIN vs subquery vs EXISTS?

**Семантически эквивалентные примеры:**

**JOIN:**
```sql
SELECT o.* FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.country = 'US';
```

**Подзапрос IN:**
```sql
SELECT * FROM orders
WHERE user_id IN (SELECT id FROM users WHERE country = 'US');
```

**EXISTS:**
```sql
SELECT * FROM orders o
WHERE EXISTS (SELECT 1 FROM users u WHERE u.id = o.user_id AND u.country = 'US');
```

**Современный планировщик PostgreSQL:**
- Обычно переписывает все три варианта в эквивалентный план
- Незначительные различия в граничных случаях

**Различия исторически:**
- **JOIN** с `SELECT *` из users → может дублировать заказы, если совпадает несколько пользователей (редко)
- **EXISTS** — возвращает один раз на внешнюю строку независимо от числа внутренних совпадений; часто оптимален для semi-join
- **IN** — нежелателен из-за NULL-семантики (NULL в списке → UNKNOWN, коварно)

**Советы по производительности:**
- `NOT IN` с подзапросом, возвращающим NULL, ломается (всегда ложь) — используйте `NOT EXISTS` или `LEFT JOIN ... WHERE ... IS NULL`
- `EXISTS` часто быстрее, когда внутренняя таблица большая (срабатывает короткое замыкание)
- `JOIN` лучше, если нужны колонки из обеих таблиц

**EXPLAIN ANALYZE — проверьте план:**
- Часто планировщик превращает `IN` → `Semi Hash Join` ≈ `EXISTS`
- Используйте тот вариант, что читается яснее; замеряйте.

## Q23. Window functions performance?

**Window functions** — вычисляют значение для каждой строки по «окну» строк (а не агрегат, схлопывающий результат).

**Примеры:**
```sql
SELECT user_id, amount,
  ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_at DESC) AS rn,
  SUM(amount) OVER (PARTITION BY user_id) AS total
FROM orders;
```

**Производительность:**

- **Нужна сортировка**, если индекс не совпадает с `PARTITION BY ... ORDER BY`
- Индекс `(user_id, created_at)` → избегает сортировки в примере выше

**Стоимость:**
- Обычно требуется **сортировка** всего набора данных
- Уход work_mem на диск → медленно
- Почти всегда лучше эквивалентного коррелированного подзапроса

**Frame clause:**
```sql
SUM(amount) OVER (ORDER BY dt ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)
```
- По умолчанию: `RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW` — нарастающий итог
- Кастомные frame могут быть дорогими

**Советы:**
- Поддержка индекса для ORDER BY
- Избегайте оконной функции поверх огромного агрегата (материализуйте промежуточный результат)
- `DISTINCT ON` (специфично для PostgreSQL) — часто более быстрая альтернатива для «первая запись в группе»:
```sql
SELECT DISTINCT ON (user_id) *
FROM orders
ORDER BY user_id, created_at DESC;
```

**Materialized CTE (PG 12+):**
```sql
WITH aggregated AS MATERIALIZED (...)
SELECT ... FROM aggregated;
```

## Q24. Materialized views vs views?

**View:** сохранённый запрос; выполняется каждый раз.
```sql
CREATE VIEW user_summary AS
SELECT user_id, SUM(amount) AS total FROM orders GROUP BY user_id;
```
- Плюс: всегда актуально
- Минус: перезапускает запрос при каждом обращении → медленно для сложных агрегатов

**Materialized view:** **результат запроса сохранён** как таблица.
```sql
CREATE MATERIALIZED VIEW user_summary_mv AS
SELECT user_id, SUM(amount) AS total FROM orders GROUP BY user_id;

CREATE UNIQUE INDEX ON user_summary_mv (user_id);
```
- Плюс: быстрое чтение (просто SELECT из таблицы)
- Минус: **устаревает** до REFRESH

**REFRESH:**
```sql
REFRESH MATERIALIZED VIEW user_summary_mv;  -- locks reads
REFRESH MATERIALIZED VIEW CONCURRENTLY user_summary_mv;  -- needs unique index
```

**Инкрементальный refresh:** нет в ядре PostgreSQL; расширения (`pg_ivm`) или решения на уровне логики (CDC → обновление).

**Сценарии:**
- Дашборды (ночной refresh)
- Отчётность (сложные join)
- Денормализация для пути чтения
- Поисковые индексы

**Альтернативы:**
- **Кэш** (Redis): на уровне приложения; похожий компромисс
- **Read replica**: запросы к реплике со слегка устаревшими данными

**Следите:** время refresh может стать узким местом — если view строится 10 мин, как часто его обновлять? Может понадобиться инкрементальное перестроение по партициям.

## Q25. (!) Read replicas — когда и как?

**Read replica:** копия БД, следующая за WAL primary и обслуживающая запросы на чтение.

**Зачем:**
- Масштабировать throughput чтения (в большинстве приложений 80%+ — чтения)
- Изолировать тяжёлую аналитику от OLTP
- HA-failover (в некоторых конфигурациях)

**PostgreSQL:**
- **Streaming replication** (физическая) — побайтовая копия; только чтение
- **Logical replication** — изменения на уровне строк; выборочные таблицы; доступна для записи (но осторожно)

**Лаг:**
- Типично: миллисекунды-секунды
- Массовые операции на primary дают всплеск лага
- Мониторинг: `pg_replication_slots`, дельта `replay_lsn`

**Проблема read-your-writes:**
- Пользователь обновляет запись и сразу читает
- Запрос может попасть на реплику → устаревшие данные
- Решения:
  - Маршрутизировать записи + немедленные чтения → на primary
  - Sticky-сессия к primary на X секунд после записи
  - Читать с primary в течение «сессии после записи»

**Архитектурные паттерны:**
- Приложение маршрутизирует запросы: `@Transactional(readOnly=true)` → реплика; иначе → primary
- Spring `AbstractRoutingDataSource` для динамической маршрутизации
- **На основе прокси:** ProxySQL, pgpool, RDS Proxy

**Компромиссы:**
- Eventual consistency (лаг)
- Сложность failover
- Число соединений удваивается (приложение подключается к обоим)

**Режимы согласованности:**
- Асинхронная репликация (по умолчанию, быстро, может потерять данные при падении)
- Синхронная репликация (`synchronous_standby_names`) — медленнее запись, нулевая потеря данных

**Облако:** RDS/Aurora делают реплики тривиальными; Aurora — общее хранилище, минимальный лаг.

## Q26. (!) Как находить slow queries в prod?

**PostgreSQL:**

**pg_stat_statements (обязательное расширение):**
```sql
CREATE EXTENSION pg_stat_statements;

SELECT query,
       calls,
       total_exec_time / 1000 AS total_sec,
       mean_exec_time AS mean_ms,
       rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;
```
- Учитывает по нормализованному запросу (параметры заменяются на `$1`)
- Суммарное время, среднее, число вызовов
- **Всегда начинайте отсюда** — топ-10 обычно объясняет 80% нагрузки

**Slow query log:**
```sql
log_min_duration_statement = 1000  -- log queries > 1s
```
- Пишет в лог-файл PostgreSQL
- Grep / отправка в ELK / Loki

**Запросы, активные прямо сейчас:**
```sql
SELECT pid, now() - query_start AS duration, state, query
FROM pg_stat_activity
WHERE state != 'idle' AND now() - query_start > interval '5 seconds'
ORDER BY duration DESC;
```
Используйте для «что тормозит прямо сейчас».

**APM (DataDog, New Relic):**
- Автоматически захватывает SQL из трейсов
- Размечает по сервису/endpoint
- Latency P95/P99 на запрос

**auto_explain:**
```sql
LOAD 'auto_explain';
SET auto_explain.log_min_duration = 1000;
SET auto_explain.log_analyze = true;  -- adds runtime overhead!
```
Логирует полный EXPLAIN для медленных запросов → подробная диагностика.

**pganalyze / pgwatch2:** дашборды поверх pg_stat_statements.

**Рабочий процесс:**
1. pg_stat_statements → топ-N
2. Выбрать самый влиятельный запрос (total_exec_time)
3. Взять пример аргументов из лога
4. EXPLAIN ANALYZE в тесте
5. Исправить (индекс, переписать, конфиг)
6. Выкатить, замерить

## Q27. Database load test (pgbench, sysbench)?

**pgbench** (идёт в комплекте с PostgreSQL):
- Встроенный бенчмарк в стиле TPC-B
- Свои сценарии
```bash
pgbench -i -s 100 testdb   # init scale 100 (~1.5GB)
pgbench -c 50 -j 4 -T 60 testdb  # 50 clients, 4 threads, 60s
```

Вывод: TPS (транзакций/сек), latency.

**Свой сценарий:**
```sql
-- my_bench.sql
\set uid random(1, 10000)
SELECT * FROM users WHERE id = :uid;
```
```bash
pgbench -c 50 -T 60 -f my_bench.sql testdb
```

**sysbench:**
- Несколько СУБД (MySQL, PostgreSQL)
- Стандартные OLTP-нагрузки
```bash
sysbench --db-driver=pgsql oltp_read_write prepare
sysbench --db-driver=pgsql oltp_read_write run --threads=64 --time=60
```

**Целевые сценарии:**
- С упором на запись (нагрузка INSERT)
- С упором на чтение (нагрузка SELECT)
- Смешанный (70/30)
- Чувствительный к latency (P99)

**Лучшие практики:**
- Объём данных как в прод (scale factor ~ размер прод)
- Конфиг как в прод (shared_buffers, work_mem)
- Сеть как в прод (может стать узким местом теста)
- Прогрев: несколько минут разогрева (заполнить кэши)
- Замеряйте: TPS, среднее, latency P50, P95, P99
- Итерируйте: меняйте один параметр, перезапускайте

**Прочие инструменты:**
- **HammerDB** — OLTP/TPC-C, TPC-H
- **jmeter** / **k6** — на уровне приложения (end-to-end, не чистая БД)
- **pg_bench_tools** (community-скрипты)

**Планирование ёмкости:** экстраполируйте «при нагрузке 5k TPS p99 = 50 мс» — сравните с SLO.

## See also

- [JVM Performance Tuning](jvm-performance-tuning-interview.md) — application side
- [Application Profiling](application-profiling-interview.md) — JFR, async-profiler
- [Caching Strategies](../architecture/caching-strategies-interview.md) — reduce DB load
- [PostgreSQL](../databases/postgresql-interview.md) — deeper DB-specific questions
- [Hibernate](../databases/hibernate-interview.md) — ORM performance patterns (N+1)
- [Database Architecture](../databases/database-architecture-interview.md) — replication, sharding
- [Caching Performance](caching-performance-interview.md) — cache tuning
- [Performance Testing](performance-testing-interview.md) — methodology, tools
- [Memory Management](memory-management-interview.md) — JVM ↔ DB interplay
- [Consistency Patterns](../architecture/consistency-patterns-interview.md) — read replicas trade-offs

- [Caching Performance](caching-performance-interview.md)
- [JVM Performance Tuning](jvm-performance-tuning-interview.md)
- [Memory Management](memory-management-interview.md)
- [Network Performance](network-performance-interview.md)
- [Performance Testing](performance-testing-interview.md)
