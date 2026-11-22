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

`MapStruct` генерирует реализации маппинга Java Bean на этапе компиляции. Получается обычный Java-код без рефлексии для самого преобразования; ошибки известных свойств и методов видны при сборке, а поведение собственных правил проверяют тестами.

## Полезные ссылки

### Официальная документация

- [MapStruct Reference Guide](https://mapstruct.org/documentation/stable/reference/html/) — полная документация
- [MapStruct GitHub](https://github.com/mapstruct/mapstruct) — исходный код и примеры

### Baeldung tutorials

- [Quick Guide to MapStruct](https://www.baeldung.com/mapstruct) — основы
- [Custom Mapper with MapStruct](https://www.baeldung.com/mapstruct-custom-mapper)
- [Nested Mapping](https://www.baeldung.com/java-mapstruct-nested-mapping)
- [Mapping Collections](https://www.baeldung.com/java-mapstruct-mapping-collections)
- [MapStruct reference: Lombok integration](https://mapstruct.org/documentation/stable/reference/html/#lombok)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. Что такое `MapStruct` и как он работает?](#q1)
- [Q2. Как подключить `MapStruct` к Maven/Gradle-проекту?](#q2)
- [Q3. Чем `MapStruct` отличается от `ModelMapper` и `Dozer`?](#q3)
- [Q4. Что такое `@Mapper` и какие параметры он принимает?](#q4)
- [Q5. Что такое `componentModel` в `@Mapper`?](#q5)

**Маппинг полей**
- [Q6. Как маппить поля с разными именами?](#q6)
- [Q7. Как игнорировать поле при маппинге?](#q7)
- [Q8. Как задать константу или значение по умолчанию?](#q8)
- [Q9. Как использовать Java-выражение в маппинге?](#q9)
- [Q10. Как замаппить вложенные объекты (nested mapping)?](#q10)

**Коллекции и enum**
- [Q11. Как маппить коллекции (`List`, `Set`, `Map`)?](#q11)
- [Q12. Что такое `@IterableMapping` и когда он нужен?](#q12)
- [Q13. Как маппить enum?](#q13)

**Продвинутые возможности**
- [Q14. Как маппить несколько source-объектов в один target?](#q14)
- [Q15. Что такое `@MappingTarget` и зачем он нужен?](#q15)
- [Q16. Что такое `@InheritConfiguration` и `@InheritInverseConfiguration`?](#q16)
- [Q17. Как использовать `@BeforeMapping` и `@AfterMapping`?](#q17)
- [Q18. Что такое `@BeanMapping` и `NullValuePropertyMappingStrategy`?](#q18)
- [Q19. Как подключить внешний / custom mapper через `uses`?](#q19)
- [Q20. Что такое `@Qualifier` и `@Named` в `MapStruct`?](#q20)
- [Q21. Что такое Decorator-паттерн в `MapStruct`?](#q21)
- [Q22. Как работает маппинг в абстрактном классе вместо интерфейса?](#q22)

**Интеграция и инфраструктура**
- [Q23. Как интегрировать `MapStruct` со Spring?](#q23)
- [Q24. Какие проблемы возникают при использовании `MapStruct` вместе с `Lombok`?](#q24)
- [Q25. Как `MapStruct` поддерживает Lombok `@Builder`?](#q25)
- [Q26. Как тестировать `MapStruct`-маппер?](#q26)
- [Q27. Какие стратегии обработки `null` есть в `MapStruct`?](#q27)
- [Q28. Как настроить маппинг с условием (conditional mapping)?](#q28)

---

<a id="q1"></a>

## Q1. Что такое `MapStruct` и как он работает?

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
- Сгенерированный код использует прямые вызовы; выигрыш по времени проверяют на конкретной нагрузке.
- Ошибки известных свойств и отсутствие нужного метода видны при компиляции; корректность бизнес-преобразования проверяют тестами.
- Сгенерированный код — обычный читаемый Java: его можно открыть, прочитать и поставить точку останова.
- Полная поддержка IDE: автодополнение, навигация, рефакторинг работают как с любым кодом.

**Итог:** MapStruct переносит маппинг с рантайма на этап компиляции — отсюда и скорость, и ранняя проверка ошибок.

<a id="q2"></a>

## Q2. Как подключить `MapStruct` к Maven/Gradle-проекту?

Нужны две вещи: зависимость на runtime-библиотеку с аннотациями и подключение annotation processor к компилятору. Без процессора аннотации просто проигнорируются, и реализация маппера не сгенерируется.

**Maven:**

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.3</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
}
```

**Ключевой момент:** MapStruct поставляется двумя jar. `mapstruct` (аннотации и runtime-интерфейсы) попадает в зависимости приложения, а `mapstruct-processor` (сам генератор кода) подключается только к компилятору — в рантайме он не нужен, поэтому его и кладут в `annotationProcessor`, а не в `implementation`.

<a id="q3"></a>

## Q3. Чем `MapStruct` отличается от `ModelMapper` и `Dozer`?

Главное различие в моменте работы: MapStruct генерирует код при компиляции, а ModelMapper и Dozer выполняют маппинг в рантайме через рефлексию. Отсюда вытекает всё остальное — скорость, момент обнаружения ошибок и прозрачность.

| Критерий | MapStruct | ModelMapper | Dozer |
|---|---|---|---|
| Как работает | Compile-time codegen | Runtime рефлексия | Runtime рефлексия |
| Производительность | Прямые вызовы в сгенерированном коде | Зависит от конфигурации и нагрузки | Зависит от конфигурации и нагрузки |
| Обнаружение ошибок | Ошибки известных свойств и методов видны при компиляции | Ошибки правил маппинга обычно видны при выполнении | Ошибки правил маппинга обычно видны при выполнении |
| Прозрачность | Сгенерированный код читаем | «Магия» внутри | XML конфиг |
| Зависимости | API-библиотека и процессор при компиляции, сгенерированный класс при выполнении | Библиотека при выполнении | Библиотека при выполнении |
| Кастомизация | Аннотации, `uses` | Converter, PropertyMap | XML / код |
| Поддержка Lombok | Через настройку совместной обработки аннотаций | Проверяют по конкретной конфигурации | Проверяют по конкретной конфигурации |

**Вывод:** MapStruct удобен, когда важны проверяемый при сборке код и предсказуемый путь преобразования. Сравнение скорости с динамическими мапперами проверяют на конкретных объектах и настройках.

<a id="q4"></a>

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

<a id="q5"></a>

## Q5. Что такое `componentModel` в `@Mapper`?

`componentModel` определяет, как создаётся экземпляр маппера и как его потом получить в коде. По сути это выбор между «достать вручную» и «отдать управление DI-контейнеру».

| Значение | Как получить маппер |
|---|---|
| `"default"` | Вручную: `Mappers.getMapper(MyMapper.class)` или синглтон `INSTANCE` |
| `"spring"` | Генерирует `@Component`-бин — инжектится через `@Autowired` / конструктор |
| `"cdi"` | Генерирует `@ApplicationScoped`-бин для CDI |
| `"jakarta"` | Jakarta Inject (`@Named`/`@Singleton`); для Jakarta CDI есть `"jakarta-cdi"` |

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

<a id="q6"></a>

## Q6. Как маппить поля с разными именами?

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

<a id="q7"></a>

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

<a id="q8"></a>

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

<a id="q9"></a>

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

<a id="q10"></a>

## Q10. Как замаппить вложенные объекты (nested mapping)?

Есть три подхода в зависимости от того, нужна ли вложенность в результате или вы её, наоборот, разворачиваете в плоскую структуру.

**Вариант 1 — автоматический (вложенность сохраняется)**: MapStruct выбирает доступный метод преобразования вложенного типа; подходящий метод можно объявить в маппере или подключить через `uses`. В простом случае MapStruct может сгенерировать вложенный метод сам:

```java
@Mapper(uses = AddressMapper.class)
public interface UserMapper {
    @Mapping(source = "address", target = "addressDto")
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
    @Mapping(source = "address", target = "addressDto")
    public abstract UserDto toDto(User user);

    protected AddressDto toAddressDto(Address address) {
        return new AddressDto(address.getCity()); // собственное правило
    }
}
```

<a id="q11"></a>

## Q11. Как маппить коллекции (`List`, `Set`, `Map`)?

Достаточно объявить метод с коллекцией в сигнатуре — MapStruct сгенерирует его сам, если уже знает, как маппить один элемент:

```java
@Mapper
public interface OrderMapper {
    OrderDto toDto(Order order);
    List<OrderDto> toDtoList(List<Order> orders);  // генерируется автоматически
    Set<OrderDto> toDtoSet(Set<Order> orders);
}
```

Под капотом он создаёт подходящую целевую коллекцию, проходит по элементам и вызывает для каждого `toDto()` — без ручного цикла в маппере.

Для `Map` он маппит отдельно ключи и значения:

```java
Map<String, OrderDto> toDtoMap(Map<String, Order> map);
```

**Подводный камень:** если ключ — пользовательский тип, проверьте, нашёл ли MapStruct нужное преобразование. При неоднозначности или особой логике добавьте явный метод конверсии ключа.

<a id="q12"></a>

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

<a id="q13"></a>

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
- `MappingConstants.ANY_REMAINING` — сначала сохраняет сопоставление по одинаковым именам, затем отправляет остальные значения в указанный target.
- `MappingConstants.ANY_UNMAPPED` — все значения без явного правила отправляет в указанный target, не пытаясь сопоставить их по имени.
- `MappingConstants.NULL` — позволяет задать, во что превращается `null`.

<a id="q14"></a>

## Q14. Как маппить несколько source-объектов в один target?

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

<a id="q15"></a>

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

**Сценарий применения:** `partialUpdate` со стратегией `IGNORE` подходит, если в DTO значение `null` означает «поле не передано». Тогда эти поля не меняют существующую сущность. Если API различает явный `null` и отсутствие поля, одного `IGNORE` недостаточно: это различие нужно сохранить в модели запроса.

<a id="q16"></a>

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

<a id="q17"></a>

## Q17. Как использовать `@BeforeMapping` и `@AfterMapping`?

Это хуки жизненного цикла: метод с `@BeforeMapping` вызывается до основного маппинга, с `@AfterMapping` — сразу после. MapStruct встраивает их вызовы в сгенерированный код. Удобно для валидации входа и пост-обработки результата:

```java
@Mapper
public abstract class UserMapper {

    @BeforeMapping
    protected void validate(User user) {
        if (user != null && user.getEmail() == null) {
            throw new IllegalArgumentException("email required");
        }
    }

    @AfterMapping
    protected void setAuditFields(@MappingTarget UserDto dto) {
        dto.setMappedAt(Instant.now());
    }

    public abstract UserDto toDto(User user);
}
```

Хуки можно реализовать в `default`-методах интерфейса, в абстрактном классе-маппере или в отдельном классе из `@Mapper(uses = ...)`. Время вызова `@BeforeMapping` зависит от параметров хука: метод без `@MappingTarget` может запускаться ещё до проверки source на `null`, а метод с `@MappingTarget` — после создания target.

Параметр `@MappingTarget` в `@AfterMapping` даёт доступ к уже заполненному результату — именно так в него дописывают вычисляемые или аудит-поля.

<a id="q18"></a>

## Q18. Что такое `@BeanMapping` и `NullValuePropertyMappingStrategy`?

`@BeanMapping` настраивает поведение одного конкретного метода маппинга целиком (тогда как `@Mapping` работает на уровне отдельного поля). Через неё задают стратегии обработки `null`, отключают автомаппинг и выбирают builder:

```java
@BeanMapping(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, // null поле → не трогать
    ignoreByDefault = true  // не маппить никакие поля без явного @Mapping
)
@Mapping(target = "name", source = "name")
@Mapping(target = "email", source = "email")
void partialUpdate(UserPatchRequest request, @MappingTarget User user);
```

**Стратегии `NullValuePropertyMappingStrategy`** (что делать, если отдельное source-поле равно `null`):

| Значение | Поведение |
|---|---|
| `SET_TO_NULL` (default) | Записать `null` в target: `target.setField(null)` |
| `IGNORE` | Не трогать target-поле — сохранить его прежнее значение |
| `SET_TO_DEFAULT` | Поставить значение по умолчанию (`0`, `""`, пустую коллекцию) |

**Эмпирическое правило:** для PATCH-операций над существующей сущностью берите `IGNORE` — иначе незаполненные поля запроса затрут уже сохранённые данные.

<a id="q19"></a>

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

<a id="q20"></a>

## Q20. Что такое `@Qualifier` и `@Named` в `MapStruct`?

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

<a id="q21"></a>

## Q21. Что такое Decorator-паттерн в `MapStruct`?

Это способ обернуть сгенерированный маппер своим классом и дописать логику, не вмешиваясь в генерацию. Аннотация `@DecoratedWith` указывает класс-декоратор, который вызывает оригинальный маппер через делегат, а вокруг этого вызова добавляет нужное поведение:

```java
@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
    UserDto toDto(User user);
}

public abstract class UserMapperDecorator implements UserMapper {
    @Autowired
    @Qualifier("delegate") // исходная сгенерированная реализация
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

<a id="q22"></a>

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

**Когда нужен абстрактный класс:** если собственная логика маппера хранит поля или использует внедрённую зависимость, его реализация удобнее интерфейса. MapStruct генерирует подкласс, а при `componentModel = "spring"` Spring регистрирует его как бин. Для отдельных преобразований можно оставить интерфейс и подключить отдельный mapper через `uses`.

<a id="q23"></a>

## Q23. Как интегрировать `MapStruct` со Spring?

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

<a id="q24"></a>

## Q24. Какие проблемы возникают при использовании `MapStruct` вместе с `Lombok`?

MapStruct должен увидеть свойства, которые Lombok создаёт во время обработки аннотаций. Без согласования двух процессоров MapStruct может не распознать геттеры, сеттеры или builder и отклонить маппинг. Порядок строк с процессорами в конфигурации сам по себе не задаёт надёжный порядок их работы.

**Симптом:** ошибка вида `No property named "fieldName" exists in source parameter...`, хотя на классе честно стоят `@Getter`/`@Setter`.

**Решение (Maven):** подключить оба процессора и `lombok-mapstruct-binding` в `annotationProcessorPaths`:

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
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```

**Решение (Gradle):**

```kotlin
annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
```

Для Lombok 1.18.16+ официальный справочник MapStruct прямо требует `lombok-mapstruct-binding`. Версии и обработку аннотаций нужно проверить на сборке проекта.

<a id="q25"></a>

## Q25. Как `MapStruct` поддерживает Lombok `@Builder`?

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

**Граничный случай:** для `@SuperBuilder` и нестандартных builder API проверьте сгенерированный код. При необходимости настройте `@Builder(buildMethod = ...)`, отключите builder для метода или используйте расширение `BuilderProvider`; необходимость собственного провайдера не следует из одного лишь `@SuperBuilder`.

<a id="q26"></a>

## Q26. Как тестировать `MapStruct`-маппер?

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

**Вариант 2 — Spring slice test** (полезен для проверки инъекции при `componentModel = "spring"`):

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

<a id="q27"></a>

## Q27. Какие стратегии обработки `null` есть в `MapStruct`?

Есть три независимых рычага, и важно их не путать: один отвечает за `null` всего объекта, второй — за `null` отдельного поля, третий — за то, добавлять ли null-проверку перед конверсией.

**`NullValueMappingStrategy`** — что делать, если весь source-объект равен `null`:

| Значение | Поведение |
|---|---|
| `RETURN_NULL` (default) | вернуть `null` |
| `RETURN_DEFAULT` | для Bean создать объект по умолчанию, для коллекции или Map — пустой результат |

**`NullValuePropertyMappingStrategy`** — что делать, если `null` у отдельного поля source при обновлении существующего target через `@MappingTarget`:

| Значение | Поведение |
|---|---|
| `SET_TO_NULL` (default) | target.setField(null) |
| `IGNORE` | не трогать target-поле |
| `SET_TO_DEFAULT` | установить значение по умолчанию (`0`, `""`, новую пустую коллекцию) |

**`NullValueCheckStrategy`** — когда проверять null перед конверсией:

| Значение | Поведение |
|---|---|
| `ON_IMPLICIT_CONVERSION` (default) | проверка только при неявных конверсиях |
| `ALWAYS` | запрашивать проверку null для свойства, кроме случаев с отдельным presence checker |

<a id="q28"></a>

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

Альтернатива в MapStruct 1.5+ — `conditionExpression`: условие прописывают строкой прямо в `@Mapping` для конкретного поля:

```java
@Mapping(
    target = "phone",
    source = "phone",
    conditionExpression = "java(user.getPhone() != null && !user.getPhone().isBlank())"
)
UserDto toDto(User user);
```

**Сравнение:** метод с `@Condition` переиспользуется и проверяется компилятором как Java-код; `conditionExpression` задаёт условие для конкретного поля, а его содержимое проверяется при компиляции сгенерированного маппера. Выбор зависит от того, нужно ли одно правило для нескольких свойств.

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
