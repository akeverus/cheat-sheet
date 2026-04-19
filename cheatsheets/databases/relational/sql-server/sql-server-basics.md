---
title: "SQL Server: Основы"
description: "Практическое руководство по Microsoft SQL Server: установка, T-SQL, типы данных, индексы, транзакции, JDBC/JDBC-интеграция, оптимизация и операционные задачи."
tags:
  - databases
  - relational
  - sql-server
difficulty: "intermediate"
updated: "2026-04-17"
---
# SQL Server: Основы

Microsoft SQL Server — коммерческая реляционная СУБД, доминирующая в .NET-экосистеме и Windows-энтерпрайзе, но с поддержкой Linux, Docker и macOS (через контейнер) начиная с версии 2017. Express-редакция бесплатна для dev и малых нагрузок (лимит 10 GB), Standard и Enterprise — для production.

Этот документ покрывает ежедневную работу: установка через Docker, базовый T-SQL, типы данных, индексы, транзакции и уровни изоляции, JDBC-подключение из Java, типовые задачи администратора и производительности. Для сравнения с другими СУБД — см. [PostgreSQL](../postgresql/), [Oracle](../oracle/), [MySQL](../mysql/).

## Полезные ссылки

### Официальная документация
- [SQL Server Documentation](https://learn.microsoft.com/en-us/sql/sql-server/)
- [T-SQL Reference](https://learn.microsoft.com/en-us/sql/t-sql/language-reference)
- [SQL Server Management Studio (SSMS)](https://learn.microsoft.com/en-us/sql/ssms/sql-server-management-studio-ssms)
- [Azure Data Studio](https://learn.microsoft.com/en-us/sql/azure-data-studio/)
- [Microsoft JDBC Driver](https://learn.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server)

### Инструменты
- [sqlcmd](https://learn.microsoft.com/en-us/sql/tools/sqlcmd/sqlcmd-utility) — CLI
- [bcp](https://learn.microsoft.com/en-us/sql/tools/bcp-utility) — bulk copy
- [DBATools (PowerShell)](https://dbatools.io/) — администрирование

### Соседние разделы
- [PostgreSQL](../postgresql/)
- [Oracle](../oracle/)
- [MySQL](../mysql/)
- [SQL](../../sql/)
- [ORM](../../orm/)

## Содержание

- [Редакции и лицензирование](#редакции-и-лицензирование)
- [Установка](#установка)
- [Подключение](#подключение)
- [Основы T-SQL](#основы-t-sql)
- [Типы данных](#типы-данных)
- [Индексы](#индексы)
- [Транзакции и изоляция](#транзакции-и-изоляция)
- [Представления, процедуры, функции](#представления-процедуры-функции)
- [Интеграция с Java](#интеграция-с-java)
- [Производительность и диагностика](#производительность-и-диагностика)
- [Бэкапы](#бэкапы)
- [High Availability](#high-availability)
- [Безопасность](#безопасность)
- [SQL Server vs альтернативы](#sql-server-vs-альтернативы)
- [Частые задачи](#частые-задачи)

## Редакции и лицензирование

| Редакция | Лимиты | Применение |
|----------|--------|-----------|
| Express | 10 GB БД, 1 GB RAM, 4 ядра | dev, edge, встраивание |
| Web | ограничения по HA | веб-хостинг |
| Standard | 128 GB RAM | mid-market |
| Enterprise | без лимитов, AlwaysOn, partitioning, TDE | prod-нагрузки |
| Developer | = Enterprise, бесплатно для non-prod | разработка |
| Azure SQL | managed в облаке | cloud-first |

## Установка

### Docker (Linux-хост, dev)

```bash
docker run -e "ACCEPT_EULA=Y" \
  -e "MSSQL_SA_PASSWORD=StrongPassword123!" \
  -p 1433:1433 \
  --name sqlserver \
  -d mcr.microsoft.com/mssql/server:2022-latest

# Проверить
docker exec -it sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "StrongPassword123!" -C \
  -Q "SELECT @@VERSION"
```

**Пароль sa** обязан содержать 8+ символов с заглавными/цифрами/спецсимволами — контейнер молча упадёт при слабом пароле.

### Windows

Загрузите [SQL Server Developer Edition](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) + SSMS. Для CI — [SQL Server on GitHub-hosted runners](https://learn.microsoft.com/en-us/sql/linux/quickstart-install-connect-ubuntu).

## Подключение

```bash
# sqlcmd
sqlcmd -S localhost,1433 -U sa -P 'StrongPassword123!' -C -d master

# Azure Data Studio — cross-platform GUI
# SSMS — Windows-only, самый богатый клиент
```

**JDBC connection string:**
```text
jdbc:sqlserver://localhost:1433;databaseName=mydb;encrypt=true;trustServerCertificate=true
```

## Основы T-SQL

T-SQL — диалект SQL в SQL Server, близок к ANSI, но с расширениями (CTE, `OUTPUT`, `MERGE`, оконные функции, переменные, процедурная логика).

```sql
-- Создание БД и таблицы
CREATE DATABASE shop;
GO
USE shop;
GO

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

-- Вставка с возвратом id
INSERT INTO users (username, email)
OUTPUT INSERTED.id
VALUES ('alice', 'alice@example.com');

-- Обновление со счётчиком
UPDATE TOP (100) orders
SET status = 'processing'
WHERE status = 'pending';

-- Временная таблица (local)
CREATE TABLE #tmp_ids (id INT);
INSERT INTO #tmp_ids VALUES (1), (2), (3);

-- Переменная таблица
DECLARE @batch TABLE (id INT, name NVARCHAR(50));

-- CTE
WITH top_customers AS (
    SELECT customer_id, SUM(amount) AS total
    FROM orders
    GROUP BY customer_id
    HAVING SUM(amount) > 10000
)
SELECT c.*, tc.total
FROM customers c
JOIN top_customers tc ON c.id = tc.customer_id;

-- Оконные функции
SELECT
    order_id,
    customer_id,
    amount,
    ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY created_at DESC) AS rn,
    SUM(amount) OVER (PARTITION BY customer_id) AS total_by_customer
FROM orders;

-- MERGE (upsert)
MERGE target AS t
USING source AS s ON t.id = s.id
WHEN MATCHED THEN UPDATE SET t.name = s.name
WHEN NOT MATCHED THEN INSERT (id, name) VALUES (s.id, s.name)
WHEN NOT MATCHED BY SOURCE THEN DELETE;
```

## Типы данных

| Категория | Тип | Примечание |
|-----------|-----|------------|
| Целые | `BIT`, `TINYINT`, `SMALLINT`, `INT`, `BIGINT` | IDENTITY только на INT/BIGINT |
| Точные | `DECIMAL(p,s)`, `NUMERIC(p,s)` | для денег |
| Приближённые | `REAL`, `FLOAT(n)` | избегайте для денег |
| Деньги | `MONEY`, `SMALLMONEY` | предпочтительно `DECIMAL(19,4)` |
| Строки | `CHAR`, `VARCHAR`, `NVARCHAR` (UTF-16) | `NVARCHAR` для Unicode |
| Большие объекты | `VARCHAR(MAX)`, `NVARCHAR(MAX)`, `VARBINARY(MAX)` | до 2 GB |
| Дата/время | `DATE`, `TIME`, `DATETIME2`, `DATETIMEOFFSET` | `DATETIME2` рекомендуется (vs legacy `DATETIME`) |
| Прочее | `UNIQUEIDENTIFIER`, `XML`, `JSON` (через `NVARCHAR`), `HIERARCHYID`, `GEOMETRY`, `GEOGRAPHY` | — |

**Унисимвольные строки:** предпочитайте `NVARCHAR` — меньше головной боли с Cyrillic/CJK. Начиная с 2019 — поддержка UTF-8 через `COLLATE ..._UTF8`.

## Индексы

```sql
-- B-Tree (clustered — физический порядок таблицы)
CREATE CLUSTERED INDEX idx_orders_created_at ON orders(created_at);

-- Non-clustered
CREATE NONCLUSTERED INDEX idx_users_email ON users(email);

-- Включённые столбцы (covering)
CREATE INDEX idx_orders_status
ON orders(status)
INCLUDE (customer_id, total);

-- Фильтрованный индекс (partial)
CREATE INDEX idx_orders_active
ON orders(id)
WHERE status = 'active';

-- Unique
CREATE UNIQUE INDEX idx_users_username ON users(username);

-- Columnstore (для аналитики, SQL Server 2012+)
CREATE CLUSTERED COLUSTORE INDEX idx_facts_cs ON facts;
```

**Правила:**
- У таблицы **один** clustered index (обычно PK).
- Nonclustered содержит ключевые столбцы + указатель на clustered.
- `INCLUDE` для covering-индексов без блоута ключа.
- Фильтрованные индексы — для разреженных условий.
- Columnstore — огромный прирост на аналитических запросах, но плохо для OLTP.

## Транзакции и изоляция

```sql
BEGIN TRAN;

UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

IF @@ERROR <> 0
    ROLLBACK
ELSE
    COMMIT;
```

**Уровни изоляции (от низкого к высокому):**

| Уровень | Грязное чтение | Неповторяемое | Фантомы | Блокировки |
|---------|---------------|---------------|---------|------------|
| READ UNCOMMITTED | ✅ | ✅ | ✅ | минимальные |
| READ COMMITTED (default) | ❌ | ✅ | ✅ | shared при чтении |
| REPEATABLE READ | ❌ | ❌ | ✅ | shared до конца tx |
| SERIALIZABLE | ❌ | ❌ | ❌ | range locks |
| SNAPSHOT (MVCC) | ❌ | ❌ | ❌ | без блокировок, row-versioning |

Включите `READ_COMMITTED_SNAPSHOT` и/или `ALLOW_SNAPSHOT_ISOLATION` на БД для MVCC-поведения, как в PostgreSQL.

```sql
ALTER DATABASE shop SET READ_COMMITTED_SNAPSHOT ON;
ALTER DATABASE shop SET ALLOW_SNAPSHOT_ISOLATION ON;
```

**Hints:**

```sql
SELECT * FROM orders WITH (NOLOCK);          -- = READ UNCOMMITTED, опасно
SELECT * FROM orders WITH (UPDLOCK, ROWLOCK); -- pessimistic lock
```

## Представления, процедуры, функции

```sql
-- View
CREATE VIEW active_users AS
SELECT * FROM users WHERE status = 1;

-- Stored procedure
CREATE OR ALTER PROCEDURE usp_upsert_user
    @username NVARCHAR(50),
    @email NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    MERGE users AS t
    USING (SELECT @username AS username, @email AS email) AS s
    ON t.username = s.username
    WHEN MATCHED THEN UPDATE SET email = s.email
    WHEN NOT MATCHED THEN INSERT (username, email) VALUES (s.username, s.email);
END;

EXEC usp_upsert_user @username = 'bob', @email = 'bob@example.com';

-- Scalar UDF (использовать осторожно — inlining с 2019 улучшил перф)
CREATE OR ALTER FUNCTION fn_full_name(@first NVARCHAR(50), @last NVARCHAR(50))
RETURNS NVARCHAR(101) AS
BEGIN
    RETURN @first + ' ' + @last;
END;

-- Table-valued UDF (inline — быстрый)
CREATE OR ALTER FUNCTION fn_orders_in_range(@from DATETIME2, @to DATETIME2)
RETURNS TABLE AS
RETURN (SELECT * FROM orders WHERE created_at BETWEEN @from AND @to);

-- Trigger
CREATE OR ALTER TRIGGER trg_orders_audit ON orders
AFTER INSERT, UPDATE AS
BEGIN
    INSERT INTO orders_audit(order_id, changed_at)
    SELECT id, SYSUTCDATETIME() FROM inserted;
END;
```

## Интеграция с Java

```java
// Gradle
// implementation 'com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11'

String url = "jdbc:sqlserver://localhost:1433;databaseName=shop;encrypt=true;trustServerCertificate=true";
try (Connection c = DriverManager.getConnection(url, "sa", "StrongPassword123!")) {
    try (PreparedStatement ps = c.prepareStatement(
            "INSERT INTO users(username, email) OUTPUT INSERTED.id VALUES (?, ?)")) {
        ps.setString(1, "alice");
        ps.setString(2, "alice@example.com");
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.println("id=" + rs.getLong(1));
            }
        }
    }
}
```

**Spring Boot (`application.yml`):**

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=shop;encrypt=true;trustServerCertificate=true
    username: sa
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    hikari:
      maximum-pool-size: 20
  jpa:
    database-platform: org.hibernate.dialect.SQLServerDialect
```

## Производительность и диагностика

```sql
-- Активные запросы
SELECT session_id, status, command, wait_type, blocking_session_id,
       cpu_time, total_elapsed_time, text
FROM sys.dm_exec_requests r
CROSS APPLY sys.dm_exec_sql_text(r.sql_handle)
WHERE session_id > 50;

-- Топ-10 медленных запросов
SELECT TOP 10
    qs.total_elapsed_time / qs.execution_count AS avg_ms,
    qs.execution_count,
    SUBSTRING(st.text, qs.statement_start_offset/2 + 1, 500) AS query
FROM sys.dm_exec_query_stats qs
CROSS APPLY sys.dm_exec_sql_text(qs.sql_handle) AS st
ORDER BY avg_ms DESC;

-- Index usage
SELECT OBJECT_NAME(s.object_id) AS tbl, i.name, s.user_seeks, s.user_scans, s.user_updates
FROM sys.dm_db_index_usage_stats s
JOIN sys.indexes i ON s.object_id = i.object_id AND s.index_id = i.index_id
WHERE s.database_id = DB_ID();

-- Missing indexes (по следам оптимизатора)
SELECT * FROM sys.dm_db_missing_index_details;

-- План запроса
SET STATISTICS IO, TIME ON;
-- ваш запрос
SET STATISTICS IO, TIME OFF;
-- или: Ctrl+M в SSMS для Actual Execution Plan
```

## Бэкапы

```sql
-- Полный бэкап
BACKUP DATABASE shop TO DISK = '/var/opt/mssql/backup/shop_full.bak'
WITH COMPRESSION, CHECKSUM, INIT;

-- Differential
BACKUP DATABASE shop TO DISK = '/var/opt/mssql/backup/shop_diff.bak'
WITH DIFFERENTIAL, COMPRESSION;

-- Transaction log
BACKUP LOG shop TO DISK = '/var/opt/mssql/backup/shop_log.trn';

-- Восстановление
RESTORE DATABASE shop FROM DISK = '/var/opt/mssql/backup/shop_full.bak'
WITH REPLACE, NORECOVERY;
RESTORE LOG shop FROM DISK = '/var/opt/mssql/backup/shop_log.trn'
WITH RECOVERY;
```

**Recovery models:**
- SIMPLE — только full/diff, без PITR.
- FULL — full + log, PITR, но нужно регулярно бэкапить лог.
- BULK_LOGGED — компромисс для bulk-операций.

## High Availability

| Решение | Уровень | Особенности |
|---------|---------|-------------|
| Always On Availability Groups | instance + БД | до 8 реплик, sync/async, read-only secondary |
| Always On Failover Cluster Instance | instance | shared storage, failover всего экземпляра |
| Database Mirroring (legacy) | БД | deprecated, использовать AG |
| Log Shipping | БД | периодический apply логов, простой DR |
| Replication | table-level | merge/transactional/snapshot |

## Безопасность

```sql
-- Login (уровень сервера) и user (уровень БД)
CREATE LOGIN app_user WITH PASSWORD = 'S3cr3t!';
USE shop;
CREATE USER app_user FOR LOGIN app_user;
GRANT SELECT, INSERT, UPDATE ON SCHEMA::dbo TO app_user;

-- Role
CREATE ROLE readonly_role;
GRANT SELECT ON SCHEMA::dbo TO readonly_role;
ALTER ROLE readonly_role ADD MEMBER app_user;

-- Row-Level Security
CREATE FUNCTION dbo.fn_user_filter(@user NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS allowed WHERE @user = USER_NAME();

CREATE SECURITY POLICY orders_policy
ADD FILTER PREDICATE dbo.fn_user_filter(owner) ON dbo.orders
WITH (STATE = ON);

-- TDE (Enterprise)
USE master;
CREATE MASTER KEY ENCRYPTION BY PASSWORD = '...';
CREATE CERTIFICATE tde_cert WITH SUBJECT = 'TDE';
USE shop;
CREATE DATABASE ENCRYPTION KEY WITH ALGORITHM = AES_256 ENCRYPTION BY SERVER CERTIFICATE tde_cert;
ALTER DATABASE shop SET ENCRYPTION ON;

-- Always Encrypted (client-side)
-- column-level, ключ у клиента, сервер не знает plaintext
```

## SQL Server vs альтернативы

| Критерий | SQL Server | PostgreSQL | Oracle | MySQL |
|----------|-----------|-----------|--------|-------|
| Лицензия | коммерческая (Express бесплатно) | open-source | коммерческая | dual (GPL/commercial) |
| MVCC | RCSI/SNAPSHOT | нативно | нативно | InnoDB (undo log) |
| JSON | `JSON` функции над NVARCHAR | JSONB (binary, indexable) | JSON data type | JSON |
| Partitioning | Enterprise | open-source | Enterprise | InnoDB/RocksDB |
| Cloud-native | Azure SQL, managed | RDS, Aurora, Cloud SQL | OCI, RDS | RDS, Aurora |
| Tooling | SSMS, Azure Data Studio | pgAdmin, DataGrip | SQL Developer | Workbench, DataGrip |
| Экосистема | .NET-тяжеловес, BI, SSIS/SSRS/SSAS | стандарт в стартапах | enterprise ERP/финсектор | веб, PHP-стек |

**Когда выбирать SQL Server:** legacy Windows-энтерпрайз, .NET-стек, Azure-first стратегия, сильная BI-составляющая (SSRS/SSAS), требования по Power BI интеграции.

## Частые задачи

| Задача | Команда |
|--------|---------|
| Размер БД | `sp_spaceused` или `sys.master_files` |
| Последний backup | `msdb.dbo.backupset` |
| Активные блокировки | `sys.dm_tran_locks`, `sys.dm_os_waiting_tasks` |
| Restart service | `systemctl restart mssql-server` |
| Смена collation БД | `ALTER DATABASE shop COLLATE Cyrillic_General_CI_AS` |
| Получить план запроса | `SET SHOWPLAN_XML ON` или Ctrl+M в SSMS |
| Compact/rebuild index | `ALTER INDEX ALL ON tbl REBUILD` |
| Статистики | `UPDATE STATISTICS tbl WITH FULLSCAN` |
| Текущая версия | `SELECT @@VERSION` |
| Включить MVCC | `ALTER DATABASE shop SET READ_COMMITTED_SNAPSHOT ON` |
