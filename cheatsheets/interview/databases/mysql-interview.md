---
title: "Вопросы на собеседовании: MySQL"
description: "MySQL 8.4 для backend: InnoDB, индексы, планы, чтение, кодировки и диагностика соединений"
tags:
  - interview
  - databases
  - mysql-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "MySQL interview"
  - "MySQL на собеседовании"
prerequisites:
  - "[[sql-interview]]"
next: []
updated: "2026-10-05"
---
# Вопросы на собеседовании: MySQL

Версионная база ответов — **MySQL 8.4 LTS, InnoDB**. Общий SQL, уровни изоляции как теория и репликация разобраны в соседних шпаргалках; здесь — поведение и диагностика MySQL в backend-сервисе.

## Содержание

- [Q1. Что даёт InnoDB и что меняет выбор storage engine?](#q1-что-даёт-innodb-и-что-меняет-выбор-storage-engine)
- [Q2. Чем clustered index InnoDB отличается от secondary index?](#q2-чем-clustered-index-innodb-отличается-от-secondary-index)
- [Q3. Как читать EXPLAIN при медленном запросе MySQL?](#q3-как-читать-explain-при-медленном-запросе-mysql)
- [Q4. Почему обычный SELECT и SELECT FOR UPDATE могут видеть и блокировать данные по-разному?](#q4-почему-обычный-select-и-select-for-update-могут-видеть-и-блокировать-данные-по-разному)
- [Q5. Как расследовать Incorrect string value при записи Unicode?](#q5-как-расследовать-incorrect-string-value-при-записи-unicode)
- [Q6. Как расследовать Communications link failure после простоя?](#q6-как-расследовать-communications-link-failure-после-простоя)
- [See also](#see-also)

## Q1. Что даёт InnoDB и что меняет выбор storage engine?

MySQL позволяет выбирать движок таблицы; в MySQL 8.4 `InnoDB` — движок по умолчанию. Он поддерживает транзакции, внешние ключи, блокировки на уровне строк, MVCC и восстановление после сбоя. `redo log` участвует в восстановлении изменений, `undo log` — в откате и чтении версий строк. Это не отменяет резервное копирование и не делает произвольную внешнюю операцию частью SQL-транзакции.

Проверьте фактический `ENGINE` таблицы перед выводами о транзакционной семантике: `SHOW TABLE STATUS` или `SHOW CREATE TABLE`. Различия движков существенны, особенно при миграции старых таблиц. [InnoDB introduction](https://dev.mysql.com/doc/refman/8.4/en/innodb-introduction.html), [ACID model](https://dev.mysql.com/doc/refman/8.4/en/mysql-acid.html), [multi-versioning](https://dev.mysql.com/doc/refman/8.4/en/innodb-multi-versioning.html).

## Q2. Чем clustered index InnoDB отличается от secondary index?

Кластерный индекс хранит данные строк; если объявлен `PRIMARY KEY`, InnoDB использует его. Запись во вторичном индексе содержит индексируемые столбцы **и значения первичного ключа**, по которым при необходимости читается строка из кластерного индекса. Поэтому широкий первичный ключ увеличивает вторичные индексы, а поиск через secondary index может потребовать дополнительного перехода к строке.

Без `PRIMARY KEY` InnoDB выбирает подходящий `UNIQUE NOT NULL` индекс, а при его отсутствии создаёт скрытый кластерный индекс. Следовательно, «таблица без PK совсем не имеет clustered index» — ошибка. Подбирайте PK по идентичности данных и измеряйте план, а не меняйте его только ради одного запроса. [Clustered and secondary indexes](https://dev.mysql.com/doc/refman/8.4/en/innodb-index-types.html).

## Q3. Как читать EXPLAIN при медленном запросе MySQL?

Начните с реального SQL и параметров, затем `EXPLAIN` показывает **план оптимизатора**, а не гарантированное время исполнения. В табличном выводе изучите `type` (метод доступа), `possible_keys`, выбранный `key`, оценку `rows`, `filtered` и `Extra`; для нескольких таблиц проверьте порядок соединения. `key = NULL` не всегда ошибка: для небольшой таблицы scan может быть дешевле индекса. Сравнивайте план, распределение данных и фактическую задержку; не добавляйте индекс только потому, что он есть в `possible_keys`.

`EXPLAIN ANALYZE` в MySQL 8.4 **исполняет запрос** и показывает фактические измерения в формате TREE, поэтому безопасно применяйте его с учётом нагрузки и побочных эффектов DML. Обычный `EXPLAIN` прежде всего оценивает план. [Optimizing queries with EXPLAIN](https://dev.mysql.com/doc/refman/8.4/en/using-explain.html), [EXPLAIN statement](https://dev.mysql.com/doc/refman/8.4/en/explain.html).

## Q4. Почему обычный SELECT и SELECT FOR UPDATE могут видеть и блокировать данные по-разному?

Обычный `SELECT` в InnoDB обычно выполняет consistent nonlocking read через снимок MVCC. При стандартном для InnoDB `REPEATABLE READ` снимок создаётся первым consistent read в транзакции и используется последующими такими чтениями. `SELECT ... FOR UPDATE` — **locking read**: он читает актуальную для блокировки версию и ставит блокировки на найденные индексные записи; диапазон блокировок зависит от запроса, индекса и уровня изоляции. Не используйте его для «ускорения» обычного чтения.

Сценарий: сначала прочитали остаток обычным `SELECT`, затем собираетесь менять его в конкурентной транзакции. Для операции read-modify-write нужны корректные блокировки, атомарный `UPDATE` с условием или optimistic concurrency; повторное чтение снимка само по себе не резервирует строку. При долгой транзакции учитывайте задержку других writers. [Consistent nonlocking reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-consistent-read.html), [locking reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html), [isolation levels](https://dev.mysql.com/doc/refman/8.4/en/innodb-transaction-isolation-levels.html).

## Q5. Как расследовать Incorrect string value при записи Unicode?

Сообщение `Incorrect string value` требует проверить **всю цепочку кодировки**: байты, которые передал клиент; `character_set_client`, `character_set_connection`, `character_set_results`; charset конкретного столбца и ограничения длины. `utf8mb4` позволяет хранить четырёхбайтовые Unicode-символы, тогда как исторический MySQL `utf8mb3` их не покрывает. Collation определяет сравнение/сортировку; её смена сама по себе не исправит колонку с неподходящим charset.

Проверьте `SHOW CREATE TABLE`, `SHOW VARIABLES LIKE 'character_set_%'` и настройки драйвера/подключения. Исправление может потребовать согласованной миграции колонки/таблицы и параметров соединения с проверкой существующих данных. `SET NAMES` влияет на соединение, но не перекодирует уже сохранённый столбец. [Connection character sets](https://dev.mysql.com/doc/refman/8.4/en/charset-connection.html), [utf8mb4 character set](https://dev.mysql.com/doc/refman/8.4/en/charset-unicode-utf8mb4.html).

## Q6. Как расследовать Communications link failure после простоя?

Ошибка Connector/J описывает потерю связи с сервером, но **не доказывает одну причину**. Сопоставьте момент отказа с логами MySQL и сети: перезапуск/недоступность сервера, idle timeout (`wait_timeout`), firewall/load balancer, TLS и использование закрытого соединения из пула. Сравните путь нового соединения и уже взятого из пула; проверьте, сколько оно простаивало, конфигурацию его максимального срока жизни и проверку пригодности перед выдачей.

Настраивайте драйвер и пул вместе с сетевыми таймаутами и сервером. Blind retry **небезопасен** для записи: после обрыва ответ мог потеряться уже после commit, так что нужен известный исход или идемпотентная операция. Детали `maxLifetime` и `keepaliveTime` принадлежат конкретной реализации пула, а не MySQL SQL. [Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html), [server timeouts](https://dev.mysql.com/doc/refman/8.4/en/server-system-variables.html#sysvar_wait_timeout).

---

## See also

- [SQL](sql-interview.md) — общие запросы и индексы.
- [Транзакции БД](database-transactions-interview.md) — теория изоляции и конкуренции.
- [Репликация БД](database-replication-interview.md) — topology и consistency.
- [PostgreSQL](postgresql-interview.md) — отдельная реализация MVCC и планирования.
