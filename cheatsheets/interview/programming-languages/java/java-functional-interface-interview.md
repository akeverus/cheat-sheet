---
title: "Вопросы на собеседовании: Java Functional Interfaces"
description: "Функциональные интерфейсы Java 8+: @FunctionalInterface, Function, Predicate, Consumer, Supplier, method references, compose/andThen"
tags:
  - interview
  - programming-languages
  - java-functional-interface-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Functional Interfaces"
prerequisites: []
next: []
updated: 2026-05-31
---
# Вопросы на собеседовании: `Java Functional Interfaces`

Функциональные интерфейсы — основа лямбда-выражений и Stream API в Java 8+. Каждый функциональный интерфейс имеет ровно один абстрактный метод (SAM). На собеседованиях проверяют знание встроенных интерфейсов, method references, compose/andThen и разницы между похожими типами.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [java.util.function Package](https://docs.oracle.com/en/java/docs/api/java.base/java/util/function/package-summary.html) — все стандартные функциональные интерфейсы
- [Baeldung: Functional Interfaces](https://www.baeldung.com/java-8-functional-interfaces) — руководство с примерами

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое функциональный интерфейс?](#q1--что-такое-функциональный-интерфейс)
- [Q2. Зачем нужна аннотация @FunctionalInterface?](#q2-зачем-нужна-аннотация-functionalinterface)
- [Q3. Чем лямбда отличается от анонимного класса?](#q3-чем-лямбда-отличается-от-анонимного-класса)

**Встроенные интерфейсы**
- [Q4. (!) Какие основные функциональные интерфейсы есть в java.util.function?](#q4--какие-основные-функциональные-интерфейсы-есть-в-javautilfunction)
- [Q5. Чем Consumer отличается от Supplier?](#q5-чем-consumer-отличается-от-supplier)
- [Q6. Чем Function отличается от UnaryOperator?](#q6-чем-function-отличается-от-unaryoperator)
- [Q7. Чем Runnable отличается от Callable и Supplier?](#q7-чем-runnable-отличается-от-callable-и-supplier)

**Method References**
- [Q8. (!) Какие 4 вида method references существуют?](#q8--какие-4-вида-method-references-существуют)

**Композиция**
- [Q9. (!) Как работают compose() и andThen() в Function?](#q9--как-работают-compose-и-andthen-в-function)
- [Q10. Как работают and(), or(), negate() в Predicate?](#q10-как-работают-and-or-negate-в-predicate)

**Дополнительно**
- [Q11. Что такое примитивные специализации функциональных интерфейсов?](#q11-что-такое-примитивные-специализации-функциональных-интерфейсов)
- [Q12. Что значит effectively final для переменных в лямбде?](#q12-что-значит-effectively-final-для-переменных-в-лямбде)
- [Q13. Можно ли выбросить checked exception из лямбды?](#q13-можно-ли-выбросить-checked-exception-из-лямбды)
- [Q14. Как создать собственный функциональный интерфейс?](#q14-как-создать-собственный-функциональный-интерфейс)

---

## Q1. (!) Что такое функциональный интерфейс?

**Функциональный интерфейс** — интерфейс ровно с одним **абстрактным** методом (Single Abstract Method, SAM). Именно это «ровно один» даёт компилятору однозначную цель: тело лямбды становится реализацией этого единственного метода. Поэтому любой SAM-интерфейс можно записать лямбдой или method reference вместо громоздкого анонимного класса.

При этом интерфейс может содержать сколько угодно `default` и `static` методов — у них есть реализация, поэтому абстрактными они не считаются и контракт не нарушают.

```java
@FunctionalInterface
public interface Transformer<T> {
    T transform(T input);  // единственный абстрактный метод

    default Transformer<T> andThen(Transformer<T> after) {  // default — ок
        return t -> after.transform(this.transform(t));
    }
}
```

Функциональный интерфейс можно реализовать лямбдой:
```java
Transformer<String> toUpper = s -> s.toUpperCase();
Transformer<String> ref = String::toUpperCase;  // method reference
```

**Граничный случай:** методы, переопределяющие публичные методы `Object` (`equals`, `hashCode`, `toString`), для SAM-проверки за абстрактные не считаются — их можно объявлять в интерфейсе, не теряя функциональности. Поэтому, например, `Comparator<T>` остаётся функциональным, хотя объявляет `equals`.

---

## Q2. Зачем нужна аннотация @FunctionalInterface?

`@FunctionalInterface` — это маркер намерения, который заставляет компилятор проверить SAM-контракт: если абстрактных методов не ровно один — compile error прямо на объявлении интерфейса, а не где-то в коде, который пытается передать туда лямбду.

```java
@FunctionalInterface
public interface BadInterface {
    void first();
    void second();  // COMPILE ERROR: Multiple non-overriding abstract methods
}
```

**Зачем она нужна:**
- **Защищает от случайной поломки.** Кто-то добавит второй абстрактный метод — и сборка упадёт сразу на интерфейсе. Без аннотации ошибка всплыла бы позже и в чужом коде: «лямбда больше не подходит».
- **Документирует контракт.** Явно говорит читателю и IDE, что интерфейс рассчитан на лямбды и method references.
- IDE по аннотации подсвечивает и подсказывает лямбда-синтаксис.

**Важно:** технически аннотация не обязательна — интерфейс с одним абстрактным методом работает как функциональный и без неё (так, `Runnable` существовал задолго до Java 8). Но для собственных SAM-интерфейсов её ставят как Рекомендацию: она ничего не стоит и предотвращает регрессии.

---

## Q3. Чем лямбда отличается от анонимного класса?

Лямбда — это **не** синтаксический сахар над анонимным классом: компилятор генерирует её иначе. Главное отличие — `this` и способ создания объекта.

| | Лямбда | Анонимный класс |
|---|---|---|
| `this` | Ссылается на внешний класс | Ссылается на себя |
| Создание объекта | Нет нового объекта (JVM invokedynamic) | Новый объект каждый раз |
| Поля | Нет собственных полей | Могут быть поля |
| Имплементирует | Только функциональный интерфейс | Любой интерфейс/класс |
| Отладка | Сложнее | Есть имя класса |
| Сериализация | Не рекомендуется | Возможна |

Два момента, на которых чаще всего ловят на собеседовании:

- **`this`.** В лямбде `this` — это объемлющий класс (лямбда не создаёт нового скоупа для `this`). В анонимном классе `this` — сам анонимный объект. Это меняет, к какому полю или методу вы обращаетесь.
- **Создание объекта.** Лямбда транслируется через `invokedynamic` и не порождает отдельный `.class`-файл и новый объект на каждый вызов — JVM может переиспользовать инстанс. Анонимный класс — это всегда новый объект и отдельный класс.

```java
// Анонимный класс
Runnable r1 = new Runnable() {
    @Override
    public void run() {
        System.out.println(this);  // this = анонимный объект
    }
};

// Лямбда
Runnable r2 = () -> System.out.println(this);  // this = внешний класс
```

---

## Q4. (!) Какие основные функциональные интерфейсы есть в java.util.function?

Все встроенные интерфейсы из `java.util.function` проще запомнить не списком, а по четырём «архетипам» — что они делают со входом и выходом:

- **`Function`** — есть вход, есть выход (преобразование).
- **`Predicate`** — есть вход, выход всегда `boolean` (проверка условия).
- **`Consumer`** — есть вход, выхода нет (побочный эффект).
- **`Supplier`** — входа нет, есть выход (фабрика, ленивое значение).

Дальше идут вариации: префикс `Bi` — два аргумента вместо одного; `UnaryOperator`/`BinaryOperator` — частный случай `Function`, где вход и выход одного типа.

| Интерфейс | Сигнатура | Назначение |
|---|---|---|
| `Function<T,R>` | `R apply(T t)` | Преобразование T → R |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | Два входа → результат |
| `UnaryOperator<T>` | `T apply(T t)` | T → T (специализация Function) |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | T, T → T |
| `Predicate<T>` | `boolean test(T t)` | Условие/фильтр |
| `BiPredicate<T,U>` | `boolean test(T t, U u)` | Условие с двумя входами |
| `Consumer<T>` | `void accept(T t)` | Побочный эффект, нет возврата |
| `BiConsumer<T,U>` | `void accept(T t, U u)` | Два входа, нет возврата |
| `Supplier<T>` | `T get()` | Нет входов → результат |
| `Runnable` | `void run()` | Нет входов, нет возврата |
| `Callable<T>` | `T call() throws Exception` | Нет входов → T (с исключением) |

```java
Function<String, Integer> length = String::length;
Predicate<String> isEmpty = String::isEmpty;
Consumer<String> print = System.out::println;
Supplier<List<String>> newList = ArrayList::new;
```

---

## Q5. Чем Consumer отличается от Supplier?

Они зеркальны: `Consumer` **берёт** значение и ничего не возвращает, `Supplier` ничего не берёт и **отдаёт** значение. Consumer — про побочный эффект (вход есть, выхода нет), Supplier — про производство значения (входа нет, выход есть).

| | `Consumer<T>` | `Supplier<T>` |
|---|---|---|
| Входные данные | Есть (тип T) | Нет |
| Возвращаемое | `void` | Значение типа T |
| Назначение | Потребление/side effects | Производство значения |
| Пример | `forEach`, логирование | Фабрика, lazy init |

```java
// Consumer — принимает, ничего не возвращает
Consumer<String> logger = msg -> log.info("Event: {}", msg);
List.of("a", "b").forEach(logger);

// Supplier — ничего не принимает, возвращает
Supplier<LocalDate> today = LocalDate::now;  // ленивое вычисление
Optional.empty().orElseGet(today);
```

---

## Q6. Чем Function отличается от UnaryOperator?

`UnaryOperator<T>` — это `Function<T, T>`, у которого вход и выход одного типа; интерфейс буквально `extends Function<T, T>`. То есть это не отдельная сущность, а более узкое имя для частого случая «преобразование внутри одного типа».

Зачем тогда отдельный интерфейс? Чтобы API мог требовать именно «T → T». Метод `List.replaceAll` принимает `UnaryOperator<E>`, а не `Function<E, R>` — так на уровне типов гарантируется, что элемент заменяется значением того же типа, а не превращается в другой.

```java
Function<String, Integer> parser = Integer::parseInt;   // String → Integer
UnaryOperator<String> trim = String::trim;              // String → String

// UnaryOperator удобен в коллекциях:
List<String> names = new ArrayList<>(List.of("  Alice  ", " Bob "));
names.replaceAll(String::trim);  // replaceAll принимает UnaryOperator<E>
```

Аналогично: `BinaryOperator<T>` — частный случай `BiFunction<T, T, T>` (оба аргумента и результат одного типа); используется, например, в `Stream.reduce`.

---

## Q7. Чем Runnable отличается от Callable и Supplier?

Все три не принимают аргументов, а отличаются по двум осям: **что возвращают** и **могут ли бросать checked exception**. `Runnable` — ничего не возвращает; `Callable` и `Supplier` возвращают `T`, но только `Callable` объявляет `throws Exception`.

| | `Runnable` | `Callable<T>` | `Supplier<T>` |
|---|---|---|---|
| Возврат | `void` | `T` | `T` |
| Исключения | Нет | `throws Exception` | Нет |
| Пакет | `java.lang` | `java.util.concurrent` | `java.util.function` |
| Использование | Thread, Executor | Future, ExecutorService | Optional, lazy evaluation |

```java
Runnable r = () -> System.out.println("fire and forget");

Callable<Integer> c = () -> {
    Thread.sleep(100);   // throws Exception — OK
    return 42;
};
Future<Integer> future = executor.submit(c);

Supplier<String> s = () -> "lazy value";
```

**Как выбирать:**
- Нужно просто что-то выполнить без результата (задача в `Thread`/`Executor`) — `Runnable`.
- Нужен результат из фонового кода, который может бросить checked-исключение (через `Future`) — `Callable`.
- Нужно лениво «достать значение» в обычном коде (`Optional.orElseGet`, ленивая инициализация) — `Supplier`.

**Ключевая разница Callable vs Supplier:** оба возвращают `T`, но `Callable` объявляет `throws Exception`, поэтому годится для кода с checked-исключениями; `Supplier` — нет, и тело его лямбды не должно бросать checked exceptions.

---

## Q8. (!) Какие 4 вида method references существуют?

Method reference — это сокращённая запись лямбды, которая просто вызывает уже существующий метод. Видов четыре, и различаются они тем, **откуда берётся объект-получатель** (`this` вызова):

| Вид | Синтаксис | Эквивалентная лямбда |
|---|---|---|
| Статический метод | `ClassName::staticMethod` | `x -> ClassName.staticMethod(x)` |
| Метод экземпляра на объекте | `instance::method` | `x -> instance.method(x)` |
| Метод экземпляра на типе | `ClassName::instanceMethod` | `x -> x.method()` |
| Конструктор | `ClassName::new` | `x -> new ClassName(x)` |

Самый неочевидный — **третий** (`ClassName::instanceMethod`). Здесь получателем становится первый аргумент лямбды, а не зафиксированный объект. Поэтому `String::toUpperCase` — это `s -> s.toUpperCase()`, а `String::compareTo` превращается в двухаргументную лямбду `(s1, s2) -> s1.compareTo(s2)`: первый параметр — получатель, остальные — аргументы метода.

```java
// 1. Статический метод
Function<String, Integer> parser = Integer::parseInt;
// эквивалентно: s -> Integer.parseInt(s)

// 2. Метод на конкретном объекте
String prefix = "Hello: ";
Function<String, String> greet = prefix::concat;
// эквивалентно: s -> prefix.concat(s)

// 3. Метод на типе (первый аргумент — receiver)
Function<String, String> upper = String::toUpperCase;
// эквивалентно: s -> s.toUpperCase()

Comparator<String> comp = String::compareTo;
// эквивалентно: (s1, s2) -> s1.compareTo(s2)

// 4. Конструктор
Supplier<ArrayList<String>> factory = ArrayList::new;
Function<Integer, ArrayList<String>> withCap = ArrayList::new;
// эквивалентно: n -> new ArrayList<>(n)
```

---

## Q9. (!) Как работают compose() и andThen() в Function?

Оба склеивают две функции в одну, но **в противоположном порядке**. Разница только в том, какая из двух функций выполняется первой; результат первой подаётся на вход второй.

- `f.andThen(g)` — сначала `f`, **затем** `g`. Читается как в тексте: «f, and then g».
- `f.compose(g)` — сначала `g`, потом `f`. Как в математике `f ∘ g` (x): сначала вычисляется внутреннее `g`, его результат уходит в `f`.

```java
Function<Integer, Integer> times2 = x -> x * 2;
Function<Integer, Integer> plus3  = x -> x + 3;

// andThen: this → затем other  (левый → правый)
Function<Integer, Integer> times2ThenPlus3 = times2.andThen(plus3);
times2ThenPlus3.apply(5);  // (5*2)+3 = 13

// compose: other → затем this  (правый → левый)
Function<Integer, Integer> plus3ThenTimes2 = times2.compose(plus3);
plus3ThenTimes2.apply(5);  // (5+3)*2 = 16
```

**Подводный камень:** у `Consumer` есть только `andThen`, но нет `compose`. Consumer возвращает `void`, передавать на вход следующей функции нечего — поэтому «обратной» композиции для него просто не существует.

---

## Q10. Как работают and(), or(), negate() в Predicate?

Это `default`-методы `Predicate`, которые комбинируют условия по правилам булевой логики и возвращают **новый** `Predicate` — исходные при этом не меняются. Так из мелких проверок собирается сложное условие без `if`-ов.

```java
Predicate<String> notEmpty = s -> !s.isEmpty();
Predicate<String> startsWithA = s -> s.startsWith("A");
Predicate<String> longEnough = s -> s.length() > 5;

// and — оба условия истинны
Predicate<String> valid = notEmpty.and(startsWithA);
valid.test("Alice");  // true
valid.test("Bob");    // false

// or — хотя бы одно истинно
Predicate<String> either = startsWithA.or(longEnough);
either.test("Andrew"); // true (starts with A)
either.test("Robert"); // true (length > 5)

// negate — инверсия
Predicate<String> empty = notEmpty.negate();
empty.test("");  // true

// Полезно в Stream:
List<String> result = names.stream()
    .filter(notEmpty.and(startsWithA).and(longEnough))
    .collect(Collectors.toList());
```

`Predicate.not()` (Java 11+) — статический метод для инверсии. Удобен, когда инвертировать нужно method reference: на нём нельзя вызвать `.negate()` напрямую, а `Predicate.not(String::isEmpty)` читается чище, чем лямбда `s -> !s.isEmpty()`.
```java
List<String> nonEmpty = list.stream()
    .filter(Predicate.not(String::isEmpty))
    .collect(Collectors.toList());
```

---

## Q11. Что такое примитивные специализации функциональных интерфейсов?

Дженерики в Java не работают с примитивами, поэтому `Function<Integer, Integer>` оперирует объектами `Integer`. На горячих участках (например, в `IntStream` на миллионах элементов) каждое значение приходится оборачивать в `Integer` и разворачивать обратно — это лишние аллокации и нагрузка на GC. **Примитивные специализации** — это версии тех же интерфейсов, которые работают напрямую с `int`/`long`/`double` и убирают boxing/unboxing.

| Паттерн | Пример |
|---|---|
| `XxxFunction<R>` | `IntFunction<R>`, `LongFunction<R>` — примитивный вход, объектный выход |
| `ToXxxFunction<T>` | `ToIntFunction<T>`, `ToDoubleFunction<T>` — объектный вход, примитивный выход |
| `XxxToYyyFunction` | `IntToDoubleFunction`, `LongToIntFunction` — примитивный → примитивный |
| `XxxPredicate` | `IntPredicate`, `LongPredicate`, `DoublePredicate` |
| `XxxConsumer` | `IntConsumer`, `LongConsumer`, `DoubleConsumer` |
| `XxxSupplier` | `IntSupplier`, `LongSupplier`, `DoubleSupplier`, `BooleanSupplier` |
| `XxxUnaryOperator` | `IntUnaryOperator`, `LongUnaryOperator`, `DoubleUnaryOperator` |
| `XxxBinaryOperator` | `IntBinaryOperator`, `LongBinaryOperator` |

```java
// Без специализации — boxing overhead:
Function<Integer, Integer> square = x -> x * x;

// С специализацией — нет boxing:
IntUnaryOperator squareInt = x -> x * x;
IntStream.range(0, 1000).map(squareInt).sum();
```

---

## Q12. Что значит effectively final для переменных в лямбде?

Переменную из внешнего скоупа лямбда может захватить, только если та `final` или **effectively final** — то есть фактически не меняется после инициализации, даже если слово `final` не написано. Если присвоить ей новое значение, компилятор запретит использовать её в лямбде.

```java
String prefix = "Hello";  // effectively final
Consumer<String> greeter = name -> System.out.println(prefix + name);  // OK

String mutable = "Start";
mutable = "Changed";  // больше не effectively final
Consumer<String> bad = name -> System.out.println(mutable + name);  // COMPILE ERROR
```

**Почему так:** лямбда захватывает **значение** локальной переменной (копию), а не ссылку на ячейку стека. Локальные переменные живут на стеке вызвавшего метода, и к моменту запуска лямбды (например, в другом потоке) этот стек может уже исчезнуть. Запрет на изменение убирает двусмысленность «какое значение видит лямбда» и заодно класс race conditions в параллельном коде.

Поля объекта (`this.field`) под это ограничение не попадают — они лежат в heap, и лямбда захватывает `this`, а через него видит актуальное значение поля. Поэтому поле менять из лямбды можно (но за thread-safety тогда отвечаете вы).

**Обходной приём для изменяемого состояния** — спрятать его за неизменной ссылкой. Сама ссылка на массив остаётся effectively final, а меняется его содержимое:
```java
int[] counter = {0};  // массив — effectively final ссылка
list.forEach(x -> counter[0]++);  // изменяем элемент, а не ссылку
// Но это НЕ thread-safe! Используйте AtomicInteger для параллелизма.
```

---

## Q13. Можно ли выбросить checked exception из лямбды?

Нет — если сам функциональный интерфейс не объявляет `throws`. Лямбда подчиняется сигнатуре своего абстрактного метода: бросить из неё можно только те checked exceptions, что объявлены в этом методе. А стандартные интерфейсы (`Function`, `Predicate`, `Consumer`, `Supplier` и т.д.) `throws` не объявляют вовсе, поэтому checked-исключение из их лямбды — ошибка компиляции.

```java
// COMPILE ERROR: checked IOException
Function<String, byte[]> reader = path -> Files.readAllBytes(Path.of(path));

// Workaround 1: обернуть в RuntimeException
Function<String, byte[]> reader = path -> {
    try {
        return Files.readAllBytes(Path.of(path));
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
};

// Workaround 2: собственный интерфейс с throws
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}

ThrowingFunction<String, byte[]> reader = Files::readAllBytes;
```

**Итог:** в Java нет встроенных «throwing»-версий стандартных интерфейсов. Выбор один из двух: либо обернуть checked в unchecked прямо в лямбде, либо завести собственный SAM-интерфейс, в сигнатуре которого есть `throws`.

---

## Q14. Как создать собственный функциональный интерфейс?

Объявите интерфейс с **одним** абстрактным методом, пометьте `@FunctionalInterface` (чтобы компилятор стерёг SAM-контракт) — и его уже можно реализовывать лямбдой. Дальше по желанию добавляете `default`-методы для удобной композиции, как это сделано в стандартных `Predicate`/`Function`.

```java
@FunctionalInterface
public interface Validator<T> {
    boolean validate(T value);

    default Validator<T> and(Validator<T> other) {
        return value -> this.validate(value) && other.validate(value);
    }

    default Validator<T> or(Validator<T> other) {
        return value -> this.validate(value) || other.validate(value);
    }

    default Validator<T> negate() {
        return value -> !this.validate(value);
    }
}

// Использование:
Validator<String> notEmpty = s -> !s.isEmpty();
Validator<String> notTooLong = s -> s.length() <= 100;
Validator<String> emailValid = s -> s.contains("@");

Validator<String> allValid = notEmpty.and(notTooLong).and(emailValid);
allValid.validate("user@example.com");  // true
```

**Когда создавать кастомный интерфейс:**
- Нужна семантическая ясность (`Validator`, `Transformer` лучше чем `Predicate`, `Function`)
- Нужен `throws`
- Нужны вспомогательные `default` методы специфичные для домена

---

## See also

## See also

- [Java 8](java-8-interview.md) — лямбды, Stream API, Optional как нововведения Java 8
- [Java Stream API](java-stream-interview.md) — Function, Predicate, Consumer в Stream
- [Java Optional](java-optional-interview.md) — Supplier в orElseGet, Consumer в ifPresent
- [Java Concurrency](java-concurrency-interview.md) — Callable, Runnable в многопоточности
- [Java Core](java-core-interview.md) — интерфейсы, default методы Java 8
- [Java Generics](java-generics-interview.md) — типизация функциональных интерфейсов
- [Java 17-21](java-17-21-interview.md) — новые возможности, records с функциональными интерфейсами
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Strategy pattern через функциональные интерфейсы
