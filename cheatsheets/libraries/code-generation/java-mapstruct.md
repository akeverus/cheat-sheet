---
title: "MapStruct: Маппинг объектов в Java"
description: "Комплексное руководство по использованию MapStruct для автоматического маппинга между Java объектами - от основ до продвинутых техник."
tags: ["libraries", "code-generation", "java-mapstruct"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# MapStruct: Маппинг объектов в **Java**

**Комплексное руководство по использованию `MapStruct` для автоматического маппинга между `Java` объектами - от основ до продвинутых техник.**

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [MapStruct](https://mapstruct.org/) — официальный сайт
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/) — полная документация
- [MapStruct GitHub](https://github.com/mapstruct/mapstruct) — репозиторий проекта

### Интеграция
- [Spring Boot MapStruct](https://mapstruct.org/documentation/spring-boot/) — **Spring Boot** интеграция
- [MapStruct Examples](https://github.com/mapstruct/mapstruct-examples) — примеры использования
- [MapStruct Spring Extensions](https://github.com/mapstruct/mapstruct-spring-extensions) — **Spring** интеграция

## Содержание

- [Введение в MapStruct](#введение-в-mapstruct)
  - [Почему MapStruct?](#почему-mapstruct)
  - [Как работает MapStruct?](#как-работает-mapstruct)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Настройка в IDE](#настройка-в-ide)
    - [IntelliJ IDEA](#intellij-idea)
    - [Eclipse](#eclipse)
  - [Базовая конфигурация](#базовая-конфигурация)
- [Основы маппинга](#основы-маппинга)
  - [Создание простого маппера](#создание-простого-маппера)
  - [Использование маппера](#использование-маппера)
- [Простые мапперы](#простые-мапперы)
  - [Базовый маппинг](#базовый-маппинг)
  - [Маппинг с различными именами полей](#маппинг-с-различными-именами-полей)
  - [Игнорирование полей](#игнорирование-полей)
- [Маппинг коллекций](#маппинг-коллекций)
  - [Списки и множества](#списки-и-множества)
  - [Массивы](#массивы)
  - [Map](#map)
- [Вложенные объекты](#вложенные-объекты)
  - [Маппинг вложенных объектов](#маппинг-вложенных-объектов)
  - [Глубокий маппинг](#глубокий-маппинг)
- [Конфигурация маппинга](#конфигурация-маппинга)
  - [Component Model](#component-model)
  - [Injection Strategy](#injection-strategy)
  - [Null Value Mapping](#null-value-mapping)
- [Обновление объектов](#обновление-объектов)
  - [Обновление существующего объекта](#обновление-существующего-объекта)
- [Наследование и полиморфизм](#наследование-и-полиморфизм)
  - [Маппинг с наследованием](#маппинг-с-наследованием)
- [Интеграция с Spring](#интеграция-с-spring)
  - [Spring Boot конфигурация](#spring-boot-конфигурация)
  - [Spring Expression Language](#spring-expression-language)
  - [Кастомные мапперы в Spring](#кастомные-мапперы-в-spring)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Кастомные методы маппинга](#кастомные-методы-маппинга)
  - [Generic мапперы](#generic-мапперы)
  - [Builder паттерн](#builder-паттерн)
  - [Маппинг enums](#маппинг-enums)
- [Best practices](#best-practices)
  - [1. Правильная структура мапперов](#1-правильная-структура-мапперов)
  - [2. Обработка null значений](#2-обработка-null-значений)
  - [3. Маппинг коллекций и сложных структур](#3-маппинг-коллекций-и-сложных-структур)
  - [4. Тестирование мапперов](#4-тестирование-мапперов)
  - [5. Производительность и оптимизация](#5-производительность-и-оптимизация)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Преимущества MapStruct](#преимущества-mapstruct)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать MapStruct](#когда-использовать-mapstruct)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)

## Введение в **MapStruct**

**MapStruct** — это **code generator**, который значительно упрощает и ускоряет реализацию маппинга между **Java bean**-ами. Во время компиляции он генерирует реализации интерфейсов мапперов, избавляя от необходимости писать **boilerplate** код.

### Почему **MapStruct**?

**MapStruct** предлагает множество преимуществ:**

1. **Высокая производительность** — Генерирует оптимальный код без **reflection**
2. **Type safety** — Полная проверка типов на этапе компиляции
3. **Простота использования** — Минимальный **boilerplate** код
4. **IDE support** — Поддержка автодополнения и рефакторинга
5. **Тестируемость** — Генерируемый код легко тестировать
6. **Без зависимостей** — Не требует **runtime** зависимостей
7. **Широкая поддержка** — Работает с любыми **Java** фреймворками
8. **Конфигурируемость** — Гибкие настройки маппинга

### Как работает **MapStruct**?

**MapStruct** использует **Annotation Processing Tool (APT)** для генерации кода во время компиляции. Он анализирует интерфейсы с аннотацией `@Mapper` и генерирует реализации, которые копируют данные между объектами используя простые **getter**/**setter** методы.

### Преимущества и недостатки

**Преимущества:**
- Быстрая разработка мапперов
- Высокая производительность
- **Type safety**
- Читаемый код
- Хорошая поддержка **IDE**

**Недостатки:**
- Только для простых маппингов
- Требует явного определения сложных трансформаций
- Не подходит для динамического маппинга

## Установка и настройка

### **Maven**

Зависимости **Maven** для **MapStruct** (**mapstruct и annotation processor для генерации кода**).

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- Для генерации кода -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

### **Gradle**

```kotlin
plugins {
    id("org.mapstruct.mapstruct-processor") version "1.5.5.Final"
}

dependencies {
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
}
```

### Настройка в **IDE**

#### **IntelliJ IDEA**
- **Annotation `Processing`:** Включено по умолчанию в современных версиях
- **MapStruct `Plugin`:** Рекомендуется установить для лучшей поддержки

#### **Eclipse**
- Требуется настройка **annotation processing**
- Рекомендуется использовать **m2e-apt plugin**

### Базовая конфигурация

```java
// В большинстве случаев MapStruct работает "из коробки"
// Для дополнительных настроек можно использовать mapstruct-config.xml

// META-INF/mapstruct-config.xml
<?xml version="1.0" encoding="UTF-8"?>
<mapstruct-config>
    <processor-options>
        <mapstruct.defaultComponentModel>default</mapstruct.defaultComponentModel>
        <mapstruct.defaultInjectionStrategy>field</mapstruct.defaultInjectionStrategy>
    </processor-options>
</mapstruct-config>
```

## Основы маппинга

### Создание простого маппера

```java
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}

// Генерируется реализация:
// public class UserMapperImpl implements UserMapper {
//     @Override
//     public UserDto toDto(User user) {
//         if (user == null) {
//             return null;
//         }
//
//         UserDto userDto = new UserDto();
//         userDto.setId(user.getId());
//         userDto.setName(user.getName());
//         userDto.setEmail(user.getEmail());
//
//         return userDto;
//     }
//
//     // Аналогично для toEntity
// }
```

### Использование маппера

```java
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    public User createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        // Дополнительная логика...
        return userRepository.save(user);
    }
}
```

## Простые мапперы

### Базовый маппинг

```java
/
 * Entity класс - представление пользователя в базе данных
 * Содержит все поля, необходимые для хранения в БД
 */
public class User {
    private Long id;              // Уникальный идентификатор пользователя
    private String firstName;     // Имя пользователя
    private String lastName;      // Фамилия пользователя
    private String email;         // Email адрес пользователя
    private LocalDate birthDate;  // Дата рождения пользователя
    private boolean active;       // Флаг активности пользователя
    private Address address;      // Адрес пользователя (вложенный объект)

    // Геттеры и сеттеры для всех полей...
}

/
 * DTO класс - Data Transfer Object для передачи данных через API
 * Содержит только те поля, которые нужно передать клиенту
 */
public class UserDto {
    private Long id;              // Уникальный идентификатор пользователя
    private String firstName;     // Имя пользователя
    private String lastName;      // Фамилия пользователя
    private String email;         // Email адрес пользователя
    private LocalDate birthDate;  // Дата рождения пользователя
    private boolean active;       // Флаг активности пользователя
    private AddressDto address;   // Адрес пользователя (DTO версия вложенного объекта)

    // Геттеры и сеттеры для всех полей...
}

/
 * MapStruct маппер интерфейс для преобразования между User и UserDto
 * MapStruct автоматически сгенерирует реализацию этого интерфейса на этапе компиляции
 */
@Mapper  // Аннотация MapStruct - указывает что это маппер интерфейс
public interface UserMapper {

    /
     * Преобразование Entity в DTO
     * MapStruct автоматически сопоставит поля с одинаковыми именами
     * @param user Entity объект пользователя
     * @return DTO объект пользователя
     */
    UserDto toDto(User user);

    /
     * Преобразование DTO в Entity
     * MapStruct автоматически сопоставит поля с одинаковыми именами
     * @param userDto DTO объект пользователя
     * @return Entity объект пользователя
     */
    User toEntity(UserDto userDto);

    /
     * Дополнительные методы для маппинга вложенных объектов
     * MapStruct автоматически использует эти методы при маппинге User <-> UserDto
     */
    AddressDto addressToAddressDto(Address address);  // Преобразование Address в AddressDto
    Address addressDtoToAddress(AddressDto addressDto); // Преобразование AddressDto в Address
}
```

### Маппинг с различными именами полей

```java
/
 * MapStruct маппер для Product с кастомным маппингом полей
 * Демонстрирует использование @Mapping для полей с разными именами
 */
@Mapper  // Аннотация MapStruct маппера
public interface ProductMapper {

    /
     * Преобразование Product Entity в ProductDto с кастомным маппингом полей
     * @Mapping указывает как сопоставить поля с разными именами
     * @param product Entity объект продукта
     * @return DTO объект продукта
     */
    @Mapping(source = "productName", target = "name")      // Поле productName в Entity -> name в DTO
    @Mapping(source = "productPrice", target = "price")    // Поле productPrice в Entity -> price в DTO
    @Mapping(source = "creationDate", target = "createdAt") // Поле creationDate в Entity -> createdAt в DTO
    ProductDto toDto(Product product);

    /
     * Обратное преобразование ProductDto в Product Entity
     * Маппинг в обратном направлении: DTO -> Entity
     * @param productDto DTO объект продукта
     * @return Entity объект продукта
     */
    @Mapping(source = "name", target = "productName")        // Поле name в DTO -> productName в Entity
    @Mapping(source = "price", target = "productPrice")      // Поле price в DTO -> productPrice в Entity
    @Mapping(source = "createdAt", target = "creationDate")  // Поле createdAt в DTO -> creationDate в Entity
    Product toEntity(ProductDto productDto);
}

/
 * MapStruct маппер для Order с множественными маппингами
 * Демонстрирует использование @Mappings для нескольких полей одновременно
 */
@Mapper  // Аннотация MapStruct маппера
public interface OrderMapper {

    /
     * Преобразование Order Entity в OrderSummaryDto с множественными кастомными маппингами
     * @Mappings позволяет указать несколько @Mapping аннотаций одновременно
     * Поддерживает вложенные поля через точечную нотацию (customer.firstName)
     * @param order Entity объект заказа
     * @return DTO объект сводки заказа
     */
    @Mappings({  // Группировка нескольких маппингов в одну аннотацию
        @Mapping(source = "customer.firstName", target = "customerName"),    // Вложенное поле: customer.firstName -> customerName
        @Mapping(source = "customer.email", target = "customerEmail"),       // Вложенное поле: customer.email -> customerEmail
        @Mapping(source = "totalAmount", target = "amount"),                // Поле totalAmount -> amount
        @Mapping(source = "orderDate", target = "date")                     // Поле orderDate -> date
    })
    OrderSummaryDto toSummaryDto(Order order);
}
```

### Игнорирование полей

```java
@Mapper
public interface UserMapper {

    @Mapping(target = "password", ignore = true)  // Игнорируем поле
    @Mapping(target = "createdAt", ignore = true) // Игнорируем поле
    @Mapping(target = "updatedAt", ignore = true) // Игнорируем поле
    UserDto toDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)        // ID генерируется БД
    @Mapping(target = "createdAt", ignore = true) // Устанавливается автоматически
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest request);
}
```

## Маппинг коллекций

### Списки и множества

```java
@Mapper
public interface OrderMapper {

    // Автоматический маппинг коллекций
    List<OrderDto> toDtoList(List<Order> orders);

    Set<OrderDto> toDtoSet(Set<Order> orders);

    // С маппингом элементов
    @Mapping(source = "items", target = "orderItems")
    OrderDto toDto(Order order);

    List<OrderItemDto> orderItemsToOrderItemDtos(List<OrderItem> orderItems);
}

// Использование
@Service
public class OrderService {

    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }

    public OrderDto getOrderWithItems(Long id) {
        Order order = orderRepository.findByIdWithItems(id);
        return orderMapper.toDto(order);
    }
}
```

### Массивы

```java
@Mapper
public interface ArrayMapper {

    String[] stringsToStringArray(List<String> strings);

    List<String> stringArrayToStrings(String[] strings);

    int[] integersToIntArray(List<Integer> integers);

    Integer[] intArrayToIntegers(int[] ints);
}
```

### **Map**

```java
@Mapper
public interface MapMapper {

    // Маппинг Map в объект
    @Mapping(source = "properties", target = "attributes")
    ConfigurationDto toDto(Configuration config);

    // Маппинг объекта в Map
    Map<String, Object> toMap(ConfigurationDto config);
}

public class Configuration {
    private Map<String, String> properties;

    // getters and setters...
}

public class ConfigurationDto {
    private Map<String, String> attributes;

    // getters and setters...
}
```

## Вложенные объекты

### Маппинг вложенных объектов

```java
// Сущности
public class Company {
    private Long id;
    private String name;
    private Address headquarters;
    private List<Department> departments;

    // getters and setters...
}

public class Address {
    private String street;
    private String city;
    private String country;
    private String zipCode;

    // getters and setters...
}

// DTO
public class CompanyDto {
    private Long id;
    private String name;
    private AddressDto headquarters;
    private List<DepartmentDto> departments;

    // getters and setters...
}

@Mapper
public interface CompanyMapper {

    CompanyDto toDto(Company company);

    Company toEntity(CompanyDto companyDto);

    // Маппинг вложенных объектов
    AddressDto addressToAddressDto(Address address);
    Address addressDtoToAddress(AddressDto addressDto);

    DepartmentDto departmentToDepartmentDto(Department department);
    List<DepartmentDto> departmentsToDepartmentDtos(List<Department> departments);
}
```

### Глубокий маппинг

```java
@Mapper
public interface DeepMappingMapper {

    @Mapping(source = "company.headquarters.city", target = "headquartersCity")
    @Mapping(source = "company.departments", target = "departmentNames")
    CompanySummaryDto toSummaryDto(Company company);

    default List<String> mapDepartments(List<Department> departments) {
        return departments.stream()
            .map(Department::getName)
            .collect(Collectors.toList());
    }
}

public class CompanySummaryDto {
    private String name;
    private String headquartersCity;
    private List<String> departmentNames;

    // getters and setters...
}
```

## Конфигурация маппинга

### **Component Model**

```java
// Spring component
@Mapper(componentModel = "spring")
public interface SpringUserMapper {
    UserDto toDto(User user);
    // Будет создан Spring bean
}

// CDI component
@Mapper(componentModel = "cdi")
public interface CdiUserMapper {
    UserDto toDto(User user);
    // Для CDI контейнеров
}

// JSR-330
@Mapper(componentModel = "jsr330")
public interface Jsr330UserMapper {
    UserDto toDto(User user);
    // Использует @Inject/@Named
}

// Default (без dependency injection)
@Mapper
public interface DefaultUserMapper {
    UserDto toDto(User user);
    // Обычный класс без DI
}
```

### **Injection Strategy**

```java
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ConstructorInjectionMapper {
    // Использует constructor injection для Spring
}

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface FieldInjectionMapper {
    // Использует field injection для Spring (по умолчанию)
}

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface SetterInjectionMapper {
    // Использует setter injection для Spring
}
```

### **Null Value Mapping**

```java
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface NullValueMapper {

    // Возвращает null если source null
    UserDto toDto(User user);
}

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface DefaultValueMapper {

    // Возвращает пустой объект если source null
    UserDto toDto(User user);
}

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IgnoreNullMapper {

    // Игнорирует null значения при маппинге
    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    UserDto toDto(User user);
}
```

## Обновление объектов

### Обновление существующего объекта

```java
@Mapper(componentModel = "spring")
public interface UserUpdateMapper {

    // Обновление существующего объекта
    @Mapping(target = "id", ignore = true)        // Не обновляем ID
    @Mapping(target = "createdAt", ignore = true) // Не обновляем дату создания
    @Mapping(target = "password", ignore = true)  // Не обновляем пароль
    void updateUserFromDto(UpdateUserRequest request, @MappingTarget User user);

    // Создание нового объекта из обновления
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User createUserFromDto(UpdateUserRequest request);
}

// Использование
@Service
public class UserService {

    private final UserUpdateMapper userUpdateMapper;

    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        userUpdateMapper.updateUserFromDto(request, user);

        return userRepository.save(user);
    }
}
```

## Наследование и полиморфизм

### Маппинг с наследованием

```java
// Базовые классы
public class Vehicle {
    protected String brand;
    protected String model;
    protected int year;

    // getters and setters...
}

public class Car extends Vehicle {
    private int seats;
    private String fuelType;

    // getters and setters...
}

public class Truck extends Vehicle {
    private double cargoCapacity;
    private int axles;

    // getters and setters...
}

// DTO
public class VehicleDto {
    private String brand;
    private String model;
    private int year;
    private String type; // "CAR" или "TRUCK"

    // getters and setters...
}

@Mapper
public interface VehicleMapper {

    // Маппинг с типом
    @Mapping(source = "type", target = "type")
    VehicleDto toDto(Vehicle vehicle);

    // Фабричные методы для создания правильного типа
    default Vehicle toEntity(VehicleDto dto) {
        if ("CAR".equals(dto.getType())) {
            return toCar(dto);
        } else if ("TRUCK".equals(dto.getType())) {
            return toTruck(dto);
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + dto.getType());
    }

    @Mapping(source = "seats", target = "seats")
    @Mapping(source = "fuelType", target = "fuelType")
    Car toCar(VehicleDto dto);

    @Mapping(source = "cargoCapacity", target = "cargoCapacity")
    @Mapping(source = "axles", target = "axles")
    Truck toTruck(VehicleDto dto);
}
```

## Интеграция с **Spring**

### **Spring Boot** конфигурация

```java
@Configuration
public class MapStructConfig {

    // Не требуется специальная конфигурация для базового использования
    // MapStruct автоматически интегрируется с Spring Boot
}

// Использование в сервисах
@Service
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // MapStruct маппер автоматически внедряется как Spring bean
}
```

### **Spring Expression Language**

```java
@Mapper(componentModel = "spring")
public interface ExpressionMapper {

    @Mapping(target = "fullName",
             expression = "java(user.getFirstName() + ' ' + user.getLastName())")
    @Mapping(target = "age",
             expression = "java(java.time.Period.between(user.getBirthDate(), java.time.LocalDate.now()).getYears())")
    UserDto toDto(User user);
}
```

### Кастомные мапперы в **Spring**

```java
@Component
public class CustomUserMapper {

    private final AddressMapper addressMapper;
    private final UserMapper userMapper;

    @Autowired
    public CustomUserMapper(AddressMapper addressMapper, UserMapper userMapper) {
        this.addressMapper = addressMapper;
        this.userMapper = userMapper;
    }

    public UserDto toDto(User user) {
        UserDto dto = userMapper.toDto(user);

        // Кастомная логика
        if (user.getAddress() != null) {
            dto.setAddressSummary(
                user.getAddress().getCity() + ", " + user.getAddress().getCountry()
            );
        }

        return dto;
    }
}
```

## Продвинутые возможности

### Кастомные методы маппинга

```java
@Mapper(componentModel = "spring")
public interface AdvancedMapper {

    @Mapping(source = "status", target = "status", qualifiedByName = "mapStatus")
    UserDto toDto(User user);

    @Named("mapStatus")
    default String mapUserStatus(UserStatus status) {
        return switch (status) {
            case ACTIVE -> "Активен";
            case INACTIVE -> "Неактивен";
            case SUSPENDED -> "Заблокирован";
            default -> "Неизвестно";
        };
    }

    // Использование @Context для передачи дополнительной информации
    @Mapping(source = "price", target = "formattedPrice")
    ProductDto toDto(Product product, @Context Locale locale);

    default String formatPrice(BigDecimal price, @Context Locale locale) {
        return NumberFormat.getCurrencyInstance(locale).format(price);
    }
}
```

### **Generic** мапперы

```java
public interface GenericMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtoList(List<E> entities);

    List<E> toEntityList(List<D> dtos);
}

@Mapper
public interface UserMapper extends GenericMapper<User, UserDto> {

    // Дополнительные методы для User
    @Mapping(target = "password", ignore = true)
    UserDto toDtoWithoutPassword(User user);
}
```

### **Builder** паттерн

```java
// Для объектов с builder паттерном
@Mapper(componentModel = "spring")
public interface BuilderMapper {

    // Автоматически работает с builder
    @Mapping(target = "name", source = "fullName")
    UserDto toDto(User user);

    // Для создания через builder
    default User createUser(CreateUserRequest request) {
        return User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .build();
    }
}
```

### Маппинг **enums**

```java
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

public enum UserStatusDto {
    ACTIVE("Активен"),
    INACTIVE("Неактивен"),
    SUSPENDED("Заблокирован");

    private final String description;

    UserStatusDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

@Mapper
public interface EnumMapper {

    // Автоматический маппинг enum по имени
    UserStatusDto mapStatus(UserStatus status);

    // Кастомный маппинг
    default UserStatusDto mapStatusCustom(UserStatus status) {
        return switch (status) {
            case ACTIVE -> UserStatusDto.ACTIVE;
            case INACTIVE -> UserStatusDto.INACTIVE;
            case SUSPENDED -> UserStatusDto.SUSPENDED;
        };
    }
}
```

## **Best practices**

### 1. Правильная структура мапперов

```java
// ✅ Хорошо - разделение ответственности
@Mapper(componentModel = "spring")
public interface UserMapper {

    // Основные методы маппинга
    UserDto toDto(User user);
    User toEntity(UserDto dto);

    // Специфические мапперы
    UserSummaryDto toSummaryDto(User user);

    // Обновление
    void updateFromDto(UserDto dto, @MappingTarget User user);
}

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDto toDto(Address address);
    Address toEntity(AddressDto dto);
}

// Композиция мапперов
@Component
public class UserServiceMapper {

    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    @Autowired
    public UserServiceMapper(UserMapper userMapper, AddressMapper addressMapper) {
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
    }

    public UserDto toFullDto(User user) {
        UserDto dto = userMapper.toDto(user);
        if (user.getAddress() != null) {
            dto.setAddress(addressMapper.toDto(user.getAddress()));
        }
        return dto;
    }
}

// ❌ Плохо - все в одном маппере
@Mapper(componentModel = "spring")
public interface MonolithicMapper {

    // Смешивание разных сущностей в одном маппере
    UserDto toUserDto(User user);
    AddressDto toAddressDto(Address address);
    ProductDto toProductDto(Product product);
}
```

### 2. Обработка **null** значений

```java
// ✅ Хорошо - явная обработка null
@Mapper(componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SafeMapper {

    @Mapping(target = "name", source = "name",
             nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    UserDto toDto(User user);

    // Кастомная обработка null
    default String mapString(String value) {
        return value != null ? value.trim() : null;
    }

    default List<String> mapStringList(List<String> list) {
        return list != null ? list.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .collect(Collectors.toList()) : null;
    }
}

// Использование в сервисе
@Service
public class UserService {

    private final SafeMapper mapper;

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return mapper.toDto(user); // Безопасно возвращает null
    }
}
```

### 3. Маппинг коллекций и сложных структур

```java
// ✅ Хорошо - правильный маппинг коллекций
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "items", target = "orderItems")
    OrderDto toDto(Order order);

    // Явное определение маппинга элементов
    List<OrderItemDto> toOrderItemDtos(List<OrderItem> items);

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "unitPrice")
    OrderItemDto toOrderItemDto(OrderItem item);

    // Маппинг с дополнительной логикой
    default OrderSummaryDto toSummaryDto(Order order) {
        return new OrderSummaryDto(
            order.getId(),
            order.getCustomer().getName(),
            order.getItems().size(),
            order.getTotalAmount()
        );
    }
}

// ✅ Хорошо - использование @Context
@Mapper(componentModel = "spring")
public interface ContextualMapper {

    @Mapping(source = "price", target = "formattedPrice")
    @Mapping(source = "date", target = "formattedDate")
    ProductDto toDto(Product product, @Context Locale locale, @Context User currentUser);

    default String formatPrice(BigDecimal price, @Context Locale locale) {
        return NumberFormat.getCurrencyInstance(locale).format(price);
    }

    default String formatDate(LocalDate date, @Context Locale locale) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(locale));
    }
}
```

### 4. Тестирование мапперов

```java
// ✅ Хорошо - тестирование мапперов
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserToDto() {
        // Given
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .birthDate(LocalDate.of(1990, 1, 1))
            .active(true)
            .build();

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    public void testNullHandling() {
        // Given
        User user = null;

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    public void testUpdateUser() {
        // Given
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .build();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Jane");
        request.setEmail("jane@example.com");

        // When
        userMapper.updateUserFromDto(request, user);

        // Then
        assertThat(user.getId()).isEqualTo(1L); // ID не изменился
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
    }
}
```

### 5. Производительность и оптимизация

```java
// ✅ Хорошо - использование @MapperConfig для глобальных настроек
@MapperConfig(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface MapperConfiguration {
    // Глобальные настройки для всех мапперов
}

@Mapper(config = MapperConfiguration.class)
public interface OptimizedMapper {

    // Все мапперы наследуют глобальную конфигурацию
    UserDto toDto(User user);
}

// ✅ Хорошо - использование @DecoratedWith для дополнительной логики
@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {

    UserDto toDto(User user);
    User toEntity(UserDto dto);
}

// Декоратор для дополнительной логики
@Component
public class UserMapperDecorator implements UserMapper {

    private final UserMapper delegate;

    @Autowired
    public UserMapperDecorator(UserMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserDto toDto(User user) {
        UserDto dto = delegate.toDto(user);

        // Дополнительная логика
        dto.setFullName(user.getFirstName() + " " + user.getLastName());

        return dto;
    }

    @Override
    public User toEntity(UserDto dto) {
        return delegate.toEntity(dto);
    }
}
```


## Заключение

**MapStruct** — это мощная библиотека для автоматического маппинга **Java** объектов, которая значительно упрощает разработку и повышает производительность приложений.

### Преимущества **MapStruct**

1. **Высокая производительность** — Генерирует оптимальный код без **reflection**
2. **Type safety** — Полная проверка типов на этапе компиляции
3. **Простота использования** — Минимальный **boilerplate** код
4. **IDE support** — Поддержка автодополнения и рефакторинга
5. **Тестируемость** — Генерируемый код легко тестировать
6. **Без зависимостей** — Не требует **runtime** зависимостей
7. **Широкая поддержка** — Работает с любыми **Java** фреймворками
8. **Конфигурируемость** — Гибкие настройки маппинга

### Основные паттерны использования

1. **DTO паттерн** — Маппинг между **entity** и **DTO**
2. **Builder паттерн** — Создание объектов через **builder**
3. **Update паттерн** — Обновление существующих объектов
4. **Collection паттерн** — Маппинг коллекций и массивов
5. **Context паттерн** — Передача дополнительной информации
6. **Inheritance паттерн** — Работа с наследованием

### Когда использовать **MapStruct**

**Рекомендуется:**
- **REST API** с **DTO** слоем
- Микросервисная архитектура
- **Enterprise** приложения
- Проекты с **complex domain model**
- Приложения с высокой нагрузкой

**Особенно полезно:**
- Для маппинга между **entity** и **DTO**
- При работе с внешними **API**
- В **CQRS** архитектуре
- При интеграции с фронтендом
- В **batch processing**

### Сравнение с альтернативами

| Библиотека | Преимущества | Недостатки |
|------------|-------------|------------|
| **MapStruct** | Быстрый, **type-safe**, без **reflection** | Только кодогенерация |
| **ModelMapper** | Простой **API**, **reflection** | Медленнее, **runtime** ошибки |
| **Dozer** | **XML** конфигурация, мощный | Сложная настройка |
| **Orika** | Высокая производительность | Большой размер |
| **Manual mapping** | Полный контроль | Много **boilerplate** |

**MapStruct** рекомендуется как основной инструмент для маппинга объектов в **enterprise Java** приложениях, особенно когда важны производительность и **type safety**.

---

[⬆️ Наверх](../)

