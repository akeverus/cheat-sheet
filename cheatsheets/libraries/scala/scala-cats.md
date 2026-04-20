---
title: "Cats"
description: "Cats - это библиотека для функционального программирования в Scala. Предоставляет type classes, data types и функции для функционального программирования в Scala. Является частью Typelevel экосистемы."
tags:
  - libraries
  - scala
  - scala-cats
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Cats

**Cats** — это библиотека для функционального программирования в **Scala**. Предоставляет **type classes**, **data types** и функции для функционального программирования в **Scala**. Является частью **Typelevel** экосистемы.

## Полезные ссылки
- [Официальная документация Cats](https://typelevel.org/cats/)
- [Cats GitHub](https://github.com/typelevel/cats)
- [Cats Type Classes](https://typelevel.org/cats/typeclasses.html)
- [Cats Effect](https://typelevel.org/cats-effect/)

## Содержание

- [Основные возможности](#основные-возможности)
  - [Type Classes](#type-classes)
    - [Functor](#functor)
    - [Applicative](#applicative)
    - [Monad](#monad)
  - [Data Types](#data-types)
    - [Option](#option)
    - [Either](#either)
    - [Validated](#validated)
    - [State Monad](#state-monad)
  - [Type Classes в Cats](#type-classes-в-cats)
    - [Semigroup и Monoid](#semigroup-и-monoid)
    - [Foldable](#foldable)
    - [Traverse](#traverse)
  - [Cats Effects (**IO Monad**)](#cats-effects-io-monad)
    - [IO для управления side effects](#io-для-управления-side-effects)
  - [Optics (**линзы**)](#optics-линзы)
    - [Lens для работы с вложенными структурами](#lens-для-работы-с-вложенными-структурами)
- [Integration с Scala](#integration-с-scala)
  - [Cats with Scala Collections](#cats-with-scala-collections)
  - [Cats with Scala Futures](#cats-with-scala-futures)
  - [Cats with Akka](#cats-with-akka)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration с Cats](#configuration-с-cats)
- [Тестирование](#тестирование)
  - [Тестирование с Cats](#тестирование-с-cats)
  - [Property-based Testing с Cats](#property-based-testing-с-cats)
- [Лучшие практики](#лучшие-практики)
  - [Railway Oriented Programming](#railway-oriented-programming)
  - [Error Accumulation](#error-accumulation)
  - [Resource Management](#resource-management)
  - [Performance Optimization](#performance-optimization)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging Cats Code](#debugging-cats-code)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Scala Standard Library](#from-scala-standard-library)
  - [From Scalaz](#from-scalaz)
  - [From Cats 1.x to 2.x](#from-cats-1x-to-2x)
- [Экспериментальные возможности](#экспериментальные-возможности)
  - [Cats Effect 3.x](#cats-effect-3x)
  - [Cats Collections](#cats-collections)
- [См. также](#см-также)

## Основные возможности

### **Type Classes**

#### **Functor**

**Type class Functor** для **Option** и **List** — **map** внутри контекста (**Scala**).

```scala
import cats.Functor
import cats.implicits._

/
 * Functor - type class для типов с функцией map
 * Позволяет применять функцию к значению внутри контекста (контейнера)
 * Законы Functor: identity (map(x)(identity) == x) и composition
 */
// Functor позволяет применять функцию к значению внутри контекста
val option = Some(42)  // Option[Int] - контекст с значением 42

// Использование Functor type class напрямую
// Functor[Option] - instance Functor для Option
// map() - применяет функцию к значению внутри Option
val mapped = Functor[Option].map(option)(_ * 2)  // Some(84)
// Функция _ * 2 применяется к 42, результат обернут в Some

// Синтаксис с extension methods (через cats.implicits._)
// Автоматически добавляет метод map() для типов с Functor instance
val result = option.map(_ * 2)  // Some(84)
// Более идиоматичный способ в Scala

// Functor для List
// List также имеет Functor instance
val list = List(1, 2, 3)
val doubled = list.map(_ * 2)  // List(2, 4, 6)
// map применяется к каждому элементу списка

// Functor для Either
// Either[E, A] имеет Functor instance (map применяется к Right значению)
val either: Either[String, Int] = Right(42)
val mappedEither = either.map(_ * 2)  // Right(84)
// Если either = Left("error"), map не применяется, остается Left("error")
```

#### **Applicative**
```scala
import cats.Applicative
import cats.implicits._

/
 * Applicative - расширяет Functor, позволяет применять функцию в контексте
 * Полезен для независимых вычислений и валидации с накоплением ошибок
 */
// Applicative позволяет применять функцию в контексте к значениям в контексте
val f: Option[Int => Int] = Some(_ * 2)  // Функция обернута в Option
val value: Option[Int] = Some(21)  // Значение обернуто в Option

// ap() - применяет функцию из контекста к значению в контексте
val result = Applicative[Option].ap(f)(value)  // Some(42)
// Если f = None или value = None, результат будет None

// Синтаксис с mapN для комбинирования нескольких значений
// mapN применяет функцию к N значениям в контексте независимо
val combined = (Option(1), Option(2), Option(3)).mapN(_ + _ + _)  // Some(6)
// Если любое значение None, результат None
// Все значения обрабатываются независимо (в отличие от Monad)

// Applicative для валидации с накоплением ошибок
// Either[List[String], A] - Left содержит список ошибок, Right - успешное значение
case class User(name: String, age: Int, email: String)

def validateName(name: String): Either[List[String], String] =
  if (name.nonEmpty) Right(name) else Left(List("Name cannot be empty"))
  // Возвращает Right с именем или Left со списком ошибок

def validateAge(age: Int): Either[List[String], Int] =
  if (age >= 18) Right(age) else Left(List("Age must be at least 18"))

def validateEmail(email: String): Either[List[String], String] =
  if (email.contains("@")) Right(email) else Left(List("Invalid email"))

def validateUser(name: String, age: Int, email: String): Either[List[String], User] = {
  // mapN комбинирует результаты валидации
  // Если все валидации успешны - создается User
  // Если есть ошибки - накапливаются все ошибки в Left
  (validateName(name), validateAge(age), validateEmail(email)).mapN(User)
  // mapN применяет конструктор User к трем Either значениям
  // Если все Right - результат Right(User(...))
  // Если есть Left - все ошибки объединяются в один Left
}

val user = validateUser("John", 25, "john@example.com")
// Right(User(John,25,john@example.com)) - все валидации успешны

val invalidUser = validateUser("", 15, "invalid")
// Left(List(Name cannot be empty, Age must be at least 18, Invalid email))
// Все три ошибки накоплены в одном Left (преимущество Applicative над Monad)
```

#### **Monad**
```scala
import cats.Monad
import cats.implicits._

/
 * Monad - расширяет Applicative, добавляет flatMap
 * Позволяет комбинировать вычисления последовательно (зависимые вычисления)
 * Законы: left identity, right identity, associativity
 */
// Monad комбинирует applicative и flatMap
val optionMonad = Monad[Option]  // Instance Monad для Option

// For-comprehension для последовательных вычислений
// Каждое вычисление зависит от предыдущего (в отличие от Applicative)
val result = for {
  x <- Some(10)  // Извлечение значения из Option
  y <- Some(20)  // Если x = None, вычисление останавливается
  z <- Some(30)  // Если y = None, вычисление останавливается
} yield x + y + z  // Some(60) - все значения успешно извлечены
// Если любое значение None, результат None

// FlatMap для Option - разворачивание вложенных контекстов
val nested: Option[Option[Int]] = Some(Some(42))
val flattened = nested.flatten  // Some(42)
// flatten эквивалентен flatMap(identity)

// Monad для Either - обработка ошибок
def divide(a: Int, b: Int): Either[String, Int] =
  if (b == 0) Left("Division by zero") else Right(a / b)
  // Either[String, Int] - Left для ошибки, Right для успеха

val computation = for {
  x <- divide(20, 2)  // Right(10) - успешное деление
  y <- divide(x, 2)   // Right(5) - деление результата предыдущего шага
  z <- divide(y, 0)   // Left("Division by zero") - ошибка
} yield z  // Left("Division by zero")
// При первой ошибке вычисление останавливается, возвращается Left
// В отличие от Applicative, ошибки не накапливаются

// Monad для List - генерация декартова произведения
val combinations = for {
  x <- List(1, 2)  // Для каждого элемента первого списка
  y <- List('a', 'b')  // Для каждого элемента второго списка
} yield s"$x$y"  // List("1a", "1b", "2a", "2b")
// Генерируются все комбинации элементов из двух списков
// Эквивалентно: List(1, 2).flatMap(x => List('a', 'b').map(y => s"$x$y"))
```

### **Data Types**

#### **Option**
```scala
import cats.implicits._

// Option в Cats - улучшенная версия Scala Option
val someValue: Option[Int] = Some(42)
val noValue: Option[Int] = None

// FlatMap
val result = someValue.flatMap(x => Some(x * 2)) // Some(84)
val noneResult = noValue.flatMap(x => Some(x * 2)) // None

// Fold
val folded = someValue.fold("default")(_.toString) // "42"
val noneFolded = noValue.fold("default")(_.toString) // "default"

// GetOrElse с эффектом
val withEffect = noValue.getOrElse { println("Computing default"); 0 }

// Filter
val filtered = someValue.filter(_ > 40) // Some(42)
val notFiltered = someValue.filter(_ < 40) // None

// Traverse (преобразование Option в другие контексты)
import cats.Traverse

val listOfOptions = List(Some(1), Some(2), None, Some(4))
val sequenced = Traverse[List].sequence(listOfOptions) // None (если хоть один None)

val allSome = List(Some(1), Some(2), Some(3))
val sequencedAll = Traverse[List].sequence(allSome) // Some(List(1, 2, 3))
```

#### **Either**
```scala
import cats.implicits._

// Either для обработки ошибок
def parseInt(s: String): Either[String, Int] = {
  try {
    Right(s.toInt)
  } catch {
    case _: NumberFormatException => Left(s"'$s' is not a valid integer")
  }
}

val valid = parseInt("42") // Right(42)
val invalid = parseInt("not-a-number") // Left("'not-a-number' is not a valid integer")

// Map и flatMap
val doubled = valid.map(_ * 2) // Right(84)
val errorDoubled = invalid.map(_ * 2) // Left("'not-a-number' is not a valid integer")

// FlatMap
val chained = valid.flatMap(x => if (x > 0) Right(x * 2) else Left("Must be positive"))
// Right(84)

// Pattern matching
valid match {
  case Right(value) => println(s"Success: $value")
  case Left(error) => println(s"Error: $error")
}

// Swap (меняет местами Left и Right)
val swapped = valid.swap // Left(42)

// Ensure (проверка условия)
val ensured = valid.ensure("Must be even")(_ % 2 == 0) // проверка четности
```

#### **Validated**
```scala
import cats.data.Validated
import cats.data.Validated.{Valid, Invalid}

// Validated для накопления ошибок (в отличие от Either, который останавливается на первой ошибке)
case class User(name: String, age: Int, email: String)

def validateName(name: String): Validated[List[String], String] =
  if (name.nonEmpty) Valid(name) else Invalid(List("Name cannot be empty"))

def validateAge(age: Int): Validated[List[String], Int] =
  if (age >= 18 && age <= 120) Valid(age) else Invalid(List("Age must be between 18 and 120"))

def validateEmail(email: String): Validated[List[String], String] =
  if (email.contains("@") && email.split("@")(1).contains("."))
    Valid(email)
  else
    Invalid(List("Invalid email format"))

def validateUser(name: String, age: Int, email: String): Validated[List[String], User] = {
  (validateName(name), validateAge(age), validateEmail(email)).mapN(User)
}

val validUser = validateUser("John", 25, "john@example.com")
// Valid(User(John,25,john@example.com))

val invalidUser = validateUser("", 15, "invalid")
// Invalid(List(Name cannot be empty, Age must be between 18 and 120, Invalid email format))

// Преобразование в Either
val asEither = invalidUser.toEither // Left(List(...))

// Fold
val result = validUser.fold(
  errors => s"Validation failed: ${errors.mkString(", ")}",
  user => s"User created: $user"
)
```

#### **State Monad**
```scala
import cats.data.State

// State для работы с изменяемым состоянием в функциональном стиле
case class Calculator(value: Int = 0) {
  def add(x: Int): Calculator = copy(value = value + x)
  def multiply(x: Int): Calculator = copy(value = value * x)
  def reset: Calculator = copy(value = 0)
}

// State[S, A] - вычисление, которое производит A и изменяет состояние S
type CalcState[A] = State[Calculator, A]

// Функции, работающие с состоянием
def add(x: Int): CalcState[Int] = State { calc =>
  val newCalc = calc.add(x)
  (newCalc, newCalc.value)
}

def multiply(x: Int): CalcState[Int] = State { calc =>
  val newCalc = calc.multiply(x)
  (newCalc, newCalc.value)
}

def reset: CalcState[Unit] = State { calc =>
  (calc.reset, ())
}

// Комбинирование операций
val program = for {
  _ <- add(10)       // calculator = 10, result = 10
  _ <- multiply(2)   // calculator = 20, result = 20
  _ <- add(5)        // calculator = 25, result = 25
  finalValue <- add(3) // calculator = 28, result = 28
} yield finalValue

val initialCalc = Calculator(0)
val (finalCalc, result) = program.run(initialCalc).value

println(s"Final calculator: $finalCalc") // Calculator(28)
println(s"Result: $result") // 28
```

### **Type Classes** в **Cats**

#### **Semigroup** и **Monoid**
```scala
import cats.Semigroup
import cats.Monoid
import cats.implicits._

// Semigroup - ассоциативная бинарная операция
val intSemigroup = Semigroup[Int]
val combined = intSemigroup.combine(1, 2) // 3

// Для List
val listSemigroup = Semigroup[List[Int]]
val combinedLists = listSemigroup.combine(List(1, 2), List(3, 4)) // List(1, 2, 3, 4)

// Monoid расширяет Semigroup пустым элементом
val intMonoid = Monoid[Int]
val identity = intMonoid.empty // 0
val combinedWithIdentity = intMonoid.combine(5, identity) // 5

// Monoid для String
val stringMonoid = Monoid[String]
val emptyString = stringMonoid.empty // ""
val combinedStrings = stringMonoid.combine("Hello, ", "World!") // "Hello, World!"

// Сложение всех элементов коллекции
val numbers = List(1, 2, 3, 4, 5)
val sum = numbers.foldLeft(intMonoid.empty)(intMonoid.combine) // 15

// CombineAll
val total = Monoid[Int].combineAll(numbers) // 15
```

#### **Foldable**
```scala
import cats.Foldable
import cats.implicits._

// Foldable позволяет сворачивать структуры данных
val listFoldable = Foldable[List]

val numbers = List(1, 2, 3, 4, 5)

// FoldLeft
val sum = listFoldable.foldLeft(numbers, 0)(_ + _) // 15

// FoldRight (стекобезопасная)
val concatenated = listFoldable.foldRight(numbers, Eval.now("0")) { (num, acc) =>
  acc.map(s => s"$num-$s")
}.value // "1-2-3-4-5-0"

// FoldMap
val stringSum = listFoldable.foldMap(numbers)(_.toString)(Monoid[String]) // "12345"

// Find
val found = listFoldable.find(numbers)(_ > 3) // Some(4)

// Exists и Forall
val existsEven = listFoldable.exists(numbers)(_ % 2 == 0) // true
val allPositive = listFoldable.forall(numbers)(_ > 0) // true

// Collect (фильтр + мап)
val evens = listFoldable.collect(numbers) {
  case x if x % 2 == 0 => x * 2
} // List(4, 8)

// Count
val countEvens = listFoldable.count(numbers)(_ % 2 == 0) // 2
```

#### **Traverse**
```scala
import cats.Traverse
import cats.implicits._

// Traverse позволяет преобразовывать структуры данных, содержащие эффекты
val listTraverse = Traverse[List]

// Sequence - преобразование List[Option[A]] в Option[List[A]]
val listOfOptions = List(Some(1), Some(2), Some(3))
val sequenced = listTraverse.sequence(listOfOptions) // Some(List(1, 2, 3))

val withNone = List(Some(1), None, Some(3))
val sequencedWithNone = listTraverse.sequence(withNone) // None

// Traverse - map + sequence
def parseInt(s: String): Option[Int] = s.toIntOption

val strings = List("1", "2", "3", "4")
val parsed = listTraverse.traverse(strings)(parseInt) // Some(List(1, 2, 3, 4))

val invalidStrings = List("1", "not-a-number", "3")
val parsedInvalid = listTraverse.traverse(invalidStrings)(parseInt) // None

// Traverse для Future
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def asyncParseInt(s: String): Future[Int] = Future(s.toInt)

val futureResult = listTraverse.traverse(strings)(asyncParseInt)
// Future[List[Int]]
```

### **Cats Effects** (**IO Monad**)

#### `IO` для управления **side effects**
```scala
import cats.effect.IO
import cats.effect.unsafe.implicits.global

// Создание IO операций
val hello: IO[String] = IO.pure("Hello, World!")
val delayed: IO[String] = IO.delay {
  Thread.sleep(1000)
  "Delayed result"
}

// Выполнение
val result = hello.unsafeRunSync() // "Hello, World!"

// FlatMap
val combined = for {
  greeting <- hello
  message <- IO.pure(s"$greeting How are you?")
} yield message

// Async операции
val asyncOperation = IO.async_[String] { callback =>
  // Имитация асинхронной операции
  Thread {
    Thread.sleep(100)
    callback(Right("Async result"))
  }.start()
}

// Resource management
val resource: IO[String] = IO {
  println("Acquiring resource")
  "resource data"
}.bracket { data =>
  IO {
    println("Using resource: $data")
    s"Processed: $data"
  }
} { _ =>
  IO.println("Releasing resource")
}

// Fiber (легковесные потоки)
val fiberProgram = for {
  fiber <- (IO.sleep(1.second) *> IO.println("Task 1 completed")).start
  _ <- IO.println("Task 2 running")
  _ <- fiber.join
} yield ()

fiberProgram.unsafeRunSync()
```

### **Optics** (**линзы**)

#### **Lens** для работы с вложенными структурами
```scala
import monocle.Lens
import monocle.macros.GenLens

case class Address(street: String, city: String, country: String)
case class Person(name: String, age: Int, address: Address)

// Создание линз
val personName = GenLens[Person](_.name)
val personAge = GenLens[Person](_.age)
val personAddress = GenLens[Person](_.address)
val addressCity = GenLens[Address](_.city)

// Композиция линз
val personCity = personAddress composeLens addressCity

val person = Person("John", 30, Address("123 Main St", "NYC", "USA"))

// Получение значений
val name = personName.get(person) // "John"
val city = personCity.get(person) // "NYC"

// Модификация
val updatedPerson = personName.set("Jane")(person)
val updatedCity = personCity.set("LA")(person)

// Модификация с функцией
val upperCaseName = personName.modify(_.toUpperCase)(person)
val nextYear = personAge.modify(_ + 1)(person)

// Compose с другими операциями
val complexUpdate = (personName.set("Bob") andThen personAge.modify(_ + 5))(person)
```

## **Integration** с **Scala**

### **Cats with Scala Collections**
```scala
import cats.implicits._

// Расширение стандартных коллекций Cats методами
val numbers = List(1, 2, 3, 4, 5)

// Traverse
val traversed = numbers.traverse { x =>
  if (x > 0) Some(x * 2) else None
} // Some(List(2, 4, 6, 8, 10))

// Sequence
val listOfOptions = List(Some(1), Some(2), Some(3))
val sequenced = listOfOptions.sequence // Some(List(1, 2, 3))

// Filter
val filtered = numbers.filterM { x =>
  List(true, x % 2 == 0) // nondeterministic filter
}

// FlatMap с эффектами
val withEffects = for {
  x <- List(1, 2, 3)
  y <- List('a', 'b')
} yield s"$x$y" // List("1a", "1b", "2a", "2b", "3a", "3b")
```

### **Cats with Scala Futures**
```scala
import cats.implicits._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Traverse для Future
val futures = List(
  Future(1),
  Future(2),
  Future(3)
)

val sequencedFutures = futures.sequence // Future[List[Int]]

// FlatMap с Futures
val combined = for {
  x <- Future(10)
  y <- Future(20)
} yield x + y // Future[Int]

// Error handling
val withErrorHandling = Future(1 / 0).attempt // Future[Either[Throwable, Int]]
```

### **Cats with Akka**
```scala
import cats.implicits._
import akka.actor.{Actor, ActorSystem, Props}

// Актор с Cats
class CalculatorActor extends Actor {
  def receive = {
    case Add(x, y) =>
      val result = (Option(x), Option(y)).mapN(_ + _)
      sender() ! result.getOrElse(0)

    case Multiply(x, y) =>
      val result = for {
        a <- Option(x) if a > 0
        b <- Option(y) if b > 0
      } yield a * b
      sender() ! result.getOrElse(0)
  }
}

case class Add(x: Int, y: Int)
case class Multiply(x: Int, y: Int)
```

## **Spring Boot Integration**

### **Configuration** с **Cats**
```scala
@Configuration
class CatsConfig {

  // Cats type classes как Spring beans
  @Bean
  def optionMonad: Monad[Option] = Monad[Option]

  @Bean
  def listTraverse: Traverse[List] = Traverse[List]

  @Bean
  def stringMonoid: Monoid[String] = Monoid[String]
}

// Service с Cats
@Service
class UserService @Autowired()(
  userRepository: UserRepository,
  validationService: ValidationService
) {

  def createUser(request: CreateUserRequest): Either[List[String], User] = {
    for {
      validatedName <- validationService.validateName(request.name)
      validatedEmail <- validationService.validateEmail(request.email)
      validatedAge <- validationService.validateAge(request.age)
      user = User(validatedName, validatedEmail, validatedAge)
      savedUser <- userRepository.save(user).toEither
    } yield savedUser
  }

  def getUsersByIds(ids: List[Long]): Future[List[User]] = {
    ids.traverse(userRepository.findById).map(_.flatten)
  }
}

// Controller с Cats
@RestController
@RequestMapping(Array("/api/users"))
class UserController @Autowired()(
  userService: UserService
) {

  @PostMapping
  def createUser(@RequestBody request: CreateUserRequest): ResponseEntity[_] = {
    userService.createUser(request) match {
      case Right(user) =>
        ResponseEntity.ok(user)
      case Left(errors) =>
        ResponseEntity.badRequest().body(Map("errors" -> errors))
    }
  }

  @GetMapping(Array("/batch"))
  def getUsersByIds(@RequestParam ids: java.util.List[Long]): CompletableFuture[List[User]] = {
    import scala.jdk.CollectionConverters._
    val scalaIds = ids.asScala.toList.map(_.toLong)

    userService.getUsersByIds(scalaIds).toCompletableFuture
  }
}
```

## Тестирование

### Тестирование с **Cats**
```scala
import cats.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserServiceSpec extends AnyFlatSpec with Matchers {

  "UserService" should "create user successfully" in {
    val mockRepository = mock[UserRepository]
    val mockValidation = mock[ValidationService]
    val userService = new UserService(mockRepository, mockValidation)

    // Given
    val request = CreateUserRequest("John", "john@example.com", 25)
    val expectedUser = User("John", "john@example.com", 25)

    when(mockValidation.validateName("John")).thenReturn(Right("John"))
    when(mockValidation.validateEmail("john@example.com")).thenReturn(Right("john@example.com"))
    when(mockValidation.validateAge(25)).thenReturn(Right(25))
    when(mockRepository.save(expectedUser)).thenReturn(IO.pure(expectedUser))

    // When
    val result = userService.createUser(request).unsafeRunSync()

    // Then
    result shouldBe Right(expectedUser)
  }

  it should "accumulate validation errors" in {
    val mockRepository = mock[UserRepository]
    val mockValidation = mock[ValidationService]
    val userService = new UserService(mockRepository, mockValidation)

    // Given
    val request = CreateUserRequest("", "invalid", 15)

    when(mockValidation.validateName("")).thenReturn(Left(List("Name is required")))
    when(mockValidation.validateEmail("invalid")).thenReturn(Left(List("Invalid email")))
    when(mockValidation.validateAge(15)).thenReturn(Left(List("Age must be at least 18")))

    // When
    val result = userService.createUser(request).unsafeRunSync()

    // Then
    result shouldBe Left(List(
      "Name is required",
      "Invalid email",
      "Age must be at least 18"
    ))
  }

  it should "handle repository errors" in {
    val mockRepository = mock[UserRepository]
    val mockValidation = mock[ValidationService]
    val userService = new UserService(mockRepository, mockValidation)

    // Given
    val request = CreateUserRequest("John", "john@example.com", 25)
    val user = User("John", "john@example.com", 25)

    when(mockValidation.validateName("John")).thenReturn(Right("John"))
    when(mockValidation.validateEmail("john@example.com")).thenReturn(Right("john@example.com"))
    when(mockValidation.validateAge(25)).thenReturn(Right(25))
    when(mockRepository.save(user)).thenReturn(IO.raiseError(new RuntimeException("DB error")))

    // When & Then
    assertThrows[RuntimeException] {
      userService.createUser(request).unsafeRunSync()
    }
  }
}
```

### **Property-based Testing** с **Cats**
```scala
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalacheck.Gen

class CatsPropertiesSpec extends AnyFlatSpec with ScalaCheckDrivenPropertyChecks with Matchers {

  "Option Monad" should "satisfy left identity law" in {
    forAll { (x: Int, f: Int => Option[Int]) =>
      val left = Monad[Option].flatMap(Monad[Option].pure(x))(f)
      val right = f(x)
      left shouldBe right
    }
  }

  it should "satisfy right identity law" in {
    forAll { (fa: Option[Int]) =>
      val left = Monad[Option].flatMap(fa)(Monad[Option].pure)
      val right = fa
      left shouldBe right
    }
  }

  "List Traverse" should "sequence successfully for all Some" in {
    forAll(Gen.listOf(Gen.posNum[Int])) { numbers =>
      val options = numbers.map(Some(_))
      val sequenced = Traverse[List].sequence(options)

      sequenced shouldBe Some(numbers)
    }
  }

  it should "return None if any element is None" in {
    forAll(Gen.listOfN(3, Gen.option(Gen.posNum[Int]))) { options =>
      whenever(options.contains(None)) {
        val sequenced = Traverse[List].sequence(options)
        sequenced shouldBe None
      }
    }
  }
}
```

## Лучшие практики

### **Railway Oriented Programming**
```scala
// Использование Either для обработки ошибок в цепочке операций
sealed trait DomainError
case class ValidationError(message: String) extends DomainError
case class NotFoundError(resource: String) extends DomainError
case class DatabaseError(cause: Throwable) extends DomainError

case class User(id: Long, name: String, email: String)

def validateUser(user: User): Either[DomainError, User] = {
  if (user.name.isEmpty) Left(ValidationError("Name cannot be empty"))
  else if (!user.email.contains("@")) Left(ValidationError("Invalid email"))
  else Right(user)
}

def findUser(id: Long): Either[DomainError, User] = {
  // Имитация поиска в БД
  if (id > 0) Right(User(id, "John", "john@example.com"))
  else Left(NotFoundError("User"))
}

def saveUser(user: User): Either[DomainError, User] = {
  try {
    // Имитация сохранения
    Right(user.copy(id = 123))
  } catch {
    case e: Exception => Left(DatabaseError(e))
  }
}

def processUser(id: Long): Either[DomainError, User] = {
  for {
    existingUser <- findUser(id)
    validatedUser <- validateUser(existingUser)
    savedUser <- saveUser(validatedUser)
  } yield savedUser
}

// Использование
val result = processUser(1)
result match {
  case Right(user) => println(s"Success: $user")
  case Left(error) => println(s"Error: $error")
}
```

### **Error Accumulation**
```scala
// Использование Validated для накопления ошибок
case class ValidationError(field: String, message: String)

def validateName(name: String): ValidatedNel[ValidationError, String] =
  if (name.nonEmpty) name.validNel
  else ValidationError("name", "cannot be empty").invalidNel

def validateEmail(email: String): ValidatedNel[ValidationError, String] =
  if (email.contains("@")) email.validNel
  else ValidationError("email", "invalid format").invalidNel

def validateAge(age: Int): ValidatedNel[ValidationError, String] =
  if (age >= 18) age.toString.validNel
  else ValidationError("age", "must be at least 18").invalidNel

def validateUser(name: String, email: String, age: Int):
    ValidatedNel[ValidationError, User] = {

  (validateName(name), validateEmail(email), validateAge(age)).mapN { (n, e, a) =>
    User(0, n, e) // age преобразуется в строку для демонстрации
  }
}

// Все ошибки накапливаются
val invalidUser = validateUser("", "invalid", 15)
// Invalid(NonEmptyList(
//   ValidationError(name,cannot be empty),
//   ValidationError(email,invalid format),
//   ValidationError(age,must be at least 18)
// ))
```

### **Resource Management**
```scala
import cats.effect.{Resource, IO}
import java.sql.Connection

// Resource для управления подключениями к БД
def databaseConnection: Resource[IO, Connection] = {
  val acquire = IO {
    // Создание подключения
    println("Acquiring DB connection")
    // createConnection()
    null // заглушка
  }

  val release = (conn: Connection) => IO {
    // Закрытие подключения
    println("Releasing DB connection")
    // conn.close()
  }

  Resource.make(acquire)(release)
}

// Использование ресурса
val program = databaseConnection.use { conn =>
  for {
    user <- IO { /* query user using conn */ User(1, "John", "john@example.com") }
    _ <- IO.println(s"Found user: $user")
  } yield user
}

// Автоматическое управление ресурсами
program.unsafeRunSync()
```

### **Performance Optimization**
```scala
// Оптимизация с Eval для ленивых вычислений
import cats.Eval

def fibonacci(n: Int): Eval[BigInt] = {
  if (n <= 1) Eval.now(n)
  else for {
    a <- Eval.defer(fibonacci(n - 1))
    b <- Eval.defer(fibonacci(n - 2))
  } yield a + b
}

// Мемоизированные вычисления
val memoizedFib = fibonacci(100).memoize

// Ленивая инициализация
class ExpensiveService {
  lazy val config: Eval[Config] = Eval.later {
    println("Loading configuration...")
    Thread.sleep(1000) // Имитация загрузки
    Config("loaded")
  }
}

// Writer Monad для логирования
import cats.data.Writer

def loggedOperation(x: Int): Writer[List[String], Int] = {
  for {
    _ <- Writer.tell(List(s"Processing $x"))
    result = x * 2
    _ <- Writer.tell(List(s"Result: $result"))
  } yield result
}

val (log, result) = loggedOperation(21).run
// log: List("Processing 21", "Result: 42")
// result: 42
```

## Устранение неполадок

### **Common Issues**
```scala
object CatsTroubleshooting {

  // Проблема: Stack overflow в рекурсии
  // Решение: Использовать Eval для stack-safe рекурсии
  def safeFactorial(n: Long): Eval[BigInt] = {
    if (n <= 1) Eval.now(1)
    else Eval.defer(safeFactorial(n - 1).map(_ * n))
  }

  // Проблема: Неявные параметры не находятся
  // Решение: Импортировать cats.implicits._
  import cats.implicits._

  val result = List(1, 2, 3).map(_ * 2) // Работает благодаря implicits

  // Проблема: Type class instance не найдена
  // Решение: Добавить явный импорт или создать instance
  implicit val customMonoid: Monoid[CustomType] = new Monoid[CustomType] {
    def empty: CustomType = CustomType.default
    def combine(x: CustomType, y: CustomType): CustomType = x.merge(y)
  }

  // Проблема: Either не short-circuit при ошибках
  // Решение: Использовать Validated для накопления ошибок
  def validateAll(name: String, age: Int): ValidatedNel[String, User] = {
    (
      if (name.nonEmpty) name.validNel else "Name required".invalidNel,
      if (age >= 18) age.validNel else "Age too low".invalidNel
    ).mapN(User(0, _, ""))
  }

  // Проблема: Performance issues с traverse
  // Решение: Использовать parTraverse для параллельной обработки
  import cats.Parallel
  import cats.effect.IO

  def parallelProcessing(items: List[Int]): IO[List[String]] = {
    val parallel = Parallel.parTraverse(items) { item =>
      IO.sleep(100.millis) *> IO.pure(s"processed $item")
    }
    parallel
  }
}
```

### **Debugging Cats Code**
```scala
// Расширение для логирования операций
implicit class DebuggableOps[F[_], A](fa: F[A]) {
  def debug(implicit functor: Functor[F]): F[A] = {
    fa.map { a =>
      println(s"Debug: $a")
      a
    }
  }

  def debug(label: String)(implicit functor: Functor[F]): F[A] = {
    fa.map { a =>
      println(s"$label: $a")
      a
    }
  }
}

// Использование
val result = Option(42)
  .map(_ * 2)
  .debug("After multiplication")
  .filter(_ > 50)
  .debug("After filtering")
// Output:
// After multiplication: 84
// After filtering: 84

// Логирование Either
implicit class EitherDebugOps[A, B](either: Either[A, B]) {
  def debugEither: Either[A, B] = {
    either match {
      case Left(error) =>
        println(s"Error: $error")
        either
      case Right(value) =>
        println(s"Success: $value")
        either
    }
  }
}

val computation = for {
  x <- Right(10).debugEither
  y <- Right(20).debugEither
} yield x + y
// Output:
// Success: 10
// Success: 20
```

## Руководство по миграции

### **From Scala Standard Library**
```scala
// Scala Option
val option: Option[Int] = Some(42)
val mapped = option.map(_ * 2)

// Cats Option (то же самое благодаря implicits)
import cats.implicits._
val catsOption = 42.some
val catsMapped = catsOption.map(_ * 2)

// Scala Either
def divide(a: Int, b: Int): Either[String, Int] = {
  if (b == 0) Left("Division by zero") else Right(a / b)
}

// Cats Either (расширенные возможности)
import cats.implicits._
val result = divide(10, 2).map(_ * 2).getOrElse(0)

// Scala Future
import scala.concurrent.Future
val future = Future(42).map(_ * 2)

// Cats Future (дополнительные операции)
import cats.implicits._
val catsFuture = Future(42).map(_ * 2)
val sequenced = List(Future(1), Future(2)).sequence
```

### **From Scalaz**
```scala
// Scalaz imports
import scalaz._
import Scalaz._

val option = 42.some
val either = 42.right[String]

// Cats equivalents
import cats.implicits._

val catsOption = 42.some
val catsEither = 42.asRight[String]

// Type classes
// Scalaz
implicitly[Monad[Option]].point(42)

// Cats
implicitly[Applicative[Option]].pure(42)
```

### **From Cats** 1.x `to 2`.x
```scala
// Cats 1.x
import cats.implicits._

val result = List(1, 2, 3).traverse(x => Option(x))

// Cats 2.x (изменения в traverse)
val result2 = List(1, 2, 3).traverse(x => Option(x))

// Новые возможности в 2.x
import cats.effect.IO

val ioProgram = IO(42).flatMap(x => IO(x * 2))
// Более строгая типизация и новые эффекты
```

## Экспериментальные возможности

### **Cats Effect** 3.x
```scala
// Cats Effect 3.x особенности
import cats.effect.{IO, Resource}
import scala.concurrent.duration._

// Более безопасные fiber operations
val fiberProgram = for {
  fiber <- (IO.sleep(1.second) *> IO.println("Task completed")).start
  result <- fiber.joinWithNever
} yield result

// BracketCase для более детального управления ресурсами
val resource = Resource.make(IO.println("Acquiring"))(_ =>
  IO.println("Releasing")
).evalMap(_ => IO.pure("resource"))

val withErrorHandling = resource.use { res =>
  IO.raiseError(new RuntimeException("error"))
}.handleErrorWith { error =>
  IO.println(s"Handled error: $error") *> IO.pure("fallback")
}

// Temporal для работы со временем
val timedOperation = IO.realTimeInstant.flatMap { start =>
  IO.sleep(1.second) *> IO.realTimeInstant.map { end =>
    println(s"Duration: ${end.epochSecond - start.epochSecond}s")
  }
}
```

### **Cats Collections**
```scala
// Cats Collections (отдельная библиотека)
import cats.collections._

// Persistent structures
val list = List(1, 2, 3, 4, 5)
val updated = list.updated(2, 42) // O(log n)

// HashMap с guaranteed ordering
val hashMap = HashMap("a" -> 1, "b" -> 2, "c" -> 3)

// Range
val range = Range(1, 100)
val evenNumbers = range.filter(_ % 2 == 0)

// Heap для priority queue operations
val heap = Heap.fromIterable(List(3, 1, 4, 1, 5))
val min = heap.minimum // Some(1)
val withoutMin = heap.remove // Heap without minimum
```
## См. также
- [Обзор библиотек](../) — **Scala** библиотеки
- [Паттерны](../../patterns/README.md) — Функциональные паттерны

