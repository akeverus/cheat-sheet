---
title: "Вопросы на собеседовании: Scala"
description: "Scala 2.x и 3.x: типы (immutable/mutable, case class, sealed, traits), pattern matching, implicits/given, futures, монады (Option, Either, Try), Cats, ZIO, Spark, Akka, интероп с Java"
tags:
  - interview
  - programming-languages
  - scala-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Scala"
  - "Scala interview"
  - "Scala собеседование"
prerequisites:
  - "[[scala-basics]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Scala`

`Scala` — гибридный (объектно-функциональный) язык на JVM. Создан Мартином Одерским (2003), работает с Java-экосистемой. Ключевые применения: **data engineering** (Spark, Flink, Kafka Streams), **бэкенд** (Akka, http4s, ZIO), **DSL и сложные доменные модели**. Сложнее Java и Kotlin, но даёт мощную систему типов.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Scala Official Documentation](https://docs.scala-lang.org/)
- [Scala 3 Book](https://docs.scala-lang.org/scala3/book/)
- [Scala 3 Migration Guide](https://docs.scala-lang.org/scala3/guides/migration/)
- [Scala by Example — Baeldung](https://www.baeldung.com/scala/)
- [Cats Library](https://typelevel.org/cats/)
- [ZIO Documentation](https://zio.dev/)
- [Akka Documentation](https://doc.akka.io/)
- [Apache Spark Programming Guide](https://spark.apache.org/docs/latest/rdd-programming-guide.html)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Scala и зачем она нужна?](#q1--что-такое-scala-и-зачем-она-нужна)
- [Q2. (!) Чем Scala отличается от Java?](#q2--чем-scala-отличается-от-java)
- [Q3. (!) Чем Scala отличается от Kotlin?](#q3--чем-scala-отличается-от-kotlin)
- [Q4. (!) Что нового в Scala 3?](#q4--что-нового-в-scala-3)

**Типы и переменные**
- [Q5. (!) В чём разница между val, var и def?](#q5--в-чём-разница-между-val-var-и-def)
- [Q6. (!) Как работает вывод типов (type inference) в Scala?](#q6--как-работает-вывод-типов-type-inference-в-scala)
- [Q7. Что такое Unit, Nothing, Any, AnyVal, AnyRef?](#q7-что-такое-unit-nothing-any-anyval-anyref)
- [Q8. (!) Зачем нужны Option, Some, None?](#q8--зачем-нужны-option-some-none)
- [Q9. Как обрабатывать ошибки через Either и Try?](#q9-как-обрабатывать-ошибки-через-either-и-try)

**Классы**
- [Q10. (!) Что такое case class?](#q10--что-такое-case-class)
- [Q11. (!) Что такое sealed trait и почему он важен?](#q11--что-такое-sealed-trait-и-почему-он-важен)
- [Q12. В чём разница между trait и abstract class?](#q12-в-чём-разница-между-trait-и-abstract-class)
- [Q13. Что такое object и companion object?](#q13-что-такое-object-и-companion-object)

**Pattern matching**
- [Q14. (!) Как работает pattern matching: основы](#q14--как-работает-pattern-matching-основы)
- [Q15. (!) Как работает деструктуризация case class через unapply?](#q15--как-работает-деструктуризация-case-class-через-unapply)
- [Q16. Как делать pattern matching на коллекциях?](#q16-как-делать-pattern-matching-на-коллекциях)

**Функциональное программирование**
- [Q17. (!) Что такое лямбды и higher-order функции?](#q17--что-такое-лямбды-и-higher-order-функции)
- [Q18. (!) Чем Scala отличается в функциональной парадигме от Java?](#q18--чем-scala-отличается-в-функциональной-парадигме-от-java)
- [Q19. Что такое for-comprehension?](#q19-что-такое-for-comprehension)
- [Q20. (!) Что такое currying и partial application?](#q20--что-такое-currying-и-partial-application)
- [Q21. Что такое хвостовая рекурсия и @tailrec?](#q21-что-такое-хвостовая-рекурсия-и-tailrec)

**Implicits / Given (Scala 3)**
- [Q22. (!) Что такое implicit в Scala 2?](#q22--что-такое-implicit-в-scala-2)
- [Q23. (!) Что такое given/using в Scala 3?](#q23--что-такое-givenusing-в-scala-3)
- [Q24. Implicit conversion: в чём польза и опасность?](#q24-implicit-conversion-в-чём-польза-и-опасность)
- [Q25. Что такое паттерн type class?](#q25-что-такое-паттерн-type-class)

**Коллекции**
- [Q26. (!) Чем Scala-коллекции отличаются от Java?](#q26--чем-scala-коллекции-отличаются-от-java)
- [Q27. В чём разница между immutable и mutable коллекциями?](#q27-в-чём-разница-между-immutable-и-mutable-коллекциями)
- [Q28. (!) Как работают map, flatMap, filter, fold?](#q28--как-работают-map-flatmap-filter-fold)
- [Q29. Что такое View и ленивые коллекции?](#q29-что-такое-view-и-ленивые-коллекции)

**Concurrency**
- [Q30. (!) Что такое Future и как устроена асинхронность в Scala?](#q30--что-такое-future-и-как-устроена-асинхронность-в-scala)
- [Q31. Как комбинировать несколько Future?](#q31-как-комбинировать-несколько-future)
- [Q32. Что такое ExecutionContext?](#q32-что-такое-executioncontext)
- [Q33. (!) Что такое Akka и actor model?](#q33--что-такое-akka-и-actor-model)

**Effect systems**
- [Q34. (!) Что такое ZIO и Cats Effect?](#q34--что-такое-zio-и-cats-effect)
- [Q35. Что означают три параметра в ZIO[R, E, A]?](#q35-что-означают-три-параметра-в-zior-e-a)

**Java интероп**
- [Q36. (!) Как Scala взаимодействует с Java?](#q36--как-scala-взаимодействует-с-java)
- [Q37. Какие подводные камни у интеропа Scala и Java?](#q37-какие-подводные-камни-у-интеропа-scala-и-java)

**Применения**
- [Q38. (!) Где Scala применяется в production?](#q38--где-scala-применяется-в-production)
- [Q39. (!) Как связаны Apache Spark и Scala?](#q39--как-связаны-apache-spark-и-scala)
- [Q40. (!) Какие у Scala минусы?](#q40--какие-у-scala-минусы)

## Q1. (!) Что такое Scala и зачем она нужна?

`Scala` (Scalable Language) — статически типизированный язык на JVM, который объединяет две парадигмы: **объектно-ориентированную и функциональную**. Создан **Мартином Одерским** (автором `javac`), первая версия вышла в 2003. Идея — взять зрелую экосистему Java и дать поверх неё гораздо более выразительную систему типов и инструменты функционального программирования.

**Где применяют:**
- **Big Data** — Apache Spark, Flink, Kafka Streams написаны на Scala, и их Scala-API самый родной.
- **Высоконагруженный backend** — Akka, http4s, Play, ZIO.
- **DSL** — мощная система типов позволяет строить выразительные доменные языки.
- **Сложные доменные модели** — функциональный стиль (immutable-данные, ADT) плюс OOP.

**Сильные стороны:**
- Мощная система типов: generics, variance, type classes, dependent types (Scala 3).
- Pattern matching как часть языка (first-class), а не библиотечный костыль.
- Immutability по умолчанию — данные не меняются на месте, меньше скрытых багов.
- 100% interop с Java в обе стороны — можно постепенно внедрять в Java-проект.

## Q2. (!) Чем Scala отличается от Java?

Обе работают на JVM и совместимы, но Scala изначально функциональнее и выразительнее: immutability и pattern matching встроены в язык, а не добавлены позже. Цена — более сложный onboarding и более медленная компиляция.

| Критерий | Scala | Java |
|----------|-------|------|
| Парадигма | OOP + FP | OOP (FP частично с Java 8+) |
| Иммутабельность | По умолчанию | Опционально |
| Type inference | Сильный | Слабый (var с Java 10) |
| Pattern matching | First-class | switch (Java 21+) |
| Higher-order types | Да (HKT) | Нет |
| Implicits / Given | Да | Нет |
| Boilerplate | Меньше (case class) | Больше |
| Скорость компиляции | Медленнее | Быстрее |
| Onboarding | Сложнее | Проще |
| Type system | Очень мощная | Простая |
| `null` | Опционально (через Option) | Везде |

```scala
case class User(name: String, age: Int)
val users = List(User("Alice", 30), User("Bob", 25))
val names = users.filter(_.age > 25).map(_.name)
```

vs Java:

```java
public record User(String name, int age) { }
List<User> users = List.of(new User("Alice", 30), new User("Bob", 25));
List<String> names = users.stream()
    .filter(u -> u.age() > 25)
    .map(User::name)
    .toList();
```

Scala лаконичнее: тот же фильтр-и-маппинг записывается без отдельного `stream()`/`.toList()` и без явного типа списка — компилятор всё выводит сам.

## Q3. (!) Чем Scala отличается от Kotlin?

Оба — JVM-языки, которые хотят быть «лучше Java», но цели разные. Kotlin прагматичен и нацелен на плавную замену Java с минимальной кривой обучения; Scala academic-ориентирована и даёт максимально мощную систему типов ценой сложности.

| Критерий | Scala | Kotlin |
|----------|-------|--------|
| Создатель | Мартин Одерский (academia) | JetBrains |
| Год | 2003 | 2011 |
| Целевая платформа | JVM, JS, Native | JVM, Android, JS, Native |
| Type system | Очень мощная | Прагматичная |
| Pattern matching | First-class, мощный | when (проще) |
| Higher-kinded types | Да | Нет (workaround через типы) |
| Implicits | Да (Scala 2/3) | Extension functions (проще) |
| Coroutines | Akka, ZIO, Cats Effect | Kotlin Coroutines (стандарт) |
| Onboarding | Сложно | Просто |
| Ecosystem | Spark, Akka, Play | Android, Spring, Ktor |

**Scala** — для тех, кому нужна **максимальная** выразительность системы типов.
**Kotlin** — для тех, кто хочет **удобную замену Java** с минимальной learning curve.

Подробнее — в [Kotlin](../kotlin/kotlin-interview.md).

## Q4. (!) Что нового в Scala 3?

Scala 3 (2021, кодовое имя Dotty) — крупнейший пересмотр языка: новый компилятор, более чистый синтаксис вокруг implicits и несколько новых конструкций системы типов. Главная цель — убрать «магию» и сделать мощные возможности более явными.

**Новые синтаксические возможности:**
- **Indentation-based syntax** (отступы вместо фигурных скобок, как в Python; опционально):
```scala
def factorial(n: Int): Int =
  if n == 0 then 1
  else n * factorial(n - 1)
```
- **`given/using` вместо `implicit`** — тот же механизм, но явный и читаемый синтаксис.
- **`enum`** — настоящие перечисления вместо обходного приёма через sealed trait + case object.
- **`extension methods`** — добавление методов к чужим типам без implicit-обёрток.
- **`opaque types`** — типобезопасные обёртки без накладных расходов в рантайме.
- **`union types`** (`String | Int`) — «или один тип, или другой».
- **`intersection types`** (`Animal & HasFur`) — «и тот, и другой тип одновременно».
- **Top-level definitions** — функции и значения прямо в файле, без обёртки в `object`.

**Внутренние изменения:**
- Новый компилятор (Dotty).
- Совместимость с Scala 2.13+ на уровне промежуточного формата TASTy — модули двух версий могут жить вместе.
- Улучшенный вывод типов (type inference).
- Match types — вычисление типов через pattern matching на уровне типов.

```scala
// Scala 3 enum
enum Color:
  case Red, Green, Blue
  case Custom(rgb: String)

// Extension methods
extension (s: String)
  def shout: String = s.toUpperCase + "!"

"hello".shout // "HELLO!"
```

## Q5. (!) В чём разница между val, var и def?

Все три объявляют именованную сущность, но различаются по двум осям: можно ли её переприсвоить и когда вычисляется значение. `val` — неизменяемая ссылка, `var` — изменяемая, `def` — метод, тело которого пересчитывается при каждом обращении.

```scala
val x = 5      // immutable, вычисляется один раз
var y = 5      // mutable
def z = 5      // вычисляется каждый раз при обращении
def f() = 5    // метод с () — может иметь side effects (соглашение)
lazy val w = compute() // вычисляется при первом обращении (lazy)
```

| Объявление | Изменяемость | Вычисление |
|------------|--------------|------------|
| `val` | immutable | один раз при инициализации |
| `var` | mutable | один раз при инициализации |
| `def` | immutable | каждый раз при обращении |
| `lazy val` | immutable | один раз при первом обращении |

Разница между `val` и `lazy val` важна: `lazy val` откладывает вычисление до первого реального обращения и кэширует результат — полезно для дорогой инициализации, которая может вообще не понадобиться.

**Эмпирическое правило:** по умолчанию всегда `val`; `var` — только когда изменяемость действительно нужна (например, локальный аккумулятор в цикле).

## Q6. (!) Как работает вывод типов (type inference) в Scala?

Компилятор сам выводит типы локальных переменных и значений, поэтому их редко пишут явно. А вот типы параметров методов почти всегда указывают руками — компилятор не может вывести их «снаружи».

```scala
val x = 5                    // Int
val list = List(1, 2, 3)     // List[Int]
def double(x: Int) = x * 2   // возвращает Int

// Параметры функций — тип нужен явно
def add(a: Int, b: Int) = a + b

// Лямбды — выводятся из контекста
val nums = List(1, 2, 3)
nums.map(x => x * 2)         // x: Int — выводится из List[Int]
nums.map(_ * 2)              // ещё короче
```

Scala 3 выводит типы точнее, особенно для дженериков, где Scala 2 иногда требовала явных аннотаций.

## Q7. Что такое Unit, Nothing, Any, AnyVal, AnyRef?

Это узловые типы иерархии Scala. В отличие от Java, где примитивы и объекты живут раздельно, в Scala всё — часть единого дерева с корнем `Any` сверху и `Nothing` снизу.

```
                    Any
                   /   \
              AnyVal   AnyRef
              /  |       \
          Int Boolean   String List
            \  |         /
             Nothing (subtype of all)
            (Null - subtype of AnyRef)
```

| Тип | Назначение |
|-----|------------|
| `Any` | Top type — родитель всех типов |
| `AnyVal` | Value types (примитивы): `Int`, `Double`, `Boolean`, `Unit`, ... |
| `AnyRef` | Reference types (объекты, =`java.lang.Object`) |
| `Unit` | Аналог `void` в Java, единственное значение `()` |
| `Nothing` | Bottom type — подтип всех. Используется для `throw`, `???` |
| `Null` | Subtype of `AnyRef`, единственное значение `null` |

```scala
def fail(): Nothing = throw new Exception("boom")
val list: List[Nothing] = List() // подходит как List[String], List[Int]...
```

Практический смысл `Nothing` — он подтип любого типа, поэтому `throw` или пустой `List()` подходят в любой контекст: пустой список совместим и с `List[String]`, и с `List[Int]`.

## Q8. (!) Зачем нужны Option, Some, None?

`Option[T]` — типобезопасная замена `null`: значение либо есть (`Some(value)`), либо его нет (`None`). Главное отличие от `null` в том, что отсутствие значения теперь видно в типе, и компилятор заставляет его обработать.

```scala
val maybeName: Option[String] = users.find(_.age > 30).map(_.name)

maybeName match {
  case Some(name) => println(s"Found: $name")
  case None       => println("Not found")
}

// Functional API
val length = maybeName.map(_.length).getOrElse(0)

// for comprehension
val result = for {
  name  <- findUser(id).map(_.name)
  email <- findEmail(name)
} yield email
```

**Преимущества над null:**
- Компилятор требует обработать случай `None` — забыть нельзя.
- Композируется через `map`, `flatMap`, for-comprehension — цепочки без ручных проверок на null.
- Нет `NullPointerException`.

В Java аналог — `Optional<T>`, в Kotlin — nullable-типы (`String?`). У Scala API богаче и функциональнее.

## Q9. Как обрабатывать ошибки через Either и Try?

`Either[L, R]` хранит ровно одно из двух значений: `Left(L)` (по соглашению — ошибка) или `Right(R)` (успех). В отличие от исключений, тип ошибки виден в сигнатуре, и компилятор заставляет её обработать.

```scala
def parseAge(s: String): Either[String, Int] =
  s.toIntOption match {
    case Some(n) if n >= 0 => Right(n)
    case Some(n)            => Left(s"Negative: $n")
    case None               => Left(s"Not a number: $s")
  }

parseAge("30") match {
  case Right(age) => println(s"OK: $age")
  case Left(err)  => println(s"Error: $err")
}
```

`Try[T]` — это `Success(value)` или `Failure(throwable)`. Удобен, когда вызываемый код может бросить исключение (например, Java API): `Try(...)` ловит его и кладёт в `Failure`, а не роняет программу.

```scala
import scala.util.Try

val result: Try[Int] = Try(Integer.parseInt("not a number"))
result.getOrElse(0)
result.recover { case _: NumberFormatException => 0 }
```

**Когда что:** `Try` — для границы с кодом, бросающим исключения; `Either` — для собственной доменной логики, где удобнее описать ошибки своим типом. В FP-стиле чаще берут `Either`: типизированная ошибка нагляднее и безопаснее любого `throw`.

## Q10. (!) Что такое case class?

`case class` — это класс, для которого компилятор автоматически генерирует кучу шаблонного кода, типичного для класса-носителя данных. Достаточно объявить поля — остальное появится само:
- Конструктор, поля которого по умолчанию public и immutable.
- `equals` / `hashCode` / `toString` на основе значений полей.
- `apply` в companion object — создание без `new`.
- `unapply` — деструктуризация в pattern matching.
- `copy` — создание изменённой копии (исходный объект не трогается).

```scala
case class User(name: String, age: Int)

val alice = User("Alice", 30)
val older = alice.copy(age = 31)

alice == User("Alice", 30) // true (value equality, не reference!)

alice match {
  case User(name, age) => println(s"$name is $age")
}
```

Похоже на `record` из Java 14+, но мощнее: `case class` даёт `copy`, может реализовывать traits и участвовать в полноценном pattern matching.

## Q11. (!) Что такое sealed trait и почему он важен?

`sealed` — модификатор, который разрешает наследоваться от типа **только внутри того же файла**. Благодаря этому компилятор знает полный список наследников и может проверять, что pattern matching охватил **все** варианты (исчерпывающая проверка):

```scala
sealed trait Shape
case class Circle(radius: Double) extends Shape
case class Rectangle(w: Double, h: Double) extends Shape
case class Triangle(a: Double, b: Double, c: Double) extends Shape

def area(s: Shape): Double = s match {
  case Circle(r)         => Math.PI * r * r
  case Rectangle(w, h)   => w * h
  case Triangle(a, b, c) =>
    val p = (a + b + c) / 2
    Math.sqrt(p * (p - a) * (p - b) * (p - c))
  // если забыть один кейс — компилятор предупредит!
}
```

Связка `sealed trait` + `case class` — это **ADT** (алгебраические типы данных) из Haskell/OCaml: тип задаётся как закрытый набор вариантов. Это ключевой приём для типобезопасного моделирования домена: добавил новый вариант — компилятор сам подсветит все `match`, которые надо дополнить.

Аналоги: в Java 17+ — `sealed classes`, в Kotlin — `sealed class/interface`.

## Q12. В чём разница между trait и abstract class?

Оба описывают абстракцию с частичной реализацией, но trait поддерживает множественное наследование (mixin) и потому используется чаще; abstract class берут в основном там, где нужны параметры конструктора (в Scala 2) или совместимость с Java.

| Критерий | trait | abstract class |
|----------|-------|----------------|
| Множественное наследование | Да | Нет |
| Параметры конструктора | Да (с Scala 3), Нет (Scala 2) | Да |
| Совместимость с Java | Хуже | Лучше |
| Использование | Mixin, ADT | Корневые классы |

```scala
trait Logger {
  def log(msg: String): Unit = println(s"[LOG] $msg")
}

trait Database {
  def query(sql: String): String
}

class App extends Database with Logger {
  def query(sql: String) = ???
}
```

Итог: trait — основной инструмент абстракции и переиспользования поведения; abstract class берут реже — в основном для совместимости с Java.

## Q13. Что такое object и companion object?

`object` объявляет singleton — единственный экземпляр, который создаётся лениво при первом обращении. Используется там, где в Java были бы `static`-методы и константы:

```scala
object MathUtils {
  def square(x: Int): Int = x * x
}

MathUtils.square(5) // 25
```

`companion object` — это `object` с тем же именем, что и класс, в том же файле. Он имеет доступ к `private`-членам класса, поэтому туда удобно класть factory-методы и логику создания:

```scala
class User private (val name: String, val age: Int)

object User {
  def apply(name: String, age: Int): User = new User(name, age) // factory
  def fromString(s: String): Option[User] = ???
}

User("Alice", 30) // эквивалентно User.apply("Alice", 30)
```

**Сценарии применения:** factory-методы (`apply`), константы, методы-аналоги `static`.

## Q14. (!) Как работает pattern matching: основы

Это аналог `switch`, но гораздо мощнее: он умеет сопоставлять не только значения, но и структуру — тип, форму case-класса, голову и хвост списка, кортеж — и сразу извлекать части совпавшего значения в переменные.

```scala
val x: Any = 42

x match {
  case 1                       => "one"
  case n: Int if n > 0          => s"positive $n"
  case s: String                => s"string $s"
  case List(1, 2, _*)           => "list starting with 1, 2"
  case (a, b)                   => s"tuple $a, $b"
  case _                        => "something else"
}
```

Pattern matching работает с:
- Литералами (`1`, `"abc"`).
- Типами, в том числе с guard-условиями (`case n: Int if n > 0`).
- Case-классами — с деструктуризацией полей.
- Списками и кортежами.
- Регулярными выражениями.
- Собственными экстракторами через `unapply`.

## Q15. (!) Как работает деструктуризация case class через unapply?

Деструктуризация в `match` опирается на метод `unapply`, который для `case class` генерируется автоматически. Он «разбирает» объект обратно на поля, и они привязываются к переменным шаблона:

```scala
case class User(name: String, age: Int)

val u = User("Alice", 30)
u match {
  case User("Alice", age) => s"Alice is $age"
  case User(n, _)         => s"Some user $n"
}
```

`unapply` можно написать и вручную — тогда любой объект-экстрактор работает в `match` как case class. `Some(...)` означает «совпало, вот извлечённые значения», `None` — «не подходит»:

```scala
object Email {
  def unapply(s: String): Option[(String, String)] = {
    val parts = s.split("@")
    if (parts.length == 2) Some((parts(0), parts(1))) else None
  }
}

"alice@example.com" match {
  case Email(user, domain) => s"$user at $domain"
  case _                   => "not an email"
}
```

## Q16. Как делать pattern matching на коллекциях?

Списки сопоставляют по структуре «голова и хвост», а массивы — по позициям элементов. Это позволяет разбирать коллекцию без ручной индексации:

```scala
val list = List(1, 2, 3, 4, 5)

list match {
  case Nil           => "empty"
  case x :: Nil      => s"single: $x"
  case x :: y :: tail => s"two: $x, $y, rest: $tail"
  case _             => "more"
}

// Для массивов
val arr = Array(1, 2, 3)
arr match {
  case Array(1, _, _) => "starts with 1"
  case _              => "other"
}
```

`::` — это оператор cons (`head :: tail`), он разделяет список на первый элемент и остаток. Работает именно так, потому что `List` реализован как односвязный список.

## Q17. (!) Что такое лямбды и higher-order функции?

В Scala функции — полноценные значения: их можно присвоить переменной, передать в другую функцию и вернуть из неё. **Higher-order функция (HOF)** — это функция, которая принимает функцию аргументом или возвращает функцию.

```scala
val double: Int => Int = _ * 2
val add: (Int, Int) => Int = _ + _

// HOF — функция, принимающая или возвращающая функцию
def applyTwice(f: Int => Int, x: Int): Int = f(f(x))
applyTwice(double, 5) // 20

// Возвращающая функцию
def adder(n: Int): Int => Int = x => x + n
val add5 = adder(5)
add5(10) // 15
```

Подробнее — в [Kotlin](../kotlin/kotlin-interview.md) (похожий синтаксис).

## Q18. (!) Чем Scala отличается в функциональной парадигме от Java?

В Java функциональные возможности добавлены поверх изначально императивного языка (лямбды и Stream API с Java 8), а в Scala FP встроена с самого начала: immutability по умолчанию, ADT, currying, for-comprehension, type classes.

| Аспект | Scala | Java |
|--------|-------|------|
| Иммутабельность | По умолчанию (`val`, immutable collections) | Опциональна |
| HOF | First-class | Через `Function` interfaces |
| Pattern matching | Мощный | Базовый (Java 21+) |
| ADT | sealed traits + case classes | sealed classes (Java 17+) |
| Currying | Built-in | Нет |
| Higher-kinded types | Да | Нет |
| Type classes | Через implicits | Нет |
| For comprehension | Да | Нет |
| Tail recursion | `@tailrec` | Нет |

Итог: Scala «функциональнее» из коробки, но требует больше дисциплины — мощные инструменты легко применить неаккуратно.

## Q19. Что такое for-comprehension?

`for { ... } yield` — это синтаксический сахар, который компилятор разворачивает в цепочку вызовов `flatMap` / `map` / `filter`. Он не привязан к коллекциям: работает с любым типом, у которого есть эти методы — `Option`, `Either`, `Future`, `IO`.

```scala
val users = List(User("Alice", 30), User("Bob", 25))
val emails = List("alice@example.com", "bob@example.com")

// Эквивалентны
val pairs1 = users.flatMap(u =>
  emails.filter(_.contains(u.name.toLowerCase))
        .map(e => (u, e))
)

val pairs2 = for {
  u <- users
  e <- emails
  if e.contains(u.name.toLowerCase)
} yield (u, e)
```

Главная польза — **цепочки монадических вычислений** (Option, Either, Future, IO): код читается как последовательность шагов, а «протаскивание» промежуточного состояния (отсутствие значения, ошибка, асинхронность) берёт на себя `flatMap`:

```scala
val result: Option[Int] = for {
  user  <- findUser(id)
  email <- user.email
  count <- emailCount(email)
} yield count

// Если любой шаг = None → result = None
```

Здесь если любой шаг вернёт `None`, вся цепочка сразу даст `None` — без ручных проверок после каждого вызова.

## Q20. (!) Что такое currying и partial application?

**Currying** — это запись функции с несколькими параметрами как нескольких списков аргументов, по одному за раз. Тогда функцию можно вызвать частично, зафиксировав первые аргументы и получив новую функцию от оставшихся (**partial application**):

```scala
def add(a: Int)(b: Int): Int = a + b

val addFive = add(5) _   // частичное применение, тип Int => Int
addFive(3)               // 8

// Эквивалентно
def add(a: Int): Int => Int = b => a + b
```

**Сценарии применения:** фабрики, builder'ы, настройка поведения, реализация DSL. Отдельный частый приём — вынести implicit-параметр в последний список аргументов, чтобы компилятор подставлял его автоматически:

```scala
def withResource[A, B](res: A)(action: A => B)(implicit close: A => Unit): B = {
  try action(res)
  finally close(res)
}
```

## Q21. Что такое хвостовая рекурсия и @tailrec?

JVM **не делает** оптимизацию хвостовых вызовов (TCO) сама, поэтому обычная рекурсия на большой глубине упирается в `StackOverflowError`. Но компилятор Scala умеет превращать **хвостово-рекурсивную** функцию в обычный `while`-цикл — без роста стека.

```scala
import scala.annotation.tailrec

@tailrec
def factorial(n: Int, acc: Int = 1): Int =
  if (n == 0) acc
  else factorial(n - 1, n * acc) // tail call

// БЕЗ @tailrec — будет StackOverflow для больших n
def factNotTail(n: Int): Int =
  if (n == 0) 1
  else n * factNotTail(n - 1) // не tail call (умножение после)
```

«Хвостовой» вызов — это когда рекурсивный вызов является самым последним действием (его результат сразу возвращается). В `factNotTail` после вызова идёт ещё умножение, поэтому он не хвостовой. Аннотация `@tailrec` ничего не оптимизирует сама — она заставляет компилятор **проверить**, что функция действительно хвостово-рекурсивна, и упасть с ошибкой компиляции, если нет. Подробнее — в [Рекурсия](../../algorithms/algorithmic-paradigms/recursion-interview.md).

## Q22. (!) Что такое implicit в Scala 2?

`implicit` помечает значение или параметр, который компилятор **подставляет автоматически** из видимой области (scope), если вы его не указали явно. На этом механизме построены implicit-параметры, неявные преобразования, extension-методы и type classes.

```scala
// Implicit parameter
def greet(name: String)(implicit greeting: String): String = s"$greeting, $name"

implicit val defaultGreeting: String = "Hello"
greet("Alice") // "Hello, Alice"
```

**Implicit conversion:**

```scala
implicit def intToString(x: Int): String = x.toString
val s: String = 42 // автоматически конвертируется
```

**Implicit class** (extension methods):

```scala
implicit class StringOps(s: String) {
  def shout: String = s.toUpperCase + "!"
}

"hello".shout // "HELLO!"
```

**Подводные камни:** implicits очень мощны, но их легко не заметить — глядя на вызов, не всегда понятно, откуда компилятор взял значение, и где искать ошибку при неоднозначности. Именно поэтому в Scala 3 их разбили на отдельные явные конструкции `given/using`, `extension`, `Conversion`.

## Q23. (!) Что такое given/using в Scala 3?

Scala 3 заменяет единый `implicit` на несколько специализированных конструкций — тот же механизм, но намерение видно явно:

```scala
// Implicit value → given
given defaultGreeting: String = "Hello"

// Implicit parameter → using
def greet(name: String)(using greeting: String): String = s"$greeting, $name"

// Extension methods вместо implicit class
extension (s: String)
  def shout: String = s.toUpperCase + "!"

// Implicit conversion → Conversion type class
given Conversion[Int, String] = _.toString
```

**Что это даёт:**
- `given`-значения видно как отдельную категорию, не путаются с обычными.
- Для extension-методов — отдельный явный синтаксис вместо implicit-классов.
- Меньше скрытой «магии»: преобразование объявляется явно через `Conversion`.

## Q24. Implicit conversion: в чём польза и опасность?

**Польза:** автоматическое преобразование между совместимыми типами без ручного вызова — например, `Int` → `BigDecimal`. Код становится короче.

**Подводные камни:**
- **Скрытое поведение** — преобразование происходит молча, и из места вызова не видно, что оно вообще было.
- **Неоднозначность** — если в scope несколько подходящих implicit-преобразований, компилятор не выберет (ambiguity).
- **Производительность** — каждое преобразование создаёт новый объект.

**Рекомендация:**
- Применять **только** для type classes, а не как «удобный автокаст».
- Никогда не конвертировать неявно между неродственными типами — это источник трудноуловимых багов.
- В Scala 3 использовать `given Conversion[A, B]` — он делает намерение явным.

## Q25. Что такое паттерн type class?

**Type class** — это паттерн для **ad-hoc-полиморфизма**: способ добавить поведение к типу, не меняя сам тип и не наследуясь от него. В Scala его реализуют через trait с параметром-типом и набор implicit-инстансов для конкретных типов:

```scala
// Type class определение
trait Show[A] {
  def show(a: A): String
}

// Instances
implicit val intShow: Show[Int] = (a: Int) => a.toString
implicit val stringShow: Show[String] = (a: String) => a

// Использование
def print[A](a: A)(implicit s: Show[A]): String = s.show(a)
print(42)       // "42"
print("hello")  // "hello"
print(3.14)     // ОШИБКА компиляции — нет Show[Double]
```

Ключевой плюс: поддержку нового типа добавляют, просто объявив для него инстанс — не трогая ни сам тип, ни код, который его использует. Попытка вызвать `print` для типа без инстанса — ошибка компиляции, а не рантайма.

**Где используется:** `Cats` (Functor, Monad, Applicative), `Circe` (encoders/decoders), `Spark` (Encoder). В Java прямого аналога нет; в Kotlin похожее частично решают extension-функции.

## Q26. (!) Чем Scala-коллекции отличаются от Java?

Главное отличие — Scala по умолчанию даёт immutable persistent-коллекции с богатым функциональным API прямо на самой коллекции, тогда как в Java большинство коллекций изменяемы, а функциональные операции вынесены в отдельный Stream API.

| Критерий | Scala | Java |
|----------|-------|------|
| Иммутабельность | По умолчанию (`scala.collection.immutable`) | Опционально |
| API | Богатый — `map`, `flatMap`, `filter`, `fold`, ... | Упрощён до Stream API |
| Persistent data structures | Да (efficient updates) | Нет (нужны copies) |
| Lazy collections | `LazyList`, `View` | `Stream` (одноразовый) |
| Pattern matching | Built-in | Нет |

```scala
val list = List(1, 2, 3, 4, 5)
val sum = list.sum
val evens = list.filter(_ % 2 == 0)
val doubled = list.map(_ * 2)
val grouped = list.groupBy(_ % 2)    // Map(0 -> List(2,4), 1 -> List(1,3,5))
val taken = list.take(3)             // List(1, 2, 3)
val (small, big) = list.partition(_ < 3) // (List(1,2), List(3,4,5))
```

## Q27. В чём разница между immutable и mutable коллекциями?

Immutable-коллекция при «изменении» возвращает новую коллекцию, а исходная остаётся нетронутой; mutable меняется на месте. По умолчанию в scope подключены именно immutable-версии.

```scala
// По умолчанию — immutable
import scala.collection.immutable._
val list = List(1, 2, 3)         // immutable
val newList = list :+ 4           // создаёт новый список

// Если нужна mutable
import scala.collection.mutable
val buf = mutable.ArrayBuffer(1, 2, 3)
buf += 4 // mutates in-place
```

**Эмпирическое правило:** по умолчанию immutable; mutable — только когда есть явная потребность (горячий цикл, локальный буфер).

Может показаться, что immutable означает «копировать всё целиком при каждом изменении», но это не так: это **persistent data structures** — они переиспользуют общую часть структуры, поэтому обновления остаются эффективными (HAMT для `Map`, дерево векторов для `Vector`).

## Q28. (!) Как работают map, flatMap, filter, fold?

Это базовые комбинаторы для обработки коллекций без явных циклов:
- **`map`** — применяет функцию к каждому элементу, сохраняя структуру.
- **`flatMap`** — то же, но функция возвращает коллекцию, а результаты «склеиваются» в один уровень.
- **`filter`** — оставляет только подходящие под предикат элементы.
- **`fold`** — сворачивает коллекцию в одно значение, протаскивая аккумулятор.

```scala
val list = List(1, 2, 3, 4, 5)

list.map(_ * 2)              // List(2, 4, 6, 8, 10)
list.filter(_ > 2)           // List(3, 4, 5)
list.flatMap(x => List(x, x)) // List(1, 1, 2, 2, 3, 3, ...)

// Fold
list.foldLeft(0)(_ + _)      // 15 — сумма
list.foldLeft("")((acc, x) => acc + x) // "12345"
list.foldRight(0)(_ + _)     // 15

// reduce — тот же fold, но без начального значения (бросает на пустом)
list.reduce(_ + _)           // 15
list.reduceOption(_ + _)     // Some(15) — safe вариант

// другие полезные
list.sum                     // 15
list.min                     // 1
list.max                     // 5
list.head, list.tail         // 1, List(2,3,4,5)
list.zipWithIndex            // List((1,0), (2,1), (3,2), (4,3), (5,4))
```

## Q29. Что такое View и ленивые коллекции?

`View` — это ленивая обёртка над коллекцией: операции `map`/`filter`/`take` не выполняются сразу, а накапливаются и применяются на лету только в момент материализации (`toList`, обход).

```scala
val v = (1 to 1_000_000).view
  .map(_ * 2)
  .filter(_ % 3 == 0)
  .take(10)
  .toList // только тут вычисляется!
```

Зачем это нужно: без `view` каждая промежуточная операция создаёт отдельную полную коллекцию — на больших данных это лишние аллокации и проходы. С `view` цепочка проходит данные один раз и обрабатывает ровно столько элементов, сколько нужно (здесь — пока не наберётся 10).

`LazyList` (раньше назывался `Stream`) — это список с ленивым хвостом: элементы вычисляются по требованию, поэтому он может быть и бесконечным:

```scala
val naturals: LazyList[Int] = LazyList.from(1)
naturals.map(_ * 2).take(5).toList // List(2, 4, 6, 8, 10)
```

## Q30. (!) Что такое Future и как устроена асинхронность в Scala?

`Future[T]` — это контейнер для результата вычисления, которое выполняется асинхронно (в другом потоке) и завершится позже либо успехом `Success(t)`, либо ошибкой `Failure(throwable)`. Важная особенность: `Future` запускается сразу при создании (eager) и требует `ExecutionContext` — пул потоков, где он выполнится.

```scala
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

val f: Future[Int] = Future {
  Thread.sleep(1000)
  42
}

f.onComplete {
  case Success(value) => println(s"Got: $value")
  case Failure(err)   => println(s"Error: $err")
}

// Sync (для тестов)
import scala.concurrent.Await
import scala.concurrent.duration._
val result = Await.result(f, 5.seconds)
```

Концептуально похоже на Java `CompletableFuture`, но API богаче и лучше композируется (for-comprehension, `recover`, `sequence`).

## Q31. Как комбинировать несколько Future?

`Future` композируются через те же комбинаторы, что и коллекции: `map`/`flatMap` (или for-comprehension) плюс `Future.sequence` для сбора списка результатов. Ключевой нюанс: `Future` eager — запускается в момент создания (см. Q30). Поэтому параллельность или последовательность определяется не самим for-comprehension, а **местом создания** Future.

```scala
def slowQuery(x: Int): Future[Int] = Future { Thread.sleep(500); x * 2 }

// (а) Параллельно: оба Future созданы val'ами ДО for-comprehension,
// Future eager — оба уже выполняются; for лишь комбинирует результаты (~500 мс)
val a: Future[Int] = slowQuery(1)
val b: Future[Int] = slowQuery(2)

val sumPar = for {
  ai <- a
  bi <- b
} yield ai + bi

// (б) Последовательно: Future создаются ВНУТРИ for (= вложенный flatMap) —
// второй стартует только после завершения первого (~1000 мс)
val sumSeq = for {
  ai <- slowQuery(1)
  bi <- slowQuery(ai) // использует результат ai
} yield ai + bi

// Параллельный сбор списка результатов
val parallel = Future.sequence(List(a, b)).map(_.sum)

// Error recovery
val safe = a.recover { case _ => 0 }

// Timeout
val withTimeout = Future.firstCompletedOf(Seq(
  a,
  Future {
    Thread.sleep(5000)
    throw new TimeoutException
  }
))
```

Правило простое: нужна параллельность — создавай Future заранее val'ами и комбинируй готовые ссылки в for; нужна последовательность (второй шаг зависит от результата первого) — создавай Future внутри for/`flatMap`. Классическая интервью-ловушка — назвать вариант (а) «последовательным»: на самом деле for там лишь ждёт уже запущенные вычисления.

## Q32. Что такое ExecutionContext?

`ExecutionContext` — это абстракция пула потоков, в котором исполняется `Future`. Каждой операции над `Future` нужен `ExecutionContext` (обычно передаётся неявно), потому что именно он решает, на каком потоке запустить вычисление и колбэки.

```scala
import scala.concurrent.ExecutionContext.Implicits.global  // ForkJoinPool

// Custom
import java.util.concurrent.Executors
implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(
  Executors.newFixedThreadPool(8)
)
```

`global` основан на `ForkJoinPool` и подходит для CPU-bound задач. **Подводный камень:** для блокирующего I/O его использовать нельзя — несколько блокирующих операций исчерпают пул, и остальные задачи зависнут. Под блокирующую работу заводят отдельный (dedicated) пул.

## Q33. (!) Что такое Akka и actor model?

`Akka` — фреймворк, реализующий **actor model** для Scala/Java. Actor — это изолированная единица с собственным состоянием и почтовым ящиком (mailbox); единственный способ с ним взаимодействовать — отправить асинхронное сообщение.

```scala
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

// Поведение актора
val greeter = Behaviors.receiveMessage[String] { msg =>
  println(s"Hello, $msg!")
  Behaviors.same
}

val system = ActorSystem(greeter, "example")
system ! "Alice"
system ! "Bob"
```

**Ключевые идеи:**
- Actor — изолированный объект со своим mailbox; его состояние недоступно снаружи.
- Сообщения передаются асинхронно и складываются в mailbox.
- Actor обрабатывает сообщения **по одному за раз**, поэтому внутри него нет гонок (race conditions) — синхронизация не нужна.
- Actor system масштабируется на несколько узлов через cluster.

Именно последовательная обработка сообщений делает модель привлекательной: конкурентность есть, а блокировок и общего изменяемого состояния — нет.

**Сценарии применения:** message brokers, системы реального времени, IoT, биржевой/финансовый трейдинг.

**Подводный камень с лицензией:** начиная с Akka 2.7+ (2022) лицензия стала **платной** для компаний с большой выручкой. Из-за этого многие мигрируют на **Apache Pekko** (open-source форк Akka) или на другие подходы.

## Q34. (!) Что такое ZIO и Cats Effect?

Это **effect systems** — библиотеки для функционального программирования с эффектами (I/O, ошибки, конкурентность). Их центральная идея: тип вроде `IO`/`ZIO` — это **описание** программы, а не её выполнение. Эффект строится как чистое значение, композируется, и только в самом конце («на краю мира») запускается. За счёт этого сохраняется referential transparency и появляется удобное управление ресурсами и конкурентностью.

**Cats Effect:**
```scala
import cats.effect._

val program: IO[Unit] = for {
  _ <- IO.println("Hello")
  _ <- IO.sleep(1.second)
  _ <- IO.println("World")
} yield ()

// IO — описание программы, не выполнение
// Запуск — IORuntime.global.unsafeRunSync(program)
```

**ZIO:**
```scala
import zio._

val program: UIO[Unit] = for {
  _ <- Console.printLine("Hello").orDie
  _ <- ZIO.sleep(1.second)
  _ <- Console.printLine("World").orDie
} yield ()

ZIOAppDefault.run(program)
```

**Что это даёт:**
- Чистые функции и referential transparency — программу можно рассуждать как обычное выражение.
- Типизированные ошибки — возможные сбои видны в типе.
- Композируемая конкурентность через лёгкие fibers (а не тяжёлые потоки).
- Управление ресурсами с гарантией освобождения (`Bracket`, `Resource`).

## Q35. Что означают три параметра в ZIO[R, E, A]?

`ZIO[R, E, A]` кодирует в одном типе сразу три вещи: какие зависимости нужны вычислению, какой ошибкой оно может завершиться и что вернёт при успехе.

```
ZIO[R, E, A]
- R — environment (зависимости)
- E — error type
- A — success type
```

```scala
val getUser: ZIO[UserRepo, DbError, User] = ???

// UIO[A] = ZIO[Any, Nothing, A] — нет deps, нет ошибок
// Task[A] = ZIO[Any, Throwable, A]
// IO[E, A] = ZIO[Any, E, A]
// URIO[R, A] = ZIO[R, Nothing, A]
// RIO[R, A] = ZIO[R, Throwable, A]
```

Type-алиасы вроде `Task` и `UIO` — это просто `ZIO` с фиксированными параметрами для частых случаев (без зависимостей, без ошибок и т.д.). Преимущество подхода: компилятор знает все возможные ошибки и зависимости вычисления прямо из его типа. По сути это объединение `Reader` (зависимости) + `Either` (ошибки) + `IO` (эффект) в одном типе.

## Q36. (!) Как Scala взаимодействует с Java?

Interop стопроцентный в обе стороны: Scala видит Java-классы напрямую, а Java может использовать Scala-классы. Чаще всего достаточно явной конвертации коллекций и оборачивания возможных `null` в `Option`.

```scala
// Использование Java из Scala
import java.util.{ArrayList, List => JList}

val javaList: JList[String] = new ArrayList()
javaList.add("hello")

// Конвертация коллекций
import scala.jdk.CollectionConverters._
val scalaList: List[String] = javaList.asScala.toList
val backToJava: JList[String] = scalaList.asJava

// Java code can use Scala classes
// public class JavaUser {
//   private scala.collection.immutable.List<String> names;
// }
```

## Q37. Какие подводные камни у интеропа Scala и Java?

1. **`null`** — Java часто возвращает `null`, а Scala-код этого не ожидает. Оборачивайте результаты Java-вызовов в `Option`, чтобы не словить NPE.
2. **Checked exceptions** — Scala их не различает: можно бросать без объявления, но и компилятор не заставит обработать.
3. **Разные коллекции** — `java.util.List` и `scala.collection.List` несовместимы напрямую; конвертируйте явно через `asScala`/`asJava`.
4. **Типы функций** — `scala.Function1` и `java.util.function.Function` — разные типы. С Scala 2.13+ есть автоматическое преобразование.
5. **Type erasure** — дженерики стираются в рантайме, как и в Java.
6. **Scala-only возможности** (implicits, traits с параметрами) могут быть недоступны или неудобны при вызове из Java.

## Q38. (!) Где Scala применяется в production?

Главная ниша Scala — data engineering (Spark и вокруг него), плюс backend в нескольких крупных компаниях и финансах:

1. **Apache Spark** — главное применение Scala.
2. **Apache Kafka** — Streams API написан на Scala.
3. **Apache Flink** — отдельные компоненты.
4. **Twitter** — backend (исторически).
5. **LinkedIn** — Norbert framework, Kafka, обработка данных.
6. **Netflix** — отдельные сервисы (Atlas, Spinnaker).
7. **Coursera** — backend.
8. **The Guardian** — управление контентом.
9. **Foursquare** — backend.
10. **Banks (HSBC, Morgan Stanley)** — моделирование рисков, трейдинг.

К **2024** общая популярность Scala снижается относительно Kotlin/Rust, но в data engineering она остаётся доминирующей.

## Q39. (!) Как связаны Apache Spark и Scala?

`Apache Spark` сам написан на Scala, поэтому Scala-API для него самый родной и наиболее полный по возможностям (feature-complete). Python/Java/R поддерживаются, но новые фичи и type-safe Datasets раньше и удобнее всего доступны именно в Scala.

```scala
import org.apache.spark.sql.SparkSession

val spark = SparkSession.builder()
  .appName("MyApp")
  .master("local[*]")
  .getOrCreate()

import spark.implicits._

case class User(name: String, age: Int)

val users = Seq(User("Alice", 30), User("Bob", 25)).toDS()

val adults = users.filter(_.age >= 18)
adults.show()

val grouped = users.groupBy("age").count()
```

**Почему Scala здесь первый класс:**
- Typed Dataset API с case-классами проверяется компилятором — в PySpark типизированных Datasets нет вовсе.
- Нет сериализационного моста: Scala-лямбды исполняются прямо в JVM executor'а, тогда как Python UDF в PySpark гоняют данные между JVM и Python-процессом (даже с Arrow это накладные расходы).
- Новые фичи появляются в Scala API первыми.

**Нюанс про Catalyst** (интервьюер со знанием Spark это проверит): для DataFrame API оптимизатор строит одинаковые логические планы из любого языка — план языконезависим, и здесь Scala преимущества не даёт. Более того, typed-лямбды в `map`/`filter` для Catalyst непрозрачны: он не может заглянуть внутрь произвольной функции и теряет оптимизации вроде predicate pushdown. Так что преимущество Scala — типобезопасность и отсутствие моста, а не «лучшая работа Catalyst».

Поэтому, хотя Spark поддерживает Python, Java и R, **Scala остаётся для него языком первого класса**.

## Q40. (!) Какие у Scala минусы?

Сила Scala — её выразительность — оборачивается сложностью: язык трудно осваивать, медленно компилируется, а экосистема расколота на конкурирующие подходы.

1. **Высокий порог входа** — мощная система типов, implicits, монады: много концепций сразу.
2. **Медленная компиляция** — особенно при активном использовании implicits и макросов.
3. **Фрагментация экосистемы** — Cats против ZIO, Akka против Pekko: разные несовместимые стили.
4. **Сложный onboarding** новых разработчиков.
5. **Поддержка в IDE** — компиляция часто долгая, IntelliJ Scala plugin не всегда работает идеально.
6. **Обратная совместимость** — Scala 3 совместима с 2.13, но с оговорками.
7. **Платная лицензия Akka** — вынуждает мигрировать на Pekko или альтернативы.
8. **Инструментарий** — sbt считается сложным; есть альтернативы (Mill, Bleep).
9. **Найм** — Scala-разработчиков на рынке меньше, чем Java/Kotlin.
10. **Производительность** — обычно быстрая, но implicits и pattern matching могут давать скрытые издержки.

В 2024 ряд компаний (Twitter, LinkedIn) **мигрируют backend со Scala на Kotlin/Rust/Go**. Scala остаётся сильна в data engineering и FP-нишах.

## See also

- [Java Core](../java/java-core-interview.md) — основа JVM, интероп
- [Kotlin](../kotlin/kotlin-interview.md) — современная альтернатива
- [Java Collections](../java/java-collections-interview.md) — конвертации
- [Java Stream API](../java/java-stream-interview.md) — vs Scala collections
- [Java Concurrency](../java/java-concurrency-interview.md) — Future базис
- [Рекурсия](../../algorithms/algorithmic-paradigms/recursion-interview.md) — @tailrec
- [Паттерны проектирования](../../design-patterns/design-patterns-interview.md) — type classes, monads
- [Apache Spark](../../data-engineering/apache-spark-interview.md) — главное применение Scala
- [Ktor](../../frameworks/jvm-alternatives/ktor-interview.md) — Kotlin аналог http4s
- [Микросервисы](../../architecture/microservices-interview.md) — Akka, ZIO HTTP
- [Event-driven паттерны](../../architecture/event-driven-patterns-interview.md) — Akka actors
- [JVM](../../jvm/jvm-interview.md) — где runs Scala

- [Go Generics](../go/go-generics-interview.md)
- [Go](../go/go-interview.md)
- [Go Memory и GC](../go/go-memory-gc-interview.md)
- [Go Modules](../go/go-modules-interview.md)
- [Go Standard Library](../go/go-stdlib-interview.md)
