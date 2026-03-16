---
title: "Micronaut: Data Access - JDBC, JPA и Repositories"
description: "Полное руководство по работе с базами данных в Micronaut: JDBC, JPA, Micronaut Data, транзакции и миграции"
tags: ["micronaut", "database", "jdbc", "jpa", "data", "repositories", "java", "kotlin"]
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-security.md", "micronaut-reactive.md"]
updated: "2026-02-11"
related: ["micronaut-http.md", "micronaut-testing.md"]
---

# Micronaut: Data Access — JDBC, JPA и Repositories



## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Data Access - JDBC, JPA и Repositories](#micronaut-data-access-jdbc-jpa-и-repositories)
- [Введение](#введение)
  - [Доступные опции](#доступные-опции)
- [Micronaut Data JDBC](#micronaut-data-jdbc)
  - [Настройка](#настройка)
  - [Entity Classes](#entity-classes)
  - [Repository Interface](#repository-interface)
  - [Использование Repository](#использование-repository)
  - [Custom Queries](#custom-queries)
  - [Join Queries](#join-queries)
- [Micronaut Data JPA](#micronaut-data-jpa)
  - [JPA Entity](#jpa-entity)
  - [JPA Repository](#jpa-repository)
- [Transactions](#transactions)
  - [Declarative Transactions](#declarative-transactions)
  - [Programmatic Transactions](#programmatic-transactions)
- [Migrations](#migrations)
  - [Flyway](#flyway)
  - [Liquibase](#liquibase)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте Repository Pattern](#1-используйте-repository-pattern)
  - [2. Используйте Transactions для Multi-step Operations](#2-используйте-transactions-для-multi-step-operations)
  - [3. Используйте @Query для Complex Queries](#3-используйте-query-для-complex-queries)
  - [4. Используйте Migrations для Schema Management](#4-используйте-migrations-для-schema-management)
  - [5. Оптимизируйте Queries](#5-оптимизируйте-queries)
- [Batch Operations](#batch-operations)
  - [Batch Insert](#batch-insert)
  - [Batch Update](#batch-update)
- [Stored Procedures](#stored-procedures)
  - [Вызов Stored Procedures](#вызов-stored-procedures)
- [Projections](#projections)
  - [DTO Projections](#dto-projections)
- [Async Operations](#async-operations)
  - [Async Repository](#async-repository)
  - [Reactive Repository](#reactive-repository)
- [Custom SQL](#custom-sql)
  - [Native Queries](#native-queries)
  - [Dynamic Queries](#dynamic-queries)
- [Query Optimization](#query-optimization)
  - [Query Hints](#query-hints)
  - [Pagination](#pagination)
- [Database Connection Pooling](#database-connection-pooling)
  - [HikariCP Configuration](#hikaricp-configuration)
- [Database Migrations](#database-migrations)
  - [Flyway Migrations](#flyway-migrations)
  - [Liquibase Migrations](#liquibase-migrations)
- [Query Result Mapping](#query-result-mapping)
  - [Custom Result Mappers](#custom-result-mappers)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет несколько способов работы с базами данных: от низкоуровневого **JDBC** до высокоуровневых репозиториев с **compile-time query generation**. Все это работает с **compile-time** обработкой, обеспечивая высокую производительность.

### Доступные опции

1. **Micronaut Data JDBC**: **Type-safe** репозитории с **compile-time query generation**
2. **Micronaut Data JPA**: **JPA** репозитории с **compile-time** обработкой
3. **Hibernate/JPA**: Стандартный **JPA** с **Hibernate**
4. **JDBC**: Низкоуровневый доступ к БД
5. **R2DBC**: **Reactive database access**

## Micronaut Data JDBC

### Настройка

**build.gradle:**

```gradle
dependencies {
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("com.h2database:h2")
}
```

**application.yml:**

```yaml
datasources:
  default:
    url: jdbc:h2:mem:devDb
    driverClassName: org.h2.Driver
    username: sa
    password: 
    schema-generate: CREATE_DROP
    dialect: H2

micronaut:
  data:
    jdbc:
      repositories:
        enabled: true
```

### Entity Classes

```java
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.GeneratedValue;

@MappedEntity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private String email;
    private Integer age;
    
    // Constructors, getters, setters
    public User() {}
    
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
```

### Repository Interface

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    // Автоматически генерируется: SELECT * FROM user WHERE name = ?
    Optional<User> findByName(String name);
    
    // Автоматически генерируется: SELECT * FROM user WHERE email = ?
    Optional<User> findByEmail(String email);
    
    // Автоматически генерируется: SELECT * FROM user WHERE age > ?
    List<User> findByAgeGreaterThan(Integer age);
    
    // Автоматически генерируется: SELECT * FROM user WHERE name LIKE ?
    List<User> findByNameLike(String name);
    
    // Автоматически генерируется: SELECT * FROM user WHERE age BETWEEN ? AND ?
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // COUNT query
    long countByAgeGreaterThan(Integer age);
    
    // EXISTS query
    boolean existsByEmail(String email);
    
    // DELETE query
    void deleteByEmail(String email);
}
```

### Использование **Repository**

```java
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User createUser(String name, String email, Integer age) {
        User user = new User(name, email, age);
        return userRepository.save(user);
    }
    
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }
    
    public List<User> findAdults() {
        return userRepository.findByAgeGreaterThan(17);
    }
    
    public long countAdults() {
        return userRepository.countByAgeGreaterThan(17);
    }
    
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}
```

### Custom Queries

```java
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.H2)
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("SELECT * FROM user WHERE age > :minAge AND age < :maxAge")
    List<User> findByAgeRange(@NonNull Integer minAge, @NonNull Integer maxAge);
    
    @Query("UPDATE user SET email = :email WHERE id = :id")
    void updateEmail(@NonNull Long id, @NonNull String email);
    
    @Query("SELECT COUNT(*) FROM user WHERE age > :age")
    long countAdults(@NonNull Integer age);
    
    @Query("SELECT * FROM user ORDER BY name LIMIT :limit OFFSET :offset")
    List<User> findAllWithPagination(@NonNull Integer limit, @NonNull Integer offset);
}
```

### Join Queries

```java
@MappedEntity
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    private User user;
    
    private BigDecimal total;
    private LocalDateTime createdAt;
    
    // Getters and setters...
}

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    
    // Автоматически генерируется JOIN
    List<Order> findByUserEmail(String email);
    
    // Custom JOIN query
    @Query("SELECT o FROM order o JOIN user u ON o.user_id = u.id WHERE u.age > :age")
    List<Order> findByUserAgeGreaterThan(Integer age);
}
```

## Micronaut Data JPA

### Настройка

**build.gradle:**

```gradle
dependencies {
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("org.postgresql:postgresql")
}
```

**application.yml:**

```yaml
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/mydb
    driverClassName: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    maximum-pool-size: 20

jpa:
  default:
    entity-scan:
      packages: 'com.example.entity'
    properties:
      hibernate:
        hbm2ddl:
          auto: update
        show_sql: true
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### JPA Entity

```java
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private Integer age;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters...
}
```

### JPA Repository

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByAgeGreaterThan(Integer age);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE :name")
    List<User> searchByName(String name);
    
    @Query("SELECT u FROM User u JOIN u.orders o WHERE o.total > :amount")
    List<User> findUsersWithOrdersGreaterThan(BigDecimal amount);
}
```

## Transactions

### Declarative Transactions

```java
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    public UserService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }
    
    @Transactional
    public User createUserWithOrder(User user, Order order) {
        User savedUser = userRepository.save(user);
        order.setUser(savedUser);
        orderRepository.save(order);
        return savedUser;
    }
    
    @Transactional(readOnly = true)
    public User getUserWithOrders(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    @Transactional(rollbackOn = {ValidationException.class})
    public void updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        userRepository.save(existing);
    }
}
```

### Programmatic Transactions

```java
import io.micronaut.transaction.SynchronousTransactionManager;
import java.sql.Connection;

@Singleton
public class UserService {
    private final SynchronousTransactionManager<Connection> transactionManager;
    private final UserRepository userRepository;
    
    public UserService(
            SynchronousTransactionManager<Connection> transactionManager,
            UserRepository userRepository) {
        this.transactionManager = transactionManager;
        this.userRepository = userRepository;
    }
    
    public User createUserInTransaction(User user) {
        return transactionManager.executeWrite(status -> {
            return userRepository.save(user);
        });
    }
    
    public User getUserInTransaction(Long id) {
        return transactionManager.executeRead(status -> {
            return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        });
    }
}
```

## Migrations

### Flyway

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.flyway:micronaut-flyway")
}
```

**application.yml:**

```yaml
flyway:
  datasources:
    default:
      enabled: true
      locations: classpath:db/migration
```

**db/migration/V1__create_users_table.sql:**

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**db/migration/V2__create_orders_table.sql:**

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Liquibase

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.liquibase:micronaut-liquibase")
}
```

**application.yml:**

```yaml
liquibase:
  datasources:
    default:
      enabled: true
      change-log: classpath:db/changelog/db.changelog-master.xml
```

**db/changelog/db.`changelog-master`.xml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">
    
    <include file="db/changelog/changes/V1__create_users_table.xml"/>
    <include file="db/changelog/changes/V2__create_orders_table.xml"/>
</databaseChangeLog>
```

## Лучшие практики

### 1. Используйте **Repository Pattern**

```java
// ✅ Хорошо
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

// ❌ Плохо - прямой доступ к EntityManager
@Singleton
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    // ...
}
```

### 2. Используйте **Transactions** для **Multi-step Operations**

```java
// ✅ Хорошо
@Transactional
public User createUserWithOrder(User user, Order order) {
    User saved = userRepository.save(user);
    order.setUser(saved);
    orderRepository.save(order);
    return saved;
}
```

### 3. Используйте @**Query** для **Complex Queries**

```java
// ✅ Хорошо
@Query("SELECT u FROM User u WHERE u.age > :minAge AND u.name LIKE :name")
List<User> findUsers(Integer minAge, String name);
```

### 4. Используйте **Migrations** для **Schema Management**

```java
// ✅ Хорошо - используйте Flyway или Liquibase
// Не используйте hbm2ddl.auto=create в production
```

### 5. Оптимизируйте **Queries**

```java
// ✅ Хорошо - используйте JOIN FETCH для избежания N+1
@Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :id")
Optional<User> findByIdWithOrders(Long id);
```

## Batch Operations

### Batch Insert

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("INSERT INTO user (name, email, age) VALUES (:name, :email, :age)")
    void insertUser(String name, String email, Integer age);
}

@Singleton
public class BatchUserService {
    private final JdbcOperations jdbcOperations;
    
    public BatchUserService(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
    
    @Transactional
    public void batchInsertUsers(List<User> users) {
        String sql = "INSERT INTO user (name, email, age) VALUES (?, ?, ?)";
        jdbcOperations.prepareStatement(sql, statement -> {
            for (User user : users) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setInt(3, user.getAge());
                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        });
    }
}
```

### Batch Update

```java
@Singleton
public class BatchUpdateService {
    private final JdbcOperations jdbcOperations;
    
    @Transactional
    public void batchUpdateUsers(List<User> users) {
        String sql = "UPDATE user SET name = ?, email = ? WHERE id = ?";
        jdbcOperations.prepareStatement(sql, statement -> {
            for (User user : users) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setLong(3, user.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            return null;
        });
    }
}
```

## Stored Procedures

### Вызов **Stored Procedures**

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Procedure("get_user_by_email")
    Optional<User> getUserByEmail(String email);
    
    @Query(value = "CALL update_user_age(:id, :age)", nativeQuery = true)
    void updateUserAge(Long id, Integer age);
}
```

## Projections

### DTO Projections

```java
public class UserSummary {
    private String name;
    private String email;
    
    public UserSummary(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // Getters
}

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("SELECT u.name, u.email FROM user u WHERE u.id = :id")
    Optional<UserSummary> findSummaryById(Long id);
    
    @Query("SELECT u.name, u.email FROM user u")
    List<UserSummary> findAllSummaries();
}
```

## Async Operations

### Async Repository

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.async.AsyncCrudRepository;
import java.util.concurrent.CompletableFuture;

@Repository
public interface AsyncUserRepository extends AsyncCrudRepository<User, Long> {
    
    CompletableFuture<Optional<User>> findByEmail(String email);
    
    CompletableFuture<List<User>> findByAgeGreaterThan(Integer age);
}
```

### Reactive Repository

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactiveStreamsRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveUserRepository 
        extends ReactiveStreamsRepository<User, Long> {
    
    Mono<User> findByEmail(String email);
    
    Flux<User> findByAgeGreaterThan(Integer age);
}
```

## Custom SQL

### Native Queries

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query(value = "SELECT * FROM users WHERE age > :age", nativeQuery = true)
    List<User> findAdultsNative(Integer age);
    
    @Query(value = "CALL get_user_statistics(:userId)", nativeQuery = true)
    UserStatistics getUserStatistics(Long userId);
}
```

### Dynamic Queries

```java
@Singleton
public class DynamicQueryService {
    private final JdbcOperations jdbcOperations;
    
    public DynamicQueryService(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }
    
    public List<User> findUsersWithDynamicQuery(Map<String, Object> filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (filters.containsKey("name")) {
            sql.append(" AND name LIKE ?");
            params.add("%" + filters.get("name") + "%");
        }
        
        if (filters.containsKey("age")) {
            sql.append(" AND age > ?");
            params.add(filters.get("age"));
        }
        
        return jdbcOperations.prepareStatement(sql.toString(), statement -> {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            return statement.executeQuery();
        });
    }
}
```

## Query Optimization

### Query Hints

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("SELECT * FROM user WHERE email = :email")
    @QueryHint(name = "fetchSize", value = "100")
    Optional<User> findByEmail(String email);
}
```

### Pagination

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("SELECT * FROM user ORDER BY name LIMIT :limit OFFSET :offset")
    List<User> findAllWithPagination(Integer limit, Integer offset);
    
    @Query("SELECT COUNT(*) FROM user")
    long countAll();
}
```

## Database Connection Pooling

### HikariCP Configuration

**application.yml:**

```yaml
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/mydb
    driverClassName: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    maximum-pool-size: 20
    minimum-idle: 5
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
```

## Database Migrations

### Flyway Migrations

**db/migration/V1__create_users.sql:**

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    age INTEGER
);
```

### Liquibase Migrations

**db/changelog/db.`changelog-master`.xml:**

```xml
<databaseChangeLog>
    <changeSet id="1" author="developer">
        <createTable tableName="users">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="email" type="VARCHAR(255)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="age" type="INTEGER"/>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

## Query Result Mapping

### Custom Result Mappers

```java
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;

@JdbcRepository
public interface UserRepository extends CrudRepository<User, Long> {
    
    @Query("SELECT name, email FROM users WHERE age > :age")
    List<UserSummary> findUserSummaries(Integer age);
}
```


## Заключение

**Micronaut Data** предоставляет мощные инструменты для работы с базами данных с **compile-time query generation**, что обеспечивает высокую производительность и типобезопасность. Поддержка **batch operations**, **stored procedures**, **projections**, миграций, транзакций, **async**/**reactive operations**, **custom SQL**, **dynamic queries**, **query optimization**, **pagination**, **connection pooling**, **database migrations**, **result mapping** и других продвинутых возможностей позволяет эффективно работать с базами данных. Выбор между **JDBC** и **JPA** зависит от требований проекта, но оба подхода поддерживаются на высоком уровне.

## Дополнительные ресурсы

- [**Micronaut Data** Documentation](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
- [**Micronaut Data JDBC**](https://micronaut-projects.github.io/micronaut-data/latest/guide/#jdbc)
- [**Micronaut Data JPA**](https://micronaut-projects.github.io/micronaut-data/latest/guide/#jpa)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [**R2DBC** Documentation](https://r2dbc.io/)
- [**HikariCP** Documentation](https://github.com/brettwooldridge/HikariCP#documentation)
