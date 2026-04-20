---
title: "Gson"
description: "Gson - это Java библиотека от Google для сериализации и десериализации Java объектов в JSON и обратно. Простая, быстрая и широко используемая альтернатива Jackson."
tags:
  - libraries
  - serialization
  - java-gson
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Gson

**Gson** — это **Java** библиотека от **Google** для сериализации и десериализации **Java** объектов в **JSON** и обратно. Простая, быстрая и широко используемая альтернатива **Jackson**.

## Полезные ссылки

### Официальная документация
- [Gson](https://github.com/google/gson) — **GitHub** репозиторий
- [Gson User Guide](https://github.com/google/gson/blob/master/UserGuide.md) — руководство пользователя
- [Gson API](https://javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/module-summary.html) — **API** документация
- [Gson examples](https://github.com/google/gson/tree/master/examples)
- [Stack Overflow — Gson](https://stackoverflow.com/questions/tagged/gson)

### См. также
- [[jackson|Jackson]] — **Jackson** для **JSON** обработки
- [[kotlin-kotlinx-serialization|Kotlinx Serialization]] — **JSON** библиотека для **Kotlin**
- [[java-protobuf|Protocol Buffers]] — бинарная сериализация

## Содержание

- [Основные возможности](#основные-возможности)
  - [Базовая сериализация и десериализация](#базовая-сериализация-и-десериализация)
  - [Работа с коллекциями](#работа-с-коллекциями)
  - [Работа с массивами](#работа-с-массивами)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Кастомная сериализация с GsonBuilder](#кастомная-сериализация-с-gsonbuilder)
  - [Custom Type Adapters](#custom-type-adapters)
  - [TypeAdapter для сложной логики](#typeadapter-для-сложной-логики)
  - [Exclusion Strategies](#exclusion-strategies)
  - [Runtime Type Adapters](#runtime-type-adapters)
- [Работа с Generics](#работа-с-generics)
  - [Parameterized Types](#parameterized-types)
  - [Wildcard Types](#wildcard-types)
- [Streaming API](#streaming-api)
  - [JsonWriter для потоковой записи](#jsonwriter-для-потоковой-записи)
  - [JsonReader для потокового чтения](#jsonreader-для-потокового-чтения)
  - [Custom Streaming Parser](#custom-streaming-parser)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Reuse Gson Instances](#reuse-gson-instances)
  - [Field Naming Strategies](#field-naming-strategies)
  - [Memory Optimization](#memory-optimization)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [RestTemplate Integration](#resttemplate-integration)
  - [Spring Data Integration](#spring-data-integration)
- [Testing](#testing)
  - [Unit Testing](#unit-testing)
  - [JSON Schema Validation](#json-schema-validation)
- [Error Handling](#error-handling)
  - [JsonParseException Handling](#jsonparseexception-handling)
  - [Custom Error Strategies](#custom-error-strategies)
- [Advanced Features](#advanced-features)
  - [Versioning Support](#versioning-support)
  - [Instance Creators](#instance-creators)
  - [Circular Reference Handling](#circular-reference-handling)
- [Migration Guide](#migration-guide)
  - [From Jackson to Gson](#from-jackson-to-gson)
  - [From org.json to Gson](#from-orgjson-to-gson)
- [Лучшие практики](#лучшие-практики)
  - [Thread Safety](#thread-safety)
  - [Null Handling](#null-handling)
  - [Performance Tips](#performance-tips)
- [Experimental Features](#experimental-features)
  - [Records Support (Java 14+)](#records-support-java-14)
  - [Sealed Classes Support (Java 17+)](#sealed-classes-support-java-17)
- [Решение проблем](#решение-проблем)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)

## Основные возможности

### Базовая сериализация и десериализация

Базовая сериализация объекта в **JSON** и десериализация **JSON** в объект через **Gson**.

```java
/**
 * Демонстрация базовой сериализации и десериализации через Gson
 * Gson автоматически преобразует Java объекты в JSON и обратно
 */
// Создание экземпляра Gson - основной класс для работы с JSON
Gson gson = new Gson();
// Gson по умолчанию использует стандартные настройки сериализации

// Сериализация объекта в JSON - преобразование Java объекта в JSON строку
User user = new User("John", "Doe", 30);  // Создаем Java объект
String json = gson.toJson(user);          // Преобразуем объект в JSON строку
System.out.println(json);  // {"name":"John","lastName":"Doe","age":30}
// Gson автоматически преобразует все поля объекта в JSON формат

// Десериализация JSON в объект - преобразование JSON строки в Java объект
String json = "{\"name\":\"Jane\",\"lastName\":\"Smith\",\"age\":25}";  // JSON строка
User user = gson.fromJson(json, User.class);  // Преобразуем JSON в User объект
System.out.println(user.getName());  // Jane
// Gson автоматически создает объект и заполняет поля значениями из JSON
// User.class указывает в какой тип нужно преобразовать JSON
```

### Работа с коллекциями
```java
/**
 * Демонстрация работы с коллекциями через Gson
 * Gson автоматически обрабатывает List, Set, Map и другие коллекции
 */
Gson gson = new Gson();

// Сериализация List - преобразование списка в JSON массив
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");  // Создаем список строк
String json = gson.toJson(names);  // Преобразуем список в JSON массив
System.out.println(json);  // ["Alice","Bob","Charlie"]
// Gson автоматически преобразует List в JSON массив

// Десериализация List - преобразование JSON массива в список
String json = "[\"Alice\",\"Bob\",\"Charlie\"]";  // JSON массив строк
// TypeToken необходим для правильной десериализации generic типов (List<String>)
// Java type erasure не позволяет напрямую использовать List<String>.class
List<String> names = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
// new TypeToken<List<String>>(){} создает анонимный класс для сохранения типа
// getType() возвращает Type объект который содержит информацию о generic типе

// Сериализация Map - преобразование карты в JSON объект
Map<String, Integer> map = new HashMap<>();
// Gson автоматически преобразует Map в JSON объект где ключи - это свойства объекта
**map.put(**"`Alice`", 25**);
**map.put(**"Bob", 30**);
**String jsonMap** = **gson.`toJson`(**map**);
**System.`out.println`(**jsonMap**); // {"**Alice**":25,"**Bob**":30}

// Десериализация **Map**
**String jsonMap** = "{\"**Alice**\":25,\"**Bob**\":30}";
**Map**<**String**, **Integer**> **map** = **gson.`fromJson`(**jsonMap, new `TypeToken`<Map<`String`, `Integer`>>(**){}.**getType**());
```

### Работа с массивами
```java
`Gson gson` = new `Gson`();

// Сериализация массива
int[] numbers = {1, 2, 3, 4, 5};
`String json` = gson.`toJson`(numbers);
`System`.`out.println`(json); // [1,2,3,4,5]

// Десериализация массива
`String json` = "[1,2,3,4,5]";
int[] numbers = gson.`fromJson`(json, int[].class);

// Сериализация двумерного массива
int[][] matrix = {{1, 2}, {3, 4}};
`String jsonMatrix` = gson.`toJson`(matrix);
`System`.`out.println`(`jsonMatrix`); // [[1,2],[3,4]]
```

## Продвинутые возможности

### Кастомная сериализация с GsonBuilder
```java
`Gson gson` = new `GsonBuilder`()
    .`setPrettyPrinting`()   // JSON
    .`disableHtmlEscaping`()  // HTML
    .`setDateFormat`("`yyyy-`MM`-dd HH`:mm:ss")   // Формат дат
    .`serializeNulls`()   // null значения
    .create();

// Использование
`User user` = new `User`("`John`", `null`, 30);
`String json` = gson.`toJson`(user);
// {
//   "name": "`John`",
//   "`lastName`": `null`,
//   "age": 30
// }
```

### Custom Type Adapters
```java
// Кастомный адаптер для `LocalDate`
public class `LocalDateAdapter` implements `JsonSerializer`<`LocalDate`>, `JsonDeserializer`<`LocalDate`> {

    private static final `DateTimeFormatter` formatter = `DateTimeFormatter`.`ofPattern`("`yyyy-`MM`-dd`");

    `@Override`
    public `JsonElement` serialize(`LocalDate` src, `Type typeOfSrc`, `JsonSerializationContext` context) {
        return new `JsonPrimitive`(`formatter.format`(src));
    }

    `@Override`
    public `LocalDate` deserialize(`JsonElement` json, `Type typeOfT`, `JsonDeserializationContext` context)
            throws `JsonParseException` {
        return `LocalDate`.parse(json.`getAsString`(), formatter);
    }
}

// Регистрация адаптера
`Gson gson` = new `GsonBuilder`()
    .`registerTypeAdapter`(`LocalDate`.class, new `LocalDateAdapter`())
    .create();

// Использование
public class `Event` {
    private `String title`;
    private `LocalDate` date;

    // getters and setters
}

`Event event` = new `Event`("`Conference`", `LocalDate`.of(`2023`, 12, 25));
`String json` = gson.`toJson`(event);  // {"title":"Conference","date":"2023-12-25"}
```

### TypeAdapter для сложной логики
```java
public class `UserTypeAdapter` extends `TypeAdapter`<`User`> {

    `@Override`
    public void write(`JsonWriter` out, `User user`) throws IOException {
        out.`beginObject`();
        `out.name`("`fullName`").value(user.`getName`() + " " + user.`getLastName`());
        `out.name`("age").value(user.`getAge`());

        if (user.`getAge`() >= 18) {
            `out.name`("adult").value(`true`);
        }

        out.`endObject`();
    }

    `@Override`
    public `User read`(`JsonReader` in) throws IOException {
        `User user` = new `User`();
        in.`beginObject`();

        while (in.`hasNext`()) {
            `String name` = in.`nextName`();
            switch (name) {
                case "`fullName`":
                    `String`[] parts = in.`nextString`().split(" ");
                    user.`setName`(parts[0]);
                    user.`setLastName`(parts[1]);
                    break;
                case "age":
                    user.`setAge`(in.`nextInt`());
                    break;
                default:
                    in.`skipValue`();
                    break;
            }
        }

        in.`endObject`();
        return user;
    }
}

// Регистрация
`Gson gson` = new `GsonBuilder`()
    .`registerTypeAdapter`(`User`.class, new `UserTypeAdapter`())
    .create();
```

### Exclusion Strategies
```java
// Исключение полей по аннотациям
public class `User` {
    private `String name`;

    `@Expose`
    private `String email`;

    ``@Expose`(serialize = `false`)`
    private `String password`;

    private transient `String tempData`; // никогда не сериализуется
}

// Использование
`Gson gson` = new `GsonBuilder`()
    .`excludeFieldsWithoutExposeAnnotation`()  // только поля с `@Expose`
    .create();

// Исключение по типу поля
`ExclusionStrategy` strategy = new `ExclusionStrategy`() {
    `@Override`
    public boolean `shouldSkipField`(`FieldAttributes` f) {
        return f.`getAnnotation`(`Exclude`.class) != `null`;
    }

    `@Override`
    public boolean `shouldSkipClass`(`Class`<?> clazz) {
        return `false`;
    }
};

`Gson gson` = new `GsonBuilder`()
    .`setExclusionStrategies`(strategy)
    .create();
```

### Runtime Type Adapters
```java
// Адаптер для runtime type information
public class `RuntimeTypeAdapter`<T> implements `JsonSerializer`<T>, `JsonDeserializer`<T> {

    private final Map<`String`, `Class`<? extends T>> `typeRegistry`;

    public `RuntimeTypeAdapter`(Map<`String`, `Class`<? extends T>> `typeRegistry`) {
        this.`typeRegistry` = `typeRegistry`;
    }

    `@Override`
    public `JsonElement` serialize(T src, `Type typeOfSrc`, `JsonSerializationContext` context) {
        `JsonObject` json = `context.serialize`(src, src.`getClass`()).`getAsJsonObject`();
        json.`addProperty`("type", src.`getClass`().`getSimpleName`());
        return json;
    }

    `@Override`
    public T deserialize(`JsonElement` json, `Type typeOfT`, `JsonDeserializationContext` context)
            throws `JsonParseException` {
        `JsonObject jsonObject` = json.`getAsJsonObject`();
        `String type` = `jsonObject`.get("type").`getAsString`();
        `Class`<? extends T> clazz = `typeRegistry`.get(type);

        if (clazz == `null`) {
            throw new `JsonParseException`("`Unknown type`: " + type);
        }

        return `context.deserialize`(json, clazz);
    }
}
```

## Работа с Generics

### Parameterized Types
```java
// Работа с generic типами
public class `Response`<T> {
    private `String status`;
    private T data;

    // getters and setters
}

// Сериализация
`Response`<`List`<`User`>> response = new `Response`<>();
response.`setStatus`("success");
response.`setData`(`Arrays`.`asList`(user1, user2));

`Type responseType` = new `TypeToken`<`Response`<`List`<`User`>>>(){}.`getType`();
`String json` = gson.`toJson`(response, `responseType`);

// Десериализация
`String json` = "{\"status\":\"success\",\"data\":[{\"name\":\"`John`\"}]}";
`Type responseType` = new `TypeToken`<`Response`<`List`<`User`>>>(){}.`getType`();
`Response`<`List`<`User`>> response = gson.`fromJson`(json, `responseType`);
```

### Wildcard Types
```java
// Работа с wildcard типами
public class `Container`<T> {
    private T value;
}

`Type containerType` = new `TypeToken`<`Container`<? extends `Number`>>(){}.`getType`();
`Container`<? extends `Number`> container = gson.`fromJson`(json, `containerType`);
```

## Streaming API

### JsonWriter для потоковой записи
```java
`StringWriter stringWriter` = new `StringWriter`();
`JsonWriter jsonWriter` = new `JsonWriter`(`stringWriter`);

`jsonWriter`.`beginObject`()
    .name("name").value("`John`")
    .name("age").value(30)
    .name("hobbies").`beginArray`()
        .value("reading")
        .value("gaming")
    .`endArray`()
.`endObject`();

`jsonWriter`.close();
`String json` = `stringWriter`.`toString`();
// {"name":"`John`","age":30,"hobbies":["reading","gaming"]}
```

### JsonReader для потокового чтения
```java
`String json` = "{\"name\":\"`John`\",\"age\":30,\"hobbies\":[\"reading\",\"gaming\"]}";
`JsonReader jsonReader` = new `JsonReader`(new `StringReader`(json));

`jsonReader`.`beginObject`();
while (`jsonReader`.`hasNext`()) {
    `String name` = `jsonReader`.`nextName`();
    switch (name) {
        case "name":
            `String userName` = `jsonReader`.`nextString`();
            break;
        case "age":
            int age = `jsonReader`.`nextInt`();
            break;
        case "hobbies":
            `jsonReader`.`beginArray`();
            while (`jsonReader`.`hasNext`()) {
                `String hobby` = `jsonReader`.`nextString`();
                // Обработка hobby
            }
            `jsonReader`.`endArray`();
            break;
    }
}
`jsonReader`.`endObject`();
`jsonReader`.close();
```

### Custom Streaming Parser
```java
public class `StreamingJsonParser` {
    public void `parseLargeJson`(`InputStream inputStream`) throws IOException {
        `JsonReader` reader = new `JsonReader`(new `InputStreamReader`(`inputStream`));

        reader.`beginObject`();
        while (reader.`hasNext`()) {
            `String name` = reader.`nextName`();
            if ("users".equals(name)) {
                reader.`beginArray`();
                while (reader.`hasNext`()) {
                    `processUser`(reader);
                }
                reader.`endArray`();
            } else {
                reader.`skipValue`();
            }
        }
        reader.`endObject`();
        `reader.close`();
    }

    private void `processUser`(`JsonReader` reader) throws IOException {
        `User user` = new `User`();
        reader.`beginObject`();
        while (reader.`hasNext`()) {
            `String field` = reader.`nextName`();
            switch (field) {
                case "name":
                    user.`setName`(reader.`nextString`());
                    break;
                case "email":
                    user.`setEmail`(reader.`nextString`());
                    break;
                default:
                    reader.`skipValue`();
                    break;
            }
        }
        reader.`endObject`();

        // Сохранить пользователя в базу данных или обработать
        `saveUser`(user);
    }
}
```

## Оптимизация производительности

### Reuse Gson Instances
```java
public class `GsonSingleton` {
    private static final `Gson INSTANCE` = new `GsonBuilder`()
        .`setDateFormat`("`yyyy-`MM`-dd`")
        .create();

    public static `Gson getInstance`() {
        return `INSTANCE`;
    }

    private `GsonSingleton`() {}
}

// Правильное использование
`Gson gson` = `GsonSingleton`.`getInstance`();
`String json` = gson.`toJson`(obj);  // один экземпляр Gson
```

### Field Naming Strategies
```java
// Snake_case в `Java` объекты
`Gson gson` = new `GsonBuilder`()
    .`setFieldNamingPolicy`(`FieldNamingPolicy`.LOWER_CASE_WITH_UNDERSCORES)
    .create();

// `JSON`: {"`user_name`":"john","`user_age`":30}
// `Java`: `userName`, `userAge`

// `Custom naming strategy`
`FieldNamingStrategy customStrategy` = new `FieldNamingStrategy`() {
    `@Override`
    public `String translateName`(`Field f`) {
        return f.`getName`().`toUpperCase`();
    }
};

`Gson gson` = new `GsonBuilder`()
    .`setFieldNamingStrategy`(`customStrategy`)
    .create();
```

### Memory Optimization
```java
// Для больших `JSON` документов
`Gson gson` = new `GsonBuilder`()
    .`disableJdkUnsafe`()  // sun.misc.Unsafe
    .create();

// `Streaming` для больших файлов
public void `processLargeJsonFile`(`Path filePath`) throws IOException {
    try (`JsonReader` reader = new `JsonReader`(`Files`.`newBufferedReader`(`filePath`))) {
        // Обработка без загрузки всего файла в память
        `processJsonStream`(reader);
    }
}
```

## Spring Boot Integration

### Configuration
```java
`@Configuration`
public class `GsonConfig` {

    `@Bean`
    public `Gson gson`() {
        return new `GsonBuilder`()
            .`setDateFormat`("`yyyy-`MM`-dd HH`:mm:ss")
            .`registerTypeAdapter`(`LocalDateTime`.class, new `LocalDateTimeAdapter`())
            .`setPrettyPrinting`()
            .create();
    }

    `@Bean`
    public `GsonHttpMessageConverter gsonHttpMessageConverter`(`Gson gson`) {
        `GsonHttpMessageConverter` converter = new `GsonHttpMessageConverter`();
        converter.`setGson`(gson);
        return converter;
    }
}
```

### RestTemplate Integration
```java
`@Configuration`
public class `RestTemplateConfig` {

    `@Bean`
    public `RestTemplate restTemplate`(`GsonHttpMessageConverter gsonConverter`) {
        `RestTemplate restTemplate` = new `RestTemplate`();
        `restTemplate`.`getMessageConverters`().add(0, `gsonConverter`);
        return `restTemplate`;
    }
}
```

### Spring Data Integration
```java
`@Component`
public class `GsonSerializer` implements `Serializer`<`User`>, `Deserializer`<`User`> {

    private final `Gson gson` = new `Gson`();

    `@Override`
    public byte[] serialize(`User user`) throws `SerializationException` {
        return gson.`toJson`(user).`getBytes`(`StandardCharsets`.UTF_8);
    }

    `@Override`
    public `User deserialize`(byte[] bytes) throws `SerializationException` {
        `String json` = new `String`(bytes, `StandardCharsets`.UTF_8);
        return gson.`fromJson`(json, `User`.class);
    }
}
```

## Testing

### Unit Testing
```java
public class `GsonTest` {

    private `Gson gson`;

    `@BeforeEach`
    void `setUp`() {
        gson = new `GsonBuilder`()
            .`registerTypeAdapter`(`LocalDate`.class, new `LocalDateAdapter`())
            .create();
    }

    `@Test`
    void `testSerialization`() {
        `User user` = new `User`("`John`", "Doe", 30);
        `String expected` = "{\"name\":\"`John`\",\"`lastName`\":\"Doe\",\"age\":30}";

        `String actual` = gson.`toJson`(user);

        `assertEquals`(expected, actual);
    }

    `@Test`
    void `testDeserialization`() {
        `String json` = "{\"name\":\"`Jane`\",\"`lastName`\":\"`Smith`\",\"age\":25}";
        `User user` = gson.`fromJson`(json, `User`.class);

        `assertEquals`("`Jane`", user.`getName`());
        `assertEquals`("`Smith`", user.`getLastName`());
        `assertEquals`(25, user.`getAge`());
    }

    `@Test`
    void `testGenericTypes`() {
        `List`<`User`> users = `Arrays`.`asList`(
            new `User`("`John`", "Doe", 30),
            new `User`("`Jane`", "`Smith`", 25)
        );

        `Type listType` = new `TypeToken`<`List`<`User`>>(){}.`getType`();
        `String json` = gson.`toJson`(users, `listType`);

        `List`<`User`> `deserializedUsers` = gson.`fromJson`(json, `listType`);

        `assertEquals`(2, `deserializedUsers`.size());
        `assertEquals`("`John`", `deserializedUsers`.get(0).`getName`());
    }
}
```

### JSON Schema Validation
```java
public class `JsonValidator` {

    private final `Gson gson` = new `Gson`();

    public boolean `isValidUserJson`(`String json`) {
        try {
            `User user` = gson.`fromJson`(json, `User`.class);
            return user.`getName`() != `null` && user.`getAge`() > 0;
        } catch (`JsonSyntaxException` e) {
            return `false`;
        }
    }

    public `List`<`String`> `validateJson`(`String json`, `Class`<?> clazz) {
        `List`<`String`> errors = new `ArrayList`<>();

        try {
            `Object object` = gson.`fromJson`(json, clazz);
            // Дополнительная валидация
            if (object instanceof `User`) {
                `User user` = (`User`) object;
                if (user.`getName`() == `null` || user.`getName`().trim().`isEmpty`()) {
                    `errors.add`("`Name is required`");
                }
                if (user.`getAge`() < 0 || user.`getAge`() > `150`) {
                    `errors.add`("`Age must be between 0 and 150`");
                }
            }
        } catch (`JsonSyntaxException` e) {
            `errors.add`("`Invalid `JSON` syntax`: " + e.`getMessage`());
        }

        return errors;
    }
}
```

## Error Handling

### JsonParseException Handling
```java
public class `SafeJsonParser` {

    private final `Gson gson` = new `Gson`();

    public <T> `Optional`<T> `parseSafely`(`String json`, `Class`<T> clazz) {
        try {
            T result = gson.`fromJson`(json, clazz);
            return `Optional`.`ofNullable`(result);
        } catch (`JsonParseException` e) {
            `System`.`err.println`("`Failed to parse JSON`: " + e.`getMessage`());
            return `Optional`.empty();
        }
    }

    public <T> T `parseWithDefault`(`String json`, `Class`<T> clazz, T `defaultValue`) {
        try {
            T result = gson.`fromJson`(json, clazz);
            return result != `null` ? result : `defaultValue`;
        } catch (`JsonParseException` e) {
            `System`.`err.println`("`Using default value due` to parse error: " + e.`getMessage`());
            return `defaultValue`;
        }
    }
}
```

### Custom Error Strategies
```java
public class `LenientGsonParser` {

    public `Gson createLenientGson`() {
        return new `GsonBuilder`()
            .`setLenient`(`true`)  // JSON
            .create();
    }

    public `List`<`JsonElement`> `parseMultipleJsonObjects`(`String jsonString`) {
        `List`<`JsonElement`> elements = new `ArrayList`<>();
        `JsonReader` reader = new `JsonReader`(new `StringReader`(`jsonString`));
        reader.`setLenient`(`true`);  // Разрешить multiple objects

        try {
            while (reader.`hasNext`()) {
                `elements.add`(`JsonParser`.`parseReader`(reader));
            }
        } catch (`Exception e`) {
            // `Handle parse errors`
        }

        return elements;
    }
}
```

## Advanced Features

### Versioning Support
```java
``@Retention`(`RetentionPolicy`.`RUNTIME`)`
``@Target`(`ElementType`.`FIELD`)`
public `@interface Since` {
    double value();
}

``@Retention`(`RetentionPolicy`.`RUNTIME`)`
``@Target`(`ElementType`.`FIELD`)`
public `@interface Until` {
    double value();
}

public class `VersionedUser` {
    private `String name`;

    ``@Since`(`1.0`)`
    private `String email`;

    ``@Since`(`1.1`)`
    private int age;

    ``@Until`(`1.5`)`
    private `String deprecatedField`;
}

// Использование
`Gson gson` = new `GsonBuilder`()
    .`setVersion`(1.2)   // 1.2
    .create();
```

### Instance Creators
```java
public class `CustomInstanceCreator` implements `InstanceCreator`<`MyClass`> {
    `@Override`
    public `MyClass createInstance`(`Type type`) {
        return new `MyClass`("default", 0);  // Кастомная инициализация
    }
}

`Gson gson` = new `GsonBuilder`()
    .`registerTypeAdapter`(`MyClass`.class, new `CustomInstanceCreator`())
    .create();
```

### Circular Reference Handling
```java
public class `Parent` {
    private `String name`;
    private `Child child`;
}

public class `Child` {
    private `String name`;
    private `Parent parent`;  // Циклическая ссылка
}

// Решение 1: `@Expose`
public class `Parent` {
    private `String name`;

    `@Expose`
    private `Child child`;
}

`Gson gson` = new `GsonBuilder`()
    .`excludeFieldsWithoutExposeAnnotation`()
    .create();

// Решение 2: `Custom adapter`
public class `CircularReferenceAdapter` implements `JsonSerializer`<`Parent`> {
    `@Override`
    public `JsonElement` serialize(`Parent src`, `Type typeOfSrc`, `JsonSerializationContext` context) {
        `JsonObject` json = new `JsonObject`();
        json.`addProperty`("name", src.`getName`());
        // Не сериализуем child для избежания цикла
        return json;
    }
}
```

## Migration Guide

### From Jackson to Gson
```java
// `Jackson`
`ObjectMapper` mapper = new `ObjectMapper`();
`String json` = mapper.`writeValueAsString`(object);
`MyClass` obj = mapper.`readValue`(json, `MyClass`.class);

// `Gson`
`Gson gson` = new `Gson`();
`String json` = gson.`toJson`(object);
`MyClass` obj = gson.`fromJson`(json, `MyClass`.class);
```

### From org.json to Gson
```java
// `org.json`
JSONObject `jsonObject` = new JSONObject("{\"name\":\"`John`\"}");
`String name` = `jsonObject`.`getString`("name");

// `Gson`
`JsonObject jsonObject` = `JsonParser`.`parseString`("{\"name\":\"`John`\"}").`getAsJsonObject`();
`String name` = `jsonObject`.get("name").`getAsString`();
```

## Лучшие практики

### Thread Safety
```java
public class `ThreadSafeGsonExample` {

    // `Gson instances are thread`-safe
    private static final `Gson GSON` = new `Gson`();

    public `String serialize`(`User user`) {
        // Безопасно использовать в многопоточной среде
        return `GSON`.`toJson`(user);
    }

    public `User deserialize`(`String json`) {
        return `GSON`.`fromJson`(json, `User`.class);
    }
}
```

### Null Handling
```java
public class `NullHandlingExample` {

    // Сериализовать `null` значения
    `Gson gsonWithNulls` = new `GsonBuilder`()
        .`serializeNulls`()
        .create();

    // Игнорировать `null` значения
    `Gson gsonWithoutNulls` = new `GsonBuilder`()
        .`serializeNulls`()  // или использовать default
        .create();

    // Кастомная обработка `null`
    public class `NullStringAdapter` implements `JsonSerializer`<`String`> {
        `@Override`
        public `JsonElement` serialize(`String src`, `Type typeOfSrc`, `JsonSerializationContext` context) {
            return src == `null` ? new `JsonPrimitive`("") : new `JsonPrimitive`(src);
        }
    }
}
```

### Performance Tips
```java
public class `GsonPerformanceTips` {

    // 1. `Reuse `Gson` instances`
    private static final `Gson GSON` = new `Gson`();

    // 2. `Use streaming for large JSON`
    public void `processLargeJson`(`InputStream` input) throws IOException {
        try (`JsonReader` reader = new `JsonReader`(new `InputStreamReader`(input))) {
            reader.`beginObject`();
            while (reader.`hasNext`()) {
                // `Process fields individually`
            }
            reader.`endObject`();
        }
    }

    // 3. `Disable unnecessary features`
    public `Gson createOptimizedGson`() {
        return new `GsonBuilder`()
            .`disableHtmlEscaping`()  // HTML не нужен
            .`disableJdkUnsafe`()     // В некоторых средах
            .create();
    }
}
```

## Experimental Features

### Records Support (Java 14+)
```java
public record `Person`(`String name`, int age) {}

// `Gson` автоматически работает с records
`Gson gson` = new `Gson`();
`Person person` = new `Person`("`John`", 30);
`String json` = gson.`toJson`(person);  // {"name":"John","age":30}
`Person deserialized` = gson.`fromJson`(json, `Person`.class);
```

### Sealed Classes Support (Java 17+)
```java
public sealed class `Shape permits Circle`, `Rectangle` {}

public final class `Circle extends Shape` {
    private final double radius;
}

public final class `Rectangle extends Shape` {
    private final double width, height;
}

// Кастомный адаптер для sealed classes
public class `ShapeAdapter` implements `JsonSerializer`<`Shape`>, `JsonDeserializer`<`Shape`> {
    `@Override`
    public `JsonElement` serialize(`Shape src`, `Type typeOfSrc`, `JsonSerializationContext` context) {
        `JsonObject` json = `context.serialize`(src, src.`getClass`()).`getAsJsonObject`();
        json.`addProperty`("type", src.`getClass`().`getSimpleName`());
        return json;
    }

    `@Override`
    public `Shape deserialize`(`JsonElement` json, `Type typeOfT`, `JsonDeserializationContext` context) {
        `JsonObject jsonObject` = json.`getAsJsonObject`();
        `String type` = `jsonObject`.get("type").`getAsString`();

        return switch (type) {
            case "`Circle`" -> `context.deserialize`(json, `Circle`.class);
            case "`Rectangle`" -> `context.deserialize`(json, `Rectangle`.class);
            default -> throw new `JsonParseException`("`Unknown shape type`: " + type);
        };
    }
}
```

## Решение проблем

### Common Issues
```java
public class `GsonTroubleshooting` {

    // Проблема: `NullPointerException` при десериализации
    public `User safeDeserialize`(`String json`) {
        try {
            return gson.`fromJson`(json, `User`.class);
        } catch (`Exception e`) {
            // Логировать ошибку
            return new `User`(); // Возвращать default объект
        }
    }

    // Проблема: `Malformed JSON`
    public boolean `isValidJson`(`String json`) {
        try {
            gson.`fromJson`(json, `Object`.class);
            return `true`;
        } catch (`JsonSyntaxException` e) {
            return `false`;
        }
    }

    // Проблема: `Date `serialization` issues`
    public `Gson createDateFriendlyGson`() {
        return new `GsonBuilder`()
            .`setDateFormat`("`yyyy-`MM`-dd`'T'`HH`:mm:ss.`SSSZ`")
            .`registerTypeAdapter`(`Date`.class, new `DateAdapter`())
            .create();
    }
}
```

### Debugging
```java
public class `GsonDebugger` {

    // Логирование сериализации/десериализации
    public class `LoggingTypeAdapter`<T> implements `JsonSerializer`<T>, `JsonDeserializer`<T> {

        private final `Gson delegate` = new `Gson`();

        `@Override`
        public `JsonElement` serialize(T src, `Type typeOfSrc`, `JsonSerializationContext` context) {
            `System`.`out.println`("`Serializing`: " + src);
            `JsonElement` result = delegate.`toJsonTree`(src, `typeOfSrc`);
            `System`.`out.println`("`Result`: " + result);
            return result;
        }

        `@Override`
        public T deserialize(`JsonElement` json, `Type typeOfT`, `JsonDeserializationContext` context) {
            `System`.`out.println`("`Deserializing`: " + json);
            T result = delegate.`fromJson`(json, `typeOfT`);
            `System`.`out.println`("`Result`: " + result);
            return result;
        }
    }
}
```

