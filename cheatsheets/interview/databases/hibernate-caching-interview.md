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
> - [ ] L1 cache (`Session`) и L2 cache (`SessionFactory`) включены по умолчанию; query cache требует отдельной настройки | L1 включён всегда, но L2 по умолчанию выключен — нужны `hibernate.cache.use_second_level_cache=true` и provider. ❌ ПОСЛЕДСТВИЕ: команда добавляет `@Cache(READ_WRITE)` на entity, но без `use_second_level_cache=true` аннотация молча игнорируется — L2 не работает, hit ratio = 0%, разработчики уверены что «всё кэшируется».
> - [ ] Hibernate имеет один уровень кэша — `SessionFactory`-scoped, общий для всех транзакций | Существует трёхуровневая модель: L1 (Session-scoped), L2 (SessionFactory-scoped), Query Cache (результаты запросов). ❌ ПОСЛЕДСТВИЕ: разработчик считает что `em.find()` всегда идёт в общий кэш, но L1 у каждой сессии свой → между сессиями entity загружается заново, неоптимизированные batch-jobs.
> - [ ] Query cache хранит сами entity и работает полностью независимо от L2 cache | Query cache хранит только идентификаторы (`List<Long>`); сами entity подтягиваются из L2 — без L2 query cache бесполезен и приводит к лишним SELECT. ❌ ПОСЛЕДСТВИЕ: включают `use_query_cache=true` без `use_second_level_cache=true` — query cache не даёт выгоды, latency не падает, в логах warning'и про missing L2 region.
> - [x] Три уровня: L1 (`Session`/Persistence Context, всегда включён), L2 (`SessionFactory`, опционально, общий для приложения), Query Cache (результаты JPQL/Criteria, требует L2) | L1 гарантирует identity в рамках транзакции, L2 переживает закрытие сессии, Query Cache хранит ID-листы запросов. ✓ ПРИМЕНЯТЬ: `Country`/`Currency` → L2 `READ_ONLY`; топ-100 товаров → Query Cache + L2; обычный CRUD → L1. 📋 ПРАВИЛО: «L1 — identity, L2 — между сессиями, Query — повторные запросы». 🔗 См. Q2, Q3, Q5.
## Q2. Как работает L1 cache (Persistence Context)?

**L1 cache** — автоматический кэш в рамках одной `EntityManager` (Session в Hibernate). Гарантирует:
- **Identity** — один объект на один ID в рамках сессии.
- **Dirty checking** — Hibernate отслеживает изменения и генерирует UPDATE.
- **Automatic flush** — изменения пишутся при commit или `flush()`.


> [!mcq]
> - [ ] L1 cache переживает `em.close()` и доступен следующим транзакциям того же потока | L1 живёт ровно столько, сколько открыт `EntityManager`/`Session`; после `close()` все managed entity становятся detached. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает что `findById` после `em.close()` вернёт ту же сущность из L1 — получает новую загрузку из БД и `LazyInitializationException` при доступе к lazy-связям.
> - [x] L1 даёт identity (один объект на ID в сессии), dirty checking (отслеживание изменений), automatic flush при commit; живёт пока открыт `EntityManager` | Автоматический per-session кэш с repeatable read внутри транзакции и сборкой UPDATE через snapshot. ✓ ПРИМЕНЯТЬ: `em.find(User, 1L)` + `user.setName(...)` + commit — Hibernate сам генерирует UPDATE; повторный `find` отдаёт тот же инстанс. 📋 ПРАВИЛО: «L1 = identity + dirty checking в рамках Session». 🔗 См. Q1, Q8.
> - [ ] L1 общий для всех `EntityManager`'ов одного приложения, как L2 | L1 строго per-session; разные `EntityManager`'ы видят свои копии одной и той же сущности. ❌ ПОСЛЕДСТВИЕ: два `@Transactional` метода в одном request читают одну entity, ожидают idential reference — получают разные инстансы → `==` сравнения ломаются, equals/hashCode падают непредсказуемо.
> - [ ] Dirty checking требует явного вызова `em.merge()` для отправки UPDATE | Dirty checking автоматический: Hibernate сравнивает текущее состояние с snapshot'ом при flush/commit и сам генерирует UPDATE. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `em.merge(user)` после каждого setter'а в managed entity — лишние SELECT'ы перед UPDATE, double work, p99 latency растёт на 30% в endpoint'ах с UPDATE.
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
> - [x] Подключить `hibernate-jcache` + конкретный provider (`ehcache`/`infinispan`), включить `use_second_level_cache=true`, указать `region.factory_class=jcache` и `javax.cache.provider` | JCache (JSR-107) — стандартный мост Hibernate к provider'у; без явного провайдера фабрика регионов не создаётся. ✓ ПРИМЕНЯТЬ: Spring Boot + Ehcache 3 — `hibernate-jcache` + `ehcache` + `EhcacheCachingProvider` + `classpath:ehcache.xml`; для кластера — `infinispan-hibernate-cache-v60`. 📋 ПРАВИЛО: «jcache-мост + provider + use_second_level_cache=true». 🔗 См. Q1, Q7.
> - [ ] Достаточно подключить `hibernate-jcache` — provider Hibernate выберет автоматически по classpath | `hibernate-jcache` — это только мост к JSR-107; нужен конкретный provider (`ehcache`, `infinispan-jcache`) и `javax.cache.provider`. ❌ ПОСЛЕДСТВИЕ: на старте приложения `NoCachingRegionFactory` тихо подменяет реальный provider, L2 не работает в production, hit ratio = 0% обнаруживается через неделю по росту DB load.
> - [ ] Установить `use_second_level_cache=false` и добавить `@Cacheable` к entity — Hibernate включит L2 при обнаружении аннотации | Аннотация `@Cacheable` без флага `use_second_level_cache=true` молча игнорируется. ❌ ПОСЛЕДСТВИЕ: команда добавляет `@Cacheable`/`@Cache` на 50 сущностей при ревью «давайте кэшировать», в production hit ratio остаётся 0% — флаг не выставлен, никакой ошибки не выводится.
> - [ ] Достаточно прописать `spring.cache.type=jcache` — Hibernate подхватит конфигурацию из Spring Cache | `spring.cache.type` управляет Spring `CacheManager` (для `@Cacheable`), но не Hibernate L2; это два независимых уровня. ❌ ПОСЛЕДСТВИЕ: `@Cacheable("users")` работает, hit ratio 80%, но `userRepository.findById()` всё равно идёт в БД — `Hibernate L2` не сконфигурирован, разработчик не понимает почему «кэш есть, но не работает».
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
> - [ ] `@Cacheable` JPA достаточно — concurrency strategy подбирается Hibernate автоматически | `@Cacheable` помечает entity как кэшируемую, но без `@Cache(usage=...)` Hibernate использует default `READ_WRITE` или fallback к no-op в зависимости от provider'а. ❌ ПОСЛЕДСТВИЕ: на справочной `Country` стоит только `@Cacheable`, провайдер выбирает `READ_WRITE` с soft-locks → лишний overhead на immutable данных, p99 select по справочнику растёт на 15-20% против `READ_ONLY`.
> - [x] `@Cacheable` (JPA) + `@Cache(usage=READ_WRITE, region="users")` (Hibernate) — JPA-аннотация включает кэширование, Hibernate-специфичная задаёт стратегию и регион | Только в паре: `@Cacheable` — стандарт JPA для включения; `@Cache` — Hibernate-only и обязателен для управления concurrency и регионом. ✓ ПРИМЕНЯТЬ: `Product` с частыми UPDATE → `@Cacheable + @Cache(READ_WRITE, region="products")`; `Country` immutable → `@Cacheable + @Cache(READ_ONLY, region="countries")`. 📋 ПРАВИЛО: «JPA `@Cacheable` включает + Hibernate `@Cache` настраивает». 🔗 См. Q4, Q6.
> - [ ] `@Cache(usage=...)` без `@Cacheable` достаточно для включения L2 на entity | Поведение зависит от `shared-cache-mode`: при `ENABLE_SELECTIVE` (default JPA) нужна `@Cacheable`; при `ALL` — необязательна. Полагаться на режим без явной аннотации опасно. ❌ ПОСЛЕДСТВИЕ: разработчик ставит только `@Cache`, на проде `shared-cache-mode=ENABLE_SELECTIVE` — entity не попадает в L2, hit ratio низкий, миграция на другой стенд с `ALL` маскирует проблему.
> - [ ] `region` в `@Cache` обязателен; без него entity не попадает в L2 | `region` опционален: по умолчанию используется полное имя класса как имя региона. Атрибут нужен только для группировки или явного управления настройками. ❌ ПОСЛЕДСТВИЕ: команда тратит часы на дебаг «почему `@Cache` без region не работает», добавляет регионы для всех entity, плодит сотни конфигураций — реальной причиной была другая проблема (отсутствие `use_second_level_cache=true`).
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
> - [ ] `READ_ONLY` можно ставить на любой entity — Hibernate сам разрешит запись через invalidate-on-update | `READ_ONLY` запрещает изменения: при попытке UPDATE Hibernate бросает `UnsupportedOperationException` или возвращает stale данные в зависимости от provider'а. ❌ ПОСЛЕДСТВИЕ: справочник `Currency` помечают `READ_ONLY`, через год добавляют edit-форму в админке — UPDATE падает с ошибкой провайдера, либо тихо корраптит кэш и финансовые расчёты дают расхождения.
> - [ ] `NONSTRICT_READ_WRITE` подходит для финансовых entity — strict consistency через soft-lock | `NONSTRICT_READ_WRITE` допускает stale read: между UPDATE в БД и инвалидацией в кэше есть окно, в котором другой узел читает старое значение. ❌ ПОСЛЕДСТВИЕ: на `Account` (баланс счёта) ставят `NONSTRICT_READ_WRITE` ради скорости — после списания средств другой запрос читает старый баланс из L2, разрешает второе списание → отрицательный баланс, financial bug.
> - [x] `READ_ONLY` для immutable справочников (`Country`); `NONSTRICT_READ_WRITE` для редко обновляемых с допустимой stale (`BlogPost`); `READ_WRITE` для часто обновляемых с soft-lock (`Product`); `TRANSACTIONAL` для критичных через JTA (`Account`) | Каждая стратегия — баланс между консистентностью и производительностью; неправильный выбор = либо stale read, либо лишний overhead. ✓ ПРИМЕНЯТЬ: `Country`/`Currency` → `READ_ONLY`; `Article`/`BlogPost` → `NONSTRICT_READ_WRITE`; `Product`/`Inventory` → `READ_WRITE`; `Account`/`Payment` → `TRANSACTIONAL` + JTA. 📋 ПРАВИЛО: «immutable=READ_ONLY, mutable=READ_WRITE, financial=TRANSACTIONAL». 🔗 См. Q3, Q10.
> - [ ] `TRANSACTIONAL` — самая быстрая стратегия, потому что использует distributed transactions | `TRANSACTIONAL` самая медленная: требует JTA, distributed two-phase commit, синхронизацию между cache и БД на каждый коммит. ❌ ПОСЛЕДСТВИЕ: команда выбирает `TRANSACTIONAL` для всех entity «для надёжности» без JTA-инфраструктуры — приложение падает при старте с `JtaPlatform not configured`, либо при наличии JTA p99 latency растёт в 3-5 раз.
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
> - [ ] `READ_WRITE` использует pessimistic lock в БД для синхронизации кэша между узлами | `READ_WRITE` применяет soft-lock на уровне самого кэша (timestamp/marker), а не БД-локи; синхронизация кэша между узлами — это уровень provider'а (distributed cache). ❌ ПОСЛЕДСТВИЕ: разработчик ожидает что `READ_WRITE` сериализует записи через `SELECT ... FOR UPDATE`, кладётся на это в design — на нагрузке выясняется что race condition между UPDATE и cache-put возможен, появляются stale значения.
> - [ ] `NONSTRICT_READ_WRITE` инвалидирует кэш до UPDATE в БД, гарантируя последующий fresh read | `NONSTRICT_READ_WRITE` инвалидирует кэш ПОСЛЕ commit'а транзакции; между UPDATE и инвалидацией есть окно, в котором другой запрос видит старое значение. ❌ ПОСЛЕДСТВИЕ: команда выбирает `NONSTRICT_READ_WRITE` для `BlogPost` ожидая «строгую» инвалидацию — после редактирования поста читатели до 100мс видят старую версию, баг репортится как «иногда правки не применяются».
> - [x] `READ_ONLY` — лучшая performance (без локов), strong consistency для immutable; `READ_WRITE` — soft-lock в кэше, strong consistency для mutable; `NONSTRICT_READ_WRITE` — eventual consistency, выше throughput; `TRANSACTIONAL` — JTA, distributed strong, но самая медленная | Это компромисс между throughput и консистентностью: чем строже стратегия, тем выше overhead. ✓ ПРИМЕНЯТЬ: справочники → `READ_ONLY` для max throughput; домен с UPDATE и SLA на консистентность → `READ_WRITE`; отчёты, которые «ок устаревшие на минуту» → `NONSTRICT_READ_WRITE`. 📋 ПРАВИЛО: «строже стратегия → выше latency, ниже throughput». 🔗 См. Q3, Q9.
> - [ ] `TRANSACTIONAL` идентична `READ_WRITE` по семантике, но проще в настройке | `TRANSACTIONAL` требует JTA-провайдера (`Atomikos`, `Narayana`, container-managed), а `READ_WRITE` работает без JTA — это принципиально разные требования к инфраструктуре. ❌ ПОСЛЕДСТВИЕ: миграция с `READ_WRITE` на `TRANSACTIONAL` «для лучшей консистентности» в Spring Boot без JTA — приложение не стартует, или выбрасывает `TransactionRequiredException` на каждом put в кэш.
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
> - [ ] Query Cache хранит сами entity целиком — каждый hit отдаёт List готовых объектов без обращения к БД | Query Cache хранит только идентификаторы (`List<Long>`) и timestamp; сами entity подтягиваются из L2 (или БД, если в L2 нет). ❌ ПОСЛЕДСТВИЕ: команда полагается что Query Cache «всё хранит», отключает L2 для экономии памяти — каждый Query Cache hit идёт N+1 раз в БД за entity, latency не падает, кэш бесполезен.
> - [ ] Query Cache можно использовать для произвольного нативного SQL без ограничений | Query Cache работает только с JPQL/Criteria/HQL; native SQL queries обычно не кэшируются (нужен явный `org.hibernate.cacheable` hint и сложная конфигурация). ❌ ПОСЛЕДСТВИЕ: команда добавляет `setHint(HINT_CACHEABLE, true)` к `createNativeQuery(...)` — кэш не работает, hit ratio = 0%, проблема обнаруживается только через метрики, не через ошибки.
> - [x] Query Cache — кэш результатов JPQL/Criteria, хранит `List<ID>` запроса; включается флагом `use_query_cache=true` + хинтом `HINT_CACHEABLE=true` на запрос; требует включённого L2 для подгрузки самих entity | Query Cache решает повторные одинаковые запросы; entity всё равно берутся из L2 — без L2 hit отдаёт ID, но за entity идёт SELECT. ✓ ПРИМЕНЯТЬ: top-N товаров по категории, фильтры в каталоге, повторяющиеся справочные запросы — `@QueryHints({@QueryHint(name="org.hibernate.cacheable", value="true")})`. 📋 ПРАВИЛО: «Query Cache = ID-лист, для entity нужен L2». 🔗 См. Q1, Q3.
> - [ ] Query Cache автоматически включается при `@Cacheable` на entity — отдельная настройка не нужна | `@Cacheable` включает только entity-cache в L2; Query Cache требует отдельного флага `use_query_cache=true` И явного хинта на каждый запрос. ❌ ПОСЛЕДСТВИЕ: разработчик уверен что `findByCategory` кэшируется автоматически, в логах видит SQL на каждый вызов — теряет часы на дебаг, реальная причина — не выставлен `HINT_CACHEABLE`.
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
> - [ ] Query Cache инвалидируется только при изменении конкретной строки, попавшей в результат запроса | Hibernate инвалидирует ВЕСЬ регион Query Cache по entity при любом INSERT/UPDATE/DELETE этой entity — он не знает, какие строки попадали в кэшированный результат. ❌ ПОСЛЕДСТВИЕ: на entity с частыми UPDATE'ами Query Cache инвалидируется на каждом коммите, hit ratio падает до 5-10%, кэш только потребляет память без выгоды.
> - [ ] Query Cache хранит entity-объекты целиком и не зависит от L2 | Query Cache хранит только список идентификаторов; сами entity всегда подтягиваются из L2 — без L2 на каждый hit идёт SELECT. ❌ ПОСЛЕДСТВИЕ: команда включает Query Cache без L2 «для отдельных запросов», ожидает выгоды — на проде SQL load не падает, потому что entity всё равно загружаются по одной.
> - [ ] Query Cache неэффективен для запросов с параметрами — параметризованные запросы не кэшируются | Hibernate кэширует с учётом параметров: один и тот же JPQL с разными значениями параметров создаёт отдельные ключи в кэше. ❌ ПОСЛЕДСТВИЕ: разработчик отказывается от Query Cache «потому что у нас параметризованные запросы», теряет возможность кэшировать частые повторяющиеся фильтры с теми же значениями.
> - [x] Query Cache требует включённого L2 (без него бесполезен) и грубо инвалидируется при любом UPDATE entity, попадающей в запрос — даже если изменена строка, не входящая в результат | Инвалидация идёт по timestamp последнего UPDATE entity; это делает Query Cache подходящим только для редко-меняющихся данных. ✓ ПРИМЕНЯТЬ: каталог товаров с фильтрами; справочные списки городов; топ-N запросов с агрегацией. 📋 ПРАВИЛО: «Query Cache + L2 для редко-меняющихся entity». 🔗 См. Q1, Q8, Q10.
**Важно**: query cache требует включённого L2 cache (хранит только IDs, сами entities подтягивает из L2).

## Q6. Что такое Cache Region и как она работает?

**Region** — именованная логическая область кэша с отдельными настройками (size, TTL, eviction).

```java
// Разные regions для разных entities
@Entity
@Cache(usage = READ_WRITE, region = "users-region")
public class User { ... }


> [!mcq]
> - [x] Регион — именованная логическая область кэша с собственными настройками (`size`, `TTL`, `eviction`); задаётся через `@Cache(region="...")` и конфигурируется в provider'е (`ehcache.xml`) | Регионы позволяют дифференцировать политики: справочники без TTL, hot-данные с малым TTL, BLOB с маленьким size. ✓ ПРИМЕНЯТЬ: `Country` → `countries` с `<expiry><none/></expiry>`; `Product` → `products` с `TTL=15min`; `UserSession` → `sessions`. 📋 ПРАВИЛО: «один регион = одна политика TTL/size». 🔗 См. Q3, Q7.
> - [ ] Все entity делят один общий регион — Hibernate не позволяет настраивать TTL/size per-entity | Каждая entity по умолчанию получает регион с именем класса; через `@Cache(region="...")` можно явно группировать или разделять регионы для индивидуального TTL/size. ❌ ПОСЛЕДСТВИЕ: команда настраивает один TTL=30мин для всех entity, справочник `Country` (immutable) и `Product` (часто меняется) делят регион — `Country` зря инвалидируется при росте `Product`, hit ratio 40% вместо 95%.
> - [ ] Регион применяется только к Query Cache, для entity-кэша регионы не нужны | Регионы применяются ко ВСЕМ типам кэша: entity, collection, query, natural-id; каждый тип может иметь свои регионы. ❌ ПОСЛЕДСТВИЕ: разработчик не указывает регион для `Product`, ожидает что один глобальный кэш с дефолтным TTL=10min подойдёт — на проде entity вытесняются раньше чем нужно, hit ratio низкий.
> - [ ] Имя региона должно совпадать с полным именем класса entity, иначе Hibernate бросит исключение | Имя региона произвольное; класс используется только как default. Можно явно задать `region="countries-region"` или группировать несколько entity в один регион. ❌ ПОСЛЕДСТВИЕ: команда тратит время на «синхронизацию» имён регионов с FQCN классов в `ehcache.xml`, при рефакторинге пакета приходится править XML — лишний overhead на ровном месте.
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
> - [ ] Ehcache — единственный production-ready provider; остальные (Infinispan, Hazelcast) только для тестов | Все перечисленные — production-grade: Infinispan используется в JBoss/WildFly, Hazelcast — в финтехе, Redis (через Redisson) — в Spring-приложениях с distributed L2. ❌ ПОСЛЕДСТВИЕ: команда выбирает Ehcache для кластера из 5 нод «потому что это standard» — каждая нода кэширует свою копию, инвалидации не распространяются, hit ratio падает с ростом кластера, появляются неконсистентности.
> - [x] Ehcache (single-node, JCache мост, простая настройка); Infinispan (native cluster, JBoss/WildFly); Hazelcast (distributed memory grid); Redis через Redisson (shared L2 для горизонтально масштабируемых сервисов); JCache (JSR-107) как unified API | Выбор зависит от топологии: single-node → Ehcache; cluster с в JVM-памяти → Infinispan/Hazelcast; shared memory вне JVM → Redis. ✓ ПРИМЕНЯТЬ: monolith на 1 ноде → Ehcache; Spring-микросервис в k8s с N репликами → Redisson + Redis; WildFly cluster → Infinispan; high-throughput in-memory grid → Hazelcast. 📋 ПРАВИЛО: «топология кластера диктует provider». 🔗 См. Q3, Q10.
> - [ ] Hazelcast и Infinispan — это два названия одного и того же продукта | Hazelcast (Hazelcast Inc.) и Infinispan (Red Hat) — независимые продукты с разными API и возможностями: Infinispan тесно интегрирован с WildFly, Hazelcast имеет более развитый client-server режим. ❌ ПОСЛЕДСТВИЕ: команда меняет provider с Hazelcast на Infinispan «как одно и то же», но конфигурация и serialization форматы разные — на проде сериализация падает, кэш не работает после деплоя.
> - [ ] Redis нельзя использовать как Hibernate L2 cache — только как Spring `@Cacheable` бэкенд | Redis работает как L2 через Redisson (`org.redisson:redisson-hibernate-6X`) — это полноценный provider с поддержкой регионов и concurrency strategies. ❌ ПОСЛЕДСТВИЕ: команда отказывается от shared L2 «потому что Redis не поддерживается», поднимает Hazelcast cluster — лишняя инфраструктура и operational overhead, при том что Redis уже есть в стеке для других нужд.
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
> - [x] Для каждого provider'а — свой `region.factory_class`: `jcache` (JSR-107 мост к Ehcache 3); `org.infinispan.hibernate.cache.v60.InfinispanRegionFactory` (Infinispan native); Hazelcast/Redisson — свои фабрики | Фабрика регионов — точка интеграции Hibernate с конкретным provider'ом; неправильный класс = no-op фабрика и hit ratio = 0%. ✓ ПРИМЕНЯТЬ: Ehcache 3 + Spring Boot → `factory_class=jcache` + JSR-107 provider; кластер на WildFly → InfinispanRegionFactory; Redisson → `org.redisson.hibernate.RedissonRegionFactory`. 📋 ПРАВИЛО: «провайдер — своя factory_class». 🔗 См. Q3, Q6.
> - [ ] `region.factory_class=jcache` достаточно для distributed cache в кластере — Hibernate автоматически синхронизирует ноды | `jcache` — мост к JSR-107, он не делает кэш distributed; для кластера нужен provider с native distribution (Infinispan, Hazelcast, Redis) или Ehcache + Terracotta. ❌ ПОСЛЕДСТВИЕ: команда настраивает `jcache + Ehcache` на 4 нодах, ожидает shared cache — каждая нода кэширует независимо, UPDATE на ноде A не инвалидирует кэш на ноде B → разные пользователи видят разные данные.
> - [ ] `factory_class` опционален: Hibernate сам определяет provider по classpath | Без явного `region.factory_class` Hibernate использует `NoCachingRegionFactory` (no-op) — кэш не работает, но и не ошибочно. ❌ ПОСЛЕДСТВИЕ: разработчик подключает `infinispan-hibernate-cache-v60`, забывает прописать factory_class — на проде кэш молча отключён, hit ratio = 0%, обнаруживается через рост DB load.
> - [ ] InfinispanRegionFactory работает только в JBoss EAP, в standalone Spring Boot не поддерживается | Infinispan можно использовать в Spring Boot standalone — он работает как embedded или client/server. JBoss/WildFly даёт нативную интеграцию, но не является обязательным. ❌ ПОСЛЕДСТВИЕ: команда отказывается от Infinispan «потому что мы не на WildFly», поднимает менее подходящий Hazelcast — потом обнаруживается, что Infinispan был лучшим выбором для их топологии.
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
> - [ ] `session.evict(entity)` инвалидирует entity в L2 — это правильный способ принудительно сбросить кэш | `session.evict()` работает только с L1 (Persistence Context); L2 инвалидируется через `sessionFactory.getCache().evict(...)` или `emf.getCache().evict(...)`. ❌ ПОСЛЕДСТВИЕ: после прямого SQL UPDATE команда вызывает `session.evict(user)` ожидая инвалидации L2 — кэш остаётся stale, последующие запросы из других сессий читают старые данные.
> - [x] L1 чистится через `session.evict(entity)` (один) или `session.clear()` (весь); L2 — через `sessionFactory.getCache().evict(Class, id)`, `evictEntityData(Class)`, `evictAll()`; JPA-стандарт — `emf.getCache().evict(Class, id)` | L1 и L2 — разные scope: L1 на сессию, L2 на фабрику; разные API на каждом уровне. ✓ ПРИМЕНЯТЬ: после bulk JDBC update `UPDATE products SET ...` — `emf.getCache().evictEntityData(Product.class)`; конкретный id — `evict(User.class, 1L)`; полный сброс L2 при release — `evictAll()`. 📋 ПРАВИЛО: «Session API → L1, SessionFactory.Cache → L2». 🔗 См. Q1, Q10.
> - [ ] `cache.evictAll()` чистит и L1 во всех открытых сессиях | `evictAll()` на JPA Cache работает только с L2 (entity и query data); L1 каждой открытой сессии остаётся со своими managed entity. ❌ ПОСЛЕДСТВИЕ: после `evictAll()` команда уверена что «все кэши пусты», но активные транзакции в других потоках продолжают работать со старыми объектами в своих L1 → unexpected stale data в текущих запросах.
> - [ ] Hibernate автоматически инвалидирует L2 после любого SQL-запроса через `JdbcTemplate` | Hibernate ничего не знает про SQL, выполненный через `JdbcTemplate`/`DataSource`; для синхронизации нужно явно вызывать `evict` или использовать SQL-trigger'ы с messaging. ❌ ПОСЛЕДСТВИЕ: команда выполняет bulk update через `JdbcTemplate` для скорости, не делает evict L2 — следующие чтения через JPA отдают старые значения, баг репортится как «иногда после batch-job'а данные неправильные».
```java
// Программно через Session API
Session session = em.unwrap(Session.class);
session.evict(user);                     // один entity из L1
session.clear();                         // весь L1

// SessionFactory — L2 cache
SessionFactory sessionFactory = em.getEntityManagerFactory()
    .unwrap(SessionFactory.class);


> [!mcq]
> - [ ] `evict(Class, id)` и `evictEntityData(Class)` идентичны — оба чистят все entity класса | `evict(Class, id)` чистит ОДИН entity по конкретному id; `evictEntityData(Class)` чистит ВСЕ entity этого класса целиком. ❌ ПОСЛЕДСТВИЕ: после изменения одного `Product` команда вызывает `evictEntityData(Product.class)` — инвалидируется весь регион из 10000 записей, hit ratio падает на 30 минут пока кэш прогревается.
> - [x] Гранулярный `evict(Class, id)` для одной записи; `evictEntityData(Class)` для всего регионa entity; `evictQueryRegions()` для всех Query Cache; `evictAll()` для полного сброса L2 | Чем гранулярнее evict, тем меньше cold-start после batch-операций. ✓ ПРИМЕНЯТЬ: REST endpoint → `evict(Product.class, id)`; ночной batch UPDATE → `evictEntityData(Product.class)`; миграция схемы → `evictAll()`. 📋 ПРАВИЛО: «гранулярный evict минимизирует cold-start». 🔗 См. Q1, Q9.
> - [ ] `evictAll()` нужно вызывать перед каждым SELECT для гарантии fresh data | `evictAll()` сводит на нет смысл L2: все чтения после него идут в БД. Это допустимо только при миграциях или при ручной коррекции данных, не на per-request basis. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `evictAll()` в interceptor «для свежести данных» — кэш всегда пуст, hit ratio = 0%, DB load растёт в 5 раз, SLA на чтение нарушается.
> - [ ] `evictQueryRegions()` инвалидирует entity-кэш и Query Cache одновременно | `evictQueryRegions()` чистит ТОЛЬКО Query Cache (списки ID); entity-кэш остаётся нетронутым — entity всё ещё доступны при `find(Class, id)`. ❌ ПОСЛЕДСТВИЕ: команда ожидает что `evictQueryRegions()` решит проблему stale entity после bulk update — Query Cache очищен, но `findById` отдаёт старые объекты из L2 entity-cache, баг не починен.
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
> - [x] Через Spring рекомендуется работать с `EntityManagerFactory.getCache()` (JPA-стандарт, портируется между provider'ами); для специфичных операций — `unwrap` к `SessionFactory.getCache()` Hibernate API | JPA даёт переносимость, Hibernate — расширенные операции (`evictQueryRegions`, evict natural-id cache). ✓ ПРИМЕНЯТЬ: точечный evict → `emf.getCache().evict(User.class, id)`; batch-job → `unwrap(SessionFactory.class).getCache().evictAllRegions()`. 📋 ПРАВИЛО: «JPA Cache для портируемости, Hibernate Cache для расширенного API». 🔗 См. Q1, Q11.
> - [ ] JPA `Cache` (через `EntityManagerFactory.getCache()`) поддерживает evict только entity целиком, без указания id | JPA `Cache` API имеет `evict(Class, Object id)` — гранулярная инвалидация одного entity по идентификатору; `evict(Class)` — всего класса; `evictAll()` — всего L2. ❌ ПОСЛЕДСТВИЕ: разработчик не использует JPA `Cache.evict(Class, id)` «потому что он только для класса целиком», вынужденно сбрасывает весь регион — лишние cold-start'ы и DB-load.
> - [ ] `EntityManagerFactory.getCache()` чистит Spring `@Cacheable` кэш — Hibernate и Spring используют один и тот же `CacheManager` | `EntityManagerFactory.getCache()` управляет ТОЛЬКО Hibernate L2; Spring `@Cacheable` управляется через `CacheManager.getCache(name).evict(key)` или `@CacheEvict`. ❌ ПОСЛЕДСТВИЕ: после UPDATE команда вызывает `emf.getCache().evict(Product.class, id)` для очистки `@Cacheable("product")` — Spring-кэш не очищен, REST endpoint отдаёт старые данные ещё час до TTL.
> - [ ] Программная инвалидация L2 не нужна — Hibernate автоматически синхронизирует L2 и БД при любом изменении | Hibernate инвалидирует L2 только при изменениях через JPA/Hibernate (persist/merge/remove); прямой SQL через `JdbcTemplate` или `executeUpdate` JPQL обходит инвалидацию — нужен ручной evict. ❌ ПОСЛЕДСТВИЕ: bulk-update через `em.createQuery("UPDATE Product...").executeUpdate()` оставляет L2 stale — другие сессии читают старые цены, заказы оформляются по неактуальным значениям.
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

> [!mcq]
> - [ ] Для оценки эффективности L2-кэша достаточно смотреть на абсолютное число `cacheHitCount` — чем больше, тем лучше | Абсолютное число hits бесполезно без отношения к misses; 1M hits с 10M misses = bad. Нужен hit ratio = hits / (hits + misses). ❌ ПОСЛЕДСТВИЕ: dashboard показывает growth «hits», команда довольна, но реальный hit ratio упал с 80% до 30% из-за роста misses → DB load неожиданно растёт под нагрузкой.
> - [ ] `Statistics` всегда включена в Hibernate по умолчанию — достаточно вызвать `getStatistics()` для получения метрик | По умолчанию отключена для performance reasons; нужен `stats.setStatisticsEnabled(true)` или `hibernate.generate_statistics=true`. ❌ ПОСЛЕДСТВИЕ: Grafana dashboard собирает данные через Micrometer/Actuator, видит нули — выглядит как «всё нормально», на деле метрики просто не собираются, реальные cache misses скрыты.
> - [x] Hit ratio (>80% — хорошо, <50% — кэш не нужен), evictions/sec (рост = маленький кэш), per-entity hit count, query cache hit ratio, размер регионов в памяти | Эти метрики позволяют отличить эффективный кэш от cargo-cult кэширования; per-entity показывает какие именно entity выгодны. ✓ ПРИМЕНЯТЬ: Spring Boot Actuator + Micrometer + Prometheus экспортируют `hibernate.cache.region.*`; Grafana dashboard с alert при hit ratio < 60%; per-entity tracking для решения «убрать ли кэш с сущности». 📋 ПРАВИЛО: «Hit ratio + evictions + per-entity = достаточно для решения». 🔗 См. Q3 (настройка), Q12 (когда НЕ кэшировать).
> - [ ] Главная метрика — суммарный размер кэша в байтах; если он стабилен — кэш работает корректно | Размер сам по себе не говорит об эффективности; кэш может быть полным, но всё равно с низким hit ratio (если кэшируются «не те» данные). ❌ ПОСЛЕДСТВИЕ: команда выделяет 4GB на L2-кэш, мониторит размер, но не hit ratio — оказывается hot-data 100MB, а 3.9GB — холодные сущности, которые DB достаёт быстрее чем cache lookup.

## Q10. Какие проблемы/pitfalls при работе с L2 cache?

1. **Неконсистентность при модификации через прямой SQL** (не через Hibernate):

```java
// Hibernate не знает об этом UPDATE → L2 cache устарел
jdbcTemplate.update("UPDATE users SET name = ? WHERE id = ?", "NewName", 1L);

// Решение: ручная инвалидация
sessionFactory.getCache().evict(User.class, 1L);
```


> [!mcq]
> - [ ] Прямой SQL через `JdbcTemplate` автоматически инвалидирует L2 — Hibernate перехватывает изменения через DataSource | Hibernate не знает о SQL, выполненном в обход его API; L2 остаётся stale до явного `evict` или TTL. ❌ ПОСЛЕДСТВИЕ: ночной batch через `JdbcTemplate.update("UPDATE products SET price=...")` не вызывает evict L2 — на следующее утро пользователи видят старые цены до 30 минут пока кэш не протухнет.
> - [x] Прямой SQL/JDBC и `executeUpdate()` обходят Hibernate — нужен явный `sessionFactory.getCache().evict(...)` или стратегия инвалидации через события для синхронизации между нодами | Hibernate отслеживает изменения только через свой API (persist/merge/remove); прямой SQL требует ручной инвалидации. ✓ ПРИМЕНЯТЬ: после bulk-update через JDBC → `evictEntityData(Product.class)`; в кластере → publish в Kafka + evict на каждой ноде, или debezium CDC. 📋 ПРАВИЛО: «прямой SQL → ручной evict + кластерный broadcast». 🔗 См. Q4, Q8.
> - [ ] `READ_ONLY` стратегия защищает от stale данных при прямом SQL — Hibernate отслеживает все DML на уровне БД | `READ_ONLY` запрещает изменения через Hibernate, но никак не отслеживает прямой SQL — entity в кэше остаются с устаревшими значениями до перезапуска. ❌ ПОСЛЕДСТВИЕ: команда ставит `READ_ONLY` на справочник, считает что «всё под контролем», админка использует JDBC для редкого UPDATE — на проде entity в L2 расходятся с БД на дни, расчёты идут по старым ставкам.
> - [ ] Достаточно установить TTL=10мин — за это время кэш сам синхронизируется с БД | TTL даёт окно stale-данных длиной до TTL; для финансовых/критичных операций это неприемлемо. Кроме того, TTL не покрывает кросс-нодовые сценарии в кластере. ❌ ПОСЛЕДСТВИЕ: на `Account` балансе ставят TTL=10мин — после списания через JDBC другая нода до 10 минут видит старый баланс, разрешает повторное списание → отрицательный баланс, financial bug.
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

> [!mcq]
> - [ ] Hibernate L2 и Spring `@Cacheable` — это один и тот же механизм; включение обоих удваивает hit ratio | Это два независимых уровня; включение обоих для одной сущности часто приводит к двойной инвалидации, неконсистентности и сложности отладки. ❌ ПОСЛЕДСТВИЕ: после `update` сущности Spring-кэш ещё держит старый DTO, Hibernate L2 уже обновлён → разные API возвращают разное состояние одной сущности.
> - [x] Hibernate L2 кэширует entity на уровне ORM с автоматической инвалидацией; Spring `@Cacheable` кэширует результат метода (часто DTO) на уровне приложения с ручной инвалидацией через `@CacheEvict` | Каждый механизм решает свою задачу: L2 — частые `findById`/lazy-загрузки; `@Cacheable` — расчёты, агрегации, REST-ответы. ✓ ПРИМЕНЯТЬ: L2 для `Product` (часто читается по id, инвалидация через `@CacheEvict` не нужна — сам ORM); `@Cacheable("topProducts")` для top-10 запроса с агрегацией. 📋 ПРАВИЛО: «L2 = entities, @Cacheable = вычисления/DTO». 🔗 См. Q1 (уровни), Q5 (Query Cache), Q14 (N+1).
> - [ ] `@Cacheable` инвалидируется автоматически при изменении entity через Hibernate | `@Cacheable` ничего не знает про Hibernate ORM; нужен явный `@CacheEvict` или TTL в провайдере. ❌ ПОСЛЕДСТВИЕ: цена товара обновлена в БД через JPA-репозиторий, но `@Cacheable("product")` отдаёт старую цену 30 минут до TTL → клиенты заказывают по неактуальной цене, потери на каждом заказе.
> - [ ] Spring `@Cacheable` не работает с Hibernate-сущностями — только с примитивами и String | `@Cacheable` сериализует любые объекты, включая JPA-entity (если provider — Redis с Jackson или сериализуемый кэш). Проблема — детали: lazy-связи, proxy-объекты, dirty checking. ❌ ПОСЛЕДСТВИЕ: команда отказывается от `@Cacheable` для DTO «потому что не работает с entity», теряет возможность кэшировать вычисления.

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

> [!mcq]
> - [ ] L2 cache всегда оправдан, если есть свободная RAM — даже для часто обновляемых entity | Часто меняющиеся entity дают высокий процент инвалидаций; кэш постоянно сбрасывается, hit ratio низкий, overhead на синхронизацию между нодами кластера часто выше выгоды. ❌ ПОСЛЕДСТВИЕ: L2 на TodoItem с update раз в секунду — кэш инвалидируется быстрее чем заполняется, hit ratio 5%, JVM heap забит «мертвыми» entries, GC паузы растут.
> - [ ] L2 особенно эффективен для append-only данных (audit logs) — каждая запись уникальна, кэш избегает повторного чтения | Append-only данные читаются только раз (или вообще не читаются после записи) — кэш бесполезен, новый объект каждый раз = 100% miss. ❌ ПОСЛЕДСТВИЕ: L2 на `AuditLog` забивает память миллионом entry, hit ratio близок к 0%, реальные часто читаемые entity вытесняются → DB load растёт.
> - [ ] L2 — единственный способ ускорить single-instance приложение без повторных запросов | Для single-instance без повторных читов L1 (Persistence Context) уже даёт всё необходимое в рамках одной транзакции; L2 добавляет overhead без выгоды. ❌ ПОСЛЕДСТВИЕ: команда включает L2 на standalone batch-job который обрабатывает каждую запись один раз → дополнительный CPU/memory overhead, время выполнения растёт на 10-15%.
> - [x] НЕ кэшировать: часто обновляемые entity (инвалидации убивают выгоду), append-only данные (нет повторов), большие BLOB (память кэша), single-instance без repeat-reads (L1 достаточно), strict consistency (L2 допускает рассинхронизацию) | Кэширование — trade-off; неподходящий профиль доступа делает L2 чистой стоимостью без выгоды. ✓ ПРИМЕНЯТЬ: hot entity с read >> write (Product, User profile) — L2 ON; transactional entity (Order, Payment) с частыми updates и strict consistency — L2 OFF. 📋 ПРАВИЛО: «read >> write + tolerate stale → L2; иначе → нет». 🔗 См. Q4 (concurrency), Q9 (метрики).

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

> [!mcq]
> - [ ] `@NaturalId` автоматически создаёт уникальный индекс в БД при `ddl-auto=update` | `@NaturalId` — Hibernate-аннотация для cache lookup'а; уникальный индекс нужно явно создавать через `@Column(unique = true)` или DDL-миграцией. ❌ ПОСЛЕДСТВИЕ: команда полагается на `@NaturalId` для уникальности email — DB позволяет дубликаты, в production два пользователя регистрируются на один email → race condition, поломанная авторизация.
> - [ ] Natural id cache — это синоним L1 cache, работает только в рамках одной транзакции | Natural id cache — это часть L2 (отдельный регион); работает между транзакциями и (при кластере) между нодами. ❌ ПОСЛЕДСТВИЕ: разработчик считает что natural id cache «уйдёт» после commit, не настраивает eviction → memory leak при росте числа уникальных значений (миллионы email).
> - [x] Natural id cache — отдельный регион L2 для lookup по бизнес-уникальному полю (email, username); включается через `@NaturalIdCache` + `@NaturalId`; запрос через `bySimpleNaturalId().load()` | Полезен для частых lookup'ов по бизнес-ключу — логин, поиск по slug; без cache такой lookup всегда идёт в DB через index. ✓ ПРИМЕНЯТЬ: User lookup при login (по email); Product lookup по SKU/article; Order lookup по уникальному номеру. 📋 ПРАВИЛО: «частый lookup по бизнес-ключу → @NaturalIdCache». 🔗 См. Q3 (настройка L2), Q6 (cache regions).

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

> [!mcq]
> - [ ] L2 cache всегда решает N+1 проблему — даже на первом запросе списка | На первой итерации цикла L2 пуст; происходят те же N лишних запросов, и только на повторных вызовах становится hit. ❌ ПОСЛЕДСТВИЕ: команда включает L2 «чтобы убрать N+1», в production первый запрос после рестарта по-прежнему делает 100 SQL → cold-start latency не улучшается, пока кэш не прогреется.
> - [ ] `JOIN FETCH` и L2 cache — взаимозаменяемые решения N+1; всегда нужно выбирать одно | Это complementary решения: JOIN FETCH — одномоментно (избегает N+1 в первом запросе); L2 — для повторных доступов между транзакциями. Часто используются вместе. ❌ ПОСЛЕДСТВИЕ: используют только L2, на первом запросе всё равно N+1 → p99 latency высокая после рестарта; используют только JOIN FETCH — повторное чтение всё ещё бьёт в БД.
> - [x] Решения N+1: `JOIN FETCH` (загрузка одним SQL), `@BatchSize` (пачками), L2 cache (повторные чтения), `@EntityGraph` для динамической fetch-стратегии — комбинируются по сценарию | Каждое решение оптимально для своего профиля доступа: одномоментный list → JOIN FETCH; повторные lookup'ы → L2; неизвестный размер коллекции → BatchSize. ✓ ПРИМЕНЯТЬ: list endpoint с pagination → JOIN FETCH; навигация по lazy-связям в DTO mapper → @BatchSize(20); справочные сущности (Country, Currency) → L2 READ_ONLY. 📋 ПРАВИЛО: «N+1 решает FETCH/BatchSize, повторное чтение решает L2». 🔗 См. Q1 (уровни), Q4 (concurrency), Q6 (regions).
> - [ ] `@BatchSize(size=N)` загружает N сущностей одним SQL вместо N отдельных запросов; работает только с L2 | `@BatchSize` — Hibernate-механизм fetching, работает БЕЗ L2; он создаёт `WHERE id IN (?, ?, ...)` для пачки lazy-загружаемых ассоциаций. ❌ ПОСЛЕДСТВИЕ: разработчик включает только L2, не использует `@BatchSize`, удивляется что N+1 остаётся для первой загрузки.

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

> [!mcq]
> - [ ] Достаточно настроить L2 cache по best practices от vendor'а провайдера — измерять hit ratio в production не обязательно | Без измерений нельзя понять, помогает ли кэш или только потребляет память. Best practices от vendor'а — отправная точка, не финальная конфигурация. ❌ ПОСЛЕДСТВИЕ: команда полагается на «default настройки EhCache», hit ratio 30%, проблему замечают только когда DB начинает throttle'ить под нагрузкой и происходит outage.
> - [ ] Кэшировать всё, у чего есть `@Id` — таким образом приложение всегда быстрее работает | Кэширование append-only данных и часто меняющихся entity вредит: память тратится на «мёртвые» записи, инвалидации забивают сеть в кластере. Selective caching > blanket caching. ❌ ПОСЛЕДСТВИЕ: команда включает L2 для всех 200 entity, heap прыгает на 4GB, GC паузы растут, hit ratio в среднем 15% — кэш делает только хуже.
> - [x] Best practices: всегда измерять (Statistics + Actuator + Prometheus); справочники → READ_ONLY; настраивать TTL и max size; для кластеров — distributed cache; не кэшировать транзакционные/append-only/BLOB; query cache требует L2 | Эти правила минимизируют риск антипаттернов и связывают L2 с observability. ✓ ПРИМЕНЯТЬ: Country/Currency → @Cache(READ_ONLY); Order/Payment → НЕ кэшировать; кластер из 3 нод → Hazelcast/Infinispan distributed L2; alert на hit ratio < 60% в Grafana. 📋 ПРАВИЛО: «измеряй → справочники READ_ONLY → транзакционные не кэшируй». 🔗 См. Q9 (метрики), Q10 (pitfalls), Q12 (когда не кэшировать).
> - [ ] Query cache можно включать независимо от L2 — это даёт buffer для частых запросов | Query cache хранит только список ID результата запроса; сами entity берёт из L2; без L2 query cache бесполезен и приводит к ConcurrentModificationException-подобным ошибкам. ❌ ПОСЛЕДСТВИЕ: разработчик включает `hibernate.cache.use_query_cache=true` без `use_second_level_cache`, query cache работает «вхолостую»; на продакшне query latency не улучшается, в логах warning'и о cache misses.

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
