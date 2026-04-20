---
title: "Cats Effect в Scala"
description: "Краткое руководство по Cats Effect - библиотека для работы с эффектами в функциональном программировании Scala."
tags:
  - languages
  - scala
  - scala-cats-effect
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Cats Effect** в **Scala**

Краткое руководство по **Cats Effect** — библиотека для работы с эффектами в функциональном программировании **Scala**.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Scala Documentation](https://docs.scala-lang.org/)
[Scala GitHub](https://github.com/scala/scala)

## Содержание

- [Введение](#введение)
- [Основы **Cats Effect**](#основы-cats-effect)
  - [Базовое использование IO](#базовое-использование-io)
- [IO **Monad**](#io-monad)
  - [Композиция IO](#композиция-io)
  - [Обработка ошибок в IO](#обработка-ошибок-в-io)
- [Практические примеры](#практические-примеры)
  - [Работа с ресурсами](#работа-с-ресурсами)
  - [Асинхронные операции](#асинхронные-операции)
  - [Обработка времени](#обработка-времени)
- [**Best practices**](#best-practices)
  - [1. Используйте **Resource** для управления ресурсами](#1-используйте-resource-для-управления-ресурсами)
  - [2. Используйте **IO.delay** для побочных эффектов](#2-используйте-iodelay-для-побочных-эффектов)
  - [Конкурентность и синхронизация](#конкурентность-и-синхронизация)
  - [Работа с файлами](#работа-с-файлами)
  - [**HTTP** клиент с **HTTP4S**](#http-клиент-с-http4s)
  - [Обработка стримов с **FS2**](#обработка-стримов-с-fs2)
  - [Обработка с использованием **Deferred**](#обработка-с-использованием-deferred)
  - [Обработка с использованием **Queue**](#обработка-с-использованием-queue)
  - [Обработка с использованием **Fiber**](#обработка-с-использованием-fiber)
  - [Обработка с использованием **Timeout**](#обработка-с-использованием-timeout)
  - [Обработка с использованием **Race**](#обработка-с-использованием-race)
  - [Обработка с использованием **Supervisor**](#обработка-с-использованием-supervisor)
  - [Обработка с использованием **Bracket**](#обработка-с-использованием-bracket)
  - [Обработка с использованием **MonadCancel**](#обработка-с-использованием-monadcancel)
  - [Обработка с использованием **Clock**](#обработка-с-использованием-clock)
- [Интеграция с другими библиотеками](#интеграция-с-другими-библиотеками)
  - [Интеграция с **Doobie**](#интеграция-с-doobie)
  - [Интеграция с **HTTP4S**](#интеграция-с-http4s)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Интеграция с **ZIO**](#интеграция-с-zio)
  - [Использование с **Ref** для разделяемого состояния](#использование-с-ref-для-разделяемого-состояния)
  - [Использование с **Deferred** для синхронизации](#использование-с-deferred-для-синхронизации)
  - [Использование с **Queue** для обмена данными](#использование-с-queue-для-обмена-данными)
  - [Использование с **Semaphore** для ограничения параллелизма](#использование-с-semaphore-для-ограничения-параллелизма)
  - [Использование с **CountDownLatch**](#использование-с-countdownlatch)
  - [Использование с **MVar**](#использование-с-mvar)
  - [Использование с **Hotswap**](#использование-с-hotswap)
  - [Использование с **Random**](#использование-с-random)
  - [Использование с **Console**](#использование-с-console)
  - [Использование с **Dispatcher**](#использование-с-dispatcher)
  - [Использование с **Resource** для управления соединениями](#использование-с-resource-для-управления-соединениями)
  - [Использование с **Background** для фоновых задач](#использование-с-background-для-фоновых-задач)
  - [Использование с **IOApp** для приложений](#использование-с-ioapp-для-приложений)
  - [Использование с **SyncIO** для синхронных операций](#использование-с-syncio-для-синхронных-операций)
  - [Использование с **AsyncIO** для асинхронных операций](#использование-с-asyncio-для-асинхронных-операций)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Cats Effect** — это библиотека для работы с эффектами в функциональном программировании **Scala**. Она предоставляет `IO` **Monad** для описания побочных эффектов в функциональном стиле.

**Cats Effect** особенно полезен для создания чистых функциональных приложений, работы с асинхронными операциями, управления ресурсами и создания типобезопасного кода.

## Основы **Cats Effect**

### Базовое использование `IO`

```scala
import cats.effect.IO

// Создание IO
val io = IO {
  println("Hello, World!")
  42
}

// Выполнение IO
val result = io.unsafeRunSync()  // "Hello, World!" и вернет 42

// Ленивое выполнение
val lazyIO = IO.delay {
  println("Executed lazily")
  42
}
```

## `IO` **Monad**

### Композиция `IO`

```scala
import cats.effect.IO

// Композиция IO
val program = for {
  _ <- IO.println("Starting program")
  value <- IO(42)
  result <- IO(value * 2)
  _ <- IO.println(s"Result: $result")
} yield result

// Выполнение
val result = program.unsafeRunSync()
```

### Обработка ошибок в `IO`

```scala
import cats.effect.IO

// Обработка ошибок
val io = IO.raiseError(new RuntimeException("Error occurred"))

// Восстановление после ошибок
val recovered = io.handleErrorWith { error =>
  IO.println(s"Error: ${error.getMessage}") *> IO.pure(0)
}

// Обработка конкретных ошибок
val specificRecovery = io.recover {
  case e: RuntimeException => 0
}
```

## Практические примеры

### Работа с ресурсами

```scala
import cats.effect.IO
import cats.effect.Resource

// Управление ресурсами с Resource
def fileResource(path: String): Resource[IO, java.io.FileInputStream] = {
  Resource.make(
    IO(new java.io.FileInputStream(path))
  )(file => IO(file.close()))
}

// Использование ресурса
val program = fileResource("file.txt").use { file =>
  IO {
    // Работа с файлом
    file.read()
  }
}
```

### Асинхронные операции

```scala
import cats.effect.IO
import cats.effect.unsafe.implicits.global

// Асинхронные операции
def asyncOperation(input: String): IO[String] = {
  IO.async { callback =>
    // Асинхронная операция
    Future {
      callback(Right(input.toUpperCase))
    }
  }
}

// Параллельное выполнение
val parallel = IO.both(
  asyncOperation("hello"),
  asyncOperation("world")
)

// Выполнение параллельно
val result = parallel.unsafeRunSync()
```

### Обработка времени

```scala
import cats.effect.IO
import cats.effect.std.Semaphore
import scala.concurrent.duration._

// Таймауты
val io = IO.sleep(1.second) *> IO(42)
val timedIO = io.timeout(500.millis)

// Retry
def retry[A](io: IO[A], maxRetries: Int): IO[A] = {
  io.handleErrorWith { error =>
    if (maxRetries > 0) {
      IO.sleep(1.second) *> retry(io, maxRetries - 1)
    } else {
      IO.raiseError(error)
    }
  }
}
```

## **Best practices**

### 1. Используйте **Resource** для управления ресурсами

```scala
// ✅ Хорошо - автоматическое управление ресурсами
Resource.make(acquire)(release).use(program)

// ❌ Плохо - ручное управление ресурсами
val resource = acquire()
try {
  program(resource)
} finally {
  release(resource)
}
```

### 2. Используйте **IO.delay** для побочных эффектов

```scala
// ✅ Хорошо - IO.delay для побочных эффектов
val io = IO.delay {
  println("Side effect")
}

// ❌ Плохо - прямое выполнение побочных эффектов
val io = IO {
  println("Side effect")  // Выполнится сразу
}
```

### Конкурентность и синхронизация

```scala
import cats.effect.IO
import cats.effect.std.Semaphore
import cats.effect.std.Ref

// Использование Semaphore для ограничения параллелизма
def limitedConcurrency[A](
  semaphore: Semaphore[IO],
  task: IO[A]
): IO[A] = {
  semaphore.permit.use(_ => task)
}

val semaphore = Semaphore[IO](5)  // Максимум 5 одновременных операций

val tasks = (1 to 100).map { i =>
  limitedConcurrency(semaphore, IO {
    println(s"Task $i")
    Thread.sleep(100)
    i
  })
}

val result = IO.parSequenceN(10)(tasks).unsafeRunSync()

// Использование Ref для разделяемого состояния
val ref = Ref.of[IO, Int](0).unsafeRunSync()

val increment = ref.update(_ + 1)
val get = ref.get

val program = for {
  _ <- IO.parSequenceN(10)(List.fill(100)(increment))
  value <- get
} yield value

val finalValue = program.unsafeRunSync()  // 100
```

### Работа с файлами

```scala
import cats.effect.IO
import cats.effect.Resource
import fs2.io.file.{Files, Path}
import fs2.{Stream, text}

// Чтение файла через FS2
val readFile: IO[String] = {
  Files[IO]
    .readAll(Path("file.txt"))
    .through(text.utf8.decode)
    .compile
    .string
}

// Запись в файл
val writeFile: IO[Unit] = {
  Stream("Hello", "World")
    .intersperse("\n")
    .through(text.utf8.encode)
    .through(Files[IO].writeAll(Path("output.txt")))
    .compile
    .drain
}

// Копирование файла
val copyFile: IO[Unit] = {
  Files[IO]
    .readAll(Path("source.txt"))
    .through(Files[IO].writeAll(Path("dest.txt")))
    .compile
    .drain
}
```

### **HTTP** клиент с **HTTP4S**

```scala
import cats.effect.IO
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

// Создание HTTP клиента
val clientResource: Resource[IO, Client[IO]] =
  EmberClientBuilder.default[IO].build

// Выполнение HTTP запроса
val program = clientResource.use { client =>
  client.expect[String]("https://api.example.com/data")
}

val result = program.unsafeRunSync()
```

### Обработка стримов с **FS2**

```scala
import cats.effect.IO
import fs2.Stream

// Создание стрима
val stream = Stream.emit(1)
  .repeat
  .take(10)
  .map(_ * 2)
  .filter(_ > 5)

// Выполнение стрима
val result = stream.compile.toList.unsafeRunSync()

// Обработка больших файлов
val fileStream = Stream.eval(
  IO(scala.io.Source.fromFile("large-file.txt"))
).flatMap { source =>
  Stream.fromIterator(source.getLines(), 1024)
    .evalMap(line => IO(processLine(line)))
    .onFinalize(IO(source.close()))
}

val processed = fileStream.compile.toList.unsafeRunSync()
```

### Обработка с использованием **Deferred**

```scala
import cats.effect.IO
import cats.effect.std.Deferred

// Использование Deferred для синхронизации
val program = for {
  deferred <- Deferred[IO, Int]

  // Первый поток устанавливает значение
  fiber1 <- (IO.sleep(1.second) *> deferred.complete(42)).start

  // Второй поток ждет значения
  fiber2 <- deferred.get.start

  // Ожидание завершения обоих потоков
  _ <- fiber1.join
  value <- fiber2.join
} yield value

val result = program.unsafeRunSync()  // 42
```

### Обработка с использованием **Queue**

```scala
import cats.effect.IO
import cats.effect.std.Queue

// Создание очереди
val program = for {
  queue <- Queue.unbounded[IO, Int]

  // Производитель
  producer <- (1 to 10).toList.traverse(i =>
    queue.offer(i) *> IO.sleep(100.millis)
  ).start

  // Потребитель
  consumer <- Stream.repeatEval(queue.take)
    .take(10)
    .evalMap(value => IO.println(s"Received: $value"))
    .compile.drain.start

  // Ожидание завершения
  _ <- producer.join
  _ <- consumer.join
} yield ()

program.unsafeRunSync()
```

### Обработка с использованием **Fiber**

```scala
import cats.effect.IO
import cats.effect.IOApp

// Использование Fiber для конкурентного выполнения
val program = for {
  fiber1 <- (IO.sleep(1.second) *> IO.println("Task 1")).start
  fiber2 <- (IO.sleep(2.second) *> IO.println("Task 2")).start
  fiber3 <- (IO.sleep(3.second) *> IO.println("Task 3")).start

  // Ожидание всех потоков
  _ <- fiber1.join
  _ <- fiber2.join
  _ <- fiber3.join
} yield ()

program.unsafeRunSync()
```

### Обработка с использованием **Timeout**

```scala
import cats.effect.IO
import scala.concurrent.duration._

// Установка таймаута
val slowOperation = IO.sleep(5.second) *> IO(42)

val timedOperation = slowOperation.timeout(2.second)

// Обработка таймаута
val result = timedOperation.attempt.unsafeRunSync() match {
  case Left(_: java.util.concurrent.TimeoutException) =>
    println("Operation timed out")
    None
  case Right(value) => Some(value)
}
```

### Обработка с использованием **Race**

```scala
import cats.effect.IO

// Гонка между операциями
val fastOperation = IO.sleep(1.second) *> IO("Fast")
val slowOperation = IO.sleep(5.second) *> IO("Slow")

val winner = IO.race(fastOperation, slowOperation).unsafeRunSync()

winner match {
  case Left("Fast") => println("Fast operation won")
  case Right("Slow") => println("Slow operation won")
}
```

### Обработка с использованием **Supervisor**

```scala
import cats.effect.IO
import cats.effect.std.Supervisor

// Использование Supervisor для управления подзадачами
val program = Supervisor[IO].use { supervisor =>
  val tasks = (1 to 10).map { i =>
    supervisor.supervise(
      IO.sleep(i.second) *> IO.println(s"Task $i completed")
    )
  }

  IO.sequence(tasks).flatMap(_.traverse(_.join))
}

program.unsafeRunSync()
```

### Обработка с использованием **Bracket**

```scala
import cats.effect.IO

// Использование Bracket для управления ресурсами
val resource = IO {
  println("Acquiring resource")
  new java.io.FileInputStream("file.txt")
}

val release = (file: java.io.FileInputStream) => IO {
  println("Releasing resource")
  file.close()
}

val use = (file: java.io.FileInputStream) => IO {
  println("Using resource")
  file.read()
}

val program = resource.bracket(use)(release)
val result = program.unsafeRunSync()
```

### Обработка с использованием **MonadCancel**

```scala
import cats.effect.IO
import cats.effect.MonadCancel

// Использование MonadCancel для отмены операций
val cancellable = MonadCancel[IO].uncancelable { poll =>
  for {
    _ <- IO.println("Starting operation")
    result <- poll(IO.sleep(2.second) *> IO(42))
    _ <- IO.println(s"Result: $result")
  } yield result
}

val fiber = cancellable.start.unsafeRunSync()

// Отмена операции
fiber.cancel.unsafeRunSync()
```

### Обработка с использованием **Clock**

```scala
import cats.effect.IO
import cats.effect.Clock

// Использование Clock для работы со временем
val program = for {
  start <- Clock[IO].realTime
  _ <- IO.sleep(1.second)
  end <- Clock[IO].realTime
  duration = end - start
} yield duration

val elapsed = program.unsafeRunSync()
println(s"Elapsed time: ${elapsed.toMillis}ms")
```

## Интеграция с другими библиотеками

### Интеграция с **Doobie**

```scala
import cats.effect.IO
import doobie._
import doobie.implicits._

// Использование Cats Effect с Doobie
val program = for {
  xa <- Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/db",
    "user",
    "password"
  )

  result <- sql"SELECT 42".query[Int].unique.transact(xa)
} yield result

val value = program.unsafeRunSync()
```

### Интеграция с **HTTP4S**

```scala
import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.blaze.server.BlazeServerBuilder

// Создание HTTP сервера с Cats Effect
val routes = HttpRoutes.of[IO] {
  case GET -> Root / "hello" =>
    Ok("Hello, World!")
}

val server = BlazeServerBuilder[IO]
  .bindHttp(8080, "localhost")
  .withHttpApp(routes.orNotFound)
  .resource

server.use(_ => IO.never).unsafeRunSync()
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Cats Effect** — это библиотека для работы с эффектами в функциональном программировании **Scala**. Понимание `IO` **Monad**, композиции эффектов, обработки ошибок, управления ресурсами, асинхронных операций и практических применений позволяет создавать чистые, типобезопасные функциональные приложения.

Использование **Cats Effect** для описания побочных эффектов, управления ресурсами, работы с асинхронными операциями, обработки ошибок и практических применений критично для создания надежных функциональных приложений.

**Cats Effect** предоставляет мощные инструменты для работы с конкурентностью, синхронизацией, файлами, **HTTP**, стримами, **Fibers**, таймаутами, гонками, супервизорами, **Bracket**, **MonadCancel**, **Clock** и интеграцией с другими библиотеками. Понимание этих техник позволяет создавать сложные, масштабируемые функциональные приложения.

### Интеграция с **ZIO**

```scala
import cats.effect.IO
import zio.{Task, Runtime}
import zio.interop.catz._

// Конвертация между IO и Task
val io: IO[Int] = IO(42)
val task: Task[Int] = io.toZIO

val task2: Task[String] = Task("Hello")
val io2: IO[String] = task2.toIO
```

### Использование с **Ref** для разделяемого состояния

```scala
import cats.effect.IO
import cats.effect.std.Ref

// Создание Ref для разделяемого состояния
val program = for {
  ref <- Ref.of[IO, Int](0)

  // Множественные обновления
  _ <- (1 to 100).toList.traverse(i => ref.update(_ + i))

  // Получение финального значения
  value <- ref.get
} yield value

val result = program.unsafeRunSync()  // 5050
```

### Использование с **Deferred** для синхронизации

```scala
import cats.effect.IO
import cats.effect.std.Deferred

// Использование Deferred для синхронизации между Fiber
val program = for {
  deferred <- Deferred[IO, String]

  // Первый Fiber устанавливает значение
  fiber1 <- (IO.sleep(1.second) *> deferred.complete("Hello")).start

  // Второй Fiber ждет значения
  fiber2 <- deferred.get.start

  // Ожидание обоих Fiber
  _ <- fiber1.join
  value <- fiber2.join
} yield value

val result = program.unsafeRunSync()  // "Hello"
```

### Использование с **Queue** для обмена данными

```scala
import cats.effect.IO
import cats.effect.std.Queue

// Создание очереди для обмена данными между Fiber
val program = for {
  queue <- Queue.unbounded[IO, Int]

  // Производитель
  producer <- (1 to 10).toList.traverse(i =>
    queue.offer(i) *> IO.sleep(100.millis)
  ).start

  // Потребитель
  consumer <- (1 to 10).toList.traverse(_ =>
    queue.take.flatMap(value => IO.println(s"Received: $value"))
  ).start

  // Ожидание завершения
  _ <- producer.join
  _ <- consumer.join
} yield ()

program.unsafeRunSync()
```

### Использование с **Semaphore** для ограничения параллелизма

```scala
import cats.effect.IO
import cats.effect.std.Semaphore

// Ограничение параллелизма с Semaphore
val program = for {
  semaphore <- Semaphore[IO](3)  // Максимум 3 одновременных операции

  tasks = (1 to 10).map { i =>
    semaphore.permit.use(_ =>
      IO.sleep(1.second) *> IO.println(s"Task $i completed")
    )
  }

  _ <- IO.parSequenceN(10)(tasks.toList)
} yield ()

program.unsafeRunSync()
```

### Использование с **CountDownLatch**

```scala
import cats.effect.IO
import cats.effect.std.CountDownLatch

// Использование CountDownLatch для синхронизации
val program = for {
  latch <- CountDownLatch[IO](3)

  // Несколько Fiber ждут сигнала
  waiters <- (1 to 3).toList.traverse(i =>
    (latch.await *> IO.println(s"Waiter $i released")).start
  )

  // Освобождение всех ожидающих
  _ <- IO.sleep(1.second)
  _ <- latch.release
  _ <- latch.release
  _ <- latch.release

  // Ожидание завершения всех Fiber
  _ <- waiters.traverse(_.join)
} yield ()

program.unsafeRunSync()
```

### Использование с **MVar**

```scala
import cats.effect.IO
import cats.effect.std.MVar

// Использование MVar для обмена данными
val program = for {
  mvar <- MVar.empty[IO, String]

  // Писатель
  writer <- (IO.sleep(1.second) *> mvar.put("Hello")).start

  // Читатель
  reader <- mvar.take.flatMap(value => IO.println(s"Received: $value")).start

  // Ожидание завершения
  _ <- writer.join
  _ <- reader.join
} yield ()

program.unsafeRunSync()
```

### Использование с **Hotswap**

```scala
import cats.effect.IO
import cats.effect.std.Hotswap

// Использование Hotswap для динамической замены ресурсов
val program = Hotswap.create[IO, String].use { hotswap =>
  for {
    _ <- hotswap.put("Resource 1")
    value1 <- hotswap.get
    _ <- IO.println(s"Current resource: $value1")

    _ <- hotswap.swap("Resource 2")
    value2 <- hotswap.get
    _ <- IO.println(s"Current resource: $value2")
  } yield ()
}

program.unsafeRunSync()
```

### Использование с **Random**

```scala
import cats.effect.IO
import cats.effect.std.Random

// Генерация случайных чисел
val program = for {
  random <- Random.scalaUtilRandom[IO]

  int <- random.nextInt
  double <- random.nextDouble
  boolean <- random.nextBoolean

  _ <- IO.println(s"Random int: $int")
  _ <- IO.println(s"Random double: $double")
  _ <- IO.println(s"Random boolean: $boolean")
} yield ()

program.unsafeRunSync()
```

### Использование с **Console**

```scala
import cats.effect.IO
import cats.effect.std.Console

// Работа с консолью
val program = for {
  _ <- Console[IO].println("Enter your name:")
  name <- Console[IO].readLine
  _ <- Console[IO].println(s"Hello, $name!")
} yield ()

program.unsafeRunSync()
```

### Использование с **Dispatcher**

```scala
import cats.effect.IO
import cats.effect.std.Dispatcher

// Использование Dispatcher для выполнения IO из callback-based API
val program = Dispatcher[IO].use { dispatcher =>
  for {
    // Создание callback-based API
    callbackApi = new CallbackBasedAPI {
      def process(data: String, callback: String => Unit): Unit = {
        // Асинхронная обработка
        Future {
          callback(s"Processed: $data")
        }
      }
    }

    // Адаптация к IO
    result <- IO.async_[String] { callback =>
      callbackApi.process("data", result => callback(Right(result)))
    }

    _ <- IO.println(s"Result: $result")
  } yield ()
}

program.unsafeRunSync()
```

### Использование с **Resource** для управления соединениями

```scala
import cats.effect.IO
import cats.effect.Resource

// Управление пулом соединений
class ConnectionPool {
  def acquire(): IO[Connection] = IO {
    println("Acquiring connection")
    new Connection()
  }

  def release(conn: Connection): IO[Unit] = IO {
    println("Releasing connection")
    conn.close()
  }
}

val pool = new ConnectionPool()

val connectionResource: Resource[IO, Connection] =
  Resource.make(pool.acquire())(pool.release)

// Использование соединения
val program = connectionResource.use { conn =>
  IO {
    conn.query("SELECT * FROM users")
  }
}

program.unsafeRunSync()
```

### Использование с **Background** для фоновых задач

```scala
import cats.effect.IO
import cats.effect.std.Background

// Запуск фоновых задач
val program = Background[IO].use { bg =>
  for {
    // Запуск фоновой задачи
    _ <- bg.schedule(
      IO.println("Background task running") *> IO.sleep(1.second),
      5.seconds
    )

    // Основная работа
    _ <- IO.println("Main work")
    _ <- IO.sleep(10.seconds)
  } yield ()
}

program.unsafeRunSync()
```

### Использование с **IOApp** для приложений

```scala
import cats.effect.{IO, IOApp}
import cats.effect.std.Console

object MyApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- Console[IO].println("Starting application")
      result <- processData()
      _ <- Console[IO].println(s"Result: $result")
    } yield ExitCode.Success
  }

  def processData(): IO[String] = {
    IO("Processed data")
  }
}
```

### Использование с **SyncIO** для синхронных операций

```scala
import cats.effect.SyncIO

// SyncIO для синхронных операций без блокировки
val syncIO = SyncIO {
  println("Synchronous operation")
  42
}

val result = syncIO.unsafeRunSync()  // Выполняется синхронно
```

### Использование с **AsyncIO** для асинхронных операций

```scala
import cats.effect.IO
import scala.concurrent.Future

// Асинхронные операции с AsyncIO
val asyncIO = IO.async_[String] { callback =>
  Future {
    callback(Right("Async result"))
  }
}

val result = asyncIO.unsafeRunSync()
```

## Дополнительные ресурсы

- [Cats Effect Documentation](https://typelevel.org/cats-effect/)
- [Cats Effect Guide](https://typelevel.org/cats-effect/docs/getting-started)
- [FS2 Documentation](https://fs2.io/)
- [HTTP4S Documentation](https://http4s.org/)

## См. также

- [[scala-akka-streams|Akka Streams в Scala]]
- [[scala-another|Scala Additional Topics]]
- [[scala-basics|Scala: основы]]
- [[scala-collections-array|Scala Collections — Array]]
- [[scala-collections-grouping|Scala Collections — Grouping and Aggregation]]
