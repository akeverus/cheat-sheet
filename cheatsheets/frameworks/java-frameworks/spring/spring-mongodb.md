---
title: "Spring Data MongoDB: Полное руководство"
description: "Комплексное руководство по Spring Data MongoDB: repositories, queries, aggregation, transactions, GridFS и best practices"
tags:
  - spring
  - mongodb
  - nosql
  - document-database
  - repositories
  - java
difficulty: "intermediate"
prerequisites: ["spring/spring-boot.md", "spring/spring-data-jpa.md"]
next: ["databases/mongodb.md", "spring/spring-data-jpa.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "spring/spring-data-jpa.md", "databases/mongodb.md"]
---

# Spring Data MongoDB: Полное руководство

## Полезные ссылки

[Официальная документация Spring](https://docs.spring.io/)
[Spring Projects](https://spring.io/projects)

## Содержание

- [Введение в Spring Data MongoDB](#введение-в-spring-data-mongodb)
  - [Основные возможности](#основные-возможности)
  - [Архитектура Spring Data MongoDB](#архитектура-spring-data-mongodb)
- [Настройка Spring Data MongoDB](#настройка-spring-data-mongodb)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [MongoDB Configuration](#mongodb-configuration)
  - [Java Configuration](#java-configuration)
- [Document Entity](#document-entity)
  - [Базовый Document](#базовый-document)
  - [Вложенные документы](#вложенные-документы)
- [MongoRepository](#mongorepository)
  - [Базовое использование](#базовое-использование)
  - [Custom Queries](#custom-queries)
  - [Queries с сортировкой и пагинацией](#queries-с-сортировкой-и-пагинацией)
- [MongoTemplate](#mongotemplate)
  - [Базовые операции](#базовые-операции)
  - [Query Building](#query-building)
  - [Projection](#projection)
- [Aggregation](#aggregation)
  - [Базовые агрегации](#базовые-агрегации)
  - [Сложные агрегации](#сложные-агрегации)
- [Transactions](#transactions)
  - [Транзакционные операции](#транзакционные-операции)
- [GridFS](#gridfs)
  - [Загрузка файлов](#загрузка-файлов)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте индексы](#1-используйте-индексы)
  - [2. Используйте проекции для больших документов](#2-используйте-проекции-для-больших-документов)
  - [3. Используйте пагинацию для больших результатов](#3-используйте-пагинацию-для-больших-результатов)
  - [4. Обрабатывайте ошибки](#4-обрабатывайте-ошибки)
  - [5. Используйте транзакции для критических операций](#5-используйте-транзакции-для-критических-операций)
- [Change Streams](#change-streams)
  - [Подписка на изменения](#подписка-на-изменения)
  - [Фильтрация Change Streams](#фильтрация-change-streams)
- [Продвинутые агрегации](#продвинутые-агрегации)
  - [Lookup Aggregation](#lookup-aggregation)
  - [Facet Aggregation](#facet-aggregation)
- [Текстовый поиск](#текстовый-поиск)
  - [Text Index](#text-index)
- [Мониторинг и метрики](#мониторинг-и-метрики)
  - [MongoDB Metrics](#mongodb-metrics)
  - [Health Check](#health-check)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Connection Pooling](#connection-pooling)
- [MongoDB Connection Pool Configuration](#mongodb-connection-pool-configuration)
  - [Read Preferences](#read-preferences)
  - [Write Concerns](#write-concerns)
- [Безопасность](#безопасность)
  - [MongoDB Authentication](#mongodb-authentication)
  - [SSL/TLS Configuration](#ssltls-configuration)
- [Тестирование](#тестирование)
  - [Embedded MongoDB](#embedded-mongodb)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Spring Data MongoDB**

**Spring Data MongoDB** предоставляет простую интеграцию с **MongoDB** для работы с документ-ориентированной базой данных. Он поддерживает репозитории, автоматические запросы, агрегации и другие возможности **MongoDB**.

### Основные возможности

- **MongoRepository**: **Spring Data** репозитории для **MongoDB**
- **MongoTemplate**: Низкоуровневый **API** для работы с **MongoDB**
- **Query Methods**: Автоматическая генерация запросов
- **Aggregation**: Поддержка агрегационных **pipeline**
- **GridFS**: Работа с файлами через **GridFS**

### Архитектура **Spring Data MongoDB**

```text
┌─────────────────────────────────────────────────────────┐
│              Application Code                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Mongo      │  │   Mongo      │  │   GridFS     │  │
│  │   Repository │  │   Template   │  │   Template   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              MongoDB Driver                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Sync       │  │   Reactive   │  │   GridFS      │  │
│  │   Driver     │  │   Driver     │  │   API         │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              MongoDB Server                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Collections│  │   Documents  │  │   Indexes    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Настройка **Spring Data MongoDB**

### Зависимости

**Зависимость **spring-`boot-starter-data`-mongodb** (**pom.xml**):**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Конфигурация

**application.properties:**

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/mydb
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=mydb
spring.data.mongodb.username=user
spring.data.mongodb.password=password
spring.data.mongodb.authentication-database=admin
```

### **Java Configuration**

```java
// Включение репозиториев MongoDB и сканирование пакета
@Configuration
@EnableMongoRepositories(basePackages = "com.example.repository")
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), "mydb");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }
}
```

## Document Entity

### Базовый **Document**

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field("user_name")
    private String name;

    private String email;

    private Integer age;

    @Indexed
    private String phoneNumber;

    private Address address;

    private List<Order> orders;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Getters and setters...
}
```

### Вложенные документы

```java
// Встраиваемый документ MongoDB (@Document)
@Document
public class Address {
    private String street;
    private String city;
    private String zipCode;
    private String country;
}

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private Address address; // Вложенный документ
    private List<Address> addresses; // Массив вложенных документов
}
```

## MongoRepository

### Базовое использование

```java
// Spring Data MongoDB репозиторий для User
public interface UserRepository extends MongoRepository<User, String> {

    // Автоматически генерируется: db.users.find({name: name})
    List<User> findByName(String name);

    // Автоматически генерируется: db.users.find({email: email})
    Optional<User> findByEmail(String email);

    // Автоматически генерируется: db.users.find({age: {$gt: age}})
    List<User> findByAgeGreaterThan(Integer age);

    // Автоматически генерируется: db.users.find({age: {$gte: minAge, $lte: maxAge}})
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    // Автоматически генерируется: db.users.find({name: {$regex: name, $options: 'i'}})
    List<User> findByNameLike(String name);

    // COUNT query
    long countByAgeGreaterThan(Integer age);

    // EXISTS query
    boolean existsByEmail(String email);

    // DELETE query
    void deleteByEmail(String email);
}
```

### **Custom Queries**

```java
// Spring Data MongoDB репозиторий для User
public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'name' : ?0 }")
    List<User> findByNameCustom(String name);

    @Query("{ 'age' : { $gt: ?0, $lt: ?1 } }")
    List<User> findByAgeRange(Integer minAge, Integer maxAge);

    @Query("{ 'email' : { $regex: ?0, $options: 'i' } }")
    List<User> findByEmailRegex(String emailPattern);

    @Query(value = "{ 'age' : { $gt: ?0 } }", fields = "{ 'name' : 1, 'email' : 1 }")
    List<User> findAdultsProjection(Integer minAge);
}
```

### **Queries** с сортировкой и пагинацией

```java
// Spring Data MongoDB репозиторий для User
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByNameOrderByAgeDesc(String name);

    Page<User> findByAgeGreaterThan(Integer age, Pageable pageable);

    Slice<User> findByName(String name, Pageable pageable);
}
```

## MongoTemplate

### Базовые операции

```java
@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public User findById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    public List<User> findAll() {
        return mongoTemplate.findAll(User.class);
    }

    public void delete(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class);
    }
}
```

### **Query Building**

```java
@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> findByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.find(query, User.class);
    }

    public List<User> findByAgeRange(Integer minAge, Integer maxAge) {
        Query query = new Query(
            Criteria.where("age").gte(minAge).lte(maxAge)
        );
        return mongoTemplate.find(query, User.class);
    }

    public List<User> findByNameAndAge(String name, Integer age) {
        Query query = new Query(
            Criteria.where("name").is(name)
                .and("age").is(age)
        );
        return mongoTemplate.find(query, User.class);
    }

    public List<User> findWithPagination(int page, int size) {
        Query query = new Query();
        query.with(PageRequest.of(page, size));
        return mongoTemplate.find(query, User.class);
    }

    public List<User> findWithSorting(String sortBy, Sort.Direction direction) {
        Query query = new Query();
        query.with(Sort.by(direction, sortBy));
        return mongoTemplate.find(query, User.class);
    }
}
```

### **Projection**

```java
@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> findNamesOnly() {
        Query query = new Query();
        query.fields().include("name").exclude("id");
        return mongoTemplate.find(query, User.class);
    }
}
```

## Aggregation

### Базовые агрегации

```java
@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<AgeGroup> groupByAge() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("age").count().as("count"),
            Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<AgeGroup> results = mongoTemplate.aggregate(
            aggregation, "users", AgeGroup.class);
        return results.getMappedResults();
    }

    public List<UserStats> getUserStats() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group()
                .avg("age").as("avgAge")
                .max("age").as("maxAge")
                .min("age").as("minAge")
                .sum("age").as("totalAge")
        );

        AggregationResults<UserStats> results = mongoTemplate.aggregate(
            aggregation, "users", UserStats.class);
        return results.getMappedResults();
    }
}
```

### Сложные агрегации

```java
@Service
public class OrderService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<OrderSummary> getOrderSummary() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is("COMPLETED")),
            Aggregation.group("userId")
                .sum("total").as("totalAmount")
                .count().as("orderCount"),
            Aggregation.lookup("users", "userId", "_id", "user"),
            Aggregation.unwind("user"),
            Aggregation.project()
                .and("user.name").as("userName")
                .and("totalAmount").as("totalAmount")
                .and("orderCount").as("orderCount"),
            Aggregation.sort(Sort.Direction.DESC, "totalAmount")
        );

        AggregationResults<OrderSummary> results = mongoTemplate.aggregate(
            aggregation, "orders", OrderSummary.class);
        return results.getMappedResults();
    }
}
```

## Transactions

### Транзакционные операции

```java
@Service
@Transactional
public class TransactionalUserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void transferBalance(String fromUserId, String toUserId, BigDecimal amount) {
        Query fromQuery = new Query(Criteria.where("id").is(fromUserId));
        Update fromUpdate = new Update().inc("balance", amount.negate());
        mongoTemplate.updateFirst(fromQuery, fromUpdate, User.class);

        Query toQuery = new Query(Criteria.where("id").is(toUserId));
        Update toUpdate = new Update().inc("balance", amount);
        mongoTemplate.updateFirst(toQuery, toUpdate, User.class);
    }
}
```

## GridFS

### Загрузка файлов

```java
@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String uploadFile(String filename, InputStream inputStream) {
        ObjectId fileId = gridFsTemplate.store(inputStream, filename, "application/pdf");
        return fileId.toString();
    }

    public GridFsResource getFile(String fileId) {
        GridFsResource[] resources = gridFsTemplate.getResources(
            new Query(Criteria.where("_id").is(new ObjectId(fileId))));
        return resources.length > 0 ? resources[0] : null;
    }

    public void deleteFile(String fileId) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
    }
}
```

## Лучшие практики

### 1. Используйте индексы

```java
// ✅ Хорошо
@Document(collection = "users")
@CompoundIndex(name = "email_age_idx", def = "{'email': 1, 'age': 1}")
public class User {
    @Indexed
    private String email;
}
```

### 2. Используйте проекции для больших документов

```java
// ✅ Хорошо
query.fields().include("name").exclude("orders");
```

### 3. Используйте пагинацию для больших результатов

```java
// ✅ Хорошо
Page<User> users = userRepository.findAll(PageRequest.of(0, 20));
```

### 4. Обрабатывайте ошибки

```java
// ✅ Хорошо
try {
    userRepository.save(user);
} catch (DuplicateKeyException e) {
    // Обработка дубликата
}
```

### 5. Используйте транзакции для критических операций

```java
// ✅ Хорошо
@Transactional
public void transferBalance(String from, String to, BigDecimal amount) {
    // Транзакционные операции
}
```

## Change Streams

### Подписка на изменения

```java
@Service
public class ChangeStreamService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void watchCollection(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        collection.watch().forEach(changeDocument -> {
            ChangeStreamDocument<Document> change = ChangeStreamDocument.create(changeDocument);

            switch (change.getOperationType()) {
                case INSERT:
                    handleInsert(change);
                    break;
                case UPDATE:
                    handleUpdate(change);
                    break;
                case DELETE:
                    handleDelete(change);
                    break;
                case REPLACE:
                    handleReplace(change);
                    break;
            }
        });
    }

    private void handleInsert(ChangeStreamDocument<Document> change) {
        Document fullDocument = change.getFullDocument();
        log.info("Document inserted: {}", fullDocument);
    }

    private void handleUpdate(ChangeStreamDocument<Document> change) {
        Document updateDescription = change.getUpdateDescription();
        log.info("Document updated: {}", updateDescription);
    }

    private void handleDelete(ChangeStreamDocument<Document> change) {
        BsonDocument documentKey = change.getDocumentKey();
        log.info("Document deleted: {}", documentKey);
    }

    private void handleReplace(ChangeStreamDocument<Document> change) {
        Document fullDocument = change.getFullDocument();
        log.info("Document replaced: {}", fullDocument);
    }
}
```

### Фильтрация **Change Streams**

```java
@Service
public class FilteredChangeStreamService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void watchWithFilter(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        List<Bson> pipeline = Arrays.asList(
            Aggregates.match(
                Filters.in("operationType", Arrays.asList("insert", "update"))
            ),
            Aggregates.match(
                Filters.eq("fullDocument.status", "active")
            )
        );

        collection.watch(pipeline).forEach(changeDocument -> {
            // Обработка отфильтрованных изменений
            processChange(changeDocument);
        });
    }

    private void processChange(BsonDocument changeDocument) {
        // Логика обработки
    }
}
```

## Продвинутые агрегации

### **Lookup Aggregation**

```java
@Service
public class LookupAggregationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Document> joinCollections() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
            .from("orders")
            .localField("_id")
            .foreignField("userId")
            .as("orders");

        Aggregation aggregation = Aggregation.newAggregation(
            lookupOperation,
            Aggregation.match(Criteria.where("orders").not().size(0))
        );

        return mongoTemplate.aggregate(aggregation, "users", Document.class)
            .getMappedResults();
    }
}
```

### **Facet Aggregation**

```java
@Service
public class FacetAggregationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, List<Document>> facetAggregation() {
        FacetOperation facetOperation = Aggregation.facet()
            .and(
                Aggregation.match(Criteria.where("age").gte(18)),
                Aggregation.group("status").count().as("count")
            ).as("adults")
            .and(
                Aggregation.match(Criteria.where("age").lt(18)),
                Aggregation.group("status").count().as("count")
            ).as("minors");

        Aggregation aggregation = Aggregation.newAggregation(facetOperation);

        AggregationResults<Document> results = mongoTemplate.aggregate(
            aggregation, "users", Document.class
        );

        return results.getUniqueMappedResult();
    }
}
```

## Текстовый поиск

### **Text Index**

```java
@Document(collection = "articles")
@TextIndexed
public class Article {
    @Id
    private String id;

    @TextIndexed(weight = 2)
    private String title;

    @TextIndexed
    private String content;
}

@Service
public class TextSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Article> searchArticles(String searchText) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
            .matchingAny(searchText);

        Query query = TextQuery.queryText(criteria)
            .sortByScore();

        return mongoTemplate.find(query, Article.class);
    }
}
```

## Мониторинг и метрики

### **MongoDB Metrics**

```java
@Component
public class MongoMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter queriesExecuted;
    private final Timer queryExecutionTime;

    public MongoMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.queriesExecuted = Counter.builder("mongodb.queries.executed")
            .description("Number of MongoDB queries executed")
            .register(meterRegistry);
        this.queryExecutionTime = Timer.builder("mongodb.query.execution.time")
            .description("MongoDB query execution time")
            .register(meterRegistry);
    }

    public <T> T measureQuery(String collection, Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = supplier.get();
            queriesExecuted.increment(Tags.of("collection", collection));
            return result;
        } finally {
            sample.stop(queryExecutionTime);
        }
    }
}
```

### **Health Check**

```java
@Component
public class MongoHealthIndicator implements HealthIndicator {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Health health() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            return Health.up()
                .withDetail("database", "MongoDB")
                .withDetail("status", "Connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## Оптимизация производительности

### **Connection Pooling**

```properties
# MongoDB Connection Pool Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/mydb
spring.data.mongodb.options.max-pool-size=50
spring.data.mongodb.options.min-pool-size=10
spring.data.mongodb.options.max-connection-idle-time=60000
```

### **Read Preferences**

```java
@Configuration
public class MongoReadPreferenceConfig {

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017/mydb"))
            .readPreference(ReadPreference.secondaryPreferred())
            .build();

        return MongoClients.create(settings);
    }
}
```

### **Write Concerns**

```java
@Service
public class WriteConcernService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveWithWriteConcern(User user) {
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
        mongoTemplate.save(user);
    }

    public void saveWithAcknowledgedWriteConcern(User user) {
        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        mongoTemplate.save(user);
    }
}
```

## Безопасность

### **MongoDB Authentication**

```properties
# MongoDB Authentication
spring.data.mongodb.uri=mongodb://username:password@localhost:27017/mydb?authSource=admin
```

### **SSL**/**TLS Configuration**

```java
@Configuration
public class SecureMongoConfig {

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017/mydb"))
            .applyToSslSettings(builder -> builder
                .enabled(true)
                .invalidHostNameAllowed(false)
            )
            .build();

        return MongoClients.create(settings);
    }
}
```

## Тестирование

### **Embedded MongoDB**

```xml
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@SpringBootTest
@AutoConfigureDataMongo
class MongoIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }
}
```


## Заключение

**Spring Data MongoDB** предоставляет мощные инструменты для работы с **MongoDB**. Правильное использование репозиториев, **MongoTemplate**, агрегаций, транзакций, **Change Streams**, текстового поиска, мониторинга, оптимизации производительности и других продвинутых возможностей позволяет создавать эффективные, масштабируемые приложения с документ-ориентированной базой данных.

## Дополнительные ресурсы

- [**Spring Data MongoDB** Documentation](https://docs.spring.io/spring-data/mongodb/reference/)
- [**MongoDB** Documentation](https://www.mongodb.com/docs/)
- [**Spring Boot** MongoDB](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.nosql.mongodb)
- [**MongoDB Aggregation Pipeline**](https://www.mongodb.com/docs/manual/core/aggregation-pipeline/)
- [**MongoDB Change Streams**](https://www.mongodb.com/docs/manual/changeStreams/)

## См. также

- [[spring-actuator|Spring Actuator: Полное руководство по мониторингу и управлению]]
- [[spring-ai|Spring AI]]
- [[spring-aop|Spring AOP: Полное руководство по аспектно-ориентированному программированию]]
- [[spring-batch|Spring Batch для Java]]
- [[spring-boot|Spring Boot — Полное руководство]]
