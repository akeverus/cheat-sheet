---
title: "Futures в Scala"
description: "Краткое руководство по Futures в Scala - асинхронное программирование и конкурентность."
tags:
  - languages
  - scala
  - scala-futures
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Futures в Scala

Краткое руководство по **Futures** в **Scala** — асинхронное программирование и конкурентность.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [Основы **Futures**](#основы-futures)
  - [Создание **Future**](#создание-future)
  - [Ожидание результата](#ожидание-результата)
- [Композиция **Futures**](#композиция-futures)
  - [**Map** и **FlatMap**](#map-и-flatmap)
  - [Параллельное выполнение](#параллельное-выполнение)
- [Обработка ошибок](#обработка-ошибок)
  - [**Recover** и **RecoverWith**](#recover-и-recoverwith)
- [**ExecutionContext**](#executioncontext)
  - [Глобальный **ExecutionContext**](#глобальный-executioncontext)
  - [Кастомный **ExecutionContext**](#кастомный-executioncontext)
  - [**ExecutionContext** для блокирующих операций](#executioncontext-для-блокирующих-операций)
- [Практические примеры](#практические-примеры)
  - [**HTTP** запросы](#http-запросы)
  - [Обработка базы данных](#обработка-базы-данных)
  - [Обработка файлов](#обработка-файлов)
  - [Кэширование с **Future**](#кэширование-с-future)
  - [**Retry** механизм](#retry-механизм)
  - [Таймауты](#таймауты)
- [Продвинутые техники](#продвинутые-техники)
  - [**Future.sequence** и **Future.traverse**](#futuresequence-и-futuretraverse)
  - [**Future.foldLeft** и **Future.reduceLeft**](#futurefoldleft-и-futurereduceleft)
  - [**Future.firstCompletedOf**](#futurefirstcompletedof)
  - [**Future.zip**](#futurezip)
- [**Best practices**](#best-practices)
  - [1. Используйте **for-comprehension** для читаемости](#1-используйте-for-comprehension-для-читаемости)
  - [2. Избегайте блокирующих операций](#2-избегайте-блокирующих-операций)
  - [3. Используйте правильный **ExecutionContext**](#3-используйте-правильный-executioncontext)
  - [4. Обрабатывайте ошибки явно](#4-обрабатывайте-ошибки-явно)
  - [5. Используйте **Future.successful** и **Future.failed** для создания уже завершенных **Futures**](#5-используйте-futuresuccessful-и-futurefailed-для-создания-уже-завершенных-futures)
  - [Использование с таймерами и задержками](#использование-с-таймерами-и-задержками)
  - [Использование для создания кастомных операций](#использование-для-создания-кастомных-операций)
  - [Использование для создания кастомных операций с таймаутом](#использование-для-создания-кастомных-операций-с-таймаутом)
  - [Использование для создания кастомных операций с **circuit breaker**](#использование-для-создания-кастомных-операций-с-circuit-breaker)
  - [Использование для создания кастомных операций с **rate limiting**](#использование-для-создания-кастомных-операций-с-rate-limiting)
  - [Использование для создания кастомных операций с кэшированием](#использование-для-создания-кастомных-операций-с-кэшированием)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Futures** в **Scala** представляют асинхронные вычисления, которые могут завершиться в будущем. **Futures** позволяют выполнять операции без блокировки основного потока, что критично для создания отзывчивых и масштабируемых приложений.

**Futures** особенно полезны для работы с I/O операциями, вызовами внешних **API**, обработки больших объемов данных и создания конкурентных приложений.

## Основы Futures

### Создание Future

```scala
// Импорт Future и глобального ExecutionContext для выполнения
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Создание Future
val future: Future[Int] = Future {
  Thread.sleep(1000)
  42
}

// Создание Future с явным ExecutionContext
val future2 = Future {
  computeValue()
}(ExecutionContext.global)
```

### Ожидание результата

```scala
// Await для блокирующего ожидания результата Future (использовать с осторожностью)
import scala.concurrent.Await
import scala.concurrent.duration._

// Ожидание результата (блокирующее)
val result = Await.result(future, 5.seconds)

// Ожидание с таймаутом
val result2 = Await.result(future, 2.seconds)
```

## Композиция Futures

### Map и FlatMap

```scala
// Map для трансформации результата
val doubled = future.map(_ * 2)

// FlatMap для композиции
val composed = future.flatMap { value =>
  Future(value * 2)
}

// For-comprehension для читаемости
val result = for {
  value <- future
  doubled <- Future(value * 2)
  tripled <- Future(doubled * 3)
} yield tripled
```

### Параллельное выполнение

```scala
// Параллельное выполнение нескольких Futures
val future1 = Future { compute1() }
val future2 = Future { compute2() }
val future3 = Future { compute3() }

// Ожидание всех результатов
val allResults = Future.sequence(List(future1, future2, future3))

// Ожидание первого завершившегося
val firstResult = Future.firstCompletedOf(List(future1, future2, future3))
```

## Обработка ошибок

### Recover и RecoverWith

```scala
// Восстановление после ошибок
val recovered = future.recover {
  case e: Exception => 0
}

// Восстановление с другим Future
val recoveredWith = future.recoverWith {
  case e: Exception => Future.successful(0)
}

// Обработка ошибок с onComplete
future.onComplete {
  case Success(value) => println(s"Success: $value")
  case Failure(e) => println(s"Error: ${e.getMessage}")
}
```

## ExecutionContext

**ExecutionContext** определяет, где и как выполняются **Futures**. Это критически важно для управления ресурсами и производительностью.

### Глобальный ExecutionContext

```scala
import scala.concurrent.ExecutionContext.Implicits.global

// Использование глобального контекста
val future = Future {
  computeValue()
}
```

### Кастомный ExecutionContext

```scala
// Собственный ExecutionContext на пуле потоков для изоляции выполнения
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

// Создание кастомного пула потоков
val executor = Executors.newFixedThreadPool(10)
val customEc = ExecutionContext.fromExecutor(executor)

val future = Future {
  computeValue()
}(customEc)
```

### ExecutionContext для блокирующих операций

```scala
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

// Отдельный ExecutionContext для блокирующих I/O операций
val blockingEc = ExecutionContext.fromExecutor(
  Executors.newCachedThreadPool()
)

def blockingIO(): Future[String] = Future {
  // Блокирующая операция
  readFromFile()
}(blockingEc)
```

## Практические примеры

### HTTP запросы

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

def fetchUrl(url: String): Future[String] = Future {
  Source.fromURL(url).mkString
}

val content = fetchUrl("https://example.com")

// Параллельные запросы
val urls = List("https://api1.com", "https://api2.com", "https://api3.com")
val contents = Future.sequence(urls.map(fetchUrl))
```

### Обработка базы данных

```scala
def findUser(id: Long): Future[Option[User]] = Future {
  // Асинхронный запрос к БД
  database.findUser(id)
}

def findUserPosts(userId: Long): Future[List[Post]] = Future {
  database.findPosts(userId)
}

// Композиция
val userWithPosts = for {
  userOpt <- findUser(1L)
  user <- Future.successful(userOpt.get)
  posts <- findUserPosts(user.id)
} yield (user, posts)

// Параллельные запросы
val userFuture = findUser(1L)
val postsFuture = findUserPosts(1L)

val combined = for {
  userOpt <- userFuture
  posts <- postsFuture
} yield (userOpt, posts)
```

### Обработка файлов

```scala
import scala.io.Source
import scala.concurrent.Future

def readFile(path: String): Future[String] = Future {
  Source.fromFile(path).mkString
}

def writeFile(path: String, content: String): Future[Unit] = Future {
  import java.nio.file.{Files, Paths}
  Files.write(Paths.get(path), content.getBytes)
}

// Параллельное чтение нескольких файлов
val files = List("file1.txt", "file2.txt", "file3.txt")
val contents = Future.sequence(files.map(readFile))
```

### Кэширование с Future

```scala
import scala.concurrent.Future
import scala.collection.mutable

class Cache[K, V] {
  private val cache = mutable.Map[K, Future[V]]()

  def get(key: K)(compute: => V): Future[V] = {
    cache.getOrElseUpdate(key, Future(compute))
  }

  def invalidate(key: K): Unit = {
    cache.remove(key)
  }
}
```

### Retry механизм

```scala
import scala.concurrent.Future
import scala.util.{Success, Failure}

def retry[T](maxRetries: Int)(f: => Future[T]): Future[T] = {
  f.recoverWith {
    case e if maxRetries > 0 =>
      Thread.sleep(1000)
      retry(maxRetries - 1)(f)
    case e => Future.failed(e)
  }
}
```

### Таймауты

```scala
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import java.util.concurrent.TimeoutException

def withTimeout[T](future: Future[T], timeout: Duration): Future[T] = {
  val promise = Promise[T]()

  Future {
    Thread.sleep(timeout.toMillis)
    promise.tryFailure(new TimeoutException("Operation timed out"))
  }

  future.onComplete(promise.tryComplete)
  promise.future
}
```

## Продвинутые техники

### Future.sequence и Future.traverse

```scala
// Future.sequence - преобразует List[Future[T]] в Future[List[T]]
val futures = List(Future(1), Future(2), Future(3))
val allResults = Future.sequence(futures)  // Future(List(1, 2, 3))

// Future.traverse - map + sequence в одном
val numbers = List(1, 2, 3)
val doubled = Future.traverse(numbers)(n => Future(n * 2))
```

### Future.foldLeft и Future.reduceLeft

```scala
val futures = List(Future(1), Future(2), Future(3))

// Свертка с начальным значением
val sum = Future.foldLeft(futures)(0)(_ + _)

// Свертка без начального значения
val product = Future.reduceLeft(futures)(_ * _)
```

### Future.firstCompletedOf

```scala
// Получение первого завершившегося Future
val futures = List(
  Future { Thread.sleep(1000); "slow" },
  Future { Thread.sleep(100); "fast" }
)

val first = Future.firstCompletedOf(futures)  // "fast"
```

### Future.zip

```scala
val future1 = Future(1)
val future2 = Future(2)

// Объединение двух Futures
val zipped = future1.zip(future2)  // Future((1, 2))
```

## Best practices

### 1. Используйте for-comprehension для читаемости

```scala
// ✅ Хорошо - читаемо
val result = for {
  user <- findUser(id)
  posts <- findUserPosts(user.id)
} yield (user, posts)

// ❌ Плохо - трудно читать
val result = findUser(id).flatMap(user =>
  findUserPosts(user.id).map(posts => (user, posts))
)
```

### 2. Избегайте блокирующих операций

```scala
// ✅ Хорошо - неблокирующее
val result = future.map(process)

// ❌ Плохо - блокирующее
val result = Await.result(future, Duration.Inf)
```

### 3. Используйте правильный ExecutionContext

```scala
// ✅ Хорошо - отдельный контекст для блокирующих операций
val blockingEc = ExecutionContext.fromExecutor(
  Executors.newCachedThreadPool()
)

def blockingIO(): Future[String] = Future {
  readFromFile()
}(blockingEc)

// ❌ Плохо - использование глобального контекста для блокирующих операций
def blockingIO(): Future[String] = Future {
  readFromFile()  // Блокирует поток из глобального пула
}
```

### 4. Обрабатывайте ошибки явно

```scala
// ✅ Хорошо - явная обработка ошибок
val result = future.recover {
  case e: TimeoutException => defaultValue
  case e: Exception => handleError(e)
}

// ❌ Плохо - игнорирование ошибок
val result = future.map(process)  // Ошибки не обрабатываются
```

### 5. Используйте Future.successful и Future.failed для создания уже завершенных Futures

```scala
// ✅ Хорошо - создание уже завершенных Futures
val success = Future.successful(42)
val failure = Future.failed(new Exception("Error"))

// ❌ Плохо - создание нового Future для уже известного значения
val success = Future { 42 }  // Неэффективно
```

### Использование с таймерами и задержками

```scala
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

// Future с задержкой
def delayedFuture[T](delay: Duration)(value: T): Future[T] = {
  Future {
    Thread.sleep(delay.toMillis)
    value
  }
}

// Использование
val future = delayedFuture(1.second)(42)
```

### Использование для создания кастомных операций

```scala
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

// Кастомная операция с retry
def retryFuture[T](maxRetries: Int, delay: Duration = 1.second)(
  f: => Future[T]
): Future[T] = {
  def attempt(retriesLeft: Int): Future[T] = {
    f.recoverWith {
      case e if retriesLeft > 0 =>
        Future {
          Thread.sleep(delay.toMillis)
          attempt(retriesLeft - 1)
        }.flatten
      case e => Future.failed(e)
    }
  }

  attempt(maxRetries)
}
```

### Использование для создания кастомных операций с таймаутом

```scala
import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

// Future с таймаутом
def withTimeout[T](future: Future[T], timeout: Duration): Future[T] = {
  val promise = Promise[T]()

  Future {
    Thread.sleep(timeout.toMillis)
    promise.tryFailure(new java.util.concurrent.TimeoutException("Operation timed out"))
  }

  future.onComplete(promise.tryComplete)
  promise.future
}
```

### Использование для создания кастомных операций с circuit breaker

```scala
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

// Circuit breaker для защиты от каскадных сбоев
class CircuitBreaker[T](
  failureThreshold: Int = 5,
  timeout: Duration = 1.second
) {
  private var failures = 0
  private var state: State = Closed

  sealed trait State
  case object Closed extends State
  case object Open extends State
  case object HalfOpen extends State

  def execute(f: => Future[T]): Future[T] = {
    state match {
      case Closed =>
        f.onComplete {
          case Success(_) => failures = 0
          case Failure(_) =>
            failures += 1
            if (failures >= failureThreshold) {
              state = Open
              Future {
                Thread.sleep(timeout.toMillis)
                state = HalfOpen
              }
            }
        }
        f

      case Open =>
        Future.failed(new Exception("Circuit breaker is open"))

      case HalfOpen =>
        f.onComplete {
          case Success(_) =>
            state = Closed
            failures = 0
          case Failure(_) =>
            state = Open
            Future {
              Thread.sleep(timeout.toMillis)
              state = HalfOpen
            }
        }
        f
    }
  }
}
```

### Использование для создания кастомных операций с rate limiting

```scala
import scala.concurrent.{Future, Promise}
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

// Rate limiter для ограничения частоты запросов
class RateLimiter(requestsPerSecond: Int) {
  private val timestamps = mutable.Queue[Long]()

  def execute[T](f: => Future[T]): Future[T] = {
    val now = System.currentTimeMillis()
    val oneSecondAgo = now - 1000

    synchronized {
      while (timestamps.nonEmpty && timestamps.head < oneSecondAgo) {
        timestamps.dequeue()
      }

      if (timestamps.size < requestsPerSecond) {
        timestamps.enqueue(now)
        f
      } else {
        val delay = 1000 - (now - timestamps.head)
        val promise = Promise[T]()
        Future {
          Thread.sleep(delay)
          execute(f).onComplete(promise.tryComplete)
        }
        promise.future
      }
    }
  }
}
```

### Использование для создания кастомных операций с кэшированием

```scala
import scala.concurrent.{Future, Promise}
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

// Кэширование Future результатов
class FutureCache[K, V] {
  private val cache = mutable.Map[K, Future[V]]()

  def get(key: K)(compute: => Future[V]): Future[V] = {
    cache.getOrElseUpdate(key, compute)
  }

  def invalidate(key: K): Unit = {
    cache.remove(key)
  }

  def clear(): Unit = {
    cache.clear()
  }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Futures** предоставляют мощные возможности для асинхронного программирования в **Scala**. Понимание создания **Futures**, композиции, обработки ошибок, параллельного выполнения и практических применений позволяет создавать эффективные, масштабируемые приложения.

Использование **Futures** для асинхронных операций, композиции, обработки ошибок, параллельного выполнения, работы с **ExecutionContext**, таймерами, задержками, **retry** механизмами, таймаутами, **circuit breaker**, **rate limiting** и кэшированием критично для создания надежных, масштабируемых асинхронных приложений.

## Дополнительные ресурсы

- [Scala Futures Documentation](https://www.scala-lang.org/api/current/scala/concurrent/Future.html)
- [Scala Concurrency](https://docs.scala-lang.org/overviews/core/futures.html)

## См. также

- [[scala-akka-streams|Akka Streams в Scala]]
- [[scala-another|Scala Additional Topics]]
- [[scala-basics|Scala: основы]]
- [[scala-cats-effect|Cats Effect в Scala]]
- [[scala-collections-array|Scala Collections — Array]]
