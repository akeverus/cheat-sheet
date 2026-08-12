---
title: "Вопросы на собеседовании: Валидация в Spring"
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
# Вопросы на собеседовании: Валидация в Spring

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
- [Q1. Что такое Bean Validation и как подключить в Spring Boot?](#q1-что-такое-bean-validation-и-как-подключить-в-spring-boot)
- [Q2. Какие стандартные constraint-аннотации есть в Bean Validation?](#q2-какие-стандартные-constraint-аннотации-есть-в-bean-validation)
- [Q3. Чем `@Valid` отличается от `@Validated`?](#q3-чем-valid-отличается-от-validated)
- [Q4. Как обработать ошибки валидации в контроллере?](#q4-как-обработать-ошибки-валидации-в-контроллере)

**Продвинутые техники**
- [Q5. Как работает каскадная валидация вложенных объектов?](#q5-как-работает-каскадная-валидация-вложенных-объектов)
- [Q6. Что такое группы валидации и зачем они нужны?](#q6-что-такое-группы-валидации-и-зачем-они-нужны)
- [Q7. Как создать кастомную constraint-аннотацию?](#q7-как-создать-кастомную-constraint-аннотацию)
- [Q8. Как реализовать кросс-field валидацию?](#q8-как-реализовать-кросс-field-валидацию)
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

**Jakarta Validation** (прежнее имя — Bean Validation; JSR 380 — это версия 2.0 с пакетом `javax.validation`, а в Spring Boot 3.x работает Jakarta Validation 3.x с пакетом `jakarta.validation`) — стандарт, который описывает правила валидации прямо в модели через аннотации: вешаешь `@NotBlank`, `@Email`, `@Min` на поля DTO — и сам объект знает, что значит «корректный». Это декларативный подход: правила лежат рядом с данными, а не разбросаны по if-проверкам в коде. Стандарт описывает только API; конкретную проверку выполняет реализация — **Hibernate Validator** (референсная реализация).

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

Этот стартер тянет за собой всё необходимое для самих проверок:
- `hibernate-validator` — реализация Bean Validation, которая фактически проверяет ограничения
- `jakarta.validation:jakarta.validation-api` — сам API (аннотации, интерфейсы), приезжает транзитивно
- `tomcat-embed-el` — реализация Jakarta EL: саму интерполяцию (подстановку `{min}`, чтение `ValidationMessages.properties`) делает Hibernate Validator, а EL нужен ему, чтобы вычислять выражения вида `${validatedValue}` внутри шаблона сообщения

Саму привязку к MVC (запуск валидации, когда на аргументе контроллера стоит `@Valid`/`@Validated`) приносит `spring-boot-starter-web`, а автоконфигурация поднимает бин `Validator` (`LocalValidatorFactoryBean`). Без реализации на classpath аннотации на полях остаются просто метаданными: проверять их некому.

**Базовый пример:**

```java
public record CreateUserRequest(
    @NotBlank String name,
    @Email @NotNull String email,
    @Min(18) int age
) {}

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userService.create(req));
    }
}
```

При нарушении ограничения на `@Valid @RequestBody` Spring бросает `MethodArgumentNotValidException`, стандартный обработчик отдаёт `400 Bad Request`, и тело метода не выполняется — проверки идут «на входе», до бизнес-логики. Две оговорки: если сразу после валидируемого аргумента объявить `BindingResult`, метод всё-таки вызовется и разбирать ошибки придётся самому (см. Q4), а валидация аргументов методов сервиса бросает другое исключение со своим кодом ответа (см. Q11).

## Q2. Какие стандартные constraint-аннотации есть в Bean Validation?

Стандарт предоставляет готовый набор ограничений на все частые случаи — в большинстве проектов своих валидаторов писать не приходится. Удобно держать их в голове сгруппированными по назначению.

**Null/empty проверки** (главная путаница на собеседовании — разница между тремя):

| Аннотация | Проходит | Не проходит |
|---|---|---|
| `@NotNull` | любой не-null | `null` |
| `@NotEmpty` | "text", [1] | `null`, "", [] |
| `@NotBlank` | "text" | `null`, "", " " |

Логика по нарастанию строгости: `@NotNull` — только не-`null`; `@NotEmpty` — ещё и непустая (длина > 0), работает со строками, коллекциями, массивами, мапами; `@NotBlank` — только для строк, дополнительно отсекает строки из одних пробелов (`trim().length() > 0`). Для текстовых полей почти всегда нужен именно `@NotBlank`.

**Числовые:**

| Аннотация | Описание |
|---|---|
| `@Min(value)` | >= value |
| `@Max(value)` | <= value |
| `@Positive` | > 0 |
| `@PositiveOrZero` | >= 0 |
| `@Negative` | < 0 |
| `@Digits(integer, fraction)` | ограничение на цифры |
| `@DecimalMin / @DecimalMax` | граница задаётся строкой, поэтому допускает дробную часть; работает с `BigDecimal`, `BigInteger`, строкой и целочисленными типами, `inclusive = false` делает сравнение строгим |

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

## Q3. Чем `@Valid` отличается от `@Validated`?

Коротко: `@Valid` — из стандарта Jakarta, умеет **каскадную** валидацию (заходит внутрь вложенных объектов), но не знает про группы. `@Validated` — Spring-аннотация, умеет **группы** и включает валидацию методов на бине, который Spring может проксировать (нужен `MethodValidationPostProcessor`, вызов снаружи бина и не-final метод), но сама каскад не запускает. Это не конкуренты, а инструменты для разных задач, их часто используют вместе.

| Параметр | `@Valid` (`jakarta.validation`) | `@Validated` (Spring) |
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

**Эмпирическое правило:** `@Valid` — когда надо провалидировать вложенные объекты, `@Validated` — когда нужны группы или валидация методов сервиса. На request body в контроллере чаще ставят `@Valid`; `@Validated(Group.class)` подключают, как только появляются разные правила для create/update.

## Q4. Как обработать ошибки валидации в контроллере?

Когда валидация падает, Spring бросает исключение. Чтобы не отдавать клиенту голый стектрейс, его перехватывают и превращают в аккуратный JSON-ответ. Стандартное место для этого — глобальный обработчик `@RestControllerAdvice` с `@ExceptionHandler`: он один на всё приложение и собирает ошибки полей в единый формат.

Ключевой нюанс — исключений два, и они разные в зависимости от того, *откуда* пришли данные:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<ValidationError> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
            .toList();

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }

    // Для @Validated на сервисном уровне
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        List<ValidationError> errors = ex.getConstraintViolations().stream()
            .map(v -> new ValidationError(
                v.getPropertyPath().toString(),
                v.getMessage()))
            .toList();

        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }
}

// собственные типы, чтобы не сталкиваться со Spring: org.springframework.validation.FieldError
// и org.springframework.web.ErrorResponse заняты фреймворком
record ValidationError(String field, String message) {}
record ValidationErrorResponse(List<ValidationError> errors) {}
```

- `MethodArgumentNotValidException` — когда невалиден `@RequestBody`/`@RequestPart` (тело читается конвертером и биндится в объект, ошибки собираются в `BindingResult`). Для `@Valid @ModelAttribute` без `BindingResult` прилетает `BindException` — в Spring 6 `MethodArgumentNotValidException` от неё и наследуется.
- `HandlerMethodValidationException` — начиная со Spring Boot 3.2 при невалидных `@RequestParam`, `@PathVariable`, `@RequestHeader` прямо в контроллере: method validation там встроена, `@Validated` на классе не нужен — более того, с этой аннотацией встроенная проверка отключается (Spring отдаёт валидацию AOP-прокси, чтобы не проверять дважды), и нарушения снова прилетают как `ConstraintViolationException`. Spring штатно переводит её в `400`, отдельный хендлер нужен только ради своего формата ответа.
- `ConstraintViolationException` — когда невалидны аргументы метода обычного бина (`@Validated` на сервисе, см. Q11).

Хендлер для `ConstraintViolationException` регистрировать обязательно: без него ошибка из сервисного слоя «протечёт» как `500 Internal Server Error` вместо `400`.

**Вариант через `BindingResult`** — если хочется обработать ошибки прямо в методе, а не глобально. Добавляешь `BindingResult` параметром сразу после валидируемого объекта (порядок важен!), и тогда Spring не бросает исключение, а складывает ошибки туда — ответственность за реакцию переходит к тебе:

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody UserRequest req, BindingResult result) {
    if (result.hasErrors()) {
        return ResponseEntity.badRequest().body(result.getAllErrors());
    }
    return ResponseEntity.ok(userService.create(req));
}
```

## Q5. Как работает каскадная валидация вложенных объектов?

По умолчанию валидатор проверяет только поля самого объекта и **не заходит внутрь** вложенных. Чтобы он спустился глубже и проверил ограничения на полях вложенного объекта, на это поле вешают `@Valid` — это явное разрешение «провалидируй и его тоже».

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

На примере выше: для `List<OrderItemRequest>` пара `@Valid + @NotEmpty` означает «список не пуст И каждый его элемент валиден» — `@Valid` распространяется на элементы коллекции, а не только на саму ссылку.

**Подводный камень:** без `@Valid` на `shippingAddress` валидатор проверит лишь `shippingAddress != null` (из-за `@NotNull`), но кривой `zip` или пустой `city` внутри `Address` пройдут незамеченными. Это классическая «дыра» — объект формально не `null`, но внутри мусор.

**Глубокая вложенность:** `@Valid` работает рекурсивно. Если `Address` сам содержит поле с `@Valid`, валидатор спустится и туда — каскад идёт на любую глубину, пока проставлены `@Valid`.

## Q6. Что такое группы валидации и зачем они нужны?

Группы позволяют включать **разные наборы ограничений на одном и том же DTO** в зависимости от контекста. Классический случай — один и тот же `UserRequest` используется и при создании, и при обновлении, но правила разные: при создании `id` должен быть пустым (его выдаёт сервер), а при обновлении — обязателен. Без групп пришлось бы заводить два почти одинаковых DTO; с группами хватает одного.

Группа — это просто маркерный интерфейс (без методов). У ограничения указываешь `groups = ...`, а в контроллере через `@Validated(Группа.class)` выбираешь, какой набор активировать:

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

**Важный нюанс:** ограничение без явного `groups` попадает в группу `Default.class`. Поэтому `@Validated(OnCreate.class)` проверит только ограничения с `OnCreate`, но **не** проверит те, что без группы — они в `Default` и в выборку не попадут. Если нужны оба набора, указывают обе группы: `@Validated({Default.class, OnCreate.class})`.

`@GroupSequence` задаёт **порядок** проверки групп: следующая группа проверяется только если предыдущая прошла без ошибок. Это позволяет делать дешёвые проверки раньше дорогих — например, сначала формат поля, и только потом обращение к БД на уникальность:

```java
public interface ExpensiveChecks {}       // уникальность e-mail проверяется запросом в БД

@GroupSequence({Default.class, ExpensiveChecks.class})
public interface OrderedChecks {}
```

Смешивать в одной последовательности взаимоисключающие контекстные группы (`OnCreate` и `OnUpdate` из примера выше) нельзя: их ограничения на `id` противоречат друг другу, и такая последовательность не пройдёт никогда.

## Q7. Как создать кастомную constraint-аннотацию?

Когда стандартных ограничений не хватает (своя бизнес-логика, особый формат), пишут собственное. Обычно это **два артефакта**: сама аннотация (объявляет ограничение и его параметры) и класс-валидатор `ConstraintValidator`, который содержит логику проверки. Исключение — композитные ограничения из Q10: там пишут `@Constraint(validatedBy = {})`, а работу делают навешенные сверху готовые ограничения. Аннотация связывается с валидатором через `@Constraint(validatedBy = ...)`.

**Шаг 1 — объявить аннотацию.** Три элемента (`message`, `groups`, `payload`) обязательны по спецификации. Компилятору они безразличны — код соберётся и без них, но при первой же валидации Hibernate Validator бросит `ConstraintDefinitionException`. Свои параметры (вроде `regexp`) добавляются по необходимости:

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

**Шаг 2 — реализовать `ConstraintValidator<A, T>`** (`A` — аннотация, `T` — тип проверяемого поля). Метод `initialize()` вызывается один раз и читает параметры аннотации, `isValid()` — собственно проверка для каждого значения:

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

**Эмпирическое правило:** для форматных ограничений в `isValid()` не проверять `null` — за обязательность поля отвечает отдельный `@NotNull`, поэтому на `null` возвращают `true`. Исключение — ограничения, которые сами требуют наличия значения (как `@NotNull`/`@NotEmpty`): они на `null` обязаны возвращать `false`. Так каждое ограничение отвечает за одно, и `@Phone` без `@NotNull` корректно пропускает пустое необязательное поле, не превращая отсутствие значения в ошибку формата.

## Q8. Как реализовать кросс-field валидацию?

Обычное ограничение видит только одно поле. Когда правило связывает **несколько полей** (`password == confirmPassword`, `startDate < endDate`), валидатору нужен доступ ко всему объекту. Поэтому аннотацию вешают на **класс**, а не на поле, и валидатор реализуют как `ConstraintValidator<..., Object>` — на вход приходит весь объект, из которого через `BeanWrapperImpl` достают нужные поля по имени.

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

    // BeanWrapperImpl читает свойства через геттеры, а не через приватные поля —
    // без них getPropertyValue бросит NotReadablePropertyException
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
}
```

**Зачем `disableDefaultConstraintViolation()` + `addPropertyNode()`:** по умолчанию ошибка класс-уровневого ограничения привязывается ко всему объекту, и фронту непонятно, какое поле подсветить. Этот блок отключает дефолтную привязку и перевешивает сообщение на конкретное поле (`confirmPassword`) — тогда клиент покажет ошибку прямо под нужным input.

## Q9. Как создать stateful validator с инъекцией Spring-бинов?

Иногда валидатору нужны зависимости — например, проверка уникальности email требует обращения к `UserRepository`. Проблема в том, что «голый» Hibernate Validator создаёт валидаторы через `new`, и в такой объект Spring ничего не заинжектит. Решение — отдать создание валидаторов Spring-контейнеру: тогда `@Autowired` внутри валидатора работает как в обычном бине.

В Spring Boot это уже настроено из коробки: `LocalValidatorFactoryBean` подставляет `SpringConstraintValidatorFactory`, а она создаёт валидатор через `AutowireCapableBeanFactory.createBean()` — то есть с полным циклом внедрения зависимостей. Помечать класс `@Component` для этого не обязательно; ставят его обычно ради единообразия и чтобы валидатор было видно как компонент:

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

**Подводный камень:** DI работает только если валидация идёт через Spring-инфраструктуру. Если получить `jakarta.validation.Validator` напрямую (минуя Spring), валидаторы снова создаются через `new` — `@Autowired` останется `null`, и при первом обращении к репозиторию валидатор упадёт.

## Q10. Как работает композиция constraints?

Композиция — это способ собрать несколько готовых ограничений в одну переиспользуемую аннотацию. Если один и тот же набор проверок (`@NotBlank + @Size + @Pattern`) повторяется на многих полях, вместо копипасты создают мета-аннотацию `@PersonName` и вешают только её. Своего валидатора у неё нет (`validatedBy = {}`) — она лишь «разворачивается» в составляющие ограничения.

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

**Роль `@ReportAsSingleViolation`** — определяет детализацию ошибок:
- **без него** — каждое нарушенное вложенное ограничение даёт свою ошибку (клиент увидит и «слишком короткое», и «недопустимый символ» по отдельности);
- **с ним** — при любом нарушении возвращается одно общее сообщение мета-аннотации («Некорректное имя»), детали скрыты.

Выбор зависит от того, что удобнее пользователю: точные подсказки по каждому правилу или единый лаконичный вердикт.

## Q11. Как включить валидацию на уровне сервиса (не только контроллера)?

В веб-слое Bean Validation срабатывает на аргументах методов контроллера: `@Valid` у `@RequestBody`/`@ModelAttribute` разворачивает `HandlerMethodArgumentResolver`, а ограничения прямо на `@RequestParam`/`@PathVariable` с версии Spring 6.1 проверяет встроенная method validation. Вне веба автоматически валидируются `@Validated`-`@ConfigurationProperties` при старте и JPA-сущности перед сохранением. А вот аргументы обычного сервиса, вызванного напрямую (из другого сервиса, из теста, из обработчика Kafka), сами по себе не проверяются. Чтобы проверять аргументы методов любого бина, нужен `MethodValidationPostProcessor` — он навешивает на бин AOP-прокси, который валидирует параметры при каждом вызове:

```java
@Configuration
public class ValidationConfig {
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
```

В Spring Boot объявлять этот бин вручную обычно не нужно — `ValidationAutoConfiguration` добавляет `MethodValidationPostProcessor` автоматически. Останется только пометить класс `@Validated` (именно она включает прокси-валидацию для этого бина):

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

При нарушении бросается `ConstraintViolationException` (а не `MethodArgumentNotValidException`, как в контроллере) — это важно помнить при настройке обработчика ошибок (см. Q4).

## Q12. Чем отличается валидация в Spring MVC от валидации на сервисном уровне?

Это два разных механизма: в контроллере валидацию запускает MVC при разборе запроса, на сервисном уровне — AOP-прокси при вызове метода. Отсюда тянутся все различия — разный тип исключения, доступность `BindingResult` и важное ограничение AOP с self-invocation.

| Аспект | Spring MVC (@Valid в контроллере) | Сервисный уровень (@Validated + MethodValidation) |
|---|---|---|
| Механизм | `HandlerMethodArgumentResolver` | AOP proxy (`MethodValidationPostProcessor`) |
| Исключение | `MethodArgumentNotValidException` для `@RequestBody`, `BindException` для `@ModelAttribute`, `HandlerMethodValidationException` для параметров (Boot 3.2+, если на классе нет `@Validated`) | `ConstraintViolationException` |
| `BindingResult` | Можно перехватить в методе | Недоступен |
| Применимость | Аргументы методов-обработчиков; тот же резолверный механизм работает и для `@Payload @Valid` в слушателях сообщений | Любой проксируемый Spring-бин |
| Self-invocation | N/A | Не работает (AOP проблема) |

**Про self-invocation:** валидация на сервисном уровне идёт через прокси, поэтому вызов одного метода бина из другого метода **этого же** бина (`this.method()`) проходит мимо прокси — и не валидируется. Это та же ловушка, что у `@Transactional`.

**Рекомендация:** держать валидацию входных данных на контроллере (fail fast — отсекаем мусор на границе системы), а `@Validated` на сервисах применять точечно — как страховочный контракт для бинов, которые вызываются в обход HTTP-слоя (фоновые задачи, обработчики событий).

## Q13. Как кастомизировать сообщения об ошибках валидации?

Сообщения задают тремя способами по нарастанию гибкости: текст прямо в аннотации (проще всего), ссылка на ключ в `.properties` (даёт i18n и переиспользование), плюс интерполяция плейсхолдеров внутри текста. Выбор зависит от того, нужна ли локализация и повторное использование формулировок.

**1. Текст прямо в `message`** — годится для простых случаев и быстрых прототипов:

```java
@NotBlank(message = "Имя обязательно для заполнения")
@Size(min = 2, max = 50, message = "Имя должно быть от {min} до {max} символов")
private String name;
```

**2. Ключ из `ValidationMessages.properties`** (`src/main/resources/ValidationMessages.properties`) — выносит текст из кода. Один ключ переиспользуется на многих полях, а для локализации заводят файлы с суффиксом локали (`ValidationMessages_en.properties`). В `message` подставляют ключ в фигурных скобках:

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

**Spring-специфика:** по умолчанию `ValidationMessages.properties` читает сам Hibernate Validator, отдельно от Spring `MessageSource`. Чтобы сообщения брались из общего `messages.properties` и уважали `LocaleResolver`, у `LocalValidatorFactoryBean` явно задают `setValidationMessageSource(messageSource)`.

**3. Интерполяция плейсхолдеров** внутри текста сообщения — чтобы вставить параметры ограничения или само проверяемое значение:
- `{min}`, `{max}`, `{value}` — атрибуты аннотации (например, границы `@Size`);
- `${validatedValue}` — проверяемое значение, через выражение EL;
- `${formatter.format('%1$.2f', validatedValue)}` — форматирование значения через EL.

Разница в скобках принципиальна: `{...}` — это ключ/атрибут сообщения, `${...}` — выражение EL, вычисляемое в момент формирования ошибки.

## Q14. Что такое Spring Validator interface и когда его использовать?

`org.springframework.validation.Validator` — программная альтернатива декларативной Bean Validation: вместо аннотаций на полях логику пишут кодом в отдельном классе. Интерфейс задаёт два метода: `supports()` сообщает, какие типы умеет проверять этот валидатор, а `validate()` содержит сами проверки и складывает найденные проблемы в `Errors` через `rejectValue()`. Подход выигрывает там, где правила сложные, ветвящиеся и опираются на несколько полей сразу — описывать такое аннотациями неудобно.

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

Чтобы валидатор сработал для запросов конкретного контроллера, его регистрируют в `WebDataBinder` через `@InitBinder`:

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

**Сценарии применения:**
- ветвящиеся правила, охватывающие объект целиком, а не отдельное поле;
- когда нужен прямой доступ к `BindingResult`/`Errors` и кодам сообщений;
- легаси-проекты, написанные до распространения Bean Validation.

Отдельные зависимости и обращения к БД сами по себе поводом не являются: их закрывает кастомный `ConstraintValidator` со Spring DI (см. Q9).

**Что выбрать:** в новых проектах по умолчанию — Bean Validation, а зависимости и состояние решаются кастомными валидаторами через Spring DI (см. Q9). Spring Validator оставляют для действительно сложной, ветвящейся бизнес-логики, где декларативные аннотации становятся громоздкими.

## Q15. Как валидировать элементы коллекции в контроллере?

Когда на вход приходит список (bulk-операция, набор тегов), часто нужно проверить не только саму коллекцию, но и каждый её элемент. Есть три рабочих приёма — выбор зависит от того, тело это или query-параметры и хочется ли заводить отдельный DTO.

**Вариант 1 — DTO-обёртка вокруг коллекции.** Самый надёжный и читаемый способ: список заворачивают в DTO, на поле ставят `@Valid + @NotEmpty`. `@Valid` распространяется на элементы — каждый `CreateUserRequest` проверяется, а ошибки приходят как обычный `MethodArgumentNotValidException` с понятными путями вида `users[0].email`:

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

**Вариант 2 — `List` параметром напрямую, без обёртки** (ограничения в generic доступны с Bean Validation 2.0, то есть со Spring Boot 2.0). В сигнатуре используют ограничение внутри generic — `List<@Valid CreateUserRequest>`. Экономит DTO-обёртку, но валидация идёт через метод-уровень, и тип ошибки зависит от того, помечен ли класс `@Validated`: с этой аннотацией проверку выполняет AOP-прокси и прилетает `ConstraintViolationException`, без неё на Spring Boot 3.2+ работает встроенная method validation и прилетает `HandlerMethodValidationException`, который Spring сам переводит в `400`. До 3.2 `@Validated` была обязательна:

```java
@Validated  // на Boot 3.2+ аннотацию убирают: она отключает встроенную method validation
@RestController
public class UserController {

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulk(
            @Valid @RequestBody @NotEmpty List<@Valid CreateUserRequest> users) { ... }
}
```

**Вариант 3 — коллекция в `@RequestParam`.** Тот же приём с ограничением в generic, но для query-параметров: `@NotBlank` внутри `List<@NotBlank String>` проверяет каждый тег, а `@Size`/`@NotEmpty` на самом параметре — список целиком:

```java
@Validated  // на Boot 3.2+ не нужна — без неё те же ограничения проверит встроенная method validation
@RestController
public class SearchController {

    @GetMapping("/search")
    public List<User> search(
            @RequestParam @NotEmpty @Size(max = 100) List<@NotBlank String> tags) { ... }
}
```

Ключевой приём вариантов 2 и 3 — ограничение **внутри** угловых скобок generic (`List<@Valid ...>`, `List<@NotBlank ...>`). Это валидация type-аргумента, появившаяся с Bean Validation 2.0; ограничение перед самим параметром (`@NotEmpty List<...>`) проверяет коллекцию как целое.

## Q16. Типичные ошибки при работе с Bean Validation?

Почти все «валидация не работает» сводятся к одному из сценариев ниже. Общий корень — валидация чаще всего запускается там, где её явно попросили (`@Valid`/`@Validated`) и где вызов прошёл через нужный механизм (MVC-resolver или AOP-прокси). Отдельные интеграции вроде JPA-валидации перед сохранением работают и без этих аннотаций.

| Симптом | Причина | Решение |
|---|---|---|
| Валидация не срабатывает | Забыли `@Valid`/`@Validated` | В контроллере — на параметр; для сервиса — `@Validated` на классе, иначе прокси не создастся |
| Вложенные объекты не валидируются | Нет `@Valid` на поле | Добавить `@Valid` на поле вложенного объекта |
| `ConstraintViolationException` вместо 400 | Прилетела из сервисного бина, хендлера нет | Добавить `@ExceptionHandler(ConstraintViolationException.class)`; ошибки параметров контроллера Spring и так отдаёт как 400 |
| Кастомный validator не получает DI | Валидатор создан не Spring-фабрикой — например, через `Validation.buildDefaultValidatorFactory()` | Валидировать через Spring-бин `Validator` (`LocalValidatorFactoryBean`) — тогда зависимости внедряются, `@Component` для этого не требуется |
| Self-invocation не валидируется | AOP проблема | Вынести в отдельный бин или inject self |
| Groups не работают | Используется `@Valid` вместо `@Validated` | Для групп нужен `@Validated(Group.class)` |
| `null` не проходит `@NotBlank` | `@NotBlank` включает проверку null | Нормальное поведение; `@NotBlank` = `@NotNull` + не пусто |

---

## See also

- [Spring MVC](spring-mvc-interview.md) — `@Valid` в контроллерах, `BindingResult`, обработка ошибок
- [Spring Boot](spring-boot-interview.md) — `spring-boot-starter-validation`, автоконфигурация `ValidationAutoConfiguration`
- [Spring Framework](spring-framework-interview.md) — `@Validated` как AOP-триггер, `MethodValidationPostProcessor`
- [Spring AOP](spring-aop-interview.md) — `@Validated` работает через AOP-прокси; self-invocation проблема
- [Spring Security](spring-security-interview.md) — `@PreAuthorize` рядом с `@Validated` — порядок применения
- [Java Annotations](../../programming-languages/java/java-annotations-interview.md) — `@Constraint`, `@Target`, `@Retention` для кастомных constraints
- [Java Exceptions](../../programming-languages/java/java-exceptions-interview.md) — `ConstraintViolationException`, `MethodArgumentNotValidException`
- [HTTP & REST](../../api/http-rest-interview.md) — возврат `400 Bad Request` с деталями ошибок, Problem Details (RFC 9457, ранее RFC 7807)
- [Spring Data JPA](spring-data-jpa-interview.md) — валидация Entity перед persist через `@PrePersist`/Hibernate Validator
- [Unit Testing](../../testing/unit-testing-interview.md) — тестирование валидационных ограничений через `ValidatorFactory`
- [Шпаргалка: Spring Validation: Полное руководство по](../../../frameworks/java-frameworks/spring/spring-validation.md) — теория
