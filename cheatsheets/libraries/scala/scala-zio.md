---
title: "ZIO"
description: "ZIO - это библиотека для функционального программирования в Scala, предоставляющая типобезопасный способ работы с эффектами, ошибками и контекстом. ZIO является альтернативой Cats Effect и других библиотек эффектов."
tags:
  - libraries
  - scala
  - scala-zio
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# ZIO

**ZIO** - это библиотека для функционального программирования в **Scala**, предоставляющая типобезопасный способ работы с эффектами, ошибками и контекстом. **ZIO** является альтернативой **Cats Effect** и других библиотек эффектов.

## Полезные ссылки

### Официальная документация
- [ZIO](https://zio.dev/) — Официальный сайт
- [ZIO GitHub](https://github.com/zio/zio) — Репозиторий проекта
- [ZIO Documentation](https://zio.dev/reference/) — Документация

### См. также
- [[scala-cats|Cats]] — **Cats** для функционального программирования
- [[scala-akka|Akka]] — **Akka** для конкурентного программирования

## Содержание

- [Основные концепции ZIO](#основные-концепции-zio)
  - [ZIO Type](#zio-type)
  - [Работа с ошибками](#работа-с-ошибками)
  - [Окружение и зависимости](#окружение-и-зависимости)
  - [Fiber - легковесные потоки](#fiber-легковесные-потоки)
  - [Schedule - повторение и retry](#schedule-повторение-и-retry)
- [ZIO Streams](#zio-streams)
  - [ZStream для работы с потоками данных](#zstream-для-работы-с-потоками-данных)
- [ZIO HTTP](#zio-http)
  - [HTTP Server с ZIO HTTP](#http-server-с-zio-http)
- [Тестирование с ZIO Test](#тестирование-с-zio-test)
  - [Unit Testing](#unit-testing)
  - [Property-based Testing](#property-based-testing)
  - [Integration Testing](#integration-testing)
- [ZIO Config](#zio-config)
  - [Configuration Management](#configuration-management)
  - [Environment Variables](#environment-variables)
- [ZIO Logging](#zio-logging)
  - [Structured Logging](#structured-logging)
- [ZIO Schema](#zio-schema)
  - [Type-safe Data Validation](#type-safe-data-validation)
- [ZIO Query](#zio-query)
  - [Compositional Data Access](#compositional-data-access)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [ZIO в **Spring** приложении](#zio-в-spring-приложении)
- [Лучшие практики](#лучшие-практики)
  - [Error Handling](#error-handling)
  - [Resource Management](#resource-management)
  - [Testing Patterns](#testing-patterns)
  - [Performance Optimization](#performance-optimization)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging ZIO Programs](#debugging-zio-programs)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Cats Effect](#from-cats-effect)
  - [From Future-based code](#from-future-based-code)
- [Experimental Features](#experimental-features)
  - [ZIO 2.x Features](#zio-2x-features)

## Основные концепции **ZIO**

### **ZIO Type**

Пример определения и использования типа **ZIO**[R, E, A] для эффектов в **Scala**.

```scala
import zio._

/
 * ZIO[R, E, A] - основной тип для работы с эффектами в ZIO
 * R - окружение (зависимости, которые требуются для выполнения)
 * E - тип ошибки (что может пойти не так)
 * A - тип успешного результата (что возвращается при успехе)
 * 
 * ZIO представляет асинхронную операцию, которая может требовать окружения,
 * может завершиться ошибкой типа E, или успешно вернуть значение типа A
 */

// Простые ZIO эффекты
// UIO[A] = ZIO[Any, Nothing, A] - эффект без окружения и без ошибок
// ZIO.succeed() - создает успешный эффект с указанным значением
val pureValue: UIO[String] = ZIO.succeed("Hello, ZIO!")
// Этот эффект всегда успешен и не требует окружения

// IO[E, A] = ZIO[Any, E, A] - эффект без окружения, но с возможной ошибкой типа E
// ZIO.fail() - создает эффект, который завершается ошибкой
val pureError: IO[String, Nothing] = ZIO.fail("Error occurred")
// Этот эффект всегда завершается ошибкой со строкой "Error occurred"

// ZIO.fromOption() - преобразует Option в ZIO
// Если Option = Some, создает успешный эффект
// Если Option = None, создает эффект с ошибкой
val fromOption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(42))
// Результат: успешный эффект со значением 42
// Если бы было None, эффект завершился бы ошибкой

// ZIO с окружением (зависимостями)
// Окружение позволяет инжектить зависимости в эффекты
trait Database {
  def getUser(id: Long): Task[User]  // Task[A] = ZIO[Any, Throwable, A]
}

// ZIO.service[T] - получение сервиса типа T из окружения
// flatMap - композиция эффектов (последовательное выполнение)
val getUserEffect: ZIO[Database, Throwable, User] =
  ZIO.service[Database].flatMap(_.getUser(1L))
// Сначала получаем Database из окружения, затем вызываем getUser(1L)
// Эффект требует Database в окружении для выполнения

// Создание ZIO эффектов из других типов
// ZIO.fromFuture - преобразует Scala Future в ZIO Task
val fromFuture: Task[Int] = ZIO.fromFuture { implicit ec =>
  import scala.concurrent.Future
  Future.successful(42)  // Future успешно возвращает 42
}
// ZIO автоматически обрабатывает асинхронность Future

// ZIO.async - создание асинхронного эффекта вручную
// callback - функция для завершения эффекта (успехом или ошибкой)
val asyncEffect: Task[String] = ZIO.async { callback =>
  // Запуск асинхронной операции в отдельном потоке
  Thread {
    Thread.sleep(100)  // Симуляция асинхронной работы
    // callback вызывается когда операция завершена
    // ZIO.succeed() - успешное завершение
    callback(ZIO.succeed("Async result"))
  }.start()
}
// Эффект завершится через 100мс со значением "Async result"

// ZIO.attemptBlocking - выполнение блокирующей операции
// Блокирующие операции выполняются в специальном пуле потоков
val blockingEffect: Task[String] = ZIO.attemptBlocking {
  // Блокирующая операция (например, чтение файла, сетевой запрос)
  Thread.sleep(1000)  // Блокирующая задержка
  "Blocking result"  // Результат после блокирующей операции
}
// ZIO автоматически использует блокирующий пул потоков для таких операций
```

### Работа с ошибками
```scala
import zio._
import zio.duration._

/
 * Работа с ошибками в ZIO
 * ZIO предоставляет мощные инструменты для обработки ошибок
 * с типобезопасностью и композицией
 */

// Типы для разных сценариев (type aliases для удобства)
// Task[A] - эффект который может выбросить Throwable (любое исключение)
type Task[+A] = ZIO[Any, Throwable, A]
// UIO[A] - эффект который не может ошибиться (Nothing в типе ошибки)
type UIO[+A] = ZIO[Any, Nothing, A]
// IO[E, A] - эффект с пользовательским типом ошибки E
type IO[+E, +A] = ZIO[Any, E, A]
// RIO[R, A] - эффект требующий окружения R, может выбросить Throwable
type RIO[-R, +A] = ZIO[R, Throwable, A]
// URIO[R, A] - эффект требующий окружения R, но не может ошибиться
type URIO[-R, +A] = ZIO[R, Nothing, A]

// Обработка ошибок
// Эффект который всегда завершается ошибкой
val riskyOperation: IO[String, Int] = ZIO.fail("Something went wrong")

// catchAll - обработка всех ошибок
// Выполняет fallback эффект при любой ошибке
val withFallback = riskyOperation.catchAll { error =>
  // error - значение ошибки (в данном случае строка "Something went wrong")
  ZIO.succeed(-1)  // fallback value - возвращаем -1 при ошибке
}
// Результат: успешный эффект со значением -1

// retry - повторение эффекта при ошибке
// Schedule.recurs(3) - повторить максимум 3 раза
val withRetry = riskyOperation.retry(Schedule.recurs(3))
// Эффект будет повторяться до 3 раз, если завершается ошибкой
// Если после 3 попыток все еще ошибка, эффект завершится ошибкой

// timeout - ограничение времени выполнения эффекта
// .some - преобразует Option[A] в A (если timeout, вернется None, затем .some уберет Option)
val withTimeout = riskyOperation.timeout(5.seconds).some
// Если эффект не завершится за 5 секунд, вернется None
// .some преобразует None в ошибку, Some(value) в успех

// mapError - преобразование типа ошибки
// Позволяет преобразовать ошибку одного типа в другой
val mappedError = riskyOperation.mapError { error =>
  // error - исходная ошибка (String)
  // Преобразуем строковую ошибку в RuntimeException
  new RuntimeException(s"Operation failed: $error")
}
// Результат: IO[RuntimeException, Int] вместо IO[String, Int]

// fold - обработка обеих веток (успех и ошибка)
// Позволяет преобразовать эффект в другой тип, обработав оба случая
val folded = riskyOperation.fold(
  error => s"Error: $error",      // Функция для обработки ошибки
  success => s"Success: $success"  // Функция для обработки успеха
)
// Результат: UIO[String] - всегда успешный эффект со строкой
// Если была ошибка: "Error: Something went wrong"
// Если был успех: "Success: <значение>"

// catchSome - частичная обработка ошибок
// Обрабатывает только определенные типы ошибок
val partialCatch = riskyOperation.catchSome {
  case "NetworkError" => ZIO.succeed(-1)
  case "TimeoutError" => ZIO.succeed(-2)
}
```

### Окружение и зависимости
```scala
import zio._

// Определение сервисов
trait UserRepository {
  def findById(id: Long): Task[Option[User]]
  def save(user: User): Task[User]
}

trait EmailService {
  def sendWelcomeEmail(email: String): Task[Unit]
}

trait Logger {
  def info(message: String): UIO[Unit]
  def error(message: String, error: Throwable): UIO[Unit]
}

// Реализация сервисов
case class UserRepositoryLive() extends UserRepository {
  def findById(id: Long): Task[Option[User]] = ZIO.succeed(Some(User(id, "John", "john@example.com")))
  def save(user: User): Task[User] = ZIO.succeed(user.copy(id = 123))
}

case class EmailServiceLive(logger: Logger) extends EmailService {
  def sendWelcomeEmail(email: String): Task[Unit] =
    logger.info(s"Sending welcome email to $email") *>
      ZIO.succeed(println(s"Email sent to $email"))
}

case class LoggerLive() extends Logger {
  def info(message: String): UIO[Unit] = ZIO.succeed(println(s"INFO: $message"))
  def error(message: String, error: Throwable): UIO[Unit] =
    ZIO.succeed(println(s"ERROR: $message - ${error.getMessage}"))
}

// ZLayers для dependency injection
val userRepositoryLive: ULayer[UserRepository] = ZLayer.succeed(UserRepositoryLive())
val loggerLive: ULayer[Logger] = ZLayer.succeed(LoggerLive())
val emailServiceLive: ZLayer[Logger, Nothing, EmailService] =
  ZLayer.fromFunction(EmailServiceLive.apply _)

// Композиция слоев
val fullLayer: ZLayer[Any, Nothing, UserRepository with EmailService with Logger] =
  userRepositoryLive ++ loggerLive >>> emailServiceLive

// Использование зависимостей
val userServiceLogic: ZIO[UserRepository with EmailService with Logger, Throwable, User] = {
  for {
    maybeUser <- ZIO.service[UserRepository].flatMap(_.findById(1L))
    user <- ZIO.fromOption(maybeUser).orElseFail(new Exception("User not found"))
    _ <- ZIO.service[EmailService].flatMap(_.sendWelcomeEmail(user.email))
    _ <- ZIO.service[Logger].flatMap(_.info(s"User processed: ${user.name}"))
  } yield user
}

// Запуск с окружением
val program = userServiceLogic.provideLayer(fullLayer)
```

### **Fiber** - легковесные потоки
```scala
import zio._

// Создание и управление fibers
val fiberProgram = for {
  // Запуск эффекта в отдельном fiber
  fiber <- ZIO.succeed("Task 1").delay(1.second).fork

  // Параллельное выполнение
  fiber2 <- ZIO.succeed("Task 2").delay(500.millis).fork

  // Ожидание результатов
  result1 <- fiber.join
  result2 <- fiber2.join

  // Interrupt fiber
  longRunningFiber <- ZIO.succeed("Long task").delay(10.seconds).fork
  _ <- longRunningFiber.interrupt.delay(1.second)

  // Race - первый завершившийся эффект
  winner <- ZIO.succeed("Fast").delay(100.millis).race(ZIO.succeed("Slow").delay(1.second))

} yield (result1, result2, winner)

// Управление группой fibers
val parallelProcessing = ZIO.foreachPar(1 to 10) { i =>
  ZIO.succeed(s"Processing item $i").delay(Random.nextInt(1000).millis)
}

// Supervisor для управления дочерними fibers
val supervisedProgram = ZIO.supervise {
  ZIO.foreachPar_(1 to 5) { i =>
    (ZIO.succeed(s"Fiber $i completed") <* ZIO.sleep(1.second))
      .onInterrupt(ZIO.succeed(s"Fiber $i interrupted"))
  }
}
```

### **Schedule** - повторение и **retry**
```scala
import zio._
import zio.Schedule._

// Базовые schedules
val fixedInterval = Schedule.fixed(1.second) // Каждую секунду
val exponentialBackoff = Schedule.exponential(100.millis, 2.0) // Экспоненциальный рост
val fibonacci = Schedule.fibonacci(100.millis) // Фибоначчи
val linear = Schedule.linear(100.millis) // Линейный рост

// Ограничения
val maxAttempts = Schedule.recurs(5) // Максимум 5 попыток
val withinTime = Schedule.spaced(1.second) && Schedule.upTo(10.seconds) // В пределах 10 секунд

// Условные schedules
val untilSuccess = Schedule.recurUntil[Exit[Throwable, String]](_.succeeded)
val whileCondition = Schedule.recurWhile[Throwable](_.isInstanceOf[TimeoutException])

// Комплексный schedule
val retrySchedule = (
  exponentialBackoff &&
  maxAttempts &&
  jittered // Добавляет jitter для избежания thundering herd
).onDecision { (decision, duration, exit) =>
  ZIO.succeed(println(s"Attempt ${decision.iteration} after ${duration.render}"))
}

// Использование schedule
val unreliableOperation = ZIO.fail("Network error").delay(Random.nextInt(500).millis)

val withRetry = unreliableOperation.retry(retrySchedule)
val withRepeat = ZIO.succeed("Success").repeat(fixedInterval.take(3))

// Schedule с состоянием
val counterSchedule = Schedule.unfold[Long](0L) { counter =>
  (counter + 1, if (counter < 5) Decision.Continue(counter + 1) else Decision.Done)
}
```

## **ZIO Streams**

### **ZStream** для работы с потоками данных
```scala
import zio.stream._

// Создание streams
val simpleStream: ZStream[Any, Nothing, Int] = ZStream(1, 2, 3, 4, 5)

val infiniteStream: ZStream[Any, Nothing, Int] = ZStream.iterate(0)(_ + 1)

val fromIterable: ZStream[Any, Nothing, Int] = ZStream.fromIterable(List(1, 2, 3, 4, 5))

val fromEffect: ZStream[Any, Nothing, String] = ZStream.fromZIO(ZIO.succeed("Hello"))

// Преобразования
val mappedStream = simpleStream.map(_ * 2) // Удвоение каждого элемента

val filteredStream = simpleStream.filter(_ % 2 == 0) // Только четные

val flatMappedStream = simpleStream.flatMap(x => ZStream(x, x * 2, x * 3))

val groupedStream = simpleStream.grouped(2) // Группировка по 2 элемента

// Агрегации
val sum: ZIO[Any, Nothing, Int] = simpleStream.runSum

val count: ZIO[Any, Nothing, Long] = simpleStream.runCount

val foldResult = simpleStream.runFold(0)(_ + _)

// Сбор в коллекции
val collected: ZIO[Any, Nothing, List[Int]] = simpleStream.runCollect

val firstElement: ZIO[Any, Nothing, Option[Int]] = simpleStream.runHead

// Эффекты в stream
val effectStream: ZStream[Any, Throwable, String] = ZStream.fromIterable(List("file1.txt", "file2.txt"))
  .mapZIO { filename =>
    ZIO.attemptBlocking {
      scala.io.Source.fromFile(filename).mkString
    }
  }

// Обработка ошибок
val withErrorHandling = simpleStream.catchAll { error =>
  ZStream("Error occurred") ++ ZStream.fail(new RuntimeException("Stream failed"))
}

// Buffering
val bufferedStream = simpleStream.buffer(10)

// Parallel processing
val parallelStream = simpleStream.mapZIOPar(4) { x =>
  ZIO.succeed(x * 2).delay(100.millis)
}

// File operations
val fileStream: ZStream[Any, Throwable, Byte] = ZStream.fromFileName("large-file.txt")

val linesFromFile: ZStream[Any, Throwable, String] = ZStream.fromFileName("data.txt")
  .transduce(ZTransducer.utf8Decode >>> ZTransducer.splitLines)

// Sink - потребители данных
val sumSink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.foldLeft(0)(_ + _)

val collectSink: ZSink[Any, Nothing, Int, Nothing, List[Int]] = ZSink.collectAll[Int]

val fileSink: ZSink[Any, Throwable, Nothing, Byte, Long] = ZSink.fromFileName("output.txt")

// Использование sinks
val result = simpleStream.run(sumSink) // ZIO[Any, Nothing, Int]
val collected = simpleStream.run(collectSink) // ZIO[Any, Nothing, List[Int]]
```

## **ZIO HTTP**

### **HTTP Server** с **ZIO HTTP**
```scala
import zio.http._
import zio.http.model.Method

// Определение routes
val healthRoute = Method.GET / "health" -> handler { (_: Request) =>
  Response.text("OK")
}

val userRoutes = Method.GET / "users" / int("id") -> handler { (id: Int, _: Request) =>
  ZIO.succeed(User(id, s"User$id", s"user$id@example.com"))
    .map(user => Response.json(user.toJson))
    .catchAll(error => Response.error(Status.InternalServerError, error.getMessage))
}

val createUserRoute = Method.POST / "users" -> handler { (req: Request) =>
  for {
    userJson <- req.body.asString
    user <- ZIO.fromEither(parseUser(userJson))
    savedUser <- userService.createUser(user)
  } yield Response.json(savedUser.toJson)
}

// Middleware
val loggingMiddleware = Middleware.requestLogging()

val authMiddleware = Middleware.customAuth { req =>
  req.headers.get("Authorization") match {
    case Some(auth) if auth.startsWith("Bearer ") => ZIO.succeed(())
    case _ => ZIO.fail(Response.unauthorized("Invalid token"))
  }
}

// Композиция приложения
val app = Routes(
  healthRoute,
  userRoutes @@ loggingMiddleware,
  createUserRoute @@ authMiddleware
) @@ Middleware.cors()

// Запуск сервера
val serverProgram = Server.serve(app).provide(
  Server.defaultWithPort(8080)
)
```

## Тестирование с **ZIO Test**

### **Unit Testing**
```scala
import zio.test._
import zio.test.Assertion._

object UserServiceSpec extends ZIOSpecDefault {

  // Определение зависимостей для тестов
  val testLayer = ZLayer.succeed(TestUserRepository())

  override def spec = suite("UserService")(

    test("should create user successfully") {
      for {
        service <- ZIO.service[UserService]
        user = CreateUserRequest("John", "john@example.com", 25)
        created <- service.createUser(user)
      } yield assert(created.name)(equalTo("John")) &&
             assert(created.email)(equalTo("john@example.com"))
    }.provide(testLayer),

    test("should fail for invalid email") {
      for {
        service <- ZIO.service[UserService]
        user = CreateUserRequest("John", "invalid-email", 25)
        result <- service.createUser(user).either
      } yield assert(result)(isLeft)
    }.provide(testLayer),

    test("should handle repository errors") {
      val failingLayer = ZLayer.succeed(FailingUserRepository())
      for {
        service <- ZIO.service[UserService]
        user = CreateUserRequest("John", "john@example.com", 25)
        result <- service.createUser(user).either
      } yield assert(result)(isLeft)
    }.provide(failingLayer)
  )
}

// Test doubles
case class TestUserRepository() extends UserRepository {
  private var users = Map.empty[Long, User]
  private var idCounter = 1L

  def findById(id: Long): Task[Option[User]] = ZIO.succeed(users.get(id))
  def save(user: User): Task[User] = ZIO.succeed {
    val newUser = user.copy(id = idCounter)
    users = users + (idCounter -> newUser)
    idCounter += 1
    newUser
  }
}

case class FailingUserRepository() extends UserRepository {
  def findById(id: Long): Task[Option[User]] = ZIO.fail(new RuntimeException("DB error"))
  def save(user: User): Task[User] = ZIO.fail(new RuntimeException("DB error"))
}
```

### **Property-based Testing**
```scala
import zio.test._
import zio.test.magnolia._

object PropertySpec extends ZIOSpecDefault {

  override def spec = suite("Property-based tests")(

    test("reverse reverse should return original") {
      check(Gen.listOf(Gen.int)) { list =>
        assert(list.reverse.reverse)(equalTo(list))
      }
    },

    test("fibonacci numbers are positive") {
      check(Gen.int(0, 100)) { n =>
        assert(fibonacci(n))(isGreaterThanEqualTo(0))
      }
    },

    test("string concatenation is associative") {
      check(Gen.string, Gen.string, Gen.string) { (a, b, c) =>
        assert((a + b) + c)(equalTo(a + (b + c)))
      }
    }
  )

  def fibonacci(n: Int): Int = {
    @tailrec
    def fib(n: Int, a: Int, b: Int): Int = {
      if (n == 0) a
      else fib(n - 1, b, a + b)
    }
    fib(n, 0, 1)
  }
}
```

### **Integration Testing**
```scala
import zio.test._
import zio.http._
import zio.http.model.Status

object HttpSpec extends ZIOSpecDefault {

  val testServer = ZLayer.scoped {
    for {
      port <- ZIO.succeed(0) // Random port
      server <- Server.serve(testApp).forkScoped
      _ <- ZIO.sleep(100.millis) // Wait for server to start
    } yield server
  }

  val testApp = Routes(
    Method.GET / "api" / "users" -> handler { (_: Request) =>
      Response.json("""[{"id":1,"name":"Test User"}]""")
    }
  )

  override def spec = suite("HTTP Integration")(

    test("should return users list") {
      for {
        client <- ZIO.service[Client]
        response <- client.request(Request.get(url"http://localhost:8080/api/users"))
        body <- response.body.asString
      } yield assert(response.status)(equalTo(Status.Ok)) &&
             assert(body)(containsString("Test User"))
    }.provide(Client.default, testServer)
  )
}
```

## **ZIO Config**

### **Configuration Management**
```scala
import zio.config._
import zio.config.magnolia._
import zio.config.typesafe.TypesafeConfigProvider

// Определение конфигурации
case class DatabaseConfig(
  host: String,
  port: Int,
  database: String,
  user: String,
  password: String
)

case class AppConfig(
  database: DatabaseConfig,
  server: ServerConfig,
  logging: LoggingConfig
)

case class ServerConfig(host: String, port: Int)
case class LoggingConfig(level: String, file: Option[String])

// Автоматическая генерация конфигуратора
implicit val databaseConfigDescriptor: ConfigDescriptor[DatabaseConfig] = DeriveConfigDescriptor.descriptor[DatabaseConfig]
implicit val serverConfigDescriptor: ConfigDescriptor[ServerConfig] = DeriveConfigDescriptor.descriptor[ServerConfig]
implicit val loggingConfigDescriptor: ConfigDescriptor[LoggingConfig] = DeriveConfigDescriptor.descriptor[LoggingConfig]
implicit val appConfigDescriptor: ConfigDescriptor[AppConfig] = DeriveConfigDescriptor.descriptor[AppConfig]

// Загрузка из HOCON
val hoconConfig =
  """
  |database {
  |  host = "localhost"
  |  port = 5432
  |  database = "myapp"
  |  user = "admin"
  |  password = "secret"
  |}
  |
  |server {
  |  host = "0.0.0.0"
  |  port = 8080
  |}
  |
  |logging {
  |  level = "INFO"
  |  file = "app.log"
  |}
  """.stripMargin

// Создание провайдера конфигурации
val configProvider = TypesafeConfigProvider.fromHoconString(hoconConfig)

// Загрузка конфигурации
val loadConfig: Task[AppConfig] = ZIO.config[AppConfig](appConfigDescriptor).provide(ZLayer.succeed(configProvider))

// Использование в программе
val program = for {
  config <- loadConfig
  _ <- ZIO.succeed(println(s"Database host: ${config.database.host}"))
  _ <- ZIO.succeed(println(s"Server port: ${config.server.port}"))
} yield ()
```

### **Environment Variables**
```scala
import zio.config._

// Конфигурация из переменных окружения
case class EnvConfig(
  appName: String,
  environment: String,
  databaseUrl: String,
  redisUrl: Option[String]
)

implicit val envConfigDescriptor: ConfigDescriptor[EnvConfig] =
  (string("APP_NAME") |@| string("ENV") |@| string("DATABASE_URL") |@| string("REDIS_URL").optional)(
    EnvConfig.apply,
    EnvConfig.unapply
  )

// Загрузка из env
val envConfigProvider = ConfigProvider.envProvider
val loadFromEnv: Task[EnvConfig] = ZIO.config[EnvConfig].provide(ZLayer.succeed(envConfigProvider))
```

## **ZIO Logging**

### **Structured Logging**
```scala
import zio.logging._
import zio.logging.slf4j.Slf4jLogger

// Настройка логгера
val slf4jLayer = Slf4jLogger.make { (context, message) =>
  val correlationId = context.get(LogAnnotation.CorrelationId)
  val level = context.get(LogAnnotation.Level)

  s"$correlationId [$level] $message"
}

// Кастомные аннотации для логов
object CustomAnnotations {
  val UserId: LogAnnotation[Long] = LogAnnotation[Long](
    name = "user_id",
    initialValue = -1L,
    combine = (_, r) => r
  )

  val RequestId: LogAnnotation[String] = LogAnnotation[String](
    name = "request_id",
    initialValue = "unknown",
    combine = (_, r) => r
  )
}

// Использование логгера
val loggingProgram = for {
  _ <- ZIO.logInfo("Application started")
  _ <- ZIO.logDebug("Debug message").when(ZIO.succeed(true))

  // Логи с контекстом
  result <- ZIO.succeed("operation result")
    @@ CustomAnnotations.UserId(123L)
    @@ CustomAnnotations.RequestId("req-456")
    @@ ZIO.logSpan("operation")

  _ <- ZIO.logInfo(s"Operation completed with result: $result")
    @@ CustomAnnotations.UserId(123L)

  // Логирование ошибок
  _ <- ZIO.fail(new RuntimeException("Something went wrong"))
    .catchAll(error => ZIO.logError(s"Operation failed: ${error.getMessage}"))

} yield result
```

## **ZIO Schema**

### **Type-safe Data Validation**
```scala
import zio.schema._

// Определение схем
case class Person(name: String, age: Int, email: String)

implicit val personSchema: Schema[Person] = DeriveSchema.gen[Person]

// Валидация данных
val validatePerson = Schema.validate[Person]

val validPerson = Person("John", 25, "john@example.com")
val invalidPerson = Person("", -5, "invalid-email")

val validResult = validatePerson(validPerson) // Right(Person(...))
val invalidResult = validatePerson(invalidPerson) // Left(ValidationError(...))

// JSON кодеки
val encoder = JsonEncoder.derive[Person]
val decoder = JsonDecoder.derive[Person]

val jsonString = """{"name":"John","age":25,"email":"john@example.com"}"""

val decoded = jsonString.fromJson[Person](decoder) // Right(Person(...))
val encoded = validPerson.toJson(encoder) // {"name":"John","age":25,"email":"john@example.com"}

// Миграции схем
val personV1Schema: Schema[PersonV1] = DeriveSchema.gen[PersonV1]
val personV2Schema: Schema[PersonV2] = DeriveSchema.gen[PersonV2]

val migration: Migration[PersonV1, PersonV2] = Migration { v1 =>
  PersonV2(v1.name, v1.age, s"${v1.name}@example.com")
}

case class PersonV1(name: String, age: Int)
case class PersonV2(name: String, age: Int, email: String)
```

## **ZIO Query**

### **Compositional Data Access**
```scala
import zio.query._

// Определение источников данных
trait UserDataSource {
  def getUserById(id: Long): ZQuery[Any, Throwable, Option[User]]
  def getUsersByIds(ids: List[Long]): ZQuery[Any, Throwable, List[User]]
  def getUserPosts(userId: Long): ZQuery[Any, Throwable, List[Post]]
}

trait PostDataSource {
  def getPostById(id: Long): ZQuery[Any, Throwable, Option[Post]]
  def getPostsByIds(ids: List[Long]): ZQuery[Any, Throwable, List[Post]]
}

// Реализация
case class User(id: Long, name: String)
case class Post(id: Long, userId: Long, title: String, content: String)

class LiveUserDataSource extends UserDataSource {
  def getUserById(id: Long): ZQuery[Any, Throwable, Option[User]] =
    ZQuery.fromZIO(ZIO.succeed(Some(User(id, s"User$id"))))

  def getUsersByIds(ids: List[Long]): ZQuery[Any, Throwable, List[User]] =
    ZQuery.collectAll(ids.map(getUserById)).map(_.flatten)

  def getUserPosts(userId: Long): ZQuery[Any, Throwable, List[Post]] =
    ZQuery.fromZIO(ZIO.succeed(List(
      Post(1, userId, "Post 1", "Content 1"),
      Post(2, userId, "Post 2", "Content 2")
    )))
}

// Композиция запросов
val getUserWithPosts: ZQuery[UserDataSource, Throwable, Option[(User, List[Post])]] = {
  for {
    userOpt <- ZQuery.service[UserDataSource].flatMap(_.getUserById(1L))
    posts <- userOpt match {
      case Some(user) =>
        ZQuery.service[UserDataSource].flatMap(_.getUserPosts(user.id))
      case None => ZQuery.succeed(List.empty[Post])
    }
  } yield userOpt.map(user => (user, posts))
}

// Батчинг запросов
val batchedUsers: ZQuery[UserDataSource, Throwable, List[User]] = {
  val userQueries = List(1L, 2L, 3L).map(id =>
    ZQuery.service[UserDataSource].flatMap(_.getUserById(id))
  )

  ZQuery.collectAll(userQueries).map(_.flatten)
}
```

## Интеграция с **Spring Boot**

### **ZIO** в **Spring** приложении
```scala
@Configuration
class ZioConfig {

  @Bean
  def runtime: Runtime[ZioEnvironment] = {
    val layer = ZLayer.wire[ZioEnvironment](
      UserRepositoryLive.layer,
      EmailServiceLive.layer,
      LoggerLive.layer
    )

    Unsafe.unsafe { implicit unsafe =>
      Runtime.unsafe.fromLayer(layer)
    }
  }

  @Bean
  def userService(runtime: Runtime[ZioEnvironment]): UserService =
    new UserServiceImpl(runtime)
}

@Service
class UserServiceImpl(runtime: Runtime[ZioEnvironment]) extends UserService {

  override def createUser(request: CreateUserRequest): Future[User] = {
    val zioProgram = for {
      user <- ZIO.fromEither(validateUser(request))
      savedUser <- userRepository.save(user)
      _ <- emailService.sendWelcomeEmail(savedUser.email)
    } yield savedUser

    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.runToFuture(zioProgram)
    }
  }
}

// ZIO сервисы
trait ZioEnvironment {
  val userRepository: UserRepository
  val emailService: EmailService
  val logger: Logger
}

case class ZioEnvironmentLive(
  userRepository: UserRepository,
  emailService: EmailService,
  logger: Logger
) extends ZioEnvironment

object ZioEnvironmentLive {
  val layer: ZLayer[Any, Nothing, ZioEnvironment] = ZLayer.succeed(
    ZioEnvironmentLive(
      UserRepositoryLive(),
      EmailServiceLive(LoggerLive()),
      LoggerLive()
    )
  )
}
```

## Лучшие практики

### **Error Handling**
```scala
// Определение domain ошибок
sealed trait DomainError extends Throwable
case class UserNotFound(id: Long) extends DomainError
case class ValidationError(message: String) extends DomainError
case class DatabaseError(cause: Throwable) extends DomainError

// Typed error channels
type UserOperation = ZIO[UserRepository, DomainError, User]
type DatabaseOperation[A] = ZIO[Database, DatabaseError, A]

// Error boundaries
val withErrorBoundary = userOperation.catchSome {
  case _: DatabaseError => ZIO.succeed(defaultUser)
}.catchAll { error =>
  logger.error("Operation failed", error) *>
  ZIO.fail(error)
}

// Refinement типов ошибок
val refinedOperation = databaseOperation.refineOrDie {
  case dbError: DatabaseError => dbError
}
```

### **Resource Management**
```scala
import zio.ZManaged

// ZManaged для управления ресурсами
val databaseConnection: ZManaged[Any, Throwable, Connection] = ZManaged.make {
  ZIO.attemptBlocking {
    // acquire connection
    DriverManager.getConnection(url, user, password)
  }
} { connection =>
  ZIO.attemptBlocking(connection.close()).orDie
}

// Использование управляемых ресурсов
val userOperation = databaseConnection.use { conn =>
  for {
    user <- getUserFromDb(conn, userId)
    posts <- getUserPostsFromDb(conn, userId)
  } yield (user, posts)
}

// Композиция ресурсов
val fullEnvironment = for {
  db <- databaseConnection
  redis <- redisConnection
  kafka <- kafkaProducer
} yield Environment(db, redis, kafka)
```

### **Testing Patterns**
```scala
// Test constructors
def testUserService(repo: UserRepository = TestUserRepository()) = {
  ZLayer.succeed(repo) >>> UserServiceLive.layer
}

// Property-based tests для ZIO
test("fibonacci properties") {
  check(Gen.int(0, 20)) { n =>
    for {
      fibN <- fibonacciZIO(n)
      fibN1 <- fibonacciZIO(n + 1)
    } yield assert(fibN + fibN1)(equalTo(fibonacci(n + 2)))
  }
}

// Integration tests
test("full user workflow") {
  ZIO.scoped {
    for {
      // Start test database
      _ <- TestDatabase.start()

      // Run business logic
      user <- userService.createUser(createUserRequest)

      // Verify side effects
      savedUser <- TestDatabase.getUser(user.id)
      emailSent <- TestEmailService.wasEmailSent(user.email)

    } yield assert(savedUser)(isSome) &&
           assert(emailSent)(isTrue)
  }
}
```

### **Performance Optimization**
```scala
// Parallel execution
val parallelOperations = ZIO.foreachPar(1 to 100) { i =>
  expensiveOperation(i)
}

// Batching
val batchedRequests = ZIO.foreach(1 to 1000) { i =>
  requests.refineOrDie {
    case _: Throwable => BatchError(i)
  }
}.grouped(100).flatMap { batch =>
  processBatch(batch)
}

// Memoization
val memoizedExpensiveCall = ZIO.memoize {
  expensiveNetworkCall()
}

// Caching with TTL
val cachedResult = Ref.make[Map[String, (Long, String)]](Map.empty).map { cache =>
  (key: String) =>
    for {
      now <- Clock.currentTime(TimeUnit.MILLISECONDS)
      cached <- cache.get.map(_.get(key))
      result <- cached match {
        case Some((timestamp, value)) if (now - timestamp) < 60000 => // 1 minute TTL
          ZIO.succeed(value)
        case _ =>
          for {
            fresh <- fetchFromDatabase(key)
            _ <- cache.update(_ + (key -> (now, fresh)))
          } yield fresh
      }
    } yield result
}
```

## Устранение неполадок

### **Common Issues**
```scala
object ZIOTroubleshooting {

  // Проблема: Stack overflow в рекурсии
  // Решение: Использовать ZIO.iterate или Eval для stack-safe рекурсии
  def safeFactorial(n: BigInt): UIO[BigInt] = {
    ZIO.iterate(BigInt(1))(n)(_ * _, (acc, _) => acc <= n)(_ * _)
  }

  // Проблема: Resource leaks
  // Решение: Всегда использовать ZManaged или .ensuring
  val leakyOperation = openFile("data.txt").flatMap { file =>
    processFile(file) // File может не закрыться при ошибке
  }

  val safeOperation = ZManaged.fromAutoCloseable(openFile("data.txt")).use { file =>
    processFile(file) // File гарантированно закроется
  }

  // Проблема: Race conditions в тестах
  // Решение: Использовать TestClock для детерминированного времени
  val timeDependentTest = test("time-based logic") {
    for {
      _ <- TestClock.adjust(1.hour)
      result <- timeBasedOperation()
    } yield assert(result)(equalTo(expected))
  }

  // Проблема: Non-deterministic tests
  // Решение: Использовать TestRandom для фиксированных значений
  val randomTest = test("random logic") {
    for {
      _ <- TestRandom.setSeed(42L)
      result <- randomOperation()
    } yield assert(result)(equalTo(expectedFixedResult))
  }

  // Проблема: Difficult debugging
  // Решение: Добавлять логирование и использовать ZIO aspects
  val loggedOperation = operation @@ ZIOAspect.logged("operation completed")

  val timedOperation = operation @@ ZIOAspect.timed @@ ZIOAspect.logged { duration =>
    s"Operation took ${duration.render}"
  }
}
```

### **Debugging ZIO Programs**
```scala
// Runtime debugging
val debugProgram = for {
  _ <- ZIO.debug("Starting operation") // Печатает в консоль
  result <- riskyOperation.tap(value => ZIO.debug(s"Got result: $value"))
  _ <- ZIO.debug(s"Final result: $result")
} yield result

// Custom aspect для трассировки
val tracingAspect = new ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] {
  override def apply[R, E, A](zio: ZIO[R, E, A])(implicit trace: Trace): ZIO[R, E, A] = {
    ZIO.debug(s"Entering: ${trace}") *>
      zio.tapError(error => ZIO.debug(s"Error in ${trace}: $error"))
  }
}

val tracedProgram = program @@ tracingAspect

// Fiber dumps для debugging concurrency
val dumpFibers = for {
  dump <- ZIO.fiberDump
  _ <- ZIO.debug(dump.prettyPrint)
} yield ()

// Stack traces
val withStackTrace = operation.stackTrace.tap { trace =>
  ZIO.debug(s"Stack trace: ${trace.prettyPrint}")
}
```

## Руководство по миграции

### **From Cats Effect**
```scala
// Cats Effect IO
import cats.effect.IO

val catsProgram: IO[Int] = for {
  x <- IO.pure(10)
  y <- IO.delay(println("Side effect"))
  z <- IO.raiseError(new Exception("error")).handleErrorWith(_ => IO.pure(0))
} yield x

// Equivalent ZIO
import zio._

val zioProgram: UIO[Int] = for {
  x <- ZIO.succeed(10)
  y <- ZIO.succeed(println("Side effect"))
  z <- ZIO.fail(new Exception("error")).catchAll(_ => ZIO.succeed(0))
} yield x

// Resource management
// Cats Effect
import cats.effect.Resource

val catsResource: Resource[IO, Connection] = Resource.make(
  IO.delay(createConnection())
)(_ => IO.delay(closeConnection()))

// ZIO
val zioResource: ZManaged[Any, Throwable, Connection] = ZManaged.make(
  ZIO.attempt(createConnection())
)(conn => ZIO.attempt(closeConnection()).orDie)
```

### **From Future-based code**
```scala
// Scala Future
import scala.concurrent.Future

def futureBasedMethod(): Future[Int] = {
  for {
    user <- getUserFromDb(userId)
    balance <- getBalanceFromCache(user.id)
  } yield balance
}

// ZIO equivalent
def zioBasedMethod(): ZIO[Database with Cache, Throwable, Int] = {
  for {
    user <- ZIO.service[Database].flatMap(_.getUser(userId))
    balance <- ZIO.service[Cache].flatMap(_.getBalance(user.id))
  } yield balance
}

// Migration helper
def fromFuture[A](future: => Future[A]): Task[A] = {
  ZIO.fromFuture(_ => future)
}
```

## **Experimental Features**

### **ZIO** 2.x **Features**
```scala
// ZIO 2.x улучшения
import zio._

// Type-level programming improvements
type Environment = UserRepository & EmailService & Logger

val program: ZIO[Environment, Throwable, User] = ???

// Better error handling
val refinedError = operation.refineToOrDie[DomainError]

// Improved streams
val zio2Stream = ZStream.fromIterable(List(1, 2, 3))
  .mapZIOParUnordered(4)(x => ZIO.succeed(x * 2))

// New concurrency primitives
val semaphore = Semaphore.make(10)
val ref = Ref.make(0)

// Improved testing
object ZIOSpec2 extends ZIOSpecDefault {
  override def spec = suite("ZIO 2.x features")(
    test("new assertions") {
      for {
        value <- ZIO.succeed(42)
      } yield assertTrue(value > 0 && value < 100)
    }
  )
}
```


## Полезные ссылки
- [Официальная документация ZIO](https://zio.dev/)
- [ZIO GitHub](https://github.com/zio/zio)
- [ZIO Examples](https://github.com/zio/zio/tree/series/2.x/examples)
- [ZIO Ecosystem](https://zio.dev/ecosystem/)
- [ZIO Discord](https://discord.gg/2ccFBr4A6j)

## См. также
- [[scala-cats|Cats]] — Альтернативная функциональная библиотека
- [[scala-akka|Akka]] — Фреймворк для конкурентного программирования
- [Паттерны](../../patterns/README.md) — Функциональные паттерны

