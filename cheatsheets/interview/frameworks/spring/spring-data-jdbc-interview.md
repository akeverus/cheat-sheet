---
title: "Вопросы на собеседовании: Spring Data JDBC"
description: "Spring Data JDBC: aggregate root, @MappedCollection, AggregateReference, отличия от JPA, @Query, @Version"
tags:
  - interview
  - frameworks
  - spring-data-jdbc-interview
aliases:
  - "Spring Data JDBC interview"
  - "Spring Data JDBC собеседование"
  - "Spring Data JDBC вопросы"
difficulty: "intermediate"
updated: "2026-04-20"
---
# Вопросы на собеседовании: `Spring Data JDBC`

`Spring Data JDBC` — простой persistence-фреймворк без ORM-магии: нет lazy loading, нет first-level cache, нет dirty checking. Опирается на концепции DDD (aggregate root). На собеседованиях проверяют понимание отличий от JPA, модели агрегатов и работы с кастомными запросами.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Data JDBC Reference](https://docs.spring.io/spring-data/relational/reference/jdbc.html) — официальная документация
- [Baeldung: Spring Data JDBC Intro](https://www.baeldung.com/spring-data-jdbc-intro) — введение с примерами

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и отличия от JPA**
- [Q1. (!) Что такое Spring Data JDBC и чем отличается от Spring Data JPA?](#q1-что-такое-spring-data-jdbc-и-чем-отличается-от-spring-data-jpa)
- [Q2. Что означает "нет lazy loading" на практике?](#q2-что-означает-нет-lazy-loading-на-практике)
- [Q3. Когда выбирать Spring Data JDBC вместо JPA?](#q3-когда-выбирать-spring-data-jdbc-вместо-jpa)

**Модель агрегатов**
- [Q4. (!) Что такое Aggregate Root в Spring Data JDBC?](#q4-что-такое-aggregate-root-в-spring-data-jdbc)
- [Q5. Как маппировать связь один-к-одному?](#q5-как-маппировать-связь-один-к-одному)
- [Q6. (!) Как маппировать связь один-ко-многим через @MappedCollection?](#q6-как-маппировать-связь-один-ко-многим-через-mappedcollection)
- [Q7. Что такое AggregateReference и когда его использовать?](#q7-что-такое-aggregatereference-и-когда-его-использовать)
- [Q8. Как работает @Embedded?](#q8-как-работает-embedded)

**Маппинг и аннотации**
- [Q9. Какие основные аннотации Spring Data JDBC?](#q9-какие-основные-аннотации-spring-data-jdbc)
- [Q10. Как работает именование таблиц и колонок по умолчанию?](#q10-как-работает-именование-таблиц-и-колонок-по-умолчанию)
- [Q11. Как создать иммутабельную сущность?](#q11-как-создать-иммутабельную-сущность)
- [Q12. Как работает оптимистичная блокировка через @Version?](#q12-как-работает-оптимистичная-блокировка-через-version)

**Репозитории и запросы**
- [Q13. Какие репозитории использует Spring Data JDBC?](#q13-какие-репозитории-использует-spring-data-jdbc)
- [Q14. (!) Как написать кастомный SQL-запрос в Spring Data JDBC?](#q14-как-написать-кастомный-sql-запрос-в-spring-data-jdbc)
- [Q15. Как выполнять modifying-запросы?](#q15-как-выполнять-modifying-запросы)
- [Q16. Как работает транзакционность в Spring Data JDBC?](#q16-как-работает-транзакционность-в-spring-data-jdbc)

---

## Q1. (!) Что такое Spring Data JDBC и чем отличается от Spring Data JPA?

`Spring Data JDBC` — persistence-фреймворк с прямым SQL-доступом без ORM. Реализует репозиторный подход Spring Data, но без Hibernate под капотом.

| Аспект | Spring Data JDBC | Spring Data JPA (Hibernate) |
|---|---|---|
| ORM | Нет | Полный ORM |
| Lazy loading | Нет | Есть |
| First-level cache | Нет | Есть (Session) |
| Dirty checking | Нет | Есть |
| JPQL / HQL | Нет — только SQL | Есть |
| Схема БД | Создаётся вручную | Может генерировать |
| Производительность | Выше для простых операций | Накладные расходы ORM |
| Сложность | Ниже | Выше (N+1, LazyInit и др.) |
| DDD-совместимость | Агрегаты first-class | Сущности, не агрегаты |

**Ключевая фраза на собеседовании:** Spring Data JDBC — "what you see is what you get": нет скрытых SQL-запросов, нет неожиданного lazy loading, нет магии.

---

## Q2. Что означает "нет lazy loading" на практике?

В JPA связанные сущности загружаются лениво — первый доступ к коллекции выполняет SELECT. В Spring Data JDBC при загрузке агрегата **все дочерние объекты загружаются сразу** (eager, через JOIN или дополнительные SELECT).

**Следствия:**
- Нет `LazyInitializationException` (проблема JPA вне транзакции)
- Нет N+1 problem в неожиданных местах — запросы явные
- Большие агрегаты с коллекциями могут быть дорогими — нужно проектировать агрегаты компактными
- Нет `@FetchType` — вся загрузка всегда eager в пределах агрегата

```java
// При findById загружается ВСЯ структура агрегата:
Order order = orderRepository.findById(id).orElseThrow();
// order.getItems() — уже загружен, никакого SELECT
```

---

## Q3. Когда выбирать Spring Data JDBC вместо JPA?

**Выбирайте Spring Data JDBC если:**
- Простая доменная модель с небольшими агрегатами
- Хотите предсказуемые SQL-запросы без ORM-магии
- Проект следует DDD с чёткими границами агрегатов
- Нужна высокая производительность на CRUD-операциях
- Команда хочет избежать сложностей Hibernate (N+1, LazyInit, кэш)

**Оставайтесь на JPA если:**
- Сложные графы сущностей с необходимостью lazy loading
- Нужна генерация схемы или schema migration через Hibernate
- Используете Criteria API или сложные JPQL-запросы
- Нужен L2 cache (Hibernate second-level cache)
- Уже есть большой проект на Hibernate

**Итог:** Spring Data JDBC — отличный выбор для новых микросервисов с чистой DDD-архитектурой. JPA — для legacy-систем или сложных объектных графов.

---

## Q4. (!) Что такое Aggregate Root в Spring Data JDBC?

**Aggregate Root** — центральная сущность агрегата, единственная точка входа для сохранения и извлечения данных. Только у агрегатного корня есть репозиторий.

```mermaid
graph TD
    AR[Order — Aggregate Root] --> OI1[OrderItem 1]
    AR --> OI2[OrderItem 2]
    AR --> A[DeliveryAddress]
    
    style AR fill:#4CAF50,color:white
```

```java
@Table("orders")
public class Order {        // Aggregate Root
    @Id
    private Long id;
    private String status;
    
    @MappedCollection(idColumn = "order_id")
    private Set<OrderItem> items;  // часть агрегата
    
    private DeliveryAddress address;  // @Embedded
}
```

**Правила агрегата:**
- Весь агрегат сохраняется/удаляется через корень (`orderRepository.save(order)`)
- Нет отдельного `OrderItemRepository` — они управляются только через `Order`
- При `deleteById` все `OrderItem` удаляются каскадно
- Ссылки на другие агрегаты — через `AggregateReference` (не загружает объект)

---

## Q5. Как маппировать связь один-к-одному?

В Spring Data JDBC один-к-одному реализуется через **вложенный объект** в той же таблице (`@Embedded`) или через отдельную таблицу с прямой ссылкой.

**Вариант 1 — @Embedded (в одной таблице):**
```java
@Table("persons")
public class Person {
    @Id Long id;
    String name;
    
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    Address address;  // колонки: address_street, address_city в таблице persons
}

public class Address {
    String street;
    String city;
}
```

**Вариант 2 — отдельная таблица:**
```java
@Table("orders")
public class Order {
    @Id Long id;
    ShippingInfo shippingInfo;  // FK: shipping_info.order_id
}
```
Spring Data JDBC автоматически добавит FK `order_id` в таблицу `shipping_info`.

---

## Q6. (!) Как маппировать связь один-ко-многим через @MappedCollection?

```java
@Table("orders")
public class Order {
    @Id
    private Long id;
    
    @MappedCollection(idColumn = "order_id")
    private Set<OrderItem> items;
    
    // Для List с порядком:
    @MappedCollection(idColumn = "order_id", keyColumn = "position")
    private List<OrderItem> orderedItems;
}

@Table("order_items")
public class OrderItem {
    @Id Long id;
    String productName;
    int quantity;
    // Нет поля orderId — управляется через idColumn в @MappedCollection
}
```

**SQL DDL:**
```sql
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    position INT,  -- для List с keyColumn
    product_name VARCHAR,
    quantity INT
);
```

**Ключевые параметры `@MappedCollection`:**
- `idColumn` — имя FK-колонки в дочерней таблице
- `keyColumn` — для `List`/`Map`: колонка порядка/ключа

**Важно:** при каждом `save(order)` Spring Data JDBC **удаляет все** `order_items` с данным `order_id` и вставляет заново. Нет "умного" merge как в JPA.

---

## Q7. Что такое AggregateReference и когда его использовать?

`AggregateReference<T, ID>` — ссылка на корень другого агрегата **по ID**, без загрузки объекта. Позволяет выразить связь между агрегатами не нарушая их границы.

```java
// Customer — отдельный агрегат
@Table("orders")
public class Order {
    @Id Long id;
    
    // Ссылка на Customer без загрузки Customer
    AggregateReference<Customer, Long> customerId;  // хранит только Long
}
```

В БД это просто колонка `customer_id` типа `BIGINT`. При загрузке `Order` данные `Customer` не подгружаются.

**Зачем нужен:**
- Граница агрегата не пересекается (Order не тянет Customer)
- Типобезопасная ссылка (vs просто `Long customerId`)
- При необходимости загружаем явно: `customerRepository.findById(order.getCustomerId().getId())`

**Итог:** `@MappedCollection` — для сущностей внутри агрегата; `AggregateReference` — для ссылок между агрегатами.

---

## Q8. Как работает @Embedded?

`@Embedded` — маппирует вложенный объект как набор колонок в той же таблице. Нет отдельного JOIN, нет отдельной таблицы.

```java
@Table("customers")
public class Customer {
    @Id Long id;
    String name;
    
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    HomeAddress homeAddress;
    
    @Embedded(prefix = "billing_", onEmpty = Embedded.OnEmpty.USE_NULL)
    BillingAddress billingAddress;
}

public record HomeAddress(String street, String city, String zip) {}
public record BillingAddress(String street, String city) {}
```

**SQL колонки:**
```
id, name, street, city, zip, billing_street, billing_city
```

`prefix` позволяет иметь несколько embedded-объектов одного типа в одной таблице.

`onEmpty = USE_NULL` — если все колонки `NULL`, объект будет `null`. `USE_EMPTY` — создаст пустой объект.

---

## Q9. Какие основные аннотации Spring Data JDBC?

| Аннотация | Назначение |
|---|---|
| `@Table("name")` | Указать имя таблицы |
| `@Id` | Первичный ключ (обязателен) |
| `@Column("name")` | Указать имя колонки |
| `@MappedCollection` | Коллекция дочерних сущностей (1:N) |
| `@Embedded` | Вложенный объект в той же таблице |
| `@Version` | Версия для оптимистичной блокировки |
| `@ReadOnlyProperty` | Поле читается из БД, не пишется |
| `@Transient` | Поле не маппируется |
| `@PersistenceCreator` | Конструктор для создания сущности |

---

## Q10. Как работает именование таблиц и колонок по умолчанию?

Spring Data JDBC по умолчанию конвертирует `camelCase` в `snake_case`:
- Класс `OrderItem` → таблица `order_item`
- Поле `firstName` → колонка `first_name`

**Изменение стратегии:**
```java
@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {
    @Override
    public NamingStrategy namingStrategy() {
        return new DefaultNamingStrategy() {
            @Override
            public String getTableName(Class<?> type) {
                return "app_" + super.getTableName(type);  // prefix
            }
        };
    }
}
```

Или через `@Table` / `@Column` непосредственно на сущности.

---

## Q11. Как создать иммутабельную сущность?

```java
@Table("products")
public class Product {
    @Id
    private final Long id;
    private final String name;
    private final BigDecimal price;
    
    @PersistenceCreator  // Spring JDBC использует этот конструктор
    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    // Getters, no setters
}
```

`@PersistenceCreator` указывает конструктор, который Spring Data JDBC использует при чтении из БД. Также работает с Java records:

```java
@Table("products")
public record Product(
    @Id Long id,
    String name,
    BigDecimal price
) {}
```

Records автоматически поддерживаются — `@PersistenceCreator` не нужен.

---

## Q12. Как работает оптимистичная блокировка через @Version?

```java
@Table("accounts")
public class Account {
    @Id Long id;
    BigDecimal balance;
    
    @Version
    long version;  // автоматически инкрементируется при каждом UPDATE
}
```

При `save(account)` генерируется:
```sql
UPDATE accounts SET balance = ?, version = version + 1
WHERE id = ? AND version = ?  -- проверяет версию
```

Если другой поток уже обновил запись (версия изменилась) → бросается `OptimisticLockingFailureException`.

**Отличие от JPA:** механизм одинаковый, но Spring Data JDBC не имеет Session — конфликт обнаруживается на уровне репозитория.

---

## Q13. Какие репозитории использует Spring Data JDBC?

```java
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    
    // Derived query methods
    List<Product> findByNameContaining(String keyword);
    Optional<Product> findByNameAndCategory(String name, String category);
    
    // Custom SQL
    @Query("SELECT * FROM products WHERE price < :maxPrice ORDER BY price")
    List<Product> findCheaperThan(@Param("maxPrice") BigDecimal maxPrice);
}
```

Доступные базовые интерфейсы:

| Интерфейс | Методы |
|---|---|
| `CrudRepository<T,ID>` | `save`, `findById`, `findAll`, `delete`, `count` |
| `PagingAndSortingRepository` | `findAll(Sort)`, `findAll(Pageable)` |
| `ListCrudRepository` | Как CrudRepository но возвращает `List` |

---

## Q14. (!) Как написать кастомный SQL-запрос в Spring Data JDBC?

Spring Data JDBC использует **plain SQL**, не JPQL. Параметры — только именованные (не `?1`).

```java
@Query("SELECT p.* FROM products p " +
       "JOIN categories c ON p.category_id = c.id " +
       "WHERE c.name = :category AND p.price BETWEEN :min AND :max")
List<Product> findByCategoryAndPriceRange(
    @Param("category") String category,
    @Param("min") BigDecimal min,
    @Param("max") BigDecimal max
);
```

**Возврат кастомных DTO через RowMapper:**
```java
@Query("SELECT p.name, COUNT(oi.id) as order_count " +
       "FROM products p LEFT JOIN order_items oi ON p.id = oi.product_id " +
       "GROUP BY p.id")
List<ProductStats> getProductStats();

public record ProductStats(String name, long orderCount) {}
// Spring Data JDBC маппирует через конструктор/поля автоматически
```

---

## Q15. Как выполнять modifying-запросы?

```java
@Modifying
@Query("UPDATE products SET price = :price WHERE id = :id")
int updatePrice(@Param("id") Long id, @Param("price") BigDecimal price);

@Modifying
@Query("DELETE FROM products WHERE category = :category")
void deleteByCategory(@Param("category") String category);
```

`@Modifying` обязателен для `INSERT`/`UPDATE`/`DELETE`. Без него Spring Data JDBC ожидает результирующий набор.

Возвращаемые типы: `void`, `int` (количество затронутых строк), `boolean`.

---

## Q16. Как работает транзакционность в Spring Data JDBC?

По умолчанию все методы репозитория выполняются в транзакции (методы `save`/`delete` — в транзакции с `@Transactional`).

При `save(order)` со вложенными коллекциями всё происходит в одной транзакции:
```
BEGIN
  DELETE FROM order_items WHERE order_id = ?
  INSERT INTO order_items (order_id, ...) VALUES (...)
  UPDATE orders SET ... WHERE id = ?
COMMIT
```

**Кастомные транзакции в сервисе:**
```java
@Service
@Transactional
public class OrderService {
    
    public void processOrder(Order order) {
        orderRepository.save(order);
        inventoryService.reserveItems(order.getItems()); // одна транзакция
    }
}
```

Отличие от JPA: нет автоматического flush/merge — `save()` всегда явный SQL.

---

## See also

- [Spring Data JPA](spring-data-jpa-interview.md) — сравнение JPA vs JDBC, ORM-концепции
- [Hibernate](../../databases/hibernate-interview.md) — под капотом Spring Data JPA
- [Spring Framework](spring-framework-interview.md) — транзакции, DI
- [Spring Boot](spring-boot-interview.md) — автоконфигурация Spring Data JDBC
- [Транзакции БД](../../databases/database-transactions-interview.md) — изоляция, оптимистичная блокировка
- [SQL](../../databases/sql-interview.md) — plain SQL в @Query Spring Data JDBC
- [Flyway / Liquibase](../../databases/flyway-liquibase-interview.md) — миграции схемы (обязательны для Spring Data JDBC)
- [DDD](../../architecture/ddd-interview.md) — агрегаты, bounded contexts, aggregate root
- [Шпаргалка: Spring Data JDBC: Полное руководство по](../../../frameworks/java-frameworks/spring/spring-data-jdbc.md) — теория
