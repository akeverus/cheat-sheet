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
updated: "2026-05-05"
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

**Параметр generic-типа** — это параметр, который позволяет использовать тип как переменную в объявлении класса, интерфейса или метода. Дженерики появились в Java 5 и обеспечивают **типобезопасность на этапе компиляции** без потери универсальности кода.

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
1. **Типобезопасность** — ошибки типов ловятся на этапе компиляции, а не в runtime
2. **Устранение кастов** — не нужно приводить `Object` к конкретному типу
3. **Переиспользование кода** — один алгоритм работает с разными типами

> [!mcq]
> - [ ] Generic Type Parameter — это значение, передаваемое в метод во время выполнения программы. | Это runtime arg, не type. ❌ ПОСЛЕДСТВИЕ: путаница типов и значений приводит к ожиданию runtime-передачи Class<T> вместо compile-time выбора — лишний boilerplate `passClass(MyType.class)` где можно было `<T>`.
> - [x] Generic Type Parameter — это переменная типа, объявляемая при описании класса, интерфейса или метода и заменяемая конкретным типом при использовании. | ✓ ПРИМЕНЯТЬ: `class Box<T>`, `<T> T firstOrNull(List<T>)`, `Map<K,V>`; основа typesafe API в JDK (`List<E>`, `Optional<T>`). Type-параметры выражают связь между входами и выходами без runtime overhead. 📋 ПРАВИЛО: "Generic Type Parameter = compile-time variable; T заменяется на конкретный тип при использовании". 🔗 См. Q3 (Generic Method vs Type), Q6 (Type Erasure), Q10 (Bounded Type Parameter).
> - [ ] Generic Type Parameter — это параметр конструктора класса, который определяет начальное значение поля. | Конструктор != type-param. ❌ ПОСЛЕДСТВИЕ: путаница terminology в дизайне приводит к API типа `new Box(MyType)` вместо `new Box<MyType>()` — мутация типа в runtime, теряется типобезопасность.
> - [ ] Generic Type Parameter — это аннотация, указывающая JVM, какой тип ожидается в runtime. | Аннотация != generic. ❌ ПОСЛЕДСТВИЕ: разработчик использует кастомные аннотации `@TypeOf(String.class)` для типизации, дублируя generics — теряет compile-time проверки, получает только runtime.

> [!mcq]
> - [ ] Generic-типы **ковариантны**: `List<Integer>` является подтипом `List<Number>`, потому что `Integer extends Number`. | Generics инвариантны. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `List<Number> nums = listOfIntegers;` — compile error «incompatible types», ищет cast вместо wildcard `List<? extends Number>`.
> - [ ] Generic-типы **контравариантны**: `List<Number>` является подтипом `List<Integer>`, потому что Number — родитель Integer. | Тоже неверно — generics инвариантны. ❌ ПОСЛЕДСТВИЕ: путаница вариантности приводит к assignment по «родительской» интуиции из массивов (где covariance работает), но для generics это compile error.
> - [x] Generic-типы по умолчанию **инвариантны**: `List<Integer>` НЕ является ни подтипом, ни супертипом `List<Number>`, несмотря на отношения между `Integer` и `Number`; для гибкости нужны wildcards (`? extends`, `? super`). | ✓ ПРИМЕНЯТЬ: для read-only API — `List<? extends Number>` (covariance); для write-only — `List<? super Integer>` (contravariance); правило **PECS** (Producer Extends, Consumer Super). Массивы ковариантны (`Integer[]` is `Number[]`) — но это unsafe, бросает `ArrayStoreException`. 📋 ПРАВИЛО: «generics invariant by default; wildcards дают co/contra-variance; arrays covariant но unsafe». 🔗 См. Q14 (Upper Bounded Wildcard), Q15 (Lower Bounded), Q16 (PECS).
> - [ ] Generic-типы ковариантны для `final` классов (например, `String`) и инвариантны для остальных. | Final не влияет. ❌ ПОСЛЕДСТВИЕ: ложное правило приводит к попыткам `List<String>` присвоить `List<CharSequence>` ожидая что `final String` даёт covariance — compile error всё равно.

## Q2. (!) Каковы преимущества использования `Generics`?

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

> [!mcq]
> - [ ] Главное преимущество дженериков — устранение необходимости писать несколько перегруженных методов для каждого типа данных. | Это побочный эффект. ❌ ПОСЛЕДСТВИЕ: команда фокусируется только на устранении дубликатов — пропускает главную ценность (typesafety). Перегрузки можно устранить и через `Object`, но без typesafety.
> - [ ] Главное преимущество дженериков — ускорение работы программы в runtime за счёт специализации байт-кода под конкретный тип. | Type erasure = no specialization. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает performance gain от generics — на деле no specialization, везде используется stub `Object`. Type-аргументы — compile-time только.
> - [x] Главное преимущество дженериков — обнаружение ошибок типов на этапе компиляции, устранение явных приведений типов и возможность писать универсальные алгоритмы. | ✓ ПРИМЕНЯТЬ: typesafe collections (`List<User>` вместо `List`); generic utility methods (`max(List<T extends Comparable<T>>)`); type-safe builders (`return self();` через `<T extends Builder<T>>`). LinkedIn 2015 кейс: миграция legacy на generics сократила NPE/CCE на 40%. 📋 ПРАВИЛО: "generics = compile-time typesafety + no casts + reusable algorithms; не runtime perf". 🔗 См. Q1 (что такое generics), Q6 (Type Erasure), Q10 (Bounded Type).
> - [ ] Главное преимущество дженериков — уменьшение потребления памяти за счёт совместного использования одного экземпляра класса несколькими типами. | Не про память. ❌ ПОСЛЕДСТВИЕ: ложные ожидания о memory benefits; на деле память не меняется (один Class на raw type был и до generics через type erasure).

> [!mcq]
> - [ ] Generics дают **zero overhead** в runtime: компилятор не вставляет никаких дополнительных инструкций, байт-код идентичен коду без generics. | Bridge methods и checkcasts вставляются. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает что `List<String>.get(0)` = чистый `list.get(0)` — на деле компилятор вставляет `checkcast String`, при override generic метода появляются bridge methods.
> - [ ] Generics требуют значительного memory overhead в runtime, так как JVM хранит type-параметры в специальной таблице на каждый instance. | Type erasure → нет per-instance overhead. ❌ ПОСЛЕДСТВИЕ: ложные ожидания о memory cost приводят к избеганию generics в hot-path; на деле runtime-стоимость ровно такая же как у raw types.
> - [x] Преимущества (typesafety, no casts, reusability) приходят с **скрытыми runtime-затратами**: компилятор вставляет `checkcast` при доступе к generic-полям и генерирует **bridge methods** для сохранения полиморфизма после erasure (например при override `get()` с covariant return type). | ✓ ПРИМЕНЯТЬ: смотрите `javap -c -p` чтобы увидеть bridge methods (`public bridge synthetic Object get()` рядом с `public String get()`); ProGuard/R8 минимизируют их в Android; understand bridge при reflection (`Method.isBridge()` для фильтрации). Cost минимален но не zero. 📋 ПРАВИЛО: «generics cost = checkcast + bridge methods; невидимы в исходниках, видны в bytecode». 🔗 См. Q1 (что такое generics), Q6 (Type Erasure), Q7 (как преобразует).
> - [ ] Главное преимущество — runtime-полиморфизм для generic-параметров: JVM выбирает реализацию метода в зависимости от actual type-аргумента. | Erasure → один метод. ❌ ПОСЛЕДСТВИЕ: разработчик пытается реализовать «specialized» logic для разных T в одном методе — на деле в runtime T = Object, никакого dispatch по type-параметру нет.

## Q3. Чем `Generic Method` отличается от `Generic Type`?

**Generic-тип** — параметр типа объявлен на уровне класса/интерфейса и доступен во всех нестатических методах.
**Generic-метод** — параметр типа объявлен перед возвращаемым типом метода и живёт только в рамках этого метода.

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

Generic-метод может быть как в generic-классе, так и в обычном, и может быть статическим (в отличие от параметра типа класса, который в статическом контексте недоступен).

> [!mcq]
> - [ ] Generic Method отличается от Generic Type тем, что Generic Method не может быть статическим, тогда как Generic Type используется только в статическом контексте. | Обратная логика. ❌ ПОСЛЕДСТВИЕ: разработчик пытается использовать `T` (параметр типа КЛАССА) в `static` методе — compile error "non-static class T cannot be referenced from static context". Static методам нужны свои type-параметры.
> - [ ] Generic Method отличается от Generic Type тем, что Generic Method допускает несколько параметров типа, а Generic Type — только один. | Оба support N. ❌ ПОСЛЕДСТВИЕ: ложные ограничения приводят к создание artificial chains `Generic<K> { class Inner<V> }` для двух параметров вместо `Generic<K, V>` напрямую.
> - [x] Generic Method отличается от Generic Type тем, что параметр типа Generic Method объявляется перед возвращаемым типом и существует только в рамках этого метода, тогда как параметр Generic Type объявляется при описании класса и доступен во всех нестатических членах. | ✓ ПРИМЕНЯТЬ: Generic Method для utility-классов (`Collections.<T>emptyList()`, `Arrays.<T>asList()`) и static-методов; Generic Type для контейнеров (`List<E>`, `Map<K,V>`). Class type-param — fields, instance methods; Method type-param — только метод. 📋 ПРАВИЛО: "Generic Method = `<T>` перед return type, scope=method, can be static; Generic Type = `<T>` после class name, scope=instance". 🔗 См. Q1 (generics базово), Q4 (Type Inference), Q10 (Bounded Type).
> - [ ] Generic Method отличается от Generic Type тем, что параметр типа Generic Method всегда должен указываться явно при вызове, тогда как для Generic Type компилятор всегда выводит тип автоматически. | Inference works для обоих. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Utils.<String>firstOrNull(list)` каждый раз — лишний boilerplate; type inference работает на 99% случаев из argument types.

## Q4. (!) Что такое `Type Inference` (вывод типов)?

**Type Inference** — способность компилятора автоматически определить параметр типа из контекста. Компилятор анализирует аргументы метода и целевой тип, чтобы вывести T.

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

> [!mcq]
> - [ ] Type Inference — это механизм, при котором JVM определяет параметр типа в runtime на основе фактических значений аргументов метода. | Compile-time only. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт runtime-magic для типов — пишет код без compile-проверок, удивляется CCE в проде. JVM не знает типы из-за type erasure.
> - [ ] Type Inference — это механизм, при котором разработчик явно указывает параметр типа в угловых скобках перед вызовом метода, чтобы компилятор мог проверить корректность. | Это explicit type, не inference. ❌ ПОСЛЕДСТВИЕ: путаница приводит к коду `Map.<String,Integer>of(...)` где можно `Map.of(...)` с автовыводом — verbose без необходимости.
> - [x] Type Inference — это способность компилятора автоматически определять параметр типа из контекста вызова, например из типа аргументов или из целевого типа переменной. | ✓ ПРИМЕНЯТЬ: diamond operator `<>` (Java 7+); inference из аргументов (`List.of("a", "b")` выводит `List<String>`); target type inference (`Map<String, Integer> map = new HashMap<>()` — Java 7+); `var list = List.of("a")` (Java 10+); улучшенный inference в Java 8 для лямбд. 📋 ПРАВИЛО: "Type Inference = compile-time deduction; diamond operator + target type + var (Java 10+)". 🔗 См. Q1 (generics базово), Q3 (Generic Method/Type), Q5 (соглашения).
> - [ ] Type Inference — это механизм, при котором компилятор подставляет тип `Object` вместо неизвестного параметра типа, когда явного указания нет. | Это type erasure. ❌ ПОСЛЕДСТВИЕ: ложное представление о механизмах приводит к недоверию compiler — разработчик считает что без `<Type>` всё становится `Object`, добавляет лишние casts.

> [!mcq]
> - [ ] В цепочке `stream().collect(Collectors.groupingBy(User::getRole))` компилятор всегда выводит результат как `Map<Object, List<Object>>`, потому что generic-методы из библиотеки не знают типа элементов. | Inference работает через chain. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Map<Role, List<User>> result = stream...collect(Collectors.<User, Role>groupingBy(...))` с лишними explicit type arguments — verbose, хотя inference справляется сам.
> - [ ] Type inference работает только на уровне одного выражения и не «протекает» через chained method calls — каждый `.collect()`, `.map()` инферится независимо. | Inference составной. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет промежуточные переменные `Stream<User> s = ...; Map<Role, ...> m = s.collect(...)` ожидая что цепочка не работает — на деле inference распространяется через всю цепь.
> - [x] Type inference в Java 8+ использует **target typing** и **chain inference**: тип возвращаемого значения цепочки (`Map<Role, List<User>> result = users.stream().collect(Collectors.groupingBy(User::getRole))`) распространяется обратно через `collect` → `groupingBy` → method reference, инферя `<T>` и `<K>` без явных type arguments. | ✓ ПРИМЕНЯТЬ: target typing работает в `return`, assignment, method args, lambda return; для лямбд `Comparator.comparing(User::getName)` — компилятор знает целевой `Comparator<User>`; при ambiguity — добавьте explicit `<>` или промежуточную переменную. 📋 ПРАВИЛО: «Java 8+ inference = bidirectional: target type ↓ + arguments ↑; цепочки и лямбды инферятся как одно целое». 🔗 См. Q1 (generics базово), Q3 (Generic Method), java-8-interview (lambda inference).
> - [ ] Type inference в Java 8+ полностью отказался от target typing в пользу контекста аргументов, поэтому `Collectors.groupingBy()` всегда требует explicit `<K, V>`. | Target typing — основа Java 8 inference. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Collectors.<User, Role>groupingBy(...)` всегда — лишний boilerplate; современный Java компилятор справляется без подсказок.

## Q5. Какие соглашения об именовании параметров типа?

По конвенции используются однобуквенные заглавные имена:

| Параметр | Значение | Пример |
|----------|----------|--------|
| `T` | Type (тип) | `class Box<T>` |
| `E` | Element (элемент коллекции) | `interface List<E>` |
| `K` | Key (ключ) | `interface Map<K, V>` |
| `V` | Value (значение) | `interface Map<K, V>` |
| `N` | Number (число) | `class Matrix<N extends Number>` |
| `S, U` | Дополнительные типы | `class Pair<S, U>` |
| `R` | Result (результат) | `Function<T, R>` |

Эти соглашения не обязательны синтаксически, но общеприняты и улучшают читаемость кода.

> [!mcq]
> - [ ] По соглашению параметр типа для элемента коллекции обозначается буквой `T`, а для общего типа — буквой `E`. | Перепутано. ❌ ПОСЛЕДСТВИЕ: nonstandard naming затрудняет чтение кода — code review медленнее, новые члены команды путаются. JDK convention однозначна.
> - [ ] По соглашению параметр типа для ключа в Map обозначается буквой `V`, а для значения — буквой `K`. | Перепутано. ❌ ПОСЛЕДСТВИЕ: путаница K/V в коде приводит к багам на ровном месте — `map.computeIfAbsent(value, k -> ...)` (где key и value перепутаны) ведёт к runtime ошибкам.
> - [x] По соглашению параметр типа для общего типа обозначается `T`, для элемента коллекции — `E`, для ключа — `K`, для значения — `V`, для числа — `N`, для результата — `R`. | ✓ ПРИМЕНЯТЬ: следуйте JDK конвенции — `Map<K,V>`, `List<E>`, `Function<T,R>`, `BiFunction<T,U,R>`. Дополнительные параметры — `S`, `U`, `V` после основных. 📋 ПРАВИЛО: "T=Type, E=Element, K=Key, V=Value, N=Number, R=Result; одна буква, capital". 🔗 См. Q1 (generics базово), Q4 (Type Inference), java-collections-interview.
> - [ ] По соглашению параметр типа всегда должен быть однобуквенным заглавным символом, и использование других имён, таких как `Type` или `Element`, вызывает ошибку компиляции. | Не enforced. ❌ ПОСЛЕДСТВИЕ: миф о compile error приводит к попыткам `<RequestBody>` или `<UserId>` для clarity — хотя синтаксически валидно, нарушает читаемость. Используйте `T`, `K`, `V` стандартно.

## Q6. (!) Что такое `Type Erasure` и зачем оно нужно?

**Type Erasure** (стирание типов) — механизм, при котором компилятор удаляет всю информацию о generic-типах при компиляции в байт-код. После стирания JVM не знает о параметрах типа — они существуют только на этапе компиляции.

**Зачем:** обратная совместимость с кодом, написанным до Java 5. Байт-код с дженериками и без должен быть взаимозаменяем.

```mermaid
graph LR
    A["List&lt;String&gt;<br/>исходный код"] -->|"Компилятор<br/>(type erasure)"| B["List<br/>байт-код"]
    C["List&lt;Integer&gt;<br/>исходный код"] -->|"Компилятор<br/>(type erasure)"| B
    B -->|"JVM"| D["Один и тот же<br/>List.class"]
```

Это значит, что `List<String>` и `List<Integer>` — это **один и тот же класс** в runtime:

```java
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// true! В runtime оба — просто ArrayList
System.out.println(strings.getClass() == integers.getClass()); // true
```

> [!mcq]
> - [ ] Type Erasure нужен для повышения производительности дженерик-кода в runtime за счёт специализации байт-кода под конкретный тип. | Erasure НЕ specialization. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает Project Valhalla-like specialization — `List<int>` все ещё `List<Integer>` с boxing. Real specialization появится только с Valhalla (Java X+).
> - [x] Type Erasure нужен для обратной совместимости с кодом, написанным до Java 5, чтобы байт-код с дженериками и без был взаимозаменяем. | ✓ ПРИМЕНЯТЬ: понимание erasure объясняет: (1) почему `new T()` не работает (нужен `Class<T>` factory); (2) почему `instanceof List<String>` запрещён (только `List<?>`); (3) почему overloading `m(List<String>)` и `m(List<Integer>)` — compile error (одинаковые erasures). C# взял другой путь — reified generics. 📋 ПРАВИЛО: "Type Erasure = compile-time only; runtime sees raw types; price = no `new T()` / `instanceof T<X>`". 🔗 См. Q7 (как преобразует код), Q8 (runtime info), Q9 (Reifiable types).
> - [ ] Type Erasure нужен для уменьшения размера `.class` файлов за счёт хранения информации о типах только в исходном коде. | Размер не цель. ❌ ПОСЛЕДСТВИЕ: ложные ожидания о binary size benefits; `.class` files той же длины с/без generics. Сама причина введения — backward compatibility со всем pre-Java 5 ecosystem.
> - [ ] Type Erasure нужен для защиты от несанкционированного доступа к параметрам типа через рефлексию в runtime. | Не security. ❌ ПОСЛЕДСТВИЕ: разработчик считает что generic typing скроет внутренние типы; на деле через `getGenericSuperclass()` всё доступно (Q8). Type Erasure — архитектурное решение, не security feature.

> [!mcq]
> - [x] Type Erasure влечёт три практических следствия: запрет `new T()` и `T.class`, запрет `instanceof T<X>` и запрет одинаковых erasures у overloaded методов; всё это — цена backward compatibility. | ✓ ПРИМЕНЯТЬ: для factory без `new T()` передавайте `Class<T>` или `Supplier<T>`; для type-tag используйте Super Type Token; для overloading с generics — переименовывайте методы (`processStrings`/`processInts`). 📋 ПРАВИЛО: «erasure забирает: `new T()`, `T.class`, `instanceof T<X>`, distinct overloads». 🔗 См. Q7 (как преобразует), Q8 (Super Type Token), Q26 (static field).
> - [ ] После erasure методы `m(List<String>)` и `m(List<Integer>)` имеют разные сигнатуры в байт-коде, поэтому overloading работает. | Сигнатуры одинаковы. ❌ ПОСЛЕДСТВИЕ: разработчик объявляет два метода с разными generic параметрами, ловит compile error «name clash: have the same erasure» и долго ищет причину.
> - [ ] После erasure JVM хранит generic-параметры в специальной секции метаданных класса и доступна только через `Unsafe`. | Generic-параметры стираются полностью (кроме signature attribute для рефлексии). ❌ ПОСЛЕДСТВИЕ: попытка извлечь runtime-тип через `Unsafe.getInt(field, offset)` — работает с raw типом, generic-аргумент недоступен; иллюзия наличия данных.
> - [ ] Erasure применяется только к generic-классам, но generic-методы сохраняют параметры типа в байт-коде через `MethodType` для invokedynamic. | Erasure применяется одинаково. ❌ ПОСЛЕДСТВИЕ: разработчик надеется, что generic-метод даст runtime type info без `Class<T>` — на практике в generic-методе `T` тоже стирается, рефлексия не помогает.

## Q7. (!) Как `Type Erasure` преобразует generic-код?

Компилятор выполняет три преобразования:

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

> [!mcq]
> - [ ] При type erasure неограниченный параметр типа `T` заменяется на `null`, а ограниченный `T extends Number` заменяется на `Object`. | `null` не type. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает specific behavior на основе ложного представления — пишет `if (field == null)` для проверки is-generic, не работает.
> - [ ] При type erasure неограниченный параметр типа `T` заменяется на `Object`, а ограниченный `T extends Number` тоже заменяется на `Object`, потому что `Number` тоже является объектом. | T extends Number → Number. ❌ ПОСЛЕДСТВИЕ: разработчик пытается оптимизировать generic-код используя `Number` методы (`doubleValue()`), думая что внутри будет `Object` — на деле компилятор уже erases на `Number`, методы доступны напрямую.
> - [x] При type erasure неограниченный параметр типа `T` заменяется на `Object`, а ограниченный `T extends Number` заменяется на первую границу `Number`; кроме того, компилятор вставляет приведения типов на стороне вызывающего кода. | ✓ ПРИМЕНЯТЬ: bounded types для доступа к методам base-class (`<T extends Comparable<T>>` даёт доступ к `compareTo`); множественные границы `<T extends Number & Comparable<T>>` (первая — class erasure). После erasure: `Box<T extends Number>` имеет `Number` поля, методы `Number` accessible. 📋 ПРАВИЛО: "unbounded T → Object; T extends X → X (первая граница); checkcast вставляется на caller-side". 🔗 См. Q6 (зачем erasure), Q8 (runtime info), Q10 (Bounded Type).
> - [ ] При type erasure неограниченный параметр типа `T` заменяется на `Object`, ограниченный `T extends Number` заменяется на `Number`, но компилятор не вставляет никаких приведений типов, так как это снизило бы производительность. | Casts вставляются. ❌ ПОСЛЕДСТВИЕ: ложные ожидания приводят к недоверию compile output — на деле checkcast обязательны для typesafety, ClassCastException бросается на caller-side при типовом нарушении.

> [!mcq]
> - [ ] При override generic-метода с covariant return type (например `Iterator<String>.next() : String` overriding `Iterator<E>.next() : E`) компилятор просто заменяет метод родителя — никакого дополнительного кода не генерируется. | Bridge methods обязательны. ❌ ПОСЛЕДСТВИЕ: разработчик не знает про bridge — при reflection видит «лишние» методы `Object next()` рядом с `String next()`, фильтрует их вручную вместо `Method.isBridge()`.
> - [ ] Bridge methods — это устаревший механизм Java 5 для совместимости с Java 1.4, и в современной Java 17+ они больше не генерируются. | Bridge methods всё ещё генерируются. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает чистый bytecode на Java 17 — на деле `javap` всё равно показывает bridge synthetic методы для поддержки virtual dispatch после erasure.
> - [x] Помимо `T → Object` (или первой границы) и checkcast на caller-side, компилятор генерирует **bridge methods** при override generic-методов: для `class StringList extends ArrayList<String>` с override `add(String)` создаётся synthetic `add(Object)`, который делегирует в `add(String)` — это сохраняет полиморфизм после erasure. | ✓ ПРИМЕНЯТЬ: bridge methods нужны потому что после erasure родительский метод имеет сигнатуру `add(Object)`, а ребёнок — `add(String)`; без bridge virtual dispatch сломался бы. Для covariant return (`@Override Cat get()` vs `Animal get()` родителя) — тоже bridge. При reflection используйте `Method.isBridge()` чтобы исключать. 📋 ПРАВИЛО: «erasure: T→Object/bound + checkcast + bridge methods для polymorphism preservation». 🔗 См. Q6 (Type Erasure), Q8 (runtime info), Q24 (covariant return).
> - [ ] Bridge methods генерируются только для абстрактных классов и интерфейсов; конкретные классы их не используют. | Любой override generic метода. ❌ ПОСЛЕДСТВИЕ: разработчик ищет bridge только в abstract `Comparable.compareTo` — пропускает их в обычных классах вроде `class IntList extends ArrayList<Integer>`, удивляется при reflection.

## Q8. (!) Доступна ли информация о generic-типе во время выполнения?

В общем случае **нет** — из-за стирания типов. Но есть исключение: когда generic-тип зафиксирован в **сигнатуре класса** (при наследовании), информация сохраняется в метаданных класса.

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

> [!mcq]
> - [ ] Информация о generic-типе доступна в runtime в полном объёме через вызов `getClass()` на экземпляре, потому что каждый объект знает свой точный тип. | getClass = raw type. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `if (list.getClass() == ArrayList<String>.class)` — compile error, потому что `ArrayList<String>.class` не существует. `list.getClass()` возвращает `ArrayList`, type-параметры стёрты.
> - [x] Информация о generic-типе в общем случае недоступна в runtime из-за type erasure, но сохраняется в метаданных класса, когда тип зафиксирован в сигнатуре при наследовании, что позволяет извлечь его через рефлексию с помощью `getGenericSuperclass()`. | ✓ ПРИМЕНЯТЬ: Super Type Token pattern (Jackson `TypeReference<T>`, Spring `ParameterizedTypeReference<T>`); `objectMapper.readValue(json, new TypeReference<List<User>>(){})` для десериализации generic коллекций; Hibernate и Spring Data используют для query results. 📋 ПРАВИЛО: "runtime generic info = только при наследовании (anonymous subclass или explicit `extends Foo<X>`); используйте Super Type Token для сохранения типа". 🔗 См. Q6 (Type Erasure), Q9 (Reifiable types), Q10 (Bounded Type).
> - [ ] Информация о generic-типе недоступна в runtime никогда, так как type erasure полностью удаляет все параметры типа из байт-кода и метаданных классов. | Не "никогда" — в superclass signature сохраняется. ❌ ПОСЛЕДСТВИЕ: разработчик не знает про Super Type Token, реализует deserialization через Class<T> + ручной cast — Jackson/Spring предоставляют готовое решение.
> - [ ] Информация о generic-типе доступна в runtime только для примитивных типов, так как JVM хранит специализированные версии классов для `int`, `long` и других примитивов. | Generics ≠ примитивы. ❌ ПОСЛЕДСТВИЕ: Java generics не работают с примитивами напрямую — `List<int>` invalid, нужен `List<Integer>` с autoboxing overhead. Project Valhalla адресует это в будущем.

> [!mcq]
> - [ ] Spring `RestTemplate.exchange()` для получения `List<User>` принимает `List.class` — фреймворк сам разберётся с generic-типом через reflection экземпляра. | List.class теряет тип. ❌ ПОСЛЕДСТВИЕ: `restTemplate.exchange(url, GET, null, List.class)` возвращает `List<LinkedHashMap>` (Jackson default), а не `List<User>` — `getMethod` без type hint не знает про User.
> - [ ] В Jackson для десериализации generic коллекций используется `ObjectMapper.readValue(json, Class<T>)` с `List.class` — Jackson автоматически определит generic тип по содержимому JSON. | Jackson не парсит без подсказок. ❌ ПОСЛЕДСТВИЕ: разработчик получает `List<LinkedHashMap>` вместо `List<User>`, добавляет ручные `mapper.convertValue` для каждого элемента — медленно, error-prone.
> - [x] Фреймворки решают проблему type erasure через **Super Type Token** pattern: Jackson `new TypeReference<List<User>>(){}` (анонимный подкласс фиксирует тип в superclass signature) и Spring `new ParameterizedTypeReference<List<User>>(){}` для `RestTemplate.exchange()` / WebClient — оба извлекают тип через `getGenericSuperclass()`. | ✓ ПРИМЕНЯТЬ: Jackson — `objectMapper.readValue(json, new TypeReference<Map<String, List<User>>>(){})`; Spring REST — `restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<User>>(){})`; Spring Data JPA query results; Guice/Dagger для injection generic types. Анонимный subclass обязателен — без `{}` тип сотрётся. 📋 ПРАВИЛО: «runtime generic в фреймворках = Super Type Token (`TypeReference`/`ParameterizedTypeReference`) + анонимный subclass `{}`». 🔗 См. Q6 (Type Erasure), Q9 (Reifiable types), Q33 (Super Type Token).
> - [ ] Spring и Jackson используют `Class.forName("java.util.List<com.example.User>")` для динамической загрузки параметризованных классов в runtime. | Такого имени класса не существует. ❌ ПОСЛЕДСТВИЕ: попытка `Class.forName("List<User>")` бросает `ClassNotFoundException`; разработчик не понимает что параметризованных Class объектов нет — только raw `List.class`.

## Q9. Что такое `Reifiable` и `Non-Reifiable` типы?

**Reifiable** тип — тип, информация о котором полностью доступна в runtime:
- Примитивы: `int`, `double`
- Non-generic классы: `String`, `Object`
- Raw types: `List`, `Map`
- Unbounded wildcards: `List<?>`, `Map<?, ?>`
- Массивы reifiable типов: `String[]`, `int[]`

**Non-reifiable** тип — информация теряется после стирания:
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

> [!mcq]
> - [ ] Reifiable тип — это тип, информация о котором доступна в runtime, и к ним относятся `List<String>`, `Map<String, Integer>` и другие параметризованные типы. | Параметризованные = NON-reifiable. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `if (obj instanceof List<String>)` ожидая что компилируется — получает `Cannot perform instanceof check against parameterized type List<String>`. Reifiable terminology путается у джунов.
> - [ ] Reifiable тип — это тип, информация о котором доступна в runtime, и к ним относятся только примитивы и массивы примитивов. | Слишком узко. ❌ ПОСЛЕДСТВИЕ: ложные ограничения приводят к избеганию `instanceof` для `String`, `Object` — на деле они reifiable, проверка работает.
> - [x] Reifiable тип — это тип, информация о котором полностью доступна в runtime; к ним относятся примитивы, non-generic классы, raw types, unbounded wildcards и массивы reifiable типов. | ✓ ПРИМЕНЯТЬ: для type checks — `instanceof List<?>` (wildcard), `instanceof String` (non-generic), `instanceof int[]` (primitive array); для creating arrays of generics — `(T[]) Array.newInstance(componentType, size)` через Class<T> token (т.к. `new T[10]` запрещён). 📋 ПРАВИЛО: "reifiable = runtime-known: primitives, non-generic, raw, wildcards, arrays of reifiable; non-reifiable = parameterized types (List<String>)". 🔗 См. Q6 (Type Erasure), Q8 (runtime info), Q10 (Bounded Type).
> - [ ] Reifiable тип — это тип, информация о котором доступна в runtime, и к ним относятся все типы с аннотацией `@Retention(RetentionPolicy.RUNTIME)`. | Не про аннотации. ❌ ПОСЛЕДСТВИЕ: путаница concepts приводит к попыткам решить generics-проблемы через annotations — `@Retention(RUNTIME)` не делает generic тип reifiable; нужны другие механизмы (Super Type Token).

## Q10. (!) Что такое `Bounded Type Parameter`?

**Bounded Type Parameter** ограничивает допустимые типы-аргументы с помощью `extends`. Это позволяет использовать методы ограничивающего типа внутри generic-кода.

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

> [!mcq]
> - [ ] `Bounded Type Parameter` — это параметр типа, значение которого ограничено диапазоном чисел в runtime через аннотации валидации. | Bounds = типы, не числа. ❌ ПОСЛЕДСТВИЕ: разработчик путает `<T extends Number>` с `@Min/@Max` из Bean Validation — ставит лишние JSR-303 аннотации на T где нужно compile-time ограничение.
> - [x] `Bounded Type Parameter` ограничивает допустимые типы-аргументы через `extends` (`<T extends Number>`), позволяя использовать методы ограничивающего типа внутри generic-кода. | ✓ ПРИМЕНЯТЬ: `<T extends Comparable<T>>` для сортировки; `<T extends Number>` для арифметики (`value.doubleValue()`); `<T extends Closeable>` для try-with-resources. После erasure `T extends Number` → `Number`, методы Number доступны без cast. 📋 ПРАВИЛО: «`extends` в generics = upper bound; T получает API родителя, после erasure заменяется на bound». 🔗 См. Q11 (Multiple Bounds), Q12 (Recursive Bound), Q14 (Upper Bounded Wildcard).
> - [ ] `Bounded Type Parameter` использует ключевое слово `super` для указания, что тип должен быть супертипом указанного класса. | `super` — для wildcards, не type-params. ❌ ПОСЛЕДСТВИЕ: `<T super Number>` — compile error; разработчик пишет невалидный код, путая type-parameter bounds (`extends`) с wildcards (`extends`/`super`).
> - [ ] `Bounded Type Parameter` обязательно требует указания нескольких границ через `&`, иначе использовать `extends` нельзя. | Single bound валиден. ❌ ПОСЛЕДСТВИЕ: ложное правило приводит к искусственным `<T extends Number & Object>` вместо простого `<T extends Number>` — лишний boilerplate без пользы.

> [!mcq]
> - [ ] Множественные границы пишутся через запятую: `<T extends Number, Comparable<T>>` — компилятор требует разделитель `,` для нескольких bound. | Запятая используется для разделения параметров. ❌ ПОСЛЕДСТВИЕ: `<T extends Number, Comparable<T>>` интерпретируется как два type-параметра T и Comparable<T> — compile error «cannot find symbol Comparable<T>» сбивает с толку.
> - [ ] Множественные границы можно писать в любом порядке — компилятор сам определит, какой из bound является классом, а какой интерфейсом. | Класс должен быть первым. ❌ ПОСЛЕДСТВИЕ: `<T extends Comparable<T> & Number>` — compile error «interface expected here»; разработчик долго ищет проблему, пока не узнает про правило «class first».
> - [x] Множественные границы записываются через `&`: `<T extends Number & Comparable<T> & Serializable>`; **класс (если есть) должен быть первым**, далее идут интерфейсы; после erasure `T` заменяется на **первую** границу (в примере — `Number`), для остальных границ компилятор вставляет checkcast при необходимости. | ✓ ПРИМЕНЯТЬ: `<T extends Number & Comparable<T>>` — арифметика + сортировка одновременно; `<T extends Closeable & Serializable>` — для типов, которые нужно сериализовать и закрывать; ставьте самый «богатый» (с большим API) bound первым — после erasure его методы будут доступны без cast. 📋 ПРАВИЛО: «multiple bounds = `class & interface1 & interface2`; класс первый; erasure → first bound». 🔗 См. Q10 (Bounded Type), Q11 (Multiple Bounds), Q12 (Recursive Bound), Q7 (как erasure преобразует).
> - [ ] Множественные границы используют `|` (вертикальную черту), как в multi-catch: `<T extends Comparable | Serializable>` означает «T — это Comparable или Serializable». | `|` — это disjunction для catch, не для generics. ❌ ПОСЛЕДСТВИЕ: разработчик путает union types из других языков (Kotlin, TypeScript) с Java generics — пишет невалидный синтаксис, не понимает что Java не поддерживает union bounds.

## Q11. (!) Что такое `Multiple Bounds` (`T extends A & B`)?

Параметр типа может иметь несколько границ, разделённых `&`. Класс (если есть) должен идти первым, далее — интерфейсы:

```java
// T должен наследовать Animal И реализовать Serializable И Comparable
public class SmartCage<T extends Animal & Serializable & Comparable<T>> {
    private List<T> animals = new ArrayList<>();

    public T getOldest() {
        return Collections.max(animals); // Comparable доступен
    }
}
```

Правила:
- Максимум **один класс** в bounds (и он должен быть первым)
- **Интерфейсов** может быть несколько
- `T extends Serializable & Comparable<T>` — OK (только интерфейсы)
- `T extends Object & Comparable<T>` — OK (`Object` как класс первый)
- `T extends Comparable<T> & Animal` — **ошибка**, если `Animal` — класс (класс не первый)

> [!mcq]
> - [ ] `Multiple Bounds` позволяет указать несколько классов через `&`, при этом порядок не имеет значения. | Только один класс. ❌ ПОСЛЕДСТВИЕ: попытка `<T extends Animal & HashMap>` — compile error «only one class allowed»; разработчик не понимает почему и пытается обойти через каст.
> - [ ] `Multiple Bounds` записывается через запятую (`<T extends A, B>`) и применяется только в generic-методах. | Разделитель — `&`. ❌ ПОСЛЕДСТВИЕ: `<T extends Serializable, Comparable<T>>` парсится как два type-параметра, а не один с двумя границами; signature теряет нужное ограничение.
> - [x] `Multiple Bounds` объявляется через `&` (`<T extends A & B & C>`); максимум один класс (и он должен быть первым), интерфейсов может быть несколько. | ✓ ПРИМЕНЯТЬ: `<T extends Animal & Serializable & Comparable<T>>` для строгих требований; `<T extends Comparable<T> & Cloneable>` (только интерфейсы). После erasure `T` заменяется на ПЕРВУЮ границу (`Animal`). 📋 ПРАВИЛО: «Multiple Bounds = `class & interface & interface`; class first, max 1 class, N interfaces; erasure → first bound». 🔗 См. Q10 (Bounded Type), Q12 (Recursive Bound), Q7 (как преобразует erasure).
> - [ ] `Multiple Bounds` поддерживает не более двух границ, иначе компилятор выдаст ошибку. | Лимита нет. ❌ ПОСЛЕДСТВИЕ: разработчик дробит API на subtypes ради искусственного «лимита 2»; на деле `<T extends A & B & C & D>` валиден, читается явно описывая контракт.

## Q12. Что такое `Recursive Type Bound` (`T extends Comparable<T>`)?

**Recursive Type Bound** — тип ограничен выражением, содержащим сам себя. Самый известный пример — `Comparable`:

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

Паттерн используется в Builder-ах для возврата правильного типа при наследовании:

```java
public abstract class Builder<T extends Builder<T>> {
    public T withName(String name) {
        this.name = name;
        return self();
    }
    protected abstract T self();
}
```

> [!mcq]
> - [ ] `Recursive Type Bound` — это рекурсивный вызов generic-метода с автоматической типизацией результата. | Это о объявлении, не вызове. ❌ ПОСЛЕДСТВИЕ: путаница terminology приводит к попыткам реализации recursion через generics — на деле это структурное ограничение типа.
> - [x] `Recursive Type Bound` — параметр типа ограничен выражением, содержащим сам себя (`<T extends Comparable<T>>`); используется чтобы T мог взаимодействовать сам с собой. | ✓ ПРИМЕНЯТЬ: `<T extends Comparable<T>>` для типобезопасной сортировки (метод `compareTo` принимает T); `Enum<E extends Enum<E>>` в JDK; CRTP-builders `<B extends Builder<B>>` для chained API с правильным return type в наследниках. 📋 ПРАВИЛО: «Recursive Bound = `<T extends F<T>>`; pattern когда T должен «знать» себя — Comparable, Enum, fluent Builder». 🔗 См. Q10 (Bounded Type), Q11 (Multiple Bounds), Q39 (Comparable vs Comparator).
> - [ ] `Recursive Type Bound` означает, что generic-класс может содержать поле своего же типа (`class Node<T> { Node<T> next; }`). | Это self-reference поля, не bound. ❌ ПОСЛЕДСТВИЕ: подмена понятий — класс с self-полем не имеет рекурсивной границы; `LinkedList<T>` ≠ `<T extends Comparable<T>>`.
> - [ ] `Recursive Type Bound` запрещён в Java и реализуется только через рефлексию. | Полностью валиден. ❌ ПОСЛЕДСТВИЕ: разработчик отказывается от типобезопасных Comparable-API и использует `Comparable` raw — теряет typesafety на ровном месте, ловит CCE при сравнении разных типов.

## Q13. (!) Что такое `Wildcard Type` и какие виды бывают?

**Wildcard** (`?`) — подстановочный знак, представляющий неизвестный тип. Используется когда конкретный тип не важен или не может быть выражен через параметр типа.

Три вида wildcards:

```mermaid
graph TD
    W["Wildcard Types"] --> UB["Upper Bounded<br/>? extends T<br/>читаем как T"]
    W --> UN["Unbounded<br/>?<br/>читаем как Object"]
    W --> LB["Lower Bounded<br/>? super T<br/>пишем T"]

    style UB fill:#d4edda
    style UN fill:#fff3cd
    style LB fill:#d1ecf1
```

| Вид | Синтаксис | Чтение | Запись | Пример |
|-----|-----------|--------|--------|--------|
| `Upper Bounded` | `? extends T` | Как `T` | Нельзя (кроме `null`) | `List<? extends Number>` |
| `Unbounded` | `?` | Как `Object` | Нельзя (кроме `null`) | `List<?>` |
| `Lower Bounded` | `? super T` | Как `Object` | Можно `T` и подтипы | `List<? super Integer>` |

> [!mcq]
> - [ ] Существует только два вида `Wildcard`: `? extends T` и `? super T`; неограниченный `?` — это синоним raw type. | `<?>` ≠ raw type. ❌ ПОСЛЕДСТВИЕ: разработчик использует `List` (raw) вместо `List<?>` — теряет compile-проверки и получает unchecked warnings везде.
> - [x] Существует три вида `Wildcard`: `? extends T` (upper bounded), `? super T` (lower bounded) и `?` (unbounded); все они представляют неизвестный тип с разными ограничениями. | ✓ ПРИМЕНЯТЬ: `? extends T` для producer (`addAll(Collection<? extends E>)`); `? super T` для consumer (`Comparator<? super T>`); `<?>` когда тип не важен (`printList(List<?>)`). PECS определяет выбор. 📋 ПРАВИЛО: «3 wildcards: `? extends T` (upper, read), `? super T` (lower, write), `?` (unbounded, читать как Object)». 🔗 См. Q14 (Upper Bounded), Q15 (Lower Bounded), Q16 (Unbounded), Q18 (PECS).
> - [ ] `Wildcard` `?` всегда взаимозаменяем с конкретным параметром типа `T` без потери функциональности. | Wildcard ≠ type-param. ❌ ПОСЛЕДСТВИЕ: попытка `List<?> list; list.add(item);` — compile error; разработчик не понимает почему `<T>` принимает добавление, а `<?>` нет.
> - [ ] `Wildcard` поддерживает множественные границы через `&` (`? extends A & B`), как и параметры типа. | Не поддерживает. ❌ ПОСЛЕДСТВИЕ: `List<? extends Number & Comparable>` — compile error; разработчик переписывает API через generic-метод `<T extends Number & Comparable>` — но это другая семантика.

## Q14. (!) Что такое `Upper Bounded Wildcard` (`? extends T`)?

`? extends T` означает «любой тип, который является `T` или его подтипом». Используется когда нужно **читать** элементы из коллекции (producer).

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

**Ограничение:** в `List<? extends Animal>` нельзя добавлять элементы (кроме `null`), потому что компилятор не знает конкретный тип:

```java
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(42);    // Compilation error!
numbers.add(null);  // OK — null допустим для любого типа
Number n = numbers.get(0); // OK — читать как Number можно
```

> [!mcq]
> - [ ] `? extends T` означает «любой тип, который является T или его супертипом», и в такую коллекцию можно свободно добавлять элементы типа T. | Это про `super`, не `extends`. ❌ ПОСЛЕДСТВИЕ: попытка `addAll` в `List<? extends Number>` — compile error; production code ломается на refactoring при изменении сигнатуры с `super` на `extends`.
> - [x] `? extends T` означает «любой тип, который является T или его подтипом»; такая коллекция — producer (можно читать как T, нельзя добавлять кроме `null`). | ✓ ПРИМЕНЯТЬ: `Collection<? extends Animal>` принимает `List<Cat>`, `List<Dog>`; `addAll(Collection<? extends E> c)` в JDK; `Stream<? extends T>` для apply-операций. Чтение `Animal a = list.get(0)` работает. 📋 ПРАВИЛО: «`? extends T` = upper bound = producer = read T, no write (kроме null)». 🔗 См. Q13 (Wildcard виды), Q15 (Lower Bounded), Q18 (PECS), Q20 (ковариантность).
> - [ ] `? extends T` запрещает чтение элементов из коллекции, разрешая только добавление. | Полностью наоборот. ❌ ПОСЛЕДСТВИЕ: разработчик пишет API `process(List<? extends Number> input) { input.clear(); input.add(...) }` — compile error на add; ломает контракт producer.
> - [ ] `? extends T` эквивалентен `? super T` при условии, что T — final-класс. | Не эквивалентны никогда. ❌ ПОСЛЕДСТВИЕ: подмена приводит к выбору `super` где нужен `extends`; copy(`<? super T>` dest, `<? extends T>` src) перестаёт работать после «оптимизации».

> [!mcq]
> - [ ] Внутри метода `void f(List<? extends Number> a, List<? extends Number> b)` оба wildcards представляют один и тот же неизвестный тип, поэтому `a.add(b.get(0))` компилируется. | Capture different. ❌ ПОСЛЕДСТВИЕ: разработчик пытается «перелить» элемент из `List<Integer>` в `List<Double>` через общий `extends Number` API — compile error «capture#1 != capture#2», ломается merge-логика.
> - [ ] Чтобы скопировать элемент внутри `List<? extends Number>` (`list.set(0, list.get(1))`) достаточно прямого вызова — компилятор сам подставит wildcard. | Set требует точный capture. ❌ ПОСЛЕДСТВИЕ: попытка `list.set(0, list.get(1))` падает с `cannot be applied to (int, capture#1 of ? extends Number)`; нужен helper-метод `<T> void swap(List<T> l, int i, int j)`.
> - [x] Каждое использование `? extends T` подвергается **capture conversion** в свежий тип `capture#N`; два разных wildcards — два разных capture, поэтому JDK-методы вроде `Collections.swap` объявлены через generic-метод `<T>`, чтобы зафиксировать тип. | ✓ ПРИМЕНЯТЬ: для операций write-after-read внутри `List<? extends T>` пишите private helper `<E> void doSwap(List<E> l) { l.set(0, l.get(1)); }`, который перехватывает wildcard в свой type parameter (capture helper pattern). 📋 ПРАВИЛО: «`? extends T` = свежий `capture#N` на каждом сайте; для self-операций — capture helper с `<E>`». 🔗 См. Q14 (Upper Bounded), Q17 (`<T>` vs `<?>`), Q18 (PECS).
> - [ ] Capture conversion работает только для `? extends T`, для `? super T` и `<?>` компилятор использует «raw-эквивалент». | Capture для всех wildcards. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает другого поведения у `? super T`, пишет ассиметричный код; компилятор с capture#N работает единообразно для всех трёх wildcards.

## Q15. (!) Что такое `Lower Bounded Wildcard` (`? super T`)?

`? super T` означает «любой тип, который является `T` или его суперклассом». Используется когда нужно **писать** элементы в коллекцию (consumer).

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

**Ограничение при чтении:** из `List<? super Integer>` элементы читаются только как `Object`, потому что компилятор не знает, является ли список `List<Integer>`, `List<Number>` или `List<Object>`:

```java
List<? super Integer> list = new ArrayList<Number>();
list.add(42);              // OK — пишем Integer
Object obj = list.get(0);  // OK — читаем как Object
Integer i = list.get(0);   // Compilation error!
```

> [!mcq]
> - [ ] `? super T` означает «тип T или его подтип», в такую коллекцию нельзя добавлять элементы T. | Это `extends`, не `super`. ❌ ПОСЛЕДСТВИЕ: путаница приводит к API `Comparator<? super T>` где разработчик подаёт `Comparator<SubT>` ожидая работу — на деле compile error; PECS-нарушения везде.
> - [x] `? super T` означает «любой тип, который является T или его супертипом»; такая коллекция — consumer (можно добавлять T и подтипы, читать только как `Object`). | ✓ ПРИМЕНЯТЬ: `Comparator<? super T>` в `Collections.sort` (компаратор Animal сортирует Cat); `Consumer<? super T>` в `forEach`; `Collections.copy(List<? super T> dest, List<? extends T> src)`. 📋 ПРАВИЛО: «`? super T` = lower bound = consumer = write T (и подтипы), read only as Object». 🔗 См. Q14 (Upper Bounded), Q18 (PECS), Q20 (контравариантность), Q36 (extends vs super).
> - [ ] `? super T` позволяет читать элементы как T напрямую без приведения типов. | Только Object. ❌ ПОСЛЕДСТВИЕ: `Integer i = list.get(0)` в `List<? super Integer>` — compile error; production-код требует явного `(Integer) list.get(0)` или рефакторинга на `<? extends T>`.
> - [ ] `? super T` принимает только `List<T>` и `List<Object>`, не работает с промежуточными типами. | Принимает все супертипы. ❌ ПОСЛЕДСТВИЕ: ложное правило приводит к API без `Number` или `Serializable`; addIntegers(`List<Number>`) перестаёт компилироваться вопреки PECS.

> [!mcq]
> - [ ] `Collections.sort(List<Cat>, Comparator<Cat>)` — единственно правильная сигнатура; `Comparator<Animal>` нельзя использовать для сортировки `List<Cat>`. | Должно работать через `? super`. ❌ ПОСЛЕДСТВИЕ: разработчик дублирует `Comparator<Animal>` и `Comparator<Cat>` для каждого подкласса вместо одного общего; reuse страдает.
> - [ ] `Collections.sort(List<T>, Comparator<? extends T>)` — корректное JDK-объявление, потому что компаратор «производит» результаты сравнения. | Comparator = consumer, нужен `super`. ❌ ПОСЛЕДСТВИЕ: попытка передать `Comparator<Animal>` в `sort(List<Cat>, ...)` ломается, потому что `Comparator<Animal>` не является `Comparator<? extends Cat>`.
> - [x] `Collections.sort` объявлен как `<T> void sort(List<T> list, Comparator<? super T> c)` — `Comparator` принимает (consume) элементы, поэтому `super`; это позволяет `Comparator<Animal>` сортировать `List<Cat>`. | ✓ ПРИМЕНЯТЬ: один общий `Comparator<Animal> byAge` сортирует `List<Cat>`, `List<Dog>` и `List<Animal>`; аналогично `Consumer<? super T>` в `forEach` принимает обработчик супертипа; `Predicate<? super T>` в `filter`. 📋 ПРАВИЛО: «функциональные интерфейсы-consumer (`Comparator`, `Consumer`, `Predicate`) → `? super T` для contravariance». 🔗 См. Q18 (PECS), Q20 (контравариантность), Q36 (extends vs super).
> - [ ] `Collections.sort` использует `Comparator<T>` без wildcards, потому что generic-метод сам параметризован `<T>`. | На практике `<? super T>`. ❌ ПОСЛЕДСТВИЕ: попытка передать `Comparator<Object> universal` в `sort(List<String>, universal)` отвергнута — но в реальном JDK с `? super T` это работает; разработчик пишет лишние generic methods.

## Q16. Что такое `Unbounded Wildcard` (`?`)?

`?` — неограниченный подстановочный знак, представляющий любой тип. Используется когда:
- Метод работает с `Object` и не зависит от параметра типа
- Метод использует только методы самой коллекции (`size()`, `clear()`, `isEmpty()`)

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

> [!mcq]
> - [ ] `List<?>` эквивалентен `List<Object>` — оба принимают любую коллекцию | Не так: `List<Object>` инвариантен и не принимает `List<String>`. ❌ ПОСЛЕДСТВИЕ: метод `void f(List<Object>)` отказывается принимать `List<String>` — compile error на каждом сайте вызова.
> - [ ] В `List<?>` можно `add(value)` — wildcard позволяет любые операции | Compiler разрешает только `add(null)`, других значений нельзя. ❌ ПОСЛЕДСТВИЕ: попытка `list.add("x")` ломает билд, рефакторинг с `List<String>` на `List<?>` падает.
> - [ ] `?` означает «нет типа», коллекция работает как raw `List` | Не так: `List<?>` сохраняет type-safety, raw — отключает все проверки. ❌ ПОСЛЕДСТВИЕ: путаница с raw приводит к unchecked warnings и `ClassCastException` в legacy.
> - [x] `List<?>` принимает `List` любого параметра, читает элементы как `Object`, запрещает `add` (кроме `null`) | Подходит для read-only API. ✓ ПРИМЕНЯТЬ: метод-printer `void log(List<?> any)`, `Collections.unmodifiableList(?)`. 📋 ПРАВИЛО: «`<?>` = read-only Object view». 🔗 См. Q17, Q40.

## Q17. В чём разница между `<T>` и `<?>`?

| Аспект | `<T>` (параметр типа) | `<?>` (wildcard) |
|--------|----------------------|------------------|
| Где используется | Объявление класса/метода | Аргумент типа |
| Можно ссылаться на тип | Да (`T value = ...`) | Нет |
| Ограничения | `extends`, `&` | `extends`, `super` |
| Связь между параметрами | Да | Нет |

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

> [!mcq]
> - [x] `<T>` — именованный параметр с возможностью ссылаться на тип в сигнатуре; `<?>` — анонимный wildcard для аргумента типа без возможности типизировать возврат | Разные роли: declaration vs use-site. ✓ ПРИМЕНЯТЬ: `<T> T first(List<T>)` для type-safe возврата, `void print(List<?>)` для агностичного API. 📋 ПРАВИЛО: «нужен возвращаемый тип — бери `<T>`». 🔗 См. Q16, Q40.
> - [ ] `<T>` и `<?>` взаимозаменяемы — это синтаксический сахар над одним и тем же | Не так: `<T>` связывает входы/выходы, `<?>` — нет. ❌ ПОСЛЕДСТВИЕ: метод `<T> List<T> wrap(T x)` нельзя выразить через `<?>` — теряется связь между параметром и возвратом.
> - [ ] `<?>` объявляется в классе/методе, а `<T>` — в аргументе типа | Перепутано наоборот: `<T>` — declaration, `<?>` — argument. ❌ ПОСЛЕДСТВИЕ: ошибочное `class Box<?> {}` не компилируется, путаница с синтаксисом.
> - [ ] `<T>` поддерживает `extends`, а `<?>` — нет | Оба поддерживают: `<T extends Number>` и `<? extends Number>`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `List<? extends Number>` и думает, что это запрещено, отказываясь от PECS.

## Q18. (!) Что такое `PECS` (`Producer Extends, Consumer Super`)?

**PECS** — мнемоника для выбора правильного wildcard при работе с коллекциями:
- **Producer Extends** — если коллекция **производит** (отдаёт) элементы → `? extends T`
- **Consumer Super** — если коллекция **потребляет** (принимает) элементы → `? super T`

```mermaid
graph LR
    subgraph "Producer Extends"
        P["Collection&lt;? extends T&gt;"] -->|"get() → T"| C["Наш код"]
    end
    subgraph "Consumer Super"
        C2["Наш код"] -->|"add(T)"| CS["Collection&lt;? super T&gt;"]
    end

    style P fill:#d4edda
    style CS fill:#d1ecf1
```

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

**Если коллекция одновременно и producer, и consumer** — используйте точный тип (без wildcard).

> [!mcq]
> - [x] `PECS` расшифровывается как «Producer Extends, Consumer Super»: коллекция-источник, из которой читаем `T`, объявляется как `<? extends T>`, а коллекция-приёмник, в которую пишем `T`, — как `<? super T>`. | Мнемоника фиксирует направление variance для wildcards. ✓ ПРИМЕНЯТЬ: `Collections.copy(List<? super T> dest, List<? extends T> src)`, `Stream.forEach(Consumer<? super T>)`, `addAll(Collection<? extends E>)` — типичные JDK-сигнатуры. 📋 ПРАВИЛО: «читаем — extends, пишем — super; смешать — точный `<T>`». 🔗 См. Q14, Q15, Q36.
> - [ ] `PECS` означает «Producer Super, Consumer Extends»: для чтения объявляем `<? super T>`, а для записи — `<? extends T>`. | Мнемоника развёрнута наоборот. ❌ ПОСЛЕДСТВИЕ: разработчик делает `addAll(Collection<? super E>)` — компилятор не даёт читать как `E`, только как `Object`; в `Collection<? extends E>` вызов `add(e)` падает «cannot be applied», API не работает.
> - [ ] `PECS` применим исключительно к интерфейсу `Collection` — для `Comparator`, `Function` и других функциональных интерфейсов wildcard не нужны, всегда хватает точного `<T>`. | Правило универсально для любого consumer/producer контракта. ❌ ПОСЛЕДСТВИЕ: команда пишет `Comparator<Cat>` вместо `Comparator<? super Cat>` — `Collections.sort(List<Cat>, comparator)` не принимает `Comparator<Animal>`, переиспользование сравнителей ломается.
> - [ ] Если коллекция одновременно и producer, и consumer, то PECS требует двойного wildcard: `<? extends T & ? super T>`. | Двойного wildcard не существует — для read+write используется точный `<T>`. ❌ ПОСЛЕДСТВИЕ: попытка `List<? extends Number & ? super Integer>` — compile error «'>' expected»; разработчик долго ищет синтаксис, пока не возвращается к простому `List<T>`.

> [!mcq]
> - [ ] `Producer Extends`, потому что `extends` физически открывает доступ к большему количеству методов производителя, чем `super`. | Дело в variance, не в API. ❌ ПОСЛЕДСТВИЕ: разработчик ищет «больше методов» в `? extends Number`, не находит, теряет доверие к мнемонике; настоящая причина — типобезопасность подстановки.
> - [ ] PECS работает потому, что `? extends T` выполняет автокаст элементов в T при добавлении, а `? super T` — при чтении. | Никакого автокаста нет, оба direction-а — compile-time запреты. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт runtime-конверсии, при чтении `Object` из `? super T` пытается каст в `T` без проверки и ловит ClassCastException.
> - [x] PECS — следствие subtyping rules: для producer `? extends T` любая `Collection<Sub>` (где `Sub <: T`) безопасно отдаёт `T` (covariance), но писать нельзя (компилятор не знает реальный `Sub`); для consumer `? super T` любая `Collection<Super>` безопасно принимает `T` (contravariance), но читать можно только как `Object`. | ✓ ПРИМЕНЯТЬ: чтобы понять, какой wildcard нужен — спросите «что я делаю в API: только читаю T (extends), только пишу T (super), или оба (точный T)?»; контр-пример показывает почему: `List<Cat> cats; List<? super Cat> = cats; cats` принимает `Cat`, но `get` возвращает только `Object` — Animal/Mammal предполагать нельзя. 📋 ПРАВИЛО: «PECS = covariance+contravariance: producer-cov-extends, consumer-contra-super; смешать → теряем typesafety». 🔗 См. Q19 (инвариантность), Q20 (variance), Q36.
> - [ ] PECS применяется только когда `T` — final-класс; для не-final типов нужна полная инвариантность `<T>`. | Применяется всегда. ❌ ПОСЛЕДСТВИЕ: разработчик отказывается от wildcards для `Number`, `Animal` («не final»), теряет совместимость API с подтипами; вся гибкость PECS пропадает.

## Q19. (!) Как дженерики работают с наследованием (инвариантность)?

Дженерики в Java **инвариантны**: `List<String>` **не является** подтипом `List<Object>`, хотя `String` является подтипом `Object`.

```java
List<String> strings = new ArrayList<>();
List<Object> objects = strings; // Compilation error!
```

**Почему это запрещено?** Если бы было разрешено:

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

> [!mcq]
> - [ ] Generic-типы в Java ковариантны: `List<String>` является подтипом `List<Object>`, потому что `String` — подтип `Object`. | Generics инвариантны намеренно. ❌ ПОСЛЕДСТВИЕ: разрешение такого присваивания дало бы дыру: `List<Object> objs = strings; objs.add(42); String s = strings.get(0);` — `ClassCastException` в runtime, поэтому компилятор блокирует assignment.
> - [ ] Generics ковариантны точно так же, как массивы: `List<String>` подтип `List<Object>`, как `String[]` подтип `Object[]`. | Generics инвариантны, массивы ковариантны — это разные подходы. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает `List<Object> = listOfStrings` и пишет API на этом допущении — refactoring ломает все вызывающие сайты, выясняется что generics закрывают дыру массивов compile-time проверкой.
> - [ ] Инвариантность распространяется и на wildcards: `List<String>` нельзя присвоить переменной `List<?>`, потому что `<?>` тоже инвариантен. | `<?>` ковариантен. ❌ ПОСЛЕДСТВИЕ: разработчик не использует `List<?>` для type-agnostic API, дублирует методы под `List<String>`/`List<Integer>` — теряет универсальность, хотя `List<?> w = listOfStrings` компилируется.
> - [x] Generic-типы в Java **инвариантны**: `List<String>` не является ни подтипом, ни супертипом `List<Object>`, несмотря на отношение `String <: Object`; для гибкости нужны wildcards `<? extends T>` (covariance) и `<? super T>` (contravariance). | Инвариантность защищает от runtime-дыры массивов на этапе компиляции. ✓ ПРИМЕНЯТЬ: API-методы с `List<? extends Number>` для чтения, `List<? super Integer>` для записи; для read+write — точный `List<T>`. Spring Data использует `Page<? extends T>` для иерархии Entity. 📋 ПРАВИЛО: «`List<Sub>` ≠ `List<Super>`; гибкость даёт wildcard, не assignment». 🔗 См. Q13, Q14, Q20.

> [!mcq]
> - [ ] `Object[] arr = new Integer[3]; arr[0] = "x";` компилируется, но **не выбросит исключение** — массив автоматически рекастится в `Object[]`. | Бросит `ArrayStoreException`. ❌ ПОСЛЕДСТВИЕ: разработчик кладёт чужой тип через ковариантную ссылку и удивляется падению в рантайме; неверная модель «массив теряет тип».
> - [ ] Массивы и generics в Java оба инвариантны, поэтому `Object[] = new Integer[]` — compile error. | Массивы ковариантны (legacy 1.0). ❌ ПОСЛЕДСТВИЕ: разработчик ожидает compile error, пишет лишний каст; на деле строка компилируется, баг прячется до runtime ArrayStoreException.
> - [x] Массивы ковариантны (`Integer[]` → `Object[]`), но эта дыра ловится в runtime через `ArrayStoreException`; generics инвариантны намеренно — Java закрывает аналогичную дыру **на этапе компиляции**, поэтому `List<Object> = new ArrayList<Integer>()` отвергается компилятором. | ✓ ПРИМЕНЯТЬ: для type-safe контейнеров предпочитайте `List<T>` массивам; если массив всё-таки нужен — храните `Object[]` явно или используйте `List<List<T>>` вместо `List<T>[]`; в API не смешивайте массивы и generics (`<T> T[] toArray(T[])` — единственный безопасный паттерн). 📋 ПРАВИЛО: «массив = runtime check (ArrayStoreException), generic = compile-time check (no assignment); generics строже для безопасности». 🔗 См. Q20 (variance), Q28 (`new T[]`), Q29 (`List<String>[]`).
> - [ ] `ArrayStoreException` бросается при чтении из ковариантного массива, а не при записи. | Бросается при записи. ❌ ПОСЛЕДСТВИЕ: разработчик защищает `arr[i] = ...` `try/catch` от `ClassCastException`, но не от ArrayStoreException; реальное место исключения — write, не read.

## Q20. Что такое ковариантность, контравариантность и инвариантность?

| Свойство | Описание | Java Generics | Java Arrays |
|----------|----------|---------------|-------------|
| **Ковариантность** | Если `A <: B`, то `F<A> <: F<B>` | `? extends T` | `String[] <: Object[]` |
| **Контравариантность** | Если `A <: B`, то `F<B> <: F<A>` | `? super T` | Нет |
| **Инвариантность** | `F<A>` и `F<B>` не связаны | `List<T>` | — |

```mermaid
graph TD
    subgraph "Иерархия типов"
        O["Object"] --> N["Number"]
        N --> I["Integer"]
        N --> D["Double"]
    end

    subgraph "Ковариантность (? extends Number)"
        LN["List&lt;? extends Number&gt;"]
        LI["List&lt;Integer&gt;"] -.->|подтип| LN
        LD["List&lt;Double&gt;"] -.->|подтип| LN
    end

    subgraph "Контравариантность (? super Integer)"
        LSI["List&lt;? super Integer&gt;"]
        LN2["List&lt;Number&gt;"] -.->|подтип| LSI
        LO["List&lt;Object&gt;"] -.->|подтип| LSI
    end
```

**Массивы в Java ковариантны** (в отличие от дженериков), что может приводить к `ArrayStoreException`:

```java
Object[] array = new String[3]; // OK — массивы ковариантны
array[0] = 42; // Компилируется, но ArrayStoreException в runtime!
```

> [!mcq]
> - [x] `<? extends T>` даёт **ковариантность** (`F<Sub> <: F<? extends Super>`), `<? super T>` — **контравариантность** (`F<Super> <: F<? super Sub>`), `List<T>` — **инвариантность**; массивы в Java ковариантны исторически (с Java 1.0), generics — инвариантны. | Variance определяет, какая иерархия типов сохраняется при подстановке параметра. ✓ ПРИМЕНЯТЬ: read-API — `List<? extends Number>` (covariance); write-API — `Consumer<? super T>` (contravariance); read+write — точный `List<T>`; массивы ковариантны → `ArrayStoreException` при `Object[] a = new Integer[3]; a[0] = "x"`. 📋 ПРАВИЛО: «Co = extends, Contra = super, In = точный `<T>`; arrays covariant (unsafe), generics invariant (safe)». 🔗 См. Q14, Q15, Q19.
> - [ ] Массивы в Java инвариантны, как и generics: `Object[] a = new String[3]` не компилируется по тем же правилам, что и `List<Object> = new ArrayList<String>()`. | Массивы ковариантны с Java 1.0. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает compile error для `Object[] a = new Integer[3]; a[0] = "x";` — на деле строка компилируется, баг прячется до `ArrayStoreException` в runtime, типичная ловушка array covariance.
> - [ ] Запись `Object[] a = new String[3]` иллюстрирует **контравариантность массивов** — массив супертипа подставляется вместо подтипа. | Это ковариантность (sub → super), не контравариантность. ❌ ПОСЛЕДСТВИЕ: путаница терминологии заставляет разработчика искать «контравариантность массивов» в JLS, тратить время; правильное название — covariance, и она именно поэтому даёт `ArrayStoreException`.
> - [ ] `<? extends T>` — это контравариантность (можно писать в коллекцию), а `<? super T>` — ковариантность (можно читать как T напрямую). | Направления development. ❌ ПОСЛЕДСТВИЕ: применение PECS наоборот: `addAll(Collection<? super E>)` → читаем `Object` вместо `E`, `Comparator<? extends T>` → не принимает `Comparator<Animal>` для `List<Cat>`; контракт API ломается на каждом шаге.

## Q21. (!) Что такое `Raw Type` и зачем избегать?

**Raw Type** — использование generic-класса без указания параметра типа. Существует для обратной совместимости с кодом, написанным до Java 5.

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

> [!mcq]
> - [ ] Raw type `List` и параметризованный `List<?>` эквивалентны — оба полностью отключают проверку типов компилятором. | Это разные конструкции с разной семантикой. ❌ ПОСЛЕДСТВИЕ: разработчик использует `List` ради «удобства» вместо `List<?>` — раздаёт unchecked warnings всему модулю, теряет защиту от heap pollution; на деле `<?>` запрещает `add(item)` (кроме `null`) на этапе компиляции.
> - [ ] Raw type больше не компилируется в Java 8+ — компилятор отвергает `List rawList = new ArrayList()` как ошибку. | Raw types сохранены ради backward compatibility. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает compile error, не настраивает `-Xlint:rawtypes` — legacy-код тихо собирается с unchecked warnings, баги уходят в production. Java 23+ сохраняет raw types для совместимости.
> - [ ] Присвоение `List<String> typed = rawList` запрещено компилятором — миграция legacy-кода всегда вынуждает явный cast. | Присвоение проходит с unchecked warning. ❌ ПОСЛЕДСТВИЕ: разработчик считает, что компилятор защитит его при миграции — на деле `typed.get(0)` бросит `ClassCastException`, если в `rawList` подложили `Integer`; нужны явные `Collections.checkedList` или `@SuppressWarnings` с обоснованием.
> - [x] **Raw type** отключает все generic-проверки и существует ради обратной совместимости с pre-Java 5 кодом; в современном API используйте `<?>` (read-only view с защитой) или конкретный параметр типа. | Raw type теряет typesafety и порождает heap pollution через unchecked операции. ✓ ПРИМЕНЯТЬ: миграция legacy — замена `List` на `List<?>` или `List<String>`; включение `-Xlint:rawtypes,unchecked` для аудита; Spring Data репозитории работают только с параметризованными `JpaRepository<User, Long>`. 📋 ПРАВИЛО: «raw — для pre-Java 5; `<?>` — для type-agnostic API; параметр — для типизированного API». 🔗 См. Q9, Q22, Q24.

> [!mcq]
> - [ ] `unchecked` и `rawtypes` — синонимы; `@SuppressWarnings("unchecked")` подавляет оба сразу. | Это два разных warning. ❌ ПОСЛЕДСТВИЕ: разработчик ставит только `unchecked` и удивляется, что компилятор продолжает ругаться на `List` (raw); приходится читать spec или комбинировать.
> - [ ] `rawtypes` — это `error`, а `unchecked` — `warning`; код с `rawtypes` не собирается. | Оба только warnings. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет лишние `@SuppressWarnings`, тратит время на «фикс» компиляции — на деле код собирался изначально.
> - [x] `rawtypes` (или `raw types`) — warning об **объявлении/использовании** raw type (`List rawList = ...`); `unchecked` — warning об **операции** без типовой проверки (`rawList.add(x)`, cast `(List<String>)` raw); подавляются раздельно через `@SuppressWarnings({"rawtypes", "unchecked"})`. | ✓ ПРИМЕНЯТЬ: при миграции legacy сначала включите `-Xlint:rawtypes,unchecked`, затем точечно `@SuppressWarnings({"rawtypes","unchecked"})` с комментарием почему безопасно (например, на JDK pre-generics API типа `Class.getDeclaredConstructors()`); IDE-quickfix чаще всего предложит оба сразу. 📋 ПРАВИЛО: «`rawtypes` = «вижу raw в типе», `unchecked` = «вижу операцию без проверки»; разные warning — разные `@SuppressWarnings`». 🔗 См. Q22, Q24 (heap pollution), Q25 (`@SafeVarargs`).
> - [ ] `@SuppressWarnings("unchecked")` нельзя использовать на поле — только на методе или классе. | Можно на поле, локальной переменной, методе, классе. ❌ ПОСЛЕДСТВИЕ: разработчик переносит `@SuppressWarnings` на класс целиком, заглушая warnings везде; реальная локальная проблема скрыта от ревью.

## Q22. Если при создании объекта не указан generic-тип, скомпилируется ли код?

Да, код скомпилируется, но с **предупреждениями компилятора** (unchecked warnings). Это обеспечивает обратную совместимость с кодом до Java 5:

```java
List list = new ArrayList();       // raw type — предупреждение
list.add("text");

List<String> typed = list;         // unchecked assignment — предупреждение
String s = typed.get(0);           // работает, но не гарантировано
```

Хотя обратная совместимость и стирание типов позволяют опускать параметры типа, это **плохая практика**. Всегда указывайте параметр типа или хотя бы `<?>`.

> [!mcq]
> - [ ] Код с raw type не скомпилируется — компилятор требует обязательного указания параметра типа `<>` для всех generic-классов. | Raw types — валидный синтаксис ради backward compatibility. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает compile error при импорте legacy-зависимости с raw `List` — на деле модуль собирается, в build-логах теряются unchecked warnings, продакшен ловит `ClassCastException` от чужих библиотек pre-Java 5.
> - [ ] Код скомпилируется без предупреждений, потому что Java 8+ автоматически выводит параметр типа из контекста использования. | Компилятор выдаёт `unchecked`/`rawtypes` warnings. ❌ ПОСЛЕДСТВИЕ: команда отключает warning-фильтры в IDE «потому что Java сама всё выводит» — реальные баги типа `list.add(42)` в `List` (raw, потом приведённый к `List<String>`) уходят в продакшен как `ClassCastException`.
> - [ ] Код скомпилируется и компилятор автоматически подставит `<?>` вместо отсутствующего параметра типа. | Raw type и `<?>` — разные сущности с разной семантикой. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `List list = ...; list.add("x")` ожидая что компилятор подставит `<?>` (где `add` запрещён) — вместо этого получает raw type без проверок и unchecked warning, защита от heap pollution отсутствует.
> - [x] Код **скомпилируется**, но компилятор выдаст **unchecked / rawtypes warnings** ради обратной совместимости с pre-Java 5; warnings — это будущие баги, а не безобидный шум. | Raw type оставлен в языке для миграции legacy-кода. ✓ ПРИМЕНЯТЬ: запускайте сборку с `-Xlint:unchecked,rawtypes -Werror` в CI чтобы warnings проваливали билд; в legacy-миграции замените `List` на `List<?>` или `List<String>`; для рефлексии и framework-кода — точечный `@SuppressWarnings({"rawtypes","unchecked"})` с комментарием. 📋 ПРАВИЛО: «warnings ≠ errors, но warnings → будущие баги; включай `-Werror` в CI». 🔗 См. Q21, Q24, Q31.

## Q23. (!) Что такое `Bridge Methods` и зачем они в дженериках?

**Bridge method** — синтетический метод, который компилятор генерирует для сохранения полиморфизма после стирания типов.

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

> [!mcq]
> - [x] **Bridge method** — synthetic-метод, генерируемый компилятором автоматически для сохранения полиморфизма после type erasure: например, при `class MyDate implements Comparable<MyDate>` рядом с `compareTo(MyDate)` появляется bridge `compareTo(Object)`, который кастует и делегирует. | Без bridge polymorphic dispatch через интерфейс `Comparable` (после erasure — `compareTo(Object)`) не нашёл бы пользовательский метод. ✓ ПРИМЕНЯТЬ: при reflection (Spring AOP, Mockito, Hibernate proxy) фильтруйте через `Method.isBridge() || Method.isSynthetic()`, иначе advice применится дважды; в stack trace bridge виден как «лишний» frame с тем же именем. 📋 ПРАВИЛО: «erasure ломает override → bridge восстанавливает; всегда `synthetic + bridge` в bytecode». 🔗 См. Q6, Q7, Q24.
> - [ ] Bridge-метод пишется разработчиком вручную в подклассе для совместимости с pre-Java 5 кодом. | Bridge генерируется компилятором, его нельзя написать руками. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `@Override public int compareTo(Object o)` рядом со своим `compareTo(MyDate)` — compile error «method does not override», или duplicate erasure; теряется час на поиск проблемы пока не запустит `javap -c -p`.
> - [ ] Bridge-методы нужны для создания массивов generic-типа: при `T[] arr = new T[10]` компилятор генерирует bridge для приведения `Object[]` к `T[]`. | Это разные механизмы — `new T[]` запрещён компилятором. ❌ ПОСЛЕДСТВИЕ: разработчик ищет «bridge для массивов» в JLS, теряет время; реальное решение — `Array.newInstance(Class<T>, n)` с явным `Class<T>` token, bridge тут ни при чём.
> - [ ] Bridge-методы существуют только для интерфейса `Comparable<T>` — это специальный случай в JDK для сортировки. | Bridge генерируется для любого override generic-метода. ❌ ПОСЛЕДСТВИЕ: разработчик не учитывает bridge для `class IntList extends ArrayList<Integer>` — Mockito-spy теряет stub-ы, Spring AOP применяет advice дважды (на bridge `add(Object)` и реальный `add(Integer)`), долгая отладка дублирующихся вызовов.

> [!mcq]
> - [ ] Bridge-методы генерируются только для generic интерфейсов; для **covariant return** (`class Sub extends Super { @Override Sub clone() }`) bridge не нужен, потому что это feature Java 1.5 без erasure. | Bridge генерируется и для covariant return. ❌ ПОСЛЕДСТВИЕ: разработчик не находит `Object clone()` в `getDeclaredMethods()` своего класса и считает, что override отсутствует — на деле bridge `Object clone()` присутствует и делегирует к `Sub clone()`.
> - [ ] Bridge-методы вызываются только через рефлексию; обычный `obj.compareTo(other)` обходит bridge напрямую через linker. | Polymorphic call идёт через bridge. ❌ ПОСЛЕДСТВИЕ: разработчик вычисляет «горячие пути» без учёта bridge-перенаправления, неправильно профилирует; реально bridge участвует в polymorphic call через интерфейс с erased сигнатурой.
> - [x] Bridge-методы генерируются в двух случаях: (1) erasure-несовпадение override (`compareTo(MyDate)` vs erased `compareTo(Object)`); (2) **covariant return** — переопределение метода с более специфичным типом возврата (`Sub clone()` поверх `Object clone()` создаёт synthetic `Object clone()`, делегирующий к `Sub clone()`). | ✓ ПРИМЕНЯТЬ: при анализе stack trace синтетические frame-ы — это bridge (имя метода то же, но `isBridge() == true`); фреймворки Spring AOP, Mockito, Hibernate proxy фильтруют bridge через `Method.isBridge()`/`isSynthetic()`, иначе advice применится дважды. 📋 ПРАВИЛО: «bridge = compiler-generated tunnel: erasure-override + covariant return; всегда `isBridge() && isSynthetic()`». 🔗 См. Q23, Q6 (erasure), Q7 (преобразование).
> - [ ] Bridge-метод имеет модификатор `private synthetic`, чтобы скрыть его от наследников. | Bridge помечен как `public synthetic bridge`. ❌ ПОСЛЕДСТВИЕ: разработчик пытается переопределить bridge `private`-методом в подклассе и получает «cannot override» или dispatch на «не тот» метод.

## Q24. Что такое `Heap Pollution`?

**Heap Pollution** (загрязнение кучи) — ситуация, когда переменная параметризованного типа ссылается на объект, не являющийся этим параметризованным типом. Возникает при смешивании raw types и generic types.

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

> [!mcq]
> - [ ] Heap pollution — это переполнение кучи (`OutOfMemoryError`) при работе с большими generic-коллекциями. | Heap pollution не имеет отношения к OOM. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `-Xmx` ради «починки» heap pollution warnings — никакого эффекта, реальная проблема (типовое несоответствие через raw types) остаётся; в логах продолжают появляться `ClassCastException`.
> - [x] **Heap pollution** — ситуация, когда переменная параметризованного типа `List<T>` ссылается на объект другого generic-типа (через raw type или unchecked cast); компилятор предупреждает, runtime ловит `ClassCastException` при первом обращении к элементу. | Возникает на стыке raw и parameterized типов или внутри generic varargs. ✓ ПРИМЕНЯТЬ: код-ревью отлавливает смешивание `List` и `List<String>`; `@SafeVarargs` ставится только если массив не утекает наружу (`List.of(T...)` — OK, `T[] toArray(T...)` — нет); включайте `-Xlint:unchecked` в build.gradle. 📋 ПРАВИЛО: «raw + parameterized = heap pollution; CCE только при доступе, не при cast». 🔗 См. Q21, Q22, Q25.
> - [ ] `@SafeVarargs` можно ставить на любой метод с varargs — компилятор сам проверит безопасность. | Аннотация ограничена `static`, `final` и `private` методами. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `@SafeVarargs` на нестатический не-final метод — compile error «Invalid SafeVarargs annotation», команда отключает аннотацию через `@SuppressWarnings("unchecked")` массово, теряет защиту от реальной heap pollution.
> - [ ] Heap pollution возникает исключительно в varargs-методах с generic-параметром, в обычном коде её не бывает. | Возникает везде, где raw types смешиваются с parameterized. ❌ ПОСЛЕДСТВИЕ: разработчик уверен что `List<String> s = (List<String>) rawList; rawList.add(42);` безопасно — на деле это классическая heap pollution без varargs; `s.get(0)` на `Integer` бросит `ClassCastException` вне зоны внимания code review.

## Q25. Как работают дженерики с `varargs` (`T... args`)?

При вызове `method(T... args)` создаётся массив. Из-за стирания типов создаётся `Object[]` вместо `T[]`, что вызывает предупреждение о heap pollution:

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

> [!mcq]
> - [x] При вызове `<T> void m(T... args)` JVM из-за type erasure создаёт массив `Object[]` под видом `T[]`; метод **безопасен** только если массив не утекает наружу — в этом случае допустимо `@SafeVarargs` на `static`, `final` или `private` методах. | Generic varargs всегда порождают потенциальную heap pollution. ✓ ПРИМЕНЯТЬ: `List.of(T...)`, `Arrays.asList(T...)`, `Collections.addAll(Collection<? super T> c, T... a)` — массив остаётся внутри, помечены `@SafeVarargs`; в своём коде помечайте только если args не возвращается, не сохраняется в поле и не передаётся «голым» наружу. 📋 ПРАВИЛО: «`@SafeVarargs` = массив остаётся внутри метода; static/final/private; не возвращать args наружу». 🔗 См. Q24, Q31.
> - [ ] При `T... args` JVM создаёт честный массив `T[]` с реальным компонентным типом `T` в runtime, поэтому `T[] r = args; return r;` всегда безопасен. | Из-за erasure компонентный тип = `Object`, не `T`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `static <T> T[] toArray(T... a) { return a; }`, вызов `String[] r = toArray("a", "b")` бросает `ClassCastException` — на деле возвращён `Object[]`, который нельзя кастить к `String[]`.
> - [ ] `@SafeVarargs` гарантирует runtime-безопасность метода — компилятор сам проверяет, что массив не утекает наружу, и блокирует unsafe варианты. | Аннотация только подавляет warning, проверки делает разработчик. ❌ ПОСЛЕДСТВИЕ: разработчик навешивает `@SafeVarargs` на `T[] toArray(T...)` ожидая защиту — на деле компилятор молчит, в продакшене ловится `ClassCastException` при `String[] r = ...`; правильный путь — code review + `Collections.addAll`-style методы.
> - [ ] Возврат `return args;` из generic varargs-метода всегда безопасен — это рекомендуемый паттерн в JDK для создания generic-массивов. | Возврат args наружу нарушает type-safety. ❌ ПОСЛЕДСТВИЕ: разработчик копирует «паттерн» в библиотечный API, клиенты вызывают `Integer[] nums = lib.toArray(1, 2)` и ловят `ClassCastException`; в JDK таких сигнатур нет — используется `<T> T[] toArray(IntFunction<T[]> generator)` с явным fabric массива.

## Q26. Дженерики в статических методах и полях?

**Статическое поле** не может использовать параметр типа класса, потому что статическое поле одно на все экземпляры (а параметр типа разный для `MyClass<String>` и `MyClass<Integer>`):

```java
public class Box<T> {
    private static T value;      // Compilation error!
    private static List<T> list; // Compilation error!
}
```

**Статический метод** может быть generic — но со своим собственным параметром типа:

```java
public class Utils {
    // Свой параметр типа T, не связанный с классом
    public static <T> T firstElement(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}
```

> [!mcq]
> - [ ] Статическое поле `private static T value` в `class Box<T>` разрешено — компилятор создаёт отдельное поле для каждой параметризации (`Box<String>.value`, `Box<Integer>.value`). | Все параметризации делят одно `Class<Box>` после erasure. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает «специализацию» полей по типу — `Box<String>.value` и `Box<Integer>.value` физически одно поле, попытка компиляции падает с «non-static class T cannot be referenced from static context», бизнес-логика на static-полях не работает.
> - [x] **Статическое поле** не может использовать параметр типа класса (одно `Class<Box>` после erasure → одно поле на все параметризации), но **статический метод** может быть generic со своим собственным параметром типа `<T>`, объявленным до возвращаемого типа. | Class-level type parameter живёт в инстансе, static-контекст инстанс не имеет. ✓ ПРИМЕНЯТЬ: `Collections.<T>emptyList()`, `Optional.<T>of(value)`, `Stream.<T>of(t)` — все static generic methods со своим `<T>`; для поля общего на все параметризации — используйте `Object` или передавайте `Class<T>` token в конструктор. 📋 ПРАВИЛО: «static field — без `<T>` класса; static method — со своим `<T>`». 🔗 См. Q3, Q6, Q26.
> - [ ] Generic-метод обязан использовать параметр типа класса (`class Box<T>` → `void method() { T x; }`); собственный `<T>` в сигнатуре метода — синтаксическая ошибка. | Generic-метод объявляет свой `<T>`, отдельно от класса. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `static T firstOrNull(List<T> list)` без `<T>` перед возвратом — compile error «cannot find symbol T»; команда копирует паттерн в utility-классы, теряет статичность, переходит на инстансы зря.
> - [ ] Все методы параметризованного класса автоматически generic — компилятор подставляет `<T>` класса в каждый метод. | Generic — только методы с явным `<T>` в сигнатуре. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт что `static List<T> empty()` в `class Box<T>` скомпилируется — на деле compile error, нужно `static <T> List<T> empty()` со своим параметром; static-методы не наследуют class-level type parameter.

## Q27. (!) Можно ли использовать примитивы как generic-тип?

**Нет.** Параметр типа стирается в `Object`, а примитивы не являются подтипами `Object`. Необходимо использовать **классы-обёртки**:

```java
List<int> ints = new ArrayList<>();      // Compilation error!
List<Integer> ints = new ArrayList<>();  // OK — autoboxing

Map<String, double> map = new HashMap<>();     // Compilation error!
Map<String, Double> map = new HashMap<>();     // OK
```

**Влияние на производительность:** autoboxing/unboxing создаёт объекты-обёртки, что увеличивает потребление памяти и нагрузку на GC. Для критичных сценариев используйте специализированные коллекции:

```java
// JDK — примитивные стримы без боксинга
IntStream.range(0, 1000).sum();
LongStream.of(1L, 2L, 3L).average();

// Eclipse Collections, Trove — примитивные коллекции
// IntArrayList, LongHashSet и т.д.
```

Подробнее о примитивах и обёртках — в [вопросах по системе типов Java](java-types-interview.md).

> [!mcq]
> - [ ] `List<int>` компилируется в Java 8+ — компилятор автоматически применяет autoboxing к параметру типа и подставляет `Integer`. | Autoboxing работает на значениях, не на параметрах типа. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт от компилятора «магию» — на деле `List<int>` сразу даёт «unexpected type: required reference, found int», команда тратит время на поиск опции компилятора, хотя достаточно `List<Integer>`.
> - [ ] Примитивы запрещены в generic, потому что у них нет методов `equals()` и `hashCode()` — а generic-код требует эти методы для коллекций. | Реальная причина — erasure в `Object`. ❌ ПОСЛЕДСТВИЕ: неверная модель приводит к попыткам «починить» через wrapper-типы только в тех местах где явно нужен `equals` — на деле любой generic с примитивом запрещён независимо от использования; разработчик усложняет API без причины.
> - [ ] Примитивы можно использовать в generic с Java 17+ через Project Valhalla — `List<int>` уже работает в production. | Valhalla в разработке, ещё не релизнут. ❌ ПОСЛЕДСТВИЕ: разработчик пытается `List<int>` в Java 21 ожидая что Valhalla релизнут — compile error, в Java 17/21 примитивы по-прежнему запрещены; Valhalla preview доступен только в EA-сборках через `--enable-preview`.
> - [x] Примитивы **запрещены** как параметры generic-типа: после erasure `T` заменяется на `Object`, а примитивы не являются подтипами `Object`; используйте wrapper-классы (`Integer`, `Long`) или специализированные API без боксинга (`IntStream`, Eclipse Collections `IntArrayList`, Trove `TIntList`). | Autoboxing-overhead на hot-path может стоить 10-30% throughput. ✓ ПРИМЕНЯТЬ: `List<Integer>`, `Map<String, Long>` — обычные коллекции; для performance-critical — `IntStream.range(0, N).sum()`, `LongStream`, Eclipse Collections; Project Valhalla обещает `List<int>` в будущем. 📋 ПРАВИЛО: «generics → только reference types; primitives → wrappers или specialized streams». 🔗 См. Q6, Q9, Q35.

## Q28. Можно ли создать массив generic-типа (`new T[]`)?

**Нельзя:** `new T[]` не компилируется, потому что T стирается в `Object` и JVM не может создать массив неизвестного типа.

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

> [!mcq]
> - [ ] `new T[10]` компилируется и автоматически создаёт `Object[10]` без предупреждений — это стандартный паттерн в JDK. | Запрещено компилятором как «generic array creation». ❌ ПОСЛЕДСТВИЕ: разработчик пишет `T[] arr = new T[size]` в библиотечном коде — compile error, команда обходит через `(T[]) new Object[size]` без `@SuppressWarnings` и комментария, ревью пропускает unsafe cast, потенциальная утечка наружу.
> - [ ] `(T[]) new Object[10]` — полностью безопасный приём, эквивалентный гипотетическому `new T[10]` без всяких рисков. | Cast несбалансирован — реальный тип массива `Object[]`. ❌ ПОСЛЕДСТВИЕ: метод `T[] toArray() { return (T[]) new Object[size]; }` ломается на стороне вызывающего: `String[] s = box.toArray()` — `ClassCastException`; внутри класса безопасно (читаем как T), но возврат наружу — heap pollution.
> - [ ] `Array.newInstance(Class<T>, n)` запрещён в generic-классах — это рефлексия, она работает только в utility static factory методах. | Это стандартный паттерн в JDK. ❌ ПОСЛЕДСТВИЕ: разработчик отказывается от рабочего решения и реализует свой `(T[]) new Object[]` cast — внутри класса работает, но при возврате `T[]` наружу падает с `ClassCastException`; на деле `ArrayList.toArray(T[])` использует `Array.newInstance` именно для type-safe создания.
> - [x] `new T[]` **запрещён** компилятором — после erasure JVM не знает компонентный тип; обходные пути: `(T[]) new Object[n]` с `@SuppressWarnings("unchecked")` (только для внутреннего хранения), `Array.newInstance(Class<T>, n)` через переданный `Class<T>` token, либо использовать `List<T>` как замену. | Решение зависит от того, утекает ли массив наружу. ✓ ПРИМЕНЯТЬ: для коллекций — `ArrayList<T>`; для возврата наружу — `Array.newInstance` с `Class<T>` token (как в `ArrayList.toArray(T[])`); для внутреннего хранения — `(T[]) new Object[]` с комментарием почему безопасно; в Java 11+ `Stream.toArray(IntFunction)` элегантнее. 📋 ПРАВИЛО: «generic array → reflection (Class<T>) или List; cast Object[] — только internal». 🔗 См. Q9, Q24, Q29.

## Q29. Почему нельзя создать `List<String>[]`?

Массивы в Java **ковариантны** и знают свой тип элемента в runtime (reifiable). `List<String>` после стирания — это просто `List` (non-reifiable). Если бы `List<String>[]` был разрешён, можно было бы нарушить типобезопасность:

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

> [!mcq]
> - [ ] `List<String>[]` запрещён, потому что `List` — это интерфейс, а массивы интерфейсов в Java вообще запрещены. | Массивы интерфейсов разрешены — `List[] arr = new List[10]` (raw) компилируется. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Comparator[] comps = new Comparator[N]` ожидая запрет — на деле компилируется как raw, теряется type-safety; реальная причина запрета `List<String>[]` — не интерфейс, а сочетание array covariance и non-reifiable.
> - [x] Массивы в Java **ковариантны и reifiable** (хранят компонентный тип в runtime), а `List<String>` после erasure — **non-reifiable**; разрешить `new List<String>[N]` означало бы дать обход type-safety: `Object[] o = new List<String>[1]; o[0] = List.of(42); String s = arr[0].get(0)` → `ClassCastException`. | Запрет защищает invariant generic от array covariance. ✓ ПРИМЕНЯТЬ: вместо `List<String>[]` — `List<List<String>>` (полностью typesafe); если массив необходим — `(List<String>[]) new List[N]` с `@SuppressWarnings` и комментарием, аккуратно используется в `ArrayList`-internals. 📋 ПРАВИЛО: «массив + non-reifiable = compile-error; List<List<T>> вместо List<T>[]». 🔗 См. Q9, Q19, Q28.
> - [ ] Можно создать `new List<?>[10]`, но `new List<String>[10]` запрещён — компилятор делает исключение для unbounded wildcard потому что `<?>` менее строгий. | Дело не в строгости, а в reifiability: `List<?>` reifiable, `List<String>` — нет. ❌ ПОСЛЕДСТВИЕ: разработчик считает что `<?>` — это «упрощённый` raw` — на деле `List<?>` сохраняет проверку (нельзя `add` кроме `null`); путаница приводит к выбору raw `List` где можно `List<?>` массив.
> - [ ] Запрет на `List<String>[]` действует только в Java 8+, в Java 11+ его сняли благодаря улучшениям type system. | Запрет постоянный с момента введения generics. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `List<String>[] arr = new List<String>[10]` в Java 21 ожидая что «новые версии разрешили» — compile error, теряется час на поиск release-notes; правильный workaround — `List<List<String>>`.

## Q30. Как получить `Class<T>` для generic-типа в runtime?

Из-за стирания типов `T` недоступен в runtime. Основные подходы:

**1. Передать `Class<T>` явно (самый надёжный):**

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

**2. Рефлексия через подкласс:**

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

**3. TypeReference (Jackson) / ParameterizedTypeReference (Spring):**

```java
// Jackson
List<User> users = objectMapper.readValue(json,
    new TypeReference<List<User>>() {});

// Spring RestTemplate
List<User> users = restTemplate.exchange(url, HttpMethod.GET, null,
    new ParameterizedTypeReference<List<User>>() {}).getBody();
```

> [!mcq]
> - [x] Из-за erasure `T.class` запрещён компилятором; `Class<T>` нужно либо передавать явно через конструктор/метод (`new Repository<>(User.class)`), либо извлекать через рефлексию анонимного подкласса (`getClass().getGenericSuperclass()` → `ParameterizedType.getActualTypeArguments()[0]`), либо использовать **Super Type Token** (`new TypeReference<List<User>>(){}`). | Каждый из трёх подходов решает свою задачу. ✓ ПРИМЕНЯТЬ: Spring Data JPA — `Repository<T, ID>` принимает `Class<T>` через `JpaRepositoryFactory`; Jackson — `objectMapper.readValue(json, new TypeReference<List<User>>(){})`; Spring REST — `restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<User>>(){})`; Hibernate `Session.get(User.class, id)` — самый простой случай. 📋 ПРАВИЛО: «Class<T> явно (простой), getGenericSuperclass (для базовых классов), Super Type Token (для composite generic)». 🔗 См. Q8, Q9, Q33.
> - [ ] `T.class` доступен напрямую внутри generic-метода — компилятор подставит реальный тип в runtime через специальный механизм. | После erasure `T` физически отсутствует. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `new ObjectMapper().readValue(json, T.class)` — compile error «cannot select from a type variable»; команда тратит время на «как заставить рефлексию увидеть T», правильный путь — передать `Class<T>` явно или использовать `TypeReference`.
> - [ ] `getClass().getGenericSuperclass()` всегда возвращает `Class<T>` напрямую без приведения типа — это типобезопасный API в JDK. | Возвращает `Type`, нужен cast к `ParameterizedType`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Class<T> c = (Class<T>) getClass().getGenericSuperclass()` — `ClassCastException` в runtime, потому что `Class.class.getGenericSuperclass()` возвращает `Class<Object>`, а параметризованный — `ParameterizedTypeImpl`; нужен caster к `ParameterizedType` и доступ через `getActualTypeArguments()[0]`.
> - [ ] `TypeReference` из Jackson — это утилита для логирования типов в debug-целях, не для десериализации; для коллекций используется обычный `List.class`. | TypeReference критически важен для generic-десериализации. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `objectMapper.readValue(json, List.class)` для `List<User>` — получает `List<LinkedHashMap>` (Jackson default mapping); попытка `User u = list.get(0)` падает с `ClassCastException`, добавляется ручное `mapper.convertValue(map, User.class)` для каждого элемента — медленно и error-prone.

## Q31. Что такое `@SuppressWarnings("unchecked")` и когда использовать?

`@SuppressWarnings("unchecked")` отключает предупреждения компилятора о непроверяемых приведениях типов, связанных с дженериками.

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

**Best practices:**
1. Применяйте на **минимальной области** — на переменную, не на весь метод или класс
2. **Всегда** добавляйте комментарий, объясняющий безопасность приведения
3. По возможности рефакторьте код, чтобы убрать необходимость в подавлении

```java
@SuppressWarnings("unchecked") // безопасно: deserialize всегда возвращает T для данного Class<T>
T result = (T) deserializer.deserialize(data, targetClass);
```

> [!mcq]
> - [ ] `@SuppressWarnings("unchecked")` отключает runtime-проверки типов — после её установки `ClassCastException` не выбрасывается. | Аннотация только compile-time. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `@SuppressWarnings` ради «защиты» от CCE — на деле runtime-checkcast никуда не уходит, исключения продолжают лететь, а warning теперь скрыт от code review; реальная проблема (unchecked cast) маскируется.
> - [ ] Лучше ставить `@SuppressWarnings("unchecked")` на весь класс — это удобнее, чем точечно на каждую переменную. | Принцип минимальной области. ❌ ПОСЛЕДСТВИЕ: новый код с unchecked cast не выдаёт warning (классовая аннотация скрывает всё), legitimate проблемы попадают в продакшен; включение `-Xlint` в CI бесполезно — всё подавлено на классе.
> - [ ] Без `@SuppressWarnings` код с unchecked cast не компилируется — компилятор требует обязательного подавления warning. | Warning ≠ error, компиляция проходит. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `@SuppressWarnings("unchecked")` везде «для компиляции» — теряет визуальный сигнал о потенциальных проблемах, code review не замечает unsafe casts; правильный подход — `-Xlint:unchecked -Werror` в CI делает warnings ошибками.
> - [x] `@SuppressWarnings("unchecked")` подавляет только **compile-time warnings**; ставится на минимальной области (локальная переменная > метод > класс), и **всегда** сопровождается комментарием с обоснованием почему cast безопасен. | Runtime-checkcast остаётся — аннотация защищает только глаза разработчика и сборку, не runtime. ✓ ПРИМЕНЯТЬ: `(T[]) new Object[n]` внутри generic-класса (с комментарием «массив не утекает наружу»); legacy API после миграции; deserializer возвращает `T` для известного `Class<T>`; в Mockito и Spring AOP проксях — точечно. 📋 ПРАВИЛО: «локально + комментарий-обоснование; никогда на классе массово». 🔗 См. Q21, Q22, Q24.

## Q32. Как сериализовать объекты с generic-полями?

При сериализации generic-информация теряется (стирание типов). Для корректной десериализации нужно передавать информацию о типе явно.

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

> [!mcq]
> - [ ] Сериализация generic-полей работает корректно через стандартный `ObjectInputStream.readObject()` — Java сама сохраняет generic-информацию в потоке. | `ObjectInputStream` сохраняет fully qualified имя класса, но не parameterized тип. ❌ ПОСЛЕДСТВИЕ: разработчик использует встроенную сериализацию для `Wrapper<User>`, при десериализации в другом JVM поле `T data` имеет тип `Object`; ручное приведение `(User) wrapper.getData()` работает, но через Jackson/JSON ту же проблему «запирают» неверно (`LinkedHashMap` вместо `User`).
> - [ ] `objectMapper.readValue(json, Wrapper.class)` корректно десериализует `Wrapper<User>` — Jackson достаточно умён, чтобы вывести generic-параметр из JSON-структуры. | Без TypeReference Jackson читает generic поля как `LinkedHashMap`. ❌ ПОСЛЕДСТВИЕ: REST-эндпоинт `/api/users` возвращает `Wrapper<User>`, контроллер делает `mapper.readValue(json, Wrapper.class)` — `wrapper.getData()` возвращает `LinkedHashMap`, последующий `User user = (User) wrapper.getData()` ловит `ClassCastException` в продакшене.
> - [ ] `Class<Wrapper<User>>` можно получить напрямую через `Wrapper<User>.class` — это синтаксис Java 11+. | `.class` работает только с raw-классами. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Class<Wrapper<User>> c = Wrapper<User>.class` — compile error «cannot select from parameterized type»; команда не находит обходного пути и переходит на raw `Wrapper.class`, теряя type-safety при десериализации.
> - [x] При сериализации generic-типов через JSON нужно использовать **Super Type Token** — `Jackson TypeReference<Wrapper<User>>(){}`, `Gson TypeToken<Wrapper<User>>(){}.getType()` или `Spring ParameterizedTypeReference<Wrapper<User>>(){}` — анонимный подкласс фиксирует тип в метаданных через `getGenericSuperclass()`. | Без Super Type Token Jackson десериализует `T` в `LinkedHashMap`. ✓ ПРИМЕНЯТЬ: Jackson `readValue(json, new TypeReference<List<User>>(){})` для коллекций; Spring WebFlux `WebClient.bodyToMono(new ParameterizedTypeReference<List<User>>(){})`; Gson `gson.fromJson(json, new TypeToken<Map<String, List<User>>>(){}.getType())`. 📋 ПРАВИЛО: «generic JSON → Super Type Token; анонимный subclass обязателен (`{}`)». 🔗 См. Q8, Q30, Q33.

## Q33. (!) Что такое `Super Type Token` и как это используется?

**Super Type Token** — паттерн для сохранения информации о generic-типе в runtime, обходящий стирание типов. Основан на том, что при наследовании с конкретным generic-аргументом тип сохраняется в метаданных класса.

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

> [!mcq]
> - [ ] Super Type Token использует `T.class` напрямую внутри generic-метода, обходя type erasure через специальный механизм компилятора. | `T.class` запрещён компилятором всегда. ❌ ПОСЛЕДСТВИЕ: разработчик пытается реализовать аналог Jackson `TypeReference` через `T.class` — compile error «cannot select from a type variable»; неделя на поиск работающего решения, хотя достаточно понять что pattern основан на `getGenericSuperclass()` подкласса.
> - [ ] Анонимный подкласс с `{}` не нужен — достаточно вызвать `new TypeReference<List<User>>()` без фигурных скобок. | TypeReference абстрактный, `{}` обязательны для создания подкласса. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `new TypeReference<List<User>>()` — compile error «is abstract; cannot be instantiated»; даже если бы класс был неабстрактным, без `{}` нет нового класса с заполненной generic-сигнатурой, `getGenericSuperclass()` вернул бы `Object`.
> - [ ] Super Type Token хранит generic-тип через `Thread.currentThread().getContextClassLoader()` — classloader сохраняет parameterized метаданные в специальной таблице. | Тип хранится в bytecode конкретного класса, classloader не при чём. ❌ ПОСЛЕДСТВИЕ: разработчик пытается переопределить classloader для «доступа к generic типу» — теряет день на debugging classloader hierarchy; реальное место — `Signature` attribute в bytecode подкласса, доступный через рефлексию.
> - [x] **Super Type Token** (паттерн Neal Gafter) — анонимный подкласс `new TypeReference<List<User>>(){}` фиксирует параметризованный тип в **bytecode-сигнатуре** подкласса; в runtime `getClass().getGenericSuperclass()` возвращает `ParameterizedType`, откуда `getActualTypeArguments()[0]` извлекает `List<User>`. | Подкласс необходим — type-параметры родителя сохраняются в `Signature` attribute классов-наследников. ✓ ПРИМЕНЯТЬ: Jackson `objectMapper.readValue(json, new TypeReference<List<User>>(){})`; Spring REST `restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<User>>(){})`; Guice `Key.get(new TypeLiteral<Map<String, User>>(){})`; Guava `TypeToken<List<String>>`. 📋 ПРАВИЛО: «`{}` в конце обязательны = новый подкласс = тип сохраняется в Signature». 🔗 См. Q8, Q30, Q32.

## Q34. Дженерики и `instanceof` — какие проверки допустимы?

Из-за стирания типов `instanceof` с параметризованным типом **невозможен**:

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

> [!mcq]
> - [ ] `obj instanceof List<String>` компилируется и проверяет тип элементов в runtime через специальный механизм generics. | Запрещён компилятором как «cannot perform instanceof check against parameterized type». ❌ ПОСЛЕДСТВИЕ: разработчик пишет `if (obj instanceof List<String>) process(...)` — compile error, команда обходит через unchecked cast `if (obj instanceof List l && !l.isEmpty() && l.get(0) instanceof String) ...` без понимания что type erasure делает такую проверку структурно невозможной.
> - [ ] `obj instanceof List<?>` запрещён компилятором — для type-checks разрешён только raw `instanceof List` (с warning). | `<?>` reifiable и предпочтительнее raw. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `if (obj instanceof List)` — компилируется с rawtypes warning; команда отключает warning массово, теряет защиту; правильный путь `instanceof List<?>` без warnings и type-safety сохраняется.
> - [x] `instanceof` с параметризованным типом запрещён (non-reifiable), но разрешён с **reifiable** формами: `instanceof List<?>`, `instanceof List` (raw, с warning), `instanceof ArrayList<?>`; pattern matching Java 16+ работает с теми же ограничениями (`obj instanceof List<?> list`). | Erasure стирает аргументы типа, но raw type и `<?>` сохраняют структуру. ✓ ПРИМЕНЯТЬ: для проверки элементов — итерация с `type.isInstance(item)` или `Collections.checkedList(list, String.class)` для runtime-валидации; pattern matching Java 21 — `if (obj instanceof List<?> list && !list.isEmpty()) ...`; Spring `ResolvableType` для introspection generic типов. 📋 ПРАВИЛО: «instanceof только reifiable: primitive, non-generic, raw, `<?>`, ArrayN[]». 🔗 См. Q6, Q9, Q33.
> - [ ] Pattern matching в Java 16+ снял ограничение erasure: `obj instanceof List<String> list` теперь компилируется и проверяет элементы. | Pattern matching не реифицирует generics. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт работающий `obj instanceof List<String> list` в Java 21 — compile error, тратит час на чтение JEP-441; ограничение остаётся пока Project Valhalla не релизнет reified generics.

## Q35. Какие изменения в дженериках появились в Java 10+?

**Java 10 — `var` (вывод локального типа):**

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

> [!mcq]
> - [ ] `var` стирает generic-информацию: `var list = new ArrayList<String>()` имеет runtime-тип `ArrayList` без параметра типа. | `var` сохраняет полный параметризованный тип. ❌ ПОСЛЕДСТВИЕ: разработчик избегает `var` для generic-полей считая что теряется type-safety, везде пишет `ArrayList<String> list = new ArrayList<>()` — verbose без необходимости; на деле `var list.add(42)` дал бы compile error, тип `String` сохраняется.
> - [ ] `var` можно использовать как тип поля и параметра метода — это локальный inference и в полях работает с Java 17+. | `var` работает только для локальных переменных. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `private var counter = 0` или `void process(var input) {}` — compile error «'var' is not allowed here»; команда тратит время на поиск конфигурации, хотя ограничение JLS постоянное.
> - [ ] Project Valhalla уже релизнут в Java 21 — `List<int>` работает в production. | Valhalla в разработке, не релизнут. ❌ ПОСЛЕДСТВИЕ: разработчик пытается `List<int>` в production Java 21 — compile error, в release-notes ничего нет; team тратит время на поиск features которых нет, хотя `IntStream` и `IntArrayList` (Eclipse) решают задачу сейчас.
> - [x] **Java 10+** ввёл `var` для локального type inference (сохраняет полный generic-тип); **Java 16+** добавил pattern matching с `instanceof Type t`; **Java 17+** разрешает `sealed interface Result<T>` с дженериками + records `record Success<T>(T value) implements Result<T>`; **Project Valhalla** (preview) обещает specialization для примитивов и value types. | Каждый release расширяет возможности дженериков. ✓ ПРИМЕНЯТЬ: `var users = repository.findAll()` сохраняет `List<User>`; `sealed interface Result<T> permits Success, Failure` для безопасных алгебраических типов; Spring 6+ поддерживает Java 17+ generics; Kotlin Coroutines `Flow<T>` — пример где sealed+generics эффективны. 📋 ПРАВИЛО: «var = локальный inference + полный generic; sealed+records = типобезопасные ADT; Valhalla — будущее без autoboxing». 🔗 См. Q4, Q27.

## Q36. (!) В чём разница между `? extends T` и `? super T`? Когда что выбирать?

`? extends T` (upper bounded wildcard) означает "тип, являющийся `T` или его подтипом". `? super T` (lower bounded wildcard) означает "тип, являющийся `T` или его супертипом". Выбор определяется принципом **PECS** (`Producer Extends, Consumer Super`).

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

> [!mcq]
> - [x] `<? extends T>` — **upper bounded** (любой подтип T), используется как **producer** — читаем как `T`, нельзя добавлять (кроме `null`); `<? super T>` — **lower bounded** (любой супертип T), используется как **consumer** — пишем `T` и подтипы, читаем только как `Object`; выбор определяет правило **PECS** (Producer Extends, Consumer Super). | Variance задаёт направление подтипности и роль в API. ✓ ПРИМЕНЯТЬ: `sum(List<? extends Number>)` — читаем числа разных подтипов; `fill(List<? super Integer>, Integer val)` — пишем Integer в любой супертип; `Collections.copy(List<? super T> dest, List<? extends T> src)` — производитель + потребитель в одной сигнатуре. 📋 ПРАВИЛО: «PECS — Producer Extends, Consumer Super; чтение vs запись определяет wildcard». 🔗 См. Q14, Q15, Q18.
> - [ ] `<? extends T>` позволяет добавлять элементы типа `T` в коллекцию — компилятор знает, что `T` подходит для любого подтипа. | Запрещено: реальный тип неизвестен компилятору. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `void add(List<? extends Number> list, Number n) { list.add(n); }` — compile error «cannot be applied»; команда обходит через unchecked cast или raw type, теряя type-safety; правильный путь — `<? super T>` для записи.
> - [ ] `<? super T>` позволяет читать элементы как `T` напрямую — это симметричный аналог `extends` для чтения. | Чтение даёт только `Object`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Integer i = list.get(0)` для `List<? super Integer>` — compile error, добавляет ручной cast `(Integer) list.get(0)` который ломается при `List<Number>` с `Double` внутри; правильный путь — `<? extends T>` для чтения.
> - [ ] `<? extends T>` и `<? super T>` взаимозаменяемы и означают одно — «любой подтип T в иерархии». | Это противоположные направления variance. ❌ ПОСЛЕДСТВИЕ: разработчик использует `<? super T>` для чтения коллекции — `forEach(item -> sum += item.intValue())` падает, item типа `Object`, нет метода `intValue()`; рефакторинг требует выкорчёвывания всех wildcards в API.

## Q37. (!) Как работает `Type Erasure` на практике и какие проблемы создаёт?

`Type Erasure` — процесс удаления информации о generic-типах во время компиляции. Все параметры типа заменяются их границей (обычно `Object`) или первым ограничением.

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

> [!mcq]
> - [ ] `Type Erasure` сохраняет параметры типа в байткоде, но скрывает их за `Class<T>` token — рефлексия может извлечь полный тип без сторонних patterns. | После erasure `T` физически отсутствует в bytecode полей и сигнатур методов. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `Class<T> c = (Class<T>) field.getType()` ожидая получить параметризованный тип — получает raw `List`, теряется день на debugging; правильный путь — `Class<T>` token явно, либо Super Type Token.
> - [ ] Стирание происходит при загрузке класса JVM, поэтому через кастомный `ClassLoader` можно «подменить» параметр типа в runtime. | Erasure — фаза компилятора, ClassLoader работает с уже стёртым bytecode. ❌ ПОСЛЕДСТВИЕ: разработчик пишет custom ClassLoader ожидая reified generics — JVM никогда не видит generic-метаданные методов; ловит `ClassCastException` при попытке cast и не понимает причину.
> - [ ] `new T[10]` запрещён, потому что массивы immutable, а generic-типы mutable — это semantic conflict. | Реальная причина — runtime не знает компонентный тип. ❌ ПОСЛЕДСТВИЕ: разработчик ищет «как сделать generic-массив immutable» через `Collections.unmodifiableList` — на деле проблема не в mutability, а в том что JVM не может проверить ArrayStoreException; heap pollution утекает скрытно через `(T[]) new Object[]`.
> - [x] Параметры типа стираются до bound: неограниченный `T` → `Object`, `T extends Number` → `Number`; **три практических следствия:** (1) `instanceof List<String>` запрещён всегда (включая Java 21), только `instanceof List<?>`; (2) `void m(List<String>)` и `void m(List<Integer>)` — compile error «name clash, same erasure»; (3) `new T[]` и `T.class` запрещены — нужен `Class<T>` token. | Erasure обеспечивает backward compatibility с pre-Java 5 ценой потери runtime-информации. ✓ ПРИМЕНЯТЬ: при дизайне generic API всегда пробрасывайте `Class<T>` token (`new Repository<>(User.class)` в Spring Data); для коллекций — Super Type Token; для overloading с generics — переименовывайте методы (`processStrings`/`processIntegers`); используйте `javap -c -p` для проверки реального bytecode. 📋 ПРАВИЛО: «erasure прячет — Class<T>, Super Type Token и переименование вытаскивают». 🔗 См. Q6, Q7, Q30.

## Q38. Что такое generic-методы и как компилятор выводит тип?

Generic-метод объявляет свои параметры типа независимо от класса-контейнера. Параметры метода выводятся из аргументов вызова через **type inference**.

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

> [!mcq]
> - [ ] Generic-метод объявляет параметр типа в сигнатуре класса, а сам метод его использует — это альтернатива generic-классу. | Это и есть generic-класс, не generic-метод. ❌ ПОСЛЕДСТВИЕ: разработчик объявляет `class Utils<T> { static T first(List<T> list) }` для utility-методов — compile error «non-static T cannot be referenced from static context»; команда создаёт `new Utils<String>().first(list)` лишь чтобы вызвать static-аналог, теряет stateless API.
> - [ ] Параметр типа `<T>` указывается **после** возвращаемого типа, перед именем метода: `public static T <T> max(List<T> list)`. | `<T>` ставится **до** возвращаемого типа. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `public static T <T> max(...)` — compile error «cannot find symbol T»; команда тратит время на debugging синтаксиса вместо работы над логикой; правильный порядок `static <T> T max(...)`.
> - [ ] `Collections.max(list)` всегда требует явного `Collections.<String>max(list)` — иначе компилятор подставит `Object` как параметр типа. | Target type inference выводит T автоматически. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `String s = Collections.<String>max(list)` везде — verbose без причины; для библиотечного API такой стиль неприемлем, IDE хайлайтит как «redundant type arguments», review требует упростить.
> - [x] **Generic-метод** объявляет свой параметр типа `<T>` **до** возвращаемого типа: `public static <T> T max(List<T> list)`; компилятор выводит `T` через **target type inference** из аргументов вызова и контекста присваивания (Java 8+ ещё и из лямбд и chained calls). | Параметры метода независимы от параметров класса-контейнера. ✓ ПРИМЕНЯТЬ: utility-методы `Collections.<T>emptyList()`, `Stream.<T>of(t)`, `Optional.<T>of(value)`; собственный код — `<T extends Comparable<T>> T min(T a, T b)`; для разрешения ambiguity — explicit type args через `<>`; в Java 8+ inference работает через chained `.collect(Collectors.toMap(...))`. 📋 ПРАВИЛО: «<T> до возврата = метод параметрический; type inference из target + arguments + chains». 🔗 См. Q3, Q4, Q26.

## Q39. Что такое `Comparable<T>` vs `Comparator<T>` с точки зрения дженериков?

`Comparable<T>` — интерфейс естественного порядка, реализуется внутри класса. `Comparator<T>` — функциональный интерфейс внешней стратегии сравнения. Оба активно используют generic-возможности Java.

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

> [!mcq]
> - [x] `T extends Comparable<? super T>` — стандартное ограничение для `Collections.sort` и подобных утилит: разрешает `T` быть наследником, у которого `compareTo` объявлен в **предке** (например, `class Cat extends Animal`, где `Animal implements Comparable<Animal>`); это PECS-расширение для consumer-позиции (`Comparable` потребляет элементы для сравнения). | `<? super T>` обеспечивает контравариантность контракта сравнения. ✓ ПРИМЕНЯТЬ: `<T extends Comparable<? super T>> void sort(List<T>)` — стандарт JDK; `Collections.sort(List<T>, Comparator<? super T>)` — аналогично для внешней стратегии; в Spring Data `PageRequest.sort(...)` использует ту же идиому; без `? super` не сортируется `List<Cat>` через `Comparable<Animal>`. 📋 ПРАВИЛО: «`Comparable<? super T>` для sort — сравнение может прийти из предка». 🔗 См. Q15, Q18, Q36.
> - [ ] `Comparable<T>` — внешняя стратегия сравнения, `Comparator<T>` — внутренний порядок класса (встроенный в иерархию). | Перепутаны определения. ❌ ПОСЛЕДСТВИЕ: разработчик кладёт элементы в `TreeSet` ожидая «внешний» порядок от `Comparable.compareTo` — на деле `TreeSet` без явного `Comparator` использует естественный (Comparable) порядок, элементы расставляются по compareTo класса; данные хаотичны если confusion распространён в команде.
> - [ ] `Comparable<? super T>` — это синтаксический сахар над `Comparable<T>`, никогда не нужен на практике. | Нужен для legal hierarchies где `compareTo` в предке. ❌ ПОСЛЕДСТВИЕ: разработчик объявляет `<T extends Comparable<T>> void sort(List<T>)` — `sort(List<Cat>)` не компилируется, потому что `Cat` не `Comparable<Cat>`, а `Comparable<Animal>` (через предка); legitimate class hierarchies перестают сортироваться.
> - [ ] `Comparator.thenComparing(...)` создаёт новый `Comparable`-объект, который можно передать в `compareTo`. | Возвращает `Comparator<T>`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `int r = a.compareTo(byName.thenComparing(byAge))` — compile error «incompatible types»; ChainedComparator передаётся в `list.sort(...)` или `Collections.sort(list, comparator)`, а не в `compareTo`.

## Q40. Что такое wildcards (`?`) и когда использовать unbounded `<?>`?

`Unbounded wildcard` `<?>` означает "любой тип" — это `? extends Object`. Используется когда:
- Метод работает с любым типом параметризованной коллекции
- Используется только API самого контейнера (не содержимого)

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

> [!mcq]
> - [x] **Unbounded wildcard** `List<?>` принимает любой `List<X>` (ковариантен), разрешает чтение как `Object` и запрещает `add` любого значения кроме `null`; используется когда метод работает только с API контейнера (`size`, `isEmpty`, итерация), не с типом элементов. | Эквивалент `List<? extends Object>` без необходимости явного bound. ✓ ПРИМЕНЯТЬ: print/audit/inspection методы (`void log(List<?> any)`); `Collections.unmodifiableList(List<?>)`; реализация `equals`/`hashCode` коллекций где тип элементов не важен; Spring `BeanUtils.copyProperties(Object src, Object dest)` принимает похожую wildcard-семантику. 📋 ПРАВИЛО: «<?> = read-only Object view; принимает любой `List<X>`, пишет только null». 🔗 См. Q13, Q16, Q17.
> - [ ] `List<?>` эквивалентен `List<Object>` — оба принимают `List<Integer>`, `List<String>` и любые другие parameterized списки. | `List<Object>` инвариантен и НЕ принимает `List<Integer>`. ❌ ПОСЛЕДСТВИЕ: разработчик объявляет утилитарный метод `void process(List<Object> list)` — вызов `process(intList)` падает с compile error «incompatible types»; команда дублирует методы под `List<Integer>`, `List<String>` или копирует данные в `List<Object>` (лишний O(n)) вместо `List<?>`.
> - [ ] В `List<?>` можно добавлять только `null` и читать только как `Object` — это **upper bounded** wildcard `<? extends Object>` с явной границей. | Это unbounded, не upper bounded — разница в семантике (отсутствие границы vs `Object` верхняя). ❌ ПОСЛЕДСТВИЕ: разработчик пишет `<? extends Object>` везде ради «чёткости» — IDE хайлайтит как «redundant `extends Object`», review требует упростить; путаница в PECS-логике приводит к лишним wildcard-uровням и непонятным API.
> - [ ] `<?>` всегда лучше `<T>` для generic-методов — wildcards универсальнее и не требуют явного объявления параметра типа. | Если возвращаемый тип привязан к параметру или нужен `add(T)`, нужен `<T>`. ❌ ПОСЛЕДСТВИЕ: разработчик объявляет `<?> first(List<?> list) { return list.get(0); }` — return type inferred как `Object`, типизация на стороне вызова потеряна; `String s = first(stringList)` ломается с compile error, нужен явный cast или return signature `<T> T first(List<T>)`.

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
