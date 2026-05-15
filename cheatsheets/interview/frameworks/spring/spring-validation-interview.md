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
>
> **Вопрос:** Что такое Bean Validation (Jakarta Validation 3.0) и как корректно подключить его в Spring Boot?
>
> ---
>
> #### A) Bean Validation — это стандарт JSR 380 / Jakarta Validation для декларативной валидации через аннотации; в Spring Boot подключается стартером `spring-boot-starter-validation`, который тянет Hibernate Validator (RI), `jakarta.validation-api` и Spring-MVC-интеграцию для `@Valid`/`@Validated`. — ✓ Верно
>
> **Развёрнутое объяснение:** Bean Validation описан в JSR 380 (Java EE 8) и переехал в `jakarta.validation` (Jakarta EE 9+, namespace `jakarta.*` вместо `javax.*`). Это контракт API + SPI: разработчик ставит аннотации (`@NotNull`, `@Email`, `@Size`), а реализация (Hibernate Validator) запускает движок и собирает `ConstraintViolation`. В Spring Boot достаточно одного стартера — авто-конфигурация регистрирует `LocalValidatorFactoryBean` и активирует `MethodValidationPostProcessor` для метод-валидации.
>
> **Пример:**
>
> ```java
> // build.gradle.kts
> // implementation("org.springframework.boot:spring-boot-starter-validation")
>
> public class CreateUserRequest {
>     @NotBlank private String name;
>     @Email @NotNull private String email;
>     @Min(18) private int age;
> }
>
> @RestController
> @RequestMapping("/users")
> public class UserController {
>     @PostMapping
>     public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest req) {
>         return ResponseEntity.ok(userService.create(req));
>     }
> }
> ```
>
> При нарушении constraint Spring MVC бросает `MethodArgumentNotValidException` → `400 Bad Request` через `DefaultHandlerExceptionResolver`.
>
> **Когда применять:** всегда, когда DTO/Request приходит извне (REST, очередь, форма) — declarative-валидация дешевле и читаемее ручных `if`-ов в сервисе.
>
> **Подводные камни:**
> - `spring-boot-starter-web` **не** тянет валидацию автоматически с Boot 2.3+, нужен отдельный стартер.
> - В Spring Boot 3 / Jakarta EE 9+ используется namespace `jakarta.validation.*`, импорты `javax.validation.*` сломают компиляцию.
> - Для активации на сервисном уровне (`@Service` + `@Validated`) нужен `MethodValidationPostProcessor` — он стартует автоматически только при наличии Bean Validation API в classpath.
>
> **Связанные вопросы:** [[Q2]], [[Q3]], [[Q11]]
>
> ---
>
> #### B) Bean Validation — это часть Spring Framework Core, доступная без отдельных зависимостей: достаточно `spring-boot-starter-web`, аннотации `@NotNull` импортируются из `org.springframework.validation`. — ❌ Неверно
>
> **Что на самом деле:** Bean Validation — это JSR/Jakarta стандарт, **независимый** от Spring. Аннотации живут в `jakarta.validation.constraints.*` (или `javax.validation.constraints.*` до Jakarta 9). Spring предоставляет лишь интеграцию (`@Valid`-обработка в `MethodArgumentResolver`, `LocalValidatorFactoryBean`), а сам движок — Hibernate Validator. Без `spring-boot-starter-validation` ни constraint-аннотации, ни их обработка работать не будут.
>
> **Откуда путаница:** в Spring есть собственный `org.springframework.validation.Validator` interface (для программной валидации через `Errors`), и его часто путают с Bean Validation API. До Boot 2.3 валидация подтягивалась транзитивно через web-стартер — отсюда миф «уже есть».
>
> **Если бы это было правдой:** не существовало бы reference implementation (Hibernate Validator), приложения были бы намертво привязаны к Spring, и валидация JPA-сущностей (которая работает на чистом Hibernate без Spring) была бы невозможна.
>
> ---
>
> #### C) Bean Validation выполняется на этапе компиляции через annotation processor: компилятор сам генерирует код проверок, поэтому `@NotNull` на поле бросает ошибку прямо в `javac`, без runtime-затрат. — ❌ Неверно
>
> **Что на самом деле:** Bean Validation — **runtime**-механизм. Hibernate Validator через reflection обходит поля/методы объекта, читает аннотации и вызывает соответствующие `ConstraintValidator`-ы. На этапе компиляции аннотации лишь сохраняются в bytecode (`@Retention(RUNTIME)`). Производительность приемлемая благодаря кешированию метаданных в `BeanMetaData`, но это не compile-time.
>
> **Откуда путаница:** существуют compile-time валидаторы (`NullAway`, Checker Framework, `@NonNull` от Lombok/JetBrains), которые действительно работают в `javac`. Их часто смешивают с Bean Validation, поскольку имена аннотаций похожи.
>
> **Если бы это было правдой:** валидация не могла бы зависеть от runtime-данных (длина строки, диапазон числа, regex по значению поля) — компилятор не знает значений переменных. `@Min(18)` для `age=15` пришедшего из JSON был бы непроверяем.
>
> ---
>
> #### D) Bean Validation активируется в Spring Boot только если в `application.yml` явно задано `spring.validation.enabled=true`, иначе все `@Valid`-аннотации молча игнорируются. — ❌ Неверно
>
> **Что на самом деле:** свойства `spring.validation.enabled` в Spring Boot **не существует**. Активация происходит автоматически при наличии стартера в classpath: `ValidationAutoConfiguration` регистрирует `LocalValidatorFactoryBean`, а `WebMvcAutoConfiguration` — `RequestMappingHandlerAdapter` с поддержкой `@Valid`. Никакого тумблера в конфиге нет.
>
> **Откуда путаница:** в Spring 2.x были discussions про подобный флаг, но в финальную spec он не вошёл. Также есть `ValidationAutoConfiguration` с `@ConditionalOnClass(ExecutableValidator.class)` — отсюда миф про «явное включение».
>
> **Если бы это было правдой:** dev-режим оставлял бы валидацию отключённой по умолчанию, в production забывали бы её включить, и `@Valid` стал бы декоративной аннотацией — серьёзная регрессия безопасности.

## Q2. Какие стандартные constraint-аннотации есть в Bean Validation?

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
>
> **Вопрос:** В чём принципиальная разница между `@NotNull`, `@NotEmpty` и `@NotBlank` для поля `String name` — что именно каждая из них отклоняет?
>
> ---
>
> #### A) Все три аннотации семантически эквивалентны для строк: каждая отклоняет `null`, пустую строку `""` и строку из пробелов `"   "` — отличаются только текстом сообщения по умолчанию. — ❌ Неверно
>
> **Что на самом деле:** аннотации различаются по строгости проверок. `@NotNull` отклоняет только `null` (пустая `""` и `"  "` проходят). `@NotEmpty` отклоняет `null` и `""`, но **пропускает** `"   "` (это непустая строка). `@NotBlank` — самая строгая для строк: отклоняет `null`, `""` и `"   "` (через `String.trim().isEmpty()`).
>
> **Откуда путаница:** в обычной речи «пустой», «null» и «состоит из пробелов» часто используются как синонимы. К тому же все три аннотации выглядят похоже и применимы к `String`.
>
> **Если бы это было правдой:** не существовало бы трёх разных аннотаций — стандарт ввёл их именно потому, что юзкейсы разные. Также `@NotEmpty` не имел бы смысла для коллекций (где `null` и `[]` — разные состояния).
>
> ---
>
> #### B) `@NotNull` запрещает только `null`; `@NotEmpty` запрещает `null` и пустую строку/коллекцию (`""`, `[]`); `@NotBlank` запрещает `null`, `""` и whitespace-only строки `"   "`. — ✓ Верно
>
> **Развёрнутое объяснение:** иерархия строгости для строк: `@NotNull` ⊂ `@NotEmpty` ⊂ `@NotBlank`. Hibernate Validator реализует их через отдельные `ConstraintValidator`: `NotNullValidator` (`value != null`), `NotEmptyValidator` (`length > 0` для строк/массивов/коллекций), `NotBlankValidator` (`CharSequence.toString().trim().length() > 0`). Применимость: `@NotNull` — к чему угодно, `@NotEmpty` — к `CharSequence`/`Collection`/`Map`/`Array`, `@NotBlank` — только к `CharSequence`.
>
> **Пример:**
>
> ```java
> public class UserRequest {
>     @NotNull(message = "id обязателен")
>     private Long id;                 // null отклонится, 0 — пройдёт
>
>     @NotEmpty(message = "имя не должно быть пустым")
>     private String name;             // null, "" отклонятся; "  " пройдёт
>
>     @NotBlank(message = "комментарий обязателен")
>     private String comment;          // null, "", "   " — все отклонятся
>
>     @NotEmpty
>     private List<String> tags;       // null, [] отклонятся
> }
>
> // Тест поведения
> @Test
> void notBlankRejectsWhitespace() {
>     UserRequest r = new UserRequest(1L, "Alice", "   ", List.of("x"));
>     Set<ConstraintViolation<UserRequest>> errors = validator.validate(r);
>     assertThat(errors).extracting(ConstraintViolation::getPropertyPath)
>         .anyMatch(p -> p.toString().equals("comment"));
> }
> ```
>
> **Когда применять:**
> - `@NotNull` — для числовых/boolean/референсных полей, где `null` недопустим, но «пустое» значение содержательно (например, `BigDecimal amount`).
> - `@NotEmpty` — для коллекций (`List<Item>`, `Map<String, Object>`), где `null` и `[]` оба бессмысленны.
> - `@NotBlank` — для пользовательского текстового ввода (имя, email, комментарий), где `"   "` — это очевидно мусор.
>
> **Подводные камни:**
> - `@NotEmpty` на `String` пропускает `"   "` — частая security/UX ошибка для имён и идентификаторов.
> - `@NotNull` не имплицирует `@Valid` — каскадная валидация вложенного объекта требует обе аннотации.
> - Для `Optional<T>` ни одна из трёх не работает корректно — нужно либо разворачивать, либо использовать `@NotEmpty` (пустой Optional).
>
> **Связанные вопросы:** [[Q1]], [[Q5]]
>
> ---
>
> #### C) `@NotBlank` работает только с числовыми типами (отклоняет `0` и отрицательные значения), `@NotEmpty` — только с коллекциями (отклоняет `null` и `[]`), `@NotNull` — только со строками. — ❌ Неверно
>
> **Что на самом деле:** распределение ровно обратное и более широкое. `@NotBlank` применим **только** к `CharSequence` (String, StringBuilder), для чисел использовать его нельзя — bean validation выдаст `UnexpectedTypeException`. `@NotEmpty` работает для `CharSequence`, `Collection`, `Map`, массивов. `@NotNull` применим ко **всем** ссылочным типам (String, Integer, Long, Object, коллекции). Для чисел «не ноль» — это `@Positive`/`@NotNull` в сочетании, но не `@NotBlank`.
>
> **Откуда путаница:** название `Blank` ассоциируется со «зачёркнутым/нулевым», `Empty` — с «пустой коллекцией». Это семантически верно для коллекций, но не для типов.
>
> **Если бы это было правдой:** валидация `String name` с `@NotBlank` падала бы с `UnexpectedTypeException` (что и происходит при применении `@NotBlank` к `Long`), и стандарт был бы бесполезен для типичных REST DTO.
>
> ---
>
> #### D) Все три аннотации применимы только к `String` — для коллекций и чисел нужно использовать `@Size(min = 1)` и `@Min(1)` соответственно, так как Hibernate Validator не поддерживает `null`-проверки для нестроковых типов. — ❌ Неверно
>
> **Что на самом деле:** `@NotNull` универсален — применим ко всем ссылочным типам (числам-wrapper, объектам, коллекциям). `@NotEmpty` поддерживает `CharSequence`, `Collection`, `Map`, `Array` — именно для коллекций он и был задуман в первую очередь. `@Size(min = 1)` пропускает `null` (Size работает только если поле не-null), поэтому без `@NotNull` он не защищает от `null`-коллекций.
>
> **Откуда путаница:** `@Size` и `@NotEmpty` действительно дают похожий эффект для коллекции с `min=1`, но семантика разная: `@Size` сообщает «допустимый диапазон длины», `@NotEmpty` — «не пусто». И `@Size` пропускает `null`.
>
> **Если бы это было правдой:** разработчикам пришлось бы дублировать `@NotNull @Size(min = 1)` на каждой коллекции, и сообщения об ошибках были бы менее информативными (`size must be between 1 and ...` вместо `must not be empty`).

## Q3. Чем `@Valid` отличается от `@Validated`?

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
>
> **Вопрос:** В чём ключевая разница между `@Valid` (jakarta.validation) и `@Validated` (Spring) при использовании в REST-контроллере и сервисном слое?
>
> ---
>
> #### A) `@Valid` и `@Validated` — полные синонимы из разных пакетов; Spring добавил `@Validated` только потому, что в эпоху Spring 2.x не было `jakarta.validation` в classpath, а сегодня они взаимозаменяемы во всех контекстах. — ❌ Неверно
>
> **Что на самом деле:** аннотации функционально пересекаются, но имеют важные различия. `@Validated` от Spring добавляет поддержку **групп валидации** (`@Validated(OnUpdate.class)`) и активирует **метод-уровень валидацию** на классе сервиса (через `MethodValidationPostProcessor`/AOP-proxy). `@Valid` — это маркер из Jakarta, который умеет в **каскадную валидацию** вложенных объектов (рекурсивный обход), но не знает про группы.
>
> **Откуда путаница:** в простейших случаях (`@Valid @RequestBody Dto`) поведение неотличимо — Spring MVC обрабатывает оба маркера в `RequestResponseBodyMethodProcessor`. Разница вылезает только когда нужны группы или метод-валидация.
>
> **Если бы это было правдой:** Spring давно убрал бы `@Validated` как deprecated, а группы валидации не работали бы вовсе.
>
> ---
>
> #### B) `@Valid` применяется только на полях для каскадной валидации, а `@Validated` — только на классах для активации AOP-проксирования; использовать их на параметрах метода запрещено спецификацией. — ❌ Неверно
>
> **Что на самом деле:** обе аннотации применимы к параметрам методов. `@Valid` ставится на параметры контроллера (`create(@Valid @RequestBody Dto dto)`) и на поля для каскадной валидации. `@Validated` применима к **классам** (включает метод-валидацию через AOP), к **параметрам методов** (с указанием групп) и **не применима к полям** (это ограничение, но не наоборот). Запрета на параметры в спецификациях нет.
>
> **Откуда путаница:** target-аннотации действительно разные: `@Valid` имеет `@Target({METHOD, FIELD, CONSTRUCTOR, PARAMETER, TYPE_USE})`, `@Validated` — `@Target({TYPE, METHOD, PARAMETER})`. Поле для `@Validated` действительно запрещено, но параметры разрешены обеим.
>
> **Если бы это было правдой:** код `public ResponseEntity<?> update(@Validated(OnUpdate.class) @RequestBody Dto dto)` не компилировался бы — но он работает в каждом Spring Boot проекте с группами.
>
> ---
>
> #### C) `@Valid` (Jakarta) поддерживает каскадную валидацию вложенных объектов, но не знает про группы; `@Validated` (Spring) добавляет группы и активирует метод-уровень валидацию на классах сервисов через AOP-прокси, но сам по себе не каскадирует. — ✓ Верно
>
> **Развёрнутое объяснение:** это два разных слоя — Jakarta-стандарт и Spring-надстройка. `@Valid` на поле DTO заставляет валидатор рекурсивно обойти вложенный объект и провалидировать его поля. `@Validated` на классе сервиса включает `MethodValidationPostProcessor`, который оборачивает бин в AOP-proxy и валидирует параметры методов (с поддержкой групп). На параметре контроллера `@Validated(Group.class)` — единственный способ указать группу валидации; `@Valid` групп не принимает. Для каскадной валидации внутри объекта, отвалидированного через `@Validated`, всё равно нужен `@Valid` на полях.
>
> **Пример:**
>
> ```java
> // Каскадная валидация — нужен @Valid
> public class OrderRequest {
>     @Valid @NotNull               // @Valid → провалидировать поля Address
>     private Address shippingAddress;
>
>     @Valid @NotEmpty              // @Valid → провалидировать каждый Item
>     private List<OrderItem> items;
> }
>
> // Группы — нужен @Validated
> public interface OnCreate {}
> public interface OnUpdate {}
>
> @PostMapping
> public ResponseEntity<?> create(
>         @Validated(OnCreate.class) @RequestBody UserRequest req) { ... }
>
> @PutMapping("/{id}")
> public ResponseEntity<?> update(
>         @Validated(OnUpdate.class) @RequestBody UserRequest req) { ... }
>
> // Метод-валидация на сервисе — @Validated на классе + @Valid на параметре
> @Service
> @Validated                        // активирует AOP-proxy для метод-валидации
> public class UserService {
>     public User register(@Valid @NotNull UserRequest req) {
>         // если req не пройдёт validation → ConstraintViolationException
>     }
> }
> ```
>
> **Когда применять:**
> - `@Valid` — везде, где нужна каскадная валидация (поля DTO, элементы коллекций) и в простых случаях контроллера без групп.
> - `@Validated` — на классе сервиса (для метод-валидации) и на параметре контроллера, когда нужны группы валидации.
> - Часто сочетаются: `@Validated(OnCreate.class) @RequestBody` + `@Valid` на полях DTO.
>
> **Подводные камни:**
> - `@Validated` на сервисе работает только через Spring-proxy: self-invocation (`this.method()`) не триггерит валидацию.
> - `ConstraintViolationException` (от `@Validated`) и `MethodArgumentNotValidException` (от MVC + `@Valid`) — разные exception, нужны два `@ExceptionHandler`.
> - `@Valid` на коллекции (`List<@Valid Item>`) валидирует элементы, но `@Valid List<Item>` без аннотации на типе — нет. В Jakarta 3.0 используется `List<@Valid Item>`.
> - Группы наследуются: если не указать группу, применяется `Default.class`; constraint без `groups = ...` попадает в `Default`.
>
> **Связанные вопросы:** [[Q1]], [[Q5]], [[Q6]], [[Q11]]
>
> ---
>
> #### D) `@Valid` запускает валидацию синхронно в текущем потоке, а `@Validated` — асинхронно через `@Async` executor, поэтому `@Validated` нужен для не-блокирующих контроллеров (WebFlux). — ❌ Неверно
>
> **Что на самом деле:** обе аннотации запускают валидацию **синхронно** в текущем потоке. `@Async` к валидации никак не относится — это отдельная Spring-аннотация для асинхронного выполнения методов. В WebFlux валидация тоже синхронная (валидатор не блокирующий — это просто проход по полям объекта в памяти), поэтому никаких асинхронных вариантов не существует.
>
> **Откуда путаница:** в WebFlux разработчики иногда ожидают, что всё «реактивное», включая валидацию. На самом деле reactive context касается I/O, а CPU-вычисления (рефлексия, обход полей) делаются синхронно в том же потоке (`bounded-elastic` или `parallel`).
>
> **Если бы это было правдой:** была бы непредсказуемая ошибка возврата: контроллер уже вернул ответ, а валидация падает в фоне с `ConstraintViolationException`. Это разрушило бы semantics REST API.

## Q4. Как обработать ошибки валидации в контроллере?

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
>
> **Вопрос:** Какие исключения и где обрабатывать, чтобы корректно конвертировать ошибки Bean Validation в `400 Bad Request` для всех типов входа (`@RequestBody`, `@PathVariable`, `@Validated`-сервисы)?
>
> ---
>
> #### A) Достаточно одного `@ExceptionHandler(ValidationException.class)` — это общий супер-класс, и Spring MVC сам приведёт к нему и `MethodArgumentNotValidException`, и `ConstraintViolationException`, и `BindException`. — ❌ Неверно
>
> **Что на самом деле:** `MethodArgumentNotValidException` **не** наследуется от `jakarta.validation.ValidationException` — он расширяет `BindException` (а та — `Exception`). Поэтому `@ExceptionHandler(ValidationException.class)` отловит только `ConstraintViolationException` (от сервисного `@Validated`), а ошибки `@Valid @RequestBody` пройдут мимо и улетят в дефолтный `ResponseEntityExceptionHandler` → `400`, но без вашего формата ошибок.
>
> **Откуда путаница:** имена `MethodArgumentNotValidException` и `ConstraintViolationException` оба содержат слово «validation», и разработчики предполагают общий предок. На деле это разные иерархии — Spring MVC `BindException` и Jakarta `ValidationException`.
>
> **Если бы это было правдой:** не пришлось бы держать два `@ExceptionHandler` в каждом `@ControllerAdvice`, и весь интернет не был бы забит вопросами «почему мой handler не ловит ошибки `@Valid`».
>
> ---
>
> #### B) `BindingResult` — единственный корректный способ обработки: добавляете его параметром после `@Valid @RequestBody`, и Spring **не бросает** исключение, а складывает ошибки в `result`. `@ExceptionHandler` для валидации не нужен. — ❌ Неверно
>
> **Что на самом деле:** `BindingResult` действительно подавляет `MethodArgumentNotValidException`, но это **не «единственный способ»** и работает только для `@RequestBody`/`@ModelAttribute` в контроллере. Для `@RequestParam`/`@PathVariable` с constraint-аннотациями (`@NotBlank`, `@Min`) бросается `ConstraintViolationException` — `BindingResult` его не ловит. Для сервисного `@Validated` — то же самое. К тому же `BindingResult` дублирует код проверок в каждом контроллере, тогда как `@ControllerAdvice` централизует обработку.
>
> **Откуда путаница:** `BindingResult` действительно работает в простых CRUD-туториалах с одним endpoint. Авторы статей не упоминают сервисный уровень.
>
> **Если бы это было правдой:** не существовал бы `RestResponseEntityExceptionHandler` в Spring и весь паттерн `@RestControllerAdvice` для глобальной обработки ошибок был бы лишним.
>
> ---
>
> #### C) В Spring Boot 3+ ничего обрабатывать не нужно: `ProblemDetail` (RFC 7807) автоматически конвертирует все `*ValidationException` в JSON с полем `errors[]`, идентичным для обоих типов исключений. — ❌ Неверно
>
> **Что на самом деле:** `ProblemDetail` в Spring 6 действительно добавляет дефолтный формат ответа (`application/problem+json`), но это всего лишь body для `400`/`422`. Полезные детали ошибок (`field`, `rejected value`, `message`) **не попадают** туда автоматически из `ConstraintViolationException` — нужно либо переопределить `handleConstraintViolation` в `ResponseEntityExceptionHandler`, либо написать свой `@ExceptionHandler` и наполнить `ProblemDetail.setProperty("errors", ...)`. Дефолтный JSON содержит только `type`, `title`, `status`, `detail` — без списка нарушенных полей.
>
> **Откуда путаница:** в release notes Spring 6 действительно подсвечена интеграция с RFC 7807, и многие думают, что это покрывает весь use-case.
>
> **Если бы это было правдой:** не было бы официального гайда «Customizing Validation Error Responses» в Spring docs.
>
> ---
>
> #### D) Нужны **два** `@ExceptionHandler` в `@RestControllerAdvice`: `MethodArgumentNotValidException` — для `@Valid @RequestBody`/`@ModelAttribute` (Spring MVC), и `ConstraintViolationException` — для `@RequestParam`/`@PathVariable` с constraint-аннотациями и для сервисного `@Validated`. У них разные API (`getBindingResult().getFieldErrors()` vs `getConstraintViolations()`), поэтому общий handler не подойдёт. — ✓ Верно
>
> **Развёрнутое объяснение:** Spring MVC обрабатывает `@Valid` на `@RequestBody` через `RequestResponseBodyMethodProcessor`, который при ошибке оборачивает `BindingResult` в `MethodArgumentNotValidException extends BindException` — Spring-специфичное исключение. Для метод-уровня (`@RestController` с `@Validated` + constraint на параметре, или `@Service` с `@Validated`) работает `MethodValidationPostProcessor` через AOP-proxy и бросает Jakarta-стандартный `ConstraintViolationException`. Это два разных контракта: `MethodArgumentNotValidException` даёт `FieldError` с `field`/`defaultMessage`/`rejectedValue`, а `ConstraintViolationException` — `ConstraintViolation` с `propertyPath`/`invalidValue`. Маппинг в единый response — задача handler-а.
>
> **Пример:**
>
> ```java
> @RestControllerAdvice
> public class ValidationExceptionHandler {
>
>     // 1) @Valid @RequestBody / @ModelAttribute → Spring MVC
>     @ExceptionHandler(MethodArgumentNotValidException.class)
>     public ResponseEntity<ErrorResponse> handleBody(MethodArgumentNotValidException ex) {
>         List<FieldErrorDto> errors = ex.getBindingResult().getFieldErrors().stream()
>             .map(fe -> new FieldErrorDto(fe.getField(), fe.getDefaultMessage(),
>                                          fe.getRejectedValue()))
>             .toList();
>         return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_FAILED", errors));
>     }
>
>     // 2) @RequestParam / @PathVariable / @Validated-сервисы → Jakarta
>     @ExceptionHandler(ConstraintViolationException.class)
>     public ResponseEntity<ErrorResponse> handleParams(ConstraintViolationException ex) {
>         List<FieldErrorDto> errors = ex.getConstraintViolations().stream()
>             .map(v -> new FieldErrorDto(
>                 v.getPropertyPath().toString(),    // например, "create.req.email"
>                 v.getMessage(),
>                 v.getInvalidValue()))
>             .toList();
>         return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_FAILED", errors));
>     }
> }
> ```
>
> **Когда применять:**
> - В любом REST-проекте — минимум эти два handler-а должны быть в `@RestControllerAdvice`.
> - Если используется `@Validated` на классе `@RestController` + constraint на параметре метода — это тоже путь через `ConstraintViolationException`.
> - Для `BindException` (form-binding в server-side rendering, `@ModelAttribute`) можно добавить третий handler или объединить с `MethodArgumentNotValidException` (общий родитель `BindException` в Spring 6+).
>
> **Подводные камни:**
> - `MethodArgumentNotValidException` сам наследуется от `BindException` — handler на `BindException` поймает оба, но потеряет точность диагностики.
> - `ConstraintViolationException` приходит из `jakarta.validation`, а не из Spring — не перепутайте импорт с `org.hibernate.exception.ConstraintViolationException` (это JDBC unique constraint).
> - `propertyPath` для сервисного `@Validated` начинается с имени метода (`create.req.email`) — клиенту обычно нужно отрезать первые два сегмента.
> - В Spring Boot `ResponseEntityExceptionHandler` уже даёт дефолтный `400` для `MethodArgumentNotValidException`, но без `errors[]` — приходится переопределять либо handler, либо `handleMethodArgumentNotValid`.
>
> **Связанные вопросы:** [[Q3]], [[Q5]], [[Q11]]

## Q5. Как работает каскадная валидация вложенных объектов?

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
>
> **Вопрос:** Что именно делает `@Valid` на поле `private Address shippingAddress` внутри `OrderRequest`, и что произойдёт, если убрать `@Valid`, оставив только `@NotNull`?
>
> ---
>
> #### A) `@Valid` на поле вложенного объекта включает **каскадную (рекурсивную) валидацию**: validator после проверки полей `OrderRequest` рекурсивно обходит поля `Address` и применяет все их constraint-аннотации (`@NotBlank street`, `@Pattern zip` и т.д.). Без `@Valid` проверится только `address != null`, но внутрь объекта validator не зайдёт — невалидный `Address` с пустыми полями пройдёт фильтр. — ✓ Верно
>
> **Развёрнутое объяснение:** `@Valid` — это маркер из Jakarta Validation, который Hibernate Validator интерпретирует как «descend into this property». При обходе графа объектов validator смотрит на каждое поле: если над ним есть `@Valid` (либо тип помечен как cascadable), он рекурсивно вызывает `validate()` для значения этого поля. Без `@Valid` поле трактуется как «leaf»: применяются только constraint-аннотации, привязанные к самому полю (`@NotNull`, `@Size`), но не constraint-ы **внутри** объекта. Поэтому `Address(null, null, "abc")` с `@NotBlank` внутри пройдёт валидацию `OrderRequest`, если на `shippingAddress` нет `@Valid`. Для коллекций аналогично: `@Valid List<OrderItem> items` валидирует **каждый** элемент; без `@Valid` проверится только не-null/не-пустота списка.
>
> **Пример:**
>
> ```java
> public class OrderRequest {
>     @NotBlank
>     private String orderRef;
>
>     @NotNull
>     @Valid                    // ← каскад: валидируем поля Address
>     private Address shippingAddress;
>
>     @NotEmpty
>     @Valid                    // ← каждый OrderItem тоже валидируется
>     private List<OrderItem> items;
> }
>
> public class Address {
>     @NotBlank private String street;
>     @NotBlank private String city;
>     @Pattern(regexp = "\\d{6}") private String zip;
> }
>
> // Тест поведения
> @Test
> void cascadeValidatesNestedFields() {
>     OrderRequest req = new OrderRequest(
>         "ORD-1",
>         new Address("", "", "abc"),         // street/city пустые, zip не подходит под regex
>         List.of(new OrderItem("SKU-1", 1)));
>
>     Set<ConstraintViolation<OrderRequest>> errors = validator.validate(req);
>
>     assertThat(errors).extracting(v -> v.getPropertyPath().toString())
>         .containsExactlyInAnyOrder(
>             "shippingAddress.street",
>             "shippingAddress.city",
>             "shippingAddress.zip");
> }
>
> @Test
> void withoutValidNestedSkipped() {
>     // Та же модель, но БЕЗ @Valid над shippingAddress
>     // Address с пустыми полями ПРОЙДЁТ — caller не узнает о проблеме
> }
> ```
>
> **Когда применять:**
> - На любом поле DTO, чей тип — не примитив/строка/число, а доменный объект, у которого есть свои constraint-аннотации.
> - На коллекциях DTO: `List<@Valid Item>` (Jakarta 3.0+) или `@Valid List<Item>` (Jakarta 2.0).
> - На `Map<K, V>` — `@Valid` валидирует значения (но не ключи) в Hibernate Validator.
>
> **Подводные камни:**
> - `@Valid` без `@NotNull` пропускает `null`-объект молча (нечего валидировать) — обычно нужны обе аннотации.
> - Циклические ссылки (`A.b → B.a → A.b`) Hibernate Validator не отслеживает — будет `StackOverflowError`. Делайте либо DTO ациклическими, либо помечайте «обратные» ссылки без `@Valid`.
> - Каскад не имеет «глубины» — он работает до листьев. Это может быть дорого для больших графов; для пакетной обработки используйте manual `Validator.validate()` с ограничением.
> - `@Valid` в Jakarta 3.0 на generic-параметре требует `@Target(TYPE_USE)` импорт-форму: `List<@Valid Item>`.
> - При каскаде применяется та же группа, что у вызывающего, если не указана иная (см. [[Q6]]).
>
> **Связанные вопросы:** [[Q3]], [[Q6]], [[Q7]]
>
> ---
>
> #### B) `@Valid` на поле объявляет, что значение поля будет **повторно сериализовано** и провалидировано на стороне клиента (через JSON Schema), а серверная валидация не запускается — без `@Valid` запускается полная серверная проверка. — ❌ Неверно
>
> **Что на самом деле:** `@Valid` — чисто **серверная** runtime-аннотация Bean Validation API. Никакой связи с JSON Schema, клиентом или re-serialization нет. Без `@Valid` валидация всё равно происходит, но **не рекурсивно** — проверяются только constraint-ы на самом поле, а не внутри объекта.
>
> **Откуда путаница:** в OpenAPI/JSON-schema есть концепция nested schema validation, и термины звучат похоже. Но Spring и Hibernate Validator не работают с JSON Schema — они отражением обходят Java-объекты.
>
> **Если бы это было правдой:** Bean Validation был бы бесполезен без подключения JSON Schema validator, и `@Valid` на поле `Address` не имел бы смысла в WebFlux/MVC, где сериализацию делает Jackson.
>
> ---
>
> #### C) `@Valid` гарантирует, что поле будет провалидировано **до** десериализации JSON: Jackson сначала проверит структуру, а потом создаст объект — это защита от bean-injection атак. Без `@Valid` объект создаётся как есть, и атакующий может протащить `prototype pollution`. — ❌ Неверно
>
> **Что на самом деле:** валидация выполняется **после** десериализации, не до. Сначала Jackson создаёт полностью заполненный Java-объект, затем `RequestResponseBodyMethodProcessor` вызывает `Validator.validate()` на собранном объекте. Никакой «защиты до десериализации» `@Valid` не даёт. От бин-injection защищают другие механизмы: `@JsonIgnore`, immutable DTO, `@JsonCreator`, отдельные read/write модели.
>
> **Откуда путаница:** «prototype pollution» — атака из JavaScript, в Java эквивалента нет. Но в обсуждениях security иногда смешивают эти понятия. Тем не менее security guides рекомендуют валидировать input — отсюда ассоциация.
>
> **Если бы это было правдой:** `@Valid` должен был бы интегрироваться с Jackson через специальный deserializer, и без `spring-boot-starter-validation` нельзя было бы безопасно использовать `@RequestBody`. Это не так.
>
> ---
>
> #### D) `@Valid` валидирует только **поля типа `Object`**, для конкретных типов (`Address`, `List<Item>`) нужно использовать `@Validated` с указанием класса: `@Validated(Address.class)`. Без `@Validated` каскад не работает. — ❌ Неверно
>
> **Что на самом деле:** `@Valid` универсален для **любых** ссылочных типов (DTO, коллекции, массивы). `@Validated` — это Spring-аннотация для активации метод-уровень валидации и групп; она **не используется** для каскада на полях. Семантика `@Validated(Address.class)` — «применить группу Address.class», а не «провалидировать объект класса Address». К тому же `@Validated` нельзя поставить на поле (`@Target` ограничен `TYPE, METHOD, PARAMETER`).
>
> **Откуда путаница:** оба маркера упоминаются вместе при объяснении валидации в Spring, и разработчик путает «активировать метод-валидацию класса» (`@Validated` на классе) с «валидировать объект класса» (это `@Valid` на поле).
>
> **Если бы это было правдой:** `@Validated(Address.class)` компилировался бы на поле и означал бы каскад — но он туда не компилируется в принципе.

## Q6. Что такое группы валидации и зачем они нужны?

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
>
> **Вопрос:** У вас один DTO `UserRequest` с полем `id`, которое при создании должно быть `null`, а при обновлении — обязательно. Как организовать валидацию, не плодя два разных DTO?
>
> ---
>
> #### A) Использовать два разных DTO — `CreateUserRequest` (без `id`) и `UpdateUserRequest` (с `@NotNull id`). Группы валидации — устаревший паттерн, признанный антипаттерном в Effective Java и в официальном Spring style guide. — ❌ Неверно
>
> **Что на самом деле:** разделение на два DTO — **тоже валидное** решение, и в некоторых командах его предпочитают (явность контрактов, легче эволюционировать независимо). Но называть группы «устаревшими» и «антипаттерном» неверно — они часть актуального Jakarta Validation 3.0, активно поддерживаются Hibernate Validator и Spring. Ни в Effective Java, ни в Spring style guide такой пометки нет. Выбор «два DTO vs группы» — вопрос вкуса и масштаба, а не deprecation.
>
> **Откуда путаница:** в больших проектах разделение DTO действительно часто удобнее (legacy-аргумент), но это не делает группы устаревшими.
>
> **Если бы это было правдой:** `@Validated(Group.class)` и `Default.class` были бы помечены `@Deprecated` — но они активно развиваются (в Jakarta 3.1 добавлено `@GroupSequence` improvements).
>
> ---
>
> #### B) Объявить marker-interfaces `OnCreate` и `OnUpdate`, навесить на поле `id` две аннотации с разными `groups` (`@Null(groups = OnCreate.class)` + `@NotNull(groups = OnUpdate.class)`), а в контроллере указывать активную группу через `@Validated(OnCreate.class)` или `@Validated(OnUpdate.class)`. Constraint без явной группы попадает в `Default.class` и проверяется всегда, когда указана `Default` (либо ничего). — ✓ Верно
>
> **Развёрнутое объяснение:** группы — это **множества констрейнтов**, которые можно активировать выборочно. Сами группы — обычные пустые интерфейсы-маркеры, используемые только как `Class<?>` token. Каждый constraint хранит атрибут `groups` (по умолчанию `{}` → `Default.class`). При вызове `validator.validate(obj, OnCreate.class)` Hibernate Validator пропускает только те констрейнты, в `groups` которых есть `OnCreate.class`. Spring `@Validated(OnCreate.class)` на параметре контроллера/сервиса передаёт это во встроенный механизм валидации. Важно: `@Valid` групп **не принимает** — только `@Validated`. Если ни одна группа не указана (`@Validated` без аргумента), применяется `Default.class`.
>
> **Пример:**
>
> ```java
> // Marker-интерфейсы
> public interface OnCreate {}
> public interface OnUpdate {}
>
> public class UserRequest {
>     @Null(groups = OnCreate.class)             // создание: id должен быть null
>     @NotNull(groups = OnUpdate.class)          // обновление: id обязателен
>     private Long id;
>
>     @NotBlank(groups = {OnCreate.class, OnUpdate.class})
>     private String name;                       // оба сценария
>
>     @NotBlank(groups = OnCreate.class)         // только при создании
>     @Size(min = 8, groups = OnCreate.class)
>     private String password;
>
>     @Email                                     // groups={} → Default.class
>     private String email;                      // НЕ проверяется при @Validated(OnCreate.class)
> }
>
> @RestController
> @RequestMapping("/users")
> public class UserController {
>
>     @PostMapping
>     public ResponseEntity<?> create(
>             @Validated(OnCreate.class) @RequestBody UserRequest req) { ... }
>
>     @PutMapping("/{id}")
>     public ResponseEntity<?> update(
>             @Validated(OnUpdate.class) @RequestBody UserRequest req) { ... }
> }
>
> // Чтобы email проверялся всегда — указать Default.class явно:
> // @Validated({OnCreate.class, Default.class})
>
> // Или через @GroupSequence — последовательный запуск групп:
> @GroupSequence({Default.class, OnCreate.class})
> public interface CreateChecks {}
> // → сначала Default; если упало — OnCreate не запускается (fail-fast по группам)
> ```
>
> **Когда применять:**
> - Когда один и тот же DTO переиспользуется в разных endpoint-ах (Create/Update/Patch) — экономит дублирование классов и mapper-логику.
> - Когда нужны разные правила в зависимости от роли пользователя (`AdminChecks` vs `UserChecks`) — группа задаётся в контроллере по роли.
> - Когда есть «этапная» проверка: сначала формат (`Default`), потом бизнес-инварианты (`BusinessRules`) через `@GroupSequence`.
>
> **Подводные камни:**
> - **Constraints без `groups` НЕ запускаются** при `@Validated(OnCreate.class)` — они в группе `Default`, которая активируется только если её явно указали. Часто это причина «`@Email` молча игнорируется».
> - `@Valid` группы **не понимает** — `@Valid(OnCreate.class)` не компилируется. Только `@Validated`.
> - При каскадной валидации группа **наследуется** от вызывающего: `@Validated(OnCreate.class)` на `OrderRequest` запустит `OnCreate` и для всех `@Valid`-полей внутри. Чтобы переключить группу при каскаде, нужен `@ConvertGroup(from = OnCreate.class, to = OnNested.class)`.
> - Группа-интерфейс должна быть **пустой** (никаких методов) — иначе теряет смысл marker-pattern.
> - `@GroupSequence` выполняет группы строго последовательно: следующая стартует только если предыдущая прошла без ошибок (fail-fast).
>
> **Связанные вопросы:** [[Q3]], [[Q5]], [[Q7]]
>
> ---
>
> #### C) В Spring достаточно создать два разных метода в `@RestControllerAdvice` с разными `@ExceptionHandler` — один для `Create`-ошибок, другой для `Update`. Bean Validation сам определит контекст по HTTP-методу (POST vs PUT) и применит соответствующие констрейнты. — ❌ Неверно
>
> **Что на самом деле:** Bean Validation **никак не связан** с HTTP-методом — он не знает про `POST`/`PUT`. Контекст переключается явно через `@Validated(Group.class)`. `@ExceptionHandler` срабатывает уже **после** валидации (на исключение), он не управляет тем, какие констрейнты применять. К тому же ошибка `@Null` и `@NotNull` приведут к одному и тому же `MethodArgumentNotValidException` — handler не сможет различить «создание упало» от «обновление упало» без явных флагов.
>
> **Откуда путаница:** разработчики ожидают, что framework «угадает» контекст по операции. Spring так делает в некоторых местах (например, `@PostMapping` vs `@PutMapping` для роутинга), но валидация — domain-level concern, она HTTP-агностична.
>
> **Если бы это было правдой:** Bean Validation работала бы только в HTTP-контексте и была бы неприменима для service-уровня, очередей, batch-jobs. Это сильно ограничило бы спецификацию.
>
> ---
>
> #### D) Достаточно вынести правило в `if` внутри контроллера: `if (req.getId() != null && isCreate) throw new ValidationException(...)`. Группы — это hack для тех, кто не понимает SOLID; нормальный код всегда явно проверяет инварианты в сервисе. — ❌ Неверно
>
> **Что на самом деле:** declarative-валидация через группы — **рекомендованный** способ для декларативных правил, потому что: (1) правило задано рядом с полем (single source of truth), (2) активируется автоматически перед сервисом (fail-fast), (3) даёт структурированный ответ клиенту (`MethodArgumentNotValidException` → 400 с полями), (4) не размывает бизнес-логику сервиса проверками формата. Имеративные `if`-ы в контроллере дублируются между endpoint-ами, плохо тестируются, не дают единого формата ошибок и нарушают DRY.
>
> **Откуда путаница:** в простых проектах с одним endpoint-ом ручной `if` действительно проще. Этот опыт переносится на сложные проекты, где он перестаёт работать.
>
> **Если бы это было правдой:** в Hibernate Validator не было бы groups API, в Jakarta-стандарте не существовало бы `@GroupSequence`, а Spring не реализовал бы `@Validated`. Все эти артефакты — ответ на реальную проблему повторного использования DTO.

## Q7. Как создать кастомную constraint-аннотацию?

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


> [!mcq] Что обязательно нужно для рабочей кастомной constraint-аннотации `@Phone`?
>
> - [ ] **A) Только объявить `@interface Phone` с атрибутами `message()`, `groups()`, `payload()` — Bean Validation сам найдёт подходящий валидатор по соглашению об именах.**
>
>   Это неверно: спецификация Jakarta Bean Validation не требует и не использует name-conventions для подбора валидатора. Если в аннотации нет `@Constraint(validatedBy = ...)` или класс `PhoneValidator` не реализует `ConstraintValidator<Phone, String>` — провайдер (Hibernate Validator) выкинет `ConstraintDefinitionException` при первой же проверке.
>
>   ❌ ПОСЛЕДСТВИЕ: при первом же запросе с `@Phone` контроллер падает с 500 вместо 400, в логах `No validator could be found for constraint 'Phone'` — пользователи получают internal error.
>
> - [ ] **B) Объявить аннотацию с `@Constraint(validatedBy = PhoneValidator.class)`, но без обязательных элементов `groups()` и `payload()`.**
>
>   Bean Validation требует, чтобы любая constraint-аннотация имела ровно три обязательных элемента: `message()`, `Class<?>[] groups()` и `Class<? extends Payload>[] payload()`. Без них Hibernate Validator при инициализации фабрики (`buildValidatorFactory()`) бросит `ConstraintDefinitionException: HV000074: Constraint definition ... is missing the mandatory element 'groups'`.
>
>   ❌ ПОСЛЕДСТВИЕ: приложение даже не стартует — `LocalValidatorFactoryBean.afterPropertiesSet()` падает, контекст Spring не поднимается, deploy откатывается.
>
> - [x] **C) Объявить `@interface Phone` с `@Constraint(validatedBy = PhoneValidator.class)` и тремя обязательными элементами + реализовать `PhoneValidator implements ConstraintValidator<Phone, String>` с методом `isValid()`, который трактует `null` как валидное значение.**
>
>   Это полный и корректный контракт. `@Constraint(validatedBy = ...)` связывает аннотацию с валидатором; три обязательных элемента (`message()`, `groups()`, `payload()`) требуются спецификацией; интерфейс `ConstraintValidator<A, T>` параметризован самой аннотацией и типом проверяемого поля. Опциональный `initialize(Phone)` читает атрибуты аннотации (например, `regexp`). Игнорирование `null` в `isValid()` — стандартное соглашение: за null отвечает отдельная аннотация `@NotNull`, иначе невозможно сделать поле опциональным.
>
>   ```java
>   @Target({ElementType.FIELD, ElementType.PARAMETER})
>   @Retention(RetentionPolicy.RUNTIME)
>   @Constraint(validatedBy = PhoneValidator.class)
>   @Documented
>   public @interface Phone {
>       String message() default "Неверный формат телефона";
>       Class<?>[] groups() default {};
>       Class<? extends Payload>[] payload() default {};
>       String regexp() default "^\\+7\\d{10}$";
>   }
>
>   public class PhoneValidator implements ConstraintValidator<Phone, String> {
>       private String regexp;
>       @Override public void initialize(Phone a) { this.regexp = a.regexp(); }
>       @Override public boolean isValid(String value, ConstraintValidatorContext ctx) {
>           if (value == null) return true; // null — забота @NotNull
>           return value.matches(regexp);
>       }
>   }
>   ```
>
>   КОГДА ВЫБИРАТЬ: всегда, когда нужна доменная проверка одного поля (телефон, ИНН, slug, hex-color) — даёт декларативный API и хорошо читается на DTO.
>
>   ✅ ПОСЛЕДСТВИЕ: `@Phone` срабатывает на любом DTO через `@Valid`, опциональные поля (null) корректно проходят, ошибки попадают в стандартный поток `MethodArgumentNotValidException` → HTTP 400 с понятным message.
>
> - [ ] **D) Реализовать `PhoneValidator extends javax.validation.spi.ValidationProvider` и зарегистрировать его в `META-INF/services` — Hibernate Validator подхватит через SPI.**
>
>   Это путаница уровней: `ValidationProvider` — это SPI для регистрации целого провайдера валидации (как Hibernate Validator или Apache BVal), а не отдельного constraint. Подмена провайдера сломает всю валидацию приложения, при этом саму аннотацию `@Phone` это всё равно не подружит с `PhoneValidator`. Правильный механизм для кастомного constraint — `ConstraintValidator<A, T>` + `@Constraint(validatedBy = ...)`.
>
>   ❌ ПОСЛЕДСТВИЕ: попытка переопределить SPI ломает `ValidationAutoConfiguration` — все `@Valid` в контроллерах перестают срабатывать; баги типа «invalid email проходит на prod» вылезают через несколько релизов, когда забывают про эту правку.

## Q8. Как реализовать кросс-field валидацию?

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


> [!mcq] Нужна проверка `password == confirmPassword` в `RegisterRequest`. Какая реализация cross-field валидации корректна и привязывает ошибку именно к полю `confirmPassword`?
>
> - [ ] **A) Повесить `@AssertTrue` на геттер `isPasswordsMatch()` внутри DTO, который возвращает `password.equals(confirmPassword)`.**
>
>   Технически валидация сработает и forms-вариант часто так и делают, но ошибка привяжется к виртуальному полю `passwordsMatch`, а не к реальному `confirmPassword`. Это критично для UX: фронт ожидает увидеть ошибку под конкретным input-полем, а не под несуществующим. К тому же геттер десинхронизируется с реальными полями при рефакторинге (легко забыть обновить).
>
>   ❌ ПОСЛЕДСТВИЕ: фронт получает `field: "passwordsMatch"` в `MethodArgumentNotValidException.fieldErrors`, не может подсветить нужный input — пользователи видят generic-баннер «форма содержит ошибки» и теряются.
>
> - [ ] **B) Сравнить пароли вручную в контроллере: `if (!req.getPassword().equals(req.getConfirmPassword())) throw new IllegalArgumentException(...)`.**
>
>   Это работает, но ломает декларативную модель: валидация размазана между аннотациями на DTO и imperative-кодом в контроллере. Сложнее тестировать (нужен полный slice-тест вместо `Validator.validate()`), невозможно переиспользовать на сервисном уровне через `@Validated`, и `IllegalArgumentException` нужно отдельно мапить в HTTP 400 в `@ControllerAdvice`.
>
>   ❌ ПОСЛЕДСТВИЕ: дублирование логики (в форме регистрации, смены пароля, восстановления) — рано или поздно одна из веток разойдётся с другими; security-баг «можно поменять пароль без подтверждения» прилетает через полгода.
>
> - [ ] **C) Объявить class-level аннотацию `@PasswordMatch` без атрибутов `password()` и `confirmPassword()` — валидатор сам по reflection найдёт поля с именами `password` и `confirmPassword`.**
>
>   Hardcoded имена полей — анти-паттерн: аннотацию нельзя переиспользовать для пары `newPassword`/`confirmNewPassword` или `email`/`confirmEmail`. Кроме того, ошибка из `addPropertyNode("confirmPassword")` тоже будет hardcoded — невозможно настроить под другую форму без правки самого валидатора.
>
>   ❌ ПОСЛЕДСТВИЕ: при добавлении формы смены пароля приходится либо копировать `@PasswordMatch` под другим именем (`@NewPasswordMatch`), либо ломать существующий контракт — растёт сложность поддержки.
>
> - [x] **D) Объявить class-level аннотацию `@PasswordMatch(password = "password", confirmPassword = "confirmPassword")` + валидатор, который через `BeanWrapperImpl` читает значения этих полей и через `ctx.addPropertyNode(confirmPasswordField).addConstraintViolation()` привязывает ошибку к нужному полю.**
>
>   Это эталонный паттерн cross-field валидации в Spring. Аннотация на `ElementType.TYPE` получает доступ к объекту целиком; имена полей — параметры аннотации (переиспользуема для любых пар); `BeanWrapperImpl` решает задачу reflection-доступа единообразно с остальным Spring; вызов `disableDefaultConstraintViolation()` + `addPropertyNode()` критичен — без него ошибка прилетит на уровне всего объекта (с пустым `field`), а не на конкретном поле.
>
>   ```java
>   @Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
>   @Constraint(validatedBy = PasswordMatchValidator.class)
>   public @interface PasswordMatch {
>       String message() default "Пароли не совпадают";
>       Class<?>[] groups() default {};
>       Class<? extends Payload>[] payload() default {};
>       String password();
>       String confirmPassword();
>   }
>
>   public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {
>       private String passField, confirmField;
>       @Override public void initialize(PasswordMatch a) {
>           this.passField = a.password();
>           this.confirmField = a.confirmPassword();
>       }
>       @Override public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
>           BeanWrapper bw = new BeanWrapperImpl(obj);
>           if (Objects.equals(bw.getPropertyValue(passField), bw.getPropertyValue(confirmField))) return true;
>           ctx.disableDefaultConstraintViolation();
>           ctx.buildConstraintViolationWithTemplate(ctx.getDefaultConstraintMessageTemplate())
>              .addPropertyNode(confirmField).addConstraintViolation();
>           return false;
>       }
>   }
>
>   @PasswordMatch(password = "password", confirmPassword = "confirmPassword")
>   public class RegisterRequest { ... }
>   ```
>
>   КОГДА ВЫБИРАТЬ: всегда, когда нужно проверить инвариант между двумя+ полями (даты `from <= to`, два пароля, цены `discount <= price`) и хочется красивый JSON-ответ с привязкой к полю.
>
>   ✅ ПОСЛЕДСТВИЕ: фронт получает `fieldErrors: [{field: "confirmPassword", message: "Пароли не совпадают"}]` — нужный input сразу подсвечивается; аннотация переиспользуется на любой паре полей без правки валидатора.

## Q9. Как создать stateful validator с инъекцией Spring-бинов?

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


> [!mcq] Нужен `@UniqueEmail`-validator, который дёргает `UserRepository.existsByEmail()`. Какой подход в Spring Boot обеспечит реальную инъекцию `UserRepository` в валидатор?
>
> - [x] **A) Пометить `UniqueEmailValidator` аннотацией `@Component` (или `@Service`) и инжектить `UserRepository` через `@Autowired` / конструктор — Spring Boot автоконфигурацией поднимает `LocalValidatorFactoryBean` с `SpringConstraintValidatorFactory`, которая создаёт валидаторы через ApplicationContext.**
>
>   Это рабочая идиома Spring Boot. `ValidationAutoConfiguration` создаёт бин `LocalValidatorFactoryBean` (реализует и `javax.validation.Validator`, и `org.springframework.validation.Validator`) с подменённой `ConstraintValidatorFactory` на `SpringConstraintValidatorFactory`. Эта фабрика при создании каждого validator-инстанса делает `applicationContext.getAutowireCapableBeanFactory().createBean(clazz)` — DI работает полноценно, включая `@Autowired`, конструкторную инъекцию, `@Value`, `@Qualifier`. Аннотация `@Component` обязательна не сама по себе (фабрика умеет создавать и не-Spring-классы), но она гарантирует, что Spring увидит класс при classpath-сканировании и провалидирует граф зависимостей на старте.
>
>   ```java
>   @Component
>   public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
>       private final UserRepository userRepository;
>       public UniqueEmailValidator(UserRepository userRepository) {
>           this.userRepository = userRepository; // конструкторная DI работает
>       }
>       @Override public boolean isValid(String email, ConstraintValidatorContext ctx) {
>           if (email == null) return true;
>           return !userRepository.existsByEmail(email);
>       }
>   }
>
>   @Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
>   @Constraint(validatedBy = UniqueEmailValidator.class)
>   public @interface UniqueEmail {
>       String message() default "Email уже зарегистрирован";
>       Class<?>[] groups() default {};
>       Class<? extends Payload>[] payload() default {};
>   }
>   ```
>
>   КОГДА ВЫБИРАТЬ: всегда, когда валидация требует обращения к Spring-инфраструктуре (БД, кэш, внешний HTTP-клиент, конфиги). Это standard-way в Spring Boot.
>
>   ✅ ПОСЛЕДСТВИЕ: валидатор получает реальный `UserRepository`, проверка на уникальность работает на любом DTO с `@UniqueEmail` через `@Valid`. Внимание: эта проверка имеет TOCTOU-race с insert'ом — её нужно дублировать unique-constraint'ом на БД.
>
> - [ ] **B) Создавать `UniqueEmailValidator` через `new` в самом валидаторе или в `@PostConstruct` контроллера, а `UserRepository` присваивать через статическое поле `UniqueEmailValidator.repository = repo`.**
>
>   Hibernate Validator по умолчанию создаёт validator-инстансы через `Class.newInstance()` (через `DefaultConstraintValidatorFactory`), поэтому `@Autowired` в валидаторе работать не будет — Spring о нём ничего не знает. Костыль со static-полем создаёт глобальное состояние: тесты ломаются (порядок-зависимы), in-process параллелизм небезопасен, при перезагрузке контекста (Spring DevTools, testcontext caching) статика остаётся «прибитой» к старому бину.
>
>   ❌ ПОСЛЕДСТВИЕ: после рестарта DevTools валидатор держит ссылку на закрытый `EntityManager`, дальше — `NullPointerException` или `IllegalStateException: Session/EntityManager is closed` на каждом запросе с `@UniqueEmail`.
>
> - [ ] **C) Использовать `Validation.buildDefaultValidatorFactory().getValidator()` напрямую и руками подкидывать `UserRepository` через `ConstraintValidatorContext.unwrap(...)`.**
>
>   `Validation.buildDefaultValidatorFactory()` строит фабрику без Spring-контекста (через дефолтный `DefaultConstraintValidatorFactory`) — DI не работает в принципе. `ConstraintValidatorContext` — это API для построения нарушений (сообщения, ноды, payload), а не для прокидывания зависимостей; ни `unwrap`, ни какой-либо другой его метод не даёт доступа к репозиторию.
>
>   ❌ ПОСЛЕДСТВИЕ: код компилируется, но `userRepository` в валидаторе остаётся `null` → `NullPointerException` при первом валидируемом запросе; в логах нет внятного сообщения, диагностика занимает часы.
>
> - [ ] **D) Превратить `UniqueEmailValidator` в bean со scope `prototype` и достать его из `ApplicationContext.getBean()` внутри `isValid()` через `ApplicationContextHolder`.**
>
>   Service-locator поверх Spring — анти-паттерн. Во-первых, лишний indirection при каждой валидации (lookup в контексте на горячем пути). Во-вторых, `ApplicationContextHolder` — статический singleton, тот же проблемный паттерн что и в варианте B. В-третьих, prototype-scope здесь не нужен и вреден: `SpringConstraintValidatorFactory` уже сама управляет жизненным циклом, дополнительный prototype может приводить к утечке инстансов через ApplicationContext.
>
>   ❌ ПОСЛЕДСТВИЕ: измеримая деградация latency на endpoint'ах с валидацией (десятки микросекунд на каждый `getBean`), плюс утечка прокси-объектов в ApplicationContext — heap растёт пока не упирается в `OutOfMemoryError`.

## Q10. Как работает композиция constraints?

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


> [!mcq] Что обязательно нужно указать, чтобы создать составную constraint-аннотацию (composed constraint) типа `@PersonName`, объединяющую `@NotBlank` + `@Size` + `@Pattern`?
>
> - [x] **A. Пометить аннотацию `@Constraint(validatedBy = {})` (без своего validator-класса), навесить сверху нужные constraints (`@NotBlank`, `@Size`, `@Pattern`), и опционально добавить `@ReportAsSingleViolation` для одного сообщения вместо нескольких.**
>
>   Это канонический паттерн Bean Validation: пустой `validatedBy` говорит провайдеру (Hibernate Validator) применить мета-constraints, расположенные на самой аннотации. `@ReportAsSingleViolation` сворачивает все нарушения в одно сообщение из мета-аннотации — удобно для UX («Некорректное имя» вместо трёх параллельных ошибок). Стандартные `message()`, `groups()`, `payload()` обязательны для совместимости со спецификацией.
>
>   ВЫГОДА: переиспользуемая семантика валидации без дублирования аннотаций в DTO и без написания собственного `ConstraintValidator`.
>
> - [ ] B. Реализовать собственный `ConstraintValidator<PersonName, String>`, в котором вручную вызвать `validator.validate(value)` для каждого вложенного constraint и аккумулировать ошибки в `ConstraintValidatorContext`.
>
>   Это работает, но избыточно: вы фактически переписываете то, что провайдер делает автоматически через композицию. Появляется ручная обработка `ConstraintViolation`, дублируется логика, теряется поддержка groups и payload в дочерних аннотациях.
>
>   ПОСЛЕДСТВИЕ: рост поддержки на 3-5x, легко забыть какой-то constraint при изменении правил, и `@ReportAsSingleViolation` уже не сработает — потому что провайдер не знает что это композиция.
>
> - [ ] C. Использовать `@GroupSequence({First.class, Second.class, Third.class})` на DTO, чтобы по очереди применить `@NotBlank`, `@Size`, `@Pattern` в нужном порядке.
>
>   `@GroupSequence` решает другую задачу — упорядоченное выполнение групп валидации (fail-fast между группами). Это не композиция: аннотации всё ещё расположены отдельно в DTO, нет единой семантической метки `@PersonName`, нет переиспользования.
>
>   ПОСЛЕДСТВИЕ: путаница абстракций — `@GroupSequence` отвечает за порядок, а композиция за повторное использование набора constraints как одного.
>
> - [ ] D. Объявить аннотацию `@PersonName` без `@Constraint`, просто навесив сверху `@NotBlank`, `@Size`, `@Pattern` — Hibernate Validator автоматически распознает её как мета-аннотацию.
>
>   Это не работает: без `@Constraint(validatedBy = ...)` провайдер вообще не считает аннотацию constraint-ом и пропустит её при сканировании. Нет `@Constraint` — нет регистрации в `ConstraintHelper`, нет валидации.
>
>   ПОСЛЕДСТВИЕ: тихий пропуск валидации в production — DTO «проходит» без проверок, баги в данных доходят до БД, обнаруживаются только по жалобам пользователей.

## Q11. Как включить валидацию на уровне сервиса (не только контроллера)?

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


> [!mcq] Что необходимо сделать, чтобы Bean Validation срабатывал на параметрах методов обычного `@Service`-бина, а не только в контроллерах Spring MVC?
>
> - [ ] A. Навесить `@Valid` на параметр метода сервиса — Spring автоматически проверит DTO так же, как в контроллере, через тот же `HandlerMethodArgumentResolver`.
>
>   `HandlerMethodArgumentResolver` — это инфраструктура Spring MVC, она вызывается только web-слоем при разрешении аргументов handler-методов. Сервисы вызываются напрямую другими бинами, без этого слоя.
>
>   ПОСЛЕДСТВИЕ: `@Valid` молча игнорируется, метод выполняется с невалидными данными, ошибки данных обнаруживаются только на уровне БД (constraint violations) или в downstream-сервисах — поздно, без понятного сообщения.
>
> - [ ] B. Аннотировать сервис `@Validated`, но не подключать `MethodValidationPostProcessor` — Spring Boot AOP сам найдёт аннотацию и обернёт бин прокси.
>
>   В Spring Boot `MethodValidationPostProcessor` действительно регистрируется автоматически через `ValidationAutoConfiguration`, но это и есть тот компонент, который создаёт AOP-прокси. Без него `@Validated` — просто маркер, не несущий поведения.
>
>   ПОСЛЕДСТВИЕ: в обычном Spring Boot всё «случайно работает», но при отключённом `ValidationAutoConfiguration` (или в plain Spring без Boot) валидация бесшумно пропадает — классический pitfall при миграции конфигов или кастомных стартеров.
>
> - [x] **C. Объявить бин `MethodValidationPostProcessor` (в Spring Boot он есть автоматически через `ValidationAutoConfiguration`) и пометить сервисный класс аннотацией `@Validated` — это создаст AOP-прокси, который валидирует параметры (`@Valid`, `@NotNull`, `@Positive` и т.д.) на каждом вызове метода; при нарушении бросается `ConstraintViolationException`.**
>
>   `MethodValidationPostProcessor` — это `BeanPostProcessor`, который оборачивает все бины с `@Validated` в AOP-прокси (`MethodValidationInterceptor`). На каждом вызове метода interceptor собирает constraints с параметров и применяет их через `Validator.forExecutables()`. В отличие от MVC-пути, нарушение приводит к `ConstraintViolationException` (а не `MethodArgumentNotValidException`) — что нужно учитывать в `@ControllerAdvice`.
>
>   ВЫГОДА: валидация работает как контракт на уровне любого Spring-бина (сервисы, репозитории, фасады), а не только в HTTP-слое — это полезно для внутренних API, message-listener-ов, scheduled-задач.
>
> - [ ] D. Использовать `Validator` (JSR-303) напрямую: внедрить `javax.validation.Validator` в сервис и в начале каждого метода вызывать `validator.validate(dto)`, выбрасывая исключение при непустом `Set<ConstraintViolation>`.
>
>   Это работает функционально, но это ручной подход — нужно повторять boilerplate в каждом методе, легко забыть, нет единого формата исключений, теряется декларативность аннотаций на параметрах (валидируются только сами объекты, но не `@NotNull` / `@Positive` на примитивах).
>
>   ПОСЛЕДСТВИЕ: размывание ответственности (валидация смешивается с бизнес-логикой), забытые проверки в новых методах, отсутствие централизованного `@ExceptionHandler` — каждый сервис ловит и формирует ошибки по-своему.

## Q12. Чем отличается валидация в Spring MVC от валидации на сервисном уровне?

| Аспект | Spring MVC (@Valid в контроллере) | Сервисный уровень (@Validated + MethodValidation) |
|---|---|---|
| Механизм | `HandlerMethodArgumentResolver` | AOP proxy (`MethodValidationPostProcessor`) |
| Исключение | `MethodArgumentNotValidException` | `ConstraintViolationException` |
| `BindingResult` | Можно перехватить в методе | Недоступен |
| Применимость | Только в контроллерах | Любой Spring-бин |
| Self-invocation | N/A | Не работает (AOP проблема) |

**Рекомендация:** валидацию лучше проводить на контроллерном уровне (fail fast), но для внутренних API и сервисов `@Validated` полезен как дополнительный контракт.


> [!mcq] В контроллере при `@Valid @RequestBody` нарушение валидации бросает `MethodArgumentNotValidException`, а в `@Validated`-сервисе — `ConstraintViolationException`. Почему так и какое из этих исключений даёт доступ к `BindingResult`?
>
> - [ ] A. Оба исключения предоставляют `BindingResult` через метод `getBindingResult()`, разница только в имени класса — `MethodArgumentNotValidException` появилось раньше, `ConstraintViolationException` — после Spring 5 для унификации.
>
>   `ConstraintViolationException` (из `javax.validation`/`jakarta.validation`) не наследуется от Spring-классов и не содержит `BindingResult` — там только `Set<ConstraintViolation<?>>` с путями вида `methodName.arg0.fieldName`. `BindingResult` — это абстракция Spring Web, она не существует на сервисном уровне.
>
>   ПОСЛЕДСТВИЕ: попытка унифицировать обработку через общий `BindingResult` в `@ControllerAdvice` приводит к `NoSuchMethodError`/`ClassCastException` в runtime, тесты на mock-MVC скрывают баг, всплывает только при реальном вызове сервиса.
>
> - [x] **B. `MethodArgumentNotValidException` (Spring MVC) наследуется от `BindException` и содержит `BindingResult` — его можно перехватить параметром метода контроллера (`public X handler(@Valid Y dto, BindingResult result)`) и обработать ошибки до выброса исключения. `ConstraintViolationException` (Bean Validation, AOP-путь) — это исключение из `jakarta.validation`, оно несёт `Set<ConstraintViolation<?>>` с путями к параметрам метода и НЕ имеет `BindingResult`; перехватить «in-place» в самом сервисном методе нельзя, только через `@ExceptionHandler`.**
>
>   Это ключевое следствие двух разных механизмов: MVC-валидация работает через `HandlerMethodArgumentResolver` ещё ДО входа в метод контроллера, поэтому Spring может «положить» результат рядом — в `BindingResult`, и если параметр объявлен, исключение не бросается, контроллер сам решает что делать. AOP-валидация работает через `MethodValidationInterceptor` вокруг вызова — она либо пропускает, либо бросает; никакого «buffer object» рядом с параметрами нет.
>
>   ВЫГОДА: понимание этого различия позволяет правильно выбрать стратегию — в контроллере иногда нужно вернуть ошибки в форме рядом с данными (паттерн `BindingResult`), в сервисе же валидация — это hard контракт, нарушение которого должно стать исключением без graceful-обработки.
>
> - [ ] C. Чтобы получить `BindingResult` в сервисе, нужно добавить параметр `BindingResult` после `@Valid`-параметра — `MethodValidationInterceptor` сам заполнит его так же, как `HandlerMethodArgumentResolver` в MVC.
>
>   `MethodValidationInterceptor` ничего не знает про `BindingResult` — это контракт Spring Web, а AOP-путь работает только через `jakarta.validation.Validator`. Параметр `BindingResult` в обычном бине будет либо `null`, либо вообще не разрешится.
>
>   ПОСЛЕДСТВИЕ: `NullPointerException` при попытке вызвать `result.hasErrors()`, либо тихий пропуск валидации (если interceptor решит, что метод сам обрабатывает ошибки) — типичный pitfall при переносе кода контроллера в сервис.
>
> - [ ] D. Различие чисто историческое: `MethodArgumentNotValidException` использовалось в Spring 3-4, `ConstraintViolationException` — современная замена, и в Spring Boot 3 они объединены в один класс `ValidationException` с общим `BindingResult`.
>
>   Никакого объединения не произошло — оба исключения сосуществуют в Spring Boot 3 и отвечают за разные слои (MVC vs AOP method validation). `ValidationException` существует в `jakarta.validation`, но это базовый класс, не «объединённая» замена. `BindingResult` живёт только в Spring Web и недоступен в `ConstraintViolationException`.
>
>   ПОСЛЕДСТВИЕ: построенный на этой ошибочной модели `@ControllerAdvice` пропускает один из типов исключений (обычно `ConstraintViolationException` от `@PathVariable`/`@RequestParam` с `@Validated` на контроллере) — пользователь получает stacktrace 500 вместо аккуратного 400.

## Q13. Как кастомизировать сообщения об ошибках валидации?

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
