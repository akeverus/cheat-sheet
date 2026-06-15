---
title: "Вопросы на собеседовании: Java Generics"
description: "Комплексное руководство по вопросам собеседования на тему Java Generics: type erasure, wildcards, PECS, bounded types, bridge methods, generic methods и best practices."
tags:
  - interview
  - programming-languages
  - java-generics-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Generics"
  - "Java Generics interview"
  - "Java Generics собеседование"
prerequisites: []
next: []
updated: "2026-05-20"
---
# Вопросы на собеседовании: `Java Generics`

Комплексное руководство по вопросам собеседования на тему `Java Generics` для `Senior Java Developer`. Охватывает `Type Erasure`, `Wildcards`, `PECS`, `Bounded Types`, `Bridge Methods`, generic-методы, ограничения дженериков и best practices.

## Полезные ссылки

### Официальная документация

- [Java Generics Tutorial](https://docs.oracle.com/javase/tutorial/java/generics/) — официальный туториал Oracle
- [Type Erasure](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html) — стирание типов
- [Wildcards](https://docs.oracle.com/javase/tutorial/java/generics/wildcards.html) — подстановочные знаки
- [Java Generics Interview Questions — Baeldung](https://www.baeldung.com/java-generics-interview-questions) — вопросы с ответами
- [Type Erasure in Java Explained — Baeldung](https://www.baeldung.com/java-type-erasure) — подробно о стирании типов
- [Java Generics PECS — Baeldung](https://www.baeldung.com/java-generics-pecs) — принцип PECS

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы дженериков**
- [Q1. (!) Что такое `Generic Type Parameter` и зачем нужны дженерики?](#q1--что-такое-generic-type-parameter-и-зачем-нужны-дженерики)
- [Q2. (!) Каковы преимущества использования `Generics`?](#q2--каковы-преимущества-использования-generics)
- [Q3. Чем `Generic Method` отличается от `Generic Type`?](#q3-чем-generic-method-отличается-от-generic-type)
- [Q4. (!) Что такое `Type Inference` (вывод типов)?](#q4--что-такое-type-inference-вывод-типов)
- [Q5. Какие соглашения об именовании параметров типа?](#q5-какие-соглашения-об-именовании-параметров-типа)

**Type Erasure (стирание типов)**
- [Q6. (!) Что такое `Type Erasure` и зачем оно нужно?](#q6--что-такое-type-erasure-и-зачем-оно-нужно)
- [Q7. (!) Как `Type Erasure` преобразует generic-код?](#q7--как-type-erasure-преобразует-generic-код)
- [Q8. (!) Доступна ли информация о generic-типе во время выполнения?](#q8--доступна-ли-информация-о-generic-типе-во-время-выполнения)
- [Q9. Что такое `Reifiable` и `Non-Reifiable` типы?](#q9-что-такое-reifiable-и-non-reifiable-типы)

**Bounded Types (ограниченные типы)**
- [Q10. (!) Что такое `Bounded Type Parameter`?](#q10--что-такое-bounded-type-parameter)
- [Q11. (!) Что такое `Multiple Bounds` (`T extends A & B`)?](#q11--что-такое-multiple-bounds-t-extends-a--b)
- [Q12. Что такое `Recursive Type Bound` (`T extends Comparable<T>`)?](#q12-что-такое-recursive-type-bound-t-extends-comparablet)

**Wildcards (подстановочные знаки)**
- [Q13. (!) Что такое `Wildcard Type` и какие виды бывают?](#q13--что-такое-wildcard-type-и-какие-виды-бывают)
- [Q14. (!) Что такое `Upper Bounded Wildcard` (`? extends T`)?](#q14--что-такое-upper-bounded-wildcard--extends-t)
- [Q15. (!) Что такое `Lower Bounded Wildcard` (`? super T`)?](#q15--что-такое-lower-bounded-wildcard--super-t)
- [Q16. Что такое `Unbounded Wildcard` (`?`)?](#q16-что-такое-unbounded-wildcard-)
- [Q17. В чём разница между `<T>` и `<?>`?](#q17-в-чём-разница-между-t-и-)

**PECS и вариантность**
- [Q18. (!) Что такое `PECS` (`Producer Extends, Consumer Super`)?](#q18--что-такое-pecs-producer-extends-consumer-super)
- [Q19. (!) Как дженерики работают с наследованием (инвариантность)?](#q19--как-дженерики-работают-с-наследованием-инвариантность)
- [Q20. Что такое ковариантность, контравариантность и инвариантность?](#q20-что-такое-ковариантность-контравариантность-и-инвариантность)

**Raw Types и обратная совместимость**
- [Q21. (!) Что такое `Raw Type` и зачем избегать?](#q21--что-такое-raw-type-и-зачем-избегать)
- [Q22. Если при создании объекта не указан generic-тип, скомпилируется ли код?](#q22-если-при-создании-объекта-не-указан-generic-тип-скомпилируется-ли-код)

**Bridge Methods и компиляция**
- [Q23. (!) Что такое `Bridge Methods` и зачем они в дженериках?](#q23--что-такое-bridge-methods-и-зачем-они-в-дженериках)
- [Q24. Что такое `Heap Pollution`?](#q24-что-такое-heap-pollution)

**Generic-методы, varargs и статика**
- [Q25. Как работают дженерики с `varargs` (`T... args`)?](#q25-как-работают-дженерики-с-varargs-t-args)
- [Q26. Дженерики в статических методах и полях?](#q26-дженерики-в-статических-методах-и-полях)

**Ограничения и практика**
- [Q27. (!) Можно ли использовать примитивы как generic-тип?](#q27--можно-ли-использовать-примитивы-как-generic-тип)
- [Q28. Можно ли создать массив generic-типа (`new T[]`)?](#q28-можно-ли-создать-массив-generic-типа-new-t)
- [Q29. Почему нельзя создать `List<String>[]`?](#q29-почему-нельзя-создать-liststring)
- [Q30. Как получить `Class<T>` для generic-типа в runtime?](#q30-как-получить-classt-для-generic-типа-в-runtime)
- [Q31. Что такое `@SuppressWarnings("unchecked")` и когда использовать?](#q31-что-такое-suppresswarningsunchecked-и-когда-использовать)
- [Q32. Как сериализовать объекты с generic-полями?](#q32-как-сериализовать-объекты-с-generic-полями)
- [Q33. (!) Что такое `Super Type Token` и как это используется?](#q33--что-такое-super-type-token-и-как-это-используется)
- [Q34. Дженерики и `instanceof` — какие проверки допустимы?](#q34-дженерики-и-instanceof--какие-проверки-допустимы)
- [Q35. Какие изменения в дженериках появились в Java 10+?](#q35-какие-изменения-в-дженериках-появились-в-java-10)

**Wildcards, Type Erasure, Comparable**
- [Q36. (!) В чём разница между `? extends T` и `? super T`? Когда что выбирать?](#q36--в-чём-разница-между--extends-t-и--super-t-когда-что-выбирать)
- [Q37. (!) Как работает `Type Erasure` на практике и какие проблемы создаёт?](#q37--как-работает-type-erasure-на-практике-и-какие-проблемы-создаёт)
- [Q38. Что такое generic-методы и как компилятор выводит тип?](#q38-что-такое-generic-методы-и-как-компилятор-выводит-тип)
- [Q39. Что такое `Comparable<T>` vs `Comparator<T>` с точки зрения дженериков?](#q39-что-такое-comparablet-vs-comparatort-с-точки-зрения-дженериков)
- [Q40. Что такое wildcards (`?`) и когда использовать unbounded `<?>`?](#q40-что-такое-wildcards--и-когда-использовать-unbounded-)

---

## Q1. (!) Что такое `Generic Type Parameter` и зачем нужны дженерики?

**Параметр generic-типа** — это «переменная для типа»: имя вроде `T`, которое в объявлении класса, интерфейса или метода стоит на месте конкретного типа, а реальный тип подставляется при использовании. Дженерики появились в Java 5 и дают **типобезопасность на этапе компиляции**, не заставляя писать отдельный класс под каждый тип данных.

Без дженериков:

```java
public interface Consumer {
    void consume(String parameter); // жёстко привязано к String
}
```

С дженериками:

```java
public interface Consumer<T> {
    void consume(T parameter); // T — параметр типа
}

// Конкретная реализация
public class IntegerConsumer implements Consumer<Integer> {
    @Override
    public void consume(Integer parameter) {
        System.out.println("Consumed: " + parameter);
    }
}
```

Дженерики решают три ключевые проблемы:
1. **Типобезопасность** — ошибки типов ловятся на этапе компиляции, а не падают как `ClassCastException` в runtime.
2. **Устранение кастов** — компилятор знает тип элементов, поэтому ручное приведение `Object` к конкретному типу не требуется.
3. **Переиспользование кода** — один алгоритм или контейнер работает с разными типами без копипасты.

## Q2. (!) Каковы преимущества использования `Generics`?

Коротко: дженерики переносят проверку типов с runtime на компиляцию, убирают ручные касты и позволяют писать обобщённые алгоритмы. Разберём по пунктам.

**1. Типобезопасность на этапе компиляции** — ошибки типов обнаруживаются до запуска программы:

```java
// Без дженериков — ClassCastException в runtime
List list = new ArrayList();
list.add("hello");
list.add(42);
Integer value = (Integer) list.get(0); // Runtime error!

// С дженериками — ошибка компиляции
List<String> typedList = new ArrayList<>();
typedList.add("hello");
typedList.add(42); // Compilation error!
```

**2. Устранение явного приведения типов:**

```java
// Без дженериков
Object obj = list.get(0);
String s = (String) obj; // нужен каст

// С дженериками
String s = typedList.get(0); // каст не нужен
```

**3. Возможность писать обобщённые алгоритмы:**

```java
public static <T extends Comparable<T>> T max(List<T> list) {
    return list.stream().max(Comparator.naturalOrder()).orElseThrow();
}
// Работает с любым Comparable: Integer, String, LocalDate...
```

## Q3. Чем `Generic Method` отличается от `Generic Type`?

Разница — в области видимости параметра типа.

- **Generic-тип** — параметр объявлен на уровне класса/интерфейса (`class Box<T>`) и доступен во всех нестатических методах. Тип фиксируется один раз при создании объекта: у `Box<String>` все методы работают со `String`.
- **Generic-метод** — параметр объявлен перед возвращаемым типом (`static <T> T first(...)`) и живёт только внутри метода. Тип выводится заново на каждом вызове.

```java
// Generic-тип — T доступен во всём классе
public class Box<T> {
    private T value;
    public T getValue() { return value; }
}

// Generic-метод — T ограничен методом
public class Utils {
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}
```

Вызов generic-метода — тип обычно выводится автоматически:

```java
String first = Utils.firstOrNull(List.of("a", "b")); // T = String
// Явное указание типа (редко нужно):
String first = Utils.<String>firstOrNull(someList);
```

**Ключевое следствие:** generic-метод может быть как в generic-классе, так и в обычном, и может быть статическим. Параметр типа класса (`T` из `Box<T>`) в статическом контексте недоступен — он привязан к экземпляру, а у статического метода экземпляра нет. Поэтому статические утилиты (`Collections.max`, `Arrays.asList`) объявляют собственный параметр типа.

## Q4. (!) Что такое `Type Inference` (вывод типов)?

**Type Inference** (вывод типов) — способность компилятора сам определить параметр типа из контекста, чтобы его не приходилось писать руками. Компилятор смотрит на типы аргументов метода и на целевой тип (тип переменной слева, ожидаемый тип параметра) и подставляет подходящий `T`.

```java
// Вывод из аргумента метода
Integer result = returnType(42);      // T = Integer
String text = returnType("hello");    // T = String

// Diamond operator (Java 7+) — вывод из левой части
List<String> list = new ArrayList<>(); // вместо new ArrayList<String>()

// Улучшенный вывод в Java 8 — вывод из контекста лямбды
List<String> sorted = sort(list, Comparator.comparing(String::length));
```

В Java 8 вывод типов был значительно улучшен — компилятор стал учитывать **целевой тип** (target type) в более широком контексте, включая аргументы лямбда-выражений. Подробнее об этом в [вопросах по Java 8](java-8-interview.md).

## Q5. Какие соглашения об именовании параметров типа?

По конвенции параметры типа называют однобуквенными заглавными именами — так их сразу видно среди обычных классов:

| Параметр | Значение | Пример |
|----------|----------|--------|
| `T` | Type (тип) | `class Box<T>` |
| `E` | Element (элемент коллекции) | `interface List<E>` |
| `K` | Key (ключ) | `interface Map<K, V>` |
| `V` | Value (значение) | `interface Map<K, V>` |
| `N` | Number (число) | `class Matrix<N extends Number>` |
| `S, U` | Дополнительные типы | `class Pair<S, U>` |
| `R` | Result (результат) | `Function<T, R>` |

Соглашения необязательны синтаксически — компилятор примет и `class Box<Foo>`, — но их соблюдение делает код узнаваемым: увидев `Map<K, V>`, любой java-разработчик сразу читает «ключ и значение».

## Q6. (!) Что такое `Type Erasure` и зачем оно нужно?

**Type Erasure** (стирание типов) — компилятор использует параметры типа для проверок на этапе компиляции, а затем стирает их из байт-кода. В runtime JVM не видит, что это был `List<String>` — для неё это просто `List`.

**Зачем так сделали:** обратная совместимость. Дженерики добавили в Java 5, а до этого код десять лет писали на raw-коллекциях. Чтобы старый и новый байт-код работали вместе (старый `List` и новый `List<String>` — один и тот же класс), generic-информацию решили не тащить в runtime.

Как это выглядит по шагам:

- `List<String>` (исходный код) → компилятор (type erasure) → `List` (байт-код).
- `List<Integer>` (исходный код) → компилятор (type erasure) → тот же `List` (байт-код).
- `List` (байт-код) → JVM → один и тот же `List.class`.

Это значит, что `List<String>` и `List<Integer>` — это **один и тот же класс** в runtime:

```java
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// true! В runtime оба — просто ArrayList
System.out.println(strings.getClass() == integers.getClass()); // true
```

## Q7. (!) Как `Type Erasure` преобразует generic-код?

При стирании компилятор делает три вещи: заменяет параметры типа на их границу, а где после этого пропадает каст — вставляет его обратно. Разберём каждое преобразование.

**1. Неограниченный тип заменяется на `Object`:**

```java
// До стирания
public class Box<T> {
    private T value;
    public T get() { return value; }
}

// После стирания
public class Box {
    private Object value;
    public Object get() { return value; }
}
```

**2. Ограниченный тип заменяется на первую границу:**

```java
// До стирания
public class NumberBox<T extends Number> {
    private T value;
}

// После стирания — T заменяется на Number
public class NumberBox {
    private Number value;
}
```

**3. Компилятор вставляет приведения типов (checkcast):**

```java
// Исходный код
List<String> list = new ArrayList<>();
String s = list.get(0);

// Что генерирует компилятор (упрощённо)
List list = new ArrayList();
String s = (String) list.get(0); // вставленный каст
```

## Q8. (!) Доступна ли информация о generic-типе во время выполнения?

В общем случае **нет** — параметр типа стирается, и в runtime его не достать. Но есть важное исключение: если generic-аргумент «зашит» в **сигнатуру класса** при наследовании (например, `class CatCage implements Cage<Cat>`), он попадает в метаданные класса и доступен через рефлексию. Стирается параметр типа переменной (`List<String> x`), а не конкретизация в `extends`/`implements`.

```java
// Тип Cat сохраняется в метаданных CatCage.class
public class CatCage implements Cage<Cat> { }

// Можно извлечь через рефлексию
ParameterizedType pt = (ParameterizedType) CatCage.class.getGenericSuperclass();
Type actualType = pt.getActualTypeArguments()[0]; // Cat.class
```

Полный пример получения generic-типа:

```java
public abstract class TypeReference<T> {
    private final Type type;

    protected TypeReference() {
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        this.type = pt.getActualTypeArguments()[0];
    }

    public Type getType() { return type; }
}

// Использование — анонимный подкласс фиксирует тип
TypeReference<List<String>> ref = new TypeReference<>() {};
System.out.println(ref.getType()); // java.util.List<java.lang.String>
```

Этот приём называется **Super Type Token** и активно используется в Jackson, Spring и других фреймворках. Подробнее в Q33.

## Q9. Что такое `Reifiable` и `Non-Reifiable` типы?

Разница в том, доживает ли полная информация о типе до runtime. **Reifiable** («овеществляемый») — тип, который JVM знает полностью; **non-reifiable** — тип, часть информации о котором стёрта. Практический смысл: с reifiable-типами работают `instanceof` и создание массивов, с non-reifiable — нет.

**Reifiable** тип — полностью доступен в runtime:
- Примитивы: `int`, `double`
- Non-generic классы: `String`, `Object`
- Raw types: `List`, `Map`
- Unbounded wildcards: `List<?>`, `Map<?, ?>` (стирать нечего — конкретного аргумента и так нет)
- Массивы reifiable типов: `String[]`, `int[]`

**Non-reifiable** тип — информация теряется после стирания, поэтому в runtime его не отличить от raw-типа:
- `List<String>`, `Map<String, Integer>` — становятся `List`, `Map`
- `List<? extends Number>` — становится `List`

```java
// Можно — reifiable типы
if (obj instanceof List<?>) { }     // OK
String[] arr = new String[10];       // OK

// Нельзя — non-reifiable типы
if (obj instanceof List<String>) { } // Compilation error
List<String>[] arr = new List<String>[10]; // Compilation error
```

## Q10. (!) Что такое `Bounded Type Parameter`?

**Bounded Type Parameter** — параметр типа с верхней границей, заданной через `extends`: `<T extends Number>` принимает только `Number` и его подтипы. Граница даёт две вещи сразу: сужает допустимые аргументы и позволяет вызывать методы граничного типа внутри generic-кода (иначе `T` стирался бы в `Object` и кроме методов `Object` ничего не было бы доступно).

```java
// T должен быть подтипом Number
public class MathBox<T extends Number> {
    private T value;

    public double doubleValue() {
        return value.doubleValue(); // метод Number доступен!
    }
}

MathBox<Integer> intBox = new MathBox<>();   // OK
MathBox<Double> dblBox = new MathBox<>();    // OK
MathBox<String> strBox = new MathBox<>();    // Compilation error!
```

Ограничение типа в generic-методе:

```java
public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}
```

После стирания типов `T extends Number` заменяется на `Number`, а `T extends Comparable<T>` — на `Comparable`.

## Q11. (!) Что такое `Multiple Bounds` (`T extends A & B`)?

Параметр типа может требовать сразу нескольких границ, разделённых `&` — тогда аргумент должен удовлетворять всем им одновременно. Класс (если есть) идёт первым, далее — интерфейсы. Зачем: чтобы внутри метода были доступны методы всех заявленных типов (например, и `Comparable`, и собственные методы класса).

```java
// T должен наследовать Animal И реализовать Serializable И Comparable
public class SmartCage<T extends Animal & Serializable & Comparable<T>> {
    private List<T> animals = new ArrayList<>();

    public T getOldest() {
        return Collections.max(animals); // Comparable доступен
    }
}
```

Правила (причина — после стирания первая граница становится фактическим типом `T`, а у класса может быть только один суперкласс):
- Максимум **один класс** в bounds, и он должен идти **первым**.
- **Интерфейсов** — сколько угодно.
- `T extends Serializable & Comparable<T>` — OK (только интерфейсы).
- `T extends Object & Comparable<T>` — OK (`Object` как класс стоит первым).
- `T extends Comparable<T> & Animal` — **ошибка**, если `Animal` — класс: класс обязан быть первым.

## Q12. Что такое `Recursive Type Bound` (`T extends Comparable<T>`)?

**Recursive Type Bound** — граница параметра типа ссылается на сам параметр (`T extends Comparable<T>`). Читается как «`T` — это тип, который умеет сравниваться с объектами того же типа `T`». Так выражают требование «элемент сопоставим сам с собой», не теряя точности типа. Самый известный пример — `Comparable`:

```java
// T сравним с самим собой
public static <T extends Comparable<T>> T max(List<T> list) {
    T result = list.get(0);
    for (T item : list) {
        if (item.compareTo(result) > 0) {
            result = item;
        }
    }
    return result;
}
```

Другой классический пример — `Enum`:

```java
// Определение в JDK
public abstract class Enum<E extends Enum<E>> implements Comparable<E> {
    public final int compareTo(E o) { ... }
}

// Конкретный enum
public enum Color extends Enum<Color> { RED, GREEN, BLUE }
```

Паттерн используется в Builder-ах, чтобы метод базового билдера возвращал тип наследника, а не базового класса (тогда цепочка `.withName(...).withAge(...)` сохраняет конкретный тип):

```java
public abstract class Builder<T extends Builder<T>> {
    public T withName(String name) {
        this.name = name;
        return self();
    }
    protected abstract T self();
}
```

## Q13. (!) Что такое `Wildcard Type` и какие виды бывают?

**Wildcard** (`?`) — подстановочный знак для неизвестного типа. Нужен там, где конкретный тип не важен или его нельзя выразить параметром типа, но при этом важно сохранить гибкость по аргументу (принять `List<Cat>` и `List<Dog>` одним методом). По сути wildcard добавляет дженерикам вариантность, которой у них нет по умолчанию (см. Q19).

Три вида wildcards:

| Вид | Синтаксис | Чтение | Запись | Пример |
|-----|-----------|--------|--------|--------|
| `Upper Bounded` | `? extends T` | Как `T` | Нельзя (кроме `null`) | `List<? extends Number>` |
| `Unbounded` | `?` | Как `Object` | Нельзя (кроме `null`) | `List<?>` |
| `Lower Bounded` | `? super T` | Как `Object` | Можно `T` и подтипы | `List<? super Integer>` |

## Q14. (!) Что такое `Upper Bounded Wildcard` (`? extends T`)?

`? extends T` означает «какой-то один неизвестный тип, который является `T` или его подтипом». Применяется, когда коллекция выступает источником данных — мы из неё **читаем** (producer). Прочитанные элементы гарантированно являются `T`, поэтому читать как `T` безопасно; а вот добавлять нельзя — конкретный подтип неизвестен.

```java
public class Farm {
    private List<Animal> animals = new ArrayList<>();

    // Без wildcard — принимает ТОЛЬКО Collection<Animal>
    // public void addAnimals(Collection<Animal> newAnimals) { ... }

    // С wildcard — принимает Collection<Cat>, Collection<Dog> и т.д.
    public void addAnimals(Collection<? extends Animal> newAnimals) {
        animals.addAll(newAnimals);
    }
}

List<Cat> cats = List.of(new Cat("Мурка"), new Cat("Барсик"));
farm.addAnimals(cats); // OK — Cat extends Animal
```

**Подводный камень:** в `List<? extends Animal>` нельзя добавлять элементы (кроме `null`). За `? extends Animal` может скрываться `List<Cat>`, и добавить туда `Dog` было бы небезопасно — поэтому компилятор запрещает `add` вообще:

```java
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(42);    // Compilation error!
numbers.add(null);  // OK — null допустим для любого типа
Number n = numbers.get(0); // OK — читать как Number можно
```

## Q15. (!) Что такое `Lower Bounded Wildcard` (`? super T`)?

`? super T` означает «какой-то неизвестный тип, который является `T` или его супертипом». Применяется, когда коллекция — приёмник данных, в который мы **пишем** (consumer). Логика зеркальна upper bounded: чему бы ни был равен неизвестный супертип, объект типа `T` в него заведомо влезает, поэтому `add(T)` безопасен. А вот читать удаётся только как `Object` — точный тип элементов неизвестен.

```java
public static void addIntegers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}

List<Number> numbers = new ArrayList<>();
addIntegers(numbers); // OK — Number super Integer

List<Object> objects = new ArrayList<>();
addIntegers(objects); // OK — Object super Integer

List<Double> doubles = new ArrayList<>();
addIntegers(doubles); // Compilation error! Double не super Integer
```

**Подводный камень при чтении:** из `List<? super Integer>` элементы читаются только как `Object`. Компилятор не знает, что это — `List<Integer>`, `List<Number>` или `List<Object>`, а общий супертип для всех вариантов один — `Object`:

```java
List<? super Integer> list = new ArrayList<Number>();
list.add(42);              // OK — пишем Integer
Object obj = list.get(0);  // OK — читаем как Object
Integer i = list.get(0);   // Compilation error!
```

## Q16. Что такое `Unbounded Wildcard` (`?`)?

`?` — неограниченный подстановочный знак: «список чего угодно». Сигнализирует, что метод не зависит от конкретного типа элементов. Применяется, когда:
- метод работает с элементами только как с `Object` (печать, подсчёт);
- метод трогает лишь API самого контейнера (`size()`, `clear()`, `isEmpty()`), не заглядывая внутрь элементов.

```java
// Принимает список любого типа
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

printList(List.of("a", "b"));  // OK
printList(List.of(1, 2, 3));   // OK
```

**Важное отличие** — `List<?>` и `List<Object>` — это **не одно и то же**:

```java
List<?> wildcardList = new ArrayList<String>();       // OK
List<Object> objectList = new ArrayList<String>();    // Compilation error!
```

`List<Object>` требует именно `List<Object>`, а `List<?>` принимает список любого типа.

## Q17. В чём разница между `<T>` и `<?>`?

Главное различие: `<T>` даёт типу **имя**, на которое можно ссылаться (и тем самым связать вход с выходом метода), а `<?>` — анонимный тип, на который сослаться нельзя.

| Аспект | `<T>` (параметр типа) | `<?>` (wildcard) |
|--------|----------------------|------------------|
| Где используется | Объявление класса/метода | Аргумент типа |
| Можно ссылаться на тип | Да (`T value = ...`) | Нет |
| Ограничения | `extends`, `&` | `extends`, `super` |
| Связь между параметрами | Да | Нет |

**Эмпирическое правило:** если тип нужно использовать дважды (например, вернуть то же, что приняли), берите `<T>`; если тип нужен только чтобы «пропустить» значение, хватит `<?>`.

```java
// <T> — можно связать входной и выходной тип
public static <T> T firstElement(List<T> list) {
    return list.get(0); // возвращаемый тип связан с T
}

// <?> — тип неизвестен, нельзя типизировать возврат
public static void printAll(List<?> list) {
    // list.get(0) возвращает Object
}
```

Подробный анализ выбора между `<T>` и `<?>` описан в статье [Type Parameter vs Wildcard — Baeldung](https://www.baeldung.com/java-generics-type-parameter-vs-wildcard).

## Q18. (!) Что такое `PECS` (`Producer Extends, Consumer Super`)?

**PECS** (Producer Extends, Consumer Super) — мнемоника, которая отвечает на вопрос «какой wildcard выбрать». Смотрите на роль коллекции относительно вашего метода:
- **Producer Extends** — коллекция **отдаёт** вам элементы (вы из неё читаете) → `? extends T`.
- **Consumer Super** — коллекция **принимает** ваши элементы (вы в неё пишете) → `? super T`.

Почему именно так: из `? extends T` безопасно читать (всё внутри — `T`), но опасно писать; в `? super T` безопасно писать `T`, но читать можно лишь как `Object`. PECS просто закрепляет это правило мнемоникой.

Две роли в виде потока данных:

- **Producer Extends:** `Collection<? extends T>` через `get()` отдаёт `T` нашему коду (читаем).
- **Consumer Super:** наш код через `add(T)` передаёт `T` в `Collection<? super T>` (пишем).

Примеры из JDK, следующие принципу PECS:

```java
// Collections.copy — src производит (extends), dest потребляет (super)
public static <T> void copy(List<? super T> dest, List<? extends T> src) { ... }

// Collection.addAll — параметр производит элементы
boolean addAll(Collection<? extends E> c);

// Collections.sort — Comparator потребляет элементы для сравнения
public static <T> void sort(List<T> list, Comparator<? super T> c) { ... }

// Stream.forEach — Consumer потребляет элементы
void forEach(Consumer<? super T> action);
```

Практический пример:

```java
public static <T> void transfer(
        List<? extends T> source,   // Producer — читаем из неё
        List<? super T> destination // Consumer — пишем в неё
) {
    for (T item : source) {
        destination.add(item);
    }
}

List<Integer> ints = List.of(1, 2, 3);
List<Number> nums = new ArrayList<>();
transfer(ints, nums); // Integer extends Number, Number super Integer
```

**Подводный камень:** если коллекция одновременно и producer, и consumer (и читаем, и пишем), wildcard не подойдёт — используйте точный тип `List<T>` без `?`.

## Q19. (!) Как дженерики работают с наследованием (инвариантность)?

Дженерики в Java **инвариантны**: `List<String>` **не является** подтипом `List<Object>`, хотя `String` — подтип `Object`. Иными словами, наследование элементов не переносится на коллекции.

```java
List<String> strings = new ArrayList<>();
List<Object> objects = strings; // Compilation error!
```

**Почему это запрещено?** Если бы присваивание прошло, можно было бы испортить типобезопасность: через ссылку `List<Object>` положить в список `Integer`, а потом получить `ClassCastException` при чтении через исходную ссылку `List<String>`. Покажем эту дыру:

```java
List<String> strings = new ArrayList<>();
List<Object> objects = strings;    // если бы было можно...
objects.add(42);                   // добавили Integer в List<String>!
String s = strings.get(0);        // ClassCastException!
```

Для гибкости используются **wildcards** (см. [Java Collections](java-collections-interview.md)):

```java
List<String> strings = List.of("a", "b");

List<? extends Object> covariant = strings;     // OK — ковариантность
List<? super String> contravariant = strings;    // OK — контравариантность
```

## Q20. Что такое ковариантность, контравариантность и инвариантность?

Вариантность отвечает на вопрос: если `A` — подтип `B`, как соотносятся обёртки `F<A>` и `F<B>`? (Запись `A <: B` читается «`A` — подтип `B`».) Возможны три варианта:

| Свойство | Описание | Java Generics | Java Arrays |
|----------|----------|---------------|-------------|
| **Ковариантность** | Если `A <: B`, то `F<A> <: F<B>` (направление сохраняется) | `? extends T` | `String[] <: Object[]` |
| **Контравариантность** | Если `A <: B`, то `F<B> <: F<A>` (направление переворачивается) | `? super T` | Нет |
| **Инвариантность** | `F<A>` и `F<B>` не связаны вообще | `List<T>` | — |

Покажем это на конкретной иерархии типов: `Object` → `Number`, а `Number` → `Integer` и `Number` → `Double` (стрелка → читается «супертип → подтип»).

- **Ковариантность (`? extends Number`):** и `List<Integer>`, и `List<Double>` являются подтипами `List<? extends Number>`.
- **Контравариантность (`? super Integer`):** и `List<Number>`, и `List<Object>` являются подтипами `List<? super Integer>`.

**Подводный камень:** массивы в Java ковариантны (в отличие от дженериков), и за это приходится платить runtime-проверкой при каждой записи — а при нарушении прилетает `ArrayStoreException`. Именно из-за этой дыры дженерики сделали инвариантными:

```java
Object[] array = new String[3]; // OK — массивы ковариантны
array[0] = 42; // Компилируется, но ArrayStoreException в runtime!
```

## Q21. (!) Что такое `Raw Type` и зачем избегать?

**Raw Type** — generic-класс, использованный без аргумента типа: `List` вместо `List<String>`. Это не то же самое, что `List<Object>` — raw type отключает generic-проверки целиком, как будто дженериков нет. Существует только ради совместимости с кодом, написанным до Java 5.

```java
// Raw type — НЕ рекомендуется
List rawList = new ArrayList();
rawList.add("hello");
rawList.add(42);               // никаких ошибок компиляции
String s = (String) rawList.get(1); // ClassCastException в runtime!

// Параметризованный тип — рекомендуется
List<String> typedList = new ArrayList<>();
typedList.add("hello");
typedList.add(42);             // Compilation error!
```

**Почему избегать raw types:**
1. Теряется типобезопасность — ошибки обнаруживаются только в runtime
2. Компилятор выдаёт предупреждения `unchecked`
3. Присвоение raw type параметризованному типу отключает проверки: `List<String> list = rawList;` — компилируется с предупреждением, но опасно

**Правило:** если тип не важен, используйте `<?>` вместо raw type: `List<?>` вместо `List`.

## Q22. Если при создании объекта не указан generic-тип, скомпилируется ли код?

Да, скомпилируется — но с **предупреждениями** (unchecked warnings), а не ошибками. Компилятор сознательно не запрещает raw types ради обратной совместимости с кодом до Java 5; предупреждение — его способ сказать «так делать нежелательно»:

```java
List list = new ArrayList();       // raw type — предупреждение
list.add("text");

List<String> typed = list;         // unchecked assignment — предупреждение
String s = typed.get(0);           // работает, но не гарантировано
```

Хотя обратная совместимость и стирание типов позволяют опускать параметры типа, это **плохая практика**. Всегда указывайте параметр типа или хотя бы `<?>`.

## Q23. (!) Что такое `Bridge Methods` и зачем они в дженериках?

**Bridge method** — синтетический (сгенерированный компилятором, отсутствующий в исходнике) метод, который чинит полиморфизм, сломанный стиранием типов. Проблема: после стирания сигнатура из generic-интерфейса и сигнатура вашей реализации перестают совпадать, и `@Override` уже не «цепляется». Bridge-метод восстанавливает совпадение. Разберём на примере.

```java
public interface Comparable<T> {
    int compareTo(T other);
}

public class MyDate implements Comparable<MyDate> {
    @Override
    public int compareTo(MyDate other) { // сигнатура с MyDate
        return this.date.compareTo(other.date);
    }
}
```

После стирания `Comparable<T>` становится `Comparable` с методом `compareTo(Object)`. Но `MyDate` имеет `compareTo(MyDate)` — другая сигнатура! Компилятор генерирует **bridge-метод**:

```java
// Сгенерированный bridge method (synthetic)
public int compareTo(Object other) {
    return compareTo((MyDate) other); // делегирует к реальному методу
}
```

Можно обнаружить bridge-методы через рефлексию:

```java
for (Method m : MyDate.class.getDeclaredMethods()) {
    if (m.isBridge()) {
        System.out.println("Bridge: " + m);
        // Bridge: public int MyDate.compareTo(Object)
    }
}
```

## Q24. Что такое `Heap Pollution`?

**Heap Pollution** (загрязнение кучи) — ситуация, когда переменная параметризованного типа ссылается на объект, который этому типу не соответствует: например, в `List<String>` реально лежит `Integer`. Из-за стирания JVM этого не замечает, и ошибка «всплывает» позже — `ClassCastException` при чтении, причём в месте, далёком от настоящей причины. Возникает при смешивании raw types и generic types.

```java
List<String> strings = new ArrayList<>();
List rawList = strings;           // raw type — unchecked warning
rawList.add(42);                  // Heap pollution! Integer в List<String>

String s = strings.get(0);       // ClassCastException в runtime
```

Heap pollution также возникает при использовании varargs с дженериками:

```java
@SafeVarargs // подавляет предупреждение
static <T> List<T> asList(T... elements) {
    // elements имеет тип T[], но в runtime это Object[]
    // Потенциальное heap pollution
    return Arrays.asList(elements);
}
```

Компилятор предупреждает о потенциальном heap pollution через `unchecked` warnings. Аннотация `@SafeVarargs` допустима только на `final`, `static` или `private` методах, где автор гарантирует безопасность.

## Q25. Как работают дженерики с `varargs` (`T... args`)?

Дженерики и varargs конфликтуют по своей природе. Varargs под капотом — это массив, а массивы должны знать тип элементов в runtime; но `T` стёрт, поэтому компилятор создаёт `Object[]` вместо `T[]`. Отсюда предупреждение о возможном heap pollution на каждом таком методе:

```java
// Предупреждение: Possible heap pollution from parameterized vararg type
static <T> List<T> listOf(T... elements) {
    List<T> list = new ArrayList<>();
    for (T e : elements) {
        list.add(e);
    }
    return list;
}
```

**Когда безопасно** — `@SafeVarargs` подавляет предупреждение:

```java
@SafeVarargs
static <T> List<T> safeListOf(T... elements) {
    return List.of(elements); // массив не утекает наружу
}
```

**Когда опасно** — массив передаётся наружу:

```java
// ОПАСНО — не помечайте @SafeVarargs!
static <T> T[] toArray(T... args) {
    return args; // возвращает Object[] под видом T[]
}

String[] result = toArray("a", "b"); // ClassCastException!
// Object[] нельзя привести к String[]
```

## Q26. Дженерики в статических методах и полях?

Ключевой момент: статика принадлежит классу, а параметр типа — экземпляру. **Статическое поле** не может использовать параметр типа класса, потому что поле одно на все экземпляры, а у `Box<String>` и `Box<Integer>` его тип был бы разным — противоречие:

```java
public class Box<T> {
    private static T value;      // Compilation error!
    private static List<T> list; // Compilation error!
}
```

**Статический метод**, наоборот, может быть generic — но он объявляет собственный параметр типа, не связанный с параметром класса. Этот `T` выводится заново на каждом вызове, поэтому проблемы «одно поле — разные типы» не возникает:

```java
public class Utils {
    // Свой параметр типа T, не связанный с классом
    public static <T> T firstElement(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}
```

## Q27. (!) Можно ли использовать примитивы как generic-тип?

**Нет.** Причина прямо вытекает из стирания: `T` превращается в `Object`, а примитивы (`int`, `double`) не наследуются от `Object` и в `Object`-переменную не помещаются. Поэтому вместо примитивов используют **классы-обёртки** (`Integer`, `Double`), а Java автоматически конвертирует туда-обратно (autoboxing):

```java
List<int> ints = new ArrayList<>();      // Compilation error!
List<Integer> ints = new ArrayList<>();  // OK — autoboxing

Map<String, double> map = new HashMap<>();     // Compilation error!
Map<String, Double> map = new HashMap<>();     // OK
```

**Компромисс — производительность:** autoboxing создаёт объект-обёртку на каждое значение, что увеличивает потребление памяти и нагрузку на GC. В горячих циклах и на больших объёмах данных это заметно. Для таких сценариев используйте специализированные API без боксинга:

```java
// JDK — примитивные стримы без боксинга
IntStream.range(0, 1000).sum();
LongStream.of(1L, 2L, 3L).average();

// Eclipse Collections, Trove — примитивные коллекции
// IntArrayList, LongHashSet и т.д.
```

Подробнее о примитивах и обёртках — в [вопросах по системе типов Java](java-types-interview.md).

## Q28. Можно ли создать массив generic-типа (`new T[]`)?

**Нельзя:** `new T[]` не компилируется. Массив в runtime обязан знать тип своих элементов (он проверяет его при каждой записи), а `T` к этому моменту уже стёрт — JVM просто не знает, массив чего создавать. Есть три обходных пути:

```java
public class Box<T> {
    // private T[] items = new T[10]; // Compilation error!

    // Обходной путь 1 — каст с подавлением предупреждения
    @SuppressWarnings("unchecked")
    private T[] items = (T[]) new Object[10]; // работает, но небезопасно

    // Обходной путь 2 — через Class<T>
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<T> type, int size) {
        return (T[]) java.lang.reflect.Array.newInstance(type, size);
    }

    // Обходной путь 3 (рекомендуемый) — использовать List<T>
    private List<T> items = new ArrayList<>();
}
```

**Рекомендация:** используйте `ArrayList<T>` вместо массивов generic-типов — это и безопаснее, и удобнее.

## Q29. Почему нельзя создать `List<String>[]`?

Тут сталкиваются два несовместимых свойства. Массивы ковариантны и **проверяют** тип элемента в runtime (reifiable). А `List<String>` после стирания — просто `List` (non-reifiable), runtime-проверке его не подвергнуть. Разреши Java создавать `List<String>[]` — массив проверял бы лишь «это `List`?», пропуская подмену `List<Integer>`, и type-safety бы рухнула:

```java
// Если бы было разрешено (не компилируется):
List<String>[] stringLists = new List<String>[1]; // Compilation error!

// Гипотетически, что могло бы пойти не так:
Object[] objects = stringLists;         // массивы ковариантны
objects[0] = List.of(42);              // List<Integer> вместо List<String>
String s = stringLists[0].get(0);      // ClassCastException!
```

**Решение:** используйте `List<List<String>>` вместо массива списков:

```java
List<List<String>> listOfLists = new ArrayList<>(); // безопасно
```

## Q30. Как получить `Class<T>` для generic-типа в runtime?

Из-за стирания `T` сам по себе в runtime недоступен, поэтому информацию о типе нужно «протащить» через какой-то reifiable канал. Три рабочих подхода — от самого надёжного к самому гибкому:

**1. Передать `Class<T>` явно (самый надёжный):** тип приходит как обычное значение-объект `Class`, никакой магии:

```java
public class Repository<T> {
    private final Class<T> entityClass;

    public Repository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T deserialize(String json) {
        return objectMapper.readValue(json, entityClass);
    }
}

var repo = new Repository<>(User.class);
```

**2. Рефлексия через подкласс:** аргумент типа, зашитый в `extends`, сохраняется в метаданных класса (см. Q8) и достаётся рефлексией:

```java
public abstract class AbstractRepository<T> {
    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    protected AbstractRepository() {
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) pt.getActualTypeArguments()[0];
    }
}

// Тип User сохраняется в метаданных UserRepository.class
public class UserRepository extends AbstractRepository<User> { }
```

**3. TypeReference (Jackson) / ParameterizedTypeReference (Spring):** тот же приём из подхода 2, но через анонимный подкласс — позволяет передать даже вложенный тип вроде `List<User>` (см. Q33):

```java
// Jackson
List<User> users = objectMapper.readValue(json,
    new TypeReference<List<User>>() {});

// Spring RestTemplate
List<User> users = restTemplate.exchange(url, HttpMethod.GET, null,
    new ParameterizedTypeReference<List<User>>() {}).getBody();
```

## Q31. Что такое `@SuppressWarnings("unchecked")` и когда использовать?

`@SuppressWarnings("unchecked")` глушит предупреждение компилятора о приведении, корректность которого он проверить не может (из-за стирания типов). Аннотация не делает каст безопасным — она лишь говорит «я, автор, отвечаю за корректность здесь». Поэтому применять её нужно прицельно и с обоснованием.

```java
// Без аннотации — предупреждение компилятора
List<String> list = (List<String>) someObject; // unchecked cast

// С аннотацией — предупреждение подавлено
@SuppressWarnings("unchecked")
List<String> list = (List<String>) someObject;
```

**Когда допустимо использовать:**
- Приведение из raw type при работе с legacy API
- `(T[]) new Object[n]` — внутри generic-класса
- Десериализация с известным типом
- Framework-код (Spring, Jackson) где приведение неизбежно

**Рекомендации:**
1. Применяйте на **минимальной области** — на переменную, не на весь метод или класс
2. **Всегда** добавляйте комментарий, объясняющий безопасность приведения
3. По возможности рефакторьте код, чтобы убрать необходимость в подавлении

```java
@SuppressWarnings("unchecked") // безопасно: deserialize всегда возвращает T для данного Class<T>
T result = (T) deserializer.deserialize(data, targetClass);
```

## Q32. Как сериализовать объекты с generic-полями?

Сериализация (объект → JSON) проблем не вызывает: библиотека видит реальные объекты и их классы. Сложность — в десериализации (JSON → объект): по JSON-строке нельзя восстановить, что внутри `Wrapper` лежал именно `User`, ведь `T` стёрт. Решение — явно передать тип через Super Type Token (Q33).

**Jackson — `TypeReference`:**

```java
public class Wrapper<T> {
    private T data;
    private String metadata;
    // getters, setters
}

// Сериализация — без проблем
String json = objectMapper.writeValueAsString(wrapper);

// Десериализация — нужен TypeReference
Wrapper<User> wrapper = objectMapper.readValue(json,
    new TypeReference<Wrapper<User>>() {});
```

**Gson — `TypeToken`:**

```java
Type type = new TypeToken<Wrapper<User>>() {}.getType();
Wrapper<User> wrapper = gson.fromJson(json, type);
```

**Общий принцип:** и `TypeReference`, и `TypeToken` используют приём Super Type Token (Q33), чтобы сохранить информацию о generic-типе.

## Q33. (!) Что такое `Super Type Token` и как это используется?

**Super Type Token** — приём, позволяющий «протащить» полный generic-тип (включая вложенный, как `Map<String, List<Integer>>`) в runtime в обход стирания. Идея: стирается тип переменной, но не аргумент типа, зашитый в `extends` суперкласса (см. Q8). Создав анонимный подкласс `new TypeReference<...>() {}`, мы фиксируем нужный тип в его сигнатуре и затем достаём рефлексией.

```java
// Абстрактный Super Type Token
public abstract class TypeReference<T> {
    private final Type type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() { return type; }
}

// Использование — создаём анонимный подкласс (фиксирует тип)
TypeReference<Map<String, List<Integer>>> ref = new TypeReference<>() {};
System.out.println(ref.getType());
// java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>
```

**Где используется:**
- **Jackson:** `new TypeReference<List<User>>() {}` для десериализации
- **Spring:** `new ParameterizedTypeReference<List<User>>() {}` в `RestTemplate` и `WebClient`
- **Guava:** `TypeToken<List<String>>` для работы с generic-типами

Паттерн впервые описан Нилом Гафтером (Neal Gafter) и стал стандартным подходом в Java-экосистеме. Подробнее — [Super Type Tokens — Baeldung](https://www.baeldung.com/java-super-type-tokens).

## Q34. Дженерики и `instanceof` — какие проверки допустимы?

`instanceof` работает только с reifiable-типами (теми, что JVM знает в runtime). Поэтому проверка с параметризованным типом (`instanceof List<String>`) **запрещена** — в runtime отличить `List<String>` от `List<Integer>` нечем. Разрешены только формы, информация о которых полностью доступна: raw type, unbounded wildcard и конкретные классы:

```java
Object obj = new ArrayList<String>();

// Нельзя — non-reifiable тип
if (obj instanceof List<String>) { }    // Compilation error!

// Можно — reifiable тип
if (obj instanceof List<?>) { }         // OK
if (obj instanceof List) { }            // OK (raw type, с предупреждением)
if (obj instanceof ArrayList<?>) { }    // OK

// Pattern matching (Java 16+) — аналогично
if (obj instanceof List<?> list) {       // OK
    System.out.println(list.size());
}
```

Если нужно проверить тип элементов, приходится делать это явно:

```java
public static <T> List<T> checkedList(List<?> raw, Class<T> type) {
    List<T> result = new ArrayList<>();
    for (Object item : raw) {
        if (type.isInstance(item)) {
            result.add(type.cast(item));
        }
    }
    return result;
}
// Или использовать Collections.checkedList для runtime-проверок
```

## Q35. Какие изменения в дженериках появились в Java 10+?

Сама модель дженериков (стирание, инвариантность) с Java 5 не менялась. Но вокруг неё добавляли синтаксический сахар и новые конструкции, которые с дженериками сочетаются.

**Java 10 — `var` (вывод типа локальной переменной):**

```java
// Компилятор выводит тип из правой части
var list = new ArrayList<String>();   // тип: ArrayList<String>
var map = Map.of("key", 1);          // тип: Map<String, Integer>
```

`var` не стирает generic-информацию — компилятор выводит полный параметризованный тип. Но `var` нельзя использовать для полей класса, параметров методов и возвращаемых типов.

**Java 17+ — sealed classes с дженериками:**

```java
sealed interface Result<T> permits Success, Failure {
    T value();
}

record Success<T>(T value) implements Result<T> { }
record Failure<T>(Exception error) implements Result<T> {
    public T value() { throw new IllegalStateException(error); }
}
```

**Будущее — Project Valhalla:** в перспективе Java может получить специализированные дженерики для примитивов (без autoboxing), что устранит одно из ключевых ограничений текущей системы дженериков.

## Q36. (!) В чём разница между `? extends T` и `? super T`? Когда что выбирать?

`? extends T` (upper bounded) — «тип `T` или его подтип», из такой коллекции безопасно читать. `? super T` (lower bounded) — «тип `T` или его супертип», в такую коллекцию безопасно писать. Выбор между ними определяет принцип **PECS** (`Producer Extends, Consumer Super`): смотрите, чем коллекция является для вашего метода — источником (читаем → extends) или приёмником (пишем → super).

```java
// ? extends T — из этой коллекции ЧИТАЕМ (producer)
static double sumNumbers(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) sum += n.doubleValue(); // OK — читаем как Number
    // list.add(1.0); // ОШИБКА — нельзя добавить (неизвестен конкретный тип)
    return sum;
}
// Работает с List<Integer>, List<Double>, List<Number> и т.д.

// ? super T — в эту коллекцию ПИШЕМ (consumer)
static void addNumbers(List<? super Integer> list) {
    list.add(1);       // OK — Integer подходит для любого супертипа Integer
    list.add(2);       // OK
    // Integer n = list.get(0); // ОШИБКА — возвращает Object, не Integer
}
// Работает с List<Integer>, List<Number>, List<Object>

// Пример из JDK: Collections.copy
// <T> void copy(List<? super T> dest, List<? extends T> src)
//                     consumer ^            producer ^
List<Number> dest = new ArrayList<>();
List<Integer> src = List.of(1, 2, 3);
Collections.copy(dest, src); // OK — Integer extends Number, Number super Integer
```

**Правило PECS в одном примере:**
```java
// Generic stack с PECS
class Stack<E> {
    // pushAll — src = producer, используем extends
    void pushAll(Iterable<? extends E> src) {
        for (E e : src) push(e);
    }

    // popAll — dst = consumer, используем super
    void popAll(Collection<? super E> dst) {
        while (!isEmpty()) dst.add(pop());
    }
}

Stack<Number> stack = new Stack<>();
stack.pushAll(List.of(1, 2, 3));        // List<Integer> extends Number ✅
Collection<Object> dest = new ArrayList<>();
stack.popAll(dest);                      // Collection<Object> super Number ✅
```

## Q37. (!) Как работает `Type Erasure` на практике и какие проблемы создаёт?

`Type Erasure` — удаление generic-информации при компиляции: каждый параметр типа заменяется на свою верхнюю границу (`Object`, если границы нет, иначе — первый bound). Дальше важны не столько детали процесса, сколько его последствия — что из-за стирания становится невозможным. Их и спрашивают на собеседовании.

```java
// Исходный код
public class Pair<A, B> {
    private A first;
    private B second;
    public Pair(A first, B second) { this.first = first; this.second = second; }
    public A getFirst() { return first; }
}

// После стирания (байткод выглядит как):
public class Pair {
    private Object first;   // A → Object (нет ограничения)
    private Object second;  // B → Object
    public Pair(Object first, Object second) { ... }
    public Object getFirst() { return first; } // + каст на стороне вызывающего кода
}
```

**Практические последствия стирания:**

```java
// 1. Нельзя использовать instanceof с параметрическим типом
List<String> strings = List.of("a");
if (strings instanceof List<String>) { } // ✅ Java 16+ (reified pattern)
// if (x instanceof List<String> ls) { } — ✅ Java 21 sealed+pattern

// 2. Перегрузка по generic не работает — обе сигнатуры одинаковы после стирания
void process(List<String> list) { }
void process(List<Integer> list) { } // ОШИБКА КОМПИЛЯЦИИ — erasure collision!

// 3. Нельзя создать массив generic-типа
T[] arr = new T[10]; // ОШИБКА — обходной путь через рефлексию

// 4. Исключения нельзя параметризовать
// class MyException<T> extends Exception { } // нельзя catch (MyException<String> e)

// Получение типа в runtime — через Class<T>
class TypeSafeContainer<T> {
    private final Class<T> type;
    private T value;

    TypeSafeContainer(Class<T> type) { this.type = type; }

    T getValue(Object raw) {
        return type.cast(raw); // безопасный каст с проверкой
    }
}

TypeSafeContainer<String> c = new TypeSafeContainer<>(String.class);
```

## Q38. Что такое generic-методы и как компилятор выводит тип?

Generic-метод объявляет собственные параметры типа (в `<>` перед возвращаемым типом), независимые от класса-контейнера. На каждом вызове компилятор выводит их заново через **type inference** — по типам аргументов и по целевому типу (тип переменной слева, ожидаемый тип параметра). Поэтому явно писать `<...>` почти никогда не нужно.

```java
// Generic-метод в обычном классе
class Collections {
    // <T> — параметр метода, T extends Comparable — ограничение
    public static <T extends Comparable<T>> T max(List<T> list) {
        return list.stream().max(Comparator.naturalOrder()).orElseThrow();
    }

    // Несколько параметров типа
    public static <K, V> Map<V, K> invertMap(Map<K, V> original) {
        Map<V, K> result = new HashMap<>();
        original.forEach((k, v) -> result.put(v, k));
        return result;
    }
}

// Вызов — тип выводится автоматически
String maxStr = Collections.max(List.of("apple", "mango", "banana")); // T = String
Integer maxInt = Collections.max(List.of(3, 1, 4, 1, 5));              // T = Integer

// Явное указание типа (редко нужно)
List<String> empty = Collections.<String>emptyList();
```

**Type inference (вывод типа):**
```java
// Java 8+ — target type inference
List<String> list = new ArrayList<>(); // T выводится из List<String>

// Java 8 — inference через chain
Map<String, List<Integer>> complex = Stream.of("a", "b")
    .collect(Collectors.toMap(
        s -> s,
        s -> List.of(s.length()) // компилятор выводит List<Integer>
    ));

// Иногда нужна подсказка
List<Object> mixed = Stream.of("str", 42)
    .<Object>map(x -> x) // явный тип для разрешения неоднозначности
    .toList();
```

## Q39. Что такое `Comparable<T>` vs `Comparator<T>` с точки зрения дженериков?

Оба задают порядок сравнения, но с разных сторон. `Comparable<T>` — «внутренний» естественный порядок: класс сам реализует его и знает, как сравнить себя с другим объектом того же типа (`compareTo`). `Comparator<T>` — «внешняя» сменная стратегия: отдельный объект, который сравнивает два чужих объекта (`compare`) и которых для одного типа может быть много. Параметр типа у них тоже значит разное, что и видно в generic-ограничениях ниже.

```java
// Comparable — класс "знает" как себя сравнивать
class Version implements Comparable<Version> {
    private final int major, minor, patch;

    Version(int major, int minor, int patch) {
        this.major = major; this.minor = minor; this.patch = patch;
    }

    @Override
    public int compareTo(Version other) {
        // Comparator.comparingInt используется для цепочки сравнений
        return Comparator.comparingInt((Version v) -> v.major)
            .thenComparingInt(v -> v.minor)
            .thenComparingInt(v -> v.patch)
            .compare(this, other);
    }
}

// Generic-ограничение: T extends Comparable<T> — T умеет сравнивать себя с T
static <T extends Comparable<T>> T min(T a, T b) {
    return a.compareTo(b) <= 0 ? a : b;
}

// Более гибкое ограничение: T extends Comparable<? super T>
// Позволяет использовать Comparable, объявленный в суперклассе
static <T extends Comparable<? super T>> void sort(List<T> list) {
    // ...
}

// Comparator — функциональный интерфейс, поддерживает композицию
Comparator<Version> byMajor = Comparator.comparingInt(v -> v.major);
Comparator<Version> byMinorDesc = Comparator.<Version, Integer>comparing(v -> v.minor)
    .reversed();
Comparator<Version> compound = byMajor.thenComparing(byMinorDesc);

List<Version> versions = new ArrayList<>(List.of(
    new Version(2, 1, 0), new Version(1, 5, 0), new Version(2, 0, 3)
));
versions.sort(compound);
```

**Ключевая разница с generic-точки зрения:**

| Аспект | `Comparable<T>` | `Comparator<T>` |
|---|---|---|
| Тип | Интерфейс в `java.lang` | Функциональный интерфейс в `java.util` |
| Параметр | `T` — тип самого класса | `T` — тип сравниваемых объектов |
| Метод | `int compareTo(T o)` | `int compare(T o1, T o2)` |
| Цепочки | Нет | `thenComparing`, `reversed`, `nullsFirst` |
| Лямбды | Нельзя | Да — `Comparator<String> c = (a,b) -> a.length() - b.length()` |

## Q40. Что такое wildcards (`?`) и когда использовать unbounded `<?>`?

`Unbounded wildcard` `<?>` означает «список чего угодно» (формально — `? extends Object`). Берите его, когда тип элементов вам безразличен:
- метод работает с коллекцией любого типа;
- он трогает только API самого контейнера (`size`, `isEmpty`), а элементы читает максимум как `Object`.

Бонус по сравнению с `List<Object>`: `<?>` принимает `List<String>`, `List<Integer>` и любой другой, тогда как `List<Object>` из-за инвариантности (Q19) — только буквально `List<Object>`.

```java
// Unbounded wildcard — работаем с контейнером, не с содержимым
void printAll(List<?> list) {
    // list.add("x"); // ОШИБКА — тип неизвестен
    for (Object obj : list) { // можно читать как Object
        System.out.println(obj);
    }
}

// Работает с любым List<X>
printAll(List.of(1, 2, 3));       // List<Integer>
printAll(List.of("a", "b"));      // List<String>
printAll(List.of(1.0, 2.0));      // List<Double>

// Полезно для проверки метаданных
static <T> boolean isHomogeneous(List<?> list) {
    if (list.isEmpty()) return true;
    Class<?> type = list.get(0).getClass();
    return list.stream().allMatch(e -> e.getClass() == type);
}

// Когда List<?> лучше List<Object>:
List<Integer> ints = List.of(1, 2, 3);
List<Object> objs = ints; // ОШИБКА — List<Integer> не является List<Object>!
List<?> wild = ints;      // OK — List<Integer> является List<?>
```

**Сравнение трёх форм wildcards:**

```java
// 1. <?> — unbounded: только читать как Object, не добавлять
void print(List<?> list) { list.forEach(System.out::println); }

// 2. <? extends T> — upper bounded: читать как T, не добавлять (Producer Extends)
double sum(List<? extends Number> list) {
    return list.stream().mapToDouble(Number::doubleValue).sum();
}

// 3. <? super T> — lower bounded: добавлять T, читать только как Object (Consumer Super)
void fill(List<? super Integer> list, int count) {
    for (int i = 0; i < count; i++) list.add(i);
}
```

---

## See also

- [Java Collections](java-collections-interview.md) — коллекции активно используют дженерики (`List<T>`, `Map<K,V>`)
- [OOP & Java](java-oop-interview.md) — полиморфизм и наследование в контексте дженериков, ковариантность
- [Java Core](java-core-interview.md) — основы Java, система типов, `Reflection API` и дженерики
- [Java 8+](java-8-interview.md) — улучшения вывода типов в Java 8, `var` в Java 10
- [Система типов Java](java-types-interview.md) — примитивы, обёртки, приведение типов
- [Java Concurrency](java-concurrency-interview.md) — `Future<T>`, `CompletableFuture<T>`, generic thread-safe типы
- [Java Stream API](java-stream-interview.md) — `Stream<T>`, `Collector<T,A,R>` как примеры generic API
- [Java Serialization](java-serialization-interview.md) — сериализация объектов с generic-полями
- [Java 17-21](java-17-21-interview.md) — `sealed interface Result<T>`, паттерн-матчинг с дженериками
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Generic Factory, Repository<T>, паттерны с дженериками

- [Java 17-21](java-17-21-interview.md)
- [Java 8](java-8-interview.md)
- [Java Annotations](java-annotations-interview.md)
- [Java Collections](java-collections-interview.md)
- [Java Concurrency](java-concurrency-interview.md)
- [Java Conditional Statements](java-conditional-statements-interview.md)
