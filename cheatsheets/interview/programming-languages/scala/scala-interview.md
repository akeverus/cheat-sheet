---
title: "Вопросы на собеседовании: Scala"
description: "Scala 2.x и 3.x: типы (immutable/mutable, case class, sealed, traits), pattern matching, implicits/given, futures, монады (Option, Either, Try), Cats, ZIO, Spark, Akka, интероп с Java"
tags:
  - interview
  - programming-languages
  - scala-interview
aliases:
  - "Scala interview"
  - "Scala собеседование"
  - "Scala 3 interview"
  - "Functional programming Scala"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Scala`

`Scala` — гибридный (объектно-функциональный) язык на JVM. Создан Мартином Одерским (2003), работает с Java-экосистемой. Ключевые применения: **data engineering** (Spark, Flink, Kafka Streams), **бэкенд** (Akka, http4s, ZIO), **DSL и сложные доменные модели**. Сложнее Java и Kotlin, но даёт мощную систему типов.

Дата последнего обновления: 2026-04-18

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
- [Q5. (!) val vs var vs def?](#q5--val-vs-var-vs-def)
- [Q6. (!) Type inference в Scala?](#q6--type-inference-в-scala)
- [Q7. Что такое Unit, Nothing, Any, AnyVal, AnyRef?](#q7-что-такое-unit-nothing-any-anyval-anyref)
- [Q8. (!) Option, Some, None — зачем?](#q8--option-some-none--зачем)
- [Q9. Either, Try — для error handling?](#q9-either-try--для-error-handling)

**Классы**
- [Q10. (!) case class — что это?](#q10--case-class--что-это)
- [Q11. (!) sealed traits и почему они важны?](#q11--sealed-traits-и-почему-они-важны)
- [Q12. trait vs abstract class?](#q12-trait-vs-abstract-class)
- [Q13. object и companion object?](#q13-object-и-companion-object)

**Pattern matching**
- [Q14. (!) Pattern matching — основы?](#q14--pattern-matching--основы)
- [Q15. (!) Деструктуризация case class через unapply?](#q15--деструктуризация-case-class-через-unapply)
- [Q16. Pattern matching на коллекциях?](#q16-pattern-matching-на-коллекциях)

**Функциональное программирование**
- [Q17. (!) Лямбды и higher-order функции?](#q17--лямбды-и-higher-order-функции)
- [Q18. (!) Чем Scala отличается в функциональной парадигме от Java?](#q18--чем-scala-отличается-в-функциональной-парадигме-от-java)
- [Q19. for-comprehension — что это?](#q19-for-comprehension--что-это)
- [Q20. (!) Currying и partial application?](#q20--currying-и-partial-application)
- [Q21. tail recursion и @tailrec?](#q21-tail-recursion-и-tailrec)

**Implicits / Given (Scala 3)**
- [Q22. (!) Что такое implicit (Scala 2)?](#q22--что-такое-implicit-scala-2)
- [Q23. (!) Что такое given/using (Scala 3)?](#q23--что-такое-givenusing-scala-3)
- [Q24. Implicit conversion — польза и опасность?](#q24-implicit-conversion--польза-и-опасность)
- [Q25. Type classes pattern?](#q25-type-classes-pattern)

**Коллекции**
- [Q26. (!) Чем Scala-коллекции отличаются от Java?](#q26--чем-scala-коллекции-отличаются-от-java)
- [Q27. immutable vs mutable коллекции?](#q27-immutable-vs-mutable-коллекции)
- [Q28. (!) map, flatMap, filter, fold?](#q28--map-flatmap-filter-fold)
- [Q29. View и Lazy collections?](#q29-view-и-lazy-collections)

**Concurrency**
- [Q30. (!) Future — асинхронность в Scala?](#q30--future--асинхронность-в-scala)
- [Q31. Composing Futures?](#q31-composing-futures)
- [Q32. ExecutionContext — что это?](#q32-executioncontext--что-это)
- [Q33. (!) Akka Actors?](#q33--akka-actors)

**Effect systems**
- [Q34. (!) Что такое ZIO/Cats Effect?](#q34--что-такое-ziocats-effect)
- [Q35. ZIO[R, E, A] — три типа?](#q35-zior-e-a--три-типа)

**Java интероп**
- [Q36. (!) Как Scala взаимодействует с Java?](#q36--как-scala-взаимодействует-с-java)
- [Q37. Подводные камни интеропа?](#q37-подводные-камни-интеропа)

**Применения**
- [Q38. (!) Где Scala применяется в production?](#q38--где-scala-применяется-в-production)
- [Q39. (!) Apache Spark и Scala — связь?](#q39--apache-spark-и-scala--связь)
- [Q40. (!) Какие минусы Scala?](#q40--какие-минусы-scala)

## Q1. (!) Что такое Scala и зачем она нужна?

`Scala` (Scalable Language) — статически типизированный, **гибридный объектно-функциональный** язык на JVM. Создан **Мартином Одерским** (создатель `javac`), первая версия — 2003.

**Для чего:**
- Big Data — Apache Spark, Flink, Kafka Streams написаны на Scala
- Высоконагруженные backend — Akka, http4s, Play, ZIO
- DSL — благодаря мощной системе типов
- Сложные доменные модели (functional + OOP)

**Сильные стороны:**
- Мощная система типов (generics, variance, type classes, dependent types в Scala 3)
- Pattern matching как first-class
- Immutability по умолчанию
- 100% interop с Java

## Q2. (!) Чем Scala отличается от Java?

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

Scala лаконичнее и менее ceremonial.

## Q3. (!) Чем Scala отличается от Kotlin?

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

Scala 3 (2021, Dotty) — крупный пересмотр языка.

**Новые синтаксические возможности:**
- **Indentation-based syntax** (Python-стиль, опционально):
```scala
def factorial(n: Int): Int =
  if n == 0 then 1
  else n * factorial(n - 1)
```
- **`given/using` вместо `implicit`** — более понятный синтаксис
- **`enum`** — настоящие enum (не sealed traits hack)
- **`extension methods`** — без implicit hacks
- **`opaque types`** — type-safe без runtime overhead
- **`union types`** (`String | Int`)
- **`intersection types`** (`Animal & HasFur`)
- **Top-level definitions** — без объявления в `object`

**Внутренние изменения:**
- Новый компилятор (Dotty)
- Совместимость с Scala 2.13+ (на уровне TASTy)
- Улучшенное type inference
- Match types

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

## Q5. (!) val vs var vs def?

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

**Idiom:** используй `val` всегда, кроме когда явно нужна mutability.

## Q6. (!) Type inference в Scala?

Scala выводит типы локальных переменных и иногда параметров:

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

Scala 3 имеет **улучшенный** inference, особенно для дженериков.

## Q7. Что такое Unit, Nothing, Any, AnyVal, AnyRef?

```
                    Any
                   /   \
              AnyVal   AnyRef
              /  |       \
          Int Boolean   ScalaObject
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

## Q8. (!) Option, Some, None — зачем?

`Option[T]` — type-safe замена `null`. Может быть `Some(value)` или `None`.

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
- Компилятор требует обработать `None` case
- Composable через `map`, `flatMap`
- Нет NullPointerException

В Java аналог — `Optional<T>`, в Kotlin — `String?` (nullable types). Scala — больше функциональный API.

## Q9. Either, Try — для error handling?

`Either[L, R]` — два возможных значения: `Left(L)` (обычно ошибка) или `Right(R)` (успех).

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

`Try[T]` — `Success(value)` или `Failure(throwable)`. Для перехвата исключений из Java API:

```scala
import scala.util.Try

val result: Try[Int] = Try(Integer.parseInt("not a number"))
result.getOrElse(0)
result.recover { case _: NumberFormatException => 0 }
```

В Scala FP-стиле часто используют `Either` — типизированные ошибки лучше любых exceptions.

## Q10. (!) case class — что это?

`case class` — класс с автоматически сгенерированными:
- Конструктором с public fields
- `equals` / `hashCode` / `toString`
- `apply` (factory method)
- `unapply` (для pattern matching)
- `copy` (для создания изменённой копии)

```scala
case class User(name: String, age: Int)

val alice = User("Alice", 30)
val older = alice.copy(age = 31)

alice == User("Alice", 30) // true (value equality, не reference!)

alice match {
  case User(name, age) => println(s"$name is $age")
}
```

Аналог `record` в Java 14+, но мощнее (поддерживает `copy`, наследование traits).

## Q11. (!) sealed traits и почему они важны?

`sealed` — модификатор, ограничивающий наследование **одним файлом**. Используется с pattern matching для **исчерпывающих** проверок:

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

**Это аналог ADT** (Algebraic Data Types) из Haskell/OCaml. Ключ к type-safe моделированию.

В Java 17+ — `sealed classes`. В Kotlin — `sealed class/interface`.

## Q12. trait vs abstract class?

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

Trait — основной механизм абстракции. Abstract class — реже, для совместимости с Java.

## Q13. object и companion object?

`object` — singleton:

```scala
object MathUtils {
  def square(x: Int): Int = x * x
}

MathUtils.square(5) // 25
```

`companion object` — `object` с именем класса. Имеет доступ к `private` членам:

```scala
class User private (val name: String, val age: Int)

object User {
  def apply(name: String, age: Int): User = new User(name, age) // factory
  def fromString(s: String): Option[User] = ???
}

User("Alice", 30) // эквивалентно User.apply("Alice", 30)
```

**Применение:** factory methods, constants, static-подобные методы.

## Q14. (!) Pattern matching — основы?

Аналог `switch`, но мощнее:

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
- Литералами
- Типами (с guards)
- Кейс-классами (деструктуризация)
- Списками, кортежами
- Регулярными выражениями
- Custom unapply

## Q15. (!) Деструктуризация case class через unapply?

case class автоматически генерирует `unapply`:

```scala
case class User(name: String, age: Int)

val u = User("Alice", 30)
u match {
  case User("Alice", age) => s"Alice is $age"
  case User(n, _)         => s"Some user $n"
}
```

Можно сделать **custom unapply**:

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

## Q16. Pattern matching на коллекциях?

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

`::` — оператор cons (head::tail). Работает потому что List реализован как linked list.

## Q17. (!) Лямбды и higher-order функции?

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

Scala "функциональнее", но требует больше дисциплины.

## Q19. for-comprehension — что это?

Синтаксический сахар для `flatMap`/`map`/`filter`:

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

Полезно для **monad chaining** — Option, Either, Future, IO:

```scala
val result: Option[Int] = for {
  user  <- findUser(id)
  email <- user.email
  count <- emailCount(email)
} yield count

// Если любой шаг = None → result = None
```

## Q20. (!) Currying и partial application?

**Currying** — функция с несколькими параметрами как несколько функций по одному:

```scala
def add(a: Int)(b: Int): Int = a + b

val addFive = add(5) _   // частичное применение, тип Int => Int
addFive(3)               // 8

// Эквивалентно
def add(a: Int): Int => Int = b => a + b
```

**Применение:** factories, builders, customization, реализация DSL.

```scala
def withResource[A, B](res: A)(action: A => B)(implicit close: A => Unit): B = {
  try action(res)
  finally close(res)
}
```

## Q21. tail recursion и @tailrec?

JVM **не поддерживает** TCO нативно, но компилятор Scala может оптимизировать **tail-recursive** функции в while-цикл.

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

`@tailrec` — компилятор **проверяет** что метод действительно tail-recursive, иначе ошибка компиляции. Подробнее — в [Рекурсия](../../algorithms/algorithmic-paradigms/recursion-interview.md).

## Q22. (!) Что такое implicit (Scala 2)?

`implicit` — параметры, которые компилятор **подставляет автоматически** из доступного scope.

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

Implicits **очень мощны**, но могут запутывать (откуда взялось это значение?). В Scala 3 заменены на `given/using`.

## Q23. (!) Что такое given/using (Scala 3)?

Scala 3 заменяет `implicit` на более понятные конструкции:

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

Преимущества:
- Различение `given`-значений и обычных
- Явный синтаксис для extension methods
- Меньше магии

## Q24. Implicit conversion — польза и опасность?

**Польза:** автоматическое преобразование между совместимыми типами. Например, `BigDecimal` ← `Int`.

**Опасность:**
- Скрытое поведение — сложно понять, что происходит
- Неоднозначность — несколько implicit'ов в scope = ambiguity
- Performance — каждое преобразование = новый объект

**Best practice:**
- Использовать **только** для type classes
- Никогда не делать неявные преобразования между неродственными типами
- В Scala 3 — `given Conversion[A, B]` явно показывает intent

## Q25. Type classes pattern?

**Type class** — pattern для **ad-hoc polymorphism** через implicits:

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

**Применения:** `Cats` (Functor, Monad, Applicative), `Circe` (encoders/decoders), `Spark` (Encoder).

В Java аналогов нет. В Kotlin — частично через extension functions.

## Q26. (!) Чем Scala-коллекции отличаются от Java?

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

## Q27. immutable vs mutable коллекции?

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

**Idiom:** используй immutable, если нет очевидной потребности в mutable.

**Persistent data structures** — immutable, но с эффективными операциями (HAMT для Map, vector trees для Vector).

## Q28. (!) map, flatMap, filter, fold?

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

## Q29. View и Lazy collections?

`View` — lazy обёртка над коллекцией:

```scala
val v = (1 to 1_000_000).view
  .map(_ * 2)
  .filter(_ % 3 == 0)
  .take(10)
  .toList // только тут вычисляется!
```

Без `view` каждая операция создаст промежуточную коллекцию (медленно для больших данных).

`LazyList` (бывший Stream) — список с lazy-вычислением хвоста:

```scala
val naturals: LazyList[Int] = LazyList.from(1)
naturals.map(_ * 2).take(5).toList // List(2, 4, 6, 8, 10)
```

## Q30. (!) Future — асинхронность в Scala?

`Future[T]` — асинхронное вычисление, может завершиться `Success(t)` или `Failure(throwable)`.

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

Похоже на Java `CompletableFuture`, но с лучшим API.

## Q31. Composing Futures?

```scala
val a: Future[Int] = Future(1)
val b: Future[Int] = Future(2)

// Sequential — b зависит от a
val sum = for {
  ai <- a
  bi <- b
} yield ai + bi

// Parallel
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

## Q32. ExecutionContext — что это?

Future нуждается в **`ExecutionContext`** — пул потоков для исполнения:

```scala
import scala.concurrent.ExecutionContext.Implicits.global  // ForkJoinPool

// Custom
import java.util.concurrent.Executors
implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(
  Executors.newFixedThreadPool(8)
)
```

`global` — основан на `ForkJoinPool`. Подходит для CPU-bound задач. Для blocking I/O — нужен dedicated pool.

## Q33. (!) Akka Actors?

`Akka` — фреймворк actor model для Scala/Java.

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

**Концепции:**
- Actor — изолированный объект с mailbox
- Сообщения передаются асинхронно
- Один actor обрабатывает по одному сообщению (нет race conditions)
- Actor system может быть распределён через cluster

**Применения:** message brokers, real-time systems, IoT, financial trading.

С Akka 2.7+ (2022) — **non-free лицензия** для большой выручки. Многие мигрируют на **Apache Pekko** (форк) или другие подходы.

## Q34. (!) Что такое ZIO/Cats Effect?

**Effect systems** — библиотеки для **типизированного** функционального программирования с эффектами (I/O, ошибки, конкурентность).

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

**Преимущества:**
- Чистые функции (referential transparency)
- Типизированные ошибки
- Composable concurrency (fibers)
- Resource management (Bracket, Resource)

## Q35. ZIO[R, E, A] — три типа?

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

Это даёт мощную систему: компилятор знает все возможные ошибки и зависимости. Аналог `Reader + Either + IO` в одном.

## Q36. (!) Как Scala взаимодействует с Java?

100% interop в обе стороны:

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

## Q37. Подводные камни интеропа?

1. **`null`** — Java часто возвращает null, Scala использует Option. Оборачивай в Option.
2. **Checked exceptions** — Scala их игнорирует, можно бросать без объявления.
3. **Различные коллекции** — `java.util.List` ≠ `scala.collection.List`. Конвертируй явно.
4. **`Function` types** — `scala.Function1` ≠ `java.util.function.Function`. С Scala 2.13+ — auto-conversion.
5. **Type erasure** — generics стираются, как в Java.
6. **Scala-only features** (implicits, traits с параметрами) могут не работать из Java.

## Q38. (!) Где Scala применяется в production?

1. **Apache Spark** — главное применение Scala
2. **Apache Kafka** — streams API на Scala
3. **Apache Flink** — некоторые компоненты
4. **Twitter** — backend (исторически)
5. **LinkedIn** — Norbert framework, Kafka, обработка данных
6. **Netflix** — некоторые сервисы (Atlas, Spinnaker)
7. **Coursera** — backend
8. **The Guardian** — content management
9. **Foursquare** — backend
10. **Banks (HSBC, Morgan Stanley)** — risk modeling, trading

В **2024** Scala теряет популярность относительно Kotlin/Rust, но в data engineering остаётся доминирующей.

## Q39. (!) Apache Spark и Scala — связь?

`Apache Spark` написан на Scala, и Scala API — самый родной и feature-complete:

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

**Причины:**
- Spark API в Scala — type-safe (Datasets с case classes)
- Closures сериализуются эффективно
- Catalyst optimizer работает лучше с Scala объектами

Хотя Spark поддерживает Python, Java, R — **Scala остаётся первым классом**.

## Q40. (!) Какие минусы Scala?

1. **Сложная learning curve** — мощная система типов, implicits, monads — много концепций
2. **Медленная компиляция** — особенно с heavy implicits и macros
3. **Ecosystem-fragmentation** — Cats vs ZIO, Akka vs Pekko, разные подходы
4. **Onboarding новых разработчиков сложен**
5. **IDE support** — компиляция часто долгая, IntelliJ Scala plugin не всегда работает идеально
6. **Backward compatibility** — Scala 3 совместима с 2.13, но с оговорками
7. **Akka non-free лицензия** — миграция на Pekko или альтернативы
8. **Tooling** — sbt считается сложным; Mill, Bleep — альтернативы
9. **Найм разработчиков** — Scala-программистов меньше чем Java/Kotlin
10. **Производительность** — обычно быстрая, но implicits и pattern matching могут быть hidden cost

В 2024 многие компании (Twitter, LinkedIn) **мигрируют со Scala на Kotlin/Rust/Go** для backend. Scala остаётся в data engineering и FP-нишах.

---

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
