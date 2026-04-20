---
title: "Scala Error Handling"
description: "Полное руководство по обработке ошибок в Scala: Option, Either, Try, обработка исключений, функциональный подход"
tags:
  - scala
  - error-handling
  - option
  - either
  - try
  - exceptions
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-basics.md", "scala/scala-fp-basics.md"]
---

# **Scala Error Handling**

Кратко: полное руководство по обработке ошибок в **Scala**: **Option**, **Either**, **Try**, обработка исключений, функциональный подход.

## Полезные ссылки

### Официальная документация
- [Scala Option](https://www.scala-lang.org/api/current/scala/Option.html)
- [Scala Either](https://www.scala-lang.org/api/current/scala/util/Either.html)
- [Scala Try](https://www.scala-lang.org/api/current/scala/util/Try.html)

### См. также
- [[scala-basics|Основы Scala]]
- [[scala-fp-basics|Функциональное программирование]]

## Содержание

- [**Scala Error Handling**](#scala-error-handling)
- [Введение в обработку ошибок](#введение-в-обработку-ошибок)
- [**Option**](#option)
- [**Either**](#either)
- [**Try**](#try)
- [Обработка исключений](#обработка-исключений)
  - [**Option** — расширенные операции](#option-расширенные-операции)
  - [**Either** — расширенные операции](#either-расширенные-операции)
  - [**Try** — расширенные операции](#try-расширенные-операции)
  - [Комбинирование **Option**, **Either** и **Try**](#комбинирование-option-either-и-try)
  - [Практический пример: Валидация данных](#практический-пример-валидация-данных)
  - [Практический пример: Обработка файлов](#практический-пример-обработка-файлов)
  - [Практический пример: Цепочка операций](#практический-пример-цепочка-операций)
  - [Обработка исключений — расширенные возможности](#обработка-исключений-расширенные-возможности)
- [Лучшие практики](#лучшие-практики)
  - [Предпочтение **Option** вместо **null**](#предпочтение-option-вместо-null)
  - [Использование **Either** для явных ошибок](#использование-either-для-явных-ошибок)
  - [Использование **Try** для операций с исключениями](#использование-try-для-операций-с-исключениями)
  - [Комбинирование типов обработки ошибок](#комбинирование-типов-обработки-ошибок)
- [Продвинутые техники обработки ошибок](#продвинутые-техники-обработки-ошибок)
  - [**Error Accumulation**](#error-accumulation)
  - [**Error Recovery**](#error-recovery)
  - [**Error Transformation**](#error-transformation)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные техники обработки ошибок](#дополнительные-техники-обработки-ошибок)
  - [Накопление ошибок с **Validated**](#накопление-ошибок-с-validated)
  - [Восстановление после ошибок](#восстановление-после-ошибок)
  - [Практические примеры: Композиция обработки ошибок](#практические-примеры-композиция-обработки-ошибок)
  - [Практические примеры: Накопление ошибок с **Validated**](#практические-примеры-накопление-ошибок-с-validated)
  - [Практические примеры: Восстановление после ошибок](#практические-примеры-восстановление-после-ошибок)
  - [Практические примеры: Комбинирование **Option**, **Either** и **Try**](#практические-примеры-комбинирование-option-either-и-try)
  - [Практические примеры: Обработка ошибок в цепочках операций](#практические-примеры-обработка-ошибок-в-цепочках-операций)
  - [Использование с различными техниками для композиции обработки ошибок](#использование-с-различными-техниками-для-композиции-обработки-ошибок)
  - [Использование с различными техниками для трансформации ошибок](#использование-с-различными-техниками-для-трансформации-ошибок)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в обработку ошибок

**Scala** предоставляет несколько подходов к обработке ошибок:**

1. **Option** — для значений, которые могут отсутствовать
2. **Either** — для значений, которые могут быть успешными или ошибочными
3. **Try** — для операций, которые могут выбросить исключения
4. **Исключения** — традиционный подход **Java**

## **Option**

**Option** представляет значение, которое может отсутствовать:**

```scala
// Создание Option
val some: Option[Int] = Some(42)
val none: Option[Int] = None

// Из значения
val fromValue = Option(42)  // Some(42)
val fromNull = Option(null)  // None

// Pattern matching
def processOption(opt: Option[Int]): String = opt match {
  case Some(value) => s"Value: $value"
  case None => "No value"
}

// Использование методов
val result = some.map(_ * 2)  // Some(84)
val default = none.getOrElse(0)  // 0
```

**Option** позволяет безопасно работать с возможными отсутствующими значениями.

## **Either**

**Either** представляет значение, которое может быть успешным (**Right**) или ошибочным (**Left**):**

```scala
// Создание Either
val right: Either[String, Int] = Right(42)
val left: Either[String, Int] = Left("Error")

// Pattern matching
def processEither(either: Either[String, Int]): String = either match {
  case Right(value) => s"Success: $value"
  case Left(error) => s"Error: $error"
}

// Использование методов
val result = right.map(_ * 2)  // Right(84)
val error = left.left.map(_.toUpperCase)  // Left("ERROR")
```

**Either** позволяет явно обрабатывать ошибки в функциональном стиле.

## **Try**

**Try** представляет операцию, которая может выбросить исключение:**

```scala
import scala.util.{Try, Success, Failure}

// Создание Try
val success = Try(42 / 2)  // Success(21)
val failure = Try(42 / 0)  // Failure(ArithmeticException)

// Pattern matching
def processTry(t: Try[Int]): String = t match {
  case Success(value) => s"Success: $value"
  case Failure(exception) => s"Error: ${exception.getMessage}"
}

// Использование методов
val result = success.map(_ * 2)  // Success(42)
val recovered = failure.recover {
  case _: ArithmeticException => 0
}  // Success(0)
```

**Try** позволяет безопасно обрабатывать операции, которые могут выбросить исключения.

## Обработка исключений

**Scala** поддерживает традиционную обработку исключений:**

```scala
// Try-catch блок
try {
  val result = 42 / 0
} catch {
  case e: ArithmeticException => println(s"Division by zero: ${e.getMessage}")
  case e: Exception => println(s"Error: ${e.getMessage}")
} finally {
  println("Cleanup")
}
```

Обработка исключений полезна для интеграции с **Java** кодом и критичных операций.

### **Option** — расширенные операции

**Option** предоставляет множество полезных методов:**

```scala
val some: Option[Int] = Some(42)
val none: Option[Int] = None

// Map и flatMap
val doubled = some.map(_ * 2)  // Some(84)
val flatMapped = some.flatMap(x => if (x > 0) Some(x * 2) else None)  // Some(84)

// Filter
val filtered = some.filter(_ > 50)  // None
val filtered2 = some.filter(_ > 0)  // Some(42)

// GetOrElse
val value = some.getOrElse(0)  // 42
val defaultValue = none.getOrElse(0)  // 0

// OrElse
val alternative = none.orElse(Some(0))  // Some(0)

// Fold
val result = some.fold(0)(_ * 2)  // 84
val result2 = none.fold(0)(_ * 2)  // 0

// For-comprehension
val result3 = for {
  a <- Some(5)
  b <- Some(3)
} yield a + b  // Some(8)
```

### **Either** — расширенные операции

**Either** предоставляет методы для работы с обоими случаями:**

```scala
val right: Either[String, Int] = Right(42)
val left: Either[String, Int] = Left("Error")

// Map для Right
val doubled = right.map(_ * 2)  // Right(84)
val unchanged = left.map(_ * 2)  // Left("Error")

// Map для Left
val upperError = left.left.map(_.toUpperCase)  // Left("ERROR")

// FlatMap
val flatMapped = right.flatMap(x =>
  if (x > 0) Right(x * 2) else Left("Negative value")
)  // Right(84)

// Fold
val result = right.fold(
  error => s"Error: $error",
  value => s"Value: $value"
)  // "Value: 42"

// For-comprehension
val result2 = for {
  a <- Right(5)
  b <- Right(3)
} yield a + b  // Right(8)

val result3 = for {
  a <- Right(5)
  b <- Left("Error")
} yield a + b  // Left("Error")
```

### **Try** — расширенные операции

**Try** предоставляет методы для безопасной обработки исключений:**

```scala
import scala.util.{Try, Success, Failure}

val success = Try(42 / 2)  // Success(21)
val failure = Try(42 / 0)  // Failure(ArithmeticException)

// Map и flatMap
val doubled = success.map(_ * 2)  // Success(42)
val flatMapped = success.flatMap(x => Try(x / 0))  // Failure

// Recover
val recovered = failure.recover {
  case _: ArithmeticException => 0
}  // Success(0)

// RecoverWith
val recovered2 = failure.recoverWith {
  case _: ArithmeticException => Try(0)
}  // Success(0)

// GetOrElse
val value = success.getOrElse(0)  // 21
val defaultValue = failure.getOrElse(0)  // 0

// ToOption
val option = success.toOption  // Some(21)
val option2 = failure.toOption  // None

// ToEither
val either = success.toEither  // Right(21)
val either2 = failure.toEither  // Left(ArithmeticException)
```

### Комбинирование **Option**, **Either** и **Try**

**Можно комбинировать различные типы обработки ошибок:**

```scala
import scala.util.{Try, Success, Failure}

// Option -> Try
def optionToTry[T](opt: Option[T], error: String): Try[T] = {
  opt.map(Success(_)).getOrElse(Failure(new NoSuchElementException(error)))
}

// Try -> Either
def tryToEither[T](t: Try[T]): Either[String, T] = {
  t.map(Right(_)).recover { case e => Left(e.getMessage) }.get
}

// Either -> Option
def eitherToOption[T](either: Either[String, T]): Option[T] = {
  either.toOption
}
```

### Практический пример: Валидация данных

```scala
def validateEmail(email: String): Either[String, String] = {
  if (email.contains("@")) Right(email)
  else Left("Invalid email format")
}

def validateAge(age: Int): Either[String, Int] = {
  if (age >= 0 && age <= 150) Right(age)
  else Left("Age must be between 0 and 150")
}

case class Person(email: String, age: Int)

def createPerson(email: String, age: Int): Either[String, Person] = {
  for {
    validEmail <- validateEmail(email)
    validAge <- validateAge(age)
  } yield Person(validEmail, validAge)
}

// Использование
createPerson("alice@example.com", 30)  // Right(Person(...))
createPerson("invalid", 200)  // Left("Invalid email format")
```

### Практический пример: Обработка файлов

```scala
import scala.util.{Try, Success, Failure}
import scala.io.Source

def readFile(filename: String): Try[List[String]] = {
  Try {
    val source = Source.fromFile(filename)
    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }
}

def processFile(filename: String): Either[String, Int] = {
  readFile(filename).fold(
    error => Left(s"Error reading file: ${error.getMessage}"),
    lines => Right(lines.length)
  )
}
```

### Практический пример: Цепочка операций

```scala
def getUser(id: Long): Option[User] = ???
def getPosts(userId: Long): Option[List[Post]] = ???
def getComments(postId: Long): Option[List[Comment]] = ???

// Комбинирование Option операций
val result = for {
  user <- getUser(1L)
  posts <- getPosts(user.id)
  post <- posts.headOption
  comments <- getComments(post.id)
} yield (user, post, comments)

// С обработкой ошибок через Either
def getUserEither(id: Long): Either[String, User] = ???
def getPostsEither(userId: Long): Either[String, List[Post]] = ???

val result2 = for {
  user <- getUserEither(1L)
  posts <- getPostsEither(user.id)
} yield (user, posts)
```

### Обработка исключений — расширенные возможности

```scala
// Try-catch с pattern matching
try {
  riskyOperation()
} catch {
  case e: ArithmeticException =>
    println(s"Arithmetic error: ${e.getMessage}")
  case e: NullPointerException =>
    println(s"Null pointer: ${e.getMessage}")
  case e: Exception =>
    println(s"General error: ${e.getMessage}")
} finally {
  cleanup()
}

// Использование Try для безопасной обработки
def safeOperation[T](f: => T): Try[T] = {
  Try(f)
}

// Комбинирование нескольких Try операций
val result = for {
  a <- Try(10 / 2)
  b <- Try(20 / 2)
  c <- Try(a + b)
} yield c  // Success(15)
```

## Лучшие практики

### Предпочтение **Option** вместо **null**

```scala
// Хорошо - использование Option
def findUser(id: Long): Option[User] = {
  // может вернуть None
}

// Плохо - возврат null
def findUserBad(id: Long): User = {
  // может вернуть null
}
```

### Использование **Either** для явных ошибок

```scala
// Хорошо - использование Either
def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("Division by zero")
  else Right(a / b)
}

// Плохо - выброс исключения
def divideBad(a: Int, b: Int): Int = {
  if (b == 0) throw new ArithmeticException("Division by zero")
  else a / b
}
```

### Использование **Try** для операций с исключениями

```scala
// Хорошо - использование Try для операций, которые могут выбросить исключения
def parseNumber(s: String): Try[Int] = {
  Try(s.toInt)
}

// Плохо - игнорирование возможных исключений
def parseNumberBad(s: String): Int = {
  s.toInt  // может выбросить NumberFormatException
}
```

### Комбинирование типов обработки ошибок

```scala
// Хорошо - использование подходящего типа для каждого случая
def findUser(id: Long): Option[User] = ???  // может отсутствовать
def validateUser(user: User): Either[String, User] = ???  // может быть невалидным
def saveUser(user: User): Try[User] = ???  // может выбросить исключение

// Плохо - использование одного типа для всех случаев
def findUserBad(id: Long): Try[User] = ???  // избыточно для простого отсутствия
```

## Продвинутые техники обработки ошибок

### **Error Accumulation**

Накопление ошибок позволяет собирать все ошибки, а не останавливаться на первой.

```scala
import cats.data.ValidatedNel
import cats.implicits._

// Валидация с накоплением ошибок
def validateUser(name: String, email: String, age: Int): ValidatedNel[String, User] = {
  (
    if (name.nonEmpty) name.validNel else "Name cannot be empty".invalidNel,
    if (email.contains("@")) email.validNel else "Invalid email".invalidNel,
    if (age >= 0 && age <= 150) age.validNel else "Invalid age".invalidNel
  ).mapN(User.apply)
}

// Использование
val result = validateUser("", "invalid", -1)
// Invalid(NonEmptyList("Name cannot be empty", "Invalid email", "Invalid age"))
```

### **Error Recovery**

Восстановление после ошибок позволяет обрабатывать ошибки и продолжать выполнение.

```scala
import scala.util.Try

// Восстановление с дефолтным значением
def parseNumber(s: String): Int = {
  Try(s.toInt).getOrElse(0)
}

// Восстановление с альтернативной операцией
def fetchData(url: String): Try[String] = {
  Try(fetchFromPrimary(url)).recoverWith {
    case _ => Try(fetchFromSecondary(url))
  }
}
```

### **Error Transformation**

Трансформация ошибок позволяет преобразовывать ошибки в более понятные формы.

```scala
import cats.data.EitherT
import cats.effect.IO

// Трансформация ошибок
def processData(data: String): EitherT[IO, String, Int] = {
  EitherT(IO {
    Try(data.toInt).toEither.left.map(_.getMessage)
  })
}

// Комбинирование с трансформацией
val result = for {
  a <- processData("5")
  b <- processData("3")
} yield a + b
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Дополнительные техники обработки ошибок

### Комбинирование **Option**, **Either** и **Try**

Комбинирование различных типов обработки ошибок позволяет создавать гибкие решения.

```scala
import scala.util.{Try, Success, Failure}

// Комбинирование Option и Try
def parseAndValidate(input: String): Try[Option[Int]] = {
  Try {
    val parsed = input.toInt
    if (parsed > 0) Some(parsed) else None
  }
}

// Комбинирование Either и Try
def processData(data: String): Either[String, Try[Int]] = {
  if (data.isEmpty) Left("Empty data")
  else Right(Try(data.toInt))
}
```

### Накопление ошибок с **Validated**

**Validated** позволяет накапливать ошибки вместо остановки при первой ошибке.

```scala
import cats.data.Validated
import cats.implicits._

// Накопление ошибок валидации
def validateUser(name: String, age: Int): Validated[List[String], User] = {
  val nameValidation = if (name.length >= 3) name.valid else List("Name too short").invalid
  val ageValidation = if (age >= 0 && age <= 120) age.valid else List("Invalid age").invalid

  (nameValidation, ageValidation).mapN(User.apply)
}
```

### Восстановление после ошибок

Восстановление после ошибок позволяет создавать устойчивые системы.

```scala
import scala.util.{Try, Success, Failure}

// Восстановление с использованием recover
val result = Try {
  riskyOperation()
}.recover {
  case e: IllegalArgumentException => defaultValue
  case e: NullPointerException => alternativeValue
}

// Восстановление с использованием recoverWith
val resultWithFallback = Try {
  primaryOperation()
}.recoverWith {
  case e: Exception => Try(fallbackOperation())
}
```

### Практические примеры: Композиция обработки ошибок

```scala
import scala.util.{Try, Success, Failure}

def parseNumber(s: String): Either[String, Int] = {
  Try(s.toInt).toEither.left.map(_ => s"Invalid number: $s")
}

def divide(a: Int, b: Int): Either[String, Double] = {
  if (b == 0) Left("Division by zero")
  else Right(a.toDouble / b)
}

def calculate(s1: String, s2: String): Either[String, Double] = {
  for {
    a <- parseNumber(s1)
    b <- parseNumber(s2)
    result <- divide(a, b)
  } yield result
}
```

### Практические примеры: Накопление ошибок с **Validated**

```scala
import cats.data.Validated
import cats.implicits._

case class User(name: String, email: String, age: Int)

def validateName(name: String): Validated[List[String], String] = {
  if (name.nonEmpty && name.length <= 100) Validated.valid(name)
  else Validated.invalid(List("Name must be between 1 and 100 characters"))
}

def validateEmail(email: String): Validated[List[String], String] = {
  if (email.contains("@")) Validated.valid(email)
  else Validated.invalid(List("Invalid email format"))
}

def validateAge(age: Int): Validated[List[String], Int] = {
  if (age >= 0 && age <= 150) Validated.valid(age)
  else Validated.invalid(List("Age must be between 0 and 150"))
}

def createUser(name: String, email: String, age: Int): Validated[List[String], User] = {
  (
    validateName(name),
    validateEmail(email),
    validateAge(age)
  ).mapN(User.apply)
}

// Все ошибки накапливаются
val result = createUser("", "invalid-email", -5)
// Invalid(List("Name must be between 1 and 100 characters",
//              "Invalid email format",
//              "Age must be between 0 and 150"))
```

### Практические примеры: Восстановление после ошибок

```scala
import scala.util.{Try, Success, Failure}

def fetchData(id: Int): Try[String] = {
  // Симуляция операции, которая может завершиться ошибкой
  if (id > 0) Success(s"Data for $id")
  else Failure(new IllegalArgumentException("Invalid ID"))
}

// Восстановление с значением по умолчанию
val result1 = fetchData(-1).recover {
  case _: IllegalArgumentException => "Default data"
}
// Success("Default data")

// Восстановление с другой операцией
val result2 = fetchData(-1).recoverWith {
  case _: IllegalArgumentException => fetchData(1)
}
// Success("Data for 1")
```

**Scala** предоставляет несколько подходов к обработке ошибок, каждый со своими преимуществами. Понимание **Option**, **Either**, **Try**, их методов и операций, комбинирования различных типов, накопления ошибок, восстановления после ошибок, трансформации ошибок, комбинирования **Option**, **Either** и **Try**, накопления ошибок с **Validated**, восстановления после ошибок, композиции обработки ошибок и практических применений позволяет выбирать подходящий подход для конкретных задач и создавать безопасный и выразительный код. Правильный выбор типа обработки ошибок, комбинирование различных типов обработки ошибок, использование **Validated** для накопления ошибок, использование **recover** и **recoverWith** для восстановления после ошибок, и композиция обработки ошибок критичны для создания надежных приложений. Обработка ошибок особенно важна для создания устойчивых систем, которые могут корректно обрабатывать различные сценарии ошибок, продолжать работу в случае проблем, накапливать ошибки для комплексной валидации, и восстанавливаться после сбоев.

### Практические примеры: Комбинирование **Option**, **Either** и **Try**

```scala
import scala.util.{Try, Success, Failure}

// Конвертация между типами обработки ошибок
def optionToEither[A](option: Option[A], error: String): Either[String, A] = {
  option.toRight(error)
}

def eitherToTry[A](either: Either[String, A]): Try[A] = {
  either.fold(
    error => Failure(new Exception(error)),
    value => Success(value)
  )
}

def tryToOption[A](tryValue: Try[A]): Option[A] = {
  tryValue.toOption
}

// Использование
val option = Some(42)
val either = optionToEither(option, "No value")
val tryValue = eitherToTry(either)
val backToOption = tryToOption(tryValue)
```

### Практические примеры: Обработка ошибок в цепочках операций

```scala
// Обработка ошибок в цепочке операций
def parseAndProcess(input: String): Either[String, Int] = {
  for {
    number <- parseNumber(input)
    validated <- validateNumber(number)
    processed <- processNumber(validated)
  } yield processed
}

def parseNumber(s: String): Either[String, Int] = {
  try Right(s.toInt)
  catch {
    case _: NumberFormatException => Left(s"Invalid number: $s")
  }
}

def validateNumber(n: Int): Either[String, Int] = {
  if (n > 0) Right(n)
  else Left("Number must be positive")
}

def processNumber(n: Int): Either[String, Int] = {
  if (n < 1000) Right(n * 2)
  else Left("Number too large")
}
```

### Использование с различными техниками для композиции обработки ошибок

```scala
import cats.MonadError
import cats.syntax.all._

// Композиция обработки ошибок с MonadError
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

### Использование с различными техниками для трансформации ошибок

```scala
import scala.util.{Try, Success, Failure}

// Трансформация ошибок
def processWithErrorTransform(input: String): Try[Int] = {
  Try(input.toInt)
    .recoverWith {
      case _: NumberFormatException =>
        Failure(new IllegalArgumentException("Invalid number format"))
    }
    .map(_ * 2)
}

// Использование
val result1 = processWithErrorTransform("42")  // Success(84)
val result2 = processWithErrorTransform("abc")  // Failure(IllegalArgumentException)
```

## Дополнительные ресурсы

**Для дальнейшего изучения обработки ошибок в **Scala** рекомендуется:**

- [Scala Option Documentation](https://www.scala-lang.org/api/current/scala/Option.html)
- [Scala Either Documentation](https://www.scala-lang.org/api/current/scala/util/Either.html)
- [Scala Try Documentation](https://www.scala-lang.org/api/current/scala/util/Try.html)
- [Functional Error Handling in Scala](https://www.scala-lang.org/api/current/scala/util/Either.html)
