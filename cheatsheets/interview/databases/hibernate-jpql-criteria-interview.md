---
title: "Вопросы на собеседовании: Hibernate JPQL & Criteria API"
description: "JPQL и Criteria API в Hibernate/JPA: синтаксис, subqueries, Criteria Query, JPA Metamodel, Spring Data Specifications, type-safety, native queries"
tags:
  - interview
  - databases
  - hibernate-jpql-criteria-interview
type: "interview"
difficulty: "advanced"
aliases:
  - "Вопросы на собеседовании"
  - "Hibernate JPQL & Criteria API"
  - "JPQL interview"
  - "Criteria API interview"
prerequisites:
  - "[[hibernate-jpql-criteria]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Hibernate JPQL & Criteria API`

`JPQL` и `Criteria API` — два основных способа запросов в JPA/Hibernate. JPQL — текстовый DSL подобный SQL, Criteria API — type-safe программный API. Важная тема для middle/senior Java-разработчиков.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Hibernate JPQL Docs](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#hql) — JPQL/HQL документация
- [Hibernate Criteria API](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria) — Criteria API
- [Baeldung: JPA Criteria](https://www.baeldung.com/hibernate-criteria-queries) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Какие способы запросов существуют в JPA/Hibernate?

JPA/Hibernate предоставляют несколько способов выполнения запросов:

| Способ | Type-safety | Dynamic | Применение |
|--------|-------------|---------|------------|
| **JPQL** | Нет (строка) | Да, через Criteria | Большинство статичных запросов |
| **Criteria API** | Да | Да | Динамические запросы |
| **Native SQL** | Нет | Да | БД-специфичные возможности |
| **HQL** (Hibernate-специфичный) | Нет | Да | Hibernate-расширения JPQL |
| **Spring Data JPA** (`@Query`, derived methods) | Частично | Нет | CRUD-репозитории |

```java
// JPQL — SQL-подобный, но работает с entity
em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)

// Criteria — type-safe построение
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
// Native — raw SQL
em.createNativeQuery("SELECT * FROM users WHERE email = ?", User.class)
```

## Q2. Что такое JPQL и чем он отличается от SQL?

**JPQL (Java Persistence Query Language)** — объектно-ориентированный язык запросов. Работает с entities и их полями, а не с таблицами и колонками.

```java
// SQL
"SELECT u.id, u.first_name FROM users u JOIN orders o ON o.user_id = u.id WHERE o.status = 'CONFIRMED'"

// JPQL
"SELECT u FROM User u JOIN u.orders o WHERE o.status = :status"
//        ^^^^ entity     ^^^^^^^^ navigation через связь
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Отличия**:
- `FROM User` — не таблица, а entity.
- `u.orders` — навигация через `@OneToMany` связь.
- `:status` — named parameter вместо `?`.
- Нет `SELECT *` — или SELECT с explicit полями/entity.
- Нет database-specific syntax (LIMIT, OFFSET — через `setFirstResult/setMaxResults`).

## Q3. Как использовать параметры в JPQL?

```java
// Named parameters (рекомендуется — читаемее)
TypedQuery<User> query = em.createQuery(
    "SELECT u FROM User u WHERE u.email = :email AND u.age > :minAge",
    User.class);
query.setParameter("email", "alice@example.com");
query.setParameter("minAge", 18);

// Positional parameters (legacy)
TypedQuery<User> query = em.createQuery(
    "SELECT u FROM User u WHERE u.email = ?1 AND u.age > ?2",
    User.class);
query.setParameter(1, "alice@example.com");
query.setParameter(2, 18);

// Правила безопасности: ВСЕГДА используйте параметры, НЕ конкатенацию строк
// ПЛОХО — SQL injection
em.createQuery("SELECT u FROM User u WHERE u.email = '" + userInput + "'");


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
// ХОРОШО
em.createQuery("SELECT u FROM User u WHERE u.email = :email")
    .setParameter("email", userInput);
```

## Q4. Как делать JOIN в JPQL?

```java
// INNER JOIN (default)
"SELECT u FROM User u JOIN u.orders o WHERE o.total > 100"

// LEFT JOIN
"SELECT u FROM User u LEFT JOIN u.orders o WHERE o.total > 100 OR o IS NULL"

// JOIN FETCH — для eager loading
"SELECT u FROM User u JOIN FETCH u.orders"
// Загрузит User + все orders одним запросом

// JOIN с ON (Hibernate 5+)
"SELECT u, o FROM User u JOIN Order o ON o.userId = u.id AND o.status = 'CONFIRMED'"

// Implicit join через навигацию
"SELECT u FROM User u WHERE u.profile.country = 'USA'"
// Эквивалентно: JOIN u.profile p WHERE p.country = 'USA'
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**JOIN FETCH vs JOIN**:
- `JOIN` — для фильтрации, коллекции могут остаться lazy.
- `JOIN FETCH` — для загрузки коллекций одним запросом (предотвращает N+1).

## Q5. Что такое Criteria API и когда его использовать?

**Criteria API** — type-safe программный способ построения запросов.

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> user = cq.from(User.class);

cq.select(user)
  .where(
      cb.and(
          cb.equal(user.get("email"), "alice@example.com"),
          cb.greaterThan(user.get("age"), 18)
      )
  );

List<User> results = em.createQuery(cq).getResultList();
```

**Когда использовать**:
- **Динамические запросы** (фильтры по optional параметрам в REST API).
- **Type-safety** важна.
- **Reuse** — компоненты query можно переиспользовать.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Когда НЕ использовать**:
- **Статичные запросы** — JPQL читаемее.
- **Сложные queries** — становится крайне verbose.

## Q6. Что такое JPA Metamodel и как его использовать?

**Metamodel** — сгенерированные классы для type-safe доступа к полям entity в Criteria.

```java
// Оригинальный entity
@Entity
public class User {
    @Id Long id;
    String email;
    Integer age;
    @OneToMany List<Order> orders;
}

// Сгенерированный metamodel (User_.java)
@StaticMetamodel(User.class)
public abstract class User_ {
    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, Integer> age;
    public static volatile ListAttribute<User, Order> orders;
}

// Использование в Criteria
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> user = cq.from(User.class);

cq.select(user)
  .where(
      cb.equal(user.get(User_.email), "alice@example.com"),   // type-safe!
      cb.greaterThan(user.get(User_.age), 18)
  );
```

**Генерация metamodel**:

```xml
<!-- Maven -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jpamodelgen</artifactId>
    <scope>provided</scope>
</dependency>
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
Компилятор автоматически сгенерирует `*_.java` классы. Изменение поля `User.email` → изменение `User_.email` → ошибка компиляции там где использовалось старое имя.

## Q7. Как делать subqueries?

```java
// JPQL subquery
"SELECT u FROM User u WHERE u.id IN (SELECT o.user.id FROM Order o WHERE o.total > 1000)"

// EXISTS
"SELECT u FROM User u WHERE EXISTS (SELECT o FROM Order o WHERE o.user = u AND o.status = 'PENDING')"

// Correlated subquery
"SELECT u FROM User u WHERE u.age > (SELECT AVG(u2.age) FROM User u2 WHERE u2.country = u.country)"

// ALL / ANY / SOME
"SELECT p FROM Product p WHERE p.price > ALL (SELECT p2.price FROM Product p2 WHERE p2.category = 'LEGACY')"
```

```java
// Criteria subquery
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> user = cq.from(User.class);

Subquery<Long> subquery = cq.subquery(Long.class);
Root<Order> order = subquery.from(Order.class);
subquery.select(order.get("user").get("id"))
        .where(cb.greaterThan(order.get("total"), 1000));


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
cq.select(user).where(user.get("id").in(subquery));
```

## Q8. Что такое Spring Data Specifications?

**Specifications** — Spring Data обёртка над Criteria API для составления переиспользуемых фильтров.

```java
// Репозиторий с JpaSpecificationExecutor
public interface UserRepository extends JpaRepository<User, Long>,
                                          JpaSpecificationExecutor<User> {}

// Specifications как лямбды
public class UserSpecifications {

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }

    public static Specification<User> olderThan(int age) {
        return (root, query, cb) -> cb.greaterThan(root.get("age"), age);
    }

    public static Specification<User> inCountry(String country) {
        return (root, query, cb) -> cb.equal(root.get("country"), country);
    }
}

// Комбинирование
@Service
public class UserService {
    @Autowired private UserRepository repository;

    public List<User> search(UserFilter filter) {
        Specification<User> spec = Specification.where(null);

        if (filter.email() != null) {
            spec = spec.and(UserSpecifications.hasEmail(filter.email()));
        }
        if (filter.minAge() != null) {
            spec = spec.and(UserSpecifications.olderThan(filter.minAge()));
        }
        if (filter.country() != null) {
            spec = spec.and(UserSpecifications.inCountry(filter.country()));
        }

        return repository.findAll(spec);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Преимущество**: чистый код с динамическими фильтрами без if-else лестниц в Criteria API.

## Q9. Что такое @NamedQuery и когда его использовать?

**@NamedQuery** — именованный JPQL запрос, закреплённый за entity.

```java
@Entity
@NamedQueries({
    @NamedQuery(
        name = "User.findByEmail",
        query = "SELECT u FROM User u WHERE u.email = :email"
    ),
    @NamedQuery(
        name = "User.countActive",
        query = "SELECT COUNT(u) FROM User u WHERE u.active = true"
    )
})
public class User { ... }

// Использование
User user = em.createNamedQuery("User.findByEmail", User.class)
    .setParameter("email", "alice@example.com")
    .getSingleResult();

Long count = em.createNamedQuery("User.countActive", Long.class)
    .getSingleResult();
```

**Преимущества**:
- **Валидация при старте** — запрос парсится при инициализации EntityManagerFactory.
- **Централизованное хранение** — все queries в одном месте.
- **Производительность** — query parse только один раз.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Недостатки**: отделены от места использования, в Spring Data JPA обычно используется `@Query` над методом репозитория.

## Q10. Как использовать native SQL запросы?

```java
// Simple native query
Query query = em.createNativeQuery(
    "SELECT * FROM users WHERE email = ?", User.class);
query.setParameter(1, "alice@example.com");
User user = (User) query.getSingleResult();

// Named native query
@Entity
@NamedNativeQuery(
    name = "User.findByEmailNative",
    query = "SELECT * FROM users WHERE email = :email",
    resultClass = User.class
)
public class User { }

// С result mapping для сложных результатов
@SqlResultSetMapping(
    name = "UserWithOrderCount",
    entities = @EntityResult(entityClass = User.class),
    columns = @ColumnResult(name = "order_count", type = Long.class)
)

em.createNativeQuery(
    "SELECT u.*, (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count " +
    "FROM users u",
    "UserWithOrderCount"
).getResultList();

// Spring Data JPA
@Query(value = "SELECT * FROM users WHERE created_at > :since", nativeQuery = true)
List<User> findRecentUsers(@Param("since") LocalDateTime since);
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Когда использовать**:
- **Database-specific features** (PostgreSQL PIVOT, SQL Server CTE с рекурсией).
- **Сложные CTE** — JPQL не поддерживает.
- **Performance-критичные запросы** — когда ORM-слой добавляет накладные.

## Q11. Что такое @EntityGraph для предотвращения N+1?

```java
// Определение graph
@Entity
@NamedEntityGraph(
    name = "User.withOrdersAndProfile",
    attributeNodes = {
        @NamedAttributeNode("orders"),
        @NamedAttributeNode("profile")
    }
)
public class User { ... }

// Использование в Spring Data
@EntityGraph("User.withOrdersAndProfile")
@Query("FROM User WHERE u.id = :id")
Optional<User> findByIdWithDetails(@Param("id") Long id);

// Ad-hoc graph
@EntityGraph(attributePaths = {"orders.items", "profile"})
List<User> findByCountry(String country);

// В JPA напрямую
EntityGraph<User> graph = em.createEntityGraph(User.class);
graph.addAttributeNodes("orders", "profile");
Subgraph<Order> orderGraph = graph.addSubgraph("orders");
orderGraph.addAttributeNodes("items");

Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
User user = em.find(User.class, 1L, hints);
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**@EntityGraph vs JOIN FETCH**:
- EntityGraph — декларативный, переиспользуемый.
- JOIN FETCH — inline в запросе, более явный.

## Q12. Какие типичные ошибки при работе с JPQL?

1. **SELECT * не работает**:

```java
// ОШИБКА
em.createQuery("SELECT * FROM User")

// ХОРОШО
em.createQuery("SELECT u FROM User u", User.class)
// или: "FROM User" (short form)
```

2. **Неправильное использование IN с коллекциями**:

```java
// ОШИБКА — кажется логичным
"FROM User u WHERE u.email IN ?1"  // positional с коллекцией может не работать

// ХОРОШО — named parameter
"FROM User u WHERE u.email IN :emails"
query.setParameter("emails", List.of("alice@x.com", "bob@x.com"))
```

3. **UPDATE/DELETE без @Modifying**:

```java
// Spring Data JPA
// ОШИБКА
@Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :threshold")
void deactivateOld(@Param("threshold") LocalDateTime threshold);

// ХОРОШО
@Modifying
@Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :threshold")
int deactivateOld(@Param("threshold") LocalDateTime threshold);
```

4. **Смешивание JOIN FETCH с pagination**:

```java
// ПЛОХО — Hibernate выдаст warning и сделает pagination в памяти!
em.createQuery("FROM User u JOIN FETCH u.orders", User.class)
    .setFirstResult(0).setMaxResults(10);
```

5. **Distinct для JOIN FETCH**:

```java
// Без distinct — дубли пользователей с каждым его заказом
em.createQuery("FROM User u JOIN FETCH u.orders", User.class)


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
// С distinct — уникальные пользователи
em.createQuery("SELECT DISTINCT u FROM User u JOIN FETCH u.orders", User.class)
```

## Q13. Что такое Projection в JPQL/Criteria?

**Projection** — выбор подмножества полей вместо полного entity.

```java
// JPQL — tuple
List<Object[]> results = em.createQuery(
    "SELECT u.id, u.email FROM User u", Object[].class)
    .getResultList();

for (Object[] row : results) {
    Long id = (Long) row[0];
    String email = (String) row[1];
}

// JPQL — DTO через constructor
public record UserDto(Long id, String email) {}

List<UserDto> users = em.createQuery(
    "SELECT new com.example.UserDto(u.id, u.email) FROM User u",
    UserDto.class)
    .getResultList();

// Criteria — Tuple
CriteriaQuery<Tuple> cq = cb.createTupleQuery();
Root<User> u = cq.from(User.class);
cq.multiselect(u.get("id"), u.get("email"));

List<Tuple> tuples = em.createQuery(cq).getResultList();
for (Tuple t : tuples) {
    Long id = t.get(0, Long.class);
    String email = t.get(1, String.class);
}

// Spring Data JPA — Interface projection
public interface UserSummary {
    Long getId();
    String getEmail();
}

List<UserSummary> findByActive(boolean active);
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Применение**: когда нужно меньше данных чем в entity (оптимизация производительности, reports).

## Q14. Как реализовать pagination в JPA?

```java
// Ручная pagination
TypedQuery<User> query = em.createQuery("FROM User", User.class);
query.setFirstResult(0);      // offset
query.setMaxResults(20);       // limit
List<User> page = query.getResultList();

// Получение общего count отдельным запросом
Long total = em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
    .getSingleResult();

// Spring Data JPA — автоматическая pagination
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByActive(boolean active, Pageable pageable);
}

Page<User> page = userRepository.findByActive(true,
    PageRequest.of(0, 20, Sort.by("createdAt").descending()));

long total = page.getTotalElements();
int totalPages = page.getTotalPages();
List<User> content = page.getContent();
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
**Проблема pagination + JOIN FETCH** — см. Q12.4.

## Q15. Какие best practices при работе с JPA queries?

1. **Предпочитайте Spring Data JPA derived methods** для простых случаев:

```java
List<User> findByEmailAndActive(String email, boolean active);
Optional<User> findByEmail(String email);
long countByCountry(String country);
```

2. **Используйте `@Query`** для средних запросов с `:parameter`:

```java
@Query("FROM User u WHERE u.email = :email AND u.active = true")
Optional<User> findActiveByEmail(@Param("email") String email);
```

3. **Используйте Specifications** для динамических фильтров.

4. **JPA Metamodel** для type-safety в Criteria.

5. **Native SQL** только когда JPQL недостаточен (DB-specific features).

6. **JOIN FETCH** для предотвращения N+1, но не с pagination.

7. **Projections** для отчётов — возвращайте только нужные поля.

8. **@EntityGraph** для декларативного fetch profile.

9. **Hibernate Statistics** для мониторинга количества SQL-запросов.

10. **Не забывайте `@Modifying` + `@Transactional`** для UPDATE/DELETE.

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Это ключевое разграничение из best practice.
> - [ ] Вариант А | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант В | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Вариант С | Почему неверно 2-3 предложения ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Hibernate](hibernate-interview.md) — основы Hibernate, Session, entity states
- [Hibernate Relationships](hibernate-relationships-interview.md) — @OneToMany, @ManyToOne и JPQL JOIN FETCH
- [Hibernate Caching](hibernate-caching-interview.md) — query cache, кэширование результатов JPQL
- [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) — Specifications API, @Query, derived methods
- [SQL](sql-interview.md) — SQL основы, JOIN, GROUP BY
- [PostgreSQL](postgresql-interview.md) — типичная БД для JPA приложений
- [Database Performance](../performance/database-performance-interview.md) — оптимизация запросов
- [Database Transactions](database-transactions-interview.md) — транзакции и видимость данных
- [Spring @Transactional](../frameworks/spring/spring-transaction-interview.md) — @Modifying запросы требуют транзакции
- [Spring Data JDBC](../frameworks/spring/spring-data-jdbc-interview.md) — альтернатива без JPQL/Criteria
