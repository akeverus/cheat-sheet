---
title: "Вопросы на собеседовании: Java Core"
description: "Комплексное руководство по вопросам собеседования на тему Java Core: ключевые слова, ООП-механизмы, контракт Object, иммутабельность, classloading, reflection, современные возможности Java 14-21."
tags:
  - interview
  - programming-languages
  - java-core-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Core"
  - "Java Core interview"
  - "Java Core собеседование"
prerequisites: []
next: []
updated: "2026-05-19"
---
# Вопросы на собеседовании: `Java Core`

Комплексное руководство по вопросам собеседования на тему `Java Core` для `Senior Java Developer`. Включает ключевые слова и модификаторы, контракт `Object`, иммутабельность, механизм загрузки классов, `Reflection API`, обработку `null`, и современные возможности `Java 14–21`.

## Полезные ссылки

### Официальная документация

- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se17/html/)
- [Java Tutorial](https://docs.oracle.com/javase/tutorial/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)
- [Java Interview Questions — Baeldung](https://www.baeldung.com/java-interview-questions)
- [Java equals() and hashCode() Contracts — Baeldung](https://www.baeldung.com/java-equals-hashcode-contracts)
- [Class Loaders in Java — Baeldung](https://www.baeldung.com/java-classloaders)
- [Guide to Java Reflection — Baeldung](https://www.baeldung.com/java-reflection)

## Содержание

**Ключевые слова и модификаторы**
- [Q1. (!) Что такое ключевое слово `final`?](#q1--что-такое-ключевое-слово-final)
- [Q2. Что такое метод по умолчанию?](#q2-что-такое-метод-по-умолчанию)
- [Q3. Что такое статические члены класса?](#q3-что-такое-статические-члены-класса)
- [Q4. Можно ли объявить класс `abstract` без `abstract`-методов?](#q4-можно-ли-объявить-класс-abstract-без-abstract-методов)
- [Q5. Что такое `Constructor Chaining`?](#q5-что-такое-constructor-chaining)
- [Q6. (!) Что такое `Overriding` и `Overloading` методов?](#q6--что-такое-overriding-и-overloading-методов)
- [Q7. Можно ли переопределить `static` метод?](#q7-можно-ли-переопределить-static-метод)
- [Q8. Что такое `visibility modifiers` (`public`, `private`, `protected`)?](#q8-что-такое-visibility-modifiers-public-private-protected)
- [Q9. Что такое `this` и `super`?](#q9-что-такое-this-и-super)
- [Q10. Можно ли переопределить `private` метод?](#q10-можно-ли-переопределить-private-метод)

**Контракт `Object`, `equals` и `hashCode`**
- [Q11. (!) Что такое `Object` и какие методы он определяет?](#q11--что-такое-object-и-какие-методы-он-определяет)
- [Q12. (!) В чём разница между `==` и `equals()`?](#q12--в-чём-разница-между--и-equals)
- [Q13. (!) Что такое контракт `equals()` и `hashCode()`?](#q13--что-такое-контракт-equals-и-hashcode)
- [Q14. Что такое `clone()` и как правильно клонировать объект?](#q14-что-такое-clone-и-как-правильно-клонировать-объект)
- [Q15. Что такое `shallow copy` и `deep copy`?](#q15-что-такое-shallow-copy-и-deep-copy)

**Иммутабельность и перечисления**
- [Q16. (!) Что такое `Immutable` класс и как его создать?](#q16--что-такое-immutable-класс-и-как-его-создать)
- [Q17. Как сравнить два значения `Enum`?](#q17-как-сравнить-два-значения-enum)
- [Q18. (!) Что такое `Marker Interface`?](#q18--что-такое-marker-interface)
- [Q19. Что такое `Var-args`?](#q19-что-такое-var-args)

**Инициализация и загрузка классов**
- [Q20. Что такое блоки инициализации?](#q20-что-такое-блоки-инициализации)
- [Q21. (!) Как работает загрузка классов в JVM?](#q21--как-работает-загрузка-классов-в-jvm)
- [Q22. Что такое делегирование загрузки классов?](#q22-что-такое-делегирование-загрузки-классов)
- [Q23. Можно ли написать собственный `ClassLoader`?](#q23-можно-ли-написать-собственный-classloader)

**`Reflection API`**
- [Q24. (!) Что такое `Reflection` и зачем он нужен?](#q24--что-такое-reflection-и-зачем-он-нужен)
- [Q25. Как получить информацию о классе через `Reflection`?](#q25-как-получить-информацию-о-классе-через-reflection)
- [Q26. Какие недостатки и ограничения у `Reflection`?](#q26-какие-недостатки-и-ограничения-у-reflection)

**Пакеты, `package` и `import`**
- [Q27. Что такое `package` и `import`?](#q27-что-такое-package-и-import)

**Типы данных и автоупаковка**
- [Q28. (!) Что такое автоупаковка и распаковка (`autoboxing`/`unboxing`)?](#q28--что-такое-автоупаковка-и-распаковка-autoboxingunboxing)
- [Q29. Что такое `String Pool`?](#q29-что-такое-string-pool)

**`null`, `Optional` и защитное программирование**
- [Q30. Что такое `null` и как избежать `NPE`?](#q30-что-такое-null-и-как-избежать-npe)
- [Q31. (!) Что такое `Optional` и когда его использовать?](#q31--что-такое-optional-и-когда-его-использовать)

**Современные возможности Java**
- [Q32. Что такое `Records` (`Java 14+`)?](#q32-что-такое-records-java-14)
- [Q33. Что такое compact constructors в `Records`?](#q33-что-такое-compact-constructors-в-records)
- [Q34. (!) Что такое `Sealed` classes (`Java 17`)?](#q34--что-такое-sealed-classes-java-17)
- [Q35. Что такое `Pattern Matching` for `instanceof` (`Java 16`)?](#q35-что-такое-pattern-matching-for-instanceof-java-16)
- [Q36. Что такое `switch expression` (`Java 14+`)?](#q36-что-такое-switch-expression-java-14)
- [Q37. Что такое `Text Blocks` (`Java 15`)?](#q37-что-такое-text-blocks-java-15)
- [Q38. Что такое `Pattern Matching` for `switch` (`Java 21`)?](#q38-что-такое-pattern-matching-for-switch-java-21)
- [Q39. Что такое `SequencedCollection` (`Java 21`)?](#q39-что-такое-sequencedcollection-java-21)
- [See also](#see-also)

---

## Q1. (!) Что такое ключевое слово `final`?

Ключевое слово `final` в `Java` ограничивает изменяемость и наследование. Применяется к четырём элементам:

| Элемент | Эффект `final` |
|---------|----------------|
| Класс | Нельзя наследовать (`String`, `Integer`) |
| Метод | Нельзя переопределить в подклассах |
| Поле | Значение присваивается один раз (в объявлении или конструкторе) |
| Локальная переменная | Значение нельзя изменить после первого присваивания |

```java
public final class UtilityClass {           // нельзя наследовать
    private final String name = "MyApp";    // неизменяемое поле

    public final void process() { }         // нельзя переопределить

    public void demo() {
        final int max = 3;
        // max = 5; // ошибка компиляции
    }
}
```

**Главная ловушка:** `final` фиксирует только саму ссылку, а не объект, на который она указывает. Ссылку нельзя перенаправить на другой объект, но **сам объект остаётся изменяемым** — например, в `final List<String> list` по-прежнему можно добавлять и удалять элементы. Чтобы запретить и это, берут неизменяемые коллекции: `List.of()` или `Collections.unmodifiableList()`.

## Q2. Что такое метод по умолчанию?

Метод по умолчанию (`default method`) — это метод с готовой реализацией прямо внутри интерфейса. Появился в `Java 8`, помечается ключевым словом `default`. Класс, реализующий интерфейс, может либо унаследовать эту реализацию как есть, либо переопределить её.

Зачем понадобились: до Java 8 любой новый метод в интерфейсе ломал все его реализации — каждую пришлось бы дописывать. `default`-методы позволили расширять интерфейсы без поломки существующего кода. Именно так в `Collection` добавили `stream()` и `forEach()`, не сломав миллионы классов.

```java
interface Loggable {
    void log(String message);

    default void logInfo(String message) {
        log("[INFO] " + message);
    }

    default void logError(String message) {
        log("[ERROR] " + message);
    }
}

class ConsoleLogger implements Loggable {
    @Override
    public void log(String message) {
        System.out.println(message);
    }
    // logInfo() и logError() доступны без переопределения
}
```

**Конфликт ромба (diamond problem):** если класс реализует два интерфейса с одинаковой сигнатурой `default`-метода, компилятор не может выбрать сам — он требует явного переопределения. Нужную реализацию выбирают через `InterfaceName.super.method()`. Подробнее о `default`-методах и лямбдах — в [вопросах по Java 8](java-8-interview.md).

## Q3. Что такое статические члены класса?

Статические члены принадлежат самому классу, а не его экземплярам — они существуют в единственном числе, независимо от того, создан хоть один объект или нет. Объявляются модификатором `static`.

**Статические поля** — одна общая ячейка на все экземпляры; инициализируются один раз при загрузке класса. Меняешь значение через любой объект — видят все.

**Статические методы** — вызываются прямо на классе (`ClassName.method()`), без создания экземпляра. Поэтому у них нет `this` и нет доступа к нестатическим полям и методам: на момент вызова конкретного объекта может попросту не быть.

```java
public class Counter {
    private static int count = 0;   // общее для всех экземпляров

    public Counter() { count++; }

    public static int getCount() {  // вызов: Counter.getCount()
        return count;
    }
}
```

**Подводные камни:** статическое поле по сути глобальное состояние. Из-за этого его одновременно меняют разные потоки (нужна синхронизация), а тесты становятся хрупкими — изменения протекают между тестами, а статический метод нельзя подменить заглушкой без `Mockito.mockStatic()`.

## Q4. Можно ли объявить класс `abstract` без `abstract`-методов?

Да, абстрактных методов не требуется. Сам модификатор `abstract` уже запрещает создавать объект напрямую (`new` не сработает) — получить экземпляр можно только через подкласс. Абстрактные методы — лишь один из инструментов абстрактного класса, но не обязательное условие.

Зачем такой класс нужен:
1. **Запрет на создание экземпляров** — когда класс осмыслен только как база, а самостоятельный объект был бы бессмыслицей.
2. **Группировка общей логики** — готовые (неабстрактные) методы и поля, которые наследники получают «бесплатно».
3. **Шаблон Template Method** — база задаёт скелет алгоритма в обычном методе, а подклассы переопределяют отдельные шаги.

```java
public abstract class AbstractRepository {
    // Нет abstract-методов, но нельзя создать new AbstractRepository()
    public void save(Object entity) {
        validate(entity);
        persist(entity);
    }

    protected void validate(Object entity) { /* общая логика */ }
    protected void persist(Object entity) { /* общая логика */ }
}
```

## Q5. Что такое `Constructor Chaining`?

Цепочка конструкторов — это когда один конструктор вызывает другой: либо в том же классе через `this(...)`, либо в родительском через `super(...)`. Такой вызов обязан стоять **первой строкой** конструктора. Смысл — собрать всю логику инициализации в одном «полном» конструкторе, а остальные сделать короткими и переадресующими; так не дублируется код и не плодятся расхождения.

```java
public class Connection {
    private final String host;
    private final int port;
    private final int timeout;

    public Connection() {
        this("localhost");              // → Connection(String)
    }

    public Connection(String host) {
        this(host, 8080);              // → Connection(String, int)
    }

    public Connection(String host, int port) {
        this(host, port, 30_000);      // → Connection(String, int, int)
    }

    public Connection(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }
}
```

**Правила:** `this()` и `super()` нельзя вызвать вместе (оба претендуют на первую строку). Если же конструктор не зовёт ни `this()`, ни `super()` явно, компилятор сам подставляет `super()` без аргументов — поэтому при отсутствии конструктора без параметров у родителя код не скомпилируется.

## Q6. (!) Что такое `Overriding` и `Overloading` методов?

Это два разных механизма с похожими названиями — главное не путать.

**Переопределение (`overriding`)** — подкласс даёт методу родителя с **той же сигнатурой** новую реализацию. Какую версию вызвать, JVM решает в **runtime** по фактическому типу объекта (динамическая диспетчеризация) — это и есть полиморфизм.

```java
class Animal {
    public String sound() { return "..."; }
}

class Cat extends Animal {
    @Override
    public String sound() { return "Мяу"; }  // runtime-полиморфизм
}
```

**Перегрузка (`overloading`)** — несколько методов с одним именем, но **разной сигнатурой** (разные типы или число аргументов). Какой из них вызвать, компилятор выбирает в **compile time** по типам аргументов. Возвращаемый тип в сигнатуру не входит — отличить методы только им нельзя.

```java
class MathUtils {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}
```

| Критерий | `Overriding` | `Overloading` |
|----------|-------------|---------------|
| Сигнатура | Одинаковая | Разная |
| Классы | Родитель + наследник | Один класс |
| Разрешение | Runtime | Compile time |
| Аннотация | `@Override` | Нет |
| Возвращаемый тип | Ковариантный (подтип) | Любой |

Подробнее о полиморфизме — в [вопросах по ООП](java-oop-interview.md).

## Q7. Можно ли переопределить `static` метод?

Нет — переопределить (`override`) нельзя, можно только **скрыть** (`hiding`). Статический метод привязан к классу, а не к объекту, поэтому динамической диспетчеризации для него нет. Если в подклассе объявить метод с той же сигнатурой, он не заменяет родительский, а прячет его: какой именно вызовется, определяется **типом ссылки на этапе компиляции**, а не реальным типом объекта.

```java
class Parent {
    public static void greet() { System.out.println("Parent"); }
}

class Child extends Parent {
    public static void greet() { System.out.println("Child"); }
}

Parent.greet();             // Parent
Child.greet();              // Child
Parent ref = new Child();
ref.greet();                // Parent — по типу ссылки, не объекта!
```

Это принципиальное отличие от `overriding`: нет динамической диспетчеризации, нет полиморфизма.

## Q8. Что такое `visibility modifiers` (`public`, `private`, `protected`)?

Модификаторы доступа определяют, откуда виден член класса, и реализуют инкапсуляцию — скрывают внутренности, оставляя наружу только нужный интерфейс. От самого закрытого к самому открытому: `private` → package-private → `protected` → `public`.

| Модификатор | Класс | Пакет | Подкласс (другой пакет) | Весь мир |
|-------------|-------|-------|-------------------------|----------|
| `private` | Да | Нет | Нет | Нет |
| package-private (без модификатора) | Да | Да | Нет | Нет |
| `protected` | Да | Да | Да | Нет |
| `public` | Да | Да | Да | Да |

**Эмпирическое правило — давать минимально достаточный доступ:**
- Поля — `private` (полная инкапсуляция, доступ только через методы).
- Методы публичного API — `public`.
- Методы-хуки для наследников — `protected`.
- Внутренняя логика пакета, не предназначенная наружу, — package-private (без модификатора).

## Q9. Что такое `this` и `super`?

`this` — ссылка на текущий объект. Используется для:
- Различения полей и параметров (`this.name = name`)
- Вызова другого конструктора (`this(args)`)
- Передачи текущего объекта в метод (`callback.accept(this)`)

`super` — ссылка на суперкласс. Используется для:
- Вызова конструктора суперкласса (`super(args)`)
- Вызова переопределённого метода суперкласса (`super.method()`)

`this()` или `super()` должны быть **первой строкой** конструктора и не могут использоваться одновременно.

## Q10. Можно ли переопределить `private` метод?

Нет. `private`-метод вообще не виден из подкласса, поэтому переопределять там нечего. Метод с такой же сигнатурой в подклассе — **отдельный независимый метод**, никак не связанный с родительским. Аннотация `@Override` на нём даст ошибку компиляции — это удобный способ убедиться, что переопределение действительно произошло.

```java
class Parent {
    private void doWork() { System.out.println("Parent"); }

    public void execute() { doWork(); }
}

class Child extends Parent {
    // Это НЕ override, это новый метод
    private void doWork() { System.out.println("Child"); }
}

new Child().execute(); // "Parent" — вызывается Parent.doWork()
```

Вывод: полиморфизм работает только для методов, видимых снаружи класса (`public`, `protected`, а также package-private в пределах одного пакета). Для `private` динамической диспетчеризации нет.

## Q11. (!) Что такое `Object` и какие методы он определяет?

`Object` — корень всей иерархии классов в `Java`: любой класс, прямо или транзитивно, наследуется от него (даже если `extends` не написан). Благодаря этому переменную любого типа можно положить в `Object`, а его методы доступны у каждого объекта.

Ключевые методы, которые `Object` отдаёт всем потомкам:

| Метод | Назначение |
|-------|-----------|
| `equals(Object)` | Логическое равенство |
| `hashCode()` | Хэш-код для хэш-коллекций |
| `toString()` | Строковое представление |
| `getClass()` | Класс объекта в runtime |
| `clone()` | Копирование объекта (shallow) |
| `finalize()` | **Deprecated** с `Java 9`, удалён в `Java 18` |
| `wait()` / `notify()` / `notifyAll()` | Механизм ожидания/уведомления потоков |

На собеседовании ожидают знание контракта `equals()`/`hashCode()` (см. Q13) и понимание, почему `finalize()` — антипаттерн (непредсказуемое время вызова, проблемы с производительностью `GC`). Подробнее о потоках и `wait/notify` — в [вопросах по многопоточности](java-concurrency-interview.md).

## Q12. (!) В чём разница между `==` и `equals()`?

Коротко: `==` сравнивает идентичность (та ли это ссылка), а `equals()` — смысловое равенство (одинаковое ли содержимое).

`==` для **примитивов** сравнивает сами значения, а для **ссылочных типов** — адреса: `true` только если обе ссылки указывают на один и тот же объект в памяти.

`equals()` — метод из `Object`; в базовой реализации он работает ровно как `==` (сравнивает ссылки). Чтобы сравнивать по содержимому, его переопределяют — именно поэтому у `String`, оберток и большинства value-объектов `equals()` сравнивает данные.

```java
String s1 = new String("hello");
String s2 = new String("hello");

System.out.println(s1 == s2);      // false — разные объекты в heap
System.out.println(s1.equals(s2)); // true  — одинаковое содержимое

String s3 = "hello";
String s4 = "hello";
System.out.println(s3 == s4);      // true  — один объект из String Pool
```

Контракт `equals()` (пять свойств):
1. **Рефлексивность**: `x.equals(x)` → `true`
2. **Симметричность**: `x.equals(y)` → `y.equals(x)`
3. **Транзитивность**: `x.equals(y)` и `y.equals(z)` → `x.equals(z)`
4. **Консистентность**: многократные вызовы дают одинаковый результат
5. **Null-безопасность**: `x.equals(null)` → `false`

## Q13. (!) Что такое контракт `equals()` и `hashCode()`?

Контракт — это правило согласованности двух методов: переопределяя один, обязательно переопределяй и второй, иначе хеш-коллекции сломаются. Суть в трёх пунктах:

1. Если `a.equals(b) == true`, то `a.hashCode() == b.hashCode()` — **обязательно**. Равные объекты должны давать одинаковый хеш.
2. Как следствие: если `a.hashCode() != b.hashCode()`, то объекты заведомо не равны (`a.equals(b) == false`).
3. Обратное **не гарантируется**: одинаковый `hashCode` ещё не значит, что объекты равны — это допустимая коллизия, дальше решает `equals()`.

**Почему это важно.** `HashMap` сначала по `hashCode()` находит бакет, и только внутри него сравнивает ключи через `equals()`. Если хеши у равных объектов разойдутся, поиск пойдёт не в тот бакет — и объект «потеряется».

**Нарушение контракта** приводит к некорректной работе `HashMap`, `HashSet`, `Hashtable`:

```java
public class Employee {
    private final String name;
    private final int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee that = (Employee) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);  // ОБЯЗАТЕЛЬНО при переопределении equals
    }
}
```

**Типичная ошибка** — переопределить `equals()`, но забыть `hashCode()`. Тогда два логически равных объекта дадут разный хеш, попадут в **разные бакеты** `HashMap`, и поиск по ключу их не найдёт. Этот баг привёл к потерям данных в LinkedIn и Uber при миграции с Hashtable на HashMap.

```java
// Без hashCode() — объект "потеряется" в HashMap
Map<Employee, String> map = new HashMap<>();
Employee e1 = new Employee("Иван", 1);
map.put(e1, "отдел продаж");

Employee e2 = new Employee("Иван", 1); // equals(e1) = true
map.get(e2); // null! — потому что hashCode разный, ищет в другом бакете
// Результат: потеря данных или дубликаты в БД
```

## Q14. Что такое `clone()` и как правильно клонировать объект?

`clone()` — `protected`-метод `Object`, создающий **поверхностную копию** (shallow copy) объекта. Чтобы он сработал, класс обязан реализовать маркерный интерфейс `Cloneable`, иначе — `CloneNotSupportedException`. Это один из самых критикуемых механизмов JDK.

**Почему `clone()` неудобен:**
- **Нарушает инкапсуляцию** — чтобы клонировать снаружи, метод приходится переопределять как `public`, а сам `Cloneable` даже не объявляет `clone()`.
- **Делает только shallow copy** — вложенные объекты остаются общими, их копирование надо дописывать вручную.
- **Хрупкий контракт** — корректная реализация обязана звать `super.clone()` по всей цепочке наследования, иначе тип копии «съедет».

**Современные альтернативы** (предпочтительнее):

```java
// 1. Копирующий конструктор
public class Address {
    private String city;
    private String street;

    public Address(Address other) {
        this.city = other.city;
        this.street = other.street;
    }
}

// 2. Статический фабричный метод
public static Address copyOf(Address original) {
    return new Address(original.city, original.street);
}
```

Joshua Bloch в «Effective Java» рекомендует **избегать `clone()`** и использовать копирующие конструкторы или фабричные методы.

## Q15. Что такое `shallow copy` и `deep copy`?

Разница в том, как глубоко копируются вложенные объекты.

**Shallow copy (поверхностная)** — поля переносятся «как есть»: примитивы дублируются по значению, а ссылки копируются как ссылки. В результате оригинал и копия **разделяют одни и те же вложенные объекты** — изменишь список в одном, изменится и в другом.

**Deep copy (глубокая)** — рекурсивно копируется и сам объект, и все вложенные объекты. Копия полностью независима: правки в ней никак не задевают оригинал.

```java
class Department {
    String name;
    List<String> employees;

    // Shallow copy — employees указывает на тот же список
    Department shallowCopy() {
        Department copy = new Department();
        copy.name = this.name;
        copy.employees = this.employees; // та же ссылка!
        return copy;
    }

    // Deep copy — employees — новый независимый список
    Department deepCopy() {
        Department copy = new Department();
        copy.name = this.name;
        copy.employees = new ArrayList<>(this.employees); // новый список
        return copy;
    }
}
```

`Object.clone()` по умолчанию делает `shallow copy`. Для `deep copy` — ручная реализация, сериализация или библиотеки (`Apache Commons`, `Cloner`).

## Q16. (!) Что такое `Immutable` класс и как его создать?

Неизменяемый (immutable) класс — это класс, объект которого после создания нельзя изменить ни одним способом. Любая «модификация» возвращает новый объект, а исходный остаётся прежним. Примеры в JDK: `String`, обёртки (`Integer`), `LocalDate`.

**Рецепт создания** (важен каждый пункт — пропуск любого открывает лазейку для изменения):
1. Объявить класс `final` — чтобы наследник не добавил мутабельность.
2. Все поля — `private final`: снаружи не видны, после конструктора не переприсваиваются.
3. Только геттеры, никаких сеттеров.
4. Для мутабельных полей (коллекции, `Date`, массивы) — **защитное копирование**: копировать на входе в конструкторе и на выходе в геттере, чтобы наружу не утекла внутренняя ссылка.
5. Все поля инициализировать в конструкторе.

```java
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;
    private final List<String> tags;

    public Money(BigDecimal amount, Currency currency, List<String> tags) {
        this.amount = amount;
        this.currency = currency;
        this.tags = List.copyOf(tags); // защитная копия
    }

    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public List<String> getTags() { return tags; } // List.copyOf уже immutable
}
```

**Плюсы иммутабельных классов:**
- **Потокобезопасность даром** — раз состояние не меняется, синхронизация не нужна и data race невозможна в принципе.
- **Надёжные ключи `HashMap`** — хеш не «уплывёт» после вставки, поэтому объект не потеряется в коллекции; это критично для Google Bigtable и других key-value stores.
- **Легко кэшировать и переиспользовать** — один и тот же объект можно безопасно отдавать многим (String interning экономит 1.5M объектов в LinkedIn).

С `Java 14+` для иммутабельных данных удобно использовать `Record` (см. Q32).

## Q17. Как сравнить два значения `Enum`?

Сравнивать через `==`. Каждая константа `Enum` существует в JVM в единственном экземпляре (синглтон), поэтому `==` корректно проверяет, что это одна и та же константа. `equals()` у `Enum` делает ровно то же самое, но `==` предпочтительнее по двум причинам:
- **Быстрее** — нет вызова метода, просто сравнение ссылок.
- **Безопасно к `null`** — `s == Status.ACTIVE` при `s == null` спокойно вернёт `false`, тогда как `s.equals(...)` бросит `NullPointerException`.

```java
enum Status { ACTIVE, INACTIVE, PENDING }

Status s = Status.ACTIVE;
if (s == Status.ACTIVE) { /* предпочтительно */ }
if (s.equals(Status.ACTIVE)) { /* работает, но менее идиоматично */ }
```

## Q18. (!) Что такое `Marker Interface`?

Маркерный интерфейс — это интерфейс вообще без методов. Он ничего не добавляет к поведению, а служит «ярлыком» на уровне типа: сам факт `implements` помечает класс свойством, которое потом проверяют через `instanceof`. По сути это метаданные, встроенные в систему типов.

Примеры в JDK:
- `Serializable` — разрешает сериализацию через `ObjectOutputStream`
- `Cloneable` — разрешает `Object.clone()`
- `RandomAccess` — сигнал, что `List` поддерживает быстрый доступ по индексу (`ArrayList`, но не `LinkedList`)

```java
public class Config implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    // ...
}
```

В современном `Java` маркерные интерфейсы часто **заменяются аннотациями** (`@Entity`, `@Deprecated`), но `Serializable` и `Cloneable` остаются по историческим причинам. Преимущество маркерного интерфейса перед аннотацией — возможность использовать его как тип в сигнатуре метода: `void process(Serializable obj)`.

## Q19. Что такое `Var-args`?

`Varargs` (variable arguments) — синтаксис, позволяющий передавать в метод произвольное число аргументов одного типа без ручного создания массива. Объявляется как `Type... name` и должен быть последним параметром. Под капотом компилятор всё равно собирает массив, поэтому внутри метода `name` — это обычный массив `Type[]`.

```java
public int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) {
        total += n;
    }
    return total;
}

sum(1, 2, 3);  // → 6
sum();          // → 0
sum(new int[]{1, 2}); // можно передать массив
```

Ограничения:
1. `Varargs`-параметр должен быть **последним** в списке аргументов
2. Только **один** `varargs`-параметр на метод
3. При перегрузке может возникнуть неоднозначность (`ambiguity`) — компилятор выдаст ошибку

## Q20. Что такое блоки инициализации?

Блоки инициализации — это куски кода, которые JVM выполняет автоматически при загрузке класса или при создании объекта, помимо конструктора. Их два вида:

**Instance initializer block** `{ ... }` — срабатывает при создании **каждого** экземпляра, перед телом конструктора. Удобен, чтобы вынести общую для всех конструкторов инициализацию.

**Static initializer block** `static { ... }` — выполняется **один раз**, при загрузке класса. Используется для настройки статических полей, которую нельзя уместить в одну строку объявления.

**Порядок инициализации** (отвечает на популярный вопрос «что выведется первым»):
1. Статические поля и `static`-блоки (сверху вниз, один раз)
2. Instance-поля и instance-блоки (сверху вниз, при каждом `new`)
3. Конструктор

```java
public class InitOrder {
    private static final String STATIC_FIELD;
    private String instanceField;

    static {
        STATIC_FIELD = "загружено";
        System.out.println("1. static блок");
    }

    {
        instanceField = "создано";
        System.out.println("2. instance блок");
    }

    public InitOrder() {
        System.out.println("3. конструктор");
    }
}
// new InitOrder() выведет: 1 → 2 → 3 (при первом вызове)
// new InitOrder() выведет: 2 → 3 (при последующих)
```

## Q21. (!) Как работает загрузка классов в JVM?

`ClassLoader` — компонент JVM, который по требованию находит `.class`-файл, читает его байткод и превращает в объект `Class` в памяти. Классы грузятся лениво — только когда впервые понадобятся. Три стандартных загрузчика образуют иерархию «родитель → потомок», где каждый следующий — потомок предыдущего:

- **Bootstrap ClassLoader** (нативный, C++) →
- **Platform ClassLoader** (бывш. Extension) →
- **Application ClassLoader** (System) →
- **Custom ClassLoader** (пользовательский)

| ClassLoader | Загружает |
|-------------|-----------|
| **Bootstrap** | `java.lang.*`, `java.util.*` — базовые классы JDK |
| **Platform** (Extension) | Модули платформы (`java.sql`, `java.xml`) |
| **Application** (System) | Классы из `classpath` приложения |

Каждый класс проходит три фазы:
1. **Loading** — загрузчик находит и читает байткод `.class`-файла, создаёт объект `Class`.
2. **Linking** — три шага: верификация (проверка корректности байткода), подготовка (выделение памяти под статические поля с дефолтными значениями) и резолвинг символьных ссылок.
3. **Initialization** — выполняются `static`-блоки и присваиваются заданные значения статическим полям.

Подробнее об архитектуре JVM — в [вопросах по JVM](../../jvm/jvm-interview.md).

## Q22. Что такое делегирование загрузки классов?

**Parent-first delegation** (модель делегирования) — базовый принцип работы `ClassLoader`: прежде чем грузить класс самому, загрузчик **поднимает запрос вверх** — отдаёт его родителю, тот своему родителю, и так до самого верхнего `Bootstrap`. Сам загрузчик берётся за дело только если ни один из родителей класс не нашёл. То есть поиск идёт сверху вниз по иерархии.

По шагам, на примере запроса «загрузить `MyClass`»:

1. Запрос приходит в `Application ClassLoader`.
2. `Application ClassLoader` **делегирует** его родителю — `Platform ClassLoader`.
3. `Platform ClassLoader` **делегирует** дальше вверх — `Bootstrap ClassLoader`.
4. `Bootstrap ClassLoader` класс **не нашёл** → возвращает запрос обратно вниз, в `Platform ClassLoader`.
5. `Platform ClassLoader` тоже **не нашёл** → возвращает запрос в `Application ClassLoader`.
6. `Application ClassLoader` **загружает `MyClass.class` из classpath** — он первый, кто реально нашёл класс.

Зачем так сделано:
- **Безопасность** — пользовательский класс с именем `java.lang.String` никогда не подменит настоящий: его сначала найдёт и загрузит `Bootstrap` из JDK.
- **Уникальность** — каждый класс загружается лишь одним загрузчиком и не дублируется; иначе один и тот же по имени класс из разных загрузчиков JVM считала бы разными типами.
- **Предсказуемость** — системные классы всегда приходят из JDK, а не из случайной точки classpath.

## Q23. Можно ли написать собственный `ClassLoader`?

Да. Наследуем `java.lang.ClassLoader` и переопределяем `findClass()` — именно его вызывает стандартный `loadClass()`, когда родители класс не нашли. Внутри читаем байты класса откуда угодно (файл, сеть, зашифрованный архив) и отдаём их в `defineClass()`, который превращает байтовый массив в объект `Class`:

```java
public class HotReloadClassLoader extends ClassLoader {

    private final Path classDir;

    public HotReloadClassLoader(Path classDir, ClassLoader parent) {
        super(parent);
        this.classDir = classDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Path classFile = classDir.resolve(
                name.replace('.', '/') + ".class");
            byte[] bytes = Files.readAllBytes(classFile);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
```

Реальные применения кастомных `ClassLoader`:
- **Hot-reload** в dev-режиме (Spring DevTools, JRebel)
- **Изоляция плагинов** (Tomcat загружает каждое веб-приложение отдельным `ClassLoader`)
- **Шифрование/обфускация** байткода
- OSGi-модульность

## Q24. (!) Что такое `Reflection` и зачем он нужен?

`Reflection API` — это способ **изучать и менять** структуру программы во время выполнения: узнавать классы, методы, поля и аннотации, создавать объекты, вызывать методы и читать поля, даже не зная типов на этапе компиляции. Точка входа — объект `Class`, а вокруг него работают `Method`, `Field`, `Constructor` из пакета `java.lang.reflect`. Именно благодаря рефлексии фреймворки умеют работать с вашими классами, ничего о них заранее не зная.

```java
// Получение класса
Class<?> clazz = Class.forName("com.example.User");

// Создание объекта
Object user = clazz.getDeclaredConstructor().newInstance();

// Вызов private метода
Method method = clazz.getDeclaredMethod("secretMethod");
method.setAccessible(true);  // обход private
Object result = method.invoke(user);

// Чтение private поля
Field field = clazz.getDeclaredField("name");
field.setAccessible(true);
String name = (String) field.get(user);
```

**Где используется:**
- **Spring/Hibernate** — DI, создание бинов, маппинг полей
- **JUnit** — поиск методов с `@Test`
- **Jackson/Gson** — сериализация/десериализация JSON
- **IDE** — автодополнение, рефакторинг

## Q25. Как получить информацию о классе через `Reflection`?

Три способа получить объект `Class<?>`:

```java
// 1. По имени класса (строка)
Class<?> c1 = Class.forName("java.util.ArrayList");

// 2. Через литерал класса
Class<?> c2 = ArrayList.class;

// 3. Через экземпляр
Class<?> c3 = new ArrayList<>().getClass();
```

Инспекция класса:

```java
Class<?> clazz = MyService.class;

// Все public-методы (включая унаследованные)
Method[] publicMethods = clazz.getMethods();

// Все методы (включая private, но без унаследованных)
Method[] allMethods = clazz.getDeclaredMethods();

// Поля, конструкторы, аннотации
Field[] fields = clazz.getDeclaredFields();
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
Annotation[] annotations = clazz.getAnnotations();

// Иерархия
Class<?> superClass = clazz.getSuperclass();
Class<?>[] interfaces = clazz.getInterfaces();
```

Подробнее об аннотациях — в [вопросах по аннотациям](java-annotations-interview.md).

## Q26. Какие недостатки и ограничения у `Reflection`?

| Недостаток | Описание |
|-----------|----------|
| **Производительность** | Рефлективные вызовы в 5-50x медленнее прямых (нет JIT-оптимизации, проверки доступа) |
| **Безопасность типов** | Нет проверки типов в compile time — ошибки только в runtime |
| **Инкапсуляция** | `setAccessible(true)` обходит `private` — нарушение модульности |
| **Модульная система** | С `Java 9+` модули блокируют рефлексию (`InaccessibleObjectException`), нужен `--add-opens` |
| **Хрупкость** | Рефакторинг (переименование метода/поля) ломает рефлективный код без ошибок компиляции |

**Вывод для собеседования:** Reflection — **мощный, но опасный инструмент**. Он уместен внутри фреймворков (Spring, Hibernate, Jackson), но почти никогда не должен попадать в бизнес-логику: там он маскирует ошибки до runtime и ломается при рефакторинге. Если задача похожа, но без рефлексии — есть более безопасные альтернативы: `MethodHandle` (`Java 7+`) и `VarHandle` (`Java 9+`) для быстрого доступа, либо генерация кода через Annotation Processing на этапе компиляции.

## Q27. Что такое `package` и `import`?

`package` — пространство имён, группирующее классы и совпадающее со структурой каталогов на диске. Оно избавляет от конфликтов имён: два разных `User` сосуществуют, если лежат в разных пакетах. Полное (квалифицированное) имя класса включает пакет — `com.example.service.UserService`.

`import` — способ обращаться к классу из другого пакета по короткому имени, не выписывая полное каждый раз:
- `import java.util.List;` — конкретный класс
- `import java.util.*;` — все классы пакета (не рекомендуется — затрудняет чтение)
- `import static java.lang.Math.PI;` — статический импорт

Без `package` класс попадает в **безымянный пакет** (default package) — подходит только для обучения, в production недопустимо.

## Q28. (!) Что такое автоупаковка и распаковка (`autoboxing`/`unboxing`)?

`Autoboxing` — автоматическое преобразование примитива в объект-обёртку (`int` → `Integer`), `unboxing` — обратное (`Integer` → `int`). Компилятор подставляет эти преобразования сам, например при `list.add(10)` или `int x = boxed`. Введено в Java 5 ради удобства, но у удобства есть цена: за каждым «бесшумным» boxing скрывается создание объекта, а за unboxing — риск `NullPointerException`.

```java
Integer boxed = 42;            // autoboxing: int → Integer (создаёт новый объект)
int unboxed = boxed;           // unboxing: Integer → int

List<Integer> list = new ArrayList<>();
list.add(10);                  // autoboxing
int value = list.get(0);      // unboxing
```

**Ловушки:**

```java
// 1. Integer cache: -128..127 кэшируются
Integer a = 127;
Integer b = 127;
System.out.println(a == b);  // true — из кэша (Integer.valueOf())

Integer c = 128;
Integer d = 128;
System.out.println(c == d);  // false — новые объекты! (вне диапазона кэша)

// 2. NPE при unboxing null
Integer nullable = null;
int x = nullable;            // NullPointerException!

// 3. Производительность в цикле (Uber столкнулась: 200ms за миллион)
Long sum = 0L;
for (long i = 0; i < 1_000_000; i++) {
    sum += i;  // создаёт новый объект Long на каждой итерации! О(n) объектов
}
```

Подробнее о типах — в [вопросах по типам данных](java-types-interview.md).

## Q29. Что такое `String Pool`?

`String Pool` (пул строк) — специальная область в `heap` (с `Java 7`; до этого был в PermGen), где JVM хранит по одному экземпляру каждого строкового литерала. Когда в коде встречается уже знакомый литерал, JVM не создаёт новую строку, а возвращает ссылку на существующую из пула. Это работает именно потому, что строки иммутабельны — общий объект безопасно делить между всеми.

```java
String s1 = "hello";          // создаётся в Pool
String s2 = "hello";          // берётся из Pool
String s3 = new String("hello"); // новый объект в heap, НЕ из Pool

System.out.println(s1 == s2);  // true  — одна ссылка из Pool
System.out.println(s1 == s3);  // false — разные объекты

String s4 = s3.intern();       // помещает в Pool / возвращает из Pool
System.out.println(s1 == s4);  // true
```

**Зачем нужен:** экономия памяти — одинаковые строки хранятся в одном экземпляре (у LinkedIn это 1.5M строк в пуле). Плюс быстрое сравнение литералов через `==`, так как они ссылаются на один объект. Подробнее — в [вопросах по String](java-string-interview.md).

## Q30. Что такое `null` и как избежать `NPE`?

`null` — специальное значение ссылки, означающее «не указывает ни на какой объект». Любая попытка обратиться через такую ссылку (вызвать метод, прочитать поле) приводит к `NullPointerException` — самой частой ошибке в Java, которую Тони Хоар назвал своей «ошибкой на миллиард долларов».

**Стратегии защиты** — не латать NPE постфактум, а не пускать `null` дальше границ:

```java
// 1. Objects.requireNonNull — fail-fast в конструкторе
public UserService(UserRepository repo) {
    this.repo = Objects.requireNonNull(repo, "repo must not be null");
}

// 2. Optional для возвращаемого значения
public Optional<User> findById(Long id) {
    return Optional.ofNullable(repo.find(id));
}

// 3. Аннотации @NonNull / @Nullable (IDE + static analysis)
public void process(@NonNull String input) { ... }

// 4. Helpful NullPointerExceptions (Java 14+)
// Сообщение: "Cannot invoke String.length() because this.name is null"
```

**Рекомендации:**
- Не возвращать `null` из методов — вместо него `Optional` (для скаляра) или пустая коллекция (для списка). Так вызывающий код не обязан помнить про проверку (Facebook использует этот подход в FOSS).
- Не принимать `null` как аргумент — проверять на входе через `Objects.requireNonNull()`, чтобы упасть сразу и понятно (Uber требует это в production).
- Валидировать в начале метода (`fail-fast`) — ошибка всплывает рядом с причиной, а не глубоко внутри (ранняя проверка экономит 50ms).

## Q31. (!) Что такое `Optional` и когда его использовать?

`Optional<T>` — это контейнер-обёртка, в которой либо лежит значение, либо она пуста. Появился в `Java 8`, чтобы метод честно сообщал в своей сигнатуре: «результата может не быть». Это явная и типобезопасная альтернатива возврату `null`, о котором вызывающий легко забывает. Главная польза не в самом хранении, а в цепочечном API (`map`, `filter`, `orElse`), который заставляет обработать «пустой» случай.

```java
// Создание
Optional<String> present = Optional.of("value");
Optional<String> empty = Optional.empty();
Optional<String> nullable = Optional.ofNullable(maybeNull);

// Использование (функциональный стиль)
String result = findUser(id)
    .map(User::getName)
    .filter(name -> name.length() > 3)
    .orElse("Anonymous");

// ifPresent — без получения значения
findUser(id).ifPresent(user -> sendEmail(user));

// orElseThrow — бросить исключение
User user = findUser(id)
    .orElseThrow(() -> new UserNotFoundException(id));
```

**Когда использовать:**
- Возвращаемое значение метода, где «нет результата» — **нормальный** случай (отсутствие БД записи, поиск без результата)

**Когда НЕ использовать:**
- Поля класса (не сериализуется, усложняет JSON conversion в API)
- Параметры методов (используйте перегрузку или default значения)
- Коллекции (возвращайте пустую коллекцию — это идиоматично в Java)
- `Optional.get()` без `isPresent()` — **антипаттерн** (бросит `NoSuchElementException`, эквивалентно NPE)

Подробнее об `Optional` и `Stream API` — в [вопросах по Java 8](java-8-interview.md).

## Q32. Что такое `Records` (`Java 14+`)?

`Record` — компактный синтаксис для неизменяемых классов-носителей данных (data carriers). Вы объявляете только список компонентов, а компилятор сам генерирует всю рутину: `private final`-поля, канонический конструктор, геттеры (с именем компонента, без `get`), а также корректные `equals()`, `hashCode()` и `toString()` по всем полям. Это устраняет десятки строк шаблонного кода и его типичные баги (забытый `hashCode`, рассинхрон `equals`).

```java
// Объявление — одна строка вместо 50+
public record Point(int x, int y) { }

// Использование
Point p = new Point(3, 4);
int x = p.x();              // геттер без префикса get
System.out.println(p);      // Point[x=3, y=4]

// equals / hashCode — автоматически по всем полям
Point p2 = new Point(3, 4);
System.out.println(p.equals(p2)); // true
```

**Ограничения `Record`** (вытекают из его сути — нести данные неизменяемо):
- Нельзя наследовать: запись неявно `final` и уже расширяет `java.lang.Record`, поэтому `extends` недоступен.
- Нельзя добавлять дополнительные instance-поля — состояние полностью описывается списком компонентов.
- Все поля `final` — после создания объект не изменить.
- Зато можно: статические поля и методы, дополнительные методы экземпляра и реализацию интерфейсов.

**Сценарий применения:** `DTO`, ключи `Map`, value-объекты, ответы `API` — везде, где нужен неизменяемый набор полей с честными `equals`/`hashCode`.

## Q33. Что такое compact constructors в `Records`?

Компактный конструктор — это специальная форма канонического конструктора `Record`, записываемая **без списка параметров** (он подразумевается из компонентов записи). Он нужен для одного: валидации и нормализации входных данных перед тем, как они станут полями. Присваивание `this.x = x` дописывать не надо — компилятор подставляет его автоматически в конце тела.

```java
public record Email(String address) {

    // Компактный конструктор — валидация и нормализация
    public Email {
        Objects.requireNonNull(address, "address must not be null");
        if (!address.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + address);
        }
        address = address.toLowerCase().strip(); // нормализация
        // this.address = address; — генерируется автоматически!
    }
}

// Использование
Email e = new Email("  USER@Example.COM  ");
System.out.println(e.address()); // "user@example.com"
```

**Ключевой нюанс:** в компактном конструкторе писать `this.address = ...` не нужно и нельзя — финальное присваивание полей JVM делает сама после тела. Но можно переприсвоить сам параметр (`address = address.toLowerCase()`) — именно это, уже нормализованное, значение и попадёт в поле.

## Q34. (!) Что такое `Sealed` classes (`Java 17`)?

`Sealed`-класс (или интерфейс) — это запечатанный тип, который через `permits` задаёт **закрытый список** разрешённых наследников. Никто, кроме перечисленных, унаследоваться не сможет. При этом каждый наследник обязан явно выбрать политику дальше: `final` (цепочка закрыта), `sealed` (снова с ограниченным списком) или `non-sealed` (снова открыт всем). Так автор API полностью контролирует иерархию.

```java
public sealed interface Shape
    permits Circle, Rectangle, Triangle {
    double area();
}

public record Circle(double radius) implements Shape {
    public double area() { return Math.PI * radius * radius; }
}

public record Rectangle(double width, double height) implements Shape {
    public double area() { return width * height; }
}

public final class Triangle implements Shape {
    private final double base, height;
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    public double area() { return 0.5 * base * height; }
}
```

**Зачем нужны:**
- **Исчерпывающий `switch`** — раз список наследников конечен и известен компилятору, он проверяет, что обработаны все варианты, и тогда `default` не требуется (с `Java 21`).
- **Контролируемая иерархия** — произвольное наследование запрещено, поэтому никто не «сломает» инварианты API чужим подтипом.
- **Идеальная пара с `Records` и `Pattern Matching`** — вместе они дают в Java аналог алгебраических типов из функциональных языков.

```java
// Java 21: exhaustive switch — все варианты покрыты
double area = switch (shape) {
    case Circle c    -> c.area();
    case Rectangle r -> r.area();
    case Triangle t  -> t.area();
    // нет default — компилятор знает, что все варианты обработаны
};
```

## Q35. Что такое `Pattern Matching` for `instanceof` (`Java 16`)?

Паттерн-матчинг для `instanceof` объединяет проверку типа и приведение в одну конструкцию: если проверка прошла, переменная-биндинг сразу получает нужный тип, и явный cast не нужен. Это убирает классический шаблон «проверил `instanceof` → вручную скастовал → присвоил» и заодно исключает ошибку рассинхрона между проверяемым и приводимым типом.

```java
// До Java 16
if (obj instanceof String) {
    String s = (String) obj;  // явный cast
    System.out.println(s.length());
}

// Java 16+
if (obj instanceof String s) {
    System.out.println(s.length());  // s уже String
}

// Можно использовать в условиях
if (obj instanceof String s && s.length() > 5) {
    process(s);
}
```

Переменная-биндинг `s` видна только там, где компилятор уверен, что `instanceof` вернул `true`. Это называется **flow-scoping**: область видимости определяется не фигурными скобками, а потоком управления — компилятор анализирует, при каких условиях проверка гарантированно прошла.

```java
// Работает даже в отрицании
if (!(obj instanceof String s)) {
    return; // s НЕ доступна здесь
}
// s доступна здесь — мы знаем, что obj — String
process(s);
```

## Q36. Что такое `switch expression` (`Java 14+`)?

`Switch expression` — это `switch`, который сам **возвращает значение** и его можно присвоить переменной, а не только выполнять побочные действия. Использует стрелочный синтаксис `->` вместо `case ... :`, и у него **нет `fall-through`** — после совпавшей ветки выполнение не проваливается дальше, поэтому `break` не нужен и забыть его невозможно.

```java
// Switch expression — компактно и безопасно
String label = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Рабочий день";
    case SATURDAY, SUNDAY -> "Выходной";
};

// yield для блоков
int numericGrade = switch (grade) {
    case "A" -> 5;
    case "B" -> 4;
    case "C" -> 3;
    default -> {
        log.warn("Unknown grade: {}", grade);
        yield 0;  // yield вместо return
    }
};
```

**Преимущества перед классическим `switch`:**
- Нет `fall-through` — не нужен `break`, а значит исчезает целый класс багов с забытым `break`.
- Компилятор проверяет **исчерпываемость** — для `enum` и `sealed`-типов требует покрыть все варианты (иначе ошибка компиляции).
- Несколько меток в одном `case` через запятую.
- Поддержка `null` отдельной веткой `case null ->` (раньше `null` всегда падал с NPE).

## Q37. Что такое `Text Blocks` (`Java 15`)?

`Text Blocks` — многострочные строковые литералы, ограниченные тройными кавычками `"""`. Внутри не нужно экранировать кавычки и склеивать строки через `\n` и `+` — текст пишется как есть. Это резко повышает читаемость встроенных SQL, JSON, HTML и XML.

```java
// До Java 15
String json = "{\n" +
    "    \"name\": \"Иван\",\n" +
    "    \"age\": 30\n" +
    "}";

// Java 15+
String json = """
        {
            "name": "Иван",
            "age": 30
        }
        """;

// SQL-запрос
String sql = """
        SELECT u.name, u.email
        FROM users u
        WHERE u.active = true
          AND u.role = 'ADMIN'
        ORDER BY u.name
        """;
```

**Особенности:**
- **Incidental whitespace** — общий отступ, выровненный по закрывающим `"""`, считается «случайным» и автоматически отрезается, так что в строку попадает только содержательный текст, а не отступы исходника.
- Поддерживает спецпоследовательности `\s` (сохранить концевой пробел) и `\` в конце строки (продолжение без переноса).
- Сочетается с `String.formatted()` для подстановки: `"""Hello, %s""".formatted(name)`.

## Q38. Что такое `Pattern Matching` for `switch` (`Java 21`)?

В `Java 21` паттерн-матчинг для `switch` стал стабильной фичей. Теперь `switch` умеет ветвиться **по типу объекта**, а не только по константам: каждая ветка `case Type t` одновременно проверяет тип и связывает переменную. Дополнительно через `when` можно навесить условие (guarded pattern), а `null` обработать отдельной веткой. Это превращает громоздкие цепочки `if (x instanceof ...)` в компактный и проверяемый компилятором `switch`:

```java
// Матчинг по типу
String describe(Object obj) {
    return switch (obj) {
        case Integer i when i > 0  -> "положительное число: " + i;
        case Integer i             -> "число: " + i;
        case String s when s.isEmpty() -> "пустая строка";
        case String s              -> "строка: " + s;
        case null                  -> "null";
        default                    -> "неизвестный тип";
    };
}

// Вместе с sealed-классами — исчерпывающий switch
sealed interface Result permits Success, Failure {}
record Success(String data) implements Result {}
record Failure(String error) implements Result {}

String handle(Result result) {
    return switch (result) {
        case Success s -> "OK: " + s.data();
        case Failure f -> "Error: " + f.error();
        // default не нужен — sealed + exhaustive
    };
}
```

**Ключевые возможности:**
- `when`-guards — дополнительное условие к ветке (ранее в preview писалось через `&&`); порядок веток важен, более узкие условия идут раньше.
- Матчинг `null` отдельной веткой `case null` вместо неизбежного NPE.
- Исчерпывающий анализ с `sealed`-типами — компилятор гарантирует, что покрыты все наследники, и `default` не нужен.
- Деконструкция `Record` — сразу разобрать на компоненты: `case Point(int x, int y)` (в `Java 21` ещё preview).

## Q39. Что такое `SequencedCollection` (`Java 21`)?

`SequencedCollection` и родственные интерфейсы решают давнюю проблему: у коллекций с понятным порядком элементов не было единого API для работы с «началом» и «концом». У `List` первый элемент брался через `get(0)`, у `Deque` — через `getFirst()`, а у `LinkedHashSet` доступа к концу не было вовсе. `Java 21` добавила в иерархию `Collections Framework` три унифицирующих интерфейса (см. [Java Collections](java-collections-interview.md)):

```
SequencedCollection → List, Deque, LinkedHashSet
SequencedSet        → SortedSet, LinkedHashSet
SequencedMap        → SortedMap, LinkedHashMap
```

Новые методы для работы с первым/последним элементом:

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));

// Доступ к первому и последнему
list.getFirst();   // "a" (бросает NoSuchElementException для пустого)
list.getLast();    // "c"

// Добавление
list.addFirst("z"); // ["z", "a", "b", "c"]
list.addLast("d");  // ["z", "a", "b", "c", "d"]

// Удаление
list.removeFirst(); // "z"
list.removeLast();  // "d"

// Обратный порядок (не копия — view!)
SequencedCollection<String> reversed = list.reversed();

// LinkedHashSet теперь поддерживает те же операции
LinkedHashSet<String> set = new LinkedHashSet<>(Set.of("x", "y", "z"));
set.getFirst(); // элемент с наименьшим порядком вставки
```

Ключевые моменты:
- Методы `getFirst()`/`getLast()` унифицируют доступ — раньше нужны были `list.get(0)` и `list.get(list.size()-1)`
- `reversed()` возвращает `view` (изменения отражаются в оригинале)
- `SequencedMap` добавляет `firstEntry()`, `lastEntry()`, `pollFirstEntry()`, `pollLastEntry()`, `reversed()`

Комбинация `Sealed classes` + `Records` + `Pattern Matching` формирует мощную альтернативу паттерну Visitor из [ООП](java-oop-interview.md).

---

## See also

- [ООП в Java](java-oop-interview.md) — наследование, полиморфизм, инкапсуляция, абстракция
- [Java 8](java-8-interview.md) — лямбды, `Stream API`, `Optional`, функциональные интерфейсы
- [Java Collections](java-collections-interview.md) — `List`, `Set`, `Map`, `HashMap` internals
- [Java Concurrency](java-concurrency-interview.md) — многопоточность, `synchronized`, `volatile`
- [JVM](../../jvm/jvm-interview.md) — устройство JVM, `GC`, память, `JIT`-компиляция
- [Generics](java-generics-interview.md) — дженерики, `type erasure`, `wildcards`
- [Java Exceptions](java-exceptions-interview.md) — обработка исключений, `checked` vs `unchecked`
- [Java String](java-string-interview.md) — строки, `String Pool`, иммутабельность строк
- [Система типов Java](java-types-interview.md) — примитивы, обёртки, `autoboxing`
- [Java Annotations](java-annotations-interview.md) — аннотации, `Retention`, `Target`, `APT`
- [Java 17-21](java-17-21-interview.md) — современные возможности: `sealed`, `records`, `pattern matching`
- [Java Serialization](java-serialization-interview.md) — `Serializable`, `Externalizable`, `transient`
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — паттерны проектирования, применяемые в Java
- [Профилирование приложений](../../performance/application-profiling-interview.md) — поиск узких мест, `heap dump`, `JFR`

- [Java 17-21](java-17-21-interview.md)
- [Java 8](java-8-interview.md)
- [Java Annotations](java-annotations-interview.md)
- [Java Collections](java-collections-interview.md)
- [Java Concurrency](java-concurrency-interview.md)
- [Java Conditional Statements](java-conditional-statements-interview.md)
