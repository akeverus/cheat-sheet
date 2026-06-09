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
- [Q1. (!) Что такое Lombok и как он работает?](#q1--что-такое-lombok-и-как-он-работает)
- [Q2. Как подключить Lombok к Maven/Gradle-проекту?](#q2-как-подключить-lombok-к-mavengradle-проекту)
- [Q3. Что генерируют `@Getter` и `@Setter`?](#q3-что-генерируют-getter-и-setter)
- [Q4. Что делает `@ToString` и как настроить вывод?](#q4-что-делает-tostring-и-как-настроить-вывод)
- [Q5. Что генерирует `@EqualsAndHashCode` и каковы риски?](#q5-что-генерирует-equalsandhashcode-и-каковы-риски)

**Конструкторы**
- [Q6. (!) Чем отличаются `@NoArgsConstructor`, `@RequiredArgsConstructor` и `@AllArgsConstructor`?](#q6--чем-отличаются-noargsconstructor-requiredargsconstructor-и-allargsconstructor)
- [Q7. Что делает `@NonNull` и где используется?](#q7-что-делает-nonnull-и-где-используется)

**Комплексные аннотации**
- [Q8. (!) Что генерирует `@Data`?](#q8--что-генерирует-data)
- [Q9. Чем `@Value` отличается от `@Data`?](#q9-чем-value-отличается-от-data)
- [Q10. Что такое `@Accessors` и зачем нужен?](#q10-что-такое-accessors-и-зачем-нужен)

**Builder**
- [Q11. (!) Как работает `@Builder`?](#q11--как-работает-builder)
- [Q12. Что такое `@Builder.Default`?](#q12-что-такое-builderdefault)
- [Q13. Что такое `@Singular` в Builder?](#q13-что-такое-singular-в-builder)
- [Q14. (!) Что такое `@SuperBuilder` и зачем нужен?](#q14--что-такое-superbuilder-и-зачем-нужен)
- [Q15. Что такое `@With`?](#q15-что-такое-with)

**Логирование и утилиты**
- [Q16. Какие аннотации логирования есть в Lombok?](#q16-какие-аннотации-логирования-есть-в-lombok)
- [Q17. Что делает `@Cleanup`?](#q17-что-делает-cleanup)
- [Q18. Что делает `@SneakyThrows` и когда его использовать?](#q18-что-делает-sneakythrows-и-когда-его-использовать)
- [Q19. Что такое `@Delegate`?](#q19-что-такое-delegate)
- [Q20. Что такое `@FieldNameConstants`?](#q20-что-такое-fieldnameconstants)

**Конфигурация и интеграция**
- [Q21. Что такое `lombok.config` и как его использовать?](#q21-что-такое-lombokconfig-и-как-его-использовать)
- [Q22. (!) Какие проблемы у Lombok с JPA-сущностями?](#q22--какие-проблемы-у-lombok-с-jpa-сущностями)
- [Q23. Как Lombok интегрируется с MapStruct?](#q23-как-lombok-интегрируется-с-mapstruct)
- [Q24. Что такое `delombok` и зачем он нужен?](#q24-что-такое-delombok-и-зачем-он-нужен)
- [Q25. Каковы критика и ограничения Lombok?](#q25-каковы-критика-и-ограничения-lombok)
- [Q26. Как работает `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`?](#q26-как-работает-equalsandhashcodeinclude--equalsandhashcodeexclude)
- [Q27. Как Lombok обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`?](#q27-как-lombok-обрабатывает-наследование-в-equalsandhashcode-и-tostring)

---

## Q1. (!) Что такое Lombok и как он работает?

`Project Lombok` — аннотационный процессор Java (JSR 269), работающий на этапе компиляции: по аннотациям он генерирует рутинный код (геттеры, сеттеры, `equals`/`hashCode`, `toString`, конструкторы, builder), чтобы его не писать руками.

Ключевая особенность — **как** он это делает. Обычные annotation processors (MapStruct, Dagger) умеют только **создавать новые файлы**, но не трогают исходный класс. Lombok идёт дальше: он встраивается в AST (Abstract Syntax Tree) компилятора через internal API и **модифицирует узлы дерева уже существующего класса** — дописывает методы прямо в `.class` без промежуточного `.java`.

```
javac → читает @Data на классе → Lombok дополняет AST → javac компилирует расширенный AST
```

Именно из-за работы с internal API компилятора возникают типичные трудности Lombok: нужен IDE-плагин (иначе IDE не «видит» сгенерированные методы и подсвечивает ошибки), сложнее отладка, и есть риск поломки при мажорных обновлениях JDK.

**Что нужно для работы:**
- Зависимость в `compile`-scope: `org.projectlombok:lombok`.
- Регистрация как annotation processor: в Maven/Gradle — через `annotationProcessorPaths`.
- IDE-плагин (в IntelliJ IDEA — «Lombok Plugin», встроен по умолчанию с версии 2020.3).

**Итог:** Lombok — это AST-трансформер, а не обычный генератор новых файлов. Отсюда и его сила (нулевой boilerplate), и его слабости (зависимость от IDE и internal API).

## Q2. Как подключить Lombok к Maven/Gradle-проекту?

Lombok нужен **только во время компиляции**, поэтому подключают его в двух местах сразу: как `compileOnly`-зависимость (виден компилятору, но не уходит в runtime и не тащится транзитивно к потребителям) и как `annotationProcessor` (чтобы javac реально запустил обработку аннотаций). Пропустить вторую часть — частая ошибка: код компилируется, но методы не генерируются.

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

**Почему `optional = true` в Maven важно:** это Maven-аналог `compileOnly`. Без него Lombok попадёт в transitive dependencies, и потребители вашей библиотеки получат его в свой classpath — хотя в runtime он не нужен вообще.

## Q3. Что генерируют `@Getter` и `@Setter`?

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

## Q4. Что делает `@ToString` и как настроить вывод?

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

## Q5. Что генерирует `@EqualsAndHashCode` и каковы риски?

`@EqualsAndHashCode` генерирует `equals()` и `hashCode()` на основе всех нестатических нетранзиентных полей. Сама генерация тривиальна — главное на собеседовании показать, что вы понимаете её риски: hash-контракт легко нарушить.

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

**Риск 1 — изменяемые поля в HashMap/HashSet:** `hashCode` считается по всем полям. Если объект уже лежит в коллекции, а вы меняете участвующее поле — его hash меняется, и объект перестаёт находиться по ключу (он «застревает» не в той корзине).

**Риск 2 — JPA-сущности:** если `equals/hashCode` строится по `id`, то до сохранения `id == null`, а значит все новые несохранённые сущности оказываются «равны» друг другу. Это ломает работу с `HashSet`/`HashMap` (подробнее в Q22).

**Риск 3 — наследование:** по умолчанию `callSuper = false`, поэтому в подклассе `equals` игнорирует поля родителя — два объекта с разным `id` родителя сочтутся равными. В иерархиях нужен `callSuper = true`.

## Q6. (!) Чем отличаются `@NoArgsConstructor`, `@RequiredArgsConstructor` и `@AllArgsConstructor`?

Все три генерируют конструктор, разница — в наборе полей, которые в него попадают: ноль, только обязательные или все.

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

**Почему `@RequiredArgsConstructor` любят в Spring:** «required» — это поля `final` и поля с `@NonNull`. Объявляете зависимости как `private final`, и Lombok генерирует конструктор ровно с ними — это и есть рекомендуемое конструкторное внедрение зависимостей, без `@Autowired` и без сеттеров. `@AllArgsConstructor`, наоборот, хрупок: добавили поле — порядок и сигнатура конструктора молча поменялись, что легко ломает вызовы по позиции аргументов.

## Q7. Что делает `@NonNull` и где используется?

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

**Частая путаница:** есть несколько одноимённых аннотаций — `lombok.NonNull`, JSR 305 `javax.annotation.Nonnull`, JetBrains `@NotNull`. Это разные вещи: Lombok генерирует runtime-проверку только для **своей** `lombok.NonNull`, остальные он игнорирует (они нужны статическим анализаторам, а не Lombok). Проверьте импорт.

## Q8. (!) Что генерирует `@Data`?

`@Data` — это «всё включено» одной аннотацией: она объединяет сразу пять других, превращая обычный класс в полноценный изменяемый POJO с аксессорами, `equals`/`hashCode`, `toString` и конструктором по обязательным полям.

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
- Включает `@EqualsAndHashCode` — отсюда проблемы с JPA (см. Q22).
- Генерирует сеттеры для всех не-`final` полей — класс становится полностью изменяемым, что не всегда нужно.
- Не принимает `callSuper` напрямую: в иерархии приходится отдельно добавлять `@EqualsAndHashCode(callSuper = true)`.

**Сценарий применения:** `@Data` удобен для простых DTO/POJO, но опасен для JPA-сущностей — там лучше точечные `@Getter`/`@Setter`/`@ToString` (см. Q22).

## Q9. Чем `@Value` отличается от `@Data`?

Коротко: `@Value` — это **иммутабельная** версия `@Data`. Если `@Data` делает изменяемый POJO с сеттерами, то `@Value` делает все поля `private final`, не генерирует сеттеры и создаёт конструктор со всеми полями — получается объект, который нельзя изменить после создания.

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

**Сценарий применения:** Value Objects в DDD, неизменяемые DTO-ответы, record-подобные классы. По сути `@Value` закрывал нишу `record` до его появления в Java 16 — в новых проектах для чисто-данных-классов часто проще взять `record`.

## Q10. Что такое `@Accessors` и зачем нужен?

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

## Q11. (!) Как работает `@Builder`?

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
- `Order build()` — собирает финальный объект (через сгенерированный all-args конструктор).
- `toBuilder()` — только если указан `@Builder(toBuilder = true)`: копирует текущий объект обратно в builder, удобно для «изменить пару полей и пересобрать».

**Сценарий применения:** `@Builder` отлично сочетается с `@Value` — получается иммутабельный объект, но с удобной пошаговой сборкой.

## Q12. Что такое `@Builder.Default`?

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

`@Builder.Default` также меняет сигнатуру конструктора — нужно учитывать при совместном использовании с `@AllArgsConstructor`.

## Q13. Что такое `@Singular` в Builder?

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

**Важный нюанс:** итоговая коллекция оборачивается в `Collections.unmodifiableList(...)`, то есть неизменяема. Попытка добавить в неё элемент уже после `build()` приведёт к `UnsupportedOperationException` — это плюс для иммутабельности, но сюрприз, если ожидался обычный изменяемый список.

## Q14. (!) Что такое `@SuperBuilder` и зачем нужен?

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

**Сценарий применения:**
- Иммутабельные доменные объекты, когда нужно «то же самое, но с одним изменённым полем».
- Event sourcing и пошаговые трансформации значений.
- Паттерн copy-on-write.

`@With` особенно ценен именно с иммутабельными классами (`@Value`): раз поля менять нельзя, единственный способ «обновить» объект — создать копию. В Java 14+ ту же задачу частично решают `record` (хотя у них нет встроенного `withX`, его пишут руками).

## Q16. Какие аннотации логирования есть в Lombok?

Lombok умеет генерировать готовое поле логгера, чтобы не писать вручную `private static final Logger log = LoggerFactory.getLogger(...)`. Аннотация выбирает фреймворк, а поле всегда называется `log`:

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

## Q17. Что делает `@Cleanup`?

`@Cleanup` гарантирует освобождение ресурса: Lombok оборачивает переменную в `try-finally` и автоматически вызывает её `.close()` при выходе из метода — в том числе при исключении.

```java
public void readFile(String path) throws IOException {
    @Cleanup InputStream in = new FileInputStream(path);
    @Cleanup OutputStream out = new FileOutputStream(path + ".copy");
    // ... работа с потоками
    // Lombok добавит try-finally с in.close() и out.close()
}
```

По сути это аналог try-with-resources. **Рекомендация:** в современной Java (try-with-resources доступен с Java 7) `@Cleanup` практически не нужен — стандартный `try (InputStream in = ...)` делает то же, но без зависимости от Lombok и понятнее в code review.

## Q18. Что делает `@SneakyThrows` и когда его использовать?

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

## Q19. Что такое `@Delegate`?

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

**Ограничение:** работает только с типами (интерфейсами), которые представлены в compile scope.

## Q20. Что такое `@FieldNameConstants`?

`@FieldNameConstants` генерирует вложенный класс `Fields` со строковыми константами — именами полей. Смысл: вместо «магической строки» `"name"` вы ссылаетесь на `User.Fields.name`, и при переименовании поля константа обновится, а опечатку поймает компилятор.

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

## Q21. Что такое `lombok.config` и как его использовать?

`lombok.config` — файл настроек, через который команда задаёт единые правила использования Lombok на весь проект: что запретить, что включить, как назвать сгенерированное. Действует **иерархически**: лежит в корне или в любой директории и применяется к её пакету и всем подпакетам (ближайший файл переопределяет верхний). Это удобный способ принудительно загнать команду в общий стиль.

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

## Q22. (!) Какие проблемы у Lombok с JPA-сущностями?

Главная мысль: `@Data` и его части удобны для DTO, но на JPA-сущностях создают сразу несколько ловушек. Корень всех проблем — Lombok ничего не знает про жизненный цикл сущности (новая/managed/detached) и про lazy-связи, а действует механически по полям.

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

Суть проблемы — порядок запуска annotation processors. MapStruct генерирует код маппера по геттерам/сеттерам, но компилятор может запустить его **раньше** Lombok, когда аксессоров ещё нет. Решается это специальным артефактом `lombok-mapstruct-binding`, который заставляет процессоры работать в правильной очерёдности.

**Симптом, по которому распознают проблему:** MapStruct не видит поля с `@Getter`/`@Setter` и падает с `No property named "x" exists`.

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

В Gradle есть задача `delombok`:

```kotlin
tasks.register<JavaExec>("delombok") {
    classpath = configurations.annotationProcessor.get()
    mainClass.set("lombok.launch.Main")
    args("delombok", "src/main/java", "-d", "build/delombok")
}
```

## Q25. Каковы критика и ограничения Lombok?

Главная претензия к Lombok сводится к одному: он работает «магически» через internal API компилятора. Отсюда вытекают и технические риски, и проблемы для команды.

**Технические:**
- Использует internal compiler API (манипуляция AST) — может сломаться при обновлении JDK.
- Java 17+ sealed classes и `record` частично перекрывают Lombok, но не заменяют полностью.
- Нестандартное поведение процессора → конфликты с некоторыми инструментами сборки и анализа.

**Командные и процессные:**
- Неявная кодогенерация усложняет code review — в diff видны только аннотации, а не реальный код.
- Всем разработчикам нужен IDE-плагин, иначе IDE «не видит» сгенерированные методы.
- `@SneakyThrows`, `@Delegate` — спорные фичи, их часто запрещают в code style guide.

**Конкретные ловушки:**
- `@Data` на JPA-сущностях → StackOverflow и сломанный `equals` (см. Q22).
- `@Builder.Default` вместе с `@AllArgsConstructor` → конфликт значений по умолчанию.
- `@Accessors(fluent = true)` → несовместимость с Jackson и JPA.
- `@EqualsAndHashCode` без `callSuper = true` в иерархиях → потеря полей родителя.

**Когда отказаться от Lombok:**
- Публичная библиотека — потребители увидят Lombok в compile-зависимостях.
- Команда принципиально против «магического» кода.
- Java 16+ — `record` уже покрывает большинство сценариев для DTO.

## Q26. Как работает `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`?

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

## Q27. Как Lombok обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`?

Ключевой факт: по умолчанию `callSuper = false`, то есть Lombok смотрит **только на поля самого класса** и полностью игнорирует поля родителя. Для `equals`/`hashCode` это прямая ошибка корректности (два объекта с разным `id` родителя окажутся равными), для `toString` — неполный вывод.

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
