---
title: "Scala JSON"
description: "Полное руководство по работе с JSON в Scala: Play JSON, Circe, сериализация, десериализация, валидация"
tags: ["scala", "json", "serialization", "play-json", "circe"]
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-basics.md", "scala/scala-play.md"]
---

# Scala JSON

Кратко: полное руководство по работе с JSON в Scala: Play JSON, Circe, сериализация, десериализация, валидация.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [Play JSON](https://www.playframework.com/documentation/latest/ScalaJson)
- [Circe](https://circe.github.io/circe/)

### См. также
- `./scala-basics.md` - основы Scala
- `./scala-play.md` - Play Framework

## Содержание

- [Введение в JSON](#введение-в-json)
- [Play JSON](#play-json)
- [Circe](#circe)
- [Сериализация и десериализация](#сериализация-и-десериализация)
- [Валидация](#валидация)
- [Лучшие практики](#лучшие-практики)

## Введение в JSON

JSON (JavaScript Object Notation) - это популярный формат обмена данными. В Scala существует несколько библиотек для работы с JSON, наиболее популярные - Play JSON и Circe.

### Основные библиотеки

- **Play JSON**: часть Play Framework, простая в использовании
- **Circe**: функциональная библиотека на основе Cats
- **Spray JSON**: легковесная библиотека

## Play JSON

Play JSON предоставляет простой API для работы с JSON:

```scala
import play.api.libs.json._

// Создание JSON
val json = Json.obj(
  "name" -> "Alice",
  "age" -> 30,
  "email" -> "alice@example.com"
)

// Парсинг JSON
val parsed = Json.parse("""{"name":"Alice","age":30}""")

// Доступ к значениям
val name = (parsed \ "name").as[String]
val age = (parsed \ "age").as[Int]
```

Play JSON обеспечивает простой и интуитивный API для работы с JSON.

## Circe

Circe - это функциональная библиотека для работы с JSON:

```scala
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

case class User(name: String, age: Int)

// Парсинг JSON
val json = """{"name":"Alice","age":30}"""
val decoded = decode[User](json)  // Right(User("Alice", 30))

// Кодирование в JSON
val user = User("Alice", 30)
val encoded = Encoder[User].apply(user)
```

Circe предоставляет типобезопасный и функциональный подход к работе с JSON.

## Сериализация и десериализация

### Play JSON

```scala
import play.api.libs.json._

case class User(name: String, age: Int)

implicit val userFormat: Format[User] = (
  (JsPath \ "name").format[String] and
  (JsPath \ "age").format[Int]
)(User.apply, unlift(User.unapply))

// Сериализация
val user = User("Alice", 30)
val json = Json.toJson(user)

// Десериализация
val user2 = json.as[User]
```

### Circe

```scala
import io.circe.generic.auto._
import io.circe.syntax._

case class User(name: String, age: Int)

// Сериализация
val user = User("Alice", 30)
val json = user.asJson

// Десериализация
val user2 = json.as[User]
```

## Валидация

Валидация JSON позволяет проверять корректность данных:

```scala
import play.api.libs.json._

// Валидация с помощью Reads
implicit val userReads: Reads[User] = (
  (JsPath \ "name").read[String](minLength[String](1)) and
  (JsPath \ "age").read[Int](min(0).keepAnd(max(150)))
)(User.apply _)

// Валидация при парсинге
val json = Json.parse("""{"name":"","age":200}""")
val result = json.validate[User]  // JsError
```

Валидация обеспечивает безопасность при работе с JSON данными.

### Play JSON - расширенные возможности

Play JSON предоставляет мощные возможности для работы с JSON:

```scala
import play.api.libs.json._

// Работа с вложенными структурами
val json = Json.obj(
  "user" -> Json.obj(
    "name" -> "Alice",
    "age" -> 30,
    "address" -> Json.obj(
      "street" -> "123 Main St",
      "city" -> "New York"
    )
  )
)

// Доступ к вложенным значениям
val name = (json \ "user" \ "name").as[String]
val city = (json \ "user" \ "address" \ "city").as[String]

// Обновление значений
val updated = json.transform(
  (__ \ "user" \ "age").json.update(__.read[Int].map(_ + 1))
)
```

### Circe - расширенные возможности

Circe предоставляет функциональный подход к работе с JSON:

```scala
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

case class User(name: String, age: Int, email: Option[String])

// Автоматическая сериализация с опциональными полями
val user = User("Alice", 30, Some("alice@example.com"))
val json = user.asJson
// {"name":"Alice","age":30,"email":"alice@example.com"}

val user2 = User("Bob", 25, None)
val json2 = user2.asJson
// {"name":"Bob","age":25}

// Десериализация с обработкой ошибок
val jsonString = """{"name":"Alice","age":30}"""
decode[User](jsonString) match {
  case Right(user) => println(s"User: $user")
  case Left(error) => println(s"Error: ${error.getMessage}")
}
```

### Кастомные форматы

Можно создавать кастомные форматы для специальных случаев:

```scala
import play.api.libs.json._

// Кастомный формат для даты
implicit val dateFormat: Format[java.util.Date] = new Format[java.util.Date] {
  def writes(date: java.util.Date): JsValue = {
    JsString(new java.text.SimpleDateFormat("yyyy-MM-dd").format(date))
  }
  
  def reads(json: JsValue): JsResult[java.util.Date] = {
    json.validate[String].map { str =>
      new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)
    }
  }
}

case class Event(name: String, date: java.util.Date)
implicit val eventFormat: Format[Event] = Json.format[Event]
```

### Валидация с Play JSON

Play JSON предоставляет мощные возможности валидации:

```scala
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(name: String, age: Int, email: String)

// Валидация с кастомными правилами
implicit val userReads: Reads[User] = (
  (JsPath \ "name").read[String](minLength[String](1)) and
  (JsPath \ "age").read[Int](min(0).keepAnd(max(150))) and
  (JsPath \ "email").read[String](email)
)(User.apply _)

// Валидация при парсинге
val json = Json.parse("""{"name":"","age":200,"email":"invalid"}""")
json.validate[User] match {
  case JsSuccess(user, _) => println(s"Valid user: $user")
  case JsError(errors) => 
    errors.foreach { case (path, errors) =>
      println(s"$path: ${errors.map(_.message).mkString(", ")}")
    }
}
```

### Валидация с Circe

Circe также поддерживает валидацию:

```scala
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

case class User(name: String, age: Int, email: String)

// Кастомный валидатор
def validateUser(json: Json): Either[String, User] = {
  for {
    name <- json.hcursor.get[String]("name").toOption
      .filter(_.nonEmpty)
      .toRight("Name cannot be empty")
    age <- json.hcursor.get[Int]("age").toOption
      .filter(a => a >= 0 && a <= 150)
      .toRight("Age must be between 0 and 150")
    email <- json.hcursor.get[String]("email").toOption
      .filter(_.contains("@"))
      .toRight("Invalid email")
  } yield User(name, age, email)
}

// Использование
val jsonString = """{"name":"Alice","age":30,"email":"alice@example.com"}"""
parse(jsonString).flatMap(validateUser) match {
  case Right(user) => println(s"Valid user: $user")
  case Left(error) => println(s"Validation error: $error")
}
```

### Работа с массивами

```scala
import play.api.libs.json._

// Массив объектов
val usersJson = Json.arr(
  Json.obj("name" -> "Alice", "age" -> 30),
  Json.obj("name" -> "Bob", "age" -> 25)
)

// Парсинг массива
val users = usersJson.as[List[User]]

// С Circe
import io.circe.generic.auto._
import io.circe.parser._

val usersJsonString = """[{"name":"Alice","age":30},{"name":"Bob","age":25}]"""
decode[List[User]](usersJsonString) match {
  case Right(users) => println(s"Users: $users")
  case Left(error) => println(s"Error: $error")
}
```

### Трансформация JSON

Можно трансформировать JSON структуры:

```scala
import play.api.libs.json._

val json = Json.obj(
  "name" -> "Alice",
  "age" -> 30,
  "email" -> "alice@example.com"
)

// Добавление поля
val withId = json + ("id" -> JsNumber(1))

// Удаление поля
val withoutEmail = json - "email"

// Обновление поля
val older = json.transform(
  (__ \ "age").json.update(__.read[Int].map(_ + 1))
)
```

### Практический пример: API ответы

```scala
import play.api.libs.json._

case class ApiResponse[T](success: Boolean, data: Option[T], error: Option[String])

implicit def apiResponseFormat[T: Format]: Format[ApiResponse[T]] = Json.format[ApiResponse[T]]

// Успешный ответ
val success = ApiResponse(success = true, Some(User("Alice", 30)), None)
val json = Json.toJson(success)
// {"success":true,"data":{"name":"Alice","age":30},"error":null}

// Ответ с ошибкой
val error = ApiResponse(success = false, None, Some("User not found"))
val jsonError = Json.toJson(error)
// {"success":false,"data":null,"error":"User not found"}
```

### Производительность

Сравнение производительности библиотек:

```scala
// Play JSON - хорошая производительность, простая интеграция с Play
// Circe - высокая производительность, функциональный подход
// Spray JSON - легковесная, но менее функциональная

// Для высокопроизводительных систем рекомендуется Circe
// Для Play приложений - Play JSON
```

## Лучшие практики

### Использование автоматических форматов

```scala
// Хорошо - использование автоматических форматов
import io.circe.generic.auto._

case class User(name: String, age: Int)
// Формат создается автоматически

// Плохо - ручное создание форматов для простых случаев
implicit val userFormat = new Format[User] {
  // много кода
}
```

### Обработка ошибок парсинга

```scala
// Хорошо - обработка ошибок
decode[User](jsonString) match {
  case Right(user) => processUser(user)
  case Left(error) => handleError(error)
}

// Плохо - игнорирование ошибок
val user = decode[User](jsonString).right.get  // может выбросить исключение
```

### Использование опциональных полей

```scala
// Хорошо - опциональные поля для обратной совместимости
case class User(name: String, age: Int, email: Option[String] = None)

// Плохо - обязательные поля, которые могут отсутствовать
case class User(name: String, age: Int, email: String)  // может сломать при отсутствии email
```

### Валидация данных

```scala
// Хорошо - валидация при десериализации
implicit val userReads: Reads[User] = (
  (JsPath \ "name").read[String](minLength[String](1)) and
  (JsPath \ "age").read[Int](min(0).keepAnd(max(150)))
)(User.apply _)

// Плохо - отсутствие валидации
implicit val userFormat: Format[User] = Json.format[User]  // принимает любые значения
```

## Продвинутые возможности работы с JSON

### Кастомные сериализаторы

Создание кастомных сериализаторов для специальных случаев.

```scala
import io.circe._
import io.circe.syntax._

// Кастомный сериализатор для LocalDate
implicit val localDateEncoder: Encoder[LocalDate] = 
  Encoder.encodeString.contramap(_.toString)

implicit val localDateDecoder: Decoder[LocalDate] = 
  Decoder.decodeString.emap { str =>
    try Right(LocalDate.parse(str))
    catch {
      case e: Exception => Left(s"Invalid date: $str")
    }
  }

// Использование
case class Event(name: String, date: LocalDate)
val event = Event("Meeting", LocalDate.now())
val json = event.asJson
```

### Трансформация JSON

Трансформация JSON структур для различных целей.

```scala
import io.circe._
import io.circe.syntax._

// Трансформация полей
def transformKeys(json: Json, f: String => String): Json = {
  json.mapObject { obj =>
    JsonObject.fromMap(
      obj.toMap.map { case (k, v) => (f(k), transformKeys(v, f)) }
    )
  }
}

// Использование
val json = Json.obj("firstName" -> "Alice".asJson, "lastName" -> "Smith".asJson)
val transformed = transformKeys(json, _.capitalize)  // "Firstname", "Lastname"
```

### Работа с большими JSON файлами

Обработка больших JSON файлов потоковым способом.

```scala
import io.circe.parser._
import fs2.Stream

// Потоковая обработка JSON
def processLargeJsonFile(filePath: String): Stream[IO, User] = {
  fs2.io.file.Files[IO]
    .readAll(Paths.get(filePath))
    .through(fs2.text.utf8.decode)
    .through(fs2.text.lines)
    .map(parse(_).flatMap(_.as[User]))
    .collect { case Right(user) => user }
}
```

## Заключение

## Дополнительные техники работы с JSON

### Обработка больших JSON файлов

Обработка больших JSON файлов требует специальных подходов.

```scala
import io.circe.parser._
import io.circe.streaming._

// Потоковая обработка больших JSON файлов
val jsonStream = Stream.emit(largeJsonString)
  .through(decodeStream[User])
  .filter(_.age > 18)
  .map(_.name)
```

### Валидация JSON данных

Валидация JSON данных критична для безопасности приложений.

```scala
import play.api.libs.json._

// Валидация JSON с использованием Reads
implicit val userReads: Reads[User] = (
  (JsPath \ "name").read[String](minLength[String](3)) and
  (JsPath \ "age").read[Int](min(0).keepAnd(max(120)))
)(User.apply _)

// Использование валидации
val json = Json.parse("""{"name": "Alice", "age": 30}""")
val result = json.validate[User]
```

### Трансформация JSON структур

Трансформация JSON структур позволяет адаптировать данные.

```scala
import io.circe._
import io.circe.syntax._

// Трансформация JSON
val originalJson = Json.obj(
  "firstName" -> "Alice".asJson,
  "lastName" -> "Smith".asJson
)

val transformed = originalJson.mapObject { obj =>
  obj.add("fullName", (obj("firstName").get.as[String].getOrElse("") + " " + 
                       obj("lastName").get.as[String].getOrElse("")).asJson)
}
```

### Практические примеры: JSON streaming для больших данных

```scala
import play.api.libs.json._
import scala.io.Source

// Потоковая обработка больших JSON файлов
def streamLargeJsonFile(filePath: String): Iterator[JsValue] = {
  Source.fromFile(filePath)
    .getLines()
    .filter(_.trim.nonEmpty)
    .map(line => Json.parse(line))
}

// Обработка JSON массива построчно
def processJsonArray(filePath: String): Iterator[JsValue] = {
  val source = Source.fromFile(filePath)
  val json = Json.parse(source.mkString)
  
  json.as[JsArray].value.iterator
}
```

### Практические примеры: Валидация JSON с Play JSON

```scala
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(id: Int, name: String, email: String, age: Int)

implicit val userReads: Reads[User] = (
  (__ \ "id").read[Int] and
  (__ \ "name").read[String](minLength[String](1) keepAnd maxLength[String](100)) and
  (__ \ "email").read[String](pattern("^[A-Za-z0-9+_.-]+@(.+)$".r)) and
  (__ \ "age").read[Int](min(0) keepAnd max(150))
)(User.apply _)

// Валидация JSON
val json = Json.parse("""
  {
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com",
    "age": 30
  }
""")

val user = json.validate[User] match {
  case JsSuccess(user, _) => Right(user)
  case JsError(errors) => Left(errors)
}
```

### Практические примеры: Кастомная сериализация с Circe

```scala
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.time.LocalDate
import java.time.format.DateTimeFormatter

implicit val localDateEncoder: Encoder[LocalDate] = 
  Encoder.encodeString.contramap[LocalDate](_.format(DateTimeFormatter.ISO_LOCAL_DATE))

implicit val localDateDecoder: Decoder[LocalDate] = 
  Decoder.decodeString.emap { str =>
    try Right(LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE))
    catch {
      case e: Exception => Left(s"Invalid date format: $str")
    }
  }

case class Event(name: String, date: LocalDate)

val event = Event("Conference", LocalDate.of(2024, 6, 15))
val json = event.asJson
val decoded = json.as[Event]
```

Работа с JSON в Scala поддерживается несколькими библиотеками, каждая со своими преимуществами. Понимание Play JSON и Circe, их возможностей для валидации, трансформации, работы с вложенными структурами и массивами, создания кастомных сериализаторов, трансформации JSON, обработки больших JSON файлов, JSON streaming, валидации JSON данных с Reads, кастомной сериализации и трансформации JSON структур позволяет выбирать подходящую библиотеку для конкретных задач и эффективно работать с JSON данными. Правильная обработка ошибок, валидация данных, использование опциональных полей, потоковая обработка больших JSON файлов, валидация JSON данных с использованием Reads, кастомная сериализация с Circe и трансформация JSON структур критичны для production-ready приложений. JSON обработка особенно важна для создания REST API, интеграции с внешними сервисами, работы с конфигурационными файлами, и обработки больших объемов данных.

### Практические примеры: Работа с вложенными JSON структурами

```scala
import play.api.libs.json._

// Вложенные структуры
case class Address(street: String, city: String, zip: String)
case class User(id: Long, name: String, address: Address)

implicit val addressFormat: OFormat[Address] = Json.format[Address]
implicit val userFormat: OFormat[User] = Json.format[User]

val user = User(1L, "Alice", Address("Main St", "NYC", "10001"))
val json = Json.toJson(user)

// Доступ к вложенным полям
val city = (json \ "address" \ "city").as[String]  // "NYC"
```

### Практические примеры: Трансформация JSON структур

```scala
import io.circe._
import io.circe.syntax._

// Трансформация JSON
val json = Json.obj(
  "name" -> "Alice".asJson,
  "age" -> 30.asJson
)

// Добавление поля
val withEmail = json.deepMerge(Json.obj("email" -> "alice@example.com".asJson))

// Удаление поля
val withoutAge = json.mapObject(_.remove("age"))
```

### Использование с различными библиотеками для обработки JSON

```scala
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

// Обработка JSON с Circe
case class User(id: Long, name: String, email: String)

val user = User(1L, "Alice", "alice@example.com")
val json = user.asJson

// Парсинг JSON
val jsonString = """{"id":1,"name":"Alice","email":"alice@example.com"}"""
val parsed = io.circe.parser.parse(jsonString)
val decoded = parsed.flatMap(_.as[User])
```

### Использование с различными техниками для валидации JSON

```scala
import play.api.libs.json._

// Валидация JSON с Play JSON
case class User(name: String, age: Int, email: String)

implicit val userReads: Reads[User] = (
  (JsPath \ "name").read[String](minLength[String](3)) and
  (JsPath \ "age").read[Int](min(0) keepAnd max(150)) and
  (JsPath \ "email").read[String](email)
)(User.apply _)

val json = Json.parse("""{"name":"Alice","age":30,"email":"alice@example.com"}""")
val user = json.validate[User]
```

### Использование с различными техниками для трансформации JSON

```scala
import io.circe._
import io.circe.syntax._

// Трансформация JSON структур
val json = Json.obj(
  "name" -> "Alice".asJson,
  "age" -> 30.asJson
)

// Добавление поля
val withEmail = json.deepMerge(Json.obj("email" -> "alice@example.com".asJson))

// Удаление поля
val withoutAge = json.mapObject(_.remove("age"))

// Изменение значения
val withNewAge = json.mapObject(_.add("age", 31.asJson))
```

## Дополнительные ресурсы

Для дальнейшего изучения работы с JSON в Scala рекомендуется:

- [Play JSON Documentation](https://www.playframework.com/documentation/latest/ScalaJson)
- [Circe Documentation](https://circe.github.io/circe/)
- [Spray JSON Documentation](https://github.com/spray/spray-json)

