---
title: "Вопросы на собеседовании: Spring Data JDBC"
description: "Spring Data JDBC: aggregate root, @MappedCollection, AggregateReference, отличия от JPA, @Query, @Version"
tags:
  - interview
  - frameworks
  - spring-data-jdbc-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Data JDBC"
  - "Spring Data JDBC interview"
  - "Spring Data JDBC собеседование"
prerequisites:
  - "[[spring-data-jdbc]]"
next: []
updated: "2026-04-25"
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
- [Q1. (!) Что такое Spring Data JDBC и чем отличается от Spring Data JPA?](#q1--что-такое-spring-data-jdbc-и-чем-отличается-от-spring-data-jpa)
- [Q2. Что означает "нет lazy loading" на практике?](#q2-что-означает-нет-lazy-loading-на-практике)
- [Q3. Когда выбирать Spring Data JDBC вместо JPA?](#q3-когда-выбирать-spring-data-jdbc-вместо-jpa)

**Модель агрегатов**
- [Q4. (!) Что такое Aggregate Root в Spring Data JDBC?](#q4--что-такое-aggregate-root-в-spring-data-jdbc)
- [Q5. Как маппировать связь один-к-одному?](#q5-как-маппировать-связь-один-к-одному)
- [Q6. (!) Как маппировать связь один-ко-многим через @MappedCollection?](#q6--как-маппировать-связь-один-ко-многим-через-mappedcollection)
- [Q7. Что такое AggregateReference и когда его использовать?](#q7-что-такое-aggregatereference-и-когда-его-использовать)
- [Q8. Как работает @Embedded?](#q8-как-работает-embedded)

**Маппинг и аннотации**
- [Q9. Какие основные аннотации Spring Data JDBC?](#q9-какие-основные-аннотации-spring-data-jdbc)
- [Q10. Как работает именование таблиц и колонок по умолчанию?](#q10-как-работает-именование-таблиц-и-колонок-по-умолчанию)
- [Q11. Как создать иммутабельную сущность?](#q11-как-создать-иммутабельную-сущность)
- [Q12. Как работает оптимистичная блокировка через @Version?](#q12-как-работает-оптимистичная-блокировка-через-version)

**Репозитории и запросы**
- [Q13. Какие репозитории использует Spring Data JDBC?](#q13-какие-репозитории-использует-spring-data-jdbc)
- [Q14. (!) Как написать кастомный SQL-запрос в Spring Data JDBC?](#q14--как-написать-кастомный-sql-запрос-в-spring-data-jdbc)
- [Q15. Как выполнять modifying-запросы?](#q15-как-выполнять-modifying-запросы)
- [Q16. Как работает транзакционность в Spring Data JDBC?](#q16-как-работает-транзакционность-в-spring-data-jdbc)

---

## Q1. (!) Что такое Spring Data JDBC и чем отличается от Spring Data JPA?

`Spring Data JDBC` — persistence-фреймворк, который даёт привычный репозиторный API Spring Data, но работает напрямую через SQL, без Hibernate и без ORM под капотом. Ту же сущность вы сохраняете тем же `repository.save(entity)`, но за этим вызовом нет ни Session, ни кэша, ни отложенной загрузки — только явный SQL.

Ключевое различие в **модели работы с состоянием**. JPA держит сущности в персистентном контексте (Session) и сама решает, что и когда писать в БД: отслеживает изменения (dirty checking), кэширует, грузит связи лениво. Spring Data JDBC ничего этого не делает — каждый вызов репозитория порождает понятный SQL прямо сейчас.

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

Практический смысл этих различий: меньше скрытого поведения и неожиданных запросов, но и меньше «удобств» — придётся самому писать схему, миграции и сложные выборки на SQL.

**Формулировка на собеседовании:** Spring Data JDBC — это «what you see is what you get»: нет скрытых SQL-запросов, нет неожиданного lazy loading, нет магии Session. Что вызвал — то и ушло в базу.

---

## Q2. Что означает "нет lazy loading" на практике?

На практике это значит, что **весь агрегат загружается целиком и сразу** при первом же запросе. В JPA связи грузятся лениво: первый доступ к коллекции выполняет отдельный SELECT, и для этого нужна открытая Session. В Spring Data JDBC Session нет вообще, поэтому ленивой загрузки тоже нет — при `findById` все дочерние объекты подтягиваются eager (через JOIN или дополнительные SELECT), а в объекте уже всё на месте.

Этот выбор убирает целый класс типичных JPA-проблем, но взамен переносит цену загрузки на момент чтения агрегата.

**Что это даёт:**
- Нет `LazyInitializationException` — той самой ошибки JPA при обращении к коллекции вне транзакции, когда Session уже закрыта.
- Нет N+1 в неожиданных местах: все запросы явные и предсказуемые, а не «случайно вылезли при итерации по коллекции».
- Нет `@FetchType` — управлять стратегией загрузки внутри агрегата нечем, она всегда eager.

**Подводный камень:** большой агрегат с объёмными коллекциями каждый раз грузится полностью, и это может быть дорого. Отсюда правило: проектируйте агрегаты компактными, не складывая в один корень всё подряд.

```java
// При findById загружается ВСЯ структура агрегата:
Order order = orderRepository.findById(id).orElseThrow();
// order.getItems() — уже загружен, никакого SELECT
```

---

## Q3. Когда выбирать Spring Data JDBC вместо JPA?

Коротко: Spring Data JDBC выигрывает, когда модель простая и важна предсказуемость, а JPA — когда модель сложная и нужны её удобства (ленивая загрузка, кэш, генерация схемы). Выбор — это компромисс между простотой и богатством возможностей.

**Берите Spring Data JDBC, если:**
- Доменная модель простая, агрегаты небольшие.
- Нужны предсказуемые SQL-запросы без ORM-магии и скрытых обращений к БД.
- Проект следует DDD с чёткими границами агрегатов — фреймворк ложится на эту модель напрямую.
- Важна производительность на CRUD: нет накладных расходов Session и dirty checking.
- Команда хочет избежать классических ловушек Hibernate (N+1, `LazyInitializationException`, нюансы кэша).

**Оставайтесь на JPA, если:**
- Графы сущностей сложные и реально нужна ленивая загрузка, чтобы не тянуть всё разом.
- Нужна генерация схемы или её эволюция средствами Hibernate.
- Используете Criteria API или сложные JPQL-запросы.
- Нужен second-level cache (L2) Hibernate.
- Уже есть большой проект на Hibernate — переписывать дороже, чем продолжать.

**Итог:** Spring Data JDBC — сильный выбор для новых микросервисов с чистой DDD-архитектурой, где ценят прозрачность. JPA — для legacy-систем и сложных объектных графов, где удобства ORM перевешивают его сложность.

---

## Q4. (!) Что такое Aggregate Root в Spring Data JDBC?

**Aggregate Root** (корень агрегата) — главная сущность группы связанных объектов и **единственная точка входа** для их сохранения и чтения. Идея пришла из DDD, и Spring Data JDBC встроил её в саму модель работы: репозиторий заводят только для корня, а всё, что внутри агрегата, живёт и обновляется через него.

Агрегат — это не просто «сущность со связями», а граница консистентности: объекты внутри неё всегда сохраняются и удаляются как единое целое, в одной транзакции.

Структура агрегата на примере заказа: корнем выступает `Order` (Aggregate Root), а внутри его границы живут дочерние объекты:

- `Order` (Aggregate Root)
  - → `OrderItem 1`
  - → `OrderItem 2`
  - → `DeliveryAddress`

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
- Весь агрегат сохраняется и удаляется только через корень: `orderRepository.save(order)` запишет и сам заказ, и его `OrderItem`.
- Отдельного `OrderItemRepository` нет и быть не должно — элементы управляются исключительно через `Order`. Это и есть «единственная точка входа».
- При `deleteById` все вложенные `OrderItem` удаляются каскадно — граница агрегата не оставляет «висячих» дочерних строк.
- Ссылка на **другой** агрегат делается через `AggregateReference` (хранит только ID и не загружает чужой объект), чтобы не нарушать его границу.

---

## Q5. Как маппировать связь один-к-одному?

Связь один-к-одному реализуется одним из двух способов: **вложить объект в ту же таблицу** через `@Embedded` или **вынести его в отдельную таблицу** с прямой ссылкой по FK. Выбор зависит от того, хотите ли вы держать данные в одной строке или разнести их по таблицам.

Выбирайте по смыслу: `@Embedded` — когда зависимый объект логически часть владельца и отдельно не нужен (адрес внутри персоны); отдельную таблицу — когда у него своя идентичность или жизненный цикл.

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
@Table("shipping_info")
public class ShippingInfo {
    @Id Long id;
    String address;
    // FK order_id автоматически добавляется Spring Data JDBC
}

@Table("orders")
public class Order {
    @Id Long id;
    ShippingInfo shippingInfo;  // FK: shipping_info.order_id
}
```
Spring Data JDBC автоматически добавит FK `order_id` в таблицу `shipping_info`.

---

## Q6. (!) Как маппировать связь один-ко-многим через @MappedCollection?

Связь один-ко-многим оформляется коллекцией (`Set`, `List` или `Map`) в корне агрегата, помеченной `@MappedCollection`. Аннотация говорит, **в какой колонке дочерней таблицы лежит FK на родителя** (`idColumn`), а для упорядоченных коллекций — ещё и колонку порядка/ключа (`keyColumn`). У самой дочерней сущности поля с FK быть не должно: связью управляет родитель.

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
- `idColumn` — имя FK-колонки в дочерней таблице, по которой строки привязываются к родителю.
- `keyColumn` — для `List`/`Map`: колонка, хранящая порядок (индекс `List`) или ключ (`Map`).

**Важный подводный камень — стратегия записи.** При каждом `save(order)` Spring Data JDBC **удаляет все** `order_items` с данным `order_id` и вставляет коллекцию заново (delete-and-reinsert). Никакого «умного» merge по отдельным строкам, как в JPA, здесь нет. Последствия, о которых стоит знать на собеседовании:
- Дочерние строки каждый раз получают **новые** первичные ключи — внешние ссылки на конкретный `OrderItem` по id ненадёжны.
- Для больших коллекций это дороже точечного UPDATE, что снова подталкивает держать агрегаты компактными.

---

## Q7. Что такое AggregateReference и когда его использовать?

`AggregateReference<T, ID>` — это **типобезопасная ссылка на корень другого агрегата по его ID**, без загрузки самого объекта. Нужна, чтобы выразить связь между агрегатами, не нарушая их границы: `Order` ссылается на `Customer`, но не тянет его в память и не управляет его жизненным циклом.

Это прямое следствие правила агрегатов из Q4: внутри агрегата объекты вкладывают через `@MappedCollection`/`@Embedded`, а на чужой агрегат ссылаются только по ID.

```java
// Customer — отдельный агрегат
@Table("orders")
public class Order {
    @Id Long id;
    
    // Ссылка на Customer без загрузки Customer
    AggregateReference<Customer, Long> customerId;  // хранит только Long
}
```

В БД это просто колонка `customer_id` типа `BIGINT`. При загрузке `Order` данные `Customer` не подгружаются — в поле лежит только идентификатор.

**Зачем он нужен:**
- Граница агрегата остаётся непересечённой: `Order` не тянет за собой `Customer`.
- Ссылка типобезопасна — `AggregateReference<Customer, Long>` выразительнее и надёжнее, чем «голый» `Long customerId`: из типа видно, на какой агрегат указываем.
- Когда `Customer` всё-таки нужен, его грузят явно через его собственный репозиторий: `customerRepository.findById(order.getCustomerId().getId())`.

**Итог — где что применять:** `@MappedCollection` — для сущностей **внутри** агрегата; `AggregateReference` — для ссылок **между** агрегатами.

---

## Q8. Как работает @Embedded?

`@Embedded` **раскладывает поля вложенного объекта в колонки той же таблицы**, что и владелец. Отдельной таблицы и JOIN не возникает — объект существует на уровне Java, но в БД это просто дополнительные колонки родительской строки. Удобно для значимых объектов (value objects) вроде адреса, которые логически часть сущности.

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

**Параметр `prefix`** решает коллизию имён: два embedded-объекта одного типа (например, домашний и платёжный адрес) дали бы одинаковые колонки `street`, `city`. Префикс `billing_` разводит их по разным колонкам, поэтому несколько embedded-объектов уживаются в одной таблице.

**Параметр `onEmpty`** управляет тем, что делать при чтении, когда все колонки объекта пусты:
- `USE_NULL` — если все колонки `NULL`, поле станет `null`.
- `USE_EMPTY` — вместо `null` создаст пустой объект (полезно, чтобы не проверять на `null` при каждом обращении).

---

## Q9. Какие основные аннотации Spring Data JDBC?

Набор намеренно небольшой — фреймворку нечего настраивать для ORM-поведения, поэтому аннотации описывают только маппинг на таблицу и пару служебных моментов. Важно: пакеты у них **свои** (`org.springframework.data.relational.core.mapping` и `org.springframework.data.annotation`), а не `javax/jakarta.persistence`, как в JPA.

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

По умолчанию имена выводятся из имён класса и полей с переводом `camelCase` → `snake_case` через `NamingStrategy`:
- Класс `OrderItem` → таблица `order_item`
- Поле `firstName` → колонка `first_name`

Если соглашение не совпадает с вашей схемой (например, нужен общий префикс таблиц), стратегию можно переопределить глобально, унаследовавшись от `DefaultNamingStrategy`:

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

Для точечных исключений менять глобальную стратегию не нужно — достаточно указать `@Table` / `@Column` прямо на сущности.

---

## Q11. Как создать иммутабельную сущность?

Иммутабельность здесь работает естественно, потому что Spring Data JDBC не делает dirty checking и не правит объект «на лету» — он лишь читает данные через конструктор и пишет их обратно. Поэтому поля можно объявить `final` и не давать сеттеров. Главное — указать, **через какой конструктор фреймворку создавать объект при чтении из БД**: это и есть роль `@PersistenceCreator`.

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

`@PersistenceCreator` нужен, когда у класса несколько конструкторов и надо однозначно показать, какой брать для гидрации из БД. С Java records он не требуется вовсе — у record единственный канонический конструктор, и фреймворк выбирает его сам:

```java
@Table("products")
public record Product(
    @Id Long id,
    String name,
    BigDecimal price
) {}
```

Records — самый лаконичный способ описать иммутабельную сущность: компилятор сам генерирует конструктор и аксессоры, а Spring Data JDBC их подхватывает.

---

## Q12. Как работает оптимистичная блокировка через @Version?

Оптимистичная блокировка защищает от потерянных обновлений (lost update), когда два потока читают одну запись и затем по очереди её перезаписывают. Поле с `@Version` хранит номер версии строки; при каждом UPDATE Spring Data JDBC **инкрементирует версию и добавляет в `WHERE` проверку её прежнего значения**. Если за это время кто-то уже изменил строку, версия не совпадёт, UPDATE затронет 0 строк — и фреймворк бросит исключение.

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

Если другой поток уже обновил запись (версия в `WHERE` не совпала и UPDATE задел 0 строк), бросается `OptimisticLockingFailureException` — приложение узнаёт о конфликте и может повторить операцию на свежих данных.

**Дополнительный эффект `@Version`:** по нему фреймворк отличает новую сущность от уже сохранённой. Версия `0`/`null` означает INSERT, заполненная — UPDATE.

**Отличие от JPA:** сам механизм версионирования тот же, но в Spring Data JDBC нет Session — конфликт обнаруживается прямо в момент вызова репозитория (`save`), а не при flush, как в JPA.

---

## Q13. Какие репозитории использует Spring Data JDBC?

Те же базовые интерфейсы Spring Data, что и в JPA: вы наследуете `CrudRepository` (или его варианты), а реализацию генерирует фреймворк. Доступны три способа задать запрос: готовые CRUD-методы, derived query methods (запрос выводится из имени метода вроде `findByNameContaining`) и явный `@Query` с SQL.

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

Базовые интерфейсы и что они добавляют:

| Интерфейс | Методы |
|---|---|
| `CrudRepository<T,ID>` | `save`, `findById`, `findAll`, `delete`, `count` |
| `PagingAndSortingRepository` | `findAll(Sort)`, `findAll(Pageable)` |
| `ListCrudRepository` | Как `CrudRepository`, но возвращает `List` вместо `Iterable` |

---

## Q14. (!) Как написать кастомный SQL-запрос в Spring Data JDBC?

В `@Query` пишут **обычный SQL вашей СУБД**, а не JPQL — никакого слоя абстракции над диалектом нет, имена таблиц и колонок указываются как в базе. Параметры передаются **только именованными** (`:name` + `@Param`), позиционные `?1`/`?2` не поддерживаются.

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

**Проекция в кастомный DTO.** Запрос может возвращать не сущность, а произвольную структуру — например, агрегаты со `COUNT`. Объявляете `record`/класс под форму результата, и Spring Data JDBC маппирует колонки на него автоматически (по конструктору или полям), сопоставляя имена колонок с именами компонентов:
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

`INSERT`/`UPDATE`/`DELETE` через `@Query` нужно дополнительно помечать `@Modifying`. Эта аннотация говорит фреймворку, что запрос **меняет данные и не возвращает результирующий набор**, — иначе Spring Data JDBC попытается прочитать выборку и упадёт.

```java
@Modifying
@Query("UPDATE products SET price = :price WHERE id = :id")
int updatePrice(@Param("id") Long id, @Param("price") BigDecimal price);

@Modifying
@Query("DELETE FROM products WHERE category = :category")
void deleteByCategory(@Param("category") String category);
```

**Возвращаемые типы** modifying-метода:
- `void` — результат не важен;
- `int` — количество затронутых строк (удобно проверить, что обновилось ровно то, что ждали);
- `boolean` — было ли затронуто хоть что-то.

---

## Q16. Как работает транзакционность в Spring Data JDBC?

Транзакции работают через тот же механизм Spring `@Transactional`, что и везде: методы стандартных репозиториев уже обёрнуты в транзакцию (CRUD-методы вроде `save`/`delete` помечены `@Transactional` в базовой реализации). Это критично для агрегата — он всегда сохраняется атомарно.

При `save(order)` со вложенными коллекциями все операции (включая delete-and-reinsert дочерних строк из Q6) идут в одной транзакции, поэтому агрегат не может оказаться в полусохранённом состоянии:
```
BEGIN
  DELETE FROM order_items WHERE order_id = ?
  INSERT INTO order_items (order_id, ...) VALUES (...)
  UPDATE orders SET ... WHERE id = ?
COMMIT
```

**Объединение нескольких операций в одну транзакцию.** Чтобы несколько вызовов репозиториев прошли атомарно, оборачивают сервисный метод в `@Transactional` — все вложенные `save`/`delete` подхватят эту транзакцию вместо своих собственных:
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

**Отличие от JPA:** нет автоматического flush и merge из персистентного контекста — `save()` сразу выполняет явный SQL. В JPA изменения могут уйти в БД «сами» при flush; здесь же запись происходит ровно тогда, когда вы вызвали репозиторий, и ровно теми запросами, что видны в логе.

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
