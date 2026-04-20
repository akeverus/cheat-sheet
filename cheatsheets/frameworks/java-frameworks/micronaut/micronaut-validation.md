---
title: "Micronaut: Validation — Bean Validation и Custom Validators"
description: "Полное руководство по валидации в Micronaut: Bean Validation, custom validators, группировка валидации и best practices"
tags:
  - micronaut
  - validation
  - bean-validation
  - jakarta-validation
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-http.md", "micronaut-data.md"]
updated: "2026-04-20"
related: ["micronaut-core.md", "micronaut-http.md"]
---

# Micronaut: Validation — Bean Validation и Custom Validators

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Validation](#настройка-validation)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Bean Validation](#bean-validation)
  - [Entity Validation](#entity-validation)
  - [Method Parameter Validation](#method-parameter-validation)
- [Custom Validators](#custom-validators)
  - [Custom Constraint Annotation](#custom-constraint-annotation)
  - [Custom Validator с Dependencies](#custom-validator-с-dependencies)
- [Validation Groups](#validation-groups)
  - [Определение Groups](#определение-groups)
  - [Использование Groups](#использование-groups)
- [HTTP Validation](#http-validation)
  - [Controller Validation](#controller-validation)
  - [Query Parameter Validation](#query-parameter-validation)
- [Error Handling](#error-handling)
  - [Validation Exception Handler](#validation-exception-handler)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте стандартные аннотации](#1-используйте-стандартные-аннотации)
  - [2. Создавайте custom validators для сложной логики](#2-создавайте-custom-validators-для-сложной-логики)
  - [3. Используйте validation groups](#3-используйте-validation-groups)
  - [4. Валидируйте на уровне контроллера](#4-валидируйте-на-уровне-контроллера)
  - [5. Обрабатывайте ошибки валидации](#5-обрабатывайте-ошибки-валидации)
- [Cross-field Validation](#cross-field-validation)
  - [Class-level Validation](#class-level-validation)
- [Custom Validation Messages](#custom-validation-messages)
  - [Message Interpolation](#message-interpolation)
  - [Internationalized Messages](#internationalized-messages)
- [Programmatic Validation](#programmatic-validation)
  - [Manual Validation](#manual-validation)
- [Validation in Reactive Code](#validation-in-reactive-code)
  - [Reactive Validation](#reactive-validation)
- [Validation Context](#validation-context)
  - [Custom Validation Context](#custom-validation-context)
- [Validation Order](#validation-order)
  - [Validation Ordering](#validation-ordering)
- [Custom Constraint Validators with Dependencies](#custom-constraint-validators-with-dependencies)
  - [Validator with Service Injection](#validator-with-service-injection)
- [Validation in HTTP Controllers](#validation-in-http-controllers)
  - [Request Validation](#request-validation)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет полную поддержку **Bean Validation** (Jakarta Validation) для валидации данных в приложениях. Это позволяет декларативно определять правила валидации и автоматически проверять данные.

### Основные возможности

- **Bean Validation**: Стандартная **Jakarta Bean Validation**
- **Custom Validators**: Создание собственных валидаторов
- **Validation Groups**: Группировка правил валидации
- **Method Validation**: Валидация параметров методов
- **Constraint Validation**: Валидация ограничений

## Настройка Validation

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.hibernate.validator:hibernate-validator")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  validation:
    enabled: true
```

## Bean Validation

### Entity Validation

```java
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

public class User {
    @NotNull
    @Min(1)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Min(18)
    @Max(100)
    private Integer age;

    @Valid
    @NotNull
    private Address address;

    // Getters and setters
}
```

### Method Parameter Validation

```java
import io.micronaut.validation.validator.Validated;
import jakarta.validation.constraints.*;
import jakarta.inject.Singleton;

@Validated
@Singleton
public class UserService {

    public User createUser(
            @NotBlank String name,
            @Email String email,
            @Min(18) @Max(100) Integer age) {
        return new User(name, email, age);
    }

    public void updateUser(
            @NotNull Long id,
            @Valid User user) {
        userRepository.update(id, user);
    }
}
```

## Custom Validators

### Custom Constraint Annotation

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmail.Validator.class)
public @interface ValidEmail {
    String message() default "Invalid email format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidEmail, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // null values handled by @NotNull
            }
            return value.contains("@") && value.contains(".");
        }
    }
}
```

### Custom Validator с Dependencies

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.inject.Singleton;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmail.Validator.class)
public @interface UniqueEmail {
    String message() default "Email already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Singleton
    class Validator implements ConstraintValidator<UniqueEmail, String> {
        private final UserRepository userRepository;

        public Validator(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            if (email == null) {
                return true;
            }
            return !userRepository.existsByEmail(email);
        }
    }
}
```

## Validation Groups

### Определение Groups

```java
public interface CreateGroup {}
public interface UpdateGroup {}

public class User {
    @NotNull(groups = UpdateGroup.class)
    private Long id;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @Email(groups = {CreateGroup.class, UpdateGroup.class})
    private String email;
}
```

### Использование Groups

```java
import jakarta.validation.Validator;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final Validator validator;

    public UserService(Validator validator) {
        this.validator = validator;
    }

    public User createUser(User user) {
        Set<ConstraintViolation<User>> violations =
            validator.validate(user, CreateGroup.class);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Set<ConstraintViolation<User>> violations =
            validator.validate(user, UpdateGroup.class);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
        return userRepository.update(user);
    }
}
```

## HTTP Validation

### Controller Validation

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Body;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;

@Validated
@Controller("/api/users")
public class UserController {

    @Post
    public User createUser(@Valid @Body User user) {
        return userService.createUser(user);
    }
}
```

### Query Parameter Validation

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import jakarta.validation.constraints.*;

@Controller("/api/users")
public class UserController {

    @Get("/search")
    public List<User> searchUsers(
            @QueryValue @NotBlank String name,
            @QueryValue @Min(18) @Max(100) Integer minAge) {
        return userService.searchUsers(name, minAge);
    }
}
```

## Error Handling

### Validation Exception Handler

```java
import io.micronaut.http.annotation.ControllerAdvice;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ValidationExceptionHandler {

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<Map<String, Object>> handleValidationException(
            ConstraintViolationException exception) {
        Map<String, Object> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }

        return HttpResponse.badRequest(Map.of("errors", errors));
    }
}
```

## Лучшие практики

### 1. Используйте стандартные аннотации

```java
// ✅ Хорошо
@NotBlank
@Email
private String email;
```

### 2. Создавайте custom validators для сложной логики

```java
// ✅ Хорошо
@UniqueEmail
private String email;
```

### 3. Используйте validation groups

```java
// ✅ Хорошо
@NotNull(groups = UpdateGroup.class)
private Long id;
```

### 4. Валидируйте на уровне контроллера

```java
// ✅ Хорошо
@Post
public User createUser(@Valid @Body User user) {
    // ...
}
```

### 5. Обрабатывайте ошибки валидации

```java
// ✅ Хорошо
@Error(exception = ConstraintViolationException.class)
public HttpResponse<?> handleValidationException(...) {
    // ...
}
```

## Cross-field Validation

### Class-level Validation

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPassword.Validator.class)
public @interface ValidPassword {
    String message() default "Password and confirmation must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidPassword, User> {
        @Override
        public boolean isValid(User user, ConstraintValidatorContext context) {
            if (user.getPassword() == null || user.getPasswordConfirmation() == null) {
                return true; // null values handled by @NotNull
            }
            return user.getPassword().equals(user.getPasswordConfirmation());
        }
    }
}

@ValidPassword
public class User {
    private String password;
    private String passwordConfirmation;

    // Getters and setters
}
```

## Custom Validation Messages

### Message Interpolation

```java
import jakarta.validation.constraints.*;

public class User {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Email must be a valid email address")
    private String email;

    @Min(value = 18, message = "Age must be at least {value}")
    @Max(value = 100, message = "Age must be at most {value}")
    private Integer age;
}
```

### Internationalized Messages

**ValidationMessages.properties:**

```properties
user.name.notblank=Имя не может быть пустым
user.email.email=Email должен быть валидным адресом
user.age.min=Возраст должен быть не менее {value}
```

## Programmatic Validation

### Manual Validation

```java
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.inject.Singleton;

@Singleton
public class ManualValidationService {
    private final Validator validator;

    public ManualValidationService(Validator validator) {
        this.validator = validator;
    }

    public void validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Validation failed:\n");
            for (ConstraintViolation<User> violation : violations) {
                message.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("\n");
            }
            throw new ValidationException(message.toString());
        }
    }
}
```

## Validation in Reactive Code

### Reactive Validation

```java
import io.micronaut.validation.validator.Validated;
import jakarta.validation.constraints.*;
import reactor.core.publisher.Mono;
import jakarta.inject.Singleton;

@Validated
@Singleton
public class ReactiveUserService {

    public Mono<User> createUser(
            @NotBlank String name,
            @Email String email,
            @Min(18) @Max(100) Integer age) {
        return Mono.fromCallable(() -> {
            User user = new User(name, email, age);
            return userRepository.save(user);
        });
    }
}
```

## Validation Context

### Custom Validation Context

```java
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<CustomConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean valid = value.length() >= 8;

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Value must be at least 8 characters long")
                .addConstraintViolation();
        }

        return valid;
    }
}
```

## Validation Order

### Validation Ordering

```java
import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, SecondGroup.class, ThirdGroup.class})
public interface OrderedValidation {}

public class User {
    @NotBlank(groups = Default.class)
    private String name;

    @Email(groups = SecondGroup.class)
    private String email;

    @Min(value = 18, groups = ThirdGroup.class)
    private Integer age;
}
```

## Custom Constraint Validators with Dependencies

### Validator with Service Injection

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.inject.Singleton;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUser.Validator.class)
public @interface ValidUser {
    String message() default "Invalid user";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Singleton
    class Validator implements ConstraintValidator<ValidUser, User> {
        private final UserRepository userRepository;

        public Validator(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public boolean isValid(User user, ConstraintValidatorContext context) {
            if (user == null) {
                return true;
            }
            return userRepository.existsById(user.getId());
        }
    }
}
```

## Validation in HTTP Controllers

### Request Validation

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Body;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;

@Validated
@Controller("/api/users")
public class UserController {

    @Post
    public User createUser(@Valid @Body User user) {
        return userService.create(user);
    }

    @Post("/bulk")
    public List<User> createUsers(@Valid @Body List<@Valid User> users) {
        return userService.createAll(users);
    }
}
```


## Заключение

**Micronaut Validation** предоставляет мощные инструменты для валидации данных. Поддержка **Bean Validation**, **custom validators**, **validation groups**, **method validation**, **HTTP validation**, **error handling**, **cross-field validation**, **custom messages**, **programmatic validation**, **reactive validation**, **validation context**, **validation ordering**, **validators with dependencies**, **HTTP request validation** и других продвинутых возможностей позволяет создавать надежные приложения с декларативной валидацией данных.

## Дополнительные ресурсы

- [**Micronaut Validation** Documentation](https://micronaut-projects.github.io/micronaut-validation/latest/guide/)
- [**Jakarta Bean Validation**](https://beanvalidation.org/2.0/)
- [**Hibernate Validator**](https://hibernate.org/validator/documentation/)
- [**Validation Best Practices**](https://www.baeldung.com/javax-validation)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
