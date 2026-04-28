---
title: "Spring Data JPA + Hibernate: ORM и работа с данными"
description: "Полное руководство по Hibernate и Spring Data JPA: маппинг сущностей, запросы, кэширование, производительность, интеграция с Spring Boot"
tags:
  - hibernate
  - spring-data-jpa
  - orm
  - jpa
  - database
  - mapping
  - queries
  - caching
type: "overview"
difficulty: "intermediate"
aliases:
  - "Spring Data JPA + Hibernate"
  - "ORM и работа с данными"
  - "spring hibernate"
prerequisites:
  - "[[spring-boot]]"
  - "[[java-basics]]"
  - "[[databases]]"
next:
  - "[[spring-data-jpa]]"
updated: "2026-04-20"
---

# Spring Data JPA + Hibernate: ORM и работа с данными

Кратко: Полное руководство по **Hibernate ORM** и **Spring Data JPA**. Включает маппинг сущностей, запросы (JPQL, `Criteria`, native SQL), кэширование (first/second level), производительность, интеграцию с **Spring Boot**.

## Полезные ссылки

### Официальная документация
- [**Hibernate** Documentation](https://hibernate.org/orm/documentation/)
- [**Spring Data JPA**](https://docs.spring.io/spring-data/jpa/reference/)
- [**JPA** Specification](https://jakarta.ee/specifications/persistence/)

### Обучающие материалы
- [**Hibernate** Tutorials](https://www.baeldung.com/learn-jpa-hibernate)
- [**Spring Data JPA**](https://www.baeldung.com/spring-data-jpa-tutorial)
- [**JPA Criteria API**](https://www.baeldung.com/hibernate-criteria-queries)

### Дополнительные ресурсы
- [Vlad Mihalcea's Blog](https://vladmihalcea.com/)
- [**JPA Buddy**](https://jpa-buddy.com/)
- [**Hibernate** Performance](https://vladmihalcea.com/tutorials/hibernate/)


### См. также
- [Spring Data JPA](spring-data-jpa.md) — Spring Data-абстракция поверх JPA/Hibernate
## Содержание

- [Введение в ORM и JPA](#введение-в-orm-и-jpa)
  - [Что такое ORM?](#что-такое-orm)
  - [JPA vs Hibernate](#jpa-vs-hibernate)
  - [Настройка проекта](#настройка-проекта)
- [Маппинг сущностей](#маппинг-сущностей)
  - [Базовая сущность](#базовая-сущность)
  - [Аннотации JPA](#аннотации-jpa)
    - [@Entity](#entity)
    - [@Id и генерация ключей](#id-и-генерация-ключей)
    - [@Column](#column)
    - [@Temporal](#temporal)
  - [Embeddable типы](#embeddable-типы)
  - [Enums](#enums)
  - [Lifecycle Callbacks](#lifecycle-callbacks)
- [Отношения между сущностями](#отношения-между-сущностями)
  - [@OneToOne](#onetoone)
  - [@OneToMany / @ManyToOne](#onetomany-manytoone)
  - [@ManyToMany](#manytomany)
  - [Fetch стратегии](#fetch-стратегии)
- [Spring Data JPA репозитории](#spring-data-jpa-репозитории)
  - [Базовый репозиторий](#базовый-репозиторий)
  - [Кастомные методы](#кастомные-методы)
  - [Specifications (динамические запросы)](#specifications-динамические-запросы)
- [Запросы и JPQL](#запросы-и-jpql)
  - [JPQL (Java Persistence Query Language)](#jpql-java-persistence-query-language)
  - [Named Queries](#named-queries)
- [Criteria API](#criteria-api)
  - [Базовое использование](#базовое-использование)
  - [Сложные запросы](#сложные-запросы)
- [Native SQL запросы](#native-sql-запросы)
  - [Простые native запросы](#простые-native-запросы)
  - [Сложные native запросы](#сложные-native-запросы)
  - [EntityManager для сложных запросов](#entitymanager-для-сложных-запросов)
- [Кэширование](#кэширование)
  - [First Level Cache (L1)](#first-level-cache-l1)
  - [Second Level Cache (L2)](#second-level-cache-l2)
  - [Query Cache](#query-cache)
- [Транзакции](#транзакции)
  - [Декларативные транзакции](#декларативные-транзакции)
  - [Настройки транзакций](#настройки-транзакций)
  - [Программные транзакции](#программные-транзакции)
- [Производительность](#производительность)
  - [N+1 проблема](#n1-проблема)
  - [Пагинация](#пагинация)
  - [Batch операции](#batch-операции)
  - [Оптимизация Hibernate](#оптимизация-hibernate)
  - [Индексы и статистика](#индексы-и-статистика)
- [Миграции базы данных](#миграции-базы-данных)
  - [Flyway](#flyway)
  - [Liquibase](#liquibase)
- [Аудит и versioning](#аудит-и-versioning)
  - [Hibernate Envers](#hibernate-envers)
  - [Кастомный аудит](#кастомный-аудит)
- [Spring Boot интеграция](#spring-boot-интеграция)
  - [Полная конфигурация](#полная-конфигурация)
- [Тестирование](#тестирование)
  - [Unit тестирование репозиториев](#unit-тестирование-репозиториев)
  - [Интеграционное тестирование](#интеграционное-тестирование)
  - [Тестирование транзакций](#тестирование-транзакций)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [1. LazyInitializationException](#1-lazyinitializationexception)
    - [2. N+1 проблема](#2-n1-проблема)
    - [3. OptimisticLockException](#3-optimisticlockexception)
    - [4. Connection pool exhaustion](#4-connection-pool-exhaustion)
    - [5. Slow queries](#5-slow-queries)
  - [Диагностика](#диагностика)
    - [Hibernate Statistics](#hibernate-statistics)
    - [SQL Logging](#sql-logging)
    - [VisualVM для профилирования](#visualvm-для-профилирования)
  - [Полезные команды](#полезные-команды)
- [Заключение](#заключение)
- [См. также](#см-также-1)

## Введение в ORM и JPA

### Что такое ORM?

**ORM** (Object-`Relational` Mapping) — технология, которая позволяет работать с реляционными базами данных используя объектно-ориентированный подход вместо **SQL**.

**Преимущества `ORM`:**
- **Продуктивность** — меньше **boilerplate** кода
- **Безопасность** — защита от **SQL injection**
- **Переносимость** — между разными БД
- **Кэширование** — автоматическое кэширование данных
- **Отношения** — управление связями между объектами

### JPA vs Hibernate

| Характеристика | **JPA** | **Hibernate** |
|----------------|-----|-----------|
| **Тип** | Спецификация (API) | Реализация **JPA** |
| **Vendor** | **Jakarta** `EE` | **Red Hat** (JBoss) |
| **Расширения** | Ограничены стандартом | Собственные возможности |
| **Использование** | Через **EntityManager** | **Hibernate Session** |
| **Кэширование** | Не определено | **Second level cache** |

### Настройка проекта

**Зависимости **Spring Boot Data JPA** и **H2** (pom.xml):**

```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Для PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: user
    password: password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate  # none, validate, update, create, create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

## Маппинг сущностей

### Базовая сущность

```java
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    // Constructors, getters, setters, equals, hashCode, toString
}
```

### Аннотации JPA

#### @Entity
```java
@Entity(name = "UserEntity")  // Имя сущности для JPQL
@Table(name = "users",        // Имя таблицы
       schema = "public",     // Схема
       catalog = "myapp",     // Каталог
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"email"}),
           @UniqueConstraint(columnNames = {"username"})
       })
public class User {
    // ...
}
```

#### `@Id` и генерация ключей

```java
// AUTO - Hibernate выбирает стратегию
@GeneratedValue(strategy = GenerationType.AUTO)

// IDENTITY - автоинкремент (MySQL, PostgreSQL)
@GeneratedValue(strategy = GenerationType.IDENTITY)

// SEQUENCE - последовательности (Oracle, PostgreSQL)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 50)

// TABLE - таблица генерации ID
@GeneratedValue(strategy = GenerationType.TABLE, generator = "user_gen")
@TableGenerator(name = "user_gen", table = "id_generator",
                pkColumnName = "gen_name", valueColumnName = "gen_val",
                pkColumnValue = "user_id", initialValue = 1000, allocationSize = 50)

// UUID
@GeneratedValue(generator = "uuid2")
@GenericGenerator(name = "uuid2", strategy = "uuid2")
@Column(columnDefinition = "BINARY(16)")
private UUID id;
```

#### @Column

```java
@Column(name = "full_name",           // Имя колонки
        nullable = false,             // NOT NULL
        unique = true,               // UNIQUE
        length = 100,                // VARCHAR(100)
        precision = 10,              // DECIMAL precision
        scale = 2,                   // DECIMAL scale
        columnDefinition = "TEXT",   // DDL определение
        insertable = true,           // Включается в INSERT
        updatable = false)           // Не обновляется
private String fullName;
```

#### @Temporal

```java
// DATE - только дата
@Temporal(TemporalType.DATE)
private Date birthDate;

// TIME - только время
@Temporal(TemporalType.TIME)
private Date loginTime;

// TIMESTAMP - дата и время
@Temporal(TemporalType.TIMESTAMP)
private Date createdAt;

// Java 8+ Date/Time (рекомендуется)
@Column(name = "created_at")
private LocalDateTime createdAt;

@Column(name = "birth_date")
private LocalDate birthDate;

@Column(name = "login_time")
private LocalTime loginTime;
```

### Embeddable типы

```java
// Встраиваемый объект (Embeddable) для адреса
@Embeddable
public class Address {

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    // Constructors, getters, setters
}

@Entity
public class Company {

    @Id
    private Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "company_street")),
        @AttributeOverride(name = "city", column = @Column(name = "company_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "company_zip"))
    })
    private Address address;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip"))
    })
    private Address billingAddress;
}
```

### Enums

```java
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, DELETED
}

// ORDINAL (не рекомендуется - меняет порядок при добавлении)
@Enumerated(EnumType.ORDINAL)
private UserStatus status;  // ACTIVE=0, INACTIVE=1, etc.

// STRING (рекомендуется)
@Enumerated(EnumType.STRING)
private UserStatus status;  // ACTIVE, INACTIVE, etc.
```

### Lifecycle Callbacks

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuditableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private Long version;

    // Custom callbacks
    @PrePersist
    public void prePersist() {
        setCreatedAt(LocalDateTime.now());
        setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreUpdate
    public void preUpdate() {
        setUpdatedAt(LocalDateTime.now());
        setLastModifiedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
```

## Отношения между сущностями

### @OneToOne

```java
@Entity
public class User {

    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile profile;

    // Bidirectional
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserSettings settings;
}

@Entity
public class UserProfile {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String bio;
    private String avatarUrl;
}
```

### @OneToMany / @ManyToOne

```java
@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Comment> comments = new ArrayList<>();

    // Helper methods
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private LocalDateTime createdAt;
}
```

### @ManyToMany

```java
@Entity
public class User {

    @Id
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

@Entity
public class Role {

    @Id
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
```

### Fetch стратегии

```java
// LAZY - загрузка по требованию (рекомендуется по умолчанию)
@OneToMany(fetch = FetchType.LAZY)
private List<Comment> comments;

// EAGER - немедленная загрузка (использовать осторожно)
@ManyToOne(fetch = FetchType.EAGER)
private User author;

// SUBSELECT - загрузка подзапросом
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.SUBSELECT)
private List<Comment> comments;

// BATCH - загрузка пачками
@OneToMany(fetch = FetchType.LAZY)
@BatchSize(size = 10)
private List<Comment> comments;
```

## Spring Data JPA репозитории

### Базовый репозиторий

```java
// Spring Data JPA репозиторий для User
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск по имени
    List<User> findByUsername(String username);

    // Поиск по email с игнорированием регистра
    Optional<User> findByEmailIgnoreCase(String email);

    // Поиск активных пользователей
    List<User> findByStatus(UserStatus status);

    // Поиск по нескольким полям
    List<User> findByUsernameAndStatus(String username, UserStatus status);

    // Поиск с пагинацией
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // Поиск с сортировкой
    List<User> findByStatusOrderByCreatedAtDesc(UserStatus status);

    // Проверка существования
    boolean existsByEmail(String email);

    // Подсчет
    long countByStatus(UserStatus status);
}
```

### Кастомные методы

```java
// Репозиторий Post с кастомным интерфейсом
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // Найти посты пользователя
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Найти посты с заголовком содержащим текст (ignore case)
    List<Post> findByTitleContainingIgnoreCase(String titlePart);

    // Найти посты созданные после даты
    List<Post> findByCreatedAtAfter(LocalDateTime date);

    // Найти посты с определенным количеством комментариев
    @Query("SELECT p FROM Post p WHERE SIZE(p.comments) >= :minComments")
    List<Post> findPostsWithMinComments(@Param("minComments") int minComments);

    // Обновление статуса
    @Modifying
    @Query("UPDATE Post p SET p.status = :status WHERE p.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") PostStatus status);

    // Удаление старых постов
    @Modifying
    @Query("DELETE FROM Post p WHERE p.createdAt < :cutoffDate")
    int deleteOldPosts(@Param("cutoffDate") LocalDateTime cutoffDate);
}

// Кастомный интерфейс для сложных запросов
public interface PostRepositoryCustom {
    List<Post> findPopularPosts(int minLikes, LocalDateTime since);
}

public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Post> findPopularPosts(int minLikes, LocalDateTime since) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> post = query.from(Post.class);

        query.select(post)
             .where(cb.and(
                 cb.greaterThanOrEqualTo(post.get("likes"), minLikes),
                 cb.greaterThanOrEqualTo(post.get("createdAt"), since)
             ))
             .orderBy(cb.desc(post.get("likes")));

        return entityManager.createQuery(query).getResultList();
    }
}
```

### Specifications (динамические запросы)

```java
public class UserSpecifications {

    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<User> usernameLike(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")),
                                          "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> cb.greaterThan(root.get("createdAt"), date);
    }

    public static Specification<User> hasRole(String roleName) {
        return (root, query, cb) -> cb.isMember(roleName,
            cb.treat(root.join("roles", JoinType.LEFT), Role.class).get("name"));
    }
}

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
                                      JpaSpecificationExecutor<User> {
    // JpaSpecificationExecutor добавляет методы для работы со спецификациями
}

// Использование
List<User> activeUsers = userRepository.findAll(
    where(UserSpecifications.hasStatus(UserStatus.ACTIVE))
    .and(UserSpecifications.createdAfter(LocalDateTime.now().minusDays(30)))
);

List<User> admins = userRepository.findAll(
    UserSpecifications.hasRole("ADMIN")
);
```

## Запросы и JPQL

### JPQL (Java Persistence Query Language)

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Простые JPQL запросы
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);

    // С проекцией
    @Query("SELECT p.name, p.price FROM Product p WHERE p.price > :minPrice")
    List<Object[]> findNamesAndPrices(@Param("minPrice") BigDecimal minPrice);

    // С DTO проекцией
    @Query("SELECT new com.example.ProductDTO(p.name, p.price, p.category.name) " +
           "FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<ProductDTO> findProductsInPriceRange(@Param("min") BigDecimal min,
                                            @Param("max") BigDecimal max);

    // С агрегацией
    @Query("SELECT p.category.name, COUNT(p), AVG(p.price) " +
           "FROM Product p GROUP BY p.category.name")
    List<Object[]> getCategoryStatistics();

    // С подзапросами
    @Query("SELECT p FROM Product p WHERE p.price > " +
           "(SELECT AVG(p2.price) FROM Product p2 WHERE p2.category = p.category)")
    List<Product> findProductsAboveAverageInCategory();

    // С обновлением
    @Modifying
    @Query("UPDATE Product p SET p.price = p.price * :factor WHERE p.category.id = :categoryId")
    int updatePricesByCategory(@Param("categoryId") Long categoryId,
                             @Param("factor") BigDecimal factor);

    // С нативным SQL
    @Query(value = "SELECT * FROM products WHERE created_at > :date", nativeQuery = true)
    List<Product> findRecentProducts(@Param("date") LocalDateTime date);
}
```

### Named Queries

```java
@Entity
@NamedQueries({
    @NamedQuery(name = "Product.findByPriceRange",
                query = "SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max"),
    @NamedQuery(name = "Product.findTopSelling",
                query = "SELECT p FROM Product p ORDER BY p.salesCount DESC")
})
public class Product {
    // ...
}

// Использование
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    List<Product> findTopSelling(Pageable pageable);
}
```

## Criteria API

### Базовое использование

```java
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findProductsByCriteria(String name, BigDecimal minPrice,
                                              BigDecimal maxPrice, String category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Динамические условия
        if (name != null && !name.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(product.get("name")),
                                 "%" + name.toLowerCase() + "%"));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get("price"), maxPrice));
        }

        if (category != null) {
            predicates.add(cb.equal(product.get("category").get("name"), category));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.asc(product.get("name")));

        return entityManager.createQuery(query).getResultList();
    }
}
```

### Сложные запросы

```java
public List<Product> findAdvancedProducts() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> query = cb.createQuery(Product.class);
    Root<Product> product = query.from(Product.class);

    // JOIN
    Join<Product, Category> category = product.join("category", JoinType.LEFT);

    // Subquery для поиска продуктов с максимальной ценой в категории
    Subquery<BigDecimal> maxPriceSubquery = query.subquery(BigDecimal.class);
    Root<Product> subProduct = maxPriceSubquery.from(Product.class);
    maxPriceSubquery.select(cb.max(subProduct.get("price")))
                   .where(cb.equal(subProduct.get("category"), category));

    // Основной запрос
    query.select(product)
         .where(cb.and(
             cb.equal(product.get("active"), true),
             cb.equal(product.get("price"), maxPriceSubquery)
         ))
         .orderBy(cb.desc(product.get("price")));

    return entityManager.createQuery(query).getResultList();
}
```

## Native SQL запросы

### Простые native запросы

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Простой native SQL
    @Query(value = "SELECT * FROM products WHERE price > :price", nativeQuery = true)
    List<Product> findExpensiveProducts(@Param("price") BigDecimal price);

    // С пагинацией
    @Query(value = "SELECT * FROM products ORDER BY created_at DESC",
           countQuery = "SELECT count(*) FROM products",
           nativeQuery = true)
    Page<Product> findAllProducts(Pageable pageable);

    // С именованными параметрами
    @Query(value = "SELECT * FROM products WHERE category_id = :categoryId " +
                   "AND price BETWEEN :minPrice AND :maxPrice", nativeQuery = true)
    List<Product> findByCategoryAndPriceRange(@Param("categoryId") Long categoryId,
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice);
}
```

### Сложные native запросы

```java
@Repository
public interface AnalyticsRepository {

    // С агрегацией
    @Query(value =
        "SELECT DATE(created_at) as date, COUNT(*) as count, SUM(amount) as total " +
        "FROM orders " +
        "WHERE created_at >= :startDate " +
        "GROUP BY DATE(created_at) " +
        "ORDER BY date DESC",
        nativeQuery = true)
    List<Object[]> getDailyOrderStats(@Param("startDate") LocalDateTime startDate);

    // С CTE (Common Table Expression)
    @Query(value =
        "WITH category_stats AS (" +
        "  SELECT c.name, COUNT(p.id) as product_count, AVG(p.price) as avg_price " +
        "  FROM categories c " +
        "  LEFT JOIN products p ON c.id = p.category_id " +
        "  GROUP BY c.id, c.name" +
        ") " +
        "SELECT * FROM category_stats WHERE product_count > 0 " +
        "ORDER BY avg_price DESC",
        nativeQuery = true)
    List<Object[]> getCategoryStatistics();

    // С оконными функциями
    @Query(value =
        "SELECT p.name, p.price, " +
        "       RANK() OVER (ORDER BY p.price DESC) as price_rank, " +
        "       AVG(p.price) OVER (PARTITION BY c.name) as category_avg " +
        "FROM products p " +
        "JOIN categories c ON p.category_id = c.id " +
        "ORDER BY price_rank",
        nativeQuery = true)
    List<Object[]> getProductRanking();
}
```

### EntityManager для сложных запросов

```java
@Service
public class ComplexQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductDTO> findComplexProducts(String searchTerm, BigDecimal minPrice) {
        String sql = """
            SELECT p.id, p.name, p.price, c.name as category_name,
                   COUNT(o.id) as order_count
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN order_items oi ON p.id = oi.product_id
            LEFT JOIN orders o ON oi.order_id = o.id AND o.status = 'COMPLETED'
            WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(:searchTerm))
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            GROUP BY p.id, p.name, p.price, c.name
            HAVING COUNT(o.id) > 0
            ORDER BY COUNT(o.id) DESC, p.price DESC
            """;

        Query query = entityManager.createNativeQuery(sql, "ProductDTO")
            .setParameter("searchTerm", searchTerm != null ? "%" + searchTerm + "%" : null)
            .setParameter("minPrice", minPrice);

        return query.getResultList();
    }
}
```

## Кэширование

### First Level Cache (L1)

```java
@Service
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    public User getUserById(Long id) {
        // Первый вызов - запрос в БД
        User user1 = entityManager.find(User.class, id);

        // Второй вызов - возврат из L1 cache
        User user2 = entityManager.find(User.class, id);

        // user1 == user2 (true, тот же объект)
        return user1;
    }

    public void demonstrateCache() {
        User user = entityManager.find(User.class, 1L);

        // Обновление в кэше
        user.setName("Updated Name");

        // Обновление в БД
        entityManager.flush();

        // Новое получение - тот же объект с обновленным именем
        User sameUser = entityManager.find(User.class, 1L);
        // sameUser.getName() == "Updated Name"
    }
}
```

### Second Level Cache (L2)

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          use_query_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory

# Ehcache 3
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>

<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
```

```java
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "user")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    // ...
}

// Использование
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Кэширование результатов запроса
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<User> findByDepartmentId(Long departmentId);
}

// Управление кэшем
@Service
public class CacheService {

    @PersistenceContext
    private EntityManager entityManager;

    public void evictUserFromCache(Long userId) {
        entityManager.getEntityManagerFactory().getCache()
            .evict(User.class, userId);
    }

    public void evictAllUsers() {
        entityManager.getEntityManagerFactory().getCache()
            .evict(User.class);
    }

    public void clearAllCaches() {
        entityManager.getEntityManagerFactory().getCache()
            .evictAll();
    }
}
```

### Query Cache

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Кэширование результатов запросов
    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true"),
        @QueryHint(name = "org.hibernate.cacheRegion", value = "productQuery")
    })
    List<Product> findByCategoryId(Long categoryId);

    // Кэширование с параметрами
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
```

## Транзакции

### Декларативные транзакции

```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InventoryService inventoryService;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. Создать заказ
        Order order = new Order(request.getCustomerId(), request.getItems());
        order = orderRepository.save(order);

        try {
            // 2. Обработать платеж
            PaymentResult payment = paymentService.processPayment(order);

            // 3. Резервировать инвентарь
            inventoryService.reserveItems(order.getItems());

            // 4. Подтвердить заказ
            order.setStatus(OrderStatus.CONFIRMED);
            order.setPaymentId(payment.getId());

            return orderRepository.save(order);

        } catch (PaymentException e) {
            // Транзакция откатится автоматически
            throw new OrderCreationException("Payment failed", e);
        } catch (InventoryException e) {
            // Транзакция откатится автоматически
            throw new OrderCreationException("Inventory reservation failed", e);
        }
    }
}
```

### Настройки транзакций

```java
@Service
public class AdvancedTransactionService {

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        // Только чтение - оптимизация для БД
        return orderRepository.findAll();
    }

    @Transactional(timeout = 30) // 30 секунд таймаут
    public Order processLargeOrder(Order order) {
        // Долгая операция с таймаутом
        return orderRepository.save(order);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateCriticalData() {
        // Максимальная изоляция для критических операций
        // Может вызвать deadlock - использовать осторожно
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditAction(String action) {
        // Новая транзакция независимо от текущей
        auditLogRepository.save(new AuditLog(action));
    }

    @Transactional(propagation = Propagation.NEVER)
    public void sendNotification() {
        // Не должна выполняться в транзакции
        emailService.sendNotification();
    }
}
```

### Программные транзакции

```java
@Service
public class ProgrammaticTransactionService {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public Order createOrderProgrammatically(CreateOrderRequest request) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setTimeout(30);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            Order order = orderRepository.save(new Order(request));

            // Бизнес логика
            paymentService.processPayment(order);
            inventoryService.reserveItems(order);

            transactionManager.commit(status);
            return order;

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new OrderCreationException("Failed to create order", e);
        }
    }
}
```

## Производительность

### N+1 проблема

```java
// Плохо - N+1 запросов
@Service
public class BadService {

    public List<PostDTO> getPostsWithComments() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
            .map(post -> {
                // Отдельный запрос для каждого поста!
                List<Comment> comments = commentRepository.findByPostId(post.getId());
                return new PostDTO(post, comments);
            })
            .collect(Collectors.toList());
    }
}

// Хорошо - JOIN FETCH или Entity Graph
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // JOIN FETCH
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.author.id = :authorId")
    List<Post> findPostsWithCommentsByAuthor(@Param("authorId") Long authorId);

    // Entity Graph
    @EntityGraph(attributePaths = {"comments", "comments.author"})
    List<Post> findByAuthorId(Long authorId);
}

// В классе сущности
@Entity
@NamedEntityGraph(name = "Post.withComments",
                  attributeNodes = @NamedAttributeNode("comments"))
public class Post {
    // ...
}
```

### Пагинация

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Пагинация с сортировкой
    Page<Product> findByCategoryIdOrderByPriceDesc(Long categoryId, Pageable pageable);

    // Кастомная пагинация
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryPaged(@Param("categoryId") Long categoryId, Pageable pageable);
}

// Использование
Pageable pageable = PageRequest.of(0, 20, Sort.by("price").descending());
Page<Product> products = productRepository.findByCategoryIdOrderByPriceDesc(categoryId, pageable);

System.out.println("Total pages: " + products.getTotalPages());
System.out.println("Total elements: " + products.getTotalElements());
System.out.println("Current page: " + products.getNumber());
```

### Batch операции

```java
@Service
public class BatchService {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void batchInsert(List<User> users) {
        int batchSize = 50;

        for (int i = 0; i < users.size(); i++) {
            entityManager.persist(users.get(i));

            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear(); // Очистка L1 cache
            }
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public void batchUpdate(List<Long> userIds, UserStatus newStatus) {
        List<List<Long>> batches = Lists.partition(userIds, 1000);

        for (List<Long> batch : batches) {
            entityManager.createQuery(
                "UPDATE User u SET u.status = :status WHERE u.id IN :ids")
                .setParameter("status", newStatus)
                .setParameter("ids", batch)
                .executeUpdate();

            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

### Оптимизация Hibernate

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
        batch_versioned_data: true

        # Connection pool
        connection:
          provider_disables_autocommit: true

        # Statistics для мониторинга
        generate_statistics: true

        # Query plan cache
        query:
          plan_cache_max_size: 2048
          plan_parameter_metadata_max_size: 128

# Enable SQL logging
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Индексы и статистика

```java
@Entity
@Table(indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_status_created", columnList = "status, created_at")
})
public class User {

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

## Миграции базы данных

### Flyway

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

```text
src/main/resources/db/migration/
├── V1.0.0__Create_users_table.sql
├── V1.0.1__Add_user_status.sql
├── V1.1.0__Create_orders_table.sql
└── V1.2.0__Add_indexes.sql
```

```sql
-- V1.0.0__Create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- V1.1.0__Create_orders_table.sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
```

### Liquibase

```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
```

```xml
<!-- db/changelog/db.changelog-master.xml -->
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.17.xsd">

    <include file="changesets/01-create-users-table.xml"/>
    <include file="changesets/02-add-user-constraints.xml"/>
</databaseChangeLog>
```

```xml
<!-- changesets/01-create-users-table.xml -->
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.17.xsd">

    <changeSet id="create-users-table" author="developer">
        <createTable tableName="users">
            <column name="id" type="BIGSERIAL">
                <constraints primaryKey="true"/>
            </column>
            <column name="username" type="VARCHAR(50)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="email" type="VARCHAR(255)">
                <constraints nullable="false" unique="true"/>
            </column>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

## Аудит и versioning

### Hibernate Envers

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-envers</artifactId>
</dependency>
```

```java
@Entity
@Audited
public class Product {

    @Id
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    private Category category;

    @NotAudited
    private String internalNotes; // Не аудитируется

    // Audit fields
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

// Репозиторий для аудита
@Repository
public interface ProductAuditRepository extends JpaRepository<ProductAudit, Long, Long> {

    List<ProductAudit> findByIdAndRevisionType(Long id, RevisionType revisionType);

    @Query("SELECT a FROM ProductAudit a WHERE a.id = :id")
    List<ProductAudit> findRevisions(@Param("id") Long id);
}
```

### Кастомный аудит

```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditableEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Version
    private Long version;
}

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext()
            .getAuthentication())
            .map(Authentication::getName);
    }
}
```

## Spring Boot интеграция

### Полная конфигурация

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000

  jpa:
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.SnakeCasePhysicalNamingStrategy
        implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 25
          order_inserts: true
          order_updates: true
        generate_statistics: true
        query:
          plan_cache_max_size: 2048

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.springframework.orm.jpa: DEBUG
    org.springframework.transaction: DEBUG
```

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableTransactionManagement
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(false);
        adapter.setGenerateDdl(false);
        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(adapter);
        factory.setPackagesToScan("com.example.entity");

        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.jdbc.batch_size", "25");
        jpaProperties.setProperty("hibernate.order_inserts", "true");
        jpaProperties.setProperty("hibernate.order_updates", "true");
        jpaProperties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        jpaProperties.setProperty("hibernate.generate_statistics", "true");

        factory.setJpaProperties(jpaProperties);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext()
            .getAuthentication())
            .map(Authentication::getName);
    }
}
```

## Тестирование

### Unit тестирование репозиториев

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User("john@example.com", "John Doe");
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldFindActiveUsers() {
        // Given
        User activeUser = new User("active@example.com", "Active User");
        activeUser.setStatus(UserStatus.ACTIVE);

        User inactiveUser = new User("inactive@example.com", "Inactive User");
        inactiveUser.setStatus(UserStatus.INACTIVE);

        entityManager.persistAndFlush(activeUser);
        entityManager.persistAndFlush(inactiveUser);

        // When
        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("active@example.com");
    }
}
```

### Интеграционное тестирование

```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrderWithTransaction() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
            customerId,
            List.of(new OrderItem(productId, 2))
        );

        // When
        Order order = orderService.createOrder(request);

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(orderRepository.findById(order.getId())).isPresent();
    }
}
```

### Тестирование транзакций

```java
@SpringBootTest
class TransactionTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldRollbackTransactionOnFailure() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(invalidCustomerId, items);

        // When & Then
        assertThrows(OrderCreationException.class, () -> {
            orderService.createOrder(request);
        });

        // Verify rollback - заказ не должен быть сохранен
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders).noneMatch(order ->
            order.getCustomerId().equals(invalidCustomerId));
    }
}
```

## Решение проблем

### Распространенные проблемы

#### 1. LazyInitializationException

```text
Проблема: Доступ к лениво загруженной коллекции вне сессии
Решение:
- Использовать JOIN FETCH в запросах
- Использовать Entity Graph
- Инициализировать коллекцию в сервисе
- Использовать DTO проекции
```

```java
// Решение 1: JOIN FETCH
@Query("SELECT p FROM Post p JOIN FETCH p.comments WHERE p.id = :id")
Optional<Post> findPostWithComments(@Param("id") Long id);

// Решение 2: Entity Graph
@EntityGraph(attributePaths = "comments")
Optional<Post> findById(Long id);

// Решение 3: Инициализация в сервисе
@Service
public class PostService {

    public PostDTO getPostWithComments(Long id) {
        Post post = postRepository.findById(id).orElseThrow();

        // Инициализация в рамках сессии
        Hibernate.initialize(post.getComments());

        return convertToDTO(post);
    }
}
```

#### 2. N+1 проблема

```text
Проблема: Множество дополнительных запросов
Решение:
- Использовать JOIN FETCH
- Использовать @BatchSize
- Использовать DTO проекции
- Настроить fetch стратегии
```

#### 3. OptimisticLockException

```text
Проблема: Конфликты версий при одновременном обновлении
Решение:
- Добавить @Version поле
- Обработать исключение и retry
- Использовать pessimistic locking для критичных операций
```

```java
@Service
@Retryable(value = OptimisticLockException.class, maxAttempts = 3)
public class VersionedService {

    @Transactional
    public void updateEntity(Long id, UpdateData data) {
        Entity entity = entityRepository.findById(id).orElseThrow();
        entity.update(data);
        entityRepository.save(entity);
    }
}
```

#### 4. Connection pool exhaustion

```text
Проблема: Исчерпание пула соединений
Решение:
- Увеличить размер пула
- Оптимизировать запросы
- Использовать read replicas
- Настроить timeouts
```

#### 5. Slow queries

```text
Проблема: Медленные запросы
Решение:
- Добавить индексы
- Использовать EXPLAIN PLAN
- Оптимизировать JPQL
- Кэшировать частые запросы
- Использовать нативные запросы для сложной логики
```

### Диагностика

#### Hibernate Statistics

```java
@Configuration
public class HibernateStatsConfig {

    @Bean
    public HibernateStatsInterceptor hibernateStatsInterceptor() {
        return new HibernateStatsInterceptor();
    }

    @Bean
    public StatisticsService statisticsService(SessionFactory sessionFactory) {
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        return new StatisticsService() {
            public String getStats() {
                return String.format(
                    "Queries: %d, Entities: %d, Collections: %d, Time: %dms",
                    statistics.getQueryExecutionCount(),
                    statistics.getEntityLoadCount(),
                    statistics.getCollectionLoadCount(),
                    statistics.getQueryExecutionMaxTime()
                );
            }
        };
    }
}
```

#### SQL Logging

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.springframework.orm.jpa: DEBUG
```

#### VisualVM для профилирования

```bash
# Запуск приложения с JMX
java -Dcom.sun.management.jmxremote \
     -Dcom.sun.management.jmxremote.port=9999 \
     -Dcom.sun.management.jmxremote.authenticate=false \
     -Dcom.sun.management.jmxremote.ssl=false \
     -jar myapp.jar
```

### Полезные команды

```bash
# Проверка структуры БД
./mvnw hibernate:schema-validate

# Генерация schema
./mvnw hibernate:schema-generate

# Вывод всех entities
./mvnw hibernate:entitymanager:show

# Hibernate statistics
./mvnw hibernate:statistics
```


## Заключение

**Hibernate** и **Spring Data JPA** предоставляют мощную **ORM** платформу для **Java** приложений:**

**Ключевые возможности:**
- **Маппинг сущностей** — декларативное описание БД схемы
- **Репозитории** — готовые **CRUD** операции и кастомные запросы
- **JPQL и Criteria** — типобезопасные запросы
- **Кэширование** — оптимизация производительности
- **Транзакции** — декларативное управление

**Лучшие практики:**
- Использовать **JPA** репозитории для простых операций
- **JPQL** для сложных запросов с проекциями
- **Criteria API** для динамических запросов
- **Native SQL** только при необходимости
- Настраивать **fetch** стратегии правильно
- Использовать миграции для управления схемой

**Производительность:**
- Избегать N+1 проблемы
- Использовать **batch** операции
- Настраивать кэширование
- Мониторить запросы
- Оптимизировать индексы

**Hibernate** остается стандартом для **Java persistence**, предоставляя баланс между простотой использования и мощностью функциональности.

## См. также

- [**Spring Data JPA**](spring-data-jpa.md) — **Spring Data JPA** основы
- [**Spring Boot**](../../spring/spring-boot.md) — **Spring Boot** интеграция
- [Spring Boot](../../spring/spring-boot.md) — настройка datasource и JPA через Spring Boot