---
title: "Вопросы на собеседовании: Java Records"
description: "Java Records (JEP 395, Java 16+): canonical/compact constructor, валидация, сериализация, pattern matching, интеграция со Spring, algebraic data types"
tags:
  - interview
  - java
  - java-records-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Records"
  - "Java Records interview"
  - "Java Records собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Java Records`

`Java Records` (JEP 395, stable в Java 16) — компактный синтаксис для immutable data carriers. Автоматически генерирует конструктор, `equals`/`hashCode`/`toString` и accessor методы. Стандарт для DTO в современной Java и часто спрашиваются в интервью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [JEP 395: Records](https://openjdk.org/jeps/395) — JEP с описанием records
- [Oracle Records Tutorial](https://docs.oracle.com/en/java/javase/21/language/records.html) — официальный туториал
- [Baeldung: Java Records](https://www.baeldung.com/java-record-keyword) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое record в Java и зачем он нужен?

**Record** (Java 14 preview, 16 stable, JEP 395) — специальный вид класса для immutable data carriers. Компилятор автоматически генерирует:

- `private final` поля для каждого компонента.
- Конструктор со всеми компонентами.
- Accessor-методы (без префикса `get` — метод называется как поле).
- `equals()` и `hashCode()` на основе всех полей.
- `toString()` в формате `Point[x=1, y=2]`.

```java
public record Point(int x, int y) {}

// Эквивалентно:
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) { this.x = x; this.y = y; }

    public int x() { return x; }
    public int y() { return y; }

    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { ... }
    @Override public String toString() { return "Point[x=" + x + ", y=" + y + "]"; }
}
```

**Применение**: DTO, value objects, tuple-подобные структуры, ключи в Map, возврат multiple values из метода.


> [!mcq]
> - [x] Record — специальный final класс; компилятор генерирует constructor, accessor (без get), equals/hashCode/toString на основе компонентов | ✓ ПРИМЕНЯТЬ: для immutable DTO, value objects, ключей Map 📋 ПРАВИЛО: record = data без поведения → compiler does the boilerplate 🔗 См. Q2
> - [ ] Record — это interface-маркер, как Serializable, но для data классов | ❌ ПОСЛЕДСТВИЕ: marker interface не генерирует код — equals/hashCode/constructor писать вручную, весь смысл record теряется
> - [ ] Record — mutable JavaBean с автогенерированными геттерами/сеттерами | ❌ ПОСЛЕДСТВИЕ: record поля final — сеттер не скомпилируется; record не JavaBean, это противоположная концепция
> - [ ] Record полностью идентичен Kotlin data class, включая метод copy() | ❌ ПОСЛЕДСТВИЕ: Java record НЕ генерирует copy() — для изменения одного поля нужны wither-методы вручную

## Q2. Чем record отличается от обычного класса с Lombok @Value?

| Критерий | Lombok @Value | Record |
|----------|--------------|--------|
| Компилятор | Требует Lombok на classpath | Встроено в Java |
| Наследование | `final` класс | `final` (нельзя расширить) |
| Геттеры | `getX()` | `x()` (без префикса) |
| Поддержка IDE | Требует плагина | Без плагинов |
| Pattern matching | Нет | Да (Java 21+) |
| Serialization | Стандарт | Специфичное поведение |
| Java < 14 | Работает | Недоступен |

**Современный код на Java 17+**: предпочитайте records, они более предсказуемы и поддерживаются JVM.


> [!mcq]
> - [ ] @Value и record идентичны: accessor методы с префиксом get, требуют внешней библиотеки | ❌ ПОСЛЕДСТВИЕ: accessor record — x(), не getX(); Jackson без @JsonProperty будет игнорировать эти методы в некоторых конфигурациях
> - [ ] Record проигрывает @Value — не поддерживает pattern matching, @Value поддерживает | ❌ ПОСЛЕДСТВИЕ: всё наоборот: record поддерживает deconstructing patterns (Java 21+), Lombok @Value не имеет поддержки pattern matching
> - [x] Record встроен в JVM (Java 16+), не нужен Lombok; accessor без get-префикса; поддерживает pattern matching Java 21+; JPA @Entity предпочитает @Value из-за mutable требований | ✓ ПРИМЕНЯТЬ: для новых проектов на Java 17+ — record, для legacy или JPA — @Value 📋 ПРАВИЛО: record = языковая фича, @Value = кодогенерация 🔗 См. Q1
> - [ ] @Value добавляет copy() метод которого нет в record — поэтому @Value предпочтительнее | ❌ ПОСЛЕДСТВИЕ: отсутствие copy() решается wither-методами в record; преимущества record (pattern matching, JVM-native) перевешивают

## Q3. Что такое Canonical Constructor и Compact Constructor?

**Canonical constructor** — автоматически генерируемый конструктор со всеми компонентами.

**Compact constructor** — сокращённая форма для валидации/преобразования без явного присваивания полей:

```java
public record Age(int years) {

    // Compact constructor — без параметров в объявлении!
    public Age {
        if (years < 0) throw new IllegalArgumentException("Age cannot be negative");
        if (years > 150) throw new IllegalArgumentException("Age too large");
        // Присваивание this.years = years происходит АВТОМАТИЧЕСКИ в конце
    }

    // Каноничный конструктор (альтернатива compact)
    public Age(int years) {
        if (years < 0) throw new IllegalArgumentException(...);
        this.years = years;  // явное присваивание
    }
}

public record Email(String value) {
    public Email {
        Objects.requireNonNull(value);
        value = value.trim().toLowerCase();  // можно модифицировать параметр
        if (!value.contains("@")) throw new IllegalArgumentException("Invalid email");
        // this.value = value происходит автоматически с модифицированным значением
    }
}
```

**Важно**: в compact constructor можно модифицировать параметры до автоматического присваивания, но не сами `this.X` поля.


> [!mcq]
> - [ ] В compact constructor нужно явно писать this.years = years в конце блока | ❌ ПОСЛЕДСТВИЕ: compile error — поля record final и присваиваются автоматически после compact constructor; явный this.x = x запрещён
> - [x] Canonical constructor принимает все компоненты явно; compact constructor — сокращённая форма без параметров, параметры можно менять до автоматического присваивания this.X | ✓ ПРИМЕНЯТЬ: compact для валидации и нормализации (trim, toLowerCase) 📋 ПРАВИЛО: compact = validate before assign, no manual this.X= 🔗 См. Q4
> - [ ] Compact constructor компилируется только если компоненты primitive типов | ❌ ПОСЛЕДСТВИЕ: compact constructor работает с любыми типами — String, Object, List; именно String value = value.trim().toLowerCase() — типичный use case
> - [ ] Canonical constructor автоматически вызывается при добавлении дополнительного конструктора | ❌ ПОСЛЕДСТВИЕ: дополнительный конструктор ОБЯЗАН явно вызвать canonical через this(...); без этого — compile error "non-canonical record constructor must delegate to another constructor"

## Q4. Можно ли в record добавить методы и дополнительные конструкторы?

```java
public record Rectangle(double width, double height) {

    // Дополнительный конструктор — должен вызвать canonical
    public Rectangle(double side) {
        this(side, side);  // квадрат
    }

    // Обычные методы
    public double area() {
        return width * height;
    }

    public double perimeter() {
        return 2 * (width + height);
    }

    // Статические методы и поля
    public static final Rectangle UNIT = new Rectangle(1, 1);

    public static Rectangle square(double side) {
        return new Rectangle(side, side);
    }
}
```

**Ограничения**:
- Нельзя объявлять instance-поля (только компоненты в заголовке).
- Нельзя объявлять `native` методы.
- Можно static поля и методы.


> [!mcq]
> - [ ] В record нельзя добавлять методы — только компоненты в заголовке | ❌ ПОСЛЕДСТВИЕ: это неверно; instance методы (area(), perimeter()), static поля и методы добавляются свободно; ограничение только на instance fields вне заголовка
> - [ ] Дополнительный конструктор record может не вызывать canonical | ❌ ПОСЛЕДСТВИЕ: compile error "non-canonical record constructor must delegate to another constructor" — каждый не-canonical constructor обязан делегировать в canonical через this(...)
> - [ ] Static поля в record запрещены — нельзя объявить static final UNIT | ❌ ПОСЛЕДСТВИЕ: static поля и методы в record разрешены; static final Rectangle UNIT = new Rectangle(1,1) — корректный код
> - [x] Record поддерживает instance методы, static поля/методы, дополнительные конструкторы; дополнительный constructor обязан делегировать в canonical через this(...); instance fields вне заголовка запрещены | ✓ ПРИМЕНЯТЬ: добавлять методы-вычисления (area()), factory methods, validation конструкторы 📋 ПРАВИЛО: record = компоненты в заголовке + произвольные методы/static 🔗 См. Q3

## Q5. Можно ли наследоваться от record?

**Нет**. Records неявно `final`. Также records не могут наследоваться от других классов (только от `java.lang.Record`, который добавляется автоматически).

```java
// ОШИБКА
public class ColoredRectangle extends Rectangle { ... }

// ОК — record может реализовывать интерфейсы
public interface Shape {
    double area();
}

public record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

**Альтернатива** наследованию: композиция или sealed interfaces.

```java
public sealed interface Shape permits Circle, Square, Triangle {}
public record Circle(double radius) implements Shape {}
public record Square(double side) implements Shape {}
public record Triangle(double a, double b, double c) implements Shape {}
```


> [!mcq]
> - [x] Records неявно final — нельзя наследоваться; record может implements интерфейсы; альтернатива иерархии — sealed interface с несколькими record subtypes | ✓ ПРИМЕНЯТЬ: sealed interface + records для моделирования ADT (Result/Either/Shape) 📋 ПРАВИЛО: record = leaf node, sealed interface = union type 🔗 См. Q13
> - [ ] Record можно пометить non-final чтобы разрешить наследование | ❌ ПОСЛЕДСТВИЕ: запрещено спецификацией JLS; record всегда расширяет java.lang.Record и всегда final — compile error при попытке снять final
> - [ ] Record не может implements интерфейсы — только наследоваться от абстрактных классов | ❌ ПОСЛЕДСТВИЕ: всё наоборот: record implements interface разрешён, extends абстрактного класса — запрещён (только java.lang.Record)
> - [ ] Для наследования record используют вложенные records | ❌ ПОСЛЕДСТВИЕ: вложенные records — это не наследование, а просто inner classes; для иерархии типов нужен sealed interface permits Circle, Square

## Q6. Как сериализовать records с Jackson?

Jackson 2.12+ поддерживает records "из коробки":

```java
public record UserDto(String name, String email, int age) {}

// Сериализация
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(new UserDto("Alice", "alice@example.com", 30));
// {"name":"Alice","email":"alice@example.com","age":30}

// Десериализация
UserDto user = mapper.readValue(json, UserDto.class);
```

```java
// Кастомная сериализация через аннотации
public record ApiUser(
    @JsonProperty("user_name") String name,
    @JsonProperty("user_email") String email,
    @JsonIgnore String password
) {}
```

Для `kotlinx.serialization` — `@Serializable` на record работает аналогично data class.


> [!mcq]
> - [ ] Jackson сериализует record через геттеры с префиксом get — accessor x() игнорируется | ❌ ПОСЛЕДСТВИЕ: accessor record называются x(), не getX(); Jackson 2.12+ специально обрабатывает record accessors без get-префикса
> - [ ] Jackson < 2.12 требует @JsonDeserialize(builder=...) для record десериализации | ❌ ПОСЛЕДСТВИЕ: record не имеет builder — кастомный десериализатор или обновление Jackson до 2.12+; в 2.12+ canonical constructor используется автоматически
> - [x] Jackson 2.12+ поддерживает records автоматически; @JsonProperty/@JsonIgnore ставятся прямо на компоненты; канонический constructor используется при десериализации | ✓ ПРИМЕНЯТЬ: records как DTO в REST API без лишних аннотаций 📋 ПРАВИЛО: Jackson 2.12+ = record-native, компоненты = поля JSON 🔗 См. Q7
> - [ ] @JsonCreator обязателен на canonical constructor для десериализации record | ❌ ПОСЛЕДСТВИЕ: @JsonCreator не нужен — Jackson 2.12+ автоматически использует canonical constructor; лишняя аннотация только добавляет шум

## Q7. Как использовать records в Spring?

Records идеальны для DTO, commands, responses:

```java
// Request/Response DTO
public record CreateOrderRequest(
    @NotBlank String customerId,
    @NotEmpty List<OrderItemDto> items,
    @Min(0) BigDecimal totalAmount
) {}

public record OrderResponse(
    String id,
    String customerId,
    BigDecimal total,
    OrderStatus status,
    Instant createdAt
) {}

// Controller
@PostMapping("/orders")
public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
    Order order = orderService.create(request);
    return new OrderResponse(order.id(), order.customerId(), order.total(),
                              order.status(), order.createdAt());
}
```

```java
// Spring Data JPA (Java 16+)
@Entity
public class Order {
    // JPA требует @Entity с mutable полями и no-arg constructor → НЕ record
}

// НО можно использовать records для проекций:
public interface OrderRepository extends JpaRepository<Order, String> {

    // Интерфейсная проекция
    interface OrderSummary {
        String getId();
        BigDecimal getTotal();
    }

    // Record-проекция (Spring Data JPA 3.x)
    record OrderInfo(String id, String customerId, BigDecimal total) {}

    List<OrderInfo> findByStatus(OrderStatus status);
}
```


> [!mcq]
> - [ ] Record работает как JPA @Entity — @Column, @Id, @GeneratedValue поддерживаются | ❌ ПОСЛЕДСТВИЕ: JPA требует no-arg constructor и mutable fields — Spring Boot не запустится с "No default constructor for entity" при попытке использовать record как Entity
> - [x] Records идеальны как @RequestBody DTO с @Valid; JPA @Entity не совместим с record (нет no-arg constructor); record-проекции в Spring Data 3.x поддерживаются | ✓ ПРИМЕНЯТЬ: records для Request/Response DTO, Spring Data проекций; @Entity — обычный класс 📋 ПРАВИЛО: record = DTO layer, class = entity layer 🔗 См. Q10
> - [ ] @Valid не работает с records — Bean Validation не валидирует final поля | ❌ ПОСЛЕДСТВИЕ: @Valid работает с record — аннотации @NotBlank, @Email на компонентах обрабатываются Hibernate Validator корректно
> - [ ] Record не может быть Spring @Bean или @Component — только обычные классы | ❌ ПОСЛЕДСТВИЕ: это фактически верно (stateless record как bean бессмысленен), но ограничения нет — record как @Component скомпилируется; проблема в архитектурном смысле

## Q8. Поддерживают ли records pattern matching?

**Да, начиная с Java 21 (JEP 440)**:

```java
public sealed interface Shape permits Circle, Square {}
public record Circle(double radius) implements Shape {}
public record Square(double side) implements Shape {}

// Pattern matching в switch (Java 21+)
public static double area(Shape shape) {
    return switch (shape) {
        case Circle(double r) -> Math.PI * r * r;    // деконструкция record
        case Square(double s) -> s * s;
    };
}

// В if/else
if (shape instanceof Circle(double r)) {
    System.out.println("Circle with radius " + r);
}

// Вложенные patterns
public record Order(Customer customer, List<Item> items) {}
public record Customer(String name, Address address) {}
public record Address(String city, String country) {}

public static String orderCity(Order order) {
    return switch (order) {
        case Order(Customer(_, Address(String city, _)), _) -> city;
    };
}
```

Pattern matching делает records мощным инструментом функционального программирования в Java.


> [!mcq]
> - [ ] Record patterns в switch поддерживаются с Java 14 (первый preview records) | ❌ ПОСЛЕДСТВИЕ: record patterns — JEP 440, stable в Java 21; в Java 14-20 были только preview самих records без pattern matching
> - [ ] case Circle(double r) — синтаксис Kotlin, в Java switch такой код не скомпилируется | ❌ ПОСЛЕДСТВИЕ: record deconstruction patterns стабилизированы в Java 21 — этот синтаксис валидный Java код
> - [ ] Pattern matching в switch с records требует явного default: case | ❌ ПОСЛЕДСТВИЕ: когда switch exhaustively covers все subtypes sealed interface (Circle, Square) — default не нужен; компилятор проверяет покрытие
> - [x] Java 21 (JEP 440): record patterns в switch/instanceof деконструируют компоненты; case Circle(double r) → r доступен напрямую; вложенные patterns для nested records | ✓ ПРИМЕНЯТЬ: замена instanceof + cast + field access цепочек 📋 ПРАВИЛО: case Type(vars) = deconstructing, exhaustive switch без default 🔗 См. Q13

## Q9. Могут ли records быть generic?

```java
public record Pair<A, B>(A first, B second) {

    public <C> Pair<A, C> withSecond(C newSecond) {
        return new Pair<>(first, newSecond);
    }

    public Pair<B, A> swap() {
        return new Pair<>(second, first);
    }
}

public record Triple<A, B, C>(A first, B second, C third) {}

// Использование
Pair<String, Integer> pair = new Pair<>("age", 30);
Pair<String, String> swapped = new Pair<>("key", "value").swap()  // ВНИМАНИЕ: типы меняются
    .withSecond("new");
```

Generic records — удобный способ заменить `Map.Entry<K, V>` и Apache Commons `Pair`.


> [!mcq]
> - [x] Да — record Pair<A, B>(A first, B second) полностью рабочий; поддерживает type bounds <T extends Comparable<T>>; методы могут возвращать другие generic типы | ✓ ПРИМЕНЯТЬ: замена Map.Entry, Apache Commons Pair 📋 ПРАВИЛО: generic record = type-safe tuple 🔗 См. Q8
> - [ ] Generic records при runtime стирают типы — equals/hashCode не различает Pair<String, Int> и Pair<Int, String> | ❌ ПОСЛЕДСТВИЕ: type erasure не влияет на equals; Pair("a", 1).equals(Pair(1, "a")) — false, компоненты сравниваются через equals каждого типа
> - [ ] Record не поддерживает bounded wildcards <T extends Comparable<T>> | ❌ ПОСЛЕДСТВИЕ: record Sorted<T extends Comparable<T>>(List<T> items) компилируется нормально; bounded type parameters поддерживаются
> - [ ] Для generic records нужно явно переопределять equals и hashCode | ❌ ПОСЛЕДСТВИЕ: компилятор автоматически генерирует equals/hashCode из компонентов; для Pair<A, B> — через first.equals() и second.equals(); дополнительного override не требуется

## Q10. Как сделать record с валидацией через Bean Validation?

```java
public record UserRegistration(
    @NotBlank(message = "Username required")
    @Size(min = 3, max = 20)
    String username,

    @Email
    String email,

    @Min(18) @Max(120)
    int age,

    @Pattern(regexp = "^[A-Z].*", message = "Must start with capital letter")
    String name
) {}

// Валидация
@PostMapping("/register")
public UserResponse register(@Valid @RequestBody UserRegistration req) { ... }

// Или программно
Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
Set<ConstraintViolation<UserRegistration>> violations = validator.validate(req);
```


> [!mcq]
> - [ ] @NotBlank нельзя ставить на record component — нужен отдельный POJO с полями | ❌ ПОСЛЕДСТВИЕ: Bean Validation аннотации на record components работают в Spring Boot 3.x + Hibernate Validator; POJO не нужен
> - [ ] Compact constructor выполняется ДО Bean Validation и поэтому @Min/@Max не работают | ❌ ПОСЛЕДСТВИЕ: Bean Validation работает через validator.validate() независимо от конструктора; compact constructor и @Valid — разные механизмы, могут дополнять друг друга
> - [x] @NotBlank, @Email, @Min ставятся прямо на компоненты record; @Valid в @RequestBody вызывает валидацию; валидацию можно запустить программно через Validator.validate() | ✓ ПРИМЕНЯТЬ: для REST API DTO — декларативная валидация через аннотации без кода 📋 ПРАВИЛО: компоненты = Bean Validation target, @Valid = trigger 🔗 См. Q7
> - [ ] Для record нужен @Validated на классе вместо @Valid в параметрах метода | ❌ ПОСЛЕДСТВИЕ: @Valid в @RequestBody record работает; @Validated нужен для AOP метод-уровневой валидации в service layer, не для REST DTO

## Q11. Что такое equals/hashCode в records?

`equals()` — сравнение всех компонентов через их `equals()`. `hashCode()` — на основе всех компонентов.

```java
public record Point(int x, int y) {}

Point p1 = new Point(1, 2);
Point p2 = new Point(1, 2);
Point p3 = new Point(1, 3);

p1.equals(p2);    // true — все компоненты равны
p1.equals(p3);    // false — y разные
p1.hashCode() == p2.hashCode();  // всегда true если equals == true

// Применение: ключи HashMap
Map<Point, String> cities = new HashMap<>();
cities.put(new Point(0, 0), "Origin");
cities.get(new Point(0, 0));  // "Origin" — эквивалентность по значению
```

**Важно**: для массивов как компонентов — стандартный `equals` сравнивает по reference, не содержимое:

```java
public record Data(int[] values) {}

Data d1 = new Data(new int[]{1, 2, 3});
Data d2 = new Data(new int[]{1, 2, 3});
d1.equals(d2);  // false! массивы сравниваются по reference
```

Для массивов нужно переопределять `equals`/`hashCode` вручную или использовать `List<Integer>`.


> [!mcq]
> - [ ] equals() в record сравнивает по reference как Object.equals() — два Point(1,2) не равны | ❌ ПОСЛЕДСТВИЕ: record-equals сравнивает ВСЕ компоненты по значению; точно Point(1,2).equals(Point(1,2)) == true; это ключевое отличие от обычного класса
> - [x] equals() сравнивает все компоненты через их equals(); для массивов — reference equality, не содержимое → использовать List вместо array компонентов | ✓ ПРИМЕНЯТЬ: records как ключи в HashMap, в equals-чувствительных коллекциях 📋 ПРАВИЛО: array в record = broken equals, замени на List 🔗 См. Q15
> - [ ] hashCode() в record всегда возвращает константу для стабильности | ❌ ПОСЛЕДСТВИЕ: константный hashCode нарушил бы HashMap — все записи попадали бы в один bucket; hashCode вычисляется из всех компонентов через Objects.hash()
> - [ ] Arrays.equals() автоматически используется для array-компонентов record | ❌ ПОСЛЕДСТВИЕ: стандартный record-equals вызывает Object.equals() для каждого компонента; для массивов это reference equality; Data(new int[]{1,2}).equals(Data(new int[]{1,2})) == false

## Q12. Как record взаимодействует с serialization?

```java
public record Event(String type, Instant timestamp) implements Serializable {}

// Стандартная Java serialization работает, но:
// - Только через canonical constructor (игнорирует обычную логику readObject)
// - Нет поддержки кастомной сериализации через writeObject/readObject
```

Для `Kryo`, `Protobuf`, Avro — обычно нужна генерация кода.

**Лучшая практика**: JSON/MessagePack через Jackson или kotlinx.serialization вместо Java Serialization.


> [!mcq]
> - [ ] Records не поддерживают Java Serialization — implements Serializable не компилируется | ❌ ПОСЛЕДСТВИЕ: record implements Serializable работает; ограничение только в кастомных writeObject/readObject — они игнорируются
> - [ ] serialVersionUID не нужен для record — автоматически вычисляется из структуры | ❌ ПОСЛЕДСТВИЕ: без явного serialVersionUID JVM вычисляет UID из структуры; добавление нового компонента изменит UID → InvalidClassException при десериализации старых данных
> - [ ] Kryo автоматически поддерживает records без регистрации | ❌ ПОСЛЕДСТВИЕ: Kryo требует явной регистрации или кастомного сериализатора для records; без этого — InstantiatorStrategy exception или corrupt output
> - [x] Record implements Serializable работает через canonical constructor; customization через writeObject/readObject недоступна; предпочтительнее JSON через Jackson | ✓ ПРИМЕНЯТЬ: для REST API и Kafka — Jackson/Avro; Java Serialization только при необходимости 📋 ПРАВИЛО: record + Serializable = basic, no customization; используй Jackson для гибкости 🔗 См. Q6

## Q13. Как использовать records для Algebraic Data Types?

Records + sealed interfaces = ADT в Java:

```java
// Sum type: Result = Success | Failure
public sealed interface Result<T> permits Result.Success, Result.Failure {
    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(String error) implements Result<T> {}
}

// Pattern matching для обработки
public static <T> String describe(Result<T> result) {
    return switch (result) {
        case Result.Success<T>(T value) -> "OK: " + value;
        case Result.Failure<T>(String error) -> "ERR: " + error;
    };
}

// JSON-подобный ADT
public sealed interface JsonValue {
    record JsonNull() implements JsonValue {}
    record JsonBool(boolean value) implements JsonValue {}
    record JsonNumber(double value) implements JsonValue {}
    record JsonString(String value) implements JsonValue {}
    record JsonArray(List<JsonValue> items) implements JsonValue {}
    record JsonObject(Map<String, JsonValue> fields) implements JsonValue {}
}
```


> [!mcq]
> - [x] Sealed interface + records = ADT: sealed permits задаёт Sum type, exhaustive switch без default обязателен; record Success<T>(T value) и record Failure<T>(String error) как nested types | ✓ ПРИМЕНЯТЬ: для Result/Either/Option паттернов вместо try/catch и null 📋 ПРАВИЛО: sealed = union, record = product type, вместе = ADT 🔗 См. Q5
> - [ ] ADT в Java только через enum — record не может быть частью иерархии типов | ❌ ПОСЛЕДСТВИЕ: enum с abstract методами ограничен константами; records + sealed interface дают полноценные sum types с произвольными данными в каждом варианте
> - [ ] sealed interface без permits автоматически включает все records в пакете | ❌ ПОСЛЕДСТВИЕ: без permits любой класс в пакете может реализовать sealed interface; без permits нет exhaustiveness check в switch — теряется главное преимущество
> - [ ] record Success<T>(T value) не скомпилируется — generic вложенные records не поддерживаются | ❌ ПОСЛЕДСТВИЕ: generic вложенные records (record Success<T>(T value) implements Result<T>) поддерживаются в Java 16+; это стандартный паттерн

## Q14. Когда НЕ стоит использовать records?

1. **JPA сущности** — требуют no-arg constructor и mutable поля.
2. **Когда нужна mutable структура** — records неизменяемы.
3. **Наследование классов** — records `final`.
4. **Сложные объекты с поведением** — если метод модифицирует состояние, это не подходит для record.
5. **Legacy код на Java < 14** — недоступны.
6. **Equality по reference, а не по значению** — если нужна identity equality, обычный класс лучше.

```java
// ПЛОХО — у Order много бизнес-логики и изменяемое состояние
public record Order(...) {
    public void addItem(Item item) { ... }  // не работает, поля final
}

// ХОРОШО — Order как обычный класс, OrderSnapshot как record для передачи
public class Order { ... }  // entity с поведением
public record OrderSnapshot(String id, List<ItemDto> items, ...) {}  // DTO
```


> [!mcq]
> - [ ] Record подходит для JPA @Entity — аннотации @Column, @Id поддерживаются | ❌ ПОСЛЕДСТВИЕ: JPA требует no-arg constructor и mutable fields; record не имеет ни того ни другого — Spring Boot не запустится
> - [ ] Records можно использовать когда нужно часто изменять отдельные поля объекта | ❌ ПОСЛЕДСТВИЕ: record поля final — нельзя изменить; каждое "изменение" создаёт новый объект через wither; для часто-изменяемых объектов это memory pressure
> - [x] Не использовать для: JPA entity, mutable state, наследования классов, legacy Java < 14; идеально для: DTO, value objects, ADT, ключей Map | ✓ ПРИМЕНЯТЬ: record для data без поведения; класс — для entity с lifecycle 📋 ПРАВИЛО: record = immutable leaf, class = mutable stateful 🔗 См. Q1
> - [ ] Record нельзя объявить как local class внутри метода | ❌ ПОСЛЕДСТВИЕ: local records поддерживаются с Java 16; record Point(int x, int y) {} внутри метода — валидный код, полезно для локальных data carriers

## Q15. Какие best practices при работе с records?

1. **Используйте для immutable data carriers** — DTO, events, commands.

2. **Compact constructor для валидации**:

```java
public record Percentage(double value) {
    public Percentage {
        if (value < 0 || value > 100) throw new IllegalArgumentException();
    }
}
```

3. **Static factory methods для сложного создания**:

```java
public record Duration(long seconds) {
    public static Duration ofMinutes(int minutes) {
        return new Duration(minutes * 60L);
    }
}
```

4. **Records + sealed = ADT для моделирования бизнес-логики**.

5. **Избегайте array полей** — используйте `List` для правильного equals/hashCode.

6. **Не используйте для mutable состояния** — records по духу immutable.

7. **withers через копирование** (Lombok @With → ручные методы):

```java
public record Config(String name, int timeout, boolean enabled) {
    public Config withTimeout(int newTimeout) {
        return new Config(name, newTimeout, enabled);
    }
}
```

## See also


> [!mcq]
> - [ ] Record компоненты только simple types (String, int) — коллекции не поддерживаются | ❌ ПОСЛЕДСТВИЕ: List, Map, Optional как компоненты — нормально; проблема только с массивами (broken equals); List<Item> в record — рекомендованный подход
> - [x] Best practices: compact constructor для валидации; List вместо array; wither-методы для "изменения"; sealed+records для ADT; static factory для сложного создания | ✓ ПРИМЕНЯТЬ: record = immutable value with validation, not plain struct 📋 ПРАВИЛО: List > array, compact > manual null check, sealed+record > instanceof chains 🔗 См. Q3
> - [ ] Можно добавить instance setter в record для изменения конкретного поля | ❌ ПОСЛЕДСТВИЕ: поля record final — setter не скомпилируется; для "изменения" одного поля — wither-метод возвращающий новый record с изменённым значением
> - [ ] Compact constructor не нужен если использовать @NotNull аннотации | ❌ ПОСЛЕДСТВИЕ: @NotNull — Bean Validation, срабатывает только при явном validate(); compact constructor — fail-fast в точке создания объекта; без него невалидный объект попадёт в систему

- [Java 17-21](java-17-21-interview.md) — все новшества Java 17-21, sealed classes
- [Java Pattern Matching](java-pattern-matching-interview.md) — record patterns, deconstruction
- [Java OOP](java-oop-interview.md) — классы и объекты, наследование
- [Java Serialization](java-serialization-interview.md) — сериализация records
- [Java Generics](java-generics-interview.md) — generic records
- [Java Lombok](java-lombok-interview.md) — сравнение records и Lombok @Value
- [Java Core](java-core-interview.md) — основы Java
- [Java Jackson](java-jackson-interview.md) — serialization/deserialization records
- [Java Stream](java-stream-interview.md) — работа records в Stream API
- [Kotlin](../kotlin/kotlin-interview.md) — сравнение с Kotlin data classes
