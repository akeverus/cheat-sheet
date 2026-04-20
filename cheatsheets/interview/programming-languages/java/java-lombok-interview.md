---
title: "Вопросы на собеседовании: Lombok"
description: "Project Lombok — аннотационный процессор для устранения boilerplate: @Data, @Builder, @Value, @Slf4j, конфигурация, интеграция с JPA и MapStruct"
tags:
  - interview
  - java
  - java-lombok-interview
aliases:
  - "Lombok interview"
  - "Lombok собеседование"
  - "Lombok вопросы"
  - "Project Lombok interview"
difficulty: "intermediate"
updated: "2026-04-20"
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
- [Q1. (!) Что такое Lombok и как он работает?](#q1-что-такое-lombok-и-как-он-работает)
- [Q2. Как подключить Lombok к Maven/Gradle-проекту?](#q2-как-подключить-lombok-к-mavengradle-проекту)
- [Q3. Что генерируют `@Getter` и `@Setter`?](#q3-что-генерируют-getter-и-setter)
- [Q4. Что делает `@ToString` и как настроить вывод?](#q4-что-делает-tostring-и-как-настроить-вывод)
- [Q5. Что генерирует `@EqualsAndHashCode` и каковы риски?](#q5-что-генерирует-equalsandhashcode-и-каковы-риски)

**Конструкторы**
- [Q6. (!) Чем отличаются `@NoArgsConstructor`, `@RequiredArgsConstructor` и `@AllArgsConstructor`?](#q6-чем-отличаются-noargsconstructor-requiredargsconstructor-и-allargsconstructor)
- [Q7. Что делает `@NonNull` и где используется?](#q7-что-делает-nonnull-и-где-используется)

**Комплексные аннотации**
- [Q8. (!) Что генерирует `@Data`?](#q8-что-генерирует-data)
- [Q9. Чем `@Value` отличается от `@Data`?](#q9-чем-value-отличается-от-data)
- [Q10. Что такое `@Accessors` и зачем нужен?](#q10-что-такое-accessors-и-зачем-нужен)

**Builder**
- [Q11. (!) Как работает `@Builder`?](#q11-как-работает-builder)
- [Q12. Что такое `@Builder.Default`?](#q12-что-такое-builderdefault)
- [Q13. Что такое `@Singular` в Builder?](#q13-что-такое-singular-в-builder)
- [Q14. (!) Что такое `@SuperBuilder` и зачем нужен?](#q14-что-такое-superbuilder-и-зачем-нужен)
- [Q15. Что такое `@With`?](#q15-что-такое-with)

**Логирование и утилиты**
- [Q16. Какие аннотации логирования есть в Lombok?](#q16-какие-аннотации-логирования-есть-в-lombok)
- [Q17. Что делает `@Cleanup`?](#q17-что-делает-cleanup)
- [Q18. Что делает `@SneakyThrows` и когда его использовать?](#q18-что-делает-sneakythrows-и-когда-его-использовать)
- [Q19. Что такое `@Delegate`?](#q19-что-такое-delegate)
- [Q20. Что такое `@FieldNameConstants`?](#q20-что-такое-fieldnameconstants)

**Конфигурация и интеграция**
- [Q21. Что такое `lombok.config` и как его использовать?](#q21-что-такое-lombokconfig-и-как-его-использовать)
- [Q22. (!) Какие проблемы у Lombok с JPA-сущностями?](#q22-какие-проблемы-у-lombok-с-jpa-сущностями)
- [Q23. Как Lombok интегрируется с MapStruct?](#q23-как-lombok-интегрируется-с-mapstruct)
- [Q24. Что такое `delombok` и зачем он нужен?](#q24-что-такое-delombok-и-зачем-он-нужен)
- [Q25. Каковы критика и ограничения Lombok?](#q25-каковы-критика-и-ограничения-lombok)
- [Q26. Как работает `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`?](#q26-как-работает-equalsandhashcodeinclude--equalsandhashcodeexclude)
- [Q27. Как Lombok обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`?](#q27-как-lombok-обрабатывает-наследование-в-equalsandhashcode-и-tostring)

---

## Q1. Что такое Lombok и как он работает?

`Project Lombok` — аннотационный процессор Java (JSR 269), работающий на этапе компиляции. Он встраивается в AST (Abstract Syntax Tree) компилятора через internal API и **добавляет/модифицирует узлы дерева** — фактически дописывает код в `.class`-файл без создания промежуточного `.java`-файла.

```
javac → читает @Data на классе → Lombok дополняет AST → javac компилирует расширенный AST
```

Это отличает Lombok от стандартных annotation processors (как MapStruct или Dagger), которые только **генерируют новые файлы**. Lombok модифицирует существующий AST — отсюда и некоторые трудности с отладкой и поддержкой IDE.

**Что нужно для работы:**
- Dependency в `compile` scope: `org.projectlombok:lombok`
- Annotation processor: в Maven/Gradle — в `annotationProcessorPaths`
- Плагин IDE (IntelliJ IDEA: «Lombok Plugin», включён по умолчанию с 2020.3)

**Итог:** Lombok работает как AST-трансформер, а не как обычный annotation processor.

## Q2. Как подключить Lombok к Maven/Gradle-проекту?

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    // для тестов — опционально:
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}
```

**Maven:**

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.32</version>
    <optional>true</optional>  <!-- не попадает в transitive deps -->
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.32</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

`optional = true` в Maven важно: Lombok нужен только для компиляции, не для runtime. Если Lombok попадёт в transitive dependencies — потребители вашей библиотеки тоже получат его.

## Q3. Что генерируют `@Getter` и `@Setter`?

`@Getter` — генерирует `getFieldName()` для каждого поля. Для `boolean` генерирует `isFieldName()`.  
`@Setter` — генерирует `setFieldName(Type value)`.

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

Можно навешивать на класс (применяется ко всем полям) или на конкретное поле.

## Q4. Что делает `@ToString` и как настроить вывод?

`@ToString` генерирует `toString()` включая имена и значения всех нестатических полей:

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

**Важно:** `@ToString` с `callSuper = false` (default) не вызывает `super.toString()`. В иерархии классов это может скрыть данные родителя.

## Q5. Что генерирует `@EqualsAndHashCode` и каковы риски?

`@EqualsAndHashCode` генерирует `equals()` и `hashCode()` на основе всех нестатических нетранзиентных полей.

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

**Риск 1 — mutable objects в HashMap/HashSet:** если объект изменить после добавления в коллекцию, он перестаёт находиться по ключу (hash изменился).

**Риск 2 — JPA-сущности:** Lombok генерирует `equals/hashCode` через `id`, но до сохранения `id == null`, а значит все новые сущности "равны" по hashCode. Это ломает коллекции (подробнее в Q22).

**Риск 3 — наследование:** по умолчанию `callSuper = false`. Если подкласс добавляет поля, `equals` игнорирует поля родителя — нужен `callSuper = true`.

## Q6. Чем отличаются `@NoArgsConstructor`, `@RequiredArgsConstructor` и `@AllArgsConstructor`?

| Аннотация | Что генерирует | Когда используют |
|---|---|---|
| `@NoArgsConstructor` | Конструктор без аргументов | JPA (требует), десериализация Jackson |
| `@RequiredArgsConstructor` | Конструктор для `final` и `@NonNull` полей | Spring DI (рекомендован), immutable-like классы |
| `@AllArgsConstructor` | Конструктор для всех полей | Редко — хрупкий (порядок аргументов) |

```java
@RequiredArgsConstructor  // любимый в Spring Boot
public class OrderService {
    private final OrderRepository repository;  // войдёт в конструктор
    private final EventPublisher publisher;    // войдёт в конструктор
    private String prefix = "ORD";             // не final → не войдёт
}
```

`@RequiredArgsConstructor` + `final` поля — стандартный способ внедрения зависимостей в Spring без `@Autowired` на конструкторе.

## Q7. Что делает `@NonNull` и где используется?

`@NonNull` на параметре или поле — Lombok вставляет `null`-проверку с `NullPointerException`:

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

**Важно:** `@NonNull` от Lombok (`lombok.NonNull`) и JSR 305 (`javax.annotation.NonNull`) или JetBrains `@NotNull` — разные аннотации. Lombok реагирует только на свой `@NonNull`.

## Q8. Что генерирует `@Data`?

`@Data` — комбинированная аннотация, аналог применения сразу:

```java
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
- Включает `@EqualsAndHashCode` — проблемы с JPA (см. Q22)
- Генерирует сеттеры для всех не-`final` полей — иногда нежелательно
- Не поддерживает `callSuper` напрямую — нужно `@EqualsAndHashCode(callSuper = true)` отдельно

`@Data` — удобно для простых DTO/POJO, но не для JPA-сущностей.

## Q9. Чем `@Value` отличается от `@Data`?

`@Value` — иммутабельный вариант `@Data`:

```java
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
| Изменяемость | mutable | immutable |

`@Value` — хорош для Value Objects в DDD, DTO-responses, record-подобных классов (до появления `record` в Java 16).

## Q10. Что такое `@Accessors` и зачем нужен?

`@Accessors` меняет стиль сгенерированных геттеров/сеттеров:

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

**Важно:** `fluent = true` ломает совместимость с Jackson и JPA, которые ожидают `getXxx()`/`setXxx()` конвенцию.

## Q11. Как работает `@Builder`?

`@Builder` генерирует статический inner class `Builder` с методом `build()`:

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

Что генерирует:
- `public static OrderBuilder builder()` — статический фабричный метод
- Inner class `OrderBuilder` с полями и fluent-сеттерами
- `Order build()` — создаёт объект через `@AllArgsConstructor`
- `toBuilder()` — если `@Builder(toBuilder = true)`, копирует текущий объект в builder

`@Builder` совместим с `@Value` для immutable builder-паттерна.

## Q12. Что такое `@Builder.Default`?

По умолчанию `@Builder` не инициализирует поля значениями из объявления — они будут `null`/0/false:

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

`@Builder.Default` также меняет сигнатуру конструктора — нужно учитывать при совместном использовании с `@AllArgsConstructor`.

## Q13. Что такое `@Singular` в Builder?

`@Singular` позволяет добавлять элементы в коллекцию по одному вместо передачи всей коллекции:

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

Коллекция создаётся `Collections.unmodifiableList(...)` — immutable. Попытка добавить элемент после `build()` → `UnsupportedOperationException`.

## Q14. Что такое `@SuperBuilder` и зачем нужен?

`@Builder` не работает с наследованием — parent fields не попадают в builder дочернего класса. `@SuperBuilder` решает эту проблему:

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
- Нельзя комбинировать с `@Builder.Default` в некоторых случаях

## Q15. Что такое `@With`?

`@With` генерирует методы `withFieldName(value)` — создают **копию объекта** с одним изменённым полем (функциональный стиль):

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

Полезен для:
- Immutable domain objects с изменением одного поля
- Event sourcing / value transformations
- Copy-on-write паттерн

В Java 14+ `record` решает ту же задачу встроенными средствами.

## Q16. Какие аннотации логирования есть в Lombok?

Lombok генерирует `private static final Logger log = ...` с полем `log`:

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

`@Slf4j` — самый популярный: SLF4J — это фасад, реализация подключается отдельно (Logback, Log4j2).

```java
// Что генерирует @Slf4j:
private static final org.slf4j.Logger log =
    org.slf4j.LoggerFactory.getLogger(OrderService.class);
```

## Q17. Что делает `@Cleanup`?

`@Cleanup` автоматически вызывает `.close()` в `finally`-блоке:

```java
public void readFile(String path) throws IOException {
    @Cleanup InputStream in = new FileInputStream(path);
    @Cleanup OutputStream out = new FileOutputStream(path + ".copy");
    // ... работа с потоками
    // Lombok добавит try-finally с in.close() и out.close()
}
```

Аналог try-with-resources, но чуть более явный синтаксически. В современном Java (`try-with-resources` с Java 7) `@Cleanup` практически не нужен — лучше использовать стандартный `try (InputStream in = ...)`.

## Q18. Что делает `@SneakyThrows` и когда его использовать?

`@SneakyThrows` позволяет бросать checked exceptions без объявления в `throws`:

```java
@SneakyThrows(IOException.class)
public void readFile(String path) {
    Files.readAllBytes(Paths.get(path));  // throws IOException — не нужно объявлять
}
```

Lombok оборачивает тело в `try-catch`, перебрасывает через `Lombok.sneakyThrow()` (использует erasure для обхода компилятора).

**Когда использовать:**
- Реализация интерфейса без `throws` (например, `Runnable`)
- Лямбды, которые не принимают checked exceptions

**Когда НЕ использовать:**
- Public API — потребитель не знает, что метод кидает checked exception
- Когда exception нужно обработать — `@SneakyThrows` скрывает это
- В большинстве обычных случаев — лучше явный `throws` или обёртка в `RuntimeException`

## Q19. Что такое `@Delegate`?

`@Delegate` реализует паттерн делегирования — генерирует методы, проксирующие вызовы к полю:

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

**Ограничение:** работает только с типами (интерфейсами), которые представлены в compile scope.

## Q20. Что такое `@FieldNameConstants`?

`@FieldNameConstants` генерирует inner class `Fields` (или `Constants`) с константами строк — именами полей:

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

Полезен при работе с JPA Criteria API, QueryDSL, Spring Data Specifications, чтобы избежать строк-магических констант.

## Q21. Что такое `lombok.config` и как его использовать?

`lombok.config` — конфигурационный файл в корне проекта (или в любой директории — применяется к пакету и подпакетам):

```properties
# lombok.config в корне проекта

# Запретить @Data на JPA-сущностях (антипаттерн)
lombok.data.flagUsage = error

# Задать addLombokGeneratedAnnotation для JaCoCo
lombok.addLombokGeneratedAnnotation = true

# Запретить @SneakyThrows (часто нежелательно)
lombok.sneakyThrows.flagUsage = warning

# Изменить имя поля логгера
lombok.log.fieldName = logger

# Отключить Lombok в конкретном пакете
lombok.addSuppressWarnings = false
```

`lombok.addLombokGeneratedAnnotation = true` — ключевая настройка для JaCoCo: сгенерированные методы помечаются `@Generated` и исключаются из покрытия тестами.

## Q22. Какие проблемы у Lombok с JPA-сущностями?

**Проблема 1 — `@EqualsAndHashCode` на основе `id`:**

JPA-сущность до сохранения имеет `id = null`. Если `equals/hashCode` считается по `id`, то:
- Все новые несохранённые сущности "равны" (`null == null`)
- При добавлении в `HashSet` → только одна запись
- После `persist()` hashCode меняется → объект "теряется" в коллекции

**Решение:** использовать `@NaturalId` (Hibernate) или `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` с полем `sku/uuid`, или вообще не использовать Lombok для equals/hashCode на Entity.

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

JPA требует no-arg конструктор. Если поля `final`, `@NoArgsConstructor` не сработает без `@NoArgsConstructor(force = true)`, который инициализирует их `null/0`.

**Рекомендация:** для JPA-сущностей использовать Lombok осторожно: только `@Getter`, `@Setter`, `@ToString(exclude=...)`, без `@Data`, `@EqualsAndHashCode`.

## Q23. Как Lombok интегрируется с MapStruct?

Проблема: компилятор вызывает annotation processors в неопределённом порядке. MapStruct может запуститься **до** Lombok, когда геттеры/сеттеры ещё не сгенерированы.

**Симптом:** MapStruct не видит поля с `@Getter`/`@Setter` → `No property named "x" exists`.

**Решение (Gradle):**

```kotlin
annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
// Порядок важен: Lombok → binding → MapStruct
```

**Решение (Maven):**

```xml
<annotationProcessorPaths>
    <!-- Lombok первым -->
    <path><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId>...</path>
    <path><groupId>org.projectlombok</groupId><artifactId>lombok-mapstruct-binding</artifactId>...</path>
    <path><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId>...</path>
</annotationProcessorPaths>
```

`lombok-mapstruct-binding` — специальный артефакт, гарантирующий правильный порядок запуска.

## Q24. Что такое `delombok` и зачем он нужен?

`delombok` — инструмент Lombok, который разворачивает аннотации в обычный Java-код:

```bash
java -jar lombok.jar delombok src/ -d src-delombok/
```

**Зачем нужен:**
- Миграция с Lombok (перейти на record, @Builder вручную)
- Понять, что именно генерирует Lombok (отладка)
- Генерация Javadoc (javadoc не понимает Lombok)
- Статический анализ инструментами, которые не знают о Lombok
- Code review в проектах где Lombok запрещён

В Gradle есть задача `delombok`:

```kotlin
tasks.register<JavaExec>("delombok") {
    classpath = configurations.annotationProcessor.get()
    mainClass.set("lombok.launch.Main")
    args("delombok", "src/main/java", "-d", "build/delombok")
}
```

## Q25. Каковы критика и ограничения Lombok?

**Технические:**
- Использует internal compiler API (AST manipulation) — может сломаться при обновлении JDK
- Java 17+ sealed classes, records — частично перекрывают Lombok, но не полностью
- Нестандартное поведение аннотационного процессора → проблемы с некоторыми инструментами

**Командные/процессные:**
- Неявная кодогенерация — code review сложнее (видишь только аннотации, а не код)
- Требует IDE-плагин у всех разработчиков
- `@SneakyThrows`, `@Delegate` — спорные фичи, часто запрещают в code style guide

**Конкретные ловушки:**
- `@Data` на JPA-сущностях → StackOverflow и broken equals (см. Q22)
- `@Builder.Default` + `@AllArgsConstructor` → конфликт
- `@Accessors(fluent = true)` → несовместим с Jackson, JPA
- `@EqualsAndHashCode` без `callSuper = true` в иерархиях

**Когда отказаться от Lombok:**
- Публичная библиотека (потребители увидят Lombok в compile deps)
- Команда принципиально против "магического" кода
- Java 16+ `record` покрывает большинство use cases для DTO

## Q26. Как работает `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`?

Позволяют точечно контролировать, какие поля участвуют в `equals/hashCode`:

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

`@EqualsAndHashCode.Include` можно ставить на методы — тогда результат метода участвует в сравнении:

```java
@EqualsAndHashCode.Include
public String normalizedName() {
    return name.toLowerCase().trim();
}
```

## Q27. Как Lombok обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`?

По умолчанию `callSuper = false` — поля родительского класса игнорируются.

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

**Lombok предупреждение:** при `@Data` в классах с родителем (не `Object`) Lombok по умолчанию выдаёт предупреждение о том, что `callSuper = false` может быть ошибкой. Это предупреждение можно убрать явным `@EqualsAndHashCode(callSuper = false)` — если это действительно intended.

---

## See also

- [[java-annotations-interview|Java Annotations]] — Lombok реализован через JSR 269 annotation processing, AST трансформация
- [[java-initialization-interview|Java Initialization]] — конструкторы, init-блоки, final поля — всё это затрагивает @Builder, @Value
- [[java-mapstruct-interview|MapStruct]] — интеграция Lombok+MapStruct, порядок annotation processors
- [[java-jackson-interview|Jackson]] — @Accessors(fluent=true) ломает Jackson; @JsonProperty взаимодействие
- [[java-core-interview|Java Core]] — equals/hashCode контракт, который Lombok реализует
- [[java-oop-interview|Java OOP]] — наследование, @SuperBuilder, callSuper в @EqualsAndHashCode
- [[spring-data-jpa-interview|Spring Data JPA]] — проблемы @Data/@EqualsAndHashCode на JPA-сущностях
- [[spring-boot-interview|Spring Boot]] — @RequiredArgsConstructor + final fields = рекомендованный Spring DI
- [[unit-testing-interview|Unit Testing]] — lombok.addLombokGeneratedAnnotation для исключения из JaCoCo
- [[design-patterns-interview|Design Patterns]] — Builder (Q11-Q14), Delegate (@Delegate), Value Object (@Value)
