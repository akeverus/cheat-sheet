---
title: "Type Classes в Scala"
description: "Краткое руководство по Type Classes в Scala - полиморфизм на основе паттерна Ad-hoc."
tags:
  - languages
  - scala
  - scala-type-classes
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Type Classes в Scala

Краткое руководство по **Type Classes** в **Scala** — полиморфизм на основе паттерна **Ad-hoc**.

**Последнее обновление**: 2026-02-11

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [Основы **Type Classes**](#основы-type-classes)
  - [Определение **Type Class**](#определение-type-class)
  - [Синтаксический сахар с **implicit class**](#синтаксический-сахар-с-implicit-class)
- [**Implicit Type Classes**](#implicit-type-classes)
  - [Реализация **Type Class** через **implicit**](#реализация-type-class-через-implicit)
  - [Композиция **Type Classes**](#композиция-type-classes)
- [Практические примеры](#практические-примеры)
  - [**Type Class** для сравнения](#type-class-для-сравнения)
  - [**Type Class** для сериализации](#type-class-для-сериализации)
- [**Type Classes** в **Cats**](#type-classes-в-cats)
  - [**Cats Type Classes**](#cats-type-classes)
  - [Практические примеры с **Cats**](#практические-примеры-с-cats)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте **companion objects** для инстансов](#1-используйте-companion-objects-для-инстансов)
  - [2. Используйте **implicit syntax classes** для удобства](#2-используйте-implicit-syntax-classes-для-удобства)
  - [Практические примеры: **Type Class** для **JSON** сериализации](#практические-примеры-type-class-для-json-сериализации)
  - [Практические примеры: **Type Class** для числовых операций](#практические-примеры-type-class-для-числовых-операций)
  - [Практические примеры: **Type Class** для ввода-вывода](#практические-примеры-type-class-для-ввода-вывода)
  - [**Type Class** для работы с монадами](#type-class-для-работы-с-монадами)
  - [**Type Class** для функторов](#type-class-для-функторов)
  - [**Type Class** для **applicative functors**](#type-class-для-applicative-functors)
  - [**Type Class** для **foldable**](#type-class-для-foldable)
  - [**Type Class** для **traversable**](#type-class-для-traversable)
  - [**Type Class** для контравариантных функторов](#type-class-для-контравариантных-функторов)
  - [**Type Class** для бифункторов](#type-class-для-бифункторов)
- [Что выбрать: Type Class, наследование или extension methods](#что-выбрать-type-class-наследование-или-extension-methods)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Использование с различными типами для композиции](#использование-с-различными-типами-для-композиции)
  - [Использование с различными типами для обработки ошибок](#использование-с-различными-типами-для-обработки-ошибок)
  - [Использование с различными типами для валидации](#использование-с-различными-типами-для-валидации)
  - [Использование с различными типами для работы с состояниями](#использование-с-различными-типами-для-работы-с-состояниями)
  - [Использование с различными типами для работы с логами](#использование-с-различными-типами-для-работы-с-логами)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Type Class** — это паттерн в функциональном программировании, который позволяет добавлять поведение к типам без модификации самих типов. В **Scala Type Classes** реализуются через **implicit** параметры и **implicit conversions**.

**Type Classes** обеспечивают полиморфизм, который более гибкий чем наследование, так как поведение можно добавлять к типам из любой иерархии.

## Основы **Type Classes**

### Определение **Type Class**

```scala
// Определение Type Class
trait Show[A] {
  def show(a: A): String
}

// Инстансы для различных типов
implicit val intShow: Show[Int] = new Show[Int] {
  def show(a: Int): String = s"Int($a)"
}

implicit val stringShow: Show[String] = new Show[String] {
  def show(a: String): String = s"String($a)"
}

// Использование Type Class
def print[A](a: A)(implicit show: Show[A]): Unit = {
  println(show.show(a))
}

print(42)     // Int(42)
print("hello") // String(hello)
```

### Синтаксический сахар с **implicit class**

```scala
trait Show[A] {
  def show(a: A): String
}

object Show {
  def apply[A](implicit instance: Show[A]): Show[A] = instance

  implicit class ShowOps[A](a: A)(implicit show: Show[A]) {
    def show: String = show.show(a)
  }
}

// Использование
import Show.ShowOps

implicit val intShow: Show[Int] = (a: Int) => s"Int($a)"
implicit val stringShow: Show[String] = (a: String) => s"String($a)"

42.show      // "Int(42)"
"hello".show // "String(hello)"
```

## **Implicit Type Classes**

### Реализация **Type Class** через **implicit**

```scala
trait Semigroup[A] {
  def combine(x: A, y: A): A
}

object Semigroup {
  implicit val intSemigroup: Semigroup[Int] = new Semigroup[Int] {
    def combine(x: Int, y: Int): Int = x + y
  }

  implicit val stringSemigroup: Semigroup[String] = new Semigroup[String] {
    def combine(x: String, y: String): String = x + y
  }

  implicit def listSemigroup[A]: Semigroup[List[A]] = new Semigroup[List[A]] {
    def combine(x: List[A], y: List[A]): List[A] = x ++ y
  }
}

// Использование
def combine[A](x: A, y: A)(implicit s: Semigroup[A]): A = s.combine(x, y)

combine(1, 2)                    // 3
combine("a", "b")                // "ab"
combine(List(1, 2), List(3, 4))  // List(1, 2, 3, 4)
```

### Композиция **Type Classes**

```scala
trait Monoid[A] extends Semigroup[A] {
  def empty: A
}

object Monoid {
  def apply[A](implicit m: Monoid[A]): Monoid[A] = m

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    def empty: Int = 0
    def combine(x: Int, y: Int): Int = x + y
  }

  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    def empty: String = ""
    def combine(x: String, y: String): String = x + y
  }

  implicit def listMonoid[A]: Monoid[List[A]] = new Monoid[List[A]] {
    def empty: List[A] = Nil
    def combine(x: List[A], y: List[A]): List[A] = x ++ y
  }
}

// Функция, которая работает с любой Monoid
def combineAll[A](list: List[A])(implicit m: Monoid[A]): A = {
  list.foldLeft(m.empty)(m.combine)
}

combineAll(List(1, 2, 3))        // 6
combineAll(List("a", "b", "c"))  // "abc"
```

## Практические примеры

### **Type Class** для сравнения

```scala
trait Ord[A] {
  def compare(x: A, y: A): Int
  def <(x: A, y: A): Boolean = compare(x, y) < 0
  def <=(x: A, y: A): Boolean = compare(x, y) <= 0
  def >(x: A, y: A): Boolean = compare(x, y) > 0
  def >=(x: A, y: A): Boolean = compare(x, y) >= 0
}

object Ord {
  implicit val intOrd: Ord[Int] = new Ord[Int] {
    def compare(x: Int, y: Int): Int = x.compareTo(y)
  }

  implicit val stringOrd: Ord[String] = new Ord[String] {
    def compare(x: String, y: String): Int = x.compareTo(y)
  }

  implicit def listOrd[A](implicit ord: Ord[A]): Ord[List[A]] = new Ord[List[A]] {
    def compare(x: List[A], y: List[A]): Int = {
      (x zip y).map { case (a, b) => ord.compare(a, b) }.find(_ != 0)
        .getOrElse(x.length.compareTo(y.length))
    }
  }
}

def max[A](x: A, y: A)(implicit ord: Ord[A]): A = {
  if (ord.>(x, y)) x else y
}

max(10, 20)  // 20
max("a", "b") // "b"
```

### **Type Class** для сериализации

```scala
trait Serializer[A] {
  def serialize(a: A): String
}

object Serializer {
  implicit val intSerializer: Serializer[Int] = _.toString
  implicit val stringSerializer: Serializer[String] = identity
  implicit val booleanSerializer: Serializer[Boolean] = _.toString

  implicit def listSerializer[A](implicit s: Serializer[A]): Serializer[List[A]] =
    list => list.map(s.serialize).mkString("[", ",", "]")

  implicit def optionSerializer[A](implicit s: Serializer[A]): Serializer[Option[A]] = {
    case Some(a) => s.serialize(a)
    case None => "null"
  }
}

def serialize[A](a: A)(implicit s: Serializer[A]): String = s.serialize(a)

serialize(42)                      // "42"
serialize(List(1, 2, 3))           // "[1,2,3]"
serialize(Some("hello"))           // "hello"
serialize(Option.empty[String])    // "null"
```

## **Type Classes** в **Cats**

**Cats** предоставляет множество готовых **Type Classes**.

### **Cats Type Classes**

```scala
import cats._
import cats.implicits._

// Functor
val list = List(1, 2, 3).map(_ * 2)  // List(2, 4, 6)

// Applicative
val result = (Option(1), Option(2), Option(3)).mapN(_ + _ + _)  // Some(6)

// Monad
val result2 = Option(10).flatMap(x => Option(x * 2))  // Some(20)

// Monoid
val sum = List(1, 2, 3).combineAll  // 6

// Foldable
val product = List(1, 2, 3).foldMap(identity)  // 6
```

### Практические примеры с **Cats**

```scala
import cats.Monad
import cats.implicits._

def replicateM[F[_]: Monad, A](n: Int, fa: F[A]): F[List[A]] = {
  Monad[F].pure(List.fill(n)(fa)).flatten
}

val result = replicateM(3, Option(42))  // Some(List(42, 42, 42))
```

## Лучшие практики

### 1. Используйте **companion objects** для инстансов

```scala
trait Show[A] {
  def show(a: A): String
}

object Show {
  // ✅ Хорошо - инстансы в companion object
  implicit val intShow: Show[Int] = _.toString
  implicit val stringShow: Show[String] = identity
}
```

### 2. Используйте **implicit syntax classes** для удобства

```scala
object ShowSyntax {
  implicit class ShowOps[A](a: A)(implicit show: Show[A]) {
    def show: String = show.show(a)
  }
}

import ShowSyntax._

42.show  // "42"
```

### Практические примеры: **Type Class** для **JSON** сериализации

```scala
trait JsonEncoder[A] {
  def encode(a: A): String
}

object JsonEncoder {
  implicit val intEncoder: JsonEncoder[Int] = _.toString
  implicit val stringEncoder: JsonEncoder[String] = s => s""""$s""""
  implicit val booleanEncoder: JsonEncoder[Boolean] = _.toString

  implicit def listEncoder[A](implicit enc: JsonEncoder[A]): JsonEncoder[List[A]] =
    list => list.map(enc.encode).mkString("[", ",", "]")

  implicit def optionEncoder[A](implicit enc: JsonEncoder[A]): JsonEncoder[Option[A]] = {
    case Some(a) => enc.encode(a)
    case None => "null"
  }
}

object JsonEncoderSyntax {
  implicit class JsonEncoderOps[A](a: A)(implicit enc: JsonEncoder[A]) {
    def toJson: String = enc.encode(a)
  }
}

import JsonEncoderSyntax._

42.toJson              // "42"
"hello".toJson         // "\"hello\""
List(1, 2, 3).toJson   // "[1,2,3]"
Some("value").toJson   // "\"value\""
None.toJson            // "null"
```

### Практические примеры: **Type Class** для числовых операций

```scala
trait NumericOps[A] {
  def zero: A
  def one: A
  def add(x: A, y: A): A
  def multiply(x: A, y: A): A
  def negate(x: A): A
}

object NumericOps {
  implicit val intNumeric: NumericOps[Int] = new NumericOps[Int] {
    def zero: Int = 0
    def one: Int = 1
    def add(x: Int, y: Int): Int = x + y
    def multiply(x: Int, y: Int): Int = x * y
    def negate(x: Int): Int = -x
  }

  implicit val doubleNumeric: NumericOps[Double] = new NumericOps[Double] {
    def zero: Double = 0.0
    def one: Double = 1.0
    def add(x: Double, y: Double): Double = x + y
    def multiply(x: Double, y: Double): Double = x * y
    def negate(x: Double): Double = -x
  }

  implicit val bigIntNumeric: NumericOps[BigInt] = new NumericOps[BigInt] {
    def zero: BigInt = BigInt(0)
    def one: BigInt = BigInt(1)
    def add(x: BigInt, y: BigInt): BigInt = x + y
    def multiply(x: BigInt, y: BigInt): BigInt = x * y
    def negate(x: BigInt): BigInt = -x
  }
}

def sum[A](list: List[A])(implicit ops: NumericOps[A]): A = {
  list.foldLeft(ops.zero)(ops.add)
}

def product[A](list: List[A])(implicit ops: NumericOps[A]): A = {
  list.foldLeft(ops.one)(ops.multiply)
}

sum(List(1, 2, 3))              // 6
sum(List(1.0, 2.0, 3.0))        // 6.0
product(List(2, 3, 4))          // 24
```

### Практические примеры: **Type Class** для ввода-вывода

```scala
trait Read[A] {
  def read(s: String): Option[A]
}

object Read {
  implicit val intRead: Read[Int] = s => s.toIntOption

  implicit val doubleRead: Read[Double] = s => s.toDoubleOption

  implicit val stringRead: Read[String] = Some(_)

  implicit val booleanRead: Read[Boolean] = s =>
    s.toLowerCase match {
      case "true" | "1" | "yes" => Some(true)
      case "false" | "0" | "no" => Some(false)
      case _ => None
    }
}

def readInput[A](prompt: String)(implicit read: Read[A]): Option[A] = {
  print(s"$prompt: ")
  val input = scala.io.StdIn.readLine()
  read.read(input)
}

// Использование
val age = readInput[Int]("Enter your age")
val salary = readInput[Double]("Enter your salary")
val name = readInput[String]("Enter your name")
```

### **Type Class** для работы с монадами

```scala
trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))
}

object Monad {
  implicit val optionMonad: Monad[Option] = new Monad[Option] {
    def pure[A](a: A): Option[A] = Some(a)
    def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] =
      fa.flatMap(f)
  }

  implicit val listMonad: Monad[List] = new Monad[List] {
    def pure[A](a: A): List[A] = List(a)
    def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
      fa.flatMap(f)
  }

  implicit def eitherMonad[E]: Monad[Either[E, *]] = new Monad[Either[E, *]] {
    def pure[A](a: A): Either[E, A] = Right(a)
    def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
      fa.flatMap(f)
  }
}

def sequence[F[_]: Monad, A](list: List[F[A]]): F[List[A]] = {
  val m = implicitly[Monad[F]]
  list.foldRight(m.pure(List.empty[A])) { (fa, acc) =>
    m.flatMap(fa)(a => m.map(acc)(as => a :: as))
  }
}

val result = sequence(List(Some(1), Some(2), Some(3)))  // Some(List(1, 2, 3))
```

### **Type Class** для функторов

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }

  implicit val listFunctor: Functor[List] = new Functor[List] {
    def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
  }

  implicit def eitherFunctor[E]: Functor[Either[E, *]] =
    new Functor[Either[E, *]] {
      def map[A, B](fa: Either[E, A])(f: A => B): Either[E, B] =
        fa.map(f)
    }
}

def fmap[F[_]: Functor, A, B](fa: F[A])(f: A => B): F[B] = {
  implicitly[Functor[F]].map(fa)(f)
}

val result = fmap(Some(10))(_ * 2)  // Some(20)
```

### **Type Class** для **applicative functors**

```scala
trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
  def ap[A, B](fa: F[A])(ff: F[A => B]): F[B]

  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
    ap(fb)(map(fa)(a => (b: B) => f(a, b)))
  }
}

object Applicative {
  implicit val optionApplicative: Applicative[Option] =
    new Applicative[Option] {
      def pure[A](a: A): Option[A] = Some(a)
      def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
      def ap[A, B](fa: Option[A])(ff: Option[A => B]): Option[B] =
        (fa, ff) match {
          case (Some(a), Some(f)) => Some(f(a))
          case _ => None
        }
    }
}

def liftA2[F[_]: Applicative, A, B, C](
  f: (A, B) => C
)(fa: F[A], fb: F[B]): F[C] = {
  implicitly[Applicative[F]].map2(fa, fb)(f)
}

val result = liftA2((a: Int, b: Int) => a + b)(Some(10), Some(20))  // Some(30)
```

### **Type Class** для **foldable**

```scala
trait Foldable[F[_]] {
  def foldLeft[A, B](fa: F[A], z: B)(f: (B, A) => B): B
  def foldRight[A, B](fa: F[A], z: B)(f: (A, B) => B): B
  def foldMap[A, B: Monoid](fa: F[A])(f: A => B): B
}

object Foldable {
  implicit val listFoldable: Foldable[List] = new Foldable[List] {
    def foldLeft[A, B](fa: List[A], z: B)(f: (B, A) => B): B =
      fa.foldLeft(z)(f)
    def foldRight[A, B](fa: List[A], z: B)(f: (A, B) => B): B =
      fa.foldRight(z)(f)
    def foldMap[A, B: Monoid](fa: List[A])(f: A => B): B = {
      val m = implicitly[Monoid[B]]
      foldLeft(fa, m.empty)((b, a) => m.combine(b, f(a)))
    }
  }
}

def sum[F[_]: Foldable, A: Monoid](fa: F[A]): A = {
  implicitly[Foldable[F]].foldMap(fa)(identity)
}

val result = sum(List(1, 2, 3))  // 6
```

### **Type Class** для **traversable**

```scala
trait Traverse[F[_]] extends Functor[F] with Foldable[F] {
  def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
  def sequence[G[_]: Applicative, A](fga: F[G[A]]): G[F[A]] =
    traverse(fga)(identity)
}

object Traverse {
  implicit val listTraverse: Traverse[List] = new Traverse[List] {
    def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
    def foldLeft[A, B](fa: List[A], z: B)(f: (B, A) => B): B =
      fa.foldLeft(z)(f)
    def foldRight[A, B](fa: List[A], z: B)(f: (A, B) => B): B =
      fa.foldRight(z)(f)
    def foldMap[A, B: Monoid](fa: List[A])(f: A => B): B = {
      val m = implicitly[Monoid[B]]
      foldLeft(fa, m.empty)((b, a) => m.combine(b, f(a)))
    }
    def traverse[G[_]: Applicative, A, B](fa: List[A])(f: A => G[B]): G[List[B]] = {
      val ap = implicitly[Applicative[G]]
      fa.foldRight(ap.pure(List.empty[B])) { (a, acc) =>
        ap.map2(f(a), acc)(_ :: _)
      }
    }
  }
}

val list = List(Some(1), Some(2), Some(3))
val result = implicitly[Traverse[List]].sequence(list)  // Some(List(1, 2, 3))
```

### **Type Class** для контравариантных функторов

```scala
trait Contravariant[F[_]] {
  def contramap[A, B](fa: F[A])(f: B => A): F[B]
}

case class Predicate[A](run: A => Boolean)

object Contravariant {
  implicit val predicateContravariant: Contravariant[Predicate] =
    new Contravariant[Predicate] {
      def contramap[A, B](fa: Predicate[A])(f: B => A): Predicate[B] =
        Predicate(b => fa.run(f(b)))
    }
}

val intPredicate = Predicate[Int](_ > 10)
val stringPredicate = implicitly[Contravariant[Predicate]]
  .contramap(intPredicate)((s: String) => s.length)

val result = stringPredicate.run("hello")  // false (length = 5)
val result2 = stringPredicate.run("hello world")  // true (length = 11)
```

### **Type Class** для бифункторов

```scala
trait Bifunctor[F[_, _]] {
  def bimap[A, B, C, D](fab: F[A, B])(f: A => C, g: B => D): F[C, D]
  def leftMap[A, B, C](fab: F[A, B])(f: A => C): F[C, B] =
    bimap(fab)(f, identity)
  def rightMap[A, B, D](fab: F[A, B])(g: B => D): F[A, D] =
    bimap(fab)(identity, g)
}

object Bifunctor {
  implicit val eitherBifunctor: Bifunctor[Either] =
    new Bifunctor[Either] {
      def bimap[A, B, C, D](fab: Either[A, B])(f: A => C, g: B => D): Either[C, D] =
        fab match {
          case Left(a) => Left(f(a))
          case Right(b) => Right(g(b))
        }
    }
}

val either: Either[String, Int] = Left("error")
val result = implicitly[Bifunctor[Either]].bimap(either)(
  _.toUpperCase,
  _ * 2
)  // Left("ERROR")
```


## Что выбрать: Type Class, наследование или extension methods

| Подход | Когда подходит лучше всего | Ограничения |
|---|---|---|
| `Type Class` | Нужно добавить поведение к внешним типам и иметь несколько реализаций поведения | сложнее discoverability, нужно контролировать implicit/given scope |
| Наследование (`trait`/`class`) | Поведение является частью доменной модели и иерархии | жёсткая связка с иерархией, сложнее переиспользование для сторонних типов |
| `extension methods` | Нужен удобный синтаксис поверх уже выбранной модели поведения | сами по себе не решают задачу ad-hoc полиморфизма |

Практическое правило: если требуется подменять реализацию поведения по контексту (например, разные сериализаторы), обычно выбирают `Type Class`.

## Решение проблем

| Проблема | Причина | Решение |
|---|---|---|
| `could not find implicit value` | инстанс `Type Class` не в scope | импортировать нужный companion/syntax модуль или явно передать инстанс |
| `ambiguous implicit values` | в scope попали два совместимых инстанса | сузить imports, оставить один source of truth для инстансов |
| Сложно понять, откуда взялся инстанс | цепочка implicit/given imports слишком широкая | держать инстансы ближе к доменной модели и документировать публичные imports |
| Ошибки миграции Scala 2 -> Scala 3 (`implicit` -> `given/using`) | смешанный стиль в проекте | зафиксировать единый стиль для модуля и последовательно мигрировать API |

## Частые вопросы

**Type Class — это «лучше», чем наследование?**
Не всегда. Это другой инструмент: лучше там, где нужен ad-hoc полиморфизм и независимость от иерархии типов.

**Где хранить инстансы Type Class?**
Чаще всего в companion object типа или в отдельном модуле `instances`, который импортируется явно.

**Можно ли использовать Type Class без Cats?**
Да, это языковой паттерн Scala. Библиотеки (`Cats`, `ZIO Prelude`) лишь дают готовые абстракции и экосистему.

**Когда extension methods достаточно без Type Class?**
Когда нужен только синтаксический sugar и нет требований к переключению реализаций поведения по контексту.

## Заключение

**Type Classes** — это мощный паттерн в **Scala**, который позволяет добавлять поведение к типам без модификации самих типов. Использование **Type Classes** обеспечивает полиморфизм, который более гибкий чем наследование.

Понимание **Type Classes**, их реализации через **implicit** параметры, создания **Type Class** для **JSON** сериализации, числовых операций, ввода-вывода и практических применений критично для эффективного использования библиотек типа **Cats** и создания гибкого, композируемого, типобезопасного кода.

**Type Classes** предоставляют мощные инструменты для работы с различными абстракциями, включая **Functor**, **Applicative**, **Monad**, **Foldable**, **Traverse**, **Contravariant** и **Bifunctor**. Понимание этих абстракций позволяет создавать сложные, типобезопасные функциональные программы.

### Использование с различными типами для композиции

```scala
import cats.Functor
import cats.syntax.all._

// Композиция Functor
def mapCompose[F[_]: Functor, G[_]: Functor, A, B](
  f: A => B
): F[G[A]] => F[G[B]] = {
  _.map(_.map(f))
}

val nested = Some(List(1, 2, 3))
val result = mapCompose[Option, List, Int, Int](_ * 2)(nested)
// Some(List(2, 4, 6))
```

### Использование с различными типами для обработки ошибок

```scala
import cats.MonadError
import cats.syntax.all._

// Обработка ошибок с MonadError
def safeDivide[F[_]: MonadError[*[_], String]](a: Int, b: Int): F[Double] = {
  if (b == 0) MonadError[F, String].raiseError("Division by zero")
  else MonadError[F, String].pure(a.toDouble / b)
}

// Использование с Either
val eitherResult = safeDivide[Either[String, ?]](10, 2)
// Right(5.0)

// Использование с Option
val optionResult = safeDivide[Option](10, 2)
// Some(5.0)
```

### Использование с различными типами для валидации

```scala
import cats.Applicative
import cats.data.ValidatedNel
import cats.syntax.all._

// Валидация с Applicative
def validateName(name: String): ValidatedNel[String, String] =
  if (name.nonEmpty) name.validNel else "Name cannot be empty".invalidNel

def validateAge(age: Int): ValidatedNel[String, Int] =
  if (age >= 0) age.validNel else "Age must be non-negative".invalidNel

val validatedResult = (validateName("Alice"), validateAge(30)).mapN((name, age) =>
  (name, age)
)
```

### Использование с различными типами для работы с состояниями

```scala
import cats.Monad
import cats.data.State
import cats.syntax.all._

// Работа с состояниями
type Counter = Int

def increment: State[Counter, Int] = State { count =>
  (count + 1, count + 1)
}

def decrement: State[Counter, Int] = State { count =>
  (count - 1, count - 1)
}

val stateResult = for {
  _ <- increment
  _ <- increment
  result <- decrement
} yield result

val (finalState, value) = stateResult.run(0).value
```

### Использование с различными типами для работы с логами

```scala
import cats.Monad
import cats.data.Writer
import cats.syntax.all._

// Работа с логами
type Logged[A] = Writer[List[String], A]

def add(x: Int, y: Int): Logged[Int] = {
  val result = x + y
  List(s"Adding $x and $y").tell.map(_ => result)
}

def multiply(x: Int, y: Int): Logged[Int] = {
  val result = x * y
  List(s"Multiplying $x and $y").tell.map(_ => result)
}

val loggedResult = for {
  sum <- add(10, 20)
  product <- multiply(sum, 2)
} yield product

val (logs, result) = loggedResult.run
```

## Дополнительные ресурсы

- [Cats Type Classes](https://typelevel.org/cats/typeclasses.html)
- [Scala Type Classes Tutorial](https://www.scala-lang.org/old/node/126.html)
- [Functional Programming in Scala](https://www.manning.com/books/functional-programming-in-scala)

## См. также

- [[scala-akka-streams|Akka Streams в Scala]]
- [[scala-another|Scala Additional Topics]]
- [[scala-basics|Scala: основы]]
- [[scala-cats-effect|Cats Effect в Scala]]
- [[scala-collections-array|Scala Collections — Array]]
