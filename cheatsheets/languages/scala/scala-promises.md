---
title: "Promises в Scala"
description: "Краткое руководство по Promises в Scala - создание и управление Futures."
tags:
  - languages
  - scala
  - scala-promises
type: "overview"
difficulty: "intermediate"
aliases:
  - "Promises в Scala"
  - "scala promises"
  - "Promises"
prerequisites:
  - "[[scala-futures]]"
next: []
updated: "2026-04-20"
---
# Promises в Scala

Краткое руководство по **Promises** в **Scala** — создание и управление **Futures**.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [Основы Promises](#основы-promises)
  - [Создание Promise](#создание-promise)
  - [Завершение Promise](#завершение-promise)
- [Практические примеры](#практические-примеры)
  - [Адаптация callback-based API](#адаптация-callback-based-api)
  - [Координация нескольких операций](#координация-нескольких-операций)
  - [Создание кастомных асинхронных операций](#создание-кастомных-асинхронных-операций)
  - [Реализация таймаутов](#реализация-таймаутов)
  - [Реализация retry механизма](#реализация-retry-механизма)
  - [Реализация circuit breaker](#реализация-circuit-breaker)
  - [Реализация rate limiter](#реализация-rate-limiter)
- [Продвинутые техники](#продвинутые-техники)
  - [Promise с таймаутом](#promise-с-таймаутом)
  - [Promise с условием](#promise-с-условием)
- [Best practices](#best-practices)
  - [1. Завершайте Promise только один раз](#1-завершайте-promise-только-один-раз)
  - [2. Обрабатывайте ошибки при завершении](#2-обрабатывайте-ошибки-при-завершении)
  - [3. Используйте Promise для адаптации внешних API](#3-используйте-promise-для-адаптации-внешних-api)
  - [4. Избегайте утечек памяти](#4-избегайте-утечек-памяти)
  - [5. Используйте Promise для координации](#5-используйте-promise-для-координации)
  - [Использование с таймерами и задержками](#использование-с-таймерами-и-задержками)
  - [Использование для координации нескольких Promise](#использование-для-координации-нескольких-promise)
  - [Использование для создания кастомных Future операций](#использование-для-создания-кастомных-future-операций)
  - [Использование для создания кастомных синхронизационных примитивов](#использование-для-создания-кастомных-синхронизационных-примитивов)
  - [Использование для создания кастомных барьеров](#использование-для-создания-кастомных-барьеров)
  - [Использование для создания кастомных latch](#использование-для-создания-кастомных-latch)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Promise** — это механизм для создания **Future**, который можно завершить вручную. **Promise** позволяет создавать **Future**, которые завершаются асинхронно из другого кода.

**Promises** особенно полезны для адаптации **callback-based API** к **Future-based API**, координации нескольких асинхронных операций и создания кастомных асинхронных абстракций.

## Основы Promises

### Создание Promise

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

// Создание Promise
val promise = Promise[Int]()

// Получение Future из Promise
val future: Future[Int] = promise.future
```

### Завершение Promise

```scala
// Успешное завершение
promise.success(42)

// Завершение с ошибкой
promise.failure(new Exception("Error occurred"))

// Попытка завершения (не выбрасывает исключение при повторном вызове)
promise.trySuccess(42)
promise.tryFailure(new Exception("Error"))
```

## Практические примеры

### Адаптация callback-based API

```scala
def callbackBasedApi(callback: (String, Throwable) => Unit): Unit = {
  // Асинхронная операция
  Thread.sleep(1000)
  callback("result", null)
}

// Адаптация к Future
def futureBasedApi(): Future[String] = {
  val promise = Promise[String]()

  callbackBasedApi { (result, error) =>
    if (error != null) promise.failure(error)
    else promise.success(result)
  }

  promise.future
}

// Адаптация Java CompletableFuture
import java.util.concurrent.CompletableFuture

def scalaFuture[T](javaFuture: CompletableFuture[T]): Future[T] = {
  val promise = Promise[T]()

  javaFuture.whenComplete { (result, error) =>
    if (error != null) promise.failure(error)
    else promise.success(result)
  }

  promise.future
}
```

### Координация нескольких операций

```scala
val promise1 = Promise[Int]()
val promise2 = Promise[String]()

// Завершение из разных потоков
Future {
  Thread.sleep(1000)
  promise1.success(42)
}

Future {
  Thread.sleep(2000)
  promise2.success("result")
}

// Ожидание обоих результатов
val combined = for {
  value1 <- promise1.future
  value2 <- promise2.future
} yield (value1, value2)
```

### Создание кастомных асинхронных операций

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AsyncQueue[T] {
  private val queue = scala.collection.mutable.Queue[T]()
  private val waiting = scala.collection.mutable.Queue[Promise[T]]()

  def enqueue(item: T): Unit = synchronized {
    if (waiting.nonEmpty) {
      waiting.dequeue().success(item)
    } else {
      queue.enqueue(item)
    }
  }

  def dequeue(): Future[T] = synchronized {
    val promise = Promise[T]()
    if (queue.nonEmpty) {
      promise.success(queue.dequeue())
    } else {
      waiting.enqueue(promise)
    }
    promise.future
  }
}
```

### Реализация таймаутов

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.duration._
import java.util.concurrent.TimeoutException

def withTimeout[T](future: Future[T], timeout: Duration): Future[T] = {
  val promise = Promise[T]()

  // Таймер
  Future {
    Thread.sleep(timeout.toMillis)
    promise.tryFailure(new TimeoutException("Operation timed out"))
  }

  // Оригинальный Future
  future.onComplete(promise.tryComplete)

  promise.future
}
```

### Реализация retry механизма

```scala
import scala.concurrent.{Promise, Future}
import scala.util.{Success, Failure}

def retry[T](maxRetries: Int, delay: Duration = 1.second)(
  f: => Future[T]
): Future[T] = {
  val promise = Promise[T]()

  def attempt(retriesLeft: Int): Unit = {
    f.onComplete {
      case Success(value) => promise.success(value)
      case Failure(e) if retriesLeft > 0 =>
        Future {
          Thread.sleep(delay.toMillis)
          attempt(retriesLeft - 1)
        }
      case Failure(e) => promise.failure(e)
    }
  }

  attempt(maxRetries)
  promise.future
}
```

### Реализация circuit breaker

```scala
import scala.concurrent.{Promise, Future}
import scala.util.{Success, Failure}

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
        val promise = Promise[T]()
        f.onComplete {
          case Success(value) =>
            failures = 0
            promise.success(value)
          case Failure(e) =>
            failures += 1
            if (failures >= failureThreshold) {
              state = Open
              Future {
                Thread.sleep(timeout.toMillis)
                state = HalfOpen
              }
            }
            promise.failure(e)
        }
        promise.future

      case Open =>
        Future.failed(new Exception("Circuit breaker is open"))

      case HalfOpen =>
        val promise = Promise[T]()
        f.onComplete {
          case Success(value) =>
            state = Closed
            failures = 0
            promise.success(value)
          case Failure(e) =>
            state = Open
            Future {
              Thread.sleep(timeout.toMillis)
              state = HalfOpen
            }
            promise.failure(e)
        }
        promise.future
    }
  }
}
```

### Реализация rate limiter

```scala
import scala.concurrent.{Promise, Future}
import scala.collection.mutable

class RateLimiter(requestsPerSecond: Int) {
  private val timestamps = mutable.Queue[Long]()

  def execute[T](f: => Future[T]): Future[T] = {
    val now = System.currentTimeMillis()
    val oneSecondAgo = now - 1000

    // Удаляем старые временные метки
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
```

## Продвинутые техники

### Promise с таймаутом

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.duration._

def promiseWithTimeout[T](timeout: Duration): (Promise[T], Future[T]) = {
  val promise = Promise[T]()

  Future {
    Thread.sleep(timeout.toMillis)
    promise.tryFailure(new TimeoutException("Promise timed out"))
  }

  (promise, promise.future)
}
```

### Promise с условием

```scala
import scala.concurrent.{Promise, Future}

class ConditionalPromise[T] {
  private val promise = Promise[T]()

  def completeIf(condition: => Boolean)(value: T): Boolean = {
    if (condition) {
      promise.trySuccess(value)
      true
    } else {
      false
    }
  }

  def future: Future[T] = promise.future
}
```

## Best practices

### 1. Завершайте Promise только один раз

```scala
// ✅ Хорошо - используйте trySuccess/tryFailure
promise.trySuccess(42)

// ❌ Плохо - может вызвать исключение
promise.success(42)
promise.success(43)  // Исключение!
```

### 2. Обрабатывайте ошибки при завершении

```scala
try {
  val result = computeValue()
  promise.success(result)
} catch {
  case e: Exception => promise.failure(e)
}
```

### 3. Используйте Promise для адаптации внешних API

```scala
// ✅ Хорошо - адаптация callback-based API
def adaptCallbackAPI[T](callback: (T, Throwable) => Unit): Future[T] = {
  val promise = Promise[T]()
  callback(
    result => promise.success(result),
    error => promise.failure(error)
  )
  promise.future
}
```

### 4. Избегайте утечек памяти

```scala
// ✅ Хорошо - очистка ссылок после завершения
val promise = Promise[T]()
promise.future.onComplete { _ =>
  // Очистка ресурсов
  cleanup()
}
```

### 5. Используйте Promise для координации

```scala
// ✅ Хорошо - координация нескольких асинхронных операций
val promise1 = Promise[Int]()
val promise2 = Promise[String]()

// Завершение из разных мест
operation1.onComplete(promise1.tryComplete)
operation2.onComplete(promise2.tryComplete)

// Ожидание обоих
val combined = for {
  value1 <- promise1.future
  value2 <- promise2.future
} yield (value1, value2)
```

### Использование с таймерами и задержками

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

// Promise с автоматическим таймаутом
def promiseWithTimeout[T](timeout: Duration): (Promise[T], Future[T]) = {
  val promise = Promise[T]()

  Future {
    Thread.sleep(timeout.toMillis)
    promise.tryFailure(new java.util.concurrent.TimeoutException("Promise timed out"))
  }

  (promise, promise.future)
}

// Использование
val (promise, future) = promiseWithTimeout[String](2.seconds)
Future {
  Thread.sleep(1000)
  promise.success("Result")
}

val result = future  // Future[String]
```

### Использование для координации нескольких Promise

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

// Координация нескольких Promise
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

### Использование для создания кастомных Future операций

```scala
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

// Создание кастомной Future операции
def retryFuture[T](maxRetries: Int, delay: Duration = 1.second)(
  f: => Future[T]
): Future[T] = {
  val promise = Promise[T]()

  def attempt(retriesLeft: Int): Unit = {
    f.onComplete {
      case scala.util.Success(value) => promise.trySuccess(value)
      case scala.util.Failure(e) if retriesLeft > 0 =>
        Future {
          Thread.sleep(delay.toMillis)
          attempt(retriesLeft - 1)
        }
      case scala.util.Failure(e) => promise.tryFailure(e)
    }
  }

  attempt(maxRetries)
  promise.future
}
```

### Использование для создания кастомных синхронизационных примитивов

```scala
import scala.concurrent.{Promise, Future}
import scala.collection.mutable

// Создание кастомного семафора
class CustomSemaphore(permits: Int) {
  private val waiting = mutable.Queue[Promise[Unit]]()
  private var available = permits

  def acquire(): Future[Unit] = {
    val promise = Promise[Unit]()

    synchronized {
      if (available > 0) {
        available -= 1
        promise.success(())
      } else {
        waiting.enqueue(promise)
      }
    }

    promise.future
  }

  def release(): Unit = {
    synchronized {
      if (waiting.nonEmpty) {
        val promise = waiting.dequeue()
        promise.success(())
      } else {
        available += 1
      }
    }
  }
}
```

### Использование для создания кастомных барьеров

```scala
import scala.concurrent.{Promise, Future}
import scala.collection.mutable

// Создание кастомного барьера
class CustomBarrier(count: Int) {
  private val promises = mutable.Queue[Promise[Unit]]()
  private var waiting = 0

  def await(): Future[Unit] = {
    val promise = Promise[Unit]()

    synchronized {
      promises.enqueue(promise)
      waiting += 1

      if (waiting >= count) {
        while (promises.nonEmpty) {
          promises.dequeue().success(())
        }
        waiting = 0
      }
    }

    promise.future
  }
}
```

### Использование для создания кастомных latch

```scala
import scala.concurrent.{Promise, Future}
import scala.collection.mutable

// Создание кастомного CountDownLatch
class CustomCountDownLatch(count: Int) {
  private val promise = Promise[Unit]()
  private var remaining = count

  def countDown(): Unit = {
    synchronized {
      remaining -= 1
      if (remaining == 0) {
        promise.trySuccess(())
      }
    }
  }

  def await(): Future[Unit] = {
    if (remaining == 0) {
      Future.successful(())
    } else {
      promise.future
    }
  }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Promises** предоставляют механизм для создания и управления **Futures** вручную. Понимание создания **Promises**, их завершения, адаптации **callback-based API**, координации операций и практических применений позволяет создавать гибкие асинхронные решения.

Использование **Promises** для адаптации **callback-based API**, координации нескольких асинхронных операций, создания кастомных асинхронных абстракций, реализации таймаутов, **retry** механизмов, **circuit breaker**, **rate limiter**, синхронизационных примитивов, барьеров и **latch** критично для создания надежных, масштабируемых асинхронных приложений.

## Дополнительные ресурсы

- [Scala Promises Documentation](https://www.scala-lang.org/api/current/scala/concurrent/Promise.html)

## См. также

- [Akka Streams в Scala](scala-akka-streams.md)
- [Scala Additional Topics](scala-another.md)
- [Scala: основы](scala-basics.md)
- [Cats Effect в Scala](scala-cats-effect.md)
- [Scala Collections — Array](scala-collections-array.md)
