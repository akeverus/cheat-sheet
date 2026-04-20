---
title: "MySQL: Основы - Полное руководство по реляционной базе данных"
description: "Комплексное руководство по MySQL: архитектура, установка, настройка, основные понятия и работа с данными."
tags:
  - databases
  - relational
  - mysql-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# MySQL: Основы — Полное руководство по реляционной базе данных

Комплексное руководство по **MySQL**: архитектура, установка, настройка, основные понятия и работа с данными.

## Полезные ссылки

### Официальная документация
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [MySQL Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/)
- [MySQL Workbench](https://dev.mysql.com/doc/workbench/en/)

### Обучающие материалы
- [Spring Boot with MySQL](https://www.baeldung.com/spring-boot-mysql) — **Spring Boot** и **MySQL**
- [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/) — **JDBC** драйвер

### См. также
- [[postgres-basics|PostgreSQL]] — сравнение с **PostgreSQL**
- [[mysql-design|mysql-design.md]] — проектирование баз данных
- [[mysql-queries|mysql-queries.md]] — **SQL** запросы в **MySQL**

## Содержание

- [Введение в **MySQL**](#введение-в-mysql)
  - [Ключевые особенности **MySQL**](#ключевые-особенности-mysql)
    - [Надежность и производительность](#надежность-и-производительность)
    - [Гибкость и расширяемость](#гибкость-и-расширяемость)
    - [Простота использования](#простота-использования)
  - [Версии **MySQL**](#версии-mysql)
    - [**MySQL** 8.0 (рекомендуемая версия)](#mysql-80-рекомендуемая-версия)
    - [**MySQL** 5.7 (LTS)](#mysql-57-lts)
    - [**MariaDB** (форк MySQL)](#mariadb-форк-mysql)
  - [Сравнение с другими СУБД](#сравнение-с-другими-субд)
- [Архитектура **MySQL**](#архитектура-mysql)
  - [Компоненты **MySQL**](#компоненты-mysql)
    - [**MySQL Server**](#mysql-server)
    - [Клиентские инструменты](#клиентские-инструменты)
  - [Движки хранения (Storage Engines)](#движки-хранения-storage-engines)
    - [**InnoDB** (по умолчанию)](#innodb-по-умолчанию)
    - [**MyISAM**](#myisam)
    - [Другие движки](#другие-движки)
  - [Архитектура подключений](#архитектура-подключений)
    - [**Connection Pool**](#connection-pool)
    - [**Thread Pool**](#thread-pool)
- [Установка и настройка](#установка-и-настройка)
  - [Установка на разных ОС](#установка-на-разных-ос)
    - [**Ubuntu**/**Debian**](#ubuntudebian)
- [Обновление пакетов](#обновление-пакетов)
- [Установка MySQL Server](#установка-mysql-server)
- [Запуск сервиса](#запуск-сервиса)
- [Безопасная настройка](#безопасная-настройка)
    - [**CentOS**/**RHEL**](#centosrhel)
- [Установка из репозитория](#установка-из-репозитория)
- [Первоначальная настройка](#первоначальная-настройка)
    - [**Docker**](#docker)
- [Запуск MySQL в Docker](#запуск-mysql-в-docker)
- [Подключение](#подключение)
  - [Конфигурация **MySQL**](#конфигурация-mysql)
    - [Основной конфигурационный файл](#основной-конфигурационный-файл)
- [/etc/mysql/mysql.conf.d/mysqld.cnf](#etcmysqlmysqlconfdmysqldcnf)
- [Базовые настройки](#базовые-настройки)
- [Движок по умолчанию](#движок-по-умолчанию)
- [Настройки памяти](#настройки-памяти)
- [Настройки подключений](#настройки-подключений)
- [Логирование](#логирование)
- [Безопасность](#безопасность)
    - [Оптимизация для разных нагрузок](#оптимизация-для-разных-нагрузок)
- [InnoDB оптимизация](#innodb-оптимизация)
- [MyISAM для чтения](#myisam-для-чтения)
- [Временные таблицы](#временные-таблицы)
- [Query cache](#query-cache)
- [Подключение к **MySQL**](#подключение-к-mysql)
  - [**JDBC** подключение](#jdbc-подключение)
  - [**Spring Data JPA**](#spring-data-jpa)
  - [**Spring JDBC Template**](#spring-jdbc-template)
- [Основные понятия](#основные-понятия)
  - [Базы данных и таблицы](#базы-данных-и-таблицы)
    - [Создание базы данных](#создание-базы-данных)
    - [Создание таблиц](#создание-таблиц)
  - [Первичные ключи](#первичные-ключи)
    - [**AUTO_INCREMENT**](#auto_increment)
    - [**Composite Primary Key**](#composite-primary-key)
  - [Индексы](#индексы)
    - [Типы индексов](#типы-индексов)
    - [Использование индексов в запросах](#использование-индексов-в-запросах)
- [Типы данных](#типы-данных)
  - [Числовые типы](#числовые-типы)
    - [Целые числа](#целые-числа)
    - [Работа с числами в **Java**](#работа-с-числами-в-java)
  - [Строковые типы](#строковые-типы)
    - [**CHAR** vs **VARCHAR**](#char-vs-varchar)
    - [Работа со строками](#работа-со-строками)
  - [Дата и время](#дата-и-время)
    - [Типы даты и времени](#типы-даты-и-времени)
    - [Работа с датами в **Java**](#работа-с-датами-в-java)
  - [**JSON** тип](#json-тип)
    - [Работа с **JSON** в **MySQL** 8.0+](#работа-с-json-в-mysql-80)
    - [Работа с **JSON** в **Java**](#работа-с-json-в-java)
- [**DDL** операции](#ddl-операции)
    - [Базовое создание таблицы](#базовое-создание-таблицы)
    - [Создание таблицы в **Java**](#создание-таблицы-в-java)
  - [Изменение таблиц](#изменение-таблиц)
    - [**ALTER TABLE** операции](#alter-table-операции)
    - [**ALTER** в **Java**](#alter-в-java)
  - [Удаление таблиц](#удаление-таблиц)
    - [**DROP TABLE** операции](#drop-table-операции)
    - [Управление таблицами в **Java**](#управление-таблицами-в-java)
- [**DML** операции](#dml-операции)
  - [**INSERT** операции](#insert-операции)
    - [Базовые вставки](#базовые-вставки)
    - [Продвинутые **INSERT**](#продвинутые-insert)
    - [**INSERT** в **Java**](#insert-в-java)
  - [**UPDATE** операции](#update-операции)
    - [Базовые обновления](#базовые-обновления)
    - [Продвинутые **UPDATE**](#продвинутые-update)
    - [**UPDATE** в **Java**](#update-в-java)
  - [**DELETE** операции](#delete-операции)
    - [Базовые удаления](#базовые-удаления)
    - [Продвинутые **DELETE**](#продвинутые-delete)
    - [**DELETE** в **Java**](#delete-в-java)
- [Ограничения и индексы](#ограничения-и-индексы)
  - [Ограничения целостности](#ограничения-целостности)
    - [**PRIMARY KEY**](#primary-key)
    - [**FOREIGN KEY**](#foreign-key)
    - [**UNIQUE** и **CHECK** ограничения](#unique-и-check-ограничения)
    - [Работа с ограничениями в **Java**](#работа-с-ограничениями-в-java)
  - [Индексы в **MySQL**](#индексы-в-mysql)
    - [Управление индексами](#управление-индексами)
    - [Оптимизация индексов в **Java**](#оптимизация-индексов-в-java)
- [Транзакции](#транзакции)
  - [**ACID** свойства](#acid-свойства)
    - [**Atomicity** (Атомарность)](#atomicity-атомарность)
    - [**Consistency** (Согласованность)](#consistency-согласованность)
    - [**Isolation** (Изоляция)](#isolation-изоляция)
    - [**Durability** (Долговечность)](#durability-долговечность)
  - [Управление транзакциями в **MySQL**](#управление-транзакциями-в-mysql)
    - [Базовые транзакции](#базовые-транзакции)
    - [**SAVEPOINT**](#savepoint)
    - [Уровни изоляции](#уровни-изоляции)
  - [Транзакции в **Java**](#транзакции-в-java)
    - [Программное управление транзакциями](#программное-управление-транзакциями)
    - [**Spring Transaction Management**](#spring-transaction-management)
  - [Обработка **deadlock**'ов](#обработка-deadlockов)
- [Хранимые процедуры](#хранимые-процедуры)
  - [Создание хранимых процедур](#создание-хранимых-процедур)
    - [Базовая процедура](#базовая-процедура)
    - [Процедура с параметрами](#процедура-с-параметрами)
    - [Процедура с результатами](#процедура-с-результатами)
  - [Вызов хранимых процедур](#вызов-хранимых-процедур)
  - [Управление процедурами](#управление-процедурами)
  - [Хранимые процедуры в **Java**](#хранимые-процедуры-в-java)
- [Триггеры](#триггеры)
  - [Создание триггеров](#создание-триггеров)
    - [**BEFORE INSERT** триггер](#before-insert-триггер)
    - [**AFTER UPDATE** триггер](#after-update-триггер)
    - [**BEFORE DELETE** триггер](#before-delete-триггер)
  - [Управление триггерами](#управление-триггерами)
  - [Триггеры в **Java**](#триггеры-в-java)
- [Представления](#представления)
  - [Создание представлений](#создание-представлений)
    - [Базовое представление](#базовое-представление)
    - [Представление с **JOIN**](#представление-с-join)
    - [Материализованное представление (MySQL 8.0+)](#материализованное-представление-mysql-80)
  - [Управление представлениями](#управление-представлениями)
  - [Представления в **Java**](#представления-в-java)
  - [Пользователи и привилегии](#пользователи-и-привилегии)
    - [Создание пользователей](#создание-пользователей)
    - [Привилегии](#привилегии)
    - [Управление привилегиями](#управление-привилегиями)
  - [Безопасность в **Java**](#безопасность-в-java)
  - [Шифрование данных](#шифрование-данных)
  - [Аудит и логирование](#аудит-и-логирование)
- [Мониторинг](#мониторинг)
  - [Системные таблицы **MySQL**](#системные-таблицы-mysql)
    - [Общая информация о сервере](#общая-информация-о-сервере)
    - [Статистика подключений](#статистика-подключений)
    - [Производительность запросов](#производительность-запросов)
  - [Мониторинг в **Java**](#мониторинг-в-java)
- [**Best Practices**](#лучшие-практики)
  - [Проектирование базы данных](#проектирование-базы-данных)
  - [Производительность](#производительность)
  - [Резервное копирование](#резервное-копирование)
  - [Преимущества использования **MySQL** в **Java**/**Spring** приложениях:](#преимущества-использования-mysql-в-javaspring-приложениях)
  - [Рекомендации по использованию:](#рекомендации-по-использованию)
- [Решение проблем](#решение-проблем)

## Введение в MySQL

**MySQL** — это самая популярная в мире система управления реляционными базами данных с открытым исходным кодом. Разработанная компанией **MySQL** `AB` (позже приобретенной Oracle), **MySQL** является основой для множества веб-приложений, от небольших сайтов до крупных корпоративных систем.

### Ключевые особенности MySQL

#### Надежность и производительность
- **ACID транзакции** — гарантия целостности данных
- **Высокая производительность** — оптимизированные запросы и индексы
- **Масштабируемость** — поддержка больших объемов данных
- **Надежность** — репликация и кластеризация

#### Гибкость и расширяемость
- **Множество движков хранения** — **InnoDB**, **MyISAM**, **Memory**, **etc**.
- **Расширяемая архитектура** — плагины и **UDF**
- **Поддержка стандартов** — **SQL**:2003, **GIS**, **JSON**
- **Интеграция** — с популярными языками и фреймворками

#### Простота использования
- **Легкая установка** — готовые пакеты для всех ОС
- **Простой синтаксис SQL** — стандартные запросы
- **Отличная документация** — подробные руководства
- **Большое сообщество** — поддержка и ресурсы

### Версии MySQL

#### MySQL `8.0` (рекомендуемая версия)
- **Window Functions** — оконные функции
- **Common `Table` Expressions** — рекурсивные запросы
- **JSON** — нативная поддержка **JSON**
- **GIS** — геопространственные данные
- **Roles** — ролевая модель безопасности
- **Performance Schema** — улучшенный мониторинг

#### MySQL `5.7` (LTS)
- Стабильная версия для **production**
- Полная поддержка до `2023` года

#### MariaDB (форк MySQL)
- Полная совместимость с **MySQL**
- Дополнительные возможности
- Более активная разработка

### Сравнение с другими СУБД

| Характеристика | **MySQL** | **PostgreSQL** | **Oracle** |
|---|---|---|---|
| **Лицензия** | **GPL** | **PostgreSQL** | Проприетарная |
| **Производительность** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Функциональность** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Простота** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| **Сообщество** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

## Архитектура MySQL

### Компоненты MySQL

#### MySQL Server
**Основной процесс, включающий:**
- **SQL Parser** — разбор **SQL** запросов
- **Query Optimizer** — оптимизация запросов
- **Storage Engines** — движки хранения
- **Connection Manager** — управление подключениями
- **Cache Manager** — управление кэшами

#### Клиентские инструменты
- **mysql** — командная строка
- **MySQL Workbench** — графический интерфейс
- **phpMyAdmin** — веб-интерфейс
- **MySQL `Connector`/J** — **JDBC** драйвер

### Движки хранения (Storage Engines)

#### InnoDB (по умолчанию)

Пример создания таблицы с движком **InnoDB**: первичный ключ с автоинкрементом, поля имени и **email**, временная метка создания.

```sql
-- Создание таблицы с InnoDB
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
```

**Особенности `InnoDB`:**
- **ACID транзакции** — полная поддержка
- **Foreign Keys** — ограничения внешних ключей
- **Row-level locking** — блокировка на уровне строк
- **MVCC** — многовариантность для консистентного чтения
- **Crash recovery** — восстановление после сбоев
- **Full-text search** — полнотекстовый поиск

#### MyISAM
```sql
-- Создание таблицы с MyISAM
CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM;
```

**Особенности `MyISAM`:**
- **Table-level locking** — блокировка на уровне таблиц
- **Full-text search** — эффективный полнотекстовый поиск
- **GIS support** — геопространственные данные
- **Compact storage** — меньший размер на диске
- **Fast reads** — быстрые операции чтения

#### Другие движки
- **Memory** — хранение в оперативной памяти
- **CSV** — хранение в **CSV** файлах
- **Archive** — сжатое хранение для логов
- **Federated** — доступ к удаленным таблицам

### Архитектура подключений

#### Connection Pool
```java
// Конфигурация источника данных MySQL (HikariCP)
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/myapp");
        config.setUsername("user");
        config.setPassword("password");

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);

        return new HikariDataSource(config);
    }
}
```

#### Thread Pool
**MySQL** использует многопоточную архитектуру:**
- **Main thread** — управление сервером
- **Connection threads** — обработка клиентских подключений
- **Worker threads** — выполнение запросов
- **Utility threads** — обслуживание и очистка

## Установка и настройка

### Установка на разных ОС

#### Ubuntu/Debian
```bash
# Обновление пакетов
sudo apt update

# Установка MySQL Server
sudo apt install mysql-server

# Запуск сервиса
sudo systemctl start mysql
sudo systemctl enable mysql

# Безопасная настройка
sudo mysql_secure_installation
```

#### CentOS/RHEL
```bash
# Установка из репозитория
sudo yum install mysql-server

# Запуск сервиса
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Первоначальная настройка
sudo mysql_secure_installation
```

#### Docker
```bash
# Запуск MySQL в Docker
docker run --name mysql-container \
  -e MYSQL_ROOT_PASSWORD=my-secret-pw \
  -e MYSQL_DATABASE=myapp \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=password \
  -p 3306:3306 -d mysql:8.0

# Подключение
docker exec -it mysql-container mysql -u root -p
```

### Конфигурация MySQL

#### Основной конфигурационный файл
```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf

[mysqld]
# Базовые настройки
bind-address = 127.0.0.1
port = 3306
socket = /var/run/mysqld/mysqld.sock

# Движок по умолчанию
default-storage-engine = InnoDB

# Настройки памяти
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M

# Настройки подключений
max_connections = 100
wait_timeout = 28800

# Логирование
general_log = 0
slow_query_log = 1
slow_query_log_file = /var/log/mysql/mysql-slow.log
long_query_time = 2

# Безопасность
skip-name-resolve
```

#### Оптимизация для разных нагрузок

**Для `OLTP` (транзакционные системы):**
```ini
[mysqld]
# InnoDB оптимизация
innodb_buffer_pool_size = 2G
innodb_log_file_size = 512M
innodb_flush_log_at_trx_commit = 1
innodb_thread_concurrency = 16

# Connection pool
max_connections = 200
innodb_max_dirty_pages_pct = 90
```

**Для аналитики:**
```ini
[mysqld]
# MyISAM для чтения
key_buffer_size = 2G
table_open_cache = 4096

# Временные таблицы
tmp_table_size = 512M
max_heap_table_size = 512M

# Query cache
query_cache_size = 256M
query_cache_type = ON
```

## Подключение к MySQL

### JDBC подключение
```java
// Подключение к MySQL через JDBC и проверка
@Service
public class MySQLConnectionService {

    @Autowired
    private DataSource dataSource;

    // Базовое подключение
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Проверка подключения
    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    // Получение информации о сервере
    public String getServerInfo() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            return String.format("MySQL %s (%s)",
                metaData.getDatabaseProductVersion(),
                metaData.getDatabaseProductName());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get server info", e);
        }
    }
}
```

### Spring Data JPA
```java
// Включение JPA репозиториев и настройка EntityManager
@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
public class JpaConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://localhost:3306/myapp?useSSL=false&serverTimezone=UTC")
            .username("user")
            .password("password")
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        return properties;
    }
}
```

### Spring JDBC Template
```java
// Репозиторий с JdbcTemplate: запросы и маппинг на объекты
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Поиск пользователя по ID
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, name, email, created_at FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new User(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ), id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Сохранение пользователя
    public User save(User user) {
        String sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setTimestamp(3, Timestamp.valueOf(user.getCreatedAt()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }
}
```

## Основные понятия

### Базы данных и таблицы

#### Создание базы данных
```sql
-- Создание базы данных
CREATE DATABASE myapp
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Использование базы данных
USE myapp;

-- Просмотр баз данных
SHOW DATABASES;

-- Удаление базы данных
DROP DATABASE IF EXISTS old_db;
```

#### Создание таблиц
```sql
-- Простая таблица
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Таблица с внешними ключами
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'paid', 'shipped', 'delivered') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_created_at (created_at)
);
```

### Первичные ключи

#### AUTO_INCREMENT
```sql
-- Первичный ключ с автоинкрементом
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(8,2) NOT NULL
);

-- Вставка данных
INSERT INTO products (name, price) VALUES
('Laptop', 999.99),
('Mouse', 29.99),
('Keyboard', 79.99);

-- Результат: id будут 1, 2, 3
```

#### Composite Primary Key
```sql
-- Составной первичный ключ
CREATE TABLE order_items (
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(8,2) NOT NULL,

    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### Индексы

#### Типы индексов
```sql
-- B-Tree индекс (по умолчанию)
CREATE INDEX idx_users_email ON users(email);

-- Уникальный индекс
CREATE UNIQUE INDEX idx_users_username ON users(username);

-- Составной индекс
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);

-- FULLTEXT индекс
ALTER TABLE articles ADD FULLTEXT INDEX idx_content (title, content);

-- SPATIAL индекс
ALTER TABLE locations ADD SPATIAL INDEX idx_coords (coordinates);
```

#### Использование индексов в запросах
```sql
-- Использует индекс idx_users_email
SELECT * FROM users WHERE email = 'john@example.com';

-- Использует составной индекс
SELECT * FROM orders
WHERE user_id = 123 AND created_at >= '2024-01-01';

-- FULLTEXT поиск
SELECT * FROM articles
WHERE MATCH(title, content) AGAINST('database optimization' IN NATURAL LANGUAGE MODE);

-- SPATIAL запрос
SELECT * FROM locations
WHERE ST_Distance(coordinates, ST_GeomFromText('POINT(37.7749 -122.4194)')) < 1000;
```

## Типы данных

### Числовые типы

#### Целые числа
```sql
-- Таблица с различными целыми типами
CREATE TABLE numeric_demo (
    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Целые числа
    tiny_val TINYINT,      -- -128 до 127 (0 до 255 unsigned)
    small_val SMALLINT,    -- -32,768 до 32,767
    medium_val MEDIUMINT,  -- -8,388,608 до 8,388,607
    normal_val INT,        -- -2,147,483,648 до 2,147,483,647
    big_val BIGINT,        -- -9,223,372,036,854,775,808 до 9,223,372,036,854,775,807

    -- С плавающей точкой
    float_val FLOAT,       -- ~7 десятичных цифр
    double_val DOUBLE,     -- ~15 десятичных цифр

    -- Фиксированная точность
    decimal_val DECIMAL(10,2), -- точные десятичные числа
    numeric_val NUMERIC(8,3)   -- синоним DECIMAL
);
```

#### Работа с числами в Java
```java
// Сервис для работы с числовыми типами MySQL (BigDecimal, целые)
@Service
public class NumericDataService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void demonstrateNumericTypes() {
        // Вставка различных числовых значений
        String sql = """
            INSERT INTO numeric_demo
            (tiny_val, small_val, medium_val, normal_val, big_val,
             float_val, double_val, decimal_val, numeric_val)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
            (byte) 127,           // TINYINT
            (short) 32767,        // SMALLINT
            8388607,              // MEDIUMINT
            2147483647,           // INT
            9223372036854775807L, // BIGINT
            3.14159f,             // FLOAT
            2.718281828459045,    // DOUBLE
            new BigDecimal("12345.67"), // DECIMAL
            new BigDecimal("123.456")   // NUMERIC
        );
    }

    public void handleLargeNumbers() {
        // Работа с большими числами
        String sql = "SELECT big_val, decimal_val FROM numeric_demo WHERE id = ?";
        jdbcTemplate.query(sql, (rs) -> {
            long bigValue = rs.getLong("big_val");
            BigDecimal decimalValue = rs.getBigDecimal("decimal_val");

            System.out.println("Big value: " + bigValue);
            System.out.println("Decimal value: " + decimalValue);
        }, 1);
    }
}
```

### Строковые типы

#### CHAR vs VARCHAR
```sql
-- Сравнение строковых типов
CREATE TABLE string_demo (
    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Фиксированной длины (дополняется пробелами)
    code CHAR(5),              -- 'ABC  ' (3 символа + 2 пробела)

    -- Переменной длины
    name VARCHAR(100),         -- до 100 символов
    description TEXT,          -- до 65,535 байт
    content MEDIUMTEXT,        -- до 16,777,215 байт
    large_content LONGTEXT,    -- до 4,294,967,295 байт

    -- Бинарные строки
    binary_data BINARY(16),    -- фиксированная бинарная строка
    varbinary_data VARBINARY(255), -- переменная бинарная строка

    -- Unicode
    unicode_text VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);
```

#### Работа со строками
```java
@Service
public class StringDataService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void demonstrateStringTypes() {
        String sql = """
            INSERT INTO string_demo
            (code, name, description, unicode_text)
            VALUES (?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
            "ABC",                    // CHAR(5) -> 'ABC  '
            "John Doe",               // VARCHAR
            "This is a long description that can contain various characters and symbols...",
            "Привет мир! 🌍"         // Unicode
        );
    }

    public void stringOperations() {
        // Работа со строками в запросах
        String searchSql = """
            SELECT name, description
            FROM string_demo
            WHERE name LIKE ?
            AND LENGTH(description) > ?
            AND LOCATE('database', LOWER(description)) > 0
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            searchSql, "%John%", 50);

        for (Map<String, Object> row : results) {
            System.out.println("Name: " + row.get("name"));
            System.out.println("Description: " + row.get("description"));
        }
    }
}
```

### Дата и время

#### Типы даты и времени
```sql
-- Таблица с временными типами
CREATE TABLE temporal_demo (
    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Дата
    birth_date DATE,                    -- '2024-01-15'

    -- Время
    event_time TIME,                    -- '14:30:45'

    -- Дата и время
    created_at DATETIME,                -- '2024-01-15 14:30:45'
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- С микросекундами
    precise_time DATETIME(6),           -- микросекунды
    event_timestamp TIMESTAMP(3),       -- миллисекунды

    -- Год
    production_year YEAR                -- 1901-2155
);
```

#### Работа с датами в Java
```java
@Service
public class TemporalDataService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void demonstrateTemporalTypes() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate birthDate = LocalDate.of(1990, 1, 15);
        LocalTime eventTime = LocalTime.of(14, 30, 45);

        String sql = """
            INSERT INTO temporal_demo
            (birth_date, event_time, created_at, precise_time)
            VALUES (?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
            Date.valueOf(birthDate),           // DATE
            Time.valueOf(eventTime),           // TIME
            Timestamp.valueOf(now),            // DATETIME
            Timestamp.valueOf(now)             // DATETIME(6)
        );
    }

    public void temporalQueries() {
        // Запросы с датами
        String sql = """
            SELECT id, created_at, updated_at
            FROM temporal_demo
            WHERE DATE(created_at) = ?
            AND HOUR(created_at) BETWEEN 9 AND 17
            AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
            ORDER BY created_at DESC
            """;

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
            sql, Date.valueOf(today));

        for (Map<String, Object> row : results) {
            Timestamp createdAt = (Timestamp) row.get("created_at");
            System.out.println("Created: " + createdAt.toLocalDateTime());
        }
    }

    public void dateFunctions() {
        // Использование функций даты
        String sql = """
            SELECT
                id,
                DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') as formatted_date,
                DATEDIFF(NOW(), created_at) as days_since_creation,
                DATE_ADD(created_at, INTERVAL 30 DAY) as expires_at,
                YEAR(created_at) as creation_year,
                MONTHNAME(created_at) as creation_month
            FROM temporal_demo
            WHERE created_at >= ?
            """;

        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        jdbcTemplate.query(sql, (rs) -> {
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Formatted: " + rs.getString("formatted_date"));
            System.out.println("Days since: " + rs.getInt("days_since_creation"));
        }, Timestamp.valueOf(weekAgo));
    }
}
```

### JSON тип

#### Работа с JSON в MySQL 8.0+
```sql
-- Таблица с JSON полем
CREATE TABLE user_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    profile_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_id (user_id),
    CONSTRAINT fk_user_profiles_user_id
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Вставка JSON данных
INSERT INTO user_profiles (user_id, profile_data) VALUES
(1, '{
    "name": "John Doe",
    "age": 30,
    "preferences": {
        "theme": "dark",
        "notifications": true
    },
    "tags": ["developer", "mysql", "java"]
}'),
(2, '{
    "name": "Jane Smith",
    "age": 25,
    "preferences": {
        "theme": "light",
        "notifications": false
    },
    "tags": ["designer", "ui", "ux"]
}');

-- Запросы к JSON
SELECT
    user_id,
    JSON_EXTRACT(profile_data, '$.name') as name,
    JSON_EXTRACT(profile_data, '$.age') as age,
    JSON_EXTRACT(profile_data, '$.preferences.theme') as theme,
    JSON_CONTAINS(profile_data, '"developer"', '$.tags') as is_developer
FROM user_profiles
WHERE JSON_EXTRACT(profile_data, '$.age') > 25;

-- Обновление JSON
UPDATE user_profiles
SET profile_data = JSON_SET(
    profile_data,
    '$.age', 31,
    '$.preferences.notifications', false,
    '$.last_updated', NOW()
)
WHERE user_id = 1;
```

#### Работа с JSON в Java
```java
@Service
public class JsonDataService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveUserProfile(int userId, UserProfile profile) {
        String jsonData = objectMapper.writeValueAsString(profile);

        String sql = """
            INSERT INTO user_profiles (user_id, profile_data)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE profile_data = VALUES(profile_data)
            """;

        jdbcTemplate.update(sql, userId, jsonData);
    }

    public UserProfile getUserProfile(int userId) {
        String sql = "SELECT profile_data FROM user_profiles WHERE user_id = ?";

        String jsonData = jdbcTemplate.queryForObject(sql, String.class, userId);
        return objectMapper.readValue(jsonData, UserProfile.class);
    }

    public void updateUserPreferences(int userId, String theme, boolean notifications) {
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

    public List<String> findUsersByTag(String tag) {
        String sql = """
            SELECT JSON_EXTRACT(profile_data, '$.name') as name
            FROM user_profiles
            WHERE JSON_CONTAINS(profile_data, ?, '$.tags')
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), "\"" + tag + "\"");
    }

    public List<Map<String, Object>> getUserStats() {
        String sql = """
            SELECT
                JSON_EXTRACT(profile_data, '$.name') as name,
                JSON_EXTRACT(profile_data, '$.age') as age,
                JSON_LENGTH(profile_data, '$.tags') as tag_count
            FROM user_profiles
            WHERE JSON_EXTRACT(profile_data, '$.age') >= ?
            ORDER BY JSON_EXTRACT(profile_data, '$.age') DESC
            """;

        return jdbcTemplate.queryForList(sql, 25);
    }
}

@Entity
@Table(name = "user_profiles")
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "profile_data", columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private UserProfile profileData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

@Converter
public class JsonConverter implements AttributeConverter<UserProfile, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UserProfile attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    @Override
    public UserProfile convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, UserProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting from JSON", e);
        }
    }
}
```

## DDL операции

### Создание таблиц

#### Базовое создание таблицы
```sql
-- Простая таблица
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    hire_date DATE NOT NULL,
    salary DECIMAL(10,2),
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_department (department_id),
    INDEX idx_hire_date (hire_date),
    INDEX idx_name (last_name, first_name)
);
```

#### Создание таблицы в Java
```java
@Service
public class TableManagementService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createEmployeeTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS employees (
                id INT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                email VARCHAR(100) UNIQUE,
                hire_date DATE NOT NULL,
                salary DECIMAL(10,2),
                department_id INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                INDEX idx_department (department_id),
                INDEX idx_hire_date (hire_date),
                INDEX idx_name (last_name, first_name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;

        jdbcTemplate.execute(sql);
    }

    public void createTableWithConstraints() {
        String sql = """
            CREATE TABLE departments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE,
                manager_id INT,
                budget DECIMAL(12,2),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                FOREIGN KEY (manager_id) REFERENCES employees(id),
                CHECK (budget > 0),
                INDEX idx_name (name)
            ) ENGINE=InnoDB
            """;

        jdbcTemplate.execute(sql);
    }
}
```

### Изменение таблиц

#### ALTER TABLE операции
```sql
-- Добавление столбца
ALTER TABLE employees ADD COLUMN phone VARCHAR(20);

-- Изменение типа столбца
ALTER TABLE employees MODIFY COLUMN salary DECIMAL(12,2);

-- Переименование столбца
ALTER TABLE employees CHANGE COLUMN first_name given_name VARCHAR(50);

-- Удаление столбца
ALTER TABLE employees DROP COLUMN phone;

-- Добавление индекса
ALTER TABLE employees ADD INDEX idx_salary (salary);

-- Добавление внешнего ключа
ALTER TABLE employees ADD CONSTRAINT fk_department
    FOREIGN KEY (department_id) REFERENCES departments(id);

-- Добавление CHECK ограничения
ALTER TABLE employees ADD CONSTRAINT chk_salary_positive
    CHECK (salary > 0);

-- Изменение ENGINE
ALTER TABLE employees ENGINE=MyISAM;
```

#### ALTER в Java
```java
@Service
public class TableAlterationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addColumn() {
        String sql = "ALTER TABLE employees ADD COLUMN middle_name VARCHAR(50) AFTER first_name";
        jdbcTemplate.execute(sql);
    }

    public void modifyColumn() {
        String sql = "ALTER TABLE employees MODIFY COLUMN email VARCHAR(150)";
        jdbcTemplate.execute(sql);
    }

    public void addIndex() {
        String sql = "ALTER TABLE employees ADD INDEX idx_email (email)";
        jdbcTemplate.execute(sql);
    }

    public void addForeignKey() {
        String sql = """
            ALTER TABLE employees ADD CONSTRAINT fk_department
            FOREIGN KEY (department_id) REFERENCES departments(id)
            ON DELETE SET NULL ON UPDATE CASCADE
            """;
        jdbcTemplate.execute(sql);
    }

    public void addCheckConstraint() {
        String sql = "ALTER TABLE employees ADD CONSTRAINT chk_salary_range CHECK (salary BETWEEN 0 AND 1000000)";
        jdbcTemplate.execute(sql);
    }

    public void renameTable() {
        String sql = "ALTER TABLE employees RENAME TO staff";
        jdbcTemplate.execute(sql);
    }
}
```

### Удаление таблиц

#### DROP TABLE операции
```sql
-- Удаление таблицы
DROP TABLE temp_data;

-- Удаление с проверкой существования
DROP TABLE IF EXISTS temp_data;

-- Каскадное удаление (если есть зависимости)
DROP TABLE departments CASCADE;

-- Очистка таблицы (без удаления структуры)
TRUNCATE TABLE temp_data;
```

#### Управление таблицами в Java
```java
@Service
public class TableDropService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        jdbcTemplate.execute(sql);
    }

    public void truncateTable(String tableName) {
        String sql = "TRUNCATE TABLE " + tableName;
        jdbcTemplate.execute(sql);
    }

    public boolean tableExists(String tableName) {
        try {
            String sql = "SELECT 1 FROM " + tableName + " LIMIT 1";
            jdbcTemplate.queryForObject(sql, Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getAllTables() {
        String sql = """
            SELECT TABLE_NAME
            FROM information_schema.TABLES
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_TYPE = 'BASE TABLE'
            ORDER BY TABLE_NAME
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

    public void createBackupTable(String originalTable, String backupTable) {
        String sql = "CREATE TABLE " + backupTable + " AS SELECT * FROM " + originalTable;
        jdbcTemplate.execute(sql);
    }
}
```

## DML операции

### INSERT операции

#### Базовые вставки
```sql
-- Простая вставка
INSERT INTO employees (first_name, last_name, email, hire_date, salary)
VALUES ('John', 'Doe', 'john.doe@company.com', '2024-01-15', 75000.00);

-- Вставка нескольких строк
INSERT INTO employees (first_name, last_name, email, hire_date, salary) VALUES
('Jane', 'Smith', 'jane.smith@company.com', '2024-01-16', 80000.00),
('Bob', 'Johnson', 'bob.johnson@company.com', '2024-01-17', 70000.00),
('Alice', 'Brown', 'alice.brown@company.com', '2024-01-18', 85000.00);

-- Вставка с подзапросом
INSERT INTO employee_history (employee_id, department_id, start_date)
SELECT id, department_id, hire_date
FROM employees
WHERE hire_date >= '2024-01-01';
```

#### Продвинутые INSERT
```sql
-- INSERT с ON DUPLICATE KEY UPDATE
INSERT INTO employees (id, first_name, last_name, email, updated_at) VALUES
(1, 'John', 'Doe', 'john.doe@company.com', NOW())
ON DUPLICATE KEY UPDATE
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    updated_at = NOW();

-- INSERT с IGNORE (игнорировать ошибки)
INSERT IGNORE INTO employees (email, first_name, last_name)
VALUES ('existing@email.com', 'Test', 'User');

-- INSERT с DEFAULT значениями
INSERT INTO employees (first_name, last_name) VALUES ('Anonymous', 'User');
-- Остальные поля получат значения по умолчанию
```

#### INSERT в Java
```java
@Repository
public class EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertEmployee(Employee employee) {
        String sql = """
            INSERT INTO employees
            (first_name, last_name, email, hire_date, salary, department_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(sql,
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail(),
            Date.valueOf(employee.getHireDate()),
            employee.getSalary(),
            employee.getDepartmentId());
    }

    public int[] insertEmployeesBatch(List<Employee> employees) {
        String sql = """
            INSERT INTO employees
            (first_name, last_name, email, hire_date, salary, department_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Employee employee = employees.get(i);
                ps.setString(1, employee.getFirstName());
                ps.setString(2, employee.getLastName());
                ps.setString(3, employee.getEmail());
                ps.setDate(4, Date.valueOf(employee.getHireDate()));
                ps.setBigDecimal(5, employee.getSalary());
                ps.setInt(6, employee.getDepartmentId());
            }

            @Override
            public int getBatchSize() {
                return employees.size();
            }
        });
    }

    public int upsertEmployee(Employee employee) {
        String sql = """
            INSERT INTO employees
            (id, first_name, last_name, email, hire_date, salary, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, NOW())
            ON DUPLICATE KEY UPDATE
                first_name = VALUES(first_name),
                last_name = VALUES(last_name),
                email = VALUES(email),
                salary = VALUES(salary),
                updated_at = NOW()
            """;

        return jdbcTemplate.update(sql,
            employee.getId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail(),
            Date.valueOf(employee.getHireDate()),
            employee.getSalary());
    }

    public void insertWithGeneratedKey(Employee employee) {
        String sql = """
            INSERT INTO employees
            (first_name, last_name, email, hire_date, salary)
            VALUES (?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getEmail());
            ps.setDate(4, Date.valueOf(employee.getHireDate()));
            ps.setBigDecimal(5, employee.getSalary());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            employee.setId(keyHolder.getKey().longValue());
        }
    }
}
```

### UPDATE операции

#### Базовые обновления
```sql
-- Обновление одного поля
UPDATE employees SET salary = salary * 1.10 WHERE id = 1;

-- Обновление нескольких полей
UPDATE employees SET
    salary = salary * 1.05,
    updated_at = NOW(),
    bonus = salary * 0.02
WHERE department_id = 5 AND hire_date < '2023-01-01';

-- Обновление с подзапросом
UPDATE employees SET
    department_id = (SELECT id FROM departments WHERE name = 'Engineering')
WHERE email LIKE '%@engineering.company.com';

-- Обновление с JOIN
UPDATE employees e
JOIN departments d ON e.department_id = d.id
SET e.salary = e.salary * 1.03
WHERE d.budget > 1000000;
```

#### Продвинутые UPDATE
```sql
-- UPDATE с LIMIT
UPDATE employees SET status = 'inactive'
WHERE last_login < DATE_SUB(NOW(), INTERVAL 1 YEAR)
ORDER BY last_login ASC
LIMIT 1000;

-- UPDATE с CASE
UPDATE products SET
    discount = CASE
        WHEN category = 'electronics' THEN 0.10
        WHEN category = 'books' THEN 0.05
        WHEN category = 'clothing' THEN 0.15
        ELSE 0.00
    END,
    updated_at = NOW()
WHERE stock_quantity < 10;

-- UPDATE с JSON
UPDATE user_profiles SET
    profile_data = JSON_SET(
        profile_data,
        '$.last_updated', NOW(),
        '$.status', 'updated'
    )
WHERE user_id = 123;
```

#### UPDATE в Java
```java
@Service
public class EmployeeUpdateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int updateEmployeeSalary(Long employeeId, BigDecimal newSalary) {
        String sql = "UPDATE employees SET salary = ?, updated_at = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql, newSalary, employeeId);
    }

    public int updateEmployeeDepartment(Long employeeId, Integer departmentId) {
        String sql = """
            UPDATE employees SET
                department_id = ?,
                updated_at = NOW()
            WHERE id = ?
            """;
        return jdbcTemplate.update(sql, departmentId, employeeId);
    }

    public int giveSalaryIncrease(String department, BigDecimal percentage) {
        String sql = """
            UPDATE employees SET
                salary = salary * (1 + ? / 100),
                updated_at = NOW()
            WHERE department_id = (
                SELECT id FROM departments WHERE name = ?
            )
            """;
        return jdbcTemplate.update(sql, percentage, department);
    }

    public int updateEmployeeDetails(Employee employee) {
        String sql = """
            UPDATE employees SET
                first_name = ?,
                last_name = ?,
                email = ?,
                salary = ?,
                updated_at = NOW()
            WHERE id = ?
            """;

        return jdbcTemplate.update(sql,
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail(),
            employee.getSalary(),
            employee.getId());
    }

    public int updateLastLogin(Long employeeId) {
        String sql = "UPDATE employees SET last_login = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql, employeeId);
    }

    public int bulkUpdateSalaries(BigDecimal percentage) {
        String sql = """
            UPDATE employees SET
                salary = salary * (1 + ? / 100),
                updated_at = NOW()
            WHERE hire_date < DATE_SUB(NOW(), INTERVAL 2 YEAR)
            """;
        return jdbcTemplate.update(sql, percentage);
    }
}
```

### DELETE операции

#### Базовые удаления
```sql
-- Удаление по ID
DELETE FROM employees WHERE id = 1;

-- Удаление с условием
DELETE FROM employees WHERE hire_date < '2020-01-01';

-- Удаление с подзапросом
DELETE FROM employees WHERE department_id IN (
    SELECT id FROM departments WHERE budget < 10000
);

-- Удаление с JOIN
DELETE e FROM employees e
JOIN departments d ON e.department_id = d.id
WHERE d.name = 'Obsolete Department';
```

#### Продвинутые DELETE
```sql
-- DELETE с LIMIT
DELETE FROM audit_log
WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR)
ORDER BY created_at ASC
LIMIT 10000;

-- DELETE с использованием временной таблицы
CREATE TEMPORARY TABLE employees_to_delete (
    id INT PRIMARY KEY
);

INSERT INTO employees_to_delete (id)
SELECT id FROM employees WHERE hire_date < '2010-01-01';

DELETE e FROM employees e
JOIN employees_to_delete etd ON e.id = etd.id;

DROP TEMPORARY TABLE employees_to_delete;
```

#### DELETE в Java
```java
@Service
public class EmployeeDeleteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int deleteEmployee(Long employeeId) {
        String sql = "DELETE FROM employees WHERE id = ?";
        return jdbcTemplate.update(sql, employeeId);
    }

    public int deleteEmployeesByDepartment(String departmentName) {
        String sql = """
            DELETE FROM employees
            WHERE department_id = (
                SELECT id FROM departments WHERE name = ?
            )
            """;
        return jdbcTemplate.update(sql, departmentName);
    }

    public int deleteOldEmployees(int years) {
        String sql = """
            DELETE FROM employees
            WHERE hire_date < DATE_SUB(NOW(), INTERVAL ? YEAR)
            AND last_login < DATE_SUB(NOW(), INTERVAL 2 YEAR)
            """;
        return jdbcTemplate.update(sql, years);
    }

    public int deleteInactiveUsers(int daysInactive) {
        String sql = """
            DELETE FROM users
            WHERE last_login < DATE_SUB(NOW(), INTERVAL ? DAY)
            AND status = 'inactive'
            """;
        return jdbcTemplate.update(sql, daysInactive);
    }

    public void cascadeDeleteDepartment(Long departmentId) {
        // Сначала удаляем сотрудников
        String deleteEmployeesSql = "DELETE FROM employees WHERE department_id = ?";
        jdbcTemplate.update(deleteEmployeesSql, departmentId);

        // Затем удаляем отдел
        String deleteDepartmentSql = "DELETE FROM departments WHERE id = ?";
        jdbcTemplate.update(deleteDepartmentSql, departmentId);
    }

    public int bulkDeleteByIds(List<Long> employeeIds) {
        String sql = """
            DELETE FROM employees
            WHERE id IN (
                SELECT * FROM (
                    SELECT id FROM employees WHERE id IN (:ids) LIMIT 1000
                ) temp
            )
            """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", employeeIds);

        return new NamedParameterJdbcTemplate(jdbcTemplate).update(sql, parameters);
    }
}
```

## Ограничения и индексы

### Ограничения целостности

#### PRIMARY KEY
```sql
-- Первичный ключ на одном поле
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE
);

-- Составной первичный ключ
CREATE TABLE order_items (
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);
```

#### FOREIGN KEY
```sql
-- Внешний ключ
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Внешний ключ с несколькими действиями
CREATE TABLE employee_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    department_id INT,
    start_date DATE,
    end_date DATE,

    FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);
```

#### UNIQUE и CHECK ограничения
```sql
-- UNIQUE ограничения
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) UNIQUE,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(8,2) CHECK (price > 0)
);

-- Множественное UNIQUE ограничение
CREATE TABLE user_permissions (
    user_id INT,
    permission VARCHAR(50),
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, permission),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### Работа с ограничениями в Java
```java
@Service
public class ConstraintsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void demonstrateConstraints() {
        // Попытка вставить дублированный email
        try {
            String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
            jdbcTemplate.update(sql, "user1", "duplicate@example.com");
            jdbcTemplate.update(sql, "user2", "duplicate@example.com"); // Это вызовет ошибку
        } catch (DuplicateKeyException e) {
            System.out.println("UNIQUE constraint violation: " + e.getMessage());
        }

        // Попытка вставить отрицательную цену
        try {
            String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
            jdbcTemplate.update(sql, "Test Product", -10.00); // Это вызовет ошибку CHECK
        } catch (DataIntegrityViolationException e) {
            System.out.println("CHECK constraint violation: " + e.getMessage());
        }
    }

    public void handleForeignKeyConstraints() {
        // Попытка вставить заказ для несуществующего пользователя
        try {
            String sql = "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";
            jdbcTemplate.update(sql, 99999, 100.00); // Несуществующий user_id
        } catch (DataIntegrityViolationException e) {
            System.out.println("Foreign key constraint violation: " + e.getMessage());
        }
    }

    public List<String> getConstraintViolations() {
        String sql = """
            SELECT
                constraint_name,
                table_name,
                constraint_type
            FROM information_schema.table_constraints
            WHERE constraint_schema = DATABASE()
            ORDER BY table_name, constraint_name
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            rs.getString("table_name") + "." +
            rs.getString("constraint_name") + " (" +
            rs.getString("constraint_type") + ")"
        );
    }
}
```

### Индексы в MySQL

#### Типы индексов
```sql
-- B-Tree индекс (по умолчанию)
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);

-- FULLTEXT индекс
ALTER TABLE articles ADD FULLTEXT INDEX idx_content (title, content, tags);

-- SPATIAL индекс (для геоданных)
ALTER TABLE locations ADD SPATIAL INDEX idx_coords (coordinates);

-- UNIQUE индекс
CREATE UNIQUE INDEX idx_products_sku ON products(sku);

-- Индекс с префиксом (для длинных строк)
CREATE INDEX idx_descriptions ON products(description(50));
```

#### Управление индексами
```sql
-- Просмотр индексов таблицы
SHOW INDEXES FROM users;

-- Удаление индекса
DROP INDEX idx_users_email ON users;
ALTER TABLE users DROP INDEX idx_users_email;

-- Перестройка индекса
ALTER TABLE users DROP INDEX idx_users_email;
ALTER TABLE users ADD INDEX idx_users_email (email);

-- Анализ использования индексов
SELECT
    object_schema,
    object_name,
    index_name,
    count_read,
    count_write,
    date_created
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = DATABASE()
ORDER BY count_read DESC;
```

#### Оптимизация индексов в Java
```java
@Service
public class IndexOptimizationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createOptimizedIndexes() {
        // Создание индексов для типичных запросов
        String[] indexQueries = {
            "CREATE INDEX idx_users_email ON users(email)",
            "CREATE INDEX idx_orders_user_status ON orders(user_id, status)",
            "CREATE INDEX idx_products_category_price ON products(category_id, price)",
            "CREATE INDEX idx_audit_log_timestamp ON audit_log(created_at)"
        };

        for (String query : indexQueries) {
            try {
                jdbcTemplate.execute(query);
                System.out.println("Created index: " + query);
            } catch (Exception e) {
                System.out.println("Index creation failed: " + e.getMessage());
            }
        }
    }

    public List<Map<String, Object>> analyzeIndexUsage() {
        String sql = """
            SELECT
                TABLE_NAME,
                INDEX_NAME,
                CARDINALITY,
                PAGES,
                FILTER_CONDITION
            FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
            AND INDEX_NAME IS NOT NULL
            ORDER BY TABLE_NAME, SEQ_IN_INDEX
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public void rebuildIndexes() {
        // Получение списка таблиц
        List<String> tables = jdbcTemplate.queryForList(
            "SHOW TABLES", String.class);

        for (String table : tables) {
            try {
                // Перестройка таблицы (включая индексы)
                jdbcTemplate.execute("OPTIMIZE TABLE " + table);
                System.out.println("Optimized table: " + table);
            } catch (Exception e) {
                System.out.println("Failed to optimize " + table + ": " + e.getMessage());
            }
        }
    }

    public List<String> getUnusedIndexes() {
        String sql = """
            SELECT CONCAT('ALTER TABLE ', table_name, ' DROP INDEX ', index_name, ';') as drop_statement
            FROM (
                SELECT
                    object_name as table_name,
                    index_name,
                    count_read,
                    count_write
                FROM performance_schema.table_io_waits_summary_by_index_usage
                WHERE object_schema = DATABASE()
                AND count_read = 0 AND count_write = 0
                AND index_name != 'PRIMARY'
            ) unused_indexes
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }
}
```

## Транзакции

### ACID свойства

#### Atomicity (Атомарность)
Операция выполняется полностью или не выполняется вообще.

#### Consistency (Согласованность)
Транзакция переводит базу данных из одного согласованного состояния в другое.

#### Isolation (Изоляция)
Результаты транзакции не видны другим транзакциям до ее завершения.

#### Durability (Долговечность)
После успешного завершения транзакции изменения сохраняются даже при сбое системы.

### Управление транзакциями в MySQL

#### Базовые транзакции
```sql
-- Начало транзакции
START TRANSACTION;

-- Выполнение операций
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

-- Фиксация изменений
COMMIT;

-- Или откат
ROLLBACK;
```

#### SAVEPOINT
```sql
START TRANSACTION;

-- Операция 1
UPDATE accounts SET balance = balance - 50 WHERE id = 1;
SAVEPOINT sp1;

-- Операция 2
UPDATE accounts SET balance = balance + 50 WHERE id = 2;
SAVEPOINT sp2;

-- Откат к определенной точке
ROLLBACK TO SAVEPOINT sp1;

COMMIT;
```

#### Уровни изоляции
```sql
-- Установка уровня изоляции для сессии
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ; -- MySQL по умолчанию
SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Для всей базы данных
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

### Транзакции в Java

#### Программное управление транзакциями
```java
@Service
public class TransactionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Ручное управление транзакцией
    public boolean transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try {
            jdbcTemplate.execute("START TRANSACTION");

            // Проверка баланса
            BigDecimal fromBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = ? FOR UPDATE",
                BigDecimal.class, fromAccountId);

            if (fromBalance.compareTo(amount) < 0) {
                jdbcTemplate.execute("ROLLBACK");
                return false;
            }

            // Списание
            jdbcTemplate.update(
                "UPDATE accounts SET balance = balance - ? WHERE id = ?",
                amount, fromAccountId);

            // Зачисление
            jdbcTemplate.update(
                "UPDATE accounts SET balance = balance + ? WHERE id = ?",
                amount, toAccountId);

            jdbcTemplate.execute("COMMIT");
            return true;

        } catch (Exception e) {
            jdbcTemplate.execute("ROLLBACK");
            throw new RuntimeException("Transfer failed", e);
        }
    }

    // Транзакция с savepoint
    public void complexTransaction() {
        try {
            jdbcTemplate.execute("START TRANSACTION");

            // Операция 1
            jdbcTemplate.update("INSERT INTO orders (user_id, amount) VALUES (?, ?)", 1, 100.00);
            jdbcTemplate.execute("SAVEPOINT sp1");

            // Операция 2
            jdbcTemplate.update("UPDATE inventory SET quantity = quantity - 1 WHERE product_id = ?", 123);
            jdbcTemplate.execute("SAVEPOINT sp2");

            // Операция 3 (может быть отменена)
            try {
                processPayment();
                jdbcTemplate.execute("SAVEPOINT sp3");
            } catch (Exception e) {
                // Откат только платежа
                jdbcTemplate.execute("ROLLBACK TO SAVEPOINT sp2");
            }

            jdbcTemplate.execute("COMMIT");

        } catch (Exception e) {
            jdbcTemplate.execute("ROLLBACK");
            throw new RuntimeException("Transaction failed", e);
        }
    }

    private void processPayment() {
        // Имитация обработки платежа
        throw new RuntimeException("Payment processing failed");
    }
}
```

#### Spring Transaction Management
```java
@Service
public class SpringTransactionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Декларативное управление транзакциями
    @Transactional
    public boolean transferMoneySpring(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Проверка баланса
        BigDecimal fromBalance = jdbcTemplate.queryForObject(
            "SELECT balance FROM accounts WHERE id = ? FOR UPDATE",
            BigDecimal.class, fromAccountId);

        if (fromBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        // Списание и зачисление в одной транзакции
        jdbcTemplate.update(
            "UPDATE accounts SET balance = balance - ? WHERE id = ?",
            amount, fromAccountId);

        jdbcTemplate.update(
            "UPDATE accounts SET balance = balance + ? WHERE id = ?",
            amount, toAccountId);

        return true;
    }

    // Транзакция с кастомными настройками
    @Transactional(
        propagation = Propagation.REQUIRES_NEW,
        isolation = Isolation.READ_COMMITTED,
        timeout = 30,
        rollbackFor = { SQLException.class, InsufficientFundsException.class }
    )
    public void processComplexTransaction() {
        // Сложная бизнес-логика в транзакции
        createOrder();
        updateInventory();
        processPayment();
        sendNotification();
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsReadOnly() {
        // Только чтение - оптимизация
        return jdbcTemplate.query(
            "SELECT id, balance FROM accounts",
            (rs, rowNum) -> new Account(rs.getLong("id"), rs.getBigDecimal("balance"))
        );
    }

    @Transactional(timeout = 10)
    public void processWithTimeout() {
        // Транзакция с таймаутом 10 секунд
        performTimeConsumingOperation();
    }

    private void createOrder() { /* ... */ }
    private void updateInventory() { /* ... */ }
    private void processPayment() { /* ... */ }
    private void sendNotification() { /* ... */ }
    private void performTimeConsumingOperation() { /* ... */ }
}
```

### Обработка deadlock'ов

```java
@Service
public class DeadlockHandlingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean safeTransferWithRetry(Long fromId, Long toId, BigDecimal amount, int maxRetries) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return transferMoney(fromId, toId, amount);
            } catch (DataAccessException e) {
                if (isDeadlockException(e) && attempt < maxRetries - 1) {
                    attempt++;
                    // Экспоненциальная задержка
                    Thread.sleep(100 * (1 << attempt));
                    continue;
                }
                throw e;
            }
        }
        return false;
    }

    private boolean transferMoney(Long fromId, Long toId, BigDecimal amount) {
        // Логика перевода денег
        return jdbcTemplate.execute("CALL transfer_money(?, ?, ?)",
            (PreparedStatementCallback<Boolean>) ps -> {
                ps.setLong(1, fromId);
                ps.setLong(2, toId);
                ps.setBigDecimal(3, amount);
                return ps.execute();
            });
    }

    private boolean isDeadlockException(DataAccessException e) {
        return e.getCause() instanceof SQLException &&
               ((SQLException) e.getCause()).getErrorCode() == 1213; // ER_LOCK_DEADLOCK
    }

    // Заказ ресурсов в фиксированном порядке для предотвращения deadlock
    public void transferOrdered(Long account1, Long account2, BigDecimal amount) {
        // Всегда блокируем аккаунты в порядке возрастания ID
        Long firstId = Math.min(account1, account2);
        Long secondId = Math.max(account1, account2);

        jdbcTemplate.execute("START TRANSACTION");

        try {
            // Проверяем баланс первого аккаунта
            BigDecimal balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = ? FOR UPDATE",
                BigDecimal.class, firstId);

            if (balance.compareTo(amount) < 0) {
                throw new InsufficientFundsException();
            }

            // Выполняем перевод
            jdbcTemplate.update(
                "UPDATE accounts SET balance = balance - ? WHERE id = ?",
                amount, firstId);

            jdbcTemplate.update(
                "UPDATE accounts SET balance = balance + ? WHERE id = ?",
                amount, secondId);

            jdbcTemplate.execute("COMMIT");

        } catch (Exception e) {
            jdbcTemplate.execute("ROLLBACK");
            throw e;
        }
    }
}
```

## Хранимые процедуры

### Создание хранимых процедур

#### Базовая процедура
```sql
DELIMITER //

CREATE PROCEDURE get_employee_count()
BEGIN
    SELECT COUNT(*) as total_employees FROM employees;
END //

DELIMITER ;
```

#### Процедура с параметрами
```sql
DELIMITER //

CREATE PROCEDURE get_employees_by_department(
    IN dept_name VARCHAR(100),
    OUT employee_count INT
)
BEGIN
    SELECT COUNT(*) INTO employee_count
    FROM employees e
    JOIN departments d ON e.department_id = d.id
    WHERE d.name = dept_name;
END //

DELIMITER ;
```

#### Процедура с результатами
```sql
DELIMITER //

CREATE PROCEDURE get_top_salaries(IN limit_count INT)
BEGIN
    SELECT
        id,
        CONCAT(first_name, ' ', last_name) as full_name,
        salary,
        department_id
    FROM employees
    ORDER BY salary DESC
    LIMIT limit_count;
END //

DELIMITER ;
```

### Вызов хранимых процедур

```sql
-- Вызов без параметров
CALL get_employee_count();

-- Вызов с входными параметрами
CALL get_employees_by_department('Engineering', @count);
SELECT @count as engineering_employees;

-- Вызов с результатами
CALL get_top_salaries(5);
```

### Управление процедурами

```sql
-- Просмотр всех процедур
SHOW PROCEDURE STATUS WHERE Db = DATABASE();

-- Просмотр определения процедуры
SHOW CREATE PROCEDURE get_employee_count;

-- Изменение процедуры
DELIMITER //

CREATE OR REPLACE PROCEDURE get_employee_count()
BEGIN
    SELECT
        COUNT(*) as total_employees,
        COUNT(CASE WHEN hire_date >= DATE_SUB(NOW(), INTERVAL 1 YEAR)
                   THEN 1 END) as new_hires
    FROM employees;
END //

DELIMITER ;

-- Удаление процедуры
DROP PROCEDURE IF EXISTS get_employee_count;
```

### Хранимые процедуры в Java

```java
@Service
public class StoredProcedureService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getEmployeeCount() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("get_employee_count");

        Map<String, Object> result = jdbcCall.execute();
        return ((Number) result.get("total_employees")).intValue();
    }

    public int getEmployeesByDepartment(String departmentName) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("get_employees_by_department")
            .declareParameters(
                new SqlParameter("dept_name", Types.VARCHAR),
                new SqlOutParameter("employee_count", Types.INTEGER)
            );

        Map<String, Object> result = jdbcCall.execute(departmentName);
        return ((Number) result.get("employee_count")).intValue();
    }

    public List<Map<String, Object>> getTopSalaries(int limit) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("get_top_salaries")
            .returningResultSet("employees",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("full_name", rs.getString("full_name"));
                    row.put("salary", rs.getBigDecimal("salary"));
                    row.put("department_id", rs.getInt("department_id"));
                    return row;
                });

        Map<String, Object> result = jdbcCall.execute(limit);
        return (List<Map<String, Object>>) result.get("employees");
    }

    public void createStoredProcedure() {
        String procedureSql = """
            CREATE PROCEDURE calculate_department_stats(IN dept_id INT)
            BEGIN
                SELECT
                    d.name as department_name,
                    COUNT(e.id) as employee_count,
                    AVG(e.salary) as avg_salary,
                    MIN(e.salary) as min_salary,
                    MAX(e.salary) as max_salary
                FROM departments d
                LEFT JOIN employees e ON d.id = e.department_id
                WHERE d.id = dept_id
                GROUP BY d.id, d.name;
            END
            """;

        jdbcTemplate.execute(procedureSql);
    }

    public Map<String, Object> calculateDepartmentStats(int departmentId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("calculate_department_stats")
            .returningResultSet("stats",
                (rs, rowNum) -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("department_name", rs.getString("department_name"));
                    stat.put("employee_count", rs.getInt("employee_count"));
                    stat.put("avg_salary", rs.getBigDecimal("avg_salary"));
                    stat.put("min_salary", rs.getBigDecimal("min_salary"));
                    stat.put("max_salary", rs.getBigDecimal("max_salary"));
                    return stat;
                });

        Map<String, Object> result = jdbcCall.execute(departmentId);
        List<Map<String, Object>> stats = (List<Map<String, Object>>) result.get("stats");
        return stats.isEmpty() ? new HashMap<>() : stats.get(0);
    }
}
```

## Триггеры

### Создание триггеров

#### BEFORE INSERT триггер
```sql
DELIMITER //

CREATE TRIGGER before_employee_insert
    BEFORE INSERT ON employees
    FOR EACH ROW
BEGIN
    -- Установка значений по умолчанию
    IF NEW.hire_date IS NULL THEN
        SET NEW.hire_date = CURDATE();
    END IF;

    -- Валидация данных
    IF NEW.salary < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Salary cannot be negative';
    END IF;

    -- Автоматическая генерация email если не указан
    IF NEW.email IS NULL THEN
        SET NEW.email = CONCAT(LOWER(NEW.first_name), '.', LOWER(NEW.last_name), '@company.com');
    END IF;
END //

DELIMITER ;
```

#### AFTER UPDATE триггер
```sql
DELIMITER //

CREATE TRIGGER after_employee_update
    AFTER UPDATE ON employees
    FOR EACH ROW
BEGIN
    -- Логирование изменений
    INSERT INTO employee_audit (
        employee_id,
        old_salary,
        new_salary,
        changed_by,
        changed_at
    ) VALUES (
        NEW.id,
        OLD.salary,
        NEW.salary,
        USER(),
        NOW()
    );
END //

DELIMITER ;
```

#### BEFORE DELETE триггер
```sql
DELIMITER //

CREATE TRIGGER before_employee_delete
    BEFORE DELETE ON employees
    FOR EACH ROW
BEGIN
    -- Архивация данных перед удалением
    INSERT INTO employees_archive (
        id, first_name, last_name, email, hire_date, salary,
        deleted_at, deleted_by
    ) VALUES (
        OLD.id, OLD.first_name, OLD.last_name, OLD.email,
        OLD.hire_date, OLD.salary, NOW(), USER()
    );
END //

DELIMITER ;
```

### Управление триггерами

```sql
-- Просмотр всех триггеров
SHOW TRIGGERS;

-- Просмотр триггеров для таблицы
SHOW TRIGGERS WHERE `table` = 'employees';

-- Удаление триггера
DROP TRIGGER IF EXISTS before_employee_insert;

-- Отключение триггеров
ALTER TABLE employees DISABLE TRIGGERS;

-- Включение триггеров
ALTER TABLE employees ENABLE TRIGGERS;
```

### Триггеры в Java

```java
@Service
public class TriggerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createAuditTrigger() {
        String triggerSql = """
            CREATE TRIGGER audit_employee_changes
                AFTER UPDATE ON employees
                FOR EACH ROW
            BEGIN
                INSERT INTO employee_audit (
                    employee_id,
                    field_name,
                    old_value,
                    new_value,
                    changed_at,
                    changed_by
                ) VALUES
                (NEW.id, 'salary', OLD.salary, NEW.salary, NOW(), USER()),
                (NEW.id, 'email', OLD.email, NEW.email, NOW(), USER()),
                (NEW.id, 'department_id', OLD.department_id, NEW.department_id, NOW(), USER());
            END
            """;

        jdbcTemplate.execute(triggerSql);
    }

    public void createNotificationTrigger() {
        String triggerSql = """
            CREATE TRIGGER notify_salary_change
                AFTER UPDATE ON employees
                FOR EACH ROW
            BEGIN
                IF OLD.salary != NEW.salary THEN
                    INSERT INTO notifications (
                        type,
                        message,
                        created_at
                    ) VALUES (
                        'salary_change',
                        CONCAT('Salary changed for employee ', NEW.id, ' from ', OLD.salary, ' to ', NEW.salary),
                        NOW()
                    );
                END IF;
            END
            """;

        jdbcTemplate.execute(triggerSql);
    }

    public List<Map<String, Object>> getTriggerAudit() {
        String sql = """
            SELECT
                employee_id,
                field_name,
                old_value,
                new_value,
                changed_at,
                changed_by
            FROM employee_audit
            WHERE changed_at >= DATE_SUB(NOW(), INTERVAL 1 DAY)
            ORDER BY changed_at DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public void testTriggers() {
        // Создание тестового сотрудника
        jdbcTemplate.update(
            "INSERT INTO employees (first_name, last_name, salary) VALUES (?, ?, ?)",
            "Test", "User", 50000);

        // Получение ID созданного сотрудника
        Long employeeId = jdbcTemplate.queryForObject(
            "SELECT LAST_INSERT_ID()", Long.class);

        // Обновление данных (должно вызвать триггеры)
        jdbcTemplate.update(
            "UPDATE employees SET salary = ?, email = ? WHERE id = ?",
            55000, "test.user@company.com", employeeId);

        // Проверка аудита
        List<Map<String, Object>> audit = getTriggerAudit();
        System.out.println("Audit records created: " + audit.size());
    }

    public List<String> getAllTriggers() {
        String sql = """
            SELECT CONCAT(
                'Trigger: ', trigger_name,
                ' | Table: ', event_object_table,
                ' | Event: ', event_manipulation,
                ' | Timing: ', action_timing
            ) as trigger_info
            FROM information_schema.triggers
            WHERE trigger_schema = DATABASE()
            ORDER BY event_object_table, trigger_name
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }
}
```

## Представления

### Создание представлений

#### Базовое представление
```sql
-- Простое представление
CREATE VIEW employee_summary AS
SELECT
    id,
    CONCAT(first_name, ' ', last_name) as full_name,
    email,
    hire_date,
    salary
FROM employees
WHERE active = true;
```

#### Представление с JOIN
```sql
-- Представление с объединением таблиц
CREATE VIEW employee_details AS
SELECT
    e.id,
    e.first_name,
    e.last_name,
    e.email,
    e.salary,
    d.name as department_name,
    m.first_name as manager_first_name,
    m.last_name as manager_last_name
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id
LEFT JOIN employees m ON e.manager_id = m.id;
```

#### Материализованное представление (MySQL 8.0+)
```sql
-- Создание таблицы для материализованного представления
CREATE TABLE monthly_sales_summary (
    month DATE PRIMARY KEY,
    total_sales DECIMAL(12,2),
    order_count INT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Процедура для обновления материализованного представления
DELIMITER //

CREATE PROCEDURE refresh_monthly_sales()
BEGIN
    REPLACE INTO monthly_sales_summary
    SELECT
        DATE_FORMAT(order_date, '%Y-%m-01') as month,
        SUM(total_amount) as total_sales,
        COUNT(*) as order_count,
        NOW() as last_updated
    FROM orders
    WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
    GROUP BY DATE_FORMAT(order_date, '%Y-%m-01');
END //

DELIMITER ;

-- Запуск обновления
CALL refresh_monthly_sales();
```

### Управление представлениями

```sql
-- Просмотр определения представления
SHOW CREATE VIEW employee_summary;

-- Изменение представления
ALTER VIEW employee_summary AS
SELECT
    id,
    CONCAT(first_name, ' ', last_name) as full_name,
    email,
    hire_date,
    salary,
    department_id
FROM employees
WHERE active = true;

-- Создание или замена представления
CREATE OR REPLACE VIEW employee_summary AS
SELECT * FROM employees WHERE active = true;

-- Удаление представления
DROP VIEW IF EXISTS employee_summary;

-- Проверка на существование
SELECT TABLE_NAME, TABLE_TYPE
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'employee_summary';
```

### Представления в Java

```java
@Service
public class ViewService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createEmployeeViews() {
        // Создание базового представления
        String createBasicView = """
            CREATE OR REPLACE VIEW employee_summary AS
            SELECT
                id,
                CONCAT(first_name, ' ', last_name) as full_name,
                email,
                hire_date,
                salary,
                department_id
            FROM employees
            WHERE active = true
            """;

        // Создание представления с JOIN
        String createDetailedView = """
            CREATE OR REPLACE VIEW employee_department AS
            SELECT
                e.id,
                e.first_name,
                e.last_name,
                e.email,
                e.salary,
                d.name as department_name,
                d.budget as department_budget
            FROM employees e
            LEFT JOIN departments d ON e.department_id = d.id
            WHERE e.active = true
            """;

        jdbcTemplate.execute(createBasicView);
        jdbcTemplate.execute(createDetailedView);
    }

    public List<Map<String, Object>> queryEmployeeSummary() {
        String sql = "SELECT * FROM employee_summary ORDER BY salary DESC LIMIT 10";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> queryEmployeeDepartments() {
        String sql = """
            SELECT
                department_name,
                COUNT(*) as employee_count,
                AVG(salary) as avg_salary,
                SUM(salary) as total_salary
            FROM employee_department
            GROUP BY department_name
            ORDER BY total_salary DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public void createMaterializedView() {
        // Создание таблицы для материализованного представления
        String createTable = """
            CREATE TABLE IF NOT EXISTS employee_stats (
                department_id INT PRIMARY KEY,
                employee_count INT,
                total_salary DECIMAL(12,2),
                avg_salary DECIMAL(10,2),
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

        // Процедура для обновления
        String createProcedure = """
            CREATE PROCEDURE refresh_employee_stats()
            BEGIN
                REPLACE INTO employee_stats
                SELECT
                    department_id,
                    COUNT(*) as employee_count,
                    SUM(salary) as total_salary,
                    AVG(salary) as avg_salary,
                    NOW() as last_updated
                FROM employees
                WHERE active = true
                GROUP BY department_id;
            END
            """;

        jdbcTemplate.execute(createTable);
        jdbcTemplate.execute(createProcedure);
    }

    public void refreshEmployeeStats() {
        // Вызов процедуры обновления
        jdbcTemplate.update("CALL refresh_employee_stats()");
    }

    public List<Map<String, Object>> getEmployeeStats() {
        String sql = "SELECT * FROM employee_stats ORDER BY total_salary DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public List<String> getAllViews() {
        String sql = """
            SELECT TABLE_NAME as view_name
            FROM information_schema.TABLES
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_TYPE = 'VIEW'
            ORDER BY TABLE_NAME
            """;

        return jdbcTemplate.queryForList(sql, String.class);
    }

    public void dropView(String viewName) {
        String sql = "DROP VIEW IF EXISTS " + viewName;
        jdbcTemplate.execute(sql);
    }
}
```

## Безопасность

### Пользователи и привилегии

#### Создание пользователей
```sql
-- Создание пользователя
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'secure_password';

-- Создание пользователя с ограничением хоста
CREATE USER 'remote_user'@'%' IDENTIFIED BY 'password123';

-- Создание пользователя с истекающим паролем
CREATE USER 'temp_user'@'localhost'
IDENTIFIED BY 'temp_pass'
PASSWORD EXPIRE INTERVAL 90 DAY;
```

#### Привилегии
```sql
-- Предоставление привилегий на базу данных
GRANT SELECT, INSERT, UPDATE ON myapp.* TO 'app_user'@'localhost';

-- Предоставление всех привилегий
GRANT ALL PRIVILEGES ON myapp.* TO 'admin'@'localhost';

-- Предоставление привилегий на конкретные таблицы
GRANT SELECT (id, name, email) ON users TO 'readonly_user'@'localhost';

-- Привилегии на процедуры и функции
GRANT EXECUTE ON PROCEDURE calculate_salary TO 'hr_user'@'localhost';

-- Привилегии на представления
GRANT SELECT ON employee_summary TO 'manager'@'localhost';
```

#### Управление привилегиями
```sql
-- Просмотр привилегий пользователя
SHOW GRANTS FOR 'app_user'@'localhost';

-- Просмотр всех пользователей
SELECT user, host, authentication_string, password_expired
FROM mysql.user
ORDER BY user;

-- Изменение пароля
ALTER USER 'app_user'@'localhost' IDENTIFIED BY 'new_password123';

-- Отзыв привилегий
REVOKE INSERT, UPDATE ON myapp.employees FROM 'app_user'@'localhost';

-- Удаление пользователя
DROP USER IF EXISTS 'temp_user'@'localhost';
```

### Безопасность в Java

```java
@Service
public class SecurityService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createApplicationUser() {
        // Создание пользователя для приложения
        jdbcTemplate.execute("""
            CREATE USER IF NOT EXISTS 'spring_app'@'localhost'
            IDENTIFIED BY 'app_password123'
            """);

        // Предоставление минимально необходимых привилегий
        jdbcTemplate.execute("""
            GRANT SELECT, INSERT, UPDATE, DELETE
            ON myapp.employees TO 'spring_app'@'localhost'
            """);

        jdbcTemplate.execute("""
            GRANT SELECT ON myapp.departments TO 'spring_app'@'localhost'
            """);

        // Перезагрузка привилегий
        jdbcTemplate.execute("FLUSH PRIVILEGES");
    }

    public void createReadOnlyUser() {
        // Создание пользователя только для чтения
        jdbcTemplate.execute("""
            CREATE USER IF NOT EXISTS 'readonly_user'@'%'
            IDENTIFIED BY 'readonly_pass123'
            """);

        // Привилегии только на чтение
        jdbcTemplate.execute("""
            GRANT SELECT ON myapp.* TO 'readonly_user'@'%'
            """);

        jdbcTemplate.execute("FLUSH PRIVILEGES");
    }

    public List<Map<String, Object>> getUserPrivileges(String username) {
        String sql = """
            SELECT
                grantee,
                privilege_type,
                table_schema,
                table_name,
                is_grantable
            FROM information_schema.user_privileges
            WHERE grantee = ?
            ORDER BY table_schema, table_name
            """;

        return jdbcTemplate.queryForList(sql, "'"+username+"'@'%'");
    }

    public void enablePasswordPolicy() {
        // Включение строгой политики паролей
        jdbcTemplate.execute("""
            SET GLOBAL validate_password.policy = 'STRONG'
            """);

        jdbcTemplate.execute("""
            SET GLOBAL validate_password.length = 12
            """);

        jdbcTemplate.execute("""
            SET GLOBAL validate_password.mixed_case_count = 1
            """);

        jdbcTemplate.execute("""
            SET GLOBAL validate_password.number_count = 1
            """);

        jdbcTemplate.execute("""
            SET GLOBAL validate_password.special_char_count = 1
            """);
    }

    public void setupSSLConnections() {
        // Требование SSL для соединений
        jdbcTemplate.execute("""
            ALTER USER 'app_user'@'localhost' REQUIRE SSL
            """);

        // Создание пользователя только с SSL
        jdbcTemplate.execute("""
            CREATE USER 'ssl_user'@'localhost'
            IDENTIFIED BY 'ssl_password123'
            REQUIRE X509
            """);

        jdbcTemplate.execute("FLUSH PRIVILEGES");
    }
}
```

### Шифрование данных

```sql
-- Создание таблицы с шифрованием
CREATE TABLE sensitive_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    credit_card VARBINARY(255), -- Зашифрованные данные
    ssn VARBINARY(255),        -- Номер социального страхования
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Вставка зашифрованных данных
INSERT INTO sensitive_data (user_id, credit_card, ssn)
VALUES (
    1,
    AES_ENCRYPT('4111111111111111', 'encryption_key'),
    AES_ENCRYPT('123-45-6789', 'encryption_key')
);

-- Чтение расшифрованных данных
SELECT
    id,
    user_id,
    AES_DECRYPT(credit_card, 'encryption_key') as credit_card,
    AES_DECRYPT(ssn, 'encryption_key') as ssn
FROM sensitive_data;
```

### Аудит и логирование

```sql
-- Включение общего лога запросов
SET GLOBAL general_log = 'ON';
SET GLOBAL general_log_file = '/var/log/mysql/mysql.log';

-- Создание таблицы для аудита
CREATE TABLE audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(50),
    table_name VARCHAR(100),
    record_id INT,
    old_values JSON,
    new_values JSON,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Триггер для аудита изменений
DELIMITER //

CREATE TRIGGER audit_employee_changes
    AFTER UPDATE ON employees
    FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        user_id, action, table_name, record_id,
        old_values, new_values, changed_by
    ) VALUES (
        NEW.id, 'UPDATE', 'employees', NEW.id,
        JSON_OBJECT('salary', OLD.salary, 'email', OLD.email),
        JSON_OBJECT('salary', NEW.salary, 'email', NEW.email),
        USER()
    );
END //

DELIMITER ;
```

## Мониторинг

### Системные таблицы MySQL

#### Общая информация о сервере
```sql
-- Версия и статус сервера
SELECT
    VERSION() as mysql_version,
    @@version_comment as version_comment,
    @@innodb_version as innodb_version,
    NOW() as current_time,
    @@uptime as uptime_seconds,
    SEC_TO_TIME(@@uptime) as uptime_formatted;

-- Использование памяти
SELECT
    @@innodb_buffer_pool_size / 1024 / 1024 as buffer_pool_mb,
    @@innodb_log_file_size / 1024 / 1024 as log_file_mb,
    @@max_connections as max_connections,
    @@query_cache_size / 1024 / 1024 as query_cache_mb;
```

#### Статистика подключений
```sql
-- Текущие подключения
SELECT
    id,
    user,
    host,
    db,
    command,
    time,
    state,
    info
FROM information_schema.processlist
WHERE command != 'Sleep'
ORDER BY time DESC;

-- Статистика по подключениям
SHOW STATUS LIKE 'Connections';
SHOW STATUS LIKE 'Max_used_connections';
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Threads_running';
```

#### Производительность запросов
```sql
-- Медленные запросы
SELECT
    sql_text,
    exec_count,
    avg_timer_wait / 1000000000 as avg_time_sec,
    min_timer_wait / 1000000000 as min_time_sec,
    max_timer_wait / 1000000000 as max_time_sec
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 1000000000  -- больше 1 секунды
ORDER BY avg_timer_wait DESC
LIMIT 10;

-- Использование индексов
SELECT
    object_name as table_name,
    index_name,
    count_read,
    count_write,
    count_fetch,
    count_insert,
    count_update,
    count_delete
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = DATABASE()
ORDER BY count_read DESC;
```

### Мониторинг в Java

```java
@Service
public class MonitoringService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getServerStatus() {
        String sql = """
            SELECT
                VERSION() as mysql_version,
                @@version_comment as version_comment,
                @@uptime as uptime_seconds,
                @@max_connections as max_connections,
                @@threads_connected as threads_connected,
                @@threads_running as threads_running,
                @@queries as total_queries,
                @@slow_queries as slow_queries
            """;

        return jdbcTemplate.queryForMap(sql);
    }

    public List<Map<String, Object>> getActiveConnections() {
        String sql = """
            SELECT
                id,
                user,
                host,
                db,
                command,
                time as time_seconds,
                state,
                LEFT(info, 100) as query_preview
            FROM information_schema.processlist
            WHERE command != 'Sleep' AND info IS NOT NULL
            ORDER BY time DESC
            LIMIT 20
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTableStatistics() {
        String sql = """
            SELECT
                table_schema,
                table_name,
                table_rows,
                data_length / 1024 / 1024 as data_mb,
                index_length / 1024 / 1024 as index_mb,
                (data_length + index_length) / 1024 / 1024 as total_mb,
                engine
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
            ORDER BY data_length DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getSlowQueries() {
        String sql = """
            SELECT
                digest_text as query,
                count_star as executions,
                avg_timer_wait / 1000000000 as avg_time_sec,
                max_timer_wait / 1000000000 as max_time_sec,
                sum_rows_affected as total_rows_affected,
                sum_rows_examined as total_rows_examined
            FROM performance_schema.events_statements_summary_by_digest
            WHERE avg_timer_wait > 1000000000  -- > 1 second
            ORDER BY avg_timer_wait DESC
            LIMIT 10
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getInnoDBStatus() {
        String sql = """
            SELECT
                name,
                count
            FROM information_schema.innodb_metrics
            WHERE name IN (
                'lock_deadlocks',
                'lock_timeouts',
                'buffer_pool_reads',
                'buffer_pool_read_requests',
                'trx_rseg_history_len'
            )
            """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        Map<String, Object> status = new HashMap<>();
        for (Map<String, Object> row : results) {
            status.put((String) row.get("name"), row.get("count"));
        }

        return status;
    }

    public void enableDetailedMonitoring() {
        // Включение performance schema
        jdbcTemplate.execute("UPDATE performance_schema.setup_instruments SET ENABLED = 'YES' WHERE NAME LIKE 'statement/%'");

        // Включение memory monitoring
        jdbcTemplate.execute("UPDATE performance_schema.setup_instruments SET ENABLED = 'YES' WHERE NAME LIKE 'memory/%'");

        // Сброс статистики
        jdbcTemplate.execute("FLUSH STATUS");
    }

    public List<Map<String, Object>> getQueryDigest() {
        String sql = """
            SELECT
                schema_name,
                digest_text as query_pattern,
                count_star as executions,
                sum_timer_wait / 1000000000 as total_time_sec,
                avg_timer_wait / 1000000000 as avg_time_sec
            FROM performance_schema.events_statements_summary_by_digest
            WHERE schema_name = DATABASE()
            ORDER BY sum_timer_wait DESC
            LIMIT 20
            """;

        return jdbcTemplate.queryForList(sql);
    }
}
```

## Лучшие практики

### Проектирование базы данных

1. **Нормализация данных**
   - Приводите данные к нормальным формам (1NF, 2NF, 3NF)
   - Избегайте избыточности данных
   - Обеспечивайте целостность данных

2. **Выбор типов данных**
   - Используйте наиболее подходящие типы данных
   - Оптимизируйте размер хранения
   - Учитывайте производительность операций

3. **Индексация**
   - Создавайте индексы для часто используемых условий
   - Избегайте избыточного индексирования
   - Мониторьте использование индексов

### Производительность

1. **Оптимизация запросов**
   - Используйте **EXPLAIN** для анализа планов выполнения
   - Избегайте **SELECT** * в больших таблицах
   - Оптимизируйте **JOIN** операции

2. **Конфигурация сервера**
   - Настройте **innodb_buffer_pool_size**
   - Оптимизируйте параметры подключений
   - Настройте логирование

3. **Мониторинг и обслуживание**
   - Регулярно анализируйте **slow queries**
   - Выполняйте **OPTIMIZE TABLE**
   - Мониторьте использование ресурсов

### Безопасность

1. **Принцип наименьших привилегий**
   - Предоставляйте минимально необходимые права
   - Регулярно пересматривайте привилегии
   - Используйте ролевую модель

2. **Защита данных**
   - Шифруйте чувствительные данные
   - Используйте **SSL** для подключений
   - Регулярно обновляйте пароли

3. **Аудит и логирование**
   - Ведите логи важных операций
   - Мониторьте подозрительную активность
   - Регулярно проверяйте логи

### Резервное копирование

1. **Регулярные бэкапы**
   - Ежедневные полные бэкапы
   - Часовые инкрементальные бэкапы
   - Тестирование восстановления

2. **Стратегия бэкапов**
   - Храните бэкапы в нескольких локациях
   - Шифруйте бэкапы
   - Документируйте процедуры восстановления

**MySQL** — мощная и надежная система управления реляционными базами данных, которая является отличным выбором для широкого спектра приложений, от небольших веб-сайтов до крупных корпоративных систем.

### Ключевые особенности MySQL:

1. **Надежность**: **ACID** транзакции, репликация, кластеризация
2. **Производительность**: Оптимизированные запросы, эффективное кэширование
3. **Масштабируемость**: Поддержка больших объемов данных и высокой нагрузки
4. **Гибкость**: Множество движков хранения, расширяемая архитектура
5. **Простота использования**: Легкая установка, интуитивный **SQL**

### Преимущества использования MySQL в Java/Spring приложениях:

- **Отличная интеграция** с **JDBC** и **Spring Data JPA**
- **Широкая поддержка** в экосистеме **Java**
- **Высокая производительность** при правильной настройке
- **Надежные транзакции** для бизнес-критичных операций
- **Мощные инструменты** для мониторинга и оптимизации

### Рекомендации по использованию:

1. **Выбирайте правильные типы данных** для оптимизации хранения
2. **Проектируйте схему** с учетом паттернов запросов
3. **Создавайте необходимые индексы** для производительности
4. **Используйте транзакции** для обеспечения целостности
5. **Мониторьте производительность** и оптимизируйте запросы
6. **Регулярно выполняйте обслуживание** базы данных
7. **Обеспечивайте безопасность** данных и подключений

## Решение проблем

**Сервер не запускается:** проверьте логи (`journalctl -u mysql` или файл ошибок в `datadir`), права на каталог данных и порт. Частые причины — нехватка памяти, повреждённые системные таблицы или конфликт порта. Восстановление из бэкапа при повреждении данных.

**Ошибка подключения (Access denied):** проверьте пользователя, хост и пароль; убедитесь, что пользователь имеет привилегию для данного хоста (`%` или конкретный IP). Сброс пароля `root` через `skip-grant-tables` при необходимости.

**Медленные запросы:** включите `slow_query_log`, настройте `long_query_time`, добавьте индексы под частые `WHERE`/`ORDER BY`/`GROUP BY`. Используйте `EXPLAIN` для анализа планов.

**Полный диск или нехватка места:** очистите бинарные логи (`PURGE BINARY LOGS`), временные таблицы, увеличьте место или перенесите `datadir`/логи на другой раздел.

**MySQL** продолжает развиваться и остается одним из самых популярных выборов для реляционных баз данных в современной разработке. С правильным проектированием и настройкой **MySQL** способен обеспечить высокую производительность и надежность для приложений любого масштаба.


