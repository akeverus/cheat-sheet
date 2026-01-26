# Slick

Slick (Scala Language-Integrated Connection Kit) - это современная database query and access library для Scala, предоставляющая функциональный, type-safe подход к работе с реляционными базами данных. Slick генерирует SQL запросы из Scala кода и обеспечивает compile-time проверку типов.

## Содержание

- [Основы Slick](#основы-slick)
  - [Подключение и конфигурация](#подключение-и-конфигурация)
  - [Определение таблиц и моделей](#определение-таблиц-и-моделей)
  - [Database configuration](#database-configuration)
- [CRUD операции](#crud-операции)
  - [Создание (Create)](#создание-create)
  - [Чтение (Read)](#чтение-read)
  - [Обновление (Update)](#обновление-update)
  - [Удаление (Delete)](#удаление-delete)
- [Продвинутые запросы](#продвинутые-запросы)
  - [Joins](#joins)
  - [Сложные запросы и агрегации](#сложные-запросы-и-агрегации)
- [Транзакции](#транзакции)
  - [Управление транзакциями](#управление-транзакциями)
- [Миграции базы данных](#миграции-базы-данных)
  - [Flyway миграции](#flyway-миграции)
- [Профили баз данных](#профили-баз-данных)
  - [Поддержка разных баз данных](#поддержка-разных-баз-данных)
- [Тестирование](#тестирование)
  - [Unit тесты для Slick](#unit-тесты-для-slick)
  - [Integration тесты](#integration-тесты)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Connection pooling](#connection-pooling)
  - [Query optimization](#query-optimization)
- [Расширенные возможности](#расширенные-возможности)
  - [Custom column types](#custom-column-types)
  - [Schema evolution](#schema-evolution)
- [Лучшие практики](#лучшие-практики)
  - [Структура кода](#структура-кода)
  - [Error handling](#error-handling)
  - [Logging и monitoring](#logging-и-monitoring)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Anorm to Slick](#from-anorm-to-slick)
  - [From Slick 3.2 to 3.4](#from-slick-32-to-34)

## Основы Slick

### Подключение и конфигурация
```scala
// build.sbt
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "org.postgresql" % "postgresql" % "42.5.1"
)

// application.conf
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
        maxConnections = 20
        minConnections = 5
      }
    }
  }
}
```

### Определение таблиц и моделей
```scala
package models

import slick.jdbc.PostgresProfile.api._
import java.time.Instant

/**
 * Определение таблиц и моделей в Slick
 * Slick использует функциональный подход для определения схемы БД
 * Table[T] - базовый класс для определения таблицы
 * T - тип модели (case class)
 */

// Case class для модели данных
// Case class представляет строку таблицы в Scala коде
case class User(
  id: Long,                    // Первичный ключ
  name: String,                // Имя пользователя
  email: String,                // Email адрес
  createdAt: Instant,           // Дата создания (не nullable)
  updatedAt: Option[Instant] = None  // Дата обновления (nullable, значение по умолчанию None)
)

// Table definition - определение таблицы в Slick
// Table[User] - таблица, которая маппится на case class User
// tag: Tag - тег для идентификации таблицы в запросах
// "users" - имя таблицы в базе данных
class UserTable(tag: Tag) extends Table[User](tag, "users") {

  // Column definitions - определение колонок таблицы
  // column[T]("name", options) - определение колонки типа T с именем "name"
  // O.PrimaryKey - первичный ключ
  // O.AutoInc - автоматическое увеличение значения (AUTO_INCREMENT/SERIAL)
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  
  // O.Length(255) - максимальная длина VARCHAR колонки
  def name = column[String]("name", O.Length(255))
  
  // O.Unique - уникальное ограничение на колонку
  def email = column[String]("email", O.Length(255), O.Unique)
  
  // O.Default(value) - значение по умолчанию для колонки
  def createdAt = column[Instant]("created_at", O.Default(Instant.now()))
  
  // Option[T] - nullable колонка (может быть NULL в БД)
  def updatedAt = column[Option[Instant]]("updated_at")

  // Default projection - маппинг между case class и колонками
  // def * - метод для маппинга всех колонок на case class
  // (id, name, email, createdAt, updatedAt) - кортеж всех колонок
  // .mapTo[User] - автоматический маппинг кортежа на case class User
  def * = (id, name, email, createdAt, updatedAt).mapTo[User]
  // При запросе SELECT * колонки автоматически маппятся на User

  // Additional indexes and constraints - дополнительные индексы и ограничения
  // index("name", column, unique) - создание индекса
  // unique = true - уникальный индекс
  def emailIndex = index("email_idx", email, unique = true)
  def createdAtIndex = index("created_at_idx", createdAt)

  // Foreign key example - пример внешнего ключа
  // def companyId = column[Long]("company_id")
  // foreignKey("name", column, referencedTable)(_.column) - внешний ключ
  // def company = foreignKey("company_fk", companyId, companies)(_.id)
}

// Table query object - объект для работы с таблицей
object UserTable {
  // TableQuery[T] - запрос к таблице типа T
  // users - объект для выполнения запросов к таблице users
  val users = TableQuery[UserTable]
  // Используется для создания запросов: users.filter(_.id === 1L)

  // Helper methods - вспомогательные методы
  def findById(id: Long) = users.filter(_.id === id)
  def findByEmail(email: String) = users.filter(_.email === email)
  def findByName(name: String) = users.filter(_.name.like(s"%$name%"))
}
```

### Database configuration
```scala
package database

import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import javax.inject.{Inject, Singleton}

@Singleton
class DatabaseConfig @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {

  // Import the API for the database
  import profile.api._

  // Database instance
  val db = dbConfig.db
}
```

## CRUD операции

### Создание (Create)
```scala
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(
  dbConfig: DatabaseConfig
)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  def create(user: User): Future[User] = {
    val insertQuery = UserTable.users returning UserTable.users.map(_.id) into { (user, id) =>
      user.copy(id = id)
    }

    dbConfig.db.run {
      insertQuery += user
    }
  }

  def createMultiple(users: Seq[User]): Future[Seq[User]] = {
    val insertQuery = UserTable.users returning UserTable.users.map(_.id)

    dbConfig.db.run {
      insertQuery ++= users
    }.map { ids =>
      users.zip(ids).map { case (user, id) => user.copy(id = id) }
    }
  }

  // Batch insert with generated keys
  def createBatch(users: Seq[User]): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users ++= users
    }.flatMap { _ =>
      // Get the last inserted ids (implementation depends on database)
      findAll().map(_.takeRight(users.size))
    }
  }

  // Upsert (insert or update)
  def upsert(user: User): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.insertOrUpdate(user)
    }
  }
}
```

### Чтение (Read)
```scala
class UserRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Find by ID
  def findById(id: Long): Future[Option[User]] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id).result.headOption
    }
  }

  // Find all
  def findAll(): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.result
    }
  }

  // Find with limit and offset
  def findAll(limit: Int, offset: Int): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.drop(offset).take(limit).result
    }
  }

  // Find by email
  def findByEmail(email: String): Future[Option[User]] = {
    dbConfig.db.run {
      UserTable.users.filter(_.email === email).result.headOption
    }
  }

  // Complex queries
  def findActiveUsers(): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.filter(_.updatedAt.isDefined).result
    }
  }

  def findUsersCreatedAfter(date: Instant): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.filter(_.createdAt > date).result
    }
  }

  def searchUsers(namePattern: String, emailPattern: String): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.filter { user =>
        user.name.like(s"%$namePattern%") || user.email.like(s"%$emailPattern%")
      }.result
    }
  }

  // Aggregation queries
  def countUsers(): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.length.result
    }
  }

  def countUsersCreatedToday(): Future[Int] = {
    val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
    dbConfig.db.run {
      UserTable.users.filter(_.createdAt >= today).length.result
    }
  }

  // Exists check
  def exists(id: Long): Future[Boolean] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id).exists.result
    }
  }

  // Distinct values
  def findAllEmails(): Future[Seq[String]] = {
    dbConfig.db.run {
      UserTable.users.map(_.email).distinct.result
    }
  }
}
```

### Обновление (Update)
```scala
class UserRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Update entire user
  def update(user: User): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === user.id)
        .update(user.copy(updatedAt = Some(Instant.now())))
    }
  }

  // Partial update - update specific fields
  def updateName(id: Long, newName: String): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id)
        .map(user => (user.name, user.updatedAt))
        .update((newName, Some(Instant.now())))
    }
  }

  def updateEmail(id: Long, newEmail: String): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id)
        .map(user => (user.email, user.updatedAt))
        .update((newEmail, Some(Instant.now())))
    }
  }

  // Bulk update
  def updateAllNames(newName: String): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.map(user => (user.name, user.updatedAt))
        .update((newName, Some(Instant.now())))
    }
  }

  def deactivateOldUsers(olderThan: Instant): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.createdAt < olderThan)
        .map(_.updatedAt)
        .update(Some(Instant.now()))
    }
  }

  // Conditional update
  def updateIfExists(id: Long, user: User): Future[Option[User]] = {
    val updateQuery = UserTable.users.filter(_.id === id)

    dbConfig.db.run {
      updateQuery.result.headOption.flatMap {
        case Some(existingUser) =>
          updateQuery.update(user.copy(updatedAt = Some(Instant.now()))).map(_ => Some(user))
        case None => DBIO.successful(None)
      }.transactionally
    }
  }
}
```

### Удаление (Delete)
```scala
class UserRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Delete by ID
  def delete(id: Long): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id).delete
    }
  }

  // Delete multiple by IDs
  def deleteMultiple(ids: Seq[Long]): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id inSet ids).delete
    }
  }

  // Delete by condition
  def deleteByEmail(email: String): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.email === email).delete
    }
  }

  // Delete old inactive users
  def deleteInactiveUsers(olderThan: Instant): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(user =>
        user.updatedAt.isEmpty || user.updatedAt < olderThan
      ).delete
    }
  }

  // Soft delete (update instead of delete)
  def softDelete(id: Long): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id)
        .map(_.updatedAt)
        .update(Some(Instant.now()))
    }
  }

  // Delete all
  def deleteAll(): Future[Int] = {
    dbConfig.db.run {
      UserTable.users.delete
    }
  }
}
```

## Продвинутые запросы

### Joins
```scala
// Модели с отношениями
case class Company(id: Long, name: String, address: String)
case class Employee(id: Long, name: String, companyId: Long, salary: BigDecimal)

class CompanyTable(tag: Tag) extends Table[Company](tag, "companies") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def address = column[String]("address")
  def * = (id, name, address).mapTo[Company]
}

class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employees") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def companyId = column[Long]("company_id")
  def salary = column[BigDecimal]("salary")
  def * = (id, name, companyId, salary).mapTo[Employee]

  def company = foreignKey("company_fk", companyId, CompanyTable.companies)(_.id)
}

object CompanyTable {
  val companies = TableQuery[CompanyTable]
}

object EmployeeTable {
  val employees = TableQuery[EmployeeTable]
}

// Репозиторий с joins
class CompanyRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Inner join
  def findEmployeesWithCompanies(): Future[Seq[(Employee, Company)]] = {
    dbConfig.db.run {
      EmployeeTable.employees.join(CompanyTable.companies).on(_.companyId === _.id).result
    }
  }

  // Left join
  def findAllEmployeesWithCompanies(): Future[Seq[(Employee, Option[Company])]] = {
    dbConfig.db.run {
      EmployeeTable.employees.joinLeft(CompanyTable.companies).on(_.companyId === _.id).result
    }
  }

  // Join с фильтрацией
  def findEmployeesInCompany(companyName: String): Future[Seq[Employee]] = {
    val query = for {
      (employee, company) <- EmployeeTable.employees join CompanyTable.companies on (_.companyId === _.id)
      if company.name === companyName
    } yield employee

    dbConfig.db.run(query.result)
  }

  // Комплексный join с агрегацией
  def getCompanyStats(): Future[Seq[(String, Int, BigDecimal)]] = {
    val query = CompanyTable.companies
      .join(EmployeeTable.employees).on(_.id === _.companyId)
      .groupBy { case (company, _) => company.name }
      .map { case (companyName, group) =>
        (companyName, group.length, group.map(_._2.salary).avg)
      }

    dbConfig.db.run(query.result)
  }

  // Self join example (иерархические данные)
  case class Category(id: Long, name: String, parentId: Option[Long])

  class CategoryTable(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def parentId = column[Option[Long]]("parent_id")
    def * = (id, name, parentId).mapTo[Category]

    def parent = foreignKey("parent_fk", parentId, CategoryTable.categories)(_.id.?)
  }

  object CategoryTable {
    val categories = TableQuery[CategoryTable]
  }

  def findSubcategories(parentId: Long): Future[Seq[Category]] = {
    dbConfig.db.run {
      CategoryTable.categories.filter(_.parentId === parentId).result
    }
  }

  def findCategoryHierarchy(): Future[Seq[(Category, Option[Category])]] = {
    dbConfig.db.run {
      CategoryTable.categories
        .joinLeft(CategoryTable.categories)
        .on(_.parentId === _.id.?).result
    }
  }
}
```

### Сложные запросы и агрегации
```scala
class AnalyticsRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Агрегационные функции
  def getUserStats(): Future[(Int, Instant, Instant)] = {
    val query = UserTable.users.map { user =>
      (user.id.count, user.createdAt.min, user.createdAt.max)
    }

    dbConfig.db.run(query.result.head)
  }

  // Группировка
  def getUsersByCreationMonth(): Future[Seq[(Int, Int, Int)]] = { // year, month, count
    val query = UserTable.users
      .groupBy { user =>
        val year = user.createdAt.part("year")
        val month = user.createdAt.part("month")
        (year, month)
      }
      .map { case ((year, month), group) =>
        (year, month, group.length)
      }
      .sortBy { case (year, month, _) => (year.desc, month.desc) }

    dbConfig.db.run(query.result)
  }

  // Подзапросы
  def findUsersWithHighActivity(): Future[Seq[User]] = {
    val activeUserIds = UserTable.users
      .filter(_.updatedAt.isDefined)
      .filter(_.updatedAt > Instant.now().minus(7, ChronoUnit.DAYS))
      .map(_.id)

    dbConfig.db.run {
      UserTable.users.filter(_.id in activeUserIds).result
    }
  }

  // Window functions
  def rankUsersByCreationDate(): Future[Seq[(User, Int)]] = {
    val query = UserTable.users
      .sortBy(_.createdAt.asc)
      .zipWithIndex

    dbConfig.db.run(query.result)
  }

  // Рекурсивные запросы (для древовидных структур)
  def findAllDescendants(categoryId: Long): Future[Seq[Category]] = {
    // Использование рекурсивного CTE (если поддерживается БД)
    val query = sql"""
      WITH RECURSIVE descendants AS (
        SELECT id, name, parent_id FROM categories WHERE id = $categoryId
        UNION ALL
        SELECT c.id, c.name, c.parent_id FROM categories c
        INNER JOIN descendants d ON c.parent_id = d.id
      )
      SELECT * FROM descendants WHERE id != $categoryId
    """.as[Category]

    dbConfig.db.run(query)
  }

  // Пагинация с курсором
  def findUsersPaginated(cursor: Option[Long], limit: Int): Future[(Seq[User], Option[Long])] = {
    val baseQuery = UserTable.users.sortBy(_.id.asc)

    val query = cursor match {
      case Some(lastId) => baseQuery.filter(_.id > lastId)
      case None => baseQuery
    }

    dbConfig.db.run {
      for {
        users <- query.take(limit + 1).result
        hasNextPage = users.length > limit
        resultUsers = if (hasNextPage) users.dropRight(1) else users
        nextCursor = if (hasNextPage) resultUsers.lastOption.map(_.id) else None
      } yield (resultUsers, nextCursor)
    }
  }

  // Full-text search
  def searchUsersFullText(searchTerm: String): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.filter { user =>
        user.name ++ " " ++ user.email like s"%$searchTerm%"
      }.result
    }
  }
}
```

## Транзакции

### Управление транзакциями
```scala
class TransactionalRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Простая транзакция
  def transferPoints(fromUserId: Long, toUserId: Long, points: Int): Future[Either[String, Unit]] = {
    val transaction = for {
      fromUserOpt <- UserTable.users.filter(_.id === fromUserId).result.headOption
      toUserOpt <- UserTable.users.filter(_.id === toUserId).result.headOption
      result <- (fromUserOpt, toUserOpt) match {
        case (Some(fromUser), Some(toUser)) if fromUser.points >= points =>
          for {
            _ <- UserTable.users.filter(_.id === fromUserId)
                  .map(_.points).update(fromUser.points - points)
            _ <- UserTable.users.filter(_.id === toUserId)
                  .map(_.points).update(toUser.points + points)
          } yield Right(())
        case (Some(_), Some(_)) =>
          DBIO.successful(Left("Insufficient points"))
        case _ =>
          DBIO.successful(Left("User not found"))
      }
    } yield result

    dbConfig.db.run(transaction.transactionally)
  }

  // Вложенные транзакции
  def complexOperation(userId: Long, data: String): Future[Either[String, User]] = {
    val transaction = (for {
      // Шаг 1: Создать запись в логе
      logId <- AuditLogTable.auditLogs returning AuditLogTable.auditLogs.map(_.id) += AuditLogEntry(userId, s"Starting operation with data: $data")

      // Шаг 2: Обновить пользователя
      updateCount <- UserTable.users.filter(_.id === userId)
        .map(u => (u.data, u.updatedAt))
        .update((data, Some(Instant.now())))

      // Шаг 3: Проверить результат
      userOpt <- UserTable.users.filter(_.id === userId).result.headOption

      result <- userOpt match {
        case Some(user) =>
          // Шаг 4: Записать успех в лог
          AuditLogTable.auditLogs.filter(_.id === logId)
            .map(_.status).update("SUCCESS") *>
          DBIO.successful(Right(user))
        case None =>
          DBIO.successful(Left("User update failed"))
      }
    } yield result).transactionally

    dbConfig.db.run(transaction)
  }

  // Программное управление транзакциями
  def executeInTransaction[T](action: DBIO[T]): Future[T] = {
    dbConfig.db.run(action.transactionally)
  }

  // Read-only транзакция
  def readInTransaction[T](action: DBIO[T]): Future[T] = {
    dbConfig.db.run(action.transactionally.withTransactionIsolation(Serializable))
  }

  // Rollback при ошибке
  def operationWithRollback(): Future[Unit] = {
    val action = for {
      _ <- UserTable.users += User(1, "temp", "temp@test.com", Instant.now())
      _ <- DBIO.failed(new RuntimeException("Simulated failure"))
      _ <- UserTable.users += User(2, "should not exist", "test@test.com", Instant.now())
    } yield ()

    dbConfig.db.run(action.transactionally).recover {
      case _ => println("Transaction rolled back as expected")
    }
  }
}

// Управление уровнем изоляции
class IsolationLevelExample @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  def readUncommittedExample(): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.result
        .withTransactionIsolation(ReadUncommitted)
    }
  }

  def serializableExample(): Future[Int] = {
    val action = UserTable.users.filter(_.active === true).length.result
    dbConfig.db.run(action.withTransactionIsolation(Serializable))
  }
}
```

## Миграции базы данных

### Flyway миграции
```scala
// build.sbt
libraryDependencies += "org.flywaydb" %% "flyway-play" % "7.0.0"

// conf/application.conf
play.modules.enabled += "org.flywaydb.play.PlayModule"

// Миграции в db/migration
// V1__Create_users_table.sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE INDEX email_idx ON users(email);
CREATE INDEX created_at_idx ON users(created_at);

// V2__Add_points_column.sql
ALTER TABLE users ADD COLUMN points INTEGER NOT NULL DEFAULT 0;

// Scala миграции (альтернатива SQL)
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

class V3__Add_indexes extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val connection = context.getConnection
    val statement = connection.createStatement()

    statement.execute("""
      CREATE INDEX CONCURRENTLY IF NOT EXISTS users_name_idx ON users(name);
      CREATE INDEX CONCURRENTLY IF NOT EXISTS users_points_idx ON users(points);
    """)

    statement.close()
  }
}
```

## Профили баз данных

### Поддержка разных баз данных
```scala
// Много профильная конфигурация
trait DatabaseProfile {
  val profile: JdbcProfile
  def users: TableQuery[UserTable]
  def db: Database
}

// PostgreSQL профиль
class PostgresDatabase @Inject()(
  dbConfigProvider: DatabaseConfigProvider
) extends DatabaseProfile with HasDatabaseConfigProvider[JdbcProfile] {

  override val profile = slick.jdbc.PostgresProfile
  import profile.api._

  override val users = TableQuery[UserTable]
  override val db = dbConfig.db
}

// MySQL профиль
class MySQLDatabase @Inject()(
  dbConfigProvider: DatabaseConfigProvider
) extends DatabaseProfile with HasDatabaseConfigProvider[JdbcProfile] {

  override val profile = slick.jdbc.MySQLProfile
  import profile.api._

  // MySQL специфические типы
  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Length(255))
    def email = column[String]("email", O.Length(255), O.Unique)
    def createdAt = column[Instant]("created_at", O.Default(Instant.now()))
    def updatedAt = column[Option[Instant]]("updated_at")
    def * = (id, name, email, createdAt, updatedAt).mapTo[User]
  }

  override val users = TableQuery[UserTable]
  override val db = dbConfig.db
}

// H2 для тестов
class H2Database @Inject()(
  dbConfigProvider: DatabaseConfigProvider
) extends DatabaseProfile with HasDatabaseConfigProvider[JdbcProfile] {

  override val profile = slick.jdbc.H2Profile
  import profile.api._

  override val users = TableQuery[UserTable]
  override val db = dbConfig.db
}
```

## Тестирование

### Unit тесты для Slick
```scala
import org.scalatestplus.play._
import org.mockito.Mockito._
import scala.concurrent.Future
import play.api.test._
import play.api.test.Helpers._

class UserRepositorySpec extends PlaySpec with MockitoSugar {

  "UserRepository" should {
    "return user by id" in {
      // Mock database
      val mockDb = mock[Database]
      val mockDbConfig = mock[DatabaseConfig[JdbcProfile]]

      when(mockDbConfig.db).thenReturn(mockDb)

      val repository = new UserRepository(mockDbConfig)

      val expectedUser = User(1, "John", "john@example.com", Instant.now())

      // Mock the database query
      when(mockDb.run(any[DBIO[Option[User]]])).thenReturn(Future.successful(Some(expectedUser)))

      val result = repository.findById(1L)

      result.futureValue mustBe Some(expectedUser)
    }

    "create user successfully" in {
      val mockDb = mock[Database]
      val mockDbConfig = mock[DatabaseConfig[JdbcProfile]]

      when(mockDbConfig.db).thenReturn(mockDb)

      val repository = new UserRepository(mockDbConfig)
      val user = User(0, "Jane", "jane@example.com", Instant.now())

      when(mockDb.run(any[DBIO[User]])).thenReturn(Future.successful(user.copy(id = 1)))

      val result = repository.create(user)

      result.futureValue.id mustBe 1
      result.futureValue.name mustBe "Jane"
    }
  }
}
```

### Integration тесты
```scala
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

class UserRepositoryIntegrationSpec extends PlaySpec with OneAppPerTest {

  "UserRepository integration" should {
    "create and retrieve user" in {
      val repository = app.injector.instanceOf[UserRepository]

      val user = User(0, "Test User", "test@example.com", Instant.now())

      val createdUserFuture = repository.create(user)
      val createdUser = createdUserFuture.futureValue

      createdUser.id must be > 0L
      createdUser.name mustBe "Test User"

      val retrievedUserFuture = repository.findById(createdUser.id)
      val retrievedUser = retrievedUserFuture.futureValue

      retrievedUser mustBe Some(createdUser)
    }

    "handle transactions correctly" in {
      val repository = app.injector.instanceOf[TransactionalRepository]

      // Test successful transfer
      val result = repository.transferPoints(1L, 2L, 50)
      result.futureValue mustBe Right(())

      // Test insufficient points
      val failedResult = repository.transferPoints(1L, 2L, 1000)
      failedResult.futureValue mustBe Left("Insufficient points")
    }
  }
}
```

## Оптимизация производительности

### Connection pooling
```scala
// application.conf
slick {
  dbs {
    default {
      db {
        connectionPool = "HikariCP"
        numThreads = 10
        maxConnections = 20
        minConnections = 5
        maxIdleTime = 300000
        maxLifetime = 600000
        leakDetectionThreshold = 60000
      }
    }
  }
}
```

### Query optimization
```scala
class OptimizedRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // Использование индексов
  def findUsersByEmailDomain(domain: String): Future[Seq[User]] = {
    dbConfig.db.run {
      UserTable.users.filter(_.email.like(s"%@$domain")).result
    }
  }

  // Пакетные операции
  def createBatchOptimized(users: Seq[User]): Future[Seq[User]] = {
    val batchSize = 1000
    val batches = users.grouped(batchSize).toSeq

    Future.sequence(batches.map { batch =>
      dbConfig.db.run {
        val insertQuery = UserTable.users returning UserTable.users.map(_.id)
        insertQuery ++= batch
      }.map { ids =>
        batch.zip(ids).map { case (user, id) => user.copy(id = id) }
      }
    }).map(_.flatten)
  }

  // Streaming для больших результатов
  def streamUsers(): DatabasePublisher[User] = {
    dbConfig.db.stream {
      UserTable.users.result
    }
  }

  // Кэширование prepared statements
  def findUserWithCache(id: Long): Future[Option[User]] = {
    // Slick автоматически кэширует prepared statements
    dbConfig.db.run {
      UserTable.users.filter(_.id === id).result.headOption
    }
  }

  // Оптимизация N+1 проблемы
  def findUsersWithCompanies(userIds: Seq[Long]): Future[Seq[(User, Company)]] = {
    val query = for {
      user <- UserTable.users if user.id inSet userIds
      company <- CompanyTable.companies if company.id === user.companyId
    } yield (user, company)

    dbConfig.db.run(query.result)
  }

  // Использование EXPLAIN для анализа запросов
  def explainQuery(): Future[String] = {
    import scala.sys.process._

    val query = UserTable.users.filter(_.createdAt > Instant.now().minus(1, ChronoUnit.DAYS))

    // Получить SQL
    val sql = query.result.statements.mkString

    // Выполнить EXPLAIN (PostgreSQL specific)
    Future {
      s"EXPLAIN $sql".!!
    }
  }
}
```

## Расширенные возможности

### Custom column types
```scala
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

// Custom enum type
object UserStatus extends Enumeration {
  type UserStatus = Value
  val Active, Inactive, Suspended = Value
}

implicit val userStatusColumnType: JdbcType[UserStatus.Value] with BaseTypedType[UserStatus.Value] =
  MappedColumnType.base[UserStatus.Value, String](
    _.toString,
    UserStatus.withName
  )

// JSON column type
import play.api.libs.json._
implicit val jsonColumnType: JdbcType[JsValue] with BaseTypedType[JsValue] =
  MappedColumnType.base[JsValue, String](
    Json.stringify,
    Json.parse
  )

// Custom table with custom types
case class AdvancedUser(
  id: Long,
  name: String,
  status: UserStatus.Value,
  preferences: JsValue,
  metadata: Map[String, String]
)

class AdvancedUserTable(tag: Tag) extends Table[AdvancedUser](tag, "advanced_users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def status = column[UserStatus.Value]("status")
  def preferences = column[JsValue]("preferences")
  def metadata = column[Map[String, String]]("metadata")(jsonMapColumnType)

  def * = (id, name, status, preferences, metadata).mapTo[AdvancedUser]
}

// Map column type
implicit val jsonMapColumnType: JdbcType[Map[String, String]] with BaseTypedType[Map[String, String]] =
  MappedColumnType.base[Map[String, String], String](
    m => Json.stringify(Json.toJson(m)),
    s => Json.parse(s).as[Map[String, String]]
  )
```

### Schema evolution
```scala
// Schema management
class SchemaManager @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  def createSchema(): Future[Unit] = {
    dbConfig.db.run {
      (UserTable.users.schema ++ CompanyTable.companies.schema).createIfNotExists
    }
  }

  def dropSchema(): Future[Unit] = {
    dbConfig.db.run {
      (UserTable.users.schema ++ CompanyTable.companies.schema).dropIfExists
    }
  }

  def recreateSchema(): Future[Unit] = {
    for {
      _ <- dropSchema()
      _ <- createSchema()
    } yield ()
  }

  // Schema validation
  def validateSchema(): Future[Boolean] = {
    dbConfig.db.run {
      UserTable.users.result.headOption.map(_.isDefined)
    }
  }

  // Migration helpers
  def addColumn(): Future[Unit] = {
    val alterQuery = sqlu"""
      ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20)
    """
    dbConfig.db.run(alterQuery)
  }

  def createIndex(): Future[Unit] = {
    val indexQuery = sqlu"""
      CREATE INDEX CONCURRENTLY IF NOT EXISTS users_phone_idx ON users(phone)
    """
    dbConfig.db.run(indexQuery)
  }
}
```

## Лучшие практики

### Структура кода
```scala
// Правильная организация репозиториев
package repositories

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(
  dbConfig: DatabaseConfig
)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  // CRUD operations
  def findById(id: Long): Future[Option[User]] = ???
  def findAll(): Future[Seq[User]] = ???
  def create(user: User): Future[User] = ???
  def update(user: User): Future[Int] = ???
  def delete(id: Long): Future[Int] = ???

  // Business logic queries
  def findActiveUsers(): Future[Seq[User]] = ???
  def findUsersByCompany(companyId: Long): Future[Seq[User]] = ???
  def searchUsers(query: String): Future[Seq[User]] = ???
}

// Composition over inheritance
trait BaseRepository[T, ID] {
  def findById(id: ID): Future[Option[T]]
  def findAll(): Future[Seq[T]]
  def save(entity: T): Future[T]
  def delete(id: ID): Future[Int]
}

@Singleton
class BaseUserRepository @Inject()(
  dbConfig: DatabaseConfig
)(implicit ec: ExecutionContext) extends BaseRepository[User, Long] {

  import dbConfig.profile.api._

  def findById(id: Long): Future[Option[User]] =
    dbConfig.db.run(UserTable.users.filter(_.id === id).result.headOption)

  def findAll(): Future[Seq[User]] =
    dbConfig.db.run(UserTable.users.result)

  def save(user: User): Future[User] = {
    val insertQuery = UserTable.users returning UserTable.users.map(_.id) into { (user, id) =>
      user.copy(id = id)
    }
    dbConfig.db.run(insertQuery += user)
  }

  def delete(id: Long): Future[Int] =
    dbConfig.db.run(UserTable.users.filter(_.id === id).delete)
}
```

### Error handling
```scala
// Domain errors
sealed trait RepositoryError
case class NotFoundError(resource: String, id: Any) extends RepositoryError
case class ValidationError(message: String) extends RepositoryError
case class DatabaseError(cause: Throwable) extends RepositoryError

class SafeUserRepository @Inject()(dbConfig: DatabaseConfig)(implicit ec: ExecutionContext) {

  import dbConfig.profile.api._

  type RepositoryResult[T] = Either[RepositoryError, T]

  def findById(id: Long): Future[RepositoryResult[User]] = {
    dbConfig.db.run {
      UserTable.users.filter(_.id === id).result.headOption
    }.map {
      case Some(user) => Right(user)
      case None => Left(NotFoundError("User", id))
    }.recover {
      case ex => Left(DatabaseError(ex))
    }
  }

  def create(user: User): Future[RepositoryResult[User]] = {
    validateUser(user) match {
      case Left(validationError) =>
        Future.successful(Left(validationError))
      case Right(validUser) =>
        dbConfig.db.run {
          (UserTable.users returning UserTable.users.map(_.id) into { (user, id) =>
            user.copy(id = id)
          }) += validUser
        }.map(Right(_)).recover {
          case ex => Left(DatabaseError(ex))
        }
    }
  }

  private def validateUser(user: User): RepositoryResult[User] = {
    if (user.name.isEmpty) Left(ValidationError("Name cannot be empty"))
    else if (!user.email.contains("@")) Left(ValidationError("Invalid email"))
    else Right(user)
  }
}
```

### Logging и monitoring
```scala
import play.api.Logger

class MonitoredUserRepository @Inject()(
  dbConfig: DatabaseConfig
)(implicit ec: ExecutionContext) {

  private val logger = Logger(this.getClass)

  import dbConfig.profile.api._

  def findById(id: Long): Future[Option[User]] = {
    val startTime = System.currentTimeMillis()

    dbConfig.db.run {
      UserTable.users.filter(_.id === id).result.headOption
    }.map { result =>
      val duration = System.currentTimeMillis() - startTime
      logger.info(s"findById($id) took ${duration}ms, result: ${result.isDefined}")
      result
    }.recover {
      case ex =>
        logger.error(s"Error finding user $id", ex)
        throw ex
    }
  }

  // Metrics collection
  def getMetrics(): Future[Map[String, Long]] = {
    dbConfig.db.run {
      for {
        userCount <- UserTable.users.length.result
        activeUsers <- UserTable.users.filter(_.updatedAt.isDefined).length.result
        recentUsers <- UserTable.users.filter(_.createdAt > Instant.now().minus(1, ChronoUnit.DAYS)).length.result
      } yield Map(
        "total_users" -> userCount,
        "active_users" -> activeUsers,
        "recent_users" -> recentUsers
      )
    }
  }
}
```

## Устранение неполадок

### Common Issues
```scala
object SlickTroubleshooting {

  // Проблема: Thread starvation
  // Решение: Увеличить maxConnections и numThreads
  slick {
    dbs {
      default {
        numThreads = 20
        maxConnections = 50
      }
    }
  }

  // Проблема: Connection leaks
  // Решение: Настроить timeouts
  db {
    maxIdleTime = 300000  // 5 minutes
    maxLifetime = 600000  // 10 minutes
    leakDetectionThreshold = 60000  // 1 minute
  }

  // Проблема: Slow queries
  // Решение: Добавить индексы и оптимизировать запросы
  def addIndexes(): Future[Unit] = {
    dbConfig.db.run(sqlu"""
      CREATE INDEX CONCURRENTLY IF NOT EXISTS users_email_idx ON users(email);
      CREATE INDEX CONCURRENTLY IF NOT EXISTS users_created_at_idx ON users(created_at);
    """)
  }

  // Проблема: Large result sets
  // Решение: Использовать streaming или pagination
  def streamUsers(): DatabasePublisher[User] = {
    dbConfig.db.stream(UserTable.users.result)
  }

  // Проблема: N+1 queries
  // Решение: Использовать joins или batch loading
  def findUsersWithCompanies(userIds: Seq[Long]): Future[Seq[(User, Company)]] = {
    val query = for {
      user <- UserTable.users if user.id inSet userIds
      company <- CompanyTable.companies if company.id === user.companyId
    } yield (user, company)

    dbConfig.db.run(query.result)
  }

  // Проблема: Deadlocks
  // Решение: Использовать consistent ordering и retry logic
  def safeUpdate(id: Long, update: User => User): Future[Int] = {
    val action = UserTable.users.filter(_.id === id).result.headOption.flatMap {
      case Some(user) =>
        UserTable.users.filter(_.id === id).update(update(user))
      case None => DBIO.successful(0)
    }

    dbConfig.db.run(action.transactionally)
  }

  // Проблема: Type mapping issues
  // Решение: Правильные implicit conversions
  implicit val instantColumnType = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    timestamp => timestamp.toInstant
  )
}
```

## Руководство по миграции

### From Anorm to Slick
```scala
// Anorm approach
def findById(id: Long): Future[Option[User]] = Future {
  db.withConnection { implicit conn =>
    SQL("SELECT id, name, email FROM users WHERE id = {id}")
      .on("id" -> id)
      .as(userParser.singleOpt)
  }
}

// Slick equivalent
def findById(id: Long): Future[Option[User]] = {
  dbConfig.db.run {
    UserTable.users.filter(_.id === id).result.headOption
  }
}

// From JDBC to Slick
// JDBC
def updateUser(id: Long, name: String): Unit = {
  val conn = DriverManager.getConnection(url, user, password)
  val stmt = conn.prepareStatement("UPDATE users SET name = ? WHERE id = ?")
  stmt.setString(1, name)
  stmt.setLong(2, id)
  stmt.executeUpdate()
  conn.close()
}

// Slick
def updateUser(id: Long, name: String): Future[Int] = {
  dbConfig.db.run {
    UserTable.users.filter(_.id === id).map(_.name).update(name)
  }
}
```

### From Slick 3.2 to 3.4
```scala
// Slick 3.2 style
class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def * = (id, name, email) <> (User.tupled, User.unapply)
}

// Slick 3.4 style (with mapTo)
def * = (id, name, email).mapTo[User]

// API changes
// Old API
val action = users.filter(_.id === 1L).result
db.run(action)

// New API (same, but with better type inference)
db.run {
  users.filter(_.id === 1L).result
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Slick](https://scala-slick.org/)
- [Slick GitHub](https://github.com/slick/slick)
- [Slick Play Integration](https://github.com/playframework/play-slick)
- [Typesafe Config](https://github.com/lightbend/config)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [Flyway](https://flywaydb.org/)

## См. также
- [Play Framework](scala-play.md) - Web framework
- [PostgreSQL](databases/postgresql.md) - Database
- [MySQL](databases/mysql.md) - Database
- [Functional Programming](patterns.md) - Patterns
