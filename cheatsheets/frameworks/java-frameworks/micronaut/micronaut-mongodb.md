---
title: "Micronaut: MongoDB Integration - Repositories и Queries"
description: "Полное руководство по интеграции с MongoDB в Micronaut: repositories, queries, aggregation, GridFS и best practices"
tags:
  - micronaut
  - mongodb
  - nosql
  - repositories
  - aggregation
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-data.md"]
next: ["micronaut-data.md", "micronaut-reactive.md"]
updated: "2026-02-11"
related: ["micronaut-data.md", "micronaut-reactive.md"]
---

# Micronaut: MongoDB Integration — Repositories и Queries

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: MongoDB Integration — Repositories и Queries](#micronaut-mongodb-integration-repositories-и-queries)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка MongoDB](#настройка-mongodb)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [MongoDB Repositories](#mongodb-repositories)
  - [Entity Definition](#entity-definition)
  - [Repository Interface](#repository-interface)
- [Custom Queries](#custom-queries)
  - [@Query Annotation](#query-annotation)
- [Aggregation](#aggregation)
  - [Aggregation Pipeline](#aggregation-pipeline)
- [GridFS](#gridfs)
  - [File Storage](#file-storage)
- [Reactive MongoDB](#reactive-mongodb)
  - [Reactive Repository](#reactive-repository)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте индексы](#1-используйте-индексы)
  - [2. Оптимизируйте запросы](#2-оптимизируйте-запросы)
  - [3. Используйте aggregation для сложных запросов](#3-используйте-aggregation-для-сложных-запросов)
- [Transactions](#transactions)
  - [MongoDB Transactions](#mongodb-transactions)
- [Change Streams](#change-streams)
  - [Change Stream Monitoring](#change-stream-monitoring)
- [MongoDB Indexes](#mongodb-indexes)
  - [Index Management](#index-management)
- [MongoDB Bulk Operations](#mongodb-bulk-operations)
  - [Bulk Write](#bulk-write)
- [MongoDB Text Search](#mongodb-text-search)
  - [Text Index and Search](#text-index-and-search)
- [MongoDB Replica Set](#mongodb-replica-set)
  - [Replica Set Configuration](#replica-set-configuration)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет отличную поддержку **MongoDB** через **Micronaut Data MongoDB**. Это позволяет работать с **MongoDB** используя **compile-time query generation** и реактивные репозитории.

### Основные возможности

- **MongoDB Repositories**: **Type-safe** репозитории
- **Query Methods**: Автоматическая генерация запросов
- **Aggregation**: Поддержка **aggregation pipeline**
- **GridFS**: Работа с файлами
- **Reactive Support**: Реактивные операции

## Настройка MongoDB

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.data:micronaut-data-mongodb")
    implementation("org.mongodb:mongodb-driver-sync")
    // или для reactive
    implementation("org.mongodb:mongodb-driver-reactivestreams")
}
```

### Конфигурация

**application.yml:**

```yaml
mongodb:
  uri: mongodb://localhost:27017
  database: mydb
```

## MongoDB Repositories

### Entity Definition

```java
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.mongodb.annotation.MongoRepository;

@MappedEntity("users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private Integer age;

    // Getters and setters
}
```

### Repository Interface

```java
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByEmail(String email);

    List<User> findByAgeGreaterThan(Integer age);

    long countByAge(Integer age);

    void deleteByEmail(String email);
}
```

## Custom Queries

### @Query Annotation

```java
@MongoRepository
public interface UserRepository extends CrudRepository<User, String> {

    @Query("{ 'age': { $gt: :minAge, $lt: :maxAge } }")
    List<User> findByAgeRange(Integer minAge, Integer maxAge);

    @Query("{ 'name': { $regex: :name, $options: 'i' } }")
    List<User> findByNameLike(String name);

    @Query(value = "{ 'age': { $gte: :age } }", sort = "{ 'name': 1 }")
    List<User> findAdultsSorted(Integer age);
}
```

## Aggregation

### Aggregation Pipeline

```java
@MongoRepository
public interface UserRepository extends CrudRepository<User, String> {

    @Aggregation("{ $group: { _id: '$age', count: { $sum: 1 } } }")
    List<AgeGroup> groupByAge();

    @Aggregation(pipeline = {
        "{ $match: { age: { $gte: :minAge } } }",
        "{ $group: { _id: '$age', users: { $push: '$$ROOT' } } }",
        "{ $sort: { _id: 1 } }"
    })
    List<AgeGroupResult> groupUsersByAge(Integer minAge);
}
```

## GridFS

### File Storage

```java
import com.mongodb.client.gridfs.GridFSBucket;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.io.OutputStream;

@Singleton
public class GridFSService {
    private final GridFSBucket gridFSBucket;

    public GridFSService(GridFSBucket gridFSBucket) {
        this.gridFSBucket = gridFSBucket;
    }

    public String storeFile(String filename, InputStream inputStream) {
        ObjectId fileId = gridFSBucket.uploadFromStream(filename, inputStream);
        return fileId.toString();
    }

    public void retrieveFile(String fileId, OutputStream outputStream) {
        gridFSBucket.downloadToStream(new ObjectId(fileId), outputStream);
    }
}
```

## Reactive MongoDB

### Reactive Repository

```java
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.reactive.ReactiveStreamsRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@MongoRepository
public interface ReactiveUserRepository
        extends ReactiveStreamsRepository<User, String> {

    Mono<User> findByEmail(String email);

    Flux<User> findByAgeGreaterThan(Integer age);

    Mono<Long> countByAge(Integer age);
}
```

## Лучшие практики

### 1. Используйте индексы

```java
// ✅ Хорошо
@MappedEntity("users")
@Indexes({
    @Index(fields = @Field("email"), unique = true),
    @Index(fields = @Field("age"))
})
public class User {
    // ...
}
```

### 2. Оптимизируйте запросы

```java
// ✅ Хорошо
@Query(value = "{ 'age': { $gte: :age } }", fields = "{ 'name': 1, 'email': 1 }")
List<User> findAdultsProjection(Integer age);
```

### 3. Используйте aggregation для сложных запросов

```java
// ✅ Хорошо
@Aggregation(pipeline = {
    "{ $match: { age: { $gte: 18 } } }",
    "{ $group: { _id: '$age', count: { $sum: 1 } } }"
})
List<AgeGroup> groupAdultsByAge();
```

## Transactions

### MongoDB Transactions

```java
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

@Singleton
public class TransactionalUserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public User createUserWithOrder(User user, Order order) {
        User savedUser = userRepository.save(user);
        order.setUserId(savedUser.getId());
        orderRepository.save(order);
        return savedUser;
    }
}
```

## Change Streams

### Change Stream Monitoring

```java
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoDatabase;
import jakarta.inject.Singleton;

@Singleton
public class ChangeStreamService {
    private final MongoDatabase mongoDatabase;

    public ChangeStreamService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void watchChanges() {
        ChangeStreamIterable<Document> changeStream =
            mongoDatabase.getCollection("users").watch();

        changeStream.forEach(change -> {
            System.out.println("Change detected: " + change);
            // Обработка изменений
        });
    }
}
```

## MongoDB Indexes

### Index Management

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import jakarta.inject.Singleton;

@Singleton
public class IndexService {
    private final MongoCollection<Document> userCollection;

    public IndexService(MongoCollection<Document> userCollection) {
        this.userCollection = userCollection;
    }

    public void createIndexes() {
        // Создание индекса на поле email
        userCollection.createIndex(Indexes.ascending("email"));

        // Создание составного индекса
        userCollection.createIndex(Indexes.compoundIndex(
            Indexes.ascending("name"),
            Indexes.ascending("age")
        ));

        // Создание текстового индекса
        userCollection.createIndex(Indexes.text("name", "description"));
    }
}
```

## MongoDB Bulk Operations

### Bulk Write

```java
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Filters;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class BulkOperationService {
    private final MongoCollection<User> userCollection;

    public BulkOperationService(MongoCollection<User> userCollection) {
        this.userCollection = userCollection;
    }

    public void bulkInsert(List<User> users) {
        List<InsertOneModel<User>> inserts = users.stream()
            .map(InsertOneModel::new)
            .collect(Collectors.toList());

        userCollection.bulkWrite(inserts, new BulkWriteOptions().ordered(false));
    }

    public void bulkUpdate(List<User> users) {
        List<UpdateOneModel<User>> updates = users.stream()
            .map(user -> new UpdateOneModel<>(
                Filters.eq("_id", user.getId()),
                Updates.set("name", user.getName())
            ))
            .collect(Collectors.toList());

        userCollection.bulkWrite(updates);
    }
}
```

## MongoDB Text Search

### Text Index and Search

```java
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Filters;

@Singleton
public class TextSearchService {
    private final MongoCollection<User> userCollection;

    public TextSearchService(MongoCollection<User> userCollection) {
        this.userCollection = userCollection;
    }

    public void createTextIndex() {
        userCollection.createIndex(Indexes.text("name", "email"));
    }

    public List<User> searchUsers(String searchText) {
        return userCollection.find(Filters.text(searchText))
            .into(new ArrayList<>());
    }
}
```

## MongoDB Replica Set

### Replica Set Configuration

**application.yml:**

```yaml
mongodb:
  uri: mongodb://localhost:27017,localhost:27018,localhost:27019/mydb?replicaSet=rs0
```


## Заключение

**Micronaut MongoDB** предоставляет мощные инструменты для работы с **MongoDB**. Поддержка **repositories**, **custom queries**, **aggregation**, **GridFS**, **reactive operations**, **transactions**, **change streams**, **indexes**, **bulk operations**, **text search**, **replica sets** и других продвинутых возможностей позволяет эффективно работать с **MongoDB** в **Micronaut** приложениях.

## Дополнительные ресурсы

- [**Micronaut Data MongoDB** Documentation](https://micronaut-projects.github.io/micronaut-mongodb/latest/guide/)
- [**MongoDB** Documentation](https://www.mongodb.com/docs/)
- [**MongoDB Java** Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)
- [**MongoDB** Transactions](https://www.mongodb.com/docs/manual/core/transactions/)
- [**MongoDB Change Streams**](https://www.mongodb.com/docs/manual/changeStreams/)
- [**MongoDB** Indexes](https://www.mongodb.com/docs/manual/indexes/)
- [**MongoDB Text Search**](https://www.mongodb.com/docs/manual/text-search/)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
