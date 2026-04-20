---
title: "Microsoft SQL Server"
description: "Точка входа в SQL Server: реляционная СУБД Microsoft с T-SQL, тесной интеграцией в .NET-стек и enterprise-возможностями."
tags:
  - meta
  - index
  - databases
  - relational
  - sql-server
type: "index"
updated: "2026-04-17"
---
# Microsoft SQL Server

Microsoft SQL Server — коммерческая реляционная СУБД от Microsoft. Исторически стандарт в Windows-окружениях и .NET-стеке, но с 2017 года полноценно работает на Linux и в Docker. Ядро: движок Database Engine, язык T-SQL, SQL Server Agent для job-ов, Always On Availability Groups для HA/DR, Columnstore индексы для аналитики, SSIS/SSAS/SSRS для BI.

Для кого: команды в Microsoft-экосистеме (ASP.NET, Dynamics, Power BI), миграции с Oracle в корпоративных средах, проекты где важны интеграция с Active Directory, SSRS-отчёты, гибрид OLTP + columnstore. В новых cloud-native проектах чаще выбирают PostgreSQL; SQL Server остаётся сильным, когда есть завязка на Microsoft-стек или готовые enterprise-фичи.

## Полезные ссылки

### Основные документы
- [[sql-server-basics|SQL Server: Основы]] — архитектура, установка, T-SQL, интеграция с Java

### Соседние разделы
- [[README|Реляционные БД]]
- [[README|Oracle]]
- [PostgreSQL](../postgresql/)
- [MySQL](../mysql/)
- [SQL-справочники](../../sql/)

### Внешние ресурсы
- [SQL Server Documentation](https://learn.microsoft.com/en-us/sql/sql-server/)
- [T-SQL Reference](https://learn.microsoft.com/en-us/sql/t-sql/)
- [SQL Server on Linux](https://learn.microsoft.com/en-us/sql/linux/)
- [Brent Ozar blog](https://www.brentozar.com/) — практические советы по SQL Server

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать SQL Server](#когда-брать-sql-server)
- [SQL Server vs альтернативы](#sql-server-vs-альтернативы)
- [Что стоит покрыть дополнительно](#что-стоит-покрыть-дополнительно)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[[sql-server-basics]] покрывает:

- Основные возможности SQL Server
- Установку (включая Docker)
- Базовый T-SQL
- Подключение из Java

Файл компактный — это стартовая точка, а не полный справочник.

## Когда брать SQL Server

- Приложение работает в **.NET / ASP.NET**-экосистеме, уже есть Entity Framework, хочется минимум трения.
- Нужны **enterprise BI-инструменты** Microsoft: SSIS (ETL), SSAS (cubes), SSRS (reports), Power BI.
- Требуется интеграция с **Active Directory / Windows Authentication**.
- Гибрид OLTP + аналитика через **Columnstore** без отдельной аналитической БД.

**Когда не брать:**
- Linux/open-source стек по умолчанию — возьмите PostgreSQL.
- Бюджет ограничен — лицензии SQL Server недёшевы (хотя Express бесплатен, но с лимитами).
- Cloud-native: managed Postgres / Aurora часто выгоднее Azure SQL, если нет привязки к Microsoft.

## SQL Server vs альтернативы

| Критерий | SQL Server | PostgreSQL | Oracle | MySQL |
|----------|------------|------------|--------|-------|
| Процедурный язык | T-SQL | PL/pgSQL + многие | PL/SQL | Ограниченный |
| HA/DR | Always On AG, Log shipping | Patroni, репликация, pgBackRest | RAC, Data Guard | Group Replication |
| Аналитика in-engine | Columnstore indexes | Parallel query, расширения | In-memory columnar (опция) | Частично |
| Интеграция с .NET | Идеальная (EF) | Хорошая (Npgsql) | Средняя (ODP.NET) | Средняя |
| Лицензия | Коммерческая, Express free (с лимитами) | Open source | Коммерческая (дорого) | GPL + коммерческая |
| Лучшее применение | MS-стек, корп. BI | Универсальный выбор | Тяжелый enterprise legacy | Простые web OLTP |

## Что стоит покрыть дополнительно

Базовый файл короткий (~80 строк). Если раздел будет развиваться, разумно добавить:

- `sql-server-tsql.md` — диалект T-SQL, хранимые процедуры, CTE, window functions, MERGE.
- `sql-server-performance.md` — индексы, статистика, execution plan, hints.
- `sql-server-ha.md` — Always On Availability Groups, Failover Cluster Instance.
- `sql-server-backup.md` — full/differential/log, point-in-time restore.
- `sql-server-security.md` — логины, роли, TDE, Always Encrypted.

## Маршруты чтения

- **Быстрый старт (1 час):** `sql-server-basics.md` → поднять контейнер через Docker → первый T-SQL запрос.
- **Миграция с другой СУБД:** `sql-server-basics.md` → сравнение T-SQL с PL/SQL / PL/pgSQL → тестовая миграция схемы.
- **Backend-разработчик на Java:** основы → JDBC (mssql-jdbc) → интеграция с Spring Data JPA.

## Куда идти дальше

- Реляционные БД в целом — [[README|databases/relational/README.md]]
- SQL-справочники — [databases/sql/](../../sql/)
- Альтернативные СУБД — [PostgreSQL](../postgresql/), [[README|Oracle]]
- Hibernate / JPA — [[hibernate-interview|interview/databases/hibernate-interview.md]]
- Интервью по SQL — [[sql-interview|interview/databases/sql-interview.md]]
