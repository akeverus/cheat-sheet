---
title: "Hibernate: кэширование (L1, L2, Query Cache)"
description: "Первый и второй уровни кэша Hibernate, Query Cache, провайдеры EHCache и Redis, регионы, стратегии вытеснения и практические советы по настройке."
tags:
  - databases
  - orm
  - hibernate
  - jpa
  - caching
  - ehcache
  - redis
  - performance
difficulty: "advanced"
updated: "2026-04-20"
---
# Hibernate: кэширование (L1, L2, Query Cache)

Кэш в Hibernate — мощный инструмент для снижения нагрузки на БД. Без понимания архитектуры он превращается в источник baggy-данных и трудноуловимых багов. Шпаргалка разбирает все три уровня: Session-кэш, second-level cache и Query Cache.

## Полезные ссылки

### Официальная документация
- [Hibernate Caching (hibernate.org)](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#caching) — официальная документация

### См. также
- [[orm-basics|ORM Basics]] — базовые концепции JPA и Hibernate
- [[hibernate-relationships|Hibernate Relationships]] — связи между сущностями
- [[hibernate-interview|Hibernate Interview]] — типовые вопросы на собеседовании
- [[hibernate-jpql-criteria|Hibernate JPQL/Criteria]] — запросы в Hibernate и JPA

## Содержание

- [Обзор архитектуры кэша](#обзор-архитектуры-кэша)
- [L1 — First Level Cache](#l1-first-level-cache)
- [L2 — Second Level Cache](#l2-second-level-cache)
- [Query Cache](#query-cache)
- [Провайдеры: EHCache](#провайдеры-ehcache)
- [Провайдеры: Redis (Redisson)](#провайдеры-redis-redisson)
- [Регионы кэша](#регионы-кэша)
- [Стратегии конкурентного доступа](#стратегии-конкурентного-доступа)
- [Вытеснение и инвалидация](#вытеснение-и-инвалидация)
- [Практические советы](#практические-советы)
- [Типичные ошибки](#типичные-ошибки)
- [См. также](#см-также-1)

## Обзор архитектуры кэша

Hibernate поддерживает три уровня кэширования:

| Уровень | Область видимости | Хранилище | Управление |
|---------|-------------------|-----------|------------|
| L1 (Session) | В рамках одной `Session` | Heap JVM | Автоматически |
| L2 (SessionFactory) | Все сессии одного приложения | Внешнее (EHCache, Redis…) | Явная настройка |
| Query Cache | Результаты JPQL/HQL-запросов | Внешнее | Явная настройка |

- **L1** всегда включён, отключить нельзя.
- **L2** требует подключения провайдера и явной аннотации `@Cache` на сущности.
- **Query Cache** работает только совместно с L2.

---

## L1 — First Level Cache

**Что хранит:** все сущности, загруженные или сохранённые в рамках текущей `Session` / `EntityManager`.

**Как работает:**
- При первом `session.get(User.class, 1L)` Hibernate идёт в БД.
- При повторном вызове с тем же ID — возвращает объект из кэша без SQL.
- `session.clear()` — очищает весь L1.
- `session.evict(entity)` — удаляет конкретный объект.

```java
Session session = sessionFactory.openSession();

User user1 = session.get(User.class, 1L); // SQL: SELECT ...
User user2 = session.get(User.class, 1L); // Из L1, SQL нет
System.out.println(user1 == user2); // true — один и тот же объект

session.evict(user1);
User user3 = session.get(User.class, 1L); // SQL снова: SELECT ...
```

**Важные нюансы:**
- L1 — это Identity Map: гарантирует уникальность объекта по ID в рамках сессии.
- JPQL-запрос (`session.createQuery(...)`) всегда идёт в БД, но результат попадает в L1.
- Длинные batch-операции стоит периодически делать `session.flush()` + `session.clear()`, иначе L1 растёт в памяти.

```java
// Пример batch insert без утечки памяти
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

for (int i = 0; i < 100_000; i++) {
    session.persist(new Product("item-" + i));
    if (i % 50 == 0) {
        session.flush();   // сброс в БД
        session.clear();   // очистка L1
    }
}
tx.commit();
session.close();
```

---

## L2 — Second Level Cache

**Что хранит:** сущности и их коллекции в десериализованном виде (CacheEntry), разделяемые между всеми `Session`.

**Подключение через Spring Boot (EHCache 3):**

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <classifier>jakarta</classifier>
</dependency>
```

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.internal.JCacheRegionFactory
          use_query_cache: false   # включить отдельно, если нужен Query Cache
        javax:
          cache:
            provider: org.ehcache.jsr107.EhcacheCachingProvider
            uri: classpath:ehcache.xml
```

**Аннотация на сущности:**

```java
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // коллекцию тоже нужно аннотировать отдельно
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Tag> tags;
}
```

**Проверка попадания в L2:**

```java
// Сессия 1
session1.get(Product.class, 42L); // SQL: SELECT

// Сессия 2 — другая транзакция
session2.get(Product.class, 42L); // Из L2, SQL нет
```

---

## Query Cache

Кэширует **список идентификаторов**, возвращённых запросом, но не сами данные — для данных всё равно нужен L2.

**Включение:**

```yaml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_query_cache: true
```

**Использование в коде:**

```java
// JPQL с Query Cache
List<Product> products = session
    .createQuery("FROM Product p WHERE p.active = true", Product.class)
    .setCacheable(true)                    // включить кэш для запроса
    .setCacheRegion("activeProducts")      // опционально: именованный регион
    .getResultList();
```

**Через Spring Data JPA:**

```java
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Product> findByActiveTrue();
}
```

**Инвалидация Query Cache:**
- Автоматически при изменении любой сущности нужного типа (INSERT/UPDATE/DELETE).
- Регион `default-query-results-region` содержит все некатегоризированные запросы.

**Когда Query Cache полезен:**
- Справочники: список стран, категорий, статусов — обновляются редко.
- **Не подходит** для запросов с параметрами пользователя — каждый уникальный набор параметров порождает новую запись.

---

## Провайдеры: EHCache

EHCache — наиболее популярный провайдер для монолитных приложений.

```xml
<!-- ehcache.xml (src/main/resources) -->
<config xmlns="http://www.ehcache.org/v3">

    <!-- Дефолтный шаблон для всех регионов без явного описания -->
    <cache-template name="default">
        <expiry>
            <ttl unit="minutes">10</ttl>
        </expiry>
        <resources>
            <heap unit="entries">1000</heap>
            <offheap unit="MB">50</offheap>
        </resources>
    </cache-template>

    <!-- Регион для сущности Product -->
    <cache alias="com.example.domain.Product" uses-template="default">
        <expiry>
            <ttl unit="hours">1</ttl>
        </expiry>
        <resources>
            <heap unit="entries">5000</heap>
        </resources>
    </cache>

    <!-- Query Cache регион -->
    <cache alias="activeProducts">
        <expiry>
            <ttl unit="minutes">5</ttl>
        </expiry>
        <resources>
            <heap unit="entries">200</heap>
        </resources>
    </cache>

</config>
```

**Типы хранилищ EHCache:**
- `heap` — Java heap (быстро, ограничено, GC-давление).
- `offheap` — вне heap (медленнее, нет GC, нужна сериализация).
- `disk` — диск (очень медленно, для больших объёмов).

---

## Провайдеры: Redis (Redisson)

Используется в кластерных/микросервисных приложениях, где L2 нужен共享 между несколькими узлами.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-hibernate-6</artifactId>
    <version>3.27.0</version>
</dependency>
```

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.redisson.hibernate.RedissonRegionFactory
        redisson:
          config: classpath:redisson.yml
```

```yaml
# redisson.yml
singleServerConfig:
  address: "redis://localhost:6379"
  connectionPoolSize: 10
  connectionMinimumIdleSize: 2
```

**Настройка TTL через аннотацию:**

```java
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "productRegion")
// Redisson позволяет задать TTL через конфиг региона в redisson.yml
public class Product { ... }
```

```yaml
# redisson.yml — конфиг регионов
hibernateRegionsConfig:
  productRegion:
    ttl: 3600000    # 1 час в миллисекундах
    maxIdleTime: 600000
    maxSize: 10000
  default-update-timestamps-region:
    ttl: 0          # вечно (не трогать!)
    maxIdleTime: 0
```

**Когда Redis лучше EHCache:**
- Несколько инстансов приложения — кэш общий.
- Нужна гибкая инвалидация из внешних систем.
- Уже используется Redis в стеке.

---

## Регионы кэша

Регион — именованное пространство внутри L2. Каждая сущность живёт в своём регионе.

**Дефолтные имена регионов:**
- Сущность: полное имя класса (`com.example.domain.Product`).
- Коллекция: `com.example.domain.Product.tags`.
- Query Cache: `default-query-results-region`.
- Timestamps: `default-update-timestamps-region` — **никогда не устанавливайте TTL на него**.

**Явное указание региона:**

```java
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "products")
public class Product { ... }
```

**Программная инвалидация региона:**

```java
// Через Hibernate SessionFactory
Cache cache = entityManagerFactory.unwrap(SessionFactory.class).getCache();
cache.evictEntityData(Product.class);          // все записи сущности
cache.evictEntityData(Product.class, 42L);     // одна запись
cache.evictCollectionData(Product.class.getName() + ".tags", 42L);
cache.evictQueryRegion("activeProducts");       // Query Cache регион
cache.evictAllRegions();                        // ядерный вариант
```

---

## Стратегии конкурентного доступа

`CacheConcurrencyStrategy` определяет, как L2 обрабатывает параллельные транзакции.

| Стратегия | Запись | Чтение | Когда использовать |
|-----------|--------|--------|---------------------|
| `READ_ONLY` | Нет | Без блокировки | Справочники, константы |
| `NONSTRICT_READ_WRITE` | Возможна race condition | Без блокировки | Допустима кратковременная устаревшесть |
| `READ_WRITE` | Soft lock | Без блокировки | Изменяемые сущности, строгая консистентность |
| `TRANSACTIONAL` | XA-транзакция | Транзакционное | JTA + XA, редко используется |

**Практический выбор:**
- Справочники (страны, категории) → `READ_ONLY`.
- Продукты, пользователи → `READ_WRITE`.
- `TRANSACTIONAL` — только если есть JTA-менеджер транзакций.

```java
// READ_ONLY — самый быстрый
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
public class Country { ... }

// READ_WRITE — с soft lock при обновлении
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Order { ... }
```

---

## Вытеснение и инвалидация

**Автоматическая инвалидация:**
- При UPDATE/DELETE сущности Hibernate автоматически удаляет её из L2.
- При операциях через JPQL (`UPDATE Product SET ...`) — инвалидируется весь регион.
- Нативные SQL-запросы (`session.createNativeQuery(...)`) Hibernate не отслеживает — нужна ручная инвалидация.

```java
// Нативный SQL — L2 не знает об изменениях
session.createNativeQuery("UPDATE products SET price = 0")
       .addSynchronizedEntityClass(Product.class) // подсказка Hibernate
       .executeUpdate();
```

**Ручная инвалидация через Spring:**

```java
@Service
public class ProductService {

    @PersistenceContext
    private EntityManager em;

    public void bulkPriceUpdate() {
        em.createQuery("UPDATE Product p SET p.price = p.price * 1.1")
          .executeUpdate();
        // L2 для Product инвалидирован автоматически при JPQL
    }

    public void evictProduct(Long id) {
        em.getEntityManagerFactory()
          .getCache()
          .evict(Product.class, id);
    }
}
```

**TTL-стратегии:**
- **TTL (Time-To-Live):** запись вытесняется через N секунд после создания.
- **TTI (Time-To-Idle):** вытесняется через N секунд после последнего доступа.
- **Max entries:** при переполнении вытесняются LRU/LFU-записи.

---

## Практические советы

**Что кэшировать:**
- Справочники с редкими изменениями: страны, категории, конфигурации.
- Тяжёлые сущности с большим количеством JOIN-ов.
- Коллекции, которые всегда загружаются вместе с родителем.

**Что не кэшировать:**
- Сущности с частыми обновлениями (заказы в реальном времени).
- Большие списки с уникальными параметрами фильтрации.
- Сущности с чувствительными данными (пароли, токены).

**Мониторинг статистики:**

```java
// Включение статистики в application.yml
// spring.jpa.properties.hibernate.generate_statistics=true

Statistics stats = entityManagerFactory
    .unwrap(SessionFactory.class)
    .getStatistics();

System.out.println("L2 hit ratio: " + stats.getSecondLevelCacheHitCount()
    + "/" + (stats.getSecondLevelCacheHitCount() + stats.getSecondLevelCacheMissCount()));
System.out.println("Query cache hits: " + stats.getQueryCacheHitCount());
```

**Через Spring Boot Actuator:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: caches
```

Эндпоинт `/actuator/caches` показывает список регионов и их состояние.

**Тюнинг производительности:**
- Начинайте с `READ_ONLY` для справочников — минимальные накладные расходы.
- `READ_WRITE` + offheap EHCache — хороший баланс для изменяемых данных.
- Redis полезен только при горизонтальном масштабировании.
- Не устанавливайте слишком большой TTL для часто изменяемых данных.

---

## Типичные ошибки

**Ошибка 1: кэшировать без L2-провайдера**

```yaml
# НЕПРАВИЛЬНО — аннотация @Cache без провайдера просто игнорируется
spring.jpa.properties.hibernate.cache.use_second_level_cache: false
```

Hibernate молча не кэширует. Убедитесь, что `use_second_level_cache: true` и провайдер подключён.

**Ошибка 2: TTL на timestamps-регионе**

Если выставить TTL на `default-update-timestamps-region`, Query Cache начнёт возвращать устаревшие данные после истечения TTL — Hibernate не сможет понять, были ли изменения.

**Ошибка 3: нативные SQL без синхронизации**

```java
// L2 не узнает об этом обновлении
session.createNativeQuery("DELETE FROM products WHERE active = false")
       .executeUpdate(); // данные в L2 останутся "живыми"

// Правильно: добавить синхронизацию или очистить вручную
session.createNativeQuery("DELETE FROM products WHERE active = false")
       .addSynchronizedEntityClass(Product.class)
       .executeUpdate();
```

**Ошибка 4: Query Cache без L2**

Query Cache кэширует только идентификаторы. Сами объекты подтягиваются из L2. Без L2 каждый закэшированный запрос всё равно пойдёт в БД за данными.

**Ошибка 5: EAGER + L2 = двойной удар**

`FetchType.EAGER` загружает связи сразу. Если связи не включены в L2, каждое попадание в L2 для родителя тянет JOIN к БД для дочерних. Либо аннотируйте коллекции тоже, либо используйте LAZY.

---

## См. также

- [[orm-basics|ORM Basics]] — базовые концепции JPA и Hibernate
- [[hibernate-relationships|Hibernate Relationships]] — связи и стратегии загрузки
- [[hibernate-interview|Hibernate Interview]] — типовые вопросы на собеседовании
- [[hibernate-jpql-criteria|Hibernate JPQL/Criteria]] — запросы в Hibernate и JPA
- [[spring-data-jpa-interview|Spring Data JPA]] — репозитории и транзакции в Spring
- [[database-transactions-interview|Database Transactions]] — изоляция и уровни транзакций
- [[java-jdbc|JDBC]] — низкоуровневая работа с БД
