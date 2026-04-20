---
title: "Quarkus: Validation — Bean Validation"
description: "Полное руководство по валидации в Quarkus: Bean Validation, custom validators, groups, method validation и best practices"
tags:
  - quarkus
  - validation
  - bean-validation
  - jakarta-validation
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-rest.md"]
updated: "2026-04-20"
related: ["quarkus-core.md", "quarkus-rest.md"]
---

# Quarkus: Validation — Bean Validation

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Basic Validation](#basic-validation)
  - [Field Validation](#field-validation)
  - [Method Validation](#method-validation)
- [REST Endpoint Validation](#rest-endpoint-validation)
  - [Request Validation](#request-validation)
  - [Path Parameter Validation](#path-parameter-validation)
  - [Query Parameter Validation](#query-parameter-validation)
- [Custom Validators](#custom-validators)
  - [Custom Constraint](#custom-constraint)
- [Validation Groups](#validation-groups)
  - [Group Definition](#group-definition)
- [Constraint Composition](#constraint-composition)
  - [Composed Constraints](#composed-constraints)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте валидацию на всех уровнях](#1-используйте-валидацию-на-всех-уровнях)
  - [2. Создавайте кастомные валидаторы для сложных правил](#2-создавайте-кастомные-валидаторы-для-сложных-правил)
  - [3. Используйте группы для разных сценариев](#3-используйте-группы-для-разных-сценариев)
  - [4. Валидируйте все входные данные](#4-валидируйте-все-входные-данные)
- [Advanced Validation](#advanced-validation)
  - [Cross-Field Validation](#cross-field-validation)
  - [Conditional Validation](#conditional-validation)
- [Validation Messages](#validation-messages)
  - [Custom Messages](#custom-messages)
  - [Internationalization](#internationalization)
- [Programmatic Validation](#programmatic-validation)
  - [Manual Validation](#manual-validation)
- [Validation Performance](#validation-performance)
  - [Lazy Validation](#lazy-validation)
  - [Validation Caching](#validation-caching)
- [Advanced Validation Patterns](#advanced-validation-patterns)
  - [Async Validation](#async-validation)
  - [Conditional Validation](#conditional-validation-1)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** предоставляет полную поддержку **Bean Validation** (Jakarta Validation) для валидации данных на различных уровнях приложения. Это позволяет обеспечивать корректность данных в **REST endpoints**, методах сервисов и других местах.

### Основные возможности

- **Bean Validation**: Валидация объектов
- **Method Validation**: Валидация параметров и возвращаемых значений
- **Custom Validators**: Создание кастомных валидаторов
- **Validation Groups**: Группы валидации
- **Constraint Composition**: Композиция ограничений

## Basic Validation

### Field Validation

**Валидация полей:**

```java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class User {

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @NotNull
    @Email
    private String email;

    @Min(18)
    @Max(100)
    private Integer age;
}
```

### Method Validation

**Валидация методов:**

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    public User createUser(@Valid @NotNull User user) {
        // Валидация выполняется автоматически
        return userRepository.save(user);
    }
}
```

## REST Endpoint Validation

### Request Validation

**Валидация запросов:**

```java
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    public Response createUser(@Valid User user) {
        // Валидация выполняется перед вызовом метода
        User created = userService.create(user);
        return Response.status(201).entity(created).build();
    }
}
```

### Path Parameter Validation

**Валидация **path** параметров:**

```java
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/users")
public class UserResource {

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") @Min(1) Long id) {
        return userService.findById(id);
    }
}
```

### Query Parameter Validation

**Валидация **query** параметров:**

```java
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;

@Path("/users")
public class UserResource {

    @GET
    public List<User> getUsers(
            @QueryParam("page") @Min(0) @DefaultValue("0") Integer page,
            @QueryParam("size") @Min(1) @Max(100) @DefaultValue("20") Integer size) {
        return userService.findAll(page, size);
    }
}
```

## Custom Validators

### Custom Constraint

**Создание кастомного ограничения:**

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PhoneNumber.Validator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<PhoneNumber, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;  // @NotNull обрабатывает null
            }
            return value.matches("^\\+?[1-9]\\d{1,14}$");
        }
    }
}
```

**Использование:**

```java
public class Contact {
    @PhoneNumber
    private String phone;
}
```

## Validation Groups

### Group Definition

**Определение групп:**

```java
public interface CreateGroup {}
public interface UpdateGroup {}
```

**Использование групп:**

```java
public class User {

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    @NotNull(groups = CreateGroup.class)
    private String email;

    @NotNull(groups = UpdateGroup.class)
    private Long id;
}
```

**Валидация с группами:**

```java
import jakarta.validation.groups.Default;

@Path("/users")
public class UserResource {

    @POST
    public Response createUser(@Valid @ConvertGroup(to = CreateGroup.class) User user) {
        // Валидация с группой CreateGroup
        return Response.ok(userService.create(user)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(
            @PathParam("id") Long id,
            @Valid @ConvertGroup(to = UpdateGroup.class) User user) {
        // Валидация с группой UpdateGroup
        return Response.ok(userService.update(id, user)).build();
    }
}
```

## Constraint Composition

### Composed Constraints

**Композиция ограничений:**

```java
@NotNull
@Size(min = 8, max = 20)
@Pattern(regexp = ".*[A-Z].*")
@Pattern(regexp = ".*[a-z].*")
@Pattern(regexp = ".*[0-9].*")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface StrongPassword {
    String message() default "Password must be 8-20 characters with uppercase, lowercase and digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

## Лучшие практики

### 1. Используйте валидацию на всех уровнях

```java
// ✅ Хорошо
@POST
public Response createUser(@Valid User user) {
    // Валидация на уровне endpoint
}
```

### 2. Создавайте кастомные валидаторы для сложных правил

```java
// ✅ Хорошо
@PhoneNumber
private String phone;
```

### 3. Используйте группы для разных сценариев

```java
// ✅ Хорошо
@Valid @ConvertGroup(to = CreateGroup.class)
User user;
```

### 4. Валидируйте все входные данные

```java
// ✅ Хорошо
@PathParam("id") @Min(1) Long id
```

## Advanced Validation

### Cross-Field Validation

**Валидация между полями:**

```java
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Constraint(validatedBy = PasswordMatch.Validator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<PasswordMatch, UserRegistration> {
        @Override
        public boolean isValid(UserRegistration registration, ConstraintValidatorContext context) {
            if (registration.getPassword() == null || registration.getConfirmPassword() == null) {
                return true;  // @NotNull обрабатывает null
            }
            return registration.getPassword().equals(registration.getConfirmPassword());
        }
    }
}

@PasswordMatch
public class UserRegistration {
    private String password;
    private String confirmPassword;
    // Getters and setters
}
```

### Conditional Validation

**Условная валидация:**

```java
import jakarta.validation.constraints.AssertTrue;

public class User {

    @NotNull
    private String email;

    private Boolean newsletter;

    @AssertTrue(message = "Email is required for newsletter")
    public boolean isEmailRequiredForNewsletter() {
        return !Boolean.TRUE.equals(newsletter) || email != null;
    }
}
```

## Validation Messages

### Custom Messages

**Кастомные сообщения:**

```java
public class User {

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 50, message = "Name must be between {min} and {max} characters")
    private String name;

    @Email(message = "Email must be a valid email address")
    private String email;
}
```

### Internationalization

**Интернационализация сообщений:**

**ValidationMessages.properties:**

```properties
jakarta.validation.constraints.NotNull.message=Поле не может быть пустым
jakarta.validation.constraints.Email.message=Некорректный email адрес
```

**ValidationMessages_en.properties:**

```properties
jakarta.validation.constraints.NotNull.message=Field cannot be null
jakarta.validation.constraints.Email.message=Invalid email address
```

## Programmatic Validation

### Manual Validation

**Ручная валидация:**

```java
import jakarta.validation.Validator;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ManualValidationService {

    @Inject
    Validator validator;

    public ValidationResult validateUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (violations.isEmpty()) {
            return ValidationResult.success();
        } else {
            Map<String, String> errors = violations.stream()
                .collect(Collectors.toMap(
                    v -> v.getPropertyPath().toString(),
                    ConstraintViolation::getMessage
                ));
            return ValidationResult.failure(errors);
        }
    }
}
```

## Validation Performance

### Lazy Validation

**Ленивая валидация:**

```java
@ApplicationScoped
public class LazyValidationService {

    @Inject
    Validator validator;

    public ValidationResult validateLazy(User user, Class<?>... groups) {
        // Валидация только при необходимости
        if (shouldValidate(user)) {
            return performValidation(user, groups);
        }
        return ValidationResult.success();
    }
}
```

### Validation Caching

**Кеширование валидации:**

```java
@ApplicationScoped
public class ValidationCacheService {

    private final Cache<String, ValidationResult> cache =
        Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public ValidationResult validateCached(User user) {
        String key = generateKey(user);
        return cache.get(key, k -> performValidation(user));
    }
}
```

## Advanced Validation Patterns

### Async Validation

**Асинхронная валидация:**

```java
@ApplicationScoped
public class AsyncValidationService {

    @Inject
    Validator validator;

    public Uni<ValidationResult> validateAsync(User user) {
        return Uni.createFrom().item(() -> {
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            return violations.isEmpty()
                ? ValidationResult.success()
                : ValidationResult.failure(violations);
        });
    }
}
```

### Conditional Validation

**Условная валидация:**

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalValidator.class)
public @interface ConditionalValid {
    String message() default "Conditional validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```


## Заключение

**Quarkus Validation** предоставляет мощные инструменты для валидации данных. Поддержка **Bean Validation**, **method validation**, **custom validators**, **validation groups**, **constraint composition**, **cross-field validation**, **conditional validation**, **internationalization**, **programmatic validation** и других продвинутых возможностей позволяет обеспечивать корректность данных на всех уровнях приложения. Правильное использование валидации, создание кастомных валидаторов, использование групп и интернационализация являются ключевыми аспектами создания качественных приложений с валидацией.

## Дополнительные ресурсы

- [**Quarkus Validation** Guide](https://quarkus.io/guides/validation)
- [**Jakarta Bean Validation** Specification](https://beanvalidation.org/2.0/)
- [**Hibernate Validator** Documentation](https://hibernate.org/validator/documentation/)
- [**Bean Validation Best Practices**](https://www.baeldung.com/javax-validation)

## См. также

- [Quarkus: Actuator — Health Checks и Metrics](quarkus-actuator.md)
- [Quarkus: Основы](quarkus-basics.md)
- [Quarkus: Cache — Кеширование данных](quarkus-cache.md)
- [Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh](quarkus-cloud.md)
- [Quarkus: Core — CDI, Bean Scopes и Configuration](quarkus-core.md)
