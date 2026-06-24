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
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Java Pattern Matching`

`Pattern Matching` — серия JEP в Java 16-21, заметно расширяющая `instanceof` и `switch`: проверка типа теперь сразу даёт типизированную переменную, а `switch` умеет матчить типы и деконструировать records. Сюда входят type patterns, record patterns, guarded patterns и exhaustiveness checking по sealed-типам. Частая тема современных Java-собеседований.

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

**Pattern Matching** проверяет значение на соответствие шаблону и одновременно извлекает из него данные — за одну операцию. Раньше это были два разных шага: сначала `instanceof`-проверка типа, потом ручной cast с объявлением переменной. Pattern matching совмещает их: проверка типа сразу даёт типизированную переменную (binding variable), без явного приведения.

**Почему это важно.** Ручной cast — источник дублирования и ошибок: тип пишется дважды (в `instanceof` и в скобках cast), компилятор не страхует от рассинхрона. Pattern matching убирает дублирование и делает код безопаснее.

В Java фича развивалась поэтапно, расширяясь от `instanceof` к `switch` и деконструкции records:

- **Java 16**: pattern matching для `instanceof` (JEP 394) — type pattern с binding variable.
- **Java 21**: pattern matching для `switch` (JEP 441) и record patterns (JEP 440) — деконструкция records и exhaustiveness с sealed-типами.
- **Java 22+**: unnamed patterns (JEP 456) — `_` для компонентов, которые не нужны.

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

## Q2. Что нового в `instanceof` pattern matching и что такое flow scoping?

В `instanceof` появился **type pattern** — `obj instanceof Type var`. Если проверка проходит, компилятор сам приводит значение и кладёт его в переменную `var` уже нужного типа. Cast и отдельное объявление переменной больше не пишутся вручную.

**Flow scoping** — компилятор анализирует поток управления и делает binding variable видимой ровно там, где её тип гарантированно подтверждён, а не строго в `if`-блоке.

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

Ключевые свойства:

- **Сужение в `&&`:** в `obj instanceof String s && !s.isEmpty()` переменная `s` уже доступна во второй части условия — к моменту её проверки тип уже подтверждён.
- **Расширение через early return/throw:** после `if (!(obj instanceof String s)) return ...;` компилятор понимает, что в остальном коде метода `obj` гарантированно `String`, поэтому `s` доступна и там.
- **Привязка к ветке:** в `||` или внутри `if`-блока с негативным условием переменная не видна — компилятор пускает её только туда, где соответствие шаблону доказано.

**Подводный камень:** binding variable — это обычная локальная переменная, её можно переприсвоить. Это считается дурным тоном: меняет смысл «переменная = распакованное значение шаблона». Лучше относиться к ней как к финальной.

## Q3. Что такое switch expressions и как они связаны с pattern matching?

**Switch expression** (Java 14, JEP 361) — это `switch`, который возвращает значение, использует стрелочный синтаксис `case L -> ...` без проваливания (fall-through) и не требует `break`. Это фундамент, на котором в Java 21 построили pattern matching для `switch`: тот же стрелочный синтаксис, но в `case` теперь можно писать не только константы, а шаблоны (`case Integer i ->`).

**Почему это связанные вещи.** Старый statement-`switch` сравнивал только с константами и проваливался между ветками без `break`. Чтобы добавить матчинг по типам, нужен был синтаксис без fall-through (иначе binding variable «протекала» бы в следующий case) и с явным возвратом значения — именно это и дал switch expression.

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

## Q4. Что такое Record Patterns (JEP 440) и зачем они нужны?

**Record pattern** (Java 21, JEP 440) — шаблон, который проверяет тип record и сразу деконструирует его на компоненты: `Point(int x, int y)` за один шаг проверяет, что значение — `Point`, и связывает `x` и `y` с его полями. Не нужно после `instanceof` вручную вызывать аксессоры `p.x()`, `p.y()`.

**Зачем нужны:** records описывают данные как набор именованных компонентов, а record pattern даёт обратную операцию — разобрать значение по этим же компонентам. Главная сила — **вложенность**: один шаблон рекурсивно разбирает дерево records на любую глубину (`Rectangle(Point(int x1, int y1), ...)`), вытаскивая нужные поля без промежуточных переменных.

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

## Q5. Что такое Sealed Classes и как они связаны с pattern matching?

**Sealed-классы и -интерфейсы** (Java 17, JEP 409) явно перечисляют через `permits` полный список наследников. Это превращает открытую иерархию в закрытый, известный компилятору набор вариантов — фактически алгебраический тип-сумму (ADT).

**Связь с pattern matching:** раз компилятор знает все возможные подтипы, он может проверить **exhaustiveness** — что `switch` покрывает их все. Если покрыты — `default` не нужен. Это и есть главная выгода связки: добавили новый подтип в `permits`, забыли обработать его в `switch` — получили ошибку компиляции, а не тихий баг в рантайме. Без `sealed` для такого `switch` компилятор всегда требовал бы `default`.

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

## Q6. Что такое Guarded Patterns (`when` clause) и почему порядок case критичен?

**Guarded pattern** — это шаблон плюс дополнительное булево условие после ключевого слова `when`: `case Integer i when i < 0`. Ветка срабатывает, только если совпал тип И выполнилось условие. Это позволяет различать значения одного типа по их содержимому, а не только по классу.

**Почему порядок критичен.** В switch с pattern matching ветки проверяются сверху вниз, и побеждает первая подходящая. Если поставить общий шаблон без guard (`case Integer i`) выше специфичных с guard, он перехватит все значения, и компилятор отметит нижние ветки как **недостижимые** (dominance) — это ошибка компиляции. Правило: располагай ветки от частного к общему, шаблон без guard ставь последним как fallback.

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

**Эмпирическое правило:** ветки с `when` для одного типа — сверху, безусловный шаблон того же типа — снизу.

```java
// Правильно: от специфичного к общему
case Integer i when i == 0 -> "zero";
case Integer i when i < 0 -> "negative";
case Integer i -> "positive";  // fallback

// НЕПРАВИЛЬНО: общий case "съест" специфичные
case Integer i -> "positive";   // всегда matches
case Integer i when i == 0 -> ...;  // недостижим!
```

## Q7. Как обрабатывать `null` в pattern matching switch (Java 21+)?

Исторически `switch` бросал `NullPointerException`, если значение оказывалось `null` — приходилось проверять на `null` отдельным `if` перед switch. В Java 21 появился **`case null`**: можно обработать `null` прямо как одну из веток switch.

Правила:

- **Без `case null`** поведение прежнее: `null` на входе → `NullPointerException`. Это сделано намеренно, для обратной совместимости.
- **`case null`** ловит `null` явной веткой.
- **`case null, default`** — объединяет: `null` и всё непокрытое идут в одну ветку. Удобно, когда `null` и «неизвестный тип» обрабатываются одинаково.

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

## Q8. Как pattern matching заменяет Visitor-паттерн?

Visitor-паттерн исторически решал задачу «добавить операцию над иерархией типов, не трогая сами типы»: иерархия объявляет метод `accept(visitor)`, а каждая новая операция — это новый класс-visitor с методом `visit` на каждый подтип. Цена — много бойлерплейта: интерфейс visitor, метод `accept` в каждом типе, double dispatch.

Pattern matching по sealed-иерархии решает ту же задачу проще: операция — это обычный метод со `switch`, который матчит подтипы напрямую. Никаких `accept`/`visit`, dispatch выражен декларативно.

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

**Плюсы pattern matching:** новую операцию добавляешь одним методом, не трогая иерархию и не плодя visitor-классы; код компактнее и читаемее.

**Компромисс с Visitor.** Когда иерархия `sealed`, выгоднее pattern matching: легко добавлять операции, exhaustiveness ловит забытые подтипы. Когда иерархия открыта для расширения сторонними реализациями (как в библиотечном API), Visitor по-прежнему уместен: он навязывает каждому новому подтипу реализацию операций через интерфейс.

## Q9. Что такое `yield` в switch expression и когда он нужен?

`yield` возвращает значение из **блочной** ветки `case { ... }` switch expression. Стрелка `case L -> выражение` сама подставляет результат выражения, но если в ветке нужно несколько операторов (логи, промежуточные вычисления), её оборачивают в `{ }` — и тогда значение из блока возвращается через `yield`.

**Зачем отдельное ключевое слово.** `return` внутри блока `case` вышел бы из всего метода, а не из switch. `yield` означает «это результат данной ветки switch expression» — switch вычисляется в значение, и выполнение продолжается дальше.

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

**Запомнить:** `yield` нужен только в блочных ветках `{ }`; для однострочных хватает `->`.

## Q10. Что такое Exhaustiveness Check и когда требуется `default`?

**Exhaustiveness** — проверка компилятором, что ветки switch покрывают все возможные значения входа. Если покрытие полное, switch гарантированно вернёт значение в любом случае, и `default` не требуется.

**Когда `default` не нужен** (покрытие доказуемо полное):

- **Sealed-тип:** компилятор знает весь список наследников из `permits` и проверяет, что каждый обработан.
- **Enum:** все константы перечислены явно.

**Когда `default` обязателен:** вход — открытый тип вроде `Object`, у которого бесконечно много подтипов; компилятор не может доказать полноту, поэтому требует `default`.

**Нюанс с enum:** даже у «полного» enum-switch компилятор может вставить скрытую защиту на случай, если в рантайме встретится константа, добавленная после компиляции, — switch не должен молча провалиться. Поэтому опираться на exhaustiveness как на железную гарантию стоит именно с sealed-типами.

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

## Q11. Как pattern matching работает с generic types и type erasure?

Из-за **type erasure** в рантайме у дженерика нет информации о параметре типа: `Box<String>` и `Box<Integer>` — это одинаковый `Box`. Поэтому в pattern matching работают только те проверки типа-параметра, которые компилятор может вывести статически; проверка вроде «это `Box` именно со `String`» в рантайме невозможна.

Как это проявляется:

- **`Box<?>(Object value)`** — wildcard всегда допустим: проверяется только сырой тип `Box`, компонент получаем как `Object`.
- **`Box<String>(String value)`** — допустим, когда компилятор может вывести параметр из контекста (например, переменная объявлена как `Box<String>`); тогда `value` сразу `String`.
- **Деконструкция выводит тип компонента,** а не проверяет его в рантайме: эрейзеру нечего проверять на уровне `<T>`.

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

## Q12. Какие практические применения pattern matching в production-коде?

Самый частый сценарий — **обработка алгебраических типов данных**: sealed-иерархия описывает закрытый набор вариантов, а switch с record patterns разбирает каждый. Два типичных примера:

- **Рекурсивный обход дерева** (рендер/обход AST, JSON): варианты значения — sealed-интерфейс, обход — switch, рекурсивно вызывающий себя на вложенных узлах. Exhaustiveness гарантирует, что ни один вид узла не забыт.
- **Диспетчеризация событий/команд:** доменные события — sealed-иерархия records, обработчик — switch по типу события с деконструкцией полей. Добавили новый тип события — компилятор заставит обработать его.

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

## Q13. В чём отличие Pattern Matching в Java от Scala/Kotlin?

Pattern matching пришёл в Java позже, чем в Scala и Kotlin, и пока уступает им по выразительности — но ключевые возможности (деконструкция, guards, exhaustiveness по sealed) уже есть. Главные смысловые различия: в Scala матчинг — центральная конструкция языка с богатым набором шаблонов (включая list patterns), в Kotlin `when` ближе к Java по уровню, а деконструкция data-классов идёт через destructuring, а не через сам матчинг.

| Критерий | Java 21+ | Scala | Kotlin |
|----------|----------|-------|--------|
| Базовое matching | `instanceof`, `switch` | `match` | `when` |
| Record deconstruction | ✓ (Java 21) | case classes | data class (не матчится напрямую) |
| Guards | `when` clause | `if` guards | `when (x) { is T -> if(...)` |
| Exhaustiveness (sealed) | ✓ | ✓ | `sealed` + exhaustive `when` |
| Type inference | Частично | Полно | Частично |
| List patterns | ✗ | `List(a, b, _*)` | Только через destructuring |
| Variable binding | `Point(int x, int y)` | `case Point(x, y)` | `is Point -> { val x = it.x }` |

**Итог:** Java закрыла основную потребность — деконструкцию records и exhaustive switch по sealed-типам, — но Scala остаётся богаче по набору шаблонов (например, list patterns с `_*`), а в Kotlin деконструкция структур идёт отдельным механизмом destructuring, а не внутри матчинга.

## Q14. Как pattern matching взаимодействует с unboxing и преобразованием типов?

Pattern matching умеет **авто-unboxing**: если тип шаблона — примитив, а значение хранится как обёртка, оно распаковывается. Но это работает только когда тип задан напрямую (как тип компонента record). Через границу дженерика (где из-за erasure стоит обёртка `T`) распаковки до примитива нет.

Ключевые случаи:

- **`instanceof Integer i`** → `i` имеет тип `Integer`; при использовании в арифметике срабатывает обычный auto-unboxing.
- **Деконструкция `Box(int v)`**, где компонент объявлен как `int`, — `v` сразу примитив `int`, без обёртки.
- **Через дженерик нельзя сразу в примитив:** `Wrapper<Integer>(int v)` не скомпилируется — параметр типа всегда ссылочный (`Integer`), к примитиву его не привести в шаблоне; пишут `Wrapper<Integer>(Integer v)`.

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

## Q15. Когда стоит / не стоит использовать pattern matching?

Базовый критерий выбора: **pattern matching — для данных, полиморфизм — для поведения**. Если объекты различаются структурой данных и над ними выполняются разные внешние операции (ADT-стиль), хорош pattern matching. Если объекты различаются поведением и хочется, чтобы каждый «знал, как себя вести» (OOP-стиль), уместнее виртуальные методы.

**Стоит использовать:**

- Работа с sealed-иерархией, где важна exhaustiveness (компилятор ловит забытые подтипы).
- Обработка иерархии с разной структурой данных (ADT).
- Замена длинных цепочек `if (x instanceof Y)` — короче и безопаснее.
- Интенсивная работа с records (деконструкция).
- Visitor-подобные задачи (см. Q8).

**Не стоит использовать:**

- **Тяжёлая бизнес-логика прямо в ветках** — если в `case` много кода, выноси в отдельные методы, в switch оставляй только dispatch.
- **OOP-иерархии с полиморфным поведением** — когда логику естественно держать в самих типах через виртуальные методы; switch по типам тогда нарушает OCP (новый подтип требует править все switch):

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

- **Много вложенных guards** — switch становится трудночитаемым; выноси условия в именованные методы-предикаты.

**Запомнить:** pattern matching — для структуры данных и диспетчеризации; для поведения — полиморфизм.

## See also

- [Java 17-21](java-17-21-interview.md) — все новшества Java 17-21
- [Java Records](java-records-interview.md) — records + pattern matching (record patterns)
- [Java OOP](java-oop-interview.md) — sealed classes, иерархии типов
- [Java Types](java-types-interview.md) — типы и приведение, instanceof
- [Java Conditional](java-conditional-statements-interview.md) — if/switch, evolution через pattern matching
- [Java Generics](java-generics-interview.md) — generic patterns
- [Java Exceptions](java-exceptions-interview.md) — sealed exceptions
- [Kotlin](../kotlin/kotlin-interview.md) — сравнение с Kotlin when expression и sealed classes
- [Scala](../scala/scala-interview.md) — оригинальный pattern matching, откуда позаимствовано
- [Java Core](java-core-interview.md) — базовые концепции
- [Java 22–25](java-22-25-interview.md) — что нового после LTS 21: FFM, gatherers, scoped values, generational ZGC, PQC.
