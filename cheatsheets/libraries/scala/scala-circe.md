---
title: "Circe"
description: "Circe - это JSON библиотека для Scala, предоставляющая функциональный подход к кодированию и декодированию JSON данных. Основана на type classes и интегрируется с Cats. Circe предоставляет автоматическую генерацию кодеков и поддержку пользовательских типов."
tags:
  - libraries
  - scala
  - scala-circe
type: "overview"
difficulty: "intermediate"
aliases:
  - "Circe"
  - "scala circe"
prerequisites:
  - "[[scala-basics]]"
next: []
updated: "2026-04-20"
---
# Circe

**Circe** — это **JSON** библиотека для **Scala**, предоставляющая функциональный подход к кодированию и декодированию **JSON** данных. Основана на **type classes** и интегрируется с **Cats**. **Circe** предоставляет автоматическую генерацию кодеков и поддержку пользовательских типов.

## Полезные ссылки
- [Официальная документация Circe](https://circe.github.io/circe/)
- [Circe GitHub](https://github.com/circe/circe)
- [Circe Examples](https://circe.github.io/circe/codecs/custom-codecs.html)
- [Cats](https://typelevel.org/cats/)
- [FS2](https://fs2.io/)
- [JSON Pointer RFC 6901](https://datatracker.ietf.org/doc/html/rfc6901)


### См. также
- [Akka](scala-akka.md)
- [Slick](scala-slick.md)
## Содержание

- [Основы Circe](#основы-circe)
  - [Подключение и базовое использование](#подключение-и-базовое-использование)
  - [Case classes и кодеки](#case-classes-и-кодеки)
  - [Ручное определение кодеков](#ручное-определение-кодеков)
- [Продвинутые возможности](#продвинутые-возможности)
  - [HCursor для навигации по JSON](#hcursor-для-навигации-по-json)
  - [Optics для функционального доступа](#optics-для-функционального-доступа)
  - [JSON Pointer (RFC 6901)](#json-pointer-rfc-6901)
- [Работа с различными типами данных](#работа-с-различными-типами-данных)
  - [Перечисления и sealed traits](#перечисления-и-sealed-traits)
  - [Опциональные поля и значения по умолчанию](#опциональные-поля-и-значения-по-умолчанию)
  - [Коллекции и сложные структуры](#коллекции-и-сложные-структуры)
- [Кастомизация и конфигурация](#кастомизация-и-конфигурация)
  - [Configuration для generic derivation](#configuration-для-generic-derivation)
  - [Кастомные type classes](#кастомные-type-classes)
- [Обработка ошибок](#обработка-ошибок)
  - [Accumulating errors](#accumulating-errors)
- [Потоковая обработка](#потоковая-обработка)
  - [FS2 интеграция](#fs2-интеграция)
- [Тестирование](#тестирование)
  - [Unit тесты для кодеков](#unit-тесты-для-кодеков)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Jsoniter Scala интеграция](#jsoniter-scala-интеграция)
  - [Memoization для кодеков](#memoization-для-кодеков)
- [Лучшие практики](#лучшие-практики)
  - [Организация кода](#организация-кода)
  - [Error handling patterns](#error-handling-patterns)
  - [Безопасность и санитизация](#безопасность-и-санитизация)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging Circe кодеков](#debugging-circe-кодеков)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Play JSON to Circe](#from-play-json-to-circe)
  - [From Spray JSON to Circe](#from-spray-json-to-circe)
  - [From Argonaut to Circe](#from-argonaut-to-circe)
- [См. также](#см-также-1)

## Основы Circe

### Подключение и базовое использование

Зависимости **Circe** в **build.sbt** и базовый импорт (Scala).

```scala
// build.sbt
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.5",        // Основная библиотека Circe
  "io.circe" %% "circe-generic" % "0.14.5",     // Автоматическая генерация кодеков для case classes
  "io.circe" %% "circe-parser" % "0.14.5",      // Парсинг JSON строк
  "io.circe" %% "circe-literal" % "0.14.5",     // JSON литералы (макросы для создания JSON)
  "io.circe" %% "circe-optics" % "0.14.5",      // Optics для функциональной навигации по JSON
  "io.circe" %% "circe-pointer" % "0.14.5"      // JSON Pointer (RFC 6901) для доступа к полям
)

import io.circe._                    // Основные типы Circe (Json, Encoder, Decoder)
import io.circe.generic.auto._        // Автоматическая генерация кодеков
import io.circe.parser._              // Парсинг JSON (parse функция)
import io.circe.syntax._              // Синтаксис для преобразования Scala типов в JSON

/
 * Базовое использование Circe для работы с JSON
 * Circe использует type classes (Encoder/Decoder) для преобразования типов
 */
// Простые типы - парсинг JSON строки
val jsonString = """{"name": "John", "age": 30}"""
// parse() - парсинг JSON строки, возвращает Either[ParsingFailure, Json]
// Right(Json) - успешный результат парсинга
// Left(ParsingFailure) - ошибка парсинга
val json = parse(jsonString)  // Right(Json)
// Результат: Either с Json объектом

// Создание JSON из Scala типов
// .asJson - extension method для преобразования Scala типов в Json
// Требует implicit Encoder для типа
val jsonFromScala = Map("key" -> "value").asJson  // Json объект из Map
val numberJson = 42.asJson                         // Json число из Int
val listJson = List(1, 2, 3).asJson               // Json массив из List

// Доступ к полям JSON
// getOrElse(Json.Null) - получение Json или значения по умолчанию
// hcursor - функциональный курсор для навигации по JSON
// get[T]("field") - получение значения поля с типом T
// Возвращает Either[DecodingFailure, T]
val name = json.getOrElse(Json.Null).hcursor.get[String]("name")  // Right("John")
// hcursor - курсор для навигации по JSON структуре
// get[String]("name") - получение строкового значения поля "name"

val age = json.getOrElse(Json.Null).hcursor.get[Int]("age")       // Right(30)
// get[Int]("age") - получение целочисленного значения поля "age"
```

### Case classes и кодеки
```scala
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

// Определение модели
case class User(id: Long, name: String, email: String, age: Int)
case class Address(street: String, city: String, country: String)
case class UserProfile(user: User, address: Address, tags: List[String])

// Автоматическая генерация кодеков
val user = User(1, "John Doe", "john@example.com", 30)

// Кодирование в JSON
val userJson = user.asJson
// {"id":1,"name":"John Doe","email":"john@example.com","age":30}

// Декодирование из JSON
val jsonString = """{"id":1,"name":"John Doe","email":"john@example.com","age":30}"""
val decodedUser = decode[User](jsonString) // Right(User(1, John Doe, john@example.com, 30))

// Вложенные структуры
val profile = UserProfile(
  User(1, "John", "john@example.com", 30),
  Address("123 Main St", "NYC", "USA"),
  List("developer", "scala")
)

val profileJson = profile.asJson
val decodedProfile = decode[UserProfile](profileJson.toString())
```

### Ручное определение кодеков
```scala
import io.circe._

// Пользовательские кодеки
implicit val userEncoder: Encoder[User] = new Encoder[User] {
  final def apply(user: User): Json = Json.obj(
    "id" -> user.id.asJson,
    "full_name" -> user.name.asJson,
    "email_address" -> user.email.asJson,
    "user_age" -> user.age.asJson
  )
}

implicit val userDecoder: Decoder[User] = new Decoder[User] {
  final def apply(c: HCursor): Decoder.Result[User] = for {
    id <- c.get[Long]("id")
    name <- c.get[String]("full_name")
    email <- c.get[String]("email_address")
    age <- c.get[Int]("user_age")
  } yield User(id, name, email, age)
}

// Или используя Decoder combinators
implicit val userDecoder2: Decoder[User] = Decoder.forProduct4("id", "full_name", "email_address", "user_age")(User.apply)

// Semi-automatic derivation (для частичного контроля)
implicit val userEncoderSemi: Encoder[User] = deriveEncoder[User]
implicit val userDecoderSemi: Decoder[User] = deriveDecoder[User]
```

## Продвинутые возможности

### HCursor для навигации по JSON
```scala
import io.circe._

val json = parse("""{
  "user": {
    "name": "John",
    "profile": {
      "age": 30,
      "hobbies": ["reading", "coding"]
    }
  },
  "active": true
}""").getOrElse(Json.Null)

val cursor = json.hcursor

// Доступ к вложенным полям
val userName = cursor.get[String]("user.name") // Right("John")
val userAge = cursor.get[Int]("user.profile.age") // Right(30)

// Навигация вниз
val userCursor = cursor.downField("user")
val profileCursor = userCursor.downField("profile")
val age = profileCursor.get[Int]("age") // Right(30)

// Работа с массивами
val hobbies = profileCursor.get[List[String]]("hobbies") // Right(List("reading", "coding"))
val firstHobby = profileCursor.downField("hobbies").downArray.get[String] // Right("reading")

// Условный доступ
val maybeAge = cursor.get[Option[Int]]("user.profile.age") // Right(Some(30))
val missingField = cursor.get[Option[String]]("user.missing") // Right(None)

// Проверка типов
val isActive = cursor.get[Boolean]("active") // Right(true)
val isString = cursor.get[String]("active") // Left(DecodingFailure)
```

### Optics для функционального доступа
```scala
import io.circe.optics.JsonPath._

// Создание линз для навигации
val json = parse("""{"users":[{"name":"John","age":30},{"name":"Jane","age":25}]}""").getOrElse(Json.Null)

// Простые пути
val _users = root.users.arr
val _firstUser = root.users.arr.index(0)
val _userNames = root.users.each.name.string

// Получение значений
val allUsers = _users.getAll(json) // List of Json objects
val firstUser = _firstUser.getOption(json) // Option[Json]
val names = _userNames.getAll(json) // List("John", "Jane")

// Модификация
val updatedJson = _firstUser.age.int.modify(_ + 1)(json) // Увеличивает возраст John на 1
val addedUser = _users.arr.modify(_.append(Json.obj("name" -> "Bob".asJson, "age" -> 35.asJson)))(json)

// Фильтрация
val adults = root.users.arr.filter(root.age.int.exist(_ >= 30)).getAll(json)
val youngUsers = root.users.each.age.int.modify { age =>
  if (age < 30) age + 10 else age
}(json)

// Комплексные трансформации
val transformed = root.users.each.obj.modify { userObj =>
  val name = userObj("name").flatMap(_.asString).getOrElse("")
  val age = userObj("age").flatMap(_.asNumber).flatMap(_.toInt).getOrElse(0)
  Json.obj(
    "fullName" -> s"$name (age: $age)".asJson,
    "isAdult" -> (age >= 18).asJson
  )
}(json)
```

### JSON Pointer (RFC 6901)
```scala
import io.circe.pointer._

val json = parse("""{
  "users": [
    {"name": "John", "profile": {"age": 30}},
    {"name": "Jane", "profile": {"age": 25}}
  ],
  "metadata": {"version": "1.0"}
}""").getOrElse(Json.Null)

// Создание указателей
val firstUserName = Pointer.root / "users" / 0 / "name"
val secondUserAge = Pointer.root / "users" / 1 / "profile" / "age"
val version = Pointer.root / "metadata" / "version"

// Получение значений
val johnName = firstUserName.get(json) // Right("John")
val janeAge = secondUserAge.get(json) // Right(25)
val apiVersion = version.get(json) // Right("1.0")

// Модификация по указателю
val updatedJson = firstUserName.set("Johnny")(json)
val updatedAge = secondUserAge.set(26)(json)

// Проверка существования
val exists = firstUserName.exists(json) // true
val notExists = (Pointer.root / "missing").exists(json) // false

// Удаление
val withoutVersion = version.delete(json)

// Комплексные операции
val pointer = Pointer.root / "users" / 0
val user = pointer.get(json)
val modifiedUser = user.map(_.mapObject(_.add("status", "active".asJson)))
val updatedJson2 = pointer.set(modifiedUser.getOrElse(Json.Null))(json)
```

## Работа с различными типами данных

### Перечисления и sealed traits
```scala
import io.circe._
import io.circe.generic.extras._

// Перечисления
object UserStatus extends Enumeration {
  type UserStatus = Value
  val Active, Inactive, Suspended = Value

  implicit val userStatusEncoder: Encoder[UserStatus] = Encoder.encodeString.contramap(_.toString)
  implicit val userStatusDecoder: Decoder[UserStatus] = Decoder.decodeString.map(UserStatus.withName)
}

// Sealed traits
sealed trait PaymentMethod
case class CreditCard(number: String, expiry: String) extends PaymentMethod
case class PayPal(email: String) extends PaymentMethod
case object Cash extends PaymentMethod

// Автоматическая генерация для sealed traits
implicit val paymentMethodEncoder: Encoder[PaymentMethod] = deriveEncoder[PaymentMethod]
implicit val paymentMethodDecoder: Decoder[PaymentMethod] = deriveDecoder[PaymentMethod]

// Использование
val creditCard = CreditCard("1234-5678-9012-3456", "12/25")
val json = creditCard.asJson // {"CreditCard":{"number":"1234-5678-9012-3456","expiry":"12/25"}}
val decoded = decode[PaymentMethod](json.toString()) // Right(CreditCard(...))
```

### Опциональные поля и значения по умолчанию
```scala
import io.circe._
import io.circe.generic.auto._

case class Person(
  name: String,
  age: Int,
  email: Option[String] = None,
  phone: Option[String] = None
)

// Декодирование с опциональными полями
val json1 = """{"name":"John","age":30}"""
val person1 = decode[Person](json1) // Right(Person(John,30,None,None))

val json2 = """{"name":"Jane","age":25,"email":"jane@example.com"}"""
val person2 = decode[Person](json2) // Right(Person(Jane,25,Some(jane@example.com),None))

// Кастомный декодер с значениями по умолчанию
implicit val personDecoder: Decoder[Person] = Decoder.forProduct4("name", "age", "email", "phone") {
  (name: String, age: Int, email: Option[String], phone: Option[String]) =>
    Person(name, age, email.orElse(Some("default@example.com")), phone)
}

// Или более гибкий подход
implicit val personDecoder2: Decoder[Person] = new Decoder[Person] {
  final def apply(c: HCursor): Decoder.Result[Person] = {
    for {
      name <- c.get[String]("name")
      age <- c.get[Int]("age")
      email <- c.getOrElse[Option[String]]("email")(None)
      phone <- c.getOrElse[Option[String]]("phone")(None)
    } yield Person(name, age, email, phone)
  }
}
```

### Коллекции и сложные структуры
```scala
import io.circe._
import io.circe.generic.auto._

// Map
case class Config(settings: Map[String, String])
val config = Config(Map("host" -> "localhost", "port" -> "8080"))
val configJson = config.asJson // {"settings":{"host":"localhost","port":"8080"}}

// Set
case class Tags(tags: Set[String])
val tags = Tags(Set("scala", "json", "circe"))
val tagsJson = tags.asJson // {"tags":["scala","json","circe"]}

// Either
case class Response[A](result: Either[String, A])
implicit def responseEncoder[A: Encoder]: Encoder[Response[A]] = deriveEncoder
implicit def responseDecoder[A: Decoder]: Decoder[Response[A]] = deriveDecoder

val successResponse = Response(Right("Success"))
val errorResponse = Response(Left("Error occurred"))

// Tuple
val tupleJson = (1, "hello", true).asJson // [1,"hello",true]
val decodedTuple = decode[(Int, String, Boolean)]("[1,\"hello\",true]") // Right((1,"hello",true))

// Recursive structures
case class Tree(value: Int, children: List[Tree] = Nil)
val tree = Tree(1, List(Tree(2), Tree(3, List(Tree(4)))))
val treeJson = tree.asJson
```

## Кастомизация и конфигурация

### Configuration для generic derivation
```scala
import io.circe.generic.extras._

// Конфигурация для generic кодеков
implicit val config: Configuration = Configuration.default
  .withDiscriminator("type") // Для sealed traits
  .withSnakeCaseMemberNames // Преобразование имен полей
  .withDefaults // Включение значений по умолчанию

// Или более детальная конфигурация
implicit val customConfig: Configuration = Configuration(
  transformMemberNames = _.toUpperCase, // Преобразование имен полей
  transformConstructorNames = _.toLowerCase, // Преобразование имен конструкторов
  useDefaults = true, // Использовать значения по умолчанию
  discriminator = Some("kind") // Дискриминатор для sealed traits
)

// Применение конфигурации
case class CustomCaseClass(
  userName: String,
  userAge: Int = 18,
  userEmail: Option[String] = None
)

val obj = CustomCaseClass("john_doe", 25, Some("john@example.com"))
val json = obj.asJson // {"USER_NAME":"john_doe","USER_AGE":25,"USER_EMAIL":"john@example.com"}
```

### Кастомные type classes
```scala
import io.circe._

// Кастомный Encoder для java.time.Instant
implicit val instantEncoder: Encoder[java.time.Instant] = Encoder.encodeString.contramap(_.toString)
implicit val instantDecoder: Decoder[java.time.Instant] = Decoder.decodeString.map(java.time.Instant.parse)

// Кастомный Encoder для BigDecimal с форматированием
implicit val bigDecimalEncoder: Encoder[BigDecimal] = Encoder.encodeString.contramap(_.setScale(2, BigDecimal.RoundingMode.HALF_UP).toString)
implicit val bigDecimalDecoder: Decoder[BigDecimal] = Decoder.decodeString.map(BigDecimal(_))

// Encoder для перечислений с кастомной логикой
implicit def enumEncoder[E <: Enumeration]: Encoder[E#Value] = Encoder.encodeString.contramap(_.toString.toUpperCase)
implicit def enumDecoder[E <: Enumeration](enum: E): Decoder[E#Value] = Decoder.decodeString.map(enum.withName)

// Применение
case class Product(id: Long, name: String, price: BigDecimal, createdAt: java.time.Instant, status: UserStatus.Value)

val product = Product(1, "Laptop", BigDecimal("999.99"), java.time.Instant.now(), UserStatus.Active)
val productJson = product.asJson
```

## Обработка ошибок

### Accumulating errors
```scala
import io.circe._
import cats.data.NonEmptyList

// Accumulating декодер (собирает все ошибки)
implicit val accumulatingUserDecoder: Decoder[User] = new Decoder[User] {
  final def apply(c: HCursor): Decoder.Result[User] = {
    val idResult = c.get[Long]("id")
    val nameResult = c.get[String]("name")
    val emailResult = c.get[String]("email")
    val ageResult = c.get[Int]("age")

    // Accumulating errors
    (idResult, nameResult, emailResult, ageResult).mapN(User.apply)
  }
}

// Пример с accumulating ошибками
val invalidJson = """{"id":"not-a-number","name":"","email":"invalid-email","age":"not-a-number"}"""
val result = decode[User](invalidJson)

// Результат будет содержать все ошибки валидации
result match {
  case Left(errors) =>
    println(s"Validation errors: ${errors.getMessage}")
  case Right(user) =>
    println(s"Decoded user: $user")
}

// Кастомные error messages
implicit val userDecoderWithMessages: Decoder[User] = new Decoder[User] {
  final def apply(c: HCursor): Decoder.Result[User] = {
    for {
      id <- c.get[Long]("id").left.map(_ => DecodingFailure("ID must be a number", c.history))
      name <- c.get[String]("name").ensure(
        DecodingFailure("Name cannot be empty", c.history)
      )(_.nonEmpty)
      email <- c.get[String]("email").ensure(
        DecodingFailure("Invalid email format", c.history)
      )(_.contains("@"))
      age <- c.get[Int]("age").ensure(
        DecodingFailure("Age must be positive", c.history)
      )(_ > 0)
    } yield User(id, name, email, age)
  }
}
```

## Потоковая обработка

### FS2 интеграция
```scala
import fs2._
import io.circe.fs2._

// Stream processing с Circe
val jsonStream: Stream[IO, String] = Stream(
  """{"id":1,"name":"John"}""",
  """{"id":2,"name":"Jane"}""",
  """{"id":3,"name":"Bob"}"""
)

// Декодирование stream of JSON strings
val decodedStream: Stream[IO, Either[Error, User]] =
  jsonStream.through(stringStreamParser[IO]).through(decoder[IO, User])

// Фильтрация и обработка
val processedStream: Stream[IO, String] = decodedStream
  .collect { case Right(user) if user.name.startsWith("J") => user }
  .map(_.name.toUpperCase)

// Кодирование в JSON stream
val users = Stream(User(1, "John", "john@example.com", 30), User(2, "Jane", "jane@example.com", 25))
val jsonOutputStream: Stream[IO, String] = users.through(encoder[IO, User])

// Комплексная обработка
val complexProcessing: Stream[IO, ProcessedResult] = jsonStream
  .through(stringStreamParser[IO])
  .through(decoder[IO, RawData])
  .evalMap(validateAndTransform)
  .through(encoder[IO, ProcessedResult])
```

## Тестирование

### Unit тесты для кодеков
```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import io.circe.parser._

class CirceCodecSpec extends AnyFlatSpec with Matchers {

  "User codec" should "encode User to JSON correctly" in {
    val user = User(1, "John Doe", "john@example.com", 30)
    val json = user.asJson

    json.hcursor.get[Long]("id") shouldBe Right(1L)
    json.hcursor.get[String]("name") shouldBe Right("John Doe")
    json.hcursor.get[String]("email") shouldBe Right("john@example.com")
    json.hcursor.get[Int]("age") shouldBe Right(30)
  }

  it should "decode JSON to User correctly" in {
    val jsonString = """{"id":1,"name":"John Doe","email":"john@example.com","age":30}"""
    val decoded = decode[User](jsonString)

    decoded shouldBe Right(User(1, "John Doe", "john@example.com", 30))
  }

  it should "handle invalid JSON gracefully" in {
    val invalidJson = """{"id":"not-a-number","name":"","age":-5}"""
    val decoded = decode[User](invalidJson)

    decoded shouldBe a[Left[_, _]] // Should be Left with error
  }

  "PaymentMethod codec" should "handle all subtypes" in {
    val creditCard = CreditCard("1234-5678-9012-3456", "12/25")
    val payPal = PayPal("user@example.com")
    val cash = Cash

    // Encoding
    val ccJson = creditCard.asJson
    val ppJson = payPal.asJson
    val cashJson = cash.asJson

    // Decoding
    decode[PaymentMethod](ccJson.toString()) shouldBe Right(creditCard)
    decode[PaymentMethod](ppJson.toString()) shouldBe Right(payPal)
    decode[PaymentMethod](cashJson.toString()) shouldBe Right(cash)
  }
}
```

## Оптимизация производительности

### Jsoniter Scala интеграция
```scala
// Для высокой производительности можно использовать jsoniter-scala
libraryDependencies += "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-circe" % "2.23.1"

// Jsoniter кодеки
import com.github.plokhotnyuk.jsoniter.scalacirce._

implicit val userCodec: JsonValueCodec[User] = JsonCodecMaker.make[User]

// Быстрое кодирование/декодирование
val user = User(1, "John", "john@example.com", 30)
val jsonBytes = writeToArray(user) // Byte array
val decodedUser = readFromArray[User](jsonBytes)
```

### Memoization для кодеков
```scala
import io.circe._
import scala.collection.concurrent.TrieMap

// Кэширование кодеков для динамических типов
object CodecCache {
  private val encoderCache = TrieMap.empty[String, Encoder[_]]
  private val decoderCache = TrieMap.empty[String, Decoder[_]]

  def getEncoder[T](key: String)(implicit encoder: Encoder[T]): Encoder[T] = {
    encoderCache.getOrElseUpdate(key, encoder).asInstanceOf[Encoder[T]]
  }

  def getDecoder[T](key: String)(implicit decoder: Decoder[T]): Decoder[T] = {
    decoderCache.getOrElseUpdate(key, decoder).asInstanceOf[Decoder[T]]
  }
}

// Использование
case class DynamicData(data: Map[String, Json])

implicit val dynamicDataEncoder: Encoder[DynamicData] = Encoder.forProduct1("data")(d => d.data)
implicit val dynamicDataDecoder: Decoder[DynamicData] = Decoder.forProduct1("data")(DynamicData.apply)
```

## Лучшие практики

### Организация кода
```scala
// Структура проекта для JSON сериализации
package models

import io.circe._
import io.circe.generic.semiauto._

// Доменные модели
case class User(id: Long, name: String, email: String)
case class Address(street: String, city: String, country: String)
case class UserProfile(user: User, address: Address)

// JSON кодеки в companion objects
object User {
  implicit val encoder: Encoder[User] = deriveEncoder[User]
  implicit val decoder: Decoder[User] = deriveDecoder[User]
}

object Address {
  implicit val encoder: Encoder[Address] = deriveEncoder[Address]
  implicit val decoder: Decoder[Address] = deriveDecoder[Address]
}

object UserProfile {
  implicit val encoder: Encoder[UserProfile] = deriveEncoder[UserProfile]
  implicit val decoder: Decoder[UserProfile] = deriveDecoder[UserProfile]
}

// Или в отдельном файле кодеков
package models.json

import io.circe.generic.auto._
import models._

object Codecs {
  // Все кодеки в одном месте для удобства импорта
  implicit val userCodec = deriveCodec[User]
  implicit val addressCodec = deriveCodec[Address]
  implicit val profileCodec = deriveCodec[UserProfile]
}

// Использование
import models._
import models.json.Codecs._

val user = User(1, "John", "john@example.com")
val json = user.asJson
```

### Error handling patterns
```scala
import cats.data.ValidatedNel
import cats.implicits._

// Валидация с накоплением ошибок
type ValidationResult[A] = ValidatedNel[String, A]

def validateUser(json: Json): ValidationResult[User] = {
  val cursor = json.hcursor

  (
    cursor.get[Long]("id").toValidatedNel,
    cursor.get[String]("name").toValidatedNel.ensure(NonEmptyList.of("Name cannot be empty"))(_.nonEmpty),
    cursor.get[String]("email").toValidatedNel.ensure(NonEmptyList.of("Invalid email"))(_.contains("@")),
    cursor.get[Int]("age").toValidatedNel.ensure(NonEmptyList.of("Age must be positive"))(_ > 0)
  ).mapN(User.apply)
}

// Использование
val userJson = parse("""{"id":1,"name":"John","email":"john@example.com","age":30}""").getOrElse(Json.Null)
val result = validateUser(userJson)

result match {
  case Validated.Valid(user) => println(s"Valid user: $user")
  case Validated.Invalid(errors) => println(s"Validation errors: ${errors.toList.mkString(", ")}")
}

// Кастомные декодеры с валидацией
implicit val validatingUserDecoder: Decoder[User] = new Decoder[User] {
  final def apply(c: HCursor): Decoder.Result[User] = {
    validateUser(c.value) match {
      case Validated.Valid(user) => Right(user)
      case Validated.Invalid(errors) =>
        Left(DecodingFailure(s"Validation failed: ${errors.toList.mkString(", ")}", c.history))
    }
  }
}
```

### Безопасность и санитизация
```scala
import io.circe._

// Санитизация входных данных
implicit val safeStringDecoder: Decoder[String] = Decoder.decodeString.map { str =>
  // Удаление потенциально опасных символов
  str.replaceAll("[<>\"']", "").trim
}

implicit val safeIntDecoder: Decoder[Int] = Decoder.decodeInt.ensure(
  DecodingFailure("Value out of safe range", Nil)
)(value => value >= 0 && value <= 10000)

// Ограничение размера JSON
object JsonSecurity {
  val MaxJsonSize = 1024 * 1024 // 1MB

  def safeParse(jsonString: String): Either[Error, Json] = {
    if (jsonString.length > MaxJsonSize) {
      Left(DecodingFailure(s"JSON size exceeds maximum allowed size of $MaxJsonSize bytes", Nil))
    } else {
      parse(jsonString)
    }
  }

  // Rate limiting для JSON операций
  import scala.collection.concurrent.TrieMap
  import java.time.Instant

  private val requestCounts = TrieMap.empty[String, (Int, Instant)]

  def checkRateLimit(clientId: String, maxRequests: Int = 100): Boolean = {
    val now = Instant.now()
    val (count, timestamp) = requestCounts.getOrElse(clientId, (0, now))

    if (now.isAfter(timestamp.plusSeconds(60))) {
      // Reset counter
      requestCounts.put(clientId, (1, now))
      true
    } else if (count < maxRequests) {
      requestCounts.put(clientId, (count + 1, timestamp))
      true
    } else {
      false
    }
  }
}
```

## Устранение неполадок

### Common Issues
```scala
object CirceTroubleshooting {

  // Проблема: Could not find implicit value for parameter encoder
  // Решение: Импортировать кодеки
  import io.circe.generic.auto._ // Для автоматической генерации
  // или
  import models.Codecs._ // Для кастомных кодеков

  // Проблема: DecodingFailure при парсинге
  // Решение: Проверить структуру JSON и типы полей
  val json = parse("""{"id": "not-a-number"}""").getOrElse(Json.Null)
  val result = decode[User](json.toString())
  result match {
    case Left(error) => println(s"Decoding error: ${error.getMessage}")
    case Right(user) => println(s"Decoded: $user")
  }

  // Проблема: Stack overflow при рекурсивных структурах
  // Решение: Использовать lazy val для кодеков
  case class Node(value: Int, children: List[Node])

  object Node {
    implicit lazy val encoder: Encoder[Node] = deriveEncoder[Node]
    implicit lazy val decoder: Decoder[Node] = deriveDecoder[Node]
  }

  // Проблема: Проблемы с null значениями
  // Решение: Использовать Option для опциональных полей
  case class Person(name: String, age: Option[Int])

  val jsonWithNull = """{"name":"John","age":null}"""
  val decoded = decode[Person](jsonWithNull) // Right(Person(John,None))

  // Проблема: Case sensitivity в именах полей
  // Решение: Использовать @JsonKey аннотацию или кастомные кодеки
  import io.circe.generic.extras._

  implicit val config = Configuration.default.withSnakeCaseMemberNames

  case class ApiResponse(userName: String, userAge: Int)
  // Будет сериализовано как {"user_name":"john","user_age":30}

  // Проблема: Performance issues с большими JSON
  // Решение: Использовать streaming parsing
  import fs2._
  import io.circe.fs2._

  val largeJsonStream = fs2.io.file.readAll[IO](Paths.get("large-file.json"), 4096)
    .through(text.utf8Decode)
    .through(stringStreamParser[IO])
    .through(decoder[IO, MyData])
    .evalMap(processRecord)
    .compile
    .drain

  // Проблема: Type erasure с generic types
  // Решение: Использовать TypeTag или явные типы
  import scala.reflect.runtime.universe._

  def genericDecoder[T: TypeTag](json: Json): Either[Error, T] = {
    val tpe = typeOf[T]
    // Кастомная логика декодирования
    ???
  }
}
```

### Debugging Circe кодеков
```scala
import io.circe._

// Расширение для отладки
implicit class DebugDecoderOps[A](decoder: Decoder[A]) {
  def debug: Decoder[A] = new Decoder[A] {
    final def apply(c: HCursor): Decoder.Result[A] = {
      println(s"Debug: Decoding at path ${c.history}")
      println(s"Debug: JSON value: ${c.value}")
      val result = decoder.apply(c)
      println(s"Debug: Result: $result")
      result
    }
  }
}

implicit class DebugEncoderOps[A](encoder: Encoder[A]) {
  def debug: Encoder[A] = new Encoder[A] {
    final def apply(a: A): Json = {
      println(s"Debug: Encoding value: $a")
      val result = encoder.apply(a)
      println(s"Debug: JSON result: $result")
      result
    }
  }
}

// Использование
val userDecoder = deriveDecoder[User].debug
val userEncoder = deriveEncoder[User].debug

val user = User(1, "John", "john@example.com", 30)
val json = user.asJson(userEncoder) // Будет печатать отладочную информацию
val decoded = json.as[User](userDecoder)

// Pretty printing для отладки
val prettyJson = json.pretty(Printer.spaces2)
println(prettyJson)

// Compact printing
val compactJson = json.noSpaces
println(compactJson)

// Кастомный printer
val customPrinter = Printer(
  dropNullValues = true,
  indent = "  ",
  lbraceLeft = "",
  rbraceLeft = "\n",
  lbraceRight = "\n",
  rbraceRight = "",
  lbracketLeft = "",
  rbracketLeft = "\n",
  lbracketRight = "\n",
  rbracketRight = "",
  lrbracketsEmpty = "",
  arrayCommaLeft = "\n",
  arrayCommaRight = "",
  objectCommaLeft = "\n",
  objectCommaRight = "",
  colonLeft = "",
  colonRight = " "
)

val formattedJson = customPrinter.print(json)
```

## Руководство по миграции

### From Play JSON to Circe
```scala
// Play JSON
import play.api.libs.json._

case class User(id: Long, name: String, email: String)

implicit val userFormat: Format[User] = Json.format[User]

val user = User(1, "John", "john@example.com")
val json = Json.toJson(user) // JsValue
val jsonString = json.toString()
val parsed = Json.parse(jsonString) // JsValue
val decoded = parsed.as[User] // User

// Circe equivalent
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class User(id: Long, name: String, email: String)

val user = User(1, "John", "john@example.com")
val json = user.asJson // Json
val jsonString = json.noSpaces
val parsed = parse(jsonString) // Either[Error, Json]
val decoded = parsed.flatMap(_.as[User]) // Either[Error, User]
```

### From Spray JSON to Circe
```scala
// Spray JSON
import spray.json._

case class User(id: Long, name: String, email: String)

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User.apply)
}

import UserJsonProtocol._
val user = User(1, "John", "john@example.com")
val json = user.toJson.toString()
val parsed = json.parseJson.convertTo[User]

// Circe equivalent
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class User(id: Long, name: String, email: String)

val user = User(1, "John", "john@example.com")
val json = user.asJson.noSpaces
val parsed = decode[User](json) // Either[Error, User]
```

### From Argonaut to Circe
```scala
// Argonaut
import argonaut._
import Argonaut._

case class User(id: Long, name: String, email: String)

implicit val userCodec: CodecJson[User] = CodecJson.derive[User]

val user = User(1, "John", "john@example.com")
val json = user.asJson.toString()
val parsed = json.decodeEither[User]

// Circe equivalent
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class User(id: Long, name: String, email: String)

val user = User(1, "John", "john@example.com")
val json = user.asJson.noSpaces
val parsed = decode[User](json) // Either[Error, User]
```
## См. также
- [Play Framework](scala-play.md) — **Web framework** с **JSON** поддержкой
- [Cats](scala-cats.md) — Функциональная библиотека
- [Паттерны](../../basics/README.md) — Функциональные паттерны

