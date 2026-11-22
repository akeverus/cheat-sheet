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

**ClickHouse** — open-source колоночная OLAP-СУБД, которую разработали в Яндексе для Яндекс Метрики и открыли в 2016 году. Её используют для интерактивной аналитики по большим потокам событий, например для расчёта перцентилей задержки по логам или для анализа конверсии по источникам трафика.

Данные хранятся по колонкам и обрабатываются пакетами (Q2). Семейство MergeTree поддерживает разреженный первичный индекс и сжатие (Q5, Q10). Для распределённого развёртывания шардирование делит данные между узлами, а репликация поддерживает копии. Пропускная способность вставки и время появления данных в запросах зависят от размера пакета, способа подтверждения вставки и топологии.

Для сервисов с частыми изменениями отдельных записей и транзакциями над несколькими сущностями выбор обычно начинается с транзакционной СУБД. ClickHouse хорошо подходит для аналитики по истории этих изменений (Q4, Q27).

## Q2. (!) Почему ClickHouse такой быстрый?

Для запроса, который вычисляет p95 длительности запросов по часам за сутки, ClickHouse читает колонки фильтра, группировки и агрегации. Столбцы с телами запросов можно вовсе не читать. Сжатие снижает объём чтения, но коэффициент зависит от данных и кодека.

- **Пропуск данных.** Разреженный первичный индекс сужает чтение до диапазонов гранул, когда предикат согласован с ключом (Q10). Skip-индексы помогают отбросить дополнительные блоки, если по их метаданным можно исключить совпадение (Q12).
- **Обработка пакетами.** Колонки проходят через операции блоками. Некоторые операции используют SIMD, когда это позволяет реализация и процессор.
- **Параллелизм.** Чтение и частичные агрегации могут выполняться параллельно на одном сервере и на нескольких шардах. Итоговое объединение результатов тоже требует времени и ресурсов.

При диагностике начните с `EXPLAIN indexes = 1` и числа прочитанных строк и байтов в `system.query_log`. Если запрос всё равно читает большую долю таблицы, одна замена ключа или добавление индекса не гарантирует ускорения. [Описание движка ClickHouse](https://clickhouse.com/uploads/Click_House_Schulze_1_c12ecfaed4.pdf).

## Q3. (!) Чем ClickHouse отличается от Snowflake и BigQuery?

Все три системы обслуживают аналитические нагрузки. При выборе сравнивают модель эксплуатации, масштабирование вычислений и способ оплаты.

| Система | Как устроены вычисления и хранение |
|---|---|
| ClickHouse | При self-hosted развёртывании команда управляет узлами и размещением данных. В ClickHouse Cloud семейство SharedMergeTree использует общее хранилище. |
| Snowflake | Управляемый сервис с общим хранилищем и независимыми виртуальными warehouse. Вычислительные ресурсы для задач можно выделять отдельно. |
| BigQuery | Управляемый сервис. Запросы выполняются с помощью слотов. Для вычислений можно выбрать оплату по обработанным данным или по выделяемой вычислительной ёмкости. |

Например, для дашборда с постоянным потоком событий сравните задержку появления записи, p95 запросов при целевой конкуренции и полную стоимость хранения, вычислений и эксплуатации. Для нерегулярных пакетных запросов отдельно оцените стоимость и время ожидания при пиковом запуске. Результат зависит от схемы, запросов и режима оплаты. [ClickHouse Cloud](https://clickhouse.com/docs/products/cloud/features/infrastructure/shared-merge-tree) · [Snowflake](https://docs.snowflake.com/en/user-guide/intro-key-concepts) · [BigQuery](https://docs.cloud.google.com/bigquery/docs/reservations-intro).

## Q4. ClickHouse vs PostgreSQL для аналитики?

Для платёжного сервиса PostgreSQL может хранить заказы и проводки. Транзакция атомарно меняет связанные строки, а уникальные ключи помогают обеспечить инварианты при конкуренции. PostgreSQL также умеет выполнять аналитические запросы, включая параллельные сканы и агрегации там, где планировщик выбирает параллельный план.

ClickHouse удобно использовать для аналитики по истории заказов. Например, полученные через CDC изменения попадают в аналитическую таблицу, а дашборд группирует их по городу и часу. Колоночное чтение полезно, когда запрос затрагивает много строк и использует небольшую часть колонок. Возможности соединений зависят от вида запроса и алгоритма (Q15).

При таком разделении между коммитом в PostgreSQL и видимостью записи в ClickHouse есть задержка доставки и обработки. При проверке баланса или статуса только что созданного заказа читайте транзакционный источник. Для дашборда задайте допустимое отставание и измеряйте его. [Параллельные планы PostgreSQL](https://www.postgresql.org/docs/current/parallel-plans.html).

## Q5. (!) Что такое MergeTree?

**MergeTree** — основной storage-движок ClickHouse, на котором строятся почти все рабочие таблицы. Имя отражает суть: данные пишутся отдельными кусками, а фоновый процесс их объединяет (merge), как в LSM-дереве.

**Как это работает:**
- Из вставляемых блоков движок создаёт **части (parts)** по затронутым партициям. Строки каждой части отсортированы по `ORDER BY`
- Фоновый **merge** объединяет выбранные части одной партиции. Меньшее число частей обычно сокращает накладные расходы на чтение
- Внутри части данные физически отсортированы по `ORDER BY` — это и даёт быстрые range-сканы
- Разреженный первичный индекс помогает отсеивать гранулы (см. Q10)

Частые мелкие вставки могут создавать части быстрее, чем фоновые слияния их объединяют. В этом случае группируйте строки в батчи или оцените асинхронные вставки (см. Q28).

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

Эти движки добавляют правила обработки строк при слиянии частей. Одному ключу могут соответствовать строки в нескольких активных частях, поэтому запрос должен учитывать правила движка даже после фоновых слияний.

**ReplacingMergeTree** — дедуплицирует строки с одинаковым ключом `ORDER BY`: в примере движок сохраняет строку с максимальным `updated_at` среди строк одного ключа, участвующих в слиянии. Если несколько обновлений помещаются в одну секунду, для однозначного выбора лучше использовать монотонный номер версии. Для чтения с дедупликацией используйте `SELECT … FINAL`, учитывая стоимость обработки при чтении.

```sql
CREATE TABLE users (
    id UInt64,
    email String,
    updated_at DateTime
) ENGINE = ReplacingMergeTree(updated_at)
ORDER BY id;
```

**Граница партиции.** Если добавить к примеру `PARTITION BY toYYYYMM(updated_at)`, версии одного `id` за октябрь и ноябрь попадут в разные партиции. Фоновое слияние их не объединит. С настройкой `do_not_merge_across_partitions_select_final = 1` даже `SELECT … FINAL` обработает партиции независимо и может вернуть обе версии. Для такого режима выбирайте ключ партиционирования, который остаётся постоянным для всех версий строки. [Руководство ReplacingMergeTree](https://clickhouse.com/docs/guides/replacing-merge-tree#exploiting-partitions-with-replacingmergetree)

**SummingMergeTree** — при merge суммирует числовые колонки для строк с одинаковым `ORDER BY`. Удобно для предагрегированных счётчиков: слияния сокращают число частичных строк, а запрос с `sum(visits)` и `GROUP BY (date, user_id)` получает итог и при нескольких активных частях.

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

**AggregatingMergeTree** — обобщение SummingMergeTree на произвольные агрегации через тип `AggregateFunction` (не только sum, но и uniq, avg, quantile и т. д.). Хранит частичные состояния агрегатов. Например, для среднего записывают `avgState(duration)`, а читают `avgMerge(state)` с группировкой по нужному ключу. Это объединяет состояния из нескольких частей без усреднения средних.

**Связка с MV:** инкрементальное материализованное представление (Q17) может записывать состояния агрегатов из каждого вставленного блока в таблицу AggregatingMergeTree.

[ReplacingMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/replacingmergetree) · [SummingMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/summingmergetree) · [AggregatingMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/aggregatingmergetree)

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
INSERT INTO events VALUES (1, -1, 100), (1, 1, 150);
```

При корректной последовательности состояний и отмен слияние убирает отменённую пару для одного ключа. В примере состояние 100 и его отмена схлопываются, остаётся состояние 150. До слияния запрос может видеть все три строки.

**Сценарий:** обновления в модели, которая в остальном неизменяема (например, изменяющийся счётчик активности пользователя).

**Подводный камень:** обычный `sum(activity_count)` учитывает и отменённое состояние, и строку отмены. Для суммы текущих состояний группируйте по `user_id`, считайте `sum(toInt128(activity_count) * sign)` и отбирайте ключи через `HAVING sum(sign) > 0`. Для чтения актуальных строк можно применить `FINAL` с учётом его стоимости.

**Порядок записи.** Для обычного `CollapsingMergeTree` формируйте последовательную историю каждого ключа: состояние, его отмена, следующее состояние. Если несколько потоков могут записать эти строки в другом порядке, используйте `VersionedCollapsingMergeTree(sign, version)`. Тогда отмена состояния 100 получает ту же версию, что и строка со значением 100, а состояние 150 — следующую версию. При слиянии движок сопоставляет строки одного ключа и версии с противоположными знаками независимо от порядка вставки. Пропущенную отмену приложение всё равно должно доставить. [VersionedCollapsingMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/versionedcollapsingmergetree)

[CollapsingMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/collapsingmergetree)

## Q8. Что такое ReplicatedMergeTree?

**ReplicatedMergeTree** — это MergeTree с репликацией: реплики одного шарда хранят копии данных. Keeper или ZooKeeper хранит общий журнал, из которого каждая реплика заполняет свою очередь задач (см. Q20).

```sql
CREATE TABLE events (event_time DateTime, user_id UInt64)
ENGINE = ReplicatedMergeTree(
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

В примере `{shard}` и `{replica}` подставляются из конфигурации. У реплик одного шарда общий путь в Keeper и разные имена реплик.

**Пример.** После обычного успешного `INSERT` чтение с другой реплики может ещё не увидеть вставку. `insert_quorum` позволяет дождаться подтверждения от заданного числа реплик. Репликация помогает пережить отказ узла, а резервные копии нужны для восстановления после ошибок, которые затронули все реплики.

[Документация ReplicatedMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/replication)

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
- **Partition pruning** — фильтр `WHERE toYYYYMM(event_time) = 202504` позволяет исключить партиции других месяцев
- **Операции над целым месяцем** — например, `DROP PARTITION 202504` удаляет партицию без построчной обработки
- **Граница слияний** — фоновые merge объединяют части только внутри одной партиции

**Рекомендации по выбору ключа:**
- Выбирайте ключ с умеренной кардинальностью. Если один INSERT затрагивает много партиций, он создаёт части в каждой из них. Части из разных партиций останутся раздельными (см. Q28)
- Типично — по месяцу или неделе для временных рядов
- **Не партиционируйте по колонке с высокой кардинальностью** (например по user_id) — это и порождает взрыв числа частей

**Не путать с ORDER BY:** партиционирование грубо отсекает диапазоны, а сортировка строк внутри каждой части помогает первичному индексу отсеивать гранулы (см. Q10–Q11).

[Партиционирование MergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/mergetree#partition-by)

## Q10. (!) Sparse index — что это?

В каждой части MergeTree **разреженный первичный индекс** хранит значение ключа первой строки каждой гранулы. При стандартном `index_granularity = 8192` гранула содержит до 8192 строк. При адаптивной гранулярности `index_granularity_bytes` влияет на число строк в грануле.

```
Primary index (sparse):
[key=10, granule=0]
[key=100, granule=1]
[key=200, granule=2]
...
```

**Как ищется значение.** При `WHERE key = 150` метки помогают сузить диапазон чтения в каждой части. В показанном фрагменте ключ может встретиться в грануле между метками 100 и 200. ClickHouse читает нужные столбцы из гранул-кандидатов и проверяет условие на строках. При нескольких частях или повторяющихся ключах кандидатов может быть несколько.

**Компромисс:**
- **Плюс:** индекс компактнее индекса с записью на каждую строку
- **Плюс:** диапазон по началу ключа сортировки позволяет пропустить гранулы вне диапазона
- **Минус:** даже для одной подходящей строки движок может прочитать несколько гранул

[Гранулы и первичные индексы MergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/mergetree#data-storage)

## Q11. Чем отличаются ORDER BY и PRIMARY KEY?

В MergeTree `ORDER BY` задаёт ключ сортировки строк внутри каждой части. `PRIMARY KEY` задаёт выражения, значения которых попадают в разреженный индекс. Явный первичный ключ должен быть префиксом ключа сортировки. Без явного `PRIMARY KEY` индекс строится по ключу сортировки. Повторы ключа допустимы.

```sql
CREATE TABLE events_by_user (
    user_id UInt64,
    event_time DateTime
) ENGINE = MergeTree()
ORDER BY (user_id, event_time)
PRIMARY KEY user_id;
```

Индекс может сократить чтение при фильтрации по `user_id`. Время сортирует события внутри группы пользователя, но в этом примере в первичном индексе его нет. При запросе только по времени первичный индекс по пользователю сам по себе мало помогает.

Выбирайте порядок выражений по реальным фильтрам. Если большинство запросов фильтрует по пользователю и диапазону времени, сравните `PRIMARY KEY user_id` с полным ключом по обеим колонкам. Смотрите на прочитанные гранулы и размер индекса. Низкая кардинальность может улучшить сжатие, когда сортировка собирает близкие значения, но её эффект зависит от распределения и запросов. [Документация MergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/mergetree).

## Q12. Что такое skip-индексы (data skipping)?

**Skip-индекс** хранит сводную информацию о блоках гранул. ClickHouse пропускает блок, когда из сводки и условия запроса следует, что в блоке нет подходящих строк. В оставшихся блоках движок проверяет условие на самих данных.

- **`minmax`** хранит минимум и максимум выражения. Полезен для диапазонов, когда значения внутри блока близки.
- **`set(N)`** хранит множество значений блока. Если различных значений больше `N`, индекс для этого блока не помогает отсечению.
- **`bloom_filter`** проверяет принадлежность к множеству. Ложное срабатывание приводит к чтению лишнего блока.
- **`tokenbf_v1` и `ngrambf_v1`** — устаревающие Bloom-индексы по токенам и n-граммам. Поиск токена `hasToken(message, 'error')` и поиск подстроки `LIKE '%error%'` задают разные условия. Для полнотекстового поиска документация рекомендует инвертированный индекс `text`.

**Пример.** Логи отсортированы по времени. Отдельный индекс помогает искать события с высокой задержкой, если они сосредоточены в отдельных блоках.

```sql
CREATE TABLE logs (
    event_time DateTime,
    latency_ms UInt32,
    message String,
    INDEX latency_idx latency_ms TYPE minmax GRANULARITY 4
) ENGINE = MergeTree()
ORDER BY event_time;

EXPLAIN indexes = 1
SELECT count()
FROM logs
WHERE latency_ms >= 5000;
```

Здесь одна запись skip-индекса покрывает блок из четырёх гранул. При максимуме ниже 5000 блок можно пропустить. Если в каждом блоке есть хотя бы один запрос с задержкой от 5000 мс, этот индекс почти не сократит чтение. В плане сравните число гранул до и после применения `latency_idx`.

[Skip-индексы](https://clickhouse.com/docs/concepts/features/performance/skip-indexes/skipping-indexes) · [EXPLAIN](https://clickhouse.com/docs/reference/statements/explain)

## Q13. (!) ClickHouse SQL — особенности?

ClickHouse использует знакомые конструкции SQL, например `SELECT`, `WHERE` и `GROUP BY`. Диалект добавляет операции над массивами, функции для временных рядов и приближённые агрегаты. При переносе запросов проверяйте сигнатуры функций и совпадение семантики.

```sql
SELECT toStartOfHour(event_time) AS hour, count(*) AS events_count
FROM events
GROUP BY hour
ORDER BY hour;

SELECT arraySum(prices), arrayAvg(prices)
FROM orders;

SELECT uniqHLL12(user_id) AS estimated_users,
       uniqExact(user_id) AS exact_users
FROM events;
```

Для дашборда число уникальных посетителей можно оценить через `uniqHLL12`, если измеренная погрешность устраивает потребителя. `uniqExact` даёт точный результат, а размер его состояния растёт с числом уникальных значений. Для перцентиля задержки `quantileTDigest(0.95)(latency_ms)` возвращает приближение. Выбор зависит от допустимой ошибки, объёма состояния и измеренного времени запроса.

[uniqHLL12](https://clickhouse.com/docs/reference/functions/aggregate-functions/uniqHLL12) · [quantileTDigest](https://clickhouse.com/docs/reference/functions/aggregate-functions/quantileTDigest)

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

**Зачем это нужно:** массивы — главный инструмент денормализации в ClickHouse. Массив подходит, когда элементы принадлежат одной строке и обычно читаются вместе с ней. Для самостоятельных сущностей с отдельным жизненным циклом можно использовать таблицу и JOIN (Q15). Обычный `ARRAY JOIN` исключает строки с пустым массивом. `LEFT ARRAY JOIN` сохраняет такую строку и возвращает значение по умолчанию для типа элемента. [Документация ARRAY JOIN](https://clickhouse.com/docs/reference/statements/select/array-join).

## Q15. (!) JOINs в ClickHouse — особенности?

Стоимость JOIN зависит от размера входов после фильтрации, кардинальности ключей и алгоритма. Для запроса по событиям и справочнику пользователей сначала определите, нужны ли поля справочника и сколько строк должно получиться при нескольких совпадениях.

- **`hash` и `parallel_hash`** строят хеш-структуры по одной стороне соединения. Память зависит от числа строк и ширины данных на этой стороне. Планировщик в подходящих случаях может поменять стороны. Проверяйте фактический план запроса.
- **`grace_hash`** делит входы на группы по хешу и может держать часть групп на диске. Выбор снижает требования к оперативной памяти ценой дискового ввода-вывода. **`full_sorting_merge`** соединяет отсортированные потоки и может пропустить сортировку стороны, если порядок чтения уже подходит. Доступность алгоритма зависит от типа и условий JOIN.
- **Распределённое выполнение.** При обычном JOIN с локальным справочником каждый шард строит свою сторону соединения. Если строки справочника распределены между шардами, совпадающие пары должны оказаться на одном шарде. Для `GLOBAL INNER JOIN` инициатор вычисляет правую сторону и отправляет временный результат удалённым серверам. В этом случае учитывайте объём сетевой передачи.

**Пример.** Для отчёта нужны только события пользователей из США. `IN` выражает проверку принадлежности. Если в справочнике два подходящих ряда для одного пользователя, `ALL INNER JOIN` выдаст две строки события, а фильтр `IN` оставит исходную строку один раз.

```sql
SELECT event_time, user_id
FROM events
WHERE user_id IN (SELECT id FROM users WHERE country = 'US');
```

Если отчёту также нужны поля пользователя, оставьте JOIN и проверьте ожидаемую кратность. Для часто повторяющегося отчёта можно сохранить атрибуты пользователя вместе с событием. При этом нужно явно решить, показывает отчёт страну на момент события или текущую страну из справочника.

[Алгоритмы JOIN](https://clickhouse.com/docs/concepts/features/operations/select/join-algorithms) · [Семантика и распределённое выполнение JOIN](https://clickhouse.com/docs/reference/statements/select/join)

## Q16. Поддерживаются ли window-функции?

Да. Оконные функции с синтаксисом `OVER (PARTITION BY ... ORDER BY ...)` поддерживаются — running totals, ранжирование, скользящие окна и т. п.

```sql
SELECT
    user_id,
    event_time,
    sum(amount) OVER (
        PARTITION BY user_id ORDER BY event_time, event_id
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS running_total,
    rank() OVER (PARTITION BY user_id ORDER BY amount DESC) AS rnk
FROM transactions;
```

Если сортировать окно только по `event_time` и оставить рамку по умолчанию, `RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW` включит все строки с тем же временем. Например, два первых события на 10 и 20 с одинаковым временем получат сумму 30 каждое. Для накопления по событиям пример задаёт `ROWS` и использует `event_id` как уникальный в пределах пользователя ключ для разрешения совпадений. `rank()` использует собственный порядок по сумме и присваивает одинаковым суммам одинаковое место. Для порядка вывода нужен отдельный `ORDER BY` запроса. [Документация оконных функций](https://clickhouse.com/docs/reference/functions/window-functions).

## Q17. (!) Materialized views в ClickHouse?

В ClickHouse есть **инкрементальные** и **обновляемые по расписанию** материализованные представления. Инкрементальное MV запускает запрос для каждого вставленного блока исходной таблицы и записывает результат в целевую таблицу. Такой подход переносит часть работы с чтения на вставку. Изменение или удаление в источнике не пересчитывает ранее записанный результат. Исторические строки нужно загрузить отдельно или включить их загрузку при создании MV.

Для примера ниже можно приостановить вставки в `events`, дождаться завершения уже начатых вставок, создать MV и выполнить тот же агрегирующий `SELECT` по истории через `INSERT INTO hourly_stats SELECT ...`. Затем возобновить запись в `events`. Повторная обработка того же исторического диапазона добавит те же частичные суммы ещё раз.

Для варианта с внутренней таблицей есть `CREATE MATERIALIZED VIEW … POPULATE`. Начиная с ClickHouse 26.8 при включённом по умолчанию `materialized_views_populate_atomically` подписка на новые вставки и получение снимка истории согласованы. При старом поведении конкурентная вставка могла выпасть из обоих путей. [Atomic POPULATE в ClickHouse 26.8](https://clickhouse.com/blog/clickhouse-release-26-08#atomic-populate)

Обновляемое по расписанию представление с `REFRESH EVERY` запускает запрос периодически. По умолчанию успешное обновление атомарно заменяет предыдущий результат. Это подходит для витрины, которая должна отразить исправления в исходных данных при следующем пересчёте. С `APPEND` результат каждого запуска дописывается, например для хранения истории снимков. Для обычного `APPEND` вставка результата может быть видна частями.

В ClickHouse 26.9 появился режим `APPEND INCREMENTAL` для журналов событий с добавлением строк. Он обрабатывает строки, зафиксированные с предыдущего обновления. Позиция определяется номером блока и смещением внутри него, поэтому поздно поступившее событие со старым `event_time` всё равно может попасть в очередное обновление. Режим требует одного источника `MergeTree` с `enable_block_number_column = 1` и `enable_block_offset_column = 1`. Запросы с `JOIN` или `UNION` для этого режима не поддерживаются. [CREATE VIEW](https://clickhouse.com/docs/reference/statements/create/view#refreshable-materialized-view)

В примере ниже используется инкрементальное MV, привязанное ко вставкам.

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

Если за один час для пользователя пришли две пачки событий, в `hourly_stats` могут лежать две строки с одним ключом. `SummingMergeTree` суммирует их при фоновых слияниях, поэтому запрос дашборда сам складывает частичные суммы.

```sql
SELECT hour, user_id,
       sum(event_count) AS events,
       sum(total_amount) AS amount
FROM hourly_stats
GROUP BY hour, user_id;
```

[Инкрементальные MV](https://clickhouse.com/docs/concepts/features/materialized-views/incremental-materialized-view) · [SummingMergeTree](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/summingmergetree) · [ClickHouse 26.9](https://clickhouse.com/blog/clickhouse-release-26-09)

## Q18. Что такое projections?

**Projection** хранится вместе с частями исходной таблицы MergeTree. Она может предоставить другой порядок сортировки и первичный индекс либо хранить заранее вычисленные агрегаты. Запрос по-прежнему обращается к исходной таблице. ClickHouse выбирает проекцию, если она подходит для запроса и по оценке уменьшает объём чтения.

Допустим, таблица `events` из Q17 отсортирована по времени, но часто нужно искать события конкретного пользователя. Добавим полный вариант проекции, отсортированный по `user_id`.

```sql
ALTER TABLE events ADD PROJECTION events_by_user (
    SELECT *
    ORDER BY user_id
);

-- Построить проекцию и для ранее записанных частей.
ALTER TABLE events MATERIALIZE PROJECTION events_by_user;
```

После `ADD PROJECTION` проекция создаётся для новых вставок. Для проверки на исторических данных дождитесь завершения материализации и выполните обычный запрос к `events` с фильтром по `user_id`. Поле `projections` в `system.query_log` покажет, использовал ли её ClickHouse. Выгода зависит от запроса и селективности фильтра.

Полная проекция требует дополнительного места и работы при вставке. Начиная с ClickHouse 25.5, проекция может хранить ключ сортировки и `_part_offset`, а остальные столбцы читать из исходной части. Это сокращает дублирование за счёт дополнительного чтения. У инкрементального MV из Q17 своя целевая таблица, и запросы к её агрегатам обращаются к ней явно.

С ClickHouse 25.6 несколько проекций могут участвовать в отборе частей для одного запроса. С 25.11 проекции с `_part_offset` позволяют также исключать гранулы исходной таблицы. Например, при фильтре по `user_id` и диапазону `event_time` проекция по пользователю и первичный индекс по времени могут вместе сузить чтение. Применение обоих индексов зависит от плана и оценки объёма чтения. С 26.1 при создании таблицы такую проекцию можно объявить через `PROJECTION events_by_user INDEX user_id TYPE basic` вместо объявления с `SELECT _part_offset`. [ClickHouse 25.6](https://clickhouse.com/blog/clickhouse-release-25-06) · [ClickHouse 25.11](https://clickhouse.com/blog/clickhouse-release-25-11) · [ClickHouse 26.1](https://clickhouse.com/blog/clickhouse-release-26-01)

[ClickHouse Projections](https://clickhouse.com/docs/concepts/features/projections/projections)

## Q19. (!) Что такое Distributed-таблица?

**Distributed-таблица** не хранит данные сама — это таблица-прокси над **шардами** кластера. На узле-инициаторе могут храниться временные файлы очереди фоновой отправки. Реальные данные лежат в локальных таблицах на каждом узле (обычно ReplicatedMergeTree), а Distributed-таблица знает топологию кластера и раскидывает по ней запись и чтение.

```sql
-- Local table on each node
CREATE TABLE events_local (...) ENGINE = ReplicatedMergeTree(...);

-- Distributed table (no data of its own, queries across shards)
CREATE TABLE events_distributed AS events_local
ENGINE = Distributed(my_cluster, default, events_local, rand());
```

- **Вставка в Distributed.** Ключ шардирования определяет, на какой шард направить каждую строку. В этом примере `rand()` распределяет строки случайно, с учётом весов шардов.
- **Чтение из Distributed.** Обычно запрос выполняется на одной выбранной реплике каждого нужного шарда. Инициатор объединяет результаты, например промежуточные состояния агрегатов. С `optimize_skip_unused_shards` ClickHouse может исключить шарды, если это следует из фильтра и ключа шардирования. С `skip_unavailable_shards` успешный запрос может вернуть данные только с доступных шардов.

**Когда клиент получает подтверждение вставки** (при `async_insert = 0`):

- При `distributed_foreground_insert = 0` блоки для фоновой отправки попадают в локальную очередь инициатора. Подтверждение может прийти до записи на целевом шарде. Очередь и ошибки доставки видны на инициаторе в `system.distribution_queue`. Настройки `fsync_after_insert` и `fsync_directories` относятся к сохранности файлов очереди на инициаторе.
- При `distributed_foreground_insert = 1` инициатор ждёт выполнения вставок на целевых шардах. С `internal_replication = true` и обычной вставкой без кворума достаточно подтверждения одной реплики на каждом затронутом шарде. Остальные реплики могут отставать. При значении `internal_replication = false` (по умолчанию) Distributed сам отправляет запись всем репликам шарда. Для локальных таблиц `ReplicatedMergeTree` в примере включён `internal_replication`, чтобы копированием внутри шарда управлял движок таблицы. Если один шард записал свой блок, а второй вернул ошибку, уже выполненная запись сохраняется.

**Пример.** Сразу после подтверждения фоновой вставки отчёт может не увидеть строки, пока очередь не отправила их на шард. При записи в foreground доставка завершается до подтверждения, но отчёт всё ещё может обратиться к отстающей реплике. При диагностике разделяйте задержку отправки с инициатора и задержку репликации внутри шарда.

Двухуровневая схема (local + distributed) разделяет ответственность: ReplicatedMergeTree обеспечивает отказоустойчивость внутри шарда (Q8), а Distributed — горизонтальное масштабирование между шардами.

**Определение кластера** в конфиге:
```xml
<remote_servers>
  <my_cluster>
    <shard>
      <internal_replication>true</internal_replication>
      <replica><host>shard1-replica1</host></replica>
      <replica><host>shard1-replica2</host></replica>
    </shard>
    <shard>
      <internal_replication>true</internal_replication>
      <replica><host>shard2-replica1</host></replica>
      <replica><host>shard2-replica2</host></replica>
    </shard>
  </my_cluster>
</remote_servers>
```

[Документация ClickHouse по Distributed](https://clickhouse.com/docs/reference/engines/table-engines/special/distributed) · [Очередь доставки](https://clickhouse.com/docs/operations/system-tables/distribution_queue)

## Q20. Replication через ZooKeeper / Keeper?

В self-managed ClickHouse движок `ReplicatedMergeTree` использует **Keeper или ZooKeeper** для координации реплик одного шарда. Координатор хранит метаданные, сведения о репликах и общий журнал операций. Части таблицы хранятся на серверах ClickHouse. При необходимости реплика скачивает часть непосредственно с другой реплики.

- **Вставка.** Запись принимает доступная реплика с рабочей сессией координатора. Она сохраняет часть и регистрирует её в метаданных. Остальные реплики асинхронно переносят задания из общего журнала в свои очереди и выполняют их. Обычный успешный `INSERT` может стать видимым на разных репликах в разное время.
- **Слияния.** Реплики с ролью лидера планируют фоновые merge. Лидеров может быть несколько. Для принятия вставки эта роль не требуется.
- **Распределённый DDL.** `ON CLUSTER` использует отдельную очередь в координаторе. Каждый узел выполняет команду локально. Если узел временно недоступен, выполнение на нём откладывается.

**Подтверждение записи.** В одном шарде с тремя репликами прямой синхронный `INSERT` в `ReplicatedMergeTree` с `insert_quorum = 2` возвращает успех, когда запись подтверждена двумя репликами. Третья может ещё отставать, поэтому обычный `SELECT`, направленный на неё сразу после вставки, может не увидеть новые строки. Если нужна видимость на любой из трёх реплик перед следующим шагом, можно дождаться всех трёх через `insert_quorum = 3`. Тогда при недоступности одной реплики вставка не сможет получить подтверждение. Это кворум реплик таблицы, а у Keeper свой кворум для операций с метаданными. [Репликация и подтверждение вставки](https://clickhouse.com/blog/building-a-data-warehouse-with-clickhouse)

**Пример.** При потере сессии Keeper реплика может перейти в режим только для чтения. Обычный локальный `SELECT` по уже имеющимся частям продолжает работать, а новая запись, для которой требуется координация, может завершиться ошибкой. Для диагностики проверьте `is_readonly`, `is_session_expired` и `queue_size` в `system.replicas`.

[Реплицируемые движки](https://clickhouse.com/docs/reference/engines/table-engines/mergetree-family/replication) · [system.replicas](https://clickhouse.com/docs/reference/system-tables/replicas) · [Распределённый DDL](https://clickhouse.com/docs/sql-reference/distributed-ddl)

## Q21. Чем ClickHouse Keeper отличается от ZooKeeper?

**ClickHouse Keeper** — сервис координации на C++ с алгоритмом **Raft**. Apache ZooKeeper написан на Java и использует **ZAB**. По умолчанию в обоих сервисах записи линеаризуемы, а локальные чтения могут возвращать устаревшие данные. Эти гарантии относятся к данным координатора. Видимость вставок в таблицах ClickHouse определяется репликацией из Q20.

- **Развёртывание.** Keeper можно запустить отдельно или встроить в процесс ClickHouse. Для кворума из трёх участников по-прежнему нужны два доступных участника.
- **Совместимость.** Клиентский протокол совместим с ZooKeeper. Межсерверный протокол и форматы журналов и снимков различаются, поэтому смешанный ансамбль невозможен. Для переноса данных ZooKeeper документация предусматривает `clickhouse-keeper-converter`.
- **Ресурсы.** Keeper обходится без JVM. Задержку и потребление памяти сравнивают на своей нагрузке с учётом размера метаданных и конфигурации.

Для новых self-managed развёртываний с репликацией документация ClickHouse рекомендует Keeper.

[ClickHouse Keeper](https://clickhouse.com/docs/guides/oss/deployment-and-scaling/keeper)

## Q22. (!) Storage engine оптимизации (compression, sparse data)?

В `MergeTree` кодек выбирается для колонки. Результат зависит от типа данных, распределения значений и порядка строк. Меньший объём чтения помогает при ограничении по вводу-выводу, а распаковка расходует CPU.

- **LZ4 и ZSTD.** LZ4 используется по умолчанию в стандартной конфигурации self-managed ClickHouse, ZSTD — в ClickHouse Cloud. Задайте кодек явно, если сравниваете варианты. ZSTD часто сильнее сжимает данные, а стоимость сжатия и чтения проверяют на своей нагрузке.
- **Delta, DoubleDelta и Gorilla.** `Delta` кодирует разницы между соседними значениями, `DoubleDelta` — изменения этих разниц. Для временных меток с регулярным шагом это даёт компактное представление. `Gorilla` использует XOR соседних значений, например у плавно меняющихся показаний датчика. Такой кодек можно поставить перед ZSTD.
- **LowCardinality(String).** Словарь заменяет повторяющиеся строки компактными идентификаторами. Тип подходит, например, для нескольких статусов. При большом числе разных значений стоимость словаря может перекрыть выигрыш.
- **Разреженная сериализация.** Когда большинство значений колонки совпадает со значением по умолчанию для её типа, ClickHouse может сохранить остальные значения и сведения об их позициях. Настройка `ratio_of_defaults_for_sparse_serialization` задаёт порог выбора представления. Решение зависит от статистики колонки в части, поэтому разные части одной таблицы могут использовать разные представления.

```sql
CREATE TABLE metrics (
    ts DateTime CODEC(DoubleDelta, ZSTD),
    value Float64 CODEC(Gorilla, ZSTD),
    status LowCardinality(String),
    errors UInt32
) ENGINE = MergeTree()
ORDER BY ts
SETTINGS ratio_of_defaults_for_sparse_serialization = 0.9;
```

Если датчик присылает показания с регулярным шагом, сравните `DoubleDelta, ZSTD` для `ts` с одним `ZSTD`. Если `errors` почти всегда равен нулю, проверьте выигрыш от разреженного представления на частях с характерной долей нулей. Загружайте одинаковые данные с одинаковым ключом сортировки, сравнивайте `data_compressed_bytes` в `system.columns` и отдельно измеряйте время характерных вставок и запросов.

[Кодеки и измерение сжатия](https://clickhouse.com/blog/optimize-clickhouse-codecs-compression-schema) · [Разреженная сериализация](https://clickhouse.com/blog/whats-new-in-clickhouse-22-1) · [LowCardinality](https://clickhouse.com/docs/reference/data-types/lowcardinality)

## Q23. Что такое многоуровневое хранение (tiered storage, hot/cold)?

**Tiered storage** распределяет части одной таблицы по томам с разной стоимостью и характеристиками чтения. Например, в self-managed ClickHouse свежие события можно хранить на SSD, а старые переносить на том с объектным хранилищем. Запросы используют ту же таблицу. Задержка чтения с холодного тома зависит от хранилища, сети и кеша.

Фрагмент серверной конфигурации использует уже настроенные диски `fast_ssd` и `s3`:

```xml
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
```

```sql
CREATE TABLE events (
    event_time DateTime,
    event_id UInt64
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_time)
ORDER BY (event_time, event_id)
TTL event_time + INTERVAL 30 DAY TO VOLUME 'cold'
SETTINGS storage_policy = 'hot_cold';
```

- **Перенос по возрасту.** `TTL … TO VOLUME` переносит целую часть, когда срок наступил для всех её строк. Если в части есть события за 1 и 20 октября, TTL переноса определяется событием за 20 октября. Фоновый перенос может произойти позже этого срока. Для уже просроченных частей, формируемых при вставке, размещение зависит от `perform_ttl_move_on_insert` у целевого тома.
- **Перенос при нехватке места.** `move_factor` задаёт долю свободного места, ниже которой ClickHouse может перемещать части на следующий том. При таком переносе возраст данных сам по себе не ограничивает выбор части. В примере это может отправить данные на `cold` раньше 30 дней.
- **Проверка.** Сравните `disk_name` активных частей в `system.parts` до и после переноса. Чтобы оценить итоговую стоимость, измерьте задержку запросов к холодному диапазону и учтите операции и трафик объектного хранилища.

[TTL в ClickHouse](https://clickhouse.com/docs/guides/developer/ttl) · [MergeTree и политика хранения](https://clickhouse.com/docs/engines/table-engines/mergetree-family/mergetree) · [system.parts](https://clickhouse.com/docs/operations/system-tables/parts)

## Q24. (!) Kafka engine для ingestion?

**Kafka Engine** подключает ClickHouse к Kafka как потребителя. Таблица с этим движком читает и разбирает сообщения. Для постоянного хранения инкрементальное материализованное представление передаёт прочитанные блоки в отдельную таблицу MergeTree.

```sql
CREATE TABLE kafka_ingest_events (
    event_id UInt64,
    event_time DateTime,
    user_id UInt64,
    data String
) ENGINE = MergeTree()
ORDER BY (event_time, event_id);

CREATE TABLE events_kafka (
    event_id UInt64,
    event_time DateTime,
    user_id UInt64,
    data String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka:9092',
    kafka_topic_list = 'events',
    kafka_group_name = 'clickhouse-consumer',
    kafka_format = 'JSONEachRow';

CREATE MATERIALIZED VIEW events_consumer TO kafka_ingest_events
AS SELECT event_id, event_time, user_id, data FROM events_kafka;
```

Смещения потребителя определяют, откуда группа продолжит чтение. Запись блока в MergeTree и фиксация смещения в Kafka происходят отдельно. Если запись прошла, а смещение не зафиксировалось, блок может прийти повторно. Обычный MergeTree не обеспечивает уникальность `event_id`. Если отчёт должен учитывать событие один раз, заложите стабильный идентификатор и дедупликацию в модель хранения или запроса.

Прямой `SELECT` из Kafka-таблицы предназначен для диагностики. Он требует разрешения `stream_like_engine_allow_direct_select`, а фиксацией смещений при таком чтении управляет `kafka_commit_on_select`. Для отчётов и повторного чтения уже принятых данных используйте целевую MergeTree-таблицу. Фоновое потребление и чтение целевой таблицы можно проверять независимо, например через `system.kafka_consumers` и количество принятых событий.

**Несовместимое сообщение.** Если продюсер отправил значение, которое нельзя разобрать по схеме таблицы, `kafka_handle_error_mode = 'default'` приводит к исключению парсера. В режиме `stream` ошибку и исходное сообщение можно читать из виртуальных колонок `_error` и `_raw_message`. При таком режиме отделите ошибочные записи от нормального потока в запросах материализованных представлений. В режиме `dead_letter_queue` сведения об ошибке попадают в таблицу ClickHouse `system.dead_letter_queue`. Этот режим касается разбора сообщения. Для сбоя вставки в целевую таблицу нужна отдельная диагностика. [Настройки Kafka Engine](https://clickhouse.com/docs/reference/engines/table-engines/integrations/kafka)

Похожая схема с материализованным представлением применяется с RabbitMQ, NATS и S3Queue. Правила подтверждения и повтора зависят от конкретного движка.

[Kafka Engine](https://clickhouse.com/docs/engines/table-engines/integrations/kafka) · [system.kafka_consumers](https://clickhouse.com/docs/operations/system-tables/kafka_consumers)

## Q25. Какие ещё табличные движки есть (S3, MySQL, PostgreSQL)?

Интеграционный движок объявляет в ClickHouse таблицу, данные которой находятся во внешнем источнике. Табличная функция открывает тот же тип источника прямо в запросе. Например, `ENGINE = S3` подходит для именованной таблицы, которую читают регулярно, а `s3(...)` — для загрузки конкретного файла.

- **S3.** Читает объекты в поддерживаемых форматах, например Parquet и CSV. При записи создаёт объекты. Повторная вставка по существующему ключу по умолчанию завершится ошибкой. Для отдельных сценариев можно настроить создание нового файла или перезапись. MergeTree с диском S3 из Q23 хранит на нём собственные части и сохраняет семантику MergeTree.
- **MySQL и PostgreSQL.** Дают читать внешние таблицы и выполнять `INSERT`, если это позволяет конфигурация и права на стороне источника. Часть простых фильтров ClickHouse может передать удалённой СУБД. Например, сравнение одного столбца с константой может выполняться на источнике, тогда как агрегат над полученными строками вычисляется в ClickHouse. С ClickHouse 26.7 можно задать удалённый запрос как источник. Если объём передачи нужно ограничить, фильтр следует включить в этот запрос, поскольку внешний `WHERE` ClickHouse отработает после чтения его результата.
- **URL и File.** Позволяют обращаться к данным по HTTP или через файловую систему соответственно. Доступность операций записи зависит от источника и настроек.

Например, при разовой загрузке архива в уже созданную таблицу MergeTree можно явно сопоставить столбцы. В примере объект доступен ClickHouse, а типы соответствуют целевой таблице.

```sql
INSERT INTO events_archive (event_time, event_id)
SELECT event_time, event_id
FROM s3('https://example-bucket.s3.amazonaws.com/events/2026-10.parquet', 'Parquet');
```

При частых тяжёлых отчётах по внешней БД оценивайте объём передаваемых строк и нагрузку на источник. Локальная копия позволяет выполнять повторные сканы в ClickHouse. Для неё нужно определить период обновления и единицу повторной обработки. Например, при ежедневной загрузке Parquet сначала загрузите день в отдельную таблицу, проверьте результат и замените соответствующую дневную партицию через `REPLACE PARTITION … FROM`. Такой сценарий требует совместимых таблиц и полного снимка за день. Повторный запуск заменяет содержимое партиции, тогда как повторный `INSERT INTO … SELECT` может добавить те же события повторно. [Операции над партициями MergeTree](https://clickhouse.com/docs/sql-reference/statements/alter/partition)

[Движок S3](https://clickhouse.com/docs/engines/table-engines/integrations/s3) · [Движок MySQL](https://clickhouse.com/docs/engines/table-engines/integrations/mysql) · [Движок PostgreSQL](https://clickhouse.com/docs/engines/table-engines/integrations/postgresql)

## Q26. (!) Какие use cases ClickHouse в production?

ClickHouse подходит для потоков событий, которые поступают большими объёмами и затем анализируются по диапазонам, группам и нескольким измерениям. При таком запросе движок может читать только нужные колонки, а порядок сортировки помогает отсечь часть гранул.

- **Телеметрия.** Хранить логи, трейсы и метрики, искать ошибки сервиса за интервал, сравнивать задержки до и после релиза. Например, ClickStack принимает телеметрию OpenTelemetry и использует ClickHouse для хранения и запросов.
- **Продуктовая и рекламная аналитика.** Считать просмотры, конверсию и расходы по каналам. Для регулярно запрашиваемых агрегатов можно использовать материализованное представление, если допустима его модель обновления (см. Q17).
- **Аналитика заказов.** Хранить события смены статуса и считать процент отмен по региону и часу. Запись заказа и проверка допустимости перехода остаются в транзакционной БД. Свежесть отчёта зависит от задержки доставки и обработки событий.

[ClickHouse для observability](https://clickhouse.com/docs/use-cases/observability) · [MergeTree](https://clickhouse.com/docs/engines/table-engines/mergetree-family/mergetree)

## Q27. (!) Какие ограничения / минусы ClickHouse?

Ограничения заметнее всего в задачах с частыми точечными операциями, строгими инвариантами между строками и запросами, которые материализуют большие промежуточные результаты.

- **Точечные чтения и целостность.** Разреженный первичный индекс помогает пропускать гранулы и допускает повторяющиеся ключи. ClickHouse поддерживает проверки `CHECK`, а уникальность и ссылочную целостность для OLTP-инвариантов нужно обеспечивать отдельно.
- **Атомарность.** Для вставки в одну партицию таблицы семейства MergeTree она зависит от укладки данных в один блок. При вставке в несколько партиций охват атомарности ограничен отдельными партициями. В Distributed отсутствует общий коммит для нескольких шардов. На каждом шарде действуют гарантии его целевой таблицы с учётом разбиения на блоки и партиции (см. Q19). Для сценария «обновить заказ и зарезервировать остаток одной транзакцией» эти гарантии сами по себе недостаточны.
- **Изменения строк.** Мутации `ALTER TABLE … UPDATE/DELETE` обрабатывают затронутые части и могут создавать заметную нагрузку на фоновые операции. Есть и облегчённые операции. Например, `UPDATE` в поддерживаемой конфигурации пишет части с изменениями, которые учитываются при чтении. Частые изменения всё равно требуют оценки нагрузки на запись, чтение и слияния.
- **JOIN и агрегации.** Потребление памяти зависит от алгоритма, объёма промежуточных данных и настроек. Для тяжёлого JOIN проверьте план и применимость алгоритма с выгрузкой на диск (см. Q15).
- **Эксплуатация.** В собственном кластере нужно спланировать ключ шардирования, объём реплик и восстановление. Широкая таблица позволяет читать отдельные колонки, но запрос ко всем её полям теряет эту экономию.

[Гарантии вставок](https://clickhouse.com/docs/guides/developer/transactional) · [UPDATE](https://clickhouse.com/docs/sql-reference/statements/update) · [CREATE TABLE и CHECK](https://clickhouse.com/docs/sql-reference/statements/create/table)

## Q28. Common pitfalls в ClickHouse production?

Если задержки растут после подключения нового потока событий, сначала выясните, где возникла нагрузка: на создании частей, их слиянии или чтении данных.

- **Мелкие вставки и слишком много партиций.** Один сброс буфера может создать части сразу в нескольких партициях. Слияния идут внутри партиции, поэтому ключ с высокой кардинальностью усугубляет проблему. Например, если поток пишет сотни мелких пачек в минуту, проверьте динамику количества активных частей по партициям, затем укрупните пачки или настройте серверную буферизацию.
- **Подтверждение асинхронной вставки.** При `async_insert=1, wait_for_async_insert=1` клиент ждёт успешной записи при сбросе буфера и получает ошибку, если сброс завершился неуспешно. При `wait_for_async_insert=0` сервер отвечает после приёма в буфер. Сбой до сброса может привести к потере уже подтверждённых клиенту данных. Подтверждение сброса само по себе не задаёт гарантию кворума реплик.
- **Медленные запросы.** Если запрос читает много гранул, проверьте соответствие `ORDER BY` типовым фильтрам через `EXPLAIN indexes = 1`. Если память расходует JOIN, оцените выбранный алгоритм, размеры входов и возможность уменьшить их перед соединением (см. Q15).
- **Повтор после таймаута.** Если клиент не получил ответ, вставка могла успеть записаться. Для повтора той же пачки можно использовать тот же `insert_deduplication_token`, если дедупликация включена для запроса и таблица ещё хранит запись о первой попытке. Для новой пачки нужен новый токен. После выхода из окна дедупликации повтор может добавить данные снова. В ClickHouse 26.1 исправили дедупликацию асинхронных вставок в цепочке с зависимыми инкрементальными MV. Дедупликация вставок работает на уровне пачек и не обеспечивает уникальность отдельных `event_id` из Q24. [Дедупликация повторных вставок](https://clickhouse.com/docs/concepts/features/operations/insert/deduplicating-inserts-on-retries) · [ClickHouse 26.1](https://clickhouse.com/blog/clickhouse-release-26-01)
- **Фоновые изменения и хранение.** Частые тяжёлые мутации конкурируют за ресурсы со слияниями. Для удаления по сроку задайте TTL и отслеживайте запас места. Истечение TTL ещё не означает, что диск освободится в тот же момент. В `ReplacingMergeTree` фоновая дедупликация также не гарантирует, что обычный `SELECT` сразу вернёт одну версию (см. Q6).
- **Восстановление.** Репликация помогает при отказе узла, а восстановление после ошибочного удаления требует доступной копии нужного состояния. Для поддерживаемого движка настройте `BACKUP` за пределы домена отказа и проверьте `RESTORE` в отдельном окружении. Успешное создание копии подтверждает только этап резервирования.

Для первого шага в сценарии с мелкими вставками можно снять такой срез на проблемном узле и сравнить его с предыдущими замерами:

```sql
SELECT partition, count() AS active_parts, sum(rows) AS stored_rows
FROM system.parts
WHERE database = 'analytics' AND table = 'events' AND active
GROUP BY partition
ORDER BY active_parts DESC;
```

[Асинхронные вставки](https://clickhouse.com/docs/optimize/asynchronous-inserts) · [system.parts](https://clickhouse.com/docs/operations/system-tables/parts) · [Резервное копирование и восстановление](https://clickhouse.com/docs/operations/backup)

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
