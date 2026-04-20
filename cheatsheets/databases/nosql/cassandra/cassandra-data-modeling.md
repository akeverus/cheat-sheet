---
title: "Cassandra: Моделирование данных - Проектирование схем в распределенной базе данных"
description: "Комплексное руководство по моделированию данных в Apache Cassandra: денормализация, паттерны запросов, партиционирование и оптимизация схем."
tags:
  - databases
  - nosql
  - cassandra-data-modeling
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Cassandra: Моделирование данных — Проектирование схем в распределенной базе данных

Комплексное руководство по моделированию данных в **Apache Cassandra**: денормализация, паттерны запросов, партиционирование и оптимизация схем.

## Полезные ссылки

### Официальная документация
- [Data Modeling](https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html)
- [CQL Data Modeling](https://cassandra.apache.org/doc/latest/cql/ddl.html)
- [Data Modeling Best Practices](https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html)

### Дизайн и архитектура
- [Cassandra Data Modeling Guide](https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html)
- [Cassandra Query Patterns](https://cassandra.apache.org/doc/latest/cassandra/data_modeling/index.html)
- [DataStax Academy](https://academy.datastax.com/)

### Инструменты
- [DataStax Studio](https://docs.datastax.com/en/studio/)
- [KDMG (Cassandra Data Model Generator)](https://github.com/nickmancol/kdm)
- [Cassandra Designer](https://www.datastax.com/resources/tools/cassandra-data-modeler)

### См. также
- [[cassandra-basics|Основы]] — **Cassandra**
- [[cassandra-queries|Запросы]] — **CQL** запросы и оптимизация
- [[cassandra-clustering|Кластеризация]] — кластеризация и масштабирование
- [[mongodb-basics|MongoDB Basics]] — сравнение с документными БД

## Содержание

- [Принципы моделирования данных в **Cassandra**](#принципы-моделирования-данных-в-cassandra)
  - [Отличия от реляционных баз данных](#отличия-от-реляционных-баз-данных)
    - [Реляционная модель (PostgreSQL/MySQL)](#реляционная-модель-postgresqlmysql)
    - [**Cassandra** модель](#cassandra-модель)
  - [Основные принципы](#основные-принципы)
    - [1. **Query-First Design**](#1-query-first-design)
    - [2. Денормализация данных](#2-денормализация-данных)
    - [3. Оптимизация для записи](#3-оптимизация-для-записи)
    - [4. Масштабируемость](#4-масштабируемость)
- [Анализ паттернов запросов](#анализ-паттернов-запросов)
  - [Методология **Query-Based Modeling**](#методология-query-based-modeling)
    - [Шаг 1: Сбор требований](#шаг-1-сбор-требований)
    - [Шаг 2: Определение **access patterns**](#шаг-2-определение-access-patterns)
    - [Шаг 3: Создание логической модели](#шаг-3-создание-логической-модели)
  - [Приоритизация запросов](#приоритизация-запросов)
    - [Классификация по важности](#классификация-по-важности)
- [Партиционирование и ключи](#партиционирование-и-ключи)
  - [Выбор **Partition Key**](#выбор-partition-key)
    - [Хорошие **Partition Keys**](#хорошие-partition-keys)
    - [Проблемные **Partition Keys**](#проблемные-partition-keys)
  - [**Clustering Columns**](#clustering-columns)
    - [Определение порядка сортировки](#определение-порядка-сортировки)
    - [Оптимизация для запросов](#оптимизация-для-запросов)
  - [**Bucket Pattern**](#bucket-pattern)
    - [Временные бакеты](#временные-бакеты)
    - [Географические бакеты](#географические-бакеты)
- [Денормализация и материализованные представления](#денормализация-и-материализованные-представления)
  - [Материализованные представления](#материализованные-представления)
    - [Создание MV](#создание-mv)
    - [Использование MV](#использование-mv)
  - [Ручная денормализация](#ручная-денормализация)
    - [Дублирование данных](#дублирование-данных)
    - [**Batch** синхронизация](#batch-синхронизация)
- [Типичные паттерны моделирования](#типичные-паттерны-моделирования)
  - [**Time Series Pattern**](#time-series-pattern)
    - [Временные ряды](#временные-ряды)
    - [Логи и события](#логи-и-события)
  - [**Queue Pattern**](#queue-pattern)
    - [Очереди сообщений](#очереди-сообщений)
    - [**Inbox Pattern**](#inbox-pattern)
  - [**Graph Pattern**](#graph-pattern)
    - [Связи между сущностями](#связи-между-сущностями)
  - [**Search Pattern**](#search-pattern)
    - [Поисковые индексы](#поисковые-индексы)
    - [**Full-text search**](#full-text-search)
- [Вторичные индексы](#вторичные-индексы)
  - [Создание вторичных индексов](#создание-вторичных-индексов)
    - [Простые вторичные индексы](#простые-вторичные-индексы)
    - [**SASI** индексы (SSTable Attached Secondary Index)](#sasi-индексы-sstable-attached-secondary-index)
  - [Когда использовать вторичные индексы](#когда-использовать-вторичные-индексы)
    - [Подходящие случаи](#подходящие-случаи)
    - [Когда избегать](#когда-избегать)
- [Пользовательские типы (UDT)](#пользовательские-типы-udt)
  - [Создание и использование **UDT**](#создание-и-использование-udt)
    - [Определение **UDT**](#определение-udt)
    - [Использование **UDT** в таблицах](#использование-udt-в-таблицах)
  - [Работа с **UDT** в **Java**](#работа-с-udt-в-java)
  - [Моделирование временных данных](#моделирование-временных-данных)
    - [Бакетирование по времени](#бакетирование-по-времени)
    - [Агрегация временных данных](#агрегация-временных-данных)
  - [**TTL** и автоматическая очистка](#ttl-и-автоматическая-очистка)
    - [Управление временем жизни данных](#управление-временем-жизни-данных)
    - [Автоматическая архивация](#автоматическая-архивация)
- [Геоданные](#геоданные)
  - [Геохэширование](#геохэширование)
    - [**Geohash** для **Cassandra**](#geohash-для-cassandra)
    - [Работа с геоданными в **Java**](#работа-с-геоданными-в-java)
- [Миграции схемы](#миграции-схемы)
  - [Управление изменениями схемы](#управление-изменениями-схемы)
    - [Версионирование схемы](#версионирование-схемы)
    - [Безопасные изменения схемы](#безопасные-изменения-схемы)
  - [Инструменты миграции](#инструменты-миграции)
    - [**Cassandra Migration**](#cassandra-migration)
    - [**Liquibase** для **Cassandra**](#liquibase-для-cassandra)
- [Производительность модели](#производительность-модели)
  - [Мониторинг производительности](#мониторинг-производительности)
    - [Метрики запросов](#метрики-запросов)
    - [Оптимизация модели](#оптимизация-модели)
- [Инструменты моделирования](#инструменты-моделирования)
  - [**Cassandra Designer**](#cassandra-designer)
    - [Визуальное моделирование](#визуальное-моделирование)
  - [**DataStax Studio**](#datastax-studio)
    - [Анализ и оптимизация запросов](#анализ-и-оптимизация-запросов)
  - [**KillrVideo**](#killrvideo)
    - [Референсная архитектура](#референсная-архитектура)
- [**Best Practices**](#лучшие-практики)
  - [Проектирование схемы](#проектирование-схемы)
    - [2. Выбор **Partition Key**](#2-выбор-partition-key)
    - [3. **Clustering Columns**](#3-clustering-columns)
  - [Оптимизация производительности](#оптимизация-производительности)
    - [1. Размер партиций](#1-размер-партиций)
    - [2. Вторичные индексы](#2-вторичные-индексы)
    - [3. Материализованные представления](#3-материализованные-представления)
  - [Масштабируемость](#масштабируемость)
    - [1. Горизонтальное масштабирование](#1-горизонтальное-масштабирование)
    - [2. Геораспределение](#2-геораспределение)
  - [Безопасность и мониторинг](#безопасность-и-мониторинг)
    - [1. Безопасность данных](#1-безопасность-данных)
    - [2. Мониторинг](#2-мониторинг)
  - [Обслуживание и поддержка](#обслуживание-и-поддержка)
    - [1. Регулярное обслуживание](#1-регулярное-обслуживание)
    - [2. Резервное копирование](#2-резервное-копирование)
    - [Решение проблем](#решение-проблем)
  - [Инструменты и автоматизация](#инструменты-и-автоматизация)
    - [1. Инструменты разработки](#1-инструменты-разработки)
    - [2. Автоматизация](#2-автоматизация)
  - [Обучение и сертификация](#обучение-и-сертификация)
    - [1. Ресурсы обучения](#1-ресурсы-обучения)
    - [2. Сертификация](#2-сертификация)
  - [Ключевые компоненты модели:](#ключевые-компоненты-модели)
  - [Стратегии моделирования:](#стратегии-моделирования)
  - [Инструменты и **best practices**:](#инструменты-и-best-practices)
  - [Вызовы и решения:](#вызовы-и-решения)
  - [Когда выбирать **Cassandra**:](#когда-выбирать-cassandra)

## Принципы моделирования данных в Cassandra

### Отличия от реляционных баз данных

#### Реляционная модель (PostgreSQL/MySQL)

Пример нормализованной схемы в реляционной БД для сравнения с **Cassandra** (SQL).

```sql
-- Нормализованная схема
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    total_amount DECIMAL(10,2),
    created_at TIMESTAMP
);

CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER REFERENCES orders(id),
    product_name VARCHAR(255),
    quantity INTEGER,
    price DECIMAL(8,2)
);

-- Гибкие JOIN запросы
SELECT u.name, o.total_amount, oi.product_name
FROM users u
JOIN orders o ON u.id = o.user_id
JOIN order_items oi ON o.id = oi.order_id
WHERE u.email = 'user@example.com';
```

#### Cassandra модель
```cql
-- Денормализованная схема для конкретных запросов
CREATE TABLE user_orders (
    user_id UUID,
    order_id UUID,
    user_name TEXT,
    user_email TEXT,
    total_amount DECIMAL,
    created_at TIMESTAMP,
    items LIST<FROZEN<item_summary>>,
    PRIMARY KEY ((user_id), created_at, order_id)
) WITH CLUSTERING ORDER BY (created_at DESC, order_id DESC);

-- Запрос без JOIN
SELECT user_name, total_amount, items
FROM user_orders
WHERE user_id = ? AND created_at >= ?;
```

### Основные принципы

#### 1. Query-First Design
```text
❌ Традиционный подход:
   Создать нормализованную схему → Написать запросы

✅ Cassandra подход:
   Определить паттерны запросов → Спроектировать схему
```

#### 2. Денормализация данных
- **Избегать JOIN** — все данные в одной таблице
- **Дублирование данных** — для разных представлений
- **Материализованные представления** — автоматическая денормализация

#### 3. Оптимизация для записи
- **Быстрые вставки** — **append-only** модель
- **Неизменяемые данные** — **updates** как новые версии
- **TTL для временных данных** — автоматическая очистка

#### 4. Масштабируемость
- **Партиционирование** — распределение по узлам
- **Репликация** — отказоустойчивость
- **Горизонтальное масштабирование** — добавление узлов

## Анализ паттернов запросов

### Методология Query-Based Modeling

#### Шаг 1: Сбор требований
```java
// Анализ бизнес-требований
public class QueryRequirements {

    // Основные запросы приложения
    public List<String> getMainQueries() {
        return Arrays.asList(
            "Найти заказы пользователя за период",
            "Показать последние действия пользователя",
            "Получить статистику продаж по категориям",
            "Найти продукты по поисковому запросу",
            "Показать геолокацию событий"
        );
    }

    // Частота запросов
    public Map<String, String> getQueryFrequency() {
        return Map.of(
            "user_orders", "1000/sec",
            "user_activity", "500/sec",
            "sales_stats", "10/sec",
            "product_search", "100/sec",
            "geo_events", "200/sec"
        );
    }

    // Требования к latency
    public Map<String, String> getLatencyRequirements() {
        return Map.of(
            "user_orders", "< 50ms",
            "user_activity", "< 100ms",
            "sales_stats", "< 500ms",
            "product_search", "< 200ms",
            "geo_events", "< 150ms"
        );
    }
}
```

#### Шаг 2: Определение access patterns
```java
@Service
public class AccessPatternAnalyzer {

    // Определение паттернов доступа
    public List<AccessPattern> analyzePatterns() {
        return Arrays.asList(
            new AccessPattern(
                "get_user_orders",
                "user_id, order_date >= ?",
                "user_id DESC, order_date DESC",
                "Чтение заказов пользователя за период"
            ),
            new AccessPattern(
                "get_user_recent_activity",
                "user_id",
                "timestamp DESC",
                "Последние действия пользователя"
            ),
            new AccessPattern(
                "get_sales_by_category",
                "category, date >= ?",
                "date DESC, amount DESC",
                "Продажи по категориям"
            ),
            new AccessPattern(
                "search_products",
                "category, search_terms",
                "relevance DESC, price ASC",
                "Поиск продуктов"
            ),
            new AccessPattern(
                "get_nearby_events",
                "geohash, timestamp >= ?",
                "timestamp DESC",
                "Геолокационные события"
            )
        );
    }

    // Анализ селективности
    public Map<String, Double> calculateSelectivity() {
        return Map.of(
            "user_id", 0.95,      // Высокая селективность
            "category", 0.85,     // Высокая селективность
            "geohash", 0.90,      // Высокая селективность
            "status", 0.05,       // Низкая селективность
            "active", 0.02        // Низкая селективность
        );
    }
}

class AccessPattern {
    private String name;
    private String partitionKey;
    private String clusteringKey;
    private String description;

    // constructor, getters, setters
}
```

#### Шаг 3: Создание логической модели
```java
@Service
public class LogicalModelBuilder {

    public LogicalDataModel buildModel(List<AccessPattern> patterns) {
        LogicalDataModel model = new LogicalDataModel();

        for (AccessPattern pattern : patterns) {
            // Определение сущностей
            List<Entity> entities = identifyEntities(pattern);

            // Определение связей
            List<Relationship> relationships = identifyRelationships(entities);

            // Создание таблиц для паттернов
            List<Table> tables = createTablesForPatterns(patterns);

            model.setEntities(entities);
            model.setRelationships(relationships);
            model.setTables(tables);
        }

        return model;
    }

    private List<Entity> identifyEntities(AccessPattern pattern) {
        // Логика идентификации сущностей из паттернов
        return new ArrayList<>();
    }

    private List<Relationship> identifyRelationships(List<Entity> entities) {
        // Логика определения связей
        return new ArrayList<>();
    }

    private List<Table> createTablesForPatterns(List<AccessPattern> patterns) {
        // Создание таблиц на основе паттернов запросов
        return patterns.stream()
            .map(this::createTableForPattern)
            .collect(Collectors.toList());
    }

    private Table createTableForPattern(AccessPattern pattern) {
        Table table = new Table();
        table.setName(pattern.getName().toLowerCase());

        // Определение первичного ключа
        PrimaryKey pk = new PrimaryKey();
        pk.setPartitionKey(parsePartitionKey(pattern.getPartitionKey()));
        pk.setClusteringKey(parseClusteringKey(pattern.getClusteringKey()));
        table.setPrimaryKey(pk);

        // Определение колонок на основе паттерна
        table.setColumns(inferColumns(pattern));

        return table;
    }

    private List<String> parsePartitionKey(String partitionKey) {
        return Arrays.asList(partitionKey.split(","));
    }

    private List<String> parseClusteringKey(String clusteringKey) {
        return Arrays.asList(clusteringKey.split(","));
    }

    private List<Column> inferColumns(AccessPattern pattern) {
        // Логика вывода колонок из паттерна
        return new ArrayList<>();
    }
}
```

### Приоритизация запросов

#### Классификация по важности
```java
public enum QueryPriority {
    CRITICAL,    // Основные бизнес-функции
    IMPORTANT,   // Важные, но не критичные
    OPTIONAL     // Дополнительные возможности
}

@Service
public class QueryPrioritizer {

    public Map<QueryPriority, List<AccessPattern>> prioritizeQueries(List<AccessPattern> patterns) {
        return patterns.stream()
            .collect(Collectors.groupingBy(this::determinePriority));
    }

    private QueryPriority determinePriority(AccessPattern pattern) {
        String name = pattern.getName().toLowerCase();

        if (name.contains("user") && name.contains("order")) {
            return QueryPriority.CRITICAL;
        } else if (name.contains("stats") || name.contains("analytics")) {
            return QueryPriority.IMPORTANT;
        } else {
            return QueryPriority.OPTIONAL;
        }
    }

    // Расчет нагрузки на таблицу
    public Map<String, Double> calculateTableLoad(List<AccessPattern> patterns) {
        return patterns.stream()
            .collect(Collectors.groupingBy(
                AccessPattern::getName,
                Collectors.summingDouble(p -> getEstimatedLoad(p))
            ));
    }

    private double getEstimatedLoad(AccessPattern pattern) {
        // Оценка нагрузки на основе частоты и сложности
        return 1.0; // placeholder
    }
}
```

## Партиционирование и ключи

### Выбор Partition Key

#### Хорошие Partition Keys
```cql
-- Высокая кардинальность, равномерное распределение
CREATE TABLE user_events (
    user_id UUID,
    event_id TIMEUUID,
    event_type TEXT,
    data TEXT,
    timestamp TIMESTAMP,
    PRIMARY KEY ((user_id), event_id, timestamp)
);

-- Составной partition key для лучшего распределения
CREATE TABLE sensor_readings (
    sensor_group TEXT,
    sensor_id UUID,
    timestamp TIMESTAMP,
    temperature DOUBLE,
    humidity DOUBLE,
    PRIMARY KEY ((sensor_group, sensor_id), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Геохэш для геоданных
CREATE TABLE location_events (
    geohash TEXT,
    event_id TIMEUUID,
    user_id UUID,
    latitude DOUBLE,
    longitude DOUBLE,
    event_type TEXT,
    PRIMARY KEY ((geohash), event_id)
);
```

#### Проблемные Partition Keys
```cql
-- Низкая кардинальность - все данные в одной партиции
CREATE TABLE system_config (
    config_key TEXT PRIMARY KEY,
    config_value TEXT
);

-- Hot partitions - неравномерное распределение
CREATE TABLE daily_orders (
    order_date DATE,
    order_id TIMEUUID,
    user_id UUID,
    total_amount DECIMAL,
    PRIMARY KEY ((order_date), order_id)
);
-- Все заказы за день в одной партиции
```

### Clustering Columns

#### Определение порядка сортировки
```cql
-- Естественная сортировка по времени
CREATE TABLE user_posts (
    user_id UUID,
    post_id TIMEUUID,
    content TEXT,
    created_at TIMESTAMP,
    likes INT,
    PRIMARY KEY ((user_id), post_id)
) WITH CLUSTERING ORDER BY (post_id DESC);

-- Множественные clustering columns
CREATE TABLE user_notifications (
    user_id UUID,
    notification_id TIMEUUID,
    type TEXT,
    title TEXT,
    content TEXT,
    read BOOLEAN,
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id), read, created_at, notification_id)
) WITH CLUSTERING ORDER BY (read ASC, created_at DESC, notification_id DESC);
```

#### Оптимизация для запросов
```cql
-- Запросы по clustering columns
SELECT * FROM user_posts
WHERE user_id = ?
ORDER BY post_id DESC
LIMIT 10;

-- Диапазонные запросы
SELECT * FROM user_notifications
WHERE user_id = ? AND read = false
ORDER BY created_at DESC;

-- Составные условия
SELECT * FROM sensor_readings
WHERE sensor_group = ? AND sensor_id = ?
AND timestamp >= ? AND timestamp <= ?;
```

### Bucket Pattern

#### Временные бакеты
```cql
-- Разделение по дням для равномерного распределения
CREATE TABLE user_activity_daily (
    user_id UUID,
    date DATE,
    activity_id TIMEUUID,
    activity_type TEXT,
    data TEXT,
    timestamp TIMESTAMP,
    PRIMARY KEY ((user_id, date), activity_id)
) WITH CLUSTERING ORDER BY (activity_id DESC);

-- Запрос за конкретный день
SELECT * FROM user_activity_daily
WHERE user_id = ? AND date = ?;

-- Запрос за период
SELECT * FROM user_activity_daily
WHERE user_id = ?
AND date >= ? AND date <= ?;
```

#### Географические бакеты
```cql
-- Бакеты по геохэшу для геоданных
CREATE TABLE geo_events (
    geohash_prefix TEXT,  -- Первые 4 символа geohash
    geohash TEXT,
    event_id TIMEUUID,
    user_id UUID,
    latitude DOUBLE,
    longitude DOUBLE,
    event_type TEXT,
    PRIMARY KEY ((geohash_prefix), geohash, event_id)
);

-- Запрос в области
SELECT * FROM geo_events
WHERE geohash_prefix = ?
AND geohash >= ? AND geohash < ?;
```

## Денормализация и материализованные представления

### Материализованные представления

#### Создание `MV`
```cql
-- Основная таблица заказов
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    user_id UUID,
    total_amount DECIMAL,
    status TEXT,
    created_at TIMESTAMP
);

-- Материализованное представление для пользователя
CREATE MATERIALIZED VIEW user_orders AS
SELECT user_id, order_id, total_amount, status, created_at
FROM orders
WHERE user_id IS NOT NULL AND order_id IS NOT NULL
PRIMARY KEY ((user_id), created_at, order_id)
WITH CLUSTERING ORDER BY (created_at DESC, order_id DESC);

-- Материализованное представление по статусу
CREATE MATERIALIZED VIEW orders_by_status AS
SELECT status, order_id, user_id, total_amount, created_at
FROM orders
WHERE status IS NOT NULL AND order_id IS NOT NULL
PRIMARY KEY ((status), created_at, order_id)
WITH CLUSTERING ORDER BY (created_at DESC, order_id DESC);
```

#### Использование `MV`
```cql
-- Запрос через MV (автоматически)
SELECT * FROM user_orders
WHERE user_id = ?
ORDER BY created_at DESC;

-- Запрос по статусу
SELECT * FROM orders_by_status
WHERE status = 'pending'
ORDER BY created_at DESC;
```

### Ручная денормализация

#### Дублирование данных
```cql
-- Основная таблица пользователей
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email TEXT,
    name TEXT,
    created_at TIMESTAMP
);

-- Денормализованная таблица заказов
CREATE TABLE user_orders_denormalized (
    user_id UUID,
    user_email TEXT,      -- Дублирование
    user_name TEXT,       -- Дублирование
    order_id UUID,
    total_amount DECIMAL,
    status TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id), created_at, order_id)
);

-- Синхронизация данных
INSERT INTO user_orders_denormalized (
    user_id, user_email, user_name, order_id, total_amount, status, created_at
)
SELECT
    u.user_id, u.email, u.name,
    o.order_id, o.total_amount, o.status, o.created_at
FROM users u
JOIN orders o ON u.user_id = o.user_id;
```

#### Batch синхронизация
```java
@Service
public class DataDenormalizationService {

    @Autowired
    private CqlSession session;

    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void syncDenormalizedData() {
        // Синхронизация через batch
        session.execute("""
            BEGIN BATCH
                UPDATE user_orders_denormalized
                SET user_email = ?, user_name = ?
                WHERE user_id = ? AND order_id = ?
                APPLY BATCH
            """, "newemail@example.com", "New Name", userId, orderId);
    }

    public void updateUserData(UUID userId, String newEmail, String newName) {
        // Обновление всех связанных записей
        session.execute("""
            UPDATE user_orders_denormalized
            SET user_email = ?, user_name = ?
            WHERE user_id = ?
            """, newEmail, newName, userId);
    }
}
```

## Типичные паттерны моделирования

### Time Series Pattern

#### Временные ряды
```cql
-- Данные сенсоров
CREATE TABLE sensor_data (
    sensor_id UUID,
    bucket TEXT,          -- Дневной бакет (YYYY-MM-DD)
    timestamp TIMESTAMP,
    temperature DOUBLE,
    humidity DOUBLE,
    pressure DOUBLE,
    PRIMARY KEY ((sensor_id, bucket), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Запрос данных за день
SELECT * FROM sensor_data
WHERE sensor_id = ? AND bucket = ?
ORDER BY timestamp DESC;

-- Агрегация по часам
SELECT
    sensor_id,
    date_format(timestamp, 'yyyy-MM-dd HH') as hour,
    avg(temperature) as avg_temp,
    min(temperature) as min_temp,
    max(temperature) as max_temp
FROM sensor_data
WHERE sensor_id = ? AND bucket >= ? AND bucket <= ?
GROUP BY sensor_id, date_format(timestamp, 'yyyy-MM-dd HH');
```

#### Логи и события
```cql
-- Логи приложений
CREATE TABLE application_logs (
    application_id TEXT,
    date DATE,
    log_id TIMEUUID,
    level TEXT,
    message TEXT,
    user_id UUID,
    session_id UUID,
    timestamp TIMESTAMP,
    PRIMARY KEY ((application_id, date), log_id)
) WITH CLUSTERING ORDER BY (log_id DESC);

-- Поиск ошибок
SELECT * FROM application_logs
WHERE application_id = ? AND date = ?
AND level = 'ERROR'
ORDER BY log_id DESC
LIMIT 100;
```

### Queue Pattern

#### Очереди сообщений
```cql
-- Очередь задач
CREATE TABLE task_queue (
    queue_name TEXT,
    priority INT,
    task_id TIMEUUID,
    status TEXT,
    payload TEXT,
    created_at TIMESTAMP,
    locked_until TIMESTAMP,
    PRIMARY KEY ((queue_name, priority), status, task_id)
) WITH CLUSTERING ORDER BY (status ASC, task_id ASC);

-- Получение следующей задачи
UPDATE task_queue
SET status = 'processing', locked_until = ?
WHERE queue_name = ? AND priority = ? AND status = 'pending'
AND task_id = (
    SELECT task_id FROM task_queue
    WHERE queue_name = ? AND priority = ? AND status = 'pending'
    LIMIT 1
);
```

#### Inbox Pattern
```cql
-- Входящие сообщения пользователя
CREATE TABLE user_inbox (
    user_id UUID,
    message_id TIMEUUID,
    sender_id UUID,
    sender_name TEXT,
    subject TEXT,
    content TEXT,
    read BOOLEAN,
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id), created_at, message_id)
) WITH CLUSTERING ORDER BY (created_at DESC, message_id DESC);

-- Непрочитанные сообщения
SELECT * FROM user_inbox
WHERE user_id = ? AND read = false
ORDER BY created_at DESC;
```

### Graph Pattern

#### Связи между сущностями
```cql
-- Связи пользователей
CREATE TABLE user_relationships (
    user_id UUID,
    relation_type TEXT,
    related_user_id UUID,
    related_user_name TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id, relation_type), related_user_id)
);

-- Друзья пользователя
SELECT related_user_id, related_user_name
FROM user_relationships
WHERE user_id = ? AND relation_type = 'friend';

-- Взаимные друзья (сложный запрос)
SELECT ur1.related_user_id, ur1.related_user_name
FROM user_relationships ur1
JOIN user_relationships ur2 ON ur1.related_user_id = ur2.user_id
WHERE ur1.user_id = ? AND ur2.user_id = ?
AND ur1.relation_type = 'friend' AND ur2.relation_type = 'friend';
```

### Search Pattern

#### Поисковые индексы
```cql
-- Индекс для поиска продуктов
CREATE TABLE product_search (
    search_term TEXT,
    product_id UUID,
    product_name TEXT,
    category TEXT,
    price DECIMAL,
    relevance_score DOUBLE,
    PRIMARY KEY ((search_term), relevance_score, product_id)
) WITH CLUSTERING ORDER BY (relevance_score DESC, product_id ASC);

-- Поиск продуктов
SELECT product_name, category, price
FROM product_search
WHERE search_term = 'laptop'
ORDER BY relevance_score DESC
LIMIT 20;
```

#### Full-text search
```cql
-- SASI индекс для полнотекстового поиска
CREATE CUSTOM INDEX product_name_sasi_idx
ON products(product_name)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
    'mode': 'CONTAINS',
    'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer',
    'case_sensitive': 'false'
};

-- Поиск по тексту
SELECT * FROM products
WHERE product_name LIKE '%laptop%'
ALLOW FILTERING;
```

## Вторичные индексы

### Создание вторичных индексов

#### Простые вторичные индексы
```cql
-- Индекс на статус заказа
CREATE INDEX idx_orders_status ON orders(status);

-- Индекс на категорию продукта
CREATE INDEX idx_products_category ON products(category);

-- Запросы с ALLOW FILTERING
SELECT * FROM orders WHERE status = 'pending' ALLOW FILTERING;
SELECT * FROM products WHERE category = 'electronics' ALLOW FILTERING;
```

#### SASI индексы (SSTable `Attached Secondary` Index)
```cql
-- SASI для префиксного поиска
CREATE CUSTOM INDEX user_name_sasi
ON users(name)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
    'mode': 'PREFIX'
};

-- SASI для полнотекстового поиска
CREATE CUSTOM INDEX product_desc_sasi
ON products(description)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
    'mode': 'CONTAINS',
    'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer'
};

-- SASI для числовых диапазонов
CREATE CUSTOM INDEX product_price_sasi
ON products(price)
USING 'org.apache.cassandra.index.sasi.SASIIndex'
WITH OPTIONS = {
    'mode': 'SPARSE'
};
```

### Когда использовать вторичные индексы

#### Подходящие случаи
```cql
-- Низкая кардинальность, частые запросы
CREATE INDEX idx_orders_status ON orders(status);
-- status: ['pending', 'processing', 'shipped', 'delivered']

-- Геоданные
CREATE INDEX idx_locations_geohash ON locations(geohash);
-- Для запросов в географических областях

-- Категоризации
CREATE INDEX idx_products_category ON products(category);
-- Для фильтрации по категориям
```

#### Когда избегать
```cql
-- Высокая кардинальность
CREATE INDEX idx_users_email ON users(email);
-- Лучше использовать отдельную таблицу users_by_email

-- Часто обновляемые колонки
CREATE INDEX idx_inventory_quantity ON inventory(quantity);
-- Количество часто меняется, индекс будет неэффективен

-- Низкая селективность
CREATE INDEX idx_users_active ON users(active);
-- active имеет только 2 значения, индекс бесполезен
```

## Пользовательские типы (UDT)

### Создание и использование UDT

#### Определение UDT
```cql
-- Адрес
CREATE TYPE address (
    street TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    country TEXT,
    coordinates POINT
);

-- Контактная информация
CREATE TYPE contact_info (
    email TEXT,
    phone TEXT,
    website TEXT,
    social_media MAP<TEXT, TEXT>
);

-- Спецификации продукта
CREATE TYPE product_specs (
    dimensions TEXT,
    weight_kg DECIMAL,
    materials SET<TEXT>,
    features MAP<TEXT, TEXT>
);
```

#### Использование UDT в таблицах
```cql
-- Компании
CREATE TABLE companies (
    company_id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    headquarters FROZEN<address>,
    contacts FROZEN<contact_info>,
    offices LIST<FROZEN<address>>
);

-- Продукты
CREATE TABLE products (
    product_id UUID PRIMARY KEY,
    name TEXT,
    price DECIMAL,
    specs FROZEN<product_specs>,
    warehouse_address FROZEN<address>
);

-- Заказы
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    user_id UUID,
    billing_address FROZEN<address>,
    shipping_address FROZEN<address>,
    items LIST<FROZEN<order_item>>
);

CREATE TYPE order_item (
    product_id UUID,
    product_name TEXT,
    quantity INT,
    unit_price DECIMAL,
    specs FROZEN<product_specs>
);
```

### Работа с UDT в Java

```java
@Configuration
public class CassandraUdtConfig {

    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("localhost", 9042))
            .withKeyspace("ecommerce")
            .withTypeCodecs(new UdtCodecProvider())
            .build();
    }
}

@Component
public class UdtCodecProvider implements TypeCodecProvider {

    @Override
    public <T> TypeCodec<T> getCodec(DataType cqlType, Class<T> javaClass) {
        if (cqlType instanceof UserDefinedType udt) {
            return createUdtCodec(udt, javaClass);
        }
        return null;
    }

    private <T> TypeCodec<T> createUdtCodec(UserDefinedType udt, Class<T> javaClass) {
        switch (udt.getName().asCql(true)) {
            case "address":
                return (TypeCodec<T>) new AddressCodec(udt);
            case "contact_info":
                return (TypeCodec<T>) new ContactInfoCodec(udt);
            case "product_specs":
                return (TypeCodec<T>) new ProductSpecsCodec(udt);
            default:
                return null;
        }
    }
}

public class AddressCodec extends TypeCodec<Address> {

    private final UserDefinedType udt;

    public AddressCodec(UserDefinedType udt) {
        super(udt, Address.class);
        this.udt = udt;
    }

    @Override
    protected Address decode(UdtValue value, ProtocolVersion protocolVersion) {
        return new Address(
            value.getString("street"),
            value.getString("city"),
            value.getString("state"),
            value.getString("zip_code"),
            value.getString("country")
        );
    }

    @Override
    protected UdtValue encode(Address address, ProtocolVersion protocolVersion) {
        return udt.newValue()
            .setString("street", address.getStreet())
            .setString("city", address.getCity())
            .setString("state", address.getState())
            .setString("zip_code", address.getZipCode())
            .setString("country", address.getCountry());
    }
}

@Service
public class CompanyService {

    @Autowired
    private CqlSession session;

    public void createCompany(Company company) {
        PreparedStatement insertCompany = session.prepare("""
            INSERT INTO companies (
                company_id, name, description,
                headquarters, contacts, offices
            ) VALUES (?, ?, ?, ?, ?, ?)
            """);

        session.execute(insertCompany.bind(
            company.getId(),
            company.getName(),
            company.getDescription(),
            company.getHeadquarters(),
            company.getContacts(),
            company.getOffices()
        ));
    }

    public Company getCompany(UUID companyId) {
        PreparedStatement selectCompany = session.prepare("""
            SELECT company_id, name, description,
                   headquarters, contacts, offices
            FROM companies WHERE company_id = ?
            """);

        ResultSet rs = session.execute(selectCompany.bind(companyId));
        Row row = rs.one();

        if (row != null) {
            Company company = new Company();
            company.setId(row.getUuid("company_id"));
            company.setName(row.getString("name"));
            company.setDescription(row.getString("description"));
            company.setHeadquarters(row.get("headquarters", Address.class));
            company.setContacts(row.get("contacts", ContactInfo.class));
            company.setOffices(row.getList("offices", Address.class));
            return company;
        }

        return null;
    }

    public void updateCompanyAddress(UUID companyId, Address newAddress) {
        PreparedStatement updateAddress = session.prepare("""
            UPDATE companies SET headquarters = ? WHERE company_id = ?
            """);

        session.execute(updateAddress.bind(newAddress, companyId));
    }

    public void addOffice(UUID companyId, Address office) {
        PreparedStatement addOffice = session.prepare("""
            UPDATE companies SET offices = offices + ? WHERE company_id = ?
            """);

        session.execute(addOffice.bind(Collections.singletonList(office), companyId));
    }
}
```

## Временные ряды

### Моделирование временных данных

#### Бакетирование по времени
```cql
-- Данные по дням
CREATE TABLE user_activity_daily (
    user_id UUID,
    date DATE,
    activity_id TIMEUUID,
    activity_type TEXT,
    data TEXT,
    timestamp TIMESTAMP,
    PRIMARY KEY ((user_id, date), activity_id)
) WITH CLUSTERING ORDER BY (activity_id DESC);

-- Данные по часам для высокой нагрузки
CREATE TABLE sensor_readings_hourly (
    sensor_id UUID,
    date DATE,
    hour INT,
    reading_id TIMEUUID,
    temperature DOUBLE,
    humidity DOUBLE,
    timestamp TIMESTAMP,
    PRIMARY KEY ((sensor_id, date, hour), reading_id)
) WITH CLUSTERING ORDER BY (reading_id DESC);
```

#### Агрегация временных данных
```cql
-- Таблица агрегированных данных
CREATE TABLE user_stats_daily (
    user_id UUID,
    date DATE,
    login_count INT,
    page_views INT,
    session_duration BIGINT,
    PRIMARY KEY ((user_id), date)
) WITH CLUSTERING ORDER BY (date DESC);

-- Процедура агрегации
CREATE OR REPLACE FUNCTION aggregate_daily_stats(user_id UUID, date DATE)
CALLED ON NULL INPUT
RETURNS VOID
LANGUAGE java AS '
    // Агрегация данных за день
    String query = "SELECT COUNT(*) as logins FROM user_activity_daily " +
                   "WHERE user_id = ? AND date = ?";
    // ... логика агрегации
';
```

### TTL и автоматическая очистка

#### Управление временем жизни данных
```cql
-- Временные данные с TTL
CREATE TABLE user_sessions (
    session_id UUID PRIMARY KEY,
    user_id UUID,
    data TEXT,
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);

-- Вставка с TTL в секундах
INSERT INTO user_sessions (session_id, user_id, data, created_at, expires_at)
VALUES (?, ?, ?, ?, ?) USING TTL 3600; -- 1 час

-- Обновление TTL
UPDATE user_sessions USING TTL 1800
SET data = ? WHERE session_id = ?;

-- Проверка TTL
SELECT session_id, ttl(data) as time_to_live
FROM user_sessions WHERE session_id = ?;
```

#### Автоматическая архивация
```java
@Service
public class DataArchivalService {

    @Autowired
    private CqlSession session;

    @Scheduled(cron = "0 0 2 * * *") // Ежедневно в 02:00
    public void archiveOldData() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);

        // Перемещение старых данных в архив
        session.execute("""
            INSERT INTO user_activity_archive (user_id, date, activity_id, activity_type, data, timestamp)
            SELECT user_id, date, activity_id, activity_type, data, timestamp
            FROM user_activity_daily
            WHERE date < ?
            """, cutoffDate);

        // Удаление старых данных
        session.execute("""
            DELETE FROM user_activity_daily WHERE date < ?
            """, cutoffDate);
    }

    public void setupTtlBasedCleanup() {
        // Создание таблицы с автоматической очисткой
        session.execute("""
            CREATE TABLE temp_notifications (
                user_id UUID,
                notification_id TIMEUUID,
                message TEXT,
                created_at TIMESTAMP,
                PRIMARY KEY ((user_id), notification_id)
            ) WITH default_time_to_live = 604800; -- 7 дней
            """);
    }
}
```

## Геоданные

### Геохэширование

#### Geohash для Cassandra
```cql
-- Таблица геособытий
CREATE TABLE geo_events (
    geohash TEXT,
    event_id TIMEUUID,
    user_id UUID,
    latitude DOUBLE,
    longitude DOUBLE,
    event_type TEXT,
    data TEXT,
    timestamp TIMESTAMP,
    PRIMARY KEY ((geohash), event_id)
) WITH CLUSTERING ORDER BY (event_id DESC);

-- Таблица с несколькими уровнями точности
CREATE TABLE location_search (
    geohash_1 TEXT,   -- 1 символ (2500km)
    geohash_2 TEXT,   -- 2 символа (630km)
    geohash_3 TEXT,   -- 3 символа (78km)
    geohash_6 TEXT,   -- 6 символов (610m)
    location_id UUID,
    name TEXT,
    latitude DOUBLE,
    longitude DOUBLE,
    PRIMARY KEY ((geohash_1, geohash_2, geohash_3), geohash_6, location_id)
);
```

#### Работа с геоданными в Java
```java
@Service
public class GeoService {

    private static final int GEOHASH_LENGTH = 6;

    public void saveGeoEvent(GeoEvent event) {
        String geohash = encodeGeohash(event.getLatitude(), event.getLongitude(), GEOHASH_LENGTH);

        session.execute("""
            INSERT INTO geo_events (
                geohash, event_id, user_id, latitude, longitude,
                event_type, data, timestamp
            ) VALUES (?, now(), ?, ?, ?, ?, ?, ?)
            """,
            geohash,
            event.getUserId(),
            event.getLatitude(),
            event.getLongitude(),
            event.getEventType(),
            event.getData(),
            event.getTimestamp()
        );
    }

    public List<GeoEvent> findNearbyEvents(double latitude, double longitude, double radiusKm) {
        String centerGeohash = encodeGeohash(latitude, longitude, GEOHASH_LENGTH);

        // Получить соседние geohash
        List<String> nearbyGeohashes = getNearbyGeohashes(centerGeohash);

        List<GeoEvent> events = new ArrayList<>();
        for (String geohash : nearbyGeohashes) {
            ResultSet rs = session.execute("""
                SELECT event_id, user_id, latitude, longitude, event_type, data, timestamp
                FROM geo_events WHERE geohash = ?
                """, geohash);

            for (Row row : rs) {
                GeoEvent event = new GeoEvent(
                    row.getUuid("event_id"),
                    row.getUuid("user_id"),
                    row.getDouble("latitude"),
                    row.getDouble("longitude"),
                    row.getString("event_type"),
                    row.getString("data"),
                    row.getInstant("timestamp")
                );

                // Проверка точного расстояния
                if (calculateDistance(latitude, longitude,
                                    event.getLatitude(), event.getLongitude()) <= radiusKm) {
                    events.add(event);
                }
            }
        }

        return events.stream()
            .sorted(Comparator.comparing(GeoEvent::getTimestamp).reversed())
            .collect(Collectors.toList());
    }

    private String encodeGeohash(double latitude, double longitude, int length) {
        // Реализация geohash кодирования
        // Можно использовать библиотеку org.geohex или собственную реализацию
        return "geohash_placeholder";
    }

    private List<String> getNearbyGeohashes(String centerGeohash) {
        // Получить соседние geohash для поиска в области
        return Arrays.asList(centerGeohash); // placeholder
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Расчет расстояния по формуле гаверсинуса
        final int R = 6371; // Радиус Земли в км

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
```

## Миграции схемы

### Управление изменениями схемы

#### Версионирование схемы
```sql
-- Таблица для отслеживания миграций
CREATE TABLE schema_migrations (
    migration_id TEXT PRIMARY KEY,
    description TEXT,
    applied_at TIMESTAMP,
    applied_by TEXT,
    checksum TEXT
);

-- Пример миграции
INSERT INTO schema_migrations (migration_id, description, applied_at, applied_by, checksum)
VALUES ('001_add_user_preferences', 'Add preferences column to users table', toTimestamp(now()), 'admin', 'abc123');
```

#### Безопасные изменения схемы
```cql
-- Добавление колонки (безопасно)
ALTER TABLE users ADD preferences MAP<TEXT, TEXT>;

-- Добавление новой таблицы (безопасно)
CREATE TABLE user_preferences (
    user_id UUID PRIMARY KEY,
    theme TEXT,
    notifications BOOLEAN,
    language TEXT
);

-- Изменение типа (рискованно, требует тестирования)
-- ALTER TABLE products ALTER price TYPE DECIMAL;

-- Удаление колонки (опасно, проверить использование)
-- ALTER TABLE users DROP old_column;
```

### Инструменты миграции

#### Cassandra Migration
```java
@Configuration
public class CassandraMigrationConfig {

    @Bean
    public CassandraMigration cassandraMigration(CqlSession session) {
        CassandraMigration migration = new CassandraMigration(session);
        migration.setLocations("classpath:cassandra/migrations");
        return migration;
    }
}

// Файлы миграций
// V1__Create_initial_schema.cql
CREATE KEYSPACE myapp WITH replication = {
    'class': 'NetworkTopologyStrategy',
    'dc1': 3
};

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email TEXT,
    name TEXT
);

// V2__Add_user_preferences.cql
ALTER TABLE users ADD preferences MAP<TEXT, TEXT>;

CREATE TABLE user_preferences (
    user_id UUID PRIMARY KEY,
    theme TEXT,
    notifications BOOLEAN
);
```

#### Liquibase для Cassandra
```xml
<!-- liquibase changelog -->
<databaseChangeLog>
    <changeSet id="1" author="developer">
        <createKeyspace keyspaceName="myapp">
            <replication>
                <strategy>NetworkTopologyStrategy</strategy>
                <datacenter name="dc1" replicationFactor="3"/>
            </replication>
        </createKeyspace>

        <createTable tableName="users" keyspaceName="myapp">
            <column name="id" type="UUID">
                <constraints primaryKey="true"/>
            </column>
            <column name="email" type="TEXT"/>
            <column name="name" type="TEXT"/>
        </createTable>
    </changeSet>

    <changeSet id="2" author="developer">
        <addColumn tableName="users" keyspaceName="myapp">
            <column name="preferences" type="MAP&lt;TEXT,TEXT&gt;"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

## Производительность модели

### Мониторинг производительности

#### Метрики запросов
```java
@Service
public class QueryPerformanceMonitor {

    @Autowired
    private CqlSession session;

    public QueryMetrics measureQuery(String queryName, String cql, Object... params) {
        long startTime = System.nanoTime();

        ResultSet rs = session.execute(cql, params);
        List<Row> results = rs.all();

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        ExecutionInfo info = rs.getExecutionInfo();

        return new QueryMetrics(
            queryName,
            results.size(),
            executionTimeMs,
            info.getCoordinator(),
            info.getSpeculativeExecutionCount(),
            info.getPagingState() != null
        );
    }

    public List<QueryMetrics> benchmarkQueries() {
        List<QueryMetrics> metrics = new ArrayList<>();

        // Тестирование различных запросов
        metrics.add(measureQuery("find_user_by_id",
            "SELECT * FROM users WHERE id = ?", UUID.randomUUID()));

        metrics.add(measureQuery("find_user_orders",
            "SELECT * FROM user_orders WHERE user_id = ? LIMIT 10", UUID.randomUUID()));

        metrics.add(measureQuery("search_products",
            "SELECT * FROM products WHERE category = ? ALLOW FILTERING LIMIT 20", "electronics"));

        return metrics.stream()
            .sorted(Comparator.comparingLong(QueryMetrics::getExecutionTimeMs).reversed())
            .collect(Collectors.toList());
    }

    public Map<String, Object> analyzeTablePerformance(String keyspace, String table) {
        // Получение статистики таблицы
        ResultSet rs = session.execute("""
            SELECT keyspace_name, table_name, memtable_data_size, sstable_count,
                   bloom_filter_false_positives, bloom_filter_false_ratio
            FROM system_schema.table_stats
            WHERE keyspace_name = ? AND table_name = ?
            """, keyspace, table);

        if (rs.hasNext()) {
            Row row = rs.one();
            Map<String, Object> stats = new HashMap<>();
            stats.put("memtableDataSize", row.getLong("memtable_data_size"));
            stats.put("sstableCount", row.getLong("sstable_count"));
            stats.put("bloomFilterFalsePositives", row.getLong("bloom_filter_false_positives"));
            stats.put("bloomFilterFalseRatio", row.getDouble("bloom_filter_false_ratio"));
            return stats;
        }

        return new HashMap<>();
    }
}

class QueryMetrics {
    private String queryName;
    private int resultCount;
    private long executionTimeMs;
    private InetAddress coordinator;
    private int speculativeExecutions;
    private boolean usedPaging;

    // constructor, getters, setters
}
```

#### Оптимизация модели
```java
@Service
public class ModelOptimizationService {

    @Autowired
    private QueryPerformanceMonitor performanceMonitor;

    public List<OptimizationRecommendation> analyzeAndRecommend(String keyspace) {
        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        // Анализ таблиц
        List<String> tables = getTables(keyspace);

        for (String table : tables) {
            Map<String, Object> stats = performanceMonitor.analyzeTablePerformance(keyspace, table);

            // Рекомендации по оптимизации
            if ((Long) stats.get("memtableDataSize") > 100 * 1024 * 1024) { // > 100MB
                recommendations.add(new OptimizationRecommendation(
                    table, "Large memtable", "Consider increasing memtable_heap_space_in_mb"
                ));
            }

            if ((Long) stats.get("sstableCount") > 50) {
                recommendations.add(new OptimizationRecommendation(
                    table, "Many SSTables", "Consider running nodetool compaction"
                ));
            }

            if ((Double) stats.get("bloomFilterFalseRatio") > 0.1) {
                recommendations.add(new OptimizationRecommendation(
                    table, "High false positive rate", "Consider adjusting bloom_filter_fp_chance"
                ));
            }
        }

        return recommendations;
    }

    public void optimizeTable(String keyspace, String table, String optimization) {
        switch (optimization) {
            case "compact":
                runNodetoolCommand("compact", keyspace, table);
                break;
            case "cleanup":
                runNodetoolCommand("cleanup", keyspace, table);
                break;
            case "scrub":
                runNodetoolCommand("scrub", keyspace, table);
                break;
        }
    }

    private List<String> getTables(String keyspace) {
        ResultSet rs = session.execute("""
            SELECT table_name FROM system_schema.tables
            WHERE keyspace_name = ?
            """, keyspace);

        return rs.all().stream()
            .map(row -> row.getString("table_name"))
            .collect(Collectors.toList());
    }

    private void runNodetoolCommand(String command, String keyspace, String table) {
        try {
            Process process = new ProcessBuilder()
                .command("nodetool", command, keyspace, table)
                .start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Nodetool command failed: " + command);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to run nodetool command", e);
        }
    }
}

class OptimizationRecommendation {
    private String tableName;
    private String issue;
    private String recommendation;

    // constructor, getters, setters
}
```

## Инструменты моделирования

### Cassandra Designer

#### Визуальное моделирование
```java
// Программное создание модели
public class CassandraModelDesigner {

    public KeyspaceDesign designEcommerceKeyspace() {
        KeyspaceDesign keyspace = new KeyspaceDesign("ecommerce");

        // Определение таблиц
        TableDesign usersTable = new TableDesign("users")
            .addPartitionKey("user_id", DataType.UUID)
            .addColumn("email", DataType.TEXT)
            .addColumn("name", DataType.TEXT)
            .addColumn("preferences", DataType.mapOf(DataType.TEXT, DataType.TEXT));

        TableDesign productsTable = new TableDesign("products")
            .addPartitionKey("product_id", DataType.UUID)
            .addColumn("name", DataType.TEXT)
            .addColumn("price", DataType.DECIMAL)
            .addColumn("category", DataType.TEXT)
            .addColumn("tags", DataType.setOf(DataType.TEXT));

        TableDesign ordersTable = new TableDesign("orders")
            .addPartitionKey("user_id", DataType.UUID)
            .addClusteringKey("order_id", DataType.UUID)
            .addColumn("total_amount", DataType.DECIMAL)
            .addColumn("status", DataType.TEXT)
            .addColumn("created_at", DataType.TIMESTAMP);

        // Добавление связей
        usersTable.addRelationship(ordersTable, RelationshipType.ONE_TO_MANY);

        keyspace.addTable(usersTable);
        keyspace.addTable(productsTable);
        keyspace.addTable(ordersTable);

        return keyspace;
    }

    public String generateCQL(KeyspaceDesign keyspace) {
        StringBuilder cql = new StringBuilder();

        // Генерация CQL для keyspace
        cql.append("CREATE KEYSPACE ").append(keyspace.getName())
           .append(" WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': 3};")
           .append("\n\n");

        // Генерация CQL для таблиц
        for (TableDesign table : keyspace.getTables()) {
            cql.append(generateTableCQL(table)).append("\n\n");
        }

        return cql.toString();
    }

    private String generateTableCQL(TableDesign table) {
        StringBuilder cql = new StringBuilder("CREATE TABLE ").append(table.getName()).append(" (\n");

        // Колонки
        List<String> columns = new ArrayList<>();
        for (ColumnDesign column : table.getColumns()) {
            columns.add("  " + column.getName() + " " + column.getDataType().getCqlType());
        }

        cql.append(String.join(",\n", columns));
        cql.append(",\n  PRIMARY KEY (");

        // Partition key
        List<String> pkParts = new ArrayList<>();
        if (table.getPartitionKeys().size() > 1) {
            pkParts.add("(" + table.getPartitionKeys().stream()
                .map(ColumnDesign::getName)
                .collect(Collectors.joining(", ")) + ")");
        } else {
            pkParts.addAll(table.getPartitionKeys().stream()
                .map(ColumnDesign::getName)
                .collect(Collectors.toList()));
        }

        // Clustering keys
        if (!table.getClusteringKeys().isEmpty()) {
            pkParts.addAll(table.getClusteringKeys().stream()
                .map(ColumnDesign::getName)
                .collect(Collectors.toList()));
        }

        cql.append(String.join(", ", pkParts));
        cql.append(")\n);");

        return cql.toString();
    }
}
```

### DataStax Studio

#### Анализ и оптимизация запросов
```cql
-- В DataStax Studio можно анализировать запросы
-- и получать рекомендации по оптимизации

-- Пример анализа плана выполнения
EXPLAIN SELECT * FROM user_orders
WHERE user_id = 123e4567-e89b-12d3-a456-426614174000
ORDER BY created_at DESC
LIMIT 10;

-- Результат покажет:
-- - Используемые индексы
-- - Количество проверяемых партиций
-- - Оценку стоимости запроса
-- - Рекомендации по оптимизации
```

### KillrVideo

#### Референсная архитектура
```java
// KillrVideo - референсное приложение Cassandra
// https://github.com/killrvideo/killrvideo-cassandra

@Service
public class VideoService {

    // Пример моделирования для видео сервиса
    public void createVideoSchema() {
        session.execute("""
            CREATE KEYSPACE killrvideo WITH replication = {
                'class': 'NetworkTopologyStrategy',
                'dc1': 3
            };
            """);

        // Таблица видео
        session.execute("""
            CREATE TABLE videos (
                video_id UUID,
                added_date DATE,
                title TEXT,
                description TEXT,
                user_id UUID,
                tags SET<TEXT>,
                PRIMARY KEY ((video_id), added_date)
            );
            """);

        // Таблица видео по пользователю
        session.execute("""
            CREATE TABLE videos_by_user (
                user_id UUID,
                added_date DATE,
                video_id UUID,
                title TEXT,
                description TEXT,
                tags SET<TEXT>,
                PRIMARY KEY ((user_id), added_date, video_id)
            ) WITH CLUSTERING ORDER BY (added_date DESC, video_id DESC);
            """);

        // Таблица видео по тегам
        session.execute("""
            CREATE TABLE videos_by_tag (
                tag TEXT,
                video_id UUID,
                added_date DATE,
                title TEXT,
                user_id UUID,
                PRIMARY KEY ((tag), added_date, video_id)
            ) WITH CLUSTERING ORDER BY (added_date DESC, video_id DESC);
            """);
    }

    // Запросы для видео
    public List<Video> findVideosByUser(UUID userId, int pageSize, ByteBuffer pagingState) {
        Statement<?> statement = SimpleStatement.newInstance("""
            SELECT video_id, title, description, added_date, tags
            FROM videos_by_user
            WHERE user_id = ?
            """, userId).setPageSize(pageSize);

        if (pagingState != null) {
            statement.setPagingState(pagingState);
        }

        ResultSet rs = session.execute(statement);
        return rs.all().stream()
            .map(this::mapToVideo)
            .collect(Collectors.toList());
    }

    public List<Video> findVideosByTag(String tag, LocalDate fromDate) {
        return session.execute("""
            SELECT video_id, title, user_id, added_date
            FROM videos_by_tag
            WHERE tag = ? AND added_date >= ?
            ORDER BY added_date DESC
            LIMIT 20
            """, tag, fromDate).all().stream()
            .map(this::mapToVideoPreview)
            .collect(Collectors.toList());
    }

    private Video mapToVideo(Row row) {
        return new Video(
            row.getUuid("video_id"),
            row.getString("title"),
            row.getString("description"),
            row.getDate("added_date"),
            row.getSet("tags", String.class)
        );
    }

    private Video mapToVideoPreview(Row row) {
        return new Video(
            row.getUuid("video_id"),
            row.getString("title"),
            null, // description not included
            row.getDate("added_date"),
            null  // tags not included
        );
    }
}
```

## Лучшие практики

### Проектирование схемы

#### 1. Query-First Design
- **Определяйте запросы до схемы**
- **Создавайте таблицы для конкретных паттернов**
- **Денормализуйте для производительности**
- **Тестируйте на реальных данных**

#### 2. Выбор Partition Key
- **Высокая кардинальность** для равномерного распределения
- **Естественное распределение** запросов
- **Избегайте hot partitions**
- **Используйте composite keys** при необходимости

#### 3. Clustering Columns
- **Определяйте порядок сортировки**
- **Используйте для range queries**
- **Ограничивайте количество** (не более 4-5)
- **Учитывайте размер clustering key**

### Оптимизация производительности

#### 1. Размер партиций
- **Идеально**: 100MB — 300MB на партицию
- **Максимум**: Не более 1GB
- **Мониторинг**: **nodetool tablehistograms**
- **Перераспределение**: при необходимости

#### 2. Вторичные индексы
- **Используйте SASI** для сложных запросов
- **Создавайте отдельные таблицы** для поиска
- **Избегайте на часто обновляемых колонках**
- **Тестируйте производительность**

#### 3. Материализованные представления
- **Автоматическая денормализация**
- **Поддержка актуальности данных**
- **Оптимизация для конкретных запросов**
- **Мониторинг синхронизации**

### Масштабируемость

#### 1. Горизонтальное масштабирование
- **Добавление узлов** без **downtime**
- **Перебалансировка** данных
- **Мониторинг распределения нагрузки**
- **Планирование capacity**

#### 2. Геораспределение
- **Несколько датацентров**
- **NetworkTopologyStrategy**
- **Локальная консистентность**
- **Резервное копирование между DC**

### Безопасность и мониторинг

#### 1. Безопасность данных
- **Шифрование** в транзите и at **rest**
- **RBAC** (Role-`Based Access` Control)
- **Аудит** доступа к данным
- **Маскировка** чувствительных данных

#### 2. Мониторинг
- **Ключевые метрики** производительности
- **Алерты** на проблемы
- **Трассировка запросов**
- **Анализ паттернов использования**

### Обслуживание и поддержка

#### 1. Регулярное обслуживание
- **Мониторинг состояния кластера**
- **Ротация логов и бэкапов**
- **Обновление версий Cassandra**
- **Оптимизация конфигурации**

#### 2. Резервное копирование
- **Регулярные бэкапы** всех **keyspace**
- **Тестирование восстановления**
- **Хранение в нескольких локациях**
- **Шифрование бэкапов**

## Решение проблем

### Диагностика и устранение
- **Диагностика проблем** с **nodetool**
- **Анализ логов Cassandra**
- **Мониторинг системных ресурсов**
- **Планирование capacity**

### Инструменты и автоматизация

#### 1. Инструменты разработки
- **DataStax Studio** для разработки
- **Cassandra Reaper** для ремонта
- **Medusa** для бэкапов
- **Cassandra Operator** для **Kubernetes**

#### 2. Автоматизация
- **Ansible** для развертывания
- **Terraform** для инфраструктуры
- **Jenkins/`GitLab` CI** для тестирования
- **Prometheus/Grafana** для мониторинга

### Обучение и сертификация

#### 1. Ресурсы обучения
- **DataStax Academy** — официальные курсы
- **Apache `Cassandra` Documentation**
- **Cassandra Summit** — конференции
- **Community форумы** и **Slack**

#### 2. Сертификация
- **DataStax `Certified Cassandra` Administrator**
- **DataStax `Certified Cassandra` Developer**
- **AWS `Certified Cassandra` Specialty**
- **Лучшие практики** от экспертов

**Моделирование данных в **Cassandra** — это фундаментально отличающийся от реляционных баз данных подход, требующий полного переосмысления принципов проектирования. Ключевые особенности:**

### Основные принципы:

1. **Query-`First` Design** — проектирование схемы на основе запросов
2. **Денормализация** — хранение данных в виде, оптимальном для чтения
3. **Партиционирование** — распределение данных по узлам кластера
4. **Масштабируемость** — возможность роста без изменения схемы

### Ключевые компоненты модели:

- **Keyspace** — логическое разделение данных
- **Table** — основная единица хранения
- **Primary Key** — **partition key** + **clustering columns**
- **Materialized Views** — автоматическая денормализация
- **User `Defined` Types** — сложные структуры данных

### Стратегии моделирования:

1. **Бакетирование** — разделение данных по времени/регионам
2. **Дублирование данных** — для разных представлений
3. **Индексы** — вторичные индексы и **SASI**
4. **TTL** — автоматическая очистка временных данных

### Инструменты и best practices:

- **DataStax Studio** для визуального моделирования
- **CQL** для определения схемы
- **Performance monitoring** для оптимизации
- **Регулярное тестирование** и профилирование

### Вызовы и решения:

1. **Сложность** — требует глубокого понимания распределенных систем
2. **Денормализация** — усложняет обновления данных
3. **Консистентность** — **trade-off** между скоростью и надежностью
4. **Масштабирование** — требует планирования с самого начала

### Когда выбирать Cassandra:

- **Big Data** с высокими требованиями к масштабируемости
- **Time-series данные** — логи, метрики, события
- **Глобальные приложения** с геораспределенными пользователями
- **IoT и реального времени** — обработка потоков данных
- **Высоконагруженные OLTP** с миллионами операций в секунду

Правильное моделирование данных — это ключ к успеху **Cassandra** приложений. Инвестиции в качественное проектирование схемы окупаются многократно при эксплуатации системы. 🎯


