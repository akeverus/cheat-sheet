---
title: "ORM: Object-Relational Mapping"
description: "Комплексное руководство по технологиям ORM (Object-Relational Mapping) для Java - от основ до продвинутых техник."
tags:
  - databases
  - orm
  - orm-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **ORM**: **Object-Relational Mapping**

**Комплексное руководство по технологиям `ORM` (**Object-Relational Mapping**) для `Java` - от основ до продвинутых техник.**

## Полезные ссылки

### Официальная документация
- [Hibernate](https://hibernate.org/orm/documentation/) — **Hibernate ORM**
- [JPA Specification](https://jakarta.ee/specifications/persistence/) — **Jakarta Persistence API**
- [jOOQ](https://www.jooq.org/doc/) — **jOOQ type-safe SQL**

### См. также
- [Spring Data JPA](../../frameworks/java-frameworks/spring/spring-data-jpa.md) — интеграция **JPA** в **Spring**
- [jOOQ](../../libraries/java/java-jooq.md) — **type-safe SQL** библиотека

## Содержание

- [**ORM**: **Object-Relational Mapping**](#orm-object-relational-mapping)
- [Введение в **ORM**](#введение-в-orm)
  - [Что такое **ORM**?](#что-такое-orm)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [**JPA** (**Jakarta Persistence API**)](#jpa-jakarta-persistence-api)
  - [**Entity** классы](#entity-классы)
  - [**EntityManager**](#entitymanager)
  - [**JPQL** запросы](#jpql-запросы)
- [**Hibernate**](#hibernate)
  - [Конфигурация **Hibernate**](#конфигурация-hibernate)
  - [**Mapping** аннотации](#mapping-аннотации)
- [**Spring Data JPA**](#spring-data-jpa)
  - [**Repository** интерфейсы](#repository-интерфейсы)
- [**Best Practices**](#лучшие-практики)

## Введение в **ORM**

**ORM (**Object-Relational Mapping**)** — это техника программирования, которая позволяет работать с реляционными базами данных используя объектно-ориентированный подход.

### Что такое **ORM**?

**ORM** автоматически преобразует объекты **Java** в записи базы данных и обратно, избавляя разработчика от написания **SQL** запросов вручную.

### Преимущества и недостатки

**Преимущества:**
- Меньше **boilerplate** кода
- Типобезопасность
- Независимость от СУБД
- Автоматическое управление транзакциями

**Недостатки:**
- Кривая обучения
- Производительность для сложных запросов
- Сложность отладки

## **JPA** (**Jakarta `Persistence` API**)

### **Entity** классы

Пример **JPA**-сущности: маппинг таблицы **users**, первичный ключ с автоинкрементом, колонки **username** и **email** с ограничениями.

```java
/*
 * Entity класс для JPA
 * Entity представляет таблицу в базе данных
 */
@Entity  // Указывает что это JPA entity
@Table(name = "users")  // Имя таблицы в БД (если отличается от имени класса)
public class User {
    
    @Id  // Первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Автоматическая генерация ID
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    // name - имя колонки в БД
    // nullable = false - NOT NULL ограничение
    // unique = true - UNIQUE ограничение
    // length = 50 - максимальная длина VARCHAR(50)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "balance", precision = 10, scale = 2)
    // precision - общее количество цифр
    // scale - количество цифр после запятой
    private BigDecimal balance;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)  // Тип временной метки
    private Date createdAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // mappedBy указывает поле в Order которое ссылается на User
    // cascade = ALL - каскадные операции (при удалении User удалятся все Order)
    // fetch = LAZY - ленивая загрузка (загружаются только при обращении)
    private List<Order> orders;
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    // ... остальные геттеры и сеттеры
}

/*
 * Entity для заказов с отношением Many-to-One
 */
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // @ManyToOne - отношение многие-к-одному
    // @JoinColumn указывает колонку внешнего ключа
    private User user;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    
    // Геттеры и сеттеры
}
```

### **EntityManager**

```java
/*
 * Работа с EntityManager в JPA
 * EntityManager - основной интерфейс для работы с JPA
 */
public class UserService {
    
    @PersistenceContext  // Инъекция EntityManager через Spring
    private EntityManager entityManager;
    
    /
     * Сохранение нового пользователя
     */
    public User createUser(String username, String email) {
        // Создание нового entity объекта
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setBalance(BigDecimal.ZERO);
        user.setCreatedAt(new Date());
        
        // Начало транзакции
        entityManager.getTransaction().begin();
        
        try {
            // Сохранение entity в БД
            // persist() добавляет entity в persistence context
            entityManager.persist(user);
            
            // Подтверждение транзакции
            entityManager.getTransaction().commit();
            
            return user;
        } catch (Exception e) {
            // Откат транзакции при ошибке
            entityManager.getTransaction().rollback();
            throw e;
        }
    }
    
    /
     * Поиск пользователя по ID
     */
    public User findById(Long id) {
        // find() загружает entity по первичному ключу
        // Возвращает null если не найден
        return entityManager.find(User.class, id);
    }
    
    /
     * Обновление пользователя
     */
    public User updateUser(Long id, String newEmail) {
        entityManager.getTransaction().begin();
        
        try {
            // Загрузка entity для обновления
            User user = entityManager.find(User.class, id);
            if (user != null) {
                // Изменение свойств entity
                user.setEmail(newEmail);
                // merge() синхронизирует изменения с БД
                user = entityManager.merge(user);
            }
            
            entityManager.getTransaction().commit();
            return user;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }
    
    /
     * Удаление пользователя
     */
    public void deleteUser(Long id) {
        entityManager.getTransaction().begin();
        
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                // remove() удаляет entity из БД
                entityManager.remove(user);
            }
            
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }
}
```

### **JPQL** запросы

```java
/*
 * Использование JPQL (Java Persistence Query Language) в JPA
 * JPQL - объектно-ориентированный язык запросов (работает с entity, а не таблицами)
 */
public class UserRepository {
    
    private EntityManager entityManager;
    
    /
     * Простой JPQL запрос
     */
    public List<User> findAll() {
        // JPQL использует имена entity классов, а не таблиц
        String jpql = "SELECT u FROM User u";
        
        // Создание TypedQuery для типобезопасных запросов
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }
    
    /*
     * JPQL запрос с параметрами
     */
    public User findByUsername(String username) {
        // :username - именованный параметр
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);  // Установка значения параметра
        
        try {
            return query.getSingleResult();  // Ожидается один результат
        } catch (NoResultException e) {
            return null;  // Если результат не найден
        }
    }
    
    /
     * JPQL запрос с JOIN
     */
    public List<User> findUsersWithOrders() {
        // JOIN в JPQL использует навигацию по связям
        String jpql = """
            SELECT DISTINCT u 
            FROM User u 
            JOIN u.orders o 
            WHERE o.totalAmount > :minAmount
            """;
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("minAmount", new BigDecimal("100.00"));
        
        return query.getResultList();
    }
    
    /*
     * JPQL запрос с агрегацией
     */
    public BigDecimal getTotalBalance() {
        String jpql = "SELECT SUM(u.balance) FROM User u";
        
        TypedQuery<BigDecimal> query = entityManager.createQuery(jpql, BigDecimal.class);
        return query.getSingleResult();
    }
    
    /*
     * Нативный SQL запрос (когда JPQL недостаточно)
     */
    public List<Object[]> findUserStats() {
        // Нативный SQL для сложных запросов
        String sql = """
            SELECT u.username, COUNT(o.id) as order_count, SUM(o.total_amount) as total_spent
            FROM users u
            LEFT JOIN orders o ON u.id = o.user_id
            GROUP BY u.id, u.username
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results;
    }
}
```

## **Hibernate**

### Конфигурация **Hibernate**

```java
/*
 * Конфигурация Hibernate через Java код
 */
@Configuration
public class HibernateConfig {
    
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.example.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
    
    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "validate");
        return props;
    }
}
```

### **Mapping** аннотации

```java
/*
 * Расширенные mapping аннотации Hibernate
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category_id")
})
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_sequence", allocationSize = 1)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    // orphanRemoval = true - удаление дочерних записей при удалении родителя
    private List<OrderItem> orderItems;
    
    @Version  // Оптимистичная блокировка
    private Long version;
    
    @CreationTimestamp  // Автоматическая установка при создании
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp  // Автоматическое обновление при изменении
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

## **Spring Data JPA**

### **Repository** интерфейсы

```java
/*
 * Spring Data JPA Repository
 * Spring автоматически создает реализацию интерфейса
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Автоматическая генерация запросов по имени метода
    // Spring Data JPA анализирует имя метода и создает JPQL запрос
    User findByUsername(String username);  // SELECT u FROM User u WHERE u.username = ?
    
    List<User> findByEmailContaining(String email);  // WHERE u.email LIKE %email%
    
    List<User> findByBalanceGreaterThan(BigDecimal amount);  // WHERE u.balance > ?
    
    @Query("SELECT u FROM User u WHERE u.balance > :minBalance")
    List<User> findRichUsers(@Param("minBalance") BigDecimal minBalance);
    
    @Modifying  // Для UPDATE/DELETE запросов
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :id")
    void addBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
```

## Лучшие практики

1. **Использование `LAZY` загрузки** для связей
2. **Пакетная обработка** для множественных операций
3. **Использование `@Transactiona`l** для управления транзакциями
4. **Избегание N+1 проблемы** через **JOIN FETCH**


