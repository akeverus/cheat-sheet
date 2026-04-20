---
title: "MySQL: Запросы и оптимизация SQL — Полное руководство по SQL в MySQL"
description: "Комплексное руководство по SQL запросам в MySQL: базовые и продвинутые конструкции, оптимизация, аналитические функции и best practices."
tags:
  - databases
  - relational
  - mysql-queries
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# MySQL: Запросы и оптимизация SQL — Полное руководство по SQL в MySQL

Комплексное руководство по **SQL** запросам в **MySQL**: базовые и продвинутые конструкции, оптимизация, аналитические функции и **best practices**.

## Полезные ссылки

### Официальная документация
- [SQL Statements (MySQL)](https://dev.mysql.com/doc/refman/8.0/en/sql-statements.html)
- [SELECT Syntax](https://dev.mysql.com/doc/refman/8.0/en/select.html)
- [JOIN Syntax](https://dev.mysql.com/doc/refman/8.0/en/join.html)

### Оптимизация запросов
- [Query Optimization](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
- [EXPLAIN Output](https://dev.mysql.com/doc/refman/8.0/en/explain-output.html)
- [Index Usage](https://dev.mysql.com/doc/refman/8.0/en/mysql-indexes.html)

### Инструменты
- [MySQL Workbench](https://dev.mysql.com/doc/workbench/en/)
- [Percona Toolkit](https://docs.percona.com/percona-toolkit/)
- [pt-query-digest](https://docs.percona.com/percona-toolkit/pt-query-digest.html)

### См. также
- [mysql-basics.md](mysql-basics.md) — основы **MySQL**
- [mysql-design.md](mysql-design.md) — проектирование баз данных
- [mysql-indexes.md](mysql-indexes.md) — индексы и производительность
- [PostgreSQL](../postgresql/postgres-queries.md) — сравнение с **PostgreSQL**

## Содержание

- [Базовые SELECT запросы](#базовые-select-запросы)
  - [Простые запросы](#простые-запросы)
    - [SELECT с условиями](#select-с-условиями)
    - [DISTINCT и LIMIT](#distinct-и-limit)
  - [Работа с NULL значениями](#работа-с-null-значениями)
  - [Запросы в Java](#запросы-в-java)
- [JOIN операции](#join-операции)
  - [INNER JOIN](#inner-join)
  - [LEFT/RIGHT JOIN](#leftright-join)
  - [CROSS JOIN и SELF JOIN](#cross-join-и-self-join)
  - [Оптимизация JOIN](#оптимизация-join)
  - [JOIN в Java](#join-в-java)
- [WHERE условия и операторы](#where-условия-и-операторы)
  - [Основные операторы сравнения](#основные-операторы-сравнения)
  - [Логические операторы](#логические-операторы)
  - [Специальные операторы](#специальные-операторы)
  - [Условия в Java](#условия-в-java)
- [Сортировка и группировка](#сортировка-и-группировка)
  - [ORDER BY](#order-by)
  - [GROUP BY](#group-by)
  - [HAVING](#having)
  - [ROLLUP и CUBE](#rollup-и-cube)
  - [Сортировка и группировка в Java](#сортировка-и-группировка-в-java)
- [Агрегатные функции](#агрегатные-функции)
  - [Основные агрегатные функции](#основные-агрегатные-функции)
  - [Продвинутые агрегатные функции](#продвинутые-агрегатные-функции)
  - [Window Functions](#window-functions)
  - [Агрегатные функции в Java](#агрегатные-функции-в-java)
- [Подзапросы](#подзапросы)
  - [Скалярные подзапросы](#скалярные-подзапросы)
  - [Коррелированные подзапросы](#коррелированные-подзапросы)
  - [Подзапросы в FROM](#подзапросы-в-from)
  - [Подзапросы в Java](#подзапросы-в-java)
- [UNION и EXCEPT](#union-и-except)
  - [UNION и UNION ALL](#union-и-union-all)
  - [INTERSECT и EXCEPT/MINUS](#intersect-и-exceptminus)
  - [Практические примеры UNION](#практические-примеры-union)
  - [UNION в Java](#union-в-java)
- [Window Functions](#window-functions-1)
  - [Основные оконные функции](#основные-оконные-функции)
  - [Продвинутые оконные функции](#продвинутые-оконные-функции)
  - [Аналитические функции](#аналитические-функции)
  - [Window Functions в Java](#window-functions-в-java)
- [Common Table Expressions (CTE)](#common-table-expressions-cte)
  - [Рекурсивные CTE](#рекурсивные-cte)
  - [Не рекурсивные CTE](#не-рекурсивные-cte)
  - [CTE в Java](#cte-в-java)
- [Рекурсивные запросы](#рекурсивные-запросы)
  - [Рекурсивные CTE для деревьев](#рекурсивные-cte-для-деревьев)
  - [Рекурсивные запросы для последовательностей](#рекурсивные-запросы-для-последовательностей)
  - [Рекурсивные запросы в Java](#рекурсивные-запросы-в-java)
- [JSON функции](#json-функции)
  - [Работа с JSON в MySQL 8.0+](#работа-с-json-в-mysql-80)
  - [Продвинутые JSON операции](#продвинутые-json-операции)
  - [JSON в запросах и индексах](#json-в-запросах-и-индексах)
  - [JSON в Java](#json-в-java)
- [Полнотекстовый поиск](#полнотекстовый-поиск)
  - [Настройка полнотекстового поиска](#настройка-полнотекстового-поиска)
  - [Полнотекстовые запросы](#полнотекстовые-запросы)
  - [Расширенные возможности полнотекстового поиска](#расширенные-возможности-полнотекстового-поиска)
  - [Полнотекстовый поиск в Java](#полнотекстовый-поиск-в-java)
- [Оптимизация запросов](#оптимизация-запросов-1)
  - [Анализ производительности запросов](#анализ-производительности-запросов)
  - [Оптимизация JOIN запросов](#оптимизация-join-запросов)
  - [Оптимизация подзапросов](#оптимизация-подзапросов)
  - [Кэширование запросов](#кэширование-запросов)
  - [Оптимизация в Java](#оптимизация-в-java)
- [EXPLAIN и анализ планов](#explain-и-анализ-планов)
  - [Чтение EXPLAIN вывода](#чтение-explain-вывода)
  - [Типы соединений в EXPLAIN](#типы-соединений-в-explain)
  - [Оптимизация на основе EXPLAIN](#оптимизация-на-основе-explain)
  - [JSON формат EXPLAIN](#json-формат-explain)
  - [EXPLAIN в Java](#explain-в-java)
- [Query Profiling](#query-profiling)
  - [Настройка профилирования](#настройка-профилирования)
  - [Performance Schema](#performance-schema)
  - [Системные переменные производительности](#системные-переменные-производительности)
  - [Профилирование в Java](#профилирование-в-java)
- [Лучшие практики](#лучшие-практики)
  - [Написание эффективных запросов](#написание-эффективных-запросов)
    - [1. Избегайте SELECT *](#1-избегайте-select)
    - [2. Используйте LIMIT для больших результатов](#2-используйте-limit-для-больших-результатов)
    - [3. Оптимизируйте условия WHERE](#3-оптимизируйте-условия-where)
    - [4. Используйте UNION ALL вместо UNION когда возможно](#4-используйте-union-all-вместо-union-когда-возможно)
  - [Оптимизация JOIN](#оптимизация-join-1)
    - [1. Выбирайте правильный тип JOIN](#1-выбирайте-правильный-тип-join)
    - [2. Порядок таблиц в JOIN](#2-порядок-таблиц-в-join)
    - [3. Избегайте CROSS JOIN](#3-избегайте-cross-join)
  - [Работа с индексами](#работа-с-индексами)
    - [1. Создавайте индексы на часто используемые условия](#1-создавайте-индексы-на-часто-используемые-условия)
    - [2. Используйте покрывающие индексы](#2-используйте-покрывающие-индексы)
    - [3. Мониторьте использование индексов](#3-мониторьте-использование-индексов)
  - [Оптимизация подзапросов](#оптимизация-подзапросов-1)
    - [1. Используйте EXISTS вместо IN для больших наборов](#1-используйте-exists-вместо-in-для-больших-наборов)
    - [2. Преобразуйте подзапросы в JOIN](#2-преобразуйте-подзапросы-в-join)
  - [Кэширование и материализованные представления](#кэширование-и-материализованные-представления)
    - [1. Используйте Query Cache (MySQL 5.7)](#1-используйте-query-cache-mysql-57)
    - [2. Материализованные представления для сложных агрегатов](#2-материализованные-представления-для-сложных-агрегатов)
  - [Мониторинг и обслуживание](#мониторинг-и-обслуживание)
    - [1. Регулярный анализ производительности](#1-регулярный-анализ-производительности)
    - [2. Обслуживание индексов и таблиц](#2-обслуживание-индексов-и-таблиц)
  - [Архитектурные решения](#архитектурные-решения)
    - [1. Read/Write Splitting](#1-readwrite-splitting)
    - [2. Шардинг (партиционирование)](#2-шардинг-партиционирование)
    - [3. Кэширование на уровне приложения](#3-кэширование-на-уровне-приложения)
  - [Профилирование и отладка](#профилирование-и-отладка)
    - [1. Используйте EXPLAIN для всех сложных запросов](#1-используйте-explain-для-всех-сложных-запросов)
    - [2. Мониторьте slow queries](#2-мониторьте-slow-queries)
    - [3. Используйте Performance Schema](#3-используйте-performance-schema)
  - [Безопасность запросов](#безопасность-запросов)
    - [1. Используйте Prepared Statements](#1-используйте-prepared-statements)
    - [2. Валидация входных данных](#2-валидация-входных-данных)
  - [Ключевые принципы оптимизации:](#ключевые-принципы-оптимизации)
  - [Основные инструменты оптимизации:](#основные-инструменты-оптимизации)
  - [Лучшие практики:](#лучшие-практики-1)
  - [Архитектурные подходы:](#архитектурные-подходы)
  - [Мониторинг и поддержка:](#мониторинг-и-поддержка)
- [Решение проблем](#решение-проблем)

## Базовые SELECT запросы

### Простые запросы

#### SELECT с условиями

Примеры базовых **SELECT**-запросов с полями и условиями (SQL).

```sql
-- Все пользователи
SELECT * FROM users;

-- Конкретные поля
SELECT id, first_name, last_name, email FROM users;

-- С псевдонимами
SELECT
    id,
    CONCAT(first_name, ' ', last_name) AS full_name,
    email,
    created_at
FROM users;

-- С вычисляемыми полями
SELECT
    id,
    first_name,
    last_name,
    TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) AS age,
    CASE
        WHEN status = 'active' THEN 'Активен'
        WHEN status = 'inactive' THEN 'Неактивен'
        ELSE 'Неизвестно'
    END AS status_text
FROM users;
```

#### DISTINCT и LIMIT
```sql
-- Уникальные значения
SELECT DISTINCT department FROM employees;

-- Ограничение количества результатов
SELECT * FROM products ORDER BY price DESC LIMIT 10;

-- Пагинация
SELECT * FROM orders
ORDER BY created_at DESC
LIMIT 20 OFFSET 40; -- Страница 3 (по 20 записей)

-- Случайная выборка
SELECT * FROM products ORDER BY RAND() LIMIT 5;
```

### Работа с NULL значениями

```sql
-- Поиск NULL значений
SELECT * FROM employees WHERE manager_id IS NULL;

-- Обработка NULL в выражениях
SELECT
    id,
    COALESCE(manager_id, 0) AS manager_id_safe,
    IFNULL(salary, 0) AS salary_safe,
    CONCAT('Manager: ', NULLIF(manager_name, '')) AS manager_info
FROM employees;

-- NULL-safe сравнение
SELECT * FROM users
WHERE email <=> 'test@example.com'; -- <=> работает с NULL

-- Агрегация с NULL
SELECT
    COUNT(*) AS total_employees,
    COUNT(salary) AS employees_with_salary, -- Игнорирует NULL
    AVG(COALESCE(salary, 0)) AS avg_salary_with_nulls
FROM employees;
```

### Запросы в Java

```java
@Repository
public class BasicQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Базовый SELECT
    public List<User> findAllUsers() {
        String sql = "SELECT id, first_name, last_name, email, created_at FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new User(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getTimestamp("created_at").toLocalDateTime()
            )
        );
    }

    // SELECT с условиями
    public List<User> findUsersByStatus(String status) {
        String sql = "SELECT * FROM users WHERE status = ?";
        return jdbcTemplate.query(sql, new Object[]{status}, userRowMapper);
    }

    // Пагинация
    public List<User> findUsersPaginated(int page, int size) {
        String sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{size, page * size}, userRowMapper);
    }

    // С вычисляемыми полями
    public List<UserSummary> findUserSummaries() {
        String sql = """
            SELECT
                id,
                CONCAT(first_name, ' ', last_name) AS full_name,
                TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) AS age,
                CASE
                    WHEN status = 'active' THEN 'Активен'
                    WHEN status = 'inactive' THEN 'Неактивен'
                    ELSE 'Неизвестно'
                END AS status_text
            FROM users
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new UserSummary(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getInt("age"),
                rs.getString("status_text")
            )
        );
    }

    // Обработка NULL значений
    public List<Employee> findEmployeesWithNullHandling() {
        String sql = """
            SELECT
                id,
                first_name,
                last_name,
                COALESCE(manager_id, 0) AS manager_id_safe,
                IFNULL(salary, 0) AS salary_safe
            FROM employees
            """;

        return jdbcTemplate.query(sql, employeeRowMapper);
    }

    // NamedParameterJdbcTemplate для сложных запросов
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<User> findUsersByCriteria(UserSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM users WHERE 1=1");

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (criteria.getFirstName() != null) {
            sql.append(" AND first_name LIKE :firstName");
            params.addValue("firstName", "%" + criteria.getFirstName() + "%");
        }

        if (criteria.getEmail() != null) {
            sql.append(" AND email = :email");
            params.addValue("email", criteria.getEmail());
        }

        if (criteria.getCreatedAfter() != null) {
            sql.append(" AND created_at >= :createdAfter");
            params.addValue("createdAfter", criteria.getCreatedAfter());
        }

        sql.append(" ORDER BY created_at DESC");

        if (criteria.getLimit() != null) {
            sql.append(" LIMIT :limit");
            params.addValue("limit", criteria.getLimit());
        }

        return namedParameterJdbcTemplate.query(sql.toString(), params, userRowMapper);
    }

    private RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
        rs.getLong("id"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        rs.getString("email"),
        rs.getTimestamp("created_at").toLocalDateTime()
    );

    private RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> new Employee(
        rs.getLong("id"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        rs.getBigDecimal("salary")
    );
}

// Вспомогательные классы
class UserSummary {
    private Long id;
    private String fullName;
    private Integer age;
    private String statusText;
    // constructor, getters, setters
}

class UserSearchCriteria {
    private String firstName;
    private String email;
    private LocalDateTime createdAfter;
    private Integer limit;
    // constructor, getters, setters
}
```

## JOIN операции

### INNER JOIN

```sql
-- Простой INNER JOIN
SELECT
    u.first_name,
    u.last_name,
    o.order_date,
    o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id;

-- Множественные JOIN
SELECT
    u.first_name,
    u.last_name,
    o.order_date,
    p.name AS product_name,
    oi.quantity,
    oi.price
FROM users u
INNER JOIN orders o ON u.id = o.user_id
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id;
```

### LEFT/RIGHT JOIN

```sql
-- LEFT JOIN - все пользователи и их заказы (если есть)
SELECT
    u.first_name,
    u.last_name,
    COUNT(o.id) AS order_count,
    COALESCE(SUM(o.total_amount), 0) AS total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.first_name, u.last_name;

-- RIGHT JOIN - все заказы и пользователи
SELECT
    u.first_name,
    u.last_name,
    o.order_date,
    o.total_amount
FROM users u
RIGHT JOIN orders o ON u.id = o.user_id;

-- FULL OUTER JOIN (эмуляция в MySQL)
SELECT
    u.first_name,
    u.last_name,
    o.order_date,
    o.total_amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
UNION
SELECT
    u.first_name,
    u.last_name,
    o.order_date,
    o.total_amount
FROM users u
RIGHT JOIN orders o ON u.id = o.user_id
WHERE u.id IS NULL;
```

### CROSS JOIN и SELF JOIN

```sql
-- CROSS JOIN - декартово произведение
SELECT
    u.first_name,
    p.name AS product_name
FROM users u
CROSS JOIN products p
LIMIT 20;

-- SELF JOIN - иерархические данные
SELECT
    e.first_name AS employee_name,
    m.first_name AS manager_name
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;

-- SELF JOIN - сравнение сотрудников одного отдела
SELECT
    e1.first_name AS employee1,
    e2.first_name AS employee2,
    e1.salary AS salary1,
    e2.salary AS salary2
FROM employees e1
INNER JOIN employees e2 ON e1.department_id = e2.department_id
    AND e1.id < e2.id; -- Избегание дубликатов
```

### Оптимизация JOIN

```sql
-- Использование индексов для JOIN
EXPLAIN SELECT
    u.first_name,
    COUNT(o.id) AS order_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.first_name;

-- Оптимизация с STRAIGHT_JOIN (принудительный порядок)
SELECT STRAIGHT_JOIN
    u.first_name,
    o.order_date
FROM users u
STRAIGHT_JOIN orders o ON u.id = o.user_id
WHERE u.active = true;
```

### JOIN в Java

```java
@Repository
public class JoinQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // INNER JOIN
    public List<OrderSummary> findOrderSummaries() {
        String sql = """
            SELECT
                u.first_name,
                u.last_name,
                o.order_date,
                o.total_amount,
                COUNT(oi.id) AS item_count
            FROM users u
            INNER JOIN orders o ON u.id = o.user_id
            INNER JOIN order_items oi ON o.id = oi.order_id
            GROUP BY u.id, u.first_name, u.last_name, o.id, o.order_date, o.total_amount
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new OrderSummary(
                rs.getString("first_name") + " " + rs.getString("last_name"),
                rs.getDate("order_date").toLocalDate(),
                rs.getBigDecimal("total_amount"),
                rs.getInt("item_count")
            )
        );
    }

    // LEFT JOIN с агрегацией
    public List<UserOrderStats> findUserOrderStats() {
        String sql = """
            SELECT
                u.id,
                u.first_name,
                u.last_name,
                COUNT(o.id) AS order_count,
                COALESCE(SUM(o.total_amount), 0) AS total_spent,
                COALESCE(AVG(o.total_amount), 0) AS avg_order_value,
                MAX(o.order_date) AS last_order_date
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            GROUP BY u.id, u.first_name, u.last_name
            ORDER BY total_spent DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new UserOrderStats(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getInt("order_count"),
                rs.getBigDecimal("total_spent"),
                rs.getBigDecimal("avg_order_value"),
                rs.getDate("last_order_date") != null ?
                    rs.getDate("last_order_date").toLocalDate() : null
            )
        );
    }

    // Множественные JOIN с фильтрами
    public List<ProductSalesInfo> findProductSalesInfo(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT
                p.id,
                p.name,
                p.category,
                SUM(oi.quantity) AS total_quantity_sold,
                SUM(oi.quantity * oi.price) AS total_revenue,
                COUNT(DISTINCT o.user_id) AS unique_buyers,
                AVG(oi.price) AS avg_sale_price
            FROM products p
            INNER JOIN order_items oi ON p.id = oi.product_id
            INNER JOIN orders o ON oi.order_id = o.id
            WHERE o.order_date BETWEEN ? AND ?
            GROUP BY p.id, p.name, p.category
            HAVING total_quantity_sold > 0
            ORDER BY total_revenue DESC
            """;

        return jdbcTemplate.query(sql,
            new Object[]{Date.valueOf(startDate), Date.valueOf(endDate)},
            productSalesRowMapper);
    }

    // SELF JOIN для иерархии
    public List<EmployeeHierarchy> findEmployeeHierarchy() {
        String sql = """
            SELECT
                e.id,
                e.first_name,
                e.last_name,
                e.position,
                m.id AS manager_id,
                m.first_name AS manager_first_name,
                m.last_name AS manager_last_name
            FROM employees e
            LEFT JOIN employees m ON e.manager_id = m.id
            ORDER BY COALESCE(m.id, 0), e.last_name
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new EmployeeHierarchy(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("position"),
                rs.getLong("manager_id"),
                rs.getString("manager_first_name"),
                rs.getString("manager_last_name")
            )
        );
    }

    // CROSS JOIN с ограничениями
    public List<UserProductRecommendation> findProductRecommendations(Long userId) {
        String sql = """
            SELECT
                u.first_name,
                u.last_name,
                p.name AS product_name,
                p.category,
                p.price
            FROM users u
            CROSS JOIN products p
            WHERE u.id = ?
            AND p.stock_quantity > 0
            AND p.category IN (
                SELECT DISTINCT p2.category
                FROM orders o
                JOIN order_items oi ON o.id = oi.order_id
                JOIN products p2 ON oi.product_id = p2.id
                WHERE o.user_id = ?
            )
            LIMIT 10
            """;

        return jdbcTemplate.query(sql, new Object[]{userId, userId},
            (rs, rowNum) -> new UserProductRecommendation(
                rs.getString("first_name") + " " + rs.getString("last_name"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getBigDecimal("price")
            )
        );
    }

    private RowMapper<ProductSalesInfo> productSalesRowMapper = (rs, rowNum) ->
        new ProductSalesInfo(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getInt("total_quantity_sold"),
            rs.getBigDecimal("total_revenue"),
            rs.getInt("unique_buyers"),
            rs.getBigDecimal("avg_sale_price")
        );
}

// Вспомогательные классы
class OrderSummary {
    private String customerName;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private Integer itemCount;
    // constructor, getters, setters
}

class UserOrderStats {
    private Long userId;
    private String firstName;
    private String lastName;
    private Integer orderCount;
    private BigDecimal totalSpent;
    private BigDecimal avgOrderValue;
    private LocalDate lastOrderDate;
    // constructor, getters, setters
}

class ProductSalesInfo {
    private Long productId;
    private String productName;
    private String category;
    private Integer totalQuantitySold;
    private BigDecimal totalRevenue;
    private Integer uniqueBuyers;
    private BigDecimal avgSalePrice;
    // constructor, getters, setters
}

class EmployeeHierarchy {
    private Long employeeId;
    private String employeeName;
    private String position;
    private Long managerId;
    private String managerName;
    // constructor, getters, setters
}

class UserProductRecommendation {
    private String userName;
    private String productName;
    private String category;
    private BigDecimal price;
    // constructor, getters, setters
}
```

## WHERE условия и операторы

### Основные операторы сравнения

```sql
-- Операторы сравнения
SELECT * FROM products WHERE price > 100;
SELECT * FROM products WHERE price >= 50 AND price <= 200;
SELECT * FROM users WHERE created_at < '2024-01-01';
SELECT * FROM products WHERE category != 'electronics';

-- BETWEEN
SELECT * FROM orders WHERE order_date BETWEEN '2024-01-01' AND '2024-12-31';

-- IN и NOT IN
SELECT * FROM products WHERE category IN ('electronics', 'books', 'clothing');
SELECT * FROM users WHERE id NOT IN (1, 2, 3, 4, 5);

-- LIKE для поиска по шаблону
SELECT * FROM users WHERE email LIKE '%@gmail.com';
SELECT * FROM products WHERE name LIKE 'Laptop%';
SELECT * FROM users WHERE first_name LIKE '_ohn'; -- _ - один символ

-- REGEXP для регулярных выражений
SELECT * FROM users WHERE email REGEXP '@[a-z]+\.com$';
SELECT * FROM products WHERE name REGEXP '^Apple.*iPhone';
```

### Логические операторы

```sql
-- AND, OR, NOT
SELECT * FROM products
WHERE (category = 'electronics' AND price > 500)
   OR (category = 'books' AND price < 50);

-- Приоритет операторов
SELECT * FROM users
WHERE active = true
  AND (last_login > '2024-01-01' OR email LIKE '%@company.com');

-- XOR (исключающее ИЛИ)
SELECT * FROM products
WHERE (category = 'electronics' XOR brand = 'Apple');
```

### Специальные операторы

```sql
-- IS NULL / IS NOT NULL
SELECT * FROM users WHERE last_login IS NULL;
SELECT * FROM products WHERE description IS NOT NULL;

-- EXISTS / NOT EXISTS
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id
    AND o.total_amount > 1000
);

-- ANY / ALL
SELECT * FROM products
WHERE price > ALL (
    SELECT AVG(price) FROM products GROUP BY category
);

-- SOME (синоним ANY)
SELECT * FROM orders
WHERE total_amount > SOME (
    SELECT price FROM products WHERE category = 'premium'
);
```

### Условия в Java

```java
@Repository
public class ConditionalQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Динамические условия
    public List<Product> findProductsByCriteria(ProductSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (criteria.getMinPrice() != null) {
            sql.append(" AND price >= ?");
            params.add(criteria.getMinPrice());
        }

        if (criteria.getMaxPrice() != null) {
            sql.append(" AND price <= ?");
            params.add(criteria.getMaxPrice());
        }

        if (criteria.getCategory() != null) {
            sql.append(" AND category = ?");
            params.add(criteria.getCategory());
        }

        if (criteria.getSearchTerm() != null) {
            sql.append(" AND (name LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + criteria.getSearchTerm() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (criteria.getInStock() != null && criteria.getInStock()) {
            sql.append(" AND stock_quantity > 0");
        }

        sql.append(" ORDER BY ").append(criteria.getSortBy() != null ?
            criteria.getSortBy() : "name");

        if (criteria.getSortDirection() != null &&
            "DESC".equalsIgnoreCase(criteria.getSortDirection())) {
            sql.append(" DESC");
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), productRowMapper);
    }

    // IN оператор
    public List<Product> findProductsByIds(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM products WHERE id IN (" +
            String.join(",", Collections.nCopies(productIds.size(), "?")) + ")";

        return jdbcTemplate.query(sql, productIds.toArray(), productRowMapper);
    }

    // EXISTS запросы
    public List<User> findUsersWithExpensiveOrders(BigDecimal minAmount) {
        String sql = """
            SELECT DISTINCT u.* FROM users u
            WHERE EXISTS (
                SELECT 1 FROM orders o
                WHERE o.user_id = u.id
                AND o.total_amount >= ?
            )
            """;

        return jdbcTemplate.query(sql, new Object[]{minAmount}, userRowMapper);
    }

    // REGEXP запросы
    public List<User> findUsersByEmailPattern(String pattern) {
        String sql = "SELECT * FROM users WHERE email REGEXP ?";
        return jdbcTemplate.query(sql, new Object[]{pattern}, userRowMapper);
    }

    // NULL-safe запросы
    public List<Product> findProductsWithOptionalCategory(String category) {
        String sql = """
            SELECT * FROM products
            WHERE (? IS NULL OR category = ?)
            """;

        return jdbcTemplate.query(sql, new Object[]{category, category}, productRowMapper);
    }

    // Диапазонные запросы
    public List<Order> findOrdersInDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT * FROM orders
            WHERE order_date BETWEEN ? AND ?
            ORDER BY order_date DESC
            """;

        return jdbcTemplate.query(sql,
            new Object[]{Date.valueOf(startDate), Date.valueOf(endDate)},
            orderRowMapper);
    }

    // Сложные условия с подзапросами
    public List<Product> findPopularProductsInCategory(String category, int minOrders) {
        String sql = """
            SELECT p.* FROM products p
            WHERE p.category = ?
            AND p.id IN (
                SELECT oi.product_id
                FROM order_items oi
                GROUP BY oi.product_id
                HAVING COUNT(*) >= ?
            )
            """;

        return jdbcTemplate.query(sql, new Object[]{category, minOrders}, productRowMapper);
    }

    private RowMapper<Product> productRowMapper = (rs, rowNum) -> new Product(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("category"),
        rs.getBigDecimal("price")
    );

    private RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
        rs.getLong("id"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        rs.getString("email")
    );

    private RowMapper<Order> orderRowMapper = (rs, rowNum) -> new Order(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getBigDecimal("total_amount"),
        rs.getDate("order_date").toLocalDate()
    );
}

// Вспомогательные классы
class ProductSearchCriteria {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String category;
    private String searchTerm;
    private Boolean inStock;
    private String sortBy;
    private String sortDirection;
    // constructor, getters, setters
}
```

## Сортировка и группировка

### ORDER `BY`

```sql
-- Базовая сортировка
SELECT * FROM products ORDER BY price DESC;
SELECT * FROM users ORDER BY last_name, first_name;

-- Сортировка с NULL значениями
SELECT * FROM users ORDER BY last_login DESC NULLS LAST;
SELECT * FROM products ORDER BY discount NULLS FIRST;

-- Сортировка по вычисляемым полям
SELECT
    id,
    name,
    price,
    CASE
        WHEN price > 1000 THEN 'premium'
        WHEN price > 100 THEN 'standard'
        ELSE 'budget'
    END AS category
FROM products
ORDER BY
    CASE
        WHEN price > 1000 THEN 1
        WHEN price > 100 THEN 2
        ELSE 3
    END,
    price DESC;
```

### GROUP `BY`

```sql
-- Базовая группировка
SELECT category, COUNT(*) AS product_count
FROM products
GROUP BY category;

-- Группировка с агрегацией
SELECT
    category,
    COUNT(*) AS product_count,
    AVG(price) AS avg_price,
    MIN(price) AS min_price,
    MAX(price) AS max_price,
    SUM(stock_quantity) AS total_stock
FROM products
GROUP BY category
ORDER BY avg_price DESC;

-- Группировка по нескольким полям
SELECT
    YEAR(order_date) AS year,
    MONTH(order_date) AS month,
    COUNT(*) AS order_count,
    SUM(total_amount) AS total_revenue
FROM orders
GROUP BY YEAR(order_date), MONTH(order_date)
ORDER BY year DESC, month DESC;
```

### HAVING

```sql
-- Фильтрация групп
SELECT category, COUNT(*) AS product_count, AVG(price) AS avg_price
FROM products
GROUP BY category
HAVING COUNT(*) > 5 AND AVG(price) > 50;

-- HAVING с подзапросом
SELECT category, AVG(price) AS avg_price
FROM products
GROUP BY category
HAVING AVG(price) > (
    SELECT AVG(price) FROM products
);
```

### ROLLUP и CUBE

```sql
-- ROLLUP - промежуточные итоги
SELECT
    YEAR(order_date) AS year,
    MONTH(order_date) AS month,
    COUNT(*) AS order_count,
    SUM(total_amount) AS total_amount
FROM orders
GROUP BY ROLLUP(YEAR(order_date), MONTH(order_date))
ORDER BY year, month;

-- CUBE - все комбинации
SELECT
    category,
    brand,
    COUNT(*) AS product_count,
    AVG(price) AS avg_price
FROM products
GROUP BY CUBE(category, brand)
ORDER BY category, brand;
```

### Сортировка и группировка в Java

```java
@Repository
public class SortGroupQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Гибкая сортировка
    public List<Product> findProductsSorted(String sortBy, String sortDirection) {
        String sql = "SELECT * FROM products ORDER BY " + sortBy +
            ("DESC".equalsIgnoreCase(sortDirection) ? " DESC" : " ASC");

        return jdbcTemplate.query(sql, productRowMapper);
    }

    // Группировка по категориям
    public List<CategoryStats> findCategoryStats() {
        String sql = """
            SELECT
                category,
                COUNT(*) AS product_count,
                AVG(price) AS avg_price,
                MIN(price) AS min_price,
                MAX(price) AS max_price,
                SUM(stock_quantity) AS total_stock
            FROM products
            GROUP BY category
            ORDER BY avg_price DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new CategoryStats(
                rs.getString("category"),
                rs.getInt("product_count"),
                rs.getBigDecimal("avg_price"),
                rs.getBigDecimal("min_price"),
                rs.getBigDecimal("max_price"),
                rs.getInt("total_stock")
            )
        );
    }

    // Группировка по датам с ROLLUP
    public List<SalesReport> findSalesReportWithRollup() {
        String sql = """
            SELECT
                COALESCE(YEAR(order_date), 'TOTAL') AS year,
                COALESCE(MONTH(order_date), 'TOTAL') AS month,
                COUNT(*) AS order_count,
                SUM(total_amount) AS total_amount,
                AVG(total_amount) AS avg_amount
            FROM orders
            GROUP BY ROLLUP(YEAR(order_date), MONTH(order_date))
            ORDER BY year IS NULL, year DESC, month IS NULL, month DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new SalesReport(
                rs.getString("year"),
                rs.getString("month"),
                rs.getInt("order_count"),
                rs.getBigDecimal("total_amount"),
                rs.getBigDecimal("avg_amount")
            )
        );
    }

    // HAVING с фильтрами
    public List<CategoryStats> findPopularCategories(int minProducts, BigDecimal minAvgPrice) {
        String sql = """
            SELECT
                category,
                COUNT(*) AS product_count,
                AVG(price) AS avg_price,
                SUM(stock_quantity) AS total_stock
            FROM products
            GROUP BY category
            HAVING COUNT(*) >= ? AND AVG(price) >= ?
            ORDER BY total_stock DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{minProducts, minAvgPrice},
            categoryStatsRowMapper);
    }

    // Сортировка по вычисляемым полям
    public List<Product> findProductsByPriceCategory(String categoryOrder) {
        String sql = """
            SELECT *,
                CASE
                    WHEN price > 1000 THEN 'premium'
                    WHEN price > 100 THEN 'standard'
                    ELSE 'budget'
                END AS price_category
            FROM products
            ORDER BY
                CASE
                    WHEN price > 1000 THEN 1
                    WHEN price > 100 THEN 2
                    ELSE 3
                END """ + ("DESC".equalsIgnoreCase(categoryOrder) ? " DESC" : " ASC") + ",
                price DESC";

        return jdbcTemplate.query(sql, productRowMapper);
    }

    // Группировка с CUBE
    public List<ProductMatrix> findProductMatrix() {
        String sql = """
            SELECT
                COALESCE(category, 'ALL') AS category,
                COALESCE(brand, 'ALL') AS brand,
                COUNT(*) AS product_count,
                AVG(price) AS avg_price
            FROM products
            GROUP BY CUBE(category, brand)
            ORDER BY category IS NULL, category, brand IS NULL, brand
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new ProductMatrix(
                rs.getString("category"),
                rs.getString("brand"),
                rs.getInt("product_count"),
                rs.getBigDecimal("avg_price")
            )
        );
    }

    private RowMapper<Product> productRowMapper = (rs, rowNum) -> new Product(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getBigDecimal("price")
    );

    private RowMapper<CategoryStats> categoryStatsRowMapper = (rs, rowNum) ->
        new CategoryStats(
            rs.getString("category"),
            rs.getInt("product_count"),
            rs.getBigDecimal("avg_price"),
            null, null, rs.getInt("total_stock")
        );
}

// Вспомогательные классы
class CategoryStats {
    private String category;
    private Integer productCount;
    private BigDecimal avgPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock;
    // constructor, getters, setters
}

class SalesReport {
    private String year;
    private String month;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private BigDecimal avgAmount;
    // constructor, getters, setters
}

class ProductMatrix {
    private String category;
    private String brand;
    private Integer productCount;
    private BigDecimal avgPrice;
    // constructor, getters, setters
}
```

## Агрегатные функции

### Основные агрегатные функции

```sql
-- COUNT
SELECT COUNT(*) FROM users;                    -- Все строки
SELECT COUNT(id) FROM users;                   -- Не-NULL значения
SELECT COUNT(DISTINCT department) FROM users;  -- Уникальные значения

-- SUM
SELECT SUM(salary) FROM employees;
SELECT SUM(CASE WHEN active = true THEN salary ELSE 0 END) FROM employees;

-- AVG
SELECT AVG(price) FROM products;
SELECT AVG(price) FILTER (WHERE category = 'electronics') FROM products;

-- MIN/MAX
SELECT MIN(price), MAX(price) FROM products;
SELECT MIN(created_at), MAX(created_at) FROM users;

-- Статистические функции
SELECT
    STDDEV(price) AS price_stddev,
    VARIANCE(price) AS price_variance
FROM products;
```

### Продвинутые агрегатные функции

```sql
-- GROUP_CONCAT - объединение строк
SELECT
    department,
    GROUP_CONCAT(employee_name ORDER BY salary DESC SEPARATOR ', ') AS employees
FROM employees
GROUP BY department;

-- BIT_AND, BIT_OR, BIT_XOR - побитовые операции
SELECT
    BIT_AND(flags) AS common_flags,
    BIT_OR(flags) AS all_flags
FROM user_permissions;

-- JSON_ARRAYAGG - агрегация в JSON массив
SELECT
    category,
    JSON_ARRAYAGG(name ORDER BY price DESC) AS products
FROM products
GROUP BY category;
```

### Window Functions

```sql
-- ROW_NUMBER
SELECT
    name,
    category,
    price,
    ROW_NUMBER() OVER (ORDER BY price DESC) AS price_rank
FROM products;

-- RANK и DENSE_RANK
SELECT
    name,
    category,
    price,
    RANK() OVER (ORDER BY price DESC) AS price_rank,
    DENSE_RANK() OVER (ORDER BY price DESC) AS dense_rank
FROM products;

-- PARTITION BY - группировка внутри окон
SELECT
    name,
    category,
    price,
    AVG(price) OVER (PARTITION BY category) AS category_avg_price,
    price - AVG(price) OVER (PARTITION BY category) AS price_diff_from_avg
FROM products;

-- LAG и LEAD - доступ к предыдущим/следующим строкам
SELECT
    name,
    price,
    LAG(price) OVER (ORDER BY name) AS prev_price,
    LEAD(price) OVER (ORDER BY name) AS next_price,
    price - LAG(price) OVER (ORDER BY name) AS price_change
FROM products;

-- FIRST_VALUE и LAST_VALUE
SELECT
    name,
    category,
    price,
    FIRST_VALUE(price) OVER (PARTITION BY category ORDER BY price) AS cheapest_in_category,
    LAST_VALUE(price) OVER (PARTITION BY category ORDER BY price
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS most_expensive_in_category
FROM products;
```

### Агрегатные функции в Java

```java
@Repository
public class AggregateQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Базовая статистика
    public ProductStats getProductStats() {
        String sql = """
            SELECT
                COUNT(*) AS total_products,
                AVG(price) AS avg_price,
                MIN(price) AS min_price,
                MAX(price) AS max_price,
                STDDEV(price) AS price_stddev,
                SUM(stock_quantity) AS total_stock
            FROM products
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
            new ProductStats(
                rs.getInt("total_products"),
                rs.getBigDecimal("avg_price"),
                rs.getBigDecimal("min_price"),
                rs.getBigDecimal("max_price"),
                rs.getBigDecimal("price_stddev"),
                rs.getInt("total_stock")
            )
        );
    }

    // Статистика по категориям
    public List<CategoryAggregate> getCategoryAggregates() {
        String sql = """
            SELECT
                category,
                COUNT(*) AS product_count,
                AVG(price) AS avg_price,
                SUM(stock_quantity) AS total_stock,
                GROUP_CONCAT(name ORDER BY price DESC SEPARATOR '; ') AS product_names
            FROM products
            GROUP BY category
            ORDER BY avg_price DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new CategoryAggregate(
                rs.getString("category"),
                rs.getInt("product_count"),
                rs.getBigDecimal("avg_price"),
                rs.getInt("total_stock"),
                rs.getString("product_names")
            )
        );
    }

    // Window Functions
    public List<ProductRanking> getProductRankings() {
        String sql = """
            SELECT
                name,
                category,
                price,
                ROW_NUMBER() OVER (ORDER BY price DESC) AS overall_rank,
                RANK() OVER (ORDER BY price DESC) AS price_rank,
                DENSE_RANK() OVER (ORDER BY price DESC) AS dense_rank,
                AVG(price) OVER (PARTITION BY category) AS category_avg_price,
                FIRST_VALUE(name) OVER (PARTITION BY category ORDER BY price) AS cheapest_in_category,
                LAG(price) OVER (ORDER BY name) AS prev_price,
                LEAD(price) OVER (ORDER BY name) AS next_price
            FROM products
            ORDER BY price DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new ProductRanking(
                rs.getString("name"),
                rs.getString("category"),
                rs.getBigDecimal("price"),
                rs.getInt("overall_rank"),
                rs.getInt("price_rank"),
                rs.getInt("dense_rank"),
                rs.getBigDecimal("category_avg_price"),
                rs.getString("cheapest_in_category"),
                rs.getBigDecimal("prev_price"),
                rs.getBigDecimal("next_price")
            )
        );
    }

    // Кумулятивные суммы
    public List<SalesCumulative> getCumulativeSales() {
        String sql = """
            SELECT
                DATE(order_date) AS sale_date,
                COUNT(*) AS daily_orders,
                SUM(total_amount) AS daily_revenue,
                SUM(COUNT(*)) OVER (ORDER BY DATE(order_date)) AS cumulative_orders,
                SUM(SUM(total_amount)) OVER (ORDER BY DATE(order_date)) AS cumulative_revenue
            FROM orders
            GROUP BY DATE(order_date)
            ORDER BY sale_date
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new SalesCumulative(
                rs.getDate("sale_date").toLocalDate(),
                rs.getInt("daily_orders"),
                rs.getBigDecimal("daily_revenue"),
                rs.getInt("cumulative_orders"),
                rs.getBigDecimal("cumulative_revenue")
            )
        );
    }

    // Процентные ранги
    public List<ProductPercentile> getProductPercentiles() {
        String sql = """
            SELECT
                name,
                price,
                PERCENT_RANK() OVER (ORDER BY price) AS price_percentile,
                CUME_DIST() OVER (ORDER BY price) AS cumulative_dist,
                NTILE(4) OVER (ORDER BY price) AS price_quartile
            FROM products
            ORDER BY price
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new ProductPercentile(
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getDouble("price_percentile"),
                rs.getDouble("cumulative_dist"),
                rs.getInt("price_quartile")
            )
        );
    }

    // Скользящие средние
    public List<MovingAverage> getMovingAverages() {
        String sql = """
            SELECT
                DATE(order_date) AS sale_date,
                SUM(total_amount) AS daily_revenue,
                AVG(SUM(total_amount)) OVER (
                    ORDER BY DATE(order_date)
                    ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
                ) AS moving_avg_7day,
                SUM(SUM(total_amount)) OVER (
                    ORDER BY DATE(order_date)
                    ROWS BETWEEN 29 PRECEDING AND CURRENT ROW
                ) / 30 AS avg_monthly
            FROM orders
            WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
            GROUP BY DATE(order_date)
            ORDER BY sale_date
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new MovingAverage(
                rs.getDate("sale_date").toLocalDate(),
                rs.getBigDecimal("daily_revenue"),
                rs.getBigDecimal("moving_avg_7day"),
                rs.getBigDecimal("avg_monthly")
            )
        );
    }
}

// Вспомогательные классы
class ProductStats {
    private Integer totalProducts;
    private BigDecimal avgPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal priceStddev;
    private Integer totalStock;
    // constructor, getters, setters
}

class CategoryAggregate {
    private String category;
    private Integer productCount;
    private BigDecimal avgPrice;
    private Integer totalStock;
    private String productNames;
    // constructor, getters, setters
}

class ProductRanking {
    private String name;
    private String category;
    private BigDecimal price;
    private Integer overallRank;
    private Integer priceRank;
    private Integer denseRank;
    private BigDecimal categoryAvgPrice;
    private String cheapestInCategory;
    private BigDecimal prevPrice;
    private BigDecimal nextPrice;
    // constructor, getters, setters
}

class SalesCumulative {
    private LocalDate saleDate;
    private Integer dailyOrders;
    private BigDecimal dailyRevenue;
    private Integer cumulativeOrders;
    private BigDecimal cumulativeRevenue;
    // constructor, getters, setters
}

class ProductPercentile {
    private String name;
    private BigDecimal price;
    private Double pricePercentile;
    private Double cumulativeDist;
    private Integer priceQuartile;
    // constructor, getters, setters
}

class MovingAverage {
    private LocalDate saleDate;
    private BigDecimal dailyRevenue;
    private BigDecimal movingAvg7Day;
    private BigDecimal avgMonthly;
    // constructor, getters, setters
}
```

## Подзапросы

### Скалярные подзапросы

```sql
-- Скалярный подзапрос
SELECT
    name,
    price,
    price - (SELECT AVG(price) FROM products) AS price_diff_from_avg
FROM products;

-- В условии WHERE
SELECT * FROM users
WHERE id = (SELECT user_id FROM orders WHERE total_amount = (
    SELECT MAX(total_amount) FROM orders
));

-- В списке SELECT
SELECT
    name,
    (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count
FROM users u;
```

### Коррелированные подзапросы

```sql
-- Коррелированный подзапрос
SELECT
    u.name,
    u.email,
    (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count,
    (SELECT SUM(total_amount) FROM orders o WHERE o.user_id = u.id) AS total_spent
FROM users u;

-- EXISTS с коррелированным подзапросом
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id
    AND o.total_amount > 1000
);

-- NOT EXISTS
SELECT * FROM products p
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi
    WHERE oi.product_id = p.id
);
```

### Подзапросы в FROM

```sql
-- Подзапрос в FROM
SELECT
    category,
    avg_price,
    product_count
FROM (
    SELECT
        category,
        AVG(price) AS avg_price,
        COUNT(*) AS product_count
    FROM products
    GROUP BY category
) category_stats
WHERE avg_price > 100;

-- С JOIN подзапроса
SELECT
    cs.category,
    cs.avg_price,
    cs.product_count,
    p.name AS most_expensive_product
FROM (
    SELECT
        category,
        AVG(price) AS avg_price,
        COUNT(*) AS product_count,
        MAX(price) AS max_price
    FROM products
    GROUP BY category
) cs
JOIN products p ON cs.category = p.category AND cs.max_price = p.price;
```

### Подзапросы в Java

```java
@Repository
public class SubqueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Скалярные подзапросы
    public List<UserOrderSummary> getUserOrderSummaries() {
        String sql = """
            SELECT
                u.id,
                u.name,
                u.email,
                (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count,
                (SELECT COALESCE(SUM(total_amount), 0) FROM orders o WHERE o.user_id = u.id) AS total_spent,
                (SELECT AVG(total_amount) FROM orders o WHERE o.user_id = u.id) AS avg_order_value
            FROM users u
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new UserOrderSummary(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("order_count"),
                rs.getBigDecimal("total_spent"),
                rs.getBigDecimal("avg_order_value")
            )
        );
    }

    // EXISTS подзапросы
    public List<User> findUsersWithLargeOrders(BigDecimal minAmount) {
        String sql = """
            SELECT * FROM users u
            WHERE EXISTS (
                SELECT 1 FROM orders o
                WHERE o.user_id = u.id
                AND o.total_amount >= ?
            )
            """;

        return jdbcTemplate.query(sql, new Object[]{minAmount}, userRowMapper);
    }

    // NOT EXISTS подзапросы
    public List<Product> findUnsoldProducts() {
        String sql = """
            SELECT * FROM products p
            WHERE NOT EXISTS (
                SELECT 1 FROM order_items oi
                WHERE oi.product_id = p.id
            )
            """;

        return jdbcTemplate.query(sql, productRowMapper);
    }

    // Подзапросы в FROM
    public List<CategoryStats> getCategoryStatsFromSubquery() {
        String sql = """
            SELECT
                cs.category,
                cs.product_count,
                cs.avg_price,
                cs.total_stock,
                p.name AS most_expensive_product
            FROM (
                SELECT
                    category,
                    COUNT(*) AS product_count,
                    AVG(price) AS avg_price,
                    SUM(stock_quantity) AS total_stock,
                    MAX(price) AS max_price
                FROM products
                GROUP BY category
            ) cs
            JOIN products p ON cs.category = p.category AND cs.max_price = p.price
            ORDER BY cs.avg_price DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new CategoryStats(
                rs.getString("category"),
                rs.getInt("product_count"),
                rs.getBigDecimal("avg_price"),
                rs.getInt("total_stock"),
                rs.getString("most_expensive_product")
            )
        );
    }

    // IN с подзапросом
    public List<Product> findProductsInTopCategories(int topCount) {
        String sql = """
            SELECT * FROM products
            WHERE category IN (
                SELECT category FROM (
                    SELECT
                        category,
                        AVG(price) AS avg_price
                    FROM products
                    GROUP BY category
                    ORDER BY avg_price DESC
                    LIMIT ?
                ) top_categories
            )
            ORDER BY category, price DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{topCount}, productRowMapper);
    }

    // Сложные коррелированные подзапросы
    public List<UserPerformance> getUserPerformanceMetrics() {
        String sql = """
            SELECT
                u.id,
                u.name,
                u.registration_date,
                (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS total_orders,
                (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id AND o.status = 'completed') AS completed_orders,
                (SELECT AVG(total_amount) FROM orders o WHERE o.user_id = u.id) AS avg_order_value,
                (SELECT MAX(order_date) FROM orders o WHERE o.user_id = u.id) AS last_order_date,
                CASE
                    WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) = 0 THEN 'new'
                    WHEN (SELECT AVG(total_amount) FROM orders o WHERE o.user_id = u.id) > 200 THEN 'vip'
                    WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 10 THEN 'regular'
                    ELSE 'occasional'
                END AS customer_segment
            FROM users u
            ORDER BY total_orders DESC, avg_order_value DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new UserPerformance(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDate("registration_date").toLocalDate(),
                rs.getInt("total_orders"),
                rs.getInt("completed_orders"),
                rs.getBigDecimal("avg_order_value"),
                rs.getDate("last_order_date") != null ?
                    rs.getDate("last_order_date").toLocalDate() : null,
                rs.getString("customer_segment")
            )
        );
    }

    private RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("email")
    );

    private RowMapper<Product> productRowMapper = (rs, rowNum) -> new Product(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getBigDecimal("price")
    );
}

// Вспомогательные классы
class UserOrderSummary {
    private Long userId;
    private String name;
    private String email;
    private Integer orderCount;
    private BigDecimal totalSpent;
    private BigDecimal avgOrderValue;
    // constructor, getters, setters
}

class UserPerformance {
    private Long userId;
    private String name;
    private LocalDate registrationDate;
    private Integer totalOrders;
    private Integer completedOrders;
    private BigDecimal avgOrderValue;
    private LocalDate lastOrderDate;
    private String customerSegment;
    // constructor, getters, setters
}
```

## UNION и EXCEPT

### UNION и UNION ALL

```sql
-- UNION (убирает дубликаты)
SELECT name, 'customer' AS type FROM customers
UNION
SELECT name, 'supplier' AS type FROM suppliers;

-- UNION ALL (сохраняет дубликаты)
SELECT name, email FROM active_users
UNION ALL
SELECT name, email FROM inactive_users;

-- С ORDER BY для всего результата
SELECT id, name, 'product' AS type FROM products
UNION
SELECT id, name, 'category' AS type FROM categories
ORDER BY name;
```

### INTERSECT и EXCEPT/MINUS

```sql
-- INTERSECT (пересечение - MySQL 8.0+)
SELECT user_id FROM premium_customers
INTERSECT
SELECT user_id FROM active_users;

-- EXCEPT (разность - эмуляция в MySQL)
SELECT user_id FROM all_users
WHERE user_id NOT IN (
    SELECT user_id FROM inactive_users
);

-- Альтернатива EXCEPT с LEFT JOIN
SELECT a.user_id
FROM all_users a
LEFT JOIN inactive_users i ON a.user_id = i.user_id
WHERE i.user_id IS NULL;
```

### Практические примеры UNION

```sql
-- Объединение отчетов по продажам
SELECT
    'online' AS channel,
    COUNT(*) AS orders,
    SUM(total_amount) AS revenue
FROM online_orders
WHERE order_date >= '2024-01-01'
UNION ALL
SELECT
    'store' AS channel,
    COUNT(*) AS orders,
    SUM(total_amount) AS revenue
FROM store_orders
WHERE order_date >= '2024-01-01';

-- Поиск по нескольким таблицам
SELECT id, name, 'user' AS entity_type FROM users WHERE name LIKE ?
UNION ALL
SELECT id, name, 'product' AS entity_type FROM products WHERE name LIKE ?
UNION ALL
SELECT id, name, 'category' AS entity_type FROM categories WHERE name LIKE ?
ORDER BY entity_type, name;
```

### UNION в Java

```java
@Repository
public class UnionQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // UNION для объединения результатов
    public List<SearchResult> searchAcrossEntities(String searchTerm) {
        String sql = """
            SELECT id, name, 'user' AS entity_type, email AS additional_info
            FROM users
            WHERE name LIKE ? OR email LIKE ?
            UNION ALL
            SELECT id, name, 'product' AS entity_type, category AS additional_info
            FROM products
            WHERE name LIKE ?
            UNION ALL
            SELECT id, name, 'category' AS entity_type, NULL AS additional_info
            FROM categories
            WHERE name LIKE ?
            ORDER BY entity_type, name
            """;

        String searchPattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql,
            new Object[]{searchPattern, searchPattern, searchPattern, searchPattern},
            (rs, rowNum) -> new SearchResult(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("entity_type"),
                rs.getString("additional_info")
            )
        );
    }

    // UNION для отчетов по каналам продаж
    public List<SalesChannelReport> getSalesChannelReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT
                'online' AS channel,
                COUNT(*) AS order_count,
                SUM(total_amount) AS total_revenue,
                AVG(total_amount) AS avg_order_value
            FROM online_orders
            WHERE order_date BETWEEN ? AND ?
            UNION ALL
            SELECT
                'store' AS channel,
                COUNT(*) AS order_count,
                SUM(total_amount) AS total_revenue,
                AVG(total_amount) AS avg_order_value
            FROM store_orders
            WHERE order_date BETWEEN ? AND ?
            UNION ALL
            SELECT
                'total' AS channel,
                COUNT(*) AS order_count,
                SUM(total_amount) AS total_revenue,
                AVG(total_amount) AS avg_order_value
            FROM (
                SELECT total_amount FROM online_orders WHERE order_date BETWEEN ? AND ?
                UNION ALL
                SELECT total_amount FROM store_orders WHERE order_date BETWEEN ? AND ?
            ) combined_orders
            """;

        Date start = Date.valueOf(startDate);
        Date end = Date.valueOf(endDate);

        return jdbcTemplate.query(sql,
            new Object[]{start, end, start, end, start, end, start, end},
            (rs, rowNum) -> new SalesChannelReport(
                rs.getString("channel"),
                rs.getInt("order_count"),
                rs.getBigDecimal("total_revenue"),
                rs.getBigDecimal("avg_order_value")
            )
        );
    }

    // EXCEPT эмуляция
    public List<Long> findActiveUserIds() {
        String sql = """
            SELECT user_id FROM all_users
            WHERE user_id NOT IN (
                SELECT user_id FROM inactive_users
            )
            """;

        return jdbcTemplate.queryForList(sql, Long.class);
    }

    // INTERSECT эмуляция
    public List<Long> findPremiumActiveUsers() {
        String sql = """
            SELECT user_id FROM premium_customers
            WHERE user_id IN (
                SELECT user_id FROM active_users
            )
            """;

        return jdbcTemplate.queryForList(sql, Long.class);
    }

    // Сложный UNION с агрегацией
    public List<MonthlyReport> getMonthlyReports(int year) {
        String sql = """
            SELECT
                month,
                'orders' AS metric_type,
                COUNT(*) AS value
            FROM orders
            WHERE YEAR(order_date) = ?
            GROUP BY MONTH(order_date)
            UNION ALL
            SELECT
                month,
                'revenue' AS metric_type,
                SUM(total_amount) AS value
            FROM orders
            WHERE YEAR(order_date) = ?
            GROUP BY MONTH(order_date)
            UNION ALL
            SELECT
                month,
                'customers' AS metric_type,
                COUNT(DISTINCT user_id) AS value
            FROM orders
            WHERE YEAR(order_date) = ?
            GROUP BY MONTH(order_date)
            ORDER BY month, metric_type
            """;

        return jdbcTemplate.query(sql, new Object[]{year, year, year},
            (rs, rowNum) -> new MonthlyReport(
                rs.getInt("month"),
                rs.getString("metric_type"),
                rs.getBigDecimal("value")
            )
        );
    }
}

// Вспомогательные классы
class SearchResult {
    private Long id;
    private String name;
    private String entityType;
    private String additionalInfo;
    // constructor, getters, setters
}

class SalesChannelReport {
    private String channel;
    private Integer orderCount;
    private BigDecimal totalRevenue;
    private BigDecimal avgOrderValue;
    // constructor, getters, setters
}

class MonthlyReport {
    private Integer month;
    private String metricType;
    private BigDecimal value;
    // constructor, getters, setters
}
```

## Window Functions

### Основные оконные функции

```sql
-- ROW_NUMBER, RANK, DENSE_RANK
SELECT
    name,
    salary,
    ROW_NUMBER() OVER (ORDER BY salary DESC) AS row_num,
    RANK() OVER (ORDER BY salary DESC) AS rank_num,
    DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank_num
FROM employees;

-- PARTITION BY - оконные функции по группам
SELECT
    department,
    name,
    salary,
    AVG(salary) OVER (PARTITION BY department) AS dept_avg_salary,
    MAX(salary) OVER (PARTITION BY department) AS dept_max_salary
FROM employees;
```

### Продвинутые оконные функции

```sql
-- LAG и LEAD - доступ к предыдущим/следующим строкам
SELECT
    name,
    order_date,
    total_amount,
    LAG(total_amount) OVER (PARTITION BY customer_id ORDER BY order_date) AS prev_order_amount,
    LEAD(total_amount) OVER (PARTITION BY customer_id ORDER BY order_date) AS next_order_amount,
    total_amount - LAG(total_amount) OVER (PARTITION BY customer_id ORDER BY order_date) AS amount_change
FROM orders;

-- FIRST_VALUE и LAST_VALUE
SELECT
    category,
    name,
    price,
    FIRST_VALUE(name) OVER (PARTITION BY category ORDER BY price) AS cheapest_product,
    LAST_VALUE(name) OVER (PARTITION BY category ORDER BY price
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS most_expensive_product
FROM products;
```

### Аналитические функции

```sql
-- PERCENT_RANK и CUME_DIST
SELECT
    name,
    salary,
    PERCENT_RANK() OVER (ORDER BY salary) AS salary_percentile,
    CUME_DIST() OVER (ORDER BY salary) AS cumulative_distribution
FROM employees;

-- NTILE - разделение на группы
SELECT
    name,
    salary,
    NTILE(4) OVER (ORDER BY salary) AS salary_quartile
FROM employees;
```

### Window Functions в Java

```java
@Repository
public class WindowFunctionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Ранжирование сотрудников
    public List<EmployeeRanking> getEmployeeRankings() {
        String sql = """
            SELECT
                name,
                department,
                salary,
                ROW_NUMBER() OVER (ORDER BY salary DESC) AS overall_rank,
                RANK() OVER (ORDER BY salary DESC) AS salary_rank,
                DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank,
                NTILE(4) OVER (ORDER BY salary DESC) AS salary_quartile
            FROM employees
            ORDER BY salary DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new EmployeeRanking(
                rs.getString("name"),
                rs.getString("department"),
                rs.getBigDecimal("salary"),
                rs.getInt("overall_rank"),
                rs.getInt("salary_rank"),
                rs.getInt("dense_rank"),
                rs.getInt("salary_quartile")
            )
        );
    }

    // Анализ заказов по клиентам
    public List<CustomerOrderAnalysis> getCustomerOrderAnalysis() {
        String sql = """
            SELECT
                c.name AS customer_name,
                o.order_date,
                o.total_amount,
                ROW_NUMBER() OVER (PARTITION BY c.id ORDER BY o.order_date) AS order_number,
                LAG(o.total_amount) OVER (PARTITION BY c.id ORDER BY o.order_date) AS prev_amount,
                LEAD(o.total_amount) OVER (PARTITION BY c.id ORDER BY o.order_date) AS next_amount,
                AVG(o.total_amount) OVER (PARTITION BY c.id) AS customer_avg_amount,
                SUM(o.total_amount) OVER (PARTITION BY c.id ORDER BY o.order_date) AS running_total
            FROM customers c
            JOIN orders o ON c.id = o.customer_id
            ORDER BY c.name, o.order_date
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new CustomerOrderAnalysis(
                rs.getString("customer_name"),
                rs.getDate("order_date").toLocalDate(),
                rs.getBigDecimal("total_amount"),
                rs.getInt("order_number"),
                rs.getBigDecimal("prev_amount"),
                rs.getBigDecimal("next_amount"),
                rs.getBigDecimal("customer_avg_amount"),
                rs.getBigDecimal("running_total")
            )
        );
    }

    // Скользящие средние и статистика
    public List<SalesAnalytics> getSalesAnalytics() {
        String sql = """
            SELECT
                DATE(order_date) AS sale_date,
                SUM(total_amount) AS daily_sales,
                AVG(SUM(total_amount)) OVER (
                    ORDER BY DATE(order_date)
                    ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
                ) AS moving_avg_7day,
                SUM(SUM(total_amount)) OVER (
                    ORDER BY DATE(order_date)
                    ROWS BETWEEN 29 PRECEDING AND CURRENT ROW
                ) / 30 AS monthly_avg,
                PERCENT_RANK() OVER (ORDER BY SUM(total_amount)) AS sales_percentile
            FROM orders
            WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
            GROUP BY DATE(order_date)
            ORDER BY sale_date
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new SalesAnalytics(
                rs.getDate("sale_date").toLocalDate(),
                rs.getBigDecimal("daily_sales"),
                rs.getBigDecimal("moving_avg_7day"),
                rs.getBigDecimal("monthly_avg"),
                rs.getDouble("sales_percentile")
            )
        );
    }

    // Анализ продуктов по категориям
    public List<ProductCategoryAnalysis> getProductCategoryAnalysis() {
        String sql = """
            SELECT
                category,
                name,
                price,
                COUNT(*) OVER (PARTITION BY category) AS products_in_category,
                AVG(price) OVER (PARTITION BY category) AS category_avg_price,
                MIN(price) OVER (PARTITION BY category) AS category_min_price,
                MAX(price) OVER (PARTITION BY category) AS category_max_price,
                RANK() OVER (PARTITION BY category ORDER BY price DESC) AS price_rank_in_category,
                price - AVG(price) OVER (PARTITION BY category) AS price_diff_from_avg
            FROM products
            ORDER BY category, price DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new ProductCategoryAnalysis(
                rs.getString("category"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getInt("products_in_category"),
                rs.getBigDecimal("category_avg_price"),
                rs.getBigDecimal("category_min_price"),
                rs.getBigDecimal("category_max_price"),
                rs.getInt("price_rank_in_category"),
                rs.getBigDecimal("price_diff_from_avg")
            )
        );
    }
}

// Вспомогательные классы
class EmployeeRanking {
    private String name;
    private String department;
    private BigDecimal salary;
    private Integer overallRank;
    private Integer salaryRank;
    private Integer denseRank;
    private Integer salaryQuartile;
    // constructor, getters, setters
}

class CustomerOrderAnalysis {
    private String customerName;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private Integer orderNumber;
    private BigDecimal prevAmount;
    private BigDecimal nextAmount;
    private BigDecimal customerAvgAmount;
    private BigDecimal runningTotal;
    // constructor, getters, setters
}

class SalesAnalytics {
    private LocalDate saleDate;
    private BigDecimal dailySales;
    private BigDecimal movingAvg7Day;
    private BigDecimal monthlyAvg;
    private Double salesPercentile;
    // constructor, getters, setters
}

class ProductCategoryAnalysis {
    private String category;
    private String name;
    private BigDecimal price;
    private Integer productsInCategory;
    private BigDecimal categoryAvgPrice;
    private BigDecimal categoryMinPrice;
    private BigDecimal categoryMaxPrice;
    private Integer priceRankInCategory;
    private BigDecimal priceDiffFromAvg;
    // constructor, getters, setters
}
```

## Common Table Expressions (CTE)

### Рекурсивные CTE

```sql
-- Рекурсивная CTE для иерархии сотрудников
WITH RECURSIVE employee_hierarchy AS (
    -- Базовый случай: топ-менеджеры (без менеджера)
    SELECT
        id,
        name,
        manager_id,
        1 AS level,
        CAST(name AS CHAR(200)) AS path
    FROM employees
    WHERE manager_id IS NULL

    UNION ALL

    -- Рекурсивный случай: подчиненные
    SELECT
        e.id,
        e.name,
        e.manager_id,
        eh.level + 1,
        CONCAT(eh.path, ' > ', e.name)
    FROM employees e
    JOIN employee_hierarchy eh ON e.manager_id = eh.id
)
SELECT * FROM employee_hierarchy ORDER BY level, path;
```

### Не рекурсивные CTE

```sql
-- CTE для сложной агрегации
WITH monthly_sales AS (
    SELECT
        DATE_FORMAT(order_date, '%Y-%m') AS month,
        COUNT(*) AS order_count,
        SUM(total_amount) AS total_revenue,
        AVG(total_amount) AS avg_order_value
    FROM orders
    WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
    GROUP BY DATE_FORMAT(order_date, '%Y-%m')
),
customer_stats AS (
    SELECT
        DATE_FORMAT(created_at, '%Y-%m') AS month,
        COUNT(*) AS new_customers
    FROM customers
    WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
    GROUP BY DATE_FORMAT(created_at, '%Y-%m')
)
SELECT
    ms.month,
    ms.order_count,
    ms.total_revenue,
    ms.avg_order_value,
    COALESCE(cs.new_customers, 0) AS new_customers,
    CASE
        WHEN COALESCE(cs.new_customers, 0) > 0
        THEN ms.total_revenue / cs.new_customers
        ELSE 0
    END AS revenue_per_new_customer
FROM monthly_sales ms
LEFT JOIN customer_stats cs ON ms.month = cs.month
ORDER BY ms.month;
```

### CTE в Java

```java
@Repository
public class CteQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Рекурсивная CTE для иерархии
    public List<EmployeeHierarchy> getEmployeeHierarchy() {
        String sql = """
            WITH RECURSIVE employee_hierarchy AS (
                SELECT
                    id,
                    name,
                    manager_id,
                    1 AS level,
                    CAST(name AS CHAR(500)) AS path
                FROM employees
                WHERE manager_id IS NULL

                UNION ALL

                SELECT
                    e.id,
                    e.name,
                    e.manager_id,
                    eh.level + 1,
                    CONCAT(eh.path, ' > ', e.name)
                FROM employees e
                JOIN employee_hierarchy eh ON e.manager_id = eh.id
            )
            SELECT id, name, manager_id, level, path
            FROM employee_hierarchy
            ORDER BY level, path
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new EmployeeHierarchy(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("manager_id"),
                rs.getInt("level"),
                rs.getString("path")
            )
        );
    }

    // CTE для анализа категорий продуктов
    public List<CategoryAnalysis> getCategoryAnalysis() {
        String sql = """
            WITH category_sales AS (
                SELECT
                    p.category,
                    COUNT(oi.id) AS items_sold,
                    SUM(oi.quantity * oi.price) AS revenue,
                    AVG(oi.price) AS avg_sale_price
                FROM products p
                JOIN order_items oi ON p.id = oi.product_id
                JOIN orders o ON oi.order_id = o.id
                WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
                GROUP BY p.category
            ),
            category_stats AS (
                SELECT
                    category,
                    COUNT(*) AS product_count,
                    AVG(price) AS avg_product_price,
                    MIN(price) AS min_price,
                    MAX(price) AS max_price
                FROM products
                GROUP BY category
            )
            SELECT
                cs.category,
                cs.product_count,
                cs.avg_product_price,
                css.items_sold,
                css.revenue,
                css.avg_sale_price,
                CASE
                    WHEN css.items_sold > 0 THEN css.revenue / css.items_sold
                    ELSE 0
                END AS avg_items_per_sale
            FROM category_stats cs
            LEFT JOIN category_sales css ON cs.category = css.category
            ORDER BY css.revenue DESC NULLS LAST
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new CategoryAnalysis(
                rs.getString("category"),
                rs.getInt("product_count"),
                rs.getBigDecimal("avg_product_price"),
                rs.getInt("items_sold"),
                rs.getBigDecimal("revenue"),
                rs.getBigDecimal("avg_sale_price"),
                rs.getBigDecimal("avg_items_per_sale")
            )
        );
    }

    // CTE для финансового отчета
    public List<FinancialReport> getFinancialReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
            WITH revenue_data AS (
                SELECT
                    DATE_FORMAT(order_date, '%Y-%m') AS month,
                    SUM(total_amount) AS revenue,
                    COUNT(*) AS orders
                FROM orders
                WHERE order_date BETWEEN ? AND ?
                GROUP BY DATE_FORMAT(order_date, '%Y-%m')
            ),
            expense_data AS (
                SELECT
                    DATE_FORMAT(expense_date, '%Y-%m') AS month,
                    SUM(amount) AS expenses
                FROM expenses
                WHERE expense_date BETWEEN ? AND ?
                GROUP BY DATE_FORMAT(expense_date, '%Y-%m')
            ),
            monthly_report AS (
                SELECT
                    COALESCE(r.month, e.month) AS month,
                    COALESCE(r.revenue, 0) AS revenue,
                    COALESCE(e.expenses, 0) AS expenses,
                    COALESCE(r.orders, 0) AS orders
                FROM revenue_data r
                FULL OUTER JOIN expense_data e ON r.month = e.month
            )
            SELECT
                month,
                revenue,
                expenses,
                revenue - expenses AS profit,
                CASE
                    WHEN expenses > 0 THEN ((revenue - expenses) / expenses) * 100
                    ELSE NULL
                END AS profit_margin_percent,
                orders
            FROM monthly_report
            ORDER BY month
            """;

        Date start = Date.valueOf(startDate);
        Date end = Date.valueOf(endDate);

        return jdbcTemplate.query(sql, new Object[]{start, end, start, end},
            (rs, rowNum) -> new FinancialReport(
                rs.getString("month"),
                rs.getBigDecimal("revenue"),
                rs.getBigDecimal("expenses"),
                rs.getBigDecimal("profit"),
                rs.getBigDecimal("profit_margin_percent"),
                rs.getInt("orders")
            )
        );
    }
}

// Вспомогательные классы
class EmployeeHierarchy {
    private Long id;
    private String name;
    private Long managerId;
    private Integer level;
    private String path;
    // constructor, getters, setters
}

class CategoryAnalysis {
    private String category;
    private Integer productCount;
    private BigDecimal avgProductPrice;
    private Integer itemsSold;
    private BigDecimal revenue;
    private BigDecimal avgSalePrice;
    private BigDecimal avgItemsPerSale;
    // constructor, getters, setters
}

class FinancialReport {
    private String month;
    private BigDecimal revenue;
    private BigDecimal expenses;
    private BigDecimal profit;
    private BigDecimal profitMarginPercent;
    private Integer orders;
    // constructor, getters, setters
}
```

## Рекурсивные запросы

### Рекурсивные CTE для деревьев

```sql
-- Рекурсивный обход дерева категорий
WITH RECURSIVE category_tree AS (
    -- Корневые категории
    SELECT
        id,
        name,
        parent_id,
        0 AS level,
        CAST(name AS CHAR(500)) AS path
    FROM categories
    WHERE parent_id IS NULL

    UNION ALL

    -- Дочерние категории
    SELECT
        c.id,
        c.name,
        c.parent_id,
        ct.level + 1,
        CONCAT(ct.path, ' > ', c.name)
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.id
)
SELECT
    id,
    LPAD('', level * 2, ' ') || name AS indented_name,
    level,
    path
FROM category_tree
ORDER BY path;
```

### Рекурсивные запросы для последовательностей

```sql
-- Генерация последовательности дат
WITH RECURSIVE date_series AS (
    SELECT DATE('2024-01-01') AS date_value
    UNION ALL
    SELECT DATE_ADD(date_value, INTERVAL 1 DAY)
    FROM date_series
    WHERE date_value < DATE('2024-12-31')
)
SELECT
    date_value,
    DAYNAME(date_value) AS day_name,
    WEEK(date_value) AS week_number,
    MONTH(date_value) AS month_number
FROM date_series;
```

### Рекурсивные запросы в Java

```java
@Repository
public class RecursiveQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Рекурсивное построение дерева категорий
    public List<CategoryTreeNode> getCategoryTree() {
        String sql = """
            WITH RECURSIVE category_tree AS (
                SELECT
                    id,
                    name,
                    parent_id,
                    0 AS level,
                    CAST(name AS CHAR(1000)) AS path,
                    CAST(id AS CHAR(200)) AS id_path
                FROM categories
                WHERE parent_id IS NULL

                UNION ALL

                SELECT
                    c.id,
                    c.name,
                    c.parent_id,
                    ct.level + 1,
                    CONCAT(ct.path, ' > ', c.name),
                    CONCAT(ct.id_path, ',', c.id)
                FROM categories c
                JOIN category_tree ct ON c.parent_id = ct.id
            )
            SELECT
                id,
                name,
                parent_id,
                level,
                path,
                id_path
            FROM category_tree
            ORDER BY path
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new CategoryTreeNode(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getLong("parent_id"),
                rs.getInt("level"),
                rs.getString("path"),
                rs.getString("id_path")
            )
        );
    }

    // Рекурсивный расчет итогов по иерархии
    public List<DepartmentSummary> getDepartmentHierarchySummary() {
        String sql = """
            WITH RECURSIVE dept_hierarchy AS (
                SELECT
                    id,
                    name,
                    parent_id,
                    0 AS level
                FROM departments
                WHERE parent_id IS NULL

                UNION ALL

                SELECT
                    d.id,
                    d.name,
                    d.parent_id,
                    dh.level + 1
                FROM departments d
                JOIN dept_hierarchy dh ON d.parent_id = dh.id
            ),
            dept_stats AS (
                SELECT
                    d.id,
                    COUNT(e.id) AS employee_count,
                    COALESCE(SUM(e.salary), 0) AS total_salary,
                    COALESCE(AVG(e.salary), 0) AS avg_salary
                FROM departments d
                LEFT JOIN employees e ON d.id = e.department_id
                GROUP BY d.id
            )
            SELECT
                dh.id,
                dh.name,
                dh.level,
                ds.employee_count,
                ds.total_salary,
                ds.avg_salary
            FROM dept_hierarchy dh
            JOIN dept_stats ds ON dh.id = ds.id
            ORDER BY dh.level, dh.name
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new DepartmentSummary(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("level"),
                rs.getInt("employee_count"),
                rs.getBigDecimal("total_salary"),
                rs.getBigDecimal("avg_salary")
            )
        );
    }

    // Рекурсивное построение материализованного пути
    public List<MaterializedPathNode> getMaterializedPathTree() {
        String sql = """
            WITH RECURSIVE materialized_path AS (
                SELECT
                    id,
                    name,
                    CAST(LPAD(id, 10, '0') AS CHAR(100)) AS path
                FROM categories
                WHERE parent_id IS NULL

                UNION ALL

                SELECT
                    c.id,
                    c.name,
                    CONCAT(mp.path, '.', LPAD(c.id, 10, '0'))
                FROM categories c
                JOIN materialized_path mp ON c.parent_id = mp.id
            )
            SELECT
                id,
                name,
                path,
                LENGTH(path) - LENGTH(REPLACE(path, '.', '')) AS depth
            FROM materialized_path
            ORDER BY path
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new MaterializedPathNode(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("path"),
                rs.getInt("depth")
            )
        );
    }
}

// Вспомогательные классы
class CategoryTreeNode {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private String path;
    private String idPath;
    // constructor, getters, setters
}

class DepartmentSummary {
    private Long id;
    private String name;
    private Integer level;
    private Integer employeeCount;
    private BigDecimal totalSalary;
    private BigDecimal avgSalary;
    // constructor, getters, setters
}

class MaterializedPathNode {
    private Long id;
    private String name;
    private String path;
    private Integer depth;
    // constructor, getters, setters
}
```

## JSON функции

### Работа с JSON в MySQL 8.0+

```sql
-- Создание таблицы с JSON полем
CREATE TABLE user_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    profile_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
);

-- Вставка JSON данных
INSERT INTO user_profiles (user_id, profile_data) VALUES
(1, '{
    "name": "John Doe",
    "age": 30,
    "preferences": {
        "theme": "dark",
        "notifications": true,
        "language": "en"
    },
    "tags": ["developer", "mysql", "java"],
    "addresses": [
        {"type": "home", "city": "New York"},
        {"type": "work", "city": "Boston"}
    ]
}');

-- JSON функции для извлечения данных
SELECT
    user_id,
    JSON_EXTRACT(profile_data, '$.name') AS name,
    JSON_EXTRACT(profile_data, '$.age') AS age,
    JSON_EXTRACT(profile_data, '$.preferences.theme') AS theme,
    JSON_EXTRACT(profile_data, '$.tags[0]') AS first_tag,
    JSON_EXTRACT(profile_data, '$.addresses[0].city') AS home_city
FROM user_profiles;
```

### Продвинутые JSON операции

```sql
-- Поиск в JSON массивах
SELECT * FROM user_profiles
WHERE JSON_CONTAINS(profile_data, '"developer"', '$.tags');

-- Поиск по вложенным объектам
SELECT * FROM user_profiles
WHERE JSON_EXTRACT(profile_data, '$.preferences.notifications') = true;

-- Агрегация JSON данных
SELECT
    JSON_EXTRACT(profile_data, '$.preferences.theme') AS theme,
    COUNT(*) AS user_count
FROM user_profiles
GROUP BY JSON_EXTRACT(profile_data, '$.preferences.theme');

-- Обновление JSON полей
UPDATE user_profiles
SET profile_data = JSON_SET(
    profile_data,
    '$.age', 31,
    '$.preferences.notifications', false,
    '$.last_updated', NOW()
)
WHERE user_id = 1;

-- Добавление элементов в JSON массив
UPDATE user_profiles
SET profile_data = JSON_ARRAY_APPEND(
    profile_data,
    '$.tags', 'spring-boot'
)
WHERE user_id = 1;
```

### JSON в запросах и индексах

```sql
-- Создание индекса на JSON поле
ALTER TABLE user_profiles
ADD INDEX idx_profile_age ((JSON_EXTRACT(profile_data, '$.age')));

-- Создание функционального индекса
ALTER TABLE user_profiles
ADD INDEX idx_profile_theme ((JSON_UNQUOTE(JSON_EXTRACT(profile_data, '$.preferences.theme'))));

-- JSON в WHERE условиях
SELECT * FROM user_profiles
WHERE JSON_EXTRACT(profile_data, '$.age') BETWEEN 25 AND 35
  AND JSON_CONTAINS(profile_data, '"dark"', '$.preferences.theme');

-- JSON в ORDER BY
SELECT
    user_id,
    JSON_EXTRACT(profile_data, '$.name') AS name,
    JSON_EXTRACT(profile_data, '$.age') AS age
FROM user_profiles
ORDER BY JSON_EXTRACT(profile_data, '$.age') DESC;
```

### JSON в Java

```java
@Repository
public class JsonQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Сохранение JSON профиля
    public void saveUserProfile(Long userId, UserProfile profile) {
        String jsonData = objectMapper.writeValueAsString(profile);

        String sql = """
            INSERT INTO user_profiles (user_id, profile_data)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE profile_data = VALUES(profile_data)
            """;

        jdbcTemplate.update(sql, userId, jsonData);
    }

    // Извлечение данных из JSON
    public List<UserProfileSummary> getUserProfileSummaries() {
        String sql = """
            SELECT
                user_id,
                JSON_UNQUOTE(JSON_EXTRACT(profile_data, '$.name')) AS name,
                JSON_EXTRACT(profile_data, '$.age') AS age,
                JSON_UNQUOTE(JSON_EXTRACT(profile_data, '$.preferences.theme')) AS theme,
                JSON_LENGTH(profile_data, '$.tags') AS tag_count
            FROM user_profiles
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new UserProfileSummary(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("theme"),
                rs.getInt("tag_count")
            )
        );
    }

    // Поиск по JSON критериям
    public List<Long> findUsersByPreferences(String theme, boolean notifications) {
        String sql = """
            SELECT user_id FROM user_profiles
            WHERE JSON_UNQUOTE(JSON_EXTRACT(profile_data, '$.preferences.theme')) = ?
              AND JSON_EXTRACT(profile_data, '$.preferences.notifications') = ?
            """;

        return jdbcTemplate.query(sql,
            new Object[]{theme, notifications},
            (rs, rowNum) -> rs.getLong("user_id"));
    }

    // Работа с JSON массивами
    public List<String> getAllUserTags() {
        String sql = """
            SELECT DISTINCT
                JSON_UNQUOTE(JSON_EXTRACT(tags.value, '$')) AS tag
            FROM user_profiles,
                 JSON_TABLE(profile_data, '$.tags[*]' COLUMNS (value JSON PATH '$')) AS tags
            ORDER BY tag
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Агрегация по JSON данным
    public Map<String, Integer> getThemeDistribution() {
        String sql = """
            SELECT
                JSON_UNQUOTE(JSON_EXTRACT(profile_data, '$.preferences.theme')) AS theme,
                COUNT(*) AS count
            FROM user_profiles
            GROUP BY JSON_EXTRACT(profile_data, '$.preferences.theme')
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
        return results.stream()
            .collect(Collectors.toMap(
                row -> (String) row.get("theme"),
                row -> ((Number) row.get("count")).intValue()
            ));
    }

    // Обновление JSON полей
    public void updateUserPreferences(Long userId, String theme, Boolean notifications) {
        String sql = """
            UPDATE user_profiles
            SET profile_data = JSON_SET(
                profile_data,
                '$.preferences.theme', ?,
                '$.preferences.notifications', ?,
                '$.last_updated', NOW()
            )
            WHERE user_id = ?
            """;

        jdbcTemplate.update(sql, theme, notifications, userId);
    }

    // Добавление тега в массив
    public void addUserTag(Long userId, String newTag) {
        String sql = """
            UPDATE user_profiles
            SET profile_data = JSON_ARRAY_APPEND(
                profile_data,
                '$.tags', ?
            )
            WHERE user_id = ?
            """;

        jdbcTemplate.update(sql, newTag, userId);
    }

    // Удаление тега из массива
    public void removeUserTag(Long userId, String tagToRemove) {
        String sql = """
            UPDATE user_profiles
            SET profile_data = (
                SELECT JSON_REMOVE(
                    up.profile_data,
                    CONCAT('$.tags[', JSON_SEARCH(up.profile_data, 'one', ?, NULL, '$.tags'), ']')
                )
                FROM user_profiles up
                WHERE up.user_id = user_profiles.user_id
            )
            WHERE user_id = ?
              AND JSON_CONTAINS(profile_data, ?, '$.tags')
            """;

        jdbcTemplate.update(sql, tagToRemove, userId, "\"" + tagToRemove + "\"");
    }
}

// Вспомогательные классы
class UserProfileSummary {
    private Long userId;
    private String name;
    private Integer age;
    private String theme;
    private Integer tagCount;
    // constructor, getters, setters
}
```

## Полнотекстовый поиск

### Настройка полнотекстового поиска

```sql
-- Создание таблицы с FULLTEXT индексом
CREATE TABLE articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author_id INT,
    published_date DATETIME,
    tags VARCHAR(500),

    FULLTEXT INDEX idx_content (title, content),
    FULLTEXT INDEX idx_tags (tags),
    INDEX idx_author_published (author_id, published_date)
);

-- Вставка тестовых данных
INSERT INTO articles (title, content, author_id, tags) VALUES
('MySQL Performance Tuning', 'Learn how to optimize MySQL queries and improve database performance...', 1, 'mysql, performance, optimization'),
('Spring Boot Integration', 'Integrating Spring Boot with MySQL for enterprise applications...', 2, 'spring, boot, mysql, java'),
('Database Design Patterns', 'Common patterns for designing scalable databases...', 1, 'database, design, patterns');
```

### Полнотекстовые запросы

```sql
-- Базовый полнотекстовый поиск
SELECT
    id,
    title,
    MATCH(title, content) AGAINST('mysql performance' IN NATURAL LANGUAGE MODE) AS relevance
FROM articles
WHERE MATCH(title, content) AGAINST('mysql performance' IN NATURAL LANGUAGE MODE)
ORDER BY relevance DESC;

-- Поиск с булевыми операторами
SELECT * FROM articles
WHERE MATCH(title, content) AGAINST('+mysql -postgres' IN BOOLEAN MODE);

-- Поиск по фразе
SELECT * FROM articles
WHERE MATCH(title, content) AGAINST('"database design"' IN BOOLEAN MODE);

-- Поиск с расширенным модификатором
SELECT * FROM articles
WHERE MATCH(title, content) AGAINST('database' WITH QUERY EXPANSION);

-- Поиск по нескольким полям
SELECT
    id,
    title,
    MATCH(title) AGAINST('mysql' IN NATURAL LANGUAGE MODE) AS title_relevance,
    MATCH(content) AGAINST('mysql' IN NATURAL LANGUAGE MODE) AS content_relevance
FROM articles
WHERE MATCH(title, content) AGAINST('mysql' IN NATURAL LANGUAGE MODE);
```

### Расширенные возможности полнотекстового поиска

```sql
-- Настройка стоп-слов
CREATE FULLTEXT INDEX idx_content_custom
ON articles(title, content)
WITH PARSER ngram;

-- Поиск с релевантностью
SELECT
    id,
    title,
    MATCH(title, content) AGAINST('mysql performance' IN NATURAL LANGUAGE MODE) AS score,
    ROUND(MATCH(title, content) AGAINST('mysql performance' IN NATURAL LANGUAGE MODE), 3) AS rounded_score
FROM articles
WHERE MATCH(title, content) AGAINST('mysql performance' IN NATURAL LANGUAGE MODE)
ORDER BY score DESC;

-- Группировка результатов
SELECT
    CASE
        WHEN score > 0.8 THEN 'High'
        WHEN score > 0.5 THEN 'Medium'
        ELSE 'Low'
    END AS relevance_category,
    COUNT(*) AS article_count
FROM (
    SELECT MATCH(title, content) AGAINST('mysql' IN NATURAL LANGUAGE MODE) AS score
    FROM articles
) scores
GROUP BY relevance_category;
```

### Полнотекстовый поиск в Java

```java
@Repository
public class FullTextSearchRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Базовый полнотекстовый поиск
    public List<ArticleSearchResult> searchArticles(String searchTerm) {
        String sql = """
            SELECT
                id,
                title,
                LEFT(content, 200) AS content_preview,
                author_id,
                MATCH(title, content) AGAINST(? IN NATURAL LANGUAGE MODE) AS relevance_score
            FROM articles
            WHERE MATCH(title, content) AGAINST(? IN NATURAL LANGUAGE MODE)
            ORDER BY relevance_score DESC
            LIMIT 20
            """;

        return jdbcTemplate.query(sql, new Object[]{searchTerm, searchTerm},
            (rs, rowNum) -> new ArticleSearchResult(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content_preview"),
                rs.getLong("author_id"),
                rs.getDouble("relevance_score")
            )
        );
    }

    // Расширенный поиск с фильтрами
    public List<ArticleSearchResult> advancedSearch(ArticleSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                id,
                title,
                LEFT(content, 300) AS content_preview,
                author_id,
                published_date,
                MATCH(title, content) AGAINST(? IN BOOLEAN MODE) AS relevance_score
            FROM articles
            WHERE MATCH(title, content) AGAINST(? IN BOOLEAN MODE)
            """);

        List<Object> params = new ArrayList<>();
        params.add(criteria.getSearchTerm());
        params.add(criteria.getSearchTerm());

        // Добавление фильтров
        if (criteria.getAuthorId() != null) {
            sql.append(" AND author_id = ?");
            params.add(criteria.getAuthorId());
        }

        if (criteria.getStartDate() != null) {
            sql.append(" AND published_date >= ?");
            params.add(Date.valueOf(criteria.getStartDate()));
        }

        if (criteria.getEndDate() != null) {
            sql.append(" AND published_date <= ?");
            params.add(Date.valueOf(criteria.getEndDate()));
        }

        sql.append(" ORDER BY relevance_score DESC LIMIT ?");
        params.add(criteria.getLimit() != null ? criteria.getLimit() : 50);

        return jdbcTemplate.query(sql.toString(), params.toArray(),
            (rs, rowNum) -> new ArticleSearchResult(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content_preview"),
                rs.getLong("author_id"),
                rs.getDouble("relevance_score")
            )
        );
    }

    // Поиск по тегам
    public List<ArticleSearchResult> searchByTags(List<String> tags) {
        String tagSearch = String.join(" ", tags);
        String sql = """
            SELECT
                id,
                title,
                tags,
                MATCH(tags) AGAINST(? IN BOOLEAN MODE) AS tag_relevance
            FROM articles
            WHERE MATCH(tags) AGAINST(? IN BOOLEAN MODE)
            ORDER BY tag_relevance DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{tagSearch, tagSearch},
            (rs, rowNum) -> new ArticleSearchResult(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("tags"),
                0L,
                rs.getDouble("tag_relevance")
            )
        );
    }

    // Комбинированный поиск
    public List<ArticleSearchResult> combinedSearch(String contentTerm, String tagTerm) {
        String sql = """
            SELECT
                id,
                title,
                LEFT(content, 200) AS content_preview,
                tags,
                (MATCH(title, content) AGAINST(? IN NATURAL LANGUAGE MODE) +
                 MATCH(tags) AGAINST(? IN NATURAL LANGUAGE MODE)) / 2 AS combined_score
            FROM articles
            WHERE MATCH(title, content) AGAINST(? IN NATURAL LANGUAGE MODE)
               OR MATCH(tags) AGAINST(? IN NATURAL LANGUAGE MODE)
            ORDER BY combined_score DESC
            LIMIT 20
            """;

        return jdbcTemplate.query(sql,
            new Object[]{contentTerm, tagTerm, contentTerm, tagTerm},
            (rs, rowNum) -> new ArticleSearchResult(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content_preview") + " | Tags: " + rs.getString("tags"),
                0L,
                rs.getDouble("combined_score")
            )
        );
    }

    // Анализ поисковых запросов
    public List<SearchAnalytics> getSearchAnalytics() {
        String sql = """
            SELECT
                DATE(created_at) AS search_date,
                COUNT(*) AS search_count,
                AVG(relevance_score) AS avg_relevance
            FROM search_logs
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY DATE(created_at)
            ORDER BY search_date
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new SearchAnalytics(
                rs.getDate("search_date").toLocalDate(),
                rs.getInt("search_count"),
                rs.getDouble("avg_relevance")
            )
        );
    }
}

// Вспомогательные классы
class ArticleSearchResult {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Double relevanceScore;
    // constructor, getters, setters
}

class ArticleSearchCriteria {
    private String searchTerm;
    private Long authorId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer limit;
    // constructor, getters, setters
}

class SearchAnalytics {
    private LocalDate searchDate;
    private Integer searchCount;
    private Double avgRelevance;
    // constructor, getters, setters
}
```

## Оптимизация запросов

### Анализ производительности запросов

```sql
-- EXPLAIN для анализа плана выполнения
EXPLAIN SELECT
    u.name,
    COUNT(o.id) AS order_count,
    SUM(o.total_amount) AS total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name
ORDER BY total_spent DESC;

-- FORMAT=JSON для детального анализа
EXPLAIN FORMAT=JSON SELECT ...;

-- ANALYZE для выполнения и анализа
EXPLAIN ANALYZE SELECT ...;
```

### Оптимизация JOIN запросов

```sql
-- Неоптимальный запрос
SELECT u.*, p.*, o.*
FROM users u
CROSS JOIN products p
LEFT JOIN orders o ON u.id = o.user_id;

-- Оптимизированный запрос
SELECT
    u.id, u.name, u.email,
    p.id, p.name, p.price,
    o.id, o.order_date, o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id
WHERE u.active = true
  AND o.order_date >= '2024-01-01';

-- Использование STRAIGHT_JOIN для принудительного порядка
SELECT STRAIGHT_JOIN
    u.name,
    COUNT(o.id) AS order_count
FROM users u
STRAIGHT_JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name;
```

### Оптимизация подзапросов

```sql
-- Неоптимальный подзапрос
SELECT *
FROM products
WHERE category_id IN (
    SELECT id FROM categories
    WHERE parent_id = 1
);

-- Оптимизированный с JOIN
SELECT p.*
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE c.parent_id = 1;

-- Оптимизация с EXISTS
SELECT p.*
FROM products p
WHERE EXISTS (
    SELECT 1 FROM categories c
    WHERE c.id = p.category_id
      AND c.parent_id = 1
);
```

### Кэширование запросов

```sql
-- Включение Query Cache (MySQL 5.7 и ниже)
SET GLOBAL query_cache_size = 268435456; -- 256MB
SET GLOBAL query_cache_type = ON;

-- Анализ использования кэша
SHOW STATUS LIKE 'Qcache%';

-- Для MySQL 8.0+ - использование prepared statements
PREPARE stmt FROM 'SELECT * FROM users WHERE active = ?';
SET @active = 1;
EXECUTE stmt USING @active;
```

### Оптимизация в Java

```java
@Repository
public class QueryOptimizationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Анализ плана выполнения
    public String analyzeQueryPlan(String sql, Object... params) {
        String explainSql = "EXPLAIN FORMAT=JSON " + sql;
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(explainSql, params);
            return result.stream()
                .map(row -> row.toString())
                .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "Error analyzing query: " + e.getMessage();
        }
    }

    // Измерение времени выполнения
    public QueryPerformanceResult measureQueryPerformance(String sql, Object... params) {
        long startTime = System.nanoTime();

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, params);

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        return new QueryPerformanceResult(result.size(), executionTimeMs);
    }

    // Оптимизация запроса с JOIN
    public List<UserOrderSummary> getOptimizedUserOrderSummary() {
        String sql = """
            SELECT
                u.id,
                u.name,
                COUNT(o.id) AS order_count,
                COALESCE(SUM(o.total_amount), 0) AS total_spent,
                MAX(o.order_date) AS last_order_date
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
                AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            WHERE u.active = true
            GROUP BY u.id, u.name
            ORDER BY total_spent DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new UserOrderSummary(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("order_count"),
                rs.getBigDecimal("total_spent"),
                rs.getDate("last_order_date") != null ?
                    rs.getDate("last_order_date").toLocalDate() : null
            )
        );
    }

    // Пакетная обработка для bulk операций
    public void processBatchOrders(List<Order> orders) {
        String sql = """
            INSERT INTO orders (user_id, total_amount, order_date)
            VALUES (?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Order order = orders.get(i);
                ps.setLong(1, order.getUserId());
                ps.setBigDecimal(2, order.getTotalAmount());
                ps.setDate(3, Date.valueOf(order.getOrderDate()));
            }

            @Override
            public int getBatchSize() {
                return orders.size();
            }
        });
    }

    // Использование prepared statements
    public List<Product> findProductsByCategoryWithPreparedStatement(String category, BigDecimal minPrice) {
        String sql = """
            SELECT id, name, price, category
            FROM products
            WHERE category = ?
              AND price >= ?
            ORDER BY price ASC
            """;

        return jdbcTemplate.query(sql, new Object[]{category, minPrice}, (rs, rowNum) ->
            new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("price"),
                rs.getString("category")
            )
        );
    }

    // Анализ индексов
    public List<String> analyzeIndexUsage() {
        List<String> recommendations = new ArrayList<>();

        // Поиск таблиц без первичных ключей
        List<String> tablesWithoutPK = jdbcTemplate.query("""
            SELECT table_name
            FROM information_schema.tables t
            WHERE t.table_schema = DATABASE()
            AND t.table_type = 'BASE TABLE'
            AND NOT EXISTS (
                SELECT 1 FROM information_schema.key_column_usage kcu
                WHERE kcu.table_schema = t.table_schema
                AND kcu.table_name = t.table_name
                AND kcu.constraint_name LIKE '%PRIMARY%'
            )
            """, (rs, rowNum) -> rs.getString("table_name"));

        for (String table : tablesWithoutPK) {
            recommendations.add("Add PRIMARY KEY to table: " + table);
        }

        // Поиск неиспользуемых индексов
        List<Map<String, Object>> unusedIndexes = jdbcTemplate.queryForList("""
            SELECT
                table_name,
                index_name,
                count_read,
                count_write
            FROM performance_schema.table_io_waits_summary_by_index_usage
            WHERE object_schema = DATABASE()
            AND count_read = 0 AND count_write = 0
            AND index_name != 'PRIMARY'
            """);

        for (Map<String, Object> index : unusedIndexes) {
            recommendations.add("Consider removing unused index: " +
                index.get("table_name") + "." + index.get("index_name"));
        }

        return recommendations;
    }

    // Кэширование результатов
    @Cacheable("productStats")
    public ProductStatistics getProductStatistics() {
        String sql = """
            SELECT
                COUNT(*) AS total_products,
                AVG(price) AS avg_price,
                COUNT(DISTINCT category) AS category_count
            FROM products
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
            new ProductStatistics(
                rs.getInt("total_products"),
                rs.getBigDecimal("avg_price"),
                rs.getInt("category_count")
            )
        );
    }
}

// Вспомогательные классы
class QueryPerformanceResult {
    private int resultCount;
    private long executionTimeMs;
    // constructor, getters, setters
}

class UserOrderSummary {
    private Long userId;
    private String name;
    private Integer orderCount;
    private BigDecimal totalSpent;
    private LocalDate lastOrderDate;
    // constructor, getters, setters
}

class ProductStatistics {
    private Integer totalProducts;
    private BigDecimal avgPrice;
    private Integer categoryCount;
    // constructor, getters, setters
}
```

## EXPLAIN и анализ планов

### Чтение EXPLAIN вывода

```sql
-- Базовый EXPLAIN
EXPLAIN SELECT
    u.name,
    COUNT(o.id) AS order_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name;

-- Результат показывает:
-- id: идентификатор SELECT
-- select_type: тип запроса (SIMPLE, PRIMARY, SUBQUERY, etc.)
-- table: имя таблицы
-- type: тип соединения (ALL, index, range, ref, eq_ref, const)
-- possible_keys: возможные индексы
-- key: используемый индекс
-- key_len: длина ключа индекса
-- ref: столбцы для сравнения
-- rows: приблизительное количество строк
-- Extra: дополнительная информация
```

### Типы соединений в EXPLAIN

```sql
-- ALL - полный скан таблицы (плохо)
EXPLAIN SELECT * FROM users; -- type: ALL

-- index - скан по индексу (лучше, чем ALL)
EXPLAIN SELECT name FROM users; -- type: index

-- range - диапазонный поиск по индексу (хорошо)
EXPLAIN SELECT * FROM users WHERE id BETWEEN 1 AND 100; -- type: range

-- ref - поиск по неуникальному индексу (хорошо)
EXPLAIN SELECT * FROM users WHERE department_id = 5; -- type: ref

-- eq_ref - поиск по уникальному индексу (отлично)
EXPLAIN SELECT u.* FROM users u INNER JOIN orders o ON u.id = o.user_id; -- type: eq_ref

-- const - поиск константы (идеально)
EXPLAIN SELECT * FROM users WHERE id = 1; -- type: const
```

### Оптимизация на основе EXPLAIN

```sql
-- Плохой запрос
EXPLAIN SELECT *
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.email LIKE '%gmail.com%';
-- Результат: type: ALL для users (нет индекса на email)

-- Создание индекса
ALTER TABLE users ADD INDEX idx_email (email);

-- Повторный анализ
EXPLAIN SELECT *
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.email LIKE '%gmail.com%';
-- Результат: type: range для users (используется индекс)
```

### JSON формат EXPLAIN

```sql
-- Детальный анализ в JSON формате
EXPLAIN FORMAT=JSON SELECT
    u.name,
    COUNT(o.id) AS order_count,
    AVG(o.total_amount) AS avg_amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.active = true
GROUP BY u.id, u.name
ORDER BY order_count DESC;

-- JSON результат содержит:
-- query_block: информация о блоке запроса
-- select_id: ID SELECT
-- cost_info: информация о стоимости
-- used_columns: используемые столбцы
-- attached_condition: прикрепленные условия
-- materialized_from_subquery: информация о материализации
```

### EXPLAIN в Java

```java
@Service
public class ExplainAnalysisService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Анализ плана выполнения
    public ExplainResult analyzeQueryPlan(String sql, Object... params) {
        String explainSql = "EXPLAIN FORMAT=JSON " + sql;

        try {
            String jsonResult = jdbcTemplate.queryForObject(explainSql, String.class, params);

            // Парсинг JSON результата
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResult);

            ExplainResult result = new ExplainResult();
            result.setQueryCost(extractQueryCost(rootNode));
            result.setUsedIndexes(extractUsedIndexes(rootNode));
            result.setTableAccessTypes(extractTableAccessTypes(rootNode));
            result.setRawJson(jsonResult);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze query plan: " + e.getMessage(), e);
        }
    }

    // Получение рекомендаций по оптимизации
    public List<String> getOptimizationRecommendations(String sql, Object... params) {
        List<String> recommendations = new ArrayList<>();

        try {
            ExplainResult explainResult = analyzeQueryPlan(sql, params);

            // Анализ типов доступа
            for (Map.Entry<String, String> entry : explainResult.getTableAccessTypes().entrySet()) {
                String table = entry.getKey();
                String accessType = entry.getValue();

                switch (accessType) {
                    case "ALL":
                        recommendations.add("Table scan on " + table + " - consider adding indexes");
                        break;
                    case "index":
                        recommendations.add("Index scan on " + table + " - may need composite index");
                        break;
                    case "range":
                        recommendations.add("Range scan on " + table + " - good for range queries");
                        break;
                }
            }

            // Проверка стоимости запроса
            if (explainResult.getQueryCost() > 1000) {
                recommendations.add("High query cost: " + explainResult.getQueryCost() +
                    " - consider query optimization");
            }

        } catch (Exception e) {
            recommendations.add("Error analyzing query: " + e.getMessage());
        }

        return recommendations;
    }

    // Сравнение планов выполнения
    public QueryPlanComparison compareQueryPlans(String sql1, String sql2, Object[] params1, Object[] params2) {
        ExplainResult plan1 = analyzeQueryPlan(sql1, params1);
        ExplainResult plan2 = analyzeQueryPlan(sql2, params2);

        return new QueryPlanComparison(
            plan1.getQueryCost(),
            plan2.getQueryCost(),
            plan1.getUsedIndexes(),
            plan2.getUsedIndexes()
        );
    }

    // Мониторинг медленных запросов
    public List<SlowQueryInfo> getSlowQueries(int limit) {
        String sql = """
            SELECT
                sql_text,
                exec_count,
                total_latency / 1000000000 AS total_time_sec,
                avg_timer_wait / 1000000000 AS avg_time_sec,
                rows_examined,
                rows_sent
            FROM performance_schema.events_statements_summary_by_digest
            WHERE schema_name = DATABASE()
            AND avg_timer_wait > 1000000000  -- > 1 second
            ORDER BY avg_timer_wait DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, new Object[]{limit}, (rs, rowNum) ->
            new SlowQueryInfo(
                rs.getString("sql_text"),
                rs.getInt("exec_count"),
                rs.getDouble("total_time_sec"),
                rs.getDouble("avg_time_sec"),
                rs.getInt("rows_examined"),
                rs.getInt("rows_sent")
            )
        );
    }

    private Double extractQueryCost(JsonNode rootNode) {
        try {
            return rootNode.path("query_block").path("cost_info").path("query_cost").asDouble();
        } catch (Exception e) {
            return 0.0;
        }
    }

    private List<String> extractUsedIndexes(JsonNode rootNode) {
        List<String> indexes = new ArrayList<>();
        extractIndexesFromNode(rootNode, indexes);
        return indexes;
    }

    private void extractIndexesFromNode(JsonNode node, List<String> indexes) {
        if (node.has("key")) {
            indexes.add(node.get("key").asText());
        }

        // Рекурсивный обход для JOIN и субзапросов
        for (JsonNode child : node) {
            if (child.isObject()) {
                extractIndexesFromNode(child, indexes);
            }
        }
    }

    private Map<String, String> extractTableAccessTypes(JsonNode rootNode) {
        Map<String, String> accessTypes = new HashMap<>();
        extractAccessTypesFromNode(rootNode, accessTypes);
        return accessTypes;
    }

    private void extractAccessTypesFromNode(JsonNode node, Map<String, String> accessTypes) {
        if (node.has("table") && node.has("access_type")) {
            accessTypes.put(node.get("table").asText(), node.get("access_type").asText());
        }

        // Рекурсивный обход
        for (JsonNode child : node) {
            if (child.isObject()) {
                extractAccessTypesFromNode(child, accessTypes);
            }
        }
    }
}

// Вспомогательные классы
class ExplainResult {
    private Double queryCost;
    private List<String> usedIndexes;
    private Map<String, String> tableAccessTypes;
    private String rawJson;
    // constructor, getters, setters
}

class QueryPlanComparison {
    private Double cost1;
    private Double cost2;
    private List<String> indexes1;
    private List<String> indexes2;
    // constructor, getters, setters
}

class SlowQueryInfo {
    private String sqlText;
    private Integer execCount;
    private Double totalTimeSec;
    private Double avgTimeSec;
    private Integer rowsExamined;
    private Integer rowsSent;
    // constructor, getters, setters
}
```

## Query Profiling

### Настройка профилирования

```sql
-- Включение профилирования для сессии
SET profiling = 1;

-- Выполнение запросов
SELECT COUNT(*) FROM users;
SELECT * FROM orders WHERE total_amount > 100;

-- Просмотр профиля
SHOW PROFILES;

-- Детальная информация о запросе
SHOW PROFILE FOR QUERY 1;

-- Профилирование по типам
SHOW PROFILE CPU, BLOCK IO, MEMORY, SWAPS FOR QUERY 1;
```

### Performance Schema

```sql
-- Включение Performance Schema
UPDATE performance_schema.setup_instruments
SET ENABLED = 'YES'
WHERE NAME LIKE 'statement/%';

-- Анализ медленных запросов
SELECT
    sql_text,
    exec_count,
    total_latency / 1000000000 AS total_time_sec,
    avg_timer_wait / 1000000000 AS avg_time_sec,
    rows_examined,
    rows_sent,
    created_at
FROM performance_schema.events_statements_summary_by_digest
WHERE schema_name = DATABASE()
ORDER BY avg_timer_wait DESC
LIMIT 10;

-- Анализ использования индексов
SELECT
    object_name AS table_name,
    index_name,
    count_read,
    count_write,
    count_fetch,
    date_created
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = DATABASE()
ORDER BY count_read DESC;
```

### Системные переменные производительности

```sql
-- Просмотр текущих настроек
SHOW VARIABLES LIKE 'innodb_buffer_pool_size';
SHOW VARIABLES LIKE 'query_cache%';
SHOW VARIABLES LIKE 'max_connections';

-- Мониторинг состояния
SHOW STATUS LIKE 'Connections';
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Innodb_buffer_pool_read_requests';
SHOW STATUS LIKE 'Innodb_buffer_pool_reads';

-- Вычисление hit rate буфера
SELECT
    ROUND(100 - ((Innodb_buffer_pool_reads / Innodb_buffer_pool_read_requests) * 100), 2) AS buffer_hit_rate
FROM (
    SELECT
        VARIABLE_VALUE AS Innodb_buffer_pool_reads
    FROM performance_schema.global_status
    WHERE VARIABLE_NAME = 'Innodb_buffer_pool_reads'
) r,
(
    SELECT
        VARIABLE_VALUE AS Innodb_buffer_pool_read_requests
    FROM performance_schema.global_status
    WHERE VARIABLE_NAME = 'Innodb_buffer_pool_read_requests'
) rr;
```

### Профилирование в Java

```java
@Service
public class QueryProfilingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Включение профилирования для сессии
    public void enableProfiling() {
        jdbcTemplate.execute("SET profiling = 1");
        jdbcTemplate.execute("SET profiling_history_size = 100");
    }

    // Получение профиля запросов
    public List<QueryProfile> getQueryProfiles() {
        return jdbcTemplate.query("""
            SELECT
                query_id,
                LEFT(sql_text, 100) AS sql_preview,
                duration,
                cpu_user,
                cpu_system,
                block_ops_in,
                block_ops_out
            FROM information_schema.profiling
            WHERE state = 'executing'
            ORDER BY duration DESC
            """, (rs, rowNum) -> new QueryProfile(
                rs.getInt("query_id"),
                rs.getString("sql_preview"),
                rs.getDouble("duration"),
                rs.getDouble("cpu_user"),
                rs.getDouble("cpu_system"),
                rs.getLong("block_ops_in"),
                rs.getLong("block_ops_out")
            )
        );
    }

    // Детальный профиль конкретного запроса
    public List<QueryProfileStep> getDetailedProfile(int queryId) {
        return jdbcTemplate.query("""
            SELECT
                seq,
                state,
                duration,
                cpu_user,
                cpu_system,
                context_voluntary,
                context_involuntary,
                block_ops_in,
                block_ops_out,
                messages_sent,
                messages_received,
                page_faults_major,
                page_faults_minor,
                swaps
            FROM information_schema.profiling
            WHERE query_id = ?
            ORDER BY seq
            """, new Object[]{queryId}, (rs, rowNum) -> new QueryProfileStep(
                rs.getInt("seq"),
                rs.getString("state"),
                rs.getDouble("duration"),
                rs.getDouble("cpu_user"),
                rs.getDouble("cpu_system"),
                rs.getInt("context_voluntary"),
                rs.getInt("context_involuntary"),
                rs.getLong("block_ops_in"),
                rs.getLong("block_ops_out"),
                rs.getInt("messages_sent"),
                rs.getInt("messages_received"),
                rs.getInt("page_faults_major"),
                rs.getInt("page_faults_minor"),
                rs.getInt("swaps")
            )
        );
    }

    // Анализ медленных запросов через Performance Schema
    public List<SlowQueryAnalysis> analyzeSlowQueries(int limit) {
        String sql = """
            SELECT
                digest_text AS query_pattern,
                count_star AS execution_count,
                avg_timer_wait / 1000000000 AS avg_time_sec,
                max_timer_wait / 1000000000 AS max_time_sec,
                sum_rows_examined AS total_rows_examined,
                sum_rows_sent AS total_rows_sent,
                first_seen,
                last_seen
            FROM performance_schema.events_statements_summary_by_digest
            WHERE schema_name = DATABASE()
            AND avg_timer_wait > 1000000000  -- > 1 second
            ORDER BY avg_timer_wait DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, new Object[]{limit}, (rs, rowNum) ->
            new SlowQueryAnalysis(
                rs.getString("query_pattern"),
                rs.getInt("execution_count"),
                rs.getDouble("avg_time_sec"),
                rs.getDouble("max_time_sec"),
                rs.getInt("total_rows_examined"),
                rs.getInt("total_rows_sent"),
                rs.getTimestamp("first_seen").toLocalDateTime(),
                rs.getTimestamp("last_seen").toLocalDateTime()
            )
        );
    }

    // Мониторинг системных метрик
    public SystemMetrics getSystemMetrics() {
        String sql = """
            SELECT
                (SELECT VARIABLE_VALUE FROM performance_schema.global_status
                 WHERE VARIABLE_NAME = 'Threads_connected') AS active_connections,
                (SELECT VARIABLE_VALUE FROM performance_schema.global_status
                 WHERE VARIABLE_NAME = 'Innodb_buffer_pool_read_requests') AS buffer_read_requests,
                (SELECT VARIABLE_VALUE FROM performance_schema.global_status
                 WHERE VARIABLE_NAME = 'Innodb_buffer_pool_reads') AS buffer_reads,
                (SELECT VARIABLE_VALUE FROM performance_schema.global_status
                 WHERE VARIABLE_NAME = 'Queries') AS total_queries,
                (SELECT VARIABLE_VALUE FROM performance_schema.global_status
                 WHERE VARIABLE_NAME = 'Slow_queries') AS slow_queries
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            long readRequests = Long.parseLong(rs.getString("buffer_read_requests"));
            long reads = Long.parseLong(rs.getString("buffer_reads"));
            double bufferHitRate = readRequests > 0 ?
                (1.0 - (double) reads / readRequests) * 100 : 0.0;

            return new SystemMetrics(
                rs.getInt("active_connections"),
                bufferHitRate,
                rs.getInt("total_queries"),
                rs.getInt("slow_queries")
            );
        });
    }

    // Рекомендации по оптимизации на основе профилирования
    public List<String> generateOptimizationRecommendations() {
        List<String> recommendations = new ArrayList<>();

        // Проверка медленных запросов
        List<SlowQueryAnalysis> slowQueries = analyzeSlowQueries(5);
        for (SlowQueryAnalysis query : slowQueries) {
            recommendations.add(String.format(
                "Slow query (%.2f sec avg): %s",
                query.getAvgTimeSec(),
                query.getQueryPattern().substring(0, 100) + "..."
            ));
        }

        // Проверка hit rate буфера
        SystemMetrics metrics = getSystemMetrics();
        if (metrics.getBufferHitRate() < 95.0) {
            recommendations.add(String.format(
                "Low buffer pool hit rate: %.2f%% - consider increasing innodb_buffer_pool_size",
                metrics.getBufferHitRate()
            ));
        }

        // Проверка количества соединений
        if (metrics.getActiveConnections() > 80) {
            recommendations.add("High number of active connections: " +
                metrics.getActiveConnections() + " - consider connection pooling");
        }

        return recommendations;
    }
}

// Вспомогательные классы
class QueryProfile {
    private Integer queryId;
    private String sqlPreview;
    private Double duration;
    private Double cpuUser;
    private Double cpuSystem;
    private Long blockOpsIn;
    private Long blockOpsOut;
    // constructor, getters, setters
}

class QueryProfileStep {
    private Integer sequence;
    private String state;
    private Double duration;
    private Double cpuUser;
    private Double cpuSystem;
    private Integer contextVoluntary;
    private Integer contextInvoluntary;
    private Long blockOpsIn;
    private Long blockOpsOut;
    private Integer messagesSent;
    private Integer messagesReceived;
    private Integer pageFaultsMajor;
    private Integer pageFaultsMinor;
    private Integer swaps;
    // constructor, getters, setters
}

class SlowQueryAnalysis {
    private String queryPattern;
    private Integer executionCount;
    private Double avgTimeSec;
    private Double maxTimeSec;
    private Integer totalRowsExamined;
    private Integer totalRowsSent;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    // constructor, getters, setters
}

class SystemMetrics {
    private Integer activeConnections;
    private Double bufferHitRate;
    private Integer totalQueries;
    private Integer slowQueries;
    // constructor, getters, setters
}
```

## Лучшие практики

### Написание эффективных запросов

#### 1. Избегайте SELECT *
```sql
-- Плохо
SELECT * FROM users WHERE active = true;

-- Хорошо
SELECT id, first_name, last_name, email FROM users WHERE active = true;
```

#### 2. Используйте LIMIT для больших результатов
```sql
-- Плохо
SELECT * FROM orders ORDER BY created_at DESC;

-- Хорошо
SELECT * FROM orders ORDER BY created_at DESC LIMIT 100;
```

#### 3. Оптимизируйте условия WHERE
```sql
-- Плохо
SELECT * FROM users WHERE YEAR(created_at) = 2024;

-- Хорошо
SELECT * FROM users WHERE created_at >= '2024-01-01' AND created_at < '2025-01-01';
```

#### 4. Используйте UNION ALL вместо UNION когда возможно
```sql
-- UNION ALL быстрее, если нет необходимости в уникальности
SELECT id, name, 'customer' AS type FROM customers
UNION ALL
SELECT id, name, 'supplier' AS type FROM suppliers;
```

### Оптимизация JOIN

#### 1. Выбирайте правильный тип JOIN
```sql
-- Используйте INNER JOIN вместо LEFT JOIN когда возможно
SELECT u.name, o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id; -- Лучше чем LEFT JOIN
```

#### 2. Порядок таблиц в JOIN
```sql
-- Начинайте с меньшей таблицы
SELECT *
FROM small_table s
INNER JOIN large_table l ON s.id = l.small_table_id;
```

#### 3. Избегайте CROSS JOIN
```sql
-- Плохо - декартово произведение
SELECT u.name, p.name FROM users u CROSS JOIN products p;

-- Хорошо - явные условия
SELECT u.name, p.name
FROM users u
INNER JOIN orders o ON u.id = o.user_id
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN products p ON oi.product_id = p.id;
```

### Работа с индексами

#### 1. Создавайте индексы на часто используемые условия
```sql
-- Индексы для WHERE условий
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);

-- Индексы для JOIN
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
```

#### 2. Используйте покрывающие индексы
```sql
-- Покрывающий индекс включает все нужные столбцы
CREATE INDEX idx_users_name_email ON users(last_name, first_name, email);
```

#### 3. Мониторьте использование индексов
```sql
-- Проверка использования индексов
SELECT
    object_name AS table_name,
    index_name,
    count_read,
    count_write
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = DATABASE()
ORDER BY count_read DESC;
```

### Оптимизация подзапросов

#### 1. Используйте EXISTS вместо `IN` для больших наборов
```sql
-- Хорошо для больших таблиц
SELECT * FROM users u
WHERE EXISTS (
    SELECT 1 FROM orders o
    WHERE o.user_id = u.id AND o.total_amount > 1000
);
```

#### 2. Преобразуйте подзапросы в JOIN
```sql
-- Подзапрос
SELECT *
FROM products
WHERE category_id IN (
    SELECT id FROM categories WHERE parent_id = 1
);

-- JOIN (часто эффективнее)
SELECT p.*
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE c.parent_id = 1;
```

### Кэширование и материализованные представления

#### 1. Используйте Query Cache (MySQL 5.7)
```sql
-- Включение кэша запросов
SET GLOBAL query_cache_size = 268435456;
SET GLOBAL query_cache_type = ON;

-- Проверка эффективности
SHOW STATUS LIKE 'Qcache%';
```

#### 2. Материализованные представления для сложных агрегатов
```sql
-- Таблица для кэширования статистики продаж
CREATE TABLE sales_summary (
    date DATE PRIMARY KEY,
    total_sales DECIMAL(12,2),
    order_count INT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Процедура обновления
DELIMITER //

CREATE PROCEDURE refresh_sales_summary()
BEGIN
    REPLACE INTO sales_summary
    SELECT
        DATE(order_date) AS date,
        SUM(total_amount) AS total_sales,
        COUNT(*) AS order_count,
        NOW() AS last_updated
    FROM orders
    GROUP BY DATE(order_date);
END //

DELIMITER ;
```

### Мониторинг и обслуживание

#### 1. Регулярный анализ производительности
```sql
-- Анализ медленных запросов
SELECT
    sql_text,
    exec_count,
    avg_timer_wait / 1000000000 AS avg_time_sec
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 1000000000
ORDER BY avg_timer_wait DESC;

-- Проверка фрагментации таблиц
SELECT
    table_name,
    data_free / 1024 / 1024 AS fragmentation_mb
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND data_free > 0
ORDER BY data_free DESC;
```

#### 2. Обслуживание индексов и таблиц
```sql
-- Перестройка таблиц
OPTIMIZE TABLE users, orders, products;

-- Анализ распределения ключей
ANALYZE TABLE users, orders, products;

-- Проверка и ремонт таблиц
CHECK TABLE users, orders, products;
REPAIR TABLE users, orders, products;
```

### Архитектурные решения

#### 1. Read/Write Splitting
```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Qualifier("readDataSource")
    public DataSource readDataSource() {
        // Настройка для чтения (реплика)
        return DataSourceBuilder.create()
            .url("jdbc:mysql://replica-host:3306/myapp")
            .username("read_user")
            .password("password")
            .build();
    }

    @Bean
    @Qualifier("writeDataSource")
    public DataSource writeDataSource() {
        // Настройка для записи (мастер)
        return DataSourceBuilder.create()
            .url("jdbc:mysql://master-host:3306/myapp")
            .username("write_user")
            .password("password")
            .build();
    }

    @Bean
    public DataSource routingDataSource(
        @Qualifier("readDataSource") DataSource readDataSource,
        @Qualifier("writeDataSource") DataSource writeDataSource) {

        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put("read", readDataSource);
        dataSources.put("write", writeDataSource);
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }
}
```

#### 2. Шардинг (партиционирование)
```sql
-- Партиционирование по диапазону
CREATE TABLE orders (
    id INT AUTO_INCREMENT,
    user_id INT,
    order_date DATE,
    total_amount DECIMAL(10,2),
    PRIMARY KEY (id, order_date)
)
PARTITION BY RANGE (YEAR(order_date)) (
    PARTITION p2020 VALUES LESS THAN (2021),
    PARTITION p2021 VALUES LESS THAN (2022),
    PARTITION p2022 VALUES LESS THAN (2023),
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- Партиционирование по хешу
CREATE TABLE user_sessions (
    session_id VARCHAR(36) PRIMARY KEY,
    user_id INT,
    data TEXT
)
PARTITION BY HASH(user_id) PARTITIONS 8;
```

#### 3. Кэширование на уровне приложения
```java
@Service
public class CachedUserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "userStats", key = "#userId")
    public UserStats getUserStats(Long userId) {
        // Дорогой запрос для агрегации
        return userRepository.calculateUserStats(userId);
    }

    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @CacheEvict(value = {"users", "userStats"}, allEntries = true)
    public void clearAllCaches() {
        // Очистка всех кэшей
    }
}
```

### Профилирование и отладка

#### 1. Используйте EXPLAIN для всех сложных запросов
```sql
-- Всегда анализируйте план выполнения
EXPLAIN FORMAT=JSON
SELECT u.name, COUNT(o.id), AVG(o.total_amount)
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.name;
```

#### 2. Мониторьте slow queries
```sql
-- Настройка логирования медленных запросов
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2; -- 2 секунды
SET GLOBAL slow_query_log_file = '/var/log/mysql/mysql-slow.log';

-- Анализ логов
mysqldumpslow /var/log/mysql/mysql-slow.log
```

#### 3. Используйте Performance Schema
```sql
-- Включение детального мониторинга
UPDATE performance_schema.setup_instruments
SET ENABLED = 'YES', TIMED = 'YES'
WHERE NAME LIKE '%statement/%';

-- Анализ использования ресурсов
SELECT
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec
FROM performance_schema.events_waits_summary_global_by_event_name
WHERE event_name LIKE 'wait/io%'
ORDER BY sum_timer_wait DESC;
```

### Безопасность запросов

#### 1. Используйте Prepared Statements
```java
@Repository
public class SecureQueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Безопасный запрос с параметрами
    public List<User> findUsersByName(String name) {
        String sql = "SELECT * FROM users WHERE first_name LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + name + "%"}, userRowMapper);
    }

    // Безопасный запрос с несколькими параметрами
    public List<Order> findOrdersByCriteria(OrderSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (criteria.getMinAmount() != null) {
            sql.append(" AND total_amount >= ?");
            params.add(criteria.getMinAmount());
        }

        if (criteria.getMaxAmount() != null) {
            sql.append(" AND total_amount <= ?");
            params.add(criteria.getMaxAmount());
        }

        if (criteria.getStartDate() != null) {
            sql.append(" AND order_date >= ?");
            params.add(Date.valueOf(criteria.getStartDate()));
        }

        sql.append(" ORDER BY order_date DESC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), orderRowMapper);
    }
}
```

#### 2. Валидация входных данных
```java
@Service
public class QueryValidationService {

    private static final Pattern SAFE_NAME_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    public boolean isValidTableName(String tableName) {
        return tableName != null &&
               SAFE_NAME_PATTERN.matcher(tableName).matches() &&
               tableName.length() <= 64;
    }

    public boolean isValidColumnName(String columnName) {
        return columnName != null &&
               SAFE_NAME_PATTERN.matcher(columnName).matches() &&
               columnName.length() <= 64;
    }

    public String sanitizeSearchTerm(String term) {
        if (term == null) return "";
        // Удаление потенциально опасных символов для LIKE
        return term.replaceAll("[;%\\\\]", "");
    }

    public BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) return null;
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount.precision() > 10) {
            throw new IllegalArgumentException("Amount precision too high");
        }
        return amount;
    }
}
```

Оптимизация запросов **MySQL** — это комплексная задача, требующая понимания архитектуры базы данных, особенностей **SQL** и инструментов анализа производительности.

### Ключевые принципы оптимизации:

1. **Понимание данных и запросов** — анализ паттернов использования
2. **Правильное проектирование схемы** — подходящие типы данных и индексы
3. **Эффективное написание запросов** — оптимальные **JOIN**, **WHERE** условия
4. **Мониторинг производительности** — использование **EXPLAIN** и профилирования
5. **Непрерывная оптимизация** — регулярный анализ и улучшения

### Основные инструменты оптимизации:

- **EXPLAIN** — анализ планов выполнения
- **Performance Schema** — детальная статистика
- **Slow `Query` Log** — выявление медленных запросов
- **Index optimization** — правильное использование индексов
- **Query rewriting** — улучшение структуры запросов

### Лучшие практики:

1. **Всегда анализируйте EXPLAIN** для сложных запросов
2. **Создавайте подходящие индексы** для условий **WHERE** и **JOIN**
3. **Используйте LIMIT** для больших результатов
4. **Оптимизируйте подзапросы** через **JOIN** или **EXISTS**
5. **Мониторьте производительность** регулярно
6. **Используйте prepared statements** для безопасности

### Архитектурные подходы:

- **Read/write splitting** — разделение нагрузки чтения/записи
- **Sharding** — горизонтальное масштабирование
- **Caching** — кэширование результатов запросов
- **Materialized views** — предварительно вычисленные агрегаты

### Мониторинг и поддержка:

- Регулярный анализ **slow queries**
- Мониторинг системных метрик
- Обслуживание индексов и таблиц
- Планирование **capacity** и масштабирования

## Решение проблем

**Медленные запросы:** включите `slow_query_log`, анализируйте логи через **pt-query-digest** или встроенный **MySQL** анализатор. Добавьте недостающие индексы, уберите полные сканы по большим таблицам, при необходимости перепишите запрос (подзапросы, `JOIN`). Долгие транзакции блокируют строки — разбивайте операции на короткие транзакции.

**Блокировки и дедлоки:** проверьте `SHOW ENGINE INNODB STATUS` и `information_schema.innodb_locks` / `innodb_lock_waits`. Уменьшите время удержания блокировок, придерживайтесь единого порядка блокировки таблиц в приложении, используйте `NOWAIT` или таймауты при необходимости.

**Ошибки подключения (too many connections):** увеличьте `max_connections` с учётом памяти, используйте пулы соединений в приложении и ограничьте число соединений на клиента. Проверьте утечки соединений (незакрытые `Connection`).

**Несогласованность или неожиданные результаты:** убедитесь в выборе уровня изоляции транзакций и в том, что запросы выполняются в ожидаемом порядке. Проверьте влияние репликации (чтение с реплики даёт eventual consistency).

Оптимизация **MySQL** — это непрерывный процесс. Важно не только решать текущие проблемы производительности, но и предотвращать их возникновение в будущем через правильное проектирование и архитектурные решения.

**Следующие темы:**
- [mysql-indexes](mysql-indexes.md) — индексы и оптимизация
- [mysql-performance](mysql-performance.md) — производительность и тюнинг
- [mysql-replication](mysql-replication.md) — репликация и высокая доступность
- [mysql-admin](mysql-admin.md) — администрирование и обслуживание

Эффективные запросы — это основа производительности любой базы данных!


