---
title: "Вопросы на собеседовании: Hibernate Caching"
description: "Hibernate L1/L2 cache, query cache, concurrency strategies (READ_ONLY, NONSTRICT_READ_WRITE, READ_WRITE, TRANSACTIONAL), провайдеры Ehcache, Infinispan, Redis"
tags:
  - interview
  - databases
  - hibernate-caching-interview
aliases:
  - "Hibernate Caching interview"
  - "Hibernate L2 cache interview"
  - "JPA caching собеседование"
  - "Hibernate cache вопросы"
  - "second level cache interview"
difficulty: "advanced"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Hibernate Caching`

`Hibernate Caching` — критичная тема для performance: `L1 cache` (Session), `L2 cache` (SessionFactory), Query Cache. Влияет на производительность JPA-приложений в разы. Часто спрашивается в контексте database performance и производительности.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Hibernate 6 Caching Docs](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#caching) — официальная документация
- [Baeldung: Hibernate Caching](https://www.baeldung.com/hibernate-second-level-cache) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Какие уровни кэша есть в Hibernate?

Hibernate предоставляет трёхуровневую модель кэширования:

| Уровень | Область | Включён по умолчанию |
|---------|---------|----------------------|
| **L1 cache** (Session/Persistence Context) | Одна сессия/транзакция | Да, всегда |
| **L2 cache** (SessionFactory / EntityManagerFactory) | Между сессиями, общий на приложение | Нет, нужна настройка |
| **Query cache** | Результаты конкретных queries | Нет, нужна настройка |

```java
// L1 cache — автоматически
EntityManager em = emf.createEntityManager();
User u1 = em.find(User.class, 1L);   // SQL запрос
User u2 = em.find(User.class, 1L);   // из L1 cache, без SQL
assertThat(u1).isSameAs(u2);          // идентичность объектов

// L2 cache — требует конфигурации
User u3 = anotherEm.find(User.class, 1L);  // если L2 включён — без SQL
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
## Q2. Как работает L1 cache (Persistence Context)?

**L1 cache** — автоматический кэш в рамках одной `EntityManager` (Session в Hibernate). Гарантирует:
- **Identity** — один объект на один ID в рамках сессии.
- **Dirty checking** — Hibernate отслеживает изменения и генерирует UPDATE.
- **Automatic flush** — изменения пишутся при commit или `flush()`.


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
```java
@Transactional
public void example() {
    User user = em.find(User.class, 1L);   // кэшируется в L1
    user.setName("Alice");                  // dirty checking запомнил изменение

    User sameUser = em.find(User.class, 1L); // из L1, тот же инстанс
    sameUser.setName("Bob");                 // user также "Bob"!

    // При commit Hibernate сгенерирует UPDATE users SET name='Bob' WHERE id=1
}
```

**Важно**: L1 cache живёт только пока открыта сессия. После `em.close()` — данные теряются.

## Q3. Как настроить L2 cache?

```xml
<!-- С Ehcache -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
```yaml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          use_query_cache: true
          region:
            factory_class: jcache
        javax:
          cache:
            provider: org.ehcache.jsr107.EhcacheCachingProvider
            uri: classpath:ehcache.xml
```

```xml
<!-- ehcache.xml -->
<config xmlns='http://www.ehcache.org/v3'>
    <cache alias="users">
        <expiry><ttl unit="minutes">30</ttl></expiry>
        <resources>
            <heap unit="entries">10000</heap>
        </resources>
    </cache>
</config>
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
```java
// Пометка entity для кэширования
@Entity
@Cacheable                                 // JPA стандарт
@Cache(usage = READ_WRITE, region = "users")  // Hibernate-specific
public class User {
    @Id Long id;
    String name;
}
```

## Q4. Какие concurrency strategies для L2 cache?

Strategy влияет на поведение кэша при concurrent modifications:

```java
// 1. READ_ONLY — только чтение, lightweight
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country { }   // справочники

// 2. NONSTRICT_READ_WRITE — eventually consistent
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BlogPost { }  // редко обновляется, stale OK

// 3. READ_WRITE — soft locking (как DB concurrency)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product { }   // часто обновляется


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
// 4. TRANSACTIONAL — distributed transaction (JTA)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Account { }   // финансовые данные
```

| Strategy | Use case | Производительность | Consistency |
|----------|----------|---------------------|-------------|
| `READ_ONLY` | Справочники | Лучшая | Strong |
| `NONSTRICT_READ_WRITE` | Редко обновляемые | Высокая | Eventual |
| `READ_WRITE` | Часто обновляемые | Средняя | Strong (via locks) |
| `TRANSACTIONAL` | Критически важные | Низкая (JTA) | Strong (distributed) |


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
## Q5. Что такое Query Cache и как его использовать?

**Query cache** — кэш результатов JPQL/Criteria запросов (не entities, а query results как List<Long>).

```java
// Включение query cache в application.yml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_query_cache: true
```

```java
// Использование
@Repository
public class ProductRepository {

    @Autowired
    private EntityManager em;


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
    public List<Product> findByCategory(String category) {
        return em.createQuery(
                "FROM Product WHERE category = :cat", Product.class)
            .setParameter("cat", category)
            .setHint(QueryHints.HINT_CACHEABLE, true)
            .setHint(QueryHints.HINT_CACHE_REGION, "products-by-category")
            .getResultList();
    }
}

// Spring Data JPA
@QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
@Query("SELECT p FROM Product p WHERE p.category = :cat")
List<Product> findByCategoryCached(@Param("cat") String category);
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
**Важно**: query cache требует включённого L2 cache (хранит только IDs, сами entities подтягивает из L2).

## Q6. Что такое Cache Region и как она работает?

**Region** — именованная логическая область кэша с отдельными настройками (size, TTL, eviction).

```java
// Разные regions для разных entities
@Entity
@Cache(usage = READ_WRITE, region = "users-region")
public class User { ... }


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
@Entity
@Cache(usage = READ_ONLY, region = "countries-region")
public class Country { ... }

@Entity
@ElementCollection
@Cache(usage = READ_WRITE, region = "user-roles-region")
public class UserRole { ... }
```

```xml
<!-- ehcache.xml — разные настройки для regions -->
<config>
    <cache alias="users-region">
        <expiry><ttl unit="minutes">15</ttl></expiry>
        <resources><heap unit="entries">10000</heap></resources>
    </cache>

    <cache alias="countries-region">
        <expiry><none/></expiry>  <!-- без expiry -->
        <resources><heap unit="entries">300</heap></resources>
    </cache>
</config>
```

## Q7. Какие провайдеры L2 cache существуют?


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
| Провайдер | Применение | Кластер | Настройка |
|-----------|-----------|---------|-----------|
| **Ehcache** | Single-node apps | Terracotta (paid) | Легко |
| **Infinispan** | Cluster, JBoss/WildFly | Native | Сложнее |
| **Hazelcast** | Distributed memory grid | Native | Средне |
| **Redis** (via Redisson) | Shared L2 cache | Native | Легко |
| **JCache (JSR-107)** | Unified API | Зависит | Средне |

```xml
<!-- Infinispan для cluster -->
<dependency>
    <groupId>org.infinispan</groupId>
    <artifactId>infinispan-hibernate-cache-v60</artifactId>
</dependency>
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
```yaml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          region:
            factory_class: org.infinispan.hibernate.cache.v60.InfinispanRegionFactory
          infinispan:
            statistics: true
```

## Q8. Как инвалидировать L2 cache?


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
```java
// Программно через Session API
Session session = em.unwrap(Session.class);
session.evict(user);                     // один entity из L1
session.clear();                         // весь L1

// SessionFactory — L2 cache
SessionFactory sessionFactory = em.getEntityManagerFactory()
    .unwrap(SessionFactory.class);


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
// Инвалидация L2 cache
sessionFactory.getCache().evict(User.class, 1L);          // один entity
sessionFactory.getCache().evictEntityData(User.class);    // все User
sessionFactory.getCache().evictQueryRegions();            // все query cache
sessionFactory.getCache().evictAll();                     // весь L2 cache

// JPA Cache API
Cache cache = emf.getCache();
cache.evict(User.class, 1L);
cache.evictAll();
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
```java
// Через Spring
@Service
public class CacheInvalidationService {
    @Autowired
    private EntityManagerFactory emf;

    public void invalidateUser(Long userId) {
        emf.getCache().evict(User.class, userId);
    }
}
```

## Q9. Какие метрики кэша нужно отслеживать?

```java
// Hibernate Statistics
Statistics stats = sessionFactory.getStatistics();
stats.setStatisticsEnabled(true);

// Метрики
long hits = stats.getSecondLevelCacheHitCount();
long misses = stats.getSecondLevelCacheMissCount();
double hitRatio = hits / (double)(hits + misses);

long queryHits = stats.getQueryCacheHitCount();
long queryMisses = stats.getQueryCacheMissCount();

// Per-entity
EntityStatistics entityStats = stats.getEntityStatistics(User.class.getName());
long userHits = entityStats.getCacheHitCount();

// Через Actuator
management:
  metrics:
    enable:
      hibernate: true
```

**Целевые значения**:
- **Hit ratio** > 80% — хороший кэш
- **Hit ratio** < 50% — кэш, возможно, не нужен или слишком мал
- **Evictions/sec** — высокий показатель = размер кэша мал

## Q10. Какие проблемы/pitfalls при работе с L2 cache?

1. **Неконсистентность при модификации через прямой SQL** (не через Hibernate):

```java
// Hibernate не знает об этом UPDATE → L2 cache устарел
jdbcTemplate.update("UPDATE users SET name = ? WHERE id = ?", "NewName", 1L);

// Решение: ручная инвалидация
sessionFactory.getCache().evict(User.class, 1L);
```


> [!mcq]
> - [x] Правильный ответ описывает основную концепцию | Это ключевой аспект темы, который нужно знать Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 - похож, но отличается | Этот вариант содержит ошибку в определении Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 - смешивает понятия | Путает с похожей, но отличной концепцией Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 - противоположное значение | Это антоним или противоположное действие Частая ошибка в реальном коде.
2. **Memory pressure** — большой cache может вытеснить важные данные из heap:

```xml
<heap unit="entries">1000000</heap>   <!-- огромный кэш -->
```

3. **Query cache invalidation** — любой UPDATE entity инвалидирует все queries этой entity:

```java
em.persist(new User(...));  // → все query cache regions с User инвалидируются
```

4. **Serialization overhead** для distributed caches (Redis, Hazelcast):

```java
// Entity должен быть Serializable
@Entity
public class User implements Serializable { ... }
```

5. **Cache stampede** при истечении TTL и множестве concurrent запросов.

6. **Ненужный кэш для append-only данных** (audit logs) — пустая трата памяти.

## Q11. Как комбинировать L2 cache с Spring @Cacheable?

Это два **разных** механизма кэширования:

| Аспект | Hibernate L2 | Spring @Cacheable |
|--------|--------------|-------------------|
| Уровень | ORM (entities) | Application (method results) |
| Инвалидация | Автомат (через Hibernate) | Ручная (@CacheEvict) |
| Ключ | Entity ID | Параметры метода |
| Работает для | JPA entities | Любые методы |

```java
// Обычно используют один из них, не оба для одной задачи
@Service
public class ProductService {

    // Вариант 1 — Hibernate L2
    public Product findById(Long id) {
        return repository.findById(id).orElse(null);  // L2 кэширует entity
    }

    // Вариант 2 — Spring @Cacheable
    @Cacheable("products")
    public ProductDto findByIdCached(Long id) {
        Product p = repository.findById(id).orElse(null);
        return toDto(p);  // кэширует DTO, не entity
    }
}
```

**Правило**: Spring @Cacheable для результатов расчётов/агрегаций; Hibernate L2 — для часто читаемых entities.

## Q12. Когда НЕ стоит включать L2 cache?

1. **Часто обновляемые entities** — инвалидации убивают выгоду:

```java
// НЕ кэшировать — TODO items постоянно меняются
@Entity @Cache(usage = READ_WRITE)  // НЕ СТОИТ
public class TodoItem { ... }
```

2. **Append-only данные** — чтение уникально, кэш не попадает:

```java
@Entity
public class AuditLog { }  // каждая запись уникальна — кэш бесполезен
```

3. **Большие объекты** (BLOB, большие JSON) — занимают много памяти кэша.

4. **Single-instance приложения без повторных запросов** — L1 достаточно.

5. **Strict consistency** требуется — L2 допускает минимальные рассинхронизации.

## Q13. Что такое natural id cache?

**Natural ID** — естественный уникальный идентификатор (email, username) вместо суррогатного (auto-generated ID).

```java
@Entity
@Cache(usage = READ_WRITE)
@NaturalIdCache  // ← отдельный cache для natural ID
public class User {
    @Id @GeneratedValue Long id;

    @NaturalId(mutable = false)  // неизменяемый email
    String email;

    @NaturalId(mutable = true)   // изменяемый username
    String username;

    String name;
}
```

```java
// Поиск по natural ID с кэшем
User user = em.unwrap(Session.class)
    .bySimpleNaturalId(User.class)
    .load("alice@example.com");  // кэшируется отдельно от обычного L2
```

Эффективно для частых lookup'ов по email/username (логин, поиск пользователя).

## Q14. Как решить N+1 проблему с кэшем?

```java
// N+1 проблема: 1 запрос за List<User>, N запросов за их Profile
List<User> users = em.createQuery("FROM User").getResultList();
for (User u : users) {
    u.getProfile().getFullName();  // N запросов!
}

// Решение 1: JOIN FETCH
em.createQuery("FROM User u JOIN FETCH u.profile").getResultList();

// Решение 2: @BatchSize (Hibernate-specific)
@Entity
public class User {
    @ManyToOne
    @BatchSize(size = 20)  // загружает profiles пачками по 20
    Profile profile;
}

// Решение 3: L2 cache для Profile
@Entity
@Cache(usage = READ_ONLY)
public class Profile { ... }
// Первая итерация цикла заполнит L2, последующие будут попадать в кэш
```

**Правило**: L2 cache помогает если data часто читается повторно; JOIN FETCH — одномоментно.

## Q15. Какие best practices при работе с Hibernate cache?

1. **Всегда измеряйте** — `Statistics` + Actuator метрики. Без данных неизвестно, помогает ли кэш.

2. **Кэшируйте справочники** (Country, Currency) с `READ_ONLY`.

3. **Настраивайте TTL и max size** — неограниченный кэш может исчерпать heap.

4. **Для кластеров** — distributed cache (Infinispan, Redis) с осторожностью к serialization.

5. **Собирайте hit ratio** в prod и реагируйте при падении.

6. **Периодически инвалидируйте полностью** в batch jobs, если прямой SQL обновляет данные.

7. **Не кэшируйте**:
   - Транзакционные данные с частыми UPDATE
   - Append-only (logs, events)
   - Уникальные query результаты
   - BLOB/CLOB поля

8. **Query cache работает только с L2** — один без другого бесполезен.

9. **Используйте metrics endpoints** для observability:

```yaml
management.endpoints.web.exposure.include: metrics,prometheus
management.metrics.enable.hibernate: true
```

## See also

- [Hibernate](hibernate-interview.md) — основы Hibernate, Session, persistent context
- [Hibernate Relationships](hibernate-relationships-interview.md) — @OneToMany, FetchType и взаимодействие с кэшем
- [Spring Cache](../frameworks/spring/spring-cache-interview.md) — @Cacheable и CacheManager
- [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) — JPA repositories
- [Redis](redis-interview.md) — Redis как L2 cache provider
- [Database Performance](../performance/database-performance-interview.md) — производительность БД
- [Caching Strategies](../architecture/caching-strategies-interview.md) — паттерны кэширования
- [Caching Performance](../performance/caching-performance-interview.md) — метрики hit/miss ratio
- [SQL](sql-interview.md) — SQL основы для понимания Query Cache
- [PostgreSQL](postgresql-interview.md) — типичная БД для Hibernate
