---
title: "Scala Slick"
description: "Полное руководство по Slick в Scala: типобезопасный доступ к базам данных, запросы, миграции, транзакции"
tags: ["scala", "slick", "database", "orm", "sql"]
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: []
updated: "2025-01-16"
related: ["scala/scala-basics.md", "scala/scala-play.md"]
---

# Scala Slick

Кратко: полное руководство по Slick в Scala: типобезопасный доступ к базам данных, запросы, миграции, транзакции.

**Дата последнего обновления:** 2025-01-16

## Полезные ссылки

### Официальная документация
- [Slick Documentation](https://scala-slick.org/docs/)

### См. также
- `./scala-basics.md` - основы Scala
- `./scala-play.md` - Play Framework

## Содержание

- [Введение в Slick](#введение-в-slick)
- [Определение схемы](#определение-схемы)
- [Запросы](#запросы)
- [Транзакции](#транзакции)
- [Миграции](#миграции)
- [Лучшие практики](#лучшие-практики)

## Введение в Slick

Slick - это Functional Relational Mapping (FRM) библиотека для Scala, которая предоставляет типобезопасный доступ к базам данных. В отличие от традиционных ORM, Slick использует функциональный подход, где запросы представлены как композируемые значения, а не как строки SQL. Это позволяет компилятору проверять корректность запросов на этапе компиляции, предотвращая ошибки времени выполнения.

Slick генерирует SQL запросы из типобезопасных Scala выражений, что обеспечивает безопасность типов и возможность рефакторинга. Запросы в Slick выглядят как операции над коллекциями, что делает их интуитивно понятными для разработчиков, знакомых с функциональным программированием.

### Основные характеристики

- **Type-safe**: запросы проверяются на этапе компиляции. Компилятор проверяет, что используемые поля существуют в таблицах, типы данных совместимы, и запросы синтаксически корректны. Это предотвращает ошибки, которые в традиционных подходах обнаруживаются только во время выполнения.

- **Functional**: использование функционального стиля программирования. Запросы в Slick - это композируемые значения, которые можно комбинировать, трансформировать и переиспользовать. Это делает код более декларативным и легким для понимания.

- **Asynchronous**: поддержка асинхронных операций через Futures. Все операции с базой данных в Slick возвращают Future, что позволяет не блокировать потоки выполнения и создавать высокопроизводительные приложения. Это особенно важно для веб-приложений, где нужно обрабатывать множество одновременных запросов.

## Определение схемы

Схема базы данных определяется через case классы и Table. Case класс представляет строку таблицы, а Table определяет структуру таблицы и маппинг между таблицей и case классом. Это обеспечивает типобезопасность на уровне компиляции и позволяет компилятору проверять корректность запросов.

Определение схемы в Slick состоит из двух частей: case класс для представления данных и класс Table для описания структуры таблицы. Метод `*` (projection) определяет, как столбцы таблицы маппятся на поля case класса. Это позволяет Slick автоматически преобразовывать результаты запросов в объекты Scala.

```scala
import slick.jdbc.PostgresProfile.api._

// Case класс представляет строку таблицы
// Поля класса соответствуют столбцам таблицы
case class User(id: Long, name: String, email: String)

// Класс Table описывает структуру таблицы
// Tag используется для создания уникальных идентификаторов столбцов
class Users(tag: Tag) extends Table[User](tag, "users") {
  // Определение столбцов с типами и опциями
  // O.PrimaryKey указывает на первичный ключ
  // O.AutoInc означает автоматическую генерацию значений
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  
  // Projection определяет маппинг между столбцами и case классом
  // <> (shaped) создает двунаправленное преобразование
  // User.tupled создает User из кортежа
  // User.unapply извлекает значения из User
  def * = (id, name, email) <> (User.tupled, User.unapply)
}

// TableQuery создает объект для выполнения запросов к таблице
val users = TableQuery[Users]
```

Определение схемы позволяет Slick генерировать типобезопасные запросы и проверять их корректность на этапе компиляции. Это предотвращает ошибки, связанные с неправильными именами столбцов или несовместимыми типами данных.

## Запросы

### Базовые запросы

```scala
import scala.concurrent.ExecutionContext.Implicits.global

// Выборка всех пользователей
val allUsers = db.run(users.result)

// Выборка по условию
val userById = db.run(users.filter(_.id === 1L).result.headOption)

// Вставка
val insertUser = db.run(users += User(0, "Alice", "alice@example.com"))

// Обновление
val updateUser = db.run(
  users.filter(_.id === 1L)
    .map(_.name)
    .update("Alice Updated")
)

// Удаление
val deleteUser = db.run(users.filter(_.id === 1L).delete)
```

### Сложные запросы

```scala
// Join
val usersWithPosts = for {
  (user, post) <- users join posts on (_.id === _.userId)
} yield (user, post)

// Left Join
val usersWithOptionalPosts = for {
  (user, post) <- users joinLeft posts on (_.id === _.userId)
} yield (user, post)

// Группировка
val userCount = db.run(users.length.result)

// Агрегация
val avgAge = db.run(users.map(_.age).avg.result)
val totalUsers = db.run(users.length.result)
val maxAge = db.run(users.map(_.age).max.result)

// Подзапросы
val activeUsers = db.run(
  users.filter(_.id in users.filter(_.active === true).map(_.id)).result
)

// Сортировка и лимит
val topUsers = db.run(
  users.sortBy(_.createdAt.desc).take(10).result
)
```

### Динамические запросы

```scala
def findUsers(
  nameFilter: Option[String] = None,
  ageFilter: Option[Int] = None,
  limit: Int = 100
): Future[Seq[User]] = {
  var query = users
  
  nameFilter.foreach { name =>
    query = query.filter(_.name like s"%$name%")
  }
  
  ageFilter.foreach { age =>
    query = query.filter(_.age >= age)
  }
  
  db.run(query.sortBy(_.name).take(limit).result)
}
```

## Транзакции

Транзакции обеспечивают атомарность операций:

```scala
val transaction = db.run(
  (for {
    _ <- users += User(0, "Alice", "alice@example.com")
    _ <- posts += Post(0, "Title", "Content", userId)
  } yield ()).transactionally
)
```

## Миграции

Миграции управляют эволюцией схемы базы данных:

```scala
import slick.migration.api._

val migration = TableMigration(users)
  .create
  .addColumns(_.id, _.name, _.email)
  .addIndexes(_.email)

// Применение миграции
db.run(migration())

// Откат миграции
db.run(migration.reverse())
```

### Использование Flyway

```scala
import org.flywaydb.core.Flyway

val flyway = Flyway.configure()
  .dataSource(dbUrl, dbUser, dbPassword)
  .load()

flyway.migrate()
```

## Репозитории

Создание репозиториев для работы с данными:

```scala
trait UserRepository {
  def findAll: Future[Seq[User]]
  def findById(id: Long): Future[Option[User]]
  def create(user: User): Future[User]
  def update(id: Long, user: User): Future[Int]
  def delete(id: Long): Future[Int]
}

class SlickUserRepository(db: Database) extends UserRepository {
  import db.profile.api._
  
  val users = TableQuery[Users]
  
  override def findAll: Future[Seq[User]] = {
    db.run(users.result)
  }
  
  override def findById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }
  
  override def create(user: User): Future[User] = {
    db.run((users returning users.map(_.id)) += user).map { id =>
      user.copy(id = id)
    }
  }
  
  override def update(id: Long, user: User): Future[Int] = {
    db.run(users.filter(_.id === id).update(user))
  }
  
  override def delete(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }
}
```

## Лучшие практики

### Использование Prepared Statements

```scala
// Хорошо - использование параметризованных запросов
val userById = users.filter(_.id === userId).result.headOption

// Плохо - конкатенация строк (SQL injection)
val userByIdBad = sql"SELECT * FROM users WHERE id = $userId".as[User]
```

### Обработка ошибок

```scala
val result = db.run(users.result).recover {
  case e: Exception => 
    logger.error("Database error", e)
    Seq.empty[User]
}
```

## Оптимизация запросов

Оптимизация запросов критична для производительности приложений, работающих с базами данных. Неоптимальные запросы могут привести к медленной работе приложения и перегрузке базы данных. Slick предоставляет различные инструменты для оптимизации запросов, включая использование индексов, избегание N+1 проблем, batch операции, и оптимизацию join операций.

Понимание того, как Slick генерирует SQL запросы, позволяет создавать эффективные запросы, которые выполняются быстро и не перегружают базу данных. Профилирование запросов и анализ планов выполнения помогает выявить узкие места и оптимизировать их.

Оптимизация запросов критична для производительности:

```scala
// Использование индексов
// Slick автоматически использует индексы, если они определены в базе данных
// Важно убедиться, что индексы созданы для часто используемых полей
val usersByEmail = users.filter(_.email === email).result.headOption
// Генерирует: SELECT * FROM users WHERE email = ? LIMIT 1
// Если на email есть индекс, запрос выполняется быстро

// Избегание N+1 проблем
// N+1 проблема возникает, когда для каждого элемента основной коллекции
// выполняется отдельный запрос для получения связанных данных
// Решение: использование join для получения всех данных одним запросом
val usersWithPosts = (users joinLeft posts on (_.id === _.userId)).result
// Генерирует один запрос с LEFT JOIN вместо N+1 запросов
// Это значительно быстрее для больших объемов данных

// Использование batch операций
// Batch операции позволяют выполнять множественные операции одним запросом
// Это более эффективно, чем выполнение отдельных операций
val batchInsert = users ++= List(
  User(0, "Alice", "alice@example.com"),
  User(0, "Bob", "bob@example.com")
)
// Генерирует один INSERT с множественными значениями
// Вместо двух отдельных INSERT запросов

// Оптимизация join операций
// Использование правильных типов join и фильтрация данных до join
val activeUsersWithPosts = (users.filter(_.active === true) 
  joinLeft posts.filter(_.published === true) on (_.id === _.userId)).result
// Фильтрация до join уменьшает объем данных для обработки
```

Оптимизация запросов позволяет улучшить производительность приложений. Правильная оптимизация может значительно ускорить выполнение запросов и уменьшить нагрузку на базу данных, что особенно важно для приложений с высокой нагрузкой.

## Асинхронные операции

Slick поддерживает асинхронные операции через Futures:

```scala
import scala.concurrent.ExecutionContext.Implicits.global

val futureUsers = db.run(users.result)
futureUsers.onComplete {
  case Success(users) => println(s"Found ${users.size} users")
  case Failure(exception) => println(s"Error: ${exception.getMessage}")
}
```

Асинхронные операции позволяют эффективно использовать ресурсы.

## Продвинутые возможности Slick

### Работа с JSON

Slick поддерживает работу с JSON данными через специальные типы и операторы.

```scala
import slick.jdbc.PostgresProfile.api._
import play.api.libs.json._

// Определение колонки с JSON
class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def metadata = column[JsValue]("metadata", O.SqlType("JSONB"))
  
  def * = (id, name, metadata) <> (User.tupled, User.unapply)
}

// Запросы с JSON
val usersWithMetadata = users
  .filter(_.metadata.+>>("status") === "active")
  .result
```

### Работа с массивами

Slick поддерживает работу с массивами в базах данных.

```scala
import slick.jdbc.PostgresProfile.api._

// Определение колонки с массивом
class Posts(tag: Tag) extends Table[Post](tag, "posts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def tags = column[Seq[String]]("tags", O.SqlType("TEXT[]"))
  
  def * = (id, title, tags) <> (Post.tupled, Post.unapply)
}

// Запросы с массивами
val postsWithTag = posts
  .filter(_.tags @> Seq("scala", "slick"))
  .result
```

### Работа с полнотекстовым поиском

Slick поддерживает полнотекстовый поиск в базах данных.

```scala
import slick.jdbc.PostgresProfile.api._

// Полнотекстовый поиск
val searchQuery = users
  .filter(_.name @@ toTsQuery("scala"))
  .result

// Ранжирование результатов
val rankedSearch = users
  .filter(_.name @@ toTsQuery("scala"))
  .sortBy(tsRank(_.name, toTsQuery("scala")).desc)
  .result
```

### Работа с оконными функциями

Slick поддерживает оконные функции SQL для аналитических запросов.

```scala
import slick.jdbc.PostgresProfile.api._

// Оконные функции
val rankedUsers = users
  .map(u => (u.id, u.name, rowNumber().over(PartitionBy(u.name).orderBy(u.id.desc))))
  .result

// Агрегатные функции с окнами
val aggregatedData = orders
  .map(o => (
    o.id,
    o.amount,
    sum(o.amount).over(PartitionBy(o.userId).orderBy(o.date))
  ))
  .result
```

## Интеграция с Play Framework

Slick интегрируется с Play Framework для создания веб-приложений с базой данных.

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
  
  def create: Action[AnyContent] = Action.async { implicit request =>
    userForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.html.users.create(formWithErrors))),
      userData => {
        val user = User(0, userData.name, userData.email)
        db.run((users returning users.map(_.id)) += user).map { id =>
          Redirect(routes.UserController.show(id))
        }
      }
    )
  }
}
```

## Миграции и управление схемой

Slick поддерживает миграции схемы базы данных через различные инструменты.

### Миграции через Flyway

```scala
import org.flywaydb.core.Flyway

val flyway = Flyway.configure()
  .dataSource(dbUrl, dbUser, dbPassword)
  .locations("classpath:db/migration")
  .load()

flyway.migrate()
```

### Миграции через Slick

```scala
import slick.jdbc.PostgresProfile.api._

// Создание таблиц
val createTables = DBIO.seq(
  users.schema.create,
  posts.schema.create,
  comments.schema.create
)

db.run(createTables)

// Удаление таблиц
val dropTables = DBIO.seq(
  comments.schema.drop,
  posts.schema.drop,
  users.schema.drop
)

db.run(dropTables)
```

## Оптимизация производительности (детально)

### Использование индексов

Правильное использование индексов критично для производительности запросов.

```scala
import slick.jdbc.PostgresProfile.api._

// Создание индексов
val createIndexes = DBIO.seq(
  sqlu"CREATE INDEX idx_users_email ON users(email)",
  sqlu"CREATE INDEX idx_posts_user_id ON posts(user_id)",
  sqlu"CREATE INDEX idx_posts_created_at ON posts(created_at)"
)

db.run(createIndexes)

// Использование индексов в запросах
val usersByEmail = users
  .filter(_.email === email)  // Использует индекс idx_users_email
  .result
```

### Оптимизация join операций

Оптимизация join операций позволяет улучшить производительность сложных запросов.

```scala
import slick.jdbc.PostgresProfile.api._

// Эффективный join
val usersWithPosts = (users joinLeft posts on (_.id === _.userId))
  .result
  .map(_.groupBy(_._1).mapValues(_.flatMap(_._2)))

// Избегание N+1 проблем
val usersWithPostsOptimized = (users joinLeft posts on (_.id === _.userId))
  .result
  .map(_.groupBy(_._1).mapValues(_.flatMap(_._2)))
```

## Продвинутые возможности Slick

### Работа с транзакциями (расширенная)

Slick предоставляет расширенные возможности для работы с транзакциями.

```scala
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

// Вложенные транзакции
val nestedTransaction = db.run {
  (for {
    userId <- (users returning users.map(_.id)) += User(0, "Alice", "alice@example.com")
    _ <- posts += Post(0, userId, "First Post", "Content")
    _ <- posts += Post(0, userId, "Second Post", "Content")
    user <- users.filter(_.id === userId).result.head
  } yield user).transactionally
}

// Транзакции с savepoints
val transactionWithSavepoint = db.run {
  (for {
    userId <- (users returning users.map(_.id)) += User(0, "Alice", "alice@example.com")
    savepoint <- DBIO.successful("sp1")
    _ <- posts += Post(0, userId, "Post", "Content")
    _ <- DBIO.failed(new Exception("Rollback"))
    user <- users.filter(_.id === userId).result.head
  } yield user).transactionally
}
```

### Работа с асинхронными операциями

Slick поддерживает асинхронные операции через Future.

```scala
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

// Асинхронные запросы
val asyncQuery: Future[Seq[User]] = db.run {
  users.filter(_.age > 18).result
}

// Параллельные запросы
val parallelQueries = for {
  users <- db.run(users.result)
  posts <- db.run(posts.result)
  comments <- db.run(comments.result)
} yield (users, posts, comments)
```

### Работа с потоками данных

Slick поддерживает потоковую обработку больших результатов.

```scala
import slick.jdbc.PostgresProfile.api._
import akka.stream.scaladsl.Source

// Потоковая обработка результатов
val stream: Source[User, NotUsed] = Source.fromPublisher(
  db.stream(users.result)
)

// Обработка потока
stream
  .filter(_.age > 18)
  .map(_.name)
  .runForeach(println)
```

### Работа с метаданными

Slick позволяет работать с метаданными схемы базы данных.

```scala
import slick.jdbc.PostgresProfile.api._

// Получение информации о таблицах
val tables = db.run {
  sql"SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"
    .as[String]
}

// Получение информации о колонках
val columns = db.run {
  sql"""
    SELECT column_name, data_type
    FROM information_schema.columns
    WHERE table_name = 'users'
  """.as[(String, String)]
}
```

### Интеграция с Play Framework (расширенная)

Slick интегрируется с Play Framework для создания веб-приложений.

```scala
import slick.jdbc.PostgresProfile.api._
import play.api.mvc._

// Контроллер с Slick
class UserController(cc: ControllerComponents, db: Database) extends AbstractController(cc) {
  def getUsers = Action.async {
    db.run(users.result).map { users =>
      Ok(Json.toJson(users))
    }
  }
  
  def createUser = Action.async(parse.json[User]) { request =>
    db.run(users += request.body).map { _ =>
      Created
    }
  }
}
```

## Заключение (расширенное)

Slick предоставляет типобезопасный и функциональный способ работы с базами данных в Scala. Понимание определения схемы, запросов, транзакций, миграций, работы с JSON и массивами, полнотекстового поиска, оконных функций, интеграции с Play Framework, оптимизации производительности, расширенной работы с транзакциями, асинхронными операциями, потоками данных, метаданными, и их практических применений позволяет создавать надежные, поддерживаемые и высокопроизводительные приложения. Оптимизация запросов, использование индексов, правильная работа с join операциями, расширенная работа с транзакциями, асинхронные операции, потоковая обработка данных, работа с метаданными, и интеграция с Play Framework критичны для производительности и функциональности. Slick особенно полезен для создания веб-приложений, которые должны эффективно работать с базами данных, обеспечивать типобезопасность на этапе компиляции, поддерживать сложные запросы и аналитику, работать с большими объемами данных, и интегрироваться с веб-фреймворками.

## Практические примеры использования Slick

### Создание типобезопасных запросов с использованием Slick

Slick позволяет создавать типобезопасные запросы на этапе компиляции.

```scala
import slick.jdbc.PostgresProfile.api._

// Определение схемы
class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def * = (id, name, email) <> (User.tupled, User.unapply)
}

val users = TableQuery[Users]

// Типобезопасные запросы
val query = users.filter(_.email.like("%@example.com"))
val result = db.run(query.result)
```

### Использование Slick для сложных запросов

Slick поддерживает сложные запросы с join операциями.

```scala
// Сложный запрос с join
val query = for {
  user <- users
  profile <- profiles if profile.userId === user.id
} yield (user.name, profile.bio)

val result = db.run(query.result)
```

### Практические примеры: Репозиторий паттерн с Slick

```scala
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Future, ExecutionContext}

trait Repository[T, K] {
  def findById(id: K): Future[Option[T]]
  def findAll(): Future[Seq[T]]
  def save(entity: T): Future[T]
  def delete(id: K): Future[Int]
  def update(entity: T): Future[Int]
}

class UserRepository(db: Database)(implicit ec: ExecutionContext) 
  extends Repository[User, Long] {
  
  private val users = TableQuery[Users]
  
  def findById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }
  
  def findAll(): Future[Seq[User]] = {
    db.run(users.result)
  }
  
  def save(user: User): Future[User] = {
    db.run((users returning users.map(_.id)) += user)
      .map(id => user.copy(id = id))
  }
  
  def delete(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }
  
  def update(user: User): Future[Int] = {
    db.run(users.filter(_.id === user.id).update(user))
  }
}
```

### Практические примеры: Миграции с Flyway и Slick

```scala
import slick.jdbc.PostgresProfile.api._
import org.flywaydb.core.Flyway

class MigrationService(dbConfig: DatabaseConfig) {
  def migrate(): Unit = {
    val flyway = Flyway.configure()
      .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
      .load()
    
    flyway.migrate()
  }
}
```

### Практические примеры: Работа с транзакциями

```scala
import slick.jdbc.PostgresProfile.api._

// Транзакции в Slick
def transferMoney(fromId: Long, toId: Long, amount: Double): DBIO[Unit] = {
  (for {
    fromAccount <- accounts.filter(_.id === fromId).result.head
    toAccount <- accounts.filter(_.id === toId).result.head
    _ <- accounts.filter(_.id === fromId).map(_.balance).update(fromAccount.balance - amount)
    _ <- accounts.filter(_.id === toId).map(_.balance).update(toAccount.balance + amount)
  } yield ()).transactionally
}

// Выполнение транзакции
val db = Database.forConfig("database")
val result = db.run(transferMoney(1L, 2L, 100.0))
```

### Практические примеры: Работа с миграциями

```scala
import org.flywaydb.core.Flyway

// Миграции с Flyway
class MigrationService(dbConfig: DatabaseConfig) {
  def migrate(): Unit = {
    val flyway = Flyway.configure()
      .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
      .locations("classpath:db/migration")
      .load()
    
    flyway.migrate()
  }
}
```

## Дополнительные ресурсы

Для дальнейшего изучения Slick рекомендуется:

- [Slick Documentation](https://scala-slick.org/docs/)

