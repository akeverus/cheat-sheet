---
title: "Lombok: Автоматизация boilerplate кода в Java"
description: "Комплексное руководство по использованию Project Lombok для автоматической генерации boilerplate кода в Java проектах."
tags:
  - libraries
  - code-generation
  - java-lombok
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Lombok: Автоматизация boilerplate кода в Java

**Комплексное руководство по использованию `Project Lombok` для автоматической генерации boilerplate кода в `Java` проектах.**

## Полезные ссылки

### Официальная документация
- [Project Lombok](https://projectlombok.org/) — официальный сайт
- [Lombok Features](https://projectlombok.org/features/) — список всех аннотаций
- [Lombok GitHub](https://github.com/projectlombok/lombok) — репозиторий проекта

### Интеграция
- [Spring Boot Lombok](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html) — **Spring Boot** интеграция
- [Lombok Maven Plugin](https://projectlombok.org/setup/maven) — настройка **Maven**
- [Lombok Gradle Plugin](https://projectlombok.org/setup/gradle) — настройка **Gradle**

## Содержание

- [Введение в Lombok](#введение-в-lombok)
  - [Почему Lombok?](#почему-lombok)
  - [Как работает Lombok?](#как-работает-lombok)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Настройка в IDE](#настройка-в-ide)
    - [IntelliJ IDEA](#intellij-idea)
    - [Eclipse](#eclipse)
    - [VS Code](#vs-code)
- [Основные аннотации](#основные-аннотации)
  - [@Getter и @Setter](#getter-и-setter)
  - [Конфигурация доступа](#конфигурация-доступа)
- [Конструкторы](#конструкторы)
  - [@NoArgsConstructor](#noargsconstructor)
  - [@AllArgsConstructor](#allargsconstructor)
  - [@RequiredArgsConstructor](#requiredargsconstructor)
- [equals, hashCode и toString](#equals-hashcode-и-tostring)
  - [@EqualsAndHashCode](#equalsandhashcode)
  - [@ToString](#tostring)
- [Builder паттерн](#builder-паттерн)
  - [@Builder](#builder)
  - [@Builder с конфигурацией](#builder-с-конфигурацией)
  - [@SuperBuilder](#superbuilder)
- [Data классы](#data-классы)
  - [@Data](#data)
  - [@Data с исключениями](#data-с-исключениями)
- [Value объекты](#value-объекты)
  - [@Value](#value)
- [Логирование](#логирование)
  - [@Log, @Slf4j, @Log4j2 и др.](#log-slf4j-log4j2-и-др)
  - [Доступные аннотации логирования](#доступные-аннотации-логирования)
- [Дополнительные возможности](#дополнительные-возможности)
  - [@NonNull](#nonnull)
  - [@Cleanup](#cleanup)
  - [@SneakyThrows](#sneakythrows)
  - [@Synchronized](#synchronized)
  - [@Getter(lazy = true)](#getterlazy-true)
  - [@Delegate](#delegate)
- [Интеграция с IDE](#интеграция-с-ide)
  - [Lombok Configuration](#lombok-configuration)
- [Настройки Lombok для всего проекта](#настройки-lombok-для-всего-проекта)
  - [Настройки для конкретных директорий](#настройки-для-конкретных-директорий)
- [Настройки только для тестов](#настройки-только-для-тестов)
  - [Lombok MapStruct Integration](#lombok-mapstruct-integration)
- [Best practices](#best-practices)
  - [1. Использование @Data разумно](#1-использование-data-разумно)
  - [2. Builder паттерн](#2-builder-паттерн)
  - [3. Value объекты для immutable данных](#3-value-объекты-для-immutable-данных)
  - [4. Логирование](#4-логирование)
  - [5. @NonNull для валидации](#5-nonnull-для-валидации)
  - [6. @Cleanup для ресурсов](#6-cleanup-для-ресурсов)
  - [7. Избегайте @Data для JPA сущностей](#7-избегайте-data-для-jpa-сущностей)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Когда использовать Lombok](#когда-использовать-lombok)
  - [Альтернативы](#альтернативы)
  - [Советы по миграции](#советы-по-миграции)

## Введение в Lombok

**Project Lombok** — это библиотека для **Java**, которая автоматически генерирует **boilerplate** код во время компиляции. Вместо написания геттеров, сеттеров, конструкторов и других повторяющихся методов, вы просто добавляете аннотации.

### Почему Lombok?

**Lombok** решает следующие проблемы:**

1. **Уменьшение boilerplate кода** — Автоматическая генерация геттеров, сеттеров, конструкторов
2. **Читаемость кода** — Фокус на бизнес-логике, а не на инфраструктурном коде
3. **Сокращение ошибок** — Меньше кода = меньше места для ошибок
4. **Производительность разработки** — Быстрее писать и поддерживать код
5. **Совместимость** — Работает с любыми **Java** фреймворками и инструментами
6. **Нулевая производительность** — Код генерируется во время компиляции
7. **Безопасность** — Генерируемый код безопасен и предсказуем

### Как работает Lombok?

**Lombok** использует **Annotation `Processing Tool` (APT)** для генерации кода во время компиляции. Аннотации **Lombok** анализируются процессором аннотаций, который генерирует соответствующий **Java** код.

### Преимущества и недостатки

**Преимущества:**
- Значительное сокращение кода
- Улучшение читаемости
- Меньше ошибок типизации
- Быстрая разработка

**Недостатки:**
- «Магическая» генерация кода
- Сложность отладки
- Зависимость от **IDE** плагина
- Возможные проблемы с рефакторингом

## Установка и настройка

### Maven

Подключение **Lombok** в **Maven**: зависимость с **scope provided** (генерация на этапе компиляции).

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```kotlin
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Для testCompile
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}
```

### Настройка в IDE

#### IntelliJ IDEA
1. **Установка плагина:**
   - **File** → **Settings** → **Plugins**
   - Поиск "**Lombok**"
   - Установка и перезапуск **IDE**

2. **Annotation `Processing`:**
   - **File** → **Settings** → **Build**, **Execution**, **Deployment** → **Compiler** → **Annotation Processors**
   - Включить "**Enable annotation processing**"

#### Eclipse
1. **Установка плагина:**
   - **Help** → **Eclipse Marketplace**
   - Поиск "**Lombok**"
   - Установка

2. **Настройка:**
   - Плагин автоматически настраивает **annotation processing**

#### `VS` Code
- Установка расширения "**Lombok Annotations Support**"

## Основные аннотации

### `@Getter` и `@Setter`

Автоматическая генерация геттеров и сеттеров для полей класса.

```java
import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter @Setter
    private String name;

    @Getter @Setter
    private int age;

    @Getter @Setter
    private String email;

    // Генерируется:
    // public String getName() { return name; }
    // public void setName(String name) { this.name = name; }
    // public int getAge() { return age; }
    // public void setAge(int age) { this.age = age; }
    // public String getEmail() { return email; }
    // public void setEmail(String email) { this.email = email; }
}
```

### Конфигурация доступа

```java
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

public class User {
    @Getter @Setter
    private String name;  // public getter/setter

    @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
    private int age;  // protected getter/setter

    @Getter @Setter(AccessLevel.NONE)
    private String password;  // only getter, no setter

    @Getter(AccessLevel.NONE) @Setter
    private String internalId;  // only setter, no getter
}
```

## Конструкторы

### `@NoArgsConstructor`

Генерирует конструктор без параметров.

```java
import lombok.NoArgsConstructor;

/
 * Пример использования @NoArgsConstructor аннотации Lombok
 * Генерирует конструктор без параметров (no-args constructor)
 */
@NoArgsConstructor  // Lombok сгенерирует публичный конструктор без параметров
public class User {
    private String name;  // Имя пользователя
    private int age;      // Возраст пользователя

    // Lombok автоматически генерирует следующий конструктор:
    // public User() {
    //     // Пустой конструктор для создания экземпляра без параметров
    //     // Необходим для JPA, Jackson десериализации и других фреймворков
    // }
}
```

### `@AllArgsConstructor`

Генерирует конструктор со всеми полями.

```java
import lombok.AllArgsConstructor;

/
 * Пример использования @AllArgsConstructor аннотации Lombok
 * Генерирует конструктор со всеми полями класса в качестве параметров
 */
@AllArgsConstructor  // Lombok сгенерирует конструктор со всеми полями
public class User {
    private String name;   // Имя пользователя
    private int age;       // Возраст пользователя
    private String email;  // Email адрес пользователя

    // Lombok автоматически генерирует следующий конструктор:
    // public User(String name, int age, String email) {
    //     this.name = name;    // Присваиваем значение поля name
    //     this.age = age;       // Присваиваем значение поля age
    //     this.email = email;   // Присваиваем значение поля email
    // }
    // Параметры конструктора идут в том же порядке, что и поля в классе
}
```

### `@RequiredArgsConstructor`

Генерирует конструктор только для **final** полей и полей, помеченных `@NonNull`.

```java
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

/
 * Пример использования @RequiredArgsConstructor аннотации Lombok
 * Генерирует конструктор только для final полей и полей, помеченных @NonNull
 * Полезно для dependency injection через конструктор
 */
@RequiredArgsConstructor  // Lombok сгенерирует конструктор только для обязательных полей
public class User {
    @NonNull              // Поле помечено как обязательное (не может быть null)
    private final String name;  // Final поле - обязательно включается в конструктор

    private final int age;      // Final поле - обязательно включается в конструктор

    private String email;  // Не final и не @NonNull - НЕ включается в конструктор

    // Lombok автоматически генерирует следующий конструктор:
    // public User(@NonNull String name, int age) {
    //     // Проверка на null для @NonNull поля
    //     if (name == null) {
    //         throw new NullPointerException("name is marked @NonNull but is null");
    //     }
    //     this.name = name;  // Присваиваем final поле name
    //     this.age = age;    // Присваиваем final поле age
    //     // Поле email не включается, так как оно не final и не @NonNull
    // }
}
```

## equals, hashCode и toString

### `@EqualsAndHashCode`

Генерирует методы **equals**() и **hashCode**().

```java
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class User {
    private String name;
    private int age;
    private String email;

    // Генерируется equals() и hashCode() для всех полей
}

@EqualsAndHashCode(of = {"name", "email"})  // только для указанных полей
public class User2 {
    private String name;
    private int age;
    private String email;
}

@EqualsAndHashCode(exclude = {"age"})  // исключая указанные поля
public class User3 {
    private String name;
    private int age;
    private String email;
}
```

### `@ToString`

Генерирует метод **toString**().

```java
import lombok.ToString;

@ToString
public class User {
    private String name;
    private int age;
    private String email;

    // Генерируется:
    // public String toString() {
    //     return "User(name=" + name + ", age=" + age + ", email=" + email + ")";
    // }
}

@ToString(of = {"name", "age"})  // только указанные поля
public class User2 {
    private String name;
    private int age;
    private String email;
}

@ToString(callSuper = true)  // включить поля суперкласса
public class ExtendedUser extends User {
    private String department;
}
```

## Builder паттерн

### `@Builder`

Генерирует **builder** паттерн для класса.

```java
import lombok.Builder;
import lombok.Data;

@Builder
public class User {
    private String name;
    private int age;
    private String email;
    private String department;

    // Генерируется:
    // public static UserBuilder builder() { ... }
    // public User build() { ... }
    // методы с именами полей для установки значений
}

// Использование:
User user = User.builder()
    .name("John Doe")
    .age(30)
    .email("john@example.com")
    .department("IT")
    .build();
```

### `@Builder` с конфигурацией

```java
import lombok.Builder;
import lombok.Singular;

@Builder(builderClassName = "CustomBuilder", toBuilder = true)
public class User {
    private String name;
    private int age;

    @Singular
    private List<String> roles;  // для коллекций

    // Генерируется CustomBuilder вместо UserBuilder
    // и метод toBuilder() для создания builder из существующего объекта
}

// Использование:
User user1 = User.builder()
    .name("John")
    .age(30)
    .role("ADMIN")  // singular для List<String>
    .role("USER")
    .build();

User user2 = user1.toBuilder()
    .age(31)
    .build();
```

### `@SuperBuilder`

Для наследования в **builder** паттерне.

```java
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Person {
    protected String name;
    protected int age;
}

@SuperBuilder
public class Employee extends Person {
    private String department;
    private double salary;
}

// Использование:
Employee employee = Employee.builder()
    .name("John Doe")
    .age(30)
    .department("IT")
    .salary(75000.0)
    .build();
```

## Data классы

### `@Data`

Комбинированная аннотация: `@Getter`, `@Setter`, `@ToString`, `@EqualsAndHashCode`, `@RequiredArgsConstructor`.

```java
import lombok.Data;

@Data
public class User {
    private final String id;  // final поле
    private String name;      // обычное поле
    private int age;          // обычное поле
    private String email;     // обычное поле

    // Генерируется:
    // - геттеры и сеттеры для всех полей
    // - toString()
    // - equals() и hashCode()
    // - конструктор для final полей (id)
}
```

### `@Data` с исключениями

```java
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"password", "temporaryField"})
public class User {
    private String name;
    private String email;
    private String password;      // не включается в equals/hashCode
    private String temporaryField; // не включается в equals/hashCode

    @ToString.Exclude
    private String sensitiveData;  // не включается в toString
}
```

## Value объекты

### `@Value`

Создает **immutable** класс: **final** поля, `@Getter`, `@EqualsAndHashCode`, `@ToString`, `@RequiredArgsConstructor`.

```java
import lombok.Value;

@Value
public class Address {
    String street;
    String city;
    String country;
    String zipCode;

    // Все поля final
    // Геттеры для всех полей
    // equals/hashCode/toString
    // Конструктор со всеми полями
}

@Value(staticConstructor = "of")  // статический фабричный метод
public class Money {
    BigDecimal amount;
    String currency;

    // Money.of(amount, currency)
}
```

## Логирование

### `@Log`, `@Slf4j`, `@Log4j2` и др.

```java
import lombok.extern.slf4j.Slf4j;
import lombok.extern.log4j.Log4j2;

@Slf4j  // для SLF4J
public class UserService {

    public void createUser(String name) {
        log.info("Creating user: {}", name);

        try {
            // бизнес логика
            log.debug("User created successfully");
        } catch (Exception e) {
            log.error("Failed to create user", e);
        }
    }
}

@Log4j2  // для Log4j2
public class ProductService {

    public void updateProduct(String id) {
        log.info("Updating product with id: {}", id);
        // ...
    }
}
```

### Доступные аннотации логирования

- `@Log` — **java.util.logging**
- `@CommonsLog` — **Apache Commons Logging**
- `@Log4j` — **Apache Log4j**
- `@Log4j2` — **Apache Log4j2**
- `@Slf4j` — **SLF4J**
- `@XSlf4j` — **SLF4J** с дополнительными методами
- `@JBossLog` — **JBoss Logging**

## Дополнительные возможности

### @NonNull

```java
import lombok.NonNull;

public class UserService {

    public void createUser(@NonNull String name, @NonNull String email) {
        // Генерируется проверка на null:
        // if (name == null) throw new NullPointerException("name");
        // if (email == null) throw new NullPointerException("email");
    }
}
```

### `@Cleanup`

Автоматическое закрытие ресурсов.

```java
import lombok.Cleanup;
import java.io.*;

public class FileProcessor {

    public void processFile(String path) throws IOException {
        @Cleanup FileInputStream fis = new FileInputStream(path);
        @Cleanup InputStreamReader isr = new InputStreamReader(fis);
        @Cleanup BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        // Автоматически вызывается close() для всех @Cleanup ресурсов
    }
}
```

### `@SneakyThrows`

Позволяет выбрасывать **checked** исключения без объявления в сигнатуре метода.

```java
import lombok.SneakyThrows;

public class FileService {

    @SneakyThrows(IOException.class)
    public String readFile(String path) {
        // Можно бросать IOException без throws в сигнатуре
        throw new IOException("File not found");
    }

    @SneakyThrows({IOException.class, InterruptedException.class})
    public void processWithMultipleExceptions() {
        Thread.sleep(1000);
        throw new IOException("Processing failed");
    }
}
```

### `@Synchronized`

Безопасная синхронизация.

```java
import lombok.Synchronized;

public class Counter {
    private int count = 0;

    @Synchronized
    public void increment() {
        count++;
    }

    @Synchronized
    public int getCount() {
        return count;
    }

    // Можно указать объект синхронизации
    private final Object lock = new Object();

    @Synchronized("lock")
    public void safeOperation() {
        // Синхронизация на объекте lock
    }
}
```

### `@Getter(lazy = true)`

**Lazy** инициализация для геттеров.

```java
import lombok.Getter;

public class ExpensiveResource {
    @Getter(lazy = true)
    private final String expensiveValue = computeExpensiveValue();

    private String computeExpensiveValue() {
        // Дорогая операция
        return "computed value";
    }
}
```

### `@Delegate`

Делегирование методов.

```java
import lombok.experimental.Delegate;

public class EnhancedList<T> implements List<T> {

    @Delegate
    private final List<T> delegate = new ArrayList<>();

    public void customMethod() {
        // Дополнительная логика
    }
}
```

## Интеграция с IDE

### Lombok Configuration

**Создайте файл `lombok.config` в корне проекта:**

```properties
# Настройки Lombok для всего проекта
config.stopBubbling = true
lombok.addLombokGeneratedAnnotation = true
lombok.anyConstructor.addConstructorProperties = true
lombok.equalsAndHashCode.callSuper = call
lombok.getter.noIsPrefix = true
lombok.setter.flagUsage = warning
lombok.toString.includeFieldNames = true
```

### Настройки для конкретных директорий

```text
# Настройки только для тестов
src/test/java/lombok.config:
lombok.addLombokGeneratedAnnotation = false
```

### Lombok MapStruct Integration

```java
// lombok.config
lombok.addLombokGeneratedAnnotation = true
lombok.anyConstructor.addConstructorProperties = true
```

## Best practices

### 1. Использование `@Data` разумно

```java
// ✅ Хорошо - для простых DTO
@Data
public class UserDto {
    private String name;
    private String email;
}

// ❌ Плохо - для entity классов
@Data
@Entity
public class UserEntity {
    @Id
    private Long id;
    private String name;
    // equals/hashCode включает id - может вызвать проблемы
}

// ✅ Лучше - явное управление
@Entity
public class UserEntity {
    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @EqualsAndHashCode.Include
    private String email;

    // Остальные поля не включаются в equals/hashCode
    private String name;
    private LocalDateTime createdAt;
}
```

### 2. Builder паттерн

```java
// ✅ Хорошо - для сложных объектов
@Builder
public class ComplexObject {
    @NonNull
    private String requiredField1;

    @NonNull
    private String requiredField2;

    private String optionalField1;
    private String optionalField2;
}

// Использование:
ComplexObject obj = ComplexObject.builder()
    .requiredField1("value1")
    .requiredField2("value2")
    .optionalField1("opt1")
    .build();
```

### 3. Value объекты для immutable данных

```java
// ✅ Хорошо - для immutable данных
@Value
public class Configuration {
    String host;
    int port;
    String username;
    String password;
}

// Использование:
Configuration config = new Configuration("localhost", 8080, "user", "pass");
```

### 4. Логирование

```java
// ✅ Хорошо - правильное использование @Slf4j
@Slf4j
@Service
public class UserService {

    public void processUser(User user) {
        log.debug("Processing user: {}", user.getId());

        if (log.isTraceEnabled()) {
            log.trace("Detailed user data: {}", user);
        }

        try {
            // бизнес логика
            log.info("User {} processed successfully", user.getId());
        } catch (Exception e) {
            log.error("Failed to process user {}", user.getId(), e);
        }
    }
}
```

### 5. `@NonNull` для валидации

```java
// ✅ Хорошо - валидация параметров
@Service
public class ValidationService {

    public void validateUser(@NonNull String name, @NonNull String email) {
        // Lombok автоматически генерирует проверки на null
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
```

### 6. `@Cleanup` для ресурсов

```java
// ✅ Хорошо - автоматическое закрытие ресурсов
@Slf4j
public class FileProcessor {

    @SneakyThrows(IOException.class)
    public void processFile(@NonNull String path) {
        @Cleanup FileInputStream fis = new FileInputStream(path);
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            processLine(line);
            lineCount++;
        }

        log.info("Processed {} lines from file: {}", lineCount, path);
    }

    private void processLine(String line) {
        // Обработка строки
    }
}
```

### 7. Избегайте `@Data` для JPA сущностей

```java
// ❌ Плохо - @Data для JPA
@Data
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;

    @ManyToOne
    private Department department;

    // equals/hashCode будут включать все поля включая department
    // Это может вызвать проблемы с lazy loading
}

// ✅ Лучше - явное управление
@Entity
@Getter
@Setter
@ToString(exclude = "department")  // Исключаем department из toString
@EqualsAndHashCode(exclude = "department")  // Исключаем department из equals/hashCode
public class User {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}
```


## Заключение

**Lombok** — это инструмент для сокращения **boilerplate** кода в **Java** проектах. Он значительно улучшает читаемость и поддерживаемость кода, позволяя разработчикам сосредоточиться на бизнес-логике.

### Когда использовать Lombok

**Рекомендуется:**
- **DTO** классы
- **Entity** классы (с осторожностью)
- Конфигурационные классы
- **Value** объекты
- **Builder** паттерны
- Логирование

**Осторожно:**
- **JPA Entity** классы (может конфликтовать с lazy loading)
- Классы с наследованием (проверить equals/hashCode)
- **Public API** (генерируемый код может измениться)

### Альтернативы

| Подход | Преимущества | Недостатки |
|--------|-------------|------------|
| **Lombok** | Мало кода, быстрая разработка | «Магическая» генерация |
| **Records (Java 14+)** | Официальный **Java**, **immutable** | Только **data** классы |
| **Кастомные аннотации** | Полный контроль | Сложная разработка |
| **IDE генерация** | Нет зависимостей | Ручная генерация кода |

### Советы по миграции

1. **Начинайте постепенно** — Добавляйте **Lombok** в новые классы
2. **Тестируйте тщательно** — **equals**/**hashCode**/**toString** могут измениться
3. **Документируйте** — Указывайте использование **Lombok** в **README**
4. **Обучайте команду** — Все разработчики должны понимать **Lombok**
5. **Следите за обновлениями** — **Lombok** активно развивается

**Lombok** особенно полезен в **enterprise** проектах с большим количеством **boilerplate** кода, где он может значительно сократить объем кода и улучшить его читаемость.


[⬆️ Наверх](../) | [[java-guava|Следующий: Guava]]

## См. также

- [[java-mapstruct|MapStruct: Маппинг объектов в Java]]
