---
title: "Вопросы на собеседовании: Java Functional Interfaces"
description: "Функциональные интерфейсы Java 8+: @FunctionalInterface, Function, Predicate, Consumer, Supplier, method references, compose/andThen"
tags:
  - interview
  - programming-languages
  - java-functional-interface-interview
aliases:
  - "Java Functional Interfaces interview"
  - "Java Functional Interfaces собеседование"
  - "функциональные интерфейсы Java вопросы"
difficulty: "intermediate"
updated: "2026-04-20"
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
- [Q1. (!) Что такое функциональный интерфейс?](#q1-что-такое-функциональный-интерфейс)
- [Q2. Зачем нужна аннотация @FunctionalInterface?](#q2-зачем-нужна-аннотация-functionalinterface)
- [Q3. Чем лямбда отличается от анонимного класса?](#q3-чем-лямбда-отличается-от-анонимного-класса)

**Встроенные интерфейсы**
- [Q4. (!) Какие основные функциональные интерфейсы есть в java.util.function?](#q4-какие-основные-функциональные-интерфейсы-есть-в-javautilfunction)
- [Q5. Чем Consumer отличается от Supplier?](#q5-чем-consumer-отличается-от-supplier)
- [Q6. Чем Function отличается от UnaryOperator?](#q6-чем-function-отличается-от-unaryoperator)
- [Q7. Чем Runnable отличается от Callable и Supplier?](#q7-чем-runnable-отличается-от-callable-и-supplier)

**Method References**
- [Q8. (!) Какие 4 вида method references существуют?](#q8-какие-4-вида-method-references-существуют)

**Композиция**
- [Q9. (!) Как работают compose() и andThen() в Function?](#q9-как-работают-compose-и-andthen-в-function)
- [Q10. Как работают and(), or(), negate() в Predicate?](#q10-как-работают-and-or-negate-в-predicate)

**Дополнительно**
- [Q11. Что такое примитивные специализации функциональных интерфейсов?](#q11-что-такое-примитивные-специализации-функциональных-интерфейсов)
- [Q12. Что значит effectively final для переменных в лямбде?](#q12-что-значит-effectively-final-для-переменных-в-лямбде)
- [Q13. Можно ли выбросить checked exception из лямбды?](#q13-можно-ли-выбросить-checked-exception-из-лямбды)
- [Q14. Как создать собственный функциональный интерфейс?](#q14-как-создать-собственный-функциональный-интерфейс)

---

## Q1. (!) Что такое функциональный интерфейс?

**Функциональный интерфейс** — интерфейс ровно с одним **абстрактным** методом (Single Abstract Method, SAM). Может содержать любое количество `default` и `static` методов — они не нарушают контракт.

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

---

## Q2. Зачем нужна аннотация @FunctionalInterface?

`@FunctionalInterface` — маркер намерения. Компилятор проверяет, что интерфейс содержит ровно один абстрактный метод, иначе — compile error.

```java
@FunctionalInterface
public interface BadInterface {
    void first();
    void second();  // COMPILE ERROR: Multiple non-overriding abstract methods
}
```

**Практическая польза:**
- Документирует, что интерфейс предназначен для лямбд
- Защищает от случайного добавления второго абстрактного метода
- IDE подсвечивает и подсказывает лямбда-синтаксис

Без аннотации интерфейс с одним методом всё равно работает как функциональный — аннотация не обязательна технически, но является best practice.

---

## Q3. Чем лямбда отличается от анонимного класса?

| | Лямбда | Анонимный класс |
|---|---|---|
| `this` | Ссылается на внешний класс | Ссылается на себя |
| Создание объекта | Нет нового объекта (JVM invokedynamic) | Новый объект каждый раз |
| Поля | Нет собственных полей | Могут быть поля |
| Имплементирует | Только функциональный интерфейс | Любой интерфейс/класс |
| Отладка | Сложнее | Есть имя класса |
| Сериализация | Не рекомендуется | Возможна |

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

`UnaryOperator<T>` расширяет `Function<T, T>` — частный случай когда тип входа и выхода одинаков.

```java
Function<String, Integer> parser = Integer::parseInt;   // String → Integer
UnaryOperator<String> trim = String::trim;              // String → String

// UnaryOperator удобен в коллекциях:
List<String> names = new ArrayList<>(List.of("  Alice  ", " Bob "));
names.replaceAll(String::trim);  // replaceAll принимает UnaryOperator<E>
```

Аналогично: `BinaryOperator<T>` — частный случай `BiFunction<T, T, T>`.

---

## Q7. Чем Runnable отличается от Callable и Supplier?

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

**Ключевая разница Callable vs Supplier:** `Callable` проверяет checked exceptions, `Supplier` — нет.

---

## Q8. (!) Какие 4 вида method references существуют?

| Вид | Синтаксис | Эквивалентная лямбда |
|---|---|---|
| Статический метод | `ClassName::staticMethod` | `x -> ClassName.staticMethod(x)` |
| Метод экземпляра на объекте | `instance::method` | `x -> instance.method(x)` |
| Метод экземпляра на типе | `ClassName::instanceMethod` | `x -> x.method()` |
| Конструктор | `ClassName::new` | `x -> new ClassName(x)` |

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

Оба объединяют функции в цепочку, но порядок выполнения **обратный**:

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

**Мнемоника:**
- `f.andThen(g)` = сначала `f`, потом `g` (как в тексте: f, а затем g)
- `f.compose(g)` = сначала `g`, потом `f` (g как аргумент для f)

`Consumer` имеет только `andThen`, так как нет возвращаемого значения для передачи.

---

## Q10. Как работают and(), or(), negate() в Predicate?

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

`Predicate.not()` (Java 11+) — статический метод для инверсии:
```java
List<String> nonEmpty = list.stream()
    .filter(Predicate.not(String::isEmpty))
    .collect(Collectors.toList());
```

---

## Q11. Что такое примитивные специализации функциональных интерфейсов?

Стандартные интерфейсы работают с объектами → boxing/unboxing при работе с `int`, `long`, `double`. Примитивные специализации избегают этого.

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

Переменные из внешней области видимости, используемые в лямбде, должны быть `final` или **effectively final** (не изменяться после инициализации).

```java
String prefix = "Hello";  // effectively final
Consumer<String> greeter = name -> System.out.println(prefix + name);  // OK

String mutable = "Start";
mutable = "Changed";  // больше не effectively final
Consumer<String> bad = name -> System.out.println(mutable + name);  // COMPILE ERROR
```

**Почему это ограничение:**
- Лямбда захватывает значение, а не ссылку на стек-переменную
- Изменяемые переменные создали бы race conditions в параллельных потоках
- Поля класса (`this.field`) изменять можно — они в heap

**Workaround для изменяемого состояния:**
```java
int[] counter = {0};  // массив — effectively final ссылка
list.forEach(x -> counter[0]++);  // изменяем элемент, а не ссылку
// Но это НЕ thread-safe! Используйте AtomicInteger для параллелизма.
```

---

## Q13. Можно ли выбросить checked exception из лямбды?

Нет, если функциональный интерфейс не объявляет `throws`. Стандартные интерфейсы (`Function`, `Predicate` и т.д.) не объявляют checked exceptions.

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

**Итог:** в Java нет встроенных "throwing" версий стандартных интерфейсов. Либо wrap в unchecked, либо создать кастомный интерфейс.

---

## Q14. Как создать собственный функциональный интерфейс?

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

- [[java-8-interview|Java 8]] — лямбды, Stream API, Optional как нововведения Java 8
- [[java-stream-interview|Java Stream API]] — Function, Predicate, Consumer в Stream
- [[java-optional-interview|Java Optional]] — Supplier в orElseGet, Consumer в ifPresent
- [[java-concurrency-interview|Java Concurrency]] — Callable, Runnable в многопоточности
- [[java-core-interview|Java Core]] — интерфейсы, default методы Java 8
- [[java-generics-interview|Java Generics]] — типизация функциональных интерфейсов
- [[java-17-21-interview|Java 17-21]] — новые возможности, records с функциональными интерфейсами
- [[design-patterns-interview|Design Patterns]] — Strategy pattern через функциональные интерфейсы
