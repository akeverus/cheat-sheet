---
title: "Вопросы на собеседовании: MapStruct"
description: "MapStruct — compile-time маппинг Java Bean: @Mapper, @Mapping, nested, collections, @MappingTarget, Spring, Lombok, тестирование"
tags:
  - interview
  - java
  - java-mapstruct-interview
aliases:
  - "MapStruct interview"
  - "MapStruct собеседование"
  - "MapStruct вопросы"
  - "Java bean mapping interview"
difficulty: "intermediate"
updated: "2026-04-20"
---
# Вопросы на собеседовании: `MapStruct`

`MapStruct` — аннотационный процессор, генерирующий реализации маппинга между Java Bean на этапе компиляции. В отличие от рефлексивных маппинг-фреймворков, код получается быстрым, явным и тип-безопасным.

## Полезные ссылки

### Официальная документация

- [MapStruct Reference Guide](https://mapstruct.org/documentation/stable/reference/html/) — полная документация
- [MapStruct GitHub](https://github.com/mapstruct/mapstruct) — исходный код и примеры

### Baeldung tutorials

- [Quick Guide to MapStruct](https://www.baeldung.com/mapstruct) — основы
- [Custom Mapper with MapStruct](https://www.baeldung.com/mapstruct-custom-mapper)
- [Nested Mapping](https://www.baeldung.com/java-mapstruct-nested-mapping)
- [Mapping Collections](https://www.baeldung.com/java-mapstruct-mapping-collections)
- [Using MapStruct With Lombok](https://www.baeldung.com/java-mapstruct-lombok)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое MapStruct и как он работает?](#q1-что-такое-mapstruct-и-как-он-работает)
- [Q2. Как подключить MapStruct к Maven/Gradle-проекту?](#q2-как-подключить-mapstruct-к-mavengradle-проекту)
- [Q3. (!) Чем MapStruct отличается от ModelMapper и Dozer?](#q3-чем-mapstruct-отличается-от-modelmapper-и-dozer)
- [Q4. Что такое `@Mapper` и какие параметры он принимает?](#q4-что-такое-mapper-и-какие-параметры-он-принимает)
- [Q5. Что такое `componentModel` в `@Mapper`?](#q5-что-такое-componentmodel-в-mapper)

**Маппинг полей**
- [Q6. (!) Как маппить поля с разными именами?](#q6-как-маппить-поля-с-разными-именами)
- [Q7. Как игнорировать поле при маппинге?](#q7-как-игнорировать-поле-при-маппинге)
- [Q8. Как задать константу или значение по умолчанию?](#q8-как-задать-константу-или-значение-по-умолчанию)
- [Q9. Как использовать Java-выражение в маппинге?](#q9-как-использовать-java-выражение-в-маппинге)
- [Q10. Как замаппить вложенные объекты (nested mapping)?](#q10-как-замаппить-вложенные-объекты-nested-mapping)

**Коллекции и enum**
- [Q11. Как маппить коллекции (List, Set, Map)?](#q11-как-маппить-коллекции-list-set-map)
- [Q12. Что такое `@IterableMapping` и когда он нужен?](#q12-что-такое-iterablemapping-и-когда-он-нужен)
- [Q13. Как маппить enum?](#q13-как-маппить-enum)

**Продвинутые возможности**
- [Q14. (!) Как маппить несколько source-объектов в один target?](#q14-как-маппить-несколько-source-объектов-в-один-target)
- [Q15. Что такое `@MappingTarget` и зачем он нужен?](#q15-что-такое-mappingtarget-и-зачем-он-нужен)
- [Q16. Что такое `@InheritConfiguration` и `@InheritInverseConfiguration`?](#q16-что-такое-inheritconfiguration-и-inheritinverseconfiguration)
- [Q17. Как использовать `@BeforeMapping` и `@AfterMapping`?](#q17-как-использовать-beforemapping-и-aftermapping)
- [Q18. Что такое `@BeanMapping` и `NullValuePropertyMappingStrategy`?](#q18-что-такое-beanmapping-и-nullvaluepropertymappingstrategy)
- [Q19. Как подключить внешний / custom mapper через `uses`?](#q19-как-подключить-внешний--custom-mapper-через-uses)
- [Q20. Что такое `@Qualifier` и `@Named` в MapStruct?](#q20-что-такое-qualifier-и-named-в-mapstruct)
- [Q21. Что такое Decorator-паттерн в MapStruct?](#q21-что-такое-decorator-паттерн-в-mapstruct)
- [Q22. Как работает маппинг в абстрактном классе вместо интерфейса?](#q22-как-работает-маппинг-в-абстрактном-классе-вместо-интерфейса)

**Интеграция и инфраструктура**
- [Q23. (!) Как интегрировать MapStruct со Spring?](#q23-как-интегрировать-mapstruct-со-spring)
- [Q24. (!) Какие проблемы возникают при использовании MapStruct вместе с Lombok?](#q24-какие-проблемы-возникают-при-использовании-mapstruct-вместе-с-lombok)
- [Q25. Как MapStruct поддерживает Lombok `@Builder`?](#q25-как-mapstruct-поддерживает-lombok-builder)
- [Q26. Как тестировать MapStruct-маппер?](#q26-как-тестировать-mapstruct-маппер)
- [Q27. Какие стратегии обработки `null` есть в MapStruct?](#q27-какие-стратегии-обработки-null-есть-в-mapstruct)
- [Q28. Как настроить маппинг с условием (conditional mapping)?](#q28-как-настроить-маппинг-с-условием-conditional-mapping)

---

## Q1. Что такое MapStruct и как он работает?

`MapStruct` — аннотационный процессор Java (JSR 269), работающий во время компиляции (`javac -processorpath`). Разработчик объявляет интерфейс-маппер с нужными методами, MapStruct генерирует реализацию в виде обычного `.java`-файла в `target/generated-sources/`.

**Механизм работы:**

```
Разработчик пишет интерфейс → javac вызывает процессор → MapStruct генерирует реализацию
```

```java
@Mapper
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}

// MapStruct сгенерирует что-то вроде:
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
```

**Ключевые преимущества:**
- Нет рефлексии → код такой же быстрый, как написанный вручную
- Ошибки маппинга видны на этапе компиляции
- Сгенерированный код читаем и отлаживаем
- Поддержка IDE (автодополнение, навигация)

**Итог:** MapStruct = compile-time code generation + zero-overhead при runtime.

## Q2. Как подключить MapStruct к Maven/Gradle-проекту?

**Maven:**

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.0</version>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    implementation("org.mapstruct:mapstruct:1.6.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")
}
```

Важно: MapStruct делится на две jar: `mapstruct` (аннотации и runtime-интерфейсы) и `mapstruct-processor` (annotation processor, нужен только во время компиляции).

## Q3. Чем MapStruct отличается от ModelMapper и Dozer?

| Критерий | MapStruct | ModelMapper | Dozer |
|---|---|---|---|
| Как работает | Compile-time codegen | Runtime рефлексия | Runtime рефлексия |
| Производительность | Как ручной код | Медленнее (рефлексия) | Медленнее |
| Обнаружение ошибок | Compile-time | Runtime | Runtime |
| Прозрачность | Сгенерированный код читаем | «Магия» внутри | XML конфиг |
| Размер проекта/deps | Только during compile | Runtime jar | Runtime jar |
| Кастомизация | Аннотации, `uses` | Converter, PropertyMap | XML / код |
| Поддержка Lombok | С настройкой процессора | Нет | Нет |

**Вывод:** MapStruct — де-факто стандарт в современных Java/Spring проектах. ModelMapper проще стартовать, но хуже в production (ошибки runtime, медленнее).

## Q4. Что такое `@Mapper` и какие параметры он принимает?

`@Mapper` — маркер для MapStruct, указывающий, что интерфейс/класс нужно обработать и сгенерировать реализацию.

Основные параметры:

```java
@Mapper(
    componentModel = "spring",          // как создавать экземпляр: "default", "spring", "cdi", "jakarta"
    uses = {AddressMapper.class},       // вспомогательные маппинг-классы
    imports = {UUID.class},             // импорты для expression/defaultExpression
    unmappedTargetPolicy = ReportingPolicy.ERROR,  // что делать с незамапленными target-полями
    unmappedSourcePolicy = ReportingPolicy.WARN,   // что делать с незамапленными source-полями
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface OrderMapper {
    OrderDto toDto(Order order);
}
```

`unmappedTargetPolicy = ERROR` — хорошая практика: компиляция упадёт, если в target-объекте есть поле, которое не замаппено. Избегает «тихих» потерь данных.

## Q5. Что такое `componentModel` в `@Mapper`?

`componentModel` определяет, как MapStruct создаёт экземпляр mapper и как его можно получить.

| Значение | Поведение |
|---|---|
| `"default"` | Mapper.getMapper(MyMapper.class) или синглтон INSTANCE |
| `"spring"` | Генерирует `@Component`-бин, можно инжектить через `@Autowired` / конструктор |
| `"cdi"` | Генерирует `@ApplicationScoped`-бин для CDI |
| `"jakarta"` | Аналогично cdi, но для Jakarta CDI |

```java
// с componentModel = "spring"
@Mapper(componentModel = "spring")
public interface UserMapper { ... }

// использование в Spring-сервисе
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
}
```

Альтернатива глобальному `componentModel` — задать в конфиге компилятора через `-Amapstruct.defaultComponentModel=spring`, тогда он применяется ко всем маппинг-интерфейсам.

## Q6. Как маппить поля с разными именами?

`@Mapping(source = "sourceField", target = "targetField")`:

```java
@Mapper
public interface PersonMapper {

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "birthDate", target = "dob")
    PersonDto toDto(Person person);
}
```

Если имена совпадают — MapStruct маппит автоматически (case-sensitive). Если тип разный, но конвертация очевидная (int→long, String→int) — тоже автоматически. Иначе нужен кастомный маппинг через `expression` или `uses`.

## Q7. Как игнорировать поле при маппинге?

```java
@Mapping(target = "internalId", ignore = true)
@Mapping(target = "createdAt", ignore = true)
PersonDto toDto(Person person);
```

Полезно, когда в target есть служебные поля (`id`, `version`, `createdAt`), которые не должны приходить из source. Без `ignore = true` при `unmappedTargetPolicy = ERROR` компиляция упадёт.

Альтернатива — `@BeanMapping(ignoreByDefault = true)` + явно перечислить только нужные поля:

```java
@BeanMapping(ignoreByDefault = true)
@Mapping(source = "name", target = "name")
@Mapping(source = "email", target = "email")
PersonDto toDtoPartial(Person person);
```

## Q8. Как задать константу или значение по умолчанию?

```java
@Mapping(target = "type", constant = "USER")           // всегда "USER"
@Mapping(target = "active", constant = "true")          // boolean из строки
@Mapping(target = "role", defaultValue = "READER")     // если source.role == null → "READER"
@Mapping(target = "score", defaultExpression = "java(0)")  // Java-выражение как default
PersonDto toDto(Person person);
```

- `constant` — всегда подставляет значение, игнорирует source
- `defaultValue` — подставляет только если source-поле равно `null`
- `defaultExpression` — вычисляемое Java-выражение как дефолт

## Q9. Как использовать Java-выражение в маппинге?

`expression = "java(...)"` позволяет написать произвольный Java-код:

```java
@Mapper(imports = {UUID.class, Instant.class})
public interface OrderMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "fullName", expression = "java(person.getFirstName() + ' ' + person.getLastName())")
    OrderDto toDto(Order order, Person person);
}
```

Классы в `expression` нужно импортировать через `@Mapper(imports = {...})`. Злоупотреблять не стоит — сложную логику лучше выносить в кастомные методы.

## Q10. Как замаппить вложенные объекты (nested mapping)?

**Вариант 1 — автоматический**: MapStruct видит `AddressDto` и `Address`, ищет маппинг (нужно добавить метод или `uses`):

```java
@Mapper(uses = AddressMapper.class)
public interface UserMapper {
    UserDto toDto(User user); // address → addressDto через AddressMapper
}
```

**Вариант 2 — явный через dot-notation**:

```java
@Mapping(source = "address.city", target = "city")
@Mapping(source = "address.zip", target = "postalCode")
UserFlatDto toFlatDto(User user);
```

**Вариант 3 — в самом методе mapper** (abstract class):

```java
@Mapper
public abstract class UserMapper {
    public UserDto toDto(User user) {
        // вызов другого метода
    }
    protected abstract AddressDto toAddressDto(Address address);
}
```

## Q11. Как маппить коллекции (List, Set, Map)?

MapStruct автоматически генерирует маппинг коллекций, если есть маппинг элементов:

```java
@Mapper
public interface OrderMapper {
    OrderDto toDto(Order order);
    List<OrderDto> toDtoList(List<Order> orders);  // генерируется автоматически
    Set<OrderDto> toDtoSet(Set<Order> orders);
}
```

Сгенерированный код итерирует коллекцию и вызывает `toDto()` для каждого элемента.

Для `Map`:

```java
Map<String, OrderDto> toDtoMap(Map<String, Order> map);
```

**Важно:** MapStruct не поддерживает `Map<Entity, Dto>` с преобразованием ключа из пользовательского типа автоматически — нужен custom метод.

## Q12. Что такое `@IterableMapping` и когда он нужен?

`@IterableMapping` нужен, когда одна коллекция должна быть конвертирована в другую нестандартным образом — например, когда есть несколько возможных методов конверсии элемента:

```java
@Mapper
public interface StringMapper {

    @Named("toUpperCase")
    default String toUpperCase(String s) { return s.toUpperCase(); }

    @Named("toLowerCase")
    default String toLowerCase(String s) { return s.toLowerCase(); }

    @IterableMapping(qualifiedByName = "toUpperCase")
    List<String> toUpperCaseList(List<String> input);

    @IterableMapping(qualifiedByName = "toLowerCase")
    List<String> toLowerCaseList(List<String> input);
}
```

Без `@IterableMapping` MapStruct не знает, какой именно метод выбрать из нескольких.

## Q13. Как маппить enum?

По умолчанию MapStruct маппит enum по имени:

```java
@Mapper
public interface StatusMapper {
    OrderStatusDto toDto(OrderStatus status);
}
// CREATED → CREATED, PAID → PAID и т.д.
```

Для кастомного маппинга:

```java
@Mapper
public interface StatusMapper {

    @ValueMappings({
        @ValueMapping(source = "CANCELLED", target = "REJECTED"),
        @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "UNKNOWN")
    })
    OrderStatusDto toDto(OrderStatus status);
}
```

- `MappingConstants.ANY_REMAINING` — все неперечисленные значения
- `MappingConstants.NULL` — маппинг null
- `MappingConstants.ANY_UNMAPPED` — только немаппированные (без тех, что явно указаны)

## Q14. Как маппить несколько source-объектов в один target?

Один target-метод может принимать несколько source-параметров:

```java
@Mapper
public interface DeliveryMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.status", target = "status")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "customer.name", target = "customerName")
    DeliveryDto toDto(Order order, Address address, Customer customer);
}
```

При конфликте имён полей (например, `order.name` и `customer.name`) MapStruct выдаст ошибку компиляции — нужно явно указать `source` для каждого поля.

## Q15. Что такое `@MappingTarget` и зачем он нужен?

`@MappingTarget` указывает, что параметр — это существующий объект, который нужно **обновить**, а не создавать новый:

```java
@Mapper
public interface UserMapper {

    // Обновляет существующий userDto из user
    void updateDto(User user, @MappingTarget UserDto dto);

    // Обновляет существующий entity из dto (patch-операции)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(UserUpdateRequest request, @MappingTarget User user);
}
```

`partialUpdate` с `IGNORE` — типичный паттерн для PATCH-эндпоинтов: обновляем только non-null поля из request, остальные оставляем нетронутыми.

## Q16. Что такое `@InheritConfiguration` и `@InheritInverseConfiguration`?

Позволяют переиспользовать настройки `@Mapping` из другого метода, не дублируя их.

```java
@Mapper
public interface ProductMapper {

    @Mapping(source = "name", target = "title")
    @Mapping(source = "categoryId", target = "category.id")
    ProductDto toDto(Product product);

    // Наследует настройки toDto(), применяя их в обратную сторону
    @InheritInverseConfiguration(name = "toDto")
    Product toEntity(ProductDto dto);

    // Обновление через @MappingTarget с теми же маппингами
    @InheritConfiguration(name = "toDto")
    void updateFromDto(Product source, @MappingTarget ProductDto target);
}
```

`@InheritInverseConfiguration` — особенно полезен когда маппинг двунаправленный: не нужно дублировать каждый `@Mapping` в обратную сторону.

## Q17. Как использовать `@BeforeMapping` и `@AfterMapping`?

Хуки для выполнения логики до/после генерированного маппинга:

```java
@Mapper
public abstract class UserMapper {

    @BeforeMapping
    protected void validate(User user) {
        if (user.getEmail() == null) throw new IllegalArgumentException("email required");
    }

    @AfterMapping
    protected void setAuditFields(@MappingTarget UserDto dto) {
        dto.setMappedAt(Instant.now());
    }

    public abstract UserDto toDto(User user);
}
```

Работает только в абстрактных классах или через `@Mapper(uses = ...)` с отдельным классом хуков. Нельзя использовать в чистых интерфейсах (только `default`-методы).

`@MappingTarget` в параметре `@AfterMapping` — позволяет модифицировать уже заполненный target.

## Q18. Что такое `@BeanMapping` и `NullValuePropertyMappingStrategy`?

`@BeanMapping` — метауровень настройки для одного метода маппинга:

```java
@BeanMapping(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, // null source → пустой target
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, // null поле → не трогать
    ignoreByDefault = true  // не маппить никакие поля без явного @Mapping
)
void partialUpdate(UserPatchRequest request, @MappingTarget User user);
```

**Стратегии `NullValuePropertyMappingStrategy`:**

| Значение | Поведение |
|---|---|
| `SET_TO_NULL` (default) | null source → target.setField(null) |
| `IGNORE` | null source → target-поле не трогаем |
| `SET_TO_DEFAULT` | null source → устанавливаем default (0, "", пустую коллекцию) |

`IGNORE` — ключевой паттерн для PATCH-операций.

## Q19. Как подключить внешний / custom mapper через `uses`?

`uses` позволяет MapStruct использовать другой mapper или обычный конвертер при маппинге:

```java
// Кастомный конвертер
public class MoneyConverter {
    public BigDecimal fromString(String s) { return new BigDecimal(s); }
    public String toString(BigDecimal d) { return d.toPlainString(); }
}

// Mapper-зависимость
@Mapper
public interface AddressMapper {
    AddressDto toDto(Address address);
}

// Использование обоих
@Mapper(uses = {AddressMapper.class, MoneyConverter.class})
public interface OrderMapper {
    OrderDto toDto(Order order); // автоматически использует AddressMapper для address → addressDto
}
```

MapStruct ищет подходящий метод в `uses`-классах по сигнатуре (тип параметра / тип возврата).

## Q20. Что такое `@Qualifier` и `@Named` в MapStruct?

Когда несколько методов могут конвертировать один тип в другой, `@Qualifier`/`@Named` помогает MapStruct выбрать нужный:

```java
@Mapper
public interface PriceMapper {

    @Named("withTax")
    default BigDecimal convertWithTax(BigDecimal price) {
        return price.multiply(new BigDecimal("1.2"));
    }

    @Named("withoutTax")
    default BigDecimal convertWithoutTax(BigDecimal price) {
        return price;
    }

    @Mapping(source = "basePrice", target = "displayPrice", qualifiedByName = "withTax")
    PriceDto toDto(Product product);
}
```

Можно также создать собственную аннотацию-квалификатор:

```java
@Qualifier
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface WithTax {}
```

## Q21. Что такое Decorator-паттерн в MapStruct?

`@DecoratedWith` позволяет добавить поведение поверх сгенерированного маппера, не теряя его реализацию:

```java
@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
    UserDto toDto(User user);
}

public abstract class UserMapperDecorator implements UserMapper {
    @Autowired
    @Delegate   // делегирует все методы в сгенерированный маппер
    private UserMapper delegate;

    @Override
    public UserDto toDto(User user) {
        UserDto dto = delegate.toDto(user);
        // дополнительная логика
        dto.setAvatarUrl(buildAvatarUrl(user.getId()));
        return dto;
    }
}
```

Полезен, когда нужно обогатить маппинг данными из сервисов (например, загрузить URL картинки), не ломая чистоту декларативного маппинга.

## Q22. Как работает маппинг в абстрактном классе вместо интерфейса?

Можно объявить маппер как `abstract class` — это даёт возможность иметь состояние, инжектить зависимости и писать произвольную логику:

```java
@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private RoleService roleService;  // Spring-зависимость

    public abstract UserDto toDto(User user);

    @AfterMapping
    protected void enrichWithRoles(User user, @MappingTarget UserDto dto) {
        dto.setRoles(roleService.findRolesForUser(user.getId()));
    }
}
```

Ограничение интерфейсов: нельзя инжектировать зависимости (Spring не управляет интерфейсом напрямую). В абстрактных классах MapStruct генерирует подкласс, который Spring регистрирует как бин.

## Q23. Как интегрировать MapStruct со Spring?

1. Добавить `componentModel = "spring"` в `@Mapper`
2. MapStruct сгенерирует реализацию с `@Component`
3. Использовать через конструкторную инъекцию или `@Autowired`

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
}

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;  // Spring инжектит автоматически

    public ProductDto getProduct(Long id) {
        return productMapper.toDto(repository.findById(id).orElseThrow());
    }
}
```

**Глобальная конфигурация** (Gradle/Maven): `-Amapstruct.defaultComponentModel=spring` — применяется ко всем маппинг-интерфейсам без необходимости указывать в каждом `@Mapper`.

## Q24. Какие проблемы возникают при использовании MapStruct вместе с Lombok?

Самая распространённая проблема: MapStruct выполняется **до** Lombok, поэтому геттеры/сеттеры, генерируемые Lombok, ещё не существуют когда MapStruct пытается построить маппинг.

**Симптом:** `No property named "fieldName" exists in source parameter...` при наличии `@Getter`/`@Setter`.

**Решение (Maven):** указать порядок annotation processors — Lombok первым:

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
    </path>
</annotationProcessorPaths>
```

**Решение (Gradle):**

```kotlin
annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
// Lombok должен быть ПЕРВЫМ
```

Также нужна зависимость `lombok-mapstruct-binding` для корректной интеграции:

```kotlin
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
```

## Q25. Как MapStruct поддерживает Lombok `@Builder`?

MapStruct умеет работать с builder-паттерном Lombok начиная с версии 1.4. По умолчанию предпочитает builder, если он есть:

```java
@Value  // Lombok: immutable + @Builder
@Builder
public class UserDto {
    String name;
    String email;
}

// MapStruct автоматически использует builder:
// UserDto.builder().name(...).email(...).build()
```

Если builder называется нестандартно:

```java
@BeanMapping(builder = @Builder(buildMethod = "create"))
UserDto toDto(User user);
```

Для `@SuperBuilder` (наследование) нужна дополнительная настройка через `BuilderProvider`.

## Q26. Как тестировать MapStruct-маппер?

**Вариант 1 — plain unit test (без Spring):**

```java
class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_mapsAllFields() {
        User user = new User(1L, "Alice", "alice@mail.com");
        UserDto dto = mapper.toDto(user);

        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    void toDto_nullInput_returnsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }
}
```

`Mappers.getMapper()` создаёт экземпляр без Spring-контекста.

**Вариант 2 — Spring slice test:**

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class})
class UserMapperSpringTest {
    @Autowired UserMapper mapper;
    // тесты...
}
```

**Что тестировать:**
- маппинг каждого поля
- обработка `null` (на уровне метода и полей)
- кастомные конверсии
- partial update (PATCH-сценарии)

## Q27. Какие стратегии обработки `null` есть в MapStruct?

**`NullValueMappingStrategy`** — что делать если весь source == null:

| Значение | Поведение |
|---|---|
| `RETURN_NULL` (default) | вернуть `null` |
| `RETURN_DEFAULT` | вернуть пустой объект (new UserDto()) |

**`NullValuePropertyMappingStrategy`** — что делать если отдельное поле source == null:

| Значение | Поведение |
|---|---|
| `SET_TO_NULL` (default) | target.setField(null) |
| `IGNORE` | не трогать target-поле |
| `SET_TO_DEFAULT` | установить default (0, "", Collections.empty()) |

**`NullValueCheckStrategy`** — когда проверять null перед конверсией:

| Значение | Поведение |
|---|---|
| `ON_IMPLICIT_CONVERSION` (default) | проверка только при неявных конверсиях |
| `ALWAYS` | всегда добавлять null-check |

## Q28. Как настроить маппинг с условием (conditional mapping)?

Начиная с MapStruct 1.5, можно использовать `@Condition`:

```java
@Mapper
public interface UserMapper {

    @Condition
    default boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    // поля типа String будут замаппены только если isNotBlank(source) == true
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
```

До 1.5 использовали `conditionExpression`:

```java
@Mapping(
    target = "phone",
    source = "phone",
    conditionExpression = "java(source.getPhone() != null && !source.getPhone().isBlank())"
)
UserDto toDto(User user);
```

`@Condition` — более чистый способ: условие задаётся один раз и применяется ко всем полям нужного типа.

---

## See also

- [Java Annotations](java-annotations-interview.md) — `@Mapper`, `@Mapping` реализованы как аннотации APT; понимание JSR 269
- [Java Generics](java-generics-interview.md) — `TypeReference`, generic-конверсии в MapStruct
- [Java Core](java-core-interview.md) — основы Bean-паттерна: геттеры/сеттеры, equals/hashCode
- [Jackson](java-jackson-interview.md) — альтернативный подход к DTO-маппингу через сериализацию
- [Java Initialization](java-initialization-interview.md) — инициализация, static fields, Builder-паттерн
- [Spring Data JPA](../../frameworks/spring/spring-data-jpa-interview.md) — маппинг Entity→DTO через MapStruct в persistence-слое
- [Spring MVC](../../frameworks/spring/spring-mvc-interview.md) — типичный use case: контроллер получает DTO, сервис использует mapper
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — интеграция MapStruct как Spring-компонента, автоконфигурация
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Decorator-паттерн в `@DecoratedWith`, Builder-паттерн
- [Unit Testing](../../testing/unit-testing-interview.md) — тестирование маппинг-логики через `Mappers.getMapper()`
- [Шпаргалка: MapStruct: Маппинг объектов в Java](../../../libraries/java/java-mapstruct.md) — теория
