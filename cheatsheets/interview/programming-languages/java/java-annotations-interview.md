---
title: "Вопросы на собеседовании: Java Annotations"
description: "Комплексное руководство по вопросам собеседования на тему Java Annotations для Senior Java Developer. Включает"
tags: ["interview", "programming-languages", "java-annotations-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Java Annotations`

Комплексное руководство по вопросам собеседования на тему `Java Annotations` для `Senior Java Developer`. Включает
детальные объяснения концепций, практические примеры на `Java` + `Spring`, best practices и troubleshooting.

Дата последнего обновления: 2026-01-29

## Полезные ссылки

### Официальная документация

- [Java Annotations Documentation](https://docs.oracle.com/javase/tutorial/java/annotations/)
- [Annotation API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/annotation/package-summary.html)
- [Reflection API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/reflect/package-summary.html)

### См. также

- [`../../../languages/java/java-annotations-reflection.md`](../../../languages/java/java-annotations-reflection.md) — аннотации и рефлексия
- [`java-core-interview.md`](java-core-interview.md) — вопросы по Java Core
- [`java-8-interview.md`](java-8-interview.md) — вопросы по Java 8

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Основы и создание аннотаций**
- [Q1. (!) Что такое `Annotation`?](#q1-важно-что-такое-annotation)
- [Q2. (!) Опишите некоторые полезные `Annotations` из стандартной библиотеки.](#q2-важно-опишите-некоторые-полезные-annotations-из-стандартной-библиотеки)
- [Q3. (!) Как создать `Annotation`?](#q3-важно-как-создать-annotation)
- [Q4. (!) Какие типы объектов могут быть возвращены из объявления метода `Annotation`?](#q4-важно-какие-типы-объектов-могут-быть-возвращены-из-объявления-метода-annotation)
- [Q5. (!) Какие элементы программы могут быть аннотированы?](#q5-важно-какие-элементы-программы-могут-быть-аннотированы)
- [Q6. Как создать аннотацию с несколькими атрибутами?](#q6-как-создать-аннотацию-с-несколькими-атрибутами)
- [Q7. Что такое `value()` и `default` в аннотациях?](#q7-что-такое-value-и-default-в-аннотациях)

**Мета-аннотации: `Target`, `Retention`, `ElementType`**
- [Q8. Как ограничить элементы, к которым можно применить `Annotation`?](#q8-как-ограничить-элементы-к-которым-можно-применить-annotation)
- [Q9. (!) Что такое `Meta-Annotations`?](#q9-важно-что-такое-meta-annotations)
- [Q10. Что такое `ElementType` и `Target`?](#q10-что-такое-elementtype-и-target)
- [Q11. Что такое `@Target`(ANNOTATION_TYPE)?](#q11-что-такое-targetannotation_type)
- [Q12. (!) Что такое `RetentionPolicy`?](#q12-важно-что-такое-retentionpolicy)
- [Q13. Что такое `@Documented` и `@Inherited`?](#q13-что-такое-documented-и-inherited)
- [Q14. Как использовать `RetentionPolicy.CLASS` для инструментации?](#q14-как-использовать-retentionpolicyclass-для-инструментации)

**Повторяемые и составные аннотации**
- [Q15. Что такое повторяющиеся `Annotations`?](#q15-что-такое-повторяющиеся-annotations)
- [Q16. Как получить повторяемые аннотации через `Reflection`?](#q16-как-получить-повторяемые-аннотации-через-reflection)
- [Q17. Что такое составные аннотации (`Composed Annotations`)?](#q17-что-такое-составные-аннотации-composed-annotations)

**Reflection и обработка аннотаций**
- [Q18. Как получить аннотации через `Reflection`?](#q18-как-получить-аннотации-через-reflection)
- [Q19. Как обработать аннотации в runtime?](#q19-как-обработать-аннотации-в-runtime)
- [Q20. Аннотации на параметрах методов (`Java` 8)?](#q20-аннотации-на-параметрах-методов-java-8)
- [Q21. Как получить аннотации с полей и параметров?](#q21-как-получить-аннотации-с-полей-и-параметров)
- [Q22. Как валидировать параметры аннотации?](#q22-как-валидировать-параметры-аннотации)
- [Q23. Как работают `Annotation Processors` с аннотациями `SOURCE`?](#q23-как-работают-annotation-processors-с-аннотациями-source)

**Использование во фреймворках (`Spring`, `Bean Validation`)**
- [Q24. Как аннотации используются в `Spring`?](#q24-как-аннотации-используются-в-spring)
- [Q25. Аннотации и прокси (`Spring AOP`)?](#q25-аннотации-и-прокси-spring-aop)
- [Q26. Как аннотации используются в `Bean Validation`?](#q26-как-аннотации-используются-в-bean-validation)

**Специальные случаи и best practices**
- [Q27. Будет ли компилироваться следующий код?](#q27-будет-ли-компилироваться-следующий-код)
- [Q28. Можно ли расширить `Annotations`?](#q28-можно-ли-расширить-annotations)
- [Q29. Что такое `@SafeVarargs` и `@FunctionalInterface`?](#q29-что-такое-safevarargs-и-functionalinterface)
- [Q30. Best practices при создании кастомных аннотаций?](#q30-best-practices-при-создании-кастомных-аннотаций)

## Q1. (!) Что такое `Annotation`?

Аннотации (`Annotations`) — механизм метапрограммирования в `Java`, позволяющий добавлять метаданные к элементам кода (классам, методам, полям, параметрам) без изменения их семантики. Введены в `Java` 5, стали частью современной `Java`-разработки, особенно в фреймворках типа `Spring`.

Аннотации — метаданные, привязанные к элементам исходного кода и не влияющие на работу кода. Предоставляют информацию, используемую компилятором, инструментами разработки, фреймворками и во время выполнения через рефлексию.

Типичные варианты использования: информация для компилятора (например, `@Override` гарантирует правильное переопределение метода), обработка во время компиляции и развертывания (`Lombok` генерирует геттеры, сеттеры), обработка во время выполнения (`Spring` использует аннотации для `dependency injection` и управления компонентами).

Пример использования аннотаций:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @Override
    public String toString() {
        return "User{id=" + id + "}";
    }
}

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }
}
```

## Q2. (!) Опишите некоторые полезные `Annotations` из стандартной библиотеки.

В пакетах `java.lang` и `java.lang.annotation` есть несколько полезных аннотаций. `@Override` отмечает, что метод предназначен для переопределения элемента суперкласса; компилятор выдаст ошибку, если переопределение неправильное. `@Deprecated` указывает, что элемент устарел и не должен использоваться; компилятор выдаст предупреждение при использовании помеченного элемента.

`@SuppressWarnings` указывает компилятору подавлять определенные предупреждения; часто используется при работе с унаследованным кодом, написанным до появления дженериков. `@FunctionalInterface` (`Java 8`) указывает, что интерфейс является функциональным и может быть реализован с помощью лямбда-выражения; компилятор проверит наличие ровно одного абстрактного метода.

Примеры использования:

```java
@Override
public String toString() {
    return "example";
}

@Deprecated
public void oldMethod() {
    // Устаревший метод
}

@SuppressWarnings("unchecked")
List<String> list = (List<String>) getRawList();

@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
}
```

## Q3. (!) Как создать `Annotation`?

Аннотации — форма интерфейса, где ключевому слову interface предшествует @, а тело содержит объявления элементов (похожи на методы). Элементы могут иметь значения по умолчанию через ключевое слово default. При указании нескольких значений для массива их необходимо заключать в квадратные скобки.

Пример создания аннотации:

```java
public @interface SimpleAnnotation {
    String value();
    int[] types();
}
```

Использование аннотации:

```java
@SimpleAnnotation(value = "element", types = 1)
public class Element {
    @SimpleAnnotation(value = "attribute", types = {1, 2})
    public Element nextElement;
}
```

Аннотация с значениями по умолчанию:

```java
public @interface SimpleAnnotation {
    String value() default "default value";
    int[] types() default {1, 2, 3};
}

@SimpleAnnotation // Можно использовать без параметров
public class Element {
}

@SimpleAnnotation(value = "custom") // Или указать только нужные
public class AnotherElement {
}
```

## Q4. (!) Какие типы объектов могут быть возвращены из объявления метода `Annotation`?

Тип возвращаемого значения метода аннотации должен быть примитивным типом, `String`, `Class`, `Enum`, аннотацией или массивом одного из перечисленных типов. Компилятор выдаст ошибку, если тип не соответствует этим требованиям.

Пример правильной аннотации:

```java
enum Complexity {LOW, HIGH}

public @interface ComplexAnnotation {
    Class<? extends Object> value();
    int[] types();
    Complexity complexity();
    String description() default "";
}
```

Неправильный пример (не скомпилируется):

```java
public @interface FailingAnnotation {
    Object complexity(); // Ошибка: Object не допустим
}
```

## Q5. (!) Какие элементы программы могут быть аннотированы?

Аннотации можно применять к объявлениям классов, конструкторов, полей, методов и их параметров, локальных переменных (включая переменные цикла и ресурсов), другим аннотациям и пакетам через файл `package-info.java`.

Примеры применения:

```java
@SimpleAnnotation
public class Apply {
    @SimpleAnnotation
    private String field;

    @SimpleAnnotation
    public Apply() {
    }

    @SimpleAnnotation
    public void method(@SimpleAnnotation String param) {
        @SimpleAnnotation
        int localVar = 10;
    }
}
```

Начиная с `Java` 8, аннотации можно применять к использованию типов (TYPE_USE) при указании `@Target(ElementType.TYPE_USE)`:

```java
@Target(ElementType.TYPE_USE)
public @interface SimpleAnnotation {
}

new @SimpleAnnotation Apply(); // Создание экземпляра
String str = (@SimpleAnnotation String) something; // Приведение типов
public class List<T> implements @SimpleAnnotation List<@SimpleAnnotation T> {}
void method() throws @SimpleAnnotation Exception {}
```

## Q6. Как создать аннотацию с несколькими атрибутами?

Методы аннотации (без параметров) задают атрибуты. При применении можно указать все атрибуты или использовать значения по умолчанию. Если есть атрибут value(), его можно указать без имени. Типы атрибутов: примитивы, `String`, `Class`, enum, аннотация, массивы перечисленного.

Пример:

```java
public @interface MyAnnotation {
    String value();
    int count() default 0;
    boolean enabled() default true;
}

@MyAnnotation(value = "test", count = 5) // Все атрибуты
@MyAnnotation("test") // Только value, остальные по умолчанию
public class MyClass {
}
```

## Q7. Что такое value() и default в аннотациях?

Атрибут value() имеет особый статус: при применении аннотации его можно указать без имени, если это единственный указанный атрибут. default значение позволяет не указывать атрибут при применении аннотации. default обязателен для всех полей, кроме одного (часто value), если аннотация должна применяться без аргументов (как `@Override`).

Пример:

```java
public @interface SimpleAnnotation {
    String value() default "default";
    int count() default 1;
}

@SimpleAnnotation // Все по умолчанию
@SimpleAnnotation("custom") // value = "custom", count = 1
@SimpleAnnotation(value = "custom", count = 5) // Оба указаны
public class MyClass {
}
```

## Q8. Как ограничить элементы, к которым можно применить `Annotation`?

Для ограничения элементов используется аннотация `@Target`. Если использовать аннотацию в неприменимом контексте, компилятор выдаст ошибку. Можно указать несколько констант `ElementType` для применения в разных контекстах. Можно также создать аннотацию без целевых элементов (`@Target`({})), если она предназначена только для использования в качестве типа-члена в сложных аннотациях.

Примеры:

```java
@Target(ElementType.FIELD) // Только поля
public @interface SimpleAnnotation {
}

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE}) // Несколько целей
public @interface MultiTargetAnnotation {
}

@Target({}) // Нет целевых элементов
public @interface NoTargetAnnotation {
}
```

## Q9. (!) Что такое `Meta-Annotations`?

Мета-аннотации — аннотации, которые применяются к другим аннотациям. Все аннотации, не отмеченные `@Target` или отмеченные с константой ANNOTATION_TYPE, являются мета-аннотациями. Стандартные мета-аннотации: `@Retention`, `@Target`, `@Inherited`, `@Documented`, `@Repeatable`.

Пример мета-аннотации:

```java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleAnnotation {
}
```

## Q10. Что такое `ElementType` и `Target`?

`ElementType` — перечисление целей аннотации, определяющее, к каким элементам кода можно применить аннотацию. Основные значения: `TYPE` (класс, интерфейс, enum), `FIELD`, `METHOD`, `PARAMETER`, `CONSTRUCTOR`, LOCAL_VARIABLE, ANNOTATION_TYPE, `PACKAGE`, TYPE_PARAMETER, TYPE_USE (`Java` 8). `@Target` используется для ограничения применения аннотации к конкретным элементам. Можно указать несколько целей через массив.

Примеры:

```java
@Target(ElementType.METHOD) // Только методы
public @interface MethodAnnotation {
}

@Target({ElementType.METHOD, ElementType.FIELD}) // Методы и поля
public @interface MultiTargetAnnotation {
}
```

## Q11. Что такое `@Target`(ANNOTATION_TYPE)?

`@Target(ElementType.ANNOTATION_TYPE)` указывает, что аннотация применима только к другим аннотациям, то есть является мета-аннотацией. Стандартные мета-аннотации: `@Retention`, `@Target`, `@Inherited`, `@Documented`, `@Repeatable`. Используется для создания семейств связанных аннотаций или составных аннотаций, которые объединяют несколько мета-аннотаций.

Пример мета-аннотации:

```java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationAnnotation {
    String message() default "";
}

@ValidationAnnotation(message = "Email validation")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
}
```

## Q12. (!) Что такое `RetentionPolicy`?

`@Retention` и `RetentionPolicy` определяют, как можно получить аннотации. `RetentionPolicy.SOURCE` — аннотации видимы только во время компиляции, удаляются после компиляции, используются для информации компилятору. `RetentionPolicy.CLASS` — аннотации сохраняются в байт-коде, но недоступны в runtime, используются для инструментации и анализа байт-кода. `RetentionPolicy.RUNTIME` — аннотации доступны во время выполнения через рефлексию, используются для обработки в runtime (`Spring`, валидация).

Выбор зависит от требований: `SOURCE` или `CLASS` для уменьшения размера кода, `RUNTIME` для обработки во время выполнения.

Пример:

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String value();
}

// Получение через рефлексию:
Description desc = AnnotatedClass.class.getAnnotation(Description.class);
System.out.println(desc.value());
```

## Q13. Что такое `@Documented` и `@Inherited`?

`@Documented` указывает, что аннотация должна попадать в `Javadoc` аннотированного элемента. Без `@Documented` аннотация не отображается в сгенерированной документации. Используется для аннотаций, важных для контракта `API` (например, `@Deprecated`).

`@Inherited` указывает, что аннотация класса наследуется подклассами. Без `@Inherited` подкласс не "видит" аннотацию родителя через `getAnnotation()`. Наследуются только аннотации классов; методы и поля не наследуют аннотации. `@Inherited` не распространяется на интерфейсы.

Пример:

```java
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritedAnnotation {
}

@InheritedAnnotation
public class Parent {
}

public class Child extends Parent {
    // Child.class.getAnnotation(InheritedAnnotation.class) вернёт аннотацию
}
```

## Q14. Как использовать `RetentionPolicy.CLASS` для инструментации?

`RetentionPolicy.CLASS` сохраняет аннотации в байт-коде, но они недоступны в runtime через рефлексию. Используется для инструментации (bytecode weaving) фреймворками типа `AspectJ`, анализа байт-кода инструментами, оптимизации без runtime `overhead`. `JVM` не загружает такие аннотации в runtime, что уменьшает использование памяти. Инструменты могут читать аннотации из байт-кода для модификации классов (добавление логирования, транзакций). Отличие от `SOURCE`: аннотации `CLASS` сохраняются в .class файлах, но не загружаются `JVM`.

Пример использования:

```java
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Instrumented {
    // Сохраняется в байт-коде для инструментации
}

@Instrumented // Может быть использована AspectJ для добавления логирования
public void method() {
}
```

## Q15. Что такое повторяющиеся `Annotations`?

Повторяющиеся аннотации можно применять более одного раза к одному объявлению элемента. В `Java` 8+ они хранятся в аннотации-контейнере, автоматически создаваемой компилятором. Объявление выполняется в два этапа: сначала объявляется повторяемая аннотация с `@Repeatable(Container.class)`, затем контейнерная аннотация с обязательным элементом value() типа массива повторяемой аннотации.

Пример:

```java
@Repeatable(Schedules.class)
public @interface Schedule {
    String time() default "morning";
}

public @interface Schedules {
    Schedule[] value();
}

@Schedule
@Schedule(time = "afternoon")
@Schedule(time = "night")
void scheduledMethod() {
}
```

## Q16. Как получить повторяемые аннотации через `Reflection`?

Для получения повторяемых аннотаций (`Java` 8+) используется `getAnnotationsByType(Class)`, который возвращает массив всех аннотаций указанного типа, включая повторяемые. Обычный `getAnnotation(Class)` вернёт `null` для повторяемых аннотаций, если они применены несколько раз. `getAnnotationsByType()` автоматически извлекает аннотации из контейнера, созданного компилятором. Также можно получить контейнерную аннотацию через `getAnnotation(Container.class)` и извлечь массив из её value().

Пример получения:

```java
@Repeatable(Schedules.class)
public @interface Schedule {
    String time();
}

@Schedule(time = "morning")
@Schedule(time = "evening")
public void scheduledMethod() {
}

// Получение всех повторяемых аннотаций:
Method method = MyClass.class.getMethod("scheduledMethod");
Schedule[] schedules = method.getAnnotationsByType(Schedule.class);
// schedules содержит обе аннотации: morning и evening

// Альтернативно через контейнер:
Schedules container = method.getAnnotation(Schedules.class);
if (container != null) {
    Schedule[] schedules2 = container.value();
}
```

## Q17. Что такое составные аннотации (`Composed Annotations`)?

Составная аннотация — аннотация, помеченная другими аннотациями (например, `@Service` = `@Component` + специфика). `Spring` и другие фреймворки объединяют мета-аннотации: при сканировании `@Service` считается подтипом `@Component`. Создание своей составной аннотации: `@MyAnnotation` с `@Component` и другими мета-аннотациями; при обработке проверять `getAnnotation(Component.class)` или `isAnnotationPresent()`. `Spring` использует `AnnotatedElementUtils` для поиска аннотаций с учётом мета-аннотаций.

Пример:

```java
@Component
@Scope("prototype")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrototypeComponent {
    // Составная аннотация, объединяющая @Component и @Scope
}

@PrototypeComponent // Эквивалентно @Component @Scope("prototype")
public class MyService {
}
```

## Q18. Как получить аннотации через `Reflection`?

Для получения аннотаций через `Reflection` используются методы: `Class.getAnnotation(Class)`, `getDeclaredAnnotation()`, `getAnnotations()` для аннотаций класса; `Method.getAnnotation()`, `Field.getAnnotation()` для аннотаций метода и поля; `Parameter.getAnnotation()` (`Java` 8) для аннотаций параметра. Аннотация должна иметь `@Retention`(`RetentionPolicy.RUNTIME`), иначе в runtime недоступна. `isAnnotationPresent()` проверяет наличие аннотации.

Пример:

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value();
}

@MyAnnotation("test")
public class MyClass {
}

// Получение аннотации:
MyAnnotation annotation = MyClass.class.getAnnotation(MyAnnotation.class);
if (annotation != null) {
    System.out.println(annotation.value()); // "test"
}
```

## Q19. Как обработать аннотации в runtime?

Обработка аннотаций в runtime выполняется через `Reflection API`. Необходимо сканировать классы (например, по пакету), для каждого класса использовать `Class.getDeclaredMethods()`, `getAnnotation(MyAnnotation.class)`. Если аннотация найдена, выполнить логику (регистрация, вызов). `Spring` делает это при `Component Scan`; кастомная обработка — цикл по классам и методам с `getAnnotation()`. Учитывать наследование: `getAnnotation()` на классе возвращает унаследованные при `@Inherited`.

Пример:

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessMe {
}

@ProcessMe
public class MyService {
    public void doWork() {
        System.out.println("Working...");
    }
}

// Обработка:
Class<?> clazz = MyService.class;
if (clazz.isAnnotationPresent(ProcessMe.class)) {
    Object instance = clazz.getDeclaredConstructor().newInstance();
    // Выполнить логику обработки
}
```

## Q20. Аннотации на параметрах методов (`Java` 8)?

`ElementType.PARAMETER` позволяет применять аннотации к параметрам методов. Чтение выполняется через `Method.getParameters()` (`Java` 8), затем `Parameter.getAnnotation()`. Флаг `-parameters` при компиляции сохраняет имена параметров; иначе `parameter.getName()` может вернуть arg0, arg1 и т.д. Используется для валидации (`@NotNull`, `@Valid`), документирования, `dependency injection` (`Spring @RequestParam`, `@PathVariable`).

Пример:

```java
public void method(@NotNull @Valid @RequestBody User user, 
                   @RequestParam String name) {
}

// Получение аннотаций:
Method method = MyClass.class.getMethod("method", User.class, String.class);
Parameter[] parameters = method.getParameters();
NotNull notNull = parameters[0].getAnnotation(NotNull.class);
```

## Q21. Как получить аннотации с полей и параметров?

`Field.getAnnotation()`, `Field.getDeclaredAnnotations()` возвращают аннотации поля. `Method.getParameterAnnotations()` возвращает массив массивов аннотаций по параметрам (каждый элемент соответствует параметру). `Parameter.getAnnotation()` (`Java` 8) возвращает аннотацию конкретного параметра. Для вложенных аннотаций (например, `@Valid` внутри `@RequestBody`) требуется рекурсивный обход через `getDeclaredAnnotations()`.

Пример:

```java
public class User {
    @NotNull
    @Size(min = 3)
    private String username;
}

// Получение аннотаций поля:
Field field = User.class.getDeclaredField("username");
NotNull notNull = field.getAnnotation(NotNull.class);
Annotation[] annotations = field.getDeclaredAnnotations();
```

## Q22. Как валидировать параметры аннотации?

Ограничения задаются типами возвращаемых значений методов аннотации (примитивы, `String`, `Class`, enum, аннотация, массивы). Компилятор проверяет соответствие типов при применении. Сложная валидация выполняется в `Annotation Processor` во время компиляции или при чтении через рефлексию в runtime. `Bean Validation` (`@Valid`) — отдельный механизм; аннотации валидации обрабатываются валидатором (`Hibernate Validator`), а не вручную.

Пример валидации в runtime:

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRange {
    int min() default 0;
    int max() default 100;
}

// При обработке проверять:
ValidRange annotation = field.getAnnotation(ValidRange.class);
if (annotation.min() > annotation.max()) {
    throw new IllegalArgumentException("Invalid range");
}
```

## Q23. Как работают `Annotation Processors` с аннотациями `SOURCE`?

`Annotation Processors` обрабатывают аннотации с `@Retention(RetentionPolicy.SOURCE)` во время компиляции. Компилятор вызывает процессоры для аннотаций, которые они поддерживают; процессоры могут генерировать код, создавать файлы, сообщать об ошибках. Аннотации `SOURCE` удаляются после компиляции и недоступны в runtime. Используются в `Lombok` (генерация геттеров, сеттеров), `MapStruct` (генерация мапперов), `Dagger` (генерация `DI` кода). Процессор регистрируется в `META-INF/services/javax.annotation.processing.Processor` (имя класса процессора в файле).

Пример процессора:

```java
@SupportedAnnotationTypes("com.example.GenerateBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, 
                          RoundEnvironment roundEnv) {
        // Генерация кода на основе аннотаций
        return true;
    }
}
```

## Q24. Как аннотации используются в `Spring`?

`Spring` сканирует классы на аннотации `@Component`, `@Service`, `@Controller`, `@Repository` и регистрирует их как `Bean`'ы. `@Autowired`, `@Value` используются для инъекции зависимостей. `@RequestMapping`, `@GetMapping`, `@PostMapping` — для маппинга `URL`. `@Transactional`, `@Cacheable` — для декларативного управления транзакциями и кэшированием. Обработка выполняется через рефлексию и прокси; аннотации с `RUNTIME` retention читаются при старте контекста и при обработке запросов.

Пример:

```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Transactional
    public User save(User user) {
        return repository.save(user);
    }
}
```

## Q25. Аннотации и прокси (`Spring AOP`)?

`Spring AOP` создаёт прокси для `Bean` с аннотированными методами (например, `@Transactional`, `@Cacheable`). При вызове метода проверяется наличие аннотации на методе или классе; применяется соответствующий совет (транзакция, кэш, логирование). Аннотации должны иметь `@Retention`(`RetentionPolicy.RUNTIME`); читаются при создании прокси (при старте контекста) и при инвокации метода. `Spring` использует `JDK` динамические прокси или `CGLIB` прокси в зависимости от интерфейсов.

Пример:

```java
@Service
public class UserService {
    @Transactional // Создаётся прокси с транзакционным советом
    public User save(User user) {
        return repository.save(user);
    }
}
```

## Q26. Как аннотации используются в `Bean Validation`?

`Bean Validation` использует аннотации для валидации данных: `@NotNull`, `@Size`, `@Email`, `@Min`, `@Max`, `@Pattern` и др. `@Valid` запускает валидацию вложенных объектов. Кастомные аннотации валидации создаются через `@Constraint` и реализацию `ConstraintValidator`. Валидация выполняется валидатором (например, `Hibernate Validator`) при вызове `Validator.validate()`, а не вручную через рефлексию. Аннотации валидации должны иметь `@Retention`(`RetentionPolicy.RUNTIME`).

Пример:

```java
@Constraint(validatedBy = AgeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Invalid age";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min() default 0;
    int max() default 150;
}

public class User {
    @ValidAge(min = 18, max = 100)
    private Integer age;
}
```

## Q27. Будет ли компилироваться следующий код?

Нет, это ошибка компиляции, если одна и та же константа перечисления появляется более одного раза в аннотации `@Target`.

Неправильно:

```java
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.FIELD}) // Дубликат FIELD
public @interface TestAnnotation {
    int[] value() default {};
}
```

Правильно:

```java
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface TestAnnotation {
    int[] value() default {};
}
```

## Q28. Можно ли расширить `Annotations`?

Нет. Аннотации всегда расширяют `java.lang.annotation.Annotation`, как указано в Спецификации языка `Java`. Аннотации не могут наследоваться от других аннотаций через ключевое слово `extends` — попытка использовать `extends` приведёт к ошибке компиляции.

Альтернативные подходы: композиция аннотаций (включение одной аннотации как элемента другой) и мета-аннотации (создание семейства связанных аннотаций через общую мета-аннотацию).

Пример композиции:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseAnnotation {
    String value() default "";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendedAnnotation {
    String value() default "";
    BaseAnnotation base() default @BaseAnnotation; // Композиция
}

@ExtendedAnnotation(value = "test", base = @BaseAnnotation("base"))
public class MyClass {
}
```

Пример мета-аннотации:

```java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationAnnotation {
    String message() default "";
}

@ValidationAnnotation(message = "Email validation")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
}
```

## Q29. Что такое `@SafeVarargs` и `@FunctionalInterface`?

`@SafeVarargs` подавляет предупреждения о небезопасных операциях с varargs при использовании дженериков. Можно применять только к final методам, конструкторам и private static методам. Указывает, что метод безопасно обрабатывает varargs параметры без `heap` pollution. `@FunctionalInterface` указывает, что интерфейс является функциональным (содержит ровно один абстрактный метод); компилятор проверит это и выдаст ошибку, если условие не выполнено. Позволяет использовать лямбда-выражения для реализации интерфейса.

Пример:

```java
@SafeVarargs
public final <T> void process(T... items) {
    // Обработка varargs без предупреждений
}

@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
    // Компилятор проверит наличие ровно одного абстрактного метода
}
```

## Q30. Best practices при создании кастомных аннотаций?

Именование: существительное или прилагательное (`@Transactional`, `@Cacheable`). Минимум полей; value для единственного атрибута. `@Documented` для `API`-аннотаций, важных для контракта. `@Retention`(`RUNTIME`) для обработки в runtime; `CLASS` или `SOURCE` для процессоров. Документировать семантику и контракт аннотации. Не дублировать стандартные или фреймворковые аннотации. Использовать осмысленные значения по умолчанию. Группировать связанные аннотации через мета-аннотации. Указывать `@Target` для ограничения применения. Проверять валидность значений атрибутов при обработке.

Пример:

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogExecution {
    /**
     * Уровень логирования
     * @return уровень логирования
     */
    LogLevel level() default LogLevel.INFO;

    boolean logParameters() default true;

    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}
```

