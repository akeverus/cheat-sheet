---
title: "Quarkus: Data Access - Hibernate ORM, Panache и Repositories"
description: "Полное руководство по работе с данными в Quarkus: Hibernate ORM, Panache, repositories, transactions, Flyway/Liquibase и best practices"
tags: ["quarkus", "hibernate", "panache", "jpa", "database", "java"]
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-security.md"]
updated: "2026-02-11"
related: ["quarkus-core.md", "quarkus-security.md"]
---

# Quarkus: Data Access - Hibernate ORM, Panache и Repositories



## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Quarkus: Data Access - Hibernate ORM, Panache и Repositories](#quarkus-data-access-hibernate-orm-panache-и-repositories)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Hibernate ORM](#hibernate-orm)
  - [Entity Definition](#entity-definition)
  - [EntityManager Usage](#entitymanager-usage)
- [Panache](#panache)
  - [Panache Entity](#panache-entity)
  - [Panache Repository](#panache-repository)
- [Transactions](#transactions)
  - [Transactional Methods](#transactional-methods)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте Panache для упрощения](#1-используйте-panache-для-упрощения)
  - [2. Используйте @Transactional правильно](#2-используйте-transactional-правильно)
  - [3. Используйте миграции](#3-используйте-миграции)
- [✅ Хорошо](#хорошо)
- [Panache Query Methods](#panache-query-methods)
  - [Query Methods](#query-methods)
  - [Custom Queries](#custom-queries)
- [Entity Relationships](#entity-relationships)
  - [One-to-Many](#one-to-many)
  - [Many-to-One](#many-to-one)
- [Database Migrations](#database-migrations)
  - [Flyway Configuration](#flyway-configuration)
  - [Liquibase Configuration](#liquibase-configuration)
- [Reactive Data Access](#reactive-data-access)
  - [Reactive Panache](#reactive-panache)
  - [4. Оптимизируйте запросы](#4-оптимизируйте-запросы)
- [Advanced Panache Features](#advanced-panache-features)
  - [Native Queries](#native-queries)
  - [One-to-Many Bidirectional](#one-to-many-bidirectional)
  - [Many-to-Many](#many-to-many)
  - [Eager vs Lazy Loading](#eager-vs-lazy-loading)
- [Transaction Management](#transaction-management)
  - [Programmatic Transactions](#programmatic-transactions)
  - [Transaction Propagation](#transaction-propagation)
  - [Flyway Migrations](#flyway-migrations)
  - [Liquibase Migrations](#liquibase-migrations)
- [Performance Optimization](#performance-optimization)
  - [Query Optimization](#query-optimization)
  - [Batch Operations](#batch-operations)
  - [Caching](#caching)
- [application.properties](#applicationproperties)
  - [5. Используйте lazy loading где возможно](#5-используйте-lazy-loading-где-возможно)
- [JDBC](#jdbc)
  - [Direct JDBC Access](#direct-jdbc-access)
  - [Reactive JDBC](#reactive-jdbc)
- [Database Connection Pooling](#database-connection-pooling)
  - [HikariCP Configuration](#hikaricp-configuration)
  - [Reactive Connection Pooling](#reactive-connection-pooling)
- [Multiple Data Sources](#multiple-data-sources)
  - [Multiple Databases](#multiple-databases)
- [Primary database](#primary-database)
- [Secondary database](#secondary-database)
  - [6. Настраивайте connection pooling](#6-настраивайте-connection-pooling)
- [Advanced Data Access Patterns](#advanced-data-access-patterns)
  - [CQRS Pattern](#cqrs-pattern)
  - [Event Sourcing](#event-sourcing)
- [Database Performance Tuning](#database-performance-tuning)
  - [Query Cache](#query-cache)
  - [Batch Processing](#batch-processing)
- [Advanced Database Patterns](#advanced-database-patterns)
  - [Repository Pattern with Specifications](#repository-pattern-with-specifications)
  - [Unit of Work Pattern](#unit-of-work-pattern)
  - [Database Sharding](#database-sharding)
- [Database Connection Management](#database-connection-management)
  - [Connection Pool Monitoring](#connection-pool-monitoring)
  - [Connection Leak Detection](#connection-leak-detection)
- [Advanced Query Optimization](#advanced-query-optimization)
  - [Query Plan Analysis](#query-plan-analysis)
  - [Lazy Loading Optimization](#lazy-loading-optimization)
- [Transaction Management Patterns](#transaction-management-patterns)
  - [Nested Transactions](#nested-transactions)
  - [Transaction Timeout](#transaction-timeout)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет несколько подходов к работе с базами данных: **Hibernate ORM**, **Panache** (**упрощенный API**), и прямые **JDBC** запросы. Все оптимизированы для быстрого запуска и низкого потребления памяти.

### Основные возможности

- **Hibernate ORM**: Полнофункциональный **ORM**
- **Panache**: Упрощенный **API** поверх **Hibernate**
- **Repositories**: **Repository pattern**
- **Transactions**: Управление транзакциями
- **Flyway/Liquibase**: Миграции БД

## Hibernate ORM

### **Entity Definition**

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    // Getters and setters
}
```

### **EntityManager Usage**

```java
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {
    @Inject
    EntityManager entityManager;
    
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }
    
    public void save(User user) {
        entityManager.persist(user);
    }
}
```

## Panache

### **Panache Entity**

```java
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    
    // Автоматически наследует методы: findById, findAll, persist, delete и др.
}
```

### **Panache Repository**

```java
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public List<User> findByName(String name) {
        return find("name", name).list();
    }
    
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
```

## Transactions

### **Transactional Methods**

```java
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionalService {
    
    @Transactional
    public User createUser(User user) {
        user.persist();
        return user;
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void updateUser(Long id, User user) {
        User existing = User.findById(id);
        existing.name = user.name;
        existing.email = user.email;
    }
}
```

## Лучшие практики

### 1. Используйте **Panache** для упрощения

```java
// ✅ Хорошо
@Entity
public class User extends PanacheEntity {
    // Упрощенный API
}
```

### 2. Используйте @**Transactional** правильно

```java
// ✅ Хорошо
@Transactional
public void saveUser(User user) {
    // Транзакционная операция
}
```

### 3. Используйте миграции

```properties
# ✅ Хорошо
quarkus.flyway.migrate-at-start=true
```

## Panache Query Methods

### **Query Methods**

```java
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public List<User> findByName(String name) {
        return find("name", name).list();
    }
    
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    public List<User> findByAgeBetween(Integer minAge, Integer maxAge) {
        return find("age between ?1 and ?2", minAge, maxAge).list();
    }
    
    public long countByStatus(String status) {
        return count("status", status);
    }
}
```

### **Custom Queries**

```java
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public List<User> findActiveUsers() {
        return find("status = 'ACTIVE'").list();
    }
    
    public List<User> findUsersWithOrders() {
        return find("SELECT u FROM User u JOIN u.orders o").list();
    }
}
```

## Entity Relationships

### **One-to-Many**

```java
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.List;

@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Order> orders;
}
```

### **Many-to-One**

```java
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Order extends PanacheEntity {
    public String orderNumber;
    
    @ManyToOne
    public User user;
}
```

## Database Migrations

### **Flyway Configuration**

**application.properties:**

```properties
quarkus.flyway.migrate-at-start=true
quarkus.flyway.locations=classpath:db/migration
```

### **Liquibase Configuration**

**application.properties:**

```properties
quarkus.liquibase.migrate-at-start=true
quarkus.liquibase.change-log=db/changelog/db.changelog-master.xml
```

## Reactive Data Access

### **Reactive Panache**

```java
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;

@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    
    public static Uni<User> findByNameReactive(String name) {
        return find("name", name).firstResult();
    }
}
```

## Advanced Panache Features

### Panache Query Methods

**Автоматическая генерация **query methods**:**

```java
@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    public Integer age;
    
    // Автоматически генерируются методы:
    // findByName, findByEmail, findByAge, etc.
}
```

### **Custom Queries**

**Создание кастомных запросов:**

```java
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public List<User> findActiveUsers() {
        return find("status = 'ACTIVE'").list();
    }
    
    public List<User> findUsersByAgeRange(Integer minAge, Integer maxAge) {
        return find("age between ?1 and ?2", minAge, maxAge).list();
    }
    
    public Optional<User> findByEmailAndStatus(String email, String status) {
        return find("email = ?1 and status = ?2", email, status)
            .firstResultOptional();
    }
}
```

### **Native Queries**

**Использование нативных **SQL** запросов:**

```java
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public List<User> findUsersWithNativeQuery() {
        return find("#User.findActive").list();
    }
}
```

**META-INF/orm.xml:**

```xml
<named-native-query name="User.findActive">
    <query>SELECT * FROM users WHERE status = 'ACTIVE'</query>
</named-native-query>
```

## Entity Relationships

### **One-to-Many Bidirectional**

```java
@Entity
public class User extends PanacheEntity {
    public String name;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Order> orders = new ArrayList<>();
}

@Entity
public class Order extends PanacheEntity {
    public String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}
```

### **Many-to-Many**

```java
@Entity
public class User extends PanacheEntity {
    public String name;
    
    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public List<Role> roles = new ArrayList<>();
}

@Entity
public class Role extends PanacheEntity {
    public String name;
    
    @ManyToMany(mappedBy = "roles")
    public List<User> users = new ArrayList<>();
}
```

### **Eager** vs **Lazy Loading**

```java
@Entity
public class User extends PanacheEntity {
    public String name;
    
    @OneToMany(fetch = FetchType.EAGER)  // Загружается сразу
    public List<Order> orders;
    
    @ManyToOne(fetch = FetchType.LAZY)  // Загружается по требованию
    public Organization organization;
}
```

## Transaction Management

### **Programmatic Transactions**

**Программное управление транзакциями:**

```java
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionalService {
    
    @Transactional
    public void createUserWithOrders(User user, List<Order> orders) {
        user.persist();
        orders.forEach(order -> {
            order.user = user;
            order.persist();
        });
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void updateUser(Long id, User user) {
        User existing = User.findById(id);
        if (existing == null) {
            throw new UserNotFoundException();
        }
        existing.name = user.name;
        existing.email = user.email;
    }
}
```

### **Transaction Propagation**

**Управление распространением транзакций:**

```java
@ApplicationScoped
public class TransactionalService {
    
    @Transactional(Transactional.TxType.REQUIRED)  // По умолчанию
    public void method1() {
        // Использует существующую транзакцию или создает новую
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void method2() {
        // Всегда создает новую транзакцию
    }
    
    @Transactional(Transactional.TxType.MANDATORY)
    public void method3() {
        // Требует существующую транзакцию
    }
    
    @Transactional(Transactional.TxType.NEVER)
    public void method4() {
        // Не должен выполняться в транзакции
    }
}
```

## Database Migrations

### **Flyway Migrations**

**Структура миграций **Flyway**:**

```text
src/main/resources/db/migration/
├── V1__Create_users_table.sql
├── V2__Create_orders_table.sql
├── V3__Add_email_index.sql
└── V4__Add_status_column.sql
```

**V1__Create_users_table.sql:**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**application.properties:**

```properties
quarkus.flyway.migrate-at-start=true
quarkus.flyway.locations=classpath:db/migration
quarkus.flyway.baseline-on-migrate=true
```

### **Liquibase Migrations**

**db/changelog/db.`changelog-master`.xml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd">
    
    <changeSet id="1" author="developer">
        <createTable tableName="users">
            <column name="id" type="BIGSERIAL">
                <constraints primaryKey="true"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="email" type="VARCHAR(255)">
                <constraints nullable="false" unique="true"/>
            </column>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

**application.properties:**

```properties
quarkus.liquibase.migrate-at-start=true
quarkus.liquibase.change-log=db/changelog/db.changelog-master.xml
```

## Performance Optimization

### **Query Optimization**

**Оптимизация запросов:**

```java
@ApplicationScoped
public class OptimizedRepository implements PanacheRepository<User> {
    
    // Использование JOIN FETCH для избежания N+1 проблемы
    public List<User> findUsersWithOrders() {
        return find("SELECT DISTINCT u FROM User u JOIN FETCH u.orders")
            .list();
    }
    
    // Использование проекций для уменьшения объема данных
    public List<UserSummary> findUserSummaries() {
        return find("SELECT new UserSummary(u.id, u.name) FROM User u")
            .list();
    }
}
```

### **Batch Operations**

**Пакетные операции:**

```java
@ApplicationScoped
public class BatchService {
    
    @Transactional
    public void batchInsert(List<User> users) {
        int batchSize = 50;
        for (int i = 0; i < users.size(); i++) {
            users.get(i).persist();
            if (i % batchSize == 0 && i > 0) {
                User.getEntityManager().flush();
                User.getEntityManager().clear();
            }
        }
    }
}
```

### **Caching**

**Настройка кеширования:**

```properties
# application.properties
quarkus.hibernate-orm.cache.use-second-level-cache=true
quarkus.hibernate-orm.cache.query-cache=true
```

```java
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends PanacheEntity {
    // Кешируемая сущность
}
```

## JDBC

### **Direct JDBC Access**

**Прямой доступ к **JDBC**:**

```java
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ApplicationScoped
public class JdbcService {
    
    @Inject
    DataSource dataSource;
    
    public List<User> findAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.id = rs.getLong("id");
                user.name = rs.getString("name");
                user.email = rs.getString("email");
                users.add(user);
            }
        }
        return users;
    }
}
```

### **Reactive JDBC**

**Реактивный доступ к **JDBC**:**

```java
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.pgclient.PgPool;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReactiveJdbcService {
    
    @Inject
    PgPool client;
    
    public Uni<List<User>> findAllUsersReactive() {
        return client.query("SELECT * FROM users")
            .execute()
            .map(rows -> {
                List<User> users = new ArrayList<>();
                for (Row row : rows) {
                    User user = new User();
                    user.id = row.getLong("id");
                    user.name = row.getString("name");
                    user.email = row.getString("email");
                    users.add(user);
                }
                return users;
            });
    }
}
```

## Database Connection Pooling

### **HikariCP Configuration**

**Настройка **HikariCP**:**

```properties
# application.properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/mydb
quarkus.datasource.jdbc.driver=org.postgresql.Driver
quarkus.datasource.jdbc.max-size=20
quarkus.datasource.jdbc.min-size=5
quarkus.datasource.jdbc.idle-timeout=30m
quarkus.datasource.jdbc.max-lifetime=60m
```

### **Reactive Connection Pooling**

**Настройка **reactive connection pool**:**

```properties
# application.properties
quarkus.datasource.reactive.max-size=20
quarkus.datasource.reactive.idle-timeout=30m
quarkus.datasource.reactive.max-lifetime=60m
```

## Multiple Data Sources

### **Multiple Databases**

**Работа с несколькими базами данных:**

```properties
# application.properties
# Primary database
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/primary
quarkus.datasource.jdbc.driver=org.postgresql.Driver

# Secondary database
quarkus.datasource.secondary.jdbc.url=jdbc:postgresql://localhost:5432/secondary
quarkus.datasource.secondary.jdbc.driver=org.postgresql.Driver
```

**Использование:**

```java
import jakarta.inject.Named;
import jakarta.inject.Inject;
import javax.sql.DataSource;

@ApplicationScoped
public class MultiDataSourceService {
    
    @Inject
    @Named("default")
    DataSource primaryDataSource;
    
    @Inject
    @Named("secondary")
    DataSource secondaryDataSource;
}
```

## Advanced Data Access Patterns

### **CQRS Pattern**

**Реализация **CQRS**:**

```java
@ApplicationScoped
public class CommandService {
    
    @Transactional
    public void createUser(CreateUserCommand command) {
        User user = new User();
        user.setName(command.getName());
        user.setEmail(command.getEmail());
        user.persist();
    }
}

@ApplicationScoped
public class QueryService {
    
    public List<UserDTO> findUsers(UserQuery query) {
        return User.find("name like ?1", "%" + query.getName() + "%")
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
```

### **Event Sourcing**

**Реализация **Event Sourcing**:**

```java
@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<UserEvent> events = new ArrayList<>();
    
    public void applyEvent(UserEvent event) {
        events.add(event);
        apply(event);
    }
    
    private void apply(UserEvent event) {
        if (event instanceof UserCreatedEvent) {
            this.name = ((UserCreatedEvent) event).getName();
            this.email = ((UserCreatedEvent) event).getEmail();
        }
    }
}
```

## Database Performance Tuning

### **Query Cache**

**Использование кеша запросов:**

```properties
quarkus.hibernate-orm.cache.use-second-level-cache=true
quarkus.hibernate-orm.cache.use-query-cache=true
```

### **Batch Processing**

**Батчевая обработка:**

```java
@ApplicationScoped
public class BatchProcessingService {
    
    @Transactional
    public void batchInsert(List<User> users) {
        int batchSize = 50;
        for (int i = 0; i < users.size(); i++) {
            users.get(i).persist();
            if ((i + 1) % batchSize == 0) {
                User.getEntityManager().flush();
                User.getEntityManager().clear();
            }
        }
    }
}
```

## Advanced Database Patterns

### **Repository Pattern with Specifications**

**Реализация спецификаций:**

```java
@ApplicationScoped
public class UserSpecification {
    
    public static Specification<User> hasName(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }
    
    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }
    
    public static Specification<User> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
```

### **Unit of Work Pattern**

**Реализация Unit of Work:**

```java
@ApplicationScoped
public class UnitOfWorkService {
    
    @Inject
    EntityManager entityManager;
    
    @Transactional
    public void executeUnitOfWork(Runnable work) {
        try {
            work.run();
            entityManager.flush();
        } catch (Exception e) {
            entityManager.clear();
            throw e;
        }
    }
}
```

### **Database Sharding**

**Шардирование базы данных:**

```java
@ApplicationScoped
public class ShardingService {
    
    @Inject
    @Named("shard1")
    EntityManager shard1;
    
    @Inject
    @Named("shard2")
    EntityManager shard2;
    
    public EntityManager getShard(Long userId) {
        return userId % 2 == 0 ? shard1 : shard2;
    }
}
```

## Database Connection Management

### **Connection Pool Monitoring**

**Мониторинг пула соединений:**

```java
@ApplicationScoped
public class ConnectionPoolMonitor {
    
    @Inject
    DataSource dataSource;
    
    public PoolStats getPoolStats() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) dataSource;
            HikariPoolMXBean poolBean = hikari.getHikariPoolMXBean();
            return new PoolStats(
                poolBean.getActiveConnections(),
                poolBean.getIdleConnections(),
                poolBean.getTotalConnections()
            );
        }
        return null;
    }
}
```

### **Connection Leak Detection**

**Обнаружение утечек соединений:**

```properties
quarkus.datasource.jdbc.leak-detection-interval=60s
```

## Advanced Query Optimization

### **Query Plan Analysis**

**Анализ плана запросов:**

```java
@ApplicationScoped
public class QueryAnalyzer {
    
    @Inject
    EntityManager entityManager;
    
    public void analyzeQuery(String query) {
        Query jpaQuery = entityManager.createQuery(query);
        // Включение статистики
        Statistics stats = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactory.class)
            .getStatistics();
        stats.setStatisticsEnabled(true);
        
        jpaQuery.getResultList();
        
        // Анализ статистики
        long queryCount = stats.getQueryExecutionCount();
        long queryTime = stats.getQueryExecutionMaxTime();
    }
}
```

### **Lazy Loading Optimization**

**Оптимизация **lazy loading**:**

```java
@Entity
public class User extends PanacheEntity {
    
    @OneToMany(fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    public List<Order> orders;
}
```

## Transaction Management Patterns

### **Nested Transactions**

**Вложенные транзакции:**

```java
@ApplicationScoped
public class NestedTransactionService {
    
    @Transactional
    public void outerTransaction() {
        // Внешняя транзакция
        innerTransaction();
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void innerTransaction() {
        // Новая транзакция
    }
}
```

### **Transaction Timeout**

**Таймаут транзакций:**

```java
@ApplicationScoped
public class TimedTransactionService {
    
    @Transactional(timeout = 30)
    public void longRunningOperation() {
        // Операция с таймаутом 30 секунд
    }
}
```

## Заключение

**Quarkus Data Access** предоставляет мощные инструменты для работы с базами данных. Поддержка **Hibernate ORM**, **Panache**, **repositories**, **transactions**, миграций, **relationships**, **reactive data access**, **JDBC**, **connection pooling**, **multiple data sources**, оптимизации производительности и других продвинутых возможностей позволяет эффективно работать с данными. Правильное использование **ORM** паттернов, управление транзакциями, миграции, оптимизация запросов и настройка **connection pooling** являются ключевыми аспектами создания качественных приложений.

## Дополнительные ресурсы

- [**Quarkus Hibernate ORM** Guide](https://quarkus.io/guides/hibernate-orm)
- [**Quarkus Panache** Guide](https://quarkus.io/guides/hibernate-orm-panache)
- [**Hibernate** Documentation](https://hibernate.org/orm/documentation/)
- [**Quarkus Reactive Hibernate**](https://quarkus.io/guides/hibernate-orm-reactive)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [**HikariCP** Documentation](https://github.com/brettwooldridge/HikariCP#documentation)
