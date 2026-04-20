---
title: "Вопросы на собеседовании: Jackson (JSON)"
description: "Jackson на собеседовании: ObjectMapper, аннотации, custom сериализаторы/десериализаторы, полиморфизм, дженерики, даты, streaming API, интеграция со Spring, производительность."
tags:
  - interview
  - programming-languages
  - java-jackson-interview
aliases:
  - "Jackson"
  - "Jackson interview"
  - "Jackson JSON"
  - "Jackson ObjectMapper"
  - "Jackson собеседование"
  - "Java JSON interview"
difficulty: "intermediate"
updated: "2026-04-20"
---
# Вопросы на собеседовании: `Jackson`

`Jackson` — де-факто стандартная библиотека JSON в экосистеме Java. На backend-собеседованиях спрашивают о `ObjectMapper`, ключевых аннотациях, кастомных сериализаторах, полиморфизме и нюансах интеграции со `Spring Boot`.

## Полезные ссылки

### Официальная документация
- [Jackson GitHub](https://github.com/FasterXML/jackson)
- [Jackson Databind Javadoc](https://fasterxml.github.io/jackson-databind/javadoc/2.15/)
- [Jackson Annotations Wiki](https://github.com/FasterXML/jackson-annotations/wiki)

### Baeldung
- [Jackson Annotation Examples](https://www.baeldung.com/jackson-annotations)
- [Intro to the Jackson ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)
- [Jackson — Custom Serializer](https://www.baeldung.com/jackson-custom-serialization)
- [Getting Started with Deserialization in Jackson](https://www.baeldung.com/jackson-deserialization)
- [Spring Boot: Customize the Jackson ObjectMapper](https://www.baeldung.com/spring-boot-customize-jackson-objectmapper)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Jackson и из каких модулей он состоит?](#q1--что-такое-jackson-и-из-каких-модулей-он-состоит)
- [Q2. (!) Что такое `ObjectMapper` и как его использовать?](#q2--что-такое-objectmapper-и-как-его-использовать)
- [Q3. Нужно ли создавать новый `ObjectMapper` на каждый запрос?](#q3-нужно-ли-создавать-новый-objectmapper-на-каждый-запрос)
- [Q4. (!) Какие у Jackson три API для работы с JSON?](#q4--какие-у-jackson-три-api-для-работы-с-json)
- [Q5. Как прочитать JSON в `Map` или `List`?](#q5-как-прочитать-json-в-map-или-list)

**Конфигурация ObjectMapper**
- [Q6. (!) Как настроить Jackson, чтобы он не падал на unknown properties?](#q6--как-настроить-jackson-чтобы-он-не-падал-на-unknown-properties)
- [Q7. Какие флаги `SerializationFeature` / `DeserializationFeature` важны?](#q7-какие-флаги-serializationfeature--deserializationfeature-важны)
- [Q8. Как включить pretty-print?](#q8-как-включить-pretty-print)
- [Q9. Как настроить стратегию именования полей (snake_case)?](#q9-как-настроить-стратегию-именования-полей-snake_case)

**Ключевые аннотации**
- [Q10. (!) Что делают `@JsonProperty`, `@JsonIgnore` и `@JsonIgnoreProperties`?](#q10--что-делают-jsonproperty-jsonignore-и-jsonignoreproperties)
- [Q11. (!) Зачем нужны `@JsonCreator` и `@JsonValue`?](#q11--зачем-нужны-jsoncreator-и-jsonvalue)
- [Q12. Что делает `@JsonInclude`?](#q12-что-делает-jsoninclude)
- [Q13. Что такое `@JsonFormat` и для чего нужен?](#q13-что-такое-jsonformat-и-для-чего-нужен)
- [Q14. В чём разница между `@JsonSetter` и `@JsonGetter`?](#q14-в-чём-разница-между-jsonsetter-и-jsongetter)
- [Q15. Что делает `@JsonAlias`?](#q15-что-делает-jsonalias)

**Дженерики и TypeReference**
- [Q16. (!) Как десериализовать `List<Foo>` или сложный generic-тип?](#q16--как-десериализовать-listfoo-или-сложный-generic-тип)
- [Q17. Почему `readValue(json, List.class)` возвращает `List<LinkedHashMap>`?](#q17-почему-readvaluejson-listclass-возвращает-listlinkedhashmap)

**Кастомные сериализаторы**
- [Q18. (!) Как написать кастомный сериализатор?](#q18--как-написать-кастомный-сериализатор)
- [Q19. (!) Как написать кастомный десериализатор?](#q19--как-написать-кастомный-десериализатор)
- [Q20. Как вызвать default-сериализатор из кастомного?](#q20-как-вызвать-default-сериализатор-из-кастомного)
- [Q21. Что такое `Module` / `SimpleModule` в Jackson?](#q21-что-такое-module--simplemodule-в-jackson)

**Полиморфизм**
- [Q22. (!) Как сериализовать полиморфную иерархию классов?](#q22--как-сериализовать-полиморфную-иерархию-классов)
- [Q23. Что такое Default Typing и почему он опасен?](#q23-что-такое-default-typing-и-почему-он-опасен)

**Даты и специальные типы**
- [Q24. (!) Почему `LocalDateTime` сериализуется как массив и как это исправить?](#q24--почему-localdatetime-сериализуется-как-массив-и-как-это-исправить)
- [Q25. Как работать с `BigDecimal` в JSON?](#q25-как-работать-с-bigdecimal-в-json)

**Streaming и Tree API**
- [Q26. Когда использовать Streaming API (`JsonParser`/`JsonGenerator`)?](#q26-когда-использовать-streaming-api-jsonparserjsongenerator)
- [Q27. Что такое Tree Model (`JsonNode`)?](#q27-что-такое-tree-model-jsonnode)

**Spring и JSON Views**
- [Q28. (!) Как настроить `ObjectMapper` в Spring Boot?](#q28--как-настроить-objectmapper-в-spring-boot)
- [Q29. Что такое `@JsonView` и когда его использовать?](#q29-что-такое-jsonview-и-когда-его-использовать)

**Производительность и ошибки**
- [Q30. (!) Частые ошибки и как их диагностировать?](#q30--частые-ошибки-и-как-их-диагностировать)
- [Q31. Насколько Jackson быстр? Когда использовать альтернативы?](#q31-насколько-jackson-быстр-когда-использовать-альтернативы)

## Q1. (!) Что такое Jackson и из каких модулей он состоит?

`Jackson` — набор Java-библиотек для работы с JSON (а также XML, YAML, CBOR, Smile). Де-факто стандарт в Spring, Dropwizard, RESTEasy.

Основные модули:

| Модуль | Что делает |
|---|---|
| `jackson-core` | потоковое чтение/запись (`JsonParser`, `JsonGenerator`) |
| `jackson-annotations` | аннотации (`@JsonProperty`, `@JsonIgnore`, ...) |
| `jackson-databind` | `ObjectMapper`, POJO ↔ JSON |
| `jackson-datatype-jsr310` | поддержка `java.time` (`LocalDateTime`, `Instant`) |
| `jackson-datatype-jdk8` | `Optional`, `Stream` |
| `jackson-module-parameter-names` | имена параметров конструктора без `@JsonProperty` |
| `jackson-module-kotlin` | поддержка Kotlin data class |

Spring Boot подключает всё автоматически через `jackson-databind`.

## Q2. (!) Что такое `ObjectMapper` и как его использовать?

`ObjectMapper` — основной класс для связывания POJO и JSON.

```java
ObjectMapper mapper = new ObjectMapper();

// сериализация
String json = mapper.writeValueAsString(user);          // POJO → String
mapper.writeValue(new File("user.json"), user);         // → File
byte[] bytes = mapper.writeValueAsBytes(user);          // → byte[]

// десериализация
User user = mapper.readValue(json, User.class);         // String → POJO
User user2 = mapper.readValue(new File("user.json"), User.class);
User user3 = mapper.readValue(inputStream, User.class);
```

`ObjectMapper` потокобезопасен **после** полной конфигурации. Настраивай один раз при старте, дальше используй как singleton.

## Q3. Нужно ли создавать новый `ObjectMapper` на каждый запрос?

Нет. `ObjectMapper` — дорогой объект (строит кэш рефлексии). Создание на каждый запрос — anti-pattern, может легко давать 10× просадку.

**Правильно:** один singleton на приложение.

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
```

Если нужны разные конфигурации — создавай именованные `ObjectMapper`-ы или используй `mapper.copy()` (поверхностная копия для быстрой настройки).

## Q4. (!) Какие у Jackson три API для работы с JSON?

1. **Streaming API** (`jackson-core`) — самый низкий уровень, token-по-токену через `JsonParser` / `JsonGenerator`. Максимальная скорость, минимум памяти, но много кода.
2. **Tree Model** (`JsonNode`) — JSON как дерево (`JsonNode`, `ObjectNode`, `ArrayNode`). Гибко, но без типизации.
3. **Data Binding** (`ObjectMapper`) — POJO ↔ JSON. 95% случаев.

```java
// streaming
JsonFactory f = new JsonFactory();
try (JsonParser p = f.createParser(json)) {
    while (p.nextToken() != null) { /* ... */ }
}

// tree
JsonNode root = mapper.readTree(json);
String name = root.get("user").get("name").asText();

// databind
User u = mapper.readValue(json, User.class);
```

**Итог:** databind по умолчанию; tree — когда структура динамическая; streaming — когда JSON огромный (гигабайты).

## Q5. Как прочитать JSON в `Map` или `List`?

```java
Map<String, Object> m = mapper.readValue(json, Map.class);
List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<>() {});
```

Второй вариант — правильный, если важна типизация элементов.

## Q6. (!) Как настроить Jackson, чтобы он не падал на unknown properties?

По умолчанию Jackson кидает `UnrecognizedPropertyException`, если в JSON есть поле, которого нет в POJO. Для совместимости с версионируемыми API это почти всегда нежелательно.

Глобально:

```java
mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
```

Для конкретного класса:

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class User { /* ... */ }
```

В Spring Boot — через `application.yml`:

```yaml
spring:
  jackson:
    deserialization:
      fail-on-unknown-properties: false
```

**Рекомендация:** выключать в сервисах-потребителях внешних API; оставлять включённым в strict-контрактах внутри компании.

## Q7. Какие флаги `SerializationFeature` / `DeserializationFeature` важны?

**SerializationFeature:**

| Флаг | Значение |
|---|---|
| `WRITE_DATES_AS_TIMESTAMPS` | даты как число vs ISO-8601 (лучше отключить) |
| `INDENT_OUTPUT` | pretty-print |
| `FAIL_ON_EMPTY_BEANS` | падать на объектах без getters |
| `WRITE_NULL_MAP_VALUES` | писать null-значения в `Map` |

**DeserializationFeature:**

| Флаг | Значение |
|---|---|
| `FAIL_ON_UNKNOWN_PROPERTIES` | падать на неизвестных полях |
| `FAIL_ON_NULL_FOR_PRIMITIVES` | null в `int` (иначе 0) |
| `ACCEPT_SINGLE_VALUE_AS_ARRAY` | `"tags": "one"` → `List.of("one")` |
| `READ_UNKNOWN_ENUM_VALUES_AS_NULL` | неизвестный enum → null |

Типичный продакшен-набор:

```java
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
```

## Q8. Как включить pretty-print?

```java
String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

// глобально
mapper.enable(SerializationFeature.INDENT_OUTPUT);
```

В продакшене pretty-print обычно не нужен — тратит bytes и CPU. Оставляй только в dev/тестовых эндпоинтах.

## Q9. Как настроить стратегию именования полей (snake_case)?

```java
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
// или
mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
```

Spring Boot `application.yml`:

```yaml
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE
```

`firstName` → `"first_name"` в JSON.

Локально — через аннотацию:

```java
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    private String firstName;   // сериализуется как "first_name"
}
```

## Q10. (!) Что делают `@JsonProperty`, `@JsonIgnore` и `@JsonIgnoreProperties`?

- `@JsonProperty("name")` — задать имя JSON-поля вручную. Также нужен для immutable-объектов без `module-parameter-names`.
- `@JsonIgnore` — не сериализовать/десериализовать это поле.
- `@JsonIgnoreProperties({"password"})` — список игнорируемых на уровне класса.
- `@JsonIgnoreProperties(ignoreUnknown = true)` — толерантность к лишним полям JSON.

```java
public class User {
    @JsonProperty("user_name")
    private String username;

    @JsonIgnore
    private String passwordHash;
}
```

Альтернатива для PII — `@JsonIgnore` + отдельный DTO для ответа.

## Q11. (!) Зачем нужны `@JsonCreator` и `@JsonValue`?

Эти аннотации — для single-value типов (enum, wrapper типов):

`@JsonValue` помечает метод, значение которого пойдёт в JSON вместо всего объекта:

```java
public enum Status {
    ACTIVE("A"), INACTIVE("I");
    private final String code;
    Status(String code) { this.code = code; }

    @JsonValue
    public String getCode() { return code; }        // сериализуется как "A"
}
```

`@JsonCreator` помечает фабричный метод/конструктор для десериализации:

```java
@JsonCreator
public static Status from(String code) {
    for (Status s : values()) if (s.code.equals(code)) return s;
    throw new IllegalArgumentException(code);
}
```

Типичное применение: value objects (`Email`, `Money`), enum с кодами, wrapper-типы UUID.

## Q12. Что делает `@JsonInclude`?

Контроль пустых значений при сериализации:

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public String name;       // null → поле пропускается
    public Integer age;
}
```

Значения:

| Значение | Что пропускается |
|---|---|
| `ALWAYS` | всегда сериализовать |
| `NON_NULL` | null |
| `NON_ABSENT` | null, пустой `Optional` |
| `NON_EMPTY` | null, пустая `Collection`/`Map`/`String` |
| `NON_DEFAULT` | значения по умолчанию класса |

Глобально: `mapper.setSerializationInclusion(Include.NON_NULL);`.

## Q13. Что такое `@JsonFormat` и для чего нужен?

Управление форматом отдельного поля при (де)сериализации — чаще всего для дат:

```java
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "UTC")
    private LocalDateTime startedAt;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Color color;       // enum как ordinal
}
```

## Q14. В чём разница между `@JsonSetter` и `@JsonGetter`?

Альтернативы `@JsonProperty`, прямо привязанные к направлению:

- `@JsonGetter("name")` — только при сериализации (на методе-getter).
- `@JsonSetter("name")` — только при десериализации (на методе-setter).

Полезно, когда JSON-имя для чтения и записи разные:

```java
public class Temperature {
    @JsonSetter("celsius")
    public void setFromCelsius(double c) { this.kelvin = c + 273.15; }

    @JsonGetter("kelvin")
    public double getKelvin() { return kelvin; }
}
```

## Q15. Что делает `@JsonAlias`?

Разрешает несколько имён для поля при **десериализации**:

```java
public class User {
    @JsonAlias({"firstName", "first_name", "fn"})
    private String name;
}
```

Читает любое из вариантов, пишет под основным именем. Удобно при миграции API.

## Q16. (!) Как десериализовать `List<Foo>` или сложный generic-тип?

Из-за type erasure нужно передать `TypeReference`:

```java
List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});
Map<String, List<Order>> map =
    mapper.readValue(json, new TypeReference<>() {});
```

Альтернатива — `TypeFactory`:

```java
JavaType type = mapper.getTypeFactory()
        .constructCollectionType(List.class, User.class);
List<User> users = mapper.readValue(json, type);
```

## Q17. Почему `readValue(json, List.class)` возвращает `List<LinkedHashMap>`?

```java
List list = mapper.readValue(json, List.class);
// list — List<LinkedHashMap>, не List<User>
```

Причина — type erasure: Jackson не знает generic-параметр `List`. Без `TypeReference` он сериализует объекты в generic `LinkedHashMap` (default-конверсия для JSON object).

**Фикс:** всегда `TypeReference` или `constructCollectionType`.

## Q18. (!) Как написать кастомный сериализатор?

```java
public class MoneySerializer extends StdSerializer<Money> {
    public MoneySerializer() { super(Money.class); }

    @Override
    public void serialize(Money value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("amount", value.amount().toPlainString());
        gen.writeStringField("currency", value.currency().getCurrencyCode());
        gen.writeEndObject();
    }
}
```

Регистрация:

```java
// вариант 1: аннотацией на классе
@JsonSerialize(using = MoneySerializer.class)
public record Money(BigDecimal amount, Currency currency) {}

// вариант 2: через модуль
SimpleModule module = new SimpleModule();
module.addSerializer(Money.class, new MoneySerializer());
mapper.registerModule(module);
```

## Q19. (!) Как написать кастомный десериализатор?

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

Регистрация — через `@JsonDeserialize(using = ...)` или `SimpleModule.addDeserializer(...)`.

## Q20. Как вызвать default-сериализатор из кастомного?

Иногда нужно добавить одно поле, но оставить всё остальное как есть. Решение — `BeanSerializerModifier` или делегирование:

```java
public class UserAddFieldSerializer extends StdSerializer<User>
        implements ResolvableSerializer {

    private final JsonSerializer<Object> defaultSer;

    public UserAddFieldSerializer(JsonSerializer<Object> defaultSer) {
        super(User.class);
        this.defaultSer = defaultSer;
    }

    @Override
    public void resolve(SerializerProvider provider) { /* resolve nested */ }

    @Override
    public void serialize(User v, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeStartObject();
        defaultSer.unwrappingSerializer(null).serialize(v, gen, sp);
        gen.writeStringField("_extra", computeExtra(v));
        gen.writeEndObject();
    }
}
```

## Q21. Что такое `Module` / `SimpleModule` в Jackson?

`Module` — контейнер для расширений: сериализаторы, десериализаторы, типы, абстрактные типы→реализации.

```java
SimpleModule m = new SimpleModule("MyModule");
m.addSerializer(Money.class, new MoneySerializer());
m.addDeserializer(Money.class, new MoneyDeserializer());
m.setAbstractTypes(new SimpleAbstractTypeResolver()
        .addMapping(Shape.class, Circle.class));
mapper.registerModule(m);
```

Готовые модули: `JavaTimeModule`, `Jdk8Module`, `ParameterNamesModule`, `KotlinModule`. Для автоматического поиска — `ObjectMapper.findAndRegisterModules()` (сканирует classpath через `ServiceLoader`).

## Q22. (!) Как сериализовать полиморфную иерархию классов?

Нужна аннотация `@JsonTypeInfo` на базовом классе + `@JsonSubTypes`:

```java
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Circle.class,    name = "circle"),
    @JsonSubTypes.Type(value = Rectangle.class, name = "rect")
})
public abstract class Shape {}
```

В JSON это будет так:

```json
{"type": "circle", "radius": 5}
{"type": "rect",   "w": 10, "h": 20}
```

Варианты `JsonTypeInfo.Id`:

| Id | Что пишется |
|---|---|
| `NAME` | логическое имя (надо описать через `@JsonSubTypes`) |
| `CLASS` | FQN класса (опасно при версионировании) |
| `MINIMAL_CLASS` | сокращённый FQN |
| `CUSTOM` | свой резолвер |

`As.PROPERTY` (default) — отдельное поле, `As.WRAPPER_OBJECT` — обёртка `{"circle": {...}}`, `As.EXISTING_PROPERTY` — тип уже есть в payload.

## Q23. Что такое Default Typing и почему он опасен?

`mapper.activateDefaultTyping(...)` заставляет Jackson записывать FQN классов в каждый JSON-объект, чтобы корректно десериализовать полиморфные поля без аннотаций.

**Опасность:** атакующий подсовывает FQN «эксплойт-класса» с cost-constructor (например `TemplatesImpl` или гаджет-цепочка) и Jackson его мгновенно инстанцирует — **remote code execution**. Именно так появились CVE-2017-7525, CVE-2019-12086 и целая серия.

**Правило:** никогда не включай default typing для данных извне. Если нужна полиморфия — только `@JsonTypeInfo` с whitelist-ом (`use = NAME` + явные `@JsonSubTypes`), либо `PolymorphicTypeValidator`.

## Q24. (!) Почему `LocalDateTime` сериализуется как массив и как это исправить?

По умолчанию Jackson не знает про `java.time` и пытается его сериализовать через bean-свойства, что даёт `[2026,4,20,12,0,0]`.

Фикс — подключить модуль:

```java
mapper.registerModule(new JavaTimeModule());
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);   // → ISO-8601 строка
```

В Spring Boot — делается автоматически, если зависимость `jackson-datatype-jsr310` на classpath (включена в `spring-boot-starter-json`).

Для custom-форматов:

```java
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
private LocalDateTime startedAt;
```

## Q25. Как работать с `BigDecimal` в JSON?

По умолчанию `BigDecimal` сериализуется как число. Риск: при десериализации в `double` теряется точность.

Рекомендация — принимать JSON-число как `BigDecimal` на стороне Jackson и включить:

```java
mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
```

Либо хранить как строку:

```java
@JsonFormat(shape = JsonFormat.Shape.STRING)
private BigDecimal amount;        // "19.99" вместо 19.99
```

**Вывод:** для денег — всегда `BigDecimal`, включай `USE_BIG_DECIMAL_FOR_FLOATS` или храни как строку для абсолютной защиты от float.

## Q26. Когда использовать Streaming API (`JsonParser`/`JsonGenerator`)?

Когда JSON настолько большой, что не помещается в память (или когда обрабатывается потоково — NDJSON, event-stream):

```java
try (JsonParser p = mapper.getFactory().createParser(input)) {
    if (p.nextToken() != JsonToken.START_ARRAY) throw new IllegalStateException();
    while (p.nextToken() != JsonToken.END_ARRAY) {
        User user = mapper.readValue(p, User.class);    // один элемент за раз
        process(user);
    }
}
```

Для обычных API (килобайты) — databind; streaming полезен для ETL или exports 100+ MB.

## Q27. Что такое Tree Model (`JsonNode`)?

Разбор JSON в дерево `JsonNode` — удобно, если структура заранее неизвестна или нужно точечно обработать несколько полей:

```java
JsonNode root = mapper.readTree(json);
String name = root.path("user").path("name").asText("unknown");
root.path("tags").forEach(n -> System.out.println(n.asText()));

ObjectNode obj = mapper.createObjectNode()
        .put("status", "ok")
        .put("count", 42);
obj.putArray("items").add(1).add(2).add(3);
```

`path(...)` vs `get(...)`: `get` вернёт `null` при отсутствии, `path` — `MissingNode` (можно безопасно цепочку вызывать).

## Q28. (!) Как настроить `ObjectMapper` в Spring Boot?

Три основных способа:

**1. Через `application.yml`:**

```yaml
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: false
      indent-output: false
    deserialization:
      fail-on-unknown-properties: false
    default-property-inclusion: non_null
    time-zone: UTC
    date-format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX
```

**2. Через `Jackson2ObjectMapperBuilderCustomizer`:**

```java
@Bean
Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
}
```

**3. Полная замена — объявить свой `@Bean ObjectMapper`:**

```java
@Bean
@Primary
public ObjectMapper objectMapper() {
    return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
}
```

**Рекомендация:** пункт 2 — наименее инвазивно, не рвёт auto-configuration.

## Q29. Что такое `@JsonView` и когда его использовать?

Позволяет выставлять **разные представления** одного объекта в разных эндпоинтах без двух DTO.

```java
public class Views {
    public interface Public {}
    public interface Internal extends Public {}
}

public class User {
    @JsonView(Views.Public.class)   public Long id;
    @JsonView(Views.Public.class)   public String name;
    @JsonView(Views.Internal.class) public String email;
    @JsonView(Views.Internal.class) public String phone;
}
```

В Spring-контроллере:

```java
@GetMapping("/public/users/{id}")
@JsonView(Views.Public.class)
public User publicView(@PathVariable Long id) { /* ... */ }

@GetMapping("/internal/users/{id}")
@JsonView(Views.Internal.class)
public User internalView(@PathVariable Long id) { /* ... */ }
```

**Когда использовать:** 2-3 view одной модели. При 5+ view — проще завести отдельные DTO с [[spring-validation|валидацией]] и [[java-annotations-interview|маппингом]].

## Q30. (!) Частые ошибки и как их диагностировать?

| Ошибка | Причина | Фикс |
|---|---|---|
| `UnrecognizedPropertyException` | unknown поле в JSON | `FAIL_ON_UNKNOWN_PROPERTIES = false` или `@JsonIgnoreProperties(ignoreUnknown = true)` |
| `InvalidDefinitionException: Cannot construct instance ... no Creators` | нет default-конструктора + нет `@JsonCreator` | добавить no-args конструктор или `@JsonCreator` |
| `JsonMappingException: Cannot deserialize value of type ... from Array` | `LocalDateTime` без `JavaTimeModule` | `registerModule(new JavaTimeModule())` |
| `Infinite recursion (StackOverflowError)` | циклическая связь (parent-child) | `@JsonManagedReference` + `@JsonBackReference` или `@JsonIdentityInfo` |
| `InvalidFormatException: not a valid ... representation` | strict-parsing enum | `READ_UNKNOWN_ENUM_VALUES_AS_NULL` или `@JsonEnumDefaultValue` |
| `Could not resolve type id ...` | неизвестный discriminator в полиморфизме | проверить `@JsonSubTypes` или `defaultImpl` |

## Q31. Насколько Jackson быстр? Когда использовать альтернативы?

Jackson — быстрый и зрелый. Типовые цифры (JMH, простой POJO):

- Serialize: ~5-10M операций/сек на одном ядре.
- Deserialize: ~3-6M операций/сек.

Альтернативы:

| Библиотека | Когда |
|---|---|
| `Gson` | простота, не важно быстродействие |
| `fastjson2` | китайский, очень быстрый, но история CVE |
| `DSL-JSON` | до 2× быстрее, если знаешь схему заранее |
| `protobuf` / `flatbuffers` | binary, внутренние сервисы |
| `json-iter`, `moshi` | mobile/Android |

**Итог:** для 99% backend-сервисов Jackson — правильный выбор. Switch на альтернативу — только при доказанном bottleneck в профилировщике.

---

## See also

- [[spring-mvc-interview]] — как Jackson работает с `HttpMessageConverter` в REST
- [[spring-webflux-interview]] — реактивная (де)сериализация через Jackson в WebFlux
- [[spring-boot-interview]] — `spring.jackson.*` настройки и auto-configuration
- [[java-serialization-interview]] — `Serializable`, разница с JSON-сериализацией
- [[java-annotations-interview]] — механика аннотаций, которую использует Jackson
- [[java-generics-interview]] — type erasure и `TypeReference`
- [[java-17-21-interview]] — `record` и Jackson (canonical constructor, `ParameterNamesModule`)
- [[http-rest-interview]] — JSON как формат REST-ответов
- [[openapi-swagger-interview]] — описание типов в OpenAPI vs Jackson-аннотации
- [[application-security-interview]] — Jackson-CVE через Default Typing, safe deserialization
- [[java-jackson|Шпаргалка: Jackson: JSON-сериализация в Java]] — теория
