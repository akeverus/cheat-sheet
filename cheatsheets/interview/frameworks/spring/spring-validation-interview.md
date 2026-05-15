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
