# Базы данных (Databases)

**Комплексные руководства по реляционным, NoSQL, графовым и временным рядам базам данных**

## 📋 Описание

Этот раздел содержит детальные руководства по различным типам баз данных: реляционные (PostgreSQL, MySQL), NoSQL (MongoDB, Redis, Cassandra), графовые (Neo4j), временные ряды (ClickHouse, InfluxDB). Каждая БД покрыта от основ до продвинутых тем.

## 📚 Структура раздела

### 📊 [Relational Databases](relational/)
**Реляционные базы данных - ACID, SQL, транзакции**

#### 🐘 [PostgreSQL](relational/postgresql/)
**21 файл** - Продвинутая реляционная БД

- **[PostgreSQL Basics](relational/postgresql/postgres-basics.md)** - Основы PostgreSQL
- **[PostgreSQL Design](relational/postgresql/postgres-design.md)** - Проектирование схемы
- **[PostgreSQL Queries](relational/postgresql/postgres-queries.md)** - SQL запросы
- **[PostgreSQL Indexes](relational/postgresql/postgres-indexes.md)** - Индексы и оптимизация
- **[PostgreSQL Transactions](relational/postgresql/postgres-transactions.md)** - Транзакции и изоляция
- **[PostgreSQL Partitioning](relational/postgresql/postgres-partitioning.md)** - Партиционирование

#### 🐬 [MySQL](relational/mysql/)
**7 файлов** - Популярная реляционная БД

- **[MySQL Basics](relational/mysql/mysql-basics.md)** - Основы MySQL
- **[MySQL Queries](relational/mysql/mysql-queries.md)** - SQL запросы

#### 🏛️ [Other Relational](relational/)
- **[Oracle](relational/oracle/oracle-basics.md)** - Oracle Database
- **[SQL Server](relational/sql-server/sql-server-basics.md)** - Microsoft SQL Server

### 🚀 [NoSQL Databases](nosql/)
**NoSQL базы данных - документы, ключ-значение, колонки**

#### 🍃 [MongoDB](nosql/mongodb/)
**11 файлов** - Документная БД

- **[MongoDB Basics](nosql/mongodb/mongodb-basics.md)** - Основы MongoDB
- **[MongoDB CRUD](nosql/mongodb/mongodb-crud.md)** - CRUD операции
- **[MongoDB Queries](nosql/mongodb/mongodb-queries.md)** - Запросы и фильтры
- **[MongoDB Aggregation](nosql/mongodb/mongodb-aggregation.md)** - Aggregation Framework
- **[MongoDB Indexes](nosql/mongodb/mongodb-indexes.md)** - Индексы
- **[MongoDB Replication](nosql/mongodb/mongodb-replication.md)** - Репликация
- **[MongoDB Sharding](nosql/mongodb/mongodb-sharding.md)** - Шардинг

#### 🔴 [Redis](nosql/redis/)
**16 файлов** - In-memory ключ-значение БД

- **[Redis Basics](nosql/redis/redis-basics.md)** - Основы Redis
- **[Redis Data Structures](nosql/redis/redis-data-structures.md)** - Структуры данных
- **[Redis Commands](nosql/redis/redis-commands.md)** - Команды Redis
- **[Redis Persistence](nosql/redis/redis-persistence.md)** - Персистентность
- **[Redis Replication](nosql/redis/redis-replication.md)** - Репликация
- **[Redis Cluster](nosql/redis/redis-cluster.md)** - Кластеризация

#### 🗄️ [Other NoSQL](nosql/)
- **[Cassandra](nosql/cassandra/)** - Wide-column БД
- **[Elasticsearch](nosql/elasticsearch/)** - Поисковая БД
- **[ClickHouse](nosql/clickhouse/)** - Аналитическая БД
- **[Couchbase](nosql/couchbase/)** - Документная БД

### 📈 [Time Series Databases](time-series/)
**Базы данных для временных рядов**

- **[InfluxDB](time-series/influxdb/influxdb-basics.md)** - InfluxDB
- **[TimescaleDB](time-series/timescaledb/timescaledb-basics.md)** - TimescaleDB

### 🕸️ [Graph Databases](graph/)
**Графовые базы данных**

- **[Neo4j](graph/neo4j/neo4j-basics.md)** - Neo4j
- **[OrientDB](graph/orientdb/orientdb-basics.md)** - OrientDB

### 🔧 [ORM](orm/)
**Object-Relational Mapping**

- **[ORM Basics](orm/orm-basics.md)** - Основы ORM

### 📝 [SQL](sql/)
**SQL основы**

- **[SQL Basics](sql/sql-basics.md)** - Основы SQL

## 🎯 Для кого этот раздел

### Новички
- **Изучение SQL** - основы работы с реляционными БД
- **Понимание NoSQL** - когда использовать документные БД
- **Выбор БД** - какой тип БД использовать

### Опытные разработчики
- **Оптимизация запросов** - индексы, партиционирование
- **Масштабирование** - репликация, шардинг, кластеризация
- **Архитектура данных** - выбор правильной БД для задачи

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. SQL Basics
   ├── SELECT, INSERT, UPDATE, DELETE
   ├── JOIN операции
   └── Индексы

2. PostgreSQL
   ├── Основы
   ├── Проектирование схемы
   └── Оптимизация запросов

3. NoSQL выбор
   ├── MongoDB (документы)
   └── Redis (кэширование)
```

### Для опытных
```
1. Выбор БД по задаче
   ├── PostgreSQL → OLTP, ACID транзакции
   ├── MongoDB → Гибкая схема, документы
   ├── Redis → Кэширование, сессии
   ├── ClickHouse → Аналитика, OLAP
   └── Neo4j → Графовые связи

2. Продвинутые темы
   ├── Репликация и шардинг
   ├── Оптимизация производительности
   └── Архитектура данных
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Frameworks](../frameworks/)** - Spring Data, Hibernate
- **[Libraries](../libraries/)** - JOOQ, HikariCP
- **[Interview](../interview/databases/)** - Вопросы на собеседовании
- **[Architecture](../architecture/)** - Database architecture patterns

### Специфичные связи
- **PostgreSQL** → [Spring Data JPA](../frameworks/java-frameworks/spring/spring-data-jpa.md)
- **MongoDB** → [Spring Data MongoDB](../frameworks/java-frameworks/spring/spring-mongodb.md)
- **Redis** → [Spring Redis](../frameworks/java-frameworks/spring/spring-redis.md)
- **Hibernate** → [Interview](../interview/databases/hibernate-interview.md)

## 📚 Полезные ресурсы

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Redis Documentation](https://redis.io/documentation)
- [ClickHouse Documentation](https://clickhouse.com/docs/)

### Учебные материалы
- [PostgreSQL Tutorial](https://www.postgresqltutorial.com/)
- [MongoDB University](https://university.mongodb.com/)
- [Redis University](https://university.redis.com/)

## 🎯 Следующие шаги

После изучения баз данных:
1. **Изучите ORM** → [ORM](orm/)
2. **Освойте Spring Data** → [Spring Data JPA](../frameworks/java-frameworks/spring/spring-data-jpa.md)
3. **Изучите архитектуру БД** → [Database Architecture](../interview/databases/database-architecture-interview.md)
4. **Подготовьтесь к интервью** → [Interview](../interview/databases/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../libraries/)

*Обновлено: 2026-01-25*
