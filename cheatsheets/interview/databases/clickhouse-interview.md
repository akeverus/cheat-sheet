---
title: "Вопросы на собеседовании: ClickHouse"
description: "ClickHouse: columnar OLAP DB от Yandex, MergeTree engines, partitioning, sparse index, materialized views, distributed tables, ReplicatedMergeTree, vs Snowflake/BigQuery"
tags:
  - interview
  - databases
  - clickhouse-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "ClickHouse"
  - "ClickHouse interview"
  - "ClickHouse собеседование"
prerequisites:
  - "[[clickhouse]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `ClickHouse`

`ClickHouse` — open-source колоночная OLAP-СУБД от **Yandex** (открыта в 2016). Известна **очень высокой скоростью** на аналитических запросах (миллиарды строк за секунды). Применяется в observability (логи, метрики, трейсы), real-time-аналитике, ad-tech. Компания **ClickHouse Inc.** (с 2021) предоставляет managed-облако.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [ClickHouse Documentation](https://clickhouse.com/docs)
- [ClickHouse GitHub](https://github.com/ClickHouse/ClickHouse)
- [ClickHouse Cloud](https://clickhouse.cloud/)
- [Awesome ClickHouse](https://github.com/ClickHouse/awesome-clickhouse)
- [ClickHouse Best Practices](https://clickhouse.com/docs/en/operations/tips/)
- [Altinity Knowledge Base](https://altinity.com/blog/) — эксперты по open-source ClickHouse

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое ClickHouse?](#q1--что-такое-clickhouse)
- [Q2. (!) Почему ClickHouse такой быстрый?](#q2--почему-clickhouse-такой-быстрый)
- [Q3. (!) Чем ClickHouse отличается от Snowflake и BigQuery?](#q3--чем-clickhouse-отличается-от-snowflake-и-bigquery)
- [Q4. ClickHouse vs PostgreSQL для аналитики?](#q4-clickhouse-vs-postgresql-для-аналитики)

**MergeTree engines**
- [Q5. (!) Что такое MergeTree?](#q5--что-такое-mergetree)
- [Q6. (!) Семейство MergeTree (ReplacingMergeTree, SummingMergeTree, AggregatingMergeTree)?](#q6--семейство-mergetree-replacingmergetree-summingmergetree-aggregatingmergetree)
- [Q7. Как работает CollapsingMergeTree?](#q7-как-работает-collapsingmergetree)
- [Q8. Что такое ReplicatedMergeTree?](#q8-что-такое-replicatedmergetree)

**Partitioning и indexing**
- [Q9. (!) Partitioning в ClickHouse?](#q9--partitioning-в-clickhouse)
- [Q10. (!) Sparse index — что это?](#q10--sparse-index--что-это)
- [Q11. Чем отличаются ORDER BY и PRIMARY KEY?](#q11-чем-отличаются-order-by-и-primary-key)
- [Q12. Что такое skip-индексы (data skipping)?](#q12-что-такое-skip-индексы-data-skipping)

**Запросы**
- [Q13. (!) ClickHouse SQL — особенности?](#q13--clickhouse-sql--особенности)
- [Q14. Какие есть функции для массивов и higher-order-функции?](#q14-какие-есть-функции-для-массивов-и-higher-order-функции)
- [Q15. (!) JOINs в ClickHouse — особенности?](#q15--joins-в-clickhouse--особенности)
- [Q16. Поддерживаются ли window-функции?](#q16-поддерживаются-ли-window-функции)

**Materialized views и projections**
- [Q17. (!) Materialized views в ClickHouse?](#q17--materialized-views-в-clickhouse)
- [Q18. Что такое projections?](#q18-что-такое-projections)

**Distributed**
- [Q19. (!) Что такое Distributed-таблица?](#q19--что-такое-distributed-таблица)
- [Q20. Replication через ZooKeeper / Keeper?](#q20-replication-через-zookeeper--keeper)
- [Q21. Чем ClickHouse Keeper отличается от ZooKeeper?](#q21-чем-clickhouse-keeper-отличается-от-zookeeper)

**Storage**
- [Q22. (!) Storage engine оптимизации (compression, sparse data)?](#q22--storage-engine-оптимизации-compression-sparse-data)
- [Q23. Что такое многоуровневое хранение (tiered storage, hot/cold)?](#q23-что-такое-многоуровневое-хранение-tiered-storage-hotcold)

**Integration**
- [Q24. (!) Kafka engine для ingestion?](#q24--kafka-engine-для-ingestion)
- [Q25. Какие ещё табличные движки есть (S3, MySQL, PostgreSQL)?](#q25-какие-ещё-табличные-движки-есть-s3-mysql-postgresql)

**Production**
- [Q26. (!) Какие use cases ClickHouse в production?](#q26--какие-use-cases-clickhouse-в-production)
- [Q27. (!) Какие ограничения / минусы ClickHouse?](#q27--какие-ограничения--минусы-clickhouse)
- [Q28. Common pitfalls в ClickHouse production?](#q28-common-pitfalls-в-clickhouse-production)

## Q1. (!) Что такое ClickHouse?

**ClickHouse** — open-source колоночная OLAP-СУБД для аналитики на больших объёмах данных. Создана в **Yandex** для **Yandex.Metrica** (аналог Google Analytics), открыта в **2016**. Главное, что её отличает — она сканирует миллиарды строк за секунды, поэтому годится для интерактивной аналитики там, где обычная реляционная БД задумалась бы на минуты.

**Из чего складывается её сила:**
- **Колоночное хранение + векторизованное выполнение** — основа всей скорости (детали в Q2)
- **Высокое сжатие** — типично 5–10x, потому что в одной колонке лежат однотипные значения, которые жмутся лучше
- **SQL** — в основном стандартный, с расширениями под аналитику
- **Распределённость** — шардирование и репликация из коробки для масштабирования и отказоустойчивости
- **Real-time-приём** — миллионы вставок/сек, данные доступны для запросов почти сразу

**Где применяют** (везде, где много данных пишется и читается аналитически, но почти не обновляется построчно):
- Агрегация логов — дешевле и быстрее ELK
- Временные ряды — вместо Prometheus, InfluxDB
- Веб-аналитика и ad-tech (анализ ставок, атрибуция)
- Бэкенд observability — Tempo, SigNoz

**Ключевая оговорка:** ClickHouse заточена под OLAP (массовые сканы и агрегации), а не под OLTP (точечные выборки и частые UPDATE по одной строке) — для этого она плохо подходит (см. Q27).

## Q2. (!) Почему ClickHouse такой быстрый?

Скорость — это не один трюк, а сумма оптимизаций на каждом уровне: меньше читать с диска, обрабатывать данные пачками и распараллеливать всё, что можно. Удобно сгруппировать их по идее.

**Читать меньше данных:**
- **Колоночное хранение** — с диска поднимаются только колонки, которые реально нужны запросу, а не вся строка
- **Сжатие** — однотипные данные в колонке жмутся в разы, так что в тот же объём I/O и кэша влезает больше строк
- **Разреженный первичный индекс** — быстро находит нужный диапазон для range-сканов (см. Q10)
- **Data skipping** — min/max-индексы и bloom-фильтры позволяют целиком пропускать блоки, которые точно не подходят (см. Q12)

**Обрабатывать быстрее то, что прочитали:**
- **Векторизованное выполнение** — данные идут батчами, а не построчно, с использованием SIMD-инструкций процессора
- **JIT-компиляция** запросов (в более новых версиях) — горячие выражения компилируются в машинный код
- **Оптимизированный I/O** — прямое и асинхронное чтение с диска

**Делать всё параллельно:**
- **Распределённый запрос** — параллельный скан по шардам кластера
- **Асинхронные параллельные вставки** — приём данных не упирается в один поток
- **Нативный протокол** — бинарный и сжатый, без накладных расходов текстового протокола

**Итог:** на аналитических запросах ClickHouse часто в **10–100x** быстрее PostgreSQL.

## Q3. (!) Чем ClickHouse отличается от Snowflake и BigQuery?

Все три — аналитические БД, но разные по модели эксплуатации. Главный водораздел: ClickHouse вы запускаете и обслуживаете сами (зато дёшево и быстро), а Snowflake и BigQuery — managed-сервисы, где за удобство платите деньгами и привязкой к вендору.

| Критерий | ClickHouse | Snowflake | BigQuery |
|-----------|-----------|-----------|----------|
| Тип | Self-hosted / облако | Только managed | Serverless |
| Архитектура | Shared-nothing (шардирование) | Общее хранилище + виртуальные warehouse | Serverless |
| Стоимость | Самый дешёвый | $$$ | $$ (за запрос) |
| Скорость | Очень высокая | Высокая | Высокая |
| Параллельность | Сотни | Тысячи | Тысячи |
| Настройка | Ручная конфигурация | Click-ops | Zero-config |
| SQL | Диалект ClickHouse | ANSI SQL | ANSI SQL |
| Экосистема | Меньше | Зрелая | Привязана к GCP |

**Выбирайте ClickHouse, когда:**
- критична стоимость
- уже есть собственный хостинг и команда для эксплуатации
- нужен real-time-приём данных (миллионы строк/сек)
- нужна sub-second-задержка запросов на огромных данных

**Выбирайте Snowflake/BigQuery, когда:**
- нужен полностью managed-сервис без своих DevOps
- это multi-tenant-хранилище данных со сложными схемами
- не хочется заниматься эксплуатацией кластера

## Q4. ClickHouse vs PostgreSQL для аналитики?

Это не «что лучше», а «для чего». PostgreSQL — построчная OLTP-СУБД: идеальна для приложений, где много точечных операций и нужна полная транзакционность. ClickHouse — колоночная OLAP-СУБД: идеальна для аналитики, где надо просканировать и сагрегировать миллиарды строк. Каждая проигрывает там, где сильна другая.

| Критерий | ClickHouse | PostgreSQL |
|----------|-----------|------------|
| Хранение | Колоночное | Построчное |
| Скорость аналитики | **Очень высокая** | Медленно на больших сканах |
| OLTP (точечные выборки) | Медленно | Быстро |
| Обновления / удаления | Ограниченно | Полный ACID |
| JOIN-ы | Ограниченно (broadcast только небольших таблиц) | Полноценные |
| Гибкость схемы | Ограниченная | Полная |

**Эмпирическое правило:** часто их ставят рядом — PostgreSQL обслуживает приложение, а данные стекают в ClickHouse для аналитики и дашбордов.

## Q5. (!) Что такое MergeTree?

**MergeTree** — основной storage-движок ClickHouse, на котором строятся почти все рабочие таблицы. Имя отражает суть: данные пишутся отдельными кусками, а фоновый процесс их объединяет (merge), как в LSM-дереве.

**Как это работает:**
- Каждая вставка создаёт новую **часть (part)** — неизменяемый чанк, отсортированный по ключу `ORDER BY`
- Фоновый **merge** периодически сливает мелкие части в более крупные. Зачем: чем меньше частей, тем меньше файлов открывать при чтении и тем эффективнее сжатие
- Внутри части данные физически отсортированы по `ORDER BY` — это и даёт быстрые range-сканы
- Разреженный первичный индекс держится в памяти (см. Q10)

Из-за того что вставка = новая часть, ClickHouse не любит частые мелкие INSERT-ы (см. Q28) — лучше вставлять большими батчами.

```sql
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64,
    event_type String,
    data String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (event_time, user_id);
```

## Q6. (!) Семейство MergeTree (ReplacingMergeTree, SummingMergeTree, AggregatingMergeTree)?

Все эти движки — варианты MergeTree, которые добавляют логику в момент merge. Идея общая: ClickHouse не умеет дешёво обновлять отдельные строки, поэтому вместо UPDATE вы просто вставляете новые строки, а движок при слиянии частей сам сворачивает их по ключу `ORDER BY` — дедуплицирует, суммирует или агрегирует. Цена — результат становится корректным только после merge (eventually).

**ReplacingMergeTree** — дедуплицирует строки с одинаковым ключом `ORDER BY`: при merge побеждает последняя версия (по указанной колонке-версии, здесь `updated_at`). Это способ имитировать «обновление» записи: пишете новую строку с тем же id, старая со временем уберётся.

```sql
CREATE TABLE users (
    id UInt64,
    email String,
    updated_at DateTime
) ENGINE = ReplacingMergeTree(updated_at)
ORDER BY id;
```

**SummingMergeTree** — при merge суммирует числовые колонки для строк с одинаковым `ORDER BY`. Удобно для предагрегированных счётчиков: вместо хранения каждого события держите по одной строке на ключ с накопленными суммами.

```sql
CREATE TABLE daily_stats (
    date Date,
    user_id UInt64,
    visits UInt64,
    clicks UInt64
) ENGINE = SummingMergeTree()
ORDER BY (date, user_id);
-- При merge: visits и clicks суммируются
```

**AggregatingMergeTree** — обобщение SummingMergeTree на произвольные агрегации через тип `AggregateFunction` (не только sum, но и uniq, avg, quantile и т. д.). Хранит частичные состояния агрегатов и доагрегирует их при merge.

**Связка с MV:** материализованные представления (Q17) часто строят поверх AggregatingMergeTree — так получаются предагрегаты в реальном времени, обновляемые на каждом INSERT.

## Q7. Как работает CollapsingMergeTree?

**CollapsingMergeTree** решает ту же задачу, что ReplacingMergeTree (обновление неизменяемых данных), но другим приёмом: каждой строке присваивается **знак** `sign` (+1 — «состояние есть», -1 — «отмена состояния»). Чтобы изменить запись, вы вставляете строку-отмену (-1) со старыми значениями и строку с новыми значениями (+1).

```sql
CREATE TABLE events (
    user_id UInt64,
    sign Int8,
    activity_count UInt64
) ENGINE = CollapsingMergeTree(sign)
ORDER BY user_id;

-- Insert
INSERT INTO events VALUES (1, 1, 100);  -- "old state"

-- Update: cancel old + add new
INSERT INTO events VALUES (1, -1, 100);  -- cancel
INSERT INTO events VALUES (1, 1, 150);   -- new value
```

При merge пары строк с противоположными знаками и одинаковым `ORDER BY` взаимно гасятся → остаётся только актуальное состояние.

**Сценарий:** обновления в модели, которая в остальном неизменяема (например, изменяющийся счётчик активности пользователя).

**Подводный камень:** до merge в таблице лежат обе строки (и +1, и -1), поэтому наивный `SELECT sum(...)` даст неверный результат — учитывайте знак вручную (`sum(value * sign)`) или используйте `FINAL`.

## Q8. Что такое ReplicatedMergeTree?

**ReplicatedMergeTree** — это MergeTree с репликацией: одни и те же данные синхронно копируются на несколько узлов ради отказоустойчивости. Реплики координируются через ZooKeeper/Keeper, где хранится общая очередь операций (см. Q20).

```sql
CREATE TABLE events (...) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/{shard}/events',  -- ZK/Keeper path
    '{replica}'                            -- replica identifier
)
ORDER BY (event_time);
```

**Свойства репликации:**
- **Multi-master** — писать можно в любую реплику, нет выделенного мастера
- **Eventually consistent** — данные доезжают до других реплик асинхронно
- **Координация через ZooKeeper / ClickHouse Keeper** — общий журнал, по которому реплики догоняют друг друга
- **Автовосстановление** — отставшая или вернувшаяся после сбоя реплика сама докачивает недостающие части

В пути `'/clickhouse/tables/{shard}/events'` `{shard}` и `{replica}` — макросы из конфига: реплики одного шарда указывают на один ZK-путь и так находят друг друга.

**Рекомендация:** в production — всегда **ReplicatedMergeTree** ради высокой доступности (HA); без репликации отказ диска означает потерю данных.

## Q9. (!) Partitioning в ClickHouse?

**Партиционирование** разбивает таблицу на физически отдельные группы частей по значению выражения `PARTITION BY` (обычно по времени). Это не индекс, а способ организации данных на диске.

```sql
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)  -- partition by month
ORDER BY event_time;
```

**Что это даёт:**
- **Partition pruning** — запрос `WHERE event_time = '2025-04-19'` читает только нужную партицию, а не всю таблицу
- **Дешёвые операции над целым диапазоном** — `DROP`/`DETACH`/`OPTIMIZE` отдельной партиции, например мгновенное удаление старого месяца целиком (вместо медленного DELETE по строкам)
- **Параллельность** — разные партиции обрабатываются независимо

**Рекомендации по выбору ключа:**
- Не дробите слишком мелко: ориентир — **≤ ~1000 партиций** на таблицу. Иначе появятся тысячи мелких частей, и merge перестанет успевать (см. Q28)
- Типично — по месяцу или неделе для временных рядов
- **Не партиционируйте по колонке с высокой кардинальностью** (например по user_id) — это и порождает взрыв числа частей

**Не путать с ORDER BY:** партиционирование грубо отсекает диапазоны, а сортировка внутри партиции (`ORDER BY`) обеспечивает точечный поиск через первичный индекс.

## Q10. (!) Sparse index — что это?

В отличие от PostgreSQL/MySQL, которые держат B-tree с записью на каждую строку, ClickHouse использует **разреженный индекс (sparse index)**: одна запись на каждые **8192 строки** (значение `index_granularity` по умолчанию). Это сознательный компромисс — крошечный индекс ценой того, что точная строка ищется не сразу.

```
Primary index (sparse):
[key=10, granule=0]
[key=100, granule=1]
[key=200, granule=2]
...
```

**Гранула (granule)** — блок из 8192 строк, минимальная единица чтения. Индекс хранит ключ первой строки каждой гранулы.

**Как ищется значение:** `WHERE key = 150` → бинарный поиск по индексу находит гранулу, в которую попадает 150 → ClickHouse читает все её 8192 строки → фильтрует. То есть даже за одной строкой поднимается целая гранула.

**Компромисс:**
- **Плюс:** индекс крошечный — целиком помещается в память даже для огромных таблиц
- **Плюс:** range-сканы эффективны — нашли границу и читаем подряд
- **Минус:** точечная выборка одной строки дороже, чем в OLTP-БД (всё равно читаем гранулу целиком)

Это прямое следствие назначения ClickHouse: он оптимизирован под **массовые сканы**, а не под точечные выборки.

## Q11. Чем отличаются ORDER BY и PRIMARY KEY?

В ClickHouse это два разных понятия (в отличие от привычного «первичный ключ = уникальность»). `ORDER BY` задаёт, как данные физически сортируются на диске; `PRIMARY KEY` — какой префикс этой сортировки попадёт в разреженный индекс. Уникальность здесь ни тот, ни другой не гарантируют.

```sql
ORDER BY (user_id, event_time)  -- physical sorting в parts
PRIMARY KEY user_id              -- prefix of ORDER BY (для index)
```

- **ORDER BY** — физическая раскладка данных в частях. От неё зависит сжатие и скорость сканов.
- **PRIMARY KEY** — префикс `ORDER BY`, по которому строится первичный индекс. Если не указан явно — **равен ORDER BY**.

Разделять их имеет смысл, когда хочется сортировать по многим колонкам (для сжатия), но не раздувать индекс — тогда в PRIMARY KEY берут только первые колонки. В большинстве случаев `PRIMARY KEY = ORDER BY` (по умолчанию).

**Настройка ORDER BY — главный рычаг производительности:**
- Ставьте колонки от низкой кардинальности к высокой — так длиннее серии одинаковых значений и лучше сжатие
- Самые частые колонки-фильтры — в начало, чтобы индекс отсекал больше гранул

## Q12. Что такое skip-индексы (data skipping)?

**Skip-индексы** (data skipping) — вторичные индексы, которые позволяют **целиком пропустить блок гранул, не читая их**. Работают «от противного»: индекс не указывает, где данные есть, а помогает доказать, что в блоке нужных данных точно нет, — и ClickHouse его не открывает. Полезны для фильтров по колонкам, которые не входят в `ORDER BY`.

**Типы:**
- `minmax` — хранит min/max по блоку; пропускает блок, если искомое значение вне диапазона
- `set` — набор уникальных значений блока (для колонок с малым числом разных значений)
- `bloom_filter` — bloom-фильтр для быстрой проверки «значения точно нет»
- `tokenbf_v1` — bloom-фильтр по токенам, поиск в духе full-text
- `ngrambf_v1` — bloom-фильтр по n-граммам, для поиска подстрок

```sql
CREATE TABLE logs (
    timestamp DateTime,
    message String,
    INDEX message_idx message TYPE tokenbf_v1(8192, 3, 0) GRANULARITY 4
) ENGINE = MergeTree()
ORDER BY timestamp;
```

**Эффект:** запрос `WHERE message LIKE '%error%'` пропускает блоки, в токенах которых нет нужного слова, и читает только потенциально подходящие.

**Подводный камень:** skip-индекс помогает, только если данные хорошо «кластеризованы» под него (искомые значения собраны в немногих блоках). Если значения размазаны по всем блокам, индекс ничего не пропустит, а лишь добавит накладные расходы.

## Q13. (!) ClickHouse SQL — особенности?

SQL в ClickHouse **в основном стандартный**, так что базовые запросы переносятся почти без правок. Отличия — это расширения под аналитику: функции для работы с массивами, обработка времени и, главное, **приближённые агрегаты**, которые меняют точность на скорость.

```sql
-- Standard SQL
SELECT user_id, count() FROM events WHERE date = '2025-04-19' GROUP BY user_id;
```

```sql
-- Higher-order functions
SELECT arraySum(prices), arrayAvg(prices) FROM orders;

-- Approximate functions (faster, slightly inaccurate)
SELECT uniqHLL12(user_id) FROM events;  -- ~uniqExact

-- Specific functions
SELECT toStartOfHour(event_time), count() FROM events GROUP BY 1;
```

**Приближённые агрегаты** — `uniqHLL12` (число уникальных через HyperLogLog), `quantileTDigest` (перцентили) — считаются за один проход в фиксированной памяти и потому **намного быстрее** точных аналогов вроде `uniqExact`, ценой небольшой погрешности. На дашбордах, где допустима ошибка в доли процента, это стандартный выбор.

## Q14. Какие есть функции для массивов и higher-order-функции?

Массивы в ClickHouse — полноценный тип колонки (`Array(T)`), а не сериализованный blob. Над ними есть набор функций, включая **higher-order** (принимающие lambda): `arrayFilter`, `arrayMap`, `arraySum` и др. Это позволяет хранить связанные значения прямо в строке и обрабатывать их без JOIN-ов.

```sql
-- Arrays — first-class type
CREATE TABLE products (
    id UInt64,
    tags Array(String),
    prices Array(Float64)
) ENGINE = MergeTree() ORDER BY id;

-- Filter array
SELECT id, arrayFilter(x -> x > 10, prices) FROM products;

-- Map
SELECT id, arrayMap(x -> x * 1.2, prices) FROM products;

-- Sum
SELECT id, arraySum(prices) FROM products;

-- Has element
SELECT id FROM products WHERE has(tags, 'discount');

-- ARRAY JOIN — explode array
SELECT id, tag FROM products ARRAY JOIN tags AS tag;
```

`ARRAY JOIN` «разворачивает» массив в строки — это аналог `UNNEST`/`explode`: один элемент массива на строку результата.

**Зачем это нужно:** массивы — главный инструмент денормализации в ClickHouse. Вместо отдельной таблицы-связки (которую пришлось бы JOIN-ить, а JOIN-ы здесь слабые — см. Q15) данные кладут массивом прямо в строку.

## Q15. (!) JOINs в ClickHouse — особенности?

Главное, что нужно знать: **ClickHouse — слабый JOIN-ер по сравнению с PostgreSQL**, и проектировать схему надо так, чтобы JOIN-ов было поменьше. Причина — в реализации.

**Как это устроено:**
- **По умолчанию правая таблица целиком грузится в память** (hash join) и рассылается (broadcast) на все узлы. Значит, правая таблица должна быть небольшой
- **Ограничение по памяти** — большой JOIN легко упирается в OOM
- **В старых версиях не было сброса хэша на диск** — JOIN либо влезал в RAM, либо падал
- JOIN всё равно медленнее обычного скана по колонкам

**Что делать вместо JOIN:**
- **Денормализуйте** данные заранее (массивы, широкие таблицы), чтобы JOIN был не нужен
- Если JOIN неизбежен — держите **маленькую таблицу справа** (она и рассылается)
- Для фильтрации используйте **`IN` вместо JOIN** — это дешевле:
```sql
-- Slower
SELECT * FROM events JOIN users ON events.user_id = users.id WHERE users.country = 'US';

-- Faster
SELECT * FROM events WHERE user_id IN (SELECT id FROM users WHERE country = 'US');
```

- **Стратегии распределённых JOIN:** GLOBAL, ALLOW_EXPERIMENTAL_PARALLEL_REPLICAS

К **2025** JOIN-ы улучшились (parallel hash join, grace hash), но в ClickHouse всё равно выигрывает **денормализация**.

## Q16. Поддерживаются ли window-функции?

Да. Оконные функции с синтаксисом `OVER (PARTITION BY ... ORDER BY ...)` поддерживаются — running totals, ранжирование, скользящие окна и т. п.

```sql
SELECT
    user_id,
    event_time,
    sum(amount) OVER (PARTITION BY user_id ORDER BY event_time) AS running_total,
    rank() OVER (PARTITION BY user_id ORDER BY amount DESC) AS rnk
FROM transactions;
```

Появились они относительно поздно — с **2021** — и по сравнению с PostgreSQL есть отдельные ограничения, но базовые сценарии (как в примере выше) работают.

## Q17. (!) Materialized views в ClickHouse?

В PostgreSQL materialized view — это снимок, который надо периодически `REFRESH`. В ClickHouse MV — это **триггер на INSERT**: каждая новая пачка строк в исходной таблице тут же прогоняется через SELECT представления, и результат дописывается в целевую таблицу.

- **Обновляются инкрементально** — обрабатывается только что вставленный блок, а не вся таблица
- **Предагрегации в реальном времени** — поверх SummingMergeTree/AggregatingMergeTree (Q6)
- **Без refresh** — данные всегда актуальны, ничего не надо пересчитывать вручную
- **Срабатывают только на INSERT** — обновление/удаление в источнике MV не увидит, и исторические данные (вставленные до создания MV) в него не попадут

```sql
-- Source table
CREATE TABLE events (
    event_time DateTime,
    user_id UInt64,
    amount Float64
) ENGINE = MergeTree() ORDER BY event_time;

-- MV для hourly aggregations
CREATE MATERIALIZED VIEW hourly_stats
ENGINE = SummingMergeTree()
ORDER BY (hour, user_id)
AS SELECT
    toStartOfHour(event_time) AS hour,
    user_id,
    count() AS event_count,
    sum(amount) AS total_amount
FROM events
GROUP BY hour, user_id;
```

При **каждом INSERT** в `events` → автоматически обновляется `hourly_stats`.

**Сценарий:** real-time-дашборды без тяжёлых запросов к сырым данным.

## Q18. Что такое projections?

**Projections** (с 2020) — это спрятанная внутри таблицы копия данных в другом порядке сортировки или с другой агрегацией. Ключевое отличие от MV: проекция живёт **внутри той же таблицы** и оптимизатор **сам** решает, использовать её или основные данные — запрос трогать не нужно.

```sql
ALTER TABLE events ADD PROJECTION events_by_user (
    SELECT *
    ORDER BY user_id
);
```

ClickHouse автоматически подставит проекцию, если запрос от её сортировки выигрывает. Это решает проблему «одного ORDER BY»: основная таблица отсортирована, скажем, по времени, а проекция — по user_id, и оба типа фильтров быстры.

**В сравнении с MV:**
- **Projection** — дополнительный порядок сортировки/агрегат для той же таблицы, выбирается прозрачно
- **MV** — отдельная таблица с предвычисленными агрегатами, на которую запрос нужно адресовать явно

## Q19. (!) Что такое Distributed-таблица?

**Distributed-таблица** не хранит данные сама — это таблица-прокси над **шардами** кластера. Реальные данные лежат в локальных таблицах на каждом узле (обычно ReplicatedMergeTree), а Distributed-таблица знает топологию кластера и раскидывает по ней запись и чтение.

```sql
-- Local table on each node
CREATE TABLE events_local (...) ENGINE = ReplicatedMergeTree(...);

-- Distributed table (no data of its own, queries across shards)
CREATE TABLE events_distributed AS events_local
ENGINE = Distributed(my_cluster, default, events_local, rand());
```

- **Вставка в Distributed** → строки маршрутизируются по шардам по ключу шардирования (здесь `rand()` — равномерно случайно)
- **Запрос к Distributed** → веером уходит на все шарды, каждый считает свою часть локально, а инициатор объединяет результаты. Так скан распараллеливается по всему кластеру

Двухуровневая схема (local + distributed) разделяет ответственность: ReplicatedMergeTree обеспечивает отказоустойчивость внутри шарда (Q8), а Distributed — горизонтальное масштабирование между шардами.

**Определение кластера** в конфиге:
```xml
<remote_servers>
  <my_cluster>
    <shard><replica><host>node1</host></replica></shard>
    <shard><replica><host>node2</host></replica></shard>
  </my_cluster>
</remote_servers>
```

## Q20. Replication через ZooKeeper / Keeper?

Реплики ClickHouse не общаются напрямую — им нужен внешний **сервис координации**, который хранит общий журнал операций и служит «единым источником правды» о том, что должно быть на каждой реплике:
- **Apache ZooKeeper** — исторический вариант
- **ClickHouse Keeper** — более новая нативная замена (см. Q21)

**Зачем именно координатор** (это решение классической задачи распределённого консенсуса):
- **Регистрация реплик** — кто входит в набор реплик шарда
- **Упорядочивание вставок** — общая очередь операций, чтобы все реплики применяли изменения в одном порядке
- **Синхронизация merge** — какие части в какие сливать, чтобы реплики не расходились
- **Выбор лидера (leader election)** — кто отвечает за назначение merge
- **Распространение DDL** — `ON CLUSTER`-команды доезжают до всех узлов

## Q21. Чем ClickHouse Keeper отличается от ZooKeeper?

**ClickHouse Keeper** (с 2021) — собственная замена ZooKeeper, написанная на C++. Делает ту же работу координатора (Q20), но без зависимости от JVM и связанных с ней проблем (GC-паузы, прожорливость к памяти).

**Чем лучше:**
- **Быстрее** — до 10x на некоторых операциях
- **Меньше памяти** — нет JVM-оверхеда
- **Можно встроить в процесс ClickHouse** — не нужен отдельный кластер ZooKeeper
- **Тот же консенсус на основе Raft** — те же гарантии корректности
- **Совместим с протоколом ZooKeeper** — переезд без переписывания клиентов

К **2025** Keeper — рекомендуемый вариант для новых развёртываний; ZooKeeper остаётся в основном ради совместимости со старыми кластерами.

## Q22. (!) Storage engine оптимизации (compression, sparse data)?

Хорошее сжатие — половина скорости ClickHouse (меньше байт с диска — быстрее запрос). Сжатие можно настраивать на уровне колонки через кодеки, причём специализированные кодеки используют структуру данных, а не просто жмут байты.

**Кодеки сжатия:**
- **LZ4** — по умолчанию, очень быстрый, среднее сжатие
- **ZSTD** — лучшее соотношение сжатия, чуть медленнее (часто ставят финальной ступенью)
- **Специализированные** — `Delta`/`DoubleDelta` хранят разницу между соседними значениями (идеально для монотонно растущих временных меток), `Gorilla` оптимизирован под float-метрики

Кодеки комбинируются: сначала специализированный преобразует данные (`DoubleDelta`), затем ZSTD дожимает результат.

```sql
CREATE TABLE metrics (
    timestamp DateTime CODEC(DoubleDelta, ZSTD),
    value Float64 CODEC(Gorilla, ZSTD)
) ENGINE = MergeTree() ORDER BY timestamp;
```

**Эффект:** типично **сжатие 5–10x**, на хорошо подобранных кодеках — до 50x.

**LowCardinality(String)** — для колонок с небольшим числом разных значений (status, country): значения заменяются на словарь (dictionary encoding), и в данных хранятся компактные коды вместо повторяющихся строк. Ускоряет и хранение, и группировки/фильтры по такой колонке.
```sql
status LowCardinality(String)  -- dictionary encoding
```

## Q23. Что такое многоуровневое хранение (tiered storage, hot/cold)?

**Tiered storage** — хранение данных на дисках разной стоимости в зависимости от их «температуры»: свежие, часто запрашиваемые данные (**hot**) лежат на быстром SSD, а старые (**cold**) автоматически переезжают на дешёвое медленное хранилище (S3/HDD). Это компромисс «скорость против цены»: вы платите за SSD только под актуальную часть данных.

```sql
-- Storage policy в config
<storage_configuration>
  <policies>
    <hot_cold>
      <volumes>
        <hot><disk>fast_ssd</disk></hot>
        <cold><disk>s3</disk></cold>
      </volumes>
      <move_factor>0.2</move_factor>
    </hot_cold>
  </policies>
</storage_configuration>

-- Apply to table
CREATE TABLE events (...) ENGINE = MergeTree()
SETTINGS storage_policy = 'hot_cold';
```

Переезд автоматизируется через **TTL** — правило «данные старше N дней перенести на cold-том»:
```sql
ALTER TABLE events MODIFY TTL event_time + INTERVAL 30 DAY TO VOLUME 'cold';
```
Здесь `move_factor` (0.2 в конфиге) задаёт порог: когда на hot-томе остаётся меньше 20% места, ClickHouse начинает сдвигать части на cold.

**Экономия** — старые данные дешёво лежат на S3, оставаясь при этом доступными для запросов.

## Q24. (!) Kafka engine для ingestion?

**Kafka Engine** превращает ClickHouse в consumer Kafka: таблица с этим движком сама вычитывает сообщения из топика. Но такая таблица — лишь «лента»: при каждом SELECT сообщения вычитываются и пропадают, данные в ней не хранятся. Чтобы они оседали, поверх ставят MV, которая перекладывает прочитанное в обычную MergeTree-таблицу.

```sql
CREATE TABLE events_kafka (
    event_time DateTime,
    user_id UInt64,
    data String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka:9092',
    kafka_topic_list = 'events',
    kafka_group_name = 'clickhouse-consumer',
    kafka_format = 'JSONEachRow';

-- MV для materialization Kafka → MergeTree table
CREATE MATERIALIZED VIEW events_consumer
TO events  -- target table (MergeTree)
AS SELECT * FROM events_kafka;
```

Получается связка из трёх частей: **Kafka Engine (читает топик) → MV (триггер) → MergeTree (хранит)**. Это даёт непрерывный приём Kafka → ClickHouse без отдельного ingestion-сервиса.

**Аналогично устроены** движки RabbitMQ, NATS и S3 Queue — тот же паттерн «движок-потребитель + MV → целевая таблица».

## Q25. Какие ещё табличные движки есть (S3, MySQL, PostgreSQL)?

Кроме MergeTree-семейства есть **интеграционные движки** — они не хранят данные, а дают читать (а часто и писать) внешний источник прямо из SQL, как будто это локальная таблица. Удобно для федеративных запросов и ETL без отдельного pipeline.

**Основные внешние движки и табличные функции:**
- **S3** — чтение/запись Parquet/CSV/JSON в объектном хранилище
```sql
SELECT * FROM s3('s3://bucket/data.parquet', 'Parquet')
```
- **PostgreSQL** — федеративные запросы
```sql
CREATE TABLE pg_users ENGINE = PostgreSQL('host:5432', 'db', 'users', 'user', 'pass');
```
- **MySQL** — аналогично
- Движки **HDFS, URL, File**

**Сценарий:** ETL без внешнего pipeline — `INSERT INTO local SELECT FROM s3(...)`.

## Q26. (!) Какие use cases ClickHouse в production?

Общий знаменатель всех сценариев один: данные **много пишутся и аналитически читаются, но почти не обновляются построчно**. Именно поэтому ClickHouse доминирует в observability и real-time-аналитике.

**Кто использует:**
- **Yandex** (создатели) — Metrica
- **Cloudflare** — аналитика
- **Uber** — observability (логи, метрики, трейсы)
- **Spotify** — аналитика
- **Mercedes-Benz, eBay, GitLab, Lyft**

**Типичные сценарии:**
- **Observability** — логи (дешевле ELK), метрики (замена Prometheus), трейсы (бэкенд Jaeger)
- **Real-time-аналитика** — дашборды
- **Ad-tech** — анализ ставок, атрибуция
- **Веб-аналитика** — clickstream
- **Временные ряды** — IoT, мониторинг
- **Хранилище данных** для real-time-аналитики

## Q27. (!) Какие ограничения / минусы ClickHouse?

Почти все минусы — оборотная сторона OLAP-оптимизации: то, чем ClickHouse платит за скорость на сканах. Сгруппируем по природе.

**Это не OLTP-база (следствие модели данных):**
- **Нет полноценных транзакций** — атомарность только в рамках одной вставки
- **Ограниченные UPDATE/DELETE** — реализованы как мутации: медленные, асинхронные, переписывают части целиком
- **Разреженная индексация плохо подходит для точечных запросов** (см. Q10)
- **Нет внешних ключей и constraints** — целостность данных на стороне приложения

**Слабее в гибкости:**
- **JOIN-ы слабее, чем в PostgreSQL** (см. Q15)
- **Schema-on-write** — жёсткие схемы, менять структуру дороже

**Эксплуатация сложнее:**
- **Крутая кривая обучения** — без знания внутренностей легко получить медленные запросы
- **Сложная эксплуатация кластера** — настройка шардирования и репликации вручную
- **Прожорлив к памяти** — тяжёлые запросы и JOIN-ы могут падать с OOM
- **Накладные расходы на строку** — очень широкие таблицы бывают неэффективны

## Q28. Common pitfalls в ClickHouse production?

Большинство граблей сводится к двум темам: неправильное проектирование схемы (ORDER BY, партиционирование, движок) и неверный паттерн записи (мелкие вставки, мутации). Вот частые ошибки и чем они грозят:

- **Неудачный ORDER BY** — индекс не отсекает гранулы, запросы медленные, индекс раздут
- **Ключ партиционирования с высокой кардинальностью** — взрыв числа частей, merge не успевает (см. Q9)
- **OOM на JOIN-ах** — в память грузится большая правая таблица (см. Q15)
- **Слишком много мелких вставок** — каждая создаёт новую часть и нагружает ZK/Keeper; вставляйте большими батчами
- **Мутации (UPDATE/DELETE)** — медленные, переписывают части целиком, мешают другим операциям
- **Асинхронные вставки** — при сбое возможна небольшая потеря данных (поведение настраивается)
- **Нет TTL для очистки** — старые данные копятся, диск переполняется
- **Нет репликации** — отказ диска = потеря данных (см. Q8)
- **Нет бэкапов** — используйте `ALTER TABLE FREEZE` и копируйте замороженные части
- **Неправильный выбор движка** — например, MergeTree там, где нужен ReplacingMergeTree для дедупликации (см. Q6)

**Рекомендация:** внимательно прочитайте [ClickHouse docs Tips and Tricks](https://clickhouse.com/docs/en/operations/tips/).

---

## See also

- [PostgreSQL](postgresql-interview.md) — OLTP comparison
- [Data Warehousing](../data-engineering/data-warehousing-interview.md) — context
- [Apache Spark](../data-engineering/apache-spark-interview.md) — alternative для batch analytics
- [Apache Flink](../data-engineering/apache-flink-interview.md) — для stream processing → ClickHouse
- [Apache Kafka](../messaging/kafka-interview.md) — Kafka engine ingestion
- [Stream Processing](../data-engineering/stream-processing-interview.md) — context
- [Loki + Grafana](../monitoring/loki-grafana-interview.md) — alternative для logs
- [ELK Stack](../monitoring/elk-stack-interview.md) — alternative для logs
- [OpenTelemetry](../monitoring/opentelemetry-interview.md) — observability data → ClickHouse
- [Микросервисы](../architecture/microservices-interview.md) — analytics backend
- [Caching](../architecture/caching-strategies-interview.md) — для acceleration
- [Database Architecture](database-architecture-interview.md) — OLAP context
- [SQL](sql-interview.md) — общие основы

- [Apache Cassandra](cassandra-interview.md)
- [CockroachDB](cockroachdb-interview.md)
- [Database Architecture](database-architecture-interview.md)
- [Транзакции и уровни изоляции](database-transactions-interview.md)
- [DynamoDB](dynamodb-interview.md)
- [Elasticsearch](elasticsearch-interview.md)
- [Шпаргалка: ClickHouse](../../databases/nosql/clickhouse/clickhouse.md) — теория
