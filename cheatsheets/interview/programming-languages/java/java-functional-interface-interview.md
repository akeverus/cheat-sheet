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
updated: "2026-04-25"
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


> [!mcq]
>
> **Вопрос:** Что определяет функциональный интерфейс в Java и какие методы НЕ нарушают этот контракт?
>
> ---
>
> #### A) Интерфейс, в котором есть только один метод любого вида (включая static и default) — ❌ Неверно
>
> **Что на самом деле:** контракт считает **только абстрактные** методы. `default` и `static` методы можно добавлять без ограничений: `Function<T,R>` имеет `apply()` + кучу default-ов (`andThen`, `compose`, `identity`) и остаётся функциональным интерфейсом.
>
> **Откуда путаница:** мнемоника «один метод» воспринимается буквально, без различения abstract vs default. Но Java 8 как раз ввела default методы чтобы интерфейсы могли расширяться, не ломая SAM-контракт.
>
> **Если бы это было правдой:** `Comparator<T>` (1 abstract + 10+ default + static `reversed`, `thenComparing`, `comparing`) не считался бы функциональным интерфейсом, и `list.sort((a,b) -> ...)` не компилировался бы.
>
> ---
>
> #### B) Интерфейс с ровно одним абстрактным методом (SAM); default и static методы не учитываются и могут быть в любом количестве — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> JLS §9.8: функциональный интерфейс — это интерфейс, у которого ровно **один** неявно или явно объявленный абстрактный метод. Методы `Object` (`equals`, `hashCode`, `toString`) явно объявленные в интерфейсе — НЕ считаются (т.к. они уже реализованы в `Object`). `default` и `static` методы — тоже не считаются.
>
> Именно поэтому `Comparator<T>` (с abstract `compare()` + переопределённым abstract `equals()` из Object + множеством default-ов) — функциональный интерфейс. Компилятор «видит» только один настоящий SAM. Это позволяет писать `Comparator<String> c = String::compareTo` несмотря на десятки методов в интерфейсе.
>
> **Пример:**
> ```java
> @FunctionalInterface
> public interface Transformer<T> {
>     T transform(T input);  // ЕДИНСТВЕННЫЙ абстрактный метод
>
>     default Transformer<T> andThen(Transformer<T> after) {
>         return t -> after.transform(this.transform(t));  // default — OK
>     }
>
>     static <T> Transformer<T> identity() {
>         return t -> t;  // static — OK
>     }
>
>     // Из Object — НЕ считается абстрактным:
>     @Override
>     boolean equals(Object obj);
> }
>
> Transformer<String> upper = String::toUpperCase;  // лямбда работает
> ```
>
> **Когда применять:**
> - Доменные операции с семантическим именем: `Validator<T>`, `Renderer<T>`, `RequestHandler<Req,Resp>`.
> - Когда нужны default-методы для композиции (`and`, `or`, `then`) специфичные для домена.
> - Когда стандартные `Function/Predicate/Consumer` не подходят семантически (читателю кода неясно что значит `Function<Order, Receipt>` без контекста).
>
> **Подводные камни:**
> - Случайно добавленный второй `abstract` метод ломает SAM-статус — `@FunctionalInterface` ловит это compile-time.
> - Generic SAM с разной арностью (например, `R apply(T t)` vs `R apply(T t, U u)`) — это разные интерфейсы, лямбда выбирает по target type.
> - Методы `Object` (`toString`, `equals`, `hashCode`) — даже если переопределены abstract — не учитываются в SAM-подсчёте.
>
> **Связанные вопросы:** [[Q2]] — `@FunctionalInterface` annotation и compile-time проверка; [[Q4]] — стандартные интерфейсы `java.util.function`.
>
> ---
>
> #### C) Интерфейс с одним методом, но без default методов — иначе компилятор не разрешит лямбду — ❌ Неверно
>
> **Что на самом деле:** default методы **разрешены** и не мешают компилятору вывести target type для лямбды. Лямбда матчится по единственному abstract методу — default'ы игнорируются при выводе.
>
> **Откуда путаница:** до Java 8 интерфейсы не имели default-методов вообще; миф «функциональный = одиночный method» унаследован от того времени. На деле default-методы — основной механизм расширения функциональных интерфейсов (вся библиотека `java.util.function` ими пронизана).
>
> **Если бы это было правдой:** `Function.andThen()`, `Predicate.and()`, `Consumer.andThen()` не могли бы существовать в самом интерфейсе — пришлось бы выносить в utility-классы. Java 8 functional API была бы намного беднее.
>
> ---
>
> #### D) Интерфейс с любым числом абстрактных методов, если хотя бы один из них помечен @Lambda — ❌ Неверно
>
> **Что на самом деле:** аннотации `@Lambda` в Java нет. Лямбда — это синтаксис implementation для **единственного** abstract метода в SAM-интерфейсе. Если abstract методов больше одного, компилятор не знает какой реализовывать.
>
> **Откуда путаница:** возможно смешение с Scala/Kotlin где есть SAM-conversion для Java-интерфейсов. Но даже там работает тот же принцип «один abstract метод».
>
> **Если бы это было правдой:** `interface Foo { void a(); void b(); }` можно было бы реализовать как `Foo f = () -> {...}` — но какой метод тут реализован? Компилятор просто не примет такой код.

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


> [!mcq]
>
> **Вопрос:** Что произойдёт при попытке скомпилировать интерфейс с `@FunctionalInterface` и двумя абстрактными методами?
>
> ---
>
> #### A) Compile error: `Multiple non-overriding abstract methods found in interface` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `@FunctionalInterface` — это **проверяемая компилятором** аннотация (как `@Override`). Если на интерфейсе с этой аннотацией есть 2+ abstract метода, javac выдаст ошибку компиляции на этапе сборки модуля. Без аннотации тот же интерфейс просто потеряет статус «функциональный» — но компиляция пройдёт. Аннотация фиксирует намерение и защищает от случайной поломки SAM-контракта при будущих правках.
>
> Это инструмент **fail-fast**: ошибка ловится в моменте написания кода, а не потом когда кто-то попытается передать лямбду в API ожидающее этот интерфейс.
>
> **Пример:**
> ```java
> // Скомпилируется БЕЗ аннотации (просто перестаёт быть functional):
> public interface NotQuiteSAM {
>     void first();
>     void second();  // OK без @FunctionalInterface
> }
> // Но Runnable r = () -> ... — невозможно: 2 abstract метода
>
> // НЕ скомпилируется С аннотацией:
> @FunctionalInterface
> public interface BadInterface {
>     void first();
>     void second();
>     // javac: Multiple non-overriding abstract methods found in interface BadInterface
> }
> ```
>
> **Когда применять:**
> - Любой интерфейс предназначенный для лямбд/method references — должен иметь `@FunctionalInterface`.
> - Доменные SAM-интерфейсы (`Validator`, `Transformer`, `RetryStrategy`) — особенно важно: документирует намерение для читателя кода.
> - НЕ нужно ставить на интерфейсы которые случайно оказались SAM, но не предназначены для лямбд (например, marker-like API).
>
> **Подводные камни:**
> - Аннотация не обязательна технически — интерфейс с одним abstract методом всё равно работает с лямбдой. Но без неё рефакторинг (добавление второго метода) ломает API без compile-error.
> - Методы из `Object` (`toString`, `equals`, `hashCode`) если объявлены abstract — НЕ считаются. `Comparator<T>` имеет abstract `equals(Object)` но остаётся functional interface.
> - Generic SAM с разной арностью считается как один метод (если bound одинаковый). Но `void foo(T t)` и `void foo(T t1, T t2)` — это 2 разных метода, ошибка.
>
> **Связанные вопросы:** [[Q1]] — определение SAM; [[Q14]] — кастомные функциональные интерфейсы.
>
> ---
>
> #### B) Compiler warning, но компиляция успешна; ошибка возникнет только в runtime при вызове лямбды — ❌ Неверно
>
> **Что на самом деле:** это compile **error**, а не warning. Сборка модуля упадёт с ненулевым exit code, JAR не соберётся. Runtime сюда не доходит — кода нет.
>
> **Откуда путаница:** некоторые аннотации (`@SuppressWarnings`, `@Deprecated`) дают только warning. Но `@FunctionalInterface` и `@Override` — обе error-уровня.
>
> **Если бы это было правдой:** разработчик мог бы накопить «битые» функциональные интерфейсы в codebase, и узнал бы об этом только когда кто-то попытается их использовать как лямбда-target. Сейчас это ловится сразу в CI.
>
> ---
>
> #### C) Аннотация работает только для документации в Javadoc и не проверяется компилятором — ❌ Неверно
>
> **Что на самом деле:** `@FunctionalInterface` имеет `@Retention(RUNTIME)` и **проверяется компилятором** (JLS §9.8). Это не просто маркер для людей, а активная compile-time валидация.
>
> **Откуда путаница:** некоторые аннотации действительно только для людей/IDE (`@NotNull` без поддерживающего processor'а). Но javac знает `@FunctionalInterface` нативно.
>
> **Если бы это было правдой:** не было бы смысла её ставить вообще — IDE-подсветка достаточна для документации. Реальная польза именно в compile-time guard'е против регрессий.
>
> ---
>
> #### D) Java автоматически удалит второй абстрактный метод и оставит первый объявленный — ❌ Неверно
>
> **Что на самом деле:** Java никогда не «удаляет» код. Если контракт нарушен — компиляция падает. Молчаливая модификация AST была бы катастрофой для отладки.
>
> **Откуда путаница:** возможно мысль о том как Kotlin/Scala иногда «магически» делают SAM-conversion. Но даже там нет «удаления» — есть выбор конкретного метода для лямбда-таргета по правилам.
>
> **Если бы это было правдой:** разработчик не контролировал бы что именно реализует его лямбда. Поведение в production было бы непредсказуемым.

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


> [!mcq]
>
> **Вопрос:** Что выведет код `Runnable r = () -> System.out.println(this);` вызванный из метода класса `OuterClass`?
>
> ---
>
> #### A) Анонимный объект Runnable@xxxx — лямбда захватывает свой собственный this — ❌ Неверно
>
> **Что на самом деле:** в лямбде `this` ссылается на **enclosing instance** (внешний класс `OuterClass`), а не на сам лямбда-объект. Лямбда не имеет «себя» как объекта в обычном смысле — она компилируется в синтетический invokedynamic call.
>
> **Откуда путаница:** в анонимных классах `this` действительно указывает на сам анонимный объект — и эта семантика часто переносится на лямбды по инерции.
>
> **Если бы это было правдой:** нельзя было бы вызвать `this.someMethod()` из лямбды для метода внешнего класса. На деле это самая частая операция в коде с лямбдами.
>
> ---
>
> #### B) `OuterClass@<hashCode>` — `this` в лямбде указывает на внешний экземпляр класса; лямбда не создаёт нового объекта — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Лямбда — это **синтаксическое выражение**, а не отдельный объект как в случае анонимного класса. JVM компилирует её в bootstrap метод через `invokedynamic` и `LambdaMetafactory`, кэширует созданный объект (если возможно) и переиспользует. Лексический scope лямбды **полностью прозрачен** — `this`, `super`, переменные внешнего метода доступны без квалификации.
>
> Анонимный класс наоборот: каждый `new Runnable() {...}` создаёт новый объект класса `OuterClass$1`, и `this` внутри ссылается на этот объект, а доступ к внешнему — через `OuterClass.this`.
>
> **Пример:**
> ```java
> public class OuterClass {
>     public void demo() {
>         // Лямбда: this = OuterClass
>         Runnable r1 = () -> System.out.println(this);
>         // Печатает: OuterClass@1b6d3586
>
>         // Анонимный класс: this = анонимный объект
>         Runnable r2 = new Runnable() {
>             @Override
>             public void run() {
>                 System.out.println(this);            // OuterClass$1@xxx
>                 System.out.println(OuterClass.this); // OuterClass@1b6d3586
>             }
>         };
>
>         r1.run();
>         r2.run();
>     }
> }
> ```
>
> **Когда применять (лямбды vs анонимные классы):**
> - **Лямбда**: 1 метод SAM-интерфейса, нет собственного состояния, нет нужды в `this` на «себя». 99% случаев в современном Java коде.
> - **Анонимный класс**: нужно реализовать несколько методов (non-SAM), нужны поля, нужна сериализация, нужен явный `this` на сам объект (например, регистрация listener'а который сам себя deregister'ит).
>
> **Подводные камни:**
> - **Lambda capture мутабельных полей** — `this.field` можно мутировать из лямбды (поле в heap), но локальные переменные должны быть effectively final. Источник тонких багов.
> - **Memory leak через capture**: лямбда из метода объекта удерживает `this` неявно. Если эту лямбду сохранить в long-lived registry (event bus), весь outer объект не GC'нется.
> - **Сериализация лямбд**: возможна через `Serializable` cast (`(Runnable & Serializable) () -> ...`), но JLS не гарантирует кросс-JVM-совместимости — внутренняя структура lambda-class зависит от компилятора.
> - **Debugging**: stack trace показывает имена вида `OuterClass.lambda$demo$0` — не сразу понятно где код.
>
> **Связанные вопросы:** [[Q12]] — effectively final и capture; [[Q1]] — SAM как target для лямбды.
>
> ---
>
> #### C) NullPointerException — `this` undefined в лямбде, так как она исполняется в отдельном контексте — ❌ Неверно
>
> **Что на самом деле:** лямбда **захватывает** окружающий контекст лексически, включая `this`. Никакого «отдельного контекста» нет, это не closure-аллокация с null-references.
>
> **Откуда путаница:** возможно ассоциация с JavaScript где `this` зависит от call-site и часто бывает `undefined`. Java семантика принципиально другая — лексический scope гарантирован спецификацией.
>
> **Если бы это было правдой:** все API типа `forEach(x -> this.process(x))` падали бы с NPE. На практике это базовый идиоматичный код.
>
> ---
>
> #### D) Compile error: лямбда не может ссылаться на `this` — ❌ Неверно
>
> **Что на самом деле:** компилируется без проблем. JLS явно разрешает использовать `this` внутри лямбды, и его значение определено — это enclosing instance.
>
> **Откуда путаница:** возможно путают с правилами для static-контекста (там `this` действительно недоступен). Если лямбда написана в static методе — да, `this` нет. Но в instance методе — есть.
>
> **Если бы это было правдой:** Streams API с methods like `.map(s -> this.transform(s))` не работал бы. Это core-паттерн Java 8+.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Чем Consumer отличается от Supplier? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Чем Function отличается от UnaryOperator? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Чем Runnable отличается от Callable и Supplier? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. (!) Какие 4 вида method references существуют? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) Как работают compose() и andThen() в Function? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как работают and(), or(), negate() в Predicate? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Что такое примитивные специализации функциональных интерфейсов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что значит effectively final для переменных в лямбде? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Можно ли выбросить checked exception из лямбды? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как создать собственный функциональный интерфейс? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Java 8](java-8-interview.md) — лямбды, Stream API, Optional как нововведения Java 8 ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Java Stream API](java-stream-interview.md) — Function, Predicate, Consumer в Stream
- [Java Optional](java-optional-interview.md) — Supplier в orElseGet, Consumer в ifPresent
- [Java Concurrency](java-concurrency-interview.md) — Callable, Runnable в многопоточности
- [Java Core](java-core-interview.md) — интерфейсы, default методы Java 8
- [Java Generics](java-generics-interview.md) — типизация функциональных интерфейсов
- [Java 17-21](java-17-21-interview.md) — новые возможности, records с функциональными интерфейсами
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Strategy pattern через функциональные интерфейсы
