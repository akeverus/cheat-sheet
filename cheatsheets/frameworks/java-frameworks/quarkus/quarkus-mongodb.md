---
title: "Quarkus: MongoDB - NoSQL Database"
description: "Полное руководство по работе с MongoDB в Quarkus: Panache MongoDB, reactive MongoDB, queries, aggregation и best practices"
tags:
  - quarkus
  - mongodb
  - nosql
  - panache
  - reactive
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-data.md"]
next: ["quarkus-data.md", "quarkus-reactive.md"]
updated: "2026-02-11"
related: ["quarkus-data.md", "quarkus-reactive.md"]
---

# Quarkus: MongoDB — NoSQL Database

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Quarkus: MongoDB — NoSQL Database](#quarkus-mongodb-nosql-database)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Configuration](#configuration)
  - [MongoDB Configuration](#mongodb-configuration)
  - [Advanced Configuration](#advanced-configuration)
- [Panache MongoDB](#panache-mongodb)
  - [Entity Definition](#entity-definition)
  - [Basic Operations](#basic-operations)
- [Queries](#queries)
  - [Query Methods](#query-methods)
  - [Native Queries](#native-queries)
- [Reactive MongoDB](#reactive-mongodb)
  - [Reactive Entity](#reactive-entity)
  - [Reactive Operations](#reactive-operations)
- [Aggregation](#aggregation)
  - [Aggregation Pipeline](#aggregation-pipeline)
- [Best Practices](#best-practices)
  - [1. Используйте Panache для упрощения](#1-используйте-panache-для-упрощения)
  - [2. Используйте reactive для неблокирующих операций](#2-используйте-reactive-для-неблокирующих-операций)
  - [3. Индексируйте часто используемые поля](#3-индексируйте-часто-используемые-поля)
- [Indexes](#indexes)
  - [Creating Indexes](#creating-indexes)
  - [Text Indexes](#text-indexes)
- [Transactions](#transactions)
  - [Transaction Support](#transaction-support)
- [Change Streams](#change-streams)
  - [Watching Changes](#watching-changes)
- [GridFS](#gridfs)
  - [File Storage](#file-storage)
  - [4. Используйте aggregation для сложных запросов](#4-используйте-aggregation-для-сложных-запросов)
  - [5. Используйте change streams для real-time обновлений](#5-используйте-change-streams-для-real-time-обновлений)
- [MongoDB Performance Optimization](#mongodb-performance-optimization)
  - [Query Optimization](#query-optimization)
  - [Index Management](#index-management)
  - [Connection Pooling](#connection-pooling)
- [Advanced MongoDB Patterns](#advanced-mongodb-patterns)
  - [Document Versioning](#document-versioning)
  - [Soft Deletes](#soft-deletes)
- [MongoDB Change Streams](#mongodb-change-streams)
  - [Real-time Change Monitoring](#real-time-change-monitoring)
  - [Change Stream Filters](#change-stream-filters)
- [MongoDB GridFS](#mongodb-gridfs)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет мощную интеграцию с **MongoDB** через **Panache MongoDB** и **reactive MongoDB**. Это позволяет эффективно работать с **NoSQL** базой данных в реактивном и императивном стилях.

### Основные возможности

- **Panache MongoDB**: Упрощенный **API** для **MongoDB**
- **Reactive MongoDB**: Реактивный доступ к **MongoDB**
- **Queries**: Гибкие запросы
- **Aggregation**: Агрегационные пайплайны

## Configuration

### MongoDB Configuration

**application.properties:**

```properties
quarkus.mongodb.connection-string=mongodb://localhost:27017
quarkus.mongodb.database=myapp
```

### Advanced Configuration

```properties
quarkus.mongodb.connection-string=mongodb://user:password@host1:27017,host2:27017/myapp?replicaSet=rs0
quarkus.mongodb.database=myapp
quarkus.mongodb.truststore.path=/path/to/truststore
quarkus.mongodb.truststore.password=password
```

## Panache MongoDB

### Entity Definition

**Определение сущности:**

```java
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

public class User extends PanacheMongoEntity {
    public String name;
    public String email;
    public Integer age;
}
```

### Basic Operations

**Базовые операции:**

```java
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    public User createUser(User user) {
        user.persist();
        return user;
    }

    public User findById(ObjectId id) {
        return User.findById(id);
    }

    public List<User> findAll() {
        return User.listAll();
    }

    public void deleteUser(ObjectId id) {
        User.deleteById(id);
    }
}
```

## Queries

### Query Methods

**Методы запросов:**

```java
@ApplicationScoped
public class UserRepository {

    public List<User> findByName(String name) {
        return User.find("name", name).list();
    }

    public Optional<User> findByEmail(String email) {
        return User.find("email", email).firstResultOptional();
    }

    public List<User> findByAgeRange(Integer minAge, Integer maxAge) {
        return User.find("age >= ?1 and age <= ?2", minAge, maxAge).list();
    }
}
```

### Native Queries

**Нативные запросы:**

```java
@ApplicationScoped
public class NativeQueryRepository {

    public List<User> findActiveUsers() {
        return User.find("{ status: 'ACTIVE' }").list();
    }

    public List<User> findUsersWithOrders() {
        return User.find("{ orders: { $exists: true, $ne: [] } }").list();
    }
}
```

## Reactive MongoDB

### Reactive Entity

**Реактивная сущность:**

```java
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;

public class ReactiveUser extends ReactivePanacheMongoEntity {
    public String name;
    public String email;

    public static Uni<ReactiveUser> findByNameReactive(String name) {
        return find("name", name).firstResult();
    }

    public static Multi<ReactiveUser> findAllReactive() {
        return streamAll();
    }
}
```

### Reactive Operations

**Реактивные операции:**

```java
@ApplicationScoped
public class ReactiveUserService {

    public Uni<ReactiveUser> createUser(ReactiveUser user) {
        return user.persist();
    }

    public Uni<ReactiveUser> findById(ObjectId id) {
        return ReactiveUser.findById(id);
    }

    public Multi<ReactiveUser> findAll() {
        return ReactiveUser.streamAll();
    }
}
```

## Aggregation

### Aggregation Pipeline

**Агрегационный пайплайн:**

```java
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AggregationService {

    public List<Document> aggregateUsers() {
        return User.mongoCollection().aggregate(
            Arrays.asList(
                Aggregates.match(Filters.eq("status", "ACTIVE")),
                Aggregates.group("$department", Accumulators.sum("count", 1))
            )
        ).into(new ArrayList<>());
    }
}
```

## Лучшие практики
### 1. Используйте Panache для упрощения

```java
// ✅ Хорошо
public class User extends PanacheMongoEntity {
    // Упрощенный API
}
```

### 2. Используйте reactive для неблокирующих операций

```java
// ✅ Хорошо
public Uni<ReactiveUser> findById(ObjectId id) {
    return ReactiveUser.findById(id);
}
```

### 3. Индексируйте часто используемые поля

```java
// ✅ Хорошо
@Indexed
public String email;
```

## Indexes

### Creating Indexes

**Создание индексов:**

```java
import org.bson.Document;
import com.mongodb.client.model.Indexes;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IndexService {

    public void createIndexes() {
        User.mongoCollection().createIndex(Indexes.ascending("email"));
        User.mongoCollection().createIndex(Indexes.compoundIndex(
            Indexes.ascending("name"),
            Indexes.ascending("age")
        ));
    }
}
```

### Text Indexes

**Текстовые индексы:**

```java
@ApplicationScoped
public class TextIndexService {

    public void createTextIndex() {
        User.mongoCollection().createIndex(Indexes.text("name", "description"));
    }

    public List<User> searchText(String query) {
        return User.find("{ $text: { $search: ?1 } }", query).list();
    }
}
```

## Transactions

### Transaction Support

**Поддержка транзакций:**

```java
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionalService {

    @Transactional
    public void createUserWithOrders(User user, List<Order> orders) {
        user.persist();
        orders.forEach(order -> {
            order.userId = user.id;
            order.persist();
        });
    }
}
```

## Change Streams

### Watching Changes

**Отслеживание изменений:**

```java
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChangeStreamService {

    public void watchChanges() {
        User.mongoCollection().watch().forEach(change -> {
            ChangeStreamDocument<Document> document = change;
            System.out.println("Change type: " + change.getOperationType());
            System.out.println("Document: " + change.getFullDocument());
        });
    }
}
```

## GridFS

### File Storage

**Хранение файлов:**

```java
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GridFSService {

    @Inject
    MongoClient mongoClient;

    public void storeFile(String filename, InputStream inputStream) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(
            mongoClient.getDatabase("myapp"), "files");
        gridFSBucket.uploadFromStream(filename, inputStream);
    }

    public void retrieveFile(String filename, OutputStream outputStream) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(
            mongoClient.getDatabase("myapp"), "files");
        gridFSBucket.downloadToStream(filename, outputStream);
    }
}
```

## MongoDB Performance Optimization

### Query Optimization

**Оптимизация запросов:**

```java
@ApplicationScoped
public class OptimizedQueries {

    public List<User> findUsersOptimized(String name) {
        // Использование индексов
        return User.find("name", name)
            .with(Sort.by("createdAt").descending())
            .page(0, 20)
            .list();
    }
}
```

### Index Management

**Управление индексами:**

```java
@ApplicationScoped
public class IndexManagement {

    @PostConstruct
    public void createIndexes() {
        User.mongoCollection().createIndex(
            Indexes.ascending("email"),
            new IndexOptions().unique(true)
        );
    }
}
```

### Connection Pooling

**Настройка пула соединений:**

```properties
quarkus.mongodb.connection-string=mongodb://localhost:27017
quarkus.mongodb.max-pool-size=50
quarkus.mongodb.min-pool-size=10
```

## Advanced MongoDB Patterns

### Document Versioning

**Версионирование документов:**

```java
@MongoEntity(collection = "users")
public class VersionedUser extends PanacheMongoEntity {
    public String name;
    public String email;
    public Integer version = 1;

    public void incrementVersion() {
        this.version++;
    }
}
```

### Soft Deletes

**Мягкое удаление:**

```java
@MongoEntity(collection = "users")
public class SoftDeletableUser extends PanacheMongoEntity {
    public String name;
    public Boolean deleted = false;
    public LocalDateTime deletedAt;

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        persist();
    }

    public static List<SoftDeletableUser> findActive() {
        return find("deleted", false).list();
    }
}
```

## MongoDB Change Streams

### Real-time Change Monitoring

**Мониторинг изменений в реальном времени:**

```java
@ApplicationScoped
public class ChangeStreamService {

    public void watchChanges() {
        User.mongoCollection().watch()
            .forEach(changeDocument -> {
                // Обработка изменений
                handleChange(changeDocument);
            });
    }
}
```

### Change Stream Filters

**Фильтры для **change streams**:**

```java
@ApplicationScoped
public class FilteredChangeStream {

    public void watchFilteredChanges() {
        List<Bson> pipeline = Arrays.asList(
            Filters.eq("operationType", "insert"),
            Filters.eq("fullDocument.status", "active")
        );

        User.mongoCollection().watch(pipeline)
            .forEach(this::handleChange);
    }
}
```

## MongoDB GridFS

### File Storage

**Хранение файлов:**

```java
@ApplicationScoped
public class GridFSService {

    @Inject
    GridFSBucket gridFSBucket;

    public ObjectId storeFile(String filename, InputStream inputStream) {
        return gridFSBucket.uploadFromStream(filename, inputStream);
    }

    public void retrieveFile(ObjectId fileId, OutputStream outputStream) {
        gridFSBucket.downloadToStream(fileId, outputStream);
    }
}
```


## Заключение

**Quarkus MongoDB** предоставляет мощные инструменты для работы с **MongoDB**. Поддержка **Panache MongoDB**, **reactive MongoDB**, **queries**, **aggregation**, **indexes**, **transactions**, **change streams**, **GridFS** и других продвинутых возможностей позволяет эффективно работать с **NoSQL** базой данных. Правильное использование **MongoDB** паттернов, индексация, транзакции и оптимизация запросов являются ключевыми аспектами создания эффективных приложений с **MongoDB**.

## Дополнительные ресурсы

- [**Quarkus MongoDB** Guide](https://quarkus.io/guides/mongodb)
- [**MongoDB** Documentation](https://www.mongodb.com/docs/)
- [**MongoDB Java** Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
