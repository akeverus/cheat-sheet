# NoSQL базы данных (NoSQL Databases)

**Документные, ключ-значение, колоночные и поисковые базы данных**

## 📋 Описание

Этот раздел содержит детальные руководства по NoSQL базам данных: MongoDB (документная), Redis (ключ-значение), Cassandra (колоночная), Elasticsearch (поисковая), ClickHouse (аналитическая), Couchbase (документная).

## 📚 Структура раздела

### 🍃 [MongoDB](mongodb/)
**11 файлов** - Документная БД

- **[MongoDB Basics](mongodb/mongodb-basics.md)** - Основы MongoDB
- **[MongoDB CRUD](mongodb/mongodb-crud.md)** - CRUD операции
- **[MongoDB Queries](mongodb/mongodb-queries.md)** - Запросы и фильтры
- **[MongoDB Aggregation](mongodb/mongodb-aggregation.md)** - Aggregation Framework
- **[MongoDB Indexes](mongodb/mongodb-indexes.md)** - Индексы
- **[MongoDB Replication](mongodb/mongodb-replication.md)** - Репликация
- **[MongoDB Sharding](mongodb/mongodb-sharding.md)** - Шардинг

### 🔴 [Redis](redis/)
**16 файлов** - In-memory ключ-значение БД

- **[Redis Basics](redis/redis-basics.md)** - Основы Redis
- **[Redis Data Structures](redis/redis-data-structures.md)** - Структуры данных
- **[Redis Commands](redis/redis-commands.md)** - Команды Redis
- **[Redis Persistence](redis/redis-persistence.md)** - Персистентность
- **[Redis Replication](redis/redis-replication.md)** - Репликация
- **[Redis Cluster](redis/redis-cluster.md)** - Кластеризация

### 🗄️ [Other NoSQL](.)
**Другие NoSQL БД**

- **[Cassandra](cassandra/)** - Wide-column БД
- **[Elasticsearch](elasticsearch/)** - Поисковая БД
- **[ClickHouse](clickhouse/)** - Аналитическая БД
- **[Couchbase](couchbase/)** - Документная БД

## 🎯 Для кого этот раздел

### Database Developers
- **Выбор NoSQL БД** - MongoDB vs Redis vs Cassandra
- **Проектирование схемы** - документная модель
- **Масштабирование** - шардинг, репликация

### Backend Developers
- **Работа с NoSQL** - CRUD операции, запросы
- **Кэширование** - Redis для кэширования
- **Поиск** - Elasticsearch для поиска

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. MongoDB
   ├── MongoDB Basics
   ├── CRUD операции
   └── Запросы

2. Redis
   ├── Redis Basics
   └── Data Structures

3. Выбор БД
   ├── MongoDB → Документы
   └── Redis → Кэширование
```

### Для опытных
```
1. Advanced MongoDB
   ├── Aggregation Framework
   ├── Индексы
   └── Шардинг

2. Advanced Redis
   ├── Persistence
   ├── Replication
   └── Cluster

3. Other NoSQL
   ├── Cassandra → Wide-column
   ├── Elasticsearch → Поиск
   └── ClickHouse → Аналитика
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Frameworks](../../frameworks/)** - Spring Data MongoDB, Spring Redis
- **[Interview](../../interview/databases/)** - Вопросы на собеседовании
- **[Architecture](../../architecture/)** - Database architecture patterns

### Специфичные связи
- **MongoDB** → [Spring Data MongoDB](../../frameworks/java-frameworks/spring/spring-mongodb.md)
- **Redis** → [Spring Redis](../../frameworks/java-frameworks/spring/spring-redis.md)
- **MongoDB** → [Interview](../../interview/databases/mongodb-interview.md)
- **Redis** → [Interview](../../interview/databases/redis-interview.md)

## 📚 Полезные ресурсы

### Официальная документация
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Redis Documentation](https://redis.io/documentation)
- [Cassandra Documentation](https://cassandra.apache.org/doc/latest/)
- [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)

### Учебные материалы
- [MongoDB University](https://university.mongodb.com/)
- [Redis University](https://university.redis.com/)

## 🎯 Следующие шаги

После изучения NoSQL БД:
1. **Изучите Spring Data** → [Spring Data MongoDB](../../frameworks/java-frameworks/spring/spring-mongodb.md)
2. **Освойте кэширование** → [Spring Redis](../../frameworks/java-frameworks/spring/spring-redis.md)
3. **Подготовьтесь к интервью** → [Interview](../../interview/databases/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../time-series/)

*Обновлено: 2026-01-25*
