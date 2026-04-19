---
title: "Spring Data: JPA, JDBC и работа с данными"
description: "Практическое руководство по Spring Data: репозитории, запросы, связи сущностей, N+1, транзакции, аудит, пагинация, Specifications и типичные ошибки."
tags:
  - spring
  - spring-data
  - jpa
  - hibernate
  - jdbc
  - transactions
  - java
difficulty: "intermediate"
prerequisites: ["spring-core.md"]
next: ["spring-security.md"]
updated: "2026-04-12"
---

# Spring Data: JPA, JDBC и работа с данными

Практическое руководство по Spring Data для backend-разработчиков: абстракция репозиториев, JPA-запросы, связи сущностей, решение проблемы N+1, транзакции, аудит и Spring Data JDBC.

## Полезные ссылки

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/)
- [Spring Data JDBC Reference](https://docs.spring.io/spring-data/jdbc/reference/)
- [Hibernate ORM Documentation](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)

## Содержание

- [Иерархия репозиториев](#иерархия-репозиториев)
- [Query-методы](#query-методы)
- [Связи сущностей](#связи-сущностей)
- [Проблема N+1 и решения](#проблема-n1-и-решения)
- [Пагинация и сортировка](#пагинация-и-сортировка)
- [Аудит](#аудит)
- [Транзакции](#транзакции)
- [Spring Data JDBC](#spring-data-jdbc)
- [Specifications и QueryDSL](#specifications-и-querydsl)
- [Типичные ошибки](#типичные-ошибки)

## Иерархия репозиториев

```text
Repository<T, ID>                    — маркерный интерфейс
└── CrudRepository<T, ID>           — CRUD (save, findById, delete, findAll)
    └── PagingAndSortingRepository  — + пагинация и сортировка
        └── JpaRepository<T, ID>    — + flush, saveAll, batch-delete, Example API
```

```java
public interface UserRepository extends JpaRepository<User, Long> {
}

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}
```

| Репозиторий | Когда использовать |
|------------|-------------------|
| `CrudRepository` | Базовый CRUD без пагинации |
| `JpaRepository` | Полный набор JPA-операций (flush, batch, Example API) |
| `PagingAndSortingRepository` | Пагинация без JPA-специфики |
| Кастомный (extends `Repository`) | Выставить только конкретные методы |

## Query-методы

### Derived Queries (по имени метода)

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);
    List<Product> findByPriceBetweenOrderByNameAsc(BigDecimal min, BigDecimal max);
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String fragment);
    boolean existsBySku(String sku);
    long countByCategoryId(Long categoryId);
    List<Product> findTop10ByOrderByCreatedAtDesc();
}
```

### @Query (JPQL и native SQL)

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL — работает с сущностями, а не таблицами
    @Query("SELECT o FROM Order o WHERE o.customer.email = :email AND o.status = :status")
    List<Order> findByCustomerEmailAndStatus(@Param("email") String email,
                                             @Param("status") OrderStatus status);

    // Native SQL — database-специфичные функции
    @Query(value = "SELECT o.* FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
                   "GROUP BY o.id HAVING SUM(oi.quantity) > :min", nativeQuery = true)
    List<Order> findBulkOrders(@Param("min") int min);

    // Модифицирующий запрос — обязательно @Modifying
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id IN :ids")
    int updateStatusByIds(@Param("status") OrderStatus status, @Param("ids") List<Long> ids);
}
```

### Проекции

```java
// Interface-based projection
public interface ProductSummary {
    String getName();
    BigDecimal getPrice();
}

// Class-based projection (DTO)
public record ProductDto(String name, BigDecimal price) {}

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<ProductSummary> findByCategory(String category);

    @Query("SELECT new com.example.dto.ProductDto(p.name, p.price) FROM Product p WHERE p.active = true")
    List<ProductDto> findActiveProducts();
}
```

## Связи сущностей

### @ManyToOne / @OneToMany

Владелец связи (`mappedBy`) всегда на стороне `@ManyToOne`:

```java
@Entity
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY обязательно для ManyToOne!
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}

@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String productName;
    private int quantity;
}
```

### @ManyToMany

```java
@Entity
public class Student {
    @ManyToMany
    @JoinTable(name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>(); // Set, не List — избегает bag-дублей
}
```

### Fetch Types и Cascade

| Аннотация | FetchType по умолчанию | Рекомендация |
|-----------|----------------------|--------------|
| `@ManyToOne` | **EAGER** | Всегда ставить `LAZY` |
| `@OneToOne` | **EAGER** | Ставить `LAZY` (нюансы с proxy) |
| `@OneToMany` | LAZY | Оставить |
| `@ManyToMany` | LAZY | Оставить |

| Cascade | Эффект |
|---------|--------|
| `PERSIST` | Сохранение родителя сохраняет детей |
| `MERGE` | Слияние родителя обновляет детей |
| `REMOVE` | Удаление родителя удаляет детей |
| `ALL` | Все + DETACH + REFRESH |

**`orphanRemoval = true`** — удаляет дочернюю сущность из БД при удалении из коллекции родителя. Не путать с `CascadeType.REMOVE` (срабатывает только при удалении самого родителя).

## Проблема N+1 и решения

При загрузке N сущностей с LAZY-связями каждая связь загружается отдельным запросом:

```java
// 1 запрос на заказы + N запросов на items
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
    order.getItems().size(); // SELECT * FROM order_items WHERE order_id = ? (каждый раз!)
}
```

### JOIN FETCH

```java
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
List<Order> findByStatusWithItems(@Param("status") OrderStatus status);

// ВНИМАНИЕ: множественный JOIN FETCH с List-коллекциями вызывает MultipleBagFetchException
// Решение: Set вместо List или разделить на два запроса
```

### @EntityGraph

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"items", "customer"})
    List<Order> findByStatus(OrderStatus status);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findWithItemsById(Long id);
}
```

### @BatchSize (Hibernate)

```java
@OneToMany(mappedBy = "order")
@BatchSize(size = 20) // WHERE order_id IN (?, ?, ...) — пачками по 20
private List<OrderItem> items;

// Или глобально: spring.jpa.properties.hibernate.default_batch_fetch_size: 20
```

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| `JOIN FETCH` | Один запрос, точный контроль | Декартово произведение при нескольких коллекциях |
| `@EntityGraph` | Декларативно, переиспользуемо | Менее гибко для условных запросов |
| `@BatchSize` | Прозрачно, не меняет запросы | Сокращает N+1 до N/batch, а не устраняет |

## Пагинация и сортировка

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(String category, Pageable pageable);
    Slice<Product> findByPriceGreaterThan(BigDecimal price, Pageable pageable);
}

// Использование
Pageable pageable = PageRequest.of(0, 20, Sort.by("price").ascending());
Page<Product> page = productRepository.findByCategory("electronics", pageable);

// Составная сортировка
Sort sort = Sort.by(Sort.Order.desc("featured"), Sort.Order.asc("price"));
```

| | `Page<T>` | `Slice<T>` |
|-|-----------|------------|
| COUNT-запрос | Да | Нет |
| `getTotalElements()` | Да | Недоступно |
| Когда использовать | UI с номерами страниц | Бесконечная прокрутка |

## Аудит

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
```

Активация и конфигурация `AuditorAware`:

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName);
    }
}
```

Для повторного использования вынесите аудит-поля в `@MappedSuperclass`:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {
    @CreatedDate @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

## Транзакции

### Основы @Transactional

```java
@Transactional // propagation=REQUIRED, isolation=DEFAULT, rollbackFor=RuntimeException
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    Account from = accountRepository.findById(fromId).orElseThrow();
    Account to = accountRepository.findById(toId).orElseThrow();
    from.debit(amount);
    to.credit(amount);
    // Коммит при выходе из метода. Rollback при RuntimeException.
}
```

### Propagation

| Propagation | Поведение |
|------------|-----------|
| `REQUIRED` (default) | Использует текущую или создаёт новую |
| `REQUIRES_NEW` | Всегда новая (текущая приостанавливается) |
| `NESTED` | Вложенная (savepoint); откат не затрагивает внешнюю |
| `MANDATORY` | Требует существующей, иначе исключение |
| `SUPPORTS` | Есть TX — участвует, нет — без неё |
| `NOT_SUPPORTED` | Приостанавливает текущую |
| `NEVER` | Исключение, если есть активная TX |

```java
// Аудит-лог не должен зависеть от основной транзакции
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void logAuditEvent(AuditEvent event) {
    auditRepository.save(event); // Сохранится даже при откате вызывающей TX
}
```

### Isolation

| Isolation | Dirty Read | Non-Repeatable Read | Phantom Read |
|-----------|-----------|-------------------|-------------|
| `READ_UNCOMMITTED` | Да | Да | Да |
| `READ_COMMITTED` | Нет | Да | Да |
| `REPEATABLE_READ` | Нет | Нет | Да |
| `SERIALIZABLE` | Нет | Нет | Нет |

### rollbackFor и readOnly

```java
// Checked exceptions НЕ вызывают rollback по умолчанию!
@Transactional(rollbackFor = Exception.class)
public void importData(InputStream input) throws IOException { /* ... */ }

// readOnly — Hibernate не делает dirty checking, оптимизация чтения
@Transactional(readOnly = true)
public List<Product> getProducts() { return productRepository.findAll(); }
```

### Self-invocation (критическая ловушка)

```java
@Service
public class OrderService {
    // НЕ РАБОТАЕТ: вызов внутри класса обходит proxy, @Transactional игнорируется
    public void processAll(List<Order> orders) {
        for (Order order : orders) {
            processSingle(order); // Self-invocation!
        }
    }
    @Transactional
    public void processSingle(Order order) { /* ... */ }
    // РЕШЕНИЕ: вынести processSingle в отдельный бин
}
```

## Spring Data JDBC

Простая альтернатива JPA: нет lazy loading, нет кеша первого уровня, нет dirty checking. Работает с **агрегатами** в духе DDD.

```java
public class Order {
    @Id
    private Long id;
    private String customerName;
    private Set<OrderItem> items = new HashSet<>(); // Часть агрегата
}

public class OrderItem {
    private String productName;
    private int quantity;
    // Нет @Id — lifecycle управляется через Order (aggregate root)
}

public interface OrderRepository extends CrudRepository<Order, Long> {
    @Query("SELECT * FROM orders WHERE customer_name = :name")
    List<Order> findByCustomerName(@Param("name") String name);
}
```

| Критерий | Spring Data JPA | Spring Data JDBC |
|----------|----------------|-----------------|
| Lazy loading | Да | Нет (всегда eager) |
| Dirty checking | Да (auto flush) | Нет (явный save) |
| Кеш 1-го уровня | Да | Нет |
| Сложность | Высокая | Низкая |
| Связи | Любые навигационные | Только внутри агрегата |
| Подходит для | Сложные домены | DDD-агрегаты, микросервисы |

## Specifications и QueryDSL

### Specifications (Criteria API)

Для динамических запросов с комбинируемыми фильтрами:

```java
public interface ProductRepository extends JpaRepository<Product, Long>,
                                           JpaSpecificationExecutor<Product> {}

public class ProductSpecs {
    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }
    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> cb.between(root.get("price"), min, max);
    }
    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}

// Комбинирование
Specification<Product> spec = Specification.where(null);
if (filter.category() != null) spec = spec.and(ProductSpecs.hasCategory(filter.category()));
if (filter.name() != null) spec = spec.and(ProductSpecs.nameContains(filter.name()));
Page<Product> results = productRepository.findAll(spec, pageable);
```

### QueryDSL

Типобезопасная альтернатива (требует генерацию Q-классов):

```java
public interface ProductRepository extends JpaRepository<Product, Long>,
                                           QuerydslPredicateExecutor<Product> {}

QProduct p = QProduct.product;
BooleanExpression predicate = p.category.eq("electronics")
        .and(p.price.between(100, 500))
        .and(p.name.containsIgnoreCase("phone"));
Page<Product> results = productRepository.findAll(predicate, pageable);
```

## Типичные ошибки

| Ошибка | Причина | Решение |
|--------|---------|---------|
| `LazyInitializationException` | Доступ к LAZY-коллекции вне сессии | `JOIN FETCH`, `@EntityGraph`, DTO или `@Transactional` |
| `@Transactional` не работает | Self-invocation | Вынести в отдельный бин или `TransactionTemplate` |
| N+1 запросов | LAZY-связи по одной | `JOIN FETCH`, `@EntityGraph`, `@BatchSize` |
| `open-in-view=true` (default!) | Сессия открыта до View, маскирует N+1 | `spring.jpa.open-in-view=false` |
| `MultipleBagFetchException` | Несколько `JOIN FETCH` для `List` | `Set` вместо `List` или два запроса |
| Detached entity merge | Сущность из другой сессии | `merge()` или `findById` заново |
| Checked exception не откатывает TX | Rollback только на `RuntimeException` | `rollbackFor = Exception.class` |
| `save()` делает UPDATE вместо INSERT | Заполненный `@Id` = existing entity | `@GeneratedValue` или `Persistable<ID>` |
| `@Modifying` без sync | Persistence context устарел | `@Modifying(clearAutomatically = true)` |
