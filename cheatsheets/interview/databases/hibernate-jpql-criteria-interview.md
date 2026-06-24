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
updated: 2026-05-31
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

JPA не навязывает один язык запросов — есть пять механизмов, и выбирают их по двум осям: нужна ли проверка типов на этапе компиляции (type-safety) и нужно ли строить запрос динамически. Понимание этой матрицы — основа осознанного выбора инструмента под задачу.

| Способ | Type-safety | Dynamic | Когда брать |
|--------|-------------|---------|------------|
| **JPQL** | Нет (строка) | Да, через Criteria | Большинство статичных запросов |
| **Criteria API** | Да | Да | Динамические запросы |
| **Native SQL** | Нет | Да | БД-специфичные возможности |
| **HQL** (Hibernate-специфичный) | Нет | Да | Hibernate-расширения JPQL |
| **Spring Data JPA** (`@Query`, derived methods) | Частично | Нет | CRUD-репозитории |

Коротко: статичный поиск по полю — JPQL, динамический фильтр по optional-параметрам — Criteria/Specifications, БД-специфичные возможности — native SQL. HQL — это тот же JPQL плюс Hibernate-расширения.

```java
// JPQL — SQL-подобный, но работает с entity
em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)

// Criteria — type-safe построение
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);

// Native — raw SQL
em.createNativeQuery("SELECT * FROM users WHERE email = ?", User.class)
```

## Q2. Что такое JPQL и чем он отличается от SQL?

**JPQL (Java Persistence Query Language)** — объектно-ориентированный язык запросов JPA. Ключевое отличие от SQL: JPQL оперирует entity и их полями, а не таблицами и колонками. Вы пишете запрос в терминах доменной модели, а Hibernate сам транслирует его в SQL под конкретную БД.

```java
// SQL
"SELECT u.id, u.first_name FROM users u JOIN orders o ON o.user_id = u.id WHERE o.status = 'CONFIRMED'"

// JPQL
"SELECT u FROM User u JOIN u.orders o WHERE o.status = :status"
//        ^^^^ entity     ^^^^^^^^ navigation через связь
```

**Чем отличается от SQL**:
- `FROM User` — это entity, а не таблица `users`.
- `u.orders` — навигация по связи `@OneToMany`; JOIN-условие выводится из маппинга, его не пишут руками.
- `:status` — named parameter вместо безымянного `?`.
- Нет `SELECT *` — указывают либо entity целиком, либо конкретные поля.
- Нет БД-специфичного синтаксиса: `LIMIT`/`OFFSET` задают через `setFirstResult`/`setMaxResults`, и Hibernate сам подставит диалект нужной СУБД.

За счёт этого один и тот же JPQL переносим между БД, а ссылки на поля проверяются маппингом, а не «вслепую» по строке.

## Q3. Как использовать параметры в JPQL?

Параметры — единственный безопасный способ подставлять значения в запрос. Есть два вида: **named** (`:email`, читаемее и не зависят от порядка) и **positional** (`?1`, legacy). Главное правило: всегда параметризуйте — никогда не склеивайте значения в строку запроса, иначе открываете дорогу SQL injection.

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

// ХОРОШО
em.createQuery("SELECT u FROM User u WHERE u.email = :email")
    .setParameter("email", userInput);
```

## Q4. Как делать JOIN в JPQL?

JOIN в JPQL чаще всего пишут не по колонкам, а по навигации через связь entity (`u.orders`), и условие соединения Hibernate берёт из маппинга. Доступны обычные `INNER`/`LEFT JOIN`, `JOIN FETCH` для загрузки коллекций и implicit join через точечную навигацию.

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

**JOIN FETCH vs JOIN** — частый вопрос на собеседовании:
- `JOIN` нужен только для фильтрации по связанным данным; сама коллекция при этом остаётся lazy и догружается отдельными запросами при обращении.
- `JOIN FETCH` ещё и загружает коллекцию тем же SQL-запросом — это основной способ убрать проблему N+1.

## Q5. Что такое Criteria API и когда его использовать?

**Criteria API** — программный, type-safe способ строить запросы из Java-объектов (`CriteriaBuilder`, `CriteriaQuery`, `Root`, `Predicate`) вместо текстовой строки. Главная ценность — динамика: предикаты можно собирать по условию, добавляя их в зависимости от заполненных фильтров, а ошибки в именах полей ловятся компилятором (при использовании Metamodel из Q6).

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
- **Динамические запросы** — фильтры по optional-параметрам в REST API, где набор условий заранее неизвестен.
- **Нужна type-safety** — поля проверяются компилятором, переименование поля ломает сборку, а не падает в рантайме.
- **Переиспользование** — отдельные предикаты можно вынести и собирать из них разные запросы.

**Когда НЕ использовать**:
- **Статичные запросы** — JPQL короче и читаемее.
- **Сложные запросы** — Criteria быстро становится громоздким и плохо читается; здесь обычно выигрывает JPQL или native SQL.

На практике поверх Criteria в Spring почти всегда берут Specifications (см. Q8) — они дают ту же динамику, но без ручной возни с `CriteriaBuilder`.

## Q6. Что такое JPA Metamodel и как его использовать?

**Metamodel** — это сгенерированные на этапе компиляции классы-двойники entity (для `User` — `User_`), где каждое поле описано как типизированный атрибут. Они дают type-safe доступ к полям в Criteria: вместо строки `user.get("email")` пишут `user.get(User_.email)`. Разница в том, что строку компилятор не проверяет, а `User_.email` — да.

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

Annotation processor `hibernate-jpamodelgen` при сборке автоматически генерирует `*_.java` классы. Главная выгода — рефакторинг становится безопасным: переименовали или удалили поле `User.email` → исчезает `User_.email` → код, где использовалось старое имя, перестаёт компилироваться. Со строковым `"email"` такая ошибка всплыла бы только в рантайме.

## Q7. Как делать subqueries?

Подзапросы в JPQL пишут внутри `WHERE` — как и в SQL. Поддерживаются `IN`, `EXISTS`, коррелированные подзапросы (ссылаются на внешнюю таблицу) и кванторы `ALL`/`ANY`/`SOME`. Важное ограничение: в стандартном JPQL подзапрос нельзя поставить во `FROM` — только в `WHERE`/`HAVING`. В Criteria API за подзапрос отвечает отдельный объект `Subquery`.

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

cq.select(user).where(user.get("id").in(subquery));
```

## Q8. Что такое Spring Data Specifications?

**Specifications** — обёртка Spring Data над Criteria API: каждый фильтр оформляется как `Specification<T>` (по сути лямбда `(root, query, cb) -> Predicate`), а потом фильтры комбинируются через `.and()`/`.or()`. Это решает главную боль голого Criteria — динамические запросы без лестницы if-else внутри `CriteriaBuilder`. Репозиторий должен наследовать `JpaSpecificationExecutor<T>`, после чего доступен `findAll(spec)`.

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

Здесь каждый необязательный параметр фильтра добавляет свой `Specification` только если он задан, а `Specification.where(null)` даёт нейтральную стартовую точку — итог получается читаемым и легко расширяемым.

**Преимущество**: динамические фильтры без громоздкого ручного Criteria, плюс переиспользование отдельных спецификаций между запросами.

## Q9. Что такое @NamedQuery и когда его использовать?

**@NamedQuery** — это статически заданный JPQL-запрос с именем, объявленный аннотацией на entity. По имени его потом вызывают через `createNamedQuery`. Главный смысл — вынести запрос из кода в одно место и проверить его заранее.

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
- **Валидация при старте** — запрос парсится при инициализации `EntityManagerFactory`, поэтому синтаксическая ошибка падает на старте приложения, а не в проде при первом вызове.
- **Централизованное хранение** — все запросы entity собраны в одном месте.
- **Производительность** — запрос парсится один раз, дальше переиспользуется готовый план.

**Недостатки**: запрос оторван от места использования (приходится прыгать между методом и entity). Поэтому в Spring Data JPA вместо `@NamedQuery` обычно пишут `@Query` прямо над методом репозитория — те же плюсы валидации, но запрос рядом с вызовом.

## Q10. Как использовать native SQL запросы?

Native SQL — это запрос на чистом SQL конкретной БД, который выполняют через `createNativeQuery` (или `@Query(nativeQuery = true)` в Spring Data). Результат можно мапить обратно в entity (`resultClass`/`@EntityResult`) или в произвольные колонки через `@SqlResultSetMapping`. Берут его, когда возможностей JPQL не хватает — ценой потери переносимости между СУБД.

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

**Когда использовать**:
- **БД-специфичные возможности** — PostgreSQL PIVOT, SQL Server рекурсивные CTE и прочее, чего нет в JPQL.
- **Сложные CTE** — стандартный JPQL их не поддерживает.
- **Критичные по производительности запросы** — когда нужно полностью контролировать SQL без накладных расходов ORM-слоя.

**Подводный камень**: native SQL привязывает код к конкретной СУБД и не участвует в кэшировании/dirty-checking так прозрачно, как JPQL, поэтому держите его как крайнее средство.

## Q11. Что такое @EntityGraph и как он предотвращает проблему N+1?

**@EntityGraph** декларативно описывает, какие связи загрузить вместе с entity одним запросом, не переписывая сам запрос. Hibernate генерирует JOIN под эти связи, поэтому коллекции приходят сразу — и проблема N+1 (отдельный SQL на каждую lazy-связь) исчезает. Граф можно задать заранее через `@NamedEntityGraph` или собрать ad-hoc списком `attributePaths`.

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

**@EntityGraph vs JOIN FETCH** — оба решают N+1, но по-разному:
- `@EntityGraph` — декларативный: что грузить, указано аннотацией отдельно от текста запроса, поэтому один граф переиспользуется разными методами.
- `JOIN FETCH` — императивный: fetch встроен прямо в JPQL, виден в самом запросе, но привязан к нему.

## Q12. Какие типичные ошибки при работе с JPQL?

Подборка граблей, на которые наступают чаще всего — половина из них компилируется и падает только в рантайме или тихо роняет производительность.

1. **`SELECT *` не работает** — JPQL оперирует entity, а не колонками; нужно либо выбрать алиас entity, либо перечислить поля:

```java
// ОШИБКА
em.createQuery("SELECT * FROM User")

// ХОРОШО
em.createQuery("SELECT u FROM User u", User.class)
// или: "FROM User" (short form)
```

2. **`IN` с коллекцией** — передавайте список через named parameter; positional `?1` с коллекцией ведёт себя непредсказуемо:

```java
// ОШИБКА — кажется логичным
"FROM User u WHERE u.email IN ?1"  // positional с коллекцией может не работать

// ХОРОШО — named parameter
"FROM User u WHERE u.email IN :emails"
query.setParameter("emails", List.of("alice@x.com", "bob@x.com"))
```

3. **`UPDATE`/`DELETE` без `@Modifying`** — без этой аннотации Spring Data попытается выполнить запрос как `SELECT` и упадёт; вдобавок методу нужна транзакция:

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

4. **`JOIN FETCH` вместе с пагинацией** — Hibernate не может применить `LIMIT` на уровне SQL (строк после JOIN больше, чем сущностей), поэтому тянет всё в память и режет страницу там, выдавая warning `HHH000104`:

```java
// ПЛОХО — Hibernate выдаст warning и сделает pagination в памяти!
em.createQuery("FROM User u JOIN FETCH u.orders", User.class)
    .setFirstResult(0).setMaxResults(10);
```

5. **Забытый `DISTINCT` при `JOIN FETCH` коллекции** — JOIN размножает родителя по числу детей, и без `DISTINCT` в списке окажутся дубли:

```java
// Без distinct — дубли пользователей с каждым его заказом
em.createQuery("FROM User u JOIN FETCH u.orders", User.class)

// С distinct — уникальные пользователи
em.createQuery("SELECT DISTINCT u FROM User u JOIN FETCH u.orders", User.class)
```

## Q13. Что такое Projection в JPQL/Criteria?

**Projection** — выбор только нужных полей вместо загрузки entity целиком. Зачем: меньше данных из БД и без накладных расходов на управление состоянием managed-сущностей — это заметно ускоряет списки и отчёты. Способов несколько: сырой `Object[]`, DTO через конструктор (`SELECT new ...`), `Tuple` в Criteria и interface-проекции в Spring Data.

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

**Сценарий применения**: read-only выборки, где из entity нужна пара полей — списки, дашборды, отчёты. Для DTO-конструктора в JPQL обязательно полное имя класса (`SELECT new com.example.UserDto(...)`), иначе Hibernate не найдёт конструктор.

## Q14. Как реализовать pagination в JPA?

Базовый механизм — `setFirstResult` (offset) и `setMaxResults` (limit) на запросе; общее количество для расчёта числа страниц считают отдельным `COUNT`-запросом. В Spring Data это упаковано в `Pageable`/`Page`: репозиторий принимает `Pageable`, а `Page` уже содержит и контент, и `totalElements`/`totalPages` (за второй COUNT-запрос платит Spring).

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

**Проблема pagination + JOIN FETCH** — см. Q12.4.

## Q15. Какие best practices при работе с JPA queries?

Общая логика — выбирать самый простой инструмент, которого хватает: derived-метод для тривиального поиска, `@Query` для среднего запроса, Criteria/Specifications для динамики, native SQL только когда JPQL бессилен. Ниже — чек-лист по нарастанию сложности.

1. **Для простых случаев — derived-методы Spring Data JPA**; имя метода само превращается в запрос:

```java
List<User> findByEmailAndActive(String email, boolean active);
Optional<User> findByEmail(String email);
long countByCountry(String country);
```

2. **`@Query`** — для запросов посложнее, с named parameter `:parameter`:

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

## Q16. (!) Что HQL 6 добавляет поверх стандартного JPQL (set-операции, оконные функции, CTE)?

`HQL` (язык запросов Hibernate) — надмножество `JPQL`. В `Hibernate 6` он получил возможности, которых в стандартном `JPQL` нет:

- **Set-операции** — `UNION`/`UNION ALL`, `INTERSECT`, `EXCEPT` между подзапросами.
- **Оконные функции** — `over()` с `row_number()`, `rank()`, `dense_rank()`, агрегатами и т.д.
- **CTE** — общие табличные выражения `with x as (...)`, включая **рекурсивные** (`with recursive`).

```java
// Оконная функция в HQL 6
List<?> rows = session.createQuery(
    "select e.name, rank() over (partition by e.dept order by e.salary desc) " +
    "from Employee e", Object[].class).getResultList();
```

Это снимает прежнее ограничение: раньше для `CTE`, `UNION` и оконных функций приходилось уходить в native SQL.

**Итог:** в `Hibernate 6` многое из «БД-специфичного SQL» (set-операции, window-функции, рекурсивные CTE) доступно прямо в `HQL` — переносимо между диалектами и без падения в native query. Стандартный `JPQL` этого по-прежнему не умеет.

## See also

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
