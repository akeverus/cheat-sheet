---
title: "Oracle Database"
description: "Точка входа в Oracle Database: корпоративная реляционная СУБД с PL/SQL, RAC, Data Guard и широким набором enterprise-возможностей."
tags:
  - meta
  - index
  - databases
  - relational
  - oracle
type: "index"
updated: "2026-04-20"
---
# Oracle Database

Oracle Database — корпоративная реляционная СУБД, долгое время являющаяся стандартом в банках, телекоме, государственном секторе и крупных enterprise-системах. Сильные стороны: зрелость оптимизатора, PL/SQL, Real Application Clusters (RAC) для HA и масштабирования, Data Guard для DR, партиционирование, Advanced Security, глубокая инструментальная поддержка (AWR, ASH, SQL Tuning Advisor).

Для кого: инженеры, которые поддерживают legacy-системы на Oracle или проектируют миграции с/на Oracle. В новых проектах Oracle часто проигрывает по стоимости и порогу входа — выбирают PostgreSQL, Microsoft SQL Server или managed-решения в облаке. Но там, где уже живёт PL/SQL с миллионами строк, понимание Oracle остаётся обязательным навыком.

## Полезные ссылки

### Основные документы
- [[oracle-basics|Oracle Database: Основы]] — архитектура, установка, SQL, PL/SQL, интеграция с Java

### Соседние разделы
- [[README|Реляционные БД]]
- [PostgreSQL](../postgresql/)
- [MySQL](../mysql/)
- [[README|SQL Server]]
- [[README|Базы данных]]

### Внешние ресурсы
- [Oracle Database Documentation](https://docs.oracle.com/en/database/oracle/oracle-database/)
- [Oracle Docker Images (GitHub)](https://github.com/oracle/docker-images)
- [Oracle Learning Library](https://education.oracle.com/)
- [Ask TOM](https://asktom.oracle.com/) — Q&A от Oracle-ветеранов

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать Oracle](#когда-брать-oracle)
- [Oracle vs альтернативы](#oracle-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[[oracle-basics]] покрывает:

- Архитектуру Oracle: instance vs database, SGA/PGA, процессы, redo/undo, tablespaces
- Компоненты: listener, CDB/PDB (multitenant)
- Установку через Docker
- SQL-диалект Oracle: типы данных, sequences, constraints
- PL/SQL: процедуры, функции, триггеры, пакеты
- Базовую интеграцию с Java через JDBC
- Частые настройки и практики

## Когда брать Oracle

- Уже работаете с **legacy Oracle-системой**: миграция затратна, а выжать из существующей больше — реально.
- Нужны **enterprise-фичи уровня Oracle**: RAC, Data Guard, AWR, in-memory columnar, OLAP-опция.
- Жёсткие требования регулятора или существующий **контракт поддержки Oracle** с SLA.
- Глубокая **PL/SQL-логика** и DBA-экспертиза в команде.

**Когда не брать:**
- Новый продукт без историй с Oracle — PostgreSQL покрывает 90% задач без лицензионных затрат.
- Аналитика DWH — ClickHouse или Snowflake дешевле и быстрее.
- Облачно-нативный стек на микросервисах — managed Postgres / Aurora / Cloud SQL проще.

## Oracle vs альтернативы

| Критерий | Oracle | PostgreSQL | SQL Server | MySQL |
|----------|--------|------------|------------|-------|
| Лицензия | Коммерческая (дорогая) | Open source (PostgreSQL License) | Коммерческая + Express free | GPL / коммерческая |
| Процедурный язык | PL/SQL | PL/pgSQL, + многие | T-SQL | stored procedures (ограниченный) |
| HA / DR | RAC, Data Guard, GoldenGate | Patroni, pgBackRest, репликация | Always On AG, Log shipping | Group Replication, ProxySQL |
| Оптимизатор | Очень зрелый, CBO + hints | Отличный | Очень зрелый | Хороший, но слабее |
| Порог входа | Высокий (DBA-ориентированный) | Средний | Средний | Низкий |
| Лучшее применение | Сложный enterprise legacy, банки | Универсальный выбор | Windows-стеки, MS-экосистема | Простые web/OLTP-приложения |

## Маршруты чтения

- **Переход с PostgreSQL/MySQL на Oracle:** `oracle-basics.md` разделы про sequences, PL/SQL, tablespaces отличия SQL-диалекта.
- **Developer, который пишет SQL к Oracle:** SQL-диалект индексы оптимизация запросов трейсы (10046).
- **Junior DBA:** архитектура процессы redo/undo backup & recovery.

## Куда идти дальше

- Реляционные БД в целом — [[README|databases/relational/README.md]]
- PostgreSQL как open-source альтернатива — [../postgresql/](../postgresql/)
- SQL для всех — [databases/sql/](../../sql/)
- Hibernate / JPA — [[hibernate-interview|interview/databases/hibernate-interview.md]]
- Интервью по БД — [[sql-interview|interview/databases/sql-interview.md]]
