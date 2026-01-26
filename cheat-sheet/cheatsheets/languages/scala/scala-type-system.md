---
title: "Scala Type System"
description: "Полное руководство по системе типов Scala: типы, generics, variance, bounds, type inference, path-dependent types"
tags: ["scala", "type-system", "generics", "variance", "type-inference"]
difficulty: "advanced"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-basics.md", "scala/scala-fp-advanced.md"]
---

# Scala Type System

Кратко: полное руководство по системе типов Scala: типы, generics, variance, bounds, type inference, path-dependent types.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [Scala Type System](https://docs.scala-lang.org/tour/unified-types.html)

### См. также
- `./scala-basics.md` - основы Scala
- `./scala-fp-advanced.md` - продвинутое ФП

## Содержание

- [Введение в систему типов](#введение-в-систему-типов)
- [Типы данных](#типы-данных)
- [Generics](#generics)
- [Variance](#variance)
- [Type Bounds](#type-bounds)
- [Type Inference](#type-inference)
- [Path-Dependent Types](#path-dependent-types)
- [Лучшие практики](#лучшие-практики)

## Введение в систему типов

Scala имеет мощную систему типов, которая обеспечивает безопасность и выразительность кода. Система типов Scala включает generics, variance, type bounds и другие продвинутые возможности.

### Основные характеристики

- **Статическая типизация**: проверка типов на этапе компиляции
- **Type Inference**: автоматический вывод типов
- **Generics**: параметрический полиморфизм
- **Variance**: ковариантность, контравариантность, инвариантность

## Типы данных

### Примитивные типы

```scala
val byte: Byte = 127
val short: Short = 32767
val int: Int = 2147483647
val long: Long = 9223372036854775807L
val float: Float = 3.14f
val double: Double = 3.14159
val char: Char = 'A'
val boolean: Boolean = true
```

### Ссылочные типы

```scala
val string: String = "Hello"
val list: List[Int] = List(1, 2, 3)
val option: Option[String] = Some("value")
```

## Generics

Generics позволяют создавать параметризованные типы:

```scala
// Простой generic класс
class Box[T](val value: T) {
  def get: T = value
  def set(newValue: T): Box[T] = new Box(newValue)
}

// Использование
val intBox = new Box[Int](42)
val stringBox = new Box[String]("Hello")
```

Generics обеспечивают типобезопасность и переиспользование кода.

## Variance

Variance определяет, как отношения между типами распространяются на generic типы:

### Covariance (ковариантность)

```scala
// List ковариантен по типу элемента
val list: List[Animal] = List(new Dog(), new Cat())

// Covariant определение
class Box[+T](val value: T)
```

Covariance позволяет использовать более конкретные типы там, где ожидается более общий.

### Contravariance (контравариантность)

```scala
// Function контравариантен по типу параметра
trait Writer[-T] {
  def write(value: T): Unit
}
```

Contravariance позволяет использовать более общие типы там, где ожидается более конкретный.

### Invariance (инвариантность)

```scala
// По умолчанию generics инвариантны
class Container[T](var value: T)
```

Invariance означает, что типы должны точно совпадать.

## Type Bounds

Type bounds ограничивают возможные типы параметров:

### Upper Bounds

```scala
// T должен быть подтипом Comparable
class SortedList[T <: Comparable[T]] {
  def add(element: T): Unit = ???
}
```

Upper bounds ограничивают тип сверху.

### Lower Bounds

```scala
// T должен быть супертипом String
def addToList[T >: String](list: List[T], element: T): List[T] = {
  element :: list
}
```

Lower bounds ограничивают тип снизу.

## Type Inference

Scala автоматически выводит типы во многих случаях:

```scala
// Явное указание типа
val x: Int = 42

// Автоматический вывод типа
val y = 42  // Int

// Вывод типа для функций
def add(x: Int, y: Int) = x + y  // возвращает Int
```

Type inference делает код более лаконичным, сохраняя безопасность типов.

## Path-Dependent Types

Path-dependent types связывают типы с конкретными экземплярами:

```scala
class Outer {
  class Inner
  
  def createInner: Inner = new Inner
}

val outer1 = new Outer
val outer2 = new Outer

val inner1: outer1.Inner = outer1.createInner
val inner2: outer2.Inner = outer2.createInner

// inner1 и inner2 имеют разные типы
```

Path-dependent types обеспечивают дополнительную типобезопасность.

## Лучшие практики

### Использование явных типов для публичных API

```scala
// Хорошо - явный тип для публичного метода
def processData(data: List[String]): List[Int] = {
  data.map(_.length)
}

// Плохо - неявный тип может быть неочевиден
def processData(data: List[String]) = {
  data.map(_.length)
}
```

### Structural Types

Structural types позволяют определять типы по структуре, а не по имени:

```scala
// Structural type - тип определяется структурой
def process(hasName: { def name: String }): String = {
  hasName.name
}

case class Person(name: String)
case class Company(name: String)

val person = Person("Alice")
val company = Company("Acme")

process(person)  // "Alice"
process(company)  // "Acme"
```

Structural types полезны для работы с объектами, имеющими общую структуру, но не общий супертип.

### Abstract Type Members

Abstract type members позволяют определять типы внутри traits:

```scala
trait Container {
  type A
  def value: A
  def set(newValue: A): Container
}

class IntContainer(val value: Int) extends Container {
  type A = Int
  def set(newValue: Int): Container = new IntContainer(newValue)
}
```

Abstract type members обеспечивают гибкость в определении типов.

### Type Projections

Type projections позволяют ссылаться на вложенные типы:

```scala
class Outer {
  class Inner
}

val outer1 = new Outer
val outer2 = new Outer

// Type projection - ссылка на Inner любого экземпляра Outer
def processInner(inner: Outer#Inner): Unit = {
  // обработка
}

val inner1: outer1.Inner = new outer1.Inner
val inner2: outer2.Inner = new outer2.Inner

processInner(inner1)  // OK
processInner(inner2)  // OK
```

Type projections позволяют работать с вложенными типами независимо от конкретного экземпляра.

### Higher-Kinded Types

Higher-Kinded Types (HKT) - это типы, которые принимают другие типы как параметры:

```scala
// F[_] - higher-kinded type
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

// Реализации для различных типов
implicit val listFunctor: Functor[List] = new Functor[List] {
  def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
}

implicit val optionFunctor: Functor[Option] = new Functor[Option] {
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
}
```

HKT позволяют создавать абстракции над типами, которые сами параметризованы типами.

### Type Lambdas

Type lambdas позволяют создавать анонимные типы:

```scala
// Type lambda для преобразования типа
type OptionList[A] = ({ type λ[α] = Option[List[α]] })#λ[A]

// Использование
val optionList: OptionList[Int] = Some(List(1, 2, 3))
```

Type lambdas полезны для работы с higher-kinded types.

### Self Types

Self types позволяют указывать зависимости между traits:

```scala
trait User {
  def name: String
}

trait Logger {
  self: User =>  // self type - Logger требует User
  def log(message: String): Unit = {
    println(s"[$name] $message")
  }
}

class UserLogger(val name: String) extends User with Logger
```

Self types обеспечивают зависимости между traits на уровне типов.

### Phantom Types

Phantom types - это типы, которые используются только на этапе компиляции:

```scala
sealed trait State
sealed trait Open extends State
sealed trait Closed extends State

class Door[S <: State] private {
  def open(implicit ev: S =:= Closed): Door[Open] = new Door[Open]
  def close(implicit ev: S =:= Open): Door[Closed] = new Door[Closed]
}

object Door {
  def apply(): Door[Closed] = new Door[Closed]
}

// Использование
val door = Door()
val opened = door.open()  // Door[Open]
val closed = opened.close()  // Door[Closed]
// door.close()  // Ошибка компиляции - дверь уже закрыта
```

Phantom types обеспечивают дополнительную безопасность на этапе компиляции.

### Type Erasure и Manifest

Scala использует type erasure, но предоставляет способы сохранения информации о типах:

```scala
import scala.reflect.ClassTag

// ClassTag сохраняет информацию о типе
def createArray[T: ClassTag](size: Int): Array[T] = {
  Array.ofDim[T](size)
}

val intArray = createArray[Int](10)  // Array[Int]
val stringArray = createArray[String](5)  // Array[String]
```

ClassTag позволяет работать с типами во время выполнения.

### Практический пример: Type-Safe Builder

```scala
sealed trait State
sealed trait Empty extends State
sealed trait WithSelect extends State
sealed trait WithFrom extends State
sealed trait WithWhere extends State

class QueryBuilder[S <: State] private(val query: String) {
  def select(columns: String)(implicit ev: S =:= Empty): QueryBuilder[WithSelect] = {
    new QueryBuilder[WithSelect](s"SELECT $columns")
  }
  
  def from(table: String)(implicit ev: S =:= WithSelect): QueryBuilder[WithFrom] = {
    new QueryBuilder[WithFrom](s"$query FROM $table")
  }
  
  def where(condition: String)(implicit ev: S =:= WithFrom): QueryBuilder[WithWhere] = {
    new QueryBuilder[WithWhere](s"$query WHERE $condition")
  }
  
  def build()(implicit ev: S =:= WithWhere): String = query
}

object QueryBuilder {
  def apply(): QueryBuilder[Empty] = new QueryBuilder[Empty]("")
}

// Использование
val query = QueryBuilder()
  .select("name, age")
  .from("users")
  .where("age > 18")
  .build()
```

Type-safe builder гарантирует правильный порядок вызовов методов.

### Практический пример: Tagged Types

```scala
import shapeless.tag
import shapeless.tag.@@

trait UserId
trait Email

type TaggedUserId = Long @@ UserId
type TaggedEmail = String @@ Email

def createUser(id: TaggedUserId, email: TaggedEmail): User = {
  User(id, email)
}

val userId: TaggedUserId = tag[UserId][Long](123L)
val email: TaggedEmail = tag[Email][String]("alice@example.com")

val user = createUser(userId, email)
// createUser(email, userId)  // Ошибка компиляции - неправильный порядок
```

Tagged types предотвращают смешивание типов с одинаковым базовым типом.

## Лучшие практики

### Использование явных типов для публичных API

```scala
// Хорошо - явный тип для публичного метода
def processData(data: List[String]): List[Int] = {
  data.map(_.length)
}

// Плохо - неявный тип может быть неочевиден
def processData(data: List[String]) = {
  data.map(_.length)
}
```

### Использование variance правильно

```scala
// Хорошо - ковариантность для immutable коллекций
class Box[+T](val value: T)

// Плохо - ковариантность для mutable коллекций
class MutableBox[+T](var value: T)  // Ошибка компиляции
```

### Использование type bounds для ограничений

```scala
// Хорошо - type bounds для ограничения типов
def process[T <: Comparable[T]](value: T): T = {
  // обработка
}

// Плохо - отсутствие ограничений может привести к ошибкам
def processBad[T](value: T): T = {
  // может не работать для всех типов
}
```

## Дополнительные темы

### Type-level вычисления

Scala позволяет выполнять вычисления на уровне типов.

```scala
// Представление чисел на уровне типов
sealed trait Nat
case object Zero extends Nat
case class Succ[N <: Nat](n: N) extends Nat

// Сложение на уровне типов
type Add[A <: Nat, B <: Nat] <: Nat = A match {
  case Zero => B
  case Succ[n] => Succ[Add[n, B]]
}

// Использование
type Two = Succ[Succ[Zero]]
type Three = Succ[Succ[Succ[Zero]]]
type Five = Add[Two, Three]
```

### Singleton Types

Singleton Types позволяют работать с конкретными значениями на уровне типов.

```scala
// Singleton type
val x: "hello" = "hello"

// Использование в функциях
def process(value: "hello"): String = {
  s"Processing $value"
}

process("hello")  // OK
// process("world")  // Ошибка компиляции
```

### Match Types

Match Types позволяют выполнять pattern matching на уровне типов.

```scala
// Match type
type Elem[X] = X match {
  case String => Char
  case Array[t] => t
  case Iterable[t] => t
}

// Использование
val char: Elem[String] = 'a'
val int: Elem[Array[Int]] = 42
```

## Заключение

## Дополнительные продвинутые возможности системы типов

### Type Erasure и Manifest

Type Erasure и Manifest позволяют работать с типами во время выполнения.

```scala
import scala.reflect.Manifest

// Использование Manifest для сохранения информации о типах
def processArray[T](arr: Array[T])(implicit m: Manifest[T]): String = {
  m.runtimeClass.getSimpleName
}

// Использование
val intArray = Array(1, 2, 3)
processArray(intArray)  // "Integer"
```

### Type Tags (расширенные)

Type Tags предоставляют расширенные возможности для работы с типами.

```scala
import scala.reflect.runtime.universe._

// Получение TypeTag
def getType[T: TypeTag](obj: T): Type = typeOf[T]

// Проверка типа
val tpe = typeOf[List[Int]]
tpe.typeArgs  // List(Int)

// Работа с классами
val mirror = runtimeMirror(getClass.getClassLoader)
val classSymbol = mirror.classSymbol(classOf[User])
```

### Structural Types (расширенные)

Structural Types позволяют определять типы по их структуре.

```scala
// Определение structural type
type HasName = { def name: String }

// Использование
def printName(obj: HasName): Unit = {
  println(obj.name)
}

// Работает с любым объектом, имеющим метод name
case class Person(name: String)
case class Animal(name: String)

printName(Person("Alice"))  // OK
printName(Animal("Dog"))    // OK
```

### Type Projections (расширенные)

Type Projections позволяют работать с типами из внешних классов.

```scala
// Определение внешнего класса
class Outer {
  class Inner
  type InnerType = Inner
}

// Использование type projection
def processInner(inner: Outer#Inner): Unit = {
  // обработка
}

// Создание экземпляров
val outer1 = new Outer
val inner1 = new outer1.Inner
processInner(inner1)  // OK
```

### Практические примеры: Type-level вычисления

```scala
import shapeless.{Nat, Succ, _0, _1, _2, _3}

// Type-level числа
type Two = Succ[Succ[_0]]
type Three = Succ[Succ[Succ[_0]]]

// Type-level операции
import shapeless.ops.nat.{Sum, Prod}

type TwoPlusThree = Sum[Two, Three]  // _5
type TwoTimesThree = Prod[Two, Three]  // _6
```

### Практические примеры: Phantom Types

```scala
// Phantom Types для типобезопасности
sealed trait State
sealed trait Open extends State
sealed trait Closed extends State

class Door[State](private var isOpen: Boolean) {
  def open(implicit ev: State =:= Closed): Door[Open] = {
    isOpen = true
    this.asInstanceOf[Door[Open]]
  }
  
  def close(implicit ev: State =:= Open): Door[Closed] = {
    isOpen = false
    this.asInstanceOf[Door[Closed]]
  }
}

// Использование
val door = new Door[Closed](false)
val openDoor = door.open  // Door[Open]
val closedDoor = openDoor.close  // Door[Closed]
// door.open.open  // Ошибка компиляции - дверь уже открыта
```

Система типов Scala предоставляет мощные инструменты для создания безопасного и выразительного кода. Понимание generics, variance, type bounds, type inference, path-dependent types, structural types, abstract type members, type projections, higher-kinded types, type lambdas, self types, phantom types, type-level вычислений, singleton types, match types, type erasure, Manifest, Type Tags, расширенных Structural Types, расширенных Type Projections, type-level вычислений с Shapeless, Phantom Types для типобезопасности и их практических применений (type-safe builders, tagged types, phantom types) позволяет эффективно использовать возможности системы типов. Правильное использование этих возможностей критично для создания типобезопасного и поддерживаемого кода. Система типов Scala особенно полезна для создания библиотек и фреймворков, которые требуют высокой типобезопасности и выразительности, type-level вычислений и использования Phantom Types для предотвращения ошибок на этапе компиляции.

### Практические примеры: Работа с Higher-Kinded Types

```scala
// Higher-Kinded Type для работы с различными контейнерами
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

implicit val listFunctor: Functor[List] = new Functor[List] {
  def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
}

implicit val optionFunctor: Functor[Option] = new Functor[Option] {
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
}

// Использование
def double[F[_]: Functor](fa: F[Int]): F[Int] = {
  implicitly[Functor[F]].map(fa)(_ * 2)
}

val listResult = double(List(1, 2, 3))  // List(2, 4, 6)
val optionResult = double(Some(5))  // Some(10)
```

### Практические примеры: Работа с Type Lambdas

```scala
// Type Lambda для работы с вложенными типами
type EitherString[A] = Either[String, A]

// Использование с Higher-Kinded Types
def processEither[A](either: EitherString[A]): Option[A] = {
  either.toOption
}
```

### Использование с различными техниками для типобезопасности

```scala
// Tagged types для типобезопасности
import shapeless.tag
import shapeless.tag.@@

trait UserId
trait ProductId

type TaggedUserId = Long @@ UserId
type TaggedProductId = Long @@ ProductId

def findUser(id: TaggedUserId): Option[User] = {
  // Реализация
  None
}

def findProduct(id: TaggedProductId): Option[Product] = {
  // Реализация
  None
}

// Использование
val userId = tag[UserId][Long](123L)
val productId = tag[ProductId][Long](456L)

findUser(userId)  // OK
findProduct(productId)  // OK
// findUser(productId)  // Ошибка компиляции
```

### Использование с различными техниками для type-level вычислений

```scala
import shapeless.{Nat, Succ, _0, _1, _2, _3}

// Type-level числа
type Two = Succ[Succ[_0]]
type Three = Succ[Succ[Succ[_0]]]

// Type-level операции
import shapeless.ops.nat.{Sum, Prod, LT}

type TwoPlusThree = Sum[Two, Three]  // _5
type TwoTimesThree = Prod[Two, Three]  // _6
type IsLess = LT[Two, Three]  // True
```

## Дополнительные ресурсы

Для дальнейшего изучения системы типов Scala рекомендуется:

- [Scala Type System Documentation](https://docs.scala-lang.org/tour/unified-types.html)
- [Scala Type System Advanced](https://docs.scala-lang.org/overviews/core/implicit-classes.html)
- [Type-Level Programming in Scala](https://blog.softwaremill.com/type-level-programming-in-scala-2-13-5e4bca5b8c0)

