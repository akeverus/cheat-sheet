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
updated: "2026-05-14"
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
> #### B) Интерфейс с любым числом абстрактных методов, если хотя бы один из них помечен @Lambda — ❌ Неверно
>
> **Что на самом деле:** аннотации `@Lambda` в Java нет. Лямбда — это синтаксис implementation для **единственного** abstract метода в SAM-интерфейсе. Если abstract методов больше одного, компилятор не знает какой реализовывать.
>
> **Откуда путаница:** возможно смешение с Scala/Kotlin где есть SAM-conversion для Java-интерфейсов. Но даже там работает тот же принцип «один abstract метод».
>
> **Если бы это было правдой:** `interface Foo { void a(); void b(); }` можно было бы реализовать как `Foo f = () -> {...}` — но какой метод тут реализован? Компилятор просто не примет такой код.
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
> #### D) Интерфейс с ровно одним абстрактным методом (SAM); default и static методы не учитываются и могут быть в любом количестве — ✓ Верно
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
> #### B) NullPointerException — `this` undefined в лямбде, так как она исполняется в отдельном контексте — ❌ Неверно
>
> **Что на самом деле:** лямбда **захватывает** окружающий контекст лексически, включая `this`. Никакого «отдельного контекста» нет, это не closure-аллокация с null-references.
>
> **Откуда путаница:** возможно ассоциация с JavaScript где `this` зависит от call-site и часто бывает `undefined`. Java семантика принципиально другая — лексический scope гарантирован спецификацией.
>
> **Если бы это было правдой:** все API типа `forEach(x -> this.process(x))` падали бы с NPE. На практике это базовый идиоматичный код.
>
> ---
>
> #### C) `OuterClass@<hashCode>` — `this` в лямбде указывает на внешний экземпляр класса; лямбда не создаёт нового объекта — ✓ Верно
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
>
> **Вопрос:** Какой стандартный интерфейс `java.util.function` подходит для `Stream.filter()`, ожидающего проверку «строка длиннее 5 символов»?
>
> ---
>
> #### A) `Function<String, Boolean>` — функция, возвращающая Boolean — ❌ Неверно
>
> **Что на самом деле:** `Stream.filter()` принимает `Predicate<T>`, а не `Function<T, Boolean>`. Это разные интерфейсы: `Predicate.test()` возвращает примитивный `boolean` (без boxing), а `Function<String, Boolean>` — обёрнутый `Boolean` (с автобоксингом). Сигнатура `filter` требует `Predicate`, ваш `Function` просто не скомпилируется.
>
> **Откуда путаница:** `Function<T, R>` действительно универсальный — кажется что любая «T → R» функция подходит. Но Java выделила `Predicate<T>` отдельно именно ради примитивного boolean и удобной композиции (`and`, `or`, `negate`).
>
> **Если бы это было правдой:** filter принимал бы любую функцию возвращающую `Boolean` — но при этом терял бы `Predicate.negate()`, `and()`, `or()` и платил boxing на каждом элементе stream'а.
>
> ---
>
> #### B) `Consumer<String>` — потребитель строки — ❌ Неверно
>
> **Что на самом деле:** `Consumer<T>` имеет сигнатуру `void accept(T t)` — НИЧЕГО не возвращает. Filter же нуждается в `true/false` для каждого элемента. Consumer подходит для `forEach`, логирования, побочных эффектов — но НЕ для предикатов.
>
> **Откуда путаница:** разработчик может смешивать «принимает T» (это и Consumer, и Predicate) с «принимает T и решает фильтровать». Различает их именно возвращаемое значение.
>
> **Если бы это было правдой:** `stream.filter(consumer)` не имел бы способа узнать «оставить элемент или нет» — нет возвращаемого значения. Filter принципиально не может работать с Consumer.
>
> ---
>
> #### C) `Predicate<String>` с методом `test(String): boolean` — специально создан для условий и интегрирован с `Stream.filter()` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `Predicate<T>` — функциональный интерфейс **специально под условные проверки**: `boolean test(T t)`. `Stream.filter()`, `Collection.removeIf()`, `Optional.filter()` — все принимают именно его. Преимущества vs `Function<T, Boolean>`:
>
> 1. **Примитивный boolean** — нет boxing на каждом элементе (важно на больших stream'ах).
> 2. **Композиция**: `.and(other)`, `.or(other)`, `.negate()`, `Predicate.not(p)` (Java 11+) — встроенные default-методы.
> 3. **Семантическая ясность** — сигнатура метода `filter(Predicate<? super T>)` сразу говорит читателю «фильтр», а не «произвольная трансформация».
>
> **Пример:**
> ```java
> Predicate<String> longerThan5 = s -> s.length() > 5;
>
> List<String> result = Stream.of("Java", "Kotlin", "Go", "Scala", "Haskell")
>     .filter(longerThan5)
>     .collect(Collectors.toList());
> // ["Kotlin", "Haskell"]
>
> // Композиция:
> Predicate<String> startsWithJ = s -> s.startsWith("J");
> Predicate<String> longAndJ = longerThan5.and(startsWithJ);
>
> // Java 11+: Predicate.not
> List<String> nonEmpty = stream
>     .filter(Predicate.not(String::isEmpty))
>     .toList();
> ```
>
> **Когда применять:**
> - Любая операция «оставить/отбросить» в Stream API.
> - Валидация: `Validator<T>` часто строится поверх `Predicate<T>`.
> - Условные удаления: `list.removeIf(predicate)`, `map.entrySet().removeIf(...)`.
> - Optional.filter: `optional.filter(predicate).map(...)`.
>
> **Подводные камни:**
> - `Predicate<T>` vs `BiPredicate<T,U>` — для двух аргументов нужна 2-arity версия (`Map.forEach((k,v) -> ...)` с BiConsumer, не Consumer).
> - **Примитивные специализации** (`IntPredicate`, `LongPredicate`, `DoublePredicate`) — для `IntStream`/`LongStream`/`DoubleStream`. Использование `Predicate<Integer>` с IntStream вызывает boxing.
> - **Negate vs not**: `p.negate()` (instance метод) и `Predicate.not(p)` (static, Java 11+) делают одно и то же, но `not` читается лучше с method references: `Predicate.not(String::isEmpty)`.
>
> **Связанные вопросы:** [[Q4]] — основные интерфейсы; [[Q10]] — and/or/negate композиция; [[Q11]] — IntPredicate и другие специализации.
>
> ---
>
> #### D) `BiPredicate<String, Integer>` — двухаргументный предикат — ❌ Неверно
>
> **Что на самом деле:** `BiPredicate<T,U>` — для случаев когда нужно проверить пару значений (например, ключ+значение в Map). Здесь же мы фильтруем `Stream<String>` — один аргумент на элемент. `BiPredicate` сюда не подойдёт по сигнатуре.
>
> **Откуда путаница:** «длиннее 5» может казаться двухаргументным условием (строка + число 5). Но 5 — это **захваченная константа** внутри лямбды, не отдельный параметр интерфейса.
>
> **Если бы это было правдой:** `Stream.filter(BiPredicate)` не существует в API. Компилятор отверг бы такое использование.

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


> [!mcq]
>
> **Вопрос:** Где правильно использовать `Supplier<T>` вместо `Consumer<T>` в стандартной Java библиотеке?
>
> ---
>
> #### A) `list.forEach(supplier)` — для каждого элемента применить supplier — ❌ Неверно
>
> **Что на самом деле:** `Collection.forEach()` принимает `Consumer<T>`, а не Supplier. forEach отдаёт каждый элемент в Consumer — это поток значений в side-effect. Supplier же ничего не принимает, только производит — нечего ему «передать» от forEach.
>
> **Откуда путаница:** название «forEach» может ассоциироваться с «производством действий». Но семантически forEach — поглотитель элементов, не источник.
>
> **Если бы это было правдой:** `list.forEach(() -> "hi")` — supplier не получал бы ни одного элемента списка. Был бы бессмысленным «for each X do something unrelated».
>
> ---
>
> #### B) `Map.merge(key, value, supplier)` — для слияния значений по ключу — ❌ Неверно
>
> **Что на самом деле:** `Map.merge` принимает `BiFunction<V, V, V>` (старое значение + новое → результат), а не Supplier. Supplier нигде в Map API не используется как BiFunction.
>
> **Откуда путаница:** есть метод `computeIfAbsent(key, mappingFunction)` где mappingFunction — это `Function<K, V>`, и его иногда путают со «снабжением» значения. Но это всё ещё Function (зависит от ключа), не Supplier.
>
> **Если бы это было правдой:** `map.merge` не получал бы информацию о существующем значении — а это его основная семантика. Был бы бесполезным.
>
> ---
>
> #### C) `executor.submit(supplier)` — отправить задачу на выполнение в пуле потоков — ❌ Неверно
>
> **Что на самом деле:** `ExecutorService.submit()` принимает `Runnable` или `Callable<T>`, но НЕ `Supplier<T>`. Если у вас Supplier и вы хотите его asynchronously запустить — оборачивайте: `CompletableFuture.supplyAsync(supplier, executor)`.
>
> **Откуда путаница:** и Supplier, и Callable — «ноль аргументов → T». Семантически близки. Но Supplier — для синхронного lazy compute (не бросает checked exceptions), Callable — для async задач (бросает Exception).
>
> **Если бы это было правдой:** `executor.submit(() -> 42)` мог бы выбираться компилятором как Supplier — но реально это всегда Callable<Integer>. Перегрузка `submit` Supplier'а нет.
>
> ---
>
> #### D) `optional.orElseGet(supplier)` — ленивое вычисление default-значения только если Optional пуст — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `Optional.orElseGet(Supplier<? extends T>)` — классическое использование Supplier: **отложенное** вычисление значения по требованию. Если Optional содержит значение — supplier не вызывается; если пуст — вызывается `get()` и результат становится значением.
>
> Это принципиально отличается от `orElse(T defaultValue)`, который **всегда** вычисляет default (eagerly), даже если Optional не пуст. Supplier даёт **lazy evaluation** — критично если default дорогой (запрос в БД, генерация UUID, чтение файла).
>
> **Пример:**
> ```java
> // Плохо: orElse — всегда вызывается expensiveFetch()
> User user = userOpt.orElse(expensiveFetch());  // вызовется даже если userOpt непустой!
>
> // Хорошо: orElseGet — вызывается только если userOpt.isEmpty()
> User user = userOpt.orElseGet(() -> expensiveFetch());
> User user2 = userOpt.orElseGet(this::expensiveFetch);  // method ref
>
> // Другие места Supplier:
> Logger log = LoggerFactory.getLogger(MyClass.class);
> log.debug(() -> "Heavy computation: " + complexToString());  // SLF4J 2.x Supplier
>
> // Java 9+: Stream.generate
> Stream<UUID> ids = Stream.generate(UUID::randomUUID).limit(10);
>
> // Стандартный фабричный паттерн:
> Supplier<List<String>> listFactory = ArrayList::new;
> List<String> a = listFactory.get();
> List<String> b = listFactory.get();  // независимые экземпляры
> ```
>
> **Когда применять:**
> - **Lazy default values** (`orElseGet`, `requireNonNullElseGet`) — когда вычисление default'а дорогое или имеет side-effects.
> - **Lazy logging** — SLF4J 2.x методы вида `log.debug(Supplier)` вычисляют сообщение только если уровень включён.
> - **Stream.generate** — бесконечные stream'ы (`Stream.generate(() -> readNext())`).
> - **Фабрики** — `ArrayList::new`, `() -> new HashMap<>()` как фабрика контейнеров для `Collector.toMap`.
> - **Memoization wrapper'ы** — Suppliers.memoize в Guava.
>
> **Подводные камни:**
> - **Supplier vs Callable**: `Supplier.get()` НЕ может бросать checked exceptions, `Callable.call() throws Exception` может. Для I/O лучше Callable.
> - **Race condition в supplier**: если supplier шарится между threads и имеет side-effect (например, инкремент счётчика) — нужна синхронизация.
> - **`orElseGet` с side-effect** — каждый вызов `orElseGet` повторит вычисление; если supplier дорогой, кешируй вне.
> - **Method reference vs lambda**: `LoggerFactory::getLogger` НЕ supplier (метод требует аргумент-класс). Нужно `() -> LoggerFactory.getLogger(...)`.
>
> **Связанные вопросы:** [[Q7]] — Supplier vs Callable vs Runnable; [[Q4]] — все стандартные интерфейсы; [[Q13]] — checked exceptions в лямбдах.

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


> [!mcq]
>
> **Вопрос:** Какой метод `List<T>` принимает именно `UnaryOperator<T>`, а не `Function<T, T>`?
>
> ---
>
> #### A) `list.map(operator)` — преобразовать все элементы — ❌ Неверно
>
> **Что на самом деле:** у `List<T>` **нет** метода `map()`. Это путаница со `Stream.map(Function<T, R>)`. На самом Stream'е метод `map` принимает Function (не UnaryOperator), потому что Stream позволяет менять тип элементов (`Stream<String>` → `Stream<Integer>`).
>
> **Откуда путаница:** в коллекциях многих языков (Kotlin, JS) есть `.map()` напрямую. В Java идёт через Stream API.
>
> **Если бы это было правдой:** наш ответ был бы сразу неверен — у List нет такого метода, компилятор отверг бы код.
>
> ---
>
> #### B) `list.forEach(operator)` — применить операцию к каждому элементу — ❌ Неверно
>
> **Что на самом деле:** `Collection.forEach` принимает `Consumer<T>`, не UnaryOperator. forEach — для побочных эффектов, без возвращаемого значения.
>
> **Откуда путаница:** UnaryOperator тоже «применяет операцию к T», но возвращает T. forEach не интересуется возвращаемым значением.
>
> **Если бы это было правдой:** результат операции (новое T) выбрасывался бы — UnaryOperator имел бы смысл Consumer'а. Это противоречит дизайну.
>
> ---
>
> #### C) `list.replaceAll(operator)` — заменить каждый элемент результатом применения оператора к нему — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `List.replaceAll(UnaryOperator<E> op)` — единственный стандартный API метод где UnaryOperator принципиально нужен **вместо** Function. Семантика: элемент типа `E` заменяется новым значением **того же типа** `E`. Если бы метод принимал `Function<E, R>`, можно было бы случайно вернуть несовместимый тип и сломать invariant List'а.
>
> Это **type-safety через интерфейс**: `UnaryOperator<E> extends Function<E, E>` — сужение типа специально для in-place операций. Любой Function<E,E> совместим с UnaryOperator<E> (через приведение или lambda), но никак не наоборот.
>
> **Пример:**
> ```java
> List<String> names = new ArrayList<>(List.of("  Alice ", "Bob ", " Charlie"));
>
> // replaceAll — UnaryOperator<String>:
> names.replaceAll(String::trim);
> // ["Alice", "Bob", "Charlie"]
>
> // А вот это не скомпилируется:
> // names.replaceAll(String::length);  // ERROR: String::length is Function<String, Integer>
>
> // Map.replaceAll — аналогично, BiFunction<K, V, V> (новое V должно быть V):
> Map<String, Integer> scores = new HashMap<>(Map.of("a", 1, "b", 2));
> scores.replaceAll((k, v) -> v * 10);  // {a=10, b=20}
>
> // BinaryOperator<T> в Stream.reduce:
> int sum = IntStream.of(1, 2, 3, 4).reduce(0, Integer::sum);
> // Integer::sum — BinaryOperator<Integer>, частный случай BiFunction<Integer, Integer, Integer>
> ```
>
> **Когда применять (UnaryOperator vs Function):**
> - **UnaryOperator<T>** — in-place преобразование коллекции (`List.replaceAll`), endo-функции в монаде (state transformer), идентичность (`UnaryOperator.identity()`).
> - **Function<T, T>** — когда хотите явно подчеркнуть «трансформация может вернуть любой тип, но здесь совпадает». В API design лучше UnaryOperator для строгой семантики.
> - **BinaryOperator<T>** — для reduce и aggregation: `(T, T) -> T` обеспечивает associative свойство.
>
> **Подводные камни:**
> - **List.replaceAll mutates the list** — это in-place операция, оригинальный список меняется. Если нужна копия — `list.stream().map(op).toList()`.
> - **UnaryOperator.identity()** — `UnaryOperator<T> id = UnaryOperator.identity()` возвращает функцию `x -> x`. Полезно как default или в Stream.collect.
> - **Boxing для примитивов**: используйте `IntUnaryOperator` для `int`, `LongUnaryOperator` для `long`. `UnaryOperator<Integer>` будет boxing на каждом элементе.
> - **Concurrent modification**: `replaceAll` на ArrayList безопасен; на `CopyOnWriteArrayList` создаёт новую копию; на стандартных immutable List'ах (`List.of()`) бросает UnsupportedOperationException.
>
> **Связанные вопросы:** [[Q4]] — все стандартные интерфейсы; [[Q11]] — примитивные специализации; [[Q9]] — andThen/compose.
>
> ---
>
> #### D) `list.sort(operator)` — сортировка с использованием UnaryOperator — ❌ Неверно
>
> **Что на самом деле:** `List.sort(Comparator<? super E> c)` принимает Comparator, а не UnaryOperator. Comparator — это `(T, T) -> int`, тогда как UnaryOperator — `T -> T`. Семантически разные операции: сравнение vs трансформация.
>
> **Откуда путаница:** оба интерфейса работают с одним типом T. Но Comparator возвращает порядок, UnaryOperator — новое значение.
>
> **Если бы это было правдой:** sort не имел бы способа узнать порядок элементов — он получал бы «преобразованный» элемент, но не знал «больше/меньше».

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


> [!mcq]
>
> **Вопрос:** В чём ключевая разница между `Callable<T>` и `Supplier<T>`, если оба не имеют входных параметров и возвращают T?
>
> ---
>
> #### A) Callable работает только в `ExecutorService`, Supplier — везде; функционально идентичны — ❌ Неверно
>
> **Что на самом деле:** `Callable.call() throws Exception` — может бросать **checked exceptions**, `Supplier.get()` — нет. Это принципиальная разница: при работе с I/O, JDBC, network — Callable не требует try/catch внутри, Supplier — требует обернуть в `try { ... } catch (...) { throw new RuntimeException(); }`.
>
> **Откуда путаница:** оба «производят значение без входа». Кажется что Callable — просто Supplier для ExecutorService.
>
> **Если бы это было правдой:** не было бы смысла иметь два интерфейса. На деле они существуют именно ради разделения checked-семантики.
>
> ---
>
> #### B) Supplier всегда возвращает immutable, Callable может возвращать mutable — ❌ Неверно
>
> **Что на самом деле:** ни один из интерфейсов не накладывает ограничений на mutability возвращаемого значения. `Supplier<List<String>>` может возвращать ArrayList (mutable). `Callable<String>` — immutable String. Это никак не связано с дизайном интерфейсов.
>
> **Откуда путаница:** возможно ассоциация с functional programming где «pure functions» возвращают immutable. Но Java не enforce'ит этого.
>
> **Если бы это было правдой:** `Supplier<ArrayList<String>> factory = ArrayList::new` не компилировался бы — но это идиоматичный код.
>
> ---
>
> #### C) `Callable.call() throws Exception` (поддерживает checked exceptions), `Supplier.get()` — не бросает checked; Callable в `java.util.concurrent`, Supplier в `java.util.function` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Три отличия:
>
> 1. **Checked exceptions**: сигнатура `T call() throws Exception` позволяет бросать любые exception без обёртки. `T get()` — только unchecked. Это критично для I/O кода: `Callable<byte[]> r = () -> Files.readAllBytes(path)` — компилируется. `Supplier<byte[]> r = () -> Files.readAllBytes(path)` — НЕ компилируется.
>
> 2. **Пакет/назначение**: `Callable<T>` в `java.util.concurrent` — для async задач в ExecutorService, ScheduledExecutorService, CompletableFuture. `Supplier<T>` в `java.util.function` — для lazy values, фабрик, default-providers (Optional, Stream.generate, requireNonNullElseGet).
>
> 3. **API integration**: `ExecutorService.submit(Callable)` → `Future<T>`. `CompletableFuture.supplyAsync(Supplier)` — обратите внимание, supplyAsync принимает Supplier, не Callable. Если ваша операция бросает checked exception — нужно обернуть в RuntimeException или использовать `completableFuture.handle()`.
>
> **Пример:**
> ```java
> // Callable: I/O с checked exception без try/catch внутри
> Callable<String> reader = () -> Files.readString(Path.of("/etc/hosts"));
> Future<String> future = executor.submit(reader);
> try {
>     String content = future.get();  // checked exceptions перепакованы в ExecutionException
> } catch (ExecutionException | InterruptedException e) { ... }
>
> // Supplier: тот же I/O требует обёртки
> Supplier<String> badReader = () -> Files.readString(Path.of("/etc/hosts"));
> // COMPILE ERROR: unhandled IOException
>
> Supplier<String> wrappedReader = () -> {
>     try {
>         return Files.readString(Path.of("/etc/hosts"));
>     } catch (IOException e) {
>         throw new UncheckedIOException(e);
>     }
> };
>
> // Supplier в Optional:
> User u = userOpt.orElseGet(() -> userRepo.findDefault());
>
> // Конвертация Callable → Supplier:
> Supplier<String> sup = () -> {
>     try { return reader.call(); }
>     catch (Exception e) { throw new RuntimeException(e); }
> };
> ```
>
> **Когда применять:**
> - **Callable**: async tasks с checked exceptions (file I/O, JDBC, RestTemplate), задачи в ExecutorService, ScheduledExecutorService.
> - **Supplier**: lazy values (`Optional.orElseGet`, SLF4J `log.debug(() -> ...)`), фабрики (`ArrayList::new`), Stream.generate, default providers.
> - **Runnable**: fire-and-forget без возврата (Thread, scheduling без результата).
>
> **Подводные камни:**
> - `executor.submit(() -> 42)` — компилятор выбирает Callable<Integer>, а не Supplier. Если хочется Future<Void> для fire-and-forget — используйте `submit(Runnable)`.
> - **CompletionStage + checked**: `CompletableFuture.supplyAsync(supplier)` НЕ принимает Callable. Для checked exceptions либо оборачивайте, либо используйте custom executor.
> - **Lambda capture в Callable**: captured-переменные те же effectively-final правила, как в любой лямбде.
> - **ScheduledExecutorService.schedule(Runnable/Callable, ...)** — обе перегрузки, выбор по target type.
>
> **Связанные вопросы:** [[Q4]] — все стандартные интерфейсы; [[Q5]] — Supplier vs Consumer; [[Q13]] — checked exceptions в лямбдах.
>
> ---
>
> #### D) Runnable идентичен Callable<Void> — оба void и без аргументов — ❌ Неверно
>
> **Что на самом деле:** `Runnable.run()` — `void`, **не бросает** checked exceptions. `Callable<Void>.call() throws Exception` — формально возвращает `Void` (тип-обёртка, обычно `return null`), **может** бросать checked. Это разные интерфейсы и не взаимозаменяемы.
>
> **Откуда путаница:** оба «не возвращают полезное значение». Но Callable<Void> ещё и может throws Exception.
>
> **Если бы это было правдой:** не было бы перегрузок `executor.submit(Runnable)` vs `executor.submit(Callable<T>)`. Компилятор различает их по сигнатуре. Также `Executors.callable(Runnable)` — adapter, который как раз делает Callable<Object> из Runnable, явно показывая что это разные типы.

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


> [!mcq]
>
> **Вопрос:** Какой тип method reference у выражения `String::toUpperCase` когда оно присваивается переменной `Function<String, String> upper`?
>
> ---
>
> #### A) Reference на метод экземпляра на типе (unbound) — receiver предоставляется как первый аргумент Function — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Это **unbound receiver** form: `ClassName::instanceMethod`. Тип лямбды требует `Function<String, String>` — функция от String к String. Компилятор разворачивает `String::toUpperCase` в `s -> s.toUpperCase()`: первый аргумент Function становится receiver'ом instance метода.
>
> Это отличается от **bound receiver** (`prefix::concat` где `prefix` — конкретный объект). Различить помогает контекст: если слева class name + справа instance method → unbound; если слева instance + справа method → bound.
>
> 4 вида method references в Java:
> 1. **Static**: `Integer::parseInt` → `s -> Integer.parseInt(s)`.
> 2. **Bound** (на конкретном объекте): `prefix::concat` → `s -> prefix.concat(s)`.
> 3. **Unbound** (instance method на типе): `String::toUpperCase` → `s -> s.toUpperCase()`.
> 4. **Constructor**: `ArrayList::new` → `() -> new ArrayList<>()` или `n -> new ArrayList<>(n)`.
>
> **Пример:**
> ```java
> // 1. Static
> Function<String, Integer> parser = Integer::parseInt;
> parser.apply("42");  // 42
>
> // 2. Bound (конкретный объект до ::)
> String prefix = "Hello: ";
> Function<String, String> greet = prefix::concat;
> greet.apply("World");  // "Hello: World"
>
> // 3. Unbound (тип до ::)
> Function<String, String> upper = String::toUpperCase;
> upper.apply("hi");  // "HI"
>
> // Двухаргументная unbound (compareTo как Comparator):
> Comparator<String> cmp = String::compareTo;
> // эквивалентно (s1, s2) -> s1.compareTo(s2)
>
> // 4. Constructor
> Supplier<ArrayList<String>> factory = ArrayList::new;
> Function<Integer, ArrayList<String>> withCap = ArrayList::new;
>
> // Array constructor:
> IntFunction<String[]> arrayMaker = String[]::new;
> String[] arr = arrayMaker.apply(10);  // new String[10]
> ```
>
> **Когда применять:**
> - **Stream API**: `.map(String::trim)`, `.filter(s -> !s.isEmpty())`, `.collect(Collectors.toList())` — method references компактнее лямбд.
> - **Comparators**: `Comparator.comparing(User::getName)`, `.thenComparing(User::getAge)`.
> - **Builder/Factory patterns**: `Stream.generate(UUID::randomUUID)`, `() -> new ArrayList<>()` лучше как `ArrayList::new`.
> - **Method references читаются лучше** лямбд когда логика — это просто вызов одного метода без преобразований.
>
> **Подводные камни:**
> - **Overloaded methods** — компилятор может не выбрать нужную перегрузку. `System.out::println` неоднозначно (есть `println(String)`, `println(int)`, ...). Target type помогает: `Consumer<String> c = System.out::println` ОК.
> - **Generic methods**: `List<String>::add` не работает напрямую — нужно `(list, item) -> list.add(item)` или `BiConsumer<List<String>, String> c = List::add`.
> - **NPE риск**: `bound::method` если bound = null — NPE сразу при создании method reference, не при вызове. `Function<String, Integer> f = (null)::length` — NullPointerException на присваивании.
> - **Refactoring**: если переименовать метод, method reference сразу ломается на compile-time (это плюс). Лямбда же продолжает компилироваться даже если поведение поменялось.
>
> **Связанные вопросы:** [[Q1]] — SAM как target для method reference; [[Q3]] — лямбда vs анонимный класс; [[Q4]] — стандартные интерфейсы.
>
> ---
>
> #### B) Reference на статический метод — `String.toUpperCase` это static-метод класса — ❌ Неверно
>
> **Что на самом деле:** `toUpperCase()` — instance method (вызывается на конкретной String), а не static. Static-методы у String: `valueOf`, `format`, `join`, `copyValueOf`. Static method reference выглядит так же синтаксически (`ClassName::method`), но семантика разная.
>
> **Откуда путаница:** синтаксис `ClassName::method` идентичен для static и для unbound instance reference. Различить помогает только знание API: открыть Javadoc → `public String toUpperCase()` (instance) vs `public static String valueOf(int i)` (static).
>
> **Если бы это было правдой:** компилятор интерпретировал бы как `s -> String.toUpperCase(s)`, что не существует — static метода с таким именем нет.
>
> ---
>
> #### C) Bound receiver reference — `String` это конкретный объект, на который привязан метод — ❌ Неверно
>
> **Что на самом деле:** `String` — это **класс**, а не конкретный объект-инстанс. Bound reference требует именно объект-instance: `"hello"::toUpperCase` — bound (привязан к строке "hello"); `String::toUpperCase` — unbound (любая String получит этот метод).
>
> **Откуда путаница:** в bound и unbound одинаковый синтаксис `Receiver::method`. Различает только что стоит слева — class (unbound) или instance (bound).
>
> **Если бы это было правдой:** результат был бы `Supplier<String>` (zero-arg → String), а не `Function<String, String>`. Сигнатуры разные.
>
> ---
>
> #### D) Constructor reference — каждый вызов создаёт новый String — ❌ Неверно
>
> **Что на самом деле:** constructor reference имеет синтаксис `ClassName::new`. `String::toUpperCase` — это вызов instance-метода `toUpperCase()`, а не конструктор. String конструкторы выглядели бы как `String::new` (что компилируется как Function для нескольких overloads).
>
> **Откуда путаница:** возможно ассоциация с тем что `toUpperCase()` создаёт новую String (true, но это side-effect метода, не конструктора).
>
> **Если бы это было правдой:** `Function<String, String> upper = String::toUpperCase` интерпретировалось бы как `s -> new String(s)`, и `upper.apply("hi")` возвращало бы "hi", а не "HI". Логически неверно.

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


> [!mcq]
>
> **Вопрос:** Что вернёт `Function<Integer, Integer> f = times2.compose(plus3); f.apply(5);` где `times2 = x -> x*2` и `plus3 = x -> x+3`?
>
> ---
>
> #### A) 13, так как `compose` применяет функции слева направо: сначала times2(5)=10, потом plus3(10)=13 — ❌ Неверно
>
> **Что на самом деле:** это поведение `andThen`, а не `compose`. `compose` идёт **справа налево** относительно записи: `times2.compose(plus3).apply(5)` = `times2(plus3(5))` = `times2(8)` = `16`. Левый в записи (`times2`) выполняется **последним**.
>
> **Откуда путаница:** интуитивно «сначала this, потом other» — но именно так работает `andThen`. `compose` инвертирует порядок.
>
> **Если бы это было правдой:** `compose` и `andThen` делали бы одно и то же — не было бы смысла иметь оба метода. Они существуют именно ради разного порядка.
>
> ---
>
> #### B) 16, так как `compose` применяет аргумент сначала к other (plus3), потом к this (times2): `times2(plus3(5))` = `times2(8)` = 16 — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Java следует математическому определению композиции из теории функций: `(f ∘ g)(x) = f(g(x))`. То есть `f.compose(g)` = «применить g, потом f» — справа налево. Если `f = times2` и `g = plus3`, то `times2.compose(plus3) (5)` = `times2(plus3(5))` = `times2(5+3)` = `times2(8)` = `16`.
>
> `andThen` — это обратный порядок, как pipeline: `f.andThen(g)` = `g(f(x))` = «сначала f, потом g». Для `times2.andThen(plus3).apply(5)` = `plus3(times2(5))` = `plus3(10)` = `13`.
>
> Мнемоника:
> - `f.andThen(g)` — «f, а **затем** g» (как читается на английском: f and then g).
> - `f.compose(g)` — «f, **компонуется с** g, где g идёт первым» (g «вкладывается внутрь» f, как `f(g(x))`).
>
> **Пример:**
> ```java
> Function<Integer, Integer> times2 = x -> x * 2;
> Function<Integer, Integer> plus3 = x -> x + 3;
>
> // compose: g applied first, then f
> Function<Integer, Integer> c = times2.compose(plus3);
> c.apply(5);  // times2(plus3(5)) = times2(8) = 16
>
> // andThen: f applied first, then g
> Function<Integer, Integer> a = times2.andThen(plus3);
> a.apply(5);  // plus3(times2(5)) = plus3(10) = 13
>
> // Практический pipeline (andThen более популярен):
> Function<String, String> trim = String::trim;
> Function<String, String> upper = String::toUpperCase;
> Function<String, Integer> length = String::length;
>
> Function<String, Integer> pipeline = trim.andThen(upper).andThen(length);
> pipeline.apply("  hello  ");  // 5 (trim → "hello", upper → "HELLO", length → 5)
>
> // compose чаще используется в математических контекстах:
> Function<Integer, Double> sqrt = Math::sqrt;
> Function<Double, Double> abs = Math::abs;
> Function<Integer, Double> sqrtOfAbs = sqrt.compose(i -> (double) Math.abs(i));
> ```
>
> **Когда применять:**
> - **andThen** — практически всегда для pipelines (data transformation: trim → validate → save). Читается естественно: «сначала это, затем то».
> - **compose** — когда хочется подчеркнуть математическую композицию или когда «inner function» уже задана а нужно её обернуть.
> - **Consumer.andThen** — только andThen (нет возвращаемого значения, compose невозможна).
> - **Predicate** не имеет ни compose ни andThen — только `and()`, `or()`, `negate()` (тоже композиция, но булевая).
>
> **Подводные камни:**
> - **`null` argument** — `f.andThen(null)` бросает NullPointerException **сразу** (не отложенно). Проверка в default-методе.
> - **Type compatibility**: `Function<T, R> f`, `Function<R, V> g` — `f.andThen(g)` возвращает `Function<T, V>`. Цепочка строится по типам.
> - **Generic capture bug**: иногда type inference не справляется, нужно явно указать generic параметр: `Function.<String>identity().andThen(s -> s.length())`.
> - **Side effects в Function**: если одна из функций имеет side-effects (логирование, мутация), порядок может стать критичным. Compose vs andThen — это не только результат, но и порядок side-effects.
> - **Производительность**: каждый `andThen/compose` создаёт новый Function-объект (через invokedynamic + LambdaMetafactory). На горячем пути в Stream это незаметно (JIT inline), но в micro-benchmarks разница есть.
>
> **Связанные вопросы:** [[Q4]] — Function/BiFunction; [[Q10]] — and/or/negate в Predicate; [[Q14]] — кастомные функциональные интерфейсы с композицией.
>
> ---
>
> #### C) 26, так как `compose` создаёт новую функцию выполняющую обе подфункции параллельно и суммирующую результаты — ❌ Неверно
>
> **Что на самом деле:** в Java нет «параллельной композиции функций» в стандартной библиотеке. `compose` — синхронная sequential операция. Параллельность достигается через `CompletableFuture.allOf` или Stream.parallel.
>
> **Откуда путаница:** возможно ассоциация с reactive streams (Flux.combineLatest), но это совсем другой API.
>
> **Если бы это было правдой:** функциональная композиция стала бы конструктом параллелизма — но это противоречит её определению.
>
> ---
>
> #### D) Compile error — Function не имеет метода compose() — ❌ Неверно
>
> **Что на самом деле:** `Function<T, R>` имеет **default метод** `compose(Function<? super V, ? extends T> before)` (а также `andThen`, `identity`). Все три — standard Java 8+ API.
>
> **Откуда путаница:** возможно путают с Consumer (у которого только `andThen`, нет `compose`).
>
> **Если бы это было правдой:** Stream API и Functional API стали бы намного беднее. compose доступен с Java 8, поведение специфицировано в JLS и Javadoc.

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


> [!mcq]
>
> **Вопрос:** Имеют ли методы `and()`, `or()` в `Predicate<T>` поведение **short-circuit** (не вычислять правый операнд если результат уже определён)?
>
> ---
>
> #### A) Нет, всегда вычисляются оба предиката — это последовательное логическое сложение/умножение — ❌ Неверно
>
> **Что на самом деле:** `Predicate.and()` и `Predicate.or()` точно повторяют семантику `&&` и `||`: **short-circuit** evaluation. В `p.and(q)`: если `p.test(x)` вернул `false` — `q.test(x)` НЕ вызывается. В `p.or(q)`: если `p.test(x)` вернул `true` — `q.test(x)` НЕ вызывается.
>
> **Откуда путаница:** возможно ассоциация с `&` и `|` (без short-circuit, побитовые/eager логические). Но Java `Predicate` использует именно `&&` и `||` в default-реализациях.
>
> **Если бы это было правдой:** дорогие предикаты в `.and(...)` всегда вычислялись бы — это убило бы производительность. Реально это критично для фильтров типа `cheapCheck.and(expensiveDbLookup)`.
>
> ---
>
> #### B) Да, `and` и `or` используют `&&`/`||` в default-методах: для `and` правый не вычисляется если левый false; для `or` — если левый true — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Реализация в JDK (`java.util.function.Predicate`):
> ```java
> default Predicate<T> and(Predicate<? super T> other) {
>     Objects.requireNonNull(other);
>     return (t) -> test(t) && other.test(t);  // && — short-circuit
> }
> default Predicate<T> or(Predicate<? super T> other) {
>     Objects.requireNonNull(other);
>     return (t) -> test(t) || other.test(t);  // || — short-circuit
> }
> default Predicate<T> negate() {
>     return (t) -> !test(t);
> }
> ```
>
> Это позволяет ставить **дешёвые** предикаты первыми, **дорогие** — последними:
> ```java
> Predicate<User> isActive = u -> u.isActive();          // O(1)
> Predicate<User> hasOrders = u -> orderDb.hasOrders(u); // DB query
>
> // Правильный порядок — дешёвый первый:
> users.stream().filter(isActive.and(hasOrders))
>     // Если user не active, hasOrders НЕ вызывается → no DB query
>
> // Неправильный — дорогой первый:
> users.stream().filter(hasOrders.and(isActive))
>     // Каждый user проходит DB query, даже неактивные
> ```
>
> **Пример:**
> ```java
> Predicate<String> notEmpty = s -> !s.isEmpty();
> Predicate<String> startsWithA = s -> s.startsWith("A");
> Predicate<String> longEnough = s -> s.length() > 5;
>
> // Композиция:
> Predicate<String> valid = notEmpty.and(startsWithA).and(longEnough);
> valid.test("Alice");    // notEmpty=true → startsWithA=true → longEnough=false → false
> valid.test("");         // notEmpty=false → остальные НЕ вычисляются → false
>
> // negate:
> Predicate<String> isEmpty = notEmpty.negate();
> isEmpty.test("");       // true
>
> // Java 11+: Predicate.not (static), удобнее с method references:
> List<String> nonEmpty = list.stream()
>     .filter(Predicate.not(String::isEmpty))
>     .collect(Collectors.toList());
>
> // Цепочка ассоциативна:
> Predicate<User> filter = isAdult.and(hasEmail).and(isVerified).or(isAdmin);
> // Эквивалентно: ((isAdult AND hasEmail) AND isVerified) OR isAdmin
> ```
>
> **Когда применять:**
> - **Stream filter** с несколькими условиями: чище чем длинная лямбда.
> - **Бизнес-валидация**: композиция «правил» через `.and()` создаёт читаемые цепочки.
> - **Performance optimization**: помещайте быстрые предикаты первыми — short-circuit спасает циклы CPU.
> - **`Predicate.not()`** (Java 11+) вместо `.negate()` если хочется static-метод: `.filter(Predicate.not(String::isBlank))`.
>
> **Подводные камни:**
> - **NullPointerException на null аргументе**: `p.and(null)` бросает NPE сразу (через `requireNonNull` в default-методе).
> - **Side effects в predicate'е**: если predicate имеет побочные эффекты (логирование!), short-circuit меняет наблюдаемое поведение. Логирование пропусков может «исчезать».
> - **Ассоциативность**: `p1.and(p2).or(p3)` это `(p1 AND p2) OR p3`, не `p1 AND (p2 OR p3)`. Скобки в лямбде важны: `.and(p2.or(p3))` явно меняет порядок.
> - **Type variance**: `Predicate.and(Predicate<? super T>)` — можно компонировать с predicate более общего типа. Это позволяет `Predicate<Number>` + `Predicate<Object>` → `Predicate<Number>`.
>
> **Связанные вопросы:** [[Q9]] — compose/andThen в Function; [[Q4]] — Predicate среди стандартных интерфейсов; [[Q11]] — IntPredicate/LongPredicate специализации.
>
> ---
>
> #### C) `and()` — short-circuit, `or()` — нет (всегда вычисляет оба для логической полноты) — ❌ Неверно
>
> **Что на самом деле:** оба метода используют short-circuit. `or` короткозамыкается на `true`, `and` — на `false`. Это симметрично и одинаково для всех булевых операторов в Java.
>
> **Откуда путаница:** возможно неверная интуиция «OR должен проверить оба варианта чтобы быть честным». Но и `&&`, и `||` short-circuit с самого Java 1.0.
>
> **Если бы это было правдой:** `nullCheck.or(callOnObject)` в Java не сработал бы как защита от NPE: вызвался бы `callOnObject` даже если `nullCheck=true`. На деле классический pattern `x == null || x.isValid()` работает именно потому что `||` short-circuits.
>
> ---
>
> #### D) `and` и `or` поддерживают short-circuit только для primitive specializations (`IntPredicate.and`); generic `Predicate` — eager — ❌ Неверно
>
> **Что на самом деле:** реализация одинакова для `Predicate<T>` и `IntPredicate`/`LongPredicate`/`DoublePredicate`. Все используют `&&`/`||` в default-методе. Источник — Java SE Javadoc/исходник OpenJDK.
>
> **Откуда путаница:** иногда кажется что примитивные специализации имеют другие правила (быстрее, JIT-friendly). Но семантика идентична.
>
> **Если бы это было правдой:** оптимизации производительности зависели бы от типа Predicate, что было бы аномалией в API дизайне.

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


> [!mcq]
>
> **Вопрос:** Почему в hot-path коде с `IntStream` лучше использовать `IntUnaryOperator` вместо `Function<Integer, Integer>`?
>
> ---
>
> #### A) `Function<Integer, Integer>` создаёт boxing/unboxing на каждом вызове: int → Integer → int — это аллокации и GC pressure; `IntUnaryOperator` работает с примитивами напрямую — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `Function<T, R>` — generic интерфейс с `R apply(T t)`. Generic'и в Java реализованы через **type erasure**: на runtime это `Function<Object, Object>`, оперирующая ссылками. Для `int` это значит autoboxing: каждый `apply(intValue)` создаёт `Integer.valueOf(intValue)` (или достаёт из кеша для маленьких значений), затем результат `Integer` распаковывается обратно через `intValue()`.
>
> `IntUnaryOperator` — специализация: `int applyAsInt(int operand)`. Никакого boxing, JVM работает с примитивами напрямую — это эффективно сразу и даёт JIT больше возможностей для inline'инга и SIMD-оптимизаций.
>
> На бенчмарках разница для tight loops может быть 3-10× по latency и значительная GC pressure для большого объёма данных (миллионы элементов).
>
> **Пример:**
> ```java
> // Плохо: boxing на каждом элементе
> Function<Integer, Integer> squareBoxed = x -> x * x;
> int sum = IntStream.range(0, 1_000_000)
>     .boxed()                              // int → Integer (1M аллокаций)
>     .map(squareBoxed)                     // Integer.intValue + boxing back
>     .mapToInt(Integer::intValue)          // ещё unboxing
>     .sum();
>
> // Хорошо: no boxing
> IntUnaryOperator square = x -> x * x;
> int sumFast = IntStream.range(0, 1_000_000)
>     .map(square)                          // примитивы → примитивы
>     .sum();
>
> // Семейство примитивных специализаций:
> IntPredicate evens = x -> x % 2 == 0;
> IntConsumer printer = System.out::println;        // void accept(int)
> IntSupplier random = () -> ThreadLocalRandom.current().nextInt(100);
> IntFunction<String> toHex = Integer::toHexString; // int → R (boxing для R только если R = тип-обёртка)
> ToIntFunction<String> length = String::length;    // T → int
> IntToLongFunction widen = x -> (long) x * 1000;
> IntBinaryOperator add = Integer::sum;             // BiFunction<int, int, int>
> ```
>
> **Когда применять:**
> - **IntStream/LongStream/DoubleStream** — все методы принимают примитивные специализации (`.map(IntUnaryOperator)`, `.filter(IntPredicate)`).
> - **Численные вычисления** на больших массивах — финансовые расчёты, ML, image processing.
> - **Hot loops** с миллионами итераций — boxing убивает throughput.
> - **API design**: если ваш интерфейс работает с числами — лучше предоставить примитивную версию рядом с generic.
>
> **Подводные камни:**
> - **Integer cache**: для значений -128..127 `Integer.valueOf(i)` возвращает кэшированные объекты — boxing для них дешевле. Но всё равно есть overhead vs прямой примитив.
> - **Только Int/Long/Double**: остальные примитивы (`byte`, `short`, `float`, `char`, `boolean`) специализаций НЕ имеют. Для `byte` используют int-специализации с приведением.
> - **API limitations**: `IntStream.flatMap` принимает `IntFunction<? extends IntStream>` — нет «BiIntFunction», некоторые комбинации недоступны.
> - **Generic совместимость**: `IntFunction<R>` (int → R) и `ToIntFunction<T>` (T → int) — разные интерфейсы. Не путайте.
> - **Composition**: `IntUnaryOperator.andThen(IntUnaryOperator)` есть, но composition между разными специализациями (например, `IntUnaryOperator` + `LongUnaryOperator`) — сложнее, иногда нужны явные приведения.
>
> **Связанные вопросы:** [[Q4]] — generic функциональные интерфейсы; [[Q5]] — Consumer vs Supplier; [[Q9]] — composition.
>
> ---
>
> #### B) `IntUnaryOperator` быстрее потому что использует SIMD-инструкции автоматически — ❌ Неверно
>
> **Что на самом деле:** SIMD-оптимизации (auto-vectorization) — это работа JIT-компилятора (C2/Graal) на bytecode-уровне. Они применяются к любому коду, удовлетворяющему условиям (отсутствие данных-зависимостей, простые операции). `IntUnaryOperator` не «использует SIMD автоматически» — но **позволяет JIT** легче применять SIMD из-за отсутствия boxing.
>
> **Откуда путаница:** связь «примитивы → SIMD» есть, но не прямая. SIMD появляется при удачных условиях, не гарантирован.
>
> **Если бы это было правдой:** все коды на примитивах в Java были бы безусловно SIMD'нуты — реально это редкая оптимизация, требующая Vector API (preview).
>
> ---
>
> #### C) Java запрещает использовать `Function<Integer, Integer>` в IntStream — это compile error — ❌ Неверно
>
> **Что на самом деле:** `Function<Integer, Integer>` можно использовать в Stream<Integer>, но НЕ в IntStream (последний требует именно `IntUnaryOperator`). Это не «запрет», а несовместимость сигнатур. И через `.boxed()` можно перейти от IntStream к Stream<Integer>.
>
> **Откуда путаница:** ограничение есть, но это просто API design, не запрет.
>
> **Если бы это было правдой:** `IntStream.range(0,10).boxed().map(Function<Integer,Integer>)` не работал бы — но это идиоматичный паттерн.
>
> ---
>
> #### D) `IntUnaryOperator` использует меньше памяти под объект-лямбду (16 байт vs 32 байт у Function) — ❌ Неверно
>
> **Что на самом деле:** объект лямбды одинакового размера (определяется LambdaMetafactory и invokedynamic, не generic параметрами). Экономия — в **отсутствии аллокаций Integer-объектов на каждый apply()**, а не в размере самой лямбды.
>
> **Откуда путаница:** интуитивно «специализированный = меньше» — но это про другую часть. Главное — устранение boxing-аллокаций на горячем пути.
>
> **Если бы это было правдой:** разница была бы фиксированной (16 байт), независимо от объёма данных. Реальная разница пропорциональна числу элементов (1M элементов → 1M аллокаций boxing).

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


> [!mcq]
>
> **Вопрос:** Почему лямбда может читать поле `this.counter` и инкрементировать его, но не может изменить локальную переменную `int n = 0`?
>
> ---
>
> #### A) Лямбды вообще не могут изменять состояние — это чисто функциональный паттерн без mutation — ❌ Неверно
>
> **Что на самом деле:** лямбды **могут** изменять состояние через поля объекта (heap), через массивы (`int[] arr = {0}; lambda → arr[0]++`), через AtomicInteger и другие изменяемые контейнеры. Запрет касается только локальных переменных (стек-фрейма).
>
> **Откуда путаница:** функциональный стиль действительно поощряет immutability, но Java лямбды не enforce этого.
>
> **Если бы это было правдой:** `forEach(x -> total[0] += x)` не работал бы — но это легитимный приём.
>
> ---
>
> #### B) Это требование Hibernate/JPA — они не могут сериализовать мутабельные captured переменные — ❌ Неверно
>
> **Что на самом деле:** ограничение в Java spec, не связано с Hibernate. Действует одинаково в любом коде, даже без JPA/persistence.
>
> **Откуда путаница:** JPA имеет свои ограничения на lazy fetching и proxy, но эти ограничения совершенно не пересекаются с лямбдами.
>
> **Если бы это было правдой:** лямбды в Standalone Spring-Boot-приложении без JPA вели бы себя по-другому. Это легко проверить — поведение идентично.
>
> ---
>
> #### C) Это историческое ограничение из Java 8, отменённое в Java 17 — теперь можно изменять любые переменные — ❌ Неверно
>
> **Что на самом деле:** правило effectively final действует и в Java 21. Это **спецификация языка** (JLS §15.27.2), а не временное ограничение. Никаких изменений за все версии Java не было.
>
> **Откуда путаница:** Java постоянно эволюционирует (records, sealed, pattern matching). Кажется, что любое ограничение когда-то снимут.
>
> **Если бы это было правдой:** Java 17/21 код мог бы менять captured local, но он бы вёл себя «как Kotlin var capture» — а такого нет.
>
> ---
>
> #### D) Локальные переменные хранятся в стеке метода и могут «уйти» до выполнения лямбды; capture делается **by-value** — захватывается копия значения. Поля объекта в heap и доступны через ссылку — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Когда метод возвращает управление, его **stack frame** удаляется. Если лямбда захватила локальную переменную и сохранилась где-то долгоиграющем (например, в коллекции listener'ов), стек переменной уже нет — обращение по адресу было бы UB. Чтобы это работало, JVM **копирует** значение локальной переменной внутрь лямбда-объекта при создании.
>
> Поскольку это копия, изменение «оригинала» не было бы видно лямбде, а изменение копии не было бы видно вне лямбды. Чтобы избежать confusing semantics, Java **запрещает изменение** локальной переменной если она captured лямбдой — она должна быть `final` или **effectively final** (де-факто не меняется).
>
> Поля объекта (`this.counter`) — другая история. Они в **heap**, доступны через ссылку `this`. Лямбда захватывает `this` (тоже by-value, но это всего лишь ссылка), и через эту ссылку можно мутировать поле. Это **не thread-safe** (нужны `volatile`/`AtomicInteger`/lock'и), но компилируется.
>
> **Пример:**
> ```java
> class Counter {
>     int field = 0;
>
>     void demo() {
>         int local = 0;
>
>         // ОК: чтение local
>         Runnable r1 = () -> System.out.println(local);
>
>         // ОШИБКА КОМПИЛЯЦИИ: local больше не effectively final
>         // local++;  // если раскомментить — лямбда выше не скомпилируется
>
>         // ОК: мутируем поле (через this)
>         Runnable r2 = () -> field++;  // компилируется, но не thread-safe
>         r2.run();
>         System.out.println(field);  // 1
>
>         // Workaround для мутации «локального» состояния:
>         int[] holder = {0};  // массив — effectively final ссылка
>         Runnable r3 = () -> holder[0]++;  // компилируется
>         r3.run();
>         System.out.println(holder[0]);  // 1
>
>         AtomicInteger atomic = new AtomicInteger(0);  // thread-safe вариант
>         Runnable r4 = () -> atomic.incrementAndGet();
>     }
> }
> ```
>
> **Когда применять:**
> - **Запрет мутации локальных** — чаще всего благо: предотвращает race conditions в multithreading.
> - **AtomicInteger/Atomic*** — правильный способ счётчиков в параллельных лямбдах (`stream.parallel().forEach(x -> count.incrementAndGet())`).
> - **`int[1]` holder hack** — для однопоточного кода, когда нужен mutable «refer cell». Менее идиоматично, но иногда самый простой workaround.
> - **Stream API alternatives**: вместо мутирующего forEach часто лучше `.reduce()`, `.collect()`, `.count()` — функциональные альтернативы без mutation.
>
> **Подводные камни:**
> - **Effectively final ≠ полная иммутабельность**: переменная-ссылка может быть «effectively final», но объект по ссылке мутируется. `final List<String> list = new ArrayList<>(); list.add(...)` — OK, лямбда видит изменения.
> - **Race conditions через holder[]**: `int[] counter = {0}; stream.parallel().forEach(x -> counter[0]++)` — НЕ thread-safe, потеряете инкременты. Нужен AtomicInteger или `.count()`.
> - **Capture огромных объектов**: если лямбда захватывает `this`, удерживает весь outer объект. Memory leak'и через event listener'ы.
> - **Java vs Kotlin**: Kotlin позволяет `var` capture в inline-функциях; Java НЕТ. Это конструктивное отличие, не баг.
>
> **Связанные вопросы:** [[Q3]] — лямбда vs анонимный класс (capture); [[Q1]] — SAM target type; [[Q13]] — checked exceptions в лямбдах.

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


> [!mcq]
>
> **Вопрос:** Какой подход к работе с `Files.readAllBytes(path)` в `Function<String, byte[]>` корректен и идиоматичен?
>
> ---
>
> #### A) Прямо передать `Files::readAllBytes` — Java автоматически оборачивает IOException — ❌ Неверно
>
> **Что на самом деле:** `Function.apply()` не объявляет `throws IOException` — следовательно `Files::readAllBytes` (бросающий IOException) **не совместим** с `Function<String, byte[]>` на уровне типов. Compile error: `unhandled exception: java.io.IOException`. Никакой автоматической обёртки нет.
>
> **Откуда путаница:** хочется верить, что Java «магически» обработает checked exceptions. Но это противоречит дизайну функциональных интерфейсов.
>
> **Если бы это было правдой:** не нужны были бы UncheckedIOException, ThrowingFunction-обёртки и Throwables.propagate из Guava — все они существуют именно из-за этой проблемы.
>
> ---
>
> #### B) Обернуть в try/catch внутри лямбды, бросая `UncheckedIOException` (или RuntimeException) — стандартный idiom Java для интеграции I/O с Stream API — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Поскольку стандартные функциональные интерфейсы (`Function`, `Predicate`, `Consumer`, `Supplier`) не объявляют `throws`, любой checked exception нужно либо **перепаковать в unchecked**, либо использовать **кастомный** функциональный интерфейс с `throws`.
>
> Idiomatic подход — wrap в `UncheckedIOException` (для IOException) или `RuntimeException` (для общих). `UncheckedIOException` появился в Java 8 специально для этого: сохраняет исходный IOException как cause, позволяя getCause() извлечь оригинал в catch-блоке выше по стеку.
>
> **Пример:**
> ```java
> // Idiomatic: unchecked wrapping
> Function<String, byte[]> reader = path -> {
>     try {
>         return Files.readAllBytes(Path.of(path));
>     } catch (IOException e) {
>         throw new UncheckedIOException(e);  // preserves original
>     }
> };
>
> // Использование в Stream:
> Map<String, byte[]> data = paths.stream()
>     .collect(Collectors.toMap(p -> p, reader));
> // если IOException — UncheckedIOException пропагируется
>
> // Кастомный throwing-интерфейс (другой путь):
> @FunctionalInterface
> public interface ThrowingFunction<T, R, E extends Exception> {
>     R apply(T t) throws E;
>
>     default Function<T, R> unchecked() {
>         return t -> {
>             try { return apply(t); }
>             catch (Exception e) {
>                 throw new RuntimeException(e);
>             }
>         };
>     }
> }
>
> ThrowingFunction<String, byte[], IOException> tf = path -> Files.readAllBytes(Path.of(path));
> Function<String, byte[]> wrapped = tf.unchecked();  // адаптер
>
> // Утилита-обёртка (популярная):
> Function<String, byte[]> sneaky = Unchecked.function(Files::readAllBytes);
> // jOOL/Vavr/lombok @SneakyThrows предоставляют такие хелперы
> ```
>
> **Когда применять:**
> - **UncheckedIOException** — для I/O в Java 8+. Семантически прозрачно для читателя кода.
> - **RuntimeException(e)** — для произвольных checked. Менее точно, но универсально.
> - **Custom ThrowingFunction** — если нужно сохранять checked-семантику в API. Например, в публичных библиотеках.
> - **try/catch внутри stream'а** — приемлемо для одиночных edge cases, но если повторяется — лучше вынести wrapper.
>
> **Подводные камни:**
> - **Информация о exception теряется**: `RuntimeException(e)` ОК для большинства, но `UncheckedIOException` лучше — типизированный wrapper позволяет catch'ить именно IO.
> - **`SneakyThrows` (Lombok)** — компиляторный хак, который позволяет бросать checked как unchecked. Опасен: компилятор не предупредит вверху по стеку, чтобы catch'ить IOException. Используйте осторожно.
> - **Optional+exception**: лучше альтернатива — вместо throwing превратить в `Optional<R>` или `Result<R, E>` (sealed pattern в Java 21).
> - **CompletableFuture + checked**: внутри `.thenApply(Function)` тот же запрет. Нужен `.thenApplyAsync` с custom wrap'ом или CompletableFuture.completeExceptionally.
> - **Streams parallel + checked wrapper**: исключения могут оборачиваться `RuntimeException` дважды (ваш + Spliterator). Распаковывайте через `getCause()` в catch.
>
> **Связанные вопросы:** [[Q1]] — SAM contract; [[Q14]] — кастомные функциональные интерфейсы с throws; [[Q7]] — Callable как «throwing Supplier».
>
> ---
>
> #### C) Использовать `Callable<byte[]>` вместо `Function<String, byte[]>` — Callable поддерживает checked exceptions — ❌ Неверно
>
> **Что на самом деле:** `Callable<T>` — это `T call() throws Exception`, **без входных аргументов**. Не подходит как замена `Function<String, byte[]>` потому что не принимает String. Можно использовать в other API (`ExecutorService.submit`), но это другой scope.
>
> **Откуда путаница:** оба поддерживают/не поддерживают checked exceptions. Но Callable семантически отличается — нет «функции от X».
>
> **Если бы это было правдой:** `Map<String, byte[]> data = paths.stream().collect(Collectors.toMap(p -> p, Callable<?>::call))` — но Stream методы не принимают Callable, нужен Function.
>
> ---
>
> #### D) Использовать `@FunctionalInterface` с `throws` — компилятор тогда разрешит лямбде бросать checked — ❌ Неверно
>
> **Что на самом деле:** добавить `throws` можно только в **свой** интерфейс. `Function<T, R>.apply()` — стандартный JDK интерфейс, его сигнатуру нельзя изменить. Создание кастомного интерфейса — действительно валидный путь (см. правильный ответ), но это не «добавить throws к Function».
>
> **Откуда путаница:** ответ частично правильный — кастомный интерфейс с throws работает. Но **вместе с Function он не совмещается** без обёртки.
>
> **Если бы это было правдой:** мы могли бы аннотировать `Function` снаружи и менять его сигнатуру — невозможно в Java (нет structural typing).

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


> [!mcq]
>
> **Вопрос:** Когда оправдано создавать кастомный функциональный интерфейс вместо использования стандартных (`Function`, `Predicate` и т.д.)?
>
> ---
>
> #### A) Никогда — стандартные интерфейсы покрывают все случаи; кастомные интерфейсы лишь усложняют код — ❌ Неверно
>
> **Что на самом деле:** стандартные интерфейсы хороши для generic-операций (filter, map, validate), но имеют недостатки: нет throws checked exceptions, нет доменной семантики (`Function<Order, Receipt>` — что это? валидация? трансформация? сериализация?), нет доменно-специфичных default-методов.
>
> **Откуда путаница:** разработчики часто используют `Function<Order, Receipt>` как «универсальный молоток» и думают что этого достаточно. На практике через 6 месяцев читатель кода не понимает что это значит.
>
> **Если бы это было правдой:** не существовало бы `Comparator<T>` (можно было бы заменить `BiFunction<T, T, Integer>`), `Runnable` (Consumer<Void>?), `Callable` (Supplier<T> with throws — но именно throws нет в Supplier). Эти кастомные SAM существуют в JDK именно ради ясности.
>
> ---
>
> #### B) Только для serialization-purposes — лямбды нельзя сериализовать без кастомного `Serializable` интерфейса — ❌ Неверно
>
> **Что на самом деле:** лямбды **можно** сериализовать через `Serializable` cast: `Runnable r = (Runnable & Serializable) () -> ...`. Серилизация — не главный мотив для кастомных интерфейсов.
>
> **Откуда путаница:** есть нюансы с сериализацией лямбд (компилятор-зависимая внутренняя структура), но это редкий use-case.
>
> **Если бы это было правдой:** не было бы смысла в `Validator`, `RetryStrategy` и других domain-интерфейсах, которые не сериализуются.
>
> ---
>
> #### C) Только когда нужно более 2 аргументов — стандартные интерфейсы ограничены BiFunction (2 аргумента) — ❌ Неверно
>
> **Что на самом деле:** хотя стандартные интерфейсы действительно ограничены 2 arity (BiFunction, BiPredicate, BiConsumer), это **одна из** причин создания кастомных, но не единственная. Семантическая ясность и throws — другие важные мотивации.
>
> **Откуда путаница:** ответ частично верен — для tri-arity и выше нужен custom (например, `TriFunction<A, B, C, R>`). Но «только когда» — слишком ограничительно.
>
> **Если бы это было правдой:** все интерфейсы у которых ≤2 аргумента было бы запрещено делать custom — но `Comparator`, `Validator` в реальном коде имеют ровно 1-2 аргумента и оправданы.
>
> ---
>
> #### D) Когда нужна доменная семантика (`Validator`, `Renderer`, `RetryStrategy`), `throws` для checked exceptions, или специфичные `default`-методы для домена — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Три категории оправданных случаев:
>
> 1. **Семантическая ясность**: `Validator<Order>` мгновенно читается как «проверка валидности заказа». `Function<Order, Boolean>` или `Predicate<Order>` требует контекста чтобы понять что это. Особенно важно для публичных API.
>
> 2. **Checked exceptions**: стандартные интерфейсы НЕ объявляют throws. Если ваш callback может бросать `SQLException`, `IOException`, etc — нужен custom interface с `throws E` (или `throws Exception`).
>
> 3. **Доменно-специфичные default-методы**: `Validator.and()`, `Validator.or()`, `Validator.named(...)`, `RetryStrategy.withMaxAttempts(int)`. Стандартные интерфейсы не дадут такого без обёрток.
>
> **Пример:**
> ```java
> // Семантически — обычный Predicate, но удобнее как Validator:
> @FunctionalInterface
> public interface Validator<T> {
>     ValidationResult validate(T value);  // не boolean — детализированный результат
>
>     default Validator<T> and(Validator<T> other) {
>         return value -> {
>             ValidationResult r1 = this.validate(value);
>             if (!r1.isValid()) return r1;
>             return other.validate(value);
>         };
>     }
>
>     default Validator<T> withMessage(String customMsg) {
>         return value -> {
>             ValidationResult r = this.validate(value);
>             return r.isValid() ? r : ValidationResult.fail(customMsg);
>         };
>     }
> }
>
> // Использование в API:
> public class OrderService {
>     private final Validator<Order> validator;
>     public OrderService(Validator<Order> v) { this.validator = v; }
>     // читатель сразу видит назначение — валидация
> }
>
> // Checked exception (custom):
> @FunctionalInterface
> public interface RestCallback<T> {
>     T call(HttpRequest req) throws IOException, HttpException;
> }
>
> RestCallback<User> fetch = req -> userClient.send(req);  // checked exceptions ОК
> ```
>
> **Когда применять:**
> - **Domain-Driven Design**: интерфейсы — часть ubiquitous language. `OrderValidator`, `PaymentProcessor`, `EventHandler` — лучше чем `Predicate`/`Function`/`Consumer`.
> - **Library API**: публичные интерфейсы должны быть expressive — пользователи запоминают `RetryStrategy.exponentialBackoff()` лучше чем `Function<Integer, Duration>`.
> - **Checked exceptions handling**: SQL, JDBC, file I/O — стандартные интерфейсы не подходят.
> - **Builder-style chaining**: default-методы для fluent композиции (`.and().withMessage().memoize()`).
>
> **Подводные камни:**
> - **Слишком много кастомных интерфейсов** — обратная крайность: каждый Function превращается в свой type. Создавайте только когда есть реальная семантическая ценность.
> - **Совместимость с Stream API**: `stream.filter(Predicate)` не примет ваш кастомный `Validator<T>` напрямую. Нужен adapter: `.filter(validator::isValid)`.
> - **`@FunctionalInterface` обязательно**: без аннотации случайное добавление второго abstract метода поломает SAM, и лямбды перестанут компилироваться без compile-error на самом интерфейсе.
> - **Generic variance**: `Validator<? super Order>` принимает `Validator<Object>` — это нужно явно объявить в API: `void validateAll(Validator<? super T> v)`.
> - **Эволюция**: если позже захотите добавить новый abstract метод (например, batch validation) — это **сломает** всех клиентов с лямбдами. default-методы — безопасный путь расширения.
>
> **Связанные вопросы:** [[Q1]] — определение SAM; [[Q2]] — `@FunctionalInterface` compile-time check; [[Q13]] — checked exceptions в лямбдах.

## See also

- [Java 8](java-8-interview.md) — лямбды, Stream API, Optional как нововведения Java 8
- [Java Stream API](java-stream-interview.md) — Function, Predicate, Consumer в Stream
- [Java Optional](java-optional-interview.md) — Supplier в orElseGet, Consumer в ifPresent
- [Java Concurrency](java-concurrency-interview.md) — Callable, Runnable в многопоточности
- [Java Core](java-core-interview.md) — интерфейсы, default методы Java 8
- [Java Generics](java-generics-interview.md) — типизация функциональных интерфейсов
- [Java 17-21](java-17-21-interview.md) — новые возможности, records с функциональными интерфейсами
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Strategy pattern через функциональные интерфейсы
