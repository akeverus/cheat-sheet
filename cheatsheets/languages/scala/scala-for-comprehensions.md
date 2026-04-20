---
title: "Scala For Comprehensions"
description: "Полное руководство по For-comprehensions в Scala: синтаксический сахар для flatMap, фильтрация, генераторы"
tags:
  - scala
  - for-comprehensions
  - functional-programming
  - monads
difficulty: "intermediate"
prerequisites: ["scala/scala-fp-basics.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-fp-basics.md", "scala/scala-collections.md"]
---

# Scala For Comprehensions

Кратко: полное руководство по **For-comprehensions** в **Scala**: синтаксический сахар для **flatMap**, фильтрация, генераторы.

## Полезные ссылки

### Официальная документация
- [Scala For Comprehensions](https://docs.scala-lang.org/tour/for-comprehensions.html)

### См. также
- [[scala-fp-basics|Функциональное программирование]]
- [[scala-collections|Коллекции]]

## Содержание

- [**Scala For Comprehensions**](#scala-for-comprehensions)
- [Введение в **For-comprehensions**](#введение-в-for-comprehensions)
  - [Основные преимущества](#основные-преимущества)
- [Базовые **For-comprehensions**](#базовые-for-comprehensions)
- [Множественные генераторы](#множественные-генераторы)
- [Фильтрация](#фильтрация)
- [**For-comprehensions** с **Option**](#for-comprehensions-с-option)
- [**For-comprehensions** с **Future**](#for-comprehensions-с-future)
  - [**For-comprehensions** с **Try**](#for-comprehensions-с-try)
  - [**For-comprehensions** с **Either**](#for-comprehensions-с-either)
  - [**For-comprehensions** с **List**](#for-comprehensions-с-list)
  - [Вложенные **for-comprehensions**](#вложенные-for-comprehensions)
  - [**For-comprehensions** с присваиваниями](#for-comprehensions-с-присваиваниями)
  - [Практический пример: Обработка вложенных структур](#практический-пример-обработка-вложенных-структур)
  - [Практический пример: Валидация данных](#практический-пример-валидация-данных)
  - [Десюгаризация **for-comprehensions**](#десюгаризация-for-comprehensions)
  - [**For-comprehensions** без **yield**](#for-comprehensions-без-yield)
- [Лучшие практики](#лучшие-практики)
  - [Использование **for-comprehensions** вместо вложенных **flatMap**](#использование-for-comprehensions-вместо-вложенных-flatmap)
  - [Использование фильтров в **for-comprehensions**](#использование-фильтров-в-for-comprehensions)
  - [Использование **for-comprehensions** с различными типами](#использование-for-comprehensions-с-различными-типами)
  - [Комбинирование различных типов](#комбинирование-различных-типов)
  - [Избегание излишней вложенности](#избегание-излишней-вложенности)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные техники работы с **For-comprehensions**](#дополнительные-техники-работы-с-for-comprehensions)
  - [Работа с вложенными **For-comprehensions**](#работа-с-вложенными-for-comprehensions)
  - [Использование **Guards** в **For-comprehensions**](#использование-guards-в-for-comprehensions)
  - [Комбинирование различных **Monads**](#комбинирование-различных-monads)
  - [Практические примеры: Вложенные **For-comprehensions**](#практические-примеры-вложенные-for-comprehensions)
  - [Практические примеры: **For-comprehensions** с **Guards**](#практические-примеры-for-comprehensions-с-guards)
  - [Практические примеры: **For-comprehensions** для валидации](#практические-примеры-for-comprehensions-для-валидации)
  - [Практические примеры: **For-comprehensions** с различными коллекциями](#практические-примеры-for-comprehensions-с-различными-коллекциями)
  - [Практические примеры: **For-comprehensions** для обработки ошибок](#практические-примеры-for-comprehensions-для-обработки-ошибок)
  - [Практические примеры: **For-comprehensions** для асинхронных операций](#практические-примеры-for-comprehensions-для-асинхронных-операций)
  - [Использование с различными типами для композиции](#использование-с-различными-типами-для-композиции)
  - [Использование с различными типами для обработки ошибок](#использование-с-различными-типами-для-обработки-ошибок)
  - [Использование с различными типами для валидации](#использование-с-различными-типами-для-валидации)
  - [Использование с различными типами для работы с состояниями](#использование-с-различными-типами-для-работы-с-состояниями)
  - [Использование с различными типами для работы с логами](#использование-с-различными-типами-для-работы-с-логами)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в For-comprehensions

**For-comprehensions** — это синтаксический сахар для работы с **Monads** в **Scala**. **For-comprehensions** делают код более читаемым и выразительным при работе с вложенными **flatMap** и **map**. Вместо написания цепочек вложенных **flatMap** и **map**, **for-comprehensions** позволяют выразить ту же логику в более императивном стиле, который легче читать и понимать.

**For-comprehensions** компилируются в комбинацию **map**, **flatMap** и **filter**, что означает, что они работают с любыми типами, которые имеют эти методы. Это включает коллекции (List, `Option`, `Future`, Try, Either) и пользовательские типы, реализующие эти методы.

**For-comprehensions** — это синтаксический сахар для работы с **Monads** в **Scala**. **For-comprehensions** делают код более читаемым и выразительным при работе с вложенными **flatMap** и **map**.

### Основные преимущества

- **Читаемость**: код становится более понятным. **For-comprehensions** позволяют выразить последовательные операции в более императивном стиле, что делает код более похожим на обычные циклы, но с функциональной семантикой. Это особенно полезно для сложных цепочек операций, где вложенные **flatMap** и **map** становятся трудными для чтения.

- **Композиция**: легко комбинировать несколько операций. **For-comprehensions** позволяют естественным образом комбинировать несколько операций, каждая из которых может зависеть от результата предыдущей. Это делает код более декларативным и выразительным, показывая последовательность операций явно.

- **Универсальность**: работает с любыми типами, имеющими **map** и **flatMap**. **For-comprehensions** не ограничены конкретными типами и работают с любыми типами, которые реализуют методы **map** и **flatMap**. Это включает стандартные типы **Scala** (List, `Option`, `Future`, Try, Either) и пользовательские типы, что делает **for-comprehensions** универсальным инструментом для работы с различными контекстами вычислений.

## Базовые For-comprehensions

Базовые **for-comprehensions** с одним генератором являются простейшей формой и эквивалентны вызову метода **map**. Они позволяют трансформировать каждый элемент коллекции, применяя функцию к каждому элементу. Это делает код более читаемым, особенно когда функция трансформации сложная и требует нескольких строк.

Базовые **for-comprehensions** особенно полезны для простых трансформаций, где использование **map** с анонимной функцией может быть менее читаемым. Они также служат основой для более сложных **for-comprehensions** с несколькими генераторами и фильтрами.

```scala
val numbers = List(1, 2, 3)

// Простой for-comprehension
// n <- numbers означает "для каждого элемента n из numbers"
// yield n * 2 означает "вернуть n * 2"
// Результат - новая коллекция с трансформированными значениями
val doubled = for (n <- numbers) yield n * 2
// List(2, 4, 6)

// Эквивалентно
// map применяет функцию к каждому элементу коллекции
val doubled2 = numbers.map(_ * 2)
// Оба выражения производят одинаковый результат
```

**For-comprehensions** с одним генератором эквивалентны вызову **map**. Компилятор **Scala** преобразует **for-comprehension** с одним генератором в вызов **map**, что означает, что нет разницы в производительности между этими двумя подходами. Выбор между ними зависит от читаемости и стиля кода.

## Множественные генераторы

```scala
val numbers = List(1, 2, 3)
val letters = List('a', 'b', 'c')

// Множественные генераторы
val pairs = for {
  n <- numbers
  l <- letters
} yield (n, l)
// List((1,a), (1,b), (1,c), (2,a), (2,b), (2,c), (3,a), (3,b), (3,c))

// Эквивалентно
val pairs2 = numbers.flatMap(n => letters.map(l => (n, l)))
```

Множественные генераторы создают декартово произведение, что эквивалентно вложенным **flatMap** и **map**.

## Фильтрация

```scala
val numbers = List(1, 2, 3, 4, 5)

// For-comprehension с фильтром
val evens = for {
  n <- numbers
  if n % 2 == 0
} yield n * 2
// List(4, 8)

// Эквивалентно
val evens2 = numbers.filter(_ % 2 == 0).map(_ * 2)
```

Фильтры в **for-comprehensions** эквивалентны вызову **filter**.

## For-comprehensions с Option

```scala
def getUser(id: Long): Option[User] = ???
def getPosts(userId: Long): Option[List[Post]] = ???

// For-comprehension с Option
val result = for {
  user <- getUser(1L)
  posts <- getPosts(user.id)
} yield (user, posts)
// Option[(User, List[Post])]

// Эквивалентно
val result2 = getUser(1L).flatMap(user =>
  getPosts(user.id).map(posts => (user, posts))
)
```

**For-comprehensions** с **Option** позволяют элегантно обрабатывать возможные отсутствующие значения.

## For-comprehensions с Future

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def getUser(id: Long): Future[User] = ???
def getPosts(userId: Long): Future[List[Post]] = ???

// For-comprehension с Future
val result = for {
  user <- getUser(1L)
  posts <- getPosts(user.id)
} yield (user, posts)
// Future[(User, List[Post])]
```

**For-comprehensions** с **Future** позволяют комбинировать асинхронные операции в последовательном стиле.

### For-comprehensions с Try

**For-comprehensions** работают с **Try** для обработки ошибок:**

```scala
import scala.util.{Try, Success, Failure}

def parseInt(s: String): Try[Int] = Try(s.toInt)
def divide(a: Int, b: Int): Try[Int] = Try(a / b)

// For-comprehension с Try
val result = for {
  a <- parseInt("10")
  b <- parseInt("2")
  c <- divide(a, b)
} yield c

result match {
  case Success(value) => println(s"Result: $value")
  case Failure(e) => println(s"Error: ${e.getMessage}")
}
```

### For-comprehensions с Either

**For-comprehensions** работают с **Either**:**

```scala
def parseInt(s: String): Either[String, Int] = {
  try Right(s.toInt)
  catch { case _: NumberFormatException => Left(s"Invalid number: $s") }
}

def divide(a: Int, b: Int): Either[String, Int] = {
  if (b != 0) Right(a / b)
  else Left("Division by zero")
}

// For-comprehension с Either
val result = for {
  a <- parseInt("10")
  b <- parseInt("2")
  c <- divide(a, b)
} yield c

result match {
  case Right(value) => println(s"Result: $value")
  case Left(error) => println(s"Error: $error")
}
```

### For-comprehensions с List

**For-comprehensions** естественно работают со списками:**

```scala
val numbers = List(1, 2, 3)
val letters = List('a', 'b', 'c')

// Декартово произведение
val combinations = for {
  n <- numbers
  l <- letters
} yield (n, l)
// List((1,a), (1,b), (1,c), (2,a), (2,b), (2,c), (3,a), (3,b), (3,c))

// С фильтрацией
val filtered = for {
  n <- numbers if n > 1
  l <- letters if l != 'b'
} yield (n, l)
// List((2,a), (2,c), (3,a), (3,c))
```

### Вложенные for-comprehensions

**For-comprehensions** можно вкладывать:**

```scala
def getUser(id: Long): Option[User] = ???
def getPosts(userId: Long): Option[List[Post]] = ???
def getComments(postId: Long): Option[List[Comment]] = ???

// Вложенные for-comprehensions
val result = for {
  user <- getUser(1L)
  posts <- getPosts(user.id)
} yield {
  for {
    post <- posts
    comments <- getComments(post.id)
  } yield (post, comments)
}
```

### For-comprehensions с присваиваниями

**В **for-comprehensions** можно использовать присваивания:**

```scala
val numbers = List(1, 2, 3, 4, 5)

val result = for {
  n <- numbers
  doubled = n * 2  // присваивание
  if doubled > 4   // фильтр после присваивания
} yield doubled
// List(6, 8, 10)
```

### Практический пример: Обработка вложенных структур

```scala
case class User(id: Long, name: String)
case class Post(userId: Long, title: String)
case class Comment(postId: Long, text: String)

def getUser(id: Long): Option[User] = ???
def getUserPosts(userId: Long): Option[List[Post]] = ???
def getPostComments(postId: Long): Option[List[Comment]] = ???

// Комбинирование нескольких операций
val result = for {
  user <- getUser(1L)
  posts <- getUserPosts(user.id)
  post <- posts.headOption
  comments <- getPostComments(post.userId)
} yield {
  s"User ${user.name} has ${comments.size} comments on post '${post.title}'"
}
```

### Практический пример: Валидация данных

```scala
def validateEmail(email: String): Either[String, String] = {
  if (email.contains("@")) Right(email)
  else Left("Invalid email")
}

def validateAge(age: Int): Either[String, Int] = {
  if (age >= 0 && age <= 150) Right(age)
  else Left("Invalid age")
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
createPerson("invalid", 200)  // Left("Invalid email")
```

### Десюгаризация for-comprehensions

**For-comprehensions** десюгаризуются в вызовы **map**, **flatMap** и **filter**:**

```scala
// For-comprehension
val result = for {
  a <- option1
  b <- option2 if b > 0
  c <- option3
} yield a + b + c

// Эквивалентный код
val result2 = option1.flatMap(a =>
  option2.filter(_ > 0).flatMap(b =>
    option3.map(c => a + b + c)
  )
)
```

### For-comprehensions без yield

**For-comprehensions** без **yield** выполняют побочные эффекты:**

```scala
val numbers = List(1, 2, 3, 4, 5)

// For-comprehension без yield
for {
  n <- numbers
  if n % 2 == 0
} {
  println(s"Even number: $n")
}

// Эквивалентно
numbers.filter(_ % 2 == 0).foreach(n => println(s"Even number: $n"))
```

## Лучшие практики

### Использование for-comprehensions вместо вложенных flatMap

```scala
// Хорошо - использование for-comprehension
val result = for {
  a <- option1
  b <- option2
  c <- option3
} yield a + b + c

// Плохо - вложенные flatMap
val result2 = option1.flatMap(a =>
  option2.flatMap(b =>
    option3.map(c => a + b + c)
  )
)
```

### Использование фильтров в for-comprehensions

```scala
// Хорошо - фильтры в for-comprehension
val result = for {
  user <- getUser(id)
  if user.isActive
  posts <- getPosts(user.id)
  if posts.nonEmpty
} yield (user, posts)

// Плохо - фильтры вне for-comprehension
val user = getUser(id).filter(_.isActive)
val posts = user.flatMap(u => getPosts(u.id)).filter(_.nonEmpty)
```

### Использование for-comprehensions с различными типами

**For-comprehensions** могут использоваться с различными типами, поддерживающими **flatMap**.

```scala
// С Option
val optionResult = for {
  a <- Some(5)
  b <- Some(10)
} yield a + b

// С Either
val eitherResult = for {
  a <- Right(5)
  b <- Right(10)
} yield a + b

// С Try
val tryResult = for {
  a <- scala.util.Try(5)
  b <- scala.util.Try(10)
} yield a + b

// С Future
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val futureResult = for {
  a <- Future(5)
  b <- Future(10)
} yield a + b
```

### Комбинирование различных типов

**For-comprehensions** могут комбинировать различные типы через трансформации.

```scala
// Комбинирование Option и Either
val combined = for {
  user <- getUser(id).toRight("User not found")
  posts <- getPosts(user.id).toRight("Posts not found")
} yield (user, posts)
```

### Избегание излишней вложенности

```scala
// Хорошо - плоская структура
val result = for {
  a <- option1
  b <- option2
  c <- option3
} yield a + b + c

// Плохо - излишняя вложенность
val result2 = for {
  a <- option1
} yield {
  for {
    b <- option2
  } yield {
    for {
      c <- option3
    } yield a + b + c
  }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Дополнительные техники работы с For-comprehensions

### Работа с вложенными For-comprehensions

Вложенные **For-comprehensions** позволяют обрабатывать сложные структуры данных.

```scala
// Вложенные For-comprehensions для обработки вложенных структур
val result = for {
  user <- getUserOption
  address <- user.address
  city <- address.city
} yield city.name
```

### Использование Guards в For-comprehensions

**Guards** позволяют фильтровать данные внутри **For-comprehensions**.

```scala
// Использование guards для фильтрации
val result = for {
  x <- List(1, 2, 3, 4, 5)
  y <- List(10, 20, 30)
  if x * y > 50  // guard
} yield (x, y)
```

### Комбинирование различных Monads

**For-comprehensions** могут комбинировать различные типы **Monads**.

```scala
import scala.concurrent.Future
import scala.util.Try

// Комбинирование Option и Future
val result = for {
  user <- getUserOption
  profile <- getProfileFuture(user.id)
} yield (user, profile)
```

### Практические примеры: Вложенные For-comprehensions

```scala
// Обработка вложенных структур
case class User(id: Int, name: String, orders: List[Order])
case class Order(id: Int, items: List[Item])
case class Item(id: Int, name: String, price: Double)

val users = List(
  User(1, "Alice", List(Order(1, List(Item(1, "Book", 10.0)))))
)

// Получение всех товаров всех заказов всех пользователей
val allItems = for {
  user <- users
  order <- user.orders
  item <- order.items
} yield item
```

### Практические примеры: For-comprehensions с Guards

```scala
// Фильтрация с использованием guards
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

val evenSquares = for {
  n <- numbers
  if n % 2 == 0  // Guard - фильтрация
  square = n * n  // Определение локальной переменной
  if square > 10  // Еще один guard
} yield square
// List(16, 36, 64, 100)
```

### Практические примеры: For-comprehensions для валидации

```scala
def validateUser(name: String, age: Int, email: String): Either[String, User] = {
  for {
    validName <- if (name.nonEmpty) Right(name) else Left("Name cannot be empty")
    validAge <- if (age >= 0 && age <= 150) Right(age) else Left("Invalid age")
    validEmail <- if (email.contains("@")) Right(email) else Left("Invalid email")
  } yield User(validName, validAge, validEmail)
}
```

**For-comprehensions** являются мощным инструментом для работы с **Monads** в **Scala**. Понимание их синтаксиса, эквивалентности с **map** и **flatMap**, работы с различными типами (Option, `Future`, Try, `Either`, List), работы с вложенными **For-comprehensions**, использования **Guards** в **For-comprehensions**, комбинирования различных **Monads**, обработки вложенных структур, валидации данных и практических применений позволяет создавать читаемый и выразительный код. **For-comprehensions** особенно полезны для обработки вложенных структур, валидации данных, комбинирования асинхронных операций, обработки вложенных структур данных, фильтрации данных с использованием **guards**, комбинирования различных типов **Monads**, обработки вложенных коллекций и валидации с накоплением ошибок.

### Практические примеры: For-comprehensions с различными коллекциями

```scala
// С Set
val set1 = Set(1, 2, 3)
val set2 = Set(4, 5, 6)

val combinations = for {
  a <- set1
  b <- set2
} yield a + b
// Set(5, 6, 7, 8, 9)

// С Map
val map1 = Map("a" -> 1, "b" -> 2)
val map2 = Map("x" -> 10, "y" -> 20)

val result = for {
  (k1, v1) <- map1
  (k2, v2) <- map2
} yield (k1 + k2, v1 + v2)
// Map("ax" -> 11, "ay" -> 21, "bx" -> 12, "by" -> 22)
```

### Практические примеры: For-comprehensions для обработки ошибок

```scala
import scala.util.{Try, Success, Failure}

def parseInt(s: String): Try[Int] = Try(s.toInt)
def divide(a: Int, b: Int): Try[Int] = Try(a / b)

// Обработка ошибок через for-comprehension
val result = for {
  a <- parseInt("10")
  b <- parseInt("2")
  c <- divide(a, b)
} yield c

result match {
  case Success(value) => println(s"Result: $value")
  case Failure(e) => println(s"Error: ${e.getMessage}")
}
```

### Практические примеры: For-comprehensions для асинхронных операций

```scala
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global

def fetchUser(id: Long): Future[Option[User]] = Future(Some(User(id, "Alice")))
def fetchPosts(userId: Long): Future[List[Post]] = Future(List(Post(1, "Post 1")))

// Комбинирование асинхронных операций
val result = for {
  userOpt <- fetchUser(1L)
  user <- Future.successful(userOpt.get)
  posts <- fetchPosts(user.id)
} yield (user, posts)
```

### Использование с различными типами для композиции

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect.IO

// Композиция с Future
def fetchUser(id: Long): Future[Option[User]] = Future(Some(User(id, "Alice")))
def fetchPosts(userId: Long): Future[List[Post]] = Future(List(Post(1, "Post 1")))

val result = for {
  userOpt <- fetchUser(1L)
  user <- Future.successful(userOpt.get)
  posts <- fetchPosts(user.id)
} yield (user, posts)

// Композиция с IO
def getUserIO(id: Long): IO[Option[User]] = IO(Some(User(id, "Alice")))
def getPostsIO(userId: Long): IO[List[Post]] = IO(List(Post(1, "Post 1")))

val ioResult = for {
  userOpt <- getUserIO(1L)
  user <- IO.pure(userOpt.get)
  posts <- getPostsIO(user.id)
} yield (user, posts)
```

### Использование с различными типами для обработки ошибок

```scala
import scala.util.{Try, Success, Failure}

// Композиция с Try
def parseInt(s: String): Try[Int] = Try(s.toInt)
def divide(a: Int, b: Int): Try[Double] = Try(a.toDouble / b)

val result = for {
  x <- parseInt("10")
  y <- parseInt("2")
  result <- divide(x, y)
} yield result

// Композиция с Either
def parseIntEither(s: String): Either[String, Int] =
  Try(s.toInt).toOption.toRight(s"Invalid integer: $s")

def divideEither(a: Int, b: Int): Either[String, Double] =
  if (b == 0) Left("Division by zero") else Right(a.toDouble / b)

val eitherResult = for {
  x <- parseIntEither("10")
  y <- parseIntEither("2")
  result <- divideEither(x, y)
} yield result
```

### Использование с различными типами для валидации

```scala
import cats.data.ValidatedNel
import cats.syntax.all._

// Композиция с ValidatedNel
def validateName(name: String): ValidatedNel[String, String] =
  if (name.nonEmpty) name.validNel else "Name cannot be empty".invalidNel

def validateAge(age: Int): ValidatedNel[String, Int] =
  if (age >= 0) age.validNel else "Age must be non-negative".invalidNel

val validatedResult = for {
  name <- validateName("Alice")
  age <- validateAge(30)
} yield (name, age)
```

### Использование с различными типами для работы с состояниями

```scala
import cats.data.State

// Композиция с State
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
import cats.data.Writer
import cats.syntax.all._

// Композиция с Writer
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

**Для дальнейшего изучения **For-comprehensions** в **Scala** рекомендуется:**

- [Scala For Comprehensions Documentation](https://docs.scala-lang.org/tour/for-comprehensions.html)
- [Scala School — For Comprehensions](https://twitter.github.io/scala_school/collections.html)
