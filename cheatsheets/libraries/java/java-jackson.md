---
title: "Jackson: JSON-сериализация в Java"
description: "Шпаргалка по Jackson: ObjectMapper, аннотации, кастомные сериализаторы, полиморфизм, даты, views, Streaming API и интеграция со Spring Boot."
tags:
  - libraries
  - java
  - jackson
  - json
  - serialization
difficulty: "intermediate"
updated: "2026-04-20"
---
# Jackson: JSON-сериализация в Java

Jackson — де-факто стандартная библиотека сериализации/десериализации JSON в Java. Используется по умолчанию в Spring Boot (`spring-boot-starter-web`), поддерживает массу аннотаций для тонкого контроля вывода и умеет работать с полиморфизмом, views, фильтрами и бинарными форматами (CBOR, Smile, MessagePack).

Состоит из трёх модулей: `jackson-core` (низкоуровневое Streaming API), `jackson-databind` (ObjectMapper и POJO-маппинг), `jackson-annotations` (аннотации). На практике используются все три — вытягиваются транзитивно.

## Полезные ссылки

### Официальная документация
- [Jackson Docs](https://github.com/FasterXML/jackson-docs) — корневой репо документации
- [Annotations Reference](https://github.com/FasterXML/jackson-annotations/wiki) — все аннотации
- [Baeldung: Intro to Jackson ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial) — ObjectMapper
- [Baeldung: Jackson Annotations](https://www.baeldung.com/jackson-annotations) — аннотации с примерами
- [Jackson Release Notes](https://github.com/FasterXML/jackson/wiki/Jackson-Releases) — что в какой версии

### См. также
- [[spring-boot|spring-boot]] — автоконфигурация Jackson
- [[spring-rest|spring-rest]] — JSON в REST-контроллерах
- [[java-bean-validation|java-bean-validation]] — валидация десериализованных DTO
- [[java-mapstruct|java-mapstruct]] — маппинг после десериализации
- [[java-lombok|java-lombok]] — DTO с Lombok
- [[spring-mvc|spring-mvc]] — обработка HTTP-запросов

## Содержание

- [ObjectMapper: базовые операции](#objectmapper-базовые-операции)
  - [JsonNode — работа без POJO](#jsonnode-работа-без-pojo)
  - [Pretty-print](#pretty-print)
- [Аннотации маппинга](#аннотации-маппинга)
  - [Имена и игнор](#имена-и-игнор)
  - [Включение/исключение значений](#включениеисключение-значений)
  - [Форматирование](#форматирование)
  - [Конструкторы](#конструкторы)
  - [@JsonValue и @JsonEnumDefaultValue](#jsonvalue-и-jsonenumdefaultvalue)
- [Даты и временные типы](#даты-и-временные-типы)
  - [Глобальный формат](#глобальный-формат)
  - [Per-field формат](#per-field-формат)
  - [Часовые пояса](#часовые-пояса)
- [Полиморфизм](#полиморфизм)
  - [Стратегии include](#стратегии-include)
  - [Стратегии use](#стратегии-use)
- [Custom serializer / deserializer](#custom-serializer-deserializer)
  - [StdSerializer](#stdserializer)
  - [StdDeserializer](#stddeserializer)
  - [Регистрация](#регистрация)
- [JsonView](#jsonview)
- [JsonFilter](#jsonfilter)
- [Generic types](#generic-types)
- [Streaming API](#streaming-api)
  - [Чтение](#чтение)
  - [Запись](#запись)
- [Обработка ошибок](#обработка-ошибок)
  - [Тихое игнорирование лишних полей](#тихое-игнорирование-лишних-полей)
- [Интеграция со Spring Boot](#интеграция-со-spring-boot)
  - [Кастомизация через Jackson2ObjectMapperBuilderCustomizer](#кастомизация-через-jackson2objectmapperbuildercustomizer)
  - [PropertyNamingStrategy](#propertynamingstrategy)
- [Производительность](#производительность)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [См. также](#см-также-1)

## ObjectMapper: базовые операции

```java
ObjectMapper mapper = new ObjectMapper();

// Сериализация
String json = mapper.writeValueAsString(user);
mapper.writeValue(new File("user.json"), user);
byte[] bytes = mapper.writeValueAsBytes(user);

// Десериализация
User user = mapper.readValue(json, User.class);
User fromFile = mapper.readValue(new File("user.json"), User.class);
User fromStream = mapper.readValue(inputStream, User.class);
```

### JsonNode — работа без POJO

```java
JsonNode root = mapper.readTree(json);
String name = root.path("user").path("name").asText();
int age = root.path("user").path("age").asInt(0);  // с дефолтом
boolean active = root.path("active").asBoolean(false);

// Модификация
ObjectNode obj = (ObjectNode) root;
obj.put("updatedAt", Instant.now().toString());
obj.remove("password");
```

### Pretty-print

```java
String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

// Глобально
mapper.enable(SerializationFeature.INDENT_OUTPUT);
```

## Аннотации маппинга

### Имена и игнор

```java
public class UserDto {
    @JsonProperty("user_id")       // в JSON будет user_id, в Java — userId
    private Long userId;

    @JsonAlias({"firstName", "first_name", "fname"})  // принимаем любое имя при чтении
    private String name;

    @JsonIgnore                     // никогда не сериализуется
    private String password;

    @JsonIgnoreProperties({"createdBy", "version"})   // на классе — исключаем список полей
    public class Order { ... }
}
```

`@JsonProperty` — двусторонний (чтение + запись). `@JsonAlias` — только чтение. Снаружи класса можно использовать `@JsonIgnoreProperties(ignoreUnknown = true)` на классе — неизвестные JSON-поля не падают ошибкой.

### Включение/исключение значений

```java
@JsonInclude(JsonInclude.Include.NON_NULL)         // не сериализуем null
@JsonInclude(JsonInclude.Include.NON_EMPTY)        // пропускаем null, "", [], {}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)      // пропускаем дефолтные (0, false, null)
@JsonInclude(JsonInclude.Include.ALWAYS)           // default — всё
public class UserDto {
    private String name;
    private List<String> roles;
}
```

Глобально:

```java
mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
```

### Форматирование

```java
public class Order {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;  // будет "123.45" вместо 123.45 (для точности)

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private Duration duration;  // { "seconds": 60, "nanos": 0 }
}
```

### Конструкторы

Когда DTO immutable — нужно подсказать Jackson, как его создать:

```java
public class UserDto {
    private final Long id;
    private final String name;

    @JsonCreator
    public UserDto(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name
    ) {
        this.id = id;
        this.name = name;
    }
}
```

С Jackson 2.12+ и параметрами, скомпилированными с `-parameters`, `@JsonProperty` не нужен — имена параметров считываются автоматически.

### `@JsonValue` и `@JsonEnumDefaultValue`

```java
public enum OrderStatus {
    NEW("new"), IN_PROGRESS("in_progress"), DONE("done");

    private final String code;
    OrderStatus(String code) { this.code = code; }

    @JsonValue                   // сериализуется как "new", а не как "NEW"
    public String getCode() { return code; }

    @JsonCreator
    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
            .filter(s -> s.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
```

`@JsonEnumDefaultValue` на enum-константе — fallback для неизвестных значений (нужен `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE`).

## Даты и временные типы

Для `java.time.*` нужен модуль `jackson-datatype-jsr310` (входит в Spring Boot).

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());

// По умолчанию Instant сериализуется как массив [1717420800, 0] — timestamp + nanos
// Переключаем на ISO-8601:
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
// Получим: "2025-06-03T14:30:00Z"
```

### Глобальный формат

```java
mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
```

### Per-field формат

```java
public class Event {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime startsAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
```

### Часовые пояса

```java
mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
```

## Полиморфизм

Когда нужно сериализовать иерархию классов, сохранив тип:

```java
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPayment.class, name = "card"),
    @JsonSubTypes.Type(value = PaypalPayment.class, name = "paypal"),
    @JsonSubTypes.Type(value = CryptoPayment.class, name = "crypto")
})
public abstract class Payment { ... }

public class CardPayment extends Payment {
    private String cardNumber;
}
```

JSON:
```json
{ "type": "card", "cardNumber": "4111...." }
```

### Стратегии `include`

| Значение | JSON |
|----------|------|
| `PROPERTY` (default) | `{ "type": "card", "cardNumber": "..." }` |
| `WRAPPER_OBJECT` | `{ "card": { "cardNumber": "..." } }` |
| `WRAPPER_ARRAY` | `["card", { "cardNumber": "..." }]` |
| `EXISTING_PROPERTY` | `type` — обычное поле, не инжектится |

### Стратегии `use`

| Значение | Ключ в JSON |
|----------|-------------|
| `NAME` | логическое имя из `@JsonSubTypes` |
| `CLASS` | FQN класса (`com.example.CardPayment`) — небезопасно |
| `MINIMAL_CLASS` | короткое имя относительно базового пакета |
| `CUSTOM` | свой `TypeIdResolver` |

**Безопасность:** `CLASS` небезопасен — злоумышленник может указать любой класс с public конструктором. Используй `NAME` и явный `@JsonSubTypes`.

## Custom serializer / deserializer

### StdSerializer

```java
public class MoneySerializer extends StdSerializer<Money> {
    public MoneySerializer() { super(Money.class); }

    @Override
    public void serialize(Money money, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("amount", money.amount().toPlainString());
        gen.writeStringField("currency", money.currency().getCurrencyCode());
        gen.writeEndObject();
    }
}
```

### StdDeserializer

```java
public class MoneyDeserializer extends StdDeserializer<Money> {
    public MoneyDeserializer() { super(Money.class); }

    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        BigDecimal amount = new BigDecimal(node.get("amount").asText());
        Currency currency = Currency.getInstance(node.get("currency").asText());
        return new Money(amount, currency);
    }
}
```

### Регистрация

Через аннотацию:

```java
@JsonSerialize(using = MoneySerializer.class)
@JsonDeserialize(using = MoneyDeserializer.class)
public class Money { ... }
```

Через модуль:

```java
SimpleModule module = new SimpleModule();
module.addSerializer(Money.class, new MoneySerializer());
module.addDeserializer(Money.class, new MoneyDeserializer());
mapper.registerModule(module);
```

## JsonView

Разные «срезы» одного DTO для разных endpoint-ов:

```java
public class Views {
    public static class Public {}
    public static class Internal extends Public {}
    public static class Admin extends Internal {}
}

public class UserDto {
    @JsonView(Views.Public.class)
    private Long id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Internal.class)
    private String email;

    @JsonView(Views.Admin.class)
    private String salaryBand;
}
```

В Spring MVC:

```java
@GetMapping("/users/{id}")
@JsonView(Views.Public.class)       // вернёт только id + name
public UserDto getPublic(@PathVariable Long id) { ... }

@GetMapping("/admin/users/{id}")
@JsonView(Views.Admin.class)        // все поля
public UserDto getForAdmin(@PathVariable Long id) { ... }
```

Для ObjectMapper:

```java
String json = mapper.writerWithView(Views.Public.class).writeValueAsString(user);
```

## JsonFilter

Динамический выбор полей в runtime (в отличие от `@JsonView`, который фиксирует набор на этапе компиляции):

```java
@JsonFilter("userFilter")
public class User { ... }

FilterProvider filters = new SimpleFilterProvider()
    .addFilter("userFilter", SimpleBeanPropertyFilter.serializeAllExcept("password"));

String json = mapper.writer(filters).writeValueAsString(user);
```

Или whitelist:

```java
SimpleBeanPropertyFilter.filterOutAllExcept("id", "name")
```

## Generic types

Для `List<User>` или `Map<String, User>` обычный `readValue(json, List.class)` вернёт `List<LinkedHashMap>`. Нужно `TypeReference`:

```java
List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});
Map<String, User> byId = mapper.readValue(json, new TypeReference<Map<String, User>>() {});

// Альтернатива — JavaType
JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, User.class);
List<User> users2 = mapper.readValue(json, type);
```

## Streaming API

Для больших JSON (сотни мегабайт) — `JsonParser` / `JsonGenerator`, не держат всё в памяти.

### Чтение

```java
JsonFactory factory = new JsonFactory();
try (JsonParser parser = factory.createParser(new File("big.json"))) {
    while (parser.nextToken() != JsonToken.END_ARRAY) {
        if (parser.getCurrentName() != null && parser.getCurrentName().equals("items")) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                Item item = mapper.readValue(parser, Item.class);
                process(item);
            }
        }
    }
}
```

### Запись

```java
try (JsonGenerator gen = factory.createGenerator(new File("out.json"), JsonEncoding.UTF8)) {
    gen.writeStartArray();
    for (User user : hugeDataset) {
        mapper.writeValue(gen, user);
    }
    gen.writeEndArray();
}
```

## Обработка ошибок

| Исключение | Когда |
|------------|-------|
| `JsonParseException` | битый JSON (синтаксис) |
| `JsonMappingException` | структура не подходит под POJO |
| `MismatchedInputException` (подкласс) | тип не совпал (число vs строка) |
| `UnrecognizedPropertyException` | unknown поле при `FAIL_ON_UNKNOWN_PROPERTIES=true` |
| `InvalidFormatException` | поле не парсится в нужный формат |

```java
try {
    User user = mapper.readValue(json, User.class);
} catch (UnrecognizedPropertyException e) {
    log.warn("Unknown field: {}", e.getPropertyName());
} catch (JsonMappingException e) {
    log.error("Path: {}", e.getPath());
}
```

### Тихое игнорирование лишних полей

```java
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
// или
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto { ... }
```

## Интеграция со Spring Boot

Spring Boot автоконфигурирует `ObjectMapper` через `Jackson2ObjectMapperBuilder`. Настройки через `application.yml`:

```yaml
spring:
  jackson:
    date-format: "yyyy-MM-dd'T'HH:mm:ssXXX"
    time-zone: UTC
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false
      indent-output: false
    deserialization:
      fail-on-unknown-properties: false
      accept-single-value-as-array: true
    property-naming-strategy: SNAKE_CASE
```

### Кастомизация через `Jackson2ObjectMapperBuilderCustomizer`

```java
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
            .modules(new JavaTimeModule(), new Jdk8Module())
            .failOnUnknownProperties(false)
            .simpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .serializerByType(Money.class, new MoneySerializer());
    }
}
```

### PropertyNamingStrategy

```java
// camelCase → snake_case
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

// camelCase → kebab-case
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
```

В Spring Boot — через `spring.jackson.property-naming-strategy`.

## Производительность

- **Один `ObjectMapper` на приложение.** Потокобезопасен, но создание дорогое. В Spring Boot — singleton bean.
- **`ObjectReader` / `ObjectWriter` — immutable и быстрее.** Для hot path переиспользуй: `mapper.readerFor(User.class).readValue(json)`.
- **Afterburner / Blackbird модули** — bytecode-генерация вместо reflection, 30-40% ускорение:

```java
mapper.registerModule(new BlackbirdModule());  // Java 11+, замена Afterburner
```

- **Streaming API для огромных документов.** Парсинг 1GB JSON в `JsonNode` съест все memory.
- **Бинарные форматы.** CBOR / Smile / Ion быстрее и компактнее JSON — те же Jackson-аннотации, другой `JsonFactory`.
- **Кешируй `TypeReference`.** Анонимные классы не бесплатны.

## Лучшие практики

- **DTO отдельно от entity.** JSON-сериализация entity из JPA — прямой путь к lazy-init exception и утечке полей.
- **`@JsonIgnoreProperties(ignoreUnknown = true)` на DTO API.** Защита от падений при добавлении поля в схему на клиенте.
- **Immutable DTO с `@JsonCreator`.** Плюс `-parameters` в компиляторе — и забываем про boilerplate.
- **`WRITE_DATES_AS_TIMESTAMPS = false`.** JSON с timestamp-числами неудобен в логах и для клиентов.
- **Не выключай `FAIL_ON_UNKNOWN_PROPERTIES` глобально.** Только для клиентских DTO. В server-side DTO лишние поля — баг.
- **Используй `@JsonView` для API-уровней.** Меньше дублей DTO.
- **Для enum — `@JsonValue` + `@JsonCreator`.** Явный контроль сериализации, терпимость к регистру.
- **BigDecimal как строка.** `@JsonFormat(shape = STRING)` — защита от потери точности при обработке JSON в JavaScript.
- **Включи `DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL`** — новые enum-значения на сервере не ломают старых клиентов.

## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| `No serializer found for class java.time.LocalDate` | Не зарегистрирован `JavaTimeModule` | `mapper.registerModule(new JavaTimeModule())` или `jackson-datatype-jsr310` |
| `Cannot construct instance of ...: no Creators` | Нет default-конструктора или `@JsonCreator` | Добавить no-arg конструктор или `@JsonCreator` |
| Даты приходят как числа | Включён `WRITE_DATES_AS_TIMESTAMPS` | Отключить через feature или `spring.jackson.serialization.write-dates-as-timestamps: false` |
| Неизвестное поле валит сериализацию | `FAIL_ON_UNKNOWN_PROPERTIES = true` | Поставить `@JsonIgnoreProperties(ignoreUnknown = true)` или выключить feature |
| Циклические ссылки `StackOverflow` | Bidirectional JPA-связи | `@JsonManagedReference` / `@JsonBackReference` или DTO |
| Password попадает в response | Нет `@JsonIgnore` | `@JsonIgnore` на поле или `@JsonProperty(access = WRITE_ONLY)` |
| `LocalDateTime` приходит, но формат другой | Локальный `@JsonFormat` override | Проверить per-field `pattern`, `timezone` |
| `JsonMappingException: (was java.lang.NullPointerException)` внутри getter | NPE в геттере | Защитить геттер или использовать `@JsonIgnore` |
| Разные форматы для чтения / записи | Нужна ассиметрия | `@JsonFormat` только на сериализации + `@JsonDeserialize(using = ...)` |
| Kotlin data class не работает | Нет `jackson-module-kotlin` | Зарегистрировать `KotlinModule` |

## См. также

- [[spring-boot|spring-boot]] — автоконфигурация ObjectMapper
- [[spring-rest|spring-rest]] — JSON в REST-контроллерах
- [[spring-mvc|spring-mvc]] — MessageConverter для Jackson
- [[java-bean-validation|java-bean-validation]] — валидация после десериализации
- [[java-mapstruct|java-mapstruct]] — маппинг entity DTO
- [[java-lombok|java-lombok]] — DTO с Lombok
- [[java-protobuf|java-protobuf]] — бинарная сериализация как альтернатива
- [[java-basics|java-basics]] — базовые концепции Java
