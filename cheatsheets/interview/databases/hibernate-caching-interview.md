---
title: "Вопросы на собеседовании: Hibernate Caching"
description: "Hibernate L1/L2 cache, query cache, concurrency strategies (READ_ONLY, NONSTRICT_READ_WRITE, READ_WRITE, TRANSACTIONAL), провайдеры Ehcache, Infinispan, Redis"
tags:
  - interview
  - databases
  - hibernate-caching-interview
type: "interview"
difficulty: "advanced"
aliases:
  - "Вопросы на собеседовании"
  - "Hibernate Caching"
  - "Hibernate Caching interview"
  - "Hibernate L2 cache interview"
prerequisites:
  - "[[hibernate-caching]]"
next: []
updated: "2026-05-07"
---
# Вопросы на собеседовании: `Hibernate Caching`

`Hibernate Caching` — одна из ключевых тем по производительности JPA-приложений. Три уровня кэша — `L1 cache` (в рамках Session), `L2 cache` (общий на SessionFactory) и Query Cache — способны кратно снизить число обращений к БД. На собеседованиях её разбирают в связке с производительностью БД: важно не только перечислить уровни, но и объяснить, когда кэш помогает, а когда вредит.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Hibernate 6 Caching Docs](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#caching) — официальная документация
- [Baeldung: Hibernate Caching](https://www.baeldung.com/hibernate-second-level-cache) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Какие уровни кэша есть в Hibernate?

В Hibernate три независимых уровня кэширования, и различаются они областью видимости — кто и как долго видит закэшированные данные.

| Уровень | Область | Включён по умолчанию |
|---------|---------|----------------------|
| **L1 cache** (Session/Persistence Context) | Одна сессия/транзакция | Да, всегда |
| **L2 cache** (SessionFactory / EntityManagerFactory) | Между сессиями, общий на приложение | Нет, нужна настройка |
| **Query cache** | Результаты конкретных queries | Нет, нужна настройка |

Логика такая: **L1** живёт в пределах одной сессии и убирает повторные SQL внутри транзакции — отключить его нельзя, это часть работы ORM. **L2** общий на всё приложение и переживает закрытие сессии: данные, прочитанные одним пользователем, могут отдаться другому без запроса в БД. **Query cache** кэширует не сами entities, а результаты конкретных запросов (список ID), и работает только поверх L2.

```java
// L1 cache — автоматически
EntityManager em = emf.createEntityManager();
User u1 = em.find(User.class, 1L);   // SQL запрос
User u2 = em.find(User.class, 1L);   // из L1 cache, без SQL
assertThat(u1).isSameAs(u2);          // идентичность объектов

// L2 cache — требует конфигурации
User u3 = anotherEm.find(User.class, 1L);  // если L2 включён — без SQL
```

## Q2. Как работает L1 cache (Persistence Context)?

**L1 cache** — это и есть persistence context: автоматический кэш в рамках одной `EntityManager` (Session в Hibernate). Он не опциональный — включён всегда, потому что без него ORM не сможет гарантировать корректность.

Из него вытекают три ключевых свойства:

- **Identity (тождество объектов)** — один объект на один ID в пределах сессии. Повторный `find` по тому же ID вернёт тот же самый инстанс, без нового SQL.
- **Dirty checking (отслеживание изменений)** — Hibernate запоминает исходное состояние загруженных объектов и сам генерирует UPDATE для тех полей, что изменились. Вызывать `save`/`update` руками не нужно.
- **Automatic flush (автосброс)** — накопленные изменения пишутся в БД при commit транзакции или явном `flush()`, а не сразу при изменении поля.

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

**Важно**: L1 cache живёт только пока открыта сессия. После `em.close()` данные теряются — поэтому L1 не помогает между разными HTTP-запросами и не разделяется между пользователями. Для этого нужен L2.

## Q3. Как настроить L2 cache?

Включение L2 — это три шага: подключить провайдер, разрешить кэш в конфигурации Hibernate и явно пометить нужные entities как кэшируемые. По умолчанию L2 выключен, и забытый последний шаг — частая причина «настроил, а кэш не работает».

**Шаг 1 — зависимости.** L2 не хранит данные сам, ему нужен провайдер. Современный путь — JCache (JSR-107) как абстракция плюс конкретная реализация, например Ehcache:

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

**Шаг 2 — конфигурация.** Включаем L2 (и при желании query cache), указываем фабрику регионов `jcache` и провайдер:

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

Сам провайдер настраивается отдельным файлом — здесь задаются TTL и размеры регионов:

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

**Шаг 3 — пометить entity.** Без этой аннотации entity в L2 не попадёт, даже если кэш включён. `@Cacheable` — стандарт JPA (включает кэширование), `@Cache` — расширение Hibernate, где задаются стратегия конкуренции и регион:

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

## Q4. Какие стратегии конкуренции (concurrency strategy) есть у L2 cache?

Стратегия конкуренции определяет, как кэш ведёт себя, когда данные **одновременно** читают и меняют разные транзакции — то есть какой компромисс между консистентностью и скоростью выбран. Есть четыре варианта, от самого быстрого и слабого до самого строгого и медленного:

- **`READ_ONLY`** — для неизменяемых данных. Кэш не отслеживает запись вообще; любая попытка изменить такой entity бросит исключение. Самый быстрый вариант, идеален для справочников.
- **`NONSTRICT_READ_WRITE`** — eventually consistent. При обновлении entity просто удаляется из кэша (без блокировок), и есть короткое окно, когда другой поток может прочитать устаревшее значение. Подходит, если редкий stale-read не критичен.
- **`READ_WRITE`** — строгая консистентность через soft locking. На время записи запись в кэше блокируется, и параллельные читатели идут в БД, а не получают грязные данные. Дороже, но безопасно для часто меняющихся данных.
- **`TRANSACTIONAL`** — кэш участвует в распределённой XA-транзакции (JTA) вместе с БД: коммит/откат атомарны для обоих. Самый строгий и самый медленный, требует JTA-провайдера (Infinispan и т.п.).

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

## Q5. Что такое Query Cache и как его использовать?

**Query cache** кэширует результаты конкретных JPQL/Criteria-запросов. Важная деталь: он хранит не сами объекты, а только список их идентификаторов (`List<Long>`) под ключом «текст запроса + параметры». Сами entities затем подтягиваются из L2 по этим ID — поэтому query cache **бесполезен без включённого L2**.

Включается он в два действия: глобально разрешить query cache в конфигурации и пометить каждый кэшируемый запрос хинтом `cacheable` (по умолчанию запросы не кэшируются, даже когда query cache включён).

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

**Подводный камень**: любое изменение entity (insert/update/delete) инвалидирует **весь** query cache по этой entity — Hibernate не знает, какие запросы затронуты, и сбрасывает их целиком. Поэтому query cache выгоден только для запросов по редко меняющимся данным; на горячих таблицах он будет постоянно промахиваться.

## Q6. Что такое Cache Region и как она работает?

**Region (регион)** — именованная область кэша со своими настройками: максимальный размер, TTL и политика вытеснения (eviction). Смысл регионов в том, чтобы разные типы данных хранились по разным правилам: справочники можно держать вечно и без ограничений, а часто меняющиеся entities — с коротким TTL и жёстким лимитом по памяти.

По умолчанию каждая entity получает регион с именем своего полного класса, но через атрибут `region` несколько entities можно собрать в один регион или, наоборот, развести по отдельным — чтобы прицельно настроить TTL и размер под каждый.

```java
// Разные regions для разных entities
@Entity
@Cache(usage = READ_WRITE, region = "users-region")
public class User { ... }

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

Hibernate сам данные не хранит — за это отвечает подключаемый провайдер. Главный критерий выбора — нужен ли кэш, общий для нескольких узлов приложения.

| Провайдер | Применение | Кластер | Настройка |
|-----------|-----------|---------|-----------|
| **Ehcache** | Single-node apps | Terracotta (paid) | Легко |
| **Infinispan** | Cluster, JBoss/WildFly | Native | Сложнее |
| **Hazelcast** | Distributed memory grid | Native | Средне |
| **Redis** (via Redisson) | Shared L2 cache | Native | Легко |
| **JCache (JSR-107)** | Unified API | Зависит | Средне |

**Как выбирать:**

- **Один инстанс приложения** — Ehcache: локальный in-heap кэш, просто настраивается, без сериализации.
- **Кластер с общим кэшем** — Infinispan, Hazelcast или Redis: данные разделяются между узлами, инвалидация согласована. Цена — сетевые задержки и обязательная сериализация entities.
- **JCache (JSR-107)** — не провайдер, а стандартный API: пишете код против него, а конкретную реализацию (тот же Ehcache или Infinispan) подключаете отдельно, не привязываясь к вендору.

```xml
<!-- Infinispan для cluster -->
<dependency>
    <groupId>org.infinispan</groupId>
    <artifactId>infinispan-hibernate-cache-v60</artifactId>
</dependency>
```

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

В обычной работе инвалидация происходит сама: когда вы меняете entity через Hibernate, он сбрасывает соответствующую запись в L2. Ручная инвалидация нужна, когда данные изменились **в обход** Hibernate — прямым SQL, миграцией, другим сервисом, — и кэш об этом не узнал.

Сбрасывать можно на разных уровнях, от точечного до полного:

- **L1 (текущая сессия):** `evict(obj)` убирает один объект, `clear()` — все.
- **L2 точечно:** `evict(User.class, id)` — конкретный entity по ID.
- **L2 по типу:** `evictEntityData(User.class)` — все экземпляры этого класса.
- **L2 полностью:** `evictQueryRegions()` — весь query cache, `evictAll()` — весь L2.

Чем шире сброс, тем больше последующих промахов и нагрузки на БД, поэтому предпочитают точечную инвалидацию.

```java
// Программно через Session API
Session session = em.unwrap(Session.class);
session.evict(user);                     // один entity из L1
session.clear();                         // весь L1

// SessionFactory — L2 cache
SessionFactory sessionFactory = em.getEntityManagerFactory()
    .unwrap(SessionFactory.class);

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

Ключевая метрика — **hit ratio**, доля попаданий: `hits / (hits + misses)`. Она показывает, реально ли кэш экономит запросы к БД или просто занимает память. Hibernate отдаёт эти счётчики через `Statistics` (по умолчанию выключены, надо включить), причём как суммарно, так и по каждой entity и по query cache отдельно — это помогает понять, какой именно регион «не греется».

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

**Как читать значения:**
- **Hit ratio > 80%** — кэш работает, большинство чтений идёт мимо БД.
- **Hit ratio < 50%** — тревожный сигнал: либо данные слишком разнообразны и кэш не попадает, либо регион мал и записи вытесняются раньше повторного чтения. Часто проще такой кэш отключить.
- **Высокий темп evictions/sec** — записи вытесняются слишком быстро, размер региона мал; стоит увеличить лимит или пересмотреть, что кэшируется.

## Q10. Какие подводные камни у L2 cache?

L2 кэш — не «бесплатное ускорение»: он добавляет источник рассинхронизации и нагрузку на память. Главные ловушки:

1. **Рассинхронизация при изменении мимо Hibernate.** Hibernate инвалидирует кэш только для своих операций. Прямой `jdbcTemplate`, миграция или другой сервис меняют БД незаметно для кэша — и он отдаёт устаревшие данные. Лечится ручной инвалидацией:

```java
// Hibernate не знает об этом UPDATE → L2 cache устарел
jdbcTemplate.update("UPDATE users SET name = ? WHERE id = ?", "NewName", 1L);

// Решение: ручная инвалидация
sessionFactory.getCache().evict(User.class, 1L);
```

2. **Давление на память (memory pressure).** In-heap кэш ест ту же память, что и приложение. Неограниченный регион способен вытеснить рабочие данные и спровоцировать долгие GC-паузы или OOM:

```xml
<heap unit="entries">1000000</heap>   <!-- огромный кэш -->
```

3. **Грубая инвалидация query cache.** Любой insert/update/delete entity сбрасывает весь query cache по ней — Hibernate не разбирает, какие именно запросы затронуты:

```java
em.persist(new User(...));  // → все query cache regions с User инвалидируются
```

4. **Накладные расходы на сериализацию.** В распределённых кэшах (Redis, Hazelcast) entity сериализуется при каждой записи и десериализуется при чтении — это стоит CPU, и сам entity обязан быть `Serializable`:

```java
// Entity должен быть Serializable
@Entity
public class User implements Serializable { ... }
```

5. **Cache stampede (лавина запросов).** Когда у популярной записи истекает TTL, десятки параллельных запросов одновременно промахиваются и разом бьют в БД, пока кэш не перезаполнится.

6. **Бесполезный кэш для append-only данных** (audit logs): каждая запись читается редко и неповторно, поэтому кэш только зря держит память.

## Q11. Как комбинировать L2 cache с Spring @Cacheable?

Это два независимых механизма, работающих на **разных уровнях**, и путать их не стоит. Hibernate L2 кэширует entities внутри ORM-слоя и инвалидируется автоматически при изменениях через Hibernate. Spring `@Cacheable` кэширует возвращаемое значение метода по его аргументам, ничего не знает про БД и требует ручной инвалидации через `@CacheEvict`.

| Аспект | Hibernate L2 | Spring @Cacheable |
|--------|--------------|-------------------|
| Уровень | ORM (entities) | Application (method results) |
| Инвалидация | Автомат (через Hibernate) | Ручная (@CacheEvict) |
| Ключ | Entity ID | Параметры метода |
| Работает для | JPA entities | Любые методы |

Главная опасность комбинации — двойная инвалидация: L2 сам сбросит изменённый entity, а вот закэшированный `@Cacheable`-метод об этом не узнает и продолжит отдавать старое значение. Поэтому для одной задачи берут что-то одно.

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

**Эмпирическое правило**: Spring `@Cacheable` — для результатов расчётов, агрегаций и собранных DTO; Hibernate L2 — для часто читаемых, редко меняющихся entities.

## Q12. Когда НЕ стоит включать L2 cache?

Кэш окупается только когда одни и те же редко меняющиеся данные читаются многократно. Если это условие не выполняется, L2 даёт отрицательный эффект — тратит память и CPU на инвалидацию, ничего не экономя. Не стоит включать его в таких случаях:

1. **Часто обновляемые entities** — постоянные инвалидации съедают всю выгоду, и до повторного чтения запись успевает протухнуть:

```java
// НЕ кэшировать — TODO items постоянно меняются
@Entity @Cache(usage = READ_WRITE)  // НЕ СТОИТ
public class TodoItem { ... }
```

2. **Append-only данные** — каждая запись уникальна и читается обычно один раз, попаданий в кэш не будет:

```java
@Entity
public class AuditLog { }  // каждая запись уникальна — кэш бесполезен
```

3. **Большие объекты** (BLOB, объёмный JSON) — несколько таких записей вытеснят сотни полезных и быстро забьют heap.

4. **Один инстанс приложения без повторных запросов** — здесь хватает L1 в рамках сессии, L2 ничего не добавит.

5. **Требуется строгая консистентность** — L2 (кроме `TRANSACTIONAL`) допускает короткие окна рассинхронизации, что неприемлемо, например, для остатков на счёте.

## Q13. Что такое natural id cache?

**Natural ID** — естественный бизнес-идентификатор (email, username), уникальный, но не первичный ключ. Обычный L2 индексирует entity по суррогатному ID, поэтому поиск по email кэш бы не ускорил. `@NaturalIdCache` заводит отдельный кэш-индекс «natural ID → суррогатный ID»: первый lookup идёт в БД, дальше Hibernate по email сразу находит ID и достаёт сам объект из основного L2 — без SQL.

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

**Сценарий применения**: частый поиск по email/username — логин, разрешение упоминаний, проверка занятости имени. Там, где по бизнес-ключу обращаются на каждый запрос, такой кэш заметно снимает нагрузку с БД.

## Q14. Как решить N+1 проблему с кэшем?

N+1 — это когда один запрос за списком сущностей порождает по отдельному запросу на каждую связанную. Кэш — лишь один из инструментов против неё, и не всегда лучший. Есть три подхода с разной областью применения:

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

**Когда что выбирать**: `JOIN FETCH` и `@BatchSize` решают N+1 здесь и сейчас, в рамках одного запроса, и работают всегда. L2 помогает только если связанные данные читаются повторно между запросами (как общие Profile у разных пользователей) — для одноразовой выборки он бесполезен, потому что первый проход всё равно идёт в БД.

## Q15. Какие практики рекомендованы при работе с Hibernate cache?

Сквозная идея: кэшировать прицельно и под измерением, а не «включить L2 на всё и надеяться». Конкретные рекомендации:

1. **Всегда измеряйте** — `Statistics` плюс метрики Actuator. Без hit ratio вы не знаете, помогает кэш или просто ест память.

2. **Кэшируйте справочники** (Country, Currency) со стратегией `READ_ONLY` — они почти не меняются и читаются постоянно, это идеальный кандидат.

3. **Задавайте TTL и максимальный размер** для каждого региона — неограниченный кэш рано или поздно исчерпает heap.

4. **Для кластеров** берите распределённый кэш (Infinispan, Redis), но помните про стоимость сериализации и держите в нём только то, что действительно общее для узлов.

5. **Следите за hit ratio в проде** и реагируйте на падение — это первый признак, что регион мал или данные стали меняться чаще.

6. **Полностью инвалидируйте кэш в batch-задачах**, если они обновляют данные прямым SQL мимо Hibernate.

7. **Не кэшируйте** то, что не даст попаданий:
   - транзакционные данные с частыми UPDATE;
   - append-only (логи, события);
   - результаты уникальных, неповторяющихся запросов;
   - тяжёлые поля BLOB/CLOB.

8. **Помните, что query cache работает только поверх L2** — включить один без другого бессмысленно.

9. **Выставляйте metrics-эндпоинты** для наблюдаемости:

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
