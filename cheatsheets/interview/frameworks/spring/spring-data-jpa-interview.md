---
title: "Вопросы на собеседовании: Spring Data JPA"
description: "Полное покрытие Spring Data JPA: репозитории, @Query, Specifications, проекции, аудит, пагинация, жизненный цикл сущностей, N+1, производительность."
tags:
  - interview
  - frameworks
  - spring-data-jpa-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Data JPA"
  - "Spring Data JPA interview"
  - "Spring Data JPA собеседование"
prerequisites:
  - "[[spring-data-jpa]]"
next: []
updated: "2026-05-05"
---
# Вопросы на собеседовании: `Spring Data JPA`

Полное покрытие `Spring Data JPA`: репозитории, `@Query`, `Specifications`, проекции, аудит, пагинация, жизненный цикл сущностей, N+1, производительность.

**`Spring Data JPA`** — подпроект `Spring Data`, который радикально упрощает работу с `JPA`-репозиториями. На собеседованиях это одна из самых частых тем для Java-backend-разработчиков: от базовых вопросов про иерархию репозиториев до глубоких — про N+1, блокировки и оптимизацию запросов. Тесно связан с [Hibernate](../../databases/hibernate-interview.md) как реализацией `JPA`.

## Полезные ссылки

### Официальная документация

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/) — актуальная документация
- [Jakarta Persistence Specification](https://jakarta.ee/specifications/persistence/) — спецификация JPA
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/) — документация Hibernate

### Baeldung

- [Introduction to Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa) — основы: репозитории, иерархия интерфейсов
- [Spring Data JPA @Query](https://www.baeldung.com/spring-data-jpa-query) — JPQL и нативные запросы через @Query
- [Derived Query Methods in Spring Data JPA](https://www.baeldung.com/spring-data-derived-queries) — методы запросов по именованию
- [REST Query Language with Spring Data JPA Specifications](https://www.baeldung.com/rest-api-search-language-spring-data-specifications) — динамические запросы через Specification
- [Joining Tables With Spring Data JPA Specifications](https://www.baeldung.com/spring-jpa-joining-tables) — JOIN через Specification API
- [Spring Data JPA Query by Example](https://www.baeldung.com/spring-data-query-by-example) — запросы через объект-образец
- [Types of JPA Queries](https://www.baeldung.com/jpa-queries) — обзор всех типов JPA-запросов

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Spring Data JPA**
- [Q1. (!) Что такое `Spring Data JPA` и зачем он нужен?](#q1--что-такое-spring-data-jpa-и-зачем-он-нужен)
- [Q2. (!) Какова иерархия репозиториев в `Spring Data`?](#q2--какова-иерархия-репозиториев-в-spring-data)
- [Q3. Что такое `JPQL` и чем он отличается от `SQL`?](#q3-что-такое-jpql-и-чем-он-отличается-от-sql)
- [Q4. Как `Spring Data JPA` создаёт реализацию репозиториев?](#q4-как-spring-data-jpa-создаёт-реализацию-репозиториев)

**Query Methods и @Query**
- [Q5. (!) Что такое `Query Methods` и какие ключевые слова поддерживаются?](#q5--что-такое-query-methods-и-какие-ключевые-слова-поддерживаются)
- [Q6. (!) Как работает аннотация `@Query`?](#q6--как-работает-аннотация-query)
- [Q7. Как выполнять нативные `SQL`-запросы?](#q7-как-выполнять-нативные-sql-запросы)
- [Q8. Что такое `@Modifying` и когда его использовать?](#q8-что-такое-modifying-и-когда-его-использовать)
- [Q9. Как использовать `@Query` с `SpEL`?](#q9-как-использовать-query-с-spel)

**Транзакции и блокировки**
- [Q10. (!) Что такое `@Transactional` и как она работает через прокси?](#q10--что-такое-transactional-и-как-она-работает-через-прокси)
- [Q11. (!) Как работает `propagation` транзакций?](#q11--как-работает-propagation-транзакций)
- [Q12. (!) Что такое `@Lock` и стратегии блокировок?](#q12--что-такое-lock-и-стратегии-блокировок)

**Проекции и DTO**
- [Q13. (!) Что такое `Projections` и какие виды бывают?](#q13--что-такое-projections-и-какие-виды-бывают)
- [Q14. Как использовать динамические проекции?](#q14-как-использовать-динамические-проекции)

**Пагинация и сортировка**
- [Q15. (!) Как работает пагинация в `Spring Data JPA`?](#q15--как-работает-пагинация-в-spring-data-jpa)
- [Q16. В чём разница между `Page`, `Slice` и `List`?](#q16-в-чём-разница-между-page-slice-и-list)

**Specifications и динамические запросы**
- [Q17. (!) Что такое `Specifications` и как их использовать?](#q17--что-такое-specifications-и-как-их-использовать)

**Аудит**
- [Q18. (!) Как работает `Auditing` в `Spring Data JPA`?](#q18--как-работает-auditing-в-spring-data-jpa)

**Жизненный цикл сущностей**
- [Q19. (!) Какие состояния имеет сущность в `JPA`?](#q19--какие-состояния-имеет-сущность-в-jpa)
- [Q20. Что такое `@EntityListeners` и callback-методы жизненного цикла?](#q20-что-такое-entitylisteners-и-callback-методы-жизненного-цикла)
- [Q21. Что такое `FlushMode` и когда его менять?](#q21-что-такое-flushmode-и-когда-его-менять)

**Fetch стратегии и N+1**
- [Q22. (!) Что такое `FetchType LAZY` vs `EAGER`?](#q22--что-такое-fetchtype-lazy-vs-eager)
- [Q23. (!) Что такое N+1 проблема и как её решить?](#q23--что-такое-n1-проблема-и-как-её-решить)
- [Q24. Что такое `@EntityGraph` и как он работает?](#q24-что-такое-entitygraph-и-как-он-работает)

**Связи и каскады**
- [Q25. Что такое `CascadeType` и когда применять каскады?](#q25-что-такое-cascadetype-и-когда-применять-каскады)
- [Q26. Что такое `@Embedded` и `@Embeddable`?](#q26-что-такое-embedded-и-embeddable)

**Наследование**
- [Q27. Как работает наследование сущностей (`JPA Inheritance`)?](#q27-как-работает-наследование-сущностей-jpa-inheritance)

**Batch-обработка и производительность**
- [Q28. (!) Как работает batch-обработка?](#q28--как-работает-batch-обработка)
- [Q29. Как настроить второуровневый кэш (`L2 cache`)?](#q29-как-настроить-второуровневый-кэш-l2-cache)

**Soft Delete и фильтры**
- [Q30. Что такое `Soft Delete` и как реализовать?](#q30-что-такое-soft-delete-и-как-реализовать)
- [Q31. Что такое `Hibernate Filters`?](#q31-что-такое-hibernate-filters)

**Custom Repositories**
- [Q32. Как создать custom repository implementation?](#q32-как-создать-custom-repository-implementation)
- [Q33. Что такое derived delete и `deleteBy`?](#q33-что-такое-derived-delete-и-deleteby)

**Тестирование**
- [Q34. (!) Как тестировать `Spring Data JPA` репозитории?](#q34--как-тестировать-spring-data-jpa-репозитории)

**Best Practices**
- [Q35. Какие best practices для производительности `JPA`?](#q35-какие-best-practices-для-производительности-jpa)

**Продвинутые возможности**
- [Q36. (!) Как работают derived query methods и каков их синтаксис?](#q36--как-работают-derived-query-methods-и-каков-их-синтаксис)
- [Q37. (!) Как использовать `Specification` API для построения динамических запросов?](#q37--как-использовать-specification-api-для-построения-динамических-запросов)
- [Q38. (!) Как работают `Projection`-интерфейсы в Spring Data JPA?](#q38--как-работают-projection-интерфейсы-в-spring-data-jpa)
- [Q39. (!) Как работает аудит через `@CreatedDate`, `@LastModifiedDate` и `@EnableJpaAuditing`?](#q39--как-работает-аудит-через-createddate-lastmodifieddate-и-enablejpaauditing)
- [Q40. (!) Как интегрировать `Flyway` со Spring Data JPA?](#q40--как-интегрировать-flyway-со-spring-data-jpa)
- [Q41. (!) Как работает `@EntityGraph` и когда он предпочтительнее `JOIN FETCH`?](#q41--как-работает-entitygraph-и-когда-он-предпочтительнее-join-fetch)
- [Q42. Как корректно использовать `@Modifying` с `@Query` для bulk-операций?](#q42-как-корректно-использовать-modifying-с-query-для-bulk-операций)

---

## Q1. (!) Что такое `Spring Data JPA` и зачем он нужен?

**`Spring Data JPA`** — надстройка над `JPA` (`Jakarta Persistence API`), которая избавляет от boilerplate при работе с репозиториями: вы объявляете интерфейс, а реализацию `Spring` генерирует сам. Это подпроект `Spring Data`, специализированный под реляционные БД через `ORM`.

Главная боль, которую он решает: в «голом» `JPA` для каждого репозитория приходилось писать класс с инжектированным `EntityManager` и вручную собирать запросы. `Spring Data JPA` убирает этот слой целиком.

**Что даёт:**
- Автоматическая реализация репозиториев по интерфейсу — не нужен `@Repository`-класс с `EntityManager`
- Генерация запросов по именам методов (`findByEmail`, `countByStatus`) — без единой строки SQL
- Встроенная поддержка пагинации, сортировки, `Specifications`
- Интеграция с `@Transactional`, аудитом, кэшированием

```java
// Минимальный рабочий пример — ни одной строки реализации
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // getters, setters
}

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatusAndLastName(UserStatus status, String lastName);
    boolean existsByEmail(String email);
}
```

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> findActiveUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.ACTIVE, pageable);
    }
}
```

На собеседовании важно показать, что `Spring Data JPA` — это не замена `JPA`, а надстройка. Под капотом используется [Hibernate](../../databases/hibernate-interview.md) (или другая реализация `JPA`), `Spring Data` лишь генерирует реализацию репозиториев.

## Q2. (!) Какова иерархия репозиториев в `Spring Data`?

Иерархия интерфейсов репозиториев — от общего к конкретному:

```mermaid
graph TD
    R["Repository&lt;T, ID&gt;<br/>маркерный интерфейс"] --> CR["CrudRepository&lt;T, ID&gt;<br/>save, findById, delete, count"]
    CR --> LCR["ListCrudRepository&lt;T, ID&gt;<br/>findAll() → List"]
    CR --> PSR["PagingAndSortingRepository&lt;T, ID&gt;<br/>findAll(Pageable), findAll(Sort)"]
    PSR --> JPR["JpaRepository&lt;T, ID&gt;<br/>flush, saveAllAndFlush,<br/>deleteInBatch, getReferenceById"]

    style JPR fill:#e1f5fe,stroke:#0277bd
    style R fill:#fff3e0,stroke:#e65100
```

```java
// CrudRepository — базовый CRUD
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);
    Optional<T> findById(ID id);
    Iterable<T> findAll();
    long count();
    void deleteById(ID id);
    boolean existsById(ID id);
}

// JpaRepository — расширяет всё + JPA-специфичные методы
public interface JpaRepository<T, ID> extends
        ListCrudRepository<T, ID>,
        ListPagingAndSortingRepository<T, ID>,
        QueryByExampleExecutor<T> {

    void flush();
    <S extends T> S saveAndFlush(S entity);
    void deleteAllInBatch();
    T getReferenceById(ID id); // lazy proxy, без запроса в БД
}
```

Логика иерархии простая: каждый уровень добавляет возможности. `Repository` — пустой маркер, `CrudRepository` даёт базовый CRUD, `PagingAndSortingRepository` добавляет пагинацию и сортировку, а `JpaRepository` — JPA-специфику (`flush`, batch-удаление, `getReferenceById`). Чем выше по иерархии наследуетесь, тем меньше методов получаете в API репозитория.

**Когда что использовать:**
- `CrudRepository` — когда нужен минимум (микросервис с простыми операциями)
- `JpaRepository` — стандартный выбор для большинства проектов (пагинация, `flush`, batch-удаление)
- `Repository` — маркерный интерфейс, когда хочется объявить только конкретные методы и не тащить весь CRUD в публичный API

## Q3. Что такое `JPQL` и чем он отличается от `SQL`?

**JPQL** (`Java Persistence Query Language`) — язык запросов `JPA`, который оперирует **сущностями и их Java-полями**, а не таблицами и колонками БД. Вы пишете запрос в терминах объектной модели, а провайдер `JPA` транслирует его в `SQL` под конкретный диалект БД.

Ключевое следствие: один и тот же `JPQL`-запрос работает на PostgreSQL, MySQL, Oracle без переписывания — переносимость даётся «бесплатно». Платите за это ограниченным набором функций (то, что не входит в спецификацию, в `JPQL` не выразить).

| Аспект | JPQL | SQL |
|--------|------|-----|
| Оперирует | Сущностями (`User`) | Таблицами (`users`) |
| Поля | `u.firstName` | `u.first_name` |
| Наследование | Полиморфные запросы | Нет |
| Переносимость | Да (диалект абстрагирован) | Зависит от БД |
| Функции | Ограниченный набор | Полный набор БД |

```java
// JPQL — имена сущностей и полей Java
@Query("SELECT u FROM User u WHERE u.status = :status AND u.lastName LIKE :pattern")
List<User> findActiveByLastName(@Param("status") UserStatus status,
                                 @Param("pattern") String pattern);

// SQL — имена таблиц и колонок БД
@Query(value = "SELECT * FROM users WHERE status = :status AND last_name LIKE :pattern",
       nativeQuery = true)
List<User> findActiveByLastNameNative(@Param("status") String status,
                                      @Param("pattern") String pattern);
```

Для сложных запросов, которые не выразить через `Query Methods`, но при этом хочется сохранить переносимость между БД — используйте `JPQL`. Для запросов с оконными функциями, `CTE`, специфичными функциями БД — нативный `SQL`.

## Q4. Как `Spring Data JPA` создаёт реализацию репозиториев?

На старте приложения `Spring` сканирует интерфейсы-репозитории и создаёт для каждого **прокси-объект** — вы никогда не пишете реализацию руками. Базовый CRUD при этом делегируется в готовый класс `SimpleJpaRepository`, а кастомные методы превращаются в запросы.

Понимание этого механизма помогает на собеседовании объяснить, почему интерфейс без реализации «волшебным образом» работает: магии нет — есть фабрика прокси и парсер имён методов.

```mermaid
graph LR
    A["@EnableJpaRepositories<br/>сканирование пакета"] --> B["JpaRepositoryFactoryBean<br/>фабрика для каждого интерфейса"]
    B --> C["SimpleJpaRepository<br/>базовая реализация CRUD"]
    B --> D["Query Method Parser<br/>разбор имён методов"]
    D --> E["PartTree<br/>дерево предикатов"]
    E --> F["JPA Criteria / JPQL<br/>сгенерированный запрос"]

    style C fill:#c8e6c9,stroke:#2e7d32
    style F fill:#e1f5fe,stroke:#0277bd
```

Процесс:
1. `@EnableJpaRepositories` (автоматически включена в `Spring Boot`) запускает сканирование
2. Для каждого интерфейса создаётся `JpaRepositoryFactoryBean`
3. Методы `CrudRepository` делегируются в `SimpleJpaRepository` (которая использует `EntityManager`)
4. Кастомные методы (`findByEmail`) разбираются `PartTree`-парсером и превращаются в `Criteria`-запросы
5. Методы с `@Query` — напрямую в `JPQL` или нативный `SQL`

```java
// Что Spring генерирует "под капотом" для findByEmail
// (упрощённо — реальный код в SimpleJpaRepository + QueryPartTree)
public Optional<User> findByEmail(String email) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    Root<User> root = query.from(User.class);
    query.where(cb.equal(root.get("email"), email));
    List<User> results = entityManager.createQuery(query).getResultList();
    return results.stream().findFirst();
}
```

## Q5. (!) Что такое `Query Methods` и какие ключевые слова поддерживаются?

`Query Methods` — методы репозитория, у которых **само имя метода и есть запрос**: `Spring Data` разбирает имя по словарю ключевых слов и генерирует `JPQL`. Это основной механизм для простых и средних по сложности выборок — писать SQL руками не нужно.

Работает так: парсер `PartTree` делит имя на префикс (`findBy`, `countBy`, `existsBy`, `deleteBy`) и набор условий, соединённых `And`/`Or`. Каждое условие — это поле сущности плюс опциональный оператор (`Between`, `GreaterThan`, `Like` и т.д.).

**Поддерживаемые ключевые слова:**

| Ключевое слово | Пример | JPQL эквивалент |
|----------------|--------|-----------------|
| `findBy` | `findByEmail(String)` | `WHERE email = ?1` |
| `countBy` | `countByStatus(Status)` | `SELECT COUNT(*) WHERE status = ?1` |
| `existsBy` | `existsByEmail(String)` | `SELECT CASE WHEN COUNT > 0` |
| `deleteBy` | `deleteByStatus(Status)` | `DELETE WHERE status = ?1` |
| `And` / `Or` | `findByFirstNameAndLastName` | `WHERE ... AND ...` |
| `Between` | `findByAgeBetween(int, int)` | `WHERE age BETWEEN ?1 AND ?2` |
| `LessThan` / `GreaterThan` | `findByAgeGreaterThan(int)` | `WHERE age > ?1` |
| `Like` / `Containing` | `findByNameContaining(String)` | `WHERE name LIKE %?1%` |
| `In` | `findByStatusIn(Collection)` | `WHERE status IN (?1)` |
| `IsNull` / `IsNotNull` | `findByDeletedAtIsNull()` | `WHERE deleted_at IS NULL` |
| `OrderBy` | `findByStatusOrderByCreatedAtDesc` | `ORDER BY created_at DESC` |
| `Top` / `First` | `findTop5ByStatus(Status)` | `LIMIT 5` |
| `Distinct` | `findDistinctByLastName(String)` | `SELECT DISTINCT ...` |

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // Простой поиск
    Optional<User> findByEmail(String email);

    // Комбинация условий
    List<User> findByStatusAndLastNameContainingIgnoreCase(
            UserStatus status, String lastNamePart);

    // Сортировка и лимит
    List<User> findTop10ByStatusOrderByCreatedAtDesc(UserStatus status);

    // Проверка существования
    boolean existsByEmailAndStatus(String email, UserStatus status);

    // Подсчёт
    long countByStatus(UserStatus status);

    // Поиск по вложенному полю (через _ или camelCase)
    List<User> findByAddress_City(String city);

    // Возврат Stream (обязательно в @Transactional)
    @Transactional(readOnly = true)
    Stream<User> findByStatus(UserStatus status);
}
```

**Подводный камень:** имя метода — это контракт, и при росте числа условий оно становится нечитаемым (`findByStatusAndCityAndAgeGreaterThanAndCreatedAtAfter`). Как только имя перестаёт читаться с первого взгляда — переходите на `@Query` (явный JPQL) или `Specifications` (динамическая сборка условий).

## Q6. (!) Как работает аннотация `@Query`?

`@Query` задаёт `JPQL` или нативный `SQL` прямо на методе репозитория — когда выразительности `Query Methods` уже не хватает (JOIN'ы, агрегации, конструкторские проекции, оконные функции).

Запрос привязывается к методу аннотацией, параметры подставляются по `@Param` (именованные) или по позиции (`?1`, `?2`). Именованные предпочтительнее: при рефакторинге сигнатуры позиционные легко перепутать местами.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL с именованными параметрами
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findUserOrders(@Param("userId") Long userId,
                                @Param("status") OrderStatus status);

    // JPQL с позиционными параметрами
    @Query("SELECT o FROM Order o WHERE o.createdAt > ?1 ORDER BY o.total DESC")
    List<Order> findRecentOrders(LocalDateTime after);

    // Агрегация
    @Query("SELECT new com.example.dto.OrderStats(o.status, COUNT(o), SUM(o.total)) " +
           "FROM Order o GROUP BY o.status")
    List<OrderStats> getOrderStatistics();

    // JOIN FETCH для избежания N+1
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    // Пагинация с @Query
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    // @Modifying для UPDATE/DELETE
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.createdAt < :before")
    int archiveOldOrders(@Param("status") OrderStatus status,
                         @Param("before") LocalDateTime before);
}
```

**Приоритет разрешения запросов** (от высшего к низшему):
1. `@Query` на методе
2. Named Query (`@NamedQuery` на сущности)
3. Разбор имени метода (`Query Method`)

## Q7. Как выполнять нативные `SQL`-запросы?

Нативный `SQL` (`@Query(..., nativeQuery = true)`) нужен там, где `JPQL` бессилен — когда требуются специфичные возможности конкретной БД: оконные функции, `CTE`, полнотекстовый поиск, `JSON`-операторы.

Цена — потеря переносимости: такой запрос привязан к диалекту вашей БД. Поэтому правило простое — нативный SQL только когда `JPQL` действительно не справляется, а не «по привычке».

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Простой нативный запрос
    @Query(value = "SELECT * FROM products WHERE category = :cat AND price < :maxPrice",
           nativeQuery = true)
    List<Product> findCheapProducts(@Param("cat") String category,
                                     @Param("maxPrice") BigDecimal maxPrice);

    // Нативный с пагинацией — Spring подставит LIMIT/OFFSET
    @Query(value = "SELECT * FROM products WHERE active = true ORDER BY rating DESC",
           countQuery = "SELECT COUNT(*) FROM products WHERE active = true",
           nativeQuery = true)
    Page<Product> findTopRated(Pageable pageable);

    // Оконная функция — невозможно в JPQL
    @Query(value = """
            SELECT p.*, RANK() OVER (PARTITION BY p.category ORDER BY p.rating DESC) as rnk
            FROM products p
            WHERE p.active = true
            """, nativeQuery = true)
    List<Object[]> findProductsWithRank();

    // Проекция с нативным запросом через интерфейс
    @Query(value = "SELECT name, price, rating FROM products WHERE category = :cat",
           nativeQuery = true)
    List<ProductSummary> findSummaryByCategory(@Param("cat") String category);
}

// Интерфейсная проекция — алиасы колонок = имена геттеров
interface ProductSummary {
    String getName();
    BigDecimal getPrice();
    Double getRating();
}
```

**Подводный камень:** при нативной пагинации всегда указывайте `countQuery`. Для `JPQL` `Spring` умеет вывести count-запрос автоматически, но для нативного SQL он лишь оборачивает ваш запрос, и для сложных конструкций (с `GROUP BY`, оконными функциями) такая обёртка ломается или считает неверно.

## Q8. Что такое `@Modifying` и когда его использовать?

`@Modifying` помечает метод как изменяющий данные (`UPDATE`, `DELETE`, `INSERT`). По умолчанию `Spring Data` считает любой `@Query` запросом на чтение и вызывает `getResultList()` — без `@Modifying` DML-запрос упадёт с ошибкой.

Главная ценность — bulk-операции: один `UPDATE`/`DELETE` напрямую в БД вместо загрузки N сущностей в память, их изменения по одной и сброса dirty checking'ом. На больших объёмах это разница в порядки.

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // Bulk UPDATE — один запрос вместо загрузки N сущностей
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE User u SET u.status = :newStatus WHERE u.lastLoginAt < :before")
    int deactivateInactiveUsers(@Param("newStatus") UserStatus newStatus,
                                 @Param("before") LocalDateTime before);

    // Bulk DELETE
    @Modifying
    @Query("DELETE FROM User u WHERE u.status = 'DELETED' AND u.deletedAt < :before")
    int purgeDeletedUsers(@Param("before") LocalDateTime before);

    // Нативный INSERT (например, для таблицы связей)
    @Modifying
    @Query(value = "INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)",
           nativeQuery = true)
    void assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
```

**Ключевые параметры** (оба про синхронизацию с кэшем первого уровня):
- `clearAutomatically = true` — очищает persistence context **после** запроса: ранее загруженные сущности устарели, их нужно «забыть»
- `flushAutomatically = true` — сбрасывает накопленные изменения в БД **перед** запросом, чтобы bulk-операция увидела актуальные данные

**Подводный камень:** bulk-запрос идёт напрямую в БД, минуя кэш первого уровня. Без `clearAutomatically` сущности в persistence context остаются со старыми значениями, и повторный `findById` в той же транзакции вернёт кэшированную (устаревшую) версию, а не то, что реально лежит в БД.

## Q9. Как использовать `@Query` с `SpEL`?

`SpEL` (`Spring Expression Language`) внутри `@Query` позволяет параметризовать сам текст запроса — чаще всего, чтобы написать один обобщённый запрос для целого семейства сущностей в базовом репозитории.

Главный приём — `#{#entityName}`: вместо жёстко зашитого имени сущности подставляется имя из дженерика репозитория. Так один метод в `BaseRepository<T>` работает и для `User`, и для `Product`, без дублирования.

```java
// #{#entityName} — подставляется имя сущности из дженерика репозитория
public interface BaseRepository<T> extends JpaRepository<T, Long> {

    // Один запрос для любой сущности с полем "active"
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = true")
    List<T> findAllActive();

    // Подсчёт активных записей
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.active = true")
    long countActive();
}

// UserRepository наследует — #{#entityName} = "User"
public interface UserRepository extends BaseRepository<User> {
    // findAllActive() → SELECT e FROM User e WHERE e.active = true
}

// ProductRepository наследует — #{#entityName} = "Product"
public interface ProductRepository extends BaseRepository<Product> {
    // findAllActive() → SELECT e FROM Product e WHERE e.active = true
}
```

`SpEL` вычисляется при создании запроса (один раз при старте), а не при каждом вызове. Помимо `#{#entityName}` можно использовать `#{#root.args[0]}` для доступа к аргументам, но на практике именованные параметры (`@Param`) удобнее и безопаснее.

## Q10. (!) Что такое `@Transactional` и как она работает через прокси?

`@Transactional` объявляет метод (или все методы класса) транзакционным, но саму транзакцию открывает не ваш код, а **прокси**, в который `Spring` оборачивает бин (через `CGLIB` для классов или `JDK Proxy` для интерфейсов).

Механика: при вызове извне обращение идёт сначала в прокси. Тот открывает транзакцию через `TransactionManager`, вызывает реальный метод и по результату делает `commit` (успех) или `rollback` (если вылетел `RuntimeException`). Сам бизнес-код о транзакции ничего не знает — он просто работает с `EntityManager`.

```mermaid
sequenceDiagram
    participant C as Controller
    participant P as Proxy @Transactional
    participant S as Service (реальный объект)
    participant TM as TransactionManager
    participant DB as Database

    C->>P: вызов метода
    P->>TM: beginTransaction()
    TM->>DB: BEGIN
    P->>S: вызов реального метода
    S->>DB: SQL запросы
    S-->>P: return / exception
    alt Успех
        P->>TM: commit()
        TM->>DB: COMMIT
    else RuntimeException
        P->>TM: rollback()
        TM->>DB: ROLLBACK
    end
    P-->>C: результат
```

```java
@Service
@Transactional(readOnly = true) // на уровне класса — все методы read-only
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    // read-only наследуется от класса
    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional // переопределяем — этот метод пишет
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setTotal(request.total());
        return orderRepository.save(order);
    }

    @Transactional(
        isolation = Isolation.REPEATABLE_READ,
        timeout = 10,
        rollbackFor = PaymentException.class // откат и для checked exception
    )
    public Order processPayment(Long orderId) {
        Order order = findById(orderId);
        paymentService.charge(order); // если выбросит PaymentException — rollback
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }
}
```

**Критический подводный камень — self-invocation:** транзакцию создаёт прокси, а внутренний вызов `this.method()` идёт напрямую в объект, минуя прокси. Поэтому вызов `@Transactional`-метода из того же класса транзакцию **не открывает** — аннотация молча игнорируется:

```java
@Service
public class UserService {

    @Transactional
    public void updateUser(Long id) { /* транзакция работает */ }

    public void doSomething() {
        // ОШИБКА: вызов через this, а не через прокси — транзакции НЕТ!
        this.updateUser(1L);
    }
}
```

Решения: вынести метод в другой бин, инжектировать `self` через `ApplicationContext`, или использовать `@Transactional` на внешнем вызывающем методе.

## Q11. (!) Как работает `propagation` транзакций?

`Propagation` (распространение) определяет, что делать, когда `@Transactional`-метод вызван внутри уже идущей транзакции: присоединиться к ней, открыть новую, приостановить текущую или вообще запретить такой вызов.

По умолчанию — `REQUIRED`: метод работает в текущей транзакции, если она есть, иначе создаёт свою. В 90% случаев этого достаточно; остальные режимы нужны для специфических задач (независимый аудит, вложенные savepoint'ы, запрет транзакции).

| Propagation | Есть транзакция | Нет транзакции |
|-------------|-----------------|----------------|
| `REQUIRED` (default) | Присоединяется | Создаёт новую |
| `REQUIRES_NEW` | Приостанавливает, создаёт новую | Создаёт новую |
| `NESTED` | Создаёт savepoint | Создаёт новую |
| `MANDATORY` | Присоединяется | Бросает исключение |
| `SUPPORTS` | Присоединяется | Без транзакции |
| `NOT_SUPPORTED` | Приостанавливает | Без транзакции |
| `NEVER` | Бросает исключение | Без транзакции |

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuditService auditService;

    @Transactional
    public void placeOrder(OrderRequest request) {
        Order order = orderRepository.save(new Order(request));

        // Аудит-лог должен сохраниться даже при откате основной транзакции
        auditService.logAction("ORDER_PLACED", order.getId());
    }
}

@Service
public class AuditService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, Long entityId) {
        // Выполняется в ОТДЕЛЬНОЙ транзакции
        // Если основная транзакция откатится — лог сохранится
        auditRepository.save(new AuditLog(action, entityId));
    }
}
```

**Любимый вопрос на собеседовании:** при `REQUIRED` внутренний и внешний методы делят **одну** транзакцию. Если внутренний метод бросил исключение, транзакция помечается как `rollback-only`. Даже если внешний метод поймает это исключение и продолжит работу — на коммите всё равно прилетит `UnexpectedRollbackException`, и весь блок откатится. Чтобы внутренний сбой не «отравлял» внешнюю транзакцию, выносите внутренний метод в `REQUIRES_NEW` (отдельная транзакция).

## Q12. (!) Что такое `@Lock` и стратегии блокировок?

`@Lock` задаёт стратегию блокировки строки при чтении — инструмент против гонок, когда несколько транзакций одновременно меняют одни и те же данные (классика — списание денег со счёта).

Два принципиально разных подхода:
- **Пессимистический** (`PESSIMISTIC_*`) — блокируем строку в БД сразу при чтении (`SELECT ... FOR UPDATE`). Конкуренты ждут. Подходит, когда конфликты частые и важна гарантия.
- **Оптимистический** (`OPTIMISTIC` + `@Version`) — не блокируем ничего, но при коммите проверяем, не изменил ли кто-то строку. Если изменил — `OptimisticLockException`. Подходит, когда конфликты редки и важна пропускная способность.

```java
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Пессимистическая блокировка на запись — SELECT ... FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    // Пессимистическая блокировка на чтение — SELECT ... FOR SHARE
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findByAccountNumber(String accountNumber);

    // Оптимистическая блокировка — проверка @Version при коммите
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Account> findById(Long id);
}
```

```java
// Оптимистическая блокировка через @Version
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version // автоматический инкремент при UPDATE
    private Long version;

    private BigDecimal balance;
}

// При конкурентном обновлении — OptimisticLockException
@Transactional
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    Account from = accountRepository.findByIdForUpdate(fromId)
        .orElseThrow(); // PESSIMISTIC_WRITE — строка заблокирована
    Account to = accountRepository.findByIdForUpdate(toId)
        .orElseThrow();

    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));
    // блокировки снимаются при COMMIT
}
```

| Стратегия | SQL | Когда |
|-----------|-----|-------|
| `PESSIMISTIC_WRITE` | `SELECT ... FOR UPDATE` | Обновление с конкуренцией (платежи, остатки) |
| `PESSIMISTIC_READ` | `SELECT ... FOR SHARE` | Чтение, которое не должно измениться до конца транзакции |
| `OPTIMISTIC` | Проверка `@Version` при commit | Редкие конфликты, высокий параллелизм чтения |

## Q13. (!) Что такое `Projections` и какие виды бывают?

**Projections** — способ вернуть из репозитория не всю сущность, а только нужные поля. Это уменьшает объём данных из БД, трафик и потребление памяти — одна из ключевых оптимизаций в `Spring Data JPA`.

Зачем: грузить сущность целиком ради двух полей расточительно, особенно для «широких» таблиц с десятками колонок и LOB'ами. Проекция переносит выбор полей в `SELECT`-список — БД отдаёт ровно то, что нужно.

### Закрытая интерфейсная проекция

```java
// Spring создаёт прокси, запрашивает ТОЛЬКО указанные поля
public interface UserSummary {
    String getFirstName();
    String getLastName();
    String getEmail();
}

public interface UserRepository extends JpaRepository<User, Long> {
    List<UserSummary> findByStatus(UserStatus status);
    // SQL: SELECT first_name, last_name, email FROM users WHERE status = ?
}
```

### DTO-проекция (class-based)

```java
// Record или класс с конструктором — один запрос с явным списком полей
public record UserDto(String firstName, String lastName, String email) {}

public interface UserRepository extends JpaRepository<User, Long> {

    // Через @Query с конструктором в JPQL
    @Query("SELECT new com.example.dto.UserDto(u.firstName, u.lastName, u.email) " +
           "FROM User u WHERE u.status = :status")
    List<UserDto> findDtoByStatus(@Param("status") UserStatus status);
}
```

### Открытая проекция (с вычисляемыми полями)

```java
public interface UserFullName {
    String getFirstName();
    String getLastName();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName(); // вычисляется на стороне Java
}
```

### Вложенная проекция

```java
public interface OrderWithUser {
    Long getId();
    BigDecimal getTotal();
    UserSummary getUser(); // вложенная проекция

    interface UserSummary {
        String getFirstName();
        String getEmail();
    }
}
```

**Ключевое для собеседования — разница между закрытой и открытой проекцией:**
- **Закрытая** (только геттеры существующих полей) — `Spring` знает заранее, какие колонки нужны, и генерирует `SELECT` ровно с ними. Самый эффективный вариант.
- **Открытая** (есть геттер с `@Value`/SpEL) — `Spring` не может вычислить список колонок заранее, поэтому грузит сущность целиком и считает выражение в Java. Экономии на уровне БД нет — только удобство.

Эмпирическое правило: нужна производительность — закрытая интерфейсная или DTO-проекция; нужно вычисляемое поле на лету — открытая, понимая её цену.

## Q14. Как использовать динамические проекции?

Динамическая проекция — это один метод репозитория, который возвращает разные представления данных в зависимости от переданного `Class<T>`. Тип проекции выбирается уже в рантайме, на стороне вызывающего кода.

Зачем это нужно: иначе пришлось бы плодить почти одинаковые методы — `findSummaryByStatus`, `findAdminByStatus`, `findFullByStatus`. Дженерик-метод `<T> List<T> findByStatus(status, Class<T>)` заменяет их все одним.

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // Тип проекции передаётся как параметр
    <T> List<T> findByStatus(UserStatus status, Class<T> type);

    <T> Optional<T> findByEmail(String email, Class<T> type);
}

// Использование — разные проекции для разных контекстов
interface UserSummary {
    String getFirstName();
    String getEmail();
}

interface UserAdmin {
    String getEmail();
    UserStatus getStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getLastLoginAt();
}

@Service
public class UserService {

    public List<UserSummary> getPublicList() {
        // Для публичного API — минимум данных
        return userRepository.findByStatus(UserStatus.ACTIVE, UserSummary.class);
    }

    public List<UserAdmin> getAdminList() {
        // Для админки — больше полей
        return userRepository.findByStatus(UserStatus.ACTIVE, UserAdmin.class);
    }

    public List<User> getFullList() {
        // Полная сущность (тоже работает)
        return userRepository.findByStatus(UserStatus.ACTIVE, User.class);
    }
}
```

Итог: один метод репозитория обслуживает и публичный API (минимум полей), и админку (больше полей), и работу с полной сущностью — без дублирования сигнатур.

## Q15. (!) Как работает пагинация в `Spring Data JPA`?

Пагинация — это выдача результата частями (страницами) вместо одной гигантской выборки. `Spring Data JPA` делает её из коробки: передаёте в метод репозитория объект `Pageable` (`PageRequest.of(page, size, sort)`), а `Spring` сам добавляет `LIMIT`/`OFFSET` и сортировку в запрос.

Зачем: грузить миллион строк в память ради показа 20 на странице — путь к `OutOfMemoryError` и тормозам. Пагинация ограничивает выборку на уровне БД.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Slice<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
    Page<Order> findByStatusWithItems(@Param("status") OrderStatus status, Pageable pageable);
}
```

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public Page<OrderDto> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.asc("id") // стабильная сортировка
        ));

        return orderRepository.findByStatus(OrderStatus.ACTIVE, pageable)
            .map(OrderDto::from);
    }
}
```

**В `Spring MVC`** можно принять `Pageable` напрямую как параметр контроллера — `Spring` автоматически создаст его из query-параметров `page`, `size`, `sort`:

```java
@GetMapping("/users")
public Page<UserDto> getUsers(Pageable pageable) {
    // GET /users?page=0&size=20&sort=firstName,asc&sort=lastName,asc
    return userRepository.findAll(pageable).map(UserDto::from);
}
```

## Q16. В чём разница между `Page`, `Slice` и `List`?

Все три — варианты возврата постраничной выборки, и различаются они **ценой в запросах**. Главный водораздел — нужно ли знать общее число элементов:

- `Page<T>` делает **дополнительный** `SELECT COUNT(*)`, чтобы посчитать всего элементов и страниц. Это плата за нумерацию страниц («1 2 3 ... 50»).
- `Slice<T>` count не делает: чтобы понять, есть ли следующая страница, он запрашивает `size + 1` запись. Если пришла лишняя — `hasNext() == true`.
- `List<T>` — просто страница данных без всяких метаданных.

| Тип | `COUNT` запрос | Метаданные | Когда использовать |
|-----|---------------|------------|-------------------|
| `Page<T>` | Да (доп. запрос) | Общее кол-во элементов и страниц | Классическая пагинация с номерами страниц |
| `Slice<T>` | Нет | Только `hasNext()` | Бесконечный скролл, "Load more" |
| `List<T>` | Нет | Нет | Когда метаданные не нужны |

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // Page — два запроса: SELECT + SELECT COUNT(*)
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    // Slice — один запрос (запрашивает size+1 записей, чтобы определить hasNext)
    Slice<User> findByCity(String city, Pageable pageable);

    // List — один запрос, без метаданных
    List<User> findByLastName(String lastName, Pageable pageable);
}
```

**Рекомендация:** на больших таблицах (миллионы строк) `COUNT(*)` сам по себе превращается в тяжёлый запрос, и `Page` становится узким местом. Если фронту не нужен точный номер последней страницы — берите `Slice` (или `List` + отдельно кэшированный приблизительный счётчик).

## Q17. (!) Что такое `Specifications` и как их использовать?

`Specifications` — способ собирать условия запроса в рантайме, как из кубиков, поверх `JPA Criteria API`. Каждая спецификация — один предикат (`WHERE`-условие); их можно комбинировать через `.and()`/`.or()` и применять только те, что реально заданы.

Идеальный сценарий — фильтрация с опциональными параметрами: поисковые формы и API, где пользователь может задать любое подмножество фильтров. Вместо комбинаторного взрыва методов (`findByCategory`, `findByCategoryAndPrice`, `findByPriceAndKeyword`, ...) у вас один `findAll(spec, pageable)`, в который собирается ровно нужный набор условий.

```java
// Репозиторий расширяет JpaSpecificationExecutor
public interface ProductRepository extends JpaRepository<Product, Long>,
                                            JpaSpecificationExecutor<Product> {
}

// Класс со спецификациями — переиспользуемые предикаты
public class ProductSpecs {

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> cb.between(root.get("price"), min, max);
    }

    public static Specification<Product> nameContains(String keyword) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    public static Specification<Product> hasMinRating(Double rating) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("rating"), rating);
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    public Page<Product> search(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecs.isActive());

        // Условия добавляются только если фильтр задан
        if (filter.category() != null) {
            spec = spec.and(ProductSpecs.hasCategory(filter.category()));
        }
        if (filter.minPrice() != null && filter.maxPrice() != null) {
            spec = spec.and(ProductSpecs.priceBetween(filter.minPrice(), filter.maxPrice()));
        }
        if (filter.keyword() != null) {
            spec = spec.and(ProductSpecs.nameContains(filter.keyword()));
        }
        if (filter.minRating() != null) {
            spec = spec.and(ProductSpecs.hasMinRating(filter.minRating()));
        }

        return productRepository.findAll(spec, pageable);
    }
}
```

```mermaid
graph LR
    F["ProductFilter<br/>category, price, keyword"] --> S1["Specification 1<br/>hasCategory"]
    F --> S2["Specification 2<br/>priceBetween"]
    F --> S3["Specification 3<br/>nameContains"]
    S1 --> AND["Specification.where().and().and()"]
    S2 --> AND
    S3 --> AND
    AND --> Q["Criteria Query<br/>SELECT ... WHERE ... AND ..."]

    style AND fill:#c8e6c9,stroke:#2e7d32
    style Q fill:#e1f5fe,stroke:#0277bd
```

**Преимущество перед множеством `@Query`-методов:** не нужно создавать `findByCategoryAndPriceBetween`, `findByCategory`, `findByPriceBetween` и т.д. — один метод `findAll(spec, pageable)` покрывает все комбинации.

## Q18. (!) Как работает `Auditing` в `Spring Data JPA`?

`Auditing` — автоматическое заполнение служебных полей «кто и когда создал/изменил запись», без ручного проставления дат в каждом сервисе.

Работает на трёх китах: `@EnableJpaAuditing` включает механизм, `AuditingEntityListener` (через `@EntityListeners`) перехватывает события persist/update, а аннотации на полях (`@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`) говорят, что куда писать. Имя текущего пользователя для `@CreatedBy`/`@LastModifiedBy` берётся из бина `AuditorAware`.

```java
// 1. Включить аудит
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}

// 2. Базовая аудируемая сущность
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}

// 3. Сущность наследует — поля заполняются автоматически
@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
}
```

При `save()` нового `Product`:
- `createdAt` / `updatedAt` заполняются текущим временем
- `createdBy` / `updatedBy` заполняются из `AuditorAware` (имя пользователя из `SecurityContext`)

При обновлении — меняются только `updatedAt` и `updatedBy`.

Подробнее о [Spring Security](spring-security-interview.md) — аудит тесно связан с аутентификацией.

## Q19. (!) Какие состояния имеет сущность в `JPA`?

Сущность в `JPA` проходит четыре состояния, и от текущего состояния зависит, попадут ли ваши изменения в БД. Понимание этого цикла — ключ к работе с `Persistence Context` (кэшем первого уровня) и к объяснению странных багов вроде «поменял поле, а в БД ничего не изменилось».

Коротко о состояниях:
- **New/Transient** — обычный Java-объект, `EntityManager` о нём не знает.
- **Managed/Persistent** — привязан к `EntityManager`; любое изменение полей будет синхронизировано в БД при flush (dirty checking).
- **Detached** — был managed, но контекст закрылся; изменения больше не отслеживаются.
- **Removed** — помечен на удаление, `DELETE` уйдёт при flush/commit.

```mermaid
stateDiagram-v2
    [*] --> New: new Entity()
    New --> Managed: persist() / save()
    Managed --> Detached: detach() / clear() / close()
    Managed --> Removed: remove() / delete()
    Detached --> Managed: merge()
    Removed --> [*]: commit → DELETE
    Managed --> Managed: flush() → SQL
    Managed --> [*]: commit → INSERT/UPDATE

    note right of Managed
        Отслеживается EntityManager.
        Dirty checking при flush/commit.
    end note

    note right of Detached
        Не отслеживается.
        Изменения не попадут в БД.
    end note
```

| Состояние | Описание | Отслеживается EM | В БД |
|-----------|----------|:----------------:|:----:|
| **New** (Transient) | Создан через `new`, нет `@Id` | Нет | Нет |
| **Managed** (Persistent) | Связан с `EntityManager` | Да | Да (или будет при flush) |
| **Detached** | Был managed, сессия закрыта | Нет | Да |
| **Removed** | Помечен на удаление | Да | Будет удалён при flush |

```java
@Transactional
public void entityLifecycleDemo() {
    User user = new User("John");      // NEW — не отслеживается

    entityManager.persist(user);        // MANAGED — теперь в persistence context
    user.setEmail("john@example.com");  // dirty checking — изменение обнаружится

    entityManager.flush();              // SQL: INSERT + UPDATE (или один INSERT)

    entityManager.detach(user);         // DETACHED — изменения не отслеживаются
    user.setEmail("new@example.com");   // НЕ попадёт в БД!

    User merged = entityManager.merge(user); // MANAGED — merged — новый managed-объект
    // ВНИМАНИЕ: user всё ещё detached! Работать нужно с merged

    entityManager.remove(merged);       // REMOVED — будет DELETE при commit
}
```

## Q20. Что такое `@EntityListeners` и callback-методы жизненного цикла?

`JPA` даёт хуки на события жизненного цикла сущности — момент перед/после `INSERT`, `UPDATE`, `DELETE` и загрузки из БД. Это места, куда можно вшить логику, которая должна срабатывать автоматически при изменении сущности.

Два способа объявить callback'и:
- **Прямо в сущности** — методы с `@PrePersist`, `@PreUpdate` и т.д. Удобно для простой логики над собственными полями (проставить дату, дефолтный статус).
- **В отдельном listener-классе** через `@EntityListeners` — чище для сложной логики (логирование, отправка событий), не засоряет сущность.

```java
// Callback-аннотации прямо на сущности
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.CREATED;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreRemove
    void onRemove() {
        // Валидация: нельзя удалить оплаченный заказ
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot delete paid order");
        }
    }
}
```

```java
// Вынос логики в отдельный listener-класс (чище для сложной логики)
public class OrderAuditListener {

    @PostPersist
    public void afterCreate(Order order) {
        // Логирование, отправка события
        log.info("Order created: {}", order.getId());
    }

    @PostUpdate
    public void afterUpdate(Order order) {
        log.info("Order updated: {} -> status {}", order.getId(), order.getStatus());
    }

    @PostRemove
    public void afterRemove(Order order) {
        log.info("Order removed: {}", order.getId());
    }
}

@Entity
@EntityListeners(OrderAuditListener.class)
public class Order {
    // ...
}
```

| Callback | Когда срабатывает |
|----------|-------------------|
| `@PrePersist` | Перед `INSERT` |
| `@PostPersist` | После `INSERT` |
| `@PreUpdate` | Перед `UPDATE` |
| `@PostUpdate` | После `UPDATE` |
| `@PreRemove` | Перед `DELETE` |
| `@PostRemove` | После `DELETE` |
| `@PostLoad` | После загрузки из БД |

**Подводный камень:** внутри callback-методов нельзя обращаться к `EntityManager` (загружать другие сущности, выполнять запросы) — вы вмешиваетесь в идущий flush, и поведение становится непредсказуемым (вплоть до исключений и зацикливания). Если нужна тяжёлая бизнес-логика, привязанная к изменению сущности, выносите её в `Spring Events` с `@TransactionalEventListener` — он сработает уже после коммита, вне опасной зоны flush.

## Q21. Что такое `FlushMode` и когда его менять?

`FlushMode` определяет момент, когда [Hibernate](../../databases/hibernate-interview.md) сбрасывает накопленные изменения из persistence context в БД (`flush` — это генерация и отправка `INSERT`/`UPDATE`/`DELETE`, но ещё не `COMMIT`).

По умолчанию `AUTO`: flush происходит и при коммите, и **перед каждым запросом** — чтобы запрос увидел ещё не сохранённые изменения и вернул согласованный результат. Менять режим стоит редко и осознанно — в основном при batch-обработке, где постоянные авто-flush'и создают лишний overhead.

| FlushMode | Когда flush | Использование |
|-----------|------------|---------------|
| `AUTO` (default) | Перед запросом и при commit | Стандартное поведение |
| `COMMIT` | Только при commit | Batch-обработка (экономия flush) |
| `MANUAL` | Только явный `flush()` | Редко — тесты, миграции |

```java
@Transactional
public void batchInsertWithFlushControl(List<CreateUserRequest> requests) {
    Session session = entityManager.unwrap(Session.class);
    session.setHibernateFlushMode(FlushMode.COMMIT); // отключаем auto-flush

    for (int i = 0; i < requests.size(); i++) {
        User user = new User(requests.get(i));
        entityManager.persist(user);

        if (i % 50 == 0) { // каждые 50 записей
            entityManager.flush();  // отправляем INSERT в БД
            entityManager.clear();  // очищаем persistence context (экономия RAM)
        }
    }
}
```

Конкретный выигрыш в коде выше: переключив режим на `COMMIT`, мы убираем авто-flush перед каждым промежуточным `SELECT` и сами контролируем, когда отправлять накопленные `INSERT` в БД — пачками по 50, с последующим `clear()` для разгрузки памяти.

## Q22. (!) Что такое `FetchType LAZY` vs `EAGER`?

`FetchType` определяет, **когда** загружаются связанные сущности: сразу вместе с родителем (`EAGER`) или отложенно, при первом обращении к связи (`LAZY`).

- `EAGER` — связь подтягивается тем же запросом (JOIN). Удобно, но опасно: грузится всегда, даже если связь не нужна, и легко раздувает выборку.
- `LAZY` — вместо реальных данных подставляется прокси; запрос за связью уходит только когда вы её действительно трогаете (`user.getOrders()`).

Важно помнить умолчания: `@ManyToOne` и `@OneToOne` по умолчанию `EAGER`, а `@OneToMany` и `@ManyToMany` — `LAZY`.

```java
@Entity
public class User {

    @Id
    private Long id;

    // EAGER (default для @ManyToOne) — загружается СРАЗУ с User
    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;

    // LAZY (default для @OneToMany) — загружается при ПЕРВОМ обращении
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    // Best practice: явно ставить LAZY на @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department departmentLazy;
}
```

```mermaid
graph TD
    subgraph EAGER
        Q1["SELECT u FROM User u"] --> R1["User + Department<br/>JOIN в одном запросе"]
    end

    subgraph LAZY
        Q2["SELECT u FROM User u"] --> R2["Только User"]
        R2 -->|"user.getOrders()"| Q3["SELECT o FROM Order o<br/>WHERE o.user_id = ?"]
        Q3 --> R3["List&lt;Order&gt;"]
    end

    style EAGER fill:#ffcdd2,stroke:#c62828
    style LAZY fill:#c8e6c9,stroke:#2e7d32
```

**Рекомендация:** ставьте `LAZY` на **все** связи (включая `@ManyToOne`, переопределяя умолчание) и загружайте нужное явно — через `@EntityGraph` или `JOIN FETCH` под конкретный сценарий. `EAGER` — мина замедленного действия: он молча грузит лишнее и провоцирует N+1, причём проблема всплывает только под нагрузкой.

**OSIV (`Open Session In View`)** — паттерн, при котором `Hibernate Session` держится открытой до конца HTTP-ответа, чтобы lazy-связи можно было дёргать прямо во view/контроллере. В `Spring Boot` он включён по умолчанию (`spring.jpa.open-in-view=true`), и это палка о двух концах: удобно, но скрытые запросы уходят вне сервисного слоя, где их не видно и не проконтролировать. Рекомендация — отключать OSIV и грузить все нужные данные явно в сервисе, под транзакцией.

## Q23. (!) Что такое N+1 проблема и как её решить?

**N+1** — это 1 запрос за список из N сущностей плюс ещё N запросов за их связанными данными, по одному на каждую сущность. Возникает, когда вы обходите коллекцию и в цикле дёргаете lazy-связь. Одна из самых частых и коварных проблем производительности: на десяти записях незаметна, на десяти тысячах кладёт сервис.

Суть в том, что lazy-загрузка срабатывает на каждом обращении отдельно — Hibernate не догадывается, что вы обойдёте весь список, и шлёт по запросу на элемент. Все решения сводятся к одному: загрузить связи **заранее**, одним или несколькими пакетными запросами.

```java
// ПРОБЛЕМА: N+1
@Transactional(readOnly = true)
public List<UserDto> getAllUsersWithOrders() {
    List<User> users = userRepository.findAll(); // 1 запрос: SELECT * FROM users

    return users.stream()
        .map(user -> new UserDto(
            user.getName(),
            user.getOrders().size() // N запросов: SELECT * FROM orders WHERE user_id = ?
        ))
        .toList();
    // Итого: 1 + N запросов!
}
```

### Решения

**1. `JOIN FETCH` в JPQL:**
```java
@Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.status = :status")
List<User> findByStatusWithOrders(@Param("status") UserStatus status);
// 1 запрос с JOIN — все данные загружены
```

**2. `@EntityGraph`:**
```java
@EntityGraph(attributePaths = {"orders", "orders.items"})
List<User> findByStatus(UserStatus status);
// Spring добавляет LEFT JOIN для указанных путей
```

**3. `@BatchSize` (Hibernate):**
```java
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    @BatchSize(size = 25) // загружает orders для 25 users за один запрос
    private List<Order> orders;
}
// Вместо N запросов — ceil(N/25) запросов с WHERE user_id IN (?, ?, ...)
```

**4. Глобальный `batch_size` в конфиге:**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 25
```

**5. DTO-проекция (вообще без связей):**
```java
@Query("SELECT new com.example.UserOrderCount(u.name, COUNT(o)) " +
       "FROM User u LEFT JOIN u.orders o GROUP BY u.name")
List<UserOrderCount> getUserOrderCounts();
// 1 запрос, без загрузки сущностей
```

**Как обнаружить N+1:** включить логирование SQL (`spring.jpa.show-sql=true` или `logging.level.org.hibernate.SQL=DEBUG`) и смотреть количество запросов. В тестах — использовать [datasource-proxy](https://github.com/ttddyy/datasource-proxy) для подсчёта.

## Q24. Что такое `@EntityGraph` и как он работает?

`@EntityGraph` — декларативный способ сказать «загрузи эти связи вместе с сущностью», не переписывая запрос на `JPQL` с `JOIN FETCH`. Вешается прямо на метод репозитория: указываете пути к связям через `attributePaths`, а Hibernate добавляет `LEFT JOIN` под капотом.

Главное преимущество перед `JOIN FETCH` — переиспользование и читаемость: один и тот же метод (`findById`, `findByStatus`) обслуживает разные сценарии загрузки, а сам запрос остаётся декларацией намерения, а не простынёй JPQL.

```java
@Entity
@NamedEntityGraph(
    name = "User.withOrdersAndItems",
    attributeNodes = {
        @NamedAttributeNode(value = "orders", subgraph = "orders-items")
    },
    subgraphs = {
        @NamedSubgraph(name = "orders-items",
                        attributeNodes = @NamedAttributeNode("items"))
    }
)
public class User {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

public interface UserRepository extends JpaRepository<User, Long> {

    // Ad-hoc EntityGraph через attributePaths
    @EntityGraph(attributePaths = {"orders"})
    List<User> findByStatus(UserStatus status);

    // Именованный EntityGraph
    @EntityGraph(value = "User.withOrdersAndItems", type = EntityGraphType.LOAD)
    Optional<User> findById(Long id);

    // EntityGraph + @Query
    @EntityGraph(attributePaths = {"orders", "department"})
    @Query("SELECT u FROM User u WHERE u.createdAt > :after")
    List<User> findRecentUsers(@Param("after") LocalDateTime after);
}
```

**`LOAD` vs `FETCH`:**
- `EntityGraphType.LOAD` — указанные связи `EAGER`, остальные по аннотации (`@ManyToOne` = `EAGER`)
- `EntityGraphType.FETCH` — указанные связи `EAGER`, **все остальные `LAZY`**

**Подводный камень:** `LEFT JOIN` по коллекции размножает строки — у родителя с 3 дочерними записями результат вернётся в трёх копиях. Лечится `DISTINCT` в запросе или объявлением связи как `Set` вместо `List` (множество само схлопывает дубликаты).

## Q25. Что такое `CascadeType` и когда применять каскады?

`CascadeType` определяет, какие операции с родительской сущностью автоматически «протекают» на дочерние. Сохранили `Order` через `save()` — с каскадом `PERSIST` сохранятся и его `OrderItem`, без отдельного вызова на каждый.

Зачем: для тесно связанных сущностей (родитель не существует без детей) это убирает ручное сохранение/удаление каждого ребёнка. Отдельно стоит `orphanRemoval = true` — он удаляет ребёнка из БД, как только тот выпал из коллекции родителя (это не то же самое, что `CascadeType.REMOVE`, который реагирует только на удаление самого родителя).

```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PERSIST + MERGE: при сохранении/обновлении Order — сохраняются и OrderItem
    // orphanRemoval: удаление item из коллекции → DELETE из БД
    @OneToMany(mappedBy = "order",
               cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this); // синхронизация обеих сторон связи
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null); // orphanRemoval удалит из БД
    }
}

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String productName;
    private int quantity;
    private BigDecimal price;
}
```

```java
@Transactional
public Order createOrderWithItems(CreateOrderRequest request) {
    Order order = new Order();

    request.items().forEach(itemReq -> {
        OrderItem item = new OrderItem();
        item.setProductName(itemReq.productName());
        item.setQuantity(itemReq.quantity());
        item.setPrice(itemReq.price());
        order.addItem(item); // cascade PERSIST сохранит item вместе с order
    });

    return orderRepository.save(order);
    // Один вызов save() — INSERT order + INSERT для каждого item
}
```

| CascadeType | Что каскадируется | Когда использовать |
|-------------|--------------------|--------------------|
| `PERSIST` | `save()` | Parent-child (`Order` → `OrderItem`) |
| `MERGE` | `merge()` / обновление | Вместе с `PERSIST` |
| `REMOVE` | `delete()` | Осторожно! Удаление parent = удаление children |
| `ALL` | Всё | Только для строго зависимых сущностей |

**Антипаттерн:** `CascadeType.ALL` (а значит и `REMOVE`) на `@ManyToMany` — например `User` → `Role`. Роли разделяются между пользователями, и удаление одного пользователя снесёт роли, которыми пользуются остальные. Каскадное удаление допустимо только для строго владеемых, незаразделяемых детей (`Order` → `OrderItem`).

## Q26. Что такое `@Embedded` и `@Embeddable`?

`@Embeddable` помечает Value Object — объект **без собственного `@Id`**, который не живёт отдельно, а встраивается в сущность-владельца. `@Embedded` ставится на поле сущности, куда такой объект вкладывается. Отдельной таблицы у него нет: его поля становятся колонками таблицы владельца.

Зачем: группировать логически связанные поля (`amount` + `currency` → `Money`; `street` + `city` + `zip` → `Address`) в осмысленный тип с поведением, не плодя для них отдельную таблицу и связь. Это про чистоту модели, а не про маппинг ради маппинга.

```java
@Embeddable
public record Money(
    @Column(name = "amount", nullable = false)
    BigDecimal amount,

    @Column(name = "currency", length = 3, nullable = false)
    String currency
) {
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

@Embeddable
public record Address(
    String street,
    String city,
    String zipCode,
    String country
) {}

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Money total; // → колонки amount, currency

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "shipping_zip")),
        @AttributeOverride(name = "country", column = @Column(name = "shipping_country"))
    })
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip")),
        @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private Address billingAddress;
}
```

**Зачем `@AttributeOverrides`:** два `@Embedded`-поля одного типа (`shippingAddress` и `billingAddress`) по умолчанию мапятся на одни и те же колонки (`street`, `city`, ...) и конфликтуют. Override переименовывает колонки для каждого вхождения (`shipping_street`, `billing_street`).

`record` с `@Embeddable` (Java 16+) — естественный выбор: Value Object по определению неизменяем, а `record` даёт неизменяемость, `equals`/`hashCode` по значению и компактный синтаксис из коробки.

## Q27. Как работает наследование сущностей (`JPA Inheritance`)?

`JPA` умеет маппить иерархию классов на таблицы тремя способами, и выбор — это компромисс между скоростью запросов и нормализацией схемы:

- **`SINGLE_TABLE`** — вся иерархия в одной таблице, тип строки различает колонка-дискриминатор. Быстро (без JOIN), но колонки подтипов обязаны быть nullable.
- **`JOINED`** — базовая таблица + по таблице на каждый подтип, связанные по `id`. Чистая нормализация и `NOT NULL`-ограничения, но каждый запрос делает JOIN.
- **`TABLE_PER_CLASS`** — отдельная самодостаточная таблица на каждый конкретный класс. Полиморфные запросы превращаются в `UNION ALL` и работают медленно.

```mermaid
graph TD
    subgraph "SINGLE_TABLE"
        ST["payments<br/>id | dtype | amount | card_number | bank_account"]
    end

    subgraph "JOINED"
        J1["payments<br/>id | amount"] --> J2["card_payments<br/>id | card_number"]
        J1 --> J3["bank_payments<br/>id | bank_account"]
    end

    subgraph "TABLE_PER_CLASS"
        TPC1["card_payments<br/>id | amount | card_number"]
        TPC2["bank_payments<br/>id | amount | bank_account"]
    end
```

```java
// SINGLE_TABLE — одна таблица, дискриминатор DTYPE
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type")
public abstract class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}

@Entity
@DiscriminatorValue("CARD")
public class CardPayment extends Payment {
    private String cardNumber;  // nullable в общей таблице
    private String cardHolder;
}

@Entity
@DiscriminatorValue("BANK")
public class BankPayment extends Payment {
    private String bankAccount; // nullable в общей таблице
    private String bankName;
}

// Полиморфный репозиторий
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Возвращает и CardPayment, и BankPayment
    List<Payment> findByAmountGreaterThan(BigDecimal amount);
}
```

| Стратегия | Плюсы | Минусы |
|-----------|-------|--------|
| `SINGLE_TABLE` | Быстрые полиморфные запросы, без JOIN | Nullable колонки подтипов, нет NOT NULL constraints |
| `JOINED` | Нормализация, NOT NULL на колонках подтипов | JOIN при каждом запросе |
| `TABLE_PER_CLASS` | Изоляция таблиц | Полиморфные запросы через UNION ALL (медленно) |

**Рекомендация:** `SINGLE_TABLE` — если подтипов мало и у них мало уникальных полей. `JOINED` — если нужна строгая нормализация. `TABLE_PER_CLASS` — почти никогда.

## Q28. (!) Как работает batch-обработка?

Batch-обработка группирует несколько `INSERT`/`UPDATE` в один сетевой round-trip к БД. Без неё Hibernate шлёт по отдельному запросу на каждую сущность — на массовых операциях сетевая задержка умножается на N и становится главным тормозом.

Включается двумя слагаемыми: настройка Hibernate (`jdbc.batch_size`, `order_inserts`, `order_updates`) плюс правильный код — периодический `flush()` + `clear()`, чтобы отправлять данные пачками и не копить весь объём в памяти. Одного без другого недостаточно.

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50          # количество операций в одном батче
          batch_versioned_data: true
        order_inserts: true        # группировать INSERT по типу сущности
        order_updates: true        # группировать UPDATE по типу сущности
```

```java
@Service
@RequiredArgsConstructor
public class ProductImportService {

    private final EntityManager entityManager;

    @Transactional
    public int importProducts(List<ProductCsvRow> rows) {
        int batchSize = 50;
        int count = 0;

        for (int i = 0; i < rows.size(); i++) {
            Product product = mapToEntity(rows.get(i));
            entityManager.persist(product);
            count++;

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();  // отправить batch INSERT в БД
                entityManager.clear();  // очистить persistence context (экономия RAM)
            }
        }

        entityManager.flush();
        entityManager.clear();
        return count;
    }
}
```

**Критический момент с `GenerationType.IDENTITY`:**
```java
// НЕ батчится! IDENTITY требует INSERT для получения id
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Батчится — SEQUENCE позволяет выделить id заранее
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
@SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 50)
private Long id;
```

Почему `IDENTITY` ломает батчинг: при этой стратегии БД присваивает id только в момент самого `INSERT`, а Hibernate обязан знать id сразу после `persist()`. Значит, отложить и сгруппировать вставки нельзя — каждая идёт отдельным запросом. `SEQUENCE` с `allocationSize = 50` решает это: Hibernate одним обращением к sequence резервирует сразу 50 id вперёд, раздаёт их в памяти и спокойно батчит 50 `INSERT` в один round-trip. Это частая ловушка — настроили `batch_size`, а батчинга нет именно из-за `IDENTITY`.

## Q29. Как настроить второуровневый кэш (`L2 cache`)?

Второуровневый кэш (`L2`) хранит сущности на уровне `SessionFactory` — то есть **между транзакциями и общий для всех сессий**. Это и отличает его от L1 (persistence context), который живёт только внутри одной транзакции и после её завершения исчезает.

Идея: справочные данные (категории, страны, тарифы) читаются постоянно и почти не меняются — гонять за ними в БД при каждом запросе расточительно. L2 держит их в памяти приложения, и повторные чтения по id идут без обращения к БД.

```mermaid
graph LR
    APP["Приложение"] --> L1["L1 Cache<br/>Persistence Context<br/>(per transaction)"]
    L1 --> L2["L2 Cache<br/>EhCache / Hazelcast<br/>(per SessionFactory)"]
    L2 --> DB["Database"]

    style L1 fill:#c8e6c9,stroke:#2e7d32
    style L2 fill:#fff9c4,stroke:#f9a825
```

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          region.factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
      javax:
        cache:
          provider: org.ehcache.jsr107.EhcacheCachingProvider
```

```java
@Entity
@Table(name = "categories")
@Cacheable                    // JPA: включить кэширование
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) // Hibernate: стратегия
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE) // кэш коллекции
    private List<Product> products;
}
```

| Стратегия | Описание | Когда |
|-----------|----------|-------|
| `READ_ONLY` | Неизменяемые данные | Справочники, enum-таблицы |
| `READ_WRITE` | Читаются чаще, чем пишутся | Большинство сценариев |
| `NONSTRICT_READ_WRITE` | Eventual consistency | Данные, где допустимо кратковременное расхождение |
| `TRANSACTIONAL` | Полная транзакционная согласованность | JTA-транзакции |

## Q30. Что такое `Soft Delete` и как реализовать?

**Soft Delete** (мягкое удаление) — запись не удаляется из таблицы физически, а помечается флагом (`deleted_at`, `is_deleted`) и просто перестаёт попадать в обычные выборки. Данные остаются в БД для аудита, восстановления и аналитики.

Реализация в Hibernate 6.4+: аннотация `@SQLRestriction("deleted_at IS NULL")` на сущности автоматически дописывает `WHERE deleted_at IS NULL` ко всем запросам — `findAll()` и derived-методы «не видят» удалённые записи без единой правки в коде репозитория. Само «удаление» — это `UPDATE`, проставляющий `deleted_at`.

```java
@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL") // Hibernate 6.4+ (заменяет @Where)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}

public interface UserRepository extends JpaRepository<User, Long> {

    // @SQLRestriction автоматически добавляет WHERE deleted_at IS NULL
    // findAll() → SELECT * FROM users WHERE deleted_at IS NULL

    // Если нужно найти удалённых — нативный запрос без фильтра
    @Query(value = "SELECT * FROM users WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<User> findDeleted();

    // Soft delete через @Modifying
    @Modifying
    @Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDelete(@Param("id") Long id);
}
```

**Подводный камень с уникальными индексами:** запись остаётся в таблице, поэтому обычный `UNIQUE(email)` не даст завести нового пользователя с тем же email, что у «удалённого». Решение — частичный (условный) уникальный индекс, который действует только на живые записи:

```sql
-- PostgreSQL: уникальность только для неудалённых записей
CREATE UNIQUE INDEX idx_users_email_active ON users(email) WHERE deleted_at IS NULL;
```

## Q31. Что такое `Hibernate Filters`?

`Hibernate Filters` — фильтр, который добавляет `WHERE`-условие ко всем запросам к сущности, но, в отличие от `@SQLRestriction`, его можно **включать и выключать в рантайме** и параметризовать. Объявляется через `@FilterDef` + `@Filter`, активируется на сессии вызовом `session.enableFilter(...)`.

Главный сценарий — multi-tenancy: один фильтр `tenant_id = :tenantId` незаметно ограничивает все запросы данными текущего арендатора. Параметр (`tenantId`) задаётся при включении фильтра — обычно в AOP-аспекте на входе в запрос. Именно динамичность и параметризация отличают фильтры от статического `@SQLRestriction`.

```java
@Entity
@Table(name = "documents")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Document {
    @Id
    private Long id;

    @Column(name = "tenant_id")
    private String tenantId;

    private String title;
    private String content;
}

@Component
@RequiredArgsConstructor
public class TenantFilterAspect {

    private final EntityManager entityManager;

    @Around("@annotation(TenantAware)")
    public Object applyTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Session session = entityManager.unwrap(Session.class);
        String tenantId = TenantContext.getCurrentTenant();

        session.enableFilter("tenantFilter")
               .setParameter("tenantId", tenantId);
        try {
            return joinPoint.proceed();
        } finally {
            session.disableFilter("tenantFilter");
        }
    }
}
```

**Важная оговорка:** фильтр действует только на запросы, идущие через `Session` (HQL, criteria, загрузка по id). Прямую загрузку по `find()` он, в зависимости от версии и настроек, может не покрывать — для строгой изоляции арендаторов это нужно учитывать.

## Q32. Как создать custom repository implementation?

Когда не хватает ни derived-методов, ни `@Query` (нужна сложная динамическая логика или прямой доступ к `EntityManager`/Criteria API), реализацию можно дописать руками — `Spring Data` встроит её в тот же репозиторий по конвенции имён.

Схема из трёх шагов: интерфейс с кастомными методами → класс-реализация с суффиксом `Impl` → основной репозиторий, наследующий и `JpaRepository`, и кастомный интерфейс. По имени `<ИнтерфейсCustom>Impl` Spring находит реализацию и «склеивает» её с автогенерированными методами — для клиента это один цельный репозиторий.

```java
// 1. Интерфейс с кастомными методами
public interface UserRepositoryCustom {
    List<User> findUsersWithComplexCriteria(UserSearchCriteria criteria);
    void bulkUpdateStatus(List<Long> ids, UserStatus status);
}

// 2. Реализация с суффиксом Impl
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<User> findUsersWithComplexCriteria(UserSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.name() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")),
                "%" + criteria.name().toLowerCase() + "%"));
        }
        if (criteria.minAge() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("age"), criteria.minAge()));
        }
        if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
            predicates.add(root.get("status").in(criteria.statuses()));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("createdAt")));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional
    public void bulkUpdateStatus(List<Long> ids, UserStatus status) {
        entityManager.createQuery(
            "UPDATE User u SET u.status = :status WHERE u.id IN :ids")
            .setParameter("status", status)
            .setParameter("ids", ids)
            .executeUpdate();
    }
}

// 3. Основной репозиторий расширяет оба интерфейса
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    // Query Methods + @Query + Custom — всё в одном репозитории
    Optional<User> findByEmail(String email);
}
```

Суффикс `Impl` — конвенция по умолчанию. Можно изменить через `@EnableJpaRepositories(repositoryImplementationPostfix = "CustomImpl")`.

## Q33. Что такое derived delete и `deleteBy`?

`Derived delete` — методы вида `deleteBy...`/`removeBy...`, которые `Spring Data` реализует по имени, как и обычные `findBy`. Удобно для точечного удаления по условию.

Но за удобством прячется важный нюанс производительности: derived delete сначала делает `SELECT` всех подходящих сущностей, а потом удаляет их по одной. Это сделано намеренно — чтобы сработали каскады и lifecycle-callback'и (`@PreRemove`). Расплата — N+1 на удалении: для массовых чисток это дорого.

```java
public interface SessionRepository extends JpaRepository<Session, Long> {

    // Derived delete — возвращает количество удалённых
    @Transactional
    long deleteByExpiredAtBefore(LocalDateTime before);

    // Derived delete — возвращает удалённые сущности
    @Transactional
    List<Session> removeByUserId(Long userId);

    // ВНИМАНИЕ: derived delete делает SELECT + DELETE для каждой сущности!
    // Для bulk delete лучше @Modifying:
    @Modifying
    @Transactional
    @Query("DELETE FROM Session s WHERE s.expiredAt < :before")
    int bulkDeleteExpired(@Param("before") LocalDateTime before);
}
```

**Когда что:** нужны каскады и callback'и при удалении (например, `orphanRemoval` для детей) — derived delete. Нужно просто стереть тысячи строк по условию и каскады не важны — `@Modifying @Query("DELETE ...")`: один `DELETE`-запрос вместо `SELECT` + N удалений.

## Q34. (!) Как тестировать `Spring Data JPA` репозитории?

Репозитории тестируют через `@DataJpaTest` — это «срезовый» (slice) тест: Spring поднимает только JPA-слой (`EntityManager`, репозитории, маппинг), без веба, сервисов и остального контекста. Тесты получаются быстрыми и сфокусированными именно на доступе к данным.

Два важных решения по умолчанию:
- `@DataJpaTest` каждый тест выполняет в транзакции и откатывает её в конце — БД остаётся чистой между тестами без ручного удаления.
- По умолчанию он подменяет datasource на embedded-БД (H2). На реальном проекте это вредно: H2 не повторяет поведение PostgreSQL (типы, нативный SQL, частичные индексы). Поэтому подмену отключают (`@AutoConfigureTestDatabase(replace = NONE)`) и поднимают настоящую БД через `Testcontainers`.

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        // given
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setStatus(UserStatus.ACTIVE);
        entityManager.persistAndFlush(user);

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void findByStatus_shouldReturnPagedResult() {
        // given
        IntStream.range(0, 25).forEach(i -> {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setStatus(UserStatus.ACTIVE);
            entityManager.persist(user);
        });
        entityManager.flush();

        // when
        Page<User> page = userRepository.findByStatus(
            UserStatus.ACTIVE,
            PageRequest.of(0, 10, Sort.by("email"))
        );

        // then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(25);
        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    @Test
    void deactivateInactiveUsers_shouldUpdateStatus() {
        // given
        User oldUser = new User();
        oldUser.setEmail("old@example.com");
        oldUser.setStatus(UserStatus.ACTIVE);
        oldUser.setLastLoginAt(LocalDateTime.now().minusMonths(6));
        entityManager.persistAndFlush(oldUser);

        // when
        int updated = userRepository.deactivateInactiveUsers(
            UserStatus.INACTIVE,
            LocalDateTime.now().minusMonths(3)
        );

        // then
        assertThat(updated).isEqualTo(1);
        entityManager.clear(); // сброс кэша L1
        User reloaded = userRepository.findById(oldUser.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }
}
```

**Ключевые практики:**
- `Testcontainers` вместо H2 — тестируем на той же СУБД, что в проде; H2 не поддерживает многие фичи PostgreSQL и даёт ложно-зелёные тесты
- `TestEntityManager` для подготовки данных — пишем фикстуры в обход тестируемого репозитория, иначе тест проверяет сам себя
- `entityManager.clear()` после `@Modifying` — сбрасываем L1-кэш, иначе проверка прочитает устаревшую сущность из persistence context, а не реальные данные из БД

## Q35. Какие best practices для производительности `JPA`?

Большинство проблем производительности `JPA` сводятся к одному принципу: **грузить ровно то, что нужно, и не больше — пакетами, а не по одной записи.** Ниже — практики, реализующие этот принцип на разных уровнях.

| Практика | Проблема | Решение |
|----------|----------|---------|
| LAZY по умолчанию | EAGER загружает лишнее | `@ManyToOne(fetch = LAZY)` на всех связях |
| Явная загрузка связей | N+1 при обходе коллекций | `@EntityGraph`, `JOIN FETCH` |
| Проекции | Загрузка всех полей сущности | `interface`/`DTO`-проекции |
| Batch size | Медленная массовая вставка | `batch_size=50`, `SEQUENCE` генератор |
| Индексы | Медленные запросы | `@Index` на `@Table`, `EXPLAIN ANALYZE` |
| readOnly | Лишний dirty checking | `@Transactional(readOnly = true)` |
| OSIV отключение | Lazy в view, неконтролируемые запросы | `spring.jpa.open-in-view=false` |
| L2 кэш | Частые одинаковые SELECT | `@Cacheable` для справочников |
| Мониторинг | Невидимые проблемы | `Hibernate Statistics`, `datasource-proxy` |

```java
// Пример: оптимальный сервис с учётом best practices
@Service
@Transactional(readOnly = true) // read-only по умолчанию — нет dirty checking
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Проекция — только нужные поля
    public Page<ProductSummary> search(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = buildSpec(filter);
        return productRepository.findAll(spec, pageable)
            .map(ProductSummary::from);
    }

    // EntityGraph для конкретного use case
    public ProductDetails getDetails(Long id) {
        return productRepository.findByIdWithCategoryAndReviews(id)
            .map(ProductDetails::from)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional // переопределяем — этот метод пишет
    public void updatePrices(Map<Long, BigDecimal> priceUpdates) {
        // Bulk update — один запрос, не N
        productRepository.bulkUpdatePrices(priceUpdates);
    }
}
```

Подробнее о настройке [Spring Boot](spring-boot-interview.md) приложений и профилировании запросов через [SQL](../../databases/sql-interview.md).

## Q36. (!) Как работают derived query methods и каков их синтаксис?

**Derived query methods** (запросы по имени метода) — механизм, где **имя метода и есть спецификация запроса**: Spring Data парсит его и генерирует JPQL автоматически. Никакой магии — есть жёсткие правила построения имени, и зная их, вы предсказуемо получаете нужный запрос.

**Структура имени метода:**

```
find|read|get|query|search|stream  By  [Conditions]  [OrderBy...]
count | exists | delete | remove   By  [Conditions]
```

**Ключевые слова условий:**

| Ключевое слово | SQL | Пример |
|----------------|-----|--------|
| `And` | `AND` | `findByNameAndEmail` |
| `Or` | `OR` | `findByNameOrEmail` |
| `Is`, `Equals` | `=` | `findByName`, `findByNameIs` |
| `Not` | `!=` | `findByNameNot` |
| `Like` | `LIKE` | `findByNameLike` |
| `StartingWith` | `LIKE 'x%'` | `findByNameStartingWith` |
| `EndingWith` | `LIKE '%x'` | `findByNameEndingWith` |
| `Containing` | `LIKE '%x%'` | `findByNameContaining` |
| `In` | `IN (...)` | `findByStatusIn` |
| `Between` | `BETWEEN` | `findByAgeBetween` |
| `LessThan` / `GreaterThan` | `<` / `>` | `findByPriceLessThan` |
| `IsNull` / `IsNotNull` | `IS NULL` | `findByDeletedAtIsNull` |
| `True` / `False` | `= true` | `findByActiveTrue` |
| `OrderBy` | `ORDER BY` | `findByStatusOrderByCreatedAtDesc` |

**Примеры:**

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT u FROM User u WHERE u.email = ?1
    Optional<User> findByEmail(String email);

    // SELECT u FROM User u WHERE u.firstName = ?1 AND u.lastName = ?2
    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    // SELECT u FROM User u WHERE u.age BETWEEN ?1 AND ?2 ORDER BY u.lastName ASC
    List<User> findByAgeBetweenOrderByLastNameAsc(int minAge, int maxAge);

    // SELECT COUNT(u) FROM User u WHERE u.status = ?1
    long countByStatus(UserStatus status);

    // DELETE FROM User u WHERE u.deletedAt IS NOT NULL
    void deleteByDeletedAtIsNotNull();

    // Проверка существования
    boolean existsByEmail(String email);

    // Ограничение результата (TOP / FIRST)
    List<User> findTop5ByStatusOrderByCreatedAtDesc(UserStatus status);
    Optional<User> findFirstByEmailOrderByCreatedAtDesc(String email);
}
```

**Граница применимости:** derived-методы хороши, пока имя читается с первого взгляда. Несколько JOIN'ов, подзапросы, агрегации в имя не уложить (или уложить так, что его невозможно прочесть) — здесь переходят на `@Query` или `Specification`.

## Q37. (!) Как использовать `Specification` API для построения динамических запросов?

`Specification` — функциональный интерфейс Spring Data, тонкая обёртка над JPA `Criteria API`. Один объект `Specification` описывает один предикат (`WHERE`-условие), а лямбда внутри получает `root` (корень запроса), `query` и `cb` (`CriteriaBuilder`) для построения этого условия типобезопасно.

Сила — в композиции: спецификации комбинируются через `.and()`/`.or()`, поэтому динамический фильтр собирается из независимых, переиспользуемых кусочков. Включается это наследованием репозитория от `JpaSpecificationExecutor`, который добавляет метод `findAll(Specification, Pageable)`.

**Включение:**

```java
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> { }
```

**Создание Specification:**

```java
public class ProductSpecs {

    // Статические фабричные методы
    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) ->
            category == null ? cb.conjunction()  // всегда true — игнорируем условие
                             : cb.equal(root.get("category"), category);
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    public static Specification<Product> nameContains(String keyword) {
        return (root, query, cb) ->
            keyword == null ? cb.conjunction()
                            : cb.like(cb.lower(root.get("name")),
                                      "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    // JOIN через Specification
    public static Specification<Product> hasBrandName(String brandName) {
        return (root, query, cb) -> {
            Join<Product, Brand> brandJoin = root.join("brand", JoinType.INNER);
            return cb.equal(brandJoin.get("name"), brandName);
        };
    }
}
```

**Комбинирование и использование:**

```java
@Service
public class ProductSearchService {

    private final ProductRepository repository;

    public Page<Product> search(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = Specification.where(isActive())
            .and(hasCategory(filter.getCategory()))
            .and(priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
            .and(nameContains(filter.getKeyword()));

        return repository.findAll(spec, pageable);
    }
}
```

**Избегание дублирования JOIN (для агрегированных запросов):**

```java
public static Specification<Product> withDistinctRoot() {
    return (root, query, cb) -> {
        query.distinct(true);  // предотвращаем дубликаты при JOIN коллекций
        return cb.conjunction();
    };
}
```

## Q38. (!) Как работают `Projection`-интерфейсы в Spring Data JPA?

`Projection` — способ вернуть из репозитория только нужные поля, а не весь объект-сущность. Меньше данных тянется из БД, меньше трафика и памяти — особенно ощутимо на широких таблицах.

**Три вида проекций** (от удобных к производительным):

### 1. Interface-based Projection (рекомендуется)

```java
// Интерфейс — Spring Data создаёт прокси
public interface UserSummary {
    Long getId();
    String getFirstName();
    String getLastName();

    // Вычисляемые поля через SpEL
    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
}

public interface UserRepository extends JpaRepository<User, Long> {
    List<UserSummary> findByStatus(UserStatus status);
    // Spring Data генерирует: SELECT u.id, u.firstName, u.lastName FROM User u WHERE u.status = ?
}
```

### 2. Class-based Projection (DTO-проекция)

```java
// Immutable DTO — без прокси, лучшая производительность
public record UserDto(Long id, String firstName, String email) { }

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT new com.example.UserDto(u.id, u.firstName, u.email) " +
           "FROM User u WHERE u.status = :status")
    List<UserDto> findDtoByStatus(@Param("status") UserStatus status);
}
```

### 3. Dynamic Projection (выбор проекции в runtime)

```java
public interface UserRepository extends JpaRepository<User, Long> {
    <T> List<T> findByStatus(UserStatus status, Class<T> type);
}

// Использование:
List<UserSummary> summaries = repo.findByStatus(ACTIVE, UserSummary.class);
List<UserDto> dtos = repo.findByStatus(ACTIVE, UserDto.class);
List<User> full = repo.findByStatus(ACTIVE, User.class);
```

**Вложенные проекции:**

```java
public interface OrderSummary {
    Long getId();
    BigDecimal getTotalAmount();
    CustomerInfo getCustomer();   // вложенная проекция

    interface CustomerInfo {
        String getEmail();
        String getFullName();
    }
}
```

**Рекомендация:** для производительности — interface-based (закрытая) или DTO-проекция: `SELECT` идёт ровно по нужным колонкам. Помните про ловушку открытой проекции: как только в интерфейсе появляется геттер с `@Value`/SpEL, Spring уже не может заранее определить набор колонок и грузит сущность целиком, вычисляя выражение в памяти. Удобно, но экономии на уровне БД нет.

## Q39. (!) Как работает аудит через `@CreatedDate`, `@LastModifiedDate` и `@EnableJpaAuditing`?

Spring Data JPA Auditing автоматически проставляет «когда» и «кто» при сохранении и изменении сущности — даты в `@CreatedDate`/`@LastModifiedDate` и пользователя в `@CreatedBy`/`@LastModifiedBy`. Вам не нужно вручную писать `setUpdatedAt(now())` в каждом сервисе.

Связка работает так: `@EnableJpaAuditing` поднимает инфраструктуру, `AuditingEntityListener` (через `@EntityListeners`) ловит persist/update-события и проставляет поля, а источник имени пользователя — бин `AuditorAware`, который чаще всего тянет логин из `SecurityContext`.

**Включение аудита:**

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // Получаем текущего пользователя из Spring Security
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

**Базовый аудируемый класс:**

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String updatedBy;
}
```

**Использование в сущностях:**

```java
@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    // createdAt, updatedAt, createdBy, updatedBy — наследуются
}
```

**Альтернатива — интерфейс `Auditable`:**

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Order implements Auditable<String, Long, Instant> {
    // Реализует все методы интерфейса Auditable
}
```

**Версионирование через `@Version`** (оптимистичная блокировка):

```java
@Version
@Column(nullable = false)
private Long version;  // автоматически инкрементируется при каждом UPDATE
```

## Q40. (!) Как интегрировать `Flyway` со Spring Data JPA?

`Flyway` версионирует схему БД через нумерованные SQL-миграции, а Spring Data JPA работает уже поверх готовой схемы. Разделение ответственности чёткое: за структуру таблиц отвечает Flyway, за маппинг и доступ — JPA.

Ключевой момент порядка: Flyway должен накатить миграции **до** того, как Hibernate инициализирует `EntityManagerFactory`. Поэтому `ddl-auto` ставят в `validate` (не `create`/`update`!) — Hibernate не трогает схему, а лишь сверяет, что таблицы соответствуют сущностям. Spring Boot гарантирует этот порядок автоконфигурацией.

**Зависимость:**

```groovy
implementation 'org.flywaydb:flyway-core'
// Для PostgreSQL дополнительно:
implementation 'org.flywaydb:flyway-database-postgresql'
```

**Конфигурация:**

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate   # НЕ create/update — схемой управляет Flyway!
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration   # папка с миграциями
    baseline-on-migrate: true           # для БД с существующей схемой
    validate-on-migrate: true           # проверять checksums
```

**Структура миграций:**

```
src/main/resources/db/migration/
├── V1__create_users.sql
├── V2__create_products.sql
├── V3__add_audit_columns.sql
└── R__refresh_user_view.sql   # repeatable migration (без версии)
```

**Пример миграции:**

```sql
-- V3__add_audit_columns.sql
ALTER TABLE products
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    ADD COLUMN updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Индекс для аудит-запросов
CREATE INDEX idx_products_created_by ON products(created_by);
```

**Порядок выполнения** (Spring Boot гарантирует):
1. `DataSource` инициализируется
2. `FlywayAutoConfiguration` запускает миграции
3. `HibernateJpaAutoConfiguration` инициализирует `EntityManagerFactory` (с `validate`)

**Тестирование с `@DataJpaTest` и Flyway:**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.flyway.locations=classpath:db/migration,classpath:db/testdata"
})
class ProductRepositoryTest {
    // Flyway выполнит и prod-миграции, и тестовые данные
}
```

## Q41. (!) Как работает `@EntityGraph` и когда он предпочтительнее `JOIN FETCH`?

`@EntityGraph` — декларативный способ задать, какие связи подтянуть в конкретном запросе, **не меняя `FetchType` в маппинге сущности**. Сущность остаётся с `LAZY`-связями (как и должно быть), а нужный набор связей догружается точечно, под конкретный метод.

Это его главное достоинство против `JOIN FETCH`: один и тот же запрос можно переиспользовать с разными графами загрузки, а с пагинацией и несколькими коллекциями `@EntityGraph` ведёт себя корректно там, где `JOIN FETCH` ломается (см. таблицу и примеры ниже).

**Определение EntityGraph на сущности:**

```java
@Entity
@NamedEntityGraph(
    name = "Order.withItemsAndCustomer",
    attributeNodes = {
        @NamedAttributeNode("items"),
        @NamedAttributeNode(value = "customer", subgraph = "customer-address")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "customer-address",
            attributeNodes = @NamedAttributeNode("address")
        )
    }
)
public class Order {
    @Id Long id;

    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
}
```

**Использование в репозитории:**

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Именованный EntityGraph
    @EntityGraph(value = "Order.withItemsAndCustomer")
    Optional<Order> findById(Long id);

    // Ad-hoc EntityGraph (без @NamedEntityGraph)
    @EntityGraph(attributePaths = {"items", "customer.address"})
    List<Order> findByStatus(OrderStatus status);
}
```

**EntityGraph vs JOIN FETCH:**

| Аспект | `@EntityGraph` | `JOIN FETCH` |
|--------|---------------|--------------|
| Повторное использование | Да, ссылка на имя | Нет |
| Работа с пагинацией | Да (Hibernate загружает в память) | Нет (warning: HHH90003004) |
| Несколько коллекций | Да (отдельные SELECT) | Нет (MultipleBagFetchException) |
| Контроль JOIN-типа | Только LEFT OUTER | LEFT / INNER явно |
| Читаемость | Высокая | Встроена в JPQL |

**Проблема JOIN FETCH с `Page`:**

```java
// НЕПРАВИЛЬНО — Hibernate загружает все записи в память для подсчёта
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

// ПРАВИЛЬНО — EntityGraph + отдельный count запрос
@EntityGraph(attributePaths = "items")
@Query(value = "SELECT o FROM Order o WHERE o.status = :status",
       countQuery = "SELECT COUNT(o) FROM Order o WHERE o.status = :status")
Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);
```

## Q42. Как корректно использовать `@Modifying` с `@Query` для bulk-операций?

`@Modifying` сообщает Spring Data, что `@Query` — это DML (`UPDATE`, `DELETE`, `INSERT`), а не чтение. Без неё Spring вызовет `getResultList()` для модифицирующего запроса и упадёт с исключением.

Смысл bulk-операции через `@Modifying` — выполнить один `UPDATE`/`DELETE` прямо в БД, минуя загрузку сущностей в память. Но именно «минуя память» и порождает главную тонкость: кэш первого уровня про эти изменения не знает, и его нужно синхронизировать вручную (флаги ниже).

**Базовый синтаксис:**

```java
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional   // обязательно — DML требует транзакции
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :ids")
    int updateStatusByIds(
        @Param("status") UserStatus status,
        @Param("ids") List<Long> ids
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.status = :status AND u.updatedAt < :before")
    int deleteInactiveUsers(
        @Param("status") UserStatus status,
        @Param("before") Instant before
    );
}
```

**Критически важно: синхронизация с Persistence Context**

Hibernate кэширует сущности в `PersistenceContext` (1-й уровень кэша). После выполнения `@Modifying`-запроса (который идёт напрямую в БД, минуя кэш) кэш становится устаревшим.

```java
@Modifying(
    clearAutomatically = true,    // очищает PersistenceContext после выполнения
    flushAutomatically = true     // сбрасывает pending changes перед выполнением
)
@Transactional
@Query("UPDATE Product p SET p.price = p.price * :factor WHERE p.category = :cat")
int adjustPrices(@Param("factor") BigDecimal factor, @Param("cat") String category);
```

**Когда какой флаг использовать:**

| Флаг | Когда нужен |
|------|-------------|
| `clearAutomatically = true` | Если после `@Modifying` в той же транзакции читаете изменённые сущности |
| `flushAutomatically = true` | Если перед `@Modifying` были изменения сущностей в PersistenceContext, которые должны попасть в БД сначала |

**Пример с нативным SQL:**

```java
@Modifying
@Transactional
@Query(
    value = """
        INSERT INTO product_audit (product_id, old_price, new_price, changed_at)
        SELECT id, price, :newPrice, NOW()
        FROM products
        WHERE category = :category
        """,
    nativeQuery = true
)
int auditAndUpdatePrices(
    @Param("category") String category,
    @Param("newPrice") BigDecimal newPrice
);
```

**Возвращаемые типы:** `int`/`Integer` — число затронутых строк (полезно для проверки «обновилось ли что-то»), `void` — когда результат не нужен. Для асинхронного выполнения — `@Async` + `Future<Integer>`.

---

## See also

- [Spring Framework](spring-framework-interview.md) — IoC и жизненный цикл бинов репозиториев
- [Spring Boot](spring-boot-interview.md) — автоконфигурация datasource и JPA
- [Spring MVC](spring-mvc-interview.md) — контроллеры, использующие JPA-репозитории
- [Spring WebFlux](spring-webflux-interview.md) — R2DBC как реактивная альтернатива JPA
- [Spring Security](spring-security-interview.md) — UserDetailsService на основе JPA
- [Spring Cloud](spring-cloud-interview.md) — репозитории в cloud-native микросервисах
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — health indicators для datasource
- [Spring Batch](spring-batch-interview.md) — JpaPagingItemReader и batch-доступ к данным
- [Hibernate](../../databases/hibernate-interview.md) — ORM под капотом Spring Data JPA
- [Архитектура баз данных](../../databases/database-architecture-interview.md) — транзакции и производительность

- [Spring AOP](spring-aop-interview.md)
- [Spring Batch](spring-batch-interview.md)
- [Spring Boot Actuator](spring-boot-actuator-interview.md)
- [Spring Boot](spring-boot-interview.md)
- [Spring Cloud](spring-cloud-interview.md)
- [Spring Framework](spring-framework-interview.md)
- [Шпаргалка: Spring Data JPA/Hibernate](../../../frameworks/java-frameworks/spring/spring-data-jpa.md) — теория
