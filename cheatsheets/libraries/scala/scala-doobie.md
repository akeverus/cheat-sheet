---
title: "Doobie"
description: "Doobie - это функциональная библиотека для работы с JDBC в Scala, предоставляющая чистый, type-safe подход к взаимодействию с реляционными базами данных. Doobie интегрируется с Cats Effect и предоставляет composable, streaming API для работы с базами данных."
tags:
  - libraries
  - scala
  - scala-doobie
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Doobie

**Doobie** — это функциональная библиотека для работы с **JDBC** в **Scala**, предоставляющая чистый, **type-safe** подход к взаимодействию с реляционными базами данных. **Doobie** интегрируется с **Cats Effect** и предоставляет **composable**, **streaming API** для работы с базами данных.

## Полезные ссылки
- [Официальная документация Doobie](https://tpolecat.github.io/doobie/)
- [Doobie GitHub](https://github.com/tpolecat/doobie)
- [Doobie Examples](https://github.com/tpolecat/doobie/tree/series/0.9.x/modules/example)
- [Cats Effect](https://typelevel.org/cats-effect/)
- [FS2](https://fs2.io/)
- [[java-hikaricp|HikariCP]]

## Содержание

- [Основы Doobie](#основы-doobie)
  - [Подключение и конфигурация](#подключение-и-конфигурация)
  - [Transactor для управления соединениями](#transactor-для-управления-соединениями)
  - [Определение моделей и мапперов](#определение-моделей-и-мапперов)
- [CRUD операции](#crud-операции)
  - [Создание (Create)](#создание-create)
  - [Чтение (Read)](#чтение-read)
  - [Обновление (Update)](#обновление-update)
  - [Удаление (Delete)](#удаление-delete)
- [Продвинутые запросы](#продвинутые-запросы)
  - [Joins и сложные запросы](#joins-и-сложные-запросы)
  - [Fragments для динамических запросов](#fragments-для-динамических-запросов)
- [Транзакции](#транзакции)
  - [Управление транзакциями](#управление-транзакциями)
- [Streaming и большие данные](#streaming-и-большие-данные)
  - [Stream API](#stream-api)
- [Connection pooling и конфигурация](#connection-pooling-и-конфигурация)
  - [HikariCP конфигурация](#hikaricp-конфигурация)
- [Тестирование](#тестирование)
  - [Unit тесты](#unit-тесты)
  - [Integration тесты](#integration-тесты)
- [Обработка ошибок](#обработка-ошибок)
  - [Domain ошибки](#domain-ошибки)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Connection pooling](#connection-pooling)
  - [Query optimization](#query-optimization)
- [Лучшие практики](#лучшие-практики)
  - [Структура кода](#структура-кода)
  - [Error handling patterns](#error-handling-patterns)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging Doobie](#debugging-doobie)
- [Руководство по миграции](#руководство-по-миграции)
  - [From JDBC to Doobie](#from-jdbc-to-doobie)
  - [From Anorm to Doobie](#from-anorm-to-doobie)
  - [From Slick to Doobie](#from-slick-to-doobie)
- [См. также](#см-также)

## Основы Doobie

### Подключение и конфигурация

Зависимости **Doobie** и конфигурация подключения к **PostgreSQL** (Scala).

```scala
// build.sbt
libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC1",  // HikariCP integration
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1", // PostgreSQL support
  "org.postgresql" % "postgresql" % "42.5.1"
)

// application.conf
doobie {
  driver = "org.postgresql.Driver"
  url = "jdbc:postgresql://localhost:5432/myapp"
  user = "myuser"
  password = "mypass"
  maxConnections = 10
}
```

### Transactor для управления соединениями
```scala
import doobie._
import doobie.implicits._
import cats.effect.{IO, Resource}
import scala.concurrent.ExecutionContext
import doobie.hikari._
import doobie.util.ExecutionContexts

/
 * Transactor - абстракция для управления соединениями с БД в Doobie
 * Transactor управляет жизненным циклом соединений и транзакций
 * F[_] - эффект (обычно IO из Cats Effect)
 */
// Простой Transactor через DriverManager
// Transactor.fromDriverManager - создает transactor используя JDBC DriverManager
// Каждый запрос создает новое соединение (не рекомендуется для production)
val transactor: Transactor[IO] = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",  // JDBC драйвер для PostgreSQL
  "jdbc:postgresql://localhost:5432/myapp",  // URL базы данных
  "myuser",  // Имя пользователя
  "mypass"   // Пароль
)
// IO - эффект из Cats Effect для асинхронных операций

// HikariCP Transactor (рекомендуется для продакшена)
// HikariCP - быстрый connection pool для JDBC
// Resource[IO, Transactor[IO]] - управляемый ресурс с автоматическим закрытием
val hikariTransactor: Resource[IO, Transactor[IO]] = for {
  // ExecutionContexts.fixedThreadPool - пул потоков для выполнения соединений
  // 32 - размер пула потоков
  ce <- ExecutionContexts.fixedThreadPool[IO](32)  // Connection execution context

  // HikariTransactor.newHikariTransactor - создание transactor с HikariCP
  // Автоматически управляет пулом соединений
  xa <- HikariTransactor.newHikariTransactor[IO](
    "org.postgresql.Driver",  // JDBC драйвер
    "jdbc:postgresql://localhost:5432/myapp",  // URL БД
    "myuser",  // Пользователь
    "mypass",  // Пароль
    ce  // ExecutionContext для выполнения соединений
  )
} yield xa
// for-comprehension создает Resource, который автоматически закрывает пул при завершении

// Использование Transactor
// Resource.use - выполняет операцию с ресурсом, автоматически закрывая его после
val program = hikariTransactor.use { xa =>
  // sql"..." - SQL запрос как интерполированная строка
  // .query[Int] - запрос возвращающий Int
  // .unique - ожидает ровно одну строку результата
  // .transact(xa) - выполняет запрос через transactor
  sql"SELECT 42".query[Int].unique.transact(xa)
}
// Результат: IO[Int] - эффект который при выполнении вернет 42
```

### Определение моделей и мапперов
```scala
import java.time.Instant
import doobie.Meta

// Case class для модели данных
case class User(
  id: Long,
  name: String,
  email: String,
  createdAt: Instant,
  updatedAt: Option[Instant]
)

// Meta instances для type mapping
object UserMeta {
  implicit val instantMeta: Meta[Instant] = Meta[java.sql.Timestamp].imap(
    timestamp => timestamp.toInstant
  )(instant => java.sql.Timestamp.from(instant))

  implicit val userRead: Read[User] = Read[(Long, String, String, Instant, Option[Instant])].map {
    case (id, name, email, createdAt, updatedAt) =>
      User(id, name, email, createdAt, updatedAt)
  }

  implicit val userWrite: Write[User] = Write[(Long, String, String, Instant, Option[Instant])].contramap { user =>
    (user.id, user.name, user.email, user.createdAt, user.updatedAt)
  }
}

// Или использовать derive для автоматической генерации
import doobie.postgres.implicits._

case class Company(id: Long, name: String, address: String)

// Автоматическая генерация Meta instances для PostgreSQL
object CompanyMeta {
  implicit val companyRead: Read[Company] = Read[(Long, String, String)].map(Company.tupled)
  implicit val companyWrite: Write[Company] = Write[(Long, String, String)].contramap(Company.unapply(_).get)
}
```

## CRUD операции

### Создание (Create)
```scala
import doobie.implicits._
import cats.effect.IO

class UserRepository(xa: Transactor[IO]) {

  // Вставка одного пользователя
  def create(user: User): IO[User] = {
    sql"""
      INSERT INTO users (name, email, created_at)
      VALUES (${user.name}, ${user.email}, ${user.createdAt})
    """.update.withUniqueGeneratedKeys[Long]("id").transact(xa).map { id =>
      user.copy(id = id)
    }
  }

  // Вставка нескольких пользователей
  def createBatch(users: List[User]): IO[List[User]] = {
    val sql = """
      INSERT INTO users (name, email, created_at)
      VALUES (?, ?, ?)
    """

    Update[User](sql).updateManyWithGeneratedKeys[Long]("id")(users)
      .compile.toList.transact(xa).map { ids =>
        users.zip(ids).map { case (user, id) => user.copy(id = id) }
      }
  }

  // Вставка с возвратом всех полей
  def createAndReturn(user: User): IO[User] = {
    sql"""
      INSERT INTO users (name, email, created_at)
      VALUES (${user.name}, ${user.email}, ${user.createdAt})
      RETURNING id, name, email, created_at, updated_at
    """.query[User].unique.transact(xa)
  }

  // Upsert операция
  def upsert(user: User): IO[User] = {
    sql"""
      INSERT INTO users (id, name, email, created_at, updated_at)
      VALUES (${user.id}, ${user.name}, ${user.email}, ${user.createdAt}, ${user.updatedAt})
      ON CONFLICT (id) DO UPDATE SET
        name = EXCLUDED.name,
        email = EXCLUDED.email,
        updated_at = EXCLUDED.updated_at
      RETURNING id, name, email, created_at, updated_at
    """.query[User].unique.transact(xa)
  }
}
```

### Чтение (Read)
```scala
class UserRepository(xa: Transactor[IO]) {

  // Получение по ID
  def findById(id: Long): IO[Option[User]] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id = $id"
      .query[User].option.transact(xa)
  }

  // Получение всех пользователей
  def findAll(): IO[List[User]] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users ORDER BY id"
      .query[User].to[List].transact(xa)
  }

  // Пагинация
  def findAll(limit: Int, offset: Int): IO[List[User]] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users ORDER BY id LIMIT $limit OFFSET $offset"
      .query[User].to[List].transact(xa)
  }

  // Поиск по email
  def findByEmail(email: String): IO[Option[User]] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users WHERE email = $email"
      .query[User].option.transact(xa)
  }

  // Поиск с параметрами
  def search(namePattern: String, emailPattern: String): IO[List[User]] = {
    sql"""
      SELECT id, name, email, created_at, updated_at FROM users
      WHERE name ILIKE $namePattern OR email ILIKE $emailPattern
      ORDER BY name
    """.query[User].to[List].transact(xa)
  }

  // Комплексные запросы
  def findActiveUsers(): IO[List[User]] = {
    sql"""
      SELECT id, name, email, created_at, updated_at FROM users
      WHERE updated_at IS NOT NULL
      AND updated_at > NOW() - INTERVAL '30 days'
      ORDER BY updated_at DESC
    """.query[User].to[List].transact(xa)
  }

  // Агрегационные запросы
  def countUsers(): IO[Int] = {
    sql"SELECT COUNT(*) FROM users".query[Int].unique.transact(xa)
  }

  def getUserStats(): IO[(Int, Instant, Instant)] = {
    sql"""
      SELECT COUNT(*), MIN(created_at), MAX(created_at)
      FROM users
    """.query[(Int, Instant, Instant)].unique.transact(xa)
  }

  // Exists проверка
  def exists(id: Long): IO[Boolean] = {
    sql"SELECT EXISTS(SELECT 1 FROM users WHERE id = $id)".query[Boolean].unique.transact(xa)
  }

  // Stream для больших результатов
  def streamUsers(): fs2.Stream[IO, User] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users ORDER BY id"
      .query[User].stream.transact(xa)
  }
}
```

### Обновление (Update)
```scala
class UserRepository(xa: Transactor[IO]) {

  // Обновление всего пользователя
  def update(user: User): IO[Int] = {
    sql"""
      UPDATE users SET
        name = ${user.name},
        email = ${user.email},
        updated_at = ${user.updatedAt}
      WHERE id = ${user.id}
    """.update.run.transact(xa)
  }

  // Частичное обновление
  def updateName(id: Long, name: String): IO[Int] = {
    sql"UPDATE users SET name = $name, updated_at = NOW() WHERE id = $id"
      .update.run.transact(xa)
  }

  def updateEmail(id: Long, email: String): IO[Int] = {
    sql"UPDATE users SET email = $email, updated_at = NOW() WHERE id = $id"
      .update.run.transact(xa)
  }

  // Обновление нескольких полей
  def updateProfile(id: Long, name: String, email: String): IO[Int] = {
    sql"""
      UPDATE users SET
        name = $name,
        email = $email,
        updated_at = NOW()
      WHERE id = $id
    """.update.run.transact(xa)
  }

  // Bulk update
  def deactivateOldUsers(olderThan: Instant): IO[Int] = {
    sql"UPDATE users SET updated_at = NOW() WHERE created_at < $olderThan"
      .update.run.transact(xa)
  }

  // Update с возвратом обновленных данных
  def updateAndReturn(id: Long, name: String, email: String): IO[Option[User]] = {
    sql"""
      UPDATE users SET
        name = $name,
        email = $email,
        updated_at = NOW()
      WHERE id = $id
      RETURNING id, name, email, created_at, updated_at
    """.query[User].option.transact(xa)
  }

  // Optimistic locking
  def updateWithVersion(id: Long, name: String, version: Int): IO[Int] = {
    sql"""
      UPDATE users SET
        name = $name,
        version = version + 1,
        updated_at = NOW()
      WHERE id = $id AND version = $version
    """.update.run.transact(xa)
  }
}
```

### Удаление (Delete)
```scala
class UserRepository(xa: Transactor[IO]) {

  // Удаление по ID
  def delete(id: Long): IO[Int] = {
    sql"DELETE FROM users WHERE id = $id".update.run.transact(xa)
  }

  // Удаление нескольких
  def deleteMultiple(ids: List[Long]): IO[Int] = {
    val idsFragment = Fragments.in(fr"id", ids)
    (fr"DELETE FROM users WHERE" ++ idsFragment).update.run.transact(xa)
  }

  // Удаление по условию
  def deleteByEmail(email: String): IO[Int] = {
    sql"DELETE FROM users WHERE email = $email".update.run.transact(xa)
  }

  // Удаление старых неактивных пользователей
  def deleteInactiveUsers(olderThan: Instant): IO[Int] = {
    sql"""
      DELETE FROM users
      WHERE updated_at IS NULL
         OR updated_at < $olderThan
    """.update.run.transact(xa)
  }

  // Soft delete
  def softDelete(id: Long): IO[Int] = {
    sql"UPDATE users SET deleted = true, updated_at = NOW() WHERE id = $id"
      .update.run.transact(xa)
  }

  // Удаление всех
  def deleteAll(): IO[Int] = {
    sql"DELETE FROM users".update.run.transact(xa)
  }

  // Cascade delete
  def deleteUserAndPosts(userId: Long): IO[Int] = {
    val deleteUser = sql"DELETE FROM users WHERE id = $userId".update.run
    val deletePosts = sql"DELETE FROM posts WHERE user_id = $userId".update.run

    (deletePosts, deleteUser).mapN(_ + _).transact(xa)
  }
}
```

## Продвинутые запросы

### Joins и сложные запросы
```scala
case class Post(id: Long, userId: Long, title: String, content: String, createdAt: Instant)
case class UserWithPosts(user: User, posts: List[Post])

class AdvancedRepository(xa: Transactor[IO]) {

  // Inner join
  def findUsersWithPosts(): IO[List[(User, Post)]] = {
    sql"""
      SELECT u.id, u.name, u.email, u.created_at, u.updated_at,
             p.id, p.user_id, p.title, p.content, p.created_at
      FROM users u
      INNER JOIN posts p ON u.id = p.user_id
      ORDER BY u.id, p.created_at
    """.query[(User, Post)].to[List].transact(xa)
  }

  // Left join
  def findAllUsersWithPosts(): IO[List[UserWithPosts]] = {
    sql"""
      SELECT u.id, u.name, u.email, u.created_at, u.updated_at,
             p.id, p.title, p.content, p.created_at
      FROM users u
      LEFT JOIN posts p ON u.id = p.user_id AND p.created_at > NOW() - INTERVAL '30 days'
      ORDER BY u.id, p.created_at DESC
    """.query[(User, Option[Post])].to[List].transact(xa).map { results =>
      results.groupBy(_._1).map { case (user, userPosts) =>
        val posts = userPosts.flatMap(_._2)
        UserWithPosts(user, posts)
      }.toList
    }
  }

  // Комплексные joins с агрегацией
  def getUserPostStats(): IO[List[(Long, String, Int, Instant)]] = {
    sql"""
      SELECT u.id, u.name, COUNT(p.id), MAX(p.created_at)
      FROM users u
      LEFT JOIN posts p ON u.id = p.user_id
      GROUP BY u.id, u.name
      HAVING COUNT(p.id) > 0
      ORDER BY COUNT(p.id) DESC
    """.query[(Long, String, Int, Instant)].to[List].transact(xa)
  }

  // Рекурсивные запросы (древовидные структуры)
  case class Category(id: Long, name: String, parentId: Option[Long])

  def findCategoryTree(): IO[List[Category]] = {
    sql"""
      WITH RECURSIVE category_tree AS (
        SELECT id, name, parent_id, 0 as level
        FROM categories
        WHERE parent_id IS NULL

        UNION ALL

        SELECT c.id, c.name, c.parent_id, ct.level + 1
        FROM categories c
        INNER JOIN category_tree ct ON c.parent_id = ct.id
      )
      SELECT id, name, parent_id FROM category_tree
      ORDER BY level, id
    """.query[Category].to[List].transact(xa)
  }

  // Full-text search
  def searchPosts(searchTerm: String): IO[List[(Post, User)]] = {
    sql"""
      SELECT p.id, p.user_id, p.title, p.content, p.created_at,
             u.id, u.name, u.email, u.created_at, u.updated_at
      FROM posts p
      INNER JOIN users u ON p.user_id = u.id
      WHERE p.title ILIKE ${"%" + searchTerm + "%"}
         OR p.content ILIKE ${"%" + searchTerm + "%"}
      ORDER BY p.created_at DESC
    """.query[(Post, User)].to[List].transact(xa)
  }

  // Window functions
  def rankUsersByPostCount(): IO[List[(User, Int)]] = {
    sql"""
      SELECT u.id, u.name, u.email, u.created_at, u.updated_at, ranking
      FROM (
        SELECT u.*, ROW_NUMBER() OVER (ORDER BY post_count DESC) as ranking
        FROM users u
        LEFT JOIN (
          SELECT user_id, COUNT(*) as post_count
          FROM posts
          GROUP BY user_id
        ) pc ON u.id = pc.user_id
      ) ranked_users
      WHERE ranking <= 10
    """.query[(User, Int)].to[List].transact(xa)
  }
}
```

### Fragments для динамических запросов
```scala
import doobie.implicits._
import doobie.Fragments

class DynamicQueryRepository(xa: Transactor[IO]) {

  // Динамическая фильтрация
  def findUsers(filters: UserFilters): IO[List[User]] = {
    val selectFragment = fr"SELECT id, name, email, created_at, updated_at FROM users"
    val whereFragment = buildWhereClause(filters)
    val orderFragment = fr"ORDER BY" ++ Fragment.const(filters.sortBy)

    val query = selectFragment ++ whereFragment ++ orderFragment
    query.query[User].to[List].transact(xa)
  }

  private def buildWhereClause(filters: UserFilters): Fragment = {
    val conditions = List(
      filters.name.map(name => fr"name ILIKE ${"%" + name + "%"}"),
      filters.email.map(email => fr"email ILIKE ${"%" + email + "%"}"),
      filters.createdAfter.map(date => fr"created_at > $date"),
      filters.createdBefore.map(date => fr"created_at < $date")
    ).flatten

    if (conditions.nonEmpty) {
      fr"WHERE" ++ conditions.reduce(_ ++ fr"AND" ++ _)
    } else {
      Fragment.empty
    }
  }

  // Динамическая сортировка
  def findUsersSorted(sortField: String, sortOrder: String): IO[List[User]] = {
    val order = if (sortOrder.toLowerCase == "desc") fr"DESC" else fr"ASC"
    val query = fr"SELECT id, name, email, created_at, updated_at FROM users" ++
                fr"ORDER BY" ++ Fragment.const(sortField) ++ order

    query.query[User].to[List].transact(xa)
  }

  // Fragments для сложных условий
  def findUsersComplex(name: Option[String], minAge: Option[Int], status: Option[String]): IO[List[User]] = {
    val conditions = List(
      name.map(n => fr"name ILIKE ${"%" + n + "%"}"),
      minAge.map(age => fr"EXTRACT(YEAR FROM AGE(created_at)) >= $age"),
      status.map(s => fr"status = $s")
    ).flatten

    val whereClause = if (conditions.nonEmpty) {
      fr"WHERE" ++ conditions.reduce(_ ++ fr"AND" ++ _)
    } else Fragment.empty

    val query = fr"SELECT id, name, email, created_at, updated_at FROM users" ++ whereClause
    query.query[User].to[List].transact(xa)
  }
}

case class UserFilters(
  name: Option[String],
  email: Option[String],
  createdAfter: Option[Instant],
  createdBefore: Option[Instant],
  sortBy: String = "id ASC"
)
```

## Транзакции

### Управление транзакциями
```scala
class TransactionalRepository(xa: Transactor[IO]) {

  // Простая транзакция
  def transferPoints(fromUserId: Long, toUserId: Long, points: Int): IO[Either[String, Unit]] = {
    val transaction = for {
      fromUser <- sql"SELECT points FROM users WHERE id = $fromUserId".query[Int].option
      toUserExists <- sql"SELECT EXISTS(SELECT 1 FROM users WHERE id = $toUserId)".query[Boolean].unique

      result <- (fromUser, toUserExists) match {
        case (Some(fromPoints), true) if fromPoints >= points =>
          for {
            _ <- sql"UPDATE users SET points = points - $points WHERE id = $fromUserId".update.run
            _ <- sql"UPDATE users SET points = points + $points WHERE id = $toUserId".update.run
          } yield Right(())
        case (Some(_), true) =>
          IO.pure(Left("Insufficient points"))
        case _ =>
          IO.pure(Left("User not found"))
      }
    } yield result

    transaction.transact(xa)
  }

  // Комплексная транзакция
  def createUserWithProfile(user: User, profile: UserProfile): IO[Either[String, (User, UserProfile)]] = {
    val transaction = for {
      // Создание пользователя
      userId <- sql"""
        INSERT INTO users (name, email, created_at)
        VALUES (${user.name}, ${user.email}, ${user.createdAt})
      """.update.withUniqueGeneratedKeys[Long]("id")

      // Создание профиля
      _ <- sql"""
        INSERT INTO user_profiles (user_id, bio, avatar_url)
        VALUES ($userId, ${profile.bio}, ${profile.avatarUrl})
      """.update.run

      // Возврат созданных сущностей
      createdUser = user.copy(id = userId)
      createdProfile = profile.copy(userId = userId)

    } yield Right((createdUser, createdProfile))

    transaction.transact(xa).recover {
      case ex: java.sql.SQLIntegrityConstraintViolationException =>
        Left("User with this email already exists")
      case ex =>
        Left(s"Database error: ${ex.getMessage}")
    }
  }

  // Вложенные транзакции
  def complexBusinessOperation(order: Order): IO[Either[String, OrderResult]] = {
    val transaction = for {
      // Проверка наличия товаров
      itemsAvailable <- checkItemsAvailability(order.items)

      result <- if (itemsAvailable) {
        for {
          // Создание заказа
          orderId <- createOrder(order)

          // Резервирование товаров
          _ <- reserveItems(orderId, order.items)

          // Обработка платежа
          paymentResult <- processPayment(orderId, order.payment)

          // Отправка уведомления
          _ <- sendOrderConfirmation(orderId)

        } yield Right(OrderResult(orderId, "Order created successfully"))
      } else {
        IO.pure(Left("Some items are not available"))
      }
    } yield result

    transaction.transact(xa)
  }

  // Savepoints для частичных rollback
  def partialRollbackExample(): IO[Unit] = {
    val program = for {
      _ <- sql"INSERT INTO logs (message) VALUES ('Starting operation')".update.run

      // Создание savepoint
      _ <- FC.setSavepoint("before_user_creation")

      userId <- sql"INSERT INTO users (name, email) VALUES ('test', 'test@example.com')"
                .update.withUniqueGeneratedKeys[Long]("id")

      // Имитация ошибки
      _ <- sql"INSERT INTO invalid_table (data) VALUES ('error')".update.run
                .onError(_ => FC.rollbackToSavepoint("before_user_creation"))

      _ <- sql"INSERT INTO logs (message) VALUES ('Operation completed')".update.run

    } yield ()

    program.transact(xa)
  }
}
```

## Streaming и большие данные

### Stream API
```scala
import fs2.Stream
import doobie.implicits._

class StreamingRepository(xa: Transactor[IO]) {

  // Stream для чтения больших объемов данных
  def streamAllUsers(): Stream[IO, User] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users"
      .query[User]
      .stream
      .transact(xa)
  }

  // Обработка данных в stream
  def processUsersInStream(): Stream[IO, ProcessedUser] = {
    streamAllUsers()
      .filter(_.updatedAt.exists(_.isAfter(Instant.now().minus(30, ChronoUnit.DAYS))))
      .map(user => ProcessedUser(user.name, user.email.toUpperCase))
      .chunkN(100) // Группировка по 100 элементов
      .map(chunk => chunk.toList)
      .flatMap(processBatch)
  }

  // Stream с пагинацией
  def streamUsersPaginated(pageSize: Int): Stream[IO, User] = {
    Stream.unfoldEval(0) { offset =>
      findUsersPaginated(pageSize, offset).map { users =>
        if (users.nonEmpty) Some((users, offset + pageSize))
        else None
      }
    }.flatten
  }

  private def findUsersPaginated(limit: Int, offset: Int): IO[List[User]] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users ORDER BY id LIMIT $limit OFFSET $offset"
      .query[User].to[List].transact(xa)
  }

  // Stream для записи
  def insertUsersStream(users: Stream[IO, User]): Stream[IO, Long] = {
    users.chunkN(100).flatMap { chunk =>
      Stream.eval {
        val sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)"
        Update[User](sql).updateManyWithGeneratedKeys[Long]("id")(chunk.toList).transact(xa)
      }.flatMap(ids => Stream.emits(ids))
    }
  }

  // Комплексная stream обработка
  def migrateUsers(): Stream[IO, MigrationResult] = {
    streamAllUsers()
      .evalMap { user =>
        validateUser(user).map(valid => (user, valid))
      }
      .collect { case (user, true) => user } // Только валидные пользователи
      .map(transformUser) // Преобразование данных
      .chunkN(50) // Группировка для batch операций
      .evalMap { chunk =>
        migrateBatch(chunk.toList).map(MigrationResult(chunk.size, _))
      }
  }

  private def validateUser(user: User): IO[Boolean] = IO.pure(user.name.nonEmpty && user.email.contains("@"))
  private def transformUser(user: User): User = user.copy(name = user.name.trim)
  private def migrateBatch(users: List[User]): IO[Int] = {
    // Имитация миграции
    IO.pure(users.size)
  }
}

case class ProcessedUser(name: String, email: String)
case class MigrationResult(batchSize: Int, migratedCount: Int)
```

## Connection pooling и конфигурация

### HikariCP конфигурация
```scala
import doobie.hikari._
import cats.effect.{IO, Resource}
import scala.concurrent.ExecutionContext

object DatabaseConfig {

  // Конфигурация HikariCP
  def createTransactor(config: DbConfig): Resource[IO, HikariTransactor[IO]] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](config.numThreads)
      xa <- HikariTransactor.newHikariTransactor[IO](
        config.driver,
        config.url,
        config.user,
        config.password,
        ce
      )
    } yield {
      // Настройка HikariCP
      xa.kernel.setMaximumPoolSize(config.maxPoolSize)
      xa.kernel.setMinimumIdle(config.minIdle)
      xa.kernel.setIdleTimeout(config.idleTimeout)
      xa.kernel.setMaxLifetime(config.maxLifetime)
      xa.kernel.setConnectionTimeout(config.connectionTimeout)
      xa.kernel.setLeakDetectionThreshold(config.leakDetectionThreshold)

      xa
    }
  }

  // Мониторинг пула соединений
  def createMonitoredTransactor(config: DbConfig): Resource[IO, HikariTransactor[IO]] = {
    createTransactor(config).evalMap { xa =>
      IO {
        // Регистрация метрик
        val mxBean = xa.kernel.getHikariPoolMXBean

        // Логирование статистики пула
        IO.delay {
          println(s"Pool stats - Active: ${mxBean.getActiveConnections}, Idle: ${mxBean.getIdleConnections}, Total: ${mxBean.getTotalConnections}")
        }.unsafeRunAndForget()

        xa
      }
    }
  }
}

case class DbConfig(
  driver: String,
  url: String,
  user: String,
  password: String,
  numThreads: Int = 32,
  maxPoolSize: Int = 10,
  minIdle: Int = 5,
  idleTimeout: Long = 300000, // 5 minutes
  maxLifetime: Long = 600000, // 10 minutes
  connectionTimeout: Long = 30000, // 30 seconds
  leakDetectionThreshold: Long = 60000 // 1 minute
)
```

## Тестирование

### Unit тесты
```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor

class UserRepositorySpec extends AnyFlatSpec with Matchers {

  // In-memory H2 для тестов
  val testTransactor: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    "",
    ""
  )

  val repository = new UserRepository(testTransactor)

  "UserRepository" should "create and retrieve user" in {
    val user = User(0, "Test User", "test@example.com", Instant.now())

    val testProgram = for {
      created <- repository.create(user)
      retrieved <- repository.findById(created.id)
    } yield (created, retrieved)

    val (created, retrieved) = testProgram.unsafeRunSync()

    created.id should be > 0L
    retrieved shouldBe Some(created)
  }

  it should "handle non-existent user" in {
    val result = repository.findById(999L).unsafeRunSync()
    result shouldBe None
  }

  it should "update user successfully" in {
    val user = User(0, "Original", "original@example.com", Instant.now())
    val updatedName = "Updated Name"

    val testProgram = for {
      created <- repository.create(user)
      updateCount <- repository.updateName(created.id, updatedName)
      updated <- repository.findById(created.id)
    } yield (updateCount, updated)

    val (updateCount, updated) = testProgram.unsafeRunSync()

    updateCount shouldBe 1
    updated.map(_.name) shouldBe Some(updatedName)
  }
}
```

### Integration тесты
```scala
import org.testcontainers.containers.PostgreSQLContainer
import cats.effect.{IO, Resource}

class UserRepositoryIntegrationSpec extends AnyFlatSpec with Matchers {

  // TestContainers для PostgreSQL
  val postgresContainer = new PostgreSQLContainer("postgres:13")
  postgresContainer.start()

  val testTransactor: Transactor[IO] = Transactor.fromDriverManager[IO](
    postgresContainer.getDriverClassName,
    postgresContainer.getJdbcUrl,
    postgresContainer.getUsername,
    postgresContainer.getPassword
  )

  val repository = new UserRepository(testTransactor)

  "UserRepository integration" should "perform CRUD operations" in {
    val user = User(0, "Integration Test", "integration@example.com", Instant.now())

    // Create
    val created = repository.create(user).unsafeRunSync()
    created.id should be > 0L

    // Read
    val retrieved = repository.findById(created.id).unsafeRunSync()
    retrieved shouldBe Some(created)

    // Update
    val newName = "Updated Integration Test"
    val updateCount = repository.updateName(created.id, newName).unsafeRunSync()
    updateCount shouldBe 1

    val updated = repository.findById(created.id).unsafeRunSync()
    updated.map(_.name) shouldBe Some(newName)

    // Delete
    val deleteCount = repository.delete(created.id).unsafeRunSync()
    deleteCount shouldBe 1

    val afterDelete = repository.findById(created.id).unsafeRunSync()
    afterDelete shouldBe None
  }

  // Очистка
  postgresContainer.stop()
}
```

## Обработка ошибок

### Domain ошибки
```scala
import doobie.util.invariant._

sealed trait RepositoryError
case class NotFoundError(resource: String, id: Any) extends RepositoryError
case class ValidationError(message: String) extends RepositoryError
case class DatabaseError(cause: Throwable) extends RepositoryError

class SafeUserRepository(xa: Transactor[IO]) {

  def findById(id: Long): IO[Either[RepositoryError, User]] = {
    sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id = $id"
      .query[User]
      .option
      .transact(xa)
      .map {
        case Some(user) => Right(user)
        case None => Left(NotFoundError("User", id))
      }
      .recover {
        case e: java.sql.SQLException => Left(DatabaseError(e))
      }
  }

  def create(user: User): IO[Either[RepositoryError, User]] = {
    validateUser(user) match {
      case Left(validationError) =>
        IO.pure(Left(validationError))
      case Right(validUser) =>
        sql"""
          INSERT INTO users (name, email, created_at)
          VALUES (${validUser.name}, ${validUser.email}, ${validUser.createdAt})
        """.update.withUniqueGeneratedKeys[Long]("id")
          .transact(xa)
          .map(id => Right(validUser.copy(id = id)))
          .recover {
            case e: java.sql.SQLIntegrityConstraintViolationException =>
              Left(ValidationError("User with this email already exists"))
            case e: java.sql.SQLException =>
              Left(DatabaseError(e))
          }
    }
  }

  private def validateUser(user: User): Either[RepositoryError, User] = {
    if (user.name.trim.isEmpty) Left(ValidationError("Name cannot be empty"))
    else if (!user.email.contains("@")) Left(ValidationError("Invalid email format"))
    else Right(user.copy(name = user.name.trim))
  }

  // Использование EitherT для composition
  import cats.data.EitherT

  def complexOperation(userId: Long, data: String): IO[Either[RepositoryError, User]] = {
    (for {
      user <- EitherT(findById(userId))
      validatedUser <- EitherT(IO.pure(validateUser(user)))
      updatedUser <- EitherT(updateUser(validatedUser.id, data))
    } yield updatedUser).value
  }

  private def updateUser(id: Long, data: String): IO[Either[RepositoryError, User]] = {
    sql"UPDATE users SET data = $data WHERE id = $id"
      .update.run.transact(xa)
      .flatMap {
        case 0 => IO.pure(Left(NotFoundError("User", id)))
        case _ => findById(id).map(_.left.map(DatabaseError(_)))
      }
  }
}
```

## Оптимизация производительности

### Connection pooling
```scala
// application.conf
doobie {
  hikari {
    maximumPoolSize = 20
    minimumIdle = 5
    idleTimeout = 300000
    maxLifetime = 600000
    leakDetectionThreshold = 60000
    connectionTimeout = 30000
  }
}

// Настройка для высоконагруженных систем
val highLoadConfig = DbConfig(
  driver = "org.postgresql.Driver",
  url = "jdbc:postgresql://localhost:5432/myapp",
  user = "myuser",
  password = "mypass",
  numThreads = 64,
  maxPoolSize = 50,
  minIdle = 10,
  idleTimeout = 600000, // 10 minutes
  maxLifetime = 1800000, // 30 minutes
  connectionTimeout = 60000, // 1 minute
  leakDetectionThreshold = 120000 // 2 minutes
)
```

### Query optimization
```scala
class OptimizedRepository(xa: Transactor[IO]) {

  // Batch операции
  def createBatchOptimized(users: List[User]): IO[List[User]] = {
    val sql = "INSERT INTO users (name, email, created_at) VALUES (?, ?, ?)"
    Update[User](sql)
      .updateManyWithGeneratedKeys[Long]("id")(users)
      .compile.toList.transact(xa)
      .map(ids => users.zip(ids).map { case (user, id) => user.copy(id = id) })
  }

  // Использование индексов
  def findUsersByEmailDomain(domain: String): IO[List[User]] = {
    // Предполагается, что есть индекс на email
    sql"SELECT id, name, email, created_at, updated_at FROM users WHERE email LIKE ${"%" + domain}"
      .query[User].to[List].transact(xa)
  }

  // Пагинация с курсором для больших таблиц
  def findUsersCursor(cursor: Option[Long], limit: Int): IO[(List[User], Option[Long])] = {
    val baseQuery = sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id > $cursor ORDER BY id"
    val query = cursor.fold(sql"SELECT id, name, email, created_at, updated_at FROM users ORDER BY id")(baseQuery)

    query.query[User].stream.take(limit + 1).compile.toList.transact(xa).map { users =>
      if (users.size > limit) {
        val resultUsers = users.dropRight(1)
        val nextCursor = resultUsers.lastOption.map(_.id)
        (resultUsers, nextCursor)
      } else {
        (users, None)
      }
    }
  }

  // Кэширование prepared statements
  private val findByIdQuery = sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id = ?".query[User]

  def findByIdCached(id: Long): IO[Option[User]] = {
    findByIdQuery.option.transact(xa)
  }

  // Оптимизация N+1 проблемы
  def findUsersWithPosts(userIds: List[Long]): IO[List[(User, List[Post])]] = {
    val usersQuery = sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id IN (${userIds})"
    val postsQuery = sql"SELECT id, user_id, title, content, created_at FROM posts WHERE user_id IN (${userIds})"

    (usersQuery.query[User].to[List], postsQuery.query[Post].to[List])
      .mapN { (users, posts) =>
        val postsByUserId = posts.groupBy(_.userId)
        users.map(user => (user, postsByUserId.getOrElse(user.id, Nil)))
      }.transact(xa)
  }

  // Stream processing для больших данных
  def processLargeDataset(): Stream[IO, ProcessedData] = {
    sql"SELECT id, data FROM large_table".query[(Long, String)].stream
      .transact(xa)
      .chunkN(1000) // Обработка чанками
      .evalMap { chunk =>
        IO.parTraverseN(4)(chunk.toList) { case (id, data) =>
          processData(id, data)
        }
      }
      .flatMap(Stream.emits)
  }

  private def processData(id: Long, data: String): IO[ProcessedData] = {
    // Имитация тяжелой обработки
    IO.sleep(10.millis) *> IO.pure(ProcessedData(id, data.length))
  }
}

case class ProcessedData(id: Long, length: Int)
```

## Лучшие практики

### Структура кода
```scala
// Правильная организация репозиториев
package repositories

import cats.effect.{IO, Resource}
import doobie.{Transactor, ConnectionIO}

trait UserRepository {
  def findById(id: Long): IO[Option[User]]
  def findAll(): IO[List[User]]
  def create(user: User): IO[User]
  def update(user: User): IO[Int]
  def delete(id: Long): IO[Int]
}

class DoobieUserRepository(xa: Transactor[IO]) extends UserRepository {

  // Композиция операций в ConnectionIO
  private def findByIdQuery(id: Long): ConnectionIO[Option[User]] =
    sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id = $id"
      .query[User].option

  private def createQuery(user: User): ConnectionIO[User] =
    sql"INSERT INTO users (name, email, created_at) VALUES (${user.name}, ${user.email}, ${user.createdAt})"
      .update.withUniqueGeneratedKeys[Long]("id")
      .map(id => user.copy(id = id))

  override def findById(id: Long): IO[Option[User]] =
    findByIdQuery(id).transact(xa)

  override def create(user: User): IO[User] =
    createQuery(user).transact(xa)

  // Batch operations
  def createBatch(users: List[User]): IO[List[User]] = {
    val queries = users.map(createQuery)
    queries.sequence.transact(xa)
  }
}

// Фабрика репозиториев
object RepositoryFactory {
  def createUserRepository(xa: Transactor[IO]): UserRepository =
    new DoobieUserRepository(xa)

  def createAllRepositories(xa: Transactor[IO]): Repositories =
    Repositories(
      userRepository = createUserRepository(xa),
      // другие репозитории
    )
}

case class Repositories(
  userRepository: UserRepository,
  // другие репозитории
)
```

### Error handling patterns
```scala
import cats.data.EitherT
import cats.implicits._

sealed trait AppError
case class DatabaseError(message: String) extends AppError
case class NotFound(resource: String) extends AppError
case class ValidationError(errors: List[String]) extends AppError

type Result[T] = Either[AppError, T]
type ResultIO[T] = EitherT[IO, AppError, T]

// Extension methods для ResultIO
implicit class ResultIOOps[T](resultIO: ResultIO[T]) {
  def recoverDatabaseErrors: ResultIO[T] = resultIO.leftFlatMap {
    case DatabaseError(_) => EitherT.leftT[IO, T](DatabaseError("Database operation failed"))
    case other => EitherT.leftT[IO, T](other)
  }

  def logErrors(service: String): ResultIO[T] = resultIO.leftSemiflatTap { error =>
    IO.println(s"[$service] Error: $error")
  }
}

class ErrorHandlingRepository(xa: Transactor[IO]) {

  def findUser(id: Long): ResultIO[User] = {
    EitherT {
      sql"SELECT id, name, email, created_at, updated_at FROM users WHERE id = $id"
        .query[User].option.transact(xa)
        .map {
          case Some(user) => Right(user)
          case None => Left(NotFound(s"User with id $id"))
        }
    }.recoverDatabaseErrors.logErrors("UserRepository")
  }

  def createUser(user: User): ResultIO[User] = {
    EitherT {
      validateUser(user) match {
        case Left(validationErrors) =>
          IO.pure(Left(ValidationError(validationErrors)))
        case Right(validUser) =>
          sql"INSERT INTO users (name, email, created_at) VALUES (${validUser.name}, ${validUser.email}, ${validUser.createdAt})"
            .update.withUniqueGeneratedKeys[Long]("id")
            .transact(xa)
            .map(id => Right(validUser.copy(id = id)))
            .recover {
              case e: java.sql.SQLIntegrityConstraintViolationException =>
                Left(DatabaseError("User with this email already exists"))
            }
      }
    }.logErrors("UserRepository")
  }

  private def validateUser(user: User): Either[List[String], User] = {
    val errors = List(
      if (user.name.trim.isEmpty) Some("Name cannot be empty") else None,
      if (!user.email.contains("@")) Some("Invalid email format") else None
    ).flatten

    if (errors.nonEmpty) Left(errors)
    else Right(user.copy(name = user.name.trim))
  }

  // Композиция операций с error handling
  def transferPoints(fromId: Long, toId: Long, points: Int): ResultIO[Unit] = {
    for {
      fromUser <- findUser(fromId)
      toUser <- findUser(toId)
      _ <- validateTransfer(fromUser, toUser, points)
      _ <- performTransfer(fromId, toId, points)
    } yield ()
  }

  private def validateTransfer(fromUser: User, toUser: User, points: Int): ResultIO[Unit] = {
    if (points <= 0) EitherT.leftT(DatabaseError("Points must be positive"))
    else if (fromUser.points < points) EitherT.leftT(DatabaseError("Insufficient points"))
    else EitherT.rightT(())
  }

  private def performTransfer(fromId: Long, toId: Long, points: Int): ResultIO[Unit] = {
    EitherT {
      val transaction = for {
        _ <- sql"UPDATE users SET points = points - $points WHERE id = $fromId".update.run
        _ <- sql"UPDATE users SET points = points + $points WHERE id = $toId".update.run
      } yield ()

      transaction.transact(xa).map(Right(_)).recover {
        case e => Left(DatabaseError(e.getMessage))
      }
    }
  }
}
```

## Устранение неполадок

### Common Issues
```scala
object DoobieTroubleshooting {

  // Проблема: Connection leaks
  // Решение: Всегда использовать transactor правильно
  val leakyCode = {
    // Плохо: не завершаем транзакцию
    val result = sql"SELECT 1".query[Int].unique // Забыт .transact(xa)
  }

  val correctCode = {
    // Хорошо: завершаем транзакцию
    val result = sql"SELECT 1".query[Int].unique.transact(xa)
  }

  // Проблема: Thread starvation
  // Решение: Использовать подходящий ExecutionContext
  val blockingOps = {
    import cats.effect.Blocker

    val blockerResource = Blocker[IO]

    blockerResource.use { blocker =>
      sql"SELECT heavy_computation()".query[String].unique.transact(xa)
    }
  }

  // Проблема: SQL injection
  // Решение: Использовать parameterized queries
  val vulnerable = sql"SELECT * FROM users WHERE name = '$userInput'" // Уязвимо!

  val safe = sql"SELECT * FROM users WHERE name = $userInput" // Безопасно

  // Проблема: Large result sets
  // Решение: Использовать streams
  val memoryHog = sql"SELECT * FROM huge_table".query[Row].to[List].transact(xa)

  val memorySafe = sql"SELECT * FROM huge_table".query[Row].stream
    .chunkN(1000)
    .evalMap(processChunk)
    .compile.drain
    .transact(xa)

  // Проблема: Connection pool exhaustion
  // Решение: Настроить timeouts и limits
  val properConfig = HikariConfig()
  properConfig.setMaximumPoolSize(20)
  properConfig.setMinimumIdle(5)
  properConfig.setIdleTimeout(300000)
  properConfig.setMaxLifetime(600000)
  properConfig.setLeakDetectionThreshold(60000)

  // Проблема: Slow queries
  // Решение: Добавить индексы и оптимизировать запросы
  val optimized = sql"""
    SELECT u.* FROM users u
    INNER JOIN user_index ui ON u.id = ui.user_id
    WHERE ui.active = true
  """.query[User] // Использует индекс

  // Проблема: Type mapping errors
  // Решение: Правильные Meta instances
  implicit val instantMeta: Meta[Instant] = Meta[Timestamp].imap(_.toInstant)(Timestamp.from)

  // Проблема: Transaction isolation issues
  // Решение: Явно задавать уровень изоляции
  val serializableTransaction = sql"SELECT * FROM accounts".query[Account]
    .to[List].transact(xa)
    .map(_.sum) // Может быть race condition

  val safeTransaction = {
    import doobie.syntax.connectionio._
    (for {
      _ <- sql"SET TRANSACTION ISOLATION LEVEL SERIALIZABLE".update.run
      accounts <- sql"SELECT * FROM accounts".query[Account].to[List]
      sum = accounts.map(_.balance).sum
    } yield sum).transact(xa)
  }
}
```

### Debugging Doobie
```scala
// Логирование SQL запросов
val loggingTransactor = Transactor.after.set(xa, HC.rollback)

val loggedQuery = for {
  _ <- sql"SELECT 1".query[Int].unique
} yield ()

loggedQuery.transact(loggingTransactor)

// Кастомный логгер
import doobie.util.log.LogHandler

implicit val logHandler: LogHandler = LogHandler {
  case Success(sql, args, exec, proc) =>
    println(s"OK [$exec ms] $sql with $args")
  case ProcessingFailure(sql, args, exec, proc, t) =>
    println(s"ERROR [$exec ms] $sql with $args: ${t.getMessage}")
  case ExecFailure(sql, args, exec, t) =>
    println(s"EXEC FAIL [$exec ms] $sql with $args: ${t.getMessage}")
}

// Проверка сгенерированного SQL
val query = sql"SELECT id, name FROM users WHERE id = $userId"
println(query.sql) // Печатает сгенерированный SQL
println(query.args) // Печатает аргументы

// Benchmarking
import cats.effect.Clock

def benchmarkQuery[T](query: ConnectionIO[T]): IO[(T, Long)] = {
  Clock[IO].realTime.flatMap { start =>
    query.transact(xa).flatMap { result =>
      Clock[IO].realTime.map { end =>
        (result, end - start)
      }
    }
  }
}

// Использование
val (result, duration) = benchmarkQuery(sql"SELECT COUNT(*) FROM users".query[Int].unique).unsafeRunSync()
println(s"Query took ${duration}ms")
```

## Руководство по миграции

### From JDBC to Doobie
```scala
// JDBC approach
def findUser(id: Long): User = {
  val conn = DriverManager.getConnection(url, user, password)
  try {
    val stmt = conn.prepareStatement("SELECT id, name, email FROM users WHERE id = ?")
    stmt.setLong(1, id)
    val rs = stmt.executeQuery()
    if (rs.next()) {
      User(rs.getLong("id"), rs.getString("name"), rs.getString("email"))
    } else {
      throw new RuntimeException("User not found")
    }
  } finally {
    conn.close()
  }
}

// Doobie equivalent
def findUser(id: Long): IO[User] = {
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User].unique.transact(xa)
}
```

### From Anorm to Doobie
```scala
// Anorm
def findById(id: Long): Future[Option[User]] = Future {
  db.withConnection { implicit conn =>
    SQL("SELECT id, name, email FROM users WHERE id = {id}")
      .on("id" -> id)
      .as(userParser.singleOpt)
  }
}

// Doobie
def findById(id: Long): IO[Option[User]] = {
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User].option.transact(xa)
}
```

### From Slick to Doobie
```scala
// Slick
def findById(id: Long): Future[Option[User]] = {
  db.run(users.filter(_.id === id).result.headOption)
}

// Doobie
def findById(id: Long): IO[Option[User]] = {
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User].option.transact(xa)
}
```
## См. также
- [[scala-cats|Cats]] — Функциональная библиотека
- [[scala-slick|Slick]] — Альтернативная **database library**
- [[scala-play|Play Framework]] — **Web framework**
- [[postgres-basics|PostgreSQL]] — **Database**

