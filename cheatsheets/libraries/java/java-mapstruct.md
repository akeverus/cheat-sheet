---
title: "MapStruct: compile-time маппинг объектов"
description: "Шпаргалка по MapStruct — генерация мапперов на этапе компиляции: базовые @Mapping, вложенные объекты, коллекции, @MappingTarget, интеграция со Spring."
tags:
  - libraries
  - java
  - mapstruct
  - dto
  - mapping
difficulty: "intermediate"
updated: "2026-04-20"
---
# MapStruct: compile-time маппинг объектов

MapStruct — annotation processor для Java, который на этапе компиляции генерирует класс-имплементацию интерфейса-маппера. Никакого reflection в runtime — только прямые вызовы геттеров/сеттеров. Скорость близка к ручному коду, ошибки маппинга ловятся на этапе сборки.

Альтернатива `ModelMapper` (reflection-based, медленнее и без compile-time проверок) и ручному копированию (много boilerplate, легко забыть поле). Для DTO-слоя в Spring-проектах — индустриальный стандарт.

## Полезные ссылки

### Официальная документация
- [MapStruct Documentation](https://mapstruct.org/documentation/) — reference guide
- [MapStruct Examples](https://github.com/mapstruct/mapstruct-examples) — примеры на GitHub
- [Baeldung: Quick Guide to MapStruct](https://www.baeldung.com/mapstruct) — базовая статья
- [MapStruct IDE Support](https://mapstruct.org/documentation/ide-support/) — настройка IDEA

### См. также
- [[java-bean-validation|java-bean-validation]] — валидация DTO
- [[java-jackson|java-jackson]] — JSON-сериализация после маппинга
- [[java-lombok|java-lombok]] — совместное использование с Lombok
- [[spring-rest|spring-rest]] — маппинг в REST-контроллерах
- [[spring-data-jpa|spring-data-jpa]] — entity ↔ DTO
- [[java-basics|java-basics]] — базовые концепции Java

## Содержание

- [Установка](#установка)
- [Первый маппер](#первый-маппер)
- [Базовые аннотации](#базовые-аннотации)
- [Вложенные объекты](#вложенные-объекты)
- [Коллекции и массивы](#коллекции-и-массивы)
- [Expression и constant](#expression-и-constant)
- [qualifiedByName и Named](#qualifiedbyname-и-named)
- [@AfterMapping и @BeforeMapping](#aftermapping-и-beforemapping)
- [@MappingTarget: обновление объекта](#mappingtarget-обновление-объекта)
- [Интеграция со Spring](#интеграция-со-spring)
- [Игнорирование и дефолты](#игнорирование-и-дефолты)
- [Условные маппинги](#условные-маппинги)
- [Lombok и MapStruct](#lombok-и-mapstruct)
- [Типичные ошибки](#типичные-ошибки)
- [Лучшие практики](#лучшие-практики)

## Установка

```gradle
dependencies {
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
}
```

```xml
<!-- Maven -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>

<build>
    <plugins>
        <plugin>
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
    </plugins>
</build>
```

В IDEA: включить annotation processing — `Settings → Build → Compiler → Annotation Processors → Enable annotation processing`.

## Первый маппер

```java
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    // геттеры/сеттеры
}

public class UserDto {
    private Long id;
    private String fullName;
    private int age;
    // геттеры/сеттеры
}

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "age", expression = "java(java.time.Period.between(user.getBirthDate(), java.time.LocalDate.now()).getYears())")
    UserDto toDto(User user);

    User toEntity(UserDto dto);
}
```

Использование:

```java
UserDto dto = UserMapper.INSTANCE.toDto(user);
```

MapStruct сгенерирует `UserMapperImpl` в `build/generated/sources/annotationProcessor/`.

## Базовые аннотации

| Аннотация | Назначение |
|-----------|------------|
| `@Mapper` | помечает интерфейс/абстрактный класс как маппер |
| `@Mapping` | правило маппинга одного поля |
| `@Mappings` | контейнер (не нужен с Java 8+, `@Mapping` repeatable) |
| `@MappingTarget` | параметр, в который пишем (обновление) |
| `@Named` | именованный метод для `qualifiedByName` |
| `@Context` | пробросить объект через цепочку мапперов |
| `@InheritConfiguration` | наследовать конфигурацию другого маппера |
| `@InheritInverseConfiguration` | автоматическая обратная конфигурация |

### @Mapping

```java
@Mapper
public interface OrderMapper {

    @Mapping(source = "customer.name", target = "customerName")  // вложенный путь
    @Mapping(source = "createdAt", target = "orderDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "status", ignore = true)
    OrderDto toDto(Order order);
}
```

### Обратный маппинг через @InheritInverseConfiguration

```java
@Mapper
public interface OrderMapper {

    @Mapping(source = "customer.name", target = "customerName")
    OrderDto toDto(Order order);

    @InheritInverseConfiguration
    Order toEntity(OrderDto dto);   // автоматически: customerName → customer.name
}
```

## Вложенные объекты

### Плоский → вложенный

```java
public class PersonFlat {
    private String street;
    private String city;
}

public class Person {
    private Address address;  // Address { street, city }
}

@Mapper
public interface PersonMapper {
    @Mapping(source = "street", target = "address.street")
    @Mapping(source = "city", target = "address.city")
    Person toEntity(PersonFlat flat);
}
```

### Вложенный → плоский

```java
@Mapping(source = "address.street", target = "street")
@Mapping(source = "address.city", target = "city")
PersonFlat toFlat(Person person);
```

### Маппер внутри маппера

```java
@Mapper(uses = AddressMapper.class)
public interface PersonMapper {
    PersonDto toDto(Person person);   // address → addressDto через AddressMapper
}

@Mapper
public interface AddressMapper {
    AddressDto toDto(Address address);
}
```

## Коллекции и массивы

MapStruct автоматически обрабатывает `List`, `Set`, `Map`, массивы, если есть метод для элементов.

```java
@Mapper
public interface OrderMapper {
    OrderDto toDto(Order order);
    List<OrderDto> toDtoList(List<Order> orders);       // использует toDto для каждого
    Set<OrderDto> toDtoSet(Set<Order> orders);

    @IterableMapping(qualifiedByName = "onlyActive")
    List<UserDto> toActiveDtos(List<User> users);

    @Named("onlyActive")
    default UserDto mapActive(User user) {
        return user.isActive() ? toDto(user) : null;
    }
}
```

### Map

```java
@MapMapping(valueDateFormat = "yyyy-MM-dd")
Map<String, String> toStringMap(Map<String, LocalDate> source);
```

## Expression и constant

```java
@Mapper(imports = {LocalDateTime.class, UUID.class})
public interface AuditMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "source", constant = "API")
    @Mapping(target = "version", constant = "1")
    AuditLog create(AuditEvent event);
}
```

**Важно:** `expression` — плохо для сложной логики (нет IDE-рефакторинга, строка). Лучше — default method или `qualifiedByName`.

## qualifiedByName и @Named

Когда одного источника мало, нужна функция:

```java
@Mapper
public interface UserMapper {

    @Mapping(source = "rawEmail", target = "email", qualifiedByName = "normalizeEmail")
    @Mapping(source = "password", target = "passwordHash", qualifiedByName = "hashPassword")
    UserEntity toEntity(UserCreateDto dto);

    @Named("normalizeEmail")
    default String normalizeEmail(String email) {
        return email == null ? null : email.toLowerCase().trim();
    }

    @Named("hashPassword")
    default String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }
}
```

### Qualifier через аннотацию

Вместо строкового имени — типизированный qualifier:

```java
@Qualifier
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface EncryptedMapping {}

@Mapper
public interface SecretMapper {
    @EncryptedMapping
    default String encrypt(String value) { return Cipher.encrypt(value); }

    @Mapping(source = "cardNumber", target = "encryptedCard", qualifiedBy = EncryptedMapping.class)
    CardDto toDto(Card card);
}
```

## @AfterMapping и @BeforeMapping

Хуки для post-processing. `@MappingTarget` указывает объект, который уже создан и заполнен.

```java
@Mapper
public abstract class UserMapper {

    public abstract UserDto toDto(User user);

    @AfterMapping
    protected void calculateFullName(User source, @MappingTarget UserDto target) {
        target.setFullName(source.getFirstName() + " " + source.getLastName());
    }

    @BeforeMapping
    protected void validate(User source) {
        if (source.getId() == null) {
            throw new IllegalArgumentException("User id must not be null");
        }
    }
}
```

`@BeforeMapping` вызывается до копирования полей, `@AfterMapping` — после. Используй абстрактный класс вместо интерфейса, если нужен сложный state.

## @MappingTarget: обновление объекта

Когда нужно не создать новый DTO, а обновить существующий:

```java
@Mapper
public interface UserMapper {

    UserDto toDto(User user);

    void updateEntity(@MappingTarget User entity, UserUpdateDto dto);
}
```

Генерируется `void updateEntity(User entity, UserUpdateDto dto)` — копирует поля из `dto` в `entity`, не трогая неуказанные.

### Частичное обновление — только non-null

```java
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    void partialUpdate(@MappingTarget User entity, UserPatchDto patch);
}
```

Теперь `null` в `patch` не перезаписывает поле в `entity` — как `PATCH` в REST.

Стратегии:
- `SET_TO_NULL` (default) — пишем `null`
- `SET_TO_DEFAULT` — пишем 0 / пустую строку / пустой список
- `IGNORE` — не трогаем

## Интеграция со Spring

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
```

Сгенерированный `UserMapperImpl` будет помечен `@Component` и доступен через DI:

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;

    public UserDto getUser(Long id) {
        return mapper.toDto(repository.findById(id).orElseThrow());
    }
}
```

### Глобальная настройка

```java
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CentralMapperConfig {}

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper { ... }
```

Другие `componentModel`: `default` (статичная фабрика), `cdi` (Jakarta CDI), `jsr330` (`@Inject`), `jakarta` (Jakarta CDI 4+).

## Игнорирование и дефолты

### Игнор полей

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "createdAt", ignore = true)
User toEntity(UserDto dto);
```

### Игнор неупомянутых target-полей

```java
@BeanMapping(ignoreByDefault = true)
@Mapping(source = "name", target = "name")
@Mapping(source = "email", target = "email")
UserDto toPublicDto(User user);   // остальные поля DTO остаются null
```

### Строгая проверка неиспользуемых source/target

```java
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper { ... }
```

Если в target есть поле, которое не замаплено — будет ошибка компиляции. Must-have для production — защита от забытых полей.

### Дефолты

```java
@Mapping(target = "status", source = "status", defaultValue = "ACTIVE")
@Mapping(target = "priority", source = "priority", defaultExpression = "java(Priority.NORMAL)")
UserEntity toEntity(UserDto dto);
```

`defaultValue` — для примитивов/строк, `defaultExpression` — для Java-выражений.

## Условные маппинги

### @Condition — пропуск поля по условию

```java
@Mapper
public interface UserMapper {
    UserEntity toEntity(UserDto dto);

    @Condition
    default boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
```

Для каждого `String`-поля MapStruct добавит проверку `isNotBlank` — пустые строки не попадут в entity.

### Для конкретного свойства

```java
@Mapping(target = "email", source = "email", conditionQualifiedByName = "validEmail")
UserEntity toEntity(UserDto dto);

@Named("validEmail")
boolean isValidEmail(String email) {
    return email != null && email.contains("@");
}
```

## Lombok и MapStruct

Порядок annotation processors имеет значение — Lombok должен отработать раньше MapStruct (чтобы MapStruct видел сгенерированные геттеры).

```gradle
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // lombok-mapstruct-binding: даёт MapStruct видеть Lombok-классы
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
}
```

**Правило:** в Gradle порядок в `annotationProcessor` = порядок обработки. В Maven — зависит от порядка в `annotationProcessorPaths`.

Пример с Lombok:

```java
@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
}

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
```

## Типичные ошибки

| Ошибка | Причина | Решение |
|--------|---------|---------|
| `No property named "..." exists in source parameter(s)` | Опечатка в `source` | Проверить точный путь: `customer.name`, а не `customerName` |
| `Unmapped target property: "..."` | Поле target не замаплено | Указать `@Mapping` или `ignore = true`, либо `unmappedTargetPolicy = IGNORE` |
| `Can't map property "X a" to "Y b"` | Типы не совпадают | Добавить конвертер: метод с нужной сигнатурой или `qualifiedByName` |
| `NullPointerException` внутри маппера | `nullValueCheckStrategy = ON_IMPLICIT_CONVERSION` | Переключить на `ALWAYS` в `@Mapper` |
| Генерация не запускается | Annotation processing выключен | В IDEA: Settings → Annotation Processors → Enable |
| `MapStructImpl` не в classpath | Классы не попали в jar | Проверить, что `build/generated/.../annotationProcessor` в source sets |
| Lombok-поля не видны MapStruct | Нет `lombok-mapstruct-binding` | Добавить зависимость |
| `@Mapper(componentModel = "spring")` не регистрирует бин | Missing DI/Spring context | Проверить `@ComponentScan`, annotation processing |
| Коллекции возвращают `null` вместо пустого списка | Default Hibernate Validator behaviour | `nullValueMappingStrategy = RETURN_DEFAULT` |

## Лучшие практики

- **`unmappedTargetPolicy = ERROR`** — обязательно для production. Защищает от забытых полей.
- **Компонентная модель Spring** — избавляет от статичных фабрик, упрощает тесты.
- **Отдельные DTO для create / update / read** — разные формы маппинга без groups.
- **Избегай `expression` для сложной логики** — вынеси в default method + `qualifiedByName`.
- **`@MapperConfig` для общих настроек** — не дублируй `componentModel` и policy.
- **Проверяй сгенерированный код** — `build/generated/sources/annotationProcessor/`. Помогает понять, что именно делает маппер.
- **Не маппь всё подряд — только DTO ↔ entity / DTO ↔ DTO.** Бизнес-логике маппер не нужен.
- **`@BeanMapping(ignoreByDefault = true)`** — явный whitelist полей для public DTO, меньше утечек данных.
- **Тестируй мапперы как обычные классы** — `UserMapperImpl` — обычный Java-класс без мокирования.

```java
@Test
void toDtoMapsFullName() {
    User user = new User(1L, "Иван", "Иванов", LocalDate.of(1990, 1, 1));
    UserDto dto = mapper.toDto(user);
    assertEquals("Иван Иванов", dto.getFullName());
}
```

## См. также

- [[java-bean-validation|java-bean-validation]] — валидация DTO после маппинга
- [[java-jackson|java-jackson]] — сериализация DTO в JSON
- [[java-lombok|java-lombok]] — совместное использование
- [[spring-rest|spring-rest]] — маппинг в контроллерах
- [[spring-data-jpa|spring-data-jpa]] — маппинг entity ↔ DTO
- [[java-basics|java-basics]] — базовые концепции
- [[java-streams-fp|java-streams-fp]] — маппинг коллекций через Stream API
