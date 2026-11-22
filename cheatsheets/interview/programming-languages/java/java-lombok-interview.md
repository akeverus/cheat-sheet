---
title: "Вопросы на собеседовании: Lombok"
description: "Project Lombok — аннотационный процессор для устранения boilerplate: @Data, @Builder, @Value, @Slf4j, конфигурация, интеграция с JPA и MapStruct"
tags:
  - interview
  - java
  - java-lombok-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Lombok"
  - "Lombok interview"
  - "Lombok собеседование"
prerequisites:
  - "[[java-lombok]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Lombok`

`Project Lombok` — аннотационный процессор, который во время компиляции генерирует рутинный код: геттеры, сеттеры, equals/hashCode, toString, конструкторы, builder. Убирает boilerplate, но требует понимания того, что именно генерируется.

## Полезные ссылки

### Официальная документация

- [Project Lombok Official](https://projectlombok.org/) — полная документация по всем аннотациям
- [Lombok Feature List](https://projectlombok.org/features/) — список возможностей

### Baeldung tutorials

- [Introduction to Project Lombok](https://www.baeldung.com/intro-to-project-lombok) — основы
- [Lombok @Builder](https://www.baeldung.com/lombok-builder) — builder pattern
- [Lombok Constructor Annotations](https://www.baeldung.com/java-lombok-constructor-annotations-comparison)
- [Lombok @With](https://www.baeldung.com/lombok-with-annotations)
- [Lombok Configuration System](https://www.baeldung.com/lombok-configuration-system)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. Что такое `Lombok` и как он работает на этапе компиляции?](#q1)
- [Q2. Как подключить `Lombok` к проекту на Maven и Gradle?](#q2)
- [Q3. Что генерируют `@Getter` и `@Setter` и как управлять видимостью?](#q3)
- [Q4. Что делает `@ToString` и как настроить вывод полей?](#q4)
- [Q5. Что генерирует `@EqualsAndHashCode` и какие у него риски?](#q5)

**Конструкторы**
- [Q6. Чем отличаются `@NoArgsConstructor`, `@RequiredArgsConstructor` и `@AllArgsConstructor`?](#q6)
- [Q7. Что делает `@NonNull` в `Lombok` и где он применяется?](#q7)

**Комплексные аннотации**
- [Q8. Что именно генерирует комбинированная аннотация `@Data`?](#q8)
- [Q9. Чем `@Value` отличается от `@Data`?](#q9)
- [Q10. Что такое `@Accessors` и зачем он нужен?](#q10)

**Builder**
- [Q11. Как работает `@Builder` и что он генерирует?](#q11)
- [Q12. Что такое `@Builder.Default` и зачем он нужен?](#q12)
- [Q13. Что такое `@Singular` в `@Builder`?](#q13)
- [Q14. Что такое `@SuperBuilder` и зачем он нужен?](#q14)
- [Q15. Что такое `@With` и какую задачу он решает?](#q15)

**Логирование и утилиты**
- [Q16. Какие аннотации логирования есть в `Lombok` и что они генерируют?](#q16)
- [Q17. Что делает `@Cleanup` и нужен ли он в современной Java?](#q17)
- [Q18. Что делает `@SneakyThrows` и когда его стоит и не стоит использовать?](#q18)
- [Q19. Что такое `@Delegate` и как он работает?](#q19)
- [Q20. Что такое `@FieldNameConstants` и где он полезен?](#q20)

**Конфигурация и интеграция**
- [Q21. Что такое `lombok.config` и как его использовать?](#q21)
- [Q22. Какие проблемы возникают у `Lombok` с JPA-сущностями?](#q22)
- [Q23. Как `Lombok` интегрируется с `MapStruct` и в чём подвох?](#q23)
- [Q24. Что такое `delombok` и зачем он нужен?](#q24)
- [Q25. Каковы основная критика и ограничения `Lombok`?](#q25)
- [Q26. Как работают `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`?](#q26)
- [Q27. Как `Lombok` обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`?](#q27)

---

<a id="q1"></a>

## Q1. Что такое `Lombok` и как он работает на этапе компиляции?

`Project Lombok` — аннотационный процессор Java (JSR 269), работающий на этапе компиляции: по аннотациям он генерирует рутинный код (геттеры, сеттеры, `equals`/`hashCode`, `toString`, конструкторы, builder), чтобы его не писать руками.

Ключевая особенность — **как** он это делает. Обычные annotation processors (MapStruct, Dagger) умеют только **создавать новые файлы**, но не трогают исходный класс. Lombok идёт дальше: он встраивается в AST (Abstract Syntax Tree) компилятора через internal API и **модифицирует узлы дерева уже существующего класса** — дописывает методы прямо в `.class` без промежуточного `.java`.

```
javac → читает @Data на классе → Lombok дополняет AST → javac компилирует расширенный AST
```

Работа с внутренними API компилятора требует проверять совместимость Lombok при обновлении JDK. Поддержка в IDE зависит от её версии и настройки обработки аннотаций; отдельный плагин нужен не во всех версиях IntelliJ IDEA.

**Что нужно для работы:**
- Зависимость во время компиляции: Gradle `compileOnly`, Maven `provided`.
- В Gradle — `annotationProcessor`, в Maven — путь процессора в `maven-compiler-plugin`. Для JDK 23+ явное включение обработки обязательно.
- Проверка поддержки Lombok в используемой IDE.

**Итог:** Lombok — это AST-трансформер, а не обычный генератор новых файлов. Отсюда и его сила (нулевой boilerplate), и его слабости (зависимость от IDE и internal API).

<a id="q2"></a>

## Q2. Как подключить `Lombok` к проекту на Maven и Gradle?

Lombok нужен для компиляции исходников, а в runtime обычно не нужен. Gradle использует `compileOnly` и `annotationProcessor`; Maven — `provided` и путь процессора в `maven-compiler-plugin`. Начиная с JDK 23 javac не запускает обработку только по наличию процессора в classpath. Для модульной сборки явная настройка нужна и на более ранних JDK.

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    // для тестов — опционально:
    testCompileOnly("org.projectlombok:lombok:1.18.48")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.48")
}
```

**Maven:**

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.48</version>
    <scope>provided</scope>  <!-- нужен при компиляции, не в runtime -->
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.48</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

В Maven `provided` выражает потребность компиляции без runtime-зависимости. Версию можно брать из согласованного dependency management, но саму обработку аннотаций нужно настроить отдельно.

<a id="q3"></a>

## Q3. Что генерируют `@Getter` и `@Setter` и как управлять видимостью?

`@Getter` генерирует геттер `getFieldName()` для каждого поля (для `boolean` — `isFieldName()`, по JavaBeans-конвенции), `@Setter` — сеттер `setFieldName(Type value)`. Навешивать их можно либо на класс (тогда они применяются ко всем полям), либо на отдельное поле.

```java
@Getter
@Setter
public class User {
    private String name;
    private boolean active;
    @Setter(AccessLevel.NONE)  // сеттер не генерируется для этого поля
    private String id;
}
// Генерирует: getName(), setName(), isActive(), setActive()
// id: только getId(), без setId()
```

**AccessLevel:**

| Уровень | Модификатор |
|---|---|
| `PUBLIC` (default) | `public` |
| `PROTECTED` | `protected` |
| `PACKAGE` | package-private |
| `PRIVATE` | `private` |
| `NONE` | метод не генерируется |

`AccessLevel.NONE` — частый приём: ставите `@Getter`/`@Setter` на весь класс, а для одного поля точечно отключаете нежелательный аксессор (как `setId` в примере выше).

<a id="q4"></a>

## Q4. Что делает `@ToString` и как настроить вывод полей?

`@ToString` генерирует `toString()`, который печатает имя класса, а затем имена и значения всех нестатических полей. Вывод настраивается двумя способами: атрибутами на уровне класса (`exclude`, `callSuper`, `onlyExplicitlyIncluded`) или точечными аннотациями на полях (`@ToString.Include` / `@ToString.Exclude`).

```java
@ToString(
    exclude = {"password"},          // исключить поля
    callSuper = true,               // включить super.toString()
    onlyExplicitlyIncluded = true   // включать только @ToString.Include
)
public class User {
    @ToString.Include private String name;
    @ToString.Include(rank = 1) private String email;  // rank определяет порядок
    private String password;  // не попадёт
}
```

Или точечно на поле:

```java
public class Order {
    private String id;
    @ToString.Exclude private BigDecimal internalCost;  // скрыть
}
```

**Подводный камень:** по умолчанию `callSuper = false`, то есть `super.toString()` не вызывается. В иерархии классов это значит, что поля родителя в выводе не появятся — для полной картины ставьте `callSuper = true`. Отдельно стоит исключать чувствительные данные (пароли, токены) через `exclude` или `@ToString.Exclude`, чтобы они не утекли в логи.

<a id="q5"></a>

## Q5. Что генерирует `@EqualsAndHashCode` и какие у него риски?

`@EqualsAndHashCode` генерирует `equals()` и `hashCode()` на основе нестатических нетранзиентных полей. Главное — выбрать поля с учётом их изменяемости, наследования и жизненного цикла объекта.

```java
@EqualsAndHashCode(
    exclude = {"createdAt"},   // исключить из сравнения
    callSuper = true           // включить поля родителя
)
public class Product extends BaseEntity {
    private String sku;
    private String name;
    private LocalDateTime createdAt;
}
```

**Риск 1 — изменяемые поля в HashMap/HashSet:** `hashCode` зависит от участвующих полей. Если объект уже лежит в коллекции, а вы меняете такое поле, его хеш может измениться и поиск по объекту перестанет работать.

**Риск 2 — JPA-сущности:** если `equals/hashCode` строится по `id`, то до сохранения `id == null`, то два разных новых объекта могут оказаться равными, если сравнение основано только на `id`. После присвоения `id` меняется hash, и объект в `HashSet` может перестать находиться (подробнее в Q22).

**Риск 3 — наследование:** по умолчанию `callSuper = false`, поэтому в подклассе `equals` игнорирует поля родителя — два объекта с разным `id` родителя сочтутся равными. В иерархиях состав равенства нужно выбирать осознанно: `callSuper = true` уместен, только если реализация родителя согласована с контрактом равенства.

<a id="q6"></a>

## Q6. Чем отличаются `@NoArgsConstructor`, `@RequiredArgsConstructor` и `@AllArgsConstructor`?

Все три генерируют конструктор, разница — в наборе полей, которые в него попадают: ноль, только обязательные или все.

| Аннотация | Что генерирует | Когда используют |
|---|---|---|
| `@NoArgsConstructor` | Конструктор без аргументов | JPA (нужна допустимая видимость), десериализация Jackson |
| `@RequiredArgsConstructor` | Конструктор для неинициализированных `final` и `@NonNull` полей | Внедрение зависимостей через конструктор |
| `@AllArgsConstructor` | Конструктор для всех полей | Редко — хрупкий (порядок аргументов) |

```java
@RequiredArgsConstructor  // любимый в Spring Boot
public class OrderService {
    private final OrderRepository repository;  // войдёт в конструктор
    private final EventPublisher publisher;    // войдёт в конструктор
    private String prefix = "ORD";             // не final → не войдёт
}
```

**Почему `@RequiredArgsConstructor` используют в Spring:** «required» — это неинициализированные поля `final` и `@NonNull`. Объявляете зависимости как `private final`, и Lombok генерирует конструктор с ними. `@AllArgsConstructor` включает и другие поля: добавление поля меняет сигнатуру и может сломать вызовы по позиции аргументов.

<a id="q7"></a>

## Q7. Что делает `@NonNull` в `Lombok` и где он применяется?

`@NonNull` заставляет Lombok вставить проверку на `null`: если значение оказывается `null`, бросается `NullPointerException` с понятным сообщением. Работает в двух местах — на параметре метода (проверка в начале тела) и на поле (проверка внутри сгенерированного конструктора/сеттера).

```java
public class UserService {

    public User create(@NonNull String name) {
        // Lombok вставит: if (name == null) throw new NullPointerException("name is marked @NonNull...")
        return new User(name);
    }
}
```

На поле в классе с `@RequiredArgsConstructor`:

```java
@RequiredArgsConstructor
public class OrderMapper {
    @NonNull private final UserMapper userMapper;  // → войдёт в конструктор с null-check
}
```

**Частая путаница:** `lombok.NonNull`, JSR 305 `javax.annotation.Nonnull` и JetBrains `@NotNull` не полностью взаимозаменяемы. На поле некоторые чужие аннотации могут служить сигналом для проверки внутри метода, который Lombok сам генерирует. Для параметра метода явная обработка определяется аннотацией `lombok.NonNull`. Проверяйте импорт и место применения.

<a id="q8"></a>

## Q8. Что именно генерирует комбинированная аннотация `@Data`?

`@Data` — это «всё включено» одной аннотацией: она объединяет сразу пять других, превращая обычный класс в полноценный изменяемый POJO с аксессорами, `equals`/`hashCode`, `toString` и конструктором по обязательным полям.

```text
@Data = @Getter + @Setter + @RequiredArgsConstructor + @ToString + @EqualsAndHashCode
```

```java
@Data
public class Address {
    @NonNull private final String street;  // final → только в @RequiredArgsConstructor
    private String city;                   // mutable, геттер + сеттер
}
```

**Типичные грабли с `@Data`:**
- Включает `@EqualsAndHashCode` — отсюда проблемы с JPA (см. Q22).
- Генерирует сеттеры для подходящих не-`final` полей — такой класс может оказаться более изменяемым, чем требуется.
- Не принимает `callSuper` напрямую: в иерархии приходится отдельно добавлять `@EqualsAndHashCode(callSuper = true)`.

**Сценарий применения:** `@Data` удобен для простых DTO/POJO, но опасен для JPA-сущностей — там лучше точечные `@Getter`/`@Setter`/`@ToString` (см. Q22).

<a id="q9"></a>

## Q9. Чем `@Value` отличается от `@Data`?

`@Value` создаёт класс с `final`-полями и без сеттеров, в отличие от изменяемого `@Data`. Это поверхностная неизменяемость: если поле указывает на изменяемую коллекцию, её содержимое может измениться. Сам класс по умолчанию тоже становится `final`.

```text
@Value = @Getter + @AllArgsConstructor + @ToString + @EqualsAndHashCode + все поля final
```

```java
@Value
public class Money {
    BigDecimal amount;
    String currency;
}
// Эквивалентно: все поля private final, только геттеры (нет сеттеров), конструктор со всеми полями
```

| Параметр | `@Data` | `@Value` |
|---|---|---|
| Поля | mutable | `private final` |
| Конструктор | `@RequiredArgsConstructor` | `@AllArgsConstructor` |
| Сеттеры | есть | нет |
| Изменяемость | меняется через сеттеры | поверхностно неизменяем |

**Сценарий применения:** Value Objects в DDD, неизменяемые DTO-ответы, record-подобные классы. По сути `@Value` закрывал нишу `record` до его появления в Java 16 — в новых проектах для чисто-данных-классов часто проще взять `record`.

<a id="q10"></a>

## Q10. Что такое `@Accessors` и зачем он нужен?

`@Accessors` меняет **стиль** сгенерированных геттеров/сеттеров — отступает от стандартной JavaBeans-конвенции `getX()`/`setX()`. Два главных режима: `chain` (сеттеры возвращают `this`, можно вызывать цепочкой) и `fluent` (имена без префиксов `get`/`set`, например `name()` вместо `getName()`).

```java
@Data
@Accessors(chain = true)  // сеттеры возвращают this (fluent API)
public class Builder {
    private String name;
    private int value;
}

// Использование:
new Builder().setName("foo").setValue(42);  // chain!
```

```java
@Getter
@Accessors(fluent = true)  // без get/set префиксов
public class Config {
    private String host;
    private int port;
}
// config.host() вместо config.getHost()
```

**Параметры:**

| Параметр | Поведение |
|---|---|
| `chain = true` | сеттер возвращает `this` |
| `fluent = true` | без `get`/`set` префиксов (implies chain) |
| `prefix = {"m_"}` | убирает префикс из имён геттеров/сеттеров |

**Подводный камень:** `fluent = true` ломает совместимость с Jackson, JPA и другими фреймворками, которые находят свойства именно по конвенции `getXxx()`/`setXxx()`. Без префиксов они не распознают поля. Поэтому fluent-стиль уместен во внутренних data-классах, но опасен на DTO, которые сериализуются или маппятся в БД.

<a id="q11"></a>

## Q11. Как работает `@Builder` и что он генерирует?

`@Builder` реализует паттерн Builder: генерирует вложенный класс-строитель и статический метод `builder()`, через которые объект собирается пошагово, по одному полю за вызов. Это решает проблему «телескопических конструкторов» и делает создание объекта со многими полями читаемым.

```java
@Builder
public class Order {
    private String id;
    private String customerId;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
}

// Использование:
Order order = Order.builder()
    .id("ORD-001")
    .customerId("CUST-123")
    .items(List.of(item1, item2))
    .build();
```

Что именно генерируется:
- `public static OrderBuilder builder()` — статический фабричный метод, точка входа.
- Вложенный класс `OrderBuilder` с полями и fluent-сеттерами (каждый возвращает `this`).
- `Order build()` — вызывает цель, указанную местом аннотации. Для `@Builder` на классе это конструктор с полями; аннотация на методе или конструкторе использует именно его.
- `toBuilder()` — только если указан `@Builder(toBuilder = true)`: копирует текущий объект обратно в builder, удобно для «изменить пару полей и пересобрать».

**Сценарий применения:** `@Builder` можно сочетать с `@Value`, чтобы пошагово собирать объект с поверхностно неизменяемыми полями.

<a id="q12"></a>

## Q12. Что такое `@Builder.Default` и зачем он нужен?

`@Builder.Default` сохраняет значение по умолчанию из объявления поля. Без него `@Builder` молча **игнорирует** инициализаторы полей: если builder не вызвал соответствующий сеттер, поле получит `null`/`0`/`false`, а не написанное вами значение. Это один из самых частых багов с Lombok.

```java
@Builder
public class Config {
    private int timeout = 30;         // игнорируется! builder вернёт 0
    private List<String> hosts;       // будет null
}
```

Решение — `@Builder.Default`:

```java
@Builder
public class Config {
    @Builder.Default
    private int timeout = 30;          // теперь default = 30

    @Builder.Default
    private List<String> hosts = new ArrayList<>();  // default = пустой список
}
```

`@Builder.Default` не меняет публичную сигнатуру конструктора. Сгенерированные Lombok конструкторы используют такие default-значения, а явно написанный конструктор должен устанавливать их сам либо делегировать сгенерированному.

<a id="q13"></a>

## Q13. Что такое `@Singular` в `@Builder`?

`@Singular` ставится на поле-коллекцию внутри `@Builder` и добавляет метод для вставки элементов **по одному**, вместо того чтобы передавать всю коллекцию целиком. Lombok при этом сам подбирает имя метода в единственном числе (или вы задаёте его явно).

```java
@Builder
public class Report {
    @Singular private List<String> errors;
    @Singular("metric") private Set<Metric> metrics;  // единственное число для метода
}

// Использование:
Report report = Report.builder()
    .error("field required")   // Lombok генерирует метод error() (singular)
    .error("email invalid")
    .metric(new Metric("cpu", 0.8))
    .build();
```

**Важный нюанс:** `build()` выдаёт неизменяемую коллекцию. Конкретная реализация зависит от типа и числа элементов; полагаться на `Collections.unmodifiableList(...)` нельзя. После `build()` в билдер можно добавить элементы и собрать другой объект, не меняя уже собранный.

<a id="q14"></a>

## Q14. Что такое `@SuperBuilder` и зачем он нужен?

`@SuperBuilder` — это builder, понимающий наследование. Проблема обычного `@Builder` в том, что он строится только по полям самого класса: у дочернего builder'а нет методов для полей родителя. `@SuperBuilder` собирает в один builder поля всей иерархии:

```java
@SuperBuilder
public class Animal {
    private String name;
    private int age;
}

@SuperBuilder
public class Dog extends Animal {
    private String breed;
}

// Использование:
Dog dog = Dog.builder()
    .name("Rex")    // поле из Animal
    .age(3)         // поле из Animal
    .breed("Husky") // поле из Dog
    .build();
```

**Ограничения:**
- Все классы в иерархии должны иметь `@SuperBuilder`
- Не совместим с обычным `@Builder` в той же иерархии
- Начиная с Lombok 1.18.48 аннотация `@SuperBuilder` находится в основном пакете `lombok`; в прежних версиях она была в `lombok.experimental`.

<a id="q15"></a>

## Q15. Что такое `@With` и какую задачу он решает?

`@With` генерирует методы `withFieldName(value)` — создают **копию объекта** с одним изменённым полем (функциональный стиль):

В отличие от обычного сеттера с `void`, метод `withX` не меняет поле текущего объекта. Он использует конструктор для всех полей, возвращает новый объект с отличающимся значением и сохраняет исходный. Для класса с `final`-полями это удобный способ получить новое значение без изменения исходной ссылки.

```java
@Value
@With
public class User {
    String name;
    String email;
    boolean active;
}

User alice = new User("Alice", "alice@mail.com", true);
User bob = alice.withName("Bob").withEmail("bob@mail.com");
// alice не изменился — создан новый объект

User deactivated = alice.withActive(false);
```

**Сценарий применения:**
- Иммутабельные доменные объекты, когда нужно «то же самое, но с одним изменённым полем».
- Event sourcing и пошаговые трансформации значений.
- Паттерн copy-on-write.

`@With` опирается на конструктор, принимающий все поля. Если передать то же значение, сгенерированный метод может вернуть `this`, поэтому новый экземпляр не гарантирован. Особенно полезен он с `@Value`: изменение поля выражается через новое значение объекта. В Java 14+ ту же задачу частично решают `record` (хотя у них нет встроенного `withX`, его пишут руками).

<a id="q16"></a>

## Q16. Какие аннотации логирования есть в `Lombok` и что они генерируют?

Lombok умеет генерировать готовое поле логгера, чтобы не писать вручную `private static final Logger log = LoggerFactory.getLogger(...)`. Аннотация выбирает API логирования. По умолчанию поле называется `log` и объявляется `private static final`; имя и статичность можно изменить в `lombok.config`:

```java
@Slf4j   // → SLF4J Logger
@Log4j2  // → Log4j2 Logger
@Log     // → java.util.logging.Logger
@CommonsLog // → Apache Commons Logging
@JBossLog   // → JBoss Logger
@Flogger    // → Google Flogger
public class OrderService {
    public void process(Order order) {
        log.info("Processing order: {}", order.getId());  // используем log
    }
}
```

**Рекомендация:** в большинстве проектов берут `@Slf4j`. SLF4J — это фасад (абстракция над логированием), а конкретная реализация (Logback, Log4j2) подключается отдельно — поэтому привязки к конкретной библиотеке в коде нет.

```java
// Что генерирует @Slf4j:
private static final org.slf4j.Logger log =
    org.slf4j.LoggerFactory.getLogger(OrderService.class);
```

<a id="q17"></a>

## Q17. Что делает `@Cleanup` и нужен ли он в современной Java?

`@Cleanup` гарантирует освобождение ресурса: Lombok оборачивает переменную в `try-finally` и автоматически вызывает её `.close()` при выходе из метода — в том числе при исключении.

```java
public void readFile(String path) throws IOException {
    @Cleanup InputStream in = new FileInputStream(path);
    @Cleanup OutputStream out = new FileOutputStream(path + ".copy");
    // ... работа с потоками
    // Lombok добавит try-finally с in.close() и out.close()
}
```

Для `AutoCloseable` обычно проще стандартный `try-with-resources`. Семантика исключений отличается: если и основной код, и `close()` выбросят исключение, в сгенерированном `finally` исходное исключение может быть скрыто исключением закрытия. `try-with-resources` сохраняет его как основное, а исключение закрытия — как suppressed.

<a id="q18"></a>

## Q18. Что делает `@SneakyThrows` и когда его стоит и не стоит использовать?

`@SneakyThrows` позволяет бросать checked exception, не объявляя его в `throws` и не оборачивая в `RuntimeException`. Фокус в том, что Lombok обманывает **компилятор**, а не JVM: на этапе компиляции исключение «прячется» через generics-трюк (`Lombok.sneakyThrow()` использует erasure), но в runtime летит оригинальное checked-исключение.

```java
@SneakyThrows(IOException.class)
public void readFile(String path) {
    Files.readAllBytes(Paths.get(path));  // throws IOException — не нужно объявлять
}
```

**Когда уместно:**
- Реализация интерфейса, в сигнатуре которого нет `throws` (например, `Runnable.run()`).
- Лямбды и stream-операции, не принимающие checked-исключения.

**Когда лучше не использовать:**
- В public API — потребитель не увидит в сигнатуре, что метод кидает checked-исключение, и не подготовится его ловить.
- Когда исключение реально нужно обработать — `@SneakyThrows` маскирует его и провоцирует «проглатывание» ошибок.
- В большинстве обычных случаев — честнее явный `throws` или осознанная обёртка в `RuntimeException`.

<a id="q19"></a>

## Q19. Что такое `@Delegate` и как он работает?

`@Delegate` реализует паттерн делегирования: для поля он генерирует методы его типа, которые просто проксируют вызовы в это поле. Это способ «расширить» класс поведением другого объекта через композицию, не наследуясь от него и не выписывая методы-обёртки руками.

```java
public class UserService {

    @Delegate
    private final UserRepository repository;

    // Lombok сгенерирует все методы UserRepository,
    // делегирующие к repository.method(...)
}

// Вместо написания:
public User findById(Long id) { return repository.findById(id); }
public void save(User user) { repository.save(user); }
// ... и т.д.
```

Можно исключить методы через `@Delegate(excludes = SomeInterface.class)`.

`@Delegate` остаётся экспериментальной функцией: сложные случаи наследования и стирания generics требуют проверки сгенерированного API. Аннотацию можно поставить на поле или метод без аргументов; тип результата должен быть известен при компиляции.

<a id="q20"></a>

## Q20. Что такое `@FieldNameConstants` и где он полезен?

`@FieldNameConstants` по умолчанию генерирует вложенный класс `Fields` со строковыми константами для нестатических нетранзиентных полей. С `asEnum = true` вместо класса создаётся enum. Смысл: вместо строкового литерала `"name"` вы ссылаетесь на `User.Fields.name`; после переименования поля ссылка на старую константу вызовет ошибку компиляции.

```java
@FieldNameConstants
public class User {
    private String name;
    private String email;
    private int age;
}

// Генерирует:
public static final class Fields {
    public static final String name = "name";
    public static final String email = "email";
    public static final String age = "age";
}

// Использование (например в JPA Criteria или QueryDSL):
criteriaBuilder.equal(root.get(User.Fields.name), "Alice");
```

**Сценарий применения:** JPA Criteria API, QueryDSL, Spring Data Specifications — везде, где иначе пришлось бы хардкодить имена полей строками.

<a id="q21"></a>

## Q21. Что такое `lombok.config` и как его использовать?

`lombok.config` — файл настроек, через который команда задаёт единые правила использования Lombok на весь проект: что запретить, что включить, как назвать сгенерированное. Действует **иерархически**: лежит в корне или в любой директории и применяется к её пакету и всем подпакетам (ближайший файл переопределяет верхний). Файл действует по дереву каталогов, а `config.stopBubbling = true` останавливает поиск настроек выше текущей директории.

```properties
# lombok.config в корне проекта

# Запретить @Data во всём дереве каталогов при таком решении команды
lombok.data.flagUsage = error

# Задать addLombokGeneratedAnnotation для JaCoCo
lombok.addLombokGeneratedAnnotation = true

# Запретить @SneakyThrows (часто нежелательно)
lombok.sneakyThrows.flagUsage = warning

# Изменить имя поля логгера
lombok.log.fieldName = logger

# Не добавлять @SuppressWarnings("all") к сгенерированному коду
lombok.addSuppressWarnings = false
```

`lombok.addLombokGeneratedAnnotation = true` помечает сгенерированный код `@lombok.Generated`; инструменты покрытия, поддерживающие эту аннотацию, могут его исключать.

<a id="q22"></a>

## Q22. Какие проблемы возникают у `Lombok` с JPA-сущностями?

Главная мысль: `@Data` и его части удобны для DTO, но на JPA-сущностях создают сразу несколько ловушек. Корень всех проблем — Lombok ничего не знает про жизненный цикл сущности (новая/managed/detached) и про lazy-связи, а действует механически по полям.

**Проблема 1 — `@EqualsAndHashCode` на основе `id`:**

JPA-сущность до сохранения имеет `id = null`. Если `equals/hashCode` считается по `id`, то:
- Если в равенстве участвует только `id`, разные новые сущности с `null`-id могут оказаться равными
- При добавлении в `HashSet` → только одна запись
- После `persist()` hashCode меняется → объект "теряется" в коллекции

**Решение:** выбрать стабильную бизнес-идентичность, если она есть, или написать `equals/hashCode` с учётом жизненного цикла сущности. Одной настройки `onlyExplicitlyIncluded = true` недостаточно, если выбранное поле тоже изменяется.

**Проблема 2 — `@Data` генерирует двунаправленную toString:**

```java
@Data
class Order {
    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}

@Data
class OrderItem {
    @ManyToOne
    private Order order;
}
// toString() вызывает order.items → каждый item.order → order.items → StackOverflowError!
```

**Решение:** `@ToString(exclude = {"items"})` или `@ToString(exclude = {"order"})`.

**Проблема 3 — `@NoArgsConstructor` с `final`-полями:**

JPA требует публичный или защищённый конструктор без аргументов. Если есть неинициализированные `final`-поля, `@NoArgsConstructor` без `force = true` не сработает. `force = true` задаст им `null`/`0`, поэтому нужно отдельно оценить инварианты сущности.

**Рекомендация:** для JPA-сущностей выбирать аннотации по полям и контрактам модели. Обычно нужны точечные аксессоры и исключение связей из `toString`; равенство и хеш требуют отдельного решения.

<a id="q23"></a>

## Q23. Как `Lombok` интегрируется с `MapStruct` и в чём подвох?

MapStruct должен увидеть членов, которые Lombok добавил в дерево класса. Для Lombok 1.18.16+ документация MapStruct требует отдельный `lombok-mapstruct-binding` на пути процессоров. Связка позволяет MapStruct дождаться изменений Lombok; порядок строк зависимостей сам по себе не заменяет binding.

**Симптом, по которому распознают проблему:** MapStruct не видит поля с `@Getter`/`@Setter` и падает с `No property named "x" exists`.

**Решение (Gradle):**

```kotlin
annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
// Нужны все три процессора; порядок строк не заменяет binding
```

**Решение (Maven):**

```xml
<annotationProcessorPaths>
    <!-- Binding нужен отдельно от Lombok и MapStruct -->
    <path><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId>...</path>
    <path><groupId>org.projectlombok</groupId><artifactId>lombok-mapstruct-binding</artifactId>...</path>
    <path><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId>...</path>
</annotationProcessorPaths>
```

`lombok-mapstruct-binding` — связующий артефакт, а не замена `mapstruct-processor`.

<a id="q24"></a>

## Q24. Что такое `delombok` и зачем он нужен?

`delombok` — инструмент Lombok, который **разворачивает** аннотации в обычный Java-код: на выходе получаются исходники с уже выписанными вручную геттерами, конструкторами, `equals` и т.д. По сути это «отпечатывание» того, что Lombok делает в AST, в реальный `.java`.

```bash
java -jar lombok.jar delombok src/ -d src-delombok/
```

**Зачем нужен:**
- Миграция с Lombok (уход на `record` или ручной `@Builder`).
- Отладка — увидеть, что именно сгенерировал Lombok.
- Генерация Javadoc (javadoc не понимает Lombok-аннотации).
- Статический анализ инструментами, которые не знают про Lombok.
- Code review в проектах, где Lombok запрещён или нежелателен.

В Gradle `delombok` можно подключить как отдельную задачу с Lombok в её classpath; точная настройка зависит от конфигурации проекта.

<a id="q25"></a>

## Q25. Каковы основная критика и ограничения `Lombok`?

Главная претензия к Lombok сводится к одному: он работает «магически» через internal API компилятора. Отсюда вытекают и технические риски, и проблемы для команды.

**Технические:**
- Использует internal compiler API (манипуляция AST) — может сломаться при обновлении JDK.
- Java 17+ sealed classes и `record` частично перекрывают Lombok, но не заменяют полностью.
- Нестандартное поведение процессора → конфликты с некоторыми инструментами сборки и анализа.

**Командные и процессные:**
- Неявная кодогенерация усложняет code review — в diff видны только аннотации, а не реальный код.
- Нужно проверить поддержку Lombok в используемой IDE и включить обработку аннотаций в сборке.
- `@SneakyThrows`, `@Delegate` — спорные фичи, их часто запрещают в code style guide.

**Конкретные ловушки:**
- `@Data` на JPA-сущностях → StackOverflow и сломанный `equals` (см. Q22).
- Явно написанные конструкторы не получают default-значения `@Builder.Default` автоматически.
- `@Accessors(fluent = true)` меняет имена аксессоров; совместимость с Jackson, JPA и MapStruct нужно проверять и при необходимости настраивать.
- `@EqualsAndHashCode` без `callSuper = true` в иерархиях → потеря полей родителя.

**Когда отказаться от Lombok:**
- Публичная библиотека — проверить, не попали ли типы Lombok в публичные сигнатуры и правильно ли задана область зависимости.
- Команда принципиально против «магического» кода.
- Java 16+ — `record` уже покрывает большинство сценариев для DTO.

<a id="q26"></a>

## Q26. Как работают `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`?

Эти аннотации точечно управляют тем, какие поля участвуют в `equals`/`hashCode`. Есть два режима: по умолчанию берутся все поля, и вы лишь исключаете лишние через `.Exclude`; либо включаете `onlyExplicitlyIncluded = true`, и тогда учитываются только поля, помеченные `.Include`, — белый список вместо чёрного.

```java
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @EqualsAndHashCode.Include
    private String sku;         // только sku для equals

    private String name;        // не участвует
    private BigDecimal price;   // не участвует

    @EqualsAndHashCode.Exclude  // при использовании без onlyExplicitlyIncluded
    private String internalCode;
}
```

Полезный приём: `@EqualsAndHashCode.Include` можно ставить не только на поля, но и на **методы** — тогда в сравнении участвует не сырое поле, а результат метода (например, нормализованное значение):

```java
@EqualsAndHashCode.Include
public String normalizedName() {
    return name.toLowerCase().trim();
}
```

<a id="q27"></a>

## Q27. Как `Lombok` обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`?

По умолчанию оба генератора пропускают состояние родителя. Для `equals/hashCode` Lombok предупреждает об этом, если класс имеет родителя, отличного от `Object`. Учитывать ли родительское равенство, нужно решать по контракту иерархии; `@ToString` настраивается отдельно.

```java
@Data
class BaseEntity {
    private Long id;
}

@Data
class Order extends BaseEntity {
    private String ref;
    // Сгенерированный equals смотрит только на ref, игнорирует id!
}
```

Чтобы включить поля родителя:

```java
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
class Order extends BaseEntity {
    private String ref;
}
```

**Предупреждение Lombok:** если `@Data`/`@EqualsAndHashCode` стоит на классе с родителем (не `Object`), Lombok сам выдаёт warning — мол, `callSuper = false` тут подозрителен. Убрать его правильно двумя способами: добавить `callSuper = true` (если поля родителя должны учитываться) или явно написать `@EqualsAndHashCode(callSuper = false)` (если это осознанное решение) — явность гасит предупреждение.

## See also

- [Java Annotations](java-annotations-interview.md) — Lombok реализован через JSR 269 annotation processing, AST трансформация
- [Java Initialization](java-initialization-interview.md) — конструкторы, init-блоки, final поля — всё это затрагивает @Builder, @Value
- [MapStruct](java-mapstruct-interview.md) — интеграция Lombok+MapStruct, порядок annotation processors
- [Jackson](java-jackson-interview.md) — @Accessors(fluent=true) ломает Jackson; @JsonProperty взаимодействие
- [Java Core](java-core-interview.md) — equals/hashCode контракт, который Lombok реализует
- [Java OOP](java-oop-interview.md) — наследование, @SuperBuilder, callSuper в @EqualsAndHashCode
- [Spring Data JPA](../../frameworks/spring/spring-data-jpa-interview.md) — проблемы @Data/@EqualsAndHashCode на JPA-сущностях
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — @RequiredArgsConstructor + final fields = рекомендованный Spring DI
- [Unit Testing](../../testing/unit-testing-interview.md) — lombok.addLombokGeneratedAnnotation для исключения из JaCoCo
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Builder (Q11-Q14), Delegate (@Delegate), Value Object (@Value)
- [Шпаргалка: Lombok: Автоматизация boilerplate кода в](../../../libraries/java/java-lombok.md) — теория
