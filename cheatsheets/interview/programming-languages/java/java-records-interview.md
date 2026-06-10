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

`Java Records` (JEP 395, stable в Java 16) — компактный синтаксис для неизменяемых носителей данных (immutable data carriers). Компилятор автоматически генерирует конструктор, `equals`/`hashCode`/`toString` и accessor-методы. Это де-факто стандарт для DTO в современной Java и частая тема собеседований.

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

**Record** (Java 14 preview, 16 stable, JEP 395) — особый вид класса для неизменяемых носителей данных (immutable data carriers). Вы описываете только сами данные в заголовке — компонент за компонентом, — а весь шаблонный код компилятор генерирует за вас. Главная идея: убрать ручное написание конструктора, геттеров, `equals`/`hashCode`/`toString`, которое раньше занимало десятки строк или требовало Lombok.

По объявлению `record Point(int x, int y) {}` компилятор автоматически генерирует:

- `private final` поля для каждого компонента — отсюда неизменяемость.
- Канонический конструктор со всеми компонентами.
- Accessor-методы (без префикса `get` — метод называется как поле: `x()`, а не `getX()`).
- `equals()` и `hashCode()` на основе **всех** компонентов — равенство по значению, а не по ссылке.
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

**Важная оговорка — неизменяемость поверхностная (shallow).** `final` здесь только ссылки на компоненты: если компонент — мутабельный объект (`List`, `Date`, массив), его содержимое можно изменить снаружи и после создания record. Стандартный приём защиты — защитное копирование в compact constructor (Q3): `items = List.copyOf(items);` — accessor тогда отдаёт неизменяемую копию, и заодно отсекаются `null`-элементы. Аналогично `Map.copyOf(...)` для map-компонентов и `values.clone()` для массивов. Это классический senior follow-up: «record неизменяем» — верно лишь для самой записи, не для её содержимого.

**Сценарии применения**: DTO, value objects, tuple-подобные структуры, ключи в `Map` (равенство по значению работает «из коробки»), возврат нескольких значений из метода одним объектом.

## Q2. Чем record отличается от обычного класса с Lombok @Value?

Оба решают одну задачу — убрать boilerplate для immutable-значений, — но record делает это средствами самого языка, а `@Value` через внешний annotation processor. Ключевое отличие: record — это часть JVM, его понимают компилятор, рефлексия и pattern matching; Lombok же генерирует обычный класс на этапе компиляции и существует «вне» спецификации языка.

| Критерий | Lombok @Value | Record |
|----------|--------------|--------|
| Компилятор | Требует Lombok на classpath | Встроено в Java |
| Наследование | `final` класс | `final` (нельзя расширить) |
| Геттеры | `getX()` | `x()` (без префикса) |
| Поддержка IDE | Требует плагина | Без плагинов |
| Pattern matching | Нет | Да (Java 21+) |
| Serialization | Стандарт | Специфичное поведение |
| Java < 14 | Работает | Недоступен |

**Рекомендация:** на Java 17+ предпочитайте records — они предсказуемее (поведение задано спецификацией, а не реализацией Lombok), не требуют плагинов в IDE и сборке, и только они умеют деконструкцию в pattern matching. Lombok `@Value` остаётся актуален лишь там, где нужна совместимость со старыми версиями Java или специфичные фичи Lombok вроде `@Builder`.

## Q3. Что такое Canonical Constructor и Compact Constructor?

Это два способа задать «главный» конструктор record — тот, что принимает все компоненты.

**Canonical constructor** — канонический конструктор со всеми компонентами. Если его не писать, компилятор сгенерирует его сам. Можно объявить явно (с обычным телом и присваиваниями `this.x = x`), когда нужна нетривиальная логика.

**Compact constructor** — компактная форма того же канонического конструктора: вы пишете тело **без списка параметров и без присваиваний**. Параметры неявно те же, что компоненты, а `this.x = x` для каждого поля компилятор подставляет сам в конце тела. Нужен, чтобы вставить валидацию или нормализацию входных данных, не дублируя присваивания.

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

**Подводные камни:**

- В compact constructor вы меняете **параметры** (`value = value.trim()`), а не поля. Присваивание в поле произойдёт автоматически — уже модифицированным значением. Обращение к `this.value` в теле compact constructor бессмысленно: поле ещё не инициализировано.
- Нельзя одновременно объявить и compact, и явный canonical конструктор — это один и тот же конструктор, выберите одну форму.
- Дополнительные (не канонические) конструкторы обязаны через `this(...)` делегировать в канонический — мимо него поля инициализировать нельзя.

## Q4. Можно ли в record добавить методы и дополнительные конструкторы?

Да. Record — полноценный класс, а не просто кортеж: ему можно добавлять обычные методы, дополнительные конструкторы, статические члены и переопределять автогенерируемые методы. Запрещено лишь то, что ломает контракт «носителя данных»: собственные instance-поля и `native`-методы.

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

**Ограничения:**

- Нельзя объявлять собственные instance-поля — состояние record исчерпывается компонентами из заголовка. Иначе нарушился бы инвариант: два record равны тогда и только тогда, когда равны все их компоненты.
- Нельзя объявлять `native`-методы.
- Дополнительный конструктор обязан первым делом вызвать канонический через `this(...)`.
- А вот static-поля и static-методы разрешены — они не входят в состояние экземпляра.

## Q5. Можно ли наследоваться от record?

**Нет — наследование запрещено в обе стороны.** От record нельзя унаследоваться, потому что он неявно `final`. И сам record не может наследовать произвольный класс: он всегда расширяет `java.lang.Record` (это подставляется автоматически), а множественного наследования классов в Java нет. Запрет осознанный — он гарантирует, что сгенерированные `equals`/`hashCode` остаются корректными: подкласс мог бы добавить состояние и нарушить равенство по значению.

Что record **может** — реализовывать интерфейсы. Это и есть штатный путь к полиморфизму.

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

**Альтернатива наследованию:** композиция или sealed-интерфейсы. Sealed-интерфейс ограничивает набор реализаций фиксированным списком (`permits`), и тогда несколько records образуют закрытую иерархию — это удобнее наследования, потому что compiler знает все варианты и может проверять полноту `switch`.

```java
public sealed interface Shape permits Circle, Square, Triangle {}
public record Circle(double radius) implements Shape {}
public record Square(double side) implements Shape {}
public record Triangle(double a, double b, double c) implements Shape {}
```

## Q6. Как сериализовать records с Jackson?

Начиная с Jackson 2.12 records поддерживаются «из коробки» — без дополнительных модулей и аннотаций. Jackson умеет это благодаря тому, что у record есть рефлексивный API компонентов (`RecordComponent`): по нему библиотека определяет имена и порядок свойств. На десериализации Jackson вызывает канонический конструктор, поэтому ваша валидация в compact constructor отработает автоматически.

```java
public record UserDto(String name, String email, int age) {}

// Сериализация
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(new UserDto("Alice", "alice@example.com", 30));
// {"name":"Alice","email":"alice@example.com","age":30}

// Десериализация
UserDto user = mapper.readValue(json, UserDto.class);
```

Аннотации вешаются прямо на компоненты заголовка — они «прорастают» и на accessor, и на параметр конструктора:

```java
// Кастомная сериализация через аннотации
public record ApiUser(
    @JsonProperty("user_name") String name,
    @JsonProperty("user_email") String email,
    @JsonIgnore String password
) {}
```

**Граничный случай — propagation аннотаций.** Аннотация на компоненте копируется компилятором на поле, параметр канонического конструктора и accessor — но только в те места, которые разрешены её `@Target` (`FIELD`, `PARAMETER`, `METHOD`, `RECORD_COMPONENT`). Если кастомная аннотация объявлена, скажем, только с `@Target(ElementType.METHOD)`, на поле она не «прорастёт» — и библиотека, читающая аннотации с полей через рефлексию, молча её не увидит. Поэтому в свои аннотации, рассчитанные на records, добавляйте `ElementType.RECORD_COMPONENT` плюс те таргеты, где аннотацию реально ищут.

## Q7. Как использовать records в Spring?

Records закрывают почти весь «транспортный» слой Spring-приложения: request/response DTO, команды, события, проекции. Они immutable, дают автоматический `equals`/`toString` для логов и тестов, и Spring их полностью понимает — Jackson десериализует тело запроса, а Bean Validation вешается прямо на компоненты. Единственное исключение — JPA-сущности (см. ниже).

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

**Почему record не годится в @Entity:** JPA-провайдеру (Hibernate) нужен конструктор без аргументов и возможность подменить объект proxy/изменять поля через reflection для lazy-загрузки и dirty checking. Record неизменяем и не имеет no-arg конструктора — поэтому сущность остаётся обычным классом, а record используют для проекций результата. Запрет касается именно `@Entity`: начиная с Hibernate 6.2 record можно использовать как `@Embeddable` — встраиваемый value-тип без собственной identity, который Hibernate создаёт через канонический конструктор.

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

**Да — это record patterns, финализированы в Java 21 (JEP 440).** Record можно не только проверить на тип, но и сразу «деконструировать»: одним паттерном `Circle(double r)` извлечь компоненты в переменные. Это работает именно для records, потому что компилятор знает их компоненты и порядок — для обычного класса деконструкции нет. Паттерны вкладываются друг в друга, что позволяет одним выражением вытащить значение из глубины структуры.

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
        case Order(Customer(var name, Address(String city, var country)), var items) -> city;
    };
}
```

**Почему это важно на собеседовании:** в паре с sealed-интерфейсом (Q5, Q13) деконструкция даёт исчерпывающий `switch` — компилятор проверит, что обработаны все варианты, и не потребует `default`. Это и есть «функциональный» способ работы с данными в Java: вместо цепочки `instanceof` + явных приведений — одно компактное выражение. Нюанс версий: на Java 21 ненужные компоненты во вложенном паттерне приходится именовать (как `var name` в примере), а с Java 22 их можно заменить unnamed pattern `_` (JEP 456).

## Q9. Могут ли records быть generic?

Да, record может быть параметризован типами, как любой класс: параметры объявляются после имени, а компоненты используют их в качестве типов. Это делает records естественной заменой кортежам — типобезопасным, с готовыми `equals`/`hashCode`/`toString`.

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

**Сценарий применения:** generic records — удобный способ заменить `Map.Entry<K, V>` и Apache Commons `Pair`/`Triple` собственным выразительным типом с осмысленными именами компонентов. В отличие от анонимных кортежей, такой record самодокументируется и сразу готов к деконструкции в pattern matching.

## Q10. Как сделать record с валидацией через Bean Validation?

Аннотации Bean Validation (`@NotBlank`, `@Email`, `@Min` и т.д.) вешаются прямо на компоненты в заголовке record. Дальше есть два пути запуска проверки: декларативный — поставить `@Valid` на параметр контроллера, и тогда Spring проверит DTO до входа в метод; и программный — вручную через `Validator.validate(...)`, что удобно вне веб-слоя (в сервисах, тестах, фоновых задачах).

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

**Эмпирическое правило:** Bean Validation проверяет формат и границы значений (длина, диапазон, регэксп) уже после создания объекта. Если же инвариант обязан соблюдаться при любом создании record, дублируйте его в compact constructor (Q3) — он отработает даже там, где `@Valid` не вызывается (`new UserRegistration(...)` напрямую в коде).

## Q11. Что такое equals/hashCode в records?

Компилятор генерирует для record **равенство по значению**: `equals()` возвращает `true`, если объект того же типа и все его компоненты попарно равны (через их собственный `equals()`), а `hashCode()` вычисляется из всех компонентов. Контракт `equals`/`hashCode` соблюдается автоматически — равные объекты гарантированно имеют одинаковый хеш, поэтому record безопасно использовать ключом в `HashMap`/`HashSet`.

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

**Подводный камень — массивы.** У массивов `equals()` сравнивает по ссылке, а не по содержимому. Сгенерированный `equals` record честно вызывает `array.equals(...)`, поэтому два record с одинаковыми по содержимому, но разными массивами будут НЕ равны:

```java
public record Data(int[] values) {}

Data d1 = new Data(new int[]{1, 2, 3});
Data d2 = new Data(new int[]{1, 2, 3});
d1.equals(d2);  // false! массивы сравниваются по reference
```

Лечится двумя способами: переопределить `equals`/`hashCode` вручную (через `Arrays.equals`/`Arrays.hashCode`) либо — что предпочтительнее — заменить массив на неизменяемый `List<Integer>`, у которого равенство по содержимому работает само.

## Q12. Как record взаимодействует с serialization?

Records поддерживают стандартную Java-сериализацию, но процесс упрощён и зафиксирован спецификацией: при десериализации объект всегда восстанавливается **через канонический конструктор**, а не через обход полей рефлексией. Это важное преимущество с точки зрения безопасности — нельзя «протащить» в record невалидное состояние мимо валидации, как это исторически делалось с обычными классами. Обратная сторона — точки кастомизации `writeObject`/`readObject` для records не работают.

```java
public record Event(String type, Instant timestamp) implements Serializable {}

// Стандартная Java serialization работает, но:
// - Только через canonical constructor (игнорирует обычную логику readObject)
// - Нет поддержки кастомной сериализации через writeObject/readObject
```

Для `Kryo`, `Protobuf`, Avro — обычно нужна генерация кода (эти библиотеки опираются на собственные схемы или сгенерированные классы, а не на структуру record напрямую).

**Рекомендация:** для межсервисного обмена и хранения предпочитайте JSON/MessagePack через Jackson, а не встроенную Java Serialization — она хрупка к изменению структуры, небезопасна и привязывает формат к JVM.

## Q13. Как использовать records для Algebraic Data Types?

Алгебраические типы данных (ADT) — это «суммы» и «произведения» типов из функциональных языков. В Java их собирают из двух кирпичиков: **record даёт product type** (`Address` — это `city` И `country` одновременно), а **sealed-интерфейс даёт sum type** (`Result` — это `Success` ИЛИ `Failure`, и других вариантов нет). `permits` фиксирует закрытый список реализаций, поэтому компилятор знает все варианты и обеспечивает исчерпывающий pattern matching без `default`.

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

Второй пример — рекурсивный ADT: модель JSON, где значение может вкладывать другие значения (`JsonArray`/`JsonObject` ссылаются на `JsonValue`).

**Сценарий применения:** ADT хорошо ложатся на доменное моделирование, где у сущности конечный набор взаимоисключающих состояний — результат операции, состояние заказа, узел AST/выражения. Главная выгода — при добавлении нового варианта в `permits` компилятор подсветит все `switch`, где его забыли обработать.

## Q14. Когда НЕ стоит использовать records?

Record — это инструмент для **неизменяемых носителей данных с равенством по значению**. Как только требование выходит за эти рамки, record начинает мешать. Признаки, что нужен обычный класс:

1. **JPA-сущности** — Hibernate требует no-arg конструктор и изменяемые поля для proxy/dirty checking (см. Q7).
2. **Нужна mutable-структура** — record неизменяем по определению; «изменение» возможно только созданием копии.
3. **Наследование от класса** — record `final` и расширять его нельзя (Q5).
4. **Объект с богатым поведением и изменяемым состоянием** — если методы меняют состояние объекта, это сущность/сервис, а не носитель данных.
5. **Legacy на Java < 14** — синтаксис недоступен.
6. **Нужно равенство по ссылке (identity)** — record навязывает равенство по значению; если два объекта с одинаковыми данными должны считаться разными, берите обычный класс.

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

Коротко: используйте record там, где данные неизменяемы и равенство определяется значением; всю валидацию и нормализацию держите в compact constructor; сложное создание прячьте за статическими фабриками.

1. **Применяйте для immutable data carriers** — DTO, события, команды. Это «родная» ниша records.

2. **Валидацию выносите в compact constructor** — она сработает при любом создании объекта, а не только через `@Valid`:

```java
public record Percentage(double value) {
    public Percentage {
        if (value < 0 || value > 100) throw new IllegalArgumentException();
    }
}
```

3. **Статические фабрики для нетривиального создания** — дают осмысленные имена и инкапсулируют преобразования вместо «голого» конструктора:

```java
public record Duration(long seconds) {
    public static Duration ofMinutes(int minutes) {
        return new Duration(minutes * 60L);
    }
}
```

4. **Records + sealed = ADT** для моделирования домена с конечным набором состояний (Q13).

5. **Избегайте полей-массивов** — у них `equals`/`hashCode` по ссылке (Q11); берите `List`.

6. **Не используйте для изменяемого состояния** — record по духу immutable; изменчивость — сигнал, что нужен обычный класс.

7. **«Изменение» — через копирование (wither-методы)**. У records нет встроенного аналога Lombok `@With`, поэтому метод `withX` создаёт новую копию с одним заменённым полем:

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
