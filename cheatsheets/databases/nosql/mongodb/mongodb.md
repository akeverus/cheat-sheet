---
title: "MongoDB: Полное руководство по документо-ориентированной NoSQL базе данных"
description: "Комплексное руководство по MongoDB: архитектура, CRUD операции, индексы, агрегация, репликация, шардирование, производительность и интеграция с Java/Spring"
tags:
  - mongodb
  - nosql
  - database
  - document-database
  - json
  - aggregation
  - replication
  - sharding
difficulty: "intermediate"
prerequisites: ["databases/postgres-basics.md", "java/java-basics.md"]
next: ["databases/redis.md", "spring/spring-data-jpa.md"]
updated: "2026-02-06"
related: ["databases/postgres-basics.md", "spring/spring-data-jpa.md", "java/java-basics.md"]
---

# MongoDB: Полное руководство по документо-ориентированной NoSQL базе данных

## Введение в MongoDB

**MongoDB** — это высокопроизводительная, документо-ориентированная **NoSQL** база данных с открытым исходным кодом. **MongoDB** хранит данные в формате **BSON** (Binary JSON) и обеспечивает гибкую схему данных, горизонтальную масштабируемость и богатый набор функций для работы с данными.

### Архитектура MongoDB

```text
┌─────────────────────────────────────────────────────────────────┐
│                          MongoDB Cluster                        │
├─────────────────────────────────────────────────────────────────┤
│  Config Servers │ Mongos Routers │ Shard Servers │ Replica Sets │
├─────────────────────────────────────────────────────────────────┤
│                    MongoDB Core Engine                          │
├─────────────────────────────────────────────────────────────────┤
│  Database │ Collections │ Documents │ Indexes │ Aggregation    │
├─────────────────────────────────────────────────────────────────┤
│                    WiredTiger Storage Engine                    │
└─────────────────────────────────────────────────────────────────┘
```

### Основные характеристики MongoDB

- **Документо-ориентированная модель**: Данные хранятся в виде **JSON**-подобных документов
- **Гибкая схема**: Не требует предварительного определения структуры данных
- **Горизонтальная масштабируемость**: Поддержка шардирования для распределения данных
- **Высокая производительность**: Оптимизированные индексы и **in-memory** операции
- **Репликация**: Автоматическое копирование данных для отказоустойчивости
- **Мощный aggregation framework**: Для сложных запросов и аналитики
- **Поддержка транзакций**: **ACID** транзакции для нескольких документов

### Преимущества MongoDB

1. **Гибкость схемы**: Легко адаптировать структуру данных без миграций
2. **Масштабируемость**: Горизонтальное масштабирование через шардирование
3. **Производительность**: Быстрые чтение/запись для больших объемов данных
4. **Разработчик-friendly**: **JSON**-подобный формат данных
5. **Rich `Query` Language**: Мощные возможности для запросов и агрегаций
6. **Community и Ecosystem**: Большое сообщество и множество инструментов

### Недостатки MongoDB

1. **ACID транзакции**: Ограниченная поддержка для сложных транзакций (до версии 4.0)
2. **JOIN операции**: Отсутствие встроенных **JOIN**, требуется денормализация
3. **Потребление памяти**: Высокое потребление **RAM** для индексов
4. **Согласованность**: **Eventual consistency** в распределенных системах

### Варианты использования MongoDB

- **Контент-менеджмент системы**: Хранение контента и метаданных
- **Социальные сети**: Профили пользователей, посты, комментарии
- **IoT и Big Data**: Хранение сенсорных данных и аналитика
- **Каталоги продуктов**: **E-commerce** каталоги с вариативными атрибутами
- **Журналирование**: Логи приложений и системных событий
- **Кэширование**: Быстрое хранение сессий и временных данных
- **Аналитика**: Обработка больших объемов данных в реальном времени
- **Конфигурационные данные**: Хранение настроек приложений
- **Файловое хранилище**: **GridFS** для больших файлов

### Сравнение с реляционными базами данных

| Аспект | **MongoDB** | Реляционные СУБД |
|--------|---------|-------------------|
| **Модель данных** | Документы (BSON) | Таблицы с фиксированной схемой |
| **Схема** | Гибкая (schema-less) | Строгая схема |
| **Запросы** | **JSON**-подобные | **SQL** |
| **Масштабируемость** | Горизонтальная | Вертикальная |
| **Транзакции** | Много-документные (с 4.0) | **ACID** по умолчанию |
| **JOIN** | $**lookup** в **aggregation** | Встроенные **JOIN** |
| **Индексы** | **B-tree**, **geospatial**, **text** | **B-tree**, **hash**, **etc**. |

## Полезные ссылки

### Официальная документация
- [MongoDB Manual](https://www.mongodb.com/docs/manual/)
- [MongoDB Getting Started](https://www.mongodb.com/docs/manual/getting-started/)
- [MongoDB Installation](https://www.mongodb.com/docs/manual/installation/)

### Обучающие материалы
- [Introduction to MongoDB](https://www.baeldung.com/java-mongodb)
- [MongoDB with Spring Boot](https://www.baeldung.com/spring-data-mongodb-tutorial)

### См. также
- [[mongodb-basics|Основы]] — **MongoDB**
- [[mongodb-crud|CRUD]] — **CRUD** операции
- [[mongodb-queries|Запросы]] — запросы и фильтры
- [[mongodb-indexes|Индексы]] — индексы и оптимизация
- [[mongodb-aggregation|Aggregation]] — **Aggregation Framework**
- [[mongodb-replication|Репликация]] — репликация
- [[mongodb-sharding|Шардирование]] — шардирование
- [[mongodb-performance|Производительность]] — производительность

## Содержание

- [**MongoDB**: Полное руководство по документо-ориентированной **NoSQL** базе данных](#mongodb-полное-руководство-по-документо-ориентированной-nosql-базе-данных)
- [Введение в **MongoDB**](#введение-в-mongodb)
  - [Архитектура **MongoDB**](#архитектура-mongodb)
  - [Основные характеристики **MongoDB**](#основные-характеристики-mongodb)
  - [Преимущества **MongoDB**](#преимущества-mongodb)
  - [Недостатки **MongoDB**](#недостатки-mongodb)
  - [Варианты использования **MongoDB**](#варианты-использования-mongodb)
  - [Сравнение с реляционными базами данных](#сравнение-с-реляционными-базами-данных)
- [Установка и настройка **MongoDB**](#установка-и-настройка-mongodb)
  - [Установка на **Ubuntu**/**Debian**](#установка-на-ubuntudebian)
- [Импорт публичного ключа MongoDB](#импорт-публичного-ключа-mongodb)
- [Создание списка источников](#создание-списка-источников)
- [Обновление пакетов и установка](#обновление-пакетов-и-установка)
- [Запуск MongoDB](#запуск-mongodb)
- [Проверка статуса](#проверка-статуса)
- [Проверка версии](#проверка-версии)
  - [Установка через **Docker**](#установка-через-docker)
- [Запуск MongoDB в Docker](#запуск-mongodb-в-docker)
- [Проверка](#проверка)
  - [Конфигурационный файл **MongoDB**](#конфигурационный-файл-mongodb)
- [/etc/mongod.conf](#etcmongodconf)
- [Основные концепции **MongoDB**](#основные-концепции-mongodb)
  - [Терминология](#терминология)
  - [Документы и **BSON**](#документы-и-bson)
  - [Типы данных **BSON**](#типы-данных-bson)
- [**CRUD** операции](#crud-операции)
  - [**Create** (Вставка)](#create-вставка)
  - [**Read** (Чтение)](#read-чтение)
  - [**Update** (Обновление)](#update-обновление)
  - [**Delete** (Удаление)](#delete-удаление)
- [Запросы и фильтры](#запросы-и-фильтры)
  - [Операторы сравнения](#операторы-сравнения)
  - [Логические операторы](#логические-операторы)
  - [Работа с массивами](#работа-с-массивами)
- [Индексы](#индексы)
  - [Создание индексов](#создание-индексов)
  - [Типы индексов](#типы-индексов)
- [**Aggregation Framework**](#aggregation-framework)
  - [**Pipeline** стадии](#pipeline-стадии)
  - [Операторы агрегации](#операторы-агрегации)
    - [**Accumulators**](#accumulators)
    - [**Expression** операторы](#expression-операторы)
- [Репликация](#репликация)
  - [**Replica Set** архитектура](#replica-set-архитектура)
  - [Настройка **Replica Set**](#настройка-replica-set)
  - [**Read Preferences**](#read-preferences)
- [Шардирование](#шардирование)
  - [**Shard Key**](#shard-key)
  - [Архитектура **Sharded Cluster**](#архитектура-sharded-cluster)
- [Производительность](#производительность)
  - [Оптимизация запросов](#оптимизация-запросов)
  - [**Memory** управление](#memory-управление)
  - [**Connection pooling**](#connection-pooling)
- [Интеграция с **Java**/**Spring**](#интеграция-с-javaspring)
  - [Зависимости **Maven**](#зависимости-maven)
  - [**Spring Boot** конфигурация](#spring-boot-конфигурация)
  - [**Entity** и **Repository**](#entity-и-repository)
  - [**Service** слой](#service-слой)
- [**Best Practices**](#лучшие-практики)
  - [1. Дизайн схемы](#1-дизайн-схемы)
    - [**Embedded** vs **References**](#embedded-vs-references)
  - [2. Индексирование](#2-индексирование)
  - [3. Производительность](#3-производительность)
    - [**Connection management**](#connection-management)
  - [4. Безопасность](#4-безопасность)
  - [5. Мониторинг](#5-мониторинг)
    - [Метрики для мониторинга](#метрики-для-мониторинга)
    - [**Health checks**](#health-checks)
  - [Ключевые преимущества:](#ключевые-преимущества)
  - [Архитектурные возможности:](#архитектурные-возможности)
  - [Когда использовать **MongoDB**:](#когда-использовать-mongodb)
  - [Когда НЕ использовать:](#когда-не-использовать)

## Установка и настройка MongoDB

### Установка на Ubuntu/Debian

```bash
# Импорт публичного ключа MongoDB
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | \
   sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg \
   --dearmor

# Создание списка источников
echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# Обновление пакетов и установка
sudo apt-get update
sudo apt-get install -y mongodb-org

# Запуск MongoDB
sudo systemctl start mongod
sudo systemctl enable mongod

# Проверка статуса
sudo systemctl status mongod

# Проверка версии
mongod --version
```

### Установка через Docker

```bash
# Запуск MongoDB в Docker
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:7.0

# Проверка
docker ps
docker logs mongodb
```

### Конфигурационный файл MongoDB

```yaml
# /etc/mongod.conf
storage:
  dbPath: /var/lib/mongodb
  journal:
    enabled: true

systemLog:
  destination: file
  logAppend: true
  path: /var/log/mongodb/mongod.log

net:
  port: 27017
  bindIp: 127.0.0.1

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

security:
  authorization: enabled

operationProfiling:
  slowOpThresholdMs: 100
  mode: slowOp
```

## Основные концепции MongoDB

### Терминология

| **MongoDB** | Реляционные СУБД |
|---------|-------------------|
| **Database** | **Database** |
| **Collection** | **Table** |
| **Document** | **Row** |
| **Field** | **Column** |
| **Index** | **Index** |

### Документы и BSON

**MongoDB** хранит данные в формате **BSON** (Binary JSON):**

```javascript
{
  _id: ObjectId("507f1f77bcf86cd799439011"),
  name: "John Doe",
  age: 30,
  email: "john@example.com",
  address: {
    street: "123 Main St",
    city: "Anytown",
    zipCode: "12345"
  },
  tags: ["developer", "mongodb"],
  createdAt: ISODate("2023-01-01T00:00:00Z")
}
```

### Типы данных BSON

- **ObjectId**: Уникальный идентификатор
- **String**: Строки текста
- **Integer/Double**: Числа
- **Boolean**: **true**/**false**
- **Date**: Дата и время
- **Array**: Массивы
- **Object**: Вложенные документы
- **Null**: **null** значения

## CRUD операции

### Create (Вставка)

```javascript
// Вставка одного документа
db.users.insertOne({
  name: "John Doe",
  email: "john@example.com",
  age: 30
})

// Вставка нескольких документов
db.users.insertMany([
  { name: "Jane Doe", email: "jane@example.com" },
  { name: "Bob Smith", email: "bob@example.com" }
])
```

### Read (Чтение)

```javascript
// Найти все документы
db.users.find()

// Найти с условием
db.users.find({ age: { $gte: 25 } })

// Найти один документ
db.users.findOne({ email: "john@example.com" })
```

### Update (Обновление)

```javascript
// Обновить один документ
db.users.updateOne(
  { email: "john@example.com" },
  { $set: { age: 31 } }
)

// Обновить несколько документов
db.users.updateMany(
  { age: { $lt: 30 } },
  { $set: { category: "young" } }
)
```

### Delete (Удаление)

```javascript
// Удалить один документ
db.users.deleteOne({ email: "john@example.com" })

// Удалить несколько документов
db.users.deleteMany({ age: { $lt: 18 } })
```

## Запросы и фильтры

### Операторы сравнения

```javascript
// Равенство
db.users.find({ age: 25 })

// Сравнение
db.users.find({ age: { $gt: 25, $lt: 40 } })

// В диапазоне
db.users.find({ age: { $gte: 18, $lte: 65 } })

// Не равно
db.users.find({ status: { $ne: "inactive" } })

// В массиве значений
db.users.find({ role: { $in: ["admin", "moderator"] } })
```

### Логические операторы

```javascript
// И (неявно)
db.users.find({ age: { $gte: 18 }, status: "active" })

// ИЛИ
db.users.find({
  $or: [
    { age: { $lt: 18 } },
    { age: { $gt: 65 } }
  ]
})

// И НЕ
db.users.find({
  age: { $gte: 18 },
  status: { $ne: "banned" }
})

// Complex условия
db.users.find({
  $and: [
    { age: { $gte: 18 } },
    { $or: [
      { role: "admin" },
      { premium: true }
    ]}
  ]
})
```

### Работа с массивами

```javascript
// Документ с массивом
{
  name: "John",
  tags: ["javascript", "mongodb", "nodejs"],
  scores: [85, 92, 78]
}

// Найти по элементу массива
db.users.find({ tags: "mongodb" })

// Найти все документы содержащие элементы
db.users.find({ tags: { $all: ["javascript", "mongodb"] } })

// Найти по размеру массива
db.users.find({ tags: { $size: 3 } })

// Найти с элементом по индексу
db.users.find({ "scores.0": { $gte: 90 } })
```

## Индексы

### Создание индексов

```javascript
// Single field index
db.users.createIndex({ email: 1 })

// Compound index
db.users.createIndex({ name: 1, age: -1 })

// Unique index
db.users.createIndex({ email: 1 }, { unique: true })

// Text index
db.articles.createIndex({ content: "text" })

// Geospatial index
db.places.createIndex({ location: "2dsphere" })
```

### Типы индексов

- **Single Field**: На одном поле
- **Compound**: На нескольких полях
- **Multikey**: На массивах
- **Text**: Полнотекстовый поиск
- **Geospatial**: Географические данные
- **Hashed**: Хэшированные значения

## Aggregation Framework

### Pipeline стадии

```javascript
db.sales.aggregate([
  // Фильтрация
  { $match: { status: "completed" } },

  // Группировка
  { $group: {
    _id: "$category",
    total: { $sum: "$amount" },
    count: { $sum: 1 }
  }},

  // Сортировка
  { $sort: { total: -1 } },

  // Ограничение
  { $limit: 10 }
])
```

### Операторы агрегации

#### Accumulators

```javascript
$group: {
  _id: "$category",
  count: { $sum: 1 },
  total: { $sum: "$amount" },
  average: { $avg: "$amount" },
  min: { $min: "$amount" },
  max: { $max: "$amount" },
  unique: { $addToSet: "$customerId" }
}
```

#### Expression операторы

```javascript
$project: {
  name: 1,
  total: { $multiply: ["$price", "$quantity"] },
  upperName: { $toUpper: "$name" },
  firstTag: { $arrayElemAt: ["$tags", 0] }
}
```

## Репликация

### Replica Set архитектура

```text
Primary Node ──┐
               ├── Secondary Node 1
               ├── Secondary Node 2
               └── Arbiter Node (optional)
```

### Настройка Replica Set

```javascript
// Инициализация
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo1:27017" },
    { _id: 1, host: "mongo2:27017" },
    { _id: 2, host: "mongo3:27017" }
  ]
})

// Проверка статуса
rs.status()
```

### Read Preferences

```javascript
// Чтение с secondary
db.collection.find().readPref("secondary")

// Предпочтительно secondary
db.collection.find().readPref("secondaryPreferred")

// Ближайший сервер
db.collection.find().readPref("nearest")
```

## Шардирование

### Shard Key

```javascript
// Включение шардирования
sh.enableSharding("mydb")

// Создание шардированного кластера
sh.shardCollection("mydb.users", { userId: 1 })

// Проверка распределения
sh.status()
```

### Архитектура Sharded Cluster

```text
Config Servers ──┐
                 ├── Mongos Router
                 └── Shard Servers (Replica Sets)
```

## Производительность

### Оптимизация запросов

```javascript
// Использование индексов
db.users.createIndex({ email: 1 })

// Explain план
db.users.find({ email: "john@example.com" }).explain("executionStats")

// Профилирование медленных запросов
db.setProfilingLevel(1, { slowms: 100 })
db.system.profile.find().sort({ millis: -1 })
```

### Memory управление

```javascript
// Проверка использования памяти
db.serverStatus().mem

// Настройка WiredTiger cache (mongod.conf)
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 4  # 50-80% от RAM
```

### Connection pooling

```javascript
// Настройка пула соединений
// В приложении:
// MongoClientSettings settings = MongoClientSettings.builder()
//     .applyConnectionString(new ConnectionString(uri))
//     .applyToConnectionPoolSettings(builder ->
//         builder.maxSize(20).minSize(5)
//     ).build();
```

## Интеграция с Java/Spring

### Зависимости Maven

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.9.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Spring Boot конфигурация

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mydb
      # Для replica set
      # uri: mongodb://host1:27017,host2:27017,host3:27017/mydb?replicaSet=rs0
```

### Entity и Repository

```java
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private int age;

    // getters and setters
}

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByAgeGreaterThan(int age);

    @Query("{ 'name' : ?0 }")
    List<User> findByName(String name);
}
```

### Service слой

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsersOlderThan(int age) {
        return userRepository.findByAgeGreaterThan(age);
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
```

## Лучшие практики

### 1. Дизайн схемы

#### Embedded vs References
```javascript
// Embedded для часто читаемых вместе данных
{
  userId: ObjectId("..."),
  profile: {
    name: "John",
    email: "john@example.com",
    address: { /* embedded */ }
  }
}

// References для связанных данных
{
  userId: ObjectId("..."),
  orders: [ObjectId("..."), ObjectId("...")]
}
```

### 2. Индексирование

```javascript
// Индексы на поля запросов
db.users.createIndex({ email: 1 }, { unique: true })
db.products.createIndex({ category: 1, price: -1 })

// Compound индексы для сортировки
db.posts.createIndex({ author: 1, createdAt: -1 })

// Удаление неиспользуемых индексов
db.collection.dropIndex("unused_index")
```

### 3. Производительность

#### Оптимизация запросов
```javascript
// Использование projection
db.users.find(
  { age: { $gte: 18 } },
  { name: 1, email: 1 }  // Только нужные поля
)

// Batch операции
db.users.bulkWrite([
  { insertOne: { document: user1 } },
  { updateOne: { filter: { _id: id }, update: { $set: update } } }
])
```

#### Connection management
```java
// Правильное управление соединениями
@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .applyToConnectionPoolSettings(builder ->
                    builder.maxSize(20).minSize(5)
                )
                .build()
        );
    }
}
```

### 4. Безопасность

```javascript
// Включение аутентификации
db.createUser({
  user: "appuser",
  pwd: "securepassword",
  roles: ["readWrite"]
})

// Использование TLS
net:
  ssl:
    mode: requireSSL
    PEMKeyFile: /etc/ssl/mongodb.pem
    CAFile: /etc/ssl/ca.pem
```

### 5. Мониторинг

#### Метрики для мониторинга
```javascript
// Операции
db.serverStatus().opcounters

// Память
db.serverStatus().mem

// Соединения
db.serverStatus().connections

// Профилирование
db.system.profile.find().limit(5)
```

#### Health checks
```javascript
// Проверка доступности
db.adminCommand({ ping: 1 })

// Проверка репликации
rs.status()

// Проверка шардирования
sh.status()
```

**MongoDB** — это мощная и гибкая **NoSQL** база данных, которая отлично подходит для современных приложений, требующих быстрой разработки и масштабируемости.

### Ключевые преимущества:

1. **Гибкая схема данных** — легкая адаптация к изменениям
2. **Высокая производительность** — оптимизированные операции
3. **Горизонтальная масштабируемость** — шардирование
4. **Rich `Query` Language** — мощные возможности запросов
5. **Разработчик-friendly** — **JSON**-подобный формат

### Архитектурные возможности:

- **Документо-ориентированная модель** — естественное хранение данных
- **Aggregation Framework** — сложная аналитика
- **Replica Sets** — высокая доступность
- **Sharding** — распределение данных
- **ACID транзакции** — консистентность данных

### Когда использовать MongoDB:

✅ **Современные веб-приложения** — **JSON API**, микросервисы
✅ **Big `Data` и аналитика** — обработка больших объемов данных
✅ **Content Management** — хранение контента и метаданных
✅ **IoT приложения** — сенсорные данные
✅ **Real-time аналитика** — потоковая обработка
✅ **Прототипирование** — быстрая итерация

### Когда НЕ использовать:

❌ **Сложные транзакции** — если нужны **ACID** между многими таблицами
❌ **Комплексные JOIN** — если требуется много связей
❌ **Строгая схема** — если важна целостность на уровне схемы
❌ **SQL опыт команды** — если команда предпочитает реляционные БД

**MongoDB** продолжает эволюционировать, добавляя новые возможности как **multi-document ACID** транзакции, **time series** коллекции и улучшенную интеграцию с облачными платформами. Это делает **MongoDB** отличным выбором для широкого спектра современных приложений.

**MongoDB** — это не просто база данных, это платформа для создания масштабируемых, высокопроизводительных приложений в современной экосистеме. 🚀

