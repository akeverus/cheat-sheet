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
>
> **Вопрос:** Какие основные формы Pattern Matching уже введены в Java по состоянию на Java 21 (LTS)?
>
> ---
>
> #### A) Только `instanceof` patterns (Java 16); `switch` pattern matching и record patterns ещё в preview — ❌ Неверно
>
> **Что на самом деле:** В Java 21 (сентябрь 2023, LTS) и `switch` pattern matching (JEP 441), и record patterns (JEP 440) стали финальными — `preview` сняли. В preview они были в Java 19-20, но в 21 уже стандарт.
>
> **Откуда путаница:** Многие команды до сих пор сидят на Java 17 LTS, где `switch` patterns были в preview (JEP 406/420/427). Если ориентироваться на Java 17, ассоциация «preview» закрепляется.
>
> **Если бы это было правдой:** Production-команды боялись бы использовать record patterns без флага `--enable-preview` — на практике в Spring Boot 3.2+ они работают без флагов из коробки.
>
> ---
>
> #### B) `instanceof` patterns (JEP 394, Java 16), `switch` pattern matching (JEP 441, Java 21), record patterns (JEP 440, Java 21); все три — стандарт без preview-флага — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pattern Matching в Java эволюционировал поэтапно:
> 1. **JEP 394 (Java 16, март 2021)** — `instanceof` с binding variable: `if (o instanceof String s)` — auto-cast + scope.
> 2. **JEP 441 (Java 21, сентябрь 2023)** — `switch` expressions/statements с type patterns, guards (`when`), `case null`, exhaustiveness check.
> 3. **JEP 440 (Java 21)** — record patterns: деконструкция `case Point(int x, int y)`, вложенные patterns.
>
> Дополнительно в preview/incubator: JEP 456 (unnamed patterns `_`, Java 22 preview), JEP 488 (primitive type patterns, Java 23 preview).
>
> **Пример:**
> ```java
> sealed interface Shape permits Circle, Square {}
> record Circle(double r) implements Shape {}
> record Square(double s) implements Shape {}
>
> // Все три формы pattern matching в одном куске:
> static double area(Object o) {
>     if (o instanceof Shape sh) {                  // JEP 394: instanceof pattern
>         return switch (sh) {                       // JEP 441: switch pattern matching
>             case Circle(double r) -> Math.PI*r*r;  // JEP 440: record pattern
>             case Square(double s) -> s*s;
>         };
>     }
>     return 0;
> }
> ```
>
> **Когда применять:**
> - Миграция legacy `if-instanceof-cast` цепочек в Spring/Hibernate коде на Java 21.
> - ADT-стиль обработки событий (`DomainEvent`, `Result<T>`) в DDD-доменах Wolt, Booking.
> - Парсинг JSON/AST в библиотеках типа Jackson 2.16+ через sealed иерархии.
>
> **Подводные камни:**
> - На Java 17 LTS `switch` patterns доступны только с `--enable-preview` (JEP 420) — синтаксис стабильный, но требует флага компилятора.
> - IDE (IntelliJ 2023.2+, Eclipse 4.30+) поначалу подсвечивали record patterns как ошибку — обновите IDE прежде чем дебажить.
> - На Android (не Java) pattern matching недоступен — Kotlin `when` ≠ Java pattern matching.
>
> **Связанные вопросы:** [[Q2]] — детали `instanceof` patterns и flow scoping; [[Q3]] — switch expressions; [[Q4]] — record patterns.
>
> ---
>
> #### C) `instanceof` patterns, switch expressions (Java 14), record deconstruction в `for`-loop — ❌ Неверно
>
> **Что на самом деле:** Switch expressions (JEP 361, Java 14) — это `int x = switch(y) { case 1 -> 100; }` без pattern matching, просто стрелочный синтаксис + yield. Pattern matching в switch (JEP 441) — отдельная фича Java 21. Record deconstruction в `for` (JEP 432, `for (Point(int x, int y) : points)`) — пока в preview.
>
> **Откуда путаница:** Switch expressions из Java 14 и switch pattern matching из Java 21 — две разные JEP. Многие думают, что это одно и то же, потому что синтаксис похож (`->`).
>
> **Если бы это было правдой:** Можно было бы писать `for (Point(int x, int y) : list)` без флага preview в Java 21 — на самом деле компилятор выдаст `error: pattern matching in for is a preview feature`.
>
> ---
>
> #### D) Pattern Matching в Java реализуется только через библиотеку Vavr или Apache Commons — это не часть JDK — ❌ Неверно
>
> **Что на самом деле:** Pattern Matching — нативная фича JDK, реализованная на уровне `javac` и JVM-байткода. Vavr предлагает `Match.of(value).of(Case($(...), ...))` как DSL — это эмуляция через лямбды и generic API, не настоящий pattern matching, медленнее в 10-100x.
>
> **Откуда путаница:** До Java 16 единственным способом получить pattern matching в Java были библиотеки (Vavr, jOOL). Разработчики, начавшие до 2021, до сих пор тянут Vavr из привычки.
>
> **Если бы это было правдой:** Sealed types и exhaustiveness checking были бы невозможны — компилятор не валидирует Vavr `Match`. Любой `addCase` после `Match.of(...)` не вызвал бы ошибку на отсутствующий subtype.

## Q2. Что нового в `instanceof` pattern matching и что такое flow scoping?

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
>
> **Вопрос:** Где доступна binding variable `s` в `if (!(obj instanceof String s)) { throw ...; } use(s);` — и почему?
>
> ---
>
> #### A) `s` доступна только внутри блока `if`, после `throw` компилятор сбрасывает binding — ❌ Неверно
>
> **Что на самом деле:** При `negated instanceof` с unconditional exit (`throw`, `return`, `System.exit`), компилятор расширяет scope binding ПОСЛЕ блока — потому что если бы `obj` не был `String`, исполнение не дошло бы до `use(s)`. Это и есть flow scoping.
>
> **Откуда путаница:** В Java до 16 scope переменной всегда был ограничен enclosing block — `{ }`-границы определяли видимость. Pattern binding ломает эту интуицию: scope определяется анализом потока, а не фигурными скобками.
>
> **Если бы это было правдой:** Идиома «guard clause» (`if (!(o instanceof T t)) throw ...; useT(t);`) не работала бы — пришлось бы дублировать проверку или класть всю логику внутрь `if-else`, что увеличивает nesting.
>
> ---
>
> #### B) `s` доступна везде в методе после объявления — её scope равен scope обычной локальной переменной — ❌ Неверно
>
> **Что на самом деле:** Scope binding строго привязан к flow analysis. Если после `if (!(obj instanceof String s)) {...}` нет unconditional exit, `s` НЕ будет доступна снаружи. Компилятор должен доказать, что `s` определена на каждом пути выполнения.
>
> **Откуда путаница:** Локальные переменные в Java имеют block scope — но не «scope от объявления до конца метода». Java != JavaScript var hoisting.
>
> **Если бы это было правдой:** Можно было бы использовать `s` после `if`, который завершается normal flow → NPE/ClassCastException в runtime, когда `obj` оказался не `String`.
>
> ---
>
> #### C) `s` доступна после `if`, потому что compiler видит unconditional `throw` внутри блока — это и есть flow scoping; если убрать `throw`, scope `s` будет только внутри `if` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Flow scoping (JEP 394) — расширение области видимости binding variable на основе анализа control flow:
> 1. **Positive scope:** `if (o instanceof String s) { use(s); }` — `s` видна только если условие true, т.е. внутри `then`-блока.
> 2. **Negative scope с exit:** `if (!(o instanceof String s)) { return; } use(s);` — `s` видна ПОСЛЕ `if`, потому что компилятор знает: если бы паттерн не сматчился, мы бы вышли (return/throw/break).
> 3. **`&&` propagation:** `if (o instanceof String s && !s.isEmpty()) { ... }` — `s` доступна в правой части `&&`.
>
> **Пример:**
> ```java
> static int safeLength(Object obj) {
>     if (!(obj instanceof String s)) {
>         throw new IllegalArgumentException("not a String");
>     }
>     // ЗДЕСЬ s доступна — компилятор доказал:
>     // если бы не String, throw бы выполнился и сюда не дошли
>     return s.length();
> }
>
> // Без throw — НЕ компилируется:
> static int badLength(Object obj) {
>     if (!(obj instanceof String s)) {
>         System.out.println("not a string");  // не unconditional exit
>     }
>     return s.length();  // error: cannot find symbol s
> }
> ```
>
> **Когда применять:**
> - Guard clauses в начале методов — типичный паттерн в Spring controllers для валидации DTO.
> - Early-return стиль в сервисах вместо вложенных `if-else` (читается легче, плоский код).
> - В библиотеках типа Jackson 2.16+ для type dispatch без cast-broadcast.
>
> **Подводные камни:**
> - `if (!(o instanceof String s)) { /* без exit */ }` не расширит scope — IntelliJ это подсветит, но javac выдаст невнятный «cannot find symbol».
> - В `else` ветке pattern binding из условия с `!` недоступна: `if (!(o instanceof String s)) {} else { use(s); /* ERROR */ }`.
> - `||` НЕ propagate: `if (o instanceof String s || ...)` — `s` недоступна, потому что right side мог сработать без pattern.
>
> **Связанные вопросы:** [[Q1]] — обзор форм pattern matching; [[Q3]] — switch + pattern matching; [[Q7]] — null handling.
>
> ---
>
> #### D) `s` доступна, потому что Java 21 ввела «implicit casting»: компилятор автоматически приводит `obj` к `String` после `instanceof` независимо от ветки — ❌ Неверно
>
> **Что на самом деле:** Никакого implicit casting в Java нет. Pattern binding `s` — это новая переменная, создаваемая только если паттерн матчится. Cast по-прежнему явный — просто `instanceof` теперь делает binding + cast одной операцией.
>
> **Откуда путаница:** В Kotlin есть smart cast: `if (obj is String) { obj.length }` — компилятор «cast-ит» `obj` к `String`. Java не делает smart cast на ОРИГИНАЛЬНОЙ переменной `obj` — она остаётся `Object`. Pattern matching создаёт НОВУЮ binding variable.
>
> **Если бы это было правдой:** После `if (o instanceof String s)` можно было бы писать `o.length()` (а не `s.length()`) — но в Java это не компилируется, `o` остаётся `Object`.

## Q3. Что такое switch expressions и как они связаны с pattern matching?

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
>
> **Вопрос:** В чём концептуальное отличие switch **expression** (JEP 361, Java 14) от классического switch **statement** — и как это связано с pattern matching (JEP 441)?
>
> ---
>
> #### A) Switch expression — это просто сокращённый синтаксис с `->` вместо `:`; семантически он идентичен statement, отличие только в форме записи — ❌ Неверно
>
> **Что на самом деле:** Switch expression — это _выражение_, возвращающее значение и подлежащее **definite assignment analysis**. Компилятор требует exhaustiveness (покрытие всех веток), запрещает fallthrough, и значение можно присвоить переменной. Switch statement — _утверждение_, может пропускать ветки, fallthrough дефолтный.
>
> **Откуда путаница:** Синтаксис `case X -> result;` действительно появился вместе с expression-формой. Многие думают, что arrow-форма === expression, но arrow-syntax работает и в statement (`switch(x) { case 1 -> doSomething(); }`).
>
> **Если бы это было правдой:** Нельзя было бы написать `int x = switch(...) { ... };` — компилятор не отличал бы expression от statement, не было бы exhaustiveness check. Pattern matching (JEP 441) лежит поверх expression-семантики — без неё `case null` и exhaustiveness просто не имели бы смысла.
>
> ---
>
> #### B) Switch expression требует обязательного `default` — даже когда покрыты все варианты sealed type — ❌ Неверно
>
> **Что на самом деле:** Switch expression требует exhaustiveness, но если sealed type или enum покрыт _полностью_, `default` не нужен — компилятор сам докажет полноту. Более того: добавление `default` к exhaustive switch по sealed снимает compile-time проверку на новые subtypes — это considered harmful.
>
> **Откуда путаница:** До Java 14 в switch statement default добавляли везде «на всякий случай». Привычка тянется в expression — но это вредная привычка, теряется главный профит exhaustiveness check.
>
> **Если бы это было правдой:** Главное преимущество sealed + switch — обнаружение пропусков при добавлении нового подтипа — потерялось бы. Knight Capital-style инциденты: добавили новый `OrderType`, забыли обработчик, в проде silent fallback в `default`.
>
> ---
>
> #### C) В switch expression `case` всегда требует `yield`, иначе значение не возвращается — ❌ Неверно
>
> **Что на самом деле:** `yield` нужен только в блочной форме `case X -> { ... yield value; }`. Однострочная `case X -> value;` возвращает значение автоматически. `yield` ввели специально для multi-statement блоков, где нужен явный return.
>
> **Откуда путаница:** В Scala `match` всегда возвращает значение последнего выражения — без специального ключевого слова. В Java выбрали `yield`, чтобы отличить return из switch-блока от `return` метода. Многие думают, что `yield` всегда нужен.
>
> **Если бы это было правдой:** `int x = switch(y) { case 1 -> 100; case 2 -> 200; default -> 0; };` не компилировался бы — но он компилируется и работает с Java 14+.
>
> ---
>
> #### D) Switch expression возвращает значение и требует **exhaustiveness** (definite assignment); pattern matching (JEP 441) добавил type patterns/guards/case null поверх этой expression-семантики — без exhaustiveness pattern matching не имел бы смысла — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Эволюция switch в Java — три независимых, но взаимно усиливающих JEP:
> 1. **JEP 361 (Java 14, март 2020)** — switch expressions: `int x = switch(y) {...};`, exhaustiveness check, `yield`, отсутствие fallthrough в arrow-форме.
> 2. **JEP 409 (Java 17)** — sealed classes: ограничение иерархии, компилятор знает все subtypes.
> 3. **JEP 441 (Java 21)** — pattern matching for switch: type patterns, record patterns, `case null`, guards (`when`).
>
> Pattern matching ОПИРАЕТСЯ на expression-семантику. Без exhaustiveness check sealed-типы бессмысленны для switch. Без `case null` обработка `null` остаётся NPE-mine. Все три фичи сложились в Java 21 в одну mature конструкцию.
>
> **Пример:**
> ```java
> sealed interface PaymentStatus permits Pending, Approved, Declined {}
> record Pending(Instant since) implements PaymentStatus {}
> record Approved(String txId) implements PaymentStatus {}
> record Declined(String reason) implements PaymentStatus {}
>
> // Switch expression + pattern matching + exhaustiveness
> static String describe(PaymentStatus s) {
>     return switch (s) {
>         case Pending(var since)   -> "pending since " + since;
>         case Approved(var txId)   -> "approved: " + txId;
>         case Declined(var reason) -> "declined: " + reason;
>         // НЕТ default — компилятор доказал exhaustiveness
>         // Добавим new record FraudCheck — compile error пока не добавим case
>     };
> }
> ```
>
> **Когда применять:**
> - DTO/event mapping в Spring controllers — заменяет цепочки `if-instanceof`.
> - Result types в functional-стиле: `Result.Success` / `Result.Failure` (à la Rust).
> - Финансовые домены — Wolt/Booking используют sealed `OrderEvent` для строгой типизации pipeline.
>
> **Подводные камни:**
> - НЕ добавляйте `default` к sealed switch — теряется exhaustiveness check.
> - В switch statement (без присваивания) exhaustiveness НЕ требуется по умолчанию — будьте внимательны: `switch(s) { case Approved a -> ...; }` без присваивания скомпилируется и пропустит остальные ветки.
> - Pattern matching с `case String s when s.length() > 5` — guard НЕ участвует в exhaustiveness; нужен fallback `case String s -> ...` для остальных строк.
>
> **Связанные вопросы:** [[Q5]] — sealed classes и exhaustiveness; [[Q9]] — yield в блочной форме; [[Q10]] — детали exhaustiveness check.

## Q4. Что такое Record Patterns (JEP 440) и зачем они нужны?

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
>
> **Вопрос:** Что делает паттерн `case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) -> ...` и какие ограничения на типы и значения он накладывает?
>
> ---
>
> #### A) Деконструирует `Rectangle` на два `Point` и каждый `Point` на координаты; матчится только на `Rectangle` чьи оба поля **не null** (паттерн-имя ≠ null); вложенные record patterns поддерживаются произвольной глубины — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Record pattern (JEP 440) выполняет три шага атомарно:
> 1. **Type check** — проверяет, что объект — экземпляр record-типа (`Rectangle`).
> 2. **Null check** — для каждого вложенного record-pattern: соответствующий компонент не null (если поле имеет record-тип). Для примитивов и обычных типов null-check не делается, но компонент-record должен быть non-null чтобы вложенный pattern сматчился.
> 3. **Деконструкция** — вызывает аксессоры (`r.topLeft()`, `r.bottomRight()`, `p.x()`, `p.y()`) и привязывает результаты к binding variables.
>
> Вложенность — произвольная: `Tree(Tree(Tree(Leaf(int v), _), _), _)` — компилируется, но `_` (unnamed pattern, JEP 456) до Java 22 — preview, в Java 21 нужны имена.
>
> **Пример:**
> ```java
> record Point(int x, int y) {}
> record Rectangle(Point topLeft, Point bottomRight) {}
>
> static int area(Object o) {
>     return switch (o) {
>         case Rectangle(Point(int x1, int y1),
>                        Point(int x2, int y2))     -> Math.abs((x2-x1) * (y2-y1));
>         case Rectangle(Point tl, Point br)        -> tl.x() + br.x();   // менее глубокий pattern
>         case null                                  -> 0;
>         default                                    -> -1;
>     };
> }
>
> // ВАЖНО: если topLeft == null, БУДЕТ NullPointerException
> // на попытке деконструировать null. Это поведение Java 21.
> Rectangle r = new Rectangle(null, new Point(1,1));
> area(r);  // throws NPE на вложенном Point(x1, y1) pattern
> ```
>
> **Когда применять:**
> - Парсинг JSON/AST в библиотеках типа Jackson 2.16+ — sealed `JsonValue` с record subtypes деконструируется элегантно.
> - Event handlers в DDD: `case OrderShipped(var orderId, var address) -> ...` — нет ручного `event.orderId()`.
> - Functional pipelines в стиле Scala/Kotlin — record patterns закрывают гэп.
>
> **Подводные камни:**
> - **NPE на вложенных null-record-полях** — Java 21 НЕ матчит и НЕ skip, а кидает NPE. Безопаснее использовать `Optional` или гарантировать non-null в record-конструкторе.
> - **Var inference в record pattern:** `case Rectangle(var tl, var br)` — `tl` и `br` будут `Point`, не общий `Object`.
> - **Generic record patterns** требуют `<?>` или явный type: `case Box<?>(Object v)` — иначе unchecked warning.
>
> **Связанные вопросы:** [[Q1]] — обзор форм pattern matching; [[Q11]] — generic types в record patterns; [[Q14]] — взаимодействие с unboxing.
>
> ---
>
> #### B) Деконструирует `Rectangle` через рефлексию — компилятор генерирует вызовы `Field.get()` для каждого поля — ❌ Неверно
>
> **Что на самом деле:** Record patterns используют **аксессоры record-а** (`r.topLeft()`, `r.bottomRight()`), сгенерированные при компиляции record-а. Это обычные методы `invokevirtual` в байткоде — нет рефлексии, нет `setAccessible`. Производительность близка к ручному cast + getter.
>
> **Откуда путаница:** Многие думают, что «магия» pattern matching = рефлексия. В Java рефлексия медленная (~10x от прямого вызова), и pattern matching действительно был бы дорогим, если бы шёл через `Field.get()`.
>
> **Если бы это было правдой:** Pattern matching был бы в 10-100x медленнее ручной проверки — никто бы не использовал в hot path. На самом деле JIT inline-ит pattern matching так же, как `instanceof + cast + getter`.
>
> ---
>
> #### C) Деконструирует `Rectangle`, но компонент-`Point` должен иметь `equals(...)` определённый по содержимому — для проверки равенства точек — ❌ Неверно
>
> **Что на самом деле:** Record pattern проверяет тип и привязывает компоненты к переменным, но НЕ выполняет equality check. `equals()` вообще не вызывается. Pattern matching — это тестирование структуры, не значений.
>
> **Откуда путаница:** В Scala `case Point(0, 0) -> "origin"` — литералы в pattern проверяют равенство. Java НЕ поддерживает literal patterns (пока) — только type patterns и binding variables.
>
> **Если бы это было правдой:** `case Rectangle(Point(0, 0), Point(10, 10))` компилировался бы — но он НЕ компилируется в Java 21. Нужно `case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) when x1==0 && y1==0 && x2==10 && y2==10`.
>
> ---
>
> #### D) Деконструирует `Rectangle`, но требует, чтобы все компоненты были **записями примитивных типов** (`int`, `long`) — деконструкция объектов недоступна — ❌ Неверно
>
> **Что на самом деле:** Деконструкция работает для record с любыми типами компонентов — объекты, дженерики, коллекции, другие record. Ограничения: тип pattern-у должен быть совместим с типом компонента (`int x` для `int`-компонента; `String s` для `String`-компонента; `var x` для inference).
>
> **Откуда путаница:** Во вложенных patterns примитивы выглядят чаще (`int x`, `double d`) — это синтаксически проще. Но `case Order(Customer c, List<Item> items) -> ...` — валидный record pattern.
>
> **Если бы это было правдой:** `record Order(Customer c, List<Item> items)` нельзя было бы матчить — половина реальных доменов выпала бы.

## Q5. Что такое Sealed Classes и как они связаны с pattern matching?

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
>
> **Вопрос:** В чём ключевое требование sealed-иерархии для exhaustiveness check switch — и что произойдёт, если добавить `record Oval(double w, double h) implements Shape {}` к существующему `sealed interface Shape permits Circle, Square, Triangle`?
>
> ---
>
> #### A) `Oval` будет автоматически принят как новый subtype — `permits` не обязателен, sealed работает через рефлексию пакета — ❌ Неверно
>
> **Что на самом деле:** `permits` — обязательная часть sealed-иерархии: подкласс должен быть **явно перечислен**. `Oval`, не указанный в `permits`, не компилируется как `implements Shape`. Sealed работает на уровне `javac` через ClassFile attribute `PermittedSubclasses`, не через рефлексию.
>
> **Откуда путаница:** В Scala `sealed trait` ищет подклассы в том же файле — без явного permit-списка. Java выбрала explicit permits как более safe-by-default подход.
>
> **Если бы это было правдой:** Любой добавленный класс в проект ломал бы exhaustiveness существующих switch — невозможно было бы делать stable API. Sealed классы из библиотек (Spring, Java SDK) разрушались бы при любом использовании.
>
> ---
>
> #### B) `Oval` не скомпилируется без добавления в `permits Shape` (нужно изменить sealed-интерфейс); даже после исправления — все existing `switch (Shape)` без default ломаются на compile-time с ошибкой «not exhaustive»; это и есть главный профит sealed + pattern matching — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Sealed types + pattern matching образуют **safe ADT (Algebraic Data Type)** в Java:
> 1. **Compile-time guarantee:** компилятор знает все подтипы и доказывает покрытие. Нет runtime-сюрпризов.
> 2. **Refactoring safety:** добавление нового подтипа → ошибка компиляции во всех switch-ах, обрабатывающих эту иерархию. Это **feature**, не bug — IDE покажет все места, требующие обновления.
> 3. **No `default` needed:** для sealed-иерархии `default` — антипаттерн, ломает refactoring safety.
>
> Sealed может быть:
> - `sealed class` + `non-sealed`/`final` subclasses,
> - `sealed interface` (как тут),
> - `record` финален неявно (можно `permits`-ить record).
>
> **Пример:**
> ```java
> // 1. Текущая sealed-иерархия
> public sealed interface Shape permits Circle, Square, Triangle {}
> public record Circle(double r) implements Shape {}
> public record Square(double s) implements Shape {}
> public record Triangle(double b, double h) implements Shape {}
>
> public static double area(Shape s) {
>     return switch (s) {                          // exhaustive — нет default
>         case Circle(double r)    -> Math.PI*r*r;
>         case Square(double a)    -> a*a;
>         case Triangle(double b, double h) -> 0.5*b*h;
>     };
> }
>
> // 2. Хотим добавить Oval:
> public record Oval(double w, double h) implements Shape {}
> // ERROR: Oval cannot be a subtype of Shape (not in permits)
>
> // 3. Расширяем sealed-интерфейс:
> public sealed interface Shape permits Circle, Square, Triangle, Oval {}
> // Теперь Oval компилируется, НО:
> // ERROR in area(): switch is not exhaustive,
> //                  missing case Oval
> ```
>
> **Когда применять:**
> - Domain events в DDD-сервисах Wolt/Booking: `sealed interface OrderEvent` — добавление нового события ломает обработчики, никаких silent miss.
> - Result types в functional code: `sealed interface Result<T> permits Success, Failure` — стиль Rust/Haskell в Java.
> - AST/IR в компиляторах и парсерах — Spring Expression Language, Jackson 2.16+.
>
> **Подводные камни:**
> - `non-sealed` подкласс «открывает» иерархию снова — exhaustiveness check для switch перестаёт работать на этой ветке.
> - Sealed классы должны быть в том же модуле (или unnamed module). В мульти-модульном приложении permits-классы не могут быть в другом jpms-модуле.
> - Если `default` всё-таки добавлен «на всякий случай» — refactoring-safety теряется молча, compile-time check пропадает.
>
> **Связанные вопросы:** [[Q3]] — switch expressions и exhaustiveness; [[Q4]] — record patterns с sealed; [[Q10]] — детали exhaustiveness check.
>
> ---
>
> #### C) `Oval` скомпилируется, но `switch` тихо начнёт возвращать `null` для `Oval` — runtime fallback без default — ❌ Неверно
>
> **Что на самом деле:** Switch expression по sealed без exhaustiveness не компилируется. Если pattern matching switch встречает значение, не покрытое ни одним case, бросается `MatchException` (не `null`). Это runtime safety, дополняющая compile-time check.
>
> **Откуда путаница:** В Kotlin `when` без exhaustiveness возвращает `Unit` (для statement) или ошибка компиляции (для expression). В JavaScript switch без default возвращает `undefined`. Эта семантика «тихого null» путается с Java pattern matching.
>
> **Если бы это было правдой:** Все sealed switch-и стали бы потенциальными источниками NPE — обратная ситуация цели sealed-типов.
>
> ---
>
> #### D) `Oval` скомпилируется и сам по себе, и старые switch-и продолжат работать — просто `Oval` попадёт в `default`-ветку, которая обязательна — ❌ Неверно
>
> **Что на самом деле:** `default` НЕ обязателен для sealed switch — это и есть профит. В оригинальном switch из вопроса default отсутствует — компилятор доказал exhaustiveness, добавление Oval сломает compile-time.
>
> **Откуда путаница:** Привычка из switch statement (до Java 14): «всегда добавляй default на всякий». В sealed switch это вредная привычка — теряется главное преимущество.
>
> **Если бы это было правдой:** Точно так же как Knight Capital потерял $440M на «забытом» обработчике — silent fallback в default скрыл бы новый Oval, площадь возвращалась бы дефолтная, расчёт счетов был бы неправильный.

## Q6. Что такое Guarded Patterns (`when` clause) и почему порядок case критичен?

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
>
> **Вопрос:** Если в switch указать `case Integer i -> "positive";` ПЕРЕД `case Integer i when i == 0 -> "zero";` — что произойдёт и почему?
>
> ---
>
> #### A) Компилятор автоматически переупорядочит case — сначала более специфичные (с guard), потом общие — ❌ Неверно
>
> **Что на самом деле:** Компилятор НЕ переупорядочивает case. Pattern matching работает по принципу «first match wins», как обычный if-else-if. Порядок написания определяет приоритет.
>
> **Откуда путаница:** В Haskell/Scala pattern matching действительно проверяет более специфичные patterns первыми (через type hierarchy), но Java следует синтаксическому порядку — это явная design choice для предсказуемости.
>
> **Если бы это было правдой:** Программисту не нужно было бы думать о порядке — но runtime поведение зависело бы от компилятора, что трудно дебажить. Каждая версия javac могла бы по-разному «оптимизировать».
>
> ---
>
> #### B) Runtime будет вызывать `case Integer i -> "positive"` для всех `Integer`, включая 0 — `0 == 0` пройдёт только если правильно использовать `case Integer i if (i == 0)` — ❌ Неверно
>
> **Что на самом деле:** Синтаксис — `case Integer i when i == 0`, не `if`. И да, первый case всегда выигрывает, потому что type pattern `Integer i` без guard покрывает всех `Integer`. Но проблема не в синтаксисе guard, а в порядке.
>
> **Откуда путаница:** В Kotlin guard в `when` пишется через `if` (`is Integer -> if (it == 0) "zero" else "other"`). В Java выбрали `when`, чтобы не пересекаться с условным выражением.
>
> **Если бы это было правдой:** Существовала бы два разных синтаксиса для одного и того же — Java выбрала ровно один: `when`.
>
> ---
>
> #### C) **Compile-time error** — компилятор обнаруживает, что `case Integer i when i == 0` **dominated** (затенён) предыдущим `case Integer i` и сообщает «label is dominated by a preceding case label» — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pattern dominance — это compile-time check, который ловит **недостижимые** case label. Правила:
> 1. **Unconditional pattern** (`case String s ->`) **доминирует** все последующие patterns того же или более узкого типа — включая guarded patterns.
> 2. **Guarded pattern** (`case String s when ...`) НЕ доминирует другой guarded pattern автоматически — компилятор не доказывает логику guard.
> 3. Порядок: специфичные guards СНАЧАЛА, общие unconditional fallback — В КОНЦЕ.
>
> Это безопаснее, чем silent dead code: в Java старый switch statement позволял писать недостижимые case, ошибка обнаруживалась только code review-ом.
>
> **Пример:**
> ```java
> // ✗ НЕ компилируется:
> String classify(Object o) {
>     return switch (o) {
>         case Integer i -> "any int";                          // доминирует
>         case Integer i when i == 0 -> "zero";                 // ERROR: dominated
>         default -> "other";
>     };
> }
>
> // ✓ Правильно: специфичные сначала
> String classifyOk(Object o) {
>     return switch (o) {
>         case Integer i when i == 0 -> "zero";
>         case Integer i when i < 0  -> "negative";
>         case Integer i when i > 100 -> "large";
>         case Integer i              -> "small positive";       // unconditional fallback
>         default                     -> "other";
>     };
> }
> ```
>
> **Когда применять:**
> - Разделение бизнес-логики на «edge cases» (null, empty, boundary) и «happy path» — типично в валидации DTO в Spring controllers.
> - State machines: `case State s when s.isFinal() -> archive(s);` — после специфичных терминальных state-ов.
> - Pricing rules в e-commerce (Wolt, Booking): `case Order o when o.total() > 1000 -> applyVipDiscount(o);`.
>
> **Подводные камни:**
> - Между двумя guarded patterns одного типа компилятор НЕ обнаружит логическое затенение: `case Integer i when i > 0` перед `case Integer i when i > 100` — оба компилируются, но второй недостижим (любой `>100` уже `>0`). Это runtime баг.
> - `default` всегда доминирует все последующие case — компилятор предупредит.
> - В мульти-pattern (`case A, B when ...`) guard применяется ко всему набору.
>
> **Связанные вопросы:** [[Q3]] — switch expressions; [[Q10]] — exhaustiveness check; [[Q15]] — когда стоит/не стоит использовать pattern matching.
>
> ---
>
> #### D) Runtime будет вызывать оба case по очереди — fallthrough из старого switch — ❌ Неверно
>
> **Что на самом деле:** В arrow-style switch (`case X -> result;`) fallthrough НЕТ. Каждый case строго изолирован. Это design choice JEP 361 — устранить классическую ошибку забытого `break`.
>
> **Откуда путаница:** Олд-скул C-style switch (Java до 14) имел fallthrough по умолчанию: `case 1: case 2: doSomething(); break;`. Многие новички перетягивают эту модель на arrow-style.
>
> **Если бы это было правдой:** Switch expression вернул бы два значения подряд — что невозможно для expression-семантики. Это бы ломало основу switch expressions.

## Q7. Как обрабатывать `null` в pattern matching switch (Java 21+)?

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
>
> **Вопрос:** Как ведёт себя `switch(obj) { case String s -> ...; default -> ...; }` если `obj == null` — в Java 21 без `case null` и почему это design choice?
>
> ---
>
> #### A) Возвращает значение из `default` ветки — null покрывается как «всё прочее» — ❌ Неверно
>
> **Что на самом деле:** Без `case null` switch бросает `NullPointerException` на этапе входа в switch. `default` НЕ покрывает null автоматически — это design choice ради backward compatibility со старым switch (до Java 21).
>
> **Откуда путаница:** В JavaScript `switch(null) { default: ... }` зайдёт в default. В Java исторически switch падал на null — pattern matching сохраняет это поведение, чтобы не сломать legacy switch код.
>
> **Если бы это было правдой:** Обновление JDK с 17 → 21 ломало бы тысячи sealed switch-ов: legacy ожидает NPE, а новый код возвращал бы default-значение. Это catastrophic для миграции.
>
> ---
>
> #### B) Возвращает `null` — компилятор автоматически добавляет `case null -> null` к каждому pattern matching switch — ❌ Неверно
>
> **Что на самом деле:** Никакой автоматической вставки `case null` нет. Без явного `case null` switch бросает NPE. Java 21 НЕ добавила «implicit null handling» — это сознательное решение JEP 441 в пользу explicit-over-implicit.
>
> **Откуда путаница:** Optional chaining в Kotlin (`?.`) и null-safety в Swift приучают к «invisible null handling». В Java остаётся обязательным явный `case null`.
>
> **Если бы это было правдой:** Любая ошибка в коде (передача null где не ожидалось) маскировалась бы — баг бы откладывался до момента, когда null доходил до места, где его не ожидают. Хуже всего — для финансовых расчётов.
>
> ---
>
> #### C) Возвращает значение из `case String s` — null trivially является String — ❌ Неверно
>
> **Что на самом деле:** В Java `null` не имеет типа — он не является экземпляром ни одного типа. `null instanceof String` всегда возвращает `false`. Type pattern `String s` НЕ матчится на null.
>
> **Откуда путаница:** В обычном if-instanceof: `if (x instanceof String s)` — false для null, проблем нет. Многие думают, что в switch — то же самое (тихий skip case). Но switch БРОСАЕТ исключение, не идёт дальше.
>
> **Если бы это было правдой:** `String s = null;` в then-ветке привело бы к NPE на любом методе `s` — это эквивалент маскированного баг.
>
> ---
>
> #### D) Бросает `NullPointerException` на входе в switch — backward compatibility со старым switch; явное `case null` нужно для обработки null без exception — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Null handling в pattern matching switch (JEP 441):
> 1. **Без `case null`:** switch с pattern matching бросает NPE на null — то же поведение, что и `switch (enumValue)` в Java 1-20.
> 2. **`case null -> ...`:** явная обработка, идиоматичный способ.
> 3. **`case null, default -> ...`:** комбинированный case — null обрабатывается как default.
> 4. **`case null, String s -> ...`:** ОШИБКА — нельзя комбинировать `case null` с type pattern (binding variable не определён для null).
> 5. **Type pattern (`case String s`)** НЕ матчит null — `null instanceof String == false`.
>
> **Пример:**
> ```java
> // ✗ NPE при obj == null
> static String unsafe(Object obj) {
>     return switch (obj) {
>         case String s   -> "str: " + s;
>         case Integer i  -> "int: " + i;
>         default         -> "other";
>     };
> }
>
> // ✓ Явная обработка null
> static String safe(Object obj) {
>     return switch (obj) {
>         case null       -> "got null";
>         case String s   -> "str: " + s;
>         case Integer i  -> "int: " + i;
>         default         -> "other";
>     };
> }
>
> // ✓ null объединён с default
> static String compact(Object obj) {
>     return switch (obj) {
>         case String s        -> "str: " + s;
>         case null, default   -> "null or unknown";
>     };
> }
> ```
>
> **Когда применять:**
> - DTO от внешних API (Jackson может десериализовать null поля) — всегда `case null` для defensive coding.
> - Sealed event hierarchies, где event может прилететь null из Kafka/RabbitMQ — Wolt order pipeline.
> - Migration legacy кода: `case null, default` помогает безопасно мигрировать с `if-else-instanceof`.
>
> **Подводные камни:**
> - `case null` для sealed types НЕ покрывает exhaustiveness — нужен ИЛИ default, ИЛИ `case null` + все subtypes.
> - Внутри record pattern null-компонента приводит к NPE: `case Box(String s) -> ...` бросит NPE если `box.value()` вернул null.
> - В switch statement (без присваивания) без default и без `case null` компилируется и бросает NPE — никакой compile-time check.
>
> **Связанные вопросы:** [[Q4]] — record patterns и NPE на null components; [[Q6]] — guarded patterns; [[Q10]] — exhaustiveness и null.

## Q8. Как pattern matching заменяет Visitor-паттерн?

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
>
> **Вопрос:** Pattern matching заменяет Visitor — но какой важный архитектурный trade-off вы делаете, выбирая pattern matching вместо `accept(Visitor v)`?
>
> ---
>
> #### A) Pattern matching выносит логику ИЗ иерархии классов в место использования — это **expression problem** trade-off: легко добавить новую операцию (новый switch), сложно добавить новый тип (нужно расширить permits + обновить все switch); Visitor — наоборот: легко добавить тип (новый класс + visit-метод), сложно добавить операцию (новый интерфейс Visitor) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> **Expression problem** (Philip Wadler, 1998) — фундаментальный trade-off в дизайне ADT:
> - **OOP / Visitor:** иерархия типов фиксирована, операции легко добавлять (новый интерфейс Visitor). Но добавление НОВОГО типа требует обновить ВСЕ Visitor-интерфейсы.
> - **FP / Pattern matching:** операции легко добавлять (новый switch). Но добавление НОВОГО типа требует обновить ВСЕ switch-блоки.
>
> Java с pattern matching + sealed выбрала FP-подход, но добавила compile-time safety: при добавлении нового sealed-подкласса все switch с exhaustiveness check ломаются на компиляции — IDE покажет места.
>
> **Пример:**
> ```java
> // Visitor — типы фиксированы, операции легко добавлять
> public interface Shape {
>     <R> R accept(ShapeVisitor<R> v);
> }
> public interface ShapeVisitor<R> {
>     R visit(Circle c);
>     R visit(Square s);
> }
> // Добавить Triangle → нужно расширить ShapeVisitor (новый метод visit(Triangle))
> // → ломаем ВСЕ реализации ShapeVisitor (в т.ч. внешние клиенты)
>
> // Pattern matching — операции легко добавлять, типы менее гибкие
> sealed interface Shape permits Circle, Square {}
> record Circle(double r) implements Shape {}
> record Square(double s) implements Shape {}
>
> static double area(Shape sh) {                 // operation #1
>     return switch (sh) {
>         case Circle(double r) -> Math.PI*r*r;
>         case Square(double a) -> a*a;
>     };
> }
> static double perimeter(Shape sh) {            // operation #2 — добавили БЕЗ изменения Shape
>     return switch (sh) {
>         case Circle(double r) -> 2*Math.PI*r;
>         case Square(double a) -> 4*a;
>     };
> }
> // Добавить Triangle → нужно обновить area() и perimeter()
> // Compile-time гарантия: оба switch не компилируются пока не добавим case Triangle
> ```
>
> **Когда применять:**
> - Закрытые ADT с редко меняющимися типами и часто меняющимися операциями: JSON node types, AST, expression trees, парсеры.
> - Domain events в стабильной доменной модели — операции (handlers) часто меняются (новые подписчики), типы (события) — редко.
> - Result/Either типы — два «типа» (Success/Failure), много операций над ними.
>
> **Подводные камни:**
> - Если домен растёт «новыми типами» — Pattern matching становится антипаттерном. Полиморфизм OOP лучше.
> - Pattern matching на mutable иерархии без sealed — теряется exhaustiveness, та же ловушка что и default.
> - Бизнес-логика внутри case → дублирование между функциями — выносите в отдельные методы.
>
> **Связанные вопросы:** [[Q5]] — sealed classes для exhaustiveness; [[Q12]] — практические применения; [[Q15]] — когда стоит/не стоит использовать pattern matching.
>
> ---
>
> #### B) Pattern matching работает быстрее Visitor — нет виртуального вызова, JIT inlines всё — ❌ Неверно
>
> **Что на самом деле:** Pattern matching на switch с type patterns компилируется в bytecode-инструкции `typeSwitch` (intrinsic в JDK 21+), которая использует таблицу переходов или `instanceof + if-else-if`. Производительность СОПОСТАВИМА с Visitor (virtual dispatch — тоже table lookup). Различие на уровне shum, не порядков.
>
> **Откуда путаница:** Микро-оптимизаторы любят искать «быстрые» альтернативы. На самом деле для большинства switch с 3-10 case разницы нет — JIT справляется с обоими.
>
> **Если бы это было правдой:** Все production-системы переписывались бы на pattern matching — но реальные бенчмарки (JMH) показывают разницу < 5%, и она НЕ оправдывает архитектурные изменения.
>
> ---
>
> #### C) Pattern matching и Visitor взаимозаменяемы — никакого trade-off, выбирайте по вкусу — ❌ Неверно
>
> **Что на самом деле:** Trade-off реален и важен: Pattern matching = «открыт для операций, закрыт для типов»; Visitor = «открыт для типов, закрыт для операций». Это разные axes расширяемости.
>
> **Откуда путаница:** В простых примерах с 3 типами оба паттерна выглядят одинаково. Разница проявляется в эволюции системы за 6-12 месяцев.
>
> **Если бы это было правдой:** Не было бы 25-летней дискуссии в академии (Expression Problem, Wadler 1998) — но trade-off фундаментален и обсуждался в Haskell, Scala, OCaml, F#, Kotlin до Java.
>
> ---
>
> #### D) Pattern matching отменяет Visitor для всех случаев — Visitor устарел с появлением Java 21 — ❌ Неверно
>
> **Что на самом деле:** Visitor остаётся уместен когда: (a) типов больше чем операций; (b) типы добавляются часто (например, AST в внешнем API); (c) нужна возможность реализовать visitor внешним клиентом без модификации иерархии. Pattern matching не покрывает эти случаи.
>
> **Откуда путаница:** Hype-driven development: «новое = лучше». В реальности Visitor использовался Spring (org.springframework.beans.factory.config.BeanPostProcessor) и JDK (Tree API в `com.sun.source.util.SimpleTreeVisitor`).
>
> **Если бы это было правдой:** Все existing visitor-based APIs (Spring, AST, Hibernate Criteria) пришлось бы переписывать — но они работают и в Java 21 без изменений.

## Q9. Что такое `yield` в switch expression и когда он нужен?

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
>
> **Вопрос:** Что вернёт `switch (1) { case 1 -> { int r = 5; r + 10; } default -> 0; }` — и почему?
>
> ---
>
> #### A) Вернёт `15` — последнее выражение в блоке автоматически становится результатом (как в Scala/Kotlin) — ❌ Неверно
>
> **Что на самом деле:** Switch expression в Java НЕ выводит значение из последнего выражения автоматически. В блочной форме (`{ ... }`) обязателен явный `yield`. Без него — compile error: «case label cannot complete normally».
>
> **Откуда путаница:** В Scala `match { case 1 => { val r = 5; r + 10 } }` — последнее выражение возвращается. В Kotlin `when` — то же самое. Java выбрала explicit `yield` для отличия от `return` метода.
>
> **Если бы это было правдой:** Не было бы compile-time проверки, что блок case действительно возвращает значение. Случайно забытый `return` в lambda — типовой баг — стал бы тихим: блок «возвращал бы» побочный эффект.
>
> ---
>
> #### B) **Compile error** — блочный case с `{ }` обязан использовать `yield`, иначе компилятор говорит «case label cannot complete normally»; правильно: `case 1 -> { int r = 5; yield r + 10; }` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `yield` — оператор для возврата значения из блочной формы case в switch expression:
> 1. **Однострочная `case X -> expr;`** — `expr` автоматически становится результатом case. `yield` НЕ нужен.
> 2. **Блочная `case X -> { ... }`** — нужно `yield value;` явно, иначе блок «не завершается нормально».
> 3. `yield` отличается от `return` тем, что возвращает значение из ОДНОГО case, а не из метода. Можно совмещать: `case X -> { if (cond) yield A; else yield B; }`.
> 4. В switch _statement_ (без присваивания) `yield` не имеет смысла — нет «возвращаемого значения».
>
> **Пример:**
> ```java
> // ✓ Правильно — yield в блочной форме
> static int computeBonus(Tier t) {
>     return switch (t) {
>         case GOLD -> 1000;                              // однострочный — без yield
>         case SILVER -> {                                // блочный — обязан yield
>             int base = 500;
>             int bonus = ThreadLocalRandom.current().nextInt(100);
>             yield base + bonus;
>         }
>         case BRONZE -> {
>             logger.info("bronze tier");
>             yield 100;                                  // yield ВСЕГДА если { }
>         }
>     };
> }
>
> // ✗ Compile error — блок без yield
> // case SILVER -> { int r = 5; r + 10; }
> // error: case label cannot complete normally
> ```
>
> **Когда применять:**
> - Сложная логика внутри case: вычисление, логирование, conditional yield.
> - Несколько ветвей внутри одного case: `if-else` с разными `yield`.
> - Try-catch внутри case с обработкой ошибки и default-значением.
>
> **Подводные камни:**
> - `yield` нельзя использовать внутри обычного switch _statement_ — только в expression.
> - Несколько `yield` в одном case (через `if-else`) — ок, но все должны возвращать совместимый тип.
> - Из вложенного `for`/`while` в case `yield` не «выпрыгивает» наружу — это `break` с labels или `yield` после loop.
> - `throw` валиден вместо `yield` для exceptional ветвей — не «завершает нормально», поэтому compile проходит.
>
> **Связанные вопросы:** [[Q3]] — switch expressions overview; [[Q6]] — guarded patterns с блочной формой; [[Q12]] — практические применения с complex logic.
>
> ---
>
> #### C) Вернёт `0` — блок без явного return уходит в default — ❌ Неверно
>
> **Что на самом деле:** Switch expression НЕ переходит в default при отсутствии yield — это не fallthrough. Каждый case независим. Без yield в блочной форме — compile error, не runtime fallback.
>
> **Откуда путаница:** Аналогия с try-catch: «если в try не дошли до return, переходим к finally». Switch-case так не работает.
>
> **Если бы это было правдой:** Любой блочный case без yield тихо уходил бы в default — катастрофа для бизнес-логики типа расчёта бонусов или платежей.
>
> ---
>
> #### D) Вернёт `5` — `yield` подразумевается на первой переменной в блоке (`r`), последующие выражения игнорируются — ❌ Неверно
>
> **Что на самом деле:** Никакого implicit yield нет. Переменная `r = 5` — это declaration statement, не expression statement и не yield. Без явного `yield` — compile error.
>
> **Откуда путаница:** Some languages (Lisp-family) возвращают значение последней expression в block. Java чётко разделяет statements и expressions — yield нужен явный.
>
> **Если бы это было правдой:** Невозможно было бы вычислить промежуточные значения в case — каждая variable declaration «возвращала бы» себя. Это сделало бы switch expressions бесполезными для нетривиальной логики.

## Q10. Что такое Exhaustiveness Check и когда требуется `default`?

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
>
> **Вопрос:** Что произойдёт в runtime, если sealed switch без default обработает значение, для которого compile-time exhaustiveness был доказан, но во время загрузки модуль `Square.class` исчез или был заменён на обновлённую версию sealed-иерархии?
>
> ---
>
> #### A) Загрузка класса не пройдёт — JVM проверяет sealed-permits на этапе ClassLoader — ❌ Неверно
>
> **Что на самом деле:** JVM проверяет `PermittedSubclasses` attribute при загрузке sealed-класса, но не проверяет «целостность» switch-блоков в зависимых классах. Если `Shape` загружен с `{Circle, Square, Triangle}`, а в новом jar только `{Circle, Triangle}` — switch продолжит работать, пока не встретит missing case.
>
> **Откуда путаница:** Compile-time exhaustiveness даёт ощущение «всё проверено». Но runtime separation classes (separate compilation) может ломать assumptions.
>
> **Если бы это было правдой:** Невозможно было бы делать hot-reload или class file swapping — стандартные техники Spring DevTools и JRebel сломались бы для любого sealed-кода.
>
> ---
>
> #### B) Switch вернёт null или дефолтное значение типа — silent fallback — ❌ Неверно
>
> **Что на самом деле:** Switch БРОСАЕТ `MatchException` (новое исключение в Java 21, JEP 441). Это runtime safety net: если compile-time гарантия нарушена (separate compilation drift), не silent corruption, а явный сбой.
>
> **Откуда путаница:** В legacy switch без default тихое поведение «ничего не выбрано» приводило к skip. Pattern matching switch специально это исключает через MatchException.
>
> **Если бы это было правдой:** Это и есть Knight Capital scenario — silent fallback на missing case привёл бы к bidding на $440M.
>
> ---
>
> #### C) Бросается `MatchException` в runtime — это safety net для случая, когда compile-time exhaustiveness нарушен из-за **separate compilation** (binary incompatibility); это специальный exception, введённый в Java 21 — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Exhaustiveness check имеет два слоя защиты:
>
> 1. **Compile-time check** (основной):
>    - Sealed types: компилятор перечисляет permits, требует все subtypes покрыты.
>    - Enum: все constants должны быть в case.
>    - Object / open type: нужен `default`.
>    - Reward — раннее обнаружение пропусков.
>
> 2. **Runtime safety net** (`MatchException`):
>    - Кидается, если ни один case не сматчился (включая `case null`).
>    - Защита от separate compilation drift: компилировали с `permits {A, B}`, deploy с `permits {A, B, C}` без рекомпиляции.
>    - Защита от reflective hacks.
>
> Compile-time проверки делаются:
> - Для switch _expression_ (`int x = switch(...) {...}`) — ВСЕГДА required.
> - Для switch _statement_ с type patterns — required в Java 21.
> - Для switch statement с legacy const-patterns (`case 1:`) — НЕ required (backward compat).
>
> **Пример:**
> ```java
> sealed interface Shape permits Circle, Square {}
> record Circle(double r) implements Shape {}
> record Square(double s) implements Shape {}
>
> static double area(Shape sh) {
>     return switch (sh) {
>         case Circle(double r) -> Math.PI*r*r;
>         case Square(double a) -> a*a;
>         // exhaustive — no default
>     };
> }
>
> // Сценарий drift:
> // 1. Compile area() с permits {Circle, Square}
> // 2. Replace Shape.class с permits {Circle, Square, Triangle}
> // 3. Pass new Triangle() to area():
> //    → java.lang.MatchException: Triangle@..
> ```
>
> **Когда применять:**
> - Финансовые расчёты в Wolt/Booking: лучше fail-fast (MatchException) чем silent wrong calculation.
> - Event handlers в распределённых системах: при отсутствии handler-а MatchException попадёт в DLQ.
> - State machines: missing transition случай.
>
> **Подводные камни:**
> - `MatchException` — `RuntimeException`, не checked. Можно «потерять» в общем catch.
> - В Spring controller MatchException по умолчанию → HTTP 500. Лучше mapper в @ControllerAdvice.
> - Stack trace может быть длинным — обернутая cause часто полезнее.
> - Если бизнес-критично «никогда не упасть», добавьте default явно (но потеряете compile-time exhaustiveness).
>
> **Связанные вопросы:** [[Q3]] — switch expressions требуют exhaustiveness; [[Q5]] — sealed classes; [[Q7]] — null handling.
>
> ---
>
> #### D) `Square` будет приведён к `Object` и попадёт в имплицитный default-case — ❌ Неверно
>
> **Что на самом деле:** Никакого имплицитного `default`-case нет. Если switch объявлен exhaustive (без default) и компилировался для closed sealed-set, runtime НЕ создаёт fallback на лету.
>
> **Откуда путаница:** В Groovy/Ruby switch имеет implicit default returning nil. Java строже — exhaustive switch без default бросает MatchException, не возвращает «что-то».
>
> **Если бы это было правдой:** Type system гарантий не было бы — exhaustive sealed switch стал бы тождественен switch с default. Compile-time проверка теряла бы смысл.

## Q11. Как pattern matching работает с generic types и type erasure?

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
>
> **Вопрос:** Что произойдёт при `if (obj instanceof Box<String> b) { ... }` где `Box<T>` — generic record, `obj` имеет тип `Object` и фактически содержит `Box<Integer>`?
>
> ---
>
> #### A) Cast пройдёт благополучно — JVM проверяет фактический type parameter в runtime — ❌ Неверно
>
> **Что на самом деле:** Java использует **type erasure** — generic type parameters стираются в runtime. `Box<String>` и `Box<Integer>` имеют один и тот же class `Box` в bytecode. Pattern `Box<String>` НЕ проверяет фактический type parameter в runtime.
>
> **Откуда путаница:** В Kotlin (на JVM) можно использовать `reified` параметры с `inline fun` — частичная runtime type info. В Java reified generics нет, и pattern matching не делает исключения.
>
> **Если бы это было правдой:** Поведение Java generics было бы radically другим — все ClassCastException на generics-mismatch исчезли бы. Это бы потребовало пересоздать JVM bytecode.
>
> ---
>
> #### B) **Compile error** — компилятор требует `Box<?>` или явный raw-type, generic-type pattern запрещён — ❌ Неверно
>
> **Что на самом деле:** Generic type pattern `Box<String>` РАЗРЕШЁН в Java 21 для record patterns. Компилятор проверяет, что target type (`Object`) совместим с `Box<String>` и выдаёт unchecked warning, если type не может быть статически доказан.
>
> **Откуда путаница:** В чистом `instanceof` (без record pattern) до Java 16 запрещалось `obj instanceof List<String>` — нужен был raw type или wildcard. С record patterns правило ослаблено.
>
> **Если бы это было правдой:** Record patterns с generic record (типа `record Result<T>(T value)`) были бы непригодны — нужен был бы `Result<?>` везде, теряя type safety binding variable.
>
> ---
>
> #### C) Runtime бросит `ClassCastException` на доступе к `b.value()` — компилятор делает наивный cast — ❌ Неверно
>
> **Что на самом деле:** Если `obj` — `Box<Integer>`, то `obj instanceof Box<String> b` СОМПИЛИРУЕТСЯ с unchecked warning (точное соответствие type parameter не проверяется), но runtime cast к `Box<String>` пройдёт — так как сам `Box`-class у обоих один. ClassCastException произойдёт только при попытке использовать `String s = b.value()` (там скрытый bridge-cast).
>
> **Откуда путаница:** Многие думают, что cast в pattern matching = `Box<String>` cast — но фактически это `Box` cast (после erasure). String-specifying происходит при доступе к компоненту.
>
> **Если бы это было правдой:** Это catastrophic: cast на entry бы убил весь pipeline, нет shot at processing. На практике ошибка отложенная — что хуже для дебага, но не «на каждой строке».
>
> ---
>
> #### D) Pattern компилируется с unchecked warning, runtime cast пройдёт (тот же `Box.class` после erasure); `b.value()` вернёт реальный объект, но cast к `String` (где использовали binding) бросит `ClassCastException` — это **heap pollution** феномен — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pattern matching с generics — тонкая зона из-за type erasure:
> 1. **Type erasure:** `Box<T>` в bytecode — просто `Box`. Type parameter `<String>` существует только в compile time.
> 2. **Unchecked warning:** компилятор предупреждает, что `instanceof Box<String>` не может быть полностью проверен в runtime.
> 3. **Heap pollution:** ситуация, когда `Box<String>` фактически содержит `Integer` — нарушение invariant без compile time проверки.
> 4. **Bridge cast:** компилятор вставляет cast `(String) b.value()` при доступе — там и взрывается.
>
> **Пример:**
> ```java
> record Box<T>(T value) {}
>
> Object obj = new Box<Integer>(42);
>
> // Compile: warning «unchecked cast»
> // Runtime: pattern сматчится — Box.class == Box.class
> if (obj instanceof Box<String> b) {
>     // На след. строке компилятор вставит (String) b.value() — ClassCastException
>     String s = b.value();   // throws ClassCastException
> }
>
> // Безопаснее — wildcard:
> if (obj instanceof Box<?> b) {
>     Object v = b.value();   // Object — без cast, безопасно
>     if (v instanceof String s) { /* теперь точно String */ }
> }
>
> // Или record pattern с типизированным компонентом:
> if (obj instanceof Box<?>(String s)) {
>     // s — String гарантированно, runtime check на компоненте
> }
> ```
>
> **Когда применять:**
> - Generic-record pattern (`Box<?>(String s)`) — наиболее безопасный для смешанных типов.
> - `Box<?> b` + последующий instanceof для двойной проверки — defensive.
> - Точный type parameter (`Box<String>`) — только когда уверены через какой-то upstream-check.
>
> **Подводные камни:**
> - Unchecked warning часто игнорируется — но именно он маркирует потенциальный ClassCastException.
> - В record pattern с generic компонентом: `case Box<?>(Object o)` — o имеет тип Object, не T.
> - Wildcard `Box<?>` без binding: можно использовать pattern, но binding variable `b` будет `Box<?>` — методы возвращают capture type.
>
> **Связанные вопросы:** [[Q4]] — record patterns; [[Q14]] — типы и unboxing в patterns; [[Q1]] — обзор форм pattern matching.

## Q12. Какие практические применения pattern matching в production-коде?

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
>
> **Вопрос:** В каком сценарии pattern matching даёт наибольшую ценность над polymorphic-dispatch (virtual method) — и почему?
>
> ---
>
> #### A) Domain events с разной структурой данных и операциями, живущими ВНЕ event-классов (handlers, logging, projections) — pattern matching группирует операцию в одном месте, без распыления через иерархию; virtual dispatch требовал бы `void handle(EventBus bus)` в каждом event, нарушая separation of concerns — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pattern matching выигрывает у polymorphism в следующих сценариях:
> 1. **Operations внешние** к иерархии — handlers, serializers, projections, validators. Не помещаются в data classes по архитектурной чистоте.
> 2. **Разная структура данных** subtypes — record-patterns extractют именно нужные поля. Virtual метод вынужден работать через accessor цепочки.
> 3. **Стабильная иерархия типов** — типы редко добавляются, операции часто. Это «closed type, open operations» из expression problem.
> 4. **Code locality** — вся обработка иерархии в одном switch, легко проверить полноту (compile-time exhaustiveness).
>
> **Пример:**
> ```java
> // Domain events в Wolt-like order pipeline
> sealed interface OrderEvent {}
> record OrderCreated(String orderId, String customerId, Money total) implements OrderEvent {}
> record OrderShipped(String orderId, String carrier, Instant when) implements OrderEvent {}
> record OrderCancelled(String orderId, String reason) implements OrderEvent {}
>
> // Handler — отдельный сервис, не часть event-иерархии
> @Service
> class OrderEventHandler {
>     void handle(OrderEvent event) {
>         switch (event) {
>             case OrderCreated(var id, var cust, var total) -> {
>                 emailService.sendConfirmation(cust, id);
>                 analytics.track("order_created", total);
>             }
>             case OrderShipped(var id, var carrier, var when) ->
>                 notificationService.sendShipped(id, carrier);
>             case OrderCancelled(var id, var reason) -> {
>                 inventoryService.restock(id);
>                 metrics.incrementCancellation(reason);
>             }
>         }
>     }
> }
> // Добавили record OrderRefunded — compile error в handle() пока не добавили case
> ```
>
> **Когда применять:**
> - Event-sourced системы (Booking, Wolt) — handlers, projections, aggregates.
> - JSON/XML/Protobuf parsers — Jackson, Jaxb (sealed tree of nodes).
> - State machines с external transition logic.
> - DTO transformations: API DTO → Domain → Persistence DTO.
>
> **Подводные камни:**
> - Если event-handler логика растёт — выносите в отдельные методы, не в case.
> - Cross-cutting concerns (logging, tracing) — рекомендуется аспект, не повторение в каждом case.
> - При thousands of events иерархия sealed становится огромной — рассмотрите hybrid approach (sealed для категорий, polymorphism внутри).
>
> **Связанные вопросы:** [[Q4]] — record patterns; [[Q5]] — sealed classes; [[Q8]] — pattern matching vs Visitor.
>
> ---
>
> #### B) Простые арифметические операции на shapes — `area()`, `perimeter()`, `name()` — pattern matching короче — ❌ Неверно
>
> **Что на самом деле:** Простые операции, ОТНОСЯЩИЕСЯ к типу (area конкретно для Circle), идеально ложатся в polymorphism. `record Circle(double r) implements Shape { double area() { return Math.PI*r*r; } }` — естественно. Pattern matching здесь нарушает encapsulation и Open-Closed Principle.
>
> **Откуда путаница:** Pattern matching выглядит «модно», и для shape-area любой пример его показывает. Реальная же сила pattern matching — НЕ в простых операциях, а в externalized logic.
>
> **Если бы это было правдой:** Использовать pattern matching для тривиальных methods усложняло бы код — нет single source of truth для «как считать area Circle».
>
> ---
>
> #### C) Performance-критичный hot path где dispatcher должен быть быстрее — ❌ Неверно
>
> **Что на самом деле:** Virtual dispatch на современных JVM (JIT, inline caches) — не медленнее pattern matching switch. JEP 441 ввёл bytecode-intrinsic `typeSwitch`, но он СОПОСТАВИМ по скорости с virtual dispatch, не радикально быстрее.
>
> **Откуда путаница:** В C++ virtual вызовы дороги (vtable indirect call), и компилируемые языки выигрывают от static dispatch. В Java JIT-оптимизация делает virtual вызовы почти бесплатными.
>
> **Если бы это было правдой:** Микро-бенчмарки показывали бы 10x разницу — но JMH-тесты показывают < 5% разницу.
>
> ---
>
> #### D) Любой код, использующий switch — pattern matching универсально лучше — ❌ Неверно
>
> **Что на самом деле:** Switch на `int`, `enum`, `String` — не нужно patterm matching. Эти классические switch-формы остаются идиоматичными. Pattern matching — для type-based dispatch и деконструкции record.
>
> **Откуда путаница:** Tool-в-руках syndrome: «у нас новый молоток, всё похоже на гвоздь».
>
> **Если бы это было правдой:** Простые enum switch (`switch(day) { case MONDAY -> ... }`) стал бы перегружен типизацией, теряя читаемость.

## Q13. В чём отличие Pattern Matching в Java от Scala/Kotlin?

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
>
> **Вопрос:** Какая возможность Scala pattern matching ОТСУТСТВУЕТ в Java 21 (и почему её сложно добавить из-за erasure)?
>
> ---
>
> #### A) Тип-проверяющие `case` ветки (`case Circle => ...`) — в Java их нет — ❌ Неверно
>
> **Что на самом деле:** Type patterns с binding (`case Circle c -> ...`) — это и есть Java 21 фича. Pattern matching for switch (JEP 441) полностью покрывает type-checking case.
>
> **Откуда путаница:** Привычка к pre-Java-21 синтаксису. Многие, использующие Java 17 LTS, не сталкивались с этими фичами в production.
>
> **Если бы это было правдой:** Pattern matching в Java был бы радикально беднее Scala — но это уже не так. Базовый type-checking case в обоих языках работает идиоматично.
>
> ---
>
> #### B) **List/sequence patterns** (`case List(first, rest @ _*) => ...` в Scala) и **literal patterns** (`case 0 => "zero"`) — нельзя матчить на содержимое коллекции или конкретное значение литерала; Scala также имеет `extractor` (`unapply`) для произвольных типов, а в Java только record-patterns — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Различия Java 21 vs Scala/Kotlin pattern matching:
> 1. **List/Array patterns** — в Scala `case List(a, b, _*)` извлекает первые элементы и tail. В Java нет — ни для `List`, ни для массивов. Workaround: проверка `size()` + access по индексу.
> 2. **Literal patterns** — `case 0 -> ...` в Scala/Kotlin. В Java только в classical switch (`case 0:`), но без integration с type-patterns в одном switch.
> 3. **Extractor objects** — в Scala можно определить `def unapply(...): Option[(A, B)]` для произвольного класса (не только case class). В Java аналогично — только record (с автоматическими аксессорами).
> 4. **Tuple patterns** — `case (a, b) => ...` в Scala. В Java аналог только через record.
>
> Из-за erasure в Java добавление list patterns — нетривиально: `case List(Integer i, ...) ` потребовал бы runtime type check на содержимом, что эрозит type safety.
>
> **Пример:**
> ```java
> // Scala (для сравнения)
> // list match {
> //   case List(0, _*)            => "starts with zero"
> //   case List(_, _, _)          => "exactly three elements"
> //   case Nil                    => "empty"
> // }
>
> // Java 21 — нет list patterns:
> static String describe(List<Integer> list) {
>     // Workaround #1 — guards
>     return switch (list) {
>         case List<Integer> l when l.isEmpty()
>             -> "empty";
>         case List<Integer> l when l.size() == 3
>             -> "exactly three";
>         case List<Integer> l when !l.isEmpty() && l.get(0) == 0
>             -> "starts with zero";
>         default
>             -> "other";
>     };
> }
>
> // Workaround #2 — wrap в record
> record Triple<T>(T a, T b, T c) {}
> // case Triple(0, var b, var c) -> ...
> ```
>
> **Когда применять:**
> - Знание этих ограничений поможет на интервью обосновать выбор между Java/Kotlin/Scala.
> - В смешанных Java/Scala проектах (Spark) — Scala часть может использовать list patterns, Java часть — нет.
> - Если нужны list/array patterns в Java — оборачивайте collection в record-структуры.
>
> **Подводные камни:**
> - Migration Scala → Java pattern matching: list-patterns придётся разворачивать в guards.
> - Kotlin `when` без exhaustiveness check в statement-форме — easier to misuse чем Java sealed switch.
> - Scala extractor (`unapply`) часто ловит NullPointerException — Java record patterns тоже, но более предсказуемо.
>
> **Связанные вопросы:** [[Q1]] — обзор форм pattern matching в Java; [[Q4]] — record patterns как замена tuple/list; [[Q11]] — generics и erasure.
>
> ---
>
> #### C) Sealed types — Scala был первым, в Java нет — ❌ Неверно
>
> **Что на самом деле:** Sealed classes/interfaces (JEP 409, Java 17) — реальная фича Java, эквивалентная Scala `sealed`. Оба языка позволяют exhaustive pattern matching через sealed.
>
> **Откуда путаница:** Java позаимствовала много из Scala/Haskell — sealed types были одним из последних добавлений (Java 17, 2021). До этого был только `final` (запрет наследования вообще).
>
> **Если бы это было правдой:** Pattern matching в Java был бы непригоден для real-world ADT — но sealed + records + patterns образуют полноценный ADT toolkit.
>
> ---
>
> #### D) Guards (`if` после pattern) — в Java guard невозможно использовать — ❌ Неверно
>
> **Что на самом деле:** Guards в Java реализованы через `when`-clause: `case Integer i when i > 0 -> ...`. Семантика аналогична Scala `case Integer(i) if i > 0`.
>
> **Откуда путаница:** Синтаксис разный — Scala использует `if`, Java выбрал `when` (для отличия от условного выражения). Многие думают, что разное keyword = разная семантика.
>
> **Если бы это было правдой:** Guard pattern был бы серьёзным гэпом — но в Java 21 он есть (JEP 441).

## Q14. Как pattern matching взаимодействует с unboxing и преобразованием типов?

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
>
> **Вопрос:** Дано `record Box(int value) {}` и `Object obj = new Box(42);`. Что компилируется и как ведёт себя `if (obj instanceof Box(int v))`?
>
> ---
>
> #### A) Компилируется; `v` имеет тип `Integer` — wrapper-тип всегда используется в pattern matching — ❌ Неверно
>
> **Что на самом деле:** В record pattern тип binding variable точно соответствует объявленному в record. `record Box(int value)` имеет component-тип `int` (primitive), и pattern `Box(int v)` даёт `v: int` (primitive). Wrapper Integer возникает только если компонент объявлен как `Integer`.
>
> **Откуда путаница:** В обычном `instanceof Integer i` (без record) `i` — Integer wrapper. Многие переносят эту интуицию на record patterns.
>
> **Если бы это было правдой:** Каждое разыменование record-component сопровождалось бы (un)boxing — ощутимая performance penalty в hot path.
>
> ---
>
> #### B) НЕ компилируется — `int` нельзя использовать как pattern, нужен `Integer` — ❌ Неверно
>
> **Что на самом деле:** Record pattern с primitive type — допустим и idiomatic в Java 21. `case Box(int v)` извлекает `v` как `int`, без autoboxing. Это специально для performance в numerical code.
>
> **Откуда путаница:** В type patterns `case int i` (без record) запрещён — primitive type patterns пока в preview (JEP 488, Java 23+). Но в record-pattern primitive component — стандарт.
>
> **Если бы это было правдой:** Record patterns с примитивными компонентами были бы непригодны — но они являются key feature для performance.
>
> ---
>
> #### C) Компилируется; `v` имеет тип `int` (primitive) — record pattern точно соответствует объявленному типу компонента, никакого autoboxing — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pattern matching и type conversion:
> 1. **Type pattern** (`case Integer i`) — на boxed type, без primitive. Чистый instanceof + binding к wrapper-типу.
> 2. **Record pattern** (`case Box(int v)`) — тип binding variable ТОЧНО соответствует record component declaration. Если component `int` — binding `int`. Если `Integer` — binding `Integer`.
> 3. **Autoboxing в pattern** — НЕ выполняется. `case Box(Integer v)` для `record Box(int value)` — compile error: «type Integer is not a subtype of int».
> 4. **var в pattern** — type inference: `case Box(var v)` → `v` будет `int` если record-component `int`.
>
> Primitive type patterns (вне record) — preview в Java 23 (JEP 488): `case int i -> ...` позволит type-check + unbox в одном.
>
> **Пример:**
> ```java
> record IntBox(int value) {}
> record IntegerBox(Integer value) {}
>
> Object obj1 = new IntBox(42);
> Object obj2 = new IntegerBox(42);
>
> // Record pattern с primitive
> if (obj1 instanceof IntBox(int v)) {
>     int doubled = v * 2;        // v — int, прямая арифметика
> }
> // НЕ компилируется:
> // if (obj1 instanceof IntBox(Integer v)) { ... }
> //   error: Integer is not subtype of int
>
> // Record pattern с wrapper
> if (obj2 instanceof IntegerBox(Integer v)) {
>     int doubled = v * 2;        // unboxing на использовании
> }
>
> // var inference
> if (obj1 instanceof IntBox(var v)) {
>     // v — int (соответствует declaration)
> }
>
> // Type pattern + Integer (вне record)
> Object i = Integer.valueOf(5);
> if (i instanceof Integer x) {
>     int y = x + 1;              // unboxing на использовании x
> }
> ```
>
> **Когда применять:**
> - Performance-критичный numerical код: `record Vec3(double x, double y, double z)` + `case Vec3(double x, double y, double z) -> Math.sqrt(...)` — без boxing.
> - Coordinate/geometry domains (game engines, GIS Yandex Lavka).
> - Финансовые расчёты на `BigDecimal` — точный contract на тип.
>
> **Подводные камни:**
> - Mixing primitive и wrapper в record вызывает compile errors при mismatch.
> - `var` в record-pattern удобен, но скрывает тип — может маскировать unexpected autoboxing.
> - Generic record (`Box<T>`) не может иметь primitive type parameter — T всегда reference. Используйте specialized records (`IntBox`, `LongBox`).
> - При деконструкции `null`-component с примитивным типом — NPE на унbox.
>
> **Связанные вопросы:** [[Q4]] — record patterns deep dive; [[Q11]] — generics и type erasure; [[Q1]] — формы pattern matching.
>
> ---
>
> #### D) Компилируется, но runtime бросает `ClassCastException` — primitive int нельзя cast от Integer — ❌ Неверно
>
> **Что на самом деле:** При записи `new Box(42)` int-value сохраняется как primitive в record (компилятор не boxes). При pattern matching `Box(int v)` value retrieved через accessor `value()` (возвращает int) — нет cast Integer → int.
>
> **Откуда путаница:** В generic code (`List<Integer>`) autoboxing → unboxing цикл может приводить к unexpected ClassCastException при mismatch. В record это не работает, тип component жёстко фиксирован.
>
> **Если бы это было правдой:** Каждое использование record pattern с primitive component было бы потенциальным runtime crash — это бы делало feature непригодной.

## Q15. Когда стоит / не стоит использовать pattern matching?

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


> [!mcq]
>
> **Вопрос:** В каком из сценариев pattern matching — НЕправильный выбор и polymorphism через override-методы лучше?
>
> ---
>
> #### A) Парсинг JSON в библиотеке Jackson 2.16+: `sealed interface JsonValue permits JsonNull, JsonNumber, JsonString, JsonArray, JsonObject` — операции `render`, `validate`, `transform` через switch — ❌ Неверно
>
> **Что на самом деле:** Парсинг JSON — каноничный кейс pattern matching: типы (5-7 nodes) стабильны, операций много и они внешние (rendering, validation, traversal). Иерархия закрытая, sealed гарантирует exhaustiveness. Это правильное использование.
>
> **Откуда путаница:** Привычка из OOP-учебников: «полиморфизм всегда лучше». На самом деле для closed type + open operations pattern matching — оптимум.
>
> **Если бы это было правдой:** Jackson 2.16+ пришлось бы переписать на classical Visitor с дополнительным boilerplate — но реальная реализация и использует pattern matching.
>
> ---
>
> #### B) Computing `area()` и `perimeter()` для геометрической иерархии `Circle`/`Square`/`Triangle` через `switch` в сервисе `ShapeCalculator` — ❌ Неверно
>
> **Что на самом деле:** Если `area()` — это «внутреннее» поведение типа (зависит ТОЛЬКО от полей этого типа), polymorphism через `record Circle(double r) implements Shape { double area() { return Math.PI*r*r; } }` — лучше. Но если внешний сервис добавляет cross-cutting concerns (logging, caching, units conversion), pattern matching ок. Это grey-area, в зависимости от деталей.
>
> **Откуда путаница:** Простые примеры pattern matching на shape-area создают впечатление, что это всегда плохо. На самом деле зависит от того, ВНУТРЕННЯЯ ли логика или внешняя.
>
> **Если бы это было правдой:** Все примеры из tutorials по pattern matching были бы антипаттерны — но они работают как обучающие примеры с правильным контекстом.
>
> ---
>
> #### C) Domain events в Wolt order pipeline: `OrderCreated`, `OrderShipped`, `OrderCancelled` — handlers вынесены в отдельный сервис — ❌ Неверно
>
> **Что на самом деле:** Domain events с external handlers — это classic case для pattern matching. Events — это data (record-структура), handlers — внешний код, который не должен класть знание о всех «куда event послать» в сам event-класс. Polymorphism здесь нарушал бы SRP.
>
> **Откуда путаница:** «Pattern matching заменяет dispatch» — но не всегда «dispatch заменяемый pattern matching = bad».
>
> **Если бы это было правдой:** Event-sourced системы (Booking, Wolt) переписали бы handler-сервисы — но они используют именно pattern matching с sealed events.
>
> ---
>
> #### D) **Big polymorphic иерархия без sealed**, где типы добавляются часто (третьими сторонами через plugin SPI), и поведение тесно связано с типом — каждый subtype имеет свою implementation метода `process()`, и владелец иерархии хочет, чтобы новые типы добавлялись БЕЗ модификации существующего dispatch-кода — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pattern matching проигрывает polymorphism в следующих сценариях:
> 1. **Open hierarchy** — типы добавляются внешними клиентами (plugin API, SPI, framework extensions). Pattern matching ломается на каждый новый type; polymorphism — нет.
> 2. **Behavior tightly coupled with type** — operation `process()` имеет уникальную реализацию для каждого type, не разделяет cross-cutting logic. Encapsulation в type безопаснее.
> 3. **Когда добавление операции редко** — если новые операции не появляются, dispatch table не растёт; polymorphism стабилен.
> 4. **Большая иерархия (50+ types)** — pattern matching switch становится unmaintainable, разносится по тысячам строк.
>
> Это «**open type, closed operations**» сторона expression problem.
>
> **Пример:**
> ```java
> // ✗ Pattern matching — плохо: иерархия открыта, поведение coupling
> // sealed нет, новые types добавляются через plugin
> abstract class JobProcessor {
>     public abstract void process(JobContext ctx);
> }
> class EmailProcessor extends JobProcessor { ... }
> class SmsProcessor extends JobProcessor { ... }
> class WebhookProcessor extends JobProcessor { ... }
> // New plugin: PushNotificationProcessor — НЕ ломает existing dispatch
>
> // ✗ Pattern matching сюда вред:
> static void run(JobProcessor jp, JobContext ctx) {
>     switch (jp) {
>         case EmailProcessor   e -> e.sendEmail(ctx);
>         case SmsProcessor     s -> s.sendSms(ctx);
>         case WebhookProcessor w -> w.callWebhook(ctx);
>         default -> throw new IllegalStateException();
>         // Каждый новый plugin → нужно обновить этот switch
>         // Который, возможно, в чужом jar (не контролируем)
>     }
> }
>
> // ✓ Polymorphism — открыт для extension:
> static void runPoly(JobProcessor jp, JobContext ctx) {
>     jp.process(ctx);  // virtual dispatch — каждый class знает своё process
> }
> ```
>
> **Когда применять:**
> - Spring `BeanPostProcessor`, `HandlerInterceptor` — open SPI, реализуют плагины.
> - JDBC driver `Connection` / `Statement` — vendor implementations.
> - Servlet API — `HttpServlet` + override `doGet/doPost`.
> - Любая plugin-архитектура (IntelliJ, VSCode extensions).
>
> **Подводные камни:**
> - Hybrid approach: sealed для closed core (известные типы) + extension point (`UserDefined`) для open part — сложно, но работает.
> - Polymorphism может маскировать business logic в data classes — нарушение SRP. Балансируйте.
> - Pattern matching НЕ заменяет good OOP design — это complementary feature.
>
> **Связанные вопросы:** [[Q5]] — sealed types для closed hierarchies; [[Q8]] — pattern matching vs Visitor; [[Q12]] — практические применения.

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
