---
title: "SQL Server: Основы"
description: "Комплексное руководство по использованию Microsoft SQL Server — реляционной СУБД от Microsoft."
tags: ["databases", "relational", "sql-server-basics"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **SQL Server**: Основы

**Комплексное руководство по использованию **Microsoft SQL Server** — реляционной СУБД от **Microsoft**.**

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [SQL Server Documentation](https://docs.microsoft.com/en-us/sql/sql-server/) — официальная документация **Microsoft**

### См. также
- [Oracle](../oracle/oracle-basics.md) — **Oracle Database**
- [PostgreSQL](../postgresql/postgres-basics.md) — **PostgreSQL**

## Содержание

- [**SQL Server**: Основы](#sql-server-основы)
- [Введение в **SQL Server**](#введение-в-sql-server)
  - [Основные возможности](#основные-возможности)
- [Установка](#установка)
- [Docker установка](#docker-установка)
- [Основы **T-SQL**](#основы-t-sql)
- [Интеграция с **Java**](#интеграция-с-java)

## Введение в **SQL Server**

**Microsoft SQL Server** — коммерческая реляционная СУБД от **Microsoft**, широко используемая в **Windows**-окружениях.

### Основные возможности

- **T-SQL** язык программирования
- Интеграция с **.NET**
- Высокая производительность
- **Enterprise features**

## Установка

```bash
# Docker установка
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=StrongPassword123!" \
  -p 1433:1433 --name sqlserver \
  mcr.microsoft.com/mssql/server:2022-latest
```

## Основы **T-SQL**

```sql
-- Создание таблицы
CREATE TABLE users (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) NOT NULL,
    email NVARCHAR(100) UNIQUE,
    created_at DATETIME2 DEFAULT GETDATE()
);

-- T-SQL специфичные функции
SELECT 
    username,
    DATEDIFF(DAY, created_at, GETDATE()) AS days_since_creation
FROM users;
```

## Интеграция с **Java**

```java
// Использование Microsoft JDBC драйвера
Connection conn = DriverManager.getConnection(
    "jdbc:sqlserver://localhost:1433;databaseName=mydb",
    "sa", "password"
);
```


