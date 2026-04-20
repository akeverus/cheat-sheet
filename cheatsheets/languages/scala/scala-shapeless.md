---
title: "Shapeless в Scala"
description: "Краткое руководство по Shapeless - библиотека для generic программирования и метапрограммирования в Scala."
tags:
  - languages
  - scala
  - scala-shapeless
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Shapeless в Scala

Краткое руководство по **Shapeless** — библиотека для **generic** программирования и метапрограммирования в **Scala**.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [HList (Heterogeneous List)](#hlist-heterogeneous-list)
  - [Базовое использование HList](#базовое-использование-hlist)
  - [Операции над HList](#операции-над-hlist)
- [Generic](#generic)
  - [Преобразование case class в HList](#преобразование-case-class-в-hlist)
  - [Преобразование между типами](#преобразование-между-типами)
- [Type-level вычисления](#type-level-вычисления)
  - [Type-level операции](#type-level-операции)
- [Lens](#lens)
  - [Использование Lens](#использование-lens)
- [Практические примеры](#практические-примеры)
  - [Автоматическая сериализация](#автоматическая-сериализация)
  - [Автоматическое сравнение](#автоматическое-сравнение)
  - [Практические примеры: Автоматическая деривация типов](#практические-примеры-автоматическая-деривация-типов)
  - [Практические примеры: Преобразование между типами с одинаковой структурой](#практические-примеры-преобразование-между-типами-с-одинаковой-структурой)
  - [Практические примеры: Копирование с изменениями](#практические-примеры-копирование-с-изменениями)
  - [Автоматическая деривация JSON сериализаторов](#автоматическая-деривация-json-сериализаторов)
  - [Автоматическая деривация equals и hashCode](#автоматическая-деривация-equals-и-hashcode)
  - [Автоматическая деривация toString](#автоматическая-деривация-tostring)
  - [Работа с Coproduct](#работа-с-coproduct)
  - [Работа с Records](#работа-с-records)
  - [Автоматическая деривация для ADT](#автоматическая-деривация-для-adt)
  - [Автоматическая деривация для конвертации между типами](#автоматическая-деривация-для-конвертации-между-типами)
  - [Автоматическая деривация для работы с опциональными полями](#автоматическая-деривация-для-работы-с-опциональными-полями)
  - [Автоматическая деривация для валидации](#автоматическая-деривация-для-валидации)
  - [Автоматическая деривация для создания Builder](#автоматическая-деривация-для-создания-builder)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Работа с TypeTags для runtime информации о типах](#работа-с-typetags-для-runtime-информации-о-типах)
  - [Работа с Witness для type-level значений](#работа-с-witness-для-type-level-значений)
  - [Работа с Sized для типобезопасных размеров коллекций](#работа-с-sized-для-типобезопасных-размеров-коллекций)
  - [Работа с Poly для полиморфных функций](#работа-с-poly-для-полиморфных-функций)
  - [Работа с Typeable для безопасного приведения типов](#работа-с-typeable-для-безопасного-приведения-типов)
  - [Работа с UnaryTCConstraint для ограничений типов](#работа-с-unarytcconstraint-для-ограничений-типов)
  - [Работа с Nat для type-level чисел](#работа-с-nat-для-type-level-чисел)
  - [Работа с Singleton для singleton типов](#работа-с-singleton-для-singleton-типов)
  - [Работа с LabelledGeneric для работы с именами полей](#работа-с-labelledgeneric-для-работы-с-именами-полей)
  - [Работа с ops.hlist для операций над HList](#работа-с-opshlist-для-операций-над-hlist)
  - [Работа с ops.tuple для операций над tuple](#работа-с-opstuple-для-операций-над-tuple)
  - [Работа с ops.record для операций над Records](#работа-с-opsrecord-для-операций-над-records)
  - [Работа с ops.coproduct для операций над Coproduct](#работа-с-opscoproduct-для-операций-над-coproduct)
  - [Работа с ops.nat для type-level арифметики](#работа-с-opsnat-для-type-level-арифметики)
  - [Работа с ops.typeable для безопасного приведения типов](#работа-с-opstypeable-для-безопасного-приведения-типов)
  - [Работа с ops.hlist.ToTraversable для преобразования HList в коллекцию](#работа-с-opshlisttotraversable-для-преобразования-hlist-в-коллекцию)
  - [Работа с ops.hlist.Mapper для применения функции к каждому элементу](#работа-с-opshlistmapper-для-применения-функции-к-каждому-элементу)
  - [Работа с ops.hlist.FlatMapper для flatMap операций](#работа-с-opshlistflatmapper-для-flatmap-операций)
  - [Работа с ops.hlist.Zip для объединения HList](#работа-с-opshlistzip-для-объединения-hlist)
  - [Работа с ops.hlist.Unzip для разделения HList](#работа-с-opshlistunzip-для-разделения-hlist)
  - [Работа с ops.hlist.Prepend для добавления элемента](#работа-с-opshlistprepend-для-добавления-элемента)
  - [Работа с ops.hlist.Append для добавления элемента в конец](#работа-с-opshlistappend-для-добавления-элемента-в-конец)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Shapeless** — это библиотека для **generic** программирования в **Scala**, которая позволяет работать с типами на уровне компиляции. Она предоставляет инструменты для автоматической деривации типов, преобразования между типами, работы с **HList** и создания **type-level** вычислений.

**Shapeless** особенно полезен для автоматической генерации кода, преобразования между типами, создания **type-safe API** и работы с гетерогенными структурами данных.

## HList (Heterogeneous List)

**HList** — это список с элементами разных типов, известными на этапе компиляции.

### Базовое использование HList

```scala
import shapeless.{HList, ::, HNil}

// HList с элементами разных типов
val hlist: String :: Int :: Boolean :: HNil = "hello" :: 42 :: true :: HNil

// Доступ к элементам по типу
val string = hlist.head  // "hello"
val int = hlist.tail.head  // 42
val boolean = hlist.tail.tail.head  // true
```

### Операции над HList

```scala
import shapeless.{HList, ::, HNil}

val hlist1: String :: Int :: HNil = "hello" :: 42 :: HNil
val hlist2: Boolean :: Double :: HNil = true :: 3.14 :: HNil

// Объединение HList
val combined: String :: Int :: Boolean :: Double :: HNil =
  hlist1 ++ hlist2

// Преобразование HList в tuple
val tuple = hlist1.toTuple  // ("hello", 42)

// Преобразование tuple в HList
import shapeless.syntax.std.tuple._
val tuple1 = ("hello", 42, true)
val hlist3 = tuple1.productElements
```

## Generic

**Generic** позволяет преобразовывать **case classes** в **HList** и обратно.

### Преобразование case class в HList

```scala
import shapeless.Generic

case class User(name: String, age: Int, email: String)

// Создание Generic для User
val genUser = Generic[User]

// Преобразование User в HList
val user = User("Alice", 30, "alice@example.com")
val hlist: String :: Int :: String :: HNil = genUser.to(user)

// Преобразование HList обратно в User
val user2 = genUser.from(hlist)  // User("Alice", 30, "alice@example.com")
```

### Преобразование между типами

```scala
import shapeless.Generic

case class User(name: String, age: Int, email: String)
case class Person(name: String, age: Int, email: String)

// Преобразование User в Person через HList
def convert[A, B, Repr](
  a: A
)(implicit genA: Generic.Aux[A, Repr], genB: Generic.Aux[B, Repr]): B = {
  genB.from(genA.to(a))
}

val user = User("Alice", 30, "alice@example.com")
val person = convert[User, Person](user)
// Person("Alice", 30, "alice@example.com")
```

## Type-level вычисления

**Shapeless** предоставляет инструменты для **type-level** вычислений.

### Type-level операции

```scala
import shapeless.{Nat, Succ, _0, _1, _2}
import shapeless.ops.nat.{Sum, Prod, Diff}

// Type-level сложение
type TwoPlusThree = Sum[_2, _3]  // _5

// Type-level умножение
type TwoTimesThree = Prod[_2, _3]  // _6

// Type-level вычитание
type FiveMinusTwo = Diff[_5, _2]  // _3
```

## Lens

**Lens** — это функциональные ссылки на поля структуры данных.

### Использование Lens

```scala
import shapeless.Lens
import shapeless.lens

case class User(name: String, age: Int, email: String, address: Address)
case class Address(street: String, city: String, zip: String)

val user = User("Alice", 30, "alice@example.com", Address("Main St", "NYC", "10001"))

// Создание Lens для поля name
val nameLens = lens[User].name

// Чтение значения
val name = nameLens.get(user)  // "Alice"

// Обновление значения
val updated = nameLens.set(user)("Bob")  // User("Bob", 30, ...)

// Композиция Lens
val addressLens = lens[User].address
val cityLens = lens[Address].city
val userCityLens = addressLens composeLens cityLens

val city = userCityLens.get(user)  // "NYC"
val updated2 = userCityLens.set(user)("London")
```

## Практические примеры

### Автоматическая сериализация

```scala
import shapeless.Generic
import shapeless.LabelledGeneric

case class User(name: String, age: Int, email: String)

// Автоматическая сериализация в Map
def toMap[A, Repr <: HList](
  a: A
)(implicit gen: LabelledGeneric.Aux[A, Repr], toMap: ToMap[Repr]): Map[String, Any] = {
  toMap(gen.to(a))
}

val user = User("Alice", 30, "alice@example.com")
val map = toMap(user)
// Map("name" -> "Alice", "age" -> 30, "email" -> "alice@example.com")
```

### Автоматическое сравнение

```scala
import shapeless.Generic

case class Point(x: Int, y: Int)

def deepEquals[A, B, Repr](
  a: A, b: B
)(implicit genA: Generic.Aux[A, Repr], genB: Generic.Aux[B, Repr]): Boolean = {
  genA.to(a) == genB.to(b)
}

val point1 = Point(1, 2)
val point2 = Point(1, 2)
val equal = deepEquals(point1, point2)  // true
```

### Практические примеры: Автоматическая деривация типов

```scala
import shapeless.{Generic, HList, HNil, ::}
import shapeless.ops.hlist.ToTraversable
import shapeless.syntax.std.tuple._

// Автоматическое получение имен полей
case class Person(name: String, age: Int, email: String)

def fieldNames[A](implicit gen: LabelledGeneric[A]): List[String] = {
  val hlist = gen.to(???)
  // Извлечение имен полей из HList
  // Реализация требует более сложной работы с символами
  List.empty
}
```

### Практические примеры: Преобразование между типами с одинаковой структурой

```scala
import shapeless.Generic

case class User(name: String, age: Int, email: String)
case class Person(name: String, age: Int, email: String)

def convert[A, B](a: A)(implicit
  genA: Generic[A],
  genB: Generic[B]
): B = {
  genB.from(genA.to(a))
}

val user = User("Alice", 30, "alice@example.com")
val person = convert[User, Person](user)
// Person("Alice", 30, "alice@example.com")
```

### Практические примеры: Копирование с изменениями

```scala
import shapeless.Lens
import shapeless.lens

case class User(name: String, age: Int, email: String, settings: Settings)
case class Settings(theme: String, notifications: Boolean)

val user = User("Alice", 30, "alice@example.com", Settings("dark", true))

// Создание копии с изменением нескольких полей
val nameLens = lens[User].name
val ageLens = lens[User].age
val settingsLens = lens[User].settings
val themeLens = lens[Settings].theme

val updatedUser = user
  |> nameLens.set("Bob")
  |> ageLens.set(31)
  |> (settingsLens composeLens themeLens).set("light")
// User("Bob", 31, "alice@example.com", Settings("light", true))
```

### Автоматическая деривация JSON сериализаторов

```scala
import shapeless.{Generic, LabelledGeneric, HList}
import shapeless.labelled.{FieldType, field}
import shapeless.syntax.singleton._

// Автоматическая деривация JSON сериализатора
trait JsonEncoder[A] {
  def encode(a: A): String
}

object JsonEncoder {
  implicit val stringEncoder: JsonEncoder[String] = (s: String) => s""""$s""""
  implicit val intEncoder: JsonEncoder[Int] = (i: Int) => i.toString
  implicit val booleanEncoder: JsonEncoder[Boolean] = (b: Boolean) => b.toString

  implicit val hnilEncoder: JsonEncoder[HNil] = (_: HNil) => "{}"

  implicit def hlistEncoder[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hEncoder: Lazy[JsonEncoder[H]],
    tEncoder: JsonEncoder[T]
  ): JsonEncoder[FieldType[K, H] :: T] = { hlist =>
    val fieldName = witness.value.name
    val headJson = hEncoder.value.encode(hlist.head)
    val tailJson = tEncoder.encode(hlist.tail)
    if (tailJson == "{}") {
      s""""$fieldName": $headJson"""
    } else {
      s""""$fieldName": $headJson, $tailJson"""
    }
  }

  implicit def genericEncoder[A, Repr <: HList](
    implicit
    gen: LabelledGeneric.Aux[A, Repr],
    encoder: Lazy[JsonEncoder[Repr]]
  ): JsonEncoder[A] = (a: A) => {
    s"{${encoder.value.encode(gen.to(a))}}"
  }
}

case class User(name: String, age: Int, email: String)
val user = User("Alice", 30, "alice@example.com")
val json = JsonEncoder[User].encode(user)
// {"name": "Alice", "age": 30, "email": "alice@example.com"}
```

### Автоматическая деривация equals и hashCode

```scala
import shapeless.Generic

// Автоматическая деривация equals и hashCode
def autoEquals[A, Repr](a1: A, a2: A)(
  implicit gen: Generic.Aux[A, Repr]
): Boolean = {
  gen.to(a1) == gen.to(a2)
}

def autoHashCode[A, Repr](a: A)(
  implicit gen: Generic.Aux[A, Repr]
): Int = {
  gen.to(a).hashCode()
}

case class Person(name: String, age: Int)
val person1 = Person("Alice", 30)
val person2 = Person("Alice", 30)
val equal = autoEquals(person1, person2)  // true
val hashCode1 = autoHashCode(person1)
val hashCode2 = autoHashCode(person2)
```

### Автоматическая деривация toString

```scala
import shapeless.Generic

// Автоматическая деривация toString
def autoToString[A, Repr](a: A)(
  implicit gen: Generic.Aux[A, Repr]
): String = {
  s"${a.getClass.getSimpleName}${gen.to(a).toString}"
}

case class Product(name: String, price: Double, quantity: Int)
val product = Product("Laptop", 999.99, 5)
val string = autoToString(product)
// "Product(Product :: Laptop :: 999.99 :: 5 :: HNil)"
```

### Работа с Coproduct

```scala
import shapeless.{:+:, CNil, Coproduct, Inl, Inr}

// Coproduct для представления альтернативных типов
type IntOrString = Int :+: String :+: CNil

val intValue: IntOrString = Coproduct[IntOrString](42)
val stringValue: IntOrString = Coproduct[IntOrString]("hello")

// Обработка Coproduct
def processIntOrString(value: IntOrString): String = {
  value.eliminate(
    (i: Int) => s"Integer: $i",
    (s: String) => s"String: $s",
    (_: CNil) => "Impossible"
  )
}

val result1 = processIntOrString(intValue)  // "Integer: 42"
val result2 = processIntOrString(stringValue)  // "String: hello"
```

### Работа с Records

```scala
import shapeless.record._
import shapeless.syntax.singleton._

// Records для типобезопасной работы с Map
type UserRecord = Record.`'name -> String, 'age -> Int, 'email -> String`.T

val user: UserRecord =
  ("name" ->> "Alice") ::
  ("age" ->> 30) ::
  ("email" ->> "alice@example.com") ::
  HNil

// Доступ к полям по символу
val name = user("name")  // "Alice"
val age = user("age")  // 30

// Обновление полей
val updatedUser = user.updateWith("age")(_ + 1)  // age становится 31
```

### Автоматическая деривация для ADT

```scala
import shapeless.Generic

// Автоматическая деривация для ADT
sealed trait Shape
case class Circle(radius: Double) extends Shape
case class Rectangle(width: Double, height: Double) extends Shape
case class Triangle(a: Double, b: Double, c: Double) extends Shape

def calculateArea[A <: Shape, Repr](shape: A)(
  implicit gen: Generic.Aux[A, Repr]
): Double = {
  shape match {
    case Circle(r) => math.Pi * r * r
    case Rectangle(w, h) => w * h
    case Triangle(a, b, c) =>
      val s = (a + b + c) / 2
      math.sqrt(s * (s - a) * (s - b) * (s - c))
  }
}

val circle = Circle(5.0)
val area = calculateArea(circle)  // ~78.54
```

### Автоматическая деривация для конвертации между типами

```scala
import shapeless.Generic

// Автоматическая конвертация между типами с одинаковой структурой
case class User(id: Long, name: String, email: String)
case class Person(id: Long, name: String, email: String)

def convert[A, B, Repr](a: A)(
  implicit
  genA: Generic.Aux[A, Repr],
  genB: Generic.Aux[B, Repr]
): B = {
  genB.from(genA.to(a))
}

val user = User(1L, "Alice", "alice@example.com")
val person = convert[User, Person](user)
// Person(1L, "Alice", "alice@example.com")
```

### Автоматическая деривация для работы с опциональными полями

```scala
import shapeless.{Generic, HList}
import shapeless.ops.hlist.Selector

// Работа с опциональными полями через HList
case class User(name: String, age: Int, email: Option[String])

def getEmail[A, Repr <: HList](user: A)(
  implicit
  gen: Generic.Aux[A, Repr],
  selector: Selector[Repr, Option[String]]
): Option[String] = {
  selector(gen.to(user))
}

val user = User("Alice", 30, Some("alice@example.com"))
val email = getEmail(user)  // Some("alice@example.com")
```

### Автоматическая деривация для валидации

```scala
import shapeless.{Generic, HList}
import shapeless.ops.hlist.Mapper

// Автоматическая валидация полей
trait Validator[A] {
  def validate(a: A): List[String]
}

object Validator {
  implicit val stringValidator: Validator[String] = (s: String) => {
    if (s.isEmpty) List("String cannot be empty") else Nil
  }

  implicit val intValidator: Validator[Int] = (i: Int) => {
    if (i < 0) List("Int must be non-negative") else Nil
  }

  implicit def hlistValidator[H, T <: HList](
    implicit
    hValidator: Validator[H],
    tValidator: Validator[T]
  ): Validator[H :: T] = { hlist =>
    hValidator.validate(hlist.head) ++ tValidator.validate(hlist.tail)
  }

  implicit def genericValidator[A, Repr <: HList](
    implicit
    gen: Generic.Aux[A, Repr],
    validator: Validator[Repr]
  ): Validator[A] = (a: A) => {
    validator.validate(gen.to(a))
  }
}

case class User(name: String, age: Int)
val user = User("", -5)
val errors = Validator[User].validate(user)
// List("String cannot be empty", "Int must be non-negative")
```

### Автоматическая деривация для создания Builder

```scala
import shapeless.{Generic, HList, HNil, ::}
import shapeless.labelled.{FieldType, field}
import shapeless.syntax.singleton._

// Автоматический Builder для case class
class Builder[A, Repr <: HList](
  private val fields: Repr
)(implicit gen: Generic.Aux[A, Repr]) {
  def set[K <: Symbol, V](key: K, value: V)(
    implicit
    witness: Witness.Aux[K]
  ): Builder[A, FieldType[K, V] :: Repr] = {
    new Builder[A, FieldType[K, V] :: Repr](field[K](value) :: fields)
  }

  def build(implicit gen: Generic.Aux[A, Repr]): A = {
    gen.from(fields.reverse)
  }
}

def builder[A, Repr <: HList](
  implicit gen: Generic.Aux[A, Repr]
): Builder[A, HNil] = {
  new Builder[A, HNil](HNil)
}

case class User(name: String, age: Int, email: String)
val user = builder[User]
  .set("name", "Alice")
  .set("age", 30)
  .set("email", "alice@example.com")
  .build
// User("Alice", 30, "alice@example.com")
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Shapeless** — это библиотека для **generic** программирования в **Scala**, которая позволяет работать с типами на уровне компиляции. Понимание **HList**, **Generic**, **Lens**, **type-level** вычислений, автоматической деривации типов, преобразования между типами, копирования с изменениями и их практических применений позволяет создавать типобезопасный, автоматически генерируемый код.

Использование **Shapeless** для автоматической сериализации, преобразования типов, создания **Lens**, **type-level** вычислений, автоматической деривации типов и практических применений критично для создания гибких, типобезопасных библиотек и фреймворков.

**Shapeless** предоставляет мощные инструменты для автоматической генерации кода, включая деривацию **JSON** сериализаторов, **equals**, **hashCode**, **toString**, работу с **Coproduct**, **Records**, **ADT**, конвертацию между типами, работу с опциональными полями, валидацию и создание **Builder**. Понимание этих техник позволяет создавать сложные, типобезопасные библиотеки и фреймворки.

### Работа с TypeTags для runtime информации о типах

```scala
import shapeless.Typeable
import shapeless.TypeCase

// TypeCase для pattern matching по типам
val intCase = TypeCase[Int]
val stringCase = TypeCase[String]

def process(value: Any): String = {
  value match {
    case intCase(i) => s"Integer: $i"
    case stringCase(s) => s"String: $s"
    case _ => "Unknown type"
  }
}

val result1 = process(42)  // "Integer: 42"
val result2 = process("hello")  // "String: hello"
```

### Работа с Witness для type-level значений

```scala
import shapeless.Witness
import shapeless.syntax.singleton._

// Witness для получения значения из type-level символа
val nameWitness = Witness("name")
val nameValue = nameWitness.value  // "name"

// Создание singleton типов
val nameSingleton = "name".narrow  // Тип: "name" (не String)
```

### Работа с Sized для типобезопасных размеров коллекций

```scala
import shapeless.Sized
import shapeless.nat._

// Sized гарантирует размер коллекции на уровне типов
val sizedList: Sized[List[Int], _3] = Sized(1, 2, 3)

// Операции типобезопасны
val doubled = sizedList.map(_ * 2)  // Sized[List[Int], _3]
val sum = sizedList.foldLeft(0)(_ + _)  // 6

// Преобразование в обычный список
val list = sizedList.unsized  // List(1, 2, 3)
```

### Работа с Poly для полиморфных функций

```scala
import shapeless.Poly1

// Полиморфная функция для различных типов
object double extends Poly1 {
  implicit def caseInt = at[Int](_ * 2)
  implicit def caseString = at[String](_ + _)
  implicit def caseDouble = at[Double](_ * 2.0)
}

val hlist = 1 :: "hello" :: 3.14 :: HNil
val doubled = hlist.map(double)
// 2 :: "hellohello" :: 6.28 :: HNil
```

### Работа с Typeable для безопасного приведения типов

```scala
import shapeless.Typeable

// Typeable для безопасного приведения типов
val intTypeable = Typeable[Int]
val stringTypeable = Typeable[String]

def safeCast[A: Typeable](value: Any): Option[A] = {
  implicitly[Typeable[A]].cast(value)
}

val maybeInt = safeCast[Int](42)  // Some(42)
val maybeString = safeCast[String](42)  // None
```

### Работа с UnaryTCConstraint для ограничений типов

```scala
import shapeless.UnaryTCConstraint

// UnaryTCConstraint для проверки, что все элементы HList имеют определенный type class
trait Show[A] {
  def show(a: A): String
}

implicit val intShow: Show[Int] = (i: Int) => i.toString
implicit val stringShow: Show[String] = (s: String) => s

def showAll[L <: HList](hlist: L)(
  implicit constraint: UnaryTCConstraint[L, Show]
): List[String] = {
  hlist.toList.map(_.asInstanceOf[Show[_]].show(_))
}
```

### Работа с Nat для type-level чисел

```scala
import shapeless.nat._

// Nat для type-level чисел
type Three = _3
type Five = _5

// Type-level операции
import shapeless.ops.nat.{Sum, Prod}

type Eight = Sum[_3, _5]  // _8
type Fifteen = Prod[_3, _5]  // _15
```

### Работа с Singleton для singleton типов

```scala
import shapeless.syntax.singleton._

// Singleton типы для значений, известных на этапе компиляции
val name = "name".narrow  // Тип: "name" (не String)
val age = 42.narrow  // Тип: 42 (не Int)

// Использование в type-level вычислениях
def processName(name: "name".type): String = {
  s"Processing: $name"
}

val result = processName("name")  // "Processing: name"
```

### Работа с LabelledGeneric для работы с именами полей

```scala
import shapeless.LabelledGeneric
import shapeless.record._

case class User(name: String, age: Int, email: String)

// LabelledGeneric сохраняет имена полей
val gen = LabelledGeneric[User]
val user = User("Alice", 30, "alice@example.com")
val record = gen.to(user)

// Доступ к полям по символу
val name = record("name")  // "Alice"
val age = record("age")  // 30
```

### Работа с ops.hlist для операций над HList

```scala
import shapeless.ops.hlist.{Length, Reverse, Take, Drop}

val hlist = "hello" :: 42 :: true :: HNil

// Получение длины на уровне типов
val length = Length[hlist.type]  // _3

// Реверс
val reversed = Reverse(hlist)  // true :: 42 :: "hello" :: HNil

// Взятие первых N элементов
val taken = Take[_2](hlist)  // "hello" :: 42 :: HNil

// Удаление первых N элементов
val dropped = Drop[_1](hlist)  // 42 :: true :: HNil
```

### Работа с ops.tuple для операций над tuple

```scala
import shapeless.ops.tuple.{Length, Reverse, Take, Drop}

val tuple = ("hello", 42, true)

// Получение длины
val length = Length[tuple.type]  // _3

// Реверс
val reversed = Reverse(tuple)  // (true, 42, "hello")

// Взятие первых N элементов
val taken = Take[_2](tuple)  // ("hello", 42)

// Удаление первых N элементов
val dropped = Drop[_1](tuple)  // (42, true)
```

### Работа с ops.record для операций над Records

```scala
import shapeless.record._
import shapeless.syntax.singleton._

type UserRecord = Record.`'name -> String, 'age -> Int, 'email -> String`.T

val user: UserRecord =
  ("name" ->> "Alice") ::
  ("age" ->> 30) ::
  ("email" ->> "alice@example.com") ::
  HNil

// Обновление поля
val updated = user.updateWith("age")(_ + 1)  // age становится 31

// Удаление поля
val withoutEmail = user.remove("email")

// Добавление поля
val withPhone = user + ("phone" ->> "123-456-7890")
```

### Работа с ops.coproduct для операций над Coproduct

```scala
import shapeless.{:+:, CNil, Coproduct}
import shapeless.ops.coproduct.{Inject, Selector}

type IntOrString = Int :+: String :+: CNil

// Inject для добавления значения в Coproduct
val intValue = Coproduct[IntOrString](42)
val stringValue = Coproduct[IntOrString]("hello")

// Selector для извлечения значения из Coproduct
def extractInt(value: IntOrString): Option[Int] = {
  Selector[IntOrString, Int].apply(value)
}

val maybeInt = extractInt(intValue)  // Some(42)
val maybeString = extractInt(stringValue)  // None
```

### Работа с ops.nat для type-level арифметики

```scala
import shapeless.nat._
import shapeless.ops.nat.{Sum, Prod, Diff, LT}

// Type-level сложение
type Eight = Sum[_3, _5]  // _8

// Type-level умножение
type Fifteen = Prod[_3, _5]  // _15

// Type-level вычитание
type Three = Diff[_5, _2]  // _3

// Type-level сравнение
type IsLess = LT[_3, _5]  // True
```

### Работа с ops.typeable для безопасного приведения типов

```scala
import shapeless.Typeable
import shapeless.ops.typeable.Cast

// Cast для безопасного приведения типов
def safeCast[A: Typeable](value: Any): Option[A] = {
  Cast[A].apply(value)
}

val maybeInt = safeCast[Int](42)  // Some(42)
val maybeString = safeCast[String](42)  // None
```

### Работа с ops.hlist.ToTraversable для преобразования HList в коллекцию

```scala
import shapeless.ops.hlist.ToTraversable

val hlist = "hello" :: 42 :: true :: HNil

// Преобразование в List
val list = ToTraversable[List].apply(hlist)  // List("hello", 42, true)

// Преобразование в Vector
val vector = ToTraversable[Vector].apply(hlist)  // Vector("hello", 42, true)
```

### Работа с ops.hlist.Mapper для применения функции к каждому элементу

```scala
import shapeless.ops.hlist.Mapper
import shapeless.Poly1

// Полиморфная функция
object double extends Poly1 {
  implicit def caseInt = at[Int](_ * 2)
  implicit def caseString = at[String](_ + _)
}

val hlist = 1 :: "hello" :: 42 :: HNil

// Применение функции к каждому элементу
val mapped = Mapper[double.type].apply(hlist)
// 2 :: "hellohello" :: 84 :: HNil
```

### Работа с ops.hlist.FlatMapper для flatMap операций

```scala
import shapeless.ops.hlist.FlatMapper
import shapeless.Poly1

// Полиморфная функция, возвращающая HList
object expand extends Poly1 {
  implicit def caseInt = at[Int](i => i :: i + 1 :: HNil)
  implicit def caseString = at[String](s => s :: s.toUpperCase :: HNil)
}

val hlist = 1 :: "hello" :: HNil

// Применение flatMap
val flatMapped = FlatMapper[expand.type].apply(hlist)
// 1 :: 2 :: "hello" :: "HELLO" :: HNil
```

### Работа с ops.hlist.Zip для объединения HList

```scala
import shapeless.ops.hlist.Zip

val hlist1 = "hello" :: 42 :: true :: HNil
val hlist2 = "world" :: 100 :: false :: HNil

// Объединение в tuple
val zipped = Zip(hlist1, hlist2)
// ("hello", "world") :: (42, 100) :: (true, false) :: HNil
```

### Работа с ops.hlist.Unzip для разделения HList

```scala
import shapeless.ops.hlist.Unzip

val hlist = ("hello", "world") :: (42, 100) :: (true, false) :: HNil

// Разделение на два HList
val (hlist1, hlist2) = Unzip(hlist)
// hlist1: "hello" :: 42 :: true :: HNil
// hlist2: "world" :: 100 :: false :: HNil
```

### Работа с ops.hlist.Prepend для добавления элемента

```scala
import shapeless.ops.hlist.Prepend

val hlist = 42 :: true :: HNil

// Добавление элемента в начало
val prepended = Prepend("hello" :: HNil, hlist)
// "hello" :: 42 :: true :: HNil
```

### Работа с ops.hlist.Append для добавления элемента в конец

```scala
import shapeless.ops.hlist.Append

val hlist = "hello" :: 42 :: HNil

// Добавление элемента в конец
val appended = Append(hlist, true :: HNil)
// "hello" :: 42 :: true :: HNil
```

## Дополнительные ресурсы

- [Shapeless Documentation](https://github.com/milessabin/shapeless)
- [Shapeless Guide](https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0)
- [Type Astronaut's Guide to Shapeless](https://github.com/underscoreio/shapeless-guide)

## См. также

- [[scala-akka-streams|Akka Streams в Scala]]
- [[scala-another|Scala Additional Topics]]
- [[scala-basics|Scala: основы]]
- [[scala-cats-effect|Cats Effect в Scala]]
- [[scala-collections-array|Scala Collections — Array]]
