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
updated: "2026-11-07"
---
# Вопросы на собеседовании: `Jackson`

`Jackson` — де-факто стандартная библиотека JSON в экосистеме Java. На backend-собеседованиях спрашивают о `ObjectMapper`, ключевых аннотациях, кастомных сериализаторах, полиморфизме и нюансах интеграции со `Spring Boot`.

## Полезные ссылки

### Официальная документация
- [Jackson GitHub](https://github.com/FasterXML/jackson)
- [Jackson Databind Javadoc](https://fasterxml.github.io/jackson-databind/javadoc/2.15/)
- [Jackson Annotations Wiki](https://github.com/FasterXML/jackson-annotations/wiki)
- [Jackson 3 Migration Guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md) — официальный разбор несовместимостей 2 → 3
- [Spring Boot 4.1: JSON](https://docs.spring.io/spring-boot/reference/features/json.html) — Jackson 3 по умолчанию и временная поддержка Jackson 2

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
- [Q1. Что такое `Jackson` и из каких модулей он состоит?](#q1)
- [Q2. Что такое `ObjectMapper` и как его использовать?](#q2)
- [Q3. Нужно ли создавать новый `ObjectMapper` на каждый запрос?](#q3)
- [Q4. Какие у `Jackson` три API для работы с JSON?](#q4)
- [Q5. Как прочитать JSON в `Map` или `List`?](#q5)

**Конфигурация ObjectMapper**
- [Q6. Как настроить `Jackson`, чтобы он не падал на unknown properties?](#q6)
- [Q7. Какие флаги `SerializationFeature` / `DeserializationFeature` важны?](#q7)
- [Q8. Как включить pretty-print?](#q8)
- [Q9. Как настроить стратегию именования полей (`snake_case`)?](#q9)

**Ключевые аннотации**
- [Q10. Что делают `@JsonProperty`, `@JsonIgnore` и `@JsonIgnoreProperties`?](#q10)
- [Q11. Зачем нужны `@JsonCreator` и `@JsonValue`?](#q11)
- [Q12. Что делает `@JsonInclude`?](#q12)
- [Q13. Что такое `@JsonFormat` и для чего нужен?](#q13)
- [Q14. В чём разница между `@JsonSetter` и `@JsonGetter`?](#q14)
- [Q15. Что делает `@JsonAlias`?](#q15)

**Дженерики и TypeReference**
- [Q16. Как десериализовать `List<Foo>` или сложный generic-тип?](#q16)
- [Q17. Почему `readValue(json, List.class)` возвращает `List<LinkedHashMap>`?](#q17)

**Кастомные сериализаторы**
- [Q18. Как написать кастомный сериализатор?](#q18)
- [Q19. Как написать кастомный десериализатор?](#q19)
- [Q20. Как вызвать default-сериализатор из кастомного?](#q20)
- [Q21. Что такое `Module` / `SimpleModule` в `Jackson`?](#q21)

**Полиморфизм**
- [Q22. Как сериализовать полиморфную иерархию классов?](#q22)
- [Q23. Что такое Default Typing и почему он опасен?](#q23)

**Даты и специальные типы**
- [Q24. Почему `LocalDateTime` сериализуется как массив и как это исправить?](#q24)
- [Q25. Как работать с `BigDecimal` в JSON?](#q25)

**Streaming и Tree API**
- [Q26. Когда использовать Streaming API (`JsonParser`/`JsonGenerator`)?](#q26)
- [Q27. Что такое Tree Model (`JsonNode`)?](#q27)

**Spring и JSON Views**
- [Q28. Как настроить `ObjectMapper` в Spring Boot?](#q28)
- [Q29. Что такое `@JsonView` и когда его использовать?](#q29)

**Производительность и ошибки**
- [Q30. Частые ошибки `Jackson` и как их диагностировать?](#q30)
- [Q31. Насколько `Jackson` быстр и когда использовать альтернативы?](#q31)
- [Q32. Что проверить при миграции Jackson 2 → 3 и Spring Boot 3 → 4?](#q32)

<a id="q1"></a>
## Q1. Что такое `Jackson` и из каких модулей он состоит?

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

<a id="q2"></a>
## Q2. Что такое `ObjectMapper` и как его использовать?

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

<a id="q3"></a>
## Q3. Нужно ли создавать новый `ObjectMapper` на каждый запрос?

Нет. При первой (де)сериализации типа `ObjectMapper` анализирует его структуру и кэширует нужные обработчики. Новый mapper на каждый запрос не использует прогретый кэш и добавляет работу на горячем пути. Размер потерь зависит от типов и нагрузки, его проверяют измерением.

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

Если нужны разные настройки для отдельных вызовов, используй неизменяемые `ObjectReader` и `ObjectWriter`. Для другой конфигурации mapper можно создать отдельно или через `mapper.copy()` в Jackson 2. Копия стоит примерно как новый mapper и не является дешёвым способом получить варианты на каждый запрос.

<a id="q4"></a>
## Q4. Какие у `Jackson` три API для работы с JSON?

Это три уровня абстракции — от управления токенами до привязки Java-объектов. Выбор зависит от структуры данных, потребления памяти и нужного контроля над обработкой.

1. **Streaming API** (`jackson-core`) — низкий уровень, чтение/запись токен за токеном через `JsonParser` / `JsonGenerator`. Позволяет обрабатывать документ постепенно, если приложение само не накапливает его целиком.
2. **Tree Model** (`JsonNode`) — весь JSON загружается в дерево узлов (`JsonNode`, `ObjectNode`, `ArrayNode`). Гибко, не требует заранее известного класса, но нет типизации — лазаешь по узлам вручную.
3. **Data Binding** (`ObjectMapper`) — автоматическое POJO ↔ JSON поверх потокового ядра. Подходит, когда структура известна и нужен готовый Java-объект.

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

Для обычной модели удобно data binding; tree полезен при динамичной структуре; streaming — при постепенной обработке крупных данных.

<a id="q5"></a>
## Q5. Как прочитать JSON в `Map` или `List`?

Когда структура заранее неизвестна, JSON удобно разобрать в `Map`/`List` обобщённых типов:

```java
Map<String, Object> m = mapper.readValue(json, Map.class);
List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<>() {});
```

Разница в том, насколько Jackson знает тип элементов. `Map.class` теряет generic-параметры из-за type erasure, но может содержать вложенные `Map` и `List` с нетипизированными значениями. Если нужен конкретный вложенный generic-тип (`List<Map<String, Object>>`), его передают через `TypeReference` — анонимный подкласс сохраняет полную сигнатуру.

<a id="q6"></a>
## Q6. Как настроить `Jackson`, чтобы он не падал на unknown properties?

У чистого Jackson 2 `FAIL_ON_UNKNOWN_PROPERTIES` включён по умолчанию: неизвестное поле вызывает `UnrecognizedPropertyException`. В Jackson 3 этот флаг по умолчанию выключен; Spring Boot также может задать своё значение. Проверь настройку конкретного mapper. Для потребителя расширяемого внешнего API неизвестные поля можно игнорировать, если это допускает контракт.

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

В строгом контракте включённая проверка помогает поймать опечатки. В расширяемом внешнем API её часто отключают после оценки совместимости.

<a id="q7"></a>
## Q7. Какие флаги `SerializationFeature` / `DeserializationFeature` важны?

Это два enum-набора булевых переключателей, которыми тонко настраивают поведение mapper-а: `SerializationFeature` влияет на запись (POJO → JSON), `DeserializationFeature` — на чтение (JSON → POJO). Знать наизусть весь список не нужно, но несколько флагов реально решают типовые проблемы прода.

**SerializationFeature:**

| Флаг | Значение |
|---|---|
| `WRITE_DATES_AS_TIMESTAMPS` | формат дат как timestamp или строка; для `LocalDateTime` с `JavaTimeModule` возможен массив |
| `INDENT_OUTPUT` | pretty-print |
| `FAIL_ON_EMPTY_BEANS` | падать на объектах без getters |
| `WRITE_NULL_MAP_VALUES` | писать null-значения в `Map` |

**DeserializationFeature:**

| Флаг | Значение |
|---|---|
| `FAIL_ON_UNKNOWN_PROPERTIES` | падать на неизвестных полях; дефолт различается между Jackson 2 и 3 |
| `FAIL_ON_NULL_FOR_PRIMITIVES` | ошибка на null для примитива; в чистом Jackson 2 выключен, в Jackson 3 включён |
| `ACCEPT_SINGLE_VALUE_AS_ARRAY` | `"tags": "one"` → `List.of("one")` |
| `READ_UNKNOWN_ENUM_VALUES_AS_NULL` | неизвестный enum → null |

Типичный продакшен-набор:

```java
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
```

<a id="q8"></a>
## Q8. Как включить pretty-print?

Есть два способа — точечный (на один вызов) и глобальный (на весь mapper):

```java
String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

// глобально
mapper.enable(SerializationFeature.INDENT_OUTPUT);
```

**Рекомендация:** в продакшене pretty-print не нужен — отступы и переносы раздувают ответ и тратят CPU, а машине читаемость не важна. Включай его только в dev/debug-эндпоинтах. Если форматирование нужно лишь иногда, удобнее точечный `writerWithDefaultPrettyPrinter()`, а не глобальный флаг.

<a id="q9"></a>
## Q9. Как настроить стратегию именования полей (`snake_case`)?

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

<a id="q10"></a>
## Q10. Что делают `@JsonProperty`, `@JsonIgnore` и `@JsonIgnoreProperties`?

Все три управляют тем, какие поля и под какими именами попадают в JSON, но отличаются уровнем и направлением действия:

- `@JsonProperty("name")` — переопределяет имя JSON-поля для свойства. На параметре конструктора может явно задать имя, если Jackson не получает его из метаданных Java; для record это не универсальное требование.
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

<a id="q11"></a>
## Q11. Зачем нужны `@JsonCreator` и `@JsonValue`?

Эти аннотации помогают задать скалярное представление value object или enum. `@JsonValue` выбирает значение для записи; `@JsonCreator` может указать конструктор или фабрику для чтения. Для обычного POJO с таким представлением обратный путь часто требует creator. Для enum Jackson также может использовать значение `@JsonValue` при десериализации без отдельного `@JsonCreator`.

`@JsonValue` помечает метод или поле, значение которого пойдёт в JSON вместо всего объекта:

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

<a id="q12"></a>
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

<a id="q13"></a>
## Q13. Что такое `@JsonFormat` и для чего нужен?

Задаёт форму и формат конкретного значения при (де)сериализации, переопределяя глобальные настройки mapper-а локально. Чаще всего нужен для дат, но `shape` управляет представлением и других типов — например, enum можно записать числом (ordinal).

```java
public class Event {
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Color color;       // enum как ordinal
}
```

`shape` выбирает категорию (STRING / NUMBER / ARRAY / OBJECT), `pattern` — формат значения. У `LocalDateTime` нет часового пояса: параметр `timezone` не превращает его в момент времени UTC. Для зонного контракта выбирают подходящий тип, например `OffsetDateTime` или `Instant`.

<a id="q14"></a>
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

<a id="q15"></a>
## Q15. Что делает `@JsonAlias`?

Разрешает принимать поле под несколькими именами **только при десериализации**:

```java
public class User {
    @JsonAlias({"firstName", "first_name", "fn"})
    private String name;
}
```

Ключевая асимметрия: на чтении Jackson распознаёт любое из перечисленных имён (плюс основное), а на запись всегда выводит одно — основное (`name`). Поэтому `@JsonAlias` идеален при миграции API: новые клиенты шлют новое имя, старые — прежнее, оба парсятся, а наружу отдаётся единый канонический ключ.

<a id="q16"></a>
## Q16. Как десериализовать `List<Foo>` или сложный generic-тип?

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

<a id="q17"></a>
## Q17. Почему `readValue(json, List.class)` возвращает `List<LinkedHashMap>`?

```java
List list = mapper.readValue(json, List.class);
// list — List<LinkedHashMap>, не List<User>
```

Причина та же — type erasure: из `List.class` Jackson видит только «это список», но не знает, во что разбирать его элементы. Когда целевой тип элемента неизвестен, Jackson по умолчанию разбирает каждый JSON-объект в `LinkedHashMap` (порядок ключей сохраняется). Снаружи код компилируется, а вот `ClassCastException` прилетит позже — когда ты обратишься к элементу как к `User`.

**Фикс:** всегда передавать полный тип — через `TypeReference` или `constructCollectionType`.

<a id="q18"></a>
## Q18. Как написать кастомный сериализатор?

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

<a id="q19"></a>
## Q19. Как написать кастомный десериализатор?

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

<a id="q20"></a>
## Q20. Как вызвать default-сериализатор из кастомного?

Если нужен свой сериализатор с сохранением стандартной записи объекта, получи исходный сериализатор через `BeanSerializerModifier.modifySerializer` и передай его в обёртку. Вызов `delegate.serialize(value, gen, provider)` запишет обычный JSON для объекта. Нельзя безусловно вызвать `gen.writeStringField` после этого вызова: делегат уже закрыл объект.

```java
SimpleModule module = new SimpleModule();
module.setSerializerModifier(new BeanSerializerModifier() {
    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config,
            BeanDescription type, JsonSerializer<?> original) {
        if (type.getBeanClass() != User.class) return original;
        return new JsonSerializer<User>() {
            @Override
            public void serialize(User value, JsonGenerator gen,
                    SerializerProvider provider) throws IOException {
                @SuppressWarnings("unchecked")
                JsonSerializer<Object> delegate = (JsonSerializer<Object>) original;
                delegate.serialize(value, gen, provider);
            }
        };
    }
});
mapper.registerModule(module);
```

Если нужно добавить поле в тот же JSON-объект, настрой виртуальное свойство через `@JsonAppend` или изменяй список `BeanPropertyWriter` в `BeanSerializerModifier.changeProperties`. Одна лишь обёртка вокруг готового сериализатора не даёт безопасно дописать поле после закрывающей скобки.

<a id="q21"></a>
## Q21. Что такое `Module` / `SimpleModule` в `Jackson`?

`Module` — это пакет расширений Jackson: один объект, который за раз регистрирует в mapper-е набор сериализаторов, десериализаторов, маппингов «абстрактный тип → реализация» и т. п. Смысл — собрать связанную функциональность в переиспользуемую единицу и подключать её одной строкой `registerModule(...)`, вместо россыпи отдельных вызовов. Именно так оформлены и стандартные расширения (поддержка `java.time`, Kotlin), и твои собственные.

```java
SimpleModule m = new SimpleModule("MyModule");
m.addSerializer(Money.class, new MoneySerializer());
m.addDeserializer(Money.class, new MoneyDeserializer());
m.setAbstractTypes(new SimpleAbstractTypeResolver()
        .addMapping(Shape.class, Circle.class));
mapper.registerModule(m);
```

В Jackson 2 доступны готовые `JavaTimeModule`, `Jdk8Module`, `ParameterNamesModule` и `KotlinModule`; `findAndRegisterModules()` ищет их через `ServiceLoader`. В Jackson 3 поддержка основных типов Java 8 встроена, поэтому зависимости и регистрацию этих модулей при миграции пересматривают.

<a id="q22"></a>
## Q22. Как сериализовать полиморфную иерархию классов?

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

<a id="q23"></a>
## Q23. Что такое Default Typing и почему он опасен?

Default Typing добавляет информацию о классе к тем полиморфным значениям, которые охватывает выбранная настройка. Это позволяет читать подтипы без аннотаций. Для внешнего JSON риск возникает, если разрешён слишком широкий набор классов.

**Почему это опасно:** имя класса может прийти из данных. При слишком широком разрешении типов атакующий выбирает доступный гаджет-класс; вызовы при его создании могут привести к выполнению кода. Исторические CVE связаны с такой комбинацией настроек и недоверенного входа, а не с обычным `ObjectMapper` без Default Typing.

Для внешних данных предпочитай логические имена известных подтипов (`@JsonTypeInfo(use = NAME)` и явную регистрацию). Если Default Typing необходим, ограничь разрешённые типы строгим `PolymorphicTypeValidator`, проверь реальные payload и обновления зависимостей. Форматирование или проверка одной лишь структуры JSON этот риск не устраняют.

<a id="q24"></a>
## Q24. Почему `LocalDateTime` сериализуется как массив и как это исправить?

В **Jackson 2** массив вида `[2026,4,20,12,0,0]` выдаёт сериализатор `LocalDateTime` из `JavaTimeModule`, когда включён `SerializationFeature.WRITE_DATES_AS_TIMESTAMPS`. `LocalDateTime` не имеет часового пояса, поэтому модуль представляет его компоненты массивом, а не одним числом timestamp. Без модуля современные Jackson 2 обычно сообщают, что тип `java.time` не поддерживается; отсутствие модуля **не является причиной массива**.

Для Jackson 2 зарегистрируйте модуль, если его нет, и отключите запись дат в timestamp-формате:

```java
mapper.registerModule(new JavaTimeModule());
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);   // → ISO-8601 строка
```

Spring Boot 3 обычно регистрирует `JavaTimeModule` из `jackson-datatype-jsr310` и настраивает даты как ISO-строки. В **Jackson 3** поддержка `java.time` встроена, а `DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS` по умолчанию выключен. Если массив появился после своей настройки, проверьте именно этот флаг и локальный `@JsonFormat`; отдельный `JavaTimeModule` для Jackson 3 не нужен.

Для custom-форматов:

```java
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
private LocalDateTime startedAt;
```

<a id="q25"></a>
## Q25. Как работать с `BigDecimal` в JSON?

По умолчанию `BigDecimal` сериализуется как JSON-число. При чтении прямо в поле `BigDecimal` Jackson сохраняет десятичное значение. Риск возникает, когда число сначала читают в нетипизированный `Object`, `Map<String, Object>` или `double`: дробь может пройти через двоичное представление и потерять точность.

Для нетипизированного чтения можно указать `BigDecimal` как тип дробных значений:

```java
mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
```

Либо хранить как строку:

```java
@JsonFormat(shape = JsonFormat.Shape.STRING)
private BigDecimal amount;        // "19.99" вместо 19.99
```

Для денежных значений сохраняй тип `BigDecimal` на всём пути обработки. Строковое представление допустимо, если его требует JSON-контракт; само по себе оно не исправит последующее преобразование в `double` у получателя.

<a id="q26"></a>
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

<a id="q27"></a>
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

<a id="q28"></a>
## Q28. Как настроить `ObjectMapper` в Spring Boot?

В **Spring Boot 3 с Jackson 2** Boot создаёт и настраивает `ObjectMapper`, поэтому обычно достаточно скорректировать его. Три способа — по нарастанию инвазивности: декларативно в `application.yml`, точечно через customizer и замена своим bean-ом. Примеры ниже относятся именно к этой связке версий.

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

**Для Boot 3:** `Jackson2ObjectMapperBuilderCustomizer` дополняет builder автонастроенного mapper без полной замены bean. При собственном `@Bean ObjectMapper` Boot отступает от создания своего mapper, и его настройки/регистрацию модулей нужно проверить отдельно.

**Для Spring Boot 4 с Jackson 3:** по умолчанию автоконфигурируется `JsonMapper`; для точечной донастройки используется `JsonMapperBuilderCustomizer`. Старый `Jackson2ObjectMapperBuilderCustomizer` относится к временной поддержке Jackson 2 и помечен deprecated. Конкретный порядок миграции — в Q32.

<a id="q29"></a>
## Q29. Что такое `@JsonView` и когда его использовать?

`@JsonView` позволяет из одного класса отдавать **разные наборы полей** в разных эндпоинтах, не заводя под каждый отдельный DTO. Поля помечаются маркер-интерфейсами («views»), а эндпоинт указывает активное представление. Аннотация может также ограничивать чтение входного JSON при активном view; её не следует считать валидацией запроса. Наследование интерфейсов выстраивает иерархию: `Internal extends Public` означает «внутреннее представление = публичные поля плюс внутренние».

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

<a id="q30"></a>
## Q30. Частые ошибки `Jackson` и как их диагностировать?

Большинство исключений Jackson читаются по имени класса исключения и тексту — он почти всегда указывает на конкретное поле и тип. Вот шпаргалка по самым частым:

| Ошибка | Причина | Фикс |
|---|---|---|
| `UnrecognizedPropertyException` | unknown поле в JSON | `FAIL_ON_UNKNOWN_PROPERTIES = false` или `@JsonIgnoreProperties(ignoreUnknown = true)` |
| `InvalidDefinitionException: Cannot construct instance ... no Creators` | нет default-конструктора + нет `@JsonCreator` | добавить no-args конструктор или `@JsonCreator` |
| `InvalidDefinitionException: Java 8 date/time type ... not supported` (Jackson 2) | отсутствует `JavaTimeModule` | зарегистрировать модуль |
| `LocalDateTime` записался массивом | модуль есть, включён `WRITE_DATES_AS_TIMESTAMPS` | выключить режим timestamps или задать формат поля |
| `Infinite recursion (StackOverflowError)` | циклическая связь (parent-child) | `@JsonManagedReference` + `@JsonBackReference` или `@JsonIdentityInfo` |
| `InvalidFormatException: not a valid ... representation` | неизвестное значение enum | `READ_UNKNOWN_ENUM_VALUES_AS_NULL` либо `@JsonEnumDefaultValue` вместе с `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE` |
| `Could not resolve type id ...` | неизвестный discriminator в полиморфизме | проверить `@JsonSubTypes` или `defaultImpl` |

<a id="q31"></a>
## Q31. Насколько `Jackson` быстр и когда использовать альтернативы?

Jackson применяют во многих Java-сервисах. Скорость зависит от модели, размера JSON, настроек, прогрева JIT и способа измерения. Если сериализация влияет на задержку, проверь это профилировщиком и измерь свой сценарий.

Альтернативы имеет смысл рассматривать только под конкретную нишу:

| Библиотека | Когда |
|---|---|
| `Gson` | простота, не важно быстродействие |
| `fastjson2` | китайский, очень быстрый, но история CVE |
| `DSL-JSON` | кандидат для сравнения на известной схеме и своей нагрузке |
| `protobuf` / `flatbuffers` | binary, внутренние сервисы |
| `json-iter`, `moshi` | mobile/Android |

Альтернативу выбирают по конкретному требованию к формату, платформе или производительности после измерений на своих данных.

<a id="q32"></a>
## Q32. Что проверить при миграции Jackson 2 → 3 и Spring Boot 3 → 4?

Миграция — это изменение API **и поведения JSON**, а не только номера зависимости. Jackson 3 требует Java 17 и переносит основные Maven group ID и Java-пакеты с `com.fasterxml.jackson` на `tools.jackson`. Исключение — `jackson-annotations`: его пакет `com.fasterxml.jackson.annotation` остаётся; аннотации из `jackson-databind`, например `@JsonSerialize`, переезжают вместе с databind. Версии модулей согласуют через BOM или управление зависимостями Spring Boot.

Проверять стоит четыре слоя:

1. **Код и зависимости.** Удалённые deprecated API и переименованные типы потребуют правок. Для JSON Jackson 3 предпочитает `JsonMapper.builder()`: mapper после создания неизменяем, его настраивают builder-ом или создают новую версию через `rebuild()`. Модули для `java.time`, `Optional` и имён параметров встроены; прежнюю ручную регистрацию и зависимости пересматривают. `JsonProcessingException` заменён на `JacksonException`, исключения Jackson стали unchecked — проверить `catch` и обработчики ошибок.
2. **JSON-контракт.** Среди изменившихся умолчаний — сортировка свойств по имени, запись дат как ISO-строк вместо timestamp, включённый `FAIL_ON_TRAILING_TOKENS`, выключенный `FAIL_ON_UNKNOWN_PROPERTIES` и включённый `FAIL_ON_NULL_FOR_PRIMITIVES`. Сравнить эталонные JSON-ответы, чтение старых payload и сохранённых документов. Отдельно проверить кастомные сериализаторы, полиморфизм и код, полагающийся на порядок полей.
3. **Spring Boot.** В Boot 4.1 Jackson 3 и `JsonMapper` — default. Для настроек остаются `spring.jackson.*` и `JsonMapperBuilderCustomizer`; старый `Jackson2ObjectMapperBuilderCustomizer` применяется к deprecated-поддержке Jackson 2. `spring.jackson.use-jackson2-defaults=true` помогает временно приблизить умолчания, но не исправляет несовместимые API. Если оба поколения на classpath, проверить выбранный JSON mapper для MVC/WebFlux и сторонних библиотек; переходный Jackson 2 использует `spring-boot-jackson2` и `spring.jackson2.*`.
4. **Приёмка.** Прогнать контрактные тесты сериализации/десериализации, тесты HTTP-ответов и чтение реальных старых данных. Исправлять конкретные отличия через настройки builder-а; не отключать новые проверки формата вслепую ради зелёных тестов.

Например, в Boot 4 настройку `JsonMapper` можно добавить, сохранив автонастройку:

```java
@Bean
JsonMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder.enable(SerializationFeature.INDENT_OUTPUT);
}
```

**Сценарий со смешанным classpath.** После перехода на Jackson 3 контрактный тест показывает, что поле стало сериализоваться обычным способом. Проверьте импорт `@JsonSerialize`: если Jackson 2 остался транзитивной зависимостью, старый импорт `com.fasterxml.jackson.databind.annotation.JsonSerialize` по-прежнему компилируется, но mapper Jackson 3 эту аннотацию игнорирует. Для Jackson 3 замените её на `tools.jackson.databind.annotation.JsonSerialize` и обновите сам сериализатор под API 3.x. Импорт `@JsonProperty` из `com.fasterxml.jackson.annotation` сохраняется. Повторите тест через тот mapper, которым приложение формирует HTTP-ответ. [Руководство Jackson 3 по миграции](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md#java-package-name-change)

**Итог:** сначала зафиксируйте JSON-контракт и зависимые библиотеки, затем переносите код на Jackson 3 и проверяйте реальные изменения поведения. Версии Boot 3/Jackson 2 и Boot 4/Jackson 3 нельзя описывать одним набором настроек `ObjectMapper`.

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
