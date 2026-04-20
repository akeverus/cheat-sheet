---
title: "Вопросы на собеседовании: Java Records"
description: "Java Records (JEP 395, Java 16+): canonical/compact constructor, валидация, сериализация, pattern matching, интеграция со Spring, algebraic data types"
tags:
  - interview
  - java
  - java-records-interview
aliases:
  - "Java Records interview"
  - "Java Records собеседование"
  - "Java Records вопросы"
  - "Java 16 records interview"
  - "data classes Java"
difficulty: "intermediate"
updated: "2026-04-20"
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

## Q12. Как record взаимодействует с serialization?

```java
public record Event(String type, Instant timestamp) implements Serializable {}

// Стандартная Java serialization работает, но:
// - Только через canonical constructor (игнорирует обычную логику readObject)
// - Нет поддержки кастомной сериализации через writeObject/readObject
```

Для `Kryo`, `Protobuf`, Avro — обычно нужна генерация кода.

**Лучшая практика**: JSON/MessagePack через Jackson или kotlinx.serialization вместо Java Serialization.

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
