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


> [!mcq]
> - [ ] Lombok — runtime agent: загружается JVM-агентом и генерирует методы через рефлексию во время выполнения | ❌ ПОСЛЕДСТВИЕ: такого агента нет, @Data не распознаётся, compile error на первом поле с @Getter
> - [x] Lombok — AST-трансформер: встраивается в javac через internal API и модифицирует синтаксическое дерево до генерации .class | ✓ ПРИМЕНЯТЬ: объяснение отличает Lombok от MapStruct/Dagger (генерируют файлы) 📋 ПРАВИЛО: Lombok = AST modification, не генерация .java-файлов 🔗 См. Q24
> - [ ] Lombok генерирует новые .java-файлы рядом с исходниками, как MapStruct | ❌ ПОСЛЕДСТВИЕ: .java-файлы не создаются, геттеры есть только в .class; delombok создаёт файлы, но это отдельная утилита
> - [ ] Lombok использует bytecode-weaving через ASM уже после компиляции .class | ❌ ПОСЛЕДСТВИЕ: weaving не вызывается, методы отсутствуют в .class, NoSuchMethodError при первом обращении

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


> [!mcq]
> - [ ] Lombok добавить в implementation scope — и компилятор, и runtime увидят аннотации | ❌ ПОСЛЕДСТВИЕ: Lombok попадает в transitive deps, потребители библиотеки получают ненужную зависимость
> - [ ] Только annotationProcessor без compileOnly — processor сам найдёт аннотации через classpath | ❌ ПОСЛЕДСТВИЕ: compile error — @Data/@Slf4j символы не видны в исходниках, SymbolNotFound
> - [x] compileOnly + annotationProcessor (Gradle) или optional=true + annotationProcessorPaths (Maven) | ✓ ПРИМЕНЯТЬ: все проекты с Lombok 📋 ПРАВИЛО: compileOnly — Lombok нужен только при компиляции, не в runtime 🔗 См. Q25
> - [ ] Lombok в runtimeOnly scope — аннотации нужны при выполнении для рефлексии | ❌ ПОСЛЕДСТВИЕ: annotation processor не вызывается при компиляции, @Data не генерирует геттеры, NoSuchMethodError

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


> [!mcq]
> - [ ] @Getter на boolean-поле генерирует getActive() вместо isActive() | ❌ ПОСЛЕДСТВИЕ: Jackson не может связать JSON "active" с геттером getActive(), поле игнорируется при десериализации
> - [ ] @Setter(AccessLevel.NONE) генерирует private сеттер вместо полного отключения | ❌ ПОСЛЕДСТВИЕ: сеттер всё равно присутствует в классе — нарушается намерение запретить мутацию поля
> - [ ] @Getter и @Setter работают только на уровне класса, не на отдельном поле | ❌ ПОСЛЕДСТВИЕ: AccessLevel.NONE на конкретном поле не применяется, у поля остаётся public setter
> - [x] @Getter → getX()/isX() для boolean; @Setter → setX(Type); AccessLevel контролирует видимость; NONE отключает генерацию | ✓ ПРИМЕНЯТЬ: точечный контроль по полям 📋 ПРАВИЛО: @Setter(AccessLevel.NONE) = «не генерировать» 🔗 См. Q5

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


> [!mcq]
> - [ ] @ToString без параметров безопасен для JPA-сущностей — он просто выводит поля | ❌ ПОСЛЕДСТВИЕ: @ToString включает lazy-loaded коллекции — каждый вызов toString() триггерит N+1 запрос к БД
> - [x] @ToString генерирует toString() для всех нестатических полей; exclude убирает поля; callSuper=false по умолчанию — поля родителя игнорируются | ✓ ПРИМЕНЯТЬ: всегда exclude lazy-коллекции в JPA 📋 ПРАВИЛО: @ToString(exclude="items") для @OneToMany — иначе StackOverflow 🔗 См. Q22
> - [ ] callSuper=true по умолчанию в @ToString — родительские поля включаются автоматически | ❌ ПОСЛЕДСТВИЕ: callSuper=false по умолчанию, поля BaseEntity (id) не попадут в toString, потеря данных в логах
> - [ ] @ToString.Include на поле вызывает eager загрузку этой ассоциации | ❌ ПОСЛЕДСТВИЕ: @ToString.Include — это просто маркер для включения в вывод, не влияет на fetch стратегию JPA

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


> [!mcq]
> - [ ] @EqualsAndHashCode безопасен для JPA-сущностей, если id-поле не null после persist | ❌ ПОСЛЕДСТВИЕ: до persist id=null → все новые сущности имеют одинаковый hashCode, HashSet содержит только одну запись
> - [ ] callSuper=true используется по умолчанию в @EqualsAndHashCode | ❌ ПОСЛЕДСТВИЕ: callSuper=false по умолчанию — поля родителя игнорируются в equals, объекты с разными id могут считаться равными
> - [ ] @EqualsAndHashCode на mutable объектах в HashMap безопасен, если поля не меняются после put | ❌ ПОСЛЕДСТВИЕ: если изменить поле после put, hashCode меняется — объект «теряется» в коллекции, get вернёт null
> - [x] Три риска: mutable objects в HashMap (hash изменился), JPA id=null до persist, inheritance callSuper=false | ✓ ПРИМЕНЯТЬ: JPA-сущности — только @NaturalId или onlyExplicitlyIncluded; mutable — не в HashSet 📋 ПРАВИЛО: @Data на Entity = антипаттерн 🔗 См. Q22

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


> [!mcq]
> - [ ] @AllArgsConstructor — предпочтительный способ DI в Spring Boot, принимает все поля | ❌ ПОСЛЕДСТВИЕ: @AllArgsConstructor хрупкий — порядок параметров фиксирован, добавление поля ломает все вызовы
> - [ ] @NoArgsConstructor нельзя использовать совместно с @Builder, они конфликтуют | ❌ ПОСЛЕДСТВИЕ: совместимы, JPA требует оба: @NoArgsConstructor для ORM, @Builder для удобного создания объектов
> - [x] @NoArgsConstructor — для JPA/Jackson; @RequiredArgsConstructor — final+@NonNull поля (Spring DI); @AllArgsConstructor — все поля | ✓ ПРИМЕНЯТЬ: @RequiredArgsConstructor + final поля = рекомендованный Spring DI без @Autowired 📋 ПРАВИЛО: RequiredArgs = final+NonNull, NoArgs = ORM/Jackson 🔗 См. Q8
> - [ ] @RequiredArgsConstructor включает поля с default-значениями в конструктор | ❌ ПОСЛЕДСТВИЕ: только final и @NonNull поля включаются; non-final поля с дефолтами — нет, иначе конструктор слишком широкий

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


> [!mcq]
> - [ ] @NonNull от Lombok и @NotNull от javax.validation взаимозаменяемы — оба генерируют проверку | ❌ ПОСЛЕДСТВИЕ: @NotNull (Bean Validation) — только для валидации в Spring MVC; Lombok реагирует только на lombok.NonNull, null-check не генерируется
> - [x] @NonNull вставляет null-check в начало метода/конструктора: if (param == null) throw NullPointerException | ✓ ПРИМЕНЯТЬ: @RequiredArgsConstructor + @NonNull = автоматическая защита от null в конструкторе 📋 ПРАВИЛО: lombok.NonNull ≠ javax.annotation.NonNull — только Lombok добавляет runtime-проверку 🔗 См. Q6
> - [ ] @NonNull на final поле в @RequiredArgsConstructor — поле НЕ включается в конструктор | ❌ ПОСЛЕДСТВИЕ: @NonNull + final = включается в @RequiredArgsConstructor С null-проверкой; без @NonNull final всё равно включается, но без проверки
> - [ ] @NonNull подавляет предупреждения компилятора, но не генерирует runtime-проверку | ❌ ПОСЛЕДСТВИЕ: @NonNull именно добавляет runtime NPE-проверку — если не хочешь проверки, нужен @SuppressWarnings иного рода

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


> [!mcq]
> - [ ] @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode (конструктор не генерируется) | ❌ ПОСЛЕДСТВИЕ: @Data также генерирует @RequiredArgsConstructor — без него конструктор придётся добавлять вручную
> - [ ] @Data на JPA-сущности безопасен, если избегать двунаправленных ассоциаций | ❌ ПОСЛЕДСТВИЕ: @Data всё равно включает @EqualsAndHashCode с проблемой id=null — HashSet сломается даже без bidirectional
> - [x] @Data = @Getter + @Setter + @RequiredArgsConstructor + @ToString + @EqualsAndHashCode; удобен для DTO, опасен на JPA-Entity | ✓ ПРИМЕНЯТЬ: DTO/POJO без JPA 📋 ПРАВИЛО: @Data на @Entity = StackOverflow (toString) + broken equals (id=null) → используй только @Getter/@Setter 🔗 См. Q22
> - [ ] @Data автоматически добавляет @NoArgsConstructor, чтобы JPA и Jackson работали | ❌ ПОСЛЕДСТВИЕ: @Data добавляет @RequiredArgsConstructor, не @NoArgsConstructor; для JPA нужно явно добавить @NoArgsConstructor

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


> [!mcq]
> - [ ] @Value генерирует @RequiredArgsConstructor, как и @Data | ❌ ПОСЛЕДСТВИЕ: @Value использует @AllArgsConstructor (все поля финализируются), а не RequiredArgs — порядок параметров фиксирован
> - [x] @Value = @Getter + @AllArgsConstructor + @ToString + @EqualsAndHashCode + все поля final; без сеттеров | ✓ ПРИМЕНЯТЬ: Value Objects в DDD, immutable DTO, record-аналог до Java 16 📋 ПРАВИЛО: @Value = иммутабельный @Data 🔗 См. Q15
> - [ ] @Value и @Data оба генерируют сеттеры, разница только в final | ❌ ПОСЛЕДСТВИЕ: @Value сеттеров не генерирует вообще, все поля final — попытка вызвать сеттер = compile error
> - [ ] @Value совместим с JPA-сущностями в отличие от @Data | ❌ ПОСЛЕДСТВИЕ: @Value делает все поля final, JPA требует mutable поля и no-arg конструктор — @Value ещё менее совместим с JPA чем @Data

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


> [!mcq]
> - [ ] @Accessors(fluent = true) совместим с Jackson — геттеры host() и port() правильно сериализуются | ❌ ПОСЛЕДСТВИЕ: Jackson ищет getXxx()/setXxx(), fluent-методы без prefix не находятся → поля не включаются в JSON
> - [ ] @Accessors(chain = true) создаёт новый объект при каждом сеттер-вызове (immutable style) | ❌ ПОСЛЕДСТВИЕ: chain = true возвращает this, не копию; объект mutable — вместо immutable нужен @With
> - [x] @Accessors(chain=true) → сеттеры возвращают this (fluent API); fluent=true → без get/set префиксов; несовместим с Jackson/JPA | ✓ ПРИМЕНЯТЬ: fluent API при явном отсутствии Jackson и JPA 📋 ПРАВИЛО: fluent=true ломает стандартные соглашения — Jackson/JPA не видят поля 🔗 См. Q11
> - [ ] @Accessors(prefix = "m_") убирает префикс из имён полей при объявлении класса | ❌ ПОСЛЕДСТВИЕ: prefix убирает его только из имён геттеров/сеттеров, поле по-прежнему называется m_field

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


> [!mcq]
> - [ ] @Builder генерирует конструктор публично, чтобы создавать объекты напрямую кроме билдера | ❌ ПОСЛЕДСТВИЕ: @Builder генерирует package-private all-args конструктор; публичный конструктор нарушает паттерн, обходя nullable-поля
> - [ ] @Builder(toBuilder = true) создаёт новый объект только при вызове .build(), не копирует существующий | ❌ ПОСЛЕДСТВИЕ: toBuilder=true добавляет instance-метод toBuilder() — копирует текущий объект в builder для изменения
> - [x] @Builder генерирует статический inner класс Builder, фабричный метод builder(), fluent-сеттеры и build() | ✓ ПРИМЕНЯТЬ: объекты с >3 параметрами, особенно с nullable-полями 📋 ПРАВИЛО: @Builder + @Value = immutable builder pattern 🔗 См. Q12
> - [ ] @Builder несовместим с @Value — иммутабельные объекты не поддерживают builder | ❌ ПОСЛЕДСТВИЕ: @Builder и @Value совместимы и рекомендуются вместе для immutable объектов с builder API

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


> [!mcq]
> - [ ] Поле с дефолтным значением в @Builder-классе автоматически получает это значение при build() | ❌ ПОСЛЕДСТВИЕ: @Builder игнорирует инициализаторы полей — без @Builder.Default поле будет null/0/false
> - [x] @Builder.Default явно сохраняет дефолтные значения полей; без него @Builder инициализирует поля null/0/false | ✓ ПРИМЕНЯТЬ: List, Map, timeout и другие поля с default-значениями в @Builder-классе 📋 ПРАВИЛО: @Builder без @Default = поля null, даже если написал = new ArrayList<>() 🔗 См. Q13
> - [ ] @Builder.Default несовместим с @AllArgsConstructor — конструктор конфликтует с Builder | ❌ ПОСЛЕДСТВИЕ: @Builder.Default меняет сигнатуру конструктора — это предупреждение, не ошибка; нужно добавить @AllArgsConstructor(onConstructor=@__(@JsonCreator)) осторожно
> - [ ] @Builder.Default применяется к final-полям, чтобы Builder не ломал immutability | ❌ ПОСЛЕДСТВИЕ: @Builder.Default работает с любыми полями; final-поля в @Builder-классах вообще нетипичны

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


> [!mcq]
> - [ ] @Singular позволяет передать коллекцию целиком методом с суффиксом -s (errors(list)) | ❌ ПОСЛЕДСТВИЕ: @Singular генерирует метод для добавления ОДНОГО элемента; для передачи коллекции нужен clearErrors() + addAll-like паттерн
> - [x] @Singular генерирует метод для поштучного добавления элементов в коллекцию; build() создаёт unmodifiable список | ✓ ПРИМЕНЯТЬ: накопление ошибок, метрик, правил через builder API 📋 ПРАВИЛО: @Singular → метод в единственном числе, список после build() — immutable (UnsupportedOperationException при add) 🔗 См. Q14
> - [ ] @Singular совместим с mutable списками — после build() можно добавлять элементы | ❌ ПОСЛЕДСТВИЕ: @Singular создаёт unmodifiableList; попытка list.add() после build() → UnsupportedOperationException
> - [ ] @Singular("metric") задаёт имя метода во множественном числе для добавления коллекции | ❌ ПОСЛЕДСТВИЕ: параметр задаёт имя метода в единственном числе (metric), не во множественном

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


> [!mcq]
> - [ ] Обычный @Builder работает с иерархией классов — Builder дочернего класса автоматически включает поля родителя | ❌ ПОСЛЕДСТВИЕ: @Builder не поддерживает наследование — поля Animal не будут доступны в Dog.builder()
> - [ ] @SuperBuilder можно комбинировать с обычным @Builder в той же иерархии | ❌ ПОСЛЕДСТВИЕ: @SuperBuilder и @Builder в одной иерархии — compile error; все классы должны использовать @SuperBuilder
> - [x] @SuperBuilder решает проблему наследования: Builder дочернего класса включает поля родителя; все классы в иерархии должны иметь @SuperBuilder | ✓ ПРИМЕНЯТЬ: иерархии DTO/Entity с общими полями 📋 ПРАВИЛО: @SuperBuilder требует аннотацию на всех классах цепочки 🔗 См. Q11
> - [ ] @SuperBuilder автоматически добавляет callSuper=true в @EqualsAndHashCode | ❌ ПОСЛЕДСТВИЕ: @SuperBuilder решает только проблему Builder-а; equals/hashCode callSuper нужно настраивать отдельно

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


> [!mcq]
> - [ ] @With изменяет текущий объект, устанавливая новое значение поля | ❌ ПОСЛЕДСТВИЕ: @With создаёт КОПИЮ с изменённым полем; оригинал не мутирует — это copy-on-write паттерн
> - [x] @With генерирует withField(value) — возвращает новый объект с одним изменённым полем; оригинал неизменён | ✓ ПРИМЕНЯТЬ: @Value + @With для immutable объектов, Event Sourcing 📋 ПРАВИЛО: with = copy constructor с одним другим полем 🔗 См. Q9
> - [ ] @With требует @Builder на том же классе — иначе не может создать копию | ❌ ПОСЛЕДСТВИЕ: @With независим от @Builder; генерирует конструктор с изменённым полем напрямую
> - [ ] @With работает на mutable объектах — меняет поле и возвращает this | ❌ ПОСЛЕДСТВИЕ: @With всегда создаёт новый объект (как record.withField); для mutable изменений достаточно сеттера

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


> [!mcq]
> - [ ] @Log4j2 и @Slf4j — взаимозаменяемы; оба используют API Log4j2 | ❌ ПОСЛЕДСТВИЕ: @Slf4j использует SLF4J-фасад (реализация подключается отдельно), @Log4j2 — прямой Log4j2 API; привязка к конкретной реализации нарушает переносимость
> - [ ] Lombok создаёт поле log с именем класса, в котором аннотация написана | ❌ ПОСЛЕДСТВИЕ: Logger создаётся с именем класса, содержащего аннотацию — это корректно и ожидаемо; проблема если скопировать класс без переименования
> - [ ] @Log генерирует SLF4J-логгер для максимальной совместимости | ❌ ПОСЛЕДСТВИЕ: @Log генерирует java.util.logging.Logger; для SLF4J нужен @Slf4j
> - [x] @Slf4j (SLF4J-фасад), @Log4j2 (Log4j2 напрямую), @Log (JUL) — генерируют private static final Logger log | ✓ ПРИМЕНЯТЬ: @Slf4j — стандарт в Spring Boot, реализация (Logback/Log4j2) подключается отдельно 📋 ПРАВИЛО: @Slf4j = log; не нужно объявлять поле вручную 🔗 См. Q21

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


> [!mcq]
> - [ ] @Cleanup лучше try-with-resources в Java 7+ — он более явный и читаемый | ❌ ПОСЛЕДСТВИЕ: try-with-resources — языковой стандарт; @Cleanup добавляет лишнюю зависимость от Lombok без преимуществ
> - [x] @Cleanup вставляет finally-блок с resource.close(); аналог try-with-resources, но в современном Java предпочтительнее стандартный синтаксис | ✓ ПРИМЕНЯТЬ: только если нужен close() нестандартного метода 📋 ПРАВИЛО: try(InputStream in = ...) предпочтительнее @Cleanup в Java 7+ 🔗 См. Q18
> - [ ] @Cleanup подходит для ресурсов, которые не реализуют AutoCloseable | ❌ ПОСЛЕДСТВИЕ: без AutoCloseable нужно явно задать @Cleanup("shutdown"); если метод не существует — compile error
> - [ ] @Cleanup(value = "disconnect") не работает — поддерживается только .close() | ❌ ПОСЛЕДСТВИЕ: @Cleanup("disconnect") работает корректно; Lombok вызывает указанный метод в finally-блоке

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


> [!mcq]
> - [ ] @SneakyThrows безопасен для public API — checked exception всплывёт как обычно | ❌ ПОСЛЕДСТВИЕ: потребитель public API не знает о возможном checked exception — catch(Exception e) его не поймает как ожидается
> - [ ] @SneakyThrows оборачивает exception в RuntimeException перед броском | ❌ ПОСЛЕДСТВИЕ: @SneakyThrows использует type erasure обход компилятора, бросает оригинальный checked exception без обёртки — стек трейс другой
> - [x] @SneakyThrows позволяет бросать checked exception без объявления в throws — через erasure trick; полезен для Runnable/lambda | ✓ ПРИМЕНЯТЬ: реализация интерфейса без throws (Runnable, Function) где checked exception нельзя объявить 📋 ПРАВИЛО: не использовать в public API — потребитель не ожидает checked exception 🔗 См. Q25
> - [ ] @SneakyThrows(IOException.class) — компилятор проверяет, что метод действительно throws IOException | ❌ ПОСЛЕДСТВИЕ: @SneakyThrows именно СНИМАЕТ проверку компилятора; если IOException не бросается — compile error без SneakyThrows

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


> [!mcq]
> - [ ] @Delegate реализует наследование — UserService наследует все методы UserRepository | ❌ ПОСЛЕДСТВИЕ: @Delegate реализует делегирование, не наследование — UserService не является UserRepository, только проксирует вызовы
> - [x] @Delegate генерирует методы, проксирующие вызовы к полю; UserService получает все методы UserRepository без наследования | ✓ ПРИМЕНЯТЬ: composition over inheritance; декорирование или адаптация интерфейсов 📋 ПРАВИЛО: @Delegate = delegation pattern без boilerplate forwarding-методов 🔗 См. Q6
> - [ ] @Delegate работает с любыми типами включая concrete classes | ❌ ПОСЛЕДСТВИЕ: @Delegate требует тип представленный в compile scope; конкретный класс без interface может не делегировать все ожидаемые методы
> - [ ] @Delegate(excludes = X.class) удаляет методы X из класса-делегата | ❌ ПОСЛЕДСТВИЕ: excludes исключает методы указанного типа из ПРОКСИРОВАНИЯ (они не генерируются в UserService), а не удаляет из UserRepository

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


> [!mcq]
> - [ ] @FieldNameConstants генерирует enum с именами полей вместо строковых констант | ❌ ПОСЛЕДСТВИЕ: @FieldNameConstants по умолчанию генерирует inner class Fields со String-константами, не enum; это важно для JPA Criteria API
> - [x] @FieldNameConstants генерирует inner class Fields с String-константами именами полей; исключает magic strings в JPA Criteria, QueryDSL | ✓ ПРИМЕНЯТЬ: JPA Criteria API, Spring Data Specifications, где нужны typesafe имена полей 📋 ПРАВИЛО: User.Fields.name вместо "name" — опечатки обнаруживаются при компиляции 🔗 См. Q22
> - [ ] @FieldNameConstants нужен для QueryDSL — он генерирует Q-классы | ❌ ПОСЛЕДСТВИЕ: Q-классы для QueryDSL генерирует QueryDSL APT-processor, а не @FieldNameConstants
> - [ ] @FieldNameConstants включает все статические поля в генерацию | ❌ ПОСЛЕДСТВИЕ: статические поля по умолчанию исключаются; в Fields попадают только нестатические поля класса

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


> [!mcq]
> - [ ] lombok.config в корне проекта применяется только к классам в корневом пакете | ❌ ПОСЛЕДСТВИЕ: lombok.config применяется к пакету где находится файл И всем подпакетам рекурсивно
> - [x] lombok.config в корне проекта — конфигурация для всего проекта: запрет @Data, настройка JaCoCo, имя поля логгера | ✓ ПРИМЕНЯТЬ: установить addLombokGeneratedAnnotation=true для корректного JaCoCo coverage 📋 ПРАВИЛО: lombok.data.flagUsage=error запрещает @Data на Entity 🔗 См. Q25
> - [ ] lombok.addLombokGeneratedAnnotation = true нужен для того, чтобы сгенерированный код попал в JaCoCo coverage | ❌ ПОСЛЕДСТВИЕ: наоборот — этот флаг добавляет @Generated, что ИСКЛЮЧАЕТ методы из JaCoCo coverage report
> - [ ] lombok.config можно использовать только в Maven-проектах, Gradle игнорирует его | ❌ ПОСЛЕДСТВИЕ: lombok.config работает независимо от build tool — это файл, который читает сам Lombok-processor

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


> [!mcq]
> - [ ] @Data на JPA-сущности безопасен, если не использовать @OneToMany ассоциации | ❌ ПОСЛЕДСТВИЕ: @Data включает @EqualsAndHashCode с id=null до persist → HashSet с новыми Entity всегда содержит одну запись
> - [ ] @EqualsAndHashCode на Entity нужно настроить с callSuper=true — тогда equals корректен | ❌ ПОСЛЕДСТВИЕ: для JPA-Entity проблема в id=null до persist, не в callSuper; нужен @NaturalId или onlyExplicitlyIncluded по uuid/sku
> - [ ] @NoArgsConstructor(force=true) безопасен для JPA-Entity с final полями | ❌ ПОСЛЕДСТВИЕ: force=true инициализирует final поля null/0 — нарушается invariant домена, @NonNull поля могут оказаться null
> - [x] Три проблемы: @EqualsAndHashCode (id=null), @ToString (StackOverflow на bidirectional), @NoArgsConstructor с final полями | ✓ ПРИМЕНЯТЬ: JPA-Entity — только @Getter/@Setter/@ToString(exclude=...), без @Data/@EqualsAndHashCode 📋 ПРАВИЛО: @Data на @Entity = StackOverflow + broken HashSet 🔗 См. Q5

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Что такое `delombok` и зачем он нужен? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Каковы критика и ограничения Lombok? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. Как работает `@EqualsAndHashCode.Include` / `@EqualsAndHashCode.Exclude`? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. Как Lombok обрабатывает наследование в `@EqualsAndHashCode` и `@ToString`? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Java Annotations](java-annotations-interview.md) — Lombok реализован через JSR 269 annotation processing, AST трансформация
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
