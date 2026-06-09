---
title: "Вопросы на собеседовании: Jackson (JSON)"
description: "Jackson на собеседовании: ObjectMapper, аннотации, custom сериализаторы/десериализаторы, полиморфизм, дженерики, даты, streaming API, интеграция со Spring, производительность."
tags:
  - interview
  - programming-languages
  - java-jackson-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Jackson"
  - "JSON"
  - "Jackson interview"
prerequisites:
  - "[[java-jackson]]"
next: []
updated: "2026-04-25"
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

`Jackson` — набор Java-библиотек для (де)сериализации JSON (а также XML, YAML, CBOR, Smile). Де-факто стандарт в Spring, Dropwizard, RESTEasy: именно его подключают по умолчанию для преобразования объектов в JSON и обратно.

Ключевая идея архитектуры — **разделение на слои**. Низкоуровневое потоковое ядро (`jackson-core`) ничего не знает про аннотации и POJO; поверх него `jackson-databind` строит привязку объектов, а отдельные datatype-модули добавляют поддержку конкретных типов (`java.time`, `Optional`, Kotlin). Поэтому Jackson легко расширять, не трогая ядро.

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

Spring Boot тянет нужный набор автоматически через `spring-boot-starter-json`, поэтому на практике достаточно работать с `ObjectMapper` — модули уже зарегистрированы.

## Q2. (!) Что такое `ObjectMapper` и как его использовать?

`ObjectMapper` — центральный класс data binding: он превращает POJO в JSON (`writeValue*`) и JSON в POJO (`readValue`). По сути это фасад над всем механизмом сериализации, хранящий конфигурацию (фичи, модули, naming strategy) и кэш рефлексии по типам.

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

**Важный нюанс потокобезопасности:** `ObjectMapper` безопасно вызывать из многих потоков **только после** того, как конфигурация завершена. Сами методы `readValue`/`writeValue` реентерабельны, а вот изменение настроек (`configure`, `registerModule`) во время работы — гонка. Поэтому стандартный паттерн: один раз настроить при старте, дальше переиспользовать как singleton, не меняя его в рантайме.

## Q3. Нужно ли создавать новый `ObjectMapper` на каждый запрос?

Нет, и это частая ошибка под нагрузкой. `ObjectMapper` дорог в создании: при первой (де)сериализации каждого типа он анализирует его рефлексией и строит сериализатор/десериализатор, который затем **кэширует внутри себя**. Новый mapper на каждый запрос обнуляет этот кэш — приходится каждый раз заново читать структуру класса. На горячем пути это легко даёт 10× просадку.

**Правильно:** один настроенный singleton на приложение — тогда кэш «прогревается» один раз и переиспользуется.

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

Если нужны разные конфигурации (например, snake_case для одного API и camelCase для другого), заведи несколько именованных bean-ов либо используй `mapper.copy()` — он копирует настройки в новый экземпляр, который можно донастроить, не трогая исходный singleton.

## Q4. (!) Какие у Jackson три API для работы с JSON?

Это три уровня абстракции — от «ручного управления каждым токеном» до «отдай объект, остальное сделаю сам». Чем выше уровень, тем меньше кода, но больше накладных расходов на память и рефлексию.

1. **Streaming API** (`jackson-core`) — самый низкий уровень, чтение/запись token-за-токеном через `JsonParser` / `JsonGenerator`. Ничего не держит в памяти целиком, поэтому максимальная скорость и минимум аллокаций, но писать руками много.
2. **Tree Model** (`JsonNode`) — весь JSON загружается в дерево узлов (`JsonNode`, `ObjectNode`, `ArrayNode`). Гибко, не требует заранее известного класса, но нет типизации — лазаешь по узлам вручную.
3. **Data Binding** (`ObjectMapper`) — автоматическое POJO ↔ JSON. Самый удобный и потому основной режим (~95% случаев); внутри он построен поверх streaming-ядра.

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

Когда структура заранее неизвестна, JSON удобно разобрать в `Map`/`List` обобщённых типов:

```java
Map<String, Object> m = mapper.readValue(json, Map.class);
List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<>() {});
```

Разница в том, насколько Jackson знает тип элементов. `Map.class` теряет generic-параметры из-за type erasure, поэтому годится лишь для плоского «строка → произвольное значение». Если же нужен вложенный generic-тип (`List<Map<String, Object>>`), его передают через `TypeReference` — анонимный подкласс сохраняет полный тип в рантайме, и Jackson правильно соберёт вложенную структуру.

## Q6. (!) Как настроить Jackson, чтобы он не падал на unknown properties?

По умолчанию Jackson кидает `UnrecognizedPropertyException`, если в JSON встретилось поле, которого нет в целевом POJO. Это защищает от опечаток в контракте, но ломает прямую совместимость: стоит поставщику API добавить новое поле — и старый клиент падает, хотя мог бы его просто проигнорировать. Поэтому для потребителей внешних/версионируемых API такую строгость почти всегда снимают.

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

Это два enum-набора булевых переключателей, которыми тонко настраивают поведение mapper-а: `SerializationFeature` влияет на запись (POJO → JSON), `DeserializationFeature` — на чтение (JSON → POJO). Знать наизусть весь список не нужно, но несколько флагов реально решают типовые проблемы прода.

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

Есть два способа — точечный (на один вызов) и глобальный (на весь mapper):

```java
String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

// глобально
mapper.enable(SerializationFeature.INDENT_OUTPUT);
```

**Рекомендация:** в продакшене pretty-print не нужен — отступы и переносы раздувают ответ и тратят CPU, а машине читаемость не важна. Включай его только в dev/debug-эндпоинтах. Если форматирование нужно лишь иногда, удобнее точечный `writerWithDefaultPrettyPrinter()`, а не глобальный флаг.

## Q9. Как настроить стратегию именования полей (snake_case)?

В Java поля принято писать в camelCase, а многие JSON-API ожидают snake_case. `PropertyNamingStrategy` автоматически переименовывает свойства в обе стороны, чтобы не расставлять `@JsonProperty` на каждом поле. Задать её можно на трёх уровнях: глобально, через Spring-конфиг или локально аннотацией.

Глобально на mapper-е:

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

Все три управляют тем, какие поля и под какими именами попадают в JSON, но отличаются уровнем и направлением действия:

- `@JsonProperty("name")` — переопределяет имя JSON-поля для одного свойства. Также обязателен для immutable-классов (record/конструктор) без `module-parameter-names`: иначе Jackson не знает, какой аргумент конструктора какому полю соответствует.
- `@JsonIgnore` — полностью исключает поле из (де)сериализации в обе стороны. Ставится на самом поле/геттере.
- `@JsonIgnoreProperties({"password"})` — то же исключение, но списком на уровне класса; удобно, когда нельзя или не хочется трогать сами поля.
- `@JsonIgnoreProperties(ignoreUnknown = true)` — другой смысл того же аннотатора: не падать на лишних полях во входящем JSON (локальный аналог `FAIL_ON_UNKNOWN_PROPERTIES = false`).

```java
public class User {
    @JsonProperty("user_name")
    private String username;

    @JsonIgnore
    private String passwordHash;
}
```

**Рекомендация для чувствительных данных (PII, пароли):** надёжнее не прятать поля аннотациями в доменной модели, а отдавать наружу отдельный DTO, где этих полей просто нет — так секрет не утечёт даже при случайной сериализации.

## Q11. (!) Зачем нужны `@JsonCreator` и `@JsonValue`?

Эта пара решает задачу: сериализовать объект не как JSON-структуру с полями, а как одно скалярное значение — и потом собрать его обратно. Типично для value object'ов и enum с кодами. `@JsonValue` отвечает за запись, `@JsonCreator` — за чтение; работают они в связке.

`@JsonValue` помечает метод, чьё возвращаемое значение пойдёт в JSON вместо всего объекта:

```java
public enum Status {
    ACTIVE("A"), INACTIVE("I");
    private final String code;
    Status(String code) { this.code = code; }

    @JsonValue
    public String getCode() { return code; }        // сериализуется как "A"
}
```

`@JsonCreator` помечает фабричный метод или конструктор, через который Jackson восстанавливает объект из этого скалярного значения:

```java
@JsonCreator
public static Status from(String code) {
    for (Status s : values()) if (s.code.equals(code)) return s;
    throw new IllegalArgumentException(code);
}
```

Типичное применение: value objects (`Email`, `Money`), enum с кодами, wrapper-типы UUID.

## Q12. Что делает `@JsonInclude`?

Определяет, какие «пустые» значения **исключать** из выходного JSON. Это и про размер ответа (не гнать клиенту десятки `null`-полей), и про чистоту контракта. Действует только при сериализации.

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

Самый частый выбор на практике — `NON_NULL`. Применить ко всему mapper-у можно глобально: `mapper.setSerializationInclusion(Include.NON_NULL);`.

## Q13. Что такое `@JsonFormat` и для чего нужен?

Задаёт форму и формат конкретного поля при (де)сериализации, переопределяя глобальные настройки mapper-а локально. Чаще всего нужен для дат (зафиксировать паттерн и таймзону), но `shape` управляет представлением и других типов — например, можно заставить enum писаться как число (ordinal), а не как имя.

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

`shape` выбирает категорию (STRING / NUMBER / ARRAY / OBJECT), `pattern` — точный формат внутри неё, `timezone` фиксирует зону для дат, чтобы вывод не зависел от настроек JVM.

## Q14. В чём разница между `@JsonSetter` и `@JsonGetter`?

Это однонаправленные версии `@JsonProperty`. Обычный `@JsonProperty` задаёт имя сразу для чтения и записи; здесь же направление разделено:

- `@JsonGetter("name")` — действует только при сериализации (вешается на getter).
- `@JsonSetter("name")` — действует только при десериализации (вешается на setter).

Это нужно в редком, но реальном случае, когда имя поля в JSON для чтения и для записи должно различаться, либо когда логика входа и выхода разная:

```java
public class Temperature {
    @JsonSetter("celsius")
    public void setFromCelsius(double c) { this.kelvin = c + 273.15; }

    @JsonGetter("kelvin")
    public double getKelvin() { return kelvin; }
}
```

## Q15. Что делает `@JsonAlias`?

Разрешает принимать поле под несколькими именами **только при десериализации**:

```java
public class User {
    @JsonAlias({"firstName", "first_name", "fn"})
    private String name;
}
```

Ключевая асимметрия: на чтении Jackson распознаёт любое из перечисленных имён (плюс основное), а на запись всегда выводит одно — основное (`name`). Поэтому `@JsonAlias` идеален при миграции API: новые клиенты шлют новое имя, старые — прежнее, оба парсятся, а наружу отдаётся единый канонический ключ.

## Q16. (!) Как десериализовать `List<Foo>` или сложный generic-тип?

Из-за type erasure в рантайме от `List<User>` остаётся просто `List` — параметр `User` стирается. Чтобы донести его до Jackson, используют `TypeReference`: это анонимный подкласс, и информация о типе-параметре сохраняется в его суперклассе через рефлексию (`getGenericSuperclass`). Поэтому важны и фигурные скобки `{}` — без них анонимного класса не возникнет.

```java
List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});
Map<String, List<Order>> map =
    mapper.readValue(json, new TypeReference<>() {});
```

Альтернатива — собрать тип программно через `TypeFactory` (удобно, когда тип элемента известен только в рантайме):

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

Причина та же — type erasure: из `List.class` Jackson видит только «это список», но не знает, во что разбирать его элементы. Когда целевой тип элемента неизвестен, Jackson по умолчанию разбирает каждый JSON-объект в `LinkedHashMap` (порядок ключей сохраняется). Снаружи код компилируется, а вот `ClassCastException` прилетит позже — когда ты обратишься к элементу как к `User`.

**Фикс:** всегда передавать полный тип — через `TypeReference` или `constructCollectionType`.

## Q18. (!) Как написать кастомный сериализатор?

Когда стандартного отображения «поле → JSON-ключ» недостаточно (нестандартное представление типа, агрегация нескольких полей), наследуешь `StdSerializer<T>` и в методе `serialize` вручную выписываешь нужный JSON через `JsonGenerator`:

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

Зарегистрировать сериализатор можно двумя путями. Аннотация привязывает его к типу намертво — удобно, когда тип твой. Модуль же не трогает класс, поэтому подходит для чужих/сторонних типов и позволяет включать сериализатор только в нужных mapper-ах:

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

Зеркально сериализатору: наследуешь `StdDeserializer<T>` и в `deserialize` сам собираешь объект из входного JSON. Здесь два стиля — либо читать JSON в дерево (`readTree`) и доставать узлы, как ниже (проще, но загружает фрагмент в память), либо идти по токенам через `JsonParser` (быстрее для крупных объектов).

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

Частая задача: добавить в вывод вычисляемое поле, но не переписывать сериализацию всего объекта руками. Писать весь JSON заново хрупко — добавишь поле в класс и забудешь про сериализатор. Правильнее переиспользовать стандартный сериализатор Jackson, а своё дописать поверх. Делается это либо через `BeanSerializerModifier`, либо делегированием в дефолтный сериализатор, как ниже:

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

`Module` — это пакет расширений Jackson: один объект, который за раз регистрирует в mapper-е набор сериализаторов, десериализаторов, маппингов «абстрактный тип → реализация» и т. п. Смысл — собрать связанную функциональность в переиспользуемую единицу и подключать её одной строкой `registerModule(...)`, вместо россыпи отдельных вызовов. Именно так оформлены и стандартные расширения (поддержка `java.time`, Kotlin), и твои собственные.

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

Проблема: при десериализации поля типа `Shape` Jackson по самому JSON не знает, какой конкретный подкласс создавать — `Circle` или `Rectangle`. Решение — записать в JSON дискриминатор (type-маркер) и заранее перечислить допустимые подтипы. За это отвечают две аннотации на базовом классе: `@JsonTypeInfo` задаёт, как и где хранить маркер, а `@JsonSubTypes` — белый список «имя ↔ класс»:

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

Default Typing — это режим, включаемый `mapper.activateDefaultTyping(...)`, при котором Jackson сам пишет полное имя класса (FQN) в каждый JSON-объект и при чтении инстанцирует ровно этот класс. Так можно сериализовать полиморфные поля без аннотаций — но за удобство платишь дырой в безопасности.

**Почему это опасно:** имя класса теперь приходит из данных, а Jackson ему доверяет. Атакующий подставляет FQN «гаджет-класса», у которого в конструкторе/сеттерах есть опасный побочный эффект (классика — `TemplatesImpl` или цепочка гаджетов), и Jackson при десериализации мгновенно его создаёт и вызывает — это и есть **remote code execution**. Так появилась целая серия CVE: CVE-2017-7525, CVE-2019-12086 и десятки других.

**Правило:** никогда не включай default typing для данных извне. Если нужна полиморфия — только `@JsonTypeInfo` с whitelist-ом (`use = NAME` + явные `@JsonSubTypes`), либо `PolymorphicTypeValidator`.

## Q24. (!) Почему `LocalDateTime` сериализуется как массив и как это исправить?

«Голый» Jackson не знает типов `java.time` и обращается с `LocalDateTime` как с обычным бином — сериализует его геттеры/компоненты (год, месяц, день, час…), что и даёт массив чисел `[2026,4,20,12,0,0]`. Чтобы он трактовал дату как единое значение, нужен модуль поддержки `java.time`.

Фикс — зарегистрировать модуль:

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

По умолчанию `BigDecimal` сериализуется как обычное JSON-число. Главный риск — на обратном пути: если Jackson разбирает дробное число в `double` (а это поведение по умолчанию для «плавающих» значений), часть точности теряется ещё до того, как значение попадёт в `BigDecimal`. Для денег это недопустимо.

**Рекомендация:** заставить Jackson читать дробные числа сразу как `BigDecimal`, минуя `double`:

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

Главный сценарий — когда нельзя или не нужно держать весь JSON в памяти: большой массив на сотни мегабайт или потоковый формат (NDJSON, event-stream). Databind собрал бы весь `List<User>` в кучу целиком; streaming же читает по одному элементу и сразу его обрабатывает, поэтому потребление памяти не растёт с размером входа.

Типичный паттерн — пройти по массиву токенами, а каждый элемент всё же десериализовать через databind, чтобы не разбирать поля руками:

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

Tree Model — представление JSON в виде дерева узлов `JsonNode`, по которому ходишь вручную (как по DOM в HTML). Берут его, когда заводить POJO под весь ответ не хочется: структура заранее неизвестна, динамическая, либо нужно вытащить лишь пару полей из большого payload. Дерево можно как читать (`readTree`), так и строить с нуля (`createObjectNode`):

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

Spring Boot сам создаёт и автоконфигурирует `ObjectMapper`, поэтому в идеале нужно лишь скорректировать его, а не строить с нуля. Три способа — по нарастанию инвазивности: декларативно в `application.yml`, точечно через customizer (рекомендуемый) и полная замена своим bean-ом.

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

**Рекомендация:** способ 2 (`Jackson2ObjectMapperBuilderCustomizer`) — золотая середина: он лишь дополняет авто-конфигурированный mapper, поэтому уже зарегистрированные Spring-ом модули (`JavaTimeModule` и пр.) остаются на месте. Полная замена своим `@Bean ObjectMapper` отключает авто-конфигурацию целиком — тогда все модули и настройки придётся прописывать вручную, иначе легко получить, например, даты-массивы из Q24.

## Q29. Что такое `@JsonView` и когда его использовать?

`@JsonView` позволяет из одного класса отдавать **разные наборы полей** в разных эндпоинтах, не заводя под каждый отдельный DTO. Поля помечаются маркер-интерфейсами («views»), а эндпоинт указывает, какой view сериализовать — в JSON попадут только поля этого view (и унаследованных). Наследование интерфейсов выстраивает иерархию: например, `Internal extends Public` означает «internal-представление = всё публичное плюс приватные поля».

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

**Когда использовать:** 2-3 view одной модели. При 5+ view — проще завести отдельные DTO с [валидацией](../../../frameworks/java-frameworks/spring/spring-validation.md) и [маппингом](java-annotations-interview.md).

## Q30. (!) Частые ошибки и как их диагностировать?

Большинство исключений Jackson читаются по имени класса исключения и тексту — он почти всегда указывает на конкретное поле и тип. Вот шпаргалка по самым частым:

| Ошибка | Причина | Фикс |
|---|---|---|
| `UnrecognizedPropertyException` | unknown поле в JSON | `FAIL_ON_UNKNOWN_PROPERTIES = false` или `@JsonIgnoreProperties(ignoreUnknown = true)` |
| `InvalidDefinitionException: Cannot construct instance ... no Creators` | нет default-конструктора + нет `@JsonCreator` | добавить no-args конструктор или `@JsonCreator` |
| `JsonMappingException: Cannot deserialize value of type ... from Array` | `LocalDateTime` без `JavaTimeModule` | `registerModule(new JavaTimeModule())` |
| `Infinite recursion (StackOverflowError)` | циклическая связь (parent-child) | `@JsonManagedReference` + `@JsonBackReference` или `@JsonIdentityInfo` |
| `InvalidFormatException: not a valid ... representation` | strict-parsing enum | `READ_UNKNOWN_ENUM_VALUES_AS_NULL` или `@JsonEnumDefaultValue` |
| `Could not resolve type id ...` | неизвестный discriminator в полиморфизме | проверить `@JsonSubTypes` или `defaultImpl` |

## Q31. Насколько Jackson быстр? Когда использовать альтернативы?

Jackson — быстрый и зрелый: для подавляющего большинства сервисов сериализация JSON не является узким местом, а узкое место — БД и сеть. Типовые цифры (JMH, простой POJO):

- Serialize: ~5-10M операций/сек на одном ядре.
- Deserialize: ~3-6M операций/сек.

Альтернативы имеет смысл рассматривать только под конкретную нишу:

| Библиотека | Когда |
|---|---|
| `Gson` | простота, не важно быстродействие |
| `fastjson2` | китайский, очень быстрый, но история CVE |
| `DSL-JSON` | до 2× быстрее, если знаешь схему заранее |
| `protobuf` / `flatbuffers` | binary, внутренние сервисы |
| `json-iter`, `moshi` | mobile/Android |

**Итог:** для 99% backend-сервисов Jackson — правильный выбор. Switch на альтернативу — только при доказанном bottleneck в профилировщике.

## See also

- [spring-webflux-interview](../../frameworks/spring/spring-webflux-interview.md) — реактивная (де)сериализация через Jackson в WebFlux
- [spring-boot-interview](../../frameworks/spring/spring-boot-interview.md) — `spring.jackson.*` настройки и auto-configuration
- [java-serialization-interview](java-serialization-interview.md) — `Serializable`, разница с JSON-сериализацией
- [java-annotations-interview](java-annotations-interview.md) — механика аннотаций, которую использует Jackson
- [java-generics-interview](java-generics-interview.md) — type erasure и `TypeReference`
- [java-17-21-interview](java-17-21-interview.md) — `record` и Jackson (canonical constructor, `ParameterNamesModule`)
- [http-rest-interview](../../api/http-rest-interview.md) — JSON как формат REST-ответов
- [openapi-swagger-interview](../../api/openapi-swagger-interview.md) — описание типов в OpenAPI vs Jackson-аннотации
- [application-security-interview](../../security/application-security-interview.md) — Jackson-CVE через Default Typing, safe deserialization
- [Шпаргалка: Jackson: JSON-сериализация в Java](../../../libraries/java/java-jackson.md) — теория
