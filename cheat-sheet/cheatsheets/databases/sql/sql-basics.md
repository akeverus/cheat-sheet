# SQL: Основы

**Комплексное руководство по SQL (Structured Query Language) — стандартному языку для работы с реляционными базами данных.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [SQL Standard](https://www.iso.org/standard/76583.html) - ISO/IEC 9075 SQL стандарт

### См. также
- `../relational/postgresql/postgres-queries.md` - PostgreSQL запросы
- `../relational/mysql/mysql-queries.md` - MySQL запросы

## Содержание

- [Введение в SQL](#введение-в-sql)
- [DDL (Data Definition Language)](#ddl-data-definition-language)
- [DML (Data Manipulation Language)](#dml-data-manipulation-language)
- [DQL (Data Query Language)](#dql-data-query-language)
- [Транзакции](#транзакции)
- [Интеграция с Java](#интеграция-с-java)

## Введение в SQL

**SQL (Structured Query Language)** — декларативный язык программирования для работы с реляционными базами данных.

### Основные категории SQL команд

- **DDL** — CREATE, ALTER, DROP
- **DML** — INSERT, UPDATE, DELETE
- **DQL** — SELECT
- **DCL** — GRANT, REVOKE
- **TCL** — COMMIT, ROLLBACK

## DDL (Data Definition Language)

```sql
/**
 * DDL команды для создания и изменения структуры БД
 */

-- Создание базы данных
CREATE DATABASE myapp;

-- Создание таблицы
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Изменение таблицы
ALTER TABLE users ADD COLUMN balance DECIMAL(10, 2) DEFAULT 0;

-- Удаление таблицы
DROP TABLE users;
```

## DML (Data Manipulation Language)

```sql
/**
 * DML команды для работы с данными
 */

-- INSERT
INSERT INTO users (username, email) VALUES ('john', 'john@example.com');

-- UPDATE
UPDATE users SET email = 'newemail@example.com' WHERE id = 1;

-- DELETE
DELETE FROM users WHERE id = 1;
```

## DQL (Data Query Language)

```sql
/**
 * SELECT запросы для выборки данных
 */

-- Простой SELECT
SELECT * FROM users;

-- SELECT с условиями
SELECT username, email FROM users WHERE balance > 1000;

-- SELECT с JOIN
SELECT u.username, o.order_id, o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id;

-- Агрегация
SELECT COUNT(*) as total_users, AVG(balance) as avg_balance
FROM users;
```

## Транзакции

```sql
/**
 * Управление транзакциями в SQL
 */

-- Начало транзакции
START TRANSACTION;

-- Операции
INSERT INTO users (username, email) VALUES ('user1', 'user1@example.com');
INSERT INTO orders (user_id, total_amount) VALUES (LAST_INSERT_ID(), 100.50);

-- Подтверждение
COMMIT;

-- Или откат
ROLLBACK;
```

## Интеграция с Java

```java
/**
 * Использование SQL через JDBC
 */
public class SQLExample {
    public static void executeQuery(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                System.out.println(rs.getString("username"));
            }
        }
    }
}
```

---

*Обновлено: 2026-01-25*
