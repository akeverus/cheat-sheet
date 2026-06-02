---
title: "Вопросы на собеседовании: Hibernate Relationships"
description: "Hibernate/JPA relationships: @OneToOne, @OneToMany, @ManyToOne, @ManyToMany, mappedBy, FetchType LAZY/EAGER, CascadeType, N+1 проблема и её решения"
tags:
  - interview
  - databases
  - hibernate-relationships-interview
type: "interview"
difficulty: "advanced"
aliases:
  - "Вопросы на собеседовании"
  - "Hibernate Relationships"
  - "OneToMany ManyToOne interview"
  - "Hibernate associations вопросы"
prerequisites:
  - "[[hibernate-relationships]]"
next: []
updated: 2026-05-31
---
# Вопросы на собеседовании: `Hibernate Relationships`

`Hibernate/JPA relationships` — настройка связей между entity: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`. Критически важная тема: неправильная настройка приводит к N+1, пустым коллекциям, невозможности сохранения. Почти всегда спрашивается на middle/senior Java интервью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Hibernate Mapping Associations](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#associations) — официальная документация
- [Baeldung: Hibernate Relationships](https://www.baeldung.com/jpa-hibernate-associations) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Какие типы связей поддерживает Hibernate?

Hibernate/JPA поддерживает четыре типа связей между entity — они отражают кардинальность отношения в реляционной модели:

- **`@OneToOne`** — один к одному. Каждой записи одной таблицы соответствует ровно одна запись другой (User ↔ UserProfile).
- **`@ManyToOne`** — многие к одному. Это «рабочая лошадка» маппинга: именно на стороне `@ManyToOne` лежит FK-колонка (OrderItem → Order).
- **`@OneToMany`** — один ко многим. Обратная сторона `@ManyToOne`: у родителя коллекция детей (Order → OrderItems).
- **`@ManyToMany`** — многие ко многим. Реализуется через отдельную junction-таблицу (Student ↔ Course).

**Ключевой момент:** в реляционной БД нет «коллекций» — есть только FK. Поэтому `@OneToMany` физически выражается тем же FK, что и `@ManyToOne`, просто смотрит на него с другой стороны. Это объясняет, почему bidirectional-связь и `mappedBy` устроены именно так (см. Q2).

```java
// @OneToOne — один к одному (User ↔ UserProfile)
@Entity
public class User {
    @Id Long id;
    @OneToOne(mappedBy = "user")
    UserProfile profile;
}

// @OneToMany — один ко многим (Order → OrderItems)
@Entity
public class Order {
    @Id Long id;
    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    List<OrderItem> items = new ArrayList<>();
}

// @ManyToOne — многие к одному (OrderItem → Order)
@Entity
public class OrderItem {
    @Id Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    Order order;
}

// @ManyToMany — многие ко многим (Student ↔ Course)
@Entity
public class Student {
    @Id Long id;
    @ManyToMany
    @JoinTable(name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"))
    Set<Course> courses = new HashSet<>();
}
```

## Q2. Чем отличается unidirectional от bidirectional связей?

Разница в том, со скольких сторон в Java-коде доступна связь. На уровне БД схема может быть одинаковой — отличается только маппинг.

**Unidirectional** — только одна сторона знает о другой. Навигация возможна лишь в одном направлении:

```java
@Entity
class Order {
    @OneToMany
    @JoinColumn(name = "order_id")  // колонка в order_item
    List<OrderItem> items;
}

@Entity
class OrderItem {
    @Id Long id;
    // нет ссылки на Order
}
```

**Bidirectional** — обе стороны знают друг о друге:

```java
@Entity
class Order {
    @OneToMany(mappedBy = "order")   // mappedBy — owning side on other entity
    List<OrderItem> items;
}

@Entity
class OrderItem {
    @ManyToOne
    @JoinColumn(name = "order_id")   // owning side
    Order order;
}
```

**Кто такой owning side.** Owning side — это сторона, которая физически владеет FK-колонкой (у неё `@JoinColumn`, нет `mappedBy`). Именно её состояние Hibernate смотрит при записи в БД. `mappedBy` помечает противоположную, inverse-сторону и говорит: «связь уже отмапплена полем `order` на той entity, новую колонку не создавай». Если забыть `mappedBy`, Hibernate решит, что это две независимые связи, и создаст лишнюю колонку или junction-таблицу.

**Правило:** `mappedBy` указывает на имя поля owning side (owning side — та, у которой `@JoinColumn`, без `mappedBy`).

**Подводный камень:** Hibernate сохраняет связь по owning side. Если выставить только inverse-сторону (`order.getItems().add(item)`), но не выставить `item.setOrder(order)`, FK останется `null`. Отсюда — необходимость helper-методов.

**Рекомендация:** helper-методы для синхронизации обеих сторон:

```java
public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);  // синхронизируем обе стороны
}
public void removeItem(OrderItem item) {
    items.remove(item);
    item.setOrder(null);
}
```

## Q3. Что такое FetchType.LAZY vs EAGER?

`FetchType` задаёт, **когда** Hibernate загружает связанные данные: сразу с родителем (`EAGER`) или отложенно, при первом обращении к полю (`LAZY`).

- **`LAZY`** — связь подменяется прокси/коллекцией-обёрткой; реальный SQL уходит только когда вы тронете данные. Дефолт для `@OneToMany` и `@ManyToMany`.
- **`EAGER`** — связь грузится одним выстрелом вместе с родителем (обычно через JOIN). Дефолт для `@ManyToOne` и `@OneToOne`.

```java
@Entity
class Order {
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)  // ← default для *ToMany
    List<OrderItem> items;

    @ManyToOne(fetch = FetchType.EAGER)   // ← default для *ToOne
    Customer customer;
}

// LAZY — загружается при первом обращении
Order order = em.find(Order.class, 1L);   // 1 SQL: SELECT * FROM orders
order.getItems().size();                   // 2 SQL: SELECT * FROM order_items

// EAGER — загружается сразу с parent
Order order = em.find(Order.class, 1L);   // 1 SQL с JOIN customer
```

**Рекомендация:** ставьте `LAZY` явно на все связи, включая `@ManyToOne`/`@OneToOne` (где дефолт — `EAGER`):

```java
@ManyToOne(fetch = FetchType.LAZY)  // явно
@JoinColumn(name = "customer_id")
Customer customer;
```

**Почему LAZY как стратегия по умолчанию:**

- `EAGER` зашит в маппинг и не отключается на лету — связь грузится **всегда**, даже в запросах, где она не нужна. Это лишний JOIN и трафик на каждом обращении к entity.
- `EAGER` на коллекциях усугубляет N+1 и легко вызывает `MultipleBagFetchException`, потому что вы не контролируете, что подтянется.
- `LAZY` гибче: по умолчанию ничего лишнего, а там, где данные действительно нужны, вы добираете их адресно через `JOIN FETCH` или `@EntityGraph` (см. Q4, Q11).

**Подводный камень:** обращение к `LAZY`-полю вне открытой сессии/транзакции даёт `LazyInitializationException`. Грузить связь нужно внутри транзакции — либо `JOIN FETCH`, либо обращением к полю до закрытия сессии.

## Q4. Что такое N+1 проблема и как её решить?

**N+1** — это когда вместо одного запроса Hibernate выполняет 1 запрос за списком родителей плюс ещё по одному за связанными данными каждого из N родителей. Итого `1 + N` запросов вместо одного-двух. Главный источник деградации производительности на ORM.

**Откуда берётся:** связь `LAZY`, вы прошли по списку родителей и в цикле тронули у каждого ленивую коллекцию — каждое обращение порождает отдельный `SELECT`.

```java
// ПЛОХО: 1 SQL за заказами + N SQL за OrderItems
List<Order> orders = em.createQuery("FROM Order", Order.class).getResultList();
for (Order order : orders) {
    System.out.println(order.getItems().size());  // лишний SELECT * FROM order_items
}
// 101 SQL для 100 заказов
```

**Решения** (по убыванию частоты применения):

- **`JOIN FETCH`** — подтягивает связь одним запросом прямо в JPQL. Точечно, под конкретный кейс.
- **`@EntityGraph`** — то же самое, но декларативно и переиспользуемо на уровне репозитория (см. Q11).
- **`@BatchSize`** — не убирает N+1, а схлопывает N запросов в `N/size` за счёт загрузки идентификаторов пачками через `IN (...)`.
- **`@Fetch(FetchMode.SUBSELECT)`** — все коллекции родителей грузятся одним подзапросом по выборке родителей.

```java
// 1. JOIN FETCH — загрузка items вместе с orders
List<Order> orders = em.createQuery(
    "FROM Order o JOIN FETCH o.items", Order.class
).getResultList();  // 1 SQL с JOIN

// 2. @EntityGraph — декларативно
@EntityGraph(attributePaths = {"items", "customer"})
@Query("FROM Order")
List<Order> findAllWithDetails();

// 3. @BatchSize — батч загрузка
@Entity
class Order {
    @OneToMany(mappedBy = "order")
    @BatchSize(size = 20)       // загружает items пачками по 20
    List<OrderItem> items;
}

// 4. @Fetch(FetchMode.SUBSELECT) — подзапрос
@OneToMany(mappedBy = "order")
@Fetch(FetchMode.SUBSELECT)
List<OrderItem> items;          // SELECT * FROM order_items WHERE order_id IN (выборка orders)
```

## Q5. Что такое CascadeType и какие варианты?

`CascadeType` определяет, какие операции с родителем автоматически распространяются на связанные дочерние entity. Без каскада нужно вручную вызывать `persist`/`merge`/`remove` на каждом ребёнке — каскад избавляет от этой рутины.

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
List<OrderItem> items;
```

Каждый тип соответствует одной операции EntityManager:

| CascadeType | Действие |
|-------------|----------|
| `PERSIST` | `em.persist(order)` → `em.persist(item)` |
| `MERGE` | `em.merge(order)` → `em.merge(item)` |
| `REMOVE` | `em.remove(order)` → `em.remove(item)` |
| `REFRESH` | `em.refresh(order)` → `em.refresh(item)` |
| `DETACH` | `em.detach(order)` → `em.detach(item)` |
| `ALL` | Все выше |

```java
// Пример с PERSIST + REMOVE (типичная связь Order → OrderItems)
@OneToMany(mappedBy = "order", cascade = {PERSIST, REMOVE})
List<OrderItem> items;

// Создание — сохранятся и Order, и все items
em.persist(new Order().addItem(new OrderItem(...)));

// Удаление — удалятся Order и все его items
em.remove(order);
```

**Подводный камень:** `CascadeType.ALL` НЕ включает `orphanRemoval` — это отдельный атрибут `@OneToMany`. `REMOVE` срабатывает только при удалении самого родителя, а `orphanRemoval` — ещё и при удалении ребёнка из коллекции (разницу см. в Q6).

**Рекомендация:** не вешайте `ALL` бездумно. Перечисляйте только реально нужные операции — иначе можно нечаянно каскадно удалить или смержить данные, которые трогать не следовало.

## Q6. Что такое orphanRemoval?

`orphanRemoval = true` означает: как только ребёнок убран из родительской коллекции, он становится «сиротой» и автоматически удаляется из БД. Так выражается отношение, в котором ребёнок не может существовать без родителя (composition, а не association).

«Сирота» — это дочерняя entity, на которую больше не ссылается родитель. Без `orphanRemoval` такая запись просто зависает в БД с осиротевшим FK:

```java
@OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
List<OrderItem> items;

// Без orphanRemoval
order.getItems().remove(item);  // item остался в БД с order_id=null (или FK violation)

// С orphanRemoval
order.getItems().remove(item);  // item УДАЛЁН из БД
em.flush();
```

**Разница с `CascadeType.REMOVE`** — это два разных триггера:

- `REMOVE` срабатывает при `em.remove(order)` — удаляешь родителя, каскадно удаляются items.
- `orphanRemoval` срабатывает при удалении item **из коллекции** (родитель остаётся жив).

Они не взаимозаменяемы: `REMOVE` не отловит сироту, оставшуюся после `remove(item)` из коллекции, а `orphanRemoval` сам по себе сработает и при удалении родителя (раз все дети становятся сиротами).

**Сценарий применения:** включайте `orphanRemoval` для строго владеемых детей, у которых нет жизни вне родителя (OrderItem без Order бессмыслен). Не включайте для shared-сущностей, на которые ссылаются и другие entity, — иначе случайное удаление из одной коллекции снесёт запись из БД целиком.

## Q7. Как моделировать @ManyToMany связь?

`@ManyToMany` маппится на отдельную junction-таблицу с двумя FK. Для bidirectional одна сторона объявляет `@JoinTable` (owning side), вторая — `mappedBy`.

```java
// Простой @ManyToMany
@Entity
class Student {
    @Id Long id;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    Set<Course> courses = new HashSet<>();
}

@Entity
class Course {
    @Id Long id;

    @ManyToMany(mappedBy = "courses")   // mappedBy для bidirectional
    Set<Student> students = new HashSet<>();
}
```

**Главное ограничение `@ManyToMany`:** junction-таблица не может нести собственные поля. Как только бизнесу нужны атрибуты самой связи (`enrolled_at`, `grade`), чистый `@ManyToMany` не годится — добавить колонку в неё некуда.

**Решение** — повысить связь до самостоятельной entity: junction-таблица становится обычной таблицей `Enrollment`, а `@ManyToMany` распадается на два `@ManyToOne` (со стороны Enrollment) / `@OneToMany` (со стороны Student и Course). Композитный ключ собирается через `@EmbeddedId` + `@MapsId`:

```java
// Студент ←→ Enrollment ←→ Course (с дополнительными полями)
@Entity
class Enrollment {
    @EmbeddedId
    EnrollmentId id;

    @ManyToOne
    @MapsId("studentId")
    Student student;

    @ManyToOne
    @MapsId("courseId")
    Course course;

    LocalDate enrolledAt;
    Integer grade;  // дополнительное поле
}

@Embeddable
class EnrollmentId implements Serializable {
    Long studentId;
    Long courseId;
}
```

## Q8. Какие коллекции использовать для @OneToMany?

Тип коллекции в маппинге влияет не только на семантику в Java, но и на то, какой SQL генерирует Hibernate и насколько эффективно работают вставки/чтения. Короткое правило: `Set` — для bidirectional и уникальности, `List` без `@OrderColumn` (bag) — для самых дешёвых вставок, `List` с порядком — когда важна последовательность.

| Коллекция | Особенности | Применение |
|-----------|-------------|------------|
| `List<T>` | Порядок | Упорядоченные (ORDER BY id) |
| `Set<T>` | Уникальность, быстрее для contains | По умолчанию без дубликатов |
| `Bag` (`List` без `@OrderColumn`) | Самый дешёвый | Большие коллекции |
| `SortedSet`/`TreeSet` | Автосортировка | Нужна сортировка в Java |
| `Map<K, V>` | Ключ-значение | Индексированный доступ |

```java
// List с ORDER BY
@OneToMany(mappedBy = "order")
@OrderBy("price DESC")           // сортировка при загрузке
List<OrderItem> items;

// Set
@OneToMany(mappedBy = "post")
Set<Comment> comments;

// Map (ключ — одно из полей child entity)
@OneToMany(mappedBy = "user")
@MapKey(name = "type")
Map<String, Phone> phones;       // Phone.type → Phone
```

**Правило:** для bidirectional-связей предпочитайте `Set`. Причина — два `List`-bag в одном `JOIN FETCH` дают `MultipleBagFetchException` (Hibernate не может корректно перемножить декартово произведение двух bag), а `Set` дедуплицирует строки и этой проблемы лишён.

## Q9. Как работает @OneToOne и какие есть варианты?

У `@OneToOne` две принципиально разные реализации на уровне БД — выбор зависит от того, как связаны ключи:

- **Shared Primary Key** — у обеих таблиц одинаковый PK; дочерняя переиспользует id родителя через `@MapsId`. Нет отдельной FK-колонки, связь гарантированно 1:1, экономит место.
- **Foreign Key** — у каждой таблицы свой PK, а на одной стороне лежит FK-колонка (`@JoinColumn`) на другую. Гибче, но допускает «висячий» FK.

```java
// Shared Primary Key (одна и та же PK)
@Entity
class User {
    @Id Long id;
    @OneToOne(mappedBy = "user", cascade = ALL)
    UserProfile profile;
}

@Entity
class UserProfile {
    @Id Long id;                    // ← тот же ID что и User

    @OneToOne
    @MapsId                         // маппит user.id на profile.id
    @JoinColumn(name = "id")
    User user;
}
```

```java
// Foreign Key (разные PK, FK в одной стороне)
@Entity
class User {
    @Id Long id;
    @OneToOne(cascade = ALL)
    @JoinColumn(name = "profile_id")
    UserProfile profile;
}

@Entity
class UserProfile {
    @Id Long id;
    // unidirectional — не знает о User
}
```

**Подводный камень — LAZY на @OneToOne часто не работает.** На inverse-стороне (где `mappedBy`) Hibernate должен знать, ставить прокси или `null`, а узнать это можно только запросом — поэтому он молча грузит связь EAGER, даже если вы написали `LAZY`. Лечится двумя способами: `@OneToOne(optional = false)` (обещаем, что связь всегда есть → можно подставить прокси без проверки) и/или включением bytecode enhancement, который умеет лениво инициализировать сами поля.

## Q10. Как работают JoinTable vs JoinColumn?

Оба определяют, где физически хранится связь, но по-разному:

- **`@JoinColumn`** — FK живёт прямо в таблице этой entity (колонка `customer_id` в `orders`). Никакой лишней таблицы. Типично для `@ManyToOne` и `@OneToOne`.
- **`@JoinTable`** — связь выносится в отдельную таблицу-связку с двумя FK. Обязательно для `@ManyToMany` (FK некуда положить в самих таблицах), иногда применяется и в `@OneToMany`, когда не хотят FK в дочерней таблице.

```java
// @JoinColumn — FK в таблице этой entity
@Entity
class Order {
    @Id Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")   // колонка customer_id в таблице orders
    Customer customer;
}
// SQL: CREATE TABLE orders (id, customer_id, ...);

// @JoinTable — отдельная таблица связей
@Entity
class Student {
    @ManyToMany
    @JoinTable(
        name = "student_course",        // отдельная таблица
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    Set<Course> courses;
}
// SQL: CREATE TABLE student_course (student_id, course_id);
```

**Эмпирическое правило:** `@JoinColumn`, когда FK помещается в таблицу самой entity; `@JoinTable`, когда нужна отдельная таблица связей (обычно `@ManyToMany`).

## Q11. Как использовать @EntityGraph для оптимизации?

`@EntityGraph` — стандартный (JPA 2.1) способ декларативно сказать, какие ассоциации подтянуть жадно **для конкретного запроса**, не меняя глобальный `FetchType` в маппинге. Это основной инструмент борьбы с N+1, когда `JOIN FETCH` хочется вынести из текста запроса.

Граф можно задать тремя способами: именованный (`@NamedEntityGraph` на entity), ad-hoc (`attributePaths` прямо над методом репозитория) и программный (`em.createEntityGraph`).

```java
// Именованный EntityGraph
@Entity
@NamedEntityGraph(
    name = "Order.withItems",
    attributeNodes = {
        @NamedAttributeNode("items"),
        @NamedAttributeNode("customer")
    }
)
class Order { ... }

// Использование
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph("Order.withItems")
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"items", "customer"})  // ad-hoc
    List<Order> findByStatus(OrderStatus status);
}
```

```java
// Программно
EntityGraph<Order> graph = em.createEntityGraph(Order.class);
graph.addAttributeNodes("items", "customer");

Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
Order order = em.find(Order.class, 1L, hints);
```

**Компромисс EntityGraph vs JOIN FETCH:** оба дают один SQL без N+1. `@EntityGraph` декларативен и переиспользуется на нескольких методах, но не даёт фильтровать по полям связи. `JOIN FETCH` пишется инлайн в JPQL — гибче (можно добавить `WHERE` по join'у), но привязан к конкретному запросу.

## Q12. Как работает @ElementCollection?

`@ElementCollection` маппит коллекцию **значений** (примитивы вроде `String`/`Integer` или `@Embeddable`), у которых нет собственной identity и своего PK. Hibernate хранит их в отдельной таблице (`@CollectionTable`), привязанной FK к владельцу, и управляет ими целиком как частью владельца.

Ключевое отличие от `@OneToMany`: элементы не являются entity. У них нет id, на них нельзя сослаться извне, а при изменении коллекции Hibernate часто просто удаляет и пересоздаёт все строки.

```java
@Entity
class User {
    @Id Long id;

    // Простые значения — отдельная таблица
    @ElementCollection
    @CollectionTable(name = "user_emails",
                      joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "email")
    Set<String> emails = new HashSet<>();

    // Embeddable
    @ElementCollection
    @CollectionTable(name = "user_addresses",
                      joinColumns = @JoinColumn(name = "user_id"))
    List<Address> addresses = new ArrayList<>();
}

@Embeddable
class Address {
    String street;
    String city;
    String country;
}
```

**Сценарий применения:** коллекции значений, целиком принадлежащие владельцу (теги, e-mail'ы, адреса). Как только элементу нужна identity — на него ссылаются другие сущности, у него свой жизненный цикл или его надо запрашивать напрямую — это уже `@OneToMany` с полноценной entity.

## Q13. Какие типичные ошибки при работе с связями?

Пять самых частых граблей, на которых сыпется большинство ORM-проектов:

1. **Лишний EAGER-fetch.** `@ManyToOne`/`@OneToOne` по умолчанию EAGER — связь грузится всегда, даже когда не нужна, и тянет за собой лишние JOIN'ы. Ставьте `LAZY` явно:

```java
// ПЛОХО
@ManyToOne  // default EAGER!
Customer customer;

// ХОРОШО
@ManyToOne(fetch = FetchType.LAZY)
Customer customer;
```

2. **Забытая синхронизация bidirectional.** Выставили только одну сторону — вторая в памяти рассинхронизирована с БД, а коллекция в текущей сессии содержит неверные данные. Спасают helper-методы (см. Q2):

```java
// ПЛОХО — только одна сторона знает
order.setCustomer(customer);  // но customer.orders не содержит order

// ХОРОШО — helper метод
order.assignToCustomer(customer);  // устанавливает обе стороны
```

3. **MultipleBagFetchException** — попытка `JOIN FETCH` сразу двух `List`-коллекций. Hibernate не умеет материализовать декартово произведение двух bag. Лечится переходом на `Set` либо разбиением на два запроса:

```java
// ОШИБКА
@Query("FROM Order o JOIN FETCH o.items JOIN FETCH o.shipments")
List<Order> findAll();  // две List → конфликт

// РЕШЕНИЕ 1: использовать Set
@OneToMany Set<OrderItem> items;
@OneToMany Set<Shipment> shipments;

// РЕШЕНИЕ 2: два отдельных запроса
List<Order> withItems = repo.findAllWithItems();
```

4. **@ManyToMany там, где связи нужны атрибуты.** Удаление через `remove` чистит junction-строку, и любые данные связи теряются. Если у связи есть поля (`enrolledAt`, `grade`) — нужна промежуточная entity (см. Q7):

```java
// Потеря полей (enrolledAt, grade)
student.getCourses().remove(course);  // удаляет из junction
```

5. **Некорректный equals/hashCode для entity в Set.** `@GeneratedValue`-id равен `null` до сохранения и появляется после — если хэш зависит от него, объект «теряется» в `HashSet` после `save`. Считайте равенство по стабильному ключу (natural ID/UUID), а `hashCode` делайте константным или по natural ID:

```java
// ПЛОХО — ломает контракт hashCode (меняется после save)
@EqualsAndHashCode  // Lombok по всем полям
class User { @Id @GeneratedValue Long id; ... }

// ХОРОШО — по natural ID или UUID
class User {
    @Id @GeneratedValue Long id;

    @Override
    public boolean equals(Object o) {
        return o instanceof User u && Objects.equals(id, u.id);
    }
    @Override
    public int hashCode() { return 31; }  // или hashCode natural ID
}
```

## Q14. Как тестировать связи между entities?

Связи тестируют через `@DataJpaTest` (поднимает урезанный JPA-контекст с реальной БД/Testcontainers). Ключевой приём — `em.flush()` + `em.clear()` между записью и чтением: это сбрасывает изменения в БД и очищает кэш первого уровня (L1), чтобы следующий `findById` реально сходил в базу, а не вернул тот же объект из памяти. Без `clear()` тест на ленивую загрузку или orphanRemoval будет проходить ложно.

Три показательных сценария:

- **save + lazy-чтение** — после `flush/clear` коллекция действительно подгружается из БД.
- **orphanRemoval** — `clear()` коллекции должен удалить детей из таблицы.
- **детект N+1** — через Hibernate `Statistics` проверяем число подготовленных запросов.

```java
@DataJpaTest
class OrderRepositoryTest {
    @Autowired OrderRepository repository;
    @Autowired TestEntityManager em;

    @Test
    void shouldSaveOrderWithItems() {
        Order order = new Order(...);
        order.addItem(new OrderItem(...));
        order.addItem(new OrderItem(...));

        Order saved = repository.save(order);
        em.flush();
        em.clear();   // сбросить L1 cache

        Order loaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getItems()).hasSize(2);  // items загружены lazy
    }

    @Test
    void shouldRemoveOrphanItems() {
        Order order = new Order(...);
        order.addItem(new OrderItem(...));
        repository.save(order);
        em.flush();

        order.getItems().clear();  // orphanRemoval удалит items
        repository.save(order);
        em.flush();

        assertThat(em.getEntityManager()
            .createQuery("FROM OrderItem", OrderItem.class)
            .getResultList()).isEmpty();
    }

    @Test
    void shouldDetectNPlusOne() {
        // Arrange — 5 orders по 3 items
        for (int i = 0; i < 5; i++) {
            Order o = new Order(...);
            for (int j = 0; j < 3; j++) o.addItem(new OrderItem(...));
            em.persist(o);
        }
        em.flush();
        em.clear();

        // Act — с Hibernate Statistics
        Statistics stats = em.getEntityManager().getEntityManagerFactory()
            .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        List<Order> orders = repository.findAll();
        orders.forEach(o -> o.getItems().size());

        // Assert — ожидаем 1 (orders) + 5 (items), не 1+5 для N+1
        assertThat(stats.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }
}
```

## Q15. Какие best practices при работе с отношениями?

Сводный чек-лист — большинство пунктов раскрыты в предыдущих вопросах, здесь они собраны в одно место:

**Fetch и производительность:**

1. **Всегда `LAZY` для всех связей** — добирайте данные адресно в конкретных запросах, а не глобально в маппинге (Q3).
2. **`@EntityGraph` / `JOIN FETCH`** там, где связь действительно нужна, — чтобы убрать N+1 (Q4, Q11).
3. **Hibernate Statistics в проде** — мониторинг числа запросов и раннее обнаружение N+1.

**Маппинг и направление связи:**

4. **Bidirectional → helper-методы** для синхронизации обеих сторон в памяти (Q2).
5. **`mappedBy` на inverse-стороне, `@JoinColumn` на owning-стороне** — иначе лишняя колонка/таблица (Q2).
6. **`Set` предпочтительнее `List`** для bidirectional-коллекций — нет `MultipleBagFetchException` (Q8).
7. **Избегайте `@ManyToMany` с атрибутами связи** — заводите промежуточную entity (Q7).

**Жизненный цикл и идентичность:**

8. **`orphanRemoval = true`** для строго владеемых детей, у которых нет жизни без родителя (Q6).
9. **Не вешайте `CascadeType.ALL` по умолчанию** — перечисляйте только нужные операции (Q5).
10. **`equals`/`hashCode` по стабильному ID, не по всем полям** — иначе entity «теряется» в `Set` после save (Q13).

## See also

- [Hibernate](hibernate-interview.md) — основы Hibernate, Session, entity states
- [Hibernate Caching](hibernate-caching-interview.md) — L1/L2 cache, как relationships взаимодействуют с кэшем
- [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) — JPA repositories, JPQL, @EntityGraph
- [Spring Data JDBC](../frameworks/spring/spring-data-jdbc-interview.md) — альтернатива без ORM магии
- [Database Performance](../performance/database-performance-interview.md) — производительность БД
- [SQL](sql-interview.md) — SQL основы, JOIN типы
- [PostgreSQL](postgresql-interview.md) — типичная БД для Hibernate
- [Database Transactions](database-transactions-interview.md) — транзакции и видимость изменений
- [Spring @Transactional](../frameworks/spring/spring-transaction-interview.md) — управление транзакциями в Spring
- [Domain-Driven Design](../architecture/ddd-interview.md) — aggregate boundaries и relationships
