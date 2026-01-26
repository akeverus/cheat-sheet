# Play Framework

Play Framework - это высокопроизводительный веб-фреймворк для Scala и Java, построенный на Akka HTTP. Предоставляет реактивную архитектуру, отличную поддержку асинхронного программирования и богатый набор инструментов для разработки современных веб-приложений.

## Содержание

- [Основы Play Framework](#основы-play-framework)
  - [Структура проекта](#структура-проекта)
  - [Контроллеры](#контроллеры)
  - [Модели и JSON](#модели-и-json)
  - [Сервисы и Dependency Injection](#сервисы-и-dependency-injection)
  - [Маршруты](#маршруты)
  - [Шаблоны (Twirl)](#шаблоны-twirl)
  - [Конфигурация](#конфигурация)
- [Работа с базами данных](#работа-с-базами-данных)
  - [Slick](#slick)
  - [Anorm](#anorm)
- [Аутентификация и авторизация](#аутентификация-и-авторизация)
  - [Silhouette](#silhouette)
- [WebSockets и Server-Sent Events](#websockets-и-server-sent-events)
  - [WebSocket Controller](#websocket-controller)
  - [Server-Sent Events](#server-sent-events)
- [Тестирование](#тестирование)
  - [Unit тесты](#unit-тесты)
  - [Integration тесты](#integration-тесты)
  - [E2E тесты с Selenium](#e2e-тесты-с-selenium)
- [Фильтры и Middleware](#фильтры-и-middleware)
  - [Кастомные фильтры](#кастомные-фильтры)
  - [CORS фильтр](#cors-фильтр)
- [Работа с файлами](#работа-с-файлами)
  - [Загрузка файлов](#загрузка-файлов)
- [Асинхронные задачи](#асинхронные-задачи)
  - [Akka Scheduler](#akka-scheduler)
- [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Kamon](#kamon)
- [Лучшие практики](#лучшие-практики)
  - [Структура проекта](#структура-проекта-1)
  - [Обработка ошибок](#обработка-ошибок)
  - [Безопасность](#безопасность)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Performance Tuning](#performance-tuning)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Play 2.7 to 2.8](#from-play-27-to-28)
  - [From Java to Scala](#from-java-to-scala)

## Основы Play Framework

### Структура проекта
```
my-play-app/
├── app/
│   ├── controllers/     # Контроллеры
│   ├── models/         # Модели данных
│   ├── services/       # Бизнес-логика
│   ├── views/          # Шаблоны (Twirl)
│   └── utils/          # Утилиты
├── conf/
│   ├── application.conf # Конфигурация
│   └── routes           # Маршруты
├── public/              # Статические ресурсы
├── test/               # Тесты
└── build.sbt           # Сборка
```

### Контроллеры
```scala
package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
 * Контроллер в Play Framework
 * @Singleton - аннотация для создания singleton экземпляра
 * @Inject() - dependency injection через конструктор
 * BaseController - базовый класс для контроллеров Play
 */
@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,  // Компоненты контроллера (для Action, Results)
  userService: UserService                         // Сервис для бизнес-логики (инжектируется)
)(implicit ec: ExecutionContext) extends BaseController {
  // implicit ec: ExecutionContext - контекст выполнения для асинхронных операций
  // Необходим для работы с Future

  // Синхронное действие - блокирующее выполнение
  // Action { } - создание синхронного действия
  // implicit request - неявный параметр запроса (используется для получения данных запроса)
  def index() = Action { implicit request: Request[AnyContent] =>
    // Ok() - HTTP 200 ответ с телом
    Ok("Hello, Play!")
    // Возвращает Result с HTTP статусом 200 и текстовым телом
  }

  // Асинхронное действие - неблокирующее выполнение
  // Action.async { } - создание асинхронного действия
  // Возвращает Future[Result] вместо Result
  def listUsers() = Action.async { implicit request =>
    // userService.getAllUsers() - возвращает Future[List[User]]
    // .map { } - преобразование результата Future
    userService.getAllUsers().map { users =>
      // Json.toJson() - преобразование Scala объекта в JSON
      // Ok() - HTTP 200 ответ с JSON телом
      Ok(Json.toJson(users))
    }
    // Результат: Future[Result] с JSON массивом пользователей
  }

  // Действие с параметрами пути
  // id: Long - параметр из URL пути (например, /users/123)
  def getUser(id: Long) = Action.async { implicit request =>
    // userService.getUser(id) - возвращает Future[Option[User]]
    userService.getUser(id).map {
      // Pattern matching для обработки Option
      case Some(user) => Ok(Json.toJson(user))  // Пользователь найден - HTTP 200 с JSON
      case None => NotFound                      // Пользователь не найден - HTTP 404
    }
    // Результат: Future[Result] с пользователем или 404
  }

  // POST действие - создание ресурса
  // parse.json - парсер для JSON тела запроса
  def createUser() = Action.async(parse.json) { implicit request =>
    // request.body - JSON тело запроса (JsValue)
    // validate[T] - валидация и десериализация JSON в тип T
    // Возвращает JsResult[T] (Either с ошибками или успешным значением)
    request.body.validate[CreateUserRequest].fold(
      // errors - ошибки валидации (JsError)
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      // userRequest - успешно десериализованный объект
      userRequest => {
        // userService.createUser() - создание пользователя, возвращает Future[User]
        userService.createUser(userRequest.name, userRequest.email).map { user =>
          // Created() - HTTP 201 (ресурс создан) с JSON телом
          Created(Json.toJson(user))
        }
      }
    )
    // Результат: Future[Result] с созданным пользователем или ошибкой валидации
  }

  // Действие с формой
  def updateUser(id: Long) = Action.async(parse.form(updateUserForm)) { implicit request =>
    val userData = request.body
    userService.updateUser(id, userData.name, userData.email).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }
}

// Формы
import play.api.data._
import play.api.data.Forms._

case class CreateUserRequest(name: String, email: String)
case class UpdateUserData(name: String, email: String)

val updateUserForm = Form(
  mapping(
    "name" -> nonEmptyText,
    "email" -> email
  )(UpdateUserData.apply)(UpdateUserData.unapply)
)
```

### Модели и JSON
```scala
package models

import play.api.libs.json._

case class User(id: Long, name: String, email: String, createdAt: java.time.Instant)

object User {
  // JSON форматтер
  implicit val userFormat: OFormat[User] = Json.format[User]

  // Кастомный форматтер
  implicit val customUserFormat: OFormat[User] = new OFormat[User] {
    def reads(json: JsValue): JsResult[User] = {
      for {
        id <- (json \ "id").validate[Long]
        name <- (json \ "name").validate[String]
        email <- (json \ "email").validate[String]
        createdAt <- (json \ "created_at").validate[java.time.Instant]
      } yield User(id, name, email, createdAt)
    }

    def writes(user: User): JsObject = Json.obj(
      "id" -> user.id,
      "name" -> user.name,
      "email" -> user.email,
      "created_at" -> user.createdAt
    )
  }

  // Reads/Writes отдельно
  implicit val userReads: Reads[User] = Json.reads[User]
  implicit val userWrites: Writes[User] = Json.writes[User]
}

// Вложенные структуры
case class Address(street: String, city: String, country: String)
case class UserProfile(user: User, address: Address, age: Int)

object UserProfile {
  implicit val addressFormat = Json.format[Address]
  implicit val userProfileFormat = Json.format[UserProfile]
}

// Перечисления
object UserStatus extends Enumeration {
  type UserStatus = Value
  val Active, Inactive, Suspended = Value

  implicit val userStatusFormat: Format[UserStatus] = new Format[UserStatus] {
    def reads(json: JsValue): JsResult[UserStatus] = json.validate[String].map(UserStatus.withName)
    def writes(status: UserStatus): JsValue = JsString(status.toString)
  }
}
```

### Сервисы и Dependency Injection
```scala
package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import models.User

@Singleton
class UserService @Inject()(
  userRepository: UserRepository,
  emailService: EmailService
)(implicit ec: ExecutionContext) {

  def getAllUsers(): Future[Seq[User]] = {
    userRepository.findAll()
  }

  def getUser(id: Long): Future[Option[User]] = {
    userRepository.findById(id)
  }

  def createUser(name: String, email: String): Future[User] = {
    val user = User(0, name, email, java.time.Instant.now())
    for {
      savedUser <- userRepository.save(user)
      _ <- emailService.sendWelcomeEmail(savedUser.email, savedUser.name)
    } yield savedUser
  }

  def updateUser(id: Long, name: String, email: String): Future[Option[User]] = {
    userRepository.update(id, name, email)
  }

  def deleteUser(id: Long): Future[Boolean] = {
    userRepository.delete(id)
  }
}

trait UserRepository {
  def findAll(): Future[Seq[User]]
  def findById(id: Long): Future[Option[User]]
  def save(user: User): Future[User]
  def update(id: Long, name: String, email: String): Future[Option[User]]
  def delete(id: Long): Future[Boolean]
}

@Singleton
class InMemoryUserRepository @Inject()()(implicit ec: ExecutionContext) extends UserRepository {

  private var users = scala.collection.mutable.Map[Long, User]()
  private var idCounter = 1L

  def findAll(): Future[Seq[User]] = Future.successful(users.values.toSeq)

  def findById(id: Long): Future[Option[User]] = Future.successful(users.get(id))

  def save(user: User): Future[User] = Future {
    val newUser = user.copy(id = idCounter)
    users += (idCounter -> newUser)
    idCounter += 1
    newUser
  }

  def update(id: Long, name: String, email: String): Future[Option[User]] = Future {
    users.get(id).map { existingUser =>
      val updatedUser = existingUser.copy(name = name, email = email)
      users += (id -> updatedUser)
      updatedUser
    }
  }

  def delete(id: Long): Future[Boolean] = Future {
    val existed = users.contains(id)
    users -= id
    existed
  }
}
```

### Маршруты
```scala
# Routes
# conf/routes

# Игнорирование favicon
GET     /                           controllers.Assets.versioned(path="/public", file="favicon.ico")

# Домашняя страница
GET     /                           controllers.HomeController.index

# API маршруты
GET     /api/users                  controllers.UserController.listUsers
GET     /api/users/:id              controllers.UserController.getUser(id: Long)
POST    /api/users                  controllers.UserController.createUser
PUT     /api/users/:id              controllers.UserController.updateUser(id: Long)
DELETE  /api/users/:id              controllers.UserController.deleteUser(id: Long)

# Админ маршруты
GET     /admin                      controllers.AdminController.index
->      /admin/users                controllers.admin.UserAdminController

# Статические ресурсы
GET     /assets/*file               controllers.Assets.versioned(path="/public", file: Asset)

# WebSocket
GET     /ws/chat/:roomId            controllers.ChatController.chat(roomId: String)

# Файловая загрузка
POST    /upload                     controllers.FileController.upload
GET     /files/:filename            controllers.FileController.download(filename: String)
```

### Шаблоны (Twirl)
```scala
// app/views/index.scala.html
@import models.User

@(title: String, users: Seq[User])(implicit request: RequestHeader)

<!DOCTYPE html>
<html lang="en">
<head>
    <title>@title</title>
    <link rel="stylesheet" href="@routes.Assets.versioned("stylesheets/main.css")">
</head>
<body>
    <header>
        <h1>@title</h1>
        <nav>
            <a href="@routes.HomeController.index">Home</a>
            <a href="@routes.UserController.listUsers">Users</a>
        </nav>
    </header>

    <main>
        <h2>Users</h2>
        @if(users.isEmpty) {
            <p>No users found.</p>
        } else {
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Created</th>
                    </tr>
                </thead>
                <tbody>
                    @for(user <- users) {
                        <tr>
                            <td>@user.id</td>
                            <td>@user.name</td>
                            <td>@user.email</td>
                            <td>@user.createdAt</td>
                        </tr>
                    }
                </tbody>
            </table>
        }
    </main>

    <footer>
        <p>© 2024 My Play App</p>
    </footer>

    <script src="@routes.Assets.versioned("javascripts/main.js")"></script>
</body>
</html>

// Форма создания пользователя
// app/views/userForm.scala.html
@(userForm: Form[CreateUserRequest])(implicit request: RequestHeader)

@import helper._

@form(routes.UserController.createUser) {
    @helper.inputText(userForm("name"))
    @helper.inputText(userForm("email"))

    @if(userForm.hasErrors) {
        <ul>
            @for(error <- userForm.errors) {
                <li>@error.message</li>
            }
        </ul>
    }

    <button type="submit">Create User</button>
}
```

### Конфигурация
```scala
// conf/application.conf

# Play настройки
play {
  http {
    port = 9000
    address = "0.0.0.0"
  }

  filters {
    enabled += play.filters.csrf.CSRFFilter
    enabled += play.filters.headers.SecurityHeadersFilter
  }
}

# База данных
slick {
  dbs {
    default {
      profile = "slick.jdbc.PostgresProfile$"
      db {
        driver = "org.postgresql.Driver"
        url = "jdbc:postgresql://localhost:5432/myapp"
        user = "myuser"
        password = "mypass"
        connectionPool = "HikariCP"
        numThreads = 10
      }
    }
  }
}

# Redis
redis {
  host = "localhost"
  port = 6379
  password = ""
  database = 0
}

# Email
play.mailer {
  host = "smtp.gmail.com"
  port = 587
  ssl = no
  tls = yes
  user = "myapp@gmail.com"
  password = "app-password"
  debug = no
  mock = no
}

# Кастомные настройки
myapp {
  pagination {
    defaultPageSize = 20
    maxPageSize = 100
  }

  features {
    enableRegistration = true
    enableNotifications = false
  }
}
```

## Работа с базами данных

### Slick
```scala
package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class User(id: Long, name: String, email: String, createdAt: java.time.Instant)

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def createdAt = column[java.time.Instant]("created_at")

  def * = (id, name, email, createdAt).mapTo[User]
}

object UserTable {
  val users = TableQuery[UserTable]
}

@Singleton
class SlickUserRepository @Inject()(
  dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext) extends UserRepository {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._

  def findAll(): Future[Seq[User]] = db.run(UserTable.users.result)

  def findById(id: Long): Future[Option[User]] =
    db.run(UserTable.users.filter(_.id === id).result.headOption)

  def save(user: User): Future[User] = {
    val insertQuery = (UserTable.users returning UserTable.users.map(_.id)) += user
    db.run(insertQuery).map(id => user.copy(id = id))
  }

  def update(id: Long, name: String, email: String): Future[Option[User]] = {
    val updateQuery = UserTable.users.filter(_.id === id)
      .map(u => (u.name, u.email))
      .update((name, email))

    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None)
      case _ => findById(id)
    }
  }

  def delete(id: Long): Future[Boolean] = {
    db.run(UserTable.users.filter(_.id === id).delete).map(_ > 0)
  }

  // Комплексные запросы
  def findByEmail(email: String): Future[Option[User]] =
    db.run(UserTable.users.filter(_.email === email).result.headOption)

  def findUsersCreatedAfter(date: java.time.Instant): Future[Seq[User]] =
    db.run(UserTable.users.filter(_.createdAt > date).result)

  def searchUsers(namePattern: String): Future[Seq[User]] =
    db.run(UserTable.users.filter(_.name.like(s"%$namePattern%")).result)

  // Пагинация
  def findUsersPaged(page: Int, pageSize: Int): Future[Seq[User]] = {
    val offset = (page - 1) * pageSize
    db.run(UserTable.users.drop(offset).take(pageSize).result)
  }

  // Транзакции
  def transferPoints(fromUserId: Long, toUserId: Long, points: Int): Future[Boolean] = {
    val transaction = for {
      fromUser <- UserTable.users.filter(_.id === fromUserId).result.headOption
      toUser <- UserTable.users.filter(_.id === toUserId).result.headOption
      result <- (fromUser, toUser) match {
        case (Some(f), Some(t)) if f.points >= points =>
          for {
            _ <- UserTable.users.filter(_.id === fromUserId)
                  .map(_.points).update(f.points - points)
            _ <- UserTable.users.filter(_.id === toUserId)
                  .map(_.points).update(t.points + points)
          } yield true
        case _ => DBIO.successful(false)
      }
    } yield result

    db.run(transaction.transactionally)
  }
}
```

### Anorm
```scala
package models

import anorm._
import play.api.db.Database
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AnormUserRepository @Inject()(
  db: Database
)(implicit ec: ExecutionContext) extends UserRepository {

  def findAll(): Future[Seq[User]] = Future {
    db.withConnection { implicit conn =>
      SQL("SELECT id, name, email, created_at FROM users").as(userParser.*)
    }
  }

  def findById(id: Long): Future[Option[User]] = Future {
    db.withConnection { implicit conn =>
      SQL("SELECT id, name, email, created_at FROM users WHERE id = {id}")
        .on("id" -> id)
        .as(userParser.singleOpt)
    }
  }

  def save(user: User): Future[User] = Future {
    db.withConnection { implicit conn =>
      val id = SQL("""
        INSERT INTO users (name, email, created_at)
        VALUES ({name}, {email}, {createdAt})
      """).on(
        "name" -> user.name,
        "email" -> user.email,
        "createdAt" -> user.createdAt
      ).executeInsert().get

      user.copy(id = id)
    }
  }

  def update(id: Long, name: String, email: String): Future[Option[User]] = Future {
    db.withConnection { implicit conn =>
      val rowsAffected = SQL("""
        UPDATE users SET name = {name}, email = {email}
        WHERE id = {id}
      """).on(
        "id" -> id,
        "name" -> name,
        "email" -> email
      ).executeUpdate()

      if (rowsAffected > 0) findById(id).value else None
    }
  }

  def delete(id: Long): Future[Boolean] = Future {
    db.withConnection { implicit conn =>
      SQL("DELETE FROM users WHERE id = {id}").on("id" -> id).executeUpdate() > 0
    }
  }

  // Парсер для User
  private val userParser: RowParser[User] = {
    SqlParser.long("id") ~
    SqlParser.str("name") ~
    SqlParser.str("email") ~
    SqlParser.date("created_at").map(_.toInstant)
  } map {
    case id ~ name ~ email ~ createdAt =>
      User(id, name, email, createdAt)
  }
}
```

## Аутентификация и авторизация

### Silhouette
```scala
package controllers

import javax.inject._
import play.api.mvc._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(
  val controllerComponents: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  credentialsProvider: CredentialsProvider,
  passwordHasher: PasswordHasher,
  userService: UserService
)(implicit ec: ExecutionContext) extends BaseController with Silhouette[DefaultEnv] {

  def signIn() = Action.async(parse.json) { implicit request =>
    request.body.validate[SignInData].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      signInData => {
        val credentials = Credentials(signInData.email, signInData.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              for {
                authenticator <- silhouette.env.authenticatorService.create(loginInfo)
                token <- silhouette.env.authenticatorService.init(authenticator)
                result <- silhouette.env.authenticatorService.embed(token,
                  Ok(Json.obj("token" -> token, "user" -> user)))
              } yield result
            case None =>
              Future.successful(Unauthorized("Invalid credentials"))
          }
        }.recover {
          case _ => Unauthorized("Invalid credentials")
        }
      }
    )
  }

  def signUp() = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUpData].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      signUpData => {
        userService.findByEmail(signUpData.email).flatMap {
          case Some(_) => Future.successful(Conflict("User already exists"))
          case None =>
            val loginInfo = LoginInfo(CredentialsProvider.ID, signUpData.email)
            val passwordInfo = passwordHasher.hash(signUpData.password)
            val user = User(0, signUpData.name, signUpData.email)

            for {
              savedUser <- userService.save(user, loginInfo, passwordInfo)
              authenticator <- silhouette.env.authenticatorService.create(loginInfo)
              token <- silhouette.env.authenticatorService.init(authenticator)
              result <- silhouette.env.authenticatorService.embed(token,
                Created(Json.obj("token" -> token, "user" -> savedUser)))
            } yield result
        }
      }
    )
  }

  def securedAction() = SecuredAction.async { implicit request =>
    Future.successful(Ok(s"Hello, ${request.identity.name}!"))
  }
}

// Модели для аутентификации
case class SignInData(email: String, password: String)
case class SignUpData(name: String, email: String, password: String)

object AuthFormats {
  implicit val signInFormat = Json.format[SignInData]
  implicit val signUpFormat = Json.format[SignUpData]
}
```

## WebSockets и Server-Sent Events

### WebSocket Controller
```scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import akka.stream.Materializer

@Singleton
class ChatController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit system: ActorSystem, mat: Materializer) extends BaseController {

  def chat(roomId: String) = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      ChatRoomActor.props(roomId, out)
    }
  }
}

import akka.actor.{Actor, ActorRef, Props}

class ChatRoomActor(roomId: String, out: ActorRef) extends Actor {
  import context.dispatcher
  import scala.concurrent.duration._

  // Присоединение к комнате
  override def preStart(): Unit = {
    ChatRoomManager.join(roomId, self)
  }

  // Отсоединение от комнаты
  override def postStop(): Unit = {
    ChatRoomManager.leave(roomId, self)
  }

  def receive = {
    case msg: String =>
      // Отправка сообщения всем в комнате
      ChatRoomManager.broadcast(roomId, s"User: $msg")
  }
}

object ChatRoomActor {
  def props(roomId: String, out: ActorRef): Props =
    Props(new ChatRoomActor(roomId, out))
}

// Менеджер комнат
object ChatRoomManager {
  import scala.collection.mutable

  private val rooms = mutable.Map.empty[String, mutable.Set[ActorRef]]

  def join(roomId: String, actor: ActorRef): Unit = {
    val room = rooms.getOrElseUpdate(roomId, mutable.Set.empty)
    room += actor
  }

  def leave(roomId: String, actor: ActorRef): Unit = {
    rooms.get(roomId).foreach(_ -= actor)
    if (rooms(roomId).isEmpty) rooms -= roomId
  }

  def broadcast(roomId: String, message: String): Unit = {
    rooms.get(roomId).foreach { actors =>
      actors.foreach(_ ! message)
    }
  }
}
```

### Server-Sent Events
```scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.EventSource
import akka.stream.scaladsl.Source
import scala.concurrent.duration._

@Singleton
class NotificationController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController {

  def notifications() = Action {
    val source = Source.tick(0.seconds, 5.seconds, ())
      .map(_ => s"data: ${java.time.Instant.now()}\n\n")
      .take(100) // Ограничение количества событий

    Ok.chunked(source).as("text/event-stream")
  }

  def notificationsWithEventSource() = Action {
    val events = Source.tick(0.seconds, 2.seconds, 1)
      .scan(0)(_ + _)
      .map { counter =>
        EventSource.Event(
          data = s"Counter: $counter",
          id = Some(counter.toString),
          name = Some("counter-update")
        )
      }

    Ok.chunked(EventSource.fromSource(events)).as("text/event-stream")
  }
}
```

## Тестирование

### Unit тесты
```scala
import org.scalatestplus.play._
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import scala.concurrent.Future
import play.api.test._
import play.api.test.Helpers._

class UserServiceSpec extends PlaySpec with MockitoSugar {

  "UserService" should {
    "return all users" in {
      val mockRepo = mock[UserRepository]
      val mockEmailService = mock[EmailService]
      val userService = new UserService(mockRepo, mockEmailService)

      val users = Seq(
        User(1, "John", "john@example.com", java.time.Instant.now()),
        User(2, "Jane", "jane@example.com", java.time.Instant.now())
      )

      when(mockRepo.findAll()) thenReturn Future.successful(users)

      val result = userService.getAllUsers()

      result.futureValue mustBe users
    }

    "create user and send welcome email" in {
      val mockRepo = mock[UserRepository]
      val mockEmailService = mock[EmailService]
      val userService = new new UserService(mockRepo, mockEmailService)

      val newUser = User(1, "John", "john@example.com", java.time.Instant.now())

      when(mockRepo.save(any[User])) thenReturn Future.successful(newUser)
      when(mockEmailService.sendWelcomeEmail("john@example.com", "John")) thenReturn Future.successful(())

      val result = userService.createUser("John", "john@example.com")

      result.futureValue mustBe newUser
      verify(mockEmailService).sendWelcomeEmail("john@example.com", "John")
    }
  }
}
```

### Integration тесты
```scala
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import scala.concurrent.Await
import scala.concurrent.duration._

class UserControllerSpec extends PlaySpec with OneAppPerTest {

  "UserController" should {
    "return list of users" in {
      val request = FakeRequest(GET, "/api/users")

      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      val users = contentAsJson(result).as[Seq[User]]
      users.length must be > 0
    }

    "create new user" in {
      val userJson = Json.obj(
        "name" -> "Test User",
        "email" -> "test@example.com"
      )

      val request = FakeRequest(POST, "/api/users")
        .withJsonBody(userJson)
        .withHeaders("Content-Type" -> "application/json")

      val result = route(app, request).get

      status(result) mustBe CREATED
      val createdUser = contentAsJson(result).as[User]
      createdUser.name mustBe "Test User"
      createdUser.email mustBe "test@example.com"
    }

    "return 404 for non-existent user" in {
      val request = FakeRequest(GET, "/api/users/99999")

      val result = route(app, request).get

      status(result) mustBe NOT_FOUND
    }
  }
}
```

### E2E тесты с Selenium
```scala
import org.scalatestplus.play._
import play.api.test._
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver

class UserE2ESpec extends PlaySpec with OneServerPerTest with Selenium {

  override implicit lazy val webDriver: WebDriver = new ChromeDriver()

  "User creation flow" should {
    "work end-to-end" in {
      go to s"http://localhost:$port"

      click on "users-link"
      click on "create-user-button"

      textField("name").value = "E2E User"
      textField("email").value = "e2e@example.com"

      click on "submit-button"

      eventually {
        pageSource must include("User created successfully")
        pageSource must include("E2E User")
      }
    }
  }
}
```

## Фильтры и Middleware

### Кастомные фильтры
```scala
import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class LoggingFilter @Inject()(
  implicit val ec: ExecutionContext
) extends EssentialFilter {

  def apply(next: EssentialAction) = EssentialAction { request =>
    val startTime = System.currentTimeMillis()

    next(request).map { result =>
      val endTime = System.currentTimeMillis()
      val requestTime = endTime - startTime

      println(s"${request.method} ${request.uri} took ${requestTime}ms and returned ${result.header.status}")

      result
    }
  }
}

@Singleton
class AuthenticationFilter @Inject()(
  userService: UserService
)(implicit val ec: ExecutionContext) extends EssentialFilter {

  def apply(next: EssentialAction) = EssentialAction { request =>
    val authToken = request.headers.get("Authorization")

    authToken match {
      case Some(token) if token.startsWith("Bearer ") =>
        val userId = extractUserIdFromToken(token.substring(7))
        userService.getUser(userId).flatMap {
          case Some(user) =>
            val enrichedRequest = request.addAttr(RequestAttrKey.User, user)
            next(enrichedRequest)
          case None =>
            Future.successful(Results.Unauthorized)
        }
      case _ =>
        Future.successful(Results.Unauthorized)
    }
  }

  private def extractUserIdFromToken(token: String): Long = {
    // Реальная логика извлечения user ID из JWT токена
    1L
  }
}

// Request attribute key
object RequestAttrKey {
  val User: TypedKey[User] = TypedKey[User]
}
```

### CORS фильтр
```scala
import play.filters.cors.CORSFilter
import play.api.http.HeaderNames

// В application.conf
play.filters.enabled += "play.filters.cors.CORSFilter"

// Или программно
val corsFilter = CORSFilter(
  corsConfig = CORSConfig(
    allowedOrigins = CORSConfig.AllowAll,
    allowedHttpMethods = Seq("GET", "POST", "PUT", "DELETE"),
    allowedHeaders = Seq(HeaderNames.CONTENT_TYPE, HeaderNames.AUTHORIZATION),
    exposedHeaders = Seq.empty,
    supportsCredentials = true,
    preflightMaxAge = 1.hour.toMillis
  )
)
```

## Работа с файлами

### Загрузка файлов
```scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.Files
import java.nio.file.{Files => JFiles, Paths, Path}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController {

  private val uploadPath = Paths.get("./uploads")

  // Убедимся, что директория существует
  JFiles.createDirectories(uploadPath)

  def upload() = Action(parse.multipartFormData) { implicit request =>
    request.body.file("file").map { file =>
      val filename = file.filename
      val contentType = file.contentType
      val filePath = uploadPath.resolve(filename)

      file.ref.moveTo(filePath, replace = true)

      Ok(s"File uploaded: $filename")
    }.getOrElse {
      BadRequest("Missing file")
    }
  }

  def uploadMultiple() = Action(parse.multipartFormData) { implicit request =>
    val uploadedFiles = request.body.files.map { file =>
      val filename = file.filename
      val filePath = uploadPath.resolve(filename)
      file.ref.moveTo(filePath, replace = true)
      filename
    }

    Ok(s"Files uploaded: ${uploadedFiles.mkString(", ")}")
  }

  def download(filename: String) = Action {
    val file = uploadPath.resolve(filename)
    if (JFiles.exists(file)) {
      Ok.sendFile(file.toFile)
    } else {
      NotFound
    }
  }

  // Streaming upload для больших файлов
  def uploadStream() = Action(parse.temporaryFile) { implicit request =>
    val file = request.body
    val filename = file.path.getFileName.toString
    val targetPath = uploadPath.resolve(filename)

    JFiles.move(file.path, targetPath)

    Ok(s"Stream uploaded: $filename")
  }
}
```

## Асинхронные задачи

### Akka Scheduler
```scala
package tasks

import javax.inject._
import play.api.inject.ApplicationLifecycle
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import akka.actor.{ActorSystem, Cancellable}

@Singleton
class ScheduledTasks @Inject()(
  actorSystem: ActorSystem,
  userService: UserService
)(implicit ec: ExecutionContext, lifecycle: ApplicationLifecycle) {

  // Планировщик задач
  private val cancellables = scala.collection.mutable.Set[Cancellable]()

  // Задача выполняемая каждый час
  private val hourlyTask = actorSystem.scheduler.scheduleWithFixedDelay(
    initialDelay = 1.hour,
    delay = 1.hour
  )(() => {
    userService.cleanupInactiveUsers().foreach { count =>
      println(s"Cleaned up $count inactive users")
    }
  })

  // Задача выполняемая ежедневно в полночь
  private val dailyTask = actorSystem.scheduler.scheduleAtFixedRate(
    initialDelay = calculateInitialDelay(),
    interval = 24.hours
  )(() => {
    userService.generateDailyReport().foreach { report =>
      println(s"Daily report generated: $report")
    }
  })

  cancellables += hourlyTask
  cancellables += dailyTask

  // Остановка задач при завершении приложения
  lifecycle.addStopHook(() => Future {
    cancellables.foreach(_.cancel())
  })

  private def calculateInitialDelay(): FiniteDuration = {
    val now = java.time.LocalTime.now()
    val midnight = java.time.LocalTime.MIDNIGHT
    val duration = java.time.Duration.between(now, midnight.plusDays(1))
    FiniteDuration(duration.toMillis, MILLISECONDS)
  }
}
```

## Мониторинг и метрики

### Kamon
```scala
import kamon.Kamon
import kamon.metric.{Counter, Histogram, Gauge}
import play.api.mvc._

class MetricsFilter extends EssentialFilter {
  private val httpRequestsTotal = Kamon.counter("http_requests_total")
  private val httpRequestDuration = Kamon.histogram("http_request_duration_seconds")

  def apply(next: EssentialAction) = EssentialAction { request =>
    val start = System.nanoTime()

    next(request).map { result =>
      val duration = (System.nanoTime() - start) / 1e9

      httpRequestsTotal
        .withTag("method", request.method)
        .withTag("status", result.header.status.toString)
        .increment()

      httpRequestDuration
        .withTag("method", request.method)
        .withTag("path", request.path)
        .record(duration)

      result
    }
  }
}

// Метрики в сервисах
class UserServiceWithMetrics @Inject()(
  userRepository: UserRepository
)(implicit ec: ExecutionContext) {

  private val usersCreated = Kamon.counter("users_created_total")
  private val userCreationDuration = Kamon.histogram("user_creation_duration_seconds")

  def createUser(name: String, email: String): Future[User] = {
    val timer = userCreationDuration.withTag("operation", "create_user").start()

    userRepository.save(User(0, name, email, java.time.Instant.now()))
      .map { user =>
        timer.stop()
        usersCreated.increment()
        user
      }
      .recover { case error =>
        timer.stop()
        throw error
      }
  }
}
```

## Лучшие практики

### Структура проекта
```scala
// Правильная организация кода
app/
├── controllers/
│   ├── api/           # API контроллеры
│   ├── web/           # Web контроллеры
│   └── admin/         # Админ контроллеры
├── models/
│   ├── domain/        # Доменные модели
│   ├── dto/          # Data Transfer Objects
│   └── forms/        # Формы
├── services/
│   ├── business/     # Бизнес логика
│   ├── external/     # Внешние интеграции
│   └── infrastructure/ # Инфраструктурный код
├── repositories/     # Репозитории
├── utils/           # Утилиты
└── views/           # Шаблоны
```

### Обработка ошибок
```scala
// Централизованная обработка ошибок
sealed trait AppError extends Throwable
case class ValidationError(message: String) extends AppError
case class NotFoundError(resource: String) extends AppError
case class DatabaseError(cause: Throwable) extends AppError

object ErrorHandler {
  def handleError(error: Throwable)(implicit request: RequestHeader): Result = error match {
    case ValidationError(msg) => BadRequest(Json.obj("error" -> msg))
    case NotFoundError(resource) => NotFound(Json.obj("error" -> s"$resource not found"))
    case DatabaseError(cause) => InternalServerError(Json.obj("error" -> "Database error"))
    case _ => InternalServerError(Json.obj("error" -> "Internal server error"))
  }
}

// Использование в контроллерах
class UserController @Inject()(...) extends BaseController {
  def getUser(id: Long) = Action.async { implicit request =>
    userService.getUser(id).map {
      case Right(user) => Ok(Json.toJson(user))
      case Left(error) => ErrorHandler.handleError(error)
    }
  }
}
```

### Безопасность
```scala
// Защита от CSRF
// В application.conf
play.filters.csrf.header.bypassHeaders {
  X-Requested-With = "*"
}

// Rate limiting
@Singleton
class RateLimitFilter @Inject()(
  cache: AsyncCacheApi
)(implicit ec: ExecutionContext) extends EssentialFilter {

  def apply(next: EssentialAction) = EssentialAction { request =>
    val key = s"rate_limit_${request.remoteAddress}"

    cache.get[Int](key).flatMap {
      case Some(count) if count >= 100 =>
        Future.successful(TooManyRequests("Rate limit exceeded"))
      case Some(count) =>
        cache.set(key, count + 1, 1.hour)
        next(request)
      case None =>
        cache.set(key, 1, 1.hour)
        next(request)
    }
  }
}

// SQL injection protection (уже встроено в Slick/Anorm)
// XSS protection (уже встроено в Twirl)
```

## Устранение неполадок

### Common Issues
```scala
object PlayTroubleshooting {

  // Проблема: Blocking operations
  // Решение: Использовать database.dispatcher для DB операций
  val databaseDispatcher = system.dispatchers.lookup("database-dispatcher")

  // Проблема: Memory leaks в контроллерах
  // Решение: Не хранить состояние в контроллерах
  // Плохо:
  class BadController extends Controller {
    var counter = 0 // Не делайте так!
  }

  // Хорошо:
  class GoodController @Inject()(counterService: CounterService) extends Controller {
    def increment() = Action {
      counterService.increment()
      Ok
    }
  }

  // Проблема: Slow JSON parsing
  // Решение: Использовать Jackson для больших JSON
  import play.api.libs.json.jackson.PlayJsonModule

  // Проблема: Connection pool exhaustion
  // Решение: Настроить connection pool
  slick {
    dbs {
      default {
        numThreads = 20
        maxConnections = 20
        minConnections = 5
      }
    }
  }

  // Проблема: Session fixation
  // Решение: Регенерировать session ID после логина
  def login() = Action.async(parse.json) { implicit request =>
    // ... аутентификация ...
    Redirect(routes.HomeController.index).withNewSession
  }

  // Проблема: Large file uploads
  // Решение: Streaming и chunked transfer
  def uploadLargeFile() = Action(parse.multipartFormData(handleFilePartAsFile)) { request =>
    // Обработка больших файлов
  }
}
```

### Performance Tuning
```scala
// Оптимизация производительности
object PerformanceOptimization {

  // 1. Connection pooling
  db {
    connectionPool = "HikariCP"
    maximumPoolSize = 20
    minimumIdle = 5
    idleTimeout = 300000
  }

  // 2. Caching
  @Singleton
  class CacheService @Inject()(cache: AsyncCacheApi) {
    def getUser(id: Long): Future[Option[User]] = {
      cache.getOrElseUpdate(s"user_$id", 1.hour) {
        userRepository.findById(id)
      }
    }
  }

  // 3. Database indexes
  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email", O.Unique) // Индекс
    def createdAt = column[java.time.Instant]("created_at")

    def emailIndex = index("email_idx", email, unique = true)
    def createdAtIndex = index("created_at_idx", createdAt)
  }

  // 4. Lazy loading для связанных данных
  case class UserWithPosts(user: User, posts: Option[Seq[Post]] = None)

  def getUserWithPosts(userId: Long, includePosts: Boolean = false): Future[UserWithPosts] = {
    for {
      user <- userRepository.findById(userId).map(_.get)
      posts <- if (includePosts) postRepository.findByUserId(userId) else Future.successful(Seq.empty)
    } yield UserWithPosts(user, if (includePosts) Some(posts) else None)
  }
}
```

## Руководство по миграции

### From Play 2.7 to 2.8
```scala
// Play 2.7 style
class OldController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def action = Action { request =>
    Ok("Hello")
  }
}

// Play 2.8 style
class NewController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def action() = Action { implicit request =>
    Ok("Hello")
  }
}

// Routing changes
// Play 2.7: routes без круглых скобок
GET     /users/:id              controllers.UserController.getUser(id: Long)

// Play 2.8: routes с круглыми скобками
GET     /users/:id              controllers.UserController.getUser(id: Long)
```

### From Java to Scala
```scala
// Java Controller
public class UserController extends Controller {
    @Inject
    private UserService userService;

    public Result getUser(Long id) {
        return userService.getUser(id)
            .map(user -> ok(Json.toJson(user)))
            .orElse(notFound());
    }
}

// Scala equivalent
@Singleton
class UserController @Inject()(
  userService: UserService
) extends BaseController {

  def getUser(id: Long) = Action.async { implicit request =>
    userService.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Play Framework](https://www.playframework.com/documentation/latest/Home)
- [Play Framework GitHub](https://github.com/playframework/playframework)
- [Play Scala Seed](https://github.com/playframework/play-scala-seed.g8)
- [Play JSON](https://www.playframework.com/documentation/latest/ScalaJson)
- [Slick](https://scala-slick.org/)
- [Silhouette](https://www.silhouette.rocks/)

## См. также
- [Akka](scala-akka.md) - Фреймворк для акторов
- [Slick](libraries.md) - Database access library
- [Twirl](templates.md) - Template engine
- [REST API](api.md) - REST API best practices
