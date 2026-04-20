---
title: "PostgreSQL: администрирование и обслуживание"
description: "Кратко: роли и права, резервное копирование, VACUUM/ANALYZE, автovacuum, базовые настройки и psql-команды."
tags:
  - databases
  - relational
  - postgres-admin
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# PostgreSQL: администрирование и обслуживание

Кратко: роли и права, резервное копирование, **VACUUM**/**ANALYZE**, автovacuum, базовые настройки и **psql**-команды.

## Полезные ссылки

### Официальная документация

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)

### Обучающие материалы

- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)


См. также: [[postgres-basics]] — [[postgres-indexes]] — [[postgres-transactions]].

## Содержание

- [PostgreSQL: администрирование и обслуживание](#postgresql-администрирование-и-обслуживание)
- [Роли и права](#роли-и-права)
- [Резервное копирование и восстановление](#резервное-копирование-и-восстановление)
- [VACUUM и ANALYZE](#vacuum-и-analyze)
  - [VACUUM — удаление мертвых версий строк](#vacuum-удаление-мертвых-версий-строк)
  - [ANALYZE — обновление статистики](#analyze-обновление-статистики)
  - [VACUUM FULL — полная очистка](#vacuum-full-полная-очистка)
- [Автовакуум](#автовакуум)
- [Общие настройки autovacuum](#общие-настройки-autovacuum)
- [Настройки для запуска vacuum](#настройки-для-запуска-vacuum)
- [Настройки для запуска analyze](#настройки-для-запуска-analyze)
- [Настройки производительности](#настройки-производительности)
  - [Раздувание базы данных (bloat)](#раздувание-базы-данных-bloat)
- [Быстрые настройки (старт)](#быстрые-настройки-старт)
- [Полезные команды psql](#полезные-команды-psql)
- [Безопасность: pg_hba.conf и SSL](#безопасность-pg_hbaconf-и-ssl)
- [Мониторинг расширениями](#мониторинг-расширениями)
- [Репликация (очень кратко)](#репликация-очень-кратко)

## Роли и права
- **Создать роль/пользователя:**
```sql
-- Создание роли и выдача прав на схему и таблицы
CREATE ROLE appuser LOGIN PASSWORD 'secret';
GRANT CONNECT ON DATABASE mydb TO appuser;
GRANT USAGE ON SCHEMA public TO appuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO appuser;
```
- Новые таблицы: `**ALTER DEFAULT PRIVILEGES** `IN` **SCHEMA public GRANT SELECT** `ON` **TABLES** `TO` **appuser**;`

## Резервное копирование и восстановление
- **Логический дамп:**
```bash
# Логический дамп и восстановление БД в custom-формате
pg_dump -Fc -d mydb -f mydb.dump
pg_restore -d mydb_restored mydb.dump
```
- Параллельный дамп (быстрее на крупных базах): `**pg_dump** -`j 4` -Fd -d **mydb** -f **dumpdir**`

## VACUUM и ANALYZE

### VACUUM — удаление мертвых версий строк

Раздувание базы данных (bloat) — это дисковое пространство, которое использовалось таблицей или индексом и доступно для повторного использования базой данных, но не было освобождено. Раздувание происходит при обновлении таблиц или индексов. Если у вас загруженная база данных с большим количеством операций удаления, раздувание может оставить много неиспользуемого пространства в вашей базе данных и повлиять на производительность, если его не убрать.

**Основная цель `VACUUM`:**
- Освободить место, занимаемое мертвыми версиями строк (dead tuples).
- Обновить статистику для планировщика запросов.
- Заморозить старые идентификаторы транзакций (предотвращение wraparound).

**Базовое использование `VACUUM`:**
```sql
-- VACUUM одной таблицы
VACUUM my_table;

-- VACUUM с обновлением статистики (ANALYZE)
VACUUM (ANALYZE) my_table;

-- VACUUM всей базы данных
VACUUM;

-- VACUUM с подробным выводом
VACUUM (VERBOSE) my_table;

-- VACUUM только с обновлением статистики (без очистки мертвых строк)
VACUUM ANALYZE my_table;
```

**Параметры `VACUUM`:**

- **`FULL` — полная очистка (блокирует таблицу):**
  ```sql
  VACUUM FULL my_table;
  ```
  - Переписывает таблицу, полностью удаляя мертвые строки.
  - Блокирует таблицу для записи (`ACCESS EXCLUSIVE`).
  - Используйте только при критическом раздувании и в окне обслуживания.

- **`FREEZE` — заморозка старых строк:**
  ```sql
  VACUUM FREEZE my_table;
  ```
  - Замораживает старые версии строк для предотвращения **wraparound**.

- **`VERBOSE` — подробный вывод:**
  ```sql
  VACUUM (VERBOSE) my_table;
  ```
  - Показывает детальную информацию о процессе очистки.

- **`ANALYZE` — обновление статистики:**
  ```sql
  VACUUM (ANALYZE) my_table;
  ```
  - Обновляет статистику для планировщика запросов.

**Статус `VACUUM` по таблицам:**
```sql
-- Таблицы с наибольшим числом мёртвых строк для приоритета VACUUM
SELECT relname,
       last_vacuum,
       last_autovacuum,
       n_dead_tup,
       n_live_tup
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC
LIMIT 20;
```

**Проверка необходимости `VACUUM`:**
```sql
-- Доля мёртвых строк по таблицам в процентах
SELECT schemaname,
       tablename,
       n_dead_tup,
       n_live_tup,
       round((n_dead_tup::numeric / nullif(n_live_tup + n_dead_tup, 0)) * 100, 2) AS dead_pct
FROM pg_stat_user_tables
WHERE n_live_tup + n_dead_tup > 0
ORDER BY dead_pct DESC
LIMIT 20;
```

Если `dead_pct` велик (>10-20%), рекомендуется запустить `VACUUM`.

### ANALYZE — обновление статистики

**ANALYZE** собирает статистику о распределении данных в таблицах, которая используется планировщиком запросов для выбора оптимального плана выполнения.

**Базовое использование `ANALYZE`:**
```sql
-- ANALYZE одной таблицы
ANALYZE my_table;

-- ANALYZE всей базы данных
ANALYZE;

-- ANALYZE конкретных столбцов
ANALYZE my_table (column1, column2);

-- ANALYZE с подробным выводом
ANALYZE VERBOSE my_table;
```

**Когда запускать `ANALYZE`:**
- После массовых изменений данных (INSERT, `UPDATE`, DELETE).
- После изменения структуры данных (ALTER TABLE).
- Если планировщик выбирает плохие планы (проверьте через EXPLAIN).

**Параметры статистики:**
```sql
-- Увеличить точность статистики для таблицы
ALTER TABLE my_table ALTER COLUMN my_column SET STATISTICS 1000;

-- По умолчанию default_statistics_target = 100
-- Чем выше значение, тем точнее статистика, но дольше сбор
```

### VACUUM FULL — полная очистка

**VACUUM FULL** переписывает таблицу полностью, удаляя все мертвые строки и сжимая таблицу до минимального размера.

**Важные предупреждения:**
- Блокирует таблицу для записи (`ACCESS EXCLUSIVE`).
- Может занять много времени для больших таблиц.
- Требует дополнительного места на диске (переписывает таблицу).

**Когда использовать `VACUUM FULL`:**
- Таблица критически раздута (мертвые строки >50%).
- У вас есть окно обслуживания.
- Обычный **VACUUM** не помогает.

**Использование:**
```sql
-- VACUUM FULL одной таблицы
VACUUM FULL my_table;

-- VACUUM FULL с подробным выводом
VACUUM (FULL, VERBOSE) my_table;
```

**Альтернатива `VACUUM FULL`:**
```sql
-- Создать новую таблицу и переписать данные
CREATE TABLE my_table_new (LIKE my_table INCLUDING ALL);
INSERT INTO my_table_new SELECT * FROM my_table;
ALTER TABLE my_table RENAME TO my_table_old;
ALTER TABLE my_table_new RENAME TO my_table;
DROP TABLE my_table_old;
```

Этот подход может быть быстрее для очень больших таблиц, но требует больше операций.

## Автовакуум

**PostgreSQL** поддерживает **AUTOVACUUM** — автоматический процесс очистки мертвых строк и обновления статистики. Автовакуум включен по умолчанию и критически важен для поддержания производительности базы данных.

**Зачем нужен autovacuum:**
- Автоматически удаляет мертвые версии строк (dead tuples).
- Автоматически обновляет статистику для планировщика.
- Предотвращает **wraparound** идентификаторов транзакций.
- Поддерживает производительность базы данных без ручного вмешательства.

**Проверка статуса autovacuum:**
```sql
-- Проверить, включен ли autovacuum
SHOW autovacuum;
-- Должно быть: on

-- Проверить настройки autovacuum
SHOW autovacuum_vacuum_scale_factor;
SHOW autovacuum_analyze_scale_factor;
SHOW autovacuum_vacuum_cost_limit;
```

**Статус последнего autovacuum по таблицам:**
```sql
SELECT relname,
       last_vacuum,
       last_autovacuum,
       last_autoanalyze,
       n_dead_tup,
       n_live_tup,
       round((n_dead_tup::numeric / nullif(n_live_tup + n_dead_tup, 0)) * 100, 2) AS dead_pct
FROM pg_stat_user_tables
WHERE n_live_tup > 0
ORDER BY dead_pct DESC
LIMIT 20;
```

**Важные параметры autovacuum в `postgresql.conf`:**

1. **autovacuum_vacuum_scale_factor** (по умолчанию: 0.2):
   - Доля мертвых строк от общего количества строк, при которой запускается **VACUUM**.
   - Например, `0.2` означает, что **VACUUM** запустится, когда мертвых строк будет 20% от общего количества.

2. **autovacuum_vacuum_threshold** (по умолчанию: 50):
   - Минимальное количество мертвых строк, при котором запускается **VACUUM**.
   - **VACUUM** запустится, если: `**n_dead_tup** > **autovacuum_vacuum_threshold** + **autovacuum_vacuum_scale_factor** * **n_live_tup**`.

3. **autovacuum_analyze_scale_factor** (по умолчанию: 0.1):
   - Доля измененных строк, при которой запускается **ANALYZE**.
   - Аналогично **vacuum_scale_factor**, но для обновления статистики.

4. **autovacuum_analyze_threshold** (по умолчанию: 50):
   - Минимальное количество измененных строк для запуска **ANALYZE**.

5. **autovacuum_vacuum_cost_limit** (по умолчанию: -1, означает использование vacuum_cost_limit):
   - Максимальная стоимость операций вакуума за период.
   - Контролирует влияние **autovacuum** на производительность.

6. **autovacuum_vacuum_cost_delay** (по умолчанию: 20ms):
   - Задержка между операциями вакуума при достижении **cost_limit**.
   - Помогает не мешать активным запросам.

**Настройка autovacuum для конкретной таблицы:**
```sql
-- Снизить threshold для "горячей" таблицы
ALTER TABLE hot_table SET (
  autovacuum_vacuum_scale_factor = 0.05,
  autovacuum_analyze_scale_factor = 0.02
);

-- Увеличить cost_limit для быстрой очистки
ALTER TABLE hot_table SET (
  autovacuum_vacuum_cost_limit = 2000
);
```

**Настройка autovacuum в `postgresql.conf`:**
```conf
# Общие настройки autovacuum
autovacuum = on
autovacuum_max_workers = 3  # Количество процессов autovacuum

# Настройки для запуска vacuum
autovacuum_vacuum_scale_factor = 0.2
autovacuum_vacuum_threshold = 50

# Настройки для запуска analyze
autovacuum_analyze_scale_factor = 0.1
autovacuum_analyze_threshold = 50

# Настройки производительности
autovacuum_vacuum_cost_limit = 200
autovacuum_vacuum_cost_delay = 20ms
```

**Проблемы с autovacuum:**

1. **Autovacuum не запускается:**
   - Проверьте, включен ли **autovacuum**: `**SHOW autovacuum**;`
   - Проверьте логи на ошибки.
   - Убедитесь, что нет долгих транзакций, блокирующих **autovacuum**.

2. **Autovacuum работает слишком медленно:**
   - Увеличьте `autovacuum_vacuum_cost_limit`.
   - Уменьшите `autovacuum_vacuum_cost_delay`.
   - Увеличьте `autovacuum_max_workers`.

3. **Autovacuum не справляется с нагрузкой:**
   - Снизьте `autovacuum_vacuum_scale_factor` для «горячих» таблиц.
   - Увеличьте `autovacuum_max_workers`.
   - Запускайте **VACUUM** вручную для критических таблиц.

**Мониторинг autovacuum:**
```sql
-- Активные процессы autovacuum
SELECT pid, datname, usename, application_name, state, query, query_start
FROM pg_stat_activity
WHERE query LIKE '%autovacuum%'
   OR query LIKE '%VACUUM%';

-- Статистика autovacuum по таблицам
SELECT schemaname, relname,
       last_vacuum,
       last_autovacuum,
       vacuum_count,
       autovacuum_count,
       last_autoanalyze,
       autoanalyze_count,
       n_dead_tup,
       n_live_tup
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC
LIMIT 20;
```

**Важные рекомендации:**
- Не отключайте **autovacuum** надолго! Это критически важно для производительности.
- Для «горячих» таблиц снижайте `autovacuum_vacuum_scale_factor` до `0.05` или ниже.
- Мониторьте `n_dead_tup` и запускайте **VACUUM** вручную при необходимости.
- Используйте `VACUUM FULL` только в окне обслуживания.

### Раздувание базы данных (bloat)

Раздувание базы данных — это дисковое пространство, которое использовалось таблицей или индексом и доступно для повторного использования базой данных, но не было освобождено.

**Причины раздувания:**
- Частые **UPDATE** — создают новые версии строк, старые становятся мертвыми.
- Массовые **DELETE** — оставляют мертвые строки.
- Отсутствие или недостаточная работа **autovacuum**.

**Оценка раздувания таблиц:**
```sql
-- Грубая оценка раздувания по статистике
SELECT schemaname,
       tablename,
       n_dead_tup,
       n_live_tup,
       round((n_dead_tup::numeric / nullif(n_live_tup + n_dead_tup, 0)) * 100, 2) AS dead_pct,
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size
FROM pg_stat_user_tables
WHERE n_live_tup + n_dead_tup > 0
ORDER BY dead_pct DESC
LIMIT 20;
```

**Более точная оценка раздувания (**требует расширения **pgstattuple**):**
```sql
-- Установить расширение (требует прав superuser)
CREATE EXTENSION IF NOT EXISTS pgstattuple;

-- Проверить раздувание таблицы
SELECT * FROM pgstattuple('my_table');

-- Результат покажет:
-- table_len: общий размер таблицы
-- tuple_count: количество живых строк
-- tuple_len: размер живых строк
-- dead_tuple_count: количество мертвых строк
-- dead_tuple_len: размер мертвых строк
-- free_space: свободное пространство
```

**Проверка раздувания индексов:**
```sql
-- Проверить размер индексов
SELECT schemaname,
       tablename,
       indexname,
       pg_size_pretty(pg_relation_size(indexrelid)) AS index_size,
       idx_scan,
       idx_tup_read,
       idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC
LIMIT 20;

-- Найти неиспользуемые индексы (кандидаты на удаление)
SELECT schemaname, relname, indexrelname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0
  AND schemaname <> 'pg_toast'
  AND schemaname <> 'pg_catalog'
ORDER BY pg_relation_size(indexrelid) DESC;
```

**Борьба с раздуванием:**

1. **Регулярный `VACUUM`:**
   - Убедитесь, что **autovacuum** работает правильно.
   - Для «горячих» таблиц снижайте `autovacuum_vacuum_scale_factor`.

2. **VACUUM `FULL` для критических случаев:**
   - Используйте только в окне обслуживания.
   - Убедитесь, что есть место на диске (требует ~2x размера таблицы).

3. **Пересоздание таблиц:**
   - Для очень больших таблиц может быть быстрее пересоздать таблицу.

4. **Партиционирование:**
   - Разделите большие таблицы на партиции для более эффективной очистки.

**Мониторинг раздувания:**
- Регулярно проверяйте `pg_stat_user_tables` на высокие значения `n_dead_tup`.
- Используйте расширение `pgstattuple` для точной оценки.
- Настройте алерты на критическое раздувание (>30-50%).


## Быстрые настройки (старт)
- Память: `shared_buffers` ~ 25% **RAM**; `work_mem` под ваши запросы/сортировки.
- **WAL**/сбросы: `checkpoint_timeout`, `max_wal_size`.
- Логи: `log_min_duration_statement` для поиска медленных запросов.
- Всегда проверяйте на тестовом стенде и с учётом нагрузки.

## Полезные команды psql
- `\l` — список баз; `\c db` — подключиться.
- `\dt` — таблицы; `\dn` — схемы; `\d **table**` — описание таблицы.
- `\i **file.sql**` — выполнить скрипт; `\**timing**` — показать время выполнения.

## Безопасность: pg_hba.conf и SSL
- В `pg_hba.conf` задавайте доступ по принципу наименьших прав; предпочитайте **scram-sha-256** (вместо **md5**).
- Включите **SSL**, если клиенты вне защищённой сети; выдайте серверный сертификат и **client cert** при необходимости.
- Закройте **superuser**-доступ из внешних сетей; создавайте роль с минимальными привилегиями для приложения.

## Мониторинг расширениями
- **`pg_stat_statements` — топ-стейтменты, среднее/макс время, планирование индексации:**
```sql
-- Подключение расширения и топ запросов по суммарному времени
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;
SELECT query, calls, total_time, rows
FROM pg_stat_statements
ORDER BY total_time DESC
LIMIT 20;
```
- **`auto_explain` — лог медленных планов:**
```conf
# Включение логирования планов запросов дольше 500 ms
shared_preload_libraries = 'auto_explain'
auto_explain.log_analyze = on
auto_explain.log_min_duration = '500ms'
```

## Репликация (очень кратко)
- **Для горячего **standby**:**
  - Включить **WAL**-архив/**replication**: `**wal_level** = **replica**`, `max_wal_senders`, `**hot_standby** = on`.
  - На **standby** выполнить `pg_ctl promote` (или соответствующую команду).
  - Следить за лагом: `pg_stat_replication`, поля `write_lag`, `flush_lag`, `replay_lag`.
- Для потоковой реплики не забывайте о бэкапах: реплика — не бэкап.

## См. также

- [[postgres-backup-restore|PostgreSQL: Резервное копирование и восстановление]]
- [[postgres-basics|PostgreSQL: Полное руководство по основам и мониторингу]]
- [[postgres-data-ops|PostgreSQL: операции с данными (CRUD)]]
- [[postgres-design|PostgreSQL: проектирование и нормализация]]
- [[postgres-extensions|PostgreSQL: Расширения]]
