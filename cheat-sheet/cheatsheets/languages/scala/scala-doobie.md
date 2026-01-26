---
title: "Scala Doobie"
description: "Полное руководство по Doobie в Scala: функциональный JDBC слой, типобезопасные запросы, транзакции, интеграция с Cats Effect"
tags: ["scala", "doobie", "database", "jdbc", "functional-programming", "cats-effect"]
difficulty: "advanced"
prerequisites: ["scala/scala-fp-advanced.md", "scala/scala-cats.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-fp-advanced.md", "scala/scala-cats.md", "scala/scala-slick.md"]
---

# Scala Doobie

Кратко: полное руководство по Doobie в Scala: функциональный JDBC слой, типобезопасные запросы, транзакции, интеграция с Cats Effect.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [Doobie Documentation](https://tpolecat.github.io/doobie/)
- [Doobie GitHub](https://github.com/tpolecat/doobie)

### См. также
- `./scala-fp-advanced.md` - продвинутое функциональное программирование
- `./scala-cats.md` - Cats библиотека
- `./scala-slick.md` - Slick ORM

## Содержание

- [Введение в Doobie](#введение-в-doobie)
- [Базовые концепции](#базовые-концепции)
- [Создание подключений](#создание-подключений)
- [Запросы](#запросы)
- [Транзакции](#транзакции)
- [Обработка ошибок](#обработка-ошибок)
- [Интеграция с Cats Effect](#интеграция-с-cats-effect)
- [Лучшие практики](#лучшие-практики)

## Введение в Doobie

Doobie - это функциональная библиотека для работы с базами данных в Scala, построенная на основе JDBC и Cats Effect. Doobie предоставляет типобезопасный и композируемый API для работы с базами данных, который следует принципам функционального программирования и позволяет создавать чистый, тестируемый и поддерживаемый код.

Doobie основан на концепции функциональных эффектов, где все операции с базой данных представлены как эффекты, которые можно комбинировать и трансформировать. Это делает код более предсказуемым и позволяет компилятору проверять корректность использования эффектов. Doobie интегрируется с Cats Effect, что обеспечивает асинхронное выполнение, конкурентность, и управление ресурсами.

### Основные характеристики

- **Функциональный подход**: все операции представлены как чистые функции и эффекты. Это делает код более предсказуемым, тестируемым и композируемым. Функциональный подход позволяет создавать код, который легко понимать, тестировать и поддерживать.

- **Type-safe**: типобезопасная работа с SQL запросами. Doobie использует типы для представления SQL запросов и результатов, что позволяет компилятору проверять корректность кода на этапе компиляции. Это предотвращает ошибки, связанные с неправильным использованием SQL.

- **Композируемость**: SQL запросы можно легко комбинировать. Композируемость позволяет создавать сложные запросы из простых компонентов, что делает код более модульным и переиспользуемым.

- **Интеграция с Cats Effect**: асинхронное выполнение и управление ресурсами через Cats Effect. Интеграция с Cats Effect обеспечивает эффективное использование ресурсов, конкурентность, и обработку ошибок в функциональном стиле.

## Базовые концепции

### ConnectionIO

`ConnectionIO[A]` представляет операцию с базой данных, которая выполняется в контексте соединения. Это эффект, который можно комбинировать и трансформировать.

```scala
import doobie._
import doobie.implicits._

// Создание ConnectionIO
val query: ConnectionIO[Int] = sql"SELECT 42".query[Int].unique

// Композиция ConnectionIO
val composed: ConnectionIO[Int] = for {
  a <- sql"SELECT 1".query[Int].unique
  b <- sql"SELECT 2".query[Int].unique
} yield a + b
```

### Transactor

`Transactor[F]` представляет способ выполнения `ConnectionIO` в контексте эффекта `F` (обычно `IO`).

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO
import doobie.util.ExecutionContexts

// Создание Transactor
val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/mydb",
  "user",
  "password"
)

// Выполнение запроса
val result: IO[Int] = query.transact(xa)
```

## Создание подключений

### Базовое подключение

Doobie предоставляет различные способы создания подключений к базе данных.

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO
import doobie.util.ExecutionContexts

// Подключение через DriverManager
val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/mydb",
  "user",
  "password"
)

// Подключение через DataSource
val xaFromDataSource = Transactor.fromDataSource[IO](dataSource)

// Подключение с пулом соединений
val xaWithPool = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/mydb",
  "user",
  "password"
)
```

### Конфигурация подключения

Doobie позволяет настраивать различные параметры подключения.

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO

val xa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/mydb",
  "user",
  "password"
).configure { connection =>
  connection.setAutoCommit(false)
  connection.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED)
}
```

## Запросы

### Простые запросы

Doobie предоставляет DSL для создания SQL запросов.

```scala
import doobie._
import doobie.implicits._

// Простой SELECT запрос
val allUsers: ConnectionIO[List[User]] = 
  sql"SELECT id, name, email FROM users"
    .query[User]
    .to[List]

// SELECT с параметрами
def userById(id: Long): ConnectionIO[Option[User]] = 
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User]
    .option

// INSERT запрос
def createUser(name: String, email: String): ConnectionIO[User] = 
  sql"INSERT INTO users (name, email) VALUES ($name, $email)"
    .update
    .withUniqueGeneratedKeys[Long]("id")
    .map(id => User(id, name, email))

// UPDATE запрос
def updateUser(id: Long, name: String): ConnectionIO[Int] = 
  sql"UPDATE users SET name = $name WHERE id = $id"
    .update
    .run

// DELETE запрос
def deleteUser(id: Long): ConnectionIO[Int] = 
  sql"DELETE FROM users WHERE id = $id"
    .update
    .run
```

### Сложные запросы

Doobie поддерживает создание сложных запросов с join, подзапросами и агрегацией.

```scala
import doobie._
import doobie.implicits._

// JOIN запрос
val usersWithPosts: ConnectionIO[List[(User, Post)]] = 
  sql"""
    SELECT u.id, u.name, u.email, p.id, p.title, p.content
    FROM users u
    JOIN posts p ON u.id = p.user_id
  """
    .query[(User, Post)]
    .to[List]

// Подзапрос
val activeUsers: ConnectionIO[List[User]] = 
  sql"""
    SELECT id, name, email
    FROM users
    WHERE id IN (SELECT user_id FROM posts WHERE created_at > NOW() - INTERVAL '30 days')
  """
    .query[User]
    .to[List]

// Агрегация
val userPostCounts: ConnectionIO[List[(Long, Int)]] = 
  sql"""
    SELECT user_id, COUNT(*) as post_count
    FROM posts
    GROUP BY user_id
  """
    .query[(Long, Int)]
    .to[List]
```

## Транзакции

### Базовые транзакции

Doobie предоставляет простой способ работы с транзакциями.

```scala
import doobie._
import doobie.implicits._

// Транзакция
val transaction: ConnectionIO[User] = for {
  userId <- sql"INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com')"
    .update
    .withUniqueGeneratedKeys[Long]("id")
  _ <- sql"INSERT INTO posts (user_id, title, content) VALUES ($userId, 'First Post', 'Content')"
    .update
    .run
  user <- sql"SELECT id, name, email FROM users WHERE id = $userId"
    .query[User]
    .unique
} yield user

// Выполнение транзакции
val result: IO[User] = transaction.transact(xa)
```

### Управление транзакциями

Doobie позволяет явно управлять транзакциями.

```scala
import doobie._
import doobie.implicits._

// Начало транзакции
val transaction: ConnectionIO[Unit] = for {
  _ <- FC.setAutoCommit(false)
  _ <- sql"INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com')"
    .update
    .run
  _ <- FC.commit
} yield ()

// Откат транзакции
val rollbackTransaction: ConnectionIO[Unit] = for {
  _ <- FC.setAutoCommit(false)
  _ <- sql"INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com')"
    .update
    .run
  _ <- FC.rollback
} yield ()
```

## Обработка ошибок

### Обработка SQL ошибок

Doobie предоставляет различные способы обработки ошибок SQL.

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO

val query: ConnectionIO[Option[User]] = 
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User]
    .option

// Обработка ошибок
val result: IO[Either[String, Option[User]]] = 
  query.transact(xa).attempt.map {
    case Right(user) => Right(user)
    case Left(e: java.sql.SQLException) => Left(s"SQL error: ${e.getMessage}")
    case Left(e) => Left(s"Unexpected error: ${e.getMessage}")
  }
```

## Интеграция с Cats Effect

### Использование с IO

Doobie интегрируется с Cats Effect IO для асинхронного выполнения.

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO

val query: ConnectionIO[List[User]] = 
  sql"SELECT id, name, email FROM users"
    .query[User]
    .to[List]

val result: IO[List[User]] = query.transact(xa)
```

### Использование с Resource

Doobie поддерживает использование Resource для управления подключениями.

```scala
import doobie._
import doobie.implicits._
import cats.effect.{IO, Resource}

val xaResource: Resource[IO, Transactor[IO]] = 
  Resource.pure(Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/mydb",
    "user",
    "password"
  ))

val program = xaResource.use { xa =>
  sql"SELECT 42".query[Int].unique.transact(xa)
}
```

## Лучшие практики

### Использование параметризованных запросов

Doobie рекомендует использовать параметризованные запросы для предотвращения SQL injection.

```scala
import doobie._
import doobie.implicits._

// Хорошо - параметризованный запрос
def userById(id: Long): ConnectionIO[Option[User]] = 
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User]
    .option

// Плохо - конкатенация строк (SQL injection)
def userByIdBad(id: Long): ConnectionIO[Option[User]] = 
  sql"SELECT id, name, email FROM users WHERE id = ${id.toString}"
    .query[User]
    .option
```

### Обработка null значений

Doobie предоставляет различные способы обработки null значений.

```scala
import doobie._
import doobie.implicits._

// Использование Option для nullable полей
case class User(id: Long, name: String, email: Option[String])

val users: ConnectionIO[List[User]] = 
  sql"SELECT id, name, email FROM users"
    .query[User]
    .to[List]
```

## Продвинутые возможности Doobie

### Работа с типами данных

Doobie предоставляет поддержку различных типов данных SQL.

```scala
import doobie._
import doobie.implicits._
import java.time._

// Работа с датами
val usersByDate: ConnectionIO[List[User]] = 
  sql"SELECT id, name, email FROM users WHERE created_at > $LocalDate.now()"
    .query[User]
    .to[List]

// Работа с UUID
import java.util.UUID
val userByUuid: ConnectionIO[Option[User]] = 
  sql"SELECT id, name, email FROM users WHERE uuid = $uuid"
    .query[User]
    .option

// Работа с JSON
import io.circe.Json
val usersWithMetadata: ConnectionIO[List[(User, Json)]] = 
  sql"SELECT id, name, email, metadata FROM users"
    .query[(User, Json)]
    .to[List]
```

### Batch операции

Doobie поддерживает batch операции для эффективной вставки множественных записей.

```scala
import doobie._
import doobie.implicits._

// Batch вставка
val users = List(
  User(0, "Alice", "alice@example.com"),
  User(0, "Bob", "bob@example.com"),
  User(0, "Charlie", "charlie@example.com")
)

val insertUsers: ConnectionIO[Int] = 
  Update[User]("INSERT INTO users (name, email) VALUES (?, ?)")
    .updateMany(users)

// Batch обновление
val updateUsers: ConnectionIO[Int] = 
  Update[(String, Long)]("UPDATE users SET name = ? WHERE id = ?")
    .updateMany(List(("Alice Updated", 1L), ("Bob Updated", 2L)))
```

### Фрагменты запросов

Doobie позволяет создавать переиспользуемые фрагменты SQL запросов.

```scala
import doobie._
import doobie.implicits._
import doobie.Fragment

// Создание фрагментов
val selectUsers = fr"SELECT id, name, email FROM users"
val whereActive = fr"WHERE active = true"
val orderByName = fr"ORDER BY name"

// Комбинирование фрагментов
val activeUsers: ConnectionIO[List[User]] = 
  (selectUsers ++ whereActive ++ orderByName)
    .query[User]
    .to[List]

// Фрагменты с параметрами
def usersByName(name: String): ConnectionIO[List[User]] = 
  (selectUsers ++ fr"WHERE name = $name" ++ orderByName)
    .query[User]
    .to[List]
```

### Оптимизация запросов

Doobie предоставляет инструменты для оптимизации запросов.

```scala
import doobie._
import doobie.implicits._

// Использование индексов
val usersByEmail: ConnectionIO[Option[User]] = 
  sql"SELECT id, name, email FROM users WHERE email = $email"
    .query[User]
    .option  // Использует индекс на email

// Избегание N+1 проблем
val usersWithPosts: ConnectionIO[List[(User, List[Post])]] = 
  sql"""
    SELECT u.id, u.name, u.email, p.id, p.title, p.content
    FROM users u
    LEFT JOIN posts p ON u.id = p.user_id
  """
    .query[(User, Option[Post])]
    .to[List]
    .map(_.groupBy(_._1).mapValues(_.flatMap(_._2)).toList)
```

### Работа с метаданными

Doobie позволяет работать с метаданными схемы базы данных.

```scala
import doobie._
import doobie.implicits._

// Получение информации о таблицах
val tables: ConnectionIO[List[String]] = 
  sql"SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"
    .query[String]
    .to[List]

// Получение информации о колонках
val columns: ConnectionIO[List[(String, String)]] = 
  sql"""
    SELECT column_name, data_type
    FROM information_schema.columns
    WHERE table_name = 'users'
  """
    .query[(String, String)]
    .to[List]
```

### Интеграция с http4s

Doobie интегрируется с http4s для создания веб-приложений с базой данных.

```scala
import org.http4s._
import org.http4s.dsl.io._
import doobie._
import doobie.implicits._

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
}
```

### Тестирование

Doobie предоставляет инструменты для тестирования запросов.

```scala
import doobie._
import doobie.implicits._
import doobie.scalatest.imports._
import org.scalatest.funspec.AnyFunSpec

class UserQueriesSpec extends AnyFunSpec with IOChecker {
  val transactor = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    "sa",
    ""
  )

  describe("User queries") {
    it("should check userById query") {
      check(sql"SELECT id, name, email FROM users WHERE id = ?".query[User])
    }
    
    it("should check createUser query") {
      check(Update[User]("INSERT INTO users (name, email) VALUES (?, ?)"))
    }
  }
}
```

### Миграции

Doobie может использоваться с инструментами миграции баз данных.

```scala
import doobie._
import doobie.implicits._

// Создание таблиц
val createTables: ConnectionIO[Unit] = 
  sql"""
    CREATE TABLE IF NOT EXISTS users (
      id BIGSERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      email VARCHAR(255) NOT NULL UNIQUE
    )
  """
    .update
    .run
    .void

// Миграции
val migrations: List[ConnectionIO[Unit]] = List(
  createTables,
  sql"CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)".update.run.void,
  sql"ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW()".update.run.void
)

val runMigrations: ConnectionIO[Unit] = 
  migrations.foldLeft(FC.unit)((acc, migration) => acc.flatMap(_ => migration))
```

## Заключение

Doobie предоставляет мощный функциональный API для работы с базами данных в Scala. Понимание базовых концепций, создания подключений, запросов, транзакций, обработки ошибок, интеграции с Cats Effect, работы с типами данных, batch операций, фрагментов запросов, оптимизации запросов, работы с метаданными, интеграции с http4s, тестирования, и миграций позволяет создавать надежные, поддерживаемые и высокопроизводительные приложения для работы с базами данных. Doobie особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой Cats Effect, и требуют эффективной работы с базами данных.

## Дополнительные ресурсы

Для дальнейшего изучения Doobie рекомендуется:

- [Doobie Documentation](https://tpolecat.github.io/doobie/)
- [Doobie Examples](https://github.com/tpolecat/doobie/tree/main/modules/example/src/main/scala/example)
- [Cats Effect Documentation](https://typelevel.org/cats-effect/)

## Расширенные примеры и паттерны

### Работа с транзакциями (расширенная)

Doobie предоставляет расширенные возможности для работы с транзакциями.

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO

// Вложенные транзакции
def nestedTransaction: ConnectionIO[User] = {
  FC.setAutoCommit(false).flatMap { _ =>
    for {
      userId <- sql"INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com')"
        .update
        .withUniqueGeneratedKeys[Long]("id")
      
      _ <- sql"INSERT INTO posts (user_id, title, content) VALUES ($userId, 'First Post', 'Content')"
        .update
        .run
      
      _ <- sql"INSERT INTO posts (user_id, title, content) VALUES ($userId, 'Second Post', 'Content')"
        .update
        .run
      
      user <- sql"SELECT id, name, email FROM users WHERE id = $userId"
        .query[User]
        .unique
      
      _ <- FC.commit
    } yield user
  }
}

// Транзакции с savepoints
def transactionWithSavepoint: ConnectionIO[User] = {
  FC.setAutoCommit(false).flatMap { _ =>
    for {
      userId <- sql"INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com')"
        .update
        .withUniqueGeneratedKeys[Long]("id")
      
      savepoint <- FC.setSavepoint("sp1")
      
      _ <- sql"INSERT INTO posts (user_id, title, content) VALUES ($userId, 'Post', 'Content')"
        .update
        .run
      
      _ <- FC.rollback(savepoint)  // Откат к savepoint
      
      user <- sql"SELECT id, name, email FROM users WHERE id = $userId"
        .query[User]
        .unique
      
      _ <- FC.commit
    } yield user
  }
}
```

### Оптимизация запросов (расширенная)

Doobie предоставляет различные техники для оптимизации запросов.

```scala
import doobie._
import doobie.implicits._

// Использование prepared statements с кэшированием
val preparedQuery = HC.prepareStatement(
  "SELECT id, name, email FROM users WHERE id = ?"
)(
  HPS.set(1, userId)(
    HPS.executeQuery(
      HRS.next(HRS.get[User])
    )
  )
)

// Использование batch операций для эффективной вставки
def insertUsersBatch(users: List[User]): ConnectionIO[Int] = {
  val sql = "INSERT INTO users (name, email) VALUES (?, ?)"
  Update[User](sql).updateMany(users)
}

// Использование bulk операций
def bulkUpdate(ids: List[Long], name: String): ConnectionIO[Int] = {
  val sql = "UPDATE users SET name = ? WHERE id = ?"
  Update[(String, Long)](sql).updateMany(ids.map((name, _)))
}
```

### Работа с метаданными (расширенная)

Doobie позволяет работать с метаданными схемы базы данных.

```scala
import doobie._
import doobie.implicits._

// Получение информации о таблицах
val tables: ConnectionIO[List[TableInfo]] = 
  sql"""
    SELECT 
      table_name,
      table_type,
      table_schema
    FROM information_schema.tables
    WHERE table_schema = 'public'
    ORDER BY table_name
  """
    .query[TableInfo]
    .to[List]

// Получение информации о колонках
val columns: ConnectionIO[List[ColumnInfo]] = 
  sql"""
    SELECT 
      column_name,
      data_type,
      is_nullable,
      column_default
    FROM information_schema.columns
    WHERE table_name = 'users'
    ORDER BY ordinal_position
  """
    .query[ColumnInfo]
    .to[List]

// Получение информации о индексах
val indexes: ConnectionIO[List[IndexInfo]] = 
  sql"""
    SELECT 
      indexname,
      indexdef
    FROM pg_indexes
    WHERE tablename = 'users'
  """
    .query[IndexInfo]
    .to[List]
```

### Интеграция с http4s (расширенная)

Doobie интегрируется с http4s для создания веб-приложений с базой данных.

```scala
import org.http4s._
import org.http4s.dsl.io._
import doobie._
import doobie.implicits._
import cats.effect.IO

// Полноценный REST API с Doobie
def userApiRoutes(xa: Transactor[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
  // GET /users - список всех пользователей
  case GET -> Root / "users" :? OptionalQueryParamMatcher(page) +& 
                              OptionalQueryParamMatcher(limit) =>
    val pageNum = page.getOrElse(0)
    val limitNum = limit.getOrElse(10)
    val offset = pageNum * limitNum
    
    sql"SELECT id, name, email FROM users ORDER BY id LIMIT $limitNum OFFSET $offset"
      .query[User]
      .to[List]
      .transact(xa)
      .flatMap(Ok(_))
  
  // GET /users/:id - получить пользователя
  case GET -> Root / "users" / LongVar(id) =>
    sql"SELECT id, name, email FROM users WHERE id = $id"
      .query[User]
      .option
      .transact(xa)
      .flatMap {
        case Some(user) => Ok(user)
        case None => NotFound()
      }
  
  // POST /users - создать пользователя
  case req @ POST -> Root / "users" =>
    req.as[User].flatMap { user =>
      sql"INSERT INTO users (name, email) VALUES (${user.name}, ${user.email})"
        .update
        .withUniqueGeneratedKeys[Long]("id")
        .transact(xa)
        .flatMap(id => Created(user.copy(id = id)))
    }
  
  // PUT /users/:id - обновить пользователя
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
  
  // DELETE /users/:id - удалить пользователя
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

### Тестирование (расширенное)

Doobie предоставляет расширенные инструменты для тестирования запросов.

```scala
import doobie._
import doobie.implicits._
import doobie.scalatest.imports._
import org.scalatest.funspec.AnyFunSpec
import cats.effect.IO

class ExtendedUserQueriesSpec extends AnyFunSpec with IOChecker {
  val transactor = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    "sa",
    ""
  )

  describe("User queries") {
    it("should check userById query") {
      check(sql"SELECT id, name, email FROM users WHERE id = ?".query[User])
    }
    
    it("should check createUser query") {
      check(Update[User]("INSERT INTO users (name, email) VALUES (?, ?)"))
    }
    
    it("should check updateUser query") {
      check(Update[(String, Long)]("UPDATE users SET name = ? WHERE id = ?"))
    }
    
    it("should check deleteUser query") {
      check(sql"DELETE FROM users WHERE id = ?".update)
    }
  }
  
  describe("User operations") {
    it("should create and retrieve user") {
      val program = for {
        userId <- sql"INSERT INTO users (name, email) VALUES ('Test', 'test@example.com')"
          .update
          .withUniqueGeneratedKeys[Long]("id")
        user <- sql"SELECT id, name, email FROM users WHERE id = $userId"
          .query[User]
          .unique
      } yield user
      
      val result = program.transact(transactor).unsafeRunSync()
      assert(result.name == "Test")
      assert(result.email == "test@example.com")
    }
  }
}
```

### Миграции (расширенные)

Doobie может использоваться с инструментами миграции баз данных.

```scala
import doobie._
import doobie.implicits._

// Создание миграций
sealed trait Migration {
  def up: ConnectionIO[Unit]
  def down: ConnectionIO[Unit]
}

case class CreateTableMigration(tableName: String, columns: List[String]) extends Migration {
  def up: ConnectionIO[Unit] = {
    val columnDefs = columns.mkString(", ")
    sql"CREATE TABLE IF NOT EXISTS $tableName ($columnDefs)".update.run.void
  }
  
  def down: ConnectionIO[Unit] = {
    sql"DROP TABLE IF EXISTS $tableName".update.run.void
  }
}

case class AddColumnMigration(tableName: String, columnName: String, columnType: String) extends Migration {
  def up: ConnectionIO[Unit] = {
    sql"ALTER TABLE $tableName ADD COLUMN IF NOT EXISTS $columnName $columnType".update.run.void
  }
  
  def down: ConnectionIO[Unit] = {
    sql"ALTER TABLE $tableName DROP COLUMN IF EXISTS $columnName".update.run.void
  }
}

// Управление миграциями
class MigrationManager(migrations: List[Migration]) {
  def runMigrations: ConnectionIO[Unit] = {
    migrations.foldLeft(FC.unit) { (acc, migration) =>
      acc.flatMap(_ => migration.up)
    }
  }
  
  def rollbackMigrations: ConnectionIO[Unit] = {
    migrations.reverse.foldLeft(FC.unit) { (acc, migration) =>
      acc.flatMap(_ => migration.down)
    }
  }
}
```

## Заключение (финальное расширенное)

Doobie предоставляет мощный функциональный API для работы с базами данных в Scala. Понимание всех аспектов Doobie, от базовых концепций до продвинутых возможностей, позволяет создавать надежные, поддерживаемые и высокопроизводительные приложения для работы с базами данных. Doobie особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой Cats Effect, и требуют эффективной работы с базами данных.

Ключевые преимущества Doobie включают функциональный подход к программированию, типобезопасность, композируемость, интеграцию с Cats Effect, поддержку различных типов данных, batch операции, фрагменты запросов, оптимизацию запросов, работу с метаданными, интеграцию с http4s, тестирование, миграции, репозиторий паттерн, потоковую обработку больших результатов, поддержку различных баз данных, расширенную работу с транзакциями, оптимизацию запросов, работу с метаданными, интеграцию с http4s, расширенное тестирование, и управление миграциями. Эти преимущества делают Doobie идеальным выбором для создания современных приложений для работы с базами данных, которые требуют типобезопасности, производительности, интеграции с функциональной экосистемой Scala, и эффективного управления схемой базы данных.

## Дополнительные темы и паттерны

### Репозиторий паттерн

Doobie позволяет создавать репозитории для работы с данными.

```scala
import doobie._
import doobie.implicits._

trait UserRepository {
  def findById(id: Long): ConnectionIO[Option[User]]
  def findAll: ConnectionIO[List[User]]
  def create(user: User): ConnectionIO[User]
  def update(id: Long, user: User): ConnectionIO[Option[User]]
  def delete(id: Long): ConnectionIO[Boolean]
}

class DoobieUserRepository extends UserRepository {
  override def findById(id: Long): ConnectionIO[Option[User]] = 
    sql"SELECT id, name, email FROM users WHERE id = $id"
      .query[User]
      .option
  
  override def findAll: ConnectionIO[List[User]] = 
    sql"SELECT id, name, email FROM users"
      .query[User]
      .to[List]
  
  override def create(user: User): ConnectionIO[User] = 
    sql"INSERT INTO users (name, email) VALUES (${user.name}, ${user.email})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => user.copy(id = id))
  
  override def update(id: Long, user: User): ConnectionIO[Option[User]] = 
    sql"UPDATE users SET name = ${user.name}, email = ${user.email} WHERE id = $id"
      .update
      .run
      .flatMap { rowsAffected =>
        if (rowsAffected > 0) findById(id)
        else FC.pure(None)
      }
  
  override def delete(id: Long): ConnectionIO[Boolean] = 
    sql"DELETE FROM users WHERE id = $id"
      .update
      .run
      .map(_ > 0)
}
```

### Работа с большими результатами

Doobie поддерживает потоковую обработку больших результатов запросов.

```scala
import doobie._
import doobie.implicits._
import fs2.Stream

// Потоковая обработка результатов
val usersStream: Stream[ConnectionIO, User] = 
  sql"SELECT id, name, email FROM users"
    .query[User]
    .stream

// Обработка потока
val processedStream: Stream[IO, User] = 
  usersStream.transact(xa)
    .chunkN(1000)  // Группировка в батчи
    .evalMap(batch => IO(processBatch(batch)))
    .flatMap(Stream.emits)
```

### Оптимизация производительности

Doobie предоставляет различные техники для оптимизации производительности.

```scala
import doobie._
import doobie.implicits._

// Использование prepared statements
val preparedQuery = 
  HC.prepareStatement("SELECT id, name, email FROM users WHERE id = ?")(
    HPS.set(1, userId)(
      HPS.executeQuery(
        HRS.next(HRS.get[User])
      )
    )
  )

// Кэширование запросов
val cachedQuery = 
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User]
    .option
    .memoize  // Кэширование результата
```

### Работа с различными базами данных

Doobie поддерживает работу с различными базами данных.

```scala
import doobie._
import doobie.implicits._

// PostgreSQL
val postgresXa = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/mydb",
  "user",
  "password"
)

// MySQL
val mysqlXa = Transactor.fromDriverManager[IO](
  "com.mysql.cj.jdbc.Driver",
  "jdbc:mysql://localhost/mydb",
  "user",
  "password"
)

// H2 (для тестирования)
val h2Xa = Transactor.fromDriverManager[IO](
  "org.h2.Driver",
  "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
  "sa",
  ""
)
```

## Заключение (расширенное)

Doobie предоставляет мощный функциональный API для работы с базами данных в Scala. Понимание всех аспектов Doobie, от базовых концепций до продвинутых возможностей, позволяет создавать надежные, поддерживаемые и высокопроизводительные приложения для работы с базами данных. Doobie особенно полезен для создания приложений, которые следуют принципам функционального программирования, обеспечивают типобезопасность, интегрируются с экосистемой Cats Effect, и требуют эффективной работы с базами данных.

## Практические примеры использования Doobie

### Создание функциональных запросов с использованием Doobie

Doobie позволяет создавать функциональные и композируемые запросы.

```scala
import doobie._
import doobie.implicits._

// Функциональные запросы
def getUserById(id: Int): ConnectionIO[Option[User]] = {
  sql"SELECT id, name, email FROM users WHERE id = $id"
    .query[User]
    .option
}

def getUserProfile(userId: Int): ConnectionIO[Option[Profile]] = {
  sql"SELECT user_id, bio FROM profiles WHERE user_id = $userId"
    .query[Profile]
    .option
}

// Композиция запросов
def getUserWithProfile(id: Int): ConnectionIO[Option[(User, Profile)]] = {
  for {
    user <- getUserById(id)
    profile <- user.traverse(u => getUserProfile(u.id))
  } yield user.zip(profile).headOption
}
```

### Использование Doobie для batch операций

Doobie поддерживает эффективные batch операции.

```scala
// Batch вставка
def insertUsers(users: List[User]): ConnectionIO[Int] = {
  val sql = "INSERT INTO users (name, email) VALUES (?, ?)"
  Update[User](sql).updateMany(users)
}
```

### Практические примеры: Doobie для потоковой обработки

```scala
import doobie._
import doobie.implicits._
import fs2.Stream
import cats.effect.IO

// Потоковая обработка больших результатов
def streamLargeResult: Stream[IO, User] = {
  sql"SELECT id, name, email FROM users WHERE active = true"
    .query[User]
    .stream
    .take(1000)  // Обработка по частям
}

// Обработка потока
streamLargeResult
  .evalMap(user => IO.println(s"Processing user: ${user.name}"))
  .compile
  .drain
```

### Практические примеры: Doobie для транзакций

```scala
import doobie._
import doobie.implicits._
import cats.effect.IO

// Транзакция с Doobie
def transferMoney(fromId: Long, toId: Long, amount: Double): ConnectionIO[Unit] = {
  for {
    _ <- sql"UPDATE accounts SET balance = balance - $amount WHERE id = $fromId".update.run
    _ <- sql"UPDATE accounts SET balance = balance + $amount WHERE id = $toId".update.run
  } yield ()
}

// Выполнение транзакции
val transactor = Transactor.fromDriverManager[IO](
  "org.postgresql.Driver",
  "jdbc:postgresql://localhost/db",
  "user",
  "password"
)

transferMoney(1, 2, 100.0).transact(transactor)
```

### Практические примеры: Doobie для сложных запросов

```scala
import doobie._
import doobie.implicits._

// Динамическое построение запросов
def findUsers(filters: Map[String, String]): ConnectionIO[List[User]] = {
  val baseQuery = sql"SELECT id, name, email FROM users WHERE 1=1"
  
  val whereClauses = filters.map { case (key, value) =>
    fr"AND" ++ Fragment.const(s"$key = '$value'")
  }
  
  val finalQuery = whereClauses.foldLeft(baseQuery)(_ ++ _)
  
  finalQuery.query[User].to[List]
}
```

### Практические примеры: Работа с метаданными

```scala
import doobie._
import doobie.implicits._

// Получение метаданных о результатах запроса
def getTableInfo(tableName: String): ConnectionIO[List[ColumnInfo]] = {
  sql"""
    SELECT column_name, data_type, is_nullable
    FROM information_schema.columns
    WHERE table_name = $tableName
  """.query[ColumnInfo].to[List]
}

case class ColumnInfo(name: String, dataType: String, nullable: String)
```

### Практические примеры: Оптимизация запросов

```scala
// Использование prepared statements для оптимизации
def findUsersByIds(ids: List[Long]): ConnectionIO[List[User]] = {
  val placeholders = ids.map(_ => "?").mkString(",")
  val sql = s"SELECT id, name, email FROM users WHERE id IN ($placeholders)"
  
  Fragment(sql, ids).query[User].to[List]
}
```

Ключевые преимущества Doobie включают функциональный подход к программированию, типобезопасность, композицию запросов, интеграцию с Cats Effect, поддержку различных типов данных, batch операции, фрагменты запросов, оптимизацию запросов, работу с метаданными, интеграцию с http4s, тестирование, миграции, репозиторий паттерн, потоковую обработку больших результатов, транзакции, динамическое построение запросов, поддержку различных баз данных и практических применений. Эти преимущества делают Doobie идеальным выбором для создания современных приложений для работы с базами данных, которые требуют типобезопасности, производительности, интеграции с функциональной экосистемой Scala, обработки больших объемов данных, транзакций и динамического построения запросов.

### Использование с различными техниками для batch операций

```scala
import doobie._
import doobie.implicits._

// Batch вставка для оптимизации
def insertUsers(users: List[User]): ConnectionIO[Int] = {
  val sql = "INSERT INTO users (name, email) VALUES (?, ?)"
  
  Update[User](sql).updateMany(users)
}

// Использование
val users = List(
  User(0, "Alice", "alice@example.com"),
  User(0, "Bob", "bob@example.com")
)

val result = insertUsers(users).transact(xa)
```

### Использование с различными техниками для динамических запросов

```scala
import doobie._
import doobie.implicits._

// Динамическое построение запросов
def findUsers(filters: Map[String, String]): ConnectionIO[List[User]] = {
  val baseQuery = sql"SELECT id, name, email FROM users WHERE 1=1"
  
  val whereClauses = filters.map { case (key, value) =>
    fr"AND" ++ Fragment.const(s"$key = '$value'")
  }
  
  val finalQuery = whereClauses.foldLeft(baseQuery)(_ ++ _)
  
  finalQuery.query[User].to[List]
}
```

## Дополнительные ресурсы

Для дальнейшего изучения Doobie рекомендуется:

- [Doobie Documentation](https://tpolecat.github.io/doobie/)
- [Doobie Tutorial](https://tpolecat.github.io/doobie/docs/01-Introduction.html)

