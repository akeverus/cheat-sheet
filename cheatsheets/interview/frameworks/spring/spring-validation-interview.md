---
title: "Вопросы на собеседовании: Spring Validation"
description: "Bean Validation (JSR 380), @Valid vs @Validated, кастомные ConstraintValidator, группы валидации, кросс-field валидация, обработка ошибок"
tags:
  - interview
  - spring
  - spring-validation-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Validation"
  - "Spring Validation interview"
  - "Bean Validation interview"
prerequisites:
  - "[[spring-validation]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Validation`

`Spring Validation` сочетает два подхода: **Bean Validation** (JSR 380 / Jakarta Validation) — декларативная валидация через аннотации, и **Spring Validator interface** — программная валидация. Большинство вопросов на собеседованиях касаются Bean Validation в контексте Spring Boot.

## Полезные ссылки

### Официальная документация

- [Jakarta Validation Spec](https://jakarta.ee/specifications/bean-validation/3.0/) — спецификация
- [Hibernate Validator Reference](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/) — reference implementation
- [Spring Validation Reference](https://docs.spring.io/spring-framework/reference/core/validation.html) — Spring docs

### Baeldung tutorials

- [Validation in Spring Boot](https://www.baeldung.com/spring-boot-bean-validation) — основы
- [@Valid vs @Validated](https://www.baeldung.com/spring-valid-vs-validated) — ключевое отличие
- [Custom Validator](https://www.baeldung.com/spring-mvc-custom-validator)
- [Constraint Composition](https://www.baeldung.com/java-bean-validation-constraint-composition)
- [Method Constraints](https://www.baeldung.com/javax-validation-method-constraints)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Bean Validation и как подключить в Spring Boot?](#q1-что-такое-bean-validation-и-как-подключить-в-spring-boot)
- [Q2. Какие стандартные constraint-аннотации есть в Bean Validation?](#q2-какие-стандартные-constraint-аннотации-есть-в-bean-validation)
- [Q3. (!) Чем `@Valid` отличается от `@Validated`?](#q3-чем-valid-отличается-от-validated)
- [Q4. Как обработать ошибки валидации в контроллере?](#q4-как-обработать-ошибки-валидации-в-контроллере)

**Продвинутые техники**
- [Q5. Как работает каскадная валидация вложенных объектов?](#q5-как-работает-каскадная-валидация-вложенных-объектов)
- [Q6. (!) Что такое группы валидации и зачем они нужны?](#q6-что-такое-группы-валидации-и-зачем-они-нужны)
- [Q7. Как создать кастомную constraint-аннотацию?](#q7-как-создать-кастомную-constraint-аннотацию)
- [Q8. (!) Как реализовать кросс-field валидацию?](#q8-как-реализовать-кросс-field-валидацию)
- [Q9. Как создать stateful validator с инъекцией Spring-бинов?](#q9-как-создать-stateful-validator-с-инъекцией-spring-бинов)
- [Q10. Как работает композиция constraints?](#q10-как-работает-композиция-constraints)

**Валидация на сервисном уровне**
- [Q11. Как включить валидацию на уровне сервиса (не только контроллера)?](#q11-как-включить-валидацию-на-уровне-сервиса-не-только-контроллера)
- [Q12. Чем отличается валидация в Spring MVC от валидации на сервисном уровне?](#q12-чем-отличается-валидация-в-spring-mvc-от-валидации-на-сервисном-уровне)

**Сообщения и i18n**
- [Q13. Как кастомизировать сообщения об ошибках валидации?](#q13-как-кастомизировать-сообщения-об-ошибках-валидации)

**Spring Validator interface**
- [Q14. Что такое Spring Validator interface и когда его использовать?](#q14-что-такое-spring-validator-interface-и-когда-его-использовать)

**Практика**
- [Q15. Как валидировать элементы коллекции в контроллере?](#q15-как-валидировать-элементы-коллекции-в-контроллере)
- [Q16. Типичные ошибки при работе с Bean Validation?](#q16-типичные-ошибки-при-работе-с-bean-validation)

---

## Q1. Что такое Bean Validation и как подключить в Spring Boot?

**Bean Validation (JSR 380)** — стандарт Java (теперь Jakarta) для декларативной валидации через аннотации. Hibernate Validator — референсная реализация.

**Подключение в Spring Boot:**

```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

```kotlin
// Gradle
implementation("org.springframework.boot:spring-boot-starter-validation")
```

Этот стартер включает:
- `hibernate-validator` — реализация Bean Validation
- `jakarta.validation:jakarta.validation-api` — API
- Интеграцию с Spring MVC (автоматическое срабатывание на `@Valid`/`@Validated`)

**Базовый пример:**

```java
public class CreateUserRequest {
    @NotBlank
    private String name;

    @Email
    @NotNull
    private String email;

    @Min(18)
    private int age;
}

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userService.create(req));
    }
}
```

При нарушении constraint Spring автоматически возвращает `400 Bad Request`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q2. Какие стандартные constraint-аннотации есть в Bean Validation? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

**Null/empty проверки:**

| Аннотация | Проходит | Не проходит |
|---|---|---|
| `@NotNull` | любой не-null | `null` |
| `@NotEmpty` | "text", [1] | `null`, "", [] |
| `@NotBlank` | "text" | `null`, "", " " |

**Числовые:**

| Аннотация | Описание |
|---|---|
| `@Min(value)` | >= value |
| `@Max(value)` | <= value |
| `@Positive` | > 0 |
| `@PositiveOrZero` | >= 0 |
| `@Negative` | < 0 |
| `@Digits(integer, fraction)` | ограничение на цифры |
| `@DecimalMin / @DecimalMax` | для BigDecimal |

**Строковые:**

| Аннотация | Описание |
|---|---|
| `@Size(min, max)` | длина строки, размер коллекции |
| `@Pattern(regexp)` | regex |
| `@Email` | email формат |

**Даты:**

| Аннотация | Описание |
|---|---|
| `@Past` | дата в прошлом |
| `@PastOrPresent` | в прошлом или сейчас |
| `@Future` | в будущем |
| `@FutureOrPresent` | в будущем или сейчас |

```java
public class EventRequest {
    @NotBlank
    private String title;

    @Size(max = 500)
    private String description;

    @Future
    private LocalDateTime startTime;

    @Positive
    private int maxParticipants;

    @Pattern(regexp = "^[A-Z]{2}-\\d{4}$", message = "Код должен быть вида 'AB-1234'")
    private String code;
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q3. Чем `@Valid` отличается от `@Validated`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Параметр | `@Valid` (javax/jakarta) | `@Validated` (Spring) |
|---|---|---|
| Источник | Jakarta Validation API | Spring Framework |
| Группы валидации | Нет | Да — `@Validated(Group.class)` |
| Каскадная валидация | Да | Нет (только маркер) |
| Применимость | Поля, параметры, методы | Класс, параметры, методы |
| Использование в MVC | Для request body, параметров | Для request body + группы |

```java
// @Valid — каскадная валидация вложенного объекта
public class OrderRequest {
    @Valid   // валидировать Address тоже
    @NotNull
    private Address address;
}

// @Validated — с группами в контроллере
@PutMapping("/{id}")
public ResponseEntity<User> update(
        @Validated(UpdateGroup.class) @RequestBody UserRequest req) { ... }

// @Validated на классе — включает метод-уровень валидацию
@Service
@Validated
public class UserService {
    public User create(@Valid @NotNull UserRequest req) { ... }
}
```

**Итог:** `@Valid` для каскадной валидации, `@Validated` для групп и метод-уровня в сервисах.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. Как обработать ошибки валидации в контроллере? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Автоматически** через `@ExceptionHandler` в `@ControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
            .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

    // Для @Validated на сервисном уровне
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        List<FieldError> errors = ex.getConstraintViolations().stream()
            .map(v -> new FieldError(
                v.getPropertyPath().toString(),
                v.getMessage()))
            .toList();

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }
}
```

`MethodArgumentNotValidException` — для `@RequestBody`/`@ModelAttribute`.  
`ConstraintViolationException` — для `@RequestParam`, `@PathVariable`, сервисный уровень.

**Вариант через BindingResult:**

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserRequest req, BindingResult result) {
    if (result.hasErrors()) {
        return ResponseEntity.badRequest().body(result.getAllErrors());
    }
    return ResponseEntity.ok(userService.create(req));
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Как работает каскадная валидация вложенных объектов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`@Valid` на поле типа другого объекта включает валидацию и этого объекта:

```java
public class OrderRequest {
    @NotNull
    private String orderRef;

    @Valid  // запустить валидацию Address
    @NotNull
    private Address shippingAddress;

    @Valid  // валидировать каждый элемент списка
    @NotEmpty
    private List<OrderItemRequest> items;
}

public class Address {
    @NotBlank private String street;
    @NotBlank private String city;
    @Pattern(regexp = "\\d{6}") private String zip;
}
```

Без `@Valid` на поле `shippingAddress` — validator проверит только что `address != null`, но не провалидирует поля внутри `Address`.

**Глубокая вложенность:** `@Valid` работает рекурсивно — если `Address` содержит поле с `@Valid`, оно тоже будет провалидировано.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое группы валидации и зачем они нужны? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Группы позволяют применять разные constraints в зависимости от контекста (создание vs обновление):

```java
// Объявляем группы
public interface OnCreate {}
public interface OnUpdate {}

public class UserRequest {
    @Null(groups = OnCreate.class)       // при создании id должен быть null
    @NotNull(groups = OnUpdate.class)    // при обновлении id обязателен
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(groups = OnCreate.class)   // только при создании
    private String password;
}

// Контроллер
@PostMapping
public ResponseEntity<?> create(
        @Validated(OnCreate.class) @RequestBody UserRequest req) { ... }

@PutMapping("/{id}")
public ResponseEntity<?> update(
        @Validated(OnUpdate.class) @RequestBody UserRequest req) { ... }
```

`Default.class` — группа по умолчанию: constraint без явной группы принадлежит ей.

`@GroupSequence` — определяет порядок проверки групп (следующая группа проверяется только если предыдущая прошла):

```java
@GroupSequence({Default.class, OnCreate.class, OnUpdate.class})
public interface OrderedChecks {}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как создать кастомную constraint-аннотацию? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Шаг 1 — объявить аннотацию:**

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface Phone {
    String message() default "Неверный формат телефона";
    Class<?>[] groups() default {};         // обязательный элемент
    Class<? extends Payload>[] payload() default {};  // обязательный элемент

    String regexp() default "^\\+7\\d{10}$";
}
```

**Шаг 2 — реализовать `ConstraintValidator<A, T>`:**

```java
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private String regexp;

    @Override
    public void initialize(Phone annotation) {
        this.regexp = annotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;  // null проверяет @NotNull отдельно
        return value.matches(regexp);
    }
}
```

**Использование:**

```java
public class UserRequest {
    @Phone
    private String phone;

    @Phone(regexp = "^\\d{7}$", message = "Городской телефон — 7 цифр")
    private String cityPhone;
}
```

**Правило:** в `isValid()` не проверять `null` — за это отвечает `@NotNull`. Если значение `null` → `return true` (constraint не нарушен).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как реализовать кросс-field валидацию? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Когда нужно проверить несколько полей вместе (например, `password == confirmPassword`), аннотацию вешают на **класс**:

```java
// Аннотация на уровне класса
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
    String message() default "Пароли не совпадают";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String password();
    String confirmPassword();
}

// Validator
public class PasswordMatchValidator
        implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatch annotation) {
        this.passwordField = annotation.password();
        this.confirmPasswordField = annotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
        try {
            Object pass = new BeanWrapperImpl(obj).getPropertyValue(passwordField);
            Object confirm = new BeanWrapperImpl(obj).getPropertyValue(confirmPasswordField);
            boolean valid = Objects.equals(pass, confirm);
            if (!valid) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
                   .addPropertyNode(confirmPasswordField)
                   .addConstraintViolation();
            }
            return valid;
        } catch (Exception e) {
            return false;
        }
    }
}

// Применение
@PasswordMatch(password = "password", confirmPassword = "confirmPassword")
public class RegisterRequest {
    @NotBlank private String password;
    @NotBlank private String confirmPassword;
}
```

`addPropertyNode` — привязывает ошибку к конкретному полю, а не к объекту.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Как создать stateful validator с инъекцией Spring-бинов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Hibernate Validator по умолчанию создаёт validators через `new`. В Spring Boot — через `ConstraintValidatorFactory`, которая использует Spring-контекст.

```java
@Component  // Spring управляет жизненным циклом
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired  // DI работает!
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext ctx) {
        if (email == null) return true;
        return !userRepository.existsByEmail(email);
    }
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email уже зарегистрирован";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Работает в Spring Boot автоматически:** `LocalValidatorFactoryBean` (autoconfigure) использует `SpringConstraintValidatorFactory`, которая делегирует создание Spring-контексту.

**Важно:** если используется `javax.validation.Validator` напрямую (не через Spring) — DI не работает.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как работает композиция constraints? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`@Constraint(validatedBy = {})` без валидатора + `@ReportAsSingleViolation` = мета-аннотация из нескольких constraints:

```java
@NotNull
@NotBlank
@Size(min = 2, max = 50)
@Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ ]+$")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})   // нет своего validator
@ReportAsSingleViolation        // одно сообщение вместо нескольких
public @interface PersonName {
    String message() default "Некорректное имя";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Использование:
public class UserRequest {
    @PersonName
    private String firstName;

    @PersonName
    private String lastName;
}
```

Без `@ReportAsSingleViolation` — каждый нарушенный constraint генерирует отдельную ошибку.  
С `@ReportAsSingleViolation` — при любом нарушении одно сообщение из мета-аннотации.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как включить валидацию на уровне сервиса (не только контроллера)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

По умолчанию Bean Validation работает только в Spring MVC (через `HandlerMethodArgumentResolver`). Для сервисного уровня нужен `MethodValidationPostProcessor`:

```java
@Configuration
public class ValidationConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

В Spring Boot он добавляется автоматически через `ValidationAutoConfiguration`.

Затем пометить класс `@Validated`:

```java
@Service
@Validated  // AOP-прокси для валидации методов
public class UserService {

    public User create(@Valid @NotNull CreateUserRequest req) {
        return repository.save(mapper.toEntity(req));
    }

    public User findById(@Positive Long id) {
        return repository.findById(id).orElseThrow();
    }
}
```

При нарушении — `ConstraintViolationException` (не `MethodArgumentNotValidException`).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Чем отличается валидация в Spring MVC от валидации на сервисном уровне? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Аспект | Spring MVC (@Valid в контроллере) | Сервисный уровень (@Validated + MethodValidation) |
|---|---|---|
| Механизм | `HandlerMethodArgumentResolver` | AOP proxy (`MethodValidationPostProcessor`) |
| Исключение | `MethodArgumentNotValidException` | `ConstraintViolationException` |
| `BindingResult` | Можно перехватить в методе | Недоступен |
| Применимость | Только в контроллерах | Любой Spring-бин |
| Self-invocation | N/A | Не работает (AOP проблема) |

**Рекомендация:** валидацию лучше проводить на контроллерном уровне (fail fast), но для внутренних API и сервисов `@Validated` полезен как дополнительный контракт.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Как кастомизировать сообщения об ошибках валидации? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Через `message` в аннотации:**

```java
@NotBlank(message = "Имя обязательно для заполнения")
@Size(min = 2, max = 50, message = "Имя должно быть от {min} до {max} символов")
private String name;
```

**Через `ValidationMessages.properties`** (`src/main/resources/ValidationMessages.properties`):

```properties
user.name.required=Имя пользователя обязательно
user.name.size=Имя должно быть от {min} до {max} символов
user.email.invalid=Некорректный email
```

```java
@NotBlank(message = "{user.name.required}")
@Size(min = 2, max = 50, message = "{user.name.size}")
private String name;
```

**Spring-специфика:** Spring Boot интегрирует `ValidationMessages.properties` с `MessageSource` — можно использовать i18n.

**Interpolation в сообщениях:**
- `{min}`, `{max}`, `{value}` — атрибуты аннотации
- `${validatedValue}` — проверяемое значение (EL)
- `${formatter.format('%1$.2f', validatedValue)}` — форматирование через EL


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Что такое Spring Validator interface и когда его использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`org.springframework.validation.Validator` — Spring-специфический подход к валидации (альтернатива Bean Validation):

```java
@Component
public class OrderValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Order.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Order order = (Order) target;

        if (order.getItems() == null || order.getItems().isEmpty()) {
            errors.rejectValue("items", "order.items.empty", "Заказ должен содержать товары");
        }

        if (order.getDeliveryDate() != null &&
            order.getDeliveryDate().isBefore(LocalDate.now().plusDays(1))) {
            errors.rejectValue("deliveryDate", "order.delivery.too-soon",
                "Дата доставки должна быть минимум завтра");
        }
    }
}
```

Использование в контроллере:

```java
@RestController
public class OrderController {

    @Autowired private OrderValidator validator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }
}
```

**Когда использовать Spring Validator:**
- Сложная бизнес-логика, требующая сервисных зависимостей
- Валидация с состоянием (проверка БД)
- Когда нужна связь с `BindingResult` напрямую
- Легаси проекты до Bean Validation

**Предпочтение:** в новых проектах — Bean Validation с кастомными validators (stateful — через Spring DI). Spring Validator — для сложных случаев.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Как валидировать элементы коллекции в контроллере? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Вариант 1 — через `@RequestBody` с коллекцией:**

```java
// DTO обёртка
public class BulkRequest {
    @Valid
    @NotEmpty
    private List<CreateUserRequest> users;
}

@PostMapping("/bulk")
public ResponseEntity<?> createBulk(@Valid @RequestBody BulkRequest req) { ... }
```

**Вариант 2 — напрямую List с `@Validated` (Spring Boot 3+):**

```java
@Validated
@RestController
public class UserController {

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulk(
            @Valid @RequestBody @NotEmpty List<@Valid CreateUserRequest> users) { ... }
}
```

**Вариант 3 — `@RequestParam`-коллекция:**

```java
@Validated
@RestController
public class SearchController {

    @GetMapping("/search")
    public List<User> search(
            @RequestParam @NotEmpty @Size(max = 100) List<@NotBlank String> tags) { ... }
}
```

Валидация элементов коллекции через `@Valid` перед каждым типом в generic (Java 11+).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Типичные ошибки при работе с Bean Validation? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

| Симптом | Причина | Решение |
|---|---|---|
| Валидация не срабатывает | Забыли `@Valid`/`@Validated` | Добавить аннотацию на параметр |
| Вложенные объекты не валидируются | Нет `@Valid` на поле | Добавить `@Valid` на поле вложенного объекта |
| `ConstraintViolationException` вместо 400 | Нет `@ControllerAdvice` | Добавить `@ExceptionHandler(ConstraintViolationException.class)` |
| Кастомный validator не получает DI | Не через Spring-контекст | Добавить `@Component`, проверить autoconfigure |
| Self-invocation не валидируется | AOP проблема | Вынести в отдельный бин или inject self |
| Groups не работают | Используется `@Valid` вместо `@Validated` | Для групп нужен `@Validated(Group.class)` |
| `null` не проходит `@NotBlank` | `@NotBlank` включает проверку null | Нормальное поведение; `@NotBlank` = `@NotNull` + не пусто |

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring MVC](spring-mvc-interview.md) — `@Valid` в контроллерах, `BindingResult`, обработка ошибок ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Spring Boot](spring-boot-interview.md) — `spring-boot-starter-validation`, автоконфигурация `ValidationAutoConfiguration`
- [Spring Framework](spring-framework-interview.md) — `@Validated` как AOP-триггер, `MethodValidationPostProcessor`
- [Spring AOP](spring-aop-interview.md) — `@Validated` работает через AOP-прокси; self-invocation проблема
- [Spring Security](spring-security-interview.md) — `@PreAuthorize` рядом с `@Validated` — порядок применения
- [Java Annotations](../../programming-languages/java/java-annotations-interview.md) — `@Constraint`, `@Target`, `@Retention` для кастомных constraints
- [Java Exceptions](../../programming-languages/java/java-exceptions-interview.md) — `ConstraintViolationException`, `MethodArgumentNotValidException`
- [HTTP & REST](../../api/http-rest-interview.md) — возврат `400 Bad Request` с деталями ошибок, RFC 7807 Problem Details
- [Spring Data JPA](spring-data-jpa-interview.md) — валидация Entity перед persist через `@PrePersist`/Hibernate Validator
- [Unit Testing](../../testing/unit-testing-interview.md) — тестирование валидационных ограничений через `ValidatorFactory`
- [Шпаргалка: Spring Validation: Полное руководство по](../../../frameworks/java-frameworks/spring/spring-validation.md) — теория
