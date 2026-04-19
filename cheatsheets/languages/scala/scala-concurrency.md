---
title: "Scala Concurrency"
description: "Полное руководство по конкурентности в Scala: Futures, Promises, Actors (Akka), параллельные коллекции, синхронизация"
tags:
  - scala
  - concurrency
  - futures
  - akka
  - actors
  - parallel-collections
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: ["scala/scala-akka.md"]
updated: "2026-02-06"
related: ["scala/scala-basics.md", "scala/scala-fp-basics.md"]
---

# **Scala Concurrency**

Кратко: полное руководство по конкурентности в **Scala**: **Futures**, **Promises**, **Actors** (**Akka**), параллельные коллекции, синхронизация.

## Полезные ссылки

### Официальная документация
- [Scala Futures](https://docs.scala-lang.org/overviews/core/futures.html)
- [Akka Documentation](https://akka.io/docs/)

### См. также
- [Основы Scala](scala-basics.md)
- [Akka Actors](../../libraries/scala/scala-akka.md)
- [Функциональное программирование](scala-fp-basics.md)

## Содержание

- [**Scala Concurrency**](#scala-concurrency)
- [Введение в конкурентность](#введение-в-конкурентность)
- [**Futures**](#futures)
  - [Создание **Futures**](#создание-futures)
  - [Операции с **Futures**](#операции-с-futures)
  - [Ожидание результатов](#ожидание-результатов)
- [**Promises**](#promises)
- [**ExecutionContext**](#executioncontext)
- [Параллельные коллекции](#параллельные-коллекции)
  - [Настройка параллелизма](#настройка-параллелизма)
- [Композиция **Futures**](#композиция-futures)
  - [Последовательное выполнение](#последовательное-выполнение)
  - [Параллельное выполнение](#параллельное-выполнение)
  - [Обработка множественных **Futures**](#обработка-множественных-futures)
- [Синхронизация](#синхронизация)
  - [**Synchronized**](#synchronized)
  - [**Atomic Variables**](#atomic-variables)
- [Лучшие практики](#лучшие-практики)
  - [Избегание блокирующих операций](#избегание-блокирующих-операций)
  - [Правильное использование **ExecutionContext**](#правильное-использование-executioncontext)
- [Обработка ошибок в **Futures**](#обработка-ошибок-в-futures)
- [Таймауты и отмена](#таймауты-и-отмена)
- [Продвинутые техники конкурентности](#продвинутые-техники-конкурентности)
  - [Координация **Future**](#координация-future)
  - [Обработка ошибок в **Future**](#обработка-ошибок-в-future)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Расширенные техники конкурентности](#расширенные-техники-конкурентности)
  - [Работа с **ExecutionContext**](#работа-с-executioncontext)
  - [Координация множественных **Future**](#координация-множественных-future)
  - [Практические примеры: Координация множественных **Future**](#практические-примеры-координация-множественных-future)
  - [Практические примеры: Таймауты и отмена](#практические-примеры-таймауты-и-отмена)
  - [Практические примеры: Работа с **ExecutionContext**](#практические-примеры-работа-с-executioncontext)
  - [Практические примеры: Работа с **Promise** для координации](#практические-примеры-работа-с-promise-для-координации)
  - [Практические примеры: Работа с параллельными коллекциями](#практические-примеры-работа-с-параллельными-коллекциями)
  - [Использование с различными техниками для синхронизации](#использование-с-различными-техниками-для-синхронизации)
  - [Использование с различными техниками для координации](#использование-с-различными-техниками-для-координации)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в конкурентность

**Scala** предоставляет несколько подходов к конкурентному программированию:**

1. **Futures и Promises** - для асинхронных вычислений
2. **Actors (**Akka**)** - для акторной модели программирования
3. **Параллельные коллекции** - для параллельной обработки данных
4. **Синхронизация** - для координации потоков

## **Futures**

**Future** представляет асинхронное вычисление, которое может завершиться успешно или с ошибкой. **Future** не блокирует текущий поток выполнения, позволяя программе продолжать работу, пока вычисление выполняется в фоновом режиме. Это ключевой механизм для создания неблокирующих асинхронных приложений в **Scala**.

### Создание **Futures**

При создании **Future** код внутри блока выполняется асинхронно в контексте **ExecutionContext**. **ExecutionContext** определяет пул потоков, где будет выполняться вычисление. Глобальный **ExecutionContext** использует **ForkJoinPool**, который автоматически масштабируется в зависимости от нагрузки.

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Создание Future
// Код внутри блока выполняется асинхронно в отдельном потоке
val future = Future {
  Thread.sleep(1000)
  42
}

// Future с явным ExecutionContext
// Полезно для контроля над пулом потоков или использования специализированного контекста
val future2 = Future {
  // вычисление
}(ExecutionContext.global)
```

### Операции с **Futures**

```scala
val future = Future(5)

// Map - преобразование результата
val doubled = future.map(_ * 2)  // Future(10)

// FlatMap - композиция Futures
val composed = future.flatMap(n => Future(n * 2))  // Future(10)

// For-comprehension
val result = for {
  a <- Future(5)
  b <- Future(3)
} yield a + b  // Future(8)

// Обработка ошибок
val withErrorHandling = future.recover {
  case e: Exception => 0
}
```

### Ожидание результатов

```scala
import scala.concurrent.Await
import scala.concurrent.duration._

val future = Future {
  Thread.sleep(1000)
  42
}

// Блокирующее ожидание (не рекомендуется в production)
val result = Await.result(future, 5.seconds)  // 42

// Неблокирующее ожидание
future.onComplete {
  case Success(value) => println(s"Success: $value")
  case Failure(exception) => println(s"Error: ${exception.getMessage}")
}
```

## **Promises**

**Promise** - это **writable Future**, который позволяет завершить **Future** извне. В то время как **Future** представляет результат вычисления, **Promise** позволяет контролировать, когда и как это вычисление завершится. Это полезно для интеграции асинхронного кода с **callback-based API** или для создания **Future** из кода, который не может быть обернут в **Future** напрямую.

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

// Создание Promise
// Promise создает Future, который можно завершить позже
val promise = Promise[Int]()
val future = promise.future

// Завершение Promise
// Promise можно завершить только один раз. Повторные вызовы игнорируются.
promise.success(42)  // завершает Future успешно
// promise.failure(new Exception("Error"))  // завершает Future с ошибкой

// Использование
// Future, полученный из Promise, ведет себя как обычный Future
future.onComplete {
  case Success(value) => println(s"Value: $value")
  case Failure(exception) => println(s"Error: ${exception.getMessage}")
}
```

**Promise** особенно полезен при работе с **callback-based API**, где нужно преобразовать **callback** в **Future**, или когда результат вычисления определяется внешними событиями (**например, пользовательский ввод или сетевые события**).

## **ExecutionContext**

**ExecutionContext** определяет, где и как выполняются асинхронные вычисления:**

```scala
import scala.concurrent.ExecutionContext

// Глобальный ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

// Создание собственного ExecutionContext
import java.util.concurrent.Executors
val executor = Executors.newFixedThreadPool(10)
val ec = ExecutionContext.fromExecutor(executor)

// Использование
val future = Future {
  // вычисление
}(ec)
```

## Параллельные коллекции

**Параллельные коллекции позволяют обрабатывать данные параллельно:**

```scala
import scala.collection.parallel.CollectionConverters._

val numbers = (1 to 1000000).toList

// Параллельная обработка
val parallel = numbers.par
val doubled = parallel.map(_ * 2).toList

// Параллельная фильтрация
val evens = parallel.filter(_ % 2 == 0).toList

// Параллельная свертка
val sum = parallel.reduce(_ + _)

// Параллельная группировка
val grouped = parallel.groupBy(_ % 10)

// Параллельная сортировка
val sorted = parallel.sorted.toList
```

### Настройка параллелизма

```scala
import scala.collection.parallel.immutable.ParVector

// Настройка уровня параллелизма
val parVector = ParVector(1 to 1000000)
parVector.tasksupport = new scala.collection.parallel.ForkJoinTaskSupport(
  new scala.concurrent.forkjoin.ForkJoinPool(4)
)
```

## Композиция **Futures**

### Последовательное выполнение

```scala
val result = for {
  user <- getUserById(1L)
  posts <- getPostsByUserId(user.id)
  comments <- getCommentsByPostIds(posts.map(_.id))
} yield (user, posts, comments)
```

### Параллельное выполнение

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val userFuture = getUserById(1L)
val postsFuture = getPostsByUserId(1L)
val commentsFuture = getCommentsByUserId(1L)

val result = for {
  user <- userFuture
  posts <- postsFuture
  comments <- commentsFuture
} yield (user, posts, comments)
```

### Обработка множественных **Futures**

```scala
import scala.concurrent.Future
import scala.util.{Success, Failure}

val futures = List(
  Future(1),
  Future(2),
  Future(3)
)

// Ожидание всех Futures
val allResults = Future.sequence(futures)

// Ожидание первого успешного
val firstSuccess = Future.firstCompletedOf(futures)

// Ожидание всех с обработкой ошибок
val results = Future.traverse(ids) { id =>
  getUserById(id).recover {
    case e: Exception => None
  }
}
```

## Синхронизация

**Scala** предоставляет различные механизмы синхронизации:**

### **Synchronized**

```scala
class Counter {
  private var count = 0
  
  def increment(): Int = synchronized {
    count += 1
    count
  }
}
```

### **Atomic Variables**

```scala
import java.util.concurrent.atomic.AtomicInteger

class AtomicCounter {
  private val count = new AtomicInteger(0)
  
  def increment(): Int = count.incrementAndGet()
  def get(): Int = count.get()
}
```

## Лучшие практики

### Избегание блокирующих операций

```scala
// Хорошо - неблокирующий код
val future = Future {
  // асинхронная операция
}

// Плохо - блокирующий код
val result = Await.result(future, Duration.Inf)
```

### Правильное использование **ExecutionContext**

```scala
// Хорошо - использование подходящего ExecutionContext
val ioEc = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
val future = Future {
  // IO операция
}(ioEc)
```

## Обработка ошибок в **Futures**

**Futures** предоставляют несколько способов обработки ошибок:**

```scala
import scala.util.{Success, Failure}

val future = Future {
  // может выбросить исключение
  42 / 0
}

// Обработка с помощью recover
val recovered = future.recover {
  case e: ArithmeticException => 0
}

// Обработка с помощью recoverWith
val recoveredWith = future.recoverWith {
  case e: ArithmeticException => Future.successful(0)
}

// Обработка с помощью onComplete
future.onComplete {
  case Success(value) => println(s"Success: $value")
  case Failure(exception) => println(s"Error: ${exception.getMessage}")
}
```

Правильная обработка ошибок критична для создания надежных асинхронных приложений.

## Таймауты и отмена

**Futures** поддерживают таймауты и отмену операций:**

```scala
import scala.concurrent.duration._
import scala.concurrent.TimeoutException

val future = Future {
  Thread.sleep(5000)
  42
}

// Таймаут
val withTimeout = Future.firstCompletedOf(
  Seq(future, Future.failed(new TimeoutException()))
)

// Использование Promise для отмены
val promise = Promise[Int]()
val cancellable = Future {
  // проверка отмены
  if (promise.isCompleted) {
    throw new CancellationException()
  }
  // выполнение
  42
}
```

Таймауты и отмена позволяют контролировать выполнение долгих операций.

## Продвинутые техники конкурентности

### **ExecutionContext**

**ExecutionContext** определяет, где и как выполняются асинхронные операции.

```scala
import scala.concurrent.ExecutionContext

// Глобальный ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

// Кастомный ExecutionContext
val customEc = ExecutionContext.fromExecutor(
  java.util.concurrent.Executors.newFixedThreadPool(10)
)

// Использование
val future = Future {
  // Выполняется в customEc
  42
}(customEc)
```

### Координация **Future**

Координация нескольких **Future** позволяет создавать сложные асинхронные сценарии.

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Параллельное выполнение
val future1 = Future(operation1())
val future2 = Future(operation2())
val future3 = Future(operation3())

// Ожидание всех Future
val allResults = for {
  r1 <- future1
  r2 <- future2
  r3 <- future3
} yield (r1, r2, r3)

// Ожидание первого завершенного Future
val firstResult = Future.firstCompletedOf(Seq(future1, future2, future3))
```

### Обработка ошибок в **Future**

Обработка ошибок в **Future** позволяет создавать устойчивые асинхронные системы.

```scala
import scala.concurrent.Future
import scala.util.{Success, Failure}

// Обработка ошибок
val future = Future {
  riskyOperation()
}

future.onComplete {
  case Success(value) => println(s"Success: $value")
  case Failure(exception) => println(s"Error: ${exception.getMessage}")
}

// Восстановление после ошибок
val recovered = future.recover {
  case e: IllegalArgumentException => defaultValue
  case e: Exception => fallbackValue
}

// Повторные попытки
def retry[T](n: Int)(f: => Future[T]): Future[T] = {
  f.recoverWith {
    case e: Exception if n > 0 => retry(n - 1)(f)
    case e => Future.failed(e)
  }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Расширенные техники конкурентности

### Работа с **ExecutionContext**

**ExecutionContext** определяет, где выполняются асинхронные операции.

```scala
import scala.concurrent.ExecutionContext

// Создание кастомного ExecutionContext
val customEC = ExecutionContext.fromExecutor(
  java.util.concurrent.Executors.newFixedThreadPool(10)
)

// Использование кастомного ExecutionContext
implicit val ec: ExecutionContext = customEC

val future = Future {
  Thread.sleep(1000)
  42
}(ec)
```

### Координация множественных **Future**

Координация множественных **Future** позволяет создавать сложные асинхронные сценарии.

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Параллельное выполнение нескольких Future
val future1 = Future(compute1())
val future2 = Future(compute2())
val future3 = Future(compute3())

// Ожидание всех Future
val allResults = for {
  result1 <- future1
  result2 <- future2
  result3 <- future3
} yield (result1, result2, result3)

// Ожидание первого завершенного Future
val firstResult = Future.firstCompletedOf(Seq(future1, future2, future3))
```

### Обработка ошибок в **Future**

Правильная обработка ошибок критична для надежности.

```scala
import scala.util.{Success, Failure}

val future = Future {
  if (Random.nextBoolean()) throw new Exception("Error")
  42
}

future.onComplete {
  case Success(value) => println(s"Success: $value")
  case Failure(exception) => println(s"Failure: ${exception.getMessage}")
}

// Использование recover для обработки ошибок
val recovered = future.recover {
  case e: Exception => 0
}
```

### Практические примеры: Координация множественных **Future**

```scala
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

def fetchUser(id: Int): Future[User] = Future {
  // Асинхронное получение пользователя
  User(id, "Alice")
}

def fetchUserPosts(userId: Int): Future[List[Post]] = Future {
  // Асинхронное получение постов
  List.empty
}

def fetchUserComments(userId: Int): Future[List[Comment]] = Future {
  // Асинхронное получение комментариев
  List.empty
}

// Параллельное выполнение нескольких Future
def fetchUserData(id: Int): Future[(User, List[Post], List[Comment])] = {
  val userFuture = fetchUser(id)
  val postsFuture = fetchUserPosts(id)
  val commentsFuture = fetchUserComments(id)
  
  for {
    user <- userFuture
    posts <- postsFuture
    comments <- commentsFuture
  } yield (user, posts, comments)
}
```

### Практические примеры: Таймауты и отмена

```scala
import scala.concurrent.{Future, TimeoutException}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

def slowOperation(): Future[String] = Future {
  Thread.sleep(5000)
  "Result"
}

// Таймаут для Future
import scala.concurrent.Promise
val timeout = Promise[String]()
Future {
  Thread.sleep(1000)
  timeout.tryFailure(new TimeoutException("Operation timed out"))
}

val result = Future.firstCompletedOf(Seq(slowOperation(), timeout.future))
```

**Scala** предоставляет мощные инструменты для конкурентного программирования. Понимание **Futures**, **Promises**, параллельных коллекций, синхронизации, **ExecutionContext**, координации **Future**, обработки ошибок в **Future**, работы с **ExecutionContext**, координации множественных **Future**, обработки ошибок в **Future**, параллельного выполнения **Future**, таймаутов и отмены и их практических применений позволяет создавать эффективные, масштабируемые и надежные приложения. Правильная обработка ошибок, использование таймаутов, отмены, координации **Future**, обработки ошибок, создание кастомных **ExecutionContext**, координация множественных **Future**, использование **recover** для обработки ошибок, параллельное выполнение нескольких **Future** и использование таймаутов критичны для создания надежных систем. Конкурентность в **Scala** особенно важна для создания высокопроизводительных приложений, которые должны обрабатывать множество одновременных операций, интегрироваться с внешними системами, обеспечивать отзывчивость пользовательского интерфейса и корректно обрабатывать таймауты.

### Практические примеры: Работа с **ExecutionContext**

```scala
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.{Executors, ThreadPoolExecutor}

// Создание кастомного ExecutionContext
val threadPool = Executors.newFixedThreadPool(10)
val customExecutionContext = ExecutionContext.fromExecutor(threadPool)

// Использование кастомного ExecutionContext
val future = Future {
  Thread.sleep(1000)
  42
}(customExecutionContext)

// Закрытие пула потоков
threadPool.shutdown()
```

### Практические примеры: Работа с **Promise** для координации

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

// Использование Promise для координации нескольких Future
val promise = Promise[Int]()

val future1 = Future {
  Thread.sleep(1000)
  10
}

val future2 = Future {
  Thread.sleep(2000)
  20
}

// Завершение Promise при завершении первого Future
future1.foreach(value => promise.success(value))

// Ожидание результата
val result = promise.future
```

### Практические примеры: Работа с параллельными коллекциями

```scala
import scala.collection.parallel.CollectionConverters._

// Параллельная обработка больших коллекций
val largeList = (1 to 1000000).toList

// Параллельная обработка
val parallelResult = largeList.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .sum

// Параллельная группировка
val grouped = largeList.par.groupBy(_ % 10)
```

### Использование с различными техниками для синхронизации

```scala
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.atomic.AtomicInteger

// Использование AtomicInteger для thread-safe счетчика
val counter = new AtomicInteger(0)

val futures = (1 to 100).map { _ =>
  Future {
    counter.incrementAndGet()
  }
}

val allResults = Future.sequence(futures)
val finalCount = counter.get()  // 100
```

### Использование с различными техниками для координации

```scala
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

// Координация нескольких Future с Promise
val promises = (1 to 5).map(_ => Promise[Int]())

// Завершение из разных потоков
promises.zipWithIndex.foreach { case (promise, index) =>
  Future {
    Thread.sleep((index + 1) * 1000)
    promise.success(index)
  }
}

// Ожидание всех результатов
val allResults = Future.sequence(promises.map(_.future))
```

## Дополнительные ресурсы

**Для дальнейшего изучения конкурентности в **Scala** рекомендуется:**

- [Scala Futures Documentation](https://docs.scala-lang.org/overviews/core/futures.html)
- [Akka Documentation](https://akka.io/docs/)
