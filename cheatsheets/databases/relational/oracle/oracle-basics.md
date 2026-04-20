---
title: "Oracle Database: Основы"
description: "Комплексное руководство по использованию Oracle Database — мощной реляционной системы управления базами данных от Oracle Corporation."
tags:
  - databases
  - relational
  - oracle-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Oracle Database**: Основы

**Комплексное руководство по использованию `Oracle Database` — мощной реляционной системы управления базами данных от `Oracle Corporation`.**

## Полезные ссылки

### Официальная документация
- [Oracle Database Documentation](https://docs.oracle.com/en/database/oracle/oracle-database/) — официальная документация
- [Oracle Docker Images (GitHub)](https://github.com/oracle/docker-images) — **Docker** образы **Oracle**
- [Oracle Learning Library](https://education.oracle.com/) — обучающие материалы

### См. также
- [[postgres-basics|PostgreSQL]] — основы **PostgreSQL**
- [[mysql-basics|MySQL]] — основы **MySQL**

## Содержание

- [Введение в **Oracle Database**](#введение-в-oracle-database)
  - [Почему **Oracle**?](#почему-oracle)
  - [Архитектура **Oracle**](#архитектура-oracle)
  - [Основные компоненты](#основные-компоненты)
- [Установка и настройка](#установка-и-настройка)
  - [**Docker** установка](#docker-установка)
- [Запуск Oracle Database в Docker](#запуск-oracle-database-в-docker)
- [Подключение к базе данных](#подключение-к-базе-данных)
  - [Базовая конфигурация](#базовая-конфигурация)
- [Основы **SQL** в **Oracle**](#основы-sql-в-oracle)
  - [Создание таблиц](#создание-таблиц)
  - [Типы данных **Oracle**](#типы-данных-oracle)
  - [**CRUD** операции](#crud-операции)
- [PL/**SQL** программирование](#plsql-программирование)
  - [Блоки PL/**SQL**](#блоки-plsql)
  - [Процедуры и функции](#процедуры-и-функции)
  - [Триггеры](#триггеры)
- [Управление транзакциями](#управление-транзакциями)
  - [**ACID** свойства](#acid-свойства)
  - [Уровни изоляции](#уровни-изоляции)
- [Индексы и оптимизация](#индексы-и-оптимизация)
  - [Типы индексов](#типы-индексов)
  - [Планы выполнения](#планы-выполнения)
- [Интеграция с **Java**](#интеграция-с-java)
  - [**JDBC** подключение](#jdbc-подключение)
  - [**Spring Boot** интеграция](#spring-boot-интеграция)
- [**Best Practices**](#лучшие-практики)
  - [Производительность](#производительность)
  - [Безопасность](#безопасность)
  - [Преимущества **Oracle Database**](#преимущества-oracle-database)
  - [Основные паттерны использования](#основные-паттерны-использования)
- [Решение проблем](#решение-проблем)

## Введение в **Oracle Database**

**Oracle Database** — это коммерческая реляционная СУБД, разработанная **Oracle Corporation**. Одна из самых мощных и функциональных баз данных, широко используемая в **enterprise** приложениях.

### Почему **Oracle**?

**Oracle Database** предлагает множество преимуществ:**

1. **Высокая производительность** — Оптимизированный движок для больших нагрузок
2. **Масштабируемость** — Поддержка кластеров и распределенных систем
3. **Надежность** — **ACID** транзакции, репликация, **backup**/**recovery**
4. **Безопасность** — Расширенные механизмы безопасности
5. **PL/SQL** — Мощный процедурный язык программирования
6. **Enterprise features** — Партиционирование, сжатие, шифрование
7. **Широкая поддержка** — Используется в крупных корпорациях
8. **Активное развитие** — Регулярные обновления и новые функции

### Архитектура **Oracle**

**Oracle** использует архитектуру с разделением памяти и процессов:**

- **SGA (**System Global Area**)** — Общая память для всех процессов
- **PGA (**Program Global Area**)** — Память для каждого процесса
- **Background Processes** — Фоновые процессы для управления БД
- **Instance** — Экземпляр **Oracle** (**память + процессы**)
- **Database** — Физические файлы данных

### Основные компоненты

Пример подключения к **Oracle** через **JDBC** в **Java**: загрузка драйвера, получение соединения, выполнение запроса.

```java
// Основные компоненты Oracle Database: SGA и фоновые процессы
/*
 * Основные компоненты Oracle Database
 */
public class OracleComponents {
    
    /*
     * SGA компоненты
     */
    public enum SGAComponent {
        SHARED_POOL("Shared Pool", "Кэш SQL запросов и словарь данных"),
        DATABASE_BUFFER_CACHE("Database Buffer Cache", "Кэш данных из таблиц"),
        REDO_LOG_BUFFER("Redo Log Buffer", "Буфер для redo логов"),
        LARGE_POOL("Large Pool", "Память для больших операций"),
        JAVA_POOL("Java Pool", "Память для Java объектов");
        
        private final String name;
        private final String description;
        
        SGAComponent(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
    
    /*
     * Фоновые процессы Oracle
     */
    public enum BackgroundProcess {
        DBWn("Database Writer", "Запись данных на диск"),
        LGWR("Log Writer", "Запись redo логов"),
        CKPT("Checkpoint", "Синхронизация данных"),
        SMON("System Monitor", "Восстановление после сбоев"),
        PMON("Process Monitor", "Мониторинг процессов"),
        ARCn("Archiver", "Архивирование redo логов");
        
        private final String name;
        private final String description;
        
        BackgroundProcess(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
```

## Установка и настройка

### **Docker** установка

```bash
# Запуск Oracle Database в Docker
docker run -d --name oracle-db \
  -p 1521:1521 \
  -p 5500:5500 \
  -e ORACLE_PWD=Oracle123 \
  -e ORACLE_CHARACTERSET=AL32UTF8 \
  container-registry.oracle.com/database/express:21.3.0-xe

# Подключение к базе данных
sqlplus sys/Oracle123@localhost:1521/XE as sysdba
```

### Базовая конфигурация

```sql
-- Создание пользователя
CREATE USER myuser IDENTIFIED BY mypassword;

-- Предоставление прав
GRANT CONNECT, RESOURCE TO myuser;
GRANT CREATE SESSION TO myuser;
GRANT CREATE TABLE TO myuser;

-- Создание табличного пространства
CREATE TABLESPACE mytablespace
DATAFILE '/u01/app/oracle/oradata/mytablespace.dbf' SIZE 100M
AUTOEXTEND ON NEXT 10M MAXSIZE 1G;
```

## Основы **SQL** в **Oracle**

### Создание таблиц

```sql
/*
 * Создание таблицы в Oracle Database
 * Демонстрирует использование различных типов данных Oracle
 */
-- Создание таблицы пользователей с Oracle-специфичными типами данных
CREATE TABLE users (
    -- NUMBER - основной числовой тип Oracle (точность, масштаб)
    id NUMBER(10) PRIMARY KEY,                    -- Целое число до 10 цифр
    -- VARCHAR2 - строковый тип (максимальная длина в байтах/символах)
    username VARCHAR2(50) NOT NULL,               -- Строка до 50 символов
    email VARCHAR2(100) UNIQUE,                    -- Уникальный email
    -- DATE - тип даты и времени Oracle (хранит дату и время)
    created_at DATE DEFAULT SYSDATE,               -- Дата создания (по умолчанию текущая)
    -- NUMBER с плавающей точкой
    balance NUMBER(10, 2),                         -- Десятичное число (10 цифр, 2 после запятой)
    -- CLOB - Character Large Object для больших текстов
    description CLOB,                              -- Большой текст (до 4GB)
    -- BLOB - Binary Large Object для бинарных данных
    avatar BLOB,                                   -- Бинарные данные (изображения, файлы)
    -- TIMESTAMP - более точная дата/время чем DATE
    last_login TIMESTAMP,                          -- Точное время последнего входа
    -- BOOLEAN через NUMBER(1) или CHAR(1)
    is_active NUMBER(1) DEFAULT 1                  -- 1 = true, 0 = false
);

-- Создание таблицы заказов с внешним ключом
CREATE TABLE orders (
    order_id NUMBER(10) PRIMARY KEY,
    user_id NUMBER(10) NOT NULL,
    -- NUMBER для денежных сумм
    total_amount NUMBER(10, 2) NOT NULL,
    -- DATE для даты заказа
    order_date DATE DEFAULT SYSDATE,
    -- VARCHAR2 для статуса
    status VARCHAR2(20) DEFAULT 'PENDING',
    -- Внешний ключ с каскадным удалением
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создание индекса для оптимизации запросов
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_order_user_date ON orders(user_id, order_date);
```

### Типы данных **Oracle**

```sql
/*
 * Основные типы данных Oracle Database
 */
-- Числовые типы
CREATE TABLE numeric_types (
    -- NUMBER - универсальный числовой тип
    id NUMBER(10),                    -- Целое число до 10 цифр
    price NUMBER(10, 2),              -- Десятичное число (10 цифр, 2 после запятой)
    -- NUMBER без параметров - максимальная точность
    big_number NUMBER,                -- Произвольная точность
    
    -- INTEGER, SMALLINT, BIGINT - синонимы NUMBER
    small_int INTEGER,                -- Эквивалент NUMBER(38)
    big_int BIGINT                    -- Эквивалент NUMBER(38)
);

-- Строковые типы
CREATE TABLE string_types (
    -- VARCHAR2 - основной строковый тип (рекомендуется)
    name VARCHAR2(100),               -- Строка до 100 байт (или символов в AL32UTF8)
    -- CHAR - фиксированная длина (дополняется пробелами)
    code CHAR(10),                    -- Фиксированная строка 10 символов
    -- CLOB - для больших текстов
    description CLOB,                 -- Большой текст (до 4GB)
    -- NCHAR, NVARCHAR2 - для Unicode данных
    unicode_name NVARCHAR2(100)        -- Unicode строка
);

-- Типы даты и времени
CREATE TABLE date_types (
    -- DATE - дата и время (точность до секунды)
    created DATE DEFAULT SYSDATE,     -- Текущая дата и время
    -- TIMESTAMP - более точная дата/время
    updated TIMESTAMP,                -- Точность до наносекунд
    -- TIMESTAMP WITH TIME ZONE - с часовым поясом
    event_time TIMESTAMP WITH TIME ZONE,
    -- INTERVAL - интервалы времени
    duration INTERVAL DAY TO SECOND   -- Интервал дней, часов, минут, секунд
);

-- Бинарные типы
CREATE TABLE binary_types (
    -- BLOB - Binary Large Object
    image_data BLOB,                  -- Бинарные данные (изображения, файлы)
    -- RAW - бинарные данные фиксированной длины
    hash_value RAW(32),               -- Хеш значение (32 байта)
    -- BFILE - ссылка на файл в файловой системе
    file_ref BFILE                    -- Ссылка на внешний файл
);
```

### **CRUD** операции

```sql
/*
 * CRUD операции в Oracle Database
 */

-- INSERT - вставка данных
INSERT INTO users (id, username, email, balance) 
VALUES (1, 'john_doe', 'john@example.com', 1000.50);

-- INSERT с подзапросом
INSERT INTO users (id, username, email)
SELECT user_id, username, email FROM temp_users WHERE status = 'ACTIVE';

-- INSERT множественных строк (Oracle 23c+)
INSERT INTO users (id, username, email) VALUES
    (2, 'jane_smith', 'jane@example.com'),
    (3, 'bob_jones', 'bob@example.com');

-- UPDATE - обновление данных
UPDATE users 
SET balance = balance + 100, 
    last_login = SYSDATE
WHERE id = 1;

-- UPDATE с подзапросом
UPDATE users u
SET balance = (
    SELECT SUM(total_amount) 
    FROM orders 
    WHERE user_id = u.id
)
WHERE EXISTS (
    SELECT 1 FROM orders WHERE user_id = u.id
);

-- DELETE - удаление данных
DELETE FROM users WHERE id = 1;

-- DELETE с условием
DELETE FROM orders 
WHERE order_date < ADD_MONTHS(SYSDATE, -12);  -- Удалить заказы старше года

-- SELECT - выборка данных
SELECT id, username, email, balance, created_at
FROM users
WHERE balance > 1000
ORDER BY created_at DESC;

-- SELECT с JOIN
SELECT u.username, o.order_id, o.total_amount, o.order_date
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE u.is_active = 1
ORDER BY o.order_date DESC;
```

## `PL`/**SQL** программирование

### Блоки `PL`/**SQL**

```sql
/*
 * Базовые блоки PL/SQL в Oracle
 * PL/SQL - процедурный язык программирования Oracle
 */

-- Анонимный блок PL/SQL
DECLARE
    -- Объявление переменных
    v_user_id NUMBER(10) := 1;                    -- Переменная с начальным значением
    v_username VARCHAR2(50);                      -- Переменная без начального значения
    v_balance NUMBER(10, 2);                      -- Переменная для баланса
    v_count NUMBER;                               -- Счетчик
BEGIN
    -- Выполняемый код
    -- Получение данных из таблицы
    SELECT username, balance INTO v_username, v_balance
    FROM users
    WHERE id = v_user_id;
    
    -- Вывод информации
    DBMS_OUTPUT.PUT_LINE('Пользователь: ' || v_username);
    DBMS_OUTPUT.PUT_LINE('Баланс: ' || v_balance);
    
    -- Условная логика
    IF v_balance > 1000 THEN
        DBMS_OUTPUT.PUT_LINE('Премиум пользователь');
    ELSIF v_balance > 500 THEN
        DBMS_OUTPUT.PUT_LINE('Стандартный пользователь');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Базовый пользователь');
    END IF;
    
    -- Цикл
    FOR i IN 1..10 LOOP
        DBMS_OUTPUT.PUT_LINE('Итерация: ' || i);
    END LOOP;
    
    -- Обработка исключений
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Пользователь не найден');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Ошибка: ' || SQLERRM);
END;
/
```

### Процедуры и функции

```sql
/*
 * Создание хранимых процедур и функций в Oracle PL/SQL
 */

-- Создание процедуры для создания пользователя
CREATE OR REPLACE PROCEDURE create_user(
    p_username IN VARCHAR2,          -- Входной параметр
    p_email IN VARCHAR2,
    p_balance IN NUMBER DEFAULT 0,    -- Параметр с значением по умолчанию
    p_user_id OUT NUMBER              -- Выходной параметр
) AS
    v_count NUMBER;                   -- Локальная переменная
BEGIN
    -- Проверка существования пользователя
    SELECT COUNT(*) INTO v_count
    FROM users
    WHERE username = p_username OR email = p_email;
    
    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Пользователь уже существует');
    END IF;
    
    -- Генерация ID
    SELECT NVL(MAX(id), 0) + 1 INTO p_user_id FROM users;
    
    -- Вставка нового пользователя
    INSERT INTO users (id, username, email, balance, created_at)
    VALUES (p_user_id, p_username, p_email, p_balance, SYSDATE);
    
    COMMIT;
    
    DBMS_OUTPUT.PUT_LINE('Пользователь создан с ID: ' || p_user_id);
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Использование процедуры
DECLARE
    v_new_id NUMBER;
BEGIN
    create_user('newuser', 'newuser@example.com', 500, v_new_id);
    DBMS_OUTPUT.PUT_LINE('Создан пользователь с ID: ' || v_new_id);
END;
/

-- Создание функции для расчета баланса пользователя
CREATE OR REPLACE FUNCTION calculate_user_balance(
    p_user_id IN NUMBER
) RETURN NUMBER AS
    v_balance NUMBER(10, 2) := 0;
    v_order_total NUMBER(10, 2);
BEGIN
    -- Получение текущего баланса
    SELECT balance INTO v_balance
    FROM users
    WHERE id = p_user_id;
    
    -- Расчет суммы заказов
    SELECT NVL(SUM(total_amount), 0) INTO v_order_total
    FROM orders
    WHERE user_id = p_user_id;
    
    -- Возврат итогового баланса
    RETURN v_balance - v_order_total;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN OTHERS THEN
        RAISE;
END;
/

-- Использование функции
SELECT id, username, calculate_user_balance(id) AS available_balance
FROM users;
```

### Триггеры

```sql
/
 * Создание триггеров в Oracle Database
 * Триггеры автоматически выполняются при определенных событиях
 */

-- Триггер BEFORE INSERT для автоматической генерации ID
CREATE OR REPLACE TRIGGER trg_users_before_insert
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    -- Автоматическая генерация ID если не указан
    IF :NEW.id IS NULL THEN
        SELECT NVL(MAX(id), 0) + 1 INTO :NEW.id FROM users;
    END IF;
    
    -- Установка даты создания если не указана
    IF :NEW.created_at IS NULL THEN
        :NEW.created_at := SYSDATE;
    END IF;
    
    -- Валидация email
    IF :NEW.email IS NOT NULL AND INSTR(:NEW.email, '@') = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Неверный формат email');
    END IF;
END;
/

-- Триггер AFTER UPDATE для логирования изменений
CREATE OR REPLACE TRIGGER trg_users_after_update
AFTER UPDATE ON users
FOR EACH ROW
DECLARE
    v_changes VARCHAR2(1000);
BEGIN
    -- Логирование изменений баланса
    IF :OLD.balance != :NEW.balance THEN
        v_changes := 'Баланс изменен с ' || :OLD.balance || ' на ' || :NEW.balance;
        -- В реальности запись в таблицу логов
        DBMS_OUTPUT.PUT_LINE('Пользователь ' || :NEW.id || ': ' || v_changes);
    END IF;
END;
/

-- Триггер INSTEAD OF для представлений
CREATE OR REPLACE VIEW user_orders_view AS
SELECT u.id, u.username, o.order_id, o.total_amount, o.order_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id;

CREATE OR REPLACE TRIGGER trg_user_orders_instead_of
INSTEAD OF INSERT ON user_orders_view
FOR EACH ROW
BEGIN
    -- Вставка в базовую таблицу при вставке в представление
    INSERT INTO orders (order_id, user_id, total_amount, order_date)
    VALUES (:NEW.order_id, :NEW.id, :NEW.total_amount, :NEW.order_date);
END;
/
```

## Управление транзакциями

### **ACID** свойства

```sql
/
 * Демонстрация ACID свойств в Oracle
 */

-- ATOMICITY (Атомарность) - все или ничего
BEGIN
    -- Начало транзакции
    INSERT INTO users (id, username, email) VALUES (10, 'user1', 'user1@example.com');
    INSERT INTO orders (order_id, user_id, total_amount) VALUES (1, 10, 100.50);
    
    -- Если произойдет ошибка, все изменения откатятся
    -- COMMIT - подтверждение всех изменений
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- ROLLBACK - откат всех изменений
        ROLLBACK;
        RAISE;
END;
/

-- CONSISTENCY (Согласованность) - проверка ограничений
ALTER TABLE users ADD CONSTRAINT chk_balance CHECK (balance >= 0);

-- Попытка установить отрицательный баланс вызовет ошибку
INSERT INTO users (id, username, email, balance) 
VALUES (11, 'user2', 'user2@example.com', -100);  -- Ошибка: нарушение ограничения

-- ISOLATION (Изоляция) - уровни изоляции
-- Oracle поддерживает: READ COMMITTED (по умолчанию), SERIALIZABLE, READ ONLY

-- Установка уровня изоляции SERIALIZABLE
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- DURABILITY (Долговечность) - гарантия сохранения после COMMIT
COMMIT;  -- Изменения гарантированно сохранены на диск
```

### Уровни изоляции

```sql
/
 * Уровни изоляции транзакций в Oracle
 */

-- READ COMMITTED (по умолчанию)
-- Видны только зафиксированные данные других транзакций
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

BEGIN
    -- Эта транзакция видит только зафиксированные данные
    SELECT * FROM users WHERE id = 1;
    -- Не видит незафиксированные изменения из других транзакций
END;
/

-- SERIALIZABLE
-- Полная изоляция, как будто транзакции выполняются последовательно
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

BEGIN
    -- Эта транзакция видит снимок данных на момент начала
    SELECT * FROM users;
    -- Изменения других транзакций не видны до COMMIT
END;
/

-- READ ONLY
-- Только чтение, без возможности изменений
SET TRANSACTION READ ONLY;

BEGIN
    SELECT * FROM users;
    -- INSERT/UPDATE/DELETE не разрешены
END;
COMMIT;
```

## Индексы и оптимизация

### Типы индексов

```sql
/
 * Создание различных типов индексов в Oracle
 */

-- B-Tree индекс (по умолчанию)
CREATE INDEX idx_users_email ON users(email);

-- Уникальный индекс
CREATE UNIQUE INDEX idx_users_username ON users(username);

-- Составной индекс
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);

-- Функциональный индекс
CREATE INDEX idx_users_upper_email ON users(UPPER(email));

-- Bitmap индекс (для колонок с небольшим количеством уникальных значений)
CREATE BITMAP INDEX idx_users_active ON users(is_active);

-- Партиционированный индекс
CREATE INDEX idx_orders_date_part ON orders(order_date) LOCAL;

-- Анализ использования индексов
EXPLAIN PLAN FOR
SELECT * FROM users WHERE email = 'john@example.com';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
```

### Планы выполнения

```sql
/
 * Анализ планов выполнения запросов в Oracle
 */

-- Включение автопроса планов выполнения
ALTER SESSION SET STATISTICS_LEVEL = ALL;

-- Выполнение запроса с сбором статистики
SELECT /*+ GATHER_PLAN_STATISTICS */ 
    u.username, o.order_id, o.total_amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE u.balance > 1000;

-- Просмотр плана выполнения
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(NULL, NULL, 'ALLSTATS LAST'));

-- Использование подсказок оптимизатора
SELECT /*+ INDEX(users idx_users_email) */ 
    username, email
FROM users
WHERE email LIKE 'john%';

-- Принудительное использование конкретного индекса
SELECT /*+ INDEX(users idx_users_email) */
    *
FROM users
WHERE email = 'john@example.com';
```

## Интеграция с **Java**

### **JDBC** подключение

```java
/
 * Подключение к Oracle Database через JDBC
 * Демонстрирует использование Oracle JDBC драйвера
 */
import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;

public class OracleJDBCConnection {
    
    /
     * Подключение через OracleDataSource (рекомендуемый способ)
     * OracleDataSource предоставляет connection pooling и оптимизации
     */
    public static Connection getConnection() throws SQLException {
        // Создание OracleDataSource для управления соединениями
        OracleDataSource ds = new OracleDataSource();
        
        // Настройка параметров подключения
        ds.setURL("jdbc:oracle:thin:@localhost:1521:XE");  // thin driver для клиент-сервер
        // Формат URL: jdbc:oracle:thin:@host:port:service_name
        // XE - это service name для Oracle Express Edition
        
        ds.setUser("myuser");      // Имя пользователя для подключения
        ds.setPassword("mypassword");  // Пароль для подключения
        
        // Дополнительные настройки connection pool
        ds.setConnectionCachingEnabled(true);  // Включение кэширования соединений
        ds.setConnectionCacheProperties(new Properties());  // Свойства кэша
        
        // Получение соединения из пула
        return ds.getConnection();
    }
    
    /
     * Подключение через DriverManager (простой способ)
     * Используется для простых приложений без connection pooling
     */
    public static Connection getSimpleConnection() throws SQLException {
        // Регистрация Oracle JDBC драйвера
        // В новых версиях Java драйвер регистрируется автоматически
        Class.forName("oracle.jdbc.driver.OracleDriver");
        
        // Создание соединения через DriverManager
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String username = "myuser";
        String password = "mypassword";
        
        return DriverManager.getConnection(url, username, password);
    }
    
    /
     * Выполнение простого запроса
     */
    public static void executeQuery() throws SQLException {
        try (Connection conn = getConnection()) {
            // Создание PreparedStatement для параметризованного запроса
            // PreparedStatement защищает от SQL injection и улучшает производительность
            String sql = "SELECT id, username, email, balance FROM users WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Установка параметра (индекс начинается с 1)
                stmt.setInt(1, 1);  // Устанавливаем значение для первого параметра (?)
                
                // Выполнение запроса
                try (ResultSet rs = stmt.executeQuery()) {
                    // Обработка результатов
                    while (rs.next()) {
                        int id = rs.getInt("id");                    // Получение значения по имени колонки
                        String username = rs.getString("username");   // Получение строки
                        String email = rs.getString("email");
                        double balance = rs.getDouble("balance");     // Получение числа с плавающей точкой
                        
                        System.out.printf("ID: %d, Username: %s, Email: %s, Balance: %.2f%n",
                                        id, username, email, balance);
                    }
                }
            }
        }
    }
    
    /
     * Выполнение INSERT с использованием RETURNING (Oracle специфичная фича)
     * RETURNING позволяет получить значения после вставки без дополнительного запроса
     */
    public static int insertUser(String username, String email, double balance) throws SQLException {
        try (Connection conn = getConnection()) {
            // Использование RETURNING для получения сгенерированного ID
            String sql = """
                INSERT INTO users (username, email, balance, created_at)
                VALUES (?, ?, ?, SYSDATE)
                RETURNING id INTO ?
                """;
            
            // Oracle требует CallableStatement для RETURNING
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                // Установка входных параметров
                stmt.setString(1, username);   // Первый параметр - username
                stmt.setString(2, email);      // Второй параметр - email
                stmt.setDouble(3, balance);   // Третий параметр - balance
                
                // Регистрация выходного параметра для RETURNING
                stmt.registerOutParameter(4, Types.INTEGER);  // Четвертый параметр - возвращаемый id
                
                // Выполнение вставки
                stmt.execute();
                
                // Получение сгенерированного ID
                int newId = stmt.getInt(4);
                System.out.println("Создан пользователь с ID: " + newId);
                
                return newId;
            }
        }
    }
    
    /
     * Работа с PL/SQL процедурами
     */
    public static void callProcedure(int userId) throws SQLException {
        try (Connection conn = getConnection()) {
            // Вызов хранимой процедуры
            String sql = "{ CALL create_user(?, ?, ?, ?) }";
            // Формат: { CALL procedure_name(param1, param2, ...) }
            
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                // Установка входных параметров
                stmt.setString(1, "newuser");      // p_username
                stmt.setString(2, "newuser@example.com");  // p_email
                stmt.setDouble(3, 500.0);         // p_balance
                
                // Регистрация выходного параметра
                stmt.registerOutParameter(4, Types.INTEGER);  // p_user_id (OUT)
                
                // Выполнение процедуры
                stmt.execute();
                
                // Получение выходного параметра
                int newUserId = stmt.getInt(4);
                System.out.println("Процедура вернула ID: " + newUserId);
            }
        }
    }
    
    /
     * Работа с транзакциями
     */
    public static void transactionExample() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            // Отключение auto-commit для ручного управления транзакциями
            conn.setAutoCommit(false);
            
            // Выполнение нескольких операций в одной транзакции
            try (PreparedStatement stmt1 = conn.prepareStatement(
                    "UPDATE users SET balance = balance - ? WHERE id = ?")) {
                stmt1.setDouble(1, 100.0);
                stmt1.setInt(2, 1);
                stmt1.executeUpdate();
            }
            
            try (PreparedStatement stmt2 = conn.prepareStatement(
                    "INSERT INTO orders (order_id, user_id, total_amount) VALUES (?, ?, ?)")) {
                stmt2.setInt(1, 100);
                stmt2.setInt(2, 1);
                stmt2.setDouble(3, 100.0);
                stmt2.executeUpdate();
            }
            
            // Подтверждение транзакции
            conn.commit();
            System.out.println("Транзакция успешно завершена");
            
        } catch (SQLException e) {
            // Откат транзакции при ошибке
            if (conn != null) {
                conn.rollback();
                System.out.println("Транзакция откачена из-за ошибки: " + e.getMessage());
            }
            throw e;
        } finally {
            // Восстановление auto-commit и закрытие соединения
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}
```

### **Spring Boot** интеграция

```java
/
 * Конфигурация Oracle Database для Spring Boot
 */
@Configuration
public class OracleConfiguration {
    
    /
     * Настройка DataSource для Oracle
     */
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        // Использование HikariCP connection pool (рекомендуется)
        HikariConfig config = new HikariConfig();
        
        // Oracle JDBC URL
        config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:XE");
        config.setUsername("myuser");
        config.setPassword("mypassword");
        config.setDriverClassName("oracle.jdbc.OracleDriver");
        
        // Настройки пула для Oracle
        config.setMaximumPoolSize(10);  // Oracle не любит большие пулы
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        
        // Oracle-специфичные настройки
        config.addDataSourceProperty("oracle.net.CONNECT_TIMEOUT", "10000");
        config.addDataSourceProperty("oracle.jdbc.ReadTimeout", "30000");
        
        return new HikariDataSource(config);
    }
    
    /
     * Настройка JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

/
 * Репозиторий для работы с Oracle через Spring Data JDBC
 */
@Repository
public class UserRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /
     * Поиск пользователя по ID
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, email, balance, created_at FROM users WHERE id = ?";
        
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setBalance(rs.getDouble("balance"));
                u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return u;
            });
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    /
     * Вызов PL/SQL функции
     */
    public double calculateBalance(int userId) {
        String sql = "{ ? = CALL calculate_user_balance(?) }";
        
        return jdbcTemplate.execute((ConnectionCallback<Double>) conn -> {
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.registerOutParameter(1, Types.DOUBLE);
                stmt.setInt(2, userId);
                stmt.execute();
                return stmt.getDouble(1);
            }
        });
    }
}
```

## Лучшие практики

### Производительность

```sql
/
 * Рекомендации по оптимизации производительности Oracle
 */

-- 1. Использование индексов для часто запрашиваемых колонок
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);

-- 2. Партиционирование больших таблиц
CREATE TABLE orders_partitioned (
    order_id NUMBER(10) PRIMARY KEY,
    user_id NUMBER(10),
    total_amount NUMBER(10, 2),
    order_date DATE
)
PARTITION BY RANGE (order_date) (
    PARTITION p2023_q1 VALUES LESS THAN (DATE '2023-04-01'),
    PARTITION p2023_q2 VALUES LESS THAN (DATE '2023-07-01'),
    PARTITION p2023_q3 VALUES LESS THAN (DATE '2023-10-01'),
    PARTITION p2023_q4 VALUES LESS THAN (DATE '2024-01-01')
);

-- 3. Использование bind variables для предотвращения hard parsing
-- Хорошо (bind variable):
SELECT * FROM users WHERE id = :user_id;

-- Плохо (literal):
SELECT * FROM users WHERE id = 1;

-- 4. Анализ таблиц для обновления статистики
ANALYZE TABLE users COMPUTE STATISTICS;
ANALYZE TABLE orders COMPUTE STATISTICS;

-- Или через DBMS_STATS (рекомендуется)
EXEC DBMS_STATS.GATHER_TABLE_STATS('MYSCHEMA', 'USERS');
```

### Безопасность

```sql
/
 * Рекомендации по безопасности Oracle Database
 */

-- 1. Создание пользователей с минимальными правами
CREATE USER app_user IDENTIFIED BY "StrongPassword123!";
GRANT CONNECT, RESOURCE TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON myuser.users TO app_user;

-- 2. Использование ролей для управления правами
CREATE ROLE app_reader;
GRANT SELECT ON myuser.users TO app_reader;
GRANT SELECT ON myuser.orders TO app_reader;

CREATE ROLE app_writer;
GRANT INSERT, UPDATE, DELETE ON myuser.users TO app_writer;
GRANT INSERT, UPDATE, DELETE ON myuser.orders TO app_writer;

GRANT app_reader TO app_user;
GRANT app_writer TO app_user;

-- 3. Аудит действий пользователей
AUDIT SELECT, INSERT, UPDATE, DELETE ON users;
AUDIT SELECT, INSERT, UPDATE, DELETE ON orders;

-- Просмотр аудита
SELECT username, action_name, obj_name, timestamp
FROM dba_audit_trail
WHERE obj_name IN ('USERS', 'ORDERS')
ORDER BY timestamp DESC;

-- 4. Шифрование чувствительных данных
-- Использование Oracle Advanced Security для шифрования колонок
ALTER TABLE users MODIFY (email ENCRYPT USING 'AES256');
```

**Oracle Database** — это мощная **enterprise-grade** СУБД с богатым функционалом. Она предоставляет высокую производительность, надежность и безопасность для критически важных приложений.

### Преимущества **Oracle Database**

1. **Высокая производительность** — Оптимизированный движок для больших нагрузок
2. **Масштабируемость** — Поддержка кластеров и распределенных систем
3. **Надежность** — **ACID** транзакции, репликация, **backup**/**recovery**
4. **Безопасность** — Расширенные механизмы безопасности
5. **PL/SQL** — Мощный процедурный язык программирования
6. **Enterprise features** — Партиционирование, сжатие, шифрование

### Основные паттерны использования

1. **Enterprise приложения** — Крупные корпоративные системы
2. **Финансовые системы** — Требующие высокой надежности
3. **Data warehousing** — Хранение и анализ больших объемов данных
4. **OLTP системы** — Высоконагруженные транзакционные системы

## Решение проблем

**ORA-12541 (TNS:no listener):** сервис прослушивания не запущен или недоступен. Запустите `lsnrctl start`, проверьте `listener.ora` (порт, хост) и файрвол. На клиенте проверьте `tnsnames.ora` или connection string.

**ORA-12154 (TNS:could not resolve connect identifier):** проверьте имя сервиса/SID в строке подключения и конфигурацию `tnsnames.ora` или настройки JDBC (URL, service_name). Убедитесь, что listener зарегистрировал сервис.

**Медленные запросы:** используйте AWR/ASH для анализа, проверьте планы выполнения и индексы. Оптимизируйте SQL, настройте статистику (`DBMS_STATS`), при необходимости добавьте подсказки (hints). Проверьте блокировки и ожидания.

**Нехватка места (ORA-01653, ORA-01654):** увеличьте размер табличного пространства или добавьте файлы данных. Настройте авторасширение. Очистите архивные логи и временные объекты. Мониторьте использование табличных пространств.

---


