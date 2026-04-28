---
title: "Jackson: JSON сериализация в Java"
description: "Комплексное руководство по использованию Jackson для работы с JSON в Java приложениях - от основ до продвинутых техник."
tags:
  - libraries
  - serialization
  - jackson
type: "overview"
difficulty: "intermediate"
aliases:
  - "Jackson"
  - "JSON сериализация в Java"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Jackson: JSON сериализация в Java

**Комплексное руководство по использованию `Jackson` для работы с `JSON` в `Java` приложениях — от основ до продвинутых техник.**

## Полезные ссылки

### Официальная документация
- [Jackson Project](https://github.com/FasterXML/jackson) — репозиторий **Jackson**
- [Jackson Documentation](https://github.com/FasterXML/jackson-docs) — документация
- [Jackson Wiki](https://github.com/FasterXML/jackson/wiki) — **Wiki** с примерами

### Интеграция
- [Spring Boot Jackson](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.json) — **Spring Boot** интеграция
- [Jackson Modules](https://github.com/FasterXML/jackson-core) — список модулей
- [Jackson Annotations](https://github.com/FasterXML/jackson-annotations/wiki) — аннотации


### См. также
- [Jackson: JSON-сериализация в Java](../java/java-jackson.md)
- [Micronaut: Serialization — JSON, XML и Custom Serializers](../../frameworks/java-frameworks/micronaut/micronaut-serialization.md)
- [JUnit 5](../testing-libraries/java-junit5.md)
- [Mockito](../testing-libraries/java-mockito.md)
## Содержание

- [Введение в Jackson](#введение-в-jackson)
  - [Почему Jackson?](#почему-jackson)
  - [Основные компоненты](#основные-компоненты)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Базовая настройка](#базовая-настройка)
- [Основы сериализации](#основы-сериализации)
  - [Сериализация простых объектов](#сериализация-простых-объектов)
  - [Сериализация коллекций](#сериализация-коллекций)
  - [Сериализация с pretty printing](#сериализация-с-pretty-printing)
- [Десериализация объектов](#десериализация-объектов)
  - [Десериализация простых объектов](#десериализация-простых-объектов)
  - [Десериализация коллекций](#десериализация-коллекций)
  - [Безопасная десериализация](#безопасная-десериализация)
- [Работа с аннотациями](#работа-с-аннотациями)
  - [Основные аннотации](#основные-аннотации)
  - [Продвинутые аннотации](#продвинутые-аннотации)
- [Настройка ObjectMapper](#настройка-objectmapper)
  - [Глобальная конфигурация](#глобальная-конфигурация)
  - [Кастомные сериализаторы и десериализаторы](#кастомные-сериализаторы-и-десериализаторы)
- [Обработка сложных типов](#обработка-сложных-типов)
  - [Generic типы](#generic-типы)
  - [Работа с JSON Tree Model](#работа-с-json-tree-model)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Оптимизация сериализации](#оптимизация-сериализации)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Автоматическая конфигурация](#автоматическая-конфигурация)
  - [Spring Boot Properties](#spring-boot-properties)
- [Лучшие практики](#лучшие-практики)
  - [1. Переиспользование ObjectMapper](#1-переиспользование-objectmapper)
  - [2. Обработка ошибок](#2-обработка-ошибок)
  - [3. Производительность](#3-производительность)
- [Заключение](#заключение)
  - [Преимущества Jackson](#преимущества-jackson)
  - [Когда использовать Jackson](#когда-использовать-jackson)
  - [Основные паттерны использования](#основные-паттерны-использования)
- [Экспериментальные и новые возможности](#экспериментальные-и-новые-возможности)
  - [Jackson 2.15+ Features](#jackson-215-features)
    - [JsonPointer Support](#jsonpointer-support)
    - [Property-based Filtering](#property-based-filtering)
    - [TypeFactory Enhancements](#typefactory-enhancements)
  - [Blackbird Module (Jackson 2.12+)](#blackbird-module-jackson-212)
  - [Afterburner Module](#afterburner-module)
- [Продвинутые возможности сериализации](#продвинутые-возможности-сериализации)
  - [Custom Serializer с Context](#custom-serializer-с-context)
  - [Streaming API с фильтрами](#streaming-api-с-фильтрами)
- [Десериализация сложных структур](#десериализация-сложных-структур)
  - [External Type Id Resolution](#external-type-id-resolution)
  - [Custom Deserializer с Dependency Injection](#custom-deserializer-с-dependency-injection)
- [Модульная архитектура](#модульная-архитектура)
  - [Создание собственных модулей](#создание-собственных-модулей)
  - [Dynamic Module Loading](#dynamic-module-loading)
- [Производительность и оптимизация](#производительность-и-оптимизация-1)
  - [Buffer Recycling](#buffer-recycling)
  - [Memory-Mapped Files](#memory-mapped-files)
- [Интеграция с другими технологиями](#интеграция-с-другими-технологиями)
  - [Reactive Streams](#reactive-streams)
  - [Akka Streams Integration](#akka-streams-integration)
- [Тестирование Jackson кода](#тестирование-jackson-кода)
  - [Jackson Test Utils](#jackson-test-utils)
  - [Property-based Testing с Jackson](#property-based-testing-с-jackson)
- [Миграция и обновление](#миграция-и-обновление)
  - [Migration Guide (Jackson 2.x)](#migration-guide-jackson-2x)
- [Решение проблем и дебаггинг](#решение-проблем-и-дебаггинг)
  - [Debug Logging](#debug-logging)
  - [Common Issues и Solutions](#common-issues-и-solutions)
- [Альтернативы](#альтернативы)
- [См. также](#см-также-1)

## Введение в Jackson

**Jackson** — это высокопроизводительная библиотека для обработки **JSON** в **Java**. Она предоставляет мощный **API** для сериализации (преобразования объектов в JSON) и десериализации (преобразования `JSON` в объекты).

### Почему Jackson?

**Jackson** предлагает несколько ключевых преимуществ:**

1. **Высокая производительность** — Одна из самых быстрых **JSON** библиотек для **Java**
2. **Гибкость** — Поддержка множества форматов и конфигураций
3. **Обширная экосистема** — Большое количество модулей и расширений
4. **Простота использования** — Интуитивный **API** с хорошими **defaults**
5. **Широкая поддержка** — Используется в **Spring Boot**, **Hibernate** и других фреймворках
6. **Streaming API** — Возможность обработки больших **JSON** файлов
7. **Tree Model** — Гибкая работа с **JSON** как с деревом объектов
8. **Data Binding** — Автоматическое преобразование между объектами и **JSON**

### Основные компоненты

- **ObjectMapper** — Главный класс для сериализации/десериализации
- **JsonParser** — **Streaming API** для чтения **JSON**
- **JsonGenerator** — **Streaming API** для записи **JSON**
- **JsonNode** — **Tree model** для работы с **JSON**
- **TypeReference** — Для работы с **generic** типами
- **Annotations** — Аннотации для настройки сериализации

## Установка и настройка

### Maven

Зависимость **Maven** для **Jackson Databind** (сериализация и десериализация JSON).

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>

<!-- Для работы с датами -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.15.2</version>
</dependency>

<!-- Для работы с Kotlin -->
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
    <version>2.15.2</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}
```

### Базовая настройка

**ObjectMapper** — центральный класс Jackson. Ниже пример конфигурации с поддержкой Java 8 Date/Time:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Конфигурация ObjectMapper для Jackson
 * Настраивает основные параметры сериализации и десериализации JSON
 */
public class JacksonConfig {

    /**
     * Создание настроенного ObjectMapper с поддержкой Java 8 Date/Time API
     * @return настроенный ObjectMapper для работы с JSON
     */
    public static ObjectMapper createObjectMapper() {
        // Создаем новый экземпляр ObjectMapper - основной класс для работы с JSON
        ObjectMapper mapper = new ObjectMapper();

        // Регистрация модуля для Java 8 Date/Time API (LocalDate, LocalDateTime, ZonedDateTime и т.д.)
        // Без этого модуля Jackson не сможет правильно сериализовать/десериализовать Java 8 даты
        mapper.registerModule(new JavaTimeModule());

        // Настройки форматирования JSON вывода
        // INDENT_OUTPUT включает красивое форматирование с отступами для читаемости
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Отключаем запись дат как timestamp (миллисекунды с 1970 года)
        // Вместо этого даты будут записываться в ISO-8601 формате (например, "2023-01-15T10:30:00")
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Возвращаем настроенный ObjectMapper
        return mapper;
    }
}
```

## Основы сериализации

### Сериализация простых объектов

```java
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicSerialization {

    /**
     * Простой класс пользователя для демонстрации сериализации
     * Все поля публичные для упрощения примера
     */
    static class User {
        public String name;  // Имя пользователя
        public int age;      // Возраст пользователя
        public String email; // Email адрес пользователя

        /**
         * Пустой конструктор необходим для десериализации
         * Jackson использует его для создания экземпляра объекта
         */
        public User() {}

        /**
         * Конструктор с параметрами для создания объекта
         * @param name имя пользователя
         * @param age возраст пользователя
         * @param email email адрес пользователя
         */
        public User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
    }

    /**
     * Основной метод демонстрации базовой сериализации
     * Показывает различные способы преобразования объекта в JSON
     */
    public static void main(String[] args) throws Exception {
        // Создаем экземпляр ObjectMapper - основной класс для работы с JSON
        ObjectMapper mapper = new ObjectMapper();

        // Создаем объект пользователя для сериализации
        User user = new User("John Doe", 30, "john@example.com");

        // Сериализация в JSON строку - самый распространенный способ
        // writeValueAsString преобразует объект в строку JSON
        String json = mapper.writeValueAsString(user);
        System.out.println(json);
        // Output: {"name":"John Doe","age":30,"email":"john@example.com"}

        // Сериализация в файл - полезно для сохранения данных
        // Файл будет создан автоматически, если не существует
        mapper.writeValue(new File("user.json"), user);

        // Сериализация в byte array - полезно для передачи по сети
        // или сохранения в базу данных как BLOB
        byte[] jsonBytes = mapper.writeValueAsBytes(user);
    }
}
```

### Сериализация коллекций

```java
import java.util.*;

public class CollectionSerialization {

    /**
     * Демонстрация сериализации различных типов коллекций
     * Jackson автоматически обрабатывает стандартные Java коллекции
     */
    public static void main(String[] args) throws Exception {
        // Создаем ObjectMapper для преобразования объектов
        ObjectMapper mapper = new ObjectMapper();

        // Сериализация List - преобразуется в JSON массив
        // List сохраняет порядок элементов
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        String jsonList = mapper.writeValueAsString(names);
        System.out.println(jsonList);
        // Output: ["Alice","Bob","Charlie"]

        // Сериализация Map - преобразуется в JSON объект
        // Ключи Map становятся ключами JSON объекта
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);    // Добавляем оценку для Alice
        scores.put("Bob", 87);      // Добавляем оценку для Bob
        scores.put("Charlie", 92);  // Добавляем оценку для Charlie

        String jsonMap = mapper.writeValueAsString(scores);
        System.out.println(jsonMap);
        // Output: {"Charlie":92,"Bob":87,"Alice":95}
        // Порядок может отличаться, так как HashMap не гарантирует порядок

        // Сериализация Set - преобразуется в JSON массив
        // Set автоматически удаляет дубликаты
        Set<String> uniqueNames = new HashSet<>(names);
        String jsonSet = mapper.writeValueAsString(uniqueNames);
        System.out.println(jsonSet);
        // Порядок элементов может отличаться от исходного List
    }
}
```

### Сериализация с pretty printing

```java
public class PrettyPrinting {

    static class Address {
        public String street;
        public String city;
        public String country;

        public Address(String street, String city, String country) {
            this.street = street;
            this.city = city;
            this.country = country;
        }
    }

    static class Person {
        public String name;
        public int age;
        public Address address;

        public Person(String name, int age, Address address) {
            this.name = name;
            this.age = age;
            this.address = address;
        }
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Person person = new Person(
            "John Doe",
            30,
            new Address("123 Main St", "Springfield", "USA")
        );

        // Компактный JSON
        String compactJson = mapper.writeValueAsString(person);
        System.out.println("Compact: " + compactJson);

        // Форматированный JSON
        String prettyJson = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(person);
        System.out.println("Pretty:\n" + prettyJson);

        // Настройка pretty printer
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String indentedJson = mapper.writeValueAsString(person);
        System.out.println("Indented:\n" + indentedJson);
    }
}
```

## Десериализация объектов

### Десериализация простых объектов

```java
public class BasicDeserialization {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = "{\"name\":\"Jane Doe\",\"age\":25,\"email\":\"jane@example.com\"}";

        // Десериализация в объект
        User user = mapper.readValue(json, User.class);
        System.out.println("Name: " + user.name);
        System.out.println("Age: " + user.age);
        System.out.println("Email: " + user.email);

        // Десериализация из файла
        User userFromFile = mapper.readValue(new File("user.json"), User.class);

        // Десериализация из InputStream
        try (InputStream is = new FileInputStream("user.json")) {
            User userFromStream = mapper.readValue(is, User.class);
        }
    }
}
```

### Десериализация коллекций

```java
public class CollectionDeserialization {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Десериализация массива в List
        String jsonArray = "[\"Alice\",\"Bob\",\"Charlie\"]";
        List<String> names = mapper.readValue(jsonArray,
            mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        System.out.println("Names: " + names);

        // Более простой способ с TypeReference
        List<String> names2 = mapper.readValue(jsonArray, new TypeReference<List<String>>() {});
        System.out.println("Names2: " + names2);

        // Десериализация объекта в Map
        String jsonMap = "{\"Alice\":95,\"Bob\":87,\"Charlie\":92}";
        Map<String, Integer> scores = mapper.readValue(jsonMap, new TypeReference<Map<String, Integer>>() {});
        System.out.println("Scores: " + scores);
    }
}
```

### Безопасная десериализация

```java
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SafeDeserialization {

    public static void safeDeserialize(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            User user = mapper.readValue(json, User.class);
            System.out.println("Successfully deserialized: " + user.name);
        } catch (JsonProcessingException e) {
            System.err.println("JSON processing error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Корректный JSON
        safeDeserialize("{\"name\":\"John\",\"age\":30,\"email\":\"john@example.com\"}");

        // Некорректный JSON
        safeDeserialize("{\"name\":\"John\",\"age\":\"thirty\"}");

        // Неполный JSON
        safeDeserialize("{\"name\":\"John\"}");

        // Extra поля (по умолчанию игнорируются)
        safeDeserialize("{\"name\":\"John\",\"age\":30,\"email\":\"john@example.com\",\"extra\":\"field\"}");
    }
}
```

## Работа с аннотациями

### Основные аннотации

```java
import com.fasterxml.jackson.annotation.*;

public class AnnotationExamples {

    static class User {
        @JsonProperty("full_name")  // Изменение имени поля в JSON
        public String name;

        @JsonIgnore  // Игнорирование поля при сериализации
        public String password;

        @JsonInclude(JsonInclude.Include.NON_NULL)  // Игнорирование null значений
        public String nickname;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  // Формат даты
        public LocalDate birthDate;

        public User() {}

        public User(String name, String password, String nickname, LocalDate birthDate) {
            this.name = name;
            this.password = password;
            this.nickname = nickname;
            this.birthDate = birthDate;
        }
    }

    static class Product {
        public String name;
        public double price;

        @JsonCreator  // Кастомный конструктор для десериализации
        public Product(@JsonProperty("name") String name,
                      @JsonProperty("price") double price) {
            this.name = name;
            this.price = price;
        }
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        User user = new User("John Doe", "secret123", null,
            LocalDate.of(1990, 1, 15));

        String json = mapper.writeValueAsString(user);
        System.out.println(json);
        // Output: {"full_name":"John Doe","birthDate":"1990-01-15"}

        // Десериализация
        String productJson = "{\"name\":\"Laptop\",\"price\":999.99}";
        Product product = mapper.readValue(productJson, Product.class);
        System.out.println("Product: " + product.name + " - $" + product.price);
    }
}
```

### Продвинутые аннотации

```java
import com.fasterxml.jackson.annotation.*;
import java.util.*;

public class AdvancedAnnotations {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat.class, name = "cat")
    })
    static abstract class Animal {
        public String name;
    }

    static class Dog extends Animal {
        public String breed;
        public boolean goodBoy;

        public Dog() {}
        public Dog(String name, String breed, boolean goodBoy) {
            this.name = name;
            this.breed = breed;
            this.goodBoy = goodBoy;
        }
    }

    static class Cat extends Animal {
        public String color;
        public boolean lazy;

        public Cat() {}
        public Cat(String name, String color, boolean lazy) {
            this.name = name;
            this.color = color;
            this.lazy = lazy;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class PrivateFields {
        private String secret;
        private int number;

        public PrivateFields() {}
        public PrivateFields(String secret, int number) {
            this.secret = secret;
            this.number = number;
        }
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Polymorphic types
        List<Animal> animals = Arrays.asList(
            new Dog("Buddy", "Golden Retriever", true),
            new Cat("Whiskers", "Gray", true)
        );

        String json = mapper.writeValueAsString(animals);
        System.out.println(json);

        // Десериализация полиморфных типов
        List<Animal> deserializedAnimals = mapper.readValue(json,
            new TypeReference<List<Animal>>() {});
        deserializedAnimals.forEach(animal -> {
            System.out.println("Animal: " + animal.name);
        });

        // Private fields
        PrivateFields obj = new PrivateFields("hidden", 42);
        String privateJson = mapper.writeValueAsString(obj);
        System.out.println("Private fields: " + privateJson);
    }
}
```

## Настройка ObjectMapper

### Глобальная конфигурация

```java
public class ObjectMapperConfiguration {

    public static ObjectMapper createConfiguredMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрация модулей
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());

        // Настройки сериализации
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Настройки десериализации
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        // Настройки парсера
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        return mapper;
    }

    public static ObjectMapper createSnakeCaseMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Snake_case naming strategy
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        return mapper;
    }
}
```

### Кастомные сериализаторы и десериализаторы

```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomSerializers {

    static class LocalDateSerializer extends JsonSerializer<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeStringField("date", value.format(FORMATTER));
        }
    }

    static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String dateString = p.getText();
            return LocalDate.parse(dateString, FORMATTER);
        }
    }

    static class Money {
        private final BigDecimal amount;
        private final String currency;

        public Money(BigDecimal amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        public BigDecimal getAmount() { return amount; }
        public String getCurrency() { return currency; }

        @Override
        public String toString() {
            return amount + " " + currency;
        }
    }

    static class MoneySerializer extends JsonSerializer<Money> {
        @Override
        public void serialize(Money value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeStartObject();
            gen.writeStringField("amount", value.getAmount().toString());
            gen.writeStringField("currency", value.getCurrency());
            gen.writeEndObject();
        }
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрация кастомных сериализаторов
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addSerializer(Money.class, new MoneySerializer());

        mapper.registerModule(module);

        // Тестирование
        LocalDate date = LocalDate.of(2023, 12, 25);
        String dateJson = mapper.writeValueAsString(date);
        System.out.println("Date JSON: " + dateJson);

        LocalDate deserializedDate = mapper.readValue(dateJson, LocalDate.class);
        System.out.println("Deserialized date: " + deserializedDate);

        Money money = new Money(new BigDecimal("123.45"), "USD");
        String moneyJson = mapper.writeValueAsString(money);
        System.out.println("Money JSON: " + moneyJson);
    }
}
```

## Обработка сложных типов

### Generic типы

```java
import java.util.*;

public class GenericTypes {

    static class ApiResponse<T> {
        private boolean success;
        private T data;
        private String message;

        public ApiResponse() {}

        public ApiResponse(boolean success, T data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }

        // getters and setters...
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Сериализация generic типа
        ApiResponse<List<String>> response = new ApiResponse<>(
            true,
            Arrays.asList("item1", "item2", "item3"),
            "Data retrieved successfully"
        );

        String json = mapper.writeValueAsString(response);
        System.out.println("Generic JSON: " + json);

        // Десериализация с TypeReference
        ApiResponse<List<String>> deserialized = mapper.readValue(json,
            new TypeReference<ApiResponse<List<String>>>() {});

        System.out.println("Success: " + deserialized.success);
        System.out.println("Data: " + deserialized.data);
        System.out.println("Message: " + deserialized.message);
    }
}
```

### Работа с JSON Tree Model

```java
import com.fasterxml.jackson.databind.node.*;

public class TreeModel {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Создание JSON дерева программно
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("name", "John Doe");
        rootNode.put("age", 30);

        ObjectNode addressNode = rootNode.putObject("address");
        addressNode.put("street", "123 Main St");
        addressNode.put("city", "Springfield");
        addressNode.put("zipCode", "12345");

        ArrayNode phoneNumbers = rootNode.putArray("phoneNumbers");
        phoneNumbers.add("555-1234");
        phoneNumbers.add("555-5678");

        String json = mapper.writeValueAsString(rootNode);
        System.out.println("Tree JSON:\n" + json);

        // Чтение JSON дерева
        JsonNode readNode = mapper.readTree(json);
        System.out.println("Name: " + readNode.get("name").asText());
        System.out.println("Age: " + readNode.get("age").asInt());
        System.out.println("City: " + readNode.get("address").get("city").asText());

        // Итерация по массиву
        JsonNode phones = readNode.get("phoneNumbers");
        if (phones.isArray()) {
            for (JsonNode phone : phones) {
                System.out.println("Phone: " + phone.asText());
            }
        }

        // Проверка типов
        if (readNode.has("age") && readNode.get("age").isNumber()) {
            System.out.println("Age is a number");
        }
    }
}
```

## Производительность и оптимизация

### Оптимизация сериализации

```java
public class PerformanceOptimization {

    // Рекомендуется переиспользовать ObjectMapper
    private static final ObjectMapper MAPPER = createOptimizedMapper();

    private static ObjectMapper createOptimizedMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Отключение ненужных фич для производительности
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Использование более быстрого генератора
        mapper.getFactory().disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        return mapper;
    }

    // Reuse ObjectMapper instance
    public static String serialize(Object obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }

    // Reuse ObjectReader/ObjectWriter
    private static final ObjectReader USER_READER = MAPPER.readerFor(User.class);
    private static final ObjectWriter USER_WRITER = MAPPER.writerFor(User.class);

    public static User deserializeUser(String json) throws Exception {
        return USER_READER.readValue(json);
    }

    public static String serializeUser(User user) throws Exception {
        return USER_WRITER.writeValueAsString(user);
    }
}
```

## Интеграция с Spring Boot

### Автоматическая конфигурация

```java
@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрация модулей
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());

        // Настройки для Spring Boot
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Snake_case для API
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        return mapper;
    }
}
```

### Spring Boot Properties

```yaml
# application.yml
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE
    serialization:
      indent-output: true
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
    default-property-inclusion: non_null
```

## Лучшие практики

### 1. Переиспользование ObjectMapper

```java
// ✅ Хорошо
@Service
public class JsonService {
    private final ObjectMapper mapper;

    public JsonService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }
}

// ❌ Плохо
public class BadJsonService {
    public String toJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); // Каждый раз новый!
        return mapper.writeValueAsString(obj);
    }
}
```

### 2. Обработка ошибок

```java
// ✅ Хорошо
public Optional<User> parseUser(String json) {
    try {
        return Optional.of(objectMapper.readValue(json, User.class));
    } catch (JsonProcessingException e) {
        log.error("Failed to parse user JSON: {}", e.getMessage());
        return Optional.empty();
    }
}
```

### 3. Производительность

```java
// ✅ Хорошо - использование views
@JsonView(Views.Public.class)
public class User {
    public String name;

    @JsonView(Views.Internal.class)
    public String email;

    @JsonView(Views.Admin.class)
    public String ssn;
}

public interface Views {
    class Public {}
    class Internal extends Public {}
    class Admin extends Internal {}
}

// Использование
String publicJson = mapper.writerWithView(Views.Public.class)
    .writeValueAsString(user);

String internalJson = mapper.writerWithView(Views.Internal.class)
    .writeValueAsString(user);
```


## Заключение

**Jackson** — это мощная и гибкая библиотека для работы с **JSON** в **Java**, которая является стандартом де-факто для большинства **Java** приложений.

### Преимущества Jackson

1. **Высокая производительность** — Одна из самых быстрых **JSON** библиотек
2. **Гибкость** — Множество способов настройки и расширения
3. **Обширная экосистема** — Большое количество модулей и интеграций
4. **Простота использования** — Интуитивный **API** с хорошими **defaults**
5. **Широкая поддержка** — **Spring Boot**, **Hibernate** и другие фреймворки
6. **Streaming API** — Эффективная обработка больших **JSON** файлов
7. **Tree Model** — Гибкая работа с **JSON** структурой
8. **Annotation-based configuration** — Легкая настройка через аннотации

### Когда использовать Jackson

- **REST API** — Сериализация/десериализация для **HTTP API**
- **Конфигурационные файлы** — Чтение **JSON** конфигураций
- **Data exchange** — Обмен данными между сервисами
- **Caching** — Сериализация объектов для кэширования
- **Logging** — Структурированное логирование в **JSON** формате
- **Database** — Работа с **JSON** полями в базах данных

### Основные паттерны использования

1. **DTO паттерн** — **Data Transfer Objects** для **API**
2. **Builder паттерн** — Для сложных объектов конфигурации
3. **Factory паттерн** — Для создания настроенных **ObjectMapper**
4. **Decorator паттерн** — Для добавления дополнительной логики

## Экспериментальные и новые возможности

### Jackson 2.15+ Features

#### JsonPointer Support
```java
// Работа с JSON Pointer (RFC 6901)
ObjectMapper mapper = new ObjectMapper();
JsonNode root = mapper.readTree(jsonString);

// Доступ к элементам через JSON Pointer
JsonNode name = root.at("/user/name");
JsonNode firstItem = root.at("/items/0");

// Создание JSON Pointer
JsonPointer pointer = JsonPointer.compile("/user/address/city");
JsonNode city = root.at(pointer);
```

#### Property-based Filtering
```java
// Фильтрация свойств на основе аннотаций
@JsonFilter("dynamicFilter")
public class User {
    public String name;
    public String password; // будет отфильтровано
    public String email;
}

ObjectMapper mapper = new ObjectMapper();
FilterProvider filters = new SimpleFilterProvider()
    .addFilter("dynamicFilter",
        SimpleBeanPropertyFilter.filterOutAllExcept("name", "email"));

mapper.setFilterProvider(filters);
String filtered = mapper.writeValueAsString(user);
```

#### TypeFactory Enhancements
```java
// Создание generic типов
TypeFactory tf = TypeFactory.defaultInstance();
JavaType listType = tf.constructCollectionType(List.class, String.class);
JavaType mapType = tf.constructMapType(HashMap.class, String.class, Integer.class);

// Параметризованные типы
JavaType paramType = tf.constructParametricType(List.class, User.class);
JavaType wildCardType = tf.constructType(new TypeReference<List<? extends Number>>() {});
```

### Blackbird Module (Jackson 2.12+)
```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-blackbird</artifactId>
    <version>2.15.2</version>
</dependency>
```

```java
// Автоматическая регистрация Blackbird для улучшения производительности
ObjectMapper mapper = JsonMapper.builder()
    .addModule(new BlackbirdModule())
    .build();
```

### Afterburner Module
```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-afterburner</artifactId>
    <version>2.15.2</version>
</dependency>
```

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new AfterburnerModule());
```

## Продвинутые возможности сериализации

### Custom Serializer с Context
```java
public class ContextAwareSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator gen,
                         SerializerProvider provider) throws IOException {
        // Доступ к контексту сериализации
        SerializationConfig config = provider.getConfig();

        gen.writeStartObject();
        gen.writeStringField("name", user.getName());

        // Условная сериализация на основе контекста
        if (config.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            gen.writeNumberField("timestamp", user.getCreatedAt().getTime());
        } else {
            gen.writeStringField("createdAt",
                DateTimeFormatter.ISO_DATE_TIME.format(user.getCreatedAt()));
        }
        gen.writeEndObject();
    }
}
```

### Streaming API с фильтрами
```java
public class FilteredJsonWriter {
    public void writeFilteredJson(JsonGenerator gen, User user) throws IOException {
        FilteringGeneratorDelegate filter = new FilteringGeneratorDelegate(gen,
            new NameFilteringWriter("name", "email"), // только name и email
            true, true);

        gen.writeStartObject();
        gen.writeStringField("name", user.getName());
        gen.writeStringField("email", user.getEmail());
        gen.writeStringField("password", user.getPassword()); // будет отфильтровано
        gen.writeEndObject();
    }
}
```

## Десериализация сложных структур

### External Type Id Resolution
```java
public class CustomTypeIdResolver extends TypeIdResolverBase {
    private JavaType baseType;

    @Override
    public void init(JavaType bt) {
        baseType = bt;
    }

    @Override
    public Id getMechanism() {
        return Id.CUSTOM;
    }

    @Override
    public String idFromValue(Object value) {
        if (value instanceof Dog) return "dog";
        if (value instanceof Cat) return "cat";
        return null;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        switch (id) {
            case "dog": return context.constructType(Dog.class);
            case "cat": return context.constructType(Cat.class);
            default: throw new IllegalArgumentException("Unknown type: " + id);
        }
    }
}
```

### Custom Deserializer с Dependency Injection
```java
@Component
public class UserDeserializer extends JsonDeserializer<User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        // Доступ к Spring контексту во время десериализации
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        User user = new User();
        user.setName(node.get("name").asText());
        user.setEmail(node.get("email").asText());

        // Дополнительная бизнес-логика
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        return user;
    }
}
```

## Модульная архитектура

### Создание собственных модулей
```java
public class CustomJacksonModule extends SimpleModule {
    public CustomJacksonModule() {
        super("CustomModule", new Version(1, 0, 0, null, null, null));

        // Регистрация сериализаторов
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());

        // Регистрация типов
        setMixInAnnotation(User.class, UserMixin.class);

        // Регистрация key десериализаторов
        addKeyDeserializer(UUID.class, new UUIDKeyDeserializer());
    }
}
```

### Dynamic Module Loading
```java
public class ModuleLoader {
    public ObjectMapper createMapperWithModules() {
        ObjectMapper mapper = new ObjectMapper();

        // Автоматическое обнаружение и загрузка модулей
        ServiceLoader<Module> modules = ServiceLoader.load(Module.class);
        for (Module module : modules) {
            mapper.registerModule(module);
        }

        return mapper;
    }
}
```

## Производительность и оптимизация

### Buffer Recycling
```java
public class OptimizedObjectMapper {
    private final ObjectMapper mapper;

    public OptimizedObjectMapper() {
        mapper = new ObjectMapper();

        // Настройка буферов
        mapper.getFactory().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.getFactory().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
    }

    public <T> T readValue(ReadableByteChannel channel, Class<T> type) throws IOException {
        try (JsonParser parser = mapper.getFactory().createParser(channel)) {
            return mapper.readValue(parser, type);
        }
    }
}
```

### Memory-Mapped Files
```java
public class MemoryMappedJsonReader {
    public JsonNode readLargeJsonFile(Path filePath) throws IOException {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ);
             MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())) {

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(new ByteArrayInputStream(buffer.array()));
        }
    }
}
```

## Интеграция с другими технологиями

### Reactive Streams
```java
public class ReactiveJsonProcessor {
    public Flux<JsonNode> processJsonStream(Flux<String> jsonLines) {
        ObjectMapper mapper = new ObjectMapper();

        return jsonLines
            .map(line -> {
                try {
                    return mapper.readTree(line);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
```

### Akka Streams Integration
```java
public class AkkaJsonFlow {
    public Flow<String, JsonNode, NotUsed> createJsonFlow() {
        ObjectMapper mapper = new ObjectMapper();

        return Flow.<String>create()
            .map(line -> mapper.readTree(line));
    }
}
```

## Тестирование Jackson кода

### Jackson Test Utils
```java
public class JacksonTestUtils {

    private final ObjectMapper mapper = new ObjectMapper();

    public void assertJsonEquals(String expectedJson, String actualJson) throws Exception {
        JsonNode expected = mapper.readTree(expectedJson);
        JsonNode actual = mapper.readTree(actualJson);
        assertEquals(expected, actual);
    }

    public void assertSerialization(Object object, String expectedJson) throws Exception {
        String json = mapper.writeValueAsString(object);
        assertJsonEquals(expectedJson, json);
    }
}
```

### Property-based Testing с Jackson
```java
@Property
public void jsonRoundTrip(@ForAll User user) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(user);
    User deserialized = mapper.readValue(json, User.class);
    assertEquals(user, deserialized);
}
```

## Миграция и обновление

### Migration Guide (Jackson 2.x)
```java
public class JacksonMigrationHelper {

    // Обновление с 2.9 на 2.10+
    public ObjectMapper migrateTo210(ObjectMapper oldMapper) {
        return JsonMapper.builder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
    }

    // Обновление с 2.10 на 2.11+
    public ObjectMapper migrateTo211(ObjectMapper oldMapper) {
        return JsonMapper.builder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();
    }
}
```

## Решение проблем и дебаггинг

### Debug Logging
```java
// Включение детального логирования
Logger jacksonLogger = LoggerFactory.getLogger("com.fasterxml.jackson");
((ch.qos.logback.classic.Logger) jacksonLogger).setLevel(Level.DEBUG);

// Или через system property
System.setProperty("logging.level.com.fasterxml.jackson", "DEBUG");
```

### Common Issues и Solutions
```java
public class JacksonTroubleshooting {

    // Проблема: Infinite recursion
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public class User {
        public Long id;
        public List<Order> orders;
    }

    // Проблема: Unknown properties
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ApiResponse {
        public String status;
        // Новые поля будут игнорироваться
    }

    // Проблема: Date serialization
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public LocalDateTime timestamp;
}
```

## Альтернативы

| Библиотека | Преимущества | Недостатки |
|------------|-------------|------------|
| **Jackson** | Быстрая, гибкая, популярная | Сложная конфигурация |
| **Gson** | Простая, быстрая | Меньше возможностей |
| **Moshi** | **Type-safe**, **Kotlin-first** | Только **Android**/**Kotlin** |
| **DSL-JSON** | Самая быстрая | Низкоуровневая |
| **Boon** | Простая, быстрая | Не поддерживается |
| **FastJSON** | Быстрая, простая | Безопасность |
| **JSON-B** | Стандарт **Java** `EE` | Медленная |

**Jackson** рекомендуется как основной выбор для большинства **Java** проектов, особенно **enterprise** приложений с **complex JSON processing requirements**.


[⬆ Наверх](../)

## См. также

- [Gson](java-gson.md)
