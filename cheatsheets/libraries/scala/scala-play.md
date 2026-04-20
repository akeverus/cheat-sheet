---
title: "Scala Play Framework"
description: "Полное руководство по Play Framework в Scala: создание веб-приложений, маршрутизация, контроллеры, формы, базы данных"
tags:
  - scala
  - play
  - web-framework
  - http
  - rest-api
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next:
  - scala-http4s
updated: "2026-04-20"
related: ["scala/scala-basics.md", "scala/scala-slick.md"]
---

# Scala Play Framework

Кратко: полное руководство по **Play Framework** в **Scala**: создание веб-приложений, маршрутизация, контроллеры, формы, базы данных.

## Полезные ссылки

### Официальная документация
- [Play Framework Documentation](https://www.playframework.com/documentation)

### См. также
- [Основы Scala](../../languages/scala/scala-basics.md)
- [Работа с базами данных](scala-slick.md)

- [Scala http4s](../../languages/scala/scala-http4s.md)
- [Scala DSL](../../languages/scala/scala-dsl.md)
- [Shapeless в Scala](../../languages/scala/scala-shapeless.md)
## Содержание

- [Введение в Play Framework](#введение-в-play-framework)
  - [Основные характеристики](#основные-характеристики)
- [Создание проекта](#создание-проекта)
  - [Установка](#установка)
  - [Структура проекта](#структура-проекта)
- [Маршрутизация](#маршрутизация)
  - [Параметры маршрутов](#параметры-маршрутов)
- [Контроллеры](#контроллеры)
- [Формы](#формы)
- [Работа с базами данных](#работа-с-базами-данных)
  - [Интеграция с Slick](#интеграция-с-slick)
  - [Работа с транзакциями](#работа-с-транзакциями)
- [JSON обработка](#json-обработка)
- [Аутентификация и авторизация](#аутентификация-и-авторизация)
  - [Базовая аутентификация](#базовая-аутентификация)
- [Лучшие практики](#лучшие-практики)
  - [Использование Action.async для асинхронных операций](#использование-actionasync-для-асинхронных-операций)
  - [Обработка ошибок](#обработка-ошибок)
  - [Валидация данных](#валидация-данных)
  - [Кэширование](#кэширование)
- [WebSockets](#websockets)
- [Dependency Injection](#dependency-injection)
  - [Асинхронная обработка запросов](#асинхронная-обработка-запросов)
  - [Middleware и Filters](#middleware-и-filters)
  - [Работа с сессиями и Flash](#работа-с-сессиями-и-flash)
  - [Тестирование Play приложений](#тестирование-play-приложений)
  - [Конфигурация приложения](#конфигурация-приложения)
  - [Практический пример: REST API](#практический-пример-rest-api)
- [Шаблоны (Templates)](#шаблоны-templates)
  - [Базовые шаблоны](#базовые-шаблоны)
  - [Работа с параметрами](#работа-с-параметрами)
  - [Композиция шаблонов](#композиция-шаблонов)
- [Работа с файлами](#работа-с-файлами)
  - [Загрузка файлов](#загрузка-файлов)
  - [Скачивание файлов](#скачивание-файлов)
- [Интернационализация (i18n)](#интернационализация-i18n)
  - [Конфигурация сообщений](#конфигурация-сообщений)
  - [Использование в контроллерах](#использование-в-контроллерах)
  - [Использование в шаблонах](#использование-в-шаблонах)
- [Безопасность](#безопасность)
  - [CSRF защита](#csrf-защита)
  - [Хеширование паролей](#хеширование-паролей)
- [Развертывание](#развертывание)
  - [Standalone развертывание](#standalone-развертывание)
  - [Конфигурация для production](#конфигурация-для-production)
- [Мониторинг и логирование](#мониторинг-и-логирование)
  - [Логирование в контроллерах](#логирование-в-контроллерах)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Кэширование ответов](#кэширование-ответов)
  - [Компрессия ответов](#компрессия-ответов)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные темы](#дополнительные-темы)
  - [Работа с WebSockets (детально)](#работа-с-websockets-детально)
  - [Работа с Akka Streams в Play](#работа-с-akka-streams-в-play)
  - [Тестирование (детально)](#тестирование-детально)
  - [Интеграция с внешними сервисами](#интеграция-с-внешними-сервисами)
- [Расширенные возможности Play Framework](#расширенные-возможности-play-framework)
  - [Работа с сессиями (детально)](#работа-с-сессиями-детально)
  - [Работа с Flash сообщениями](#работа-с-flash-сообщениями)
  - [Работа с Cookies](#работа-с-cookies)
  - [Обработка ошибок (детально)](#обработка-ошибок-детально)
  - [Валидация данных (расширенная)](#валидация-данных-расширенная)
  - [Работа с файлами (расширенная)](#работа-с-файлами-расширенная)
  - [Работа с базами данных (расширенная)](#работа-с-базами-данных-расширенная)
  - [Производительность и оптимизация (детально)](#производительность-и-оптимизация-детально)
  - [Безопасность (расширенная)](#безопасность-расширенная)
- [Практические примеры и паттерны](#практические-примеры-и-паттерны)
  - [Паттерн Repository](#паттерн-repository)
  - [Паттерн Service Layer](#паттерн-service-layer)
  - [Обработка ошибок на уровне приложения](#обработка-ошибок-на-уровне-приложения)
  - [Middleware для логирования](#middleware-для-логирования)
  - [Паттерн Action Composition](#паттерн-action-composition)
  - [Работа с WebSockets (расширенная)](#работа-с-websockets-расширенная)
  - [Работа с Server-Sent Events (SSE)](#работа-с-server-sent-events-sse)
  - [Интеграция с Akka Actors](#интеграция-с-akka-actors)
  - [Работа с конфигурацией (расширенная)](#работа-с-конфигурацией-расширенная)
  - [Работа с асинхронными операциями (расширенная)](#работа-с-асинхронными-операциями-расширенная)
  - [Работа с JSON (расширенная)](#работа-с-json-расширенная)
  - [Работа с формами (расширенная)](#работа-с-формами-расширенная)
  - [Работа с базами данных (расширенная)](#работа-с-базами-данных-расширенная-1)
  - [Тестирование (расширенное)](#тестирование-расширенное)
- [Развертывание и DevOps](#развертывание-и-devops)
  - [Развертывание в production](#развертывание-в-production)
  - [Конфигурация для production](#конфигурация-для-production-1)
  - [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Health checks](#health-checks)
- [Дополнительные темы и паттерны](#дополнительные-темы-и-паттерны)
  - [Event Sourcing в Play](#event-sourcing-в-play)
  - [CQRS (Command Query Responsibility Segregation)](#cqrs-command-query-responsibility-segregation)
  - [Микросервисная архитектура](#микросервисная-архитектура)
  - [Работа с очередями сообщений](#работа-с-очередями-сообщений)
  - [Работа с кэшем (расширенная)](#работа-с-кэшем-расширенная)
  - [Оптимизация производительности (детально)](#оптимизация-производительности-детально)
- [Заключение](#заключение-1)
  - [Практические примеры: Play Action Composition](#практические-примеры-play-action-composition)
  - [Практические примеры: Play WebSockets](#практические-примеры-play-websockets)
  - [Практические примеры: Play для микросервисов](#практические-примеры-play-для-микросервисов)
  - [Практические примеры: Работа с WebSockets](#практические-примеры-работа-с-websockets)
  - [Практические примеры: Работа с Akka интеграцией](#практические-примеры-работа-с-akka-интеграцией)
  - [Использование с различными техниками для создания микросервисов](#использование-с-различными-техниками-для-создания-микросервисов)
  - [Использование с различными техниками для WebSockets](#использование-с-различными-техниками-для-websockets)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в Play Framework

**Play Framework** — это современный веб-фреймворк для создания масштабируемых веб-приложений на **Scala** и **Java**. **Play** следует принципам **RESTful** архитектуры и предоставляет реактивную модель программирования, которая позволяет создавать высокопроизводительные приложения, способные обрабатывать множество одновременных запросов без блокировки потоков. **Play Framework** был разработан с учетом лучших практик веб-разработки и функционального программирования, что делает его идеальным выбором для создания современных веб-приложений.

**Play Framework** основан на принципе "**stateless**" архитектуры, где каждый **HTTP** запрос обрабатывается независимо, без сохранения состояния на сервере. Это обеспечивает горизонтальную масштабируемость и упрощает развертывание приложений в облачных средах. **Play** также предоставляет мощные инструменты для разработки, включая **hot reload**, который позволяет видеть изменения в коде без перезапуска приложения.

### Основные характеристики

- **Реактивная модель**: асинхронная обработка запросов. **Play Framework** построен на асинхронной модели программирования, где все операции ввода-вывода выполняются асинхронно, не блокируя потоки выполнения. Это позволяет приложению обрабатывать тысячи одновременных запросов с минимальным количеством потоков, что обеспечивает высокую производительность и эффективное использование ресурсов. Асинхронная модель особенно важна для приложений, которые взаимодействуют с базами данных, внешними **API** или файловой системой.

- **Type-safe**: типобезопасная маршрутизация и шаблоны. **Play Framework** обеспечивает типобезопасность на всех уровнях приложения, от маршрутизации до шаблонов. Маршруты проверяются на этапе компиляции, что предотвращает ошибки времени выполнения, связанные с неправильными путями или параметрами. Типобезопасные шаблоны позволяют компилятору проверять корректность использования переменных и методов в шаблонах, что делает разработку более безопасной и предсказуемой.

- **Hot reload**: автоматическая перезагрузка при изменении кода. **Play Framework** предоставляет механизм **hot reload**, который автоматически перезагружает приложение при изменении исходного кода. Это значительно ускоряет процесс разработки, так как разработчику не нужно вручную перезапускать приложение для проверки изменений. **Hot reload** работает как для **Scala**, так и для **Java** кода, и поддерживает перезагрузку контроллеров, моделей, шаблонов и конфигурационных файлов.

- **RESTful**: поддержка **REST API** из коробки. **Play Framework** предоставляет встроенную поддержку для создания **RESTful API**, включая обработку **JSON**, маршрутизацию, валидацию и сериализацию данных. Это делает **Play** идеальным выбором для создания микросервисов и **API**-серверов, которые должны обрабатывать **JSON** запросы и ответы. **Play** также поддерживает различные форматы данных, включая **XML**, и предоставляет инструменты для создания версионированных **API**.

## Создание проекта

### Установка

```bash
# Установка через sbt
sbt new playframework/play-scala-seed.g8
```

### Структура проекта

```text
app/
  controllers/
  models/
  views/
conf/
  application.conf
  routes
public/
  css/
  js/
  images/
```

## Маршрутизация

Маршрутизация в **Play Framework** определяет, как **HTTP** запросы сопоставляются с методами контроллеров. Маршруты определяются декларативно в файле `conf/routes`, что делает структуру приложения понятной и легко поддерживаемой. **Play Framework** использует компилятор для проверки маршрутов на этапе компиляции, что обеспечивает типобезопасность и предотвращает ошибки времени выполнения.

Файл **routes** является центральным местом для определения всех маршрутов приложения, что упрощает понимание структуры **API** и навигации по приложению. **Play** также поддерживает **reverse routing**, который позволяет генерировать **URL** на основе имен контроллеров и методов, что делает код более поддерживаемым и безопасным при рефакторинге.

**Маршруты определяются в файле `conf/routes` в формате: HTTP метод, путь, контроллер.метод:**

```text
# Маршруты
# Формат: HTTP_METHOD    PATH    CONTROLLER.METHOD(PARAMETERS)

# Главная страница
GET     /                    controllers.HomeController.index

# Список пользователей
GET     /users               controllers.UserController.list

# Просмотр конкретного пользователя
# :id - параметр маршрута, который передается в метод контроллера
GET     /users/:id           controllers.UserController.show(id: Long)

# Создание нового пользователя
POST    /users               controllers.UserController.create

# Обновление пользователя
PUT     /users/:id           controllers.UserController.update(id: Long)

# Удаление пользователя
DELETE  /users/:id           controllers.UserController.delete(id: Long)
```

Каждая строка в файле **routes** определяет один маршрут, состоящий из **HTTP** метода, пути и метода контроллера. Параметры маршрута (обозначенные префиксом `:`) автоматически извлекаются из **URL** и передаются в метод контроллера с соответствующими типами. **Play Framework** автоматически преобразует строковые параметры из **URL** в указанные типы, что обеспечивает типобезопасность маршрутизации.

### Параметры маршрутов

```scala
# Обязательные параметры
GET     /users/:id           controllers.UserController.show(id: Long)

# Опциональные параметры
GET     /search              controllers.SearchController.search(q: Option[String])

# Фиксированные значения
GET     /users/:id/posts     controllers.UserController.posts(id: Long)
```

## Контроллеры

Контроллеры в **Play Framework** являются основными компонентами, которые обрабатывают **HTTP** запросы и генерируют **HTTP** ответы. Контроллеры содержат методы действий (actions), каждый из которых обрабатывает определенный тип запроса. Методы действий возвращают `Action`, который представляет асинхронную обработку запроса и генерацию ответа.

Контроллеры в **Play Framework** следуют принципам функционального программирования, где каждый метод действия является чистой функцией, которая принимает запрос и возвращает результат. Это делает контроллеры легко тестируемыми и предсказуемыми. **Play** также предоставляет мощные инструменты для работы с запросами, включая извлечение параметров, работу с сессиями, **cookies** и заголовками.

**Контроллеры обрабатывают **HTTP** запросы:**

```scala
package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

// BaseController предоставляет базовую функциональность для контроллеров
// ControllerComponents инжектируется через dependency injection
class UserController(cc: ControllerComponents) extends BaseController {

  // Метод действия для получения списка пользователей
  // Action[AnyContent] означает, что действие принимает любой тип контента
  // implicit request позволяет использовать request в неявных параметрах
  def list: Action[AnyContent] = Action { implicit request =>
    // Синхронное получение пользователей (для примера)
    // В реальном приложении следует использовать Action.async для асинхронных операций
    val users = UserService.getAllUsers()
    // Ok создает HTTP ответ со статусом 200 и телом ответа
    // views.html.users.list - это типобезопасный шаблон
    Ok(views.html.users.list(users))
  }

  // Метод действия для просмотра конкретного пользователя
  // id: Long - параметр из маршрута
  def show(id: Long): Action[AnyContent] = Action { implicit request =>
    UserService.findById(id) match {
      case Some(user) => Ok(views.html.users.show(user))
      case None => NotFound  // HTTP 404 - ресурс не найден
    }
  }

  // Метод действия для создания нового пользователя
  def create: Action[AnyContent] = Action { implicit request =>
    // bindFromRequest() извлекает данные из HTTP запроса и привязывает их к форме
    val userForm = userForm.bindFromRequest()
    // fold обрабатывает два случая: ошибки валидации и успешную валидацию
    userForm.fold(
      // Если есть ошибки валидации, возвращаем форму с ошибками
      formWithErrors => BadRequest(views.html.users.create(formWithErrors)),
      // Если валидация успешна, создаем пользователя и перенаправляем
      user => {
        UserService.create(user)
        // Redirect создает HTTP ответ со статусом 303 See Other
        // routes.UserController.list() - reverse routing для генерации URL
        Redirect(routes.UserController.list())
      }
    )
  }
}
```

Контроллеры в **Play Framework** предоставляют множество возможностей для обработки запросов, включая работу с различными типами контента (JSON, `XML`, формы), обработку ошибок, работу с сессиями и **cookies**, и создание различных типов **HTTP** ответов. Понимание работы контроллеров критично для создания эффективных веб-приложений на **Play Framework**.

## Формы

**Play Framework** предоставляет мощную систему типобезопасных форм, которая позволяет валидировать и обрабатывать данные из **HTTP** запросов. Формы в **Play** основаны на функциональном подходе, где валидация и обработка данных выполняются декларативно. Это обеспечивает типобезопасность и предотвращает ошибки, связанные с неправильной обработкой данных.

Система форм **Play** поддерживает различные типы полей, включая текстовые поля, числа, даты, файлы и вложенные объекты. Формы также поддерживают кастомную валидацию, что позволяет создавать сложные правила проверки данных. Результат валидации формы представлен в виде **Either**, где **Left** содержит ошибки валидации, а **Right** — валидные данные.

**Play** предоставляет типобезопасные формы:**

```scala
import play.api.data.Form
import play.api.data.Forms._

// Case класс для данных формы
// Этот класс представляет структуру данных, которые будут получены из формы
case class UserData(name: String, email: String, age: Int)

// Определение формы с валидацией
// Form создает типобезопасную форму с правилами валидации
val userForm = Form(
  // mapping определяет структуру формы и правила валидации
  mapping(
    // "name" - имя поля в форме
    // nonEmptyText - валидатор, который проверяет, что поле не пустое
    "name" -> nonEmptyText,

    // email - валидатор для email адресов
    // Проверяет формат email адреса
    "email" -> email,

    // number - валидатор для чисел
    // min и max определяют диапазон допустимых значений
    "age" -> number(min = 0, max = 150)
  )(UserData.apply)(UserData.unapply)
  // UserData.apply - функция для создания UserData из кортежа
  // UserData.unapply - функция для извлечения значений из UserData
)

// В контроллере
// bindFromRequest() извлекает данные из HTTP запроса и применяет валидацию
def create: Action[AnyContent] = Action { implicit request =>
  userForm.bindFromRequest().fold(
    // Обработка ошибок валидации
    // formWithErrors содержит форму с ошибками валидации
    formWithErrors => BadRequest(views.html.users.create(formWithErrors)),

    // Обработка успешной валидации
    // userData содержит валидные данные из формы
    userData => {
      val user = User(userData.name, userData.email, userData.age)
      UserService.create(user)
      Redirect(routes.UserController.list())
    }
  )
}
```

Формы в **Play Framework** предоставляют мощные возможности для валидации и обработки данных, включая поддержку вложенных объектов, массивов, опциональных полей и кастомной валидации. Это делает работу с формами безопасной и выразительной, предотвращая ошибки, связанные с неправильной обработкой пользовательского ввода.

## Работа с базами данных

**Play Framework** предоставляет мощную интеграцию с базами данных через различные библиотеки, включая **Slick**, **Anorm**, и другие. **Play** поддерживает асинхронную работу с базами данных, что критично для создания высокопроизводительных приложений. Интеграция с базами данных в **Play** основана на принципах функционального программирования, где операции с базой данных представлены как **Future**, что позволяет комбинировать их с другими асинхронными операциями.

**Play Framework** поддерживает различные базы данных, включая **PostgreSQL**, **MySQL**, **H2**, и другие через **JDBC** драйверы. **Play** также предоставляет встроенную поддержку для миграций баз данных через **Evolutions**, что упрощает управление схемой базы данных в процессе разработки. Конфигурация подключения к базе данных выполняется через файл **application.conf**, что позволяет легко переключаться между различными окружениями.

**Play** интегрируется с различными базами данных через **Slick**, **Anorm** и другие библиотеки:**

```scala
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserRepository {
  val users = TableQuery[Users]

  def findAll: Future[Seq[User]] = {
    db.run(users.result)
  }

  def findById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def create(user: User): Future[User] = {
    db.run((users returning users.map(_.id)) += user).map { id =>
      user.copy(id = id)
    }
  }

  def update(id: Long, user: User): Future[Int] = {
    db.run(users.filter(_.id === id).update(user))
  }

  def delete(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }
}
```

### Интеграция с Slick

```scala
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

class UserController @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider,
  cc: ControllerComponents
) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def list: Action[AnyContent] = Action.async { implicit request =>
    db.run(users.result).map { users =>
      Ok(views.html.users.list(users))
    }
  }
}
```

### Работа с транзакциями

```scala
def createWithPosts(user: User, posts: List[Post]): Future[User] = {
  db.run(
    (for {
      userId <- (users returning users.map(_.id)) += user
      _ <- postsTable ++= posts.map(_.copy(userId = userId))
    } yield userId).transactionally
  ).map { userId =>
    user.copy(id = userId)
  }
}
```

## JSON обработка

**Play Framework** предоставляет мощную встроенную поддержку для работы с **JSON**, которая основана на функциональном подходе и обеспечивает типобезопасность. **JSON** библиотека **Play** позволяет легко сериализовать и десериализовать объекты **Scala** в **JSON** и обратно, с автоматической генерацией форматов для **case** классов. Это делает работу с **JSON** простой и безопасной, предотвращая ошибки, связанные с неправильной обработкой **JSON** данных.

**JSON** библиотека **Play** поддерживает различные форматы **JSON**, включая стандартный **JSON**, **JSON** с дополнительными полями, и валидацию **JSON** данных. **Play** также предоставляет инструменты для работы с **JSON** в контроллерах, включая автоматическое извлечение **JSON** из запросов и генерацию **JSON** ответов. Это делает создание **REST API** простым и выразительным.

**Play** предоставляет встроенную поддержку **JSON**:**

```scala
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(id: Long, name: String, email: String)

// Определение формата JSON
implicit val userFormat: Format[User] = (
  (JsPath \ "id").format[Long] and
  (JsPath \ "name").format[String] and
  (JsPath \ "email").format[String]
)(User.apply, unlift(User.unapply))

// Контроллер с JSON
def getJson(id: Long): Action[AnyContent] = Action.async { implicit request =>
  UserService.findById(id).map {
    case Some(user) => Ok(Json.toJson(user))
    case None => NotFound(Json.obj("error" -> "User not found"))
  }
}

def createJson: Action[JsValue] = Action.async(parse.json) { implicit request =>
  request.body.validate[User].fold(
    errors => Future.successful(BadRequest(JsError.toJson(errors))),
    user => UserService.create(user).map { created =>
      Created(Json.toJson(created))
    }
  )
}
```

## Аутентификация и авторизация

### Базовая аутентификация

```scala
import play.api.mvc.{Action, Request, Result}
import play.api.mvc.Results._

trait Secured {
  def username(request: RequestHeader): Option[String] = {
    request.session.get("username")
  }

  def onUnauthorized(request: RequestHeader): Result = {
    Unauthorized("You must be logged in")
  }

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = {
    Action { request =>
      username(request).map { user =>
        f(user)(request)
      }.getOrElse(onUnauthorized(request))
    }
  }
}

class UserController extends Controller with Secured {
  def profile = IsAuthenticated { username => implicit request =>
    Ok(views.html.profile(username))
  }
}
```

## Лучшие практики

### Использование Action.async для асинхронных операций

```scala
// Хорошо - асинхронная операция
def list: Action[AnyContent] = Action.async { implicit request =>
  UserService.getAllUsers().map { users =>
    Ok(views.html.users.list(users))
  }
}

// Плохо - блокирующая операция
def listBad: Action[AnyContent] = Action { implicit request =>
  val users = UserService.getAllUsersBlocking()  // блокирует поток
  Ok(views.html.users.list(users))
}
```

### Обработка ошибок

```scala
def show(id: Long): Action[AnyContent] = Action.async { implicit request =>
  UserService.findById(id).map {
    case Some(user) => Ok(views.html.users.show(user))
    case None => NotFound(views.html.errors.notFound())
  }.recover {
    case e: Exception => InternalServerError(views.html.errors.error(e))
  }
}
```

### Валидация данных

```scala
def create: Action[AnyContent] = Action.async { implicit request =>
  userForm.bindFromRequest().fold(
    formWithErrors => {
      Future.successful(BadRequest(views.html.users.create(formWithErrors)))
    },
    userData => {
      UserService.create(userData).map { user =>
        Redirect(routes.UserController.show(user.id))
          .flashing("success" -> "User created successfully")
      }.recover {
        case e: Exception =>
          BadRequest(views.html.users.create(userForm.fill(userData)))
            .flashing("error" -> e.getMessage)
      }
    }
  )
}
```

### Кэширование

```scala
import play.api.cache.Cached

class UserController @Inject()(
  cached: Cached,
  cc: ControllerComponents
) extends AbstractController(cc) {

  def list = cached("users.list") {
    Action.async { implicit request =>
      UserService.getAllUsers().map { users =>
        Ok(views.html.users.list(users))
      }
    }
  }
}
```

## WebSockets

**Play** поддерживает **WebSockets** для двусторонней связи:**

```scala
import play.api.mvc.WebSocket

def socket: WebSocket = WebSocket.accept[String, String] { request =>
  ActorFlow.actorRef { out =>
    WebSocketActor.props(out)
  }
}
```

**WebSockets** позволяют создавать интерактивные приложения с реальным временем обновления.

## Dependency Injection

**Play** использует **dependency injection** для управления зависимостями:**

```scala
import javax.inject.{Inject, Singleton}

@Singleton
class UserService @Inject()(userRepository: UserRepository) {
  def findUser(id: Long): Option[User] = {
    userRepository.findById(id)
  }
}
```

**Dependency Injection** упрощает тестирование и управление зависимостями.

### Асинхронная обработка запросов

**Play Framework** построен на асинхронной модели:**

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def list: Action[AnyContent] = Action.async { implicit request =>
  UserService.getAllUsers().map { users =>
    Ok(views.html.users.list(users))
  }
}

// Комбинирование нескольких асинхронных операций
def getUserWithPosts(id: Long): Action[AnyContent] = Action.async { implicit request =>
  val userFuture = UserService.findById(id)
  val postsFuture = PostService.findByUserId(id)

  for {
    user <- userFuture
    posts <- postsFuture
  } yield {
    user match {
      case Some(u) => Ok(views.html.users.showWithPosts(u, posts))
      case None => NotFound
    }
  }
}
```

### Middleware и Filters

**Play** поддерживает **middleware** для обработки запросов:**

```scala
import play.api.mvc._
import play.api.mvc.Results._

class LoggingFilter @Inject()(implicit ec: ExecutionContext) extends EssentialFilter {
  def apply(next: EssentialAction): EssentialAction = {
    EssentialAction { request =>
      val startTime = System.currentTimeMillis()
      next(request).map { result =>
        val duration = System.currentTimeMillis() - startTime
        play.Logger.info(s"${request.method} ${request.uri} took ${duration}ms and returned ${result.header.status}")
        result
      }
    }
  }
}

// Регистрация фильтра
class Filters @Inject()(loggingFilter: LoggingFilter) extends HttpFilters {
  def filters = Seq(loggingFilter)
}
```

### Работа с сессиями и Flash

```scala
// Установка сессии
def login: Action[AnyContent] = Action { implicit request =>
  val userForm = loginForm.bindFromRequest()
  userForm.fold(
    formWithErrors => BadRequest(views.html.login(formWithErrors)),
    userData => {
      // аутентификация
      Redirect(routes.HomeController.index())
        .withSession("username" -> userData.username)
    }
  )
}

// Чтение из сессии
def profile: Action[AnyContent] = Action { implicit request =>
  request.session.get("username").map { username =>
    Ok(views.html.profile(username))
  }.getOrElse(Redirect(routes.AuthController.login()))
}

// Flash сообщения
def create: Action[AnyContent] = Action.async { implicit request =>
  userForm.bindFromRequest().fold(
    formWithErrors => Future.successful(BadRequest(views.html.users.create(formWithErrors))),
    userData => {
      UserService.create(userData).map { user =>
        Redirect(routes.UserController.show(user.id))
          .flashing("success" -> "User created successfully")
      }
    }
  )
}
```

### Тестирование Play приложений

```scala
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

class UserControllerSpec extends PlaySpec with OneAppPerTest {

  "UserController" should {
    "return list of users" in {
      val request = FakeRequest(GET, "/users")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
    }

    "create a new user" in {
      val request = FakeRequest(POST, "/users")
        .withFormUrlEncodedBody("name" -> "Alice", "email" -> "alice@example.com", "age" -> "30")
      val result = route(app, request).get

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/users")
    }
  }
}
```

### Конфигурация приложения

```scala
// conf/application.conf
application.name = "My Play App"
application.secret = "changeme"

db.default.driver = "org.postgresql.Driver"
db.default.url = "jdbc:postgresql://localhost/mydb"
db.default.username = "user"
db.default.password = "password"

# Production конфигурация
play.http.secret.key = ${?APPLICATION_SECRET}
```

### Практический пример: REST API

```scala
import play.api.libs.json._
import play.api.mvc._

class UserApiController @Inject()(
  userService: UserService,
  cc: ControllerComponents
) extends AbstractController(cc) {

  implicit val userFormat: Format[User] = Json.format[User]

  def list: Action[AnyContent] = Action.async {
    userService.getAllUsers().map { users =>
      Ok(Json.toJson(users))
    }
  }

  def show(id: Long): Action[AnyContent] = Action.async {
    userService.findById(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("error" -> "User not found"))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      user => userService.create(user).map { created =>
        Created(Json.toJson(created))
      }
    )
  }

  def update(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      user => userService.update(id, user).map {
        case Some(updated) => Ok(Json.toJson(updated))
        case None => NotFound(Json.obj("error" -> "User not found"))
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    userService.delete(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("error" -> "User not found"))
    }
  }
}
```

## Шаблоны (Templates)

**Play Framework** использует типобезопасные шаблоны на основе **Scala**, которые компилируются в обычные функции **Scala**. Это обеспечивает проверку типов на этапе компиляции и предотвращает ошибки времени выполнения, связанные с неправильным использованием переменных в шаблонах. Шаблоны в **Play** основаны на синтаксисе, похожем на **JSP** и **ASP.NET**, но с полной поддержкой **Scala** выражений.

### Базовые шаблоны

```scala
@* app/views/users/list.scala.html *@
@(users: List[User])

@main("Users") {
  <h1>Users</h1>
  <ul>
  @for(user <- users) {
    <li>@user.name - @user.email</li>
  }
  </ul>
}
```

### Работа с параметрами

```scala
@* Шаблон с параметрами *@
@(title: String, users: List[User])(implicit request: RequestHeader)

@main(title) {
  <h1>@title</h1>
  <table>
    <thead>
      <tr>
        <th>Name</th>
        <th>Email</th>
      </tr>
    </thead>
    <tbody>
    @for(user <- users) {
      <tr>
        <td>@user.name</td>
        <td>@user.email</td>
      </tr>
    }
    </tbody>
  </table>
}
```

### Композиция шаблонов

```scala
@* app/views/main.scala.html *@
@(title: String)(content: Html)

<!DOCTYPE html>
<html>
  <head>
    <title>@title</title>
  </head>
  <body>
    @content
  </body>
</html>

@* Использование *@
@main("Users") {
  <h1>Users List</h1>
}
```

## Работа с файлами

**Play Framework** предоставляет мощные возможности для работы с файлами, включая загрузку файлов, скачивание файлов, и работу с временными файлами. Все операции с файлами в **Play** выполняются асинхронно, что обеспечивает высокую производительность при работе с большими файлами.

### Загрузка файлов

```scala
import play.api.mvc._
import play.api.libs.Files.TemporaryFile

def upload: Action[MultipartFormData[TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
  request.body.file("file").map { file =>
    val filename = file.filename
    val contentType = file.contentType
    file.ref.moveTo(new java.io.File(s"/tmp/$filename"))
    Ok("File uploaded")
  }.getOrElse {
    BadRequest("Missing file")
  }
}
```

### Скачивание файлов

```scala
def download(filename: String): Action[AnyContent] = Action {
  val file = new java.io.File(s"/tmp/$filename")
  if (file.exists()) {
    Ok.sendFile(file)
  } else {
    NotFound
  }
}
```

## Интернационализация (i18n)

**Play Framework** предоставляет встроенную поддержку интернационализации, которая позволяет создавать многоязычные приложения. Интернационализация в **Play** основана на файлах сообщений, которые содержат переводы для различных языков. **Play** автоматически определяет язык пользователя на основе заголовков **HTTP** запроса или настроек сессии.

### Конфигурация сообщений

```scala
// conf/messages
hello=Hello
goodbye=Goodbye

// conf/messages.ru
hello=Привет
goodbye=До свидания
```

### Использование в контроллерах

```scala
import play.api.i18n.{I18nSupport, Messages, MessagesApi}

class UserController @Inject()(
  val messagesApi: MessagesApi,
  cc: ControllerComponents
) extends AbstractController(cc) with I18nSupport {

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(Messages("hello"))
  }
}
```

### Использование в шаблонах

```scala
@()(implicit messages: Messages)

<h1>@Messages("hello")</h1>
```

## Безопасность

**Play Framework** предоставляет встроенные механизмы безопасности, включая защиту от **CSRF** атак, безопасную работу с сессиями, и поддержку различных методов аутентификации. **Play** также предоставляет инструменты для работы с паролями, включая хеширование и проверку паролей.

### CSRF защита

```scala
import play.filters.csrf.CSRF

// В шаблоне
@helper.form(action = routes.UserController.create) {
  @helper.CSRF.formField
  @helper.inputText(userForm("name"))
  @helper.inputText(userForm("email"))
  <button type="submit">Create</button>
}
```

### Хеширование паролей

```scala
import play.api.libs.Crypto

val password = "mypassword"
val hashed = Crypto.sign(password)
```

## Развертывание

**Play Framework** предоставляет различные способы развертывания приложений, включая **standalone** режим, развертывание в контейнерах, и развертывание в облачных платформах. **Play** также поддерживает различные конфигурации для различных окружений (development, test, production).

### Standalone развертывание

```bash
# Сборка приложения
sbt dist

# Запуск
./target/universal/stage/bin/myapp
```

### Конфигурация для production

```scala
// conf/application.conf
play.http.secret.key = ${?APPLICATION_SECRET}
play.http.session.secure = true
play.http.session.httpOnly = true
```

## Мониторинг и логирование

**Play Framework** предоставляет встроенную поддержку логирования через **SLF4J** и **Logback**. **Play** также поддерживает различные уровни логирования и позволяет настраивать логирование для различных компонентов приложения.

### Логирование в контроллерах

```scala
import play.api.Logger

class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private val logger = Logger(this.getClass)

  def index: Action[AnyContent] = Action { implicit request =>
    logger.info("User index page accessed")
    Ok(views.html.index())
  }
}
```

## Оптимизация производительности

**Play Framework** предоставляет различные инструменты для оптимизации производительности, включая кэширование, компрессию ответов, и оптимизацию запросов к базе данных. Понимание этих инструментов критично для создания высокопроизводительных приложений.

### Кэширование ответов

```scala
import play.api.cache.Cached

class UserController @Inject()(
  cached: Cached,
  cc: ControllerComponents
) extends AbstractController(cc) {

  def list = cached("users.list", duration = 5.minutes) {
    Action.async { implicit request =>
      UserService.getAllUsers().map { users =>
        Ok(views.html.users.list(users))
      }
    }
  }
}
```

### Компрессия ответов

```scala
// conf/application.conf
play.filters.enabled += "play.filters.gzip.GzipFilter"
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Play Framework** покрывает основные потребности веб-приложений на **Scala**: маршрутизация, контроллеры, формы, работа с БД, async-обработка, middleware, сессии, тестирование, REST API, WebSockets, DI, шаблоны, файлы, i18n, безопасность, деплой, мониторинг, performance tuning.

## Дополнительные темы

### Работа с WebSockets (детально)

**WebSockets** в **Play Framework** позволяют создавать двустороннюю связь между клиентом и сервером, что критично для создания интерактивных приложений с реальным временем обновления. **WebSockets** в **Play** основаны на **Akka Actors**, что обеспечивает масштабируемость и надежность соединений.

```scala
import play.api.mvc.WebSocket
import akka.actor.ActorRef
import akka.stream.scaladsl.{Flow, Sink, Source}

def chat: WebSocket = WebSocket.accept[String, String] { request =>
  Flow[String].map { message =>
    s"Echo: $message"
  }
}
```

### Работа с Akka Streams в Play

**Play Framework** интегрируется с **Akka Streams** для обработки потоков данных:**

```scala
import akka.stream.scaladsl.{Source, Sink}
import play.api.mvc._

def stream: Action[AnyContent] = Action {
  val source = Source(1 to 100)
  Ok.chunked(source.map(_.toString))
}
```

### Тестирование (детально)

**Play Framework** предоставляет мощные инструменты для тестирования приложений:**

```scala
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

class UserControllerSpec extends PlaySpec with OneAppPerTest {

  "UserController" should {
    "return list of users" in {
      val request = FakeRequest(GET, "/users")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Users")
    }
  }
}
```

### Интеграция с внешними сервисами

**Play Framework** предоставляет мощные инструменты для работы с внешними **HTTP** сервисами через **WSClient** (Web `Service` Client). **WSClient** является асинхронным **HTTP** клиентом, который позволяет выполнять **HTTP** запросы к внешним **API** без блокировки потоков. Это критично для создания микросервисных архитектур, где приложение должно взаимодействовать с множеством внешних сервисов.

**WSClient** поддерживает различные типы **HTTP** запросов (GET, `POST`, `PUT`, DELETE), работу с заголовками, аутентификацию, таймауты, и обработку различных типов контента. **WSClient** также поддерживает работу с **WebSockets** и **Server-Sent Events**, что расширяет возможности интеграции с внешними сервисами.

**Play Framework** предоставляет инструменты для работы с внешними **HTTP** сервисами:**

```scala
import play.api.libs.ws.WSClient
import scala.concurrent.ExecutionContext.Implicits.global

class ExternalService @Inject()(ws: WSClient) {
  // Простой GET запрос
  def fetchData: Future[String] = {
    ws.url("https://api.example.com/data")
      .get()
      .map(_.body)  // Извлекаем тело ответа
  }

  // POST запрос с JSON
  def postData(data: JsValue): Future[WSResponse] = {
    ws.url("https://api.example.com/data")
      .post(data)
      .map { response =>
        // Обработка ответа
        response
      }
  }

  // Запрос с заголовками и таймаутом
  def fetchWithHeaders: Future[String] = {
    ws.url("https://api.example.com/data")
      .withHttpHeaders("Authorization" -> "Bearer token")
      .withRequestTimeout(5.seconds)
      .get()
      .map(_.body)
  }
}
```

**WSClient** в **Play Framework** предоставляет типобезопасный и асинхронный способ работы с внешними **HTTP** сервисами, что делает интеграцию с внешними **API** простой и эффективной.

## Расширенные возможности Play Framework

### Работа с сессиями (детально)

Сессии в **Play Framework** позволяют хранить данные на стороне сервера, связанные с конкретным пользователем. Сессии в **Play** основаны на **cookies**, которые шифруются и подписываются для обеспечения безопасности. Это позволяет хранить данные, которые должны быть доступны между различными запросами одного пользователя, такие как идентификатор пользователя, настройки, или временные данные.

```scala
// Установка значения в сессию
def login: Action[AnyContent] = Action { implicit request =>
  val userForm = loginForm.bindFromRequest()
  userForm.fold(
    formWithErrors => BadRequest(views.html.login(formWithErrors)),
    userData => {
      // Аутентификация пользователя
      val user = authenticate(userData.username, userData.password)
      Redirect(routes.HomeController.index())
        .withSession("username" -> user.username, "userId" -> user.id.toString)
    }
  )
}

// Получение значения из сессии
def profile: Action[AnyContent] = Action { implicit request =>
  request.session.get("username") match {
    case Some(username) => Ok(views.html.profile(username))
    case None => Redirect(routes.AuthController.login())
  }
}

// Удаление сессии (выход)
def logout: Action[AnyContent] = Action {
  Redirect(routes.HomeController.index()).withNewSession
}
```

### Работа с Flash сообщениями

**Flash** сообщения в **Play Framework** позволяют передавать данные между запросами, которые автоматически удаляются после первого использования. Это особенно полезно для передачи сообщений об успехе или ошибке после редиректа, когда данные не могут быть переданы через параметры **URL**.

```scala
// Установка flash сообщения
def create: Action[AnyContent] = Action { implicit request =>
  userForm.bindFromRequest().fold(
    formWithErrors => BadRequest(views.html.users.create(formWithErrors)),
    userData => {
      UserService.create(userData)
      Redirect(routes.UserController.list())
        .flashing("success" -> "User created successfully")
    }
  )
}

// Получение flash сообщения в шаблоне
@(implicit flash: Flash)

@flash.get("success").map { message =>
  <div class="alert alert-success">@message</div>
}
```

### Работа с Cookies

**Cookies** в **Play Framework** позволяют хранить данные на стороне клиента. В отличие от сессий, **cookies** хранятся в браузере пользователя и отправляются с каждым запросом. **Play** предоставляет безопасные способы работы с **cookies**, включая шифрование и подпись для защиты данных.

```scala
// Установка cookie
def setCookie: Action[AnyContent] = Action {
  Ok("Cookie set")
    .withCookies(Cookie("theme", "dark", maxAge = Some(3600)))
}

// Получение cookie
def getCookie: Action[AnyContent] = Action { implicit request =>
  request.cookies.get("theme") match {
    case Some(cookie) => Ok(s"Theme: ${cookie.value}")
    case None => Ok("No theme cookie")
  }
}

// Удаление cookie
def removeCookie: Action[AnyContent] = Action {
  Ok("Cookie removed")
    .discardingCookies(DiscardingCookie("theme"))
}
```

### Обработка ошибок (детально)

**Play Framework** предоставляет мощные механизмы для обработки ошибок на различных уровнях приложения. Это включает глобальные обработчики ошибок, кастомные страницы ошибок, и обработку исключений в контроллерах. Правильная обработка ошибок критична для создания надежных приложений.

```scala
// Глобальный обработчик ошибок
import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

class ErrorHandler extends HttpErrorHandler {
  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(
      Status(statusCode)(views.html.errors.clientError(statusCode, message))
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      InternalServerError(views.html.errors.serverError(exception))
    )
  }
}

// Регистрация в application.conf
// play.http.errorHandler = "ErrorHandler"
```

### Валидация данных (расширенная)

**Play Framework** предоставляет расширенные возможности для валидации данных, включая кастомные валидаторы, условную валидацию, и валидацию вложенных объектов. Это позволяет создавать сложные правила валидации для различных сценариев.

```scala
import play.api.data.validation.Constraint

// Кастомный валидатор
val passwordConstraint: Constraint[String] = Constraint("constraints.password") { password =>
  val errors = password match {
    case p if p.length < 8 => Some(ValidationError("Password must be at least 8 characters"))
    case p if !p.exists(_.isUpper) => Some(ValidationError("Password must contain uppercase letter"))
    case p if !p.exists(_.isLower) => Some(ValidationError("Password must contain lowercase letter"))
    case p if !p.exists(_.isDigit) => Some(ValidationError("Password must contain digit"))
    case _ => None
  }
  errors.fold(Valid)(Invalid(_))
}

// Использование кастомного валидатора
val userForm = Form(
  mapping(
    "username" -> nonEmptyText,
    "password" -> text.verifying(passwordConstraint),
    "email" -> email
  )(UserData.apply)(UserData.unapply)
)
```

### Работа с файлами (расширенная)

**Play Framework** предоставляет расширенные возможности для работы с файлами, включая загрузку множественных файлов, валидацию типов файлов, работу с большими файлами, и обработку файлов в фоновом режиме.

```scala
// Загрузка множественных файлов
def uploadMultiple: Action[MultipartFormData[TemporaryFile]] =
  Action(parse.multipartFormData) { implicit request =>
    val files = request.body.files
    files.foreach { file =>
      val filename = file.filename
      val contentType = file.contentType
      file.ref.moveTo(new java.io.File(s"/tmp/$filename"))
    }
    Ok("Files uploaded")
  }

// Валидация типа файла
def uploadImage: Action[MultipartFormData[TemporaryFile]] =
  Action(parse.multipartFormData) { implicit request =>
    request.body.file("image").map { file =>
      val allowedTypes = Seq("image/jpeg", "image/png", "image/gif")
      if (allowedTypes.contains(file.contentType.getOrElse(""))) {
        file.ref.moveTo(new java.io.File(s"/tmp/${file.filename}"))
        Ok("Image uploaded")
      } else {
        BadRequest("Invalid file type")
      }
    }.getOrElse {
      BadRequest("Missing file")
    }
  }
```

### Работа с базами данных (расширенная)

**Play Framework** предоставляет расширенные возможности для работы с базами данных, включая работу с транзакциями, миграции, **connection pooling**, и оптимизацию запросов.

```scala
// Работа с транзакциями
import slick.jdbc.PostgresProfile.api._

def transferMoney(fromId: Long, toId: Long, amount: Double): Future[Unit] = {
  db.run(
    (for {
      fromAccount <- accounts.filter(_.id === fromId).result.headOption
      toAccount <- accounts.filter(_.id === toId).result.headOption
      _ <- fromAccount match {
        case Some(acc) if acc.balance >= amount =>
          for {
            _ <- accounts.filter(_.id === fromId).map(_.balance).update(acc.balance - amount)
            _ <- accounts.filter(_.id === toId).map(_.balance).update(toAccount.get.balance + amount)
          } yield ()
        case _ => DBIO.failed(new Exception("Insufficient funds"))
      }
    } yield ()).transactionally
  )
}

// Миграции через Evolutions
// conf/evolutions/default/1.sql
# --- !Ups
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE
);

# --- !Downs
DROP TABLE users;
```

### Производительность и оптимизация (детально)

**Play Framework** предоставляет множество инструментов для оптимизации производительности приложений, включая кэширование на различных уровнях, оптимизацию запросов к базе данных, компрессию ответов, и мониторинг производительности.

```scala
// Кэширование на уровне приложения
import play.api.cache.AsyncCacheApi

class UserService @Inject()(
  cache: AsyncCacheApi,
  userRepository: UserRepository
) {
  def findById(id: Long): Future[Option[User]] = {
    cache.getOrElseUpdate(s"user.$id", 5.minutes) {
      userRepository.findById(id)
    }
  }
}

// Оптимизация запросов к базе данных
def findUsersWithPosts: Future[Seq[(User, Seq[Post])]] = {
  val usersQuery = users.result
  val postsQuery = posts.result

  db.run(
    for {
      users <- usersQuery
      posts <- postsQuery
    } yield {
      val postsByUserId = posts.groupBy(_.userId)
      users.map(user => (user, postsByUserId.getOrElse(user.id, Seq.empty)))
    }
  )
}
```

### Безопасность (расширенная)

**Play Framework** предоставляет расширенные механизмы безопасности, включая защиту от различных типов атак, безопасную работу с паролями, и поддержку различных методов аутентификации.

```scala
// Защита от XSS
import play.api.mvc.SecurityHeadersFilter

// В application.conf
play.filters.enabled += "play.filters.headers.SecurityHeadersFilter"

// Безопасное хеширование паролей
import org.mindrot.jbcrypt.BCrypt

def hashPassword(password: String): String = {
  BCrypt.hashpw(password, BCrypt.gensalt())
}

def checkPassword(password: String, hashed: String): Boolean = {
  BCrypt.checkpw(password, hashed)
}

// JWT токены для аутентификации
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

def generateToken(userId: Long): String = {
  val claim = JwtClaim(
    content = s"""{"userId":$userId}""",
    expiration = Some(System.currentTimeMillis() / 1000 + 3600)
  )
  Jwt.encode(claim, "secret", JwtAlgorithm.HS256)
}
```

## Практические примеры и паттерны

### Паттерн Repository

Паттерн **Repository** обеспечивает абстракцию над источником данных, что упрощает тестирование и позволяет легко переключаться между различными источниками данных.

```scala
trait UserRepository {
  def findById(id: Long): Future[Option[User]]
  def findAll: Future[Seq[User]]
  def create(user: User): Future[User]
  def update(id: Long, user: User): Future[Option[User]]
  def delete(id: Long): Future[Boolean]
}

class SlickUserRepository @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with UserRepository {

  import profile.api._

  val users = TableQuery[Users]

  override def findById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  override def findAll: Future[Seq[User]] = {
    db.run(users.result)
  }

  override def create(user: User): Future[User] = {
    db.run((users returning users.map(_.id)) += user).map { id =>
      user.copy(id = id)
    }
  }

  override def update(id: Long, user: User): Future[Option[User]] = {
    db.run(users.filter(_.id === id).update(user)).flatMap { rowsAffected =>
      if (rowsAffected > 0) findById(id)
      else Future.successful(None)
    }
  }

  override def delete(id: Long): Future[Boolean] = {
    db.run(users.filter(_.id === id).delete).map(_ > 0)
  }
}
```

### Паттерн Service Layer

**Service Layer** предоставляет бизнес-логику приложения, отделяя ее от контроллеров и репозиториев. Это упрощает тестирование и делает код более модульным.

```scala
@Singleton
class UserService @Inject()(
  userRepository: UserRepository,
  emailService: EmailService
)(implicit ec: ExecutionContext) {

  def findById(id: Long): Future[Option[User]] = {
    userRepository.findById(id)
  }

  def create(userData: UserData): Future[User] = {
    val user = User(
      name = userData.name,
      email = userData.email,
      age = userData.age
    )
    for {
      created <- userRepository.create(user)
      _ <- emailService.sendWelcomeEmail(created.email)
    } yield created
  }

  def update(id: Long, userData: UserData): Future[Option[User]] = {
    userRepository.findById(id).flatMap {
      case Some(existing) =>
        val updated = existing.copy(
          name = userData.name,
          email = userData.email,
          age = userData.age
        )
        userRepository.update(id, updated)
      case None => Future.successful(None)
    }
  }
}
```

### Обработка ошибок на уровне приложения

Глобальная обработка ошибок позволяет централизованно обрабатывать исключения и создавать единообразные ответы об ошибках.

```scala
import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Logger

class GlobalErrorHandler extends HttpErrorHandler {
  private val logger = Logger(this.getClass)

  def onClientError(
    request: RequestHeader,
    statusCode: Int,
    message: String
  ): Future[Result] = {
    logger.warn(s"Client error: $statusCode - $message for ${request.uri}")
    Future.successful(
      Status(statusCode)(views.html.errors.clientError(statusCode, message))
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"Server error for ${request.uri}", exception)
    Future.successful(
      InternalServerError(views.html.errors.serverError(exception))
    )
  }
}
```

### Middleware для логирования

**Middleware** позволяет перехватывать запросы и ответы для выполнения дополнительной обработки, такой как логирование, мониторинг, или добавление заголовков.

```scala
import play.api.mvc._
import play.api.Logger
import scala.concurrent.ExecutionContext

class LoggingMiddleware @Inject()(implicit ec: ExecutionContext) extends EssentialFilter {
  private val logger = Logger(this.getClass)

  def apply(next: EssentialAction): EssentialAction = {
    EssentialAction { request =>
      val startTime = System.currentTimeMillis()
      next(request).map { result =>
        val duration = System.currentTimeMillis() - startTime
        logger.info(
          s"${request.method} ${request.uri} " +
          s"took ${duration}ms " +
          s"and returned ${result.header.status}"
        )
        result.withHeaders("X-Response-Time" -> s"${duration}ms")
      }
    }
  }
}
```

### Паттерн Action Composition

**Action Composition** позволяет создавать переиспользуемые компоненты для обработки запросов, такие как аутентификация, авторизация, или валидация.

```scala
import play.api.mvc._

case class AuthenticatedRequest[A](
  user: User,
  request: Request[A]
) extends WrappedRequest[A](request)

object AuthenticatedAction {
  def apply(block: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      request.session.get("userId") match {
        case Some(userId) =>
          UserService.findById(userId.toLong) match {
            case Some(user) => block(AuthenticatedRequest(user, request))
            case None => Unauthorized("Invalid session")
          }
        case None => Unauthorized("Not authenticated")
      }
    }
  }
}

// Использование
def profile = AuthenticatedAction { request =>
  Ok(views.html.profile(request.user))
}
```

### Работа с WebSockets (расширенная)

**WebSockets** в **Play Framework** предоставляют мощные возможности для создания интерактивных приложений с реальным временем обновления. **WebSockets** основаны на **Akka Streams**, что обеспечивает масштабируемость и обработку **backpressure**.

```scala
import play.api.mvc.WebSocket
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.actor.ActorRef

// Простой WebSocket с эхом
def echo: WebSocket = WebSocket.accept[String, String] { request =>
  Flow[String].map { message =>
    s"Echo: $message"
  }
}

// WebSocket с актором
def chat: WebSocket = WebSocket.accept[String, String] { request =>
  ActorFlow.actorRef { out =>
    ChatActor.props(out)
  }
}

// WebSocket с обработкой ошибок
def robustChat: WebSocket = WebSocket.accept[String, String] { request =>
  Flow[String]
    .map { message =>
      // Обработка сообщения
      processMessage(message)
    }
    .recover {
      case e: Exception =>
        s"Error: ${e.getMessage}"
    }
}
```

### Работа с Server-Sent Events (SSE)

**Server-Sent Events** позволяют серверу отправлять данные клиенту в реальном времени через однонаправленное соединение.

```scala
import akka.stream.scaladsl.Source
import play.api.mvc._

def events: Action[AnyContent] = Action {
  val source = Source.tick(1.second, 1.second, ())
    .map(_ => s"data: ${System.currentTimeMillis()}\n\n")

  Ok.chunked(source).as("text/event-stream")
}
```

### Интеграция с Akka Actors

**Play Framework** интегрируется с **Akka Actors** для создания распределенных и конкурентных приложений.

```scala
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import javax.inject._

@Singleton
class ApplicationActor @Inject()(system: ActorSystem) {
  val actor: ActorRef = system.actorOf(Props[MyActor], "myActor")

  def sendMessage(message: String): Unit = {
    actor ! message
  }
}
```

### Работа с конфигурацией (расширенная)

**Play Framework** предоставляет мощную систему конфигурации через **Typesafe Config**, которая поддерживает различные форматы конфигурации и переменные окружения.

```scala
import play.api.Configuration

class ConfigService @Inject()(config: Configuration) {
  // Получение простого значения
  val appName: String = config.get[String]("application.name")

  // Получение опционального значения
  val optionalValue: Option[String] = config.getOptional[String]("optional.key")

  // Получение значения с дефолтом
  val withDefault: String = config.get[String]("key", "default")

  // Получение вложенных значений
  val nested: String = config.get[String]("database.default.url")

  // Получение списка значений
  val list: Seq[String] = config.get[Seq[String]]("list.key")
}
```

### Работа с асинхронными операциями (расширенная)

**Play Framework** построен на асинхронной модели, и понимание работы с асинхронными операциями критично для создания высокопроизводительных приложений.

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// Комбинирование нескольких Future
def getCombinedData: Action[AnyContent] = Action.async { implicit request =>
  val userFuture = UserService.findById(1L)
  val postsFuture = PostService.findByUserId(1L)
  val commentsFuture = CommentService.findByPostId(1L)

  for {
    user <- userFuture
    posts <- postsFuture
    comments <- commentsFuture
  } yield {
    Ok(views.html.userData(user, posts, comments))
  }
}

// Обработка ошибок в Future
def getDataWithErrorHandling: Action[AnyContent] = Action.async { implicit request =>
  UserService.findById(1L)
    .map {
      case Some(user) => Ok(views.html.user(user))
      case None => NotFound
    }
    .recover {
      case e: DatabaseException => InternalServerError("Database error")
      case e: Exception => InternalServerError("Unexpected error")
    }
}

// Таймауты для Future
import scala.concurrent.duration._
import akka.pattern.after
import akka.actor.ActorSystem

def getDataWithTimeout(implicit system: ActorSystem): Action[AnyContent] = Action.async { implicit request =>
  val dataFuture = UserService.findById(1L)
  val timeoutFuture = after(5.seconds, system.scheduler)(
    Future.failed(new TimeoutException("Operation timed out"))
  )

  Future.firstCompletedOf(Seq(dataFuture, timeoutFuture))
    .map {
      case Some(user) => Ok(views.html.user(user))
      case None => NotFound
    }
}
```

### Работа с JSON (расширенная)

**Play Framework** предоставляет расширенные возможности для работы с **JSON**, включая кастомные форматы, валидацию, и работу с вложенными структурами.

```scala
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(id: Long, name: String, email: String, posts: List[Post])

// Кастомный формат с валидацией
implicit val userFormat: Format[User] = (
  (JsPath \ "id").format[Long] and
  (JsPath \ "name").format[String](minLength[String](3)) and
  (JsPath \ "email").format[String](email) and
  (JsPath \ "posts").format[List[Post]]
)(User.apply, unlift(User.unapply))

// Валидация JSON
def createUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
  request.body.validate[User].fold(
    errors => {
      Future.successful(BadRequest(JsError.toJson(errors)))
    },
    user => {
      UserService.create(user).map { created =>
        Created(Json.toJson(created))
      }
    }
  )
}

// Работа с JSON в шаблонах
@(user: User)(implicit writes: Writes[User])
<script>
  var user = @Html(Json.toJson(user).toString());
</script>
```

### Работа с формами (расширенная)

**Play Framework** предоставляет расширенные возможности для работы с формами, включая вложенные формы, динамические формы, и кастомную валидацию.

```scala
import play.api.data.Form
import play.api.data.Forms._

// Вложенные формы
case class Address(street: String, city: String, zipCode: String)
case class UserWithAddress(name: String, email: String, address: Address)

val addressForm = Form(
  mapping(
    "street" -> nonEmptyText,
    "city" -> nonEmptyText,
    "zipCode" -> nonEmptyText
  )(Address.apply)(Address.unapply)
)

val userWithAddressForm = Form(
  mapping(
    "name" -> nonEmptyText,
    "email" -> email,
    "address" -> addressForm.mapping
  )(UserWithAddress.apply)(UserWithAddress.unapply)
)

// Динамические формы
val dynamicForm = Form(
  mapping(
    "name" -> nonEmptyText,
    "tags" -> list(nonEmptyText)
  )(DynamicData.apply)(DynamicData.unapply)
)

// Кастомная валидация
val customForm = Form(
  mapping(
    "email" -> email.verifying("Email already exists", email => !emailExists(email)),
    "password" -> text.verifying("Password too weak", password => isStrongPassword(password))
  )(LoginData.apply)(LoginData.unapply)
)
```

### Работа с базами данных (расширенная)

**Play Framework** предоставляет расширенные возможности для работы с базами данных, включая **connection pooling**, миграции, и оптимизацию запросов.

```scala
// Connection pooling конфигурация
// conf/application.conf
db.default {
  driver = "org.postgresql.Driver"
  url = "jdbc:postgresql://localhost/mydb"
  username = "user"
  password = "password"
  hikaricp {
    maximumPoolSize = 10
    minimumIdle = 5
    connectionTimeout = 30000
  }
}

// Работа с несколькими базами данных
db.default.driver = "org.postgresql.Driver"
db.default.url = "jdbc:postgresql://localhost/mydb"

db.readonly.driver = "org.postgresql.Driver"
db.readonly.url = "jdbc:postgresql://readonly-server/mydb"
```

### Тестирование (расширенное)

**Play Framework** предоставляет мощные инструменты для тестирования приложений на различных уровнях.

```scala
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

// Тестирование контроллеров
class UserControllerSpec extends PlaySpec with OneAppPerTest {

  "UserController" should {
    "return list of users" in {
      val request = FakeRequest(GET, "/users")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Users")
    }

    "create a new user" in {
      val request = FakeRequest(POST, "/users")
        .withFormUrlEncodedBody("name" -> "Alice", "email" -> "alice@example.com", "age" -> "30")
      val result = route(app, request).get

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/users")
    }
  }
}

// Тестирование с моками
class UserServiceSpec extends PlaySpec {
  "UserService" should {
    "find user by id" in {
      val mockRepository = mock[UserRepository]
      when(mockRepository.findById(1L)).thenReturn(Future.successful(Some(User(1L, "Alice", "alice@example.com"))))

      val service = new UserService(mockRepository)
      val result = await(service.findById(1L))

      result mustBe Some(User(1L, "Alice", "alice@example.com"))
    }
  }
}
```

## Развертывание и DevOps

### Развертывание в production

Развертывание **Play** приложений в **production** требует правильной конфигурации и настройки окружения. **Play** поддерживает различные способы развертывания, включая **standalone** режим, развертывание в контейнерах, и развертывание в облачных платформах.

```bash
# Сборка production версии
sbt dist

# Создает архив в target/universal/
# Распаковка и запуск
unzip target/universal/myapp-1.0.zip
cd myapp-1.0
./bin/myapp -Dconfig.file=/path/to/production.conf
```

### Конфигурация для production

```scala
// conf/application.conf
play.http.secret.key = ${?APPLICATION_SECRET}
play.http.session.secure = true
play.http.session.httpOnly = true
play.http.session.maxAge = 1h

# Database
db.default {
  driver = ${?DB_DRIVER}
  url = ${?DB_URL}
  username = ${?DB_USERNAME}
  password = ${?DB_PASSWORD}
  hikaricp {
    maximumPoolSize = 20
    minimumIdle = 10
  }
}

# Logging
logger.root = INFO
logger.play = INFO
logger.application = DEBUG
```

### Мониторинг и метрики

**Play Framework** предоставляет инструменты для мониторинга приложений и сбора метрик.

```scala
import play.api.mvc._
import play.api.libs.json._

class MetricsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def metrics: Action[AnyContent] = Action {
    val metrics = Map(
      "requests" -> getRequestCount(),
      "errors" -> getErrorCount(),
      "responseTime" -> getAverageResponseTime()
    )
    Ok(Json.toJson(metrics))
  }
}
```

### Health checks

**Health checks** позволяют проверить состояние приложения и его зависимостей.

```scala
class HealthController @Inject()(
  db: Database,
  cc: ControllerComponents
) extends AbstractController(cc) {

  def health: Action[AnyContent] = Action.async {
    // Проверка базы данных
    db.run(sql"SELECT 1".as[Int]).map { _ =>
      Ok(Json.obj("status" -> "healthy"))
    }.recover {
      case e: Exception =>
        ServiceUnavailable(Json.obj("status" -> "unhealthy", "error" -> e.getMessage))
    }
  }
}
```

## Дополнительные темы и паттерны

### Event Sourcing в Play

**Event Sourcing** позволяет хранить все изменения состояния как последовательность событий, что обеспечивает полную историю изменений и возможность восстановления состояния на любой момент времени.

```scala
sealed trait UserEvent
case class UserCreated(name: String, email: String) extends UserEvent
case class UserUpdated(name: String) extends UserEvent
case class UserDeleted() extends UserEvent

class UserEventStore {
  private var events: List[UserEvent] = List.empty

  def append(event: UserEvent): Unit = {
    events = events :+ event
  }

  def getEvents: List[UserEvent] = events

  def rebuildState(events: List[UserEvent]): User = {
    events.foldLeft(User.empty) { (user, event) =>
      event match {
        case UserCreated(name, email) => User(name, email)
        case UserUpdated(name) => user.copy(name = name)
        case UserDeleted() => user.copy(deleted = true)
      }
    }
  }
}
```

### CQRS (Command `Query Responsibility` Segregation)

**CQRS** разделяет операции чтения и записи, что позволяет оптимизировать каждую сторону независимо.

```scala
// Command side (запись)
trait UserCommand
case class CreateUser(name: String, email: String) extends UserCommand
case class UpdateUser(id: Long, name: String) extends UserCommand

class UserCommandHandler @Inject()(eventStore: UserEventStore) {
  def handle(command: UserCommand): Future[Unit] = {
    command match {
      case CreateUser(name, email) =>
        eventStore.append(UserCreated(name, email))
        Future.successful(())
      case UpdateUser(id, name) =>
        eventStore.append(UserUpdated(name))
        Future.successful(())
    }
  }
}

// Query side (чтение)
class UserQueryService @Inject()(readModel: UserReadModel) {
  def findById(id: Long): Future[Option[User]] = {
    readModel.findById(id)
  }

  def findAll: Future[Seq[User]] = {
    readModel.findAll
  }
}
```

### Микросервисная архитектура

**Play Framework** хорошо подходит для создания микросервисов благодаря своей асинхронной модели и поддержке различных протоколов связи.

```scala
// Service discovery
class ServiceDiscovery @Inject()(config: Configuration) {
  def getServiceUrl(serviceName: String): String = {
    config.get[String](s"services.$serviceName.url")
  }
}

// Circuit breaker для устойчивости
import akka.pattern.CircuitBreaker
import scala.concurrent.duration._

class ResilientService @Inject()(ws: WSClient) {
  val breaker = new CircuitBreaker(
    system.scheduler,
    maxFailures = 5,
    callTimeout = 10.seconds,
    resetTimeout = 1.minute
  )

  def callExternalService: Future[String] = {
    breaker.withCircuitBreaker {
      ws.url("https://external-service.com/api")
        .get()
        .map(_.body)
    }
  }
}
```

### Работа с очередями сообщений

Интеграция с очередями сообщений позволяет создавать асинхронные системы обработки задач.

```scala
import com.rabbitmq.client._

class MessageQueueService @Inject()(config: Configuration) {
  val connectionFactory = new ConnectionFactory()
  connectionFactory.setHost(config.get[String]("rabbitmq.host"))

  def publishMessage(queue: String, message: String): Unit = {
    val connection = connectionFactory.newConnection()
    val channel = connection.createChannel()
    channel.queueDeclare(queue, false, false, false, null)
    channel.basicPublish("", queue, null, message.getBytes)
    channel.close()
    connection.close()
  }

  def consumeMessages(queue: String, handler: String => Unit): Unit = {
    val connection = connectionFactory.newConnection()
    val channel = connection.createChannel()
    channel.queueDeclare(queue, false, false, false, null)

    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(
        tag: String,
        envelope: Envelope,
        properties: AMQP.BasicProperties,
        body: Array[Byte]
      ): Unit = {
        val message = new String(body, "UTF-8")
        handler(message)
      }
    }

    channel.basicConsume(queue, true, consumer)
  }
}
```

### Работа с кэшем (расширенная)

**Play Framework** предоставляет различные стратегии кэширования для оптимизации производительности.

```scala
import play.api.cache.AsyncCacheApi
import scala.concurrent.duration._

class CachedUserService @Inject()(
  cache: AsyncCacheApi,
  userRepository: UserRepository
) {
  // Кэширование с TTL
  def findById(id: Long): Future[Option[User]] = {
    cache.getOrElseUpdate(s"user.$id", 5.minutes) {
      userRepository.findById(id)
    }
  }

  // Инвалидация кэша
  def update(id: Long, user: User): Future[Option[User]] = {
    userRepository.update(id, user).map { result =>
      cache.remove(s"user.$id")
      result
    }
  }

  // Кэширование с условием
  def findActiveUsers: Future[Seq[User]] = {
    cache.getOrElseUpdate("active.users", 10.minutes) {
      userRepository.findActive()
    }
  }
}
```

### Оптимизация производительности (детально)

Оптимизация производительности **Play** приложений включает различные техники, от оптимизации запросов к базе данных до оптимизации шаблонов и кэширования.

```scala
// Оптимизация запросов к базе данных
def findUsersWithPostsOptimized: Future[Seq[(User, Seq[Post])]] = {
  // Использование одного запроса вместо N+1 запросов
  val query = for {
    users <- users.result
    posts <- posts.filter(_.userId inSet users.map(_.id)).result
  } yield {
    val postsByUserId = posts.groupBy(_.userId)
    users.map(user => (user, postsByUserId.getOrElse(user.id, Seq.empty)))
  }

  db.run(query)
}

// Оптимизация шаблонов
// Использование view для ленивых вычислений
def renderLargeList: Action[AnyContent] = Action {
  val largeList = (1 to 1000000).toList
  val view = largeList.view
    .filter(_ % 2 == 0)
    .map(_ * 2)
    .take(100)
    .toList
  Ok(views.html.list(view))
}
```

## Заключение

### Практические примеры: Play Action Composition

```scala
import play.api.mvc._
import scala.concurrent.Future

// Создание кастомного Action
case class LoggingAction[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    println(s"Request: ${request.method} ${request.uri}")
    action(request)
  }

  override def parser: BodyParser[A] = action.parser
  override def executionContext: ExecutionContext = action.executionContext
}

// Использование
def index = LoggingAction {
  Action {
    Ok("Hello World")
  }
}
```

### Практические примеры: Play WebSockets

```scala
import play.api.mvc._
import akka.stream.scaladsl._

def socket = WebSocket.accept[String, String] { request =>
  Flow[String].map { message =>
    s"Echo: $message"
  }
}

// Использование с JSON
import play.api.libs.json._

case class Message(text: String)
implicit val messageFormat = Json.format[Message]

def jsonSocket = WebSocket.accept[Message, Message] { request =>
  Flow[Message].map { msg =>
    Message(s"Echo: ${msg.text}")
  }
}
```

### Практические примеры: Play для микросервисов

```scala
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.json._

// Service для работы с данными
trait UserService {
  def findById(id: Long): Future[Option[User]]
  def createUser(user: User): Future[User]
}

// Контроллер для микросервиса
class UserController(service: UserService) extends Controller {
  implicit val userFormat = Json.format[User]

  def getUser(id: Long) = Action.async {
    service.findById(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def createUser = Action.async(parse.json[User]) { request =>
    service.createUser(request.body).map { user =>
      Created(Json.toJson(user))
    }
  }
}
```

**Play Framework** — реактивный веб-фреймворк для **Scala**. Покрывает: маршрутизацию, контроллеры, Action Composition, WebSockets, интеграцию с Akka, Event Sourcing, CQRS, микросервисы, очереди, performance, безопасность, деплой, мониторинг. Построен на принципах функционального и реактивного программирования — подходит для приложений с высокой конкурентностью и realtime-взаимодействиями.

### Практические примеры: Работа с WebSockets

```scala
import play.api.mvc._
import play.api.libs.streams.AkkaStreams
import akka.stream.scaladsl._

// WebSocket для реального времени
def chatSocket: WebSocket = WebSocket.accept[String, String] { request =>
  Flow[String].map { message =>
    s"Echo: $message"
  }
}
```

### Практические примеры: Работа с Akka интеграцией

```scala
import play.api.mvc._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

// Интеграция с Akka Actors
class UserController @Inject()(
  actorSystem: ActorSystem,
  cc: ControllerComponents
) extends AbstractController(cc) {

  implicit val timeout: Timeout = Timeout(5.seconds)

  def getUser(id: Long) = Action.async {
    val userActor = actorSystem.actorSelection(s"/user/user-$id")
    (userActor ? GetUser(id)).mapTo[User].map { user =>
      Ok(Json.toJson(user))
    }
  }
}
```

### Использование с различными техниками для создания микросервисов

```scala
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.json._

// Service для работы с данными
trait UserService {
  def findById(id: Long): Future[Option[User]]
  def createUser(user: User): Future[User]
}

// Контроллер для микросервиса
class UserController(service: UserService) extends Controller {
  implicit val userFormat = Json.format[User]

  def getUser(id: Long) = Action.async {
    service.findById(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def createUser = Action.async(parse.json[User]) { request =>
    service.createUser(request.body).map { user =>
      Created(Json.toJson(user))
    }
  }
}
```

### Использование с различными техниками для WebSockets

```scala
import play.api.mvc._
import play.api.libs.streams.AkkaStreams
import akka.stream.scaladsl._

// WebSocket для реального времени
def chatSocket: WebSocket = WebSocket.accept[String, String] { request =>
  Flow[String].map { message =>
    s"Echo: $message"
  }
}
```

## Дополнительные ресурсы

**Для дальнейшего изучения **Play Framework** рекомендуется:**

- [Play Framework Documentation](https://www.playframework.com/documentation)
- [Play Framework Tutorial](https://www.playframework.com/documentation/latest/Home)
- [Play Framework Best Practices](https://www.playframework.com/documentation/latest/BestPractices)
- [Play Framework Reactive Streams](https://www.playframework.com/documentation/latest/Streams)
- [Play Framework Security](https://www.playframework.com/documentation/latest/SecurityHeaders)
- [Play Framework Testing](https://www.playframework.com/documentation/latest/Testing)
- [Play Framework Deployment](https://www.playframework.com/documentation/latest/Deploying)
- [Play Framework WebSockets](https://www.playframework.com/documentation/latest/ScalaWebSockets)
- [Play Framework JSON](https://www.playframework.com/documentation/latest/ScalaJson)

