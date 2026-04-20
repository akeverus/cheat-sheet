---
title: "MySQL: Проектирование баз данных — Полное руководство по проектированию схем"
description: "Комплексное руководство по проектированию баз данных MySQL: нормализация, денормализация, индексы, партиционирование и лучшие практики."
tags:
  - databases
  - relational
  - mysql-design
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# MySQL: Проектирование баз данных — Полное руководство по проектированию схем

Комплексное руководство по проектированию баз данных **MySQL**: нормализация, денормализация, индексы, партиционирование и лучшие практики.

## Полезные ссылки

### Официальная документация
- [MySQL Data Types](https://dev.mysql.com/doc/refman/8.0/en/data-types.html)
- [CREATE TABLE](https://dev.mysql.com/doc/refman/8.0/en/create-table.html)
- [Database Design (MySQL)](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)

### Дизайн и архитектура
- [Database Normalization](https://dev.mysql.com/doc/refman/8.0/en/glossary.html#glos_normalization) — нормализация
- [MySQL Schema Design](https://dev.mysql.com/doc/refman/8.0/en/create-table.html) — проектирование таблиц

### Инструменты
- [MySQL Workbench](https://dev.mysql.com/doc/workbench/en/)
- [dbdiagram.io](https://dbdiagram.io/docs) — диаграммы БД
- [ERwin Data Modeler](https://www.erwin.com/) — моделирование данных

### См. также
- [mysql-basics.md](mysql-basics.md) — основы **MySQL**
- [mysql-indexes.md](mysql-indexes.md) — индексы и оптимизация
- [PostgreSQL](../postgresql/postgres-design.md) — сравнение с **PostgreSQL**

## Содержание

- [Принципы проектирования баз данных](#принципы-проектирования-баз-данных)
  - [Основные принципы](#основные-принципы)
    - [1. Целостность данных (Data Integrity)](#1-целостность-данных-data-integrity)
    - [2. Производительность](#2-производительность)
    - [3. Поддерживаемость](#3-поддерживаемость)
    - [4. Безопасность](#4-безопасность)
  - [Этапы проектирования](#этапы-проектирования)
    - [1. Анализ требований](#1-анализ-требований)
    - [2. Концептуальное моделирование](#2-концептуальное-моделирование)
    - [3. Логическое моделирование](#3-логическое-моделирование)
    - [4. Физическое моделирование](#4-физическое-моделирование)
- [Нормализация данных](#нормализация-данных)
  - [Первая нормальная форма (1NF)](#первая-нормальная-форма-1nf)
    - [Пример нарушения 1NF:](#пример-нарушения-1nf)
  - [Вторая нормальная форма (2NF)](#вторая-нормальная-форма-2nf)
    - [Пример нарушения 2NF:](#пример-нарушения-2nf)
  - [Третья нормальная форма (3NF)](#третья-нормальная-форма-3nf)
    - [Пример нарушения 3NF:](#пример-нарушения-3nf)
  - [Нормальная форма Бойса-Кодда (BCNF)](#нормальная-форма-бойса-кодда-bcnf)
    - [Пример нарушения BCNF:](#пример-нарушения-bcnf)
  - [Четвертая и пятая нормальные формы](#четвертая-и-пятая-нормальные-формы)
    - [4NF: Многозначная зависимость](#4nf-многозначная-зависимость)
    - [5NF: Зависимость соединения](#5nf-зависимость-соединения)
  - [Практическое применение нормализации в Java](#практическое-применение-нормализации-в-java)
- [Денормализация](#денормализация)
  - [Когда использовать денормализацию](#когда-использовать-денормализацию)
    - [Преимущества денормализации:](#преимущества-денормализации)
    - [Недостатки денормализации:](#недостатки-денормализации)
  - [Стратегии денормализации](#стратегии-денормализации)
    - [1. Дублирование данных](#1-дублирование-данных)
    - [2. Агрегированные таблицы](#2-агрегированные-таблицы)
    - [3. Материализованные представления](#3-материализованные-представления)
  - [Денормализация в Java](#денормализация-в-java)
- [Выбор типов данных](#выбор-типов-данных)
  - [Анализ требований к данным](#анализ-требований-к-данным)
    - [Объем данных](#объем-данных)
    - [Характер использования](#характер-использования)
  - [Оптимизация типов данных](#оптимизация-типов-данных)
    - [Числовые типы](#числовые-типы)
    - [Строковые типы](#строковые-типы)
    - [Оптимизация в Java](#оптимизация-в-java)
- [Ключи и ограничения](#ключи-и-ограничения)
  - [Первичные ключи](#первичные-ключи)
    - [Автоинкрементные ключи](#автоинкрементные-ключи)
    - [Натуральные vs суррогатные ключи](#натуральные-vs-суррогатные-ключи)
  - [Внешние ключи](#внешние-ключи)
    - [Каскадные операции](#каскадные-операции)
    - [Ограничения ссылочной целостности](#ограничения-ссылочной-целостности)
  - [Проверка ограничений в Java](#проверка-ограничений-в-java)
- [Индексы](#индексы)
  - [Стратегии индексации](#стратегии-индексации)
    - [Основные принципы](#основные-принципы-1)
    - [Оптимизация индексов](#оптимизация-индексов)
  - [Индексы в Java](#индексы-в-java)
- [Партиционирование](#партиционирование)
  - [Типы партиционирования](#типы-партиционирования)
    - [Партиционирование по диапазону](#партиционирование-по-диапазону)
    - [Партиционирование по списку](#партиционирование-по-списку)
    - [Партиционирование по хешу](#партиционирование-по-хешу)
    - [Субпартиционирование](#субпартиционирование)
  - [Управление партициями](#управление-партициями)
    - [Добавление и удаление партиций](#добавление-и-удаление-партиций)
    - [Оптимизация партиций](#оптимизация-партиций)
  - [Партиционирование в Java](#партиционирование-в-java)
- [Представления и материализованные представления](#представления-и-материализованные-представления)
  - [Создание представлений](#создание-представлений)
    - [Базовые представления](#базовые-представления)
    - [Материализованные представления](#материализованные-представления)
  - [Представления в Java](#представления-в-java)
- [Хранимые процедуры и функции](#хранимые-процедуры-и-функции)
  - [Создание хранимых процедур](#создание-хранимых-процедур)
    - [Базовые процедуры](#базовые-процедуры)
    - [Процедуры с выходными параметрами](#процедуры-с-выходными-параметрами)
    - [Функции](#функции)
  - [Хранимые процедуры в Java](#хранимые-процедуры-в-java)
- [Триггеры](#триггеры)
  - [Создание триггеров](#создание-триггеров)
    - [BEFORE INSERT триггер](#before-insert-триггер)
    - [AFTER UPDATE триггер](#after-update-триггер)
    - [BEFORE DELETE триггер](#before-delete-триггер)
  - [Управление триггерами в Java](#управление-триггерами-в-java)
- [Безопасность на уровне схемы](#безопасность-на-уровне-схемы)
  - [Привилегии и роли](#привилегии-и-роли)
    - [Создание ролей](#создание-ролей)
    - [Безопасные представления](#безопасные-представления)
  - [Шифрование данных](#шифрование-данных)
    - [Шифрование чувствительных полей](#шифрование-чувствительных-полей)
  - [Безопасность в Java](#безопасность-в-java)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Оптимизация запросов](#оптимизация-запросов)
    - [Анализ плана выполнения](#анализ-плана-выполнения)
    - [Оптимизация JOIN](#оптимизация-join)
  - [Профилирование в Java](#профилирование-в-java)
- [Миграции и versioning](#миграции-и-versioning)
  - [Управление миграциями](#управление-миграциями)
    - [Создание системы миграций](#создание-системы-миграций)
  - [Миграции в Java](#миграции-в-java)
- [Лучшие практики](#лучшие-практики)
  - [Проектирование схемы](#проектирование-схемы)
    - [1. Следуйте нормальным формам](#1-следуйте-нормальным-формам)
    - [2. Выбор правильных типов данных](#2-выбор-правильных-типов-данных)
    - [3. Индексы](#3-индексы)
  - [Безопасность](#безопасность)
    - [1. Принцип наименьших привилегий](#1-принцип-наименьших-привилегий)
    - [2. Шифрование чувствительных данных](#2-шифрование-чувствительных-данных)
    - [3. Аудит и мониторинг](#3-аудит-и-мониторинг)
  - [Производительность](#производительность)
    - [1. Оптимизация запросов](#1-оптимизация-запросов)
    - [2. Индексы и партиционирование](#2-индексы-и-партиционирование)
    - [3. Кэширование и материализованные представления](#3-кэширование-и-материализованные-представления)
  - [Поддержка и развитие](#поддержка-и-развитие)
    - [1. Документирование](#1-документирование)
    - [2. Миграции](#2-миграции)
    - [3. Мониторинг](#3-мониторинг)
- [Решение проблем](#решение-проблем)
  - [Ключевые принципы успешного проектирования:](#ключевые-принципы-успешного-проектирования)
  - [Основные компоненты хорошо спроектированной базы данных:](#основные-компоненты-хорошо-спроектированной-базы-данных)
  - [Рекомендации по реализации:](#рекомендации-по-реализации)
  - [Инструменты для проектирования:](#инструменты-для-проектирования)

## Принципы проектирования баз данных

### Основные принципы

#### 1. Целостность данных (Data Integrity)
- **Entity Integrity**: Каждая сущность должна иметь уникальный идентификатор
- **Referential Integrity**: Связи между таблицами должны поддерживаться
- **Domain Integrity**: Данные должны соответствовать допустимым значениям

#### 2. Производительность
- **Оптимальные запросы**: Минимизация **JOIN** и сложных операций
- **Эффективная индексация**: Правильное использование индексов
- **Масштабируемость**: Возможность роста без потери производительности

#### 3. Поддерживаемость
- **Читаемость**: Понятные имена таблиц, колонок, связей
- **Документированность**: Описание назначения и логики
- **Гибкость**: Возможность изменений без полного перепроектирования

#### 4. Безопасность
- **Принцип наименьших привилегий**: Минимально необходимые права
- **Шифрование**: Защита чувствительных данных
- **Аудит**: Отслеживание изменений

### Этапы проектирования

#### 1. Анализ требований

Пример анализа требований и создания схемы при проектировании БД (SQL).

```sql
-- Анализ бизнес-требований
-- Определение сущностей и их атрибутов
-- Выявление связей между сущностями
-- Определение бизнес-правил и ограничений
```

#### 2. Концептуальное моделирование
```sql
-- Создание ER-диаграммы
-- Определение сущностей и атрибутов
-- Установление связей (1:1, 1:N, N:M)
-- Определение доменов данных
```

#### 3. Логическое моделирование
```sql
-- Преобразование в реляционную модель
-- Нормализация данных
-- Определение первичных и внешних ключей
-- Создание логической схемы
```

#### 4. Физическое моделирование
```sql
-- Выбор типов данных MySQL
-- Определение индексов
-- Партиционирование таблиц
-- Оптимизация для конкретной нагрузки
```

## Нормализация данных

### Первая нормальная форма (1NF)

**Правило**: Все атрибуты должны содержать атомарные значения.

#### Пример нарушения 1NF:
```sql
-- Неправильная структура (не атомарные значения)
CREATE TABLE students_bad (
    student_id INT PRIMARY KEY,
    name VARCHAR(100),
    courses VARCHAR(500)  -- "Math, Physics, Chemistry" - не атомарно
);

-- Правильная структура (1NF)
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE student_courses (
    student_id INT,
    course_name VARCHAR(100),
    PRIMARY KEY (student_id, course_name),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);
```

### Вторая нормальная форма (2NF)

**Правило**: Отношение находится во 2NF, если оно находится в 1NF и каждый неключевой атрибут полностью зависит от первичного ключа.

#### Пример нарушения 2NF:
```sql
-- Нарушение 2NF (course_name зависит только от course_id)
CREATE TABLE enrollments_bad (
    student_id INT,
    course_id INT,
    student_name VARCHAR(100),  -- Зависит только от student_id
    course_name VARCHAR(100),   -- Зависит только от course_id
    grade CHAR(1),
    PRIMARY KEY (student_id, course_id)
);

-- Правильная структура (2NF)
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    student_name VARCHAR(100)
);

CREATE TABLE courses (
    course_id INT PRIMARY KEY,
    course_name VARCHAR(100)
);

CREATE TABLE enrollments (
    student_id INT,
    course_id INT,
    grade CHAR(1),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
```

### Третья нормальная форма (3NF)

**Правило**: Отношение находится в 3NF, если оно находится во 2NF и не содержит транзитивных зависимостей.

#### Пример нарушения 3NF:
```sql
-- Нарушение 3NF (department_budget зависит от department_name)
CREATE TABLE employees_bad (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100),
    department_name VARCHAR(100),
    department_budget DECIMAL(10,2),  -- Транзитивная зависимость
    salary DECIMAL(8,2)
);

-- Правильная структура (3NF)
CREATE TABLE departments (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(100),
    department_budget DECIMAL(10,2)
);

CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100),
    department_id INT,
    salary DECIMAL(8,2),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
```

### Нормальная форма Бойса-Кодда (BCNF)

**Правило**: Для любой нетривиальной функциональной зависимости X Y, X должен быть суперключом.

#### Пример нарушения BCNF:
```sql
-- Нарушение BCNF (professor зависит от course, но course не является ключом)
CREATE TABLE course_professors_bad (
    course_id INT,
    professor_id INT,
    semester VARCHAR(20),
    PRIMARY KEY (course_id, professor_id, semester),
    -- professor_id -> professor_name (нарушение BCNF)
    professor_name VARCHAR(100)
);

-- Правильная структура (BCNF)
CREATE TABLE professors (
    professor_id INT PRIMARY KEY,
    professor_name VARCHAR(100)
);

CREATE TABLE course_assignments (
    course_id INT,
    professor_id INT,
    semester VARCHAR(20),
    PRIMARY KEY (course_id, professor_id, semester),
    FOREIGN KEY (professor_id) REFERENCES professors(professor_id)
);
```

### Четвертая и пятая нормальные формы

#### 4NF: Многозначная зависимость
**Правило**: Отсутствие многозначных зависимостей не от суперключа.

#### 5NF: Зависимость соединения
**Правило**: Отсутствие зависимостей соединения, которые не вытекают из ключей.

### Практическое применение нормализации в Java

```java
@Service
public class NormalizationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Создание нормализованной схемы
    public void createNormalizedSchema() {
        // 1NF: Атомарные значения
        jdbcTemplate.execute("""
            CREATE TABLE contacts (
                contact_id INT PRIMARY KEY AUTO_INCREMENT,
                person_id INT,
                contact_type VARCHAR(20), -- 'email', 'phone'
                contact_value VARCHAR(255),
                FOREIGN KEY (person_id) REFERENCES persons(person_id)
            )
            """);

        // 2NF: Устранение частичных зависимостей
        jdbcTemplate.execute("""
            CREATE TABLE persons (
                person_id INT PRIMARY KEY AUTO_INCREMENT,
                first_name VARCHAR(50),
                last_name VARCHAR(50),
                date_of_birth DATE
            )
            """);

        // 3NF: Устранение транзитивных зависимостей
        jdbcTemplate.execute("""
            CREATE TABLE departments (
                department_id INT PRIMARY KEY AUTO_INCREMENT,
                department_name VARCHAR(100),
                location_id INT,
                FOREIGN KEY (location_id) REFERENCES locations(location_id)
            )
            """);

        jdbcTemplate.execute("""
            CREATE TABLE locations (
                location_id INT PRIMARY KEY AUTO_INCREMENT,
                city VARCHAR(100),
                country VARCHAR(100),
                timezone VARCHAR(50)
            )
            """);
    }

    // Проверка нормализации
    public List<String> validateNormalization() {
        List<String> issues = new ArrayList<>();

        // Проверка 1NF: Поиск неатомарных значений
        List<Map<String, Object>> nonAtomic = jdbcTemplate.queryForList("""
            SELECT table_name, column_name
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
            AND data_type IN ('text', 'mediumtext', 'longtext')
            AND column_name LIKE '%list%' OR column_name LIKE '%array%'
            """);

        if (!nonAtomic.isEmpty()) {
            issues.add("Найдены потенциально неатомарные столбцы: " + nonAtomic);
        }

        // Проверка внешних ключей
        List<Map<String, Object>> noFK = jdbcTemplate.queryForList("""
            SELECT table_name
            FROM information_schema.tables t
            WHERE t.table_schema = DATABASE()
            AND t.table_type = 'BASE TABLE'
            AND NOT EXISTS (
                SELECT 1 FROM information_schema.key_column_usage kcu
                WHERE kcu.table_schema = t.table_schema
                AND kcu.table_name = t.table_name
                AND kcu.constraint_name LIKE 'fk_%'
            )
            """);

        if (!noFK.isEmpty()) {
            issues.add("Таблицы без внешних ключей: " + noFK);
        }

        return issues;
    }

    // Генерация скрипта нормализации
    public String generateNormalizationScript(String tableName) {
        StringBuilder script = new StringBuilder();

        script.append("-- Нормализация таблицы ").append(tableName).append("\n");

        // Получение структуры таблицы
        List<Map<String, Object>> columns = jdbcTemplate.queryForList("""
            SELECT column_name, data_type, is_nullable, column_default
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
            AND table_name = ?
            ORDER BY ordinal_position
            """, tableName);

        // Анализ для нормализации
        for (Map<String, Object> column : columns) {
            String columnName = (String) column.get("column_name");
            String dataType = (String) column.get("data_type");

            // Проверка на потенциальные нарушения нормализации
            if (columnName.contains("list") || columnName.contains("array")) {
                script.append("-- POTENTIAL 1NF VIOLATION: ").append(columnName).append("\n");
            }
        }

        return script.toString();
    }
}
```

## Денормализация

### Когда использовать денормализацию

#### Преимущества денормализации:
- **Улучшение производительности чтения**
- **Снижение количества JOIN**
- **Упрощение запросов**
- **Кэширование вычисляемых данных**

#### Недостатки денормализации:
- **Увеличение избыточности данных**
- **Сложность обновлений**
- **Риск несогласованности данных**
- **Увеличение размера базы данных**

### Стратегии денормализации

#### 1. Дублирование данных
```sql
-- Денормализованная таблица заказов
CREATE TABLE orders_denormalized (
    order_id INT PRIMARY KEY,
    customer_id INT,
    customer_name VARCHAR(100),        -- Дублирование из customers
    customer_email VARCHAR(100),       -- Дублирование из customers
    order_date DATE,
    total_amount DECIMAL(10,2),
    status VARCHAR(20),
    product_count INT,                 -- Вычисляемое поле
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Процедура синхронизации данных
DELIMITER //

CREATE PROCEDURE sync_customer_data()
BEGIN
    UPDATE orders_denormalized od
    JOIN customers c ON od.customer_id = c.customer_id
    SET od.customer_name = c.name,
        od.customer_email = c.email,
        od.last_updated = NOW()
    WHERE od.last_updated < c.updated_at;
END //

DELIMITER ;
```

#### 2. Агрегированные таблицы
```sql
-- Таблица для хранения агрегированных данных
CREATE TABLE sales_summary (
    date DATE PRIMARY KEY,
    total_sales DECIMAL(12,2),
    order_count INT,
    avg_order_value DECIMAL(8,2),
    top_product_id INT,
    top_product_sales DECIMAL(10,2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Процедура обновления сводных данных
DELIMITER //

CREATE PROCEDURE update_sales_summary(target_date DATE)
BEGIN
    REPLACE INTO sales_summary
    SELECT
        DATE(o.order_date) as date,
        SUM(o.total_amount) as total_sales,
        COUNT(*) as order_count,
        AVG(o.total_amount) as avg_order_value,
        (
            SELECT product_id
            FROM order_items oi
            JOIN orders o2 ON oi.order_id = o2.order_id
            WHERE DATE(o2.order_date) = target_date
            GROUP BY product_id
            ORDER BY SUM(oi.quantity * oi.price) DESC
            LIMIT 1
        ) as top_product_id,
        (
            SELECT SUM(oi.quantity * oi.price)
            FROM order_items oi
            JOIN orders o2 ON oi.order_id = o2.order_id
            WHERE DATE(o2.order_date) = target_date
            AND oi.product_id = (
                SELECT product_id
                FROM order_items oi2
                JOIN orders o3 ON oi2.order_id = o3.order_id
                WHERE DATE(o3.order_date) = target_date
                GROUP BY product_id
                ORDER BY SUM(oi2.quantity * oi2.price) DESC
                LIMIT 1
            )
        ) as top_product_sales,
        NOW() as last_updated
    FROM orders o
    WHERE DATE(o.order_date) = target_date
    GROUP BY DATE(o.order_date);
END //

DELIMITER ;
```

#### 3. Материализованные представления
```sql
-- Материализованное представление для аналитики
CREATE TABLE customer_analytics (
    customer_id INT PRIMARY KEY,
    total_orders INT,
    total_spent DECIMAL(10,2),
    avg_order_value DECIMAL(8,2),
    last_order_date DATE,
    customer_segment VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Процедура полного обновления
DELIMITER //

CREATE PROCEDURE refresh_customer_analytics()
BEGIN
    REPLACE INTO customer_analytics
    SELECT
        c.customer_id,
        COUNT(o.order_id) as total_orders,
        COALESCE(SUM(o.total_amount), 0) as total_spent,
        COALESCE(AVG(o.total_amount), 0) as avg_order_value,
        MAX(DATE(o.order_date)) as last_order_date,
        CASE
            WHEN SUM(o.total_amount) >= 10000 THEN 'VIP'
            WHEN SUM(o.total_amount) >= 1000 THEN 'Gold'
            WHEN SUM(o.total_amount) >= 100 THEN 'Silver'
            ELSE 'Bronze'
        END as customer_segment,
        NOW() as updated_at
    FROM customers c
    LEFT JOIN orders o ON c.customer_id = o.customer_id
    GROUP BY c.customer_id;
END //

DELIMITER ;
```

### Денормализация в Java

```java
@Service
public class DenormalizationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Синхронизация денормализованных данных
    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void syncDenormalizedData() {
        String sql = """
            UPDATE orders_denormalized od
            JOIN customers c ON od.customer_id = c.customer_id
            SET od.customer_name = c.name,
                od.customer_email = c.email,
                od.last_updated = NOW()
            WHERE od.last_updated < c.updated_at
            """;

        int updatedRows = jdbcTemplate.update(sql);
        System.out.println("Synchronized " + updatedRows + " denormalized records");
    }

    // Обновление агрегированных данных
    public void updateSalesSummary(LocalDate date) {
        String sql = """
            REPLACE INTO sales_summary
            SELECT
                DATE(o.order_date) as date,
                SUM(o.total_amount) as total_sales,
                COUNT(*) as order_count,
                AVG(o.total_amount) as avg_order_value,
                NOW() as last_updated
            FROM orders o
            WHERE DATE(o.order_date) = ?
            GROUP BY DATE(o.order_date)
            """;

        jdbcTemplate.update(sql, Date.valueOf(date));
    }

    // Обновление сегментов клиентов
    public void updateCustomerSegments() {
        String sql = """
            UPDATE customer_analytics ca
            JOIN (
                SELECT
                    customer_id,
                    CASE
                        WHEN total_spent >= 10000 THEN 'VIP'
                        WHEN total_spent >= 1000 THEN 'Gold'
                        WHEN total_spent >= 100 THEN 'Silver'
                        ELSE 'Bronze'
                    END as new_segment
                FROM customer_analytics
            ) updates ON ca.customer_id = updates.customer_id
            SET ca.customer_segment = updates.new_segment,
                ca.updated_at = NOW()
            WHERE ca.customer_segment != updates.new_segment
            """;

        int updatedRows = jdbcTemplate.update(sql);
        System.out.println("Updated segments for " + updatedRows + " customers");
    }

    // Проверка consistency денормализованных данных
    public List<Map<String, Object>> checkDataConsistency() {
        String sql = """
            SELECT
                'customer_name_mismatch' as issue_type,
                COUNT(*) as count
            FROM orders_denormalized od
            JOIN customers c ON od.customer_id = c.customer_id
            WHERE od.customer_name != c.name

            UNION ALL

            SELECT
                'customer_email_mismatch' as issue_type,
                COUNT(*) as count
            FROM orders_denormalized od
            JOIN customers c ON od.customer_id = c.customer_id
            WHERE od.customer_email != c.email
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Исправление inconsistencies
    public void fixDataInconsistencies() {
        String sql = """
            UPDATE orders_denormalized od
            JOIN customers c ON od.customer_id = c.customer_id
            SET od.customer_name = c.name,
                od.customer_email = c.email,
                od.last_updated = NOW()
            WHERE od.customer_name != c.name
               OR od.customer_email != c.email
            """;

        int fixedRows = jdbcTemplate.update(sql);
        System.out.println("Fixed inconsistencies in " + fixedRows + " records");
    }
}
```

## Выбор типов данных

### Анализ требований к данным

#### Объем данных
```sql
-- Оценка размера таблицы
SELECT
    table_name,
    table_rows,
    ROUND((data_length + index_length) / 1024 / 1024, 2) as size_mb,
    ROUND((data_length + index_length) / table_rows, 2) as avg_row_size
FROM information_schema.tables
WHERE table_schema = DATABASE()
ORDER BY data_length DESC;
```

#### Характер использования
```sql
-- Анализ паттернов запросов
SELECT
    table_name,
    column_name,
    data_type,
    column_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = DATABASE()
ORDER BY table_name, ordinal_position;
```

### Оптимизация типов данных

#### Числовые типы
```sql
-- Выбор оптимального числового типа
CREATE TABLE measurements (
    sensor_id INT,                    -- INT для ID
    temperature DECIMAL(5,2),         -- DECIMAL для точности
    humidity TINYINT UNSIGNED,        -- TINYINT для процентов 0-100
    pressure SMALLINT,                -- SMALLINT для давления
    timestamp TIMESTAMP(3),           -- TIMESTAMP с миллисекундами

    PRIMARY KEY (sensor_id, timestamp),
    INDEX idx_timestamp (timestamp),
    INDEX idx_temperature (temperature)
) ENGINE=InnoDB;

-- Анализ использования памяти
SELECT
    'Current size' as metric,
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) as value_mb
FROM information_schema.tables
WHERE table_schema = DATABASE()
    AND table_name = 'measurements'

UNION ALL

SELECT
    'Optimized size estimate',
    ROUND((
        -- sensor_id: 4 bytes
        4 +
        -- temperature: 5 bytes (DECIMAL(5,2))
        5 +
        -- humidity: 1 byte (TINYINT)
        1 +
        -- pressure: 2 bytes (SMALLINT)
        2 +
        -- timestamp: 4 bytes (TIMESTAMP)
        4 +
        -- overhead: ~20 bytes per row
        20
    ) * (SELECT table_rows FROM information_schema.tables
         WHERE table_schema = DATABASE() AND table_name = 'measurements') / 1024 / 1024, 2);
```

#### Строковые типы
```sql
-- Оптимизация строковых типов
CREATE TABLE user_profiles (
    user_id INT PRIMARY KEY,
    username VARCHAR(50) CHARACTER SET utf8mb4,     -- Ограниченная длина
    email VARCHAR(255) CHARACTER SET utf8mb4,       -- Стандарт для email
    bio TEXT,                                       -- Длинный текст
    avatar_url VARCHAR(500),                        -- URL может быть длинным
    preferences JSON,                              -- Структурированные данные
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Анализ распределения длин строк
SELECT
    'username_avg' as metric, AVG(LENGTH(username)) as value
FROM user_profiles
UNION ALL
SELECT 'email_avg', AVG(LENGTH(email))
FROM user_profiles
UNION ALL
SELECT 'bio_avg', AVG(LENGTH(bio))
FROM user_profiles;
```

#### Оптимизация в Java
```java
@Service
public class DataTypeOptimizationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Анализ текущего использования типов данных
    public List<Map<String, Object>> analyzeDataTypes() {
        String sql = """
            SELECT
                table_name,
                column_name,
                data_type,
                column_type,
                ROUND(AVG(LENGTH(IFNULL(column_name, ''))), 2) as avg_length,
                MAX(LENGTH(IFNULL(column_name, ''))) as max_length,
                COUNT(*) as total_rows,
                SUM(CASE WHEN column_name IS NULL THEN 1 ELSE 0 END) as null_count
            FROM information_schema.columns c
            LEFT JOIN (
                SELECT table_name, column_name, data_type
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
            ) t ON c.table_name = t.table_name
            WHERE c.table_schema = DATABASE()
            AND c.data_type IN ('varchar', 'text', 'mediumtext', 'longtext')
            GROUP BY table_name, column_name, data_type, column_type
            ORDER BY avg_length DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Рекомендации по оптимизации
    public List<String> generateOptimizationRecommendations() {
        List<String> recommendations = new ArrayList<>();

        List<Map<String, Object>> analysis = analyzeDataTypes();

        for (Map<String, Object> row : analysis) {
            String tableName = (String) row.get("table_name");
            String columnName = (String) row.get("column_name");
            String dataType = (String) row.get("data_type");
            Double avgLength = ((Number) row.get("avg_length")).doubleValue();
            Long maxLength = ((Number) row.get("max_length")).longValue();

            if ("varchar".equals(dataType) && avgLength < 50 && maxLength < 100) {
                recommendations.add(String.format(
                    "OPTIMIZE %s.%s: VARCHAR(%d) -> VARCHAR(%d) (avg: %.1f)",
                    tableName, columnName, getCurrentLength(columnName), maxLength.intValue(), avgLength));
            }

            if ("text".equals(dataType) && avgLength < 1000) {
                recommendations.add(String.format(
                    "OPTIMIZE %s.%s: TEXT -> VARCHAR(%d) (avg: %.1f)",
                    tableName, columnName, Math.min(maxLength.intValue() + 10, 4000), avgLength));
            }
        }

        return recommendations;
    }

    private int getCurrentLength(String columnName) {
        // В реальности нужно получить из information_schema
        return 255; // placeholder
    }

    // Генерация скрипта оптимизации
    public String generateOptimizationScript() {
        StringBuilder script = new StringBuilder();
        script.append("-- Data Type Optimization Script\n");
        script.append("-- Generated at: ").append(LocalDateTime.now()).append("\n\n");

        List<String> recommendations = generateOptimizationRecommendations();

        for (String rec : recommendations) {
            if (rec.startsWith("OPTIMIZE")) {
                script.append("-- ").append(rec).append("\n");
                // Здесь можно добавить ALTER TABLE statements
            }
        }

        return script.toString();
    }

    // Проверка на oversized поля
    public List<Map<String, Object>> findOversizedFields() {
        String sql = """
            SELECT
                table_name,
                column_name,
                data_type,
                ROUND(AVG(LENGTH(IFNULL(column_name, ''))), 2) as avg_length,
                ROUND(STDDEV(LENGTH(IFNULL(column_name, ''))), 2) as stddev_length
            FROM information_schema.columns c
            WHERE c.table_schema = DATABASE()
            AND c.data_type = 'varchar'
            AND c.character_maximum_length > 100
            GROUP BY table_name, column_name, data_type
            HAVING AVG(LENGTH(IFNULL(column_name, ''))) < 50
            ORDER BY AVG(LENGTH(IFNULL(column_name, ''))) ASC
            """;

        return jdbcTemplate.queryForList(sql);
    }
}
```

## Ключи и ограничения

### Первичные ключи

#### Автоинкрементные ключи
```sql
-- Простой автоинкремент
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Составной первичный ключ
CREATE TABLE user_permissions (
    user_id INT,
    permission_id INT,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by INT,

    PRIMARY KEY (user_id, permission_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- UUID как первичный ключ
CREATE TABLE documents (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Натуральные vs суррогатные ключи
```sql
-- Натуральный ключ (использование существующего поля)
CREATE TABLE countries (
    country_code CHAR(3) PRIMARY KEY,  -- ISO 3166-1 alpha-3
    country_name VARCHAR(100) NOT NULL UNIQUE,
    population BIGINT,
    area_sq_km DECIMAL(12,2)
);

-- Суррогатный ключ (искусственный ID)
CREATE TABLE countries_surrogate (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country_code CHAR(3) UNIQUE NOT NULL,
    country_name VARCHAR(100) NOT NULL UNIQUE,
    population BIGINT,
    area_sq_km DECIMAL(12,2)
);
```

### Внешние ключи

#### Каскадные операции
```sql
-- Каскадное удаление и обновление
CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    manager_id INT,
    budget DECIMAL(12,2),

    FOREIGN KEY (manager_id) REFERENCES employees(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    manager_id INT,
    salary DECIMAL(10,2),

    FOREIGN KEY (department_id) REFERENCES departments(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES employees(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);
```

#### Ограничения ссылочной целостности
```sql
-- Проверка существования родительской записи
DELIMITER //

CREATE TRIGGER check_department_exists
    BEFORE INSERT ON employees
    FOR EACH ROW
BEGIN
    IF NOT EXISTS (SELECT 1 FROM departments WHERE id = NEW.department_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Department does not exist';
    END IF;
END //

DELIMITER ;
```

### Проверка ограничений в Java

```java
@Service
public class ConstraintsValidationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Валидация перед вставкой
    public boolean validateEmployeeInsertion(String name, Integer departmentId, Integer managerId) {
        List<String> errors = new ArrayList<>();

        // Проверка существования отдела
        if (departmentId != null) {
            Integer deptCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM departments WHERE id = ?", Integer.class, departmentId);
            if (deptCount == 0) {
                errors.add("Department with ID " + departmentId + " does not exist");
            }
        }

        // Проверка менеджера
        if (managerId != null) {
            Integer managerCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employees WHERE id = ?", Integer.class, managerId);
            if (managerCount == 0) {
                errors.add("Manager with ID " + managerId + " does not exist");
            }
        }

        // Проверка уникальности имени в отделе
        if (departmentId != null) {
            Integer nameCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM employees WHERE name = ? AND department_id = ?",
                Integer.class, name, departmentId);
            if (nameCount > 0) {
                errors.add("Employee with name '" + name + "' already exists in this department");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }

        return true;
    }

    // Проверка ссылочной целостности
    public List<Map<String, Object>> checkReferentialIntegrity() {
        String sql = """
            SELECT
                'orphaned_employees' as issue_type,
                COUNT(*) as count
            FROM employees e
            LEFT JOIN departments d ON e.department_id = d.id
            WHERE e.department_id IS NOT NULL AND d.id IS NULL

            UNION ALL

            SELECT
                'orphaned_department_managers' as issue_type,
                COUNT(*) as count
            FROM departments d
            LEFT JOIN employees e ON d.manager_id = e.id
            WHERE d.manager_id IS NOT NULL AND e.id IS NULL

            UNION ALL

            SELECT
                'orphaned_employee_managers' as issue_type,
                COUNT(*) as count
            FROM employees e1
            LEFT JOIN employees e2 ON e1.manager_id = e2.id
            WHERE e1.manager_id IS NOT NULL AND e2.id IS NULL
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Исправление проблем целостности
    public void fixIntegrityIssues() {
        // Удаление orphaned записей
        int orphanedEmployees = jdbcTemplate.update(
            "DELETE e FROM employees e LEFT JOIN departments d ON e.department_id = d.id WHERE e.department_id IS NOT NULL AND d.id IS NULL");

        int orphanedManagers = jdbcTemplate.update(
            "UPDATE departments SET manager_id = NULL WHERE manager_id IS NOT NULL AND manager_id NOT IN (SELECT id FROM employees)");

        System.out.println("Fixed " + orphanedEmployees + " orphaned employees and " + orphanedManagers + " invalid managers");
    }

    // Анализ ограничений
    public List<Map<String, Object>> analyzeConstraints() {
        String sql = """
            SELECT
                tc.table_name,
                tc.constraint_name,
                tc.constraint_type,
                kcu.column_name,
                kcu.referenced_table_name,
                kcu.referenced_column_name
            FROM information_schema.table_constraints tc
            LEFT JOIN information_schema.key_column_usage kcu
                ON tc.constraint_name = kcu.constraint_name
                AND tc.table_schema = kcu.table_schema
            WHERE tc.table_schema = DATABASE()
            AND tc.constraint_type IN ('PRIMARY KEY', 'FOREIGN KEY', 'UNIQUE')
            ORDER BY tc.table_name, tc.constraint_type, tc.constraint_name
            """;

        return jdbcTemplate.queryForList(sql);
    }
}
```

## Индексы

### Стратегии индексации

#### Основные принципы
```sql
-- Индексы для первичных запросов
CREATE TABLE articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author_id INT,
    category_id INT,
    published_date DATETIME,
    status ENUM('draft', 'published', 'archived') DEFAULT 'draft',
    view_count INT DEFAULT 0,

    FOREIGN KEY (author_id) REFERENCES authors(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),

    -- Индексы для типичных запросов
    INDEX idx_author_published (author_id, published_date),
    INDEX idx_category_status (category_id, status),
    INDEX idx_published_views (published_date, view_count),
    INDEX idx_status_date (status, published_date)
);

-- FULLTEXT индекс для поиска
ALTER TABLE articles ADD FULLTEXT INDEX idx_content (title, content);

-- SPATIAL индекс для геоданных
ALTER TABLE locations ADD SPATIAL INDEX idx_coordinates (coordinates);
```

#### Оптимизация индексов
```sql
-- Анализ использования индексов
SELECT
    table_name,
    index_name,
    cardinality,
    pages,
    filter_condition
FROM information_schema.statistics
WHERE table_schema = DATABASE()
AND index_name IS NOT NULL
ORDER BY table_name, seq_in_index;

-- Поиск неиспользуемых индексов
SELECT
    object_name as table_name,
    index_name,
    count_read,
    count_write,
    count_fetch
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = DATABASE()
AND count_read = 0 AND count_write = 0
AND index_name != 'PRIMARY';

-- Оптимизация индексов
ANALYZE TABLE articles;
OPTIMIZE TABLE articles;
```

### Индексы в Java

```java
@Service
public class IndexOptimizationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Создание оптимального набора индексов
    public void createOptimalIndexes() {
        List<String> indexQueries = Arrays.asList(
            "CREATE INDEX idx_articles_author_published ON articles(author_id, published_date)",
            "CREATE INDEX idx_articles_category_status ON articles(category_id, status)",
            "CREATE INDEX idx_articles_published_views ON articles(published_date, view_count DESC)",
            "CREATE INDEX idx_articles_status_date ON articles(status, published_date DESC)",
            "CREATE FULLTEXT INDEX idx_articles_content ON articles(title, content)",
            "CREATE INDEX idx_users_email_active ON users(email, active) WHERE active = 1"
        );

        for (String query : indexQueries) {
            try {
                jdbcTemplate.execute(query);
                System.out.println("Created index: " + query.substring(0, 50) + "...");
            } catch (Exception e) {
                System.err.println("Failed to create index: " + e.getMessage());
            }
        }
    }

    // Анализ эффективности индексов
    public List<Map<String, Object>> analyzeIndexEfficiency() {
        String sql = """
            SELECT
                t.table_name,
                i.index_name,
                i.cardinality,
                i.pages,
                ROUND(i.cardinality / t.table_rows * 100, 2) as selectivity_percent,
                CASE
                    WHEN i.cardinality / t.table_rows > 0.8 THEN 'HIGH'
                    WHEN i.cardinality / t.table_rows > 0.5 THEN 'MEDIUM'
                    ELSE 'LOW'
                END as selectivity_rating
            FROM information_schema.statistics i
            JOIN information_schema.tables t ON i.table_schema = t.table_schema
                AND i.table_name = t.table_name
            WHERE i.table_schema = DATABASE()
            AND i.index_name IS NOT NULL
            AND t.table_rows > 0
            ORDER BY selectivity_percent DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Поиск дублированных индексов
    public List<Map<String, Object>> findDuplicateIndexes() {
        String sql = """
            SELECT
                table_name,
                GROUP_CONCAT(index_name ORDER BY index_name) as duplicate_indexes,
                column_names,
                COUNT(*) as index_count
            FROM (
                SELECT
                    table_name,
                    index_name,
                    GROUP_CONCAT(column_name ORDER BY seq_in_index) as column_names
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                AND index_name != 'PRIMARY'
                GROUP BY table_name, index_name
            ) t
            GROUP BY table_name, column_names
            HAVING COUNT(*) > 1
            ORDER BY table_name
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Удаление неиспользуемых индексов
    public List<String> identifyUnusedIndexes() {
        String sql = """
            SELECT CONCAT(
                'ALTER TABLE ', table_name,
                ' DROP INDEX ', index_name, '; -- Last used: ',
                IFNULL(last_used, 'NEVER')
            ) as drop_statement
            FROM (
                SELECT
                    i.table_name,
                    i.index_name,
                    MAX(s.last_update) as last_used
                FROM information_schema.statistics i
                LEFT JOIN sys.schema_index_statistics s
                    ON i.table_name = s.table_name
                    AND i.index_name = s.index_name
                WHERE i.table_schema = DATABASE()
                AND i.index_name != 'PRIMARY'
                GROUP BY i.table_name, i.index_name
            ) usage
            WHERE last_used IS NULL
               OR last_used < DATE_SUB(NOW(), INTERVAL 90 DAY)
            ORDER BY table_name, index_name
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

    // Создание покрывающих индексов
    public void createCoveringIndexes() {
        // Индексы, покрывающие часто используемые запросы
        List<String> coveringIndexes = Arrays.asList(
            // Для запроса комментариев с данными пользователя
            "CREATE INDEX idx_comments_post_user_date ON comments(post_id, user_id, created_at)",

            // Для поиска заказов с деталями клиента
            "CREATE INDEX idx_orders_customer_date_total ON orders(customer_id, order_date, total_amount)",

            // Для аналитики продуктов
            "CREATE INDEX idx_products_category_price_stock ON products(category_id, price, stock_quantity)"
        );

        for (String query : coveringIndexes) {
            try {
                jdbcTemplate.execute(query);
                System.out.println("Created covering index");
            } catch (Exception e) {
                System.err.println("Failed to create covering index: " + e.getMessage());
            }
        }
    }

    // Мониторинг размера индексов
    public List<Map<String, Object>> monitorIndexSize() {
        String sql = """
            SELECT
                table_name,
                index_name,
                ROUND(sum(data_length + index_length) / 1024 / 1024, 2) as size_mb,
                count(*) as columns_count,
                GROUP_CONCAT(column_name ORDER BY seq_in_index) as columns
            FROM information_schema.statistics s
            LEFT JOIN information_schema.tables t ON s.table_schema = t.table_schema
                AND s.table_name = t.table_name
            WHERE s.table_schema = DATABASE()
            AND s.index_name IS NOT NULL
            GROUP BY table_name, index_name
            ORDER BY size_mb DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }
}
```

## Партиционирование

### Типы партиционирования

#### Партиционирование по диапазону
```sql
-- Партиционирование по датам
CREATE TABLE sales (
    id INT AUTO_INCREMENT,
    sale_date DATE NOT NULL,
    customer_id INT,
    product_id INT,
    quantity INT,
    amount DECIMAL(10,2),
    PRIMARY KEY (id, sale_date)
)
PARTITION BY RANGE (YEAR(sale_date)) (
    PARTITION p2020 VALUES LESS THAN (2021),
    PARTITION p2021 VALUES LESS THAN (2022),
    PARTITION p2022 VALUES LESS THAN (2023),
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- Добавление новой партиции
ALTER TABLE sales ADD PARTITION (
    PARTITION p2024 VALUES LESS THAN (2025)
);
```

#### Партиционирование по списку
```sql
-- Партиционирование по регионам
CREATE TABLE orders (
    id INT AUTO_INCREMENT,
    order_date DATE,
    customer_id INT,
    region_code CHAR(2), -- 'US', 'EU', 'AS', etc.
    total_amount DECIMAL(10,2),
    PRIMARY KEY (id, order_date)
)
PARTITION BY LIST (region_code) (
    PARTITION p_us VALUES IN ('US', 'CA'),
    PARTITION p_eu VALUES IN ('UK', 'DE', 'FR', 'IT'),
    PARTITION p_as VALUES IN ('JP', 'KR', 'CN', 'IN'),
    PARTITION p_other VALUES IN ('AU', 'BR', 'RU')
);
```

#### Партиционирование по хешу
```sql
-- Равномерное распределение по партициям
CREATE TABLE user_sessions (
    session_id CHAR(36) PRIMARY KEY,
    user_id INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    data TEXT
)
PARTITION BY HASH(user_id) PARTITIONS 8;

-- Линейное хеширование
CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    log_time TIMESTAMP,
    level VARCHAR(10),
    message TEXT
)
PARTITION BY LINEAR HASH(id) PARTITIONS 16;
```

#### Субпартиционирование
```sql
-- Партиционирование по диапазону с субпартиционированием по хешу
CREATE TABLE measurements (
    sensor_id INT,
    timestamp TIMESTAMP,
    temperature DECIMAL(5,2),
    humidity DECIMAL(5,2)
)
PARTITION BY RANGE (YEAR(timestamp))
SUBPARTITION BY HASH(sensor_id) SUBPARTITIONS 4 (
    PARTITION p2023 VALUES LESS THAN (2024) (
        SUBPARTITION sp2023_0,
        SUBPARTITION sp2023_1,
        SUBPARTITION sp2023_2,
        SUBPARTITION sp2023_3
    ),
    PARTITION p2024 VALUES LESS THAN (2025) (
        SUBPARTITION sp2024_0,
        SUBPARTITION sp2024_1,
        SUBPARTITION sp2024_2,
        SUBPARTITION sp2024_3
    )
);
```

### Управление партициями

#### Добавление и удаление партиций
```sql
-- Добавление партиции
ALTER TABLE sales ADD PARTITION (
    PARTITION p2025 VALUES LESS THAN (2026)
);

-- Удаление старой партиции
ALTER TABLE sales DROP PARTITION p2020;

-- Переорганизация партиции
ALTER TABLE sales REORGANIZE PARTITION p_future INTO (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- Слияние партиций
ALTER TABLE sales REORGANIZE PARTITION p2021, p2022 INTO (
    PARTITION p2021_2022 VALUES LESS THAN (2023)
);
```

#### Оптимизация партиций
```sql
-- Анализ использования партиций
SELECT
    table_name,
    partition_name,
    partition_method,
    partition_expression,
    partition_description,
    table_rows,
    data_length / 1024 / 1024 as data_mb,
    index_length / 1024 / 1024 as index_mb
FROM information_schema.partitions
WHERE table_schema = DATABASE()
ORDER BY table_name, partition_ordinal_position;

-- Оптимизация партиции
ALTER TABLE sales OPTIMIZE PARTITION p2023;

-- Проверка партиции
ALTER TABLE sales CHECK PARTITION p2023;

-- Ремонт партиции
ALTER TABLE sales REPAIR PARTITION p2023;

-- Анализ партиции
ALTER TABLE sales ANALYZE PARTITION p2023;
```

### Партиционирование в Java

```java
@Service
public class PartitioningService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Создание партиционированной таблицы
    public void createPartitionedTable() {
        String sql = """
            CREATE TABLE sales_partitioned (
                id INT AUTO_INCREMENT,
                sale_date DATE NOT NULL,
                customer_id INT,
                amount DECIMAL(10,2),
                PRIMARY KEY (id, sale_date)
            )
            PARTITION BY RANGE (YEAR(sale_date)) (
                PARTITION p2020 VALUES LESS THAN (2021),
                PARTITION p2021 VALUES LESS THAN (2022),
                PARTITION p2022 VALUES LESS THAN (2023),
                PARTITION p2023 VALUES LESS THAN (2024),
                PARTITION p_future VALUES LESS THAN MAXVALUE
            )
            """;

        jdbcTemplate.execute(sql);
    }

    // Управление партициями
    public void managePartitions() {
        // Добавление новой партиции
        jdbcTemplate.execute("""
            ALTER TABLE sales_partitioned ADD PARTITION (
                PARTITION p2024 VALUES LESS THAN (2025)
            )
            """);

        // Удаление старой партиции
        jdbcTemplate.execute("ALTER TABLE sales_partitioned DROP PARTITION p2020");
    }

    // Анализ партиций
    public List<Map<String, Object>> analyzePartitions(String tableName) {
        String sql = """
            SELECT
                partition_name,
                partition_method,
                partition_expression,
                partition_description,
                table_rows,
                ROUND(data_length / 1024 / 1024, 2) as data_mb,
                ROUND(index_length / 1024 / 1024, 2) as index_mb,
                ROUND((data_length + index_length) / 1024 / 1024, 2) as total_mb
            FROM information_schema.partitions
            WHERE table_schema = DATABASE()
            AND table_name = ?
            AND partition_name IS NOT NULL
            ORDER BY partition_ordinal_position
            """;

        return jdbcTemplate.queryForList(sql, tableName);
    }

    // Оптимизация партиций
    public void optimizePartitions(String tableName) {
        // Получение списка партиций
        List<String> partitions = jdbcTemplate.query(
            "SELECT partition_name FROM information_schema.partitions WHERE table_schema = DATABASE() AND table_name = ? AND partition_name IS NOT NULL",
            (rs, rowNum) -> rs.getString("partition_name"),
            tableName
        );

        for (String partition : partitions) {
            try {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " OPTIMIZE PARTITION " + partition);
                System.out.println("Optimized partition: " + partition);
            } catch (Exception e) {
                System.err.println("Failed to optimize partition " + partition + ": " + e.getMessage());
            }
        }
    }

    // Автоматическое управление партициями
    @Scheduled(cron = "0 0 2 * * *") // Каждый день в 02:00
    public void maintainPartitions() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        // Добавление партиции для следующего года
        try {
            jdbcTemplate.execute("ALTER TABLE sales_partitioned ADD PARTITION (PARTITION p" + (currentYear + 1) + " VALUES LESS THAN (" + (currentYear + 2) + "))");
            System.out.println("Added partition for year " + (currentYear + 1));
        } catch (Exception e) {
            System.out.println("Partition for year " + (currentYear + 1) + " may already exist");
        }

        // Удаление старых партиций (старше 5 лет)
        int cutoffYear = currentYear - 5;
        try {
            List<String> oldPartitions = jdbcTemplate.query(
                "SELECT partition_name FROM information_schema.partitions WHERE table_schema = DATABASE() AND table_name = 'sales_partitioned' AND partition_name LIKE 'p%' AND CAST(SUBSTRING(partition_name, 2) AS UNSIGNED) < ?",
                (rs, rowNum) -> rs.getString("partition_name"),
                cutoffYear
            );

            for (String partition : oldPartitions) {
                jdbcTemplate.execute("ALTER TABLE sales_partitioned DROP PARTITION " + partition);
                System.out.println("Dropped old partition: " + partition);
            }
        } catch (Exception e) {
            System.err.println("Error maintaining partitions: " + e.getMessage());
        }
    }

    // Выбор партиции для запроса
    public List<Map<String, Object>> queryPartition(String tableName, String partitionName) {
        String sql = "SELECT * FROM " + tableName + " PARTITION (" + partitionName + ") LIMIT 100";
        return jdbcTemplate.queryForList(sql);
    }
}
```

## Представления и материализованные представления

### Создание представлений

#### Базовые представления
```sql
-- Простое представление
CREATE VIEW active_users AS
SELECT id, username, email, created_at
FROM users
WHERE active = true;

-- Представление с JOIN
CREATE VIEW user_orders AS
SELECT
    u.id as user_id,
    u.username,
    u.email,
    o.id as order_id,
    o.total_amount,
    o.order_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.active = true;
```

#### Материализованные представления
```sql
-- Таблица для материализованного представления
CREATE TABLE monthly_sales_summary (
    month DATE PRIMARY KEY,
    total_sales DECIMAL(12,2),
    order_count INT,
    avg_order_value DECIMAL(8,2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Процедура обновления материализованного представления
DELIMITER //

CREATE PROCEDURE refresh_monthly_sales()
BEGIN
    REPLACE INTO monthly_sales_summary
    SELECT
        DATE_FORMAT(order_date, '%Y-%m-01') as month,
        SUM(total_amount) as total_sales,
        COUNT(*) as order_count,
        AVG(total_amount) as avg_order_value,
        NOW() as last_updated
    FROM orders
    WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
    GROUP BY DATE_FORMAT(order_date, '%Y-%m-01');
END //

DELIMITER ;
```

### Представления в Java

```java
@Service
public class ViewManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Создание представлений
    public void createViews() {
        // Активные пользователи
        jdbcTemplate.execute("""
            CREATE OR REPLACE VIEW active_users AS
            SELECT id, username, email, created_at, last_login
            FROM users
            WHERE active = true
            AND last_login >= DATE_SUB(NOW(), INTERVAL 30 DAY)
            """);

        // Сводка заказов по пользователям
        jdbcTemplate.execute("""
            CREATE OR REPLACE VIEW user_order_summary AS
            SELECT
                u.id,
                u.username,
                COUNT(o.id) as total_orders,
                COALESCE(SUM(o.total_amount), 0) as total_spent,
                COALESCE(AVG(o.total_amount), 0) as avg_order_value,
                MAX(o.order_date) as last_order_date
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            GROUP BY u.id, u.username
            """);

        // Продукты с рейтингом
        jdbcTemplate.execute("""
            CREATE OR REPLACE VIEW product_ratings AS
            SELECT
                p.id,
                p.name,
                p.category,
                COUNT(r.id) as review_count,
                COALESCE(AVG(r.rating), 0) as avg_rating,
                COALESCE(MIN(r.rating), 0) as min_rating,
                COALESCE(MAX(r.rating), 0) as max_rating
            FROM products p
            LEFT JOIN reviews r ON p.id = r.product_id
            GROUP BY p.id, p.name, p.category
            """);
    }

    // Работа с материализованными представлениями
    public void createMaterializedViews() {
        // Создание таблицы для ежедневной статистики
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS daily_stats (
                date DATE PRIMARY KEY,
                user_registrations INT DEFAULT 0,
                orders_placed INT DEFAULT 0,
                total_revenue DECIMAL(12,2) DEFAULT 0,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);

        // Процедура обновления
        jdbcTemplate.execute("""
            CREATE PROCEDURE refresh_daily_stats()
            BEGIN
                REPLACE INTO daily_stats
                SELECT
                    DATE(created_at) as date,
                    COUNT(*) as user_registrations,
                    0 as orders_placed,
                    0 as total_revenue,
                    NOW() as last_updated
                FROM users
                WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                GROUP BY DATE(created_at);
            END
            """);

        // Обновление заказов в статистике
        jdbcTemplate.execute("""
            CREATE PROCEDURE update_daily_orders()
            BEGIN
                UPDATE daily_stats ds
                JOIN (
                    SELECT
                        DATE(order_date) as date,
                        COUNT(*) as orders_count,
                        SUM(total_amount) as revenue
                    FROM orders
                    WHERE DATE(order_date) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                    GROUP BY DATE(order_date)
                ) o ON ds.date = o.date
                SET ds.orders_placed = o.orders_count,
                    ds.total_revenue = o.revenue,
                    ds.last_updated = NOW();
            END
            """);
    }

    // Обновление материализованных представлений
    @Scheduled(fixedRate = 3600000) // Каждый час
    public void refreshMaterializedViews() {
        try {
            jdbcTemplate.update("CALL refresh_daily_stats()");
            jdbcTemplate.update("CALL update_daily_orders()");
            System.out.println("Refreshed materialized views at " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Failed to refresh materialized views: " + e.getMessage());
        }
    }

    // Запросы к представлениям
    public List<Map<String, Object>> getActiveUsers() {
        return jdbcTemplate.queryForList("SELECT * FROM active_users ORDER BY last_login DESC");
    }

    public List<Map<String, Object>> getTopCustomers() {
        return jdbcTemplate.queryForList("""
            SELECT * FROM user_order_summary
            WHERE total_orders > 0
            ORDER BY total_spent DESC
            LIMIT 10
            """);
    }

    public List<Map<String, Object>> getProductRatings() {
        return jdbcTemplate.queryForList("""
            SELECT * FROM product_ratings
            WHERE review_count > 0
            ORDER BY avg_rating DESC, review_count DESC
            """);
    }

    // Анализ представлений
    public List<Map<String, Object>> analyzeViews() {
        return jdbcTemplate.queryForList("""
            SELECT
                table_name as view_name,
                'VIEW' as type,
                'User defined' as engine,
                create_time,
                update_time
            FROM information_schema.views
            WHERE table_schema = DATABASE()

            UNION ALL

            SELECT
                table_name as view_name,
                'MATERIALIZED' as type,
                engine,
                create_time,
                update_time
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
            AND table_name LIKE '%summary%'
            AND table_type = 'BASE TABLE'
            """);
    }
}
```

## Хранимые процедуры и функции

### Создание хранимых процедур

#### Базовые процедуры
```sql
DELIMITER //

CREATE PROCEDURE get_user_orders(IN user_id INT)
BEGIN
    SELECT
        o.id,
        o.order_date,
        o.total_amount,
        o.status
    FROM orders o
    WHERE o.user_id = user_id
    ORDER BY o.order_date DESC;
END //

DELIMITER ;

-- Вызов процедуры
CALL get_user_orders(123);
```

#### Процедуры с выходными параметрами
```sql
DELIMITER //

CREATE PROCEDURE calculate_user_stats(
    IN user_id INT,
    OUT total_orders INT,
    OUT total_spent DECIMAL(10,2),
    OUT avg_order_value DECIMAL(8,2)
)
BEGIN
    SELECT
        COUNT(*),
        COALESCE(SUM(total_amount), 0),
        COALESCE(AVG(total_amount), 0)
    INTO total_orders, total_spent, avg_order_value
    FROM orders
    WHERE user_id = user_id;
END //

DELIMITER ;

-- Использование выходных параметров
CALL calculate_user_stats(123, @orders, @spent, @avg);
SELECT @orders, @spent, @avg;
```

#### Функции
```sql
DELIMITER //

CREATE FUNCTION calculate_discount(price DECIMAL(10,2), discount_percent INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN price * (1 - discount_percent / 100.0);
END //

DELIMITER ;

-- Использование функции
SELECT
    product_name,
    original_price,
    calculate_discount(original_price, 10) as discounted_price
FROM products;
```

### Хранимые процедуры в Java

```java
@Service
public class StoredProcedureService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Вызов простой процедуры
    public List<Map<String, Object>> getUserOrders(int userId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("get_user_orders")
            .returningResultSet("orders",
                (rs, rowNum) -> {
                    Map<String, Object> order = new HashMap<>();
                    order.put("id", rs.getInt("id"));
                    order.put("order_date", rs.getDate("order_date"));
                    order.put("total_amount", rs.getBigDecimal("total_amount"));
                    order.put("status", rs.getString("status"));
                    return order;
                });

        Map<String, Object> result = jdbcCall.execute(userId);
        return (List<Map<String, Object>>) result.get("orders");
    }

    // Вызов процедуры с выходными параметрами
    public UserStats calculateUserStats(int userId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("calculate_user_stats")
            .declareParameters(
                new SqlParameter("user_id", Types.INTEGER),
                new SqlOutParameter("total_orders", Types.INTEGER),
                new SqlOutParameter("total_spent", Types.DECIMAL),
                new SqlOutParameter("avg_order_value", Types.DECIMAL)
            );

        Map<String, Object> result = jdbcCall.execute(userId);

        return new UserStats(
            ((Number) result.get("total_orders")).intValue(),
            (BigDecimal) result.get("total_spent"),
            (BigDecimal) result.get("avg_order_value")
        );
    }

    // Вызов функции
    public List<Map<String, Object>> getProductsWithDiscount(int discountPercent) {
        return jdbcTemplate.query("""
            SELECT
                product_name,
                original_price,
                calculate_discount(original_price, ?) as discounted_price,
                (original_price - calculate_discount(original_price, ?)) as savings
            FROM products
            ORDER BY savings DESC
            """, discountPercent, discountPercent);
    }

    // Создание процедуры через Java
    public void createStoredProcedures() {
        // Процедура для очистки старых данных
        jdbcTemplate.execute("""
            CREATE PROCEDURE cleanup_old_data(IN days_old INT)
            BEGIN
                DELETE FROM audit_log WHERE created_at < DATE_SUB(NOW(), INTERVAL days_old DAY);
                DELETE FROM user_sessions WHERE last_activity < DATE_SUB(NOW(), INTERVAL days_old DAY);
                DELETE FROM temp_files WHERE created_at < DATE_SUB(NOW(), INTERVAL days_old DAY);
            END
            """);

        // Процедура для создания отчета
        jdbcTemplate.execute("""
            CREATE PROCEDURE generate_sales_report(IN start_date DATE, IN end_date DATE)
            BEGIN
                SELECT
                    DATE(order_date) as date,
                    COUNT(*) as orders_count,
                    SUM(total_amount) as total_revenue,
                    AVG(total_amount) as avg_order_value,
                    COUNT(DISTINCT customer_id) as unique_customers
                FROM orders
                WHERE order_date BETWEEN start_date AND end_date
                GROUP BY DATE(order_date)
                ORDER BY date;
            END
            """);
    }

    // Вызов процедуры очистки
    public int cleanupOldData(int daysOld) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("cleanup_old_data");

        Map<String, Object> result = jdbcCall.execute(daysOld);
        // В реальности нужно вернуть количество удаленных записей
        return 0; // placeholder
    }

    // Генерация отчета о продажах
    public List<Map<String, Object>> generateSalesReport(LocalDate startDate, LocalDate endDate) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("generate_sales_report")
            .returningResultSet("report",
                (rs, rowNum) -> {
                    Map<String, Object> record = new HashMap<>();
                    record.put("date", rs.getDate("date"));
                    record.put("orders_count", rs.getInt("orders_count"));
                    record.put("total_revenue", rs.getBigDecimal("total_revenue"));
                    record.put("avg_order_value", rs.getBigDecimal("avg_order_value"));
                    record.put("unique_customers", rs.getInt("unique_customers"));
                    return record;
                });

        Map<String, Object> result = jdbcCall.execute(
            Date.valueOf(startDate),
            Date.valueOf(endDate)
        );

        return (List<Map<String, Object>>) result.get("report");
    }

    // Управление процедурами
    public List<String> listStoredProcedures() {
        return jdbcTemplate.query("""
            SELECT CONCAT(routine_schema, '.', routine_name) as procedure_name
            FROM information_schema.routines
            WHERE routine_schema = DATABASE()
            AND routine_type = 'PROCEDURE'
            ORDER BY routine_name
            """,
            (rs, rowNum) -> rs.getString("procedure_name"));
    }

    public void dropProcedure(String procedureName) {
        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS " + procedureName);
    }
}

// Вспомогательные классы
class UserStats {
    private int totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal avgOrderValue;

    // constructor, getters, setters
}
```

## Триггеры

### Создание триггеров

#### BEFORE INSERT триггер
```sql
DELIMITER //

CREATE TRIGGER validate_user_email
    BEFORE INSERT ON users
    FOR EACH ROW
BEGIN
    -- Проверка формата email
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid email format';
    END IF;

    -- Автоматическая генерация username если не указан
    IF NEW.username IS NULL THEN
        SET NEW.username = CONCAT(LOWER(NEW.first_name), '.', LOWER(NEW.last_name));
    END IF;
END //

DELIMITER ;
```

#### AFTER UPDATE триггер
```sql
DELIMITER //

CREATE TRIGGER audit_price_changes
    AFTER UPDATE ON products
    FOR EACH ROW
BEGIN
    -- Логирование изменений цены
    IF OLD.price != NEW.price THEN
        INSERT INTO price_history (
            product_id,
            old_price,
            new_price,
            changed_at,
            changed_by
        ) VALUES (
            NEW.id,
            OLD.price,
            NEW.price,
            NOW(),
            USER()
        );
    END IF;
END //

DELIMITER ;
```

#### BEFORE DELETE триггер
```sql
DELIMITER //

CREATE TRIGGER prevent_admin_deletion
    BEFORE DELETE ON users
    FOR EACH ROW
BEGIN
    -- Предотвращение удаления администраторов
    IF OLD.role = 'admin' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete admin users';
    END IF;
END //

DELIMITER ;
```

### Управление триггерами в Java

```java
@Service
public class TriggerManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Создание аудита триггеров
    public void createAuditTriggers() {
        // Аудит изменений пользователей
        jdbcTemplate.execute("""
            CREATE TRIGGER audit_user_changes
                AFTER UPDATE ON users
                FOR EACH ROW
            BEGIN
                INSERT INTO user_audit (
                    user_id,
                    action,
                    field_changed,
                    old_value,
                    new_value,
                    changed_at,
                    changed_by
                ) VALUES
                (NEW.id, 'UPDATE', 'email', OLD.email, NEW.email, NOW(), USER()),
                (NEW.id, 'UPDATE', 'first_name', OLD.first_name, NEW.first_name, NOW(), USER()),
                (NEW.id, 'UPDATE', 'last_name', OLD.last_name, NEW.last_name, NOW(), USER());
            END
            """);

        // Аудит заказов
        jdbcTemplate.execute("""
            CREATE TRIGGER audit_order_status_changes
                AFTER UPDATE ON orders
                FOR EACH ROW
            BEGIN
                IF OLD.status != NEW.status THEN
                    INSERT INTO order_status_history (
                        order_id,
                        old_status,
                        new_status,
                        changed_at,
                        changed_by
                    ) VALUES (
                        NEW.id,
                        OLD.status,
                        NEW.status,
                        NOW(),
                        USER()
                    );
                END IF;
            END
            """);
    }

    // Создание валидационных триггеров
    public void createValidationTriggers() {
        // Валидация email
        jdbcTemplate.execute("""
            CREATE TRIGGER validate_email_format
                BEFORE INSERT ON users
                FOR EACH ROW
            BEGIN
                IF NEW.email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
                    SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Invalid email format';
                END IF;
            END
            """);

        // Валидация возраста
        jdbcTemplate.execute("""
            CREATE TRIGGER validate_user_age
                BEFORE INSERT ON users
                FOR EACH ROW
            BEGIN
                IF NEW.birth_date IS NOT NULL AND TIMESTAMPDIFF(YEAR, NEW.birth_date, CURDATE()) < 13 THEN
                    SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'User must be at least 13 years old';
                END IF;
            END
            """);
    }

    // Создание каскадных триггеров
    public void createCascadeTriggers() {
        // Автоматическое обновление статистики пользователя
        jdbcTemplate.execute("""
            CREATE TRIGGER update_user_stats_on_order
                AFTER INSERT ON orders
                FOR EACH ROW
            BEGIN
                INSERT INTO user_stats (user_id, last_order_date, total_orders)
                VALUES (NEW.user_id, NEW.order_date, 1)
                ON DUPLICATE KEY UPDATE
                    last_order_date = GREATEST(last_order_date, NEW.order_date),
                    total_orders = total_orders + 1;
            END
            """);
    }

    // Управление триггерами
    public List<Map<String, Object>> listTriggers() {
        return jdbcTemplate.queryForList("""
            SELECT
                trigger_name,
                event_object_table as table_name,
                event_manipulation as trigger_event,
                action_timing as timing,
                action_statement as definition
            FROM information_schema.triggers
            WHERE trigger_schema = DATABASE()
            ORDER BY event_object_table, trigger_name
            """);
    }

    public void disableTrigger(String triggerName) {
        jdbcTemplate.execute("ALTER TABLE users DISABLE TRIGGER " + triggerName);
    }

    public void enableTrigger(String triggerName) {
        jdbcTemplate.execute("ALTER TABLE users ENABLE TRIGGER " + triggerName);
    }

    public void dropTrigger(String triggerName) {
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS " + triggerName);
    }

    // Тестирование триггеров
    public void testTriggers() {
        try {
            // Тест валидационного триггера
            jdbcTemplate.update(
                "INSERT INTO users (first_name, last_name, email) VALUES (?, ?, ?)",
                "Test", "User", "invalid-email");

        } catch (DataIntegrityViolationException e) {
            System.out.println("Trigger validation worked: " + e.getMessage());
        }

        try {
            // Тест аудита
            int userId = 1;
            jdbcTemplate.update(
                "UPDATE users SET email = ? WHERE id = ?",
                "newemail@example.com", userId);

            // Проверка аудита
            List<Map<String, Object>> audit = jdbcTemplate.queryForList(
                "SELECT * FROM user_audit WHERE user_id = ? ORDER BY changed_at DESC LIMIT 5",
                userId);

            System.out.println("Audit records created: " + audit.size());

        } catch (Exception e) {
            System.err.println("Trigger test failed: " + e.getMessage());
        }
    }
}
```

## Безопасность на уровне схемы

### Привилегии и роли

#### Создание ролей
```sql
-- Создание ролей
CREATE ROLE 'read_only_role';
CREATE ROLE 'application_role';
CREATE ROLE 'admin_role';

-- Предоставление привилегий ролям
GRANT SELECT ON myapp.* TO 'read_only_role';
GRANT SELECT, INSERT, UPDATE ON myapp.* TO 'application_role';
GRANT ALL PRIVILEGES ON myapp.* TO 'admin_role';

-- Создание пользователей с ролями
CREATE USER 'readonly_user'@'%' IDENTIFIED BY 'password123';
CREATE USER 'app_user'@'%' IDENTIFIED BY 'password456';
CREATE USER 'admin_user'@'%' IDENTIFIED BY 'password789';

-- Назначение ролей пользователям
GRANT 'read_only_role' TO 'readonly_user'@'%';
GRANT 'application_role' TO 'app_user'@'%';
GRANT 'admin_role' TO 'admin_user'@'%';

-- Активация ролей по умолчанию
SET DEFAULT ROLE 'read_only_role' FOR 'readonly_user'@'%';
SET DEFAULT ROLE 'application_role' FOR 'app_user'@'%';
SET DEFAULT ROLE 'admin_role' FOR 'admin_user'@'%';
```

#### Безопасные представления
```sql
-- Представление с ограничением доступа
CREATE VIEW user_orders_secure AS
SELECT
    o.id,
    o.order_date,
    o.total_amount,
    o.status
FROM orders o
JOIN user_permissions p ON o.user_id = p.user_id
WHERE p.can_view_orders = true
AND o.user_id = CURRENT_USER_ID();

-- Предоставление доступа к представлению
GRANT SELECT ON user_orders_secure TO 'customer_role';
```

### Шифрование данных

#### Шифрование чувствительных полей
```sql
-- Создание таблицы с шифрованием
CREATE TABLE sensitive_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    credit_card VARBINARY(255),
    ssn VARBINARY(255),
    medical_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Вставка зашифрованных данных
INSERT INTO sensitive_data (user_id, credit_card, ssn)
VALUES (
    1,
    AES_ENCRYPT('4111111111111111', SHA2('encryption_key', 256)),
    AES_ENCRYPT('123-45-6789', SHA2('encryption_key', 256))
);

-- Чтение расшифрованных данных
SELECT
    id,
    user_id,
    AES_DECRYPT(credit_card, SHA2('encryption_key', 256)) as credit_card,
    AES_DECRYPT(ssn, SHA2('encryption_key', 256)) as ssn
FROM sensitive_data;
```

### Безопасность в Java

```java
@Service
public class SecurityService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ENCRYPTION_KEY = "my-secret-encryption-key";

    // Шифрование данных перед сохранением
    public void saveEncryptedData(int userId, String creditCard, String ssn) {
        String encryptedCard = encryptData(creditCard);
        String encryptedSsn = encryptData(ssn);

        jdbcTemplate.update("""
            INSERT INTO sensitive_data (user_id, credit_card, ssn)
            VALUES (?, AES_ENCRYPT(?, SHA2(?, 256)), AES_ENCRYPT(?, SHA2(?, 256)))
            """,
            userId, encryptedCard, ENCRYPTION_KEY, encryptedSsn, ENCRYPTION_KEY);
    }

    // Расшифровка данных при чтении
    public Map<String, Object> getDecryptedData(int userId) {
        return jdbcTemplate.queryForMap("""
            SELECT
                id,
                user_id,
                AES_DECRYPT(credit_card, SHA2(?, 256)) as credit_card,
                AES_DECRYPT(ssn, SHA2(?, 256)) as ssn
            FROM sensitive_data
            WHERE user_id = ?
            """, ENCRYPTION_KEY, ENCRYPTION_KEY, userId);
    }

    private String encryptData(String data) {
        // В реальности использовать AES шифрование
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    // Row Level Security (RLS)
    public void implementRowLevelSecurity() {
        // Создание политик безопасности
        jdbcTemplate.execute("""
            CREATE FUNCTION get_current_user_id() RETURNS INT
            BEGIN
                RETURN @current_user_id;
            END
            """);

        // Политика: пользователи видят только свои заказы
        jdbcTemplate.execute("""
            CREATE VIEW user_orders_secure AS
            SELECT * FROM orders
            WHERE user_id = get_current_user_id()
            """);

        // Политика: менеджеры видят заказы своих подчиненных
        jdbcTemplate.execute("""
            CREATE VIEW manager_orders AS
            SELECT o.* FROM orders o
            JOIN user_hierarchy uh ON o.user_id = uh.subordinate_id
            WHERE uh.manager_id = get_current_user_id()
            """);
    }

    // Аудит доступа к данным
    public void createAuditTriggers() {
        jdbcTemplate.execute("""
            CREATE TRIGGER audit_sensitive_data_access
                AFTER SELECT ON sensitive_data
                FOR EACH ROW
            BEGIN
                INSERT INTO access_log (
                    table_name,
                    record_id,
                    accessed_by,
                    accessed_at,
                    access_type
                ) VALUES (
                    'sensitive_data',
                    NEW.id,
                    USER(),
                    NOW(),
                    'SELECT'
                );
            END
            """);
    }

    // Безопасное выполнение динамических запросов
    public List<Map<String, Object>> executeSafeQuery(String tableName, String column, Object value) {
        // Валидация входных параметров
        if (!isValidTableName(tableName) || !isValidColumnName(tableName, column)) {
            throw new IllegalArgumentException("Invalid table or column name");
        }

        // Параметризованный запрос для предотвращения SQL injection
        String sql = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
        return jdbcTemplate.queryForList(sql, value);
    }

    private boolean isValidTableName(String tableName) {
        List<String> validTables = jdbcTemplate.queryForList(
            "SHOW TABLES", String.class);
        return validTables.contains(tableName);
    }

    private boolean isValidColumnName(String tableName, String columnName) {
        List<String> validColumns = jdbcTemplate.query(
            "DESCRIBE " + tableName,
            (rs, rowNum) -> rs.getString("Field"));
        return validColumns.contains(columnName);
    }
}
```

## Производительность и оптимизация

### Оптимизация запросов

#### Анализ плана выполнения
```sql
-- EXPLAIN для анализа запроса
EXPLAIN SELECT
    u.username,
    COUNT(o.id) as order_count,
    SUM(o.total_amount) as total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.created_at >= '2024-01-01'
GROUP BY u.id, u.username
ORDER BY total_spent DESC
LIMIT 10;

-- Анализ с дополнительной информацией
EXPLAIN FORMAT=JSON SELECT ...;
EXPLAIN ANALYZE SELECT ...;
```

#### Оптимизация JOIN
```sql
-- Неоптимальный запрос
SELECT u.*, p.*
FROM users u
CROSS JOIN products p; -- Декартово произведение

-- Оптимизированный запрос
SELECT u.username, p.name, oi.quantity
FROM users u
JOIN orders o ON u.id = o.user_id
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE u.active = true;
```

### Профилирование в Java

```java
@Service
public class PerformanceOptimizationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Анализ плана выполнения
    public String explainQuery(String sql, Object... params) {
        String explainSql = "EXPLAIN FORMAT=JSON " + sql;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(explainSql, params);

        // Форматирование результата
        return result.stream()
            .map(row -> row.toString())
            .collect(Collectors.joining("\n"));
    }

    // Измерение производительности запроса
    public QueryMetrics measureQueryPerformance(String sql, Object... params) {
        long startTime = System.nanoTime();

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, params);

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        return new QueryMetrics(result.size(), executionTimeMs);
    }

    // Оптимизация индексов
    public List<String> suggestIndexOptimizations() {
        List<String> suggestions = new ArrayList<>();

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
            suggestions.add("Consider adding PRIMARY KEY to table: " + table);
        }

        // Поиск таблиц с низкой селективностью индексов
        List<Map<String, Object>> lowSelectivityIndexes = jdbcTemplate.queryForList("""
            SELECT
                t.table_name,
                i.index_name,
                i.cardinality,
                t.table_rows,
                ROUND(i.cardinality / t.table_rows * 100, 2) as selectivity_percent
            FROM information_schema.statistics i
            JOIN information_schema.tables t ON i.table_schema = t.table_schema
                AND i.table_name = t.table_name
            WHERE i.table_schema = DATABASE()
            AND i.index_name != 'PRIMARY'
            AND t.table_rows > 1000
            HAVING selectivity_percent < 10
            ORDER BY selectivity_percent ASC
            """);

        for (Map<String, Object> index : lowSelectivityIndexes) {
            suggestions.add(String.format(
                "Low selectivity index %s.%s: %.2f%% - consider removing",
                index.get("table_name"),
                index.get("index_name"),
                index.get("selectivity_percent")
            ));
        }

        return suggestions;
    }

    // Оптимизация запросов
    public List<String> optimizeSlowQueries() {
        List<String> optimizations = new ArrayList<>();

        // Поиск запросов без использования индексов
        List<Map<String, Object>> tableScans = jdbcTemplate.queryForList("""
            SELECT
                sql_text,
                exec_count,
                total_latency / exec_count / 1000000000 as avg_time_sec
            FROM performance_schema.events_statements_summary_by_digest
            WHERE schema_name = DATABASE()
            AND sql_text LIKE '%SELECT%'
            AND NO_INDEX_USED > 0
            AND avg_timer_wait > 1000000000
            ORDER BY avg_timer_wait DESC
            LIMIT 10
            """);

        for (Map<String, Object> query : tableScans) {
            optimizations.add("Query performing table scan: " +
                ((String) query.get("sql_text")).substring(0, 100) + "...");
        }

        return optimizations;
    }

    // Кэширование результатов
    @Cacheable("queryResults")
    public List<Map<String, Object>> getCachedQueryResult(String cacheKey) {
        // Имитация тяжелого запроса
        return jdbcTemplate.queryForList("""
            SELECT
                category,
                COUNT(*) as count,
                AVG(price) as avg_price
            FROM products
            WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 DAY)
            GROUP BY category
            """);
    }

    // Пакетная обработка
    public void processBatchUpdates(List<Product> products) {
        String sql = "UPDATE products SET price = ?, updated_at = NOW() WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Product product = products.get(i);
                ps.setBigDecimal(1, product.getPrice());
                ps.setLong(2, product.getId());
            }

            @Override
            public int getBatchSize() {
                return products.size();
            }
        });
    }
}

// Вспомогательные классы
class QueryMetrics {
    private int resultCount;
    private long executionTimeMs;

    // constructor, getters
}
```

## Миграции и versioning

### Управление миграциями

#### Создание системы миграций
```sql
-- Таблица для отслеживания миграций
CREATE TABLE schema_migrations (
    version VARCHAR(255) PRIMARY KEY,
    description TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    applied_by VARCHAR(255) DEFAULT USER(),
    checksum VARCHAR(255)
);

-- Процедура применения миграции
DELIMITER //

CREATE PROCEDURE apply_migration(
    IN migration_version VARCHAR(255),
    IN migration_description TEXT,
    IN migration_sql TEXT
)
BEGIN
    DECLARE migration_exists INT DEFAULT 0;

    -- Проверка, была ли уже применена миграция
    SELECT COUNT(*) INTO migration_exists
    FROM schema_migrations
    WHERE version = migration_version;

    IF migration_exists = 0 THEN
        -- Выполнение миграции
        SET @migration_sql = migration_sql;
        PREPARE stmt FROM @migration_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        -- Запись в журнал миграций
        INSERT INTO schema_migrations (version, description, checksum)
        VALUES (migration_version, migration_description, MD5(migration_sql));

        SELECT CONCAT('Migration ', migration_version, ' applied successfully') as result;
    ELSE
        SELECT CONCAT('Migration ', migration_version, ' already applied') as result;
    END IF;
END //

DELIMITER ;
```

### Миграции в Java

```java
@Service
public class MigrationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeMigrations() {
        // Создание таблицы миграций
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS schema_migrations (
                version VARCHAR(255) PRIMARY KEY,
                description TEXT,
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                applied_by VARCHAR(255) DEFAULT USER(),
                checksum VARCHAR(255)
            )
            """);
    }

    // Применение миграции
    public boolean applyMigration(String version, String description, String sql) {
        try {
            // Проверка, была ли уже применена миграция
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM schema_migrations WHERE version = ?",
                Integer.class, version);

            if (count > 0) {
                System.out.println("Migration " + version + " already applied");
                return true;
            }

            // Выполнение миграции
            jdbcTemplate.execute(sql);

            // Запись в журнал
            jdbcTemplate.update("""
                INSERT INTO schema_migrations (version, description, checksum)
                VALUES (?, ?, ?)
                """, version, description, generateChecksum(sql));

            System.out.println("Migration " + version + " applied successfully");
            return true;

        } catch (Exception e) {
            System.err.println("Failed to apply migration " + version + ": " + e.getMessage());
            return false;
        }
    }

    // Генерация списка миграций
    public List<Migration> getMigrations() {
        return jdbcTemplate.query("""
            SELECT version, description, applied_at, applied_by, checksum
            FROM schema_migrations
            ORDER BY applied_at DESC
            """, (rs, rowNum) -> new Migration(
                rs.getString("version"),
                rs.getString("description"),
                rs.getTimestamp("applied_at").toLocalDateTime(),
                rs.getString("applied_by"),
                rs.getString("checksum")
            ));
    }

    // Создание новой миграции
    public void createMigration(String name, String upSql, String downSql) {
        String version = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        jdbcTemplate.update("""
            INSERT INTO migration_scripts (version, name, up_sql, down_sql, created_at)
            VALUES (?, ?, ?, ?, NOW())
            """, version, name, upSql, downSql);

        System.out.println("Created migration: " + version + "_" + name);
    }

    // Rollback миграции
    public boolean rollbackMigration(String version) {
        try {
            // Получение down SQL
            String downSql = jdbcTemplate.queryForObject(
                "SELECT down_sql FROM migration_scripts WHERE version = ?",
                String.class, version);

            if (downSql != null) {
                jdbcTemplate.execute(downSql);
                jdbcTemplate.update(
                    "DELETE FROM schema_migrations WHERE version = ?", version);
                System.out.println("Rolled back migration: " + version);
                return true;
            } else {
                System.out.println("No rollback script for migration: " + version);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Failed to rollback migration " + version + ": " + e.getMessage());
            return false;
        }
    }

    private String generateChecksum(String sql) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(sql.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

// Вспомогательные классы
class Migration {
    private String version;
    private String description;
    private LocalDateTime appliedAt;
    private String appliedBy;
    private String checksum;

    // constructor, getters, setters
}
```

## Лучшие практики

### Проектирование схемы

#### 1. Следуйте нормальным формам
- **1NF**: Атомарные значения
- **2NF**: Устранение частичных зависимостей
- **3NF**: Устранение транзитивных зависимостей
- **BCNF**: Все детерминанты являются ключами

#### 2. Выбор правильных типов данных
- Используйте минимально достаточные типы
- Учитывайте **NULL** значения
- Оптимизируйте для частых операций

#### 3. Индексы
- Создавайте индексы для условий **WHERE**
- Используйте составные индексы для **JOIN**
- Мониторьте использование индексов

### Безопасность

#### 1. Принцип наименьших привилегий
- Предоставляйте минимально необходимые права
- Используйте роли вместо прямых грантов
- Регулярно пересматривайте привилегии

#### 2. Шифрование чувствительных данных
- Шифруйте **PII** данные
- Используйте **AES_ENCRYPT**/**AES_DECRYPT**
- Храните ключи **securely**

#### 3. Аудит и мониторинг
- Логируйте важные операции
- Мониторьте подозрительную активность
- Регулярно проверяйте логи

### Производительность

#### 1. Оптимизация запросов
- Используйте **EXPLAIN** для анализа
- Избегайте **SELECT** * в больших таблицах
- Оптимизируйте **JOIN** операции

#### 2. Индексы и партиционирование
- Создавайте подходящие индексы
- Используйте партиционирование для больших таблиц
- Мониторьте фрагментацию

#### 3. Кэширование и материализованные представления
- Используйте материализованные представления для агрегатов
- Кэшируйте часто используемые данные
- Оптимизируйте кэш **InnoDB**

### Поддержка и развитие

#### 1. Документирование
- Документируйте назначение таблиц и колонок
- Описывайте бизнес-правила и ограничения
- Ведите **changelog** изменений схемы

#### 2. Миграции
- Используйте систему миграций для изменений схемы
- Тестируйте миграции на копии **production**
- Имейте **rollback** скрипты

#### 3. Мониторинг
- Мониторьте использование ресурсов
- Отслеживайте производительность запросов
- Анализируйте рост данных

## Решение проблем

1. **Преждевременная денормализация**
   - Не денормализуйте без необходимости
   - Измеряйте выигрыш в производительности
   - Учитывайте стоимость обновлений

2. **Неправильное использование индексов**
   - Не создавайте индексы на все подряд
   - Мониторьте использование индексов
   - Удаляйте неиспользуемые индексы

3. **Игнорирование типов данных**
   - Выбирайте подходящие типы
   - Учитывайте хранение и производительность
   - Планируйте будущий рост

4. **Отсутствие ограничений**
   - Всегда добавляйте необходимые ограничения
   - Используйте **CHECK constraints**
   - Валидируйте данные на уровне БД

Проектирование баз данных **MySQL** — это комплексная задача, требующая учета многих факторов: производительности, безопасности, поддерживаемости и масштабируемости.

### Ключевые принципы успешного проектирования:

1. **Понимание требований**: Глубокий анализ бизнес-требований и паттернов использования
2. **Нормализация**: Правильное структурирование данных для обеспечения целостности
3. **Оптимизация**: Выбор подходящих типов данных, индексов и архитектурных решений
4. **Безопасность**: Защита данных и контроль доступа
5. **Масштабируемость**: Проектирование с учетом будущего роста

### Основные компоненты хорошо спроектированной базы данных:

- **Логическая модель**: Сущности, атрибуты, связи
- **Физическая модель**: Таблицы, колонки, индексы, ограничения
- **Оптимизации**: Индексы, партиционирование, денормализация
- **Безопасность**: Роли, привилегии, шифрование
- **Мониторинг**: Метрики производительности и использования

### Рекомендации по реализации:

1. **Начинайте с концептуального моделирования** — `ER`-диаграммы, анализ требований
2. **Применяйте нормализацию** — доведите до 3NF или **BCNF**
3. **Оптимизируйте физическую модель** — индексы, партиционирование
4. **Тестируйте производительность** — нагрузочное тестирование
5. **Планируйте сопровождение** — миграции, мониторинг, бэкапы

### Инструменты для проектирования:

- **MySQL Workbench** — визуальное моделирование
- **dbdiagram.io** — онлайн диаграммы
- **Liquibase/Flyway** — управление миграциями
- **EXPLAIN ANALYZE** — анализ запросов

Правильное проектирование базы данных — это фундамент успешного приложения. Инвестиции в качественное проектирование на начальном этапе многократно окупаются в процессе эксплуатации системы.

**Следующие темы:**
- [mysql-queries](mysql-queries.md) — запросы и оптимизация **SQL**
- [mysql-indexes](mysql-indexes.md) — индексы и производительность
- [mysql-replication](mysql-replication.md) — репликация и высокая доступность
- [mysql-performance](mysql-performance.md) — производительность и тюнинг
- [mysql-admin](mysql-admin.md) — администрирование и обслуживание

Проектирование баз данных — это баланс между идеальной нормализацией и практическими требованиями производительности и удобства использования.


