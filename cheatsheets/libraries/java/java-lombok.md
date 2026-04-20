---
title: "Lombok: кодогенерация в Java"
description: "Шпаргалка по Lombok: @Getter, @Setter, @Data, @Value, @Builder, @Slf4j, Delombok, подводные камни с JPA и сравнение с Java 17 records."
tags:
  - libraries
  - java
  - lombok
  - codegen
  - boilerplate
difficulty: "beginner"
updated: "2026-04-20"
---
# Lombok: кодогенерация в Java

Project Lombok — annotation processor, который на этапе компиляции генерирует геттеры, сеттеры, конструкторы, `equals`/`hashCode`, билдеры и логгеры. Избавляет от десятков строк boilerplate в POJO.

Работает через модификацию AST на лету — сгенерированный код не виден в исходниках, но присутствует в `.class`. Требует плагина в IDE (встроен в IntelliJ с 2020.x, для Eclipse — отдельный установщик).

С Java 14+ части Lombok есть нативные аналоги: records (immutable DTO), sealed classes (иерархии), pattern matching. Но `@Builder`, `@Slf4j`, `@SneakyThrows` и `@With` остаются актуальными даже в проектах на Java 21.

## Полезные ссылки

### Официальная документация
- [Project Lombok Features](https://projectlombok.org/features/) — полный список аннотаций
- [Configuration System](https://projectlombok.org/features/configuration) — `lombok.config`
- [Baeldung: Intro to Project Lombok](https://www.baeldung.com/intro-to-project-lombok) — базовая статья
- [Lombok GitHub](https://github.com/projectlombok/lombok) — исходники и issues

### См. также
- [[java-bean-validation]] — Lombok в DTO с валидацией
- [[java-mapstruct]] — совместное использование
- [[java-jackson]] — сериализация Lombok-классов
- [[spring-data-jpa]] — подводные камни с JPA
- [[java-basics]] — базовые концепции Java
- [[spring-boot]] — Lombok в Spring-проектах

## Содержание

- [Установка](#установка)
- [Базовые аннотации](#базовые-аннотации)
  - [@Getter и @Setter](#getter-и-setter)
  - [@ToString](#tostring)
  - [@EqualsAndHashCode](#equalsandhashcode)
  - [Конструкторы](#конструкторы)
  - [@Data и @Value](#data-и-value)
- [@Builder](#builder)
  - [@Builder.Default](#builderdefault)
  - [toBuilder — «скопировать с изменениями»](#tobuilder-скопировать-с-изменениями)
  - [@Singular для коллекций](#singular-для-коллекций)
  - [@SuperBuilder для наследования](#superbuilder-для-наследования)
- [Логгеры](#логгеры)
- [Продвинутые аннотации](#продвинутые-аннотации)
  - [@Accessors](#accessors)
  - [@With](#with)
  - [@Delegate](#delegate)
  - [@Cleanup](#cleanup)
  - [@SneakyThrows](#sneakythrows)
  - [@UtilityClass](#utilityclass)
  - [@FieldDefaults](#fielddefaults)
- [Delombok](#delombok)
- [Подводные камни с JPA](#подводные-камни-с-jpa)
  - [@EqualsAndHashCode + lazy loading](#equalsandhashcode-lazy-loading)
  - [@ToString + lazy](#tostring-lazy)
  - [@Builder + @NoArgsConstructor](#builder-noargsconstructor)
- [Lombok vs Records](#lombok-vs-records)
- [Конфигурация lombok.config](#конфигурация-lombokconfig)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [См. также](#см-также-1)

## Установка

```gradle
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
}
```

```xml
<!-- Maven -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.34</version>
    <scope>provided</scope>
</dependency>
```

В IntelliJ IDEA: плагин встроен, но нужно включить annotation processing — `Settings Build Compiler Annotation Processors Enable`.

## Базовые аннотации

### @Getter и @Setter

```java
import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter @Setter
    private Long id;

    @Getter
    private String name;          // только getter, immutable снаружи

    @Setter(AccessLevel.PROTECTED)
    private String passwordHash;   // setter с protected доступом

    @Getter(lazy = true)
    private final String expensiveValue = computeExpensive();  // lazy init, thread-safe
}
```

На классе — сразу для всех полей:

```java
@Getter
@Setter
public class User {
    private Long id;
    private String name;
    private String email;
}
```

### @ToString

```java
@ToString
public class User {
    private Long id;
    private String name;
    @ToString.Exclude
    private String password;         // не попадёт в toString
}

// Включить только конкретные поля
@ToString(onlyExplicitlyIncluded = true)
public class Order {
    @ToString.Include
    private Long id;
    private String customerName;     // не попадёт
}

// Без вызова getter'ов (по умолчанию использует fields, если они есть)
@ToString(doNotUseGetters = true)
```

Важно с JPA — `@ToString` может триггерить lazy-loading. Исключи коллекции и lazy-связи через `@ToString.Exclude`.

### @EqualsAndHashCode

```java
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
}

// Только по некоторым полям
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private Long id;
    private String name;             // не участвует в equals
}

// Без вызова super.equals()
@EqualsAndHashCode(callSuper = false)
public class UserExt extends User { ... }
```

### Конструкторы

| Аннотация | Что генерирует |
|-----------|---------------|
| `@NoArgsConstructor` | `public User()` |
| `@AllArgsConstructor` | `public User(Long id, String name, ...)` |
| `@RequiredArgsConstructor` | только `final` и `@NonNull` поля |

```java
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private String prefix = "USER_";           // не попадает в конструктор

    // Сгенерируется: public UserService(UserRepository repo, PasswordEncoder encoder)
}
```

`@NonNull` добавляет null-check в начало конструктора:

```java
@RequiredArgsConstructor
public class User {
    @NonNull private final String name;
    // if (name == null) throw new NullPointerException("name is marked non-null but is null");
}
```

### @Data и @Value

`@Data` = `@Getter` + `@Setter` + `@ToString` + `@EqualsAndHashCode` + `@RequiredArgsConstructor`.

```java
@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
```

`@Value` = immutable DTO: `@Getter` + `@ToString` + `@EqualsAndHashCode` + `@AllArgsConstructor` + все поля `private final` + сам класс `final`.

```java
@Value
public class Money {
    BigDecimal amount;
    String currency;
}
// Эквивалент: public final class Money { private final BigDecimal amount; ... }
```

**Не используй `@Data` с JPA-entity** — `@EqualsAndHashCode` по всем полям ломается с lazy-связями и proxy.

## @Builder

Генерирует fluent-builder:

```java
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}

// Использование
User user = User.builder()
    .id(1L)
    .name("Иван")
    .email("ivan@example.com")
    .build();
```

### @Builder.Default

Без `@Builder.Default` дефолтные значения полей игнорируются билдером:

```java
@Builder
public class Order {
    @Builder.Default
    private OrderStatus status = OrderStatus.NEW;

    @Builder.Default
    private List<Item> items = new ArrayList<>();
}
```

### toBuilder — «скопировать с изменениями»

```java
@Builder(toBuilder = true)
public class User { ... }

User copy = user.toBuilder().email("new@example.com").build();
```

### @Singular для коллекций

```java
@Builder
public class Order {
    @Singular
    private List<Item> items;
}

Order order = Order.builder()
    .item(new Item("A"))     // по одному
    .item(new Item("B"))
    .items(List.of(c, d))    // или сразу список
    .build();
```

### @SuperBuilder для наследования

`@Builder` не поддерживает наследование — используй `@SuperBuilder`:

```java
@SuperBuilder
public abstract class BaseEvent {
    private Instant timestamp;
    private String source;
}

@SuperBuilder
public class OrderCreatedEvent extends BaseEvent {
    private Long orderId;
}

OrderCreatedEvent event = OrderCreatedEvent.builder()
    .timestamp(Instant.now())        // из базового
    .source("orders-service")
    .orderId(42L)                     // из своего
    .build();
```

## Логгеры

Lombok создаёт `private static final` логгер с именем `log`:

| Аннотация | Что создаёт |
|-----------|-------------|
| `@Slf4j` | SLF4J `Logger log` (стандарт в Spring Boot) |
| `@Log4j2` | Log4j 2 `Logger log` |
| `@CommonsLog` | Apache Commons Logging |
| `@Flogger` | Google Flogger |
| `@Log` | `java.util.logging.Logger log` |
| `@XSlf4j` | SLF4J extended logger |

```java
@Slf4j
public class OrderService {
    public void create(Order order) {
        log.info("Creating order {}", order.getId());
        log.debug("Order details: {}", order);
    }
}
```

Для Spring Boot — `@Slf4j`: SLF4J API + Logback под капотом.

## Продвинутые аннотации

### @Accessors

Fluent-стиль (без префиксов get/set):

```java
@Getter @Setter
@Accessors(fluent = true)
public class User {
    private String name;
}

user.name("Иван").name();   // вместо setName/getName
```

Или chainable — setter возвращает `this`:

```java
@Setter
@Accessors(chain = true)
public class User {
    private String name;
    private String email;
}

User u = new User().setName("Иван").setEmail("i@e.com");
```

### @With

Генерирует `withField` — копия объекта с изменённым полем (immutable update):

```java
@With
@AllArgsConstructor
public class User {
    private final String name;
    private final String email;
}

User updated = user.withEmail("new@example.com");  // новый User с другим email
```

Отлично дополняет `@Value`.

### @Delegate

Делегирует все методы интерфейса на вложенное поле:

```java
public class UserList {
    @Delegate
    private final List<User> users = new ArrayList<>();
}

UserList list = new UserList();
list.add(user);          // вызов делегируется в users.add(user)
list.size();
```

Опасно для больших интерфейсов — генерирует много методов. Лучше явный интерфейс.

### @Cleanup

Автоматический close ресурса (аналог try-with-resources):

```java
public void copy(String from, String to) throws IOException {
    @Cleanup InputStream in = new FileInputStream(from);
    @Cleanup OutputStream out = new FileOutputStream(to);
    in.transferTo(out);
}
```

Но try-with-resources в Java 7+ — более идиоматично.

### @SneakyThrows

Позволяет бросать checked exception без объявления в `throws`:

```java
@SneakyThrows
public String read(String path) {
    return Files.readString(Path.of(path));    // IOException не в сигнатуре
}

@SneakyThrows({IOException.class, InterruptedException.class})
public void process() { ... }
```

**Спорная фича.** Использовать только в тестах, main-методах, Runnable/Supplier, где checked exception бесполезен. В production-коде прячет ошибки и ломает compile-time проверки.

### @UtilityClass

Делает класс utility: `final`, приватный конструктор с `UnsupportedOperationException`, все поля/методы `static`.

```java
@UtilityClass
public class StringUtils {
    public String capitalize(String s) {       // автоматически static
        return s.toUpperCase();
    }
}

StringUtils.capitalize("hello");
// new StringUtils() — ошибка
```

### @FieldDefaults

Массовая настройка access / final для всех полей:

```java
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class User {
    Long id;              // = private final Long id;
    String name;          // = private final String name;
}
```

## Delombok

Инструмент, превращающий Lombok-код в обычный Java. Нужен для:
- Отладки (посмотреть, что именно генерируется)
- Миграции с Lombok на vanilla Java
- Генерации Javadoc (стандартный Javadoc не видит generated методы)

```bash
java -jar lombok.jar delombok src/main/java -d target/delombok

# Через Gradle-плагин
./gradlew delombok
```

В IntelliJ — `Refactor Delombok` на выбранном файле / папке.

## Подводные камни с JPA

### `@EqualsAndHashCode` + lazy loading

```java
@Entity
@Data                          // содержит @EqualsAndHashCode по всем полям
public class Order {
    @Id private Long id;
    @OneToMany private List<Item> items;   // lazy — при equals() триггерит SELECT
}
```

Проблемы:
- `equals()` инициализирует lazy-коллекции N+1
- Два объекта с одинаковым id, но разным состоянием коллекций могут быть не равны
- Hibernate proxy vs реальный объект — разный `getClass()`, разные хеши

**Правильно:**

```java
@Entity
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    @Id
    @EqualsAndHashCode.Include
    private Long id;           // equals/hashCode только по id

    @OneToMany private List<Item> items;
}
```

### `@ToString` + lazy

`@ToString` включает все поля — тоже триггерит lazy. Исключи коллекции:

```java
@ToString(exclude = {"items", "customer"})
```

Или `@ToString.Exclude` на полях.

### `@Builder` + `@NoArgsConstructor`

JPA требует no-args конструктор, но `@Builder` создаёт `@AllArgsConstructor`. Комбинируй:

```java
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order { ... }
```

## Lombok vs Records

| Критерий | Lombok `@Value` | Java 14+ record |
|----------|-----------------|-----------------|
| Immutability | `final` класс, `final` поля | встроено |
| equals/hashCode/toString | генерирует Lombok | встроено |
| Геттеры | `getName()` | `name()` (без get) |
| Наследование | нельзя наследовать от классов | нельзя (implicitly `final`) |
| JPA entity | условно ок | нельзя (нет no-arg constructor) |
| Сериализация Jackson | нужен `@JsonCreator` | работает из коробки (Jackson 2.12+) |
| Custom сеттеры | можно | нельзя (только custom constructor) |
| Поддержка рантайма | compile-time magic | языковая фича |
| Версия Java | 8+ | 16+ (GA) |

**Когда record:**
- Immutable value object, DTO для API / сообщений между сервисами
- Jackson-десериализация DTO в Spring Boot 3
- Pattern matching (`case Point(int x, int y)`)

**Когда Lombok:**
- JPA entity — records не подходят
- Builder со сложной логикой — `@Builder` / `@SuperBuilder`
- Нужна mutable DTO (редко, но бывает)
- Java 11 / 8 проекты
- `@Slf4j`, `@SneakyThrows` — этого в records нет

**Итог:** в Java 17+ для новых DTO используй records, Lombok оставь для entity, сервисов с логгером, builder-heavy классов.

## Конфигурация lombok.config

Файл `lombok.config` в корне проекта (или модуля) — общие настройки для всего дерева.

```properties
# Не искать lombok.config выше по дереву
config.stopBubbling = true

# Запретить @Data — чтобы не появлялись опасные equals на entity
lombok.data.flagUsage = error

# Запретить @SneakyThrows в production-коде
lombok.sneakyThrows.flagUsage = warning

# Добавить @Generated на сгенерированный код (видно в coverage)
lombok.addLombokGeneratedAnnotation = true

# Не копировать аннотации из Java-пакетов
lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier

# Запретить @Accessors(chain=true) — часто путает людей
lombok.accessors.chain.flagUsage = warning

# Use log variable name "logger" instead of "log"
lombok.log.fieldName = logger
```

`lombok.addLombokGeneratedAnnotation = true` — JaCoCo исключит сгенерированный код из покрытия тестами.

## Лучшие практики

- **Не используй `@Data` на JPA-entity.** Используй `@Getter`, `@Setter`, `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` по id.
- **`@Value` — только для immutable DTO.** С Java 17+ предпочитай records.
- **`@Builder.Default` обязательно** для любого поля с инициализатором, иначе билдер вернёт `null`.
- **`@RequiredArgsConstructor` + `final` поля — стандарт для Spring-сервисов.** `private final` поля + `@RequiredArgsConstructor` = DI без boilerplate.
- **`@Slf4j` везде.** Чистый способ получить логгер, без `LoggerFactory.getLogger(...)`.
- **`@SneakyThrows` только в редких местах** — тесты, main, лямбды. В production-коде вредит.
- **`@ToString.Exclude` на lazy-полях** — иначе при `log.info("{}", entity)` будет N+1 или `LazyInitializationException`.
- **`lombok.config` в корне репозитория** — общие правила для команды.
- **Проверяй Delombok перед отдачей в legacy-код.** Если команда не готова к Lombok — покажи, что получится в чистом Java.
- **Combo Lombok + MapStruct + Bean Validation** — самая частая связка. Не забудь `lombok-mapstruct-binding` (см. [[java-mapstruct]]).

## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| IDEA не видит геттеры/сеттеры | Annotation processing выключен | Settings Annotation Processors Enable |
| `cannot find symbol: method getName()` при сборке | Lombok не в `annotationProcessor` | Добавить в `annotationProcessor` (Gradle) |
| `@Builder` игнорирует дефолты | Забыт `@Builder.Default` | Добавить аннотацию на поле |
| `@EqualsAndHashCode` бесконечно рекурсит | Bidirectional связь | `@EqualsAndHashCode.Exclude` на обратной связи |
| Jackson не десериализует `@Value` | Нет `@JsonCreator` | Добавить `@JsonCreator` + `@JsonProperty` или перейти на record |
| `NoClassDefFoundError: lombok/...` в runtime | Lombok в `implementation` вместо `compileOnly` | Переключить на `compileOnly` + `annotationProcessor` |
| MapStruct не видит Lombok-геттеры | Нет `lombok-mapstruct-binding` | Добавить processor |
| `@SuperBuilder` не компилируется | Родитель не аннотирован | Оба класса должны быть `@SuperBuilder` |
| IntelliJ подчёркивает `log.info(...)` красным | Plugin не активен | Обновить IDEA / плагин Lombok |
| Покрытие тестами падает из-за generated | Jacoco считает generated код | `lombok.addLombokGeneratedAnnotation = true` |

## См. также

- [[java-bean-validation]] — валидация в DTO с Lombok
- [[java-mapstruct]] — маппинг DTO entity
- [[java-jackson]] — сериализация Lombok-классов
- [[spring-boot]] — Lombok в Spring-проектах
- [[spring-data-jpa]] — подводные камни с JPA
- [[java-basics]] — базовые концепции
- [[java-exceptions]] — checked vs unchecked, `@SneakyThrows`
