---
title: "Hibernate: JPQL, HQL и Criteria API"
description: "Запросы в Hibernate и JPA: JPQL, HQL-расширения, именованные запросы, Criteria API, джойны, fetch-джойны, подзапросы, пагинация, проекции и практические примеры."
tags:
  - databases
  - orm
  - hibernate
  - jpa
  - jpql
  - hql
  - criteria-api
  - queries
difficulty: "intermediate"
updated: "2026-04-20"
---
# Hibernate: JPQL, HQL и Criteria API

JPQL — объектно-ориентированный язык запросов JPA. HQL — его расширение от Hibernate. Criteria API — типобезопасная альтернатива для динамических запросов. Шпаргалка охватывает все три подхода с упором на практику.

Базовые концепции ORM: [[orm-basics|ORM Basics]]. Связи между сущностями: [[hibernate-relationships|Hibernate Relationships]]. Spring-обёртки над JPA: [[spring-data-jpa-interview|Spring Data JPA]].

## Содержание

- [JPQL vs HQL vs Criteria API](#jpql-vs-hql-vs-criteria-api)
- [Базовые JPQL-запросы](#базовые-jpql-запросы)
- [Джойны (JOIN, JOIN FETCH)](#джойны-join-join-fetch)
  - [Обычный JOIN](#обычный-join)
  - [JOIN FETCH — решение N+1](#join-fetch-решение-n1)
  - [JOIN ON — дополнительные условия джойна](#join-on-дополнительные-условия-джойна)
- [Подзапросы](#подзапросы)
- [Именованные запросы](#именованные-запросы)
- [Пагинация и сортировка](#пагинация-и-сортировка)
- [Проекции и DTO](#проекции-и-dto)
- [Criteria API](#criteria-api)
- [Динамические запросы через Criteria](#динамические-запросы-через-criteria)
- [Criteria Tuple и мультиселект](#criteria-tuple-и-мультиселект)
- [UPDATE и DELETE через JPQL](#update-и-delete-через-jpql)
- [Практические советы](#практические-советы)
- [Типичные ошибки](#типичные-ошибки)
- [См. также](#см-также)

## JPQL vs HQL vs Criteria API

| Критерий | JPQL | HQL | Criteria API |
|----------|------|-----|--------------|
| Стандарт | JPA (Jakarta EE) | Hibernate-расширение | JPA |
| Синтаксис | Строки | Строки | Java-объекты |
| Типобезопасность | Нет | Нет | Да (с Metamodel) |
| Динамические условия | Сложно | Сложно | Просто |
| Читаемость | Высокая | Высокая | Низкая |
| Переносимость | Высокая | Средняя | Высокая |

**Когда что использовать:**
- JPQL — стандартные запросы с фиксированной структурой.
- HQL — нужны Hibernate-специфичные функции (например, `FILTER`, `elements()`).
- Criteria API — динамические фильтры (поисковые формы, pagination API).

---

## Базовые JPQL-запросы

Ключевое отличие JPQL от SQL: работаем с именами **классов** и **полей**, а не с таблицами и колонками.

```java
// Получить все сущности
TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
List<User> users = query.getResultList();

// Условие WHERE с named parameter
TypedQuery<User> query = em.createQuery(
    "SELECT u FROM User u WHERE u.email = :email", User.class);
query.setParameter("email", "ivan@example.com");
User user = query.getSingleResult(); // NoResultException если нет, NonUniqueResultException если несколько

// Positional parameters (лучше избегать — не читаемо)
TypedQuery<User> q = em.createQuery(
    "SELECT u FROM User u WHERE u.age > ?1 AND u.active = ?2", User.class);
q.setParameter(1, 18);
q.setParameter(2, true);
```

**Агрегатные функции:**

```java
// COUNT, SUM, AVG, MIN, MAX
TypedQuery<Long> countQuery = em.createQuery(
    "SELECT COUNT(u) FROM User u WHERE u.active = true", Long.class);
Long count = countQuery.getSingleResult();

// GROUP BY + HAVING
List<Object[]> result = em.createQuery(
    "SELECT u.department, COUNT(u) FROM User u " +
    "GROUP BY u.department HAVING COUNT(u) > 5")
    .getResultList();
```

**Функции работы со строками:**

```java
em.createQuery(
    "SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(:name)", User.class)
  .setParameter("name", "%ivan%")
  .getResultList();

// CONCAT, SUBSTRING, LENGTH, TRIM, UPPER, LOWER
em.createQuery(
    "SELECT CONCAT(u.firstName, ' ', u.lastName) FROM User u", String.class)
  .getResultList();
```

**BETWEEN, IN, IS NULL:**

```java
// BETWEEN
em.createQuery("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max", Product.class)
  .setParameter("min", 100.0)
  .setParameter("max", 500.0)
  .getResultList();

// IN с коллекцией
List<Long> ids = List.of(1L, 2L, 3L);
em.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
  .setParameter("ids", ids)
  .getResultList();

// IS NULL / IS NOT NULL
em.createQuery("SELECT u FROM User u WHERE u.deletedAt IS NULL", User.class)
  .getResultList();
```

---

## Джойны (JOIN, JOIN FETCH)

### Обычный JOIN

Используется для фильтрации и выбора данных из связанных сущностей. Не инициализирует коллекцию в памяти.

```java
// INNER JOIN — только пользователи с заказами
List<User> usersWithOrders = em.createQuery(
    "SELECT DISTINCT u FROM User u JOIN u.orders o WHERE o.status = :status", User.class)
  .setParameter("status", OrderStatus.PAID)
  .getResultList();

// LEFT JOIN — все пользователи, в т.ч. без заказов
List<User> allUsers = em.createQuery(
    "SELECT u FROM User u LEFT JOIN u.orders o WHERE o IS NULL OR o.status = :status", User.class)
  .setParameter("status", OrderStatus.PENDING)
  .getResultList();

// JOIN с алиасом для WHERE
em.createQuery(
    "SELECT u FROM User u JOIN u.address a WHERE a.city = :city", User.class)
  .setParameter("city", "Moscow")
  .getResultList();
```

### JOIN FETCH — решение N+1

`JOIN FETCH` загружает связанные сущности в одном SQL-запросе, инициализируя коллекцию.

```java
// Без JOIN FETCH: 1 запрос на users + N запросов на orders (N+1!)
List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();

// С JOIN FETCH: один SQL с JOIN
List<User> users = em.createQuery(
    "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.orders", User.class)
  .getResultList();
// DISTINCT нужен, чтобы убрать дубликаты из-за JOIN
```

**Несколько JOIN FETCH одновременно — только для @ManyToOne/@OneToOne:**

```java
// Можно: один JOIN FETCH коллекции + несколько JOIN FETCH @ManyToOne
em.createQuery(
    "SELECT o FROM Order o " +
    "JOIN FETCH o.user " +       // ManyToOne — OK
    "JOIN FETCH o.product " +    // ManyToOne — OK
    "LEFT JOIN FETCH o.items",   // OneToMany — OK, но только одна такая коллекция
    Order.class).getResultList();
```

**MultipleBagFetchException — нельзя два FETCH для двух коллекций одновременно:**

```java
// ОШИБКА: два JOIN FETCH для List-коллекций
em.createQuery(
    "SELECT u FROM User u JOIN FETCH u.orders JOIN FETCH u.roles", User.class)
  .getResultList(); // MultipleBagFetchException!

// Решение 1: использовать Set вместо List
@OneToMany
private Set<Order> orders; // Set вместо List

// Решение 2: два отдельных запроса
List<User> users = em.createQuery("SELECT DISTINCT u FROM User u JOIN FETCH u.orders", User.class).getResultList();
// Hibernate сам догрузит roles из L1/L2
```

### JOIN ON — дополнительные условия джойна

```java
// HQL-расширение: JOIN ... ON
em.createQuery(
    "SELECT u FROM User u LEFT JOIN u.orders o ON o.status = 'PAID' AND o.amount > 1000",
    User.class).getResultList();
```

---

## Подзапросы

JPQL поддерживает подзапросы в `WHERE` и `HAVING`, но **не в FROM и SELECT** (в отличие от SQL).

```java
// EXISTS — есть ли хотя бы один заказ
em.createQuery(
    "SELECT u FROM User u WHERE EXISTS " +
    "(SELECT o FROM Order o WHERE o.user = u AND o.status = 'PAID')", User.class)
  .getResultList();

// NOT EXISTS
em.createQuery(
    "SELECT u FROM User u WHERE NOT EXISTS " +
    "(SELECT o FROM Order o WHERE o.user = u)", User.class)
  .getResultList();

// IN с подзапросом
em.createQuery(
    "SELECT u FROM User u WHERE u.id IN " +
    "(SELECT o.user.id FROM Order o WHERE o.amount > 1000)", User.class)
  .getResultList();

// ALL / ANY / SOME
em.createQuery(
    "SELECT p FROM Product p WHERE p.price > ALL " +
    "(SELECT o.price FROM Order o WHERE o.status = 'CANCELLED')", Product.class)
  .getResultList();

// Коррелированный подзапрос с агрегатом
em.createQuery(
    "SELECT u FROM User u WHERE " +
    "(SELECT COUNT(o) FROM Order o WHERE o.user = u) > 5", User.class)
  .getResultList();
```

---

## Именованные запросы

`@NamedQuery` объявляется на уровне класса и компилируется при старте приложения — ошибки синтаксиса выявляются сразу.

```java
@Entity
@NamedQuery(
    name = "User.findByEmail",
    query = "SELECT u FROM User u WHERE u.email = :email"
)
@NamedQuery(
    name = "User.findActiveUsers",
    query = "SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC"
)
public class User {
    @Id private Long id;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
}

// Несколько именованных запросов
@NamedQueries({
    @NamedQuery(name = "User.countActive",
                query = "SELECT COUNT(u) FROM User u WHERE u.active = true"),
    @NamedQuery(name = "User.deactivateOld",
                query = "UPDATE User u SET u.active = false WHERE u.lastLogin < :cutoff")
})
```

**Использование:**

```java
List<User> users = em.createNamedQuery("User.findByEmail", User.class)
    .setParameter("email", "test@example.com")
    .getResultList();

Long count = em.createNamedQuery("User.countActive", Long.class)
    .getSingleResult();
```

**@NamedNativeQuery — для нативного SQL:**

```java
@NamedNativeQuery(
    name = "User.findByEmailNative",
    query = "SELECT * FROM users WHERE email = :email",
    resultClass = User.class
)
```

---

## Пагинация и сортировка

```java
// Постраничная выборка
int page = 2;
int pageSize = 20;

List<Product> products = em.createQuery(
    "SELECT p FROM Product p ORDER BY p.createdAt DESC", Product.class)
  .setFirstResult((page - 1) * pageSize)  // offset
  .setMaxResults(pageSize)                 // limit
  .getResultList();

// Подсчёт общего количества (для пагинации)
Long total = em.createQuery(
    "SELECT COUNT(p) FROM Product p", Long.class)
  .getSingleResult();
```

**Пагинация с JOIN FETCH — антипаттерн:**

```java
// ПРОБЛЕМА: Hibernate применяет пагинацию в памяти, не в SQL
// В логах появится: HHH90003004: firstResult/maxResults specified with collection fetch
List<User> users = em.createQuery(
    "SELECT DISTINCT u FROM User u JOIN FETCH u.orders", User.class)
  .setFirstResult(0)
  .setMaxResults(10)
  .getResultList(); // всё грузится в память, потом режется!

// ПРАВИЛЬНО: два запроса
List<Long> ids = em.createQuery(
    "SELECT u.id FROM User u ORDER BY u.id", Long.class)
  .setFirstResult(0)
  .setMaxResults(10)
  .getResultList();

List<User> users = em.createQuery(
    "SELECT DISTINCT u FROM User u JOIN FETCH u.orders WHERE u.id IN :ids", User.class)
  .setParameter("ids", ids)
  .getResultList();
```

**Cursor-based пагинация (keyset pagination) через HQL:**

```java
// Эффективнее OFFSET для больших датасетов
em.createQuery(
    "SELECT p FROM Product p WHERE p.id > :lastId ORDER BY p.id ASC", Product.class)
  .setParameter("lastId", lastSeenId)
  .setMaxResults(20)
  .getResultList();
```

---

## Проекции и DTO

Вместо загрузки целых сущностей можно выбирать конкретные поля.

**Проекция в Object[]:**

```java
List<Object[]> result = em.createQuery(
    "SELECT u.id, u.name, u.email FROM User u WHERE u.active = true")
  .getResultList();

for (Object[] row : result) {
    Long id = (Long) row[0];
    String name = (String) row[1];
    String email = (String) row[2];
}
```

**Проекция через конструктор DTO (рекомендуемый подход):**

```java
// DTO-класс
public class UserSummaryDto {
    private final Long id;
    private final String name;
    private final String email;

    public UserSummaryDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    // getters...
}

// JPQL с NEW-конструктором
List<UserSummaryDto> dtos = em.createQuery(
    "SELECT NEW com.example.dto.UserSummaryDto(u.id, u.name, u.email) " +
    "FROM User u WHERE u.active = true", UserSummaryDto.class)
  .getResultList();
```

**Tuple — удобная альтернатива Object[]:**

```java
List<Tuple> tuples = em.createQuery(
    "SELECT u.id AS id, u.name AS name FROM User u", Tuple.class)
  .getResultList();

for (Tuple t : tuples) {
    Long id = t.get("id", Long.class);
    String name = t.get("name", String.class);
}
```

---

## Criteria API

Criteria API строит запросы как дерево Java-объектов. Многословнее JPQL, но безопасен на этапе компиляции.

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);

// WHERE u.active = true AND u.age >= 18
cq.select(root)
  .where(
      cb.and(
          cb.equal(root.get("active"), true),
          cb.greaterThanOrEqualTo(root.get("age"), 18)
      )
  )
  .orderBy(cb.asc(root.get("name")));

List<User> users = em.createQuery(cq).getResultList();
```

**С Metamodel (полностью типобезопасно):**

```java
// Сгенерированный Metamodel: User_.java
// Генерация: hibernate-jpamodelgen (добавить в pom.xml)

CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);

// Используем User_.active вместо строки "active"
cq.select(root)
  .where(cb.equal(root.get(User_.active), true));

// Metamodel dependency:
// <dependency>
//   <groupId>org.hibernate.orm</groupId>
//   <artifactId>hibernate-jpamodelgen</artifactId>
//   <scope>provided</scope>
// </dependency>
```

**JOIN через Criteria:**

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> user = cq.from(User.class);

// JOIN
Join<User, Order> orderJoin = user.join("orders", JoinType.LEFT);

cq.select(user)
  .where(cb.equal(orderJoin.get("status"), "PAID"))
  .distinct(true);
```

**Агрегаты и GROUP BY:**

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
Root<Order> root = cq.from(Order.class);

Expression<Long> count = cb.count(root);
Expression<Double> sum = cb.sum(root.get("amount"));

cq.multiselect(root.get("status"), count, sum)
  .groupBy(root.get("status"))
  .having(cb.gt(count, 5L));

List<Object[]> results = em.createQuery(cq).getResultList();
```

---

## Динамические запросы через Criteria

Главное преимущество Criteria API — легко строить запросы с опциональными условиями.

```java
public List<Product> searchProducts(String name, Double minPrice, Double maxPrice, Long categoryId) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);

    List<Predicate> predicates = new ArrayList<>();

    if (name != null && !name.isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }
    if (minPrice != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
    }
    if (maxPrice != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
    }
    if (categoryId != null) {
        predicates.add(cb.equal(root.get("category").get("id"), categoryId));
    }

    cq.select(root)
      .where(predicates.toArray(new Predicate[0]))
      .orderBy(cb.asc(root.get("name")));

    return em.createQuery(cq).getResultList();
}
```

---

## Criteria Tuple и мультиселект

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Tuple> cq = cb.createTupleQuery();
Root<User> root = cq.from(User.class);

cq.multiselect(
    root.get("id").alias("id"),
    root.get("name").alias("name"),
    cb.count(root).alias("orderCount")
);

List<Tuple> result = em.createQuery(cq).getResultList();
for (Tuple t : result) {
    Long id = t.get("id", Long.class);
    String name = t.get("name", String.class);
}
```

**Проекция в DTO через Criteria:**

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<UserSummaryDto> cq = cb.createQuery(UserSummaryDto.class);
Root<User> root = cq.from(User.class);

cq.select(cb.construct(
    UserSummaryDto.class,
    root.get("id"),
    root.get("name"),
    root.get("email")
));

List<UserSummaryDto> dtos = em.createQuery(cq).getResultList();
```

---

## UPDATE и DELETE через JPQL

Bulk-операции — обновление/удаление множества записей в одном SQL без загрузки в память.

```java
// Bulk UPDATE
int updated = em.createQuery(
    "UPDATE Product p SET p.active = false WHERE p.stock = 0")
  .executeUpdate();

// Bulk UPDATE с параметром
int deactivated = em.createQuery(
    "UPDATE User u SET u.active = false WHERE u.lastLogin < :cutoff")
  .setParameter("cutoff", LocalDateTime.now().minusYears(2))
  .executeUpdate();

// Bulk DELETE
int deleted = em.createQuery(
    "DELETE FROM AuditLog a WHERE a.createdAt < :cutoff")
  .setParameter("cutoff", LocalDate.now().minusMonths(6).atStartOfDay())
  .executeUpdate();
```

**Важно при bulk-операциях:**
- L1-кэш (Session) не синхронизируется автоматически — после bulk-операции нужен `em.clear()`.
- L2-кэш для затронутых сущностей инвалидируется Hibernate автоматически (при JPQL).
- Нет каскада — `CascadeType.REMOVE` не работает с `DELETE` через JPQL.

```java
// Правильный порядок при bulk update
em.createQuery("UPDATE Product p SET p.active = false WHERE p.stock = 0")
  .executeUpdate();
em.clear(); // сбросить L1 после bulk-операции
```

---

## Практические советы

**Всегда используйте именованные параметры:**

```java
// Плохо — positional параметры путают
query.setParameter(1, "value1").setParameter(2, 42);

// Хорошо — именованные параметры
query.setParameter("status", "ACTIVE").setParameter("minAge", 18);
```

**Используйте `TypedQuery` вместо сырого `Query`:**

```java
// Плохо — unchecked cast
Query q = em.createQuery("SELECT u FROM User u");
List<User> users = (List<User>) q.getResultList(); // warning

// Хорошо — типобезопасно
TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
List<User> users = q.getResultList(); // без cast
```

**Optional вместо getSingleResult:**

```java
// getSingleResult бросает исключение если нет результата
try {
    User user = em.createQuery("SELECT u FROM User u WHERE u.email = :e", User.class)
        .setParameter("e", email)
        .getSingleResult();
} catch (NoResultException e) {
    return Optional.empty();
}

// Через getResultList + stream
Optional<User> user = em.createQuery("SELECT u FROM User u WHERE u.email = :e", User.class)
    .setParameter("e", email)
    .getResultStream()
    .findFirst();
```

**Fetch-план вместо EAGER-связей:**

```java
// Вместо @ManyToOne(fetch = EAGER) — явный JOIN FETCH в запросе
@ManyToOne(fetch = FetchType.LAZY) // по умолчанию LAZY
private Category category;

// Загружаем вместе только там, где нужно
em.createQuery("SELECT p FROM Product p JOIN FETCH p.category", Product.class)
  .getResultList();
```

**Используйте `exists()` вместо `count()` для проверки наличия:**

```java
// Плохо: COUNT делает полный подсчёт
Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :e", Long.class)
    .setParameter("e", email).getSingleResult();
boolean exists = count > 0;

// Хорошо: EXISTS останавливается на первом совпадении
Boolean exists = em.createQuery(
    "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :e",
    Boolean.class)
  .setParameter("e", email).getSingleResult();
```

---

## Типичные ошибки

**N+1 при ленивой загрузке:**

```java
// Загружаем пользователей, потом в цикле обращаемся к orders
List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
for (User u : users) {
    System.out.println(u.getOrders().size()); // N SQL-запросов!
}

// Решение: JOIN FETCH
List<User> users = em.createQuery(
    "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.orders", User.class).getResultList();
```

**LazyInitializationException вне транзакции:**

```java
// Транзакция закрыта, сессия уже закрыта
User user = userService.findById(1L);
user.getOrders().size(); // LazyInitializationException!

// Решение 1: JOIN FETCH в запросе
// Решение 2: @Transactional на вызывающем методе
// Решение 3: Hibernate.initialize() внутри транзакции
```

**Картезианское произведение при нескольких JOIN FETCH:**

```java
// Два FETCH создают декартово произведение строк
em.createQuery("SELECT u FROM User u JOIN FETCH u.orders o JOIN FETCH u.roles r")
  // пользователь с 5 заказами и 3 ролями = 15 строк в ResultSet!
  // DISTINCT в JPQL убирает дубли на уровне Java, но SQL всё равно вернёт 15 строк
```

**Использование JPQL для работы со связями через строковый путь:**

```java
// Путь через точку работает только для @ManyToOne/@OneToOne
em.createQuery("SELECT o FROM Order o WHERE o.user.email = :email", Order.class)
  .setParameter("email", "test@example.com");
// Hibernate сам сгенерирует JOIN

// Для коллекций нужен явный JOIN
em.createQuery("SELECT o FROM Order o JOIN o.items i WHERE i.name = :name", Order.class);
```

---

## См. также

- [[orm-basics|ORM Basics]] — базовые концепции JPA и EntityManager
- [[hibernate-relationships|Hibernate Relationships]] — связи, LAZY/EAGER, N+1
- [[hibernate-interview|Hibernate Interview]] — типовые вопросы на собеседовании
- [[spring-data-jpa-interview|Spring Data JPA]] — `@Query`, Specification, QueryDSL
- [[database-transactions-interview|Database Transactions]] — изоляция, блокировки, deadlock
- [[java-jdbc|JDBC]] — PreparedStatement и нативные запросы
