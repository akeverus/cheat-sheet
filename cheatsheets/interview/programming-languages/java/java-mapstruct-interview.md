---
title: "Вопросы на собеседовании: MapStruct"
description: "MapStruct — compile-time маппинг Java Bean: @Mapper, @Mapping, nested, collections, @MappingTarget, Spring, Lombok, тестирование"
tags:
  - interview
  - java
  - java-mapstruct-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "MapStruct"
  - "MapStruct interview"
  - "MapStruct собеседование"
prerequisites:
  - "[[java-mapstruct]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `MapStruct`

`MapStruct` — annotation processor, который на этапе компиляции генерирует реализации маппинга между Java Bean. В отличие от рефлексивных фреймворков, код получается быстрым (без оверхеда рантайма), явным и типобезопасным — ошибки видны ещё при сборке.

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
- [Q1. (!) Что такое MapStruct и как он работает?](#q1--что-такое-mapstruct-и-как-он-работает)
- [Q2. Как подключить MapStruct к Maven/Gradle-проекту?](#q2-как-подключить-mapstruct-к-mavengradle-проекту)
- [Q3. (!) Чем MapStruct отличается от ModelMapper и Dozer?](#q3--чем-mapstruct-отличается-от-modelmapper-и-dozer)
- [Q4. Что такое `@Mapper` и какие параметры он принимает?](#q4-что-такое-mapper-и-какие-параметры-он-принимает)
- [Q5. Что такое `componentModel` в `@Mapper`?](#q5-что-такое-componentmodel-в-mapper)

**Маппинг полей**
- [Q6. (!) Как маппить поля с разными именами?](#q6--как-маппить-поля-с-разными-именами)
- [Q7. Как игнорировать поле при маппинге?](#q7-как-игнорировать-поле-при-маппинге)
- [Q8. Как задать константу или значение по умолчанию?](#q8-как-задать-константу-или-значение-по-умолчанию)
- [Q9. Как использовать Java-выражение в маппинге?](#q9-как-использовать-java-выражение-в-маппинге)
- [Q10. Как замаппить вложенные объекты (nested mapping)?](#q10-как-замаппить-вложенные-объекты-nested-mapping)

**Коллекции и enum**
- [Q11. Как маппить коллекции (List, Set, Map)?](#q11-как-маппить-коллекции-list-set-map)
- [Q12. Что такое `@IterableMapping` и когда он нужен?](#q12-что-такое-iterablemapping-и-когда-он-нужен)
- [Q13. Как маппить enum?](#q13-как-маппить-enum)

**Продвинутые возможности**
- [Q14. (!) Как маппить несколько source-объектов в один target?](#q14--как-маппить-несколько-source-объектов-в-один-target)
- [Q15. Что такое `@MappingTarget` и зачем он нужен?](#q15-что-такое-mappingtarget-и-зачем-он-нужен)
- [Q16. Что такое `@InheritConfiguration` и `@InheritInverseConfiguration`?](#q16-что-такое-inheritconfiguration-и-inheritinverseconfiguration)
- [Q17. Как использовать `@BeforeMapping` и `@AfterMapping`?](#q17-как-использовать-beforemapping-и-aftermapping)
- [Q18. Что такое `@BeanMapping` и `NullValuePropertyMappingStrategy`?](#q18-что-такое-beanmapping-и-nullvaluepropertymappingstrategy)
- [Q19. Как подключить внешний / custom mapper через `uses`?](#q19-как-подключить-внешний--custom-mapper-через-uses)
- [Q20. Что такое `@Qualifier` и `@Named` в MapStruct?](#q20-что-такое-qualifier-и-named-в-mapstruct)
- [Q21. Что такое Decorator-паттерн в MapStruct?](#q21-что-такое-decorator-паттерн-в-mapstruct)
- [Q22. Как работает маппинг в абстрактном классе вместо интерфейса?](#q22-как-работает-маппинг-в-абстрактном-классе-вместо-интерфейса)

**Интеграция и инфраструктура**
- [Q23. (!) Как интегрировать MapStruct со Spring?](#q23--как-интегрировать-mapstruct-со-spring)
- [Q24. (!) Какие проблемы возникают при использовании MapStruct вместе с Lombok?](#q24--какие-проблемы-возникают-при-использовании-mapstruct-вместе-с-lombok)
- [Q25. Как MapStruct поддерживает Lombok `@Builder`?](#q25-как-mapstruct-поддерживает-lombok-builder)
- [Q26. Как тестировать MapStruct-маппер?](#q26-как-тестировать-mapstruct-маппер)
- [Q27. Какие стратегии обработки `null` есть в MapStruct?](#q27-какие-стратегии-обработки-null-есть-в-mapstruct)
- [Q28. Как настроить маппинг с условием (conditional mapping)?](#q28-как-настроить-маппинг-с-условием-conditional-mapping)

---

## Q1. (!) Что такое MapStruct и как он работает?

`MapStruct` — это annotation processor (JSR 269), который во время компиляции генерирует код маппинга между Java Bean. Вместо рефлексии в рантайме он один раз, при сборке, пишет за вас обычный `.java`-класс с явными вызовами геттеров и сеттеров.

Разработчик объявляет интерфейс-маппер с нужными методами, а MapStruct в ходе компиляции (`javac -processorpath`) создаёт его реализацию — файл в `target/generated-sources/`, который компилируется вместе с остальным кодом.

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

**Почему это удобно:**
- Нет рефлексии → код такой же быстрый, как написанный вручную, без оверхеда в рантайме.
- Ошибки маппинга (несовпадение полей, отсутствие конвертера) ловятся на этапе компиляции, а не падают в проде.
- Сгенерированный код — обычный читаемый Java: его можно открыть, прочитать и поставить точку останова.
- Полная поддержка IDE: автодополнение, навигация, рефакторинг работают как с любым кодом.

**Итог:** MapStruct переносит маппинг с рантайма на этап компиляции — отсюда и скорость, и ранняя проверка ошибок.

## Q2. Как подключить MapStruct к Maven/Gradle-проекту?

Нужны две вещи: зависимость на runtime-библиотеку с аннотациями и подключение annotation processor к компилятору. Без процессора аннотации просто проигнорируются, и реализация маппера не сгенерируется.

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

**Ключевой момент:** MapStruct поставляется двумя jar. `mapstruct` (аннотации и runtime-интерфейсы) попадает в зависимости приложения, а `mapstruct-processor` (сам генератор кода) подключается только к компилятору — в рантайме он не нужен, поэтому его и кладут в `annotationProcessor`, а не в `implementation`.

## Q3. (!) Чем MapStruct отличается от ModelMapper и Dozer?

Главное различие в моменте работы: MapStruct генерирует код при компиляции, а ModelMapper и Dozer выполняют маппинг в рантайме через рефлексию. Отсюда вытекает всё остальное — скорость, момент обнаружения ошибок и прозрачность.

| Критерий | MapStruct | ModelMapper | Dozer |
|---|---|---|---|
| Как работает | Compile-time codegen | Runtime рефлексия | Runtime рефлексия |
| Производительность | Как ручной код | Медленнее (рефлексия) | Медленнее |
| Обнаружение ошибок | Compile-time | Runtime | Runtime |
| Прозрачность | Сгенерированный код читаем | «Магия» внутри | XML конфиг |
| Размер проекта/deps | Только during compile | Runtime jar | Runtime jar |
| Кастомизация | Аннотации, `uses` | Converter, PropertyMap | XML / код |
| Поддержка Lombok | С настройкой процессора | Нет | Нет |

**Вывод:** MapStruct — де-факто стандарт в современных Java/Spring проектах. У ModelMapper ниже порог входа (меньше настройки на старте), но в production он проигрывает: ошибки маппинга всплывают только в рантайме и сам он медленнее из-за рефлексии. Dozer практически вытеснен и считается legacy.

## Q4. Что такое `@Mapper` и какие параметры он принимает?

`@Mapper` помечает интерфейс или абстрактный класс как маппер: процессор находит эти типы и генерирует для них реализацию. Параметры аннотации настраивают поведение всего маппера сразу.

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

**Рекомендация:** ставьте `unmappedTargetPolicy = ERROR`. Тогда компиляция упадёт, если в target-объекте останется поле, которое никуда не замаппилось. Это защищает от «тихих» потерь данных — самой коварной ошибки в маппинге, когда новое поле в DTO добавили, а заполнить его забыли, и оно молча приходит `null`.

## Q5. Что такое `componentModel` в `@Mapper`?

`componentModel` определяет, как создаётся экземпляр маппера и как его потом получить в коде. По сути это выбор между «достать вручную» и «отдать управление DI-контейнеру».

| Значение | Как получить маппер |
|---|---|
| `"default"` | Вручную: `Mappers.getMapper(MyMapper.class)` или синглтон `INSTANCE` |
| `"spring"` | Генерирует `@Component`-бин — инжектится через `@Autowired` / конструктор |
| `"cdi"` | Генерирует `@ApplicationScoped`-бин для CDI |
| `"jakarta"` | То же, что `cdi`, но для Jakarta CDI |

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

Чтобы не повторять `componentModel = "spring"` в каждом маппере, задайте его глобально в опциях компилятора: `-Amapstruct.defaultComponentModel=spring`. Тогда значение применится ко всем маппинг-интерфейсам проекта.

## Q6. (!) Как маппить поля с разными именами?

Аннотацией `@Mapping(source = "...", target = "...")` на методе маппера — она явно связывает поле источника с полем результата:

```java
@Mapper
public interface PersonMapper {

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "birthDate", target = "dob")
    PersonDto toDto(Person person);
}
```

`@Mapping` нужен только для расхождений. Поля с одинаковыми именами MapStruct связывает сам (имена сравниваются с учётом регистра). Если типы разные, но конвертация очевидна (`int`→`long`, `String`→`int`) — он тоже справится автоматически. А вот для неочевидных преобразований придётся подсказать ему через `expression` или подключённый маппер в `uses`.

## Q7. Как игнорировать поле при маппинге?

Атрибутом `ignore = true` в `@Mapping` по конкретному target-полю:

```java
@Mapping(target = "internalId", ignore = true)
@Mapping(target = "createdAt", ignore = true)
PersonDto toDto(Person person);
```

**Сценарий применения:** в target есть служебные поля (`id`, `version`, `createdAt`), которые заполняются не из source, а где-то ещё (БД, аудит). Если включён `unmappedTargetPolicy = ERROR`, такие поля обязательно нужно либо замаппить, либо явно пометить `ignore = true` — иначе компиляция упадёт.

Когда игнорировать нужно почти всё, а маппить — пару полей, удобнее обратный подход: `@BeanMapping(ignoreByDefault = true)` отключает автомаппинг, и вы перечисляете только нужные поля.

```java
@BeanMapping(ignoreByDefault = true)
@Mapping(source = "name", target = "name")
@Mapping(source = "email", target = "email")
PersonDto toDtoPartial(Person person);
```

## Q8. Как задать константу или значение по умолчанию?

Через атрибуты `@Mapping`: `constant` (жёсткое значение), `defaultValue` или `defaultExpression` (значение-заглушка на случай `null`). Важно различать «всегда» и «только если пусто»:

```java
@Mapping(target = "type", constant = "USER")           // всегда "USER"
@Mapping(target = "active", constant = "true")          // boolean из строки
@Mapping(target = "role", defaultValue = "READER")     // если source.role == null → "READER"
@Mapping(target = "score", defaultExpression = "java(0)")  // Java-выражение как default
PersonDto toDto(Person person);
```

- `constant` — подставляет значение всегда, source при этом не читается вообще.
- `defaultValue` — берёт значение source, но если оно `null`, подставляет указанное (строковый литерал).
- `defaultExpression` — то же, что `defaultValue`, но дефолт вычисляется Java-выражением (когда литерала недостаточно).

## Q9. Как использовать Java-выражение в маппинге?

Атрибут `expression = "java(...)"` вставляет произвольный Java-код прямо в сгенерированный маппер — внутри скобок пишется любое валидное выражение, которое и станет значением target-поля:

```java
@Mapper(imports = {UUID.class, Instant.class})
public interface OrderMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "fullName", expression = "java(person.getFirstName() + ' ' + person.getLastName())")
    OrderDto toDto(Order order, Person person);
}
```

Классы, которые упоминаются в `expression`, нужно объявить в `@Mapper(imports = {...})` — иначе сгенерированный файл не скомпилируется.

**Подводный камень:** выражение — это просто строка, его не проверяет компилятор маппера и не подсвечивает IDE. Опечатку вы увидите только при сборке сгенерированного кода. Поэтому держите выражения короткими, а сложную логику выносите в обычные `default`-методы или классы в `uses`.

## Q10. Как замаппить вложенные объекты (nested mapping)?

Есть три подхода в зависимости от того, нужна ли вложенность в результате или вы её, наоборот, разворачиваете в плоскую структуру.

**Вариант 1 — автоматический (вложенность сохраняется)**: MapStruct видит у source поле типа `Address`, а у target — `AddressDto`, и сам подбирает маппер для этой пары (его надо предоставить методом или через `uses`):

```java
@Mapper(uses = AddressMapper.class)
public interface UserMapper {
    UserDto toDto(User user); // address → addressDto через AddressMapper
}
```

**Вариант 2 — разворачивание через dot-notation (flattening)**: когда вложенный объект нужно «расплющить» в поля верхнего уровня:

```java
@Mapping(source = "address.city", target = "city")
@Mapping(source = "address.zip", target = "postalCode")
UserFlatDto toFlatDto(User user);
```

**Вариант 3 — собственный метод-делегат в абстрактном классе**: когда логику вложенного маппинга нужно описать руками:

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

Достаточно объявить метод с коллекцией в сигнатуре — MapStruct сгенерирует его сам, если уже знает, как маппить один элемент:

```java
@Mapper
public interface OrderMapper {
    OrderDto toDto(Order order);
    List<OrderDto> toDtoList(List<Order> orders);  // генерируется автоматически
    Set<OrderDto> toDtoSet(Set<Order> orders);
}
```

Под капотом он просто создаёт новую коллекцию того же типа, проходит по элементам и вызывает для каждого `toDto()` — ровно то, что вы написали бы руками в цикле.

Для `Map` он маппит отдельно ключи и значения:

```java
Map<String, OrderDto> toDtoMap(Map<String, Order> map);
```

**Подводный камень:** если ключ — пользовательский тип, который тоже нужно преобразовать (`Map<Entity, Dto>`), автоматики не хватит — придётся писать собственный метод конверсии ключа.

## Q12. Что такое `@IterableMapping` и когда он нужен?

`@IterableMapping` управляет тем, как именно маппится элемент коллекции. Он нужен, когда для одного типа элемента есть несколько подходящих методов и MapStruct не может выбрать сам — тогда вы указываете нужный квалификатором `qualifiedByName`:

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

Здесь оба метода `String → String`, поэтому без `@IterableMapping` MapStruct увидел бы неоднозначность и упал с ошибкой компиляции. Помимо выбора метода, `@IterableMapping` умеет задавать и другие параметры элемента — например, формат даты или стратегию обработки `null`.

## Q13. Как маппить enum?

По умолчанию MapStruct связывает константы двух enum по совпадению имён (`CREATED` → `CREATED`), и для этого вообще не нужны аннотации — достаточно объявить метод:

```java
@Mapper
public interface StatusMapper {
    OrderStatusDto toDto(OrderStatus status);
}
// CREATED → CREATED, PAID → PAID и т.д.
```

Когда имена не совпадают, переопределяете отдельные значения через `@ValueMapping`:

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

Специальные источники для «catch-all»-правил:
- `MappingConstants.ANY_REMAINING` — все значения, не перечисленные явно (включая совпадающие по имени).
- `MappingConstants.ANY_UNMAPPED` — только те, что не нашли пару (значения, совпавшие по имени, остаются как есть).
- `MappingConstants.NULL` — позволяет задать, во что превращается `null`.

## Q14. (!) Как маппить несколько source-объектов в один target?

Объявите метод с несколькими source-параметрами, и MapStruct соберёт результат из всех них. Чтобы он понял, какое поле из какого параметра брать, в `source` указывается имя параметра через точку (`order.id`, `address.city`):

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

**Подводный камень:** если в разных параметрах есть одноимённые поля (`order.name` и `customer.name`), MapStruct не знает, какое из них взять, и падает с ошибкой компиляции. Лечится явным указанием полного пути в `source` для каждого спорного target-поля.

## Q15. Что такое `@MappingTarget` и зачем он нужен?

`@MappingTarget` помечает параметр как уже существующий объект, в который нужно записать данные — то есть метод **обновляет** переданный экземпляр, а не создаёт и возвращает новый. Такой метод обычно объявляют `void`:

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

**Сценарий применения:** `partialUpdate` со стратегией `IGNORE` — классический паттерн для PATCH-эндпоинтов. Из request переносятся только заполненные (non-null) поля, а те, что в запросе отсутствуют, остаются в сущности нетронутыми. Без `IGNORE` пустые поля запроса затёрли бы данные в `null`.

## Q16. Что такое `@InheritConfiguration` и `@InheritInverseConfiguration`?

Обе аннотации переиспользуют набор `@Mapping` из другого метода, чтобы не копировать их вручную. `@InheritConfiguration` берёт настройки как есть (для родственного метода в ту же сторону), а `@InheritInverseConfiguration` разворачивает их `source` и `target` местами — для обратного маппинга.

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

**Сценарий применения:** двунаправленный маппинг Entity ↔ DTO. Описываете правила один раз в `toDto()`, а `toEntity()` получает их автоматически в обратную сторону — меньше дублирования и ниже риск рассинхронизации правил.

## Q17. Как использовать `@BeforeMapping` и `@AfterMapping`?

Это хуки жизненного цикла: метод с `@BeforeMapping` вызывается до основного маппинга, с `@AfterMapping` — сразу после. MapStruct встраивает их вызовы в сгенерированный код. Удобно для валидации входа и пост-обработки результата:

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

**Ограничение:** хуки нужно где-то реализовать телом метода, а в чистом интерфейсе обычные методы тела не имеют. Поэтому их размещают в абстрактном классе-маппере, в его `default`-методах либо в отдельном классе, подключённом через `@Mapper(uses = ...)`.

Параметр `@MappingTarget` в `@AfterMapping` даёт доступ к уже заполненному результату — именно так в него дописывают вычисляемые или аудит-поля.

## Q18. Что такое `@BeanMapping` и `NullValuePropertyMappingStrategy`?

`@BeanMapping` настраивает поведение одного конкретного метода маппинга целиком (тогда как `@Mapping` работает на уровне отдельного поля). Через неё задают стратегии обработки `null`, отключают автомаппинг и выбирают builder:

```java
@BeanMapping(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, // null source → пустой target
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, // null поле → не трогать
    ignoreByDefault = true  // не маппить никакие поля без явного @Mapping
)
void partialUpdate(UserPatchRequest request, @MappingTarget User user);
```

**Стратегии `NullValuePropertyMappingStrategy`** (что делать, если отдельное source-поле равно `null`):

| Значение | Поведение |
|---|---|
| `SET_TO_NULL` (default) | Записать `null` в target: `target.setField(null)` |
| `IGNORE` | Не трогать target-поле — сохранить его прежнее значение |
| `SET_TO_DEFAULT` | Поставить значение по умолчанию (`0`, `""`, пустую коллекцию) |

**Эмпирическое правило:** для PATCH-операций над существующей сущностью берите `IGNORE` — иначе незаполненные поля запроса затрут уже сохранённые данные.

## Q19. Как подключить внешний / custom mapper через `uses`?

Параметр `uses` в `@Mapper` подключает другие мапперы или обычные классы-конвертеры. Когда основной маппер встречает поле, которое не умеет преобразовать сам, он ищет подходящий метод в подключённых классах и переиспользует его:

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

Выбор метода идёт по сигнатуре — типу параметра и типу возвращаемого значения. Поэтому никаких аннотаций на сам конвертер вешать не нужно: достаточно, чтобы метод принимал нужный source-тип и возвращал нужный target-тип.

## Q20. Что такое `@Qualifier` и `@Named` в MapStruct?

Это механизм разрешения неоднозначности. Если один и тот же тип можно преобразовать несколькими методами, MapStruct не угадает нужный — вы помечаете методы именами (`@Named("...")`) и в `@Mapping` указываете, какой брать, через `qualifiedByName`:

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

`@Named` — это готовый квалификатор по строковому имени. Если хочется типобезопасности вместо строк, можно объявить собственную аннотацию, помеченную `@Qualifier`, и ссылаться на неё через `qualifiedBy`:

```java
@Qualifier
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface WithTax {}
```

## Q21. Что такое Decorator-паттерн в MapStruct?

Это способ обернуть сгенерированный маппер своим классом и дописать логику, не вмешиваясь в генерацию. Аннотация `@DecoratedWith` указывает класс-декоратор, который вызывает оригинальный маппер через делегат, а вокруг этого вызова добавляет нужное поведение:

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

**Сценарий применения:** обогащение результата данными из сторонних сервисов (например, собрать URL аватара по id), когда такую логику не выразить декларативными `@Mapping`. Простой маппинг полей остаётся в чистом интерфейсе и генерируется автоматически, а вся «грязная» логика изолирована в декораторе.

## Q22. Как работает маппинг в абстрактном классе вместо интерфейса?

Если объявить маппер как `abstract class`, у него появляются возможности обычного класса: поля-состояние, внедрённые зависимости и собственные методы с произвольной логикой. Абстрактные методы MapStruct реализует сам, а остальное вы пишете руками:

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

**Почему именно абстрактный класс:** в интерфейс нельзя внедрить Spring-зависимость — поля и `@Autowired` там невозможны. Для абстрактного класса MapStruct генерирует наследника, и уже этот наследник становится Spring-бином: его поля Spring заполняет как обычно. Поэтому, как только маппингу нужны зависимости или состояние, переходят с интерфейса на абстрактный класс.

## Q23. (!) Как интегрировать MapStruct со Spring?

Достаточно сказать MapStruct, чтобы он сделал маппер Spring-бином, и дальше он живёт в контейнере как любой другой компонент. Три шага:

1. Указать `componentModel = "spring"` в `@Mapper`.
2. MapStruct пометит сгенерированную реализацию аннотацией `@Component` — она попадёт в контекст при компонентном сканировании.
3. Внедрять маппер как обычный бин — через конструктор (предпочтительно) или `@Autowired`.

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

## Q24. (!) Какие проблемы возникают при использовании MapStruct вместе с Lombok?

Корень всех бед — порядок annotation processors. Оба (Lombok и MapStruct) работают на этапе компиляции, и если MapStruct отрабатывает раньше Lombok, то геттеров и сеттеров, которые должен сгенерировать Lombok, ещё нет — MapStruct «не видит» полей и не может построить маппинг.

**Симптом:** ошибка вида `No property named "fieldName" exists in source parameter...`, хотя на классе честно стоят `@Getter`/`@Setter`.

**Решение (Maven):** явно задать порядок процессоров так, чтобы Lombok шёл первым:

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

Кроме порядка, нужна зависимость `lombok-mapstruct-binding` — это «мостик», который заставляет два процессора корректно дождаться друг друга:

```kotlin
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
```

## Q25. Как MapStruct поддерживает Lombok `@Builder`?

Начиная с версии 1.3, MapStruct распознаёт builder и при наличии предпочитает его обычному конструктору с сеттерами. Это и нужно для immutable-DTO (`@Value` + `@Builder`), у которых сеттеров нет вовсе:

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

Если завершающий метод билдера называется не `build()`, его имя указывают явно:

```java
@BeanMapping(builder = @Builder(buildMethod = "create"))
UserDto toDto(User user);
```

**Граничный случай:** для `@SuperBuilder` (билдер с поддержкой наследования) стандартного распознавания не хватает — нужен собственный `BuilderProvider`.

## Q26. Как тестировать MapStruct-маппер?

Маппер — обычный класс с детерминированной логикой, поэтому тестируется как чистая функция: подали вход, проверили выход. Контекст Spring почти никогда не нужен, и большинство мапперов покрываются быстрыми unit-тестами без него.

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

`Mappers.getMapper()` поднимает экземпляр напрямую, минуя Spring-контекст, — отсюда и скорость.

**Вариант 2 — Spring slice test** (нужен, только если маппер инжектит зависимости через `componentModel = "spring"`):

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

Есть три независимых рычага, и важно их не путать: один отвечает за `null` всего объекта, второй — за `null` отдельного поля, третий — за то, добавлять ли null-проверку перед конверсией.

**`NullValueMappingStrategy`** — что делать, если весь source-объект равен `null`:

| Значение | Поведение |
|---|---|
| `RETURN_NULL` (default) | вернуть `null` |
| `RETURN_DEFAULT` | вернуть пустой объект (new UserDto()) |

**`NullValuePropertyMappingStrategy`** — что делать, если `null` лишь отдельное поле source:

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

Условный маппинг — это «маппить поле только если выполнено условие». Начиная с MapStruct 1.5 для этого есть аннотация `@Condition`: помечаете ей метод-предикат, и MapStruct вставляет его проверку перед записью соответствующих полей:

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

До 1.5 то же делали через `conditionExpression` — условие прописывали строкой прямо в `@Mapping`, отдельно для каждого поля:

```java
@Mapping(
    target = "phone",
    source = "phone",
    conditionExpression = "java(source.getPhone() != null && !source.getPhone().isBlank())"
)
UserDto toDto(User user);
```

**Сравнение:** `@Condition` чище и безопаснее. Условие пишется один раз обычным Java-методом (его проверяет компилятор) и автоматически применяется ко всем полям подходящего типа, тогда как `conditionExpression` — непроверяемая строка, которую приходится дублировать на каждом поле.

## See also

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
