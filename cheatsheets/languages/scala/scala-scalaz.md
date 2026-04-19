---
title: "Scalaz в Scala"
description: "Краткое руководство по Scalaz - библиотека функционального программирования для Scala."
tags:
  - languages
  - scala
  - scala-scalaz
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Scalaz в Scala

Краткое руководство по **Scalaz** - библиотека функционального программирования для **Scala**.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [**Type Classes** в **Scalaz**](#type-classes-в-scalaz)
  - [**Semigroup** и **Monoid**](#semigroup-и-monoid)
  - [**Functor**](#functor)
- [**Monads** и **Functors**](#monads-и-functors)
  - [**Monad**](#monad)
  - [**Applicative**](#applicative)
- [**Validation**](#validation)
  - [Накопление ошибок с **Validation**](#накопление-ошибок-с-validation)
- [**Lens**](#lens)
  - [Использование **Lens**](#использование-lens)
- [Практические примеры](#практические-примеры)
  - [Валидация формы](#валидация-формы)
  - [Работа с эффектами](#работа-с-эффектами)
  - [Практические примеры: **Reader Monad** для **dependency injection**](#практические-примеры-reader-monad-для-dependency-injection)
  - [Практические примеры: **State Monad**](#практические-примеры-state-monad)
  - [Практические примеры: **Kleisli** для композиции функций](#практические-примеры-kleisli-для-композиции-функций)
  - [Работа с **Task** для асинхронных операций](#работа-с-task-для-асинхронных-операций)
  - [Работа с **Either** для обработки ошибок](#работа-с-either-для-обработки-ошибок)
  - [Работа с **Tree** для иерархических структур](#работа-с-tree-для-иерархических-структур)
  - [Работа с **NonEmptyList** для гарантии непустых списков](#работа-с-nonemptylist-для-гарантии-непустых-списков)
  - [Работа с **IList** для иммутабельных списков](#работа-с-ilist-для-иммутабельных-списков)
  - [Работа с **ISet** для иммутабельных множеств](#работа-с-iset-для-иммутабельных-множеств)
  - [Работа с \/ (**Disjunction**) для обработки ошибок](#работа-с-disjunction-для-обработки-ошибок)
  - [Работа с **Maybe** (**Option**) с дополнительными операциями](#работа-с-maybe-option-с-дополнительными-операциями)
  - [Работа с **Const** для **type-level** вычислений](#работа-с-const-для-type-level-вычислений)
  - [Работа с **Free Monad** для **DSL**](#работа-с-free-monad-для-dsl)
  - [Работа с **Coyoneda** для оптимизации](#работа-с-coyoneda-для-оптимизации)
  - [Работа с **Codensity** для оптимизации **Monad** операций](#работа-с-codensity-для-оптимизации-monad-операций)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Работа с **Order** для упорядочивания](#работа-с-order-для-упорядочивания)
  - [Работа с **Equal** для сравнения](#работа-с-equal-для-сравнения)
  - [Работа с **Show** для преобразования в строку](#работа-с-show-для-преобразования-в-строку)
  - [Работа с **Foldable** для операций над коллекциями](#работа-с-foldable-для-операций-над-коллекциями)
  - [Работа с **Traverse** для трансформации коллекций](#работа-с-traverse-для-трансформации-коллекций)
  - [Работа с **Plus** для комбинирования значений](#работа-с-plus-для-комбинирования-значений)
  - [Работа с **ApplicativePlus** для дополнительных операций](#работа-с-applicativeplus-для-дополнительных-операций)
  - [Работа с **MonadPlus** для дополнительных операций](#работа-с-monadplus-для-дополнительных-операций)
  - [Работа с **Arrow** для композиции функций](#работа-с-arrow-для-композиции-функций)
  - [Работа с **Comonad** для операций над структурами](#работа-с-comonad-для-операций-над-структурами)
  - [Работа с **Bifunctor** для работы с двумя типами](#работа-с-bifunctor-для-работы-с-двумя-типами)
  - [Работа с **Profunctor** для работы с функциями](#работа-с-profunctor-для-работы-с-функциями)
  - [Работа с **Contravariant** для контравариантных функторов](#работа-с-contravariant-для-контравариантных-функторов)
  - [Работа с **InvariantFunctor** для инвариантных функторов](#работа-с-invariantfunctor-для-инвариантных-функторов)
  - [Работа с **Zip** для параллельной композиции](#работа-с-zip-для-параллельной-композиции)
  - [Работа с **Unzip** для разделения](#работа-с-unzip-для-разделения)
  - [Работа с **Align** для выравнивания коллекций](#работа-с-align-для-выравнивания-коллекций)
  - [Работа с **Cozip** для разделения **Coproduct**](#работа-с-cozip-для-разделения-coproduct)
  - [Работа с **Cobind** для операций над структурами](#работа-с-cobind-для-операций-над-структурами)
  - [Работа с **Cojoin** для дублирования структуры](#работа-с-cojoin-для-дублирования-структуры)
  - [Работа с **Compose** для композиции](#работа-с-compose-для-композиции)
  - [Работа с **Category** для категорий](#работа-с-category-для-категорий)
  - [Работа с **Strong** для сильных профункторов](#работа-с-strong-для-сильных-профункторов)
  - [Работа с **Choice** для выбора](#работа-с-choice-для-выбора)
  - [Работа с **Split** для разделения](#работа-с-split-для-разделения)
  - [Работа с **Choice** для выбора между функциями](#работа-с-choice-для-выбора-между-функциями)
  - [Работа с **Costar** для контравариантных функторов](#работа-с-costar-для-контравариантных-функторов)
  - [Работа с **Star** для ковариантных функторов](#работа-с-star-для-ковариантных-функторов)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Scalaz** - это библиотека функционального программирования для **Scala**, которая предоставляет множество абстракций и утилит для функционального программирования. **Scalaz** включает **Type Classes**, **Monads**, **Functors**, **Applicative**, **Validation**, **Lens** и множество других абстракций.

**Scalaz** особенно полезен для создания чистого функционального кода, обработки ошибок, работы с эффектами и создания композируемого кода.

## **Type Classes** в **Scalaz**

**Scalaz** предоставляет множество **Type Classes** для различных операций.

### **Semigroup** и **Monoid**

```scala
import scalaz._
import Scalaz._

// Semigroup для комбинирования значений
val intSemigroup: Semigroup[Int] = implicitly[Semigroup[Int]]
val result = intSemigroup.append(1, 2)  // 3

// Monoid расширяет Semigroup с нейтральным элементом
val intMonoid: Monoid[Int] = implicitly[Monoid[Int]]
val empty = intMonoid.zero  // 0
val combined = intMonoid.append(1, 2)  // 3

// Использование оператора
val sum = 1 |+| 2  // 3
val list = List(1, 2, 3) |+| List(4, 5)  // List(1, 2, 3, 4, 5)
```

### **Functor**

```scala
import scalaz._
import Scalaz._

// Functor для применения функции к значению внутри контекста
val optionFunctor: Functor[Option] = implicitly[Functor[Option]]

val option = Some(10)
val mapped = optionFunctor.map(option)(_ * 2)  // Some(20)

// Использование оператора
val result = option.map(_ * 2)  // Some(20)
```

## **Monads** и **Functors**

### **Monad**

```scala
import scalaz._
import Scalaz._

val optionMonad: Monad[Option] = implicitly[Monad[Option]]

// flatMap для композиции
val result = optionMonad.bind(Some(10))(x => Some(x * 2))  // Some(20)

// Использование для композиции
val computation = for {
  x <- Some(10)
  y <- Some(20)
  z <- Some(30)
} yield x + y + z
// Some(60)
```

### **Applicative**

```scala
import scalaz._
import Scalaz._

val optionApplicative: Applicative[Option] = implicitly[Applicative[Option]]

// Применение функции внутри Option к значению в другом Option
val add: Option[Int => Int] = Some(_ + 1)
val value: Option[Int] = Some(10)
val result = optionApplicative.ap(value)(add)  // Some(11)

// Использование операторов
val result2 = (some(_ + 1) <*> some(10))  // Some(11)
```

## **Validation**

**Validation** позволяет накапливать ошибки валидации.

### Накопление ошибок с **Validation**

```scala
import scalaz._
import Scalaz._

case class User(name: String, email: String, age: Int)

def validateName(name: String): ValidationNel[String, String] = {
  if (name.nonEmpty && name.length <= 100) name.successNel
  else "Name must be between 1 and 100 characters".failureNel
}

def validateEmail(email: String): ValidationNel[String, String] = {
  if (email.contains("@")) email.successNel
  else "Invalid email format".failureNel
}

def validateAge(age: Int): ValidationNel[String, Int] = {
  if (age >= 0 && age <= 150) age.successNel
  else "Age must be between 0 and 150".failureNel
}

def createUser(name: String, email: String, age: Int): ValidationNel[String, User] = {
  (validateName(name) |@| validateEmail(email) |@| validateAge(age)) {
    (n, e, a) => User(n, e, a)
  }
}

// Все ошибки накапливаются
val result = createUser("", "invalid-email", -5)
// Failure(NonEmptyList("Name must be between 1 and 100 characters",
//                       "Invalid email format",
//                       "Age must be between 0 and 150"))
```

## **Lens**

**Lens** в **Scalaz** - это функциональные ссылки на поля.

### Использование **Lens**

```scala
import scalaz.Lens

case class User(name: String, age: Int, address: Address)
case class Address(street: String, city: String)

val user = User("Alice", 30, Address("Main St", "NYC"))

// Создание Lens для поля name
val nameLens = Lens.lensId[User] >>= Lens.lensg(
  u => u.name,
  (u, n) => u.copy(name = n)
)

// Чтение значения
val name = nameLens.get(user)  // "Alice"

// Обновление значения
val updated = nameLens.set(user, "Bob")  // User("Bob", 30, ...)

// Композиция Lens
val addressLens = Lens.lensId[User] >>= Lens.lensg(
  u => u.address,
  (u, a) => u.copy(address = a)
)

val cityLens = Lens.lensId[Address] >>= Lens.lensg(
  a => a.city,
  (a, c) => a.copy(city = c)
)

val userCityLens = addressLens >=> cityLens
val city = userCityLens.get(user)  // "NYC"
```

## Практические примеры

### Валидация формы

```scala
import scalaz._
import Scalaz._

case class RegistrationForm(
  username: String,
  email: String,
  password: String,
  confirmPassword: String
)

def validateRegistration(form: RegistrationForm): ValidationNel[String, RegistrationForm] = {
  (
    validateUsername(form.username) |@|
    validateEmail(form.email) |@|
    validatePassword(form.password, form.confirmPassword)
  ) { (_, _, _) => form }
}

def validateUsername(username: String): ValidationNel[String, String] = {
  if (username.length >= 3 && username.length <= 20) username.successNel
  else "Username must be between 3 and 20 characters".failureNel
}

def validateEmail(email: String): ValidationNel[String, String] = {
  if (email.contains("@")) email.successNel
  else "Invalid email format".failureNel
}

def validatePassword(password: String, confirm: String): ValidationNel[String, String] = {
  val validations = List(
    if (password.length >= 8) password.successNel else "Password must be at least 8 characters".failureNel,
    if (password == confirm) password.successNel else "Passwords do not match".failureNel
  )
  validations.reduce(_ <* _)
}
```

### Работа с эффектами

```scala
import scalaz._
import Scalaz._

// Writer Monad для накопления логов
type Logged[A] = Writer[List[String], A]

def add(x: Int, y: Int): Logged[Int] = {
  val result = x + y
  List(s"Adding $x and $y").tell.map(_ => result)
}

def multiply(x: Int, y: Int): Logged[Int] = {
  val result = x * y
  List(s"Multiplying $x and $y").tell.map(_ => result)
}

val computation: Logged[Int] = for {
  sum <- add(10, 20)
  product <- multiply(sum, 2)
} yield product

val (logs, result) = computation.run
// logs: List("Adding 10 and 20", "Multiplying 30 and 2")
// result: 60
```

### Практические примеры: **Reader Monad** для **dependency injection**

```scala
import scalaz._
import Scalaz._

case class Config(host: String, port: Int, timeout: Int)

type ConfigReader[A] = Reader[Config, A]

val getHost: ConfigReader[String] = Reader(_.host)
val getPort: ConfigReader[Int] = Reader(_.port)
val getTimeout: ConfigReader[Int] = Reader(_.timeout)

// Композиция операций, зависящих от конфигурации
val connectionString: ConfigReader[String] = for {
  host <- getHost
  port <- getPort
  timeout <- getTimeout
} yield s"$host:$port (timeout: ${timeout}ms)"

val config = Config("localhost", 8080, 30000)
val result = connectionString.run(config)
// "localhost:8080 (timeout: 30000ms)"
```

### Практические примеры: **State Monad**

```scala
import scalaz._
import Scalaz._

type Counter = Int

def increment: State[Counter, Int] = for {
  count <- get[Counter]
  _ <- put(count + 1)
  newCount <- get[Counter]
} yield newCount

val initialState = 0
val (finalState, result) = increment.run(initialState)
// finalState: 1, result: 1
```

### Практические примеры: **Kleisli** для композиции функций

```scala
import scalaz._
import Scalaz._

type StringOption = String => Option[String]

def parseNumber(s: String): Option[Int] = s.toIntOption

def divide(a: Int, b: Int): Option[Double] = 
  if (b == 0) None else Some(a.toDouble / b)

// Композиция через Kleisli
val parseAndDivide: String => String => Option[Double] = 
  Kleisli(parseNumber) >=> Kleisli(divide(_, 2))

val result = parseAndDivide("10")("")  // Some(5.0)
```

### Работа с **Task** для асинхронных операций

```scala
import scalaz.concurrent.Task
import scalaz.syntax.all._

// Использование Task для асинхронных операций
val asyncOperation: Task[Int] = Task {
  Thread.sleep(1000)
  42
}

val computation: Task[String] = for {
  value <- asyncOperation
  result <- Task(s"Result: $value")
} yield result

val future = computation.runAsync(result => println(result))
```

### Работа с **Either** для обработки ошибок

```scala
import scalaz._
import Scalaz._

type Result[A] = Either[String, A]

def parseInt(s: String): Result[Int] = {
  s.toIntOption match {
    case Some(i) => i.right
    case None => s"Invalid integer: $s".left
  }
}

def divide(a: Int, b: Int): Result[Double] = {
  if (b == 0) "Division by zero".left
  else (a.toDouble / b).right
}

val computation: Result[Double] = for {
  x <- parseInt("10")
  y <- parseInt("2")
  result <- divide(x, y)
} yield result

val result = computation
// Right(5.0)
```

### Работа с **Tree** для иерархических структур

```scala
import scalaz._
import Scalaz._

// Использование Tree для представления иерархических структур
val tree: Tree[Int] = 
  1.node(
    2.leaf,
    3.node(
      4.leaf,
      5.leaf
    )
  )

// Обход дерева
val flattened = tree.flatten  // List(1, 2, 3, 4, 5)
val sum = tree.foldMap(identity)  // 15
val depth = tree.foldMap(_ => 1)  // 3
```

### Работа с **NonEmptyList** для гарантии непустых списков

```scala
import scalaz._
import Scalaz._

// NonEmptyList гарантирует, что список не пуст
val nel: NonEmptyList[Int] = NonEmptyList(1, 2, 3, 4, 5)

// Операции безопасны, так как список всегда не пуст
val head = nel.head  // 1
val tail = nel.tail  // List(2, 3, 4, 5)
val last = nel.last  // 5

// Композиция с валидацией
def validateNonEmpty[A](list: List[A]): ValidationNel[String, NonEmptyList[A]] = {
  if (list.isEmpty) "List cannot be empty".failureNel
  else NonEmptyList.fromSeq(list).get.successNel
}
```

### Работа с **IList** для иммутабельных списков

```scala
import scalaz.IList

// IList - иммутабельный список из Scalaz
val ilist: IList[Int] = IList(1, 2, 3, 4, 5)

// Операции над IList
val mapped = ilist.map(_ * 2)  // IList(2, 4, 6, 8, 10)
val filtered = ilist.filter(_ % 2 == 0)  // IList(2, 4)
val sum = ilist.foldLeft(0)(_ + _)  // 15
```

### Работа с **ISet** для иммутабельных множеств

```scala
import scalaz.ISet

// ISet - иммутабельное множество из Scalaz
val iset1: ISet[Int] = ISet(1, 2, 3, 4, 5)
val iset2: ISet[Int] = ISet(4, 5, 6, 7, 8)

// Операции над множествами
val union = iset1 union iset2  // ISet(1, 2, 3, 4, 5, 6, 7, 8)
val intersection = iset1 intersection iset2  // ISet(4, 5)
val difference = iset1 difference iset2  // ISet(1, 2, 3)
```

### Работа с \/ (**Disjunction**) для обработки ошибок

```scala
import scalaz._
import Scalaz._

// \/ (Disjunction) - более мощная альтернатива Either
type Result[A] = String \/ A

def parseInt(s: String): Result[Int] = {
  s.toIntOption match {
    case Some(i) => i.right
    case None => s"Invalid integer: $s".left
  }
}

def divide(a: Int, b: Int): Result[Double] = {
  if (b == 0) "Division by zero".left
  else (a.toDouble / b).right
}

val computation: Result[Double] = for {
  x <- parseInt("10")
  y <- parseInt("2")
  result <- divide(x, y)
} yield result

// Альтернативные операции
val mapped = computation.map(_ * 2)
val flatMapped = computation.flatMap(r => (r + 1).right)
```

### Работа с **Maybe** (**Option**) с дополнительными операциями

```scala
import scalaz._
import Scalaz._

// Maybe - аналог Option с дополнительными операциями
val maybe: Maybe[Int] = 42.just
val empty: Maybe[Int] = Maybe.empty

// Операции над Maybe
val mapped = maybe.map(_ * 2)  // Maybe(84)
val flatMapped = maybe.flatMap(x => (x * 2).just)  // Maybe(84)
val getOrElse = empty.getOrElse(0)  // 0

// Композиция с Validation
val validated = maybe.toSuccessNel("Value is missing")
```

### Работа с **Const** для **type-level** вычислений

```scala
import scalaz._
import Scalaz._

// Const - полезен для type-level вычислений
val const: Const[String, Int] = Const("constant")

// Получение значения
val value = const.getConst  // "constant"

// Использование в Traverse
val traverseResult = List(1, 2, 3).traverse(x => Const("a"))
// Const("aaa")
```

### Работа с **Free Monad** для **DSL**

```scala
import scalaz._
import Scalaz._
import scalaz.Free

// Определение DSL через Free Monad
sealed trait UserDSL[A]
case class FindUser(id: Long) extends UserDSL[Option[User]]
case class SaveUser(user: User) extends UserDSL[User]

type UserProgram[A] = Free[UserDSL, A]

// Создание программ
def findUser(id: Long): UserProgram[Option[User]] = {
  Free.liftF(FindUser(id))
}

def saveUser(user: User): UserProgram[User] = {
  Free.liftF(SaveUser(user))
}

// Композиция программ
val program: UserProgram[User] = for {
  maybeUser <- findUser(1L)
  user <- maybeUser match {
    case Some(u) => Free.pure(u)
    case None => saveUser(User(1L, "Alice", "alice@example.com"))
  }
} yield user

// Интерпретатор
def interpreter: UserDSL ~> Id = new (UserDSL ~> Id) {
  def apply[A](fa: UserDSL[A]): Id[A] = fa match {
    case FindUser(id) => Some(User(id, "Alice", "alice@example.com"))
    case SaveUser(user) => user
  }
}

// Выполнение программы
val result = program.foldMap(interpreter)
```

### Работа с **Coyoneda** для оптимизации

```scala
import scalaz._
import Scalaz._

// Coyoneda - оптимизация для Functor операций
val list = List(1, 2, 3, 4, 5)

// Отложенное применение функции
val coyoneda = Coyoneda.lift(list)
val mapped = coyoneda.map(_ * 2).map(_ + 1)

// Применение всех функций за один проход
val result = mapped.run
// List(3, 5, 7, 9, 11)
```

### Работа с **Codensity** для оптимизации **Monad** операций

```scala
import scalaz._
import Scalaz._

// Codensity - оптимизация для Monad операций
val list = List(1, 2, 3, 4, 5)

// Отложенное выполнение операций
val codensity = Codensity.point[List, Int](list.head)
val flatMapped = codensity.flatMap(x => Codensity.point[List, Int](x * 2))

// Применение всех операций
val result = flatMapped.run
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Scalaz** - это библиотека функционального программирования для **Scala**, которая предоставляет множество абстракций для создания чистого функционального кода. Понимание **Type Classes**, **Monads**, **Functors**, **Applicative**, **Validation**, **Lens**, **Reader Monad** для **dependency injection**, **State Monad**, **Kleisli** для композиции функций и их практических применений позволяет создавать композируемый, типобезопасный и выразительный код.

Использование **Scalaz** для обработки ошибок, работы с эффектами, валидации, создания **Lens**, **dependency injection**, управления состоянием, композиции функций и практических применений критично для создания надежных, гибких функциональных приложений.

**Scalaz** предоставляет мощные инструменты для работы с асинхронными операциями (**Task**), обработки ошибок (**Either, \/**), иерархических структур (**Tree**), непустыми списками (**NonEmptyList**), иммутабельными структурами (**IList, ISet**), **Maybe**, **type-level** вычислениями (**Const**), **DSL** (**Free Monad**), и оптимизацией (**Coyoneda, Codensity**). Понимание этих техник позволяет создавать сложные, масштабируемые функциональные приложения.

### Работа с **Order** для упорядочивания

```scala
import scalaz._
import Scalaz._

// Order для упорядочивания значений
val intOrder: Order[Int] = implicitly[Order[Int]]

val comparison = intOrder.order(10, 20)  // Ordering.LT
val isEqual = intOrder.equal(10, 10)  // true

// Использование операторов
val result = 10 ?|? 20  // Ordering.LT
val isLess = 10 <| 20  // true
```

### Работа с **Equal** для сравнения

```scala
import scalaz._
import Scalaz._

// Equal для сравнения значений
val intEqual: Equal[Int] = implicitly[Equal[Int]]

val isEqual = intEqual.equal(10, 10)  // true
val isNotEqual = intEqual.equal(10, 20)  // false

// Использование операторов
val result = 10 === 10  // true
val notEqual = 10 =/= 20  // true
```

### Работа с **Show** для преобразования в строку

```scala
import scalaz._
import Scalaz._

// Show для преобразования значений в строку
val intShow: Show[Int] = implicitly[Show[Int]]

val string = intShow.show(42)  // "42"

// Использование оператора
val result = 42.shows  // "42"
```

### Работа с **Foldable** для операций над коллекциями

```scala
import scalaz._
import Scalaz._

// Foldable для операций над коллекциями
val listFoldable: Foldable[List] = implicitly[Foldable[List]]

val list = List(1, 2, 3, 4, 5)
val sum = listFoldable.foldLeft(list, 0)(_ + _)  // 15
val product = listFoldable.foldRight(list, 1)(_ * _)  // 120

// Использование операторов
val sum2 = list.suml  // 15
val product2 = list.productr  // 120
```

### Работа с **Traverse** для трансформации коллекций

```scala
import scalaz._
import Scalaz._

// Traverse для трансформации коллекций с эффектами
val list = List(1, 2, 3, 4, 5)

// Трансформация с Option
val maybeList = list.traverse(x => if (x > 0) Some(x * 2) else None)
// Some(List(2, 4, 6, 8, 10))

// Трансформация с Validation
val validatedList = list.traverse(x => 
  if (x > 0) x.successNel[String] else "Invalid".failureNel[Int]
)
// Success(List(1, 2, 3, 4, 5))
```

### Работа с **Plus** для комбинирования значений

```scala
import scalaz._
import Scalaz._

// Plus для комбинирования значений
val optionPlus: Plus[Option] = implicitly[Plus[Option]]

val option1 = Some(1)
val option2 = Some(2)
val combined = optionPlus.plus(option1, option2)  // Some(1)

// Использование оператора
val result = option1 <+> option2  // Some(1)
```

### Работа с **ApplicativePlus** для дополнительных операций

```scala
import scalaz._
import Scalaz._

// ApplicativePlus расширяет Applicative с Plus
val listApplicativePlus: ApplicativePlus[List] = implicitly[ApplicativePlus[List]]

val list1 = List(1, 2, 3)
val list2 = List(4, 5, 6)

// Комбинирование
val combined = list1 <+> list2  // List(1, 2, 3, 4, 5, 6)

// Пустое значение
val empty = listApplicativePlus.empty[List[Int]]  // List()
```

### Работа с **MonadPlus** для дополнительных операций

```scala
import scalaz._
import Scalaz._

// MonadPlus расширяет Monad с Plus
val listMonadPlus: MonadPlus[List] = implicitly[MonadPlus[List]]

val list = List(1, 2, 3, 4, 5)

// Фильтрация
val filtered = list.filterM(x => List(x > 2))  // List(3, 4, 5)

// Guard для условного выполнения
val result = for {
  x <- list
  _ <- if (x > 2) List(()) else List.empty
} yield x * 2
// List(6, 8, 10)
```

### Работа с **Arrow** для композиции функций

```scala
import scalaz._
import Scalaz._

// Arrow для композиции функций
val arrow: Arrow[Function1] = implicitly[Arrow[Function1]]

val f: Int => Int = _ * 2
val g: Int => Int = _ + 1

// Композиция
val composed = arrow.compose(f, g)  // x => (x + 1) * 2

// Параллельная композиция
val parallel = arrow.split(f, g)  // (x, y) => (x * 2, y + 1)
```

### Работа с **Comonad** для операций над структурами

```scala
import scalaz._
import Scalaz._

// Comonad для операций над структурами
val streamComonad: Comonad[Stream] = implicitly[Comonad[Stream]]

val stream = Stream(1, 2, 3, 4, 5)

// Извлечение значения
val head = streamComonad.copoint(stream)  // 1

// Применение функции
val mapped = streamComonad.cobind(stream)(s => s.sum)  // Stream(15, 14, 12, 9, 5)
```

### Работа с **Bifunctor** для работы с двумя типами

```scala
import scalaz._
import Scalaz._

// Bifunctor для работы с двумя типами
val eitherBifunctor: Bifunctor[Either] = implicitly[Bifunctor[Either]]

val either: Either[String, Int] = Right(42)

// Применение функции к обоим типам
val mapped = eitherBifunctor.bimap(
  either,
  (s: String) => s.toUpperCase,
  (i: Int) => i * 2
)
// Right(84)
```

### Работа с **Profunctor** для работы с функциями

```scala
import scalaz._
import Scalaz._

// Profunctor для работы с функциями
val functionProfunctor: Profunctor[Function1] = implicitly[Profunctor[Function1]]

val f: Int => String = _.toString

// Применение функции к входу и выходу
val mapped = functionProfunctor.dimap(
  f,
  (s: String) => s.toInt,
  (i: Int) => i.toString
)
// String => String
```

### Работа с **Contravariant** для контравариантных функторов

```scala
import scalaz._
import Scalaz._

// Contravariant для контравариантных функторов
trait Ordering[A] {
  def compare(a1: A, a2: A): Int
}

implicit val orderingContravariant: Contravariant[Ordering] = 
  new Contravariant[Ordering] {
    def contramap[A, B](fa: Ordering[A])(f: B => A): Ordering[B] = 
      new Ordering[B] {
        def compare(b1: B, b2: B): Int = fa.compare(f(b1), f(b2))
      }
  }
```

### Работа с **InvariantFunctor** для инвариантных функторов

```scala
import scalaz._
import Scalaz._

// InvariantFunctor для инвариантных функторов
trait Codec[A] {
  def encode(a: A): String
  def decode(s: String): Option[A]
}

implicit val codecInvariant: InvariantFunctor[Codec] = 
  new InvariantFunctor[Codec] {
    def xmap[A, B](fa: Codec[A])(f: A => B, g: B => A): Codec[B] = 
      new Codec[B] {
        def encode(b: B): String = fa.encode(g(b))
        def decode(s: String): Option[B] = fa.decode(s).map(f)
      }
  }
```

### Работа с **Zip** для параллельной композиции

```scala
import scalaz._
import Scalaz._

// Zip для параллельной композиции
val listZip: Zip[List] = implicitly[Zip[List]]

val list1 = List(1, 2, 3)
val list2 = List(4, 5, 6)

// Параллельная композиция
val zipped = listZip.zip(list1, list2)  // List((1, 4), (2, 5), (3, 6))
```

### Работа с **Unzip** для разделения

```scala
import scalaz._
import Scalaz._

// Unzip для разделения
val listUnzip: Unzip[List] = implicitly[Unzip[List]]

val list = List((1, 4), (2, 5), (3, 6))

// Разделение
val (list1, list2) = listUnzip.unzip(list)
// list1: List(1, 2, 3)
// list2: List(4, 5, 6)
```

### Работа с **Align** для выравнивания коллекций

```scala
import scalaz._
import Scalaz._

// Align для выравнивания коллекций разной длины
val listAlign: Align[List] = implicitly[Align[List]]

val list1 = List(1, 2, 3)
val list2 = List(4, 5)

// Выравнивание
val aligned = listAlign.align(list1, list2)
// List(\/-(1, 4), \/-(2, 5), -\/(3))
```

### Работа с **Cozip** для разделения **Coproduct**

```scala
import scalaz._
import Scalaz._

// Cozip для разделения Coproduct
type IntOrString = Int \/ String

val cozip: Cozip[\/[Int, ?]] = implicitly[Cozip[\/[Int, ?]]]

val either: IntOrString = Right("hello")

// Разделение
val (maybeInt, maybeString) = cozip.cozip(either)
// maybeInt: Option[Int] = None
// maybeString: Option[String] = Some("hello")
```

### Работа с **Cobind** для операций над структурами

```scala
import scalaz._
import Scalaz._

// Cobind для операций над структурами
val streamCobind: Cobind[Stream] = implicitly[Cobind[Stream]]

val stream = Stream(1, 2, 3, 4, 5)

// Применение функции к структуре
val mapped = streamCobind.cobind(stream)(s => s.sum)  // Stream(15, 14, 12, 9, 5)
```

### Работа с **Cojoin** для дублирования структуры

```scala
import scalaz._
import Scalaz._

// Cojoin для дублирования структуры
val streamCojoin: Cojoin[Stream] = implicitly[Cojoin[Stream]]

val stream = Stream(1, 2, 3)

// Дублирование
val duplicated = streamCojoin.cojoin(stream)
// Stream(Stream(1, 2, 3), Stream(2, 3), Stream(3))
```

### Работа с **Compose** для композиции

```scala
import scalaz._
import Scalaz._

// Compose для композиции
val functionCompose: Compose[Function1] = implicitly[Compose[Function1]]

val f: Int => String = _.toString
val g: String => Int = _.length

// Композиция
val composed = functionCompose.compose(f, g)  // Int => Int
```

### Работа с **Category** для категорий

```scala
import scalaz._
import Scalaz._

// Category для категорий
val functionCategory: Category[Function1] = implicitly[Category[Function1]]

val f: Int => String = _.toString
val g: String => Int = _.length

// Композиция
val composed = functionCategory.compose(f, g)  // Int => Int

// Identity
val id = functionCategory.id[Int]  // Int => Int
```

### Работа с **Strong** для сильных профункторов

```scala
import scalaz._
import Scalaz._

// Strong для сильных профункторов
val functionStrong: Strong[Function1] = implicitly[Strong[Function1]]

val f: Int => String = _.toString

// Усиление
val strengthened = functionStrong.first(f)  // (Int, B) => (String, B)
```

### Работа с **Choice** для выбора

```scala
import scalaz._
import Scalaz._

// Choice для выбора
val functionChoice: Choice[Function1] = implicitly[Choice[Function1]]

val f: Int => String = _.toString

// Выбор
val chosen = functionChoice.choice(f, f)  // Either[Int, Int] => Either[String, String]
```

### Работа с **Split** для разделения

```scala
import scalaz._
import Scalaz._

// Split для разделения
val functionSplit: Split[Function1] = implicitly[Split[Function1]]

val f: Int => String = _.toString
val g: Int => Int = _ * 2

// Разделение
val split = functionSplit.split(f, g)  // (Int, Int) => (String, Int)
```

### Работа с **Choice** для выбора между функциями

```scala
import scalaz._
import Scalaz._

// Choice для выбора между функциями
val functionChoice: Choice[Function1] = implicitly[Choice[Function1]]

val f: Int => String = _.toString
val g: String => Int = _.length

// Выбор
val chosen = functionChoice.choice(f, g)  // Either[Int, String] => Either[String, Int]
```

### Работа с **Costar** для контравариантных функторов

```scala
import scalaz._
import Scalaz._

// Costar для контравариантных функторов
val functionCostar: Costar[Function1, Option] = 
  new Costar[Function1, Option] {
    def apply[A, B](f: Option[A] => B): A => B = 
      a => f(Some(a))
  }
```

### Работа с **Star** для ковариантных функторов

```scala
import scalaz._
import Scalaz._

// Star для ковариантных функторов
val functionStar: Star[Function1, Option] = 
  new Star[Function1, Option] {
    def apply[A, B](f: A => Option[B]): Option[A] => Option[B] = 
      _.flatMap(f)
  }
```

## Дополнительные ресурсы

- [Scalaz Documentation](https://scalaz.github.io/scalaz/)
- [Scalaz Guide](https://github.com/scalaz/scalaz)
- [Learning Scalaz](http://eed3si9n.com/learning-scalaz/)

