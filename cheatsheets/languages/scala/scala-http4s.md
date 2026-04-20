---
title: "Scala http4s"
description: "Полное руководство по http4s в Scala: функциональный HTTP клиент и сервер, маршрутизация, middleware, интеграция с Cats Effect"
tags:
  - scala
  - http4s
  - http
  - web-framework
  - functional-programming
  - cats-effect
difficulty: "advanced"
prerequisites: ["scala/scala-fp-advanced.md", "scala/scala-cats.md"]
next: []
updated: "2026-02-06"
related: ["scala/scala-fp-advanced.md", "scala/scala-cats.md", "scala/scala-play.md"]
---

# Scala http4s

Кратко: полное руководство по **http4s** в **Scala**: функциональный **HTTP** клиент и сервер, маршрутизация, **middleware**, интеграция с **Cats Effect**.

## Полезные ссылки

### Официальная документация
- [http4s Documentation](https://http4s.org/)
- [http4s GitHub](https://github.com/http4s/http4s)

### См. также
- [[scala-fp-advanced|Продвинутое функциональное программирование]]
- [[scala-cats|Cats]]
- [[scala-play|Play Framework]]

## Содержание

- [Scala http4s](#scala-http4s)
- [Введение в http4s](#введение-в-http4s)
  - [Основные характеристики](#основные-характеристики)
- [Базовые концепции](#базовые-концепции)
  - [Request и Response](#request-и-response)
  - [HttpRoutes](#httproutes)
  - [HttpApp](#httpapp)
- [HTTP сервер](#http-сервер)
  - [Создание простого сервера](#создание-простого-сервера)
  - [Конфигурация сервера](#конфигурация-сервера)
- [HTTP клиент](#http-клиент)
  - [Создание клиента](#создание-клиента)
  - [Выполнение запросов](#выполнение-запросов)
- [Маршрутизация](#маршрутизация)
  - [Базовые маршруты](#базовые-маршруты)
  - [Комбинирование маршрутов](#комбинирование-маршрутов)
  - [Вложенные маршруты](#вложенные-маршруты)
- [Middleware](#middleware)
  - [Логирование](#логирование)
  - [CORS](#cors)
  - [Аутентификация](#аутентификация)
- [Работа с JSON](#работа-с-json)
  - [Сериализация и десериализация](#сериализация-и-десериализация)
  - [Валидация JSON](#валидация-json)
- [Аутентификация и авторизация](#аутентификация-и-авторизация)
  - [JWT токены](#jwt-токены)
- [Тестирование](#тестирование)
  - [Тестирование маршрутов](#тестирование-маршрутов)
- [Лучшие практики](#лучшие-практики)
  - [Использование Resource для управления клиентами](#использование-resource-для-управления-клиентами)
  - [Обработка ошибок](#обработка-ошибок)
- [Продвинутые возможности http4s](#продвинутые-возможности-http4s)
  - [Работа с потоками данных](#работа-с-потоками-данных)
  - [Кастомные middleware](#кастомные-middleware)
  - [Работа с файлами](#работа-с-файлами)
  - [WebSockets](#websockets)
  - [Интеграция с базами данных](#интеграция-с-базами-данных)
  - [Кэширование](#кэширование)
  - [Rate Limiting](#rate-limiting)
- [Практические примеры](#практические-примеры)
  - [REST API](#rest-api)
  - [Микросервисная архитектура](#микросервисная-архитектура)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные темы](#дополнительные-темы)
  - [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Безопасность](#безопасность)
  - [Оптимизация производительности](#оптимизация-производительности)
  - [Интеграция с внешними сервисами](#интеграция-с-внешними-сервисами)
- [Заключение (расширенное)](#заключение-расширенное)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Расширенные примеры и паттерны (продолжение)](#расширенные-примеры-и-паттерны-продолжение)
  - [Работа с асинхронными операциями](#работа-с-асинхронными-операциями)
  - [Работа с заголовками](#работа-с-заголовками)
  - [Работа с cookies (расширенная)](#работа-с-cookies-расширенная)
  - [Работа с формами (расширенная)](#работа-с-формами-расширенная)
  - [Работа с WebSockets (расширенная)](#работа-с-websockets-расширенная)
  - [Интеграция с базами данных (расширенная)](#интеграция-с-базами-данных-расширенная)
  - [Кэширование (расширенное)](#кэширование-расширенное)
  - [Rate Limiting (расширенное)](#rate-limiting-расширенное)
  - [Мониторинг и метрики (расширенное)](#мониторинг-и-метрики-расширенное)
- [Заключение (финальное расширенное)](#заключение-финальное-расширенное)
- [Расширенные примеры и паттерны](#расширенные-примеры-и-паттерны)
  - [Dependency Injection](#dependency-injection)
  - [Обработка версионирования API](#обработка-версионирования-api)
  - [Работа с формами](#работа-с-формами)
  - [Работа с cookies](#работа-с-cookies)
  - [Работа с сессиями](#работа-с-сессиями)
  - [Обработка CORS (расширенная)](#обработка-cors-расширенная)
  - [Обработка ошибок (расширенная)](#обработка-ошибок-расширенная)
  - [Тестирование (расширенное)](#тестирование-расширенное)
- [Заключение (финальное)](#заключение-финальное)
- [Практические примеры использования http4s](#практические-примеры-использования-http4s)
  - [Создание функционального HTTP API с использованием http4s](#создание-функционального-http-api-с-использованием-http4s)
  - [Использование http4s для создания middleware](#использование-http4s-для-создания-middleware)
  - [Практические примеры: http4s для создания REST API](#практические-примеры-http4s-для-создания-rest-api)
  - [Практические примеры: http4s для работы с файлами](#практические-примеры-http4s-для-работы-с-файлами)
  - [Практические примеры: http4s для WebSockets](#практические-примеры-http4s-для-websockets)
  - [Практические примеры: Работа с middleware](#практические-примеры-работа-с-middleware)
  - [Практические примеры: Работа с аутентификацией](#практические-примеры-работа-с-аутентификацией)
  - [Использование с различными техниками для создания REST API](#использование-с-различными-техниками-для-создания-rest-api)
  - [Использование с различными техниками для работы с файлами](#использование-с-различными-техниками-для-работы-с-файлами)

## Введение в http4s

**http4s** — это функциональная библиотека для создания **HTTP** клиентов и серверов в **Scala**, построенная на основе **Cats Effect** и функционального программирования. **http4s** предоставляет типобезопасный и композируемый **API** для работы с **HTTP**, который следует принципам функционального программирования и позволяет создавать чистый, тестируемый и поддерживаемый код.

**http4s** основан на концепции функциональных эффектов, где все операции представлены как эффекты, которые можно комбинировать и трансформировать. Это делает код более предсказуемым и позволяет компилятору проверять корректность использования эффектов. **http4s** интегрируется с **Cats Effect**, что обеспечивает асинхронное выполнение, конкурентность, и управление ресурсами.

### Основные характеристики

- **Функциональный подход**: все операции представлены как чистые функции и эффекты. Это делает код более предсказуемым, тестируемым и композируемым. Функциональный подход позволяет создавать код, который легко понимать, тестировать и поддерживать.

- **Type-safe**: типобезопасная работа с **HTTP** запросами и ответами. **http4s** использует типы для представления **HTTP** сущностей, что позволяет компилятору проверять корректность кода на этапе компиляции. Это предотвращает ошибки, связанные с неправильным использованием **HTTP API**.

- **Композируемость**: **HTTP** сервисы и **middleware** можно легко комбинировать. Композируемость позволяет создавать сложные **HTTP** приложения из простых компонентов, что делает код более модульным и переиспользуемым.

- **Интеграция с `Cats` Effect**: асинхронное выполнение и управление ресурсами через **Cats Effect**. Интеграция с **Cats Effect** обеспечивает эффективное использование ресурсов, конкурентность, и обработку ошибок в функциональном стиле.

## Базовые концепции

### Request и Response

В **http4s HTTP** запросы и ответы представлены как неизменяемые типы `Request[F]` и `Response[F]`, где `F` - это тип эффекта (обычно `IO` из `Cats` Effect).

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

// Создание Request
val request = Request[IO](
  method = Method.GET,
  uri = uri"https://api.example.com/users"
)

// Создание Response
val response = Response[IO](
  status = Status.Ok,
  body = Stream.emit("Hello, World!")
)
```

### HttpRoutes

`HttpRoutes[F]` представляет функцию, которая принимает `Request[F]` и возвращает `OptionT[F, Response[F]]`. `OptionT` используется для представления опционального результата, что позволяет комбинировать несколько маршрутов.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "hello" =>
    Ok("Hello, World!")

  case GET -> Root / "users" / IntVar(id) =>
    Ok(s"User $id")

  case POST -> Root / "users" =>
    Ok("User created")
}
```

### HttpApp

`HttpApp[F]` представляет полное **HTTP** приложение, которое всегда возвращает `Response[F]` (в отличие от `HttpRoutes`, который возвращает `OptionT`).

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

val app: HttpApp[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "hello" =>
    Ok("Hello, World!")
}.orNotFound  // Преобразование HttpRoutes в HttpApp
```

## HTTP сервер

### Создание простого сервера

**http4s** предоставляет простой способ создания **HTTP** сервера.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeServerBuilder
import cats.effect.{IO, ExitCode}
import cats.effect.unsafe.implicits.global

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "hello" =>
    Ok("Hello, World!")
}

val server = BlazeServerBuilder[IO]
  .bindHttp(8080, "localhost")
  .withHttpApp(routes.orNotFound)
  .serve
  .compile
  .drain
  .as(ExitCode.Success)

server.unsafeRunSync()
```

### Конфигурация сервера

**http4s** позволяет настраивать различные параметры сервера.

```scala
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.duration._

val server = BlazeServerBuilder[IO]
  .bindHttp(8080, "localhost")
  .withHttpApp(routes.orNotFound)
  .withIdleTimeout(30.seconds)
  .withResponseHeaderTimeout(10.seconds)
  .withMaxConnections(100)
  .serve
  .compile
  .drain
```

## HTTP клиент

### Создание клиента

**http4s** предоставляет функциональный **API** для создания **HTTP** клиентов.

```scala
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.Client
import cats.effect.IO
import cats.effect.unsafe.implicits.global

val clientResource = BlazeClientBuilder[IO].resource

val program = clientResource.use { client =>
  val request = Request[IO](Method.GET, uri"https://api.example.com/users")
  client.expect[String](request)
}

val result = program.unsafeRunSync()
```

### Выполнение запросов

**http4s** предоставляет различные методы для выполнения **HTTP** запросов.

```scala
import org.http4s._
import org.http4s.client.Client

// Простой GET запрос
val getRequest = Request[IO](Method.GET, uri"https://api.example.com/users")
val response = client.expect[String](getRequest)

// POST запрос с телом
val postRequest = Request[IO](
  method = Method.POST,
  uri = uri"https://api.example.com/users"
).withEntity(User("Alice", "alice@example.com"))

val created = client.expect[User](postRequest)

// Обработка ответа
val result = client.run(getRequest).use { response =>
  response.status match {
    case Status.Ok => response.as[String]
    case Status.NotFound => IO.pure("Not found")
    case _ => IO.raiseError(new Exception("Unexpected status"))
  }
}
```

## Маршрутизация

### Базовые маршруты

**http4s** предоставляет **DSL** для определения маршрутов.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  // Простой GET запрос
  case GET -> Root / "hello" =>
    Ok("Hello, World!")

  // Параметры пути
  case GET -> Root / "users" / IntVar(userId) =>
    Ok(s"User $userId")

  // Несколько параметров
  case GET -> Root / "users" / IntVar(userId) / "posts" / IntVar(postId) =>
    Ok(s"Post $postId of user $userId")

  // Query параметры
  case GET -> Root / "search" :? QueryParamDecoderMatcher(query) =>
    Ok(s"Searching for: $query")
}
```

### Комбинирование маршрутов

Маршруты можно комбинировать различными способами.

```scala
import org.http4s._
import org.http4s.dsl.io._

// Определение отдельных маршрутов
val userRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" / IntVar(id) =>
    Ok(s"User $id")
}

val postRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "posts" / IntVar(id) =>
    Ok(s"Post $id")
}

// Комбинирование через <+>
val allRoutes = userRoutes <+> postRoutes

// Комбинирование с префиксом
val apiRoutes = Router(
  "/api" -> userRoutes,
  "/api" -> postRoutes
)
```

### Вложенные маршруты

**http4s** поддерживает вложенные маршруты для организации кода.

```scala
import org.http4s._
import org.http4s.dsl.io._

val userRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root =>
    Ok("Users list")

  case GET -> Root / IntVar(id) =>
    Ok(s"User $id")

  case POST -> Root =>
    Ok("User created")
}

val postRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root =>
    Ok("Posts list")

  case GET -> Root / IntVar(id) =>
    Ok(s"Post $id")
}

// Вложенные маршруты
val apiRoutes = Router(
  "/api" -> userRoutes,
  "/api" -> postRoutes
)
```

## Middleware

### Логирование

**http4s** предоставляет **middleware** для логирования запросов и ответов.

```scala
import org.http4s.server.middleware.Logger
import org.http4s.server.middleware.RequestLogger
import org.http4s.server.middleware.ResponseLogger

// Логирование запросов
val loggedRoutes = RequestLogger.httpRoutes(true, true)(routes)

// Логирование ответов
val responseLoggedRoutes = ResponseLogger.httpRoutes(true, true)(routes)

// Логирование запросов и ответов
val fullyLoggedRoutes = Logger.httpRoutes(true, true)(routes)
```

### CORS

**http4s** предоставляет **middleware** для обработки **CORS**.

```scala
import org.http4s.server.middleware.CORS
import org.http4s.headers.Origin

val corsPolicy = CORS.policy
  .withAllowOriginHost(Set(
    Origin.Host(Uri.Scheme.https, Uri.RegName("example.com"), None)
  ))
  .withAllowMethods(Set(Method.GET, Method.POST))
  .withAllowHeaders(Set("Content-Type", "Authorization"))

val corsRoutes = corsPolicy(routes)
```

### Аутентификация

**http4s** предоставляет **middleware** для аутентификации.

```scala
import org.http4s.server.middleware.authentication.BasicAuth
import org.http4s._

val authMiddleware = BasicAuth("realm", (credentials: BasicCredentials) =>
  if (credentials.username == "admin" && credentials.password == "password")
    Some(User(credentials.username))
  else
    None
)

val protectedRoutes = authMiddleware(routes)
```

## Работа с JSON

### Сериализация и десериализация

**http4s** интегрируется с **Circe** для работы с **JSON**.

```scala
import org.http4s._
import org.http4s.circe._
import io.circe._
import io.circe.generic.auto._

case class User(id: Long, name: String, email: String)

// Неявные декодеры и энкодеры
implicit val userDecoder: EntityDecoder[IO, User] = jsonOf[IO, User]
implicit val userEncoder: EntityEncoder[IO, User] = jsonEncoderOf[IO, User]

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" / IntVar(id) =>
    Ok(User(id, "Alice", "alice@example.com"))

  case req @ POST -> Root / "users" =>
    req.as[User].flatMap { user =>
      Ok(User(user.id, user.name, user.email))
    }
}
```

### Валидация JSON

**http4s** позволяет валидировать **JSON** данные.

```scala
import org.http4s._
import org.http4s.circe._
import io.circe._
import cats.syntax.all._

def validateUser(user: User): Either[String, User] = {
  if (user.name.isEmpty)
    Left("Name cannot be empty")
  else if (!user.email.contains("@"))
    Left("Invalid email")
  else
    Right(user)
}

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "users" =>
    req.as[User].flatMap { user =>
      validateUser(user) match {
        case Right(validUser) =>
          Ok(validUser)
        case Left(error) =>
          BadRequest(error)
      }
    }
}
```

## Аутентификация и авторизация

### JWT токены

**http4s** может работать с **JWT** токенами для аутентификации.

```scala
import org.http4s._
import org.http4s.dsl.io._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

def generateToken(userId: Long): String = {
  val claim = JwtClaim(
    content = s"""{"userId":$userId}""",
    expiration = Some(System.currentTimeMillis() / 1000 + 3600)
  )
  Jwt.encode(claim, "secret", JwtAlgorithm.HS256)
}

def validateToken(token: String): Option[Long] = {
  Jwt.decode(token, "secret", Seq(JwtAlgorithm.HS256)).toOption.flatMap { claim =>
    claim.content.toLongOption
  }
}

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case POST -> Root / "login" =>
    Ok(generateToken(1L))

  case req @ GET -> Root / "profile" =>
    req.headers.get[org.http4s.headers.Authorization].flatMap { authHeader =>
      val token = authHeader.value.replace("Bearer ", "")
      validateToken(token).map { userId =>
        Ok(s"User $userId profile")
      }
    }.getOrElse(Unauthorized())
}
```

## Тестирование

### Тестирование маршрутов

**http4s** предоставляет инструменты для тестирования маршрутов.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.scalatest.funspec.AnyFunSpec
import cats.effect.IO

class RoutesSpec extends AnyFunSpec {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" =>
      Ok("Hello, World!")
  }

  describe("Routes") {
    it("should return hello") {
      val request = Request[IO](Method.GET, uri"/hello")
      val response = routes.orNotFound.run(request).unsafeRunSync()

      assert(response.status == Status.Ok)
      assert(response.as[String].unsafeRunSync() == "Hello, World!")
    }
  }
}
```

## Лучшие практики

### Использование Resource для управления клиентами

**http4s** рекомендует использовать `Resource` для управления жизненным циклом клиентов.

```scala
import org.http4s.client.blaze.BlazeClientBuilder
import cats.effect.{IO, Resource}

val clientResource: Resource[IO, Client[IO]] = BlazeClientBuilder[IO].resource

val program = clientResource.use { client =>
  val request = Request[IO](Method.GET, uri"https://api.example.com/users")
  client.expect[String](request)
}
```

### Обработка ошибок

**http4s** предоставляет различные способы обработки ошибок.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" / IntVar(id) =>
    findUser(id).flatMap {
      case Some(user) => Ok(user)
      case None => NotFound("User not found")
    }
}.handleErrorWith { error =>
  HttpRoutes.of[IO] {
    case _ => InternalServerError(error.getMessage)
  }
}
```

## Продвинутые возможности http4s

### Работа с потоками данных

**http4s** поддерживает работу с потоками данных через `fs2.Stream`.

```scala
import org.http4s._
import org.http4s.dsl.io._
import fs2.Stream
import cats.effect.IO

// Отправка потока данных
val streamRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "stream" =>
    val dataStream = Stream.range(1, 1000)
      .map(i => s"Data point $i\n")
      .through(fs2.text.utf8.encode)

    Ok(dataStream)
}

// Прием потока данных
val receiveStreamRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "stream" =>
    req.body
      .through(fs2.text.utf8.decode)
      .evalMap(line => IO(processLine(line)))
      .compile
      .drain
      .flatMap(_ => Ok("Stream processed"))
}
```

### Кастомные middleware

**http4s** позволяет создавать кастомные **middleware** для обработки запросов и ответов.

```scala
import org.http4s._
import org.http4s.server._
import cats.effect.IO

def timingMiddleware(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  Kleisli { req =>
    val start = System.currentTimeMillis()
    service(req).map { responseOption =>
      responseOption.map { response =>
        val duration = System.currentTimeMillis() - start
        response.putHeaders("X-Response-Time" -> s"${duration}ms")
      }
    }
  }
}

val timedRoutes = timingMiddleware(routes)
```

### Работа с файлами

**http4s** поддерживает работу с файлами для загрузки и скачивания.

```scala
import org.http4s._
import org.http4s.dsl.io._
import java.nio.file.Paths

// Загрузка файла
val uploadRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "upload" =>
    req.body
      .through(fs2.io.file.Files[IO].writeAll(Paths.get("/tmp/uploaded")))
      .compile
      .drain
      .flatMap(_ => Ok("File uploaded"))
}

// Скачивание файла
val downloadRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "download" / filename =>
    val filePath = Paths.get(s"/tmp/$filename")
    Ok(fs2.io.file.Files[IO].readAll(filePath))
}
```

### WebSockets

**http4s** поддерживает **WebSockets** для двусторонней связи.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.websocket.WebSocketBuilder
import fs2.Stream

val websocketRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "ws" =>
    val send: Stream[IO, WebSocketFrame] = Stream.awakeEvery[IO](1.second)
      .map(_ => WebSocketFrame.Text("Hello"))

    val receive: Pipe[IO, WebSocketFrame, Unit] = _.evalMap { frame =>
      IO(println(s"Received: $frame"))
    }

    WebSocketBuilder[IO].build(send, receive)
}
```

### Интеграция с базами данных

**http4s** интегрируется с базами данных через различные библиотеки.

```scala
import org.http4s._
import org.http4s.dsl.io._
import doobie._
import doobie.implicits._

val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/mydb",
  "user",
  "password"
)

val dbRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" / IntVar(id) =>
    val query = sql"SELECT * FROM users WHERE id = $id"
      .query[User]
      .option

    query.transact(xa).flatMap {
      case Some(user) => Ok(user)
      case None => NotFound()
    }
}
```

### Кэширование

**http4s** поддерживает кэширование ответов для улучшения производительности.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.Caching
import java.util.concurrent.TimeUnit

val cachePolicy = Caching.publicCache[IO](java.time.Duration.of(1, TimeUnit.HOURS))

val cachedRoutes = cachePolicy(routes)
```

### Rate Limiting

**http4s** позволяет ограничивать частоту запросов.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.std.Semaphore

def rateLimitMiddleware(maxConcurrent: Int)(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  Semaphore[IO](maxConcurrent).flatMap { semaphore =>
    Kleisli { req =>
      semaphore.permit.use { _ =>
        service(req)
      }
    }
  }
}

val rateLimitedRoutes = rateLimitMiddleware(10)(routes)
```

## Практические примеры

### REST API

Создание полноценного **REST API** с **http4s**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.generic.auto._

case class User(id: Long, name: String, email: String)

implicit val userDecoder: EntityDecoder[IO, User] = jsonOf[IO, User]
implicit val userEncoder: EntityEncoder[IO, User] = jsonEncoderOf[IO, User]

val userRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  // GET /users - список пользователей
  case GET -> Root / "users" =>
    Ok(List(
      User(1, "Alice", "alice@example.com"),
      User(2, "Bob", "bob@example.com")
    ))

  // GET /users/:id - получить пользователя
  case GET -> Root / "users" / LongVar(id) =>
    findUser(id).flatMap {
      case Some(user) => Ok(user)
      case None => NotFound()
    }

  // POST /users - создать пользователя
  case req @ POST -> Root / "users" =>
    req.as[User].flatMap { user =>
      createUser(user).flatMap(created => Created(created))
    }

  // PUT /users/:id - обновить пользователя
  case req @ PUT -> Root / "users" / LongVar(id) =>
    req.as[User].flatMap { user =>
      updateUser(id, user).flatMap {
        case Some(updated) => Ok(updated)
        case None => NotFound()
      }
    }

  // DELETE /users/:id - удалить пользователя
  case DELETE -> Root / "users" / LongVar(id) =>
    deleteUser(id).flatMap {
      case true => NoContent()
      case false => NotFound()
    }
}
```

### Микросервисная архитектура

**http4s** идеально подходит для создания микросервисов.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.client.Client

// Сервис пользователей
val userService: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" / LongVar(id) =>
    Ok(User(id, "Alice", "alice@example.com"))
}

// Сервис заказов
val orderService: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "orders" / LongVar(id) =>
    Ok(Order(id, 1L, 100.0))
}

// API Gateway
val gateway: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ GET -> Root / "api" / "users" / rest =>
    userService.orNotFound.run(req.withUri(uri"/users" / rest))

  case req @ GET -> Root / "api" / "orders" / rest =>
    orderService.orNotFound.run(req.withUri(uri"/orders" / rest))
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**http4s** предоставляет мощный функциональный **API** для создания **HTTP** клиентов и серверов в **Scala**. Понимание базовых концепций, создания серверов и клиентов, маршрутизации, **middleware**, работы с **JSON**, аутентификации и авторизации, тестирования, работы с потоками данных, файлами, **WebSockets**, интеграции с базами данных, кэширования, **rate limiting**, и создания **REST API** и микросервисов позволяет создавать надежные, масштабируемые и поддерживаемые **HTTP** приложения. **http4s** особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой **Cats Effect**, и требуют высокой производительности и надежности.

## Дополнительные темы

### Мониторинг и метрики

**http4s** интегрируется с системами мониторинга для сбора метрик.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.Metrics

// Сбор метрик
val metricsRoutes = Metrics[IO](registry)(routes)

// Кастомные метрики
def metricsMiddleware(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  Kleisli { req =>
    val start = System.nanoTime()
    service(req).map { responseOption =>
      responseOption.map { response =>
        val duration = System.nanoTime() - start
        recordMetric("request_duration", duration)
        recordMetric("request_count", 1)
        response
      }
    }
  }
}
```

### Безопасность

**http4s** предоставляет инструменты для обеспечения безопасности приложений.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.HSTS

// HSTS заголовки
val secureRoutes = HSTS(routes)

// Защита от CSRF
val csrfRoutes = CSRF(routes, csrfToken)

// Валидация входных данных
def validateInput(input: String): Either[String, String] = {
  if (input.length > 100)
    Left("Input too long")
  else if (input.contains("<script>"))
    Left("Invalid input")
  else
    Right(input)
}
```

### Оптимизация производительности

**http4s** предоставляет различные техники для оптимизации производительности.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.GZip

// Сжатие ответов
val compressedRoutes = GZip(routes)

// Кэширование статических ресурсов
val staticRoutes = StaticFile.fromResource("/static", None).getOrElseF(NotFound())

// Параллельная обработка
val parallelRoutes = routes.mapK(IO.parTraverse(_))
```

### Интеграция с внешними сервисами

**http4s** позволяет интегрироваться с внешними сервисами через **HTTP** клиент.

```scala
import org.http4s._
import org.http4s.client.Client
import org.http4s.dsl.io._

def externalServiceRoutes(client: Client[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "external" / "data" =>
    val request = Request[IO](Method.GET, uri"https://api.external.com/data")
    client.expect[String](request).flatMap(Ok(_))
}

// Circuit breaker для устойчивости
def resilientRoutes(client: Client[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "resilient" / "data" =>
    val request = Request[IO](Method.GET, uri"https://api.external.com/data")
    client.expect[String](request)
      .timeout(5.seconds)
      .retry(3)
      .flatMap(Ok(_))
      .handleErrorWith(_ => ServiceUnavailable())
}
```

## Заключение (расширенное)

**http4s** предоставляет мощный функциональный **API** для создания **HTTP** клиентов и серверов в **Scala**. Понимание всех аспектов **http4s**, от базовых концепций до продвинутых возможностей, позволяет создавать надежные, масштабируемые и поддерживаемые **HTTP** приложения. **http4s** особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой **Cats Effect**, и требуют высокой производительности, надежности и безопасности.

Ключевые преимущества **http4s** включают функциональный подход к программированию, типобезопасность, композируемость, интеграцию с **Cats Effect**, поддержку потоков данных, работу с файлами и **WebSockets**, интеграцию с базами данных, кэширование, **rate limiting**, мониторинг, безопасность, и оптимизацию производительности. Эти преимущества делают **http4s** идеальным выбором для создания современных **HTTP** приложений, микросервисов, и **REST API**.

## Дополнительные ресурсы

**Для дальнейшего изучения **http4s** рекомендуется:**

- [http4s Documentation](https://http4s.org/)
- [http4s Examples](https://http4s.org/v1/docs/)
- [Cats Effect Documentation](https://typelevel.org/cats-effect/)
- [http4s Tutorial](https://http4s.org/v1/docs/)
- [http4s Best Practices](https://http4s.org/v1/docs/)

## Расширенные примеры и паттерны (продолжение)

### Работа с асинхронными операциями

**http4s** поддерживает асинхронные операции через **Cats Effect**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import cats.effect.unsafe.implicits.global

// Асинхронная обработка запросов
val asyncRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "async" / "data" =>
    val asyncData = IO.async_[String] { cb =>
      // Асинхронная операция
      fetchDataFromExternalService().onComplete {
        case Success(data) => cb(Right(data))
        case Failure(e) => cb(Left(e))
      }
    }
    asyncData.flatMap(Ok(_))
}

// Параллельная обработка нескольких запросов
val parallelRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "parallel" =>
    val data1 = fetchData1()
    val data2 = fetchData2()
    val data3 = fetchData3()

    (data1, data2, data3).parMapN { (d1, d2, d3) =>
      s"Data1: $d1, Data2: $d2, Data3: $d3"
    }.flatMap(Ok(_))
}
```

### Работа с заголовками

**http4s** предоставляет удобные способы работы с **HTTP** заголовками.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers._

// Чтение заголовков
val headerRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ GET -> Root / "headers" =>
    val userAgent = req.headers.get[`User-Agent`].map(_.value)
    val accept = req.headers.get[Accept].map(_.mediaRanges)
    val contentType = req.headers.get[`Content-Type`].map(_.mediaType)

    Ok(s"User-Agent: $userAgent, Accept: $accept, Content-Type: $contentType")
}

// Установка заголовков
val setHeaderRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "custom-header" =>
    Ok("Response with custom header")
      .map(_.putHeaders(
        Header("X-Custom-Header", "custom-value"),
        Header("X-Request-ID", java.util.UUID.randomUUID().toString)
      ))
}
```

### Работа с cookies (расширенная)

**http4s** предоставляет расширенные возможности для работы с **cookies**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers.Cookie
import org.http4s.circe._

// Установка cookies с параметрами
val cookieRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "set-cookie" =>
    Ok("Cookie set").map(_.addCookie(
      ResponseCookie(
        name = "session",
        content = "abc123",
        maxAge = Some(3600),
        path = Some("/"),
        domain = Some("example.com"),
        secure = true,
        httpOnly = true,
        sameSite = Some(SameSite.Strict)
      )
    ))

  // Чтение cookies
  case req @ GET -> Root / "get-cookie" =>
    req.headers.get[Cookie].flatMap { cookie =>
      cookie.values.find(_.name == "session").map { sessionCookie =>
        Ok(s"Session: ${sessionCookie.content}")
      }
    }.getOrElse(Ok("No session cookie"))

  // Удаление cookies
  case GET -> Root / "delete-cookie" =>
    Ok("Cookie deleted").map(_.removeCookie("session"))
}
```

### Работа с формами (расширенная)

**http4s** поддерживает расширенную работу с **HTML** формами.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.UrlForm

// Обработка multipart форм
val multipartRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "upload" =>
    req.decode[Multipart[IO]] { multipart =>
      multipart.parts.traverse { part =>
        part.name match {
          case Some("file") =>
            part.body.compile.toVector.flatMap { bytes =>
              saveFile(part.filename.getOrElse("unknown"), bytes.toArray)
            }
          case Some("description") =>
            part.body.through(fs2.text.utf8.decode).compile.string.flatMap { desc =>
              saveDescription(desc)
            }
          case _ => IO.unit
        }
      }.flatMap(_ => Ok("File uploaded"))
    }
}

// Валидация форм
def validateForm(form: UrlForm): Either[String, User] = {
  val name = form.getFirst("name").getOrElse("")
  val email = form.getFirst("email").getOrElse("")
  val age = form.getFirst("age").flatMap(_.toIntOption)

  if (name.isEmpty) Left("Name is required")
  else if (email.isEmpty || !email.contains("@")) Left("Invalid email")
  else if (age.isEmpty || age.get < 0) Left("Invalid age")
  else Right(User(0, name, email, age))
}

val validatedFormRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "form" =>
    req.as[UrlForm].flatMap { form =>
      validateForm(form) match {
        case Right(user) => Ok(user)
        case Left(error) => BadRequest(error)
      }
    }
}
```

### Работа с WebSockets (расширенная)

**http4s** предоставляет расширенные возможности для работы с **WebSockets**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.websocket.WebSocketBuilder
import fs2.Stream
import fs2.concurrent.Topic

// WebSocket с broadcast
val websocketRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "ws" / "broadcast" =>
    Topic[IO, String].flatMap { topic =>
      val send: Stream[IO, WebSocketFrame] = topic.subscribe(100).map(WebSocketFrame.Text(_))
      val receive: Pipe[IO, WebSocketFrame, Unit] = _.evalMap {
        case WebSocketFrame.Text(text, _) => topic.publish1(text)
        case _ => IO.unit
      }
      WebSocketBuilder[IO].build(send, receive)
    }

// WebSocket с heartbeat
val heartbeatRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "ws" / "heartbeat" =>
    val send: Stream[IO, WebSocketFrame] = Stream.awakeEvery[IO](30.seconds)
      .map(_ => WebSocketFrame.Ping())
    val receive: Pipe[IO, WebSocketFrame, Unit] = _.evalMap {
      case WebSocketFrame.Pong(_) => IO(println("Pong received"))
      case _ => IO.unit
    }
    WebSocketBuilder[IO].build(send, receive)
}
```

### Интеграция с базами данных (расширенная)

**http4s** интегрируется с различными библиотеками для работы с базами данных.

```scala
import org.http4s._
import org.http4s.dsl.io._
import doobie._
import doobie.implicits._
import cats.effect.IO

// Использование Doobie с http4s
def userRoutes(xa: Transactor[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" / LongVar(id) =>
    sql"SELECT id, name, email FROM users WHERE id = $id"
      .query[User]
      .option
      .transact(xa)
      .flatMap {
        case Some(user) => Ok(user)
        case None => NotFound()
      }

  case req @ POST -> Root / "users" =>
    req.as[User].flatMap { user =>
      sql"INSERT INTO users (name, email) VALUES (${user.name}, ${user.email})"
        .update
        .withUniqueGeneratedKeys[Long]("id")
        .transact(xa)
        .flatMap(id => Created(user.copy(id = id)))
    }

  case req @ PUT -> Root / "users" / LongVar(id) =>
    req.as[User].flatMap { user =>
      sql"UPDATE users SET name = ${user.name}, email = ${user.email} WHERE id = $id"
        .update
        .run
        .transact(xa)
        .flatMap { rowsAffected =>
          if (rowsAffected > 0) Ok(user.copy(id = id))
          else NotFound()
        }
    }

  case DELETE -> Root / "users" / LongVar(id) =>
    sql"DELETE FROM users WHERE id = $id"
      .update
      .run
      .transact(xa)
      .flatMap { rowsAffected =>
        if (rowsAffected > 0) NoContent()
        else NotFound()
      }
}
```

### Кэширование (расширенное)

**http4s** поддерживает различные стратегии кэширования.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.Caching
import java.util.concurrent.TimeUnit
import java.time.Duration

// Публичное кэширование
val publicCache = Caching.publicCache[IO](Duration.of(1, TimeUnit.HOURS))
val cachedRoutes = publicCache(routes)

// Приватное кэширование
val privateCache = Caching.privateCache[IO](Duration.of(30, TimeUnit.MINUTES))
val privateCachedRoutes = privateCache(routes)

// Кэширование с условиями
def conditionalCache(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  Kleisli { req =>
    val cacheKey = req.uri.toString
    val cached = getFromCache(cacheKey)

    cached match {
      case Some(response) if !isStale(response) =>
        OptionT.pure(response)
      case _ =>
        service(req).map { response =>
          cacheResponse(cacheKey, response)
          response
        }
    }
  }
}
```

### Rate Limiting (расширенное)

**http4s** позволяет реализовывать различные стратегии **rate limiting**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.std.Semaphore
import cats.effect.Ref

// Token Bucket алгоритм
class TokenBucket(maxTokens: Int, refillRate: Int) {
  private val tokens = Ref.of[IO, Int](maxTokens).unsafeRunSync()
  private val lastRefill = Ref.of[IO, Long](System.currentTimeMillis()).unsafeRunSync()

  def acquire(): IO[Boolean] = {
    for {
      now <- IO(System.currentTimeMillis())
      last <- lastRefill.get
      elapsed = (now - last) / 1000
      refilled = (elapsed * refillRate).toInt
      current <- tokens.get
      newTokens = math.min(maxTokens, current + refilled)
      acquired <- if (newTokens > 0) {
        tokens.update(_ - 1).as(true)
      } else {
        IO.pure(false)
      }
      _ <- lastRefill.set(now)
    } yield acquired
  }
}

def rateLimitMiddleware(bucket: TokenBucket)(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  Kleisli { req =>
    bucket.acquire().flatMap { allowed =>
      if (allowed) service(req)
      else OptionT.pure(Response[IO](Status.TooManyRequests))
    }
  }
}
```

### Мониторинг и метрики (расширенное)

**http4s** интегрируется с системами мониторинга для сбора метрик.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.Ref

// Сбор метрик запросов
class MetricsCollector {
  private val requestCount = Ref.of[IO, Long](0).unsafeRunSync()
  private val errorCount = Ref.of[IO, Long](0).unsafeRunSync()
  private val responseTime = Ref.of[IO, List[Long]](Nil).unsafeRunSync()

  def recordRequest(duration: Long, status: Status): IO[Unit] = {
    for {
      _ <- requestCount.update(_ + 1)
      _ <- if (status.code >= 400) errorCount.update(_ + 1) else IO.unit
      _ <- responseTime.update(duration :: _)
    } yield ()
  }

  def getMetrics(): IO[Metrics] = {
    for {
      requests <- requestCount.get
      errors <- errorCount.get
      times <- responseTime.get
    } yield Metrics(
      totalRequests = requests,
      totalErrors = errors,
      averageResponseTime = if (times.nonEmpty) times.sum / times.length else 0
    )
  }
}

def metricsMiddleware(collector: MetricsCollector)(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  Kleisli { req =>
    val start = System.nanoTime()
    service(req).map { responseOption =>
      responseOption.map { response =>
        val duration = (System.nanoTime() - start) / 1000000
        collector.recordRequest(duration, response.status).unsafeRunSync()
        response
      }
    }
  }
}
```

## Заключение (финальное расширенное)

**http4s** предоставляет мощный и гибкий функциональный **API** для создания **HTTP** клиентов и серверов в **Scala**. Понимание всех аспектов **http4s**, от базовых концепций до продвинутых возможностей, паттернов, и практических примеров, позволяет создавать надежные, масштабируемые, поддерживаемые и безопасные **HTTP** приложения. **http4s** особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой **Cats Effect**, и требуют высокой производительности, надежности, безопасности, и тестируемости.

Ключевые преимущества **http4s** включают функциональный подход к программированию, типобезопасность, композируемость, интеграцию с **Cats Effect**, поддержку потоков данных, работу с файлами и **WebSockets**, интеграцию с базами данных, кэширование, **rate limiting**, мониторинг, безопасность, оптимизацию производительности, **dependency injection**, версионирование **API**, работу с формами, **cookies** и сессиями, расширенную обработку **CORS** и ошибок, асинхронные операции, работу с заголовками, расширенную работу с формами и **WebSockets**, интеграцию с базами данных, расширенное кэширование и **rate limiting**, и мощные инструменты тестирования и мониторинга. Эти преимущества делают **http4s** идеальным выбором для создания современных **HTTP** приложений, микросервисов, **REST API**, и распределенных систем, которые требуют высокой производительности, надежности, безопасности, масштабируемости, и наблюдаемости.

## Расширенные примеры и паттерны

### Dependency Injection

**http4s** поддерживает **dependency injection** через различные подходы.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

// Использование Reader Monad для DI
case class AppConfig(apiKey: String, databaseUrl: String)

def userRoutes(config: AppConfig): HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "users" =>
    Ok(s"Users with API key: ${config.apiKey}")
}

// Использование с Resource
def createApp(config: AppConfig): Resource[IO, HttpApp[IO]] = {
  Resource.pure(userRoutes(config).orNotFound)
}
```

### Обработка версионирования API

**http4s** позволяет создавать версионированные **API**.

```scala
import org.http4s._
import org.http4s.dsl.io._

val v1Routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "v1" / "users" =>
    Ok("V1 users")
}

val v2Routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "v2" / "users" =>
    Ok("V2 users")
}

val versionedRoutes = v1Routes <+> v2Routes
```

### Работа с формами

**http4s** поддерживает работу с **HTML** формами.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.UrlForm

val formRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case req @ POST -> Root / "form" =>
    req.as[UrlForm].flatMap { form =>
      val name = form.getFirst("name").getOrElse("Unknown")
      val email = form.getFirst("email").getOrElse("Unknown")
      Ok(s"Name: $name, Email: $email")
    }
}
```

### Работа с cookies

**http4s** поддерживает работу с **cookies**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers.Cookie

val cookieRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "set-cookie" =>
    Ok("Cookie set").map(_.addCookie(ResponseCookie("session", "abc123")))

  case req @ GET -> Root / "get-cookie" =>
    req.headers.get[Cookie].flatMap { cookie =>
      cookie.values.find(_.name == "session").map { sessionCookie =>
        Ok(s"Session: ${sessionCookie.content}")
      }
    }.getOrElse(Ok("No session cookie"))
}
```

### Работа с сессиями

**http4s** позволяет работать с сессиями для хранения состояния.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.middleware.Session

val sessionRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root / "session" / "set" / value =>
    Ok("Session set").map(_.withSession("key" -> value))

  case req @ GET -> Root / "session" / "get" =>
    req.session.get("key") match {
      case Some(value) => Ok(s"Session value: $value")
      case None => Ok("No session value")
    }
}
```

### Обработка CORS (расширенная)

**http4s** предоставляет расширенные возможности для работы с **CORS**.

```scala
import org.http4s._
import org.http4s.server.middleware.CORS
import org.http4s.headers.Origin

val corsPolicy = CORS.policy
  .withAllowOriginHost(Set(
    Origin.Host(Uri.Scheme.https, Uri.RegName("example.com"), None),
    Origin.Host(Uri.Scheme.https, Uri.RegName("api.example.com"), None)
  ))
  .withAllowMethods(Set(Method.GET, Method.POST, Method.PUT, Method.DELETE))
  .withAllowHeaders(Set("Content-Type", "Authorization", "X-Requested-With"))
  .withExposeHeaders(Set("X-Total-Count"))
  .withMaxAge(1.day)

val corsRoutes = corsPolicy(routes)
```

### Обработка ошибок (расширенная)

**http4s** предоставляет расширенные возможности для обработки ошибок.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

def errorHandlingMiddleware(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  service.handleErrorWith {
    case e: IllegalArgumentException =>
      HttpRoutes.of[IO] {
        case _ => BadRequest(e.getMessage)
      }
    case e: NoSuchElementException =>
      HttpRoutes.of[IO] {
        case _ => NotFound(e.getMessage)
      }
    case e: Exception =>
      HttpRoutes.of[IO] {
        case _ => InternalServerError(e.getMessage)
      }
  }
}

val errorHandledRoutes = errorHandlingMiddleware(routes)
```

### Тестирование (расширенное)

**http4s** предоставляет расширенные инструменты для тестирования.

```scala
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.scalatest.funspec.AnyFunSpec
import cats.effect.IO

class ExtendedRoutesSpec extends AnyFunSpec {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "users" / LongVar(id) =>
      Ok(User(id, "Alice", "alice@example.com"))

    case req @ POST -> Root / "users" =>
      req.as[User].flatMap { user =>
        Created(user)
      }
  }

  describe("Extended Routes") {
    it("should return user by id") {
      val request = Request[IO](Method.GET, uri"/users/1")
      val response = routes.orNotFound.run(request).unsafeRunSync()

      assert(response.status == Status.Ok)
      val user = response.as[User].unsafeRunSync()
      assert(user.id == 1L)
      assert(user.name == "Alice")
    }

    it("should create user") {
      val user = User(0, "Bob", "bob@example.com")
      val request = Request[IO](Method.POST, uri"/users")
        .withEntity(user)
      val response = routes.orNotFound.run(request).unsafeRunSync()

      assert(response.status == Status.Created)
      val created = response.as[User].unsafeRunSync()
      assert(created.name == "Bob")
    }
  }
}
```

## Заключение (финальное)

**http4s** предоставляет мощный и гибкий функциональный **API** для создания **HTTP** клиентов и серверов в **Scala**. Понимание всех аспектов **http4s**, от базовых концепций до продвинутых возможностей, паттернов, и практических примеров, позволяет создавать надежные, масштабируемые, поддерживаемые и безопасные **HTTP** приложения. **http4s** особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой **Cats Effect**, и требуют высокой производительности, надежности, безопасности, и тестируемости.

## Практические примеры использования http4s

### Создание функционального HTTP API с использованием http4s

**http4s** позволяет создавать функциональные и композируемые **HTTP API**.

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

// Определение маршрутов
val routes = HttpRoutes.of[IO] {
  case GET -> Root / "users" / IntVar(id) =>
    getUserById(id).flatMap {
      case Some(user) => Ok(user.asJson)
      case None => NotFound()
    }

  case POST -> Root / "users" =>
    req => req.as[User].flatMap { user =>
      createUser(user).flatMap(created => Created(created.asJson))
    }
}
```

### Использование http4s для создания middleware

**http4s** поддерживает создание **middleware** для обработки запросов.

```scala
import org.http4s.server.middleware._

// Middleware для логирования
val loggedRoutes = Logger.httpRoutes(logHeaders = true, logBody = true)(routes)

// Middleware для CORS
val corsRoutes = CORS.policy.withAllowOriginAll(loggedRoutes)
```

### Практические примеры: http4s для создания REST API

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

// REST API с http4s
val routes = HttpRoutes.of[IO] {
  case GET -> Root / "users" / LongVar(id) =>
    getUser(id).flatMap {
      case Some(user) => Ok(user.asJson)
      case None => NotFound()
    }

  case POST -> Root / "users" :? request =>
    request.as[User].flatMap { user =>
      createUser(user).flatMap(Ok(_))
    }

  case PUT -> Root / "users" / LongVar(id) :? request =>
    request.as[User].flatMap { user =>
      updateUser(id, user).flatMap(Ok(_))
    }

  case DELETE -> Root / "users" / LongVar(id) =>
    deleteUser(id).flatMap(NoContent())
}
```

### Практические примеры: http4s для работы с файлами

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import fs2.Stream

// Загрузка файлов
val fileRoutes = HttpRoutes.of[IO] {
  case req @ POST -> Root / "upload" =>
    req.body.compile.to(Array).flatMap { data =>
      saveFile(data).flatMap(Ok(_))
    }

  case GET -> Root / "download" / fileName =>
    readFile(fileName).flatMap { data =>
      Ok(Stream.emits(data))
        .withContentType(`Content-Type`(MediaType.application.octetStream))
    }
}
```

### Практические примеры: http4s для WebSockets

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import fs2.Stream

val websocketRoutes = HttpRoutes.of[IO] {
  case GET -> Root / "ws" =>
    WebSocketBuilder[IO].build(
      receive = _.evalMap { msg =>
        IO.println(s"Received: $msg") *> IO(msg)
      },
      send = Stream.emit(Text("Connected"))
    )
}
```

### Практические примеры: Работа с middleware

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

// Middleware для логирования
val loggingMiddleware = org.http4s.server.middleware.Logger[IO](
  logHeaders = true,
  logBody = true
)

// Middleware для CORS
val corsMiddleware = org.http4s.server.middleware.CORS.policy
  .withAllowOriginAll
  .withAllowMethodsAll
  .withAllowHeadersAll

// Применение middleware
val routes = HttpRoutes.of[IO] {
  case GET -> Root / "hello" => Ok("Hello, World!")
}

val app = loggingMiddleware(corsMiddleware(routes))
```

### Практические примеры: Работа с аутентификацией

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO

// Middleware для аутентификации
def authMiddleware(service: HttpRoutes[IO]): HttpRoutes[IO] = {
  AuthedRoutes.of[String, IO] {
    case GET -> Root / "protected" as user =>
      Ok(s"Hello, $user!")
  }.local(req => req.headers.get("Authorization").map(_.value).getOrElse("anonymous"))
}

val protectedRoutes = authMiddleware(HttpRoutes.of[IO] {
  case req => Ok("Protected resource")
})
```

Ключевые преимущества **http4s** включают функциональный подход к программированию, типобезопасность, композируемость, интеграцию с **Cats Effect**, поддержку потоков данных, работу с файлами и **WebSockets**, интеграцию с базами данных, кэширование, **rate limiting**, мониторинг, безопасность, оптимизацию производительности, **dependency injection**, версионирование **API**, работу с формами, **cookies** и сессиями, расширенную обработку **CORS** и ошибок, создание **REST API**, работу с файлами, **WebSockets** и мощные инструменты тестирования. Эти преимущества делают **http4s** идеальным выбором для создания современных **HTTP** приложений, микросервисов, **REST API**, распределенных систем, файловых сервисов и приложений реального времени.

### Использование с различными техниками для создания REST API

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._

case class User(id: Long, name: String, email: String)

val userRoutes = HttpRoutes.of[IO] {
  case GET -> Root / "users" / LongVar(id) =>
    findUser(id).flatMap {
      case Some(user) => Ok(user.asJson)
      case None => NotFound()
    }

  case req @ POST -> Root / "users" =>
    req.as[User].flatMap { user =>
      createUser(user).flatMap(Ok(_))
    }
}
```

### Использование с различными техниками для работы с файлами

```scala
import org.http4s._
import org.http4s.dsl.io._
import cats.effect.IO
import fs2.Stream

// Загрузка файлов
val fileRoutes = HttpRoutes.of[IO] {
  case req @ POST -> Root / "upload" =>
    req.body.compile.to(Array).flatMap { data =>
      saveFile(data).flatMap(Ok(_))
    }

  case GET -> Root / "download" / fileName =>
    readFile(fileName).flatMap { data =>
      Ok(Stream.emits(data))
        .withContentType(`Content-Type`(MediaType.application.octetStream))
    }
}
```

## Дополнительные ресурсы

**Для дальнейшего изучения **http4s** рекомендуется:**

- [http4s Documentation](https://http4s.org/)
- [http4s Tutorial](https://http4s.org/v1/docs/)
