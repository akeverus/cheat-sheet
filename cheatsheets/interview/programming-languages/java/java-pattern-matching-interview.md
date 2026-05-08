---
title: "Вопросы на собеседовании: Java Pattern Matching"
description: "Pattern Matching в Java (JEP 441, 440): instanceof patterns, switch expressions, record patterns, guarded patterns, sealed types, exhaustiveness checking"
tags:
  - interview
  - java
  - java-pattern-matching-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Pattern Matching"
  - "Pattern Matching собеседование"
  - "Java 21 pattern matching"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Java Pattern Matching`

`Pattern Matching` — серия JEP в Java 16-21, значительно расширяющая возможности `instanceof` и `switch`. Включает type patterns, record patterns, guarded patterns и exhaustiveness checking с sealed типами. Горячая тема в современных Java интервью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) — JEP для switch
- [JEP 440: Record Patterns](https://openjdk.org/jeps/440) — JEP для record patterns
- [Baeldung: Pattern Matching](https://www.baeldung.com/java-pattern-matching-instanceof) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Pattern Matching в Java и какие формы он принимает?

**Pattern Matching** — возможность тестировать значение против шаблона и извлекать данные за одну операцию. В Java развивалось постепенно:

- **Java 16**: Pattern matching for `instanceof` (JEP 394).
- **Java 21**: Pattern matching for `switch` (JEP 441), Record patterns (JEP 440).
- **Java 22+**: Unnamed patterns (JEP 456).

```java
// До pattern matching — verbose и небезопасно
Object obj = ...;
if (obj instanceof String) {
    String s = (String) obj;      // ручной cast
    System.out.println(s.length());
}

// С pattern matching (Java 16+)
if (obj instanceof String s) {    // auto-cast + binding
    System.out.println(s.length());
}

// В switch (Java 21+)
String result = switch (obj) {
    case String s -> "String: " + s;
    case Integer i -> "Int: " + i;
    case null -> "null";
    default -> "Unknown";
};
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q2. Что нового в instanceof pattern matching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
Object obj = getValue();

// Type test + binding variable
if (obj instanceof Integer i) {
    System.out.println(i + 1);   // i уже Integer, auto-unboxing
}

// Scope binding — i доступен только если условие true
if (obj instanceof String s && !s.isEmpty()) {
    System.out.println(s.length());
}

// Работает с && и ||
if (!(obj instanceof String s)) {
    return -1;  // s не доступен здесь
}
// ЗДЕСЬ s доступен (flow scoping)
return s.length();

// Negative pattern
if (!(obj instanceof String s)) {
    throw new IllegalArgumentException();
}
use(s);  // можно, компилятор знает, что s определён
```

**Flow scoping** — компилятор анализирует поток управления и расширяет область видимости binding variable.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q3. Что такое switch expressions и как они связаны с pattern matching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// Старый switch statement — requires break, падения вниз
switch (day) {
    case MONDAY:
    case FRIDAY:
    case SUNDAY:
        System.out.println(6);
        break;
    case TUESDAY:
        System.out.println(7);
        break;
    default:
        System.out.println(0);
}

// Java 14+ switch expression — стрелки, нет падения, возвращает значение
int letters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    case THURSDAY, SATURDAY -> 8;
    case WEDNESDAY -> 9;
};

// Java 21+ pattern matching в switch
String format(Object obj) {
    return switch (obj) {
        case Integer i -> "int %d".formatted(i);
        case Long l -> "long %d".formatted(l);
        case Double d -> "double %f".formatted(d);
        case String s -> "String %s".formatted(s);
        case null, default -> "Unknown";
    };
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. Что такое Record Patterns? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Record Pattern** — деконструкция record через pattern matching (Java 21, JEP 440):

```java
public record Point(int x, int y) {}
public record Rectangle(Point topLeft, Point bottomRight) {}

// Деконструкция в instanceof
Object obj = new Point(3, 5);
if (obj instanceof Point(int x, int y)) {
    System.out.println("x=" + x + ", y=" + y);
}

// В switch
int area(Object shape) {
    return switch (shape) {
        case Point(int x, int y) -> 0;  // точка имеет нулевую площадь
        case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) ->
            Math.abs((x2 - x1) * (y2 - y1));
        default -> -1;
    };
}

// Вложенная деконструкция
record Pair<A, B>(A first, B second) {}
record Triple<A, B, C>(A a, B b, C c) {}

Object obj = new Triple<>(1, new Pair<>("a", "b"), 3.0);

if (obj instanceof Triple(Integer i, Pair(String s1, String s2), Double d)) {
    System.out.println(i + ", " + s1 + s2 + ", " + d);
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Что такое Sealed Classes и как они связаны с pattern matching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Sealed classes/interfaces** (Java 17, JEP 409) ограничивают иерархию классов. Компилятор может доказать exhaustiveness pattern matching.

```java
public sealed interface Shape permits Circle, Square, Triangle {}
public record Circle(double radius) implements Shape {}
public record Square(double side) implements Shape {}
public record Triangle(double base, double height) implements Shape {}

// Exhaustive switch — компилятор знает ВСЕ возможные подклассы
double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square s -> s.side() * s.side();
        case Triangle t -> 0.5 * t.base() * t.height();
        // default НЕ нужен — exhaustiveness доказан
    };
}

// При добавлении новой формы → ошибка компиляции
// public record Oval(double width, double height) implements Shape {}
// area() больше не exhaustive → нужно добавить case
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое Guarded Patterns (with-clause)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Guarded pattern** — комбинация pattern matching с булевым условием:

```java
String categorize(Object obj) {
    return switch (obj) {
        // Guard с when (Java 21+)
        case Integer i when i < 0 -> "Negative int";
        case Integer i when i == 0 -> "Zero";
        case Integer i when i > 100 -> "Large int";
        case Integer i -> "Small positive int";
        case String s when s.length() > 10 -> "Long string";
        case String s -> "Short string";
        default -> "Unknown";
    };
}
```

**Порядок имеет значение**: guards проверяются сверху вниз, первый match выигрывает.

```java
// Правильно: от специфичного к общему
case Integer i when i == 0 -> "zero";
case Integer i when i < 0 -> "negative";
case Integer i -> "positive";  // fallback

// НЕПРАВИЛЬНО: общий case "съест" специфичные
case Integer i -> "positive";   // всегда matches
case Integer i when i == 0 -> ...;  // недостижим!
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как обрабатывать null в pattern matching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// ДО Java 21 — switch бросал NPE на null
switch (obj) {
    case String s -> ...;  // если obj == null → NullPointerException
}

// Java 21+ — явная обработка null
String handle(Object obj) {
    return switch (obj) {
        case null -> "null value";              // явно
        case String s -> "String: " + s;
        case Integer i -> "Int: " + i;
        default -> "Unknown";
    };
}

// Комбинация null с default
String handle2(Object obj) {
    return switch (obj) {
        case Integer i -> "Int";
        case null, default -> "null or unknown";  // null OR default
    };
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как использовать pattern matching для visitor-паттерна? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Pattern matching часто заменяет классический Visitor:

```java
// Классический Visitor (verbose)
public interface ShapeVisitor<R> {
    R visit(Circle c);
    R visit(Square s);
    R visit(Triangle t);
}

public interface Shape {
    <R> R accept(ShapeVisitor<R> visitor);
}
// ...много бойлерплейта

// Pattern matching (Java 21+)
double area(Shape shape) {
    return switch (shape) {
        case Circle(double r) -> Math.PI * r * r;
        case Square(double s) -> s * s;
        case Triangle(double b, double h) -> 0.5 * b * h;
    };
}

double perimeter(Shape shape) {
    return switch (shape) {
        case Circle(double r) -> 2 * Math.PI * r;
        case Square(double s) -> 4 * s;
        case Triangle(double b, double h) -> {
            double c = Math.sqrt(b * b + h * h);
            yield b + h + c;
        }
    };
}
```

**Преимущество pattern matching**: не нужно модифицировать иерархию классов при добавлении операции, код читаемее.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Что такое yield в switch expression? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`yield` — возврат значения из блока case со сложной логикой:

```java
int process(int x) {
    return switch (x) {
        case 0 -> 0;  // простое выражение
        case 1 -> {
            System.out.println("Processing 1");
            int result = computeSomething();
            yield result + 1;  // возврат из блока
        }
        default -> -1;
    };
}

// В pattern matching
String describe(Object obj) {
    return switch (obj) {
        case Integer i -> {
            int squared = i * i;
            yield "Int " + i + " squared = " + squared;
        }
        case String s -> "String: " + s;
        default -> "Other";
    };
}
```

`yield` используется только внутри блочных `{ }` case; для однострочных достаточно `->`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Что такое Exhaustiveness Check? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Exhaustiveness** — компилятор гарантирует, что switch покрывает все возможные значения.

```java
// Exhaustive для sealed type
sealed interface Result permits Success, Failure {}
record Success(String value) implements Result {}
record Failure(String error) implements Result {}

String handle(Result r) {
    return switch (r) {
        case Success s -> s.value();
        case Failure f -> "ERR: " + f.error();
        // компилятор ДОКАЗАЛ exhaustiveness
    };
}

// Для Object — нужен default
String handleAny(Object o) {
    return switch (o) {
        case Integer i -> "int";
        case String s -> "string";
        default -> "other";  // требуется, т.к. Object — неограничен
    };
}

// Для enum без default — exhaustive если все constants
enum Color { RED, GREEN, BLUE }

String colorName(Color c) {
    return switch (c) {
        case RED -> "Red";
        case GREEN -> "Green";
        case BLUE -> "Blue";
        // exhaustive — все варианты enum покрыты
    };
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как pattern matching работает с generic types? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// Деконструкция generic record
record Box<T>(T value) {}

Object obj = new Box<String>("hello");

// Generic type inference
if (obj instanceof Box<?>(Object value)) {
    System.out.println(value);  // Object
}

// Явный type parameter
if (obj instanceof Box<String>(String value)) {
    System.out.println(value.toUpperCase());  // String methods
}

// В switch
<T> String describe(Box<T> box) {
    return switch (box) {
        case Box(Integer i) -> "Integer box: " + i;  // raw или inferred
        case Box(String s) -> "String box: " + s;
        case Box<T>(T value) -> "Generic box: " + value;
    };
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Какие практические применения pattern matching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// 1. Type dispatcher для JSON
sealed interface JsonValue {}
record JsonNull() implements JsonValue {}
record JsonNumber(double value) implements JsonValue {}
record JsonString(String value) implements JsonValue {}
record JsonArray(List<JsonValue> items) implements JsonValue {}
record JsonObject(Map<String, JsonValue> fields) implements JsonValue {}

String render(JsonValue json) {
    return switch (json) {
        case JsonNull n -> "null";
        case JsonNumber(double v) -> String.valueOf(v);
        case JsonString(String s) -> "\"" + s + "\"";
        case JsonArray(var items) -> items.stream()
            .map(this::render)
            .collect(joining(",", "[", "]"));
        case JsonObject(var fields) -> fields.entrySet().stream()
            .map(e -> "\"" + e.getKey() + "\":" + render(e.getValue()))
            .collect(joining(",", "{", "}"));
    };
}

// 2. Event handling
sealed interface DomainEvent {}
record OrderCreated(String orderId, String customerId) implements DomainEvent {}
record OrderCancelled(String orderId, String reason) implements DomainEvent {}
record PaymentReceived(String orderId, BigDecimal amount) implements DomainEvent {}

void handle(DomainEvent event) {
    switch (event) {
        case OrderCreated(var id, var cust) -> sendConfirmationEmail(cust, id);
        case OrderCancelled(var id, var reason) -> logCancellation(id, reason);
        case PaymentReceived(var id, var amt) -> updateOrderStatus(id, CONFIRMED);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. В чём отличие Pattern Matching в Java от Scala/Kotlin? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Критерий | Java 21+ | Scala | Kotlin |
|----------|----------|-------|--------|
| Базовое matching | `instanceof`, `switch` | `match` | `when` |
| Record deconstruction | ✓ (Java 21) | case classes | data class (не матчится напрямую) |
| Guards | `when` clause | `if` guards | `when (x) { is T -> if(...)` |
| Exhaustiveness (sealed) | ✓ | ✓ | `sealed` + exhaustive `when` |
| Type inference | Частично | Полно | Частично |
| List patterns | ✗ | `List(a, b, _*)` | Только через destructuring |
| Variable binding | `Point(int x, int y)` | `case Point(x, y)` | `is Point -> { val x = it.x }` |

Pattern matching в Java пока беднее Scala, но догоняет. Scala и Kotlin не имеют ограничений на arrow-style patterns.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как pattern matching взаимодействует с преобразованием типов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// Unboxing через pattern
Object obj = Integer.valueOf(42);
if (obj instanceof Integer i) {
    int j = i + 1;  // auto-unboxing
}

// Deconstruction автоматически делает auto-unboxing
record Box(int value) {}  // int primitive
Box b = new Box(42);
if (b instanceof Box(int v)) {  // v — int, не Integer
    int doubled = v * 2;
}

// Но не работает на generic bounds
record Wrapper<T>(T value) {}
Object obj = new Wrapper<>(42);
// if (obj instanceof Wrapper<Integer>(int v))  // ОШИБКА — Integer не int
if (obj instanceof Wrapper<Integer>(Integer v)) { ... }
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Когда стоит/не стоит использовать pattern matching? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Стоит использовать**:
- Работа с sealed hierarchy (exhaustive matching).
- Обработка иерархии с разной структурой данных (ADT).
- Замена цепочек `if (x instanceof Y)`.
- Тяжёлая работа с records.
- Visitor-like паттерны.

**Не стоит использовать**:
- **Бизнес-логика в switch** — если cases содержат много кода, лучше extraction в отдельные методы.
- **OOP-иерархии с полиморфизмом** — когда можно решить через virtual-методы:

```java
// ПЛОХО: pattern matching — нарушает OCP
double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square s -> s.side() * s.side();
    };
}

// ЛУЧШЕ: полиморфизм
sealed interface Shape { double area(); }
record Circle(double radius) implements Shape {
    public double area() { return Math.PI * radius * radius; }
}
```

- **Слишком много guards** — усложняет понимание, лучше extract в методы.

**Правило**: pattern matching — для выражения структуры данных и dispatch; для поведения — полиморфизм.

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Java 17-21](java-17-21-interview.md) — все новшества Java 17-21 ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Java Records](java-records-interview.md) — records + pattern matching (record patterns)
- [Java OOP](java-oop-interview.md) — sealed classes, иерархии типов
- [Java Types](java-types-interview.md) — типы и приведение, instanceof
- [Java Conditional](java-conditional-statements-interview.md) — if/switch, evolution через pattern matching
- [Java Generics](java-generics-interview.md) — generic patterns
- [Java Exceptions](java-exceptions-interview.md) — sealed exceptions
- [Kotlin](../kotlin/kotlin-interview.md) — сравнение с Kotlin when expression и sealed classes
- [Scala](../scala/scala-interview.md) — оригинальный pattern matching, откуда позаимствовано
- [Java Core](java-core-interview.md) — базовые концепции
