---
title: "Bean Validation (JSR-380 / Jakarta Validation 3.0)"
description: "Шпаргалка по Bean Validation: аннотации, группы, кастомные constraint, интеграция со Spring, i18n-сообщения и programmatic Validator API."
tags:
  - libraries
  - java
  - validation
  - jakarta
  - spring
difficulty: "intermediate"
updated: "2026-04-20"
---
# Bean Validation (JSR-380 / Jakarta Validation 3.0)

Bean Validation — стандарт Jakarta EE для декларативной проверки объектов через аннотации на полях, геттерах, параметрах и возвращаемых значениях методов. Референсная имплементация — Hibernate Validator. Спецификация 3.0 переехала в пакет `jakarta.validation` (Jakarta EE 9+), 2.0 и ниже — `javax.validation`.

На практике используется в каждом Spring Boot сервисе для валидации DTO в контроллерах, параметров сервисов и сущностей JPA. В связке со Spring MVC срабатывает через `@Valid` / `@Validated` и автоматически формирует `400 Bad Request` при нарушениях. Подробнее об интеграции см. в [[spring-validation]] и [[spring-rest]].

## Полезные ссылки

### Официальная документация
- [Jakarta Validation 3.0 Spec](https://jakarta.ee/specifications/bean-validation/3.0/) — спецификация
- [Hibernate Validator Reference](https://docs.jboss.org/hibernate/validator/8.0/reference/en-US/html_single/) — reference guide
- [Baeldung: Java Bean Validation Basics](https://www.baeldung.com/javax-validation) — базовая статья
- [Spring Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html) — интеграция со Spring

### См. также
- [[spring-validation|spring-validation]] — Spring Validation и интеграция
- [[spring-rest|spring-rest]] — валидация в REST-контроллерах
- [[spring-boot|spring-boot]] — автоконфигурация Hibernate Validator
- [[java-jackson|java-jackson]] — десериализация и валидация входных JSON
- [[spring-data-jpa|spring-data-jpa]] — валидация сущностей перед persist
- [[java-basics|java-basics]] — базовые концепции Java

## Содержание

- [Установка](#установка)
- [Встроенные аннотации](#встроенные-аннотации)
  - [Null-safety](#null-safety)
  - [Размер и длина](#размер-и-длина)
  - [Числа](#числа)
  - [Строки и форматы](#строки-и-форматы)
  - [Даты](#даты)
- [Композиция и вложенные объекты](#композиция-и-вложенные-объекты)
- [Группы валидации](#группы-валидации)
- [Custom constraint](#custom-constraint)
- [Интеграция со Spring](#интеграция-со-spring)
  - [Валидация в контроллерах](#валидация-в-контроллерах)
  - [Method-level validation](#method-level-validation)
  - [Обработка ошибок](#обработка-ошибок)
- [Сообщения об ошибках и i18n](#сообщения-об-ошибках-и-i18n)
- [Programmatic Validator API](#programmatic-validator-api)
- [Cross-field валидация](#cross-field-валидация)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)

## Установка

```xml
<!-- Maven: Spring Boot starter включает Hibernate Validator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

```gradle
// Gradle
implementation("org.springframework.boot:spring-boot-starter-validation")

// Или без Spring
implementation("jakarta.validation:jakarta.validation-api:3.0.2")
implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
implementation("org.glassfish:jakarta.el:4.0.2")
```

Для Spring Boot 3.x (Jakarta EE 9+) используется пакет `jakarta.validation.*`. Для старых версий — `javax.validation.*`.

## Встроенные аннотации

### Null-safety

| Аннотация | Значение | Применимо к |
|-----------|----------|-------------|
| `@NotNull` | не `null` | любой тип |
| `@Null` | должен быть `null` | любой тип |
| `@NotEmpty` | не `null` и размер > 0 | `String`, `Collection`, `Map`, массив |
| `@NotBlank` | не `null` и содержит хотя бы один не-whitespace символ | только `String` |

```java
public class UserDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;   // "  " не пройдёт

    @NotEmpty
    private List<String> roles;
}
```

**Разница:** `@NotNull` не проверяет длину, `@NotEmpty` требует `size > 0`, `@NotBlank` ещё и запрещает строку из одних пробелов.

### Размер и длина

```java
@Size(min = 3, max = 50)
private String username;

@Size(max = 1000)
private String description;

@Size(min = 1, max = 10)
private List<Tag> tags;
```

`@Size` работает для `String`, `Collection`, `Map`, массивов. Для чисел используйте `@Min` / `@Max` / `@Digits`.

### Числа

| Аннотация | Описание |
|-----------|----------|
| `@Min(5)` | целое ≥ 5 |
| `@Max(100)` | целое ≤ 100 |
| `@DecimalMin("0.01")` | decimal ≥ значения (строка — для точности) |
| `@DecimalMax("9999.99")` | decimal ≤ значения |
| `@Positive` | > 0 |
| `@PositiveOrZero` | ≥ 0 |
| `@Negative` | < 0 |
| `@NegativeOrZero` | ≤ 0 |
| `@Digits(integer=5, fraction=2)` | число с ≤ 5 цифрами до и ≤ 2 после точки |

```java
public class PriceDto {
    @Positive
    private BigDecimal amount;

    @Min(0) @Max(100)
    private int discountPercent;

    @Digits(integer = 6, fraction = 2)
    private BigDecimal total;
}
```

### Строки и форматы

```java
@Email
private String email;

@Pattern(regexp = "^[A-Z]{2}\\d{10}$", message = "Неверный формат паспорта")
private String passportNumber;

@Pattern(regexp = "^\\+7\\d{10}$")
private String phone;
```

`@Email` использует встроенный regex Hibernate Validator; для строгой RFC 5322 лучше свой `@Pattern` или `javax.mail.internet.InternetAddress`.

### Даты

| Аннотация | Значение |
|-----------|----------|
| `@Past` | строго в прошлом |
| `@PastOrPresent` | в прошлом или сейчас |
| `@Future` | строго в будущем |
| `@FutureOrPresent` | в будущем или сейчас |

```java
public class EventDto {
    @FutureOrPresent
    private LocalDate startDate;

    @Past
    private LocalDate birthDate;
}
```

Работают с `LocalDate`, `LocalDateTime`, `Instant`, `ZonedDateTime`, `Date`, `Calendar`.

## Композиция и вложенные объекты

Чтобы провалидировать поля вложенного объекта, поставь `@Valid`:

```java
public class OrderDto {
    @NotNull
    private Long customerId;

    @Valid  // рекурсивная валидация
    @NotNull
    private AddressDto address;

    @Valid
    @NotEmpty
    private List<@Valid OrderItemDto> items;  // элементы коллекции тоже валидируются
}

public class AddressDto {
    @NotBlank
    private String street;

    @Pattern(regexp = "\\d{6}")
    private String zipCode;
}
```

**Важно:** без `@Valid` на поле `address` Hibernate Validator проверит только сам факт `@NotNull`, но не полезет внутрь `AddressDto`.

Для generics (Java 8+) аннотация ставится на тип: `List<@Valid @NotNull OrderItemDto>` — валидация каждого элемента плюс запрет `null` в списке.

## Группы валидации

Группы нужны, когда один DTO валидируется по-разному в разных сценариях (создание vs обновление).

```java
// Маркер-интерфейсы
public interface OnCreate {}
public interface OnUpdate {}

public class UserDto {
    @Null(groups = OnCreate.class)        // при создании id не передаём
    @NotNull(groups = OnUpdate.class)     // при обновлении — обязателен
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(groups = OnCreate.class)    // пароль обязателен только при регистрации
    @Size(min = 8, groups = {OnCreate.class, OnUpdate.class})
    private String password;
}
```

В Spring MVC группы подключаются через `@Validated`:

```java
@PostMapping("/users")
public UserDto create(@Validated(OnCreate.class) @RequestBody UserDto dto) { ... }

@PutMapping("/users/{id}")
public UserDto update(@Validated(OnUpdate.class) @RequestBody UserDto dto) { ... }
```

**Default group** срабатывает, если `groups` не указана. Чтобы включить её вместе со своей — наследуйся от `jakarta.validation.groups.Default`:

```java
public interface OnUpdate extends Default {}
// теперь @Validated(OnUpdate.class) проверит и OnUpdate, и все аннотации без groups
```

### GroupSequence — упорядоченные проверки

```java
@GroupSequence({Basic.class, Advanced.class})
public interface OrderedChecks {}
```

Сначала проверяется `Basic`, и только если он прошёл — `Advanced`. Полезно когда тяжёлые проверки не имеет смысла запускать, если базовые провалились.

## Custom constraint

Когда встроенных аннотаций мало — создаём свою.

### Шаг 1: аннотация

```java
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface ValidPhone {
    String message() default "Неверный формат телефона";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String country() default "RU";  // кастомные параметры
}
```

Обязательный контракт: методы `message()`, `groups()`, `payload()` — без них аннотация не примется.

### Шаг 2: валидатор

```java
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private String country;

    @Override
    public void initialize(ValidPhone annotation) {
        this.country = annotation.country();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true;  // @NotNull — отдельная аннотация

        return switch (country) {
            case "RU" -> value.matches("^\\+7\\d{10}$");
            case "US" -> value.matches("^\\+1\\d{10}$");
            default -> false;
        };
    }
}
```

**Правило:** `null` считается валидным — пусть за это отвечает `@NotNull`. Это convention, нарушение усложняет композицию.

### Шаг 3: использование

```java
public class ContactDto {
    @ValidPhone(country = "RU")
    private String phone;
}
```

### Динамическое сообщение

```java
@Override
public boolean isValid(String value, ConstraintValidatorContext ctx) {
    if (value == null) return true;
    if (!value.startsWith("+")) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate("Телефон должен начинаться с +")
            .addConstraintViolation();
        return false;
    }
    return true;
}
```

## Интеграция со Spring

Spring Boot автоконфигурирует `LocalValidatorFactoryBean` при наличии `spring-boot-starter-validation`. Использует Hibernate Validator под капотом и регистрирует бины `Validator` и `MethodValidationPostProcessor`. Базовая настройка описана в [[spring-boot]].

### Валидация в контроллерах

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto dto) {
        return userService.create(dto);
    }

    @GetMapping
    public Page<UserDto> search(
        @RequestParam @NotBlank String query,
        @RequestParam @Min(0) int page,
        @RequestParam @Min(1) @Max(100) int size
    ) {
        return userService.search(query, page, size);
    }
}
```

Для параметров запроса (`@RequestParam`, `@PathVariable`) нужна `@Validated` на уровне класса:

```java
@RestController
@Validated
public class UserController { ... }
```

### Method-level validation

```java
@Service
@Validated
public class OrderService {

    public Order create(@Valid CreateOrderDto dto) { ... }

    @NotNull
    public Order findById(@NotNull @Positive Long id) { ... }
}
```

Работает благодаря AOP-прокси через `MethodValidationPostProcessor`. `@Valid` на параметре — рекурсивная проверка объекта, обычные аннотации — на самом параметре или возвращаемом значении.

### Обработка ошибок

Spring автоматически бросает `MethodArgumentNotValidException` (для `@RequestBody`) или `ConstraintViolationException` (для method-level). Свой обработчик:

```java
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBody(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (a, b) -> a
            ));
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleParams(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                v -> v.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", errors));
    }
}
```

В Spring Boot 3.2+ по умолчанию включён `ProblemDetail` (RFC 7807) — встроенные обработчики возвращают структурированный JSON без ручного кода.

## Сообщения об ошибках и i18n

### Inline-сообщения

```java
@NotBlank(message = "Имя обязательно")
private String name;
```

### Через `messages.properties`

Файл `src/main/resources/ValidationMessages.properties`:

```properties
user.name.required=Имя пользователя обязательно
user.email.invalid=Некорректный email: ${validatedValue}
user.age.range=Возраст должен быть от {min} до {max}, получено {validatedValue}
```

```java
public class UserDto {
    @NotBlank(message = "{user.name.required}")
    private String name;

    @Email(message = "{user.email.invalid}")
    private String email;

    @Min(value = 18, message = "{user.age.range}")
    @Max(value = 120, message = "{user.age.range}")
    private int age;
}
```

**Плейсхолдеры:**
- `{min}`, `{max}`, `{value}`, `{regexp}` — параметры самой аннотации
- `${validatedValue}` — фактическое значение (EL-expression)

### Локализация

Для нескольких языков — `ValidationMessages_ru.properties`, `ValidationMessages_en.properties`. Hibernate Validator выбирает файл по текущей локали (`LocaleContextHolder` в Spring).

Кастомный `MessageSource` для слияния со стандартным:

```java
@Bean
public LocalValidatorFactoryBean validator(MessageSource messageSource) {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource);
    return bean;
}
```

## Programmatic Validator API

Когда декларативно не получается — используем `Validator` напрямую:

```java
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();

UserDto dto = new UserDto(null, "", "bad@");
Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

for (ConstraintViolation<UserDto> v : violations) {
    System.out.println(v.getPropertyPath() + ": " + v.getMessage());
}
```

В Spring — инжектим:

```java
@Service
@RequiredArgsConstructor
public class CustomValidationService {
    private final Validator validator;

    public List<String> validate(Object obj) {
        return validator.validate(obj).stream()
            .map(v -> v.getPropertyPath() + " " + v.getMessage())
            .toList();
    }
}
```

### Валидация одного свойства

```java
Set<ConstraintViolation<UserDto>> v = validator.validateProperty(dto, "email");
```

### Валидация значения без объекта

```java
Set<ConstraintViolation<UserDto>> v = validator.validateValue(UserDto.class, "email", "test");
```

## Cross-field валидация

Встроенных аннотаций для сравнения двух полей нет — делаем class-level constraint.

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatches {
    String message() default "Пароли не совпадают";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatches, RegisterDto> {
    @Override
    public boolean isValid(RegisterDto dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;
        boolean ok = Objects.equals(dto.getPassword(), dto.getPasswordConfirm());
        if (!ok) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                .addPropertyNode("passwordConfirm")  // привязка ошибки к конкретному полю
                .addConstraintViolation();
        }
        return ok;
    }
}

@PasswordMatches
public class RegisterDto {
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String passwordConfirm;
}
```

## Лучшие практики

- **Валидируй DTO, а не entity.** Entity валидирует JPA перед persist (см. [[spring-data-jpa]]), но основной контроль — на границе API.
- **Не смешивай бизнес-правила и формат.** Bean Validation — для формата (длина, regex, not-null). Бизнес-правила (уникальность email в БД, баланс > 0) — в сервисе.
- **`@NotBlank` для `String`, `@NotEmpty` для коллекций, `@NotNull` для чисел/boxed.** Частая ошибка: `@NotEmpty` на `int` — не скомпилируется в runtime.
- **Null — не ошибка в `ConstraintValidator`.** Композируется через `@NotNull` отдельно.
- **Используй `@Validated` на классе для method-level валидации.** Без неё аннотации на параметрах сервиса игнорируются.
- **Группы только когда реально разный сценарий.** Часто проще два DTO — `CreateUserDto` и `UpdateUserDto`.
- **Параметры в сообщениях через `{name}`, значение — через `${validatedValue}`.** Простой `${}` без валидной EL выражения упадёт.
- **ProblemDetail (RFC 7807) для новых API.** Spring Boot 3.2+ делает это автоматически.

## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| Аннотации на параметрах сервиса не срабатывают | Нет `@Validated` на классе | Добавить `@Validated` над классом сервиса |
| `@Valid` на `@PathVariable` игнорируется | Нужна `@Validated` на классе контроллера | Добавить `@Validated` и поймать `ConstraintViolationException` |
| Сообщения из `ValidationMessages.properties` не применяются | Файл не в classpath / опечатка в ключе | Проверить `src/main/resources/ValidationMessages.properties`, ключ в `{}` |
| Валидация вложенного объекта не запускается | Нет `@Valid` на поле | Добавить `@Valid` перед типом вложенного объекта |
| `jakarta.el.ExpressionFactory not found` | Отсутствует `jakarta.el` | Добавить `jakarta.el:4.0.2` (Spring Boot starter включает автоматически) |
| `NoSuchMethodError: javax.validation.*` в Boot 3 | Старая зависимость с `javax.*` | Перейти на `jakarta.validation.*`, обновить библиотеки |
| `MethodArgumentNotValidException` возвращает 500 вместо 400 | Кастомный handler перехватывает все исключения | Явно обработать `MethodArgumentNotValidException` 400 |
| Пустая строка `""` проходит `@NotNull` | `@NotNull` проверяет только `null` | Использовать `@NotBlank` для `String` |

## См. также

- [[spring-validation|spring-validation]] — валидация в Spring Framework
- [[spring-boot|spring-boot]] — автоконфигурация Validator
- [[spring-rest|spring-rest]] — валидация REST-контроллеров
- [[java-jackson|java-jackson]] — десериализация JSON перед валидацией
- [[spring-data-jpa|spring-data-jpa]] — валидация сущностей в JPA
- [[java-mapstruct|java-mapstruct]] — маппинг DTO entity
- [[java-lombok|java-lombok]] — генерация геттеров/сеттеров для DTO
- [[java-exceptions|java-exceptions]] — обработка ошибок валидации
